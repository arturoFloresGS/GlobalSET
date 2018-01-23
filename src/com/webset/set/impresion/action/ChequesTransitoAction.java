package com.webset.set.impresion.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.bancaelectronica.service.MovimientosBEService;
import com.webset.set.impresion.dto.ChequesTransitoDto;
import com.webset.set.impresion.service.ChequesTransitoService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;
/**
* YEC
* 06 de enero del 2016
*/
public class ChequesTransitoAction {
	Contexto contexto=new Contexto();
	Bitacora bitacora=new Bitacora();
	Funciones funciones = new Funciones();
	ChequesTransitoService objChequesTransitoService;
	MovimientosBEService objMovimientosBEService;
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboMotivos(){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return list;
		try{
			objChequesTransitoService  = (ChequesTransitoService)contexto.obtenerBean("objChequesTransitoBusiness");
			list=objChequesTransitoService .llenarComboMotivos();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Impresion, C:ChequesTransitoAction, M:llenarComboMotivos");
		}return list;
	}
	
	@DirectMethod
	public List<ChequesTransitoDto> llenaGrid(String noEmpresa,String noBanco,String idChequera,String noCheque,String fechaIni,String fechaFin,String dias){
		List<ChequesTransitoDto> listaResultado = new ArrayList<ChequesTransitoDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 197))
			return listaResultado;
		try{
			objChequesTransitoService = (ChequesTransitoService)contexto.obtenerBean("objChequesTransitoBusiness");
			noEmpresa=noEmpresa!=null && !noEmpresa.equals("")? noEmpresa:"";
			noBanco=noBanco!=null && !noBanco.equals("")? noBanco:"";
			idChequera=idChequera!=null && !idChequera.equals("")? idChequera:"";
			noCheque=noCheque!=null && !noCheque.equals("")? noCheque:"";
			fechaIni=fechaIni!=null && !fechaIni.equals("")? fechaIni:"";
			fechaFin=fechaFin!=null && !fechaFin.equals("")? fechaFin:"";
			dias=dias!=null && !dias.equals("")? dias:"";
			listaResultado = objChequesTransitoService.llenaGrid(noEmpresa,noBanco, idChequera,noCheque, fechaIni, fechaFin, dias);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:impresion, C:ChequesTransitoAction, M: llenaGrid");
		} return listaResultado;
	}
	
	@DirectMethod
	public String cancelarCheque(String sJson){
		String resultado="";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 197))
			return resultado;
		Gson gson = new Gson();
		ChequesTransitoDto dto=new ChequesTransitoDto();
		List<Map<String, String>> objDatos = gson.fromJson(sJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		int t;	
		try{
			objChequesTransitoService = (ChequesTransitoService)contexto.obtenerBean("objChequesTransitoBusiness");
			String usuario= GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+"";
			if(usuario.equals(""))
				return "Error al obtener usuario.";
			for(t=0; t<objDatos.size(); t++) {
				dto.setNoFolioDetalle(objDatos.get(t).get("noFolioDetalle")!=null ? objDatos.get(t).get("noFolioDetalle"):"0" );
				dto.setTipoOperacion(objDatos.get(t).get("tipoOperacion")!=null ? objDatos.get(t).get("tipoOperacion"):"0" );
				dto.setIdEstatus(objDatos.get(t).get("idEstatus")!=null ? objDatos.get(t).get("idEstatus"):"" );
				dto.setOrigenMovimiento(objDatos.get(t).get("origenMovimiento")!=null ? objDatos.get(t).get("origenMovimiento"):"" );
				dto.setFormaPago(objDatos.get(t).get("formaPago")!=null ? objDatos.get(t).get("formaPago"):"0" );
				dto.setbEntregado(objDatos.get(t).get("bEntregado")!=null ? objDatos.get(t).get("bEntregado"):"" );
				dto.setTipoMovimiento(objDatos.get(t).get("tipoMovimiento")!=null ? objDatos.get(t).get("tipoMovimiento"):"" );
				dto.setImporte(objDatos.get(t).get("importe")!=null ? objDatos.get(t).get("importe"):"0" );
				dto.setNoEmpresa(objDatos.get(t).get("noEmpresa")!=null ? objDatos.get(t).get("noEmpresa"):"0" );
				dto.setNoCuenta(objDatos.get(t).get("noCuenta")!=null ? objDatos.get(t).get("noCuenta"):"0" );
				dto.setIdChequera(objDatos.get(t).get("idChequera")!=null ? objDatos.get(t).get("idChequera"):"" );
				dto.setIdBanco(objDatos.get(t).get("idBanco")!=null ? objDatos.get(t).get("idBanco"):"0" );
				dto.setUsuarioAlta(objDatos.get(t).get("usuarioAlta")!=null ? objDatos.get(t).get("usuarioAlta"):"0" );
				dto.setNoDocumento(objDatos.get(t).get("noDocumento")!=null ? objDatos.get(t).get("noDocumento"):"" );
				dto.setLoteE(objDatos.get(t).get("loteE")!=null ? objDatos.get(t).get("loteE"):"0" );
				dto.setBsbc(objDatos.get(t).get("bsbc")!=null ? objDatos.get(t).get("bsbc"):"" );
				dto.setFecTransferencia(objDatos.get(t).get("fecTransferencia")!=null ? objDatos.get(t).get("fecTransferencia"):"" );
				dto.setDivisa(objDatos.get(t).get("divisa")!=null ? objDatos.get(t).get("divisa"):"" );
				dto.setDias(objDatos.get(t).get("dias")!=null ? objDatos.get(t).get("dias"):"" );
				dto.setObservacion(objDatos.get(t).get("observacion")!=null ? objDatos.get(t).get("observacion"):"" );
				dto.setDivision(objDatos.get(t).get("division")!=null ? objDatos.get(t).get("division"):"" );
				dto.setIdPoliza(objDatos.get(t).get("idPoliza")!=null ? objDatos.get(t).get("idPoliza"):"0" );
				dto.setFecOperacion(objDatos.get(t).get("fecOperacion")!=null ? objDatos.get(t).get("fecOperacion"):"" );
				dto.setFecAlta(objDatos.get(t).get("fecAlta")!=null ? objDatos.get(t).get("fecAlta"):"" );
				dto.setFecValorOriginal(objDatos.get(t).get("fecValorOriginal")!=null ? objDatos.get(t).get("fecValorOriginal"):"" );
				dto.setImporteOriginal(objDatos.get(t).get("importeOriginal")!=null ? objDatos.get(t).get("importeOriginal"):"0" );
				dto.setTipoCambio(objDatos.get(t).get("tipoCambio")!=null ? objDatos.get(t).get("tipoCambio"):"0" );
				dto.setIdCaja(objDatos.get(t).get("idCaja")!=null ? objDatos.get(t).get("idCaja"):"0" );
				dto.setIdDivisaOriginal(objDatos.get(t).get("idDivisaOriginal")!=null ? objDatos.get(t).get("idDivisaOriginal"):"" );
				dto.setIdBancoBenef(objDatos.get(t).get("idBancoBenef")!=null ? objDatos.get(t).get("idBancoBenef"):"0" );
				dto.setIdLeyenda(objDatos.get(t).get("idLeyenda")!=null ? objDatos.get(t).get("idLeyenda"):"" );
				dto.setClabe(objDatos.get(t).get("clabe")!=null ? objDatos.get(t).get("clabe"):"" );
				dto.setSolicita(objDatos.get(t).get("solicita")!=null ? objDatos.get(t).get("solicita"):"" );
				dto.setAutoriza(objDatos.get(t).get("autoriza")!=null ? objDatos.get(t).get("autoriza"):"" );
				dto.setPlaza(objDatos.get(t).get("plaza")!=null ? objDatos.get(t).get("plaza"):"0" );
				dto.setSucursal(objDatos.get(t).get("sucursal")!=null ? objDatos.get(t).get("sucursal"):"0" );
				dto.setNoFolioMov(objDatos.get(t).get("noFolioMov")!=null ? objDatos.get(t).get("noFolioMov"):"0" );
				dto.setIdRubro(objDatos.get(t).get("idRubro")!=null ? objDatos.get(t).get("idRubro"):"0" );
				dto.setIdGrupo(objDatos.get(t).get("idGrupo")!=null ? objDatos.get(t).get("idGrupo"):"0" );
				dto.setNoCliente(objDatos.get(t).get("noCliente")!=null ? objDatos.get(t).get("noCliente"):"" );
				dto.setIdChequeraBenef(objDatos.get(t).get("idChequeraBenef")!=null ? objDatos.get(t).get("idChequeraBenef"):"");
				dto.setReferencia(objDatos.get(t).get("referencia")!=null ? objDatos.get(t).get("referencia"):"");
				dto.setConcepto(objDatos.get(t).get("concepto")!=null ? objDatos.get(t).get("concepto"):"");
				dto.setBeneficiario(objDatos.get(t).get("beneficiario")!=null ? objDatos.get(t).get("beneficiario"):"");
				dto.setUsuario(usuario);
				dto.setPwd(objDatos.get(t).get("pwd")!=null ? objDatos.get(t).get("pwd"):"");
				dto.setNoCheque(objDatos.get(t).get("noCheque")!=null ? objDatos.get(t).get("noCheque"):"0");
				dto.setEjercicio(objDatos.get(t).get("ejercicio")!=null ? objDatos.get(t).get("ejercicio"):"0");
				dto.setPoHeders(objDatos.get(t).get("poHeaders")!=null ? objDatos.get(t).get("poHeaders"):"0");

			}
				resultado = objChequesTransitoService.cancelarCheque(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:ChequesTransitoAction, M:cancelarCheque");
		} return resultado;	
	}
	
	@DirectMethod
	public HSSFWorkbook obtenerExcel(String nombre) {
		FileInputStream file = null;
		HSSFWorkbook workbook = null;
		File arch = new File(nombre);
		try {
			file = new FileInputStream(arch);
			workbook = new HSSFWorkbook(file);
			file.close();
		} catch (FileNotFoundException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:impresion.mantenimientos, C:ChequesTransitoAction, M:obtenerExcel");
		} catch (IOException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:impresion.mantenimientos, C:ChequesTransitoAction, M:obtenerExcel");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:impresion.mantenimientos, C:ChequesTransitoAction, M:obtenerExcel");
		}
		arch.delete();
		return workbook;
	}
	
	@DirectMethod
	public String exportaExcel(String datos) {
		String resultado = "";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 197))
			return resultado;
		try {
			objChequesTransitoService  = (ChequesTransitoService )contexto.obtenerBean("objChequesTransitoBusiness");
			resultado = objChequesTransitoService .exportaExcel(datos);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: impresion.mantenimientos, C:ChequesTransitoAction, M: exportaExcel");
		}
		return resultado;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboBancos(int noEmpresa){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 197))
			return list;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMovimientosBEService = (MovimientosBEService)contexto.obtenerBean("objMovimientosBEBusiness");
			list=objMovimientosBEService.llenarComboBancos(noEmpresa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Bancaelectronica, C:MovimientosBEAction, M:llenarComboBancos");
		}return list;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboChequeras(int noBanco, int noEmpresa){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !(Utilerias.tienePermiso(WebContextManager.get(), 197)||Utilerias.tienePermiso(WebContextManager.get(), 77)))
			return list;

		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			objMovimientosBEService = (MovimientosBEService)contexto.obtenerBean("objMovimientosBEBusiness");
			list=objMovimientosBEService.llenarComboChequeras(noBanco, noEmpresa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Bancaelectronica, C:MovimientosBEAction, M:llenarComboChequeras");	
		}
		return list;
	}
}