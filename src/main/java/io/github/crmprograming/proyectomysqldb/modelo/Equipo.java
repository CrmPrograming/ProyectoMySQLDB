package io.github.crmprograming.proyectomysqldb.modelo;

/**
 * Clase gestora de un equipo junto con sus características.
 */
public class Equipo extends Registro {	
	
	private int codEquipo;	
	private String nomEquipo;
	private String codLiga;
	private String liga;
	private String localidad;
	private boolean internacional;
	
	/**
	 * Constructor de la clase.
	 *
	 * @param codEquipo Código del equipo
	 * @param nomEquipo Nombre del equipo
	 * @param codLiga Código de la liga a la que pertenece el equipo
	 * @param liga Nombre de la liga a la que pertenece el equipo
	 * @param localidad Localidad del equipo
	 * @param internacional ¿El equipo es internacional?
	 */
	public Equipo(int codEquipo, String nomEquipo, String codLiga, String liga, String localidad, boolean internacional) {
		this.codEquipo = codEquipo;
		this.nomEquipo = nomEquipo;
		this.codLiga = codLiga;
		this.liga = liga;
		this.localidad = localidad;
		this.internacional = internacional;
	}
	
	@Override
	public Object[] obtenerDatos() {
		return new Object[] {codEquipo, nomEquipo, liga, localidad, (internacional)?"Sí":"No"};
	}
	
	// ######################
	//
	// 	 GETTERS Y SETTERS
	//
	// ######################
		
	/**
	 * Getter del código del equipo.
	 *
	 * @return codEquipo
	 */
	public int getCodEquipo() {
		return codEquipo;
	}

	/**
	 * Setter del código del equipo.
	 *
	 * @param codEquipo
	 */
	public void setCodEquipo(int codEquipo) {
		this.codEquipo = codEquipo;
	}

	/**
	 * Getter del nombre del equipo.
	 *
	 * @return codEquipo
	 */
	public String getNomEquipo() {
		return nomEquipo;
	}

	/**
	 * Setter del nombre del equipo.
	 *
	 * @param nomEquipo
	 */
	public void setNomEquipo(String nomEquipo) {
		this.nomEquipo = nomEquipo;
	}
	
	/**
	 * Getter del código de la liga.
	 *
	 * @return codLiga
	 */
	public String getCodLiga() {
		return codLiga;
	}

	/**
	 * Setter del código de la liga.
	 *
	 * @param codLiga
	 */
	public void setCodLiga(String codLiga) {
		this.codLiga = codLiga;
	}

	/**
	 * Getter del nombre de la liga.
	 *
	 * @return liga
	 */
	public String getLiga() {
		return liga;
	}

	/**
	 * Setter del nombre de la liga.
	 *
	 * @param liga
	 */
	public void setLiga(String liga) {
		this.liga = liga;
	}

	/**
	 * Getter de la localidad del equipo.
	 *
	 * @return localidad
	 */
	public String getLocalidad() {
		return localidad;
	}

	/**
	 * Setter de la localidad del equipo.
	 *
	 * @param localidad
	 */
	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	
	/**
	 * Comprueba si es internacional.
	 *
	 * @return true, si es internacional
	 */
	public boolean isInternacional() {
		return internacional;
	}

	/**
	 * Setter de internacional.
	 *
	 * @param internacional
	 */
	public void setInternacional(boolean internacional) {
		this.internacional = internacional;
	}

}
