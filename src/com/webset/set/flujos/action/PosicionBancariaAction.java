package com.webset.set.flujos.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.flujos.dto.PosicionBancariaDto;
import com.webset.set.flujos.service.PosicionBancariaService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.utils.tools.Utilerias;


public class PosicionBancariaAction {
	Bitacora bitacora = new Bitacora();
	Contexto contexto = new Contexto();
	Funciones funciones = new Funciones();
	PosicionBancariaService posicionBancariaService = (PosicionBancariaService)contexto.obtenerBean("posicionBancariaBusinessImpl");
	
	@DirectMethod
	public List<PosicionBancariaDto> buscaDivisas() {
		List<PosicionBancariaDto> result = null;
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			result = posicionBancariaService.buscaDivisas();
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaAction, M:buscaDivisas");
		}
		return result;
	}
	
	@DirectMethod
	public List<PosicionBancariaDto> obtenHeads(int tipoOpcion){
		StringBuffer columns = new StringBuffer();
		StringBuffer fields = new StringBuffer();
		List<PosicionBancariaDto> listDatos = new ArrayList<PosicionBancariaDto>();
		PosicionBancariaDto dtoDatos = new PosicionBancariaDto();
		List<String> header = new ArrayList<String>();
		String name = "";
		int mul = 0;
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			switch (tipoOpcion) {
				case 1:	//Banco/Empresa
					header.add("No. Banco");
					header.add("Banco");
					header.add("No. Empresa");
					header.add("Empresa");
					break;
				case 2:	//Bancos
					header.add("No. Banco");
					header.add("Banco");
					break;
				case 3:	//Cuentas
					header.add("Empresa");
					header.add("No. Banco");
					header.add("Banco");
					header.add("Chequera");
					header.add("Desc. Chequera");
					break;
				case 4:	//Empresas
					header.add("No. Empresa");
					header.add("Empresa");
					break;
				case 5:	//Rubro
					header.add("No. Rubro");
					header.add("Rubro");
					break;
				case 6:
					header.add("No. Agrupaci�n");
					header.add("Agrupaci�n");
					break;
			}
			columns.append("[");
			fields.append("[");
		
		    /*fields.append("{name: 'indoor', type: 'bool'}, ");
			columns.append("{xtype: 'checkcolumn', header: 'indoor', dataIndex: 'indoor', width: 55}, ");*/
			columns.append("  {header: '', width: 55, dataIndex: 'indoor', type: 'checkcolumn'}, ");
			
			for(int i=0; i<header.size(); i++) {
				name = "col" + i;
				mul = 16;
				if(header.get(i).indexOf("No.") >= 0 || header.get(i).indexOf("Desc.") >= 0) mul = 7;
				
				fields.append("{");
				fields.append(" name: '"+ name +"'}");
				
				columns.append("{");
				columns.append("header: '"+ header.get(i) +"',");
				columns.append("width: "+ header.get(i).length() * mul +",");
				columns.append("dataIndex: '"+ name +"'}");
				
				if(i != header.size()-1){
					columns.append(",");
					fields.append(",");
				}
			}
			columns.append("]");
			fields.append("]");
			
			dtoDatos.setTipoPosicion(tipoOpcion);
			dtoDatos.setColumnas(columns.toString());
			dtoDatos.setFields(fields.toString());
			listDatos.add(dtoDatos);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: PosicionBancariaAction, M: obtenerHeads");
		}
		return listDatos;
	}
	
	/*@DirectMethod
	public List<PosicionBancariaDto> posicionBancaria(int iPosicion) {
		List<PosicionBancariaDto> result = null;
		
		try {
			result = posicionBancariaService.posicionBancaria(iPosicion);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaAction, M:posicionBancaria");
		}
		return result;
	}*/
	

	@DirectMethod
	public List<PosicionBancariaDto>posicionBancaria(int iPosicion){
		List<PosicionBancariaDto> listConsCon = new ArrayList<PosicionBancariaDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			posicionBancariaService  = (	PosicionBancariaService) contexto.obtenerBean("posicionBancariaBusinessImpl");
		    listConsCon = posicionBancariaService.posicionBancaria(iPosicion);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaAction, M:posicionBancaria");
		}
		return listConsCon;
	}
	
	//ahorita este metodo es exclusivamente para el diario de tesoreria, posteriormente se juntara a los demas reportes para que sea dinamico en el grid
	@DirectMethod
	public List<PosicionBancariaDto> buscaDetalleBE(String datos, String param) {
		Gson gson = new Gson();
		List<Map<String, String>> datosGrid = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<Map<String, String>> parametros = gson.fromJson(param, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<PosicionBancariaDto> listResult = new ArrayList<PosicionBancariaDto>();
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			listResult = posicionBancariaService.buscaDetalleBE(datosGrid, parametros);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaAction, M:buscaDetalleBE");
		}
		return listResult;
	}
	
	@DirectMethod
	public List<PosicionBancariaDto> obtenHeadsCash(int tipoReporte, String sFecIni, String sFecFin){
		StringBuffer columns = new StringBuffer();
		StringBuffer fields = new StringBuffer();
		List<PosicionBancariaDto> listDatos = new ArrayList<PosicionBancariaDto>();
		PosicionBancariaDto dtoDatos = new PosicionBancariaDto();
		String name = "";
		String fechaGrid = "";
		int iDias = 0;
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			iDias = funciones.diasEntreFechas(funciones.ponerFechaDate(sFecIni), funciones.ponerFechaDate(sFecFin)) + 1;
			columns.append("[");
			fields.append("[");
			
			
			//Muestra el RUBRO EN EL GRID
			/*fields.append("{");
			fields.append(" name: 'rubro'},");
			
			columns.append("{");
			columns.append("header: 'RUBRO',");
			columns.append("width: 50,");
			columns.append("dataIndex: 'rubro'}, ");*/
			
			fields.append("{");
			fields.append(" name: 'descripcion'},");
			
			columns.append("{");
			columns.append("header: 'DESCRIPCI�N',");
			columns.append("width: 200,");
			columns.append("dataIndex: 'descripcion',");
			columns.append("renderer: this.colores}, ");
	        
			for(int i=0; i<iDias; i++) {
				fechaGrid = funciones.ponerDiaLetraYFecha(funciones.modificarFecha("d", i, funciones.ponerFechaDate(sFecIni)), true);
				fechaGrid = fechaGrid.substring(0, 3) + " , " + funciones.ponerFechaSola(funciones.modificarFecha("d", i, funciones.ponerFechaDate(sFecIni)));
				name = "col" + i;
				
				fields.append("{");
				fields.append(" name: '"+ name +"'}");				
				
				columns.append("{");
				columns.append("header: '"+ fechaGrid +"',");
				columns.append("width: "+ sFecIni.length() * 9.5 +",");
				columns.append("dataIndex: '"+ name +"'}");
				
				if(i != iDias-1){
					columns.append(",");
					fields.append(",");
				}
			}
			
			fields.append(",{");
			fields.append(" name: 'totales'}");
			
			columns.append(",{");
			columns.append("header: 'TOTALES',");
			columns.append("width: 100,");
			columns.append("dataIndex: 'totales'}");
			
			fields.append(",{");
			fields.append(" name: 'color'}");
			
			columns.append("]");
			fields.append("]");
			
			dtoDatos.setNomReporte(nombreReporte(tipoReporte));
			dtoDatos.setTipoPosicion(tipoReporte);
			dtoDatos.setColumnas(columns.toString());
			dtoDatos.setFields(fields.toString());
			listDatos.add(dtoDatos);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: PosicionBancariaAction, M: obtenerHeads");
		}
		return listDatos;
	}
	
	@DirectMethod
	public PosicionBancariaDto posicionBancariaCash(String datos, String param) {
		Gson gson = new Gson();
		List<Map<String, String>> datosGrid = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<Map<String, String>> parametros = gson.fromJson(param, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		PosicionBancariaDto listResult = new PosicionBancariaDto();
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			listResult = posicionBancariaService.posicionBancariaCash(datosGrid, parametros);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaAction, M:buscaDetalleBE");
		}
		return listResult;
	}
	
	public String nombreReporte(int tipoReporte) {
		String x = "";
		
		switch(tipoReporte) {
			case 0:
				return "DIARIO TESORERIA";
			case 1:
				return "FICHA VALOR EMPRESA";
			case 2:
				return "FICHA VALOR CUENTA";
			case 3:
				return "FICHA VALOR GLOBAL";
			case 4:
				return "MOVIMIENTOS TESORERIA";
			case 5:
				return "SALDOS POR MONTO";
			case 6:
				return "REPORTE DE SALDOS";
		}
		return x;
	}
}