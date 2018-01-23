package com.webset.set.consultas.dto;

public class SaldoDto {
	
	int noEmpresa;
	int idBanco;
	String idChequera;
	String idDivisa;
	String idTipoMovto;
	double saldoInicial;
	double abonos;
	double cargos;
	double saldoFinal;
	double importe;
	String fecValor;
	
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
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public double getSaldoInicial() {
		return saldoInicial;
	}
	public void setSaldoInicial(double saldoInicial) {
		this.saldoInicial = saldoInicial;
	}
	public double getAbonos() {
		return abonos;
	}
	public void setAbonos(double abonos) {
		this.abonos = abonos;
	}
	public double getCargos() {
		return cargos;
	}
	public void setCargos(double cargos) {
		this.cargos = cargos;
	}
	public double getSaldoFinal() {
		return saldoFinal;
	}
	public void setSaldoFinal(double saldoFinal) {
		this.saldoFinal = saldoFinal;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public String getIdTipoMovto() {
		return idTipoMovto;
	}
	public void setIdTipoMovto(String idTipoMovto) {
		this.idTipoMovto = idTipoMovto;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
	public String getFecValor() {
		return fecValor;
	}
	public void setFecValor(String fecValor) {
		this.fecValor = fecValor;
	}
}
