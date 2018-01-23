package com.webset.set.prestamosinterempresas.dto;

import java.util.Date;

public class HistSaldoDto {
	
	private int noEmpresa;
	private Date fecValor;
	private String concepto;
	private double sdoIni;
	private double depositos;
	private double retiros;
	private double sdoFin;
	private int etiqueta;
	
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public Date getFecValor() {
		return fecValor;
	}
	public void setFecValor(Date fecValor) {
		this.fecValor = fecValor;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public double getSdoIni() {
		return sdoIni;
	}
	public void setSdoIni(double sdoIni) {
		this.sdoIni = sdoIni;
	}
	public double getDepositos() {
		return depositos;
	}
	public void setDepositos(double depositos) {
		this.depositos = depositos;
	}
	public double getRetiros() {
		return retiros;
	}
	public void setRetiros(double retiros) {
		this.retiros = retiros;
	}
	public double getSdoFin() {
		return sdoFin;
	}
	public void setSdoFin(double sdoFin) {
		this.sdoFin = sdoFin;
	}
	public int getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(int etiqueta) {
		this.etiqueta = etiqueta;
	}
	
}
