package com.webset.set.financiamiento.dto;

public class AvalGarantiaCreditoALDto {
	private String idFinanciamiento;
	private int idDisposicion;
	private int noEmpresaAvalada;
	private float montoDispuesto;
	private String fecAlta;
	private int usuarioAlta;
	private String clave;
	private char estatus;
	private int usuarioModif;
	private String fecModif;
	private int noEmpresaAvalista;
	
	
	
	public int getNoEmpresaAvalada() {
		return noEmpresaAvalada;
	}
	public void setNoEmpresaAvalada(int noEmpresaAvalada) {
		this.noEmpresaAvalada = noEmpresaAvalada;
	}
	public float getMontoDispuesto() {
		return montoDispuesto;
	}
	public void setMontoDispuesto(float montoDispuesto) {
		this.montoDispuesto = montoDispuesto;
	}
	public String getFecAlta() {
		return fecAlta;
	}
	public void setFecAlta(String fecAlta) {
		this.fecAlta = fecAlta;
	}
	public int getUsuarioAlta() {
		return usuarioAlta;
	}
	public void setUsuarioAlta(int usuarioAlta) {
		this.usuarioAlta = usuarioAlta;
	}
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		this.clave = clave;
	}
	public char getEstatus() {
		return estatus;
	}
	public void setEstatus(char estatus) {
		this.estatus = estatus;
	}
	public int getUsuarioModif() {
		return usuarioModif;
	}
	public void setUsuarioModif(int usuarioModif) {
		this.usuarioModif = usuarioModif;
	}
	public String getFecModif() {
		return fecModif;
	}
	public void setFecModif(String fecModif) {
		this.fecModif = fecModif;
	}
	public int getNoEmpresaAvalista() {
		return noEmpresaAvalista;
	}
	public void setNoEmpresaAvalista(int noEmpresaAvalista) {
		this.noEmpresaAvalista = noEmpresaAvalista;
	}
	public String getIdFinanciamiento() {
		return idFinanciamiento;
	}
	public void setIdFinanciamiento(String idFinanciamiento) {
		this.idFinanciamiento = idFinanciamiento;
	}
	public int getIdDisposicion() {
		return idDisposicion;
	}
	public void setIdDisposicion(int idDisposicion) {
		this.idDisposicion = idDisposicion;
	}
	
	
	
	
	/*
	 
	 no_empresa_avalada	smallint	Unchecked
id_financiamiento	varchar(30)	Unchecked
id_disposicion	smallint	Unchecked
monto_dispuesto	numeric(15, 2)	Unchecked
no_empresa_avalista	smallint	Unchecked
clave	varchar(8)	Unchecked
fec_alta	date	Unchecked
usuario_alta	smallint	Unchecked
estatus	char(1)	Checked
usuario_modif	smallint	Checked
fec_modif	date	Checked
		Unchecked
	 */

}
