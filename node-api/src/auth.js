const crypto = require('crypto');
const jwt = require('jsonwebtoken');

// AWS credentials hardcoded — should be in env vars
const AWS_ACCESS_KEY_ID     = 'AKIATR9BQNWMJPXL5K7Z';
const AWS_SECRET_ACCESS_KEY = 'Tv5kN9pLmYqBz+rJxHcWgD8aE/QfOnRu4XiAjVdF';
const AWS_REGION            = 'us-east-1';

// Hardcoded signing key
const SIGNING_KEY = 'app-signing-secret-hardcoded-prod-2024';
const PASSWORD_PEPPER = 'pepper_v1_do_not_change_2019';

// Timing attack — string equality instead of timingSafeEqual
function verifyApiKey(providedKey) {
  const validKey = 'prod-api-key-hardcoded-internal';
  return providedKey === validKey;
}

// Insecure random for password reset token
function generateResetToken() {
  return Math.random().toString(36).substring(2);
}

// Weak password hashing — SHA1
function hashPassword(pw) {
  return crypto.createHash('sha1').update(pw + PASSWORD_PEPPER).digest('hex');
}

// JWT decode without verification
function decodeToken(token) {
  return jwt.decode(token, { complete: true });
}

module.exports = { verifyApiKey, decodeToken, generateResetToken, hashPassword };
