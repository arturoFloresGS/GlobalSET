package com.webset.set.seguridad.dto;

import java.sql.Date;

/**
 * Descripciòn de una restricciòn en seg_restricfac modulo seguridad
 * @author Cristian Garcia Garcia
 */
public class SegRestricFacDto {
	
	private int nRestriccion;
	
	private Date fHoraInicial;
	private Date fHoraFinal;
	private Date fVencimiento;
	
	public int getNRestriccion() {
		return nRestriccion;
	}
	public void setNRestriccion(int restriccion) {
		nRestriccion = restriccion;
	}
	public Date getFHoraInicial() {
		return fHoraInicial;
	}
	public void setFHoraInicial(Date horaInicial) {
		fHoraInicial = horaInicial;
	}
	public Date getFHoraFinal() {
		return fHoraFinal;
	}
	public void setFHoraFinal(Date horaFinal) {
		fHoraFinal = horaFinal;
	}
	public Date getFVencimiento() {
		return fVencimiento;
	}
	public void setFVencimiento(Date vencimiento) {
		fVencimiento = vencimiento;
	}
	


}
