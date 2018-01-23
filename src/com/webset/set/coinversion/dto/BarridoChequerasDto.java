package com.webset.set.coinversion.dto;

import java.util.Date;

public class BarridoChequerasDto {
	private int noEmpresa;
	private int idBanco;
	private int noLinea;
	private int noControladora;
	private int secuencia;
	private int tipoSaldo;
	private double saldoChequera;
	private double saldoFinal;
	private double saldoMinimo;
	private double diferencia;
	private double sobregiro;
	private double montoSobregiro;
	private double saldoCredito;
	private double saldoCoinversion;
	private double traspaso;
	private Date fecValor;
	private String descBanco;
	private String idChequera;
	private String nomEmpresa;
	private String idDivisa;
	
	public int getTipoSaldo() {
		return tipoSaldo;
	}
	public void setTipoSaldo(int tipoSaldo) {
		this.tipoSaldo = tipoSaldo;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public int getSecuencia() {
		return secuencia;
	}
	public void setSecuencia(int secuencia) {
		this.secuencia = secuencia;
	}
	public int getNoLinea() {
		return noLinea;
	}
	public void setNoLinea(int noLinea) {
		this.noLinea = noLinea;
	}
	public double getSaldoChequera() {
		return saldoChequera;
	}
	public void setSaldoChequera(double saldoChequera) {
		this.saldoChequera = saldoChequera;
	}
	public double getSaldoFinal() {
		return saldoFinal;
	}
	public void setSaldoFinal(double saldoFinal) {
		this.saldoFinal = saldoFinal;
	}
	public double getSaldoMinimo() {
		return saldoMinimo;
	}
	public void setSaldoMinimo(double saldoMinimo) {
		this.saldoMinimo = saldoMinimo;
	}
	public double getDiferencia() {
		return diferencia;
	}
	public void setDiferencia(double diferencia) {
		this.diferencia = diferencia;
	}
	public double getSobregiro() {
		return sobregiro;
	}
	public void setSobregiro(double sobregiro) {
		this.sobregiro = sobregiro;
	}
	public double getMontoSobregiro() {
		return montoSobregiro;
	}
	public void setMontoSobregiro(double montoSobregiro) {
		this.montoSobregiro = montoSobregiro;
	}
	public double getSaldoCredito() {
		return saldoCredito;
	}
	public void setSaldoCredito(double saldoCredito) {
		this.saldoCredito = saldoCredito;
	}
	public Date getFecValor() {
		return fecValor;
	}
	public void setFecValor(Date fecValor) {
		this.fecValor = fecValor;
	}
	public String getDescBanco() {
		return descBanco;
	}
	public void setDescBanco(String descBanco) {
		this.descBanco = descBanco;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public String getNomEmpresa() {
		return nomEmpresa;
	}
	public void setNomEmpresa(String nomEmpresa) {
		this.nomEmpresa = nomEmpresa;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public double getTraspaso() {
		return traspaso;
	}
	public void setTraspaso(double traspaso) {
		this.traspaso = traspaso;
	}
	public int getNoControladora() {
		return noControladora;
	}
	public void setNoControladora(int noControladora) {
		this.noControladora = noControladora;
	}
	public double getSaldoCoinversion() {
		return saldoCoinversion;
	}
	public void setSaldoCoinversion(double saldoCoinversion) {
		this.saldoCoinversion = saldoCoinversion;
	}
}
