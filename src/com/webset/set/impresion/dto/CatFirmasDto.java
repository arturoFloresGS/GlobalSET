package com.webset.set.impresion.dto;

public class CatFirmasDto {
	private String nombre;
	private String ctaNo;
	private String tipoFirma;
	private String pathFirma;
	private int bDeter;
	private int noPersona;
	private int numFirmas;
	public int getNumFirmas() {
		return numFirmas;
	}
	public void setNumFirmas(int numFirmas) {
		this.numFirmas = numFirmas;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getCtaNo() {
		return ctaNo;
	}
	public void setCtaNo(String ctaNo) {
		this.ctaNo = ctaNo;
	}
	public String getTipoFirma() {
		return tipoFirma;
	}
	public void setTipoFirma(String tipoFirma) {
		this.tipoFirma = tipoFirma;
	}
	public int getBDeter() {
		return bDeter;
	}
	public void setBDeter(int deter) {
		bDeter = deter;
	}
	public int getNoPersona() {
		return noPersona;
	}
	public void setNoPersona(int noPersona) {
		this.noPersona = noPersona;
	}
	public String getPathFirma() {
		return pathFirma;
	}
	public void setPathFirma(String pathFirma) {
		this.pathFirma = pathFirma;
	}

}
