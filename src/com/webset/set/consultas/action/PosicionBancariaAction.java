package com.webset.set.consultas.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.consultas.dto.PosicionBancariaDTO;
import com.webset.set.consultas.dto.UsuarioPlantilla;
import com.webset.set.consultas.service.PosicionBancariaService;
import com.webset.set.flujos.dto.PosicionBancariaDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.RubroRegNeg;
import com.webset.utils.tools.Utilerias;

public class PosicionBancariaAction {

	private Contexto contexto = new Contexto();
	private PosicionBancariaService posicionBancariaService;
	private Bitacora bitacora = new Bitacora();
	private Funciones funciones = new Funciones();
	
	@DirectMethod
	public List<PosicionBancariaDTO> consultarCriterioSeleccion(int idCriterio){
		
		List<PosicionBancariaDTO>list= new ArrayList<PosicionBancariaDTO>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 120))
			return list;
		
		try{
			posicionBancariaService =(PosicionBancariaService)contexto.obtenerBean("posicionBancariaBusiness");
			list = posicionBancariaService.consultarCriterioSeleccion(idCriterio);
			 
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Consultas, C:PosicionBancariaAction, M:consultarCriterioSeleccion");	
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboDivisaDto>llenarComboDivisa() {
		List<LlenaComboDivisaDto> list= new ArrayList<LlenaComboDivisaDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 120)){
			return list;
		}
		
		try {
			posicionBancariaService = (PosicionBancariaService)contexto.obtenerBean("posicionBancariaBusiness");
			list = posicionBancariaService.llenarComboDivisa();
			
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaAction, M:llenarComboDivisa");	
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboPlantillas(String pantalla) {
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 120)){
			return list;
		}
		
		try {
			posicionBancariaService = (PosicionBancariaService)contexto.obtenerBean("posicionBancariaBusiness");
			list = posicionBancariaService.llenarComboPlantillas(pantalla);
			
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaAction, M:llenarComboPlantillas");	
		}
		return list;
	}
	
	@DirectMethod
	public List<RubroRegNeg>consultarRubros() {
		List<RubroRegNeg> list= new ArrayList<RubroRegNeg>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 120)){
			return list;
		}
		
		try {
			posicionBancariaService = (PosicionBancariaService)contexto.obtenerBean("posicionBancariaBusiness");
			list = posicionBancariaService.consultarRubros();
			
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaAction, M:consultarRubros");	
		}
		return list;
	}
	@DirectMethod
	public List<PosicionBancariaDTO>consultarDetallePosicion(String jsonRubros, double importeIni, 
															double importeFin, String jsonBancos, String jsonChequeras, String jsonEmpresas,
															String divisa, String fechaIni, String fechaFin, int optConsulta, boolean bSinRubros) {
		
		List<PosicionBancariaDTO> list = new ArrayList<PosicionBancariaDTO>();
		
		List<String> listRubros = new ArrayList<String>();
		List<String> listBancos = new ArrayList<>();
		List<String> listChequeras = new ArrayList<>();
		List<String> listEmpresas = new ArrayList<String>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 120)){
			return list;
		}
		
		
		try {
			if (jsonRubros.equals("[]"))
				jsonRubros = "";
			
			if(!jsonRubros.equals("")){
				
				jsonRubros = Utilerias.validarCadenaSQL(jsonRubros);
				
				Gson gson = new Gson();
				List<Map<String, String>> objRubros = gson.fromJson(jsonRubros, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				
				for(int i=0; i<objRubros.size(); i++){
					String idRubro = objRubros.get(i).get("idRubro");
					listRubros.add(idRubro);
				}
			}
			
			if(!jsonBancos.equals("")){
				
				jsonBancos = Utilerias.validarCadenaSQL(jsonBancos);
				
				Gson gson = new Gson();
				List<Map<String, String>> objBancos = gson.fromJson(jsonBancos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				
				for(int i=0; i<objBancos.size(); i++){
					String idBanco = objBancos.get(i).get("idBanco");
					listBancos.add(idBanco);
				}
			}
			
			if(!jsonChequeras.equals("")){
				
				jsonChequeras = Utilerias.validarCadenaSQL(jsonChequeras);
				
				Gson gson = new Gson();
				List<Map<String, String>> objChequeras = gson.fromJson(jsonChequeras, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				
				for(int i=0; i<objChequeras.size(); i++){
					String idChequera = objChequeras.get(i).get("idChequera");
					listChequeras.add(idChequera);
				}
			}
			
			if(!jsonEmpresas.equals("")){
				
				jsonEmpresas = Utilerias.validarCadenaSQL(jsonEmpresas);
				
				Gson gson = new Gson();
				List<Map<String, String>> objEmpresa = gson.fromJson(jsonEmpresas, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				
				for(int i=0; i<objEmpresa.size(); i++){
					String noEmpresa = objEmpresa.get(i).get("noEmpresa");
					listEmpresas.add(noEmpresa);
				}
			}
			
			divisa = Utilerias.validarCadenaSQL(divisa);
			fechaIni = Utilerias.validarCadenaSQL(fechaIni);
			fechaFin = Utilerias.validarCadenaSQL(fechaFin);
			posicionBancariaService = (PosicionBancariaService)contexto.obtenerBean("posicionBancariaBusiness");
			list = posicionBancariaService.consultarDetallePosicion(listRubros, importeIni, importeFin, listBancos,
																	listChequeras, listEmpresas, divisa,
																	fechaIni, fechaFin, optConsulta, bSinRubros);
			
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaAction, M:consultarDetallePosicion");	
		}
		return list;
	}

	
	/*
	 * M�todo para la modificaci�n de rubros cuando se da doble click son un registro xEmp o xCta.
	 */
	@DirectMethod
	public List<PosicionBancariaDTO>consultarDetallePosicion2(String rubro, String jsonChequeras, 
															  String jsonEmpresas, String divisa, 
															  String fechaIni, String fechaFin, 
															  String consulta, String tipoMovto) {
		
		List<PosicionBancariaDTO> list = new ArrayList<PosicionBancariaDTO>();
		
		List<String> listChequeras = new ArrayList<>();
		List<String> listEmpresas = new ArrayList<String>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 120)){
			return list;
		}
		
		try {
			
			if(!jsonChequeras.equals("")){
				
				jsonChequeras = Utilerias.validarCadenaSQL(jsonChequeras);
				
				Gson gson = new Gson();
				List<Map<String, String>> objChequeras = gson.fromJson(jsonChequeras, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				
				for(int i=0; i<objChequeras.size(); i++){
					String idChequera = objChequeras.get(i).get("idChequera");
					listChequeras.add(idChequera);
				}
			}
			
			if(!jsonEmpresas.equals("")){
				
				jsonEmpresas = Utilerias.validarCadenaSQL(jsonEmpresas);
				
				Gson gson = new Gson();
				List<Map<String, String>> objEmpresa = gson.fromJson(jsonEmpresas, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				
				for(int i=0; i<objEmpresa.size(); i++){
					String noEmpresa = objEmpresa.get(i).get("noEmpresa");
					listEmpresas.add(noEmpresa);
				}
			}
			
			divisa = Utilerias.validarCadenaSQL(divisa);
			fechaIni = Utilerias.validarCadenaSQL(fechaIni);
			fechaFin = Utilerias.validarCadenaSQL(fechaFin);
			
			posicionBancariaService = (PosicionBancariaService)contexto.obtenerBean("posicionBancariaBusiness");
			list = posicionBancariaService.consultarDetallePosicion2(rubro, listChequeras, listEmpresas, divisa,
																	fechaIni, fechaFin, consulta, tipoMovto);
			
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaAction, M:consultarDetallePosicion2");	
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> obtenerGruposRubros(String tipoMovto) {
		
		List<LlenaComboGralDto> list = new ArrayList<>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 120)){
			return list;
		}
		
		try {
			posicionBancariaService = (PosicionBancariaService)contexto.obtenerBean("posicionBancariaBusiness");
			list = posicionBancariaService.obtenerGruposRubros(tipoMovto);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:PosicionBancariaAction, M:obtenerGrupoRubros");
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboRubro(String idGrupoRubro) {
		
		List<LlenaComboGralDto> list = new ArrayList<>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 120)){
			return list;
		}
		
		try {
			posicionBancariaService = (PosicionBancariaService)contexto.obtenerBean("posicionBancariaBusiness");
			list = posicionBancariaService.llenarComboRubro(idGrupoRubro);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:PosicionBancariaAction, M:llenarComboRubro");
		}
		return list;
	}
	
	
	@DirectMethod
	public Map<String, String>actualizaRubros(String noFolioDet,String idGrupo, String idRubro,
											double importe, String referencia, boolean actualizaTodo ){
		
		Map<String, String> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 120))
			return resultado;
		
		try{
			
			posicionBancariaService = (PosicionBancariaService)contexto.obtenerBean("posicionBancariaBusiness");
			resultado = posicionBancariaService.actualizaRubros(noFolioDet, idGrupo, idRubro,
																importe, referencia, actualizaTodo);
			
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Consultas, C:PosicionBancariaAction, M:actualizaRubros");	
		}
		return resultado;
	}
	
	@DirectMethod
	public String exportaExcel(String datos, String tipoConsulta) {
		String resp = "";
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 120))
			return resp;
		
		try {
			
			posicionBancariaService = (PosicionBancariaService)contexto.obtenerBean("posicionBancariaBusiness");
			resp = posicionBancariaService.exportaExcel(datos, tipoConsulta);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Consultas, C:PosicionBancariaAction, M:exportaExcel");
		}
		return resp;
	}
	
	@DirectMethod
	public String exportaExcelPosicion(String datos) {
		String resp = "";
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 120))
			return resp;
		
		try {
			
			posicionBancariaService = (PosicionBancariaService)contexto.obtenerBean("posicionBancariaBusiness");
			resp = posicionBancariaService.exportaExcelPosicion(datos);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Consultas, C:PosicionBancariaAction, M:exportaExcel");
		}
		return resp;
	}
	
	@DirectMethod
	public String exportaExcelPosicion2(String datos) {
		String resp = "";
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 120))
			return resp;
		
		try {
			
			posicionBancariaService = (PosicionBancariaService)contexto.obtenerBean("posicionBancariaBusiness");
			resp = posicionBancariaService.exportaExcelPosicion2(datos);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Consultas, C:PosicionBancariaAction, M:exportaExcel");
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
			+"P:Consultas, C:PosicionBancariaAction, M:obtenerExcel");
		} catch (IOException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Consultas, C:PosicionBancariaAction, M:obtenerExcel");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Consultas, C:PosicionBancariaAction, M:obtenerExcel");
		}
		arch.delete();
		return workbook;
	}
	
	
	@DirectMethod
	public List<PosicionBancariaDTO>consultarMovtoTes(String jsonBancos,String jsonChequeras, String jsonEmpresas,
													  String fechaIni, String fechaFin, int seleccion,
													  String divisa) {
		
		List<PosicionBancariaDTO> list = new ArrayList<PosicionBancariaDTO>();
		
		List<String> listBancos = new ArrayList<>();
		List<String> listChequeras = new ArrayList<>();
		List<String> listEmpresas = new ArrayList<String>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 120)){
			return list;
		}
		
		try {
			
			if(!jsonBancos.equals("")){
				
				jsonBancos = Utilerias.validarCadenaSQL(jsonBancos);
				
				Gson gson = new Gson();
				List<Map<String, String>> objBancos = gson.fromJson(jsonBancos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				
				for(int i=0; i<objBancos.size(); i++){
					String idBanco = objBancos.get(i).get("idBanco");
					listBancos.add(idBanco);
				}
			}
			
			if(!jsonChequeras.equals("")){
				
				jsonChequeras = Utilerias.validarCadenaSQL(jsonChequeras);
				
				Gson gson = new Gson();
				List<Map<String, String>> objChequeras = gson.fromJson(jsonChequeras, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				
				for(int i=0; i<objChequeras.size(); i++){
					String idChequera = objChequeras.get(i).get("idChequera");
					listChequeras.add(idChequera);
				}
			}
			
			if(!jsonEmpresas.equals("")){
				
				jsonEmpresas = Utilerias.validarCadenaSQL(jsonEmpresas);
				
				Gson gson = new Gson();
				List<Map<String, String>> objEmpresa = gson.fromJson(jsonEmpresas, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				
				for(int i=0; i<objEmpresa.size(); i++){
					String noEmpresa = objEmpresa.get(i).get("noEmpresa");
					listEmpresas.add(noEmpresa);
				}
			}
			
			divisa = Utilerias.validarCadenaSQL(divisa);
			fechaIni = Utilerias.validarCadenaSQL(fechaIni);
			fechaFin = Utilerias.validarCadenaSQL(fechaFin);
			
			posicionBancariaService = (PosicionBancariaService)contexto.obtenerBean("posicionBancariaBusiness");
			list = posicionBancariaService.consultarMovtoTes(listBancos,listChequeras, listEmpresas,
															 fechaIni, fechaFin, seleccion, divisa);
			
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaAction, M:consultarMovtoTes");	
		}
		return list;
	}
	
	@DirectMethod
	public List<PosicionBancariaDTO>consultarDiarioTes(String jsonBancos,String jsonChequeras, String jsonEmpresas,
													  String fechaIni, String fechaFin, int seleccion,
													  String divisa) {
		
		List<PosicionBancariaDTO> list = new ArrayList<PosicionBancariaDTO>();
		
		List<String> listBancos = new ArrayList<>();
		List<String> listChequeras = new ArrayList<>();
		List<String> listEmpresas = new ArrayList<String>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 120)){
			return list;
		}
		
		try {
			
			if(!jsonBancos.equals("")){
				
				jsonBancos = Utilerias.validarCadenaSQL(jsonBancos);
				
				Gson gson = new Gson();
				List<Map<String, String>> objBancos = gson.fromJson(jsonBancos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				
				for(int i=0; i<objBancos.size(); i++){
					String idBanco = objBancos.get(i).get("idBanco");
					listBancos.add(idBanco);
				}
			}
			
			if(!jsonChequeras.equals("")){
				
				jsonChequeras = Utilerias.validarCadenaSQL(jsonChequeras);
				
				Gson gson = new Gson();
				List<Map<String, String>> objChequeras = gson.fromJson(jsonChequeras, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				
				for(int i=0; i<objChequeras.size(); i++){
					String idChequera = objChequeras.get(i).get("idChequera");
					listChequeras.add(idChequera);
				}
			}
			
			if(!jsonEmpresas.equals("")){
				
				jsonEmpresas = Utilerias.validarCadenaSQL(jsonEmpresas);
				
				Gson gson = new Gson();
				List<Map<String, String>> objEmpresa = gson.fromJson(jsonEmpresas, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				
				for(int i=0; i<objEmpresa.size(); i++){
					String noEmpresa = objEmpresa.get(i).get("noEmpresa");
					listEmpresas.add(noEmpresa);
				}
			}
			
			divisa = Utilerias.validarCadenaSQL(divisa);
			fechaIni = Utilerias.validarCadenaSQL(fechaIni);
			fechaFin = Utilerias.validarCadenaSQL(fechaFin);
			
			posicionBancariaService = (PosicionBancariaService)contexto.obtenerBean("posicionBancariaBusiness");
			list = posicionBancariaService.consultarDiarioTes(listBancos,listChequeras, listEmpresas,
															 fechaIni, fechaFin, seleccion, divisa);
			
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaAction, M:consultarMovtoTes");	
		}
		return list;
	}
	
	@DirectMethod
	public List<PosicionBancariaDto> obtenHeadsCash(int tipoReporte, String sFecIni, String sFecFin){
		StringBuffer columns = new StringBuffer();
		StringBuffer fields = new StringBuffer();
		List<PosicionBancariaDto> listDatos = new ArrayList<PosicionBancariaDto>();
		PosicionBancariaDto dtoDatos = new PosicionBancariaDto();
		String name = "";
		String fechaGrid = "";
		int iDias = 0;
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			iDias = funciones.diasEntreFechas(funciones.ponerFechaDate(sFecIni), funciones.ponerFechaDate(sFecFin)) + 1;
			columns.append("[");
			fields.append("[");
			
			
			//Muestra el RUBRO EN EL GRID
			/*fields.append("{");
			fields.append(" name: 'rubro'},");
			
			columns.append("{");
			columns.append("header: 'RUBRO',");
			columns.append("width: 50,");
			columns.append("dataIndex: 'rubro'}, ");*/
			
			fields.append("{");
			fields.append(" name: 'descripcion'},");
			
			columns.append("{");
			columns.append("header: 'DESCRIPCI�N',");
			columns.append("width: 200,");
			columns.append("dataIndex: 'descripcion',");
			columns.append("renderer: this.colores}, ");
	        
			for(int i=0; i<iDias; i++) {
				fechaGrid = funciones.ponerDiaLetraYFecha(funciones.modificarFecha("d", i, funciones.ponerFechaDate(sFecIni)), true);
				fechaGrid = fechaGrid.substring(0, 3) + " , " + funciones.ponerFechaSola(funciones.modificarFecha("d", i, funciones.ponerFechaDate(sFecIni)));
				name = "col" + i;
				
				fields.append("{");
				fields.append(" name: '"+ name +"'}");				
				
				columns.append("{");
				columns.append("header: '"+ fechaGrid +"',");
				columns.append("width: "+ sFecIni.length() * 9.5 +",");
				columns.append("dataIndex: '"+ name +"'}");
				
				if(i != iDias-1){
					columns.append(",");
					fields.append(",");
				}
			}
			
			fields.append(",{");
			fields.append(" name: 'totales'}");
			
			columns.append(",{");
			columns.append("header: 'TOTALES',");
			columns.append("width: 100,");
			columns.append("dataIndex: 'totales'}");
			
			fields.append(",{");
			fields.append(" name: 'color'}");
			
			columns.append("]");
			fields.append("]");
			
			dtoDatos.setNomReporte(nombreReporte(tipoReporte));
			dtoDatos.setTipoPosicion(tipoReporte);
			dtoDatos.setColumnas(columns.toString());
			dtoDatos.setFields(fields.toString());
			listDatos.add(dtoDatos);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: PosicionBancariaAction, M: obtenerHeads");
		}
		return listDatos;
	}
	
	
	@DirectMethod
	public List<PosicionBancariaDTO> posicionXEmp(String jsonBancos, String jsonChequeras,
													String jsonEmpresas, String fechaIni,
													String fechaFin, int seleccion,
													String divisa, int tipoReporte) {
		
		List<PosicionBancariaDTO> listResult = new ArrayList<>();
		List<String> listBancos = new ArrayList<>();
		List<String> listChequeras = new ArrayList<>();
		List<String> listEmpresas = new ArrayList<String>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 120)){
			return listResult;
		}
		
		try {
			
			if(!jsonBancos.equals("")){
				
				jsonBancos = Utilerias.validarCadenaSQL(jsonBancos);
				
				Gson gson = new Gson();
				List<Map<String, String>> objBancos = gson.fromJson(jsonBancos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				
				for(int i=0; i<objBancos.size(); i++){
					String idBanco = objBancos.get(i).get("idBanco");
					listBancos.add(idBanco);
				}
			}
			
			if(!jsonChequeras.equals("")){
				
				jsonChequeras = Utilerias.validarCadenaSQL(jsonChequeras);
				
				Gson gson = new Gson();
				List<Map<String, String>> objChequeras = gson.fromJson(jsonChequeras, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				
				for(int i=0; i<objChequeras.size(); i++){
					String idChequera = objChequeras.get(i).get("idChequera");
					listChequeras.add(idChequera);
				}
			}
			
			if(!jsonEmpresas.equals("")){
				
				jsonEmpresas = Utilerias.validarCadenaSQL(jsonEmpresas);
				
				Gson gson = new Gson();
				List<Map<String, String>> objEmpresa = gson.fromJson(jsonEmpresas, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				
				for(int i=0; i<objEmpresa.size(); i++){
					String noEmpresa = objEmpresa.get(i).get("noEmpresa");
					listEmpresas.add(noEmpresa);
				}
			}
			
			divisa = Utilerias.validarCadenaSQL(divisa);
			fechaIni = Utilerias.validarCadenaSQL(fechaIni);
			fechaFin = Utilerias.validarCadenaSQL(fechaFin);
			
			posicionBancariaService = (PosicionBancariaService)contexto.obtenerBean("posicionBancariaBusiness");
			
			//Llena los headers de valor x empresa
			if(tipoReporte == 1){
				listResult = posicionBancariaService.posicionXEmp(listBancos,listChequeras, listEmpresas, //subrev1
						fechaIni, fechaFin, seleccion,
						divisa, tipoReporte);
			}else if(tipoReporte == 2){
				//llena los headers de valor x cuenta
				listResult = posicionBancariaService.posicionXCta(listBancos,listChequeras, listEmpresas, //subrev2
																	fechaIni, fechaFin, seleccion,
																	divisa, tipoReporte);

			}else if(tipoReporte == 3){
				//llena los headers de reporte Saldos
				listResult = posicionBancariaService.repSaldos(listBancos,listChequeras, listEmpresas, //subrev3
																	fechaIni, fechaFin, seleccion,
																	divisa, tipoReporte);

			}
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaAction, M:buscaDetalleBE");
		}
		return listResult;
	}
	
	
	@DirectMethod
	public List<HashMap<String, String>> posicionXEmp2(String jsonBancos, String jsonChequeras,
													String jsonEmpresas, String fechaIni,
													String fechaFin, int seleccion,
													String divisa, int tipoReporte) {
		
		List<HashMap<String, String>> listResult = new ArrayList<>();
		List<String> listBancos = new ArrayList<>();
		List<String> listChequeras = new ArrayList<>();
		List<String> listEmpresas = new ArrayList<String>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 120)){
			return listResult;
		}
		
		try {
			
			if(!jsonBancos.equals("")){
				
				jsonBancos = Utilerias.validarCadenaSQL(jsonBancos);
				
				Gson gson = new Gson();
				List<Map<String, String>> objBancos = gson.fromJson(jsonBancos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				
				for(int i=0; i<objBancos.size(); i++){
					String idBanco = objBancos.get(i).get("idBanco");
					listBancos.add(idBanco);
				}
			}
			
			if(!jsonChequeras.equals("")){
				
				jsonChequeras = Utilerias.validarCadenaSQL(jsonChequeras);
				
				Gson gson = new Gson();
				List<Map<String, String>> objChequeras = gson.fromJson(jsonChequeras, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				
				for(int i=0; i<objChequeras.size(); i++){
					String idChequera = objChequeras.get(i).get("idChequera");
					listChequeras.add(idChequera);
				}
			}
			
			if(!jsonEmpresas.equals("")){
				
				jsonEmpresas = Utilerias.validarCadenaSQL(jsonEmpresas);
				
				Gson gson = new Gson();
				List<Map<String, String>> objEmpresa = gson.fromJson(jsonEmpresas, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				
				for(int i=0; i<objEmpresa.size(); i++){
					String noEmpresa = objEmpresa.get(i).get("noEmpresa");
					listEmpresas.add(noEmpresa);
				}
			}
			
			divisa = Utilerias.validarCadenaSQL(divisa);
			fechaIni = Utilerias.validarCadenaSQL(fechaIni);
			fechaFin = Utilerias.validarCadenaSQL(fechaFin);
			
			posicionBancariaService = (PosicionBancariaService)contexto.obtenerBean("posicionBancariaBusiness");
			
			if(tipoReporte==1){
				listResult = posicionBancariaService.posicionXEmp2(listBancos,listChequeras, listEmpresas, //subrev1
						fechaIni, fechaFin, seleccion,
						divisa, tipoReporte);
			}else if(tipoReporte==2){
				listResult = posicionBancariaService.posicionXCta2(listBancos,listChequeras, listEmpresas, //subrev2
						fechaIni, fechaFin, seleccion,
						divisa, tipoReporte);
			}else if(tipoReporte==3){
				listResult = posicionBancariaService.repSaldos2(listBancos,listChequeras, listEmpresas, //subrev3
						fechaIni, fechaFin, seleccion,
						divisa, tipoReporte);
			}
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaAction, M:posicionXEmp2");
		}
		return listResult;
	}
		
	public String nombreReporte(int tipoReporte) {
		String x = "";
		
		switch(tipoReporte) {
			case 0:
				return "DIARIO TESORERIA";
			case 1:
				return "FICHA VALOR EMPRESA";
			case 2:
				return "FICHA VALOR CUENTA";
			case 3:
				return "FICHA VALOR GLOBAL";
			case 4:
				return "MOVIMIENTOS TESORERIA";
			case 5:
				return "SALDOS POR MONTO";
			case 6:
				return "REPORTE DE SALDOS";
		}
		return x;
	}
	
	@DirectMethod
	public Map<String, String> insertarPlantilla(String jsonBancos,String jsonChequeras, String jsonEmpresas,
													  String jsonOrden, String nomPlantilla,
													  String consulta, String fechaIni, 
													  String fechaFin, int seleccion, String divisa) {
		
		Map<String, String> result = new HashMap<>();
		result.put("error", "");
		result.put("msgUsuario", "");
		
		List<String> listBancos = new ArrayList<>();
		List<String> listChequeras = new ArrayList<>();
		List<String> listEmpresas = new ArrayList<String>();
		List<String> listOrden = new ArrayList<>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 120)){
			return result;
		}
		
		try {
			
			if(!jsonBancos.equals("")){
				
				jsonBancos = Utilerias.validarCadenaSQL(jsonBancos);
				
				Gson gson = new Gson();
				List<Map<String, String>> objBancos = gson.fromJson(jsonBancos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				
				for(int i=0; i<objBancos.size(); i++){
					String idBanco = objBancos.get(i).get("idBanco");
					listBancos.add(idBanco);
				}
			}
			
			if(!jsonChequeras.equals("")){
				
				jsonChequeras = Utilerias.validarCadenaSQL(jsonChequeras);
				
				Gson gson = new Gson();
				List<Map<String, String>> objChequeras = gson.fromJson(jsonChequeras, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				
				for(int i=0; i<objChequeras.size(); i++){
					String idChequera = objChequeras.get(i).get("idChequera");
					listChequeras.add(idChequera);
				}
			}
			
			if(!jsonEmpresas.equals("")){
				
				jsonEmpresas = Utilerias.validarCadenaSQL(jsonEmpresas);
				
				Gson gson = new Gson();
				List<Map<String, String>> objEmpresa = gson.fromJson(jsonEmpresas, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				
				for(int i=0; i<objEmpresa.size(); i++){
					String noEmpresa = objEmpresa.get(i).get("noEmpresa");
					listEmpresas.add(noEmpresa);
				}
			}
			
			if(!jsonOrden.equals("")){
				
				jsonOrden = Utilerias.validarCadenaSQL(jsonOrden);
				
				Gson gson = new Gson();
				List<Map<String, String>> objOrden = gson.fromJson(jsonOrden, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				
				for(int i=0; i<objOrden.size(); i++){
					String orden = objOrden.get(i).get("orden");
					listOrden.add(orden);
				}
			}

			divisa = Utilerias.validarCadenaSQL(divisa);
			fechaIni = Utilerias.validarCadenaSQL(fechaIni);
			fechaFin = Utilerias.validarCadenaSQL(fechaFin);
			nomPlantilla = Utilerias.validarCadenaSQL(nomPlantilla);
			consulta = Utilerias.validarCadenaSQL(consulta);
			
			posicionBancariaService = (PosicionBancariaService)contexto.obtenerBean("posicionBancariaBusiness");
			result = posicionBancariaService.insertarPlantilla(listBancos, listChequeras, listEmpresas,
															   listOrden, nomPlantilla,
															   consulta, fechaIni, 
															   fechaFin, seleccion, divisa);
			
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaAction, M:insertarPlantilla");	
		}
		return result;
	}
	
	@DirectMethod
	public Map<String, Object> obtenerPlantilla(int idPlantilla) {
		
		Map<String, Object> result = new HashMap<>();
		result.put("error", "");
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 120)){
			result.put("error", "Error de inicio de sesion o pantalla no asignada.");
			return result;
		}
		
		try {
			
			posicionBancariaService = (PosicionBancariaService)contexto.obtenerBean("posicionBancariaBusiness");
			UsuarioPlantilla plantilla = new UsuarioPlantilla();
			plantilla = posicionBancariaService.obtenerPlantilla(idPlantilla);
			
			if(plantilla != null){
				result.put("plantilla", plantilla);
			}
			
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaAction, M:obtenerPlantilla");
			result.put("error", "Error al obtener datos de plantilla");
		}
		return result;
	}
	
	@DirectMethod
	public Map<String, String> eliminarPlantilla(int idPlantilla) {
		
		Map<String, String> result = new HashMap<>();
		result.put("error", "");
		result.put("msgUsuario", "");
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 120)){
			result.put("error", "Error de inicio de sesion o pantalla no asignada.");
			return result;
		}
		
		try {
			
			posicionBancariaService = (PosicionBancariaService)contexto.obtenerBean("posicionBancariaBusiness");
			
			result = posicionBancariaService.eliminarPlantilla(idPlantilla);
			
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaAction, M:eliminarPlantilla");
			result.put("error", "Error al eliminar plantilla");
		}
		return result;
	}
}
