package com.webset.set.seguridad.dto;
/**
 * 
 * @author Sergio Vaca
 * @since 19/Octubre/2010
 * @see <p>Tabla seg_perfil</p>
 */

/**
 * clase que contiene los metodos get y set para los atributos de la tabla
 */
public class SegPerfilDto {
	private int idPerfil;
	private String descripcion;
	private String estatus;
	/**
	 * Variables para el llenar combo de perfil en usuario.jsp
	 */
	
	public int getIdPerfil() {
		return idPerfil;
	}
	public void setIdPerfil(int idPerfil) {
		this.idPerfil = idPerfil;
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
