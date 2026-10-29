# RFC 9980 (Post-Quantum Cryptography in OpenPGP) Appendix A test vectors

The armored blocks of Appendix A of RFC 9980, extracted verbatim from
https://www.rfc-editor.org/rfc/rfc9980.txt (page breaks removed). They are the
non-normative samples the RFC publishes "to help with implementing this
specification". Every message is the signed text `Testing\n`, encrypted to the
certificate of its section and signed by the matching secret key; every detached
signature is over `Testing\n`.

| File | RFC section | Content |
|---|---|---|
| `v6-ed25519-mlkem768x25519-{sk,pk}.asc` | A.1.1, A.1.2 | v6 Ed25519 primary (fingerprint `c789e17d…e947`), v6 ML-KEM-768+X25519 subkey (`dafe0eeb…8b9d`) |
| `v6-ed25519-mlkem768x25519-message.asc` | A.1.3 | v6 PKESK + v2 SEIPD |
| `v4-ed25519-mlkem768x25519-{sk,pk}.asc` | A.2.1, A.2.2 | v4 Ed25519 primary (`342e5db2…ff2d`), v4 ML-KEM-768+X25519 subkey (`e51dbfea…1a51`) - the one PQ(/T) algorithm RFC 9980 sec. 3.5 allows in a v4 key |
| `v4-ed25519-mlkem768x25519-message-seipd1.asc` | A.2.3 | v3 PKESK + v1 SEIPD |
| `v4-ed25519-mlkem768x25519-message-seipd2.asc` | A.2.4 | v6 PKESK + v2 SEIPD |
| `v6-mldsa65ed25519-mlkem768x25519-{sk,pk,message,signature}.asc` | A.3.1 - A.3.4 | ML-DSA-65+Ed25519 primary (`a3e2e14b…242f`), ML-KEM-768+X25519 subkey (`7dae8fbc…65e4`) |
| `v6-mldsa87ed448-mlkem1024x448-{sk,pk,message,signature}.asc` | A.4.1 - A.4.4 | ML-DSA-87+Ed448 primary (`0d7a8be1…ee20`), ML-KEM-1024+X448 subkey (`65090e14…0c8f`) |
| `v6-slhdsa128s-mlkem768x25519-{sk,pk,message,signature}.asc` | A.5.1 - A.5.4 | SLH-DSA-SHAKE-128s primary (`eed4d13f…1d92`), ML-KEM-768+X25519 subkey (`3e8745a4…4bfc`) |
| `v6-slhdsa128f-mlkem768x25519-{sk,pk,signature}.asc` | A.6.1 - A.6.3 | SLH-DSA-SHAKE-128f primary (`d54e0307…37f4`), ML-KEM-768+X25519 subkey (`d8875664…7002`) |
| `v6-slhdsa256s-mlkem1024x448-{sk,pk,signature}.asc` | A.7.1 - A.7.3 | SLH-DSA-SHAKE-256s primary (`72fff848…fa7a`), ML-KEM-1024+X448 subkey (`570a5bba…f15f`) |

The RFC also prints, for each encrypted message, the hex intermediate values of
the key combiner (mlkemKeyShare, ecdhKeyShare, ecdhCipherText, ecdhPublicKey,
the resulting KEK and the session key); the bc-java test
`org.bouncycastle.openpgp.test.PGPRFC9980VectorTest` carries those inline.

The files in the parent directory (`v6-mldsa-65-sample-*.asc`, `v6-slhdsa-*`, …)
predate the RFC and come from an earlier draft-ietf-openpgp-pqc appendix; the
files here are the published ones.
