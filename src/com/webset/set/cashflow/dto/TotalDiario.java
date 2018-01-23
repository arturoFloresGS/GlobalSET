package com.webset.set.cashflow.dto;

public class TotalDiario {
	
	private String fec_valor;
	private double total;
	private String origen;
	private int row_total_soa;
	private int row_total_sd;
	
	public String getFec_valor() {
		return fec_valor;
	}
	public void setFec_valor(String fec_valor) {
		this.fec_valor = fec_valor;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public int getRow_total_soa() {
		return row_total_soa;
	}
	public void setRow_total_soa(int row_total_soa) {
		this.row_total_soa = row_total_soa;
	}
	public int getRow_total_sd() {
		return row_total_sd;
	}
	public void setRow_total_sd(int row_total_sd) {
		this.row_total_sd = row_total_sd;
	}
	public String getOrigen() {
		return origen;
	}
	public void setOrigen(String origen) {
		this.origen = origen;
	}
	
	

}
