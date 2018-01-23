package com.webset.set.impresion.dto;

public class ConfiguracionChequeDto {
	private int idCampo;
	private String campo;
	private String tipoLetra;
	private int tamanoLetra;
	private double coordX;
	private double coordY;
	private String formato;
	private String driver;
	private int idConf;
	private ChequeContinuoDto chequeContinuo;
	
	public int getIdCampo() {
		return idCampo;
	}
	public void setIdCampo(int idCampo) {
		this.idCampo = idCampo;
	}
	public int getIdConf() {
		return idConf;
	}
	public void setIdConf(int idConf) {
		this.idConf = idConf;
	}
	public ChequeContinuoDto getChequeContinuo() {
		return chequeContinuo;
	}
	public void setChequeContinuo(ChequeContinuoDto chequeContinuo) {
		this.chequeContinuo = chequeContinuo;
	}
	public String getCampo() {
		return campo;
	}
	public void setCampo(String campo) {
		this.campo = campo;
	}
	public String getTipoLetra() {
		return tipoLetra;
	}
	public void setTipoLetra(String tipoLetra) {
		this.tipoLetra = tipoLetra;
	}
	public int getTamanoLetra() {
		return tamanoLetra;
	}
	public void setTamanoLetra(int tamanoLetra) {
		this.tamanoLetra = tamanoLetra;
	}
	public double getCoordX() {
		return coordX;
	}
	public void setCoordX(double d) {
		this.coordX = d;
	}
	public double getCoordY() {
		return coordY;
	}
	public void setCoordY(double d) {
		this.coordY = d;
	}
	public String getFormato() {
		return formato;
	}
	public void setFormato(String formato) {
		this.formato = formato;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
}
