package com.webset.set.cashflow.business;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.webset.set.cashflow.dto.AColReporteDto;
import com.webset.set.cashflow.dto.ARowReporteDto;
import com.webset.set.cashflow.dto.EmpresaDto;
import com.webset.set.cashflow.dto.GrupoEmpresasDto;
import com.webset.set.cashflow.dto.ReporteFlujoDto;

/****************************************************************************INIT*
 * AUTOR            : JUAN RAMIRO BARRERA MARTINEZ
 * NAME             : CashFlowBusinnesInterface()
 * TYPE             : INTERFACE				   
 * RESPONSABILITY   : DEFINIR EL CONTRATO PARA LAS IMPLEMENTACIONES DE LAS CLASES
 *                    DE LA CAPA DE NEGOCIOS	
 ****************************************************************************INIT*/
public interface CashFlowBusinnesInterface {
	
	//OBTIENE UNA LISTA DE GRUPO DE EMPRESAS
	public List<GrupoEmpresasDto> getGrupoEmpresasBusiness();
	//OBTIENE UNA LISTA DE GRUPO DE REPORTES
	public List<ReporteFlujoDto> getReportesFlujoBusiness();
	//OBTIENE UNA LISTA DE GRUPO DE EMPRESAS
	public List<EmpresaDto> getEmpresasBusiness(int idGrupo, int noUsuario);
	//OBTIENE UNA LISTA CON LA ESTRUCTURA PARA LA CABECERA DEL REPORTE
	public List<AColReporteDto> getStructureReportBusiness(int paramIdGrupo,int paramNoEmpresa,String paramFechaInicial,String paramagFechaFinal,int paramIdReporte) throws Exception;
	//OBTIENE UNA LISTA CON LA ESTRUCTURA PARA EL CUERPO DEL REPORTE
	public ARowReporteDto getBodyReportBusiness(int paramIdGrupo,int paramNoEmpresa,String paramFechaInicial,String paramagFechaFinal,int paramIdReporte);
	//DETERMINA SI UNA FECHA DADA ES INHABIL
	public boolean  getFecInhabilBusiness( String sFecha );
	 
}