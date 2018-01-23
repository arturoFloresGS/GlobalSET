package com.webset.set.conciliacionbancoset.dto;

import java.util.Date;

public class CruceConciliaDto {
	private int grupo;
	private int secuencia;
	private int noFolio1;
	private int noFolio2;
	private String fuenteMovto;
	private String tipoConcilia;
	private Date fecAlta;
	private int usuarioAlta;
	private int noEmpresa;
	private int idBanco;
	private String idChequera;
	private Date fecExporta;
	
	public int getGrupo() {
		return grupo;
	}
	public void setGrupo(int grupo) {
		this.grupo = grupo;
	}
	public int getSecuencia() {
		return secuencia;
	}
	public void setSecuencia(int secuencia) {
		this.secuencia = secuencia;
	}
	public int getNoFolio1() {
		return noFolio1;
	}
	public void setNoFolio1(int noFolio1) {
		this.noFolio1 = noFolio1;
	}
	public int getNoFolio2() {
		return noFolio2;
	}
	public void setNoFolio2(int noFolio2) {
		this.noFolio2 = noFolio2;
	}
	public String getFuenteMovto() {
		return fuenteMovto;
	}
	public void setFuenteMovto(String fuenteMovto) {
		this.fuenteMovto = fuenteMovto;
	}
	public String getTipoConcilia() {
		return tipoConcilia;
	}
	public void setTipoConcilia(String tipoConcilia) {
		this.tipoConcilia = tipoConcilia;
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
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public Date getFecExporta() {
		return fecExporta;
	}
	public void setFecExporta(Date fecExporta) {
		this.fecExporta = fecExporta;
	}

}
