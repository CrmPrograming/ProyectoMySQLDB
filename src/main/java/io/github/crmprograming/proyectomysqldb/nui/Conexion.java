package io.github.crmprograming.proyectomysqldb.nui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Clase gestora de las conexiones a la base de datos, hace de intermediaria entre la interfaz y la bd.
 * Todas las operaciones de consulta, borrado, modificación y alta se realizan aquí.
 */
public abstract class Conexion {
	
	public enum TIPO_CONEXION {
		MYSQL(0), SQLSERVER(1), ACCESS(2);
		
		private final int id;
		
		private TIPO_CONEXION(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
	}
	
	public static TIPO_CONEXION conexionDefinida;
	
	public static String[] descripcionConexion = {"MySQL", "SQLServ.", "Access"};
	
	private static String ejecProcedimiento;
	
	private static HashMap<String, String> datosConexion;
	
	/**
	 * Método encargado de inicializar la clase. Buscará en el fichero de
	 * configuración los datos requeridos para establecer la conexión.
	 * 
	 * Si el método no consigue localizar el fichero o no está en un buen formato,
	 * la aplicación se detendrá y no dejará continuar.
	 * 
	 * La estructura válida del fichero debe ser:
	 * 
	 * driver=[dato1]
	 * url=[dato2]
	 * usuario=[dato3]
	 * passwd=[dato4]
	 * bd_nombre=[dato5]
	 * hostname=[dato6]
	 * 
	 * Si alguno de los parámetros no tiene valor, dejar en blanco sin eliminar
	 * el campo asociado.
	 * 
	 * @param fichero Ruta del fichero dentro de la carpeta de recursos.
	 * @param mysql 
	 * @throws IOException
	 */
	public static void init(String fichero, TIPO_CONEXION tConexion) throws IOException {
		InputStream inputStream = Conexion.class.getClassLoader().getResourceAsStream(fichero);
		InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
		BufferedReader reader = new BufferedReader(streamReader);
		ArrayList<String> cabeceras = new ArrayList<String>(Arrays.asList("driver", "url", "usuario", "passwd", "bd_nombre", "hostname"));
		String linea;
		String[] _parametro;
		boolean validado = true;
		
		datosConexion = new HashMap<String, String>();
		
		while (validado && (linea = reader.readLine()) != null) {
			_parametro = linea.split("=");
			if (validado = cabeceras.contains(_parametro[0]))
				datosConexion.put(_parametro[0], (_parametro.length == 2)?_parametro[1]:"");
		}
		
		reader.close();
		streamReader.close();
		
		if (!validado)
			throw new IOException("Fichero de configuración alterado o mal construido");
		
		conexionDefinida = tConexion;
		
		ejecProcedimiento = ((conexionDefinida == TIPO_CONEXION.MYSQL)?"Call":"Exec");
	}
	
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
			Class.forName(datosConexion.get("driver"));
			
			switch (conexionDefinida) {
				case MYSQL:
					con = DriverManager.getConnection(datosConexion.get("url")
							+ datosConexion.get("hostname") + "/"
							+ datosConexion.get("bd_nombre"),
							datosConexion.get("usuario"),
							datosConexion.get("passwd"));
					break;
				case SQLSERVER:
					con = DriverManager.getConnection(datosConexion.get("url")
							+ datosConexion.get("hostname") + ";DataBaseName="
							+ datosConexion.get("bd_nombre"),
							datosConexion.get("usuario"),
							datosConexion.get("passwd"));
					break;
				case ACCESS:
					con = DriverManager.getConnection(datosConexion.get("url")
							+ datosConexion.get("hostname") + "\\"
							+ datosConexion.get("bd_nombre"));
					break;
				default:
					break;
			}
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
	
	/**
	 * Método encargado de dar de alta un equipo utilizando el procedimiento
	 * asociado de la base de datos.
	 *
	 * @param equipo Instancia de Equipo con los datos a insertar
	 * @param _error Array de String con los posibles errores que pudiera dar la operación
	 * @return booleano con el estado resultante de la operación
	 */
	public static boolean insertarEquipoFunciones(Equipo equipo, String[] _error) {
		boolean result;
		Connection con = conectar(_error);
		
		if (result = _error[0].equals("")) {
			try {
				CallableStatement stmt = con.prepareCall(ejecProcedimiento + " " + ((Conexion.conexionDefinida == Conexion.TIPO_CONEXION.SQLSERVER)?"dbo.":"")  + "insertarEquipo(?, ?, ?, ?, ?, ?)");
				stmt.setString(1, equipo.getNomEquipo());
				stmt.setString(2, equipo.getCodLiga());
				stmt.setString(3, equipo.getLocalidad());
				stmt.setBoolean(4, equipo.isInternacional());
				stmt.registerOutParameter(5, Types.BIT);
				stmt.registerOutParameter(6, Types.BIT);
				
				stmt.execute();
				if (stmt.getBoolean(5)) {
					if (!(result = stmt.getBoolean(6)))
					_error[0] = "Se ha producido un error al dar de alta el equipo: El equipo " + equipo.getNomEquipo() + " ya existe.";
				} else {
					result = false;
					_error[0] = "Se ha producido un error al dar de alta el equipo: La liga " + equipo.getCodLiga() + " no existe.";
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
	 * Método encargado de obtener los contratos de un futbolista dado.
	 *
	 * @param dni DNI o NIE del jugador
	 * @param _error Array de String con los posibles errores que pudiera dar la operación
	 * @return ArrayList con los contratos conseguidos
	 */
	public static ArrayList<Registro> obtenerContratosFutbolista(String dni, String[] _error) {
		ArrayList<Registro> listado = new ArrayList<Registro>();
		Connection con = conectar(_error);
		
		if (_error[0].equals("")) {
			try {
				ResultSet rows;
				ResultSetMetaData rMeta;
				CallableStatement stmt = con.prepareCall(ejecProcedimiento + " " + ((Conexion.conexionDefinida == Conexion.TIPO_CONEXION.SQLSERVER)?"dbo.":"")  + "listarContratoFutbolista(?)");
				stmt.setString(1, dni);
				rows = stmt.executeQuery();
				rMeta = rows.getMetaData();
				// Comprobamos si el procedimiento devuelve valores o un mensaje
				if (rMeta.getColumnName(1).equals("id")) {
					while (rows.next()) {
						listado.add(new Contrato(
								rows.getInt("id"),
								rows.getString("equipo"),
								rows.getString("liga"),
								rows.getString("fechaInicio"),
								rows.getString("fechaFin"),
								rows.getLong("precioanual"),
								rows.getLong("preciorecision"))
						);
					}
				}
			} catch (SQLException e) {
				_error[0] = "Se ha producido un error al buscar el jugador dado: " + e.getLocalizedMessage();
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
	 * Método encargado de obtener la cantidad de contratos en activo que tiene
	 * el jugador indicado junto con los criterios especificados para su precio anual
	 * y precio de recisión.
	 *
	 * @param idEquipo Código del equipo al que pertenece el jugador
	 * @param activosPrecioAnual Precio anual máximo
	 * @param activosPrecioRecision Precio de recisión máximo
	 * @param _error Array de String con los posibles errores que pudiera dar la operación
	 * @return array con los valores obtenidos en sus posiciones 0 y 1. Puede valer null en caso de error
	 */
	public static int[] obtenerContratosEnActivo(int idEquipo, int activosPrecioAnual, int activosPrecioRecision, String[] _error) {
		int[] result = null;
		Connection con = conectar(_error);
		
		if (_error[0].equals("")) {
			try {
				CallableStatement stmt = con.prepareCall(ejecProcedimiento + " " + ((Conexion.conexionDefinida == Conexion.TIPO_CONEXION.SQLSERVER)?"dbo.":"")  + "futbolistasActivos(?, ?, ?, ?, ?)");
				stmt.setInt(1, idEquipo);
				stmt.setInt(2, activosPrecioAnual);
				stmt.setInt(3, activosPrecioRecision);
				stmt.registerOutParameter(4, Types.INTEGER);
				stmt.registerOutParameter(5, Types.INTEGER);
				stmt.execute();
				
				if (stmt.getInt(4) != -1 && stmt.getInt(5) != -1) {
					result = new int[] {stmt.getInt(4), stmt.getInt(5)};
				}
			} catch (SQLException e) {
				_error[0] = "Se ha producido un error al buscar los contratos: " + e.getLocalizedMessage();
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
	 * Método encargado de obtener los meses activos totales de un futbolista.
	 *
	 * @param dni DNI o NIE del futbolista
	 * @param _error Array de String con los posibles errores que pudiera dar la operación
	 * @return Int con el total de meses activos
	 */
	public static int obtenerMesesActivosFutbolista(String dni, String[] _error) {
		int result = 0;
		Connection con = conectar(_error);
		
		if (_error[0].equals("")) {
			try {
				ResultSet row;
				PreparedStatement stmt = con.prepareCall("SELECT " + ((Conexion.conexionDefinida == Conexion.TIPO_CONEXION.SQLSERVER)?"dbo.":"")  + "fnTotalMeses(?)");
				stmt.setString(1, dni);				
				row = stmt.executeQuery();
				
				if (row.next()) {
					result = row.getInt(1);
				}
			} catch (SQLException e) {
				_error[0] = "Se ha producido un error al buscar los contratos: " + e.getLocalizedMessage();
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
