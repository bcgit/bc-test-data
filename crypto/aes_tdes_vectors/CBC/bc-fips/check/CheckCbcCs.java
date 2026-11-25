import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.Security;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.CipherOutputStream;
import org.bouncycastle.crypto.SymmetricKey;
import org.bouncycastle.crypto.SymmetricSecretKey;
import org.bouncycastle.crypto.fips.FipsAES;
import org.bouncycastle.crypto.fips.FipsInputDecryptor;
import org.bouncycastle.crypto.fips.FipsOutputEncryptor;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;

/**
 * The acceptance check for another implementation's CBC-CS output: decrypts it with bc-fips.
 *
 * <p>Run from the directory above {@code check/}:
 * <pre>
 *   java -cp bc-fips-2.1.3.jar check/CheckCbcCs.java &lt;ciphertexts.txt&gt;
 * </pre>
 * where each line of the input is {@code <keyBits> <len> <variant> <ciphertext hex>}, the variant
 * one of {@code cs1}, {@code cs2}, {@code cs3} or {@code cts}, for the key, IV and plaintext that
 * {@code gen/GenCbcCs.java} derives for that key size and length (the derivation is repeated
 * here, unchanged). A {@code cs1}..{@code cs3} line is decrypted through
 * {@code FipsAES.CBCwithCS1/CS2/CS3}, a {@code cts} line through the provider's
 * {@code AES/CTS/NoPadding}. Each line passes if it decrypts to the plaintext; the program also
 * reports whether the ciphertext is byte-identical to bc-fips's own, and exits non-zero if any
 * line fails.
 */
public class CheckCbcCs
{
    static final String LABEL = "bc-test-data CBC-CS bc-fips vectors";

    /** As GenCbcCs.derive. */
    static byte[] derive(String what, int keyBits, int len, int n)
        throws Exception
    {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256", "SUN");
        byte[] out = new byte[n];
        int off = 0;
        for (int i = 0; off < n; i++)
        {
            sha256.reset();
            sha256.update(LABEL.getBytes(StandardCharsets.US_ASCII));
            sha256.update((byte)0);
            sha256.update(what.getBytes(StandardCharsets.US_ASCII));
            sha256.update((byte)0);
            sha256.update(int4(keyBits));
            sha256.update(int4(len));
            sha256.update(int4(i));
            byte[] h = sha256.digest();
            int take = Math.min(h.length, n - off);
            System.arraycopy(h, 0, out, off, take);
            off += take;
        }
        return out;
    }

    static byte[] int4(int v)
    {
        return new byte[] { (byte)(v >>> 24), (byte)(v >>> 16), (byte)(v >>> 8), (byte)v };
    }

    static FipsAES.ParametersWithIV variant(String name)
    {
        switch (name)
        {
        case "cs1":
            return FipsAES.CBCwithCS1;
        case "cs2":
            return FipsAES.CBCwithCS2;
        case "cs3":
        case "cts":
            return FipsAES.CBCwithCS3;
        default:
            throw new IllegalArgumentException("unknown variant " + name);
        }
    }

    static byte[] fipsEncrypt(FipsAES.ParametersWithIV variant, byte[] key, byte[] iv, byte[] pt)
        throws Exception
    {
        SymmetricKey k = new SymmetricSecretKey(variant, key);
        FipsOutputEncryptor<FipsAES.Parameters> enc = new FipsAES.OperatorFactory().createOutputEncryptor(k, variant.withIV(iv));
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        CipherOutputStream cOut = enc.getEncryptingStream(bOut);
        cOut.write(pt);
        cOut.close();
        return bOut.toByteArray();
    }

    static byte[] fipsDecrypt(FipsAES.ParametersWithIV variant, byte[] key, byte[] iv, byte[] ct)
        throws Exception
    {
        SymmetricKey k = new SymmetricSecretKey(variant, key);
        FipsInputDecryptor<FipsAES.Parameters> dec = new FipsAES.OperatorFactory().createInputDecryptor(k, variant.withIV(iv));
        InputStream in = dec.getDecryptingStream(new ByteArrayInputStream(ct));
        byte[] pt = in.readAllBytes();
        in.close();
        return pt;
    }

    static byte[] jcaDecrypt(String transformation, byte[] key, byte[] iv, byte[] ct)
        throws Exception
    {
        Cipher c = Cipher.getInstance(transformation, "BCFIPS");
        c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
        return c.doFinal(ct);
    }

    static byte[] unhex(String s)
    {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i != b.length; i++)
        {
            b[i] = (byte)Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
        }
        return b;
    }

    public static void main(String[] args)
        throws Exception
    {
        if (args.length != 1)
        {
            System.err.println("usage: java -cp bc-fips-<version>.jar check/CheckCbcCs.java <ciphertexts.txt>");
            System.exit(2);
        }
        Security.addProvider(new BouncyCastleFipsProvider());
        String version = BouncyCastleFipsProvider.class.getPackage().getImplementationVersion();

        List<String> lines = Files.readAllLines(Paths.get(args[0]), StandardCharsets.US_ASCII);
        int passed = 0;
        int identical = 0;
        int failed = 0;
        for (String line : lines)
        {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#"))
            {
                continue;
            }
            String[] f = line.split("\\s+");
            int keyBits = Integer.parseInt(f[0]);
            int len = Integer.parseInt(f[1]);
            String name = f[2];
            byte[] ct = unhex(f.length > 3 ? f[3] : "");
            byte[] key = derive("key", keyBits, len, keyBits / 8);
            byte[] iv = derive("iv", keyBits, len, 16);
            byte[] pt = derive("pt", keyBits, len, len);

            byte[] recovered = name.equals("cts")
                ? jcaDecrypt("AES/CTS/NoPadding", key, iv, ct)
                : fipsDecrypt(variant(name), key, iv, ct);
            if (Arrays.equals(recovered, pt))
            {
                passed++;
            }
            else
            {
                failed++;
                System.out.println("FAIL: AES-" + keyBits + " len " + len + " " + name + " does not decrypt to the plaintext");
            }
            if (Arrays.equals(ct, fipsEncrypt(variant(name), key, iv, pt)))
            {
                identical++;
            }
        }
        System.out.println("bc-fips " + version + " on " + System.getProperty("java.version") + ": " + passed + " of "
            + (passed + failed) + " ciphertexts decrypt to the plaintext; " + identical + " are byte-identical to bc-fips's own");
        System.exit(failed == 0 ? 0 : 1);
    }
}
