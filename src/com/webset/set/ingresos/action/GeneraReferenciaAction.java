package com.webset.set.ingresos.action;

//UTIL
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

import com.softwarementors.extjs.djn.config.annotations.DirectFormPostMethod;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.ingresos.business.CatBancoBusiness;
import com.webset.set.ingresos.dto.CatArmaIngresoDto;
import com.webset.set.ingresos.dto.CatBancoDto;
import com.webset.set.ingresos.dto.CatCtaBancoDto;
import com.webset.set.ingresos.dto.CatGrupoRubroDto;
import com.webset.set.seguridad.dto.EmpresaDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.utils.tools.Utilerias;

public class GeneraReferenciaAction {
	private Contexto contexto = new Contexto();
	private Bitacora bitacora = new Bitacora();
	private CatBancoBusiness catBancoBusiness;
	private static Logger logger = Logger.getLogger(GeneraReferenciaAction.class);
	private static class SubmitResult {
		public boolean success = true;
		public Map<String, String> errors;
		@SuppressWarnings("unused")
		public Map<String, String> debug_formPacket;
	}
	
	private static class ComboBanco {
		public String descBanco;
		public int idBanco;

		private ComboBanco(int idBanco, String descBanco) {
			this.descBanco = descBanco;
			this.idBanco = idBanco;
		}
	}
	
	private static class ComboChequera{
		public String idChequera;
		
		private ComboChequera(String idChequera){
			this.idChequera = idChequera;
		}
	}
	
	private static class ComboEmpresa{
		public int noEmpresa;
		public String nomEmpresa;
		
		private ComboEmpresa(int noEmpresa, String nomEmpresa){
			this.noEmpresa = noEmpresa;
			this.nomEmpresa = nomEmpresa;
		}
		
	}
	
	/**
	 * metodo para llenar el combo Banco
	 * */
	@DirectMethod
	public List<ComboBanco> obtenerBanco(int emp) {
		List<CatBancoDto> bancos = new ArrayList<CatBancoDto>();
		List<ComboBanco> comboBanco = new ArrayList<ComboBanco>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return comboBanco;
		try {
			catBancoBusiness = (CatBancoBusiness) contexto.obtenerBean("catBancoBusiness");
			bancos = catBancoBusiness.consultar(emp);
			ListIterator<CatBancoDto> itr = bancos.listIterator();

			while (itr.hasNext()) {
				CatBancoDto bancoTmp = itr.next();
				comboBanco.add(new ComboBanco(bancoTmp.getIdBanco(), bancoTmp.getDescBanco()));
			}
		} 
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Ingresos, C:GeneraReferenciaAction, M:obtenerBanco");
			}
		return comboBanco;
	}
	
	/**
	 * metodo para llenar el combo Banco FID
	 * */
	@DirectMethod
	public List<ComboBanco> obtenerBancoFID(int emp) {
		List<CatBancoDto> bancos = new ArrayList<CatBancoDto>();
		List<ComboBanco> comboBanco = new ArrayList<ComboBanco>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return comboBanco;
		try {
			catBancoBusiness = (CatBancoBusiness) contexto.obtenerBean("catBancoBusiness");
			bancos = catBancoBusiness.consultarFID(emp);
			ListIterator<CatBancoDto> itr = bancos.listIterator();
			while (itr.hasNext()) {
				CatBancoDto bancoTmp = itr.next();
				comboBanco.add(new ComboBanco(bancoTmp.getIdBanco(), bancoTmp.getDescBanco()));
			}
		} 
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Ingresos, C:GeneraReferenciaAction, M:obtenerBancoFID");
			}
		return comboBanco;
	}
	

	/**
	 * metodo para llenar el combo Chequera
	 * */
	@DirectMethod
	public List<ComboChequera> obtenerChequera(int ban, int emp) {
		List<CatCtaBancoDto> bancos = new ArrayList<CatCtaBancoDto>();
		List<ComboChequera> comboChequera = new ArrayList<ComboChequera>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return comboChequera;
		try {
			catBancoBusiness = (CatBancoBusiness) contexto.obtenerBean("catBancoBusiness");
			bancos = catBancoBusiness.consultar(ban, emp);
			ListIterator<CatCtaBancoDto> itr = bancos.listIterator();
			while (itr.hasNext()) {
				CatCtaBancoDto chequeTmp = itr.next();
				comboChequera.add(new ComboChequera(chequeTmp.getIdChequera()));
			}
		} 
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Ingresos, C:GeneraReferenciaAction, M:obtenerChequera");
			}
		return comboChequera;
	}
	
	/**
	 * metodo para llenar el combo Chequera
	 * */
	@DirectMethod
	public List<ComboChequera> obtenerChequeraFID(int ban, int emp) {
		List<ComboChequera> comboChequera = new ArrayList<ComboChequera>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return comboChequera;
		List<CatCtaBancoDto> bancos = new ArrayList<CatCtaBancoDto>();
		try {
			catBancoBusiness = (CatBancoBusiness) contexto.obtenerBean("catBancoBusiness");
			bancos = catBancoBusiness.consultarFID(ban, emp);
			ListIterator<CatCtaBancoDto> itr = bancos.listIterator();
			while (itr.hasNext()) {
				CatCtaBancoDto chequeTmp = itr.next();
				comboChequera.add(new ComboChequera(chequeTmp.getIdChequera()));
			}
		} 
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Ingresos, C:GeneraReferenciaAction, M:obtenerChequeraFID");
			}
		return comboChequera;
	}
	
	/**
	 * metodo para llenar el combo Empresa
	 * */
	@DirectMethod
	public List<ComboEmpresa> obtenerEmpresa() {
		List<EmpresaDto> empresas = new ArrayList<EmpresaDto>();
		EmpresaDto empresa = new EmpresaDto();
		List<ComboEmpresa> comboEmpresa = new ArrayList<ComboEmpresa>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return comboEmpresa;
		try {
			catBancoBusiness = (CatBancoBusiness) contexto.obtenerBean("catBancoBusiness");
			empresas = catBancoBusiness.consultar(empresa);
			ListIterator<EmpresaDto> itr = empresas.listIterator();
			while (itr.hasNext()) {
				EmpresaDto empresaTmp = itr.next();
				comboEmpresa.add(new ComboEmpresa(empresaTmp.getNoEmpresa(), empresaTmp.getNomEmpresa()));
			}
		} 
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Ingresos, C:GeneraReferenciaAction, M:obtenerEmpresa");
			}
		return comboEmpresa;
	}
	
	/**
	 * metodo para llenar el combo Empresa FID
	 * */
	@DirectMethod
	public List<ComboEmpresa> obtenerEmpresaFID() {
		List<ComboEmpresa> comboEmpresa = new ArrayList<ComboEmpresa>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return comboEmpresa;
		List<EmpresaDto> empresas = new ArrayList<EmpresaDto>();
		EmpresaDto empresa = new EmpresaDto();
		ListIterator<EmpresaDto> itr = null;
		try {
			catBancoBusiness = (CatBancoBusiness) contexto.obtenerBean("catBancoBusiness");
			empresas = catBancoBusiness.consultarFID(empresa);
			itr = empresas.listIterator();
			while (itr.hasNext()) {
				EmpresaDto empresaTmp = itr.next();
				comboEmpresa.add(new ComboEmpresa(empresaTmp.getNoEmpresa(), empresaTmp.getNomEmpresa()));
			}
		} 
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Ingresos, C:GeneraReferenciaAction, M:obtenerEmpresa");
			}
		return comboEmpresa;
	}
	
	
	/**
	 * metodo para llenar el Grid
	 * */
	@DirectMethod
	public List<CatArmaIngresoDto> llenarGrid (int ban, int emp, String cheque){
		List<CatArmaIngresoDto> ingreso = new ArrayList<CatArmaIngresoDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return ingreso;
		try{
			catBancoBusiness = (CatBancoBusiness)contexto.obtenerBean("catBancoBusiness");
			ingreso = catBancoBusiness.llenaGrid(ban, emp, cheque);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Ingresos, C:GeneraReferenciaAction, M:llenarGrid");
		}
		return ingreso;
	}
	
	/**
	 * metodo para llenar el el combo TipoIngreso
	 * */
	@DirectMethod
	public List<CatArmaIngresoDto> obtenerTipoIngreso (int emp, int ban, String cheque){
		List<CatArmaIngresoDto> ingreso = new ArrayList<CatArmaIngresoDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return ingreso;
		try{
			catBancoBusiness = (CatBancoBusiness)contexto.obtenerBean("catBancoBusiness");
			ingreso = catBancoBusiness.llenarTipoIngreso(emp, ban, cheque);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Ingresos, C:GeneraReferenciaAction, M:obtenerTipoIngreso");
		}
		return ingreso;
	}
	
	/**
	 * metodo para llenar el el combo Grupo
	 * */
	@DirectMethod
	public List<CatGrupoRubroDto> obtenerGrupo (){
		List<CatGrupoRubroDto> grupo = new ArrayList<CatGrupoRubroDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return grupo;
		try{
			catBancoBusiness = (CatBancoBusiness)contexto.obtenerBean("catBancoBusiness");
			grupo = catBancoBusiness.llenarGrupo();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Ingresos, C:GeneraReferenciaAction, M:obtenerGrupo");
		}
		return grupo;
	}
	
	
	/**
	 * metodo para llenar el el combo Rubro
	 * */
	@DirectMethod
	public List<CatGrupoRubroDto> obtenerRubro (int grupo){
		List<CatGrupoRubroDto> rubro = new ArrayList<CatGrupoRubroDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return rubro;
		try{
			catBancoBusiness = (CatBancoBusiness)contexto.obtenerBean("catBancoBusiness");
			rubro = catBancoBusiness.llenarRubro(grupo);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Ingresos, C:GeneraReferenciaAction, M:obtenerRubro");
		}
		return rubro;
	}
	
	
	/**
	 * metodo para insertar valores para armar la referencia
	 * */
	@DirectFormPostMethod
	public SubmitResult insertarModificarIngreso(Map<String, String> formParameters,	Map<String, FileItem> fileFields){
		assert formParameters != null;
		assert fileFields != null;
		SubmitResult result = new SubmitResult();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return result;
		try {
			catBancoBusiness = (CatBancoBusiness) contexto.obtenerBean("catBancoBusiness");
			CatArmaIngresoDto ingreso = new CatArmaIngresoDto();
			String pf = formParameters.get("prefijo");
			boolean pbCambiaCtas = formParameters.get("bolCambiaCtas").equals("true");
			boolean bandera = formParameters.get("banderaF").equals("true");
			ingreso.setOrdenEmpresa(formParameters.get(pf+"txtoEmpresa") != null ? Integer.parseInt(formParameters.get(pf+"txtoEmpresa")) : 0);
			ingreso.setDestinoEmpresa(formParameters.get("empresaD") != null ? formParameters.get("empresaD") : "");
			ingreso.setNoEmpresa(formParameters.get("empresa") != null ? Integer.parseInt(formParameters.get("empresa")) : 0);
			ingreso.setIdBanco(formParameters.get("banco") != null ? Integer.parseInt(formParameters.get("banco")) : 0);
			ingreso.setIdChequera(formParameters.get("chequera") != null ? formParameters.get("chequera") : "");
			ingreso.setIdCorresponde(formParameters.get("corresponde") != null  ? formParameters.get("corresponde") : "");
			ingreso.setLongReferencia(formParameters.get("sumLong") != null ? Integer.parseInt(formParameters.get("sumLong")) : 0);
			ingreso.setOrdenCliente(formParameters.get(pf+"txtoCliente") != null ? Integer.parseInt(formParameters.get(pf+"txtoCliente")) : 0);
			ingreso.setOrdenCodigo(formParameters.get(pf+"txtoCodigo") != null ? Integer.parseInt(formParameters.get(pf+"txtoCodigo")) : 0);
			ingreso.setOrdenSubcodigo(formParameters.get(pf+"txtoSubcodigo") != null ? Integer.parseInt(formParameters.get(pf+"txtoSubcodigo")) : 0);
			ingreso.setOrdenDivision(formParameters.get(pf+"txtoDivision") != null ? Integer.parseInt(formParameters.get(pf+"txtoDivision")) : 0);
			ingreso.setOrdenConst1(formParameters.get(pf+"txtoCons1") != null ? Integer.parseInt(formParameters.get(pf+"txtoCons1")) : 0);
			ingreso.setOrdenConst2(formParameters.get(pf+"txtoCons2") != null ? Integer.parseInt(formParameters.get(pf+"txtoCons2")) : 0);
			ingreso.setOrdenConst3(formParameters.get(pf+"txtoCons3") != null ? Integer.parseInt(formParameters.get(pf+"txtoCons3")) : 0);
			ingreso.setLongEmpresa(formParameters.get(pf+"txtlEmpresa") != null ? Integer.parseInt(formParameters.get(pf+"txtlEmpresa")) : 0);
			ingreso.setLongCliente(formParameters.get(pf+"txtlCliente") != null ? Integer.parseInt(formParameters.get(pf+"txtlCliente")) : 0);
			ingreso.setLongCodigo(formParameters.get(pf+"txtlCodigo") != null ? Integer.parseInt(formParameters.get(pf+"txtlCodigo")) : 0);
			ingreso.setLongSubcodigo(formParameters.get(pf+"txtlSubcodigo") != null ?Integer.parseInt(formParameters.get(pf+"txtlSubcodigo")) : 0);
			ingreso.setLongDivision(formParameters.get(pf+"txtlDivision") != null ? Integer.parseInt(formParameters.get(pf+"txtlDivision")) : 0);
			ingreso.setConst1(formParameters.get(pf+"txtlCons1") != null ? formParameters.get(pf+"txtlCons1") : "");
			ingreso.setConst2(formParameters.get(pf+"txtlCons2") != null ? formParameters.get(pf+"txtlCons2") : "");
			ingreso.setConst3(formParameters.get(pf+"txtlCons3") != null ? formParameters.get(pf+"txtlCons3") : "");
			ingreso.setLongVar1(formParameters.get(pf+"txtlVar1") != null ? Integer.parseInt(formParameters.get(pf+"txtlVar1")) : 0);
			ingreso.setLongVar2(formParameters.get(pf+"txtlVar2") != null ? Integer.parseInt(formParameters.get(pf+"txtlVar2")) : 0);
			ingreso.setLongVar3(formParameters.get(pf+"txtlVar3") != null ? Integer.parseInt(formParameters.get(pf+"txtlVar3")) : 0);
			ingreso.setBaseCalculo("b10");
			ingreso.setIdRubro(formParameters.get("rubro") != null ? Integer.parseInt(formParameters.get("rubro")) : 0);
			ingreso.setIdChequeraDestino(formParameters.get("chequeD") != null ? formParameters.get("chequeD") : "");
			ingreso.setDestinoConst1(formParameters.get("const1") != null ? formParameters.get("const1") : "");
			ingreso.setDestinoConst2(formParameters.get("const2") != null ? formParameters.get("const2") : "");
			ingreso.setDestinoConst3(formParameters.get("const3") != null ? formParameters.get("const3") : "");
			ingreso.setOrdenVar1(formParameters.get(pf+"txtoVar1") != null ? Integer.parseInt(formParameters.get(pf+"txtoVar1")) : 0);
			ingreso.setOrdenVar2(formParameters.get(pf+"txtoVar2") != null ? Integer.parseInt(formParameters.get(pf+"txtoVar2")) : 0);
			ingreso.setOrdenVar3(formParameters.get(pf+"txtoVar3") != null ? Integer.parseInt(formParameters.get(pf+"txtoVar3")) : 0);
			ingreso.setDestinoVar1(formParameters.get("var1") != null ? formParameters.get("var1") : "");
			ingreso.setDestinoVar2(formParameters.get("var2") != null ? formParameters.get("var2") : "");
			ingreso.setDestinoVar3(formParameters.get("var3") != null ? formParameters.get("var3") : "");
			ingreso.setBPredeterminada(formParameters.get("predet") != null ? formParameters.get("predet") : "");
			
				if(!bandera)
				{
				result.success = catBancoBusiness.agregar(ingreso, pbCambiaCtas) > 0;
				logger.info("insert " + result.success);
				}
				else{
					result.success = catBancoBusiness.modificar(ingreso, pbCambiaCtas)>0;
					logger.info("update " + result.success);
				}
	
				if (!result.success) {
					result.errors = new HashMap<String, String>();
					result.errors.put("Error",	"No se pudieron insertar los datos");
					logger.info("No se pudieron insertar los datos");
				}
	
				result.debug_formPacket = formParameters;
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Ingresos, C:GeneraReferenciaAction, M:insertarModificarIngreso");
		}

		return result;
	}
	
	
	/**
	 * metodo para eliminar un usuario
	 * */
	@DirectMethod
	public boolean eliminarArmaIngreso(int empresa, int banco, String cheque, String corresp) {
		boolean result = false;
		if(!Utilerias.haveSession(WebContextManager.get()))
			return result;
		int res = 0;
		CatArmaIngresoDto ingreso = new CatArmaIngresoDto();
		CatBancoBusiness catBancoBusiness = (CatBancoBusiness) contexto.obtenerBean("catBancoBusiness");
		try {
			ingreso.setNoEmpresa(empresa);
			ingreso.setIdBanco(banco);
			ingreso.setIdChequera(cheque);
			ingreso.setIdCorresponde(corresp);
			res = (catBancoBusiness.eliminar(ingreso));
			if(res > 0)
				result = true;
			else
				result = false;

		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seguridad, C:SegUsuarioAction, M:eliminarArmaIngreso");
					}

		return result;
	}
	
}
