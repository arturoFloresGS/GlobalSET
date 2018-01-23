package com.webset.set.inversiones.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.financiamiento.dto.Amortizaciones;
import com.webset.set.financiamiento.service.FinanciamientoModificacionCService;
import com.webset.set.inversiones.dto.PosicionInversionDto;
import com.webset.set.inversiones.middleware.service.PosicionInversionService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;

public class PosicionInversionAction {
	
	private static final int Map = 0;
	Bitacora bitacora = new Bitacora();
	Contexto contexto=new  Contexto();
	Funciones funciones = new Funciones();
	
	@DirectMethod 
	public List<LlenaComboGralDto> llenarCmbEmpresa(boolean bExistentes){
		System.out.println("angelEmpresa"+bExistentes);
		List<LlenaComboGralDto> listLista = new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listLista ;
		try{																										   
			PosicionInversionService posicionInversionService= (PosicionInversionService) contexto.obtenerBean("posicionInversionBusinessImpl");
			listLista  = posicionInversionService.consultarEmpresa(bExistentes);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: FinanciamientoModificacionC C: FinanciamientoModificacionCAction M: llenarCmbLineas");
		}
		return listLista ;
	}
	
	
	
	
	
	@DirectMethod 
	public List<LlenaComboGralDto> llenarCmbDivisa(int empresa){

		List<LlenaComboGralDto> listLista = new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listLista ;
		try{																										   
			PosicionInversionService posicionInversionService= (PosicionInversionService) contexto.obtenerBean("posicionInversionBusinessImpl");
			listLista  = posicionInversionService.consultarDivisa(empresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: FinanciamientoModificacionC C: FinanciamientoModificacionCAction M: llenarCmbLineas");
		}
		return listLista ;
	}
	
	

	@DirectMethod 
	public List<LlenaComboGralDto> llenarCmbBanco(int emp, String divisa){

		List<LlenaComboGralDto> listLista = new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listLista ;
		try{																										   
			PosicionInversionService posicionInversionService= (PosicionInversionService) contexto.obtenerBean("posicionInversionBusinessImpl");
			listLista  = posicionInversionService.consultarBanco(emp, divisa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: FinanciamientoModificacionC C: FinanciamientoModificacionCAction M: llenarCmbLineas");
		}
		return listLista ;
	}
	
	
	
	
	
	@DirectMethod 
	public List<LlenaComboGralDto> llenarCmbChequera(int emp, String divisa, int banco){
System.out.println("cheuqera");
		List<LlenaComboGralDto> listLista = new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listLista ;
		try{																										   
			PosicionInversionService posicionInversionService= (PosicionInversionService) contexto.obtenerBean("posicionInversionBusinessImpl");
			listLista  = posicionInversionService.consultarChequera(emp, divisa,banco);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: FinanciamientoModificacionC C: FinanciamientoModificacionCAction M: llenarCmbLineas");
		}
		return listLista ;
	}
	
	
	
	
	
	@DirectMethod 
	public String institucion(){
System.out.println("cheuqera");
		String  listLista = "";
		
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listLista ;
		try{																										   
			PosicionInversionService posicionInversionService= (PosicionInversionService) contexto.obtenerBean("posicionInversionBusinessImpl");
			listLista  = posicionInversionService.consultarInstitucion();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: FinanciamientoModificacionC C: FinanciamientoModificacionCAction M: llenarCmbLineas");
		}
		return listLista ;
	}
	
	
	
	
	@DirectMethod 
	public String fecha(String fecha){
System.out.println("fecha:.."+fecha);
		String  listLista = "";
		
		
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listLista ;
		try{																										   
			listLista  = funciones.cambiarFecha(fecha);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: FinanciamientoModificacionC C: FinanciamientoModificacionCAction M: llenarCmbLineas");
		}
		
		return listLista ;
	}
	
	
	
	
	
	@DirectMethod 
	public List<PosicionInversionDto> llenarGrid1(String fecha, int caja, String institucion, int empresa,String divisa, int banco, String chequera){
		List<PosicionInversionDto> listDis = new ArrayList<PosicionInversionDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listDis;
		try{																										   
			PosicionInversionService posicionInversionService= (PosicionInversionService) contexto.obtenerBean("posicionInversionBusinessImpl");
			listDis= posicionInversionService.llenarGrid1(fecha, caja, institucion, empresa,divisa, banco, chequera);
			
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: FinanciamientoModificacionC C: FinanciamientoModificacionCAction M: llenarGrid");
		}

		
		return listDis;
	}	
	

	
	
	@DirectMethod 
	public List<PosicionInversionDto> llenarGrid2(String fecha, int caja, String institucion, int empresa,String divisa, int banco, String chequera){
		List<PosicionInversionDto> listDis = new ArrayList<PosicionInversionDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listDis;
		try{																										   
			PosicionInversionService posicionInversionService= (PosicionInversionService) contexto.obtenerBean("posicionInversionBusinessImpl");
			listDis= posicionInversionService.llenarGrid2(fecha, caja, institucion, empresa,divisa, banco, chequera);
			
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: FinanciamientoModificacionC C: FinanciamientoModificacionCAction M: llenarGrid");
		}

		
		return listDis;
	}	
	
	
	
	
	@DirectMethod 
	public List<PosicionInversionDto> llenarGrid3(String fecha, int caja, String institucion, int empresa,String divisa, int banco, String chequera){
		List<PosicionInversionDto> listDis = new ArrayList<PosicionInversionDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listDis;
		try{																										   
			PosicionInversionService posicionInversionService= (PosicionInversionService) contexto.obtenerBean("posicionInversionBusinessImpl");
			listDis= posicionInversionService.llenarGrid3(fecha, caja, institucion, empresa,divisa, banco, chequera);
			
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: FinanciamientoModificacionC C: FinanciamientoModificacionCAction M: llenarGrid");
		}

		
		return listDis;
	}	
	
	
	
	
	
	
	
	@DirectMethod 
	public List<PosicionInversionDto> llenarGrid4(String fecha, int caja, String institucion, int empresa,String divisa, int banco, String chequera){
		List<PosicionInversionDto> listDis = new ArrayList<PosicionInversionDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listDis;
		try{																										   
			PosicionInversionService posicionInversionService= (PosicionInversionService) contexto.obtenerBean("posicionInversionBusinessImpl");
			listDis= posicionInversionService.llenarGrid4(fecha, caja, institucion, empresa,divisa, banco, chequera);
			
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: FinanciamientoModificacionC C: FinanciamientoModificacionCAction M: llenarGrid");
		}

		
		return listDis;
	}	
	
	
	
	
	
	@DirectMethod 
	public int saldoInicial(int banco, String chequera){
		int   listLista = 0;
		
		
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listLista ;
		try{																										   
		
			PosicionInversionService posicionInversionService= (PosicionInversionService) contexto.obtenerBean("posicionInversionBusinessImpl");
			listLista  =  posicionInversionService.consultarSaldoInicial(banco, chequera);
	
	
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: FinanciamientoModificacionC C: FinanciamientoModificacionCAction M: llenarCmbLineas");
		}
		
		return listLista ;
	}
	
	
	
	
	
	

}
