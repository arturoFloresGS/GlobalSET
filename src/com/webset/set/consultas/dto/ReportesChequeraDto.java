package com.webset.set.consultas.dto;

import java.util.Date;

public class ReportesChequeraDto {
	
	Integer noEmpresa;
	Integer idBanco;
	
	String idChequera;
	String sFechaDesde;
	String sFechaHasta;
	
	Date fechaDesde;
	Date fechaHasta;
	public Integer getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(Integer noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public Integer getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(Integer idBanco) {
		this.idBanco = idBanco;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public String getsFechaDesde() {
		return sFechaDesde;
	}
	public void setsFechaDesde(String sFechaDesde) {
		this.sFechaDesde = sFechaDesde;
	}
	public String getsFechaHasta() {
		return sFechaHasta;
	}
	public void setsFechaHasta(String sFechaHasta) {
		this.sFechaHasta = sFechaHasta;
	}
	public Date getFechaDesde() {
		return fechaDesde;
	}
	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}
	public Date getFechaHasta() {
		return fechaHasta;
	}
	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}
	
	
}
