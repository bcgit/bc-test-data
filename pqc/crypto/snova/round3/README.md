# SNOVA known-answer tests, NIST round 3

Read by bc-java's `core/src/test/java/org/bouncycastle/pqc/crypto/test/SnovaTest.java` once bc-java implements
round 3. Until then it reads the round 2 files in `../`, which can be deleted once bc-java main has the round 3 port.

- `PQCsignKAT_SNOVA_<set>.rsp` and `PQCsignKAT_SNOVA_<set>_AES.rsp` are the 18 NIST response files of the SNOVA
  round 3 submission: one for each of the 9 parameter sets, with the public key expanded by SHAKE (no suffix) or by
  AES (`_AES`). Each has 100 entries, and `sm` is the signature followed by the message.
- They are `SNOVA_<set>[_AES]/PQCsignKAT_SNOVA_<set>[_AES].rsp` in https://github.com/PQCLAB-SNOVA/SNOVA_KAT at commit
  `0633b84469c0c4bc5997dbea346dc1801467094c` ("SNOVA Round 3 KAT files"), unchanged. The `.req` files are left out.
- `KAT-SHAKE256` is the file of the same name in that repository, and the same as `round3/KAT-SHAKE256` in
  https://github.com/PQCLAB-SNOVA/NIST_Submissions: the first 24 bytes of the SHAKE256 of each `.req` and `.rsp` file.
  Check the `.rsp` files in this directory with
  `python3 -c "import hashlib; [print(n, 'OK' if hashlib.shake_256(open('PQCsignKAT_'+n,'rb').read()).hexdigest(24)==h else 'FAIL') for n,h in (l.split() for l in open('KAT-SHAKE256')) if n.endswith('.rsp')]"`.
- The reference implementation of the submission (`round3/core` in NIST_Submissions) does not ship the KAT files. Its
  `ref_kat/KAT_DIGESTS.sha256` gives their SHA-256, naming each set by its 7-tuple (table below), and these files
  match it once their first line, `# SNOVA_<set>[_AES]`, names the set by its tuple instead.

Round 3 replaces every round 2 parameter set. The secret key is 96 bytes in every set:

| `<set>` | category | (v, o, q, l, r, m1, m2) | public key | signature |
|---|---|---|---|---|
| `I_K`   | 1 | (29, 3, 16, 4, 8, 5, 96)  | 376  | 528 |
| `I_B`   | 1 | (27, 4, 16, 4, 6, 5, 96)  | 656  | 388 |
| `I_S`   | 1 | (27, 5, 16, 4, 4, 5, 80)  | 1016 | 272 |
| `III_K` | 3 | (38, 4, 16, 4, 8, 7, 128) | 912  | 688 |
| `III_B` | 3 | (38, 5, 16, 4, 6, 7, 120) | 1416 | 532 |
| `III_S` | 3 | (38, 6, 16, 4, 5, 7, 120) | 2032 | 456 |
| `V_K`   | 5 | (40, 4, 16, 5, 8, 6, 160) | 1216 | 896 |
| `V_B`   | 5 | (40, 5, 16, 5, 6, 6, 150) | 1891 | 691 |
| `V_S`   | 5 | (40, 6, 16, 5, 5, 6, 150) | 2716 | 591 |

The lengths are in bytes and are the same for the `_AES` files.
