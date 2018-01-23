package com.webset.set.flujos.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webset.set.flujos.dao.PosicionBancariaDao;
import com.webset.set.flujos.dto.PosicionBancariaDto;
import com.webset.set.flujos.service.PosicionBancariaService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;

@SuppressWarnings("unchecked")
public class PosicionBancariaBusinessImpl implements PosicionBancariaService {
	Bitacora bitacora = new Bitacora();
	PosicionBancariaDao posicionBancariaDao;
	Funciones funciones = new Funciones();
	
	List<PosicionBancariaDto> listFinalReporte = new ArrayList<PosicionBancariaDto>();
	double dSumE = 0;
	double dSumI = 0;
	double dImpRubro = 0;
	double dAcumulado1 = 0;
	double dAcumulado2 = 0;
	String sRubroIni = "";
	String sChequeraIni = "";
	String bancos = "";
	String empresas = "";
	String ctas = "";
	String sTodasEmpresas = "";
	int nEmpresas = 0;
	
	int noEmpresa = 0;
	double totalDerecho = 0 ;
	double totalI = 0;
	double totalE = 0;
	double granTotalI = 0;
	double granTotalE = 0;
	
	List<PosicionBancariaDto> listFlujoEmp = new ArrayList<PosicionBancariaDto>();
	
	public List<PosicionBancariaDto> buscaDivisas() {
		return posicionBancariaDao.buscaDivisas();
	}
	
	public List<PosicionBancariaDto> posicionBancaria(int iPosicion) {
		List<PosicionBancariaDto> listReporte = new ArrayList<PosicionBancariaDto>();
		
		try {
			switch(iPosicion) {
				case 1:  //Bancos/Empresas
					listReporte = posicionBancariaDao.posicionBancariaBE();
					break;
				case 2:  //Bancos
					listReporte = posicionBancariaDao.posicionBancariaB();
					break;
				case 3:  //Cuentas
					listReporte = posicionBancariaDao.posicionBancariaC();
					break;
				case 4:	//Empresa
					listReporte = posicionBancariaDao.posicionBancariaE();
					break;
				case 5:	//Rubros
					listReporte = posicionBancariaDao.posicionBancariaR();
					break;
				case 6:	//Agrupaciones
					break;
			}
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaBusiness, M:posicionBancaria");
		}
		return listReporte;
	}
	
	public List<PosicionBancariaDto> buscaDetalleBE(List<Map<String, String>> datosGrid, List<Map<String, String>> parametros) {
		try {
			diarioTesoreria(datosGrid, parametros);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaBusiness, M:buscaDetalleBE");
		}
		return listFinalReporte;
	}
	
	public List<PosicionBancariaDto> diarioTesoreria(List<Map<String, String>> datosGrid, List<Map<String, String>> parametros) {
		List<PosicionBancariaDto> listReporte = new ArrayList<PosicionBancariaDto>();
			
		try {	
			listReporte = posicionBancariaDao.buscaDetalleBE(datosGrid, parametros);
			
			if(listReporte.size() > 0) {
				sRubroIni = listReporte.get(0).getIdRubro();
				sChequeraIni = listReporte.get(0).getIdChequera();
				
				for(int i=0; i<listReporte.size(); i++) {
					if(!sRubroIni.equals(listReporte.get(i).getIdRubro())) {
						listRetorno(listReporte, i, "R");
						dAcumulado2 += listReporte.get(i).getImporte();
						dImpRubro = 0;
						sRubroIni = listReporte.get(i).getIdRubro();
					}else
						dAcumulado2 += listReporte.get(i).getImporte();
					
					if(!sChequeraIni.equals(listReporte.get(i).getIdChequera())) {
						listRetorno(listReporte, i, "C");
						listRetorno(listReporte, i, "T");
						dSumI = 0;
						dSumE = 0;
						dAcumulado2 = 0;
						dAcumulado2 += listReporte.get(i).getImporte();
						dImpRubro += listReporte.get(i).getImporte();
						
						if(listReporte.get(i).getIdTipoMovto().equals("E"))
							dSumE += listReporte.get(i).getImporte();
						else
							dSumI += listReporte.get(i).getImporte();
						dAcumulado1 = listReporte.get(i).getImporte();
						sRubroIni = listReporte.get(i).getIdRubro();
						sChequeraIni = listReporte.get(i).getIdChequera();
						
					}else {
						if(listReporte.get(i).getIdTipoMovto().equals("E"))
							dSumE += listReporte.get(i).getImporte();
						else
							dSumI += listReporte.get(i).getImporte();
						dAcumulado1 += listReporte.get(i).getImporte();
						dImpRubro += listReporte.get(i).getImporte();
					}
					//Metodo para agregar los datos a la nueva lista
					listRetorno(listReporte, i, "");
				}
				listRetorno(listReporte, 0, "R");
				listRetorno(listReporte, 0, "C");
				listRetorno(listReporte, 0, "T");
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaBusiness, M:buscaDetalleBE");
		}
		return listFinalReporte;
	}
	
	public void listRetorno(List<PosicionBancariaDto> listReporte, int i, String sTotal) {
		String sConcepto = "TOTAL INGRESOS: ";
		double dImporte = dSumI;
		
		try {
			PosicionBancariaDto cons = new PosicionBancariaDto();
			
			if(sTotal.equals("R") || sTotal.equals("C")) {
				cons.setDescBanco("");
				cons.setIdChequera(sTotal.equals("R") ? "" : sChequeraIni);
				cons.setIdGrupo("");
				cons.setIdRubro(sTotal.equals("R") ? sRubroIni : "");
				cons.setFecValor("");
				cons.setFecOperacion("");
				cons.setImporte(sTotal.equals("R") ? dImpRubro : 0);
				cons.setReferencia("");
				cons.setConcepto("");
				cons.setIdTipoMovto("");
				
			}else if(sTotal.equals("T")) {
				for(int x=0; x<3; x++) {
					if(x == 1) {
						cons = new PosicionBancariaDto();
						sConcepto = "TOTAL EGRESOS: ";
						dImporte = dSumE;
					}else if(x == 2){
						cons = new PosicionBancariaDto();
						sConcepto = "TOTAL: ";
						dImporte = dSumI - dSumE;
					}
					cons.setDescBanco("");
					cons.setIdChequera(sConcepto);
					cons.setIdGrupo("");
					cons.setIdRubro("");
					cons.setFecValor("");
					cons.setFecOperacion("");
					cons.setImporte(dImporte);
					cons.setReferencia("");
					cons.setConcepto("");
					cons.setIdTipoMovto("");
					
					if(x < 2) listFinalReporte.add(cons);
				}
			}else if(sTotal.equals("")){
				cons.setDescBanco(listReporte.get(i).getDescBanco());
				cons.setIdChequera(listReporte.get(i).getIdChequera());
				cons.setIdGrupo(listReporte.get(i).getIdGrupo());
				cons.setIdRubro(listReporte.get(i).getIdRubro());
				cons.setFecValor(listReporte.get(i).getFecValor());
				cons.setFecOperacion(listReporte.get(i).getFecOperacion());
				cons.setImporte(listReporte.get(i).getImporte());
				cons.setReferencia(listReporte.get(i).getReferencia());
				cons.setConcepto(listReporte.get(i).getConcepto());
				cons.setIdTipoMovto(listReporte.get(i).getIdTipoMovto());
			}
			listFinalReporte.add(cons);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaBusiness, M:listRetorno");
		}
	}
	
	public PosicionBancariaDto posicionBancariaCash(List<Map<String, String>> datosGrid, List<Map<String, String>> parametros) {
		PosicionBancariaDto listResult;
		
		try {
			
			switch(Integer.parseInt(parametros.get(0).get("tipoReporte"))) {
				case 0:	//Diario de Tesoreria
					diarioTesoreria(datosGrid, parametros);
					break;
				case 1:	//Ficha Valor Empresa
					listResult = new PosicionBancariaDto(fichaValorEmpresa(datosGrid, parametros));
					return listResult;
				case 2:	//Ficha Valor Cuenta
					listResult = new PosicionBancariaDto(fichaValorCuenta(datosGrid, parametros));
					return listResult;
				case 3:	//Ficha Valor Global
					//fichaValorGlobal(datosGrid, sFecIni, sFecFin, iSelec, tipoRep);
					break;
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaBusiness, M:buscaDetalleBE");
		}
		return null;
	}
	
	public List<HashMap> fichaValorEmpresa(List<Map<String, String>> datosGrid, List<Map<String, String>> parametros) {
		List<PosicionBancariaDto> listCtasEmpI = new ArrayList<PosicionBancariaDto>();
		List<PosicionBancariaDto> listCtasEmpE = new ArrayList<PosicionBancariaDto>();
		
		List<HashMap> bodyRow = new ArrayList<HashMap>();
		int iDias = 0;
		boolean banI;
		boolean banE;
		int ii = 0;
		
		try {
			obtieneEmpsBcosCtas(datosGrid, parametros);
			
			listCtasEmpI = posicionBancariaDao.obtieneCuentaEmpresa("I", bancos, ctas, 0, parametros);
			listCtasEmpE = posicionBancariaDao.obtieneCuentaEmpresa("E", bancos, ctas, 0, parametros);
			iDias = funciones.diasEntreFechas(funciones.ponerFechaDate(parametros.get(0).get("fecIni")), funciones.ponerFechaDate(parametros.get(0).get("fecFin"))) + 1;
			
			for(int x=0; x<listFlujoEmp.size(); x++) {
				banI = false;
				banE = false;
				
				for(ii=0; ii<listCtasEmpI.size(); ii++) {
					if(listFlujoEmp.get(x).getNoEmpresa() == listCtasEmpI.get(ii).getNoEmpresa()) {
						banI = true;
						break;
					}
				}
				if(!banI) {
					for(ii=0; ii<listCtasEmpE.size(); ii++) {
						if(listFlujoEmp.get(x).getNoEmpresa() == listCtasEmpE.get(ii).getNoEmpresa()) {
							banE = true;
							break;
						}
					}
				}
				if(banI || banE) {
					
					HashMap cols = new HashMap();
					//Obtiene el saldo inicial de la empresa
					//obtieneSaldoInicial(x, iDias, parametros, cols, "I");
					
					//Con esto agrega al grid la fila con informaci�n de los saldos iniciales
					//bodyRow.add(cols);
					
					//Llena el grid con los ingresos
					llenaGrid(listCtasEmpI, x, iDias, cols, parametros.get(0).get("fecIni"), bodyRow, "I", parametros);
					
					//Llena el grid con los Egresos
					llenaGrid(listCtasEmpE, x, iDias, cols, parametros.get(0).get("fecIni"), bodyRow, "E", parametros);
				}
			}
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaBusiness, M:fichaValorEmpresa");
		}
		return bodyRow;
	}
	
	public void obtieneEmpsBcosCtas(List<Map<String, String>> datosGrid, List<Map<String, String>> parametros) {
		List<PosicionBancariaDto> listCtas = new ArrayList<PosicionBancariaDto>();
		int i = 0;
		
		for(i=0; i<datosGrid.size(); i++) {
			bancos += datosGrid.get(i).get("campo1") + ",";
			empresas += datosGrid.get(i).get("campo2") + ",";
		}
		
		if(Integer.parseInt(parametros.get(0).get("selec")) != 3) {
			listCtas = posicionBancariaDao.obtenerCuentas(bancos.substring(0, bancos.length()-1), empresas.substring(0, empresas.length()-1), Integer.parseInt(parametros.get(0).get("selec")));
			empresas = "";
			bancos = "";
			ctas = "";
			
			for(i=0; i<listCtas.size(); i++) {
				if(empresas.indexOf(listCtas.get(i).getNomEmpresa()) < 0)
					empresas += listCtas.get(i).getNomEmpresa() + ",";
				
				String banc = listCtas.get(i).getIdBanco() + "";
				if(bancos.indexOf(banc) < 0)
					bancos += listCtas.get(i).getIdBanco() + ",";
				ctas += "'" + listCtas.get(i).getIdChequera() + "'" + ",";
			}
		}else
			ctas = empresas;
		empresas = empresas.substring(0, empresas.length()-1);
		bancos = bancos.substring(0, bancos.length()-1);
		ctas = ctas.substring(0, ctas.length()-1);
		
		sTodasEmpresas = "";
		nEmpresas = 0;
		
		listFlujoEmp = posicionBancariaDao.obtieneFlujoEmp(bancos, ctas);
		
		if(listFlujoEmp.size() > 0) {
			nEmpresas = listFlujoEmp.size();
			
			for(i=0; i<listFlujoEmp.size(); i++)
				sTodasEmpresas += listFlujoEmp.get(i).getNoEmpresa() + ":" + listFlujoEmp.get(i).getNomEmpresa() + "|";
		}
	}
	
	public void obtieneSaldoInicial(int x, int iDias, List<Map<String, String>> parametros, HashMap cols, String tipoMovto) {
		List<PosicionBancariaDto> listSaldoEmp = new ArrayList<PosicionBancariaDto>();
		String fecha = "";
		double total = 0;
		boolean chkSaldos = parametros.get(0).get("chkSaldos").equals("true") ? true : false;
		
		for(int i=0; i<iDias; i++) {
			fecha = funciones.ponerFechaSola(funciones.modificarFecha("d", i, funciones.ponerFechaDate(parametros.get(0).get("fecIni"))));
			listSaldoEmp = posicionBancariaDao.obtieneSaldodiario(bancos, ctas, chkSaldos, fecha, listFlujoEmp.get(x).getNoEmpresa(), parametros.get(0).get("divisa"));
			
			if(i==0 && tipoMovto.equals("I")) {
				cols.put("descripcion", "SALDO INICIAL");
				cols.put("color", "#04B404");
			}
			if(listSaldoEmp.size() > 0) {
				cols.put("col" + i, listSaldoEmp.get(0).getImporte());
				total += listSaldoEmp.get(0).getImporte();
			}else
				cols.put("col" + i, 0);
			
			if(i == iDias-1)
				cols.put("totales", total);
		}
	}
	
	public void llenaGrid(List<PosicionBancariaDto> listCtasEmp, int x, int iDias, HashMap cols, String sFecIni, List<HashMap> bodyRow, String tipoMovto, List<Map<String, String>> parametros) {
		int y = 0;
		String rubro = "";
		totalDerecho = 0;
		totalI = 0;
		totalE = 0;
		boolean tieneDato = false;
		
		//Variables para calcular saldo inicial...
		List<PosicionBancariaDto> listSaldoEmp = new ArrayList<PosicionBancariaDto>();
		String fecha = "";
		@SuppressWarnings("unused")
		double saldoInicial = 0;		
		boolean chkSaldos = parametros.get(0).get("chkSaldos").equals("true") ? true : false;
		
		for(y=0; y<listCtasEmp.size(); y++) {
			if(listFlujoEmp.get(x).getNoEmpresa() == listCtasEmp.get(y).getNoEmpresa()) {
				tieneDato = true;
				
				if(y==0 && noEmpresa!=listCtasEmp.get(y).getNoEmpresa()) {	//Esto es para colocar el nombre de la empresa en la segunda columna
					cols = new HashMap();
					HashMap colSaldoInicial = new HashMap();
					
					cols.put("descripcion", listFlujoEmp.get(x).getNomEmpresa());
					noEmpresa = listFlujoEmp.get(x).getNoEmpresa();
					bodyRow.add(cols);					
					
					//Obtiene el saldo inicial de la empresa y lo inserta...
					obtieneSaldoInicial(x, iDias, parametros, colSaldoInicial, "I");					
					//Con esto agrega al grid la fila con informaci�n de los saldos iniciales
					bodyRow.add(colSaldoInicial);
				}
				
				if(!rubro.equals(listCtasEmp.get(y).getIdGrupo())) {
					if(totalDerecho != 0) {
						cols.put("totales", totalDerecho);
						bodyRow.add(cols);
						totalDerecho = 0;
					}					
					
					cols = new HashMap();
					cols.put("rubro", listCtasEmp.get(y).getIdGrupo());
					cols.put("descripcion", listCtasEmp.get(y).getDescGrupo());			
					rubro = listCtasEmp.get(y).getIdGrupo();					
				}				
				
				for(int z=0; z<iDias; z++) {
					if(funciones.ponerFechaDate(funciones.cambiarFecha(listCtasEmp.get(y).getFecValor(), true)).compareTo(funciones.modificarFecha("d", z, funciones.ponerFechaDate(sFecIni))) == 0) {
						cols.put("col" + z, listCtasEmp.get(y).getImporte());
						totalDerecho += listCtasEmp.get(y).getImporte();
						
						if(tipoMovto.equals("I")) {
							totalI += listCtasEmp.get(y).getImporte();
							granTotalI += listCtasEmp.get(y).getImporte();
						}else {
							totalE += listCtasEmp.get(y).getImporte();
							granTotalE += listCtasEmp.get(y).getImporte();
						}
					}
				}
			}
		}
		
		if(totalDerecho != 0)
			cols.put("totales", totalDerecho);
		
		if(!tieneDato) {
			cols = new HashMap();
			HashMap colSaldoInicial = new HashMap();
			
			cols.put("descripcion", listFlujoEmp.get(x).getNomEmpresa());
			noEmpresa = listFlujoEmp.get(x).getNoEmpresa();
			bodyRow.add(cols);			
			
			//Obtiene el saldo inicial de la empresa y lo inserta...
			obtieneSaldoInicial(x, iDias, parametros, colSaldoInicial, "I");			
			//Con esto agrega al grid la fila con informaci�n de los saldos iniciales
			bodyRow.add(colSaldoInicial);			
		}		
		
		//Obtengo importe total de saldo inicial...
		for(int i=0; i<iDias; i++) {
			fecha = funciones.ponerFechaSola(funciones.modificarFecha("d", i, funciones.ponerFechaDate(parametros.get(0).get("fecIni"))));
			listSaldoEmp = posicionBancariaDao.obtieneSaldodiario(bancos, ctas, chkSaldos, fecha, listFlujoEmp.get(x).getNoEmpresa(), parametros.get(0).get("divisa"));
			
			if(listSaldoEmp.size() > 0)
				saldoInicial = listSaldoEmp.get(0).getImporte();
		}		
		
		cols = new HashMap();		
		if(tipoMovto.equals("I")) {
			cols.put("descripcion", "TOTAL INGRESOS");
			cols.put("totales", totalI);
			cols.put("color", "#2E64FE");
		}else {
			cols.put("descripcion", "TOTAL EGRESOS");
			cols.put("totales", totalE);
			cols.put("color", "#DF0101");
			bodyRow.add(cols);
			cols = new HashMap();
			cols.put("descripcion", "SALDO TOTAL");
			cols.put("totales", saldoInicial + granTotalI - granTotalE);
			cols.put("color", "#04B4AE");
			granTotalI = 0;
			granTotalE = 0;
		}
		bodyRow.add(cols);
	}
	
	public List<HashMap> fichaValorCuenta(List<Map<String, String>> datosGrid,  List<Map<String, String>> parametros) {
		List<PosicionBancariaDto> listCtasEmpI = new ArrayList<PosicionBancariaDto>();
		List<PosicionBancariaDto> listCtasEmpE = new ArrayList<PosicionBancariaDto>();
		List<PosicionBancariaDto> listCtasPreliminar = new ArrayList<PosicionBancariaDto>();
		
		StringBuffer ctasEmp = new StringBuffer();
		StringBuffer ctasPre = new StringBuffer();
		StringBuffer ctasPreD = new StringBuffer();
		
		List<HashMap> bodyRow = new ArrayList<HashMap>();
		int iDias = 0;
		boolean banI;
		boolean banE;
		int ii = 0;
		
		try {
			obtieneEmpsBcosCtas(datosGrid, parametros);
			
			listCtasEmpI = posicionBancariaDao.obtieneFichaCuenta("I", bancos, ctas, 0, parametros);
			listCtasEmpE = posicionBancariaDao.obtieneFichaCuenta("E", bancos, ctas, 0, parametros);
			
			iDias = funciones.diasEntreFechas(funciones.ponerFechaDate(parametros.get(0).get("fecIni")), funciones.ponerFechaDate(parametros.get(0).get("fecFin"))) + 1;
			
			for(int x=0; x<listFlujoEmp.size(); x++) {
				banI = false;
				banE = false;
				
				for(ii=0; ii<listCtasEmpI.size(); ii++) {
					if(listFlujoEmp.get(x).getNoEmpresa() == listCtasEmpI.get(ii).getNoEmpresa()) {
						banI = true;
						break;
					}
				}
				if(!banI) {
					for(ii=0; ii<listCtasEmpE.size(); ii++) {
						if(listFlujoEmp.get(x).getNoEmpresa() == listCtasEmpE.get(ii).getNoEmpresa()) {
							banE = true;
							break;
						}
					}
				}
				if(banI || banE) {
					HashMap cols = new HashMap();
					//Obtiene el saldo inicial de la empresa
					//obtieneSaldoInicial(x, iDias, parametros, cols, "I");
					
					//Con esto agrega al grid la fila con informaci�n de los saldos iniciales
					//bodyRow.add(cols);
					
					//Llena el grid con los ingresos
					llenaGrid(listCtasEmpI, x, iDias, cols, parametros.get(0).get("fecIni"), bodyRow, "I", parametros);
					
					//Llena el grid con los Egresos
					llenaGrid(listCtasEmpE, x, iDias, cols, parametros.get(0).get("fecIni"), bodyRow, "E", parametros);
				}
			}
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaBusiness, M:fichaValorEmpresa");
		}
		return bodyRow;
	}
	
	public PosicionBancariaDao getPosicionBancariaDao() {
		return posicionBancariaDao;
	}

	public void setPosicionBancariaDao(PosicionBancariaDao posicionBancariaDao) {
		this.posicionBancariaDao = posicionBancariaDao;
	}
}