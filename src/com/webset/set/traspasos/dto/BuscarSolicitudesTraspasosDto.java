package com.webset.set.traspasos.dto;

import java.util.Date;

import org.jfree.data.time.Hour;
/**
 * @author Jessica Arelly Cruz Cruz
 * @since 29/03/2011
 *
 */
public class BuscarSolicitudesTraspasosDto {
	private int plazo;
	private int noCuenta;
	private int idContratoMass;
	private int noPersona;
	private int noMedio;
	private int noDireccion;
	private int idGrupoFlujo;
	private int nivelAutorizacion;
	private int nivelProp;
	private int noFolioDet;
	private int idBanco;
	private int idBancoInv;
	private int noFolioMov;
	private int idBancoBenef;
	private int idCveOperacion;
	private int idTipoOperacion;
	private int noEmpresa;
	private int noEmpresaCXP;
	private int noEmpresaBenef;
	private int loteEntrada;
	private int plaza;
	private int idUsuario;
	private double importeTraspaso;
	private double traspasoEjecutado;
	private double tasa;
	private double isr;
	private double interes;
	private double importeOriginal;
	private double importeParcial;
	private double importe;
	private double importeInv;
	private double tipoCambio;
	private boolean opcionMismoBanco;
	private boolean opcionEmpresaActual;
	private boolean opcionSpeua;
	private boolean opcionInternacional;
	private boolean opcionInversion;
	private boolean opcionInterbancaria;
	//opciones del frame tipos de envio HSBC
	private boolean opcionBitalH2H;
	private boolean opcionBitalNormal;
	//opciones del frame tipos de envio banamex
	private boolean opcionBanamexNormal;
	private boolean opcionBanamexTEF;
	private boolean opcionBanamexMassPayment;
	private boolean opcionCitiBankFlatFile;
	private boolean opcionCitiBankPaylinkMN;
	private boolean opcionCitiBankACH;
	private boolean cambioDivisa;
	private Date fecVenc;
	private Date fecOperacion;
	private Date fecValor;
	private Date fechaHoy;
	private Date fecValorOriginal;
	private Date fecPropuesta;
	private Date fechaIni;
	private Date fechaFin;
	private Date fechaPropuesta;
	private Hour hora;
	private String beneficiario;
	private String origenMov;
	private String invoiceType;
	private String nomEmpresa;
	private String idChequera;
	private String idChequeraInv;
	private String descBanco;
	private String descBancoBenef;
	private String idBancoCity;
	private String bLayoutComerica;
	private String descPlaza;
	private String plazaBenef;
//	private String plaza;
	private String idEstatusMov;
	private String noOrden;
	private String descCveOperacion;
	private String referencia;
	private String noCliente;
	private String descSucursal;
	private String sucursalDestino;
	private String sucursalOrigen;
	private String instFinan;
	private String instFinanBenef;
	private String idClabe;
	private String clabeBenef;
	private String swiftCode;
	private String swiftBenef;
	private String tipoChequera;
	private String tipoChequeraBenef;
	private String idTipoPersona;
	private String idTipoMedio;
	private String descPais;
	private String idTipoDireccion;
	private String idPais;
	private String ciudad;
	private String idEstado;
	private String noDocto;
	private String idDivisa;
	private String idChequeraBenef;
	private String concepto;
	private String idTipoMovto;
	private String observacion;
	private String descBancoInbursa;
	private String operMayEsp;
	private String validaClabe;
	private String aba;
	private String abaBenef;
	private String swift;
	private String nacExt;
	private String habilitado;
	private String cveControl;
	private String rfc;
	private String rfcBenef;
	private String complemento;
	private String especiales;
	private String direccionBenef;
	private String paisBenef;
	private String descUsuarioBital;
	private String descServicioBital;
	private String cmbBenef;
	
	private String usr1;
	private String usr2;
	private String color;
	private int usrUno;
	private int usrDos;
	private boolean bSumImp;
	private boolean chkH2HSantander;
	
	public String getCmbBenef() {
		return cmbBenef;
	}
	public void setCmbBenef(String cmbBenef) {
		this.cmbBenef = cmbBenef;
	}
	public String getDescUsuarioBital() {
		return descUsuarioBital;
	}
	public void setDescUsuarioBital(String descUsuarioBital) {
		this.descUsuarioBital = descUsuarioBital;
	}
	public String getDescServicioBital() {
		return descServicioBital;
	}
	public void setDescServicioBital(String descServicioBital) {
		this.descServicioBital = descServicioBital;
	}
	public int getPlazo() {
		return plazo;
	}
	public void setPlazo(int plazo) {
		this.plazo = plazo;
	}
	public int getNoCuenta() {
		return noCuenta;
	}
	public void setNoCuenta(int noCuenta) {
		this.noCuenta = noCuenta;
	}
	public int getIdContratoMass() {
		return idContratoMass;
	}
	public void setIdContratoMass(int idContratoMass) {
		this.idContratoMass = idContratoMass;
	}
	public int getNoPersona() {
		return noPersona;
	}
	public void setNoPersona(int noPersona) {
		this.noPersona = noPersona;
	}
	public int getNoMedio() {
		return noMedio;
	}
	public void setNoMedio(int noMedio) {
		this.noMedio = noMedio;
	}
	public int getNoDireccion() {
		return noDireccion;
	}
	public void setNoDireccion(int noDireccion) {
		this.noDireccion = noDireccion;
	}
	public int getIdGrupoFlujo() {
		return idGrupoFlujo;
	}
	public void setIdGrupoFlujo(int idGrupoFlujo) {
		this.idGrupoFlujo = idGrupoFlujo;
	}
	public int getNivelAutorizacion() {
		return nivelAutorizacion;
	}
	public void setNivelAutorizacion(int nivelAutorizacion) {
		this.nivelAutorizacion = nivelAutorizacion;
	}
	public int getNoFolioDet() {
		return noFolioDet;
	}
	public void setNoFolioDet(int noFolioDet) {
		this.noFolioDet = noFolioDet;
	}
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public int getNoFolioMov() {
		return noFolioMov;
	}
	public void setNoFolioMov(int noFolioMov) {
		this.noFolioMov = noFolioMov;
	}
	public int getIdBancoBenef() {
		return idBancoBenef;
	}
	public void setIdBancoBenef(int idBancoBenef) {
		this.idBancoBenef = idBancoBenef;
	}
	public int getIdCveOperacion() {
		return idCveOperacion;
	}
	public void setIdCveOperacion(int idCveOperacion) {
		this.idCveOperacion = idCveOperacion;
	}
	public int getIdTipoOperacion() {
		return idTipoOperacion;
	}
	public void setIdTipoOperacion(int idTipoOperacion) {
		this.idTipoOperacion = idTipoOperacion;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public int getLoteEntrada() {
		return loteEntrada;
	}
	public void setLoteEntrada(int loteEntrada) {
		this.loteEntrada = loteEntrada;
	}
//	public int getPlaza() {
//		return plaza;
//	}
//	public void setPlaza(int plaza) {
//		this.plaza = plaza;
//	}
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
	public double getTasa() {
		return tasa;
	}
	public void setTasa(double tasa) {
		this.tasa = tasa;
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
	public double getImporteOriginal() {
		return importeOriginal;
	}
	public void setImporteOriginal(double importeOriginal) {
		this.importeOriginal = importeOriginal;
	}
	public double getImporteParcial() {
		return importeParcial;
	}
	public void setImporteParcial(double importeParcial) {
		this.importeParcial = importeParcial;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
	public Date getFecVenc() {
		return fecVenc;
	}
	public void setFecVenc(Date fecVenc) {
		this.fecVenc = fecVenc;
	}
	public Date getFecOperacion() {
		return fecOperacion;
	}
	public void setFecOperacion(Date fecOperacion) {
		this.fecOperacion = fecOperacion;
	}
	public Date getFecValor() {
		return fecValor;
	}
	public void setFecValor(Date fecValor) {
		this.fecValor = fecValor;
	}
	public Date getFechaHoy() {
		return fechaHoy;
	}
	public void setFechaHoy(Date fechaHoy) {
		this.fechaHoy = fechaHoy;
	}
	public Date getFecValorOriginal() {
		return fecValorOriginal;
	}
	public void setFecValorOriginal(Date fecValorOriginal) {
		this.fecValorOriginal = fecValorOriginal;
	}
	public Date getFecPropuesta() {
		return fecPropuesta;
	}
	public void setFecPropuesta(Date fecPropuesta) {
		this.fecPropuesta = fecPropuesta;
	}
	public Hour getHora() {
		return hora;
	}
	public void setHora(Hour hora) {
		this.hora = hora;
	}
	public String getOrigenMov() {
		return origenMov;
	}
	public void setOrigenMov(String origenMov) {
		this.origenMov = origenMov;
	}
	public String getInvoiceType() {
		return invoiceType;
	}
	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}
	public String getNomEmpresa() {
		return nomEmpresa;
	}
	public void setNomEmpresa(String nomEmpresa) {
		this.nomEmpresa = nomEmpresa;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public String getDescBanco() {
		return descBanco;
	}
	public void setDescBanco(String descBanco) {
		this.descBanco = descBanco;
	}
	public String getIdBancoCity() {
		return idBancoCity;
	}
	public void setIdBancoCity(String idBancoCity) {
		this.idBancoCity = idBancoCity;
	}
	public String getBLayoutComerica() {
		return bLayoutComerica;
	}
	public void setBLayoutComerica(String layoutComerica) {
		bLayoutComerica = layoutComerica;
	}
	public String getDescPlaza() {
		return descPlaza;
	}
	public void setDescPlaza(String descPlaza) {
		this.descPlaza = descPlaza;
	}
	public String getIdEstatusMov() {
		return idEstatusMov;
	}
	public void setIdEstatusMov(String idEstatusMov) {
		this.idEstatusMov = idEstatusMov;
	}
	public String getNoOrden() {
		return noOrden;
	}
	public void setNoOrden(String noOrden) {
		this.noOrden = noOrden;
	}
	public String getDescCveOperacion() {
		return descCveOperacion;
	}
	public void setDescCveOperacion(String descCveOperacion) {
		this.descCveOperacion = descCveOperacion;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getNoCliente() {
		return noCliente;
	}
	public void setNoCliente(String noCliente) {
		this.noCliente = noCliente;
	}
	public String getDescSucursal() {
		return descSucursal;
	}
	public void setDescSucursal(String descSucursal) {
		this.descSucursal = descSucursal;
	}
	public String getInstFinan() {
		return instFinan;
	}
	public void setInstFinan(String instFinan) {
		this.instFinan = instFinan;
	}
	public String getIdClabe() {
		return idClabe;
	}
	public void setIdClabe(String idClabe) {
		this.idClabe = idClabe;
	}
	public String getSwiftCode() {
		return swiftCode;
	}
	public void setSwiftCode(String swiftCode) {
		this.swiftCode = swiftCode;
	}
	public String getTipoChequera() {
		return tipoChequera;
	}
	public void setTipoChequera(String tipoChequera) {
		this.tipoChequera = tipoChequera;
	}
	public String getIdTipoPersona() {
		return idTipoPersona;
	}
	public void setIdTipoPersona(String idTipoPersona) {
		this.idTipoPersona = idTipoPersona;
	}
	public String getIdTipoMedio() {
		return idTipoMedio;
	}
	public void setIdTipoMedio(String idTipoMedio) {
		this.idTipoMedio = idTipoMedio;
	}
	public String getDescPais() {
		return descPais;
	}
	public void setDescPais(String descPais) {
		this.descPais = descPais;
	}
	public String getIdTipoDireccion() {
		return idTipoDireccion;
	}
	public void setIdTipoDireccion(String idTipoDireccion) {
		this.idTipoDireccion = idTipoDireccion;
	}
	public String getIdPais() {
		return idPais;
	}
	public void setIdPais(String idPais) {
		this.idPais = idPais;
	}
	public String getCiudad() {
		return ciudad;
	}
	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}
	public String getIdEstado() {
		return idEstado;
	}
	public void setIdEstado(String idEstado) {
		this.idEstado = idEstado;
	}
	public String getNoDocto() {
		return noDocto;
	}
	public void setNoDocto(String noDocto) {
		this.noDocto = noDocto;
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
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public String getIdTipoMovto() {
		return idTipoMovto;
	}
	public void setIdTipoMovto(String idTipoMovto) {
		this.idTipoMovto = idTipoMovto;
	}
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	public String getDescBancoInbursa() {
		return descBancoInbursa;
	}
	public void setDescBancoInbursa(String descBancoInbursa) {
		this.descBancoInbursa = descBancoInbursa;
	}
	public String getOperMayEsp() {
		return operMayEsp;
	}
	public void setOperMayEsp(String operMayEsp) {
		this.operMayEsp = operMayEsp;
	}
	public String getValidaClabe() {
		return validaClabe;
	}
	public void setValidaClabe(String validaClabe) {
		this.validaClabe = validaClabe;
	}
	public String getAba() {
		return aba;
	}
	public void setAba(String aba) {
		this.aba = aba;
	}
	public String getSwift() {
		return swift;
	}
	public void setSwift(String swift) {
		this.swift = swift;
	}
	public String getNacExt() {
		return nacExt;
	}
	public void setNacExt(String nacExt) {
		this.nacExt = nacExt;
	}
	public String getHabilitado() {
		return habilitado;
	}
	public void setHabilitado(String habilitado) {
		this.habilitado = habilitado;
	}
	public String getCveControl() {
		return cveControl;
	}
	public void setCveControl(String cveControl) {
		this.cveControl = cveControl;
	}
	public String getRfc() {
		return rfc;
	}
	public void setRfc(String rfc) {
		this.rfc = rfc;
	}
	public boolean isOpcionMismoBanco() {
		return opcionMismoBanco;
	}
	public void setOpcionMismoBanco(boolean opcionMismoBanco) {
		this.opcionMismoBanco = opcionMismoBanco;
	}
	public boolean isOpcionEmpresaActual() {
		return opcionEmpresaActual;
	}
	public void setOpcionEmpresaActual(boolean opcionEmpresaActual) {
		this.opcionEmpresaActual = opcionEmpresaActual;
	}
	public boolean isOpcionSpeua() {
		return opcionSpeua;
	}
	public void setOpcionSpeua(boolean opcionSpeua) {
		this.opcionSpeua = opcionSpeua;
	}
	public boolean isOpcionInternacional() {
		return opcionInternacional;
	}
	public void setOpcionInternacional(boolean opcionInternacional) {
		this.opcionInternacional = opcionInternacional;
	}
	public boolean isOpcionInversion() {
		return opcionInversion;
	}
	public void setOpcionInversion(boolean opcionInversion) {
		this.opcionInversion = opcionInversion;
	}
	public boolean isOpcionInterbancaria() {
		return opcionInterbancaria;
	}
	public void setOpcionInterbancaria(boolean opcionInterbancaria) {
		this.opcionInterbancaria = opcionInterbancaria;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
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
	public String getBeneficiario() {
		return beneficiario;
	}
	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}
	public int getNivelProp() {
		return nivelProp;
	}
	public void setNivelProp(int nivelProp) {
		this.nivelProp = nivelProp;
	}
	public int getIdBancoInv() {
		return idBancoInv;
	}
	public void setIdBancoInv(int idBancoInv) {
		this.idBancoInv = idBancoInv;
	}
	public int getNoEmpresaCXP() {
		return noEmpresaCXP;
	}
	public void setNoEmpresaCXP(int noEmpresaCXP) {
		this.noEmpresaCXP = noEmpresaCXP;
	}
	public double getImporteInv() {
		return importeInv;
	}
	public void setImporteInv(double importeInv) {
		this.importeInv = importeInv;
	}
	public Date getFechaPropuesta() {
		return fechaPropuesta;
	}
	public void setFechaPropuesta(Date fechaPropuesta) {
		this.fechaPropuesta = fechaPropuesta;
	}
	public String getIdChequeraInv() {
		return idChequeraInv;
	}
	public void setIdChequeraInv(String idChequeraInv) {
		this.idChequeraInv = idChequeraInv;
	}
	public String getDescBancoBenef() {
		return descBancoBenef;
	}
	public void setDescBancoBenef(String descBancoBenef) {
		this.descBancoBenef = descBancoBenef;
	}
	public String getPlazaBenef() {
		return plazaBenef;
	}
	public void setPlazaBenef(String plazaBenef) {
		this.plazaBenef = plazaBenef;
	}
//	public String getPlaza() {
//		return plaza;
//	}
//	public void setPlaza(String plaza) {
//		this.plaza = plaza;
//	}
	public int getNoEmpresaBenef() {
		return noEmpresaBenef;
	}
	public void setNoEmpresaBenef(int noEmpresaBenef) {
		this.noEmpresaBenef = noEmpresaBenef;
	}
	public String getSucursalDestino() {
		return sucursalDestino;
	}
	public void setSucursalDestino(String sucursalDestino) {
		this.sucursalDestino = sucursalDestino;
	}
	public String getSucursalOrigen() {
		return sucursalOrigen;
	}
	public void setSucursalOrigen(String sucursalOrigen) {
		this.sucursalOrigen = sucursalOrigen;
	}
	public String getInstFinanBenef() {
		return instFinanBenef;
	}
	public void setInstFinanBenef(String instFinanBenef) {
		this.instFinanBenef = instFinanBenef;
	}
	public String getClabeBenef() {
		return clabeBenef;
	}
	public void setClabeBenef(String clabeBenef) {
		this.clabeBenef = clabeBenef;
	}
	public String getSwiftBenef() {
		return swiftBenef;
	}
	public void setSwiftBenef(String swiftBenef) {
		this.swiftBenef = swiftBenef;
	}
	public String getTipoChequeraBenef() {
		return tipoChequeraBenef;
	}
	public void setTipoChequeraBenef(String tipoChequeraBenef) {
		this.tipoChequeraBenef = tipoChequeraBenef;
	}
	public String getAbaBenef() {
		return abaBenef;
	}
	public void setAbaBenef(String abaBenef) {
		this.abaBenef = abaBenef;
	}
	public String getRfcBenef() {
		return rfcBenef;
	}
	public void setRfcBenef(String rfcBenef) {
		this.rfcBenef = rfcBenef;
	}
	public String getComplemento() {
		return complemento;
	}
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	public String getEspeciales() {
		return especiales;
	}
	public void setEspeciales(String especiales) {
		this.especiales = especiales;
	}
	public String getDireccionBenef() {
		return direccionBenef;
	}
	public void setDireccionBenef(String direccionBenef) {
		this.direccionBenef = direccionBenef;
	}
	public String getPaisBenef() {
		return paisBenef;
	}
	public void setPaisBenef(String paisBenef) {
		this.paisBenef = paisBenef;
	}
	public boolean isOpcionBitalH2H() {
		return opcionBitalH2H;
	}
	public void setOpcionBitalH2H(boolean opcionBitalH2H) {
		this.opcionBitalH2H = opcionBitalH2H;
	}
	public boolean isOpcionBitalNormal() {
		return opcionBitalNormal;
	}
	public void setOpcionBitalNormal(boolean opcionBitalNormal) {
		this.opcionBitalNormal = opcionBitalNormal;
	}
	public boolean isOpcionBanamexNormal() {
		return opcionBanamexNormal;
	}
	public void setOpcionBanamexNormal(boolean opcionBanamexNormal) {
		this.opcionBanamexNormal = opcionBanamexNormal;
	}
	public boolean isOpcionBanamexTEF() {
		return opcionBanamexTEF;
	}
	public void setOpcionBanamexTEF(boolean opcionBanamexTEF) {
		this.opcionBanamexTEF = opcionBanamexTEF;
	}
	public boolean isOpcionBanamexMassPayment() {
		return opcionBanamexMassPayment;
	}
	public void setOpcionBanamexMassPayment(boolean opcionBanamexMassPayment) {
		this.opcionBanamexMassPayment = opcionBanamexMassPayment;
	}
	public boolean isOpcionCitiBankFlatFile() {
		return opcionCitiBankFlatFile;
	}
	public void setOpcionCitiBankFlatFile(boolean opcionCitiBankFlatFile) {
		this.opcionCitiBankFlatFile = opcionCitiBankFlatFile;
	}
	public boolean isOpcionCitiBankPaylinkMN() {
		return opcionCitiBankPaylinkMN;
	}
	public void setOpcionCitiBankPaylinkMN(boolean opcionCitiBankPaylinkMN) {
		this.opcionCitiBankPaylinkMN = opcionCitiBankPaylinkMN;
	}
	public boolean isOpcionCitiBankACH() {
		return opcionCitiBankACH;
	}
	public void setOpcionCitiBankACH(boolean opcionCitiBankACH) {
		this.opcionCitiBankACH = opcionCitiBankACH;
	}
	public boolean isCambioDivisa() {
		return cambioDivisa;
	}
	public void setCambioDivisa(boolean cambioDivisa) {
		this.cambioDivisa = cambioDivisa;
	}
	public int getPlaza() {
		return plaza;
	}
	public void setPlaza(int plaza) {
		this.plaza = plaza;
	}
	public double getTipoCambio() {
		return tipoCambio;
	}
	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}
	public String getUsr1() {
		return usr1;
	}
	public void setUsr1(String usr1) {
		this.usr1 = usr1;
	}
	public String getUsr2() {
		return usr2;
	}
	public void setUsr2(String usr2) {
		this.usr2 = usr2;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public int getUsrUno() {
		return usrUno;
	}
	public void setUsrUno(int usrUno) {
		this.usrUno = usrUno;
	}
	public int getUsrDos() {
		return usrDos;
	}
	public void setUsrDos(int usrDos) {
		this.usrDos = usrDos;
	}
	public boolean isBSumImp() {
		return bSumImp;
	}
	public void setBSumImp(boolean sumImp) {
		bSumImp = sumImp;
	}
	public boolean isChkH2HSantander() {
		return chkH2HSantander;
	}
	public void setChkH2HSantander(boolean chkH2HSantander) {
		this.chkH2HSantander = chkH2HSantander;
	}
}
