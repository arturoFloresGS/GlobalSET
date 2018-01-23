package com.webset.set.impresion.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.webset.set.bancaelectronica.dto.CatBancoDto;
import com.webset.set.bancaelectronica.dto.DetArchTransferDto;
import com.webset.set.impresion.dto.CatFirmasDto;
import com.webset.set.impresion.dto.ConfiguracionChequeDto;
import com.webset.set.impresion.dto.ConsultaChequesDto;
import com.webset.set.impresion.dto.ControlPapelDto;
import com.webset.set.impresion.dto.InsertaBitacoraChequesDto;
import com.webset.set.impresion.dto.LayoutProteccionDto;
import com.webset.set.ingresos.dto.CatCtaBancoDto;
import com.webset.set.interfaz.dto.GuiaContableDto;
import com.webset.set.traspasos.dto.MovimientoDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.LlenaComboImpresora;
/**
 * interface dao modulo impresion
 * @author Jessica Arelly Cruz Cruz
 * @since 18/05/2011
 * @Modificado Eric Medina Serrato
 * @Fecha 15/12/2015
 * 
 */
public interface ImpresionDao {
	//public List<Map<String, Object>> consultarDetalleChaqueAzteca(String noFolioDet);
	public List<MovimientoDto>consultarChequesPendientes(ConsultaChequesDto dtoCheques);
	public String seleccionarBHabilita();
	public List<LlenaComboEmpresasDto>llenarComboEmpresa(int usuario);
	public List<LlenaComboImpresora>llenarComboImpresora(int caja);
	public List<LlenaComboGralDto>llenarComboGral(LlenaComboGralDto dto);
	public List<CatFirmasDto>consultarFirmasChequera(String cuenta, int banco);
	public List<LlenaComboGralDto>consultarProveedores(String texto);
	public List<LlenaComboImpresora>consultarCharolasImpresora(int noImpresora, int caja);
	public List<CatCtaBancoDto>consultarImpresionContinua(int banco, String chequera);
	public List<ConfiguracionChequeDto>consultarConfiguracionCheques(int banco, String moneda, int empresa, String idConfiguracion);
	public List<LlenaComboImpresora>consultarBancoDeCharola(int impresora, int idBanco);
	public int actualizarCharolas(int banco, int impresora);
	public List<ControlPapelDto>consultarFoliosPapel(int banco, int caja);
	public List<CatFirmasDto> consultarContarFirmas(int banco, String cuenta);
	public int actualizarMovimiento(Date fechaHoy, int usuario, Date fechaChequeCapturada, Date fechaCheque,
		int noCheque, int noFolioMov, int noPapel);
	public int insertarBitacoraCheques(InsertaBitacoraChequesDto dto);
	public int actualizarCaja(int caja, int noFolioMov);
	public int actualizaControlPapel(int banco, int caja);
	public List<CatFirmasDto>consultarCatalogoFirmas(int banco, String chequera);
	public List<CatCtaBancoDto>consultarChequeras(int banco, int empresa);
	public List<MovimientoDto>consultarDocumentosCheques(ConsultaChequesDto dtoCheques);
	public List<LlenaComboEmpresasDto> consultarEmpresas(int usuario);
	public List<DetArchTransferDto> consultarTipoEnvio(String documento, double importe, int folios);
	public int actualizarDetArchTransfer(int folios);
	//public int insertarDetArchTransfer(String sArchivo2, String sBenef, double dImporte, int sFolios, Date dFecha,
	//String sChequera, int iBanco, String sConcepto, String sDocto, int iCheque);
	public int insertarDetArchTransfer(String sArchivo2, String poHeaders,String sBenef, double dImporte,
			String fecPropuesta, String idChequera, int idBanco, String sConcepto,int noEmpresa, int iCheque);
	
	public int insertarArchTransfer(String sArchivo2, int iBanco, Date dFecha, double dImporte, int iUsuario, int iNreg);
	public int actualizarMovimientoHistMovimiento(String sArchProtegido, String sFolios);
	public List<CatBancoDto> consultarPropiedadesArchivo(int banco);
	public int actualizarCatBanco(int banco);
	public List<MovimientoDto>consultarSolicitudesReimpresion(ConsultaChequesDto dtoCheques);
	public List<MovimientoDto>consultarEntregaCheque(String string);
	public int actualizarMovimientoReimpresion(Date fechaCheque, Date fechaReimprime, int noCheque, int folioDet);
	public List<LayoutProteccionDto> obtieneFechaHoy();
	public int seleccionarFolioReal(String tipoFolio);
	public int actualizarFolioReal(String tipoFolio);
	public String consultarConfiguraSet(int piIndice);
	//public List<GuiaContableDto> consultarCtaContableRubro(int noFolioDet);
	//public List<CatEquivaleCuentaContableDto> consultarEquivalenciaCtaContable(int idEmpresa, String chequera);
	public int obtieneUltimoCheqImp(String chequera);
	public int actualizaCheque(int noCheque, String sCuenta);
	public String obtieneNomCarpeta(int noEmpresa);
	public boolean esAgrupado(int noCheque);
	public List<GuiaContableDto> consultarCtaContableRubroAgrupado(int noCheque);
	public List<LlenaComboGralDto> llenarComboBeneficiario(LlenaComboGralDto dto);
	public List<LlenaComboGralDto> llenarComboLeyenda();
	public List<LlenaComboGralDto> llenarComboConfiguracion();//YEC 28.01.2016
	public List<GuiaContableDto> obtenerCtaContable(String idChequera);
	public List<ControlPapelDto> consultarFoliosPapel(int iBanco, int iCaja, String idChequera);	
	public List<ControlPapelDto> consultarSigChequera(int idBanco, String tipoFolio);
	//Control papel
	public boolean existeControlPapel(int idBanco, String tipoFolio, String idChequera , String noEmpresa, String estatus);
	public 	ControlPapelDto datosControlPapel(int idBanco, String tipoFolio, String idChequera , String noEmpresa, String estatus);
	public	int actualizarEstatusControlPapel(ControlPapelDto cp, String estatus);
	public int totalHojasControlPapel(int idBanco,String idChequera , String noEmpresa , boolean continua);
	public int obtenerFirmantes(int idBanco, String idChequera, String noFolioDet , int noFirmante);
	public List<LlenaComboGralDto> llenarComboBancos(int noEmpresa);
	public String obtieneCuentaContable(String idChequera, int idBanco, int noEmpresa, double importe);
	public int actualizaControlPapel(int idControlCheque);
	public List<LlenaComboGralDto> llenarComboChequera(int idBanco, int noEmpresa);
	public String obtenerNombreEmpresa(int noEmpresa);
	public String obtieneDescripcionCatCheque(String idConfiguracion);
	public int getUltimoImpCtrlCheque(String noEmpresa, String noBanco, String noChequera);
	public List<LlenaComboGralDto> llenarComboBancosCC(int idEmpresa);
	public List<MovimientoDto> consultarChequesPendientes2(ConsultaChequesDto dtoCheques);
	public List<Map<String, Object>> consultarDetalleChaqueAzteca(int noEmpresa, String poHeaders,
			int idBanco, String idChequera, String fecPropuesta);
	public Date obtieneFechaVencimiento();
}
