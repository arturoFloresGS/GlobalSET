package com.webset.set.utilerias.business.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.webset.set.reportes.business.ReportesBusiness;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dao.MantenimientoConceptosDao;
import com.webset.set.utilerias.service.MantenimientoConceptosService;
import com.webset.set.utileriasmod.dto.MantenimientoConceptosDto;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

public class MantenimientoConceptosBusinessImpl implements MantenimientoConceptosService{
	MantenimientoConceptosDao objMantenimientoConceptosDao;
	private static Logger logger = Logger.getLogger(MantenimientoConceptosBusinessImpl.class);
	MantenimientoConceptosDto objMantenimientoConceptosDto = new MantenimientoConceptosDto();
	
	Bitacora bitacora;
	int recibeEntero = 0;
	////inicio del metodo para obtener los datos del dao este metodo tendra que estar declarado en la interfaz MantenimientoConceptosDao
	public JRDataSource obtenerDatosConcepto(String nomReporte, Map<String, Object> parameters){
		JRMapArrayDataSource  jrDataSource = null; 
	    XStream xStream = new XStream(new DomDriver());
	    List<Map<String, Object>> resMap = null;
	    String resXml = "";
		try{

			
			resMap = objMantenimientoConceptosDao.ejecutarReporteConcepto(parameters);
			logger.info("***resMap="+resMap);
			System.out.println(resMap);
			// convert to XML 
			//xStream.alias("map", java.util.Map.class);
			xStream.alias("map",  java.util.List.class);
			resXml = xStream.toXML(resMap.toArray());
			//logger.info("***xml="+resXml);
			
			String [] cadenas = new String[2];
            jrDataSource = new JRMapArrayDataSource(resMap.toArray());
			//xmlDataSource = new JRXmlDataSource(resXml, "/");

		}catch(Exception e){
			//bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:SET, C:ReportesAction, M:obtenerXMLReporte");
		}
		return jrDataSource;
	}
	//fin/
	List<MantenimientoConceptosDto> recibeDatos = new ArrayList<MantenimientoConceptosDto>();
	
	public List<MantenimientoConceptosDto> llenaBancos(){	
		return objMantenimientoConceptosDao.llenaBancos();
	}
	
	public List<MantenimientoConceptosDto> llenaFormaPago(String ingresoEgreso){
		return objMantenimientoConceptosDao.llenaFormaPago(ingresoEgreso);
	}
	
	public List<MantenimientoConceptosDto> llenaGrid(int idBanco, String conceptoBanco, String formaPago, String ingresoEgreso){
		return objMantenimientoConceptosDao.llenaGrid(idBanco, conceptoBanco, formaPago, ingresoEgreso);			
	}
	
	public String aceptar (List<Map<String, String>> registro){
		String mensaje = "";

		try{						
			//Se valida primero la informacion para cuando se inserta un nuevo registro
			if (registro.get(0).get("modificar").equals("insertar"))
			{				
				if (!registro.get(0).get("idBanco").equals("") && !registro.get(0).get("idBanco").equals("0"))
					objMantenimientoConceptosDto.setIdBanco(Integer.parseInt(registro.get(0).get("idBanco")));
				else
					return "Debe de seleccionar un Banco";
				
				if (registro.get(0).get("idBanco").equals("12")) {
					if (!registro.get(0).get("cveConcepto").equals(""))
						objMantenimientoConceptosDto.setCveConcepto(registro.get(0).get("cveConcepto"));
					else
						return "Debe ingresar una clave de concepto para bancomer";
				}else
					objMantenimientoConceptosDto.setCveConcepto("");
				
				if (!registro.get(0).get("conceptoSet").equals("") && !registro.get(0).get("conceptoSet").equals("0"))
					objMantenimientoConceptosDto.setConceptoSet(registro.get(0).get("conceptoSet"));
				else
					return "Debe de seleccionar una Forma de Pago";
				
				if (!registro.get(0).get("conceptoBanco").equals("") && !registro.get(0).get("conceptoBanco").equals("0"))
					objMantenimientoConceptosDto.setConceptoBanco(registro.get(0).get("conceptoBanco"));
				else
					return "Ingrese el Concepto";
				
				if (!registro.get(0).get("clasChequera").equals("") && !registro.get(0).get("clasChequera").equals("0"))
					objMantenimientoConceptosDto.setClasChequera(registro.get(0).get("clasChequera"));
				else
					return "Por favor verifique que este concepto no sea importable, ya que de lo contrario tendra que seleccionar al menos un tipo de chequera a afectar";
				
				objMantenimientoConceptosDto.setCargoAbono(registro.get(0).get("ingresoEgreso"));
				objMantenimientoConceptosDto.setDespliega(registro.get(0).get("despliega"));
				objMantenimientoConceptosDto.setImporta(registro.get(0).get("importa"));
				objMantenimientoConceptosDto.setRechazo(registro.get(0).get("rechazo"));
				objMantenimientoConceptosDto.setCargoCuenta(registro.get(0).get("cargoCuenta"));
				
				//Se valida que no este dado de alta ya el concepto en equivale_concepto				
				recibeDatos = objMantenimientoConceptosDao.buscaConcepto(objMantenimientoConceptosDto);
				
				if (recibeDatos.size() > 0)
					mensaje = "Este concepto ya existe dado de alta para este Banco";
				else {
					//Se valida que este dada la alta la forma de pago 
					//Si existe la forma de pago se obtiene el valor del SBC y el concepto_Set
					recibeDatos = objMantenimientoConceptosDao.buscaFormaPago(objMantenimientoConceptosDto);
					if (recibeDatos.size() > 0){						
						//Se inserta el registro en equivale_concepto
						objMantenimientoConceptosDto.setConceptoSet(recibeDatos.get(0).getConceptoSet());
						objMantenimientoConceptosDto.setSbc(recibeDatos.get(0).getSbc());
						
						recibeEntero = objMantenimientoConceptosDao.insertaConcepto(objMantenimientoConceptosDto);
						
						if (recibeEntero > 0)
							mensaje = "El concepto fue dado de alta con exito";
						else
							mensaje = "Ocurrion un problema durante la alta del concepto";
					}					
				}
			}else {
				//Se actualiza la informacion del concepto
				objMantenimientoConceptosDto.setIdBanco(Integer.parseInt(registro.get(0).get("idBanco")));
				
				if (registro.get(0).get("idBanco").equals("12")) {
					if (!registro.get(0).get("cveConcepto").equals(""))
						objMantenimientoConceptosDto.setCveConcepto(registro.get(0).get("cveConcepto"));
					else
						return "Debe ingresar una clave de concepto para bancomer";
				}else
					objMantenimientoConceptosDto.setCveConcepto("");
				
				objMantenimientoConceptosDto.setConceptoBanco(registro.get(0).get("conceptoBanco"));
				objMantenimientoConceptosDto.setConceptoSet(registro.get(0).get("conceptoSet"));
				objMantenimientoConceptosDto.setCargoAbono(registro.get(0).get("ingresoEgreso"));
				objMantenimientoConceptosDto.setDespliega(registro.get(0).get("despliega"));
				objMantenimientoConceptosDto.setImporta(registro.get(0).get("importa"));
				objMantenimientoConceptosDto.setCargoCuenta(registro.get(0).get("cargoCuenta"));
				objMantenimientoConceptosDto.setRechazo(registro.get(0).get("rechazo"));
				objMantenimientoConceptosDto.setClasChequera(registro.get(0).get("clasChequera"));
				
				recibeEntero = objMantenimientoConceptosDao.actualizaConcepto(objMantenimientoConceptosDto);
				
				if(recibeEntero > 0)
					mensaje = "El concepto se actualizo correctamente";
				else
					mensaje = "Ocurrio un error en la actualizaci�n";
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C:MantenimientoConceptosBusinessImpl");
		}
		return mensaje;
	}
	
	public int eliminaConcepto (int idBanco, String conceptoBanco, String ingresoEgreso){
		return objMantenimientoConceptosDao.eliminaConcepto(idBanco, conceptoBanco, ingresoEgreso);
	}
	
	public List<MantenimientoConceptosDto> llenaTipoOperacion (){		
		return objMantenimientoConceptosDao.llenaTipoOperacion();
	}
//************** 
	public MantenimientoConceptosDao getObjMantenimientoConceptosDao() {
		return objMantenimientoConceptosDao;
	}

	public void setObjMantenimientoConceptosDao(
			MantenimientoConceptosDao objMantenimientoConceptosDao) {
		this.objMantenimientoConceptosDao = objMantenimientoConceptosDao;
	}

}
