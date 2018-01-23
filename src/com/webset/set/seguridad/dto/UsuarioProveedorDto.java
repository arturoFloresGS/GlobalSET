package com.webset.set.seguridad.dto;

/**
 * 
 * @author vtello
 * @Table usuario_proveedor
 */
public class UsuarioProveedorDto {
	private int noUsuario;
	private int noPersona;
	
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
	 * @return the noPersona
	 */
	public int getNoPersona() {
		return noPersona;
	}
	/**
	 * @param noPersona the noPersona to set
	 */
	public void setNoPersona(int noPersona) {
		this.noPersona = noPersona;
	}
}
