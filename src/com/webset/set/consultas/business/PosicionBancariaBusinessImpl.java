package com.webset.set.consultas.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.consultas.dao.PosicionBancariaDao;
import com.webset.set.consultas.dto.PosicionBancariaDTO;
import com.webset.set.consultas.dto.UsuarioPlantilla;
import com.webset.set.consultas.service.PosicionBancariaService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.RubroRegNeg;
import com.webset.utils.tools.Utilerias;

public class PosicionBancariaBusinessImpl implements PosicionBancariaService {

	private PosicionBancariaDao posicionBancariaDao;
	private Bitacora bitacora = new Bitacora();
	private Funciones funciones = new Funciones();
	List<PosicionBancariaDTO> listFlujoEmp = new ArrayList<PosicionBancariaDTO>();
	
	private String bancos;
	private String chequeras;
	private String empresas;
	
	//Para llenar grid
	int noEmpresa = 0;
	double totalDerecho = 0 ;
	double totalI = 0;
	double totalE = 0;
	double granTotalI = 0;
	double granTotalE = 0;
	
	String sTodasEmpresas = "";
	int nEmpresas = 0;
	private int numEmpresas = 0;
	private int numCuentas = 0;
	
	List<PosicionBancariaDTO> listExport = new ArrayList<>();
	
	/****************************************************************************/
	public PosicionBancariaDao getPosicionBancariaDao() {
		return posicionBancariaDao;
	}
	public void setPosicionBancariaDao(PosicionBancariaDao posicionBancariaDao) {
		this.posicionBancariaDao = posicionBancariaDao;
	}
	/****************************************************************************/
	
	@Override
	public List<PosicionBancariaDTO> consultarCriterioSeleccion(int idCriterio) {
		
		List<PosicionBancariaDTO>list= new ArrayList<PosicionBancariaDTO>();
		
		try{
			
			list = posicionBancariaDao.consultarCriterioSeleccion(idCriterio);
			 
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Consultas, C:PosicionBancariaBusinessImpl, M:consultarCriterioSeleccion");	
		}
		return list;
	}

	@Override
	public List<LlenaComboDivisaDto>llenarComboDivisa() {
		return posicionBancariaDao.llenarComboDivisa();
	}
	
	@Override
	public List<LlenaComboGralDto> llenarComboPlantillas(String pantalla) {
		return posicionBancariaDao.llenarComboPlantillas(pantalla);
	}
	
	@Override
	public List<RubroRegNeg> consultarRubros() {
		return posicionBancariaDao.consultarRubros();
	}
	
	@Override
	public List<PosicionBancariaDTO> consultarDetallePosicion(List<String> listRubros, double importeIni, double importeFin,
															  List<String> listBancos, List<String> listChequeras,
															  List<String> listEmpresas, String divisa, 
															  String fechaIni, String fechaFin, int optConsulta, boolean bSinRubros) {
		
		List<PosicionBancariaDTO> list = new ArrayList<>();
		
		try{
			
			if(listRubros.size() == 0 && importeIni == 0 && importeFin == 0 && !bSinRubros){
				return list;
			}
			
			
			StringBuffer idBancos = new StringBuffer();
			
			/** ELIMINA LOS REGISTROS REPETIDOS	**/
			HashSet<String> hsBancos = new HashSet<>();
			hsBancos.addAll(listBancos);
			listBancos.clear();
			listBancos.addAll(hsBancos);
			/************************************/
			
			if(listBancos.size()>0){
				for(int i=0; i<listBancos.size(); i++){
						idBancos.append(""+listBancos.get(i)+",");
				}
				
				if(idBancos.length()>0){
					idBancos.delete(idBancos.length()-1, idBancos.length());
				}
			}
			
			StringBuffer idChequeras = new StringBuffer();
			HashSet<String> hsChequeras = new HashSet<>();
			hsChequeras.addAll(listChequeras);
			listChequeras.clear();
			listChequeras.addAll(hsChequeras);
			
			if(listChequeras.size()>0){
				
				for(int i=0; i<listChequeras.size(); i++){
					idChequeras.append("'"+listChequeras.get(i)+"',");
				}
				
				if(idChequeras.length()>0){
					idChequeras.delete(idChequeras.length()-1, idChequeras.length());
				}
			}
			
			StringBuffer noEmpresas = new StringBuffer();
			HashSet<String> hsEmpresas = new HashSet<>();
			hsEmpresas.addAll(listEmpresas);
			listEmpresas.clear();
			listEmpresas.addAll(hsEmpresas);
			
			if(listEmpresas.size()>0){
				
				for(int i=0; i<listEmpresas.size(); i++){
					//if(!listEmpresas.contains(listEmpresas.get(i)))
						noEmpresas.append(""+listEmpresas.get(i)+",");
				}
				
				if(noEmpresas.length()>0){
					noEmpresas.delete(noEmpresas.length()-1, noEmpresas.length());
				}
			}
			
			//CONSULTA POR RUBROS
			StringBuffer idRubros = new StringBuffer();
			
			if(listRubros.size()>0){
				
				for(int i=0; i<listRubros.size(); i++){
					idRubros.append("'"+listRubros.get(i)+"',");
				}
				
				if(idRubros.length()>0){
					idRubros.delete(idRubros.length()-1, idRubros.length());
				}
			}
			
			list = posicionBancariaDao.consultarDetallePosicion(idRubros.toString(), importeIni, importeFin, idBancos.toString(),
																idChequeras.toString(), noEmpresas.toString(),
																divisa, fechaIni, fechaFin, optConsulta, bSinRubros);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Consultas, C:PosicionBancariaBusinessImpl, M:consultarDetallePosicion");	
		}
		return list;
	}
	

	@Override
	public List<PosicionBancariaDTO> consultarDetallePosicion2(String rubro, List<String> listChequeras,
															  List<String> listEmpresas, String divisa, 
															  String fechaIni, String fechaFin, 
															  String optConsulta, String tipoMovto) {
		
		List<PosicionBancariaDTO> list = new ArrayList<>();
		
		try{
			
			StringBuffer idChequeras = new StringBuffer();
			HashSet<String> hsChequeras = new HashSet<>();
			hsChequeras.addAll(listChequeras);
			listChequeras.clear();
			listChequeras.addAll(hsChequeras);
			
			if(listChequeras.size()>0){
				
				for(int i=0; i<listChequeras.size(); i++){
					idChequeras.append("'"+listChequeras.get(i)+"',");
				}
				
				if(idChequeras.length()>0){
					idChequeras.delete(idChequeras.length()-1, idChequeras.length());
				}
			}
			
			StringBuffer noEmpresas = new StringBuffer();
			HashSet<String> hsEmpresas = new HashSet<>();
			hsEmpresas.addAll(listEmpresas);
			listEmpresas.clear();
			listEmpresas.addAll(hsEmpresas);
			
			if(listEmpresas.size()>0){
				
				for(int i=0; i<listEmpresas.size(); i++){
					//if(!listEmpresas.contains(listEmpresas.get(i)))
						noEmpresas.append(""+listEmpresas.get(i)+",");
				}
				
				if(noEmpresas.length()>0){
					noEmpresas.delete(noEmpresas.length()-1, noEmpresas.length());
				}
			}
			
			list = posicionBancariaDao.consultarDetallePosicion2(rubro, idChequeras.toString(), 
																noEmpresas.toString(), divisa, 
																fechaIni, fechaFin, optConsulta,
																tipoMovto);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Consultas, C:PosicionBancariaBusinessImpl, M:consultarDetallePosicion2");	
		}
		return list;
	}
	
	@Override
	public List<LlenaComboGralDto> obtenerGruposRubros(String idTipoMovto) {
		
		List<LlenaComboGralDto> list = new ArrayList<>(); 
		
		try{
			list = posicionBancariaDao.obtenerGruposRubros(idTipoMovto);		
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Consultas, C:PosicionBancariaBusinessImpl, M:obtenerGruposRubros");	
		}
		return list;
	}
	
	@Override
	public List<LlenaComboGralDto> llenarComboRubro(String idGrupoRubro) {
		
		List<LlenaComboGralDto> list = new ArrayList<>(); 
		
		try{
			list = posicionBancariaDao.llenarComboRubro(idGrupoRubro);		
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Consultas, C:PosicionBancariaBusinessImpl, M:llenarComboRubro");	
		}
		return list;
	}
	
	@Override
	public Map<String, String> actualizaRubros(String noFolioDet,String idGrupo, String idRubro,
												double importe, String referencia, boolean actualizaTodo ){
		
		Map<String, String> result = new HashMap<>(); 
		
		try{
			result = posicionBancariaDao.actualizaRubros(noFolioDet, idGrupo, idRubro,
														importe, referencia, actualizaTodo);		
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Consultas, C:PosicionBancariaBusinessImpl, M:actualizaRubros");	
		}
		return result;
	}
	
	public String exportaExcel(String datos, String tipoConsulta) {
		String respuesta = "";
	    Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
	    try {	    	

	    	HSSFWorkbook wb = null;
	    	
	    	if(tipoConsulta.equals("MovtoTes")){
	    		
	    		wb = Utilerias.generarExcel(new String[]{
						"Banco",
			            "Chequera",
			            "Grupo",
			            "Rubro",
			            "Fecha Valor",
			            "Fecha Operaci�n",
			            "Referencia",
			            "Concepto",
			            "Ingresos",
			            "Egresos"
					}, 
					parameters, 
					new String[]{
							"descBanco", //usuario_uno, usuario_dos -> obtiene el numero de usuario
							"idChequera",
							"idGrupo",
							"idRubro",
							"fecValor",
							"fecOperacion",														
							"referencia", 
							"concepto",
							"ingresos", 
							"egresos"
					},"RESUMEN DE MOVIMIENTOS TESORER�A");	
	    	}else if(tipoConsulta.equals("diarioTeso")){
	    		wb = Utilerias.generarExcel(new String[]{
						"Banco",
			            "Chequera",
			            "Grupo",
			            "Rubro",
			            "Fecha Valor",
			            "Fecha Operaci�n",
			            "importe",
			            "Referencia",
			            "Concepto"
					}, 
					parameters, 
					new String[]{
							"descBanco", //usuario_uno, usuario_dos -> obtiene el numero de usuario
							"idChequera",
							"idGrupo",
							"idRubro",
							"fecValor",
							"fecOperacion",
							"importe",
							"referencia", 
							"concepto"
					},"RESUMEN DE DIARIO TESORER�A");	
	    	}else if(tipoConsulta.equals("fecValorXEmp")){
	    		
	    		String titulos[] = new String[parameters.get(0).size()];
	    		String idDatos[] = new String[parameters.get(0).size()];
	    		
	    		titulos[0] = "CONCEPTO";
	    		idDatos[0] = "descripcion";
	    		
	    		for(int i=0; i< parameters.get(0).size()-1; i++){
	    				titulos[i+1] = parameters.get(0).get("col"+i);
	    				idDatos[i+1] = "col"+i;
	    		}
	    		
	    		parameters.remove(0); //Elimino los encabezados, pues ya se pasaron en la variable de "titulos".
	    		
	    		wb = Utilerias.generarExcel(titulos, 
					parameters, 
					idDatos,
					"RESUMEN DE FECHA VALOR POR EMPRESA");	
	    		
	    	}else if(tipoConsulta.equals("fecValorXCta")){
	    		
	    		String titulos[] = new String[parameters.get(0).size()];
	    		String idDatos[] = new String[parameters.get(0).size()];
	    		
	    		titulos[0] = "CONCEPTO";
	    		idDatos[0] = "descripcion";
	    		
	    		for(int i=0; i< parameters.get(0).size()-1; i++){
	    				titulos[i+1] = parameters.get(0).get("col"+i);
	    				idDatos[i+1] = "col"+i;
	    		}
	    		
	    		parameters.remove(0); //Elimino los encabezados, pues ya se pasaron en la variable de "titulos".
	    		
	    		wb = Utilerias.generarExcel(titulos, 
					parameters, 
					idDatos,
					"RESUMEN DE FECHA VALOR POR CUENTA");
	    		
	    	}else if(tipoConsulta.equals("repSaldos")){
	    		
	    		String titulos[] = new String[parameters.get(0).size()];
	    		String idDatos[] = new String[parameters.get(0).size()];
	    		
	    		titulos[0] = "BANCO";
	    		titulos[1] = "CHEQUERA";
	    		titulos[2] = "EMPRESA";
	    		titulos[3] = "DIVISA";
	    		idDatos[0] = "descBanco";
	    		idDatos[1] = "idChequera";
	    		idDatos[2] = "nomEmpresa";
	    		idDatos[3] = "idDivisa";
	    		
	    		for(int i=0; i< parameters.get(0).size()-4; i++){
	    				titulos[i+4] = parameters.get(0).get("col"+i);
	    				idDatos[i+4] = "col"+i;
	    		}
	    		
	    		parameters.remove(0); //Elimino los encabezados, pues ya se pasaron en la variable de "titulos".
	    		
	    		wb = Utilerias.generarExcel(titulos, 
					parameters, 
					idDatos,
					"REPORTE DE SALDOS");
	    		
	    	}
	    	
				
            
            respuesta = ConstantesSet.RUTA_EXCEL + "propuestas " + Utilerias.indicadorFecha() +".xls";
            File outputFile = new File(respuesta);
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
        } catch (IOException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Consultas, C:PosicionBancariaBusinessImpl, M:exportaExcel");
        	respuesta = "";
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Consultas, C:PosicionBancariaBusinessImpl, M:exportaExcel");
            respuesta = "";
        }
        return respuesta;
	}
	
	public String exportaExcelPosicion(String datos) {
		String respuesta = "";
	    Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
	    try {	    	
			//Se crea el libro Excel
			HSSFWorkbook wb = Utilerias.generarExcel(new String[]{
													"Desc. Banco",
										            "Chequera",
										            "Rubro",
										            "Desc. Rubro",
										            "Importe",
										            "Concepto",
										            "Referencia",
										            "Fecha Operaci�n",
										            "Fecha Valor",
										            "Divisa",
										            "Tipo Movto",
										            "Beneficiario",
										            "No. Docto"
												}, 
												parameters, 
												new String[]{
														"descBanco", //usuario_uno, usuario_dos -> obtiene el numero de usuario
														"idChequera",
														"idRubro",
														"descRubro",
														"importe",														
														"concepto", 
														"referencia",
														"fecOperacion", 
														"fecValor",
														"idDivisa",
														"idTipoMovto",
														"beneficiario",
														"noDocto"
												},"RESUMEN DE MOVIMIENTOS POSICI�N BANCARIA");			
            
            respuesta = ConstantesSet.RUTA_EXCEL + "propuestas " + Utilerias.indicadorFecha() +".xls";
            File outputFile = new File(respuesta);
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
        } catch (IOException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Consultas, C:PosicionBancariaBusinessImpl, M:exportaExcelPosicion");
        	respuesta = "";
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Consultas, C:PosicionBancariaBusinessImpl, M:exportaExcelPosicion");
            respuesta = "";
        }
        return respuesta;
	}
	
	@Override
	public String exportaExcelPosicion2(String datos) {
		String respuesta = "";
	    Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
	    try {	    	
			//Se crea el libro Excel
			HSSFWorkbook wb = Utilerias.generarExcel(new String[]{
													"Desc. Banco",
										            "Chequera",
										            "Empresa",
										            "Rubro",
										            "Desc. Rubro",
										            "Importe",
										            "Concepto",
										            "Referencia",
										            "Fecha Operaci�n",
										            "Fecha Valor",
										            "Divisa",
										            "Tipo Movto",
										            "Beneficiario",
										            "No. Docto"
												}, 
												parameters, 
												new String[]{
														"descBanco", //usuario_uno, usuario_dos -> obtiene el numero de usuario
														"idChequera",
														"nomEmpresa",
														"idRubro",
														"descRubro",
														"importe",														
														"concepto", 
														"referencia", 
														"fecOperacion", 
														"fecValor",
														"idDivisa",
														"idTipoMovto",
														"beneficiario",
														"noDocto"
												},"RESUMEN DE MOVIMIENTOS POSICI�N BANCARIA");			
            
            respuesta = ConstantesSet.RUTA_EXCEL + "propuestas " + Utilerias.indicadorFecha() +".xls";
            File outputFile = new File(respuesta);
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
        } catch (IOException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Consultas, C:PosicionBancariaBusinessImpl, M:exportaExcelPosicion2");
        	respuesta = "";
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Consultas, C:PosicionBancariaBusinessImpl, M:exportaExcelPosicion2");
            respuesta = "";
        }
        return respuesta;
	}
	@Override
	public List<PosicionBancariaDTO> consultarMovtoTes(List<String> listBancos, List<String> listChequeras,
															  List<String> listEmpresas,  String fechaIni, String fechaFin,
															  int seleccion, String divisa) {
		
		List<PosicionBancariaDTO> list = new ArrayList<>();
		List<PosicionBancariaDTO> nvaLista = new ArrayList<>();
		
		try{
			
			StringBuffer idBancos = new StringBuffer();
			
			/** ELIMINA LOS REGISTROS REPETIDOS	**/
			HashSet<String> hsBancos = new HashSet<>();
			hsBancos.addAll(listBancos);
			listBancos.clear();
			listBancos.addAll(hsBancos);
			/************************************/
			
			if(listBancos.size()>0){
				for(int i=0; i<listBancos.size(); i++){
						idBancos.append(""+listBancos.get(i)+",");
				}
				
				if(idBancos.length()>0){
					idBancos.delete(idBancos.length()-1, idBancos.length());
				}
			}
			
			StringBuffer idChequeras = new StringBuffer();
			HashSet<String> hsChequeras = new HashSet<>();
			hsChequeras.addAll(listChequeras);
			listChequeras.clear();
			listChequeras.addAll(hsChequeras);
			
			if(listChequeras.size()>0){
				
				for(int i=0; i<listChequeras.size(); i++){
					idChequeras.append("'"+listChequeras.get(i)+"',");
				}
				
				if(idChequeras.length()>0){
					idChequeras.delete(idChequeras.length()-1, idChequeras.length());
				}
			}
			
			StringBuffer noEmpresas = new StringBuffer();
			HashSet<String> hsEmpresas = new HashSet<>();
			hsEmpresas.addAll(listEmpresas);
			listEmpresas.clear();
			listEmpresas.addAll(hsEmpresas);
			
			if(listEmpresas.size()>0){
				
				for(int i=0; i<listEmpresas.size(); i++){
						noEmpresas.append(""+listEmpresas.get(i)+",");
				}
				
				if(noEmpresas.length()>0){
					noEmpresas.delete(noEmpresas.length()-1, noEmpresas.length());
				}
			}
			
			list = posicionBancariaDao.consultarMovtoTes(idBancos.toString(), idChequeras.toString(), noEmpresas.toString(),
																fechaIni, fechaFin, seleccion, divisa);
			
			int contador = 0;
			String rubroIni = ""; 
			String chequeraIni = ""; 
			String rubroActual = ""; 
			String chequeraActual = ""; 
			
			//double acumulado = 0;
			//double acumulado2 = 0;
			double sumI = 0;
			double sumE = 0;
			
			//Comienza el proceso de sumas y clasificacion.
			String chequera = "";
			for(PosicionBancariaDTO posicionBancaria: list){
				
				if(chequera.equals("")){
					chequera = posicionBancaria.getIdChequera();
					PosicionBancariaDTO pos = new PosicionBancariaDTO();
					pos.setIdChequera(posicionBancaria.getNomEmpresa());
					pos.setColor("#8888FF");
					nvaLista.add(pos);
				}
	
				if(contador == 0){
					rubroIni = posicionBancaria.getIdGrupo();
					chequeraIni = posicionBancaria.getIdChequera();
					rubroActual = posicionBancaria.getIdGrupo();
					chequeraActual = posicionBancaria.getIdChequera();
				}
				contador++;
				rubroActual = posicionBancaria.getIdGrupo();
				chequeraActual = posicionBancaria.getIdChequera();
				
				if(!rubroIni.equals(rubroActual)){
					contador++;
					/*if(posicionBancaria.getIdTipoMovto().equals("I")){
						acumulado2 = posicionBancaria.getIngreso();
					}else{
						acumulado2 = posicionBancaria.getEgreso();
					}
					*/
					rubroIni = posicionBancaria.getIdRubro();
				}else{
					
					/*if(posicionBancaria.getIdTipoMovto().equals("I")){
						acumulado2 += posicionBancaria.getIngreso();
					}else{
						acumulado2 += posicionBancaria.getEgreso();
					}*/
				}
				
				if(!chequeraIni.equals(chequeraActual)){
					
					PosicionBancariaDTO posI = new PosicionBancariaDTO();
					posI.setConcepto("Total Ingresos");
					posI.setEgreso(sumI);
					posI.setColor("#CCCCCC");
					nvaLista.add(posI);
					
					PosicionBancariaDTO posE = new PosicionBancariaDTO();
					posE.setConcepto("Total Egresos");
					posE.setEgreso(sumE);
					posE.setColor("#CCCCCC");
					nvaLista.add(posE);
					
					PosicionBancariaDTO posT = new PosicionBancariaDTO();
					posT.setConcepto("Total");
					posT.setEgreso(sumI - sumE);
					posT.setColor("#CCCCCC");
					nvaLista.add(posT);
					
					if(!chequera.equals(posicionBancaria.getIdChequera())){
						chequera = posicionBancaria.getIdChequera();
						
						PosicionBancariaDTO pos = new PosicionBancariaDTO();
						pos.setIdChequera(posicionBancaria.getNomEmpresa());
						pos.setColor("#8888FF");
						nvaLista.add(pos);
					}
			                
					sumE = 0;
					sumI = 0;
					//acumulado2 = 0;
					
				    if(posicionBancaria.getIdTipoMovto().equals("I")){
				    	//acumulado2 += posicionBancaria.getIngreso();
				    	sumI = posicionBancaria.getIngreso();
				    }else{
				    	//acumulado2 += posicionBancaria.getEgreso();
				    	sumE = posicionBancaria.getEgreso();
				    }
				    
				    contador += 4;
				    
				   /* if(posicionBancaria.getIdTipoMovto().equals("I")){
				    	acumulado = posicionBancaria.getIngreso();
				    }else{
				    	acumulado = posicionBancaria.getEgreso();
				    }
				    */
				    rubroIni = posicionBancaria.getIdGrupo();
				    chequeraIni = posicionBancaria.getIdChequera();
				    rubroActual = posicionBancaria.getIdGrupo();
				    chequeraActual = posicionBancaria.getIdChequera();
				    
				}else{
					
					if(posicionBancaria.getIdTipoMovto().equals("I")){
						//acumulado += posicionBancaria.getIngreso();
						sumI += posicionBancaria.getIngreso();
					}else{
						//acumulado += posicionBancaria.getEgreso();
						sumE += posicionBancaria.getEgreso();
						
					}
		                
				}
				
				nvaLista.add(posicionBancaria);
			}//fin ciclo for
			
			contador++;
			
			PosicionBancariaDTO posI = new PosicionBancariaDTO();
			posI.setConcepto("Total Ingresos");
			posI.setEgreso(sumI);
			posI.setColor("#CCCCCC");
			nvaLista.add(posI);
			
			PosicionBancariaDTO posE = new PosicionBancariaDTO();
			posE.setConcepto("Total Egresos");
			posE.setEgreso(sumE);
			posE.setColor("#CCCCCC");
			nvaLista.add(posE);
			
			PosicionBancariaDTO posT = new PosicionBancariaDTO();
			posT.setConcepto("Total");
			posT.setEgreso(sumI - sumE);
			posT.setColor("#CCCCCC");
			nvaLista.add(posT);
			
			sumE = 0;
			sumI = 0;
			contador += 3;
			        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Consultas, C:PosicionBancariaBusinessImpl, M:consultarMovtoTes");	
		}
		return nvaLista;
	}
	
	@Override
	public List<PosicionBancariaDTO> consultarDiarioTes(List<String> listBancos, List<String> listChequeras,
															  List<String> listEmpresas,  String fechaIni, String fechaFin,
															  int seleccion, String divisa) {
		
		List<PosicionBancariaDTO> list = new ArrayList<>();
		List<PosicionBancariaDTO> nvaLista = new ArrayList<>();
		
		try{
			
			StringBuffer idBancos = new StringBuffer();
			
			/** ELIMINA LOS REGISTROS REPETIDOS	**/
			HashSet<String> hsBancos = new HashSet<>();
			hsBancos.addAll(listBancos);
			listBancos.clear();
			listBancos.addAll(hsBancos);
			/************************************/
			
			if(listBancos.size()>0){
				for(int i=0; i<listBancos.size(); i++){
						idBancos.append(""+listBancos.get(i)+",");
				}
				
				if(idBancos.length()>0){
					idBancos.delete(idBancos.length()-1, idBancos.length());
				}
			}
			
			StringBuffer idChequeras = new StringBuffer();
			HashSet<String> hsChequeras = new HashSet<>();
			hsChequeras.addAll(listChequeras);
			listChequeras.clear();
			listChequeras.addAll(hsChequeras);
			
			if(listChequeras.size()>0){
				
				for(int i=0; i<listChequeras.size(); i++){
					idChequeras.append("'"+listChequeras.get(i)+"',");
				}
				
				if(idChequeras.length()>0){
					idChequeras.delete(idChequeras.length()-1, idChequeras.length());
				}
			}
			
			StringBuffer noEmpresas = new StringBuffer();
			HashSet<String> hsEmpresas = new HashSet<>();
			hsEmpresas.addAll(listEmpresas);
			listEmpresas.clear();
			listEmpresas.addAll(hsEmpresas);
			
			if(listEmpresas.size()>0){
				
				for(int i=0; i<listEmpresas.size(); i++){
						noEmpresas.append(""+listEmpresas.get(i)+",");
				}
				
				if(noEmpresas.length()>0){
					noEmpresas.delete(noEmpresas.length()-1, noEmpresas.length());
				}
			}
			
			list = posicionBancariaDao.consultarDiarioTes(idBancos.toString(), idChequeras.toString(), noEmpresas.toString(),
																fechaIni, fechaFin, seleccion, divisa);
			
			int contador = 0;
			String rubroIni = ""; 
			String chequeraIni = ""; 
			String rubroActual = ""; 
			String chequeraActual = ""; 
			//String bancoIni = "";
			
			double acumulado = 0;
			double acumulado2 = 0;
			double sumI = 0;
			double sumE = 0;
			
			boolean cambioCheRubro = false;
			
			//Comienza el proceso de sumas y clasificaci�n.
			
			for(PosicionBancariaDTO posicionBancaria: list){
				
				if(contador == 0){
					rubroIni = posicionBancaria.getIdGrupo();
					chequeraIni = posicionBancaria.getIdChequera();
					rubroActual = posicionBancaria.getIdGrupo();
					chequeraActual = posicionBancaria.getIdChequera();
					//bancoIni = posicionBancaria.getDescBanco();
				}
				contador++;
				rubroActual = posicionBancaria.getIdGrupo();
				chequeraActual = posicionBancaria.getIdChequera();
				
				if(!rubroIni.equals(rubroActual)){
					
					PosicionBancariaDTO pos = new PosicionBancariaDTO();
					pos.setIdGrupo(rubroIni);
					pos.setImporte(acumulado2);
					pos.setColor("#8888FF"); //H80FFFF
					nvaLista.add(pos);
					
					contador++;
					
					
					acumulado2 = posicionBancaria.getImporte();
					rubroIni = posicionBancaria.getIdGrupo();
					
					cambioCheRubro = true;
					
				}else{
					acumulado2 += posicionBancaria.getImporte();
				}
				
				if(!chequeraIni.equals(chequeraActual)){
					
					if(!cambioCheRubro){
						PosicionBancariaDTO pos = new PosicionBancariaDTO();
						pos.setIdGrupo(rubroIni);
						pos.setImporte(acumulado2-posicionBancaria.getImporte());
						pos.setColor("#8888FF"); //H80FFFF
						nvaLista.add(pos);
						
						contador++;
						
						//acumulado2 = posicionBancaria.getImporte();
						//rubroIni = posicionBancaria.getIdGrupo();
						
					}
					
					cambioCheRubro = false;
					
					PosicionBancariaDTO posT = new PosicionBancariaDTO();
					posT.setIdChequera(chequeraIni);
					posT.setImporte(acumulado);
					posT.setColor("#ABCDEF");
					nvaLista.add(posT);
										
					PosicionBancariaDTO posI = new PosicionBancariaDTO();
					posI.setIdGrupo("Total Ingresos");
					posI.setFecOperacion(""+sumI);
					posI.setColor("#CCCCCC");
					nvaLista.add(posI);
					
					PosicionBancariaDTO posE = new PosicionBancariaDTO();
					posE.setIdGrupo("Total Egresos");
					posE.setFecOperacion(""+sumE);
					posE.setColor("#CCCCCC");
					nvaLista.add(posE);
					
					PosicionBancariaDTO posT2 = new PosicionBancariaDTO();
					posT2.setIdGrupo("Total");
					posT2.setFecOperacion(""+(sumI - sumE));
					posT2.setColor("#CCCCCC");
					nvaLista.add(posT2);
					
					sumE = 0;
					sumI = 0;
					acumulado2 = 0;
					acumulado2 = posicionBancaria.getImporte();
					

					if(posicionBancaria.getIdTipoMovto().equals("I")){
						sumI = posicionBancaria.getImporte();
					}else{
						sumE = posicionBancaria.getImporte();
					}
				    
					contador += 4;
					
					acumulado = posicionBancaria.getImporte();
					
				    rubroIni = posicionBancaria.getIdGrupo();
				    chequeraIni = posicionBancaria.getIdChequera();
				    rubroActual = posicionBancaria.getIdGrupo();
				    chequeraActual = posicionBancaria.getIdChequera();
				    
				}else{
					
					if(posicionBancaria.getIdTipoMovto().equals("I")){
						sumI += posicionBancaria.getImporte();
					}else{
						sumE += posicionBancaria.getImporte();
					}
					
					acumulado += posicionBancaria.getImporte();
				}
				
				nvaLista.add(posicionBancaria);

			}//fin ciclo for
			
			contador++;
			
			PosicionBancariaDTO pos = new PosicionBancariaDTO();
			pos.setIdGrupo(rubroIni);
			pos.setImporte(acumulado2);
			pos.setColor("#8888FF"); //H80FFFF
			nvaLista.add(pos);
			
			PosicionBancariaDTO posC = new PosicionBancariaDTO();
			posC.setIdChequera(chequeraIni);
			posC.setImporte(acumulado);
			posC.setColor("#CCCCCC");
			nvaLista.add(posC);
			
			PosicionBancariaDTO posI = new PosicionBancariaDTO();
			posI.setIdGrupo("Total Ingresos");
			posI.setFecOperacion(""+sumI);
			posI.setColor("#CCCCCC");
			nvaLista.add(posI);
			
			PosicionBancariaDTO posE = new PosicionBancariaDTO();
			posE.setIdGrupo("Total Egresos");
			posE.setFecOperacion(""+sumE);
			posE.setColor("#CCCCCC");
			nvaLista.add(posE);
			
			PosicionBancariaDTO posT = new PosicionBancariaDTO();
			posT.setIdGrupo("Total");
			posT.setFecOperacion(""+(sumI - sumE));
			posT.setColor("#CCCCCC");
			nvaLista.add(posT);
			
			sumE = 0;
			sumI = 0;
			contador += 3;
			        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Consultas, C:PosicionBancariaBusinessImpl, M:consultarMovtoTes");	
		}
		return nvaLista;
	}
	
	@Override
	public List<PosicionBancariaDTO> posicionXEmp(List<String> listBancos, List<String> listChequeras,
											List<String> listEmpresas, String fechaIni, 
											String fechaFin, int seleccion, 
											String divisa, int tipoReporte) {
	
		List<PosicionBancariaDTO> listDatos = new ArrayList<PosicionBancariaDTO>();
		PosicionBancariaDTO dtoDatos = new PosicionBancariaDTO();
		
		try{
	
			StringBuffer idBancos = new StringBuffer();
			
			/** ELIMINA LOS REGISTROS REPETIDOS	**/
			HashSet<String> hsBancos = new HashSet<>();
			hsBancos.addAll(listBancos);
			listBancos.clear();
			listBancos.addAll(hsBancos);
			/************************************/
	
			if(listBancos.size()>0){
				for(int i=0; i<listBancos.size(); i++){
					idBancos.append(""+listBancos.get(i)+",");
				}
	
				if(idBancos.length()>0){
					idBancos.delete(idBancos.length()-1, idBancos.length());
					bancos = idBancos.toString();
				}else{
					bancos = "";
				}
			}
	
			StringBuffer idChequeras = new StringBuffer();
			HashSet<String> hsChequeras = new HashSet<>();
			hsChequeras.addAll(listChequeras);
			listChequeras.clear();
			listChequeras.addAll(hsChequeras);
	
			if(listChequeras.size()>0){
				
				for(int i=0; i<listChequeras.size(); i++){
					idChequeras.append("'"+listChequeras.get(i)+"',");
				}
				
				if(idChequeras.length()>0){
					idChequeras.delete(idChequeras.length()-1, idChequeras.length());
					chequeras = idChequeras.toString();
				}else{
					chequeras = "";
				}
			}
	
			StringBuffer noEmpresas = new StringBuffer();
			HashSet<String> hsEmpresas = new HashSet<>();
			hsEmpresas.addAll(listEmpresas);
			listEmpresas.clear();
			listEmpresas.addAll(hsEmpresas);
			
			if(listEmpresas.size()>0){
				
				for(int i=0; i<listEmpresas.size(); i++){
					noEmpresas.append(""+listEmpresas.get(i)+",");
					numEmpresas++;
				}
				
				if(noEmpresas.length()>0){
					noEmpresas.delete(noEmpresas.length()-1, noEmpresas.length());
					empresas = noEmpresas.toString();
				}else{
					empresas = "";
				}
			}
			
			if(seleccion != 1){
				
				//listFlujoEmp = posicionBancariaDao.obtieneFlujoEmp(bancos, chequeras,empresas, seleccion);
				listFlujoEmp = posicionBancariaDao.obtenerCuentas(bancos, empresas, seleccion, false);
				
				for(int i=0; i<listFlujoEmp.size(); i++){
					if(i==0){
						bancos = "";
						chequeras = "";
						empresas = "";
						numEmpresas = 0;
						
						if (idBancos.length() > 1)
							idBancos.delete(0, idBancos.length());
						
						if(idChequeras.length() > 1)
							idChequeras.delete(0, idChequeras.length());
						
						if(noEmpresas.length() > 1)
							noEmpresas.delete(0, noEmpresas.length());
					}
					
					if(idBancos.indexOf(""+listFlujoEmp.get(i).getIdBanco()) < 0){
						idBancos.append(""+listFlujoEmp.get(i).getIdBanco()+",");
					}
					
					if(noEmpresas.indexOf(""+listFlujoEmp.get(i).getNoEmpresa()) < 0){
						noEmpresas.append(""+listFlujoEmp.get(i).getNoEmpresa()+",");
						numEmpresas++;
					}
									
					idChequeras.append("'"+listFlujoEmp.get(i).getIdChequera()+"',");
				}
	
				if(idBancos.length()>0){
					idBancos.delete(idBancos.length()-1, idBancos.length());
					bancos = idBancos.toString();
				}else{
					bancos = "";
				}
				
				if(idChequeras.length()>0){
					idChequeras.delete(idChequeras.length()-1, idChequeras.length());
					chequeras = idChequeras.toString();
				}else{
					chequeras = "";
				}
				
				if(noEmpresas.length()>0){
					noEmpresas.delete(noEmpresas.length()-1, noEmpresas.length());
					empresas = noEmpresas.toString();
				}else{
					empresas = "";
				}
				
			}
			
			int diasDif = funciones.diasEntreFechas(funciones.ponerFechaDate(fechaIni), funciones.ponerFechaDate(fechaFin)) + 1;
			
			int noColumnas = (numEmpresas * diasDif)+2;

			StringBuffer columns = new StringBuffer();
			StringBuffer fields = new StringBuffer();
			//ENCABEZADOS
			columns.append("[");
			fields.append("[");
			
			fields.append("{");
			fields.append(" name: 'descripcion'},");
			fields.append("{");
			fields.append(" name: 'tipoMovto'},");
			columns.append("{");
			columns.append("header: 'CONCEPTO',");
			columns.append("width: 200,");
			columns.append("dataIndex: 'descripcion',");
			columns.append("renderer: this.colores}, ");
	        int noEmpresaAct = 1;
	        int diaX = 0;
	        
			for(int i=0; i<noColumnas-2; i++) {
				String fechaGrid = funciones.ponerDiaLetraYFecha(funciones.modificarFecha("d", diaX, funciones.ponerFechaDate(fechaIni)), true);
				fechaGrid = fechaGrid.substring(0, 3) + " , " + funciones.ponerFechaSola(funciones.modificarFecha("d", diaX, funciones.ponerFechaDate(fechaIni)));
				String name = "col" + i;
				
				fields.append("{");
				fields.append(" name: '"+ name +"'}");				
				
				columns.append("{");
				columns.append("header: '"+ fechaGrid +"',");
				columns.append("dataIndex: '"+ name +"' }");
				//columns.append("dataIndex: '"+ name +"' ");
				////columns.append(" return '$ ' + NS.formatNumber(Math.round((value)*100)/100); } }");
				
				//columns.append(",renderer: function (value, meta, record) {");
				//columns.append(" if(!isNaN(value)){return '$ ' + NS.formatNumber(Math.round((value)*100)/100);} "); 
				//columns.append(" else{return value;} }");
				
				if(diaX != diasDif -1){
					columns.append(",");
					fields.append(",");
					diaX++;
				}else if(noEmpresaAct <= numEmpresas){
					diaX = 0; 
					noEmpresaAct++;
					columns.append(",");
					fields.append(",");
				}
			}
			
			if (columns.substring(columns.length()-1,columns.length()).equals(",")) {
				columns.delete(columns.length()-1, columns.length());
			}
			
			if (fields.substring(fields.length()-1,fields.length()).equals(",")) {
				fields.delete(fields.length()-1, fields.length());
			}
			
			/*fields.append(",{");
			fields.append(" name: 'totales'}");
			
			columns.append(",{");
			columns.append("header: 'TOTALES',");
			columns.append("width: 100,");
			columns.append("dataIndex: 'totales'}");
			*/
			fields.append(",{");
			fields.append(" name: 'color'}");
			
			columns.append("]");
			fields.append("]");
			
			dtoDatos.setNomReporte("Fecha Valor");
			dtoDatos.setTipoPosicion(tipoReporte);
			dtoDatos.setColumnas(columns.toString());
			dtoDatos.setFields(fields.toString());
			listDatos.add(dtoDatos);		
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaBusiness, M:posicionXEmp");
		}
		
		return listDatos;
		
		//return listResult;
	}

	@Override
	public List<HashMap<String, String>> posicionXEmp2(List<String> listBancos, List<String> listChequeras,
														List<String> listEmpresas, String fechaIni, 
														String fechaFin, int seleccion, 
														String divisa, int tipoReporte) {
									
		List<HashMap<String, String>> listResult = new ArrayList<>();
		
		try{
			
				StringBuffer idBancos = new StringBuffer();
				
				/** ELIMINA LOS REGISTROS REPETIDOS	**/
				HashSet<String> hsBancos = new HashSet<>();
				hsBancos.addAll(listBancos);
				listBancos.clear();
				listBancos.addAll(hsBancos);
				/************************************/
		
				if(listBancos.size()>0){
					for(int i=0; i<listBancos.size(); i++){
						idBancos.append(""+listBancos.get(i)+",");
					}
		
					if(idBancos.length()>0){
						idBancos.delete(idBancos.length()-1, idBancos.length());
						bancos = idBancos.toString();
					}else{
						bancos = "";
					}
				}
		
				StringBuffer idChequeras = new StringBuffer();
				HashSet<String> hsChequeras = new HashSet<>();
				hsChequeras.addAll(listChequeras);
				listChequeras.clear();
				listChequeras.addAll(hsChequeras);
		
				if(listChequeras.size()>0){
					
					for(int i=0; i<listChequeras.size(); i++){
						idChequeras.append("'"+listChequeras.get(i)+"',");
					}
					
					if(idChequeras.length()>0){
						idChequeras.delete(idChequeras.length()-1, idChequeras.length());
						chequeras = idChequeras.toString();
					}else{
						chequeras = "";
					}
				}
		
				StringBuffer noEmpresas = new StringBuffer();
				HashSet<String> hsEmpresas = new HashSet<>();
				hsEmpresas.addAll(listEmpresas);
				listEmpresas.clear();
				listEmpresas.addAll(hsEmpresas);
				
				if(listEmpresas.size()>0){
					
					for(int i=0; i<listEmpresas.size(); i++){
						noEmpresas.append(""+listEmpresas.get(i)+",");
						numEmpresas++;
					}
					
					if(noEmpresas.length()>0){
						noEmpresas.delete(noEmpresas.length()-1, noEmpresas.length());
						empresas = noEmpresas.toString();
					}else{
						empresas = "";
					}
				}
				
				if(seleccion != 1){
					
					//listFlujoEmp = posicionBancariaDao.obtieneFlujoEmp(bancos, chequeras,empresas, seleccion);
					listFlujoEmp = posicionBancariaDao.obtenerCuentas(bancos, empresas, seleccion, false);
					
					for(int i=0; i<listFlujoEmp.size(); i++){
						if(i==0){
							bancos = "";
							chequeras = "";
							empresas = "";
							numEmpresas=0;
							
							if (idBancos.length() > 1)
								idBancos.delete(0, idBancos.length());
							
							if(idChequeras.length() > 1)
								idChequeras.delete(0, idChequeras.length());
							
							if(noEmpresas.length() > 1)
								noEmpresas.delete(0, noEmpresas.length());
						}
						
						if(idBancos.indexOf(""+listFlujoEmp.get(i).getIdBanco()) < 0){
							idBancos.append(""+listFlujoEmp.get(i).getIdBanco()+",");
						}
						
						if(noEmpresas.indexOf(""+listFlujoEmp.get(i).getNoEmpresa()) < 0){
							noEmpresas.append(""+listFlujoEmp.get(i).getNoEmpresa()+",");
							numEmpresas++;
						}
						
						//idBancos.append(""+listFlujoEmp.get(i).getIdBanco()+",");
						idChequeras.append("'"+listFlujoEmp.get(i).getIdChequera()+"',");
						//noEmpresas.append(""+listFlujoEmp.get(i).getNoEmpresa()+",");
					}
		
					if(idBancos.length()>0){
						idBancos.delete(idBancos.length()-1, idBancos.length());
						bancos = idBancos.toString();
					}else{
						bancos = "";
					}
					
					if(idChequeras.length()>0){
						idChequeras.delete(idChequeras.length()-1, idChequeras.length());
						chequeras = idChequeras.toString();
					}else{
						chequeras = "";
					}
					
					if(noEmpresas.length()>0){
						noEmpresas.delete(noEmpresas.length()-1, noEmpresas.length());
						empresas = noEmpresas.toString();
					}else{
						empresas = "";
					}
					
				}
				
				int diasDif = funciones.diasEntreFechas(funciones.ponerFechaDate(fechaIni), funciones.ponerFechaDate(fechaFin)) + 1;
				
				listFlujoEmp.clear();
				listFlujoEmp = posicionBancariaDao.obtieneFlujoEmp(bancos, chequeras);
				
				String[] vecEmpresas = null;
				
				if(listFlujoEmp.size() > 0) {
					vecEmpresas = new String[listFlujoEmp.size()];
					
					for(int i=0; i<listFlujoEmp.size(); i++)
						//todasEmpresas.append(listFlujoEmp.get(i).getNoEmpresa() + ":" + listFlujoEmp.get(i).getNomEmpresa() + "|");
						vecEmpresas[i] = listFlujoEmp.get(i).getNoEmpresa() + ":" + listFlujoEmp.get(i).getNomEmpresa();
					
				}
				
				if(seleccion != 1){
					numEmpresas = listFlujoEmp.size();
				}
				
				int noColumnas = (numEmpresas * diasDif)+2;
				
				List<PosicionBancariaDTO> listCuentaEmpresaI = new ArrayList<>();
				List<PosicionBancariaDTO> listCuentaEmpresaE = new ArrayList<>();
				
				listCuentaEmpresaI = posicionBancariaDao.obtieneCuentaEmpresa("I", bancos, chequeras, empresas, 0, fechaIni,
						fechaFin, seleccion, divisa);

				listCuentaEmpresaE = posicionBancariaDao.obtieneCuentaEmpresa("E", bancos, chequeras, empresas, 0, fechaIni,
						fechaFin, seleccion, divisa);

				
		        int noEmpresaAct = 1;
		        int diaX = 0;
		        int idxEmp = 0;
		        
		        HashMap<String, String> colEmpresas = new HashMap<>();
		        
		      //La primera vez que entre saca las empresas
		        for(int i=0; i<noColumnas-2; i++) {
					
		        	String name = "col" + i;
					
					colEmpresas.put(name, vecEmpresas[idxEmp]);
					
					if(diaX != diasDif -1){
						diaX++;
					}else if(noEmpresaAct <= numEmpresas){
						diaX = 0; 
						noEmpresaAct++;
						idxEmp++;
					}
				}
		        
		        listResult.add(colEmpresas);

		        /*	SALDOS	INICIALES */
		        List<PosicionBancariaDTO> listSaldoEmp = new ArrayList<PosicionBancariaDTO>();
		        HashMap<String, String> colSaldoInicial = new HashMap<>();
		        colSaldoInicial.put("descripcion", "SALDO INICIAL");
				
				String []vecNoEmpresa = empresas.split(",");
				diaX = 0;
				idxEmp = 0;
				noEmpresaAct = 1;
				
				for(int i=0; i<noColumnas-2; i++) {
					String fecha = funciones.ponerFechaSola(funciones.modificarFecha("d", diaX, funciones.ponerFechaDate(fechaIni)));
		        	String name = "col" + i;
					
		        	//if(diaX == 0){
		        		 listSaldoEmp = posicionBancariaDao.obtieneSaldodiario(bancos, "", fecha, Integer.parseInt(vecNoEmpresa[idxEmp]), divisa);
		        	//}
		        	
		        	colSaldoInicial.put(name, ""+listSaldoEmp.get(0).getImporte());
					
					if(diaX != diasDif -1){
						diaX++;
					}else if(noEmpresaAct <= numEmpresas){
						diaX = 0; 
						noEmpresaAct++;
						idxEmp++;
					}
				}
				
				listResult.add(colSaldoInicial);
				
				diaX = 0;
				idxEmp = 0;
				noEmpresaAct = 1;
				
		        //COMIEZA LA VALIDACI�N DE MOVIMIENTOS INGRESO
				int contador = 0;
		        String rubroIni = ""; 
				String rubroActual = ""; 
				
				HashMap<String, String> colsI = new HashMap<>();
				boolean tieneColumna = false; //para validar al final si tiene al menos un rubro de ingreso
				
				 for(PosicionBancariaDTO posicionBancaria : listCuentaEmpresaI){
			        	
			        	if(contador == 0){
							rubroIni = posicionBancaria.getIdGrupo();
							rubroActual = posicionBancaria.getIdGrupo();
							
							colsI.put("descripcion", rubroIni);
							colsI.put("tipoMovto", "I");
						}

			        	contador++;
						rubroActual = posicionBancaria.getIdGrupo();

						if(!rubroIni.equals(rubroActual)){	
							listResult.add(colsI);
							colsI = new HashMap<String,String>();
							colsI.put("descripcion", rubroActual); //rubro actual
							rubroIni = rubroActual;
							//contador++;
						}

						diaX = 0;
						idxEmp = 0;
						noEmpresaAct = 1;
						
						//listEmpresas.subList(arg0, arg1)
						
						for(int i=0; i<noColumnas-2; i++) {
			        		
							String name = "col" + i;
							
							if(posicionBancaria.getNoEmpresa() == Integer.parseInt(vecNoEmpresa[noEmpresaAct-1])){
								if(funciones.ponerFechaDate(funciones.cambiarFecha(posicionBancaria.getFecValor(), true)).compareTo(funciones.modificarFecha("d", diaX, funciones.ponerFechaDate(fechaIni))) == 0) {
									colsI.put(name, ""+posicionBancaria.getImporte());
									tieneColumna = true;
								}
								
							}
							
							if(diaX != diasDif -1){
								diaX++;
							}else if(noEmpresaAct <= numEmpresas){
								diaX = 0; 
								noEmpresaAct++;
								idxEmp++;
							}
						}
			        	
			        }
		
			        //Al final no hay cambio de rubro, es por eso que se inserta la �ltima columna
				 	if(tieneColumna){
				 		listResult.add(colsI);
				 	}
			        
		
			    /**	NOTA: PARA LA SUMA DE TODOS LOS TOTALES (INGRESO, EGRESOS, SALDOS) 
			     * SE HACE DESDE EL JS YA TENIENDO LOS DATOS EN EL GRID 	
			     **/
			        
		        /*	TOTAL INGRESO */
		        HashMap<String, String> colTotalIngreso = new HashMap<>();
		        colTotalIngreso.put("descripcion", "TOTAL INGRESO");
		        listResult.add(colTotalIngreso);
		        
		        contador = 0;
		        rubroIni = ""; 
				rubroActual = ""; 
				tieneColumna = false;
				
				HashMap<String, String> cols = new HashMap<>();
				HashMap<String, String> colTotalEgreso = new HashMap<>();
				String name = "";
		      //COMIEZA LA VALIDACI�N DE MOVIMIENTOS EGRESOS
		        for(PosicionBancariaDTO posicionBancaria : listCuentaEmpresaE){
		        	
		        	if(contador == 0){
						rubroIni = posicionBancaria.getIdGrupo();
						rubroActual = posicionBancaria.getIdGrupo();
						cols.put("descripcion", rubroIni);
					}

		        	contador++;
					rubroActual = posicionBancaria.getIdGrupo();

					if(!rubroIni.equals(rubroActual)){	
						listResult.add(cols);
						cols = new HashMap<String,String>();
						cols.put("descripcion", rubroActual); //rubro actual
						cols.put("tipoMovto", "E");
						rubroIni = rubroActual;
					}

					diaX = 0;
					idxEmp = 0;
					noEmpresaAct = 1;
					
					for(int i=0; i<noColumnas-2; i++) {
		        		
						name = "col" + i;
						
						if(posicionBancaria.getNoEmpresa() == Integer.parseInt(vecNoEmpresa[noEmpresaAct-1])){
							if(funciones.ponerFechaDate(funciones.cambiarFecha(posicionBancaria.getFecValor(), true)).compareTo(funciones.modificarFecha("d", diaX, funciones.ponerFechaDate(fechaIni))) == 0) {
								cols.put(name, ""+posicionBancaria.getImporte());
								tieneColumna = true;
							}
							
						}						

						if(diaX != diasDif -1){
							diaX++;
						}else if(noEmpresaAct <= numEmpresas){
							diaX = 0; 
							noEmpresaAct++;
							idxEmp++;
						}
					}
		        	
		        }
	
		        //Al final no hay cambio de rubro, es por eso que se inserta la ultima columna
		        if (tieneColumna) {
					listResult.add(cols);
				}
		        
		        colTotalEgreso.put("descripcion", "TOTAL EGRESO");
		        listResult.add(colTotalEgreso);
		        
		        /*	SALDO TOTAL */
		        HashMap<String, String> colSaldoTotal = new HashMap<>();
		        colSaldoTotal.put("descripcion", "SALDO TOTAL");
		        listResult.add(colSaldoTotal);
		        
			return listResult;
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaBusiness, M:posicionXEmp2");
		}
		
		return listResult;
	}
	
	
	@Override
	public List<PosicionBancariaDTO> posicionXCta(List<String> listBancos, List<String> listChequeras,
											List<String> listEmpresas, String fechaIni, 
											String fechaFin, int seleccion, 
											String divisa, int tipoReporte) {
	
		List<PosicionBancariaDTO> listDatos = new ArrayList<PosicionBancariaDTO>();
		PosicionBancariaDTO dtoDatos = new PosicionBancariaDTO();
		
		try{
	
			StringBuffer idBancos = new StringBuffer();
			
			/** ELIMINA LOS REGISTROS REPETIDOS	**/
			HashSet<String> hsBancos = new HashSet<>();
			hsBancos.addAll(listBancos);
			listBancos.clear();
			listBancos.addAll(hsBancos);
			/************************************/
	
			if(listBancos.size()>0){
				for(int i=0; i<listBancos.size(); i++){
					idBancos.append(""+listBancos.get(i)+",");
				}
	
				if(idBancos.length()>0){
					idBancos.delete(idBancos.length()-1, idBancos.length());
					bancos = idBancos.toString();
				}else{
					bancos = "";
				}
			}
	
			StringBuffer idChequeras = new StringBuffer();
			HashSet<String> hsChequeras = new HashSet<>();
			hsChequeras.addAll(listChequeras);
			listChequeras.clear();
			listChequeras.addAll(hsChequeras);
	
			if(listChequeras.size()>0){
				
				for(int i=0; i<listChequeras.size(); i++){
					idChequeras.append("'"+listChequeras.get(i)+"',");
					numCuentas++;
				}
				
				if(idChequeras.length()>0){
					idChequeras.delete(idChequeras.length()-1, idChequeras.length());
					chequeras = idChequeras.toString();
				}else{
					chequeras = "";
				}
			}
	
			StringBuffer noEmpresas = new StringBuffer();
			HashSet<String> hsEmpresas = new HashSet<>();
			hsEmpresas.addAll(listEmpresas);
			listEmpresas.clear();
			listEmpresas.addAll(hsEmpresas);
			
			if(listEmpresas.size()>0){
				
				for(int i=0; i<listEmpresas.size(); i++){
					noEmpresas.append(""+listEmpresas.get(i)+",");
					numEmpresas++;
				}
				
				if(noEmpresas.length()>0){
					noEmpresas.delete(noEmpresas.length()-1, noEmpresas.length());
					empresas = noEmpresas.toString();
				}else{
					empresas = "";
				}
			}
			
			if(seleccion != 1){
				
				listFlujoEmp = posicionBancariaDao.obtenerCuentas(bancos, empresas, seleccion,true);
				
				for(int i=0; i<listFlujoEmp.size(); i++){
					if(i==0){
						bancos = "";
						chequeras = "";
						empresas = "";
						numCuentas = 0;
						
						if (idBancos.length() > 1)
							idBancos.delete(0, idBancos.length());
						
						if(idChequeras.length() > 1)
							idChequeras.delete(0, idChequeras.length());
						
						if(noEmpresas.length() > 1)
							noEmpresas.delete(0, noEmpresas.length());
					}
					
					if(idBancos.indexOf(""+listFlujoEmp.get(i).getIdBanco()) < 0){
						idBancos.append(""+listFlujoEmp.get(i).getIdBanco()+",");
					}
					
					if(noEmpresas.indexOf(""+listFlujoEmp.get(i).getNoEmpresa()) < 0){
						noEmpresas.append(""+listFlujoEmp.get(i).getNoEmpresa()+",");
						numEmpresas++;
					}
					
					idChequeras.append("'"+listFlujoEmp.get(i).getIdChequera()+"',");
				}
	
				if(idBancos.length()>0){
					idBancos.delete(idBancos.length()-1, idBancos.length());
					bancos = idBancos.toString();
				}else{
					bancos = "";
				}
				
				if(idChequeras.length()>0){
					idChequeras.delete(idChequeras.length()-1, idChequeras.length());
					chequeras = idChequeras.toString();
				}else{
					chequeras = "";
				}
				
				if(noEmpresas.length()>0){
					noEmpresas.delete(noEmpresas.length()-1, noEmpresas.length());
					empresas = noEmpresas.toString();
				}else{
					empresas = "";
				}
				
			}
			
			int diasDif = funciones.diasEntreFechas(funciones.ponerFechaDate(fechaIni), funciones.ponerFechaDate(fechaFin)) + 1;
			
			String[] vecChequeras = null;
			String[] vecEmpresas = null;
			
			if(listFlujoEmp.size() > 0) {
				vecChequeras = new String[listFlujoEmp.size()];
				vecEmpresas = new String[listFlujoEmp.size()];
				
				for(int i=0; i<listFlujoEmp.size(); i++){
					vecChequeras[i] = listFlujoEmp.get(i).getIdChequera();
					vecEmpresas[i] = listFlujoEmp.get(i).getNoEmpresa() + ":" + listFlujoEmp.get(i).getNomEmpresa();
					//vecEmpresas[i] = listFlujoEmp.get(i).getNoEmpresa() + ":" + listFlujoEmp.get(i).getNomEmpresa();
				}
			}
			
			if(seleccion != 1){
				numCuentas = listFlujoEmp.size();
			}
			
			int noColumnas = (numCuentas * diasDif)+2;

			StringBuffer columns = new StringBuffer();
			StringBuffer fields = new StringBuffer();
			//ENCABEZADOS
			columns.append("[");
			fields.append("[");
			
			fields.append("{");
			fields.append(" name: 'descripcion'},");
			fields.append("{");
			fields.append(" name: 'tipoMovto'},");
			columns.append("{");
			columns.append("header: 'CONCEPTO',");
			columns.append("width: 200,");
			columns.append("dataIndex: 'descripcion',");
			columns.append("renderer: this.colores}, ");
	        int noCuentasActual = 1;
	        int diaX = 0;
	        
			for(int i=0; i<noColumnas-2; i++) {
				String fechaGrid = funciones.ponerDiaLetraYFecha(funciones.modificarFecha("d", diaX, funciones.ponerFechaDate(fechaIni)), true);
				fechaGrid = fechaGrid.substring(0, 3) + " , " + funciones.ponerFechaSola(funciones.modificarFecha("d", diaX, funciones.ponerFechaDate(fechaIni)));
				String name = "col" + i;
				
				fields.append("{");
				fields.append(" name: '"+ name +"'}");				
				
				columns.append("{");
				columns.append("header: '"+ fechaGrid +"',");
				columns.append("dataIndex: '"+ name +"' }");
				//columns.append("dataIndex: '"+ name +"' ");
				////columns.append(" return '$ ' + NS.formatNumber(Math.round((value)*100)/100); } }");
				
				//columns.append(",renderer: function (value, meta, record) {");
				//columns.append(" if(!isNaN(value)){return '$ ' + NS.formatNumber(Math.round((value)*100)/100);} "); 
				//columns.append(" else{return value;} }");
				
				if(diaX != diasDif -1){
					columns.append(",");
					fields.append(",");
					diaX++;
				}else if(noCuentasActual <= numCuentas){
					diaX = 0; 
					noCuentasActual++;
					columns.append(",");
					fields.append(",");
				}
			}
			
			if (columns.substring(columns.length()-1,columns.length()).equals(",")) {
				columns.delete(columns.length()-1, columns.length());
			}
			
			if (fields.substring(fields.length()-1,fields.length()).equals(",")) {
				fields.delete(fields.length()-1, fields.length());
			}
			
			/*fields.append(",{");
			fields.append(" name: 'totales'}");
			
			columns.append(",{");
			columns.append("header: 'TOTALES',");
			columns.append("width: 100,");
			columns.append("dataIndex: 'totales'}");
			*/
			fields.append(",{");
			fields.append(" name: 'color'}");
			
			columns.append("]");
			fields.append("]");
			
			dtoDatos.setNomReporte("Fecha Valor");
			dtoDatos.setTipoPosicion(tipoReporte);
			dtoDatos.setColumnas(columns.toString());
			dtoDatos.setFields(fields.toString());
			listDatos.add(dtoDatos);		
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaBusiness, M:posicionXEmp");
		}
		
		return listDatos;
		
		//return listResult;
	}
	
	
	@Override
	public List<HashMap<String, String>> posicionXCta2(List<String> listBancos, List<String> listChequeras,
														List<String> listEmpresas, String fechaIni, 
														String fechaFin, int seleccion, 
														String divisa, int tipoReporte) {
									
		List<HashMap<String, String>> listResult = new ArrayList<>();
		
		String[] vecChequeras = null;
		String[] vecEmpresas = null;
		
		numEmpresas = 0;
		numCuentas = 0;
		
		try{
			
				StringBuffer idBancos = new StringBuffer();
				
				/** ELIMINA LOS REGISTROS REPETIDOS	**/
				HashSet<String> hsBancos = new HashSet<>();
				hsBancos.addAll(listBancos);
				listBancos.clear();
				listBancos.addAll(hsBancos);
				/************************************/
		
				if(listBancos.size()>0){
					for(int i=0; i<listBancos.size(); i++){
						idBancos.append(""+listBancos.get(i)+",");
					}
		
					if(idBancos.length()>0){
						idBancos.delete(idBancos.length()-1, idBancos.length());
						bancos = idBancos.toString();
					}else{
						bancos = "";
					}
				}
		
				StringBuffer idChequeras = new StringBuffer();
				HashSet<String> hsChequeras = new HashSet<>();
				hsChequeras.addAll(listChequeras);
				listChequeras.clear();
				listChequeras.addAll(hsChequeras);
		
				if(listChequeras.size()>0){
					
					for(int i=0; i<listChequeras.size(); i++){
						idChequeras.append("'"+listChequeras.get(i)+"',");
						numCuentas++;
					}
					
					if(idChequeras.length()>0){
						idChequeras.delete(idChequeras.length()-1, idChequeras.length());
						chequeras = idChequeras.toString();
					}else{
						chequeras = "";
					}
				}
		
				StringBuffer noEmpresas = new StringBuffer();
				HashSet<String> hsEmpresas = new HashSet<>();
				hsEmpresas.addAll(listEmpresas);
				listEmpresas.clear();
				listEmpresas.addAll(hsEmpresas);
				
				if(listEmpresas.size()>0){
					
					for(int i=0; i<listEmpresas.size(); i++){
						noEmpresas.append(""+listEmpresas.get(i)+",");
						numEmpresas++;
					}
					
					if(noEmpresas.length()>0){
						noEmpresas.delete(noEmpresas.length()-1, noEmpresas.length());
						empresas = noEmpresas.toString();
						
					}else{
						empresas = "";
					}
				}
				
				//if(seleccion != 1){
					
					//EMS: A�ad� el parametro de chequeras para retornar el nombre de empresas en base a esas chequeras.
					listFlujoEmp = posicionBancariaDao.obtenerCuentas(bancos, chequeras, empresas, seleccion, true);
					
					if(seleccion != 1){
					
						for(int i=0; i<listFlujoEmp.size(); i++){
							if(i==0){
								bancos = "";
								chequeras = "";
								empresas = "";
								numCuentas = 0;
								
								if (idBancos.length() > 1)
									idBancos.delete(0, idBancos.length());
								
								if(idChequeras.length() > 1)
									idChequeras.delete(0, idChequeras.length());
								
								if(noEmpresas.length() > 1)
									noEmpresas.delete(0, noEmpresas.length());
							}
							
							if(idBancos.indexOf(""+listFlujoEmp.get(i).getIdBanco()) < 0){
								idBancos.append(""+listFlujoEmp.get(i).getIdBanco()+",");
							}
							
							if(noEmpresas.indexOf(""+listFlujoEmp.get(i).getNoEmpresa()) < 0){
								noEmpresas.append(""+listFlujoEmp.get(i).getNoEmpresa()+",");
								numEmpresas++;
							}
							
							//idBancos.append(""+listFlujoEmp.get(i).getIdBanco()+",");
							idChequeras.append("'"+listFlujoEmp.get(i).getIdChequera()+"',");
							//noEmpresas.append(""+listFlujoEmp.get(i).getNoEmpresa()+",");
						}
			
						if(idBancos.length()>0){
							idBancos.delete(idBancos.length()-1, idBancos.length());
							bancos = idBancos.toString();
						}else{
							bancos = "";
						}
						
						if(idChequeras.length()>0){
							idChequeras.delete(idChequeras.length()-1, idChequeras.length());
							chequeras = idChequeras.toString();
						}else{
							chequeras = "";
						}
						
						if(noEmpresas.length()>0){
							noEmpresas.delete(noEmpresas.length()-1, noEmpresas.length());
							empresas = noEmpresas.toString();
						}else{
							empresas = "";
						}
					}
				//}
				
				int diasDif = funciones.diasEntreFechas(funciones.ponerFechaDate(fechaIni), funciones.ponerFechaDate(fechaFin)) + 1;
				
				if(listFlujoEmp.size() > 0) {
					vecChequeras = new String[listFlujoEmp.size()];
					vecEmpresas = new String[listFlujoEmp.size()];
					
					for(int i=0; i<listFlujoEmp.size(); i++){
						vecChequeras[i] = listFlujoEmp.get(i).getIdChequera();
						vecEmpresas[i] = listFlujoEmp.get(i).getNoEmpresa() + ":" + listFlujoEmp.get(i).getNomEmpresa();
						//vecEmpresas[i] = listFlujoEmp.get(i).getNoEmpresa() + ":" + listFlujoEmp.get(i).getNomEmpresa();
					}
				}
				
				//if(seleccion != 1){
					numCuentas = listFlujoEmp.size();
				//}
				
				int noColumnas = (numCuentas * diasDif)+2;
				
				 int noChequeras = 1;
		        int diaX = 0;
		        int idxEmp = 0;
		        
		        HashMap<String, String> colEmpresas = new HashMap<>();
		        HashMap<String, String> colChequeras = new HashMap<>();
		        
		      // ENCABEZADOS EMPRESA/CHEQUERA
		       for(int i=0; i<noColumnas-2; i++) {
					
		        	String name = "col" + i;
					
					colEmpresas.put(name, vecEmpresas[idxEmp]);
					colChequeras.put(name, vecChequeras[idxEmp]);
					
					if(diaX != diasDif -1){
						diaX++;
					}else if(noChequeras <= numCuentas){
						diaX = 0; 
						noChequeras++;
						idxEmp++;
					}
				}
		       
		        listResult.add(colEmpresas);
		        listResult.add(colChequeras);
			        
		        
				List<PosicionBancariaDTO> listCuentaEmpresaI = new ArrayList<>();
				List<PosicionBancariaDTO> listCuentaEmpresaE = new ArrayList<>();
				
				listCuentaEmpresaI = posicionBancariaDao.obtieneFichaCuenta("I", bancos, chequeras, 0, fechaIni,
						fechaFin, seleccion, divisa);

				listCuentaEmpresaE = posicionBancariaDao.obtieneFichaCuenta("E", bancos, chequeras, 0, fechaIni,
						fechaFin, seleccion, divisa);
		        
		        /*	SALDOS	INICIALES */				
		        List<PosicionBancariaDTO> listSaldo = new ArrayList<PosicionBancariaDTO>();
		        HashMap<String, String> colSaldoInicial = new HashMap<>();
		        colSaldoInicial.put("descripcion", "SALDO INICIAL");
				
				diaX = 0;
				idxEmp = 0;
				noChequeras = 1;
				
				for(int i=0; i<noColumnas-2; i++) {
					String fecha = funciones.ponerFechaSola(funciones.modificarFecha("d", diaX, funciones.ponerFechaDate(fechaIni)));
		        	String name = "col" + i;
					
		        	//if(diaX == 0){
		        		 listSaldo = posicionBancariaDao.obtieneSaldodiario(bancos, vecChequeras[idxEmp], fecha, 0, divisa);
		        	//}
		        	
		        	colSaldoInicial.put(name, ""+listSaldo.get(0).getImporte());
					
					if(diaX != diasDif -1){
						diaX++;
					}else if(noChequeras <= numCuentas){
						diaX = 0; 
						noChequeras++;
						idxEmp++;
					}
				}
				
				listResult.add(colSaldoInicial);
				
				
				diaX = 0;
				idxEmp = 0;
				noChequeras = 1;
				
		        //COMIEZA LA VALIDACI�N DE MOVIMIENTOS INGRESO
				int contador = 0;
		        String rubroIni = ""; 
				String rubroActual = ""; 
				
				HashMap<String, String> colsI = new HashMap<>();
				boolean tieneColumna = false; //para validar al final si tiene al menos un rubro de ingreso
				
				 for(PosicionBancariaDTO posicionBancaria : listCuentaEmpresaI){
			        	
			        	if(contador == 0){
							rubroIni = posicionBancaria.getIdGrupo();
							rubroActual = posicionBancaria.getIdGrupo();
							
							colsI.put("descripcion", rubroIni);
							colsI.put("tipoMovto", "I");
						}

			        	contador++;
						rubroActual = posicionBancaria.getIdGrupo();

						if(!rubroIni.equals(rubroActual)){	
							listResult.add(colsI);
							colsI = new HashMap<String,String>();
							colsI.put("descripcion", rubroActual); //rubro actual
							rubroIni = rubroActual;
							//contador++;
						}

						diaX = 0;
						//idxEmp = 0;
						noChequeras = 1;
						
						//listEmpresas.subList(arg0, arg1)
						
						for(int i=0; i<noColumnas-2; i++) {
			        		
							String name = "col" + i;
							
							if(posicionBancaria.getIdChequera().equals(vecChequeras[noChequeras-1]) ){
								if(funciones.ponerFechaDate(funciones.cambiarFecha(posicionBancaria.getFecValor(), true)).compareTo(funciones.modificarFecha("d", diaX, funciones.ponerFechaDate(fechaIni))) == 0) {
									colsI.put(name, ""+posicionBancaria.getImporte());
									tieneColumna = true;
								}
								
							}
							
							if(diaX != diasDif -1){
								diaX++;
							}else if(noChequeras <= numCuentas){
								diaX = 0; 
								noChequeras++;
								//idxEmp++;
							}
						}
			        	
			        }
		
			        //Al final no hay cambio de rubro, es por eso que se inserta la �ltima columna
				 	if(tieneColumna){
				 		listResult.add(colsI);
				 	}
		
				 	
			    /**	NOTA: PARA LA SUMA DE TODOS LOS TOTALES (INGRESO, EGRESOS, SALDOS) 
			     * SE HACE DESDE EL JS YA TENIENDO LOS DATOS EN EL GRID 	
			     **/
			        
		        /*	TOTAL INGRESO */		      
		        HashMap<String, String> colTotalIngreso = new HashMap<>();
		       
		        colTotalIngreso.put("descripcion", "TOTAL INGRESO");
		        listResult.add(colTotalIngreso);
		      
		        
		        contador = 0;
		        rubroIni = ""; 
				rubroActual = ""; 
				tieneColumna = false;
				
				HashMap<String, String> cols = new HashMap<>();
				HashMap<String, String> colTotalEgreso = new HashMap<>();
				String name = "";
		      //COMIEZA LA VALIDACI�N DE MOVIMIENTOS EGRESOS
				
				/**
				 * 
				 */
				for(int i=0 ; i<vecChequeras.length; i++){
					System.out.println("i: " + vecChequeras[i]);
				}
				
		        for(PosicionBancariaDTO posicionBancaria : listCuentaEmpresaE){
		        	
		        	if(contador == 0){
						rubroIni = posicionBancaria.getIdGrupo();
						rubroActual = posicionBancaria.getIdGrupo();
						cols.put("descripcion", rubroIni);
						cols.put("tipoMovto", "E");
					}

		        	contador++;
					rubroActual = posicionBancaria.getIdGrupo();

					if(!rubroIni.equals(rubroActual)){	
						listResult.add(cols);
						cols = new HashMap<String,String>();
						cols.put("descripcion", rubroActual); //rubro actual
						rubroIni = rubroActual;
					}

					diaX = 0;
					idxEmp = 0;
					noChequeras = 1;
					
					for(int i=0; i<noColumnas-2; i++) {
		        		
						name = "col" + i;
						
						if(posicionBancaria.getIdChequera().equals(vecChequeras[noChequeras-1])){
							if(funciones.ponerFechaDate(funciones.cambiarFecha(posicionBancaria.getFecValor(), true)).compareTo(funciones.modificarFecha("d", diaX, funciones.ponerFechaDate(fechaIni))) == 0) {
								cols.put(name, ""+posicionBancaria.getImporte());
								tieneColumna = true;
							}
							
						}					

						if(diaX != diasDif -1){
							diaX++;
						}else if(noChequeras <= numCuentas){
							diaX = 0; 
							noChequeras++;
							idxEmp++;
						}
					}
		        	
		        }
	
		        //Al final no hay cambio de rubro, es por eso que se inserta la ultima columna
		        if (tieneColumna) {
					listResult.add(cols);
				}
		        
		        colTotalEgreso.put("descripcion", "TOTAL EGRESO");
		        listResult.add(colTotalEgreso);
		        
		        /*	SALDO TOTAL */
		        HashMap<String, String> colSaldoTotal = new HashMap<>();
		        colSaldoTotal.put("descripcion", "SALDO TOTAL");
		        listResult.add(colSaldoTotal);
		        
			return listResult;
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaBusiness, M:posicionXCta2");
		}
		
		return listResult;
	}

	@Override
	public List<PosicionBancariaDTO> repSaldos(List<String> listBancos, List<String> listChequeras,
											List<String> listEmpresas, String fechaIni, 
											String fechaFin, int seleccion, 
											String divisa, int tipoReporte) {
	
		List<PosicionBancariaDTO> listDatos = new ArrayList<PosicionBancariaDTO>();
		PosicionBancariaDTO dtoDatos = new PosicionBancariaDTO();
		
		try{
	
			StringBuffer idBancos = new StringBuffer();
			
			/** ELIMINA LOS REGISTROS REPETIDOS	**/
			HashSet<String> hsBancos = new HashSet<>();
			hsBancos.addAll(listBancos);
			listBancos.clear();
			listBancos.addAll(hsBancos);
			/************************************/
	
			if(listBancos.size()>0){
				for(int i=0; i<listBancos.size(); i++){
					idBancos.append(""+listBancos.get(i)+",");
				}
	
				if(idBancos.length()>0){
					idBancos.delete(idBancos.length()-1, idBancos.length());
					bancos = idBancos.toString();
				}else{
					bancos = "";
				}
			}
	
			StringBuffer idChequeras = new StringBuffer();
			HashSet<String> hsChequeras = new HashSet<>();
			hsChequeras.addAll(listChequeras);
			listChequeras.clear();
			listChequeras.addAll(hsChequeras);
	
			if(listChequeras.size()>0){
				
				for(int i=0; i<listChequeras.size(); i++){
					idChequeras.append("'"+listChequeras.get(i)+"',");
				}
				
				if(idChequeras.length()>0){
					idChequeras.delete(idChequeras.length()-1, idChequeras.length());
					chequeras = idChequeras.toString();
				}else{
					chequeras = "";
				}
			}
	
			StringBuffer noEmpresas = new StringBuffer();
			HashSet<String> hsEmpresas = new HashSet<>();
			hsEmpresas.addAll(listEmpresas);
			listEmpresas.clear();
			listEmpresas.addAll(hsEmpresas);
			
			if(listEmpresas.size()>0){
				
				for(int i=0; i<listEmpresas.size(); i++){
					noEmpresas.append(""+listEmpresas.get(i)+",");
					numEmpresas++;
				}
				
				if(noEmpresas.length()>0){
					noEmpresas.delete(noEmpresas.length()-1, noEmpresas.length());
					empresas = noEmpresas.toString();
				}else{
					empresas = "";
				}
			}
			
			if(seleccion != 1){
				
				listFlujoEmp = posicionBancariaDao.obtenerCuentas(bancos, empresas, seleccion, false);
				
				for(int i=0; i<listFlujoEmp.size(); i++){
					if(i==0){
						bancos = "";
						chequeras = "";
						empresas = "";
						numEmpresas = 0;
						
						if (idBancos.length() > 1)
							idBancos.delete(0, idBancos.length());
						
						if(idChequeras.length() > 1)
							idChequeras.delete(0, idChequeras.length());
						
						if(noEmpresas.length() > 1)
							noEmpresas.delete(0, noEmpresas.length());
					}
					
					if(idBancos.indexOf(""+listFlujoEmp.get(i).getIdBanco()) < 0){
						idBancos.append(""+listFlujoEmp.get(i).getIdBanco()+",");
					}
					
					if(noEmpresas.indexOf(""+listFlujoEmp.get(i).getNoEmpresa()) < 0){
						noEmpresas.append(""+listFlujoEmp.get(i).getNoEmpresa()+",");
						//numEmpresas++;
					}
									
					idChequeras.append("'"+listFlujoEmp.get(i).getIdChequera()+"',");
				}
	
				if(idBancos.length()>0){
					idBancos.delete(idBancos.length()-1, idBancos.length());
					bancos = idBancos.toString();
				}else{
					bancos = "";
				}
				
				if(idChequeras.length()>0){
					idChequeras.delete(idChequeras.length()-1, idChequeras.length());
					chequeras = idChequeras.toString();
				}else{
					chequeras = "";
				}
				
				if(noEmpresas.length()>0){
					noEmpresas.delete(noEmpresas.length()-1, noEmpresas.length());
					empresas = noEmpresas.toString();
				}else{
					empresas = "";
				}
				
			}
			
			int diasDif = funciones.diasEntreFechas(funciones.ponerFechaDate(fechaIni), funciones.ponerFechaDate(fechaFin)) + 1;
			
			int noColumnas = (diasDif)+2;

			StringBuffer columns = new StringBuffer();
			StringBuffer fields = new StringBuffer();
			//ENCABEZADOS
			columns.append("[");
			fields.append("[");
			
			fields.append("{");
			fields.append(" name: 'descBanco'},");
			
			columns.append("{");
			columns.append("header: 'BANCO',");
			columns.append("width: 200,");
			columns.append("dataIndex: 'descBanco'}, ");
			
			fields.append("{");
			fields.append(" name: 'idChequera'},");
			
			columns.append("{");
			columns.append("header: 'CHEQUERA',");
			columns.append("width: 200,");
			columns.append("dataIndex: 'idChequera'}, ");
			
			fields.append("{");
			fields.append(" name: 'nomEmpresa'},");
			
			columns.append("{");
			columns.append("header: 'EMPRESA',");
			columns.append("width: 200,");
			columns.append("dataIndex: 'nomEmpresa'}, ");
			
			fields.append("{");
			fields.append(" name: 'idDivisa'},");
			
			columns.append("{");
			columns.append("header: 'DIVISA',");
			columns.append("width: 80,");
			columns.append("dataIndex: 'idDivisa'}, ");
	        
			for(int i=0; i<noColumnas-2; i++) {
				String fechaGrid = funciones.ponerDiaLetraYFecha(funciones.modificarFecha("d", i, funciones.ponerFechaDate(fechaIni)), true);
				fechaGrid = fechaGrid.substring(0, 3) + " , " + funciones.ponerFechaSola(funciones.modificarFecha("d", i, funciones.ponerFechaDate(fechaIni)));
				String name = "col" + i;
				
				fields.append("{");
				fields.append(" name: '"+ name +"'}");				
				
				columns.append("{");
				columns.append("header: '"+ fechaGrid +"',");
				columns.append("dataIndex: '"+ name +"' }");
				
				if(i != diasDif -1){
					columns.append(",");
					fields.append(",");					
				}
			}
			
			if (columns.substring(columns.length()-1,columns.length()).equals(",")) {
				columns.delete(columns.length()-1, columns.length());
			}
			
			if (fields.substring(fields.length()-1,fields.length()).equals(",")) {
				fields.delete(fields.length()-1, fields.length());
			}
						
			fields.append(",{");
			fields.append(" name: 'color'}");
			
			columns.append("]");
			fields.append("]");
			
			dtoDatos.setNomReporte("Reporte Saldos");
			dtoDatos.setTipoPosicion(tipoReporte);
			dtoDatos.setColumnas(columns.toString());
			dtoDatos.setFields(fields.toString());
			listDatos.add(dtoDatos);		
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaBusiness, M:repSaldos");
		}
		
		return listDatos;
	}
	
	@Override
	public List<HashMap<String, String>>repSaldos2(List<String> listBancos, List<String> listChequeras,
														List<String> listEmpresas, String fechaIni, 
														String fechaFin, int seleccion, 
														String divisa, int tipoReporte) {
									
		List<HashMap<String, String>> listResult = new ArrayList<>();
		
		try{
			
				StringBuffer idBancos = new StringBuffer();
				
				/** ELIMINA LOS REGISTROS REPETIDOS	**/
				HashSet<String> hsBancos = new HashSet<>();
				hsBancos.addAll(listBancos);
				listBancos.clear();
				listBancos.addAll(hsBancos);
				/************************************/
		
				if(listBancos.size()>0){
					for(int i=0; i<listBancos.size(); i++){
						idBancos.append(""+listBancos.get(i)+",");
					}
		
					if(idBancos.length()>0){
						idBancos.delete(idBancos.length()-1, idBancos.length());
						bancos = idBancos.toString();
					}else{
						bancos = "";
					}
				}
		
				StringBuffer idChequeras = new StringBuffer();
				HashSet<String> hsChequeras = new HashSet<>();
				hsChequeras.addAll(listChequeras);
				listChequeras.clear();
				listChequeras.addAll(hsChequeras);
		
				if(listChequeras.size()>0){
					
					for(int i=0; i<listChequeras.size(); i++){
						idChequeras.append("'"+listChequeras.get(i)+"',");
					}
					
					if(idChequeras.length()>0){
						idChequeras.delete(idChequeras.length()-1, idChequeras.length());
						chequeras = idChequeras.toString();
					}else{
						chequeras = "";
					}
				}
		
				StringBuffer noEmpresas = new StringBuffer();
				HashSet<String> hsEmpresas = new HashSet<>();
				hsEmpresas.addAll(listEmpresas);
				listEmpresas.clear();
				listEmpresas.addAll(hsEmpresas);
				
				if(listEmpresas.size()>0){
					
					for(int i=0; i<listEmpresas.size(); i++){
						noEmpresas.append(""+listEmpresas.get(i)+",");
						numEmpresas++;
					}
					
					if(noEmpresas.length()>0){
						noEmpresas.delete(noEmpresas.length()-1, noEmpresas.length());
						empresas = noEmpresas.toString();
					}else{
						empresas = "";
					}
				}
				
				List<PosicionBancariaDTO> listC = new ArrayList<>();
				listC = posicionBancariaDao.obtenCuentasEmpresa(seleccion, bancos, chequeras, empresas, divisa);
				
				int diasDif = funciones.diasEntreFechas(funciones.ponerFechaDate(fechaIni), funciones.ponerFechaDate(fechaFin)) + 1;
				
				int noColumnas = (diasDif)+2;

		        List<PosicionBancariaDTO> listSaldo = new ArrayList<PosicionBancariaDTO>();
		        
		        for(int i=0; i<listC.size(); i++){
		        	
		        	HashMap<String, String> cols = new HashMap<>();
		        	cols.put("descBanco", listC.get(i).getDescBanco());
		        	cols.put("idChequera", listC.get(i).getIdChequera());
					cols.put("nomEmpresa", listC.get(i).getNomEmpresa());
					cols.put("idDivisa", listC.get(i).getIdDivisa());
					
		        	for(int j=0; j<noColumnas-2; j++) {
						
						String fecha = funciones.ponerFechaSola(funciones.modificarFecha("d", j, funciones.ponerFechaDate(fechaIni)));
			        	String name = "col" + j;

			        	listSaldo = posicionBancariaDao.obtieneSaldoFinal(listC.get(i).getIdChequera(), fecha, divisa);
			        	cols.put(name, ""+listSaldo.get(0).getImporte());
					}
		        	listResult.add(cols);
				}				
		        
			return listResult;
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaBusiness, M:repSaldos2");
		}
		
		return listResult;
	}
	
	
	@Override
	public Map<String, String> insertarPlantilla(List<String> listBancos, List<String> listChequeras,
												  List<String> listEmpresas, List<String> listOrden,
												  String nomPlantilla, String consulta,
												  String fechaIni, String fechaFin,
												  int seleccion, String divisa) {
		
		Map<String, String> result = new HashMap<>();
		result.put("error", "");
		result.put("msgUsuario", "");
		
		try{
			
			StringBuffer idBancos = new StringBuffer();
			
			/** ELIMINA LOS REGISTROS REPETIDOS	**/
			//No paso por proceso de eliminar los repetidos xq los ordena y se pierde el orden de seleccion.
			
			/*HashSet<String> hsBancos = new HashSet<>();
			hsBancos.addAll(listBancos);
			listBancos.clear();
			listBancos.addAll(hsBancos);*/
			/*************************************/
			
			if(listBancos.size()>0){
				for(int i=0; i<listBancos.size(); i++){
						idBancos.append(""+listBancos.get(i)+",");
				}
				
				if(idBancos.length()>0){
					idBancos.delete(idBancos.length()-1, idBancos.length());
				}
			}
			
			StringBuffer idChequeras = new StringBuffer();
			
			/*HashSet<String> hsChequeras = new HashSet<>();
			hsChequeras.addAll(listChequeras);
			listChequeras.clear();
			listChequeras.addAll(hsChequeras);
			*/
			
			if(listChequeras.size()>0){
				
				for(int i=0; i<listChequeras.size(); i++){
					idChequeras.append(""+listChequeras.get(i)+",");
				}
				
				if(idChequeras.length()>0){
					idChequeras.delete(idChequeras.length()-1, idChequeras.length());
				}
			}
			
			StringBuffer noEmpresas = new StringBuffer();
			/*HashSet<String> hsEmpresas = new HashSet<>();
			hsEmpresas.addAll(listEmpresas);
			listEmpresas.clear();
			listEmpresas.addAll(hsEmpresas);
			*/
			if(listEmpresas.size()>0){
				
				for(int i=0; i<listEmpresas.size(); i++){
						noEmpresas.append(""+listEmpresas.get(i)+",");
				}
				
				if(noEmpresas.length()>0){
					noEmpresas.delete(noEmpresas.length()-1, noEmpresas.length());
				}
			}
			
			StringBuffer orden = new StringBuffer();
			/*HashSet<String> hsOrden = new HashSet<>();
			hsOrden.addAll(listOrden);
			listOrden.clear();
			listOrden.addAll(hsOrden);
			*/
			if(listOrden.size()>0){
				
				for(int i=0; i<listOrden.size(); i++){
						orden.append(""+listOrden.get(i)+",");
				}
				
				if(orden.length()>0){
					orden.delete(orden.length()-1, orden.length());
				}
			}
			
			int folio =  posicionBancariaDao.obtenerFolioPlantilla();
			
			result = posicionBancariaDao.insertarPlantilla(idBancos.toString(), idChequeras.toString(), 
															noEmpresas.toString(), orden.toString(),
														    nomPlantilla,consulta, fechaIni, 
														    fechaFin, seleccion, divisa, folio);
				    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Consultas, C:PosicionBancariaBusinessImpl, M:insertarPlantilla");	
		}
		return result;
	}
	
	@Override
	public UsuarioPlantilla obtenerPlantilla(int idPlantilla) {
		
		UsuarioPlantilla plantilla = new UsuarioPlantilla();
		
		try{
		
			plantilla = posicionBancariaDao.obtenerPlantilla(idPlantilla);
				    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Consultas, C:PosicionBancariaBusinessImpl, M:obtenerPlantilla");	
		}
		return plantilla;
	}
	
	@Override
	public Map<String,String> eliminarPlantilla(int idPlantilla) {
		
		Map<String, String> result = new HashMap<>();
		result.put("error", "");
		result.put("msgUsuario", "");
		
		try{
		
			result = posicionBancariaDao.eliminarPlantilla(idPlantilla);
				    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Consultas, C:PosicionBancariaBusinessImpl, M:eliminarPlantilla");	
		}
		return result;
	}
	
}
