package com.webset.set.prestamosinterempresas.dto;

public class ArbolEmpresaDto {
	
	private int noEmpresa;
	private int empresaRaiz;
	private int padre;
	private int hijo;
	private int nieto;
	private int bisnieto;
	private int tataranieto;
	private int chosno;
	private int chosno2;
	private int chosno3;
	private int chosno4;
	private String bPadre;
	private double montoMaximo;
	
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public int getEmpresaRaiz() {
		return empresaRaiz;
	}
	public void setEmpresaRaiz(int empresaRaiz) {
		this.empresaRaiz = empresaRaiz;
	}
	public int getPadre() {
		return padre;
	}
	public void setPadre(int padre) {
		this.padre = padre;
	}
	public int getHijo() {
		return hijo;
	}
	public void setHijo(int hijo) {
		this.hijo = hijo;
	}
	public int getNieto() {
		return nieto;
	}
	public void setNieto(int nieto) {
		this.nieto = nieto;
	}
	public int getBisnieto() {
		return bisnieto;
	}
	public void setBisnieto(int bisnieto) {
		this.bisnieto = bisnieto;
	}
	public int getTataranieto() {
		return tataranieto;
	}
	public void setTataranieto(int tataranieto) {
		this.tataranieto = tataranieto;
	}
	public int getChosno() {
		return chosno;
	}
	public void setChosno(int chosno) {
		this.chosno = chosno;
	}
	public int getChosno2() {
		return chosno2;
	}
	public void setChosno2(int chosno2) {
		this.chosno2 = chosno2;
	}
	public int getChosno3() {
		return chosno3;
	}
	public void setChosno3(int chosno3) {
		this.chosno3 = chosno3;
	}
	public int getChosno4() {
		return chosno4;
	}
	public void setChosno4(int chosno4) {
		this.chosno4 = chosno4;
	}
	public String getBPadre() {
		return bPadre;
	}
	public void setBPadre(String padre) {
		bPadre = padre;
	}
	public double getMontoMaximo() {
		return montoMaximo;
	}
	public void setMontoMaximo(double montoMaximo) {
		this.montoMaximo = montoMaximo;
	}
}
