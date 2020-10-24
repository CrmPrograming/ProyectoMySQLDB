package io.github.crmprograming.proyectomysqldb.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;

import io.github.crmprograming.proyectomysqldb.modelo.Equipo;
import io.github.crmprograming.proyectomysqldb.modelo.Liga;
import io.github.crmprograming.proyectomysqldb.modelo.Registro;

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
	
	public static ArrayList<Registro> obtenerListadoEquipos(String[] _error) {
		ArrayList<Registro> listado = new ArrayList<Registro>();
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
				
			} catch (SQLException e) {
				_error[0] = "Se ha producido un error al construir el listado de equipos: " + e.getLocalizedMessage();
			} finally {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return listado;
	}
	
	public static ArrayList<Registro> obtenerListadoLigas(String[] _error) {
		ArrayList<Registro> listado = new ArrayList<Registro>();
		Connection con = conectar(_error);
		
		if (_error[0].equals("")) {
			try {
				Statement stmt = con.createStatement();
				ResultSet rows = stmt.executeQuery("SELECT * FROM ligas");
				while (rows.next()) {
					listado.add(new Liga(rows.getString("codLiga"), rows.getString("nomLiga")));
				}
				
			} catch (SQLException e) {
				_error[0] = "Se ha producido un error al construir el listado de equipos: " + e.getLocalizedMessage();
			} finally {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return listado;
	}
	
	public static boolean insertarEquipo(String nomEquipo, String codLiga, String localidad, boolean internacional, String[] _error) {
		boolean result = true;
		Connection con = conectar(_error);
		
		if (result = _error[0].equals("")) {
			try {
				// Comprobamos primero si la liga dada existe				
				ResultSet rows;
				PreparedStatement stmt = con.prepareStatement("SELECT * FROM ligas WHERE codLiga = ?");
				stmt.setString(1, codLiga);
				rows = stmt.executeQuery();
				
				if (rows.next()) {
					stmt = con.prepareStatement("INSERT INTO equipos VALUES (NULL, ?, ?, ?, ?)");
					stmt.setString(1, nomEquipo);
					stmt.setString(2, codLiga);
					stmt.setString(3, localidad);
					stmt.setBoolean(4, internacional);
					result = (stmt.executeUpdate() > 0);
				} else {
					_error[0] = "Se ha producido un error al dar de alta el equipo: " + "No existe la liga llamada " + codLiga;
				}
			} catch (SQLException e) {
				result = false;
				_error[0] = "Se ha producido un error al dar de alta el equipo: " + e.getLocalizedMessage();
			} finally {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}
	
	public static boolean comprobarEquipoExiste(int codEquipo, String[] _error) {
		boolean result = true;
		Connection con = conectar(_error);
		
		if (_error[0].equals("")) {
			try {
				ResultSet row;
				PreparedStatement stmt = con.prepareStatement("SELECT * FROM equipos WHERE codEquipo = ?");
				
				stmt.setInt(1, codEquipo);
				row = stmt.executeQuery();
				
				result = row.next();
			} catch (SQLException e) {
				_error[0] = "Se ha producido un error al borrar el equipo: " + e.getLocalizedMessage();
			} finally {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public static boolean borrarEquipo(int idEquipo, String[] _error) {
		boolean result = true;
		Connection con = conectar(_error);
		
		if (result = _error[0].equals("")) {
			try {
				PreparedStatement stmt = con.prepareStatement("DELETE FROM equipos WHERE codEquipo = ?");
				stmt.setInt(1, idEquipo);
				result = (stmt.executeUpdate() > 0);
			} catch (SQLIntegrityConstraintViolationException e) {
				result = false;
				_error[0] = "No se puede borrar el equipo " + idEquipo + ": El equipo está siendo referenciado en otra(s) tabla(s)";
			} catch (SQLException e) {
				result = false;
				_error[0] = "Se ha producido un error al borrar el equipo: " + e.getLocalizedMessage();
			} finally {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

}
