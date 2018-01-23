package com.webset.set.seguridad.dto;
/**
 * Esta clase representa la tabla seg_tipofac en el modulo Seguridad
 * @author Cristian Garcia Garcia
 *
 */
public class SegTipoFacDto {
	
	private String tTipoFacultad;
	private String dDescripcion;
	private String bBloqueado;
	public String getTTipoFacultad() {
		return tTipoFacultad;
	}
	public void setTTipoFacultad(String tipoFacultad) {
		tTipoFacultad = tipoFacultad;
	}
	public String getDDescripcion() {
		return dDescripcion;
	}
	public void setDDescripcion(String descripcion) {
		dDescripcion = descripcion;
	}
	public String getBBloqueado() {
		return bBloqueado;
	}
	public void setBBloqueado(String bloqueado) {
		bBloqueado = bloqueado;
	}

}
