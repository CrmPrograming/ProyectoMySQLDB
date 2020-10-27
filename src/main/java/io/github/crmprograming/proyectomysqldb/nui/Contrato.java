package io.github.crmprograming.proyectomysqldb.nui;

public class Contrato extends Registro {
	
	private int id;
	private String equipo;
	private String liga;
	private String fechaInicio;
	private String fechaFin;
	private long precioAnual;
	private long precioRecision;
	
	public Contrato(int id, String equipo, String liga, String fechaInicio, String fechaFin, long precioAnual, long precioRecision) {
		this.id = id;
		this.equipo = equipo;
		this.liga = liga;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
		this.precioAnual = precioAnual;
		this.precioRecision = precioRecision;
	}
	
	@Override
	public Object[] obtenerDatos() {
		return new Object[] {Integer.toString(id), equipo, liga, fechaInicio, fechaFin, Long.toString(precioAnual), Long.toString(precioRecision)};
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEquipo() {
		return equipo;
	}

	public void setEquipo(String equipo) {
		this.equipo = equipo;
	}

	public String getLiga() {
		return liga;
	}

	public void setLiga(String liga) {
		this.liga = liga;
	}

	public String getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(String fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public String getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(String fechaFin) {
		this.fechaFin = fechaFin;
	}

	public long getPrecioAnual() {
		return precioAnual;
	}

	public void setPrecioAnual(long precioAnual) {
		this.precioAnual = precioAnual;
	}

	public long getPrecioRecision() {
		return precioRecision;
	}

	public void setPrecioRecision(long precioRecision) {
		this.precioRecision = precioRecision;
	}

	@Override
	public String toString() {
		return "Contrato [id=" + id + ", equipo=" + equipo + ", liga=" + liga + ", fechaInicio=" + fechaInicio
				+ ", fechaFin=" + fechaFin + ", precioAnual=" + precioAnual + ", precioRecision=" + precioRecision
				+ "]";
	}
	
	

}
