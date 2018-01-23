package com.webset.set.egresos.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.webset.set.egresos.dto.PagosPropuestosDto;
import com.webset.set.utilerias.dto.ComunEgresosDto;
import com.webset.set.utilerias.dto.GrupoEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.SaldosChequerasDto;
import com.webset.set.utilerias.dto.SeleccionAutomaticaGrupoDto;

import net.sf.jasperreports.engine.JRDataSource;

public interface PagoDePropuestasSetService {
	public List<LlenaComboGralDto> llenarComboGrupoEmpresa(GrupoEmpresasDto dto);
	public List<LlenaComboGralDto> llenarComboDivXEmp(int IdUsuario);
	public SaldosChequerasDto obtenerSaldosChequeras(SaldosChequerasDto dto);
	public List<MovimientoDto>consultarDetalle(SeleccionAutomaticaGrupoDto dtoIn);
	public List<MovimientoDto>consultarDetallePropuestasNoPagadas(SeleccionAutomaticaGrupoDto dtoIn);
	public List<MovimientoDto>consultarPropuestas(PagosPropuestosDto dtoIn);
	public Map<String, Object> ejecutarPropuestas(List<ComunEgresosDto> listGrid, String fecHoy);
	public HSSFWorkbook consultaPagosExcel(String clave);
	public List<ComunEgresosDto> validarCpaVtaTransfer(List<ComunEgresosDto> listGrid);
	public Map<String,Object> agruparMovimientos(List<ComunEgresosDto> listGrid);
	public boolean verificarNuevo(int noEmpresa, int noProveedor, String idDivisa, int idFormaPago, 
			int idBancoPag, String idChequeraPago, String psBeneficiario, String psAgrupaCheEmp, String psCveControl,
			List<MovimientoDto> listGrupos, int cLote,String cveControl, String idChequeraBenef);
	public Map<String,Integer> llamarPagadorFactoraje(int noUsuario, String sFolios, int iFoliosRech, String psMensaje, int noEmpresa);
	
	public JRDataSource reportePagoPropuestas(PagosPropuestosDto dtoIn);
	public Map<String, Object> insertarFoliosZexpFact(String datos, String fecHoy, int idUsuario);

}
