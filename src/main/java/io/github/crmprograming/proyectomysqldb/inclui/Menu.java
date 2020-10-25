package io.github.crmprograming.proyectomysqldb.inclui;

import java.util.ArrayList;
import java.util.Scanner;

import io.github.crmprograming.proyectomysqldb.conexion.Conexion;
import io.github.crmprograming.proyectomysqldb.modelo.Equipo;
import io.github.crmprograming.proyectomysqldb.modelo.Liga;
import io.github.crmprograming.proyectomysqldb.modelo.Registro;

public abstract class Menu {
	
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
	
	private static void gestionarMenuInicial() {
		ArrayList<Registro> listado;
		Scanner in = new Scanner(System.in);
		int opc = -1;
		String[] _error;
		String nomEquipo, codLiga, localidad;
		boolean internacional;
		int idEquipo;
		
		do {
			_error = new String[] {""};
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
						mostrarInsertarEquipo();
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
							if (Conexion.comprobarEquipoExiste(idEquipo, _error))								
								if (Conexion.borrarEquipo(idEquipo, _error))
									System.out.printf("Se ha borrado el equipo %d correctamente%n", idEquipo);
							else if (_error[0].equals(""))
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
							if (Conexion.comprobarEquipoExiste(idEquipo, _error)) {
								
							}
								
						}
						
					} while (idEquipo != -1);
				break;
	
				default:
					System.out.println("- Opción introducida no válida");
			}
			
			if (!_error[0].equals(""))
				System.err.println(_error[0]);
			
		} while(opc != 0);
		in.close();
	}
	
	private static void mostrarTablaEquipos(ArrayList<Registro> listado) {
		int i;
		
		for (i = 0; i < listado.size(); i++) {
			Equipo actual = (Equipo) listado.get(i);
			if (i % 5 == 0)
				System.out.printf("%n%n%11s | %-40s | %-50s | %-60s | %-15s%n", "codEquipo", "nomEquipo", "nomLiga", "localidad", "internacional");
			System.out.printf("%n%11s | %-40s | %-50s | %-60s | %-15s ",
					actual.getCodEquipo(),
					actual.getNomEquipo(),
					actual.getLiga(),
					actual.getLocalidad(),
					(actual.isInternacional())? "Sí" : "No");
		}
		System.out.println();
	}
	
	private static void mostrarInsertarEquipo() {
		System.out.println();
		System.out.println("1) Dar datos del equipo");
		System.out.println("2) Mostrar ligas con sus identificadores");
		System.out.println("0) Cancelar operación");
		System.out.print("\n> Introduzca la opción a ejecutar: ");
	}
	
	private static void mostrarTablaLigas(ArrayList<Registro> listado) {
		int i;
		
		for (i = 0; i < listado.size(); i++) {
			Liga actual = (Liga) listado.get(i);
			if (i % 5 == 0)
				System.out.printf("%n%n%10s | %-50s", "codLiga", "nomLiga");
			System.out.printf("%n%n%10s | %-50s", actual.getCodigo(), actual.getNombre());
		}
		System.out.println();
	}
	
	public static void iniciar() {
		gestionarMenuInicial();
	}

}
