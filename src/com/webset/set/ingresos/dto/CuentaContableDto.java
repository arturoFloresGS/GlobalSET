package com.webset.set.ingresos.dto;

import java.util.Date;

public class CuentaContableDto {
	
	private String cuentaContable;
	private String noFactura;
	private double importe;
	private double importeA;
	private String idDivisa;
	private String descDivisa;
	private String fecFact;
	private String concepto;
	private String noBenef;
	private String razonSocial;
	
	public String getCuentaContable() {
		return cuentaContable;
	}
	
	public void setCuentaContable(String cuentaContable) {
		this.cuentaContable = cuentaContable;
	}

	public String getNoFactura() {
		return noFactura;
	}

	public void setNoFactura(String noFactura) {
		this.noFactura = noFactura;
	}

	public double getImporte() {
		return importe;
	}

	public void setImporte(double importe) {
		this.importe = importe;
	}
	public double getImporteA() {
		return importeA;
	}

	public void setImporteA(double importeA) {
		this.importeA = importeA;
	}

	public String getIdDivisa() {
		return idDivisa;
	}

	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}

	public String getDescDivisa() {
		return descDivisa;
	}

	public void setDescDivisa(String descDivisa) {
		this.descDivisa = descDivisa;
	}

	public String getFecFact() {
		return fecFact;
	}

	public void setFecFact(String fecFact) {
		this.fecFact = fecFact;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public String getNoBenef() {
		return noBenef;
	}

	public void setNoBenef(String noBenef) {
		this.noBenef = noBenef;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	
}
