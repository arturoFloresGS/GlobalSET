package com.webset.set.egresos.dto;

import java.util.Date;
/**
 * parametros de busqueda: incluye todos los criterios que se encuentran disponibles
 * en el combo de criterios de busqueda para llenar e grid
 * @author Jessica Arelly Cruz Cruz
 * @since 18/02/2011
 *
 */
public class BusquedaPropuestaPagoManualDto {
	private int 	grupoEmpresa;
	private int 	idUsuario;
	private boolean tipoChequeNE;
	private String 	criterio;
	private String 	psDivision;
	/**criterios**/
	private int 	bancoReceptor;
	private int 	caja;
	private int 	bancoPagador;
	private int 	claveOperacion;
	private int 	noPedido;
	private int 	formaPago;
	private double 	monto1;
	private double 	monto2;
	private Date 	fecha1;
	private Date 	fecha2;
	private String 	estatus;
	private String 	concepto;
	private String 	loteEntrada;
	private String 	division;
	private String 	bloqueo;
	private String 	noProveedor1;
	private String 	noProveedor2;
	private String 	factura;
	private String 	origenMovimiento;
	private String 	rubroMovimiento;
	private String 	chequeraBenef;
	private String 	chequeraPagadora;
	private String 	noDocumentoIni;
	private String 	noDocumentoFin;
	private String 	claseDocumento;
	private String 	nombreProveedor;
	private String 	divisa;
	public int getGrupoEmpresa() {
		return grupoEmpresa;
	}
	public void setGrupoEmpresa(int grupoEmpresa) {
		this.grupoEmpresa = grupoEmpresa;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public boolean isTipoChequeNE() {
		return tipoChequeNE;
	}
	public void setTipoChequeNE(boolean tipoChequeNE) {
		this.tipoChequeNE = tipoChequeNE;
	}
	public String getCriterio() {
		return criterio;
	}
	public void setCriterio(String criterio) {
		this.criterio = criterio;
	}
	public String getPsDivision() {
		return psDivision;
	}
	public void setPsDivision(String psDivision) {
		this.psDivision = psDivision;
	}
	public int getBancoReceptor() {
		return bancoReceptor;
	}
	public void setBancoReceptor(int bancoReceptor) {
		this.bancoReceptor = bancoReceptor;
	}
	public int getCaja() {
		return caja;
	}
	public void setCaja(int caja) {
		this.caja = caja;
	}
	public int getBancoPagador() {
		return bancoPagador;
	}
	public void setBancoPagador(int bancoPagador) {
		this.bancoPagador = bancoPagador;
	}
	public int getClaveOperacion() {
		return claveOperacion;
	}
	public void setClaveOperacion(int claveOperacion) {
		this.claveOperacion = claveOperacion;
	}
	public int getNoPedido() {
		return noPedido;
	}
	public void setNoPedido(int noPedido) {
		this.noPedido = noPedido;
	}
	public int getFormaPago() {
		return formaPago;
	}
	public void setFormaPago(int formaPago) {
		this.formaPago = formaPago;
	}
	public double getMonto1() {
		return monto1;
	}
	public void setMonto1(double monto1) {
		this.monto1 = monto1;
	}
	public double getMonto2() {
		return monto2;
	}
	public void setMonto2(double monto2) {
		this.monto2 = monto2;
	}
	public Date getFecha1() {
		return fecha1;
	}
	public void setFecha1(Date fecha1) {
		this.fecha1 = fecha1;
	}
	public Date getFecha2() {
		return fecha2;
	}
	public void setFecha2(Date fecha2) {
		this.fecha2 = fecha2;
	}
	public String getEstatus() {
		return estatus;
	}
	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public String getLoteEntrada() {
		return loteEntrada;
	}
	public void setLoteEntrada(String loteEntrada) {
		this.loteEntrada = loteEntrada;
	}
	public String getDivision() {
		return division;
	}
	public void setDivision(String division) {
		this.division = division;
	}
	public String getBloqueo() {
		return bloqueo;
	}
	public void setBloqueo(String bloqueo) {
		this.bloqueo = bloqueo;
	}
	public String getFactura() {
		return factura;
	}
	public void setFactura(String factura) {
		this.factura = factura;
	}
	public String getOrigenMovimiento() {
		return origenMovimiento;
	}
	public void setOrigenMovimiento(String origenMovimiento) {
		this.origenMovimiento = origenMovimiento;
	}
	public String getRubroMovimiento() {
		return rubroMovimiento;
	}
	public void setRubroMovimiento(String rubroMovimiento) {
		this.rubroMovimiento = rubroMovimiento;
	}
	public String getChequeraBenef() {
		return chequeraBenef;
	}
	public void setChequeraBenef(String chequeraBenef) {
		this.chequeraBenef = chequeraBenef;
	}
	public String getChequeraPagadora() {
		return chequeraPagadora;
	}
	public void setChequeraPagadora(String chequeraPagadora) {
		this.chequeraPagadora = chequeraPagadora;
	}
	public String getClaseDocumento() {
		return claseDocumento;
	}
	public void setClaseDocumento(String claseDocumento) {
		this.claseDocumento = claseDocumento;
	}
	public String getNombreProveedor() {
		return nombreProveedor;
	}
	public void setNombreProveedor(String nombreProveedor) {
		this.nombreProveedor = nombreProveedor;
	}
	public String getDivisa() {
		return divisa;
	}
	public void setDivisa(String divisa) {
		this.divisa = divisa;
	}
	public String getNoDocumentoIni() {
		return noDocumentoIni;
	}
	public void setNoDocumentoIni(String noDocumentoIni) {
		this.noDocumentoIni = noDocumentoIni;
	}
	public String getNoDocumentoFin() {
		return noDocumentoFin;
	}
	public void setNoDocumentoFin(String noDocumentoFin) {
		this.noDocumentoFin = noDocumentoFin;
	}
	public String getNoProveedor1() {
		return noProveedor1;
	}
	public void setNoProveedor1(String noProveedor1) {
		this.noProveedor1 = noProveedor1;
	}
	public String getNoProveedor2() {
		return noProveedor2;
	}
	public void setNoProveedor2(String noProveedor2) {
		this.noProveedor2 = noProveedor2;
	}
}
