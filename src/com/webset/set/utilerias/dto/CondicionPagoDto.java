package com.webset.set.utilerias.dto;

public class CondicionPagoDto {

	private int idCondicionPago;
	private String fechaBase;
	private String accionDia;
	private String claseDia;
	
	private int diasAdicionales;
	private boolean aplicaDiasAdicionales;
	
	private String finMes;
	private String bancoInterlocutor;
	
	/** TABLA CONTABI_VENCIMIENTO_REG_NEG **/
	private int idContab;
	private boolean aplicaContab;
	private String diaPago1;
	private String diaPago2;
	private String diaPago3;
	private String diaPago4;
	private String diaPago5;
	private String diaPago6;
	private String diaPago7;
	private String semana1;
	private String semana2;
	private String semana3;
	private String semana4;
	private String semana5;
	private String semana6;
	private String semana7;
	/** TABLA DIAS_EXCEP_REG_NEG **/
	private int idDiasExcep;
	private boolean aplicaDiasExcep;
	private int diaEspecifico1;
	private String diaExcep1;
	private String diaPagoExcep1;
	private int rangoDiaDesde1;
	private int rangoDiaHasta1;
	private int diaEspecifico2;
	private String diaExcep2;
	private String diaPagoExcep2;
	private int rangoDiaDesde2;
	private int rangoDiaHasta2;
	
	/** TABLA PLAN_PAGOS_REG_NEG -> se genera su propio DTO al ser uno a muchos. **/
	
	
	public int getIdCondicionPago() {
		return idCondicionPago;
	}
	public void setIdCondicionPago(int idCondicionPago) {
		this.idCondicionPago = idCondicionPago;
	}
	public String getFechaBase() {
		return fechaBase;
	}
	public void setFechaBase(String fechaBase) {
		this.fechaBase = fechaBase;
	}
	public String getAccionDia() {
		return accionDia;
	}
	public void setAccionDia(String accionDia) {
		this.accionDia = accionDia;
	}
	public int getDiasAdicionales() {
		return diasAdicionales;
	}
	public void setDiasAdicionales(int diasAdicionales) {
		this.diasAdicionales = diasAdicionales;
	}
	public String getFinMes() {
		return finMes;
	}
	public void setFinMes(String finMes) {
		this.finMes = finMes;
	}
	public int getIdContab() {
		return idContab;
	}
	public void setIdContab(int idContab) {
		this.idContab = idContab;
	}
	public String getDiaPago1() {
		return diaPago1;
	}
	public void setDiaPago1(String diaPago1) {
		this.diaPago1 = diaPago1;
	}
	public String getDiaPago2() {
		return diaPago2;
	}
	public void setDiaPago2(String diaPago2) {
		this.diaPago2 = diaPago2;
	}
	public String getDiaPago3() {
		return diaPago3;
	}
	public void setDiaPago3(String diaPago3) {
		this.diaPago3 = diaPago3;
	}
	public String getDiaPago4() {
		return diaPago4;
	}
	public void setDiaPago4(String diaPago4) {
		this.diaPago4 = diaPago4;
	}
	public String getDiaPago5() {
		return diaPago5;
	}
	public void setDiaPago5(String diaPago5) {
		this.diaPago5 = diaPago5;
	}
	public String getDiaPago6() {
		return diaPago6;
	}
	public void setDiaPago6(String diaPago6) {
		this.diaPago6 = diaPago6;
	}
	public String getDiaPago7() {
		return diaPago7;
	}
	public void setDiaPago7(String diaPago7) {
		this.diaPago7 = diaPago7;
	}
	public String getSemana1() {
		return semana1;
	}
	public void setSemana1(String semana1) {
		this.semana1 = semana1;
	}
	public String getSemana2() {
		return semana2;
	}
	public void setSemana2(String semana2) {
		this.semana2 = semana2;
	}
	public String getSemana3() {
		return semana3;
	}
	public void setSemana3(String semana3) {
		this.semana3 = semana3;
	}
	public String getSemana4() {
		return semana4;
	}
	public void setSemana4(String semana4) {
		this.semana4 = semana4;
	}
	public String getSemana5() {
		return semana5;
	}
	public void setSemana5(String semana5) {
		this.semana5 = semana5;
	}
	public String getSemana6() {
		return semana6;
	}
	public void setSemana6(String semana6) {
		this.semana6 = semana6;
	}
	public String getSemana7() {
		return semana7;
	}
	public void setSemana7(String semana7) {
		this.semana7 = semana7;
	}
	public int getIdDiasExcep() {
		return idDiasExcep;
	}
	public void setIdDiasExcep(int idDiasExcep) {
		this.idDiasExcep = idDiasExcep;
	}
	public int getDiaEspecifico1() {
		return diaEspecifico1;
	}
	public void setDiaEspecifico1(int diaEspecifico1) {
		this.diaEspecifico1 = diaEspecifico1;
	}
	public String getDiaExcep1() {
		return diaExcep1;
	}
	public void setDiaExcep1(String diaExcep1) {
		this.diaExcep1 = diaExcep1;
	}
	public String getDiaPagoExcep1() {
		return diaPagoExcep1;
	}
	public void setDiaPagoExcep1(String diaPagoExcep1) {
		this.diaPagoExcep1 = diaPagoExcep1;
	}
	public int getRangoDiaDesde1() {
		return rangoDiaDesde1;
	}
	public void setRangoDiaDesde1(int rangoDiaDesde1) {
		this.rangoDiaDesde1 = rangoDiaDesde1;
	}
	public int getRangoDiaHasta1() {
		return rangoDiaHasta1;
	}
	public void setRangoDiaHasta1(int rangoDiaHasta1) {
		this.rangoDiaHasta1 = rangoDiaHasta1;
	}
	public int getDiaEspecifico2() {
		return diaEspecifico2;
	}
	public void setDiaEspecifico2(int diaEspecifico2) {
		this.diaEspecifico2 = diaEspecifico2;
	}
	public String getDiaExcep2() {
		return diaExcep2;
	}
	public void setDiaExcep2(String diaExcep2) {
		this.diaExcep2 = diaExcep2;
	}
	public String getDiaPagoExcep2() {
		return diaPagoExcep2;
	}
	public void setDiaPagoExcep2(String diaPagoExcep2) {
		this.diaPagoExcep2 = diaPagoExcep2;
	}
	public int getRangoDiaDesde2() {
		return rangoDiaDesde2;
	}
	public void setRangoDiaDesde2(int rangoDiaDesde2) {
		this.rangoDiaDesde2 = rangoDiaDesde2;
	}
	public int getRangoDiaHasta2() {
		return rangoDiaHasta2;
	}
	public void setRangoDiaHasta2(int rangoDiaHasta2) {
		this.rangoDiaHasta2 = rangoDiaHasta2;
	}
	public boolean isAplicaContab() {
		return aplicaContab;
	}
	public void setAplicaContab(boolean aplicaContab) {
		this.aplicaContab = aplicaContab;
	}
	public boolean isAplicaDiasExcep() {
		return aplicaDiasExcep;
	}
	public void setAplicaDiasExcep(boolean aplicaDiasExcep) {
		this.aplicaDiasExcep = aplicaDiasExcep;
	}
	public boolean isAplicaDiasAdicionales() {
		return aplicaDiasAdicionales;
	}
	public void setAplicaDiasAdicionales(boolean aplicaDiasAdicionales) {
		this.aplicaDiasAdicionales = aplicaDiasAdicionales;
	}
	public String getClaseDia() {
		return claseDia;
	}
	public void setClaseDia(String claseDia) {
		this.claseDia = claseDia;
	}
	public String getBancoInterlocutor() {
		return bancoInterlocutor;
	}
	public void setBancoInterlocutor(String bancoInterlocutor) {
		this.bancoInterlocutor = bancoInterlocutor;
	}	
}
