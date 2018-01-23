package com.webset.set.bancaelectronica.dto;

/**
 * @author Eduardo Ayala
 * @since 14/Octubre/2010
 * @table CONFIGURA_SET
 */
public class ConfiguraSetDto {

	/** 
	 * Este atributo mapea la columna VALOR en la tabla CONFIGURA_SET.
	 */
	private int indice;

	/** 
	 * Este atributo mapea la columna VALOR en la tabla CONFIGURA_SET.
	 */
	private String valor;

	/** 
	 * Este atributo mapea la columna DESCRIPCION en la tabla CONFIGURA_SET.
	 */
	private String descripcion;

	/**
	 * Constructor 'ConfiguraSet'
	 * 
	 */
	public ConfiguraSetDto()
	{
	}

	/**
	 * @return indice
	 */
	public int getIndice() {
		return indice;
	}

	/**
	 * @param indice the indice to set
	 */
	public void setIndice(int indice) {
		this.indice = indice;
	}

	/**
	 * @return valor
	 */
	public String getValor() {
		return valor;
	}

	/**
	 * @param valor asigna el contenido a valor
	 */
	public void setValor(String valor) {
		this.valor = valor;
	}

	/**
	 * @return descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}

	/**
	 * @param descripcion asigna el valor a descripcion
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	/**
	 * Imprime el valor de todas las variables del objeto.
	 * @return String
	 */
	public String toString()
	{
		StringBuffer ret = new StringBuffer();
		ret.append( "com.webset.set.dto.bancaelectronica.ConfiguraSet: " );
		ret.append( "indice=" + indice );
		ret.append( ", valor=" + valor );
		ret.append( ", descripcion=" + descripcion );
		return ret.toString();
	}	

}
