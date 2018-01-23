package com.webset.set.egresos.dto;

import java.util.Date;

public class ConfirmacionTransferenciasDto {
	private Date fecAyer;
	private String horaActual;
	private String folioRef;
	private String idEstatusCancela;
	private int registrosSet;
	private int secuencia;
	private String descripcion;
	
	public String getIdEstatusCancela() {
		return idEstatusCancela;
	}
	public void setIdEstatusCancela(String idEstatusCancela) {
		this.idEstatusCancela = idEstatusCancela;
	}
	public int getRegistrosSet() {
		return registrosSet;
	}
	public void setRegistrosSet(int registrosSet) {
		this.registrosSet = registrosSet;
	}
	public int getSecuencia() {
		return secuencia;
	}
	public void setSecuencia(int secuencia) {
		this.secuencia = secuencia;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public void setFecAyer(Date fecAyer) {
		this.fecAyer = fecAyer;
	}
	public Date getFecAyer() {
		return fecAyer;
	}
	public void setHoraActual(String horaActual) {
		this.horaActual = horaActual;
	}
	public String getHoraActual() {
		return horaActual;
	}
	public void setFolioRef(String folioRef) {
		this.folioRef = folioRef;
	}
	public String getFolioRef() {
		return folioRef;
	}
}
