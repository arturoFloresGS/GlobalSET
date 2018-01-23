package com.webset.set.coinversion.dto;

public class SaldoDto {

	private int noEmpresa;
	private int noCuenta;
	private int noLinea;
	private int tipoSaldo;
	private double importe;
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public int getNoCuenta() {
		return noCuenta;
	}
	public void setNoCuenta(int noCuenta) {
		this.noCuenta = noCuenta;
	}
	public int getNoLinea() {
		return noLinea;
	}
	public void setNoLinea(int noLinea) {
		this.noLinea = noLinea;
	}
	public int getTipoSaldo() {
		return tipoSaldo;
	}
	public void setTipoSaldo(int tipoSaldo) {
		this.tipoSaldo = tipoSaldo;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
}
