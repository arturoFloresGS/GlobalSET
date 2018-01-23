/**
 * 
 */
package com.webset.set.bancaelectronica.dto;

/**
 * @author Eduardo Ayala
 * @since 20/Octubre/2010
 * @see <p>Tabla REGLA_REPARA</p>
 */
public class ReglaReparaDto {

	/** 
	 * Este atributo mapea la columna ID_TIPO_OPERACION_ACT en la tabla REGLA_REPARA.
	 */
	private int idTipoOperacionAct;

	/** 
	 * Este atributo mapea la columna SECUENCIA en la tabla REGLA_REPARA.
	 */
	private int secuencia;

	/** 
	 * Este atributo mapea la columna ID_CORRESPONDE en la tabla REGLA_REPARA.
	 */
	private String idCorresponde;

	/** 
	 * Este atributo mapea la columna VAL_REPARA1 en la tabla REGLA_REPARA.
	 */
	private String valRepara1;

	/** 
	 * Este atributo mapea la columna VAL_REPARA2 en la tabla REGLA_REPARA.
	 */
	private String valRepara2;

	/** 
	 * Este atributo mapea la columna ID_TIPO_OPERACION_NVA en la tabla REGLA_REPARA.
	 */
	private int idTipoOperacionNva;	
	
	
	public ReglaReparaDto() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * @return idTipoOperacionAct
	 */
	public int getIdTipoOperacionAct() {
		return idTipoOperacionAct;
	}

	/**
	 * @param idTipoOperacionAct asigna el valor a idTipoOperacionAct
	 */
	public void setIdTipoOperacionAct(int idTipoOperacionAct) {
		this.idTipoOperacionAct = idTipoOperacionAct;
	}

	/**
	 * @return secuencia
	 */
	public int getSecuencia() {
		return secuencia;
	}

	/**
	 * @param secuencia asigna el valor a secuencia
	 */
	public void setSecuencia(int secuencia) {
		this.secuencia = secuencia;
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
	 * @return valRepara1
	 */
	public String getValRepara1() {
		return valRepara1;
	}

	/**
	 * @param valRepara1 asigna el valor a valRepara1
	 */
	public void setValRepara1(String valRepara1) {
		this.valRepara1 = valRepara1;
	}

	/**
	 * @return valRepara2
	 */
	public String getValRepara2() {
		return valRepara2;
	}

	/**
	 * @param valRepara2 asigna el valor a valRepara2
	 */
	public void setValRepara2(String valRepara2) {
		this.valRepara2 = valRepara2;
	}

	/**
	 * @return idTipoOperacionNva
	 */
	public int getIdTipoOperacionNva() {
		return idTipoOperacionNva;
	}

	/**
	 * @param idTipoOperacionNva asigna el valor a idTipoOperacionNva
	 */
	public void setIdTipoOperacionNva(int idTipoOperacionNva) {
		this.idTipoOperacionNva = idTipoOperacionNva;
	}

	/**
	 * Imprime el valor de todas las variables del objeto.
	 * @return String
	 */	
	public String toString()
	{
		StringBuffer ret = new StringBuffer();
		ret.append( "com.webset.set.dto.bancaelectronica.ReglaRepara: " );
		ret.append( "idTipoOperacionAct=" + idTipoOperacionAct );
		ret.append( ", secuencia=" + secuencia );
		ret.append( ", idCorresponde=" + idCorresponde );
		ret.append( ", valRepara1=" + valRepara1 );
		ret.append( ", valRepara2=" + valRepara2 );
		ret.append( ", idTipoOperacionNva=" + idTipoOperacionNva );
		return ret.toString();
	}	
	
}
