NODE_ENV = ENV['NODE_ENV']
CLIENT_ID = ENV['CLIENT_ID']
CLIENT_SECRET = ENV['CLIENT_SECRET']

is_prod = NODE_ENV == "production"

SAMPLE_API_BASE = is_prod ? "https://api.paypal.com" : "https://api.sandbox.paypal.com"

IsProd = is_prod
SAMPLE_API_BASE = SAMPLE_API_BASE
CLIENT_ID = CLIENT_ID
CLIENT_SECRET = CLIENT_SECRET
