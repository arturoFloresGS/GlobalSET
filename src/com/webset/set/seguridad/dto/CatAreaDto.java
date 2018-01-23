package com.webset.set.seguridad.dto;

/**
 *  
 * @author Victor Hugo Tello
 * @since 19/Octubre/2010
 * @see <p>Tabla CAT_AREA</p>
 */

public class CatAreaDto {
	private int idArea;
	
	private String descArea;

	/**
	 * @return the idArea
	 */
	public int getIdArea() {
		return idArea;
	}

	/**
	 * @param idArea the idArea to set
	 */
	public void setIdArea(int idArea) {
		this.idArea = idArea;
	}

	/**
	 * @return the descArea
	 */
	public String getDescArea() {
		return descArea;
	}

	/**
	 * @param descArea the descArea to set
	 */
	public void setDescArea(String descArea) {
		this.descArea = descArea;
	}
}
