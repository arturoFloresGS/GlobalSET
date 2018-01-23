package com.webset.set.coinversion.dto;

public class ParamsBusquedaBarridoDto {

	private int noEmpresa;
	private int idBanco;
	private boolean opcFondeo;
	private boolean opcSobregiro;
	private String idDivisa;
	private String chequera;
	private String nomEmpresa;
	public String getNomEmpresa() {
		return nomEmpresa;
	}
	public void setNomEmpresa(String nomEmpresa) {
		this.nomEmpresa = nomEmpresa;
	}
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
	public boolean isOpcFondeo() {
		return opcFondeo;
	}
	public void setOpcFondeo(boolean opcFondeo) {
		this.opcFondeo = opcFondeo;
	}
	public boolean isOpcSobregiro() {
		return opcSobregiro;
	}
	public void setOpcSobregiro(boolean opcSobregiro) {
		this.opcSobregiro = opcSobregiro;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public String getChequera() {
		return chequera;
	}
	public void setChequera(String chequera) {
		this.chequera = chequera;
	}
}
