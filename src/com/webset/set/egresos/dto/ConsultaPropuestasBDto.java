package com.webset.set.egresos.dto;

public class ConsultaPropuestasBDto {

	private int  grupoEmpresa;
    private int  pvGrupoRubro;
    private String  fechaIni;
    private String  fechaFin;
    private int idCliente;
    private int idUsuario;
    private String pvsDivision;
	private boolean soloPropAct;
    private boolean soloMisProp;
	private boolean todasPropuestas;
	//AGREGADO EMS OCT/2015
	private boolean divMN;
	private boolean divDLS;
	private boolean divEUR;
	private boolean divOtras;
	//AGREGADO EMS 12/11/2015
	private String tipoRegla;
	private String reglaNegocio;
	private String fechaPago;
	private String poHeaders;
	
	
	
	public String getPoHeaders() {
		return poHeaders;
	}
	public void setPoHeaders(String poHeaders) {
		this.poHeaders = poHeaders;
	}
	public String getFechaPago() {
		return fechaPago;
	}
	public void setFechaPago(String fechaPago) {
		this.fechaPago = fechaPago;
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
	public boolean isTodasPropuestas() {
		return todasPropuestas;
	}
	public void setTodasPropuestas(boolean todasPropuestas) {
		this.todasPropuestas = todasPropuestas;
	}
	public boolean isDivMN() {
		return divMN;
	}
	public void setDivMN(boolean divMN) {
		this.divMN = divMN;
	}
	public boolean isDivDLS() {
		return divDLS;
	}
	public void setDivDLS(boolean divDLS) {
		this.divDLS = divDLS;
	}
	public boolean isDivEUR() {
		return divEUR;
	}
	public void setDivEUR(boolean divEUR) {
		this.divEUR = divEUR;
	}
	public boolean isDivOtras() {
		return divOtras;
	}
	public void setDivOtras(boolean divOtras) {
		this.divOtras = divOtras;
	}
	public String getTipoRegla() {
		return tipoRegla;
	}
	public void setTipoRegla(String tipoRegla) {
		this.tipoRegla = tipoRegla;
	}
	public String getReglaNegocio() {
		return reglaNegocio;
	}
	public void setReglaNegocio(String reglaNegocio) {
		this.reglaNegocio = reglaNegocio;
	}
	
}
