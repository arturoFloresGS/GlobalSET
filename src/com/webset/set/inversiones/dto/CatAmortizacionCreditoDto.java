package com.webset.set.inversiones.dto;

import java.util.Date;

public class CatAmortizacionCreditoDto {
	//Atributos de la clase.
	private int idDisposicion;
	private int idAmortizacion;
	private int idUsuarioAlta;
	
	private String idContrato;
	private String estatus;
	private String idDivisa;
	
	private Date fecVencimiento;
	
	private Double capital;
	private Double interes;
	private Double gasto;
	private Double comision;
	private Double tasaVigente;
	private Double saldoInsoluto;

	//Getters and Setters
	public String getIdDivisa(){
		return this.idDivisa;
	}
	public void setIdDivisa(String idDivisa){
		this.idDivisa = idDivisa;
	}
	public int getIdDisposicion() {
		return idDisposicion;
	}
	public void setIdDisposicion(int idDisposicion) {
		this.idDisposicion = idDisposicion;
	}
	public int getIdAmortizacion() {
		return idAmortizacion;
	}
	public void setIdAmortizacion(int idAmortizacion) {
		this.idAmortizacion = idAmortizacion;
	}
	public int getIdUsuarioAlta() {
		return idUsuarioAlta;
	}
	public void setIdUsuarioAlta(int idUsuarioAlta) {
		this.idUsuarioAlta = idUsuarioAlta;
	}
	public String getIdContrato() {
		return idContrato;
	}
	public void setIdContrato(String idContrato) {
		this.idContrato = idContrato;
	}
	public String getEstatus() {
		return estatus;
	}
	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}
	public Date getFecVencimiento() {
		return fecVencimiento;
	}
	public void setFecVencimiento(Date fecVencimiento) {
		this.fecVencimiento = fecVencimiento;
	}
	public Double getCapital() {
		return capital;
	}
	public void setCapital(Double capital) {
		this.capital = capital;
	}
	public Double getInteres() {
		return interes;
	}
	public void setInteres(Double interes) {
		this.interes = interes;
	}
	public Double getGasto() {
		return gasto;
	}
	public void setGasto(Double gasto) {
		this.gasto = gasto;
	}
	public Double getComision() {
		return comision;
	}
	public void setComision(Double comision) {
		this.comision = comision;
	}
	public Double getTasaVigente() {
		return tasaVigente;
	}
	public void setTasaVigente(Double tasaVigente) {
		this.tasaVigente = tasaVigente;
	}
	public Double getSaldoInsoluto() {
		return saldoInsoluto;
	}
	public void setSaldoInsoluto(Double saldoInsoluto) {
		this.saldoInsoluto = saldoInsoluto;
	}
}
