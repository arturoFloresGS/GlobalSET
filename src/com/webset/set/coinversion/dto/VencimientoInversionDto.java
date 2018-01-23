package com.webset.set.coinversion.dto;

import java.util.Date;

public class VencimientoInversionDto {
	private int noEmpresa;
	private int noControladora;
	private double interes;
	private double isr;
	private double comision;
	private double tasaRend;
	private double interesDev;
	private double saldoPromedio;
	private double iva;
	private Date fecDesde;
	private Date fecHasta;
	private String idDivisa;
	private String bComisionCap;
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public int getNoControladora() {
		return noControladora;
	}
	public void setNoControladora(int noControladora) {
		this.noControladora = noControladora;
	}
	public double getInteres() {
		return interes;
	}
	public void setInteres(double interes) {
		this.interes = interes;
	}
	public double getIsr() {
		return isr;
	}
	public void setIsr(double isr) {
		this.isr = isr;
	}
	public double getComision() {
		return comision;
	}
	public void setComision(double comision) {
		this.comision = comision;
	}
	public double getTasaRend() {
		return tasaRend;
	}
	public void setTasaRend(double tasaRend) {
		this.tasaRend = tasaRend;
	}
	public double getInteresDev() {
		return interesDev;
	}
	public void setInteresDev(double interesDev) {
		this.interesDev = interesDev;
	}
	public double getSaldoPromedio() {
		return saldoPromedio;
	}
	public void setSaldoPromedio(double saldoPromedio) {
		this.saldoPromedio = saldoPromedio;
	}
	public double getIva() {
		return iva;
	}
	public void setIva(double iva) {
		this.iva = iva;
	}
	public Date getFecDesde() {
		return fecDesde;
	}
	public void setFecDesde(Date fecDesde) {
		this.fecDesde = fecDesde;
	}
	public Date getFecHasta() {
		return fecHasta;
	}
	public void setFecHasta(Date fecHasta) {
		this.fecHasta = fecHasta;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public String getBComisionCap() {
		return bComisionCap;
	}
	public void setBComisionCap(String comisionCap) {
		bComisionCap = comisionCap;
	}
	

}
