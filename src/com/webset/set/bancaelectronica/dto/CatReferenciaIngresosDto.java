package com.webset.set.bancaelectronica.dto;

public class CatReferenciaIngresosDto {

	private int noEmpresa;
	private int idBanco;
	private int idRubro;
	private String idChequera;
	private String referencia;
	private String concepto;
	private String noProveedor;
	private String division;
	private String  bCliente;
	
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
	public int getIdRubro() {
		return idRubro;
	}
	public void setIdRubro(int idRubro) {
		this.idRubro = idRubro;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public String getNoProveedor() {
		return noProveedor;
	}
	public void setNoProveedor(String noProveedor) {
		this.noProveedor = noProveedor;
	}
	public String getDivision() {
		return division;
	}
	public void setDivision(String division) {
		this.division = division;
	}
	public String getBCliente() {
		return bCliente;
	}
	public void setBCliente(String cliente) {
		bCliente = cliente;
	}
}
