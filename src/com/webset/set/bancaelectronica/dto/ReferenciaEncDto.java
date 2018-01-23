package com.webset.set.bancaelectronica.dto;

public class ReferenciaEncDto {
	
	private int idBanco;
	private int noEmpresa;	
	private int noDigitos;
	private int campoVerificador;
	private String baseCalculo;
	private String numAlfa;
	private String repara1;
	private String repara2;
	
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public int getNoDigitos() {
		return noDigitos;
	}
	public void setNoDigitos(int noDigitos) {
		this.noDigitos = noDigitos;
	}
	public int getCampoVerificador() {
		return campoVerificador;
	}
	public void setCampoVerificador(int campoVerificador) {
		this.campoVerificador = campoVerificador;
	}
	public String getBaseCalculo() {
		return baseCalculo;
	}
	public void setBaseCalculo(String baseCalculo) {
		this.baseCalculo = baseCalculo;
	}
	public String getNumAlfa() {
		return numAlfa;
	}
	public void setNumAlfa(String numAlfa) {
		this.numAlfa = numAlfa;
	}
	public String getRepara1() {
		return repara1;
	}
	public void setRepara1(String repara1) {
		this.repara1 = repara1;
	}
	public String getRepara2() {
		return repara2;
	}
	public void setRepara2(String repara2) {
		this.repara2 = repara2;
	}

}
