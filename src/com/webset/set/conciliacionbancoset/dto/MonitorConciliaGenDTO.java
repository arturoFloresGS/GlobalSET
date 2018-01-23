package com.webset.set.conciliacionbancoset.dto;

import java.math.BigDecimal;

public class MonitorConciliaGenDTO {
	private Integer cantidad;
	private BigDecimal importe;
	private BigDecimal importe2;
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	public BigDecimal getImporte() {
		return importe;
	}
	public void setImporte(BigDecimal importe) {
		this.importe = importe;
	}
	public BigDecimal getImporte2() {
		return importe2;
	}
	public void setImporte2(BigDecimal importe2) {
		this.importe2 = importe2;
	}
	
	
}
