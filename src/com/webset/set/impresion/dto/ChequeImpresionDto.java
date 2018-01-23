package com.webset.set.impresion.dto;

/**
 * 
 * @author Eric
 * @since 03/03/2016
 * @see Clase para mandar los datos de los cheques a imprimir, se genera esta clase
 * 		ya que hay datos que se pueden repetir con diferente formato como puede ser importe,
 * 		y no agregarlos al DTO de Movimiento. 
 */

public class ChequeImpresionDto {

	private String importe;
	private String importe2;
	private String importe3;
	private String importe4;
	private String importeLetra;
	private String beneficiario;
	private String beneficiario2;
	private String noAcreedor;
	private String bancoPagador; //Es el nombre del banco.
	private String chequeraBeneficiaria;
	private String chequeraPagadora;
	private String noCheque;
	private String noCheque2;
	private String fechaCheque;
	private String fechaCheque2;
	private String divisaOriginal;
	private String documentoCompensacion;
	private String cuentaContable;
	private String concepto;
	private String concepto2;
	private String leyenda;
	private String empresaPagadora;
	private String nombreEmpresa;
	private String noDocto;
	private String noDocto2;
	private String factura;
	private String tamanoHoja;
	
	public String getImporte() {
		return importe;
	}
	public void setImporte(String importe) {
		this.importe = importe;
	}
	public String getImporte2() {
		return importe2;
	}
	public void setImporte2(String importe2) {
		this.importe2 = importe2;
	}
	public String getImporte3() {
		return importe3;
	}
	public void setImporte3(String importe3) {
		this.importe3 = importe3;
	}
	public String getImporte4() {
		return importe4;
	}
	public void setImporte4(String importe4) {
		this.importe4 = importe4;
	}
	public String getImporteLetra() {
		return importeLetra;
	}
	public void setImporteLetra(String importeLetra) {
		this.importeLetra = importeLetra;
	}
	public String getBeneficiario() {
		return beneficiario;
	}
	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}
	public String getBeneficiario2() {
		return beneficiario2;
	}
	public void setBeneficiario2(String beneficiario2) {
		this.beneficiario2 = beneficiario2;
	}
	public String getNoAcreedor() {
		return noAcreedor;
	}
	public void setNoAcreedor(String noAcreedor) {
		this.noAcreedor = noAcreedor;
	}
	public String getBancoPagador() {
		return bancoPagador;
	}
	public void setBancoPagador(String bancoPagador) {
		this.bancoPagador = bancoPagador;
	}
	public String getChequeraBeneficiaria() {
		return chequeraBeneficiaria;
	}
	public void setChequeraBeneficiaria(String chequeraBeneficiaria) {
		this.chequeraBeneficiaria = chequeraBeneficiaria;
	}
	public String getChequeraPagadora() {
		return chequeraPagadora;
	}
	public void setChequeraPagadora(String chequeraPagadora) {
		this.chequeraPagadora = chequeraPagadora;
	}
	public String getNoCheque() {
		return noCheque;
	}
	public void setNoCheque(String noCheque) {
		this.noCheque = noCheque;
	}
	public String getNoCheque2() {
		return noCheque2;
	}
	public void setNoCheque2(String noCheque2) {
		this.noCheque2 = noCheque2;
	}
	public String getFechaCheque() {
		return fechaCheque;
	}
	public void setFechaCheque(String fechaCheque) {
		this.fechaCheque = fechaCheque;
	}
	public String getFechaCheque2() {
		return fechaCheque2;
	}
	public void setFechaCheque2(String fechaCheque2) {
		this.fechaCheque2 = fechaCheque2;
	}
	public String getDivisaOriginal() {
		return divisaOriginal;
	}
	public void setDivisaOriginal(String divisaOriginal) {
		this.divisaOriginal = divisaOriginal;
	}
	public String getDocumentoCompensacion() {
		return documentoCompensacion;
	}
	public void setDocumentoCompensacion(String documentoCompensacion) {
		this.documentoCompensacion = documentoCompensacion;
	}
	public String getCuentaContable() {
		return cuentaContable;
	}
	public void setCuentaContable(String cuentaContable) {
		this.cuentaContable = cuentaContable;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public String getConcepto2() {
		return concepto2;
	}
	public void setConcepto2(String concepto2) {
		this.concepto2 = concepto2;
	}
	public String getLeyenda() {
		return leyenda;
	}
	public void setLeyenda(String leyenda) {
		this.leyenda = leyenda;
	}
	public String getEmpresaPagadora() {
		return empresaPagadora;
	}
	public void setEmpresaPagadora(String empresaPagadora) {
		this.empresaPagadora = empresaPagadora;
	}
	public String getNombreEmpresa() {
		return nombreEmpresa;
	}
	public void setNombreEmpresa(String nombreEmpresa) {
		this.nombreEmpresa = nombreEmpresa;
	}
	public String getNoDocto() {
		return noDocto;
	}
	public void setNoDocto(String noDocto) {
		this.noDocto = noDocto;
	}
	public String getNoDocto2() {
		return noDocto2;
	}
	public void setNoDocto2(String noDocto2) {
		this.noDocto2 = noDocto2;
	}
	public String getFactura() {
		return factura;
	}
	public void setFactura(String factura) {
		this.factura = factura;
	}
	public String getTamanoHoja() {
		return tamanoHoja;
	}
	public void setTamanoHoja(String tamanoHoja) {
		this.tamanoHoja = tamanoHoja;
	}
			  
}
