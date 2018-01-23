package com.webset.set.inversiones.dto;

import java.sql.Date;
import java.util.List;
import java.util.Map;

public class CotizacionesDto {
	
	private List<Map<String, Object>> tasas;
	private int idInstitucion;
	private String tipoValor;
	private Date fecha;
	private String hora;
	private int noEmpresa;
	private int idUsuario;
	private String idDivisa;
	
	/**
	 * @return the tasas
	 */
	public List<Map<String, Object>> getTasas() {
		return tasas;
	}
	/**
	 * @param tasas the tasas to set
	 */
	public void setTasas(List<Map<String, Object>> tasas) {
		this.tasas = tasas;
	}
	/**
	 * @return the idInstitucion
	 */
	public int getIdInstitucion() {
		return idInstitucion;
	}
	/**
	 * @param idInstitucion the idInstitucion to set
	 */
	public void setIdInstitucion(int idInstitucion) {
		this.idInstitucion = idInstitucion;
	}
	/**
	 * @return the tipoValor
	 */
	public String getTipoValor() {
		return tipoValor;
	}
	/**
	 * @param tipoValor the tipoValor to set
	 */
	public void setTipoValor(String tipoValor) {
		this.tipoValor = tipoValor;
	}
	/**
	 * @return the fecha
	 */
	public Date getFecha() {
		return fecha;
	}
	/**
	 * @param fecha the fecha to set
	 */
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	/**
	 * @return the hora
	 */
	public String getHora() {
		return hora;
	}
	/**
	 * @param hora the hora to set
	 */
	public void setHora(String hora) {
		this.hora = hora;
	}
	/**
	 * @return the noEmpresa
	 */
	public int getNoEmpresa() {
		return noEmpresa;
	}
	/**
	 * @param noEmpresa the noEmpresa to set
	 */
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	/**
	 * @return the idUsuario
	 */
	public int getIdUsuario() {
		return idUsuario;
	}
	/**
	 * @param idUsuario the idUsuario to set
	 */
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	/**
	 * @return the idDivisa
	 */
	public String getIdDivisa() {
		return idDivisa;
	}
	/**
	 * @param idDivisa the idDivisa to set
	 */
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}

}
