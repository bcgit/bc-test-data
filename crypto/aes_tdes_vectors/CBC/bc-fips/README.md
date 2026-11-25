# bc-fips interop vectors for CBC ciphertext stealing (CBC-CS1, CBC-CS2, CBC-CS3)

Two small Java programs against bc-fips (`org.bouncycastle:bc-fips`) 2.1.3, which implements the three
ciphertext-stealing variants of the Addendum to NIST SP 800-38A (October 2010) as
`FipsAES.CBCwithCS1`, `CBCwithCS2` and `CBCwithCS3`, and names CS3 "CTS" in its JCA provider
(`AES/CTS/NoPadding` and `AES/CBC/CTSPadding`, as RFC 2040 Sec 8 defines CTS):

- `gen/GenCbcCs.java` - `java -cp bc-fips-2.1.3.jar gen/GenCbcCs.java <out.json>` writes the
  vector file. Under AES-128, AES-192 and AES-256 it covers every length from 15 to 49 bytes, and
  0, 1, 63, 64, 65, 100, 255, 256, 257, 1000 and 4097: a single block, every partial final block
  after one and after two whole blocks, the block boundaries, and longer messages. Each record
  holds the key, IV, plaintext and the CS1, CS2 and CS3 ciphertexts from the low-level API.
  Before a record is written the program checks that:
  - the JCA provider (`AES/CBC/CS1Padding`, `CS2Padding`, `CS3Padding`) gives the same bytes;
  - both CTS aliases give the CS3 bytes;
  - every ciphertext is as long as its plaintext and decrypts back through both APIs.

  A length below one block is written as `"refused": true` once every variant has been checked to
  refuse it through both APIs, in both directions.
- `check/CheckCbcCs.java` - `java -cp bc-fips-2.1.3.jar check/CheckCbcCs.java <ciphertexts.txt>`
  decrypts another implementation's output with bc-fips: the acceptance check for bc-rust output.
  Each input line is `<keyBits> <len> <cs1|cs2|cs3|cts> <ciphertext hex>` for the inputs the
  generator derives at that key size and length; a `cts` line is decrypted through
  `AES/CTS/NoPadding`. It reports how many lines decrypt to the plaintext and how many are
  byte-identical to bc-fips's own ciphertext, and exits non-zero if any line fails.

Both run as single-file source programs (`java <file>.java`, JDK 11 or later); there is no build file
and no other dependency. The jar is from Maven Central and is not kept here.

The keys, IVs and plaintexts are not random: each is the first bytes of SHA-256 over a fixed label,
the field name, the key size, the length and a counter (`derive` in both programs), so the vector
file is reproducible from the generator alone.

`AES-CBC-CS-bc-fips-2.1.3.json` is the generator's output, produced on 2026-11-24 with bc-fips 2.1.3
(SHA-256 of the jar `f55fc476ede820d5c03fe87e6ced5bb0095e1dc30b387445846a3ff0274f8c0e`, signed by
Legion of the Bouncy Castle Inc.) on Oracle JDK 26.0.2.1: 129 records with ciphertexts and 9 refused
lengths. These are interoperability vectors produced by one implementation, not NIST vectors; the
NIST ACVP vectors for the same three modes are beside this directory, in
`../ACVP-AES-CBC-CS{1,2,3}-1.0.*.json`.
