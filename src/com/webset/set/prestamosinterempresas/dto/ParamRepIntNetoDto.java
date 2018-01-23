package com.webset.set.prestamosinterempresas.dto;

public class ParamRepIntNetoDto {
	
	private int empresaCon;
	private int anio;
	private int mes;
	private int idUsuario;
	private boolean facTodas;
	private String idDivisa;
	
	public int getEmpresaCon() {
		return empresaCon;
	}
	public void setEmpresaCon(int empresaCon) {
		this.empresaCon = empresaCon;
	}
	public int getAnio() {
		return anio;
	}
	public void setAnio(int anio) {
		this.anio = anio;
	}
	public int getMes() {
		return mes;
	}
	public void setMes(int mes) {
		this.mes = mes;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public boolean isFacTodas() {
		return facTodas;
	}
	public void setFacTodas(boolean facTodas) {
		this.facTodas = facTodas;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	

}
