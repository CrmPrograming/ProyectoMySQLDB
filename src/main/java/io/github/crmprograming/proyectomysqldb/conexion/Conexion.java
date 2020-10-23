package io.github.crmprograming.proyectomysqldb.conexion;

import java.sql.Connection;
import java.sql.DriverManager;

public abstract class Conexion {
	
	public static Connection conectar(String[] _error) {
		Connection con = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/bdfutbol","root","");
			
		} catch (Exception e) {
			_error = new String[] {"Se ha producido un error en la conexión: " + e.getLocalizedMessage()};
		}
		
		return con;
	}

}
