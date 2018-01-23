package com.webset.set.impresion.dto;

import java.util.Date;

public class ConsultaChequesDto {
	private int criterioBanco;
	private int criterioEmpresa;
	private int criterioCajaPago;
	private int criterioLote;
	private int criterioCentroCostos;
	private int criterioSolicitudIni;
	private int criterioSolicitudFin;
	private int criterioDivision;
	private int idUsuario;
	private int idCaja;
	private String criterioBeneficiario;
	private int noEmpresa;
	private int numImpresora;
	private int bancoCharola;
	private int chequeNomina;
	private int motivo;
	private int noCheque;
	//folios de las cajas de texto de la ventana charolas
	private int folioInicial;
	private int folioFinal;
	private double criterioImporteIni;
	private double criterioImporteFin;
	private boolean optSeleccion;
	private boolean optPendientes;
	private boolean optPorConfirmar;
	private boolean optFirmados;
	private boolean chkNuevoFormato;
	private boolean chkNegociable;
	private boolean chkAbono;
	private boolean chkTodas;
	private Date criterioFechaIni;
	private Date criterioFechaFin;
	private Date fechaCheque;
	private String criterioNoDocto;
	private int criterioNoChequeIni;
	private int criterioNoChequeFin;
	private String psEquivalePersona;
	private String psbImpreContinua;
	private String criterioChequera;
	private String criterioTipoEgreso;
	private String criterioConcepto;
	private String criterioDivisa;
	private String criterioCveControl;
	
	public String getCriterioConcepto() {
		return criterioConcepto;
	}
	public void setCriterioConcepto(String criterioConcepto) {
		this.criterioConcepto = criterioConcepto;
	}
	public String getCriterioDivisa() {
		return criterioDivisa;
	}
	public void setCriterioDivisa(String criterioDivisa) {
		this.criterioDivisa = criterioDivisa;
	}
	public String getCriterioBeneficiario() {
		return criterioBeneficiario;
	}
	public void setCriterioBeneficiario(String criterioBeneficiario) {
		this.criterioBeneficiario = criterioBeneficiario;
	}
	public int getCriterioBanco() {
		return criterioBanco;
	}
	public void setCriterioBanco(int criterioBanco) {
		this.criterioBanco = criterioBanco;
	}
	public int getCriterioEmpresa() {
		return criterioEmpresa;
	}
	public void setCriterioEmpresa(int criterioEmpresa) {
		this.criterioEmpresa = criterioEmpresa;
	}
	public int getCriterioCajaPago() {
		return criterioCajaPago;
	}
	public void setCriterioCajaPago(int criterioCajaPago) {
		this.criterioCajaPago = criterioCajaPago;
	}
	public int getCriterioLote() {
		return criterioLote;
	}
	public void setCriterioLote(int criterioLote) {
		this.criterioLote = criterioLote;
	}
	public int getCriterioCentroCostos() {
		return criterioCentroCostos;
	}
	public void setCriterioCentroCostos(int criterioCentroCostos) {
		this.criterioCentroCostos = criterioCentroCostos;
	}
	public int getCriterioSolicitudIni() {
		return criterioSolicitudIni;
	}
	public void setCriterioSolicitudIni(int criterioSolicitudIni) {
		this.criterioSolicitudIni = criterioSolicitudIni;
	}
	public int getCriterioSolicitudFin() {
		return criterioSolicitudFin;
	}
	public void setCriterioSolicitudFin(int criterioSolicitudFin) {
		this.criterioSolicitudFin = criterioSolicitudFin;
	}
	public String getCriterioNoDocto() {
		return criterioNoDocto;
	}
	public void setCriterioNoDocto(String criterioNoDocto) {
		this.criterioNoDocto = criterioNoDocto;
	}
	public String getCriterioTipoEgreso() {
		return criterioTipoEgreso;
	}
	public void setCriterioTipoEgreso(String criterioTipoEgreso) {
		this.criterioTipoEgreso = criterioTipoEgreso;
	}
	public int getCriterioDivision() {
		return criterioDivision;
	}
	public void setCriterioDivision(int criterioDivision) {
		this.criterioDivision = criterioDivision;
	}
	public double getCriterioImporteIni() {
		return criterioImporteIni;
	}
	public void setCriterioImporteIni(double criterioImporteIni) {
		this.criterioImporteIni = criterioImporteIni;
	}
	public double getCriterioImporteFin() {
		return criterioImporteFin;
	}
	public void setCriterioImporteFin(double criterioImporteFin) {
		this.criterioImporteFin = criterioImporteFin;
	}
	public boolean isOptSeleccion() {
		return optSeleccion;
	}
	public void setOptSeleccion(boolean optSeleccion) {
		this.optSeleccion = optSeleccion;
	}
	public boolean isOptPendientes() {
		return optPendientes;
	}
	public void setOptPendientes(boolean optPendientes) {
		this.optPendientes = optPendientes;
	}
	public boolean isOptPorConfirmar() {
		return optPorConfirmar;
	}
	public void setOptPorConfirmar(boolean optPorConfirmar) {
		this.optPorConfirmar = optPorConfirmar;
	}
	public boolean isOptFirmados() {
		return optFirmados;
	}
	public void setOptFirmados(boolean optFirmados) {
		this.optFirmados = optFirmados;
	}
	public Date getCriterioFechaIni() {
		return criterioFechaIni;
	}
	public void setCriterioFechaIni(Date criterioFechaIni) {
		this.criterioFechaIni = criterioFechaIni;
	}
	public Date getCriterioFechaFin() {
		return criterioFechaFin;
	}
	public void setCriterioFechaFin(Date criterioFechaFin) {
		this.criterioFechaFin = criterioFechaFin;
	}
	public String getPsEquivalePersona() {
		return psEquivalePersona;
	}
	public void setPsEquivalePersona(String psEquivalePersona) {
		this.psEquivalePersona = psEquivalePersona;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public String getPsbImpreContinua() {
		return psbImpreContinua;
	}
	public void setPsbImpreContinua(String psbImpreContinua) {
		this.psbImpreContinua = psbImpreContinua;
	}
	public String getCriterioChequera() {
		return criterioChequera;
	}
	public void setCriterioChequera(String criterioChequera) {
		this.criterioChequera = criterioChequera;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public int getIdCaja() {
		return idCaja;
	}
	public void setIdCaja(int idCaja) {
		this.idCaja = idCaja;
	}
	public int getNumImpresora() {
		return numImpresora;
	}
	public void setNumImpresora(int numImpresora) {
		this.numImpresora = numImpresora;
	}
	public int getBancoCharola() {
		return bancoCharola;
	}
	public void setBancoCharola(int bancoCharola) {
		this.bancoCharola = bancoCharola;
	}
	public int getFolioInicial() {
		return folioInicial;
	}
	public void setFolioInicial(int folioInicial) {
		this.folioInicial = folioInicial;
	}
	public int getFolioFinal() {
		return folioFinal;
	}
	public void setFolioFinal(int folioFinal) {
		this.folioFinal = folioFinal;
	}
	public boolean isChkNuevoFormato() {
		return chkNuevoFormato;
	}
	public void setChkNuevoFormato(boolean chkNuevoFormato) {
		this.chkNuevoFormato = chkNuevoFormato;
	}
	public boolean isChkNegociable() {
		return chkNegociable;
	}
	public void setChkNegociable(boolean chkNegociable) {
		this.chkNegociable = chkNegociable;
	}
	public boolean isChkAbono() {
		return chkAbono;
	}
	public void setChkAbono(boolean chkAbono) {
		this.chkAbono = chkAbono;
	}
	public Date getFechaCheque() {
		return fechaCheque;
	}
	public void setFechaCheque(Date fechaCheque) {
		this.fechaCheque = fechaCheque;
	}
	public boolean isChkTodas() {
		return chkTodas;
	}
	public void setChkTodas(boolean chkTodas) {
		this.chkTodas = chkTodas;
	}
	public int getChequeNomina() {
		return chequeNomina;
	}
	public void setChequeNomina(int chequeNomina) {
		this.chequeNomina = chequeNomina;
	}
	public int getMotivo() {
		return motivo;
	}
	public void setMotivo(int motivo) {
		this.motivo = motivo;
	}
	public int getNoCheque() {
		return noCheque;
	}
	public void setNoCheque(int noCheque) {
		this.noCheque = noCheque;
	}
	public int getCriterioNoChequeIni() {
		return criterioNoChequeIni;
	}
	public void setCriterioNoChequeIni(int criterioNoChequeIni) {
		this.criterioNoChequeIni = criterioNoChequeIni;
	}
	public int getCriterioNoChequeFin() {
		return criterioNoChequeFin;
	}
	public void setCriterioNoChequeFin(int criterioNoChequeFin) {
		this.criterioNoChequeFin = criterioNoChequeFin;
	}
	public String getCriterioCveControl() {
		return criterioCveControl;
	}
	public void setCriterioCveControl(String criterioCveControl) {
		this.criterioCveControl = criterioCveControl;
	}
	
}
