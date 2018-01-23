package com.webset.set.conciliacionbancoset.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.conciliacionbancoset.dao.ConciliacionBancoSetDao;
import com.webset.set.conciliacionbancoset.dto.ConciliaBancoDto;
import com.webset.set.conciliacionbancoset.dto.CriteriosBusquedaDto;
import com.webset.set.conciliacionbancoset.dto.CriteriosReporteDto;
import com.webset.set.conciliacionbancoset.dto.CruceConciliaDto;
import com.webset.set.conciliacionbancoset.dto.MonitorConciliacionDTO;
import com.webset.set.conciliacionbancoset.dto.ParamBusquedaDto;
import com.webset.set.conciliacionbancoset.dto.MonitorConciliaGenDTO;
import com.webset.set.conciliacionbancoset.dto.TmpBancoSetMonitorDto;
import com.webset.set.conciliacionbancoset.dto.TmpBcoSetResumenDto;
import com.webset.set.conciliacionbancoset.dto.TmpConcenConciliaDto;
import com.webset.set.conciliacionbancoset.service.ConciliacionBancoSetService;
import com.webset.set.global.business.GlobalBusiness;
import com.webset.set.ingresos.dto.CuentaContableDto;
import com.webset.set.layout.business.CreacionArchivosBusiness;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.CatCtaBancoDto;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.ParametroDto;

/**
 * Clase de negocio del modulo conciliacion banco - set 
 * @author Jessica Arelly Cruz Cruz
 *
 */
public class ConciliacionBancoSetBusinessImpl implements ConciliacionBancoSetService{

	private ConciliacionBancoSetDao conciliacionDao;
	private Bitacora bitacora = new Bitacora();
	private GlobalSingleton globalSingleton;
	private GlobalBusiness globalBusiness;
	private Funciones funciones = new Funciones();
	private CreacionArchivosBusiness creacionArchivosBusiness;
	
	
	public List<LlenaComboGralDto> llenarComboBancos(int iEmpresa){
		List<LlenaComboGralDto> listDatos = new ArrayList<LlenaComboGralDto>();
		try{
			listDatos = conciliacionDao.consultarBancos(iEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:llenarComboBancos");
		}
		return listDatos;
	}
	
	public String consultarFechaConciliacion(int iBanco, int tEmpresa){
		List<CatCtaBancoDto> listFecha = new ArrayList<CatCtaBancoDto>();
		String sFecha = "";
		int iEmpresa = 0;
		int iUsuario = 0;
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			iEmpresa = globalSingleton.getUsuarioLoginDto().getNumeroEmpresa();
			iUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			listFecha = conciliacionDao.consultarFechaConciliacion(iBanco, tEmpresa, iEmpresa, iUsuario);
			if(listFecha.size() > 0)
				sFecha = funciones.ponerFechaSola(listFecha.get(0).getFecConciliacion());
			else
				sFecha = "";
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:consultarFechaConciliacion");
		}
		return sFecha;
	}
	
	/**
	 * metodo que llena el grid para seleccionar las 
	 * chequeras que se van a conciliar automaticamente
	 */
	public List<CatCtaBancoDto> consultarChequerasConciliar(int noEmpresa, int noBanco){
		List<CatCtaBancoDto> listChequeras = new ArrayList<CatCtaBancoDto>();
		int noUsuario = 0;
		
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			noUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			
			listChequeras = conciliacionDao.consultarChequerasConciliar(noBanco, noEmpresa, noUsuario);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:consultarChequerasConciliar");
		}
		return listChequeras;
	}
	
	/**
	 * metodo que ejecuta los stores respectivos para la conciliacion automatica
	 */
	public String ejecutarConciliacionAutomaticaBS(List<CatCtaBancoDto> listDatos){
		String result = "";
		Map<String, Object> mapConciliadorAtm = new HashMap<String, Object>();
		String sArchivo = "conciliados.txt";
		String sRegistro = "";
		
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			creacionArchivosBusiness.eliminarArchivoLayout("\\", "", sArchivo);
			creacionArchivosBusiness.escribirArchivoLayout("\\","",sArchivo, listDatos.get(0).getDescBanco());
			
			for(int i = 0; i < listDatos.size(); i ++) {
				ParamBusquedaDto dto = new ParamBusquedaDto();
				dto.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
				dto.setIdEmpresa(listDatos.get(i).getNoEmpresa());
				dto.setIdBanco(listDatos.get(i).getIdBanco());
				dto.setIdChequera(listDatos.get(i).getIdChequera());
				dto.setResultado(0);
				
				result = this.conciliadorAutomaticoBS(dto);
				/*
				if(Integer.parseInt(mapConciliadorAtm.get("result").toString()) == 0) {
					sRegistro = listDatos.get(i).getIdChequera() + "\t" + 
								listDatos.get(i).getDescSucursal() + 
								"\t \t Se Conciliaron: " + mapConciliadorAtm.get("conciliados").toString();
					
	                result = "Conciliacion Realizada";
				}else {
	            	sRegistro = listDatos.get(i).getIdChequera() + "\t" + 
								listDatos.get(i).getDescSucursal() + 
								"\t \t Error: " + mapConciliadorAtm.get("mensaje").toString();
	            	
	            	result = "Se presentaron errores, consulte el archivo c:\\conciliados.txt";
	            }
				creacionArchivosBusiness.insertarRegistro("\\","",sArchivo, sRegistro);*/
			}
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:ejecutarConciliacionAutomaticaBS");
		}
		return result;
	}
	
	/**
	 * llamada al store sp_concilia_banco_movto
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String conciliadorAutomaticoBS(ParamBusquedaDto dto){
		Map<String, Object> resConciliador = new HashMap<String, Object>();
		Map<String, Object> mapConciliadorAtm = new HashMap<String, Object>();
		int iConciliados = 0;
		int iResult = 0;
		String sMensaje = "";
		try
		{
			Thread.sleep(2000);
			//resConciliador = conciliacionDao.ejecutaSPconciliaBancoMovto(dto);
			sMensaje=this.conciliacionDao.ejecutaSPconciliaBancoMovto(dto);
			if(sMensaje.equals("exito")){
				sMensaje = "Conciliaci�n realizada";
			}else{
				sMensaje = "Error al conciliar";
			}
			//iResult = funciones.convertirCadenaInteger(resConciliador.get("resultado").toString());
			//mapConciliadorAtm.put("result", iResult);
			/*if(iResult == 0)
			{
				mapConciliadorAtm.put("conciliados", iConciliados);
			}
			else
			{
				if(iResult == 2)
					sMensaje = "Esta chequera ya esta siendo conciliada.";
				else
					sMensaje = "Error del conciliador: " + iResult;
			}*/
			
			//mapConciliadorAtm.put("mensaje", sMensaje);
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:conciliadorAutomaticoBS");
		}
		return sMensaje;
	}
	
	/**
	 * metodo que llena el combo del criterio chequeras
	 */
	public List<LlenaComboChequeraDto> llenarCmbChequeras(int iBanco, int iEmpresa, int iOpc){
		List<LlenaComboChequeraDto> list = new ArrayList<LlenaComboChequeraDto> ();
		try{
			list = conciliacionDao.consultarChequeras(iBanco, iEmpresa, iOpc);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:llenarCmbChequeras");
		}
		return list;
	}
	
	/**
	 * metodo que llena el combo del criterio estatus
	 */
	public List<LlenaComboChequeraDto> consultarEstatus(boolean bCancelacion){
		List<LlenaComboChequeraDto> list = new ArrayList<LlenaComboChequeraDto> ();
		try{
			list = conciliacionDao.consultarEstatus(bCancelacion);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:consultarEstatus");
		}
		return list;
	}
	
	/**
	 * metodo que valida si la chequera seleccionado no esta siendo conciliada
	 */
	public Map<String, Object> validarConciliacion(CriteriosBusquedaDto dto, boolean bCancelacion){
		int iUsuario;
		Map<String, Object> mapRet = new HashMap<String, Object>();
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			iUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			
			conciliacionDao.borrarTmpControlConcilia(iUsuario);
			
		    listMap = conciliacionDao.consultarTmpControlConcilia(dto.getIdBanco(), dto.getIdChequera(), bCancelacion);
		    
		    if(listMap.size() > 0)
		    {
		        mapRet.put("msgUsuario", "La chequera est� siendo conciliada por el usuario " + listMap.get(0).get("nombre") + " " + listMap.get(0).get("apaterno") + " " + listMap.get(0).get("amaterno"));
		        listMap.clear();
		    }
		    else
		    {
		    	conciliacionDao.insertarTmpControlConcilia(dto.getIdBanco(), dto.getIdChequera(), iUsuario, bCancelacion);
		    	mapRet.put("msgUsuario", "");
		    }
	    	listMap.clear();
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:validarConciliacion");
		}
		return mapRet;
	}
	
	/**
	 * metodo que llena el grid de los movimientos del banco para conciliar manualmente
	 * @param dto
	 * @return
	 */
	public List<ConciliaBancoDto> llenarMovsBanco(CriteriosBusquedaDto dto){
		List<ConciliaBancoDto> movsBanco = new ArrayList<ConciliaBancoDto>();
		try{
			movsBanco = conciliacionDao.consultarMovsBanco(dto);
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:llenarMovsBanco");
		}
		return movsBanco;
	}
	
	/**
	 * metodo que llena el grid de los movimientos de la empresa para conciliar manualmente
	 * @param dto
	 * @return
	 */
	public List<MovimientoDto> llenarMovsEmpresa(CriteriosBusquedaDto dto){
		List<MovimientoDto> movsEmpresa = new ArrayList<MovimientoDto>();
		try{
			movsEmpresa = conciliacionDao.consultarMovsEmpresa(dto);

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:llenarMovsEmpresa");
		}
		return movsEmpresa;
	}
	
	/**
	 * metodo que consulta tabla cat_rubro
	 * del evento click del combo rubro
	 * @param sRubro
	 * @return
	 */
	public Map<String, Object> consultarContabiliza(String sRubro){
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		Map<String, Object> mapRet = new HashMap<String, Object>();
		try{
			listMap = conciliacionDao.consultarContabiliza(sRubro);
			if(listMap.size() > 0)
			{
				mapRet.put("contabi", listMap.get(0).get("equivaleRP"));
	                  
				if(listMap.get(0).get("bContabiliza").toString().equals("S"))
					mapRet.put("chkContabiliza", 1);
	            else
	            	mapRet.put("chkContabiliza", 0);
				listMap.clear();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:consultarContabiliza");
		}
		return mapRet;
	}
	
	/**
	 * Metodo que actualiza y selecciona el folio real
	 * @param tipoFolio
	 * @return
	 */
	public int obtenerFolioReal(String tipoFolio){
		int folio = 0;
		try{
			conciliacionDao.actualizarFolioReal(tipoFolio);
			folio = conciliacionDao.seleccionarFolioReal(tipoFolio);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:obtenerFolioReal");
		}
		return folio;
	}
	
	/**
	 * metodo que crea el movimiento equivalente en SET de los registros seleccionados del banco
	 * @param listBanco
	 * @param dto
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public Map<String, Object> crearMovimientoSET(List<ConciliaBancoDto> listBanco, CriteriosBusquedaDto dto){
		Map<String, Object> mapRet = new HashMap<String, Object>();
		Map<String, Object> resGenerador = new  HashMap<String, Object>();
		List<CatCtaBancoDto> listDivisa = new ArrayList<CatCtaBancoDto>();
		List<Map<String, Object>> listCatRubro = new ArrayList<Map<String, Object>>();
		GeneradorDto generadorDto;
		ParametroDto paramDto;
		CruceConciliaDto cruceDto;
		int iEmpresa = 0;
		int iBanco = 0;
		int iTipoOperacion = 0;
		int iGrupo = 0;
		int iNoDocto = 0;
		int iFolio = 0;
		int iUsuario;
		int iCaja;
		int iFolioMov = 300;
		int iFolioDet = 200;
		int iAfectados = 0;
		Date dFechaHoy;
		Date dFechaActual;
		String sHora = "";
		String sChequera = "";
		String sDivisa = "";
		String sNaturaleza = "";
		String sGenConta = "";
		String sOrigen = "";
		String sRefe = "";
		
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			iUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			iCaja = globalSingleton.getUsuarioLoginDto().getIdCaja();
			dFechaHoy = globalSingleton.getFechaHoy();
			for(int i = 0; i < listBanco.size(); i ++)
			{
	            //Recupera divisa
				listDivisa = conciliacionDao.consultarEmpDiv(dto);
	            
				if(listDivisa.size() > 0)
				{
					sDivisa = listDivisa.get(0).getIdDivisa();
					iEmpresa = listDivisa.get(0).getNoEmpresa();
					iBanco = listDivisa.get(0).getIdBanco();
					sChequera = listDivisa.get(0).getIdChequera();
				}
	            else
	            	sDivisa = "MN";
				
				
				listDivisa.clear();
	            
				listCatRubro = conciliacionDao.consultarIngresoEgreso(dto);
				if(listCatRubro.size() > 0)
				{
					if(listCatRubro.get(0).get("ingresoEgreso").toString().equals("E"))
						iTipoOperacion = 1018;
					else
						iTipoOperacion = 3101;
					
					sNaturaleza = listCatRubro.get(0).get("ingresoEgreso").toString();
					if(dto.isContabiliza())
					{
						sGenConta = "S";
						sOrigen = "BCE";
					}
					else
					{
						sGenConta = "N";
						sOrigen = "SET";
					}
					listCatRubro.clear();
				}
	            
	            if(sNaturaleza.equals("E"))
	            	sNaturaleza = "C";
	            
	            if(sNaturaleza.equals("I"))
	            	sNaturaleza = "A";
	            
	            if(!listBanco.get(i).getCargoAbono().equals(sNaturaleza))
	            {
	            	mapRet.put("msgUsuario", "La naturaleza del rubro no corresponde a la del banco");
	            	return mapRet;
	            }
	                                    
	            iGrupo = this.obtenerFolioReal("grupo_conci");
	            if(iGrupo <= 0)
	            {
	            	mapRet.put("msgUsuario", "Error al obtener el grupo de folios");
	            	return mapRet;
	            }
	            
	            iNoDocto = this.obtenerFolioReal("no_docto");
	            if(iNoDocto <= 0)
	            {
	            	mapRet.put("msgUsuario", "Error al obtener el n�mero de documento");
	            	return mapRet;
	            }
	                                
	            iFolio = this.obtenerFolioReal("no_folio_param");
	            if(iFolio <= 0)
	            {
	            	mapRet.put("msgUsuario", "Error al obtener el folio para la tabla parametro");
	            	return mapRet;
	            }
	            
	            sRefe = "";
	            if(dto.isContabiliza())
	            	sRefe = iFolio + " " + listBanco.get(i).getReferencia();
	            else
	            	sRefe = iNoDocto + " " + listBanco.get(i).getReferencia();

	            paramDto = new ParametroDto();
	            paramDto.setNoEmpresa(iEmpresa);
	            paramDto.setNoFolioParam(iFolio);
	            paramDto.setIdTipoDocto(0);
	            paramDto.setIdFormaPago(3);
	            paramDto.setIdTipoOperacion(iTipoOperacion);
	            paramDto.setUsuarioAlta(iUsuario);
	            paramDto.setNoCuenta(iEmpresa);
	            paramDto.setNoCliente("0");
	            paramDto.setFecValor(dFechaHoy);
	            paramDto.setFecValorOriginal(dFechaHoy);
	            paramDto.setFecOperacion(listBanco.get(i).getFecOperacion());
	            paramDto.setFecAlta(dFechaHoy);
	            paramDto.setImporte(listBanco.get(i).getImporte());
	            paramDto.setImporteOriginal(listBanco.get(i).getImporte());
	            paramDto.setIdCaja(iCaja);
	            paramDto.setIdDivisa(sDivisa);
	            paramDto.setIdDivisaOriginal(sDivisa);
	            paramDto.setOrigenReg("SET");
	            paramDto.setReferencia(sRefe);
	            paramDto.setConcepto(dto.getDescRubro());
	            paramDto.setAplica(1);
	            paramDto.setIdEstatusMov("A");
	            paramDto.setBSalvoBuenCobro("S");
	            paramDto.setIdEstatusReg("P");
	            paramDto.setIdBanco(iBanco);
	            paramDto.setIdChequera(sChequera);
	            paramDto.setFolioBanco(listBanco.get(i).getSecuencia()+"");
	            paramDto.setOrigenMov(sOrigen);
	            paramDto.setObservacion(listBanco.get(i).getConcepto());
	            paramDto.setIdInvCbolsa(1);
	            paramDto.setNoFolioMov(0);
	            paramDto.setFolioRef(0);
	            paramDto.setGrupo(iFolio);
	            paramDto.setImporteDesglosado(listBanco.get(i).getImporte());
	            paramDto.setLote(0);
	            paramDto.setHoraRecibo("0");
	            paramDto.setDivision("0");
	            //paramDto.setIdRubroInt(funciones.convertirCadenaInteger(dto.getIdRubro()));
	            paramDto.setIdRubroInt(dto.getIdRubro()!= null && !dto.getIdRubro().equals("") ? dto.getIdRubro():"0");
	            paramDto.setNoRecibo(0);
	            paramDto.setNoDocto(iNoDocto+"");
	            
	            iAfectados = 0;
	            iAfectados = conciliacionDao.insertarParametro(paramDto);
	            
	            generadorDto = new GeneradorDto();
				generadorDto.setIdUsuario(iUsuario);
				generadorDto.setNomForma("ConciliacionManual"); 
				generadorDto.setEmpresa(iEmpresa);
				generadorDto.setFolParam(iFolio);
				generadorDto.setFolMovi(iFolioMov);
				generadorDto.setFolDeta(iFolioDet);
				generadorDto.setResult(0); //1
				generadorDto.setMensajeResp("inicia generador");
				
				resGenerador = conciliacionDao.ejecutarGenerador(generadorDto);
	            
				if(funciones.convertirCadenaInteger(resGenerador.get("result").toString()) != 0)
				{
					if(funciones.convertirCadenaInteger(resGenerador.get("result").toString()) != 1053)
					{
						mapRet.put("msgUsuario","Error " + resGenerador.get("result") + " en Generador " +
								"no_folio_param = " + iFolio);
						break;
					}
					break;
				}
	
				//Actualiza en concilia banco id_estatus_cb a "C"
				iAfectados = 0;
				iAfectados = conciliacionDao.actualizarConciliaBanco(iEmpresa, iBanco, sChequera, listBanco.get(i).getSecuencia());
			    
			    //Actualiza en movto_identificado estatus a "C"
				iAfectados = 0;
				iAfectados = conciliacionDao.actualizarMovtoIdentificado(iEmpresa, iBanco, sChequera, listBanco.get(i).getSecuencia());
				
				iFolioDet = conciliacionDao.obtenerNoFolioDet(iFolio);
				
				
				if(dto.isContabiliza())
				{
					sRefe = "";
					sRefe = iFolioDet + " " + listBanco.get(i).getReferencia();
				}
				else
					sRefe = "";
				
				dFechaActual= new Date();
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				
				String horas = "";
				
				if(c.get(Calendar.HOUR_OF_DAY)<10){
					horas = "0"+c.get(Calendar.HOUR_OF_DAY);
				}else
					horas = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
					
				String minutos = "";
				
				if(c.get(Calendar.MINUTE)<10){
					minutos = "0"+c.get(Calendar.MINUTE);
				}else
					minutos = String.valueOf(c.get(Calendar.MINUTE));
				
				String segundos = "";
				
				if(c.get(Calendar.SECOND)<10){
					segundos = "0"+c.get(Calendar.SECOND);
				}else
					segundos = String.valueOf(c.get(Calendar.SECOND));

				sHora = horas + ":" + minutos + ":" + segundos;
				iAfectados = 0;
				
				iAfectados = conciliacionDao.actualizarCbMovimiento(iFolioDet, sGenConta, listBanco.get(i).getImporte(), dFechaHoy, dto.getNomComputadora(), 
																	iBanco, sChequera, iUsuario, sHora, sRefe);
				
			    
				cruceDto = new CruceConciliaDto();
				cruceDto.setGrupo(iGrupo);
				cruceDto.setSecuencia(1);
				cruceDto.setNoFolio1(iFolioDet);
//				cruceDto.setNoFolio1(Integer.parseInt(resGenerador.get("folDeta").toString()));
				cruceDto.setNoFolio2(listBanco.get(i).getSecuencia());
				cruceDto.setFuenteMovto("1");
				cruceDto.setTipoConcilia("M");
				cruceDto.setFecAlta(listBanco.get(i).getFecOperacion());
				cruceDto.setUsuarioAlta(iUsuario);
				cruceDto.setNoEmpresa(listBanco.get(i).getNoEmpresa());
				cruceDto.setIdBanco(listBanco.get(i).getIdBanco());
				cruceDto.setIdChequera(listBanco.get(i).getIdChequera());
				
				iAfectados = 0;
				iAfectados = conciliacionDao.insertarCruceConcilia(cruceDto);
			}
			
			mapRet.put("msgUsuario", "Se Importaron a SET y se Conciliaron Correctamente los Registros Seleccionados");

		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:crearMovimientoSET");
		}
		return mapRet;
	}
	
	/**
	 * metodo que realiza la conciliacion manual BS de los registros seleccionados
	 * @param listBanco
	 * @param listEmpresa
	 * @param dto
	 * @return
	 */
	public Map<String, Object> ejecutarConciliacionManualBS(List<ConciliaBancoDto> listBanco, List<MovimientoDto> listEmpresa, CriteriosBusquedaDto dto){
		Map<String, Object> mapRet = new HashMap<String, Object>();
		Map<String, Object> mapCta = new HashMap<String, Object>();
		Map<String, Object> mapAjuste = new HashMap<String, Object>();
		List<String> sMensajes = new ArrayList<String>();
		CruceConciliaDto cruceDto;
	    int i , j;
	    int iTipoOperacion = 0;
	    int iEmpresa = 0;
	    int iCuenta = 0;
	    int iGrupo = 0;
	    int iFolioAjuste = -1;
	    int iFolio1 = 0;
	    int iFolio2 = 0;
	    int iSecuencia = 0;
	    int iAfectados = 0;
	    int iUsuario;
	    double dImporte = 0;
	    Date dFechaHoy;
	    String sEstatus = "";
	    String sDivisa = "";
	    String sFolioSET = "";
	    String sFoliosBan = "";
	    String sFuenteMovto = "";
		
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			iEmpresa = listBanco.get(0).getNoEmpresa();
			iUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			dFechaHoy = globalSingleton.getFechaHoy();
			
			if(!dto.getAclaracion().equals(""))
		        sEstatus = "A";
		    else
		        sEstatus = "M";
		                
//			System.out.println("Ajuste "+ dto.isAjuste());
//			System.out.println("Diferencia "+ dto.getDiferencia());
			
			if(dto.getDiferencia() > 0)
			{
		        //se genera el movimiento de ajuste
				if(dto.getCargoAbono().equals("C"))
		            iTipoOperacion = 1019;
		        else
		            iTipoOperacion = 1020;

				mapCta = this.obtenerCuenta(iEmpresa, dto.getIdBanco(), dto.getIdChequera());
				
				if(funciones.convertirCadenaBoolean(mapCta.get("obtenCuenta").toString()))
				{
					iCuenta = funciones.convertirCadenaInteger(mapCta.get("cuenta").toString());
					sDivisa = funciones.validarCadena(mapCta.get("divisa").toString());
					
					mapAjuste = this.movimientoAjuste(iCuenta, dto.getIdChequera(), dto.getIdBanco(), dto.getDiferencia(), sDivisa, "MOVIMIENTO DE AJUSTE", iTipoOperacion, iFolioAjuste, iEmpresa);
					
					iFolioAjuste = conciliacionDao.obtenerNoFolioDet((Integer)mapAjuste.get("iFolioParam"));
					
//					iFolioAjuste = funciones.convertirCadenaInteger(mapAjuste.get("iFolioAjuste").toString());
					
					if(!funciones.convertirCadenaBoolean(mapAjuste.get("movAjuste").toString()))
					{
						sMensajes.add(mapAjuste.get("mensaje").toString());
						sMensajes.add("Fallo la generacion del Movimiento de Ajuste");
						mapRet.put("msgUsuario", sMensajes);
						return mapRet;
					}
				}
			}

			iGrupo = this.obtenerFolioReal("grupo_conci");
			if(iGrupo <= 0)
			{
				sMensajes.add("Error al obtener el grupo de Folios.");
				mapRet.put("msgUsuario", sMensajes);
				return mapRet;
			}
		    
		    //bIniciaTrans = True
			do{
				iFolio1 = -1;
				if(iFolioAjuste > 0)
				{
					iFolio1 = iFolioAjuste;
					sFolioSET = sFolioSET + iFolio1 + ",";
					iFolioAjuste = -1;
				}
				else
				{
					for(i = 0; i < listEmpresa.size(); i ++){
						sFolioSET = sFolioSET + listEmpresa.get(i).getNoFolioDet() + ",";
						iFolio1 = listEmpresa.get(i).getNoFolioDet();
						listEmpresa.remove(i);
						break;//checar esto
					}
				}
				
				iFolio2 = -1;
				for(j = 0; j < listBanco.size(); j ++){
					dImporte = dImporte + listBanco.get(j).getImporte();
					sFoliosBan = sFoliosBan + listBanco.get(j).getSecuencia();
					iFolio2 = listBanco.get(j).getSecuencia();
					listBanco.remove(j);
					break;
				}
				
				if(iFolio1 > 0 || iFolio2 > 0)
				{
					if(sFuenteMovto.equals(""))
					{
						if(iFolio1 == -1)	//no hay movimientos del SET
							sFuenteMovto = "3";	//conciliacion ban-ban
						else if(iFolio2 == -1)	//no hay movimientos del banco
							sFuenteMovto = "2";	//conciliacion set-set
						else	//hay movimientos de ambos lados
							sFuenteMovto = "1";	//conciliacion ban-set
					}
					
					iSecuencia = iSecuencia + 1;
					
					cruceDto = new CruceConciliaDto();
					cruceDto.setGrupo(iGrupo);
					cruceDto.setSecuencia(iSecuencia);
					cruceDto.setNoFolio1(iFolio1 > 0  ? iFolio1 : 0);
					cruceDto.setNoFolio2(iFolio2 > 0 ? iFolio2 : 0);
					cruceDto.setFuenteMovto(sFuenteMovto);
					cruceDto.setTipoConcilia(sEstatus);
					cruceDto.setFecAlta(dFechaHoy);
					cruceDto.setUsuarioAlta(iUsuario);
					cruceDto.setNoEmpresa(iEmpresa);
					cruceDto.setIdBanco(dto.getIdBanco());
					cruceDto.setIdChequera(dto.getIdChequera());
					iAfectados = 0;
					iAfectados = conciliacionDao.insertarCruceConcilia(cruceDto);
						
				}
			}while(iFolio1 > 0 || iFolio2 > 0);

			if(sFolioSET.length() > 0)
			{
				sFolioSET = sFolioSET.substring(0, sFolioSET.length() - 1);
				iAfectados = conciliacionDao.actualizarHistMovimiento(sEstatus, iGrupo, dFechaHoy);
			}
			
	        //Actualizo estatus concilia_banco
			if(sFoliosBan.length() > 0)
			{
				sFoliosBan = sFoliosBan.substring(0, sFoliosBan.length() - 1);
				iAfectados = 0;
				iAfectados = conciliacionDao.actualizarConciliaBanco(sEstatus, dto.getAclaracion(), 
																	iEmpresa, dto.getIdBanco(), 
																	dto.getIdChequera(), iGrupo);
				
				//aqui modificacion para que actualize el estatus='C' de movto_identificado
				iAfectados= 0;
				iAfectados = conciliacionDao.actualizarEstatusMI(iEmpresa, dto.getIdBanco(), dto.getIdChequera(), iGrupo);
			}
		        
			sMensajes.add("Datos Registrados");
			mapRet.put("msgUsuario", sMensajes);
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:ejecutarConciliacionBS");
		}
		return mapRet;
	}
	
	public Map<String, Object> obtenerCuenta(int iEmpresa, int iBanco, String sChequera){
		List<CatCtaBancoDto> list = new ArrayList<CatCtaBancoDto>();
		Map<String, Object> mapCta = new HashMap<String, Object>();
		int iCuenta = 0;
		boolean obtenCuenta = false;
		String sDivisa = "";
		try{
		   list = conciliacionDao.obtenerCuenta(iBanco, iEmpresa, sChequera);
		   
		   if(list.size() > 0)
		   {
			   iCuenta = list.get(0).getNoEmpresa();
			   sDivisa = list.get(0).getIdDivisa();
			   obtenCuenta = true;
		   }
		   list.clear();

		   mapCta.put("cuenta", iCuenta);
		   mapCta.put("divisa", sDivisa);
		   mapCta.put("obtenCuenta", obtenCuenta);

		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:obtenerCuenta");
		}
		return mapCta;
	}
	
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> movimientoAjuste(int iCuenta, String sChequera, int iBanco, double uDiferencia, 
			String sDivisa, String sConcepto, int iTipoOperacion, int iFolioAjuste, int noEmpresa){
		Map<String, Object> mapAjuste = new HashMap<String, Object>();
		Map<String, Object> resGenerador = new  HashMap<String, Object>();
		ParametroDto paramDto = new ParametroDto();
		GeneradorDto generadorDto;
		boolean movimientoAjuste = false;
		Integer iFolioParam = 0;
		int iAfectados = 0;
		int iEmpresa = 0 ;
		int iCaja = 0;
		int iUsuario = 0;
		int iFolioMov = 0;
		Date dFechaHoy = new Date();
		String sMensaje = "";
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			iEmpresa = noEmpresa;
			iCaja = globalSingleton.getUsuarioLoginDto().getIdCaja();
			iUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			dFechaHoy = globalSingleton.getFechaHoy();
			
			movimientoAjuste = true;
			iFolioParam = this.obtenerFolioReal("no_folio_param");
			if(iFolioParam <= 0)
				movimientoAjuste = false;

            Integer iNoDocto = this.obtenerFolioReal("no_docto");
            if(iNoDocto <= 0)
            {
            	mapAjuste.put("mensaje", "Error al obtener el número de documento");
            	return mapAjuste;
            }
                                

            paramDto.setNoEmpresa(iEmpresa);//8
			paramDto.setSecuencia(1);//9
			paramDto.setNoFolioParam(iFolioParam);//10
			paramDto.setNoFolioMov(0);//11
			paramDto.setAplica(1);//12
			paramDto.setIdTipoOperacion(iTipoOperacion);//13
			paramDto.setNoCuenta(iCuenta);//14
			paramDto.setIdEstatusMov("A");//1
			paramDto.setIdChequera(sChequera);//2
			paramDto.setIdBanco(iBanco);//15
			paramDto.setIdCaja(iCaja);//16
			paramDto.setImporte(uDiferencia);//17
			paramDto.setImporteOriginal(uDiferencia);//18
			paramDto.setIdDivisa(sDivisa);//3
			paramDto.setIdDivisaOriginal(sDivisa);//4
			paramDto.setUsuarioAlta(iUsuario);//19
			paramDto.setFecOperacion(dFechaHoy);//5
			paramDto.setFecValor(dFechaHoy);//5
			paramDto.setFecValorOriginal(dFechaHoy);//5
			paramDto.setFecAlta(dFechaHoy);//5
			paramDto.setConcepto(sConcepto);//6
			paramDto.setIdFormaPago(3);//20
			paramDto.setOrigenMov("SET");//7
			paramDto.setGrupo(iFolioParam);//21
			paramDto.setNoDocto(iNoDocto.toString());
			
			iAfectados = conciliacionDao.insertarParametro1(paramDto);
			
			generadorDto = new GeneradorDto();
			generadorDto.setIdUsuario(iUsuario);
			generadorDto.setNomForma("ConciliacionManual"); 
			generadorDto.setEmpresa(iEmpresa);
			generadorDto.setFolParam(iFolioParam);
			generadorDto.setFolMovi(iFolioMov);
			generadorDto.setFolDeta(iFolioAjuste);
			generadorDto.setResult(0); //1
			generadorDto.setMensajeResp("inicia generador");
			
			resGenerador = conciliacionDao.ejecutarGenerador(generadorDto);		
			if(funciones.convertirCadenaInteger(resGenerador.get("result").toString()) != 0)
				sMensaje = "Error en Generador # " + resGenerador.get("result").toString();
			
			mapAjuste.put("movAjuste", movimientoAjuste);
			mapAjuste.put("mensaje", sMensaje);
			mapAjuste.put("iFolioAjuste", resGenerador.get("folDeta"));
			mapAjuste.put("iFolioParam",iFolioParam);

		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:movimientoAjuste");
		}
		return mapAjuste;
	}
	
	public boolean exportado(int iGrupo){
		List<CruceConciliaDto> list = new ArrayList<CruceConciliaDto>();
		boolean pbExportado = false;
		try{
			list = conciliacionDao.consultarExportado(iGrupo);
			if(!list.isEmpty())
			{
				if(list.get(0).getGrupo() > 0)
					pbExportado = true;
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:exportado");
		}
		return pbExportado;
	}
	
	public List<ConciliaBancoDto> llenarGridMovsBanco(CriteriosBusquedaDto dto){
		List<ConciliaBancoDto> listBanco = new ArrayList<ConciliaBancoDto>();
		boolean pbExportado = false;
		int iSize = 0;
		try{
			listBanco = conciliacionDao.consultarCancMovsBanco(dto);
			iSize = listBanco.size();
			for(int i = 0; i < iSize; i ++)
			{
				ConciliaBancoDto dtoBanco =listBanco.get(i);
				pbExportado = this.exportado(listBanco.get(i).getGrupo());
				dtoBanco.setExportado(pbExportado == true ? "SI" : "NO");
				listBanco.set(i,dtoBanco);
			}
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:llenarGridMovsBanco");
		}
		return listBanco;
	}
	
	public List<MovimientoDto> llenarGridMovsEmpresa(CriteriosBusquedaDto dto){
		List<MovimientoDto> listEmpresa = new ArrayList<MovimientoDto>();
		boolean pbExportado = false;
		int iSize = 0;
		try{
			listEmpresa = conciliacionDao.consultarCancMovsEmpresa(dto);
			iSize = listEmpresa.size();
			for(int i = 0; i < iSize; i ++)
			{
				MovimientoDto dtoEmpresa = listEmpresa.get(i);
				pbExportado = this.exportado(listEmpresa.get(i).getGrupoPago());
				dtoEmpresa.setBEntregado(pbExportado == true ? "SI" : "NO");
				listEmpresa.set(i,dtoEmpresa);
			}
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:llenarGridMovsEmpresa");
		}
		return listEmpresa;
	}
	
	/**
	 * Metodo que realiza la desconciliacion de los registros seleccionados
	 * @param listBanco
	 * @param listEmpresa
	 * @return
	 */
	public Map<String, Object> cancelarConciliaciones(List<ConciliaBancoDto> listBanco, List<MovimientoDto> listEmpresa, CriteriosBusquedaDto dto){
		Map<String, Object> mapRet = new HashMap<String, Object>();
		try{
			if(this.regresarUnoaUno(listBanco, listEmpresa, dto.getAclaracion()))
				mapRet.put("msgUsuario", "Registros Procesados");
			else
				mapRet.put("msgUsuario", "Los registros no se procesaron");
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:cancelarConciliaciones");
		}
		return mapRet;
	}
	
	public boolean regresarUnoaUno(List<ConciliaBancoDto> listBanco, List<MovimientoDto> listEmpresa, String sObservaciones){
		boolean bRegresarUnoaUno = false, bEmpresa, bBanco;
		try{
			bEmpresa = true;
		    //Barrer registros del grid de Empresa (set)
			for(int i = 0; i < listEmpresa.size(); i ++)
			{
				if(listEmpresa.get(i).getIdEstatusCb().equals("F") || listEmpresa.get(i).getIdEstatusCb().equals("X") || listEmpresa.get(i).getIdEstatusCb().equals("D"))
				{
					bEmpresa = this.actualizarEstatusFolio(listEmpresa.get(i).getNoFolioDet(), sObservaciones, "empresa");
				}
				else
				{
					if(listEmpresa.get(i).getGrupoPago() == 0)
					{
						bEmpresa = this.actualizarEstatusFolio(listEmpresa.get(i).getNoFolioDet(), sObservaciones, "empresa");
					}
					else if(listEmpresa.get(i).getBEntregado().equals("NO"))
					{
						bEmpresa = this.actualizarEstatusGrupo(listEmpresa.get(i).getGrupoPago(), listEmpresa.get(i).getNoFolioDet(), sObservaciones);
					}
				}
			}
		    
		    //Barrer registros del grid de Banco
			
		    bBanco = true;
		    for(int j = 0; j < listBanco.size(); j ++)
		    {
		    	if(listBanco.get(j).getIdEstatuscb().equals("F") || listBanco.get(j).getIdEstatuscb().equals("X") || listBanco.get(j).getIdEstatuscb().equals("D"))
		    	{
		    		bBanco = this.actualizarEstatusFolio(listBanco.get(j).getSecuencia(), sObservaciones, "banco");
		    	}
		    	else
		    	{
		    		if(listBanco.get(j).getGrupo() == 0)
		    		{
		    			bBanco = this.actualizarEstatusFolio(listBanco.get(j).getSecuencia(), sObservaciones, "banco");
		    		}
		    		else if(listBanco.get(j).getExportado().equals("NO"))
		    		{
		    			bBanco = this.actualizarEstatusGrupo(listBanco.get(j).getGrupo(), listBanco.get(j).getSecuencia(), sObservaciones);
		    		}
		    	}
		    }
		    bRegresarUnoaUno = bBanco && bEmpresa;
		}catch(Exception e){
			bRegresarUnoaUno = false;
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:regresarUnoaUno");
		}
		return bRegresarUnoaUno;
	}
	
	/**
	 * HazPendientes
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public boolean actualizarEstatusFolio(int iFolios, String sObservaciones, String sTipoMovimiento){
		boolean bActualiza = false;
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			int iUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			Date dFechaHoy = globalSingleton.getFechaHoy();
			
			if(sTipoMovimiento.equals("empresa"))
			{
				conciliacionDao.actualizarDesconciliaHistorico(iFolios);
				
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				
				String horas = "";
				String minutos = "";
				String segundos = "";
				
				if(c.get(Calendar.HOUR)<10){
					horas = "0"+ c.get(Calendar.HOUR);
				}else
					horas += c.get(Calendar.HOUR);

				if(c.get(Calendar.MINUTE)<10){
					minutos = "0"+ c.get(Calendar.MINUTE);
				}else
					minutos += c.get(Calendar.MINUTE);
				
				if(c.get(Calendar.SECOND)<10){
					segundos = "0"+ c.get(Calendar.SECOND);
				}else
					segundos += c.get(Calendar.SECOND);

				String fecha = funciones.ponerFechaSola(dFechaHoy) + " " + horas+ ":" + minutos + ":"+ segundos;
				
				conciliacionDao.insertarDetalleDesconciliacion(iFolios, 0, iUsuario, fecha, sObservaciones);
			}
	        else
	        	conciliacionDao.actualizarDesconciliacion(iFolios);
			bActualiza = true;
	
		}catch(Exception e){
			bActualiza = false;
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:actualizarEstatusFolio");
		}
		return bActualiza;
	}
	
	/**
	 * HazPendienteGrupo
	 * @param iGrupo
	 * @param iFolioDet
	 * @param sObservaciones
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public boolean actualizarEstatusGrupo(int iGrupo, int iFolioDet, String sObservaciones){
		boolean bActualiza = false;
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			int iUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			Date dFechaHoy = globalSingleton.getFechaHoy();
			
			conciliacionDao.actualizarHistMovimiento(iGrupo);		//Actualiza hist_movimiento id_estatus_cb = 'P'
		       
			conciliacionDao.actualizarConciliaBanco(iGrupo);		//actualiza concilia_banco id_estatus_cb = 'P'
		    
			conciliacionDao.borrarCruceConcilia(iGrupo);			//delete from cruce_concilia
		    //Inserta en detalle_desconciliacion para llevar una bitacora de las cancelaciones
			
			Calendar c = Calendar.getInstance();
			c.setTime(new Date());
			
			String horas = "";
			String minutos = "";
			String segundos = "";
			
			if(c.get(Calendar.HOUR)<10){
				horas = "0"+ c.get(Calendar.HOUR);
			}else
				horas += c.get(Calendar.HOUR);

			if(c.get(Calendar.MINUTE)<10){
				minutos = "0"+ c.get(Calendar.MINUTE);
			}else
				minutos += c.get(Calendar.MINUTE);
			
			if(c.get(Calendar.SECOND)<10){
				segundos = "0"+ c.get(Calendar.SECOND);
			}else
				segundos += c.get(Calendar.SECOND);

			String fecha = funciones.ponerFechaSola(dFechaHoy) + " " + horas+ ":" + minutos + ":"+ segundos;

			conciliacionDao.insertarDetalleDesconciliacion(iFolioDet, iGrupo, iUsuario, fecha, sObservaciones);
			
			bActualiza = true;
			
		}catch(Exception e){
			bActualiza = false;
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:actualizarEstatusGrupo");
		}
		return bActualiza;
	}
	
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteCancelados(Map parameters){
		JRMapArrayDataSource  jrDataSource = null; 
	    List<Map<String, Object>> resMap = null;
		try{
			//parameters.put("subtitulo", "Cancelacion de Conciliaciones al "+funciones.ponerFechaLetra(globalSingleton.getFechaHoy(), false));
			resMap = conciliacionDao.consultarReporteCancelados(funciones.convertirCadenaInteger(parameters.get("empresa").toString()));
            jrDataSource = new JRMapArrayDataSource(resMap.toArray());

		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:obtenerReporteCancelados");
		}
		return jrDataSource;
	}
	
	public List<MovimientoDto> consultarMovsFicticios(CriteriosBusquedaDto dto){
		List<MovimientoDto> listMov = new ArrayList<MovimientoDto>();
		try{
			listMov = conciliacionDao.consultarMovsFicticios(dto);
		}catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:consultarMovsFicticios");
		}
		return listMov;
	}
	
	public Map<String, Object> ejecutarMovsFicticios(List<MovimientoDto> listMov, CriteriosBusquedaDto dto){
		Map<String, Object> mapRet = new HashMap<String, Object>();
		int iAfectados = 0;
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			int iUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			Date dFechaHoy = globalSingleton.getFechaHoy();
			for(int i = 0; i < listMov.size(); i ++)
			{
                iAfectados = conciliacionDao.actualizarFicticios(listMov.get(i).getNoFolioDet(), dto.getNoEmpresa()) ;
                iAfectados = conciliacionDao.insertarDetalleFicticios(listMov.get(i).getNoFolioDet(), "B", iUsuario, dFechaHoy, dto.getAclaracion()) ;

                //listMov.remove(i);
			}
			if(iAfectados <= 0)
				mapRet.put("msgUsuario", "Datos no Procesados");
			else
				mapRet.put("msgUsuario", "Datos Registrados");
	        
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:ejecutarMovsFicticios");
		}
		return mapRet;
	}
	
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteMovsFicticios(Map parameters){
		JRMapArrayDataSource  jrDataSource = null; 
	    List<Map<String, Object>> resMap = null;
		try{
			//parameters.put("subtitulo", "Cancelacion de Conciliaciones al "+funciones.ponerFechaLetra(globalSingleton.getFechaHoy(), false));
			resMap = conciliacionDao.consultarReporteMovsFicticios(funciones.convertirCadenaInteger(parameters.get("empresa").toString()));
            jrDataSource = new JRMapArrayDataSource(resMap.toArray());

		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:obtenerReporteMovsFicticios");
		}
		return jrDataSource;
	}
	
	public List<ConciliaBancoDto> consultarMovsDuplicados(CriteriosBusquedaDto dto){
		List<ConciliaBancoDto> listMov = new ArrayList<ConciliaBancoDto>();
		try{
			listMov = conciliacionDao.consultarMovsDuplicados(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:consultarMovsDuplicados");
		}
		return listMov;
	}
	
	public Map<String, Object> ejecutarMovsDuplicados(List<ConciliaBancoDto> listMov, CriteriosBusquedaDto dto){
		Map<String, Object> mapRet = new HashMap<String, Object>();
		int iAfectados = 0;
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			int iUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			Date dFechaHoy = globalSingleton.getFechaHoy();
			for(int i = 0; i < listMov.size(); i ++)
			{
                iAfectados = conciliacionDao.actualizarDuplicados(listMov.get(i).getSecuencia(), dto.getNoEmpresa());
                iAfectados = conciliacionDao.insertarDetalleDuplicadosBanco(listMov.get(i).getSecuencia(), iUsuario, dFechaHoy, dto.getAclaracion());

                //listMov.remove(i);
			}
			if(iAfectados <= 0)
				mapRet.put("msgUsuario", "Datos no Procesados");
			else
				mapRet.put("msgUsuario", "Datos Registrados");
	        
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:ejecutarMovsDuplicados");
		}
		return mapRet;
	}
	
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteMovsDuplicados(Map parameters){
		JRMapArrayDataSource  jrDataSource = null; 
	    List<Map<String, Object>> resMap = null;
		try{
			resMap = conciliacionDao.consultarReporteMovsDuplicados(funciones.convertirCadenaInteger(parameters.get("empresa").toString()));
            jrDataSource = new JRMapArrayDataSource(resMap.toArray());

		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:obtenerReporteMovsDuplicados");
		}
		return jrDataSource;
	}
	
	public List<LlenaComboGralDto> llenarComboBancosEmpresa(String sEmpresas){
		List<LlenaComboGralDto> listDatos = new ArrayList<LlenaComboGralDto>();
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			int iIdUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			
			if(sEmpresas.equals("0"))
				sEmpresas = globalBusiness.obtenerCadenaEmpresasUsuario(iIdUsuario);
			
			listDatos = conciliacionDao.consultarBancosEmpresas(sEmpresas);
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:llenarComboBancosEmpresa");
		}
		return listDatos;
	}
	
	public List<CatCtaBancoDto> llenarGridChequerasBanco(String sEmpresas, int iBanco){
		List<CatCtaBancoDto> listCheque = new ArrayList<CatCtaBancoDto>();
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			int iIdUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			
			if(sEmpresas.equals("0"))
				sEmpresas = globalBusiness.obtenerCadenaEmpresasUsuario(iIdUsuario);
			
			listCheque = conciliacionDao.consultarChequerasBanco(sEmpresas, iBanco);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:llenarGridChequerasBanco");
		}
		return listCheque;
	}
	
	@SuppressWarnings("unchecked")
	public List<JRDataSource> obtenerReporteDetalle(Map parameters){
		Gson gson = new Gson();
		List<JRDataSource> listJR = new ArrayList<JRDataSource>();
		List<Map<String, String>> paramsGridChequeras;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			int iIdUsuario = Integer.parseInt(parameters.get("iIdUsuario").toString());
			String datosGrid = parameters.get("aDatosGrid").toString();
			String tipoReporte= parameters.get("estatus").toString();
			paramsGridChequeras = gson.fromJson(datosGrid, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			
			if(tipoReporte.equals("ACLARADO"))
			{
				for(int iContador = 0; iContador < paramsGridChequeras.size(); iContador ++)
				{
		            listJR.add(this.reporteAclaradoManual(iIdUsuario, iContador, "A", paramsGridChequeras, parameters));
				}
			}
			if(tipoReporte.equals("CONCILIADO MANUAL"))
			{
				for(int iContador = 0; iContador < paramsGridChequeras.size(); iContador ++)
				{
		            listJR.add(this.reporteAclaradoManual(iIdUsuario, iContador, "M", paramsGridChequeras, parameters));
				}
			}
			if(tipoReporte.equals("CONCILIADO AUTOMATICO"))
			{
				for(int iContador = 0; iContador < paramsGridChequeras.size(); iContador ++)
				{
		            listJR.add(this.reporteConciliacionAtm(iContador, paramsGridChequeras, parameters));
				}
			}
			
			if(tipoReporte.equals("DETECTADOS"))
			{
				for(int iContador = 0; iContador < paramsGridChequeras.size(); iContador ++)
				{
		            listJR.add(this.reporteDetectadosyPendientes(iContador, "D", "D", paramsGridChequeras, parameters));
				}
			}
			
			if(tipoReporte.equals("PENDIENTE POR CONCILIAR"))
			{
				for(int iContador = 0; iContador < paramsGridChequeras.size(); iContador ++)
				{
					if(parameters.get("dFechaFin").equals(funciones.ponerFechaSola(globalSingleton.getFechaHoy())))
					{
						list = conciliacionDao.consultarFechaSaldo(funciones.convertirCadenaInteger(paramsGridChequeras.get(iContador).get("noEmpresa").toString()),
								funciones.convertirCadenaInteger(parameters.get("iIdBanco").toString()), 
								paramsGridChequeras.get(iContador).get("idChequera").toString());
						
						if(list.size() > 0)
							parameters.put("dFechaFin", funciones.cambiarFecha(list.get(0).get("fecha_saldo").toString(), true));
					}
		            listJR.add(this.reporteDetectadosyPendientes(iContador, "P", "N", paramsGridChequeras, parameters));
				}
			}
			
			if(tipoReporte.equals("MOVIMIENTOS DE AJUSTE"))
			{
				for(int iContador = 0; iContador < paramsGridChequeras.size(); iContador ++)
				{
					listJR.add(this.reporteAjustes(iContador, paramsGridChequeras, parameters));
				}
			}
			
			if(tipoReporte.equals("CARGA INICIAL"))
			{
				for(int iContador = 0; iContador < paramsGridChequeras.size(); iContador ++)
				{
					listJR.add(this.reporteFicticios(iContador, paramsGridChequeras, parameters));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:obtenerReporteDetalle");
		}
		return listJR;
	}
	
	@SuppressWarnings("unchecked")
	public JRDataSource reporteAclaradoManual(int iIdUsuario, int iContador, String sTipoConcillia, List<Map<String, String>> paramsGridChequeras,Map parameters){
		JRMapArrayDataSource  jrDataSource = null; 
	    List<Map<String, Object>> resMap = new ArrayList<Map<String, Object>>();
	    CriteriosReporteDto dto ;
		try{
			dto = new CriteriosReporteDto();
            dto.setINoEmpresa(funciones.convertirCadenaInteger(paramsGridChequeras.get(iContador).get("noEmpresa").toString()));
            dto.setSChequera(paramsGridChequeras.get(iContador).get("idChequera").toString());
            dto.setBValChequera(!paramsGridChequeras.get(iContador).get("idChequera").toString().equals("%"));
            dto.setIIdBanco(funciones.convertirCadenaInteger(parameters.get("iIdBanco").toString()));
			dto.setSTipoConcilia(sTipoConcillia);
			dto.setDFechaIni(funciones.ponerFechaDate(parameters.get("dFechaIni").toString()));
			dto.setDFechaFin(funciones.ponerFechaDate(parameters.get("dFechaFin").toString()));
            
			
			System.out.println("La chequera es "+dto.getSChequera());
			resMap = conciliacionDao.consultarReporteconAclaracion(dto);
			
            jrDataSource = new JRMapArrayDataSource(resMap.toArray());
            conciliacionDao.borraTmpConciliaSetBanco(iIdUsuario);
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:reporteAclaradoManual");
		}
		return jrDataSource;
	}
	
	@SuppressWarnings("unchecked")
	public JRDataSource reporteConciliacionAtm(int iContador,List<Map<String, String>> paramsGridChequeras,Map parameters){
		JRMapArrayDataSource  jrDataSource = null; 
	    List<Map<String, Object>> resMap = new ArrayList<Map<String, Object>>();
	    CriteriosReporteDto dto ;
		try{
			dto = new CriteriosReporteDto();
            dto.setINoEmpresa(funciones.convertirCadenaInteger(paramsGridChequeras.get(iContador).get("noEmpresa").toString()));
            dto.setSChequera(paramsGridChequeras.get(iContador).get("idChequera").toString());
            dto.setBValChequera(!paramsGridChequeras.get(iContador).get("idChequera").toString().equals("%"));
            dto.setIIdBanco(funciones.convertirCadenaInteger(parameters.get("iIdBanco").toString()));
			dto.setDFechaIni(funciones.ponerFechaDate(parameters.get("dFechaIni").toString()));
			dto.setDFechaFin(funciones.ponerFechaDate(parameters.get("dFechaFin").toString()));
			
			resMap = conciliacionDao.consultarReporteConciliadoAutomatico(dto);
			jrDataSource = new JRMapArrayDataSource(resMap.toArray());
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:reporteConciliacionAtm");
		}
		return jrDataSource;
	}
	
	@SuppressWarnings("unchecked")
	public JRDataSource reporteDetectadosyPendientes(int iContador, String sEstatus1, String sEstatus2,List<Map<String, String>> paramsGridChequeras,Map parameters){
		JRMapArrayDataSource  jrDataSource = null; 
	    List<Map<String, Object>> resMap = new ArrayList<Map<String, Object>>();
	    CriteriosReporteDto dto ;
		try{
			dto = new CriteriosReporteDto();
            dto.setINoEmpresa(funciones.convertirCadenaInteger(paramsGridChequeras.get(iContador).get("noEmpresa").toString()));
            dto.setSChequera(paramsGridChequeras.get(iContador).get("idChequera").toString());
            dto.setBValChequera(!paramsGridChequeras.get(iContador).get("idChequera").toString().equals("%"));
            dto.setIIdBanco(funciones.convertirCadenaInteger(parameters.get("iIdBanco").toString()));
			dto.setDFechaIni(funciones.ponerFechaDate(parameters.get("dFechaIni").toString()));
			dto.setDFechaFin(funciones.ponerFechaDate(parameters.get("dFechaFin").toString()));
			dto.setIdEstatusCb1(sEstatus1);
			dto.setIdEstatusCb2(sEstatus2);
			
			System.out.println("La chequera es "+ dto.getSChequera());
			
			resMap = conciliacionDao.consultarReporteMovimientosDetectados(dto);
			jrDataSource = new JRMapArrayDataSource(resMap.toArray());
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:reporteDetectadosyPendientes");
		}
		return jrDataSource;
	}
	
	@SuppressWarnings("unchecked")
	public JRDataSource reporteAjustes(int iContador,List<Map<String, String>> paramsGridChequeras,Map parameters){
		JRMapArrayDataSource  jrDataSource = null; 
	    List<Map<String, Object>> resMap = new ArrayList<Map<String, Object>>();
	    CriteriosReporteDto dto ;
		try{
			dto = new CriteriosReporteDto();
            dto.setINoEmpresa(funciones.convertirCadenaInteger(paramsGridChequeras.get(iContador).get("noEmpresa").toString()));
            dto.setSChequera(paramsGridChequeras.get(iContador).get("idChequera").toString());
            dto.setBValChequera(!paramsGridChequeras.get(iContador).get("idChequera").toString().equals("%"));
            dto.setIIdBanco(funciones.convertirCadenaInteger(parameters.get("iIdBanco").toString()));
			dto.setDFechaIni(funciones.ponerFechaDate(parameters.get("dFechaIni").toString()));
			dto.setDFechaFin(funciones.ponerFechaDate(parameters.get("dFechaFin").toString()));
			
			resMap = conciliacionDao.consultarReporteAjustes(dto);
			jrDataSource = new JRMapArrayDataSource(resMap.toArray());
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:reporteAjustes");
		}
		return jrDataSource;
	}
	
	@SuppressWarnings("unchecked")
	public JRDataSource reporteFicticios(int iContador,List<Map<String, String>> paramsGridChequeras,Map parameters){
		JRMapArrayDataSource  jrDataSource = null; 
	    List<Map<String, Object>> resMap = new ArrayList<Map<String, Object>>();
	    CriteriosReporteDto dto ;
		try{
			dto = new CriteriosReporteDto();
            dto.setINoEmpresa(funciones.convertirCadenaInteger(paramsGridChequeras.get(iContador).get("noEmpresa").toString()));
            dto.setSChequera(paramsGridChequeras.get(iContador).get("idChequera").toString());
            dto.setBValChequera(!paramsGridChequeras.get(iContador).get("idChequera").toString().equals("%"));
            dto.setIIdBanco(funciones.convertirCadenaInteger(parameters.get("iIdBanco").toString()));
            dto.setDFechaIni(funciones.ponerFechaDate(parameters.get("dFechaIni").toString()));
			dto.setDFechaFin(funciones.ponerFechaDate(parameters.get("dFechaFin").toString()));
			
			resMap = conciliacionDao.consultarReporteMovimientosFicticios(dto);
			jrDataSource = new JRMapArrayDataSource(resMap.toArray());
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:reporteFicticios");
		}
		return jrDataSource;
	}
	
	/**
	 * reglas del negocio de la pantalla reporte global de conciliaciones
	 */
	@SuppressWarnings("unchecked")
	public JRDataSource reporteGlobal(ParamBusquedaDto dtoBusqueda){
		JRMapArrayDataSource  jrDataSource = null; 
		List<Map<String, Object>> resMap;
		try{
			if(dtoBusqueda.getEstatus().equals("PENDIENTE"))
			{
				conciliacionDao.borraTmpConcenConcilia(dtoBusqueda.getIdUsuario());
				dtoBusqueda.setEstatus("P");
				if(this.prepararDatosTmpConcenConcilia(dtoBusqueda))
				{
					resMap = new ArrayList<Map<String, Object>>();
					resMap = conciliacionDao.consultarReportePendientes(dtoBusqueda.getIdUsuario());
					jrDataSource = new JRMapArrayDataSource(resMap.toArray());
			        
			        conciliacionDao.borraTmpConcenConcilia(dtoBusqueda.getIdUsuario());
				}
			}
			
			if(dtoBusqueda.getEstatus().equals("DETECTADO"))
			{
				conciliacionDao.borraTmpConcenConcilia(dtoBusqueda.getIdUsuario());
				dtoBusqueda.setEstatus("D");
				if(this.prepararDatosTmpConcenConcilia(dtoBusqueda))
				{
					resMap = new ArrayList<Map<String, Object>>();
					resMap = conciliacionDao.consultarReportePendientes(dtoBusqueda.getIdUsuario());
					jrDataSource = new JRMapArrayDataSource(resMap.toArray());
			        
			        conciliacionDao.borraTmpConcenConcilia(dtoBusqueda.getIdUsuario());
				}
			}
			
			if(dtoBusqueda.getEstatus().equals("RESUMEN PENDIENTE POR CONCILIAR"))
			{
				conciliacionDao.borraTmpResumen(dtoBusqueda.getIdUsuario());
				if(this.prepararDatosTmpBcoSetResumen(dtoBusqueda))
				{
					resMap = new ArrayList<Map<String, Object>>();
					resMap = conciliacionDao.consultarReporteResumenPendientes(dtoBusqueda.getIdUsuario());
					jrDataSource = new JRMapArrayDataSource(resMap.toArray());
					
			        conciliacionDao.borraTmpResumen(dtoBusqueda.getIdUsuario());
				}
			}
			
			if(dtoBusqueda.getEstatus().equals("MONITOR DE CONCILIACIONES"))
			{
				conciliacionDao.borraTmpMonitor(dtoBusqueda.getIdUsuario());
				if(this.prepararDatosTmpBcoSetMonitor(dtoBusqueda))
				{
					resMap = new ArrayList<Map<String, Object>>();
					resMap = conciliacionDao.consultarReporteMonitor(dtoBusqueda.getIdUsuario());
					jrDataSource = new JRMapArrayDataSource(resMap.toArray());
			        
			        conciliacionDao.borraTmpMonitor(dtoBusqueda.getIdUsuario());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:reporteGlobal");
		}
		return jrDataSource;
		
	}
	
	public boolean prepararDatosTmpConcenConcilia(ParamBusquedaDto dtoBusqueda){
		boolean preparaDatos = false;
		List<TmpConcenConciliaDto> listLlenaTmp = new ArrayList<TmpConcenConciliaDto>();
		TmpConcenConciliaDto dtoTmp;
		int iInsertado = 0;
		try{
			listLlenaTmp = conciliacionDao.consultarTmpConcenConcilia(dtoBusqueda);
		    if(listLlenaTmp.size() > 0)
		    {
		    	for(int i = 0; i < listLlenaTmp.size(); i ++)
		    	{
		    		dtoTmp = new TmpConcenConciliaDto();
		    		dtoTmp.setUsuarioAlta(dtoBusqueda.getIdUsuario());
		    		dtoTmp.setNoEmpresa(listLlenaTmp.get(i).getNoEmpresa());
		    		dtoTmp.setIdBanco(listLlenaTmp.get(i).getIdBanco());
		    		dtoTmp.setDescBanco(listLlenaTmp.get(i).getDescBanco());
		    		dtoTmp.setIdChequera(listLlenaTmp.get(i).getIdChequera());
		    		dtoTmp.setPendTotCargo(listLlenaTmp.get(i).getPendTotCargo());
		    		dtoTmp.setPendImpCargo(listLlenaTmp.get(i).getPendImpCargo());
		    		dtoTmp.setMovtoTotCargo(listLlenaTmp.get(i).getMovtoTotCargo());
		    		dtoTmp.setMovtoImpCargo(listLlenaTmp.get(i).getMovtoImpCargo());
		    		dtoTmp.setMovtoTotAbono(listLlenaTmp.get(i).getMovtoTotAbono());
		    		dtoTmp.setMovtoImpAbono(listLlenaTmp.get(i).getMovtoImpAbono());
		    		dtoTmp.setBancoTotCargo(listLlenaTmp.get(i).getBancoTotCargo());
		    		dtoTmp.setBancoImpCargo(listLlenaTmp.get(i).getBancoImpCargo());
		    		dtoTmp.setBancoTotAbono(listLlenaTmp.get(i).getBancoTotAbono());
		    		dtoTmp.setBancoImpAbono(listLlenaTmp.get(i).getBancoImpAbono());
	                
		    		iInsertado = conciliacionDao.insertaTmpConcenConcilia(dtoTmp);
		    	}
		    	if(iInsertado > 0)
		    		preparaDatos = true;
		    }
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:prepararDatosTmpConcenConcilia");
		}
		return preparaDatos;
	}
	
	public boolean prepararDatosTmpBcoSetResumen(ParamBusquedaDto dtoBusqueda){
		List<TmpBcoSetResumenDto> listLlenaTmpResumen = new ArrayList<TmpBcoSetResumenDto>();
		TmpBcoSetResumenDto dtoTmp;
		boolean preparaDatos = false;
		int iInsertado = 0;
		try{
			listLlenaTmpResumen = conciliacionDao.consultarTmpBancoSetResumen(dtoBusqueda);
			//Set rstdatos = gobjSQL.FunSQLSelectresumenpend(no_emp1, GI_USUARIO, banco1, banco2, chequera, fec1, fec2)
		    
			if(listLlenaTmpResumen.size() > 0)
			{
				for(int i = 0; i < listLlenaTmpResumen.size(); i ++)
				{
					dtoTmp = new TmpBcoSetResumenDto();
					dtoTmp.setUsuarioAlta(dtoBusqueda.getIdUsuario());
					dtoTmp.setNoEmpresa(listLlenaTmpResumen.get(i).getNoEmpresa());
					dtoTmp.setIdBanco(listLlenaTmpResumen.get(i).getIdBanco());
					dtoTmp.setIdChequera(listLlenaTmpResumen.get(i).getIdChequera());
					dtoTmp.setSdoEdoCuenta(listLlenaTmpResumen.get(i).getSdoEdoCuenta());
					dtoTmp.setCargosBanco(listLlenaTmpResumen.get(i).getCargosBanco());
					dtoTmp.setCreditosBanco(listLlenaTmpResumen.get(i).getCreditosBanco());
					dtoTmp.setCargosSet(listLlenaTmpResumen.get(i).getCargosSet());
					dtoTmp.setCreditosSet(listLlenaTmpResumen.get(i).getCreditosSet() + listLlenaTmpResumen.get(i).getCreditosSetPend());
	                
					dtoTmp.setSdoDisponible(dtoTmp.getSdoEdoCuenta()
											+ dtoTmp.getCargosBanco()
											- dtoTmp.getCreditosBanco()
											+ dtoTmp.getCargosSet()
											- dtoTmp.getCreditosSet());
					dtoTmp.setSdoSet(listLlenaTmpResumen.get(i).getSdoFinal());
					dtoTmp.setDiferencia(dtoTmp.getSdoDisponible() - dtoTmp.getSdoSet());
	                       
	                iInsertado = conciliacionDao.insertaTmpBcoSetResumen(dtoTmp);
				}
				if(iInsertado > 0)
					preparaDatos = true;
			}
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:prepararDatosTmpBcoSetResumen");
		}
		return preparaDatos;
	}
	
	public boolean prepararDatosTmpBcoSetMonitor(ParamBusquedaDto dtoBusqueda){
		List<TmpBancoSetMonitorDto> listTmp = new ArrayList<TmpBancoSetMonitorDto>();
		TmpBancoSetMonitorDto dtoTmp;
		int iInsertado = 0;
		boolean preparaDatos = false;
		int tot_can_cargo_set;
		double tot_imp_cargo_set;
		int tot_can_abono_set;
		double tot_imp_abono_set;
		int tot_can_cargo_bco;
		double tot_imp_cargo_bco;
		int tot_can_abono_bco;
		double tot_imp_abonp_bco;
		try{
			listTmp = conciliacionDao.consultarTmpBcoSetMonitor(dtoBusqueda);
		    //Set rstdatos = gobjSQL.FunSQLSelectmonitor(no_emp1, GI_USUARIO, banco1, banco2, chequera, fec1, fec2)
		    if(listTmp.size() > 0)
		    {
		    	for(int i = 0; i < listTmp.size(); i ++)
		    	{
		    		tot_can_cargo_set = 0;
	                tot_imp_cargo_set = 0;
	                tot_can_abono_set = 0;
	                tot_imp_abono_set = 0;
	                tot_can_cargo_bco = 0;
	                tot_imp_cargo_bco = 0;
	                tot_can_abono_bco = 0;
	                tot_imp_abonp_bco = 0;
	                
	                for(int j = 0; j < 12; j ++)
	                {
	                	dtoTmp = new TmpBancoSetMonitorDto();
	                	switch(j)
	                	{
	                	case 0:
	                		dtoTmp.setConcepto("SALDO INICIAL SET");
	                		dtoTmp.setCanCargoSet(0);
	                		dtoTmp.setImpCargoSet(0);
	                		dtoTmp.setCanAbonoSet(0);
	                		dtoTmp.setImpAbonoSet(0);
	                		dtoTmp.setCanCargoBco(0);
	                		dtoTmp.setImpCargoBco(0);
	                		dtoTmp.setCanAbonoBco(0);
	                		dtoTmp.setImpAbonoBco(0);
	                		dtoTmp.setSaldo(listTmp.get(i).getSdoBancoIni());
	                		break;
	                	case 1:
	                		dtoTmp.setConcepto("Mov. al Dia");
	                		dtoTmp.setCanCargoSet(listTmp.get(i).getTotalHistMovCargo() + listTmp.get(i).getTotalPendMovCargo());
	                		dtoTmp.setImpCargoSet(listTmp.get(i).getImpHistMovCargo() + listTmp.get(i).getImpPendMovCargo());
	                		dtoTmp.setCanAbonoSet(listTmp.get(i).getTotalHistMovAbono());
	                		dtoTmp.setImpAbonoSet(listTmp.get(i).getImporteHistMovAbono());
	                		dtoTmp.setCanCargoBco(listTmp.get(i).getTotalBeCargo1() + listTmp.get(i).getTotalBeCargo2());
	                		dtoTmp.setImpCargoBco(listTmp.get(i).getImporteBeCargo1() + listTmp.get(i).getImporteBeCargo2());
	                		dtoTmp.setCanAbonoBco(listTmp.get(i).getTotalBeAbono1() + listTmp.get(i).getTotalBeAbono2());
	                		dtoTmp.setImpAbonoBco(listTmp.get(i).getImporteBeAbono1() + listTmp.get(i).getImporteBeAbono2());
	                		dtoTmp.setSaldo(0);
	                		break;
	                	case 2:
	                		dtoTmp.setConcepto("Con Aut.");
	                		dtoTmp.setCanCargoSet(listTmp.get(i).getMovTotalConciliadosCargo());
	                		dtoTmp.setImpCargoSet(listTmp.get(i).getMovImporteConciliadosCargo());
	                		dtoTmp.setCanAbonoSet(listTmp.get(i).getMovTotalConciliadosAbono());
	                		dtoTmp.setImpAbonoSet(listTmp.get(i).getMovImporteConciliadosAbono());
	                		dtoTmp.setCanCargoBco(listTmp.get(i).getBeTotalConciliadosCargo());
	                		dtoTmp.setImpCargoBco(listTmp.get(i).getBeImporteConciliadosCargo());
	                		dtoTmp.setCanAbonoBco(listTmp.get(i).getBeTotalConciliadosAbono());
	                		dtoTmp.setImpAbonoBco(listTmp.get(i).getBeImporteConciliadosAbono());
	                		dtoTmp.setSaldo(0);
	                		
	                		tot_can_cargo_set = tot_can_cargo_set + dtoTmp.getCanCargoSet();
                            tot_imp_cargo_set = tot_imp_cargo_set + dtoTmp.getImpCargoSet();
                            tot_can_abono_set = tot_can_abono_set + dtoTmp.getCanAbonoSet();
                            tot_imp_abono_set = tot_imp_abono_set + dtoTmp.getImpAbonoSet();
                            tot_can_cargo_bco = tot_can_cargo_bco + dtoTmp.getCanCargoBco();
                            tot_imp_cargo_bco = tot_imp_cargo_bco + dtoTmp.getImpCargoBco();
                            tot_can_abono_bco = tot_can_abono_bco + dtoTmp.getCanAbonoBco();
                            tot_imp_abonp_bco = tot_imp_abonp_bco + dtoTmp.getImpAbonoBco();
	                		break;
	                	case 3:
	                		dtoTmp.setConcepto("Con Man.");
	                		dtoTmp.setCanCargoSet(listTmp.get(i).getMovTotalManualCargo());
	                		dtoTmp.setImpCargoSet(listTmp.get(i).getMovImporteManualCargo());
	                		dtoTmp.setCanAbonoSet(listTmp.get(i).getMovTotalManualAbono());
	                		dtoTmp.setImpAbonoSet(listTmp.get(i).getMovImporteManualAbono());
	                		dtoTmp.setCanCargoBco(listTmp.get(i).getBeTotalManualCargo());
	                		dtoTmp.setImpCargoBco(listTmp.get(i).getBeImporteManualCargo());
	                		dtoTmp.setCanAbonoBco(listTmp.get(i).getBeTotalManualAbono());
	                		dtoTmp.setImpAbonoBco(listTmp.get(i).getBeImporteManualAbono());
	                		dtoTmp.setSaldo(0);
	                		
	                		tot_can_cargo_set = tot_can_cargo_set + dtoTmp.getCanCargoSet();
                            tot_imp_cargo_set = tot_imp_cargo_set + dtoTmp.getImpCargoSet();
                            tot_can_abono_set = tot_can_abono_set + dtoTmp.getCanAbonoSet();
                            tot_imp_abono_set = tot_imp_abono_set + dtoTmp.getImpAbonoSet();
                            tot_can_cargo_bco = tot_can_cargo_bco + dtoTmp.getCanCargoBco();
                            tot_imp_cargo_bco = tot_imp_cargo_bco + dtoTmp.getImpCargoBco();
                            tot_can_abono_bco = tot_can_abono_bco + dtoTmp.getCanAbonoBco();
                            tot_imp_abonp_bco = tot_imp_abonp_bco + dtoTmp.getImpAbonoBco();
                            break;
	                	case 4:
	                		dtoTmp.setConcepto("Detectados");
	                		dtoTmp.setCanCargoSet(listTmp.get(i).getMovTotalDetectadosCargo());
	                		dtoTmp.setImpCargoSet(listTmp.get(i).getMovImporteDetectadosCargo());
	                		dtoTmp.setCanAbonoSet(listTmp.get(i).getMovTotalDetectadosAbono());
	                		dtoTmp.setImpAbonoSet(listTmp.get(i).getMovImporteDetectadosAbono());
	                		dtoTmp.setCanCargoBco(listTmp.get(i).getBeTotalDetectadosCargo());
	                		dtoTmp.setImpCargoBco(listTmp.get(i).getBeImporteDetectadosCargo());
	                		dtoTmp.setCanAbonoBco(listTmp.get(i).getBeTotalDetectadosAbono());
	                		dtoTmp.setImpAbonoBco(listTmp.get(i).getBeImporteDetectadosAbono());
	                		dtoTmp.setSaldo(0);
	                		
	                		tot_can_cargo_set = tot_can_cargo_set + dtoTmp.getCanCargoSet();
                            tot_imp_cargo_set = tot_imp_cargo_set + dtoTmp.getImpCargoSet();
                            tot_can_abono_set = tot_can_abono_set + dtoTmp.getCanAbonoSet();
                            tot_imp_abono_set = tot_imp_abono_set + dtoTmp.getImpAbonoSet();
                            tot_can_cargo_bco = tot_can_cargo_bco + dtoTmp.getCanCargoBco();
                            tot_imp_cargo_bco = tot_imp_cargo_bco + dtoTmp.getImpCargoBco();
                            tot_can_abono_bco = tot_can_abono_bco + dtoTmp.getCanAbonoBco();
                            tot_imp_abonp_bco = tot_imp_abonp_bco + dtoTmp.getImpAbonoBco();
                            break;
	                	case 5:
	                		dtoTmp.setConcepto("Aclarados");
	                		dtoTmp.setCanCargoSet(listTmp.get(i).getMovTotalAclaradoCargo());
	                		dtoTmp.setImpCargoSet(listTmp.get(i).getMovImporteAclaradoCargo());
	                		dtoTmp.setCanAbonoSet(listTmp.get(i).getMovTotalAclaradoAbono());
	                		dtoTmp.setImpAbonoSet(listTmp.get(i).getMovImporteAclaradoAbono());
	                		dtoTmp.setCanCargoBco(listTmp.get(i).getBeTotalAclaradoCargo());
	                		dtoTmp.setImpCargoBco(listTmp.get(i).getBeImporteAclaradoCargo());
	                		dtoTmp.setCanAbonoBco(listTmp.get(i).getBeTotalAclaradoAbono());
	                		dtoTmp.setImpAbonoBco(listTmp.get(i).getBeImporteAclaradoAbono());
	                		dtoTmp.setSaldo(0);
	                		
	                		tot_can_cargo_set = tot_can_cargo_set + dtoTmp.getCanCargoSet();
                            tot_imp_cargo_set = tot_imp_cargo_set + dtoTmp.getImpCargoSet();
                            tot_can_abono_set = tot_can_abono_set + dtoTmp.getCanAbonoSet();
                            tot_imp_abono_set = tot_imp_abono_set + dtoTmp.getImpAbonoSet();
                            tot_can_cargo_bco = tot_can_cargo_bco + dtoTmp.getCanCargoBco();
                            tot_imp_cargo_bco = tot_imp_cargo_bco + dtoTmp.getImpCargoBco();
                            tot_can_abono_bco = tot_can_abono_bco + dtoTmp.getCanAbonoBco();
                            tot_imp_abonp_bco = tot_imp_abonp_bco + dtoTmp.getImpAbonoBco();
                            break;
	                	case 6:
	                		dtoTmp.setConcepto("Por Conciliar");
	                		dtoTmp.setCanCargoSet(listTmp.get(i).getMovTotalPendienteCargo());
	                		dtoTmp.setImpCargoSet(listTmp.get(i).getMovImportePendienteCargo());
	                		dtoTmp.setCanAbonoSet(listTmp.get(i).getMovTotalPendienteAbono());
	                		dtoTmp.setImpAbonoSet(listTmp.get(i).getMovImportePendienteAbono());
	                		dtoTmp.setCanCargoBco(listTmp.get(i).getBeTotalPendienteCargo());
	                		dtoTmp.setImpCargoBco(listTmp.get(i).getBeImportePendienteCargo());
	                		dtoTmp.setCanAbonoBco(listTmp.get(i).getBeTotalPendienteAbono());
	                		dtoTmp.setImpAbonoBco(listTmp.get(i).getBeImportePendienteAbono());
	                		dtoTmp.setSaldo(0);
	                		
	                		tot_can_cargo_set = tot_can_cargo_set + dtoTmp.getCanCargoSet();
                            tot_imp_cargo_set = tot_imp_cargo_set + dtoTmp.getImpCargoSet();
                            tot_can_abono_set = tot_can_abono_set + dtoTmp.getCanAbonoSet();
                            tot_imp_abono_set = tot_imp_abono_set + dtoTmp.getImpAbonoSet();
                            tot_can_cargo_bco = tot_can_cargo_bco + dtoTmp.getCanCargoBco();
                            tot_imp_cargo_bco = tot_imp_cargo_bco + dtoTmp.getImpCargoBco();
                            tot_can_abono_bco = tot_can_abono_bco + dtoTmp.getCanAbonoBco();
                            tot_imp_abonp_bco = tot_imp_abonp_bco + dtoTmp.getImpAbonoBco();
                            break;
	                	case 7:
	                		dtoTmp.setConcepto("Mov. Pendientes");
	                		dtoTmp.setCanCargoSet(listTmp.get(i).getTotalPendMovCargo());
	                		dtoTmp.setImpCargoSet(listTmp.get(i).getImpPendMovCargo());
	                		dtoTmp.setCanAbonoSet(0);
	                		dtoTmp.setImpAbonoSet(0);
	                		dtoTmp.setCanCargoBco(0);
	                		dtoTmp.setImpCargoBco(0);
	                		dtoTmp.setCanAbonoBco(0);
	                		dtoTmp.setImpAbonoBco(0);
	                		dtoTmp.setSaldo(0);
	                		
	                		tot_can_cargo_set = tot_can_cargo_set + dtoTmp.getCanCargoSet();
                            tot_imp_cargo_set = tot_imp_cargo_set + dtoTmp.getImpCargoSet();
                            tot_can_abono_set = tot_can_abono_set + dtoTmp.getCanAbonoSet();
                            tot_imp_abono_set = tot_imp_abono_set + dtoTmp.getImpAbonoSet();
                            tot_can_cargo_bco = tot_can_cargo_bco + dtoTmp.getCanCargoBco();
                            tot_imp_cargo_bco = tot_imp_cargo_bco + dtoTmp.getImpCargoBco();
                            tot_can_abono_bco = tot_can_abono_bco + dtoTmp.getCanAbonoBco();
                            tot_imp_abonp_bco = tot_imp_abonp_bco + dtoTmp.getImpAbonoBco();
                            break;
	                	case 8:
	                		dtoTmp.setConcepto("Total");
	                		dtoTmp.setCanCargoSet(tot_can_cargo_set);
	                		dtoTmp.setImpCargoSet(tot_imp_cargo_set);
	                		dtoTmp.setCanAbonoSet(tot_can_abono_set);
	                		dtoTmp.setImpAbonoSet(tot_imp_abono_set);
	                		dtoTmp.setCanCargoBco(tot_can_cargo_bco);
	                		dtoTmp.setImpCargoBco(tot_imp_cargo_bco);
	                		dtoTmp.setCanAbonoBco(tot_can_abono_bco);
	                		dtoTmp.setImpAbonoBco(tot_imp_abonp_bco);
	                		dtoTmp.setSaldo(0);
	                		break;
	                	case 9:
	                		dtoTmp.setConcepto("Ajuste");
	                		dtoTmp.setCanCargoSet(listTmp.get(i).getMovTotalAjusteCargo());
	                		dtoTmp.setImpCargoSet(listTmp.get(i).getMovImporteAjusteCargo());
	                		dtoTmp.setCanAbonoSet(0);
	                		dtoTmp.setImpAbonoSet(0);
	                		dtoTmp.setCanCargoBco(0);
	                		dtoTmp.setImpCargoBco(0);
	                		dtoTmp.setCanAbonoBco(0);
	                		dtoTmp.setImpAbonoBco(0);
	                		dtoTmp.setSaldo(0);
	                		
	                		tot_can_cargo_set = tot_can_cargo_set + dtoTmp.getCanCargoSet();
                            tot_imp_cargo_set = tot_imp_cargo_set + dtoTmp.getImpCargoSet();
                            tot_can_abono_set = tot_can_abono_set + dtoTmp.getCanAbonoSet();
                            tot_imp_abono_set = tot_imp_abono_set + dtoTmp.getImpAbonoSet();
                            tot_can_cargo_bco = tot_can_cargo_bco + dtoTmp.getCanCargoBco();
                            tot_imp_cargo_bco = tot_imp_cargo_bco + dtoTmp.getImpCargoBco();
                            tot_can_abono_bco = tot_can_abono_bco + dtoTmp.getCanAbonoBco();
                            tot_imp_abonp_bco = tot_imp_abonp_bco + dtoTmp.getImpAbonoBco();
                            break;
	                	case 10:
	                		dtoTmp.setConcepto("Gran Total");
	                		dtoTmp.setCanCargoSet(tot_can_cargo_set);
	                		dtoTmp.setImpCargoSet(tot_imp_cargo_set);
	                		dtoTmp.setCanAbonoSet(tot_can_abono_set);
	                		dtoTmp.setImpAbonoSet(tot_imp_abono_set);
	                		dtoTmp.setCanCargoBco(tot_can_cargo_bco);
	                		dtoTmp.setImpCargoBco(tot_imp_cargo_bco);
	                		dtoTmp.setCanAbonoBco(tot_can_abono_bco);
	                		dtoTmp.setImpAbonoBco(tot_imp_abonp_bco);
	                		dtoTmp.setSaldo(0);
	                		break;
	                	case 11:
	                		dtoTmp.setConcepto("SALDO BANCO");
	                		dtoTmp.setCanCargoSet(0);
	                		dtoTmp.setImpCargoSet(0);
	                		dtoTmp.setCanAbonoSet(0);
	                		dtoTmp.setImpAbonoSet(0);
	                		dtoTmp.setCanCargoBco(0);
	                		dtoTmp.setImpCargoBco(0);
	                		dtoTmp.setCanAbonoBco(0);
	                		dtoTmp.setImpAbonoBco(0);
	                		dtoTmp.setSaldo(listTmp.get(i).getSdoBancoIni());
	                		break;
	                	}
	                    
	                	dtoTmp.setUsuarioAlta(dtoBusqueda.getIdUsuario());
	                    dtoTmp.setNoEmpresa(listTmp.get(i).getNoEmpresa());
		                dtoTmp.setIdBanco(listTmp.get(i).getIdBanco());
		                dtoTmp.setIdChequera(listTmp.get(i).getIdChequera());
	                        
		                iInsertado = conciliacionDao.insertarTmpBcoSetMonitor(dtoTmp);
	                }
		    	}
		    	if(iInsertado > 0)
		    		preparaDatos = true;
		    }
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:prepararDatosTmpBcoSetMonitor");
		}
		return preparaDatos;
	}

	public ConciliacionBancoSetDao getConciliacionDao() {
		return conciliacionDao;
	}

	public void setConciliacionDao(ConciliacionBancoSetDao conciliacionDao) {
		this.conciliacionDao = conciliacionDao;
	}
	
	public CreacionArchivosBusiness getCreacionArchivosBusiness() {
		return creacionArchivosBusiness;
	}

	public void setCreacionArchivosBusiness(
			CreacionArchivosBusiness creacionArchivosBusiness) {
		this.creacionArchivosBusiness = creacionArchivosBusiness;
	}
	
	public GlobalBusiness getGlobalBusiness() {
		return globalBusiness;
	}

	public void setGlobalBusiness(GlobalBusiness globalBusiness) {
		this.globalBusiness = globalBusiness;
	}
	
	public List<MovimientoDto> llenarMovsEmpresaVIngresos(CriteriosBusquedaDto dto){
		List<MovimientoDto> movsEmpresa = new ArrayList<MovimientoDto>();
		
		try{
			movsEmpresa = conciliacionDao.consultarMovsEmpresaVIngresos(dto);
										  

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:llenarMovsEmpresa");
		}
		return movsEmpresa;
	}
	
	
	public Map<String, Object> updateMovimientoSETVIngresos(List<ConciliaBancoDto> listBanco, CriteriosBusquedaDto dto){
		Map<String, Object> mapRet = new HashMap<String, Object>();
		int band = 0;
		int res;
		
		try{
			for(int i = 0; i < listBanco.size(); i ++) {
				res = conciliacionDao.existeMov(Integer.parseInt(listBanco.get(i).getFolioBanco()));
				
				if(res == 0) conciliacionDao.pasaHistAMov(Integer.parseInt(listBanco.get(i).getFolioBanco()));
				
				band = conciliacionDao.updateMovimientoSETVIngresos(listBanco.get(i).getFolioBanco(),dto);
				
				if(band != 0) {
					if(conciliacionDao.existeFlujoMovtos(Integer.parseInt(listBanco.get(i).getFolioBanco())) == 0)
						band = conciliacionDao.insertaDatosConta(dto, i, Integer.parseInt(listBanco.get(i).getFolioBanco()));
					else
						band = conciliacionDao.updateDatosConta(dto, i, Integer.parseInt(listBanco.get(i).getFolioBanco()));
				}
			}
			if (band != 0)				
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

	@Override
	public List<CuentaContableDto> getCuentaContable(int noEmpresa, String idGrupo,String idRubro) {
		List<CuentaContableDto> listDatos = new ArrayList<CuentaContableDto>();
		try{
			listDatos = conciliacionDao.getCuentaContable(noEmpresa, idGrupo,idRubro);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:llenarComboBancos");
		}
		
		return listDatos;
	}
	
	public List<CuentaContableDto> consultarFacturasCXC(String noCliente, int noEmpresa) {
		return conciliacionDao.consultarFacturasCXC(noCliente, noEmpresa);
	}
	
	public String clasificaIngresos(List<Map<String, String>> datosGrid, boolean ietu, boolean iva, String concepto) {
		String result = "";
		int res;
		
		try{
			
			for(int i=0; i<datosGrid.size(); i++) {
				res = conciliacionDao.clasificaIngresos(datosGrid, i, ietu, iva);
				if(res <= 0) result = "Algun registro no fue procesado correctamente!!";
			}
			
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:clasificaIngresos");
			return "Error al procesar la informaci�n";
		}
		return result;
	}
	
	public List<LlenaComboGralDto> llenarComboDeptos(int noEmpresa) {
		return conciliacionDao.llenarComboDeptos(noEmpresa);
	}

		
	public List<LlenaComboGralDto> llenarComboEmpresa(int iUsuario){
		List<LlenaComboGralDto> listDatos = new ArrayList<LlenaComboGralDto>();
		try{
			listDatos = conciliacionDao.llenarComboEmpresa(iUsuario);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:llenarComboBancos");
		}
		return listDatos;
	}
	
	@Override
	public Map<String, String> consultarHeadersMonitor(CriteriosBusquedaDto dto){
		Map<String, String> headers = new HashMap<String, String>();
		try{
//			CriteriosBusquedaDto dto = new CriteriosBusquedaDto();
//			dto.setIdBanco(idBanco);
//			dto.setNoEmpresa(noEmpresa);;
//			dto.setIdBanco(idBanco);
			
			headers = conciliacionDao.consultarHeadersMonitor(dto);
		}catch(Exception e){
			headers.put("Error", e.getMessage());
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:llenarComboBancos");
		}
		return headers;
	}
	
	public MonitorConciliacionDTO consultarMonitorConciliacion(ParamBusquedaDto dto){
		try{
			MonitorConciliacionDTO monitorConciliacion = new MonitorConciliacionDTO();
			
			MonitorConciliaGenDTO mg = null;
			
			dto.setTipoMovimiento("E");
			mg = conciliacionDao.obtenerMovsSETMonitor(dto);
			
			monitorConciliacion.setCantMovAlDiaCargoSET(mg.getCantidad());
			monitorConciliacion.setImpMovAlDiaCargoSET(mg.getImporte());
			
			dto.setTipoMovimiento("I");
			mg = conciliacionDao.obtenerMovsSETMonitor(dto);
			
			monitorConciliacion.setCantMovAlDiaAbonoSET(mg.getCantidad());
			monitorConciliacion.setImpMovAlDiaAbonoSET(mg.getImporte());

			dto.setTipoMovimiento("E");
			mg = conciliacionDao.obtenerMovsBANCOMonitor(dto);

			monitorConciliacion.setCantMovAlDiaCargoBANCO(mg.getCantidad());
			monitorConciliacion.setImpMovAlDiaCargoBANCO(mg.getImporte());

			dto.setTipoMovimiento("I");
			mg = conciliacionDao.obtenerMovsBANCOMonitor(dto);
			
			monitorConciliacion.setCantMovAlDiaAbonoBANCO(mg.getCantidad());
			monitorConciliacion.setImpMovAlDiaAbonoBANCO(mg.getImporte());
			
			/*Automático SET (I)*/
//			cantManAgrupCargoSET
			dto.setTipoMovimiento("E");
			dto.setEstatus("C");
			mg = conciliacionDao.obtenerMovsConcAutomYManualSET(dto);

			monitorConciliacion.setCantConsAutomCargoSET(mg.getCantidad());
			monitorConciliacion.setImpConsAutomCargoSET(mg.getImporte());
			
//			cantManAgrupCargoSET
			dto.setTipoMovimiento("I");
			dto.setEstatus("C");
			mg = conciliacionDao.obtenerMovsConcAutomYManualSET(dto);

			monitorConciliacion.setCantConsAutomAbonoSET(mg.getCantidad());
			monitorConciliacion.setImpConsAutomAbonoSET(mg.getImporte());
			/*Automático SET (T)*/
			
			/*Manual y agrupado SET (I)*/
//			cantManAgrupCargoSET
			dto.setTipoMovimiento("E");
			dto.setEstatus("M");
			mg = conciliacionDao.obtenerMovsConcAutomYManualSET(dto);

			monitorConciliacion.setCantManAgrupCargoSET(mg.getCantidad());
			monitorConciliacion.setImpManAgrupCargoSET(mg.getImporte());
			
//			cantManAgrupCargoSET
			dto.setTipoMovimiento("I");
			dto.setEstatus("M");
			mg = conciliacionDao.obtenerMovsConcAutomYManualSET(dto);

			monitorConciliacion.setCantManAgrupAbonoSET(mg.getCantidad());
			monitorConciliacion.setImpManAgrupAbonoSET(mg.getImporte());
			/*Manual y agrupado SET (T)*/
			
			/*Manual y Agrupado BANCO I */
			
			dto.setTipoMovimiento("E");
			dto.setEstatus("M");
			mg = conciliacionDao.obtenerMovsConcAutomYManualBANCO(dto);

			monitorConciliacion.setCantManAgrupCargoBANCO(mg.getCantidad());
			monitorConciliacion.setImpManAgrupCargoBANCO(mg.getImporte());
			
			dto.setTipoMovimiento("I");
			dto.setEstatus("M");
			mg = conciliacionDao.obtenerMovsConcAutomYManualBANCO(dto);

			monitorConciliacion.setCantManAgrupAbonoBANCO(mg.getCantidad());
			monitorConciliacion.setImpManAgrupAbonoBANCO(mg.getImporte());
			/*Manual y Agrupado BANCO I */

			/*Cons Atuom BANCO I */
			dto.setTipoMovimiento("E");
			dto.setEstatus("C");
			mg = conciliacionDao.obtenerMovsConcAutomYManualBANCO(dto);

			monitorConciliacion.setCantConsAutomCargoBANCO(mg.getCantidad());
			monitorConciliacion.setImpConsAutomCargoBANCO(mg.getImporte());
			
			dto.setTipoMovimiento("I");
			dto.setEstatus("C");
			mg = conciliacionDao.obtenerMovsConcAutomYManualBANCO(dto);

			monitorConciliacion.setCantConsAutomAbonoBANCO(mg.getCantidad());
			monitorConciliacion.setImpConsAutomAbonoBANCO(mg.getImporte());
			/*Cons Atuom BANCO I */

			/*Detectados SET I*/
			
				/*Detectados SET Cargo*/
			dto.setTipoMovimiento("E");
			dto.setEstatus("D");
			mg = conciliacionDao.obtenerAclaradosYDetectadosSET(dto);

			monitorConciliacion.setCantDetectadosCargoSET(mg.getCantidad());
			monitorConciliacion.setImpDetectadosCargoSET(mg.getImporte());
	
				/*Detectados SET Abono*/
			dto.setTipoMovimiento("I");
			dto.setEstatus("D");
			mg = conciliacionDao.obtenerAclaradosYDetectadosSET(dto);

			monitorConciliacion.setCantDetectadosAbonoSET(mg.getCantidad());
			monitorConciliacion.setImpDetectadosAbonoSET(mg.getImporte());
			
			/*Detectados SET T*/
			
			/*Aclarados SET I*/
			
				/*Aclarados SET Cargo*/
			dto.setTipoMovimiento("E");
			dto.setEstatus("A");
			mg = conciliacionDao.obtenerAclaradosYDetectadosSET(dto);
	
			monitorConciliacion.setCantAclaradosCargoSET(mg.getCantidad());
			monitorConciliacion.setImpAclaradosCargoSET(mg.getImporte());
	
				/*Aclarados SET Abono*/
			dto.setTipoMovimiento("I");
			dto.setEstatus("A");
			mg = conciliacionDao.obtenerAclaradosYDetectadosSET(dto);
	
			monitorConciliacion.setCantAclaradosAbonoSET(mg.getCantidad());
			monitorConciliacion.setImpAclaradosAbonoSET(mg.getImporte());
			
			/*Aclarados SET T*/



			/*Detectados BANCO I*/
			
				/*Detectados BANCO Cargo*/
			dto.setTipoMovimiento("E");
			dto.setEstatus("D");
			mg = conciliacionDao.obtenerAclaradosYDetectadosBANCO(dto);

			monitorConciliacion.setCantDetectadosCargoBANCO(mg.getCantidad());
			monitorConciliacion.setImpDetectadosCargoBANCO(mg.getImporte());
	
				/*Detectados BANCO Abono*/
			dto.setTipoMovimiento("I");
			dto.setEstatus("D");
			mg = conciliacionDao.obtenerAclaradosYDetectadosBANCO(dto);

			monitorConciliacion.setCantDetectadosAbonoBANCO(mg.getCantidad());
			monitorConciliacion.setImpDetectadosAbonoBANCO(mg.getImporte());
			
			/*Detectados BANCO T*/
			
			/*Aclarados BANCO I*/
			
				/*Aclarados BANCO Cargo*/
			dto.setTipoMovimiento("E");
			dto.setEstatus("A");
			mg = conciliacionDao.obtenerAclaradosYDetectadosBANCO(dto);
	
			monitorConciliacion.setCantAclaradosCargoBANCO(mg.getCantidad());
			monitorConciliacion.setImpAclaradosCargoBANCO(mg.getImporte());
	
				/*Aclarados BANCO Abono*/
			dto.setTipoMovimiento("I");
			dto.setEstatus("A");
			mg = conciliacionDao.obtenerAclaradosYDetectadosBANCO(dto);
	
			monitorConciliacion.setCantAclaradosAbonoBANCO(mg.getCantidad());
			monitorConciliacion.setImpAclaradosAbonoBANCO(mg.getImporte());
			
			/*Aclarados BANCO T*/

			/*Por conciliar SET I*/
			
				/*Por conciliar SET Cargo*/
			dto.setTipoMovimiento("E");
//			dto.setEstatus("D");
			mg = conciliacionDao.obtenerPorConciliarSET(dto);

			monitorConciliacion.setCantPorConcCargoSET(mg.getCantidad());
			monitorConciliacion.setImpPorConcCargoSET(mg.getImporte());

//			System.out.println("monitorConciliacion.getCantPorConcCargoSET() "+ monitorConciliacion.getCantPorConcCargoSET());
//			System.out.println("mg.getCantidad() "+ mg.getCantidad());
	
				/*Por conciliar SET Abono*/
			dto.setTipoMovimiento("I");
//			dto.setEstatus("D");
			mg = conciliacionDao.obtenerPorConciliarSET(dto);

			monitorConciliacion.setCantPorConcAbonoSET(mg.getCantidad());
			monitorConciliacion.setImpPorConcAbonoSET(mg.getImporte());
			
			/*Por conciliar SET T*/
			
			/*Por conciliar BANCO I*/
			
				/*Por conciliar BANCO Cargo*/
			dto.setTipoMovimiento("E");
//			dto.setEstatus("D");
			mg = conciliacionDao.obtenerPorConciliarBANCO(dto);

			monitorConciliacion.setCantPorConcCargoBANCO(mg.getCantidad());
			monitorConciliacion.setImpPorConcCargoBANCO(mg.getImporte());
	
				/*Por conciliar BANCO Abono*/
			dto.setTipoMovimiento("I");
//			dto.setEstatus("D");
			mg = conciliacionDao.obtenerPorConciliarBANCO(dto);

			monitorConciliacion.setCantPorConcAbonoBANCO(mg.getCantidad());
			monitorConciliacion.setImpPorConcAbonoBANCO(mg.getImporte());
			
			/*Por conciliar BANCO T*/
			
			
			/*Pendientes SET I*/
			
				/*Pendientes SET Cargo*/
			dto.setTipoMovimiento("E");
//			dto.setEstatus("D");
			mg = conciliacionDao.obtenerPendientesSET(dto);

			monitorConciliacion.setCantPendientesCargoSET(mg.getCantidad());
			monitorConciliacion.setImpPendientesCargoSET(mg.getImporte());
	
				/*Pendientes SET Abono*/
			dto.setTipoMovimiento("I");
//			dto.setEstatus("D");
			mg = conciliacionDao.obtenerPendientesSET(dto);

			monitorConciliacion.setCantPendientesAbonoSET(mg.getCantidad());
			monitorConciliacion.setImpPendientesAbonoSET(mg.getImporte());
			
			/*Pendientes SET T*/
			
			/*Pendientes BANCO I*/
			
				/*Pendientes BANCO Cargo*/

			monitorConciliacion.setCantPendientesCargoBANCO(0);
			monitorConciliacion.setImpPendientesCargoBANCO(new BigDecimal(0));
	
			monitorConciliacion.setCantPendientesAbonoBANCO(0);
			monitorConciliacion.setImpPendientesAbonoBANCO(new BigDecimal(0));
			
			/*Pendientes BANCO T*/
			
			
			/*Total SET I*/
			
				/*Total SET Cargo*/
				
			Integer cantTotalCargosSet = monitorConciliacion.getCantConsAutomCargoSET() +
										monitorConciliacion.getCantManAgrupCargoSET()	+
										monitorConciliacion.getCantAclaradosCargoSET() + 
										monitorConciliacion.getCantPorConcCargoSET() +
										monitorConciliacion.getCantDetectadosCargoSET()+
										monitorConciliacion.getCantPendientesCargoSET();
			
			System.out.println(cantTotalCargosSet + " = " + monitorConciliacion.getCantConsAutomCargoSET() + " + " +
					monitorConciliacion.getCantManAgrupCargoSET()+ " + " +
					monitorConciliacion.getCantAclaradosCargoSET()+ " + " + 
					monitorConciliacion.getCantPorConcCargoSET()+ " + " +
					monitorConciliacion.getCantDetectadosCargoSET()+ " + " +
					monitorConciliacion.getCantPendientesCargoSET());
				
			BigDecimal impTotalCargosSet = monitorConciliacion.getImpConsAutomCargoSET().add(
										monitorConciliacion.getImpManAgrupCargoSET()).add(
										monitorConciliacion.getImpAclaradosCargoSET()).add( 
										monitorConciliacion.getImpPorConcCargoSET()).add(
										monitorConciliacion.getImpDetectadosCargoSET()).add(
										monitorConciliacion.getImpPendientesCargoSET());

			monitorConciliacion.setCantTotalCargoSET(cantTotalCargosSet);
			monitorConciliacion.setImpTotalCargoSET(impTotalCargosSet);
	
				/*Total SET Abono*/
			Integer cantTotalAbonosSet = monitorConciliacion.getCantConsAutomAbonoSET() +
					monitorConciliacion.getCantManAgrupAbonoSET()	+
					monitorConciliacion.getCantAclaradosAbonoSET() + 
					monitorConciliacion.getCantPorConcAbonoSET() +
					monitorConciliacion.getCantDetectadosAbonoSET()+
					monitorConciliacion.getCantPendientesAbonoSET();

			BigDecimal impTotalAbonosSet = monitorConciliacion.getImpConsAutomAbonoSET().add(
								monitorConciliacion.getImpManAgrupAbonoSET()).add(
								monitorConciliacion.getImpAclaradosAbonoSET()).add( 
								monitorConciliacion.getImpPorConcAbonoSET()).add(
								monitorConciliacion.getImpDetectadosAbonoSET()).add(
								monitorConciliacion.getImpPendientesAbonoSET());
			
			monitorConciliacion.setCantTotalAbonoSET(cantTotalAbonosSet);
			monitorConciliacion.setImpTotalAbonoSET(impTotalAbonosSet);
			/*Total SET T*/
			
			/*Total SET I*/
			
			/*Total BANCO Cargo*/
			
			Integer cantTotalCargosBanco = monitorConciliacion.getCantConsAutomCargoBANCO() +
										monitorConciliacion.getCantManAgrupCargoBANCO()	+
										monitorConciliacion.getCantAclaradosCargoBANCO() + 
										monitorConciliacion.getCantPorConcCargoBANCO() +
										monitorConciliacion.getCantDetectadosCargoBANCO()+
										monitorConciliacion.getCantPendientesCargoBANCO();
				
			BigDecimal impTotalCargosBanco = monitorConciliacion.getImpConsAutomCargoBANCO().add(
										monitorConciliacion.getImpManAgrupCargoBANCO()).add(
										monitorConciliacion.getImpAclaradosCargoBANCO()).add( 
										monitorConciliacion.getImpPorConcCargoBANCO()).add(
										monitorConciliacion.getImpDetectadosCargoBANCO()).add(
										monitorConciliacion.getImpPendientesCargoBANCO());
	
			monitorConciliacion.setCantTotalCargoBANCO(cantTotalCargosBanco);
			monitorConciliacion.setImpTotalCargoBANCO(impTotalCargosBanco);
	
				/*Total BANCO Abono*/
			Integer cantTotalAbonosBanco = monitorConciliacion.getCantConsAutomAbonoBANCO() +
					monitorConciliacion.getCantManAgrupAbonoBANCO()	+
					monitorConciliacion.getCantAclaradosAbonoBANCO() + 
					monitorConciliacion.getCantPorConcAbonoBANCO() +
					monitorConciliacion.getCantDetectadosAbonoBANCO()+
					monitorConciliacion.getCantPendientesAbonoBANCO();
	
			BigDecimal impTotalAbonosBanco = monitorConciliacion.getImpConsAutomAbonoBANCO().add(
								monitorConciliacion.getImpManAgrupAbonoBANCO()).add(
								monitorConciliacion.getImpAclaradosAbonoBANCO()).add( 
								monitorConciliacion.getImpPorConcAbonoBANCO()).add(
								monitorConciliacion.getImpDetectadosAbonoBANCO()).add(
								monitorConciliacion.getImpPendientesAbonoBANCO());
			
			monitorConciliacion.setCantTotalAbonoBANCO(cantTotalAbonosBanco);
			monitorConciliacion.setImpTotalAbonoBANCO(impTotalAbonosBanco);
			/*Total BANCO T*/

			/*Ajuste SET I*/
			
				/*Ajuste SET Cargo*/
			dto.setTipoMovimiento("E");
	//		dto.setEstatus("D");
			mg = conciliacionDao.obtenerAjusteSET(dto);
	
			monitorConciliacion.setCantAjusteCargoSET(mg.getCantidad());
			monitorConciliacion.setImpAjusteCargoSET(mg.getImporte());
	
				/*Ajuste SET Abono*/
			dto.setTipoMovimiento("I");
	//		dto.setEstatus("D");
			mg = conciliacionDao.obtenerAjusteSET(dto);
	
			monitorConciliacion.setCantAjusteAbonoSET(mg.getCantidad());
			monitorConciliacion.setImpAjusteAbonoSET(mg.getImporte());
			/*Ajuste SET T*/

			/*Ajuste BANCO I*/
				/*Ajuste BANCO Cargo*/
			monitorConciliacion.setCantAjusteCargoBANCO(0);
			monitorConciliacion.setImpAjusteCargoBANCO(new BigDecimal(0));
	
				/*Ajuste BANCO Abono*/
			monitorConciliacion.setCantAjusteAbonoBANCO(0);
			monitorConciliacion.setImpAjusteAbonoBANCO(new BigDecimal(0));
			/*Ajuste BANCO T*/

			/*Gran Total SET I*/
			/*GranTotal SET Cargo*/
			
			Integer cantGranTotalCargosSet = monitorConciliacion.getCantTotalCargoSET() +
					monitorConciliacion.getCantAjusteCargoSET();
				
			BigDecimal impGranTotalCargosSet = monitorConciliacion.getImpTotalCargoSET().add(
					monitorConciliacion.getImpAjusteCargoSET());
	
			monitorConciliacion.setCantGranTotalCargoSET(cantGranTotalCargosSet);
			monitorConciliacion.setImpGranTotalCargoSET(impGranTotalCargosSet);
	
				/*Total SET Abono*/
			Integer cantGranTotalAbonosSet = monitorConciliacion.getCantTotalAbonoSET() +
					monitorConciliacion.getCantAjusteAbonoSET();
	
			BigDecimal impGranTotalAbonosSet = monitorConciliacion.getImpTotalAbonoSET().add(
								monitorConciliacion.getImpAjusteAbonoSET());
			
			monitorConciliacion.setCantGranTotalAbonoSET(cantGranTotalAbonosSet);
			monitorConciliacion.setImpGranTotalAbonoSET(impGranTotalAbonosSet);
			/*Gran Total SET T*/

			/*Gran Total BANCO I*/
			/*GranTotal BANCO Cargo*/
			
			Integer cantGranTotalCargosBanco = monitorConciliacion.getCantTotalCargoBANCO() +
					monitorConciliacion.getCantAjusteCargoBANCO();
				
			BigDecimal impGranTotalCargosBanco = monitorConciliacion.getImpTotalCargoBANCO().add(
					monitorConciliacion.getImpAjusteCargoBANCO());
	
			monitorConciliacion.setCantGranTotalCargoBANCO(cantGranTotalCargosBanco);
			monitorConciliacion.setImpGranTotalCargoBANCO(impGranTotalCargosBanco);
	
				/*Total BANCO Abono*/
			Integer cantGranTotalAbonosBanco = monitorConciliacion.getCantTotalAbonoBANCO() +
					monitorConciliacion.getCantAjusteAbonoBANCO();
	
			BigDecimal impGranTotalAbonosBanco = monitorConciliacion.getImpTotalAbonoBANCO().add(
								monitorConciliacion.getImpAjusteAbonoBANCO());
			
			monitorConciliacion.setCantGranTotalAbonoBANCO(cantGranTotalAbonosBanco);
			monitorConciliacion.setImpGranTotalAbonoBANCO(impGranTotalAbonosBanco);
			/*Gran Total BANCO T*/

			mg = conciliacionDao.obtenerSaldosInicialesMonitor(dto);
			monitorConciliacion.setSalodInicialSET(mg.getImporte());
			monitorConciliacion.setSalodInicialBANCO(mg.getImporte2());
			
			monitorConciliacion.setImporteTotalSET(impGranTotalAbonosSet.subtract(impGranTotalCargosSet));
			monitorConciliacion.setImporteTotalBANCO(impGranTotalAbonosBanco.subtract(impGranTotalCargosBanco));

			return monitorConciliacion;
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:llenarComboBancos");
			
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteMonitor(Map<String, Object> datosReporte){
		JRMapArrayDataSource  jrDataSource = null; 
	    List<Map<String, Object>> resMap = new ArrayList<Map<String, Object>>();
		try{
			//parameters.put("subtitulo", "Cancelacion de Conciliaciones al "+funciones.ponerFechaLetra(globalSingleton.getFechaHoy(), false));
			resMap.add(datosReporte);
            jrDataSource = new JRMapArrayDataSource(resMap.toArray());

		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetBusinessImpl, M:obtenerReporteCancelados");
		}
		return jrDataSource;
	}

}
