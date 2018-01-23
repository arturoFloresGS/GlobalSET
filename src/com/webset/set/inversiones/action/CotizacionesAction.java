package com.webset.set.inversiones.action;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.inversiones.dto.ContratoInstitucionDto;
import com.webset.set.inversiones.dto.CotizacionesDto;
import com.webset.set.inversiones.dto.LlenaComboValoresDto;
import com.webset.set.inversiones.middleware.service.CotizacionesService;
import com.webset.set.inversiones.middleware.service.InversionesComunService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.dto.FuncionesSql;
import com.webset.utils.tools.Utilerias;

/**
 * Action para la pantalla de Control de Inversiones
 * @autor COINSIDE
 */
public class CotizacionesAction {
	
	Bitacora bitacora = new Bitacora();
	Contexto contexto=new  Contexto();
	FuncionesSql funciones = new FuncionesSql();
	//List<Boolean> resultPerfil;// = new ArrayList<Boolean>();
	String sLetras = " ";
	int iCen = 0;
	int iDes = 0;
	int iUni = 0;
		
	@DirectMethod
	public List<LlenaComboValoresDto> obtenerInstitucion()
	{
		List<LlenaComboValoresDto>lista=new ArrayList<LlenaComboValoresDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				InversionesComunService inversionesComunService = (InversionesComunService) contexto.obtenerBean("inversionesComunBusinessImpl");
				lista = inversionesComunService.consultarInstitucion();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Inversiones, C:CotizacionesAction, M:obtenerInstitucion");			
		}
		return lista;
	}
	
	@DirectMethod
	public List<LlenaComboValoresDto> obtenerTipoValor()
	{
		List<LlenaComboValoresDto>lista=new ArrayList<LlenaComboValoresDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				InversionesComunService inversionesComunService = (InversionesComunService) contexto.obtenerBean("inversionesComunBusinessImpl");
				lista = inversionesComunService.consultarTipoValor();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Inversiones, C:CotizacionesAction, M:obtenerTipoValor");			
		}
		return lista;
	}
	
	@DirectMethod
	public List<LlenaComboValoresDto> obtenerContactosInst(int noInstitucion)
	{
		List<LlenaComboValoresDto>lista=new ArrayList<LlenaComboValoresDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CotizacionesService cotizacionesService = (CotizacionesService) contexto.obtenerBean("cotizacionesBusinessImpl");
				lista = cotizacionesService.consultarContactosInst(noInstitucion);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Inversiones, C:CotizacionesAction, M:obtenerContactosInst");			
		}
		return lista;
	}
	
	@DirectMethod
	public List<ContratoInstitucionDto> obtenerContratos(int noInstitucion)
	{
		List<ContratoInstitucionDto>lista=new ArrayList<ContratoInstitucionDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CotizacionesService cotizacionesService = (CotizacionesService) contexto.obtenerBean("cotizacionesBusinessImpl");
				lista = cotizacionesService.consultarContratos(noInstitucion);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Inversiones, C:CotizacionesAction, M:obtenerContratos");			
		}
		return lista;
	}
	
	@DirectMethod
	public String consultarDivisaTV(String idTipoValor)
	{
		String divisa = "";
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CotizacionesService cotizacionesService = (CotizacionesService) contexto.obtenerBean("cotizacionesBusinessImpl");
				divisa = cotizacionesService.consultarDivisaTV(idTipoValor);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Inversiones, C:CotizacionesAction, M:consultarDivisaTV");			
		}
		return divisa;
	}
	
	@DirectMethod
	public String registrarCotizaciones (String tasas, int idInstitucion, String tipoValor, String fechaDivisa, int idUsuario, int noEmpresa)
	{
		String salida = "";
		try{
			if(!(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50))){
				return null;
			}
			CotizacionesService cotizacionesService = (CotizacionesService) contexto.obtenerBean("cotizacionesBusinessImpl");
			
			List<Map<String, Object>> listaTasas = new ArrayList<Map<String,Object>>();
			Map<String, Object> mapaTasa;
			Gson gson = new Gson();
			//Map<String, Object> mapRetFondeo = new HashMap<String, Object>();
			
			List<Map<String, String>> objParams = gson.fromJson(tasas, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			
			for(int i = 0; i < objParams.size(); i++){
				mapaTasa = new HashMap<String, Object>();
				mapaTasa.put("Plazo", Integer.parseInt((objParams.get(i).get("plazo") ) ));
				mapaTasa.put("Tasa", (String)(objParams.get(i).get("tasa") ) );
				
				listaTasas.add(mapaTasa);
			}
			CotizacionesDto cotizacionesDto = new CotizacionesDto();
			
			String fecha = fechaDivisa.substring(0,10);
			String hora = fechaDivisa.substring(fechaDivisa.indexOf('-') + 1, fechaDivisa.indexOf('@'));
			String idDivisa = fechaDivisa.substring(fechaDivisa.indexOf('@') + 1, fechaDivisa.length());
			
			cotizacionesDto.setFecha(FuncionesSql.ponerFechaDate(fecha));
			cotizacionesDto.setHora(hora);
			cotizacionesDto.setIdDivisa(idDivisa);
			cotizacionesDto.setIdInstitucion(idInstitucion);
			cotizacionesDto.setIdUsuario(idUsuario);
			cotizacionesDto.setNoEmpresa(noEmpresa);
			cotizacionesDto.setTipoValor(tipoValor);
			cotizacionesDto.setTasas(listaTasas);
			
			salida = cotizacionesService.registrarCotizaciones(cotizacionesDto);

		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Inversiones, C:CotizacionesAction, M:consultarDivisaTV");			
		}
		return salida;
	}

}
