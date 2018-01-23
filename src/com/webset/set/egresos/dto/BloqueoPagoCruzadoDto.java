package com.webset.set.egresos.dto;

public class BloqueoPagoCruzadoDto {
	
	private String id;
	private String proveedor;
	private String divisaOrig;
	private String divisaPago;
	private String empresa;
	private String noDocumento;
	private String motivo;
	private double importe;
	private String fechaPago;
	private String concepto;
	private boolean bloqueado;
	private String fechaBloqueo;
	private String usuario; //Se pone como String por bloqueos 'SAP'
	private String cveControl;
	
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
	public String getEmpresa() {
		return empresa;
	}
	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}
	public String getNoDocumento() {
		return noDocumento;
	}
	public void setNoDocumento(String noDocumento) {
		this.noDocumento = noDocumento;
	}
	public String getMotivo() {
		return motivo;
	}
	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
	public String getFechaPago() {
		return fechaPago;
	}
	public void setFechaPago(String fechaPago) {
		this.fechaPago = fechaPago;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public boolean isBloqueado() {
		return bloqueado;
	}
	public void setBloqueado(boolean bloqueado) {
		this.bloqueado = bloqueado;
	}
	public String getFechaBloqueo() {
		return fechaBloqueo;
	}
	public void setFechaBloqueo(String fechaBloqueo) {
		this.fechaBloqueo = fechaBloqueo;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getCveControl() {
		return cveControl;
	}
	public void setCveControl(String cveControl) {
		this.cveControl = cveControl;
	}

	
}
