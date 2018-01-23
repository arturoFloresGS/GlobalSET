package com.webset.set.egresos.dto;

import java.util.Date;

public class PagosPropuestosDto {
	
	private int idGrupoEmpresa;
	private int idGrupo;
	private String cveControl;
	private String nombreRegla;

	private Date fecha;
	private String psDivision; 
	private String pdOrigenMov;
	
	private Date fechaIni;
	private Date fechaFin;
	private Date fechaHoy;
	private String autorizaPropuesta;
	private boolean bLocal;
	private int IdUsuario; 
	private boolean bSoloUsrAct;
	private boolean bSoloManuales;
	private boolean bTambienIntercos;
	private boolean bSoloCheques;
	private int idDivision;
	private int tipoReporte;
	private String total;
	
	public int getIdGrupoEmpresa() {
		return idGrupoEmpresa;
	}
	public void setIdGrupoEmpresa(int idGrupoEmpresa) {
		this.idGrupoEmpresa = idGrupoEmpresa;
	}
	public int getIdGrupo() {
		return idGrupo;
	}
	public void setIdGrupo(int idGrupo) {
		this.idGrupo = idGrupo;
	}
	public String getCveControl() {
		return cveControl;
	}
	public void setCveControl(String cveControl) {
		this.cveControl = cveControl;
	}
	public String getNombreRegla() {
		return nombreRegla;
	}
	public void setNombreRegla(String nombreRegla) {
		this.nombreRegla = nombreRegla;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getPsDivision() {
		return psDivision;
	}
	public void setPsDivision(String psDivision) {
		this.psDivision = psDivision;
	}
	public String getPdOrigenMov() {
		return pdOrigenMov;
	}
	public void setPdOrigenMov(String pdOrigenMov) {
		this.pdOrigenMov = pdOrigenMov;
	}
	public Date getFechaIni() {
		return fechaIni;
	}
	public void setFechaIni(Date fechaIni) {
		this.fechaIni = fechaIni;
	}
	public Date getFechaFin() {
		return fechaFin;
	}
	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}
	public Date getFechaHoy() {
		return fechaHoy;
	}
	public void setFechaHoy(Date fechaHoy) {
		this.fechaHoy = fechaHoy;
	}
	public String getAutorizaPropuesta() {
		return autorizaPropuesta;
	}
	public void setAutorizaPropuesta(String autorizaPropuesta) {
		this.autorizaPropuesta = autorizaPropuesta;
	}
	public boolean isBLocal() {
		return bLocal;
	}
	public void setBLocal(boolean local) {
		bLocal = local;
	}
	public int getIdUsuario() {
		return IdUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		IdUsuario = idUsuario;
	}
	public boolean isBSoloUsrAct() {
		return bSoloUsrAct;
	}
	public void setBSoloUsrAct(boolean soloUsrAct) {
		bSoloUsrAct = soloUsrAct;
	}
	public boolean isBSoloManuales() {
		return bSoloManuales;
	}
	public void setBSoloManuales(boolean soloManuales) {
		bSoloManuales = soloManuales;
	}
	public boolean isBTambienIntercos() {
		return bTambienIntercos;
	}
	public void setBTambienIntercos(boolean tambienIntercos) {
		bTambienIntercos = tambienIntercos;
	}
	public boolean isBSoloCheques() {
		return bSoloCheques;
	}
	public void setBSoloCheques(boolean soloCheques) {
		bSoloCheques = soloCheques;
	}
	public int getIdDivision() {
		return idDivision;
	}
	public void setIdDivision(int idDivision) {
		this.idDivision = idDivision;
	}
	public int getTipoReporte() {
		return tipoReporte;
	}
	public void setTipoReporte(int tipoReporte) {
		this.tipoReporte = tipoReporte;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	
	
}
