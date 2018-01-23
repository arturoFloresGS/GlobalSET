package com.webset.set.consultas.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.consultas.dto.PosicionBancariaDTO;
import com.webset.set.consultas.dto.UsuarioPlantilla;
import com.webset.set.flujos.dto.PosicionBancariaDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.RubroRegNeg;

public interface PosicionBancariaDao {

	List<PosicionBancariaDTO> consultarCriterioSeleccion(int idCriterio);
	List<LlenaComboDivisaDto> llenarComboDivisa();
	List<LlenaComboGralDto> llenarComboPlantillas(String pantalla);
	List<RubroRegNeg> consultarRubros();

	List<LlenaComboGralDto> obtenerGruposRubros(String idTipoMovto);
	List<LlenaComboGralDto> llenarComboRubro(String idGrupoRubro);
	Map<String, String> actualizaRubros(String noFolioDet, String idGrupo, String idRubro, double importe,
			String referencia, boolean actualizaTodo);
	List<PosicionBancariaDTO> consultarMovtoTes(String idBancos, String idChequeras, String noEmpresas, String fechaIni,
			String fechaFin, int seleccion, String divisa);
	List<PosicionBancariaDTO> consultarDiarioTes(String string, String string2, String string3, String fechaIni,
			String fechaFin, int seleccion, String divisa);
	List<PosicionBancariaDTO> obtieneCuentaEmpresa(String tipoMovto, String bancos, String chequeras, String empresas,
			int iTipoSelec, String fechaIni, String fechaFin, int seleccion, String divisa);
	List<PosicionBancariaDTO> obtieneSaldodiario(String bancos, String chequeras, String fecha, int noEmpresa,
			String divisa);
	//List<PosicionBancariaDTO> obtenerCuentas(String bancos, String empresas, int seleccion);
	List<PosicionBancariaDTO> obtieneFlujoEmp(String bancos, String chequeras);
	List<PosicionBancariaDTO> obtieneFichaCuenta(String tipoMovto, String bancos, String chequeras, int tipoSelect, String fechaIni,
			String fechaFin, int seleccion, String divisa);
	List<PosicionBancariaDTO> obtenerCuentas(String bancos, String empresas, int iSelec, boolean nomEmpresa);
	List<PosicionBancariaDTO> obtenerNombreEmpresas(String empresas);
	List<PosicionBancariaDTO> obtenerCuentas(String bancos, String chequeras, String empresas, int iSelec,
			boolean nomEmpresa);
	Map<String, String> insertarPlantilla(String bancos, String chequeras, String empresas, String orden,
			String nomPlantilla, String consulta, String fechaIni, String fechaFin, int seleccion, String divisa, int folio);
	int obtenerFolioPlantilla();
	UsuarioPlantilla obtenerPlantilla(int idPlantilla);
	List<PosicionBancariaDTO> obtenCuentasEmpresa(int seleccion, String bancos, String chequeras, String empresas, String divisa);
	List<PosicionBancariaDTO> obtieneSaldoFinal(String chequera, String fecha, String sDivisa);
	List<PosicionBancariaDTO> consultarDetallePosicion2(String rubro, String idChequeras, String noEmpresas,
			String divisa, String fechaIni, String fechaFin, String consulta, String tipoMovto);
	Map<String, String> eliminarPlantilla(int idPlantilla);
	
	List<PosicionBancariaDTO> consultarDetallePosicion(String idRubros, double importeIni, double importeFin,
			String idBancos, String idChequeras, String noEmpresas, String divisa, String fechaIni, String fechaFin,
			int optConsulta, boolean bSinRubros);
	UsuarioPlantilla obtenerPlantilla(String nombrePlantilla);


}
