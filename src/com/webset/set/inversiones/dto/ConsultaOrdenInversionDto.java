package com.webset.set.inversiones.dto;

import java.util.Date;

/**
 * Este dto contiene campos de la tabla orden_inversion,
 * movimiento, persona
 * @author cristian 
 *
 */
public class ConsultaOrdenInversionDto {
	
	private int noEmpresa;
	private	String noOrden;
	private	String idPapel;
	private	String idTipoValor;
	private	int noCuenta;
	private	int noInstitucion;
	private	int noContacto;
	private	Date fecVenc;
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
	private	int noOrdenRef;
	private	String bAutoriza;
	private	int noAvisoRef;
	private	int grupoInversion;
	private	String tipoOrden;
	private String idDivisa;
	
	//campos de persona
	private String nombreCorto;
	private String nombreCompleto;
	
	//campos de cat_tipo_valor
	private String descTipoValor;
	
	//campos de movimiento
	private int noFolioDet;
	private int idTipoOperacion;
	private int idFormaPago;
	private int loteEntrada;
	private int noFolioRef;
	private String origenMov;
	private String idTipoMovto;
	private String bSalvoBuenCobro;
	private String idEstatusMov;
	private String bEntregado;
	
	private String nomEmpresa;
	private	int idBancoInst;
	private	String idChequeraInst;
	
	private String sFecAlta;
	private String sFecVenc;
	
	private Double neto;
	
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
	public int getNoOrdenRef() {
		return noOrdenRef;
	}
	public void setNoOrdenRef(int noOrdenRef) {
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
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public String getNombreCorto() {
		return nombreCorto;
	}
	public void setNombreCorto(String nombreCorto) {
		this.nombreCorto = nombreCorto;
	}
	public String getNombreCompleto() {
		return nombreCompleto;
	}
	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}
	public String getDescTipoValor() {
		return descTipoValor;
	}
	public void setDescTipoValor(String descTipoValor) {
		this.descTipoValor = descTipoValor;
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
	public int getIdFormaPago() {
		return idFormaPago;
	}
	public void setIdFormaPago(int idFormaPago) {
		this.idFormaPago = idFormaPago;
	}
	public int getLoteEntrada() {
		return loteEntrada;
	}
	public void setLoteEntrada(int loteEntrada) {
		this.loteEntrada = loteEntrada;
	}
	public String getOrigenMov() {
		return origenMov;
	}
	public void setOrigenMov(String origenMov) {
		this.origenMov = origenMov;
	}
	public String getIdTipoMovto() {
		return idTipoMovto;
	}
	public void setIdTipoMovto(String idTipoMovto) {
		this.idTipoMovto = idTipoMovto;
	}
	public String getBSalvoBuenCobro() {
		return bSalvoBuenCobro;
	}
	public void setBSalvoBuenCobro(String salvoBuenCobro) {
		bSalvoBuenCobro = salvoBuenCobro;
	}
	public String getIdEstatusMov() {
		return idEstatusMov;
	}
	public void setIdEstatusMov(String idEstatusMov) {
		this.idEstatusMov = idEstatusMov;
	}
	public String getBEntregado() {
		return bEntregado;
	}
	public void setBEntregado(String entregado) {
		bEntregado = entregado;
	}
	public int getNoFolioRef() {
		return noFolioRef;
	}
	public void setNoFolioRef(int noFolioRef) {
		this.noFolioRef = noFolioRef;
	}
	public String getbGarantia() {
		return bGarantia;
	}
	public void setbGarantia(String bGarantia) {
		this.bGarantia = bGarantia;
	}
	public String getbAutoriza() {
		return bAutoriza;
	}
	public void setbAutoriza(String bAutoriza) {
		this.bAutoriza = bAutoriza;
	}
	public String getbSalvoBuenCobro() {
		return bSalvoBuenCobro;
	}
	public void setbSalvoBuenCobro(String bSalvoBuenCobro) {
		this.bSalvoBuenCobro = bSalvoBuenCobro;
	}
	public String getbEntregado() {
		return bEntregado;
	}
	public void setbEntregado(String bEntregado) {
		this.bEntregado = bEntregado;
	}
	public String getNomEmpresa() {
		return nomEmpresa;
	}
	public void setNomEmpresa(String nomEmpresa) {
		this.nomEmpresa = nomEmpresa;
	}
	public int getIdBancoInst() {
		return idBancoInst;
	}
	public void setIdBancoInst(int idBancoInst) {
		this.idBancoInst = idBancoInst;
	}
	public String getIdChequeraInst() {
		return idChequeraInst;
	}
	public void setIdChequeraInst(String idChequeraInst) {
		this.idChequeraInst = idChequeraInst;
	}
	public String getsFecAlta() {
		return sFecAlta;
	}
	public void setsFecAlta(String sFecAlta) {
		this.sFecAlta = sFecAlta;
	}
	public String getsFecVenc() {
		return sFecVenc;
	}
	public void setsFecVenc(String sFecVenc) {
		this.sFecVenc = sFecVenc;
	}
	public Double getNeto() {
		return neto;
	}
	public void setNeto(Double neto) {
		this.neto = neto;
	}

}
