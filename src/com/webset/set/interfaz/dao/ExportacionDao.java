package com.webset.set.interfaz.dao;

import java.util.List;

import com.webset.set.conciliacionbancoset.dto.CriteriosBusquedaDto;
import com.webset.set.interfaz.dto.GuiaContableDto;
import com.webset.set.interfaz.dto.InterfazDto;

public interface ExportacionDao {
	public List<InterfazDto> llenaComboEmpresa();
	public List<InterfazDto> llenaGridRealizados(int noEmpresa, String fecHoy);
	public List<InterfazDto> llenaGridCancelados(int noEmpresa, String fecHoy);
	public int llamaExportador(String folios, String prefijo);
	public List<InterfazDto> llenaGridRechazados(int noEmpresa, String fecHoy);
	public List<GuiaContableDto> llenaComboCuentas(String noCuenta, String idTipoMovto);
	public int updateMovimientoSET(String noFolioDet, CriteriosBusquedaDto dto);
	public List<InterfazDto> llenaGridPendientesCXC(int noEmpresa, String fecHoy);
	public int exportaRegistrosCXC(List<InterfazDto> listaDatos,int i);
	public List<InterfazDto> listaRegistrosCXC(String folios);
	public List<InterfazDto> listaRegistrosDetalleCXC(int folio);
	public int exportaRegistrosDetalleCXC(List<InterfazDto> listaDatosDet,int i, int ban);
	public int insertaRegistrosIETU_IVACXC(String folios, int tipoMovto);
	public List<InterfazDto> totalFolioRef(int noEmpresa, double tipoCambio);
	public int insertComisionesAgrup(List<InterfazDto> listMovtos, int i, double importe, int folioDetConta, int noOper);
	public int insertComisionesAgrupDet(List<InterfazDto> listMovtos, int i, double tipoCambio, int folioDetConta, 
			boolean comple, int folioComple, List<InterfazDto> ctasConta, boolean regTotal, double importeTotal);
	public int updateMovComisiones(int noFolioDet);
	public List<InterfazDto> totalEmpresas();
	public List<InterfazDto> buscaMovtos(String foliosDet, boolean comisiones);
	public int insertZexpFact(List<InterfazDto> listMovtos, int i, List<InterfazDto> ctasConta, double tipoCambio, List<InterfazDto> listDepCCPro);
	public int insertZexpFactDet(List<InterfazDto> listMovtos, int i, double tipoCambio, int noFolioDet, 
			List<InterfazDto> ctasConta, boolean comple, boolean grupoPago, List<InterfazDto> listDepCCPro);
	public int updateMovimiento(List<InterfazDto> listMovtos, int i);
	public double obtenTipoCambio();
	public int seleccionarFolioReal(String tipoFolio);
	public int actualizarFolioReal(String tipoFolio);
	public List<InterfazDto> ctasContables(int noFolioDet, boolean divisaDLS);
	public List<InterfazDto> buscaDeptoCCProyect(List<InterfazDto> listMovtos, int i);
	public int buscaComisionesAgrup(int noFolioDet);
	
//	public int colocaCtaCargo(List<InterfazDto> listMovtos, int i, List<InterfazDto> ctasConta);
}