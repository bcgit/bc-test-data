import java.io.FileOutputStream;
import java.security.KeyStore;

import javax.crypto.spec.SecretKeySpec;

/**
 * Writes the jdk-secret-keys fixtures with the JDK's own PKCS12 keystore (provider SUN).
 * Run with no other provider installed: java GenJdkSecretKeyStores <outdir>
 */
public class GenJdkSecretKeyStores
{
    static final char[] PASSWORD = "jdk-secret-keys".toCharArray();

    // alias, JCA algorithm name, key length in bytes
    static final Object[][] KEYS = {
        {"aes", "AES", 16},
        {"desede", "DESede", 24},
        {"des", "DES", 8},
        {"rc2", "RC2", 16},
        {"blowfish", "Blowfish", 16},
        {"hmacsha1", "HmacSHA1", 20},
        {"hmacsha224", "HmacSHA224", 28},
        {"hmacsha256", "HmacSHA256", 32},
        {"hmacsha384", "HmacSHA384", 48},
        {"hmacsha512", "HmacSHA512", 64},
        {"hmacsha512-224", "HmacSHA512/224", 28},
        {"hmacsha512-256", "HmacSHA512/256", 32},
        {"hmacsha3-224", "HmacSHA3-224", 28},
        {"hmacsha3-256", "HmacSHA3-256", 32},
        {"hmacsha3-384", "HmacSHA3-384", 48},
        {"hmacsha3-512", "HmacSHA3-512", 64},
    };

    // key i, byte j has the value (i * 16 + j) & 0xff
    static byte[] keyBytes(int i, int len)
    {
        byte[] k = new byte[len];
        for (int j = 0; j != len; j++)
        {
            k[j] = (byte)(i * 16 + j);
        }
        return k;
    }

    static void write(String file, char[] storePassword)
        throws Exception
    {
        KeyStore ks = KeyStore.getInstance("PKCS12", "SUN");
        ks.load(null, null);
        for (int i = 0; i != KEYS.length; i++)
        {
            ks.setEntry((String)KEYS[i][0],
                new KeyStore.SecretKeyEntry(new SecretKeySpec(keyBytes(i, (Integer)KEYS[i][2]), (String)KEYS[i][1])),
                new KeyStore.PasswordProtection(PASSWORD));
        }
        FileOutputStream out = new FileOutputStream(file);
        ks.store(out, storePassword);
        out.close();
    }

    public static void main(String[] args)
        throws Exception
    {
        write(args[0] + "/jdk-secret-keys-mac.p12", PASSWORD);
        write(args[0] + "/jdk-secret-keys-nostorepw.p12", null);
        System.setProperty("keystore.pkcs12.macAlgorithm", "NONE");
        write(args[0] + "/jdk-secret-keys-macnone.p12", PASSWORD);
    }
}
