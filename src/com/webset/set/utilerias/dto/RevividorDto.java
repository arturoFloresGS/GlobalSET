package com.webset.set.utilerias.dto;


public class RevividorDto {

	private int noFolioDet; 
	private int idTipoOperacion;
	private int idFormaPago;
	private double importe;
	private int noEmpresa; 
	private int noCuenta; 
	private int idBanco;
	private int idUsuario; 
	private int noDocto; 
	private int lote;
	private int noFolioRef;//Este atributo no se utiliza directamente para el revividor
	private String fecConfTrans; 
	private String idDivisa; 
	private boolean pbAutomatico;
	private String idTipoMovto;
	private String idEstatusMov;
	private String psRevividor;
	private String psTipoCancelacion;
	private String psOrigenMov;
	private String bEntregado;
	private String idChequera;
	private String bSalvoBuenCobro;
	private String psResultado;
	private String nomForma;
	private String observaciones; //para ingresar el observaciones al movimiento
	private int ejercicio;
	private String poHeaders;
	private String noDocumento;
	
	private String peridoDescompensar;
	private String fecPropuestaStr;
	private String origen;
	
	public String getNoDocumento() {
		return noDocumento;
	}
	public void setNoDocumento(String noDocumento) {
		this.noDocumento = noDocumento;
	}
	public String getPoHeaders() {
		return poHeaders;
	}
	public void setPoHeaders(String poHeaders) {
		this.poHeaders = poHeaders;
	}
	public int getEjercicio() {
		return ejercicio;
	}
	public void setEjercicio(int ejercicio) {
		this.ejercicio = ejercicio;
	}
	public int getNoFolioDet() {
		return noFolioDet;
	}
	public void setNoFolioDet(int noFolioDet) {
		this.noFolioDet = noFolioDet;
	}
	public int getIdTipoOperacion() {
		return idTipoOperacion;
	}
	public void setIdTipoOperacion(int idTipoOperacion) {
		this.idTipoOperacion = idTipoOperacion;
	}
	public String getIdEstatusMov() {
		return idEstatusMov;
	}
	public void setIdEstatusMov(String idEstatusMov) {
		this.idEstatusMov = idEstatusMov;
	}
	public int getIdFormaPago() {
		return idFormaPago;
	}
	public void setIdFormaPago(int idFormaPago) {
		this.idFormaPago = idFormaPago;
	}
	public String getIdTipoMovto() {
		return idTipoMovto;
	}
	public void setIdTipoMovto(String idTipoMovto) {
		this.idTipoMovto = idTipoMovto;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public int getNoCuenta() {
		return noCuenta;
	}
	public void setNoCuenta(int noCuenta) {
		this.noCuenta = noCuenta;
	}
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public int getNoDocto() {
		return noDocto;
	}
	public void setNoDocto(int noDocto) {
		this.noDocto = noDocto;
	}
	public int getLote() {
		return lote;
	}
	public void setLote(int lote) {
		this.lote = lote;
	}
	public String getFecConfTrans() {
		return fecConfTrans;
	}
	public void setFecConfTrans(String fecConfTrans) {
		this.fecConfTrans = fecConfTrans;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public boolean isPbAutomatico() {
		return pbAutomatico;
	}
	public void setPbAutomatico(boolean pbAutomatico) {
		this.pbAutomatico = pbAutomatico;
	}
	public String getPsRevividor() {
		return psRevividor;
	}
	public void setPsRevividor(String psRevividor) {
		this.psRevividor = psRevividor;
	}
	public String getPsTipoCancelacion() {
		return psTipoCancelacion;
	}
	public void setPsTipoCancelacion(String psTipoCancelacion) {
		this.psTipoCancelacion = psTipoCancelacion;
	}
	public String getPsOrigenMov() {
		return psOrigenMov;
	}
	public void setPsOrigenMov(String psOrigenMov) {
		this.psOrigenMov = psOrigenMov;
	}
	public String getBEntregado() {
		return bEntregado;
	}
	public void setBEntregado(String entregado) {
		bEntregado = entregado;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public String getBSalvoBuenCobro() {
		return bSalvoBuenCobro;
	}
	public void setBSalvoBuenCobro(String salvoBuenCobro) {
		bSalvoBuenCobro = salvoBuenCobro;
	}
	public String getPsResultado() {
		return psResultado;
	}
	public void setPsResultado(String psResultado) {
		this.psResultado = psResultado;
	}
	public String getNomForma() {
		return nomForma;
	}
	public void setNomForma(String nomForma) {
		this.nomForma = nomForma;
	}
	public int getNoFolioRef() {
		return noFolioRef;
	}
	public void setNoFolioRef(int noFolioRef) {
		this.noFolioRef = noFolioRef;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	public String getPeridoDescompensar() {
		return peridoDescompensar;
	}
	public void setPeridoDescompensar(String peridoDescompensar) {
		this.peridoDescompensar = peridoDescompensar;
	}
	public String getFecPropuestaStr() {
		return fecPropuestaStr;
	}
	public void setFecPropuestaStr(String fecPropuestaStr) {
		this.fecPropuestaStr = fecPropuestaStr;
	}
	public String getOrigen() {
		return origen;
	}
	public void setOrigen(String origen) {
		this.origen = origen;
	}
	
}
