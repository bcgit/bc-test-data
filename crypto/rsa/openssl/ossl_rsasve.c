/* OpenSSL RSASVE (EVP_PKEY_encapsulate, kemop "RSASVE") cross-check vector generator.
 * gen <bits> <count> : new RSA key (e=65537), print CRT components as JSON-ish lines, then
 *                      <count> OpenSSL encapsulations (C, Z).
 * decap <pem> <hexC> : decapsulate hexC with the PEM private key, print Z. */
#include <openssl/evp.h>
#include <openssl/pem.h>
#include <openssl/core_names.h>
#include <openssl/bn.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
static void hex(const unsigned char *b, size_t n) { for (size_t i = 0; i < n; i++) printf("%02X", b[i]); }
static void bnp(EVP_PKEY *k, const char *name, const char *label, int pad) {
    BIGNUM *bn = NULL; EVP_PKEY_get_bn_param(k, name, &bn);
    unsigned char buf[1024]; BN_bn2binpad(bn, buf, pad);
    printf("\"%s\": \"", label); hex(buf, pad); printf("\",\n"); BN_free(bn);
}
int main(int argc, char **argv) {
    if (argc == 4 && !strcmp(argv[1], "gen")) {
        int bits = atoi(argv[2]), count = atoi(argv[3]), nlen = bits / 8;
        EVP_PKEY *k = EVP_RSA_gen(bits);
        FILE *f = fopen("key.pem", "w"); PEM_write_PrivateKey(f, k, NULL, NULL, 0, NULL, NULL); fclose(f);
        printf("{\"bits\": %d,\n", bits);
        bnp(k, OSSL_PKEY_PARAM_RSA_N, "n", nlen);
        printf("\"e\": 65537,\n");
        bnp(k, OSSL_PKEY_PARAM_RSA_FACTOR1, "p", nlen / 2);
        bnp(k, OSSL_PKEY_PARAM_RSA_FACTOR2, "q", nlen / 2);
        bnp(k, OSSL_PKEY_PARAM_RSA_EXPONENT1, "dP", nlen / 2);
        bnp(k, OSSL_PKEY_PARAM_RSA_EXPONENT2, "dQ", nlen / 2);
        bnp(k, OSSL_PKEY_PARAM_RSA_COEFFICIENT1, "qInv", nlen / 2);
        printf("\"encapsulations\": [\n");
        for (int i = 0; i < count; i++) {
            EVP_PKEY_CTX *c = EVP_PKEY_CTX_new(k, NULL);
            if (EVP_PKEY_encapsulate_init(c, NULL) <= 0 || EVP_PKEY_CTX_set_kem_op(c, "RSASVE") <= 0) return 1;
            size_t ctl = 0, zl = 0; EVP_PKEY_encapsulate(c, NULL, &ctl, NULL, &zl);
            unsigned char ct[1024], z[1024];
            if (EVP_PKEY_encapsulate(c, ct, &ctl, z, &zl) <= 0) return 2;
            printf("  {\"C\": \""); hex(ct, ctl); printf("\", \"Z\": \""); hex(z, zl); printf("\"}%s\n", i + 1 < count ? "," : "");
            EVP_PKEY_CTX_free(c);
        }
        printf("]}\n");
        return 0;
    }
    if (argc == 4 && !strcmp(argv[1], "decap")) {
        FILE *f = fopen(argv[2], "r"); EVP_PKEY *k = PEM_read_PrivateKey(f, NULL, NULL, NULL); fclose(f);
        size_t cl = strlen(argv[3]) / 2; unsigned char ct[1024], z[1024];
        for (size_t i = 0; i < cl; i++) sscanf(argv[3] + 2 * i, "%2hhx", &ct[i]);
        EVP_PKEY_CTX *c = EVP_PKEY_CTX_new(k, NULL);
        if (EVP_PKEY_decapsulate_init(c, NULL) <= 0 || EVP_PKEY_CTX_set_kem_op(c, "RSASVE") <= 0) return 1;
        size_t zl = 0;
        if (EVP_PKEY_decapsulate(c, NULL, &zl, ct, cl) <= 0 || zl > sizeof z) return 4;
        if (EVP_PKEY_decapsulate(c, z, &zl, ct, cl) <= 0) { fprintf(stderr, "decap failed\n"); return 3; }
        hex(z, zl); printf("\n"); return 0;
    }
    fprintf(stderr, "usage\n"); return 9;
}
