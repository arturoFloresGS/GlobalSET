package com.webset.set.prestamosinterempresas.dto;

import java.util.Date;

/**
 * Esta clase representa la tabla Interes_Prestamo
 * @author Cristian García García
 */
public class InteresPrestamoDto {
	
	private int noEmpresa;
	private Date fecInicioCalculo;
	private Date fecFinCalculo;
	private String idDivisa;
	private double tasa;
	private double saldoPromedio;
	private double interes;
	private double iva;
	
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public Date getFecInicioCalculo() {
		return fecInicioCalculo;
	}
	public void setFecInicioCalculo(Date fecInicioCalculo) {
		this.fecInicioCalculo = fecInicioCalculo;
	}
	public Date getFecFinCalculo() {
		return fecFinCalculo;
	}
	public void setFecFinCalculo(Date fecFinCalculo) {
		this.fecFinCalculo = fecFinCalculo;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public double getTasa() {
		return tasa;
	}
	public void setTasa(double tasa) {
		this.tasa = tasa;
	}
	public double getSaldoPromedio() {
		return saldoPromedio;
	}
	public void setSaldoPromedio(double saldoPromedio) {
		this.saldoPromedio = saldoPromedio;
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

}
