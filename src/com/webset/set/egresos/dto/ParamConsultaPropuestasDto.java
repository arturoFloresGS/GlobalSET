package com.webset.set.egresos.dto;

public class ParamConsultaPropuestasDto {
	
	private int  grupoEmpresa;
    private int  pvGrupoRubro;
    private String  fechaIni;
    private String  fechaFin;
    private int idCliente;
    private int idUsuario;
    private String pvsDivision;
    private boolean soloPropAct;
    private boolean soloMisProp;
    int noEmpresa;
    String noDocto;
    String invoice;
    int noFolioDet;
    int idBanco;
    String idChequera;
    int noCheque;
    double importe;
    String idEstatusMov;
    String idDivisa;
    int usuarioModif;
    int idCaja;
    String beneficiario;
    String concepto;
    String fecCheque;
    int causa;
    String fecHoy;
    int noFolioMov;
    String origenMov;
    int idTipoOperacion;
    int idFormaPago;
    String bEntregado;
    String idTipoMovto;
    int noCuenta;
    int loteEntrada;
    String fecConfTrans;
    String bSBC;
    String tipoRevividor;
    String tipoCancelacion;
    boolean automatico;
    String resultado;
    
    String nomUsuarios;
    boolean todasPropuestas;
    
	public String getResultado() {
		return resultado;
	}
	public void setResultado(String resultado) {
		this.resultado = resultado;
	}
	public boolean isAutomatico() {
		return automatico;
	}
	public void setAutomatico(boolean automatico) {
		this.automatico = automatico;
	}
	public String getTipoCancelacion() {
		return tipoCancelacion;
	}
	public void setTipoCancelacion(String tipoCancelacion) {
		this.tipoCancelacion = tipoCancelacion;
	}
	public String getTipoRevividor() {
		return tipoRevividor;
	}
	public void setTipoRevividor(String tipoRevividor) {
		this.tipoRevividor = tipoRevividor;
	}
	public String getOrigenMov() {
		return origenMov;
	}
	public void setOrigenMov(String origenMov) {
		this.origenMov = origenMov;
	}
	public int getIdTipoOperacion() {
		return idTipoOperacion;
	}
	public void setIdTipoOperacion(int idTipoOperacion) {
		this.idTipoOperacion = idTipoOperacion;
	}
	public int getIdFormaPago() {
		return idFormaPago;
	}
	public void setIdFormaPago(int idFormaPago) {
		this.idFormaPago = idFormaPago;
	}
	public String getBEntregado() {
		return bEntregado;
	}
	public void setBEntregado(String entregado) {
		bEntregado = entregado;
	}
	public String getIdTipoMovto() {
		return idTipoMovto;
	}
	public void setIdTipoMovto(String idTipoMovto) {
		this.idTipoMovto = idTipoMovto;
	}
	public int getNoCuenta() {
		return noCuenta;
	}
	public void setNoCuenta(int noCuenta) {
		this.noCuenta = noCuenta;
	}
	public int getLoteEntrada() {
		return loteEntrada;
	}
	public void setLoteEntrada(int loteEntrada) {
		this.loteEntrada = loteEntrada;
	}
	public String getFecConfTrans() {
		return fecConfTrans;
	}
	public void setFecConfTrans(String fecConfTrans) {
		this.fecConfTrans = fecConfTrans;
	}
	public String getBSBC() {
		return bSBC;
	}
	public void setBSBC(String bsbc) {
		bSBC = bsbc;
	}
	public int getNoFolioMov() {
		return noFolioMov;
	}
	public void setNoFolioMov(int noFolioMov) {
		this.noFolioMov = noFolioMov;
	}
	public String getFecHoy() {
		return fecHoy;
	}
	public void setFecHoy(String fecHoy) {
		this.fecHoy = fecHoy;
	}
	public int getCausa() {
		return causa;
	}
	public void setCausa(int causa) {
		this.causa = causa;
	}
	public int getNoFolioDet() {
		return noFolioDet;
	}
	public void setNoFolioDet(int noFolioDet) {
		this.noFolioDet = noFolioDet;
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
	public int getNoCheque() {
		return noCheque;
	}
	public void setNoCheque(int noCheque) {
		this.noCheque = noCheque;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
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
	public int getUsuarioModif() {
		return usuarioModif;
	}
	public void setUsuarioModif(int usuarioModif) {
		this.usuarioModif = usuarioModif;
	}
	public int getIdCaja() {
		return idCaja;
	}
	public void setIdCaja(int idCaja) {
		this.idCaja = idCaja;
	}
	public String getBeneficiario() {
		return beneficiario;
	}
	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public String getFecCheque() {
		return fecCheque;
	}
	public void setFecCheque(String fecCheque) {
		this.fecCheque = fecCheque;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public String getNoDocto() {
		return noDocto;
	}
	public void setNoDocto(String noDocto) {
		this.noDocto = noDocto;
	}
	public String getInvoice() {
		return invoice;
	}
	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}
	public int getGrupoEmpresa() {
		return grupoEmpresa;
	}
	public void setGrupoEmpresa(int grupoEmpresa) {
		this.grupoEmpresa = grupoEmpresa;
	}
	public int getPvGrupoRubro() {
		return pvGrupoRubro;
	}
	public void setPvGrupoRubro(int pvGrupoRubro) {
		this.pvGrupoRubro = pvGrupoRubro;
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
	public int getIdCliente() {
		return idCliente;
	}
	public void setIdCliente(int idCliente) {
		this.idCliente = idCliente;
	}
	public int getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	public String getPvsDivision() {
		return pvsDivision;
	}
	public void setPvsDivision(String pvsDivision) {
		this.pvsDivision = pvsDivision;
	}
	public boolean isSoloPropAct() {
		return soloPropAct;
	}
	public void setSoloPropAct(boolean soloPropAct) {
		this.soloPropAct = soloPropAct;
	}
	public boolean isSoloMisProp() {
		return soloMisProp;
	}
	public void setSoloMisProp(boolean soloMisProp) {
		this.soloMisProp = soloMisProp;
	}
	public String getNomUsuarios() {
		return nomUsuarios;
	}
	public void setNomUsuarios(String nomUsuarios) {
		this.nomUsuarios = nomUsuarios;
	}
	public boolean isTodasPropuestas() {
		return todasPropuestas;
	}
	public void setTodasPropuestas(boolean todasPropuestas) {
		this.todasPropuestas = todasPropuestas;
	}
}
