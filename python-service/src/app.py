from flask import Flask, request, jsonify, render_template_string
import sqlite3
import subprocess
import pickle
import yaml
import hashlib
import os
import requests

app = Flask(__name__)

# Hardcoded secrets
SECRET_KEY       = "flask-secret-key-hardcoded-2024"
DB_PASSWORD      = "postgres://admin:Adm1nP@ss!@prod-db/appdb"
SLACK_TOKEN      = "xoxb-798876-2946561-tFVb9NrNfmHvz1WPBMT2"
GITHUB_TOKEN     = "ghp_aBcDeFgHiJkLmNoPqRsTuVwXyZ123456"
INTERNAL_API_KEY = "int_key_9f8e7d6c5b4a3f2e1d0c"

@app.route('/users')
def get_users():
    user_id = request.args.get('id')
    conn = sqlite3.connect('app.db')
    # SQL injection — f-string in query
    rows = conn.execute(f"SELECT * FROM users WHERE id = {user_id}").fetchall()
    return jsonify(rows)

@app.route('/search')
def search():
    q = request.args.get('q')
    # SSTI — user input in template string
    template = f"<h2>Results for: {q}</h2>"
    return render_template_string(template)

@app.route('/run')
def run_cmd():
    cmd = request.args.get('cmd')
    # Command injection
    out = subprocess.check_output(cmd, shell=True)
    return out

@app.route('/load-config')
def load_config():
    path = request.args.get('path')
    with open(path) as f:
        # Unsafe yaml.load — code execution via !!python/object
        config = yaml.load(f)
    return jsonify(config)

@app.route('/restore')
def restore():
    data = request.get_data()
    # Unsafe pickle deserialization — RCE
    obj = pickle.loads(data)
    return str(obj)

@app.route('/proxy')
def proxy():
    url = request.args.get('url')
    # SSRF — no validation of target URL
    resp = requests.get(url, timeout=5)
    return resp.text

@app.route('/file')
def read_file():
    name = request.args.get('name')
    # Path traversal
    with open(os.path.join('/var/app/data', name)) as f:
        return f.read()

@app.route('/hash')
def make_hash():
    pw = request.args.get('pw')
    # Weak hash — MD5 for password
    return hashlib.md5(pw.encode()).hexdigest()

if __name__ == '__main__':
    # Debug mode in production — exposes interactive debugger
    app.run(debug=True, host='0.0.0.0')
