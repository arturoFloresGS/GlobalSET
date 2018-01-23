package com.webset.set.utilerias.dto;

import java.util.Date;

public class SaldosChequerasDto {
	
	private boolean bPorEmpresa; 
	private int idEmpresa;
	private boolean bPorGrupo;
	private int idGrupo;
	private int idUsuario;
	private boolean bPorDivisa;
	private String idDivisa;
	private boolean  bporFormaPago;
	private int formaPago;
	private Date fecha;
	private String psDivision;
	private double saldoInicial;
	private double cargo;
	private double abono;
	private double saldoFinal;
	private double tipoCambio;
	private String idChequera;
	
	private double saldoInicialMn;
	private double cargoMn;
	private double abonoMn;
	private double saldoFinalMn;
	private double inversionesMn;
	
	private double saldoInicialDls;
	private double cargoDls;
	private double abonoDls;
	private double saldoFinalDls;
	private double inversionesDls;
	
	public boolean isBPorEmpresa() {
		return bPorEmpresa;
	}
	public void setBPorEmpresa(boolean porEmpresa) {
		bPorEmpresa = porEmpresa;
	}
	public int getIdEmpresa() {
		return idEmpresa;
	}
	public void setIdEmpresa(int idEmpresa) {
		this.idEmpresa = idEmpresa;
	}
	public boolean isBPorGrupo() {
		return bPorGrupo;
	}
	public void setBPorGrupo(boolean porGrupo) {
		bPorGrupo = porGrupo;
	}
	public int getIdGrupo() {
		return idGrupo;
	}
	public void setIdGrupo(int idGrupo) {
		this.idGrupo = idGrupo;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public boolean isBPorDivisa() {
		return bPorDivisa;
	}
	public void setBPorDivisa(boolean porDivisa) {
		bPorDivisa = porDivisa;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public boolean isBporFormaPago() {
		return bporFormaPago;
	}
	public void setBporFormaPago(boolean bporFormaPago) {
		this.bporFormaPago = bporFormaPago;
	}
	public int getFormaPago() {
		return formaPago;
	}
	public void setFormaPago(int formaPago) {
		this.formaPago = formaPago;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getPsDivision() {
		return psDivision;
	}
	public void setPsDivision(String psDivision) {
		this.psDivision = psDivision;
	}
	public double getSaldoInicial() {
		return saldoInicial;
	}
	public void setSaldoInicial(double saldoInicial) {
		this.saldoInicial = saldoInicial;
	}
	public double getCargo() {
		return cargo;
	}
	public void setCargo(double cargo) {
		this.cargo = cargo;
	}
	public double getAbono() {
		return abono;
	}
	public void setAbono(double abono) {
		this.abono = abono;
	}
	public double getSaldoFinal() {
		return saldoFinal;
	}
	public void setSaldoFinal(double saldoFinal) {
		this.saldoFinal = saldoFinal;
	}
	public double getTipoCambio() {
		return tipoCambio;
	}
	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public double getSaldoInicialMn() {
		return saldoInicialMn;
	}
	public void setSaldoInicialMn(double saldoInicialMn) {
		this.saldoInicialMn = saldoInicialMn;
	}
	public double getCargoMn() {
		return cargoMn;
	}
	public void setCargoMn(double cargoMn) {
		this.cargoMn = cargoMn;
	}
	public double getAbonoMn() {
		return abonoMn;
	}
	public void setAbonoMn(double abonoMn) {
		this.abonoMn = abonoMn;
	}
	public double getSaldoFinalMn() {
		return saldoFinalMn;
	}
	public void setSaldoFinalMn(double saldoFinalMn) {
		this.saldoFinalMn = saldoFinalMn;
	}
	public double getInversionesMn() {
		return inversionesMn;
	}
	public void setInversionesMn(double inversionesMn) {
		this.inversionesMn = inversionesMn;
	}
	public double getSaldoInicialDls() {
		return saldoInicialDls;
	}
	public void setSaldoInicialDls(double saldoInicialDls) {
		this.saldoInicialDls = saldoInicialDls;
	}
	public double getCargoDls() {
		return cargoDls;
	}
	public void setCargoDls(double cargoDls) {
		this.cargoDls = cargoDls;
	}
	public double getAbonoDls() {
		return abonoDls;
	}
	public void setAbonoDls(double abonoDls) {
		this.abonoDls = abonoDls;
	}
	public double getSaldoFinalDls() {
		return saldoFinalDls;
	}
	public void setSaldoFinalDls(double saldoFinalDls) {
		this.saldoFinalDls = saldoFinalDls;
	}
	public double getInversionesDls() {
		return inversionesDls;
	}
	public void setInversionesDls(double inversionesDls) {
		this.inversionesDls = inversionesDls;
	}

}
