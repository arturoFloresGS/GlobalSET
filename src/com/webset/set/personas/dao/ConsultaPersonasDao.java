package com.webset.set.personas.dao;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.webset.set.personas.dto.ConsultaPersonasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface ConsultaPersonasDao {
	public List<ConsultaPersonasDto> llenaComboEmpresas();
	public List<ConsultaPersonasDto> llenaComboTipoPersona();
	public List<ConsultaPersonasDto> llenaGrid(String tipoPersona,String equivalePersona, String razonSocial, String paterno, 
											   String materno, String nombre, boolean inactivas);
	
	//public List<ConsultaPersonasDto> llenaGrid(String tipoPersona);
	public List<ConsultaPersonasDto> comboEstadoCivil();
	public List<ConsultaPersonasDto> llenaComboGrupo();
	public List<ConsultaPersonasDto> llenaActividadEconomica();
	public List<ConsultaPersonasDto> llenaActividadGenerica();
	public List<ConsultaPersonasDto> llenaEstadoCivil();
	public List<ConsultaPersonasDto> llenaGiro();
	public List<ConsultaPersonasDto> llenaDireccion();
	public List<ConsultaPersonasDto> llenaPais();
	public List<ConsultaPersonasDto> llenaEstado();
	public List<ConsultaPersonasDto> llenaCaja();
	public List<ConsultaPersonasDto> llenaRiesgo();
	public List<ConsultaPersonasDto> comboTamano();
	public List<ConsultaPersonasDto> comboCalidad();
	public List<ConsultaPersonasDto> comboFormaPago();
	public List<ConsultaPersonasDto> comboTipoInmueble();
	public List<ConsultaPersonasDto> comboFormaPagoProv(int noPersona, int noEmpresa);
	public List<ConsultaPersonasDto> comboTEF();
	public List<ConsultaPersonasDto> comboPayment();
	public List<ConsultaPersonasDto> obtieneDatos(int noEmpresa, String noPersona, String tipoPersona);
	public List<ConsultaPersonasDto> obtienePersonaFisica(int noEmpresa, String noPersona, String tipoPersona);
	public List<ConsultaPersonasDto> obtieneDireccion(int noEmpresa, String noPersona, String tipoPersona);
	public String configuraSet(int indice);
	public String proveedorBasico(int noEmpresa, int noPersona);
	public List<ConsultaPersonasDto> obtieneCaja(int noEmpresa);
	public List<ConsultaPersonasDto> obtenerDatosEmpresa(int noEmpresa);
	public List<ConsultaPersonasDto> validaGrupoEmpresa(int noEmpresa);
	public List<ConsultaPersonasDto> validaDivisionesEmpresa(int noEmpresa);
	public int actualizaDatos(ConsultaPersonasDto objDto);
	public int actualizaPersonaFisica(ConsultaPersonasDto objDto);
	public int actualizaDireccion(ConsultaPersonasDto objDto);
	public int insertaDireccion(ConsultaPersonasDto objDto);
	public int altaRelacion(ConsultaPersonasDto objDto);
	public int insertaDatos(ConsultaPersonasDto objDto, int noCuenta, int noLinea, int noEmpresaC);
	public int actualizaCaja(int idCaja, int noPersona);
	public int actualizaProveedor(int noPersona, int noEmpresa, String proveedorBasico);
	public int actualizaEmpresa(ConsultaPersonasDto objDto);
	public int existePersona (int noPersona, int noEmpresa, String tipoPersona);
	public int existeDireccion (int noPersona, ConsultaPersonasDto obj, String tipoPersona);
	public int seleccionarFolioReal(String tipoFolio);
	public int actualizarFolioReal(String tipoFolio);
	public String verificaEmpresa(int noEmpresa);
	public int cambiaTipoPersona(int noPersona);
	public List<ConsultaPersonasDto> comboBenef(int noEmpresa, String noBenef, String descBenef);
	public int actualizaInstFinanciera(int noPersona, String noEmpresa);
	public int insertaCuenta(int noEmpresa, int noLinea, int noPersona, int noCuenta, String fechaHoy, int usuario);
	public int insertaLinea(int noEmpresa,int noLinea, String fechaHoy, int usuario);
	public int insertaEmpresa (ConsultaPersonasDto obj);
	public int insertaSaldos(int noEmpresa, int noLinea, int noCuenta, int valor);
	public int obtieneCuentaEmpresa(int noEmpresa);
	public int insertaProveedor(int noEmpresa, int noPersona, int usuario, String fechaHoy, int noCuentaEmp, boolean bProvBasico, String provBasico);
	public List<ConsultaPersonasDto> verificaRegistro(int noPersona, String tipoPersona);
	public int inhabilitaPersona(int noPersona, String tipoPersona);
	
	//***************************************************MANTENIMIENTO CUENTAS PROVEEDOR ***********************************************************
	public List<ConsultaPersonasDto> consultaCuentasProveedor (int noPersona, int noEmpresa, String tipoPersona);
	public List<ConsultaPersonasDto> comboDivisasCuentas();
	public List<ConsultaPersonasDto> comboBancoCuentas(String nacionalidad);
	public String buscaDescripcionBanco(int noBanco);
	public List<ConsultaPersonasDto> validaMovimientos(int noPersona, String chequera);
	public List<ConsultaPersonasDto> validaTransferencias(ConsultaPersonasDto obj);
	public int eliminaCuentaProveedor(ConsultaPersonasDto obj);
	public int insertaCuentaHistorico(ConsultaPersonasDto obj);
	public int insertaCuentaProveedor(ConsultaPersonasDto obj);
	public int actualizaCuentaProveedor(ConsultaPersonasDto obj);
	
	public Map<String, String> actualizaChequerasProv(int noPersona);
	public List<ConsultaPersonasDto> buscaFormaPagoProv(int noPersona, int noEmpresa);
	public int insertaFormaPago(int noEmpresa, int noPersona, String tipoPersona);
	//**********************************************************************************************************************************************
	
	//**************************************INICIO RELACIONES ******************************************************************
	public List<ConsultaPersonasDto> obtenerPersonas(String condicion,boolean todos);
	public List<LlenaComboGralDto> obtenerRelaciones(LlenaComboGralDto obj);
	public int existeRelacion(List<Map<String, String>> datos);
	
	public List<ConsultaPersonasDto> consultarRelaciones(int noPersona, int noEmpresa);
	public int eliminarRelaciones(List<Map<String, String>> datos, int i);
	//**************************************FIN RELACIONES ******************************************************************
	public List<Map<String, String>> reportePersonas(String tipoPersona);
	public void eliminaPersona(ConsultaPersonasDto objConsultaPersonasDto);
	public void eliminaEmpresa(ConsultaPersonasDto objConsultaPersonasDto);
	public void eliminaLinea(ConsultaPersonasDto objConsultaPersonasDto);
	public List<ConsultaPersonasDto> llenaGridMedios(String equivale);
	public int modificarReferencia(String tipoReferencia, int noPersona, String tipoPersona);
	public List<ConsultaPersonasDto> llenaGridCP(String tipoPersona, String noPersona);
	public List<ConsultaPersonasDto> llenaComboDivisaCP();
	public String guardaNuevoMedioContacto(String no_persona, String mail, int empre);
	public int eliminaCuentaProveedor(String no_persona, String mail);
	
	
	
}