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
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.service.MantenimientoTiendasService;
import com.webset.set.utileriasmod.dto.MantenimientoTiendasDto;
import com.webset.utils.tools.Utilerias;

public class MantenimientoTiendasAction {
	Contexto contexto = new Contexto();
	Bitacora bitacora = new Bitacora();
	MantenimientoTiendasService objMantenimientoTiendasService;
		
	@DirectMethod
	public List<MantenimientoTiendasDto> llenaGridTiendas(String noAcreedor){
		List<MantenimientoTiendasDto> listaResultado = new ArrayList<MantenimientoTiendasDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 188))
			return listaResultado;
		try{
			objMantenimientoTiendasService = (MantenimientoTiendasService)contexto.obtenerBean("objMantenimientoTiendasBusinessImpl");
			listaResultado = objMantenimientoTiendasService.llenaGridTiendas(noAcreedor);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoTiendasAction, M: llenaGridTiendas");
		} return listaResultado;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboBeneficiario(String ePersona, String nombre){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 188))
			return list;
		try{
			objMantenimientoTiendasService = (MantenimientoTiendasService)contexto.obtenerBean("objMantenimientoTiendasBusinessImpl");
			ePersona=ePersona!=null && !ePersona.equals("")?ePersona:"";
			nombre=nombre!=null && !nombre.equals("")?nombre:"";
			list = objMantenimientoTiendasService.llenarComboBeneficiario(ePersona,nombre);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+"P:Egresos, C:MantenimientoTiendasAction, M:llenarComboBeneficiario");	
		}
		return list;
	}
		
	@DirectMethod
	public String insertaMantenimientoTiendas(String sJson){
		String resultado="";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 188))
			return resultado;
		Gson gson = new Gson();
		MantenimientoTiendasDto dto=new MantenimientoTiendasDto();
		List<Map<String, String>> objDatos = gson.fromJson(sJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		int t;	
		try{
			objMantenimientoTiendasService = (MantenimientoTiendasService)contexto.obtenerBean("objMantenimientoTiendasBusinessImpl");
			String usuario= GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+"";
			if(usuario.equals(""))
				return "Error al obtener usuario.";
			for(t=0; t<objDatos.size(); t++) {
				dto.setCcid(objDatos.get(t).get("ccid")!=null && !objDatos.get(t).get("ccid").equals("") ? Integer.parseInt(objDatos.get(t).get("ccid")):0);
				dto.setDescSucursal(objDatos.get(t).get("descSucursal")!=null ? objDatos.get(t).get("descSucursal"):"" );
				dto.setNoAcreedor(objDatos.get(t).get("noAcreedor")!=null ? objDatos.get(t).get("noAcreedor"):"" );
				dto.setFecAlta(objDatos.get(t).get("fecAlta")!=null ? objDatos.get(t).get("fecAlta"):"" );
				dto.setUsuarioAlta(usuario);
				dto.setNoEmpresa(objDatos.get(t).get("noEmpresa")!=null ? objDatos.get(t).get("noEmpresa"):"");
				dto.setRazonSocial(objDatos.get(t).get("razonSocial")!=null ? objDatos.get(t).get("razonSocial"):"");
			}
			resultado = objMantenimientoTiendasService.insertaMantenimientoTiendas(dto);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoTiendasAction, M: llenaGridTiendas");
		}
		System.out.println("Action resultado: "+resultado);
		return resultado;	
	}
		
	@DirectMethod
	public String updateMantenimientoTiendas(String sJson){
		String resultado="";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 188))
			return resultado;
		Gson gson = new Gson();
		MantenimientoTiendasDto dto=new MantenimientoTiendasDto();
		List<Map<String, String>> objDatos = gson.fromJson(sJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		int t;
		try{
			objMantenimientoTiendasService = (MantenimientoTiendasService)contexto.obtenerBean("objMantenimientoTiendasBusinessImpl");
			String usuario= GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+"";
			if(usuario.equals(""))
				return "Error al obtener usuario.";
			for(t=0; t<objDatos.size(); t++) {
				dto.setCcid(objDatos.get(t).get("ccid")!=null && !objDatos.get(t).get("ccid").equals("") ? Integer.parseInt(objDatos.get(t).get("ccid")):0);
				dto.setDescSucursal(objDatos.get(t).get("descSucursal")!=null ? objDatos.get(t).get("descSucursal"):"" );
				dto.setNoAcreedor(objDatos.get(t).get("noAcreedor")!=null ? objDatos.get(t).get("noAcreedor"):"" );
				dto.setFecMod(objDatos.get(t).get("fechaMod")!=null ? objDatos.get(t).get("fechaMod"):"" );
				dto.setUsuarioMod(usuario);
				dto.setMotivoModif(objDatos.get(t).get("motivo")!=null ? objDatos.get(t).get("motivo"):"" );
				dto.setNoEmpresa(objDatos.get(t).get("noEmpresa")!=null ? objDatos.get(t).get("noEmpresa"):"");
				dto.setRazonSocial(objDatos.get(t).get("razonSocial")!=null ? objDatos.get(t).get("razonSocial"):"");
			}
			resultado = objMantenimientoTiendasService.updateMantenimientoTiendas(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoTiendasAction, M: updateMantenimientoTiendas");
		} return resultado;	
	}
		
	@DirectMethod
	public String deleteMantenimientoTiendas(String sJson){
		String resultado="";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 188))
			return resultado;
		Gson gson = new Gson();
		MantenimientoTiendasDto dto=new MantenimientoTiendasDto();
		List<Map<String, String>> objDatos = gson.fromJson(sJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		int t;
		try{
			objMantenimientoTiendasService = (MantenimientoTiendasService)contexto.obtenerBean("objMantenimientoTiendasBusinessImpl");
			String usuario= GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+"";
			if(usuario.equals(""))
				return "Error al obtener usuario.";
			for(t=0; t<objDatos.size(); t++) {
				dto.setCcid(objDatos.get(t).get("ccid")!=null && !objDatos.get(t).get("ccid").equals("") ? Integer.parseInt(objDatos.get(t).get("ccid")):0);
				dto.setFecMod(objDatos.get(t).get("fechaMod")!=null ? objDatos.get(t).get("fechaMod"):"" );
				dto.setUsuarioMod(usuario);
				dto.setMotivoModif(objDatos.get(t).get("motivo")!=null ? objDatos.get(t).get("motivo"):"" );
				dto.setDescSucursal(objDatos.get(t).get("descSucursal")!=null ? objDatos.get(t).get("descSucursal"):"" );
				dto.setNoAcreedor(objDatos.get(t).get("noAcreedor")!=null ? objDatos.get(t).get("noAcreedor"):"" );
				dto.setNoEmpresa(objDatos.get(t).get("noEmpresa")!=null ? objDatos.get(t).get("noEmpresa"):"");
				dto.setRazonSocial(objDatos.get(t).get("razonSocial")!=null ? objDatos.get(t).get("razonSocial"):"");
			}
			resultado = objMantenimientoTiendasService.deleteMantenimientoTiendas(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoTiendasAction, M: deleteMantenimientoTiendas");
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
			+"P:Utilerias, C:MantenimientoTiendasAction, M:obtenerExcel");
		} catch (IOException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:MantenimientoTiendasAction, M:obtenerExcel");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:MantenimientoTiendasAction, M:obtenerExcel");
		}
		arch.delete();
		return workbook;
	}
	
	@DirectMethod
	public String exportaExcel(String datos) {
		String resultado = "";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 188))
			return resultado;
		try {
			objMantenimientoTiendasService  = (MantenimientoTiendasService )contexto.obtenerBean("objMantenimientoTiendasBusinessImpl");
			resultado = objMantenimientoTiendasService .exportaExcel(datos);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C:MantenimientoTiendasAction, M: exportaExcel");
		}
		return resultado;
	}
		
}
