package com.webset.set.bancaelectronica.dto;

import java.util.Date;

/**
 * 
 * @author Cristian Garcia Garcia
 * @since 21/Octubre/2010
 * @see <p>Tabla det_arch_transfer</p>
 *
 */
public class DetArchTransferDto {

	private int noFolioDet;
	private int idBanco;
	private int idBancoBenef;
	private int sucursal;
	private int plaza;
	
	private double importe;
	
	private String nomArch;
	private String noDocto;
	private String idEstatusArch;
	private String fecValor;
	private String idChequera;
	private String idChequeraBenef;
	private String prefijoBenef;
	private String beneficiario;
	private String concepto;
	private String tipoEnvioLayout;
	
	private Date fecValorDate;
	
	//setters & getters
	public String getNomArch() {
		return nomArch;
	}
	public void setNomArch(String nomArch) {
		this.nomArch = nomArch;
	}
	public int getNoFolioDet() {
		return noFolioDet;
	}
	public void setNoFolioDet(int noFolioDet) {
		this.noFolioDet = noFolioDet;
	}
	public String getNoDocto() {
		return noDocto;
	}
	public void setNoDocto(String noDocto) {
		this.noDocto = noDocto;
	}
	public String getIdEstatusArch() {
		return idEstatusArch;
	}
	public void setIdEstatusArch(String idEstatusArch) {
		this.idEstatusArch = idEstatusArch;
	}
	public String getFecValor() {
		return fecValor;
	}
	public void setFecValor(String fecValor) {
		this.fecValor = fecValor;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public int getIdBancoBenef() {
		return idBancoBenef;
	}
	public void setIdBancoBenef(int idBancoBenef) {
		this.idBancoBenef = idBancoBenef;
	}
	public String getIdChequeraBenef() {
		return idChequeraBenef;
	}
	public void setIdChequeraBenef(String idChequeraBenef) {
		this.idChequeraBenef = idChequeraBenef;
	}
	public String getPrefijoBenef() {
		return prefijoBenef;
	}
	public void setPrefijoBenef(String prefijoBenef) {
		this.prefijoBenef = prefijoBenef;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
	public String getBeneficiario() {
		return beneficiario;
	}
	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}
	public int getSucursal() {
		return sucursal;
	}
	public void setSucursal(int sucursal) {
		this.sucursal = sucursal;
	}
	public int getPlaza() {
		return plaza;
	}
	public void setPlaza(int plaza) {
		this.plaza = plaza;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public Date getFecValorDate() {
		return fecValorDate;
	}
	public void setFecValorDate(Date fecValorDate) {
		this.fecValorDate = fecValorDate;
	}
	public String getTipoEnvioLayout() {
		return tipoEnvioLayout;
	}
	public void setTipoEnvioLayout(String tipoEnvioLayout) {
		this.tipoEnvioLayout = tipoEnvioLayout;
	}

}
