package com.webset.set.seguridad.action;

//UTIL
import java.util.Date;
import java.util.List;

// EXTJS DIRECT 
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;

//SET APP
import com.webset.set.seguridad.business.SegFacultadBusiness;
import com.webset.set.seguridad.business.SegFacultadComponenteBusiness;
import com.webset.set.seguridad.dto.SegFacultadComponenteDto;
import com.webset.set.seguridad.dto.SegFacultadDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.dto.Retorno;

/**
 * 
 * @author Sergio Vaca
 *
 */
public class SegFacultadComponenteAction {
	//private static Logger log = Logger.getLogger(SegFacultadComponenteAction.class);
	private Contexto contexto = new Contexto();
	private SegFacultadBusiness segFacultadBusiness;
	private SegFacultadComponenteBusiness segFacultadComponenteBusiness;
	private Bitacora bitacoraDao = new Bitacora();
	
	@DirectMethod
	public List<SegFacultadDto> llenarComboFacultades(int id){
		List<SegFacultadDto> facultades = null;
		segFacultadBusiness = (SegFacultadBusiness)contexto.obtenerBean("segFacultadBusiness");
		try{
			facultades = segFacultadBusiness.llenarCombo(id);
		}catch(Exception e){
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seg, C:SegFacultadComponenteAction, M:llenarComboFacultades");
		}
		return facultades;
	}
	
	@DirectMethod
	public List<SegFacultadComponenteDto> seleccionarComponentes(int idFacultad, boolean ban){
		List<SegFacultadComponenteDto> componentes = null;
		segFacultadComponenteBusiness = (SegFacultadComponenteBusiness)contexto.obtenerBean("segFacultadComponenteBusiness");
		try{
			SegFacultadComponenteDto segFacultadComponenteDto = new SegFacultadComponenteDto();
			segFacultadComponenteDto.setIdFacultad(idFacultad);
			componentes = segFacultadComponenteBusiness.consultar(segFacultadComponenteDto, ban);
		}catch(Exception e){
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seg, C:SegFacultadComponenteAction, M:seleccionarComponentes");
		}
		return componentes;
	}
	
	@DirectMethod
	public boolean asignarTodas(int idFacultad){
		int i=-1;
		SegFacultadComponenteDto facultad = new SegFacultadComponenteDto();
		facultad.setIdFacultad(idFacultad);
		segFacultadComponenteBusiness = (SegFacultadComponenteBusiness)contexto.obtenerBean("segFacultadComponenteBusiness");
		try{
			i = segFacultadComponenteBusiness.insertar(facultad);
		}catch(Exception e){
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seg, C:SegFacultadComponenteAction, M:asignarTodas");
		}
		return (i>0);
	}
	
	@DirectMethod
	public Retorno asignarUna(String dato){
		int i=-1;
		int idFacultad = Integer.parseInt(dato.substring(0, dato.indexOf(",")));
		int idComponente = Integer.parseInt(dato.substring(dato.indexOf(",")+1));
		Retorno retorno = new Retorno();
		SegFacultadComponenteDto facultad = new SegFacultadComponenteDto();
		facultad.setIdFacultad(idFacultad);
		facultad.setIdComponente(idComponente);
		retorno.setId(idFacultad);
		retorno.setIdA(idComponente);
		segFacultadComponenteBusiness = (SegFacultadComponenteBusiness)contexto.obtenerBean("segFacultadComponenteBusiness");
		try{
			i = segFacultadComponenteBusiness.insertar(facultad);
		}catch(Exception e){
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seg, C:SegFacultadComponenteAction, M:asignarUna");
		}
		retorno.setResultado(i>0);
		return retorno;
	}
	
	@DirectMethod
	public Retorno eliminarUna(String dato){
		int i=-1;
		int idFacultad = Integer.parseInt(dato.substring(0, dato.indexOf(",")));
		int idComponente = Integer.parseInt(dato.substring(dato.indexOf(",")+1));
		SegFacultadComponenteDto facultad = new SegFacultadComponenteDto();
		Retorno retorno = new Retorno();
		retorno.setId(idFacultad);
		retorno.setIdA(idComponente);
		facultad.setIdFacultad(idFacultad);
		facultad.setIdComponente(idComponente);
		segFacultadComponenteBusiness = (SegFacultadComponenteBusiness)contexto.obtenerBean("segFacultadComponenteBusiness");
		try{
			i = segFacultadComponenteBusiness.eliminar(facultad);
		}catch(Exception e){
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seg, C:SegFacultadComponenteAction, M:asignarUna");
		}
		retorno.setResultado(i>0);
		return retorno;
	}
	
	@DirectMethod
	public boolean eliminarTodas(int idFacultad){
		int i=-1;
		SegFacultadComponenteDto facultad = new SegFacultadComponenteDto();
		facultad.setIdFacultad(idFacultad);
		segFacultadComponenteBusiness = (SegFacultadComponenteBusiness)contexto.obtenerBean("segFacultadComponenteBusiness");
		try{
			i = segFacultadComponenteBusiness.eliminar(facultad);
		}catch(Exception e){
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seg, C:SegFacultadComponenteAction, M:eliminarTodas");
		}
		return (i>0);
	}

}
