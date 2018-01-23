package com.webset.set.egresos.dto;

public class BitacoraPropuestasDto {

	private String fecha;
	private int usuario;
	private String operacion;
	private String propuesta;
	private String valorAnt;
	private String valorNuevo;
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public int getUsuario() {
		return usuario;
	}
	public void setUsuario(int usuario) {
		this.usuario = usuario;
	}
	public String getOperacion() {
		return operacion;
	}
	public void setOperacion(String operacion) {
		this.operacion = operacion;
	}
	public String getPropuesta() {
		return propuesta;
	}
	public void setPropuesta(String propuesta) {
		this.propuesta = propuesta;
	}
	public String getValorAnt() {
		return valorAnt;
	}
	public void setValorAnt(String valorAnt) {
		this.valorAnt = valorAnt;
	}
	public String getValorNuevo() {
		return valorNuevo;
	}
	public void setValorNuevo(String valorNuevo) {
		this.valorNuevo = valorNuevo;
	}
	
	
}
