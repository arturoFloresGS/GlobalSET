package com.webset.set.egresos.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.webset.set.egresos.dto.ConsultaPropuestasDto;
import com.webset.set.egresos.dto.BitacoraPropuestasDto;
import com.webset.set.egresos.dto.BloqueoPagoCruzadoDto;
import com.webset.set.egresos.dto.ColoresBitacoraDto;
import com.webset.set.egresos.dto.ParamConsultaPropuestasDto;
import com.webset.set.traspasos.dto.GrupoDTO;
import com.webset.set.traspasos.dto.RubroDTO;
import com.webset.set.utilerias.dto.GrupoEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.SeleccionAutomaticaGrupoDto;

import net.sf.jasperreports.engine.JRDataSource;

public interface ConsultaPropuestasBService {
	
	public List<LlenaComboGralDto>LlenarComboGral(LlenaComboGralDto dto);
	public Date obtenerFechaHoy();
	public List<LlenaComboGralDto>llenarComboBeneficiario(LlenaComboGralDto dto);
	public List<SeleccionAutomaticaGrupoDto> consultarSeleccionAutomatica(ConsultaPropuestasDto dtoIn);
	public List<MovimientoDto>consultarDetalle(SeleccionAutomaticaGrupoDto dtoIn);
	public Map<String, Object>autorizarProp(List<SeleccionAutomaticaGrupoDto> listaDto, String detalle, String autoDesauto, int autCheq);
	public List<LlenaComboGralDto> llenarComboGrupoEmpresa(GrupoEmpresasDto dto);
	public String eliminarProp(String cveControl, int noEmpresa);
	public String modificarProp(SeleccionAutomaticaGrupoDto dtoGridProp, List<MovimientoDto> listDetProp);
	public String nombreUsuarios(String usr1, String usr2, String usr3);
	public JRDataSource reporteDetalleCupos(SeleccionAutomaticaGrupoDto dtoIn);
	public String consultarConfiguraSet(int indice);
	public JRDataSource reporteConSelecAut(ParamConsultaPropuestasDto dtoIn);
	public int validaFacultad(int idFacultad);
	public List<LlenaComboDivisaDto>llenarComboDivisa();
	public double obtenerTipoCambio(List<Map<String, String>> objDatos, String sDivisa, Date fecHoy);
	public List<LlenaComboGralDto>llenarComboBancoPag(String sDivisa, String noEmpresa);
	public List<LlenaComboGralDto>llenarComboChequeraPag(int idBanco, int noEmpresa, String sDivisa);
	public String aceptarModificar(List<Map<String, String>> mapGridProp, List<Map<String, String>> mapGridDetProp,
			int formaPago, int idBanco, String idChequera, int idBancoBenef, String idChequeraBenef, boolean actualizaBeneficiario,
			String fecHoy);
	public List<MovimientoDto> consultarDetPagos(int noCliente, String fecIni, String fecFin);
	public List<GrupoDTO> llenarComboGrupo(String idTipoGrupo, int noEmpresa);
	public List<RubroDTO> llenarComboRubros(int idGrupo);
	public Map<String, Object> insertarFlujoMovtos(List<Map<String, String>> dtoMovto);
	public String exportaExcel(String datos); //Agregado EMS 170915
	public String validaModPropMaestro(SeleccionAutomaticaGrupoDto dtoGridProp[]); //Agregado EMS 180915
	public Map<String, Object> autorizarModificaciones(SeleccionAutomaticaGrupoDto dtoGMaster); //Agregado EMS 180915
	public String updateFecProp(SeleccionAutomaticaGrupoDto dtoGridProp[], String fechaActualiza);
	public List<BloqueoPagoCruzadoDto> consultaPagosCruzados(String noPersona);
	public List<LlenaComboGralDto> llenarComboBenefPagoCruzado(String noPersona);
	public List<LlenaComboDivisaDto> obtenerDivisas();
	public String insertaNuevoProvPagoCruzado(BloqueoPagoCruzadoDto pagoCruzado);
	public String eliminarProvPagoCruzado(String noProv, String divOriginal);
	public Map<String, Object> buscarBloqueo(String noEmpresa, String noProv, String noDocto, String periodo);
	public Map<String, Object> insertarBloqueo(String noEmpresa, String noProv, String noDocto, String motivo, int noUsuario);
	public Map<String, Object> eliminarBloqueo(String noEmpresa, String noProv, String noDocto);
	public HSSFWorkbook datosExcelBloqueados(String nombre, Map<String, String> params);
	public List<BloqueoPagoCruzadoDto> consultaProveedoresBloqueados(String empresas, String proveedores);
	public String eliminaDetallePropuesta(List<Map<String, String>> objGrid);
	public List<LlenaComboGralDto> llenarComboPeriodos(LlenaComboGralDto dtoIn);
	public List<LlenaComboGralDto> llenarComboReglaNegocio(LlenaComboGralDto dtoIn);
	public List<LlenaComboGralDto> llenarComboBancoPagBenef(String sDivisa, int noPersona);
	public List<LlenaComboGralDto> llenarComboChequeraPagBenef(int idBanco, int noEmpresa, String sDivisa, int idBenef);
	public Map<String, String> validaCvePropuesta(String cveControl);
	public Map<String, String> insertaSubPropuesta(double montoMaximo, String cveControl, String oldCveControl, int idUsuario, String fecha);
	public Map<String, String> actualizaMontoPropuesta(double montoMaximo, String cveControl, String stencero);
	public Map<String, String> actualizaPropuesta(String nvaCveControl, String noDoctos, String oldCveControl,
			String fecha);
	public List<LlenaComboGralDto> llenarComboGrupoEmpresaUnica(GrupoEmpresasDto dtoIn); //YEC 11/11/2015
	public Map<String, String> insertaBitacoraPropuesta(List<BitacoraPropuestasDto> listBitacora);	 //EMS 13/11/2015
	public int validaFacultadUsuario(String facultad);
	public List<MovimientoDto> existeChequeraBenef(List<MovimientoDto> lmovimientos);
	public List<LlenaComboEmpresasDto> llenarComboEmpresa(int usuario);
	public Map<String, Object> validarBancoChequera(List<SeleccionAutomaticaGrupoDto> listSeleccion); //07/01/2016
	public String exportaExcelDetalles(String datos);
	public Map<String, String> actualizaMultisociedad(String cveControl, StringBuffer folios, MovimientoDto movDatos);
	public List<SeleccionAutomaticaGrupoDto> consultarSeleccionAutomaticaStored(ConsultaPropuestasDto dtoIn);
	public List<ColoresBitacoraDto> obtenerColoresPropuesta();
	public List<ColoresBitacoraDto> obtenerColoresDetalle();
}