/**
 * 
 */
package com.webset.set.bancaelectronica.dto;

/**
 * @author Eduardo Ayala
 * @since 20/Octubre/2010
 * @see <p>Tabla REGLA_CONCEPTO</p>
 */
public class ReglaConceptoDto {
	
	/** 
	 * Este atributo mapea la columna INGRESO_EGRESO en la tabla REGLA_CONCEPTO.
	 */
	private String ingresoEgreso;

	/** 
	 * Este atributo mapea la columna ID_FORMA_PAGO en la tabla REGLA_CONCEPTO.
	 */
	private int idFormaPago;

	/** 
	 * Este atributo mapea la columna DESC_FORMA_PAGO en la tabla REGLA_CONCEPTO.
	 */
	private String descFormaPago;

	/** 
	 * Este atributo mapea la columna ID_TIPO_OPERACION en la tabla REGLA_CONCEPTO.
	 */
	private int idTipoOperacion;

	/** 
	 * Este atributo mapea la columna ORIGEN_MOV en la tabla REGLA_CONCEPTO.
	 */
	private String origenMov;

	/** 
	 * Este atributo mapea la columna CONCEPTO_SET en la tabla REGLA_CONCEPTO.
	 */
	private String conceptoSet;

	/** 
	 * Este atributo mapea la columna B_SALVO_BUEN_COBRO en la tabla REGLA_CONCEPTO.
	 */
	private String bSalvoBuenCobro;

	/** 
	 * Este atributo mapea la columna ID_CORRESPONDE en la tabla REGLA_CONCEPTO.
	 */
	private String idCorresponde;

	/** 
	 * Este atributo mapea la columna B_USA_IMPORTA en la tabla REGLA_CONCEPTO.
	 */
	private String bUsaImporta;

	
	/**
	 * 
	 */
	public ReglaConceptoDto() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * @return ingresoEgreso
	 */
	public String getIngresoEgreso() {
		return ingresoEgreso;
	}

	/**
	 * @param ingresoEgreso asigna el valor a ingresoEgreso
	 */
	public void setIngresoEgreso(String ingresoEgreso) {
		this.ingresoEgreso = ingresoEgreso;
	}

	/**
	 * @return idFormaPago
	 */
	public int getIdFormaPago() {
		return idFormaPago;
	}

	/**
	 * @param idFormaPago asigna el valor a idFormaPago
	 */
	public void setIdFormaPago(int idFormaPago) {
		this.idFormaPago = idFormaPago;
	}

	/**
	 * @return descFormaPago
	 */
	public String getDescFormaPago() {
		return descFormaPago;
	}

	/**
	 * @param descFormaPago asigna el valor a descFormaPago
	 */
	public void setDescFormaPago(String descFormaPago) {
		this.descFormaPago = descFormaPago;
	}

	/**
	 * @return idTipoOperacion
	 */
	public int getIdTipoOperacion() {
		return idTipoOperacion;
	}

	/**
	 * @param idTipoOperacion asigna el valor a idTipoOperacion
	 */
	public void setIdTipoOperacion(int idTipoOperacion) {
		this.idTipoOperacion = idTipoOperacion;
	}

	/**
	 * @return origenMov
	 */
	public String getOrigenMov() {
		return origenMov;
	}

	/**
	 * @param origenMov asigna el valor a origenMov
	 */
	public void setOrigenMov(String origenMov) {
		this.origenMov = origenMov;
	}

	/**
	 * @return conceptoSet
	 */
	public String getConceptoSet() {
		return conceptoSet;
	}

	/**
	 * @param conceptoSet asigna el valor a conceptoSet
	 */
	public void setConceptoSet(String conceptoSet) {
		this.conceptoSet = conceptoSet;
	}

	/**
	 * @return bSalvoBuenCobro
	 */
	public String getBSalvoBuenCobro() {
		return bSalvoBuenCobro;
	}

	/**
	 * @param salvoBuenCobro asigna el valor a bSalvoBuenCobro
	 */
	public void setBSalvoBuenCobro(String salvoBuenCobro) {
		bSalvoBuenCobro = salvoBuenCobro;
	}

	/**
	 * @return idCorresponde
	 */
	public String getIdCorresponde() {
		return idCorresponde;
	}

	/**
	 * @param idCorresponde asigna el valor a idCorresponde
	 */
	public void setIdCorresponde(String idCorresponde) {
		this.idCorresponde = idCorresponde;
	}

	/**
	 * @return bUsaImporta
	 */
	public String getBUsaImporta() {
		return bUsaImporta;
	}

	/**
	 * @param usaImporta asigna el valor a bUsaImporta
	 */
	public void setBUsaImporta(String usaImporta) {
		bUsaImporta = usaImporta;
	}
	
	/**
	 * Imprime el valor de todas las variables del objeto.
	 * @return String
	 */
	public String toString()
	{
		StringBuffer ret = new StringBuffer();
		ret.append( "com.webset.set.dto.bancaelectronica.ReglaConcepto: " );
		ret.append( "ingresoEgreso=" + ingresoEgreso );
		ret.append( ", idFormaPago=" + idFormaPago );
		ret.append( ", descFormaPago=" + descFormaPago );
		ret.append( ", idTipoOperacion=" + idTipoOperacion );
		ret.append( ", origenMov=" + origenMov );
		ret.append( ", conceptoSet=" + conceptoSet );
		ret.append( ", bSalvoBuenCobro=" + bSalvoBuenCobro );
		ret.append( ", idCorresponde=" + idCorresponde );
		ret.append( ", bUsaImporta=" + bUsaImporta );
		return ret.toString();
	}	

}
