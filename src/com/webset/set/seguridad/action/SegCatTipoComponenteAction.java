package com.webset.set.seguridad.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;

import com.softwarementors.extjs.djn.config.annotations.DirectFormPostMethod;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.webset.set.seguridad.business.SegCatTipoComponenteBusiness;
import com.webset.set.seguridad.dto.SegCatTipoComponenteDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.dto.Retorno;

/**
 * 
 * @author Sergio Vaca
 *
 */
public class SegCatTipoComponenteAction {
	private Contexto contexto = new Contexto();
	private SegCatTipoComponenteBusiness segCatTipoComponenteBusiness;
	private Bitacora bitacoraDao = new Bitacora();
	
	private static class SubmitResult {
		public boolean success = true; 
		public Map<String, String> errors; 
	}
	
	@DirectMethod
	public List<SegCatTipoComponenteDto> llenarComboCatTipoComponentes(int id){
		List<SegCatTipoComponenteDto> facultades = null;
		segCatTipoComponenteBusiness = (SegCatTipoComponenteBusiness)contexto.obtenerBean("segCatTipoComponenteBusiness");
		try{
			facultades = segCatTipoComponenteBusiness.llenarCombo(id);
		}catch(Exception e){
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seg, C:SegCatTipoComponenteCatTipoComponenteAction, M:llenarComboCatTipoComponentees");
		}
		return facultades;
	}
	
	@DirectFormPostMethod 
	public SubmitResult insertarModificar(Map<String, String> formParameters, Map<String, FileItem> fileFields ) {
		//logger.info(formParameters.get("idTipoComponenteF")+ " "+formParameters.get("claveTipoComponenteF").toUpperCase()+" "+formParameters.get("descripcionF").toUpperCase()+" "+formParameters.get("cmbEstatusTipoComponenteNuevo"));
		assert formParameters != null; 
		assert fileFields != null; 
		SubmitResult result = new SubmitResult();
		try{
			String pf = formParameters.get("prefijo");
			boolean bandera = formParameters.get("banderaF").equals("true");
			segCatTipoComponenteBusiness = (SegCatTipoComponenteBusiness)contexto.obtenerBean("segCatTipoComponenteBusiness");
			SegCatTipoComponenteDto segCatTipoComponenteDto = new SegCatTipoComponenteDto();
			segCatTipoComponenteDto.setIdTipoComponente(Integer.parseInt(formParameters.get("idTipoComponenteF")));
			segCatTipoComponenteDto.setDescripcion((formParameters.get(pf+"descripcionF").toUpperCase()));
			segCatTipoComponenteDto.setEstatus((formParameters.get(pf+"cmbEstatusTipoComponenteNuevo").equals("Activo")? "A" : "I"));
		 
			if(!bandera)
				result.success = segCatTipoComponenteBusiness.agregar(segCatTipoComponenteDto)>0;
			else{
				result.success = segCatTipoComponenteBusiness.modificar(segCatTipoComponenteDto)>0;
			}
			if(!result.success) { 
				result.errors = new HashMap<String,String>(); 
				result.errors.put("Error", "Hubo un error con los datos"); 
			}
		}catch(Exception e){ 
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seg, C:SegCatTipoComponenteCatTipoComponenteAction, M:insertarModificar");
		}
		return result; 
	}
	
	@DirectMethod
	public Retorno eliminar(int idCatTipoComponente){
		Retorno retorno = null;
		int res = -1;
		segCatTipoComponenteBusiness = (SegCatTipoComponenteBusiness)contexto.obtenerBean("segCatTipoComponenteBusiness");
		try{
			retorno = new Retorno();
			res = segCatTipoComponenteBusiness.eliminar(idCatTipoComponente);
			retorno.setId(idCatTipoComponente);
			if(res>0)
				retorno.setResultado(true);
		}catch(Exception e){
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seg, C:SegCatTipoComponenteAction, M:eliminar");
		}
		return retorno;
	}
	
	/**
	 * 
	 * @param idTipoComponente
	 * @param estatus
	 * @return List<SegCatTipoComponenteDto>
	 * 
	 * Consulta la tabla de seg_cat_tipo
	 */
	@DirectMethod
	public List<SegCatTipoComponenteDto> consultar (int idTipoComponente, String estatus){
		SegCatTipoComponenteDto dto = null;
		List<SegCatTipoComponenteDto> facultades = null;
		try{
			segCatTipoComponenteBusiness = (SegCatTipoComponenteBusiness)contexto.obtenerBean("segCatTipoComponenteBusiness");
			dto = new SegCatTipoComponenteDto();
			dto.setIdTipoComponente(idTipoComponente);
			dto.setEstatus(estatus);
			facultades = segCatTipoComponenteBusiness.consultar(dto);
		}catch(Exception e){
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seg, C:SegCatTipoComponenteAction, M:Consultar");
		}
		return facultades;
	}
}
