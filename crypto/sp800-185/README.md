# NIST SP 800-185 sample values

The `.rsp` files in this directory are the published sample values for the SHA-3 derived functions
of **NIST Special Publication 800-185** — cSHAKE, KMAC and TupleHash.

They are not Bouncy Castle work. See the attribution below before copying them onward.

## Attribution

| | |
|---|---|
| Specification | NIST SP 800-185, *SHA-3 Derived Functions: cSHAKE, KMAC, TupleHash and ParallelHash* (December 2016) |
| Authors | John Kelsey, Shu-jen Chang, Ray Perlner (NIST) |
| Specification DOI | <https://doi.org/10.6028/NIST.SP.800-185> |
| Sample values | NIST Cryptographic Standards and Guidelines, "Examples with Intermediate Values" |
| Upstream | <https://csrc.nist.gov/Projects/Cryptographic-Standards-and-Guidelines/example-values> |
| Copyright | Not subject to copyright in the United States. NIST asks that attribution be given. |
| Retrieved | 2026-09-07 |

## Contents

| File | Vectors | Defined in |
|---|---|---|
| `cSHAKE.rsp` | 4 | SP 800-185 Sec 3.3 |
| `KMAC.rsp` | 6 | SP 800-185 Sec 4.3 |
| `KMACXOF.rsp` | 6 | SP 800-185 Sec 4.3.1 |
| `TupleHash.rsp` | 6 | SP 800-185 Sec 5.3 |
| `TupleHashXOF.rsp` | 6 | SP 800-185 Sec 5.3.1 |
| `ParallelHash.rsp` | 6 | SP 800-185 Sec 6.3 |
| `ParallelHashXOF.rsp` | 6 | SP 800-185 Sec 6.3.1 |

## Format

Plain-text records in the style of the NIST CAVP `.rsp` files already in `crypto/sha3/`: blank-line
separated blocks of `Key = value`, opening with `COUNT`. Lines beginning with `#` are comments.

| Field | Meaning |
|---|---|
| `Strength` | 128 or 256 — selects cSHAKE128/cSHAKE256, KMAC128/KMAC256, and so on |
| `N` | the function-name string, as characters (cSHAKE only; empty in every published sample) |
| `S` | the customization string, as characters; empty means no customization |
| `Key` | the key, in hex (KMAC only) |
| `Msg` | the message, in hex |
| `B` | the parallel block size, **in bytes** (ParallelHash only) |
| `Count` | the number of tuple elements (TupleHash only) |
| `Tuple1`..`TupleN` | the tuple elements, in hex (TupleHash only) |
| `Outputlen` | the requested output length, **in bits** |
| `Output` | the expected output, in hex |

Lengths follow the specification and are counted in bits. `N` and `S` are character strings rather
than hex, matching how the sample files present them; an empty value means the empty string, which
for cSHAKE is the case where the function is defined to equal plain SHAKE.

## How these were produced

The upstream sample values are PDFs containing full intermediate state dumps — the sponge state
before and after each permutation — which is far more than a test harness needs and is not a format
any project here parses. The parameters and the final `Outval` of each sample were extracted into
the `.rsp` files above. No values were altered.

The source documents, under
`https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/`:

| Document | SHA-256 |
|---|---|
| `cSHAKE_samples.pdf` | `49fbf71bed8b6dd8069720250b43ce029706695b6586c201270d76889b41873e` |
| `KMAC_samples.pdf` | `445ee87689670da2bee611e88b765f22a43b4155295fd7f1cddea0ac671b24e1` |
| `KMACXOF_samples.pdf` | `ba8432a008c998f6d27013bc7dbeabc793c114048da36861a285776200fc2959` |
| `TupleHash_samples.pdf` | `7d4491c28e6dcf4751975e6f15ec73c8b4a676a3b653d524e7561e3141cc6465` |
| `TupleHashXOF_samples.pdf` | `c3be9c3891bfad850347ff5664e504cbbd832c4d942b98aec48555a1a390678c` |
| `ParallelHash_samples.pdf` | `4e1a6ff692f68bdebabbbcbf0596f6d35ec39636c9ab71dc434f5d47c5634d80` |
| `ParallelHashXOF_samples.pdf` | `c9d460ea1c8fcc8032f043fe0861b248fae1522f19c3d14935ccbb0a693e4ca6` |

To refresh, re-download those documents, confirm the checksums, and re-extract the `Outval` of each
`Sample #n` together with its parameters.

One trap when re-extracting: the hex blocks are split across page boundaries, and the continuation
line then begins with a form feed rather than spaces. A naive "indented hex lines" pattern stops at
the break and silently truncates the value, which is easy to miss because the result is still
well-formed hex. Check every extracted value against its declared length -- `Outputlen`, `Length of
data is`, `Length of Key is` -- rather than only that it parses.

## Independent check

Every value here was compared against the expected digests asserted in bc-java's own test suite,
which was written from the specification independently:

| File | Confirmed by | |
|---|---|---|
| `cSHAKE.rsp` | `CSHAKETest` | 2 of 4 |
| `KMAC.rsp` | `KMACTest` | 6 of 6 |
| `TupleHash.rsp` | `TupleHashTest` | 6 of 6 |
| `ParallelHash.rsp` | `ParallelHashTest` | 6 of 6 |

20 of the 22 fixed-length outputs match. The two that do not appear are the cSHAKE256 samples:
bc-java's `CSHAKETest` exercises only the two cSHAKE128 ones, so there is nothing there to compare
them with. They come from the same NIST document as the rest and were checked against their
declared output lengths.

The XOF variants (`KMACXOF`, `TupleHashXOF`, `ParallelHashXOF`) have no corresponding fixed
assertions in bc-java and were checked against their declared lengths only.
