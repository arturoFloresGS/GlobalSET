package com.webset.set.consultas.dto;

public class ChequeraGridDto {
	private String noDocto;
	private String concepto;
	private double importe;
	private String beneficiario;
	private String noFolioDet;
	private String descFormaPago;
	private String observacion;
	public String getNoDocto() {
		return noDocto;
	}
	public void setNoDocto(String noDocto) {
		this.noDocto = noDocto;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
	public String getBeneficiario() {
		return beneficiario;
	}
	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}
	public String getNoFolioDet() {
		return noFolioDet;
	}
	public void setNoFolioDet(String noFolioDet) {
		this.noFolioDet = noFolioDet;
	}
	public String getDescFormaPago() {
		return descFormaPago;
	}
	public void setDescFormaPago(String descFormaPago) {
		this.descFormaPago = descFormaPago;
	}
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	
}
