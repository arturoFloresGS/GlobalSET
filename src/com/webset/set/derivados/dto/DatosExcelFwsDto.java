package com.webset.set.derivados.dto;

public class DatosExcelFwsDto {
	
	private int folio;
	private String unidad_negocio;
	private String divisa_venta;
	private int banco_cargo;
	private String chequera_cargo;
	private String divisa_compra;
	private int banco_abono;
	private String chequera_abono;	
	private String forma_pago;	
	private Double importe_pago;
	private Double importe_compra;  // fws	
	private Double tc;
	private String fec_compra;
	private String fec_vto;
	private int institucion;
	private String desc_institucion;
	private String nom_contacto;
	private int banco_benef;
	private String chequera_benef;	
	private String rubro_cargo;
	private String subrubro_cargo;
	private String rubro_abono;		 			 
	private String subrubro_abono;
	private char estatus_mov;
	private char estatus_imp;
	private String firmante1;
	private String firmante2;
	private String no_docto;
	private Double spot;
	private Double puntos_forward;
	private String observacion;
	private int caja;
	private String referencia;
	private String concepto;
	
	
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public int getCaja() {
		return caja;
	}
	public void setCaja(int caja) {
		this.caja = caja;
	}
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	public int getFolio() {
		return folio;
	}
	public void setFolio(int folio) {
		this.folio = folio;
	}
	public String getUnidad_negocio() {
		return unidad_negocio;
	}
	public void setUnidad_negocio(String unidad_negocio) {
		this.unidad_negocio = unidad_negocio;
	}
	public String getDivisa_venta() {
		return divisa_venta;
	}
	public void setDivisa_venta(String divisa_venta) {
		this.divisa_venta = divisa_venta;
	}
	public int getBanco_cargo() {
		return banco_cargo;
	}
	public void setBanco_cargo(int banco_cargo) {
		this.banco_cargo = banco_cargo;
	}
	public String getChequera_cargo() {
		return chequera_cargo;
	}
	public void setChequera_cargo(String chequera_cargo) {
		this.chequera_cargo = chequera_cargo;
	}
	public String getDivisa_compra() {
		return divisa_compra;
	}
	public void setDivisa_compra(String divisa_compra) {	
		this.divisa_compra = divisa_compra;
	}
	public int getBanco_abono() {
		return banco_abono;
	}
	public void setBanco_abono(int banco_abono) {
		this.banco_abono = banco_abono;
	}
	public String getChequera_abono() {
		return chequera_abono;
	}
	public void setChequera_abono(String chequera_abono) {
		this.chequera_abono = chequera_abono;
	}
	public String getForma_pago() {
		return forma_pago;
	}
	public void setForma_pago(String forma_pago) {
		this.forma_pago = forma_pago;
	}
	public Double getImporte_pago() {
		return importe_pago;
	}
	public void setImporte_pago(Double importe_pago) {
		this.importe_pago = importe_pago;
	}
	public Double getImporte_compra() {
		return importe_compra;
	}
	public void setImporte_compra(Double importe_compra) {
		this.importe_compra = importe_compra;
	}
	public Double getTc() {
		return tc;
	}
	public void setTc(Double tc) {
		this.tc = tc;
	}
	public String getFec_compra() {
		return fec_compra;
	}
	public void setFec_compra(String fec_compra) {
		this.fec_compra = fec_compra;
	}
	public String getFec_vto() {
		return fec_vto;
	}
	public void setFec_vto(String fec_vto) {
		this.fec_vto = fec_vto;
	}
	public int getInstitucion() {
		return institucion;
	}
	public void setInstitucion(int institucion) {
		this.institucion = institucion;
	}
	public String getDesc_institucion() {
		return desc_institucion;
	}
	public void setDesc_institucion(String desc_institucion) {
		this.desc_institucion = desc_institucion;
	}
	public String getNom_contacto() {
		return nom_contacto;
	}
	public void setNom_contacto(String nom_contacto) {
		this.nom_contacto = nom_contacto;
	}
	public int getBanco_benef() {
		return banco_benef;
	}
	public void setBanco_benef(int banco_benef) {
		this.banco_benef = banco_benef;
	}
	public String getChequera_benef() {
		return chequera_benef;
	}
	public void setChequera_benef(String chequera_benef) {
		this.chequera_benef = chequera_benef;
	}
	public String getRubro_cargo() {
		return rubro_cargo;
	}
	public void setRubro_cargo(String rubro_cargo) {
		this.rubro_cargo = rubro_cargo;
	}
	public String getSubrubro_cargo() {
		return subrubro_cargo;
	}
	public void setSubrubro_cargo(String subrubro_cargo) {
		this.subrubro_cargo = subrubro_cargo;
	}
	public String getRubro_abono() {
		return rubro_abono;
	}
	public void setRubro_abono(String rubro_abono) {
		this.rubro_abono = rubro_abono;
	}
	public String getSubrubro_abono() {
		return subrubro_abono;
	}
	public void setSubrubro_abono(String subrubro_abono) {
		this.subrubro_abono = subrubro_abono;
	}
	public char getEstatus_mov() {
		return estatus_mov;
	}
	public void setEstatus_mov(char estatus_mov) {
		this.estatus_mov = estatus_mov;
	}
	public char getEstatus_imp() {
		return estatus_imp;
	}
	public void setEstatus_imp(char estatus_imp) {
		this.estatus_imp = estatus_imp;
	}
	public String getFirmante1() {
		return firmante1;
	}
	public void setFirmante1(String firmante1) {
		this.firmante1 = firmante1;
	}
	public String getFirmante2() {
		return firmante2;
	}
	public void setFirmante2(String firmante2) {
		this.firmante2 = firmante2;
	}
	public String getNo_docto() {
		return no_docto;
	}
	public void setNo_docto(String no_docto) {
		this.no_docto = no_docto;
	}
	public Double getSpot() {
		return spot;
	}
	public void setSpot(Double spot) {
		this.spot = spot;
	}
	public Double getPuntos_forward() {
		return puntos_forward;
	}
	public void setPuntos_forward(Double puntos_forward) {
		this.puntos_forward = puntos_forward;
	}
	
	
	
		
	
}

