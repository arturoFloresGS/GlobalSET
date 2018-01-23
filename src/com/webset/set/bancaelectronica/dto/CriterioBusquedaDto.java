package com.webset.set.bancaelectronica.dto;

public class CriterioBusquedaDto {
	private double pdMontoIni;
	private double pdMontoFin;
	private int plBancoReceptor;
	private String psFechaValor;
	private String psFechaValorOrig;
	private String psDivisa;
	private String psDivision;
	
	private int idEmpresa;
	private String optTipoEnvio;
	private int idUsuario;
	private String idDivisa;
	private int idBanco;
	private boolean convenioCie;
	private String optComerica;
	private double montoIni;
	private double montoFin;
	private String sCasaCambio;
	
	private boolean chkTodasEmpresas;
	private boolean chkSoloLocales;
	private boolean chkConvenioCie;
	private boolean chkConvenioSant;
	private boolean chkMassPayment;
	private boolean chkH2H;
	private boolean chkH2HSantander;
	public boolean isChkDebito() {
		return chkDebito;
	}
	public void setChkDebito(boolean chkDebito) {
		this.chkDebito = chkDebito;
	}
	private boolean chkDebito;
	private boolean nomina;
	
	private boolean massPorCheq;
	private boolean optVenc;
	
	int optEnvioBNMX;
	int optEnvioHSBC;
	int optEnvioBBVA;
	
	public int getOptEnvioBBVA() {
		return optEnvioBBVA;
	}
	public void setOptEnvioBBVA(int optEnvioBBVA) {
		this.optEnvioBBVA = optEnvioBBVA;
	}
	//Campos para la pantalla de envio de transferencias del layout banamex mismo banco
	int idTipoEnvio;
	int secuencia;
	String campo;
	int posIni;
	int posFin;
	int longitud;
	String bRequerido;
	String valorDefault;
	int idTipoEquivale;
	String formato;
	String justifica;
	String separador;
	String complemento;
	String tabla;
	String condicionTabla;
	String tipoDato;
	String equivaleCampo;
	String validaCampo;
	int validacionEspecial;
	String funcionEspecial;
	String valorCampo;
	String valorEquivale;
	
	String noFactura;
	String concepto;
	
	/*boolean optDigitem;
	boolean optTEF;
	boolean optCitiWL;
	boolean optMassPayment;
	boolean optCitiFFDls;
	boolean optCitiPayLinkMN;
	boolean optCitiACH;
	
	public boolean isOptDigitem() {
		return optDigitem;
	}
	public void setOptDigitem(boolean optDigitem) {
		this.optDigitem = optDigitem;
	}
	public boolean isOptTEF() {
		return optTEF;
	}
	public void setOptTEF(boolean optTEF) {
		this.optTEF = optTEF;
	}
	public boolean isOptCitiWL() {
		return optCitiWL;
	}
	public void setOptCitiWL(boolean optCitiWL) {
		this.optCitiWL = optCitiWL;
	}
	public boolean isOptMassPayment() {
		return optMassPayment;
	}
	public void setOptMassPayment(boolean optMassPayment) {
		this.optMassPayment = optMassPayment;
	}
	public boolean isOptCitiFFDls() {
		return optCitiFFDls;
	}
	public void setOptCitiFFDls(boolean optCitiFFDls) {
		this.optCitiFFDls = optCitiFFDls;
	}
	public boolean isOptCitiPayLinkMN() {
		return optCitiPayLinkMN;
	}
	public void setOptCitiPayLinkMN(boolean optCitiPayLinkMN) {
		this.optCitiPayLinkMN = optCitiPayLinkMN;
	}
	public boolean isOptCitiACH() {
		return optCitiACH;
	}
	public void setOptCitiACH(boolean optCitiACH) {
		this.optCitiACH = optCitiACH;
	}
	*/
	
	public String getNoFactura() {
		return noFactura;
	}
	public void setNoFactura(String noFactura) {
		this.noFactura = noFactura;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public String getValorCampo() {
		return valorCampo;
	}
	public void setValorCampo(String valorCampo) {
		this.valorCampo = valorCampo;
	}
	public String getValorEquivale() {
		return valorEquivale;
	}
	public void setValorEquivale(String valorEquivale) {
		this.valorEquivale = valorEquivale;
	}
	public int getIdTipoEnvio() {
		return idTipoEnvio;
	}
	public void setIdTipoEnvio(int idTipoEnvio) {
		this.idTipoEnvio = idTipoEnvio;
	}
	public int getSecuencia() {
		return secuencia;
	}
	public void setSecuencia(int secuencia) {
		this.secuencia = secuencia;
	}
	public String getCampo() {
		return campo;
	}
	public void setCampo(String campo) {
		this.campo = campo;
	}
	public int getPosIni() {
		return posIni;
	}
	public void setPosIni(int posIni) {
		this.posIni = posIni;
	}
	public int getPosFin() {
		return posFin;
	}
	public void setPosFin(int posFin) {
		this.posFin = posFin;
	}
	public int getLongitud() {
		return longitud;
	}
	public void setLongitud(int longitud) {
		this.longitud = longitud;
	}
	public String getBRequerido() {
		return bRequerido;
	}
	public void setBRequerido(String requerido) {
		bRequerido = requerido;
	}
	public String getValorDefault() {
		return valorDefault;
	}
	public void setValorDefault(String valorDefault) {
		this.valorDefault = valorDefault;
	}
	public int getIdTipoEquivale() {
		return idTipoEquivale;
	}
	public void setIdTipoEquivale(int idTipoEquivale) {
		this.idTipoEquivale = idTipoEquivale;
	}
	public String getFormato() {
		return formato;
	}
	public void setFormato(String formato) {
		this.formato = formato;
	}
	public String getJustifica() {
		return justifica;
	}
	public void setJustifica(String justifica) {
		this.justifica = justifica;
	}
	public String getSeparador() {
		return separador;
	}
	public void setSeparador(String separador) {
		this.separador = separador;
	}
	public String getComplemento() {
		return complemento;
	}
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	public String getTabla() {
		return tabla;
	}
	public void setTabla(String tabla) {
		this.tabla = tabla;
	}
	public String getCondicionTabla() {
		return condicionTabla;
	}
	public void setCondicionTabla(String condicionTabla) {
		this.condicionTabla = condicionTabla;
	}
	public String getTipoDato() {
		return tipoDato;
	}
	public void setTipoDato(String tipoDato) {
		this.tipoDato = tipoDato;
	}
	public String getEquivaleCampo() {
		return equivaleCampo;
	}
	public void setEquivaleCampo(String equivaleCampo) {
		this.equivaleCampo = equivaleCampo;
	}
	public String getValidaCampo() {
		return validaCampo;
	}
	public void setValidaCampo(String validaCampo) {
		this.validaCampo = validaCampo;
	}
	public int getValidacionEspecial() {
		return validacionEspecial;
	}
	public void setValidacionEspecial(int validacionEspecial) {
		this.validacionEspecial = validacionEspecial;
	}
	public String getFuncionEspecial() {
		return funcionEspecial;
	}
	public void setFuncionEspecial(String funcionEspecial) {
		this.funcionEspecial = funcionEspecial;
	}
	public int getOptEnvioBNMX() {
		return optEnvioBNMX;
	}
	public void setOptEnvioBNMX(int optEnvioBNMX) {
		this.optEnvioBNMX = optEnvioBNMX;
	}
	public int getOptEnvioHSBC() {
		return optEnvioHSBC;
	}
	public void setOptEnvioHSBC(int optEnvioHSBC) {
		this.optEnvioHSBC = optEnvioHSBC;
	}
	public boolean isChkH2H() {
		return chkH2H;
	}
	public void setChkH2H(boolean chkH2H) {
		this.chkH2H = chkH2H;
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
	public String getPsFechaValor() {
		return psFechaValor;
	}
	public void setPsFechaValor(String psFechaValor) {
		this.psFechaValor = psFechaValor;
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
	public int getIdEmpresa() {
		return idEmpresa;
	}
	public void setIdEmpresa(int idEmpresa) {
		this.idEmpresa = idEmpresa;
	}
	public String getOptTipoEnvio() {
		return optTipoEnvio;
	}
	public void setOptTipoEnvio(String optTipoEnvio) {
		this.optTipoEnvio = optTipoEnvio;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public boolean isConvenioCie() {
		return convenioCie;
	}
	public void setConvenioCie(boolean convenioCie) {
		this.convenioCie = convenioCie;
	}
	public String getOptComerica() {
		return optComerica;
	}
	public void setOptComerica(String optComerica) {
		this.optComerica = optComerica;
	}
	public double getMontoIni() {
		return montoIni;
	}
	public void setMontoIni(double montoIni) {
		this.montoIni = montoIni;
	}
	public double getMontoFin() {
		return montoFin;
	}
	public void setMontoFin(double montoFin) {
		this.montoFin = montoFin;
	}
	public String getSCasaCambio() {
		return sCasaCambio;
	}
	public void setSCasaCambio(String casaCambio) {
		sCasaCambio = casaCambio;
	}
	public boolean isChkTodasEmpresas() {
		return chkTodasEmpresas;
	}
	public void setChkTodasEmpresas(boolean chkTodasEmpresas) {
		this.chkTodasEmpresas = chkTodasEmpresas;
	}
	public boolean isChkSoloLocales() {
		return chkSoloLocales;
	}
	public void setChkSoloLocales(boolean chkSoloLocales) {
		this.chkSoloLocales = chkSoloLocales;
	}
	public boolean isChkConvenioCie() {
		return chkConvenioCie;
	}
	public void setChkConvenioCie(boolean chkConvenioCie) {
		this.chkConvenioCie = chkConvenioCie;
	}
	public boolean isChkMassPayment() {
		return chkMassPayment;
	}
	public void setChkMassPayment(boolean chkMassPayment) {
		this.chkMassPayment = chkMassPayment;
	}
	public boolean isMassPorCheq() {
		return massPorCheq;
	}
	public void setMassPorCheq(boolean massPorCheq) {
		this.massPorCheq = massPorCheq;
	}
	public boolean isOptVenc() {
		return optVenc;
	}
	public void setOptVenc(boolean optVenc) {
		this.optVenc = optVenc;
	}
	public boolean isChkH2HSantander() {
		return chkH2HSantander;
	}
	public void setChkH2HSantander(boolean chkH2HSantander) {
		this.chkH2HSantander = chkH2HSantander;
	}
	public boolean isNomina() {
		return nomina;
	}
	public void setNomina(boolean nomina) {
		this.nomina = nomina;
	}
	public boolean isChkConvenioSant() {
		return chkConvenioSant;
	}
	public void setChkConvenioSant(boolean chkConvenioSant) {
		this.chkConvenioSant = chkConvenioSant;
	}
}
