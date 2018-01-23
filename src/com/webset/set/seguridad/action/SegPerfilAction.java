package com.webset.set.seguridad.action;

//UTIL
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

//EXTJS DIRECT 
import com.softwarementors.extjs.djn.config.annotations.DirectFormPostMethod;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
//APP
import com.webset.set.seguridad.business.SegPerfilBusiness;
import com.webset.set.seguridad.dto.SegPerfilDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.dto.Retorno;
import com.webset.utils.tools.Utilerias;

/**
 * 
 * @author Sergio Vaca
 *
 */
public class SegPerfilAction {
	private Contexto contexto = new Contexto();
	private SegPerfilBusiness segPerfilBusiness;
	private Bitacora bitacoraDao = new Bitacora();
	
	
	class SubmitResult { 
		public boolean banActu = false;
		public boolean success = true; 
		public Map<String, String> errors;
		public Map<String,String> debug_formPacket; 
	}
	
	@DirectMethod
	public List<SegPerfilDto> llenarComboPerfiles(int id){
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 27))
			return null;
		List<SegPerfilDto> perfiles = null;
		segPerfilBusiness = (SegPerfilBusiness)contexto.obtenerBean("segPerfilBusiness");
		try{
			perfiles = segPerfilBusiness.llenarCombo(id);
		}catch(Exception e){
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seg, C:SegPerfilFacultadAction, M:llenarComboPerfiles");
		}
		return perfiles;
	}
	
	@DirectFormPostMethod 
	public SubmitResult insertarModificar(Map<String, String> formParameters, Map<String, FileItem> fileFields ) {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 27))
			return null;
		assert formParameters != null; 
		assert fileFields != null;
		SubmitResult result = new SubmitResult();
		try{ 
			String pf = formParameters.get("prefijo");
			boolean bandera = formParameters.get("banderaF").equals("true");
			
			segPerfilBusiness = (SegPerfilBusiness)contexto.obtenerBean("segPerfilBusiness");

			SegPerfilDto segPerfilDto = new SegPerfilDto();
			segPerfilDto.setIdPerfil(Integer.parseInt(formParameters.get("idPerfilF")));
			segPerfilDto.setDescripcion((formParameters.get(pf+"descripcionF").toUpperCase()));
			segPerfilDto.setEstatus((formParameters.get(pf+"cmbEstatusPerfilNuevo").equals("Activo")? "A" : "I"));
			
			if(!bandera)
				result.success = segPerfilBusiness.agregar(segPerfilDto)>0;
			else{
				result.success = segPerfilBusiness.modificar(segPerfilDto)>0;
				result.banActu = true;
			}
			result.debug_formPacket = formParameters; 
		}catch(Exception e){ 
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seg, C:SegPerfilFacultadAction, M:insertarModificar");
		}
		return result; 
	}
	
	@DirectMethod
	public Retorno eliminar(int idPerfil){
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 27))
			return null;
		Retorno retorno = null;
		int res = -1;
		segPerfilBusiness = (SegPerfilBusiness)contexto.obtenerBean("segPerfilBusiness");
		try{
			retorno = new Retorno();
			res = segPerfilBusiness.eliminar(idPerfil);
			retorno.setId(idPerfil);
			if(res>0)
				retorno.setResultado(true);
		}catch(Exception e){
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seg, C:SegPerfilAction, M:eliminar");
		}
		return retorno;
	}
	
	@DirectMethod
	public List<SegPerfilDto> consultar (int idPerfil, String estatus){
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 27))
			return null;
		SegPerfilDto dto = null;
		List<SegPerfilDto> perfiles = null;
		try{
			if (Utilerias.haveSession(WebContextManager.get()) && 
					Utilerias.tienePermiso(WebContextManager.get(), 27)) {
				segPerfilBusiness = (SegPerfilBusiness)contexto.obtenerBean("segPerfilBusiness");
				dto = new SegPerfilDto();
				dto.setIdPerfil(idPerfil);
				dto.setEstatus(estatus);
				perfiles = segPerfilBusiness.consultar(dto);
			}
			
		}catch(Exception e){
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seg, C:SegPerfilAction, M:Consultar");
		}
		return perfiles;
	}
}
