import json
import uuid
from jwcrypto import jwk

def generate_jwk_pair():
    # Benzersiz bir Key ID (kid) oluşturuyoruz
    key_id = str(uuid.uuid4())

    # 1. RSA Anahtarını Üret
    # kty: Key Type (RSA)
    # size: Anahtar boyutu (2048 bit standarttır)
    # use: Kullanım amacı (sig -> signature/imza)
    # alg: Algoritma (RS256)
    key = jwk.JWK.generate(
        kty='RSA',
        size=2048,
        kid=key_id,
        use='sig',
        alg='RS256'
    )

    # 2. Private Key'i Çıkart (Tüm gizli asal çarpanlar dahil: d, p, q vb.)
    private_jwk_str = key.export(private_key=True)
    private_jwk = json.loads(private_jwk_str)

    # 3. Public Key'i Çıkart (Sadece açık bileşenler: n ve e)
    public_jwk_str = key.export(private_key=False)
    public_jwk = json.loads(public_jwk_str)

    return private_jwk, public_jwk

# Fonksiyonu çalıştır ve sonuçları yazdır
private_key, public_key = generate_jwk_pair()

print("--- PRIVATE JWK (Only Auth Service) ---")
print(json.dumps(private_key, indent=2))

print("\n--- PUBLIC JWK (Distribute microservices) ---")
print(json.dumps(public_key, indent=2))
