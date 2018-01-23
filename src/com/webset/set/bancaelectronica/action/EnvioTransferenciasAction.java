package com.webset.set.bancaelectronica.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import net.sf.jasperreports.engine.JRDataSource;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.bancaelectronica.business.EnvioTransferenciasBusiness;
import com.webset.set.bancaelectronica.dto.CriterioBusquedaDto;
import com.webset.set.egresos.service.ConfirmacionTransferenciasService;
import com.webset.set.egresos.service.ConsultaPropuestasService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.utils.tools.Utilerias;

public class EnvioTransferenciasAction {
	Bitacora bitacora = new Bitacora();
	Contexto contexto = new Contexto();
	Funciones funciones = new Funciones();
	ConsultaPropuestasService consultaPropuestasService;
	
	@DirectMethod
	public List<LlenaComboGralDto>llenarCombo(String campoUno, String campoDos, String tabla, String condicion, String orden){
		
		if(!Utilerias.haveSession(WebContextManager.get()) && !Utilerias.tienePermiso(WebContextManager.get(),38)){
			List<LlenaComboGralDto> tmp = new ArrayList<>();
			return tmp;
		}
				
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		LlenaComboGralDto dto= new LlenaComboGralDto();
		try{
			consultaPropuestasService = (ConsultaPropuestasService)contexto.obtenerBean("consultaPropuestasBusinessImpl");
			dto.setCampoUno(campoUno!=null && !campoUno.equals("")?campoUno:"");
			dto.setCampoDos(campoDos!=null && !campoDos.equals("")?campoDos:"");
			dto.setTabla(tabla!=null && !tabla.equals("")?tabla:"");
			dto.setCondicion(condicion!=null && !condicion.equals("")?condicion:"");
			dto.setOrden(orden!=null && !orden.equals("")?orden:"");
			list = consultaPropuestasService.LlenarComboGral(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:llenarCombo");	
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboEmpresasDto> obtenerEmpresas(int idUsuario) {
		
		List<LlenaComboEmpresasDto> list = new ArrayList<LlenaComboEmpresasDto>();
		try {
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),38)){
				ConfirmacionTransferenciasService confirmacionTransferenciasService = (ConfirmacionTransferenciasService)contexto.obtenerBean("confirmacionTransferenciasBusinessImpl");
				list = confirmacionTransferenciasService.obtenerEmpresas(idUsuario);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionTransferenciasAction, M:obtenerEmpresas");
		}
		return list;
	}
	
	
	@DirectMethod
	public List<LlenaComboGralDto> obtenerBancoEmisor(int idUsuario){
		if(!Utilerias.haveSession(WebContextManager.get()) && !Utilerias.tienePermiso(WebContextManager.get(),38))
			return null;
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),38)){
				EnvioTransferenciasBusiness envioTransferenciasBusiness=(EnvioTransferenciasBusiness)contexto.obtenerBean("envioTransferenciasBusiness");
				list=envioTransferenciasBusiness.llenarBancoEmisor(idUsuario);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:EnvioTransferenciasAction, M:obtenerBancoEmisor");
		}
		return list;
	}
	
	
	@DirectMethod
	public List<LlenaComboGralDto> llenaComboCveControl(){
		if(!Utilerias.haveSession(WebContextManager.get()) && !Utilerias.tienePermiso(WebContextManager.get(),38))
			return null;
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),38)){
				EnvioTransferenciasBusiness envioTransferenciasBusiness=(EnvioTransferenciasBusiness)contexto.obtenerBean("envioTransferenciasBusiness");
				list=envioTransferenciasBusiness.llenaComboCveControl();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:EnvioTransferenciasAction, M:obtenerBancoEmisor");
		}
		return list;
	}
	
	
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboGral(String campoUno, String campoDos, String tabla, String condicion, String orden){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		LlenaComboGralDto dto= new LlenaComboGralDto();
		if(!Utilerias.haveSession(WebContextManager.get()) && !Utilerias.tienePermiso(WebContextManager.get(),38)) 
			return null;
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),38)){
			EnvioTransferenciasBusiness envioTransferenciasBusiness=(EnvioTransferenciasBusiness)contexto.obtenerBean("envioTransferenciasBusiness");
			dto.setCampoUno(campoUno!=null && !campoUno.equals("")?campoUno:"");
			dto.setCampoDos(campoDos!=null && !campoDos.equals("")?campoDos:"");
			dto.setTabla(tabla!=null && !tabla.equals("")?tabla:"");
			dto.setCondicion(condicion!=null && !condicion.equals("")?condicion:"");
			dto.setOrden(orden!=null && !orden.equals("")?orden:"");
			list=envioTransferenciasBusiness.llenarComboGral(dto);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BancaElectronica, C:EnvioTransferenciasAction, M:llenarComboGral");	
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboDivisaDto>llenarComboDivisa(){
		if(!Utilerias.haveSession(WebContextManager.get()) && !Utilerias.tienePermiso(WebContextManager.get(),38)) 
			return null;
		List<LlenaComboDivisaDto> list= new ArrayList<LlenaComboDivisaDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),38)){
			EnvioTransferenciasBusiness envioTransferenciasBusiness=(EnvioTransferenciasBusiness)contexto.obtenerBean("envioTransferenciasBusiness");
			
			list=envioTransferenciasBusiness.llenarComboDivisa();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BancaElectronica, C:EnvioTransferenciasAction, M:llenarComboDivisa");	
		}
		return list;
	}
	
	@DirectMethod
	public List<MovimientoDto> consultarPagos(boolean chkTodasEmpresas, boolean chkSoloLocales, boolean chkConvenioCie,
		boolean chkMassPayment, String optTipoEnvio, String optComerica, boolean optVenc,String montoIni, String montoFin, String fechaValor,
			String fecValorOrig, String idBancoRec, String idBancoEmi, String idDivisa, String idDivision, String idUsuario, int idEmpresa, 
				String cveControl, boolean nomina, boolean chkConvenioSant, boolean chkDebito){
	
		CriterioBusquedaDto dtoBus = new CriterioBusquedaDto();
		List<MovimientoDto> list = new ArrayList<MovimientoDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) && !Utilerias.tienePermiso(WebContextManager.get(),38)) 
			return null;
		
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),38)){
			EnvioTransferenciasBusiness envioTransferenciasBusiness=(EnvioTransferenciasBusiness)contexto.obtenerBean("envioTransferenciasBusiness");
			
			dtoBus.setChkTodasEmpresas(idEmpresa != 0 ? chkTodasEmpresas : true);
			dtoBus.setChkSoloLocales(chkSoloLocales);
			dtoBus.setChkConvenioCie(chkConvenioCie);
			dtoBus.setChkMassPayment(chkMassPayment);
			dtoBus.setOptTipoEnvio(optTipoEnvio);
			dtoBus.setOptComerica(optComerica!=null?optComerica:"");
			dtoBus.setOptVenc(optVenc);
			dtoBus.setMontoIni(montoIni!=null && !montoIni.equals("")?Double.parseDouble(montoIni):0);
			dtoBus.setMontoFin(montoFin!=null && !montoFin.equals("") ?Double.parseDouble(montoFin):0);
			dtoBus.setPsFechaValor(fechaValor!=null?fechaValor:"");
			dtoBus.setPsFechaValorOrig(fecValorOrig!=null?fecValorOrig:"");
			dtoBus.setPlBancoReceptor(idBancoRec!=null && !idBancoRec.equals("")?Integer.parseInt(idBancoRec):0);
			dtoBus.setIdBanco(idBancoEmi!=null && !idBancoEmi.equals("")?Integer.parseInt(idBancoEmi):0);
			dtoBus.setIdDivisa(idDivisa!=null?idDivisa:"");
			dtoBus.setPsDivision(idDivision!=null?idDivision:"");
			dtoBus.setIdUsuario(idUsuario!=null && !idUsuario.equals("")?Integer.parseInt(idUsuario):0);
			dtoBus.setIdEmpresa(idEmpresa);
			dtoBus.setValidaCampo(cveControl);
			dtoBus.setNomina(nomina);
			dtoBus.setChkConvenioSant(chkConvenioSant);
			dtoBus.setChkDebito(chkDebito);
			
			list=envioTransferenciasBusiness.consultarPagos(dtoBus);
			
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BancaElectronica, C:EnvioTransferenciasAction, M:consultarPagos");
			e.printStackTrace();
		}
		return list;
	}
	
	//Revisado A.A.G
	@DirectMethod
	public Map<String,Object> ejecutarEnvioTrans(String datosGrid,String criterios,int h2hAfrd,int h2hBBVA){
		if(!Utilerias.haveSession(WebContextManager.get()) && !Utilerias.tienePermiso(WebContextManager.get(),38)) 
			return null;
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(datosGrid, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<Map<String, String>> crite = gson.fromJson(criterios, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		CriterioBusquedaDto dtoBus = new CriterioBusquedaDto();
		List<MovimientoDto> listBus = new ArrayList<MovimientoDto>();
		Map<String,Object> mapRet = new HashMap<String,Object>();
		String respuesta = "";
		
		try {
			respuesta = validaChequerasBenef(objParams);
			if(!respuesta.equals("")) {
				mapRet.put("msgUsuario", respuesta);
				return mapRet;
			}
			EnvioTransferenciasBusiness envioTransferenciasBusiness=(EnvioTransferenciasBusiness)contexto.obtenerBean("envioTransferenciasBusiness");
			for(int i=0; i<objParams.size();i++) {
				MovimientoDto dto = new MovimientoDto();
				dto.setNoDocto(objParams.get(i).get("noDocto")!=null?objParams.get(i).get("noDocto"):"");
				if(objParams.get(i).get("fecOperacion")!=null)
					dto.setFecOperacion(funciones.ponerFechaDate(objParams.get(i).get("fecOperacion")));
				dto.setIdBancoCityStr( objParams.get(i).get("idBancoCityStr"));
				if(objParams.get(i).get("beneficiario").length()<=35){
					dto.setBeneficiario(objParams.get(i).get("beneficiario"));
				}else{
					String benef=objParams.get(i).get("beneficiario");
					int tama=benef.length();
					System.out.print("tamano de benef"+tama);
					String cadena2=benef.substring(0,35);
					System.out.println("benef final "+cadena2);
					System.out.println("tam final "+cadena2.length());
					dto.setBeneficiario(cadena2);
				}
				
				
				dto.setImporte(objParams.get(i).get("importe")!=null?Double.parseDouble(objParams.get(i).get("importe")):0);
				dto.setImporteStr(objParams.get(i).get("importe")!=null?objParams.get(i).get("importe"):"0");
				dto.setIdDivisa(objParams.get(i).get("idDivisa"));
				dto.setNombreBancoBenef(objParams.get(i).get("nombreBancoBenef"));
				dto.setIdChequeraBenef(objParams.get(i).get("idChequeraBenef"));
				//System.out.println("tipo envio "+crite.get(0).get("optTipoEnvio"));
				
				if(crite.get(0).get("optTipoEnvio").equals("Mismo") && Integer.parseInt(crite.get(0).get("idBancoEmi"))!=12){
					dto.setClabeBenef(objParams.get(i).get("idChequeraBenef"));
					//System.out.println("chequera benef "+objParams.get(i).get("idChequeraBenef"));
				}else{
					dto.setClabeBenef(objParams.get(i).get("clabeBenef") != null ?objParams.get(i).get("clabeBenef"):"");
					//System.out.println("clabe del beneficiario "+objParams.get(i).get("clabeBenef"));
				}
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
				//dto.setClabeBenef(objParams.get(i).get("clabeBenef") != null ?objParams.get(i).get("clabeBenef"):"");
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
				dtoBus.setChkTodasEmpresas(crite.get(0).get("chkTodasEmpresas")!=null &&crite.get(0).get("chkTodasEmpresas").equals("true")?true:false);
				dtoBus.setChkSoloLocales(crite.get(0).get("chkSoloLocales")!=null && crite.get(0).get("chkSoloLocales").equals("true")?true:false);
				dtoBus.setChkConvenioCie(crite.get(0).get("chkSoloLocales")!=null && crite.get(0).get("chkConvenioCie").equals("true")?true:false);
				dtoBus.setChkMassPayment(crite.get(0).get("chkSoloLocales")!=null && crite.get(0).get("chkMassPayment").equals("true")?true:false);
				dtoBus.setChkH2H(crite.get(0).get("chkH2H") != null && crite.get(0).get("chkH2H").equals("true") ? true : false);
				dtoBus.setChkH2HSantander(crite.get(0).get("chkH2HSantander") != null && crite.get(0).get("chkH2HSantander").equals("true") ? true : false);
				dtoBus.setNomina(crite.get(0).get("nomina") != null && crite.get(0).get("nomina").equals("true") ? true : false);
				dtoBus.setOptEnvioBNMX(crite.get(0).get("optEnvioBNMX") != null ? Integer.parseInt(crite.get(0).get("optEnvioBNMX")) : 0);
				dtoBus.setOptEnvioHSBC(crite.get(0).get("optEnvioHSBC") != null ? Integer.parseInt(crite.get(0).get("optEnvioHSBC")) : 0);
				
				if(h2hBBVA==1){
					dtoBus.setOptEnvioBBVA(1);
					System.out.println("envio..:::.."+dtoBus.getOptEnvioBBVA());
						
				}else{
					dtoBus.setOptEnvioBBVA(crite.get(0).get("optEnvioBBVA") != null ? Integer.parseInt(crite.get(0).get("optEnvioHSBC")) : 0);
					System.out.println("envio..:::.."+dtoBus.getOptEnvioBBVA());
					
				}
				
				
				dtoBus.setOptTipoEnvio(crite.get(0).get("optTipoEnvio") != null ? crite.get(0).get("optTipoEnvio") : "");
				dtoBus.setOptComerica(crite.get(0).get("optComerica") != null ? crite.get(0).get("optComerica") : "");
				dtoBus.setOptVenc(crite.get(0).get("optVenc")!=null?true:false);
				dtoBus.setMontoIni(crite.get(0).get("montoIni")!=null?Double.parseDouble(crite.get(0).get("montoIni")):0);
				dtoBus.setMontoFin(crite.get(0).get("montoFin")!=null?Double.parseDouble(crite.get(0).get("montoFin")):0);
				dtoBus.setPsFechaValor(crite.get(0).get("fechaValor"));
				dtoBus.setPsFechaValorOrig(crite.get(0).get("fecValorOrig"));
				
				dtoBus.setPlBancoReceptor(crite.get(0).get("idBancoRec")!=null?Integer.parseInt(crite.get(0).get("idBancoRec")):0);
				dtoBus.setIdBanco(crite.get(0).get("idBancoEmi")!=null?Integer.parseInt(crite.get(0).get("idBancoEmi")):0);
				dtoBus.setIdDivisa(crite.get(0).get("idDivisa"));
				dtoBus.setPsDivision(crite.get(0).get("idDivision"));
				dtoBus.setIdUsuario(crite.get(0).get("idUsuario")!=null?Integer.parseInt(crite.get(0).get("idUsuario")):0);
				dtoBus.setIdEmpresa(crite.get(0).get("idEmpresa")!=null?Integer.parseInt(crite.get(0).get("idEmpresa")):0);
				dtoBus.setChkConvenioSant(crite.get(0).get("chkConvenioSant") != null 
						&& crite.get(0).get("chkConvenioSant").equals("true") ? true : false);
				dtoBus.setChkDebito(crite.get(0).get("chkDebito") != null 
						&& crite.get(0).get("chkDebito").equals("true") ? true : false);
			}
			mapRet=envioTransferenciasBusiness.ejecutarEnvioTrans(listBus, dtoBus,h2hAfrd,h2hBBVA);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BancaElectronica, C:EnvioTransferenciasAction, M:ejecutarEnvioTrans");
			e.printStackTrace();
		}
	return mapRet;
	}
	
	@DirectMethod
	public JRDataSource reporteDetArchivoTransf(Map<String, Object> datos, ServletContext context) {
		JRDataSource jrDataSource = null;
		CriterioBusquedaDto dtoBus = new CriterioBusquedaDto();
		
		try {
			System.out.println("datos "+datos);
			EnvioTransferenciasBusiness envioTransferenciasBusiness=(EnvioTransferenciasBusiness)contexto.obtenerBean("envioTransferenciasBusiness", context);
			dtoBus.setCampo(datos.get("archivo") != null ? datos.get("archivo").toString() : "");
			jrDataSource = envioTransferenciasBusiness.reporteDetArchivoTransf(dtoBus);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:EnvioTransferenciasAction, M:reporteDetArchivoTransf");	
		}
		return jrDataSource;
	}
	
	public String validaChequerasBenef(List<Map<String, String>> objParams) {
		int resp = 0;
		
		try {
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),38)){
			EnvioTransferenciasBusiness envioTransferenciasBusiness = (EnvioTransferenciasBusiness)contexto.obtenerBean("envioTransferenciasBusiness");
			
			for(int i=0; i<objParams.size(); i++) {
				resp = envioTransferenciasBusiness.buscaDatosBenef(objParams, i);
				
				if(resp == 0) 
					return "Verificar los datos del beneficiario: '"+ objParams.get(i).get("beneficiario") +"', \n banco o chequera son incorrectos";
			}
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:EnvioTransferenciasAction, M:validaChequerasBenef");
			return "Ocurrio un error al validar los datos del beneficiario";
		}
		return "";
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> obtenerBancosBenef(int noPersona){
		if(!Utilerias.haveSession(WebContextManager.get()) && !Utilerias.tienePermiso(WebContextManager.get(),38)) 
			return null;
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),38)){
			EnvioTransferenciasBusiness envioTransferenciasBusiness=(EnvioTransferenciasBusiness)contexto.obtenerBean("envioTransferenciasBusiness");
			list = envioTransferenciasBusiness.obtenerBancosBenef(noPersona);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:EnvioTransferenciasAction, M:obtenerBancosBenef");
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> obtenerChequerasBenef(int noPersona, int idBanco){
		
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),38)){
			EnvioTransferenciasBusiness envioTransferenciasBusiness=(EnvioTransferenciasBusiness)contexto.obtenerBean("envioTransferenciasBusiness");
			list = envioTransferenciasBusiness.obtenerChequerasBenef(noPersona, idBanco);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:EnvioTransferenciasAction, M:obtenerChequerasBenef");
		}
		return list;
	}
	
	@DirectMethod
	public String actualizaMovtoCheqBenef(String noFactura, int noEmpresa, int noCliente, int noFolioMov, int idBanco, String chequera) {
		if(!Utilerias.haveSession(WebContextManager.get()) && !Utilerias.tienePermiso(WebContextManager.get(),38)) 
			return null;
		String resp = "";
		
		try {
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),38)){
			EnvioTransferenciasBusiness envioTransferenciasBusiness = (EnvioTransferenciasBusiness)contexto.obtenerBean("envioTransferenciasBusiness");
			resp = envioTransferenciasBusiness.actualizaMovtoCheqBenef(noFactura, noEmpresa, noCliente, noFolioMov, idBanco, chequera);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:EnvioTransferenciasAction, M:actualizaMovtoCheqBenef");
			return "Error al modificar el registro";
		}
		return resp;
	}
}
