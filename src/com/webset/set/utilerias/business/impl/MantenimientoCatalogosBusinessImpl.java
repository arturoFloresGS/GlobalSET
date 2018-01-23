package com.webset.set.utilerias.business.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dao.MantenimientoCatalogosDao;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.service.MantenimientoCatalogosService;
import com.webset.set.utileriasmod.dto.MantenimientoCatalogosDto;
import com.webset.utils.tools.Utilerias;

public class MantenimientoCatalogosBusinessImpl implements MantenimientoCatalogosService{
	Bitacora bitacora = new Bitacora();
	MantenimientoCatalogosDao objMantenimientoCatalogosDao;
	//MantenimientoCatalogosDto objMantenimientoCatalogosDto;
	int lastCol = 0;
	MantenimientoCatalogosDto objMantenimientoCatalogosDto = new MantenimientoCatalogosDto();

	public List<MantenimientoCatalogosDto> llenaComboCatalogos(){
		return objMantenimientoCatalogosDao.llenaComboCatalogos();
	}
	
	@Override
	public List<Map<String, Object>> llenaGridCatalogos(String nombreCatalogo,String noEmpresa,String noBanco,String idChequera){
		Map<String, String> colOrden = objMantenimientoCatalogosDao.getColumnas(nombreCatalogo);
		
		String columnas = colOrden.get("campos");
		
		String[] vecColumnas = columnas.split("\\|");
		
		Gson gson = new Gson();

		Map<String, String> datos = gson.fromJson(
				colOrden.get("botones"), 
				new TypeToken<Map<String, String>>() {}.getType());
		
		String orden = datos.get("orden");
				
		return objMantenimientoCatalogosDao.llenaGrid(nombreCatalogo,noEmpresa,noBanco,idChequera, vecColumnas, orden);
	}
	public List<LlenaComboGralDto> llenarComboBancos(int noEmpresa){
		return objMantenimientoCatalogosDao.llenarComboBancos(noEmpresa);
	}
	public List<LlenaComboGralDto> llenarComboChequeras(int noBanco,int noEmpresa){
		return objMantenimientoCatalogosDao.llenarComboChequeras(noBanco, noEmpresa);
	}
	
	public String addRecord(String catalogo, String key,String tipo) {
		System.out.println(key+" Bussiness");
		return objMantenimientoCatalogosDao.addRecord(catalogo,key,tipo);
	}
	public int deleteRecord(String catalogo, String key,String valor) {
		return objMantenimientoCatalogosDao.deleteRecord(catalogo,key,valor);
	}
	public int saveRecord(String catalogo,
			List<Map<String, String>> matRegistros,
			List<Map<String, String>> defCols) {
		
		return objMantenimientoCatalogosDao.saveRecord(catalogo,convertirFechas(matRegistros, defCols),defCols);
	}
	private List<Map<String, String>> convertirFechas(List<Map<String, String>> matRegistros,
			List<Map<String, String>> defCols) {
		Funciones fun = new Funciones();
		for (Map<String, String> renglon : matRegistros) {
			for (Map<String, String> columna : defCols) {
				if (columna.get("type").equals("date")) {
					renglon.put(columna.get("name"), 
							fun.cambiarFechaGregoriana(
									renglon.get(columna.get("name"))));
				}
			}
		}
		
		return matRegistros;
	}

//**************************************
	public MantenimientoCatalogosDao getObjMantenimientoCatalogosDao() {
		return objMantenimientoCatalogosDao;
	}

	public void setObjMantenimientoCatalogosDao(
			MantenimientoCatalogosDao objMantenimientoCatalogosDao) {
		this.objMantenimientoCatalogosDao = objMantenimientoCatalogosDao;
	}
	@Override
	public MantenimientoCatalogosDto getBodyReport() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public HSSFWorkbook reporteCatalogo(String catalogo) {
		HSSFWorkbook hb=null;
		try {
			//n|id_caja|s|desc_caja|s|ubicacion|s|b_activa|n|equivale_banco|n|fondo_fijo_mn|n|fondo_fijo_dls
			
			Map<String, String> colOrden = objMantenimientoCatalogosDao.getColumnas(catalogo);
			
			String columnas = colOrden.get("campos");
			
			String[] col = columnas.split("\\|");
			
			Gson gson = new Gson();

			Map<String, String> datos = gson.fromJson(
					colOrden.get("botones"), 
					new TypeToken<Map<String, String>>() {}.getType());
			
			String orden = datos.get("orden");
			
			String campos = "";
			List<String> titulosKeys = new ArrayList<String>();
			
			for (int i = 1; i < col.length; i+=2) {
				titulosKeys.add(col[i]);
				campos += col[i] + ",";
			}
			
			campos = campos.substring(0, campos.length()-1);
			
			String[] keys = titulosKeys.toArray(new String[titulosKeys.size()]);
			
			hb=Utilerias.generarExcel(keys, 
					objMantenimientoCatalogosDao.reporteCatalogo(catalogo, campos, keys, orden), keys);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: MantenimientoCatalogosBusinessImpl, M: reporteCatalogos");
		}return hb;
	}

}