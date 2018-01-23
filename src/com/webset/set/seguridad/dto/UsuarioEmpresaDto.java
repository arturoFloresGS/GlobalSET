package com.webset.set.seguridad.dto;

/**
 * 
 * @author vtello
 * @Table usuario_empresa
 */
public class UsuarioEmpresaDto {
	private int noUsuario;
	private int noEmpresa;
	
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
	 * @return the noEmpresa
	 */
	public int getNoEmpresa() {
		return noEmpresa;
	}
	/**
	 * @param noEmpresa the noEmpresa to set
	 */
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
}
