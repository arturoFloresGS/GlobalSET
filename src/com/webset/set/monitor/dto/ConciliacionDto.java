package com.webset.set.monitor.dto;

/**
 * Representación de las conciliaciones pendientes.
 * Depositos no identificados. Cargos no referenciados.
 * 
 * @author WebSet
 */
public class ConciliacionDto {
	//Atributos
	private int noEmpresa;
	private int idBanco;
	private int idTipoOperacion;
	
	private String nomEmpresa;
	private String descBanco;
	private String idChequera;
	private String idTipoMovto;
	private String idDivisa;
	
	private Double importe;
	
	//Getters and Setters
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

	public int getIdTipoOperacion() {
		return idTipoOperacion;
	}

	public void setIdTipoOperacion(int idTipoOperacion) {
		this.idTipoOperacion = idTipoOperacion;
	}

	public String getNomEmpresa() {
		return nomEmpresa;
	}

	public void setNomEmpresa(String nomEmpresa) {
		this.nomEmpresa = nomEmpresa;
	}

	public String getDescBanco() {
		return descBanco;
	}

	public void setDescBanco(String descBanco) {
		this.descBanco = descBanco;
	}

	public String getIdChequera() {
		return idChequera;
	}

	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}

	public String getIdTipoMovto() {
		return idTipoMovto;
	}

	public void setIdTipoMovto(String idTipoMovto) {
		this.idTipoMovto = idTipoMovto;
	}

	public String getIdDivisa() {
		return idDivisa;
	}

	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}

	public Double getImporte() {
		return importe;
	}

	public void setImporte(Double importe) {
		this.importe = importe;
	}
}
