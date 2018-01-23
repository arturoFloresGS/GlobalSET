package com.webset.set.egresos.dto;

import java.util.Date;

public class PagosPendientesDto {
	
	private String nombreEmpresa;
	private String formaPago;
	private String beneficiario;
	private String factura;
	private String bancoP;
	private String texto;
	private String numeroDcoumento;
	private String chequera;
	private String referenciaBanc;
	private String divisa;
	private String estatus;
	private String equivalePersona;
	private int numeroEmpresa;
	private int usuario;
	private int folio;
	private int index;
	private int idBanco;
	private double vencido;
	private double porVencer;
	private double total;
	private double importeNeto;
	private double vencidoS;
	private double vencidoP;
	private double porVencerS;
	private double porVencerP;
	private Date fecValor;
	private Date fecCont;
	private Date fecOperacion;
	private String noFact;
	private String fechaPropuesta;
	private String cveControl;
	private double totalGlobal;
	private double importeOriginal;
	private String claseDocumento;
	private String seleccionado;
	
	private boolean bandera;
	private boolean bPorVencer;
	
	private String fechaFactura;
	
	
	public String getSeleccionado() {
		return seleccionado;
	}
	public void setSeleccionado(String seleccionado) {
		this.seleccionado = seleccionado;
	}
	public boolean isbPorVencer() {
		return bPorVencer;
	}
	public void setbPorVencer(boolean bPorVencer) {
		this.bPorVencer = bPorVencer;
	}
	public boolean isBandera() {
		return bandera;
	}
	public void setBandera(boolean bandera) {
		this.bandera = bandera;
	}
	public double getImporteOriginal() {
		return importeOriginal;
	}
	public void setImporteOriginal(double importeOriginal) {
		this.importeOriginal = importeOriginal;
	}
	public double getTotalGlobal() {
		return totalGlobal;
	}
	public void setTotalGlobal(double totalGlobal) {
		this.totalGlobal = totalGlobal;
	}
	public String getCveControl() {
		return cveControl;
	}
	public void setCveControl(String cveControl) {
		this.cveControl = cveControl;
	}
	public String getFechaPropuesta() {
		return fechaPropuesta;
	}
	public void setFechaPropuesta(String fechaPropuesta) {
		this.fechaPropuesta = fechaPropuesta;
	}
	public Date getFecValor() {
		return fecValor;
	}
	public void setFecValor(Date fecValor) {
		this.fecValor = fecValor;
	}
	public Date getFecCont() {
		return fecCont;
	}
	public void setFecCont(Date fecCont) {
		this.fecCont = fecCont;
	}
	public Date getFecOperacion() {
		return fecOperacion;
	}
	public void setFecOperacion(Date fecOperacion) {
		this.fecOperacion = fecOperacion;
	}
	public String getNoFact() {
		return noFact;
	}
	public void setNoFact(String noFact) {
		this.noFact = noFact;
	}
	public String getBancoP() {
		return bancoP;
	}
	public void setBancoP(String bancoP) {
		this.bancoP = bancoP;
	}
	public double getPorVencerS() {
		return porVencerS;
	}
	public void setPorVencerS(double porVencerS) {
		this.porVencerS = porVencerS;
	}
	public double getPorVencerP() {
		return porVencerP;
	}
	public void setPorVencerP(double porVencerP) {
		this.porVencerP = porVencerP;
	}
	public String getEstatus() {
		return estatus;
	}
	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}
	public String getDivisa() {
		return divisa;
	}
	public void setDivisa(String divisa) {
		this.divisa = divisa;
	}
	public double getVencidoP() {
		return vencidoP;
	}
	public void setVencidoP(double vencidoP) {
		this.vencidoP = vencidoP;
	}
	public String getReferenciaBanc() {
		return referenciaBanc;
	}
	public void setReferenciaBanc(String referenciaBanc) {
		this.referenciaBanc = referenciaBanc;
	}
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public String getChequera() {
		return chequera;
	}
	public void setChequera(String chequera) {
		this.chequera = chequera;
	}
	public double getVencidoS() {
		return vencidoS;
	}
	public void setVencidoS(double vencidoS) {
		this.vencidoS = vencidoS;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getFactura() {
		return factura;
	}
	public void setFactura(String factura) {
		this.factura = factura;
	}
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}
	public String getNumeroDcoumento() {
		return numeroDcoumento;
	}
	public void setNumeroDcoumento(String numeroDcoumento) {
		this.numeroDcoumento = numeroDcoumento;
	}
	public int getUsuario() {
		return usuario;
	}
	public void setUsuario(int usuario) {
		this.usuario = usuario;
	}
	public int getFolio() {
		return folio;
	}
	public void setFolio(int folio) {
		this.folio = folio;
	}
	public double getImporteNeto() {
		return importeNeto;
	}
	public void setImporteNeto(double importeNeto) {
		this.importeNeto = importeNeto;
	}
	public String getEquivalePersona() {
		return equivalePersona;
	}
	public void setEquivalePersona(String equivalePersona) {
		this.equivalePersona = equivalePersona;
	}
	public String getBeneficiario() {
		return beneficiario;
	}
	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}
	public double getVencido() {
		return vencido;
	}
	public void setVencido(double vencido) {
		this.vencido = vencido;
	}
	public double getPorVencer() {
		return porVencer;
	}
	public void setPorVencer(double porVencer) {
		this.porVencer = porVencer;
	}
	public String getFormaPago() {
		return formaPago;
	}
	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}
	public int getNumeroEmpresa() {
		return numeroEmpresa;
	}
	public void setNumeroEmpresa(int numeroEmpresa) {
		this.numeroEmpresa = numeroEmpresa;
	}
	public String getNombreEmpresa() {
		return nombreEmpresa;
	}
	public void setNombreEmpresa(String nombreEmpresa) {
		this.nombreEmpresa = nombreEmpresa;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public String getClaseDocumento() {
		return claseDocumento;
	}
	public void setClaseDocumento(String claseDocumento) {
		this.claseDocumento = claseDocumento;
	}
	public String getFechaFactura() {
		return fechaFactura;
	}
	public void setFechaFactura(String fechaFactura) {
		this.fechaFactura = fechaFactura;
	}
	
}
