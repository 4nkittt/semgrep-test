import subprocess
import pickle
import yaml
import sqlite3

# Hardcoded credentials
AWS_ACCESS_KEY = "AKIAIOSFODNN7EXAMPLE"
AWS_SECRET = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"

def load_config(config_file):
    # Unsafe yaml.load - arbitrary code execution via !!python/object
    with open(config_file) as f:
        return yaml.load(f)

def deserialize_model(data):
    # Unsafe pickle - RCE via crafted payload
    return pickle.loads(data)

def run_pipeline(user_input):
    # Command injection
    result = subprocess.run(f"python process.py {user_input}", shell=True, capture_output=True)
    return result.stdout

def query_results(db_path, run_id):
    conn = sqlite3.connect(db_path)
    # SQL injection
    return conn.execute(f"SELECT * FROM runs WHERE id = {run_id}").fetchall()

def write_output(filename, data):
    # Path traversal - filename not sanitized
    with open("/data/outputs/" + filename, "w") as f:
        f.write(data)

def send_alert(webhook_url, message):
    # SSRF - user-controlled URL
    import urllib.request
    return urllib.request.urlopen(webhook_url).read()
