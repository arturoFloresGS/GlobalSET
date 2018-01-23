package com.webset.set.seguridad.action;

//UTIL
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.log4j.Logger;

// EXTJS DIRECT 
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
//SET APP
import com.webset.set.seguridad.business.SegComponenteBusiness;
import com.webset.set.seguridad.dto.SegComponenteDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;

/**
 * 
 * @author Armando Rodriguez
 *
 */
public class SegMenuAction {

		//private static Logger log = Logger.getLogger(SegFacultadComponenteAction.class);
		private Contexto contexto = new Contexto();
		private SegComponenteBusiness segComponenteBusiness;
		private Bitacora bitacoraDao = new Bitacora();
		private static Logger logger = Logger.getLogger(SegMenuAction.class);

		@DirectMethod 
		public Map<String, Object> listaModulos(String cveUsuario) {
			List<SegComponenteDto> listaComponentes = new ArrayList<SegComponenteDto>();
			
			Map<String, Object> result = new HashMap<String, Object>();
			List<Map<String, Object>> listModulos = new ArrayList<Map<String, Object>>();

			assert cveUsuario != null; 

			try{
				segComponenteBusiness = (SegComponenteBusiness)contexto.obtenerBean("segComponenteBusiness");
				
				listaComponentes = segComponenteBusiness.obtenerModulos(cveUsuario);
				
				ListIterator<SegComponenteDto> itr = listaComponentes.listIterator();
				
				while(itr.hasNext()){
					SegComponenteDto compTmp = itr.next();
					Map<String, Object> row = new HashMap<String, Object>();
					
					row.put("idComponente", Integer.toString(compTmp.getIdComponente()));
					row.put("etiqueta", compTmp.getEtiqueta());
					row.put("ruta_imagen", compTmp.getRutaImagen());
					listModulos.add(row);
				}
				
				result.put("listModulos", listModulos);
				
			}catch(Exception e){ 
				bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + " - " + e.getStackTrace()
						+ "P:Seg, C:SegMenuAction, M:listaModulos");
			}
			return result; 
		}
		
		
		@DirectMethod
		public String listaPermisos(String cveUsuario) {
			System.out.println("entro permisossssss");
			assert cveUsuario != null; 

			String  result ="";
			try{
				segComponenteBusiness = (SegComponenteBusiness)contexto.obtenerBean("segComponenteBusiness");
				
				result= segComponenteBusiness.obtenerPermiso(cveUsuario);
			
			}catch(Exception e){ 
				bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + " - " + e.getStackTrace()
						+ "P:Seg, C:SegMenuAction, M:listaModulos");
			}
			return result; 
		}

		@DirectMethod 
		public String obtenerArbolSubMenus(String params) {
			logger.info("obtenerArbolSubMenus params="+params);
			
			Gson gson = new Gson();
			  
			//List<Map<String, String>> objParams = gson.fromJson(params, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());  // para una lista de hashmaps
			
			// Genear un mapa del objecto json
			Map<String, String> objParams = gson.fromJson(params, new TypeToken<Map<String, String>>(){}.getType());

			
			logger.info(objParams);
			
			//convert JSON into java objec
			//TestJsonFromObject obj = gson.fromJson(json, TestJsonFromObject.class);
					  
			String arbolComponentes = null;
			//assert params != null; 
		
			try{
				segComponenteBusiness = (SegComponenteBusiness)contexto.obtenerBean("segComponenteBusiness");
				arbolComponentes = segComponenteBusiness.obtenerArbolSubMenus(objParams.get("sUserLogin"), Integer.parseInt(objParams.get("nModulo")) );
				
				//System.out.println(arbolComponentes);
				
				//result.debug_formPacket = formParameters; 
			}catch(Exception e){ 
				bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + " - " + e.getStackTrace()
						+ "P:Seg, C:SegMenuAction, M:listaModulos");
			}
			return arbolComponentes; 
		}
		
		public static void main(String args[]){
			long a= System.currentTimeMillis();
			SegMenuAction segMenuAction = new SegMenuAction();
			
			segMenuAction.listaModulos("juanito");
			long b=System.currentTimeMillis();
			logger.info(b-a);
		}
		

}
