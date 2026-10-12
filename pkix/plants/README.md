# Merkle Tree Certificates subtree test vectors

`large_inclusion_proofs.json` and `large_consistency_proofs.json` are the large-tree vectors
referenced from Appendix C.2.2 and C.2.3 of draft-ietf-plants-merkle-tree-certs-07, copied
unchanged from the working group repository:

- https://github.com/ietf-plants-wg/merkle-tree-certs/tree/main/demo/large_inclusion_proofs.json
- https://github.com/ietf-plants-wg/merkle-tree-certs/tree/main/demo/large_consistency_proofs.json

Each file is a JSON list of flat objects whose values are decimal strings (`Index`, `Start`,
`End`, `TreeSize`) or base64 (`EntryHash`, `SubtreeHash`, `TreeHash`, `Proof`, the proof being
the SHA-256 hashes concatenated in order). The trees are of size about 2^48, 2^63 and 2^64;
Appendix C.2 lets an implementation skip the sizes it cannot represent.
