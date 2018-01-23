package com.webset.set.financiamiento.dto;

/*rodrig
 * echo por angel israel JR
 * esta clase representa todos los datos de la tabla 
 * cat_disposicion_credito y cat_amortizacion_credito
 * para pegarlos al grid 
 * Financiamiento --->   credito bancario---> MODIFICACION-C
 * 
 * */ 
public class Amortizaciones {
	
	private String fec_pago;
	private String fec_inicio;
	private int dias;
	private float saldo_insoluto;
	private float interes;
	private String periodo;
	private String tasa_base;
	private float valor_tasa;
	private float puntos;
	private float monto_disposicion;
	private float iva;
	
	
	
	public float getMonto_disposicion() {
		return monto_disposicion;
	}
	public void setMonto_disposicion(float monto_disposicion) {
		this.monto_disposicion = monto_disposicion;
	}
	public String getFec_pago() {
		return fec_pago;
	}
	public void setFec_pago(String fec_pago) {
		this.fec_pago = fec_pago;
	}
	public String getFec_inicio() {
		return fec_inicio;
	}
	public void setFec_inicio(String fec_inicio) {
		this.fec_inicio = fec_inicio;
	}
	public int getDias() {
		return dias;
	}
	public void setDias(int dias) {
		this.dias = dias;
	}
	public float getSaldo_insoluto() {
		return saldo_insoluto;
	}
	public void setSaldo_insoluto(float saldo_insoluto) {
		this.saldo_insoluto = saldo_insoluto;
	}
	public float getInteres() {
		return interes;
	}
	public void setInteres(float interes) {
		this.interes = interes;
	}
	public String getPeriodo() {
		return periodo;
	}
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public float getValor_tasa() {
		return valor_tasa;
	}
	public void setValor_tasa(float valor_tasa) {
		this.valor_tasa = valor_tasa;
	}
	public float getPuntos() {
		return puntos;
	}
	public void setPuntos(float puntos) {
		this.puntos = puntos;
	}

	public float getIva() {
		return iva;
	}
	public void setIva(float iva) {
		this.iva = iva;
	}
	public String getTasa_base() {
		return tasa_base;
	}
	public void setTasa_base(String tasa_base) {
		this.tasa_base = tasa_base;
	}
	
	
	
	
	
	
	

}
