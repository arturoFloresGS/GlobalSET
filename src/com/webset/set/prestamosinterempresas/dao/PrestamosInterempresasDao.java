package com.webset.set.prestamosinterempresas.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.webset.set.prestamosinterempresas.dto.ArbolEmpresaDto;
import com.webset.set.prestamosinterempresas.dto.CuentaDto;
import com.webset.set.prestamosinterempresas.dto.HistSaldoDto;
import com.webset.set.prestamosinterempresas.dto.InteresPrestamoDto;
import com.webset.set.prestamosinterempresas.dto.ParamComunDto;
import com.webset.set.prestamosinterempresas.dto.ParamInteresPresNoDoc;
import com.webset.set.prestamosinterempresas.dto.ParamRepIntNetoDto;
import com.webset.set.prestamosinterempresas.dto.PersonaDto;
import com.webset.set.prestamosinterempresas.dto.RetInteresPresNoDoc;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.ParametroDto;
import com.webset.set.utilerias.dto.TmpEdoCuentaDto;

public interface PrestamosInterempresasDao {
	
	public List<LlenaComboGralDto> consultarEmpresasRaiz(boolean bExistentes);
	public List<LlenaComboGralDto> consultarEmpresasHijo(int iEmpRaiz);
	public List<LlenaComboGralDto> consultarEmpresaPadre(int iEmpresaRaiz);
	public List<LlenaComboGralDto> consultarEmpresasHijos(int iEmpresaRaiz, int iEmpPadre);
	public List<LlenaComboGralDto> consultarEmpresasNietos(int iEmpresaRaiz, int iEmpHijo);
	public List<LlenaComboGralDto> consultarEmpresasBisnietos(int iEmpresaRaiz, int iEmpNieto);
	public List<LlenaComboGralDto> consultarEmpresasTataranietos(int iEmpresaRaiz, int iEmpBisnieto);
	public List<LlenaComboGralDto> consultarEmpresasChosnoUno(int iEmpresaRaiz, int iEmpTataranieto);
	public List<LlenaComboGralDto> consultarEmpresasChosnoDos(int iEmpresaRaiz, int iEmpChosno1);
	public List<LlenaComboGralDto> consultarEmpresasChosnoTres(int iEmpresaRaiz, int iEmpChosno2);
	public List<LlenaComboGralDto> consultarEmpresasChosnoCuatro(int iEmpresaRaiz, int iEmpChosno3);
	public List<LlenaComboGralDto> consultarEmpresaConcentradora(int iIdUsuario);
	public List<LlenaComboGralDto> consultarBancosEmpresa(int iIdEmpresa, String sIdDivisa);
	public List<LlenaComboGralDto> consultarEmpresasArbol(int iIdEmpresa, String sCondicion);
	public List<LlenaComboGralDto> consultarChequeras(int iIdEmpresa, int iIdBanco, String sIdDivisa);
	public String consultarTipoDivisa(int iIdBanco, String sIdChequera);
	public double consultarSaldoFinal(int iIdEmpresa, int iIdBanco, String sIdChequera);
	public double consultarImporteChequesXEntregar(int iIdEmpresa, int iIdBanco, String sIdChequera, String sIdDivisa);
	public List<Map<String, Object>> consultarSaldoFinalYCoinversion(int iIdBanco, String sIdChequera, int iNoEmpresa,
			int iNoEmpInv, String sIdDivisa);
	public List<ArbolEmpresaDto> consultarParentesco(int iIdEmpresa);
	public List<LlenaComboGralDto> consultarBancoChequera(int iIdEmpresa);
	public int consultarNumCta(int iIdEmpresa);
	public int consultarNumPersona(int iIdEmpresa);
	public int consultarDivisaSoin(String sIdDivisa);
	public int consultarCtaCredInter(int iEmpresa, int iLinea, int iPersona, int iCuenta);
	public int insertarCtaCredInter(CuentaDto dto);
	public int seleccionarFolioReal(String tipoFolio);
	public int actualizarFolioReal(String tipoFolio);
	public int insertarParametroTrasCred(ParametroDto dto);
	public int consultarNumCtaPagadora(int iEmpresa);
	public String consultarNomEmpresa(int iNoEmpresa);
	@SuppressWarnings("unchecked")
	public Map ejecutarGenerador(GeneradorDto dto);
	public List<LlenaComboGralDto> consultarEmpresasArbol();
	public List<RetInteresPresNoDoc> consultarInteresPresNoDoc(ParamInteresPresNoDoc dto);
	public int insertarParametroCalInt(ParametroDto dto);
	public List<LlenaComboGralDto> consultarCheqTraspaso(int iEmpresa, String sIdDivisa);
	public List<LlenaComboGralDto> consultarCheqMadreMismoBan(int iEmpresa, String sIdDivisa, int iIdBanco);
	public int consultarCtaCoinversora(int iNoEmpresa);
	public double consultarSaldoCoinversion(int iEmpCoinv, int iEmpresa, String sDivisa);
	public int insertarInteresPrestamo(InteresPrestamoDto dto);
	public List<LlenaComboGralDto> consultarPeriodosPrestamos();
	public List<Map<String, Object>> consultarReportePresNoDoc(ParamComunDto dto);
	public List<LlenaComboGralDto> consultarAnioInteresPres();
	public List<LlenaComboGralDto> consultarMes(int iAnio);
	public List<Map<String, Object>> consultarRepInteresNeto(ParamRepIntNetoDto dto);
	public List<LlenaComboGralDto> consultarSectores();
	public List<LlenaComboGralDto> consultarArbolEmpresaUsuario(int iIdUsuario);
	public double consultarSaldoInicial(ParamComunDto dto);
	public double consultarSaldoFinal(ParamComunDto dto);
	public List<InteresPrestamoDto> consultarValoresDeCredito(int iIdEmpresa, int iMes, 
			int iAnio, String sIdDivisa);
	public List<Map<String, Object>> consultarReporteEstadoDeCuenta(ParamComunDto dto);
	public Date consultarFechaMinMax(String sTipo, int iNoEmpresa, int iNoCuenta, 
			int iNoLinea, Date dFecIni, Date dFecFin);
	public int borrarTmpEdoCuenta(int iIdUsuario);
	public List<HistSaldoDto> consultarSaldoDepRet(int iNoEmpresa, int iNoCuenta, int iNoLinea, Date dFecha);
	public int insertarTmpEdoCuenta(TmpEdoCuentaDto dto);
	public List<Map<String, Object>> consultarSaldoDepRetDetalle(boolean bFecIgualHoy, int iNoEmpresa, int iNoCuenta,
			String sIdDivisa, Date dFecIni, Date dFecFin);
	public List<Map<String, Object>> consultarSaldoFin(boolean bFecFinIgualHoy, int iNoEmpresa, int iNoLinea,
			int iNoCuenta, Date dFecHoy, Date dFecFin);
	public List<PersonaDto> consultarDatosEmpresa(int iNoEmpresa);
	public List<Map<String, Object>> consultarReporteEdoCuentaDetallado(int iIdUsuario);
	public List<Map<String, Object>> consultarReporteSolicitudesDeCredito(int iEmpresaInf, int iEmpresaSup, 
			String sIdDivisa, boolean bCredito);
	public List<Map<String, Object>> consultarFlujoNetoSinSectores(ParamComunDto dto);
	public List<Map<String, Object>> consultarFlujoNetoSectores(ParamComunDto dto);
	public void insertarTmpArbol(int iIdUsuario, int iSecuencia, String sNomEmpresa, String sTipo);
	public void eliminarTmpArbol(int iIdUsuario);
	public List<Map<String, Object>> consultarTmpArbol(int iIdUsuario);
	public List<Integer> consultarEmpArbolExis(int iIdEmpRaiz, int iIdEmpresa);
	public void insertarArbolEmpresa(String sCampos, String sValoresCampos);
	public void actualizarArbEmpresaEstatusPadre(int iIdEmpresaRaiz, int iIdEmpresa, String sEstatus);
	public List<ArbolEmpresaDto> consultarValidaPadre(int iIdEmpresaRaiz, int iIdEmpresa);
	public double consultarSaldoEmpresaArbol(int iIdEmpresa);
	public int eliminarNodoArbolEmpresa(int iIdEmpresaRaiz, int iIdEmpresa);
	public List<String> consultarCampoArbolEmpresa(int iIdEmpresaRaiz, int iIdEmpresa);
	public int consultarNumeroEmpresasArbol(int iIdEmpresaRaiz, int iIdEmpresa);
	
	
}
