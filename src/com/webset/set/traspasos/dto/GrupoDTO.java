package com.webset.set.traspasos.dto;

public class GrupoDTO{
	
	private Integer idGrupo;
	private int idPoliza;
	private String decPoliza;
	private String descGrupo;
	
	
	
	public int getIdPoliza() {
		return idPoliza;
	}
	public void setIdPoliza(int idPoliza) {
		this.idPoliza = idPoliza;
	}
	public String getDecPoliza() {
		return decPoliza;
	}
	public void setDecPoliza(String decPoliza) {
		this.decPoliza = decPoliza;
	}
	public Integer getIdGrupo() {
		return idGrupo;
	}
	public void setIdGrupo(Integer idGrupo) {
		this.idGrupo = idGrupo;
	}
	public String getDescGrupo() {
		return descGrupo;
	}
	public void setDescGrupo(String descGrupo) {
		this.descGrupo = descGrupo;
	}
	
}
