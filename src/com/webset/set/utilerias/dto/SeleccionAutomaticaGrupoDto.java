package com.webset.set.utilerias.dto;

import java.util.Date;

public class SeleccionAutomaticaGrupoDto {
	
	private int idGrupo;
	private String fechaPropuesta;
	private int idGrupoFlujo;
	private double montoMaximo;
	private double cupoManual;
	private double cupoAutomatico;
	private double cupoTotal;
	private String fecLimiteSelecc;
	private String cveControl;
	private String concepto;
	private int usuarioUno;
	private int usuarioDos;
	private int usuarioTres;
	private String idDivision;
	private String habilitado;
	private String motivoRechazo;
	private int idGrupoCupo;
	private String descGrupoCupo;
	private String descGrupoFlujo;
	private int nivelAutorizacion;
	private int numIntercos;
	private int totalIntercos;
	private double propuesto;
	private String estatus;
	private int idUsuario;
	private String psw;
	private String fechaPago;
	private int tieneAutorizacion;
	private int sinChequeraBenef;
	private int sinRfc;
	private int sinBancoPagador;
	private String total;
	
	public String getFechaPago() {
		return fechaPago;
	}
	public void setFechaPago(String fechaPago) {
		this.fechaPago = fechaPago;
	}
	private String user1;
	private String user2;
	private String user3;
	
	private String color;
	
	//AGREGADOS PARA GRUPO SALINAS
	private String divisa;
	private String origenPropuesta;
	
	//Agregado EMS 12/11/2015
	private String viaPago;
	private String nombreRegla;
	
	public int getIdGrupo() {
		return idGrupo;
	}
	public void setIdGrupo(int idGrupo) {
		this.idGrupo = idGrupo;
	}
	public String getFechaPropuesta() {
		return fechaPropuesta;
	}
	public void setFechaPropuesta(String fechaPropuesta) {
		this.fechaPropuesta = fechaPropuesta;
	}
	public int getIdGrupoFlujo() {
		return idGrupoFlujo;
	}
	public void setIdGrupoFlujo(int idGrupoFlujo) {
		this.idGrupoFlujo = idGrupoFlujo;
	}
	public double getMontoMaximo() {
		return montoMaximo;
	}
	public void setMontoMaximo(double montoMaximo) {
		this.montoMaximo = montoMaximo;
	}
	public double getCupoManual() {
		return cupoManual;
	}
	public void setCupoManual(double cupoManual) {
		this.cupoManual = cupoManual;
	}
	public double getCupoAutomatico() {
		return cupoAutomatico;
	}
	public void setCupoAutomatico(double cupoAutomatico) {
		this.cupoAutomatico = cupoAutomatico;
	}
	public double getCupoTotal() {
		return cupoTotal;
	}
	public void setCupoTotal(double cupoTotal) {
		this.cupoTotal = cupoTotal;
	}
	public String getFecLimiteSelecc() {
		return fecLimiteSelecc;
	}
	public void setFecLimiteSelecc(String fecLimiteSelecc) {
		this.fecLimiteSelecc = fecLimiteSelecc;
	}
	public String getCveControl() {
		return cveControl;
	}
	public void setCveControl(String cveControl) {
		this.cveControl = cveControl;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public int getUsuarioUno() {
		return usuarioUno;
	}
	public void setUsuarioUno(int usuarioUno) {
		this.usuarioUno = usuarioUno;
	}
	public int getUsuarioDos() {
		return usuarioDos;
	}
	public void setUsuarioDos(int usuarioDos) {
		this.usuarioDos = usuarioDos;
	}
	public int getUsuarioTres() {
		return usuarioTres;
	}
	public void setUsuarioTres(int usuarioTres) {
		this.usuarioTres = usuarioTres;
	}
	public String getIdDivision() {
		return idDivision;
	}
	public void setIdDivision(String idDivision) {
		this.idDivision = idDivision;
	}
	public String getHabilitado() {
		return habilitado;
	}
	public void setHabilitado(String habilitado) {
		this.habilitado = habilitado;
	}
	public String getMotivoRechazo() {
		return motivoRechazo;
	}
	public void setMotivoRechazo(String motivoRechazo) {
		this.motivoRechazo = motivoRechazo;
	}
	public int getIdGrupoCupo() {
		return idGrupoCupo;
	}
	public void setIdGrupoCupo(int idGrupoCupo) {
		this.idGrupoCupo = idGrupoCupo;
	}
	public String getDescGrupoCupo() {
		return descGrupoCupo;
	}
	public void setDescGrupoCupo(String descGrupoCupo) {
		this.descGrupoCupo = descGrupoCupo;
	}
	public String getDescGrupoFlujo() {
		return descGrupoFlujo;
	}
	public void setDescGrupoFlujo(String descGrupoFlujo) {
		this.descGrupoFlujo = descGrupoFlujo;
	}
	public int getNivelAutorizacion() {
		return nivelAutorizacion;
	}
	public void setNivelAutorizacion(int nivelAutorizacion) {
		this.nivelAutorizacion = nivelAutorizacion;
	}
	public int getTotalIntercos() {
		return totalIntercos;
	}
	public void setTotalIntercos(int totalIntercos) {
		this.totalIntercos = totalIntercos;
	}
	public int getNumIntercos() {
		return numIntercos;
	}
	public void setNumIntercos(int numIntercos) {
		this.numIntercos = numIntercos;
	}
	public double getPropuesto() {
		return propuesto;
	}
	public void setPropuesto(double propuesto) {
		this.propuesto = propuesto;
	}
	public String getEstatus() {
		return estatus;
	}
	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public String getPsw() {
		return psw;
	}
	public void setPsw(String psw) {
		this.psw = psw;
	}
	public String getUser1() {
		return user1;
	}
	public void setUser1(String user1) {
		this.user1 = user1;
	}
	public String getUser2() {
		return user2;
	}
	public void setUser2(String user2) {
		this.user2 = user2;
	}
	public String getUser3() {
		return user3;
	}
	public void setUser3(String user3) {
		this.user3 = user3;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getDivisa() {
		return divisa;
	}
	public void setDivisa(String divisa) {
		this.divisa = divisa;
	}
	public String getOrigenPropuesta() {
		return origenPropuesta;
	}
	public void setOrigenPropuesta(String origenPropuesta) {
		this.origenPropuesta = origenPropuesta;
	}
	public String getViaPago() {
		return viaPago;
	}
	public void setViaPago(String viaPago) {
		this.viaPago = viaPago;
	}
	public String getNombreRegla() {
		return nombreRegla;
	}
	public void setNombreRegla(String nombreRegla) {
		this.nombreRegla = nombreRegla;
	}
	public int getTieneAutorizacion() {
		return tieneAutorizacion;
	}
	public void setTieneAutorizacion(int tieneAutorizacion) {
		this.tieneAutorizacion = tieneAutorizacion;
	}
	public int getSinChequeraBenef() {
		return sinChequeraBenef;
	}
	public void setSinChequeraBenef(int sinChequeraBenef) {
		this.sinChequeraBenef = sinChequeraBenef;
	}
	public int getSinRfc() {
		return sinRfc;
	}
	public void setSinRfc(int sinRfc) {
		this.sinRfc = sinRfc;
	}
	public int getSinBancoPagador() {
		return sinBancoPagador;
	}
	public void setSinBancoPagador(int sinBancoPagador) {
		this.sinBancoPagador = sinBancoPagador;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	
	
}
