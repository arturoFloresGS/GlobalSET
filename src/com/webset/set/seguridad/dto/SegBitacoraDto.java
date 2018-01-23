package com.webset.set.seguridad.dto;

import java.util.Date;

/**
 * 
 * @author Sergio Vaca
 * @since 19/Octubre/2010
 * @see <p>Tabla seg_bitacora</p>
 */
/**
 * clase que contiene los metodos get y set para los atributos de la tabla
 */
public class SegBitacoraDto {
	private int idBitacora;
	private int idError;
	private int idTipoBitacora;
	private int idUsuario;
	private Date fecha;
	private String descripcion;	
	private String claveUsuario;
	
	public int getIdBitacora() {
		return idBitacora;
	}
	public void setIdBitacora(int idBitacora) {
		this.idBitacora = idBitacora;
	}
	public int getIdError() {
		return idError;
	}
	public void setIdError(int idError) {
		this.idError = idError;
	}
	public int getIdTipoBitacora() {
		return idTipoBitacora;
	}
	public void setIdTipoBitacora(int idTipoBitacora) {
		this.idTipoBitacora = idTipoBitacora;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public String getClaveUsuario() {
		return claveUsuario;
	}
	public void setClaveUsuario(String claveUsuario) {
		this.claveUsuario = claveUsuario;
	}
}
