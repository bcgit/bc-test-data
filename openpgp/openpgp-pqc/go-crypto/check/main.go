// Interop checker for bc-java's RFC 9980 OpenPGP support, built on ProtonMail/go-crypto v1.5.2.
//
// Usage: check <secret-key.asc> <message.asc> <signature.asc>
//
// Reads the armored secret key, decrypts the message with it and verifies its embedded signature
// against the same key, then verifies the detached signature over the recovered plaintext.
package main

import (
	"bytes"
	"fmt"
	"io"
	"os"

	"github.com/ProtonMail/go-crypto/openpgp/armor"
	"github.com/ProtonMail/go-crypto/openpgp/packet"
	openpgp "github.com/ProtonMail/go-crypto/openpgp/v2"
)

func main() {
	if len(os.Args) != 4 {
		fmt.Fprintln(os.Stderr, "usage: check <secret-key.asc> <message.asc> <signature.asc>")
		os.Exit(2)
	}
	keyFile, err := os.Open(os.Args[1])
	must(err)
	keyring, err := openpgp.ReadArmoredKeyRing(keyFile)
	must(err)

	cfg := &packet.Config{}

	msgFile, err := os.Open(os.Args[2])
	must(err)
	block, err := armor.Decode(msgFile)
	must(err)
	md, err := openpgp.ReadMessage(block.Body, keyring, nil, cfg)
	must(err)
	plain, err := io.ReadAll(md.UnverifiedBody)
	must(err)
	if !md.IsEncrypted {
		fail("message is not encrypted")
	}
	if !md.IsSigned {
		fail("message is not signed")
	}
	if md.SignatureError != nil {
		fail("embedded signature: " + md.SignatureError.Error())
	}
	fmt.Printf("decrypted %q, embedded signature by %X verified\n", string(plain), md.SignedBy.PublicKey.Fingerprint)

	sigFile, err := os.Open(os.Args[3])
	must(err)
	sigBlock, err := armor.Decode(sigFile)
	must(err)
	_, signer, err := openpgp.VerifyDetachedSignature(keyring, bytes.NewReader(plain), sigBlock.Body, cfg)
	must(err)
	fmt.Printf("detached signature by %X verified\n", signer.PrimaryKey.Fingerprint)
}

func must(err error) {
	if err != nil {
		fail(err.Error())
	}
}

func fail(msg string) {
	fmt.Fprintln(os.Stderr, "FAIL:", msg)
	os.Exit(1)
}
