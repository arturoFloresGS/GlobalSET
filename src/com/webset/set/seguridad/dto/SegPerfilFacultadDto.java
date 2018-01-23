package com.webset.set.seguridad.dto;
/**
 * 
 * @author Sergio Vaca
 * @since 19/Octubre/2010
 * @see <p>Tabla seg_perfil_facultad</p>
 */
/**
 * clase que contiene los metodos get y set para los atributos de la tabla
 */
public class SegPerfilFacultadDto {
	private int idPerfil;
	private int idFacultad;
	private String clavePerfil;
	private String claveFacultad;
	private String descripcion;

	public String getClaveFacultad() {
		return claveFacultad;
	}
	public void setClaveFacultad(String claveFacultad) {
		this.claveFacultad = claveFacultad;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public int getIdPerfil() {
		return idPerfil;
	}
	public void setIdPerfil(int idPerfil) {
		this.idPerfil = idPerfil;
	}
	public int getIdFacultad() {
		return idFacultad;
	}
	public void setIdFacultad(int idFacultad) {
		this.idFacultad = idFacultad;
	}
	public String getClavePerfil() {
		return clavePerfil;
	}
	public void setClavePerfil(String clavePerfil) {
		this.clavePerfil = clavePerfil;
	}
}
