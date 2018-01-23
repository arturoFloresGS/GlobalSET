package com.webset.set.utilerias.dto;
import java.util.Date;
/**
 * Dto que representa a la tabla cat_cta_banco
 * @author crgarcia
 *
 */
public class CatCtaBancoDto {
	
	private int	noEmpresa;	
	private int	idBanco;	
	private int	ultCheqImpreso;	
	private int		usuarioAlta;	
	private int		subCuenta;	
	private int 	ssubCuenta;
	private int	usuarioModif;	
	private int	periodoConciliacion;
	private int usuarioConci;
	private int	usuarioConciliacionBc;
	
	private double	saldoInicial;	
	private double	cargo;	
	private double	abono;
	private double	saldoFinal;	
	private double	cargoSbc;
	private double	abonoSbc;	
	private double	saldoBancoIni;	
	private double	saldoMinimo;	
	private double	saldoContaIni;	
	private double	sobregiro;	
	
	private Date	fecAlta;
	private Date	fecModif;	
	private Date	fecConciliacion;	
	private Date	fecConciIni;
	private Date	fechaBanca;
	private Date	fecConciliacionBc;	
	private Date	periodoConciliacionBc;	
	
	private String	idChequera;	
	private String	descChequera;	
	private String	idDivisa;	
	private String 	descPlaza;	
	private String	descSucursal;	
	private String	tipoChequera;	
	private String	bConcentradora;
	private String	nacExt;	
	private String	bTraspaso;	
	private String	bConcilia;	
	private String	idEstatusCta;	
	private String	bCheque;	
	private String	bTransferencia;	
	private String	bChequeOcurre;	
	private String 	bExporta;	
	private String	idClabe;	
	private String	bImpreContinua;	
	private String	aba;	
	private String	swiftCode;	
	private String	idDivision;	
	private String	pagoMass;	
	private String	bCargoEnCuenta;	
	private String	mismoBanco;
	private String descBanco;
	
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
	public int getUltCheqImpreso() {
		return ultCheqImpreso;
	}
	public void setUltCheqImpreso(int ultCheqImpreso) {
		this.ultCheqImpreso = ultCheqImpreso;
	}
	public int getUsuarioAlta() {
		return usuarioAlta;
	}
	public void setUsuarioAlta(int usuarioAlta) {
		this.usuarioAlta = usuarioAlta;
	}
	public int getSubCuenta() {
		return subCuenta;
	}
	public void setSubCuenta(int subCuenta) {
		this.subCuenta = subCuenta;
	}
	public int getSsubCuenta() {
		return ssubCuenta;
	}
	public void setSsubCuenta(int ssubCuenta) {
		this.ssubCuenta = ssubCuenta;
	}
	public int getUsuarioModif() {
		return usuarioModif;
	}
	public void setUsuarioModif(int usuarioModif) {
		this.usuarioModif = usuarioModif;
	}
	public int getPeriodoConciliacion() {
		return periodoConciliacion;
	}
	public void setPeriodoConciliacion(int periodoConciliacion) {
		this.periodoConciliacion = periodoConciliacion;
	}
	public int getUsuarioConci() {
		return usuarioConci;
	}
	public void setUsuarioConci(int usuarioConci) {
		this.usuarioConci = usuarioConci;
	}
	public int getUsuarioConciliacionBc() {
		return usuarioConciliacionBc;
	}
	public void setUsuarioConciliacionBc(int usuarioConciliacionBc) {
		this.usuarioConciliacionBc = usuarioConciliacionBc;
	}
	public double getSaldoInicial() {
		return saldoInicial;
	}
	public void setSaldoInicial(double saldoInicial) {
		this.saldoInicial = saldoInicial;
	}
	public double getCargo() {
		return cargo;
	}
	public void setCargo(double cargo) {
		this.cargo = cargo;
	}
	public double getAbono() {
		return abono;
	}
	public void setAbono(double abono) {
		this.abono = abono;
	}
	public double getSaldoFinal() {
		return saldoFinal;
	}
	public void setSaldoFinal(double saldoFinal) {
		this.saldoFinal = saldoFinal;
	}
	public double getCargoSbc() {
		return cargoSbc;
	}
	public void setCargoSbc(double cargoSbc) {
		this.cargoSbc = cargoSbc;
	}
	public double getAbonoSbc() {
		return abonoSbc;
	}
	public void setAbonoSbc(double abonoSbc) {
		this.abonoSbc = abonoSbc;
	}
	public double getSaldoBancoIni() {
		return saldoBancoIni;
	}
	public void setSaldoBancoIni(double saldoBancoIni) {
		this.saldoBancoIni = saldoBancoIni;
	}
	public double getSaldoMinimo() {
		return saldoMinimo;
	}
	public void setSaldoMinimo(double saldoMinimo) {
		this.saldoMinimo = saldoMinimo;
	}
	public double getSaldoContaIni() {
		return saldoContaIni;
	}
	public void setSaldoContaIni(double saldoContaIni) {
		this.saldoContaIni = saldoContaIni;
	}
	public double getSobregiro() {
		return sobregiro;
	}
	public void setSobregiro(double sobregiro) {
		this.sobregiro = sobregiro;
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
	public Date getFecConciliacion() {
		return fecConciliacion;
	}
	public void setFecConciliacion(Date fecConciliacion) {
		this.fecConciliacion = fecConciliacion;
	}
	public Date getFecConciIni() {
		return fecConciIni;
	}
	public void setFecConciIni(Date fecConciIni) {
		this.fecConciIni = fecConciIni;
	}
	public Date getFechaBanca() {
		return fechaBanca;
	}
	public void setFechaBanca(Date fechaBanca) {
		this.fechaBanca = fechaBanca;
	}
	public Date getFecConciliacionBc() {
		return fecConciliacionBc;
	}
	public void setFecConciliacionBc(Date fecConciliacionBc) {
		this.fecConciliacionBc = fecConciliacionBc;
	}
	public Date getPeriodoConciliacionBc() {
		return periodoConciliacionBc;
	}
	public void setPeriodoConciliacionBc(Date periodoConciliacionBc) {
		this.periodoConciliacionBc = periodoConciliacionBc;
	}
	public String getIdChequera() {
		return idChequera;
	}
	public void setIdChequera(String idChequera) {
		this.idChequera = idChequera;
	}
	public String getDescChequera() {
		return descChequera;
	}
	public void setDescChequera(String descChequera) {
		this.descChequera = descChequera;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
	public String getDescPlaza() {
		return descPlaza;
	}
	public void setDescPlaza(String descPlaza) {
		this.descPlaza = descPlaza;
	}
	public String getDescSucursal() {
		return descSucursal;
	}
	public void setDescSucursal(String descSucursal) {
		this.descSucursal = descSucursal;
	}
	public String getTipoChequera() {
		return tipoChequera;
	}
	public void setTipoChequera(String tipoChequera) {
		this.tipoChequera = tipoChequera;
	}
	public String getBConcentradora() {
		return bConcentradora;
	}
	public void setBConcentradora(String concentradora) {
		bConcentradora = concentradora;
	}
	public String getNacExt() {
		return nacExt;
	}
	public void setNacExt(String nacExt) {
		this.nacExt = nacExt;
	}
	public String getBTraspaso() {
		return bTraspaso;
	}
	public void setBTraspaso(String traspaso) {
		bTraspaso = traspaso;
	}
	public String getBConcilia() {
		return bConcilia;
	}
	public void setBConcilia(String concilia) {
		bConcilia = concilia;
	}
	public String getIdEstatusCta() {
		return idEstatusCta;
	}
	public void setIdEstatusCta(String idEstatusCta) {
		this.idEstatusCta = idEstatusCta;
	}
	public String getBCheque() {
		return bCheque;
	}
	public void setBCheque(String cheque) {
		bCheque = cheque;
	}
	public String getBTransferencia() {
		return bTransferencia;
	}
	public void setBTransferencia(String transferencia) {
		bTransferencia = transferencia;
	}
	public String getBChequeOcurre() {
		return bChequeOcurre;
	}
	public void setBChequeOcurre(String chequeOcurre) {
		bChequeOcurre = chequeOcurre;
	}
	public String getBExporta() {
		return bExporta;
	}
	public void setBExporta(String exporta) {
		bExporta = exporta;
	}
	public String getIdClabe() {
		return idClabe;
	}
	public void setIdClabe(String idClabe) {
		this.idClabe = idClabe;
	}
	public String getBImpreContinua() {
		return bImpreContinua;
	}
	public void setBImpreContinua(String impreContinua) {
		bImpreContinua = impreContinua;
	}
	public String getAba() {
		return aba;
	}
	public void setAba(String aba) {
		this.aba = aba;
	}
	public String getSwiftCode() {
		return swiftCode;
	}
	public void setSwiftCode(String swiftCode) {
		this.swiftCode = swiftCode;
	}
	public String getIdDivision() {
		return idDivision;
	}
	public void setIdDivision(String idDivision) {
		this.idDivision = idDivision;
	}
	public String getPagoMass() {
		return pagoMass;
	}
	public void setPagoMass(String pagoMass) {
		this.pagoMass = pagoMass;
	}
	public String getBCargoEnCuenta() {
		return bCargoEnCuenta;
	}
	public void setBCargoEnCuenta(String cargoEnCuenta) {
		bCargoEnCuenta = cargoEnCuenta;
	}
	public String getMismoBanco() {
		return mismoBanco;
	}
	public void setMismoBanco(String mismoBanco) {
		this.mismoBanco = mismoBanco;
	}
	public String getDescBanco() {
		return descBanco;
	}
	public void setDescBanco(String descBanco) {
		this.descBanco = descBanco;
	}	

		

}
