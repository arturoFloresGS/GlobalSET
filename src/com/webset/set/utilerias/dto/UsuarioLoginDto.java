package com.webset.set.utilerias.dto;

public class UsuarioLoginDto {
	private int idUsuario;
	private String apellidoPaterno;
	private String apellidoMaterno;
	private String nombre;
	private int numeroEmpresa; 
	private String nombreEmpresa;
	private int idCaja;
	private String descripcionCaja;
	private String noCuentaEmpresa;
	private String codigoSesion;
	private int idPerfil;
	private String claveUsuario;
	
	public String getClaveUsuario() {
		return claveUsuario;
	}
	public void setClaveUsuario(String claveUsuario) {
		this.claveUsuario = claveUsuario;
	}
	public int getIdPerfil() {
		return idPerfil;
	}
	public void setIdPerfil(int idPerfil) {
		this.idPerfil = idPerfil;
	}
	public String getCodigoSesion() {
		return codigoSesion;
	}
	public void setCodigoSesion(String codigoSesion) {
		this.codigoSesion = codigoSesion;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public String getApellidoPaterno() {
		return apellidoPaterno;
	}
	public void setApellidoPaterno(String apellidoPaterno) {
		this.apellidoPaterno = apellidoPaterno;
	}
	public String getApellidoMaterno() {
		return apellidoMaterno;
	}
	public void setApellidoMaterno(String apellidoMaterno) {
		this.apellidoMaterno = apellidoMaterno;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getNumeroEmpresa() {
		return numeroEmpresa;
	}
	public void setNumeroEmpresa(int numeroEmpresa) {
		this.numeroEmpresa = numeroEmpresa;
	}
	public String getNombreEmpresa() {
		return nombreEmpresa;
	}
	public void setNombreEmpresa(String nombreEmpresa) {
		this.nombreEmpresa = nombreEmpresa;
	}
	public int getIdCaja() {
		return idCaja;
	}
	public void setIdCaja(int idCaja) {
		this.idCaja = idCaja;
	}
	public String getDescripcionCaja() {
		return descripcionCaja;
	}
	public void setDescripcionCaja(String descripcionCaja) {
		this.descripcionCaja = descripcionCaja;
	}
	public String getNoCuentaEmpresa() {
		return noCuentaEmpresa;
	}
	public void setNoCuentaEmpresa(String noCuentaEmpresa) {
		this.noCuentaEmpresa = noCuentaEmpresa;
	}	

}
