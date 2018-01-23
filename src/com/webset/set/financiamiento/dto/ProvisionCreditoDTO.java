package com.webset.set.financiamiento.dto;

import java.util.Date;

public class ProvisionCreditoDTO {
	private int noFolioDet;
	private String idFinanciamiento;
	private int idDisposicion;
	private int  noEmpresa;
	private int  consecutivo;
	private int idBanco;
	private Date fecIniProv;
	private Date  fecFinProv;
	private String iDivisa;
	private double montoSaldo;
	private double montoProvision;
	private double tasa;
	private char estatus;
	private Date fecCalculoProv;
	private int usuario;
	private int usuarioModif;
	private Date fecModif;
	private int noFolioAmort;
	//provisionIntereses
	private String descEmpresa;
	private String descBanco;
	private String descDivisa;
	private String fecInicial;
	private String fecFinal;
	private int dias;
	private double capital;
	private double valorTasa;
	private double valor;
	private double puntos;

	public int getNoEmpresa() {
		return noEmpresa;
	}

	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}

	public String getDescEmpresa() {
		return descEmpresa;
	}

	public void setDescEmpresa(String descEmpresa) {
		this.descEmpresa = descEmpresa;
	}

	public String getDescBanco() {
		return descBanco;
	}

	public void setDescBanco(String descBanco) {
		this.descBanco = descBanco;
	}

	public String getDescDivisa() {
		return descDivisa;
	}

	public void setDescDivisa(String descDivisa) {
		this.descDivisa = descDivisa;
	}

	public String getFecInicial() {
		return fecInicial;
	}

	public void setFecInicial(String fecInicial) {
		this.fecInicial = fecInicial;
	}

	public String getFecFinal() {
		return fecFinal;
	}

	public void setFecFinal(String fecFinal) {
		this.fecFinal = fecFinal;
	}

	public int getDias() {
		return dias;
	}

	public void setDias(int dias) {
		this.dias = dias;
	}

	public double getCapital() {
		return capital;
	}

	public void setCapital(double capital) {
		this.capital = capital;
	}

	public double getValorTasa() {
		return valorTasa;
	}

	public void setValorTasa(double valorTasa) {
		this.valorTasa = valorTasa;
	}

	public int getNoFolioDet() {
		return noFolioDet;
	}

	public void setNoFolioDet(int noFolioDet) {
		this.noFolioDet = noFolioDet;
	}

	public String getIdFinanciamiento() {
		return idFinanciamiento;
	}

	public void setIdFinanciamiento(String idFinanciamiento) {
		this.idFinanciamiento = idFinanciamiento;
	}

	public int getIdDisposicion() {
		return idDisposicion;
	}

	public void setIdDisposicion(int idDisposicion) {
		this.idDisposicion = idDisposicion;
	}

	public int getNo_empresa() {
		return noEmpresa;
	}

	public void setNo_empresa(int no_empresa) {
		this.noEmpresa = no_empresa;
	}

	public int getConsecutivo() {
		return consecutivo;
	}

	public void setConsecutivo(int consecutivo) {
		this.consecutivo = consecutivo;
	}

	public int getIdBanco() {
		return idBanco;
	}

	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}

	public Date getFecIniProv() {
		return fecIniProv;
	}

	public void setFecIniProv(Date fecIniProv) {
		this.fecIniProv = fecIniProv;
	}

	public Date getFecFinProv() {
		return fecFinProv;
	}

	public void setFecFinProv(Date fecFinProv) {
		this.fecFinProv = fecFinProv;
	}

	public String getiDivisa() {
		return iDivisa;
	}

	public void setiDivisa(String iDivisa) {
		this.iDivisa = iDivisa;
	}

	public double getMontoSaldo() {
		return montoSaldo;
	}

	public void setMontoSaldo(double montoSaldo) {
		this.montoSaldo = montoSaldo;
	}

	public double getMontoProvision() {
		return montoProvision;
	}

	public void setMontoProvision(double montoProvision) {
		this.montoProvision = montoProvision;
	}

	public double getTasa() {
		return tasa;
	}

	public void setTasa(double tasa) {
		this.tasa = tasa;
	}

	public char getEstatus() {
		return estatus;
	}

	public void setEstatus(char estatus) {
		this.estatus = estatus;
	}

	public Date getFecCalculoProv() {
		return fecCalculoProv;
	}

	public void setFecCalculoProv(Date fecCalculoProv) {
		this.fecCalculoProv = fecCalculoProv;
	}

	public int getUsuario() {
		return usuario;
	}

	public void setUsuario(int usuario) {
		this.usuario = usuario;
	}

	public int getUsuarioModif() {
		return usuarioModif;
	}

	public void setUsuarioModif(int usuarioModif) {
		this.usuarioModif = usuarioModif;
	}

	public Date getFecModif() {
		return fecModif;
	}

	public void setFecModif(Date fecModif) {
		this.fecModif = fecModif;
	}

	public int getNoFolioAmort() {
		return noFolioAmort;
	}

	public void setNoFolioAmort(int noFolioAmort) {
		this.noFolioAmort = noFolioAmort;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

	public double getPuntos() {
		return puntos;
	}

	public void setPuntos(double puntos) {
		this.puntos = puntos;
	}
	

}
