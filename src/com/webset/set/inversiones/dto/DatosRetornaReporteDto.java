package com.webset.set.inversiones.dto;

import java.util.Date;
/**
 * Esta clase contiene los campos en comun que contienen los reportes
 * de la forma ReportesDeInversiones.js
 * @author Crar
 *
 */
public class DatosRetornaReporteDto {
	
	private int noEmpresa;
	private	int noCuenta;
	private	int noInstitucion;
	private	int noContacto;
	private	int hora;
	private	int minuto;
	private	int plazo;
	private	int usuarioAlta;
	private	int usuarioModif;
	private	int diasAnual;
	private	int noContactoV;
	private	int horaV;
	private	int minutoV;
	private	int idBanco;
	private	int idBancoReg;
	private	int noOrdenRef;
	private	int noAvisoRef;
	private	int grupoInversion;
	private int contrato;
	
	private	double	importe;
	private	double	isr;
	private	double interes;
	private	double	ajusteInt;
	private	double	ajusteIsr;
	private	double	tasaIsr;
	private	double tasa;
	private	double importeTraspaso;
	private	double traspasoEjecutado;
	private	double tasaCurva28;
	private double total;
	private double remanente;
	private boolean nuevoInversion;
	
	private	String noOrden;
	private	String idPapel;
	private	String idTipoValor;
	private	String idEstatusOrd;
	private	String nota;
	private	String idChequera;
	private	String idChequeraReg;
	private	String bGarantia;
	private	String bAutoriza;
	private	String tipoOrden;
	private String idDivisa;
	private String noOrdenNva;
	private String descEstatus;
	private String razonSocial;
	private String contacto;
	private String descBanco;
	private String sHora;
	private String tipoChequera;
	
	private	Date fecVenc;
	private	Date fecAlta;
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
	public int getPlazo() {
		return plazo;
	}
	public void setPlazo(int plazo) {
		this.plazo = plazo;
	}
	public int getUsuarioAlta() {
		return usuarioAlta;
	}
	public void setUsuarioAlta(int usuarioAlta) {
		this.usuarioAlta = usuarioAlta;
	}
	public int getUsuarioModif() {
		return usuarioModif;
	}
	public void setUsuarioModif(int usuarioModif) {
		this.usuarioModif = usuarioModif;
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
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public int getIdBancoReg() {
		return idBancoReg;
	}
	public void setIdBancoReg(int idBancoReg) {
		this.idBancoReg = idBancoReg;
	}
	public int getNoOrdenRef() {
		return noOrdenRef;
	}
	public void setNoOrdenRef(int noOrdenRef) {
		this.noOrdenRef = noOrdenRef;
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
	public int getContrato() {
		return contrato;
	}
	public void setContrato(int contrato) {
		this.contrato = contrato;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
	public double getIsr() {
		return isr;
	}
	public void setIsr(double isr) {
		this.isr = isr;
	}
	public double getInteres() {
		return interes;
	}
	public void setInteres(double interes) {
		this.interes = interes;
	}
	public double getAjusteInt() {
		return ajusteInt;
	}
	public void setAjusteInt(double ajusteInt) {
		this.ajusteInt = ajusteInt;
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
	public double getTasaCurva28() {
		return tasaCurva28;
	}
	public void setTasaCurva28(double tasaCurva28) {
		this.tasaCurva28 = tasaCurva28;
	}
	public boolean isNuevoInversion() {
		return nuevoInversion;
	}
	public void setNuevoInversion(boolean nuevoInversion) {
		this.nuevoInversion = nuevoInversion;
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
	public String getIdEstatusOrd() {
		return idEstatusOrd;
	}
	public void setIdEstatusOrd(String idEstatusOrd) {
		this.idEstatusOrd = idEstatusOrd;
	}
	public String getNota() {
		return nota;
	}
	public void setNota(String nota) {
		this.nota = nota;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
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
	public String getBAutoriza() {
		return bAutoriza;
	}
	public void setBAutoriza(String autoriza) {
		bAutoriza = autoriza;
	}
	public String getTipoOrden() {
		return tipoOrden;
	}
	public void setTipoOrden(String tipoOrden) {
		this.tipoOrden = tipoOrden;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public String getNoOrdenNva() {
		return noOrdenNva;
	}
	public void setNoOrdenNva(String noOrdenNva) {
		this.noOrdenNva = noOrdenNva;
	}
	public Date getFecVenc() {
		return fecVenc;
	}
	public void setFecVenc(Date fecVenc) {
		this.fecVenc = fecVenc;
	}
	public Date getFecAlta() {
		return fecAlta;
	}
	public void setFecAlta(Date fecAlta) {
		this.fecAlta = fecAlta;
	}
	public String getDescEstatus() {
		return descEstatus;
	}
	public void setDescEstatus(String descEstatus) {
		this.descEstatus = descEstatus;
	}
	public String getRazonSocial() {
		return razonSocial;
	}
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	public String getContacto() {
		return contacto;
	}
	public void setContacto(String contacto) {
		this.contacto = contacto;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public String getDescBanco() {
		return descBanco;
	}
	public void setDescBanco(String descBanco) {
		this.descBanco = descBanco;
	}
	public String getSHora() {
		return sHora;
	}
	public void setSHora(String hora) {
		sHora = hora;
	}
	public String getTipoChequera() {
		return tipoChequera;
	}
	public void setTipoChequera(String tipoChequera) {
		this.tipoChequera = tipoChequera;
	}
	public double getRemanente() {
		return remanente;
	}
	public void setRemanente(double remanente) {
		this.remanente = remanente;
	}
	
	
	
	
}
