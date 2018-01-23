package com.webset.set.barridosfondeos.service;

import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;

import com.webset.set.barridosfondeos.dto.BusquedaFondeoDto;
import com.webset.set.barridosfondeos.dto.FilialDto;
import com.webset.set.barridosfondeos.dto.FondeoAutomaticoDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;

public interface BarridosFondeosService {
	/**
	 * Obtiene las empresas concentradoras del usuario
	 * @param idUsuario
	 * @param bMantenimiento
	 * @return
	 */
	public List<LlenaComboEmpresasDto>obtenerEmpresas(int idUsuario, boolean bMantenimiento);
	
	/**
	 * Obtiene las empresas filiales de alguna concentradora y usuario
	 * @param noEmpresa
	 * @param idUsuario
	 * @return
	 */
	public List<FilialDto>obtenerEmpresasFiliales(int noEmpresa, int idUsuario);
	
	/**
	 * Obtiene los datos de las empresas pertenecientes a un arbol de fondeo para llenado de combo
	 * @param noEmpresaRaiz
	 * @return datos para combo
	 */
	public List<LlenaComboGralDto> obtenerEmpresasArbolFondeo (int noEmpresaRaiz);
	
	public List<LlenaComboGralDto> obtenerTipoArbol (boolean bExistentes, String tipoOperacion);
	
	public String obtenerIgualDiferente(int tipoArbol);
	
	/*--- config de arboles ---*/
	public List<LlenaComboGralDto> consultarArboles(boolean bExistentes);
	public List<LlenaComboGralDto> consultarArbolesHijos(boolean bExistentes, int idRaiz);
	public List<LlenaComboGralDto> consultarArbolesHijosInterempresas(boolean bExistentes, int idRaiz); 
	public List<LlenaComboGralDto> consultarArbolesInterempresas(boolean bExistentes); 
	public List<LlenaComboGralDto> consultarArbolesInterempresas1(boolean bExistentes);
	public List<LlenaComboGralDto> obtenerEmpresasRaiz(boolean bExistentes, int idArbol);
	public List<LlenaComboGralDto> obtenerEmpresasRaizInterempresas(boolean bExistentes);
	public List<LlenaComboGralDto> obtenerEmpresasHijo(int iEmpresaRaiz);
	public List<LlenaComboGralDto> obtenerEmpresasHijoIn(int  iEmpresaRaiz);
	public String obtenerArbolEmpresa(int iEmpresaRaiz);
	public String obtenerArbolEmpresaIn(int iEmpresaRaiz);
	public String obtenerArbolEmpresaInterempresas(int iEmpresaRaiz);
	public List<LlenaComboGralDto> obtenerEmpresaConcentradora();
	public List<LlenaComboGralDto> obtenerEmpresasArbol();
	public JRDataSource obtenerReporteArbolEmpresa();
	public String agregarNodosArbol(boolean bNvoHijo, String sRuta,
			int iIdEmpresaRaiz, int iIdEmpresa, double uMonto, String nombreArbol,
			String tipoValor, int tipoOperacion, int iIdEmpresaPadre);
	
	

	
	
	public String agregarNodosArbolIn(boolean bNvoHijo, String sRuta,
			int iIdEmpresaRaiz, int iIdEmpresa, double uMonto, String nombreArbol,/*
			String tipoValor,*/ int tipoOperacion, int iIdEmpresaPadre);

	public String eliminarNodosArbolIn(int iIdEmpresaRaiz, int iIdEmpresaActual, int iIdEmpresaPadre);
	public String actualizarMonto( int monto, int  idEmpresa,int  idRaiz);
	
	public String eliminarNodosArbol(int iIdEmpresaRaiz, int iIdEmpresaActual, int iIdEmpresaPadre);
	public List<LlenaComboGralDto> consultarTipoOperacion();
	/*--- termina config de arboles ---*/
	public List<LlenaComboGralDto> consultarBancosRaiz(int idEmpresa, String idDivisa);
	public List<LlenaComboGralDto> llenarComboChequeraFondeo(int iIdEmpresa, String sIdDivisa, int iIdBanco);
	
	/**
	 * Consulta las chequeras que tiene la empresa raiz del arbol en un banco y divisa (para combo de chequeras)
	 * @param idEmpresa
	 * @param idBanco
	 * @param idDivisa
	 * @return
	 */
	public List<LlenaComboGralDto> consultarChequerasRaiz(int idEmpresa, int idBanco, String idDivisa);
	
	/**
	 * Consulta las necesidades de fondeo de las empresas de un arbol
	 * @param busquedaFondeoDto
	 * @return
	 */
	public List<FondeoAutomaticoDto> consultarFondeoAutomatico (BusquedaFondeoDto busquedaFondeoDto);
	
	/**
	 * Ejecuta los fondeos automaticos seleccionados
	 * @param fondeos
	 * @param generales
	 * @return Mensaje al usuario
	 */
	public String prepararFondeoAutomatico (List<FondeoAutomaticoDto> fondeos, BusquedaFondeoDto generales);	
	/**
	 * Ejecuta los fondeos automaticos seleccionados
	 * @param fondeos
	 * @param generales
	 * @return Mensaje al usuario
	 */
	public String ejecutarFondeoAutomatico (List<FondeoAutomaticoDto> fondeos, BusquedaFondeoDto generales);
	
	/**
	 * Ejecuta los barridos seleccionados
	 * @param fondeos - detalle: los barridos que se debe ejecutar
	 * @param generales
	 * @return Mensaje al usuario
	 */
	public String ejecutarBarridoAutomatico (List<FondeoAutomaticoDto> barridos, BusquedaFondeoDto generales);
	
	/**
	 * Obtiene los pagos pendientes
	 * @param dtoBus
	 * @return
	 */
	public List<MovimientoDto> obtenerPagos(BusquedaFondeoDto dtoBus);
	
}
