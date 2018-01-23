package com.webset.set.interfaz.business;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis.AxisFault;

import com.webset.set.interfaz.dao.ExportacionPolizasDao;
import com.webset.set.interfaz.service.ExportacionPolizasService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.GrupoEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;

import mx.com.gruposalinas.Poliza.DT_Polizas_OBPolizas;
import mx.com.gruposalinas.Poliza.DT_Polizas_ResponseResponse;
import mx.com.gruposalinas.Poliza.SOS_PolizasBindingStub;
import mx.com.gruposalinas.Poliza.SOS_PolizasServiceLocator;

public class ExportacionPolizasBusiness implements ExportacionPolizasService{

	private ExportacionPolizasDao exportacionPolizasDao;
	private Bitacora bitacora = new Bitacora(); 
	Funciones funciones = new Funciones();
	private GlobalSingleton globalSingleton;
	public List<LlenaComboGralDto> llenarComboGrupoEmpresa(GrupoEmpresasDto dto) {
		List<LlenaComboGralDto> result = new ArrayList<LlenaComboGralDto>();
		try {
			result = exportacionPolizasDao.llenarComboGrupoEmpresa(dto);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Interfaz, C:ExportacionPolizasBusiness, M:llenarComboGrupoEmpresas");
		}
		return result;
	}
	
	public List<MovimientoDto> consultaPolizasExportar(String empresa, String origen, String fecInicio, String fecFin){
		List<MovimientoDto> result = new ArrayList<MovimientoDto>();
		try {
			result = exportacionPolizasDao.consultaPolizasExportar(empresa, origen, fecInicio, fecFin);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Interfaz, C:ExportacionPolizasBusiness, M:consultaPolizasExportar");
		}
		return result;
	}
	
	public Map<String, Object> ejecutarExportacionPolizas (List<MovimientoDto> movimientos){
		Map<String, Object> result = new HashMap<String,Object>();
		Map<String, Object> resultDao = new HashMap<String,Object>();
		result.put("mensaje", "Error desconocido.");
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			resultDao=exportacionPolizasDao.insertaZoperaciones(movimientos, String.valueOf(globalSingleton.getUsuarioLoginDto().getIdUsuario()));
			if (resultDao.get("msgUsuario").equals("")) {
				List<MovimientoDto> movimientosInsertados = new ArrayList<>();
				movimientosInsertados = (List<MovimientoDto>)resultDao.get("movimientosInsertados");
				List<DT_Polizas_OBPolizas> list_dt_Polizas_OBPolizas = new ArrayList<DT_Polizas_OBPolizas>();
				DT_Polizas_OBPolizas dt_Polizas_OBPolizas = new DT_Polizas_OBPolizas();
						
				for (int i = 0; i < movimientosInsertados.size(); i++) {
					dt_Polizas_OBPolizas.setNO_EMPRESA(funciones.ajustarLongitudCampo(
							movimientosInsertados.get(i).getNoEmpresa()+"", 4, "D", "", "0"));
					dt_Polizas_OBPolizas.setID_POLIZA(String.valueOf(movimientosInsertados.get(i).getNoPoliza()));
					dt_Polizas_OBPolizas.setID_BANCO(String.valueOf(movimientosInsertados.get(i).getIdBanco()));
					dt_Polizas_OBPolizas.setIMP_PAGO(String.valueOf(movimientosInsertados.get(i).getImporte()));
					dt_Polizas_OBPolizas.setIMP_USA(String.valueOf(movimientosInsertados.get(i).getImporte()));
					dt_Polizas_OBPolizas.setID_DIVISA(movimientosInsertados.get(i).getIdDivisa());
					dt_Polizas_OBPolizas.setNO_FOLIO_SET(String.valueOf(movimientosInsertados.get(i).getNoFolioDet()));
					dt_Polizas_OBPolizas.setFORMA_PAGO("T");
					dt_Polizas_OBPolizas.setREFERENCIA(movimientosInsertados.get(i).getReferencia());
					dt_Polizas_OBPolizas.setID_DIVISA_ORIGINAL(movimientosInsertados.get(i).getIdDivisaOriginal());
					dt_Polizas_OBPolizas.setTIPO_CAMB(movimientosInsertados.get(i).getCab());
					dt_Polizas_OBPolizas.setDIVISION(movimientosInsertados.get(i).getDivision());
					dt_Polizas_OBPolizas.setID_CHEQUE(movimientosInsertados.get(i).getEquivale());
					dt_Polizas_OBPolizas.setFEC_VALOR(funciones.ponerFechaSola(movimientosInsertados.get(i).getFecValor()));
					dt_Polizas_OBPolizas.setFEC_FACTURA(funciones.ponerFechaSola(movimientosInsertados.get(i).getFecAlta()));
					dt_Polizas_OBPolizas.setCONCEPTO(movimientosInsertados.get(i).getConcepto());
					list_dt_Polizas_OBPolizas.add(dt_Polizas_OBPolizas);
				}
				
				
				if (list_dt_Polizas_OBPolizas != null && !list_dt_Polizas_OBPolizas.isEmpty()) {
					SOS_PolizasServiceLocator service = new SOS_PolizasServiceLocator();
					SOS_PolizasBindingStub sos_PolizasBindingStub = new SOS_PolizasBindingStub(new URL(service.getHTTP_PortAddress()), service);
					sos_PolizasBindingStub.setUsername(exportacionPolizasDao.consultarConfiguaraSet(ConstantesSet.USERNAME_WS_POLIZAS));
					sos_PolizasBindingStub.setPassword(exportacionPolizasDao.consultarConfiguaraSet(ConstantesSet.PASSWORD_WS_POLIZAS));
					DT_Polizas_ResponseResponse[] dt_Polizas_ResponseResponse  = sos_PolizasBindingStub.SOS_Polizas(list_dt_Polizas_OBPolizas.toArray(new DT_Polizas_OBPolizas[list_dt_Polizas_OBPolizas.size()]));
					
					if (dt_Polizas_ResponseResponse != null && dt_Polizas_ResponseResponse.length != 0) {
						result=exportacionPolizasDao.insertaBitacoraPoliza(dt_Polizas_ResponseResponse, movimientosInsertados);
						if(result.get("msgUsuario").equals("")){
							result.put("mensaje","Error al procesar las respuestas.");
							return result;
						}else{
							result.put("mensaje","Se exportaron las siguientes polizas  \n"+result.get("msgUsuario").toString());
							return result;
						}
					}else{
						//elimina todo zoperacione
						exportacionPolizasDao.eliminaPolizasZoperaciones(movimientosInsertados);
						result.put("mensaje","Sin respuesta del Web Service.");
						return result;
					}
				}	
			} else {
				result.put("mensaje"," "+resultDao.get("msgUsuario"));
				return result;
			}
			
		} catch (MalformedURLException e1) {
			result.put("mensaje", "Error al conectarce al web service.");
			bitacora.insertarRegistro(new Date().toString() + " " + e1.toString()
			+"P:Interfaz, C:ExportacionPolizasBusiness, M:ejecutarExportacionPolizas");
			e1.printStackTrace();
		} catch (AxisFault e) {
			result.put("mensaje", "Error al procesar los datos en el web service.");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Interfaz, C:ExportacionPolizasBusiness, M:ejecutarExportacionPolizas");
		
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Interfaz, C:ExportacionPolizasBusiness, M:ejecutarExportacionPolizas");
		}
		return result;
	}
	
	//getters and setters
	public ExportacionPolizasDao getExportacionPolizasDao() {
		return exportacionPolizasDao;
	}

	public void setExportacionPolizasDao(ExportacionPolizasDao exportacionPolizasDao) {
		this.exportacionPolizasDao = exportacionPolizasDao;
	}

}
