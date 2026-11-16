"""Build bc-test-data UOV fixtures from the pqov round3 KAT archives.

Streams PQCsignKAT_*.rsp out of each uov-round3-<set>-KAT.tar.xz, takes the
entries named in WANT, and writes key=value fixtures in the format UOVTest reads.
sk_seed is the first 32 bytes of sk (classic/pkc sk = sk_seed || O || P1 || S,
pkc-skc sk = sk_seed), the salt is the last 16 bytes of sm (sig = w || salt),
and sig = sm[mlen:].
"""
import sys
import tarfile

KATS = sys.argv[1]
OUT = sys.argv[2]

SIGLEN = {"Is": 96, "Ip": 135, "III": 209, "V": 275}

# set name -> list of counts to keep
WANT = {
    "Ip": [0, 1], "Ip-pkc": [0], "Ip-pkc-skc": [0],
    "III": [0], "III-pkc": [0], "III-pkc-skc": [0],
    "V": [0], "V-pkc": [0], "V-pkc-skc": [0],
    "Is-pkc-skc": [0],
}


def entries(f, last):
    cur = {}
    for raw in f:
        line = raw.decode("ascii").strip()
        if not line or line.startswith("#"):
            continue
        k, _, v = line.partition("=")
        k = k.strip()
        v = v.strip()
        if k == "count":
            if cur:
                yield cur
            if int(v) > last:
                return
            cur = {"count": int(v)}
        else:
            cur[k] = v
    if cur:
        yield cur


for name, counts in WANT.items():
    arch = "uov-round3-%s-KAT.tar.xz" % name
    with tarfile.open("%s/%s" % (KATS, arch), "r|xz") as tf:
        for member in tf:
            if not member.name.endswith(".rsp"):
                continue
            rsp = member.name
            f = tf.extractfile(member)
            for e in entries(f, max(counts)):
                if e["count"] not in counts:
                    continue
                mlen = int(e["mlen"])
                msg = bytes.fromhex(e["msg"])
                sm = bytes.fromhex(e["sm"])
                sk = bytes.fromhex(e["sk"])
                assert len(msg) == mlen, name
                assert int(e["smlen"]) == len(sm), name
                assert sm[:mlen] == msg, name
                sig = sm[mlen:]
                level = name.split("-")[0]
                assert len(sig) == SIGLEN[level], (name, len(sig))
                if name.endswith("pkc-skc"):
                    assert len(sk) == 32, name
                fname = "uov_%s_vec%d.txt" % (name.replace("-", "_"), e["count"])
                with open("%s/%s" % (OUT, fname), "w") as o:
                    o.write("# pqov round3 KAT, %s %s, count %d\n" % (arch, rsp, e["count"]))
                    o.write("# seed %s\n" % e["seed"])
                    o.write("sk_seed=%s\n" % sk[:32].hex())
                    o.write("salt=%s\n" % sig[-16:].hex())
                    o.write("msg=%s\n" % e["msg"].lower())
                    o.write("pk=%s\n" % e["pk"].lower())
                    o.write("sk=%s\n" % e["sk"].lower())
                    o.write("sig=%s\n" % sig.hex())
                print(fname, "pk", len(e["pk"]) // 2, "sk", len(sk), "sig", len(sig))
            break
