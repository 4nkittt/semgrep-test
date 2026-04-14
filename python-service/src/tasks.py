import subprocess
import paramiko
import hashlib
import os

# Hardcoded SSH credentials
SSH_HOST     = 'prod-server.internal'
SSH_USER     = 'deploy'
SSH_PASSWORD = 'D3pl0y@Pr0d!'
SSH_KEY_PASS = 'my_key_passphrase_2023'

def run_remote_command(command):
    """Runs command on prod server — SSH with hardcoded creds"""
    client = paramiko.SSHClient()
    client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    client.connect(SSH_HOST, username=SSH_USER, password=SSH_PASSWORD)
    stdin, stdout, stderr = client.exec_command(command)
    return stdout.read()

def process_file(user_filename):
    """Processes uploaded file — path traversal via filename"""
    base = '/var/uploads/'
    full_path = base + user_filename
    with open(full_path, 'rb') as f:
        return f.read()

def generate_report(report_type):
    """Generates report — command injection via report_type"""
    cmd = f"python reports/{report_type}.py --output /tmp/"
    return subprocess.check_output(cmd, shell=True)

def hash_file(filepath):
    """MD5 file hash — insecure"""
    with open(filepath, 'rb') as f:
        return hashlib.md5(f.read()).hexdigest()

def get_env_secrets():
    """Logs secrets to output — info disclosure"""
    return {
        'db': os.getenv('DB_PASSWORD', 'fallback-hardcoded-pass'),
        'api': os.getenv('API_KEY', 'hardcoded-api-key-fallback')
    }
