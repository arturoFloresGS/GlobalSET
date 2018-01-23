package com.webset.set.financiamiento.dto;

public class AvalGarantiaDto {
	private int noEmpresa;
	private String nomEmpresa;
	private char idAvalGarantia;
	private String avalGarantia;
	private String clave;
	private String descripcion;
	private double valorTotal;
	private String idDivisa	;
	private  String fecInicial;
	private  String fecFinal;
	private double pjeGarantia;
	private char gtiaEspecial;
	private char estatus;
	private int usuarioModif;
	private String fecModif;
	private String garantiaEspecial;
	private double disponible;
	//AL
	private String idFinanciamiento;
	private int idDisposicion;
	private int noEmpresaAvalada;
	private double montoDispuesto;
	private String fecAlta;
	private int usuarioAlta;
	private int noEmpresaAvalista;
	private String noEmpresaAvalistaAux;
	//AE
	private double montoAvalado;
	//reporteAvaladas
	private String noEmpresaS;
	private String color;
	private String dispuesto;
	private String garantiaAsignada;
	private String montoAsignado;
	private String banco;
	private String descBanco;
	private String credito;
	private String montoAvaladoS;
	private String identifica;
	private String montoDisponible;
	//reporteAvalistas
	private String disponibleReal;
	private String dispuestoReal;
	public String getDisponibleReal() {
		return disponibleReal;
	}
	public void setDisponibleReal(String disponibleReal) {
		this.disponibleReal = disponibleReal;
	}
	public String getDispuestoReal() {
		return dispuestoReal;
	}
	public void setDispuestoReal(String dispuestoReal) {
		this.dispuestoReal = dispuestoReal;
	}
	public String getMontoDisponible() {
		return montoDisponible;
	}
	public void setMontoDisponible(String montoDisponible) {
		this.montoDisponible = montoDisponible;
	}
	public String getBanco() {
		return banco;
	}
	public void setBanco(String banco) {
		this.banco = banco;
	}
	public String getDescBanco() {
		return descBanco;
	}
	public void setDescBanco(String descBanco) {
		this.descBanco = descBanco;
	}
	public String getMontoAsignado() {
		return montoAsignado;
	}
	public void setMontoAsignado(String montoAsignado) {
		this.montoAsignado = montoAsignado;
	}
	public String getGarantiaAsignada() {
		return garantiaAsignada;
	}
	public void setGarantiaAsignada(String garantiaAsignada) {
		this.garantiaAsignada = garantiaAsignada;
	}
	public String getDispuesto() {
		return dispuesto;
	}
	public void setDispuesto(String dispuesto) {
		this.dispuesto = dispuesto;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getNoEmpresaS() {
		return noEmpresaS;
	}
	public void setNoEmpresaS(String noEmpresaS) {
		this.noEmpresaS = noEmpresaS;
	}
	public String getGarantiaEspecial() {
		return garantiaEspecial;
	}
	public void setGarantiaEspecial(String garantiaEspecial) {
		this.garantiaEspecial = garantiaEspecial;
	}
	public String getNomEmpresa() {
		return nomEmpresa;
	}
	public void setNomEmpresa(String nomEmpresa) {
		this.nomEmpresa = nomEmpresa;
	}
	public String getAvalGarantia() {
		return avalGarantia;
	}
	public char getIdAvalGarantia() {
		return idAvalGarantia;
	}
	public void setIdAvalGarantia(char idAvalGarantia) {
		this.idAvalGarantia = idAvalGarantia;
	}
	public void setAvalGarantia(String avalGarantia) {
		this.avalGarantia = avalGarantia;
	}
	public String getIdFinanciamiento() {
		return idFinanciamiento;
	}
	public void setIdFinanciamiento(String idFinanciamiento) {
		this.idFinanciamiento = idFinanciamiento;
	}
	public int getIdDisposicion() {
		return idDisposicion;
	}
	public void setIdDisposicion(int idDisposicion) {
		this.idDisposicion = idDisposicion;
	}
	public int getNoEmpresaAvalada() {
		return noEmpresaAvalada;
	}
	public void setNoEmpresaAvalada(int noEmpresaAvalada) {
		this.noEmpresaAvalada = noEmpresaAvalada;
	}
	public double getMontoDispuesto() {
		return montoDispuesto;
	}
	public void setMontoDispuesto(double montoDispuesto) {
		this.montoDispuesto = montoDispuesto;
	}
	public String getFecAlta() {
		return fecAlta;
	}
	public void setFecAlta(String fecAlta) {
		this.fecAlta = fecAlta;
	}
	public int getUsuarioAlta() {
		return usuarioAlta;
	}
	public void setUsuarioAlta(int usuarioAlta) {
		this.usuarioAlta = usuarioAlta;
	}
	public int getNoEmpresaAvalista() {
		return noEmpresaAvalista;
	}
	public void setNoEmpresaAvalista(int noEmpresaAvalista) {
		this.noEmpresaAvalista = noEmpresaAvalista;
	}
	public double getMontoAvalado() {
		return montoAvalado;
	}
	public void setMontoAvalado(double montoAvalado) {
		this.montoAvalado = montoAvalado;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		this.clave = clave;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public double getValorTotal() {
		return valorTotal;
	}
	public void setValorTotal(double valorTotal) {
		this.valorTotal = valorTotal;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public String getFecInicial() {
		return fecInicial;
	}
	public void setFecInicial(String fecInicial) {
		this.fecInicial = fecInicial;
	}
	public String getFecFinal() {
		return fecFinal;
	}
	public void setFecFinal(String fecFinal) {
		this.fecFinal = fecFinal;
	}
	public double getPjeGarantia() {
		return pjeGarantia;
	}
	public void setPjeGarantia(double pjeGarantia) {
		this.pjeGarantia = pjeGarantia;
	}
	public char getGtiaEspecial() {
		return gtiaEspecial;
	}
	public void setGtiaEspecial(char gtiaEspecial) {
		this.gtiaEspecial = gtiaEspecial;
	}
	public char getEstatus() {
		return estatus;
	}
	public void setEstatus(char estatus) {
		this.estatus = estatus;
	}
	public int getUsuarioModif() {
		return usuarioModif;
	}
	public void setUsuarioModif(int usuarioModif) {
		this.usuarioModif = usuarioModif;
	}
	public String getFecModif() {
		return fecModif;
	}
	public void setFecModif(String fecModif) {
		this.fecModif = fecModif;
	}
	public double getDisponible() {
		return disponible;
	}
	public void setDisponible(double disponible) {
		this.disponible = disponible;
	}
	public String getCredito() {
		return credito;
	}
	public void setCredito(String credito) {
		this.credito = credito;
	}
	public String getMontoAvaladoS() {
		return montoAvaladoS;
	}
	public void setMontoAvaladoS(String montoAvaladoS) {
		this.montoAvaladoS = montoAvaladoS;
	}
	public String getIdentifica() {
		return identifica;
	}
	public void setIdentifica(String identifica) {
		this.identifica = identifica;
	}
	public String getNoEmpresaAvalistaAux() {
		return noEmpresaAvalistaAux;
	}
	public void setNoEmpresaAvalistaAux(String noEmpresaAvalistaAux) {
		this.noEmpresaAvalistaAux = noEmpresaAvalistaAux;
	}
	
	
}
