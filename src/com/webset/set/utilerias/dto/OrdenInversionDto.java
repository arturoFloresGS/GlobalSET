package com.webset.set.utilerias.dto;

import java.util.Date;
/**
 * Este dto representa la tabla 
 * orden_inversion
 * @author Cristian García García
 */
public class OrdenInversionDto {
	
	private int noEmpresa;
	private	String noOrden;
	private	String idPapel;
	private	String idTipoValor;
	private	int noCuenta;
	private	int noInstitucion;
	private	int noContacto;
	private	Date fecVenc;
	private String mes;
	private int numMes;
	private int anio;
	private	int hora;
	private	int minuto;
	private	double	importe;
	private	int plazo;
	private	double	isr;
	private	int usuarioAlta;
	private	Date fecAlta;
	private	int usuarioModif;
	private	String idEstatusOrd;
	private	double interes;
	private	int diasAnual;
	private	int noContactoV;
	private	int horaV;
	private	int minutoV;
	private	double	ajusteInt;
	private	String nota;
	private	double	ajusteIsr;
	private	double	tasaIsr;
	private	double tasa;
	private	double importeTraspaso;
	private	double traspasoEjecutado;
	private	int idBanco;
	private	String idChequera;
	private	double tasaCurva28;
	private	int idBancoReg;
	private	String idChequeraReg;
	private	String bGarantia;
	private	String noOrdenRef;
	private	String bAutoriza;
	private	int noAvisoRef;
	private	int grupoInversion;
	private	String tipoOrden;
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public String getNoOrden() {
		return noOrden;
	}
	public void setNoOrden(String noOrden) {
		this.noOrden = noOrden;
	}
	public String getIdPapel() {
		return idPapel;
	}
	public void setIdPapel(String idPapel) {
		this.idPapel = idPapel;
	}
	public String getIdTipoValor() {
		return idTipoValor;
	}
	public void setIdTipoValor(String idTipoValor) {
		this.idTipoValor = idTipoValor;
	}
	public int getNoCuenta() {
		return noCuenta;
	}
	public void setNoCuenta(int nocuenta) {
		this.noCuenta = nocuenta;
	}
	public int getNoInstitucion() {
		return noInstitucion;
	}
	public void setNoInstitucion(int noInstitucion) {
		this.noInstitucion = noInstitucion;
	}
	public int getNoContacto() {
		return noContacto;
	}
	public void setNoContacto(int noContacto) {
		this.noContacto = noContacto;
	}
	public Date getFecVenc() {
		return fecVenc;
	}
	public void setFecVenc(Date fecVenc) {
		this.fecVenc = fecVenc;
	}
	public int getHora() {
		return hora;
	}
	public void setHora(int hora) {
		this.hora = hora;
	}
	public int getMinuto() {
		return minuto;
	}
	public void setMinuto(int minuto) {
		this.minuto = minuto;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
	public int getPlazo() {
		return plazo;
	}
	public void setPlazo(int plazo) {
		this.plazo = plazo;
	}
	public double getIsr() {
		return isr;
	}
	public void setIsr(double isr) {
		this.isr = isr;
	}
	public int getUsuarioAlta() {
		return usuarioAlta;
	}
	public void setUsuarioAlta(int usuarioAlta) {
		this.usuarioAlta = usuarioAlta;
	}
	public Date getFecAlta() {
		return fecAlta;
	}
	public void setFecAlta(Date fecAlta) {
		this.fecAlta = fecAlta;
	}
	public int getUsuarioModif() {
		return usuarioModif;
	}
	public void setUsuarioModif(int usuarioModif) {
		this.usuarioModif = usuarioModif;
	}
	public String getIdEstatusOrd() {
		return idEstatusOrd;
	}
	public void setIdEstatusOrd(String idEstatusOrd) {
		this.idEstatusOrd = idEstatusOrd;
	}
	public double getInteres() {
		return interes;
	}
	public void setInteres(double interes) {
		this.interes = interes;
	}
	public int getDiasAnual() {
		return diasAnual;
	}
	public void setDiasAnual(int diasAnual) {
		this.diasAnual = diasAnual;
	}
	public int getNoContactoV() {
		return noContactoV;
	}
	public void setNoContactoV(int noContactoV) {
		this.noContactoV = noContactoV;
	}
	public int getHoraV() {
		return horaV;
	}
	public void setHoraV(int horaV) {
		this.horaV = horaV;
	}
	public int getMinutoV() {
		return minutoV;
	}
	public void setMinutoV(int minutoV) {
		this.minutoV = minutoV;
	}
	public double getAjusteInt() {
		return ajusteInt;
	}
	public void setAjusteInt(double ajusteInt) {
		this.ajusteInt = ajusteInt;
	}
	public String getNota() {
		return nota;
	}
	public void setNota(String nota) {
		this.nota = nota;
	}
	public double getAjusteIsr() {
		return ajusteIsr;
	}
	public void setAjusteIsr(double ajusteIsr) {
		this.ajusteIsr = ajusteIsr;
	}
	public double getTasaIsr() {
		return tasaIsr;
	}
	public void setTasaIsr(double tasaIsr) {
		this.tasaIsr = tasaIsr;
	}
	public double getTasa() {
		return tasa;
	}
	public void setTasa(double tasa) {
		this.tasa = tasa;
	}
	public double getImporteTraspaso() {
		return importeTraspaso;
	}
	public void setImporteTraspaso(double importeTraspaso) {
		this.importeTraspaso = importeTraspaso;
	}
	public double getTraspasoEjecutado() {
		return traspasoEjecutado;
	}
	public void setTraspasoEjecutado(double traspasoEjecutado) {
		this.traspasoEjecutado = traspasoEjecutado;
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
	public double getTasaCurva28() {
		return tasaCurva28;
	}
	public void setTasaCurva28(double tasaCurva28) {
		this.tasaCurva28 = tasaCurva28;
	}
	public int getIdBancoReg() {
		return idBancoReg;
	}
	public void setIdBancoReg(int idBancoReg) {
		this.idBancoReg = idBancoReg;
	}
	public String getIdChequeraReg() {
		return idChequeraReg;
	}
	public void setIdChequeraReg(String idChequeraReg) {
		this.idChequeraReg = idChequeraReg;
	}
	public String getBGarantia() {
		return bGarantia;
	}
	public void setBGarantia(String garantia) {
		bGarantia = garantia;
	}
	public String getNoOrdenRef() {
		return noOrdenRef;
	}
	public void setNoOrdenRef(String noOrdenRef) {
		this.noOrdenRef = noOrdenRef;
	}
	public String getBAutoriza() {
		return bAutoriza;
	}
	public void setBAutoriza(String autoriza) {
		bAutoriza = autoriza;
	}
	public int getNoAvisoRef() {
		return noAvisoRef;
	}
	public void setNoAvisoRef(int noAvisoRef) {
		this.noAvisoRef = noAvisoRef;
	}
	public int getGrupoInversion() {
		return grupoInversion;
	}
	public void setGrupoInversion(int grupoInversion) {
		this.grupoInversion = grupoInversion;
	}
	public String getTipoOrden() {
		return tipoOrden;
	}
	public void setTipoOrden(String tipoOrden) {
		this.tipoOrden = tipoOrden;
	}
	public int getAnio() {
		return anio;
	}
	public void setAnio(int anio) {
		this.anio = anio;
	}
	public String getMes() {
		return mes;
	}
	public void setMes(String mes) {
		this.mes = mes;
	}
	public int getNumMes() {
		return numMes;
	}
	public void setNumMes(int numMes) {
		this.numMes = numMes;
	}
}
