package com.webset.set.coinversion.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import net.sf.jasperreports.engine.JRDataSource;

import com.webset.set.coinversion.dto.BarridoChequerasDto;
import com.webset.set.coinversion.dto.DivisasEncontradasDto;
import com.webset.set.coinversion.dto.InteresesDto;
import com.webset.set.coinversion.dto.ParamBusquedaFondeoDto;
import com.webset.set.coinversion.dto.ParamRetornoFondeoAutDto;
import com.webset.set.coinversion.dto.ParamTraspasoCoinversionDto;
import com.webset.set.coinversion.dto.ParamsBusquedaBarridoDto;
import com.webset.set.seguridad.dto.EmpresaDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.OrdenInversionDto;
import com.webset.set.utilerias.dto.Retorno;

public interface CoinversionService {
	public List<Retorno> consultarConfiguraSet();
	public List<LlenaComboGralDto> llenarComboEmpresaRaiz(boolean bExistentes);
	public List<LlenaComboGralDto> llenarComboBancoConcentradora(boolean bConcentradora,
			int idEmpresa, String idDivisa);
	public List<LlenaComboGralDto> llenarComboChequeraFondeo(int iIdEmpresa, String sIdDivisa, int iIdBanco);
	public List<ParamRetornoFondeoAutDto> consultarFondeoAutomatico(ParamBusquedaFondeoDto dtoBus);
	public double obtenerTipoCambioHoy();
	public List<LlenaComboGralDto> llenarBancosRaiz(int iIdRaiz, String sIdDivisa);
	public Map<String, Object> realizarFondeoAutomatico(List<ParamRetornoFondeoAutDto> listGridFon, ParamBusquedaFondeoDto dtoBus);
	public double sacarSaldoChequera(int iIdEmpresa, String sIdChequera, 
			int iIdBanco, boolean bSobregiro);
	public boolean verificarMovimientosNeg(int iIdEmpresaCon, int iIdEmpresaF, double uImporte, String sIdDivisa);
	public List<MovimientoDto> obtenerPagos(ParamBusquedaFondeoDto dtoBus);
	public List<ParamRetornoFondeoAutDto> obtenerDesglosePagos(ParamBusquedaFondeoDto dtoBus);
	public List<MovimientoDto> obtenerFondeoCheques(String sIdChequera, int iIdBanco, String sVencimiento);
	public Map<String, Object> ejecutarFondeoCheques(List<MovimientoDto> listVencHoy,
			List<MovimientoDto> listVencAnt, int iIdBanco, String sIdChequera);
	public List<DivisasEncontradasDto> consultarDivisasFilialesEncontradas(int iEmpresa);
	public List<DivisasEncontradasDto> consultarDivisasFilialesNoEncontradas(int iEmpresa);
	public Map<String, Object> ejecutarAltaFilial(EmpresaDto dtoEmpresas, List<DivisasEncontradasDto> listDivisas, int concentradora);
	public Map<String, Object> eliminarFilial(EmpresaDto dtoEmpresas, List<DivisasEncontradasDto> listDivisas, int concentradora);
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteCoinversion(Map parameters);
	public List<Map<String, Object>> consultarReporteCoinversion(int plEmpresa, int plUsuario);
	public List<LlenaComboGralDto> llenarComboBancosConcentradora(boolean bConcentradora,int iIdEmpresa, String idDivisa);
	public List<LlenaComboChequeraDto> llenarComboChequerasBarrido(boolean bPagadora, int iBanco, int iNoEmpresa);
	public List<BarridoChequerasDto> consultarBarridoChequerasPorSaldo(int iConcentradora, String sDivisa, String sBancos);
	public Map<String, Object> importarBancos(String sBancos);
	public Map<String, Object> ejecutarBarrido(List<BarridoChequerasDto> listBarrido, ParamsBusquedaBarridoDto dtoParams);
	public List<LlenaComboEmpresasDto> llenarCmbEmpresasCoinversionistas(int iEmpresa, String sDivisa);
	public List<LlenaComboGralDto> llenarCmbBancos(int iEmpresa, String sDivisa);
	public List<Double> consultarSaldoFinal(int iEmpresa, int iBanco,String sDivisa, String sChequera);
	public List<Map<String,String>> consultarSaldoCreditoCoinvPorChequera(int iCoinversora, int iEmpresa, String sDivisa);
	public List<LlenaComboGralDto> consultarBancos(int iEmpresa, int iBanco, String sChequera);
	public Map<String, Object> ejecutarSolicitudBarrido(ParamTraspasoCoinversionDto dtoParam);
	public List<LlenaComboGralDto> llenarCmbBancosSolFondeo2(int iEmpresa, String sDivisa);
	public List<LlenaComboChequeraDto> llenarCmbChequeras(int iBanco, int iNoEmpresa);
	public List<Double> consultarSaldoFinal2(int iBanco, String sChequera, int iEmpresa, int iEmpInv, String sDivisa);
	public Map<String, Object> ejecutarSolicitudFondeo(ParamTraspasoCoinversionDto dtoParam);
	public List<InteresesDto> consultarValoresSdoPromyCapitalizacion(int iEmpresa, String sDivisa);
	public List<InteresesDto> buscarIntereses(int iEmpresa, double dIva, String sDivisa, String sFecha1, String sFecha2);
	public List<InteresesDto> calcularInteresesPorDevengar(InteresesDto dtoParams);
	public Map<String, Object> ejecutarCapitalizacion(List<InteresesDto> listGrid, InteresesDto dtoParam);
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteSaldoPromedio(Map parameters);
	public List<OrdenInversionDto> llenarCmbAnios();
	public List<OrdenInversionDto> llenarCmbMeses(int iAnio);
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteTasasInversion(Map parameters);
	public List<EmpresaDto> llenarGridEmpresasCoinv(int iEmpresa, String sDivisa);
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteEstadodeCuenta(Map parameters);
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteHistoricoSaldos(Map parameters);
	public JRDataSource reportePrueba(Map<?, ?> parameters);
	public HSSFWorkbook obtenerExcel(Map<String, String> map, String ruta);
	public List<LlenaComboGralDto> llenarTipoSaldo();
}
