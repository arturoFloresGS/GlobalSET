package com.webset.set.financiamiento.business;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.financiamiento.dao.ProvisionInteresesCDao;
import com.webset.set.financiamiento.dto.ProvisionCreditoDTO;
import com.webset.set.financiamiento.service.ProvisionInteresesCService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public class ProvisionInteresesCBusinessImpl implements ProvisionInteresesCService {
	private Bitacora bitacora = new Bitacora();
	private ProvisionInteresesCDao provisionInteresesCDao;
	private Funciones funciones=new Funciones();

	public Bitacora getBitacora() {
		return bitacora;
	}

	public void setBitacora(Bitacora bitacora) {
		this.bitacora = bitacora;
	}


	public ProvisionInteresesCDao getProvisionInteresesCDao() {
		return provisionInteresesCDao;
	}

	public void setProvisionInteresesCDao(ProvisionInteresesCDao provisionInteresesCDao) {
		this.provisionInteresesCDao = provisionInteresesCDao;
	}

	@Override
	public List<LlenaComboGralDto> llenarCmbEmpresa(int piNoUsuario, boolean pbMismaEmpresa, int plEmpresa) {
		return provisionInteresesCDao.llenarCmbEmpresa(piNoUsuario, pbMismaEmpresa,plEmpresa);
	}
	//Se agregó el parámetro tipo para indicar a que consulta le corresponde.
	//1.FunSQLSelectGeneraProvision
	//2.FunSQLSelectProvisionHistorico
	//3.FunSQLSelectProvisionMesHoy
	//4.FunSQLSelectProvisionHistorico
	@Override
	public List<ProvisionCreditoDTO> llenarGridProvisiones(String psFecha, int plEmpresa, String psFechaIni,
			String psTipoFuncion, String psDivisa, int tipo) {
		List<ProvisionCreditoDTO> lista=new ArrayList<ProvisionCreditoDTO>();
		if(tipo==4||tipo==2)
			lista= provisionInteresesCDao.selectProvisionHistorico(psFecha,plEmpresa,psFechaIni,psTipoFuncion,psDivisa);
		if(tipo==3)
			lista= provisionInteresesCDao.selectProvisionMesHoy(psFecha,plEmpresa,psFechaIni,psDivisa);
		if(tipo==1)
			lista= provisionInteresesCDao.selectGeneraProvision(psFecha,plEmpresa,psDivisa);
		return lista;
	}
	@Override
	public Map<String, Object> diaHabilReg(String fecha) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List <ProvisionCreditoDTO> result=new ArrayList<ProvisionCreditoDTO>();
		String diaHabil="";
		System.out.println("entra"+fecha);
		Calendar calendar;
		try {
			while (diaHabil.equals("")){
				result = provisionInteresesCDao.selectInhabil(fecha);
				if(result.isEmpty()){
					diaHabil=fecha;
				}
				else{
					calendar=Calendar.getInstance();
					calendar.setTime(funciones.ponerFechaDate(fecha)); 
					calendar.add(Calendar.DAY_OF_YEAR, -1);
					fecha=funciones.ponerFecha(calendar.getTime());
					diaHabil="";
				}
			}
			mapResult.put("fecha", diaHabil);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:ProvisionInteresesCBusinessImpl, M:diaHabilReg");
		}
		return mapResult;
	}
	@Override
	public Map<String, Object> updateProvisionEstatus(String provisiones) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		Gson gson = new Gson();
		int afectados=0;
		List<Map<String, String>> paramProvisiones = gson.fromJson(provisiones,
				new TypeToken<ArrayList<Map<String, String>>>() {
		}.getType());
		List<String> mensajes = new ArrayList<String>();
		try {
			int result = 0;
			for (int i = 0; i < paramProvisiones.size(); i++) {
				ProvisionCreditoDTO dto=new ProvisionCreditoDTO();
				dto.setIdFinanciamiento(paramProvisiones.get(i).get("idFinanciamiento"));
				dto.setIdDisposicion(Integer.parseInt(paramProvisiones.get(i).get("idDisposicion")));
				dto.setFecInicial(paramProvisiones.get(i).get("fecInicial"));
				dto.setFecFinal(paramProvisiones.get(i).get("fecFinal"));
				result = provisionInteresesCDao.updateProvisionEstatus(dto);
				if (result >0) {
					afectados+=1;
				}else{
					mensajes.add("Error al intentar registrar la provisión.");
					mensajes.add("Provisiones registradas: "+afectados+" de "+(paramProvisiones.size())+" seleccionadas.");
					mapResult.put("msgUsuario", mensajes);
					return mapResult;
				}
			}
			if(afectados==paramProvisiones.size()){
				mensajes.add("Provisiones registradas exitosamente.");
				mapResult.put("msgUsuario", mensajes);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:ProvisionInteresesCBusinessImpl, M:updateProvisionEstatus");
		}
		return mapResult;
	}
	@Override
	public Map<String, Object> updateProvisionX(String provisiones,String fecha) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		Gson gson = new Gson();
		List<Map<String, String>> paramProvisiones = gson.fromJson(provisiones,
				new TypeToken<ArrayList<Map<String, String>>>() {
		}.getType());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaAux=null,fechaProvision=null;
		int afectados=0;
		List<String> mensajes = new ArrayList<String>();
		try {
			int result = 0;
			try {
				fechaAux = sdf.parse(fecha);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			for (int i = 0; i < paramProvisiones.size(); i++) {
				try {
					fechaProvision = sdf.parse(paramProvisiones.get(i).get("fecFinal"));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if(fechaProvision.compareTo(fechaAux)<0){
					ProvisionCreditoDTO dto=new ProvisionCreditoDTO();
					dto.setIdFinanciamiento(paramProvisiones.get(i).get("idFinanciamiento"));
					dto.setIdDisposicion(Integer.parseInt(paramProvisiones.get(i).get("idDisposicion")));
					dto.setFecFinal(paramProvisiones.get(i).get("fecFinal"));
					result = provisionInteresesCDao.updateProvisionX(dto);
					if (result >0) {
						afectados+=1;
					}else{
						//Si ocurre un error cancela la eliminación de las posteriores
						mensajes.add("No se pudo actualizar la provisión seleccionada("+(i+1)+")");
						mensajes.add("Provisiones eliminadas: "+afectados+" de "+(paramProvisiones.size())+" seleccionadas.");
						mapResult.put("msgUsuario", mensajes);
						return mapResult;
					}
				}else{
					mensajes.add("Sólo se pueden cancelar provisiones menores al: "+fecha);
				}
			}
			if(afectados==paramProvisiones.size())
				mensajes.add("Provisiones eliminadas.");
			else if(afectados<paramProvisiones.size())
				mensajes.add("Provisiones eliminadas: "+afectados+" de "+(paramProvisiones.size())+" seleccionadas.");
			mapResult.put("msgUsuario", mensajes);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:ProvisionInteresesCBusinessImpl, M:updateProvisionEstatus");
		}
		return mapResult;
	}

}
