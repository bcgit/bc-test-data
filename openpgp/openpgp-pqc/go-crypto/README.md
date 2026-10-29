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

Run from this directory (`go.mod` pins the go-crypto version; `go mod tidy` fetches it).
bc-java's `PGPRFC9980VectorTest` style loader can read the generated files from an
`openpgp/openpgp-pqc/go-crypto/out` directory once they are committed; see the bc-java
release notes for the status of that interop run.
