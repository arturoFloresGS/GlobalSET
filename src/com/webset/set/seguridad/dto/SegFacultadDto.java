package com.webset.set.seguridad.dto;
/**
 * 
 * @author Sergio Vaca
 * @since 19/Octubre/2010
 * @see <p>Tabla seg_facultad</p>
 */

/**
 * clase que contiene los metodos get y set para los atributos de la tabla
 */
public class SegFacultadDto {
	private int idFacultad;
	private String descripcion;
	private String estatus;
	
	public int getIdFacultad() {
		return idFacultad;
	}
	public void setIdFacultad(int idFacultad) {
		this.idFacultad = idFacultad;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getEstatus() {
		return estatus;
	}
	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}
	
	
}
