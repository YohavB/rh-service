#!/bin/bash

# Firebase Environment Variable Extractor
# This script helps extract Firebase service account credentials from a JSON file
# and format them as environment variables

if [ $# -eq 0 ]; then
    echo "Usage: $0 <path-to-firebase-service-account.json>"
    echo ""
    echo "This script will extract Firebase credentials from your service account JSON file"
    echo "and output them as environment variables that you can use in your .env files."
    echo ""
    echo "Example:"
    echo "  $0 ~/Downloads/rushhour-firebase-adminsdk.json"
    exit 1
fi

JSON_FILE="$1"

if [ ! -f "$JSON_FILE" ]; then
    echo "Error: File '$JSON_FILE' not found!"
    exit 1
fi

echo "# Firebase Environment Variables"
echo "# Copy these to your env.prod or env.local file"
echo ""

# Extract and format the Firebase environment variables
echo "FIREBASE_PROJECT_ID=$(jq -r '.project_id' "$JSON_FILE")"
echo "FIREBASE_PRIVATE_KEY_ID=$(jq -r '.private_key_id' "$JSON_FILE")"
echo "FIREBASE_PRIVATE_KEY=\"$(jq -r '.private_key' "$JSON_FILE" | sed 's/$/\\n/g' | tr -d '\n')\""
echo "FIREBASE_CLIENT_EMAIL=$(jq -r '.client_email' "$JSON_FILE")"
echo "FIREBASE_CLIENT_ID=$(jq -r '.client_id' "$JSON_FILE")"
echo "FIREBASE_AUTH_URI=$(jq -r '.auth_uri' "$JSON_FILE")"
echo "FIREBASE_TOKEN_URI=$(jq -r '.token_uri' "$JSON_FILE")"
echo "FIREBASE_AUTH_PROVIDER_X509_CERT_URL=$(jq -r '.auth_provider_x509_cert_url' "$JSON_FILE")"
echo "FIREBASE_CLIENT_X509_CERT_URL=$(jq -r '.client_x509_cert_url' "$JSON_FILE")"

echo ""
echo "# Copy the above lines to your environment file"
echo "# Then run: source env.prod  # or source env.local"
