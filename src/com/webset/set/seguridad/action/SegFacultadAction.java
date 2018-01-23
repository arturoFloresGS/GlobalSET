package com.webset.set.seguridad.action;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

import com.softwarementors.extjs.djn.config.annotations.DirectFormPostMethod;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.seguridad.business.SegFacultadBusiness;
import com.webset.set.seguridad.dto.SegFacultadDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.dto.Retorno;
import com.webset.utils.tools.Utilerias;

/**
 * 
 * @author Sergio Vaca
 *
 */
public class SegFacultadAction {
	private Contexto contexto = new Contexto();
	private SegFacultadBusiness segFacultadBusiness;
	private Bitacora bitacoraDao = new Bitacora();
	
	
	class SubmitResult { 
		public boolean banActu = false;
		public boolean success = true; 
		public Map<String, String> errors;
		public Map<String,String> debug_formPacket; 
	}
	
	@DirectMethod
	public List<SegFacultadDto> llenarComboFacultades(int id){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<SegFacultadDto> facultades = null;
		segFacultadBusiness = (SegFacultadBusiness)contexto.obtenerBean("segFacultadBusiness");
		try{
			facultades = segFacultadBusiness.llenarCombo(id);
		}catch(Exception e){
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seg, C:SegFacultadFacultadAction, M:llenarComboFacultades");
		}
		return facultades;
	}
	
	@DirectFormPostMethod 
	public SubmitResult insertarModificar(Map<String, String> formParameters, Map<String, FileItem> fileFields ) {
		assert formParameters != null; 
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		assert fileFields != null; 
		SubmitResult result = new SubmitResult();
		try{	
			String pf = formParameters.get("prefijo");
			boolean bandera = formParameters.get("banderaF").equals("true");
			segFacultadBusiness = (SegFacultadBusiness)contexto.obtenerBean("segFacultadBusiness");
			SegFacultadDto segFacultadDto = new SegFacultadDto();
			segFacultadDto.setIdFacultad(Integer.parseInt(formParameters.get("idFacultadF")));
			//segFacultadDto.setClaveFacultad((formParameters.get(pf+"claveFacultadF").toUpperCase()));
			segFacultadDto.setDescripcion((formParameters.get(pf+"descripcionF").toUpperCase()));
			segFacultadDto.setEstatus((formParameters.get(pf+"cmbEstatusFacultadNuevo").equals("Activo")? "A" : "I"));
			if(!bandera)
				result.success = segFacultadBusiness.agregar(segFacultadDto)>0;
			else
				result.success = segFacultadBusiness.modificar(segFacultadDto)>0;
				result.banActu = true;
			result.debug_formPacket = formParameters; 
		}catch(Exception e){ 
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seg, C:SegFacultadFacultadAction, M:insertarModificar");
		}
		return result; 
	}
	
	@DirectMethod
	public Retorno eliminar(int idFacultad){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		Retorno retorno = null;
		int res = -1;
		segFacultadBusiness = (SegFacultadBusiness)contexto.obtenerBean("segFacultadBusiness");
		try{
			retorno = new Retorno();
			res = segFacultadBusiness.eliminar(idFacultad);
			retorno.setId(idFacultad);
			if(res>0)
				retorno.setResultado(true);
		}catch(Exception e){
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seg, C:SegFacultadAction, M:eliminar");
		}
		return retorno;
	}
	
	@DirectMethod
	public List<SegFacultadDto> consultar (int idFacultad, String estatus){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		SegFacultadDto dto = null;
		List<SegFacultadDto> facultades = null;
		try{
			segFacultadBusiness = (SegFacultadBusiness)contexto.obtenerBean("segFacultadBusiness");
			dto = new SegFacultadDto();
			dto.setIdFacultad(idFacultad);
			dto.setEstatus(estatus);
			facultades = segFacultadBusiness.consultar(dto);
		}catch(Exception e){
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seg, C:SegFacultadAction, M:Consultar");
		}
		return facultades;
	}

}
