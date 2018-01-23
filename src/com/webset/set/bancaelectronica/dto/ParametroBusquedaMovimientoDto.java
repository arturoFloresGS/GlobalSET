package com.webset.set.bancaelectronica.dto;
/**
 * 
 * @author Sergio Vaca
 * @since 23/Octubre/2010
 * 
 */
public class ParametroBusquedaMovimientoDto {
	
	private int plNoEmpresa;
	private boolean pbOptMismo;
	private int piNoUsuario;
	private String psDivisaA;
	private boolean pbSpeua;
	private int piBanco;
	private boolean pbConvenioCIE;
	private boolean pbSoloComericaACH;	
	private double pdMontoIni;
	private double pdMontoFin;
	private int plBancoReceptor;
	private String psfechaValor;
	private String psFechaValorOrig;
	private String psDivisa;
	private String psDivision;
	private String sCasaCambio;
	private boolean pbWorldLink;
	private boolean pbInternacional;
	private boolean pbMassPorCheq;
	
	private int piTodas;
	private int plNoUsuario; 
	
	/*para Actuliazar la tabla movimientos estos 3 parametros*/
	private String psTipoEnvio;
	private String psNomArchivos;
	private boolean pbMasspayment;
	
	
	
	
	public String getPsTipoEnvio() {
		return psTipoEnvio;
	}
	public void setPsTipoEnvio(String psTipoEnvio) {
		this.psTipoEnvio = psTipoEnvio;
	}
	public String getPsNomArchivos() {
		return psNomArchivos;
	}
	public void setPsNomArchivos(String psNomArchivos) {
		this.psNomArchivos = psNomArchivos;
	}
	public boolean isPbMasspayment() {
		return pbMasspayment;
	}
	public void setPbMasspayment(boolean pbMasspayment) {
		this.pbMasspayment = pbMasspayment;
	}
	public int getPiTodas() {
		return piTodas;
	}
	public void setPiTodas(int piTodas) {
		this.piTodas = piTodas;
	}
	public int getPlNoUsuario() {
		return plNoUsuario;
	}
	public void setPlNoUsuario(int plNoUsuario) {
		this.plNoUsuario = plNoUsuario;
	}
	public int getPlNoEmpresa() {
		return plNoEmpresa;
	}
	public void setPlNoEmpresa(int plNoEmpresa) {
		this.plNoEmpresa = plNoEmpresa;
	}
	public boolean isPbOptMismo() {
		return pbOptMismo;
	}
	public void setPbOptMismo(boolean pbOptMismo) {
		this.pbOptMismo = pbOptMismo;
	}
	public int getPiNoUsuario() {
		return piNoUsuario;
	}
	public void setPiNoUsuario(int piNoUsuario) {
		this.piNoUsuario = piNoUsuario;
	}
	public String getPsDivisaA() {
		return psDivisaA;
	}
	public void setPsDivisaA(String psDivisaA) {
		this.psDivisaA = psDivisaA;
	}
	public boolean isPbSpeua() {
		return pbSpeua;
	}
	public void setPbSpeua(boolean pbSpeua) {
		this.pbSpeua = pbSpeua;
	}
	public int getPiBanco() {
		return piBanco;
	}
	public void setPiBanco(int piBanco) {
		this.piBanco = piBanco;
	}
	public boolean isPbConvenioCIE() {
		return pbConvenioCIE;
	}
	public void setPbConvenioCIE(boolean pbConvenioCIE) {
		this.pbConvenioCIE = pbConvenioCIE;
	}
	public boolean isPbSoloComericaACH() {
		return pbSoloComericaACH;
	}
	public void setPbSoloComericaACH(boolean pbSoloComericaACH) {
		this.pbSoloComericaACH = pbSoloComericaACH;
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
	public int getPlBancoReceptor() {
		return plBancoReceptor;
	}
	public void setPlBancoReceptor(int plBancoReceptor) {
		this.plBancoReceptor = plBancoReceptor;
	}
	public String getPsfechaValor() {
		return psfechaValor;
	}
	public void setPsfechaValor(String psfechaValor) {
		this.psfechaValor = psfechaValor;
	}
	public String getPsFechaValorOrig() {
		return psFechaValorOrig;
	}
	public void setPsFechaValorOrig(String psFechaValorOrig) {
		this.psFechaValorOrig = psFechaValorOrig;
	}
	public String getPsDivisa() {
		return psDivisa;
	}
	public void setPsDivisa(String psDivisa) {
		this.psDivisa = psDivisa;
	}
	public String getPsDivision() {
		return psDivision;
	}
	public void setPsDivision(String psDivision) {
		this.psDivision = psDivision;
	}
	public String getSCasaCambio() {
		return sCasaCambio;
	}
	public void setSCasaCambio(String casaCambio) {
		sCasaCambio = casaCambio;
	}
	public boolean isPbWorldLink() {
		return pbWorldLink;
	}
	public void setPbWorldLink(boolean pbWorldLink) {
		this.pbWorldLink = pbWorldLink;
	}
	public boolean isPbInternacional() {
		return pbInternacional;
	}
	public void setPbInternacional(boolean pbInternacional) {
		this.pbInternacional = pbInternacional;
	}
	public boolean isPbMassPorCheq() {
		return pbMassPorCheq;
	}
	public void setPbMassPorCheq(boolean pbMassPorCheq) {
		this.pbMassPorCheq = pbMassPorCheq;
	}
}
