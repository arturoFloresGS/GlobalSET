

package com.webset.set.seguridad.action;

//UTIL
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//EXTJS DIRECT 
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
//SET APP
import com.webset.set.seguridad.business.SegPerfilBusiness;
import com.webset.set.seguridad.business.SegPerfilFacultadBusiness;
import com.webset.set.seguridad.dto.SegPerfilDto;
import com.webset.set.seguridad.dto.SegPerfilFacultadDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.Retorno;

/**
 * 
 * @author Sergio Vaca
 *
 */
public class SegPerfilFacultadAction {
		private Contexto contexto = new Contexto();
		private SegPerfilBusiness segPerfilBusiness;
		private Bitacora bitacoraDao = new Bitacora();
		private SegPerfilFacultadBusiness segPerfilFacultadBusiness;
		private GlobalSingleton globalSingleton;
		
		@DirectMethod
		public Map<String, Object> verificarFacultad(String idComponente) {
			//boolean r = false;		
			Map<String, Object> result = new HashMap<>();  
			result.put("conn", "");
			result.put("excep", "");
			result.put("facultad", false);
			
			try{
				globalSingleton = GlobalSingleton.getInstancia();
				segPerfilBusiness = (SegPerfilBusiness)contexto.obtenerBean("segPerfilBusiness");
				
				result = segPerfilBusiness.verificarFacultad(idComponente, globalSingleton.getUsuarioLoginDto().getIdUsuario());
			}catch(Exception e){
				bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
						+ "P:Seg, C:SegPerfilFacultadAction, M:verificarFacultad");
			}
			return result;
		}
		
		@DirectMethod
		public List<SegPerfilDto> llenarComboPerfiles(int id){
			List<SegPerfilDto> perfiles = null;
			segPerfilBusiness = (SegPerfilBusiness)contexto.obtenerBean("segPerfilBusiness");
			try{
				perfiles = segPerfilBusiness.llenarCombo(id);
			}catch(Exception e){
				bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
						+ "P:Seg, C:SegPerfilFacultadAction, M:llenarComboFacultades");
			}
			return perfiles;
		}
		
		@DirectMethod
		public List<SegPerfilFacultadDto> seleccionarFacultades(int idPerfil, boolean ban){
			List<SegPerfilFacultadDto> facultades = null;
			segPerfilFacultadBusiness = (SegPerfilFacultadBusiness)contexto.obtenerBean("segPerfilFacultadBusiness");
			try{
				SegPerfilFacultadDto segPerfilFacultadDto = new SegPerfilFacultadDto();
				segPerfilFacultadDto.setIdPerfil(idPerfil);
				facultades = segPerfilFacultadBusiness.consultar(segPerfilFacultadDto, ban);
			}catch(Exception e){
				bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
						+ "P:Seg, C:SegPerfilFacultadAction, M:seleccionarComponentes");
			}
			return facultades;
		}
		
		@DirectMethod
		public boolean asignarTodas(int idPerfil){
			int i=-1;
			SegPerfilFacultadDto facultad = new SegPerfilFacultadDto();
			facultad.setIdPerfil(idPerfil);
			segPerfilFacultadBusiness = (SegPerfilFacultadBusiness)contexto.obtenerBean("segPerfilFacultadBusiness");
			try{
				i = segPerfilFacultadBusiness.insertar(facultad);
			}catch(Exception e){
				bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
						+ "P:Seg, C:SegPerfilFacultadAction, M:asignarTodas");
			}
			return (i>0);
		}
		
		@DirectMethod
		public Retorno asignarUna(String dato){
			int i=-1;
			int idPerfil = Integer.parseInt(dato.substring(0, dato.indexOf(",")));
			int idFacultad = Integer.parseInt(dato.substring(dato.indexOf(",")+1));
			Retorno retorno = new Retorno();
			SegPerfilFacultadDto facultad = new SegPerfilFacultadDto();
			facultad.setIdPerfil(idPerfil);
			facultad.setIdFacultad(idFacultad);
			retorno.setId(idPerfil);
			retorno.setIdA(idFacultad);
			segPerfilFacultadBusiness = (SegPerfilFacultadBusiness)contexto.obtenerBean("segPerfilFacultadBusiness");
			try{
				i = segPerfilFacultadBusiness.insertar(facultad);
			}catch(Exception e){
				bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
						+ "P:Seg, C:SegPerfilFacultadAction, M:asignarUna");
			}
			retorno.setResultado(i>0);
			return retorno;
		}
		
		@DirectMethod
		public Retorno eliminarUna(String dato){
			int i=-1;
			int idPerfil = Integer.parseInt(dato.substring(0, dato.indexOf(",")));
			int idFacultad = Integer.parseInt(dato.substring(dato.indexOf(",")+1));
			Retorno retorno = new Retorno();
			SegPerfilFacultadDto facultad = new SegPerfilFacultadDto();
			facultad.setIdPerfil(idPerfil);
			facultad.setIdFacultad(idFacultad);
			retorno.setId(idPerfil);
			retorno.setIdA(idFacultad);
			segPerfilFacultadBusiness = (SegPerfilFacultadBusiness)contexto.obtenerBean("segPerfilFacultadBusiness");
			try{
				i = segPerfilFacultadBusiness.eliminar(facultad);
			}catch(Exception e){
				bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
						+ "P:Seg, C:SegPerfilFacultadAction, M:asignarUna");
			}
			retorno.setResultado(i>0);
			return retorno;
		}
		
		@DirectMethod
		public boolean eliminarTodas(int idPerfil){
			int i=-1;
			SegPerfilFacultadDto facultad = new SegPerfilFacultadDto();
			facultad.setIdPerfil(idPerfil);
			segPerfilFacultadBusiness = (SegPerfilFacultadBusiness)contexto.obtenerBean("segPerfilFacultadBusiness");
			try{
				i = segPerfilFacultadBusiness.eliminar(facultad);
			}catch(Exception e){
				bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
						+ "P:Seg, C:SegPerfilFacultadAction, M:eliminarTodas");
			}
			return (i>0);
		}
}
