package com.webset.set.utilerias.dto;

import java.util.Date;

public class ComunEgresosDto {
	
	private String cveControl;
	private Date fechaHoy;
	private int count;
	private double suma;
	private int idGrupoEmpresas;
	private int idGrupoRubros;
	private Date fechaPago;
	private int agrupaEmpChe;
	private String psDivision;
	private boolean compraVtaTransfer;
	private Date fecha;
	private String psBandera;
	private int idFormaPago;
	private String psOrigenMov;
	private String psAgrupaCheques;
	private String psAgrupaTransfers;
	private String mensaje;
	private String mensaje2;
	private double importe;
	
	
	public String getCveControl() {
		return cveControl;
	}
	public void setCveControl(String cveControl) {
		this.cveControl = cveControl;
	}
	public Date getFechaHoy() {
		return fechaHoy;
	}
	public void setFechaHoy(Date fechaHoy) {
		this.fechaHoy = fechaHoy;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public double getSuma() {
		return suma;
	}
	public void setSuma(double suma) {
		this.suma = suma;
	}
	public int getIdGrupoEmpresas() {
		return idGrupoEmpresas;
	}
	public void setIdGrupoEmpresas(int idGrupoEmpresas) {
		this.idGrupoEmpresas = idGrupoEmpresas;
	}
	public int getIdGrupoRubros() {
		return idGrupoRubros;
	}
	public void setIdGrupoRubros(int idGrupoRubros) {
		this.idGrupoRubros = idGrupoRubros;
	}
	public Date getFechaPago() {
		return fechaPago;
	}
	public void setFechaPago(Date fechaPago) {
		this.fechaPago = fechaPago;
	}
	public int getAgrupaEmpChe() {
		return agrupaEmpChe;
	}
	public void setAgrupaEmpChe(int agrupaEmpChe) {
		this.agrupaEmpChe = agrupaEmpChe;
	}
	public String getPsDivision() {
		return psDivision;
	}
	public void setPsDivision(String psDivision) {
		this.psDivision = psDivision;
	}
	public boolean isCompraVtaTransfer() {
		return compraVtaTransfer;
	}
	public void setCompraVtaTransfer(boolean compraVtaTransfer) {
		this.compraVtaTransfer = compraVtaTransfer;
	}	
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getPsBandera() {
		return psBandera;
	}
	public void setPsBandera(String psBandera) {
		this.psBandera = psBandera;
	}
	public int getIdFormaPago() {
		return idFormaPago;
	}
	public void setIdFormaPago(int idFormaPago) {
		this.idFormaPago = idFormaPago;
	}
	public String getPsOrigenMov() {
		return psOrigenMov;
	}
	public void setPsOrigenMov(String psOrigenMov) {
		this.psOrigenMov = psOrigenMov;
	}
	public String getPsAgrupaCheques() {
		return psAgrupaCheques;
	}
	public void setPsAgrupaCheques(String psAgrupaCheques) {
		this.psAgrupaCheques = psAgrupaCheques;
	}	
	public String getPsAgrupaTransfers() {
		return psAgrupaTransfers;
	}
	public void setPsAgrupaTransfers(String psAgrupaTransfers) {
		this.psAgrupaTransfers = psAgrupaTransfers;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	public String getMensaje2() {
		return mensaje2;
	}
	public void setMensaje2(String mensaje2) {
		this.mensaje2 = mensaje2;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
}
