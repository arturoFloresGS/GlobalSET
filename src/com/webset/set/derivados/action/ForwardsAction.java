
/**
 * @autor Eric Cesar Guzman Malvaez
 * @fecha 08/03/17
 */

package com.webset.set.derivados.action;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;

import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.derivados.dao.impl.ForwardsDaoImpl;
import com.webset.set.derivados.dto.DatosExcelFwsDto;
import com.webset.set.derivados.service.ForwardsService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.LlenarConsultaFws;
import com.webset.utils.tools.Utilerias;

public class ForwardsAction {

	Bitacora bitacora   = new Bitacora();
	Contexto contexto   = new  Contexto();
	Funciones funciones = new Funciones();
	private static Logger logger = Logger.getLogger(ForwardsDaoImpl.class);

	@DirectMethod
	public List<LlenaComboEmpresasDto> obtenerEmpresas (int iEmpresaRaiz){
//		System.out.println("FORWARDS Obtener Empresas _:" + iEmpresaRaiz);
		List<LlenaComboEmpresasDto> lista = new ArrayList<LlenaComboEmpresasDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				ForwardsService compraForwardsService = (ForwardsService)contexto.obtenerBean("forwardsBusinessImpl");
				lista = compraForwardsService.obtenerEmpresas(iEmpresaRaiz);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Derivados, C:ForwardsAction, M:obtenerEmpresas");			
		}
		return lista;

	}
	
	@DirectMethod
	public String consultaBanco(int id_banco){
		System.out.println("Entro al action a consultar banco: " + id_banco);
		//		System.out.println("Crear  insertaForward" + matrizDatos);
		//		System.out.println("Usuario: " + usuario);
		String mensaje = "";
		try {
		
			
				ForwardsService forwardsService = (ForwardsService)contexto.obtenerBean("forwardsBusinessImpl");
				mensaje = forwardsService.consultaBanco(id_banco);
			
		} catch (Exception e) {
			logger.error(e.toString());
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Derivados, C:ForwardsAction, M:consultaBanco");
		}
		System.out.println(mensaje);
		return mensaje;
	}
	
	

	@DirectMethod
	public List<DatosExcelFwsDto> validarDatosExcel(String datos){
		//		System.out.println("VALIDANDO STORE datos fws excel");
		List<DatosExcelFwsDto> datosExcelDto = new ArrayList<DatosExcelFwsDto>();

		try {
			if(Utilerias.haveSession(WebContextManager.get())  && Utilerias.tienePermiso(WebContextManager.get(), 182)){
				ForwardsService forwardsService = (ForwardsService)contexto.obtenerBean("forwardsBusinessImpl");
				datosExcelDto = forwardsService.validarDatosExcel(datos);
			}

		} catch (Exception e) {
			logger.error(e.toString());
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Derivados, C:ForwardsAction, M:validarDatosExcel");	
		}
		return datosExcelDto;
	}

	@DirectMethod
	public String insertaForward(String matrizDatos, int usuario){
		//		System.out.println("Crear  insertaForward" + matrizDatos);
		//		System.out.println("Usuario: " + usuario);
		String mensaje = "";
		try {
			if(Utilerias.haveSession(WebContextManager.get())  && Utilerias.tienePermiso(WebContextManager.get(), 182)){
				ForwardsService forwardsService = (ForwardsService)contexto.obtenerBean("forwardsBusinessImpl");
				mensaje = forwardsService.insertaForward(matrizDatos, usuario);
			}

		} catch (Exception e) {
			logger.error(e.toString());
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Derivados, C:ForwardsAction, M:insertaForward");
		}

		return mensaje;
	}
	
	@DirectMethod
	public String eliminaForward(String matrizDatos, int usuario){
				System.out.println("Crear  eliminaForward" + matrizDatos);
				System.out.println("Usuario: " + usuario);
		String mensaje = "";
		try {
			if(Utilerias.haveSession(WebContextManager.get())  && Utilerias.tienePermiso(WebContextManager.get(), 182)){
				ForwardsService forwardsService = (ForwardsService)contexto.obtenerBean("forwardsBusinessImpl");
				mensaje = forwardsService.eliminaForward(matrizDatos, usuario);
			}

		} catch (Exception e) {
			logger.error(e.toString());
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Derivados, C:ForwardsAction, M:eliminaForward");
		}

		return mensaje;
	}
	
	@DirectMethod
	public String modificaForward(String matrizDatos, int usuario){
		System.out.println("modificaForward" + matrizDatos);
		String mensaje = "";
		try {
			if(Utilerias.haveSession(WebContextManager.get())  && Utilerias.tienePermiso(WebContextManager.get(), 182)){
				ForwardsService forwardsService = (ForwardsService)contexto.obtenerBean("forwardsBusinessImpl");
				mensaje = forwardsService.modificaForward(matrizDatos, usuario);
			}

		} catch (Exception e) {
			logger.error(e.toString());
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Derivados, C:ForwardsAction, M:modificaForward");
		}

		return mensaje;
	}
	
	@DirectMethod
	public String swapForward(String matrizDatos, int usuario){
		System.out.println("cambiastatusSwapForward" + matrizDatos);
		String mensaje = "";
		try {
			if(Utilerias.haveSession(WebContextManager.get())  && Utilerias.tienePermiso(WebContextManager.get(), 182)){
				ForwardsService forwardsService = (ForwardsService)contexto.obtenerBean("forwardsBusinessImpl");
				mensaje = forwardsService.swapForward(matrizDatos, usuario);
			}

		} catch (Exception e) {
			logger.error(e.toString());
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Derivados, C:ForwardsAction, M:modificaForward");
		}

		return mensaje;
	}
	@DirectMethod
	public HSSFWorkbook obtenerExcel(String nombre) {
		//		System.out.println("nombre --------> " + nombre);
		FileInputStream file = null;
		HSSFWorkbook workbook = null;
		File arch = new File(nombre);
		try {
			file = new FileInputStream(arch);
			workbook = new HSSFWorkbook(file);
			file.close();
		} catch (FileNotFoundException e) {
			logger.error(e.toString());
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Derivados, C:ForwardsAction, M:obtenerExcel");
		} catch (IOException e) {
			logger.error(e.toString());
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Derivados, C:ForwardsAction, M:obtenerExcel");
		} catch (Exception e) {
			logger.error(e.toString());
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Derivados, C:ForwardsAction, M:obtenerExcel");
		}
		arch.delete();
		return workbook;
	}
	@DirectMethod
	public String exportaExcel(String datos, String opcion) {
		String resultado = "";

		if(!Utilerias.haveSession(WebContextManager.get()) || !(Utilerias.tienePermiso(WebContextManager.get(), 109)||Utilerias.tienePermiso(WebContextManager.get(), 110)))
			return resultado;
		try {
			ForwardsService forwardsService = (ForwardsService)contexto.obtenerBean("forwardsBusinessImpl");	
			resultado = forwardsService.exportaExcel(datos,opcion);
		}catch(Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Derivados, C:ForwardsAction, M:exportarExcel");
		}
		return resultado;
	} 









	@DirectMethod 
	public List<LlenaComboGralDto> llenarCmbDiv(int bExistentes, int idDiv){
//		System.out.println(bExistentes);
//		System.out.println(idDiv);
		List<LlenaComboGralDto> lista = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return lista;
		try{																										   
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				ForwardsService compraForwardsService = (ForwardsService)contexto.obtenerBean("forwardsBusinessImpl");
				lista = compraForwardsService.obtenerdivisas(bExistentes,idDiv);
			}	

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Derivados, C:ForwardsAction, M:obtenerEmpresas");	
		}
		return lista;
	}

	@DirectMethod 
	public List<LlenaComboGralDto> llenarCmbBancoAbono(int idEmp, String idDiv){
//		System.out.println("datos para banco abono\n"+idEmp+"\n"+idDiv+"\n"+"*********************************");
		List<LlenaComboGralDto> lista = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return lista;
		try{																										   
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				ForwardsService compraForwardsService = (ForwardsService)contexto.obtenerBean("forwardsBusinessImpl");
				lista = compraForwardsService.obtenerBanco(idEmp,  idDiv);
			}	

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Derivados, C:ForwardsAction, M:obtenerEmpresas");	
		}
		return lista;
	}	




	@DirectMethod
	public List<LlenaComboGralDto> obtenerChequeraAbono (int noEmpresa,String idDivisa,int idBanco) {

//		System.out.println(noEmpresa);
//		System.out.println(idDivisa);
//		System.out.println(idBanco);
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				ForwardsService compraForwardsService = (ForwardsService)contexto.obtenerBean("forwardsBusinessImpl");
				list = compraForwardsService.obtenerChequeraBanco(noEmpresa, idDivisa, idBanco);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Derivados, C:ForwardsAction, M:obtenerEmpresas");		
		}
		return list;
	}




	@DirectMethod 
	public List<LlenarConsultaFws> llenarGrid(boolean foco, int tipo, String fecA, String fecV,int noEm){
		List<LlenarConsultaFws>list = new ArrayList<LlenarConsultaFws>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return list;
		try{																										   
			ForwardsService compraForwardsService = (ForwardsService)contexto.obtenerBean("forwardsBusinessImpl");
			list = compraForwardsService.llenarGrid(foco,tipo,  fecA, fecV,noEm );

				
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Derivados, C:ForwardsAction, M:obtenerEmpresas");		
		}
		return list;
	}
	
	

	


	public HSSFWorkbook reporteFwd(String fwd ,ServletContext context){
//		System.out.println("/*************/");
//		System.out.println(fwd);
//		System.out.println("/*************/");

		HSSFWorkbook wb=null;
		try {

			ForwardsService compraForwardsService = (ForwardsService)contexto.obtenerBean("forwardsBusinessImpl");
			wb = compraForwardsService.reportFwd(fwd );

		} catch (Exception e) {


			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Derivados, C:ForwardsAction, M:obtenerEmpresas");	
		}
		return wb;
	}




	public HSSFWorkbook reportePersonas2(String tipoPersona,String foco,String tipo,String fecA, String fecV,ServletContext context){
		HSSFWorkbook wb=null;
//		System.out.println("------------y--------------------------");
//		System.out.println(tipoPersona);
//		System.out.println(foco);
//		System.out.println(tipo);
//		System.out.println(fecA);
//		System.out.println(fecV);

//		System.out.println("-----------y----------------------------");

		try {	
			ForwardsService forwardsService = (ForwardsService)contexto.obtenerBean("forwardsBusinessImpl",context);
			wb = forwardsService.reportePersonas2(tipoPersona,foco,tipo, fecA, fecV);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasAction, M: reportePersonas");
		}return wb;
	}


	@DirectMethod
	public String agregarFwds(int noEmpresa, String idDivVenta, int bancoCargo, String cheCargo, String idDivCompra,
			int bancoAbono, String cheAbono, int formaPago,
			float importePago, float importeFwd, float tipoCambio, String fecAlta, String fecVencimiento,
			int noInstitucion, String nomContacto, int idBancoBenef, String idCheBenef, String rubroCarg,
			String subRubroCarg, String rubroAbono, String subRubroAbono, String estatusMov, String estatusImporte,
			String firmante1, String firmante2, String noDocto, float spot, float pntsFwd, String desc_institucion ,int usuario) {
		String sMsgUsuario = "";
//		System.out.println(desc_institucion+usuario);
		if (!Utilerias.haveSession(WebContextManager.get()))
			return sMsgUsuario;

		try {
			if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				ForwardsService compraForwardsService = (ForwardsService) contexto.obtenerBean("forwardsBusinessImpl");
				sMsgUsuario = compraForwardsService.agregarFwd(noEmpresa, idDivVenta, bancoCargo, cheCargo, idDivCompra,
						bancoAbono, cheAbono, formaPago, importePago, importeFwd, tipoCambio, fecAlta, fecVencimiento,
						noInstitucion, nomContacto, idBancoBenef, idCheBenef, rubroCarg, subRubroCarg, rubroAbono,
						subRubroAbono, estatusMov, estatusImporte, firmante1, firmante2, noDocto, spot, pntsFwd,desc_institucion,usuario);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Derivados, C:ForwardsAction, M:obtenerEmpresas");
		}
		return sMsgUsuario;
	}




}

