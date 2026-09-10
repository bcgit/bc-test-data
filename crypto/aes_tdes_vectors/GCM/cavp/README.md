# NIST CAVP GCM test vectors (`gcmtestvectors.zip`)

The six `.rsp` files in this directory are the **NIST Cryptographic Algorithm Validation Program
(CAVP)** known-answer vectors for AES-GCM, redistributed here with no change other than git's
line-ending normalization (see below) so that the Bouncy Castle projects can run them alongside their other common test data. They are the
CAVS-format predecessors of the ACVP JSON files in the parent directory and cover far more input
shapes than those do.

## Provenance

| | |
|---|---|
| Upstream | <https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Algorithm-Validation-Program/documents/mac/gcmtestvectors.zip> |
| Published by | NIST Computer Security Resource Center, CAVP "Block Cipher Modes" page |
| Generator | CAVS 14.0, files dated 2012-08-31 (see the `#` header lines) |
| Specification | NIST SP 800-38D, *Recommendation for Block Cipher Modes of Operation: Galois/Counter Mode (GCM) and GMAC*, November 2007 |
| Retrieved | 2026-09-10 |
| Zip SHA-256 | `f9fc479e134cde2980b3bb7cddbcb567b2cd96fd753835243ed067699f26a023` |
| Copyright | Work of the United States Government, not subject to copyright in the United States (17 U.S.C. 105). NIST asks for attribution. |

The zip contained exactly these six files and nothing else; it was extracted with `unzip` and no
file was edited. The files in the zip have CRLF line endings; this repository stores text
LF-normalized (as it does the TDES `.rsp` set next door), so a checkout has LF endings unless
`core.autocrlf` re-adds CR on the way out. Parsers should trim both.

## What is in the files

Each file has 7875 cases in 105 sections. A section header gives the parameters in **bits**:

```
[Keylen = 128]
[IVlen = 96]
[PTlen = 0]
[AADlen = 0]
[Taglen = 128]
```

followed by 75 records of `Count = / Key = / IV = / PT = / AAD = / CT = / Tag =` (encrypt files) or
`Count = / Key = / IV = / CT = / AAD = / Tag =` followed by either `PT = ...` or the single word `FAIL`
(decrypt files). Every hex value in every file has been checked against the section's declared
length; there are no mismatches.

| File | Direction | Cases | `FAIL` cases |
|---|---|---|---|
| `gcmEncryptExtIV128.rsp` | encrypt, AES-128 | 7875 | -- |
| `gcmEncryptExtIV192.rsp` | encrypt, AES-192 | 7875 | -- |
| `gcmEncryptExtIV256.rsp` | encrypt, AES-256 | 7875 | -- |
| `gcmDecrypt128.rsp` | decrypt, AES-128 | 7875 | 4011 |
| `gcmDecrypt192.rsp` | decrypt, AES-192 | 7875 | 3978 |
| `gcmDecrypt256.rsp` | decrypt, AES-256 | 7875 | 3919 |

Parameter coverage, identical in all six files (each combination has 75 cases):

| Parameter | Values (bits) |
|---|---|
| `IVlen` | 8, 96, 1024 |
| `Taglen` | 32, 64, 96, 104, 112, 120, 128 |
| `PTlen` | 0, 104, 128, 256, 408 |
| `AADlen` | 0, 128, 160, 384, 720 |

So 2625 cases per file have the 96-bit IV that SP 800-38D Sec 5.2.1.1 recommends, and of those 1875
have a tag of 96 bits or more. `PTlen = 0` with `AADlen > 0` is GMAC. `PTlen = 104` and `408` end
in a partial block; `408` is three blocks and a 7-byte tail. An implementation that supports only
96-bit IVs or only tags of 96 bits and up should skip the other sections and report the count, not
drop them silently.

"ExtIV" means the IV is supplied externally by the test; there is no internal-IV-generation file
in this set.

## Verifying what is here

As stored in this repository (LF line endings -- what `git cat-file -p` and an `autocrlf=input`
checkout give you):

```
sha256sum -c <<'SUMS'
151d24c10d999e41aa121627992bdce37aa8d1a55313274decb3455017f20768  gcmDecrypt128.rsp
5baf60379f70f1f2fba449c2dbfdafb63a4c01b24765b5cd585042c7cb89a124  gcmDecrypt192.rsp
5d9d15f459824a78370ad73e1c0104f0e7763ede0040eeece6be84de02266fea  gcmDecrypt256.rsp
200a0ac542bda39dab5432934a443c5b82126c4622990cf2abd932c46ec4d52a  gcmEncryptExtIV128.rsp
168af5f4a4d2b44cf6183fea6f2793ddfa6798d9444e0470cd105ff9e366f171  gcmEncryptExtIV192.rsp
7fea14302354ec6f943409a1af1f34e91c4fa355f4768f46671024223f13ee33  gcmEncryptExtIV256.rsp
SUMS
```

As extracted from the zip (CRLF line endings), for checking a fresh download against NIST:

```
sha256sum -c <<'SUMS'
0d1f98d0c9e176ede1f3e85484dd13de92c3436ca97df378ec3481e04c19a732  gcmDecrypt128.rsp
ca65640844441d58828e29bff87b7ec4571ef6778f4c331fc06697264edb2ffd  gcmDecrypt192.rsp
ed318735a517d5a85c82d2846c23dcf56e57a352fb0f0a163d9e7617a7bd12ad  gcmDecrypt256.rsp
30156236f768b0d81e58a3e5bd3bd79ce294cf9ef0ba0a84cf7b869c89ebd9fd  gcmEncryptExtIV128.rsp
86827942bf67d1c67f34532bd057adf0a41cd911396a36023d9033f89fc6f571  gcmEncryptExtIV192.rsp
4448b4998c3f58cd9f5542ddf7c573189a721ff3d3ac24cd5177bb4910a787f8  gcmEncryptExtIV256.rsp
SUMS
```

## Used by

bc-rust `crypto/modes/tests/cavp_gcm_tests.rs` (planned with the GCM implementation).
