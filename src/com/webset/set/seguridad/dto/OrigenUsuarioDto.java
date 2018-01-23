package com.webset.set.seguridad.dto;

/**
 *  
 * @author Victor Hugo Tello
 * @since 19/Octubre/2010
 * @see <p>Tabla ORIGEN_USUARIO</p>
 */

public class OrigenUsuarioDto {
	private int noUsuario;
	
	private String origenMov;
	
	/**
	 * @return the noUsuario
	 */
	public int getNoUsuario() {
		return noUsuario;
	}
	/**
	 * @param noUsuario the noUsuario to set
	 */
	public void setNoUsuario(int noUsuario) {
		this.noUsuario = noUsuario;
	}
	/**
	 * @return the origenMov
	 */
	public String getOrigenMov() {
		return origenMov;
	}
	/**
	 * @param origenMov the origenMov to set
	 */
	public void setOrigenMov(String origenMov) {
		this.origenMov = origenMov;
	}
}
