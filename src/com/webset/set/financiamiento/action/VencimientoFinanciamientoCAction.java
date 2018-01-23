package com.webset.set.financiamiento.action;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.webset.set.financiamiento.dto.AmortizacionCreditoDto;
import com.webset.set.financiamiento.service.VencimientoFinanciamientoCService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public class VencimientoFinanciamientoCAction {

	Bitacora bitacora = new Bitacora();
	Contexto contexto = new Contexto();
	Funciones funciones = new Funciones();
	VencimientoFinanciamientoCService vencimientoFinanciamientoCService;

	@DirectMethod
	public List<LlenaComboGralDto> obtenerPaisVenc() {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			//	if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			vencimientoFinanciamientoCService = (VencimientoFinanciamientoCService) contexto
					.obtenerBean("vencimientoFinanciamientoCBusinessImpl");
			list = vencimientoFinanciamientoCService.obtenerPaisVenc();
			//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCAction, M:obtenerPaisVenc");
		}
		return list;
	}
	@DirectMethod
	public List<LlenaComboGralDto> obtenerEmpresas() {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			//	if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			vencimientoFinanciamientoCService = (VencimientoFinanciamientoCService) contexto
					.obtenerBean("vencimientoFinanciamientoCBusinessImpl");
			list = vencimientoFinanciamientoCService.obtenerEmpresas();
			//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCAction, M:obtenerPaisVenc");
		}
		return list;
	}
	@DirectMethod
	public List<LlenaComboGralDto> obtenerContratos(int piEmpresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			//	if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			vencimientoFinanciamientoCService = (VencimientoFinanciamientoCService) contexto
					.obtenerBean("vencimientoFinanciamientoCBusinessImpl");
			list = vencimientoFinanciamientoCService.obtenerContratos(piEmpresa);
			//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCAction, M:obtenerContratos");
		}
		return list;
	}
	@DirectMethod
	public List<LlenaComboGralDto> obtenerDivisas(int noEmpresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			//	if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			vencimientoFinanciamientoCService = (VencimientoFinanciamientoCService) contexto
					.obtenerBean("vencimientoFinanciamientoCBusinessImpl");
			list = vencimientoFinanciamientoCService.obtenerDivisas( noEmpresa);
			//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCAction, M:obtenerPaisVenc");
		}
		return list;
	}
	@DirectMethod
	public List<AmortizacionCreditoDto> selectMovimientoAzt(String psFecIni,String psPais,
			int plEmpresa,int piBanco,String psLinea,int piTipoFinan,String psDivisa,int plCredito) {
		List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>();
		try {
			//	if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			vencimientoFinanciamientoCService = (VencimientoFinanciamientoCService) contexto
					.obtenerBean("vencimientoFinanciamientoCBusinessImpl");
			list = vencimientoFinanciamientoCService.selectMovimientoAzt( psFecIni, psPais,
					plEmpresa, piBanco, psLinea, piTipoFinan, psDivisa, plCredito);
			//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCAction, M:obtenerPaisVenc");
		}
		return list;
	}
	@DirectMethod
	public List<LlenaComboGralDto> obtenerBancoVenci(String psNac, String psTipoMenu) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			//	if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			vencimientoFinanciamientoCService = (VencimientoFinanciamientoCService) contexto
					.obtenerBean("vencimientoFinanciamientoCBusinessImpl");
			list = vencimientoFinanciamientoCService.obtenerBancoVenci(psNac,psTipoMenu);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCAction, M:obtenerBancoVenci");
		}
		return list;
	}
	@DirectMethod
	public List<LlenaComboGralDto> obtenerBancoPago(int piEmpresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			//if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			vencimientoFinanciamientoCService = (VencimientoFinanciamientoCService) contexto
					.obtenerBean("vencimientoFinanciamientoCBusinessImpl");
			list = vencimientoFinanciamientoCService.obtenerBancoPago(piEmpresa);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCAction, M:obtenerBancoPago");
		}
		return list;
	}
	@DirectMethod
	public List<AmortizacionCreditoDto> storeSelectCapital(String psLinea,int piDisposicion) {
		List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>();
		try {
			//	if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			vencimientoFinanciamientoCService = (VencimientoFinanciamientoCService) contexto
					.obtenerBean("vencimientoFinanciamientoCBusinessImpl");
			list = vencimientoFinanciamientoCService.storeSelectCapital(psLinea,piDisposicion);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCAction, M:obtenerBancoPago");
		}
		return list;
	}
	@DirectMethod
	public List<AmortizacionCreditoDto> selectPrimerAmortAct(String psLinea,int piDisposicion){
		List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>();
		try {
			//if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			vencimientoFinanciamientoCService = (VencimientoFinanciamientoCService) contexto
					.obtenerBean("vencimientoFinanciamientoCBusinessImpl");
			list = vencimientoFinanciamientoCService.selectPrimerAmortAct(psLinea,piDisposicion);
			//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCAction, M:selectPrimerAmortAct");
		}
		return list;
	}
	@DirectMethod
	public Map<String, Object> pagoAnticipadoParcial(String linea, int disposicion, String fecInicio,String fechaVto,
			double importePago, int amortizacion,boolean primeras, int noEmpresa,double saldoPago, String fechaVencimiento,String fecHoy) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			//	if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			vencimientoFinanciamientoCService = (VencimientoFinanciamientoCService) contexto
					.obtenerBean("vencimientoFinanciamientoCBusinessImpl");
			AmortizacionCreditoDto dto=new AmortizacionCreditoDto();
			dto.setIdFinanciamiento(linea);
			dto.setIdDisposicion(disposicion);
			dto.setFecInicio(fecInicio);
			dto.setFecVencimiento(fechaVto);
			dto.setImporte(importePago);
			dto.setIdAmortizacion(amortizacion);
			dto.setPrimeras(primeras);

			if((saldoPago==importePago)&&(funciones.ponerFechaDate(fechaVencimiento).compareTo(funciones.ponerFechaDate(fecHoy))==0))
				mapResult = vencimientoFinanciamientoCService.pagoAnticipadoTotal(dto, noEmpresa);
			else
				mapResult = vencimientoFinanciamientoCService.pagoAnticipadoParcial(dto, noEmpresa);
			//	}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCAction, M:pagoAnticipadoParcial");
		}
		return mapResult;
	}
	@DirectMethod
	public Map<String, Object> pagoAmortizaciones(String amortizaciones, String txtDivisaPag,int txtBancoPag,String cmbChequeraPag,double txtTipoCambio) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		Gson gson = new Gson();
		List<Map<String, String>> paramAmortizaciones= gson.fromJson(amortizaciones,
				new TypeToken<ArrayList<Map<String, String>>>() {
		}.getType());

		try {
			//		if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			vencimientoFinanciamientoCService = (VencimientoFinanciamientoCService) contexto
					.obtenerBean("vencimientoFinanciamientoCBusinessImpl");
			System.out.println(amortizaciones);
			String separadas="[";
			String agrupadas="[";
			String fecha1="",fecha2="", contrato1="",contrato2="", amortizacion1="",amortizacion2="", disposicion1="",disposicion2="";
			for (int i = 0; i < paramAmortizaciones.size(); i++) {
				if (paramAmortizaciones.get(i).get("checked").equals("true")
						&& !paramAmortizaciones.get(i).get("valorPago").equals("-")) {

					if(i<=paramAmortizaciones.size()-1){
						fecha1=funciones.ponerFechaSola(paramAmortizaciones.get(i).get("fecVen"));
						fecha2=funciones.ponerFechaSola(paramAmortizaciones.get(i+1).get("fecVen"));
						contrato1=paramAmortizaciones.get(i).get("idContrato");
						contrato2=paramAmortizaciones.get(i+1).get("idContrato");
						disposicion1=paramAmortizaciones.get(i).get("idDisposicion");
						disposicion2=paramAmortizaciones.get(i+1).get("idDisposicion");
						amortizacion1=paramAmortizaciones.get(i).get("idAmortizacion");
						amortizacion2=paramAmortizaciones.get(i+1).get("idAmortizacion");
					}
					else{
						fecha1="";
						fecha2="";
						contrato1="";
						contrato2="";
						disposicion1="";
						disposicion2="";
						amortizacion1="";
						amortizacion2="";
					}

					if(fecha1.compareTo(funciones.ponerFechaSola(fecha2))==0&&
							contrato1.compareTo(contrato2)==0&&
							disposicion1.compareTo(disposicion2)==0&&
							amortizacion1.equals("0")&&!amortizacion2.equals("0")){
						//Amortización de interés
						agrupadas+="{\"fecVen\":\""+fecha1+"\",";
						agrupadas+="\"idContrato\":\""+contrato1+"\",";
						agrupadas+="\"idDisposicion\":"+disposicion1+",";
						agrupadas+="\"idAmortizacion\":"+amortizacion1+",";
						agrupadas+="\"capital\":"+paramAmortizaciones.get(i).get("capital")+",";
						agrupadas+="\"interes\":"+paramAmortizaciones.get(i).get("interes")+",";
						agrupadas+="\"renta\":"+paramAmortizaciones.get(i).get("renta")+",";
						agrupadas+="\"iva\":"+paramAmortizaciones.get(i).get("iva")+",";
						agrupadas+="\"empresa\":"+paramAmortizaciones.get(i).get("empresa")+",";
						agrupadas+="\"descEmpresa\":\""+paramAmortizaciones.get(i).get("descEmpresa")+"\",";
						agrupadas+="\"institucion\":"+paramAmortizaciones.get(i).get("institucion")+",";
						agrupadas+="\"descInstitucion\":\""+paramAmortizaciones.get(i).get("descInstitucion")+"\",";
						agrupadas+="\"saldo\":"+paramAmortizaciones.get(i).get("saldo")+",";
						agrupadas+="\"formaPago\":"+paramAmortizaciones.get(i).get("formaPago")+",";
						agrupadas+="\"equivalente\":\""+paramAmortizaciones.get(i).get("equivalente")+"\",";
						agrupadas+="\"gpoEmpresa\":"+paramAmortizaciones.get(i).get("gpoEmpresa")+",";
						agrupadas+="\"descPago\":\""+paramAmortizaciones.get(i).get("descPago")+"\",";
						agrupadas+="\"valorPago\":\""+paramAmortizaciones.get(i).get("valorPago")+"\",";
						agrupadas+="\"bancoBenef\":"+paramAmortizaciones.get(i).get("bancoBenef")+",";
						agrupadas+="\"chequeraBenef\":\""+paramAmortizaciones.get(i).get("chequeraBenef")+"\",";
						agrupadas+="\"divisa\":\""+paramAmortizaciones.get(i).get("divisa")+"\",";
						agrupadas+="\"fecIni\":\""+paramAmortizaciones.get(i).get("fecIni")+"\",";
						agrupadas+="\"totalPago\":"+paramAmortizaciones.get(i).get("totalPago")+",";
						agrupadas+="\"checked\":"+paramAmortizaciones.get(i).get("checked")+"},";
						//Amortización de Capital
						agrupadas+="{\"fecVen\":\""+fecha2+"\",";
						agrupadas+="\"idContrato\":\""+contrato2+"\",";
						agrupadas+="\"idDisposicion\":"+disposicion2+",";
						agrupadas+="\"idAmortizacion\":"+amortizacion2+",";
						agrupadas+="\"capital\":"+paramAmortizaciones.get(i+1).get("capital")+",";
						agrupadas+="\"interes\":"+paramAmortizaciones.get(i+1).get("interes")+",";
						agrupadas+="\"renta\":"+paramAmortizaciones.get(i+1).get("renta")+",";
						agrupadas+="\"iva\":"+paramAmortizaciones.get(i+1).get("iva")+",";
						agrupadas+="\"empresa\":"+paramAmortizaciones.get(i+1).get("empresa")+",";
						agrupadas+="\"descEmpresa\":\""+paramAmortizaciones.get(i+1).get("descEmpresa")+"\",";
						agrupadas+="\"institucion\":"+paramAmortizaciones.get(i+1).get("institucion")+",";
						agrupadas+="\"descInstitucion\":\""+paramAmortizaciones.get(i+1).get("descInstitucion")+"\",";
						agrupadas+="\"saldo\":"+paramAmortizaciones.get(i+1).get("saldo")+",";
						agrupadas+="\"formaPago\":"+paramAmortizaciones.get(i+1).get("formaPago")+",";
						agrupadas+="\"equivalente\":\""+paramAmortizaciones.get(i+1).get("equivalente")+"\",";
						agrupadas+="\"gpoEmpresa\":"+paramAmortizaciones.get(i+1).get("gpoEmpresa")+",";
						agrupadas+="\"descPago\":\""+paramAmortizaciones.get(i+1).get("descPago")+"\",";
						agrupadas+="\"valorPago\":\""+paramAmortizaciones.get(i+1).get("valorPago")+"\",";
						agrupadas+="\"bancoBenef\":"+paramAmortizaciones.get(i+1).get("bancoBenef")+",";
						agrupadas+="\"chequeraBenef\":\""+paramAmortizaciones.get(i).get("chequeraBenef")+"\",";
						agrupadas+="\"divisa\":\""+paramAmortizaciones.get(i+1).get("divisa")+"\",";
						agrupadas+="\"fecIni\":\""+paramAmortizaciones.get(i+1).get("fecIni")+"\",";
						agrupadas+="\"totalPago\":"+paramAmortizaciones.get(i+1).get("totalPago")+",";
						agrupadas+="\"checked\":"+paramAmortizaciones.get(i+1).get("checked")+"},";
						
						i=i+1;
					}else{
						separadas+="{\"fecVen\":\""+fecha1+"\",";
						separadas+="\"idContrato\":\""+contrato1+"\",";
						separadas+="\"idDisposicion\":"+disposicion1+",";
						separadas+="\"idAmortizacion\":"+amortizacion1+",";
						separadas+="\"capital\":"+paramAmortizaciones.get(i).get("capital")+",";
						separadas+="\"interes\":"+paramAmortizaciones.get(i).get("interes")+",";
						separadas+="\"renta\":"+paramAmortizaciones.get(i).get("renta")+",";
						separadas+="\"iva\":"+paramAmortizaciones.get(i).get("iva")+",";
						separadas+="\"empresa\":"+paramAmortizaciones.get(i).get("empresa")+",";
						separadas+="\"descEmpresa\":\""+paramAmortizaciones.get(i).get("descEmpresa")+"\",";
						separadas+="\"institucion\":"+paramAmortizaciones.get(i).get("institucion")+",";
						separadas+="\"descInstitucion\":\""+paramAmortizaciones.get(i).get("descInstitucion")+"\",";
						separadas+="\"saldo\":"+paramAmortizaciones.get(i).get("saldo")+",";
						separadas+="\"formaPago\":"+paramAmortizaciones.get(i).get("formaPago")+",";
						separadas+="\"equivalente\":\""+paramAmortizaciones.get(i).get("equivalente")+"\",";
						separadas+="\"gpoEmpresa\":"+paramAmortizaciones.get(i).get("gpoEmpresa")+",";
						separadas+="\"descPago\":\""+paramAmortizaciones.get(i).get("descPago")+"\",";
						separadas+="\"valorPago\":\""+paramAmortizaciones.get(i).get("valorPago")+"\",";
						separadas+="\"bancoBenef\":"+paramAmortizaciones.get(i).get("bancoBenef")+",";
						separadas+="\"chequeraBenef\":\""+paramAmortizaciones.get(i).get("chequeraBenef")+"\",";
						separadas+="\"divisa\":\""+paramAmortizaciones.get(i).get("divisa")+"\",";
						separadas+="\"fecIni\":\""+paramAmortizaciones.get(i).get("fecIni")+"\",";
						separadas+="\"totalPago\":"+paramAmortizaciones.get(i).get("totalPago")+",";
						separadas+="\"checked\":"+paramAmortizaciones.get(i).get("checked")+"},";

					}

				}
			}
			if(!agrupadas.equals("["))
				agrupadas = agrupadas.substring(0, agrupadas.length()-1); 
			if(!separadas.equals("["))
				separadas = separadas.substring(0, separadas.length()-1); 
			separadas+="]";
			agrupadas+="]";
			if(!separadas.equals("[]")&&agrupadas.equals("[]"))
				mapResult = vencimientoFinanciamientoCService.pagoAmortizaciones(separadas,txtDivisaPag,txtBancoPag,cmbChequeraPag,txtTipoCambio);
			else if(!agrupadas.equals("[]")&&separadas.equals("[]"))
				mapResult = vencimientoFinanciamientoCService.pagoAmortizacionesAgrupadas(agrupadas,txtDivisaPag,txtBancoPag,cmbChequeraPag,txtTipoCambio);
			else 
				mapResult = vencimientoFinanciamientoCService.pagoAmortizacionesMezcla(amortizaciones,agrupadas, separadas,txtDivisaPag,txtBancoPag,cmbChequeraPag,txtTipoCambio);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCAction, M:pagoAmortizaciones");
		}
		return mapResult;
	}
	public List<Map<String, Object>> obtenerReporteVencimientos(String amortizaciones,String fecha,String divisa,String empresa, ServletContext context) {
		List<Map<String, Object>> lista =new ArrayList<Map<String, Object>>();
		Gson gson = new Gson();
		DecimalFormat formato= new DecimalFormat("###,###.##");
		List<Map<String, String>> paramAmortizaciones= gson.fromJson(amortizaciones,
				new TypeToken<ArrayList<Map<String, String>>>() {
		}.getType());
		//if (contexto == null) {
		//bitacora.insertarRegistro(
		//	"P:Financiamiento, C:VencimientoFinanciamientoCAction, M:obtenerReporteVencimientos" + " contexto nulo");
		//	return null;
		//}
		try {
			for (int i = 0; i < paramAmortizaciones.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("fecVen",paramAmortizaciones.get(i).get("fecVen"));
				map.put("idContrato",paramAmortizaciones.get(i).get("idContrato"));
				map.put("idDisposicion",paramAmortizaciones.get(i).get("idDisposicion"));
				map.put("idAmortizacion",paramAmortizaciones.get(i).get("idAmortizacion"));
				map.put("capital",formato.format(Double.parseDouble(paramAmortizaciones.get(i).get("capital"))));
				map.put("interes",formato.format(Double.parseDouble(paramAmortizaciones.get(i).get("interes"))));
				map.put("iva",paramAmortizaciones.get(i).get("iva"));
				map.put("descEmpresa",paramAmortizaciones.get(i).get("descEmpresa"));
				map.put("descInstitucion",paramAmortizaciones.get(i).get("descInstitucion"));
				map.put("totalPago",formato.format(Double.parseDouble(paramAmortizaciones.get(i).get("totalPago"))));
				map.put("fecha",fecha);
				map.put("divisa",divisa);
				map.put("empresa",empresa);
				lista.add(map);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCAction, M:obtenerReporteVencimientos");
		}
		return lista;
	}
	/*@DirectMethod
	public void correo() {

		try {
	//		if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				vencimientoFinanciamientoCService = (VencimientoFinanciamientoCService) contexto
						.obtenerBean("vencimientoFinanciamientoCBusinessImpl");
				vencimientoFinanciamientoCService.enviarCorreoVencimientos();
		//	}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:VencimientoFinanciamientoCAction, M:pagoAmortizaciones");
		}
	}*/
}