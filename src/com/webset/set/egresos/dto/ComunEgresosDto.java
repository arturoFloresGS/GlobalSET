package com.webset.set.egresos.dto;

import java.util.Date;

public class ComunEgresosDto {
	
	private String cveControl;
	private Date fechaHoy;
	private int count;
	private double suma;
	private int idGrupoEmpresas;
	private int idGrupoRubros;
	private Date fechaPago;
	private int agrupaEmpChe;
	private String psDivision;
	private boolean compraVtaTransfer;
	private Date fecha;
	
	private int idBanco;
	private String idChequera;
	private String descBanco;
	
	public String getCveControl() {
		return cveControl;
	}
	public void setCveControl(String cveControl) {
		this.cveControl = cveControl;
	}
	public Date getFechaHoy() {
		return fechaHoy;
	}
	public void setFechaHoy(Date fechaHoy) {
		this.fechaHoy = fechaHoy;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public double getSuma() {
		return suma;
	}
	public void setSuma(double suma) {
		this.suma = suma;
	}
	public int getIdGrupoEmpresas() {
		return idGrupoEmpresas;
	}
	public void setIdGrupoEmpresas(int idGrupoEmpresas) {
		this.idGrupoEmpresas = idGrupoEmpresas;
	}
	public int getIdGrupoRubros() {
		return idGrupoRubros;
	}
	public void setIdGrupoRubros(int idGrupoRubros) {
		this.idGrupoRubros = idGrupoRubros;
	}
	public Date getFechaPago() {
		return fechaPago;
	}
	public void setFechaPago(Date fechaPago) {
		this.fechaPago = fechaPago;
	}
	public int getAgrupaEmpChe() {
		return agrupaEmpChe;
	}
	public void setAgrupaEmpChe(int agrupaEmpChe) {
		this.agrupaEmpChe = agrupaEmpChe;
	}
	public String getPsDivision() {
		return psDivision;
	}
	public void setPsDivision(String psDivision) {
		this.psDivision = psDivision;
	}
	public boolean isCompraVtaTransfer() {
		return compraVtaTransfer;
	}
	public void setCompraVtaTransfer(boolean compraVtaTransfer) {
		this.compraVtaTransfer = compraVtaTransfer;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
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
	public String getDescBanco() {
		return descBanco;
	}
	public void setDescBanco(String descBanco) {
		this.descBanco = descBanco;
	}
}
