package com.webset.set.consultas.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webset.set.consultas.dto.PosicionBancariaDTO;
import com.webset.set.consultas.dto.UsuarioPlantilla;
import com.webset.set.flujos.dto.PosicionBancariaDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.RubroRegNeg;

public interface PosicionBancariaService {

	List<PosicionBancariaDTO> consultarCriterioSeleccion(int idCriterio);
	List<LlenaComboDivisaDto> llenarComboDivisa();
	List<LlenaComboGralDto> llenarComboPlantillas(String pantalla);
	List<RubroRegNeg> consultarRubros();

	List<LlenaComboGralDto> obtenerGruposRubros(String tipoMovto);
	List<LlenaComboGralDto> llenarComboRubro(String idGrupoRubro);
	Map<String, String> actualizaRubros(String noFolioDet, String idGrupo, String idRubro, double importe,
			String referencia, boolean actualizaTodo);
	String exportaExcelPosicion(String datos);
	List<PosicionBancariaDTO> consultarMovtoTes(List<String> listBancos, List<String> listChequeras,
			List<String> listEmpresas, String fechaIni, String fechaFin, int seleccion, String divisa);
	List<PosicionBancariaDTO> consultarDiarioTes(List<String> listBancos, List<String> listChequeras,
			List<String> listEmpresas, String fechaIni, String fechaFin, int seleccion, String divisa);
	//PosicionBancariaDTO posicionBancariaCash(List<String> listBancos, List<String> listChequeras,
		//	List<String> listEmpresas, String fechaIni, String fechaFin, int seleccion, String divisa, int tipoReporte);
	List<PosicionBancariaDTO> posicionXEmp(List<String> listBancos, List<String> listChequeras, List<String> listEmpresas,
			String fechaIni, String fechaFin, int seleccion, String divisa, int tipoReporte);
	List<HashMap<String, String>> posicionXEmp2(List<String> listBancos, List<String> listChequeras,
												List<String> listEmpresas, String fechaIni, String fechaFin,
												int seleccion, String divisa, int tipoReporte);
	List<HashMap<String, String>> posicionXCta2(List<String> listBancos, List<String> listChequeras,
												List<String> listEmpresas, String fechaIni, 
												String fechaFin, int seleccion, String divisa, int tipoReporte);
	List<PosicionBancariaDTO> posicionXCta(List<String> listBancos, List<String> listChequeras,
			List<String> listEmpresas, String fechaIni, String fechaFin, int seleccion, String divisa, int tipoReporte);
	Map<String, String> insertarPlantilla(List<String> listBancos, List<String> listChequeras,
			List<String> listEmpresas, List<String> listOrden, String nomPlantilla, String consulta, String fechaIni,
			String fechaFin, int seleccion, String divisa);
	UsuarioPlantilla obtenerPlantilla(int idPlantilla);
	List<PosicionBancariaDTO> repSaldos(List<String> listBancos, List<String> listChequeras, List<String> listEmpresas,
			String fechaIni, String fechaFin, int seleccion, String divisa, int tipoReporte);
	List<HashMap<String, String>> repSaldos2(List<String> listBancos, List<String> listChequeras,
			List<String> listEmpresas, String fechaIni, String fechaFin, int seleccion, String divisa, int tipoReporte);
	List<PosicionBancariaDTO> consultarDetallePosicion2(String rubro, List<String> listChequeras,
			List<String> listEmpresas, String divisa, String fechaIni, String fechaFin, String optConsulta,
			String tipoMovto);
	Map<String, String> eliminarPlantilla(int idPlantilla);
	String exportaExcelPosicion2(String datos);
	String exportaExcel(String datos, String tipoConsulta);
	List<PosicionBancariaDTO> consultarDetallePosicion(List<String> listRubros, double importeIni, double importeFin,
			List<String> listBancos, List<String> listChequeras, List<String> listEmpresas, String divisa,
			String fechaIni, String fechaFin, int optConsulta, boolean bSinRubros); 

}
