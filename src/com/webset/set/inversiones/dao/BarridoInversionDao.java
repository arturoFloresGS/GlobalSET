package com.webset.set.inversiones.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.webset.set.inversiones.dto.CanastaInversionesDto;
import com.webset.set.utilerias.dto.MovimientoDto;

@SuppressWarnings("unchecked")
public interface BarridoInversionDao {
	public List<CanastaInversionesDto> empresasConcentradoras();
	public List<CanastaInversionesDto> todosLosBancos(String idDivisa);
	public List<CanastaInversionesDto> obtenerRegistros(String idDivisa, String noBanco);
	public Date obtenerFechaHoy();
	public int buscaRegistro(List<Map<String, String>> datosGrid, int i, Date fecHoy);
	public int insertarBarridos(List<Map<String, String>> datosGrid, int i, Date fecHoy);
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++ PROCESO DE EJECUCION DE BARRIDO INVERSION +++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	public List<CanastaInversionesDto> obtenerSolicitudesBarrido(String idDivisa, String noBanco);
	public int validaFacultad(int idFacultad);
	public boolean validaPassword(String pass);
	public int verificaBarridoAut(List<Map<String, String>> datosGrid, int i);
	public int verificaBarridoDes(List<Map<String, String>> datosGrid, int i);
	public int autorizaBarrido(List<Map<String, String>> datosGrid, int i);
	public int desAutorizaBarrido(List<Map<String, String>> datosGrid, int i);
	public int regresarBarrido(List<Map<String, String>> datosGrid, int i);
	public Map<String, Object> ejecutarBarridos(String noSolicitudes);
	public Map<String, Object> armaCanastas();
	public String configuraSet(int indice);
	public List<MovimientoDto> buscaMovimientosTraspasos();
	public List<MovimientoDto>seleccionarDetalleArchivo(String folios);
	public int insertarDetArchTransfer(MovimientoDto parametro, String psNomArch);
	public int insertarArchTransfer(String psNomArch, int banco, Date fecha, double total, int registros, int usuario);
	public int actualizarMovimientoTipoEnvio(String psTipoEnvio, String psNomArchivos, boolean pbMasspayment);
	public int cambiaEstCtrlBarrido(String noSolicitudes);
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++        MONITOR DE INVERSION        ++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	public List<CanastaInversionesDto> obtenerCanastasInv(String noEmpresa, String idDivisa);
	public List<CanastaInversionesDto> obtenerCanastasVencidasHoy(String noEmpresa, String idDivisa);
	public List<CanastaInversionesDto> crearNodosRaizArbol(String noEmpresa, String idDivisa);
	public List<CanastaInversionesDto> crearNodosArbol(String noEmpresa, String idDivisa);
	public List<CanastaInversionesDto> crearNodosHijosArbol(int noCanasta);
	public int actualizaTasaReal(int noCanasta, String tasaReal);
	public int actualizaCanastaIntIsr(int noCanasta);
	public Map<String, Object> vencimientoCanastas(int noCanasta, int noEmpresa);
	public Map<String, Object> regresoCanastas(int noCanasta, int noEmpresa);
	public int actualizaCanastaCodigo(int noCanasta, String codigo);
	public int actualizaDetalleCanastacodigo(int noOrden, String codigoDet);
	public List<CanastaInversionesDto> detalleEmpresas(int noCanasta);
	public List<CanastaInversionesDto> consultarDetalleCanasta(int canasta);
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++       REPORTES DE INVERSION        ++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	public List<Map<String, Object>> reportesBarridos(Map datos);
	public List<Map<String, Object>> reporteCanastasPendientes(Map datos);
	public List<Map<String, Object>> reporteCanastasVencenHoy(Map datos);
	public List<Map<String, Object>> reporteCanastasVencimientos(Map datos);
	public List<Map<String, Object>> InversionesDetalladas(Map datos);
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++       TASAS Y COMISION DE INVERSION        ++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	public List<CanastaInversionesDto> obtenerTasaComision();
	public int existeTasa(int dias);
	public int insertarTasa(int dias, double valorTasa);
	public int modificarTasa(int dias, double valorTasa);
	public int existeComision(double comision, Date fecHoy);
	public int insertarComision(double comision, Date fecHoy);
}
