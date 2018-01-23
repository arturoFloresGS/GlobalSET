package com.webset.set.inversiones.dto;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * Objeto DTO que contiene los valores que se registran en la base de datos
 * para una instancia de cotizacion
 * @author COINSIDE
 *
 */
public class CotizacionDto {
	private int idInstitucion;
	private String tipoValor;
	private String fecha;
	private Date fecha2;
	
	private String hora;
	private int noEmpresa;
	private int plazo;
	private String tasa;
	private BigDecimal tasaEquiv28;
	private int idUsuario;
	private String idDivisa;
	
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
	public String getFecha() {
		return fecha;
	}
	/**
	 * @param fecha the fecha to set
	 */
	public void setFecha(String fecha) {
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
	 * @return the plazo
	 */
	public int getPlazo() {
		return plazo;
	}
	/**
	 * @param plazo the plazo to set
	 */
	public void setPlazo(int plazo) {
		this.plazo = plazo;
	}
	/**
	 * @return the tasa
	 */
	public String getTasa() {
		return tasa;
	}
	/**
	 * @param tasa the tasa to set
	 */
	public void setTasa(String tasa) {
		this.tasa = tasa;
	}
	/**
	 * @return the tasaEquiv28
	 */
	public BigDecimal getTasaEquiv28() {
		return tasaEquiv28;
	}
	/**
	 * @param tasaEquiv28 the tasaEquiv28 to set
	 */
	public void setTasaEquiv28(BigDecimal tasaEquiv28) {
		this.tasaEquiv28 = tasaEquiv28;
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
	public Date getFecha2() {
		return fecha2;
	}
	public void setFecha2(Date fecha2) {
		this.fecha2 = fecha2;
	}


}
