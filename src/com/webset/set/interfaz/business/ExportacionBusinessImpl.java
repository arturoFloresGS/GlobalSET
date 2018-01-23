package com.webset.set.interfaz.business;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.webset.set.conciliacionbancoset.dto.CriteriosBusquedaDto;
import com.webset.set.interfaz.dao.ExportacionDao;
import com.webset.set.interfaz.dto.GuiaContableDto;
import com.webset.set.interfaz.dto.InterfazDto;
import com.webset.set.interfaz.service.ExportacionService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;

public class ExportacionBusinessImpl implements ExportacionService{
	Bitacora bitacora;
	ExportacionDao objExportacionDao;
	Funciones funciones;
	ExportadorAgrupado exportadorClass;
	
	//Logger
	private Logger logger = Logger.getLogger(ExportacionBusinessImpl.class);
	
	public List<InterfazDto> llenaComboEmpresa(){
		return objExportacionDao.llenaComboEmpresa();
	}	
	
	public List<InterfazDto> llenaGrid(int noEmpresa, String fecHoy, int estatus){
		List<InterfazDto> listaResultado = new ArrayList<InterfazDto>();
		try{
			if (estatus == 1) //Busca los registros realizados
			{
				listaResultado = objExportacionDao.llenaGridRealizados(noEmpresa, fecHoy); 
			}
			
			if (estatus == 0)//busca los registros cancelados
			{
	 			listaResultado = objExportacionDao.llenaGridCancelados(noEmpresa, fecHoy);
			}
			
			if (estatus == 2)//busca los registros cancelados
			{
	 			listaResultado = objExportacionDao.llenaGridRechazados(noEmpresa, fecHoy);
			}
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Interfaz, C: ExportacionBusinessImpl, M: llenaGrid");
		} return listaResultado;
	}
	
	/**
	 * Metodo que exporta los registros a la ZEXP_FACT.
	 * Se llama desde la pagina.
	 */
	
	public String exportaRegistros(String folios, int noEmpresa){
		return exportadorClass.exportador(folios.substring(0, folios.length() - 1), noEmpresa);
	}
	
	/**
	 * Exportador automatico a la ZEXP_FACT. Se llama desde el CRON.
	 */
	public void exportaRegistrosAuto() {
		try {
			logger.debug("Entra a exportaRegistrosAuto");
			objExportacionDao.llamaExportador("", "PR");
			logger.debug("Sale de exportaRegistrosAuto");
		}
		catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:ExportacionBusinessImpl, M:exportaRegistrosAuto");
		}
	}
	
	@Override
	public List<GuiaContableDto> llenaComboCuentas(String noCuenta, String idTipoMovto) {
		
		return objExportacionDao.llenaComboCuentas(noCuenta,idTipoMovto);
		
	}	
	
	public Map<String, Object> updateMovimientoSET(List<GuiaContableDto> listMovs, CriteriosBusquedaDto dto){
	                                              
		
		Map<String, Object> mapRet = new HashMap<String, Object>();
		
		int band = -1;
		
		try{
			
			for(int i = 0; i < listMovs.size(); i ++)
			{
	           
				band = objExportacionDao.updateMovimientoSET(listMovs.get(i).getNoFolioDet().toString(),dto);
				
			}
			
			if (band != -1 )				
				mapRet.put("msgUsuario", "Se ejecuto correctamente la operacion");			
			else
				mapRet.put("msgUsuario", "Ocurrio un error.");

		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:updateMovimientoSETVIngresos");
		}
		return mapRet;
	}

	public List<InterfazDto> llenaGridCXC(int noEmpresa, String fecHoy, int estatus){
		List<InterfazDto> listaResultado = new ArrayList<InterfazDto>();
		try{
			if (estatus == 1) //Busca los registros realizados
			{
				listaResultado = objExportacionDao.llenaGridPendientesCXC(noEmpresa, fecHoy); 
			}
			
			if (estatus == 0)//busca los registros cancelados
			{
//	 			listaResultado = objExportacionDao.llenaGridCanceladosCXC(noEmpresa, fecHoy);
			}
			
			if (estatus == 2)//busca los registros cancelados
			{
//	 			listaResultado = objExportacionDao.llenaGridRechazadosCXC(noEmpresa, fecHoy);
			}
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Interfaz, C: ExportacionBusinessImpl, M: llenaGrid");
		} return listaResultado;
	}

	@SuppressWarnings("unused")
	public String exportaRegistrosCXC(String folios){
		List<InterfazDto> listaDatos = new ArrayList<InterfazDto>();
		List<InterfazDto> listaDatosDet = new ArrayList<InterfazDto>();
		String mensaje = "";
		int resul = 0;
		int resu = 0;
		int res = 0;
		int ietu_iva = 0;
		String iva = "";
		String ietu = "";
		int cont = 0;
		
		try{
			folios = folios.substring(0, folios.length() - 1);
			listaDatos =  objExportacionDao.listaRegistrosCXC(folios);
			
			if(listaDatos.size()>0){
				for(int i=0;i<listaDatos.size();i++) {
					resul = objExportacionDao.exportaRegistrosCXC(listaDatos,i);
					resu = objExportacionDao.exportaRegistrosDetalleCXC(listaDatos,i,3);
					listaDatosDet = objExportacionDao.listaRegistrosDetalleCXC(listaDatos.get(i).getNoFolioDet2());
		
					if(listaDatosDet.size() > 0){
						cont = listaDatosDet.size();
						ietu = listaDatosDet.get(0).getIetuDet();
						iva = listaDatosDet.get(0).getIvaDet();
						
						for(int j=0; j<listaDatosDet.size();j++)
							res = objExportacionDao.exportaRegistrosDetalleCXC(listaDatosDet,j,0);
						
						if(ietu.equals("S")) {
							cont = cont - 1;
							ietu_iva = objExportacionDao.exportaRegistrosDetalleCXC(listaDatosDet,cont,1);
						}
						
						if(iva.equals("S")) {
							cont = cont - 1;
							ietu_iva = objExportacionDao.exportaRegistrosDetalleCXC(listaDatosDet,cont,2);
						}
					}
				}
			}
			
			if (resul != 0 && resul != -1)
				mensaje = "Registros exportados con exito";
			else
				mensaje = "Ocurrio un error en la Exportaci�n de los registro";
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C: ExportacionBusinessImpl, M: exportaRegistros");
		}return mensaje;	
	}
	
	//***************************************************************************************************************************************
	public ExportacionDao getObjExportacionDao() 
	{
		return objExportacionDao;
	}

	public void setObjExportacionDao(ExportacionDao objExportacionDao) 
	{
		this.objExportacionDao = objExportacionDao;
	}

	public ExportadorAgrupado getExportadorClass() {
		return exportadorClass;
	}

	public void setExportadorClass(ExportadorAgrupado exportadorClass) {
		this.exportadorClass = exportadorClass;
	}
}
