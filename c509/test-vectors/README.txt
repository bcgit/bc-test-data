C509 test vectors extracted from draft-ietf-cose-c509-test-vectors-04 (2026-11-16), which
aligns with draft-ietf-cose-cbor-encoded-cert-21. The same vectors are published by the
draft's authors at https://github.com/lijun-nio/id-c509-cert-test-vectors.

For each case: <tag>_x509.hex is the DER certificate, <tag>_t3.hex the C509 type 3
(re-encoded) form and <tag>_t2.hex the C509 type 2 (natively signed) form, all hex.
The tag is the draft section number with dots replaced by underscores.

Some cases do not publish all three forms: section 3.4 shares the X.509 certificate of
section 3.3 (so there is no 3_4_x509.hex), section 8 publishes no type 2 form, and
section 7 publishes only the type 2 form (see below).
The type 3 form of section 3.11 has no numbered heading of its own: it sits under an
unrendered "### C509 Type 3 Certificate" line inside section 3.11.2.

Sections 7, 11 and 12.4 use 8-byte integer values that no C509 registry defines, for
the RDN attribute type, extension ID, extended key usage, certificate policy
identifier and CR attribute type. No object identifier is associated with them, so
there is no X.509 counterpart and no type 3 form.

Note: the draft makes elliptic curve point compression OPTIONAL for type 3
(draft-ietf-cose-cbor-encoded-cert sec. 3.2.1 says keys 'may' be point-compressed).
These vectors keep uncompressed Weierstrass points as they appear in the DER, whereas
the main draft's own Appendix A examples compress them (with the 0xfe/0xfd markers).
Both are conformant, so an encoder must be told which form to produce. Section 3.4 is
the sole case published in the compressed form - the certificate of section 3.3 with
its point compressed - so the pair covers both forms of one certificate.

section 2      CA Certificate
section 3.1    RSA Public Key With Public Exponent 65537
section 3.2    RSA Public Key With Public Exponent 4294967297
section 3.3    Weierstrass EC Public Key On Curve secp256r1
section 3.4    Compressed Weierstrass EC Public Key On Curve secp256r1
section 3.5    Weierstrass EC Public Key On Curve secp384r1
section 3.6    Weierstrass EC Public Key On Curve secp521r1
section 3.7    Weierstrass EC Public Key On Curve sm2p256v1
section 3.8    Weierstrass EC Public Key On Curve brainpoolP256r1
section 3.9    Weierstrass EC Public Key On Curve brainpoolP384r1
section 3.10   Weierstrass EC Public Key On Curve brainpoolP512r1
section 3.11   Weierstrass EC Public Key On Curve frp256v1
section 3.12   Montgomery EC Public Key On Curve X25519
section 3.13   Montgomery EC Public Key On Curve X448
section 3.14   Ed25519 Public Key
section 3.15   Ed448 Public Key
section 4.1    RSASSA-PKCS1-v1_5 With SHA-1
section 4.17   RSASSA-PKCS1-v1_5 With SHA-512
section 4.18   RSASSA-PSS With SHA-256
section 4.19   RSASSA-PSS With SHA-384
section 4.20   RSASSA-PSS With SHA-512
section 4.21   RSASSA-PSS With SHAKE128
section 4.22   RSASSA-PSS With SHAKE256
section 7      Certificate With 8-Byte Integer Values
section 8      X.509 Certificate With Unconvertible RDN Attributes And Extensions

Certification requests (section 9): <tag>_pkcs10.hex is the DER RFC 2986 request,
<tag>_t3.hex the C509 type 3 form and <tag>_t2.hex the C509 type 2 form. Section 9.2
publishes no type 2 form. Sections 9.3 to 9.5 use the RFC 6955 proof-of-possession
algorithms, whose "signature" is a DhSigStatic, in both its bare hashValue byte string
and its [issuer, serial, hashValue] array forms. Sections 9.6 and 9.7 carry the RFC 9883
privateKeyPossessionStatement attribute, 9.7 with the signature certificate embedded
(as a type 3 C509 certificate in the type 3 request, type 2 in the type 2 request).
Section 11 (11_t2.hex) is a type 2 request with an 8-byte CR attribute type.

section 9.1    ECDSA With SHA-256
section 9.2    ECDSA With SHA-384
section 9.3    PoP With SHA-256 And HMAC-SHA256
section 9.4    PoP With SHA-384 And HMAC-SHA384
section 9.5    PoP With SHA-512 And HMAC-SHA512
section 9.6    Unsigned PoP With X25519 Key
section 9.7    Unsigned PoP With X25519 Key And Cert
section 11     Certification Request With 8-Byte Identifiers

Certification request templates (section 12): <tag>_template.hex.

section 12.1   All Fields Set to "undefined" Where Possible
section 12.2   With One Element in Each Field
section 12.3   Complex Template
section 12.4   With 8-Byte Integer Values
