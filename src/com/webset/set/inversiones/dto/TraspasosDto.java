package com.webset.set.inversiones.dto;

import java.util.Date;

public class TraspasosDto {
	
	private int numCuenta;
	private double importe;
	private int idBancoCargo;
	private String idChequeraCargo;
	private int idBancoRegreso;
	private String idChequeraRegreso;
	private int idBancoBenefLiq;
	private String idChequeraBenefLiq;
	private String grupo;
	private double tipoCambioActual;
	private String origenMovto;
	private int idEmpresa;
	private String beneficiario;
	private int idEmpresaBenef;
	private Date fecha;
	private boolean bInterna;
	private boolean bInvMismaEmpresa;
	private boolean bTraspasos;
	private int noFolioMov;
	private int noFolioDet;
	private int noFolioRef;
	private String idEstatusMov;
	private int secuencia;
	private String concepto;
	private int aplica;
	private int diasInv;
	private double valorTasa;
	private String noOrden;
	private String idDivisa;

	public String getNoOrden() {
		return noOrden;
	}
	public void setNoOrden(String noOrden) {
		this.noOrden = noOrden;
	}
	public boolean isBTraspasos() {
		return bTraspasos;
	}
	public void setBTraspasos(boolean traspasos) {
		bTraspasos = traspasos;
	}
	public boolean isBInterna() {
		return bInterna;
	}
	public void setBInterna(boolean interna) {
		bInterna = interna;
	}
	public int getNumCuenta() {
		return numCuenta;
	}
	public void setNumCuenta(int numCuenta) {
		this.numCuenta = numCuenta;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
	public int getIdBancoCargo() {
		return idBancoCargo;
	}
	public void setIdBancoCargo(int idBancoCargo) {
		this.idBancoCargo = idBancoCargo;
	}
	public String getIdChequeraCargo() {
		return idChequeraCargo;
	}
	public void setIdChequeraCargo(String idChequeraCargo) {
		this.idChequeraCargo = idChequeraCargo;
	}
	public int getIdBancoRegreso() {
		return idBancoRegreso;
	}
	public void setIdBancoRegreso(int idBancoRegreso) {
		this.idBancoRegreso = idBancoRegreso;
	}
	public String getIdChequeraRegreso() {
		return idChequeraRegreso;
	}
	public void setIdChequeraRegreso(String idChequeraRegreso) {
		this.idChequeraRegreso = idChequeraRegreso;
	}
	public String getGrupo() {
		return grupo;
	}
	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}
	public double getTipoCambioActual() {
		return tipoCambioActual;
	}
	public void setTipoCambioActual(double tipoCambioActual) {
		this.tipoCambioActual = tipoCambioActual;
	}
	public String getOrigenMovto() {
		return origenMovto;
	}
	public void setOrigenMovto(String origenMovto) {
		this.origenMovto = origenMovto;
	}
	public int getIdEmpresa() {
		return idEmpresa;
	}
	public void setIdEmpresa(int idEmpresa) {
		this.idEmpresa = idEmpresa;
	}
	public String getBeneficiario() {
		return beneficiario;
	}
	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}
	public int getIdEmpresaBenef() {
		return idEmpresaBenef;
	}
	public void setIdEmpresaBenef(int idEmpresaBenef) {
		this.idEmpresaBenef = idEmpresaBenef;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public boolean isBInvMismaEmpresa() {
		return bInvMismaEmpresa;
	}
	public void setBInvMismaEmpresa(boolean invMismaEmpresa) {
		bInvMismaEmpresa = invMismaEmpresa;
	}
	public int getIdBancoBenefLiq() {
		return idBancoBenefLiq;
	}
	public void setIdBancoBenefLiq(int idBancoBenefLiq) {
		this.idBancoBenefLiq = idBancoBenefLiq;
	}
	public String getIdChequeraBenefLiq() {
		return idChequeraBenefLiq;
	}
	public void setIdChequeBenefLiq(String idChequeBenefLiq) {
		this.idChequeraBenefLiq = idChequeBenefLiq;
	}
	public int getNoFolioMov() {
		return noFolioMov;
	}
	public void setNoFolioMov(int noFolioMov) {
		this.noFolioMov = noFolioMov;
	}
	public int getNoFolioDet() {
		return noFolioDet;
	}
	public void setNoFolioDet(int noFolioDet) {
		this.noFolioDet = noFolioDet;
	}
	public int getNoFolioRef() {
		return noFolioRef;
	}
	public void setNoFolioRef(int noFolioRef) {
		this.noFolioRef = noFolioRef;
	}
	public String getIdEstatusMov() {
		return idEstatusMov;
	}
	public void setIdEstatusMov(String idEstatusMov) {
		this.idEstatusMov = idEstatusMov;
	}
	public int getSecuencia() {
		return secuencia;
	}
	public void setSecuencia(int secuencia) {
		this.secuencia = secuencia;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public int getAplica() {
		return aplica;
	}
	public void setAplica(int aplica) {
		this.aplica = aplica;
	}
	public int getDiasInv() {
		return diasInv;
	}
	public void setDiasInv(int diasInv) {
		this.diasInv = diasInv;
	}
	public double getValorTasa() {
		return valorTasa;
	}
	public void setValorTasa(double valorTasa) {
		this.valorTasa = valorTasa;
	}
	public void setIdChequeraBenefLiq(String idChequeraBenefLiq) {
		this.idChequeraBenefLiq = idChequeraBenefLiq;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	

}
