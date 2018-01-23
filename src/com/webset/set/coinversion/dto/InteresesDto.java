package com.webset.set.coinversion.dto;

import java.util.Date;

public class InteresesDto {

	private double interes;
	private double iva;
	private double isr;
	private double dIsr;
	private double dComision;
	private double dIva;
	private double interesDev;
	private double isrDev;
	private double comision;
	private double factor;
	private double tasaDeterminada;
	private double saldoPromedio;
	private double isrVencido;
	private double total;
	private double interesT;
	private double isrT;
	private double comisionT;
	private double interesDevT;
	private double isrDevT;
	private double ivaT;
	private int noEmpresa;
	private boolean bCalculo;
	private boolean bTasaPromedio;
	private boolean bTasaDeterminada;
	private String divisa;
	private String nomEmpresa;
	private Date fechaIni;
	private Date fechaFin;
	
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public boolean isBCalculo() {
		return bCalculo;
	}
	public void setBCalculo(boolean calculo) {
		bCalculo = calculo;
	}
	public double getInteres() {
		return interes;
	}
	public void setInteres(double interes) {
		this.interes = interes;
	}
	public double getIva() {
		return iva;
	}
	public void setIva(double iva) {
		this.iva = iva;
	}
	public double getIsr() {
		return isr;
	}
	public void setIsr(double isr) {
		this.isr = isr;
	}
	public double getInteresDev() {
		return interesDev;
	}
	public void setInteresDev(double interesDev) {
		this.interesDev = interesDev;
	}
	public double getIsrDev() {
		return isrDev;
	}
	public void setIsrDev(double isrDev) {
		this.isrDev = isrDev;
	}
	public double getComision() {
		return comision;
	}
	public void setComision(double comision) {
		this.comision = comision;
	}
	public double getFactor() {
		return factor;
	}
	public void setFactor(double factor) {
		this.factor = factor;
	}
	public double getTasaDeterminada() {
		return tasaDeterminada;
	}
	public void setTasaDeterminada(double tasaDeterminada) {
		this.tasaDeterminada = tasaDeterminada;
	}
	public double getSaldoPromedio() {
		return saldoPromedio;
	}
	public void setSaldoPromedio(double saldoPromedio) {
		this.saldoPromedio = saldoPromedio;
	}
	public double getIsrVencido() {
		return isrVencido;
	}
	public void setIsrVencido(double isrVencido) {
		this.isrVencido = isrVencido;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public String getDivisa() {
		return divisa;
	}
	public void setDivisa(String divisa) {
		this.divisa = divisa;
	}
	public String getNomEmpresa() {
		return nomEmpresa;
	}
	public void setNomEmpresa(String nomEmpresa) {
		this.nomEmpresa = nomEmpresa;
	}
	public Date getFechaIni() {
		return fechaIni;
	}
	public void setFechaIni(Date fechaIni) {
		this.fechaIni = fechaIni;
	}
	public Date getFechaFin() {
		return fechaFin;
	}
	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}
	public boolean isBTasaPromedio() {
		return bTasaPromedio;
	}
	public void setBTasaPromedio(boolean tasaPromedio) {
		bTasaPromedio = tasaPromedio;
	}
	public boolean isBTasaDeterminada() {
		return bTasaDeterminada;
	}
	public void setBTasaDeterminada(boolean tasaDeterminada) {
		bTasaDeterminada = tasaDeterminada;
	}
	public double getInteresT() {
		return interesT;
	}
	public void setInteresT(double interesT) {
		this.interesT = interesT;
	}
	public double getIsrT() {
		return isrT;
	}
	public void setIsrT(double isrT) {
		this.isrT = isrT;
	}
	public double getComisionT() {
		return comisionT;
	}
	public void setComisionT(double comisionT) {
		this.comisionT = comisionT;
	}
	public double getInteresDevT() {
		return interesDevT;
	}
	public void setInteresDevT(double interesDevT) {
		this.interesDevT = interesDevT;
	}
	public double getIsrDevT() {
		return isrDevT;
	}
	public void setIsrDevT(double isrDevT) {
		this.isrDevT = isrDevT;
	}
	public double getIvaT() {
		return ivaT;
	}
	public void setIvaT(double ivaT) {
		this.ivaT = ivaT;
	}
	public double getDIsr() {
		return dIsr;
	}
	public void setDIsr(double isr) {
		dIsr = isr;
	}
	public double getDComision() {
		return dComision;
	}
	public void setDComision(double comision) {
		dComision = comision;
	}
	public double getDIva() {
		return dIva;
	}
	public void setDIva(double iva) {
		dIva = iva;
	}
}
