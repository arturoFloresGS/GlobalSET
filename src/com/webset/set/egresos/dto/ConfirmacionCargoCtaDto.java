package com.webset.set.egresos.dto;

import java.util.Date;

public class ConfirmacionCargoCtaDto {
	String fechaIni;
	String fechaFin;
	int noEmpresa;
	int idBanco;
	String idChequera;
	boolean chkRechNom;
	String id_cve_concepto;
	
	//Para el select de movto_banca_e
	String secuencia;
	boolean todos;
	String descripcion;
	Double importe;
	Date fecValor;
	String concepto;
	
	//Para ejecutar la confirmacion cargos
	int noFolioDet;
	
	//Para la pantalla de Consulta de mantenimiento de cupos
	int idGrupoFlujo;
	String descGrupoFlujo;
	String idDivision;
	String descDivision;
	String fecLimiteSelecc;
	String cveControl;
	double montoMaximo;
	String mensaje;
	double cupoManual;
	double cupoAutomatico;
	String descGrupoCupo;
	String fechaPropuesta;
	int idGrupo;
	
	//Para la pantalla de pago de propuestas automatico
	String idValor;
	String descValor;
	String descCveControl;
	double cupoTotal;
	String idDivisa;
	String fecDivisa;
	double valor;
	int usuarioUno;
	int usuarioDos;
	int usuarioTres;
	int nivelAutorizacion;
	boolean chkPropuestos;
	boolean chkProvServ;
	boolean bChequera;
	
	double importeOriginal;
	String nomEmpresa;
	String descEstatus;
	String noFactura;
	String noDocto;
	String descCveOperacion;
	int idFormaPago;
	String descFormaPago;
	String descBanco;
	String idChequeraBenef;
	String beneficiario;
	int noFolioMov;
	String noCuenta;
	String idCveOperacion;
	int idBancoBenef;
	int idCaja;
	String idLeyenda;
	String idEstatusMov;
	String noCliente;
	double tipoCambio;
	String origenMov;
	String solicita;
	String autoriza;
	String observacion;
	int loteEntrada;
	String descCaja;
	String equivalePersona;
	int agrupa1;
	int agrupa2;
	double idRubro;
	int agrupa3;
	int noPedido;
	double importeMn;
	String nomProveedor;
	String fecPropuesta;
	String fecModif;
	int tipo;
	String idContable;
	double totalPropuesto;
	boolean bAgregar;
	String tipoGrid;
	String contacto;
	
	//Para la pantalla de compra venta de divisas
	int usuario;
	String sFolio;
	String fechaValor;
	String fechaLiquidacion;
	String nomOperador;
	String divisaVenta;
	int bancoCargo;
	String chequeraCargo;  
	String divisaCompra;
	int bancoAbono;
	String chequeraAbono;  
	int idCasaCambio;
	String descCasaCambio;
	int idOperador;
	int grupo;
	int bancoNes;
	double importeCompra;
	double importeVenta;
	String chequeraBenef;
	int custodia;
	int cliente;
	String noClaveControl;
	int idBancoCasa;
	String noProveedor;
	String numFormaPago;
	String fechaHoy;
	int noBancoPago;
	String bancoPago;
	String observaciones;
	String divisaOriginal;
	int dias;
	String bancoCte;
	String bcoPagoCruzado;
	String cheqPagoCruzado;
	String divPagoCruzado;
	String chequeraCte;
	String idDivisaPago;
	String idDivisaCte;
	String descGrupo;
	String descRubro;
	String descDivisa;
	String formaPago;
	String impPagoCruzado;
	String descBancoCasa;
	

	String descDivisaVenta	;
	String descDivisaCompra	; 
	String descBancoCargo;
	String descBancoAbono;
	String descGrupoEgreso;
	String descRubroEgreso;
	int idFirmante;
	String nombreFirmante;
	String referencia;
	int grupoIngreso; 
	int rubroIngreso;
	
	
	
	public String getContacto() {
		return contacto;
	}
	public void setContacto(String contacto) {
		this.contacto = contacto;
	}
	public String getDescDivisaVenta() {
		return descDivisaVenta;
	}
	public void setDescDivisaVenta(String descDivisaVenta) {
		this.descDivisaVenta = descDivisaVenta;
	}
	public String getDescDivisaCompra() {
		return descDivisaCompra;
	}
	public void setDescDivisaCompra(String descDivisaCompra) {
		this.descDivisaCompra = descDivisaCompra;
	}
	public String getDescBancoCargo() {
		return descBancoCargo;
	}
	public void setDescBancoCargo(String descBancoCargo) {
		this.descBancoCargo = descBancoCargo;
	}
	public String getDescBancoAbono() {
		return descBancoAbono;
	}
	public void setDescBancoAbono(String descBancoAbono) {
		this.descBancoAbono = descBancoAbono;
	}
	public String getDescGrupoEgreso() {
		return descGrupoEgreso;
	}
	public void setDescGrupoEgreso(String descGrupoEgreso) {
		this.descGrupoEgreso = descGrupoEgreso;
	}
	public String getDescRubroEgreso() {
		return descRubroEgreso;
	}
	public void setDescRubroEgreso(String descRubroEgreso) {
		this.descRubroEgreso = descRubroEgreso;
	}
	public int getIdFirmante() {
		return idFirmante;
	}
	public void setIdFirmante(int idFirmante) {
		this.idFirmante = idFirmante;
	}
	public String getNombreFirmante() {
		return nombreFirmante;
	}
	public void setNombreFirmante(String nombreFirmante) {
		this.nombreFirmante = nombreFirmante;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public int getGrupoIngreso() {
		return grupoIngreso;
	}
	public void setGrupoIngreso(int grupoIngreso) {
		this.grupoIngreso = grupoIngreso;
	}
	public int getRubroIngreso() {
		return rubroIngreso;
	}
	public void setRubroIngreso(int rubroIngreso) {
		this.rubroIngreso = rubroIngreso;
	}
	public String getTipoGrid() {
		return tipoGrid;
	}
	public void setTipoGrid(String tipoGrid) {
		this.tipoGrid = tipoGrid;
	}
	public boolean isBAgregar() {
		return bAgregar;
	}
	public void setBAgregar(boolean agregar) {
		bAgregar = agregar;
	}
	public double getTotalPropuesto() {
		return totalPropuesto;
	}
	public void setTotalPropuesto(double totalPropuesto) {
		this.totalPropuesto = totalPropuesto;
	}
	public double getImporteOriginal() {
		return importeOriginal;
	}
	public void setImporteOriginal(double importeOriginal) {
		this.importeOriginal = importeOriginal;
	}
	public String getNomEmpresa() {
		return nomEmpresa;
	}
	public void setNomEmpresa(String nomEmpresa) {
		this.nomEmpresa = nomEmpresa;
	}
	public String getDescEstatus() {
		return descEstatus;
	}
	public void setDescEstatus(String descEstatus) {
		this.descEstatus = descEstatus;
	}
	public String getNoFactura() {
		return noFactura;
	}
	public void setNoFactura(String noFactura) {
		this.noFactura = noFactura;
	}
	public String getNoDocto() {
		return noDocto;
	}
	public void setNoDocto(String noDocto) {
		this.noDocto = noDocto;
	}
	public String getDescCveOperacion() {
		return descCveOperacion;
	}
	public void setDescCveOperacion(String descCveOperacion) {
		this.descCveOperacion = descCveOperacion;
	}
	public int getIdFormaPago() {
		return idFormaPago;
	}
	public void setIdFormaPago(int idFormaPago) {
		this.idFormaPago = idFormaPago;
	}
	public String getDescFormaPago() {
		return descFormaPago;
	}
	public void setDescFormaPago(String descFormaPago) {
		this.descFormaPago = descFormaPago;
	}
	public String getDescBanco() {
		return descBanco;
	}
	public void setDescBanco(String descBanco) {
		this.descBanco = descBanco;
	}
	public String getIdChequeraBenef() {
		return idChequeraBenef;
	}
	public void setIdChequeraBenef(String idChequeraBenef) {
		this.idChequeraBenef = idChequeraBenef;
	}
	public String getBeneficiario() {
		return beneficiario;
	}
	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}
	public int getNoFolioMov() {
		return noFolioMov;
	}
	public void setNoFolioMov(int noFolioMov) {
		this.noFolioMov = noFolioMov;
	}
	public String getNoCuenta() {
		return noCuenta;
	}
	public void setNoCuenta(String noCuenta) {
		this.noCuenta = noCuenta;
	}
	public String getIdCveOperacion() {
		return idCveOperacion;
	}
	public void setIdCveOperacion(String idCveOperacion) {
		this.idCveOperacion = idCveOperacion;
	}
	public int getIdBancoBenef() {
		return idBancoBenef;
	}
	public void setIdBancoBenef(int idBancoBenef) {
		this.idBancoBenef = idBancoBenef;
	}
	public int getIdCaja() {
		return idCaja;
	}
	public void setIdCaja(int idCaja) {
		this.idCaja = idCaja;
	}
	public String getIdLeyenda() {
		return idLeyenda;
	}
	public void setIdLeyenda(String idLeyenda) {
		this.idLeyenda = idLeyenda;
	}
	public String getIdEstatusMov() {
		return idEstatusMov;
	}
	public void setIdEstatusMov(String idEstatusMov) {
		this.idEstatusMov = idEstatusMov;
	}
	public String getNoCliente() {
		return noCliente;
	}
	public void setNoCliente(String noCliente) {
		this.noCliente = noCliente;
	}
	public double getTipoCambio() {
		return tipoCambio;
	}
	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}
	public String getOrigenMov() {
		return origenMov;
	}
	public void setOrigenMov(String origenMov) {
		this.origenMov = origenMov;
	}
	public String getSolicita() {
		return solicita;
	}
	public void setSolicita(String solicita) {
		this.solicita = solicita;
	}
	public String getAutoriza() {
		return autoriza;
	}
	public void setAutoriza(String autoriza) {
		this.autoriza = autoriza;
	}
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	public int getLoteEntrada() {
		return loteEntrada;
	}
	public void setLoteEntrada(int loteEntrada) {
		this.loteEntrada = loteEntrada;
	}
	public String getDescCaja() {
		return descCaja;
	}
	public void setDescCaja(String descCaja) {
		this.descCaja = descCaja;
	}
	public String getEquivalePersona() {
		return equivalePersona;
	}
	public void setEquivalePersona(String equivalePersona) {
		this.equivalePersona = equivalePersona;
	}
	public int getAgrupa1() {
		return agrupa1;
	}
	public void setAgrupa1(int agrupa1) {
		this.agrupa1 = agrupa1;
	}
	public int getAgrupa2() {
		return agrupa2;
	}
	public void setAgrupa2(int agrupa2) {
		this.agrupa2 = agrupa2;
	}
	public double getIdRubro() {
		return idRubro;
	}
	public void setIdRubro(double idRubro) {
		this.idRubro = idRubro;
	}
	public int getAgrupa3() {
		return agrupa3;
	}
	public void setAgrupa3(int agrupa3) {
		this.agrupa3 = agrupa3;
	}
	public int getNoPedido() {
		return noPedido;
	}
	public void setNoPedido(int noPedido) {
		this.noPedido = noPedido;
	}
	public double getImporteMn() {
		return importeMn;
	}
	public void setImporteMn(double importeMn) {
		this.importeMn = importeMn;
	}
	public String getNomProveedor() {
		return nomProveedor;
	}
	public void setNomProveedor(String nomProveedor) {
		this.nomProveedor = nomProveedor;
	}
	public String getFecPropuesta() {
		return fecPropuesta;
	}
	public void setFecPropuesta(String fecPropuesta) {
		this.fecPropuesta = fecPropuesta;
	}
	public String getFecModif() {
		return fecModif;
	}
	public void setFecModif(String fecModif) {
		this.fecModif = fecModif;
	}
	public int getTipo() {
		return tipo;
	}
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
	public String getIdContable() {
		return idContable;
	}
	public void setIdContable(String idContable) {
		this.idContable = idContable;
	}
	public boolean isBChequera() {
		return bChequera;
	}
	public void setBChequera(boolean chequera) {
		bChequera = chequera;
	}
	public boolean isChkPropuestos() {
		return chkPropuestos;
	}
	public void setChkPropuestos(boolean chkPropuestos) {
		this.chkPropuestos = chkPropuestos;
	}
	public boolean isChkProvServ() {
		return chkProvServ;
	}
	public void setChkProvServ(boolean chkProvServ) {
		this.chkProvServ = chkProvServ;
	}
	public int getUsuarioUno() {
		return usuarioUno;
	}
	public void setUsuarioUno(int usuarioUno) {
		this.usuarioUno = usuarioUno;
	}
	public int getUsuarioDos() {
		return usuarioDos;
	}
	public void setUsuarioDos(int usuarioDos) {
		this.usuarioDos = usuarioDos;
	}
	public int getUsuarioTres() {
		return usuarioTres;
	}
	public void setUsuarioTres(int usuarioTres) {
		this.usuarioTres = usuarioTres;
	}
	public int getNivelAutorizacion() {
		return nivelAutorizacion;
	}
	public void setNivelAutorizacion(int nivelAutorizacion) {
		this.nivelAutorizacion = nivelAutorizacion;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public String getFecDivisa() {
		return fecDivisa;
	}
	public void setFecDivisa(String fecDivisa) {
		this.fecDivisa = fecDivisa;
	}
	public double getValor() {
		return valor;
	}
	public void setValor(double valor) {
		this.valor = valor;
	}
	public double getCupoTotal() {
		return cupoTotal;
	}
	public void setCupoTotal(double cupoTotal) {
		this.cupoTotal = cupoTotal;
	}
	public String getDescCveControl() {
		return descCveControl;
	}
	public void setDescCveControl(String descCveControl) {
		this.descCveControl = descCveControl;
	}
	public String getIdValor() {
		return idValor;
	}
	public void setIdValor(String idValor) {
		this.idValor = idValor;
	}
	public String getDescValor() {
		return descValor;
	}
	public void setDescValor(String descValor) {
		this.descValor = descValor;
	}
	public String getFecLimiteSelecc() {
		return fecLimiteSelecc;
	}
	public void setFecLimiteSelecc(String fecLimiteSelecc) {
		this.fecLimiteSelecc = fecLimiteSelecc;
	}
	public String getFechaPropuesta() {
		return fechaPropuesta;
	}
	public void setFechaPropuesta(String fechaPropuesta) {
		this.fechaPropuesta = fechaPropuesta;
	}
	
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	public String getCveControl() {
		return cveControl;
	}
	public void setCveControl(String cveControl) {
		this.cveControl = cveControl;
	}
	public double getMontoMaximo() {
		return montoMaximo;
	}
	public void setMontoMaximo(double montoMaximo) {
		this.montoMaximo = montoMaximo;
	}
	public double getCupoManual() {
		return cupoManual;
	}
	public void setCupoManual(double cupoManual) {
		this.cupoManual = cupoManual;
	}
	public double getCupoAutomatico() {
		return cupoAutomatico;
	}
	public void setCupoAutomatico(double cupoAutomatico) {
		this.cupoAutomatico = cupoAutomatico;
	}
	public String getDescGrupoCupo() {
		return descGrupoCupo;
	}
	public void setDescGrupoCupo(String descGrupoCupo) {
		this.descGrupoCupo = descGrupoCupo;
	}
	public int getIdGrupo() {
		return idGrupo;
	}
	public void setIdGrupo(int idGrupo) {
		this.idGrupo = idGrupo;
	}
	
	public String getIdDivision() {
		return idDivision;
	}
	public void setIdDivision(String idDivision) {
		this.idDivision = idDivision;
	}
	public String getDescDivision() {
		return descDivision;
	}
	public void setDescDivision(String descDivision) {
		this.descDivision = descDivision;
	}
	public int getIdGrupoFlujo() {
		return idGrupoFlujo;
	}
	public void setIdGrupoFlujo(int idGrupoFlujo) {
		this.idGrupoFlujo = idGrupoFlujo;
	}
	public String getDescGrupoFlujo() {
		return descGrupoFlujo;
	}
	public void setDescGrupoFlujo(String descGrupoFlujo) {
		this.descGrupoFlujo = descGrupoFlujo;
	}
	public int getNoFolioDet() {
		return noFolioDet;
	}
	public void setNoFolioDet(int noFolioDet) {
		this.noFolioDet = noFolioDet;
	}
	public Date getFecValor() {
		return fecValor;
	}
	public void setFecValor(Date fecValor) {
		this.fecValor = fecValor;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Double getImporte() {
		return importe;
	}
	public void setImporte(Double importe) {
		this.importe = importe;
	}
	
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	
	public String getFechaIni() {
		return fechaIni;
	}
	public void setFechaIni(String fechaIni) {
		this.fechaIni = fechaIni;
	}
	public String getFechaFin() {
		return fechaFin;
	}
	public void setFechaFin(String fechaFin) {
		this.fechaFin = fechaFin;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public boolean isChkRechNom() {
		return chkRechNom;
	}
	public void setChkRechNom(boolean chkRechNom) {
		this.chkRechNom = chkRechNom;
	}
	public String getSecuencia() {
		return secuencia;
	}
	public void setSecuencia(String secuencia) {
		this.secuencia = secuencia;
	}
	public boolean isTodos() {
		return todos;
	}
	public void setTodos(boolean todos) {
		this.todos = todos;
	}
	public int getUsuario() {
		return usuario;
	}
	public void setUsuario(int usuario) {
		this.usuario = usuario;
	}
	public String getSFolio() {
		return sFolio;
	}
	public void setSFolio(String folio) {
		sFolio = folio;
	}
	public String getFechaValor() {
		return fechaValor;
	}
	public void setFechaValor(String fechaValor) {
		this.fechaValor = fechaValor;
	}
	public String getFechaLiquidacion() {
		return fechaLiquidacion;
	}
	public void setFechaLiquidacion(String fechaLiquidacion) {
		this.fechaLiquidacion = fechaLiquidacion;
	}
	public String getNomOperador() {
		return nomOperador;
	}
	public void setNomOperador(String nomOperador) {
		this.nomOperador = nomOperador;
	}
	public String getDivisaVenta() {
		return divisaVenta;
	}
	public void setDivisaVenta(String divisaVenta) {
		this.divisaVenta = divisaVenta;
	}
	public int getBancoCargo() {
		return bancoCargo;
	}
	public void setBancoCargo(int bancoCargo) {
		this.bancoCargo = bancoCargo;
	}
	public String getChequeraCargo() {
		return chequeraCargo;
	}
	public void setChequeraCargo(String chequeraCargo) {
		this.chequeraCargo = chequeraCargo;
	}
	public String getDivisaCompra() {
		return divisaCompra;
	}
	public void setDivisaCompra(String divisaCompra) {
		this.divisaCompra = divisaCompra;
	}
	public int getBancoAbono() {
		return bancoAbono;
	}
	public void setBancoAbono(int bancoAbono) {
		this.bancoAbono = bancoAbono;
	}
	public String getChequeraAbono() {
		return chequeraAbono;
	}
	public void setChequeraAbono(String chequeraAbono) {
		this.chequeraAbono = chequeraAbono;
	}
	public int getIdCasaCambio() {
		return idCasaCambio;
	}
	public void setIdCasaCambio(int idCasaCambio) {
		this.idCasaCambio = idCasaCambio;
	}
	public String getDescCasaCambio() {
		return descCasaCambio;
	}
	public void setDescCasaCambio(String descCasaCambio) {
		this.descCasaCambio = descCasaCambio;
	}
	public int getIdOperador() {
		return idOperador;
	}
	public void setIdOperador(int idOperador) {
		this.idOperador = idOperador;
	}
	public int getGrupo() {
		return grupo;
	}
	public void setGrupo(int grupo) {
		this.grupo = grupo;
	}
	public int getBancoNes() {
		return bancoNes;
	}
	public void setBancoNes(int bancoNes) {
		this.bancoNes = bancoNes;
	}
	public double getImporteCompra() {
		return importeCompra;
	}
	public void setImporteCompra(double importeCompra) {
		this.importeCompra = importeCompra;
	}
	public double getImporteVenta() {
		return importeVenta;
	}
	public void setImporteVenta(double importeVenta) {
		this.importeVenta = importeVenta;
	}
	public String getChequeraBenef() {
		return chequeraBenef;
	}
	public void setChequeraBenef(String chequeraBenef) {
		this.chequeraBenef = chequeraBenef;
	}
	public int getCustodia() {
		return custodia;
	}
	public void setCustodia(int custodia) {
		this.custodia = custodia;
	}
	public int getCliente() {
		return cliente;
	}
	public void setCliente(int cliente) {
		this.cliente = cliente;
	}
	public String getNoClaveControl() {
		return noClaveControl;
	}
	public void setNoClaveControl(String noClaveControl) {
		this.noClaveControl = noClaveControl;
	}
	public int getIdBancoCasa() {
		return idBancoCasa;
	}
	public void setIdBancoCasa(int idBancoCasa) {
		this.idBancoCasa = idBancoCasa;
	}
	public String getNoProveedor() {
		return noProveedor;
	}
	public void setNoProveedor(String noProveedor) {
		this.noProveedor = noProveedor;
	}
	public String getNumFormaPago() {
		return numFormaPago;
	}
	public void setNumFormaPago(String numFormaPago) {
		this.numFormaPago = numFormaPago;
	}
	public String getFechaHoy() {
		return fechaHoy;
	}
	public void setFechaHoy(String fechaHoy) {
		this.fechaHoy = fechaHoy;
	}
	public int getNoBancoPago() {
		return noBancoPago;
	}
	public void setNoBancoPago(int noBancoPago) {
		this.noBancoPago = noBancoPago;
	}
	public String getBancoPago() {
		return bancoPago;
	}
	public void setBancoPago(String bancoPago) {
		this.bancoPago = bancoPago;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	public String getDivisaOriginal() {
		return divisaOriginal;
	}
	public void setDivisaOriginal(String divisaOriginal) {
		this.divisaOriginal = divisaOriginal;
	}
	public int getDias() {
		return dias;
	}
	public void setDias(int dias) {
		this.dias = dias;
	}
	public String getBancoCte() {
		return bancoCte;
	}
	public void setBancoCte(String bancoCte) {
		this.bancoCte = bancoCte;
	}
	public String getBcoPagoCruzado() {
		return bcoPagoCruzado;
	}
	public void setBcoPagoCruzado(String bcoPagoCruzado) {
		this.bcoPagoCruzado = bcoPagoCruzado;
	}
	public String getCheqPagoCruzado() {
		return cheqPagoCruzado;
	}
	public void setCheqPagoCruzado(String cheqPagoCruzado) {
		this.cheqPagoCruzado = cheqPagoCruzado;
	}
	public String getDivPagoCruzado() {
		return divPagoCruzado;
	}
	public void setDivPagoCruzado(String divPagoCruzado) {
		this.divPagoCruzado = divPagoCruzado;
	}
	public String getChequeraCte() {
		return chequeraCte;
	}
	public void setChequeraCte(String chequeraCte) {
		this.chequeraCte = chequeraCte;
	}
	public String getIdDivisaPago() {
		return idDivisaPago;
	}
	public void setIdDivisaPago(String idDivisaPago) {
		this.idDivisaPago = idDivisaPago;
	}
	public String getIdDivisaCte() {
		return idDivisaCte;
	}
	public void setIdDivisaCte(String idDivisaCte) {
		this.idDivisaCte = idDivisaCte;
	}
	public String getDescGrupo() {
		return descGrupo;
	}
	public void setDescGrupo(String descGrupo) {
		this.descGrupo = descGrupo;
	}
	public String getDescRubro() {
		return descRubro;
	}
	public void setDescRubro(String descRubro) {
		this.descRubro = descRubro;
	}
	public String getDescDivisa() {
		return descDivisa;
	}
	public void setDescDivisa(String descDivisa) {
		this.descDivisa = descDivisa;
	}
	public String getFormaPago() {
		return formaPago;
	}
	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}
	public String getImpPagoCruzado() {
		return impPagoCruzado;
	}
	public void setImpPagoCruzado(String impPagoCruzado) {
		this.impPagoCruzado = impPagoCruzado;
	}
	public String getDescBancoCasa() {
		return descBancoCasa;
	}
	public void setDescBancoCasa(String descBancoCasa) {
		this.descBancoCasa = descBancoCasa;
	}
	public String getId_cve_concepto() {
		return id_cve_concepto;
	}
	public void setId_cve_concepto(String id_cve_concepto) {
		this.id_cve_concepto = id_cve_concepto;
	}
	
	
}
