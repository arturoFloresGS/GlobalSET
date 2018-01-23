package com.webset.set.consultas.dto;

public class ParamBusSolicitudDto {
	
	private int idTipoOperacion;
	private int noEmpresa;
	private String idProveedor;
	private String nomTabla;
	private String noDocto;
	private String periodo;
	private String secuencia;
	private String fechaOperacion;
	private String factura;
	private String estatus;
	private String sCausa;
	private int formaPago;
	private int noBenef;
	private String estatusCompensa;
	public String getEstatusCompensa() {
		return estatusCompensa;
	}
	public void setEstatusCompensa(String estatusCompensa) {
		this.estatusCompensa = estatusCompensa;
	}
	public int getNoBenef() {
		return noBenef;
	}
	public void setNoBenef(int noBenef) {
		this.noBenef = noBenef;
	}
	public String getSCausa() {
		return sCausa;
	}
	public void setSCausa(String causa) {
		sCausa = causa;
	}
	public String getEstatus() {
		return estatus;
	}
	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}
	public String getFactura() {
		return factura;
	}
	public void setFactura(String factura) {
		this.factura = factura;
	}
	public int getIdTipoOperacion() {
		return idTipoOperacion;
	}
	public void setIdTipoOperacion(int idTipoOperacion) {
		this.idTipoOperacion = idTipoOperacion;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public String getIdProveedor() {
		return idProveedor;
	}
	public void setIdProveedor(String idProveedor) {
		this.idProveedor = idProveedor;
	}
	public String getNomTabla() {
		return nomTabla;
	}
	public void setNomTabla(String nomTabla) {
		this.nomTabla = nomTabla;
	}
	public String getNoDocto() {
		return noDocto;
	}
	public void setNoDocto(String noDocto) {
		this.noDocto = noDocto;
	}
	public String getPeriodo() {
		return periodo;
	}
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}
	public String getSecuencia() {
		return secuencia;
	}
	public void setSecuencia(String secuencia) {
		this.secuencia = secuencia;
	}
	public String getFechaOperacion() {
		return fechaOperacion;
	}
	public void setFechaOperacion(String fechaOperacion) {
		this.fechaOperacion = fechaOperacion;
	}
	public int getFormaPago() {
		return formaPago;
	}
	public void setFormaPago(int formaPago) {
		this.formaPago = formaPago;
	}

}
