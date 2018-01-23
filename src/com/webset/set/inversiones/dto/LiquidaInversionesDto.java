package com.webset.set.inversiones.dto;

import java.util.Date;

public class LiquidaInversionesDto {
	private int idEmpresa;
	private int tipoPago;
	private boolean bInterna;
	private boolean bCuentaPropia;
	private int idBancoRegreso;
	private String idChequeraRegreso;
	private int idBancoLiquida;
	private String idChequeraLiquida;
	private String idDivisa;
	private Date fechaAlta;
	private double importe;
	private String nomEmpresa;
	private boolean chequerasDiferentesUno;
	private boolean chequerasDiferentesDos;
	
	public String getNomEmpresa() {
		return nomEmpresa;
	}
	public void setNomEmpresa(String nomEmpresa) {
		this.nomEmpresa = nomEmpresa;
	}
	public Date getFechaAlta() {
		return fechaAlta;
	}
	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public int getIdEmpresa() {
		return idEmpresa;
	}
	public void setIdEmpresa(int idEmpresa) {
		this.idEmpresa = idEmpresa;
	}
	public int getTipoPago() {
		return tipoPago;
	}
	public void setTipoPago(int tipoPago) {
		this.tipoPago = tipoPago;
	}
	public boolean isBInterna() {
		return bInterna;
	}
	public void setBInterna(boolean interna) {
		bInterna = interna;
	}
	public boolean isBCuentaPropia() {
		return bCuentaPropia;
	}
	public void setBCuentaPropia(boolean cuentaPropia) {
		bCuentaPropia = cuentaPropia;
	}
	public int getIdBancoRegreso() {
		return idBancoRegreso;
	}
	public void setIdBancoRegreso(int idBancoRegreso) {
		this.idBancoRegreso = idBancoRegreso;
	}
	public String getIdChequeraRegreso() {
		return idChequeraRegreso;
	}
	public void setIdChequeraRegreso(String idChequeraRegreso) {
		this.idChequeraRegreso = idChequeraRegreso;
	}
	public int getIdBancoLiquida() {
		return idBancoLiquida;
	}
	public void setIdBancoLiquida(int idBancoLiquida) {
		this.idBancoLiquida = idBancoLiquida;
	}
	public String getIdChequeraLiquida() {
		return idChequeraLiquida;
	}
	public void setIdChequeraLiquida(String idChequeraLiquida) {
		this.idChequeraLiquida = idChequeraLiquida;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
	public boolean isChequerasDiferentesUno() {
		return chequerasDiferentesUno;
	}
	public void setChequerasDiferentesUno(boolean chequerasDiferentesUno) {
		this.chequerasDiferentesUno = chequerasDiferentesUno;
	}
	public boolean isChequerasDiferentesDos() {
		return chequerasDiferentesDos;
	}
	public void setChequerasDiferentesDos(boolean chequerasDiferentesDos) {
		this.chequerasDiferentesDos = chequerasDiferentesDos;
	}

}
