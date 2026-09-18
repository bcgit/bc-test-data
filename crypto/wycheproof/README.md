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
| Taken at commit | `e0df04e0c033` (2025-10-07) |; `3fa63dd0344a` (2026-08-24) for the ML-DSA files |
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
```

Update the commit, the version and the checksums above when you do, and re-run the suites that read
them: upstream revises vectors, and a silent change to test data is worse than no test data.
