package com.webset.set.utilerias.dto;

public class ComunDto {
	private int idBanco;
	private int idEmpresa;
	private double saldo;
	private double suma;
	private String descripcion;
	private String idChequera;
	private int idProveedor;
	private String idDivisa;
	private int noFolioDet;
	private String cveControl;
	private String psDivision;
	private String bancaElect;
	private String bExporta;
	
	public String getBExporta() {
		return bExporta;
	}
	public void setBExporta(String exporta) {
		bExporta = exporta;
	}
	public double getSaldo() {
		return saldo;
	}
	public String getBancaElect() {
		return bancaElect;
	}
	public void setBancaElect(String bancaElect) {
		this.bancaElect = bancaElect;
	}
	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public int getIdEmpresa() {
		return idEmpresa;
	}
	public void setIdEmpresa(int idEmpresa) {
		this.idEmpresa = idEmpresa;
	}
	public int getIdProveedor() {
		return idProveedor;
	}
	public void setIdProveedor(int proveedor) {
		this.idProveedor = proveedor;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public int getNoFolioDet() {
		return noFolioDet;
	}
	public void setNoFolioDet(int noFolioDet) {
		this.noFolioDet = noFolioDet;
	}
	public String getCveControl() {
		return cveControl;
	}
	public void setCveControl(String cveControl) {
		this.cveControl = cveControl;
	}
	public String getPsDivision() {
		return psDivision;
	}
	public void setPsDivision(String psDivision) {
		this.psDivision = psDivision;
	}
	public double getSuma() {
		return suma;
	}
	public void setSuma(double suma) {
		this.suma = suma;
	}
	

}
