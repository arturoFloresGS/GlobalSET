package com.webset.set.financiamiento.dto;

public class GastoComisionCreditoDto {

	private String idContrato;
	private int idDisposicion;
	private int tipoGasto;
	private String descripcion;
	private double gasto;
	private double comision;
	private double iva;
	private int bancoGastcom;
	private String descBanco;
	private String clabeBancariaGastcom;
	private int idAmortizacion;
	private char pagar;
	private String fecPago;
	private double total;
	private int tipoPago;

	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public int getTipoPago() {
		return tipoPago;
	}
	public void setTipoPago(int tipoPago) {
		this.tipoPago = tipoPago;
	}
	public String getIdContrato() {
		return idContrato;
	}
	public void setIdContrato(String idContrato) {
		this.idContrato = idContrato;
	}
	public int getIdDisposicion() {
		return idDisposicion;
	}
	public void setIdDisposicion(int idDisposicion) {
		this.idDisposicion = idDisposicion;
	}
	public int getTipoGasto() {
		return tipoGasto;
	}
	public void setTipoGasto(int tipoGasto) {
		this.tipoGasto = tipoGasto;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public double getGasto() {
		return gasto;
	}
	public void setGasto(double gasto) {
		this.gasto = gasto;
	}
	public double getComision() {
		return comision;
	}
	public void setComision(double comision) {
		this.comision = comision;
	}
	public double getIva() {
		return iva;
	}
	public void setIva(double iva) {
		this.iva = iva;
	}
	public int getBancoGastcom() {
		return bancoGastcom;
	}
	public void setBancoGastcom(int bancoGastcom) {
		this.bancoGastcom = bancoGastcom;
	}
	public String getDescBanco() {
		return descBanco;
	}
	public void setDescBanco(String descBanco) {
		this.descBanco = descBanco;
	}
	public String getClabeBancariaGastcom() {
		return clabeBancariaGastcom;
	}
	public void setClabeBancariaGastcom(String clabeBancariaGastcom) {
		this.clabeBancariaGastcom = clabeBancariaGastcom;
	}
	public int getIdAmortizacion() {
		return idAmortizacion;
	}
	public void setIdAmortizacion(int idAmortizacion) {
		this.idAmortizacion = idAmortizacion;
	}
	public char getPagar() {
		return pagar;
	}
	public void setPagar(char pagar) {
		this.pagar = pagar;
	}
	public String getFecPago() {
		return fecPago;
	}
	public void setFecPago(String fecPago) {
		this.fecPago = fecPago;
	}
	
}
