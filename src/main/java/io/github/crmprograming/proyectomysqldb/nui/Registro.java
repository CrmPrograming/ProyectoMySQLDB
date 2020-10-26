package io.github.crmprograming.proyectomysqldb.nui;

/**
 * Clase plantilla representativa de los registros que puede
 * devolver una consulta.
 * 
 * Se utiliza para facilitar la manipulación de datos similares
 * y poder reutilizar código.
 */
public abstract class Registro {
	
	public abstract Object[] obtenerDatos();

}
