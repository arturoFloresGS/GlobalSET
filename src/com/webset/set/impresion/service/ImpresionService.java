package com.webset.set.impresion.service;

import java.util.List;
import java.util.Map;

import com.webset.set.impresion.dto.CatFirmasDto;
import com.webset.set.impresion.dto.ConsultaChequesDto;
import com.webset.set.impresion.dto.ControlPapelDto;
import com.webset.set.ingresos.dto.CatCtaBancoDto;
import com.webset.set.traspasos.dto.MovimientoDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.LlenaComboImpresora;

/**
 * interface business modulo impresion
 * @author Jessica Arelly Cruz Cruz
 * @since 18/05/2011
 * @Modificado Eric Medina Serrato
 * @Fecha 15/12/2015
 */
public interface ImpresionService {
	public Map<String, Object>consultarChequesPendientes(ConsultaChequesDto dtoCheques);
	public List<LlenaComboEmpresasDto>llenarComboEmpresa(int usuario);
	public List<LlenaComboImpresora>llenarComboImpresora(int caja);
	public List<LlenaComboGralDto>llenarComboGral(LlenaComboGralDto dto);
	public List<CatFirmasDto>consultarFirmasChequera(String cuenta, int banco);
	public List<LlenaComboGralDto>consultarProveedores(String texto);
	public List<MovimientoDto>consultarChequesPendientes1(ConsultaChequesDto dtoCheques);
	public Map<String, Object>ejecutarImpresionCheques(List<MovimientoDto> listCheques, ConsultaChequesDto dtoBusCheques, String idConfiguracion);
	public List<LlenaComboImpresora>consultarBancoDeCharola(int impresora, int idBanco);
	public List<ControlPapelDto>consultarFoliosPapel(int banco, int caja);
	public List<CatCtaBancoDto>consultarChequeras(int banco, int empresa);
	public List<MovimientoDto>consultarDocumentosCheques(ConsultaChequesDto dtoCheques);
	public List<LlenaComboEmpresasDto> consultarEmpresas(int usuario);
	public Map<String, Object>ejecutarProteccionCheque(List<MovimientoDto> listCheques, boolean chkH2H, boolean pbPendientes);
	public List<MovimientoDto>consultarSolicitudesReimpresion(ConsultaChequesDto dtoCheques);
	public Map<String, Object>ejecutarReimpresionCheques(List<MovimientoDto> listCheques, ConsultaChequesDto dtoBusCheques);
	public void obtenerReporteCheque(Map parameters);
	public int obtieneUltimoCheqImp(String chequera);
	public String escribirExcel(String datosGrid);
	public List<LlenaComboGralDto> llenarComboBeneficiario(LlenaComboGralDto dto);
	public List<LlenaComboGralDto> llenarComboLeyenda();
	public List<LlenaComboGralDto>llenarComboConfiguracion(); //YEC 28.01.16
	public List<LlenaComboGralDto> llenarComboBancos(int noEmpresa);
	public List<LlenaComboGralDto> llenarComboChequera(int idBanco, int noEmpresa);
	public int getUltimoImpCtrlCheque(String noEmpresa, String noBanco, String noChequera);
	public List<LlenaComboGralDto> llenarComboBancosCC(int idBanco);
	public String accionarLeyEmpresa(int noEmpresa);
}
