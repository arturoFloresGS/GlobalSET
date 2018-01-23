package com.webset.set.inversiones.dto;

public class MantenimientoDePapelDto {
	
	private String idPapel;
	private String idTipoValor;
	private String descripcion;
	private String confir;
	
	
	public String getIdPapel() {
		return idPapel;
	}
	public void setIdPapel(String idPapel) {
		this.idPapel = idPapel;
	}
	
	public String getidTipoValor() {
		return idTipoValor;
	}
	public void setIdTipoValor(String idTipoValor) {
		this.idTipoValor = idTipoValor;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public String getConfir() {
		return confir;
	}
	public void setConfirm(String confir) {
		this.confir = confir;
	}

}
