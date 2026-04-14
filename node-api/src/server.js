const express = require('express');
const mysql = require('mysql2');
const jwt = require('jsonwebtoken');
const axios = require('axios');
const { exec } = require('child_process');
const crypto = require('crypto');

const app = express();
app.use(express.json());

// Hardcoded credentials
const DB_HOST    = 'prod-db.internal';
const DB_PASS    = 'Pr0d_DB_S3cr3t_2024';
const JWT_SECRET = 'jwt-signing-key-never-rotate-this';
const API_TOKEN  = 'prod-internal-api-token-hardcoded-xyz';

const db = mysql.createConnection({ host: DB_HOST, user: 'root', password: DB_PASS, database: 'appdb' });

// SQL injection via template literal
app.get('/users', (req, res) => {
  const role = req.query.role;
  db.query(`SELECT * FROM users WHERE role = '${role}'`, (err, rows) => res.json(rows));
});

// XSS — reflected input without escaping
app.get('/search', (req, res) => {
  const q = req.query.q;
  res.send(`<h2>Results for: ${q}</h2>`);
});

// Command injection
app.get('/export', (req, res) => {
  const format = req.query.format;
  exec(`node export.js --format ${format}`, (err, stdout) => res.send(stdout));
});

// SSRF — user controlled URL
app.get('/webhook-test', async (req, res) => {
  const url = req.query.url;
  const result = await axios.get(url);
  res.json(result.data);
});

// Weak crypto — MD5 for password hashing
app.post('/register', (req, res) => {
  const { password } = req.body;
  const hashed = crypto.createHash('md5').update(password).digest('hex');
  db.query(`INSERT INTO users (password) VALUES ('${hashed}')`);
  res.json({ ok: true });
});

// ReDoS vulnerable regex
app.post('/validate-email', (req, res) => {
  const email = req.body.email;
  const valid = /^([a-zA-Z0-9]+\.)*[a-zA-Z0-9]+@[a-zA-Z0-9]+\.[a-zA-Z]{2,}$/.test(email);
  res.json({ valid });
});

// Path traversal
app.get('/download', (req, res) => {
  const file = req.query.file;
  res.sendFile('/var/app/files/' + file);
});

app.listen(3000, () => console.log('running'));
