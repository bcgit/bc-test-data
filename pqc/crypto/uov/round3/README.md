# UOV reference vectors, NIST round 3

Read by bc-java's `core/src/test/java/org/bouncycastle/pqc/crypto/test/UOVTest.java` once bc-java
implements round 3. Until then it reads the round 2 vectors in `../ref/`, which can be deleted once
bc-java main has the round 3 port. Each file holds one vector as key=value lines: `sk_seed`, `salt`,
`msg`, `pk`, `sk`, `sig`. Lines starting with `#` are comments.

- `uov_Ip_*`, `uov_III_*`, `uov_V_*` and `uov_Is_pkc_skc_vec1.txt` are entries of the pqov round3
  KAT files (https://github.com/pqov/pqov/releases/tag/round3, `uov-round3-<set>-KAT.tar.xz`,
  checked against that release's `SHA256SUMS`). The comment lines name the archive, the `.rsp`
  file, the entry count and the DRBG seed. `round3_kat_to_fixtures.py <dir of archives> <out dir>`
  writes them: `sk_seed` is the first 32 bytes of `sk`, the salt is the last 16 bytes of the
  signature, and `sig` is `sm` without the message.
- The other `uov_Is_*` files are copies of the ones in `../ref/`, which were generated from the pqov
  round 2 reference with a fixed seed and salt. uov-Is is the same in rounds 2 and 3, so they still
  apply.
