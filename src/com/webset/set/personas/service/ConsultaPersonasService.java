package com.webset.set.personas.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.webset.set.personas.dto.ConsultaPersonasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface ConsultaPersonasService {
	public List<ConsultaPersonasDto> llenaComboEmpresas ();
	public List<ConsultaPersonasDto> llenaComboTipoPersona();
	public String validaDatos(String tipoPersona);
	public List<ConsultaPersonasDto> llenaGrid(String tipoPersona,  String equivalePersona, String razonSocial, String paterno, String materno, 
											   String nombre, boolean inactivas);
	public List<ConsultaPersonasDto> comboEstadoCivil();
	public List<ConsultaPersonasDto> llenaComboGrupo();
	public List<ConsultaPersonasDto> llenaActividadEconomica();
	public List<ConsultaPersonasDto> llenaActividadGenerica();
	public List<ConsultaPersonasDto> llenaEstadoCivil();
	public List<ConsultaPersonasDto> llenaGiro();
	public List<ConsultaPersonasDto> llenaDireccion();
	public List<ConsultaPersonasDto> llenaEstado();
	public List<ConsultaPersonasDto> llenaPais();
	public List<ConsultaPersonasDto> llenaCaja();
	public List<ConsultaPersonasDto> llenaRiesgo();
	public List<ConsultaPersonasDto> comboTamano();
	public List<ConsultaPersonasDto> comboCalidad(); 
	public List<ConsultaPersonasDto> comboTipoInmueble();
	public List<ConsultaPersonasDto> comboFormaPago();
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
	public String aceptar(List<Map<String, String>> registro);
	public String guardarIB(List<Map<String, String>> registro);
	public String guardarF(List<Map<String, String>> registro);
	public String guardarPF(List<Map<String, String>> registro);
	public String guardarPM(List<Map<String, String>> registro);
	public String guardarCasa(List<Map<String, String>> registro);
	public String guardarDireccion(List<Map<String,String>> registro);
	public String validaDatosInsertUpdate(List<Map<String, String>> registro);
	public String verificaEmpresa(int noEmpresa);
	public int obtieneFolio(String tipoFolio);
//	public int actualizaFolio(String tipoFolio);
	public String cambiaTipoPersona(int noPersona);
	public List<ConsultaPersonasDto> comboBenef(int noEmpresa, String noBenef, String descBenef);
	public List<ConsultaPersonasDto> verificaRegistro(int noPersona, String tipoPersona);
	public String inhabilitaPersona(int noPersona, String tipoPersona);
	
	//**************************************MANTENIMIENTO CUENTAS DE PROVEEDOR ******************************************************************
	public List<ConsultaPersonasDto> consultaCuentasProveedor(int noPersona, int noEmpresa, String tipoPersona);
	public List<ConsultaPersonasDto> comboDivisasCuentas();
	public List<ConsultaPersonasDto> comboBancoCuentas(String nacionalidad);
	public String buscaDescripcionBanco(int noBanco);
	public List<ConsultaPersonasDto> validaMovimientos(int noPersona, String chequera);
	public List<ConsultaPersonasDto> validaTransferencias(List<Map<String, String>> registro);
	public String eliminaCuentaProveedor(List<Map<String, String>> registro);
	public String insertaCuentaHistorico(List<Map<String, String>> registro);
	public String insertaActualizaCuentasProveedor(List<Map<String, String>> registro);
	public String validaDatosCuentas(List<Map<String, String>> registro);
	//*******************************************************************************************************************************************
	
	//**************************************INICIO RELACIONES ******************************************************************
	public List<ConsultaPersonasDto> obtenerPersonas(String condicion,boolean todos);
	public List<LlenaComboGralDto> obtenerRelaciones(LlenaComboGralDto obj);
	public String altaRelaciones(List<Map<String, String>> datos);
	public List<ConsultaPersonasDto> consultarRelaciones(int noPersona, int noEmpresa);
	public String eliminarRelaciones(List<Map<String, String>> reg);
	//**************************************FIN RELACIONES ******************************************************************
	public HSSFWorkbook reportePersonas(String tipoPersona);
	public List<ConsultaPersonasDto> llenaGridMedios(String equivale);
	public String modificarReferencia(String tipoReferencia, int noPersona, String tipoPersona);
	public List<ConsultaPersonasDto> llenaGridCP(String tipoPersona, String noPersona);
	public List<ConsultaPersonasDto> llenaComboDivisaCP();
	public String guardaNuevoMedioContacto(String no_persona, String mail);
	public String guardaNuevoMedioContacto(String no_persona, String mail, int empre);
	public String eliminaMedioContacto(String no_persona, String mail);
	
	
	
}