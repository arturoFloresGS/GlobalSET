package com.webset.set.utilerias.dto;

/**
 * Esta clase representa a la tabla cat_tipo_valor
 * @author Cristian García García
 *
 */
public class CatTipoValorDto {
	private String idTipoValor;
	private String descTipoValor;
	private String bIsr;
	private String idDivisa;
	
	public String getIdTipoValor() {
		return idTipoValor;
	}
	public void setIdTipoValor(String idTipoValor) {
		this.idTipoValor = idTipoValor;
	}
	public String getDescTipoValor() {
		return descTipoValor;
	}
	public void setDescTipoValor(String descTipoValor) {
		this.descTipoValor = descTipoValor;
	}
	public String getBIsr() {
		return bIsr;
	}
	public void setBIsr(String isr) {
		bIsr = isr;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}

}
