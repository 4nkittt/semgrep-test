import org.springframework.web.bind.annotation.*;
import javax.servlet.http.*;
import java.sql.*;
import java.security.*;
import java.util.Random;
import java.io.*;
import java.net.URL;

@RestController
public class UserService {

    // Hardcoded DB credentials
    private static final String DB_URL    = "jdbc:mysql://prod-db:3306/appdb";
    private static final String DB_USER   = "root";
    private static final String DB_PASS   = "Pr0dMySQL@2024!";
    private static final String API_TOKEN = "Bearer prod_token_7f8e9d0c1b2a3456";
    private static final String S3_SECRET = "s3-secret-key-prod-do-not-commit";

    // SQL injection — string concat
    @GetMapping("/user")
    public String getUser(@RequestParam String id) throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE id = " + id);
        return rs.next() ? rs.getString("name") : "not found";
    }

    // XSS — reflected input in response
    @GetMapping("/greet")
    public String greet(@RequestParam String name, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        resp.getWriter().println("<h1>Hello " + name + "</h1>");
        return null;
    }

    // XXE — XML parsing without disabling external entities
    @PostMapping("/import")
    public String importData(@RequestBody String xml) throws Exception {
        javax.xml.parsers.DocumentBuilderFactory factory =
            javax.xml.parsers.DocumentBuilderFactory.newInstance();
        javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
        builder.parse(new org.xml.sax.InputSource(new StringReader(xml)));
        return "imported";
    }

    // SSRF — user supplied URL fetched server-side
    @GetMapping("/fetch")
    public String fetchUrl(@RequestParam String url) throws IOException {
        return new URL(url).openConnection().getInputStream().toString();
    }

    // Path traversal
    @GetMapping("/file")
    public String readFile(@RequestParam String name) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("/var/data/" + name));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        return sb.toString();
    }

    // Weak random — not cryptographically secure
    @GetMapping("/token")
    public String generateToken() {
        return String.valueOf(new Random().nextLong());
    }

    // MD5 password hash
    @PostMapping("/hash")
    public String hashPassword(@RequestParam String pw) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(pw.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    // Command injection
    @GetMapping("/report")
    public String runReport(@RequestParam String type) throws IOException {
        Process p = Runtime.getRuntime().exec("generate-report " + type);
        return new String(p.getInputStream().readAllBytes());
    }
}
