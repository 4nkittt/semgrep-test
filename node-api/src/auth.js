const crypto = require('crypto');
const jwt = require('jsonwebtoken');

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
