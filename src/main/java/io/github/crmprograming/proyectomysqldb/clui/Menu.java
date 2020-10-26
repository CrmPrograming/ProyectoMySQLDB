package io.github.crmprograming.proyectomysqldb.clui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import io.github.crmprograming.proyectomysqldb.nui.Conexion;
import io.github.crmprograming.proyectomysqldb.nui.Equipo;
import io.github.crmprograming.proyectomysqldb.nui.Registro;

/**
 * Clase gestora de la interfaz de la aplicación.
 */
public abstract class Menu {

	/**
	 * Método encargado de mostrar el menú inicial de la aplicación.
	 */
	private static void mostrarMenuInicial() {
		System.out.println();
		System.out.println("########################");
		System.out.printf("#%22s#%n", "");
		System.out.println("#    PROYECTO MySQL    #");
		System.out.printf("#%22s#%n", "");
		System.out.println("########################");
		System.out.println();
		System.out.println("- Autor: César Ravelo Martínez\n");

		System.out.println("1) Visualizar todos los datos de los equipos");
		System.out.println("2) Insertar equipo");
		System.out.println("3) Eliminar equipo");
		System.out.println("4) Modificar los datos de un equipo");
		System.out.println("5) Funciones adicionales");
		System.out.println("0) Salir");
		System.out.print("\n> Introduzca la opción a ejecutar: ");
	}

	/**
	 * Método encargado de gestionar el menú principal de la aplicación.
	 */
	private static void gestionarMenuInicial() {
		ArrayList<Registro> listado;
		Scanner in = new Scanner(System.in);
		int opc = -1;
		String[] _error;
		String nomEquipo, codLiga, localidad;
		boolean internacional;
		int idEquipo;
		Equipo actual;

		do {
			_error = new String[] { "" };
			mostrarMenuInicial();
			opc = in.nextInt();

			switch (opc) {
			case 0:
				break;
			case 1: // Mostrar listado
				listado = Conexion.obtenerListadoEquipos(_error);

				if (_error[0].equals(""))
					mostrarTablaEquipos(listado);
				break;

			case 2: // Insertar equipo
				opc = -1;
				do {
					mostrarMenuInsertarEquipo();
					opc = in.nextInt();

					switch (opc) {
					case 0:
						break;
					case 1: // Pedir datos del equipo
						in.nextLine();

						System.out.print("> Nombre del equipo (LIMIT 40): ");
						nomEquipo = in.nextLine();
						System.out.print("> Código de la liga (LIMIT 5): ");
						codLiga = in.nextLine();
						System.out.print("> Localidad del equipo (LIMIT 60): ");
						localidad = in.nextLine();
						System.out.print("> ¿El equipo es internacional? (s|n): ");
						internacional = in.nextLine().toUpperCase().equals("S");
						System.out.println();

						if (Conexion.insertarEquipo(new Equipo(-1, nomEquipo, codLiga, null, localidad, internacional), _error))
							System.out.println("Se ha dado de alta el equipo " + nomEquipo + " sin problemas");
						break;
					case 2: // Mostrar ligas y sus códigos
						listado = Conexion.obtenerListadoLigas(_error);

						if (_error[0].equals(""))
							mostrarTablaLigas(listado);
						break;

					default:
						System.out.println("- Opción introducida no válida");
					}

				} while (opc != 0 && _error[0].equals(""));
				opc = 2;
				break;

			case 3: // Eliminar Equipo
				do {
					System.out.print("> Introduzca el ID del equipo a eliminar o -1 para cancelar: ");
					idEquipo = in.nextInt();

					if (idEquipo != -1) {
						if (Conexion.isEquipoExiste(idEquipo, _error)) {
							if (Conexion.borrarEquipo(idEquipo, _error))
								System.out.printf("Se ha borrado el equipo %d correctamente%n", idEquipo);
						} else if (_error[0].equals(""))
							System.out.println("No existe ningún equipo con el id " + idEquipo);
					}

					if (!_error[0].equals("")) {
						System.err.println(_error[0]);
						_error[0] = "";
					}

				} while (idEquipo != -1);

				break;

			case 4: // Modificar Equipo
				do {
					System.out.print("> Introduzca el ID del equipo a modificar o -1 para cancelar: ");
					idEquipo = in.nextInt();

					if (idEquipo != -1) {
						if (Conexion.isEquipoExiste(idEquipo, _error)) {
							actual = Conexion.obtenerEquipo(idEquipo, _error);

							if (actual != null) {
								in.nextLine();

								System.out.println("\nIndique a continuación los nuevos datos del equipo.");
								System.out.println("Los actuales vendrán indicados entre paréntesis\n");

								System.out.printf("> Nombre del equipo (LIMIT 40) (%s): ", actual.getNomEquipo());
								nomEquipo = in.nextLine();
								System.out.printf("> Código de la liga (LIMIT 5) (%s): ", actual.getCodLiga());
								codLiga = in.nextLine();
								System.out.printf("> Localidad del equipo (LIMIT 60) (%s): ", actual.getLocalidad());
								localidad = in.nextLine();
								System.out.printf("> ¿El equipo es internacional? (s|n) (%s): ", (actual.isInternacional()) ? "S" : "N");
								internacional = in.nextLine().toUpperCase().equals("S");
								System.out.println();

								if (Conexion.modificarEquipo(new Equipo(actual.getCodEquipo(), nomEquipo, codLiga, null, localidad, internacional), _error))
									System.out.println("Se han actualizado los datos correctamente.");
								else
									System.out.println("No se pudieron actualizar los datos.");
							}
						} else if (_error[0].equals(""))
							System.out.println("No existe ningún equipo con el id " + idEquipo);
						
						if (!_error[0].equals("")) {
							System.err.println(_error[0]);
							_error[0] = "";
						}
					}

				} while (idEquipo != -1);
				break;

			default:
				System.out.println("- Opción introducida no válida");
			}

			if (!_error[0].equals(""))
				System.err.println(_error[0]);

		} while (opc != 0);
		in.close();
	}

	/**
	 * Método encargado de mostrar el menú para insertar un equipo
	 */
	private static void mostrarMenuInsertarEquipo() {
		System.out.println();
		System.out.println("1) Dar datos del equipo");
		System.out.println("2) Mostrar ligas con sus identificadores");
		System.out.println("0) Cancelar operación");
		System.out.print("\n> Introduzca la opción a ejecutar: ");
	}

	/**
	 * Método encargado de mostrar el listado de equipos.
	 * Delega la tarea de dar formato a la tabla a la función mostrarTablaDatos.
	 * 
	 * @param listado ArrayList con los datos a mostrar
	 * @see mostrarTablaDatos
	 */
	private static void mostrarTablaEquipos(ArrayList<Registro> listado) {
		mostrarTablaDatos(listado, new String[] {"codEquipo", "nomEquipo", "nomLiga", "localidad", "internacional"}, "%n%11s | %-40s | %-50s | %-60s | %-15s");
	}
	
	/**
	 * Método encargado de mostrar el listado de ligas.
	 * Delega la tarea de dar formato a la tabla a la función mostrarTablaDatos.
	 *
	 * @param listado ArrayList con los datos a mostrar
	 * @see mostrarTablaDatos
	 */
	private static void mostrarTablaLigas(ArrayList<Registro> listado) {
		mostrarTablaDatos(listado, new String[] {"codLiga", "nomLiga"}, "%n%10s | %-50s");
	}
	
	/**
	 * Método encargado de construir una tabla.
	 * Gracias al formato indicado por parámetro, construirá la tabla
	 * independiente de los datos que le vengan.
	 * La cabecera reaparece cada 5 filas mostradas.
	 * 
	 * @param listado ArrayList con los datos a mostrar
	 * @param _cabecera Array con las cabeceras en String de cada columna
	 * @param formato String con el formato preparado asociado a los datos a mostrar
	 */
	private static void mostrarTablaDatos(ArrayList<Registro> listado, Object[] _cabecera, String formato) {
		int i;
		
		for (i = 0; i < listado.size(); i++) {			
			if (i % 5 == 0)
				System.out.printf("%n" + formato, _cabecera);
			System.out.printf(formato, listado.get(i).obtenerDatos());			
		}
		System.out.println();
	}

	/**
	 * Método encargado de iniciar la ejecución del menú
	 */
	public static void iniciar() {
		try {
			Conexion.init("config.properties");
			gestionarMenuInicial();
		} catch (IOException e) {
			System.err.println("La aplicación no se pudo iniciar. " + e.getLocalizedMessage());
		}
	}

}
