package com.webset.set.egresos.action;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
/**
 * @autor Cristian Garcia Garcia
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.egresos.business.CapturaSolicitudesPagoBusinessImpl;
import com.webset.set.egresos.business.ConsultaPropuestasBusinessImpl;
import com.webset.set.egresos.dto.BitacoraPropuestasDto;
import com.webset.set.egresos.dto.BloqueoPagoCruzadoDto;
import com.webset.set.egresos.dto.ColoresBitacoraDto;
import com.webset.set.egresos.dto.ConsultaPropuestasDto;
import com.webset.set.egresos.dto.ParamConsultaPropuestasDto;
import com.webset.set.egresos.service.ConsultaPropuestasService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.GrupoEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.SeleccionAutomaticaGrupoDto;
import com.webset.utils.tools.Utilerias;

import net.sf.jasperreports.engine.JRDataSource;

public class ConsultaPropuestasAction {
	Bitacora bitacora = new Bitacora();
	Contexto contexto=new  Contexto();
	Funciones funciones = new Funciones();
	ConsultaPropuestasService consultaPropuestasService;
	private Logger logger = Logger.getLogger(ConsultaPropuestasAction.class);
	/* //Valida que se tenga una session, si no no permite seguir con el m�dodo
	 * Implementado en cada metodo
	 * if(!Utilerias.haveSession(WebContextManager.get())){
			List<LlenaComboGralDto> tmp = new ArrayList<>();
			return tmp;
		}
		
		--Valida que el usuario tenga asignada la pantalla.
		|| !Utilerias.tienePermiso(WebContextManager.get(), 57)
	 */
	
	//revisado
	@DirectMethod
	public List<LlenaComboGralDto>llenarCombo(String campoUno, String campoDos, String tabla, String condicion, String orden){
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51)){
			List<LlenaComboGralDto> tmp = new ArrayList<>();
			return tmp;
		}
				
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		LlenaComboGralDto dto= new LlenaComboGralDto();
		try{
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			dto.setCampoUno(campoUno!=null && !campoUno.equals("")?campoUno:"");
			dto.setCampoDos(campoDos!=null && !campoDos.equals("")?campoDos:"");
			dto.setTabla(tabla!=null && !tabla.equals("")?tabla:"");
			dto.setCondicion(condicion!=null && !condicion.equals("")?condicion:"");
			dto.setOrden(orden!=null && !orden.equals("")?orden:"");
			list = consultaPropuestasService.LlenarComboGral(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:llenarCombo");	
		}
		return list;
	}@DirectMethod
	
	public List<LlenaComboGralDto> llenarComboDivisaPago(){
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51)){
			List<LlenaComboGralDto> tmp = new ArrayList<>();
			return tmp;
		}
				
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		LlenaComboGralDto dto= new LlenaComboGralDto();
		dto.setCampoUno("id_divisa");
		dto.setCampoDos("desc_divisa");
		dto.setTabla("cat_divisa");
		dto.setCondicion("muestra_divisa = 'S'");
		try{
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			list = consultaPropuestasService.LlenarComboGralB(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:llenarCombo");	
		}
		return list;
	}

	//revisado
	@DirectMethod
	public String obtenerFechaHoy(){
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51)){
			return "";
		}
		
		String fechaString="";
		try{
			CapturaSolicitudesPagoBusinessImpl consultaPropuestasService= (CapturaSolicitudesPagoBusinessImpl)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
			fechaString= consultaPropuestasService.obtenerFechaHoy().toString();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:obtenerFechaHoy");			
		}
		return fechaString;
	}
	
	//revisado - Se agregaros las divisas
	@DirectMethod 
	public List<SeleccionAutomaticaGrupoDto>consultarSeleccionAutomatica(int idGrupoEmpresa,String idProv,int idGrupoRubro,  
			String fecIni, String fecFin, String todosUsuarios, String propVigentes, boolean todasPro,
			boolean divMN, boolean divDLS, boolean divEUR, boolean divOtras, String tipoRegla, String reglaNegocio, 
			boolean nominaRH, boolean nominaTes){
		
		List<SeleccionAutomaticaGrupoDto>list= new ArrayList<SeleccionAutomaticaGrupoDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51)){
			return list;
		}
		
		//ParamConsultaPropuestasDto dtoIn= new ParamConsultaPropuestasDto();
		ConsultaPropuestasDto dtoIn = new ConsultaPropuestasDto();
		
		try{
			
			consultaPropuestasService =(ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			
			dtoIn.setGrupoEmpresa(idGrupoEmpresa);
			dtoIn.setIdCliente(idProv);
			dtoIn.setPvGrupoRubro(idGrupoRubro);
			dtoIn.setIdUsuario(GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
			dtoIn.setFechaIni(fecIni!=null && !fecIni.equals("")?fecIni:"");
			dtoIn.setFechaFin(fecFin!=null && !fecFin.equals("")?fecFin:"");
			dtoIn.setPvsDivision(""); //Vacio porque no se muestra en pantalla
			
			if(todosUsuarios!=null && todosUsuarios.equals("true"))
				dtoIn.setSoloMisProp(false);
			else
				dtoIn.setSoloMisProp(true);
			
			if(propVigentes!=null && propVigentes.equals("true"))
				dtoIn.setSoloPropAct(true);
			else
				dtoIn.setSoloPropAct(false);
			
			dtoIn.setTodasPropuestas(todasPro);
			
			dtoIn.setDivMN(divMN);
			dtoIn.setDivDLS(divDLS);
			dtoIn.setDivEUR(divEUR);
			dtoIn.setDivOtras(divOtras);
			dtoIn.setTipoRegla(tipoRegla);
			dtoIn.setReglaNegocio(reglaNegocio);
			dtoIn.setNominaRH(nominaRH);
			dtoIn.setNominaTes(nominaTes);
			
			list = consultaPropuestasService.consultarSeleccionAutomatica(dtoIn);
			//list = consultaPropuestasService.consultarSeleccionAutomaticaStored(dtoIn); 
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:consultarSeleccionAutomatica");	
		}
		return list;
	}
	
	//revisado
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboGrupoEmpresas(){
		GrupoEmpresasDto dtoIn= new GrupoEmpresasDto();
		List<LlenaComboGralDto> listRet= new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51)){
			return listRet;
		}
		
		try{
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			dtoIn.setIdUsuario(""+GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
			listRet = consultaPropuestasService.llenarComboGrupoEmpresa(dtoIn);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:llenarComboGrupoEmpresas");	
		}
		return listRet;
	}
	
	//revisado
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboBeneficiario(String campoUno, String campoDos, String tabla, String condicion, String orden, boolean regUnico){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		LlenaComboGralDto dto= new LlenaComboGralDto();

		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51)){
			return list;
		}
		
		try{
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			dto.setCampoUno(campoUno!=null && !campoUno.equals("")?campoUno:"");
			dto.setCampoDos(campoDos!=null && !campoDos.equals("")?campoDos:"");
			dto.setTabla(tabla!=null && !tabla.equals("")?tabla:"");
			dto.setCondicion(condicion!=null && !condicion.equals("")?condicion:"");
			dto.setOrden(orden!=null && !orden.equals("")?orden:"");
			dto.setRegistroUnico(regUnico);
			list = consultaPropuestasService.llenarComboBeneficiario(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:llenarComboBeneficiario");	
		}
		return list;
	}
	
	//revisado
	@DirectMethod
	public List<MovimientoDto>consultarDetalle(int idGrupoEmpresa, int idGrupo, String cveControl, int usr1, int usr2, int usr3){
		SeleccionAutomaticaGrupoDto dtoIn= new SeleccionAutomaticaGrupoDto();
		List<MovimientoDto>listDetalle= new ArrayList<MovimientoDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51)){
			return listDetalle;
		}
		
		try{
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			dtoIn.setIdGrupoFlujo(idGrupoEmpresa);
			dtoIn.setIdGrupo(idGrupo);
			dtoIn.setCveControl(cveControl);
			dtoIn.setUsuarioUno(usr1);
			dtoIn.setUsuarioDos(usr2);
			dtoIn.setUsuarioTres(usr3);
//			System.out.println(idGrupoEmpresa+" "+idGrupo+" "+cveControl+" "+usr1+
//					" "+usr2+" "+usr3);
			listDetalle = consultaPropuestasService.consultarDetalle(dtoIn);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:consultarDetalle");	
		}
		return listDetalle;
	}
	
	//revisado
	@DirectMethod
	public Map<String, Object> autorizar(String data, String detalle, String autoDesauto, int autCheq) {
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(data, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
	  
		Map<String,Object> result= new HashMap<String,Object>();
		List<SeleccionAutomaticaGrupoDto> listaDto = new ArrayList<SeleccionAutomaticaGrupoDto>();
		
		result.put("bit","0");
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51)){
			return result;
		}
		
		try {
			System.out.println("objParams "+objParams+"detalle "+detalle+"autodesauto "+autoDesauto+"autchek "+autCheq);
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			
			for(int t=0; t<objParams.size(); t++) {	//Por ahora solo lo hace una vez y despues se habilitara para que autorize varios
				SeleccionAutomaticaGrupoDto dtoGridMaster= new SeleccionAutomaticaGrupoDto();
				dtoGridMaster.setIdGrupoFlujo(objParams.get(t).get("idGrupoFlujo")!=null?Integer.parseInt(objParams.get(t).get("idGrupoFlujo")):0);
				dtoGridMaster.setIdGrupo(objParams.get(t).get("idGrupo")!=null?Integer.parseInt(objParams.get(t).get("idGrupo")):0);
				dtoGridMaster.setCveControl(objParams.get(t).get("cveControl")!=null?objParams.get(t).get("cveControl"):"");
				dtoGridMaster.setUsuarioUno(objParams.get(t).get("usr1")!=null?Integer.parseInt(objParams.get(t).get("usr1")):0);
				dtoGridMaster.setUsuarioDos(objParams.get(t).get("usr2")!=null?Integer.parseInt(objParams.get(t).get("usr2")):0);
				dtoGridMaster.setUsuarioTres(objParams.get(t).get("usr3")!=null?Integer.parseInt(objParams.get(t).get("usr3")):0);
				dtoGridMaster.setNivelAutorizacion(objParams.get(t).get("nivelAut")!=null?Integer.parseInt(objParams.get(t).get("nivelAut")):0);
				dtoGridMaster.setCupoTotal(objParams.get(t).get("cupoTotal")!=null?Double.parseDouble(objParams.get(t).get("cupoTotal")):0);
				dtoGridMaster.setIdUsuario(GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
				//dtoGridMaster.setIdUsuario(objParams.get(t).get("idUsuario")!=null?Integer.parseInt(objParams.get(t).get("idUsuario")):0);
				dtoGridMaster.setPsw(objParams.get(t).get("psw")!=null?objParams.get(t).get("psw"):"");
				listaDto.add(dtoGridMaster);
			}
			
			if(listaDto.size() > 1){
				result = consultaPropuestasService.autorizarProp(listaDto, detalle, autoDesauto, autCheq);
			}else{
				
				if(autoDesauto.equals("DAUT") && listaDto.get(0).getIdUsuario() == listaDto.get(0).getUsuarioUno() && listaDto.get(0).getUsuarioDos() != 0)
					result.put("msgUsuario", "No puede quitar la primer autorizacion hasta que se elimine la segunda");
				else
					result = consultaPropuestasService.autorizarProp(listaDto, detalle, autoDesauto, autCheq);
				
			}
			
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasAction, M:autorizar");
		}
		return result;
	}
	
	@DirectMethod
	public Map<String, Object> autorizarModificaciones(String data) {
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(data, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		Map<String,Object> result= new HashMap<String,Object>();
		SeleccionAutomaticaGrupoDto dtoGridMaster= new SeleccionAutomaticaGrupoDto();
		result.put("msgUsuario", "");
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51)){
			return result;
		}
		
		try {
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			
			for(int t=0; t<objParams.size(); t++) {	//Por ahora solo lo hace una vez y despues se habilitara para que autorize varios
				dtoGridMaster.setIdGrupoFlujo(objParams.get(t).get("idGrupoFlujo")!=null?Integer.parseInt(objParams.get(t).get("idGrupoFlujo")):0);
				dtoGridMaster.setIdGrupo(objParams.get(t).get("idGrupo")!=null?Integer.parseInt(objParams.get(t).get("idGrupo")):0);
				dtoGridMaster.setCveControl(objParams.get(t).get("cveControl")!=null?objParams.get(t).get("cveControl"):"");
				dtoGridMaster.setUsuarioUno(objParams.get(t).get("usr1")!=null?Integer.parseInt(objParams.get(t).get("usr1")):0);
				dtoGridMaster.setUsuarioDos(objParams.get(t).get("usr2")!=null?Integer.parseInt(objParams.get(t).get("usr2")):0);
				dtoGridMaster.setUsuarioTres(objParams.get(t).get("usr3")!=null?Integer.parseInt(objParams.get(t).get("usr3")):0);
				dtoGridMaster.setNivelAutorizacion(objParams.get(t).get("nivelAut")!=null?Integer.parseInt(objParams.get(t).get("nivelAut")):0);
				dtoGridMaster.setCupoTotal(objParams.get(t).get("cupoTotal")!=null?Double.parseDouble(objParams.get(t).get("cupoTotal")):0);
				//dtoGridMaster.setIdUsuario(objParams.get(t).get("idUsuario")!=null?Integer.parseInt(objParams.get(t).get("idUsuario")):0);
				dtoGridMaster.setIdUsuario(GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
				dtoGridMaster.setPsw(objParams.get(t).get("psw")!=null?objParams.get(t).get("psw"):"");
			}
			
			result = consultaPropuestasService.autorizarModificaciones(dtoGridMaster);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasAction, M:autorizar");
		}
		return result;
	}
	
	//revisado
	@DirectMethod
	public String eliminarProp(String cveControl,int noEmpresa) {
		String result = "";
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51)){
			return result;
		}
		
		try {
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			result = consultaPropuestasService.eliminarProp(cveControl,noEmpresa);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasAction, M:eliminarProp");
		}
		return result;
	}
	
	//revisado
	@DirectMethod
	public String modificarProp(String gridProp, String gridDetProp) {
		
		String result = "";
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51)){
			return result;
		}
		
		Gson gson = new Gson();
		List<Map<String, String>> mapGridProp = gson.fromJson(gridProp, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<Map<String, String>> mapGridDetProp = gson.fromJson(gridDetProp, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<MovimientoDto> listDetProp = new ArrayList<MovimientoDto>();
		SeleccionAutomaticaGrupoDto dtoGridProp = new SeleccionAutomaticaGrupoDto();
		int t;
		
		try {
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			
			for(t=0; t<mapGridProp.size(); t++) {
				dtoGridProp.setCveControl(mapGridProp.get(t).get("cveControl") != null ? mapGridProp.get(t).get("cveControl") : "");
				dtoGridProp.setUsuarioUno(mapGridProp.get(t).get("usr1") != null ? Integer.parseInt(mapGridProp.get(t).get("usr1")) : 0);
				dtoGridProp.setUsuarioDos(mapGridProp.get(t).get("usr2") != null ? Integer.parseInt(mapGridProp.get(t).get("usr2")) : 0);
				dtoGridProp.setUsuarioTres(mapGridProp.get(t).get("usr3") != null ? Integer.parseInt(mapGridProp.get(t).get("usr3")) : 0);
				dtoGridProp.setNumIntercos(mapGridProp.get(t).get("numIntercos") != null ? Integer.parseInt(mapGridProp.get(t).get("numIntercos")) : 0);
			}
			for(t=0; t<mapGridDetProp.size(); t++) {
				MovimientoDto dtoGridDetProp = new MovimientoDto();
				dtoGridDetProp.setNoEmpresa(mapGridDetProp.get(t).get("noEmpresa") != null ? Integer.parseInt(mapGridDetProp.get(t).get("noEmpresa")) : 0);
				dtoGridDetProp.setIdDivisa(mapGridDetProp.get(t).get("idDivisa") != null ? mapGridDetProp.get(t).get("idDivisa") : "");
				dtoGridDetProp.setIdBancoPago(mapGridDetProp.get(t).get("bancoPago") != null ? Integer.parseInt(mapGridDetProp.get(t).get("bancoPago")) : 0);
				dtoGridDetProp.setIdBancoBenef(mapGridDetProp.get(t).get("idBancoBenef") != null ? Integer.parseInt(mapGridDetProp.get(t).get("idBancoBenef")) : 0);
				dtoGridDetProp.setNoCliente(mapGridDetProp.get(t).get("noCliente") != null ? mapGridDetProp.get(t).get("noCliente") : "");
				listDetProp.add(dtoGridDetProp);
			}
			result = consultaPropuestasService.modificarProp(dtoGridProp, listDetProp);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasAction, M:modificarProp");
		}
		return result;
	}
	
	//Creado EMS 180915
	@DirectMethod
	public String validaModPropMaestro(String gridProp) {
		
		String result = "";
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51)){
			return result;
		}
		
		Gson gson = new Gson();
		List<Map<String, String>> mapGridProp = gson.fromJson(gridProp, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		SeleccionAutomaticaGrupoDto matDtoProp[] = new SeleccionAutomaticaGrupoDto[mapGridProp.size()];
		
		int t;
		
		try {
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			
			for(t=0; t<mapGridProp.size(); t++) {
				SeleccionAutomaticaGrupoDto dtoGridProp = new SeleccionAutomaticaGrupoDto();
				dtoGridProp.setCveControl(mapGridProp.get(t).get("cveControl") != null ? mapGridProp.get(t).get("cveControl") : "");
				dtoGridProp.setUsuarioUno(mapGridProp.get(t).get("usr1") != null ? Integer.parseInt(mapGridProp.get(t).get("usr1")) : 0);
				dtoGridProp.setUsuarioDos(mapGridProp.get(t).get("usr2") != null ? Integer.parseInt(mapGridProp.get(t).get("usr2")) : 0);
				dtoGridProp.setUsuarioTres(mapGridProp.get(t).get("usr3") != null ? Integer.parseInt(mapGridProp.get(t).get("usr3")) : 0);
				dtoGridProp.setNumIntercos(mapGridProp.get(t).get("numIntercos") != null ? Integer.parseInt(mapGridProp.get(t).get("numIntercos")) : 0);
				matDtoProp[t] = dtoGridProp;
			}
			
			for(t=0; t<matDtoProp.length; t++) {
				System.out.println(matDtoProp[t].getCveControl());
			}
			
			result = consultaPropuestasService.validaModPropMaestro(matDtoProp);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasAction, M:modificarProp");
		}
		return result;
	}
	
	//revisado
	@DirectMethod
	public String nombreUsuarios(String usr1, String usr2, String usr3) {
		
		String result = "";
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51)){
			return result;
		}
		
		try{
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			result = consultaPropuestasService.nombreUsuarios(usr1, usr2, usr3);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasAction, M:nombreUsuarios");
		}
		return result;
	}

	//revisado
	@DirectMethod
	public JRDataSource reporteDetalleCupos(Map<String, Object> datos, ServletContext context) {
		JRDataSource jrDataSource = null;
		SeleccionAutomaticaGrupoDto dtoIn= new SeleccionAutomaticaGrupoDto();
		
		try {
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl", context);
			
			dtoIn.setIdGrupoFlujo(datos.get("idGrupoEmpresa") != null ? Integer.parseInt(datos.get("idGrupoEmpresa").toString()) : 0);
			dtoIn.setIdGrupo(datos.get("idGrupoRubro") != null ? Integer.parseInt(datos.get("idGrupoRubro").toString()) : 0);
			dtoIn.setCveControl(datos.get("cveControl").toString());
			dtoIn.setUsuarioUno(datos.get("idUsuario1") != null ? Integer.parseInt(datos.get("idUsuario1").toString()) : 0);
			dtoIn.setUsuarioDos(datos.get("idUsuario2") != null ? Integer.parseInt(datos.get("idUsuario2").toString()) : 0);
			dtoIn.setUsuarioTres(datos.get("idUsuario3") != null ? Integer.parseInt(datos.get("idUsuario3").toString()) : 0);
			
			jrDataSource = consultaPropuestasService.reporteDetalleCupos(dtoIn);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasAction, M:reporteDetalleCupos");	
		}
		return jrDataSource;
	}
	
	//revisado
	@DirectMethod
	public String eliminaDetallePropuesta(String gridDatos){
		String mensaje = "";
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51)){
			return mensaje;
		}
		
		Gson gson = new Gson();
		List<Map<String, String>> objGrid = gson.fromJson(gridDatos, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		
		try{
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			mensaje = consultaPropuestasService.eliminaDetallePropuesta(objGrid);
		} catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasAction, M: eliminaDetallePropuesta");
		}return mensaje;
	}
	
	//revisado
	@DirectMethod
	public String configuraSet(int indice){
		String mensaje = "";
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51)){
			return mensaje;
		}
		
		try{
			ConsultaPropuestasService consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			mensaje = consultaPropuestasService.consultarConfiguraSet(indice);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasAction, M: configuraSet");
		}return mensaje;
	}
	
	@DirectMethod
	public JRDataSource reporteConSelecAut(Map<String, Object> datos, ServletContext context){
		JRDataSource jrDataSource = null;
		
		ParamConsultaPropuestasDto dtoIn= new ParamConsultaPropuestasDto();
		
		try {
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl", context);
			
			System.out.println(datos.get("idGrupoEmpresa"));
			System.out.println(datos.get("idUsuario"));
			System.out.println(datos.get("fecIni"));
			System.out.println(datos.get("fecFin"));
			System.out.println(datos.get("soloMisProp"));
			System.out.println(datos.get("propVigentes"));
			
			dtoIn.setGrupoEmpresa(datos.get("idGrupoEmpresa") != null ? Integer.parseInt(datos.get("idGrupoEmpresa").toString()) : 0);
			dtoIn.setIdCliente(0);
			dtoIn.setPvGrupoRubro(0);
			dtoIn.setIdUsuario(datos.get("idUsuario") != null ? Integer.parseInt(datos.get("idUsuario").toString()) : 0);
			dtoIn.setFechaIni(datos.get("fecIni") != null && !datos.get("fecIni").equals("") ? datos.get("fecIni").toString() : "");
			dtoIn.setFechaFin(datos.get("fecFin") != null && !datos.get("fecFin").equals("") ? datos.get("fecFin").toString() : "");
			dtoIn.setPvsDivision("");//Este parametro se inicializa con vacio por no mostrarse en la pantalla
			dtoIn.setSoloMisProp(datos.get("soloMisProp").equals("true") ? true : false);
			dtoIn.setSoloPropAct(datos.get("propVigentes").equals("true") ? true : false);
			
			jrDataSource = consultaPropuestasService.reporteConSelecAut(dtoIn);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasAction, M:reporteConSelecAut");	
		}
		return jrDataSource;
	}
	
	//revisado
	@DirectMethod
	public int validaFacultad(int idFacultad) {
		int res = 0;
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51)){
			return res;
		}
		
		try {
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			res = consultaPropuestasService.validaFacultad(idFacultad);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Traspasos, C: TraspasosAction, M: validaFacultad");
		}
		return res;
	}
	
	
	//revisado
	@DirectMethod
	public List<LlenaComboDivisaDto>llenarComboDivisa() {
		List<LlenaComboDivisaDto> list= new ArrayList<LlenaComboDivisaDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51)){
			return list;
		}
		
		try {
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			list = consultaPropuestasService.llenarComboDivisa();
			
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasAction, M:llenarComboDivisa");	
		}
		return list;
	}
	
	//revisado
	@DirectMethod
	public double obtenerTipoCambioD(String sDivisa, String fecHoy) {
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51)){
			return 0.00;
		}
		
		try {
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			return consultaPropuestasService.obtenerTipoCambio( sDivisa, funciones.ponerFechaDate(fecHoy));
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasAction, M:obtenerTipoCambio");	
			return 0.00;
		}
	}
	@DirectMethod
	public double obtenerTipoCambio(String sJson, String sDivisa, String fecHoy) {
		double val = 0;
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51)){
			return val;
		}
		
		Gson gson = new Gson();
		List<Map<String, String>> objDatos = gson.fromJson(sJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
		try {
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			val = consultaPropuestasService.obtenerTipoCambio(objDatos, sDivisa, funciones.ponerFechaDate(fecHoy));
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasAction, M:obtenerTipoCambio");	
		}
		return val;
	}
	
	//revisado
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboBancoPag(String sDivisa, String noEmpresa){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51)){
			return list;
		}
		
		try{
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			list = consultaPropuestasService.llenarComboBancoPag(sDivisa, noEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:llenarComboBancoPag");	
		}
		return list;
	}
	
	//Agregado EMS: 04/11/2015
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboBancoPagBenef(String sDivisa, int noPersona, String descFormaPago){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return list;
		
		
		try{
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			list = consultaPropuestasService.llenarComboBancoPagBenef(sDivisa, noPersona, descFormaPago);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:llenarComboBancoPagBenef");	
		}
		return list;
	}
	
	//revisado
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboChequeraPag(int idBanco, int noEmpresa, String sDivisa) {
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return list;
		
		try{
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			list = consultaPropuestasService.llenarComboChequeraPag(idBanco, noEmpresa, sDivisa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:llenarComboChequeraPag");	
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboChequeraPagBenef(int idBanco, int noEmpresa, String sDivisa, int idBenef, String descFormaPago) {
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return list;
		
		try{
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			list = consultaPropuestasService.llenarComboChequeraPagBenef(idBanco, noEmpresa, sDivisa, idBenef, descFormaPago);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:llenarComboChequeraPag");	
		}
		return list;
	}
	
	//revisado
	@DirectMethod
	public String aceptarModificar(String gridProp, String gridDetProp, String formaPago, 
									String idBanco, String idChequera,
									String idBancoBenef, String idChequeraBenef,
									boolean actualizaBeneficiario, String fecHoy, String idDivisa, double tipoCambio, String folios,
									String campoRef, String referencia1, String referencia2,String referencia3
									) {
		
		String resp = "";
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return resp;
		
		Gson gson = new Gson();
		List<Map<String, String>> mapGridProp = gson.fromJson(gridProp, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<Map<String, String>> mapGridDetProp = gson.fromJson(gridDetProp, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
		try {
			//System.out.println("forma pago "+formaPago);
			resp = validaDatos(mapGridProp, mapGridDetProp, formaPago, idBanco, idChequera, idBancoBenef, idChequeraBenef, actualizaBeneficiario, fecHoy, referencia1, referencia2, referencia3);
			
			if(resp.equals("")) {
				//System.out.println("entro a modificar action");
				consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
				resp = consultaPropuestasService.aceptarModificar(mapGridProp, mapGridDetProp, formaPago.equals("") ? 0 : Integer.parseInt(formaPago),
								idBanco.equals("") ? 0 : Integer.parseInt(idBanco), idChequera,
								idBancoBenef.equals("") ? 0 : Integer.parseInt(idBancoBenef), idChequeraBenef,
								actualizaBeneficiario ,fecHoy, idDivisa, tipoCambio, folios, campoRef, referencia1, referencia2,referencia3
								);
			}
		}catch(Exception e) {
			e.printStackTrace();
			logger.error(e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasAction, M:aceptarModificar");	
		}
		return resp;
	}
	
	//revisado
	public String validaDatos(List<Map<String, String>> mapGridProp, List<Map<String, String>> mapGridDetProp, String formaPago,
								 String idBanco, String idChequera,
								 String idBancoBenef, String idChequeraBenef, 
								 boolean actualizaBeneficiario, String fecHoy, String referencia1, String referencia2, String referencia3) {
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return "";
		
		try {
			
			//if(formaPago.equals("") && idBanco.equals("") && idChequera.equals(""))
				//if(!actualizaBeneficiario)
					//return "Seleccione una forma de pago o un banco y una chequera";
			
			if(actualizaBeneficiario){
				if(idBancoBenef.equals("") && idChequeraBenef.equals(""))
					return "Seleccione un banco y una chequera de beneficiario";
			}
			
			/*if(!fecPago.equals("")) {
				if(funciones.ponerFechaDate(funciones.cambiarFecha(fecPago)).compareTo(funciones.ponerFechaDate(fecHoy)) < 0)
					return "La fecha popuesta no puede ser menor a hoy";
			}*/
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Egresos, C:ConsultaPropuestasAction, M:validaDatos");
			return e.toString();
		}
		return "";
	}
	//revisado
	@DirectMethod
	public List<MovimientoDto> consultarDetPagos(int noCliente, String fecIni, String fecFin, int iUserId){
		List<MovimientoDto> resp = new ArrayList<MovimientoDto>();

		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return resp;
		
		try {
			//if(fecInicial.compareTo(fecFinal) > 0) return "La fecha inicial, no puede ser mayor que la fecha final!!";
			
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			resp = consultaPropuestasService.consultarDetPagos(noCliente, fecIni, fecFin);
		}
		catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasAction, M:consultarDetPagos");	
		}
		return resp;
	}
	
	/** 
	 * Se este llamando en un js incorrecto, PropuestaPagoManual
	@DirectMethod
	public List<GrupoDTO> llenarComboGrupo(String idTipoGrupo, int noEmpresa){
		List<GrupoDTO> grupo = null;
		
		consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
				
		try{
			grupo = consultaPropuestasService.llenarComboGrupo(idTipoGrupo, noEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasAction, M:llenarComboGrupo");
		}
		return grupo;
	}
	@DirectMethod
	public List<RubroDTO> llenarComboRubros(int idGrupo){
		
		System.out.println("Entra = " + idGrupo);
				
		List<RubroDTO> rubros = null;
		
		consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
				
		try{
			rubros = consultaPropuestasService.llenarComboRubros(idGrupo);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasAction, M:llenarComboGrupo");
		}
		return rubros;
	}
	
	@SuppressWarnings("unchecked")
	@DirectMethod
	public Map insertarFlujoMovtos(String datos){
		Map<String, Object> mapRet = new HashMap<String, Object>();
		Gson gson = new Gson();

		try{
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			List<Map<String, String>> LisMovtos = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			
			mapRet = consultaPropuestasService.insertarFlujoMovtos(LisMovtos);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Egresos, C:PropuestaPagoManualAction, M:insertarFlujoMovtos");
		}	
		return mapRet;
	}
	*/
	
	@DirectMethod
	public String exportaExcelPropuestas(String datos) {
		String resp = "";
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return resp;
		
		try {
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			resp = consultaPropuestasService.exportaExcelPropuestas(datos);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Reportes, C:ConsultaPropuestasAction, M:exportaExcel");
		}
		return resp;
	}
	
	
	@DirectMethod
	public HSSFWorkbook obtenerExcel(String nombre) {
		FileInputStream file = null;
		HSSFWorkbook workbook = null;
		File arch = new File(nombre);
		
		try {
			file = new FileInputStream(arch);
			workbook = new HSSFWorkbook(file);
			file.close();
		} catch (FileNotFoundException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:obtenerExcel");
		} catch (IOException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:obtenerExcel");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:obtenerExcel");
		}
		arch.delete();
		return workbook;
	}
	
	//Agregado 30/03/2016 - Excel de detalles en base a consulta, se cambia por el barrido de grid 
	//ya que se agreg� la paginaci�n al grid de detalles.
	@DirectMethod
	public HSSFWorkbook obtenerExcelDetalles(String idUsuario, ServletContext context) {
		HSSFWorkbook wb=null;
		try {
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl",context);
			wb = consultaPropuestasService.obtenerDetallesReporte(Integer.parseInt(idUsuario)); 
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ " P:Egresos, C:ConsultaPropuestasAction, M:obtenerExcelDetalles");
		}
		return wb;
	}
	
	//Agregado EMS 170915.
	@DirectMethod
	public String exportaExcel(String datos) {
		String resp = "";
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return resp;
		
		try {
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			resp = consultaPropuestasService.exportaExcel(datos);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Reportes, C:ConsultaPropuestasAction, M:exportaExcel");
		}
		return resp;
	}
	
	
	@DirectMethod
	public String exportaExcelPropDet(String datos) {
		String resp = "";
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return resp;
		
		try {
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			resp = consultaPropuestasService.exportaExcelPropDet(datos);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Reportes, C:ConsultaPropuestasAction, M:exportaExcel");
		}
		return resp;
	}
	//Agregado EMS 26/01/2016 - Para barrido de grid
	@DirectMethod
	public String exportaExcelDetalles(String datos) {
		String resp = "";
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return resp;
		
		try {
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			resp = consultaPropuestasService.exportaExcelDetalles(datos);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Reportes, C:ConsultaPropuestasAction, M:exportaExcelDetalles");
		}
		return resp;
	}
	
	
	@DirectMethod
	public String exportaExcelDetallesC() {
		String resp = "";
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return resp;
		
		try {
			resp = ""+ GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario();
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Reportes, C:ConsultaPropuestasAction, M:exportaExcelDetallesC");
		}
		return resp;
	}
	
	//Agregado EMS 21/09/15
	@DirectMethod
	public String updateFecProp(String gridProp, String fechaActualiza) {
		
		String result = "";
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return result;
		
		Gson gson = new Gson();
		List<Map<String, String>> mapGridProp = gson.fromJson(gridProp, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		SeleccionAutomaticaGrupoDto matProp[] = new SeleccionAutomaticaGrupoDto[mapGridProp.size()];
		int t;
		
		try {
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			
			for(t=0; t<mapGridProp.size(); t++) {
				SeleccionAutomaticaGrupoDto dtoGridProp = new SeleccionAutomaticaGrupoDto();
				dtoGridProp.setCveControl(mapGridProp.get(t).get("cveControl") != null ? mapGridProp.get(t).get("cveControl") : "");
				matProp[t]=dtoGridProp;
			}
			
			result = consultaPropuestasService.updateFecProp(matProp, fechaActualiza);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasAction, M:modificarProp");
		}
		return result;
	}
	
	//Agregado EMS 23/09/15: Carga los proveedores con pagos cruzados.
	@DirectMethod
	public List<BloqueoPagoCruzadoDto>consultaPagosCruzados(String noPersona){

		List<BloqueoPagoCruzadoDto>list= new ArrayList<BloqueoPagoCruzadoDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return list;
		
		try{
			consultaPropuestasService =(ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			list = consultaPropuestasService.consultaPagosCruzados(noPersona);
			 
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:consultaPagosCruzados");	
		}
		return list;
	}
	
	//Agregado EMS: 24/09/15
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboBenefPagoCruzado(String noPersona){

		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return list;
		
		try{
			consultaPropuestasService =(ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			list = consultaPropuestasService.llenarComboBenefPagoCruzado(noPersona);
			 
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:llenarComboBenefPagoCruzado");	
		}
		return list;
	}
	
	//Agregado EMS: 25/09/15 para carga de divisas en pagos cruzados
	@DirectMethod
	public List<LlenaComboDivisaDto> obtenerDivisas(){
		List<LlenaComboDivisaDto>list=new ArrayList<LlenaComboDivisaDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return list;
		
		try{
			ConsultaPropuestasService consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			list = consultaPropuestasService.obtenerDivisas();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:obtenerDivisas");			
		}
		return list;
	}
	
	//Agregado EMS: 25/09/15
	@DirectMethod
	public String insertaNuevoProvPagoCruzado(String data){
		
		if(data.equals("")){
			return "�Datos invalidos!";
		}
		
		String result = "";
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return result;
		
		Gson gson = new Gson();
		List<Map<String, String>> mapGridProp = gson.fromJson(data, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		BloqueoPagoCruzadoDto pagoCruzado = new BloqueoPagoCruzadoDto();
		
		try {
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			pagoCruzado.setProveedor(mapGridProp.get(0).get("noProv") != null ? mapGridProp.get(0).get("noProv") : "");
			pagoCruzado.setDivisaOrig(mapGridProp.get(0).get("divOriginal") != null ? mapGridProp.get(0).get("divOriginal") : "");
			pagoCruzado.setDivisaPago(mapGridProp.get(0).get("divPago") != null ? mapGridProp.get(0).get("divPago") : "");
			
			result = consultaPropuestasService.insertaNuevoProvPagoCruzado(pagoCruzado);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasAction, M:insertaNuevoProvPagoCruzado");
		}
		
		return result;
	}
	
	@DirectMethod
	public String eliminarProvPagoCruzado(String noProv, String divOriginal){
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return "";
		
		consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
		return consultaPropuestasService.eliminarProvPagoCruzado(noProv, divOriginal);
	}
	
	//agregado EMS: 28/09/15
	@DirectMethod
	public Map<String, Object> buscarBloqueo(String noEmpresa, String noProv, String noDocto, String periodo){
		
		Map<String, Object> datos = new HashMap<String, Object>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return datos;
		
		try {
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			datos = consultaPropuestasService.buscarBloqueo(noEmpresa, noProv, noDocto, periodo);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasAcion, M:buscarBloqueo");
		}
		return datos;
	}
	
	//Agregado EMS 30/09/15
	@DirectMethod
	public Map<String, Object> insertarBloqueo(String noEmpresa, String noProv, String noDocto, String motivo){
		
		Map<String, Object> datos = new HashMap<String, Object>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return datos;
		
		try {
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			datos = consultaPropuestasService.insertarBloqueo(noEmpresa, noProv, noDocto, motivo, 
																	GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasAcion, M:insertarBloqueo");
		}
		return datos;
	}
	
	//Agregado EMS 30/09/15
	@DirectMethod
	public Map<String, Object> eliminarBloqueo(String noEmpresa, String noProv, String noDocto){
		
		Map<String, Object> datos = new HashMap<String, Object>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return datos;
		
		try {
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			datos = consultaPropuestasService.eliminarBloqueo(noEmpresa, noProv, noDocto);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasAcion, M:eliminarBloqueo");
		}
		return datos;
	}
	
	//Agregado EMS 30/09/15
	@DirectMethod
	public HSSFWorkbook datosExcelBloqueados(String nombre, Map<String, String> params, ServletContext context){
		
		@SuppressWarnings("resource")
		HSSFWorkbook workbook = new HSSFWorkbook();
		
		try {
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl", context);
			workbook = consultaPropuestasService.datosExcelBloqueados(nombre, params);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasAcion, M:datosExcelBloqueados");
		}
		
		try{
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return workbook;
	}

	
	//Agregado EMS 01/10/15
		@DirectMethod
		public List<BloqueoPagoCruzadoDto>consultaProveedoresBloqueados(String empresas, String propuestas){

			List<BloqueoPagoCruzadoDto>list= new ArrayList<BloqueoPagoCruzadoDto>();
			if(!Utilerias.haveSession(WebContextManager.get())
					|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
				return list;
			
			try
			{
				System.out.println("Empresas/propuestas "+empresas+" "+propuestas);
				consultaPropuestasService =(ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
				list = consultaPropuestasService.consultaProveedoresBloqueados(empresas, propuestas);
				 
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:Egresos, C:ConsultaPropuestasAction, M:consultaProveedoresBloqueados");	
			}
			return list;
		}
		
		@DirectMethod
		public List<LlenaComboGralDto> llenarComboPeriodos(){
			List<LlenaComboGralDto> listRet= new ArrayList<LlenaComboGralDto>();
			
			if(!Utilerias.haveSession(WebContextManager.get())
					|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
				return listRet;
			
			LlenaComboGralDto dtoIn = new LlenaComboGralDto();
			try{
				consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
				listRet = consultaPropuestasService.llenarComboPeriodos(dtoIn);
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:Egresos, C:ConsultaPropuestasAction, M:llenarComboPeriodos");	
			}
			return listRet;
		}
		
		@DirectMethod
		public List<LlenaComboGralDto> llenarComboReglaNegocio(String tipoRegla){
			List<LlenaComboGralDto> listRet= new ArrayList<LlenaComboGralDto>();
			
			if(!Utilerias.haveSession(WebContextManager.get())
					|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
				return listRet;
			
			LlenaComboGralDto dtoIn = new LlenaComboGralDto();
			
			dtoIn.setTabla("REGLA_NEGOCIO");
			dtoIn.setCampoUno("id_regla");
			dtoIn.setCampoDos("regla_negocio");
			dtoIn.setOrden("regla_negocio");
			dtoIn.setCondicion(tipoRegla);
			
			try{
				consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
				listRet = consultaPropuestasService.llenarComboReglaNegocio(dtoIn);
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:Egresos, C:ConsultaPropuestasAction, M:llenarComboReglaNegocio");	
			}
			return listRet;
		}
		
		//Agregado EMS 10/11/2015
		@DirectMethod
		public Map<String, String>validaCvePropuesta(String cveControl){
			Map<String, String> resultado = new HashMap<>();
			resultado.put("error", "");
			resultado.put("mensaje", "");
			
			if(!Utilerias.haveSession(WebContextManager.get())
					|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
				return resultado;
			
			try{
				consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
				resultado = consultaPropuestasService.validaCvePropuesta(cveControl);
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:Egresos, C:ConsultaPropuestasAction, M:validaCvePropuesta");	
			}
			return resultado;
		}
		
		//Agregado EMS 10/11/2015
		@DirectMethod
		public Map<String, String>insertaSubPropuesta(double montoMaximo, String nvaCveControl, String oldCveControl, 
													   String fecha){
			Map<String, String> resultado = new HashMap<>();
			resultado.put("error", "");
			resultado.put("mensaje", "");
			
			if(!Utilerias.haveSession(WebContextManager.get())
					|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
				return resultado;
			
			try{
				consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
				resultado = consultaPropuestasService.insertaSubPropuesta(montoMaximo, nvaCveControl,oldCveControl,
																GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario(),
																fecha);
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:Egresos, C:ConsultaPropuestasAction, M:insertaSubPropuesta");	
			}
			return resultado;
		}
		
		//Agregado EMS 10/11/2015
		@DirectMethod
		public Map<String, String>actualizaMontoPropuesta(double montoMaximo, String cveControl, String stencero){
			
			Map<String, String> resultado = new HashMap<>();
			resultado.put("error", "");
			resultado.put("mensaje", "");
			
			if(!Utilerias.haveSession(WebContextManager.get())
					|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
				return resultado;
			
			try{
				consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
				resultado = consultaPropuestasService.actualizaMontoPropuesta(montoMaximo, cveControl, stencero);
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:Egresos, C:ConsultaPropuestasAction, M:insertaSubPropuesta");	
			}
			return resultado;
		}
		
		//Agregado EMS 10/11/2015
		@DirectMethod
		public Map<String, String>actualizaPropuesta(String nvaCveControl, String noDoctos, String oldCveControl, String fecha){
			
			Map<String, String> resultado = new HashMap<>();
			resultado.put("error", "");
			resultado.put("mensaje", "");
			
			if(!Utilerias.haveSession(WebContextManager.get())
					|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
				return resultado;
			
			try{
				consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
				resultado = consultaPropuestasService.actualizaPropuesta(nvaCveControl, noDoctos, oldCveControl, fecha);
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:Egresos, C:ConsultaPropuestasAction, M:insertaSubPropuesta");	
			}
			return resultado;
		}
		
		@DirectMethod
		public List<LlenaComboGralDto> llenarComboGrupoEmpresaUnica(String idEmpresa){
			GrupoEmpresasDto dtoIn= new GrupoEmpresasDto();
			List<LlenaComboGralDto> listRet= new ArrayList<LlenaComboGralDto>();
			
			if(!Utilerias.haveSession(WebContextManager.get())
					|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
				return listRet;
			
			try{
				consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
				dtoIn.setIdUsuario(""+GlobalSingleton.getInstancia().getUsuarioLoginDto().getClaveUsuario());
				
				if(idEmpresa == null || idEmpresa.equals("") || idEmpresa.equals("0")){
					return listRet;
				}else{
					try{
						dtoIn.setIdEmpresa(Integer.parseInt(idEmpresa));
					}catch(NumberFormatException nfe){
						return listRet; //Retornara nulo.
					}
				}
				
				listRet = consultaPropuestasService.llenarComboGrupoEmpresaUnica(dtoIn);

			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:Egresos, C:ConsultaPropuestasAction, M:llenarComboGrupoEmpresasUnica");	
			}
			return listRet;
		}

		@DirectMethod
		public Map<String, String> insertaBitacoraPropuesta(String operacion, String propuestas, String valorAnterior, String valorNuevo){
			
			System.out.println(operacion);
			Map<String, String> resultado = new HashMap<>();
			resultado.put("error", "");
			resultado.put("mensaje", "");
			System.out.println("inserta en bitacora action "+operacion);
			if(!Utilerias.haveSession(WebContextManager.get())
					|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
				return resultado;
			
			int usuario = GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario();
			
			if(usuario <= 0 ){
				resultado.put("error", "Usuario no valido");
				return resultado;
			}
			
			if(operacion.equals("")){
				resultado.put("error", "Operacion no valida");
				return resultado;
			}
			
			if(propuestas.equals("") && (!operacion.equals("bloqueoProv") && !operacion.equals("desbloqueoProv") && !operacion.equals("nvoPagCruz") && !operacion.equals("elimPagCruz"))){
				resultado.put("error", "Propuestas no validas");
				return resultado;
			}
			
			Gson gson = new Gson();
			List<Map<String, String>> mProps = new ArrayList<>();
			
			//Propuestas
			if(!operacion.equals("subpropuesta") && !operacion.equals("bloqueoProv") && !operacion.equals("bloqueoProv") && !operacion.equals("desbloqueoProv") && !operacion.equals("nvoPagCruz") && !operacion.equals("elimPagCruz")){
				mProps = gson.fromJson(propuestas, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			}
			
			List<BitacoraPropuestasDto> listBitacora = new ArrayList<>();
			
			try {
				consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
				
				for(int i=0; i<mProps.size(); i++) {
					BitacoraPropuestasDto bitacoraP = new BitacoraPropuestasDto();
					bitacoraP.setUsuario(usuario);
					bitacoraP.setOperacion(operacion);
					bitacoraP.setPropuesta(mProps.get(i).get("cveControl")!= null ? mProps.get(i).get("cveControl"):"");
					
					if(operacion.equals("AUT") || operacion.equals("DAUT")){
						String usr1 = mProps.get(i).get("usr1")!= null ? mProps.get(i).get("usr1"):"";
						String usr2 = mProps.get(i).get("usr2")!= null ? mProps.get(i).get("usr2"):"";
						bitacoraP.setValorAnt("usr1:"+usr1+",usr2:"+usr2);
						bitacoraP.setValorNuevo(""+usuario);
					}else if(operacion.equalsIgnoreCase("ModDet")){
						
						List<Map<String, String>> mDetProps = gson.fromJson(valorAnterior, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
						String concat = "DOCTO:"+ (mDetProps.get(0).get("noDocto") != null ? mDetProps.get(0).get("noDocto"): "");
						concat += "FP:"+ (mDetProps.get(0).get("formaPago") != null ? mDetProps.get(0).get("formaPago"): ""); 
						concat += ",B:"+ (mDetProps.get(0).get("bancoPago") != null ? mDetProps.get(0).get("bancoPago"): "0");
						concat += ",C:"+ (mDetProps.get(0).get("idChequera") != null ? mDetProps.get(0).get("idChequera"): "0"); 
						concat += ",BB:"+ (mDetProps.get(0).get("idBancoBenef") != null ? mDetProps.get(0).get("idBancoBenef"): "0");
						concat += ",CB:"+ (mDetProps.get(0).get("idChequeraBenef") != null ? mDetProps.get(0).get("idChequeraBenef"): "0");
						
						bitacoraP.setValorAnt(concat);
						bitacoraP.setValorNuevo(valorNuevo);
					}else if(operacion.equals("ModProp")){
						bitacoraP.setValorAnt(mProps.get(i).get("fechaProp") != null ? mProps.get(i).get("fechaProp"): "");
						bitacoraP.setValorNuevo(valorNuevo);
					}else if(operacion.equals("ElimDetProp")){
						bitacoraP.setValorAnt("folioDet:" + mProps.get(i).get("noFolioDet") != null ? mProps.get(i).get("noFolioDet"): "");
						bitacoraP.setValorNuevo(" ");
					}else{
						bitacoraP.setValorAnt(valorAnterior);
						bitacoraP.setValorNuevo(valorNuevo);
					}
					
					
					listBitacora.add(bitacoraP);
				}
				
				if(operacion.equals("subpropuesta") || operacion.equals("bloqueoProv") || operacion.equals("desbloqueoProv") || operacion.equals("nvoPagCruz") || operacion.equals("elimPagCruz")){
					BitacoraPropuestasDto bitacoraP = new BitacoraPropuestasDto();
					bitacoraP.setUsuario(usuario);
					bitacoraP.setOperacion(operacion);
					bitacoraP.setPropuesta(propuestas);
					bitacoraP.setValorAnt(valorAnterior);
					bitacoraP.setValorNuevo(valorNuevo);
					listBitacora.add(bitacoraP);
				}
				
				resultado = consultaPropuestasService.insertaBitacoraPropuesta(listBitacora);
				
				
			}catch(Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasAction, M:insertarBitacoraPropuesta");
			}
			
			return resultado;
		}
		
		//Agregado 27/11/2015 : Para las nuevas facultades por usuario.
		@DirectMethod
		public int validaFacultadUsuario(String facultad) {
			int res = 0;
			System.out.println("Facultad "+facultad);
			if(!Utilerias.haveSession(WebContextManager.get())
					|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
				return res;
			
			try {
				consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
				res = consultaPropuestasService.validaFacultadUsuario(facultad);
			}catch(Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Traspasos, C: TraspasosAction, M: validaFacultadUsuario");
			}
			return res;
		}
		
		//Agregado EMS 06/01/2016 - validar que existan las chequeras benef de los detalles
		@DirectMethod
		public List<MovimientoDto> existeChequeraBenef(String gridDetProp) {
			
			List<MovimientoDto> result = new ArrayList<>();
			
			if(!Utilerias.haveSession(WebContextManager.get())
					|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
				return result;
			
			int t;
			Gson gson = new Gson();
			List<Map<String, String>> mapGridProp = gson.fromJson(gridDetProp, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			List<MovimientoDto> lmovimientos = new ArrayList<>();
			//MovimientoDto matDtoProp[] = new MovimientoDto[mapGridProp.size()];
			
			try {
				consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
				
				for(t=0; t<mapGridProp.size(); t++) {
					MovimientoDto movimiento = new MovimientoDto();
					//movimiento.setCveControl(cveControl);
					movimiento.setNoDocto(mapGridProp.get(t).get("noDocto") != null ? mapGridProp.get(t).get("noDocto") : "");
					movimiento.setIdChequeraBenef(mapGridProp.get(t).get("idChequeraBenef") != null ? mapGridProp.get(t).get("idChequeraBenef") : "");
					movimiento.setNoCliente(mapGridProp.get(t).get("noPersona") != null ? mapGridProp.get(t).get("noPersona") : "");
					//matDtoProp[t] = movimiento;
					lmovimientos.add(movimiento);
				}
				
				result = consultaPropuestasService.existeChequeraBenef(lmovimientos);
				
			}catch(Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasAction, M:existeChequeraBenef");
			}
			return result;
		}
		
		@DirectMethod
		public List<LlenaComboEmpresasDto>llenarComboEmpresa(){
			
			List<LlenaComboEmpresasDto> listEmp = new ArrayList<LlenaComboEmpresasDto>();
			
			if(!Utilerias.haveSession(WebContextManager.get())
					|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
				return listEmp;
			
			try{
				consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
				listEmp = consultaPropuestasService.llenarComboEmpresa(GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:Egresos, C:ConsultaPropuestasAction, M:llenarComboEmpresa");
			}
			return listEmp;
		}
		
		//Agregado EMS 07/01/2016 - Metodo que v�lida que los documentos para autorizar tengan su banco y chequera pagadora.
		//En caso de las forma pago = 3 verificar tambien que tengan su banco y chequera benef.
		@DirectMethod
		public Map<String, Object>validarBancoChequera(String jsonPropuestas){
			
			Map<String, Object> resultado = new HashMap<>();
			resultado.put("error", "");
			resultado.put("lista", "");
			
			if(!Utilerias.haveSession(WebContextManager.get())
					|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
				return resultado;
			
			if(jsonPropuestas.equals("")) return resultado;
			
			Gson gson = new Gson();
			List<Map<String, String>> objParams = gson.fromJson(jsonPropuestas, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			
			List<SeleccionAutomaticaGrupoDto> listSeleccion = new ArrayList<>();
			
			try{
				System.out.println("Propuestas A"+jsonPropuestas);
				consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
				
				for(int t=0; t<objParams.size(); t++) {	
					SeleccionAutomaticaGrupoDto dtoGridMaster= new SeleccionAutomaticaGrupoDto();					
					dtoGridMaster.setCveControl(objParams.get(t).get("cveControl")!=null?objParams.get(t).get("cveControl"):"");
					listSeleccion.add(dtoGridMaster);
				}
				
				if(listSeleccion.size() > 0){
					resultado = consultaPropuestasService.validarBancoChequera(listSeleccion);
				}
				
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:Egresos, C:ConsultaPropuestasAction, M:validarBancoChequera");	
			}
			return resultado;
		}
		
	@DirectMethod
	public Map<String, String> actualizaMultisociedad(String cveControl, String jsonDetalles, String jsonDatos ){
		
		Map<String, String> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("msgUsuario", "");
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return resultado;
		
		if(cveControl.equals("")){
			resultado.put("error", "No se ha seleccionado propuesta.");
			return resultado;
		}
		
		Gson gson = new Gson();
		List<Map<String, String>> mapDetalles = gson.fromJson(jsonDetalles, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<Map<String, String>> mapDatos = gson.fromJson(jsonDatos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
		StringBuffer folios = new StringBuffer();
		MovimientoDto movDatos = new MovimientoDto();
		
		try{
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			
			
			for(int i=0; i<mapDetalles.size();i++){
				folios.append("'"+
						(mapDetalles.get(i).get("noFolioDet") != null ? mapDetalles.get(i).get("noFolioDet"): "0")
							+ "',");
			}
			
			if(!folios.toString().equals("")){
				//Eliminamos la ultima ,
				folios.delete(folios.length()-1, folios.length());
			}
			
			if(mapDatos.size() >0){
				movDatos.setNoEmpresa(
							Integer.parseInt(
									mapDatos.get(0).get("noEmpresa") != null ? mapDatos.get(0).get("noEmpresa"): "0"));
				movDatos.setIdBanco(
							Integer.parseInt(
									mapDatos.get(0).get("idBanco") != null ? mapDatos.get(0).get("idBanco"): "0"));
				movDatos.setIdChequera(
							mapDatos.get(0).get("idChequera") != null ? mapDatos.get(0).get("idChequera"): "");
			}
			
			
			resultado = consultaPropuestasService.actualizaMultisociedad(cveControl, folios, movDatos);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:actualizaMultisociedad");	
		}
		
		return resultado;
		
	}
		
	
	@DirectMethod
	public List<ColoresBitacoraDto> obtenerColoresPropuesta(){
		
		List<ColoresBitacoraDto> result = new ArrayList<>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return result;
		
		try{
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			
			result = consultaPropuestasService.obtenerColoresPropuesta();
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:obtenerColoresPropuestas");	
		}
		
		return result;
	}
	
	@DirectMethod
	public List<ColoresBitacoraDto> obtenerColoresDetalle(){
		
		List<ColoresBitacoraDto> result = new ArrayList<>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return result;
		
		try{
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			
			result = consultaPropuestasService.obtenerColoresDetalle();
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:obtenerColoresDetalle");	
		}
		
		return result;
	}

	@DirectMethod
	public Map<String, String> eliminarPropuesta(String jsonClaves){		
		Map<String, String> result = new HashMap<>();
		
		if(!Utilerias.haveSession(WebContextManager.get())) {
			result.put("error", "0");
			result.put("mensaje", "Sesi?nvalida");			
			return result;
		}	
		
		Gson gson = new Gson();
		List<String> ListaCveControl = gson.fromJson(jsonClaves, new TypeToken<ArrayList<String>>() {}.getType());		
		try{
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");			
			result = consultaPropuestasService.eliminarPropuesta(ListaCveControl);			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:eliminarPropuesta");	
		}
		return result;
	}
	
	@DirectMethod
	public Map<String, String> actualizaCompraTransfer(String jsonClaves, String operacion){
		
		Map<String, String> result = new HashMap<>();
		result.put("error", "");
		result.put("mensaje", "");
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return result;
		
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(jsonClaves, 
											  		new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<SeleccionAutomaticaGrupoDto> listSag = new ArrayList<>();
		
		
		for(int t=0; t<objParams.size(); t++) {
			SeleccionAutomaticaGrupoDto sag= new SeleccionAutomaticaGrupoDto();
			sag.setCveControl(objParams.get(t).get("cveControl")!=null?objParams.get(t).get("cveControl"):"");
			listSag.add(sag);
		}
		
		try{
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			
			result = consultaPropuestasService.actualizaCompraTransfer(listSag, operacion);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:actualizaCompraTransfer");	
		}
		
		return result;
	}
	
	@DirectMethod
	public String sumaTotalDetalles(String cveControl){
		
		String result = "";
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return result;
		
		try{
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			
			result = consultaPropuestasService.sumaTotalDetalles(cveControl);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:sumaTotalDetalles");	
		}
		
		return result;
	}	
	
	

	@DirectMethod
	public Map<String, String> actualizarChequeraNoAgrupa(String cveControl, String jsonFolios){
		
		Map<String, String> result = new HashMap<>();
		result.put("error", "");
		result.put("mensaje", "");
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return result;
		
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(jsonFolios, 
											  		new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		StringBuffer folios = new StringBuffer();
		
		
		for(int t=0; t<objParams.size(); t++) {
			folios.append(objParams.get(t).get("noFolioDet")+",");
		}
		
		if(folios.length()>0)
			folios.delete(folios.length()-1, folios.length());
		
		try{
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			result = consultaPropuestasService.actualizarChequeraNoAgrupa(cveControl, folios.toString());
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:actualizarChequeraNoAgrupa");	
		}
		
		return result;
	}
	
	//Agregado EMS 02/06/2016 RF
		@DirectMethod
		public String verificaFacultadUsuario(String facultad){
			
			String result = "";
			
			if(!Utilerias.haveSession(WebContextManager.get())
					|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
				return result;
			
			try{
				consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
				result = consultaPropuestasService.verificaFacultadUsuario(facultad);
				
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:Egresos, C:ConsultaPropuestasAction, M:verificaFacultadUsuario");	
			}
			
			return result;
		}

	@DirectMethod
	public Map<String, Object> verificaChequerabenef(String idDivisa, String jsonFolios) {
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("mensage", "Ocurrio un error en la conexi�n.");
		result.put("error", 1);
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return result;		
		try{
			String[] folios = jsonFolios.replace("[", "").replace("]", "").split(",");
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			result = consultaPropuestasService.verificaChequerabenef(idDivisa, folios);			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:verificaFacultadUsuario");
			result.put("mensage", "Error en:" + e.toString());
			result.put("error", 2);
		}
		return result;
	}
	
	
	@DirectMethod
	public String obtenerDivisaChequera(String chequera){
		String result = "";
		
		if(!Utilerias.haveSession(WebContextManager.get()))
			return result;
		
		try{
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			result = consultaPropuestasService.obtenerDivisaChequera(chequera);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:obtenerDivisaChequera");	
		}
		
		return result;
	}
	@DirectMethod
	public List<SeleccionAutomaticaGrupoDto> obtenerNiveles(){
		
		List<SeleccionAutomaticaGrupoDto> listEmp = new ArrayList<SeleccionAutomaticaGrupoDto>();
		GrupoEmpresasDto dtoIn=new GrupoEmpresasDto();
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return listEmp;
		
		try{
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			dtoIn.setIdUsuario(""+GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
			listEmp = consultaPropuestasService.obtenerNiveles(dtoIn);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:llenarComboEmpresa");
		}
		return listEmp;
	}
	
	@DirectMethod
	public Map<String, Object> buscarPropuestasPagadas(String data) {
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(data, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
	  
		Map<String,Object> result= new HashMap<String,Object>();
		List<SeleccionAutomaticaGrupoDto> listaDto = new ArrayList<SeleccionAutomaticaGrupoDto>();
		
		result.put("bit","0");
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51)){
			return result;
		}
		
		try {
			System.out.println("entro a buscar propuestas pagadas action");
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			
			for(int t=0; t<objParams.size(); t++) {	//Por ahora solo lo hace una vez y despues se habilitara para que autorize varios
				SeleccionAutomaticaGrupoDto dtoGridMaster= new SeleccionAutomaticaGrupoDto();
				dtoGridMaster.setIdGrupoFlujo(objParams.get(t).get("idGrupoFlujo")!=null?Integer.parseInt(objParams.get(t).get("idGrupoFlujo")):0);
				dtoGridMaster.setIdGrupo(objParams.get(t).get("idGrupo")!=null?Integer.parseInt(objParams.get(t).get("idGrupo")):0);
				dtoGridMaster.setCveControl(objParams.get(t).get("cveControl")!=null?objParams.get(t).get("cveControl"):"");
				dtoGridMaster.setUsuarioUno(objParams.get(t).get("usr1")!=null?Integer.parseInt(objParams.get(t).get("usr1")):0);
				dtoGridMaster.setUsuarioDos(objParams.get(t).get("usr2")!=null?Integer.parseInt(objParams.get(t).get("usr2")):0);
				dtoGridMaster.setUsuarioTres(objParams.get(t).get("usr3")!=null?Integer.parseInt(objParams.get(t).get("usr3")):0);
				dtoGridMaster.setNivelAutorizacion(objParams.get(t).get("nivelAut")!=null?Integer.parseInt(objParams.get(t).get("nivelAut")):0);
				dtoGridMaster.setCupoTotal(objParams.get(t).get("cupoTotal")!=null?Double.parseDouble(objParams.get(t).get("cupoTotal")):0);
				dtoGridMaster.setIdUsuario(GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
				//dtoGridMaster.setIdUsuario(objParams.get(t).get("idUsuario")!=null?Integer.parseInt(objParams.get(t).get("idUsuario")):0);
				dtoGridMaster.setPsw(objParams.get(t).get("psw")!=null?objParams.get(t).get("psw"):"");
				listaDto.add(dtoGridMaster);
			}
			System.out.println("lista tam "+listaDto.size());
			if(listaDto.size() > 0){
			
				result = consultaPropuestasService.buscarPropuestasPagadas(listaDto);
			}
			
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasAction, M:autorizar");
		}
		return result;
	}
	
	@DirectMethod
	public JRDataSource obtenerReportePropuestasAutorizadas(Map<String, Object> datos, ServletContext context){
		JRDataSource jrDataSource = null;
		SeleccionAutomaticaGrupoDto dtoIn= new SeleccionAutomaticaGrupoDto();
		
		try{
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl",context);
			dtoIn.setIdGrupoFlujo(Integer.parseInt(datos.get("idGrupoEmpresa").toString()));
			dtoIn.setIdGrupo(Integer.parseInt(datos.get("idGrupoRubro").toString()));
			dtoIn.setCveControl(datos.get("cveControl").toString());
			dtoIn.setUsuarioUno(Integer.parseInt(datos.get("idUsuario1").toString()));
			dtoIn.setUsuarioDos(Integer.parseInt(datos.get("idUsuario2").toString()));
			dtoIn.setUsuarioTres(Integer.parseInt(datos.get("idUsuario3").toString()));
			dtoIn.setTotal(datos.get("total").toString());
			
			//System.out.println("total "+datos.get("total").toString());
//			System.out.println(datos.get("idGrupoEmpresa")+" "+datos.get("idGrupoRubro")+" "+datos.get("cveControl").toString()
//					+" "+datos.get("idUsuario1")+" "+datos.get("idUsuario2")+" "+datos.get("idUsuario3"));
			jrDataSource = consultaPropuestasService.obtenerReportePropuestasAutorizadas(dtoIn);
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:consultarDetalle");	
		}
		return jrDataSource;
	}
	
	
	@DirectMethod
	public Map<String, String> actualizaCVT( String jsonFolios){
		
		Map<String, String> result = new HashMap<>();
		result.put("error", "");
		result.put("mensaje", "");
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return result;
		
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(jsonFolios, 
											  		new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		StringBuffer folios = new StringBuffer();
		
		
		for(int t=0; t<objParams.size(); t++) {
			folios.append(objParams.get(t).get("noFolioDet")+",");
		}
		
		if(folios.length()>0)
			folios.delete(folios.length()-1, folios.length());
		
		try{
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			result = consultaPropuestasService.actualizaCVT(folios.toString());
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:actualizaCVT");	
		}
		
		return result;
	}
	
	
	
	
}
