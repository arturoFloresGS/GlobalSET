package com.webset.set.bancaelectronica.dto;

import java.util.Date;

public class ArchTransferDto {
	private int idBanco;
	private int noUsuarioAlta;
	private int noUsuarioModif;
	private int registros;
	private int todas;
	private int tipoCriterio;
	private int noFolioDet;
	private int idBancoBenef;
	private int idBancoCity;
	private int noEmpresa;
	private int noLinea;
	private int noCuenta;
	private int ruta;
	private String horaRecibo;
	private double importe;
	
	private Date fecTrans;
	private Date fecRetrans;
	private String nomArch;		
	private String estatusEnArchivo;	
	private String fecInicial;
	private String fecFinal;
	private String fecArchivo;
	private String noDocto;
	private String descBanco;
	private String fecTransferencia;
	private String fecRetransferencia;
	private String descEstatus;
	private String fecValor;
	private String idChequera;
	private String beneficiario;
	private String bEntregado;
	private String idEstatusMov;
	private String idDivisa;
	private String concepto;
	private String observacion;
	private String nomEmpresa;
	private String noCliente;
	private String descOperacion;
	private String descBancoBenef;
	private String idChequeraBenef;
	private String plaza;
	private String sucursal;
	private String institucionFinan;
	private String aba;
	private String plazaBenef;
	private String layoutComerica;
	private String fecHoy;
	private String sucOrigen;
	private String idChequeraBenefReal;
	private String descBancoInbursa;
	private String idClabeBenef;
	private String tipoEnvioComerica;
	private String idChequeraBanamex;
	private String abaSwift;
	private String swiftInter;
	private String bancoInter;
	private String abaCorres;
	private String swiftCorres;
	private String bancoCorres;
	private String idTipoOperacion;
	private String equivalePersona;
	private String noFactura;
	private String idServicioBE;
	private String RfcBenef;
	private String fecValorOriginal;
	private String abaInter;
	private String rfc;
	private String contratoWlink;
	private String usuarioBital;
	private String servicioBital;
	private String complemento;
	private String especiales;
	private String tipoEnvioLayoutCtas;
	private String paisBenef;
	private String direccionBenef;
	private String telefonoBenef;
	private String referenciaBan;
	private String valor;
	private String tipoChequera;
	private String referencia;
	private String idAbaSwift;
	private String idAbaSwiftInter;
	private String idAbaSwiftCorres;
	private String abaSwiftCorres;
	private String hora;
	private String folioDetReferencia;
	private String origenMov;
	private String color;
	private String nomUsrAlta;
	private String nomUsrModif;
	private String abaSwiftInter;	
	private String bChequeOcurre;
	private String tipoEnvioLayout;
	
	public String getHoraRecibo() {
		return horaRecibo;
	}
	public void setHoraRecibo(String horaRecibo) {
		this.horaRecibo = horaRecibo;
	}
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public int getNoUsuarioAlta() {
		return noUsuarioAlta;
	}
	public void setNoUsuarioAlta(int noUsuarioAlta) {
		this.noUsuarioAlta = noUsuarioAlta;
	}
	public int getNoUsuarioModif() {
		return noUsuarioModif;
	}
	public void setNoUsuarioModif(int noUsuarioModif) {
		this.noUsuarioModif = noUsuarioModif;
	}
	public int getRegistros() {
		return registros;
	}
	public void setRegistros(int registros) {
		this.registros = registros;
	}
	public int getTodas() {
		return todas;
	}
	public void setTodas(int todas) {
		this.todas = todas;
	}
	public int getTipoCriterio() {
		return tipoCriterio;
	}
	public void setTipoCriterio(int tipoCriterio) {
		this.tipoCriterio = tipoCriterio;
	}
	public int getNoFolioDet() {
		return noFolioDet;
	}
	public void setNoFolioDet(int noFolioDet) {
		this.noFolioDet = noFolioDet;
	}
	public int getIdBancoBenef() {
		return idBancoBenef;
	}
	public void setIdBancoBenef(int idBancoBenef) {
		this.idBancoBenef = idBancoBenef;
	}
	public int getIdBancoCity() {
		return idBancoCity;
	}
	public void setIdBancoCity(int idBancoCity) {
		this.idBancoCity = idBancoCity;
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
	public int getRuta() {
		return ruta;
	}
	public void setRuta(int ruta) {
		this.ruta = ruta;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}
	public Date getFecTrans() {
		return fecTrans;
	}
	public void setFecTrans(Date fecTrans) {
		this.fecTrans = fecTrans;
	}
	public Date getFecRetrans() {
		return fecRetrans;
	}
	public void setFecRetrans(Date fecRetrans) {
		this.fecRetrans = fecRetrans;
	}
	public String getNomArch() {
		return nomArch;
	}
	public void setNomArch(String nomArch) {
		this.nomArch = nomArch;
	}
	public String getEstatusEnArchivo() {
		return estatusEnArchivo;
	}
	public void setEstatusEnArchivo(String estatusEnArchivo) {
		this.estatusEnArchivo = estatusEnArchivo;
	}
	public String getFecInicial() {
		return fecInicial;
	}
	public void setFecInicial(String fecInicial) {
		this.fecInicial = fecInicial;
	}
	public String getFecFinal() {
		return fecFinal;
	}
	public void setFecFinal(String fecFinal) {
		this.fecFinal = fecFinal;
	}
	public String getFecArchivo() {
		return fecArchivo;
	}
	public void setFecArchivo(String fecArchivo) {
		this.fecArchivo = fecArchivo;
	}
	public String getNoDocto() {
		return noDocto;
	}
	public void setNoDocto(String noDocto) {
		this.noDocto = noDocto;
	}
	public String getDescBanco() {
		return descBanco;
	}
	public void setDescBanco(String descBanco) {
		this.descBanco = descBanco;
	}
	public String getFecTransferencia() {
		return fecTransferencia;
	}
	public void setFecTransferencia(String fecTransferencia) {
		this.fecTransferencia = fecTransferencia;
	}
	public String getFecRetransferencia() {
		return fecRetransferencia;
	}
	public void setFecRetransferencia(String fecRetransferencia) {
		this.fecRetransferencia = fecRetransferencia;
	}
	public String getDescEstatus() {
		return descEstatus;
	}
	public void setDescEstatus(String descEstatus) {
		this.descEstatus = descEstatus;
	}
	public String getFecValor() {
		return fecValor;
	}
	public void setFecValor(String fecValor) {
		this.fecValor = fecValor;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public String getBeneficiario() {
		return beneficiario;
	}
	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}
	public String getBEntregado() {
		return bEntregado;
	}
	public void setBEntregado(String entregado) {
		bEntregado = entregado;
	}
	public String getIdEstatusMov() {
		return idEstatusMov;
	}
	public void setIdEstatusMov(String idEstatusMov) {
		this.idEstatusMov = idEstatusMov;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	public String getNomEmpresa() {
		return nomEmpresa;
	}
	public void setNomEmpresa(String nomEmpresa) {
		this.nomEmpresa = nomEmpresa;
	}
	public String getNoCliente() {
		return noCliente;
	}
	public void setNoCliente(String noCliente) {
		this.noCliente = noCliente;
	}
	public String getDescOperacion() {
		return descOperacion;
	}
	public void setDescOperacion(String descOperacion) {
		this.descOperacion = descOperacion;
	}
	public String getDescBancoBenef() {
		return descBancoBenef;
	}
	public void setDescBancoBenef(String descBancoBenef) {
		this.descBancoBenef = descBancoBenef;
	}
	public String getIdChequeraBenef() {
		return idChequeraBenef;
	}
	public void setIdChequeraBenef(String idChequeraBenef) {
		this.idChequeraBenef = idChequeraBenef;
	}
	public String getPlaza() {
		return plaza;
	}
	public void setPlaza(String plaza) {
		this.plaza = plaza;
	}
	public String getSucursal() {
		return sucursal;
	}
	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}
	public String getInstitucionFinan() {
		return institucionFinan;
	}
	public void setInstitucionFinan(String institucionFinan) {
		this.institucionFinan = institucionFinan;
	}
	public String getAba() {
		return aba;
	}
	public void setAba(String aba) {
		this.aba = aba;
	}
	public String getPlazaBenef() {
		return plazaBenef;
	}
	public void setPlazaBenef(String plazaBenef) {
		this.plazaBenef = plazaBenef;
	}
	public String getLayoutComerica() {
		return layoutComerica;
	}
	public void setLayoutComerica(String layoutComerica) {
		this.layoutComerica = layoutComerica;
	}
	public String getFecHoy() {
		return fecHoy;
	}
	public void setFecHoy(String fecHoy) {
		this.fecHoy = fecHoy;
	}
	public String getSucOrigen() {
		return sucOrigen;
	}
	public void setSucOrigen(String sucOrigen) {
		this.sucOrigen = sucOrigen;
	}
	public String getIdChequeraBenefReal() {
		return idChequeraBenefReal;
	}
	public void setIdChequeraBenefReal(String idChequeraBenefReal) {
		this.idChequeraBenefReal = idChequeraBenefReal;
	}
	public String getDescBancoInbursa() {
		return descBancoInbursa;
	}
	public void setDescBancoInbursa(String descBancoInbursa) {
		this.descBancoInbursa = descBancoInbursa;
	}
	public String getIdClabeBenef() {
		return idClabeBenef;
	}
	public void setIdClabeBenef(String idClabeBenef) {
		this.idClabeBenef = idClabeBenef;
	}
	public String getTipoEnvioComerica() {
		return tipoEnvioComerica;
	}
	public void setTipoEnvioComerica(String tipoEnvioComerica) {
		this.tipoEnvioComerica = tipoEnvioComerica;
	}
	public String getIdChequeraBanamex() {
		return idChequeraBanamex;
	}
	public void setIdChequeraBanamex(String idChequeraBanamex) {
		this.idChequeraBanamex = idChequeraBanamex;
	}
	public String getAbaSwift() {
		return abaSwift;
	}
	public void setAbaSwift(String abaSwift) {
		this.abaSwift = abaSwift;
	}
	public String getSwiftInter() {
		return swiftInter;
	}
	public void setSwiftInter(String swiftInter) {
		this.swiftInter = swiftInter;
	}
	public String getBancoInter() {
		return bancoInter;
	}
	public void setBancoInter(String bancoInter) {
		this.bancoInter = bancoInter;
	}
	public String getAbaCorres() {
		return abaCorres;
	}
	public void setAbaCorres(String abaCorres) {
		this.abaCorres = abaCorres;
	}
	public String getSwiftCorres() {
		return swiftCorres;
	}
	public void setSwiftCorres(String swiftCorres) {
		this.swiftCorres = swiftCorres;
	}
	public String getBancoCorres() {
		return bancoCorres;
	}
	public void setBancoCorres(String bancoCorres) {
		this.bancoCorres = bancoCorres;
	}
	public String getIdTipoOperacion() {
		return idTipoOperacion;
	}
	public void setIdTipoOperacion(String idTipoOperacion) {
		this.idTipoOperacion = idTipoOperacion;
	}
	public String getEquivalePersona() {
		return equivalePersona;
	}
	public void setEquivalePersona(String equivalePersona) {
		this.equivalePersona = equivalePersona;
	}
	public String getNoFactura() {
		return noFactura;
	}
	public void setNoFactura(String noFactura) {
		this.noFactura = noFactura;
	}
	public String getIdServicioBE() {
		return idServicioBE;
	}
	public void setIdServicioBE(String idServicioBE) {
		this.idServicioBE = idServicioBE;
	}
	public String getRfcBenef() {
		return RfcBenef;
	}
	public void setRfcBenef(String rfcBenef) {
		RfcBenef = rfcBenef;
	}
	public String getFecValorOriginal() {
		return fecValorOriginal;
	}
	public void setFecValorOriginal(String fecValorOriginal) {
		this.fecValorOriginal = fecValorOriginal;
	}
	public String getAbaInter() {
		return abaInter;
	}
	public void setAbaInter(String abaInter) {
		this.abaInter = abaInter;
	}
	public String getRfc() {
		return rfc;
	}
	public void setRfc(String rfc) {
		this.rfc = rfc;
	}
	public String getContratoWlink() {
		return contratoWlink;
	}
	public void setContratoWlink(String contratoWlink) {
		this.contratoWlink = contratoWlink;
	}
	public String getUsuarioBital() {
		return usuarioBital;
	}
	public void setUsuarioBital(String usuarioBital) {
		this.usuarioBital = usuarioBital;
	}
	public String getServicioBital() {
		return servicioBital;
	}
	public void setServicioBital(String servicioBital) {
		this.servicioBital = servicioBital;
	}
	public String getComplemento() {
		return complemento;
	}
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	public String getEspeciales() {
		return especiales;
	}
	public void setEspeciales(String especiales) {
		this.especiales = especiales;
	}
	public String getTipoEnvioLayoutCtas() {
		return tipoEnvioLayoutCtas;
	}
	public void setTipoEnvioLayoutCtas(String tipoEnvioLayoutCtas) {
		this.tipoEnvioLayoutCtas = tipoEnvioLayoutCtas;
	}
	public String getPaisBenef() {
		return paisBenef;
	}
	public void setPaisBenef(String paisBenef) {
		this.paisBenef = paisBenef;
	}
	public String getDireccionBenef() {
		return direccionBenef;
	}
	public void setDireccionBenef(String direccionBenef) {
		this.direccionBenef = direccionBenef;
	}
	public String getTelefonoBenef() {
		return telefonoBenef;
	}
	public void setTelefonoBenef(String telefonoBenef) {
		this.telefonoBenef = telefonoBenef;
	}
	public String getReferenciaBan() {
		return referenciaBan;
	}
	public void setReferenciaBan(String referenciaBan) {
		this.referenciaBan = referenciaBan;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	public String getTipoChequera() {
		return tipoChequera;
	}
	public void setTipoChequera(String tipoChequera) {
		this.tipoChequera = tipoChequera;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getIdAbaSwift() {
		return idAbaSwift;
	}
	public void setIdAbaSwift(String idAbaSwift) {
		this.idAbaSwift = idAbaSwift;
	}
	public String getIdAbaSwiftInter() {
		return idAbaSwiftInter;
	}
	public void setIdAbaSwiftInter(String idAbaSwiftInter) {
		this.idAbaSwiftInter = idAbaSwiftInter;
	}
	public String getIdAbaSwiftCorres() {
		return idAbaSwiftCorres;
	}
	public void setIdAbaSwiftCorres(String idAbaSwiftCorres) {
		this.idAbaSwiftCorres = idAbaSwiftCorres;
	}
	public String getAbaSwiftCorres() {
		return abaSwiftCorres;
	}
	public void setAbaSwiftCorres(String abaSwiftCorres) {
		this.abaSwiftCorres = abaSwiftCorres;
	}
	public String getHora() {
		return hora;
	}
	public void setHora(String hora) {
		this.hora = hora;
	}
	public String getFolioDetReferencia() {
		return folioDetReferencia;
	}
	public void setFolioDetReferencia(String folioDetReferencia) {
		this.folioDetReferencia = folioDetReferencia;
	}
	public String getOrigenMov() {
		return origenMov;
	}
	public void setOrigenMov(String origenMov) {
		this.origenMov = origenMov;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getNomUsrAlta() {
		return nomUsrAlta;
	}
	public void setNomUsrAlta(String nomUsrAlta) {
		this.nomUsrAlta = nomUsrAlta;
	}
	public String getNomUsrModif() {
		return nomUsrModif;
	}
	public void setNomUsrModif(String nomUsrModif) {
		this.nomUsrModif = nomUsrModif;
	}
	public String getAbaSwiftInter() {
		return abaSwiftInter;
	}
	public void setAbaSwiftInter(String abaSwiftInter) {
		this.abaSwiftInter = abaSwiftInter;
	}
	public String getBChequeOcurre() {
		return bChequeOcurre;
	}
	public void setBChequeOcurre(String chequeOcurre) {
		bChequeOcurre = chequeOcurre;
	}
	public String getTipoEnvioLayout() {
		return tipoEnvioLayout;
	}
	public void setTipoEnvioLayout(String tipoEnvioLayout) {
		this.tipoEnvioLayout = tipoEnvioLayout;
	}
}
