package com.webset.set.utilerias.dto;

public class RetencionDto {

	private int plazoMin;
	private int periodo;
	private int plazoMax;
	private double tasaIsr;
	private String leyenda;
	private double tasaMin;
	private double tasaMax;
	private String leyendaPagare;
	
	public int getPlazoMin() {
		return plazoMin;
	}
	public void setPlazoMin(int plazoMin) {
		this.plazoMin = plazoMin;
	}
	public int getPeriodo() {
		return periodo;
	}
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}
	public int getPlazoMax() {
		return plazoMax;
	}
	public void setPlazoMax(int plazoMax) {
		this.plazoMax = plazoMax;
	}
	public double getTasaIsr() {
		return tasaIsr;
	}
	public void setTasaIsr(double tasaIsr) {
		this.tasaIsr = tasaIsr;
	}
	public String getLeyenda() {
		return leyenda;
	}
	public void setLeyenda(String leyenda) {
		this.leyenda = leyenda;
	}
	public double getTasaMin() {
		return tasaMin;
	}
	public void setTasaMin(double tasaMin) {
		this.tasaMin = tasaMin;
	}
	public double getTasaMax() {
		return tasaMax;
	}
	public void setTasaMax(double tasaMax) {
		this.tasaMax = tasaMax;
	}
	public String getLeyendaPagare() {
		return leyendaPagare;
	}
	public void setLeyendaPagare(String leyendaPagare) {
		this.leyendaPagare = leyendaPagare;
	}
	
}
