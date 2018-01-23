package com.webset.set.utileriasmod.dto;

public class GrupoEmpresasDto {
	int idGrupo;
	int noEmpresa;
	String descGrupo;	
	String nomEmpresa;
	String remitenteCorreo;
	String correoEmpresa;
	int nivelAutorizacion;
	
	
	public int getNivelAutorizacion() {
		return nivelAutorizacion;
	}
	public void setNivelAutorizacion(int nivelAutorizacion) {
		this.nivelAutorizacion = nivelAutorizacion;
	}
	public String getRemitenteCorreo() {
		return remitenteCorreo;
	}
	public void setRemitenteCorreo(String remitenteCorreo) {
		this.remitenteCorreo = remitenteCorreo;
	}
	public String getCorreoEmpresa() {
		return correoEmpresa;
	}
	public void setCorreoEmpresa(String correoEmpresa) {
		this.correoEmpresa = correoEmpresa;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public String getNomEmpresa() {
		return nomEmpresa;
	}
	public void setNomEmpresa(String nomEmpresa) {
		this.nomEmpresa = nomEmpresa;
	}
	public int getIdGrupo() {
		return idGrupo;
	}
	public void setIdGrupo(int idGrupo) {
		this.idGrupo = idGrupo;
	}
	public String getDescGrupo() {
		return descGrupo;
	}
	public void setDescGrupo(String descGrupo) {
		this.descGrupo = descGrupo;
	}
	
}
