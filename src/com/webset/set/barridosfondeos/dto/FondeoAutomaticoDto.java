package com.webset.set.barridosfondeos.dto;

public class FondeoAutomaticoDto {
	
	private int orden;  
	private int noEmpresaOrigen;
	private int noEmpresaDestino;
	private int idBanco;
	private int noFolioDet;
	private double prestamos;
	private double saldoChequera;
	private double saldoMinimoChequera;
	private double importeF;
	private double tipoCambio;
	private double pm;
	private double ingresos;
	private double importeB;
	private double pmCheques;
	private double importeTraspaso;
	private double importeBarrido;
	private double saldoCoinversion;
	private double saldoCoinversionOtraDivisa;
	private String nomEmpresaOrigen;
	private String nomEmpresaDestino;
	private String descBanco;
	private String idChequera;
	private String idChequeraPadre;
	private String concepto;
	private String idDivisa;
	private String rechazado;
	private String cveControl;
	private String descFormaPago;
	private String beneficiario;
	private String mensaje;
	
	private int idCaja;
	
	public int getOrden() {
		return orden;
	}
	public void setOrden(int orden) {
		this.orden = orden;
	}
	public int getNoEmpresaOrigen() {
		return noEmpresaOrigen;
	}
	public void setNoEmpresaOrigen(int noEmpresaOrigen) {
		this.noEmpresaOrigen = noEmpresaOrigen;
	}
	public int getNoEmpresaDestino() {
		return noEmpresaDestino;
	}
	public void setNoEmpresaDestino(int noEmpresaDestino) {
		this.noEmpresaDestino = noEmpresaDestino;
	}
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public double getPrestamos() {
		return prestamos;
	}
	public void setPrestamos(double prestamos) {
		this.prestamos = prestamos;
	}
	public double getSaldoChequera() {
		return saldoChequera;
	}
	public void setSaldoChequera(double saldoChequera) {
		this.saldoChequera = saldoChequera;
	}
	public double getSaldoMinimoChequera() {
		return saldoMinimoChequera;
	}
	public void setSaldoMinimoChequera(double saldoMinimoChequera) {
		this.saldoMinimoChequera = saldoMinimoChequera;
	}
	public double getImporteF() {
		return importeF;
	}
	public void setImporteF(double importeF) {
		this.importeF = importeF;
	}
	public double getTipoCambio() {
		return tipoCambio;
	}
	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}
	public double getPm() {
		return pm;
	}
	public void setPm(double pm) {
		this.pm = pm;
	}
	public double getPmCheques() {
		return pmCheques;
	}
	public void setPmCheques(double pmCheques) {
		this.pmCheques = pmCheques;
	}
	public double getImporteTraspaso() {
		return importeTraspaso;
	}
	public void setImporteTraspaso(double importeTraspaso) {
		this.importeTraspaso = importeTraspaso;
	}
	public double getSaldoCoinversion() {
		return saldoCoinversion;
	}
	public void setSaldoCoinversion(double saldoCoinversion) {
		this.saldoCoinversion = saldoCoinversion;
	}
	public String getNomEmpresaOrigen() {
		return nomEmpresaOrigen;
	}
	public void setNomEmpresaOrigen(String nomEmpresaOrigen) {
		this.nomEmpresaOrigen = nomEmpresaOrigen;
	}
	public String getNomEmpresaDestino() {
		return nomEmpresaDestino;
	}
	public void setNomEmpresaDestino(String nomEmpresaDestino) {
		this.nomEmpresaDestino = nomEmpresaDestino;
	}
	public String getDescBanco() {
		return descBanco;
	}
	public void setDescBanco(String descBanco) {
		this.descBanco = descBanco;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public double getSaldoCoinversionOtraDivisa() {
		return saldoCoinversionOtraDivisa;
	}
	public void setSaldoCoinversionOtraDivisa(double saldoCoinversionOtraDivisa) {
		this.saldoCoinversionOtraDivisa = saldoCoinversionOtraDivisa;
	}
	public String getRechazado() {
		return rechazado;
	}
	public void setRechazado(String rechazado) {
		this.rechazado = rechazado;
	}
	public double getImporteBarrido() {
		return importeBarrido;
	}
	public void setImporteBarrido(double importeBarrido) {
		this.importeBarrido = importeBarrido;
	}
	public String getCveControl() {
		return cveControl;
	}
	public void setCveControl(String cveControl) {
		this.cveControl = cveControl;
	}
	public String getDescFormaPago() {
		return descFormaPago;
	}
	public void setDescFormaPago(String descFormaPago) {
		this.descFormaPago = descFormaPago;
	}
	public int getNoFolioDet() {
		return noFolioDet;
	}
	public void setNoFolioDet(int noFolioDet) {
		this.noFolioDet = noFolioDet;
	}
	public String getBeneficiario() {
		return beneficiario;
	}
	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}
	public String getIdChequeraPadre() {
		return idChequeraPadre;
	}
	public void setIdChequeraPadre(String idChequeraPadre) {
		this.idChequeraPadre = idChequeraPadre;
	}
	/**
	 * @return the ingresos
	 */
	public double getIngresos() {
		return ingresos;
	}
	/**
	 * @param ingresos the ingresos to set
	 */
	public void setIngresos(double ingresos) {
		this.ingresos = ingresos;
	}
	/**
	 * @return the importeB
	 */
	public double getImporteB() {
		return importeB;
	}
	/**
	 * @param importeB the importeB to set
	 */
	public void setImporteB(double importeB) {
		this.importeB = importeB;
	}
	/**
	 * @return the mensaje
	 */
	public String getMensaje() {
		return mensaje;
	}
	/**
	 * @param mensaje the mensaje to set
	 */
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	public int getIdCaja() {
		return idCaja;
	}
	public void setIdCaja(int idCaja) {
		this.idCaja = idCaja;
	}
	
	
}
