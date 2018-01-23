package com.webset.set.egresos.dto;

/*
 * Autor: Luis Alfredo Serrato Montes de Oca
 * 22102015
 */

public class DatosExcelDto {
 	
    private int ccid;
    private int sociedad;
    private int numeroAcredor;
    private String nombreAcredor;
    private String docSap;
    private String viaPago;
    private String banco;
    private String cuenta;
    private String moneda;
    private String fechaContabilizacion;
    private Double total;
    private String observacion;
    private String noFolioDet;
    private String referencia;
    private String bandera;
    
	public String getFechaContabilizacion() {
		return fechaContabilizacion;
	}
	public void setFechaContabilizacion(String fechaContabilizacion) {
		this.fechaContabilizacion = fechaContabilizacion;
	}
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	public int getCcid() {
		return ccid;
	}
	public void setCcid(int ccid) {
		this.ccid = ccid;
	}
	public int getSociedad() {
		return sociedad;
	}
	public void setSociedad(int sociedad) {
		this.sociedad = sociedad;
	}
	public int getNumeroAcredor() {
		return numeroAcredor;
	}
	public void setNumeroAcredor(int numeroAcredor) {
		this.numeroAcredor = numeroAcredor;
	}
	public String getNombreAcredor() {
		return nombreAcredor;
	}
	public void setNombreAcredor(String nombreAcredor) {
		this.nombreAcredor = nombreAcredor;
	}
	public String getDocSap() {
		return docSap;
	}
	public void setDocSap(String docSap) {
		this.docSap = docSap;
	}
	public String getViaPago() {
		return viaPago;
	}
	public void setViaPago(String viaPago) {
		this.viaPago = viaPago;
	}
	public String getBanco() {
		return banco;
	}
	public void setBanco(String banco) {
		this.banco = banco;
	}
	public String getCuenta() {
		return cuenta;
	}
	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}
	public String getMoneda() {
		return moneda;
	}
	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}
	public Double getTotal() {
		return total;
	}
	public void setTotal(Double total) {
		this.total = total;
	}
	public String getNoFolioDet() {
		return noFolioDet;
	}
	public void setNoFolioDet(String noFolioDet) {
		this.noFolioDet = noFolioDet;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getBandera() {
		return bandera;
	}
	public void setBandera(String bandera) {
		this.bandera = bandera;
	}
}
