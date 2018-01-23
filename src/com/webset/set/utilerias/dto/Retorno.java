package com.webset.set.utilerias.dto;

public class Retorno {
	private int id;
	private int idA;
	private boolean resultado;
	private String valorConfiguraSet;
	private String fechaHoy;
	private String fechaAyer;
	public String getFechaHoy() {
		return fechaHoy;
	}
	public void setFechaHoy(String fechaHoy) {
		this.fechaHoy = fechaHoy;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdA() {
		return idA;
	}
	public void setIdA(int idA) {
		this.idA = idA;
	}
	public boolean isResultado() {
		return resultado;
	}
	public void setResultado(boolean resultado) {
		this.resultado = resultado;
	}
	public String getValorConfiguraSet() {
		return valorConfiguraSet;
	}
	public void setValorConfiguraSet(String valorConfiguraSet) {
		this.valorConfiguraSet = valorConfiguraSet;
	}
	public String getFechaAyer() {
		return fechaAyer;
	}
	public void setFechaAyer(String fechaAyer) {
		this.fechaAyer = fechaAyer;
	}
}
