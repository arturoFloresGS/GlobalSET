package com.webset.set.conciliacionbancoset.dto;

import java.util.Date;

public class CriteriosReporteDto {
	private int iNoEmpresa;
	private int iIdBanco;
	private boolean bValChequera;
	private Date dFechaIni;
	private Date dFechaFin;
	private String sChequera;
	private String idEstatusCb1;
	private String idEstatusCb2;
	private String sTipoConcilia;
	public String getSTipoConcilia() {
		return sTipoConcilia;
	}
	public void setSTipoConcilia(String tipoConcilia) {
		sTipoConcilia = tipoConcilia;
	}
	public int getINoEmpresa() {
		return iNoEmpresa;
	}
	public void setINoEmpresa(int noEmpresa) {
		iNoEmpresa = noEmpresa;
	}
	public int getIIdBanco() {
		return iIdBanco;
	}
	public void setIIdBanco(int idBanco) {
		iIdBanco = idBanco;
	}
	public boolean isBValChequera() {
		return bValChequera;
	}
	public void setBValChequera(boolean valChequera) {
		bValChequera = valChequera;
	}
	public Date getDFechaIni() {
		return dFechaIni;
	}
	public void setDFechaIni(Date fechaIni) {
		dFechaIni = fechaIni;
	}
	public Date getDFechaFin() {
		return dFechaFin;
	}
	public void setDFechaFin(Date fechaFin) {
		dFechaFin = fechaFin;
	}
	public String getSChequera() {
		return sChequera;
	}
	public void setSChequera(String chequera) {
		sChequera = chequera;
	}
	public String getIdEstatusCb1() {
		return idEstatusCb1;
	}
	public void setIdEstatusCb1(String idEstatusCb1) {
		this.idEstatusCb1 = idEstatusCb1;
	}
	public String getIdEstatusCb2() {
		return idEstatusCb2;
	}
	public void setIdEstatusCb2(String idEstatusCb2) {
		this.idEstatusCb2 = idEstatusCb2;
	}
	
}
