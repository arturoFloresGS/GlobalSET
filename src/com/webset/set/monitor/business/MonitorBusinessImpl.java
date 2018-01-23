package com.webset.set.monitor.business;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webset.set.monitor.dao.MonitorDao;
import com.webset.set.monitor.service.MonitorService;
import com.webset.set.utilerias.Bitacora;

public class MonitorBusinessImpl implements MonitorService {
	private Bitacora bitacora = new Bitacora();
	MonitorDao objMonitorDao;
	
	
	public String obtenerArbol(String divisa , String opt){
		StringBuffer resultado = new StringBuffer();
		List<Map<String,Object>> listaResultadoSql = new ArrayList<Map<String,Object>>();
		boolean bBisabuelo = false;
		String auxiliar="";
		
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		symbols.setGroupingSeparator(',');
		String pattern = "###,##0.00";
		DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
		
		try{
			
			resultado.append("[");
			if(opt.equals("CAJA")){
				listaResultadoSql = objMonitorDao.obtenerArbolCaja(divisa);
				bBisabuelo=true;
				auxiliar = importeTotal (listaResultadoSql ,true, "" , "" , "");
				resultado.append("{'text' : ' Total  : $ " +auxiliar + " "+ divisa+"' ,'id':1 ,'leaf':false , 'cls' :'folder', 'children': [");
			}
			
			if(opt.equals("CONCILIACIONES")){
				listaResultadoSql =objMonitorDao.obtenerArbolConciliaciones(divisa);
				bBisabuelo=true;
			}
			
			if( opt.equals("INVERSION")){
				listaResultadoSql =objMonitorDao.obtenerArbolInversion(divisa);
				auxiliar = importeTotal (listaResultadoSql ,true, "" , "" , "");
				resultado.append("{'text' : ' Inversion  : $ " +auxiliar  + " "+ divisa +"' ,'id':1 ,'leaf':false , 'cls' :'folder', 'children': [");
			}
			
			if(opt.equals("DEUDAS")){
				listaResultadoSql = objMonitorDao.obtenerArbolDeudas(divisa);
				auxiliar = importeTotal (listaResultadoSql ,true, "" , "" , "");
				resultado.append("{'text' : ' Deuda Global  : $" +auxiliar  + " "+ divisa +"' ,'id':1 ,'leaf':false , 'cls' :'folder', 'children': [");
			}
			
			if(opt.equals("COMPARATIVO")){
				auxiliar = objMonitorDao.calcularGlobal(divisa);
				resultado.append("{'text' : ' Diferencia Global  : $ " +auxiliar  + " "+ divisa +"' ,'id':1 ,'leaf':false , 'cls' :'folder', 'children': [");
				listaResultadoSql =objMonitorDao.obtenerArbolComparativo(divisa);
				bBisabuelo=true;
			}
			
			
			/***
			 * Formar arbol
			 */
			
			if(listaResultadoSql!=null && !listaResultadoSql.isEmpty() && listaResultadoSql.size()>0){
				String bisabuelo= "";
				String abuelo ="";
				String padre = "";
				String hijo="";
				
				String saldo="0";
				for (int i = 0; i < listaResultadoSql.size() ; i++) {
				   saldo=listaResultadoSql.get(i).get("total")+ "";
				   if(i==0){
					   if(bBisabuelo){
						   bisabuelo=listaResultadoSql.get(i).get("bisabuelo")+ "";
							auxiliar = importeTotal (listaResultadoSql ,false, bisabuelo , "" , "");
						   resultado.append("{'text' : '" +(listaResultadoSql.get(i).get("descBisabuelo") + "  : $ " +auxiliar +" " + divisa) +"' ,'id':"+ bisabuelo+ ",'leaf':false , 'cls' :'folder' , 'children': [");
					   }else
						   bisabuelo="";
					   
					   abuelo =listaResultadoSql.get(i).get("abuelo")+ "";
					   auxiliar = importeTotal (listaResultadoSql ,false, bisabuelo , abuelo , "");
					   resultado.append("{'text' : '" + (listaResultadoSql.get(i).get("descAbuelo") + "  : $ " +auxiliar +" " + divisa) +"' ,'id':"+ abuelo+ ",'leaf':false , 'cls' :'folder', 'children': [");
					   
					   padre = listaResultadoSql.get(i).get("padre")+ "";
					   auxiliar = importeTotal (listaResultadoSql ,false, bisabuelo , abuelo , padre);
					   resultado.append("{'text' : '" +(listaResultadoSql.get(i).get("descPadre") + "  : $ " +auxiliar +" " + divisa) +"' ,'id':"+ padre+ ",'leaf':false , 'cls' :'folder', 'children': ["); 
					   
					   hijo = listaResultadoSql.get(i).get("hijo")+ "";
					   try {
						   double x = Double.parseDouble(saldo);
						   auxiliar = decimalFormat.format(x);
						} catch (Exception e) {
							auxiliar = decimalFormat.format(0.00);
						}
					   resultado.append("{'text' : '" + (listaResultadoSql.get(i).get("descHijo")+ "  : $ " + auxiliar + " " + divisa) +"' ,'id':"+ hijo+ ",'leaf':true , 'cls' :'file'},"); 
				   }
				   
				   //Grupo Empresa.
				   if(listaResultadoSql.get(i).get("bisabuelo") == null || bisabuelo.equals(listaResultadoSql.get(i).get("bisabuelo")+ "")){
					   if(abuelo.equals(listaResultadoSql.get(i).get("abuelo")+ "")){
						   if(padre.equals(listaResultadoSql.get(i).get("padre")+ "")){
							   if(hijo.equals(listaResultadoSql.get(i).get("hijo")+ "")){   
							   }else{
								   hijo = listaResultadoSql.get(i).get("hijo")+ "";
								   resultado.append("{'text' : '" +(listaResultadoSql.get(i).get("descHijo")+ ":" + saldo+" " + divisa) +"' ,'id':"+ hijo+ ",'leaf':true , 'cls' :'file'},");   
							   }
						   }else{
							   padre= listaResultadoSql.get(i).get("padre")+ "";
							   auxiliar = importeTotal (listaResultadoSql ,false, bisabuelo , abuelo , padre);
							   resultado.append("]}, {'text' : '" + (listaResultadoSql.get(i).get("descPadre") + "  : $ " +auxiliar +" " + divisa)  +"' ,'id':"+ padre+ ",'leaf':false , 'cls' :'folder', 'children': ["); 
							  
							   hijo = listaResultadoSql.get(i).get("hijo")+ "";
							   try {
								   double x = Double.parseDouble(saldo);
								   auxiliar = decimalFormat.format(x);
								} catch (Exception e) {
									auxiliar = decimalFormat.format(0.00);
								}
							   resultado.append("{'text' : '" +(listaResultadoSql.get(i).get("descHijo") + "  :  $ " + saldo) +"' ,'id':"+ hijo+ ",'leaf':true , 'cls' :'file'},"); 
							   
						   }
					   }else{
						   abuelo =listaResultadoSql.get(i).get("abuelo")+ "";
						   auxiliar = importeTotal (listaResultadoSql ,false, bisabuelo , abuelo , "");
						   resultado.append("]}, ]}, {'text' : '" + (listaResultadoSql.get(i).get("descAbuelo") + "  : $" +auxiliar +" " + divisa) +"' ,'id':"+ abuelo+ ",'leaf':false , 'cls' :'folder', 'children': [");	 
						   
						   padre = listaResultadoSql.get(i).get("padre")+ "";
						   auxiliar = importeTotal (listaResultadoSql ,false, bisabuelo , abuelo , padre);
						   resultado.append("{'text' : '" + (listaResultadoSql.get(i).get("descPadre") + "  : $ " +auxiliar +" " + divisa) +"' ,'id':"+ padre+ ",'leaf':false , 'cls' :'folder', 'children': [");
						   
						   hijo = listaResultadoSql.get(i).get("hijo")+ "";
						   try {
							   double x = Double.parseDouble(saldo);
							   auxiliar = decimalFormat.format(x);
							} catch (Exception e) {
								auxiliar = decimalFormat.format(0.00);
							}
						   resultado.append("{'text' : '" +(listaResultadoSql.get(i).get("descHijo") + "  :  $ " + saldo) +"' ,'id':"+ hijo+ ",'leaf':true , 'cls' :'file'},"); 
						   
					   }
				   }else{
					  if (bBisabuelo) {
						  bisabuelo = listaResultadoSql.get(i).get("bisabuelo")+ "";
						  auxiliar = importeTotal (listaResultadoSql ,false, bisabuelo , "" , "");
						  resultado.append("]}, ]}, ]}, {'text' : '" + (listaResultadoSql.get(i).get("descBisabuelo") + "  : $ " +auxiliar +" " + divisa) +"' ,'id':"+ bisabuelo+ ",'leaf':false , 'cls' :'folder' , 'children': [");
						 
						  abuelo =listaResultadoSql.get(i).get("abuelo")+ "";
						  auxiliar = importeTotal (listaResultadoSql ,false, bisabuelo , abuelo , "");
						  resultado.append("{'text' : '" +(listaResultadoSql.get(i).get("descAbuelo") + "  : $ " +auxiliar +" " + divisa) +"' ,'id':"+ abuelo+ ",'leaf':false , 'cls' :'folder', 'children': [");
						  
						  padre = listaResultadoSql.get(i).get("padre")+ "";
						  auxiliar = importeTotal (listaResultadoSql ,false, bisabuelo , abuelo , padre);
						  resultado.append("{'text' : '" + (listaResultadoSql.get(i).get("descPadre") + "  : $ " +auxiliar +" " + divisa) +"' ,'id':"+ padre+ ",'leaf':false , 'cls' :'folder', 'children': ["); 
					  
						  hijo = listaResultadoSql.get(i).get("hijo")+ "";
						  try {
							   double x = Double.parseDouble(saldo);
							   auxiliar = decimalFormat.format(x);
							} catch (Exception e) {
								auxiliar = decimalFormat.format(0.00);
							}
						  resultado.append("{'text' : '" +(listaResultadoSql.get(i).get("descHijo")+ "  : $" + saldo) +"' ,'id':"+ hijo+ ",'leaf':true , 'cls' :'file'},"); 
					  }
				   }
				   
				}
				
				if(bBisabuelo)
					if(opt.equals("CAJA"))
						resultado.append("]}, ]}, ]},]} ]");
					else if (opt.equals("COMPARATIVO"))
						resultado.append("]}, ]}, ]} ,]} ]");
					else 
						resultado.append("]}, ]},]},]");
				else	
						resultado.append("]}, ]}, ]} ]");
			}else{
				if(opt.equals("CAJA") || opt.equals("INVERSION") || opt.equals("DEUDAS")||opt.equals("COMPARATIVO") ){
					resultado.append("]} ]");
				}
				
				if(opt.equals("CONCILIACIONES")){
					resultado.delete(0, resultado.length());
					resultado.append("{}");
				}
				
				
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Monitor, C:MonitorBusinessImpl, M:obtenerArbolCaja");
		}return resultado.toString();
	}
	
	
	/***************************************************************************
	 * 
	 */
	
	public String importeTotal (List<Map<String,Object>> lista ,boolean suma, String bisabuelo , String abuelo , String padre){
		String resultado="0.0";
		if(lista!=null && !lista.isEmpty() && lista.size()>0){
			
			DecimalFormatSymbols symbols = new DecimalFormatSymbols();
			symbols.setDecimalSeparator('.');
			symbols.setGroupingSeparator(',');
			String pattern = "###,##0.00";
			DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
			
			double sumaTotal= 0;
			
			if(suma){
				for (int i = 0; i < lista.size(); i++) {
					String total = "" + lista.get(i).get("total");
					double n=0;
					try {
						//total=decimalFormat.format());
						n= Double.parseDouble(total);
					} catch (Exception e) {
						n=0;
					}
					sumaTotal = sumaTotal +n;
				}
			}else{
				for (int i = 0; i < lista.size(); i++) {
					String total = "" + lista.get(i).get("total");
					double n=0;
					try {
						n= Double.parseDouble(total);
					} catch (Exception e) {
						n=0;
					}
					
					if(bisabuelo==null || bisabuelo.equals("")){
						if(abuelo.trim().equals(("" + lista.get(i).get("abuelo")).trim())){
							 if(padre.equals("")){
								 sumaTotal = sumaTotal +n;
							 }else{
								 if(padre.equals("" + lista.get(i).get("padre"))){
									 sumaTotal = sumaTotal +n;
								 }
							 }
						 }
					}else{
						if(bisabuelo.equals( "" + lista.get(i).get("bisabuelo"))){
							if(abuelo.equals("")){
								sumaTotal = sumaTotal +n;
							}else{
								 if(abuelo.equals("" + lista.get(i).get("abuelo"))){
									 if(padre.equals("")){
										 sumaTotal = sumaTotal +n;
									 }else{
										 if(padre.equals("" + lista.get(i).get("padre"))){
											 sumaTotal = sumaTotal +n;
										 }
									 }
								 }
							}	 
						}
					}
				}
			}
		resultado=decimalFormat.format(sumaTotal);
		}return resultado;
	}
	
	/************************************
	 *  GRID
	 */
	
	public List<Map<String, String>> consultaGridDiversos(String divisa){
		List<Map<String, String>> resultado= new ArrayList<Map<String, String>>();
		List<Map<String,Object>> listaObjeto = new ArrayList<Map<String, Object>>();
		try{	
			listaObjeto = objMonitorDao.consultaGridDiversos(divisa);
			for (int i = 0; i < listaObjeto.size(); i++) {
				Map<String,String> map = new HashMap<String,String>();
				map.put("descripcion", ""+listaObjeto.get(i).get("descripcion"));
				map.put("total", ""+listaObjeto.get(i).get("total"));
				resultado.add(map);
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Monitor, C:MonitorBusinessImpl, M:consultaGridDiversos");
		}return resultado;
	}
	
	/************************************
	 * Consulta de indicadores bancarios
	 */
	
	public String  consultaIndicadoresBancarios (){
		StringBuffer resultado=new StringBuffer();
		try{	
			List<Map<String,Object>> listaObjeto = objMonitorDao.consultaIndicadoresBancarios();
			for (int i = 0; i < listaObjeto.size(); i++) {
				String auxiliar= listaObjeto.get(i).get("id")+"";
				String tipo = listaObjeto.get(i).get("tipo")+"";
				if(tipo.equals("divisa") && !auxiliar.equals("MN"))
					resultado.append(listaObjeto.get(i).get("id") +"/MN = " +listaObjeto.get(i).get("valor") + "  ");
				if(tipo.equals("tasa") && !auxiliar.equals("TASA"))
					resultado.append(listaObjeto.get(i).get("id") +" = " +listaObjeto.get(i).get("valor") + "  ");
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Monitor, C:MonitorBusinessImpl, M:consultaIndicadoresBancarios");
		}
		System.out.println(resultado.toString());
		return resultado.toString();
	}
		
	/************************************************
	 * GRAFICAS
	 */
	
	public List<Map<String, String>> datosGrafica(String tipoGrafica, String divisa){
		List<Map<String, String>> resultado= new ArrayList<Map<String, String>>();
		List<Map<String,Object>> listaObjeto = new ArrayList<Map<String, Object>>();
		try{	
			switch (tipoGrafica) {
				case "CAJA":
						listaObjeto = objMonitorDao.graficaPoscionCaja(divisa);
					break;
								
				case "INVERSION":
					listaObjeto = objMonitorDao.graficarPoscionInversion(divisa);
								break;
								
				case "DEUDA":
					listaObjeto = objMonitorDao.graficarPoscionDeuda(divisa);
					break;
					
				case "CONCILIACION":
					listaObjeto = objMonitorDao.graficaPoscionConciliacion(divisa);
					break;
			}
			
			for (int i = 0; i < listaObjeto.size(); i++) {
				Map<String,String> map = new HashMap<String,String>();
				map.put("descripcion", ""+listaObjeto.get(i).get("descripcion"));
				map.put("total", ""+listaObjeto.get(i).get("total"));
				resultado.add(map);
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Monitor, C:MonitorBusinessImpl, M:graficarPoscionDeuda");
		}return resultado;
	}
	/**********************************************/
	
	public MonitorDao getObjMonitorDao() {
		return objMonitorDao;
	}

	public void setObjMonitorDao(MonitorDao objMonitorDao) {
		this.objMonitorDao = objMonitorDao;
	}
	
}
