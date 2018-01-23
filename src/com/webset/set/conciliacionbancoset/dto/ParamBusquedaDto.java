package com.webset.set.conciliacionbancoset.dto;

import java.util.Date;

public class ParamBusquedaDto {
	private int idBanco;
	private int idUsuario;
	private int idEmpresa;
	private int resultado;
	private int idBanco2;
	private String mensaje;
	private String descBanco;
	private String idChequera;
	private String estatus;
	private Date fechaIni;
	private Date fechaFin;
	private String tipoMovimiento;
	
	private String sFechaIni;
	private String sFechaFin;
	
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public String getDescBanco() {
		return descBanco;
	}
	public void setDescBanco(String descBanco) {
		this.descBanco = descBanco;
	}
	public Date getFechaIni() {
		return fechaIni;
	}
	public void setFechaIni(Date fechaIni) {
		this.fechaIni = fechaIni;
	}
	public Date getFechaFin() {
		return fechaFin;
	}
	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}
	public int getIdEmpresa() {
		return idEmpresa;
	}
	public void setIdEmpresa(int idEmpresa) {
		this.idEmpresa = idEmpresa;
	}
	public int getResultado() {
		return resultado;
	}
	public void setResultado(int resultado) {
		this.resultado = resultado;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public int getIdBanco2() {
		return idBanco2;
	}
	public void setIdBanco2(int idBanco2) {
		this.idBanco2 = idBanco2;
	}
	public String getEstatus() {
		return estatus;
	}
	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}
	public String getsFechaIni() {
		return sFechaIni;
	}
	public void setsFechaIni(String sFechaIni) {
		this.sFechaIni = sFechaIni;
	}
	public String getsFechaFin() {
		return sFechaFin;
	}
	public void setsFechaFin(String sFechaFin) {
		this.sFechaFin = sFechaFin;
	}
	public String getTipoMovimiento() {
		return tipoMovimiento;
	}
	public void setTipoMovimiento(String tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}
}
