package com.webset.set.impresion.dto;

public class ControlPapelDto {
	
	private int folioInvIni;
	private int folioInvFin;
	private int folioUltImpreso;
	
	/****	AGREGADOS EMS: 28/12/2015	*****/
	private int cajaClave;
	private int idBanco;
	private String fechaAlta;
	private String idChequera;
	private int noEmpresa;
	private String nomEmpresa;
	private String tipoFolio;
	private int stock;
	private int idControlCheque;
	private String estatus;
	/*****************************************/
	
	public int getFolioInvIni() {
		return folioInvIni;
	}
	public void setFolioInvIni(int folioInvIni) {
		this.folioInvIni = folioInvIni;
	}
	public int getFolioInvFin() {
		return folioInvFin;
	}
	public void setFolioInvFin(int folioInvFin) {
		this.folioInvFin = folioInvFin;
	}
	public int getFolioUltImpreso() {
		return folioUltImpreso;
	}
	public void setFolioUltImpreso(int folioUltImpreso) {
		this.folioUltImpreso = folioUltImpreso;
	}
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public String getFechaAlta() {
		return fechaAlta;
	}
	public void setFechaAlta(String fechaAlta) {
		this.fechaAlta = fechaAlta;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public String getTipoFolio() {
		return tipoFolio;
	}
	public void setTipoFolio(String tipoFolio) {
		this.tipoFolio = tipoFolio;
	}
	public int getCajaClave() {
		return cajaClave;
	}
	public void setCajaClave(int cajaClave) {
		this.cajaClave = cajaClave;
	}
	public int getStock() {
		return stock;
	}
	public void setStock(int stock) {
		this.stock = stock;
	}
	public String getNomEmpresa() {
		return nomEmpresa;
	}
	public void setNomEmpresa(String nomEmpresa) {
		this.nomEmpresa = nomEmpresa;
	}
	public int getIdControlCheque() {
		return idControlCheque;
	}
	public void setIdControlCheque(int idControlCheque) {
		this.idControlCheque = idControlCheque;
	}
	public String getEstatus() {
		return estatus;
	}
	public void setEstatus(String estatus) {
		this.estatus = estatus;
	} 
}
