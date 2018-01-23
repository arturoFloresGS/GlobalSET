package com.webset.set.seguridad.dto;

public class SegMantenimientoCatalogosDto {
	private int idEmpresa;
	private String nombreCatalogo;
	private String descCatalogo;
	private String tituloColumnas;
	private String campos;
	private String botones;
	
	public int getIdEmpresa() {
		return idEmpresa;
	}
	public void setIdEmpresa(int idEmpresa) {
		this.idEmpresa = idEmpresa;
	}
	public String getNombreCatalogo() {
		return nombreCatalogo;
	}
	public void setNombreCatalogo(String nombreCatalogo) {
		this.nombreCatalogo = nombreCatalogo;
	}
	public String getDescCatalogo() {
		return descCatalogo;
	}
	public void setDescCatalogo(String descCatalogo) {
		this.descCatalogo = descCatalogo;
	}
	public String getTituloColumnas() {
		return tituloColumnas;
	}
	public void setTituloColumnas(String tituloColumnas) {
		this.tituloColumnas = tituloColumnas;
	}
	public String getCampos() {
		return campos;
	}
	public void setCampos(String campos) {
		this.campos = campos;
	}
	public String getBotones() {
		return botones;
	}
	public void setBotones(String botones) {
		this.botones = botones;
	}
}
