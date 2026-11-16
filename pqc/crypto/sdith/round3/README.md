# SDitH known-answer tests, NIST round 3 (SDitH v3.0)

Read by bc-java's `core/src/test/java/org/bouncycastle/pqc/crypto/test/SDitHTest.java` once bc-java implements
round 3. bc-java main implements SDitH v1.1, the hypercube and threshold variants of round 1, and reads the files in
`../`, which can be deleted once bc-java main has the round 3 port.

- `sdith_<set>/PQCsignKAT_<secret key bytes>.rsp` are the 12 NIST response files of the SDitH round 3 submission,
  the VOLE-in-the-Head variant, one for each parameter set. Each has 100 entries, and `sm` is the message followed
  by the signature.
- They are the `.rsp` files in `KAT/` of the round 3 submission package,
  https://csrc.nist.gov/csrc/media/Projects/pqc-dig-sig/documents/sdith3_submission_package.zip (28767034 bytes,
  SHA-256 `3eedaba7c968096634754e9013ce5c50cbeacda33a43355f8a20985b06d34dc5`), unchanged. The `.req` files are left
  out. They are byte-identical to `kat_r3/SDITH_<SET>/` in https://github.com/sdith/sdith at tag `v3.0.0` (commit
  `ce7adf06476674d4ff990f378f83f9981cd6745b`).
- `full_kat_hashes.txt` is `kat_r3/full_kat_hashes.txt` of that tag: the SHA-256 of each file, with the directory
  name in upper case. Check the files in this directory with
  `awk '{print $3"  "tolower($1)"/"$2}' full_kat_hashes.txt | shasum -a 256 -c`.

`<set>` is the category (`cat1`, `cat3` or `cat5`), then `short` or `fast`, then, for half of the sets,
`cipherpow`: those do the proof-of-work grinding with a block cipher (AES-128 for category 1, Rijndael-256 for
categories 3 and 5) instead of SHAKE. The liboqs names are `SDitH3-L<category>-gf2-<short|fast>[-cipherpow]`.

| `<set>` | public key | secret key | signature |
|---|---|---|---|
| `cat1_short`           | 70  | 147 | 3721  |
| `cat1_short_cipherpow` | 70  | 147 | 3721  |
| `cat1_fast`            | 70  | 147 | 4914  |
| `cat1_fast_cipherpow`  | 70  | 147 | 4643  |
| `cat3_short`           | 98  | 208 | 8484  |
| `cat3_short_cipherpow` | 98  | 208 | 8484  |
| `cat3_fast`            | 98  | 208 | 10852 |
| `cat3_fast_cipherpow`  | 98  | 208 | 10452 |
| `cat5_short`           | 132 | 275 | 15147 |
| `cat5_short_cipherpow` | 132 | 275 | 15147 |
| `cat5_fast`            | 132 | 275 | 19144 |
| `cat5_fast_cipherpow`  | 132 | 275 | 19144 |

The lengths are in bytes.
