package com.webset.set.impresion.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.egresos.dto.BloqueoPagoCruzadoDto;
import com.webset.set.impresion.dao.ControlChequesDao;
import com.webset.set.impresion.dto.ControlPapelDto;
import com.webset.set.impresion.service.ControlChequesService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.SeleccionAutomaticaGrupoDto;
import com.webset.utils.tools.Utilerias;

/**
 * @author Eric Medina Serrato
 * @since 21/12/2015 (Casi navidad :D)
 */


public class ControlChequesBusinessImpl implements ControlChequesService {

	private ControlChequesDao controlChequesDao;
	private Bitacora bitacora =  new Bitacora();
	
	public List<LlenaComboEmpresasDto>llenarComboEmpresa(){
		return controlChequesDao.llenarComboEmpresa();
	}

	/**************************************************************************************/
	public ControlChequesDao getControlChequesDao() {
		return controlChequesDao;
	}

	public void setControlChequesDao(ControlChequesDao controlChequesDao) {
		this.controlChequesDao = controlChequesDao;
	}
	/***************************************************************************************/

	@Override
	public List<LlenaComboGralDto> llenarComboBanco() {
		List<LlenaComboGralDto> list = new ArrayList<>();
		
		try{
			list = controlChequesDao.llenarComboBanco();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:llenarComboBancoPagBenef");	
		}
		return list;
	}

	@Override
	public List<LlenaComboGralDto> llenarComboChequera(int idBanco, int noEmpresa) {
		return controlChequesDao.llenarComboChequera(idBanco, noEmpresa);
	}

	@Override
	public Map<String, Object> guardarControlCheque(ControlPapelDto controlPapel) {
		
		Map<String, Object> result = new HashMap<>();
		result.put("error", "");
		result.put("mensaje", "");
		
		try{
			
			if(controlPapel == null ){
				result.put("error","Error no hay datos para guardar.");
				return result;
			}
			
			/*Revisamos si ya existe alguna chequera registrada, en caso afirmativo revisar si el folio_ini (Nuevo) 
				es el consecutivo de folio_fin (viejo), caso afirmativo se pondra como inactivo el registro de la chequera vieja.
			*/
			List<ControlPapelDto> list = new ArrayList<ControlPapelDto>();
			
			if(controlPapel.getTipoFolio().equals("P")){ 
				list = controlChequesDao.existeChequeraControlPapel(controlPapel, 1);
			}else if(controlPapel.getTipoFolio().equals("C")){
				list = controlChequesDao.existeChequeraControlPapel(controlPapel, 2);
			}
			
			if(list.size() > 0 && controlPapel.getTipoFolio().equals("C")){
				int folioFin = list.get(0).getFolioInvFin();
				if(controlPapel.getFolioInvIni() == (folioFin+1)){
					
					int cont = controlChequesDao.existeChequeraControlPapelInactivo(controlPapel,2);
					if(cont>0){
						result.put("error","Error, ya existe la chequera con los folios indicados.");
					}else{
						result = controlChequesDao.guardarControlCheque(controlPapel,2);
					}
					
				}else{
					result.put("error","La chequera ya existe y el folio inicial no coincide con el consecutivo del folio final de la chequera existente.");
				}
			}else if(list.size() > 0 && controlPapel.getTipoFolio().equals("P")){
				
				int folioFin = list.get(0).getFolioInvFin();
				if(controlPapel.getFolioInvIni() == (folioFin+1)){
					
					int cont = controlChequesDao.existeChequeraControlPapelInactivo(controlPapel,1);
					if(cont>0){
						result.put("error","Error, ya existe la chequera con los folios indicados.");
					}else{
						result = controlChequesDao.guardarControlCheque(controlPapel,2);
					}
					
				}else{
					result.put("error","La chequera ya existe y el folio inicial no coincide con el consecutivo del folio final de la chequera existente.");
				}
				
				
			}else{
				result = controlChequesDao.guardarControlCheque(controlPapel,1);
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresión, C:ControlChequesBusinessImpl, M:guardarControlCheque");	
		}
		
		return result;
	}

	@Override
	public List<ControlPapelDto> consultarCheques(ControlPapelDto controlPapel, boolean folioPapel, boolean estatusChequeras, boolean estatusChequerasT) {
		
		List<ControlPapelDto>list= new ArrayList<ControlPapelDto>();
		
		try {
			list = controlChequesDao.consultarCheques(controlPapel, folioPapel, estatusChequeras, estatusChequerasT);

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresión, C:ControlChequesBusinessImpl, M:consultarCheques");
		}
		return list;
		
	}

	@Override
	public Map<String, Object> eliminarControlCheque(ControlPapelDto controlPapel) {
		
		Map<String, Object> result = new HashMap<>();
		result.put("error", "");
		result.put("mensaje", "");
		
		try{
			
			if(controlPapel == null ){
				result.put("error","Error no hay datos para modificar.");
				return result;
			}
			
			result = controlChequesDao.eliminarControlCheque(controlPapel);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresión, C:ControlChequesBusinessImpl, M:eliminarControlCheque");	
		}
		
		return result;
		
	}
	
	@Override
	public Map<String, Object> modificarControlCheque(ControlPapelDto controlPapelOrig, ControlPapelDto controlPapel) {
		
		Map<String, Object> result = new HashMap<>();
		result.put("error", "");
		result.put("mensaje", "");
		
		try{
			
			if(controlPapelOrig == null || controlPapel == null){
				result.put("error","Error no hay datos para modificar.");
				return result;
			}
			
			result = controlChequesDao.modificarControlCheque(controlPapelOrig, controlPapel);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresión, C:ControlChequesBusinessImpl, M:modificarControlCheque");	
		}
		return result;
	}
	
	//Exporta excel con datos recibidos de un grid en formato jsson.
	public String exportaExcel(String datos, String titulo) {
		String respuesta = "";
	    Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
	    try {	    	
			//Se crea el libro Excel
			HSSFWorkbook wb = Utilerias.generarExcel(new String[]{	//Titulos columnas
													"Banco",
										            "Chequera",
										            "Folio Inicial",
										            "Folio Final",
										            "Folio Actual",
										            "Fecha",
										            "Empresa",
										            "Stock",
										            "Tipo folio",
										            "Estatus"
												}, 
												parameters, //Datos 
												new String[]{	//nombres de los campos a leer del json enviado
														"idBanco",
														"idChequera",
														"folioIni",
														"folioFin",
														"folioActual",														
														"fecha", 
														"empresa", 
														"stock",
														"tipoFolio",
														"estatus"
												}, 
												titulo
												);			
            
            respuesta = ConstantesSet.RUTA_EXCEL + "propuestas " + Utilerias.indicadorFecha() +".xls";
            File outputFile = new File(respuesta);
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
        } catch (IOException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Impresión, C:ControlChequesBusinessImpl, M:exportaExcel");
        	respuesta = "";
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Impresión, C:ControlChequesBusinessImpl, M:exportaExcel");
            respuesta = "";
        }
        return respuesta;
	}
	
	
}
