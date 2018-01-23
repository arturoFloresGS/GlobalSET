package com.webset.set.impresion.dto;

import java.util.Date;

public class LayoutProteccionDto {
	
	private int registro;
	private int idBanco;
	private int noCheque;
	private double importe;
	private String beneficiario;
	private String idChequera;
	private String concepto;
	private Date fecImprime;
	private Date fecHoy;
	private String fecPropuesta;
	
	public Date getFecHoy() {
		return fecHoy;
	}
	public void setFecHoy(Date fecHoy) {
		this.fecHoy = fecHoy;
	}
	public int getRegistro() {
		return registro;
	}
	public void setRegistro(int registro) {
		this.registro = registro;
	}
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public int getNoCheque() {
		return noCheque;
	}
	public void setNoCheque(int noCheque) {
		this.noCheque = noCheque;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
	public String getBeneficiario() {
		return beneficiario;
	}
	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public Date getFecImprime() {
		return fecImprime;
	}
	public void setFecImprime(Date fecImprime) {
		this.fecImprime = fecImprime;
	}
	public String getFecPropuesta() {
		return fecPropuesta;
	}
	public void setFecPropuesta(String fecPropuesta) {
		this.fecPropuesta = fecPropuesta;
	}
}
