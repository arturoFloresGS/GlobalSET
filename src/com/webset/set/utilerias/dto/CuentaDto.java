package com.webset.set.utilerias.dto;
 
import java.util.Date;
/**
 * Este dto representa la tabla cuenta
 * @author Cristian García García
 *
 */

public class CuentaDto {
	
	private int noEmpresa;
	private int noLinea;
	private int noCuenta;
	private int idPlanAmortiza;
	private int idPrograma;
	private int noPeriodos;
	private int ultAmortizacion;
	private int noDisposiciones;
	private int noDispReal;	
	private int dia1Corte;
	private int dia2Corte;
	private int dia3Corte;
	private int dia4Corte;
	private int diasRecordatorio;	
	private int progRecordatorio;
	private int progEdoCuenta;
	private int diasDnticipoDisp;	
	private int periodoDisp;	
	private int noPeriodoGracia;	
	private int usuarioAlta;	
	private int usuarioModif;	
	private int noPersona;
	private int cuentaReinversion;
	private Date fecProxCorte;
	private Date fecProxRev;
	private Date fecVencim;
	private Date fechaAmortiza;
	private Date fecAlta;
	private Date fecModif;
	private double montoAutorizado;
	private double montoDispuesto;
	private double tasaFija	;
	private double puntos;
	private double factor;
	private double puntosAlt;
	private double factorAlt;
	private double tasaMoratoria;
	private double aforo;
	private double porcCapitalizaInt;
	private double tasaVigente;
	private double cantidadAmortiza;
	private String idDivisa;
	private String idEstatusCta;	
	private String tipoFondeo;
	private String idEstatusCar;
	private String idPlazoPeriodo;	
	private String idTasabase;	
	private String idTasaAlt;	
	private String condicionAlt;
	private String idRevisionTasa;	
	private String horizontalVertical;	
	private String dispMinistra;
	private String bRecordatorioPago;
	private String bEdoCuenta;
	private String bCompPago;	
	private String bDispVencido;
	private String bDispDoctoPend;	
	private String bDispPersauto;	
	private String personaAutoriza;	
	private String diaInhabil;
	private String corteDiaInhabil;	
	private String bSeguroDeudor;	
	private int plazoInv;	
	private String descCuenta;	
	private String idTipoCuenta;
	private int noInstitucion;
	private int noContacto1;
	private int noContacto2;
	private long noContacto3;
	private String socGl;
	private String subCuenta;
	private String contratoInstitucion;
	private int noProducto;
	private String bIsrBisiesto;
	private String idManejoIntmor;
	private double valorSalida;
	private int idBancoDep;	
	private String idChequeraDep;
	private int periodoCompra;	
	private int area;	
	private double porciento_valreci;
	private double rentaInterina;
	private double rentaTerminal;
	private double valorCompraAnt;
	private double depositoSeguridad;
	private double comision;
	private double comisionRemerc;
	
	//Valores adicionales
	private String descEstatus;
	private String nombreCorto;
	private String descBanco;
	private String idChequeraDos;
	private String razonSocial;
	
	private Integer formaPago;
	
	private String aplicaISR;
	private String isrIgualInt;
	
	
	public String getRazonSocial() {
		return razonSocial;
	}
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	public String getIdChequeraDos() {
		return idChequeraDos;
	}
	public void setIdChequeraDos(String idChequeraDos) {
		this.idChequeraDos = idChequeraDos;
	}
	public String getDescBanco() {
		return descBanco;
	}
	public void setDescBanco(String descBanco) {
		this.descBanco = descBanco;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public int getNoLinea() {
		return noLinea;
	}
	public void setNoLinea(int noLinea) {
		this.noLinea = noLinea;
	}
	public int getNoCuenta() {
		return noCuenta;
	}
	public void setNoCuenta(int noCuenta) {
		this.noCuenta = noCuenta;
	}
	public int getIdPlanAmortiza() {
		return idPlanAmortiza;
	}
	public void setIdPlanAmortiza(int idPlanAmortiza) {
		this.idPlanAmortiza = idPlanAmortiza;
	}
	public int getIdPrograma() {
		return idPrograma;
	}
	public void setIdPrograma(int idPrograma) {
		this.idPrograma = idPrograma;
	}
	public int getNoPeriodos() {
		return noPeriodos;
	}
	public void setNoPeriodos(int noPeriodos) {
		this.noPeriodos = noPeriodos;
	}
	public int getUltAmortizacion() {
		return ultAmortizacion;
	}
	public void setUltAmortizacion(int ultAmortizacion) {
		this.ultAmortizacion = ultAmortizacion;
	}
	public int getNoDisposiciones() {
		return noDisposiciones;
	}
	public void setNoDisposiciones(int noDisposiciones) {
		this.noDisposiciones = noDisposiciones;
	}
	public int getNoDispReal() {
		return noDispReal;
	}
	public void setNoDispReal(int noDispReal) {
		this.noDispReal = noDispReal;
	}
	public int getDia1Corte() {
		return dia1Corte;
	}
	public void setDia1Corte(int dia1Corte) {
		this.dia1Corte = dia1Corte;
	}
	public int getDia2Corte() {
		return dia2Corte;
	}
	public void setDia2Corte(int dia2Corte) {
		this.dia2Corte = dia2Corte;
	}
	public int getDia3Corte() {
		return dia3Corte;
	}
	public void setDia3Corte(int dia3Corte) {
		this.dia3Corte = dia3Corte;
	}
	public int getDia4Corte() {
		return dia4Corte;
	}
	public void setDia4Corte(int dia4Corte) {
		this.dia4Corte = dia4Corte;
	}
	public int getDiasRecordatorio() {
		return diasRecordatorio;
	}
	public void setDiasRecordatorio(int diasRecordatorio) {
		this.diasRecordatorio = diasRecordatorio;
	}
	public int getProgRecordatorio() {
		return progRecordatorio;
	}
	public void setProgRecordatorio(int progRecordatorio) {
		this.progRecordatorio = progRecordatorio;
	}
	public int getProgEdoCuenta() {
		return progEdoCuenta;
	}
	public void setProgEdoCuenta(int progEdoCuenta) {
		this.progEdoCuenta = progEdoCuenta;
	}
	public int getDiasDnticipoDisp() {
		return diasDnticipoDisp;
	}
	public void setDiasDnticipoDisp(int diasDnticipoDisp) {
		this.diasDnticipoDisp = diasDnticipoDisp;
	}
	public int getPeriodoDisp() {
		return periodoDisp;
	}
	public void setPeriodoDisp(int periodoDisp) {
		this.periodoDisp = periodoDisp;
	}
	public int getNoPeriodoGracsinia() {
		return noPeriodoGracia;
	}
	public void setNoPeriodoGracia(int noPeriodoGracia) {
		this.noPeriodoGracia = noPeriodoGracia;
	}
	public int getUsuarioAlta() {
		return usuarioAlta;
	}
	public void setUsuarioAlta(int usuarioAlta) {
		this.usuarioAlta = usuarioAlta;
	}
	public int getUsuarioModif() {
		return usuarioModif;
	}
	public void setUsuarioModif(int usuarioModif) {
		this.usuarioModif = usuarioModif;
	}
	public int getNoPersona() {
		return noPersona;
	}
	public void setNoPersona(int noPersona) {
		this.noPersona = noPersona;
	}
	public int getCuentaReinversion() {
		return cuentaReinversion;
	}
	public void setCuentaReinversion(int cuentaReinversion) {
		this.cuentaReinversion = cuentaReinversion;
	}
	public Date getFecProxCorte() {
		return fecProxCorte;
	}
	public void setFecProxCorte(Date fecProxCorte) {
		this.fecProxCorte = fecProxCorte;
	}
	public Date getFecProxRev() {
		return fecProxRev;
	}
	public void setFecProxRev(Date fecProxRev) {
		this.fecProxRev = fecProxRev;
	}
	public Date getFecVencim() {
		return fecVencim;
	}
	public void setFecVencim(Date fecVencim) {
		this.fecVencim = fecVencim;
	}
	public Date getFechaAmortiza() {
		return fechaAmortiza;
	}
	public void setFechaAmortiza(Date fechaAmortiza) {
		this.fechaAmortiza = fechaAmortiza;
	}
	public Date getFecAlta() {
		return fecAlta;
	}
	public void setFecAlta(Date fecAlta) {
		this.fecAlta = fecAlta;
	}
	public Date getFecModif() {
		return fecModif;
	}
	public void setFecModif(Date fecModif) {
		this.fecModif = fecModif;
	}
	public double getMontoAutorizado() {
		return montoAutorizado;
	}
	public void setMontoAutorizado(double montoAutorizado) {
		this.montoAutorizado = montoAutorizado;
	}
	public double getMontoDispuesto() {
		return montoDispuesto;
	}
	public void setMontoDispuesto(double montoDispuesto) {
		this.montoDispuesto = montoDispuesto;
	}
	public double getTasaFija() {
		return tasaFija;
	}
	public void setTasaFija(double tasaFija) {
		this.tasaFija = tasaFija;
	}
	public double getPuntos() {
		return puntos;
	}
	public void setPuntos(double puntos) {
		this.puntos = puntos;
	}
	public double getFactor() {
		return factor;
	}
	public void setFactor(double factor) {
		this.factor = factor;
	}
	public double getPuntosAlt() {
		return puntosAlt;
	}
	public void setPuntosAlt(double puntosAlt) {
		this.puntosAlt = puntosAlt;
	}
	public double getFactorAlt() {
		return factorAlt;
	}
	public void setFactorAlt(double factorAlt) {
		this.factorAlt = factorAlt;
	}
	public double getTasaMoratoria() {
		return tasaMoratoria;
	}
	public void setTasaMoratoria(double tasaMoratoria) {
		this.tasaMoratoria = tasaMoratoria;
	}
	public double getAforo() {
		return aforo;
	}
	public void setAforo(double aforo) {
		this.aforo = aforo;
	}
	public double getPorcCapitalizaInt() {
		return porcCapitalizaInt;
	}
	public void setPorcCapitalizaInt(double porcCapitalizaInt) {
		this.porcCapitalizaInt = porcCapitalizaInt;
	}
	public double getTasaVigente() {
		return tasaVigente;
	}
	public void setTasaVigente(double tasaVigente) {
		this.tasaVigente = tasaVigente;
	}
	public double getCantidadAmortiza() {
		return cantidadAmortiza;
	}
	public void setCantidadAmortiza(double cantidadAmortiza) {
		this.cantidadAmortiza = cantidadAmortiza;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public String getIdEstatusCta() {
		return idEstatusCta;
	}
	public void setIdEstatusCta(String idEstatusCta) {
		this.idEstatusCta = idEstatusCta;
	}
	public String getTipoFondeo() {
		return tipoFondeo;
	}
	public void setTipoFondeo(String tipoFondeo) {
		this.tipoFondeo = tipoFondeo;
	}
	public String getIdEstatusCar() {
		return idEstatusCar;
	}
	public void setIdEstatusCar(String idEstatusCar) {
		this.idEstatusCar = idEstatusCar;
	}
	public String getIdPlazoPeriodo() {
		return idPlazoPeriodo;
	}
	public void setIdPlazoPeriodo(String idPlazoPeriodo) {
		this.idPlazoPeriodo = idPlazoPeriodo;
	}
	public String getIdTasabase() {
		return idTasabase;
	}
	public void setIdTasabase(String idTasabase) {
		this.idTasabase = idTasabase;
	}
	public String getIdTasaAlt() {
		return idTasaAlt;
	}
	public void setIdTasaAlt(String idTasaAlt) {
		this.idTasaAlt = idTasaAlt;
	}
	public String getCondicionAlt() {
		return condicionAlt;
	}
	public void setCondicionAlt(String condicionAlt) {
		this.condicionAlt = condicionAlt;
	}
	public String getIdRevisionTasa() {
		return idRevisionTasa;
	}
	public void setIdRevisionTasa(String idRevisionTasa) {
		this.idRevisionTasa = idRevisionTasa;
	}
	public String getHorizontalVertical() {
		return horizontalVertical;
	}
	public void setHorizontalVertical(String horizontalVertical) {
		this.horizontalVertical = horizontalVertical;
	}
	public String getDispMinistra() {
		return dispMinistra;
	}
	public void setDispMinistra(String dispMinistra) {
		this.dispMinistra = dispMinistra;
	}
	public String getBRecordatorioPago() {
		return bRecordatorioPago;
	}
	public void setBRecordatorioPago(String recordatorioPago) {
		bRecordatorioPago = recordatorioPago;
	}
	public String getBEdoCuenta() {
		return bEdoCuenta;
	}
	public void setBEdoCuenta(String edoCuenta) {
		bEdoCuenta = edoCuenta;
	}
	public String getBCompPago() {
		return bCompPago;
	}
	public void setBCompPago(String compPago) {
		bCompPago = compPago;
	}
	public String getBDispVencido() {
		return bDispVencido;
	}
	public void setBDispVencido(String dispVencido) {
		bDispVencido = dispVencido;
	}
	public String getBDispDoctoPend() {
		return bDispDoctoPend;
	}
	public void setBDispDoctoPend(String dispDoctoPend) {
		bDispDoctoPend = dispDoctoPend;
	}
	public String getBDispPersauto() {
		return bDispPersauto;
	}
	public void setBDispPersauto(String dispPersauto) {
		bDispPersauto = dispPersauto;
	}
	public String getPersonaAutoriza() {
		return personaAutoriza;
	}
	public void setPersonaAutoriza(String personaAutoriza) {
		this.personaAutoriza = personaAutoriza;
	}
	public String getDiaInhabil() {
		return diaInhabil;
	}
	public void setDiaInhabil(String diaInhabil) {
		this.diaInhabil = diaInhabil;
	}
	public String getCorteDiaInhabil() {
		return corteDiaInhabil;
	}
	public void setCorteDiaInhabil(String corteDiaInhabil) {
		this.corteDiaInhabil = corteDiaInhabil;
	}
	public String getBSeguroDeudor() {
		return bSeguroDeudor;
	}
	public void setBSeguroDeudor(String seguroDeudor) {
		bSeguroDeudor = seguroDeudor;
	}
	public int getPlazoInv() {
		return plazoInv;
	}
	public void setPlazoInv(int plazoInv) {
		this.plazoInv = plazoInv;
	}
	public String getDescCuenta() {
		return descCuenta;
	}
	public void setDescCuenta(String descCuenta) {
		this.descCuenta = descCuenta;
	}
	public String getIdTipoCuenta() {
		return idTipoCuenta;
	}
	public void setIdTipoCuenta(String idTipoCuenta) {
		this.idTipoCuenta = idTipoCuenta;
	}
	public int getNoInstitucion() {
		return noInstitucion;
	}
	public void setNoInstitucion(int noInstitucion) {
		this.noInstitucion = noInstitucion;
	}
	public int getNoContacto1() {
		return noContacto1;
	}
	public void setNoContacto1(int noContacto1) {
		this.noContacto1 = noContacto1;
	}
	public int getNoContacto2() {
		return noContacto2;
	}
	public void setNoContacto2(int noContacto2) {
		this.noContacto2 = noContacto2;
	}
	public long getNoContacto3() {
		return noContacto3;
	}
	public void setNoContacto3(long nocontacto3) {
		this.noContacto3 = nocontacto3;
	}
	public String getContratoInstitucion() {
		return contratoInstitucion;
	}
	public void setContratoInstitucion(String contratoInstitucion) {
		this.contratoInstitucion = contratoInstitucion;
	}
	public int getNoProducto() {
		return noProducto;
	}
	public void setNoProducto(int noProducto) {
		this.noProducto = noProducto;
	}
	public String getBIsrBisiesto() {
		return bIsrBisiesto;
	}
	public void setBIsrBisiesto(String isrBisiesto) {
		bIsrBisiesto = isrBisiesto;
	}
	public String getIdManejoIntmor() {
		return idManejoIntmor;
	}
	public void setIdManejoIntmor(String idManejoIntmor) {
		this.idManejoIntmor = idManejoIntmor;
	}
	public double getValorSalida() {
		return valorSalida;
	}
	public void setValorSalida(double valorSalida) {
		this.valorSalida = valorSalida;
	}
	public int getIdBancoDep() {
		return idBancoDep;
	}
	public void setIdBancoDep(int idBancoDep) {
		this.idBancoDep = idBancoDep;
	}
	public String getIdChequeraDep() {
		return idChequeraDep;
	}
	public void setIdChequeraDep(String idChequeraDep) {
		this.idChequeraDep = idChequeraDep;
	}
	public int getPeriodoCompra() {
		return periodoCompra;
	}
	public void setPeriodoCompra(int periodoCompra) {
		this.periodoCompra = periodoCompra;
	}
	public int getArea() {
		return area;
	}
	public void setArea(int area) {
		this.area = area;
	}
	public double getPorciento_valreci() {
		return porciento_valreci;
	}
	public void setPorciento_valreci(double porciento_valreci) {
		this.porciento_valreci = porciento_valreci;
	}
	public double getRentaInterina() {
		return rentaInterina;
	}
	public void setRentaInterina(double rentaInterina) {
		this.rentaInterina = rentaInterina;
	}
	public double getRentaTerminal() {
		return rentaTerminal;
	}
	public void setRentaTerminal(double rentaTerminal) {
		this.rentaTerminal = rentaTerminal;
	}
	public double getValorCompraAnt() {
		return valorCompraAnt;
	}
	public void setValorCompraAnt(double valorCompraAnt) {
		this.valorCompraAnt = valorCompraAnt;
	}
	public double getDepositoSeguridad() {
		return depositoSeguridad;
	}
	public void setDepositoSeguridad(double depositoSeguridad) {
		this.depositoSeguridad = depositoSeguridad;
	}
	public double getComision() {
		return comision;
	}
	public void setComision(double comision) {
		this.comision = comision;
	}
	public double getComisionRemerc() {
		return comisionRemerc;
	}
	public void setComisionRemerc(double comisionRemerc) {
		this.comisionRemerc = comisionRemerc;
	}
	public String getDescEstatus() {
		return descEstatus;
	}
	public void setDescEstatus(String descEstatus) {
		this.descEstatus = descEstatus;
	}
	public String getNombreCorto() {
		return nombreCorto;
	}
	public void setNombreCorto(String nombreCorto) {
		this.nombreCorto = nombreCorto;
	}
	public Integer getFormaPago() {
		return formaPago;
	}
	public void setFormaPago(Integer formaPago) {
		this.formaPago = formaPago;
	}
	public String getAplicaISR() {
		return aplicaISR;
	}
	public void setAplicaISR(String aplicaISR) {
		this.aplicaISR = aplicaISR;
	}
	public String getIsrIgualInt() {
		return isrIgualInt;
	}
	public void setIsrIgualInt(String isrIgualInt) {
		this.isrIgualInt = isrIgualInt;
	}
	public String getSocGl() {
		return socGl;
	}
	public void setSocGl(String socGl) {
		this.socGl = socGl;
	}
	public String getSubCuenta() {
		return subCuenta;
	}
	public void setSubCuenta(String subCuenta) {
		this.subCuenta = subCuenta;
	}


}
