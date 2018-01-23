package com.webset.set.conciliacionbancoset.service;

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;

import com.webset.set.conciliacionbancoset.dto.ConciliaBancoDto;
import com.webset.set.conciliacionbancoset.dto.CriteriosBusquedaDto;
import com.webset.set.conciliacionbancoset.dto.MonitorConciliaGenDTO;
import com.webset.set.conciliacionbancoset.dto.MonitorConciliacionDTO;
import com.webset.set.conciliacionbancoset.dto.ParamBusquedaDto;
import com.webset.set.ingresos.dto.CuentaContableDto;
import com.webset.set.utilerias.dto.CatCtaBancoDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;

public interface ConciliacionBancoSetService {
	
	public List<LlenaComboGralDto> llenarComboBancos(int iEmpresa);
	public String consultarFechaConciliacion(int iBanco, int tEmpresa);
	public List<CatCtaBancoDto> consultarChequerasConciliar(int noEmpresa, int noBanco);
	public String ejecutarConciliacionAutomaticaBS(List<CatCtaBancoDto> listDatos);
	public List<LlenaComboChequeraDto> llenarCmbChequeras(int iBanco, int iEmpresa, int iOpc);
	public List<LlenaComboChequeraDto> consultarEstatus(boolean bCancelacion);
	public Map<String, Object> validarConciliacion(CriteriosBusquedaDto dto, boolean bCancelacion);
	public List<ConciliaBancoDto> llenarMovsBanco(CriteriosBusquedaDto dto);
	public List<MovimientoDto> llenarMovsEmpresa(CriteriosBusquedaDto dto);
	public Map<String, Object> consultarContabiliza(String sRubro);
	public Map<String, Object> crearMovimientoSET(List<ConciliaBancoDto> listBanco, CriteriosBusquedaDto dto);
	public Map<String, Object> ejecutarConciliacionManualBS(List<ConciliaBancoDto> listBanco, List<MovimientoDto> listEmpresa, CriteriosBusquedaDto dto);
	public List<ConciliaBancoDto> llenarGridMovsBanco(CriteriosBusquedaDto dto);
	public List<MovimientoDto> llenarGridMovsEmpresa(CriteriosBusquedaDto dto);
	public Map<String, Object> cancelarConciliaciones(List<ConciliaBancoDto> listBanco, List<MovimientoDto> listEmpresa, CriteriosBusquedaDto dto);
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteCancelados(Map parameters);
	public List<MovimientoDto> consultarMovsFicticios(CriteriosBusquedaDto dto);
	public Map<String, Object> ejecutarMovsFicticios(List<MovimientoDto> listMov, CriteriosBusquedaDto dto);
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteMovsFicticios(Map parameters);
	public List<ConciliaBancoDto> consultarMovsDuplicados(CriteriosBusquedaDto dto);
	public Map<String, Object> ejecutarMovsDuplicados(List<ConciliaBancoDto> listMov, CriteriosBusquedaDto dto);
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteMovsDuplicados(Map parameters);
	public List<LlenaComboGralDto> llenarComboBancosEmpresa(String sEmpresas);
	public List<CatCtaBancoDto> llenarGridChequerasBanco(String sEmpresas, int iBanco);
	@SuppressWarnings("unchecked")
	public List<JRDataSource> obtenerReporteDetalle(Map parameters);
	public JRDataSource reporteGlobal(ParamBusquedaDto dto);
	public List<MovimientoDto> llenarMovsEmpresaVIngresos(
			CriteriosBusquedaDto dto);
	public Map<String, Object> updateMovimientoSETVIngresos(
			List<ConciliaBancoDto> listBanco, CriteriosBusquedaDto dtoParams);
	public List<CuentaContableDto> getCuentaContable(int noEmpresa, String idGrupo, String idRubro);
	public List<CuentaContableDto> consultarFacturasCXC(String noCliente, int noEmpresa);
	public String clasificaIngresos(List<Map<String, String>> datosGrid, boolean ietu, boolean iva, String concepto);
	public List<LlenaComboGralDto> llenarComboDeptos(int noEmpresa);
	public List<LlenaComboGralDto> llenarComboEmpresa(int iUsuario);
	public Map<String, String> consultarHeadersMonitor(CriteriosBusquedaDto dto);
	public MonitorConciliacionDTO consultarMonitorConciliacion(ParamBusquedaDto dto);
	public JRDataSource obtenerReporteMonitor(Map<String, Object> datosReporte);
}
