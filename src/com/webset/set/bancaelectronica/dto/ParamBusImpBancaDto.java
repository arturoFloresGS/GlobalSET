package com.webset.set.bancaelectronica.dto;
/**
 * Clase utilizada como dto para los parametros del metodo de buscar en 
 * ImportaBancaElectronica
 * @author Cristian García García
 * @since 07/12/2010
 */
public class ParamBusImpBancaDto {
	
	
	private boolean pvOption ; 
	private int noEmpresa;
    private boolean pbTodasEmp;
    private String  psTipoMovto;
    private int plNoUsuario;
    private int plBanco;
    private String psChequera; 
    private double pdMontoIni;
    private double pdMontoFin;
    private String psFechaValorIni;
    private String psFechaValorFin;
    private String psConcepto;
    private boolean pbReporte;
    private boolean pbTipoCheq;
    private boolean pbInversion;
    private boolean optCapturados;
    
	public boolean isPvOption() {
		return pvOption;
	}
	public void setPvOption(boolean pvOption) {
		this.pvOption = pvOption;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public boolean isPbTodasEmp() {
		return pbTodasEmp;
	}
	public void setPbTodasEmp(boolean pbTodasEmp) {
		this.pbTodasEmp = pbTodasEmp;
	}
	public String getPsTipoMovto() {
		return psTipoMovto;
	}
	public void setPsTipoMovto(String psTipoMovto) {
		this.psTipoMovto = psTipoMovto;
	}
	public int getPlNoUsuario() {
		return plNoUsuario;
	}
	public void setPlNoUsuario(int plNoUsuario) {
		this.plNoUsuario = plNoUsuario;
	}
	public int getPlBanco() {
		return plBanco;
	}
	public void setPlBanco(int plBanco) {
		this.plBanco = plBanco;
	}
	public String getPsChequera() {
		return psChequera;
	}
	public void setPsChequera(String psChequera) {
		this.psChequera = psChequera;
	}
	public double getPdMontoIni() {
		return pdMontoIni;
	}
	public void setPdMontoIni(double pdMontoIni) {
		this.pdMontoIni = pdMontoIni;
	}
	public double getPdMontoFin() {
		return pdMontoFin;
	}
	public void setPdMontoFin(double pdMontoFin) {
		this.pdMontoFin = pdMontoFin;
	}
	public String getPsFechaValorIni() {
		return psFechaValorIni;
	}
	public void setPsFechaValorIni(String psFechaValorIni) {
		this.psFechaValorIni = psFechaValorIni;
	}
	public String getPsFechaValorFin() {
		return psFechaValorFin;
	}
	public void setPsFechaValorFin(String psFechaValorFin) {
		this.psFechaValorFin = psFechaValorFin;
	}
	public String getPsConcepto() {
		return psConcepto;
	}
	public void setPsConcepto(String psConcepto) {
		this.psConcepto = psConcepto;
	}
	public boolean isPbReporte() {
		return pbReporte;
	}
	public void setPbReporte(boolean pbReporte) {
		this.pbReporte = pbReporte;
	}
	public boolean isPbTipoCheq() {
		return pbTipoCheq;
	}
	public void setPbTipoCheq(boolean pbTipoCheq) {
		this.pbTipoCheq = pbTipoCheq;
	}
	public boolean isPbInversion() {
		return pbInversion;
	}
	public void setPbInversion(boolean pbInversion) {
		this.pbInversion = pbInversion;
	}
	public boolean isOptCapturados() {
		return optCapturados;
	}
	public void setOptCapturados(boolean optCapturados) {
		this.optCapturados = optCapturados;
	}

}
