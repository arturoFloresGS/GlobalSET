package com.webset.set.utilerias.service;

import java.util.List;
import java.util.Map;

import com.webset.set.egresos.dto.ConsultaPropuestasDto;
import com.webset.set.utilerias.dto.AcreedorDoctoDto;
import com.webset.set.utilerias.dto.CondicionPagoDto;
import com.webset.set.utilerias.dto.DoctoTesoreriaDto;
import com.webset.set.utilerias.dto.EmpresaRegNegDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.PlanPagosDto;
import com.webset.set.utilerias.dto.ReglasNegocioDto;
import com.webset.set.utilerias.dto.RubroRegNeg;
import com.webset.set.utilerias.dto.SeleccionAutomaticaGrupoDto;

public interface ReglasNegocioService {
 
	public List<ReglasNegocioDto> obtenerRelacion();
	public List<EmpresaRegNegDto> obtenerEmpresas(int idUsuario, boolean bExiste, int idRegla);
	public List<AcreedorDoctoDto> obtenerAcreedorDocumento(boolean bTipoAcre, int idRegla, String clasificacion);
	public List<DoctoTesoreriaDto> obtenerDoctoTesoreria(int idRegla, String clasificacion);
	public CondicionPagoDto obtenerCondicionesPago(int idRegla);
	public List<PlanPagosDto> obtenerPlanPago(int idCondicioPago);
	public Map<String, String> insertarActualizarReglaNegocio(ReglasNegocioDto reglaNegocio,
			List<EmpresaRegNegDto> listEmpresas, List<AcreedorDoctoDto> listAcreDocto,
			List<DoctoTesoreriaDto> listDoctoTes, CondicionPagoDto condicionPago, List<PlanPagosDto> listPlanPagos,
			List<RubroRegNeg> listRubros, String tipoOperacion, List<AcreedorDoctoDto> lEliminarAcreDocto,
			List<DoctoTesoreriaDto> lEliminarDoctoTes, List<PlanPagosDto> lEliminarFechas, 
			boolean eliminaDiasContabVenc, boolean eliminaDiasAdicionales, boolean eliminaDiasespecificos, boolean eliminaPlanPagos, boolean simulador);
	public Map<String, String> eliminarReglaNegocio(int idRegla);
	public Map<String, String> validarDocumento(List<AcreedorDoctoDto> list);
	public Map<String, Object> validarAcreedores(List<AcreedorDoctoDto> list, String tipoOper);
	public Map<String, String> validarEmpresas(List<EmpresaRegNegDto> list, int idRegla, int idUsuario);
	public List<RubroRegNeg> obtenerRubros(boolean bAsignados, int idRegla);
	public String exportaExcel(String datos);
	public String procesaSimulador(int idRegla);
	public List<SeleccionAutomaticaGrupoDto> consultarSeleccionAutomaticaSIM(ConsultaPropuestasDto dtoIn);
	public List<MovimientoDto> consultarDetalle(SeleccionAutomaticaGrupoDto dtoIn);
	public Map<String, Object> actualizarMovtosSimulador(int idRegla);
	public Map<String, Object> deshacerCambiosSimulador();
	public Map<String, Object> loginSimulador();
	public Map<String, Object> insertarLoginSimulador();
	Map<String, Object> eliminaLogueoUsuario();
	
}
