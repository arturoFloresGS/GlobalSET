package com.webset.set.egresos.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.egresos.dto.SeguimientoCPDto;
import com.webset.set.egresos.service.SeguimientoCPService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.utils.tools.Utilerias;

public class SeguimientoCPAction {
	
	Contexto contexto = new Contexto();
	Bitacora bitacora = new Bitacora();
	SeguimientoCPService objSeguimientoCPService;
	
	@DirectMethod
	public String cadenaDeInformacion (String idEmpresa, String idBeneficiario,
			String noDocumento, String periodo){
		String cadenaDeInformacion = "No se ha podido obtener los datos";
		try {
			
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),204)) {
			objSeguimientoCPService = (SeguimientoCPService)contexto.obtenerBean("objSeguimientoCPBusinessImpl");
			cadenaDeInformacion = objSeguimientoCPService.cadenaDeInformacion(idEmpresa,idBeneficiario, noDocumento, periodo);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: SeguimientoCPAction, M: llenaGrid");
		}return cadenaDeInformacion;
	}
	

	@DirectMethod
	public List<SeguimientoCPDto> llenaGrid(String idEmpresa, String idBeneficiario, 
			 String noDocumento, String periodo){
		System.out.println("entra grid");
		List<SeguimientoCPDto> listaResultado = new ArrayList<SeguimientoCPDto>();
		try{	
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),204)) {
			objSeguimientoCPService = (SeguimientoCPService)contexto.obtenerBean("objSeguimientoCPBusinessImpl");
			listaResultado = objSeguimientoCPService.llenaGrid(idEmpresa,idBeneficiario, noDocumento, periodo);
			}
		} 
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: SeguimientoCPAction, M: llenaGrid");
		}return listaResultado;
	}
	
	
}
