package com.webset.set.flujos.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import net.sf.jasperreports.engine.JRDataSource;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.flujos.dto.PosicionBancariaDto;
import com.webset.set.flujos.service.CashFlowNService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.utils.tools.Utilerias;

public class CashFlowNAction{
	Bitacora bitacora = new Bitacora();
	Contexto contexto = new Contexto();
	Funciones funciones = new Funciones();
	CashFlowNService cashFlowNService;
	
	@DirectMethod
	public List<PosicionBancariaDto> obtenHeadsCash(int noEmpresa, String sFecIni, String sFecFin, int tipoReporte){
		List<PosicionBancariaDto> listDatos = new ArrayList<PosicionBancariaDto>();
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			switch (tipoReporte) {
				case 0:
				case 1: //Diario y Semanal
					listDatos = obtenHeadsCashFlow(noEmpresa, sFecIni, sFecFin, tipoReporte);
					break;
				case 2: //Mensual
					listDatos = obtenHeadsCashF(noEmpresa, sFecIni, sFecFin, tipoReporte);
					break;
			}
			
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Flujo, C: CashFlowNAction, M: obtenHeadsCash");
		}
		return listDatos;
	}
	
	@DirectMethod
	public List<PosicionBancariaDto> obtenHeadsCashF(int noEmpresa, String sFecIni, String sFecFin, int tipoReporte){
		StringBuffer columns = new StringBuffer();
		StringBuffer fields = new StringBuffer();
		List<PosicionBancariaDto> listDatos = new ArrayList<PosicionBancariaDto>();
		PosicionBancariaDto dtoDatos = new PosicionBancariaDto();
		String name = "";
		String mesGrid = "";
		
		int iMesIni = Integer.parseInt(sFecIni.substring(5, 7));
		int iMesFin = Integer.parseInt(sFecFin.substring(5, 7));
		int year = Integer.parseInt(sFecIni.substring(0, 4));
		int iMeses = 0;
		int yearIni = 0;
		int mesIni = 0;
		boolean inicia = false;
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			if(Integer.parseInt(sFecIni.substring(0, 4)) == Integer.parseInt(sFecFin.substring(0, 4)))
				 iMeses = (iMesFin - iMesIni);
			else {
				yearIni = Integer.parseInt(sFecIni.substring(0, 4));
				mesIni = iMesIni;
				
				while (yearIni < Integer.parseInt(sFecFin.substring(0, 4)) || (mesIni != iMesFin)){
					if(mesIni == 12) {
						mesIni = 1;
						iMeses++;
						yearIni++;
					}else {
						mesIni++;
						iMeses++;
					}
				}
			}
			
			//iDias = funciones.diasEntreFechas(funciones.ponerFechaDate(sFecIni), funciones.ponerFechaDate(sFecFin)) + 1;
			columns.append("[");
			fields.append("[");
			
			fields.append("{");
			fields.append(" name: 'idRubro'},");
			
			columns.append("{");
			columns.append("header: 'RUBRO',");
			columns.append("width: 80,");
			columns.append("align: 'left',");
			columns.append("css: 'text-align:left;',");
			columns.append("dataIndex: 'idRubro',");
			columns.append("hidden: true,");
			columns.append("renderer: this.colores}, ");
	        
			
			fields.append("{");
			fields.append(" name: 'descripcion'},");
			
			columns.append("{");
			columns.append("header: 'DESCRIPCI�N',");
			columns.append("width: 220,");
			columns.append("align: 'left',");
			columns.append("css: 'text-align:left;',");
			columns.append("dataIndex: 'descripcion',");
			columns.append("renderer: this.colores}, ");
	        
			for(int i=0; i<=(iMeses); i++) {
				if(Integer.parseInt(sFecIni.substring(0, 4)) == Integer.parseInt(sFecFin.substring(0, 4)))
					mesGrid = funciones.nombreMes(iMesIni + i);
				else {
					if(inicia){
						iMesIni++;
						mesGrid = funciones.nombreMes(iMesIni) + year;
					}else
						mesGrid = funciones.nombreMes(iMesIni + i) + year;
				}
				if(!inicia)
					name = "col" + (iMesIni + i) + year;
				else {
					name = "col" + (iMesIni) + year;
				}
				fields.append("{");
				fields.append(" name: '"+ name +"'}");
				
				columns.append("{");
				columns.append("header: '"+ mesGrid +"' ,");
				columns.append("width: 120,");
				columns.append("align: 'right',");
				columns.append("css: 'text-align:right;',");
				columns.append("dataIndex: '"+ name +"'}");
				
				if(i != iMeses){
					columns.append(",");
					fields.append(",");
				}
				if((iMesIni + i) == 12) {
					year++;
					iMesIni = 0;
					inicia = true;
				}
			}
			fields.append(",{");
			fields.append(" name: 'totales'}");
			
			columns.append(",{");
			columns.append("header: 'TOTALES',");
			columns.append("width: 100,");
			columns.append("align: 'right',");
			columns.append("css: 'text-align:right;',");
			columns.append("dataIndex: 'totales'}");
			
			fields.append(",{");
			fields.append(" name: 'color'}");
			
			columns.append("]");
			fields.append("]");
			
			//dtoDatos.setNomReporte(nombreReporte(tipoReporte));
			dtoDatos.setNomReporte("FLUJO DE EFECTIVO");
			dtoDatos.setTipoPosicion(tipoReporte);
			dtoDatos.setColumnas(columns.toString());
			dtoDatos.setFields(fields.toString());
			listDatos.add(dtoDatos);
			
			System.out.println("Columnas: " + columns.toString());
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Flujo, C: CashFlowNAction, M: obtenHeadsCash");
		}
		return listDatos;
	}
	
	@DirectMethod
	public PosicionBancariaDto cashFlowDatos(String param) {
		Gson gson = new Gson();
		List<Map<String, String>> parametros = gson.fromJson(param, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		PosicionBancariaDto dtoResult = new PosicionBancariaDto();
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			cashFlowNService = (CashFlowNService)contexto.obtenerBean("cashFlowNBusiness");
			dtoResult = cashFlowNService.cashFlowDatos(parametros);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:CashFlowNAction, M:CashFlowDatos");
		}
		return dtoResult;
	}
	
	@DirectMethod
	public String generaExcel(String param) {
		Gson gson = new Gson();
		List<Map<String, String>> parametros = gson.fromJson(param, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		String resp = "";
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			cashFlowNService = (CashFlowNService)contexto.obtenerBean("cashFlowNBusiness");
			resp = cashFlowNService.generaExcel(parametros);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:CashFlowNAction, M:generaExcel");
		}
		return resp;
	}
	

	public List<PosicionBancariaDto> obtenHeadsCashFlow(int noEmpresa, String sFecIni, String sFecFin, int tipoReporte){
			StringBuffer columns = new StringBuffer();
			StringBuffer fields = new StringBuffer();
			List<PosicionBancariaDto> listDatos = new ArrayList<PosicionBancariaDto>();
			PosicionBancariaDto dtoDatos = new PosicionBancariaDto();
			
			GregorianCalendar calFecIni = new GregorianCalendar();
			GregorianCalendar calFecFin = new GregorianCalendar();
			
			Date fecIni = funciones.ponerFechaDate(funciones.cambiarFecha(sFecIni));
			Date fecFin = funciones.ponerFechaDate(funciones.cambiarFecha(sFecFin));
			String nomEncabezado = "";
			String nomCol = "";
			
			String nomEncabezado2 = "";
			String nomCol2 = "";
			
			String nomEncabezado3 = "";
			String nomCol3 = "";
			
			int periodo;
			int diaIni = 0;
			int finMes;
			int finMesSem = 0;
			int totSem = 0;
			int totSemE = 0;
			
			boolean esDomingo = false;
			boolean esFinMes = false;
			
			int iMesIni = Integer.parseInt(sFecIni.substring(5, 7));
			int iMesFin = Integer.parseInt(sFecFin.substring(5, 7));
			int year = Integer.parseInt(sFecIni.substring(0, 4));
			
			try {
				if (Utilerias.haveSession(WebContextManager.get())) {
				periodo = funciones.diasEntreFechas(fecIni, fecFin);
				calFecIni.setTime(fecIni);
				finMes = calFecIni.getActualMaximum(Calendar.DAY_OF_MONTH);
				
				columns.append("[");
				fields.append("[");
				
				fields.append("{");
				fields.append(" name: 'idRubro'},");
				
				columns.append("{");
				columns.append("header: 'RUBRO',");
				columns.append("width: 80,");
				columns.append("align: 'left',");
				columns.append("css: 'text-align:left;',");
				columns.append("dataIndex: 'idRubro',");
				columns.append("hidden: true,");
				columns.append("renderer: this.colores}, ");
		        
				fields.append("{");
				fields.append(" name: 'descripcion'},");
				
				columns.append("{");
				columns.append("header: 'DESCRIPCI�N',");
				columns.append("width: 220,");
				columns.append("align: 'left',");
				columns.append("css: 'text-align:left;',");
				columns.append("dataIndex: 'descripcion',");
				columns.append("renderer: this.colores}, ");
				
				//mesGrid = funciones.nombreMes(iMesIni + i) + year;}
				
				for(int i=0; i<=(periodo); i++) {
					calFecIni.setTime(fecIni);
					diaIni = funciones.obtenerDia(fecIni);
					nomEncabezado = funciones.nombreDia(calFecIni.get(Calendar.DAY_OF_WEEK)) + " " + diaIni;
					nomCol = "col" + funciones.ponerFecha(fecIni).substring(0, 10);
					if(diaIni == finMes)
						esFinMes = true;
					
					fields.append("{");
					fields.append(" name: '"+ nomCol +"'}");
					columns.append("{");
					columns.append("header: '"+ nomEncabezado +"' ,");
					columns.append("width: 120,");
					columns.append("align: 'right',");
					columns.append("css: 'text-align:right;',");
					columns.append("dataIndex: '"+ nomCol +"',");
					columns.append("hidden: "+ ocultaColumnas(tipoReporte, calFecIni) +"}");
					
					if(i != (periodo + 2)) {
						columns.append(",");
						fields.append(",");
					}
					
					if(calFecIni.get(Calendar.DAY_OF_WEEK) == 6){
						calFecIni.setTime(fecIni);
						totSem = calFecIni.get(Calendar.WEEK_OF_YEAR);
						totSemE = totSemE + 1 ;
						nomCol2 = "colTotSem" + totSem;
						nomEncabezado2 = "SEMANA " + totSemE;
						
						fields.append("{");
						fields.append(" name: '"+ nomCol2 +"'}");
						columns.append("{");
						columns.append("header: '"+ nomEncabezado2 +"' ,");
						columns.append("width: 120,");
						columns.append("align: 'right',");
						columns.append("css: 'text-align:right;',");
						columns.append("dataIndex: '"+ nomCol2 +"',");
						if(tipoReporte == 2)
							columns.append("hidden: true }");
						else
							columns.append("hidden: false }");
						
						if(i != (periodo + 2)) {
							columns.append(",");
							fields.append(",");
						}
					}
					
					if(esFinMes) {
						nomCol3 = "colTotMes" + nomCol.substring(6,8)+ nomCol.substring(9,13);
						nomEncabezado3 = "TOTAL MENSUAL";
						esFinMes = false;
						finMesSem = 0;
						diaIni = 0;
						totSemE = 0;
						fields.append("{");
						fields.append(" name: '"+ nomCol3 +"'}");
						
						columns.append("{");
						columns.append("header: '"+ nomEncabezado3 +"' ,");
						columns.append("width: 120,");
						columns.append("align: 'right',");
						columns.append("css: 'text-align:right;',");
						columns.append("dataIndex: '"+ nomCol3 +"',");
						columns.append("hidden: false}");
						if(i != (periodo + 2)) {
							columns.append(",");
							fields.append(",");
						}
					}
					
					fecIni = funciones.modificarFecha("d", 1, fecIni);
					calFecIni.setTime(fecIni);
					finMes = calFecIni.getActualMaximum(Calendar.DAY_OF_MONTH);
				}
				fields.append("{");
				fields.append(" name: 'totales'}");
				
				columns.append("{");
				columns.append("header: 'TOTALES',");
				columns.append("width: 100,");
				columns.append("align: 'right',");
				columns.append("css: 'text-align:right;',");
				columns.append("dataIndex: 'totales'}");
				
				fields.append(",{");
				fields.append(" name: 'color'}");
				
				columns.append("]");
				fields.append("]");
				
				//dtoDatos.setNomReporte(nombreReporte(tipoReporte));
				dtoDatos.setNomReporte("FLUJO DE EFECTIVO");
				dtoDatos.setTipoPosicion(tipoReporte);
				dtoDatos.setColumnas(columns.toString());
				dtoDatos.setFields(fields.toString());
				listDatos.add(dtoDatos);
				
				System.out.println("Columnas: " + columns.toString());
				}
			}catch(Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Flujo, C: CashFlowNAction, M: obtenHeadsCash");
			}
			return listDatos;
		}

	@DirectMethod
	public List<PosicionBancariaDto> consultarDetalleFlujo(String param, String idRubro){
		List<PosicionBancariaDto> listDatos = new ArrayList<PosicionBancariaDto>();
		Gson gson = new Gson();
		List<Map<String, String>> parametros = gson.fromJson(param, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			cashFlowNService = (CashFlowNService)contexto.obtenerBean("cashFlowNBusiness");
			listDatos = cashFlowNService.consultarDetalleFlujo(parametros, idRubro);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Flujo, C: CashFlowNAction, M: obtenHeadsCash");
		}
		return listDatos;
	}
	
	public boolean ocultaColumnas(int tipoReporte, GregorianCalendar calFecIni) {
		switch (tipoReporte) {
			case 0:
				switch (calFecIni.get(Calendar.DAY_OF_WEEK)) {
					case 1:	
					case 7:
						return true;
		    	}
				break;
			case 1:
				switch (calFecIni.get(Calendar.DAY_OF_WEEK)) {
					case 1:
			        case 2:
			        case 3:
			        case 4:
			        case 5:
			        case 6:
			        case 7:
			        	return true;
		    	}
				break;
			case 2:
				switch (calFecIni.get(Calendar.DAY_OF_WEEK)) {
					case 1:
					case 2:
			        case 3:
			        case 4:
			        case 5:
			        case 6:
			        case 7:
			        	return true;
		    	}
				break;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@DirectMethod
	public JRDataSource reporteDetalleFlujo(Map datos, ServletContext context) {
		JRDataSource jrDataSource = null;
		PosicionBancariaDto dtoIn = new PosicionBancariaDto();

		try {
			
			System.out.println("no mpresa "+datos.get("noEmpresa"));
			System.out.println("fecIni "+datos.get("fecIni"));
			System.out.println("fecFin "+datos.get("fecFin"));
			System.out.println("tipoReporte "+datos.get("tipoReporte"));
			System.out.println("idDivisa "+datos.get("idDivisa"));
			System.out.println("idRubro "+datos.get("idRubro"));
			System.out.println("dataIndex "+datos.get("dataIndex"));
			
			cashFlowNService = (CashFlowNService)contexto.obtenerBean("cashFlowNBusiness", context);
			
			dtoIn.setNoEmpresa(datos.get("noEmpresa")!= null ? Integer.parseInt(datos.get("noEmpresa").toString()) : 0);
			dtoIn.setFecIni(datos.get("fecIni").toString());
			dtoIn.setFecFin(datos.get("fecFin").toString());
			dtoIn.setTipoReporte(datos.get("tipoReporte")!= null ? Integer.parseInt(datos.get("tipoReporte").toString()) : 0);
			dtoIn.setIdDivisa(datos.get("idDivisa").toString());
			dtoIn.setIdRubro(datos.get("idRubro").toString());
			dtoIn.setDataIndex(datos.get("dataIndex").toString());
			jrDataSource = cashFlowNService.reporteDetalleFlujo(dtoIn);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:CashFlowNAction, M:reporteDetalleFlujo");	
		}
		return jrDataSource;
	}
	
	@DirectMethod
	public List<PosicionBancariaDto> comboGrupoRubro(String idTipoMovto){
		
				cashFlowNService = (CashFlowNService)contexto.obtenerBean("cashFlowNBusiness");
				return cashFlowNService.comboGrupoRubro(idTipoMovto);
				
		
	}
	
	@DirectMethod
	public List<PosicionBancariaDto> comboRubro(int idGrupo) {
		cashFlowNService = (CashFlowNService)contexto.obtenerBean("cashFlowNBusiness");
		return cashFlowNService.comboRubro(idGrupo);
	}
	
	@DirectMethod
	public String reclasificaMovtos(int noFolioDet, int idGrupo, int idRubro,int interes,String concepto,String fecValor) {
		cashFlowNService = (CashFlowNService)contexto.obtenerBean("cashFlowNBusiness");
		return cashFlowNService.reclasificaMovtos(noFolioDet, idGrupo, idRubro, interes, concepto, fecValor);
	}
}

