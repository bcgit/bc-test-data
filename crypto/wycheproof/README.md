# Project Wycheproof test vectors

The JSON files in this directory are **Project Wycheproof** test vectors, redistributed here
unmodified so that the Bouncy Castle projects can run them alongside their other common test data.

They are not Bouncy Castle work. See the attribution and licence below before copying them onward.

## Attribution

| | |
|---|---|
| Project | Project Wycheproof |
| Upstream | <https://github.com/C2SP/wycheproof> |
| Originally by | Google |
| Now maintained by | C2SP (Community Cryptography Specification Project) |
| Licence | Apache License 2.0 |
| Source path | `testvectors_v1/` |
| Taken at commit | `e0df04e0c033` (2025-10-07) |; `3fa63dd0344a` (2026-08-24) for the ML-DSA and RSA-OAEP files |
| Vector set version | `google-wycheproof` 0.9rc5, as recorded in each file's `source` field |

Project Wycheproof tests crypto libraries against known attacks. Its value over a conformance
vector set such as NIST's ACVP is that a large share of its cases are *invalid* by design: they
carry input chosen to trip an implementation up, and the library is expected to reject them.

## Licence

Apache License 2.0. The full text is in `LICENSE` in this directory, copied verbatim from
<https://github.com/C2SP/wycheproof/blob/main/LICENSE>; a copy must accompany any further
redistribution of these files, so keep it with them. Retain this README, or equivalent
attribution, alongside them as well.

## Files

| File | Algorithm | Tests | Valid | Invalid | Used by |
|---|---|---|---|---|---|
| `aes_cbc_pkcs5_test.json` | AES-CBC-PKCS5 | 216 | 72 | 144 | bc-rust `crypto/aes-lowmemory/tests/wycheproof_cbc_tests.rs` |
| `camellia_cbc_pkcs5_test.json` | Camellia-CBC-PKCS5 | 216 | 72 | 144 | bc-rust `crypto/camellia-lowmemory/tests/wycheproof_cbc_tests.rs` |
| `aria_cbc_pkcs5_test.json` | ARIA-CBC-PKCS5 | 216 | 72 | 144 | bc-rust `crypto/aria-lowmemory/tests/wycheproof_cbc_tests.rs` |
| `sm4_ccm_test.json` | SM4-CCM | 184 | 135 | 49 | not yet: no CCM implementation |
| `sm4_gcm_test.json` | SM4-GCM | 104 | 75 | 29 | not yet: no GCM implementation |
| `aes_gcm_test.json` | AES-GCM | 316 | 229 | 87 | bc-rust `crypto/modes/tests/wycheproof_gcm_tests.rs` (planned with the GCM implementation) |
| `mldsa_44_verify_test.json` | ML-DSA-44 verify | 180 | 77 | 103 | bc-java `core/.../pqc/crypto/test/MLDSAWycheproofTest.java` |
| `mldsa_65_verify_test.json` | ML-DSA-65 verify | 210 | 79 | 131 | bc-java `core/.../pqc/crypto/test/MLDSAWycheproofTest.java` |
| `mldsa_87_verify_test.json` | ML-DSA-87 verify | 241 | 71 | 170 | bc-java `core/.../pqc/crypto/test/MLDSAWycheproofTest.java` |
| `rsa_oaep_2048_sha1_mgf1sha1_test.json` | RSAES-OAEP | 36 | 17 | 19 | bc-rust `crypto/rsa/tests/rsaes_oaep_tests.rs` |
| `rsa_oaep_2048_sha224_mgf1sha1_test.json` | RSAES-OAEP | 31 | 13 | 18 | bc-rust `crypto/rsa/tests/rsaes_oaep_tests.rs` |
| `rsa_oaep_2048_sha224_mgf1sha224_test.json` | RSAES-OAEP | 35 | 17 | 18 | bc-rust `crypto/rsa/tests/rsaes_oaep_tests.rs` |
| `rsa_oaep_2048_sha256_mgf1sha1_test.json` | RSAES-OAEP | 31 | 13 | 18 | bc-rust `crypto/rsa/tests/rsaes_oaep_tests.rs` |
| `rsa_oaep_2048_sha256_mgf1sha256_test.json` | RSAES-OAEP | 37 | 18 | 19 | bc-rust `crypto/rsa/tests/rsaes_oaep_tests.rs` |
| `rsa_oaep_2048_sha384_mgf1sha1_test.json` | RSAES-OAEP | 31 | 13 | 18 | bc-rust `crypto/rsa/tests/rsaes_oaep_tests.rs` |
| `rsa_oaep_2048_sha384_mgf1sha384_test.json` | RSAES-OAEP | 34 | 16 | 18 | bc-rust `crypto/rsa/tests/rsaes_oaep_tests.rs` |
| `rsa_oaep_2048_sha512_224_mgf1sha1_test.json` | RSAES-OAEP | 32 | 13 | 19 | bc-rust `crypto/rsa/tests/rsaes_oaep_tests.rs` |
| `rsa_oaep_2048_sha512_224_mgf1sha512_224_test.json` | RSAES-OAEP | 35 | 16 | 19 | bc-rust `crypto/rsa/tests/rsaes_oaep_tests.rs` |
| `rsa_oaep_2048_sha512_mgf1sha1_test.json` | RSAES-OAEP | 31 | 13 | 18 | bc-rust `crypto/rsa/tests/rsaes_oaep_tests.rs` |
| `rsa_oaep_2048_sha512_mgf1sha512_test.json` | RSAES-OAEP | 33 | 14 | 19 | bc-rust `crypto/rsa/tests/rsaes_oaep_tests.rs` |
| `rsa_oaep_3072_sha256_mgf1sha1_test.json` | RSAES-OAEP | 32 | 13 | 19 | bc-rust `crypto/rsa/tests/rsaes_oaep_tests.rs` |
| `rsa_oaep_3072_sha256_mgf1sha256_test.json` | RSAES-OAEP | 37 | 18 | 19 | bc-rust `crypto/rsa/tests/rsaes_oaep_tests.rs` |
| `rsa_oaep_3072_sha512_256_mgf1sha1_test.json` | RSAES-OAEP | 31 | 13 | 18 | bc-rust `crypto/rsa/tests/rsaes_oaep_tests.rs` |
| `rsa_oaep_3072_sha512_256_mgf1sha512_256_test.json` | RSAES-OAEP | 37 | 18 | 19 | bc-rust `crypto/rsa/tests/rsaes_oaep_tests.rs` |
| `rsa_oaep_3072_sha512_mgf1sha1_test.json` | RSAES-OAEP | 31 | 13 | 18 | bc-rust `crypto/rsa/tests/rsaes_oaep_tests.rs` |
| `rsa_oaep_3072_sha512_mgf1sha512_test.json` | RSAES-OAEP | 33 | 15 | 18 | bc-rust `crypto/rsa/tests/rsaes_oaep_tests.rs` |
| `rsa_oaep_4096_sha256_mgf1sha1_test.json` | RSAES-OAEP | 32 | 13 | 19 | bc-rust `crypto/rsa/tests/rsaes_oaep_tests.rs` |
| `rsa_oaep_4096_sha256_mgf1sha256_test.json` | RSAES-OAEP | 37 | 18 | 19 | bc-rust `crypto/rsa/tests/rsaes_oaep_tests.rs` |
| `rsa_oaep_4096_sha512_mgf1sha1_test.json` | RSAES-OAEP | 31 | 13 | 18 | bc-rust `crypto/rsa/tests/rsaes_oaep_tests.rs` |
| `rsa_oaep_4096_sha512_mgf1sha512_test.json` | RSAES-OAEP | 36 | 17 | 19 | bc-rust `crypto/rsa/tests/rsaes_oaep_tests.rs` |
| `rsa_oaep_misc_test.json` | RSAES-OAEP | 395 | 392 (+3 acceptable) | 0 | bc-rust `crypto/rsa/tests/rsaes_oaep_tests.rs` |

The three CBC-PKCS5 files are the ones in use today. Of each 216 cases, 144 are invalid and 141 of
those are flagged `BadPadding` -- ciphertexts whose plaintext does not end in well-formed PKCS#7
padding, which decryption must reject. That is what makes them worth having: they are adversarial
where the ACVP and published vectors are confirmatory. PKCS#5 and PKCS#7 are the same padding for a
16-byte block; the name is Wycheproof's.

The two SM4 files are AEAD vectors, kept here ready for a CCM or GCM implementation. They are not
read by anything yet.

`aes_gcm_test.json` is for the AES-GCM implementation. Its 45 groups are 15 IV sizes (0, 8, 16, 32,
48, 64, 80, 96, 120, 128, 160, 256, 512, 1024 and 2056 bits) times the three AES key sizes, all with
a 128-bit tag; the 96-bit-IV groups hold 197 of the 316 cases, 116 `valid` and 81 `invalid`. The
`invalid` cases are modified or truncated tags, altered ciphertext and AAD, and the zero-length and
oversized IVs an implementation must refuse. The data itself is also the ACVP/CAVP coverage gap:
messages from 0 to several blocks with every partial-block length, which the NIST GCM sets touch
only at a few points.

The three `mldsa_*_verify_test.json` files are ML-DSA signature verification, 631 cases of which
404 are invalid. That ratio is the point: the ACVP and NIST KAT material bc-java already runs
confirms the specified paths, while these carry signatures violating the infinity norm bound
(`InfinityNormViolation`, 166 across the three), malformed hint encodings (`InvalidHintsEncoding`),
zero and wrong-length public keys (`ZeroPublicKey`, `IncorrectPublicKeyLength`), wrong-length
signatures, out-of-range contexts (`InvalidContext`) and an omitted modular reduction
(`MissingReduction`, ML-DSA-87 only). A verifier may reject at any stage, so the bc-java runner
accepts a refusal at key construction, at init or from verification itself. The matching
`mldsa_*_sign_seed_test.json` and `mldsa_*_sign_noseed_test.json` files upstream are not copied
here yet.

The 22 `rsa_oaep_*_test.json` files are RSAES-OAEP decryption. The 21 named for a modulus size, a
label hash and an MGF1 hash hold 389 invalid cases between them, flagged `InvalidOaepPadding` (273)
or `InvalidCiphertext` (116), which an implementation must refuse without telling the two apart.
`rsa_oaep_misc_test.json` has no invalid cases: 392 valid and 3 acceptable (`SmallIntegerCiphertext`,
at sizes bc-rust has no key type for) over more pairings of the hashes and more modulus sizes, 1024
to 8192 bits. bc-rust runs the 255 at 2048, 3072, 4096 and 8192 bits and skips the other 140.

## Verifying what is here

```
sha256sum -c <<'SUMS'
e45234427e10cf91f27324e52afe8c00906f294dbae061535e2ae13dd300a46a  aes_cbc_pkcs5_test.json
6cc19e584fef532d70bfd0dd0a7cc7a564ca4c8d73d3904690bb29958f46193e  camellia_cbc_pkcs5_test.json
9d72bd9e5884acfbcd2e905fd7a81a2c542c38a65f4bd3910f77c1dcc27cf714  aria_cbc_pkcs5_test.json
5fcfe25c71da0837a1e570cbf864410d46f9d4fcfcf90553ff80fba6bdbf20fe  sm4_ccm_test.json
425295e9974806b4a0635fb17c653757a049c4e5877596f991b7b371ceca0640  sm4_gcm_test.json
985e5ecc172e181eaf49e89508b9470dcf478002eb7e8559c707eb42dc97dfe7  aes_gcm_test.json
0ca1b5df4575263e29b31fae7569a3da41df9a3b6fee56720a992d0cd1153b68  mldsa_44_verify_test.json
49ac366d76115eab56b7116f10d06e288e6f23fe6cfb90b26bfb2d731a8d1e02  mldsa_65_verify_test.json
e9e04216d4217265a5affba2568476d35742dbd8ffc9d4c23b3441334a08a224  mldsa_87_verify_test.json
2c7b3694cd9265eb88ddb586ca29aefcea02938bde772e19a71a4fa601fc71c5  rsa_oaep_2048_sha1_mgf1sha1_test.json
de52321ae1088869603ed0226a0e9456e436282d2a173a0e954c38dabd7abc41  rsa_oaep_2048_sha224_mgf1sha1_test.json
0a3d5c4da6a7a1d70a555e07511410d3540207f6057bfcb9b722f028e535e81b  rsa_oaep_2048_sha224_mgf1sha224_test.json
d3f25242297957b3663ab5c9a7ff3fe7e34f8171ed1ff275842a0cfaccb5104d  rsa_oaep_2048_sha256_mgf1sha1_test.json
83dce701e3d8640ac56e6d7bf37a5a8f9bd6a336e1e932a1e9bfe4ba9c5ea3a9  rsa_oaep_2048_sha256_mgf1sha256_test.json
88f1c87ba2ec53c4d6b091535480eceabd4c7e26993346566642da8d012889bd  rsa_oaep_2048_sha384_mgf1sha1_test.json
b789151c90035789b5760f3c3bc3cb2338604b482362a31b22a0fdedce73b21c  rsa_oaep_2048_sha384_mgf1sha384_test.json
8602158e55507e1938a524153bf0178eae3d93500de771f00df404abf7761e6e  rsa_oaep_2048_sha512_224_mgf1sha1_test.json
dc5ef81233688d0bbfd1831902f5fc389579667861f98a12442bf1d273e560d4  rsa_oaep_2048_sha512_224_mgf1sha512_224_test.json
59e7a7e6f287dfee584cbe2169ce849fc0e8b9ebc286e8f5847a6dfa32e1e106  rsa_oaep_2048_sha512_mgf1sha1_test.json
37aa37a21910b59fcca10c29ffc726ff2f437938455457314d30587c1ebeee86  rsa_oaep_2048_sha512_mgf1sha512_test.json
c5cbfc3fccf45efcaaecb520abb3c61e95f85f7fceafdea982a9e55c74bde98c  rsa_oaep_3072_sha256_mgf1sha1_test.json
c8122302f522bfd539650c71c4aa966bd1f89acb50c6f4c190030fdb9b2684a4  rsa_oaep_3072_sha256_mgf1sha256_test.json
3d7d210a05338402c411606b7ae6b2a8497a8621f175f072f091704fdf449686  rsa_oaep_3072_sha512_256_mgf1sha1_test.json
fff874a24fd1a925be3213c490cca505b69247b11aa04e141d7fe2cc0a685d2d  rsa_oaep_3072_sha512_256_mgf1sha512_256_test.json
bae6e423e1bbbed93f828490e609a84dd524fac31f597512ba92add889014f66  rsa_oaep_3072_sha512_mgf1sha1_test.json
ddef1a4ad4be281f49a8be85ed11adbe69a03d667530cc7638739d5c7f519fc9  rsa_oaep_3072_sha512_mgf1sha512_test.json
d826b470b7c4f76d9dfe1135e446f9654d559dc71629475a83f7f7524c92409a  rsa_oaep_4096_sha256_mgf1sha1_test.json
e0eb85bcd6d66a85941ee3d98e44f7d1ffb0cf870ba6550e1ed3bec4174c60a9  rsa_oaep_4096_sha256_mgf1sha256_test.json
0d0f90964bd91e2f58d7b4d3dfc95352a38866bf9ec97f5dfbdf62072fc0a141  rsa_oaep_4096_sha512_mgf1sha1_test.json
5b5a83d2071b8aeb4aa8463adb8150bb0422ee1c461e4ab33f7e3dd63c79c0d6  rsa_oaep_4096_sha512_mgf1sha512_test.json
a989f2f0fb3151fe45ba93c629beb620fc949a0ba0ff1f694e451a6885449c79  rsa_oaep_misc_test.json
SUMS
```

`LICENSE` is byte-identical to the upstream file:

```
sha256sum -c <<'SUMS'
58d1e17ffe5109a7ae296caafcadfdbe6a7d176f0bc4ab01e12a689b0499d8bd  LICENSE
SUMS
```

## Refreshing

```
BASE=https://raw.githubusercontent.com/C2SP/wycheproof/main/testvectors_v1
for f in aes_cbc_pkcs5_test.json camellia_cbc_pkcs5_test.json aria_cbc_pkcs5_test.json \
         sm4_ccm_test.json sm4_gcm_test.json aes_gcm_test.json \
         mldsa_44_verify_test.json mldsa_65_verify_test.json mldsa_87_verify_test.json; do
    curl -sL "$BASE/$f" -o "$f"
done
for f in 2048_sha1_mgf1sha1 2048_sha224_mgf1sha1 2048_sha224_mgf1sha224 2048_sha256_mgf1sha1 \
         2048_sha256_mgf1sha256 2048_sha384_mgf1sha1 2048_sha384_mgf1sha384 2048_sha512_224_mgf1sha1 \
         2048_sha512_224_mgf1sha512_224 2048_sha512_mgf1sha1 2048_sha512_mgf1sha512 \
         3072_sha256_mgf1sha1 3072_sha256_mgf1sha256 3072_sha512_256_mgf1sha1 \
         3072_sha512_256_mgf1sha512_256 3072_sha512_mgf1sha1 3072_sha512_mgf1sha512 \
         4096_sha256_mgf1sha1 4096_sha256_mgf1sha256 4096_sha512_mgf1sha1 4096_sha512_mgf1sha512 misc; do
    curl -sL "$BASE/rsa_oaep_${f}_test.json" -o "rsa_oaep_${f}_test.json"
done
```

Update the commit, the version and the checksums above when you do, and re-run the suites that read
them: upstream revises vectors, and a silent change to test data is worse than no test data.
