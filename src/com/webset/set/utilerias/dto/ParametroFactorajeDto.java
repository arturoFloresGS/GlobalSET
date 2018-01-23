package com.webset.set.utilerias.dto;

import java.util.Date;

public class ParametroFactorajeDto {
	
	private int noUsuario; 
	private String sFolios;
	private int plFolio;
	private int plFolio2; 
	private Date fechaHoy;
	
	public int getNoUsuario() {
		return noUsuario;
	}
	public void setNoUsuario(int noUsuario) {
		this.noUsuario = noUsuario;
	}
	public String getSFolios() {
		return sFolios;
	}
	public void setSFolios(String folios) {
		sFolios = folios;
	}
	public int getPlFolio() {
		return plFolio;
	}
	public void setPlFolio(int plFolio) {
		this.plFolio = plFolio;
	}
	public int getPlFolio2() {
		return plFolio2;
	}
	public void setPlFolio2(int plFolio2) {
		this.plFolio2 = plFolio2;
	}
	public Date getFechaHoy() {
		return fechaHoy;
	}
	public void setFechaHoy(Date fechaHoy) {
		this.fechaHoy = fechaHoy;
	}

}
