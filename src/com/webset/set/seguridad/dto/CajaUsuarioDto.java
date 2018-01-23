package com.webset.set.seguridad.dto;

/**
 *  
 * @author Victor Hugo Tello
 * @since 19/Octubre/2010
 * @see <p>Tabla CAJA_USUARIO</P>
 */

public class CajaUsuarioDto {
	private int noUsuario;
	private int idCaja;
	
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
	 * @return the idCaja
	 */
	public int getIdCaja() {
		return idCaja;
	}
	/**
	 * @param idCaja the idCaja to set
	 */
	public void setIdCaja(int idCaja) {
		this.idCaja = idCaja;
	}
}
