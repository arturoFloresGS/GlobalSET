package com.webset.set.bancaelectronica.dto;

public class ParamRevividorDto {
	
	private int idBanco;	
	private double importe;
	private double plFolioRechazado;
	private String referencia;
	private String idChequera;
	private String psOperacion;
	
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
	public double getPlFolioRechazado() {
		return plFolioRechazado;
	}
	public void setPlFolioRechazado(double plFolioRechazado) {
		this.plFolioRechazado = plFolioRechazado;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public String getPsOperacion() {
		return psOperacion;
	}
	public void setPsOperacion(String psOperacion) {
		this.psOperacion = psOperacion;
	}
	

}
