# go-crypto (ProtonMail) interop programs for RFC 9980

Two small Go programs against `github.com/ProtonMail/go-crypto` v1.5.2, which
implements RFC 9980 (ML-KEM, ML-DSA and SLH-DSA in OpenPGP, go-crypto issue #316):

- `gen/main.go` - `go run ./gen <outdir>` generates, for every RFC 9980 key profile
  (ML-DSA-65+Ed25519, ML-DSA-87+Ed448, the three SLH-DSA sets, each with the matching
  ML-KEM subkey, plus a v6 and a v4 Ed25519 key with an ML-KEM-768+X25519 subkey), the
  armored secret and public key, an encrypted-and-signed message of `Testing\n`
  (v6 PKESK, v2 SEIPD, AES-256/OCB) and a detached signature over `Testing\n`:
  `<profile>-{sk,pk,message,signature}.asc`.
- `check/main.go` - `go run ./check <sk.asc> <message.asc> <signature.asc>` decrypts a
  message with the key, verifies its embedded signature, and verifies the detached
  signature over the recovered plaintext - the acceptance check for bc-java output.

Run from this directory (`go.mod` and `go.sum` pin go-crypto v1.5.2 and its dependencies).

`out/` holds the generator's output for all seven profiles, produced with go-crypto v1.5.2
on 2026-10-30 (`go run ./gen out`): 28 files, four per profile. A bc-java test that reads
them in the manner of `PGPRFC9980VectorTest` is still to be written; see the bc-java
release notes for the status of the interop run.
