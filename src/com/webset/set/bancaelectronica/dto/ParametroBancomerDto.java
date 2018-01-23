package com.webset.set.bancaelectronica.dto;

public class ParametroBancomerDto {
	private boolean pbSwiftMT940MN;
	private boolean movDiarios;
	private int idBanco;
	private String idChequera;
	public boolean isPbSwiftMT940MN() {
		return pbSwiftMT940MN;
	}
	public void setPbSwiftMT940MN(boolean pbSwiftMT940MN) {
		this.pbSwiftMT940MN = pbSwiftMT940MN;
	}
	public boolean isMovDiarios() {
		return movDiarios;
	}
	public void setMovDiarios(boolean movDiarios) {
		this.movDiarios = movDiarios;
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
}
