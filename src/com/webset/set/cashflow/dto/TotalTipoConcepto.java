package com.webset.set.cashflow.dto;

public class TotalTipoConcepto {
	
	private String fec_valor;
	private String ingresoegreso;
	private double total;
	private String origen;
	private int id_tipo_concepto;
	private String descripcion;
	int row_tipo_concepto;
	public String getFec_valor() {
		return fec_valor;
	}
	public void setFec_valor(String fec_valor) {
		this.fec_valor = fec_valor;
	}
	public String getIngresoegreso() {
		return ingresoegreso;
	}
	public void setIngresoegreso(String ingresoegreso) {
		this.ingresoegreso = ingresoegreso;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public String getOrigen() {
		return origen;
	}
	public void setOrigen(String origen) {
		this.origen = origen;
	}
	public int getId_tipo_concepto() {
		return id_tipo_concepto;
	}
	public void setId_tipo_concepto(int id_tipo_concepto) {
		this.id_tipo_concepto = id_tipo_concepto;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public int getRow_tipo_concepto() {
		return row_tipo_concepto;
	}
	public void setRow_tipo_concepto(int row_tipo_concepto) {
		this.row_tipo_concepto = row_tipo_concepto;
	}
	
	

}
