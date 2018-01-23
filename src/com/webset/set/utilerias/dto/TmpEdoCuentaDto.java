package com.webset.set.utilerias.dto;

import java.util.Date;

public class TmpEdoCuentaDto {
	
	private int usuarioAlta;
	private double secuencia;
	private Date fecha;
	private String concepto;
	private double saldoInicial;
	private double depositos;
	private double retiros;
	private double saldoFinal;
	private int etiqueta;
	
	public int getUsuarioAlta() {
		return usuarioAlta;
	}
	public void setUsuarioAlta(int usuarioAlta) {
		this.usuarioAlta = usuarioAlta;
	}
	public double getSecuencia() {
		return secuencia;
	}
	public void setSecuencia(double secuencia) {
		this.secuencia = secuencia;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public double getSaldoInicial() {
		return saldoInicial;
	}
	public void setSaldoInicial(double saldoInicial) {
		this.saldoInicial = saldoInicial;
	}
	public double getDepositos() {
		return depositos;
	}
	public void setDepositos(double depositos) {
		this.depositos = depositos;
	}
	public double getRetiros() {
		return retiros;
	}
	public void setRetiros(double retiros) {
		this.retiros = retiros;
	}
	public double getSaldoFinal() {
		return saldoFinal;
	}
	public void setSaldoFinal(double saldoFinal) {
		this.saldoFinal = saldoFinal;
	}
	public int getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(int etiqueta) {
		this.etiqueta = etiqueta;
	}
}
