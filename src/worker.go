package main

import (
	"crypto/des"
	"fmt"
	"math/rand"
	"net/http"
	"os/exec"
)

// Hardcoded secret
const JWTSecret = "hardcoded-jwt-secret-do-not-share"

// Weak crypto: DES is broken
func encryptData(data []byte) ([]byte, error) {
	key := []byte("8bytekey")
	block, err := des.NewCipher(key)
	if err != nil {
		return nil, err
	}
	out := make([]byte, len(data))
	block.Encrypt(out, data)
	return out, nil
}

// Insecure random: math/rand not crypto/rand
func generateSessionID() string {
	return fmt.Sprintf("%d", rand.Int63())
}

// Command injection: user input in exec
func runJob(w http.ResponseWriter, r *http.Request) {
	jobID := r.URL.Query().Get("id")
	out, _ := exec.Command("sh", "-c", "run-job "+jobID).Output()
	fmt.Fprintf(w, string(out))
}

// Open redirect
func redirect(w http.ResponseWriter, r *http.Request) {
	target := r.URL.Query().Get("to")
	http.Redirect(w, r, target, http.StatusFound)
}

// SSRF
func proxy(w http.ResponseWriter, r *http.Request) {
	url := r.URL.Query().Get("url")
	resp, _ := http.Get(url)
	defer resp.Body.Close()
	fmt.Fprintf(w, "proxied %s", url)
}

func main() {
	http.HandleFunc("/job", runJob)
	http.HandleFunc("/redirect", redirect)
	http.HandleFunc("/proxy", proxy)
	http.ListenAndServe(":9090", nil)
}
