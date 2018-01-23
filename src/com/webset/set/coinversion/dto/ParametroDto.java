package com.webset.set.coinversion.dto;

import java.util.Date;

public class ParametroDto {
	
	private int noEmpresa; 
	private int noFolioParam;
	private int aplica; 
	private int secuencia;
	private int idTipoOperacion; 
	private int cuenta;
	private int idBanco;
	private int noUsuario;
	private int idFormaPago;
	private int idBancoBenef;
	private int idCaja; 
	private int noFolioMov;
	private int folioRef; 
	private int idGrupo;
	private int noDocto;
	private int noLinea;
	private int idRubro;
	private int idUsuario;
	private int noFactura;
	private int noCliente; 
	private int diasInv;
	private int tipo_saldo;
	
	private double importe;
	private double tipoCambio;
	private double importeOriginal;
	private double valorTasa;
	private double montoSobregiro;
	
	private String idEstatusMov; 
	private String idChequera;
	private String bSBC; 
	private Date fecValor;
	private Date fecValorOriginal;
	private Date fecValorAlta;
	private Date fecOperacion;
	private String idEstatusReg; 
	private String idDivisa;
	private String idChequeraBenef;
	private String origenMov; 
	private String concepto;
	private String beneficiario;
	
	
	
	public int getTipo_saldo() {
		return tipo_saldo;
	}
	public void setTipo_saldo(int tipo_saldo) {
		this.tipo_saldo = tipo_saldo;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public int getNoFolioParam() {
		return noFolioParam;
	}
	public void setNoFolioParam(int noFolioParam) {
		this.noFolioParam = noFolioParam;
	}
	public int getAplica() {
		return aplica;
	}
	public void setAplica(int aplica) {
		this.aplica = aplica;
	}
	public int getSecuencia() {
		return secuencia;
	}
	public void setSecuencia(int secuencia) {
		this.secuencia = secuencia;
	}
	public int getIdTipoOperacion() {
		return idTipoOperacion;
	}
	public void setIdTipoOperacion(int idTipoOperacion) {
		this.idTipoOperacion = idTipoOperacion;
	}
	public int getCuenta() {
		return cuenta;
	}
	public void setCuenta(int cuenta) {
		this.cuenta = cuenta;
	}
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public int getNoUsuario() {
		return noUsuario;
	}
	public void setNoUsuario(int noUsuario) {
		this.noUsuario = noUsuario;
	}
	public int getIdFormaPago() {
		return idFormaPago;
	}
	public void setIdFormaPago(int idFormaPago) {
		this.idFormaPago = idFormaPago;
	}
	public int getIdBancoBenef() {
		return idBancoBenef;
	}
	public void setIdBancoBenef(int idBancoBenef) {
		this.idBancoBenef = idBancoBenef;
	}
	public int getIdCaja() {
		return idCaja;
	}
	public void setIdCaja(int idCaja) {
		this.idCaja = idCaja;
	}
	public int getNoFolioMov() {
		return noFolioMov;
	}
	public void setNoFolioMov(int noFolioMov) {
		this.noFolioMov = noFolioMov;
	}
	public int getFolioRef() {
		return folioRef;
	}
	public void setFolioRef(int folioRef) {
		this.folioRef = folioRef;
	}
	public int getIdGrupo() {
		return idGrupo;
	}
	public void setIdGrupo(int idGrupo) {
		this.idGrupo = idGrupo;
	}
	public int getNoDocto() {
		return noDocto;
	}
	public void setNoDocto(int noDocto) {
		this.noDocto = noDocto;
	}
	public int getNoLinea() {
		return noLinea;
	}
	public void setNoLinea(int noLinea) {
		this.noLinea = noLinea;
	}
	public int getIdRubro() {
		return idRubro;
	}
	public void setIdRubro(int idRubro) {
		this.idRubro = idRubro;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
	public double getTipoCambio() {
		return tipoCambio;
	}
	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}
	public String getIdEstatusMov() {
		return idEstatusMov;
	}
	public void setIdEstatusMov(String idEstatusMov) {
		this.idEstatusMov = idEstatusMov;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public String getBSBC() {
		return bSBC;
	}
	public void setBSBC(String bsbc) {
		bSBC = bsbc;
	}
	public Date getFecValor() {
		return fecValor;
	}
	public void setFecValor(Date fecValor) {
		this.fecValor = fecValor;
	}
	public String getIdEstatusReg() {
		return idEstatusReg;
	}
	public void setIdEstatusReg(String idEstatusReg) {
		this.idEstatusReg = idEstatusReg;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public String getIdChequeraBenef() {
		return idChequeraBenef;
	}
	public void setIdChequeraBenef(String idChequeraBenef) {
		this.idChequeraBenef = idChequeraBenef;
	}
	public String getOrigenMov() {
		return origenMov;
	}
	public void setOrigenMov(String origenMov) {
		this.origenMov = origenMov;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public String getBeneficiario() {
		return beneficiario;
	}
	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public int getNoFactura() {
		return noFactura;
	}
	public void setNoFactura(int noFactura) {
		this.noFactura = noFactura;
	}
	public int getNoCliente() {
		return noCliente;
	}
	public void setNoCliente(int noCliente) {
		this.noCliente = noCliente;
	}
	public Date getFecValorOriginal() {
		return fecValorOriginal;
	}
	public void setFecValorOriginal(Date fecValorOriginal) {
		this.fecValorOriginal = fecValorOriginal;
	}
	public Date getFecValorAlta() {
		return fecValorAlta;
	}
	public void setFecValorAlta(Date fecValorAlta) {
		this.fecValorAlta = fecValorAlta;
	}
	public Date getFecOperacion() {
		return fecOperacion;
	}
	public void setFecOperacion(Date fecOperacion) {
		this.fecOperacion = fecOperacion;
	}
	public double getImporteOriginal() {
		return importeOriginal;
	}
	public void setImporteOriginal(double importeOriginal) {
		this.importeOriginal = importeOriginal;
	}
	public double getValorTasa() {
		return valorTasa;
	}
	public void setValorTasa(double valorTasa) {
		this.valorTasa = valorTasa;
	}
	public int getDiasInv() {
		return diasInv;
	}
	public void setDiasInv(int diasInv) {
		this.diasInv = diasInv;
	}
	public double getMontoSobregiro() {
		return montoSobregiro;
	}
	public void setMontoSobregiro(double montoSobregiro) {
		this.montoSobregiro = montoSobregiro;
	}
	
	
	
	

}
