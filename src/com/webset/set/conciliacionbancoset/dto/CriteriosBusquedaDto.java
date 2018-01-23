package com.webset.set.conciliacionbancoset.dto;

import java.util.Date;

public class CriteriosBusquedaDto {
	private int noFolioDet;
	private int idBanco;
	private int noEmpresa;
	private int formaPago; 
	private int grupoIni;
	private int grupoFin;
	private double montoIni;
	private double montoFin;
	private double diferencia;
	private boolean contabiliza;
	private boolean ajuste;
	private Date fechaIni;
	private Date fechaFin;
	private String idChequera;
	private String estatus;
	private String cargoAbono;
	private String idGrupo;
	private String idRubro;
	private String descRubro;
	private String nomComputadora;
	private String aclaracion;
	private String cuentaContable;
	
	private int idSubGrupo;
    private int idSubSubGrupo;
    private int idRubroC;
    private int identificados;
    
    //agregado XGM 09/02/2017 - Para modificación por bloque
    private String division;
    private String beneficiario;
    
    //agregado EMS 12/06/2016 - Para concialición banca cobranza
    private String idDivisa;
    
    
	public double getDiferencia() {
		return diferencia;
	}
	public void setDiferencia(double diferencia) {
		this.diferencia = diferencia;
	}
	public String getAclaracion() {
		return aclaracion;
	}
	public void setAclaracion(String aclaracion) {
		this.aclaracion = aclaracion;
	}
	public String getNomComputadora() {
		return nomComputadora;
	}
	public void setNomComputadora(String nomComputadora) {
		this.nomComputadora = nomComputadora;
	}
	public String getDescRubro() {
		return descRubro;
	}
	public void setDescRubro(String descRubro) {
		this.descRubro = descRubro;
	}
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public int getFormaPago() {
		return formaPago;
	}
	public void setFormaPago(int formaPago) {
		this.formaPago = formaPago;
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
	public Date getFechaIni() {
		return fechaIni;
	}
	public void setFechaIni(Date fechaIni) {
		this.fechaIni = fechaIni;
	}
	public Date getFechaFin() {
		return fechaFin;
	}
	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public String getEstatus() {
		return estatus;
	}
	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}
	public String getCargoAbono() {
		return cargoAbono;
	}
	public void setCargoAbono(String cargoAbono) {
		this.cargoAbono = cargoAbono;
	}
	public String getIdGrupo() {
		return idGrupo;
	}
	public void setIdGrupo(String idGrupo) {
		this.idGrupo = idGrupo;
	}
	public String getIdRubro() {
		return idRubro;
	}
	public void setIdRubro(String idRubro) {
		this.idRubro = idRubro;
	}
	public boolean isContabiliza() {
		return contabiliza;
	}
	public void setContabiliza(boolean contabiliza) {
		this.contabiliza = contabiliza;
	}
	public boolean isAjuste() {
		return ajuste;
	}
	public void setAjuste(boolean ajuste) {
		this.ajuste = ajuste;
	}
	public int getGrupoIni() {
		return grupoIni;
	}
	public void setGrupoIni(int grupoIni) {
		this.grupoIni = grupoIni;
	}
	public int getGrupoFin() {
		return grupoFin;
	}
	public void setGrupoFin(int grupoFin) {
		this.grupoFin = grupoFin;
	}
	public int getNoFolioDet() {
		return noFolioDet;
	}
	public void setNoFolioDet(int noFolioDet) {
		this.noFolioDet = noFolioDet;
	}
	public String getCuentaContable() {
		return cuentaContable;
	}
	public void setCuentaContable(String cuentaContable) {
		this.cuentaContable = cuentaContable;
	}
	public int getIdSubGrupo() {
		return idSubGrupo;
	}
	public void setIdSubGrupo(int idSubGrupo) {
		this.idSubGrupo = idSubGrupo;
	}
	public int getIdSubSubGrupo() {
		return idSubSubGrupo;
	}
	public void setIdSubSubGrupo(int idSubSubGrupo) {
		this.idSubSubGrupo = idSubSubGrupo;
	}
	public int getIdRubroC() {
		return idRubroC;
	}
	public void setIdRubroC(int idRubroC) {
		this.idRubroC = idRubroC;
	}
	public int getIdentificados() {
		return identificados;
	}
	public void setIdentificados(int identificados) {
		this.identificados = identificados;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public String getDivision() {
		return division;
	}
	public void setDivision(String division) {
		this.division = division;
	}
	public String getBeneficiario() {
		return beneficiario;
	}
	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}
	
}
