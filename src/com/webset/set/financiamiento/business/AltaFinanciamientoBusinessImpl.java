package com.webset.set.financiamiento.business;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.jdbc.core.JdbcTemplate;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.coinversion.dto.DivisasEncontradasDto;
import com.webset.set.financiamiento.action.AltaFinanciamientoAction;
import com.webset.set.financiamiento.dao.AltaFinanciamientoDao;
import com.webset.set.financiamiento.dto.AmortizacionCreditoDto;
import com.webset.set.financiamiento.dto.AvalGarantiaDto;
import com.webset.set.financiamiento.dto.BitacoraCreditoBanDto;
import com.webset.set.financiamiento.dto.ContratoCreditoDto;
import com.webset.set.financiamiento.dto.DisposicionCreditoDto;
import com.webset.set.financiamiento.dto.ObligacionCreditoDto;
import com.webset.set.financiamiento.dto.Parametro;
import com.webset.set.financiamiento.dto.ProvisionCreditoDTO;
import com.webset.set.financiamiento.service.AltaFinanciamientoService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.Retorno;

public class AltaFinanciamientoBusinessImpl implements AltaFinanciamientoService {
	private Bitacora bitacora = new Bitacora();
	private AltaFinanciamientoDao altaFinanciamientoDao;

	private ConsultasGenerales consultasGenerales;
	private JdbcTemplate jdbcTemplate;

	public AltaFinanciamientoDao getAltaFinanciamientoDao() {
		return altaFinanciamientoDao;
	}
	public void setAltaFinanciamientoDao(AltaFinanciamientoDao altaFinanciamientoDao) {
		this.altaFinanciamientoDao = altaFinanciamientoDao;
	}
	@Override
	public List<Retorno> consultarConfiguraSet() {
		return altaFinanciamientoDao.consultarConfiguraSet();
	} 
	@Override
	public List<LlenaComboGralDto>obtenerEmpresas(int idUsuario, boolean bMantenimiento ){
	return altaFinanciamientoDao.obtenerEmpresas(idUsuario,bMantenimiento);
	} 
	@Override
	public List<LlenaComboGralDto> obtenerContratos(int noEmpresa) {
		return altaFinanciamientoDao.obtenerContratos(noEmpresa);
	}
	@Override
	public List<LlenaComboGralDto> obtenerDestinoRecursos() {
		return altaFinanciamientoDao.obtenerDestinoRecursos();
	}
	@Override
	public List<LlenaComboGralDto> obtenerPais() {
		return altaFinanciamientoDao.obtenerPais();
	}
	@Override
	public List<LlenaComboGralDto> obtenerTipoContratos(String psTipoFinan, boolean pbTipoContrato) {
		return altaFinanciamientoDao.obtenerTipoContratos(psTipoFinan, pbTipoContrato);
	}
	@Override
	public List<LlenaComboGralDto> obtenerBancos(String psNacionalidad, String psDivisa,int noEmpresa){
		return altaFinanciamientoDao.obtenerBancos(psNacionalidad, psDivisa,noEmpresa);
	}
	@Override
	public List<LlenaComboGralDto> obtenerDivisas(boolean bRestringido) {
		return altaFinanciamientoDao.obtenerDivisas(bRestringido);
	}
	@Override
	public List<LlenaComboGralDto> obtenerTasa() {
		return altaFinanciamientoDao.obtenerTasa();
	}
	@Override
	public List<LlenaComboGralDto> obtenerArrendadoras() {
		return altaFinanciamientoDao.obtenerArrendadoras();
	}
	@Override
	public List<ContratoCreditoDto> obtenerContratoCredito(String clave) {
		return altaFinanciamientoDao.obtenerContratoCredito(clave);
	}
	@Override
	public List<ContratoCreditoDto> obtenerNoDisp(String idFin) {
		return altaFinanciamientoDao.obtenerNoDisp(idFin);
	}
	@Override
	public List<ContratoCreditoDto> obtenerTipoCambio(String idDiv) {
		return altaFinanciamientoDao.obtenerTipoCambio(idDiv);
	}
	@Override
	public List<LlenaComboGralDto> obtenerDisposiciones(String psIdContrato, boolean pbEstatus) {
		return altaFinanciamientoDao.obtenerDisposiciones(psIdContrato, pbEstatus);
	}
	@Override
	public List<ContratoCreditoDto> selectPrefijo(int piBanco) {
		return altaFinanciamientoDao.selectPrefijo(piBanco);
	}
	@Override
	public List<ContratoCreditoDto> selectConsecutivoLinea() {
		return altaFinanciamientoDao.selectConsecutivoLinea();
	}
	@Override
	public List<ContratoCreditoDto> selectInhabil(String pvFechaInhabil) {
		return altaFinanciamientoDao.selectInhabil(pvFechaInhabil);
	}
	@Override
	public List<ContratoCreditoDto> selectContratoCredito(String psIdContrato) {
		return altaFinanciamientoDao.selectContratoCredito(psIdContrato);
	}
	@Override
	public Map<String, Object> altaContrato(ContratoCreditoDto dto,int noEmpresa) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		List<DivisasEncontradasDto> list = new ArrayList<DivisasEncontradasDto>();
		try {
			int result = 0;
			result = altaFinanciamientoDao.insertAltaContrato(dto,noEmpresa);
			if (result > 0) {
				mensajes.add("Línea de crédito guardada.");
			} else {
				mensajes.add("La Línea de crédito no se pudo guardar.");
			}
			mapResult.put("msgUsuario", mensajes);
			mapResult.put("result", result);

		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento,  C:AltaFinanciamientoBusinessImpl, M:altaContrato");
		}
		return mapResult;
	}

	@Override
	public List<ContratoCreditoDto> selectExisteDispAmort(String psIdContrato) {
		return altaFinanciamientoDao.selectExisteDispAmort(psIdContrato);
	}
	@Override
	public Map<String, Object> deleteDispAmortizacion(String psFinanciamiento) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		List<DivisasEncontradasDto> list = new ArrayList<DivisasEncontradasDto>();
		try {
			int result = 0;
			result = altaFinanciamientoDao.deleteDispAmortizacion(psFinanciamiento);
			if (result > 0) {
				mensajes.add("Registros Eliminados.");

			} else {
				mensajes.add("Error al eliminar la línea.");
			}
			mapResult.put("msgUsuario", mensajes);
			mapResult.put("result", result);

		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento,  C:AltaFinanciamientoBusinessImpl, M:deleteDispAmortizacion");
		}
		return mapResult;
	}
	@Override
	public List<DisposicionCreditoDto> selectDisposicionCred(String psIdContrato, int piDisposicion) {
		return altaFinanciamientoDao.selectDisposicionCred(psIdContrato, piDisposicion);
	}
	@Override
	public List<ContratoCreditoDto> selectAltaAmortizaciones(String psIdContrato) {
		return altaFinanciamientoDao.selectAltaAmortizaciones(psIdContrato);
	}
	@Override
	public Map<String, Object> updateLinea(ContratoCreditoDto dto) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		List<DivisasEncontradasDto> list = new ArrayList<DivisasEncontradasDto>();
		try {
			int result = 0;
			result = altaFinanciamientoDao.updateLinea(dto);
			if (result > 0) {
				mensajes.add("El registro ha sido modificado.");
			} else {
				mensajes.add("El registro no se ha modificado correctamente.");
			}
			mapResult.put("msgUsuario", mensajes);
			mapResult.put("result", result);

		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento,  C:AltaFinanciamientoBusinessImpl, M:updateLinea");
		}
		return mapResult;
	}
	@Override
	public List<LlenaComboGralDto> obtenerEquivalencia(String psDesBanco, int piBanco) {
		return altaFinanciamientoDao.obtenerEquivalencia(psDesBanco, piBanco);
	}
	@Override
	public List<ContratoCreditoDto> funSQLTasa(String psTasa) {
		return altaFinanciamientoDao.funSQLTasa(psTasa);
	}
	@Override
	public List<LlenaComboGralDto> funSQLComboClabe(int pvvValor2, String psDivisa, int noEmpresa) {
		return altaFinanciamientoDao.funSQLComboClabe(pvvValor2, psDivisa,noEmpresa);
	}
	@Override
	public List<ContratoCreditoDto> selectBancoNacionalidad(int piBanco) {
		return altaFinanciamientoDao.selectBancoNacionalidad(piBanco);
	}
	@Override
	public List<ContratoCreditoDto> selectNoDisp(String finac) {
		return altaFinanciamientoDao.selectNoDisp(finac);
	}
	@Override
	public Map<String, Object> updateLineaBancoCheq(ContratoCreditoDto dto) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		List<DivisasEncontradasDto> list = new ArrayList<DivisasEncontradasDto>();
		try {
			int result = 0;
			result = altaFinanciamientoDao.updateLineaBancoCheq(dto);
			mapResult.put("result", result);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento,  C:AltaFinanciamientoBusinessImpl, M:updateLineaBancoCheq");
		}
		return mapResult;
	}

	@Override
	public Map<String, Object> altaDisposicion(DisposicionCreditoDto dto) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		List<DivisasEncontradasDto> list = new ArrayList<DivisasEncontradasDto>();
		try {
			int result = 0;
			result = altaFinanciamientoDao.insertDisposicion(dto);
			if (result > 0) {
				mensajes.add("La Disposición ha sido guardada.");
			} else {
				mensajes.add("La Disposición no se ha guardado correctamente.");
			}
			mapResult.put("msgUsuario", mensajes);
			mapResult.put("result", result);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoBusinessImpl, M:altaDisposicion");
		}
		return mapResult;
	}

	@Override
	public Map<String, Object> updateAmortizacionReestructurada(String psLinea, int piDisposicion) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		List<DivisasEncontradasDto> list = new ArrayList<DivisasEncontradasDto>();
		try {
			int result = 0;
			result = altaFinanciamientoDao.updateAmortizacionReestructurada(psLinea, piDisposicion);
			mapResult.put("result", result);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoBusinessImpl, M:updateAmortizacionReestructurada");
		}
		return mapResult;
	}
	@Override
	public List<ContratoCreditoDto> selectDivision(int piBanco, String psChequera) {
		return altaFinanciamientoDao.selectDivision(piBanco, psChequera);
	}
	// prueba para obtener folios
	@Override
	public int obtieneFolioReal(String tipoFolio) {
		return altaFinanciamientoDao.seleccionarFolioReal(tipoFolio);
	}
	@Override
	public Map<String, Object> altaParametro(Parametro dto,int noEmpresa) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		Map<String, Object> mapGenerador = new HashMap<String, Object>();
		List<DivisasEncontradasDto> list = new ArrayList<DivisasEncontradasDto>();
		GeneradorDto generador = new GeneradorDto();
		GlobalSingleton globalSingleton;
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			mapGenerador = new HashMap<String, Object>();
			int result = 0;
			int plresp=0;
			result = altaFinanciamientoDao.insertParametro(dto,noEmpresa);
			if (result <= 0) {
				mensajes.add("Error al Guardar Datos para el Generador: " + 3120);
			}else{
				generador.setEmpresa(noEmpresa);
				generador.setFolParam(Integer.parseInt(dto.getNoFolioParam()));
				mapGenerador = altaFinanciamientoDao.generador(generador);
				plresp=Integer.parseInt(mapGenerador.get("result").toString());
				if (Integer.parseInt(mapGenerador.get("result").toString()) > 0) {
					mensajes.add("Error al Generar: " + 3120);
					bitacora.insertarRegistro(
							new Date().toString() + "P:Financiamiento, C:AltaFinanciamientoBusinessImpl, M:altaParametro "
									+ globalSingleton.getUsuarioLoginDto().getIdUsuario()
									+ "Error al contabilizar(sp_generador)" );
				}		      
			}
			mapResult.put("msgUsuario", mensajes);
			mapResult.put("result", result);
			mapResult.put("plresp", plresp);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoBusinessImpl, M:altaParametro");
		}
		return mapResult;
	}
	@Override
	public List<DisposicionCreditoDto> selectDisp(String psIdContrato, int psIdDisp) {
		return altaFinanciamientoDao.selectDisp(psIdContrato, psIdDisp);
	}
	@Override
	public List<DisposicionCreditoDto> buscaComisiones(String psLinea, int piDisp) {
		return altaFinanciamientoDao.buscaComisiones( psLinea, piDisp);
	}
	@Override
	public List<AmortizacionCreditoDto> selectAmortizaciones(String psIdContrato, int piDisposicion,boolean pbCambioTasa ,String psTipoMenu ,String psProyecto ,int piCapital) {
		return altaFinanciamientoDao.selectAmortizaciones(psIdContrato, piDisposicion,pbCambioTasa ,psTipoMenu ,psProyecto , piCapital);
	}
	@Override
	public Map<String, Object> updateDisposicion(DisposicionCreditoDto dto) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		List<DivisasEncontradasDto> list = new ArrayList<DivisasEncontradasDto>();
		try {
			int result = 0;
			result = altaFinanciamientoDao.updateDisposicion(dto);
			if (result > 0) {
				mensajes.add("Se modificó la Disposición correctamente");
			} else {
				mensajes.add("No se modificó la Disposición correctamente.");
			}
			mapResult.put("msgUsuario", mensajes);
			mapResult.put("result", result);

		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoBusinessImpl, M:updateDisposicion");
		}
		return mapResult;
	}
	@Override
	public List<AmortizacionCreditoDto> selectExisteAmort(String psLinea , String psCredito) {
		return altaFinanciamientoDao.selectExisteAmort(psLinea, psCredito);
	}
	@Override
	public Map<String, Object> deleteAmortizacion(String psFinanciamiento, int piDisp, boolean piInteres,
			boolean pbDisposicion) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			int result = 0;
			result = altaFinanciamientoDao.deleteAmortizacion(psFinanciamiento, piDisp, piInteres, pbDisposicion);
			mapResult.put("result", result);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoBusinessImpl, M:deleteAmortizacion");
		}
		return mapResult;
	}
	@Override
	public Map<String, Object> deleteAGAsigLin(String psFinanciamiento, int piDisp) {
		Map<String, Object> mapResult = new HashMap<String, Object>();	
		try {
			int result = 0;
			result = altaFinanciamientoDao.deleteAGAsigLin(psFinanciamiento, piDisp);
			mapResult.put("result", result);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoBusinessImpl, M:deleteAGAsigLin");
		}
		return mapResult;
	}
	@Override
	public Map<String, Object> deleteFactFacturas(String psFinanciamiento, int piDisp) {
		Map<String, Object> mapResult = new HashMap<String, Object>();	
		try {
			int result = 0;
			result = altaFinanciamientoDao.deleteFactFacturas(psFinanciamiento, piDisp);
			mapResult.put("result", result);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoBusinessImpl, M:deleteFactFacturas");
		}
		return mapResult;
	}
	@Override
	public Map<String, Object> cancelaMovimiento(String psFinanciamiento, int piDisp) {
		Map<String, Object> mapResult = new HashMap<String, Object>();	
		try {
			int result = 0;
			result = altaFinanciamientoDao.cancelaMovimiento(psFinanciamiento, piDisp);
			mapResult.put("result", result);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoBusinessImpl, M:cancelaMovimiento");
		}
		return mapResult;
	}
	@Override
	public List<LlenaComboGralDto> funSQLComboPeriodo(boolean pdAmort) {
		return altaFinanciamientoDao.funSQLComboPeriodo(pdAmort);
	}

	@Override
	public Map<String, Object> funSQLDeleteProvisiones(String psFinanciamiento, int piDisp,boolean pbEstatus){
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			int result = 0;
			result = altaFinanciamientoDao.funSQLDeleteProvisiones(psFinanciamiento, piDisp, pbEstatus);
			mapResult.put("result", result);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoBusinessImpl, M:funSQLDeleteProvisiones");
		}
		return mapResult;
	}
	
	@Override
	public List<AmortizacionCreditoDto> funSQLSelectAmortizacionesIntProv(String IdFinanciamiento, int iIdDisposicion) {
		return altaFinanciamientoDao.funSQLSelectAmortizacionesIntProv(IdFinanciamiento ,iIdDisposicion);
	}
	@Override
	public Map<String, Object> insertBitacora(String psFinanciamiento ,int piDisposicion ,String psNota,String psFinanciamientoHijo, int noEmpresa) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		try {
			int result = 0;
			result = altaFinanciamientoDao.insertBitacora(psFinanciamiento,piDisposicion,psNota,psFinanciamientoHijo,noEmpresa);
			if (result <= 0) {
				mensajes.add("Error al guardar la bitácora");
			}else{
				mensajes.add("La bitácora ha sido guardada");
			}	
			mapResult.put("msgUsuario", mensajes);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoBusinessImpl, M:insertBitacora");
		}
		return mapResult;
	}
	@Override
	public List<BitacoraCreditoBanDto> selectBitacora(String vsContrato, int viDisp, int noEmpresa) {
		return altaFinanciamientoDao.selectBitacora(vsContrato,viDisp,noEmpresa);
	}
	@Override
	public List<ObligacionCreditoDto> obtenerObligaciones(String psFinanciamiento, int noEmpresa) {
		return altaFinanciamientoDao.obtenerObligaciones(psFinanciamiento,noEmpresa);
	}
	@Override
	public List<ObligacionCreditoDto> obtenerObligacionesTotal(String psFinanciamiento, int noEmpresa) {
		return altaFinanciamientoDao.obtenerObligacionesTotal(psFinanciamiento,noEmpresa);
	}
	@Override
	public int insertObligacion(String psFinanciamiento ,int piClave, String descripcion, int noEmpresa) {
		return altaFinanciamientoDao.insertObligacion(psFinanciamiento,piClave,descripcion,noEmpresa);
	}
	@Override
	public int deleteObligacion(List<ObligacionCreditoDto> listObligaciones, int noEmpresa) {
		int resultado=0;
		try {
			for (int i = 0; i < listObligaciones.size(); i++) {
				resultado = altaFinanciamientoDao.deleteObligacion(listObligaciones.get(i).getIdClave(),listObligaciones.get(i).getIdFinanciamiento(), noEmpresa);	
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
	+ "P:Financiamiento, C:AltaFinanciamientoBusinessImpl, M:deleteObligacion");
		}
		return resultado;
	}
	@Override
	public List<Map<String, Object>> obtenerReporteContratos(String idFinanciamiento) {
		return this.altaFinanciamientoDao.obtenerReporteContratos(idFinanciamiento);
	}
	@Override
	public int obtenerDifMeses(String idFinanciamiento,String idDisp){
		return altaFinanciamientoDao.obtenerDifMeses(idFinanciamiento,idDisp);
	}
	@Override
	public List<LlenaComboGralDto> selectComboEmpresaAval(int noEmpresa) {
		return altaFinanciamientoDao.selectComboEmpresaAval(noEmpresa);
	}
	@Override
	public List<AvalGarantiaDto> obtenerMontoDispuestoAvalada(String idFinanciamiento,int idDisp,int noEmpresa) {
		return altaFinanciamientoDao.obtenerMontoDispuestoAvalada(idFinanciamiento,idDisp, noEmpresa);
	}
	@Override
	public List<LlenaComboGralDto> selectComboAvalGtia(String piEmpresaAvalista,String psDivisa, int empresa) {
		char tipoPersona;
		int cveEmpresa=0;
			cveEmpresa=Integer.parseInt(separarEmpresa(piEmpresaAvalista,'E'));
			tipoPersona=separarEmpresa(piEmpresaAvalista,'T').charAt(0);
		return altaFinanciamientoDao.selectComboAvalGtia(cveEmpresa,psDivisa, empresa,tipoPersona);
	}
	@Override
	public int selectAvaladaGtia(int piEmpresa,String psClave, int noEmpresa) {
		return altaFinanciamientoDao.selectAvaladaGtia(piEmpresa,psClave,noEmpresa);
	}
	@Override
	public int insertAvalGtiaLin(String psFinanciamiento, int piDisposicion, double pdMontoDispuesto ,String piEmpresaAvalista, String psClave, int noEmpresa) {
		int resultado=0;
		char tipoPersona;
		int cveEmpresa=0;
			cveEmpresa=Integer.parseInt(separarEmpresa(piEmpresaAvalista,'E'));
			tipoPersona=separarEmpresa(piEmpresaAvalista,'T').charAt(0);
		try {
			resultado = altaFinanciamientoDao.insertAvalGtiaLin(psFinanciamiento,piDisposicion,pdMontoDispuesto,cveEmpresa,psClave, noEmpresa,tipoPersona);	
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
	+ "P:Financiamiento, C:AltaFinanciamientoBusinessImpl, M:insertAvalGtiaLin");
		}
		return resultado;
	}
	@Override
	public int existeAvalGtiaLinea(List<AvalGarantiaDto> listAG, int noEmpresa) {
		int resultado=0;
		char tipoPersona;
		int cveEmpresa=0;
		try {
			for (int i = 0; i < listAG.size(); i++) {
					cveEmpresa=Integer.parseInt(separarEmpresa(listAG.get(i).getNoEmpresaAvalistaAux(),'E'));
					tipoPersona=separarEmpresa(listAG.get(i).getNoEmpresaAvalistaAux(),'T').charAt(0);
				resultado = altaFinanciamientoDao.existeAvalGtiaLinea(cveEmpresa,listAG.get(i).getClave(), noEmpresa,tipoPersona);
				if(resultado==1){
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
	+ "P:Financiamiento, C:AltaFinanciamientoBusinessImpl, M:existeAvalGtiaLinea");
			return -1;
		}
		return resultado;
	}
	@Override
	public int deleteLineaAvalada(List<AvalGarantiaDto> listAG,String linea,int credito, int noEmpresa){
		int resultado=0;
		try {
			char tipoPersona;
			int cveEmpresa=0;
			for (int i = 0; i < listAG.size(); i++) {
				cveEmpresa=Integer.parseInt(separarEmpresa(listAG.get(i).getNoEmpresaAvalistaAux(),'E'));
				tipoPersona=separarEmpresa(listAG.get(i).getNoEmpresaAvalistaAux(),'T').charAt(0);
				resultado = altaFinanciamientoDao.deleteLineaAvalada(cveEmpresa,listAG.get(i).getClave(),linea,credito, noEmpresa,tipoPersona);
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
	+ "P:Financiamiento, C:AltaFinanciamientoBusinessImpl, M:deleteLineaAvalada");
			return -1;
		}
		return resultado;
	}
	@Override
	public Map<String, Object> insertAmortCapitales(String capital,char ps_tasa,double tasaVigente,
			double tasaFija, String vsBisiesto,int lFolio,String cmbPeriodo,int txtNoAmort, 
			int cmbDisp,String txtLinea,String pvTasaBase,Double txtBase,Double puntos, Double txtIva,
			int cmbDiaCorte,int txtDias, String txtComentario,String cmbDiaCorteInt,boolean insertaIntereses){
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		int resultado=0,piNoAmort,res=0;
		String ps_periodo,vsNFecIni;
		Gson gson = new Gson();
		List<Map<String, String>> paramCapital= gson.fromJson(capital,
				new TypeToken<ArrayList<Map<String, String>>>() {
		}.getType());
		try {
			for (int i = 0; i < paramCapital.size(); i++) {
				AmortizacionCreditoDto amortizacionCreditoDto = new AmortizacionCreditoDto();
				amortizacionCreditoDto.setIdAmortizacion(Integer.parseInt(paramCapital.get(i).get("noAmort")));
				amortizacionCreditoDto.setFecInicio(paramCapital.get(i).get("fecIni"));
				amortizacionCreditoDto.setFecVencimiento(paramCapital.get(i).get("fecVen"));
				amortizacionCreditoDto.setFecPago(paramCapital.get(i).get("fecPag"));
				amortizacionCreditoDto.setDias(Integer.parseInt(paramCapital.get(i).get("dias")));
				amortizacionCreditoDto.setCapital(Double.parseDouble(paramCapital.get(i).get("capital")));
				amortizacionCreditoDto.setSaldo(Double.parseDouble(paramCapital.get(i).get("saldo")));
				amortizacionCreditoDto.setEstatus(paramCapital.get(i).get("estatus").charAt(0));
				amortizacionCreditoDto.setDescEstatus((paramCapital.get(i).get("descEstatus")));
				if(cmbPeriodo.equals("")){
					piNoAmort = 0;
					for (int j= 0; j < paramCapital.size(); j++) {
						if(amortizacionCreditoDto.getIdAmortizacion()!=0)
							piNoAmort = piNoAmort + 1;
					}
					ps_periodo = "NO";
				}else{
					piNoAmort = txtNoAmort;
					ps_periodo = cmbPeriodo;
				}
				vsNFecIni=amortizacionCreditoDto.getFecInicio();
				if(i == 0) {
					res=altaFinanciamientoDao.selectExisteAmortizacion(txtLinea,cmbDisp,amortizacionCreditoDto.getIdAmortizacion());
					if(res>0)
						break;
				}
				amortizacionCreditoDto.setIdFinanciamiento(txtLinea);
				amortizacionCreditoDto.setIdDisposicion(cmbDisp);
				amortizacionCreditoDto.setGasto(0);
				amortizacionCreditoDto.setComision(0);
				amortizacionCreditoDto.setInteresA(0);
				amortizacionCreditoDto.setTasaVigente(tasaVigente);
				amortizacionCreditoDto.setTasaFija(tasaFija);
				amortizacionCreditoDto.setPeriodo(ps_periodo);
				amortizacionCreditoDto.setNoAmortizaciones(piNoAmort);
				amortizacionCreditoDto.setTasaBase(pvTasaBase);
				amortizacionCreditoDto.setValorTasa(txtBase);
				amortizacionCreditoDto.setPuntos(puntos);
				amortizacionCreditoDto.setTasa(ps_tasa);
				amortizacionCreditoDto.setIva(txtIva);
				amortizacionCreditoDto.setDiaCortecap(cmbDiaCorte);
				amortizacionCreditoDto.setDiasPeriodo(txtDias);
				amortizacionCreditoDto.setBancoGastcom(0);
				amortizacionCreditoDto.setClabeBancariaGastcom("");
				amortizacionCreditoDto.setComentario(txtComentario);
				amortizacionCreditoDto.setTipoGasto(0);
				amortizacionCreditoDto.setSobreTasacb(0);
				amortizacionCreditoDto.setPagar('S');
				amortizacionCreditoDto.setFactCapital(0);
				amortizacionCreditoDto.setSoloRenta(0);
				amortizacionCreditoDto.setRenta(0);
				amortizacionCreditoDto.setNoFolioAmort(lFolio);
				resultado = altaFinanciamientoDao.insertAmort(amortizacionCreditoDto,vsBisiesto);
				if(resultado<1){
					mensajes.add("Error al guardar el Plan de Amortizaciones.");
					break;
				}
				else{
					if(!insertaIntereses)
						mensajes.add("Plan de amortizaciones guardado.");
				}
			}
			mapResult.put("msgUsuario", mensajes);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
	+ "P:Financiamiento, C:AltaFinanciamientoBusinessImpl, M:insertAmortCapitales");
		}
		return mapResult;
	}
	@Override
	public int  selectExisteAmortizacion(String contrato,int disposicion,int noAmortizacion) {
		int resultado=0;
		try {
			resultado=altaFinanciamientoDao.selectExisteAmortizacion(contrato,disposicion,noAmortizacion);	               

		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
	+ "P:Financiamiento, C:AltaFinanciamientoBusinessImpl, M:selectExisteAmortizacion");
			return -1;
		}
		return resultado;
	}
	@Override
	public Map<String, Object> insertAmortInteres(String interes, String vsBisiesto, int lFolio, String ps_tasa,
			double vdTasaFij, double vdTasaVar, int viSobreTasaBC,String cmbPeriodoInt, int txtNoAmortInt,String txtLinea, int cmbDisp,boolean chkSobreTasa,String pvTasaBase,
			Double txtBase,Double puntos, Double txtIva,int cmbDiaCorte,int txtDias, String txtComentario,String cmbDiaCorteInt){
		int piNoAmort=0,res=0,resultado=0;
		String ps_periodo="";
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		Gson gson = new Gson();
		List<Map<String, String>> paramIntereses= gson.fromJson(interes,
				new TypeToken<ArrayList<Map<String, String>>>() {
		}.getType());
		System.out.println("Entra en "+paramIntereses.size());
		try {
			for (int i = 0; i < paramIntereses.size(); i++) {
				System.out.println(i+"    -------------------------------");
				AmortizacionCreditoDto amortizacionCreditoDto = new AmortizacionCreditoDto();
				amortizacionCreditoDto.setFecInicio(paramIntereses.get(i).get("fecInicio"));
				amortizacionCreditoDto.setFecVencimiento(paramIntereses.get(i).get("fecVenc"));
				amortizacionCreditoDto.setFecPago(paramIntereses.get(i).get("fecPago"));
				amortizacionCreditoDto.setDias(Integer.parseInt(paramIntereses.get(i).get("dias")));
				amortizacionCreditoDto.setSaldo(Double.parseDouble(paramIntereses.get(i).get("saldo")));
				amortizacionCreditoDto.setSaldoInsoluto(Double.parseDouble(paramIntereses.get(i).get("saldoIns")));
				amortizacionCreditoDto.setInteresA(Double.parseDouble(paramIntereses.get(i).get("interes")));
				
				amortizacionCreditoDto.setIva(0);
				//System.out.println(paramIntereses.get(i).get("iva"));
				
				amortizacionCreditoDto.setIva(Double.parseDouble(paramIntereses.get(i).get("iva")));
				amortizacionCreditoDto.setEstatus(paramIntereses.get(i).get("estatus").charAt(0));
				amortizacionCreditoDto.setDescEstatus((paramIntereses.get(i).get("descEstatus")));
				if(cmbPeriodoInt.equals("")){
					piNoAmort = paramIntereses.size();
					ps_periodo = "NO";
				}else{
					piNoAmort = txtNoAmortInt;
					ps_periodo = cmbPeriodoInt;
				}
				if(ps_tasa == "F"){
					vdTasaFij = amortizacionCreditoDto.getSaldoInsoluto();
					vdTasaVar = 0;
				}
				else{
					vdTasaVar =amortizacionCreditoDto.getSaldoInsoluto();
					vdTasaFij = 0;
				}
				if(i == 0) {
					res=altaFinanciamientoDao.selectExisteAmortizacion(txtLinea,cmbDisp,0);
					if(res>0) {
						break;
					}
					
		           
				}
				//If vsTipoMenu = "B" And frmAltaCredito.txtSobreTasaCB.Text <> 0 And chkSobreTasa.Value = 1 Then
				//viSobreTasaBC = 2
				//Else
				viSobreTasaBC = 1;
				//End If
				amortizacionCreditoDto.setIdAmortizacion(0);
				amortizacionCreditoDto.setIdFinanciamiento(txtLinea);
				amortizacionCreditoDto.setIdDisposicion(cmbDisp);
				amortizacionCreditoDto.setCapital(0);
				amortizacionCreditoDto.setGasto(0);
				amortizacionCreditoDto.setComision(0);
				amortizacionCreditoDto.setTasaVigente(vdTasaVar);
				amortizacionCreditoDto.setTasaFija(vdTasaFij);
				amortizacionCreditoDto.setPeriodo(ps_periodo);
				amortizacionCreditoDto.setNoAmortizaciones(piNoAmort);
				amortizacionCreditoDto.setTasaBase(pvTasaBase);
				amortizacionCreditoDto.setValorTasa(txtBase);
				amortizacionCreditoDto.setPuntos(puntos);
				amortizacionCreditoDto.setTasa(ps_tasa.charAt(0));
				//amortizacionCreditoDto.setIva(txtIva);
				amortizacionCreditoDto.setDiaCortecap(cmbDiaCorte);
				amortizacionCreditoDto.setDiaCorteint(Integer.parseInt(cmbDiaCorteInt));
				amortizacionCreditoDto.setDiasPeriodo(txtDias);
				amortizacionCreditoDto.setBancoGastcom(0);
				amortizacionCreditoDto.setClabeBancariaGastcom("");
				amortizacionCreditoDto.setComentario(txtComentario);
				amortizacionCreditoDto.setTipoGasto(0);
				amortizacionCreditoDto.setSobreTasacb(viSobreTasaBC);
				amortizacionCreditoDto.setPagar('S');
				amortizacionCreditoDto.setFactCapital(0);
				amortizacionCreditoDto.setSoloRenta(0);
				amortizacionCreditoDto.setRenta(0);
				amortizacionCreditoDto.setNoFolioAmort(lFolio);
				if(amortizacionCreditoDto.getSaldo()!=0&&!amortizacionCreditoDto.getFecInicio().equals(""))
					resultado = altaFinanciamientoDao.insertAmort(amortizacionCreditoDto,vsBisiesto);
				if(resultado<1){
					mensajes.add("Error al guardar el Plan de Amortizaciones.");
					break;
				}
				else
					mensajes.add("Plan de amortizaciones guardado.");
			}
			mapResult.put("msgUsuario", mensajes);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
	+ "P:Financiamiento, C:AltaFinanciamientoBusinessImpl, M:insertAmortInteres");
		}
		return mapResult;
	}
	@Override
	public int existeIntProv(String psIdContrato , int psIdDisposicion ,  int iConsecutivo, Date dFecVencimiento, int noEmpresa) {
		int resultado=0;
		try {
			resultado = altaFinanciamientoDao.existeIntProv(psIdContrato,psIdDisposicion,iConsecutivo,dFecVencimiento, noEmpresa);			
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
	+ "P:Financiamiento, C:AltaFinanciamientoBusinessImpl, M:existeIntProv");
			return -1;
		}
		return resultado;
	}
	@Override
	public int insertProvisionInteres(ProvisionCreditoDTO provision) {
		int resultado=0;
		try {
			resultado = altaFinanciamientoDao.insertProvisionInteres(provision);	
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
	+ "P:Financiamiento, C:AltaFinanciamientoBusinessImpl, M:insertProvisionInteres");
		}
		return resultado;
	}
	@Override
	public int updateProvision(ProvisionCreditoDTO provision) {
		int resultado=0;
		try {
			resultado = altaFinanciamientoDao.updateProvision(provision);	
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
	+ "P:Financiamiento, C:AltaFinanciamientoBusinessImpl, M:updateProvision");
		}
		return resultado;
	}
	@Override
	public HSSFWorkbook reporteAmortizaciones(String contrato, String disposicion,String montoAutorizado,String banco,String montoDisposicion,
			String divisa, String fechaInicio, String fechaFin, String tasa) {
		HSSFWorkbook hb=null;
		try {
			hb=generarExcel(new String[]{
					"Amortizaciones",
					"Fecha Inicial",
					"Fecha Final",
					"Fecha Pago",
					"Mes",
					"Días",
					"Tasa",
					"Capital",
					"Interes",
					"IVA",
					"Pago Total",
					"Saldo Insoluto",
					"Estatus",
			}, 
					altaFinanciamientoDao.reporteAmorizaciones(contrato, disposicion), new String[]{
							"id_amortizacion",
							"fec_inicio",
							"fec_vencimiento",
							"fec_pago",
							"mes",
							"dias",
							"tasa",
							"capital",
							"interes",
							"iva",
							"pagoTotal",
							"saldo_insoluto",
							"desc_estatus",
			},
					contrato,disposicion, montoAutorizado, banco, montoDisposicion,
					divisa,  fechaInicio,  fechaFin,  tasa);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: reportePersonas");
		}return hb;
	}	
	public static HSSFWorkbook generarExcel(String[] headers,
			List<Map<String, String>> data,
			String[] keys,String contrato,String disposicion,String montoAutorizado,String banco,String montoDisposicion,
			String divisa,  String fechaInicio,String  fechaFin, String tasa) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		int rowIdx = 0;
		int cellIdx = 0;
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		//Encabezado
		HSSFRow hssfHeader = sheet.createRow(rowIdx);
		HSSFFont font = wb.createFont();
		HSSFCellStyle cellStyle = wb.createCellStyle();
		font.setBold(true);
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(CellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.BLUE_GREY().getIndex());
		hssfHeader = sheet.createRow(rowIdx);
		HSSFCell celdaTitulo = hssfHeader.createCell(cellIdx);
		celdaTitulo.setCellStyle(cellStyle);
		celdaTitulo.setCellValue(new HSSFRichTextString("TABLA DE AMORTIZACIONES"));
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));
		//Renglón 3 
		rowIdx = 3;
		cellIdx = 0;
		HSSFCellStyle cellStyle2 = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle2.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle2.setFillPattern(CellStyle.FINE_DOTS );
		cellStyle2.setFont(font);
		cellStyle2.setFillForegroundColor(new HSSFColor.CORNFLOWER_BLUE().getIndex());
		hssfHeader = sheet.createRow(rowIdx);
		HSSFCell celdaLinea = hssfHeader.createCell(cellIdx);
		celdaLinea.setCellStyle(cellStyle2);
		celdaLinea.setCellValue(new HSSFRichTextString("Línea"));
		cellIdx = 1;
		HSSFCell celdaLinea2 = hssfHeader.createCell(cellIdx);
		celdaLinea2.setCellStyle(cellStyle2);
		celdaLinea2.setCellValue(new HSSFRichTextString(contrato));
		cellIdx = 2;
		HSSFCell celdaLineaX = hssfHeader.createCell(cellIdx);
		celdaLineaX.setCellStyle(cellStyle2);
		celdaLineaX.setCellValue(new HSSFRichTextString(""));
		cellIdx = 3;
		HSSFCell celdaLinea3 = hssfHeader.createCell(cellIdx);
		celdaLinea3.setCellStyle(cellStyle2);
		celdaLinea3.setCellValue(new HSSFRichTextString(montoAutorizado));
		cellIdx = 4;
		HSSFCell celdaLinea4 = hssfHeader.createCell(cellIdx);
		celdaLinea4.setCellStyle(cellStyle2);
		celdaLinea4.setCellValue(new HSSFRichTextString("Autorizado"));
		cellIdx = 5;
		HSSFCell celdaLinea5 = hssfHeader.createCell(cellIdx);
		celdaLinea5.setCellStyle(cellStyle2);
		celdaLinea5.setCellValue(new HSSFRichTextString(""));
		cellIdx = 6;
		HSSFCell celdaLinea7 = hssfHeader.createCell(cellIdx);
		celdaLinea7.setCellStyle(cellStyle2);
		celdaLinea7.setCellValue(new HSSFRichTextString(""));
		cellIdx = 7;
		HSSFCell celdaLinea8 = hssfHeader.createCell(cellIdx);
		celdaLinea8.setCellStyle(cellStyle2);
		celdaLinea8.setCellValue(new HSSFRichTextString("Banco"));
		cellIdx = 8;
		HSSFCell celdaLinea9 = hssfHeader.createCell(cellIdx);
		celdaLinea9.setCellStyle(cellStyle2);
		celdaLinea9.setCellValue(new HSSFRichTextString(banco));	
		//Renglón 5
		rowIdx = 5;
		cellIdx = 0;
		HSSFCellStyle cellStyle3 = wb.createCellStyle();
		HSSFFont font2 = wb.createFont();
		font2.setBold(true);
		font2.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle3.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle3.setFillPattern(CellStyle.FINE_DOTS );
		cellStyle3.setFont(font2);
		cellStyle3.setFillForegroundColor(new HSSFColor.BRIGHT_GREEN().getIndex());
		hssfHeader = sheet.createRow(rowIdx);
		HSSFCell celdaDisposicion = hssfHeader.createCell(cellIdx);
		celdaDisposicion.setCellStyle(cellStyle3);
		celdaDisposicion.setCellValue(new HSSFRichTextString("Disposición"));
		cellIdx = 1;
		HSSFCell celdaDisposicionD = hssfHeader.createCell(cellIdx);
		celdaDisposicionD.setCellStyle(cellStyle3);
		celdaDisposicionD.setCellValue(new HSSFRichTextString(disposicion));
		cellIdx = 2;
		HSSFCell celdaDisposicion2 = hssfHeader.createCell(cellIdx);
		celdaDisposicion2.setCellStyle(cellStyle3);
		celdaDisposicion2.setCellValue(new HSSFRichTextString(""));
		cellIdx = 3;
		HSSFCell celdaDisposicion3 = hssfHeader.createCell(cellIdx);
		celdaDisposicion3.setCellStyle(cellStyle3);
		celdaDisposicion3.setCellValue(new HSSFRichTextString(montoDisposicion));
		cellIdx = 4;
		HSSFCell celdaDisposicion4 = hssfHeader.createCell(cellIdx);
		celdaDisposicion4.setCellStyle(cellStyle3);
		celdaDisposicion4.setCellValue(new HSSFRichTextString("Dispuesto"));
		cellIdx = 5;
		HSSFCell celdaDisposicion5 = hssfHeader.createCell(cellIdx);
		celdaDisposicion5.setCellStyle(cellStyle3);
		celdaDisposicion5.setCellValue(new HSSFRichTextString(divisa));
		//Renglón 6 
		rowIdx = 6;
		cellIdx = 0;
		HSSFCellStyle cellStyle4 = wb.createCellStyle();
		HSSFFont font3 = wb.createFont();
		font3.setBold(true);
		font3.setColor(new HSSFColor.BLACK().getIndex());
		cellStyle4.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle4.setFont(font3);
		cellStyle4.setFillForegroundColor(new HSSFColor.WHITE().getIndex());
		hssfHeader = sheet.createRow(rowIdx);
		HSSFCell celdaFecIni = hssfHeader.createCell(cellIdx);
		celdaFecIni.setCellStyle(cellStyle4);
		celdaFecIni.setCellValue(new HSSFRichTextString("Fecha Inicio"));
		cellIdx=1;
		HSSFCell celdaFecIni2 = hssfHeader.createCell(cellIdx);
		celdaFecIni2.setCellStyle(cellStyle4);
		celdaFecIni2.setCellValue(new HSSFRichTextString(fechaInicio));
		//Renglón 7 
		rowIdx = 7;
		cellIdx = 0;
		hssfHeader = sheet.createRow(rowIdx);
		HSSFCell celdaFecFin= hssfHeader.createCell(cellIdx);
		celdaFecFin.setCellStyle(cellStyle4);
		celdaFecFin.setCellValue(new HSSFRichTextString("Fecha Final"));
		cellIdx = 1;
		HSSFCell celdaFecFin2= hssfHeader.createCell(cellIdx);
		celdaFecFin2.setCellStyle(cellStyle4);
		celdaFecFin2.setCellValue(new HSSFRichTextString(fechaFin));
		//Renglón 8 
		rowIdx = 8;
		cellIdx = 0;
		hssfHeader = sheet.createRow(rowIdx);
		HSSFCell celdaTasa= hssfHeader.createCell(cellIdx);
		celdaTasa.setCellStyle(cellStyle4);
		celdaTasa.setCellValue(new HSSFRichTextString("Tasa"));
		cellIdx = 1;
		HSSFCell celdaTasa2= hssfHeader.createCell(cellIdx);
		celdaTasa2.setCellStyle(cellStyle4);
		celdaTasa2.setCellValue(new HSSFRichTextString(tasa));
		//Encabezado tabla
		cellIdx = 0;
		rowIdx = 10;
		hssfHeader = sheet.createRow(rowIdx);
		font = wb.createFont();
		cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(CellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.LIGHT_BLUE().getIndex());
		for (String string : headers) {
			HSSFCell hssfCell = hssfHeader.createCell(cellIdx++);
			hssfCell.setCellStyle(cellStyle);
			hssfCell.setCellValue(new HSSFRichTextString(string));
		}
		//Datos tabla
		rowIdx = 11;
		int fila=0;
		double totalCapital=0,totalInteres=0,totalPagoTotal=0;
		for (Iterator<Map<String, String>> rows = data.iterator(); rows.hasNext();) {
			Map<String, String> row = rows.next();
			HSSFRow hssfRow = sheet.createRow(rowIdx++);
			cellIdx = 0;
			for (String string : keys) {
				if(string.equals("capital")){
					totalCapital+=Double.parseDouble(row.get(string));
				}
				if(string.equals("interes")){
					totalInteres+=Double.parseDouble(row.get(string));
				}
				if(string.equals("pagoTotal")){
					totalPagoTotal+=Double.parseDouble(row.get(string));
				}
				HSSFCell hssfCell = hssfRow.createCell(cellIdx++);
				hssfCell.setCellValue(new HSSFRichTextString(row.get(string)));
			}
		}
		totalCapital=Math.rint(totalCapital*100)/100;
		totalInteres=Math.rint(totalInteres*100)/100;
		totalPagoTotal=Math.rint(totalPagoTotal*100)/100;
		font.setBold(true);
		//Renglón Total Capital
		font.setColor(new HSSFColor.WHITE().getIndex());
		hssfHeader = sheet.createRow(rowIdx);
		cellIdx = 7;
		HSSFCell celdaTotalCapital = hssfHeader.createCell(cellIdx);
		celdaTotalCapital.setCellStyle(cellStyle2);
		celdaTotalCapital.setCellValue(new HSSFRichTextString(totalCapital+""));
		//Renglón Total Interes
		cellIdx = 8;
		HSSFCell celdaTotalInteres = hssfHeader.createCell(cellIdx);
		celdaTotalInteres.setCellStyle(cellStyle2);
		celdaTotalInteres.setCellValue(new HSSFRichTextString(totalInteres+""));
		//Renglón Pago Total
		cellIdx = 10;
		HSSFCell celdaTotalPago = hssfHeader.createCell(cellIdx);
		celdaTotalPago.setCellStyle(cellStyle2);
		celdaTotalPago.setCellValue(new HSSFRichTextString(totalPagoTotal+""));
		wb.setSheetName(0, "Hoja 1");
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn((short)i);
		}	
		return wb;
	}		
	@Override
	public List<AmortizacionCreditoDto> selectAmortizacionesCapital(String psIdContrato, int piDisposicion,boolean pbCambioTasa ,String psTipoMenu ,String psProyecto ,int piCapital) {
		return altaFinanciamientoDao.selectAmortizacionesCapital(psIdContrato, piDisposicion,pbCambioTasa ,psTipoMenu ,psProyecto , piCapital);
	}
	@Override
	public List<AmortizacionCreditoDto> selectAmortizacionesInteres(String psIdContrato, int piDisposicion,boolean pbCambioTasa ,String psTipoMenu ,String psProyecto ,int piCapital,int dias) {
		return altaFinanciamientoDao.selectAmortizacionesInteres(psIdContrato, piDisposicion,pbCambioTasa ,psTipoMenu ,psProyecto , piCapital, dias);
	}
	@Override
	public void deleteProvisionesFuturas(String IdFinanciamiento, int iIdDisposicion, Date vsFechaFin) {
		try {
			 altaFinanciamientoDao.deleteProvisionesFuturas(IdFinanciamiento,iIdDisposicion,vsFechaFin);	
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
	+ "P:Financiamiento, C:AltaFinanciamientoBusinessImpl, M:deleteProvisionesFuturas");
		}
	}
	
	/*Este método sirve para obtener la clave de la persona o el tipo de la persona, según se indique en el 
	 parámetro tipo 
	 E: clave de la persona
	 T: tipo de persona, retorna 'F' si es física y 'E' si es moral*/
	public String separarEmpresa(String empresaTipo,char tipo){
		String cadenaSeparada[];
		String cadena="";
		cadenaSeparada=empresaTipo.split("_");
		if(tipo=='T')
			cadena=cadenaSeparada[0];
		else
			cadena=cadenaSeparada[1];
		return cadena;
	}
	
	@Override
	public Map<String, Object> provision(String contrato, int disposicion, int noEmpresa) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		int lFolio=0, result=0;
		AltaFinanciamientoAction altaFinanciamientoAction = new AltaFinanciamientoAction();
		try {
			lFolio = altaFinanciamientoDao.obtieneFolioAmort(contrato, disposicion);	
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
