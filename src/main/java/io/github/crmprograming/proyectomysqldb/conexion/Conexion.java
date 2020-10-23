package io.github.crmprograming.proyectomysqldb.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import io.github.crmprograming.proyectomysqldb.modelo.Equipo;

public abstract class Conexion {
	
	private final static String USUARIO = "root";
	private final static String PASSWD = "";
	private final static String BD_NOMBRE = "bdfutbol";
	private final static String HOSTNAME = "localhost:3306";
	
	public static Connection conectar(String[] _error) {
		Connection con = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://"
												+ HOSTNAME + "/"
												+ BD_NOMBRE,
												  USUARIO,
												  PASSWD);
		} catch (Exception e) {
			_error[0] = "Se ha producido un error en la conexión: " + e.getLocalizedMessage();
		}
		
		return con;
	}
	
	public static ArrayList<Equipo> obtenerListadoEquipos(String[] _error) {
		ArrayList<Equipo> listado = new ArrayList<Equipo>();
		Connection con = conectar(_error);
		
		if (_error[0].equals("")) {			
			try {
				Statement stmt = con.createStatement();
				ResultSet rows = stmt.executeQuery("SELECT codEquipo, nomEquipo, nomLiga, localidad, internacional FROM equipos"
												+ " INNER JOIN ligas ON equipos.codLiga = ligas.codLiga");
				while (rows.next()) {
					listado.add(new Equipo(
							rows.getInt("codEquipo"),
							rows.getString("nomEquipo"),
							rows.getString("nomLiga"),
							rows.getString("localidad"),
							rows.getBoolean("internacional")));
				}
				
				con.close();
			} catch (SQLException e) {
				_error[0] = "Se ha producido un error al construir el listado de equipos: " + e.getLocalizedMessage();
			}
		}
		
		return listado;
	}

}
