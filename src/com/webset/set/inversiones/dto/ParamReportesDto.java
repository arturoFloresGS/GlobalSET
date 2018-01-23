package com.webset.set.inversiones.dto;

import java.util.Date;

public class ParamReportesDto {
	
	private int idEmpresa;
	private Date fecInicial;
	private Date fecFinal;
	private String idDivisa; 
	private boolean bTodasDivisas;
	private boolean bActual;
	
	public boolean isBTodasDivisas() {
		return bTodasDivisas;
	}
	public void setBTodasDivisas(boolean todasDivisas) {
		bTodasDivisas = todasDivisas;
	}
	public boolean isBActual() {
		return bActual;
	}
	public void setBActual(boolean actual) {
		bActual = actual;
	}
	public int getIdEmpresa() {
		return idEmpresa;
	}
	public void setIdEmpresa(int idEmpresa) {
		this.idEmpresa = idEmpresa;
	}
	public Date getFecInicial() {
		return fecInicial;
	}
	public void setFecInicial(Date fecInicial) {
		this.fecInicial = fecInicial;
	}
	public Date getFecFinal() {
		return fecFinal;
	}
	public void setFecFinal(Date fecFinal) {
		this.fecFinal = fecFinal;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	
}
