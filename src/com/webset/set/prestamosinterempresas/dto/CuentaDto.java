package com.webset.set.prestamosinterempresas.dto;

import java.util.Date;

public class CuentaDto {
	
	private int noEmpresa;
	private int noLinea;
	private int noPersona;
	private int noCuenta;
	private Date fecAlta;
	private int usuarioAlta;
	private String idTipoCuenta;
	
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public int getNoLinea() {
		return noLinea;
	}
	public void setNoLinea(int noLinea) {
		this.noLinea = noLinea;
	}
	public int getNoPersona() {
		return noPersona;
	}
	public void setNoPersona(int noPersona) {
		this.noPersona = noPersona;
	}
	public int getNoCuenta() {
		return noCuenta;
	}
	public void setNoCuenta(int noCuenta) {
		this.noCuenta = noCuenta;
	}
	public Date getFecAlta() {
		return fecAlta;
	}
	public void setFecAlta(Date fecAlta) {
		this.fecAlta = fecAlta;
	}
	public int getUsuarioAlta() {
		return usuarioAlta;
	}
	public void setUsuarioAlta(int usuarioAlta) {
		this.usuarioAlta = usuarioAlta;
	}
	public String getIdTipoCuenta() {
		return idTipoCuenta;
	}
	public void setIdTipoCuenta(String idTipoCuenta) {
		this.idTipoCuenta = idTipoCuenta;
	}
 
 
}
