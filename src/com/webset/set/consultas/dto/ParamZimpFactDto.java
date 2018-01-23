package com.webset.set.consultas.dto;
/**
 * Esta clase es utilizada para pasar parametros de busqueda 
 * @author Crar
 *
 */
public class ParamZimpFactDto {
	private int noEmpresa;
	private String noDocto;
	private String periodo;
	private String noProveedor;
	private String factura;
	
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
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
	public String getNoProveedor() {
		return noProveedor;
	}
	public void setNoProveedor(String noProveedor) {
		this.noProveedor = noProveedor;
	}
	public String getFactura() {
		return factura;
	}
	public void setFactura(String factura) {
		this.factura = factura;
	}
 
}
