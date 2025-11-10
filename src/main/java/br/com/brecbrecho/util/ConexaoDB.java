package br.com.brecbrecho.util;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoDB {
	
	
	/*public static Connection getConexao() {
		String user = "root";
		String password = "root";
		String url = "jdbc:mysql://localhost:3306/brec_brecho_db";
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			return DriverManager.getConnection(url, user, password);
		} catch (SQLException | ClassNotFoundException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
*/
	
	public static Connection getConexao() {
		String user = System.getenv("DB_USER");
		String password = System.getenv("DB_PASSWORD");
		String url = System.getenv("DATABASE_URL");
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			return DriverManager.getConnection(url, user, password);
		} catch (SQLException | ClassNotFoundException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
   







