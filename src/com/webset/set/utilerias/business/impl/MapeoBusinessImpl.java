package com.webset.set.utilerias.business.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dao.MapeoDao;
import com.webset.set.utilerias.dto.MapeoDto;
import com.webset.set.utilerias.service.MapeoService;
import com.webset.utils.tools.Utilerias;



public class MapeoBusinessImpl<objMapeoDao> implements MapeoService{
	
	private MapeoDao objMapeoDao;
	

	Bitacora bitacora;
	Funciones funciones = new Funciones();
	MapeoDto objMapeoDto = new MapeoDto();
	ConsultasGenerales consultasGenerales;
	List<MapeoDto> recibeDatos = new ArrayList<MapeoDto>();
	int recibeResultadoEntero = 0;
	

	
	public List<MapeoDto> llenaGrid(){
		List<MapeoDto> resultado = new ArrayList<MapeoDto>();
		try {
			resultado= objMapeoDao.llenaGrid();
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoBusinessImpl, M: llenaGrid");
		}return resultado;
		
	}
	
	public List<MapeoDto> obtenerChequeras(String idBanco, int secuencia) {
		List<MapeoDto> resultado = new ArrayList<MapeoDto>();

		try {
			resultado = objMapeoDao.obtenerChequeras(idBanco, secuencia);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoBusinessImpl, M: obtenerChequeras");
		
		}return resultado;
	}
	
	
	
	public List<MapeoDto> obtenerChequerasM(String idBanco,String secuencia) {
		
		List<MapeoDto> resultado = new ArrayList<MapeoDto>();

		try {
			resultado = objMapeoDao.obtenerChequerasM(idBanco,secuencia);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoBusinessImpl, M: obtenerChequerasM");
		
		}return resultado;
	
	}
	
	public List<MapeoDto> obtenerChequerasAV(String idBanco, int secuencia) {
		List<MapeoDto> resultado = new ArrayList<MapeoDto>();

		try {
			resultado = objMapeoDao.obtenerChequerasAV(idBanco, secuencia);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoBusinessImpl, M: obtenerChequerasAV");
		
		}return resultado;
		
	
	}
	
	public int asignarChequeras(int idBanco, int idChequera, boolean todos) {
		try {
			return objMapeoDao.asignarChequeras(idBanco, idChequera, todos);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:MapeoBusinessImpl, M:asignarChequeras");
			return 0;
		}
	}
	
	
	
	public List<MapeoDto> llenaPoliza(){
		
		List<MapeoDto> resultado = new ArrayList<MapeoDto>();

		try {
			resultado = objMapeoDao.llenaPoliza();
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoBusinessImpl, M: llenaPoliza");
		
		}return resultado;
		
	}
	
	public List<MapeoDto> llenaGrupo(String poliza){
		
		List<MapeoDto> resultado = new ArrayList<MapeoDto>();

		try {
			resultado =  objMapeoDao.llenaGrupo(poliza);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoBusinessImpl, M: llenaGrupo");
		
		}return resultado;
		
	}
	
	public List<MapeoDto> llenaRubro(String poliza){
		System.out.println("llego bussines");
		List<MapeoDto> resultado = new ArrayList<MapeoDto>();

		try {
			resultado = objMapeoDao.llenaRubro(poliza);

		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoBusinessImpl, M: llenaRubro");
		
		}return resultado;
	}
	
	public List<MapeoDto> llenaBanco(){
		List<MapeoDto> resultado = new ArrayList<MapeoDto>();

		try {
			resultado = objMapeoDao.llenaBanco();

		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoBusinessImpl, M: llenaBanco");
		
		}return resultado;
		
	}
	
	public MapeoDao getObjMapeoDao() {
		return objMapeoDao;
	}

	public void setObjMapeoDao(MapeoDao objMapeoDao) {
		this.objMapeoDao = objMapeoDao;
	}
	
	@Override
	public String actualizaMapeo(List<Map<String, String>> registro, String idSecuencia) {
		String mensaje="";
		String resultado="";
		
		try {
			
		
		if (registro.get(0).get("idPoliza").equals(""))
		mensaje = "Falta la poliza";				
		
		if (registro.get(0).get("idGrupo").equals(""))
		mensaje = "Falta el grupo";
	
		if (registro.get(0).get("idRubro").equals(""))
		mensaje = "Falta el Rubro";
	
		if (registro.get(0).get("idBanco").equals(""))
		mensaje = "Falta Banco";
		
		MapeoDto objMapeoDto = new MapeoDto();
		objMapeoDto.setIdPoliza((registro.get(0).get("idPoliza")));
		objMapeoDto.setIdGrupo((registro.get(0).get("idGrupo")));
		objMapeoDto.setIdRubro((registro.get(0).get("idRubro")));
		objMapeoDto.setIdBanco((registro.get(0).get("idBanco")));
		objMapeoDto.setReferencia(registro.get(0).get("referencia")==null || registro.get(0).get("referencia").equals("")
					? "": registro.get(0).get("referencia"));
		objMapeoDto.setConcepto(registro.get(0).get("concepto")==null || registro.get(0).get("concepto").equals("")
				? "": registro.get(0).get("concepto"));
		objMapeoDto.setDescripcion(registro.get(0).get("descripcion")==null || registro.get(0).get("descripcion").equals("")
				? "": registro.get(0).get("descripcion"));
		objMapeoDto.setObservacion(registro.get(0).get("observacion")==null || registro.get(0).get("observacion").equals("")
				? "": registro.get(0).get("observacion"));
		objMapeoDto.setTipo(registro.get(0).get("tipo")==null || registro.get(0).get("tipo").equals("")
				? "": registro.get(0).get("tipo"));
		objMapeoDto.setEspecial(registro.get(0).get("especial")==null || registro.get(0).get("especial").equals("")
				? "": registro.get(0).get("especial"));
		objMapeoDto.setActivo(registro.get(0).get("activo")==null || registro.get(0).get("activo").equals("")
				? "": registro.get(0).get("activo"));
		
		resultado = objMapeoDao.actualizaMapeo(objMapeoDto, idSecuencia);
		
			if (resultado == "Exito al modificar el mapeo") {
				
				mensaje = "Mapeo modificado correctamente";

			}else{
				mensaje = "Error al insertar la cabecera";
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoBusinessImpl, M: actualizaMapeo");
		
		}return mensaje;
	}

	@Override
	public String insertaMapeo(List<Map<String, String>> registro,List<Map<String, String>> chequeras, String idSecuencia) {
		String mensaje="";
		String resultado="";
		
		try {
		
		if (registro.get(0).get("idPoliza").equals(""))
		mensaje = "Falta la poliza";				
		
		if (registro.get(0).get("idGrupo").equals(""))
		mensaje = "Falta el grupo";
	
		if (registro.get(0).get("idRubro").equals(""))
		mensaje = "Falta el Rubro";
	
		if (registro.get(0).get("idBanco").equals(""))
		mensaje = "Falta Banco";
		
		MapeoDto objMapeoDto = new MapeoDto();
		objMapeoDto.setIdPoliza((registro.get(0).get("idPoliza")));
		objMapeoDto.setIdGrupo((registro.get(0).get("idGrupo")));
		objMapeoDto.setIdRubro((registro.get(0).get("idRubro")));
		objMapeoDto.setIdBanco((registro.get(0).get("idBanco")));
		objMapeoDto.setReferencia(registro.get(0).get("referencia")==null || registro.get(0).get("referencia").equals("")
					? "": registro.get(0).get("referencia"));
		objMapeoDto.setConcepto(registro.get(0).get("concepto")==null || registro.get(0).get("concepto").equals("")
				? "": registro.get(0).get("concepto"));
		objMapeoDto.setDescripcion(registro.get(0).get("descripcion")==null || registro.get(0).get("descripcion").equals("")
				? "": registro.get(0).get("descripcion"));
		objMapeoDto.setObservacion(registro.get(0).get("observacion")==null || registro.get(0).get("observacion").equals("")
				? "": registro.get(0).get("observacion"));
		objMapeoDto.setTipo(registro.get(0).get("tipo")==null || registro.get(0).get("tipo").equals("")
				? "": registro.get(0).get("tipo"));
		objMapeoDto.setEspecial(registro.get(0).get("especial")==null || registro.get(0).get("especial").equals("")
				? "": registro.get(0).get("especial"));
		objMapeoDto.setActivo(registro.get(0).get("activo")==null || registro.get(0).get("activo").equals("")
				? "": registro.get(0).get("activo"));
		
		resultado = objMapeoDao.insertaMapeo(objMapeoDto, chequeras,idSecuencia);
		
			if(resultado == "Exito al insertar la chequera"){
				mensaje = "Mapeo Guardado Correctamente";
			}else{
				mensaje= "Error al Guardar el Mapeo";
		
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoBusinessImpl, M: insertaMapeo");
		}return mensaje;
	}
	
	public List<MapeoDto> obtieneDatos(String idPoliza, String idGrupo, String idRubro, String idBanco){
		List<MapeoDto> result = new ArrayList<MapeoDto>();
		try {
			result = objMapeoDao.obtieneDatos(idPoliza, idGrupo, idRubro, idBanco);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Utilerias, C: MapeoBusinessImpl, M: obtieneDatos");
		}
		return result;
	}

	
	@Override
	public HSSFWorkbook reporteMapeo() {
		HSSFWorkbook hb=null;
		try {
			hb=Utilerias.generarExcel(new String[]{
					"Secuencia",
					"Poliza",
					"Grupo Rubro",
					"Rubro",
					"Id Banco",	
					"Banco",
					"Id Chequera",
					"Descripci�n Chequera",
					"Referencia",
					"Concepto",
					"Descripci�n",
					"Observaci�n",
					"Tipo",
					"Especial",
					"Activo"
					
					
						
			}, 
					objMapeoDao.reporteMapeo(), 
					new String[]{
							"secuencia",
							"idPoliza",
							"idGrupo",
							"idRubro",
							"idBanco",
							"descBanco",
							"idChequera",
							"descChequera",
							"referencia",
							"concepto",
							"descripcion",
							"observacion",
							"tipo",
							"especial",
							"activo"
							
							
							
					});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoBusinessImpl, M: reporteMapeo");
		}return hb;
	}

	@Override
	public String retornaIdMapeo(MapeoDto objMapeoDto) {
		String retorno="";
		try {
			
			retorno=objMapeoDao.retornaIdMapeo(objMapeoDto);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoBusinessImpl, M: retornoIdMapeo");

		}
		return retorno;
	}


	@Override
	public List<MapeoDto> verificaRegistro(int secuencia) {
	
			List<MapeoDto> result = new ArrayList<MapeoDto>();
			try {
				result = objMapeoDao.verificaRegistro(secuencia);
			} catch (Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
						"P: Utilerias, C: MapeoBusinessImpl, M: verificaRegistro");
			}
			return result;
		
	}
	
	public String inhabilitaMapeo (int secuencia){
		String mensaje = "";
		int recibeDatoEntero = 0;
		try{
			recibeDatoEntero = objMapeoDao.inhabilitaMapeo(secuencia);
			
			if (recibeDatoEntero > 0)
				mensaje = "Se elimino el mapeo correctamente";
			else
				mensaje = "Ocurrio un error durante la eliminaci�n";
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoBusinessImpl, M: inhabilitaMapeo");
		}return mensaje;
	}

	

	@Override
	public void agregarChequerasAV(int secuencia, String chequeras, String banco) {
		
		try {
		 objMapeoDao.agregarChequerasAV( secuencia,chequeras,banco);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoBusinessImpl, M: agregarChequerasAV");
		}
	}

	@Override
	public void eliminarChequerasAV(int secuencia, String chequeras, String banco) {
		try {
		 objMapeoDao.eliminarChequerasAV( secuencia,chequeras,banco);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoBusinessImpl, M: eliminarChequerasAV");
		}

	}

	@Override
	public String inhabilitaPersona(int secuencia) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String eliminaChequera(int secuencia) {
		String mensaje = "";
		int recibeDatoEntero = 0;
		try{
			recibeDatoEntero = objMapeoDao.eliminaChequera(secuencia);
			
			if (recibeDatoEntero > 0)
				mensaje = "Se elimino el mapeo correctamente";
			else
				mensaje = "Ocurrio un error durante la eliminaci�n";
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoBusinessImpl, M: eliminaChequera");
		}return mensaje;
	}

	public int asignarChequeraT(int secuencia, boolean todos) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void agregarTodasM(String idBanco, String secuencia) {
		try {
			 objMapeoDao.agregarTodasM(idBanco,secuencia);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoBusinessImpl, M: agregarTodasM");

		}
		
	}

	@Override
	public void quitarTodasM(String secuencia) {
		try {
			 objMapeoDao.quitarTodasM(secuencia);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoBusinessImpl, M: quitarTodasM");

		}
		
		
	}

	

	
	

	

	


}
