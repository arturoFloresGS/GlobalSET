package com.webset.set.utilerias.dto;

/**
 * @author Eric Medina
 * @Fecha 14/10/2015
 * 
 * @Desc Clase que hace referencia a la tabla ACRE_DOCTO_REG_NEG
 * Llena los grids tanto de acreedores como de documentos de Reglas de Negocios.
 * Se diferencia por clasificacion: A -> Acreedor, D -> Documento.
 */

public class AcreedorDoctoDto {

	private int idAcreDocto;
	private String deAcre;
	private String aAcre;
	private String clasificacion;
	private String tipoAcre;
	
	public int getIdAcreDocto() {
		return idAcreDocto;
	}
	public void setIdAcreDocto(int idAcreDocto) {
		this.idAcreDocto = idAcreDocto;
	}
	public String getDeAcre() {
		return deAcre;
	}
	public void setDeAcre(String deAcre) {
		this.deAcre = deAcre;
	}
	public String getaAcre() {
		return aAcre;
	}
	public void setaAcre(String aAcre) {
		this.aAcre = aAcre;
	}
	public String getClasificacion() {
		return clasificacion;
	}
	public void setClasificacion(String clasificacion) {
		this.clasificacion = clasificacion;
	}
	public String getTipoAcre() {
		return tipoAcre;
	}
	public void setTipoAcre(String tipoAcre) {
		this.tipoAcre = tipoAcre;
	}
	
}
