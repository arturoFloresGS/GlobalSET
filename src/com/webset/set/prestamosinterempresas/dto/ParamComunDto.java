package com.webset.set.prestamosinterempresas.dto;

import java.util.Date;

public class ParamComunDto {
	
	private Date fecIni;
	private Date fecFin;
	private double iva;
	private String divisa;
	public double tasa;
	public int idEmpresa;
	private Date fecHoy;
	private Date fecValor;
	private int idUsuario;
	private int sector;
	private boolean bSector;
	
	public int getSector() {
		return sector;
	}
	public void setSector(int sector) {
		this.sector = sector;
	}
	public Date getFecHoy() {
		return fecHoy;
	}
	public void setFecHoy(Date fecHoy) {
		this.fecHoy = fecHoy;
	}
	public double getTasa() {
		return tasa;
	}
	public void setTasa(double tasa) {
		this.tasa = tasa;
	}
	public String getDivisa() {
		return divisa;
	}
	public void setDivisa(String divisa) {
		this.divisa = divisa;
	}
	public Date getFecIni() {
		return fecIni;
	}
	public void setFecIni(Date fecIni) {
		this.fecIni = fecIni;
	}
	public Date getFecFin() {
		return fecFin;
	}
	public void setFecFin(Date fecFin) {
		this.fecFin = fecFin;
	}
	public double getIva() {
		return iva;
	}
	public void setIva(double iva) {
		this.iva = iva;
	}
	public int getIdEmpresa() {
		return idEmpresa;
	}
	public void setIdEmpresa(int idEmpresa) {
		this.idEmpresa = idEmpresa;
	}
	public Date getFecValor() {
		return fecValor;
	}
	public void setFecValor(Date fecValor) {
		this.fecValor = fecValor;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public boolean isBSector() {
		return bSector;
	}
	public void setBSector(boolean sector) {
		bSector = sector;
	}
}
