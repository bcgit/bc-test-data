// Interop generator for bc-java's RFC 9980 OpenPGP support, built on ProtonMail/go-crypto v1.5.2.
//
// Usage: gen <outdir>
//
// For each RFC 9980 key profile it generates a v6 key (and the v4 ML-KEM-768+X25519 variant RFC 9980
// sec. 3.5 allows), writes the armored secret and public key, an encrypted-and-signed message of the
// text "Testing\n" (v6 PKESK + v2 SEIPD, AES-256/OCB) and a detached signature over the same text.
package main

import (
	"bytes"
	"fmt"
	"io"
	"os"
	"path/filepath"

	"github.com/ProtonMail/go-crypto/openpgp/armor"
	"github.com/ProtonMail/go-crypto/openpgp/packet"
	openpgp "github.com/ProtonMail/go-crypto/openpgp/v2"
)

const message = "Testing\n"

type profile struct {
	name string
	algo packet.PublicKeyAlgorithm
	v6   bool
}

var profiles = []profile{
	{"v6-mldsa65ed25519-mlkem768x25519", packet.PubKeyAlgoMldsa65Ed25519, true},
	{"v6-mldsa87ed448-mlkem1024x448", packet.PubKeyAlgoMldsa87Ed448, true},
	{"v6-slhdsa128s-mlkem768x25519", packet.PubKeyAlgoSlhdsaShake128s, true},
	{"v6-slhdsa128f-mlkem768x25519", packet.PubKeyAlgoSlhdsaShake128f, true},
	{"v6-slhdsa256s-mlkem1024x448", packet.PubKeyAlgoSlhdsaShake256s, true},
	// an Ed25519 primary with an ML-KEM-768+X25519 encryption subkey: go-crypto pairs Ed25519 with X25519,
	// so the ML-KEM subkey is added explicitly below
	{"v6-ed25519-mlkem768x25519", packet.PubKeyAlgoEd25519, true},
	{"v4-ed25519-mlkem768x25519", packet.PubKeyAlgoEd25519, false},
}

func armored(blockType string, w func(io.Writer) error) ([]byte, error) {
	buf := new(bytes.Buffer)
	aw, err := armor.Encode(buf, blockType, nil)
	if err != nil {
		return nil, err
	}
	if err := w(aw); err != nil {
		return nil, err
	}
	if err := aw.Close(); err != nil {
		return nil, err
	}
	buf.WriteString("\n")
	return buf.Bytes(), nil
}

func write(dir, name string, data []byte) error {
	return os.WriteFile(filepath.Join(dir, name), data, 0644)
}

func main() {
	if len(os.Args) != 2 {
		fmt.Fprintln(os.Stderr, "usage: gen <outdir>")
		os.Exit(2)
	}
	dir := os.Args[1]
	if err := os.MkdirAll(dir, 0755); err != nil {
		panic(err)
	}
	for _, p := range profiles {
		if err := generate(dir, p); err != nil {
			fmt.Fprintf(os.Stderr, "%s: %v\n", p.name, err)
			os.Exit(1)
		}
		fmt.Println("generated", p.name)
	}
}

func generate(dir string, p profile) error {
	aead := packet.AEADConfig{DefaultMode: packet.AEADModeOCB}
	cfg := &packet.Config{
		Algorithm:     p.algo,
		V6Keys:        p.v6,
		DefaultCipher: packet.CipherAES256,
		DefaultHash:   0, // library default (SHA-256 or better)
		AEADConfig:    &aead,
	}
	entity, err := openpgp.NewEntity("RFC 9980 interop", "", "pqc@example.org", cfg)
	if err != nil {
		return fmt.Errorf("NewEntity: %w", err)
	}
	if p.algo == packet.PubKeyAlgoEd25519 {
		// replace the X25519 encryption subkey go-crypto generated with an ML-KEM-768+X25519 one
		entity.Subkeys = nil
		subCfg := *cfg
		subCfg.Algorithm = packet.PubKeyAlgoMlkem768X25519
		if err := entity.AddEncryptionSubkey(&subCfg); err != nil {
			return fmt.Errorf("AddEncryptionSubkey: %w", err)
		}
	}

	sk, err := armored("PGP PRIVATE KEY BLOCK", func(w io.Writer) error { return entity.SerializePrivate(w, cfg) })
	if err != nil {
		return err
	}
	pk, err := armored("PGP PUBLIC KEY BLOCK", func(w io.Writer) error { return entity.Serialize(w) })
	if err != nil {
		return err
	}
	msg, err := armored("PGP MESSAGE", func(w io.Writer) error {
		pt, err := openpgp.Encrypt(w, []*openpgp.Entity{entity}, nil, []*openpgp.Entity{entity}, nil, cfg)
		if err != nil {
			return err
		}
		if _, err := pt.Write([]byte(message)); err != nil {
			return err
		}
		return pt.Close()
	})
	if err != nil {
		return fmt.Errorf("Encrypt: %w", err)
	}
	sig, err := armored("PGP SIGNATURE", func(w io.Writer) error {
		return openpgp.DetachSign(w, []*openpgp.Entity{entity}, bytes.NewReader([]byte(message)), cfg)
	})
	if err != nil {
		return fmt.Errorf("DetachSign: %w", err)
	}

	for name, data := range map[string][]byte{"-sk.asc": sk, "-pk.asc": pk, "-message.asc": msg, "-signature.asc": sig} {
		if err := write(dir, p.name+name, data); err != nil {
			return err
		}
	}
	return nil
}
