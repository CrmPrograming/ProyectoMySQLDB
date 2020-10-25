package io.github.crmprograming.proyectomysqldb.modelo;

/**
 * Clase gestora de una liga junto con sus características.
 */
public class Liga extends Registro {
		
	private String codigo;
	private String nombre;
	
	/**
	 * Constructor de la clase.
	 *
	 * @param codigo Código de la liga
	 * @param nombre Nombre de la liga
	 */
	public Liga(String codigo, String nombre) {
		super();
		this.codigo = codigo;
		this.nombre = nombre;
	}
	
	// ######################
	//
	// 	 GETTERS Y SETTERS
	//
	// ######################

	/**
	 * Getter del código de la liga.
	 *
	 * @return codigo
	 */
	public String getCodigo() {
		return codigo;
	}

	/**
	 * Setter del código de la liga
	 *
	 * @param codigo
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	/**
	 * Getter del nombre de la liga.
	 *
	 * @return nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * Setter del nombre de la liga.
	 *
	 * @param nombre
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

}
