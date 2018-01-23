package com.webset.set.utilerias.dto;

public class CuadrantesDto {

	private int idUsuario;
	private int result;
	private String mensaje;
	private String nomForma;
	private String folios;
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	public String getNomForma() {
		return nomForma;
	}
	public void setNomForma(String nomForma) {
		this.nomForma = nomForma;
	}
	public String getFolios() {
		return folios;
	}
	public void setFolios(String folios) {
		this.folios = folios;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
}
