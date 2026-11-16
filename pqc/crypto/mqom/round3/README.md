# MQOM known-answer tests, NIST round 3 (MQOM v3)

Read by bc-java's `core/src/test/java/org/bouncycastle/pqc/crypto/test/MQOMKatTest.java` once bc-java implements
MQOM v3. Until then it reads the MQOM v2.1 files in `../kat/`, which can be deleted once bc-java main has the v3 port.

- `mqom3_<set>/PQCsignKAT_<secret key bytes>.rsp` are the 18 NIST response files of the MQOM round 3 submission, one
  for each parameter set. Each has 100 entries, and `sm` is the message followed by the signature.
- They are the `.rsp` files in `KAT/` of the round 3 submission package,
  https://csrc.nist.gov/csrc/media/Projects/pqc-dig-sig/documents/mqom3_submission_package.zip (51601327 bytes,
  SHA-256 `e01f8e53bba57d9e355f3f524e6d88eb868092fd47cb36611686ae703b8aeb92`), unchanged. The `.req` files are left
  out.
- The source repository, https://github.com/mqom/mqom-v3, has no KAT files. liboqs, which takes its MQOM code from
  there, keeps a SHA-256 of each set's KAT in `tests/KATs/sig/kats.json`, computed over the `count` to `sm` lines
  without the `#` line; mqom-v3's `integration/liboqs/kats.json` has the same values. These files give those hashes.

`<set>` is `<category>_<field>_<trade-off>_<variant>`: category `cat1`, `cat3` or `cat5`; field `gf2` or `gf16`;
trade-off `shorter` (gf2 only), `fast` or `short` (gf16 only); variant `ct` (correlated trees) or `ot` (one tree).
None of the 36 v2.1 parameter sets is kept in v3.

| `<set>` | category | public key | secret key | signature |
|---|---|---|---|---|
| `cat1_gf16_fast_ct`   | 1 | 64  | 96  | 3316  |
| `cat1_gf16_fast_ot`   | 1 | 64  | 96  | 3316  |
| `cat1_gf16_short_ct`  | 1 | 64  | 96  | 2932  |
| `cat1_gf16_short_ot`  | 1 | 64  | 96  | 2932  |
| `cat1_gf2_shorter_ct` | 1 | 52  | 72  | 2492  |
| `cat1_gf2_shorter_ot` | 1 | 52  | 72  | 2492  |
| `cat3_gf16_fast_ct`   | 3 | 96  | 144 | 7564  |
| `cat3_gf16_fast_ot`   | 3 | 96  | 144 | 7660  |
| `cat3_gf16_short_ct`  | 3 | 96  | 144 | 6556  |
| `cat3_gf16_short_ot`  | 3 | 96  | 144 | 6556  |
| `cat3_gf2_shorter_ct` | 3 | 78  | 108 | 5932  |
| `cat3_gf2_shorter_ot` | 3 | 78  | 108 | 5890  |
| `cat5_gf16_fast_ct`   | 5 | 128 | 192 | 13540 |
| `cat5_gf16_fast_ot`   | 5 | 128 | 192 | 13380 |
| `cat5_gf16_short_ct`  | 5 | 128 | 192 | 12100 |
| `cat5_gf16_short_ot`  | 5 | 128 | 192 | 11716 |
| `cat5_gf2_shorter_ct` | 5 | 104 | 144 | 10836 |
| `cat5_gf2_shorter_ot` | 5 | 104 | 144 | 10804 |

The lengths are in bytes.
