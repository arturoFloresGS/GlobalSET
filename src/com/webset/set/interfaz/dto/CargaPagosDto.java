package com.webset.set.interfaz.dto;

public class CargaPagosDto 
{
	//Para pantalla de importacion de pagos *******************************************************************
	
	int noEmpresa;
	String nomEmpresa;
	int noCuentaEmpresa;
	String origen;
	String noDocto;
	int idBanco;
	String idFormaPago;
	String descFormaPago;
	int idBancoBenef;
	String descBanco;
	int nuevoBanco;
	String fechaAlta;
	int usuarioAlta;
	String codDivisaSET;
	String idGrupo; 
	String idRubro;
	int idCaja;
	String idDivisa;
	String houseBank;
	int bancoPagador;
	String chequeraPagadora;
	String secuencia;
	String fecValor;
	String noBenef;
	double importe;
	String concepto;
	String mandante;
	String profitCenter;
	String centroCostos;
	String rfc;
	String curp;
	String clabe;
	String descPropuesta;
	String chequeraBenef;
	int sucursalBenef;
	String nombreBancoBenef;
	String paisBancoBenef;
	String cdBancoBenef;
	String swiftBenef;
	String iban;
	String forFurtherCredit;
	String nombreBancoIntermediario;
	String aba;
	String paisBancoIntermediario;
	String cdBancoIntermediario;
	String apellidoPaterno;
	String apellidoMaterno;
	String nombre;
	String razonSocial;
	String mail;
	String direccionBenef;
	String cdBenef;
	String regionBenef;
	String cpBenef;
	String paisBenef;
	String localizacion;
	int noCheque;
	String tipoDocumento;
	String conceptoRechazo;
	int totalRegistros;
	int totalError;
	int totalOK;
	double totalImporteMN;
	double totalImporteDLS;
	String tipoPersona;
	String idRubroMe;
	String conceptoCie;
	String referenciaCie;
	String estatusPropuesta;
	String fecFact;
	String estatusAlta;
	String codDivisaERP;
	
	String noFactura;
	double impSolic;
	double tipoCamb;
	String cveLeyen;
	String benefAlt;
	int idBcoAlt;
	String idChqAlt;
	private String gpoTesor;
	String codBloq;
	String indMayEs;
	String fechaImp;
	
	public String getGpoTesor() {
		return gpoTesor;
	}
	public void setGpoTesor(String gpoTesor) {
		this.gpoTesor = gpoTesor;
	}
	public String getCodDivisaERP() {
		return codDivisaERP;
	}
	public void setCodDivisaERP(String codDivisaERP) {
		this.codDivisaERP = codDivisaERP;
	}
	public String getEstatusAlta() {
		return estatusAlta;
	}
	public void setEstatusAlta(String estatusAlta) {
		this.estatusAlta = estatusAlta;
	}
	public String getFecFact() {
		return fecFact;
	}
	public void setFecFact(String fecFact) {
		this.fecFact = fecFact;
	}
	public String getIdGrupo() {
		return idGrupo;
	}
	public void setIdGrupo(String idGrupo) {
		this.idGrupo = idGrupo;
	}
	public String getEstatusPropuesta() {
		return estatusPropuesta;
	}
	public void setEstatusPropuesta(String estatusPropuesta) {
		this.estatusPropuesta = estatusPropuesta;
	}
	public String getIdRubroMe() {
		return idRubroMe;
	}
	public void setIdRubroMe(String idRubroMe) {
		this.idRubroMe = idRubroMe;
	}
	public String getConceptoCie() {
		return conceptoCie;
	}
	public void setConceptoCie(String conceptoCie) {
		this.conceptoCie = conceptoCie;
	}
	public String getReferenciaCie() {
		return referenciaCie;
	}
	public void setReferenciaCie(String referenciaCie) {
		this.referenciaCie = referenciaCie;
	}
	public String getRazonSocial() {
		return razonSocial;
	}
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	public String getTipoPersona() {
		return tipoPersona;
	}
	public void setTipoPersona(String tipoPersona) {
		this.tipoPersona = tipoPersona;
	}
	public int getTotalRegistros() {
		return totalRegistros;
	}
	public void setTotalRegistros(int totalRegistros) {
		this.totalRegistros = totalRegistros;
	}
	public int getTotalError() {
		return totalError;
	}
	public void setTotalError(int totalError) {
		this.totalError = totalError;
	}
	public int getTotalOK() {
		return totalOK;
	}
	public void setTotalOK(int totalOK) {
		this.totalOK = totalOK;
	}
	public double getTotalImporteMN() {
		return totalImporteMN;
	}
	public void setTotalImporteMN(double totalImporteMN) {
		this.totalImporteMN = totalImporteMN;
	}
	public double getTotalImporteDLS() {
		return totalImporteDLS;
	}
	public void setTotalImporteDLS(double totalImporteDLS) {
		this.totalImporteDLS = totalImporteDLS;
	}
	public String getMandante() {
		return mandante;
	}
	public void setMandante(String mandante) {
		this.mandante = mandante;
	}
	public String getProfitCenter() {
		return profitCenter;
	}
	public void setProfitCenter(String profitCenter) {
		this.profitCenter = profitCenter;
	}
	public String getCentroCostos() {
		return centroCostos;
	}
	public void setCentroCostos(String centroCostos) {
		this.centroCostos = centroCostos;
	}
	public String getRfc() {
		return rfc;
	}
	public void setRfc(String rfc) {
		this.rfc = rfc;
	}
	public String getCurp() {
		return curp;
	}
	public void setCurp(String curp) {
		this.curp = curp;
	}
	public String getClabe() {
		return clabe;
	}
	public void setClabe(String clabe) {
		this.clabe = clabe;
	}
	public String getDescPropuesta() {
		return descPropuesta;
	}
	public void setDescPropuesta(String descPropuesta) {
		this.descPropuesta = descPropuesta;
	}
	public String getChequeraBenef() {
		return chequeraBenef;
	}
	public void setChequeraBenef(String chequeraBenef) {
		this.chequeraBenef = chequeraBenef;
	}
	public int getSucursalBenef() {
		return sucursalBenef;
	}
	public void setSucursalBenef(int sucursalBenef) {
		this.sucursalBenef = sucursalBenef;
	}
	public String getNombreBancoBenef() {
		return nombreBancoBenef;
	}
	public void setNombreBancoBenef(String nombreBancoBenef) {
		this.nombreBancoBenef = nombreBancoBenef;
	}
	public String getPaisBancoBenef() {
		return paisBancoBenef;
	}
	public void setPaisBancoBenef(String paisBancoBenef) {
		this.paisBancoBenef = paisBancoBenef;
	}
	public String getCdBancoBenef() {
		return cdBancoBenef;
	}
	public void setCdBancoBenef(String cdBancoBenef) {
		this.cdBancoBenef = cdBancoBenef;
	}
	public String getSwiftBenef() {
		return swiftBenef;
	}
	public void setSwiftBenef(String swiftBenef) {
		this.swiftBenef = swiftBenef;
	}
	public String getIban() {
		return iban;
	}
	public void setIban(String iban) {
		this.iban = iban;
	}
	public String getForFurtherCredit() {
		return forFurtherCredit;
	}
	public void setForFurtherCredit(String forFurtherCredit) {
		this.forFurtherCredit = forFurtherCredit;
	}
	public String getNombreBancoIntermediario() {
		return nombreBancoIntermediario;
	}
	public void setNombreBancoIntermediario(String nombreBancoIntermediario) {
		this.nombreBancoIntermediario = nombreBancoIntermediario;
	}
	public String getAba() {
		return aba;
	}
	public void setAba(String aba) {
		this.aba = aba;
	}
	public String getPaisBancoIntermediario() {
		return paisBancoIntermediario;
	}
	public void setPaisBancoIntermediario(String paisBancoIntermediario) {
		this.paisBancoIntermediario = paisBancoIntermediario;
	}
	public String getCdBancoIntermediario() {
		return cdBancoIntermediario;
	}
	public void setCdBancoIntermediario(String cdBancoIntermediario) {
		this.cdBancoIntermediario = cdBancoIntermediario;
	}
	public String getApellidoPaterno() {
		return apellidoPaterno;
	}
	public void setApellidoPaterno(String apellidoPaterno) {
		this.apellidoPaterno = apellidoPaterno;
	}
	public String getApellidoMaterno() {
		return apellidoMaterno;
	}
	public void setApellidoMaterno(String apellidoMaterno) {
		this.apellidoMaterno = apellidoMaterno;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getDireccionBenef() {
		return direccionBenef;
	}
	public void setDireccionBenef(String direccionBenef) {
		this.direccionBenef = direccionBenef;
	}
	public String getCdBenef() {
		return cdBenef;
	}
	public void setCdBenef(String cdBenef) {
		this.cdBenef = cdBenef;
	}
	public String getRegionBenef() {
		return regionBenef;
	}
	public void setRegionBenef(String regionBenef) {
		this.regionBenef = regionBenef;
	}
	public String getCpBenef() {
		return cpBenef;
	}
	public void setCpBenef(String cpBenef) {
		this.cpBenef = cpBenef;
	}
	public String getPaisBenef() {
		return paisBenef;
	}
	public void setPaisBenef(String paisBenef) {
		this.paisBenef = paisBenef;
	}
	public String getLocalizacion() {
		return localizacion;
	}
	public void setLocalizacion(String localizacion) {
		this.localizacion = localizacion;
	}
	public int getNoCheque() {
		return noCheque;
	}
	public void setNoCheque(int noCheque) {
		this.noCheque = noCheque;
	}
	public String getTipoDocumento() {
		return tipoDocumento;
	}
	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	public String getConceptoRechazo() {
		return conceptoRechazo;
	}
	public void setConceptoRechazo(String conceptoRechazo) {
		this.conceptoRechazo = conceptoRechazo;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public String getNoBenef() {
		return noBenef;
	}
	public void setNoBenef(String noBenef) {
		this.noBenef = noBenef;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
	public String getFecValor() {
		return fecValor;
	}
	public void setFecValor(String fecValor) {
		this.fecValor = fecValor;
	}
	public String getSecuencia() {
		return secuencia;
	}
	public void setSecuencia(String secuencia) {
		this.secuencia = secuencia;
	}
	public String getChequeraPagadora() {
		return chequeraPagadora;
	}
	public void setChequeraPagadora(String chequeraPagadora) {
		this.chequeraPagadora = chequeraPagadora;
	}
	public int getBancoPagador() {
		return bancoPagador;
	}
	public void setBancoPagador(int bancoPagador) {
		this.bancoPagador = bancoPagador;
	}
	public String getHouseBank() {
		return houseBank;
	}
	public void setHouseBank(String houseBank) {
		this.houseBank = houseBank;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public int getIdCaja() {
		return idCaja;
	}
	public void setIdCaja(int idCaja) {
		this.idCaja = idCaja;
	}
	public String getIdRubro() {
		return idRubro;
	}
	public void setIdRubro(String idRubro) {
		this.idRubro = idRubro;
	}
	public String getCodDivisaSET() {
		return codDivisaSET;
	}
	public void setCodDivisaSET(String codDivisaSET) {
		this.codDivisaSET = codDivisaSET;
	}
	public int getUsuarioAlta() {
		return usuarioAlta;
	}
	public void setUsuarioAlta(int usuarioAlta) {
		this.usuarioAlta = usuarioAlta;
	}
	public String getFechaAlta() {
		return fechaAlta;
	}
	public void setFechaAlta(String fechaAlta) {
		this.fechaAlta = fechaAlta;
	}
	public int getNuevoBanco() {
		return nuevoBanco;
	}
	public void setNuevoBanco(int nuevoBanco) {
		this.nuevoBanco = nuevoBanco;
	}
	public int getIdBancoBenef() {
		return idBancoBenef;
	}
	public void setIdBancoBenef(int idBancoBenef) {
		this.idBancoBenef = idBancoBenef;
	}
	public String getDescBanco() {
		return descBanco;
	}
	public void setDescBanco(String descBanco) {
		this.descBanco = descBanco;
	}
	public String getIdFormaPago() {
		return idFormaPago;
	}
	public void setIdFormaPago(String idFormaPago) {
		this.idFormaPago = idFormaPago;
	}
	public String getDescFormaPago() {
		return descFormaPago;
	}
	public void setDescFormaPago(String descFormaPago) {
		this.descFormaPago = descFormaPago;
	}	
	public int getNoCuentaEmpresa() {
		return noCuentaEmpresa;
	}
	public void setNoCuentaEmpresa(int noCuentaEmpresa) {
		this.noCuentaEmpresa = noCuentaEmpresa;
	}	
	public String getOrigen() {
		return origen;
	}
	public void setOrigen(String origen) {
		this.origen = origen;
	}
	public String getNoDocto() {
		return noDocto;
	}
	public void setNoDocto(String noDocto) {
		this.noDocto = noDocto;
	}
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
	public String getNomEmpresa() {
		return nomEmpresa;
	}
	public void setNomEmpresa(String nomEmpresa) {
		this.nomEmpresa = nomEmpresa;
	}
	public String getNoFactura() {
		return noFactura;
	}
	public void setNoFactura(String noFactura) {
		this.noFactura = noFactura;
	}
	public double getImpSolic() {
		return impSolic;
	}
	public void setImpSolic(double impSolic) {
		this.impSolic = impSolic;
	}
	public double getTipoCamb() {
		return tipoCamb;
	}
	public void setTipoCamb(double tipoCamb) {
		this.tipoCamb = tipoCamb;
	}
	public String getCveLeyen() {
		return cveLeyen;
	}
	public void setCveLeyen(String cveLeyen) {
		this.cveLeyen = cveLeyen;
	}
	public String getBenefAlt() {
		return benefAlt;
	}
	public void setBenefAlt(String benefAlt) {
		this.benefAlt = benefAlt;
	}
	public int getIdBcoAlt() {
		return idBcoAlt;
	}
	public void setIdBcoAlt(int idBcoAlt) {
		this.idBcoAlt = idBcoAlt;
	}
	public String getIdChqAlt() {
		return idChqAlt;
	}
	public void setIdChqAlt(String idChqAlt) {
		this.idChqAlt = idChqAlt;
	}
	
	
	public String getCodBloq() {
		return codBloq;
	}
	public void setCodBloq(String codBloq) {
		this.codBloq = codBloq;
	}
	public String getIndMayEs() {
		return indMayEs;
	}
	public void setIndMayEs(String indMayEs) {
		this.indMayEs = indMayEs;
	}
	public String getFechaImp() {
		return fechaImp;
	}
	public void setFechaImp(String fechaImp) {
		this.fechaImp = fechaImp;
	}
}
