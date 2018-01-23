package com.webset.set.utilerias.action;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.service.MantenimientoLeyendasService;
import com.webset.set.utileriasmod.dto.MantenimientoLeyendasDto;
import com.webset.utils.tools.Utilerias;

public class MantenimientoLeyendasAction {
	Contexto contexto = new Contexto();
	Bitacora bitacora = new Bitacora();
	MantenimientoLeyendasService objMantenimientoLeyendasService;
		
	@DirectMethod
	public List<MantenimientoLeyendasDto> llenaGridLeyendas(String descLeyenda){
		List<MantenimientoLeyendasDto> listaResultado = new ArrayList<MantenimientoLeyendasDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 189))
			return listaResultado;
		try{
			objMantenimientoLeyendasService = (MantenimientoLeyendasService)contexto.obtenerBean("objMantenimientoLeyendasBusinessImpl");
			listaResultado = objMantenimientoLeyendasService.llenaGridLeyendas(descLeyenda);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoLeyendasAction, M: llenaGridLeyendas");
		} 
		return listaResultado;
	}
		
	@DirectMethod
	public String insertaMantenimientoLeyendas(String sJson){
		System.out.println("entro en el action");
		String resultado="";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 189))
			return resultado;
		Gson gson = new Gson();
		MantenimientoLeyendasDto dto=new MantenimientoLeyendasDto();
		List<Map<String, String>> objDatos = gson.fromJson(sJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		int t;	
		try{
			objMantenimientoLeyendasService = (MantenimientoLeyendasService)contexto.obtenerBean("objMantenimientoLeyendasBusinessImpl");
			String usuario= GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+"";
			if(usuario.equals(""))
				return "Error al obtener usuario.";
			for(t=0; t<objDatos.size(); t++) {
				dto.setDescLeyenda(objDatos.get(t).get("descLeyenda")!=null ? objDatos.get(t).get("descLeyenda"):"" );
				dto.setFecAlta(objDatos.get(t).get("fecAlta")!=null ? objDatos.get(t).get("fecAlta"):"" );
				dto.setUsuarioAlta(usuario);
			}
			resultado = objMantenimientoLeyendasService.insertaMantenimientoLeyendas(dto);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoLeyendasAction, M: llenaGridLeyendas");
		} return resultado;	
	}
		
	@DirectMethod
	public String updateMantenimientoLeyendas(String sJson){
		
		String resultado="";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 189))
			return resultado;
		Gson gson = new Gson();
		MantenimientoLeyendasDto dto=new MantenimientoLeyendasDto();
		List<Map<String, String>> objDatos = gson.fromJson(sJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		int t;
		try{
			objMantenimientoLeyendasService = (MantenimientoLeyendasService)contexto.obtenerBean("objMantenimientoLeyendasBusinessImpl");
			for(t=0; t<objDatos.size(); t++) {
				dto.setIdLeyenda(objDatos.get(t).get("idLeyenda")!=null && !objDatos.get(t).get("idLeyenda").equals("") ? Integer.parseInt(objDatos.get(t).get("idLeyenda")):0);
				dto.setDescLeyenda(objDatos.get(t).get("descLeyenda")!=null ? objDatos.get(t).get("descLeyenda"):"" );
				}
			resultado = objMantenimientoLeyendasService.updateMantenimientoLeyendas(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoLeyendasAction, M: updateMantenimientoLeyendas");
		} return resultado;	
	}
		
	@DirectMethod
	public String deleteMantenimientoLeyendas(String sJson){
		String resultado="";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 189))
			return resultado;
		Gson gson = new Gson();
		MantenimientoLeyendasDto dto=new MantenimientoLeyendasDto();
		List<Map<String, String>> objDatos = gson.fromJson(sJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		int t;
		try{
			objMantenimientoLeyendasService = (MantenimientoLeyendasService)contexto.obtenerBean("objMantenimientoLeyendasBusinessImpl");
			for(t=0; t<objDatos.size(); t++) {
				dto.setIdLeyenda(objDatos.get(t).get("idLeyenda")!=null && !objDatos.get(t).get("idLeyenda").equals("") ? Integer.parseInt(objDatos.get(t).get("idLeyenda")):0);
			}
			resultado = objMantenimientoLeyendasService.deleteMantenimientoLeyendas(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoLeyendasAction, M: deleteMantenimientoLeyendas");
		} return resultado;	
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
			+"P:Utilerias, C:MantenimientoLeyendasAction, M:obtenerExcel");
		} catch (IOException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:MantenimientoLeyendasAction, M:obtenerExcel");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:MantenimientoLeyendasAction, M:obtenerExcel");
		}
		arch.delete();
		return workbook;
	}
	
	@DirectMethod
	public String exportaExcel(String datos) {
		String resultado = "";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 189))
			return resultado;
		try {
			objMantenimientoLeyendasService  = (MantenimientoLeyendasService )contexto.obtenerBean("objMantenimientoLeyendasBusinessImpl");
			resultado = objMantenimientoLeyendasService .exportaExcel(datos);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C:MantenimientoBitacoraRentasAction, M: exportaExcel");
		}
		return resultado;
	}
		
}
