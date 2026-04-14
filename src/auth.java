import java.sql.*;
import java.security.MessageDigest;
import java.util.Random;

public class AuthService {

    private static final String DB_URL = "jdbc:mysql://localhost/prod";
    private static final String DB_USER = "root";
    // Hardcoded password
    private static final String DB_PASS = "SuperSecret@2024";

    // SQL injection: string concatenation in query
    public static ResultSet getUserByName(String username) throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        Statement stmt = conn.createStatement();
        return stmt.executeQuery("SELECT * FROM users WHERE username = '" + username + "'");
    }

    // Weak crypto: MD5 for passwords
    public static String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    // Weak random: not cryptographically secure
    public static String generateToken() {
        Random rand = new Random();
        return String.valueOf(rand.nextLong());
    }

    // XXE: XML parsing without disabling external entities
    public static void parseUserXML(String xml) throws Exception {
        javax.xml.parsers.DocumentBuilderFactory factory =
            javax.xml.parsers.DocumentBuilderFactory.newInstance();
        javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
        builder.parse(new org.xml.sax.InputSource(new java.io.StringReader(xml)));
    }
}
