package com.webset.set.inversiones.dto;

import java.util.Date;

public class MovimientoDto {
	private int	noEmpresa;
	private int	noFolioDet;
	private int	noFolioMov;
	private int	idTipoOperacion;
	private int	idCveOperacion;
	private int	idTipoSaldo;
	private int	noLinea;
	private int	idInvCbolsa;
	private int	noCuenta;
	private String	idEstatusMov;
	private int	noCheque;
	private String	idChequera;
	private int	idBanco;
	private int	idFormaPago;
	private double	importe;
	private double	importeOriginal;
	private String	bSalvoBuenCobro;
	private int	idTipoDocto;
	private String	noDocto;
	private double	valorTasa;
	private Date fecOperacion;
	private Date fecValor;
	private Date fecValorOriginal;
	private Date fecRecalculo;
	private int	folioRef;
	private int	diasInv;
	private String	idTipoMovto;
	private int	idCaja;
	private String	idDivisa;
	private String	idDivisaOriginal;
	private Date fecExportacion;
	private Date fecReimprime;
	private Date fecImprime;
	private Date fecCheque;
	private int	loteEntrada;
	private int	loteSalida;
	private String referencia;
	private String folioBanco;
	private String beneficiario;
	private String idLeyenda;
	private int	idBancoBenef;
	private String	idChequeraBenef;
	private String	concepto;
	private String	idContable;
	private int	folioSeg;
	private String	archProtegido;
	private int	usuarioImprime;
	private Date	fecAlta;
	private int	usuarioAlta;
	private Date	fecModif;
	private int	usuarioModif;
	private int	noCuentaRef;
	private String	idEstatusCb;
	private String	idEstatusCc;
	private String	noCliente;
	private int	noCobrador;
	private int	sucursal;
	private int	plaza;
	private String	observacion;
	private int	noRecibo;
	private String	origenMov;
	private String	noDoctoCus;
	private String	solicita;
	private String	autoriza;
	private String	bEntregado;
	private double	importeDesglosado;
	private Date	fecEntregado;
	private Date	fecConfTrans;
	private Date	fecRechTrans;
	private double	tipoCambio;
	private String	tipoCancelacion;
	private Date	fecExportaFlujo;
	private Date	fecTrans;
	private String	bGenContable;
	private String	idCodigo;
	private String	idSubcodigo;
	private String	fisicaMoral;
	private String	nacExt;
	private String	idEstatusCheq;
	private int	invoiceId;
	private int	vendorId;
	private int	vendorSiteId;
	private String	invoiceType;
	private String	noFactura;
	private int	grupoPago;
	private double	idRubro;
	private int	agrupa1;
	private int	agrupa2;
	private int	agrupa3;
	private String	poHeaders;
	private String	alternateVendorIdEft;
	private String	alternateVendorSiteIdEft;
	private String	invoiceTypeLookupCode;
	private String	bAutorizaImpre;
	private int	firmante1;
	private int	firmante2;
	private String	operMayEsp;
	private String	codBloqueo;
	private String	rfc;
	private String	bGenConta;
	private int	noPoliza;
	private int	cPoliza;
	private int	cLote;
	private int	cPeriodo;
	private int	cMes;
	private String	division;
	private String	indIva;
	private String	contrarecibo;
	private String	deudor;
	private String	descripcion;
	private String	rango;
	private int	noPedido;
	private String	referenciaBan;
	private String	horaRecibo;
	/**/ private Date	fecConciliaCaja;
	/**/ private Date	fecPropuesta;
	private double	importeParcial;
	private String	bArchSeg;
	private String	cveControl;
	private int	idBancoAnt;
	private String	idChequeraAnt;
	private String	origenMovAnt;
	private String	noClienteAnt;
	private String	idServicioBe;
	private String	eMail;
	private String	clabe;
	private int	idArea;
	private String	montoSobregiro;
	private int	idGrupo;
	private String	tablaOrigen;
	/**/ private Date	fechaContabilizacion;
	private String	bCertificado;
	/**/ private Date	fecCertificacion;
	private String	abba;
	private String	swift;
	private String	nombreProvDivisas;
	private String	bancoDivisas;
	private String	direccionDivisas;
	private String	cuentaDivisas;
	private String	sortCode;
	private String	abi;
	private String	cab;
	private String	comentario1;
	private String	comentario2;
	private String contacto;
	private String instFinan;
	
	private int noMovimientos;
	private String psFolios;
	
	private int idProveedor;
	private int folioFondeo;
	
	private String empCheStr;
	private String idBancoCityStr;
	private String descBancoBenef;
	private boolean seleccionado;
	private String sBandera;//Este parametro ayuda a identificar un registro dependiendo la l�gica de negocio en OrdenInversion
	
	private double interes;
	private double isr;
	private double totalImporte;
	private String nomContacto;
	
	//Atributos extra para obtener los diferentes registros de una orden, utilizado en VencimientoDeInversion
	private int cveOperacionA;
	private int cveOperacionB;
	private int cveOperacionC;
	private int folioRefA;
	private int folioRefB;
	private int folioRefC;
	private int folioDetA;
	private int folioDetB;
	private int folioDetC;
	private double interesAnt;
	private double impuestoAnt;
	private boolean bInversionInterna;
	private double importeDos;
	private double interesDos;
	private double impuestoDos;
	private int tipoOperacionB;
	private int tipoOperacionC;
	private String nota;
	private String sFecValor;
	
	public double getImporteDos() {
		return importeDos;
	}
	public void setImporteDos(double importeDos) {
		this.importeDos = importeDos;
	}
	public double getInteresDos() {
		return interesDos;
	}
	public void setInteresDos(double interesDos) {
		this.interesDos = interesDos;
	}
	public double getImpuestoDos() {
		return impuestoDos;
	}
	public void setImpuestoDos(double impuestoDos) {
		this.impuestoDos = impuestoDos;
	}
	public boolean isBInversionInterna() {
		return bInversionInterna;
	}
	public void setBInversionInterna(boolean inversionInterna) {
		bInversionInterna = inversionInterna;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public int getNoFolioDet() {
		return noFolioDet;
	}
	public void setNoFolioDet(int noFolioDet) {
		this.noFolioDet = noFolioDet;
	}
	public int getNoFolioMov() {
		return noFolioMov;
	}
	public void setNoFolioMov(int noFolioMov) {
		this.noFolioMov = noFolioMov;
	}
	public int getIdTipoOperacion() {
		return idTipoOperacion;
	}
	public void setIdTipoOperacion(int idTipoOperacion) {
		this.idTipoOperacion = idTipoOperacion;
	}
	public int getIdCveOperacion() {
		return idCveOperacion;
	}
	public void setIdCveOperacion(int idCveOperacion) {
		this.idCveOperacion = idCveOperacion;
	}
	public int getIdTipoSaldo() {
		return idTipoSaldo;
	}
	public void setIdTipoSaldo(int idTipoSaldo) {
		this.idTipoSaldo = idTipoSaldo;
	}
	public int getNoLinea() {
		return noLinea;
	}
	public void setNoLinea(int noLinea) {
		this.noLinea = noLinea;
	}
	public int getIdInvCbolsa() {
		return idInvCbolsa;
	}
	public void setIdInvCbolsa(int idInvCbolsa) {
		this.idInvCbolsa = idInvCbolsa;
	}
	public int getNoCuenta() {
		return noCuenta;
	}
	public void setNoCuenta(int noCuenta) {
		this.noCuenta = noCuenta;
	}
	public String getIdEstatusMov() {
		return idEstatusMov;
	}
	public void setIdEstatusMov(String idEstatusMov) {
		this.idEstatusMov = idEstatusMov;
	}
	public int getNoCheque() {
		return noCheque;
	}
	public void setNoCheque(int noCheque) {
		this.noCheque = noCheque;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public int getIdFormaPago() {
		return idFormaPago;
	}
	public void setIdFormaPago(int idFormaPago) {
		this.idFormaPago = idFormaPago;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
	public double getImporteOriginal() {
		return importeOriginal;
	}
	public void setImporteOriginal(double importeOriginal) {
		this.importeOriginal = importeOriginal;
	}
	public String getBSalvoBuenCobro() {
		return bSalvoBuenCobro;
	}
	public void setBSalvoBuenCobro(String salvoBuenCobro) {
		bSalvoBuenCobro = salvoBuenCobro;
	}
	public int getIdTipoDocto() {
		return idTipoDocto;
	}
	public void setIdTipoDocto(int idTipoDocto) {
		this.idTipoDocto = idTipoDocto;
	}
	public String getNoDocto() {
		return noDocto;
	}
	public void setNoDocto(String noDocto) {
		this.noDocto = noDocto;
	}
	public double getValorTasa() {
		return valorTasa;
	}
	public void setValorTasa(double valorTasa) {
		this.valorTasa = valorTasa;
	}
	public Date getFecOperacion() {
		return fecOperacion;
	}
	public void setFecOperacion(Date fecOperacion) {
		this.fecOperacion = fecOperacion;
	}
	public Date getFecValor() {
		return fecValor;
	}
	public void setFecValor(Date fecValor) {
		this.fecValor = fecValor;
	}
	public Date getFecValorOriginal() {
		return fecValorOriginal;
	}
	public void setFecValorOriginal(Date fecValorOriginal) {
		this.fecValorOriginal = fecValorOriginal;
	}
	public Date getFecRecalculo() {
		return fecRecalculo;
	}
	public void setFecRecalculo(Date fecRecalculo) {
		this.fecRecalculo = fecRecalculo;
	}
	public int getFolioRef() {
		return folioRef;
	}
	public void setFolioRef(int folioRef) {
		this.folioRef = folioRef;
	}
	public int getDiasInv() {
		return diasInv;
	}
	public void setDiasInv(int diasInv) {
		this.diasInv = diasInv;
	}
	public String getIdTipoMovto() {
		return idTipoMovto;
	}
	public void setIdTipoMovto(String idTipoMovto) {
		this.idTipoMovto = idTipoMovto;
	}
	public int getIdCaja() {
		return idCaja;
	}
	public void setIdCaja(int idCaja) {
		this.idCaja = idCaja;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public String getIdDivisaOriginal() {
		return idDivisaOriginal;
	}
	public void setIdDivisaOriginal(String idDivisaOriginal) {
		this.idDivisaOriginal = idDivisaOriginal;
	}
	public Date getFecExportacion() {
		return fecExportacion;
	}
	public void setFecExportacion(Date fecExportacion) {
		this.fecExportacion = fecExportacion;
	}
	public Date getFecReimprime() {
		return fecReimprime;
	}
	public void setFecReimprime(Date fecReimprime) {
		this.fecReimprime = fecReimprime;
	}
	public Date getFecImprime() {
		return fecImprime;
	}
	public void setFecImprime(Date fecImprime) {
		this.fecImprime = fecImprime;
	}
	public Date getFecCheque() {
		return fecCheque;
	}
	public void setFecCheque(Date fecCheque) {
		this.fecCheque = fecCheque;
	}
	public int getLoteEntrada() {
		return loteEntrada;
	}
	public void setLoteEntrada(int loteEntrada) {
		this.loteEntrada = loteEntrada;
	}
	public int getLoteSalida() {
		return loteSalida;
	}
	public void setLoteSalida(int loteSalida) {
		this.loteSalida = loteSalida;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getFolioBanco() {
		return folioBanco;
	}
	public void setFolioBanco(String folioBanco) {
		this.folioBanco = folioBanco;
	}
	public String getBeneficiario() {
		return beneficiario;
	}
	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}
	public String getIdLeyenda() {
		return idLeyenda;
	}
	public void setIdLeyenda(String idLeyenda) {
		this.idLeyenda = idLeyenda;
	}
	public int getIdBancoBenef() {
		return idBancoBenef;
	}
	public void setIdBancoBenef(int idBancoBenef) {
		this.idBancoBenef = idBancoBenef;
	}
	public String getIdChequeraBenef() {
		return idChequeraBenef;
	}
	public void setIdChequeraBenef(String idChequeraBenef) {
		this.idChequeraBenef = idChequeraBenef;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public String getIdContable() {
		return idContable;
	}
	public void setIdContable(String idContable) {
		this.idContable = idContable;
	}
	public int getFolioSeg() {
		return folioSeg;
	}
	public void setFolioSeg(int folioSeg) {
		this.folioSeg = folioSeg;
	}
	public String getArchProtegido() {
		return archProtegido;
	}
	public void setArchProtegido(String archProtegido) {
		this.archProtegido = archProtegido;
	}
	public int getUsuarioImprime() {
		return usuarioImprime;
	}
	public void setUsuarioImprime(int usuarioImprime) {
		this.usuarioImprime = usuarioImprime;
	}
	public Date getFecAlta() {
		return fecAlta;
	}
	public void setFecAlta(Date fecAlta) {
		this.fecAlta = fecAlta;
	}
	public int getUsuarioAlta() {
		return usuarioAlta;
	}
	public void setUsuarioAlta(int usuarioAlta) {
		this.usuarioAlta = usuarioAlta;
	}
	public Date getFecModif() {
		return fecModif;
	}
	public void setFecModif(Date fecModif) {
		this.fecModif = fecModif;
	}
	public int getUsuarioModif() {
		return usuarioModif;
	}
	public void setUsuarioModif(int usuarioModif) {
		this.usuarioModif = usuarioModif;
	}
	public int getNoCuentaRef() {
		return noCuentaRef;
	}
	public void setNoCuentaRef(int noCuentaRef) {
		this.noCuentaRef = noCuentaRef;
	}
	public String getIdEstatusCb() {
		return idEstatusCb;
	}
	public void setIdEstatusCb(String idEstatusCb) {
		this.idEstatusCb = idEstatusCb;
	}
	public String getIdEstatusCc() {
		return idEstatusCc;
	}
	public void setIdEstatusCc(String idEstatusCc) {
		this.idEstatusCc = idEstatusCc;
	}
	public String getNoCliente() {
		return noCliente;
	}
	public void setNoCliente(String noCliente) {
		this.noCliente = noCliente;
	}
	public int getNoCobrador() {
		return noCobrador;
	}
	public void setNoCobrador(int noCobrador) {
		this.noCobrador = noCobrador;
	}
	public int getSucursal() {
		return sucursal;
	}
	public void setSucursal(int sucursal) {
		this.sucursal = sucursal;
	}
	public int getPlaza() {
		return plaza;
	}
	public void setPlaza(int plaza) {
		this.plaza = plaza;
	}
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	public int getNoRecibo() {
		return noRecibo;
	}
	public void setNoRecibo(int noRecibo) {
		this.noRecibo = noRecibo;
	}
	public String getOrigenMov() {
		return origenMov;
	}
	public void setOrigenMov(String origenMov) {
		this.origenMov = origenMov;
	}
	public String getNoDoctoCus() {
		return noDoctoCus;
	}
	public void setNoDoctoCus(String noDoctoCus) {
		this.noDoctoCus = noDoctoCus;
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
	public String getBEntregado() {
		return bEntregado;
	}
	public void setBEntregado(String entregado) {
		bEntregado = entregado;
	}
	public double getImporteDesglosado() {
		return importeDesglosado;
	}
	public void setImporteDesglosado(double importeDesglosado) {
		this.importeDesglosado = importeDesglosado;
	}
	public Date getFecEntregado() {
		return fecEntregado;
	}
	public void setFecEntregado(Date fecEntregado) {
		this.fecEntregado = fecEntregado;
	}
	public Date getFecConfTrans() {
		return fecConfTrans;
	}
	public void setFecConfTrans(Date fecConfTrans) {
		this.fecConfTrans = fecConfTrans;
	}
	public Date getFecRechTrans() {
		return fecRechTrans;
	}
	public void setFecRechTrans(Date fecRechTrans) {
		this.fecRechTrans = fecRechTrans;
	}
	public double getTipoCambio() {
		return tipoCambio;
	}
	public void setTipoCambio(double tipoCambio) {
		this.tipoCambio = tipoCambio;
	}
	public String getTipoCancelacion() {
		return tipoCancelacion;
	}
	public void setTipoCancelacion(String tipoCancelacion) {
		this.tipoCancelacion = tipoCancelacion;
	}
	public Date getFecExportaFlujo() {
		return fecExportaFlujo;
	}
	public void setFecExportaFlujo(Date fecExportaFlujo) {
		this.fecExportaFlujo = fecExportaFlujo;
	}
	public Date getFecTrans() {
		return fecTrans;
	}
	public void setFecTrans(Date fecTrans) {
		this.fecTrans = fecTrans;
	}
	public String getBGenContable() {
		return bGenContable;
	}
	public void setBGenContable(String genContable) {
		bGenContable = genContable;
	}
	public String getIdCodigo() {
		return idCodigo;
	}
	public void setIdCodigo(String idCodigo) {
		this.idCodigo = idCodigo;
	}
	public String getIdSubcodigo() {
		return idSubcodigo;
	}
	public void setIdSubcodigo(String idSubcodigo) {
		this.idSubcodigo = idSubcodigo;
	}
	public String getFisicaMoral() {
		return fisicaMoral;
	}
	public void setFisicaMoral(String fisicaMoral) {
		this.fisicaMoral = fisicaMoral;
	}
	public String getNacExt() {
		return nacExt;
	}
	public void setNacExt(String nacExt) {
		this.nacExt = nacExt;
	}
	public String getIdEstatusCheq() {
		return idEstatusCheq;
	}
	public void setIdEstatusCheq(String idEstatusCheq) {
		this.idEstatusCheq = idEstatusCheq;
	}
	public int getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(int invoiceId) {
		this.invoiceId = invoiceId;
	}
	public int getVendorId() {
		return vendorId;
	}
	public void setVendorId(int vendorId) {
		this.vendorId = vendorId;
	}
	public int getVendorSiteId() {
		return vendorSiteId;
	}
	public void setVendorSiteId(int vendorSiteId) {
		this.vendorSiteId = vendorSiteId;
	}
	public String getInvoiceType() {
		return invoiceType;
	}
	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}
	public String getNoFactura() {
		return noFactura;
	}
	public void setNoFactura(String noFactura) {
		this.noFactura = noFactura;
	}
	public int getGrupoPago() {
		return grupoPago;
	}
	public void setGrupoPago(int grupoPago) {
		this.grupoPago = grupoPago;
	}
	public double getIdRubro() {
		return idRubro;
	}
	public void setIdRubro(double idRubro) {
		this.idRubro = idRubro;
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
	public int getAgrupa3() {
		return agrupa3;
	}
	public void setAgrupa3(int agrupa3) {
		this.agrupa3 = agrupa3;
	}
	public String getPoHeaders() {
		return poHeaders;
	}
	public void setPoHeaders(String poHeaders) {
		this.poHeaders = poHeaders;
	}
	public String getAlternateVendorIdEft() {
		return alternateVendorIdEft;
	}
	public void setAlternateVendorIdEft(String alternateVendorIdEft) {
		this.alternateVendorIdEft = alternateVendorIdEft;
	}
	public String getAlternateVendorSiteIdEft() {
		return alternateVendorSiteIdEft;
	}
	public void setAlternateVendorSiteIdEft(String alternateVendorSiteIdEft) {
		this.alternateVendorSiteIdEft = alternateVendorSiteIdEft;
	}
	public String getInvoiceTypeLookupCode() {
		return invoiceTypeLookupCode;
	}
	public void setInvoiceTypeLookupCode(String invoiceTypeLookupCode) {
		this.invoiceTypeLookupCode = invoiceTypeLookupCode;
	}
	public String getBAutorizaImpre() {
		return bAutorizaImpre;
	}
	public void setBAutorizaImpre(String autorizaImpre) {
		bAutorizaImpre = autorizaImpre;
	}
	public int getFirmante1() {
		return firmante1;
	}
	public void setFirmante1(int firmante1) {
		this.firmante1 = firmante1;
	}
	public int getFirmante2() {
		return firmante2;
	}
	public void setFirmante2(int firmante2) {
		this.firmante2 = firmante2;
	}
	public String getOperMayEsp() {
		return operMayEsp;
	}
	public void setOperMayEsp(String operMayEsp) {
		this.operMayEsp = operMayEsp;
	}
	public String getCodBloqueo() {
		return codBloqueo;
	}
	public void setCodBloqueo(String codBloqueo) {
		this.codBloqueo = codBloqueo;
	}
	public String getRfc() {
		return rfc;
	}
	public void setRfc(String rfc) {
		this.rfc = rfc;
	}
	public String getBGenConta() {
		return bGenConta;
	}
	public void setBGenConta(String genConta) {
		bGenConta = genConta;
	}
	public int getNoPoliza() {
		return noPoliza;
	}
	public void setNoPoliza(int noPoliza) {
		this.noPoliza = noPoliza;
	}
	public int getCPoliza() {
		return cPoliza;
	}
	public void setCPoliza(int poliza) {
		cPoliza = poliza;
	}
	public int getCLote() {
		return cLote;
	}
	public void setCLote(int lote) {
		cLote = lote;
	}
	public int getCPeriodo() {
		return cPeriodo;
	}
	public void setCPeriodo(int periodo) {
		cPeriodo = periodo;
	}
	public int getCMes() {
		return cMes;
	}
	public void setCMes(int mes) {
		cMes = mes;
	}
	public String getDivision() {
		return division;
	}
	public void setDivision(String division) {
		this.division = division;
	}
	public String getIndIva() {
		return indIva;
	}
	public void setIndIva(String indIva) {
		this.indIva = indIva;
	}
	public String getContrarecibo() {
		return contrarecibo;
	}
	public void setContrarecibo(String contrarecibo) {
		this.contrarecibo = contrarecibo;
	}
	public String getDeudor() {
		return deudor;
	}
	public void setDeudor(String deudor) {
		this.deudor = deudor;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getRango() {
		return rango;
	}
	public void setRango(String rango) {
		this.rango = rango;
	}
	public int getNoPedido() {
		return noPedido;
	}
	public void setNoPedido(int noPedido) {
		this.noPedido = noPedido;
	}
	public String getReferenciaBan() {
		return referenciaBan;
	}
	public void setReferenciaBan(String referenciaBan) {
		this.referenciaBan = referenciaBan;
	}
	public String getHoraRecibo() {
		return horaRecibo;
	}
	public void setHoraRecibo(String horaRecibo) {
		this.horaRecibo = horaRecibo;
	}
	public Date getFecConciliaCaja() {
		return fecConciliaCaja;
	}
	public void setFecConciliaCaja(Date fecConciliaCaja) {
		this.fecConciliaCaja = fecConciliaCaja;
	}
	public Date getFecPropuesta() {
		return fecPropuesta;
	}
	public void setFecPropuesta(Date fecPropuesta) {
		this.fecPropuesta = fecPropuesta;
	}
	public double getImporteParcial() {
		return importeParcial;
	}
	public void setImporteParcial(double importeParcial) {
		this.importeParcial = importeParcial;
	}
	public String getBArchSeg() {
		return bArchSeg;
	}
	public void setBArchSeg(String archSeg) {
		bArchSeg = archSeg;
	}
	public String getCveControl() {
		return cveControl;
	}
	public void setCveControl(String cveControl) {
		this.cveControl = cveControl;
	}
	public int getIdBancoAnt() {
		return idBancoAnt;
	}
	public void setIdBancoAnt(int idBancoAnt) {
		this.idBancoAnt = idBancoAnt;
	}
	public String getIdChequeraAnt() {
		return idChequeraAnt;
	}
	public void setIdChequeraAnt(String idChequeraAnt) {
		this.idChequeraAnt = idChequeraAnt;
	}
	public String getOrigenMovAnt() {
		return origenMovAnt;
	}
	public void setOrigenMovAnt(String origenMovAnt) {
		this.origenMovAnt = origenMovAnt;
	}
	public String getNoClienteAnt() {
		return noClienteAnt;
	}
	public void setNoClienteAnt(String noClienteAnt) {
		this.noClienteAnt = noClienteAnt;
	}
	public String getIdServicioBe() {
		return idServicioBe;
	}
	public void setIdServicioBe(String idServicioBe) {
		this.idServicioBe = idServicioBe;
	}
	public String getEMail() {
		return eMail;
	}
	public void setEMail(String mail) {
		eMail = mail;
	}
	public String getClabe() {
		return clabe;
	}
	public void setClabe(String clabe) {
		this.clabe = clabe;
	}
	public int getIdArea() {
		return idArea;
	}
	public void setIdArea(int idArea) {
		this.idArea = idArea;
	}
	public String getMontoSobregiro() {
		return montoSobregiro;
	}
	public void setMontoSobregiro(String montoSobregiro) {
		this.montoSobregiro = montoSobregiro;
	}
	public int getIdGrupo() {
		return idGrupo;
	}
	public void setIdGrupo(int idGrupo) {
		this.idGrupo = idGrupo;
	}
	public String getTablaOrigen() {
		return tablaOrigen;
	}
	public void setTablaOrigen(String tablaOrigen) {
		this.tablaOrigen = tablaOrigen;
	}
	public Date getFechaContabilizacion() {
		return fechaContabilizacion;
	}
	public void setFechaContabilizacion(Date fechaContabilizacion) {
		this.fechaContabilizacion = fechaContabilizacion;
	}
	public String getBCertificado() {
		return bCertificado;
	}
	public void setBCertificado(String certificado) {
		bCertificado = certificado;
	}
	public Date getFecCertificacion() {
		return fecCertificacion;
	}
	public void setFecCertificacion(Date fecCertificacion) {
		this.fecCertificacion = fecCertificacion;
	}
	public String getAbba() {
		return abba;
	}
	public void setAbba(String abba) {
		this.abba = abba;
	}
	public String getSwift() {
		return swift;
	}
	public void setSwift(String swift) {
		this.swift = swift;
	}
	public String getNombreProvDivisas() {
		return nombreProvDivisas;
	}
	public void setNombreProvDivisas(String nombreProvDivisas) {
		this.nombreProvDivisas = nombreProvDivisas;
	}
	public String getBancoDivisas() {
		return bancoDivisas;
	}
	public void setBancoDivisas(String bancoDivisas) {
		this.bancoDivisas = bancoDivisas;
	}
	public String getDireccionDivisas() {
		return direccionDivisas;
	}
	public void setDireccionDivisas(String direccionDivisas) {
		this.direccionDivisas = direccionDivisas;
	}
	public String getCuentaDivisas() {
		return cuentaDivisas;
	}
	public void setCuentaDivisas(String cuentaDivisas) {
		this.cuentaDivisas = cuentaDivisas;
	}
	public String getSortCode() {
		return sortCode;
	}
	public void setSortCode(String sortCode) {
		this.sortCode = sortCode;
	}
	public String getAbi() {
		return abi;
	}
	public void setAbi(String abi) {
		this.abi = abi;
	}
	public String getCab() {
		return cab;
	}
	public void setCab(String cab) {
		this.cab = cab;
	}
	public String getComentario1() {
		return comentario1;
	}
	public void setComentario1(String comentario1) {
		this.comentario1 = comentario1;
	}
	public String getComentario2() {
		return comentario2;
	}
	public void setComentario2(String comentario2) {
		this.comentario2 = comentario2;
	}
	public String getInstFinan() {
		return instFinan;
	}
	public void setInstFinan(String instFinan) {
		this.instFinan = instFinan;
	}
	public int getNoMovimientos() {
		return noMovimientos;
	}
	public void setNoMovimientos(int noMovimientos) {
		this.noMovimientos = noMovimientos;
	}
	public String getPsFolios() {
		return psFolios;
	}
	public void setPsFolios(String psFolios) {
		this.psFolios = psFolios;
	}
	public int getIdProveedor() {
		return idProveedor;
	}
	public void setIdProveedor(int idProveedor) {
		this.idProveedor = idProveedor;
	}
	public String getEmpCheStr() {
		return empCheStr;
	}
	public void setEmpCheStr(String empCheStr) {
		this.empCheStr = empCheStr;
	}
	public String getIdBancoCityStr() {
		return idBancoCityStr;
	}
	public void setIdBancoCityStr(String idBancoCityStr) {
		this.idBancoCityStr = idBancoCityStr;
	}
	public int getFolioFondeo() {
		return folioFondeo;
	}
	public void setFolioFondeo(int folioFondeo) {
		this.folioFondeo = folioFondeo;
	}
	public boolean isSeleccionado() {
		return seleccionado;
	}
	public void setSeleccionado(boolean seleccionado) {
		this.seleccionado = seleccionado;
	}
	public String getSBandera() {
		return sBandera;
	}
	public void setSBandera(String bandera) {
		sBandera = bandera;
	}
	public double getInteres() {
		return interes;
	}
	public void setInteres(double interes) {
		this.interes = interes;
	}
	public double getIsr() {
		return isr;
	}
	public void setIsr(double isr) {
		this.isr = isr;
	}
	public double getTotalImporte() {
		return totalImporte;
	}
	public void setTotalImporte(double totalImporte) {
		this.totalImporte = totalImporte;
	}
	public String getNomContacto() {
		return nomContacto;
	}
	public void setNomContacto(String nomContacto) {
		this.nomContacto = nomContacto;
	}
	public int getCveOperacionA() {
		return cveOperacionA;
	}
	public void setCveOperacionA(int cveOperacionA) {
		this.cveOperacionA = cveOperacionA;
	}
	public int getCveOperacionB() {
		return cveOperacionB;
	}
	public void setCveOperacionB(int cveOperacionB) {
		this.cveOperacionB = cveOperacionB;
	}
	public int getCveOperacionC() {
		return cveOperacionC;
	}
	public void setCveOperacionC(int cveOperacionC) {
		this.cveOperacionC = cveOperacionC;
	}
	public int getFolioRefA() {
		return folioRefA;
	}
	public void setFolioRefA(int folioRefA) {
		this.folioRefA = folioRefA;
	}
	public int getFolioRefB() {
		return folioRefB;
	}
	public void setFolioRefB(int folioRefB) {
		this.folioRefB = folioRefB;
	}
	public int getFolioRefC() {
		return folioRefC;
	}
	public void setFolioRefC(int folioRefC) {
		this.folioRefC = folioRefC;
	}
	public int getFolioDetA() {
		return folioDetA;
	}
	public void setFolioDetA(int folioDetA) {
		this.folioDetA = folioDetA;
	}
	public int getFolioDetB() {
		return folioDetB;
	}
	public void setFolioDetB(int folioDetB) {
		this.folioDetB = folioDetB;
	}
	public int getFolioDetC() {
		return folioDetC;
	}
	public void setFolioDetC(int folioDetC) {
		this.folioDetC = folioDetC;
	}
	public String getDescBancoBenef() {
		return descBancoBenef;
	}
	public void setDescBancoBenef(String descBancoBenef) {
		this.descBancoBenef = descBancoBenef;
	}
	public double getInteresAnt() {
		return interesAnt;
	}
	public void setInteresAnt(double interesAnt) {
		this.interesAnt = interesAnt;
	}
	public double getImpuestoAnt() {
		return impuestoAnt;
	}
	public void setImpuestoAnt(double impuestoAnt) {
		this.impuestoAnt = impuestoAnt;
	}
	public int getTipoOperacionB() {
		return tipoOperacionB;
	}
	public void setTipoOperacionB(int tipoOperacionB) {
		this.tipoOperacionB = tipoOperacionB;
	}
	public int getTipoOperacionC() {
		return tipoOperacionC;
	}
	public void setTipoOperacionC(int tipoOperacionC) {
		this.tipoOperacionC = tipoOperacionC;
	}
	public String getNota() {
		return nota;
	}
	public void setNota(String nota) {
		this.nota = nota;
	}
	public String getContacto() {
		return contacto;
	}
	public void setContacto(String contacto) {
		this.contacto = contacto;
	}
	public String getsFecValor() {
		return sFecValor;
	}
	public void setsFecValor(String sFecValor) {
		this.sFecValor = sFecValor;
	}
	
}
