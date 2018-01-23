package com.webset.set.inversiones.dto;

import java.util.Date;

public class ComunInversionesDto {
	
	private int idEmpresa;
	private int idBanco;
	private int contrato;
	private int noOrden;
	private boolean anterior;
	private boolean bInversionInterna;
	private String idChequera;
	private String idDivisa;
	private String institucion;
	private String srefmov;
	private String descEmpresa;
	private Date fecValor;
	private String descBanco;
	private double importe;
	private Integer idUsuario;
	
	
	public String getDescBanco() {
		return descBanco;
	}
	public void setDescBanco(String descBanco) {
		this.descBanco = descBanco;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
	public int getIdEmpresa() {
		return idEmpresa;
	}
	public void setIdEmpresa(int idEmpresa) {
		this.idEmpresa = idEmpresa;
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
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public int getContrato() {
		return contrato;
	}
	public void setContrato(int contrato) {
		this.contrato = contrato;
	}
	public String getInstitucion() {
		return institucion;
	}
	public void setInstitucion(String institucion) {
		this.institucion = institucion;
	}
	public Date getFecValor() {
		return fecValor;
	}
	public void setFecValor(Date fecValor) {
		this.fecValor = fecValor;
	}
	public boolean isAnterior() {
		return anterior;
	}
	public void setAnterior(boolean anterior) {
		this.anterior = anterior;
	}
	public int getNoOrden() {
		return noOrden;
	}
	public void setNoOrden(int noOrden) {
		this.noOrden = noOrden;
	}
	public boolean isBInversionInterna() {
		return bInversionInterna;
	}
	public void setBInversionInterna(boolean inversionInterna) {
		bInversionInterna = inversionInterna;
	}
	public String getSrefmov() {
		return srefmov;
	}
	public void setSrefmov(String srefmov) {
		this.srefmov = srefmov;
	}
	public String getDescEmpresa() {
		return descEmpresa;
	}
	public void setDescEmpresa(String descEmpresa) {
		this.descEmpresa = descEmpresa;
	}
	public Integer getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

}
