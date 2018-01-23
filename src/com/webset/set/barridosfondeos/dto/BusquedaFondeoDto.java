package com.webset.set.barridosfondeos.dto;

import java.util.Date;

public class BusquedaFondeoDto {
	
	private int idEmpresa;
	private int idEmpresaRaiz;
	private String idDivisa;
	private Date fechaHoy;
	private int noLinea;
	private int idBanco;
	private int idUsuario;
	private int TipoSaldo;
	private boolean bTodaslasChequeras;
	private String sTipoBusqueda;
	private String idChequera;
	private String cveControl;
	private boolean chkMismoBanco;
	private String nomEmpresaRaiz; 

	
	
	public int getTipoSaldo() {
		return TipoSaldo;
	}
	public void setTipoSaldo(int tipoSaldo) {
		TipoSaldo = tipoSaldo;
	}
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
	
	public String getCveControl() {
		return cveControl;
	}
	public void setCveControl(String cveControl) {
		this.cveControl = cveControl;
	}
	public String getNomEmpresaRaiz() {
		return nomEmpresaRaiz;
	}
	public void setNomEmpresaRaiz(String nomEmpresaRaiz) {
		this.nomEmpresaRaiz = nomEmpresaRaiz;
	}
}

