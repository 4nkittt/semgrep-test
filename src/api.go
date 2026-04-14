package main

import (
	"crypto/md5"
	"database/sql"
	"fmt"
	"net/http"
	"os/exec"
)

var db *sql.DB

// SQL injection: user input directly in query
func getUser(w http.ResponseWriter, r *http.Request) {
	username := r.URL.Query().Get("username")
	query := "SELECT * FROM users WHERE username = '" + username + "'"
	rows, err := db.Query(query)
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	defer rows.Close()
	fmt.Fprintf(w, "rows: %v", rows)
}

// Command injection: unsanitized input passed to shell
func runDiag(w http.ResponseWriter, r *http.Request) {
	host := r.URL.Query().Get("host")
	out, err := exec.Command("sh", "-c", "ping -c 1 "+host).Output()
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	fmt.Fprintf(w, string(out))
}

// Weak crypto: MD5 for password hashing
func hashPassword(password string) string {
	h := md5.New()
	h.Write([]byte(password))
	return fmt.Sprintf("%x", h.Sum(nil))
}

// Hardcoded credentials
const (
	DBPassword = "admin1234"
	APISecret  = "sk-prod-9x8y7z6w5v4u3t2s1r0q"
)

// SSRF: user-controlled URL fetched server-side
func fetchURL(w http.ResponseWriter, r *http.Request) {
	target := r.URL.Query().Get("url")
	resp, err := http.Get(target)
	if err != nil {
		http.Error(w, err.Error(), 500)
		return
	}
	defer resp.Body.Close()
	fmt.Fprintf(w, "status: %s", resp.Status)
}

func main() {
	http.HandleFunc("/user", getUser)
	http.HandleFunc("/diag", runDiag)
	http.HandleFunc("/fetch", fetchURL)
	http.ListenAndServe(":8080", nil)
}
