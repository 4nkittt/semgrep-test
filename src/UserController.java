import javax.servlet.http.*;
import java.sql.*;
import java.security.*;
import java.util.Random;
import java.io.*;

public class UserController extends HttpServlet {

    // Hardcoded DB credentials
    private static final String DB_URL  = "jdbc:mysql://prod-db:3306/users";
    private static final String DB_USER = "admin";
    private static final String DB_PASS = "Pr0d@dm1n2024!";

    // SQL injection via string concat
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, SQLException {
        String userId = req.getParameter("id");
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        ResultSet rs = conn.createStatement()
            .executeQuery("SELECT * FROM users WHERE id = " + userId);
        PrintWriter out = resp.getWriter();
        while (rs.next()) out.println(rs.getString("username"));
    }

    // XSS: user input reflected without escaping
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String name = req.getParameter("name");
        resp.getWriter().println("<h1>Hello " + name + "</h1>");
    }

    // Weak random token
    public static String generateToken() {
        return String.valueOf(new Random().nextLong());
    }

    // Path traversal
    public static String readFile(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("/var/data/" + filename));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();
        return sb.toString();
    }

    // MD5 password hash
    public static String hashPassword(String pw) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(pw.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
