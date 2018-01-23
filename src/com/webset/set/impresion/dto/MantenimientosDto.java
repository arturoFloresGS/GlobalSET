package com.webset.set.impresion.dto;

public class MantenimientosDto {
	//Para la pantalla de Mantenimiento de Firmantes
	int noPersona;
	String nombre;
	String pathFirma;
	String depto;
	String puesto;
	
	//Para la pantalla de Mantenimiento de Firmas
	int idBanco;
	String descBanco;
	String idChequera;
	String bDeter;
	String tipoFirma;
	double limite;
	String nivel;
	
	private int noImpresora;
	private String descCaja;
	private int noCharola;
	private int cveCaja;
	
	public String getBDeter() {
		return bDeter;
	}
	public void setBDeter(String deter) {
		bDeter = deter;
	}
	public String getTipoFirma() {
		return tipoFirma;
	}
	public void setTipoFirma(String tipoFirma) {
		this.tipoFirma = tipoFirma;
	}
	public double getLimite() {
		return limite;
	}
	public void setLimite(double limite) {
		this.limite = limite;
	}
	public String getNivel() {
		return nivel;
	}
	public void setNivel(String nivel) {
		this.nivel = nivel;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public String getDescBanco() {
		return descBanco;
	}
	public void setDescBanco(String descBanco) {
		this.descBanco = descBanco;
	}
	public int getNoPersona() {
		return noPersona;
	}
	public void setNoPersona(int noPersona) {
		this.noPersona = noPersona;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getPathFirma() {
		return pathFirma;
	}
	public void setPathFirma(String pathFirma) {
		this.pathFirma = pathFirma;
	}
	public String getDepto() {
		return depto;
	}
	public void setDepto(String depto) {
		this.depto = depto;
	}
	public String getPuesto() {
		return puesto;
	}
	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}
	public int getNoImpresora() {
		return noImpresora;
	}
	public void setNoImpresora(int noImpresora) {
		this.noImpresora = noImpresora;
	}
	public String getDescCaja() {
		return descCaja;
	}
	public void setDescCaja(String descCaja) {
		this.descCaja = descCaja;
	}
	public int getNoCharola() {
		return noCharola;
	}
	public void setNoCharola(int noCharola) {
		this.noCharola = noCharola;
	}
	public int getCveCaja() {
		return cveCaja;
	}
	public void setCveCaja(int cveCaja) {
		this.cveCaja = cveCaja;
	}
}
