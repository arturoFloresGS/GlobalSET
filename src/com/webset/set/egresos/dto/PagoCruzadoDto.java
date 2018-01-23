package com.webset.set.egresos.dto;

public class PagoCruzadoDto {
	
	private String id;
	private String proveedor;
	private String divisaOrig;
	private String divisaPago;
	private int consecutivo;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getProveedor() {
		return proveedor;
	}
	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}
	public String getDivisaOrig() {
		return divisaOrig;
	}
	public void setDivisaOrig(String divisaOrig) {
		this.divisaOrig = divisaOrig;
	}
	public String getDivisaPago() {
		return divisaPago;
	}
	public void setDivisaPago(String divisaPago) {
		this.divisaPago = divisaPago;
	}
	public int getConsecutivo() {
		return consecutivo;
	}
	public void setConsecutivo(int consecutivo) {
		this.consecutivo = consecutivo;
	}

}
