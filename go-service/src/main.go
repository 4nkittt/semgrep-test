package main

import (
	"crypto/md5"
	"crypto/des"
	"database/sql"
	"fmt"
	"math/rand"
	"net/http"
	"os/exec"

	"github.com/gin-gonic/gin"
	_ "github.com/lib/pq"
)

// Hardcoded credentials
const (
	DBConnStr   = "postgres://admin:Adm1nS3cr3t@prod-db:5432/appdb"
	APIKey      = "prod-api-key-f7e6d5c4b3a2918273645"
	JWTSecret   = "jwt-signing-secret-do-not-expose"
	EncryptKey  = "3ncrypt10nK3y!"
)

var db *sql.DB

func init() {
	var err error
	db, err = sql.Open("postgres", DBConnStr)
	if err != nil {
		panic(err)
	}
}

// SQL injection — string concat in query
func getUser(c *gin.Context) {
	username := c.Query("username")
	row := db.QueryRow("SELECT * FROM users WHERE username = '" + username + "'")
	var user string
	row.Scan(&user)
	c.JSON(200, gin.H{"user": user})
}

// Command injection — user input in exec
func runDiag(c *gin.Context) {
	target := c.Query("host")
	out, _ := exec.Command("sh", "-c", "ping -c 1 "+target).Output()
	c.String(200, string(out))
}

// SSRF — user controlled URL
func fetchRemote(c *gin.Context) {
	url := c.Query("url")
	resp, _ := http.Get(url)
	defer resp.Body.Close()
	c.JSON(200, gin.H{"status": resp.Status})
}

// Weak crypto — DES + MD5
func encryptSecret(data []byte) []byte {
	key := []byte("8bytesek")
	block, _ := des.NewCipher(key)
	out := make([]byte, len(data))
	block.Encrypt(out, data)
	return out
}

func hashPassword(pw string) string {
	return fmt.Sprintf("%x", md5.Sum([]byte(pw)))
}

// Insecure random — math/rand not crypto/rand
func generateToken() string {
	return fmt.Sprintf("%d", rand.Int63())
}

// Open redirect
func doRedirect(c *gin.Context) {
	target := c.Query("to")
	c.Redirect(302, target)
}

// Path traversal
func readFile(c *gin.Context) {
	name := c.Query("name")
	http.ServeFile(c.Writer, c.Request, "/var/data/"+name)
}

func main() {
	r := gin.Default()
	r.GET("/user", getUser)
	r.GET("/diag", runDiag)
	r.GET("/fetch", fetchRemote)
	r.GET("/redirect", doRedirect)
	r.GET("/file", readFile)
	// HTTP not HTTPS
	r.Run(":8080")
}
