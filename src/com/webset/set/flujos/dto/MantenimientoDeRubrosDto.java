package com.webset.set.flujos.dto;

public class MantenimientoDeRubrosDto {
	private int idGrupo;
	private String desripcion;
	private int idRubro;
	private String descRubro;
	private String ingresoEgreso;
	
	public int getIdGrupo() {
		return idGrupo;
	}
	public void setIdGrupo(int idGrupo) {
		this.idGrupo = idGrupo;
	}
	public String getDesripcion() {
		return desripcion;
	}
	public void setDesripcion(String desripcion) {
		this.desripcion = desripcion;
	}
	public int getIdRubro() {
		return idRubro;
	}
	public void setIdRubro(int idRubro) {
		this.idRubro = idRubro;
	}
	public String getDescRubro() {
		return descRubro;
	}
	public void setDescRubro(String descRubro) {
		this.descRubro = descRubro;
	}
	public String getIngresoEgreso() {
		return ingresoEgreso;
	}
	public void setIngresoEgreso(String ingresoEgreso) {
		this.ingresoEgreso = ingresoEgreso;
	}

}
