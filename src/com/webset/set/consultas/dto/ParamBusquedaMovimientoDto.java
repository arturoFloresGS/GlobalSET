package com.webset.set.consultas.dto;

/**
 * 
 * @author Cristian García García
 * @since 01/Sept/2011
 *
 */
public class ParamBusquedaMovimientoDto{
	private int idBanco;
	private int idProveedor;
	private int idCaja;
	private int idDivision;
	private int folioMovimiento;
	private int idFormaPago;
	private int noCheque;
	private int noFactura;
	private int tipoOperacion;
	private int idContrato;
	private int noContrato;
	private int noPersona;
	private int noPagos;
	private int idUsuario;
	
	private double montoInicial;
	private double montoFinal;
	private double montoOriginal;
	private double montoPagado;
	private double montoAdeudo;
	private double saldoInicial;
	private double saldoFinal;
	private double cargo;
	private double abono;
	
	private String idChequera;
	private String concepto;
	private String idDivisa;
	private String estatus;
	private String cvePropuesta;
	private String noDocto;
	private String origen;
	private String valorCustodia;
	private String fechaInicial;
	private String fechaFinal;
	private String beneficiario;
	private String razonSocial;
	private String descContrato;
	private String noContrato2;
	private String nomUsuario;
	private String descBanco;
	private String descChequera;
	
	private boolean optEmp;
	private boolean optMovto;
	
	private int folioDet;
	private boolean bSoloFolioDet;
	private int iTalblaInicio;
	private int iTablaFin;
	private boolean bReporte;
	private boolean bEncReporte;
	private int iHist;
	private String sEmpresas;
	private int noEmpresa;
	private String pooHeaders;
	
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public int getIdProveedor() {
		return idProveedor;
	}
	public void setIdProveedor(int idProveedor) {
		this.idProveedor = idProveedor;
	}
	public int getIdCaja() {
		return idCaja;
	}
	public void setIdCaja(int idCaja) {
		this.idCaja = idCaja;
	}
	public int getIdDivision() {
		return idDivision;
	}
	public void setIdDivision(int idDivision) {
		this.idDivision = idDivision;
	}
	public int getFolioMovimiento() {
		return folioMovimiento;
	}
	public void setFolioMovimiento(int folioMovimiento) {
		this.folioMovimiento = folioMovimiento;
	}
	public int getIdFormaPago() {
		return idFormaPago;
	}
	public void setIdFormaPago(int idFormaPago) {
		this.idFormaPago = idFormaPago;
	}
	public int getNoCheque() {
		return noCheque;
	}
	public void setNoCheque(int noCheque) {
		this.noCheque = noCheque;
	}
	public int getNoFactura() {
		return noFactura;
	}
	public void setNoFactura(int noFactura) {
		this.noFactura = noFactura;
	}
	public int getTipoOperacion() {
		return tipoOperacion;
	}
	public void setTipoOperacion(int tipoOperacion) {
		this.tipoOperacion = tipoOperacion;
	}
	public double getMontoInicial() {
		return montoInicial;
	}
	public void setMontoInicial(double montoInicial) {
		this.montoInicial = montoInicial;
	}
	public double getMontoFinal() {
		return montoFinal;
	}
	public void setMontoFinal(double montoFinal) {
		this.montoFinal = montoFinal;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public String getEstatus() {
		return estatus;
	}
	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}
	public String getCvePropuesta() {
		return cvePropuesta;
	}
	public void setCvePropuesta(String cvePropuesta) {
		this.cvePropuesta = cvePropuesta;
	}
	public String getNoDocto() {
		return noDocto;
	}
	public void setNoDocto(String noDocto) {
		this.noDocto = noDocto;
	}
	public String getOrigen() {
		return origen;
	}
	public void setOrigen(String origen) {
		this.origen = origen;
	}
	public String getValorCustodia() {
		return valorCustodia;
	}
	public void setValorCustodia(String valorCustodia) {
		this.valorCustodia = valorCustodia;
	}
	public String getFechaInicial() {
		return fechaInicial;
	}
	public void setFechaInicial(String fechaInicial) {
		this.fechaInicial = fechaInicial;
	}
	public String getFechaFinal() {
		return fechaFinal;
	}
	public void setFechaFinal(String fechaFinal) {
		this.fechaFinal = fechaFinal;
	}
	public boolean isOptEmp() {
		return optEmp;
	}
	public void setOptEmp(boolean optEmp) {
		this.optEmp = optEmp;
	}
	public boolean isOptMovto() {
		return optMovto;
	}
	public void setOptMovto(boolean optMovto) {
		this.optMovto = optMovto;
	}
	public int getFolioDet() {
		return folioDet;
	}
	public void setFolioDet(int folioDet) {
		this.folioDet = folioDet;
	}
	public int getITalblaInicio() {
		return iTalblaInicio;
	}
	public void setITalblaInicio(int talblaInicio) {
		iTalblaInicio = talblaInicio;
	}
	public int getITablaFin() {
		return iTablaFin;
	}
	public void setITablaFin(int tablaFin) {
		iTablaFin = tablaFin;
	}
	public boolean isBReporte() {
		return bReporte;
	}
	public void setBReporte(boolean reporte) {
		bReporte = reporte;
	}
	public boolean isBSoloFolioDet() {
		return bSoloFolioDet;
	}
	public void setBSoloFolioDet(boolean soloFolioDet) {
		bSoloFolioDet = soloFolioDet;
	}
	public int getIHist() {
		return iHist;
	}
	public void setIHist(int hist) {
		iHist = hist;
	}
	public boolean isBEncReporte() {
		return bEncReporte;
	}
	public void setBEncReporte(boolean encReporte) {
		bEncReporte = encReporte;
	}
	public String getSEmpresas() {
		return sEmpresas;
	}
	public void setSEmpresas(String empresas) {
		sEmpresas = empresas;
	}
	public String getBeneficiario() {
		return beneficiario;
	}
	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public int getIdContrato(){
		return idContrato;
	}
	public void setIdContrato(int idContrato){
		this.idContrato = idContrato;
	}
	public String getRazonSocial(){
		return razonSocial;
	}
	public void setRazonSocial(String razonSocial){
		this.razonSocial = razonSocial;
	}
	public int getNoContrato(){
		return noContrato;
	}
	public void setNoContrato(int noContrato){
		this.noContrato = noContrato;
	}
	public String getDescContrato(){
		return descContrato;
	}
	public void setDescContrato(String descContrato){
		this.descContrato = descContrato;
	}
	public double getMontoOriginal(){
		return montoOriginal;
	}
	public void setMontoOriginal(double montoOriginal){
		this.montoOriginal = montoOriginal;
	}
	public double getMontoPagado(){
		return montoPagado;
	}
	public void setMontoPagado(double montoPagado){
		this.montoPagado = montoPagado;
	}
	public double getMontoAdeudo(){
		return montoAdeudo;
	}
	public void setMontoAdeudo(double montoAdeudo){
		this.montoAdeudo = montoAdeudo;
	}
	public int getNoPersona(){
		return noPersona;
	}
	public void setNoPersona(int noPersona){
		this.noPersona = noPersona;
	}
	public int getNoPagos(){
		return noPagos;
	}
	public void setNoPagos(int noPagos){
		this.noPagos = noPagos;
	}
	public String getNoContrato2(){
		return noContrato2;
	}
	public void setNoContrato2(String noContrato2){
		this.noContrato2= noContrato2;
	}
	public int getIdUsuario(){
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario){
		this.idUsuario = idUsuario;
	}
	public String getNomUsuario(){
		return nomUsuario;
	}
	public void setNomUsuario(String nomUsuario){
		this.nomUsuario = nomUsuario;
	}
	public String getDescBanco(){
		return descBanco;
	}
	public void setDescBanco(String descBanco){
		this.descBanco = descBanco;
	}
	public String getDescChequera(){
		return descChequera;
	}
	public void setDescChequera(String descChequera){
		this.descChequera = descChequera;
	}
	public Double getSaldoInicial(){
		return saldoInicial;
	}
	public void setSaldoInicial(Double saldoInicial){
		this.saldoInicial = saldoInicial;
	}
	public Double getSaldoFinal(){
		return saldoFinal;
	}
	public void setSaldoFinal(Double saldoFinal){
		this.saldoFinal = saldoFinal;
	}
	public Double getCargo(){
		return cargo;
	}
	public void setCargo(Double cargo){
		this.cargo = cargo;
	}
	public Double getAbono(){
		return abono;
	}
	public void setAbono(Double abono){
		this.abono = abono;
	}
	
	public String getPooHeaders() {
		return pooHeaders;
	}
	public void setPooHeaders(String pooHeaders) {
		this.pooHeaders = pooHeaders;
	}
}
