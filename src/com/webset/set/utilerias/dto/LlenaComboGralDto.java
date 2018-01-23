package com.webset.set.utilerias.dto;

/**
 * 
 * @author Cristian Garcia Garcia
 *
 */
public class LlenaComboGralDto {
	//Atributos
	private int id;
	private String descripcion;
	private String tabla;
	private String orden;
	private String campoUno;//este parametro es de entrada
	private String campoDos;//esta descripcion es de entrada
	private String condicion;
	private boolean registroUnico;//este parametro sirve si solo keremos un registro
	private String idStr;
	private float valor;
	private String idCadena;
	
	//Getters and Setters
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
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
	public float getValor() {
		return valor;
	}
	public void setValor(float valor) {
		this.valor = valor;
	}
	public String getIdCadena() {
		return idCadena;
	}
	public void setIdCadena(String idCadena) {
		this.idCadena = idCadena;
	}
}
