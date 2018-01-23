package com.webset.set.seguridad.dto;
/**
 * 
 * @author Sergio Vaca
 * @since 19/Octubre/2010
 * @see <p>Tabla seg_facultad_componente</p>
 */
/**
 * Clase que continene los metodos set y get para los datos
 */
public class SegFacultadComponenteDto {
	private int idFacultad;
	private int idComponente;
	private String clavefacultad;
	private String claveComponente;
	private String descripcion;
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public int getIdFacultad() {
		return idFacultad;
	}
	public void setIdFacultad(int idFacultad) {
		this.idFacultad = idFacultad;
	}
	public int getIdComponente() {
		return idComponente;
	}
	public void setIdComponente(int idComponente) {
		this.idComponente = idComponente;
	}
	public String getClavefacultad() {
		return clavefacultad;
	}
	public void setClavefacultad(String clavefacultad) {
		this.clavefacultad = clavefacultad;
	}
	public String getClaveComponente() {
		return claveComponente;
	}
	public void setClaveComponente(String claveComponente) {
		this.claveComponente = claveComponente;
	}
	
}
