/**
 * @author Jessica Arelly Cruz Cruz
 * @since 05/01/2011
 */
package com.webset.set.traspasos.dto;

public class CtasContratoDto {
	private int noEmpresa;
	private int noLinea;
	private int noCuenta;
	private int idBanco;
	private String idChequera;
	
	private String razonSocial;
	private String personaAutoriza;
	public String getRazonSocial() {
		return razonSocial;
	}
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	public String getPersonaAutoriza() {
		return personaAutoriza;
	}
	public void setPersonaAutoriza(String personaAutoriza) {
		this.personaAutoriza = personaAutoriza;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
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
