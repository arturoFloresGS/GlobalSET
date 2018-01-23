//@autor Edgar Estrada
package com.webset.set.seguridad.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.seguridad.business.SegPolizasBusiness;
import com.webset.set.seguridad.dto.ComboUsuario;
import com.webset.set.seguridad.dto.PolizaDto;
import com.webset.set.seguridad.dto.UsuarioPolizaDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.utils.tools.Utilerias;

public class SegPolizasAction {
	private Contexto contexto= new Contexto();
	private Bitacora bitacora=new Bitacora();
	
	@DirectMethod
	public List<PolizaDto> obtenerPolizas(int intIdUsuario, boolean bExiste){
		List<PolizaDto> polizas= new ArrayList<PolizaDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(), 181)) {
			SegPolizasBusiness segPolizasBusiness = (SegPolizasBusiness) contexto.obtenerBean("segPolizasBusinnes");
			polizas= segPolizasBusiness.obtenerPolizas(intIdUsuario, bExiste);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegPolizasAction, M:obtenerPolizas");
		}
		
		return polizas;
	}
	
	@DirectMethod
	public List<ComboUsuario> obtenerUsuarios() {
		List<ComboUsuario> usuarios = new ArrayList<ComboUsuario>();
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(), 181)) {
			SegPolizasBusiness segPolizasBusiness = (SegPolizasBusiness)contexto.obtenerBean("segPolizasBusinnes");
			usuarios = segPolizasBusiness.obtenerUsuarios();
			}
		}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Seguridad, C:SegPolizasAction, M:obtenerUsuarios");
		}
		return usuarios;
	}
	
	@DirectMethod
	public boolean asignarPoliza(String claves)
	{
		try {
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(), 181)) {
			int ind=0, noUsuario=0, idPoliza=0, res=0;
			boolean todos=false;
			
		SegPolizasBusiness segPolizasBusiness = (SegPolizasBusiness) contexto.obtenerBean("segPolizasBusinnes");	
		ind=claves.indexOf(",");
		if(ind>0){
			noUsuario=Integer.parseInt(claves.substring(0,ind));
			idPoliza=Integer.parseInt(claves.substring(ind+1));
			todos=false;
			}
		else{
			noUsuario=Integer.parseInt(claves.substring(0));
			todos=true;
		}
		
		
		res=segPolizasBusiness.asignarPoliza(noUsuario, idPoliza, todos);
		return res>0;
			}else{
				return false;
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegPolizaAction, M:asignarPoliza");
			return false;
		}
	}
	
	@DirectMethod
	public boolean eliminarPolizas(String claves)  
	{
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 181))
			return false;
		int ind=0, noUsuario=0, idPoliza=0, res=0;
		boolean todos=false;
		
		try{
			SegPolizasBusiness segPolizasBusiness = (SegPolizasBusiness) contexto.obtenerBean("segPolizasBusinnes");
			UsuarioPolizaDto dtoPolizaDto= new UsuarioPolizaDto();
		ind=claves.indexOf(",");
		if(ind>0){
			noUsuario=Integer.parseInt(claves.substring(0,ind));
			idPoliza=Integer.parseInt(claves.substring(ind+1));
			todos=false;
			}
		else{
			noUsuario=Integer.parseInt(claves.substring(0));
			todos=true;
		}
	
		dtoPolizaDto.setNoUsuario(noUsuario);
		//dtoUsuarioEmpresa.setNoUsuario(noUsuario);
		dtoPolizaDto.setIdPoliza(idPoliza);
		//dtoUsuarioEmpresa.setNoEmpresa(noEmpresa);
		
		res=segPolizasBusiness.eliminar(dtoPolizaDto,todos);
		}catch(Exception e){
			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seguridad, C:SegPolizaAction, M:eliminarPolizas");
		}
		if(res>0)
			return true;
		else
			return false;
	}
}
