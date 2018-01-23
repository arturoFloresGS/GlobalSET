package com.webset.set.cashflow.dto;

public class ParamSpFlujoDto {
	
	private int    IdGrupo;
	private int    NoEmpresa;
	private String FechaInicial;
	private String FechaHoy;
	private String FechaManana;
	private String FechaFinal;	
	private int    IdReporte;
	private String IdDivisa;
	private int    NoUsuario;
	private int    opcion; 
	
	
	 
 
	public int getOpcion() {
		return opcion;
	}
	public void setOpcion(int opcion) {
		this.opcion = opcion;
	}
	public int getIdGrupo() {
		return IdGrupo;
	}
	public void setIdGrupo(int idGrupo) {
		IdGrupo = idGrupo;
	}
	public int getNoEmpresa() {
		return NoEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		NoEmpresa = noEmpresa;
	}
	public String getFechaInicial() {
		return FechaInicial;
	}
	public void setFechaInicial(String fechaInicial) {
		FechaInicial = fechaInicial;
	}
	public String getFechaHoy() {
		return FechaHoy;
	}
	public void setFechaHoy(String fechaHoy) {
		FechaHoy = fechaHoy;
	}
	public String getFechaManana() {
		return FechaManana;
	}
	public void setFechaManana(String fechaManana) {
		FechaManana = fechaManana;
	}
	public String getFechaFinal() {
		return FechaFinal;
	}
	public void setFechaFinal(String fechaFinal) {
		FechaFinal = fechaFinal;
	}
	public int getIdReporte() {
		return IdReporte;
	}
	public void setIdReporte(int idReporte) {
		IdReporte = idReporte;
	}
	public String getIdDivisa() {
		return IdDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		IdDivisa = idDivisa;
	}
	public int getNoUsuario() {
		return NoUsuario;
	}
	public void setNoUsuario(int noUsuario) {
		NoUsuario = noUsuario;
	}
	public ParamSpFlujoDto(int idGrupo, int noEmpresa,
			String fechaInicial, String fechaHoy, String fechaManana,
			String fechaFinal, int idReporte, String idDivisa, int noUsuario) {
		super();
		IdGrupo = idGrupo;
		NoEmpresa = noEmpresa;
		FechaInicial = fechaInicial;
		FechaHoy = fechaHoy;
		FechaManana = fechaManana;
		FechaFinal = fechaFinal;
		IdReporte = idReporte;
		IdDivisa = idDivisa;
		NoUsuario = noUsuario;
	}

	
	


	
	
	
	
	

}
