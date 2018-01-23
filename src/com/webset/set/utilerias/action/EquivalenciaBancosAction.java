package com.webset.set.utilerias.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.egresos.service.AplicacionDescuentosPropuestasService;
import com.webset.set.impresion.service.ChequesPorEntregarService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.dto.EquivalenciaBancosDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.service.EquivalenciaBancosService;
import com.webset.set.utilerias.service.MantenimientoLeyendasService;
import com.webset.set.utilerias.service.MapeoService;
import com.webset.set.utileriasmod.dto.MantenimientoLeyendasDto;
import com.webset.utils.tools.Utilerias;

public class EquivalenciaBancosAction {
	private Contexto contexto = new Contexto();
	private Bitacora bitacora = new Bitacora();
	private EquivalenciaBancosService objEquivalenciaBancosService;
		
	@DirectMethod
	public List<EquivalenciaBancosDto> llenaGridBancos(String bankl,String banka,String idBanco,String descBanco){
		List<EquivalenciaBancosDto> listaResultado = new ArrayList<EquivalenciaBancosDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 213))
			return listaResultado;
		try{
			objEquivalenciaBancosService = (EquivalenciaBancosService)contexto.obtenerBean("objEquivalenciaBancosBusinessImpl");
			listaResultado = objEquivalenciaBancosService.llenaGridBancos(bankl,banka,idBanco,descBanco);
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:EquivalenciaBancosAction, M: llenaGridBancos");
		} 
		return listaResultado;
	}
	
	@DirectMethod
	public List<EquivalenciaBancosDto>consultarBancos(String texto){
		List<EquivalenciaBancosDto> list = new ArrayList<EquivalenciaBancosDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 57))
			return list;
		
		try{
			objEquivalenciaBancosService = (EquivalenciaBancosService)contexto.obtenerBean("objEquivalenciaBancosBusinessImpl");
			list = objEquivalenciaBancosService.consultarBancos(texto);
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:EquivalenciaBancosAction, M:consultarBancos");
		}
		return list;
	}

	@DirectMethod
	public List<EquivalenciaBancosDto>llenarComboBanco(String campoUno, String campoDos, String tabla, String condicion, String orden, boolean regUnico){
		List<EquivalenciaBancosDto> list= new ArrayList<EquivalenciaBancosDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get()))
			return list;
		
		EquivalenciaBancosDto dto= new EquivalenciaBancosDto();

		try{
			objEquivalenciaBancosService = (EquivalenciaBancosService)contexto.obtenerBean("objEquivalenciaBancosBusinessImpl");
			dto.setCampoUno(campoUno!=null && !campoUno.equals("")?campoUno:"");
			dto.setCampoDos(campoDos!=null && !campoDos.equals("")?campoDos:"");
			dto.setTabla(tabla!=null && !tabla.equals("")?tabla:"");
			dto.setCondicion(condicion!=null && !condicion.equals("")?condicion:"");
			dto.setOrden(orden!=null && !orden.equals("")?orden:"");
			dto.setRegistroUnico(regUnico);
			list = objEquivalenciaBancosService.llenarComboBanco(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:EquivalenciaBancosAction, M:llenarComboBanco");	
		}
		return list;
	}
	
	@DirectMethod
	public String actualizaEquivaleBanco(String bankl, String idBancoGrid, String idBancoText){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		String mensaje = "";
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				objEquivalenciaBancosService = (EquivalenciaBancosService)contexto.obtenerBean("objEquivalenciaBancosBusinessImpl");
				mensaje = objEquivalenciaBancosService.
						actualizaEquivaleBanco(bankl, 
								idBancoGrid !=null?idBancoGrid:""
								, idBancoText);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:EquivalenciaBancosAction, M:aplicarDescuentoSimple");
		}
		
		return mensaje;
	}
	
	@DirectMethod
	public HSSFWorkbook reporteBancosExt(ServletContext context){
		HSSFWorkbook wb=null;
		try {
			
			objEquivalenciaBancosService = (EquivalenciaBancosService)contexto.obtenerBean("objEquivalenciaBancosBusinessImpl", context);
			wb=objEquivalenciaBancosService.reporteBancosExt();
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:EquivalenciaBancosAction, M:reporteBancosExt");
		}return wb;
	}
	
}
