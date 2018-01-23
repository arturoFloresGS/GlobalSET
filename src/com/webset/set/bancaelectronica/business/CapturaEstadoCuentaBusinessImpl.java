package com.webset.set.bancaelectronica.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.webset.set.bancaelectronica.dao.CapturaEstadoCuentaDao;
import com.webset.set.bancaelectronica.dto.CapturaEstadoCuentaDto;
import com.webset.set.bancaelectronica.service.CapturaEstadoCuentaService;
import com.webset.set.global.dao.GlobalDao;
import com.webset.set.personas.dto.ConsultaPersonasDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;
//import com.webset.set.utilerias.business.impl.CargasInicialesBusinessImpl;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;

public class CapturaEstadoCuentaBusinessImpl implements CapturaEstadoCuentaService{
	private static Logger logger = Logger.getLogger(CapturaEstadoCuentaBusinessImpl.class);

	CapturaEstadoCuentaDao objCapturaEstadoCuentaDao;
	GlobalDao globalDao;
	CapturaEstadoCuentaDto objCapturaEstadoCuentaDto = new CapturaEstadoCuentaDto();
	private Funciones funciones = new Funciones();
	
	@Override
	public List<LlenaComboEmpresasDto> llenaComboEmpresas(int numUsuario) {
		List<LlenaComboEmpresasDto> result = new ArrayList<LlenaComboEmpresasDto>();
		try {
			result = objCapturaEstadoCuentaDao.llenaComboEmpresas(numUsuario);
		} catch (Exception e) {
//			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
//					"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaComboTipoPersona");
		}
		return result;
	}
	@Override
	public List<CapturaEstadoCuentaDto> llenaComboTipoBanco(int numEmpresa) {
		List<CapturaEstadoCuentaDto> result = new ArrayList<CapturaEstadoCuentaDto>();
		try {
			result = objCapturaEstadoCuentaDao.llenaComboTipoBanco(numEmpresa);
		} catch (Exception e) {
//			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
//					"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaComboTipoPersona");
		}
		return result;
	}
	@Override
	public List<CapturaEstadoCuentaDto> llenaComboChequera(int numEmpresa,int numBanco) {
		List<CapturaEstadoCuentaDto> result = new ArrayList<CapturaEstadoCuentaDto>();
		try {
			result = objCapturaEstadoCuentaDao.llenaComboChequera(numEmpresa,numBanco);
		} catch (Exception e) {
//			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
//					"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaComboTipoPersona");
		}
		return result;
	}
	
	@Override
	public List<CapturaEstadoCuentaDto> llenaGrid(int numEmpresa,boolean banco,int numBanco,boolean chequera,String numChequera,boolean fecha,String fechaV) {
		System.out.println("businness");
		List<CapturaEstadoCuentaDto> result = new ArrayList<CapturaEstadoCuentaDto>();
		try {
			System.out.println("businness dd");
			result = objCapturaEstadoCuentaDao.llenaGrid(numEmpresa,banco,numBanco,chequera,numChequera,fecha,fechaV);
		} catch (Exception e) {
//			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
//					"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaComboTipoPersona");
		}
		return result;
	}
	
	@Override
	public List<CapturaEstadoCuentaDto> llenaComboConcepto(int numBanco) {
		System.out.println("businness concepto");
		List<CapturaEstadoCuentaDto> result = new ArrayList<CapturaEstadoCuentaDto>();
		try {
			System.out.println("businness conceptoo");
			result = objCapturaEstadoCuentaDao.llenaComboConcepto(numBanco);
		} catch (Exception e) {
//			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
//					"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaComboTipoPersona");
		}
		return result;
	}
	
	@Override
	public String obtenerCargo(String concepto,int numBanco) {
		String cargo="";
		System.out.println("businness obtenerconcepto");
		
		try {
			
			cargo= objCapturaEstadoCuentaDao.obtenerCargo(concepto,numBanco);
		} catch (Exception e) {
//			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
//					"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaComboTipoPersona");
		}
		return cargo;
	}
	@Override
	public String obtenerSalvoBuenCobro(int numBanco,String concepto) {
		String cargo="";
		System.out.println("businness obtener salvob");
		try {
			
			cargo= objCapturaEstadoCuentaDao.obtenerSalvoBuenCobro(numBanco,concepto);
		} catch (Exception e) {
//			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
//					"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaComboTipoPersona");
		}
		return cargo;
	}
	@Override
	public String obtenerSucursal(int numBanco,String chequera) {
		String sucursal="";
		System.out.println("businness obtener salvob");
		try {
			
			sucursal=objCapturaEstadoCuentaDao.obtenerSucursal(numBanco,chequera);
		} catch (Exception e) {
//			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
//					"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaComboTipoPersona");
		}
		return sucursal;
	}
	
	
	@Override
	public String guardarNuevoEstado(List<Map<String, String>> registro,String fecha) {
		int respuesta=0;
		String mensaje="";
		String fechaActual="";
		//String importe="";
		Calendar f = new GregorianCalendar();
		int secu=0;
		try {
			fechaActual=f.get(Calendar.DAY_OF_MONTH)+"/"+f.get(Calendar.MONTH)+"/"+f.get(Calendar.YEAR);
			//importe=funciones.ponerFormatoImporte(Double.parseDouble(registro.get(0).get("importe")));
	//		System.out.println("importe "+registro.get(0).get("importe"));
			objCapturaEstadoCuentaDto.setIdEmpresa(Integer.parseInt(registro.get(0).get("numEmpresa")));
			objCapturaEstadoCuentaDto.setIdBanco(Integer.parseInt(registro.get(0).get("numBanco")));
			objCapturaEstadoCuentaDto.setIdChequera(registro.get(0).get("chequera"));
			objCapturaEstadoCuentaDto.setSucursal(registro.get(0).get("sucursal"));
			objCapturaEstadoCuentaDto.setFolioBanco(registro.get(0).get("folio"));
			objCapturaEstadoCuentaDto.setReferencia(registro.get(0).get("referencia"));
			objCapturaEstadoCuentaDto.setCargoAbono(registro.get(0).get("cargoAbono"));
			//funciones.convertirCadenaDouble(registro.get(0).get("importe"));
			objCapturaEstadoCuentaDto.setImporte(Double.parseDouble(registro.get(0).get("importe")));
			objCapturaEstadoCuentaDto.setSalvoBuenCobro(registro.get(0).get("salvo"));
			objCapturaEstadoCuentaDto.setConceptoSet(registro.get(0).get("concepto"));
			objCapturaEstadoCuentaDto.setObservacion(registro.get(0).get("observaciones"));
			objCapturaEstadoCuentaDto.setIdRubro(Float.parseFloat(registro.get(0).get("rubro")));
			objCapturaEstadoCuentaDto.setId_cve_concepto(registro.get(0).get("cveConcepto"));
			if(registro.get(0).get("tipoOperacion").equals("MODIFICAR")){
				System.out.println("ENTRO A MODIFICAR");
				objCapturaEstadoCuentaDto.setSecuencia(Integer.parseInt(registro.get(0).get("secuencia")));
				respuesta=objCapturaEstadoCuentaDao.actualizarEstadoCuenta(objCapturaEstadoCuentaDto);
				if(respuesta>0){
					mensaje="El registro se actualizo correctamente";
				}else{
					mensaje="No se pudo actualizar el registro";
				}
				
			}else{
				if(registro.get(0).get("tipoOperacion").equals("INSERTAR")){
					System.out.println("ENTRO A INSERTAR");
					objCapturaEstadoCuentaDto.setSecuencia(globalDao.obtenerFolioReal("secuencia"));
					respuesta=objCapturaEstadoCuentaDao.guardarNuevoEstado(objCapturaEstadoCuentaDto,fecha,fechaActual);
					if(respuesta>0){
						mensaje="Registro de Estado de Cuenta exitoso";
					}else{
						mensaje="Ocurrio un error al guardar los datos";
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			System.out.println(e.toString());
//			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
//					"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaComboTipoPersona");
		}
		return mensaje;
	}
	
	@Override
	public String eliminarEstadoCuenta(int numEmpresa,int secuencia) {
		int respuesta=0;
		String mensaje="";
		String fechaActual="";
		Calendar f = new GregorianCalendar();
		int secu=0;
		try {
			respuesta=objCapturaEstadoCuentaDao.eliminarEstadoCuenta(numEmpresa,secuencia);
			if(respuesta>0){
				mensaje="El Registro de Estado de Cuenta fue eliminado";
			}else{
				mensaje="Ocurrio un error al borrar los datos";
			}
	
			
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			
//			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
//					"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaComboTipoPersona");
		}
		return mensaje;
	}
	public GlobalDao getGlobalDao() {
		return globalDao;
	}

	public void setGlobalDao(GlobalDao globalDao) {
		this.globalDao = globalDao;
	}
	public CapturaEstadoCuentaDao getObjCapturaEstadoCuentaDao() {
		return objCapturaEstadoCuentaDao;
	}
	public void setObjCapturaEstadoCuentaDao(CapturaEstadoCuentaDao objCapturaEstadoCuentaDao) {
		this.objCapturaEstadoCuentaDao = objCapturaEstadoCuentaDao;
	}
	public static Logger getLogger() {
		return logger;
	}
	public static void setLogger(Logger logger) {
		CapturaEstadoCuentaBusinessImpl.logger = logger;
	}
	
	
	

}
