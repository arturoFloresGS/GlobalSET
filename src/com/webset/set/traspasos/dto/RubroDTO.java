package com.webset.set.traspasos.dto;

import java.io.Serializable;

public class RubroDTO implements Serializable {
	
	
	private Integer noEmpresa;
	private String nomEmpresa;	
	private String cuentaContable;
	private String ingresoEgreso;
	private String idGrupo;
	private String descGrupo;
	private String idRubro;
	private String descRubro;
	private String idSubGrupo;
	
	
	public String getIdGrupo() {
		return idGrupo;
	}
	public void setIdGrupo(String idGrupo) {
		this.idGrupo = idGrupo;
	}
	public String getIdRubro() {
		return idRubro;
	}
	public void setIdRubro(String string) {
		this.idRubro = string;
	}
	public String getDescRubro() {
		return descRubro;
	}
	public void setDescRubro(String descRubro) {
		this.descRubro = descRubro;
	}
	
	public String getDescGrupo() {
		return descGrupo;
	}
	public void setDescGrupo(String descGrupo) {
		this.descGrupo = descGrupo;
	}
	public Integer getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(Integer noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public String getNomEmpresa() {
		return nomEmpresa;
	}
	public void setNomEmpresa(String nomEmpresa) {
		this.nomEmpresa = nomEmpresa;
	}
	public String getCuentaContable() {
		return cuentaContable;
	}
	public void setCuentaContable(String cuentaContable) {
		this.cuentaContable = cuentaContable;
	}
	public String getIngresoEgreso() {
		return ingresoEgreso;
	}
	public void setIngresoEgreso(String ingresoEgreso) {
		this.ingresoEgreso = ingresoEgreso;
	}
	public String getIdSubGrupo() {
		return idSubGrupo;
	}
	public void setIdSubGrupo(String idSubGrupo) {
		this.idSubGrupo = idSubGrupo;
	}
}
