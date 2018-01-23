package com.webset.set.seguridad.dto;

/**
 * 
 * @author Sergio Vaca
 * @since 19/Octubre/2010
 * @see <p>Tabla seg_componente</p>
 */

/**
 * clase que contiene los metodos get y set para los atributos de la tabla
 */
public class SegComponenteDto {
	private int idComponente;
	private String claveComponente;
	private String descripcion;
	private String estatus;
	private int idTipoComponente;
	private String rutaImagen;
	private int idComponentePadre;
	private String claveComponentePadre;
	private String url;

	private String etiqueta;
	private int orden;
	
	public int getIdComponente() {
		return idComponente;
	}
	public void setIdComponente(int idComponente) {
		this.idComponente = idComponente;
	}
	public String getClaveComponente() {
		return claveComponente;
	}
	public void setClaveComponente(String claveComponente) {
		this.claveComponente = claveComponente;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getEstatus() {
		return estatus;
	}
	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}
	public int getIdTipoComponente() {
		return idTipoComponente;
	}
	public void setIdTipoComponente(int idTipoComponente) {
		this.idTipoComponente = idTipoComponente;
	}
	public String getRutaImagen() {
		return rutaImagen;
	}
	public void setRutaImagen(String rutaImagen) {
		this.rutaImagen = rutaImagen;
	}
	public int getIdComponentePadre() {
		return idComponentePadre;
	}
	public void setIdComponentePadre(int idComponentePadre) {
		this.idComponentePadre = idComponentePadre;
	}
	public String getClaveComponentePadre() {
		return claveComponentePadre;
	}
	public void setClaveComponentePadre(String claveComponentePadre) {
		this.claveComponentePadre = claveComponentePadre;
	}
	public int getOrden() {
		return orden;
	}
	public void setOrden(int orden) {
		this.orden = orden;
	}
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	public String getURL() {
		return url;
	}
	
	public void setURL(String url) {
		this.url = url;
	}
}
