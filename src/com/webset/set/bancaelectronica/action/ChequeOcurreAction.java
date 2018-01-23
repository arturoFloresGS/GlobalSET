package com.webset.set.bancaelectronica.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.bancaelectronica.business.EnvioTransferenciasBusiness;
import com.webset.set.bancaelectronica.dto.CriterioBusquedaDto;
import com.webset.set.bancaelectronica.service.ChequeOcurreService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.utils.tools.Utilerias;

public class ChequeOcurreAction {
	Bitacora bitacora = new Bitacora();
	Contexto contexto = new  Contexto();
	Funciones funciones = new Funciones();

	@DirectMethod
	public List<LlenaComboEmpresasDto> obtenerEmpresas(int idUsuario) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboEmpresasDto> list = new ArrayList<LlenaComboEmpresasDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				ChequeOcurreService chequeOcurreService = (ChequeOcurreService)contexto.obtenerBean("chequeOcurreBusiness");
				list = chequeOcurreService.obtenerEmpresas(idUsuario);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BE, C:ChequeOcurreAction, M:obtenerEmpresas");
		}
		return list;
	}

	@DirectMethod
	public List<LlenaComboGralDto> obtenerBancos(int noEmpresa) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			//if (Utilerias.haveSession(WebContextManager.get())) {
				ChequeOcurreService chequeOcurreService = (ChequeOcurreService)contexto.obtenerBean("chequeOcurreBusiness");
				list = chequeOcurreService.llenaComboBanco(noEmpresa);
			//}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BE, C:ChequeOcurreAction, M:obtenerEmpresas");
		}
		return list;
	}
	
	@DirectMethod
	public List<MovimientoDto> consultarCheques(int noEmpresa, int idBanco) {
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<MovimientoDto> list = new ArrayList<MovimientoDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
				ChequeOcurreService chequeOcurreService = (ChequeOcurreService)contexto.obtenerBean("chequeOcurreBusiness");
				list = chequeOcurreService.consultarCheques(noEmpresa, idBanco);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BE, C:ChequeOcurreAction, M:consultarCheques");
		}
		return list;
	}
	
//	@DirectMethod
//	public Map<String, Object> ejecutar(String jSonMovimientos, int optSantanderDirecto, int idBanco, boolean chkSantanderH2H) {
//		Map<String,Object> respuesta= new HashMap<String,Object>();
//		if (!Utilerias.haveSession(WebContextManager.get())){
//			respuesta.put("msgUsuario", "No tiene una sesión Iniciada");
//			return respuesta;
//		} 
//			
//		try {
//			if (Utilerias.haveSession(WebContextManager.get())) {
//				Gson gson = new Gson();
//				List<Map<String, String>> list = gson.fromJson(jSonMovimientos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
//				List<MovimientoDto> movimientos = new ArrayList<MovimientoDto>();
//				System.out.println("entro a ejecutar action"+optSantanderDirecto+" "+chkSantanderH2H);
//				for (int i = 0; i < list.size(); i++) {
//					MovimientoDto movimiento = new MovimientoDto();
//					movimiento.setFecValor(funciones.ponerFechaDate(list.get(i).get("fecValor").toString()));
//					movimiento.setNoEmpresa(list.get(i).get("noEmpresa")!=null?Integer.parseInt(list.get(i).get("noEmpresa")):0);
//					movimiento.setIdBanco(list.get(i).get("idBanco")!=null?Integer.parseInt(list.get(i).get("idBanco")):0);
//					movimiento.setNoCliente(list.get(i).get("noCliente")!=null?list.get(i).get("noCliente"):"");
//					movimiento.setIdChequera(list.get(i).get("idChequera") != null ? list.get(i).get("idChequera") : "");
//					movimiento.setIdChequeraBenef(list.get(i).get("idChequeraBenef") != null ? list.get(i).get("idChequeraBenef") : "");
//					movimiento.setImporte(list.get(i).get("importe")!=null?Double.parseDouble(list.get(i).get("importe")):0);
//					movimiento.setNoFolioDet(list.get(i).get("noFolioDet")!=null?Integer.parseInt(list.get(i).get("noFolioDet")):0);
//					movimiento.setReferencia(list.get(i).get("referencia") != null ? list.get(i).get("referencia") : "");
//					movimiento.setBeneficiario(list.get(i).get("beneficiario") != null ? list.get(i).get("beneficiario") : "");
//					movimiento.setRfcBenef(list.get(i).get("rfcBenef") != null ? list.get(i).get("rfcBenef") : "");
//					movimiento.setConcepto(list.get(i).get("concepto") != null ? list.get(i).get("concepto") : "");
//					movimiento.setSucDestino(list.get(i).get("sucursal") != null ? list.get(i).get("sucursal") : "0");
//		    		movimiento.setPlazaBenef(list.get(i).get("plaza") != null ? list.get(i).get("plaza") : "0");
//		    		movimiento.setNoDocto(list.get(i).get("noDocto") != null ? list.get(i).get("noDocto") : "");
//		    		movimiento.setIdBancoBenef(list.get(i).get("idBancoBenef")!=null ? Integer.parseInt(list.get(i).get("idBancoBenef")):0);
//					movimientos.add(movimiento);
//				}
//				ChequeOcurreService chequeOcurreService = (ChequeOcurreService)contexto.obtenerBean("chequeOcurreBusiness");				
//				return chequeOcurreService.ejecutar(movimientos, optSantanderDirecto, idBanco, chkSantanderH2H);
//			
//			}else{
//				respuesta.put("msgUsuario", "No tiene una sesión Iniciada");
//				return respuesta;
//			} 
//		}catch(Exception e){
//			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BE, C:ChequeOcurreAction, M:ejecutarCheques");
//			System.out.println("Error"+e.toString());
//			respuesta.put("msgUsuario", "Error al crear conexión con el servidor"+e.toString());
//			return respuesta;
//		}
//	}
	
	
	@DirectMethod
	public List<MovimientoDto> consultaCheques(boolean chkTodasEmpresas, boolean chkSoloLocales, boolean chkConvenioCie,
		boolean chkMassPayment, String optTipoEnvio, String optComerica, boolean optVenc, String idBancoEmi, String idUsuario, int idEmpresa, 
				 boolean nomina, boolean chkConvenioSant, boolean chkDebito){
		
		CriterioBusquedaDto dtoBus = new CriterioBusquedaDto();
		List<MovimientoDto> list = new ArrayList<MovimientoDto>();
//		if(!Utilerias.haveSession(WebContextManager.get()) && !Utilerias.tienePermiso(WebContextManager.get(),38)) 
//			return null;
		System.out.println("entro a consultaCheques action ");
		try{
			
			//if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),38)){
			//EnvioTransferenciasBusiness envioTransferenciasBusiness=(EnvioTransferenciasBusiness)contexto.obtenerBean("envioTransferenciasBusiness");
			ChequeOcurreService chequeOcurreService = (ChequeOcurreService)contexto.obtenerBean("chequeOcurreBusiness");

			dtoBus.setChkTodasEmpresas(idEmpresa != 0 ? chkTodasEmpresas : true);
			dtoBus.setChkSoloLocales(chkSoloLocales);
			dtoBus.setChkConvenioCie(chkConvenioCie);
			dtoBus.setChkMassPayment(chkMassPayment);
			dtoBus.setOptTipoEnvio(optTipoEnvio);
			dtoBus.setOptComerica(optComerica!=null?optComerica:"");
			dtoBus.setOptVenc(optVenc);
		//	dtoBus.setMontoIni(montoIni!=null && !montoIni.equals("")?Double.parseDouble(montoIni):0);
		//	dtoBus.setMontoFin(montoFin!=null && !montoFin.equals("") ?Double.parseDouble(montoFin):0);
		//	dtoBus.setPsFechaValor(fechaValor!=null?fechaValor:"");
		//	dtoBus.setPsFechaValorOrig(fecValorOrig!=null?fecValorOrig:"");
		//	dtoBus.setPlBancoReceptor(idBancoRec!=null && !idBancoRec.equals("")?Integer.parseInt(idBancoRec):0);
			dtoBus.setIdBanco(idBancoEmi!=null && !idBancoEmi.equals("")?Integer.parseInt(idBancoEmi):0);
//			dtoBus.setIdDivisa(idDivisa!=null?idDivisa:"");
//			dtoBus.setPsDivision(idDivision!=null?idDivision:"");
			dtoBus.setIdUsuario(idUsuario!=null && !idUsuario.equals("")?Integer.parseInt(idUsuario):0);
			dtoBus.setIdEmpresa(idEmpresa);
//			dtoBus.setValidaCampo(cveControl);
			dtoBus.setNomina(nomina);
			dtoBus.setChkConvenioSant(chkConvenioSant);
			dtoBus.setChkDebito(chkDebito);
			list = chequeOcurreService.consultaCheques(dtoBus);
			//list=envioTransferenciasBusiness.consultarPagos(dtoBus);
			
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BancaElectronica, C:EnvioTransferenciasAction, M:consultarPagos");
			e.printStackTrace();
		}
		return list;
	}
	
	

	@DirectMethod
	public Map<String, Object> ejecutarCheques(String jSonMovimientos, String criterios, int h2hAfrd) {
		Map<String,Object> respuesta= new HashMap<String,Object>();
		if (!Utilerias.haveSession(WebContextManager.get())){
			respuesta.put("msgUsuario", "No tiene una sesión Iniciada");
			return respuesta;
		} 
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(jSonMovimientos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<MovimientoDto> movimientos = new ArrayList<MovimientoDto>();
		List<Map<String, String>> crite = gson.fromJson(criterios, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());

		CriterioBusquedaDto dtoBus = new CriterioBusquedaDto();
		List<MovimientoDto> listBus = new ArrayList<MovimientoDto>();
		Map<String,Object> mapRet = new HashMap<String,Object>();
		try {
				
				System.out.println("entro a ejecutar action"+" "+crite.get(0).get("optEnvioBNMX")+ h2hAfrd);
				
				ChequeOcurreService chequeOcurreService = (ChequeOcurreService)contexto.obtenerBean("chequeOcurreBusiness");				

				for (int i = 0; i < objParams.size(); i++) {
					MovimientoDto dto = new MovimientoDto();
					dto.setNoDocto(objParams.get(i).get("noDocto")!=null?objParams.get(i).get("noDocto"):"");
					if(objParams.get(i).get("fecOperacion")!=null)
						dto.setFecOperacion(funciones.ponerFechaDate(objParams.get(i).get("fecOperacion")));
					dto.setIdBancoCityStr( objParams.get(i).get("idBancoCityStr"));
					dto.setBeneficiario(objParams.get(i).get("beneficiario"));
					dto.setImporte(objParams.get(i).get("importe")!=null?Double.parseDouble(objParams.get(i).get("importe")):0);
					dto.setImporteStr(objParams.get(i).get("importe")!=null?objParams.get(i).get("importe"):"0");
					dto.setIdDivisa(objParams.get(i).get("idDivisa"));
					dto.setNombreBancoBenef(objParams.get(i).get("nombreBancoBenef"));
					dto.setIdChequeraBenef(objParams.get(i).get("idChequeraBenef"));
					dto.setIdChequeraBenefReal(objParams.get(i).get("idChequeraBenefReal"));
					dto.setConcepto(objParams.get(i).get("concepto"));
					dto.setNoFolioDet(objParams.get(i).get("noFolioDet")!=null?Integer.parseInt(objParams.get(i).get("noFolioDet")):0);	 
					dto.setNoEmpresa(objParams.get(i).get("noEmpresa")!=null?Integer.parseInt(objParams.get(i).get("noEmpresa")):0);
					dto.setOrigenMov(objParams.get(i).get("origenMov"));
					dto.setDivision(objParams.get(i).get("division"));
					dto.setNoCliente(objParams.get(i).get("noCliente"));
					dto.setEspeciales(objParams.get(i).get("especiales"));	 
					dto.setComplemento(objParams.get(i).get("complemento"));
					dto.setClave(objParams.get(i).get("clave"));
					dto.setNomEmpresa(objParams.get(i).get("nomEmpresa"));
					dto.setIdBancoBenef(objParams.get(i).get("idBancoBenef")!=null?Integer.parseInt(objParams.get(i).get("idBancoBenef")):0);
					dto.setPlazaBenef(objParams.get(i).get("plazaBenef"));
					dto.setPlaza(objParams.get(i).get("plaza")!=null?Integer.parseInt(objParams.get(i).get("plaza")):0);
					dto.setSucDestino(objParams.get(i).get("sucDestino") != null ?  objParams.get(i).get("sucDestino"):"");
					dto.setClabeBenef(objParams.get(i).get("clabeBenef") != null ?objParams.get(i).get("clabeBenef"):"");
					dto.setBLayoutComerica(objParams.get(i).get("bLayoutComerica") != null ? objParams.get(i).get("bLayoutComerica") : "");
					dto.setIdChequera(objParams.get(i).get("idChequera"));
					//dto.setConcepto(objParams.get(i).get("concepto") != null ? objParams.get(i).get("sucDestino"):"");
					dto.setConcepto(objParams.get(i).get("concepto") != null ? objParams.get(i).get("concepto"):"");
					dto.setFecValor(funciones.ponerFechaDate(objParams.get(i).get("fecValor").toString()));
					dto.setIdBanco(objParams.get(i).get("idBanco")!=null?Integer.parseInt(objParams.get(i).get("idBanco")):0);
					dto.setInstFinan(objParams.get(i).get("instFinan"));
					dto.setSucOrigen(objParams.get(i).get("sucOrigen"));
					dto.setRfcBenef(objParams.get(i).get("rfcBenef") == null ? "" : objParams.get(i).get("rfcBenef"));
					dto.setHoraRecibo(objParams.get(i).get("horaRecibo"));
					dto.setIdContratoMass(objParams.get(i).get("idContratoMass") != null ? Integer.parseInt(objParams.get(i).get("idContratoMass")) : 0);
					dto.setTipoEnvioLayout(objParams.get(i).get("tipoEnvioLayout") != null ? Integer.parseInt(objParams.get(i).get("tipoEnvioLayout")) : 0);
					dto.setNomBancoIntermediario(objParams.get(i).get("nomBancoIntermediario") != null ? objParams.get(i).get("nomBancoIntermediario") : "");
					dto.setDescBancoBenef(objParams.get(i).get("descBancoBenef") != null ? objParams.get(i).get("descBancoBenef") : "");
				//	dto.setIdChequeraBanamex(objParams.get(i).get("idBancoEmi").equals('2')?'':'');
					String cheq=objParams.get(i).get("idChequera");
					int tamano=cheq.length();
					System.out.print("tamano de cheque"+tamano);
					String cadena=cheq.substring(5);
					System.out.print("id_chequera= "+cheq+" cadena="+cadena);
					String cad2=funciones.ponerCeros(cadena, 11);
					System.out.print("cadena final "+cad2);
					dto.setIdChequeraBanamex(cad2);
					//  Format(Mid(!id_chequera & "", 5), String(11, "0"))
						
					
					if(objParams.get(i).get("aba") != null && !objParams.get(i).get("aba").equals("") ) {
						dto.setAba("FW");
						dto.setSwiftCode(objParams.get(i).get("aba").trim());
					}else if( objParams.get(i).get("swiftCode") != null && !objParams.get(i).get("swiftCode").equals("")) {
						dto.setAba("IS");
						dto.setSwiftCode(objParams.get(i).get("swiftCode").trim());
					}	
					if(objParams.get(i).get("abaIntermediario") != null && !objParams.get(i).get("abaIntermediario").equals("")) {
						dto.setAbaIntermediario("FW");
						dto.setSwiftIntermediario(objParams.get(i).get("abaIntermediario").trim());
					}else if( objParams.get(i).get("swiftIntermediario") != null && !objParams.get(i).get("swiftIntermediario").equals("") ) {
						dto.setAbaIntermediario("IS");
						dto.setSwiftIntermediario(objParams.get(i).get("swiftIntermediario").trim());
					}
					if( objParams.get(i).get("abaCorresponsal") != null && !objParams.get(i).get("abaCorresponsal").equals("") ) {
						dto.setAbaIntermediario("//FW=" + objParams.get(i).get("abaCorresponsal").trim());
						dto.setSwiftIntermediario("");
					}else if(objParams.get(i).get("swiftCorresponsal") != null && !objParams.get(i).get("swiftCorresponsal").equals("")) {
						dto.setAbaIntermediario("IS" + objParams.get(i).get("swiftCorresponsal").trim());
						dto.setSwiftIntermediario("");
					}
					dto.setNomBancoCorresponsal(objParams.get(i).get("nomBancoCorresponsal") != null ? objParams.get(i).get("nomBancoCorresponsal") : "");
					dto.setDescUsuarioBital(objParams.get(i).get("descUsuarioBital") != null ? objParams.get(i).get("descUsuarioBital") : "");
					dto.setDescServicioBital(objParams.get(i).get("descServicioBital") != null ? objParams.get(i).get("descServicioBital") : "");
				
					listBus.add(dto);
				}
				for(int in=0; in<crite.size();in++)
				{
//					dtoBus.setChkTodasEmpresas(crite.get(0).get("chkTodasEmpresas")!=null &&crite.get(0).get("chkTodasEmpresas").equals("true")?true:false);
//					dtoBus.setChkSoloLocales(crite.get(0).get("chkSoloLocales")!=null && crite.get(0).get("chkSoloLocales").equals("true")?true:false);
//					dtoBus.setChkConvenioCie(crite.get(0).get("chkSoloLocales")!=null && crite.get(0).get("chkConvenioCie").equals("true")?true:false);
//					dtoBus.setChkMassPayment(crite.get(0).get("chkSoloLocales")!=null && crite.get(0).get("chkMassPayment").equals("true")?true:false);
					dtoBus.setChkH2H(crite.get(0).get("chkH2H") != null && crite.get(0).get("chkH2H")=="1" ? true : false);
//					dtoBus.setChkH2HSantander(crite.get(0).get("chkH2HSantander") != null && crite.get(0).get("chkH2HSantander").equals("true") ? true : false);
//					dtoBus.setNomina(crite.get(0).get("nomina") != null && crite.get(0).get("nomina").equals("true") ? true : false);
					dtoBus.setOptEnvioBNMX(crite.get(0).get("optEnvioBNMX") != null ? Integer.parseInt(crite.get(0).get("optEnvioBNMX")) : 0);
//					dtoBus.setOptEnvioHSBC(crite.get(0).get("optEnvioHSBC") != null ? Integer.parseInt(crite.get(0).get("optEnvioHSBC")) : 0);
					dtoBus.setOptTipoEnvio(crite.get(0).get("optTipoEnvio") != null ? crite.get(0).get("optTipoEnvio") : "");
//					dtoBus.setOptComerica(crite.get(0).get("optComerica") != null ? crite.get(0).get("optComerica") : "");
//					dtoBus.setOptVenc(crite.get(0).get("optVenc")!=null?true:false);
//					dtoBus.setMontoIni(crite.get(0).get("montoIni")!=null?Double.parseDouble(crite.get(0).get("montoIni")):0);
//					dtoBus.setMontoFin(crite.get(0).get("montoFin")!=null?Double.parseDouble(crite.get(0).get("montoFin")):0);
//					dtoBus.setPsFechaValor(crite.get(0).get("fechaValor"));
//					dtoBus.setPsFechaValorOrig(crite.get(0).get("fecValorOrig"));
//					
//					dtoBus.setPlBancoReceptor(crite.get(0).get("idBancoRec")!=null?Integer.parseInt(crite.get(0).get("idBancoRec")):0);
					dtoBus.setIdBanco(crite.get(0).get("idBancoEmi")!=null?Integer.parseInt(crite.get(0).get("idBancoEmi")):0);
//					dtoBus.setIdDivisa(crite.get(0).get("idDivisa"));
//					dtoBus.setPsDivision(crite.get(0).get("idDivision"));
					dtoBus.setIdUsuario(crite.get(0).get("idUsuario")!=null?Integer.parseInt(crite.get(0).get("idUsuario")):0);
					dtoBus.setIdEmpresa(crite.get(0).get("idEmpresa")!=null?Integer.parseInt(crite.get(0).get("idEmpresa")):0);
//					dtoBus.setChkConvenioSant(crite.get(0).get("chkConvenioSant") != null 
//							&& crite.get(0).get("chkConvenioSant").equals("true") ? true : false);
//					dtoBus.setChkDebito(crite.get(0).get("chkDebito") != null 
//							&& crite.get(0).get("chkDebito").equals("true") ? true : false);
				}
			mapRet=chequeOcurreService.ejecutarCheques(listBus, dtoBus,h2hAfrd);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BE, C:ChequeOcurreAction, M:ejecutarCheques");
			System.out.println("Error"+e.toString());
			respuesta.put("msgUsuario", "Error al crear conexión con el servidor"+e.toString());
			
		}
		return mapRet;
		
	}
	
}
