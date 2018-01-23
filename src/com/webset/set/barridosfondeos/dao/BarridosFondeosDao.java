package com.webset.set.barridosfondeos.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.barridosfondeos.dto.BusquedaFondeoDto;
import com.webset.set.barridosfondeos.dto.FilialDto;
import com.webset.set.barridosfondeos.dto.FondeoAutomaticoDto;
import com.webset.set.barridosfondeos.dto.ParametroDto;
import com.webset.set.barridosfondeos.dto.TmpTraspasoFondeoDto;
import com.webset.set.coinversion.dto.ArbolEmpresaDto;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.utils.tools.Utilerias;

public interface BarridosFondeosDao {  //
	public List<LlenaComboEmpresasDto>obtenerEmpresas(int idUsuario, boolean bMantenimiento);
	
	public List<FilialDto> obtenerEmpresasFiliales (int noEmpresa, int idUsuario);
	
	public List<LlenaComboGralDto> obtenerTipoArbol (boolean bExistentes, String tipoOperacion);
	
	/**
	 * Devuelve el valor (Igual o diferente) de un arbol dado
	 * @param idArbol
	 * @return I - igual; D - Diferente
	 */
	public String obtenerIgualDif (int idArbol);
	 
	/*--- configuracion de arboles ---*/
	public List<LlenaComboGralDto> consultarChequeraFondeo(int iIdEmpresa, String sIdDivisa, int iIdBanco);
	public List<LlenaComboGralDto> consultarArboles(boolean bExistentes);
	public List<LlenaComboGralDto> consultarArbolesInterempresas(boolean bExistentes);
	public List<LlenaComboGralDto> consultarArbolesHijos(boolean bExistentes, int idRaiz);
	public List<LlenaComboGralDto> consultarArbolesHijosInterempresas(boolean bExistentes, int idRaiz);
	public List<LlenaComboGralDto> consultarEmpresasRaiz(boolean bExistentes, int idArbol);
	public List<LlenaComboGralDto> consultarEmpresasRaizInterempresas(boolean bExistentes);
	public List<LlenaComboGralDto> consultarEmpresasHijo(int iEmpRaiz);
	public List<LlenaComboGralDto> consultarEmpresasHijoIn(int iEmpRaiz);
	public List<LlenaComboGralDto> consultarEmpresaPadre(int iEmpresaRaiz);
	

	public List<LlenaComboGralDto> consultarEmpresaPadreIn(int iEmpresaRaiz);
	
	public List<LlenaComboGralDto> consultarEmpresasHijos(int iEmpresaRaiz, int iEmpPadre);
	public List<LlenaComboGralDto> consultarEmpresasNietos(int iEmpresaRaiz, int iEmpHijo);
	public List<LlenaComboGralDto> consultarEmpresasBisnietos(int iEmpresaRaiz, int iEmpNieto);
	public List<LlenaComboGralDto> consultarEmpresasTataranietos(int iEmpresaRaiz, int iEmpBisnieto);
	public List<LlenaComboGralDto> consultarEmpresasChosnoUno(int iEmpresaRaiz, int iEmpTataranieto);
	public List<LlenaComboGralDto> consultarEmpresasChosnoDos(int iEmpresaRaiz, int iEmpChosno1);
	public List<LlenaComboGralDto> consultarEmpresasChosnoTres(int iEmpresaRaiz, int iEmpChosno2);
	public List<LlenaComboGralDto> consultarEmpresasChosnoCuatro(int iEmpresaRaiz, int iEmpChosno3);
	
	
	
	public List<LlenaComboGralDto> consultarEmpresasHijosIn(int iEmpresaRaiz, int iEmpPadre);//
	public List<LlenaComboGralDto> consultarEmpresasNietosIn(int iEmpresaRaiz, int iEmpHijo);
	public List<LlenaComboGralDto> consultarEmpresasBisnietosIn(int iEmpresaRaiz, int iEmpNieto);
	public List<LlenaComboGralDto> consultarEmpresasTataranietosIn(int iEmpresaRaiz, int iEmpBisnieto);
	public List<LlenaComboGralDto> consultarEmpresasChosnoUnoIn(int iEmpresaRaiz, int iEmpTataranieto);
	public List<LlenaComboGralDto> consultarEmpresasChosnoDosIn(int iEmpresaRaiz, int iEmpChosno1);
	public List<LlenaComboGralDto> consultarEmpresasChosnoTresIn(int iEmpresaRaiz, int iEmpChosno2);
	public List<LlenaComboGralDto> consultarEmpresasChosnoCuatroIn(int iEmpresaRaiz, int iEmpChosno3);

	
	
	
	public List<LlenaComboGralDto> consultarEmpresaConcentradora(int iIdUsuario);
	public List<LlenaComboGralDto> consultarBancosEmpresa(int iIdEmpresa, String sIdDivisa);
	public List<LlenaComboGralDto> consultarEmpresasArbol(int iIdEmpresa, String sCondicion);
	public List<LlenaComboGralDto> consultarEmpresasArbol();
	public void eliminarTmpArbol(int iIdUsuario);
	public List<Map<String, Object>> consultarTmpArbol(int iIdUsuario);
	public List<Integer> consultarEmpArbolExis(int iIdEmpRaiz, int iIdEmpresa);
	
	

	public List<Integer> consultarEmpArbolExisIn(int iIdEmpRaiz, int iIdEmpresa);
	
	public void insertarArbolEmpresa(String sCampos, String sValoresCampos, String nombreArbol, boolean insertaArbol);
	
	public void insertarArbolEmpresaIn(String sCampos, String sValoresCampos, String nombreArbol, boolean insertaArbol);
	
	public void actualizarArbEmpresaEstatusPadre(int iIdEmpresaRaiz, int iIdEmpresa, String sEstatus);
	
	public void actualizarArbEmpresaEstatusPadreIn(int iIdEmpresaRaiz, int iIdEmpresa, String sEstatus);
	
	public void insertarTmpArbol(int iIdUsuario, int iSecuencia, String sNomEmpresa, String sTipo);
	public List<ArbolEmpresaDto> consultarValidaPadre(int iIdEmpresaRaiz, int iIdEmpresa);
	public List<ArbolEmpresaDto> consultarValidaPadreIn(int iIdEmpresaRaiz, int iIdEmpresa);
	
	public double consultarSaldoEmpresaArbol(int iIdEmpresa);
	public double consultarSaldoEmpresaArbolIn(int iIdEmpresa);
	
	public int eliminarNodoArbolEmpresa(int iIdEmpresaRaiz, int iIdEmpresa);

	public int eliminarNodoArbolEmpresaIn(int iIdEmpresaRaiz, int iIdEmpresa);
	
	public List<String> consultarCampoArbolEmpresa(int iIdEmpresaRaiz, int iIdEmpresa);

	public List<String> consultarCampoArbolEmpresaIn(int iIdEmpresaRaiz, int iIdEmpresa);
	

	public int consultarNumeroEmpresasArbolIn(int iIdEmpresaRaiz, int iIdEmpresa);
	
	public int consultarNumeroEmpresasArbol(int iIdEmpresaRaiz, int iIdEmpresa);
	public Integer consultarRutaEmpArbolExis(int iIdEmpRaiz, int iIdEmpresaPadre, int iIdEmpresa);

	public Integer consultarRutaEmpArbolExisIn(int iIdEmpRaiz, int iIdEmpresaPadre, int iIdEmpresa);//angel--modificarlo para arbol_empresa
	
	public List<LlenaComboGralDto> consultarTipoOperacion();
	public void insertarArbolEmpresaFondeo(int idEmpresaRaiz, int empresaOrigen, int empresaDestino,
			int tipoOperacion,  String tipoValor, double valor, String continua, int orden, String descArbol);
	
	
	public void insertarArbolEmpresaIn(int idEmpresaRaiz, int empresaOrigen, int empresaDestino,
			int tipoOperacion,  String tipoValor, double valor, String continua, int orden, String descArbol);
	
	
	/*--- configuracion de arboles ---*/
	
	/**
	 * Consulta los bancos de la empresa raiz de un arbol
	 * @param idEmpresa
	 * @param idDivisa
	 * @return
	 */
	public List<LlenaComboGralDto> consultarBancosRaiz(int idEmpresa, String idDivisa);
	
	/**
	 * Consulta las chequeras pagadoras, concentradoras o mixtas que tiene la empresa raiz de un arbol
	 * @param idEmpresa
	 * @param idBanco
	 * @param idDivisa
	 * @return
	 */
	public List<LlenaComboGralDto> consultarChequeraRaiz(int idEmpresa, int idBanco, String idDivisa);
	
	/**
	 * Consulta las empresas que pertenecen a un arbol de fondeo
	 * @param idEmpresaRaiz
	 * @return datos para llenado de combo
	 */
	public List<LlenaComboGralDto> consultarEmpresaArbolFondeo (int idEmpresaRaiz);
	
	/**
	 * Consulta las necesidades de fondeo y los saldos para barrido de las empresas de un arbol
	 * @param busquedaFondeoDto
	 * @return
	 */
	public List<FondeoAutomaticoDto> consultarFondeoArbol (BusquedaFondeoDto busquedaFondeoDto);
	
	/**
	 * Consulta la existencia de chequera para la empresa, banco y divisa
	 * @param noEmpresa
	 * @param idBanco
	 * @param idDivisa
	 * @return chequera (idChequera)
	 */
	public String consultarChequera(int noEmpresa, int idBanco, String idDivisa);
	
	/**
	 * Inserta los datos del traspaso de barrido/fondeo para su ejecucion
	 * @param tmpTraspasoFondeo
	 */
	public int consultarBanco(int noEmpresa, int idBanco, String idDivisa);
	/**
	 * 
	 */
	/**
	 * Inserta los datos del traspaso de barrido/fondeo para su ejecucion
	 * @param tmpTraspasoFondeo
	 */
	public void insertarTmpTraspaso (TmpTraspasoFondeoDto tmpTraspasoFondeo) throws Exception; 
	
	/**
	 * Obtiene los datos para el fondeo a partir del arbol
	 * @return
	 */
	public List<Map<String, Object>> consultarTmpArbolFondeo ();
	
	/**
	 * Obtiene el valor del folio para el tipo dado, a partir de la tabla FOLIO
	 * @param tipo
	 * @return
	 */
	public Long obtenerFolioReal (String tipo);
	
	/**
	 * Obtiene banco y chequera para una empresa y divisa
	 * @param noEmpresa
	 * @param idDivisa
	 * @return
	 */
	public Map<String,Object> consultarDatosChequera (int noEmpresa, String idDivisa);
	
	/**
	 * Inserta en la tabla PARAMETRO
	 * @param parametroDto
	 * @throws Exception
	 */
	public void insertarParametro (ParametroDto parametroDto) throws Exception;
	
	/**
	 * Obtiene la cuenta de la empresa dada (tabla cuenta)
	 * @param noEmpresa
	 * @return
	 */
	public int buscarCuentaEmpresa (int noEmpresa);
	
	public Map<String, Object> ejecutarGenerador(GeneradorDto generadorDto);
	
	/**
	 * Registra la informacion para el control de los fondeos y hacer cuadres
	 * @param fondeoDto
	 * @return no_fondeo insertado
	 */
	public int registrarControlFondeo(FondeoAutomaticoDto fondeoDto, BusquedaFondeoDto busquedaDto);
	
	/**
	 * Actualiza el no_docto de control_fondeos
	 * @param idTipoArbol
	 * @param noEmpresaPadre
	 * @param noEmpresaHijo
	 * @param idChequeraPadre
	 * @param idChequeraHijo
	 * @param noFondeo
	 * @param noDocto
	 */
	public void actualizarControlFondeos (int idTipoArbol, int noEmpresaPadre, int noEmpresaHijo, 
			String idChequeraPadre, String idChequeraHijo, int noFondeo, int noDocto);
	
	/**
	 * Elimina los registros de TMP_TRASPASO_FONDEO
	 * @throws Exception
	 */
	public void borrarTmpTraspasoFondeo() throws Exception;
	
	/**
	 * Obtiene la informacion de los pagos pendientes
	 * @param dtoBus
	 * @return
	 */
	public Map<String,Object> obtenerDatosPadre(int noEmpresa, int idBanco, String idChequera, int idArbol);
	
	/**
	 * 
	 * Obtiene la informacion de los pagos pendientes
	 * @param dtoBus
	 * @return
	 */
	public List<MovimientoDto> consultarPagos(BusquedaFondeoDto dtoBus);
	
	/**
	 * Obtiene los datos para el reporte de arbol de empresas
	 * @param noEmpresaRaiz
	 * @return Mapa con: empresaOrigen, EmpresaDestino, tipoOperacion, tipoValor,
	 * valor, cuentaOrigen, cuentaDestino
	 */
	public List<Map<String, Object>> obtenerReporteArbolEmpresa(int noEmpresaRaiz);
	
	/**
	 * Obtiene los datos para el reporte de fondeo automatico
	 * @param idUsuario
	 * @return
	 */
	public List<Map<String, Object>> obtenerReporteFondeo(int idUsuario, String tipoOperacion);
	
	/**
	 * Obtiene los datos para el reporte de la estructura de arbol de empresas
	 * @param noEmpresaRaiz
	 * @param idUsuario
	 * @return
	 */
	public List<Map<String, Object>> obtenerReporteArbolEmpresaEst(int noEmpresaRaiz, int idUsuario);
	
	/**
	 * Obtiene el reporte de empresas filiales de una concentradora
	 * @param noEmpresa
	 * @return
	 */
	public List<Map<String, Object>> obtenerReporteFiliales (int noEmpresa);
	
	/**
	 * Obtiene los datos para el reporte de barridos y fondeos
	 * @param noEmpresa
	 * @param idUsuario
	 * @param fecha
	 * @return
	 */
	public List<Map<String, Object>> obtenerReporteBarridosFondeos (int noEmpresa, int idUsuario, String fecha);
	
	/**
	 * Obtiene los datos para el reporte de barridos y fondeos
	 * @param noEmpresa
	 * @param idUsuario
	 * @param fecha
	 * @return
	 */
	public List<Map<String, String>> obtenerReporteBarridosFondeosStr (int noEmpresa, int idUsuario, String fecha);
	
	/**
	 * Obtiene los datos para el reporte de Cuadre de Fondeo
	 * @param noEmpresa - empresa raiz
	 * @param idUsuario - usuario que consulta
	 * @param fecha 
	 * @return lista de mapas de cadena - todos los datos se presentan como String
	 */
	public List<Map<String, String>> obtenerReporteCuadre (int noEmpresa, int idUsuario, String fecha);

	public String getIdChequeraHijo(int noEmpresaDestino, String idDivisa, int lsBanco, int orden);

	public int getIdBanco(String lsChequera);
	
	public Double getSaldoEmpresa(Integer noEmpresa, Integer idTipoSaldo);
	
	public Integer buscarCuentaEmpresa(Integer noEmpresa, String divisa);
	public int consultarCuentaEmpresa(int noEmpresa);
	
	public String consultarChequeraTraspaso(Integer noEmpresa, String divisa);

	public void consultarMontoAct(int monto, int idEmpresa, int idRaiz);

	public int buscaAbono(int tipoSaldo);
}
