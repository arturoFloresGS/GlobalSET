package com.webset.set.barridosfondeos.dto;

/**
 * Estructura de empresas filiales para configuracion de Barridos y Fondeos
 * @author COINSIDE
 *
 */
public class FilialDto {

	private int noEmpresa;
	private String nombreEmpresa;
	private Long noCuentaEmp;
	private String tipoPersona;
	private Long noCuenta;
	private int noControladora;
	
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public String getNombreEmpresa() {
		return nombreEmpresa;
	}
	public void setNombreEmpresa(String nombreEmpresa) {
		this.nombreEmpresa = nombreEmpresa;
	}
	public Long getNoCuentaEmp() {
		return noCuentaEmp;
	}
	public void setNoCuentaEmp(Long noCuentaEmp) {
		this.noCuentaEmp = noCuentaEmp;
	}
	public String getTipoPersona() {
		return tipoPersona;
	}
	public void setTipoPersona(String tipoPersona) {
		this.tipoPersona = tipoPersona;
	}
	public Long getNoCuenta() {
		return noCuenta;
	}
	public void setNoCuenta(Long noCuenta) {
		this.noCuenta = noCuenta;
	}
	public int getNoControladora() {
		return noControladora;
	}
	public void setNoControladora(int noControladora) {
		this.noControladora = noControladora;
	}
	
	
}
