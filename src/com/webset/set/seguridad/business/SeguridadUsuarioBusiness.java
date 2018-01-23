package com.webset.set.seguridad.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.seguridad.dao.SeguridadUsuarioDao;
import com.webset.set.seguridad.dto.ComboUsuario;
import com.webset.set.seguridad.dto.GridUsuario;
import com.webset.set.seguridad.dto.SegUsuarioDto;
import com.webset.set.seguridad.exceptions.BusinessException;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;

public class SeguridadUsuarioBusiness {
	SegUsuarioDto usuario = new SegUsuarioDto();
	SeguridadUsuarioDao seguridadUsuarioDao;
	private static Logger logger = Logger.getLogger(SegUsuarioBusiness.class);
	private Bitacora bitacora = new Bitacora();
	
	
	public List<GridUsuario> consultarGrid(int idUsuario, String estatus) throws Exception, BusinessException{
		usuario.setIdUsuario(idUsuario);
		usuario.setEstatus(estatus);
		
	 List<GridUsuario> usuarios = seguridadUsuarioDao.consultarGrid(usuario); 
		if(usuarios.size() == 0)
			logger.info("No existe el usuario. Verifique");
		return usuarios;
		
	}
	
	public List<ComboUsuario> consultar() throws Exception, BusinessException{		
		List<ComboUsuario> comboUsuarios = seguridadUsuarioDao.consultar();;
		if(comboUsuarios.size() == 0){
			logger.info("No existe el usuario. Verifique");
		}		
		return comboUsuarios;
	}
	
	public List<LlenaComboGralDto> obtenerTodasCajas() throws Exception, BusinessException{
		List<LlenaComboGralDto> cajas = seguridadUsuarioDao.obtenerTodasCajas();
		return cajas;
	}
	
	public List<LlenaComboGralDto> comboPerfilesUsuario() throws Exception, BusinessException{
		return seguridadUsuarioDao.comboPerfilUsuario();
	}
	
	public String eliminar(int idUsuario) throws Exception, BusinessException{
		if(idUsuario!=0)
			return seguridadUsuarioDao.deleteUsuario(idUsuario+"");
		else
			return "Error al eliminar. Usuario vacio";
	}
	
	public String agregar(SegUsuarioDto usuario, String idPerfil) throws Exception, BusinessException{
		int ret=0;
		String respuesta="Error al obtener id";
		usuario.setIntentos(0);
		usuario.setIdUsuario(seguridadUsuarioDao.getSecuencia());
		if(usuario.getIdUsuario()>=0)
			respuesta ="Error al guardar usuario";
		else
			return respuesta;
		
		
		ret = seguridadUsuarioDao.insertarSegUsuario(usuario);
		if(ret>=0)
			respuesta ="Error al guardar la categoria del usuario";
		else
			return respuesta;
		
		ret = seguridadUsuarioDao.insertarCatUsuario(usuario);
		if(ret>=0)
			respuesta ="Error al guardar el perfil";
		else
			return respuesta;
		
		ret = seguridadUsuarioDao.insertarPerfil(usuario.getIdUsuario(), idPerfil);
		
		if(ret>=0)
			respuesta ="Exito";
		
		return respuesta;
	}
	
	
	public String modificar(SegUsuarioDto usuario, String idPerfil) throws Exception, BusinessException{
		int ret=0;
		
		String respuesta="Error al modificar el perfil";
		ret = seguridadUsuarioDao.updatePerfil(usuario.getIdUsuario(), idPerfil);
		
		if(ret!=0)
			respuesta ="Error al modificar el usuario"; //seg_usuario
		else
			return respuesta;
		
		ret = seguridadUsuarioDao.modificar(usuario);
		
		if(ret!=0)
			respuesta ="Error al modificar el usuario"; //seg_usuario
		else
			return respuesta;
		
		seguridadUsuarioDao.modificarCatUsuarios(usuario);
		
		if(ret!=0)
			respuesta ="Exito"; //seg_usuario
		
		return respuesta;
	}
	
	/**********Excel***************/			 
	public String exportaExcel(String datos) {
		String respuesta = "";
	    Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
	    try {	    	
			
			HSSFWorkbook wb = Utilerias.generarExcel(new String[]{
													"Id",
													"Usuario",
										            "Nombre",
													"Apellido paterno",
													"Apellido materno",
										            "Estatus",
										            "Intentos",
													"Correo electronico",
													"Fecha ultimo acceso",
										            "Fecha vencimiento",
													"Empresa",
										            "Caja",
													"Perfil"
												}, 
												parameters, 
												new String[]{
														"idUsuario",
														"cveUsuario",
														"nombreU",
														"paterno",
														"materno",
														"estatus",
														"intentos",
														"mail",
														"acceso",
														"vencimiento",
														"nomEmpresa",
														"nomCaja",
														"perfil"
														
												},"Usuarios");
            
            respuesta = ConstantesSet.RUTA_EXCEL + "propuestas " + Utilerias.indicadorFecha() +".xls";
            File outputFile = new File(respuesta);
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
        } catch (IOException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Utilerias, C:SeguridadUsuarioAction, M:exportaExcel");
        	respuesta = "";
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Utilerias, C:SeguridadUsuarioAction, M:exportaExcel");
            respuesta = "";
        }
        return respuesta;
	}
	
	/********Importante getterandsetters***/
	//getters && setters
		public SeguridadUsuarioDao getSeguridadUsuarioDao() {
			return seguridadUsuarioDao;
		}

		public void setSeguridadUsuarioDao(SeguridadUsuarioDao seguridadUsuarioDao) {
			this.seguridadUsuarioDao = seguridadUsuarioDao;
		}
}
