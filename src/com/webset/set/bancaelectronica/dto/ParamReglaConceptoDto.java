package com.webset.set.bancaelectronica.dto;

public class ParamReglaConceptoDto {
	private int idFormaPago ;
	private int idTipoOperacion;
	private String origenMov;
	private String ingresoEgreso;
	private String conceptoSet;
    private String idCorresponde;
    private String descFormaPago;
    private String bSalvoBuenCobro;
    private String bUsaImporta;
    private String idGrupo;
    private String idRubro;
    
	public int getIdFormaPago() {
		return idFormaPago;
	}
	public void setIdFormaPago(int idFormaPago) {
		this.idFormaPago = idFormaPago;
	}
	public int getIdTipoOperacion() {
		return idTipoOperacion;
	}
	public void setIdTipoOperacion(int idTipoOperacion) {
		this.idTipoOperacion = idTipoOperacion;
	}
	public String getOrigenMov() {
		return origenMov;
	}
	public void setOrigenMov(String origenMov) {
		this.origenMov = origenMov;
	}
	public String getIngresoEgreso() {
		return ingresoEgreso;
	}
	public void setIngresoEgreso(String ingresoEgreso) {
		this.ingresoEgreso = ingresoEgreso;
	}
	public String getConceptoSet() {
		return conceptoSet;
	}
	public void setConceptoSet(String conceptoSet) {
		this.conceptoSet = conceptoSet;
	}
	public String getIdCorresponde() {
		return idCorresponde;
	}
	public void setIdCorresponde(String idCorresponde) {
		this.idCorresponde = idCorresponde;
	}
	public String getDescFormaPago() {
		return descFormaPago;
	}
	public void setDescFormaPago(String descFormaPago) {
		this.descFormaPago = descFormaPago;
	}
	public String getBSalvoBuenCobro() {
		return bSalvoBuenCobro;
	}
	public void setBSalvoBuenCobro(String salvoBuenCobro) {
		bSalvoBuenCobro = salvoBuenCobro;
	}
	public String getBUsaImporta() {
		return bUsaImporta;
	}
	public void setBUsaImporta(String usaImporta) {
		bUsaImporta = usaImporta;
	}
	public String getIdGrupo() {
		return idGrupo;
	}
	public void setIdGrupo(String idGrupo) {
		this.idGrupo = idGrupo;
	}
	public String getIdRubro() {
		return idRubro;
	}
	public void setIdRubro(String idRubro) {
		this.idRubro = idRubro;
	}
}
