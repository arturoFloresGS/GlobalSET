package com.webset.set.interfaz.dto;

public class GuiaContableDto {
	
	private int idGuia;
	private int noEmpresa;
	private int idGrupo;
	private int idRubro;
	private String cuentaContable;
	private String noFolioDet;
	
	public int getIdGuia() {
		return idGuia;
	}
	public void setIdGuia(int idGuia) {
		this.idGuia = idGuia;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public int getIdGrupo() {
		return idGrupo;
	}
	public void setIdGrupo(int idGrupo) {
		this.idGrupo = idGrupo;
	}
	public int getIdRubro() {
		return idRubro;
	}
	public void setIdRubro(int idRubro) {
		this.idRubro = idRubro;
	}
	public String getCuentaContable() {
		return cuentaContable;
	}
	public void setCuentaContable(String cuentaContable) {
		this.cuentaContable = cuentaContable;
	}
	public String getNoFolioDet() {
		return noFolioDet;
	}
	public void setNoFolioDet(String noFolioDet) {
		this.noFolioDet = noFolioDet;
	}

}
