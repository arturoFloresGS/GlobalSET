package com.webset.set.financiamiento.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.financiamiento.dao.GastosFinanciamientoCDao;
import com.webset.set.financiamiento.dto.AmortizacionCreditoDto;
import com.webset.set.financiamiento.dto.GastoComisionCreditoDto;
import com.webset.set.financiamiento.service.GastosFinanciamientoCService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public class GastosFinanciamientoCBusinessImpl implements GastosFinanciamientoCService {
	private Bitacora bitacora = new Bitacora();
	private GastosFinanciamientoCDao gastosFinanciamientoCDao;
	
	public GastosFinanciamientoCDao getGastosFinanciamientoCDao() {
		return gastosFinanciamientoCDao;
	}
	public void setGastosFinanciamientoCDao(GastosFinanciamientoCDao gastosFinanciamientoCDao) {
		this.gastosFinanciamientoCDao = gastosFinanciamientoCDao;
	}
	private ConsultasGenerales consultasGenerales;
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<LlenaComboGralDto> obtenerContratos(String psTipoMenu, int iBanco, int noEmpresa) {
		return gastosFinanciamientoCDao.obtenerContratos( psTipoMenu, iBanco, noEmpresa);
	}
	@Override
	public List<LlenaComboGralDto> obtenerGastos() {
		return gastosFinanciamientoCDao.obtenerGastos();
	}
	@Override
	public List<LlenaComboGralDto> obtenerDisposiciones(String linea, boolean estatus) {
		return gastosFinanciamientoCDao.obtenerDisposiciones(linea,estatus);
	}
	@Override
	public List<GastoComisionCreditoDto> selectGastos(String linea, int disposicion){
		return gastosFinanciamientoCDao.selectGastos(linea,disposicion);
	}
	
	@Override
	public Map<String, Object> insertAmort(String gastos){
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		int resultado=0;
		String vsBisiesto="0";
		Gson gson = new Gson();
		List<Map<String, String>> paramGastos= gson.fromJson(gastos,
				new TypeToken<ArrayList<Map<String, String>>>() {
		}.getType());
		try {
			System.out.println("para"+paramGastos.size());
			for (int i = 0; i < paramGastos.size(); i++) {
				AmortizacionCreditoDto amortizacionCreditoDto = new AmortizacionCreditoDto();
				amortizacionCreditoDto.setIdFinanciamiento(paramGastos.get(i).get("idContrato"));
				amortizacionCreditoDto.setIdDisposicion(Integer.parseInt(paramGastos.get(i).get("idDisposicion")));
				amortizacionCreditoDto.setFecVencimiento(paramGastos.get(i).get("fecPago"));
				amortizacionCreditoDto.setFecPago(paramGastos.get(i).get("fecPago"));
				amortizacionCreditoDto.setGasto(Double.parseDouble(paramGastos.get(i).get("gasto")));
				amortizacionCreditoDto.setComision(Double.parseDouble(paramGastos.get(i).get("comision")));
				amortizacionCreditoDto.setIva(Double.parseDouble(paramGastos.get(i).get("comision")));
				amortizacionCreditoDto.setBancoGastcom(Integer.parseInt(paramGastos.get(i).get("bancoGastcom")));
				amortizacionCreditoDto.setClabeBancariaGastcom(paramGastos.get(i).get("clabeBancariaGastcom"));
				amortizacionCreditoDto.setTipoGasto(Integer.parseInt(paramGastos.get(i).get("tipoGasto")));
				amortizacionCreditoDto.setPorcentaje(Double.parseDouble(paramGastos.get(i).get("porcentaje")));
				amortizacionCreditoDto.setPagar(paramGastos.get(i).get("pagar").charAt(0));
				amortizacionCreditoDto.setCapital(0);
				amortizacionCreditoDto.setInteresA(0);
				amortizacionCreditoDto.setEstatus('A');
				amortizacionCreditoDto.setTasaVigente(0);
				amortizacionCreditoDto.setTasaFija(0);
				amortizacionCreditoDto.setPeriodo("");
				amortizacionCreditoDto.setNoAmortizaciones(0);
				amortizacionCreditoDto.setTasaBase("");
				amortizacionCreditoDto.setPuntos(0);
				amortizacionCreditoDto.setTasa(' ');
				amortizacionCreditoDto.setSaldo(0);
				amortizacionCreditoDto.setValorTasa(0);
				amortizacionCreditoDto.setDiaCortecap(0);
				amortizacionCreditoDto.setDiasPeriodo(0);
				amortizacionCreditoDto.setComentario("");
				amortizacionCreditoDto.setSobreTasacb(0);
				amortizacionCreditoDto.setFactCapital(0);
				amortizacionCreditoDto.setSoloRenta(0);
				amortizacionCreditoDto.setRenta(0);
				amortizacionCreditoDto.setNoFolioAmort(0);
				amortizacionCreditoDto.setIdAmortizacion(gastosFinanciamientoCDao.obtenerIdAmortizacion(amortizacionCreditoDto.getIdFinanciamiento(), amortizacionCreditoDto.getIdDisposicion()));				
				resultado = gastosFinanciamientoCDao.insertAmort(amortizacionCreditoDto,vsBisiesto);
				if(resultado<1){
					mensajes.add("Error al guardar los gastos.");
					break;
				}
			}
			if(resultado>=1)
				mensajes.add("Gasto guardado.");
			mapResult.put("result", resultado);
			mapResult.put("msgUsuario", mensajes);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
	+ "P:Financiamiento, C:AltaFinanciamientoBusinessImpl, M:insertAmortCapitales");
		}
		return mapResult;
	}
	@Override
	public Map<String, Object> eliminarGastos(String gastos){
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		int resultado=0;
		Gson gson = new Gson();
		List<Map<String, String>> paramGastos= gson.fromJson(gastos,
				new TypeToken<ArrayList<Map<String, String>>>() {
		}.getType());
		try {
			for (int i = 0; i < paramGastos.size(); i++) {
				AmortizacionCreditoDto amortizacionCreditoDto = new AmortizacionCreditoDto();
				amortizacionCreditoDto.setIdFinanciamiento(paramGastos.get(i).get("idContrato"));
				amortizacionCreditoDto.setIdDisposicion(Integer.parseInt(paramGastos.get(i).get("idDisposicion")));
				amortizacionCreditoDto.setIdAmortizacion(Integer.parseInt(paramGastos.get(i).get("idAmortizacion")));
				resultado = gastosFinanciamientoCDao.eliminarGastos(amortizacionCreditoDto);
				if(resultado<1){
					mensajes.add("Error al eliminar los gastos.");
					break;
				}
			}
			if(resultado>=1)
				mensajes.add("Registro(s) eliminado(s).");
			mapResult.put("msgUsuario", mensajes);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
	+ "P:Financiamiento, C:AltaFinanciamientoBusinessImpl, M:insertAmortCapitales");
		}
		return mapResult;
	}
	@Override
	public List<Map<String, Object>> obtenerReporteGastos(String idLinea,int idDisposicion) {
		return this.gastosFinanciamientoCDao.obtenerReporteGastos(idLinea,idDisposicion);
	}

	
}
