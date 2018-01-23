package com.webset.set.seguridad.dto;

/**
 *  
 * @author Victor Hugo Tello
 * @since 19/Octubre/2010
 * @see <p>Tabla CAT_CAJA</p>
 */

public class CatCajaDto {
	private int deposito;
	private int equivaleBanco;
	private int idCaja;
	
	private String bAbrirCaja; 
	private String bActiva;
	private String descCaja;
	private String ubicacion;
	
	private double fondoFijoMn;
	private double fondoFijoDls;
	private double morralla;
	
	/**
	 * @return the deposito
	 */
	public int getDeposito() {
		return deposito;
	}
	/**
	 * @param deposito the deposito to set
	 */
	public void setDeposito(int deposito) {
		this.deposito = deposito;
	}
	/**
	 * @return the equivaleBanco
	 */
	public int getEquivaleBanco() {
		return equivaleBanco;
	}
	/**
	 * @param equivaleBanco the equivaleBanco to set
	 */
	public void setEquivaleBanco(int equivaleBanco) {
		this.equivaleBanco = equivaleBanco;
	}
	/**
	 * @return the idCaja
	 */
	public int getIdCaja() {
		return idCaja;
	}
	/**
	 * @param idCaja the idCaja to set
	 */
	public void setIdCaja(int idCaja) {
		this.idCaja = idCaja;
	}
	/**
	 * @return the bAbrirCaja
	 */
	public String getBAbrirCaja() {
		return bAbrirCaja;
	}
	/**
	 * @param abrirCaja the bAbrirCaja to set
	 */
	public void setBAbrirCaja(String abrirCaja) {
		bAbrirCaja = abrirCaja;
	}
	/**
	 * @return the bActiva
	 */
	public String getBActiva() {
		return bActiva;
	}
	/**
	 * @param activa the bActiva to set
	 */
	public void setBActiva(String activa) {
		bActiva = activa;
	}
	/**
	 * @return the descCaja
	 */
	public String getDescCaja() {
		return descCaja;
	}
	/**
	 * @param descCaja the descCaja to set
	 */
	public void setDescCaja(String descCaja) {
		this.descCaja = descCaja;
	}
	/**
	 * @return the ubicacion
	 */
	public String getUbicacion() {
		return ubicacion;
	}
	/**
	 * @param ubicacion the ubicacion to set
	 */
	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}
	/**
	 * @return the fondoFijoMn
	 */
	public double getFondoFijoMn() {
		return fondoFijoMn;
	}
	/**
	 * @param fondoFijoMn the fondoFijoMn to set
	 */
	public void setFondoFijoMn(double fondoFijoMn) {
		this.fondoFijoMn = fondoFijoMn;
	}
	/**
	 * @return the fondoFijoDls
	 */
	public double getFondoFijoDls() {
		return fondoFijoDls;
	}
	/**
	 * @param fondoFijoDls the fondoFijoDls to set
	 */
	public void setFondoFijoDls(double fondoFijoDls) {
		this.fondoFijoDls = fondoFijoDls;
	}
	/**
	 * @return the morralla
	 */
	public double getMorralla() {
		return morralla;
	}
	/**
	 * @param morralla the morralla to set
	 */
	public void setMorralla(double morralla) {
		this.morralla = morralla;
	}
}

