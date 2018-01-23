package com.webset.set.bancaelectronica.dto;
/**
 * 
 * @author Sergio Vaca
 * @since 29/Octubre/2010
 *
 */
/**
 * dto que sirve para los parametros de busqueda de empesa
 */
public class ParametroBuscarEmpresaDto {
	private String psChequera;
	private int piBanco;
	private int idUsuario;
	private int noEmpresa;
	private boolean pbSwiftMT940MN;
	private boolean movDiarios;
	public String getPsChequera() {
		return psChequera;
	}
	public void setPsChequera(String psChequera) {
		this.psChequera = psChequera;
	}
	public int getPiBanco() {
		return piBanco;
	}
	public void setPiBanco(int piBanco) {
		this.piBanco = piBanco;
	}
	public boolean isPbSwiftMT940MN() {
		return pbSwiftMT940MN;
	}
	public void setPbSwiftMT940MN(boolean pbSwiftMT940MN) {
		this.pbSwiftMT940MN = pbSwiftMT940MN;
	}
	public boolean isMovDiarios() {
		return movDiarios;
	}
	public void setMovDiarios(boolean movDiarios) {
		this.movDiarios = movDiarios;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
}
