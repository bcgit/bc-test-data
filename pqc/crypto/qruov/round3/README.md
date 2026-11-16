# QR-UOV known-answer tests, NIST round 3

Read by bc-java's `core/src/test/java/org/bouncycastle/pqc/crypto/test/QRUOVTest.java` once bc-java implements
round 3. Until then it reads the round 2 files in `../kat_shake/` and `../kat_aes/`, which can be deleted once
bc-java main has the round 3 port.

- `<set>-<prg>/PQCsignKAT_32.rsp` are the 30 NIST response files of the QR-UOV round 3 submission: one for each of
  the 15 parameter sets with each of the two public-seed PRGs (`shake` for SHAKE128, `aes` for AES-128-CTR), 100
  entries each. Round 3 uses a 32-byte secret key at every security level, so every file has the same name.
- They are the `.rsp` files of `round3/kat_q{7,31,127}_{aes,shake}.zip` in https://github.com/qruov/kat at commit
  `dd6530b0d41d8bcf56e9f70bc56cb9b6b44db47f` ("KAT for round 3"), unchanged. The `.req` files are left out.
- `KAT_SHA256SUMS.txt` is `test/KAT_SHA256SUMS.txt` of the round 3 reference implementation,
  https://github.com/qruov/round3 at commit `bd284d2dc7af0482c398fb29dd7dc4eb821e107c`. Check the files with
  `shasum -a 256 -c KAT_SHA256SUMS.txt` in this directory.

The directory names give the security category, q and L, but not v and m:

| `<set>` | (q, L, v, m) | in the round 3 specification |
|---|---|---|
| `1q127L3`  | (127, 3, 156, 54)   | main parameters (Table 1) |
| `1q127L10` | (127, 10, 540, 60)  | main parameters, new in round 3 |
| `3q127L3`  | (127, 3, 228, 78)   | main parameters |
| `3q127L10` | (127, 10, 820, 90)  | main parameters, new in round 3 |
| `5q127L3`  | (127, 3, 306, 105)  | main parameters |
| `5q127L10` | (127, 10, 1040, 110) | main parameters, new in round 3 |
| `1q31L3`   | (31, 3, 165, 60)    | additional parameters (Table 16) |
| `1q31L10`  | (31, 10, 600, 70)   | additional parameters |
| `1q7L10`   | (7, 10, 740, 100)   | additional parameters |
| `3q31L3`   | (31, 3, 246, 87)    | additional parameters |
| `3q31L10`  | (31, 10, 890, 100)  | additional parameters |
| `3q7L10`   | (7, 10, 1100, 140)  | additional parameters |
| `5q31L3`   | (31, 3, 324, 114)   | additional parameters |
| `5q31L10`  | (31, 10, 1120, 120) | additional parameters |
| `5q7L10`   | (7, 10, 1490, 190)  | additional parameters |

The 12 sets that round 2 also had keep their names, but none of their keys or signatures carries over: entry 0 of
each round 2 file has the same seed and message as here, and a different pk, sk and signature.
