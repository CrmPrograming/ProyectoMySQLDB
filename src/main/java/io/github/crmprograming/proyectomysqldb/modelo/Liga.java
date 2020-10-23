package io.github.crmprograming.proyectomysqldb.modelo;

public class Liga extends Registro {
	
	private String codigo;
	private String nombre;
	
	public Liga(String codigo, String nombre) {
		super();
		this.codigo = codigo;
		this.nombre = nombre;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Override
	public String toString() {
		return "Liga [codigo=" + codigo + ", nombre=" + nombre + "]";
	}

}
