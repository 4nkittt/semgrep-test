const express = require('express');
const { exec } = require('child_process');
const crypto = require('crypto');
const fs = require('fs');

const app = express();
app.use(express.json());

// Hardcoded secret
const JWT_SECRET = 'super-secret-jwt-key-hardcoded';
const DB_PASS    = 'postgres-prod-pass-2024';

// SQL injection via string template
app.get('/user', (req, res) => {
  const id = req.query.id;
  const query = `SELECT * FROM users WHERE id = ${id}`;
  db.query(query, (err, rows) => res.json(rows));
});

// XSS: reflected input
app.get('/greet', (req, res) => {
  const name = req.query.name;
  res.send(`<h1>Hello ${name}</h1>`);
});

// Command injection
app.get('/ping', (req, res) => {
  const host = req.query.host;
  exec(`ping -c 1 ${host}`, (err, stdout) => res.send(stdout));
});

// Path traversal
app.get('/file', (req, res) => {
  const name = req.query.name;
  const content = fs.readFileSync('/var/data/' + name, 'utf8');
  res.send(content);
});

// Weak crypto: MD5
app.post('/hash', (req, res) => {
  const hash = crypto.createHash('md5').update(req.body.data).digest('hex');
  res.json({ hash });
});

// ReDoS vulnerable regex
app.post('/validate', (req, res) => {
  const input = req.body.input;
  const vulnerable = /^(a+)+$/.test(input);
  res.json({ valid: vulnerable });
});

// SSRF
app.get('/fetch', async (req, res) => {
  const url = req.query.url;
  const response = await fetch(url);
  res.send(await response.text());
});

app.listen(3000);
