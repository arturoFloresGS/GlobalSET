package com.webset.set.utilerias.dto;

public class ReglasNegocioDto {

	private int idRegla;
	private String reglaNegocio;
	private String tipoRegla;
	private String fechaCaptura;
	private int usuarioCaptura;
	private String claveUsuarioCaptura;
	private String generaPropuestaAutomatica;
	private String usuarios;
	private String indicador;
	private String descuento;
	private int longChequera;
	
	public int getIdRegla() {
		return idRegla;
	}
	public void setIdRegla(int idRegla) {
		this.idRegla = idRegla;
	}
	public String getReglaNegocio() {
		return reglaNegocio;
	}
	public void setReglaNegocio(String reglaNegocio) {
		this.reglaNegocio = reglaNegocio;
	}
	public String getTipoRegla() {
		return tipoRegla;
	}
	public void setTipoRegla(String tipoRegla) {
		this.tipoRegla = tipoRegla;
	}
	public String getFechaCaptura() {
		return fechaCaptura;
	}
	public void setFechaCaptura(String fechaCaptura) {
		this.fechaCaptura = fechaCaptura;
	}
	public int getUsuarioCaptura() {
		return usuarioCaptura;
	}
	public void setUsuarioCaptura(int usuarioCaptura) {
		this.usuarioCaptura = usuarioCaptura;
	}
	public String getGeneraPropuestaAutomatica() {
		return generaPropuestaAutomatica;
	}
	public void setGeneraPropuestaAutomatica(String generaPropuestaAutomatica) {
		this.generaPropuestaAutomatica = generaPropuestaAutomatica;
	}
	public String getUsuarios() {
		return usuarios;
	}
	public void setUsuarios(String usuarios) {
		this.usuarios = usuarios;
	}
	public String getIndicador() {
		return indicador;
	}
	public void setIndicador(String indicador) {
		this.indicador = indicador;
	}
	public String getClaveUsuarioCaptura() {
		return claveUsuarioCaptura;
	}
	public void setClaveUsuarioCaptura(String claveUsuarioCaptura) {
		this.claveUsuarioCaptura = claveUsuarioCaptura;
	}
	public String getDescuento() {
		return descuento;
	}
	public void setDescuento(String descuento) {
		this.descuento = descuento;
	}
	public int getLongChequera() {
		return longChequera;
	}
	public void setLongChequera(int longChequera) {
		this.longChequera = longChequera;
	}
 	
}
