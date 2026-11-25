import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.Security;
import java.util.ArrayList;
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
 * Generates the bc-fips CBC-CS1 / CBC-CS2 / CBC-CS3 interoperability vectors for AES-128, AES-192
 * and AES-256 (Addendum to NIST SP 800-38A, October 2010).
 *
 * <p>Run from the directory above {@code gen/}, with the bc-fips jar on the class path:
 * <pre>
 *   java -cp bc-fips-2.1.3.jar gen/GenCbcCs.java AES-CBC-CS-bc-fips-2.1.3.json
 * </pre>
 *
 * <p>The keys, IVs and plaintexts are not random: {@link #derive} computes them from SHA-256 over a
 * fixed label, so this source alone reproduces the output file. Every ciphertext is the low-level
 * API's ({@code FipsAES.CBCwithCS1/CS2/CS3} through {@code FipsAES.OperatorFactory}); before a
 * record is written the generator checks that the JCA provider ({@code AES/CBC/CS1Padding},
 * {@code CS2Padding}, {@code CS3Padding}) gives the same bytes, that both of the provider's CTS
 * aliases ({@code AES/CTS/NoPadding} and {@code AES/CBC/CTSPadding}) give the CS3 bytes, and that
 * each ciphertext decrypts back through both APIs. A length below one block is recorded as refused
 * after checking that every variant, through both APIs, refuses it in both directions.
 */
public class GenCbcCs
{
    static final String LABEL = "bc-test-data CBC-CS bc-fips vectors";

    static final int[] KEY_BITS = { 128, 192, 256 };

    static final FipsAES.ParametersWithIV[] VARIANTS = { FipsAES.CBCwithCS1, FipsAES.CBCwithCS2, FipsAES.CBCwithCS3 };

    static final String[] JCA_VARIANTS = { "AES/CBC/CS1Padding", "AES/CBC/CS2Padding", "AES/CBC/CS3Padding" };

    /** The provider's two names for ciphertext stealing, both of which must be CS3. */
    static final String[] JCA_CTS_ALIASES = { "AES/CTS/NoPadding", "AES/CBC/CTSPadding" };

    static int[] lengths()
    {
        List<Integer> l = new ArrayList<Integer>();
        // refused: shorter than one block
        l.add(0); l.add(1); l.add(15);
        // one block, then every partial final block after one and after two whole blocks
        l.add(16);
        for (int i = 17; i <= 31; i++) l.add(i);
        l.add(32);
        for (int i = 33; i <= 47; i++) l.add(i);
        // block boundaries and longer messages
        int[] more = { 48, 49, 63, 64, 65, 100, 255, 256, 257, 1000, 4097 };
        for (int m : more) l.add(m);
        int[] out = new int[l.size()];
        for (int i = 0; i != out.length; i++) out[i] = l.get(i);
        return out;
    }

    /**
     * The first {@code n} bytes of SHA-256(LABEL || 0x00 || what || 0x00 || keyBits || len || i) for
     * i = 0, 1, 2, ..., each integer as 4 big-endian bytes.
     */
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

    static byte[] jca(String transformation, int mode, byte[] key, byte[] iv, byte[] input)
        throws Exception
    {
        Cipher c = Cipher.getInstance(transformation, "BCFIPS");
        c.init(mode, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
        return c.doFinal(input);
    }

    /** True if {@code task} throws: the refusal of a length below one block. */
    static boolean refuses(Task task)
    {
        try
        {
            task.run();
            return false;
        }
        catch (Exception e)
        {
            return true;
        }
    }

    interface Task
    {
        void run()
            throws Exception;
    }

    static void check(boolean ok, String what)
    {
        if (!ok)
        {
            throw new IllegalStateException("check failed: " + what);
        }
    }

    static String hex(byte[] b)
    {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte x : b)
        {
            sb.append(Character.forDigit((x >> 4) & 0xf, 16)).append(Character.forDigit(x & 0xf, 16));
        }
        return sb.toString();
    }

    public static void main(String[] args)
        throws Exception
    {
        if (args.length != 1)
        {
            System.err.println("usage: java -cp bc-fips-<version>.jar gen/GenCbcCs.java <output.json>");
            System.exit(2);
        }
        Security.addProvider(new BouncyCastleFipsProvider());
        // The jar's own version (its manifest's Implementation-Version, the Maven version), not the
        // provider's getVersionStr(), which reports 2.1.3 as "2.0103".
        String version = BouncyCastleFipsProvider.class.getPackage().getImplementationVersion();

        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"description\": \"CBC-CS1, CBC-CS2 and CBC-CS3 (Addendum to NIST SP 800-38A, October 2010) under AES, generated with bc-fips. Interoperability vectors, not NIST vectors. len is in bytes; a refused record is a length below one block, which bc-fips refuses in every variant and both directions.\",\n");
        json.append("  \"generator\": \"gen/GenCbcCs.java\",\n");
        json.append("  \"provider\": \"org.bouncycastle:bc-fips:" + version + " (FipsAES.CBCwithCS1, CBCwithCS2, CBCwithCS3)\",\n");
        json.append("  \"java\": \"" + System.getProperty("java.vendor") + " " + System.getProperty("java.version") + "\",\n");
        json.append("  \"testGroups\": [\n");

        int tcId = 0;
        int written = 0;
        int refused = 0;
        int[] lens = lengths();
        for (int g = 0; g != KEY_BITS.length; g++)
        {
            int keyBits = KEY_BITS[g];
            json.append("    {\n      \"tgId\": " + (g + 1) + ",\n      \"keyLen\": " + keyBits + ",\n      \"tests\": [\n");
            for (int li = 0; li != lens.length; li++)
            {
                final int len = lens[li];
                final byte[] key = derive("key", keyBits, len, keyBits / 8);
                final byte[] iv = derive("iv", keyBits, len, 16);
                final byte[] pt = derive("pt", keyBits, len, len);
                tcId++;
                json.append("        {\"tcId\": " + tcId + ", \"len\": " + len + ", \"key\": \"" + hex(key) + "\", \"iv\": \"" + hex(iv) + "\", \"pt\": \"" + hex(pt) + "\"");

                if (len < 16)
                {
                    for (int v = 0; v != VARIANTS.length; v++)
                    {
                        final FipsAES.ParametersWithIV variant = VARIANTS[v];
                        final String transformation = JCA_VARIANTS[v];
                        check(refuses(() -> fipsEncrypt(variant, key, iv, pt)), "low-level encrypt refuses len " + len);
                        check(refuses(() -> fipsDecrypt(variant, key, iv, pt)), "low-level decrypt refuses len " + len);
                        check(refuses(() -> jca(transformation, Cipher.ENCRYPT_MODE, key, iv, pt)), transformation + " encrypt refuses len " + len);
                        check(refuses(() -> jca(transformation, Cipher.DECRYPT_MODE, key, iv, pt)), transformation + " decrypt refuses len " + len);
                    }
                    for (final String alias : JCA_CTS_ALIASES)
                    {
                        check(refuses(() -> jca(alias, Cipher.ENCRYPT_MODE, key, iv, pt)), alias + " encrypt refuses len " + len);
                    }
                    json.append(", \"refused\": true}");
                    refused++;
                }
                else
                {
                    byte[][] cts = new byte[3][];
                    for (int v = 0; v != VARIANTS.length; v++)
                    {
                        byte[] ct = fipsEncrypt(VARIANTS[v], key, iv, pt);
                        check(ct.length == len, "ciphertext as long as the plaintext");
                        check(Arrays.equals(ct, jca(JCA_VARIANTS[v], Cipher.ENCRYPT_MODE, key, iv, pt)), JCA_VARIANTS[v] + " agrees with the low-level API");
                        check(Arrays.equals(pt, fipsDecrypt(VARIANTS[v], key, iv, ct)), "low-level round trip");
                        check(Arrays.equals(pt, jca(JCA_VARIANTS[v], Cipher.DECRYPT_MODE, key, iv, ct)), JCA_VARIANTS[v] + " round trip");
                        cts[v] = ct;
                    }
                    for (String alias : JCA_CTS_ALIASES)
                    {
                        check(Arrays.equals(cts[2], jca(alias, Cipher.ENCRYPT_MODE, key, iv, pt)), alias + " is CS3");
                        check(Arrays.equals(pt, jca(alias, Cipher.DECRYPT_MODE, key, iv, cts[2])), alias + " decrypts CS3");
                    }
                    json.append(", \"cs1\": \"" + hex(cts[0]) + "\", \"cs2\": \"" + hex(cts[1]) + "\", \"cs3\": \"" + hex(cts[2]) + "\"}");
                    written++;
                }
                json.append(li + 1 == lens.length ? "\n" : ",\n");
            }
            json.append("      ]\n    }" + (g + 1 == KEY_BITS.length ? "\n" : ",\n"));
        }
        json.append("  ]\n}\n");

        Files.write(Paths.get(args[0]), json.toString().getBytes(StandardCharsets.US_ASCII));
        System.out.println("bc-fips " + version + " on " + System.getProperty("java.version") + ": " + written + " records with ciphertexts, " + refused + " refused lengths, written to " + args[0]);
    }
}
