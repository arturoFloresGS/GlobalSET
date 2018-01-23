package com.webset.set.financiamiento.business;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.coinversion.dto.DivisasEncontradasDto;
import com.webset.set.financiamiento.action.AltaFinanciamientoAction;
import com.webset.set.financiamiento.dao.FinanciamientoModificacionCDao;
import com.webset.set.financiamiento.service.FinanciamientoModificacionCService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.financiamiento.dto.AmortizacionCreditoDto;
import com.webset.set.financiamiento.dto.Amortizaciones;
import com.webset.set.financiamiento.dto.ContratoCreditoDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public class FinanciamientoModificacionCBusinessImpl implements FinanciamientoModificacionCService {

	private FinanciamientoModificacionCDao financiamientoModificacionCDao;
	Funciones funciones= new Funciones();
	Bitacora bitacora = new Bitacora();
	private GlobalSingleton globalSingleton;
	boolean bFecha=false,bTodos=false;
	String vsTipoMenu="";
	private AltaFinanciamientoAction altaFinanciamientoAction;
	public FinanciamientoModificacionCDao getFinanciamientoModificacionCDao() {
		return financiamientoModificacionCDao;
	}
	public void setFinanciamientoModificacionCDao(FinanciamientoModificacionCDao financiamientoModificacionCDao) {
		this.financiamientoModificacionCDao = financiamientoModificacionCDao;
	}
	public List<AmortizacionCreditoDto> selectAmortizaciones(String psIdContrato, int piDisposicion,boolean pbCambioTasa ,String psTipoMenu ,String psProyecto ,int piCapital) {
		List<AmortizacionCreditoDto> list=new ArrayList<AmortizacionCreditoDto>();
		list=financiamientoModificacionCDao.selectAmortizaciones(psIdContrato, piDisposicion,pbCambioTasa ,psTipoMenu ,psProyecto , piCapital);
		List<AmortizacionCreditoDto> listAux=new ArrayList<AmortizacionCreditoDto>();
		String psTasa="";
		if(!list.isEmpty()){
			for (int i = 0; i < list.size(); i++) {
				if(!list.get(i).getTasaBase().equals("")){
					listAux=financiamientoModificacionCDao.selectNombreTasa(list.get(i).getTasaBase().trim());
					if(!listAux.isEmpty())
						psTasa=listAux.get(0).getDescTasa();
					else
						psTasa="La tasa no está registrada";
				}
				else
					psTasa="";
				list.get(i).setTasaBase(psTasa);
			}
		}
		return list;
	}
	@Override
	public List<LlenaComboGralDto> obtenerContratos(int noEmpresa) {
		return financiamientoModificacionCDao.obtenerContratos(noEmpresa);
	}
	@Override
	public List<LlenaComboGralDto> obtenerEmpresas(int idUsuario, boolean bMantenimiento) {
		return financiamientoModificacionCDao.obtenerEmpresas(idUsuario,bMantenimiento);
	}
	@Override
	public List<LlenaComboGralDto> obtenerDisposiciones(String linea, boolean estatus) {
		return financiamientoModificacionCDao.obtenerDisposiciones(linea,estatus);
	}

	@Override
	public List<LlenaComboGralDto> llenarCmbTasa() {
		List<LlenaComboGralDto> listDisp = new ArrayList<LlenaComboGralDto>();
		try{
			listDisp = financiamientoModificacionCDao.consultarTasa();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: Financiamiento C: FinanciamientoModificacionCBusinessImpl M: llenarCmbTasa");
		}
		return listDisp;
	}
	@Override
	public List<ContratoCreditoDto> funSQLTasa(String psTasa) {
		return financiamientoModificacionCDao.funSQLTasa(psTasa);
	}

	@Override
	public Map<String, Object> modificar(String contrato, int disposicion,int optTasa, String cmbTasaBase,String txtValTasa,String txtPuntos,String txtTasaVig, String txtTasaFij,String txtFecCor, boolean chkCapital,
			String txtRenta, String txtIva, String jsonGrid) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		String vsLinea="",vsTipoTasa="",vsIdTasaBasa="";
		Gson gson = new Gson();
		List<Map<String, String>> paramAmort = gson.fromJson(jsonGrid,
				new TypeToken<ArrayList<Map<String, String>>>() {
		}.getType());
		
		DecimalFormat formato = new DecimalFormat("##0.00000");
		String vdPuntos="0",vdBase="0",vdValorTasa="0",vdValTasaVig="0",vdValTasaFij="0";
		int vlDisp=0, respuesta=0;
		try {
			vsLinea = contrato.trim();
			vlDisp = disposicion;
			if(optTasa==1){
				vsTipoTasa = "V";
				if(!cmbTasaBase.equals(""))
					vsIdTasaBasa=cmbTasaBase;
				vdBase = formato.format(Double.parseDouble(txtValTasa));
				vdPuntos = formato.format(Double.parseDouble(txtPuntos));
				vdValorTasa = formato.format(Double.parseDouble(txtTasaVig));
				vdValTasaVig = vdValorTasa;
			}else if(optTasa==0){
				vsTipoTasa = "F";
				vdValorTasa =formato.format(Double.parseDouble(txtTasaFij)); 
				vdValTasaFij = vdValorTasa;
				vdPuntos = formato.format(Double.parseDouble(txtPuntos));
			}
			int result = 0;
			result = financiamientoModificacionCDao.updTasaVariableDisp(vsLinea, vlDisp, vsTipoTasa, vsIdTasaBasa, vdValorTasa, vdPuntos);
			if (result <= 0) {
				mensajes.add("No se actualizó correctamente la disposición.");
				mapResult.put("msgUsuario", mensajes);
				mapResult.put("result", result);
				return mapResult;
			} 
			for (int i = 0; i < paramAmort.size(); i++) {
				if(Integer.parseInt(paramAmort.get(i).get("idAmortizacion"))==0){
				respuesta=segundo(paramAmort,txtFecCor,i,Double.parseDouble(txtTasaVig),chkCapital,vsLinea,vlDisp,vdValTasaVig,vdValTasaFij,
						vsIdTasaBasa, vdBase,vdPuntos,vsTipoTasa,txtRenta, txtIva,paramAmort.size());
				if(respuesta==0){
					mensajes.add("Error al actualizar las amortizaciones.");
					mapResult.put("msgUsuario", mensajes);
					mapResult.put("result", 0);
					return mapResult;
				}else if(respuesta==-1){
					mensajes.add("La fecha solo se puede modificar para el primer registro.");
					mapResult.put("msgUsuario", mensajes);
					mapResult.put("result", 0);
				return mapResult;
				}
				}
			}
			mensajes.add("ok");
			mapResult.put("msgUsuario", mensajes);
			mapResult.put("result", respuesta);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento,  C: FinanciamientoModificacionCBusinessImpl, M:modificar");
		}
		return mapResult;
	}
	//SEGUNDO:
	public int segundo(List<Map<String, String>> paramAmort,String txtFecCor,int i, double vdValorTasa,boolean chkCapital,String contrato, int disposicion, 
			String vdValTasaVig, String vdValTasaFij, String vsIdTasaBasa, String vdBase, String vdPuntos, String vsTipoTasa, 
			String txtRenta, String txtIva, int totReg){
		double vdInteres=0,vdSaldoInsoluto=0,vlDiasDif=0;
		int resp=0;
		Date vdFecVen,vdFecCorte,vdFecIni;
		boolean vbRenta=false,bFecha=false;
		try {
			System.out.println("entra segundo");
			vdInteres = Double.parseDouble(paramAmort.get(i).get("interes"));
			System.out.println(vdInteres);
			vdSaldoInsoluto = Double.parseDouble(paramAmort.get(i).get("saldoInsoluto"));
			System.out.println(vdSaldoInsoluto);
			vdFecVen = funciones.ponerFechaDate(paramAmort.get(i).get("fecVencimiento"));
			System.out.println(vdFecVen);
			vdFecCorte = funciones.ponerFechaDate(paramAmort.get(i).get("fecVencimiento"));
			System.out.println(vdFecCorte);
			vdFecIni = funciones.ponerFechaDate(paramAmort.get(i).get("fecInicio"));
			System.out.println(vdFecIni);
			vbRenta = false;
			if(bFecha)
				vdFecIni = funciones.ponerFechaDate(txtFecCor);
			if (vsTipoMenu.equals("A"))
				vbRenta = true;
			if(vdFecVen.compareTo(funciones.ponerFechaDate(txtFecCor))!=0&&i==0){
				bFecha = true;
				 if (bTodos) 
					 return -1;
                 else
				vdFecCorte = funciones.ponerFechaDate(txtFecCor);
			}
			vlDiasDif =funciones.diasEntreFechas(vdFecIni,vdFecCorte);
			vdInteres = (((vdValorTasa / 100 / 360) * vdSaldoInsoluto) * vlDiasDif);
			if(!chkCapital){
				resp =financiamientoModificacionCDao.updateAmortizacion(contrato, disposicion, 0, 0,vdInteres, vdValTasaVig, 
						vdValTasaFij, vsIdTasaBasa, vdBase, vdPuntos, vsTipoTasa, vdFecCorte,
						"", vbRenta, txtRenta, txtIva, vdFecVen, bFecha, 
						vdFecIni);
				if(resp<=0)
					return 0;
				if (vsTipoMenu.equals("A"))
					resp =financiamientoModificacionCDao.updateAmortizacion(contrato, disposicion, 1, (Double.parseDouble(txtRenta)- vdInteres), 0.00, 
							vdValTasaVig, vdValTasaFij, vsIdTasaBasa, vdBase, vdPuntos, 
							vsTipoTasa, vdFecCorte, "", vbRenta,txtRenta,txtIva,
							vdFecVen, false, null);
				if(resp<=0)
					return 0;
			}
			if(totReg > 1&& bFecha &&i == 0){
				//i+=1;
                segundo(paramAmort,txtFecCor,i,vdValorTasa,chkCapital,contrato,disposicion,vdValTasaVig,vdValTasaFij,
						vsIdTasaBasa, vdBase,vdPuntos,vsTipoTasa,txtRenta, txtIva,paramAmort.size());
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento,  C: FinanciamientoModificacionCBusinessImpl, M:segundo");
		}
		return 1;
	}
	@Override
	public Map<String, Object> modificaProvision(String contrato, int disposicion, int noEmpresa) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		int lFolio=0, result=0;
		altaFinanciamientoAction=new AltaFinanciamientoAction();
		try {
			lFolio = financiamientoModificacionCDao.obtieneFolioAmort(contrato, disposicion);	
			if(lFolio!=0){
				result=altaFinanciamientoAction.insertaProvision(lFolio, contrato, disposicion, noEmpresa);
				if(result>0){
					mensajes.add("Provisiones actualizadas.");
					mapResult.put("msgUsuario", mensajes);
					return mapResult;
				}else{
					mensajes.add("No se han grabado las amortizaciones.");
					mapResult.put("msgUsuario", mensajes);
				}
			}
			else{
				mensajes.add("No se han grabado las amortizaciones.");
				mapResult.put("msgUsuario", mensajes);
				return mapResult;
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
	+ "P:Financiamiento, C: FinanciamientoModificacionCBusinessImpl, M:modificaProvision");
		}
		return mapResult;
	}

}
