package com.webset.set.egresos.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webset.set.egresos.dao.FactorajeDao;
import com.webset.set.egresos.dto.PagosPendientesDto;
import com.webset.set.egresos.service.FactorajeService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.ComunEgresosDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public class FactorajeBusinessImpl implements FactorajeService {
	private FactorajeDao factorajeDao;
	Bitacora bitacora = new Bitacora();
	GlobalSingleton gb = GlobalSingleton.getInstancia();
	SimpleDateFormat formato = new SimpleDateFormat("ddMMyyyy");
	private PagosEnSAPBusiness pagosEnSAPBusiness;
	
	public PagosEnSAPBusiness getPagosEnSAPBusiness() {
		return pagosEnSAPBusiness;
	}

	public void setPagosEnSAPBusiness(PagosEnSAPBusiness pagosEnSAPBusiness) {
		this.pagosEnSAPBusiness = pagosEnSAPBusiness;
	}

	@Override
	public List<PagosPendientesDto> obtenerListaFactoraje(int empresa, int proveedor, String fechaIni, String fechaFin){
		List<PagosPendientesDto> listFactoraje = new ArrayList<PagosPendientesDto>();
		try {
			listFactoraje = factorajeDao.obtenerListaFactoraje(empresa, proveedor, fechaIni, fechaFin);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:FactorajeBusinessImpl, M:obtenerListaFactoraje");
		}
		
		return listFactoraje;
	}
	
	public List<LlenaComboGralDto>llenarComboBeneficiario(LlenaComboGralDto dto){
		return factorajeDao.llenarComboBeneficiario(dto);	
	}
	
	@Override
	public List<LlenaComboGralDto> obtenerProveedores(String filtro){
		List<LlenaComboGralDto> listProveedores =  new ArrayList<LlenaComboGralDto>();
		try{
			listProveedores = factorajeDao.obtenerProveedores(filtro);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:FactorajeBusinessImpl, M:obtenerProveedores");
		}
		
		
		return listProveedores;
	}
	
	@Override
	public List<LlenaComboGralDto> obtenerIntermediarios(){
		List<LlenaComboGralDto> listIntermediario = new ArrayList<LlenaComboGralDto>();
		
		try {
			listIntermediario = factorajeDao.obtenerIntermediarios();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:FactorajeBusinessImpl, M:obtenerIntermediarios");
		}
		return listIntermediario;
	}
	
	
	
	@Override
	public Map<String, Object> enviarDatos(String json, int noFactorae, String fechaFactoraje) {
		Map<String, Object>mapRetorno = new HashMap<String, Object>();
		mapRetorno.put("msgError", "Error desconocido");
		mapRetorno.put("estatus", false);
		
		try {
			
			mapRetorno = pagosEnSAPBusiness.compensaPropuestasSAP(json, fechaFactoraje, gb.getUsuarioLoginDto().getIdUsuario(),
					false, false,new ArrayList<ComunEgresosDto>(), true, noFactorae);

		} catch (Exception e) {
			//factorajeDao.eliminarZexpFact(json);
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:FactorajeBusinessImpl, M:enviarDatos");
		}
				
		
		return mapRetorno;
	}
	
	
	
	
	/******************************************/
	/********GETTERS AND SETTERS***************/
	/******************************************/
	
	
	public FactorajeDao getFactorajeDao() {
		return factorajeDao;
	}

	public void setFactorajeDao(FactorajeDao factorajeDaro) {
		this.factorajeDao = factorajeDaro;
	}

	
}
