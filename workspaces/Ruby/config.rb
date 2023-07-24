require 'rubygems'
require 'dotenv/load'

NODE_ENV = ENV['NODE_ENV']
CLIENT_ID = ENV['CLIENT_ID']
CLIENT_SECRET = ENV['CLIENT_SECRET']
is_prod = NODE_ENV == "production"

PAYPAL_API_BASE = is_prod ? "https://api.paypal.com" : "https://api.sandbox.paypal.com"

