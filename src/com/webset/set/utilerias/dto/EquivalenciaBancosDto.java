package com.webset.set.utilerias.dto;

public class EquivalenciaBancosDto {
	private String bankl;
	private String banka;
	private String idBanco;
	private String descBanco;
	private String tabla;
	private String orden;
	private String campoUno;//este parametro es de entrada
	private String campoDos;//esta descripcion es de entrada
	private String condicion;
	
	public String getTabla() {
		return tabla;
	}
	public void setTabla(String tabla) {
		this.tabla = tabla;
	}
	public String getOrden() {
		return orden;
	}
	public void setOrden(String orden) {
		this.orden = orden;
	}
	public String getCampoUno() {
		return campoUno;
	}
	public void setCampoUno(String campoUno) {
		this.campoUno = campoUno;
	}
	public String getCampoDos() {
		return campoDos;
	}
	public void setCampoDos(String campoDos) {
		this.campoDos = campoDos;
	}
	public String getCondicion() {
		return condicion;
	}
	public void setCondicion(String condicion) {
		this.condicion = condicion;
	}
	public boolean isRegistroUnico() {
		return registroUnico;
	}
	public void setRegistroUnico(boolean registroUnico) {
		this.registroUnico = registroUnico;
	}
	public String getIdStr() {
		return idStr;
	}
	public void setIdStr(String idStr) {
		this.idStr = idStr;
	}
	private boolean registroUnico;//este parametro sirve si solo keremos un registro
	private String idStr;
	
	public String getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(String idBanco) {
		this.idBanco = idBanco;
	}
	public String getDescBanco() {
		return descBanco;
	}
	public void setDescBanco(String descBanco) {
		this.descBanco = descBanco;
	}
	public String getBankl() {
		return bankl;
	}
	public void setBankl(String bankl) {
		this.bankl = bankl;
	}
	public String getBanka() {
		return banka;
	}
	public void setBanka(String banka) {
		this.banka = banka;
	}
	
	
}
