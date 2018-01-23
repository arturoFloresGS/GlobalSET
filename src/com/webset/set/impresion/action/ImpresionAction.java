package com.webset.set.impresion.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.impresion.dto.CatFirmasDto;
import com.webset.set.impresion.dto.ConsultaChequesDto;
import com.webset.set.impresion.dto.ControlPapelDto;
import com.webset.set.impresion.service.ImpresionService;
import com.webset.set.ingresos.dto.CatCtaBancoDto;
import com.webset.set.traspasos.dto.MovimientoDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.LlenaComboImpresora;
import com.webset.utils.tools.Utilerias;

/**
 * clase action modulo impresion
 * @author Jessica Arelly Cruz Cruz
 * @since 18/05/2011
 * @Modificado Eric Medina Serrato
 * @Fecha 15/12/2015
 */


public class ImpresionAction {
	Contexto contexto = new Contexto();
	private Bitacora bitacora = new Bitacora();
	private Funciones funciones = new Funciones();
	private ImpresionService impresionService;
	
	private static Logger logger = Logger.getLogger(ImpresionAction.class);
	
	@DirectMethod
	public Map<String, Object>consultarChequesPendientes(String banco,String chequera,String empresa, String cajaPago, String lote,
			String centroCostos, String solicitudIni, String solicitudFin, String fechaIni,String fechaFin,
			String tipoEgreso, String division, String importeIni, String importeFin, String noDocto,
			boolean optSeleccion, boolean optPendientes, boolean optPorConfirmar, boolean optFirmados, String psEquivale,
			String psNoEmpresa, String psbImpreContinua){
	
		Map<String, Object>consulta = new HashMap<String, Object>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 57))
			return consulta;
		
		ConsultaChequesDto dtoCheques = new ConsultaChequesDto(); 
		try{
			impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl");
			
			dtoCheques.setOptSeleccion(optSeleccion);
			dtoCheques.setOptPendientes(optPendientes);
			dtoCheques.setOptPorConfirmar(optPorConfirmar);
			dtoCheques.setOptFirmados(optFirmados);
			dtoCheques.setCriterioBanco(banco != null ? Integer.parseInt(banco) : 0);
			dtoCheques.setCriterioChequera(chequera != null ? chequera : "");
			dtoCheques.setCriterioEmpresa(empresa != null ? Integer.parseInt(empresa): 0);
			dtoCheques.setCriterioCajaPago(cajaPago != null ? Integer.parseInt(cajaPago) : 0);
			dtoCheques.setCriterioLote(lote != null ? Integer.parseInt(lote) : 0);
			dtoCheques.setCriterioCentroCostos(centroCostos != null ? Integer.parseInt(centroCostos) : 0);
			dtoCheques.setCriterioSolicitudIni(solicitudIni != null ? Integer.parseInt(solicitudIni) : 0);
			dtoCheques.setCriterioSolicitudFin(solicitudFin != null ? Integer.parseInt(solicitudFin) : 0);
			if(!fechaIni.equals(""))
				dtoCheques.setCriterioFechaIni(funciones.ponerFechaDate(fechaIni));
			if(!fechaFin.equals(""))
				dtoCheques.setCriterioFechaFin(funciones.ponerFechaDate(fechaFin));
			dtoCheques.setCriterioTipoEgreso(tipoEgreso != null ? tipoEgreso : "");
			dtoCheques.setCriterioDivision(division != null ? Integer.parseInt(division) : 0);
			dtoCheques.setCriterioImporteIni(importeIni != null ? Double.parseDouble(importeIni) : 0);
			dtoCheques.setCriterioImporteFin(importeFin != null ? Double.parseDouble(importeFin) : 0);
			dtoCheques.setCriterioNoDocto(noDocto != null ? noDocto : "");
			dtoCheques.setPsEquivalePersona(psEquivale != null ? psEquivale : "");
			dtoCheques.setNoEmpresa(psNoEmpresa != null ? Integer.parseInt(psNoEmpresa) : 0);
			dtoCheques.setPsbImpreContinua(psbImpreContinua != null ? psbImpreContinua : "");
			consulta = impresionService.consultarChequesPendientes(dtoCheques);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ImpresionAction, M:consultarChequesPendientes");	
		}
		return consulta;
	}
	
	//Revisado EMS 16/12/2015
	@DirectMethod
	public List<MovimientoDto>consultarChequesPendientes1(String banco,String chequera, String cajaPago, 
			String centroCostos, String solicitudIni, String solicitudFin, String fechaIni,String fechaFin,
			String importeIni, String importeFin, String noDocto,
			boolean optSeleccion, boolean optPendientes, boolean optPorConfirmar, boolean optFirmados, String psEquivale,
			String psNoEmpresa, String psbImpreContinua, int idCaja, String cveControl){
		
		List<MovimientoDto>consulta = new ArrayList<MovimientoDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 57))
			return consulta;
		
		ConsultaChequesDto dtoCheques = new ConsultaChequesDto();
		
		try{
			
			impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl");
			
			dtoCheques.setOptSeleccion(optSeleccion);
			dtoCheques.setOptPendientes(optPendientes);
			dtoCheques.setOptPorConfirmar(optPorConfirmar);
			dtoCheques.setOptFirmados(optFirmados);
			dtoCheques.setCriterioBanco(funciones.convertirCadenaInteger(banco.trim()));
			dtoCheques.setCriterioChequera(funciones.validarCadena(chequera.trim()));
			//dtoCheques.setCriterioEmpresa(funciones.convertirCadenaInteger(empresa.trim()));
			dtoCheques.setCriterioCajaPago(funciones.convertirCadenaInteger(cajaPago.trim()));
			dtoCheques.setCriterioCentroCostos(funciones.convertirCadenaInteger(centroCostos.trim()));
			dtoCheques.setCriterioSolicitudIni(funciones.convertirCadenaInteger(solicitudIni.trim()));
			dtoCheques.setCriterioSolicitudFin(funciones.convertirCadenaInteger(solicitudFin.trim()));
			if(!fechaIni.equals(""))
				dtoCheques.setCriterioFechaIni(funciones.ponerFechaDate(fechaIni.trim()));
			if(!fechaFin.equals(""))
				dtoCheques.setCriterioFechaFin(funciones.ponerFechaDate(fechaFin.trim()));
			
			dtoCheques.setCriterioImporteIni(funciones.convertirCadenaDouble(importeIni.trim()));
			dtoCheques.setCriterioImporteFin(funciones.convertirCadenaDouble(importeFin.trim()));
			dtoCheques.setCriterioNoDocto(funciones.validarCadena(noDocto.trim()));
			dtoCheques.setPsEquivalePersona(funciones.validarCadena(psEquivale.trim()));
			dtoCheques.setNoEmpresa(funciones.convertirCadenaInteger(psNoEmpresa.trim()));
			dtoCheques.setPsbImpreContinua(funciones.validarCadena(psbImpreContinua.trim()));
			dtoCheques.setIdUsuario(GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
			dtoCheques.setIdCaja(idCaja);
			dtoCheques.setCriterioCveControl(funciones.validarCadena(cveControl).trim());
			
			consulta = impresionService.consultarChequesPendientes1(dtoCheques);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ImpresionAction, M:consultarChequesPendientes1");
		}
		return consulta;
	}
	
	//Revisado EMS - 15/12/2015
	@DirectMethod
	public List<LlenaComboEmpresasDto>llenarComboEmpresa(){
		List<LlenaComboEmpresasDto> listEmp = new ArrayList<LlenaComboEmpresasDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| (!Utilerias.tienePermiso(WebContextManager.get(), 57) && !Utilerias.tienePermiso(WebContextManager.get(), 62)))
			return listEmp;
		
		try{
			impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl");
			listEmp = impresionService.llenarComboEmpresa(
										GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ImpresionAction, M:llenarComboEmpresa");
		}
		return listEmp;
	}
	
	/*@DirectMethod
	public List<LlenaComboImpresora>llenarComboImpresora(int caja){
		List<LlenaComboImpresora> impresora = new ArrayList<LlenaComboImpresora>();
		try{
			impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl");
			impresora = impresionService.llenarComboImpresora(caja);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ImpresionAction, M:llenarComboImpresora");
		}
		return impresora;
		
	}*/
	
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboGeneral(String campoUno, String campoDos, String tabla, String condicion, String orden){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 57))
			return list;
		
		LlenaComboGralDto dto= new LlenaComboGralDto();
		try{
			impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl");
			dto.setCampoUno(campoUno!=null && !campoUno.equals("")?campoUno:"");
			dto.setCampoDos(campoDos!=null && !campoDos.equals("")?campoDos:"");
			dto.setTabla(tabla!=null && !tabla.equals("")?tabla:"");
			dto.setCondicion(condicion!=null && !condicion.equals("")?condicion:"");
			dto.setOrden(orden!=null && !orden.equals("")?orden:"");
			list=impresionService.llenarComboGral(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ImpresionAction, M:llenarComboGeneral");	
		}
		return list;
	}
	
	@DirectMethod
	public List<CatFirmasDto>consultarFirmasChequera(String cuenta, int banco){
		List<CatFirmasDto> list = new ArrayList<CatFirmasDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 57))
			return list;
		
		try{
			impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl");
			list = impresionService.consultarFirmasChequera(cuenta, banco);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ImpresionAction, M:consultarFirmasChequera");
		}
		return list;
	}
	
	//Revisado 
	@DirectMethod
	public List<LlenaComboGralDto>consultarProveedores(String texto){
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 57))
			return list;
		
		try{
			impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl");
			list = impresionService.consultarProveedores(texto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ImpresionAction, M:consultarProveedores");
		}
		return list;
	}
	
	@DirectMethod
	public Map<String, Object>ejecutarImprimirCheques(String regGrid, String criterios){
		
		Map<String, Object> result =new HashMap<String, Object>();

		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 57))
			return result;

		Gson gson = new Gson();
		List<Map<String, String>> paramsGrid = gson.fromJson(regGrid, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<Map<String, String>> paramsCriterio = gson.fromJson(criterios, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		ConsultaChequesDto dtoBusCheques = new ConsultaChequesDto();
		List<MovimientoDto> listCheques = new ArrayList<MovimientoDto>();

		//Se declaran e inicializan para que no de error en el JS si hay alguna excepcion en esta clase.
		result.put("error", "");
		result.put("datos", "");

		try
		{
			impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl");

			for(int i = 0; i < paramsGrid.size(); i++){

				MovimientoDto dto = new MovimientoDto();

				dto.setBcoCve(funciones.convertirCadenaInteger(paramsGrid.get(i).get("bcoCve")));
				dto.setIdChequera(funciones.validarCadena(paramsGrid.get(i).get("ctaAlias")));
				dto.setMoneda(funciones.validarCadena(paramsGrid.get(i).get("moneda")));
				dto.setDescBanco(funciones.validarCadena(paramsGrid.get(i).get("descBanco")));
				dto.setIdDivisaOriginal(funciones.validarCadena(paramsGrid.get(i).get("idDivisaOriginal")));
				dto.setNoEmpresa(funciones.convertirCadenaInteger(paramsGrid.get(i).get("noEmpresa")));
				dto.setDescCaja(funciones.validarCadena(paramsGrid.get(i).get("descCaja")));
				dto.setNoSolicitud(Integer.parseInt(paramsGrid.get(i).get("noSolicitud")));
				dto.setImporte(funciones.convertirCadenaDouble(paramsGrid.get(i).get("importe")));
				dto.setNoCliente(funciones.validarCadena(paramsGrid.get(i).get("noCliente")));
				dto.setBeneficiario(funciones.validarCadena(paramsGrid.get(i).get("beneficiario")));
				dto.setConcepto(funciones.validarCadena(paramsGrid.get(i).get("concepto")));
				dto.setCiaNmbr(funciones.validarCadena(paramsGrid.get(i).get("ciaNmbr")));
				dto.setCentroCosto(funciones.convertirCadenaInteger(paramsGrid.get(i).get("centroCosto")));
				//dto.setTipoEgreso(funciones.validarCadena(paramsGrid.get(i).get("tipoEgreso")));
				dto.setImporteOriginal(funciones.convertirCadenaDouble(paramsGrid.get(i).get("importeOriginal")));
				dto.setNoContrarecibo(paramsGrid.get(i).get("noContrarecibo"));
				dto.setRfc(funciones.validarCadena(paramsGrid.get(i).get("rfc")));
				dto.setIdContable(funciones.validarCadena(paramsGrid.get(i).get("idContable")));
				dto.setCtaNo(funciones.validarCadena(paramsGrid.get(i).get("ctaNo")));
				dto.setLeyendaProte(funciones.validarCadena(paramsGrid.get(i).get("leyendaProte")));
				dto.setPlaza(Integer.parseInt(paramsGrid.get(i).get("plaza")));
				dto.setSucursal(Integer.parseInt(paramsGrid.get(i).get("sucursal")));
				dto.setBProtegido(funciones.validarCadena(paramsGrid.get(i).get("bProtegido")));
				dto.setNoCuentaS(funciones.validarCadena(paramsGrid.get(i).get("noCuentaS")));
				dto.setCodigoSeguridad(funciones.validarCadena(paramsGrid.get(i).get("codigoSeguridad")));
				dto.setCvePlaza(funciones.validarCadena(paramsGrid.get(i).get("cvePlaza")));
				dto.setIdBanco(funciones.convertirCadenaInteger(paramsGrid.get(i).get("idBanco")));
				dto.setCiaDir(funciones.validarCadena(paramsGrid.get(i).get("ciaDir")));
				dto.setCiaDir2(funciones.validarCadena(paramsGrid.get(i).get("ciaDir2")));
				dto.setCiaRfc(funciones.validarCadena(paramsGrid.get(i).get("ciaRfc")));
				dto.setIdCaja(funciones.convertirCadenaInteger(paramsGrid.get(i).get("idCaja")));

				if(paramsGrid.get(i).get("fecCheque") != null) {
					dto.setFecCheque(funciones.ponerFechaDate(paramsGrid.get(i).get("fecCheque")));
					dto.setFecChequeStr(paramsGrid.get(i).get("fecCheque"));
				}
				dto.setNoCheque(funciones.convertirCadenaInteger(paramsGrid.get(i).get("noCheque")));
				dto.setFirmante1(funciones.validarCadena(paramsGrid.get(i).get("firmante1")));
				dto.setFirmante2(funciones.validarCadena(paramsGrid.get(i).get("firmante2")));
				dto.setNo_firmante1(funciones.convertirCadenaInteger(paramsGrid.get(i).get("noFirmante1")));
				dto.setNo_firmante2(funciones.convertirCadenaInteger(paramsGrid.get(i).get("noFirmante2")));
				dto.setEquivaleBenef(funciones.validarCadena(paramsGrid.get(i).get("equivaleBenef")));
				dto.setIdGrupo(funciones.convertirCadenaInteger(paramsGrid.get(i).get("idGrupo")));
				dto.setIdRubroStr(paramsGrid.get(i).get("idRubroStr"));
				dto.setNoPedido(funciones.convertirCadenaInteger(paramsGrid.get(i).get("noPedido")));
				dto.setEquivaleEmpresa(funciones.convertirCadenaInteger(paramsGrid.get(i).get("equivaleEmpresa")));
				dto.setTipoCambio(funciones.convertirCadenaDouble(paramsGrid.get(i).get("tipoCambio")));
				dto.setLogoBanco(funciones.validarCadena(paramsGrid.get(i).get("logoBanco")));
				dto.setLogoEmpresa(funciones.validarCadena(paramsGrid.get(i).get("logoEmpresa")));
				//dto.setDivision(funciones.validarCadena(paramsGrid.get(i).get("division")));
				dto.setIdCp(funciones.validarCadena(paramsGrid.get(i).get("idCp")));
				dto.setDirLogoAlterno(funciones.validarCadena(paramsGrid.get(i).get("dirLogoAlterno")));
				dto.setTipoImpresion(funciones.validarCadena(paramsGrid.get(i).get("tipoImpresion")));
				dto.setPoHeaders(funciones.validarCadena(paramsGrid.get(i).get("poHeaders")));

				if(paramsGrid.get(i).get("fecValor") != null)
					dto.setFecValor(funciones.ponerFechaDate(paramsGrid.get(i).get("fecValor")));
				dto.setInvoiceType(funciones.validarCadena(paramsGrid.get(i).get("invoiceType")));
				dto.setCPeriodo(funciones.convertirCadenaInteger(paramsGrid.get(i).get("cPeriodo")));
				dto.setNoFolioMov(funciones.convertirCadenaInteger(paramsGrid.get(i).get("noFolioMov")));
				listCheques.add(dto);
			}
			
			dtoBusCheques.setNoEmpresa(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("psNoEmpresa")));
			dtoBusCheques.setNumImpresora(1); //Se pone por default 1, ya que no se utilizar�n las impresoras.
			dtoBusCheques.setIdCaja(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("idCaja")));
			dtoBusCheques.setPsbImpreContinua(funciones.validarCadena(paramsCriterio.get(0).get("psbImpreContinua")));
			dtoBusCheques.setCriterioBanco(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("banco")));
			dtoBusCheques.setCriterioChequera(funciones.validarCadena(paramsCriterio.get(0).get("chequera")));
			dtoBusCheques.setCriterioEmpresa(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("empresa")));
			dtoBusCheques.setCriterioCajaPago(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("cajaPago")));
			dtoBusCheques.setCriterioLote(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("lote")));
			dtoBusCheques.setCriterioCentroCostos(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("centroCostos")));
			dtoBusCheques.setCriterioSolicitudIni(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("solicitudIni")));
			dtoBusCheques.setCriterioSolicitudFin(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("solicitudFin")));
			if(paramsCriterio.get(0).get("fechaIni") != null) {
				dtoBusCheques.setCriterioFechaIni(funciones.ponerFechaDate(paramsCriterio.get(0).get("fechaIni")));
				dtoBusCheques.setCriterioFechaFin(funciones.ponerFechaDate(paramsCriterio.get(0).get("fechaFin")));
			}
			//dtoBusCheques.setCriterioTipoEgreso(funciones.validarCadena(paramsCriterio.get(0).get("tipoEgreso")));
			//dtoBusCheques.setCriterioDivision(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("division")));
			dtoBusCheques.setCriterioImporteIni(funciones.convertirCadenaDouble(paramsCriterio.get(0).get("importeIni")));
			dtoBusCheques.setCriterioImporteFin(funciones.convertirCadenaDouble(paramsCriterio.get(0).get("importeFin")));
			dtoBusCheques.setCriterioNoDocto(funciones.validarCadena(paramsCriterio.get(0).get("noDocto")));
			dtoBusCheques.setCriterioCveControl(funciones.validarCadena(paramsCriterio.get(0).get("cveControl")));
			dtoBusCheques.setPsEquivalePersona(funciones.validarCadena(paramsCriterio.get(0).get("psEquivale")));
			dtoBusCheques.setOptFirmados(funciones.convertirCadenaBoolean(paramsCriterio.get(0).get("optFirmados")));
			dtoBusCheques.setOptSeleccion(funciones.convertirCadenaBoolean(paramsCriterio.get(0).get("optSeleccion")));
			dtoBusCheques.setOptPendientes(funciones.convertirCadenaBoolean(paramsCriterio.get(0).get("optPendientes")));
			dtoBusCheques.setOptPorConfirmar(funciones.convertirCadenaBoolean(paramsCriterio.get(0).get("optPorConfirmar")));
			//criterios extras
			dtoBusCheques.setBancoCharola(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("bancoCharola")));
			dtoBusCheques.setFolioInicial(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("folioIni")));
			dtoBusCheques.setFolioFinal(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("folioFin")));
			dtoBusCheques.setChkNuevoFormato(funciones.convertirCadenaBoolean(paramsCriterio.get(0).get("nuevoFormato")));
			dtoBusCheques.setChkNegociable(funciones.convertirCadenaBoolean(paramsCriterio.get(0).get("negociable")));
			dtoBusCheques.setChkAbono(funciones.convertirCadenaBoolean(paramsCriterio.get(0).get("abono")));
			if(paramsCriterio.get(0).get("fechaCheque") != null)
				dtoBusCheques.setFechaCheque(funciones.ponerFechaDate(paramsCriterio.get(0).get("fechaCheque").toString()));

			String idConfiguracion = paramsCriterio.get(0).get("idConfiguracion").toString();

			result = impresionService.ejecutarImpresionCheques(listCheques, dtoBusCheques, idConfiguracion);
		}
		catch(Exception e){
			System.err.println(e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ImpresionAction, M:ejecutarImprimirCheques");
			logger.error("Ocurrio un error: " + Bitacora.getStackTrace(e));
		}
		return result;
	}
	
	/**
	 * regresa la lista de bancos que tiene asignados la charola seleccionada
	 * @param impresora
	 * @return
	 */
	//Modificado EMS 15/01/2016 - Agregado idBanco
	@DirectMethod
	public List<LlenaComboImpresora>llenarComboBancoImpLaser(int impresora, int idBanco){
		List<LlenaComboImpresora> listEmp = new ArrayList<LlenaComboImpresora>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 57))
			return listEmp;
		
		try{
			impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl");
			listEmp = impresionService.consultarBancoDeCharola(impresora, idBanco);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ImpresionAction, M:llenarComboBancoImpLaser");
		}
		return listEmp;
	}
	
	@DirectMethod
	public List<ControlPapelDto>consultarFoliosPapel(int banco, int caja){
		List<ControlPapelDto> listafolio = new ArrayList<ControlPapelDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 57))
			return listafolio;
		
		try{
			impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl");
			listafolio = impresionService.consultarFoliosPapel(banco, caja);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ImpresionAction, M:consultarFolios");
		}
		return listafolio;
	}
	
	@DirectMethod
	public List<CatCtaBancoDto>llenarComboChequeras(int banco, int empresa){
		List<CatCtaBancoDto> list = new ArrayList<CatCtaBancoDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| (!Utilerias.tienePermiso(WebContextManager.get(), 57) && !Utilerias.tienePermiso(WebContextManager.get(), 62)))
			return list;
		
		try{
			impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl");
			list = impresionService.consultarChequeras(banco, empresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ImpresionAction, M:llenarComboChequeras");
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboEmpresasDto>llenarComboEmpresas(int usuario){
		List<LlenaComboEmpresasDto> list = new ArrayList<LlenaComboEmpresasDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| (!Utilerias.tienePermiso(WebContextManager.get(), 57) && !Utilerias.tienePermiso(WebContextManager.get(), 62)))
			return list;
		
		try{
			impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl");
			list = impresionService.consultarEmpresas(usuario);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ImpresionAction, M:llenarComboEmpresas");
		}
		return list;
	}
	
	@DirectMethod
	public List<MovimientoDto>consultarDocumentosCheques(String fechaIni,String fechaFin,String importeIni, 
			String importeFin, String banco,String chequera,String empresa, boolean optSeleccion, boolean chkTodas){
		List<MovimientoDto>consulta = new ArrayList<MovimientoDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| (!Utilerias.tienePermiso(WebContextManager.get(), 57) && !Utilerias.tienePermiso(WebContextManager.get(), 62)))
			return consulta;
		
		ConsultaChequesDto dtoCheques = new ConsultaChequesDto();
		
		try{
			impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl");
			
			//dtoCheques.setOptSeleccion(funciones.convertirCadenaBoolean(optSeleccion));//generar
			dtoCheques.setOptSeleccion(optSeleccion);//generar
			dtoCheques.setChkTodas(chkTodas);
			dtoCheques.setCriterioBanco(funciones.convertirCadenaInteger(banco));
			dtoCheques.setCriterioChequera(funciones.validarCadena(chequera));
			if(!fechaIni.equals(""))
				dtoCheques.setCriterioFechaIni(funciones.ponerFechaDate(fechaIni));
			if(!fechaFin.equals(""))
				dtoCheques.setCriterioFechaFin(funciones.ponerFechaDate(fechaFin));
			dtoCheques.setCriterioImporteIni(funciones.convertirCadenaDouble(importeIni));
			dtoCheques.setCriterioImporteFin(funciones.convertirCadenaDouble(importeFin));
			dtoCheques.setNoEmpresa(funciones.convertirCadenaInteger(empresa));
			consulta = impresionService.consultarDocumentosCheques(dtoCheques);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ImpresionAction, M:consultarDocumentosCheques");	
		}
		return consulta;
	}
	
	
	@DirectMethod
	public Map<String, Object>ejecutarProteccionCheque(String regGrid, boolean chkH2H, boolean pbPendientes){
		Map<String, Object> result =new HashMap<String, Object>();
		result.put("msgUsuario", "Error desconocido.");
		if(!Utilerias.haveSession(WebContextManager.get())
				|| (!Utilerias.tienePermiso(WebContextManager.get(), 57) && !Utilerias.tienePermiso(WebContextManager.get(), 62)))
			return result;
		
		Gson gson = new Gson();
		List<Map<String, String>> paramsGrid = gson.fromJson(regGrid, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<MovimientoDto> listCheques = new ArrayList<MovimientoDto>();
		
		try{
			impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl");
			for(int i = 0; i < paramsGrid.size(); i++)
			{
				MovimientoDto dto = new MovimientoDto();
				dto.setPoHeaders(funciones.validarCadena(paramsGrid.get(i).get("poHeaders")));
				dto.setNoEmpresa(Integer.parseInt(paramsGrid.get(i).get("noEmpresa")));
				dto.setDescBanco(funciones.validarCadena(paramsGrid.get(i).get("descBanco")));
				dto.setIdChequera(funciones.validarCadena(paramsGrid.get(i).get("idChequera")));
				dto.setNoCheque(funciones.convertirCadenaInteger(paramsGrid.get(i).get("noCheque")));
				dto.setBeneficiario(funciones.validarCadena(paramsGrid.get(i).get("beneficiario")));
				dto.setImporte(funciones.convertirCadenaDouble(paramsGrid.get(i).get("importe")));
				dto.setFecImprime(funciones.ponerFechaDate(paramsGrid.get(i).get("fecImprime")));
				dto.setFecPropuestaStr(paramsGrid.get(i).get("fecPropuestaStr"));
				dto.setNoFolioDet(funciones.convertirCadenaInteger(paramsGrid.get(i).get("noFolioDet")));
				dto.setIdBanco(funciones.convertirCadenaInteger(paramsGrid.get(i).get("idBanco")));
				dto.setConcepto(funciones.validarCadena(paramsGrid.get(i).get("concepto")));
				dto.setPlaza(Integer.parseInt(paramsGrid.get(i).get("plaza")));
				dto.setIdDivisa(funciones.validarCadena(paramsGrid.get(i).get("idDivisa")));
				dto.setNoDocto(funciones.validarCadena(paramsGrid.get(i).get("documento")));
				
				listCheques.add(dto);
			}
			result = impresionService.ejecutarProteccionCheque(listCheques, chkH2H, pbPendientes);
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ImpresionAction, M:ejecutarProteccionCheque");	
		}
		return result;
	}
	
	@DirectMethod
	public String accionarLeyEmpresa (int noEmpresa){
		System.out.println("Action");
		String result = "";
		try {
			impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl");
			result = impresionService.accionarLeyEmpresa(noEmpresa);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ImpresionAction, M:accionarLeyEmpresa");	
		}
		return result;
	}
	
	/** reimpresion de cheques **/
	
	@DirectMethod
	public List<MovimientoDto>consultarSolicitudesReimpresion(String empresa,String usuario,String caja, 
			String banco, String chequera,String solicitudIni,String solicitudFin, String documento, String cheNomina){
		List<MovimientoDto>consulta = new ArrayList<MovimientoDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 57))
			return consulta;
		
		ConsultaChequesDto dtoCheques = new ConsultaChequesDto(); 
		try{
			impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl");
			
			dtoCheques.setCriterioEmpresa(funciones.convertirCadenaInteger(empresa));
			dtoCheques.setIdUsuario(funciones.convertirCadenaInteger(usuario));
			dtoCheques.setIdCaja(funciones.convertirCadenaInteger(caja));
			dtoCheques.setCriterioBanco(funciones.convertirCadenaInteger(banco));
			dtoCheques.setCriterioChequera(funciones.validarCadena(chequera));
			dtoCheques.setCriterioSolicitudIni(funciones.convertirCadenaInteger(solicitudIni));
			dtoCheques.setCriterioSolicitudFin(funciones.convertirCadenaInteger(solicitudFin));
			dtoCheques.setCriterioNoDocto(funciones.validarCadena(documento));
			dtoCheques.setChequeNomina(funciones.convertirCadenaInteger(cheNomina));
			
			consulta = impresionService.consultarSolicitudesReimpresion(dtoCheques);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ImpresionAction, M:consultarSolicitudesReimpresion");	
		}
		return consulta;
	}
	
	@DirectMethod
	public Map<String, Object>ejecutarReimprimirCheques(String regGrid, String criterios){
		Map<String, Object> result =new HashMap<String, Object>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 57))
			return result;
		
		Gson gson = new Gson();
		List<Map<String, String>> paramsGrid = gson.fromJson(regGrid, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<Map<String, String>> paramsCriterio = gson.fromJson(criterios, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		ConsultaChequesDto dtoBusCheques = new ConsultaChequesDto();
		List<MovimientoDto> listCheques = new ArrayList<MovimientoDto>();
		try{
			impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl");
			
			//dtoBusCheques.setNumImpresora(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("numImpresora")));
			dtoBusCheques.setNumImpresora(1);
			dtoBusCheques.setIdCaja(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("caja")));
			dtoBusCheques.setPsbImpreContinua(funciones.validarCadena(paramsCriterio.get(0).get("psbImpreContinua")));
			dtoBusCheques.setCriterioBanco(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("banco")));
			dtoBusCheques.setCriterioChequera(funciones.validarCadena(paramsCriterio.get(0).get("chequera")));
			dtoBusCheques.setCriterioEmpresa(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("empresa")));
			dtoBusCheques.setCriterioSolicitudIni(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("solicitudIni")));
			dtoBusCheques.setCriterioSolicitudFin(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("solicitudFin")));
			dtoBusCheques.setCriterioNoDocto(funciones.validarCadena(paramsCriterio.get(0).get("documento")));
			dtoBusCheques.setChequeNomina(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("cheNomina")));
			//criterios extras
			dtoBusCheques.setChkNuevoFormato(funciones.convertirCadenaBoolean(paramsCriterio.get(0).get("nuevoFormato")));
			//dtoBusCheques.setChkNegociable(funciones.convertirCadenaBoolean(paramsCriterio.get(0).get("negociable")));
			//dtoBusCheques.setChkAbono(funciones.convertirCadenaBoolean(paramsCriterio.get(0).get("abono")));
			if(!paramsCriterio.get(0).get("fechaCheque").toString().equals(""))
				dtoBusCheques.setFechaCheque(funciones.ponerFechaDate(paramsCriterio.get(0).get("fechaCheque").toString()));
			dtoBusCheques.setMotivo(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("motivo")));
			dtoBusCheques.setNoCheque(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("noCheque")));
			dtoBusCheques.setIdUsuario(funciones.convertirCadenaInteger(paramsCriterio.get(0).get("usuario")));
			
			for(int i = 0; i < paramsGrid.size(); i++)
			{
				MovimientoDto dto = new MovimientoDto();
				logger.info(paramsGrid.get(i).get("ctaAlias"));
				dto.setDescCaja(funciones.validarCadena(paramsGrid.get(i).get("descCaja")));
				dto.setNoEmpresa(funciones.convertirCadenaInteger(paramsGrid.get(i).get("noEmpresa")));
				dto.setNoSolicitud(Integer.parseInt(paramsGrid.get(i).get("noSolicitud")));
				dto.setImporte(funciones.convertirCadenaDouble(paramsGrid.get(i).get("importe")));
				dto.setBeneficiario(funciones.validarCadena(paramsGrid.get(i).get("beneficiario")));
				dto.setDescBanco(funciones.validarCadena(paramsGrid.get(i).get("descBanco")));
				dto.setCtaAlias(funciones.validarCadena(paramsGrid.get(i).get("ctaAlias")));
				dto.setCjaUbic(funciones.validarCadena(paramsGrid.get(i).get("cjaUbic")));
				dto.setConcepto(funciones.validarCadena(paramsGrid.get(i).get("concepto")));
				dto.setCiaNmbr(funciones.validarCadena(paramsGrid.get(i).get("ciaNmbr")));
				dto.setCentroCosto(funciones.convertirCadenaInteger(paramsGrid.get(i).get("centroCosto")));
				dto.setBcoCve(funciones.convertirCadenaInteger(paramsGrid.get(i).get("bcoCve")));
				//dto.setTipoEgreso(funciones.validarCadena(paramsGrid.get(i).get("tipoEgreso")));
				dto.setImporteOriginal(funciones.convertirCadenaDouble(paramsGrid.get(i).get("importeOriginal")));
				dto.setNoContrarecibo(paramsGrid.get(i).get("noContrarecibo"));
				dto.setRfc(funciones.validarCadena(paramsGrid.get(i).get("rfc")));
				dto.setMoneda(funciones.validarCadena(paramsGrid.get(i).get("moneda")));
				dto.setIdContable(funciones.validarCadena(paramsGrid.get(i).get("idContable")));
				dto.setCtaNo(funciones.validarCadena(paramsGrid.get(i).get("ctaNo")));
				dto.setLeyendaProte(funciones.validarCadena(paramsGrid.get(i).get("leyendaProte")));
				dto.setPlaza(Integer.parseInt(paramsGrid.get(i).get("plaza")));
				dto.setSucursal(Integer.parseInt(paramsGrid.get(i).get("sucursal")));
				dto.setBProtegido(funciones.validarCadena(paramsGrid.get(i).get("bProtegido")));
				dto.setNoCuentaS(funciones.validarCadena(paramsGrid.get(i).get("noCuentaS")));
				dto.setCodigoSeguridad(funciones.validarCadena(paramsGrid.get(i).get("codigoSeguridad")));
				dto.setCvePlaza(funciones.validarCadena(paramsGrid.get(i).get("cvePlaza")));
				dto.setIdBanco(funciones.convertirCadenaInteger(paramsGrid.get(i).get("idBanco")));
				dto.setCiaDir(funciones.validarCadena(paramsGrid.get(i).get("ciaDir")));
				dto.setCiaDir2(funciones.validarCadena(paramsGrid.get(i).get("ciaDir2")));
				dto.setCiaRfc(funciones.validarCadena(paramsGrid.get(i).get("ciaRfc")));
				dto.setFirmante1(funciones.validarCadena(paramsGrid.get(i).get("firmante1")));
				dto.setFirmante2(funciones.validarCadena(paramsGrid.get(i).get("firmante2")));
				dto.setNoCheque(funciones.convertirCadenaInteger(paramsGrid.get(i).get("noChequeAnt")));//numero cheque anterior
				dto.setFecCheque(funciones.ponerFechaDate(paramsGrid.get(i).get("fecCheque")));
				dto.setEquivaleBenef(funciones.validarCadena(paramsGrid.get(i).get("equivaleBenef")));
				dto.setIdRubroStr(paramsGrid.get(i).get("idRubroStr"));
				dto.setNoPedido(funciones.convertirCadenaInteger(paramsGrid.get(i).get("noPedido")));
				dto.setEquivaleEmpresa(funciones.convertirCadenaInteger(paramsGrid.get(i).get("equivaleEmpresa")));
				dto.setIdDivisaOriginal(funciones.validarCadena(paramsGrid.get(i).get("idDivisaOriginal")));
				dto.setTipoCambio(funciones.convertirCadenaDouble(paramsGrid.get(i).get("tipoCambio")));
				//dto.setDivision(funciones.validarCadena(paramsGrid.get(i).get("division")));
				dto.setIdCp(funciones.validarCadena(paramsGrid.get(i).get("idCp")));
				dto.setLogoEmpresa(funciones.validarCadena(paramsGrid.get(i).get("logoEmpresa")));
				dto.setLogoBanco(funciones.validarCadena(paramsGrid.get(i).get("logoBanco")));
				dto.setDirLogoAlterno(funciones.validarCadena(paramsGrid.get(i).get("dirLogoAlterno")));
				dto.setIndIva(funciones.validarCadena(paramsGrid.get(i).get("indIva")));
				
				listCheques.add(dto);
			}
			result = impresionService.ejecutarReimpresionCheques(listCheques, dtoBusCheques);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ImpresionAction, M:ejecutarReimprimirCheques");
		}
		return result;
	}

	@DirectMethod
	public void obtenerReporteCheque(Map<String, Object> datos, ServletContext context)
	{
		//Map<String, Object> result =new HashMap<String, Object>();
		//JRDataSource jrDataSource = null;
		impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl", context);
		//jrDataSource = impresionService.obtenerReporteCheque(datos);
		impresionService.obtenerReporteCheque(datos);
		
		//return jrDataSource;
	}
	
	@DirectMethod
	public int obtieneUltimoCheqImp(String chequera) {
		int noCheque = 0;
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 57))
			return noCheque;
		
		try{
			impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl");
			noCheque = impresionService.obtieneUltimoCheqImp(chequera);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ImpresionAction, M:obtieneUltimoCheqImp");	
		}
		return noCheque;
	}
	
	
	@DirectMethod
	public int getUltimoImpCtrlCheque(String noEmpresa, String noBanco, String noChequera) {
		int noCheque = 0;
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 57))
			return noCheque;
		
		try{
			impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl");
			noCheque = impresionService.getUltimoImpCtrlCheque(noEmpresa, noBanco, noChequera);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ImpresionAction, M:getUltimoImpCtrlCheque");	
		}
		return noCheque;
	}
	
	
	
	@DirectMethod
	public String escribirExcel(String datosGrid) {
		String resp = "";
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 57))
			return resp;
		
		try{
			impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl");
			resp = impresionService.escribirExcel(datosGrid);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ImpresionAction, M:escribirExcel");	
		}
		return resp;
	}
	
	//Agregado EMS 12/15/2015
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboBeneficiario(String campoUno, String campoDos, String tabla, String condicion, String orden, boolean regUnico){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 57))
			return list;
		
		LlenaComboGralDto dto= new LlenaComboGralDto();

		try{
			impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl");
			dto.setCampoUno(campoUno!=null && !campoUno.equals("")?campoUno:"");
			dto.setCampoDos(campoDos!=null && !campoDos.equals("")?campoDos:"");
			dto.setTabla(tabla!=null && !tabla.equals("")?tabla:"");
			dto.setCondicion(condicion!=null && !condicion.equals("")?condicion:"");
			dto.setOrden(orden!=null && !orden.equals("")?orden:"");
			dto.setRegistroUnico(regUnico);
			list = impresionService.llenarComboBeneficiario(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ImpresionAction, M:llenarComboBeneficiario");	
		}
		return list;
	}
	
	//Agregado EMS 16/12/2015
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboLeyenda(){
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 57))
			return list;
		
		try{
			impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl");
			list = impresionService.llenarComboLeyenda();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ImpresionAction, M:llenarComboLayout");
		}
		return list;
	}
	
	//Agregado YEC 28-ENE-2016
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboConfiguracion(){
		List<LlenaComboGralDto> lista = new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 57))
			return lista;
		
		try{
			impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl");
			lista = impresionService.llenarComboConfiguracion();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+"P:Impresion, C:ImpresionAction, M:llenarComboConfiguracion");
		}
		return lista;
	}
	
	//Agregado EMS: 29/01/2016
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboBancos(int noEmpresa){
		
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 57))
			return list;
		
		try{
			impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl");
			list = impresionService.llenarComboBancos(noEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ImpresionAction, M:llenarComboBancos");
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboBancosCC(int idBanco){
		
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 57))
			return list;
		
		try{
			impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl");
			list = impresionService.llenarComboBancosCC(idBanco);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ImpresionAction, M:llenarComboBancosCC");
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboChequera(int idBanco, int noEmpresa){
		
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 57))
			return list;
		
		try{
			impresionService = (ImpresionService)contexto.obtenerBean("impresionBusinessImpl");
			list = impresionService.llenarComboChequera(idBanco, noEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ImpresionAction, M:llenarComboChequera");
		}
		return list;
	}
	
}
