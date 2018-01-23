package com.webset.set.egresos.dto;

public class OperacionDivisaDTO {
	
	private	String 	tipoOperacionDivTransf;	
	private String  noDocSap; 
	private	String	Cliente	;
	private	String	usuario	;
	private	String	folio	;	
	private	int		noEmpresa;	
	private	String  nomEmpresa;	
	
	private	String	idDivisaVenta	;
	private	String	descDivisaVenta	;
	
	private	int		idBancoCargo	;
	private	String	descBancoCargo	;
	private	String	chequeraCargo	;
	
	private	String	idDivisaCompra	;
	private	String	descDivisaCompra; 	
	private	int		idBancoAbono	;
	private	String	descBancoAbono	;
	private	String	chequeraAbono	;
	private	int		idCasaDeCambio	;
	private	String	nomCasaDeCambio	;
	private	int		idBancoCasaCambio;	
	private	String	descBancoCasaCambio;	
	private	String	chequeraCasaCambio;	
	private	int		idOperador	;
	private	String	nomOperador	;
	private	String	fechaValor	;
	private	String	fechaLiquidacion;	
	private	double	importeCompra	;
	private	double	tipoDeCambio	;
	private	double	importeVenta	;
	private	int     idFormaPago	;	
	private	String  descFormaPago	; 	
	private	String	concepto	;
	private	String	referencia	;
	private	String	firma1	;
	private	String	firma2	;
	private	int	    idGrupoEgreso	;
	private	String	descGrupoEgreso	;
	private	int	    idRubroEgreso	;
	private	String	descRubroEgreso	;
	private	int		idGrupoIngreso	;
	private	String  descGrupoIngreso;		
	private	int		idRubroIngreso	;
	private	String  descRubroIngreso;		
	private	String	custodia;	
	private	int     noProveedor; 		
	private	String  nomProveedor;		
	private	String  abaOswift;
	private String estatusAutorizacion; 
	private String cveControl;
	private String fecPropuesta;
	
	
	
	public String getFecPropuesta() {
		return fecPropuesta;
	}
	public void setFecPropuesta(String fecPropuesta) {
		this.fecPropuesta = fecPropuesta;
	}
	public String getCveControl() {
		return cveControl;
	}
	public void setCveControl(String cveControl) {
		this.cveControl = cveControl;
	}
	
	public String getTipoOperacionDivTransf() {
		return tipoOperacionDivTransf;
	}
	public void setTipoOperacionDivTransf(String tipoOperacionDivTransf) {
		this.tipoOperacionDivTransf = tipoOperacionDivTransf;
	}
	public String getNoDocSap() {
		return noDocSap;
	}
	public void setNoDocSap(String noDocSap) {
		this.noDocSap = noDocSap;
	}
	public String getCliente() {
		return Cliente;
	}
	public void setCliente(String cliente) {
		Cliente = cliente;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getFolio() {
		return folio;
	}
	public void setFolio(String folio) {
		this.folio = folio;
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
	public String getIdDivisaVenta() {
		return idDivisaVenta;
	}
	public void setIdDivisaVenta(String idDivisaVenta) {
		this.idDivisaVenta = idDivisaVenta;
	}
	public String getDescDivisaVenta() {
		return descDivisaVenta;
	}
	public void setDescDivisaVenta(String descDivisaVenta) {
		this.descDivisaVenta = descDivisaVenta;
	}
	public int getIdBancoCargo() {
		return idBancoCargo;
	}
	public void setIdBancoCargo(int idBancoCargo) {
		this.idBancoCargo = idBancoCargo;
	}
	public String getDescBancoCargo() {
		return descBancoCargo;
	}
	public void setDescBancoCargo(String descBancoCargo) {
		this.descBancoCargo = descBancoCargo;
	}
	public String getChequeraCargo() {
		return chequeraCargo;
	}
	public void setChequeraCargo(String chequeraCargo) {
		this.chequeraCargo = chequeraCargo;
	}
	public String getIdDivisaCompra() {
		return idDivisaCompra;
	}
	public void setIdDivisaCompra(String idDivisaCompra) {
		this.idDivisaCompra = idDivisaCompra;
	}
	public String getDescDivisaCompra() {
		return descDivisaCompra;
	}
	public void setDescDivisaCompra(String descDivisaCompra) {
		this.descDivisaCompra = descDivisaCompra;
	}
	public int getIdBancoAbono() {
		return idBancoAbono;
	}
	public void setIdBancoAbono(int idBancoAbono) {
		this.idBancoAbono = idBancoAbono;
	}
	public String getDescBancoAbono() {
		return descBancoAbono;
	}
	public void setDescBancoAbono(String descBancoAbono) {
		this.descBancoAbono = descBancoAbono;
	}
	public String getChequeraAbono() {
		return chequeraAbono;
	}
	public void setChequeraAbono(String chequeraAbono) {
		this.chequeraAbono = chequeraAbono;
	}
	public int getIdCasaDeCambio() {
		return idCasaDeCambio;
	}
	public void setIdCasaDeCambio(int idCasaDeCambio) {
		this.idCasaDeCambio = idCasaDeCambio;
	}
	public String getNomCasaDeCambio() {
		return nomCasaDeCambio;
	}
	public void setNomCasaDeCambio(String nomCasaDeCambio) {
		this.nomCasaDeCambio = nomCasaDeCambio;
	}
	public int getIdBancoCasaCambio() {
		return idBancoCasaCambio;
	}
	public void setIdBancoCasaCambio(int idBancoCasaCambio) {
		this.idBancoCasaCambio = idBancoCasaCambio;
	}
	public String getDescBancoCasaCambio() {
		return descBancoCasaCambio;
	}
	public void setDescBancoCasaCambio(String descBancoCasaCambio) {
		this.descBancoCasaCambio = descBancoCasaCambio;
	}
	public String getChequeraCasaCambio() {
		return chequeraCasaCambio;
	}
	public void setChequeraCasaCambio(String chequeraCasaCambio) {
		this.chequeraCasaCambio = chequeraCasaCambio;
	}
	public int getIdOperador() {
		return idOperador;
	}
	public void setIdOperador(int idOperador) {
		this.idOperador = idOperador;
	}
	public String getNomOperador() {
		return nomOperador;
	}
	public void setNomOperador(String nomOperador) {
		this.nomOperador = nomOperador;
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
	public double getImporteCompra() {
		return importeCompra;
	}
	public void setImporteCompra(double importeCompra) {
		this.importeCompra = importeCompra;
	}
	public double getTipoDeCambio() {
		return tipoDeCambio;
	}
	public void setTipoDeCambio(double tipoDeCambio) {
		this.tipoDeCambio = tipoDeCambio;
	}
	public double getImporteVenta() {
		return importeVenta;
	}
	public void setImporteVenta(double importeVenta) {
		this.importeVenta = importeVenta;
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
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getFirma1() {
		return firma1;
	}
	public void setFirma1(String firma1) {
		this.firma1 = firma1;
	}
	public String getFirma2() {
		return firma2;
	}
	public void setFirma2(String firma2) {
		this.firma2 = firma2;
	}
	public int getIdGrupoEgreso() {
		return idGrupoEgreso;
	}
	public void setIdGrupoEgreso(int idGrupoEgreso) {
		this.idGrupoEgreso = idGrupoEgreso;
	}
	public String getDescGrupoEgreso() {
		return descGrupoEgreso;
	}
	public void setDescGrupoEgreso(String descGrupoEgreso) {
		this.descGrupoEgreso = descGrupoEgreso;
	}
	public int getIdRubroEgreso() {
		return idRubroEgreso;
	}
	public void setIdRubroEgreso(int idRubroEgreso) {
		this.idRubroEgreso = idRubroEgreso;
	}
	public String getDescRubroEgreso() {
		return descRubroEgreso;
	}
	public void setDescRubroEgreso(String descRubroEgreso) {
		this.descRubroEgreso = descRubroEgreso;
	}
	public int getIdGrupoIngreso() {
		return idGrupoIngreso;
	}
	public void setIdGrupoIngreso(int idGrupoIngreso) {
		this.idGrupoIngreso = idGrupoIngreso;
	}
	public String getDescGrupoIngreso() {
		return descGrupoIngreso;
	}
	public void setDescGrupoIngreso(String descGrupoIngreso) {
		this.descGrupoIngreso = descGrupoIngreso;
	}
	public int getIdRubroIngreso() {
		return idRubroIngreso;
	}
	public void setIdRubroIngreso(int idRubroIngreso) {
		this.idRubroIngreso = idRubroIngreso;
	}
	public String getDescRubroIngreso() {
		return descRubroIngreso;
	}
	public void setDescRubroIngreso(String descRubroIntreso) {
		this.descRubroIngreso = descRubroIntreso;
	}
	public String getCustodia() {
		return custodia;
	}
	public void setCustodia(String custodia) {
		this.custodia = custodia;
	}
	public int getNoProveedor() {
		return noProveedor;
	}
	public void setNoProveedor(int noProveedor) {
		this.noProveedor = noProveedor;
	}
	public String getNomProveedor() {
		return nomProveedor;
	}
	public void setNomProveedor(String nomProveedor) {
		this.nomProveedor = nomProveedor;
	}
	public String getAbaOswift() {
		return abaOswift;
	}
	public void setAbaOswift(String abaOswift) {
		this.abaOswift = abaOswift;
	}
	public String getEstatusAutorizacion() {
		return estatusAutorizacion;
	}
	public void setEstatusAutorizacion(String estatusAutorizacion) {
		this.estatusAutorizacion = estatusAutorizacion;
	} 		
	
	
	
}
