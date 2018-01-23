package com.webset.set.bancaelectronica.dto;

public class CatReferenciaChequeraDto {
	
	private int noEmpresa;
	private int idBanco;
	private int noCaso;
	private int longitud;
	private int idRubro;
	private int longCliente;
	private int inicioCliente;
	private String idChequera;
	private String formato;	
	private String bDivision;
	private String bFecha;
	private String bPertenencia;
	private String valorDefault;
	private String bValidaCheq;
	private String bProveedor;
	private String bParque;
	
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
	public int getNoCaso() {
		return noCaso;
	}
	public void setNoCaso(int noCaso) {
		this.noCaso = noCaso;
	}
	public int getLongitud() {
		return longitud;
	}
	public void setLongitud(int longitud) {
		this.longitud = longitud;
	}
	public String getFormato() {
		return formato;
	}
	public void setFormato(String formato) {
		this.formato = formato;
	}
	public int getIdRubro() {
		return idRubro;
	}
	public void setIdRubro(int idRubro) {
		this.idRubro = idRubro;
	}
	public int getLongCliente() {
		return longCliente;
	}
	public void setLongCliente(int longCliente) {
		this.longCliente = longCliente;
	}
	public String getBDivision() {
		return bDivision;
	}
	public void setBDivision(String division) {
		bDivision = division;
	}
	public String getBFecha() {
		return bFecha;
	}
	public void setBFecha(String fecha) {
		bFecha = fecha;
	}
	public String getBPertenencia() {
		return bPertenencia;
	}
	public void setBPertenencia(String pertenencia) {
		bPertenencia = pertenencia;
	}
	public String getValorDefault() {
		return valorDefault;
	}
	public void setValorDefault(String valorDefault) {
		this.valorDefault = valorDefault;
	}
	public String getBValidaCheq() {
		return bValidaCheq;
	}
	public void setBValidaCheq(String validaCheq) {
		bValidaCheq = validaCheq;
	}
	public String getBProveedor() {
		return bProveedor;
	}
	public void setBProveedor(String proveedor) {
		bProveedor = proveedor;
	}
	public int getInicioCliente() {
		return inicioCliente;
	}
	public void setInicioCliente(int inicioCliente) {
		this.inicioCliente = inicioCliente;
	}
	public String getBParque() {
		return bParque;
	}
	public void setBParque(String parque) {
		bParque = parque;
	}
	

	

}
