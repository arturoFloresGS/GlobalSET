package com.webset.set.inversiones.dto;

import java.math.BigDecimal;
import java.util.Date;
/**
 * Este dto representa la tabla 
 * orden_inversion
 * @author Cristian García García
 */
public class OrdenInversionDto {
	
	private    Integer noEmpresa;
	private	String noOrden;
	private	String idPapel;
	private	String idTipoValor;
	private	Integer noCuenta;
	private	Integer noInstitucion;
	private	Integer noContacto;
	private	Date fecVenc;
	private	Integer hora;
	private	Integer minuto;
	private	double	importe;
	private	Integer plazo;
	private	double	isr;
	private	Integer usuarioAlta;
	private	Date fecAlta;
	private	Integer usuarioModif;
	private	String idEstatusOrd;
	private	double interes;
	private	Integer diasAnual;
	private	Integer noContactoV;
	private	Integer horaV;
	private	Integer minutoV;
	private	double	ajusteInt;
	private	String nota;
	private	double	ajusteIsr;
	private	double	tasaIsr;
	private	double tasa;
	private	double importeTraspaso;
	private	double traspasoEjecutado;
	private	Integer idBanco;
	private	String idChequera;
	private	double tasaCurva28;
	private	String bGarantia;
	private	Integer noOrdenRef;
	private	String bAutoriza;
	private	Integer noAvisoRef;
	private	Integer grupoInversion;
	private	String tipoOrden;
	private String idDivisa;
	private boolean nuevoInversion;
	private String contrato;
	private String noOrdenNva;
	
	private Double neto;

	private	Integer idBancoInv;
	private	Integer idBancoReg;
	private	Integer idBancoInst;
	private	String idChequeraReg;
	private	String idChequeraInv;
	private	String idChequeraInst;
	private    Integer folio;
	private  	String contraparte;
	private String instrumento;
	private Integer cveContrato;
	private Double impuestoEsperado;
	private Double factorImpuesto;
	private String moneda;
	private String institucion;
	private String horaMinuto;
	private Integer emisionDe;
	private Integer diasInvertir;
	private Date fechaValor;
	private String origen;
	private String estatus;
	private String garantia;
	private BigDecimal rubro;
	private BigDecimal grupo;
	private BigDecimal rubroReg;
	private BigDecimal grupoReg;
	private BigDecimal rubroInt;
	private BigDecimal grupoInt;
	private BigDecimal rubroISR;
	private BigDecimal grupoISR;
	
	private String sFechaValor;
	private String sFecVenc;
	private String sFecAlta;
	private Integer folioSeq;
	
	public Integer getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(Integer noEmpresa) {
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
	public Integer getNoCuenta() {
		return noCuenta;
	}
	public void setNoCuenta(Integer nocuenta) {
		this.noCuenta = nocuenta;
	}
	public Integer getNoInstitucion() {
		return noInstitucion;
	}
	public void setNoInstitucion(Integer noInstitucion) {
		this.noInstitucion = noInstitucion;
	}
	public Integer getNoContacto() {
		return noContacto;
	}
	public void setNoContacto(Integer noContacto) {
		this.noContacto = noContacto;
	}
	public Date getFecVenc() {
		return fecVenc;
	}
	public void setFecVenc(Date fecVenc) {
		this.fecVenc = fecVenc;
	}
	public Integer getHora() {
		return hora;
	}
	public void setHora(Integer hora) {
		this.hora = hora;
	}
	public Integer getMinuto() {
		return minuto;
	}
	public void setMinuto(Integer minuto) {
		this.minuto = minuto;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
	public Integer getPlazo() {
		return plazo;
	}
	public void setPlazo(Integer plazo) {
		this.plazo = plazo;
	}
	public double getIsr() {
		return isr;
	}
	public void setIsr(double isr) {
		this.isr = isr;
	}
	public Integer getUsuarioAlta() {
		return usuarioAlta;
	}
	public void setUsuarioAlta(Integer usuarioAlta) {
		this.usuarioAlta = usuarioAlta;
	}
	public Date getFecAlta() {
		return fecAlta;
	}
	public void setFecAlta(Date fecAlta) {
		this.fecAlta = fecAlta;
	}
	public Integer getUsuarioModif() {
		return usuarioModif;
	}
	public void setUsuarioModif(Integer usuarioModif) {
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
	public Integer getDiasAnual() {
		return diasAnual;
	}
	public void setDiasAnual(Integer diasAnual) {
		this.diasAnual = diasAnual;
	}
	public Integer getNoContactoV() {
		return noContactoV;
	}
	public void setNoContactoV(Integer noContactoV) {
		this.noContactoV = noContactoV;
	}
	public Integer getHoraV() {
		return horaV;
	}
	public void setHoraV(Integer horaV) {
		this.horaV = horaV;
	}
	public Integer getMinutoV() {
		return minutoV;
	}
	public void setMinutoV(Integer minutoV) {
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
	public Integer getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(Integer idBanco) {
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
	public Integer getIdBancoReg() {
		return idBancoReg;
	}
	public void setIdBancoReg(Integer idBancoReg) {
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
	public Integer getNoOrdenRef() {
		return noOrdenRef;
	}
	public void setNoOrdenRef(Integer noOrdenRef) {
		this.noOrdenRef = noOrdenRef;
	}
	public String getBAutoriza() {
		return bAutoriza;
	}
	public void setBAutoriza(String autoriza) {
		bAutoriza = autoriza;
	}
	public Integer getNoAvisoRef() {
		return noAvisoRef;
	}
	public void setNoAvisoRef(Integer noAvisoRef) {
		this.noAvisoRef = noAvisoRef;
	}
	public Integer getGrupoInversion() {
		return grupoInversion;
	}
	public void setGrupoInversion(Integer grupoInversion) {
		this.grupoInversion = grupoInversion;
	}
	public String getTipoOrden() {
		return tipoOrden;
	}
	public void setTipoOrden(String tipoOrden) {
		this.tipoOrden = tipoOrden;
	}
	public boolean isNuevoInversion() {
		return nuevoInversion;
	}
	public void setNuevoInversion(boolean nuevoInversion) {
		this.nuevoInversion = nuevoInversion;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public String getContrato() {
		return contrato;
	}
	public void setContrato(String contrato) {
		this.contrato = contrato;
	}
	public String getNoOrdenNva() {
		return noOrdenNva;
	}
	public void setNoOrdenNva(String noOrdenNva) {
		this.noOrdenNva = noOrdenNva;
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
	public Integer getIdBancoInv() {
		return idBancoInv;
	}
	public void setIdBancoInv(Integer idBancoInv) {
		this.idBancoInv = idBancoInv;
	}
	public Integer getIdBancoInst() {
		return idBancoInst;
	}
	public void setIdBancoInst(Integer idBancoInst) {
		this.idBancoInst = idBancoInst;
	}
	public String getIdChequeraInv() {
		return idChequeraInv;
	}
	public void setIdChequeraInv(String idChequeraInv) {
		this.idChequeraInv = idChequeraInv;
	}
	public String getIdChequeraInst() {
		return idChequeraInst;
	}
	public void setIdChequeraInst(String idChequeraInst) {
		this.idChequeraInst = idChequeraInst;
	}
	public Integer getFolio() {
		return folio;
	}
	public void setFolio(Integer folio) {
		this.folio = folio;
	}
	public String getContraparte() {
		return contraparte;
	}
	public void setContraparte(String contraparte) {
		this.contraparte = contraparte;
	}
	public String getInstrumento() {
		return instrumento;
	}
	public void setInstrumento(String instrumento) {
		this.instrumento = instrumento;
	}
	public Integer getCveContrato() {
		return cveContrato;
	}
	public void setCveContrato(Integer cveContrato) {
		this.cveContrato = cveContrato;
	}
	public Double getImpuestoEsperado() {
		return impuestoEsperado;
	}
	public void setImpuestoEsperado(Double impuestoEsperado) {
		this.impuestoEsperado = impuestoEsperado;
	}
	public Double getFactorImpuesto() {
		return factorImpuesto;
	}
	public void setFactorImpuesto(Double factorImpuesto) {
		this.factorImpuesto = factorImpuesto;
	}
	public String getMoneda() {
		return moneda;
	}
	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}
	public String getInstitucion() {
		return institucion;
	}
	public void setInstitucion(String institucion) {
		this.institucion = institucion;
	}
	public String getHoraMinuto() {
		return horaMinuto;
	}
	public void setHoraMinuto(String horaMinuto) {
		this.horaMinuto = horaMinuto;
	}
	public Integer getEmisionDe() {
		return emisionDe;
	}
	public void setEmisionDe(Integer emisionDe) {
		this.emisionDe = emisionDe;
	}
	public Integer getDiasInvertir() {
		return diasInvertir;
	}
	public void setDiasInvertir(Integer diasInvertir) {
		this.diasInvertir = diasInvertir;
	}
	public Date getFechaValor() {
		return fechaValor;
	}
	public void setFechaValor(Date fechaValor) {
		this.fechaValor = fechaValor;
	}
	public String getOrigen() {
		return origen;
	}
	public void setOrigen(String origen) {
		this.origen = origen;
	}
	public String getEstatus() {
		return estatus;
	}
	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}
	public String getGarantia() {
		return garantia;
	}
	public void setGarantia(String garantia) {
		this.garantia = garantia;
	}
	public BigDecimal getRubro() {
		return rubro;
	}
	public void setRubro(BigDecimal rubro) {
		this.rubro = rubro;
	}
	public String getsFechaValor() {
		return sFechaValor;
	}
	public void setsFechaValor(String sFechaValor) {
		this.sFechaValor = sFechaValor;
	}
	public String getsFecVenc() {
		return sFecVenc;
	}
	public void setsFecVenc(String sFecVenc) {
		this.sFecVenc = sFecVenc;
	}
	public String getsFecAlta() {
		return sFecAlta;
	}
	public void setsFecAlta(String sFecAlta) {
		this.sFecAlta = sFecAlta;
	}
	public Integer getFolioSeq() {
		return folioSeq;
	}
	public void setFolioSeq(Integer folioSeq) {
		this.folioSeq = folioSeq;
	}
	public BigDecimal getGrupo() {
		return grupo;
	}
	public void setGrupo(BigDecimal grupo) {
		this.grupo = grupo;
	}
	public BigDecimal getRubroReg() {
		return rubroReg;
	}
	public void setRubroReg(BigDecimal rubroReg) {
		this.rubroReg = rubroReg;
	}
	public BigDecimal getGrupoReg() {
		return grupoReg;
	}
	public void setGrupoReg(BigDecimal grupoReg) {
		this.grupoReg = grupoReg;
	}
	public BigDecimal getRubroInt() {
		return rubroInt;
	}
	public void setRubroInt(BigDecimal rubroInt) {
		this.rubroInt = rubroInt;
	}
	public BigDecimal getGrupoInt() {
		return grupoInt;
	}
	public void setGrupoInt(BigDecimal grupoInt) {
		this.grupoInt = grupoInt;
	}
	public BigDecimal getRubroISR() {
		return rubroISR;
	}
	public void setRubroISR(BigDecimal rubroISR) {
		this.rubroISR = rubroISR;
	}
	public BigDecimal getGrupoISR() {
		return grupoISR;
	}
	public void setGrupoISR(BigDecimal grupoISR) {
		this.grupoISR = grupoISR;
	}
	public Double getNeto() {
		return neto;
	}
	public void setNeto(Double neto) {
		this.neto = neto;
	}
	
}
