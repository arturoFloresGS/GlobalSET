package com.webset.set.utilerias.dto;

public class GeneradorDto {
	
	private int empresa; 
	private int folParam;
	private int folMovi;
	private int folDeta; 
	private int result;
	private int idUsuario;
	private String mensajeResp;
	private String nomForma;
	private String divisa;
	private int id_forma_pago;
	
	
	
	@Override
	public String toString() {
		return "GeneradorDto [empresa=" + empresa + ", folParam=" + folParam + ", folMovi=" + folMovi + ", folDeta="
				+ folDeta + ", result=" + result + ", idUsuario=" + idUsuario + ", mensajeResp=" + mensajeResp
				+ ", nomForma=" + nomForma + "]";
	}
	public int getEmpresa() {
		return empresa;
	}
	public void setEmpresa(int empresa) {
		this.empresa = empresa;
	}
	public int getFolParam() {
		return folParam;
	}
	public void setFolParam(int folParam) {
		this.folParam = folParam;
	}
	public int getFolMovi() {
		return folMovi;
	}
	public void setFolMovi(int folMovi) {
		this.folMovi = folMovi;
	}
	public int getFolDeta() {
		return folDeta;
	}
	public void setFolDeta(int folDeta) {
		this.folDeta = folDeta;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public String getMensajeResp() {
		return mensajeResp;
	}
	public void setMensajeResp(String mensajeResp) {
		this.mensajeResp = mensajeResp;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public String getNomForma() {
		return nomForma;
	}
	public void setNomForma(String nomForma) {
		this.nomForma = nomForma;
	}
	public String getDivisa() {
		return divisa;
	}
	public void setDivisa(String divisa) {
		this.divisa = divisa;
	}
	public int getId_forma_pago() {
		return id_forma_pago;
	}
	public void setId_forma_pago(int id_forma_pago) {
		this.id_forma_pago = id_forma_pago;
	}
	
}
