package com.webset.set.egresos.dto;

public class ResultadoDto {
	
	private boolean resultado; 
	private String mensaje; 
	private String folio;
	
	
	
	public ResultadoDto() {
		super();
	}
	public ResultadoDto(boolean resultado, String mensaje, String folio) {
		super();
		this.resultado = resultado;
		this.mensaje = mensaje;
		this.folio = folio;
	}
	public boolean isResultado() {
		return resultado;
	}
	public void setResultado(boolean resultado) {
		this.resultado = resultado;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	public String getFolio() {
		return folio;
	}
	public void setFolio(String folio) {
		this.folio = folio;
	} 
	
	

}
