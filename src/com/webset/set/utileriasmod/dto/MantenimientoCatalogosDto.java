package com.webset.set.utileriasmod.dto;

public class MantenimientoCatalogosDto {
	String descCatalogo;
	String nombreCatalogo;
	String titulos;
	String col;
	private String botones;
	
	

	public String getBotones() {
		return botones;
	}

	public void setBotones(String botones) {
		this.botones = botones;
	}

	public String getCol() {
		return col;
	}

	public void setCol(String col) {
		this.col = col;
	}

	public String getNombreCatalogo() {
		return nombreCatalogo;
	}

	public void setNombreCatalogo(String nombreCatalogo) {
		this.nombreCatalogo = nombreCatalogo;
	}

	public String getDescCatalogo() {
		return descCatalogo;
	}

	public void setDescCatalogo(String descCatalogo) {
		this.descCatalogo = descCatalogo;
	}

	public String getTitulos() {
		return titulos;
	}

	public void setTitulos(String titulos) {
		this.titulos = titulos;
	}
	

}
