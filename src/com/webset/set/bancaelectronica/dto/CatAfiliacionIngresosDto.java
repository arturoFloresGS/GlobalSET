package com.webset.set.bancaelectronica.dto;
/**
 * Esta clase representa el dto de la tabla cat_afiliacion_ingresos
 * @author Cristian Garcia Garcia
 * @since 13/12/2010
 */
public class CatAfiliacionIngresosDto {

	private int noEmpresa;
	private int idBanco;
	private int idTipoOperacion;
	
	private String idChequera;
	private String afiliacion;
	private String concepto;
	private String cuentaPuente;
	private String idDivision;
	
	
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
	public String getAfiliacion() {
		return afiliacion;
	}
	public void setAfiliacion(String afiliacion) {
		this.afiliacion = afiliacion;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public String getCuentaPuente() {
		return cuentaPuente;
	}
	public void setCuentaPuente(String cuentaPuente) {
		this.cuentaPuente = cuentaPuente;
	}
	public String getIdDivision() {
		return idDivision;
	}
	public void setIdDivision(String idDivision) {
		this.idDivision = idDivision;
	}
	public int getIdTipoOperacion() {
		return idTipoOperacion;
	}
	public void setIdTipoOperacion(int idTipoOperacion) {
		this.idTipoOperacion = idTipoOperacion;
	}
	
	
}
