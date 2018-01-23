package com.webset.set.seguridad.dto;

public class SegCatTipoComponenteDto {
	
	private int idTipoComponente;
	private String descripcion;
	private String estatus;
	
	public int getIdTipoComponente() {
		return idTipoComponente;
	}
	public void setIdTipoComponente(int idTipoComponente) {
		this.idTipoComponente = idTipoComponente;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getEstatus() {
		return estatus;
	}
	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}
}
