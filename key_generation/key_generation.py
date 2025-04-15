import secrets
import string
import uuid
import base64

def generate_client_credentials():
    # Generate a client ID (using UUID v4)
    client_id = str(uuid.uuid4())

    # Generate a client secret (random string of 40 characters)
    alphabet = string.ascii_letters + string.digits
    client_secret = ''.join(secrets.choice(alphabet) for _ in range(40))

    return client_id, client_secret

# Alternative method using base64-encoded random bytes
def generate_client_credentials_base64():
    # Generate client ID (32 bytes, base64 encoded)
    client_id_bytes = secrets.token_bytes(24)
    client_id = base64.urlsafe_b64encode(client_id_bytes).decode('utf-8').rstrip('=')

    # Generate client secret (48 bytes, base64 encoded)
    client_secret_bytes = secrets.token_bytes(36)
    client_secret = base64.urlsafe_b64encode(client_secret_bytes).decode('utf-8').rstrip('=')

    return client_id, client_secret

# Generate and display credentials using both methods
uuid_id, uuid_secret = generate_client_credentials()
b64_id, b64_secret = generate_client_credentials_base64()

print("Method 1 (UUID + random string):")
print(f"Client ID: {uuid_id}")
print(f"Client Secret: {uuid_secret}")
print("\nMethod 2 (Base64 encoded random bytes):")
print(f"Client ID: {b64_id}")
print(f"Client Secret: {b64_secret}")
