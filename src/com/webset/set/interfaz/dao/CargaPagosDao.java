package com.webset.set.interfaz.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.interfaz.dto.CargaPagosDto;

@SuppressWarnings("unchecked")
public interface CargaPagosDao 
{
	public List<CargaPagosDto> llenaComboEmpresas(int noUsuario);	
	public String consultarConfiguraSet(int indice);	
	public List<CargaPagosDto> sacaHouseBank(String psHouseBank);
	public List<CargaPagosDto> obtieneNombreEmpresa(int noEmpresa);
	public List<CargaPagosDto> obtieneFormaPago(String idFormaPago, boolean esNumero);
	public List<CargaPagosDto> obtieneBancoBenef(int bancoBenef);
	public int obtieneMaximoNumeroBanco();
	public int insertaBanco(CargaPagosDto objCargaPagosDto);
	public List<CargaPagosDto> obtieneEquivaleDivisa(String origen, String divisaERP, String divisaSET);
	public List<CargaPagosDto> obtieneRubroCorrecto(String noBenef);
	public int obtieneNumeroCaja(int idCaja);
	public List<CargaPagosDto> obtenerBancoYChequera(CargaPagosDto objCargaPagosDto);
	public List<CargaPagosDto> buscaRubroPersona (String noBenef);
	public List<CargaPagosDto> existePersonaZinterprov(int noEmpresa, String noBenef);
	public int limpiaZinterprov(int noEmpresa, String noBenef);
	public int insertaDatosPersona(List<Map<String, String>> objCamposGrid, int posicion, int idRubro);	
	public int actualizaDatosPersona(List<Map<String, String>> objCamposGrid, int posicion, int idRubro);
	public int borraFormaPago(String noBenef, int noEmpresa, String mandante);
	public int insertaFormaPago(String noBenef, int noEmpresa, String mandante, int formaPago);
	public boolean buscaChequeraCtasBanco(List<Map<String, String>> objCamposGrid, int posicion);
	public int buscaChequeraZimpcheqprov(List<Map<String, String>> objCamposGrid, int posicion);
	public int actualizaZimpcheqprov(List<Map<String, String>> objCamposGrid, int posicion, String psEstatus);
	public int insertaZimpcheqprov(List<Map<String, String>> objCamposGrid, int posicion, String psEstatus);
	public int buscaRegistroZimpFact(List<Map<String, String>> objCamposGrid, int posicion);
	public int eliminaRegistroZimpFact(List<Map<String, String>> objCamposGrid, int posicion);
	public int actualizaRegistroZimpFact(List<Map<String, String>> objCamposGrid, int posicion, String concepto, String refernciaCie);
	public int insertaRegistroZimpFact(List<Map<String, String>> objCamposGrid, int posicion, String concepto, String referenciaCie, String estatus);
//	public List<CargaPagosDto>buscaMovtosAInsertar(List<Map<String, String>> objCamposGrid, int posicion);
	public Map importaProveedor(int usuario, int noEmpresa);
	public Map importaMovimientos(int usuario, String noEmpresa, int valor);	
	public List<CargaPagosDto> buscaCtasBanco(String noBenef);
	public List<CargaPagosDto> sacaFechaModifMax(String noBenef);
	public int actualizaEstatusAlta(String noBenef, String estatus, String fechaModif);
	public int seleccionaFolio(String tipoFolio);
	public int actualizaFolio(String tipoFolio);
	public List<CargaPagosDto> selectImportaCP(int iTipoPago, String fecHoy, int usuarioAlta, String noEmpresaParam, String estatus, String fecIni, String fecFin);
	public List<CargaPagosDto> buscaCheqPag();
	public int actualizaDatosBcoCheq(List<CargaPagosDto> result, int i);
	public int actDatosPropuesta(List<Map<String, String>> gridDatos, int i);
	public int actEstatusPropuesta();
	public int buscaDatosBenef(List<Map<String, String>> gridDatos, int i);
	public int validaFacultad(int facultad);
	
	public List<CargaPagosDto> llenaGridCXC(int noEmpresa, String estatus);
	public int existeCXC(List<Map<String, String>> objCamposGrid, int i);
	public int insertaRegistrosCXC(List<Map<String, String>> objCamposGrid, int i);
	public int modificaRegistrosCXC(List<Map<String, String>> objCamposGrid, int i, String estatus, String cusaRech);
	public List<Map<String, String>> reporteInterfaces(String tipoValor, String fecHoy, String usuarioAlta,
			String idEmpresa, String estatus, String fecIni, String fecFin);
	
}
