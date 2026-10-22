# PKCS#12 secret keys written by the JDK

Keystores written by the JDK's own `PKCS12` keystore (provider `SUN`, OpenJDK 25), holding one
`KeyStore.SecretKeyEntry` for each secret-key algorithm the JDK can store. The JDK encodes each one
as a `secretBag` whose `secretTypeId` is `pkcs8ShroudedKeyBag` and whose value is an encrypted
PKCS#8 `PrivateKeyInfo` naming the key algorithm. It puts the bags in a plain `data` ContentInfo, not
an `encryptedData` one, because each key is already encrypted on its own. Reading this format in BC
needs `org.bouncycastle.pkcs12.allow_sun_secret_keys` set.

The store password and every entry password are `jdk-secret-keys`.

| File | Written with | MAC |
|---|---|---|
| `jdk-secret-keys-mac.p12` | `store(out, password)` | HMAC-SHA256 (the JDK default) |
| `jdk-secret-keys-nostorepw.p12` | `store(out, null)` | none |
| `jdk-secret-keys-macnone.p12` | `store(out, password)` with `keystore.pkcs12.macAlgorithm=NONE` | none |

The aliases, algorithms and key lengths are in `GenJdkSecretKeyStores.java`, which wrote the files.
Byte `j` of the `i`th key (counting from 0 in that table) is `(i * 16 + j) & 0xff`. For example,
`aes` is `000102...0f`.

Some of the PKCS#8 algorithm OIDs the JDK writes are not the ones BC uses for the same key:
- AES gets the bare arc `2.16.840.1.101.3.4.1`, not a key-size-specific CBC OID.
- DESede gets the OIW OID `1.3.14.3.2.17`, not `1.2.840.113549.3.7`.
