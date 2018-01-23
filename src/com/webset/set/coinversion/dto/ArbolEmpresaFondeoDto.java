package com.webset.set.coinversion.dto;
/**
 * Dto que representa la tabla arbol_empresa_fondeo
 * @author cgarcia
 *
 */
public class ArbolEmpresaFondeoDto {
	
	private int tipoArbol;
	private int empresaOrigen;
	private int empresaDestino;
	private int tipoOperacion;
	private String tipoValor;
	private String valor;
	private String continua;
	private int orden;
	private String descArbol;
	private String descTipoOperacion;
	
	public int getTipoArbol() {
		return tipoArbol;
	}
	public void setTipoArbol(int tipoArbol) {
		this.tipoArbol = tipoArbol;
	}
	public int getEmpresaOrigen() {
		return empresaOrigen;
	}
	public void setEmpresaOrigen(int empresaOrigen) {
		this.empresaOrigen = empresaOrigen;
	}
	public int getEmpresaDestino() {
		return empresaDestino;
	}
	public void setEmpresaDestino(int empresaDestino) {
		this.empresaDestino = empresaDestino;
	}
	public int getTipoOperacion() {
		return tipoOperacion;
	}
	public void setTipoOperacion(int tipoOperacion) {
		this.tipoOperacion = tipoOperacion;
	}
	public String getTipoValor() {
		return tipoValor;
	}
	public void setTipoValor(String tipoValor) {
		this.tipoValor = tipoValor;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public String getContinua() {
		return continua;
	}
	public void setContinua(String continua) {
		this.continua = continua;
	}
	public int getOrden() {
		return orden;
	}
	public void setOrden(int orden) {
		this.orden = orden;
	}
	public String getDescArbol() {
		return descArbol;
	}
	public void setDescArbol(String descArbol) {
		this.descArbol = descArbol;
	}
	public String getDescTipoOperacion() {
		return descTipoOperacion;
	}
	public void setDescTipoOperacion(String descTipoOperacion) {
		this.descTipoOperacion = descTipoOperacion;
	}

}
