package com.webset.set.coinversion.dto;

import java.util.Date;

public class ParamBusquedaFondeoDto {
	
	private int idEmpresa;
	private int idEmpresaRaiz;
	private String idDivisa;
	private Date fechaHoy;
	private int noLinea;
	private int idBanco;
	private int idUsuario;
	private boolean bTodaslasChequeras;
	private boolean bUbicacionCCM;
	private String sTipoBusqueda;
	private String idChequera;
	private String cveControl;
	private boolean chkMismoBanco;
	private boolean bMontoMinFondeo;
	private double montoMinFondeo;
	
	public int getIdEmpresa() {
		return idEmpresa;
	}
	public void setIdEmpresa(int idEmpresa) {
		this.idEmpresa = idEmpresa;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public Date getFechaHoy() {
		return fechaHoy;
	}
	public void setFechaHoy(Date fechaHoy) {
		this.fechaHoy = fechaHoy;
	}
	public int getNoLinea() {
		return noLinea;
	}
	public void setNoLinea(int noLinea) {
		this.noLinea = noLinea;
	}
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public boolean isBTodaslasChequeras() {
		return bTodaslasChequeras;
	}
	public void setBTodaslasChequeras(boolean todaslasChequeras) {
		bTodaslasChequeras = todaslasChequeras;
	}
	public boolean isBUbicacionCCM() {
		return bUbicacionCCM;
	}
	public void setBUbicacionCCM(boolean ubicacionCCM) {
		bUbicacionCCM = ubicacionCCM;
	}
	public String getSTipoBusqueda() {
		return sTipoBusqueda;
	}
	public void setSTipoBusqueda(String tipoBusqueda) {
		sTipoBusqueda = tipoBusqueda;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public int getIdEmpresaRaiz() {
		return idEmpresaRaiz;
	}
	public void setIdEmpresaRaiz(int idEmpresaRaiz) {
		this.idEmpresaRaiz = idEmpresaRaiz;
	}
	public boolean isChkMismoBanco() {
		return chkMismoBanco;
	}
	public void setChkMismoBanco(boolean chkMismoBanco) {
		this.chkMismoBanco = chkMismoBanco;
	}
	public boolean isBMontoMinFondeo() {
		return bMontoMinFondeo;
	}
	public void setBMontoMinFondeo(boolean montoMinFondeo) {
		bMontoMinFondeo = montoMinFondeo;
	}
	public double getMontoMinFondeo() {
		return montoMinFondeo;
	}
	public void setMontoMinFondeo(double montoMinFondeo) {
		this.montoMinFondeo = montoMinFondeo;
	}
	public String getCveControl() {
		return cveControl;
	}
	public void setCveControl(String cveControl) {
		this.cveControl = cveControl;
	}
}
