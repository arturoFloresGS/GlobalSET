package com.webset.set.seguridad.dto;
/**
 * 
 * @author Armando Rodriguez
 * @since 31/Enero/2011
 * @see <p>Tabla seg_usuario_perfil</p>
 */
/**
 * clase que contiene los metodos get y set para los atributos de la tabla
 */
public class SegUsuarioPerfilDto {
	private int idUsuario;
	private int idPerfil;
	
	public int getIdPerfil() {
		return idPerfil;
	}
	public void setIdPerfil(int idPerfil) {
		this.idPerfil = idPerfil;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
}
