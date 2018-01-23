package com.webset.set.egresos.dto;

public class ControlProyectoDto {
	private int idFuncionalidad;
	private int idModulo;
	private String descFuncionalidad;
	private String bHabilita;
	
	public int getIdFuncionalidad() {
		return idFuncionalidad;
	}
	public void setIdFuncionalidad(int idFuncionalidad) {
		this.idFuncionalidad = idFuncionalidad;
	}
	public int getIdModulo() {
		return idModulo;
	}
	public void setIdModulo(int idModulo) {
		this.idModulo = idModulo;
	}
	public String getDescFuncionalidad() {
		return descFuncionalidad;
	}
	public void setDescFuncionalidad(String descFuncionalidad) {
		this.descFuncionalidad = descFuncionalidad;
	}
	public String getBHabilita() {
		return bHabilita;
	}
	public void setBHabilita(String habilita) {
		bHabilita = habilita;
	}

}
