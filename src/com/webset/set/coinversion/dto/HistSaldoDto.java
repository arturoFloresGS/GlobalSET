package com.webset.set.coinversion.dto;

import java.util.Date;

public class HistSaldoDto {

	private int noEmpresa;
	private int idTipo_saldo;
	private Date fecValor;
	private int noLinea;
	private int noCuenta;
	private double importe;
	private String concepto;
	private double saldoIni;
	private double depositos;
	private double retiros;
	private double saldoFin;
	private int etiqueta;
	private int secuencia;
	public int getSecuencia() {
		return secuencia;
	}
	public void setSecuencia(int secuencia) {
		this.secuencia = secuencia;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public int getIdTipo_saldo() {
		return idTipo_saldo;
	}
	public void setIdTipo_saldo(int idTipo_saldo) {
		this.idTipo_saldo = idTipo_saldo;
	}
	public Date getFecValor() {
		return fecValor;
	}
	public void setFecValor(Date fecValor) {
		this.fecValor = fecValor;
	}
	public int getNoLinea() {
		return noLinea;
	}
	public void setNoLinea(int noLinea) {
		this.noLinea = noLinea;
	}
	public int getNoCuenta() {
		return noCuenta;
	}
	public void setNoCuenta(int noCuenta) {
		this.noCuenta = noCuenta;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public double getSaldoIni() {
		return saldoIni;
	}
	public void setSaldoIni(double saldoIni) {
		this.saldoIni = saldoIni;
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
	public double getSaldoFin() {
		return saldoFin;
	}
	public void setSaldoFin(double saldoFin) {
		this.saldoFin = saldoFin;
	}
	public int getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(int etiqueta) {
		this.etiqueta = etiqueta;
	}
}
