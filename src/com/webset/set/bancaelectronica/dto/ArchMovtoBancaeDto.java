package com.webset.set.bancaelectronica.dto;

import java.util.Date;


public class ArchMovtoBancaeDto {
	
	private String archivo;
	private String idChequera;
	private Date fecAlta;
	private int idBanco;
	
	//setters & getters
	public String getArchivo() {
		return archivo;
	}
	public void setArchivo(String archivo) {
		this.archivo = archivo;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public Date getFecAlta() {
		return fecAlta;
	}
	public void setFecAlta(Date fec_Alta) {
		this.fecAlta = fec_Alta;
	}
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}


}
