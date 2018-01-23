package com.webset.set.financiamiento.dto;

public class AmortizacionCreditoDto {
	private String idContrato;
	private int idDisposicion;
	private int idAmortizacion;
	private String idFinanciamiento;
	private boolean interesB;
	private boolean disposicion;
	private String fecVencimiento;
	private double tasaVigente;
	private double saldoInsoluto; 
	private double capital;
	private double gasto;
	private double comision;
	private char estatus;
	private double tasaFija;
	 private String periodo;
	private int noAmortizaciones;
	private String tasaBase;
	private double  valorTasa;
	private double puntos;
	private char tasa;
	private double interesA;
	private char pagar;
	private String fecInicio;
	private double  iva;
	private String fechaCalint	;
	private int usuarioAlta;
	private int diaCortecap;
	private int diaCorteint;
	private int dias;
	private String fecPago;
	private int diasPeriodo;
	private int bancoGastcom;
	private String clabeBancariaGastcom;
	private String comentario;
	private int tipoGasto;
	private int sobreTasacb;
	private double  factCapital;
	private int soloRenta;
	private double  renta;
	private int usuarioModif;
	private String fecModif;
	private int noFolioAmort;
	private String cveControl;
	private String descEstatus;
	private double saldo;
	private int empresa;
	private String descEmpresa;
	private int institucion;
	private String descInstitucion;
	private int formaPago;
	private String equivalente;
	private int gpoEmpresa;
	private String descPago;
	private String valorPago;
	private int bancoBenef;
	private String chequeraBenef;
	private String divisa;
	private double totalPago;
	private double importe;
	private boolean primeras;
	private String checked;
	private String division;
	//grid Capital, Intereses
	private int noAmort;
	private String fecIni;
	private String fecVen;
	private String fecPag;
	private String fecVenc;
	private double saldoIns;
	private double interes;
	private double porcentaje;
	private double montoDisposicion;
	private String tipoTasaBase;
	private String idTasaBase;
	private String descTasa;
	
	public double getMontoDisposicion() {
		return montoDisposicion;
	}
	public void setMontoDisposicion(double montoDisposicion) {
		this.montoDisposicion = montoDisposicion;
	}
	public String getDivision() {
		return division;
	}
	public void setDivision(String division) {
		this.division = division;
	}
	public String getChecked() {
		return checked;
	}
	public void setChecked(String checked) {
		this.checked = checked;
	}
	public boolean isPrimeras() {
		return primeras;
	}
	public void setPrimeras(boolean primeras) {
		this.primeras = primeras;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
	
	public String getDescPago() {
		return descPago;
	}
	public void setDescPago(String descPago) {
		this.descPago = descPago;
	}
	public String getValorPago() {
		return valorPago;
	}
	public void setValorPago(String valorPago) {
		this.valorPago = valorPago;
	}
	public int getBancoBenef() {
		return bancoBenef;
	}
	public void setBancoBenef(int bancoBenef) {
		this.bancoBenef = bancoBenef;
	}
	public String getChequeraBenef() {
		return chequeraBenef;
	}
	public void setChequeraBenef(String chequeraBenef) {
		this.chequeraBenef = chequeraBenef;
	}
	public String getDivisa() {
		return divisa;
	}
	public void setDivisa(String divisa) {
		this.divisa = divisa;
	}
	public double getTotalPago() {
		return totalPago;
	}
	public void setTotalPago(double totalPago) {
		this.totalPago = totalPago;
	}
	public int getGpoEmpresa() {
		return gpoEmpresa;
	}
	public void setGpoEmpresa(int gpoEmpresa) {
		this.gpoEmpresa = gpoEmpresa;
	}
	public String getEquivalente() {
		return equivalente;
	}
	public void setEquivalente(String equivalente) {
		this.equivalente = equivalente;
	}
	public int getFormaPago() {
		return formaPago;
	}
	public void setFormaPago(int formaPago) {
		this.formaPago = formaPago;
	}
	public int getEmpresa() {
		return empresa;
	}
	public void setEmpresa(int empresa) {
		this.empresa = empresa;
	}
	public String getDescEmpresa() {
		return descEmpresa;
	}
	public void setDescEmpresa(String descEmpresa) {
		this.descEmpresa = descEmpresa;
	}
	public int getInstitucion() {
		return institucion;
	}
	public void setInstitucion(int institucion) {
		this.institucion = institucion;
	}
	public String getDescInstitucion() {
		return descInstitucion;
	}
	public void setDescInstitucion(String descInstitucion) {
		this.descInstitucion = descInstitucion;
	}
	public double getPorcentaje() {
		return porcentaje;
	}
	public void setPorcentaje(double porcentaje) {
		this.porcentaje = porcentaje;
	}
	public int getNoAmort() {
		return noAmort;
	}
	public void setNoAmort(int noAmort) {
		this.noAmort = noAmort;
	}
	public String getFecIni() {
		return fecIni;
	}
	public void setFecIni(String fecIni) {
		this.fecIni = fecIni;
	}
	public String getFecVen() {
		return fecVen;
	}
	public void setFecVen(String fecVen) {
		this.fecVen = fecVen;
	}
	public String getFecPag() {
		return fecPag;
	}
	public void setFecPag(String fecPag) {
		this.fecPag = fecPag;
	}
	public String getFecVenc() {
		return fecVenc;
	}
	public void setFecVenc(String fecVenc) {
		this.fecVenc = fecVenc;
	}
	public double getSaldoIns() {
		return saldoIns;
	}
	public void setSaldoIns(double saldoIns) {
		this.saldoIns = saldoIns;
	}
	public double getSaldo() {
		return saldo;
	}
	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}
	public String getDescEstatus() {
		return descEstatus;
	}
	public void setDescEstatus(String descEstatus) {
		this.descEstatus = descEstatus;
	}
	public void setTasaBase(String tasaBase) {
		this.tasaBase = tasaBase;
	}
	public double getInteresA() {
		return interesA;
	}
	public void setInteresA(double interesA) {
		this.interesA = interesA;
	}
	public double getCapital() {
		return capital;
	}
	public void setCapital(double capital) {
		this.capital = capital;
	}
	public double getGasto() {
		return gasto;
	}
	public void setGasto(double gasto) {
		this.gasto = gasto;
	}
	public double getComision() {
		return comision;
	}
	public void setComision(double comision) {
		this.comision = comision;
	}
	public char getEstatus() {
		return estatus;
	}
	public void setEstatus(char estatus) {
		this.estatus = estatus;
	}
	public double getTasaFija() {
		return tasaFija;
	}
	public void setTasaFija(double tasaFija) {
		this.tasaFija = tasaFija;
	}
	public String getPeriodo() {
		return periodo;
	}
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}
	public int getNoAmortizaciones() {
		return noAmortizaciones;
	}
	public void setNoAmortizaciones(int noAmortizaciones) {
		this.noAmortizaciones = noAmortizaciones;
	}
	public String getTasaBase() {
		return tasaBase;
	}
	public void setTasaTase(String tasaBase) {
		this.tasaBase = tasaBase;
	}
	public double getValorTasa() {
		return valorTasa;
	}
	public void setValorTasa(double valorTasa) {
		this.valorTasa = valorTasa;
	}
	public double getPuntos() {
		return puntos;
	}
	public void setPuntos(double puntos) {
		this.puntos = puntos;
	}
	public char getTasa() {
		return tasa;
	}
	public void setTasa(char tasa) {
		this.tasa = tasa;
	}
	
	public char getPagar() {
		return pagar;
	}
	public void setPagar(char pagar) {
		this.pagar = pagar;
	}
	public String getFecInicio() {
		return fecInicio;
	}
	public void setFecInicio(String fecInicio) {
		this.fecInicio = fecInicio;
	}
	public double getIva() {
		return iva;
	}
	public void setIva(double iva) {
		this.iva = iva;
	}
	public String getFechaCalint() {
		return fechaCalint;
	}
	public void setFechaCalint(String fechaCalint) {
		this.fechaCalint = fechaCalint;
	}
	public int getUsuarioAlta() {
		return usuarioAlta;
	}
	public void setUsuarioAlta(int usuarioAlta) {
		this.usuarioAlta = usuarioAlta;
	}
	public int getDiaCortecap() {
		return diaCortecap;
	}
	public void setDiaCortecap(int diaCortecap) {
		this.diaCortecap = diaCortecap;
	}
	public int getDiaCorteint() {
		return diaCorteint;
	}
	public void setDiaCorteint(int diaCorteint) {
		this.diaCorteint = diaCorteint;
	}
	public int getDias() {
		return dias;
	}
	public void setDias(int dias) {
		this.dias = dias;
	}
	public String getFecPago() {
		return fecPago;
	}
	public void setFecPago(String fecPago) {
		this.fecPago = fecPago;
	}
	public int getDiasPeriodo() {
		return diasPeriodo;
	}
	public void setDiasPeriodo(int diasPeriodo) {
		this.diasPeriodo = diasPeriodo;
	}
	public int getBancoGastcom() {
		return bancoGastcom;
	}
	public void setBancoGastcom(int bancoGastcom) {
		this.bancoGastcom = bancoGastcom;
	}
	public String getClabeBancariaGastcom() {
		return clabeBancariaGastcom;
	}
	public void setClabeBancariaGastcom(String clabeBancariaGastcom) {
		this.clabeBancariaGastcom = clabeBancariaGastcom;
	}
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	public int getTipoGasto() {
		return tipoGasto;
	}
	public void setTipoGasto(int tipoGasto) {
		this.tipoGasto = tipoGasto;
	}
	public int getSobreTasacb() {
		return sobreTasacb;
	}
	public void setSobreTasacb(int sobreTasacb) {
		this.sobreTasacb = sobreTasacb;
	}
	public double getFactCapital() {
		return factCapital;
	}
	public void setFactCapital(double factCapital) {
		this.factCapital = factCapital;
	}
	public int getSoloRenta() {
		return soloRenta;
	}
	public void setSoloRenta(int soloRenta) {
		this.soloRenta = soloRenta;
	}
	public double getRenta() {
		return renta;
	}
	public void setRenta(double renta) {
		this.renta = renta;
	}
	public int getUsuarioModif() {
		return usuarioModif;
	}
	public void setUsuarioModif(int usuarioModif) {
		this.usuarioModif = usuarioModif;
	}
	public String getFecModif() {
		return fecModif;
	}
	public void setFecModif(String fecModif) {
		this.fecModif = fecModif;
	}
	public int getNoFolioAmort() {
		return noFolioAmort;
	}
	public void setNoFolioAmort(int noFolioAmort) {
		this.noFolioAmort = noFolioAmort;
	}
	public String getCveControl() {
		return cveControl;
	}
	public void setCveControl(String cveControl) {
		this.cveControl = cveControl;
	}
	public double getTasaVigente() {
		return tasaVigente;
	}
	public void setTasaVigente(double tasaVigente) {
		this.tasaVigente = tasaVigente;
	}
	public double getSaldoInsoluto() {
		return saldoInsoluto;
	}
	public void setSaldoInsoluto(double saldoInsoluto) {
		this.saldoInsoluto = saldoInsoluto;
	}
	public String getIdFinanciamiento() {
		return idFinanciamiento;
	}
	public void setIdFinanciamiento(String idFinanciamiento) {
		this.idFinanciamiento = idFinanciamiento;
	}
	public boolean isInteres() {
		return interesB;
	}
	public void setInteres(boolean interesB) {
		this.interesB = interesB;
	}
	public double getInteres() {
		return interes;
	}
	public void setInteres(double interes) {
		this.interes = interes;
	}
	public boolean isDisposicion() {
		return disposicion;
	}
	public void setDisposicion(boolean disposicion) {
		this.disposicion = disposicion;
	}
	public String getIdContrato() {
		return idContrato;
	}
	public void setIdContrato(String idContrato) {
		this.idContrato = idContrato;
	}
	public int getIdDisposicion() {
		return idDisposicion;
	}
	public void setIdDisposicion(int idDisposicion) {
		this.idDisposicion = idDisposicion;
	}
	public int getIdAmortizacion() {
		return idAmortizacion;
	}
	public void setIdAmortizacion(int idAmortizacion) {
		this.idAmortizacion = idAmortizacion;
	}
	public String getFecVencimiento() {
		return fecVencimiento;
	}
	public void setFecVencimiento(String fecVencimiento) {
		this.fecVencimiento = fecVencimiento;
	}
	public boolean isInteresB() {
		return interesB;
	}
	public void setInteresB(boolean interesB) {
		this.interesB = interesB;
	}
	public String getTipoTasaBase() {
		return tipoTasaBase;
	}
	public void setTipoTasaBase(String tipoTasaBase) {
		this.tipoTasaBase = tipoTasaBase;
	}
	public String getIdTasaBase() {
		return idTasaBase;
	}
	public void setIdTasaBase(String idTasaBase) {
		this.idTasaBase = idTasaBase;
	}
	public void setNoAmortizaciones(String string) {
	
		
	}
	public String getDescTasa() {
		return descTasa;
	}
	public void setDescTasa(String descTasa) {
		this.descTasa = descTasa;
	}
	
	
}
