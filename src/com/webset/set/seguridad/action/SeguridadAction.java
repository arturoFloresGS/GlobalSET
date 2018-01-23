/*
 * @author : Armando Rodriguez - WEBSET
 */
package com.webset.set.seguridad.action;

import java.util.Date;

/*
import java.io.IOException;
import java.io.PrintWriter;
*/

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

import com.softwarementors.extjs.djn.config.annotations.DirectFormPostMethod;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContext;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.seguridad.business.SegUsuarioBusiness;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;


public class SeguridadAction {
	private Funciones funciones = new Funciones();
	private GlobalSingleton globalSingleton;
	private static Logger logger = Logger.getLogger(SeguridadAction.class);
	private static Contexto contexto = new Contexto();
	SegUsuarioBusiness segUsuarioBusiness;
	Bitacora bitacora = new Bitacora();
	
// VALIDAR LOGIN CORRECTO
	
	public String validarLogin(String sUsr, String sPass){
		String resp = "";
		int result;
		
		try {
			segUsuarioBusiness = (SegUsuarioBusiness)contexto.obtenerBean("segUsuarioBusiness");
			result = (Integer)segUsuarioBusiness.validarContrasena(sUsr, sPass).get("id_usuario");
			resp = result <= 0 ? "Usuario y/o contrase\u00f1a incorrectos" : "";
		} catch(Exception e){
			e.printStackTrace();
		}
		return resp;
	}
	
	  private static class SubmitResult {
		public boolean success = true;
		public Map<String, String> errors;
		@SuppressWarnings("unused")
		public Map<String,String> debug_formPacket;
	  }
	
	@DirectFormPostMethod
	public SubmitResult loginSubmit( Map<String, String> formParameters, Map<String, FileItem> fileFields ) {
		String sUsuario = formParameters.get( "userLogin").toUpperCase();
		String sPassword = formParameters.get( "passLogin");
		
		SubmitResult result = new SubmitResult(); 
		
		 if (sUsuario != null && sPassword != null) {
			if (!sUsuario.equals("") && !sPassword.equals("")) {
				assert formParameters != null;
				assert fileFields != null;
				
				String sCodSession = "";
				globalSingleton = GlobalSingleton.getInstancia();
				WebContext context = WebContextManager.get();

				Contexto contexto = new Contexto();				

				SegUsuarioBusiness segUsuarioBusiness = (SegUsuarioBusiness)contexto.obtenerBean("segUsuarioBusiness");
				
				logger.info("segUsuarioBusiness=" +segUsuarioBusiness);

				try{
					//logger.info("validarContrasena()=" +sUsuario+","+sPassword);

					Map<String, Object> resultBusiness = segUsuarioBusiness.validarContrasena(sUsuario, sPassword);
					
					result.success = (Integer)resultBusiness.get("id_usuario") > 0;
					
					if (result.success) {
						sCodSession = funciones.generarNumSession((Integer)resultBusiness.get("id_usuario"));
						
						if(globalSingleton.obtenerUsuarioActivo())			
							globalSingleton.borrarUsuarioActivo();
						
					    result.debug_formPacket = formParameters;
					    
					    HttpSession sesion = context.getSession();
					    
					    sesion.setAttribute("userLogin", sUsuario);
					    sesion.setAttribute("id_usuario", 
					    		(Integer)resultBusiness.get("id_usuario"));
					    
					    sesion.setAttribute("codSession", sCodSession);						
					} else {
						result.errors = new HashMap<String,String>();
						result.errors.put("Error", resultBusiness.get("msg").toString());
						return result;
					}				    
				} catch(Exception e){
					e.printStackTrace();
				}
				
				return result;
								
			} else {
				result.success = false;
				result.errors = new HashMap<String,String>();
				result.errors.put("Error", "Usuario y Contrase\u00f1a obligatorios");
				return result;
			}			
		} else {
			result.success = false;
			result.errors = new HashMap<String,String>();
			result.errors.put("Error", "Informacion no valida");
			return result;
		}		
	}
	
	@DirectMethod
	public String obtenerUsuario(){
		String sUsuario = null;

		WebContext context = WebContextManager.get();
		
		try{
		    HttpSession sesion = context.getSession();
		    
			sUsuario = (String)sesion.getAttribute("userLogin");

		} catch(Exception e){
			e.printStackTrace();
		}
		
		return sUsuario;
	}
	
	@DirectMethod
	public String obtenerIdUsuario(){
		String sIdUsuario = null;

		WebContext context = WebContextManager.get();
		
		try{
		    HttpSession sesion = context.getSession();
		    
		    sIdUsuario = (String)sesion.getAttribute("id_usuario");

		} catch(Exception e){
			e.printStackTrace();
		}
		
		return sIdUsuario;
	}
	
	@DirectMethod
	public String cambiarPassword(String sUsr, String sPass, String sNewUsr, String sNewConfPass) {
		String resp = "";
		int result;
		
		try {
			resp = validaDatos(sUsr.toUpperCase(), sPass, sNewUsr, sNewConfPass);
			
			if(!resp.equals("")) return resp;
			
			segUsuarioBusiness = (SegUsuarioBusiness)contexto.obtenerBean("segUsuarioBusiness");
			result = segUsuarioBusiness.cambiaContrasena(sUsr.toUpperCase(), sNewUsr);
			
			if(result != 0)
				resp = "Contrase\u00f1a modificada correctamente";
			else
				resp = "Error al modificar contrase\u00f1a";
		}catch(Exception e) {
			e.printStackTrace();
		}
		return resp;
	}
	
	
	
	public String validaDatos(String sUsr, String sPass, String sNewPass, String sConfirmNewPass){
		String resp = "";
		
		resp = validarLogin(sUsr, sPass);
		
		if(!resp.equals("")) return resp;
		
		try {
			if(sUsr.equals("") || sPass.equals("") || sNewPass.equals("") || sConfirmNewPass.equals(""))
				return "Introdusca todos los datos";
			else if(!sNewPass.equals(sConfirmNewPass))
				return "Las nuevas contrase\u00f1as son diferentes";
			else if(sNewPass.indexOf(" ") >= 0)
				return "La nueva contrase\u00f1a no puede contener espacios vacios";
			
			Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,20}$");
			Matcher matcher = pattern.matcher(sNewPass);
			
			if (!matcher.find()) return "La contrase\u00f1a no puede ser menor a 6 y mayor a 20 caracteres, debe contener almenos uno de los siguientes: 0-9, a-z, A-Z";
			
			Pattern patternC = Pattern.compile("^.@_-~#]+$");
			Matcher matcherC = patternC.matcher(sNewPass);
			
			if (matcherC.find()) return "La contrase\u00f1a no puede contener caracteres especiales como: .@_-~#]+$";
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SeguridadAction, M:validaDatos");
		}
		return resp;
	}
	
	
  /*
	// ESTRUCTURA DE DATOS 
	public static class BasicInfo {
		public static class Data {
			public String foo;
			public String name;
			public String company;
			public String email;
		}
	
		public boolean success = true;    
		public Data data = new Data();
	}
  
	@DirectMethod
	public BasicInfo getBasicInfo( Long userId, String foo ) {
		assert userId != null;
		assert foo != null;
		    
		BasicInfo result = new BasicInfo();
		result.data.foo = foo;
		result.data.name = "Aaron Conran";
		result.data.company = "Ext JS, LLC";
		result.data.email = "aaron@extjs.com";
		return result;
	}
	  
	
	
	
  // OBTENER UN ARBOL
  public static class Node {
    public String id;
    public String text;
    public boolean leaf;
  }
  
  @DirectMethod 
  public List<Node> getTree( String id ) {
    List<Node> result = new ArrayList<Node>();
    if( id.equals("root")) {
      for( int i = 1; i <= 5; ++i ) {
        Node node = new Node();
        node.id = "n" + i;
        node.text = "Node " + i;
        node.leaf = false;
        result.add(node);
      }
    }
    else if( id.length() == 2) {
      String num = id.substring(1);
      for( int i = 1; i <= 5; ++i ) {
        Node node = new Node();
        node.id = "id" + i;
        node.text = "Node " + num + "." + i;
        node.leaf = true;
        result.add(node);
      }
    }
    return result;
  }*/
}
