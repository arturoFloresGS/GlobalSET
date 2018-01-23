/**
 * 
 */
package com.webset.set.bancaelectronica.dto;

import java.util.Date;

/**
 * @author Eduardo Ayala
 * @since 14/Octubre/2010
 * @see <p>Tabla MOVTO_CONTA</p>
 */
public class MovtoConta {
	
	/** 
	 * Este atributo mapea la columna NO_EMPRESA en la tabla MOVTO_CONTA.
	 */
	private short noEmpresa;

	/** 
	 * Este atributo mapea la columna ID_BANCO en la tabla MOVTO_CONTA.
	 */
	private short idBanco;

	/** 
	 * Este atributo mapea la columna ID_CHEQUERA en la tabla MOVTO_CONTA.
	 */
	private String idChequera;

	/** 
	 * Este atributo mapea la columna SECUENCIA en la tabla MOVTO_CONTA.
	 */
	private long secuencia;

	/** 
	 * Este atributo mapea la columna POLIZA en la tabla MOVTO_CONTA.
	 */
	private String poliza;

	/** 
	 * Este atributo mapea la columna FEC_VALOR en la tabla MOVTO_CONTA.
	 */
	private Date fecValor;

	/** 
	 * Este atributo mapea la columna REFERENCIA2 en la tabla MOVTO_CONTA.
	 */
	private String referencia2;

	/** 
	 * Este atributo mapea la columna REFERENCIA en la tabla MOVTO_CONTA.
	 */
	private String referencia;

	/** 
	 * Este atributo mapea la columna CARGO_ABONO en la tabla MOVTO_CONTA.
	 */
	private String cargoAbono;

	/** 
	 * Este atributo mapea la columna IMPORTE_ORIGINAL en la tabla MOVTO_CONTA.
	 */
	private double importeOriginal;

	/** 
	 * This attribute represents whether the primitive attribute importeOriginal is null.
	 */
	private boolean importeOriginalNull = true;

	/** 
	 * Este atributo mapea la columna IMPORTE en la tabla MOVTO_CONTA.
	 */
	private double importe;

	/** 
	 * This attribute represents whether the primitive attribute importe is null.
	 */
	private boolean importeNull = true;

	/** 
	 * Este atributo mapea la columna ID_DIVISA en la tabla MOVTO_CONTA.
	 */
	private String idDivisa;

	/** 
	 * Este atributo mapea la columna CONCEPTO en la tabla MOVTO_CONTA.
	 */
	private String concepto;

	/** 
	 * Este atributo mapea la columna ORIGEN en la tabla MOVTO_CONTA.
	 */
	private String origen;

	/** 
	 * Este atributo mapea la columna B_TRASP_CONTA en la tabla MOVTO_CONTA.
	 */
	private String bTraspConta;

	/** 
	 * Este atributo mapea la columna COD_DIARIO en la tabla MOVTO_CONTA.
	 */
	private double codDiario;

	/** 
	 * Este atributo mapea la columna FEC_ALTA en la tabla MOVTO_CONTA.
	 */
	private Date fecAlta;

	/** 
	 * Este atributo mapea la columna USUARIO_ALTA en la tabla MOVTO_CONTA.
	 */
	private short usuarioAlta;

	/** 
	 * Este atributo mapea la columna NO_POLIZA en la tabla MOVTO_CONTA.
	 */
	private int noPoliza;

	/** 
	 * Este atributo mapea la columna CVE_DOCTO en la tabla MOVTO_CONTA.
	 */
	private int cveDocto;

	/** 
	 * Este atributo mapea la columna B_TRASP_BC en la tabla MOVTO_CONTA.
	 */
	private String bTraspBc;

	/** 
	 * Este atributo mapea la columna LOTE en la tabla MOVTO_CONTA.
	 */
	private int lote;

	/** 
	 * Este atributo mapea la columna CUENTA en la tabla MOVTO_CONTA.
	 */
	private String cuenta;

	/** 
	 * Este atributo mapea la columna SCUENTA en la tabla MOVTO_CONTA.
	 */
	private String scuenta;

	/** 
	 * Este atributo mapea la columna SSCUENTA en la tabla MOVTO_CONTA.
	 */
	private String sscuenta;

	/** 
	 * Este atributo mapea la columna USUARIO_ERP en la tabla MOVTO_CONTA.
	 */
	private String usuarioErp;

	/** 
	 * Este atributo mapea la columna TIPO_DOC en la tabla MOVTO_CONTA.
	 */
	private String tipoDoc;

	/** 
	 * Este atributo mapea la columna NO_DOC_ANULADO en la tabla MOVTO_CONTA.
	 */
	private String noDocAnulado;

	/** 
	 * Este atributo mapea la columna FEC_DOCTO en la tabla MOVTO_CONTA.
	 */
	private Date fecDocto;

	/** 
	 * Este atributo mapea la columna PERIODO en la tabla MOVTO_CONTA.
	 */
	private short periodo;

	/**
	 * Constructor MovtoConta
	 * 
	 */
	public MovtoConta()
	{
	}

	/**
	 * @return noEmpresa
	 */
	public short getNoEmpresa() {
		return noEmpresa;
	}

	/**
	 * @param noEmpresa asigna el valor a noEmpresa
	 */
	public void setNoEmpresa(short noEmpresa) {
		this.noEmpresa = noEmpresa;
	}

	/**
	 * @return idBanco
	 */
	public short getIdBanco() {
		return idBanco;
	}

	/**
	 * @param idBanco asigna el valor a idBanco
	 */
	public void setIdBanco(short idBanco) {
		this.idBanco = idBanco;
	}

	/**
	 * @return idChequera
	 */
	public String getIdChequera() {
		return idChequera;
	}

	/**
	 * @param idChequera asigna el valor a idChequera
	 */
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}

	/**
	 * @return secuencia
	 */
	public long getSecuencia() {
		return secuencia;
	}

	/**
	 * @param secuencia asigna el valor a secuencia
	 */
	public void setSecuencia(long secuencia) {
		this.secuencia = secuencia;
	}

	/**
	 * @return poliza
	 */
	public String getPoliza() {
		return poliza;
	}

	/**
	 * @param poliza asigna el valor a poliza
	 */
	public void setPoliza(String poliza) {
		this.poliza = poliza;
	}

	/**
	 * @return fecValor
	 */
	public Date getFecValor() {
		return fecValor;
	}

	/**
	 * @param fecValor asigna el valor a fecValor
	 */
	public void setFecValor(Date fecValor) {
		this.fecValor = fecValor;
	}

	/**
	 * @return referencia2
	 */
	public String getReferencia2() {
		return referencia2;
	}

	/**
	 * @param referencia2 asigna el valor a referencia2
	 */
	public void setReferencia2(String referencia2) {
		this.referencia2 = referencia2;
	}

	/**
	 * @return referencia
	 */
	public String getReferencia() {
		return referencia;
	}

	/**
	 * @param referencia asigna el valor a referencia
	 */
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	/**
	 * @return cargoAbono
	 */
	public String getCargoAbono() {
		return cargoAbono;
	}

	/**
	 * @param cargoAbono asigna el valor a cargoAbono
	 */
	public void setCargoAbono(String cargoAbono) {
		this.cargoAbono = cargoAbono;
	}

	/**
	 * @return importeOriginal
	 */
	public double getImporteOriginal() {
		return importeOriginal;
	}

	/**
	 * @param importeOriginal asigna el valor a importeOriginal
	 */
	public void setImporteOriginal(double importeOriginal) {
		this.importeOriginal = importeOriginal;
	}

	/**
	 * @return importeOriginalNull
	 */
	public boolean isImporteOriginalNull() {
		return importeOriginalNull;
	}

	/**
	 * @param importeOriginalNull asigna el valor a importeOriginalNull
	 */
	public void setImporteOriginalNull(boolean importeOriginalNull) {
		this.importeOriginalNull = importeOriginalNull;
	}

	/**
	 * @return importe
	 */
	public double getImporte() {
		return importe;
	}

	/**
	 * @param importe asigna el valor a importe
	 */
	public void setImporte(double importe) {
		this.importe = importe;
	}

	/**
	 * @return importeNull
	 */
	public boolean isImporteNull() {
		return importeNull;
	}

	/**
	 * @param importeNull asigna el valor a importeNull
	 */
	public void setImporteNull(boolean importeNull) {
		this.importeNull = importeNull;
	}

	/**
	 * @return idDivisa
	 */
	public String getIdDivisa() {
		return idDivisa;
	}

	/**
	 * @param idDivisa asigna el valor a idDivisa
	 */
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}

	/**
	 * @return concepto
	 */
	public String getConcepto() {
		return concepto;
	}

	/**
	 * @param concepto asigna el valor a concepto
	 */
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	/**
	 * @return origen
	 */
	public String getOrigen() {
		return origen;
	}

	/**
	 * @param origen asigna el valor a origen
	 */
	public void setOrigen(String origen) {
		this.origen = origen;
	}

	/**
	 * @return bTraspConta
	 */
	public String getBTraspConta() {
		return bTraspConta;
	}

	/**
	 * @param traspConta asigna el valor a bTraspConta
	 */
	public void setBTraspConta(String traspConta) {
		bTraspConta = traspConta;
	}

	/**
	 * @return codDiario
	 */
	public double getCodDiario() {
		return codDiario;
	}

	/**
	 * @param codDiario asigna el valor a codDiario
	 */
	public void setCodDiario(double codDiario) {
		this.codDiario = codDiario;
	}

	/**
	 * @return fecAlta
	 */
	public Date getFecAlta() {
		return fecAlta;
	}

	/**
	 * @param fecAlta asigna el valor a fecAlta
	 */
	public void setFecAlta(Date fecAlta) {
		this.fecAlta = fecAlta;
	}

	/**
	 * @return usuarioAlta
	 */
	public short getUsuarioAlta() {
		return usuarioAlta;
	}

	/**
	 * @param usuarioAlta asigna el valor a usuarioAlta
	 */
	public void setUsuarioAlta(short usuarioAlta) {
		this.usuarioAlta = usuarioAlta;
	}

	/**
	 * @return noPoliza
	 */
	public int getNoPoliza() {
		return noPoliza;
	}

	/**
	 * @param noPoliza asigna el valor a noPoliza
	 */
	public void setNoPoliza(int noPoliza) {
		this.noPoliza = noPoliza;
	}

	/**
	 * @return cveDocto
	 */
	public int getCveDocto() {
		return cveDocto;
	}

	/**
	 * @param cveDocto asigna el valor a cveDocto
	 */
	public void setCveDocto(int cveDocto) {
		this.cveDocto = cveDocto;
	}

	/**
	 * @return bTraspBc
	 */
	public String getBTraspBc() {
		return bTraspBc;
	}

	/**
	 * @param traspBc asigna el valor a bTraspBc
	 */
	public void setBTraspBc(String traspBc) {
		bTraspBc = traspBc;
	}

	/**
	 * @return lote
	 */
	public int getLote() {
		return lote;
	}

	/**
	 * @param lote asigna el valor a lote
	 */
	public void setLote(int lote) {
		this.lote = lote;
	}

	/**
	 * @return cuenta
	 */
	public String getCuenta() {
		return cuenta;
	}

	/**
	 * @param cuenta asigna el valor a cuenta
	 */
	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}

	/**
	 * @return scuenta
	 */
	public String getScuenta() {
		return scuenta;
	}

	/**
	 * @param scuenta asigna el valor a scuenta
	 */
	public void setScuenta(String scuenta) {
		this.scuenta = scuenta;
	}

	/**
	 * @return sscuenta
	 */
	public String getSscuenta() {
		return sscuenta;
	}

	/**
	 * @param sscuenta asigna el valor a sscuenta
	 */
	public void setSscuenta(String sscuenta) {
		this.sscuenta = sscuenta;
	}

	/**
	 * @return usuarioErp
	 */
	public String getUsuarioErp() {
		return usuarioErp;
	}

	/**
	 * @param usuarioErp asigna el valor a usuarioErp
	 */
	public void setUsuarioErp(String usuarioErp) {
		this.usuarioErp = usuarioErp;
	}

	/**
	 * @return tipoDoc
	 */
	public String getTipoDoc() {
		return tipoDoc;
	}

	/**
	 * @param tipoDoc asigna el valor a tipoDoc
	 */
	public void setTipoDoc(String tipoDoc) {
		this.tipoDoc = tipoDoc;
	}

	/**
	 * @return noDocAnulado
	 */
	public String getNoDocAnulado() {
		return noDocAnulado;
	}

	/**
	 * @param noDocAnulado asigna el valor a noDocAnulado
	 */
	public void setNoDocAnulado(String noDocAnulado) {
		this.noDocAnulado = noDocAnulado;
	}

	/**
	 * @return fecDocto
	 */
	public Date getFecDocto() {
		return fecDocto;
	}

	/**
	 * @param fecDocto asigna el valor a fecDocto
	 */
	public void setFecDocto(Date fecDocto) {
		this.fecDocto = fecDocto;
	}

	/**
	 * @return periodo
	 */
	public short getPeriodo() {
		return periodo;
	}

	/**
	 * @param periodo asigna el valor a periodo
	 */
	public void setPeriodo(short periodo) {
		this.periodo = periodo;
	}

	/**
	 * Imprime el valor de todas las variables del objeto.
	 * @return String
	 */
	public String toString()
	{
		StringBuffer ret = new StringBuffer();
		ret.append( "com.webset.set.dto.bancaelectronica.MovtoConta: " );
		ret.append( "noEmpresa=" + noEmpresa );
		ret.append( ", idBanco=" + idBanco );
		ret.append( ", idChequera=" + idChequera );
		ret.append( ", secuencia=" + secuencia );
		ret.append( ", poliza=" + poliza );
		ret.append( ", fecValor=" + fecValor );
		ret.append( ", referencia2=" + referencia2 );
		ret.append( ", referencia=" + referencia );
		ret.append( ", cargoAbono=" + cargoAbono );
		ret.append( ", importeOriginal=" + importeOriginal );
		ret.append( ", importe=" + importe );
		ret.append( ", idDivisa=" + idDivisa );
		ret.append( ", concepto=" + concepto );
		ret.append( ", origen=" + origen );
		ret.append( ", bTraspConta=" + bTraspConta );
		ret.append( ", codDiario=" + codDiario );
		ret.append( ", fecAlta=" + fecAlta );
		ret.append( ", usuarioAlta=" + usuarioAlta );
		ret.append( ", noPoliza=" + noPoliza );
		ret.append( ", cveDocto=" + cveDocto );
		ret.append( ", bTraspBc=" + bTraspBc );
		ret.append( ", lote=" + lote );
		ret.append( ", cuenta=" + cuenta );
		ret.append( ", scuenta=" + scuenta );
		ret.append( ", sscuenta=" + sscuenta );
		ret.append( ", usuarioErp=" + usuarioErp );
		ret.append( ", tipoDoc=" + tipoDoc );
		ret.append( ", noDocAnulado=" + noDocAnulado );
		ret.append( ", fecDocto=" + fecDocto );
		ret.append( ", periodo=" + periodo );
		return ret.toString();
	}	
	
	
}
