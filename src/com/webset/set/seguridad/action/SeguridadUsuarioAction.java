package com.webset.set.seguridad.action;

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
import com.webset.set.seguridad.business.SeguridadUsuarioBusiness;
import com.webset.set.seguridad.dto.ComboUsuario;
import com.webset.set.seguridad.dto.GridUsuario;
import com.webset.set.seguridad.dto.SegUsuarioDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.service.MantenimientoLeyendasService;
import com.webset.utils.tools.Utilerias;

public class SeguridadUsuarioAction {
	private Contexto contexto = new Contexto();
	private Bitacora bitacora = new Bitacora();
	SeguridadUsuarioBusiness seguridadUsuarioBusiness;
	
	@DirectMethod
	public List<GridUsuario> obtenerUsuariosGrid(int idUsuario, String estatus) {
		List<GridUsuario> gridUsuarios = new ArrayList<GridUsuario>();
		if(!Utilerias.haveSession(WebContextManager.get())|| !Utilerias.tienePermiso(WebContextManager.get(), 201))
			return gridUsuarios;
		try {
			seguridadUsuarioBusiness = (SeguridadUsuarioBusiness)contexto.obtenerBean("seguridadUsuarioBusiness");
			gridUsuarios = seguridadUsuarioBusiness.consultarGrid(idUsuario, estatus);
		} 
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Seguridad, C:SeguridadUsuarioAction, M:obtenerUsuariosGrid");
		}		
		return gridUsuarios;
	}
	
	@DirectMethod
	public List<ComboUsuario> obtenerUsuarios() {
		List<ComboUsuario> usuarios = new ArrayList<ComboUsuario>();
		if(!Utilerias.haveSession(WebContextManager.get())|| !Utilerias.tienePermiso(WebContextManager.get(), 201))
			return null;
		try{
			seguridadUsuarioBusiness = (SeguridadUsuarioBusiness)contexto.obtenerBean("seguridadUsuarioBusiness");
			usuarios = seguridadUsuarioBusiness.consultar();
		}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Seguridad, C:SeguridadUsuarioAction, M:obtenerUsuarios");
		}return usuarios;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> obtenerTodasCajas() {
		List<LlenaComboGralDto> cajas = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get())|| !Utilerias.tienePermiso(WebContextManager.get(), 201))
			return cajas;
		try{//seguridadUsuarioBusiness
			seguridadUsuarioBusiness = (SeguridadUsuarioBusiness)contexto.obtenerBean("seguridadUsuarioBusiness");
			cajas = seguridadUsuarioBusiness.obtenerTodasCajas();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Seguridad, C:SeguridadUsuarioAction, M:obtenerTodasCajas");
		}return cajas;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> comboPerfilesUsuario(){
		List<LlenaComboGralDto> listaUsuarioPerfil = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 201))
			return listaUsuarioPerfil;
		
		try{
			seguridadUsuarioBusiness = (SeguridadUsuarioBusiness)contexto.obtenerBean("seguridadUsuarioBusiness");
			listaUsuarioPerfil = seguridadUsuarioBusiness.comboPerfilesUsuario();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Seg, C:SeguridadUsuarioAction, M:comboPerfilesUsuario");
		}return listaUsuarioPerfil;
	}

	
	@DirectMethod
	public String eliminar(int idU) {
		String mensaje="Error";
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 201))
			return mensaje;
		try {
			seguridadUsuarioBusiness = (SeguridadUsuarioBusiness)contexto.obtenerBean("seguridadUsuarioBusiness");
			mensaje =seguridadUsuarioBusiness.eliminar(idU);
		
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Seguridad, C:SeguridadUsuarioAction, M:eliminar");
		}		
		return mensaje;
	}
	
	@DirectMethod
	public String insertarModificarB(String sJson){
		String resultado="";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 201))
			return resultado;
		Gson gson = new Gson();
		SegUsuarioDto dto = new SegUsuarioDto();
		List<Map<String, String>> objDatos = gson.fromJson(sJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		int t;	
		try{
			seguridadUsuarioBusiness = (SeguridadUsuarioBusiness)contexto.obtenerBean("seguridadUsuarioBusiness");
			
			boolean bandera=false;
			for(t=0; t<objDatos.size(); t++) {
				bandera=(objDatos.get(0).get("banderaF").equals("true") ? true:false );
				dto.setIdUsuario(objDatos.get(t).get("idUsuarioF")!=null ? Integer.parseInt(objDatos.get(t).get("idUsuarioF")):0);
				dto.setClaveUsuario(objDatos.get(t).get("clave")!=null ? objDatos.get(t).get("clave"):"" );
				dto.setNombre(objDatos.get(t).get("nombre")!=null ? objDatos.get(t).get("nombre"):"");
				dto.setApellidoPaterno(objDatos.get(t).get("apPaterno")!=null ? objDatos.get(t).get("apPaterno"):"");
				dto.setApellidoMaterno(objDatos.get(t).get("apMaterno")!=null ? objDatos.get(t).get("apMaterno"):"");
				dto.setContrasena(objDatos.get(t).get("contrasena")!=null ? objDatos.get(t).get("contrasena"):"");
				String estatus=objDatos.get(t).get("cmbEstatusNuevo").equals("A")? "A":"I";
				System.out.println("estatus"+estatus);
				dto.setEstatus(estatus);
				dto.setCorreoElectronico(objDatos.get(t).get("correoElectronico")!=null ? objDatos.get(t).get("correoElectronico"):"");
				dto.setFechaVencimiento(objDatos.get(t).get("fecVencimiento")!=null ? objDatos.get(t).get("fecVencimiento"):"");
				dto.setNoEmpresa(objDatos.get(t).get("noEmpresa")!=null ? Integer.parseInt(objDatos.get(t).get("noEmpresa")):0);
				String intentos=objDatos.get(t).get("intentos")!=null ? objDatos.get(t).get("intentos"):"";
				dto.setIdCaja(objDatos.get(t).get("idCaja")!=null ? Integer.parseInt(objDatos.get(t).get("idCaja")):0);
				dto.setIdPerfil(objDatos.get(t).get("idPerfil")!=null ? Integer.parseInt(objDatos.get(t).get("idPerfil")):0);
				if(intentos.equals("true")){
					dto.setIntentos(3);
				}else{
					dto.setIntentos(0);
				}
				
				if(!bandera) {
					resultado = seguridadUsuarioBusiness.agregar(dto, dto.getIdPerfil()+"")+"";
				}else{
					resultado = seguridadUsuarioBusiness.modificar(dto, dto.getIdPerfil()+"")+"";
				}
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:SeguridadUsuarioAction, M: insertarModificarB");
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
			+"P:Utilerias, C:SeguridadUsuarioAction, M:obtenerExcel");
		} catch (IOException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:SeguridadUsuarioAction, M:obtenerExcel");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:SeguridadUsuarioAction, M:obtenerExcel");
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
			seguridadUsuarioBusiness = (SeguridadUsuarioBusiness)contexto.obtenerBean("seguridadUsuarioBusiness");
			resultado = seguridadUsuarioBusiness.exportaExcel(datos);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C:SeguridadUsuarioAction, M: exportaExcel");
		}
		return resultado;
	}
}
