package io.github.crmprograming.proyectomysqldb.inclui;

import java.util.ArrayList;
import java.util.Scanner;

import io.github.crmprograming.proyectomysqldb.conexion.Conexion;
import io.github.crmprograming.proyectomysqldb.modelo.Equipo;

public abstract class Menu {
	
	private static void mostrarMenuInicial() {
		System.out.println();
		System.out.println("########################");
		System.out.printf("#%22s#%n", "");
		System.out.println("#    PROYECTO MySQL    #");
		System.out.printf("#%22s#%n", "");
		System.out.println("########################");
		System.out.println();
		System.out.println("- Autor: César Ravelo Martínez");
		
		System.out.println("1) Visualizar todos los datos de los equipos");
		System.out.println("2) Insertar equipo");
		System.out.println("3) Eliminar equipo");
		System.out.println("4) Modificar los datos de un equipo");
		System.out.println("5) Funciones adicionales");
		System.out.println("0) Salir");
		System.out.print("\n > Introduzca la opción a ejecutar: ");
	}
	
	private static void gestionarMenuInicial() {
		Scanner in = new Scanner(System.in);
		int opc = -1;
		do {
			mostrarMenuInicial();
			opc = in.nextInt();
			
			switch (opc) {
				case 0:
				break;
				case 1: // Mostrar listado
					String[] _error = {""};
					ArrayList<Equipo> listado = Conexion.obtenerListadoEquipos(_error);
					
					if (_error[0].equals("")) {
						mostrarTablaEquipos(listado);
					} else
						System.out.println(_error[0]);
				break;
	
				default:
					System.out.println("- Opción introducida no válida");
				break;
			}
			
		} while(opc != 0);
		in.close();
	}
	
	private static void mostrarTablaEquipos(ArrayList<Equipo> listado) {
		int i = 0;
		final String CABECERA = "codEquipo | nomEquipo | liga | localidad | internacional";
		
		for (i = 0; i < listado.size(); i++) {
			if (i % 5 == 0)
				System.out.println(CABECERA);
			System.out.print(listado.get(i).getCodEquipo() + " ");
			System.out.print(listado.get(i).getNomEquipo() + " ");
			System.out.print(listado.get(i).getLiga() + " ");
			System.out.print(listado.get(i).getLocalidad() + " ");
			System.out.println((listado.get(i).isInternacional())? "Sí" : "No");
		}
	}
	
	public static void iniciar() {
		gestionarMenuInicial();
	}

}
