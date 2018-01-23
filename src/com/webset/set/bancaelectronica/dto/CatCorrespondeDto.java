/**
 * 
 */
package com.webset.set.bancaelectronica.dto;

/**
 * @author Eduardo Ayala
 * @since 20/Octubre/2010
 * @see <p>Tabla CAT_CORRESPONDE</p>
 */
public class CatCorrespondeDto {

	/** 
	 * Este atributo mapea la columna ID_CORRESPONDE en la tabla CAT_CORRESPONDE.
	 */
	private String idCorresponde;

	/** 
	 * Este atributo mapea la columna DESC_CORRESPONDE en la tabla CAT_CORRESPONDE table.
	 */
	private String descCorresponde;
	
	
	/**
	 * Constructor CatCorrespondeDto
	 */
	public CatCorrespondeDto() {
	
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
	 * @return descCorresponde
	 */
	public String getDescCorresponde() {
		return descCorresponde;
	}

	/**
	 * @param descCorresponde asigna el valor a descCorresponde
	 */
	public void setDescCorresponde(String descCorresponde) {
		this.descCorresponde = descCorresponde;
	}

	/**
	 * Imprime el valor de todas las variables del objeto.
	 * @return String
	 */		
	public String toString()
	{
		StringBuffer ret = new StringBuffer();
		ret.append( "com.webset.set.dto.bancaelectronica.CatCorresponde: " );
		ret.append( "idCorresponde=" + idCorresponde );
		ret.append( ", descCorresponde=" + descCorresponde );
		return ret.toString();
	}	
	

}
