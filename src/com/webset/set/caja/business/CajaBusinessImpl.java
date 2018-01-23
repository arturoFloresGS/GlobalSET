package com.webset.set.caja.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

import org.apache.log4j.Logger;

import com.webset.set.caja.dao.CajaDao;
import com.webset.set.caja.service.CajaService;
import com.webset.set.impresion.dto.ConsultaChequesDto;
import com.webset.set.traspasos.dto.MovimientoDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.ComunEgresosDto;
import com.webset.set.utilerias.dto.CuadrantesDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.ParamMovto3000Dto;
import com.webset.set.utilerias.dto.StoreParamsComunDto;

/**
 * Reglas del negocio Modulo Caja
 * @author Jessica Arelly Cruz Cruz
 * @since 27/07/2011
 */

public class CajaBusinessImpl implements CajaService{
	
	private CajaDao cajaDao;
	private Bitacora bitacora = new Bitacora();
	private Funciones funciones;
	private GlobalSingleton globalSingleton;
	 private static Logger logger = Logger.getLogger(CajaBusinessImpl.class);
	 
	public List<LlenaComboEmpresasDto> llenarComboEmpresas(int idUsuario){
		List<LlenaComboEmpresasDto> list = new ArrayList<LlenaComboEmpresasDto>();
		try{
			list = cajaDao.consultarEmpresas(idUsuario);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Caja, C:CajaBusinessImpl, M:llenarComboEmpresas");
		}
		return list;
	}
	
	public List<LlenaComboGralDto> llenarComboBancos(int iEmpresa){
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try{
			list = cajaDao.consultarBancos(iEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Caja, C:CajaBusinessImpl, M:llenarComboBancos");
		}
		return list;
	}
	
	public List<LlenaComboGralDto> llenarComboBancosVentana(int iEmpresa){
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try{
			list = cajaDao.consultarBancosVentana(iEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Caja, C:CajaBusinessImpl, M:llenarBancosVentana");
		}
		return list;
	}
	
	public List<LlenaComboDivisaDto> llenarComboDivisas(int cnt){
		List<LlenaComboDivisaDto> list = new ArrayList<LlenaComboDivisaDto>();
		try{
			list = cajaDao.consultarDivisas(cnt);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Caja, C:CajaBusinessImpl, M:llenarComboDivisas");
		}
		return list;
	}
	
	public List<LlenaComboChequeraDto> llenarComboChequeras(int iBanco, int iEmpresa){
		List<LlenaComboChequeraDto> list = new ArrayList<LlenaComboChequeraDto>();
		try{
			list = cajaDao.consultarChequeras(iBanco, iEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Caja, C:CajaBusinessImpl, M:llenarComboChequeras");
		}
		return list;
	}
	
	public List<LlenaComboChequeraDto> llenarChequerasVentana(int iBanco, int iEmpresa){
		List<LlenaComboChequeraDto> list = new ArrayList<LlenaComboChequeraDto>();
		try{
			list = cajaDao.consultarChequerasVentana(iBanco, iEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Caja, C:CajaBusinessImpl, M:llenarChequerasVentana");
		}
		return list;
	}
	
	public List<MovimientoDto> llenarGridChequesPorEntregar(ConsultaChequesDto dtoBus){
		List<MovimientoDto> list = new ArrayList<MovimientoDto>();
		try{
			list = cajaDao.consultarChequesPorEntregar(dtoBus);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Caja, C:CajaBusinessImpl, M:llenarGridChequesPorEntregar");
		}
		return list;
	}
	
	/**
	 * procedimiento que ejecuta la entrega de cheques
	 * ChequeEntregado
	 * @param listCheques
	 * @return
	 */
	public Map<String, Object> ejecutarEntregaCheques(List<MovimientoDto> listCheques, String fecHoy, int usuario){
		Map<String, Object> mapRetorno = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		StoreParamsComunDto dtoPagador= new StoreParamsComunDto();
		
		try{
			String sFoliosEfvo = "";//ps_folios_efvo
			String sFoliosEfvo1 = "";//ps_folios_efvo1
			int iDatos = 0;//pi_datos
			int iRegMarcados = 0;
			String sMatDoc[] = new String [listCheques.size()];//ps_matdoc
			int iAfectados = 0;//pl_afectados
			int iPosicion = 0;//pi_pos1
			int iFolioCaja = 0; //pl_folio_caja
			String iFoliosRech = "";
			String llamaCuadrantes = "";
			String sFoliosRech = "";//ps_folios_rech
		    iDatos = 0;
			String sBandera = "N";
			String sFolios = "";
			Map lResp= new HashMap();
			
		    for(int iContador = 0; iContador < listCheques.size(); iContador ++)
		    {
		    	if(listCheques.get(iContador).getIdChequera().equals("EFECTIVOFIJ")) {
		    		sFoliosEfvo = sFoliosEfvo + listCheques.get(iContador).getNoFolioDet() + ",";
		    	}else if((listCheques.get(iContador).getIdBanco() == 0 || listCheques.get(iContador).getIdChequera().equals("")) && (!listCheques.get(iContador).getCveControl().trim().substring(0,2).equals("MF"))) {
    				mapRetorno.put("msgUsuario", "Falta chequera pagadora para la empresa = " + listCheques.get(iContador).getNoEmpresa() + " no se afectaron");
    				return mapRetorno;
    			}else {
                    iRegMarcados = iRegMarcados + 1;
                    sMatDoc[iDatos] = ""+listCheques.get(iContador).getNoFolioDet();
                    logger.info("sMatDoc[ "+iDatos+"] = " + listCheques.get(iContador).getNoFolioDet());
                    iDatos = iDatos + 1;
                    
                    sFolios += listCheques.get(iContador).getNoFolioDet() + ",";
                }
		    }
		    
		    /**Cambiar el estatus de Pagos en Efectivo**/
		    if(!sFoliosEfvo.equals(""))
		    {
		    	sFoliosEfvo = sFoliosEfvo.substring(0, sFoliosEfvo.length() - 1);
		    	iAfectados = 0;
		    	iAfectados = cajaDao.actualizarEstatusEfvo(sFoliosEfvo);
		    	if(iAfectados == 0)
		    		mensajes.add("Error, los movimientos" + sFoliosEfvo + " no se afectaron");
		    	else
		    	{
		    		while(!sFoliosEfvo.equals(""))
		    		{
		    			sFoliosEfvo1 = "";
			            iPosicion = 0;
			            iPosicion = sFoliosEfvo.indexOf(",");
			            if(iPosicion > 0)
			            {
			            	sFoliosEfvo1 = sFoliosEfvo.substring(0, iPosicion - 1);
			                sFoliosEfvo = sFoliosEfvo.substring(iPosicion + 1);
			            }
			            else
			            {
			            	sFoliosEfvo1 = sFoliosEfvo;
			            	sFoliosEfvo = "";
			            }
			             
			            if(!sFoliosEfvo1.equals(""))
			            {
			            	iFolioCaja = cajaDao.seleccionarFolioReal("no_recibo_caja");
			            	logger.info("iFolioCaja " + iFolioCaja);
			            	iAfectados = 0;
			            	iAfectados = cajaDao.actualizaFolioCaja(iFolioCaja, Integer.parseInt(sFoliosEfvo1));
			                //impresion_reporte (CDbl(ps_folios_efvo1))
			            }
		    		}
		    	}
		    }
		    llamaCuadrantes = this.ejecutaCuadrantes(sMatDoc, iRegMarcados, sFoliosRech);
		    if(!llamaCuadrantes.equals("")) mensajes.add("Error, en afecta cuadrantes los movimientos " + iFoliosRech + " no se afectaron");
		    
			for(int i=0; i<listCheques.size(); i++) {
				dtoPagador = new StoreParamsComunDto();
			    dtoPagador.setParametro(sBandera + "," + 0 + "," + usuario + "," + listCheques.get(i).getIdBanco() + "," 
			    		+ listCheques.get(i).getIdChequera() + ","+ listCheques.get(i).getNoFolioDet() + "," + 1);
				
				dtoPagador.setMensaje("iniciar pagador");
				dtoPagador.setResult(0);
				lResp = cajaDao.ejecutarPagador(dtoPagador);
				
				if (!lResp.isEmpty() && Integer.parseInt(lResp.get("result").toString()) != 0) {
					mapRetorno.put("msgUsuario", "� Error en pagador #" + lResp.get("result") + ":");
				    return mapRetorno;
				}
				cajaDao.actualizarEstatusEfvo(listCheques.get(i).getNoFolioDet()+"");
				cajaDao.actualizarFecPago(listCheques.get(i).getCveControl(), fecHoy);
			}
			mensajes.add("Datos Registrados");
		    mapRetorno.put("msgUsuario", mensajes);
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Caja, C:CajaBusinessImpl, M:ejecutarEntregaCheques");
		}
		return mapRetorno;
	}
	
	/**
	 * Procedimiento que hace la llamada al store sp_CuadranteNETROSET
	 * @param matDoc
	 * @param iRegMarcados
	 * @param sFoliosAcep
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String ejecutaCuadrantes(String matDoc[], int iRegMarcados, String sFoliosAcep){
		Map<String, Object> resCuadrantes = new  HashMap<String, Object>();
		String mensaje = "";
		int iInicio = iRegMarcados;
	    int iAcumula = 0;
	    int iIntermedio = 0;
	    int iSiguiente = 0;
	    int i = 0;
	    String sFolios = "";
	    CuadrantesDto cuadrantesDto;
	    
		try{
			globalSingleton = GlobalSingleton.getInstancia();
		    
		    while (iInicio > 0)
		    {
		    	if(iInicio > 50)
		    	{
		    		iSiguiente = 50;
		    		iInicio = iInicio - iSiguiente;
		    	}
		        else
		        {
		            iSiguiente = iInicio;
		            iInicio = 0;
		        }
		    	
		    	iAcumula = iAcumula + iSiguiente;
		    	for(i = iIntermedio; i < iAcumula; i++)
		    	{
		    		sFolios = sFolios + matDoc[i] + ",";
		    		logger.info("sFolios " + sFolios);
		    	}
		    	
		    	cuadrantesDto = new CuadrantesDto();
		    	cuadrantesDto.setNomForma("frmchqxent");
		    	cuadrantesDto.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
		    	cuadrantesDto.setFolios(cuadrantesDto.getNomForma() + "," + sFolios);
		    	cuadrantesDto.setResult(0);
		    	cuadrantesDto.setMensaje("Inicia Cuadrantes");
		    	logger.info("usuario "+cuadrantesDto.getIdUsuario());
		    	resCuadrantes = cajaDao.ejecutaCuadranteNetroSet(cuadrantesDto);
		    	
		    	logger.info("cuadrante " + resCuadrantes.get("result").toString());
				if(funciones.convertirCadenaInteger(resCuadrantes.get("result").toString()) != 0)
				{
					mensaje = "Error en cuadrantes # " + resCuadrantes.get("result");
					return mensaje;
				}
		        
		        sFolios = "";
		        iIntermedio = iIntermedio + 50;
		    }

		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Caja, C:CajaBusinessImpl, M:ejecutaCuadrantes");
		}
		return mensaje;
	}
	
	/**
	 * llamada a las consultas para llenar los reportes
	 */
	@SuppressWarnings("unchecked")
	public JRDataSource llenarReporteCheques(String nomReporte, Map parameters){
		JRMapArrayDataSource  jrDataSource = null; 
	    List<Map<String, Object>> resMap = null;
		try
		{
			if(nomReporte.equals("reporteChequesPorEntregar")){
				resMap = cajaDao.consultarReporteChequesPorEntregar(parameters);
			} 
			else if(nomReporte.equals("reporteChequesEntregados")){
				resMap = cajaDao.consultarReporteChequesEntregados(parameters);
			} 
			else if(nomReporte.equals("reporteHistoricoChequesEntregados")){
				resMap = cajaDao.consultarReporteHistoricoChequesEntregados(parameters);
			} 
			else if(nomReporte.equals("reporteChequesEntregadosPorCaja")){
				resMap = cajaDao.consultarReporteChequesEntregadosPorCaja(parameters);
			}
			else if(nomReporte.equals("reporteHistoricoChequesEntregadosPorCaja")){
				resMap = cajaDao.consultarReporteHistoricoChequesEntregadosPorCaja(parameters);
			}
			
            jrDataSource = new JRMapArrayDataSource(resMap.toArray());
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Caja, C:CajaBusinessImpl, M:llenarReporteCheques");
		}
		return jrDataSource;
	}
	
	public List<Map<String, Object>> llenarReporteChequesPorEntregar(Map<String, Object> datos){
		List<Map<String, Object>> mapRet = null;
		try{
			mapRet = cajaDao.consultarReporteChequesPorEntregar(datos);
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Caja, C:CajaBusinessImpl, M:consultarReporteChequesPorEntregar");
		}
		return mapRet;
	}

	public CajaDao getCajaDao() {
		return cajaDao;
	}

	public void setCajaDao(CajaDao cajaDao) {
		this.cajaDao = cajaDao;
	}
}
