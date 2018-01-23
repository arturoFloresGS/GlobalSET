package com.webset.set.utilerias.dto;

import java.util.Date;

/**
 * Esta clase representa la tabla hist_saldo
 * @author Crar
 *
 */
public class HistSaldoDto {

	private int noEmpresa;
	private int idTipoSaldo;
	private Date fecValor;
	private int noLinea;
	private int noCuenta;
	private double importe;
	
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public int getIdTipoSaldo() {
		return idTipoSaldo;
	}
	public void setIdTipoSaldo(int idTipoSaldo) {
		this.idTipoSaldo = idTipoSaldo;
	}
	public Date getFecValor() {
		return fecValor;
	}
	public void setFecValor(Date fecValor) {
		this.fecValor = fecValor;
	}
	public int getNoLinea() {
		return noLinea;
	}
	public void setNoLinea(int noLinea) {
		this.noLinea = noLinea;
	}
	public int getNoCuenta() {
		return noCuenta;
	}
	public void setNoCuenta(int noCuenta) {
		this.noCuenta = noCuenta;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
}
