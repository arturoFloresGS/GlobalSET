package com.webset.set.bancaelectronica.dto;

public class ParamDeterminaCorrespondeDto {
	
	
	private int noEmpresa;
	private int idBanco;
	private int longReferencia;
	private int orden;
    private int longitud;
    private int valor;
    private int ordenEmpresa;
    private int longEmpresa;
	private String sReferencia;
    private String idChequera;
    private String[][] masReferencia; 
    private String sCaja;
    private String sCodigo;
    private String sSubCodigo;
    private String idCorresponde;
    private String baseCalculo;    
    private String campo;    
    private String bCambiaOrigen;
    private String bCambiaDestino;
    private String destinoEmpresa;
    
	public String getSReferencia() {
		return sReferencia;
	}
	public void setSReferencia(String referencia) {
		sReferencia = referencia;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}

	public String getSCaja() {
		return sCaja;
	}
	public void setSCaja(String caja) {
		sCaja = caja;
	}
	public String getSCodigo() {
		return sCodigo;
	}
	public void setSCodigo(String codigo) {
		sCodigo = codigo;
	}
	public String getSSubCodigo() {
		return sSubCodigo;
	}
	public void setSSubCodigo(String subCodigo) {
		sSubCodigo = subCodigo;
	}
	public int getLongReferencia() {
		return longReferencia;
	}
	public void setLongReferencia(int longReferencia) {
		this.longReferencia = longReferencia;
	}
	public String getIdCorresponde() {
		return idCorresponde;
	}
	public void setIdCorresponde(String idCorresponde) {
		this.idCorresponde = idCorresponde;
	}
	public String getBaseCalculo() {
		return baseCalculo;
	}
	public void setBaseCalculo(String baseCalculo) {
		this.baseCalculo = baseCalculo;
	}
	public int getOrden() {
		return orden;
	}
	public void setOrden(int orden) {
		this.orden = orden;
	}
	public int getLongitud() {
		return longitud;
	}
	public void setLongitud(int longitud) {
		this.longitud = longitud;
	}
	public String getCampo() {
		return campo;
	}
	public void setCampo(String campo) {
		this.campo = campo;
	}
	public int getValor() {
		return valor;
	}
	public void setValor(int valor) {
		this.valor = valor;
	}
	public int getOrdenEmpresa() {
		return ordenEmpresa;
	}
	public void setOrdenEmpresa(int ordenEmpresa) {
		this.ordenEmpresa = ordenEmpresa;
	}
	public String getBCambiaOrigen() {
		return bCambiaOrigen;
	}
	public void setBCambiaOrigen(String cambiaOrigen) {
		bCambiaOrigen = cambiaOrigen;
	}
	public String getBCambiaDestino() {
		return bCambiaDestino;
	}
	public void setBCambiaDestino(String cambiaDestino) {
		bCambiaDestino = cambiaDestino;
	}
	public int getLongEmpresa() {
		return longEmpresa;
	}
	public void setLongEmpresa(int longEmpresa) {
		this.longEmpresa = longEmpresa;
	}
	public String getDestinoEmpresa() {
		return destinoEmpresa;
	}
	public void setDestinoEmpresa(String destinoEmpresa) {
		this.destinoEmpresa = destinoEmpresa;
	}
	public String[][] getMasReferencia() {
		return masReferencia;
	}
	public void setMasReferencia(String[][] masReferencia) {
		this.masReferencia = masReferencia;
	}

}
