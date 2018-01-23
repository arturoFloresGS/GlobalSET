package com.webset.set.bancaelectronica.dto;

import java.util.Date;

public class MovtoBancaEDto {
	
	private int noEmpresa;
	private int idBanco;
	private long secuencia; //Cambio de int a long EMS 01/12/2015
	private int folioDetConf;
	private int noLineaArch;
	private int idEstatusCancela;
	private int usuarioAlta;
	private int movtoArch;
	private int noPersona;
	
	private double saldoBancario;
	private double idRubro;
	private double importe;
	
	private String idChequera;	
	private Date   fecValor;
	private String sucursal;
	private String folioBanco;
	private String referencia;
	private String cargoAbono;	
	private String bSalvoBuenCobro;
	private String concepto;
	private String idEstatusTrasp;
	private Date fecAlta;
	private String bTraspBanco;
	private String bTraspConta;	
	private String observacion;
	private String bTraspBc;	
	private String descripcion;
	private String archivo;
	private String estatusExp;
	private Date fecConciliaCaja;	
	private String idCveConcepto;	
	private String  idEstatusBarrido;
	private String descBanco;
	private String codDivisaSET;
	private String idDivisa;
	private String fechaOperacion;
	private String descChequera;
	private String fechaAlta;
	private String importeString;
	private int folioDet;
	private String equivalePersona;
	private String origenMovAnt;
	private String codDivisaERP;
	private int noCheque;
	private String beneficiario;
	private String noFactura;
	//Agregado EMS 01/12/2015
	private String estatus;
	
	//mail
	private String avisoMail;
	private String introMail;
	private String bancoMail;
	private String referenciaMail;
	private String cuentaMail;
	private String divisaMail;
	private String tipoCambioMail;
	private String beneficMail;
	private String noFacturaMail;
	private String importeMail;
	private String conceptoMail;
	private String caracterMail;
	private String despedidaMail;
	private String fecMail;
	private String correoElectronico;
	private String tipoCambio;
	private String correosCopia;
	private String nomEmpresa;
	private String descFormaPago;
	
	public int getNoCheque() {
		return noCheque;
	}
	public void setNoCheque(int noCheque) {
		this.noCheque = noCheque;
	}
	public String getCodDivisaERP() {
		return codDivisaERP;
	}
	public void setCodDivisaERP(String codDivisaERP) {
		this.codDivisaERP = codDivisaERP;
	}
	public String getDescBanco() {
		return descBanco;
	}
	public void setDescBanco(String descBanco) {
		this.descBanco = descBanco;
	}
	public String getCodDivisaSET() {
		return codDivisaSET;
	}
	public void setCodDivisaSET(String codDivisaSET) {
		this.codDivisaSET = codDivisaSET;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public String getFechaOperacion() {
		return fechaOperacion;
	}
	public void setFechaOperacion(String fechaOperacion) {
		this.fechaOperacion = fechaOperacion;
	}
	public String getDescChequera() {
		return descChequera;
	}
	public void setDescChequera(String descChequera) {
		this.descChequera = descChequera;
	}
	public String getFechaAlta() {
		return fechaAlta;
	}
	public void setFechaAlta(String fechaAlta) {
		this.fechaAlta = fechaAlta;
	}
	public String getImporteString() {
		return importeString;
	}
	public void setImporteString(String importeString) {
		this.importeString = importeString;
	}
	public int getFolioDet() {
		return folioDet;
	}
	public void setFolioDet(int folioDet) {
		this.folioDet = folioDet;
	}
	public String getEquivalePersona() {
		return equivalePersona;
	}
	public void setEquivalePersona(String equivalePersona) {
		this.equivalePersona = equivalePersona;
	}
	public String getOrigenMovAnt() {
		return origenMovAnt;
	}
	public void setOrigenMovAnt(String origenMovAnt) {
		this.origenMovAnt = origenMovAnt;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public long getSecuencia() {
		return secuencia;
	}
	public void setSecuencia(long secuencia) {
		this.secuencia = secuencia;
	}
	public int getFolioDetConf() {
		return folioDetConf;
	}
	public void setFolioDetConf(int folioDetConf) {
		this.folioDetConf = folioDetConf;
	}
	public int getNoLineaArch() {
		return noLineaArch;
	}
	public void setNoLineaArch(int noLineaArch) {
		this.noLineaArch = noLineaArch;
	}
	public int getIdEstatusCancela() {
		return idEstatusCancela;
	}
	public void setIdEstatusCancela(int idEstatusCancela) {
		this.idEstatusCancela = idEstatusCancela;
	}
	public int getUsuarioAlta() {
		return usuarioAlta;
	}
	public void setUsuarioAlta(int usuarioAlta) {
		this.usuarioAlta = usuarioAlta;
	}
	public int getMovtoArch() {
		return movtoArch;
	}
	public void setMovtoArch(int movtoArch) {
		this.movtoArch = movtoArch;
	}
	public int getNoPersona() {
		return noPersona;
	}
	public void setNoPersona(int noPersona) {
		this.noPersona = noPersona;
	}
	public double getSaldoBancario() {
		return saldoBancario;
	}
	public void setSaldoBancario(double saldoBancario) {
		this.saldoBancario = saldoBancario;
	}
	public double getIdRubro() {
		return idRubro;
	}
	public void setIdRubro(double idRubro) {
		this.idRubro = idRubro;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public Date getFecValor() {
		return fecValor;
	}
	public void setFecValor(Date fecValor) {
		this.fecValor = fecValor;
	}
	public String getSucursal() {
		return sucursal;
	}
	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}
	public String getFolioBanco() {
		return folioBanco;
	}
	public void setFolioBanco(String folioBanco) {
		this.folioBanco = folioBanco;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getCargoAbono() {
		return cargoAbono;
	}
	public void setCargoAbono(String cargoAbono) {
		this.cargoAbono = cargoAbono;
	}
	public String getBSalvoBuenCobro() {
		return bSalvoBuenCobro;
	}
	public void setBSalvoBuenCobro(String salvoBuenCobro) {
		bSalvoBuenCobro = salvoBuenCobro;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public String getIdEstatusTrasp() {
		return idEstatusTrasp;
	}
	public void setIdEstatusTrasp(String idEstatusTrasp) {
		this.idEstatusTrasp = idEstatusTrasp;
	}
	public Date getFecAlta() {
		return fecAlta;
	}
	public void setFecAlta(Date fecAlta) {
		this.fecAlta = fecAlta;
	}
	public String getBTraspBanco() {
		return bTraspBanco;
	}
	public void setBTraspBanco(String traspBanco) {
		bTraspBanco = traspBanco;
	}
	public String getBTraspConta() {
		return bTraspConta;
	}
	public void setBTraspConta(String traspConta) {
		bTraspConta = traspConta;
	}
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	public String getBTraspBc() {
		return bTraspBc;
	}
	public void setBTraspBc(String traspBc) {
		bTraspBc = traspBc;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getArchivo() {
		return archivo;
	}
	public void setArchivo(String archivo) {
		this.archivo = archivo;
	}
	public String getEstatusExp() {
		return estatusExp;
	}
	public void setEstatusExp(String estatusExp) {
		this.estatusExp = estatusExp;
	}
	public Date getFecConciliaCaja() {
		return fecConciliaCaja;
	}
	public void setFecConciliaCaja(Date fecConciliaCaja) {
		this.fecConciliaCaja = fecConciliaCaja;
	}
	public String getIdCveConcepto() {
		return idCveConcepto;
	}
	public void setIdCveConcepto(String idCveConcepto) {
		this.idCveConcepto = idCveConcepto;
	}
	public String getIdEstatusBarrido() {
		return idEstatusBarrido;
	}
	public void setIdEstatusBarrido(String idEstatusBarrido) {
		this.idEstatusBarrido = idEstatusBarrido;
	}
	public String getBeneficiario() {
		return beneficiario;
	}
	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}
	public String getNoFactura() {
		return noFactura;
	}
	public void setNoFactura(String noFactura) {
		this.noFactura = noFactura;
	}
	
	public String getAvisoMail() {
		return avisoMail;
	}
	public void setAvisoMail(String avisoMail) {
		this.avisoMail = avisoMail;
	}
	public String getIntroMail() {
		return introMail;
	}
	public void setIntroMail(String introMail) {
		this.introMail = introMail;
	}
	public String getCaracterMail() {
		return caracterMail;
	}
	public void setCaracterMail(String caracterMail) {
		this.caracterMail = caracterMail;
	}
	public String getDespedidaMail() {
		return despedidaMail;
	}
	public void setDespedidaMail(String despedidaMail) {
		this.despedidaMail = despedidaMail;
	}
	public String getBancoMail() {
		return bancoMail;
	}
	public void setBancoMail(String bancoMail) {
		this.bancoMail = bancoMail;
	}
	public String getReferenciaMail() {
		return referenciaMail;
	}
	public void setReferenciaMail(String referenciaMail) {
		this.referenciaMail = referenciaMail;
	}
	public String getCuentaMail() {
		return cuentaMail;
	}
	public void setCuentaMail(String cuentaMail) {
		this.cuentaMail = cuentaMail;
	}
	public String getDivisaMail() {
		return divisaMail;
	}
	public void setDivisaMail(String divisaMail) {
		this.divisaMail = divisaMail;
	}
	public String getBeneficMaill() {
		return beneficMail;
	}
	public void setBeneficMail(String beneficMail) {
		this.beneficMail = beneficMail;
	}
	public String getTipoCambioMail() {
		return tipoCambioMail;
	}
	public void setTipoCambioMail(String tipoCambioMail) {
		this.tipoCambioMail = tipoCambioMail;
	}
	public String getNoFacturaMail() {
		return noFacturaMail;
	}
	public void setNoFacturaMail(String noFacturaMail) {
		this.noFacturaMail = noFacturaMail;
	}
	public String getImporteMail() {
		return importeMail;
	}
	public void setImporteMail(String importeMail) {
		this.importeMail = importeMail;
	}
	public String getConceptoMail() {
		return conceptoMail;
	}
	public void setConceptoMail(String conceptoMail) {
		this.conceptoMail = conceptoMail;
	}
	public String getFecMail() {
		return fecMail;
	}
	public void setFecMail(String fecMail) {
		this.fecMail = fecMail;
	}
	public String getCorreoElectronico() {
		return correoElectronico;
	}
	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}
	public String getTipoCambio() {
		return tipoCambio;
	}
	public void setTipoCambio(String tipoCambio) {
		this.tipoCambio = tipoCambio;
	}
	public String getCorreosCopia() {
		return correosCopia;
	}
	public void setCorreosCopia(String correosCopia) {
		this.correosCopia = correosCopia;
	}
	public String getNomEmpresa() {
		return nomEmpresa;
	}
	public void setNomEmpresa(String nomEmpresa) {
		this.nomEmpresa = nomEmpresa;
	}
	public String getBeneficMail() {
		return beneficMail;
	}
	public String getDescFormaPago() {
		return descFormaPago;
	}
	public void setDescFormaPago(String descFormaPago) {
		this.descFormaPago = descFormaPago;
	}
	public String getEstatus() {
		return estatus;
	}
	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}
}
