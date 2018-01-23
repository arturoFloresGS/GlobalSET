package com.webset.set.inversiones.dto;

import java.util.Date;

public class CanastaInversionesDto {
	private int noEmpresa;
	private String nomEmpresa;
	private int noBanco;
	private String nomBanco;
	private String idChequera;
	private String descChequera;
	private String idDivisa;
	private double saldoFinal;
	private double saldoTransito;
	private double saldoBancario;
	private String saldoInvertir;
	private String dias;
	
	private int noEmpresaBenef;
	private String nomEmpresaBenef;
	private int noBancoBenef;
	private String nomBancoBenef;
	private String idChequeraBenef;
	private Date fechaSolicitud;
	private String descDivisa;
	private String concepto;
	private double importeTraspaso;
	private int idUsuarioAut;
	private String claveUsuario;
	private int noSolicitud;
	private String color;
	
	private String canasta;
	private double importe;
	private double interes;
	private double isr;
	private Date fechaVence;
	private double tasa;
	private Date fechaAlta;
	
	private double totImporte;
	private double totInteres;
	private double totIsr;
	private int noCanasta;
	private int noOrden;
	private double comision;
	private String folioBanco;
	
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public String getNomEmpresa() {
		return nomEmpresa;
	}
	public void setNomEmpresa(String nomEmpresa) {
		this.nomEmpresa = nomEmpresa;
	}
	public int getNoBanco() {
		return noBanco;
	}
	public void setNoBanco(int noBanco) {
		this.noBanco = noBanco;
	}
	public String getNomBanco() {
		return nomBanco;
	}
	public void setNomBanco(String nomBanco) {
		this.nomBanco = nomBanco;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public String getDescChequera() {
		return descChequera;
	}
	public void setDescChequera(String descChequera) {
		this.descChequera = descChequera;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public double getSaldoFinal() {
		return saldoFinal;
	}
	public void setSaldoFinal(double saldoFinal) {
		this.saldoFinal = saldoFinal;
	}
	public double getSaldoTransito() {
		return saldoTransito;
	}
	public void setSaldoTransito(double saldoTransito) {
		this.saldoTransito = saldoTransito;
	}
	public double getSaldoBancario() {
		return saldoBancario;
	}
	public void setSaldoBancario(double saldoBancario) {
		this.saldoBancario = saldoBancario;
	}
	public String getSaldoInvertir() {
		return saldoInvertir;
	}
	public void setSaldoInvertir(String saldoInvertir) {
		this.saldoInvertir = saldoInvertir;
	}
	public String getDias() {
		return dias;
	}
	public void setDias(String dias) {
		this.dias = dias;
	}
	public int getNoEmpresaBenef() {
		return noEmpresaBenef;
	}
	public void setNoEmpresaBenef(int noEmpresaBenef) {
		this.noEmpresaBenef = noEmpresaBenef;
	}
	public String getNomEmpresaBenef() {
		return nomEmpresaBenef;
	}
	public void setNomEmpresaBenef(String nomEmpresaBenef) {
		this.nomEmpresaBenef = nomEmpresaBenef;
	}
	public int getNoBancoBenef() {
		return noBancoBenef;
	}
	public void setNoBancoBenef(int noBancoBenef) {
		this.noBancoBenef = noBancoBenef;
	}
	public String getNomBancoBenef() {
		return nomBancoBenef;
	}
	public void setNomBancoBenef(String nomBancoBenef) {
		this.nomBancoBenef = nomBancoBenef;
	}
	public String getIdChequeraBenef() {
		return idChequeraBenef;
	}
	public void setIdChequeraBenef(String idChequeraBenef) {
		this.idChequeraBenef = idChequeraBenef;
	}
	public Date getFechaSolicitud() {
		return fechaSolicitud;
	}
	public void setFechaSolicitud(Date fechaSolicitud) {
		this.fechaSolicitud = fechaSolicitud;
	}
	public String getDescDivisa() {
		return descDivisa;
	}
	public void setDescDivisa(String descDivisa) {
		this.descDivisa = descDivisa;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public double getImporteTraspaso() {
		return importeTraspaso;
	}
	public void setImporteTraspaso(double importeTraspaso) {
		this.importeTraspaso = importeTraspaso;
	}
	public int getIdUsuarioAut() {
		return idUsuarioAut;
	}
	public void setIdUsuarioAut(int idUsuarioAut) {
		this.idUsuarioAut = idUsuarioAut;
	}
	public String getClaveUsuario() {
		return claveUsuario;
	}
	public void setClaveUsuario(String claveUsuario) {
		this.claveUsuario = claveUsuario;
	}
	public int getNoSolicitud() {
		return noSolicitud;
	}
	public void setNoSolicitud(int noSolicitud) {
		this.noSolicitud = noSolicitud;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getCanasta() {
		return canasta;
	}
	public void setCanasta(String canasta) {
		this.canasta = canasta;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
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
	public Date getFechaVence() {
		return fechaVence;
	}
	public void setFechaVence(Date fechaVence) {
		this.fechaVence = fechaVence;
	}
	public double getTasa() {
		return tasa;
	}
	public void setTasa(double tasa) {
		this.tasa = tasa;
	}
	public Date getFechaAlta() {
		return fechaAlta;
	}
	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}
	public double getTotImporte() {
		return totImporte;
	}
	public void setTotImporte(double totImporte) {
		this.totImporte = totImporte;
	}
	public double getTotInteres() {
		return totInteres;
	}
	public void setTotInteres(double totInteres) {
		this.totInteres = totInteres;
	}
	public double getTotIsr() {
		return totIsr;
	}
	public void setTotIsr(double totIsr) {
		this.totIsr = totIsr;
	}
	public int getNoCanasta() {
		return noCanasta;
	}
	public void setNoCanasta(int noCanasta) {
		this.noCanasta = noCanasta;
	}
	public int getNoOrden() {
		return noOrden;
	}
	public void setNoOrden(int noOrden) {
		this.noOrden = noOrden;
	}
	public double getComision() {
		return comision;
	}
	public void setComision(double comision) {
		this.comision = comision;
	}
	public String getFolioBanco() {
		return folioBanco;
	}
	public void setFolioBanco(String folioBanco) {
		this.folioBanco = folioBanco;
	}
}
