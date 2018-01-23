package com.webset.set.ingresos.dto;
/** 
 * @author Jessica Arelly Cruz Cruz
 */

public class CatGrupoRubroDto {
	private int idGrupoG;
	private int	idGrupoR;
	private int	idRubroR;
	private int	idRubroEquivaleR;
	private String descGrupoG;
	private String descRubroR;
	private String bNegativoG;
	private String	ingresoEgresoR;
	private String	bContabilizaR;
	
	public int getIdGrupoG() {
		return idGrupoG;
	}
	public void setIdGrupoG(int idGrupoG) {
		this.idGrupoG = idGrupoG;
	}
	public int getIdGrupoR() {
		return idGrupoR;
	}
	public void setIdGrupoR(int idGrupoR) {
		this.idGrupoR = idGrupoR;
	}
	public int getIdRubroR() {
		return idRubroR;
	}
	public void setIdRubroR(int idRubroR) {
		this.idRubroR = idRubroR;
	}
	public int getIdRubroEquivaleR() {
		return idRubroEquivaleR;
	}
	public void setIdRubroEquivaleR(int idRubroEquivaleR) {
		this.idRubroEquivaleR = idRubroEquivaleR;
	}
	public String getDescGrupoG() {
		return descGrupoG;
	}
	public void setDescGrupoG(String descGrupoG) {
		this.descGrupoG = descGrupoG;
	}
	public String getBNegativoG() {
		return bNegativoG;
	}
	public void setBNegativoG(String negativoG) {
		bNegativoG = negativoG;
	}
	public String getIngresoEgresoR() {
		return ingresoEgresoR;
	}
	public void setIngresoEgresoR(String ingresoEgresoR) {
		this.ingresoEgresoR = ingresoEgresoR;
	}
	public String getBContabilizaR() {
		return bContabilizaR;
	}
	public void setBContabilizaR(String contabilizaR) {
		bContabilizaR = contabilizaR;
	}
	public String getDescRubroR() {
		return descRubroR;
	}
	public void setDescRubroR(String descRubroR) {
		this.descRubroR = descRubroR;
	}



}
