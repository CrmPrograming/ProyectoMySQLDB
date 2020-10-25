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

/**
 * Clase gestora de las conexiones a la base de datos, hace de intermediaria entre la interfaz y la bd.
 * Todas las operaciones de consulta, borrado, modificación y alta se realizan aquí.
 */
public abstract class Conexion {
	
	private final static String USUARIO = "root";
	
	private final static String PASSWD = "";
	
	private final static String BD_NOMBRE = "bdfutbol";
	
	private final static String HOSTNAME = "localhost:3306";
	
	/**
	 * Método encargado de establecer conexión con la base de datos
	 * y retornar un objeto Connection con la conexión ya realizada.
	 * 
	 * Delega el cerrado de la conexión a la función invocadora de esta.
	 *
	 * @param _error Array de String con los posibles errores que pudiera dar la operación
	 * @return Instancia de Connection con la conexión establecida
	 */
	private static Connection conectar(String[] _error) {
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
	
	/**
	 * Método encargado de rescatar el listado de todos los equipos existentes
	 * en la base de datos. Retornará un ArrayList de Registro el cual puede contener:
	 * - Ningún elemento, si hubo error o no hay equipos almacenados
	 * - Conjunto de mínimo un equipo
	 *
	 * @param _error Array de String con los posibles errores que pudiera dar la operación
	 * @return ArrayList de Registro con los equipos recuperados
	 */
	public static ArrayList<Registro> obtenerListadoEquipos(String[] _error) {
		ArrayList<Registro> listado = new ArrayList<Registro>();
		Connection con = conectar(_error);
		
		if (_error[0].equals("")) {			
			try {
				Statement stmt = con.createStatement();
				ResultSet rows = stmt.executeQuery("SELECT codEquipo, nomEquipo, equipos.codLiga, nomLiga, localidad, internacional FROM equipos"
												+ " INNER JOIN ligas ON equipos.codLiga = ligas.codLiga");
				while (rows.next()) {
					listado.add(new Equipo(
							rows.getInt("codEquipo"),
							rows.getString("nomEquipo"),
							rows.getString("codLiga"),
							rows.getString("nomLiga"),
							rows.getString("localidad"),
							rows.getBoolean("internacional"))
					);
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
	
	/**
	 * Método encargado de rescatar el listado de todas las ligas existentes
	 * en la base de datos. Retornará un ArrayList de Registro el cual puede contener:
	 * - Ningún elemento, si hubo error o no hay ligas almacenadas
	 * - Conjunto de mínimo una liga
	 *
	 * @param _error Array de String con los posibles errores que pudiera dar la operación
	 * @return ArrayList de Registro con las ligas recuperadas
	 */
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
	
	/**
	 * Método encargado de dar de alta un equipo.
	 *
	 * @param equipo Instancia de Equipo con los datos a insertar
	 * @param _error Array de String con los posibles errores que pudiera dar la operación
	 * @return booleano con el estado resultante de la operación
	 */
	public static boolean insertarEquipo(Equipo equipo, String[] _error) {
		boolean result;
		Connection con = conectar(_error);
		
		if (result = _error[0].equals("")) {
			try {
				// Comprobamos primero si la liga dada existe
				if (isLigaExiste(equipo.getCodLiga(), con)) {
					PreparedStatement stmt = con.prepareStatement("INSERT INTO equipos VALUES (NULL, ?, ?, ?, ?)");
					stmt.setString(1, equipo.getNomEquipo());
					stmt.setString(2, equipo.getCodLiga());
					stmt.setString(3, equipo.getLocalidad());
					stmt.setBoolean(4, equipo.isInternacional());
					result = (stmt.executeUpdate() > 0);
				} else {
					result = false;
					_error[0] = "Se ha producido un error al dar de alta el equipo: " + "No existe la liga con código " + equipo.getCodLiga();
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
	
	/**
	 * Método encargado de comprobar si la liga dada existe en la base de datos.
	 *
	 * @param codLiga Código de la liga a consultar
	 * @param con Conexión a la base de datos
	 * @return booleano con el estado resultante de la operación
	 * @throws SQLException
	 */
	public static boolean isLigaExiste(String codLiga, Connection con) throws SQLException {
		PreparedStatement stmt = con.prepareStatement("SELECT * FROM ligas WHERE codLiga = ?");
		stmt.setString(1,  codLiga);
		return (stmt.executeQuery().next());
	}
	
	/**
	 * Método encargado de comprobar si el equipo dado existe en la base de datos.
	 *
	 * @param codEquipo Código del equipo a consultar
	 * @param con Conexión a la base de datos
	 * @return booleano con el estado resultante de la operación
	 * @throws SQLException
	 */
	public static boolean isEquipoExiste(int codEquipo, String[] _error) {
		boolean result;
		Connection con = conectar(_error);
		
		if (result = _error[0].equals("")) {
			try {
				ResultSet row;
				PreparedStatement stmt = con.prepareStatement("SELECT * FROM equipos WHERE codEquipo = ?");
				
				stmt.setInt(1, codEquipo);
				row = stmt.executeQuery();
				
				result = row.next();
			} catch (SQLException e) {
				_error[0] = "Se ha producido un error al buscar el equipo: " + e.getLocalizedMessage();
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

	/**
	 * Método encargado de borrar el equipo dado de la base de datos.
	 *
	 * @param idEquipo Identificador del equipo a borrar
	 * @param _error Array de String con los posibles errores que pudiera dar la operación
	 * @return booleano con el estado resultante de la operación
	 */
	public static boolean borrarEquipo(int idEquipo, String[] _error) {
		boolean result;
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

	/**
	 * Método encargado de recuperar los datos de un equipo dado.
	 *
	 * @param idEquipo Identificador del equipo a buscar
	 * @param _error Array de String con los posibles errores que pudiera dar la operación
	 * @return Instancia de Equipo con los datos del equipo recuperado
	 */
	public static Equipo obtenerEquipo(int idEquipo, String[] _error) {
		Equipo result = null;
		Connection con = conectar(_error);
		
		if (_error[0].equals("")) {
			try {
				ResultSet row;
				PreparedStatement stmt = con.prepareStatement("SELECT codEquipo, nomEquipo, equipos.codLiga, nomLiga, localidad, internacional FROM equipos "
															+ "INNER JOIN ligas ON equipos.codLiga = ligas.codLiga WHERE codEquipo = ?");
				stmt.setInt(1, idEquipo);
				row = stmt.executeQuery();
				if (row.next())
					result = new Equipo(
							row.getInt("codEquipo"),
							row.getString("nomEquipo"),
							row.getString("codLiga"),
							row.getString("nomLiga"),
							row.getString("localidad"),
							row.getBoolean("internacional")
					);
				else
					_error[0] = "No existe el equipo " + idEquipo;
			} catch (SQLException e) {
				_error[0] = "Se ha producido un error al obtener el equipo: " + e.getLocalizedMessage();
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

	/**
	 * Método encargado de modificar los datos de un equipo en la base de datos.
	 * Se entiende que el equipo indicado por argumentos tiene el id del equipo
	 * a actualizar, junto con los nuevos datos que deberá tener.
	 *
	 * @param equipo Instancia del equipo con los nuevos datos
	 * @param _error Array de String con los posibles errores que pudiera dar la operación
	 * @return booleano con el estado resultante de la operación
	 */
	public static boolean modificarEquipo(Equipo equipo, String[] _error) {
		boolean result;
		Connection con = conectar(_error);
		
		if (result = _error[0].equals("")) {
			try {
				// Comprobamos primero si la liga dada existe
				if (isLigaExiste(equipo.getCodLiga(), con)) {
					PreparedStatement stmt = con.prepareStatement("UPDATE equipos SET nomEquipo = ?, codLiga = ?, localidad = ?, internacional = ?"
																+ " WHERE codEquipo = ?");
					stmt.setString(1, equipo.getNomEquipo());
					stmt.setString(2, equipo.getCodLiga());
					stmt.setString(3, equipo.getLocalidad());
					stmt.setBoolean(4, equipo.isInternacional());
					stmt.setInt(5, equipo.getCodEquipo());
					
					result = (stmt.executeUpdate() > 0);
					
				} else {
					result = false;
					_error[0] = "Se ha producido un error al modificar el equipo: " + "No existe la liga con código " + equipo.getCodLiga();
				}
				
			} catch (SQLException e) {
				_error[0] = "Se ha producido un error al modificar el equipo: " + e.getLocalizedMessage();
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
