package com.webset.set.utilerias.dto;

public class MapeoDto {
	
	//['poliza','grupoRubro', 'rubro', 'banco', 'referencia',  'concepto', 'descripcion', 'observacion', 'tipo', 'especial', 'activo']
	private String referencia;
	private String concepto;
	private String descripcion;
	private String observacion;
	private String tipo;
	private String especial;
	private String activo;
	private String tipoPersona;
	private String razonSocial;
	private String paterno;
	private String materno;
	private String nombre;
	private boolean inactivas;
	private String idPoliza;
	private String descPoliza;
	private String idGrupo;
	private String descGrupo;
	private String idRubro;
	private String descRubro;
	private int secuencia;
	private String idBanco;
	private String descBanco;
	
	public int getSecuencia() {
		return secuencia;
	}
	
	public String getIdPoliza() {
		return idPoliza;
	}

	public void setIdPoliza(String idPoliza) {
		this.idPoliza = idPoliza;
	}

	public String getDescPoliza() {
		return descPoliza;
	}

	public void setDescPoliza(String descPoliza) {
		this.descPoliza = descPoliza;
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

	public String getIdBanco() {
		return idBanco;
	}

	public void setIdBanco(String idBanco) {
		this.idBanco = idBanco;
	}

	public String getDescBanco() {
		return descBanco;
	}

	public void setDescBanco(String descBanco) {
		this.descBanco = descBanco;
	}

	public void setSecuencia(int secuencia) {
		this.secuencia = secuencia;
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
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getEspecial() {
		return especial;
	}
	public void setEspecial(String especial) {
		this.especial = especial;
	}
	public String getActivo() {
		return activo;
	}
	public void setActivo(String activo) {
		this.activo = activo;
	}
	
	
	public String getDescRubro() {
		return descRubro;
	}
	public void setDescRubro(String descRubro) {
		this.descRubro = descRubro;
	}
	
	
	
	public String getTipoPersona() {
		return tipoPersona;
	}
	public void setTipoPersona(String tipoPersona) {
		this.tipoPersona = tipoPersona;
	}
	public String getRazonSocial() {
		return razonSocial;
	}
	
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	public String getPaterno() {
		return paterno;
	}
	public void setPaterno(String paterno) {
		this.paterno = paterno;
	}
	public String getMaterno() {
		return materno;
	}
	public void setMaterno(String materno) {
		this.materno = materno;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public boolean isInactivas() {
		return inactivas;
	}
	public void setInactivas(boolean inactivas) {
		this.inactivas = inactivas;
	}
	
	
}
