package com.webset.set.flujos.dto;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unchecked")
public class PosicionBancariaDto {
	private String idDivisa;
	private String descDivisa;
	private int idBanco;
	private int tipoReporte;
	private String descBanco;
	private int noEmpresa;
	private String nomEmpresa;
	private String idChequera;
	private String descChequera;
	private String idGrupo;
	private String descGrupo;
	private String idRubro;
	private String dataIndex;
	private String fecIni;
	private String fecFin;
	private String fecValor;
	private String fecOperacion;
	private double importe;
	private String referencia;
	private String concepto;
	private String idTipoMovto;
	
	private String nomReporte;
	private int tipoPosicion;
	private String columnas;
	private String fields;
	
	private String col0;
	private String col1;
	private String col2;
	private String col3;
	private String col4;
	private String col5;
	
	private List<HashMap> row;
	
	private int idTipoConcepto;
	private String descripcion;
	private String descRubro;
	private int mes;
	private int anio;
	private int noFolioDet;
	private String nomCol;
	private double sdoIni;
	private double sdoFin;
	
	public PosicionBancariaDto() {		
		
	}
	
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public String getDescDivisa() {
		return descDivisa;
	}
	public void setDescDivisa(String descDivisa) {
		this.descDivisa = descDivisa;
	}
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public String getDescBanco() {
		return descBanco;
	}
	public void setDescBanco(String descBanco) {
		this.descBanco = descBanco;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public String getNomEmpresa() {
		return nomEmpresa;
	}
	public void setNomEmpresa(String nomEmpresa) {
		this.nomEmpresa = nomEmpresa;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public String getDescChequera() {
		return descChequera;
	}
	public void setDescChequera(String descChequera) {
		this.descChequera = descChequera;
	}
	public String getIdGrupo() {
		return idGrupo;
	}
	public void setIdGrupo(String idGrupo) {
		this.idGrupo = idGrupo;
	}
	public String getDescGrupo() {
		return descGrupo;
	}
	public void setDescGrupo(String descGrupo) {
		this.descGrupo = descGrupo;
	}
	public String getIdRubro() {
		return idRubro;
	}
	public void setIdRubro(String idRubro) {
		this.idRubro = idRubro;
	}
	public String getFecIni() {
		return fecIni;
	}
	public void setDataIndex(String dataIndex) {
		this.dataIndex = dataIndex;
	}
	public String getDataIndex() {
		return dataIndex;
	}
	public void setFecIni(String fecIni) {
		this.fecIni = fecIni;
	}
	public String getFecFin() {
		return fecFin;
	}
	public void setFecFin(String fecFin) {
		this.fecFin = fecFin;
	}
	public String getFecValor() {
		return fecValor;
	}
	public void setFecValor(String fecValor) {
		this.fecValor = fecValor;
	}
	public String getFecOperacion() {
		return fecOperacion;
	}
	public void setFecOperacion(String fecOperacion) {
		this.fecOperacion = fecOperacion;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public String getIdTipoMovto() {
		return idTipoMovto;
	}
	public void setIdTipoMovto(String idTipoMovto) {
		this.idTipoMovto = idTipoMovto;
	}
	public String getNomReporte() {
		return nomReporte;
	}
	public void setNomReporte(String nomReporte) {
		this.nomReporte = nomReporte;
	}
	public int getTipoPosicion() {
		return tipoPosicion;
	}
	public void setTipoPosicion(int tipoPosicion) {
		this.tipoPosicion = tipoPosicion;
	}
	public String getColumnas() {
		return columnas;
	}
	public void setColumnas(String columnas) {
		this.columnas = columnas;
	}
	public String getFields() {
		return fields;
	}
	public void setFields(String fields) {
		this.fields = fields;
	}
	public String getCol0() {
		return col0;
	}
	public void setCol0(String col0) {
		this.col0 = col0;
	}
	public String getCol1() {
		return col1;
	}
	public void setCol1(String col1) {
		this.col1 = col1;
	}
	public String getCol2() {
		return col2;
	}
	public void setCol2(String col2) {
		this.col2 = col2;
	}
	public String getCol3() {
		return col3;
	}
	public void setCol3(String col3) {
		this.col3 = col3;
	}
	public String getCol4() {
		return col4;
	}
	public void setCol4(String col4) {
		this.col4 = col4;
	}
	public String getCol5() {
		return col5;
	}
	public void setCol5(String col5) {
		this.col5 = col5;
	}
	
	public PosicionBancariaDto(List<HashMap> row) {		
		this.row = row;
	}
	public List<HashMap> getRow() {
		return row;
	}

	public void setRow(List<HashMap> row) {
		this.row = row;
	}

	public int getIdTipoConcepto() {
		return idTipoConcepto;
	}

	public void setIdTipoConcepto(int idTipoConcepto) {
		this.idTipoConcepto = idTipoConcepto;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getDescRubro() {
		return descRubro;
	}

	public void setDescRubro(String descRubro) {
		this.descRubro = descRubro;
	}

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}
	
	public int getTipoReporte() {
		return tipoReporte;
	}

	public void setTipoReporte(int tipoReporte) {
		this.tipoReporte = tipoReporte;
	}

	public int getAnio() {
		return anio;
	}

	public void setAnio(int anio) {
		this.anio = anio;
	}
	
	public int getNoFolioDet() {
		return noFolioDet;
	}

	public void setNoFolioDet(int noFolioDet) {
		this.noFolioDet = noFolioDet;
	}

	public String getNomCol() {
		return nomCol;
	}

	public void setNomCol(String nomCol) {
		this.nomCol = nomCol;
	}

	public double getSdoIni() {
		return sdoIni;
	}

	public void setSdoIni(double sdoIni) {
		this.sdoIni = sdoIni;
	}

	public double getSdoFin() {
		return sdoFin;
	}

	public void setSdoFin(double sdoFin) {
		this.sdoFin = sdoFin;
	}
	
}
