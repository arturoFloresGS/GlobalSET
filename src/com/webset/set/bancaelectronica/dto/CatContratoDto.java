package com.webset.set.bancaelectronica.dto;

public class CatContratoDto {
	private int id_contrato;
	public int getId_contrato() {
		return id_contrato;
	}
	public void setId_contrato(int id_contrato) {
		this.id_contrato = id_contrato;
	}
	public String getNo_contrato() {
		return no_contrato;
	}
	public void setNo_contrato(String no_contrato) {
		this.no_contrato = no_contrato;
	}
	public int getConsecutivo_dia() {
		return consecutivo_dia;
	}
	public void setConsecutivo_dia(int consecutivo_dia) {
		this.consecutivo_dia = consecutivo_dia;
	}
	public String getId_banco() {
		return id_banco;
	}
	public void setId_banco(String id_banco) {
		this.id_banco = id_banco;
	}
	public int getCod_cliente() {
		return cod_cliente;
	}
	public void setCod_cliente(int cod_cliente) {
		this.cod_cliente = cod_cliente;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	private String no_contrato;
	private int consecutivo_dia;
	private String id_banco;
	private int cod_cliente;
	private String valor;
	private String descripcion;
}
