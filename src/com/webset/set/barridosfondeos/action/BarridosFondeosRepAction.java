
/**
 * @autor COINSIDE
 */

package com.webset.set.barridosfondeos.action;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

//import javax.servlet.ServletContext;







import javax.servlet.ServletContext;





import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.webset.set.barridosfondeos.dto.BusquedaFondeoDto;
import com.webset.set.barridosfondeos.dto.FondeoAutomaticoDto;
//import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
//import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.barridosfondeos.service.BarridosFondeosRepService;
import com.webset.set.barridosfondeos.service.BarridosFondeosService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.utils.tools.Utilerias;
//import com.webset.utils.tools.Utilerias;

public class BarridosFondeosRepAction {
	
	Bitacora bitacora = new Bitacora();
	Contexto contexto = new  Contexto();
	Funciones funciones = new Funciones();
	//List<Boolean> resultPerfil;// = new ArrayList<Boolean>();
	String sLetras = " ";
	int iCen = 0;
	int iDes = 0;
	int iUni = 0;
	
	public List<Map<String, Object>> obtenerReporteArbol(int noEmpresa, ServletContext context)
	{
		List<Map<String, Object>>lista = null;
		if (contexto == null)
			bitacora.insertarRegistro("P:BarridosFondeos, C:BarridosFondeosRepAction, M:obtenerReporteArbol" + " contexto nulo");
		
		try{
			System.out.println("Antes del if");
			//if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				System.out.println("Antes del contexto");
				BarridosFondeosRepService barridosFondeosService = (BarridosFondeosRepService) contexto.obtenerBean("barridosFondeosRepBusinessImpl", context);
				System.out.println("Despues del contexto");
				lista = barridosFondeosService.obtenerReporteArbol(noEmpresa);
				System.out.println("Despues del reporte");
			//}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BarridosFondeos, C:BarridosFondeosRepAction, M:obtenerReporteArbol");			
		}
		return lista;
	}
	
	public List<Map<String, Object>> obtenerReporteFondeo(int idUsuario,String tipoOperacion, ServletContext context)
	{
		List<Map<String, Object>>lista = null;
		if (contexto == null)
			bitacora.insertarRegistro("P:BarridosFondeos, C:BarridosFondeosRepAction, M:obtenerReporteArbol" + " contexto nulo");
		
		try{
			//if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				BarridosFondeosRepService barridosFondeosService = (BarridosFondeosRepService) contexto.obtenerBean("barridosFondeosRepBusinessImpl", context);
				lista = barridosFondeosService.obtenerReporteFondeo(idUsuario, tipoOperacion);
			//}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BarridosFondeos, C:BarridosFondeosRepAction, M:obtenerReporteFondeo");			
		}
		return lista;
	}
	@DirectMethod
	public List<Map<String, Object>> generarReporteAutomatico(String datosGrid, int idEmpresa, int idEmpresaRaiz, int idBanco, 
			String idDivisa, String idChequera, boolean chkMismoBanco, String sTipoBusqueda, boolean bVisibleMontoMinFondeo,
			String montoMinFondeo, int idUsuario, String nomEmpresaRaiz, ServletContext context){
		
		List<Map<String, Object>>lista = null;
		String salida = "";

		Gson gson = new Gson();
		//Map<String, Object> mapRetFondeo = new HashMap<String, Object>();
		
		List<FondeoAutomaticoDto> listGridFondeo = new ArrayList<FondeoAutomaticoDto>();
		try{
			
			BarridosFondeosService barridosFondeosService = (BarridosFondeosService)contexto.obtenerBean("barridosFondeosBusinessImpl", context);
			List<Map<String, String>> objParams = gson.fromJson(datosGrid, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			
			for(int i = 0; i < objParams.size(); i++)
			{
				FondeoAutomaticoDto dto = new FondeoAutomaticoDto();
				dto.setOrden(funciones.convertirCadenaInteger(objParams.get(i).get("orden")));
				dto.setPrestamos(funciones.convertirCadenaDouble(objParams.get(i).get("prestamos")));
		        dto.setNoEmpresaOrigen(funciones.convertirCadenaInteger(objParams.get(i).get("noEmpresaOrigen")));
		     	dto.setNomEmpresaOrigen(funciones.validarCadena(objParams.get(i).get("nomEmpresaOrigen").toString()));
		     	dto.setNoEmpresaDestino(funciones.convertirCadenaInteger(objParams.get(i).get("noEmpresaDestino")));
		     	dto.setNomEmpresaDestino(funciones.validarCadena(objParams.get(i).get("nomEmpresaDestino").toString()));
		     	dto.setDescBanco(funciones.validarCadena(objParams.get(i).get("descBanco").toString()));
		     	dto.setIdBanco(funciones.convertirCadenaInteger(objParams.get(i).get("idBanco")));
		     	dto.setIdChequera(funciones.validarCadena( objParams.get(i).get("idChequera").toString()));
		     	if (objParams.get(i).get("idChequeraPadre") != null)
		     		dto.setIdChequeraPadre(funciones.validarCadena( objParams.get(i).get("idChequeraPadre").toString()));
		     	else
		     		dto.setIdChequeraPadre("");
		     	dto.setSaldoChequera(funciones.convertirCadenaDouble(objParams.get(i).get("saldoChequera")));
		     	dto.setSaldoMinimoChequera(funciones.convertirCadenaDouble(objParams.get(i).get("saldoMinimoChequera")));
		     	dto.setImporteF(funciones.convertirCadenaDouble(objParams.get(i).get("importeF")));
		     	dto.setTipoCambio(funciones.convertirCadenaDouble(objParams.get(i).get("tipoCambio")));
		     	dto.setIdDivisa(funciones.validarCadena(objParams.get(i).get("idDivisa")));
		     	dto.setPm(funciones.convertirCadenaDouble(objParams.get(i).get("pm")));
		     	dto.setPmCheques(funciones.convertirCadenaDouble(objParams.get(i).get("pmCheques")));
		     	dto.setConcepto(funciones.validarCadena(objParams.get(i).get("concepto")));
		     	dto.setImporteTraspaso(funciones.convertirCadenaDouble(objParams.get(i).get("importeTraspaso")));
		     	dto.setImporteBarrido(funciones.convertirCadenaDouble(objParams.get(i).get("importeBarrido")));
		     	dto.setSaldoCoinversion(funciones.convertirCadenaDouble(objParams.get(i).get("saldoCoinversion")));
		     	sTipoBusqueda = objParams.get(i).get("concepto").toString();
		     	listGridFondeo.add(dto);
			}
			
			BusquedaFondeoDto dtoBus = new BusquedaFondeoDto();
			dtoBus.setIdEmpresa(idEmpresa > 0 ? idEmpresa : 0);
			dtoBus.setIdEmpresaRaiz(idEmpresaRaiz >0 ? idEmpresaRaiz : 0);
			dtoBus.setIdBanco(idBanco > 0 ? idBanco : 0);
			dtoBus.setIdDivisa(idDivisa != null && !idDivisa.equals("") ? idDivisa : "");
			dtoBus.setIdChequera(idChequera != null && !idChequera.equals("") ? idChequera : "");
			dtoBus.setChkMismoBanco(chkMismoBanco);
			dtoBus.setSTipoBusqueda(sTipoBusqueda);
			dtoBus.setIdUsuario(idUsuario);
			dtoBus.setNomEmpresaRaiz(nomEmpresaRaiz);
			
			barridosFondeosService.prepararFondeoAutomatico(listGridFondeo, dtoBus);

			BarridosFondeosRepService barridosFondeosRepService = (BarridosFondeosRepService) contexto.obtenerBean("barridosFondeosRepBusinessImpl", context);
			lista = barridosFondeosRepService.obtenerReporteFondeo(idUsuario, sTipoBusqueda);
			
			
			/*
			BarridosFondeosMail barridosMail = new BarridosFondeosMail();
			WebContext context = WebContextManager.get();
			ServletContext contextEnviar = context.getServletContext();
			
			barridosMail.enviarFondeoAutomatico(datosGrid, idEmpresaRaiz, idUsuario, 'F', nomEmpresaRaiz, contextEnviar);
			*/
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Coinversion, C:CoinversionAction, M:ejecutarFondeoAutomatico");
		}
		return lista;
	}
	
	/**
	 * Representa la interface entre vista y servicio para la obtencion del reporte de barridos y fondeos automaticos
	 * @param datosGrid
	 * @param idEmpresa
	 * @param idEmpresaRaiz
	 * @param idBanco
	 * @param idDivisa
	 * @param idChequera
	 * @param chkMismoBanco
	 * @param sTipoBusqueda
	 * @param bVisibleMontoMinFondeo
	 * @param montoMinFondeo
	 * @return Lista de mapas con la informacion
	 */
	public List<Map<String, Object>> obtenerReporteBarridoAut(String datosGrid, ServletContext context){
		
		Gson gson = new Gson();
		//Map<String, Object> mapRetFondeo = new HashMap<String, Object>();
		
		List<FondeoAutomaticoDto> listGridFondeo = new ArrayList<FondeoAutomaticoDto>();
		List<Map<String, Object>> listaFinal = null;
		try{
			
			BarridosFondeosRepService barridosFondeosRepService = (BarridosFondeosRepService)contexto.obtenerBean("barridosFondeosRepBusinessImpl", context);
			List<Map<String, String>> objParams = gson.fromJson(datosGrid, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			
			for(int i = 0; i < objParams.size(); i++)
			{
				FondeoAutomaticoDto dto = new FondeoAutomaticoDto();
				dto.setOrden(funciones.convertirCadenaInteger(objParams.get(i).get("orden")));
				dto.setPrestamos(funciones.convertirCadenaDouble(objParams.get(i).get("prestamos")));
		        dto.setNoEmpresaOrigen(funciones.convertirCadenaInteger(objParams.get(i).get("noEmpresaOrigen")));
		     	dto.setNomEmpresaOrigen(funciones.validarCadena(objParams.get(i).get("nomEmpresaOrigen").toString()));
		     	dto.setNoEmpresaDestino(funciones.convertirCadenaInteger(objParams.get(i).get("noEmpresaDestino")));
		     	dto.setNomEmpresaDestino(funciones.validarCadena(objParams.get(i).get("nomEmpresaDestino").toString()));
		     	dto.setDescBanco(funciones.validarCadena(objParams.get(i).get("descBanco").toString()));
		     	dto.setIdBanco(funciones.convertirCadenaInteger(objParams.get(i).get("idBanco")));
		     	if (objParams.get(i).get("idChequeraPadre") != null)
		     		dto.setIdChequeraPadre(funciones.validarCadena( objParams.get(i).get("idChequeraPadre").toString()));
		     	else
		     		dto.setIdChequeraPadre("");
		     	dto.setIdChequera(funciones.validarCadena( objParams.get(i).get("idChequera").toString()));
		     	dto.setSaldoChequera(funciones.convertirCadenaDouble(objParams.get(i).get("saldoChequera")));
		     	dto.setSaldoMinimoChequera(funciones.convertirCadenaDouble(objParams.get(i).get("saldoMinimoChequera")));
		     	dto.setImporteF(funciones.convertirCadenaDouble(objParams.get(i).get("importeF")));
		     	dto.setTipoCambio(funciones.convertirCadenaDouble(objParams.get(i).get("tipoCambio")));
		     	dto.setIdDivisa(funciones.validarCadena(objParams.get(i).get("idDivisa")));
		     	dto.setPm(funciones.convertirCadenaDouble(objParams.get(i).get("pm")));
		     	dto.setPmCheques(funciones.convertirCadenaDouble(objParams.get(i).get("pmCheques")));
		     	dto.setConcepto(funciones.validarCadena(objParams.get(i).get("concepto")));
		     	dto.setImporteTraspaso(funciones.convertirCadenaDouble(objParams.get(i).get("importeTraspaso")));
		     	dto.setImporteBarrido(funciones.convertirCadenaDouble(objParams.get(i).get("importeBarrido")));
		     	dto.setSaldoCoinversion(funciones.convertirCadenaDouble(objParams.get(i).get("saldoCoinversion")));
		     	listGridFondeo.add(dto);
			}
			
			listaFinal = barridosFondeosRepService.obtenerReporteFondeoBarridoAut(listGridFondeo);
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:BarridosFondeos, C:BarridosFondeosRepAction!, M:obtenerReporteBarridoAut");
		}
		return listaFinal;
	}

	public List<Map<String, Object>> obtenerReporteArbolEstruct(int noEmpresa, int idUsuario, ServletContext context)
	{
		List<Map<String, Object>>lista = null;
		if (contexto == null){
			bitacora.insertarRegistro("P:BarridosFondeos, C:BarridosFondeosRepAction, M:obtenerReporteArbolEstruct" + " contexto nulo");
			return null;
		}		
		
		try{
			BarridosFondeosRepService barridosFondeosService = (BarridosFondeosRepService) contexto.obtenerBean("barridosFondeosRepBusinessImpl", context);
			lista = barridosFondeosService.obtenerReporteArbolEstruc(noEmpresa, idUsuario);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BarridosFondeos, C:BarridosFondeosRepAction, M:obtenerReporteArbolEstruct");			
		}
		return lista;
	}
	
	public List<Map<String, Object>> obtenerReporteFiliales(int noEmpresa, ServletContext context)
	{
		List<Map<String, Object>>lista = null;
		if (contexto == null){
			bitacora.insertarRegistro("P:BarridosFondeos, C:BarridosFondeosRepAction, M:obtenerReporteFiliales" + " contexto nulo");
			return null;
		}		
		
		try{
			BarridosFondeosRepService barridosFondeosService = (BarridosFondeosRepService) contexto.obtenerBean("barridosFondeosRepBusinessImpl", context);
			lista = barridosFondeosService.obtenerReporteFiliales(noEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BarridosFondeos, C:BarridosFondeosRepAction, M:obtenerReporteFiliales");			
		}
		return lista;
	}
	
	public List<Map<String, Object>> obtenerReporteBarridosFeondeos(int noEmpresa, int idUsuario, String fecha, ServletContext context)
	{
		List<Map<String, Object>>lista = null;
		if (contexto == null){
			bitacora.insertarRegistro("P:BarridosFondeos, C:BarridosFondeosRepAction, M:obtenerReporteFiliales" + " contexto nulo");
			return null;
		}		
		
		try{
			BarridosFondeosRepService barridosFondeosService = (BarridosFondeosRepService) contexto.obtenerBean("barridosFondeosRepBusinessImpl", context);
			lista = barridosFondeosService.obtenerReporteBarridosFondeos(noEmpresa, idUsuario, fecha);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BarridosFondeos, C:BarridosFondeosRepAction, M:obtenerReporteBarridosFeondeos");			
		}
		return lista;
	}
	
	public HSSFWorkbook obtenerExcelBarridosFondeos(int noEmpresa, int idUsuario, String fecha, ServletContext context)
	{
		HSSFWorkbook hb=null;
		BarridosFondeosRepService barridosFondeosService = (BarridosFondeosRepService) contexto.obtenerBean("barridosFondeosRepBusinessImpl", context);
		List <Map<String, String>> lista = barridosFondeosService.obtenerReporteBarridosFondeosStr(noEmpresa, idUsuario, fecha);
		try{
			hb = Utilerias.generarExcel(new String[]{
						"tipo",
						"banco_origen",					
						"chequera_origen",
						"empresa filial",
						"banco_destinol",
						"chequera_destino",				
						"importe"	
				}, lista, new String[] {"tipo", 
					"desc_banco", 
					"id_chequera", 
					"desc_empresa_benef",
					"desc_banco_benef",
					"id_chequera_benef",
					"importe"}, "FONDEOS Y BARRIDOS AL " + fecha);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BarridosFondeos, C:BarridosFondeosRepAction, M:obtenerExcelBarridosFondeos");			
		}
		
		return hb;
	}
	
	public List<Map<String, String>> obtenerReporteCuadreFeondeo(int noEmpresa, int idUsuario, String fecha, ServletContext context)
	{
		List<Map<String, String>>lista = null;
		if (contexto == null){
			bitacora.insertarRegistro("P:BarridosFondeos, C:BarridosFondeosRepAction, M:obtenerReporteCuadreFeondeo" + " contexto nulo");
			return null;
		}		
		
		try{
			BarridosFondeosRepService barridosFondeosService = (BarridosFondeosRepService) contexto.obtenerBean("barridosFondeosRepBusinessImpl", context);
			lista = barridosFondeosService.obtenerReporteCuadreFondeo(noEmpresa, idUsuario, fecha);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BarridosFondeos, C:BarridosFondeosRepAction, M:obtenerReporteCuadreFeondeo");			
		}
		return lista;
	}
	
	public HSSFWorkbook obtenerExcelCuadreFeondeo(int noEmpresa, int idUsuario, String fecha, ServletContext context)
	{
		HSSFWorkbook hb=null;
		BarridosFondeosRepService barridosFondeosService = (BarridosFondeosRepService) contexto.obtenerBean("barridosFondeosRepBusinessImpl", context);
		List <Map<String, String>> lista = barridosFondeosService.obtenerReporteCuadreFondeo(noEmpresa, idUsuario, fecha);
		try{
			hb = Utilerias.generarExcel(new String[]{
						"EMPRESA PADRE",
						"CHEQUERA ORIGEN",					
						"EMPRESA",
						"BANCO",
						"CHEQUERA",
						"DIVISA",
						"BENEFICIARIO",
						"DOCUMENTO",
						"FONDEO REQUERIDO",				
						"FONDEO APLICADO"	
				}, lista, new String[] {"no_empresa_padre",
					"id_chequera_padre",					
					"nom_empresa",
					"desc_banco",
					"id_chequera_hijo",
					"desc_divisa",
					"beneficiario",
					"no_docto",
					"importe",
					"importe_fondeo"}, "CUADRE DE FONDEO AL " + fecha);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BarridosFondeos, C:BarridosFondeosRepAction, M:obtenerExcelBarridosFondeos");			
		}
		
		return hb;
	}
}
