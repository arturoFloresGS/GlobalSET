package com.webset.set.personas.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.webset.set.global.dao.GlobalDao;
import com.webset.set.personas.dao.ConsultaPersonasDao;
import com.webset.set.personas.dto.ConsultaPersonasDto;
import com.webset.set.personas.service.ConsultaPersonasService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;

public class ConsultaPersonasBusinessImpl implements ConsultaPersonasService{
	ConsultaPersonasDao objConsultaPersonasDao;
	GlobalDao globalDao;
	
	Bitacora bitacora = new Bitacora();
	Funciones funciones = new Funciones();
	ConsultaPersonasDto objConsultaPersonasDto = new ConsultaPersonasDto();
	ConsultasGenerales consultasGenerales;
	List<ConsultaPersonasDto> recibeDatos = new ArrayList<ConsultaPersonasDto>();
	int recibeResultadoEntero = 0;

	public List<ConsultaPersonasDto> llenaComboEmpresas(){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.llenaComboEmpresas();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaComboEmpresas");
		}
		return result;
	}
	
	public List<ConsultaPersonasDto> llenaComboTipoPersona(){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.llenaComboTipoPersona();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaComboTipoPersona");
		}
		return result;
	}
	public List<ConsultaPersonasDto> llenaComboDivisaCP(){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.llenaComboDivisaCP();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaComboTipoPersona");
		}
		return result;
	}
	
	public String validaDatos(String tipoPersona){		
		//SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");		
		try{
			if (tipoPersona.equals(""))
				return "Debe de selecciona el tipo de persona";
			
			return "";	
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: validaDatos");
		}
		System.out.println("Hola aqui estamos viendo el error.");
		return "Error en los datos";
	}
	
	public List<ConsultaPersonasDto> llenaGrid(String tipoPersona, String equivalePersona, String razonSocial, String paterno, 
			   								   String materno, String nombre, boolean inactivas){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.llenaGrid(tipoPersona,equivalePersona, razonSocial, paterno, materno, nombre, inactivas);
		
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaGrid");
		}
		return result;
	}
	public List<ConsultaPersonasDto> llenaGridCP(String tipoPersona, String noPersona){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
		result = objConsultaPersonasDao.llenaGridCP(tipoPersona,noPersona);
		
		} catch(Exception e){
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
		"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaGrid");
		}
		return result;
	}
	
	public List<ConsultaPersonasDto> comboEstadoCivil(){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.comboEstadoCivil();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: comboEstadoCivil");
		}
		return result;
	}
		
	public List<ConsultaPersonasDto> llenaComboGrupo(){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.llenaComboGrupo();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaComboGrupo");
		}
		return result;
	}
	
	public List<ConsultaPersonasDto> llenaActividadEconomica(){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.llenaActividadEconomica();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaActividadEconomica");
		}
		return result;
	}
	
	public List<ConsultaPersonasDto> llenaActividadGenerica(){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.llenaActividadGenerica();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaActividadGenerica");
		}
		return result;
	}
	
	public List<ConsultaPersonasDto> llenaEstadoCivil(){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.llenaEstadoCivil();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaEstadoCivil");
		}
		return result;
	}
	
	public List<ConsultaPersonasDto> llenaDireccion(){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.llenaDireccion();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaDireccion");
		}
		return result;
	}
	
	public List<ConsultaPersonasDto> llenaEstado(){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.llenaEstado();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaEstado");
		}
		return result;
	}
	
	public List<ConsultaPersonasDto> llenaPais(){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.llenaPais();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaPais");
		}
		return result;
	}
	
	public List<ConsultaPersonasDto> llenaGiro(){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.llenaGiro();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaGiro");
		}
		return result;
	}
	
	public List<ConsultaPersonasDto> llenaCaja(){		
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.llenaCaja();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaCaja");
		}
		return result;
	}
	
	public List<ConsultaPersonasDto> llenaRiesgo(){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.llenaRiesgo();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaRiesgo");
		}
		return result;
	}
	
	public List<ConsultaPersonasDto> comboTamano(){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.comboTamano();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: comboTamano");
		}
		return result;
	}
	
	public List<ConsultaPersonasDto> comboCalidad(){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.comboCalidad();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: comboCalidad");
		}
		return result;
	}
	
	public List<ConsultaPersonasDto> comboTipoInmueble(){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.comboTipoInmueble();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: comboTipoInmueble");
		}
		return result;
	}
	
	public List<ConsultaPersonasDto> comboFormaPago(){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.comboFormaPago();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: comboFormaPago");
		}
		return result;
	}
	
	public List<ConsultaPersonasDto> comboFormaPagoProv(int noPersona, int noEmpresa){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.comboFormaPagoProv(noPersona, noEmpresa);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: comboFormaPagoProv");
		}
		return result;
	}
	
	public List<ConsultaPersonasDto> comboTEF (){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.comboTEF();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: comboTEF");
		}
		return result;
	}
	
	public List<ConsultaPersonasDto> comboPayment (){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.comboPayment();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: comboPayment");
		}
		return result;
	}
	
	public List<ConsultaPersonasDto> obtieneDatos(int noEmpresa, String noPersona, String tipoPersona){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.obtieneDatos(noEmpresa, noPersona, tipoPersona);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: obtieneDatos");
		}
		return result;
	}
	
	public List<ConsultaPersonasDto> obtienePersonaFisica(int noEmpresa, String noPersona, String tipoPersona){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.obtienePersonaFisica(noEmpresa, noPersona, tipoPersona);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: obtienePersonaFisica");
		}
		return result;
	}
	
	public List<ConsultaPersonasDto> obtieneDireccion(int noEmpresa, String persona, String tipoPersona){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.obtieneDireccion(noEmpresa, persona, tipoPersona);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: obtieneDireccion");
		}
		return result;
	}
	
	public String configuraSet(int indice){
		try {
			return objConsultaPersonasDao.configuraSet(indice);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: configuraSet");
		}
		return null;		 
	}
	
	public String proveedorBasico(int noEmpresa, int noPersona){
		try {
			return objConsultaPersonasDao.proveedorBasico(noEmpresa, noPersona);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: proveedorBasico");
		}
		return null;
	}
	
	public List<ConsultaPersonasDto> obtieneCaja (int noEmpresa){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.obtieneCaja(noEmpresa);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: obtieneCaja");
		}
		return result;
	}
	
	public List<ConsultaPersonasDto> obtenerDatosEmpresa(int noEmpresa){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.obtenerDatosEmpresa(noEmpresa);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: obtenerDatosEmpresa");
		}
		return result;
	}
	
	
	public String validaDatosInsertUpdate(List<Map<String, String>> registro){
		String mensaje = "";
		String fechaRFC = "";		
		
		try{			
			
			//Si el tipo de persona es Moral
			if (registro.get(0).get("fisicaMoral").equals("M")){
				if (registro.get(0).get("razonSocial").equals(""))
					mensaje = "La Raz\u00f2n Social est\u00e1 incompleta";
				
				if (registro.get(0).get("fechaIngreso").equals(""))
					mensaje = "Es necesario introducir la Fecha de Ingreso";				
			}
			else{ //Cuando es fisica
				if (registro.get(0).get("paterno").equals(""))
					mensaje = "Falta el Apellido Paterno";				
				
				if (registro.get(0).get("nombre").equals(""))
					mensaje = "Falta el Nombre de la persona";				
			}			
			//Para validar el RFC EN EL CASO DE CAABSA TODOS ENTRAN COMO PERSONAS MORALES
			if (!registro.get(0).get("rfc").equals("")){
				System.out.println(registro.get(0).get("rfc"));
				
				if (registro.get(0).get("fisicaMoral").equals("F")){					
					if (registro.get(0).get("rfc").length() != 13){
						mensaje = "Debe capturar un RFC valido";						
					}else{
							fechaRFC = registro.get(0).get("rfc").substring(8, 10) + "/" + registro.get(0).get("rfc").substring(6, 8) + "/" + registro.get(0).get("rfc").substring(4, 6);
							System.out.println(fechaRFC + " Fecha");
							if (funciones.isDate(fechaRFC, false) == false)
								mensaje = "El formato del RFC no es valido por la Fecha";
					}
				}
				else{//AQUI DEBERIA DE SER != 12
					if (registro.get(0).get("rfc").length() > 13){
						mensaje = "Debe capturar un RFC valido";						
					}
					else{
						if(registro.get(0).get("rfc").trim().length() == 13)
							fechaRFC = registro.get(0).get("rfc").substring(8, 10) + "/" + registro.get(0).get("rfc").substring(6, 8) + "/" + registro.get(0).get("rfc").substring(4, 6);
						else
							fechaRFC = registro.get(0).get("rfc").substring(7, 9) + "/" + registro.get(0).get("rfc").substring(5, 7) + "/" + registro.get(0).get("rfc").substring(3, 5);
						
						if (funciones.isDate(fechaRFC, false) == false)
							mensaje = "El formato del RFC no es valido por la Fecha";
					}
				}
			}
			
			//Valida las divisiones
			if (registro.get(0).get("manejaDivision").equals(true) && registro.get(0).get("tipoPersona").equals("E")){
				System.out.println("Entra validacion de division");
				if (registro.get(0).get("division").equals(true)){
					recibeDatos = objConsultaPersonasDao.validaGrupoEmpresa(Integer.parseInt(registro.get(0).get("noPersona")));
					
					if (recibeDatos.size() > 0)
						mensaje = "La empresa no puede usar divisiones si pertenece a un Grupo de Empresa";
				}
				else{
					recibeDatos = objConsultaPersonasDao.validaDivisionesEmpresa(Integer.parseInt(registro.get(0).get("noPersona")));
					if (recibeDatos.size() > 0)
						mensaje = "La empresa cuenta con Divisiones, no puede quitar esta funcionalidad";
				}
			}		
			
			if (registro.get(0).get("tipoOperacion").equals("inserta")){
				if (registro.get(0).get("noPersona").equals("") && registro.get(0).get("equivalePersona").equals(""))
					mensaje = "El n\u00famero de persona no puede ir vacio";
			}
			
			////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (registro.get(0).get("accionesA").length() > 9)
				mensaje = "En las Acciones A se excedio de la longitud! maximo debe tener 9 caracteres.";
			
			if (registro.get(0).get("accionesB").length() > 9)
				mensaje = "En las Acciones B se excedio de la longitud! maximo debe tener 9 caracteres.";
			
			if (registro.get(0).get("accionesC").length() > 9)
				mensaje = "En las Acciones C se excedio de la longitud! maximo debe tener 9 caracteres.";
			
			if (funciones.isNumeric(registro.get(0).get("diaLimite"))){
				if (registro.get(0).get("diaLimite").length() > 1){
					mensaje = "La longitud del D\u00eda Limite se excedio!, maximo debe tener 1 caracter.";
				}
			}
			else
				mensaje = "Valor incorrecto no es N\u00famerico el D\u00eda Limite.";
			
			if (funciones.isNumeric(registro.get(0).get("diaRecepcion"))){
				if (registro.get(0).get("diaRecepcion").length() > 1){
					mensaje = "La longitud del D\u00eda de Recepcion se excedio!, maximo debe tener 1 caracter.";
				}
			}
			else
				mensaje = "Valor incorrecto no es N\u00famerico el D\u00eda de Recepcion.";
							
			if (registro.get(0).get("materno").length() > 25)
				mensaje = "El Apellido Materno debe tener maximo 25 caracteres.";
			
			if (registro.get(0).get("paterno").length() > 25)
				mensaje = "El Apellido Paterno debe tener maximo 25 caracteres.";
			
			if (registro.get(0).get("nombre").length() > 30)
				mensaje = "El Nombre debe tener maximo 30 caracteres.";
			
			if (registro.get(0).get("razonSocial").length() > 80)
				mensaje = "La Razon Social debe tener maximo 80 caracteres.";
			
			if (registro.get(0).get("noPersona").length() > 9)
				mensaje = "El N\u00famero de Persona debe tener maximo 9 caracteres.";
			
			if (registro.get(0).get("objetoSocial").length() > 150)
				mensaje = "El Objeto Social debe tener maximo 150 caracteres.";
			
			if (registro.get(0).get("puesto").length() > 30)
				mensaje = "El Puesto debe tener maximo 30 caracteres.";
						
			if (funciones.isNumeric(registro.get(0).get("ventasAnuales"))){ 
				if (registro.get(0).get("ventasAnuales").length() > 12){
					mensaje = "La longitud de las Ventas Anuales deben ser maximo de 12 caracteres.";
				}
			}
			else
				mensaje = "Valor incorrecto, deben ser N\u00famericas las Ventas Anuales.";
			
			if (registro.get(0).get("pagoReferenciado").length() > 40)
				mensaje = "La Referencia debe ser de maximo 40 caracteres.";
			/*
			if(registro.get(0).get("tipoPersona").equals("P")) {
				if(registro.get(0).get("idRubro").equals("") || registro.get(0).get("grupoRubro").equals(""))
					mensaje = "Introdusca el Grupo y el Rubro";
			}
			*/
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: validaDatosInsertUpdate");
		}
		return mensaje;
	}
	
	public String guardarPM (List<Map<String, String>> registro){
		String mensaje = "";
//		String sexo = "";
//		String psProveedorBasico = "";
//		String psProveedor = "";
//		String psAsociacion = "";
//		String psContratoInversion = "";
//		String psConcentradora = "";
//		String psExporta = "";
//		String psDivision = "";
//		String psPagosCruzados = "";
//		int piContratoTEF = 0;
//		int piContratoPayment = 0;		
		int recibeDatoEntero = 0;
		int piNoCuenta = 0;
		int piNoLinea = 0;
		int noPersona = 0;
		String psRazonSocial = "";
//		String psNombre = "";
//		String psPaterno = "";
//		String psMaterno = "";
		String recibeCadena = "";
		int noEmpresa = 0;
		int noCuentaEmp = 0;
		int noEmpresaC = 0;
		
		try{
			
			noPersona = registro.get(0).get("noPersona").equals("") ? 0 : Integer.parseInt(registro.get(0).get("noPersona"));
					
			//Valida sexo							
			//sexo = registro.get(0).get("sexo");
			
						
			//Si el tipo de persona es proveedor
//			if (registro.get(0).get("tipoPersona").equals("P")){				
//				if (registro.get(0).get("proveedorServiciosBasicos").equals("true")){					
//					psProveedorBasico = "S";
//				}
//				else{					
//					psProveedorBasico = "N";
//				}
//			}
//				psProveedor = "N";
//				psAsociacion = "N";
//				psContratoInversion = "N";
//				psNombre =  registro.get(0).get("nombre");
//			psPaterno =  registro.get(0).get("paterno");
//			psMaterno = registro.get(0).get("materno");
			
			
			
			//Para actualizar o insertar la informacion
			noEmpresaC = registro.get(0).get("noEmpresa").equals("") ? 0 : Integer.parseInt(registro.get(0).get("noEmpresa"));
			
			objConsultaPersonasDto.setNoEmpresa(registro.get(0).get("noEmpresa").equals("") ? 0 : Integer.parseInt(registro.get(0).get("noEmpresa")));
			objConsultaPersonasDto.setNoPersona(registro.get(0).get("noPersona").equals("") ? 0 : Integer.parseInt(registro.get(0).get("noPersona")));			
			objConsultaPersonasDto.setTipoPersona(registro.get(0).get("tipoPersona"));
			objConsultaPersonasDto.setFechaIngreso(registro.get(0).get("fechaIngreso").equals("") ? registro.get(0).get("fechaHoy") : registro.get(0).get("fechaIngreso"));
			objConsultaPersonasDto.setRfc(registro.get(0).get("rfc"));	
			objConsultaPersonasDto.setRazonSocial(psRazonSocial);	
			objConsultaPersonasDto.setNombreCorto(registro.get(0).get("nombreCorto"));
			objConsultaPersonasDto.setIdFormaPagoProv(registro.get(0).get("idFormaPagoProv").equals("") ? 0 : Integer.parseInt(registro.get(0).get("idFormaPagoProv")));			

			
			if (registro.get(0).get("tipoOperacion").equals("MODIFICAR")){
			
				//Se actualiza los datos en Persona
				recibeDatoEntero = objConsultaPersonasDao.actualizaDatos(objConsultaPersonasDto);
			
				if (recibeDatoEntero > 0)
					mensaje = "El registro se actualizo correctamente";
				else
					mensaje = "Ocurrio un error durante la actualizaci\u00f2n";
				System.out.println("10");
				if (registro.get(0).get("tipoPersona").equals("E"))
				{
					if (!registro.get(0).get("idCaja").equals("") && !registro.get(0).get("idCaja").equals(0)){
						recibeDatoEntero = objConsultaPersonasDao.actualizaCaja(Integer.parseInt(registro.get(0).get("idCaja")), Integer.parseInt(registro.get(0).get("noPersona")));						
					}
					else{
						recibeDatoEntero = objConsultaPersonasDao.actualizaCaja(-1, Integer.parseInt(registro.get(0).get("noPersona")));
					}					
				}
				
				if(registro.get(0).get("tipoPersona").equals("P")){					
					recibeDatoEntero = objConsultaPersonasDao.actualizaProveedor(Integer.parseInt(registro.get(0).get("noPersona")), 
																				 Integer.parseInt(registro.get(0).get("noEmpresa")), "");					
				}
				
				if (registro.get(0).get("tipoPersona").equals("E") || registro.get(0).get("tipoPersona").equals("I")){
					recibeDatoEntero = objConsultaPersonasDao.actualizaEmpresa(objConsultaPersonasDto);
				}		
				
			}//Termina la funcion del update
			
			else if (registro.get(0).get("tipoOperacion").equals("INSERTAR")){// para hacer un insert de una nueva persona	
					
				if (registro.get(0).get("tipoPersona").equals("K") || registro.get(0).get("tipoPersona").equals("P"))
					objConsultaPersonasDto.setNoEmpresa(552);
				else
					objConsultaPersonasDto.setNoEmpresa(Integer.parseInt(registro.get(0).get("noEmpresa")));
							
				if (registro.get(0).get("tipoPersona").equals("R") || registro.get(0).get("tipoPersona").equals("F")){					
//					objConsultaPersonasDto.setNoPersona(objConsultaPersonasDao.seleccionarFolioReal("no_persona"));
//					objConsultaPersonasDao.actualizarFolioReal("no_persona");
					objConsultaPersonasDto.setNoPersona(globalDao.obtenerFolioReal("no_persona"));
				}
				else{					
					if (registro.get(0).get("noPersona").equals("") || registro.get(0).get("noPersona").equals("0")){						
						noPersona = Integer.parseInt(registro.get(0).get("equivalePersona"));						
						}					
					else
						noPersona = Integer.parseInt(registro.get(0).get("noPersona"));
					
					objConsultaPersonasDto.setNoPersona(noPersona);
				}					

				if (!registro.get(0).get("tipoPersona").equals("E") && !registro.get(0).get("tipoPersona").equals("P") &&
					!registro.get(0).get("tipoPersona").equals("C") && !registro.get(0).get("tipoPersona").equals("I") &&
					!registro.get(0).get("tipoPersona").equals("R") && !registro.get(0).get("tipoPersona").equals("K")){
					
					objConsultaPersonasDto.setNoEmpresa(1);
//					objConsultaPersonasDto.setNoPersona(objConsultaPersonasDao.seleccionarFolioReal("no_persona"));
					//objConsultaPersonasDao.actualizarFolioReal("no_persona");
					objConsultaPersonasDto.setNoPersona(globalDao.obtenerFolioReal("no_persona"));
					
				}
				
				if (registro.get(0).get("tipoPersona").equals("I") || registro.get(0).get("tipoPersona").equals("E") ||
					registro.get(0).get("tipoPersona").equals("P") || registro.get(0).get("tipoPersona").equals("C") ||
					registro.get(0).get("tipoPersona").equals("R")){
//					piNoCuenta = objConsultaPersonasDao.seleccionarFolioReal("no_cuenta");
//					objConsultaPersonasDao.actualizarFolioReal("no_cuenta");
//					piNoLinea = objConsultaPersonasDao.seleccionarFolioReal("no_linea");
//					objConsultaPersonasDao.actualizarFolioReal("no_linea");
					piNoCuenta =globalDao.obtenerFolioReal("no_cuenta");
					piNoLinea =globalDao.obtenerFolioReal("no_linea");
					objConsultaPersonasDto.setNoLinea(piNoLinea);
					objConsultaPersonasDto.setNoCuenta(piNoCuenta);
				}
				recibeDatoEntero = objConsultaPersonasDao.existePersona(objConsultaPersonasDto.getNoPersona(), objConsultaPersonasDto.getNoEmpresa(), registro.get(0).get("tipoPersona"));
				
				if (recibeDatoEntero > 0){
					mensaje = "El n\u00famero de persona ya existe";
				}
				else{				
					if (registro.get(0).get("tipoPersona").equals("E"))
						/////////////Revisar noPersona
						objConsultaPersonasDto.setNoEmpresa(noPersona);
					
					recibeDatoEntero = objConsultaPersonasDao.insertaDatos(objConsultaPersonasDto, piNoCuenta, piNoLinea, noEmpresaC);				
					if (recibeDatoEntero > 0)
						mensaje = "El registro se inserto correctamente";
					else
						mensaje = "Ocurrio un error al almacenar el registro";
					
					recibeCadena = objConsultaPersonasDao.configuraSet(268);
					
					if (recibeCadena.equals("SI"))
						objConsultaPersonasDao.actualizaInstFinanciera(noPersona, "NULL");
					
					if (!registro.get(0).get("noEmpresa").equals(""))
						objConsultaPersonasDao.actualizaInstFinanciera(noPersona, registro.get(0).get("noEmpresa"));
					
					if (registro.get(0).get("tipoPersona").equals("I") || registro.get(0).get("tipoPersona").equals("E")){
						noEmpresa = noPersona;
						GlobalSingleton globalSingleton = GlobalSingleton.getInstancia();
						System.out.println("<----------------------------->");
						System.out.println(globalSingleton.getUsuarioLoginDto().getIdUsuario());
												
						objConsultaPersonasDto.setNoEmpresa(noEmpresa);
						recibeDatoEntero = objConsultaPersonasDao.insertaEmpresa(objConsultaPersonasDto);
						
						recibeDatoEntero = objConsultaPersonasDao.insertaLinea(noEmpresa, piNoLinea, registro.get(0).get("fechaHoy"),globalSingleton.getUsuarioLoginDto().getIdUsuario() );
						System.out.println("Hola Paso");
						recibeDatoEntero = objConsultaPersonasDao.insertaCuenta(noEmpresa, piNoLinea, noPersona, piNoCuenta,
								   registro.get(0).get("fechaHoy"),globalSingleton.getUsuarioLoginDto().getIdUsuario() );
						
						recibeDatoEntero = objConsultaPersonasDao.insertaSaldos(noEmpresa, piNoLinea, piNoCuenta, 90);
						recibeDatoEntero = objConsultaPersonasDao.insertaSaldos(noEmpresa, piNoLinea, piNoCuenta, 91);
						recibeDatoEntero = objConsultaPersonasDao.insertaSaldos(noEmpresa, piNoLinea, piNoCuenta, 8);
						recibeDatoEntero = objConsultaPersonasDao.insertaSaldos(noEmpresa, piNoLinea, piNoCuenta, 9);						
					}
					else if (registro.get(0).get("tipoPersona").equals("P")){
						noEmpresa = Integer.parseInt(registro.get(0).get("noEmpresa"));
						recibeDatoEntero = objConsultaPersonasDao.obtieneCuentaEmpresa(noEmpresa);
						if (recibeDatoEntero > 0)
							noCuentaEmp = recibeDatoEntero;
						else
							noCuentaEmp = 0;
						System.out.println(objConsultaPersonasDao==null);
						System.out.println(registro.get(0).get("fechaHoy")==null);
						System.out.println(registro.get(0).get("usuarioModif")==null);
						GlobalSingleton globalSingleton = GlobalSingleton.getInstancia();
						objConsultaPersonasDao.insertaProveedor(noEmpresa, noPersona,globalSingleton.getUsuarioLoginDto().getIdUsuario() ,
																registro.get(0).get("fechaHoy"), noCuentaEmp, false, "B");
					}
					
					if (registro.get(0).get("tipoPersona").equals("I") || registro.get(0).get("tipoPersona").equals("E") 
						|| registro.get(0).get("tipoPersona").equals("P") || registro.get(0).get("tipoPersona").equals("C")){
						mensaje = "Datos registrados con n\u00famero de persona "  + " y N\u00famero de Empresa " + noEmpresa;
						
						
					}
					else{
						mensaje = "Datos registrados con n\u00famero de persona " + registro.get(0).get("noPersona");
					}					
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: guardarPM");
		}return mensaje;
	}

	
	public String guardarPF (List<Map<String, String>> registro){
		String mensaje = "";
//		String sexo = "";
		String psProveedorBasico = "";
//		String psProveedor = "";
//		String psAsociacion = "";
//		String psContratoInversion = "";
//		String psConcentradora = "";
//		String psExporta = "";
//		String psDivision = "";
//		String psPagosCruzados = "";
//		int piContratoTEF = 0;
//		int piContratoPayment = 0;		
		int recibeDatoEntero = 0;
		int piNoCuenta = 0;
		int piNoLinea = 0;
		int noPersona = 0;
	//	String psRazonSocial = "";
		String psNombre = "";
		String psPaterno = "";
		String psMaterno = "";
		String recibeCadena = "";
		int noEmpresa = 0;
		int noCuentaEmp = 0;
		int noEmpresaC = 0;
		
		try{
			
			noPersona = registro.get(0).get("noPersona").equals("") ? 0 : Integer.parseInt(registro.get(0).get("noPersona"));
					
			//Valida sexo							
			//sexo = registro.get(0).get("sexo");
			
						
			//Si el tipo de persona es proveedor
			if (registro.get(0).get("tipoPersona").equals("P")){				
				if (registro.get(0).get("proveedorServiciosBasicos").equals("true")){					
					psProveedorBasico = "S";
				}
				else{					
					psProveedorBasico = "N";
				}
			}
		
				//psProveedor = "N";
			
			//Valida el checkbox de asociacion
//			if(registro.get(0).get("asociacion").equals("true")){
//				psAsociacion = "S";
//			}
//			else
				//psAsociacion = "N";
			//Checkbox de contrato de inversion
//			if (registro.get(0).get("contratoInversion").equals("true"))
//				psContratoInversion = "S";
//			else
				//psContratoInversion = "N";
						
			//RadioButton de empresa concentradora			
			//psConcentradora = registro.get(0).get("concentradora");
						
			//RadioButton de exportacion
			//psExporta = registro.get(0).get("exporta");
									
			
//			
//			if (registro.get(0).get("tipoPersona").equals("E")){
//				//Division
////				if (registro.get(0).get("division").equals("true"))
////					psDivision = "S";
////				else
//					psDivision = "N";
//				
//				//Pagos Cruzados
////				if (registro.get(0).get("pagosCruzados").equals("true"))
////					psPagosCruzados = "S";
////				else
//					psPagosCruzados = "N";			
//			}			
			
			//psRazonSocial = registro.get(0).get("razonSocial");
			
			psNombre =  registro.get(0).get("nombre");
//			psNombre = psNombre.toUpperCase();
			psPaterno =  registro.get(0).get("paterno");
//			psPaterno = psPaterno.toUpperCase();
//
			psMaterno = registro.get(0).get("materno");
//			psMaterno = psMaterno.toUpperCase();
			
			
			
			//Para actualizar o insertar la informacion
			noEmpresaC = registro.get(0).get("noEmpresa").equals("") ? 0 : Integer.parseInt(registro.get(0).get("noEmpresa"));
			
				objConsultaPersonasDto.setNoEmpresa(registro.get(0).get("noEmpresa").equals("") ? 0 : Integer.parseInt(registro.get(0).get("noEmpresa")));

				
			objConsultaPersonasDto.setNoPersona(registro.get(0).get("noPersona").equals("") ? 0 : Integer.parseInt(registro.get(0).get("noPersona")));			
			
			objConsultaPersonasDto.setTipoPersona(registro.get(0).get("tipoPersona"));

			
			//objConsultaPersonasDto.setDiaLimite(registro.get(0).get("diaLimite").equals("") ? 0 : Integer.parseInt(registro.get(0).get("diaLimite")));
			
			//objConsultaPersonasDto.setDiaRecepcion(registro.get(0).get("diaRecepcion").equals("") ? 0 : Integer.parseInt(registro.get(0).get("diaRecepcion")));
			
//			objConsultaPersonasDto.setIdActividadGenerica(registro.get(0).get("idActividadGenerica").equals("") ? 0 : Integer.parseInt(registro.get(0).get("idActividadGenerica")));
//			
//			objConsultaPersonasDto.setIdActividadEconomica(registro.get(0).get("idActividadEconomica").equals("") ? 0 : Integer.parseInt(registro.get(0).get("idActividadEconomica")));
//			
//			objConsultaPersonasDto.setIdGiro(registro.get(0).get("idGiro").equals("") ? 0 : Integer.parseInt(registro.get(0).get("idGiro")));
			
			//objConsultaPersonasDto.setIdRiesgo(registro.get(0).get("idRiesgo").equals("") ? 0 : Integer.parseInt(registro.get(0).get("idRiesgo")));
			//objConsultaPersonasDto.setNoEmpleados(registro.get(0).get("noEmpleados").equals("") ? 0 :Integer.parseInt(registro.get(0).get("noEmpleados")));
			//objConsultaPersonasDto.setUsuarioModif(registro.get(0).get("usuarioModif").equals("") ? 0 : Integer.parseInt(registro.get(0).get("usuarioModif")));
			//objConsultaPersonasDto.setAccionA(registro.get(0).get("accionesA").equals("") ? 0 : Integer.parseInt(registro.get(0).get("accionesA")));
			//objConsultaPersonasDto.setAccionB(registro.get(0).get("accionesB").equals("") ? 0 : Integer.parseInt(registro.get(0).get("accionesB")));
			//objConsultaPersonasDto.setAccionC(registro.get(0).get("accionesC").equals("") ? 0 : Integer.parseInt(registro.get(0).get("accionesC")));
//			objConsultaPersonasDto.setFechaIngreso(registro.get(0).get("fechaIngreso").equals("") ? registro.get(0).get("fechaHoy") : registro.get(0).get("fechaIngreso"));
//			objConsultaPersonasDto.setFechaHoy(registro.get(0).get("fechaHoy").substring(0, 10));
			
			//objConsultaPersonasDto.setVentasAnuales(registro.get(0).get("ventasAnuales").equals("") ? 0.0 : Double.parseDouble(registro.get(0).get("ventasAnuales")));	
			objConsultaPersonasDto.setRfc(registro.get(0).get("rfc"));	
			//objConsultaPersonasDto.setRazonSocial(psRazonSocial);
			objConsultaPersonasDto.setPaterno(psPaterno);
			objConsultaPersonasDto.setMaterno(psMaterno);
			objConsultaPersonasDto.setNombre(psNombre);			
			objConsultaPersonasDto.setNombreCorto(registro.get(0).get("nombreCorto"));
			//objConsultaPersonasDto.setObjetoSocial(registro.get(0).get("objetoSocial"));
			//objConsultaPersonasDto.setPuesto(registro.get(0).get("puesto"));			
			//objConsultaPersonasDto.setIdEstatus(registro.get(0).get("idEstatus"));
			//objConsultaPersonasDto.setIdTama\u00f1o(registro.get(0).get("idTama\u00f1o"));
			//Calidad y tipo de inmueble
//			if (registro.get(0).get("idCalidad").equals(""))
//				objConsultaPersonasDto.setIdCalidad("0");
//			else
//				objConsultaPersonasDto.setIdCalidad(registro.get(0).get("idCalidad"));
//			
//			
//			if (registro.get(0).get("idInmueble").equals(""))
//				objConsultaPersonasDto.setIdInmueble("0");
//			else
//				objConsultaPersonasDto.setIdInmueble(registro.get(0).get("idInmueble"));
//			//
			//objConsultaPersonasDto.setFisicaMoral(registro.get(0).get("fisicaMoral"));
			//objConsultaPersonasDto.setIdEstadoCivil(registro.get(0).get("idEstadoCivil"));
			//objConsultaPersonasDto.setSexo(registro.get(0).get("sexo"));
//			objConsultaPersonasDto.setBProveedor(psProveedor);
//			objConsultaPersonasDto.setBAsociacion(psAsociacion);
//			objConsultaPersonasDto.setBContratoInversion(psContratoInversion);
//			objConsultaPersonasDto.setIdRubro(registro.get(0).get("idRubro").toString().equals("") ? 0 : Integer.parseInt(registro.get(0).get("idRubro")));
//			objConsultaPersonasDto.setEquivalePersona(registro.get(0).get("equivalePersona"));
//			objConsultaPersonasDto.setPagoReferenciado(registro.get(0).get("pagoReferenciado"));
//			objConsultaPersonasDto.setIdGrupo(registro.get(0).get("grupoRubro").equals("") ? 0 : Integer.parseInt(registro.get(0).get("grupoRubro")));
			//objConsultaPersonasDto.setIdCaja(registro.get(0).get("idCaja").equals("") ? 0 : Integer.parseInt(registro.get(0).get("idCaja")));
//			objConsultaPersonasDto.setIdFormaPagoProv(registro.get(0).get("idFormaPagoProv").equals("") ? 0 : Integer.parseInt(registro.get(0).get("idFormaPagoProv")));			
//			objConsultaPersonasDto.setDivision(psDivision);
//			objConsultaPersonasDto.setPagoCruzado(psPagosCruzados);
//			objConsultaPersonasDto.setExporta(psExporta);
//			objConsultaPersonasDto.setConcentradora(psConcentradora);
//			objConsultaPersonasDto.setIdContrato(registro.get(0).get("contratoTEF").equals("") ? 0 : Integer.parseInt(registro.get(0).get("contratoTEF")));
//			objConsultaPersonasDto.setIdContratoPayment(registro.get(0).get("contratoPayment").equals("") ? 0 : Integer.parseInt(registro.get(0).get("contratoPayment")));
//			objConsultaPersonasDto.setDescBenef(registro.get(0).get("descBenef"));
//			objConsultaPersonasDto.setSGrupoRubro(registro.get(0).get("grupoRubro").toString().equals("") ? 0 : Integer.parseInt(registro.get(0).get("grupoRubro")));
	
			
			if (registro.get(0).get("tipoOperacion").equals("MODIFICAR")){
			
				//Se actualiza los datos en Persona
				recibeDatoEntero = objConsultaPersonasDao.actualizaDatos(objConsultaPersonasDto);
				System.out.println("Actualiza Datos");
			
				System.out.println("##########################");
				System.out.println(recibeDatoEntero);
				System.out.println("##########################");
				if (recibeDatoEntero > 0){
					mensaje = "El registro se actualizo correctamente";
				}else
					mensaje = "Ocurrio un error durante la actualizaci\u00f2n";
				System.out.println("10");
				if (registro.get(0).get("tipoPersona").equals("E"))
				{
					if (!registro.get(0).get("idCaja").equals("") && !registro.get(0).get("idCaja").equals(0)){
						recibeDatoEntero = objConsultaPersonasDao.actualizaCaja(Integer.parseInt(registro.get(0).get("idCaja")), Integer.parseInt(registro.get(0).get("noPersona")));						
					}
					else{
						recibeDatoEntero = objConsultaPersonasDao.actualizaCaja(-1, Integer.parseInt(registro.get(0).get("noPersona")));
					}					
				}
				
				if(registro.get(0).get("tipoPersona").equals("P")){					
					recibeDatoEntero = objConsultaPersonasDao.actualizaProveedor(Integer.parseInt(registro.get(0).get("noPersona")), 
																				 Integer.parseInt(registro.get(0).get("noEmpresa")), psProveedorBasico);					
				}
				
				if (registro.get(0).get("tipoPersona").equals("E") || registro.get(0).get("tipoPersona").equals("I")){
					recibeDatoEntero = objConsultaPersonasDao.actualizaEmpresa(objConsultaPersonasDto);
				}		
				
			}//Termina la funcion del update
			
			else if (registro.get(0).get("tipoOperacion").equals("INSERTAR")){// para hacer un insert de una nueva persona	
					
				if (registro.get(0).get("tipoPersona").equals("K") || registro.get(0).get("tipoPersona").equals("P"))
					objConsultaPersonasDto.setNoEmpresa(552);
				else
					objConsultaPersonasDto.setNoEmpresa(Integer.parseInt(registro.get(0).get("noEmpresa")));
							
				if (registro.get(0).get("tipoPersona").equals("R") || registro.get(0).get("tipoPersona").equals("F")){					
//					objConsultaPersonasDto.setNoPersona(objConsultaPersonasDao.seleccionarFolioReal("no_persona"));
//					objConsultaPersonasDao.actualizarFolioReal("no_persona");
					objConsultaPersonasDto.setNoPersona(globalDao.obtenerFolioReal("no_persona"));

				}
				else{					
					if (registro.get(0).get("noPersona").equals("") || registro.get(0).get("noPersona").equals("0")){						
						noPersona = Integer.parseInt(registro.get(0).get("equivalePersona"));						
						}					
					else
						noPersona = Integer.parseInt(registro.get(0).get("noPersona"));
					
					objConsultaPersonasDto.setNoPersona(noPersona);
				}					

				if (!registro.get(0).get("tipoPersona").equals("E") && !registro.get(0).get("tipoPersona").equals("P") &&
					!registro.get(0).get("tipoPersona").equals("C") && !registro.get(0).get("tipoPersona").equals("I") &&
					!registro.get(0).get("tipoPersona").equals("R") && !registro.get(0).get("tipoPersona").equals("K")){
					
					objConsultaPersonasDto.setNoEmpresa(1);
//					objConsultaPersonasDto.setNoPersona(objConsultaPersonasDao.seleccionarFolioReal("no_persona"));
//					objConsultaPersonasDao.actualizarFolioReal("no_persona");
					objConsultaPersonasDto.setNoPersona(globalDao.obtenerFolioReal("no_persona"));

				}
				
				if (registro.get(0).get("tipoPersona").equals("I") || registro.get(0).get("tipoPersona").equals("E") ||
					registro.get(0).get("tipoPersona").equals("P") || registro.get(0).get("tipoPersona").equals("C") ||
					registro.get(0).get("tipoPersona").equals("R")){
//					piNoCuenta = objConsultaPersonasDao.seleccionarFolioReal("no_cuenta");
//					objConsultaPersonasDao.actualizarFolioReal("no_cuenta");
//					piNoLinea = objConsultaPersonasDao.seleccionarFolioReal("no_linea");
//					objConsultaPersonasDao.actualizarFolioReal("no_linea");
					piNoCuenta = globalDao.obtenerFolioReal("no_cuenta");
					piNoLinea = globalDao.obtenerFolioReal("no_linea");
					
					objConsultaPersonasDto.setNoLinea(piNoLinea);
					objConsultaPersonasDto.setNoCuenta(piNoCuenta);
				}
				recibeDatoEntero = objConsultaPersonasDao.existePersona(objConsultaPersonasDto.getNoPersona(), objConsultaPersonasDto.getNoEmpresa(), registro.get(0).get("tipoPersona"));
				
				if (recibeDatoEntero > 0){
					mensaje = "El n\u00famero de persona ya existe";
				}
				else{				
					if (registro.get(0).get("tipoPersona").equals("E"))
						/////////////Revisar noPersona
						objConsultaPersonasDto.setNoEmpresa(noPersona);
					//envia datos para insertar en persona
					recibeDatoEntero = objConsultaPersonasDao.insertaDatos(objConsultaPersonasDto, piNoCuenta, piNoLinea, noEmpresaC);				
					if (recibeDatoEntero > 0)
						mensaje = "El registro se inserto correctamente";
					else
						mensaje = "Ocurrio un error al almacenar el registro";
					
					recibeCadena = objConsultaPersonasDao.configuraSet(268);
					
					if (recibeCadena.equals("SI"))
						//actualiza la inst finan de la empresa
						objConsultaPersonasDao.actualizaInstFinanciera(noPersona, "NULL");
					
					if (!registro.get(0).get("noEmpresa").equals(""))
						objConsultaPersonasDao.actualizaInstFinanciera(noPersona, registro.get(0).get("noEmpresa"));
					
					if (registro.get(0).get("tipoPersona").equals("I") || registro.get(0).get("tipoPersona").equals("E")){
						noEmpresa = noPersona;
						GlobalSingleton globalSingleton = GlobalSingleton.getInstancia();
						System.out.println("<----------------------------->");
						System.out.println(globalSingleton.getUsuarioLoginDto().getIdUsuario());
						
						
												
						objConsultaPersonasDto.setNoEmpresa(noEmpresa);
						//insertar datos en empresa
						recibeDatoEntero = objConsultaPersonasDao.insertaEmpresa(objConsultaPersonasDto);
						//insertar en linea
						recibeDatoEntero = objConsultaPersonasDao.insertaLinea(noEmpresa,piNoLinea, registro.get(0).
								get("fechaHoy"),globalSingleton.getUsuarioLoginDto().getIdUsuario() );
						
						recibeDatoEntero = objConsultaPersonasDao.insertaCuenta(noEmpresa, piNoLinea, noPersona, piNoCuenta,
								   registro.get(0).get("fechaHoy"),globalSingleton.getUsuarioLoginDto().getIdUsuario() );
						//envia los datos para insertar en saldos
						recibeDatoEntero = objConsultaPersonasDao.insertaSaldos(noEmpresa, piNoLinea, piNoCuenta, 90);
						recibeDatoEntero = objConsultaPersonasDao.insertaSaldos(noEmpresa, piNoLinea, piNoCuenta, 91);
						recibeDatoEntero = objConsultaPersonasDao.insertaSaldos(noEmpresa, piNoLinea, piNoCuenta, 8);
						recibeDatoEntero = objConsultaPersonasDao.insertaSaldos(noEmpresa, piNoLinea, piNoCuenta, 9);						
					}
					else if (registro.get(0).get("tipoPersona").equals("P")){
						noEmpresa = Integer.parseInt(registro.get(0).get("noEmpresa"));
						recibeDatoEntero = objConsultaPersonasDao.obtieneCuentaEmpresa(noEmpresa);
						if (recibeDatoEntero > 0)
							noCuentaEmp = recibeDatoEntero;
						else
							noCuentaEmp = 0;
						System.out.println(objConsultaPersonasDao==null);
						System.out.println(registro.get(0).get("fechaHoy")==null);
						System.out.println(registro.get(0).get("usuarioModif")==null);
						GlobalSingleton globalSingleton = GlobalSingleton.getInstancia();
						objConsultaPersonasDao.insertaProveedor(noEmpresa, noPersona,globalSingleton.getUsuarioLoginDto().getIdUsuario() ,
																registro.get(0).get("fechaHoy"), noCuentaEmp, false, "B");
						/*GlobalSingleton globalSingleton = GlobalSingleton.getInstancia();
						objConsultaPersonasDao.insertaProveedor(noEmpresa, noPersona,globalSingleton.getUsuarioLoginDto().getIdUsuario(), registro.get(0).get("fechaHoy"), noCuentaEmp, false, "B");
					*/
					}
					
					if (registro.get(0).get("tipoPersona").equals("I") || registro.get(0).get("tipoPersona").equals("E") 
						|| registro.get(0).get("tipoPersona").equals("P") || registro.get(0).get("tipoPersona").equals("C")){
						mensaje = "Datos registrados con n\u00famero de persona "  + " y N\u00famero de Empresa " + noEmpresa;
					}
					else{
						mensaje = "Datos registrados con n\u00famero de persona " + registro.get(0).get("noPersona");
					}					
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: guardarPF");
		}return mensaje;
	}
	//////////////////////////////
	
/* ***************************************GUARDAR DIRECCION**************************************************
************************************************************************************************************ */
	
	public String guardarDireccion (List<Map<String, String>> registro){
		String mensaje = "";
//		String sexo = "";
//		String psProveedorBasico = "";
//		String psProveedor = "";
//		String psAsociacion = "";
//		String psContratoInversion = "";
//		String psConcentradora = "";
//		String psExporta = "";
//		String psDivision = "";
//		String psPagosCruzados = "";
//		int piContratoTEF = 0;
//		int piContratoPayment = 0;		
		int recibeDatoEntero = 0;
//		int piNoCuenta = 0;
//		int piNoLinea = 0;
		int noPersona = 0;
//		String psRazonSocial = "";
//		String psNombre = "";
//		String psPaterno = "";
//		String psMaterno = "";
		String recibeCadena = "";
		int noEmpresa = 0;
//		int noCuentaEmp = 0;
//		int noEmpresaC = 0;
		
		try{
			
		//	psRazonSocial = registro.get(0).get("razonSocial");
			
//			psNombre =  "NULL";
//			psPaterno =  "NULL";
//			psMaterno = "NULL";
			
			objConsultaPersonasDto.setNoEmpresa(registro.get(0).get("noEmpresa")==null || registro.get(0).get("noEmpresa").equals("") ? 0 : Integer.parseInt(registro.get(0).get("noEmpresa")));			
			objConsultaPersonasDto.setTipoPersona(registro.get(0).get("tipoPersona"));
			objConsultaPersonasDto.setPersona(registro.get(0).get("persona")==null || registro.get(0).get("persona").equals("") ? 0 : Integer.parseInt(registro.get(0).get("persona")));
			objConsultaPersonasDto.setRfc(registro.get(0).get("rfc"));	
			objConsultaPersonasDto.setRazonSocialDI(registro.get(0).get("razonSocial"));
			objConsultaPersonasDto.setIdTipoD(registro.get(0).get("idTipoD"));
			objConsultaPersonasDto.setCalle(registro.get(0).get("calle"));
			objConsultaPersonasDto.setColonia(registro.get(0).get("colonia"));
			objConsultaPersonasDto.setCp(registro.get(0).get("cp"));
			objConsultaPersonasDto.setDelegacion(registro.get(0).get("delegacion"));
			objConsultaPersonasDto.setCiudad(registro.get(0).get("ciudad"));
			objConsultaPersonasDto.setEstado(registro.get(0).get("estado"));
			objConsultaPersonasDto.setPais(registro.get(0).get("pais"));

			//vector.tipoOperacion = "MODIFICAR"
			if (registro.get(0).get("tipoOperacion").equals("MODIFICAR")){
				System.out.println("Entra a modificar Direcciones");
				//Se actualiza los datos en Persona
				recibeDatoEntero = objConsultaPersonasDao.actualizaDireccion(objConsultaPersonasDto);
			System.out.println(recibeDatoEntero);
				if (recibeDatoEntero > 0)
					mensaje = "El registro se actualizo correctamente";
				else
					mensaje = "Ocurrio un error durante la actualizaci\u00f2n";
				System.out.println("10");
				
				
			}//Termina la funcion del update
			
			else if (registro.get(0).get("tipoOperacion").equals("INSERTAR")){// para hacer un insert de una nueva persona	
					System.out.println("Entra a insertar");
			
				recibeDatoEntero = objConsultaPersonasDao.existeDireccion(objConsultaPersonasDto.getPersona(), objConsultaPersonasDto , registro.get(0).get("tipoPersona"));
				
				if (recibeDatoEntero > 0){
					mensaje = "Esta persona ya cuenta con direccion";
				}
				else{				
						objConsultaPersonasDto.setNoEmpresa(noPersona);
					///Revisar
						System.out.println("#############################"); 
						recibeDatoEntero = objConsultaPersonasDao.insertaDireccion(objConsultaPersonasDto);	
					System.out.println("#############################");
					System.out.println(recibeDatoEntero);
					if (recibeDatoEntero > 0)
						mensaje = "La dirección se almaceno correctamente";
					else
						mensaje = "Ocurrio un error al almacenar el registro";
					
					recibeCadena = objConsultaPersonasDao.configuraSet(268);
					
					if (recibeCadena.equals("SI"))
						objConsultaPersonasDao.actualizaInstFinanciera(noPersona, "NULL");
					
						noEmpresa = noPersona;
						GlobalSingleton globalSingleton = GlobalSingleton.getInstancia();
						System.out.println("<----------------------------->");
						System.out.println(globalSingleton.getUsuarioLoginDto().getIdUsuario());
						
						objConsultaPersonasDto.setNoEmpresa(noEmpresa);
						
					}
				
//				if (recibeDatoEntero > 0){
//					mensaje = "La direccion fue registrada con exito";
//				}
//				else{		
//					mensaje = "Ocurrio un error durante el registro";
//				}	
				}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: guardarDireccion");
		}return mensaje;
	}
	
	/* ***************************************GUARDAR CASA DE CAMBIO**************************************************
	************************************************************************************************************ */
	
	
	public String guardarCasa (List<Map<String, String>> registro){
		String mensaje = "";
//		String sexo = "";
		String psProveedorBasico = "";
//		String psProveedor = "";
//		String psAsociacion = "";
//		String psContratoInversion = "";
//		String psConcentradora = "";
//		String psExporta = "";
//		String psDivision = "";
//		String psPagosCruzados = "";
//		int piContratoTEF = 0;
//		int piContratoPayment = 0;		
		int recibeDatoEntero = 0;
		int piNoCuenta = 0;
		int piNoLinea = 0;
		int noPersona = 0;
		String psRazonSocial = "";
		String psNombre = "";
		String psPaterno = "";
		String psMaterno = "";
		String recibeCadena = "";
		int noEmpresa = 0;
		int noCuentaEmp = 0;
		int noEmpresaC = 0;
		
		try{
			
			noPersona = registro.get(0).get("noPersona").equals("") ? 0 : Integer.parseInt(registro.get(0).get("noPersona"));
					
			//Valida sexo							
			//sexo = registro.get(0).get("sexo");
					
			//Si el tipo de persona es proveedor
			if (registro.get(0).get("tipoPersona").equals("P")){				
				if (registro.get(0).get("proveedorServiciosBasicos").equals("true")){					
					psProveedorBasico = "S";
				}
				else{					
					psProveedorBasico = "N";
				}
			}
	
//				psProveedor = "N";
//				psAsociacion = "N";
//				psContratoInversion = "N";
						
			//RadioButton de empresa concentradora			
//			psConcentradora = registro.get(0).get("concentradora");
						
			//RadioButton de exportacion
//			psExporta = registro.get(0).get("exporta");
									
//			if (registro.get(0).get("tipoPersona").equals("E")){
//				
//					psDivision = "N";
//					psPagosCruzados = "N";			
//			}			
			
			psRazonSocial = registro.get(0).get("razonSocial");
			
			psNombre =  "NULL";
			psPaterno =  "NULL";
			psMaterno = "NULL";
			
			objConsultaPersonasDto.setNoPersona(registro.get(0).get("noPersona").equals("") ? 0 : Integer.parseInt(registro.get(0).get("noPersona")));
			objConsultaPersonasDto.setTipoPersona(registro.get(0).get("tipoPersona"));
			objConsultaPersonasDto.setFechaIngreso(registro.get(0).get("fechaIngreso").equals("") ? registro.get(0).get("fechaHoy") : registro.get(0).get("fechaIngreso"));
			objConsultaPersonasDto.setFechaHoy(registro.get(0).get("fechaHoy").substring(0, 10));
			objConsultaPersonasDto.setRfc(registro.get(0).get("rfc"));	
			objConsultaPersonasDto.setRazonSocial(psRazonSocial);
			objConsultaPersonasDto.setPaterno(psPaterno);
			objConsultaPersonasDto.setMaterno(psMaterno);
			objConsultaPersonasDto.setNombre(psNombre);			
			objConsultaPersonasDto.setNombreCorto(registro.get(0).get("nombreCorto"));

			//vector.tipoOperacion = "MODIFICAR"
			if (registro.get(0).get("tipoOperacion").equals("MODIFICAR")){
			
				//Se actualiza los datos en Persona
				recibeDatoEntero = objConsultaPersonasDao.actualizaDatos(objConsultaPersonasDto);
				System.out.println(recibeDatoEntero);
				if (recibeDatoEntero > 0)
					mensaje = "El registro se actualizo correctamente";
				else
					mensaje = "Ocurrio un error durante la actualizaci\u00f2n";
				System.out.println("10");
				if (registro.get(0).get("tipoPersona").equals("E"))
				{
					if (!registro.get(0).get("idCaja").equals("") && !registro.get(0).get("idCaja").equals(0)){
						recibeDatoEntero = objConsultaPersonasDao.actualizaCaja(Integer.parseInt(registro.get(0).get("idCaja")), Integer.parseInt(registro.get(0).get("noEmpresa")));						
					}
					else{
						recibeDatoEntero = objConsultaPersonasDao.actualizaCaja(-1, Integer.parseInt(registro.get(0).get("noPersona")));
					}					
				}
				
				if(registro.get(0).get("tipoPersona").equals("P")){					
					recibeDatoEntero = objConsultaPersonasDao.actualizaProveedor(Integer.parseInt(registro.get(0).get("noPersona")), 
																				 Integer.parseInt(registro.get(0).get("noEmpresa")), psProveedorBasico);					
				}
				
				if (registro.get(0).get("tipoPersona").equals("E") || registro.get(0).get("tipoPersona").equals("I")){
					recibeDatoEntero = objConsultaPersonasDao.actualizaEmpresa(objConsultaPersonasDto);
				}		
				
			}//Termina la funcion del update
			
			else if (registro.get(0).get("tipoOperacion").equals("INSERTAR")){// para hacer un insert de una nueva persona	
					
				if (registro.get(0).get("tipoPersona").equals("K") || registro.get(0).get("tipoPersona").equals("P"))
					objConsultaPersonasDto.setNoEmpresa(552);
				else
					objConsultaPersonasDto.setNoEmpresa(Integer.parseInt(registro.get(0).get("noEmpresa")));
							
				if (registro.get(0).get("tipoPersona").equals("R") || registro.get(0).get("tipoPersona").equals("F")){					
//					objConsultaPersonasDto.setNoPersona(objConsultaPersonasDao.seleccionarFolioReal("no_persona"));
//					objConsultaPersonasDao.actualizarFolioReal("no_persona");
					objConsultaPersonasDto.setNoPersona(globalDao.obtenerFolioReal("no_persona"));
				
				}
				else{					
				
					objConsultaPersonasDto.setNoPersona(noPersona);
				}				

				if (!registro.get(0).get("tipoPersona").equals("E") && !registro.get(0).get("tipoPersona").equals("P") &&
					!registro.get(0).get("tipoPersona").equals("C") && !registro.get(0).get("tipoPersona").equals("I") &&
					!registro.get(0).get("tipoPersona").equals("R") && !registro.get(0).get("tipoPersona").equals("K")){
					
					objConsultaPersonasDto.setNoEmpresa(1);
					 //objConsultaPersonasDto.setNoPersona(objConsultaPersonasDao.seleccionarFolioReal("no_persona"));
					 objConsultaPersonasDto.setNoPersona(globalDao.seleccionarFolio("no_persona"));
				}
				
				if (registro.get(0).get("tipoPersona").equals("I") || registro.get(0).get("tipoPersona").equals("E") ||
					registro.get(0).get("tipoPersona").equals("P") || registro.get(0).get("tipoPersona").equals("C") ||
					registro.get(0).get("tipoPersona").equals("R")){
//					piNoCuenta = objConsultaPersonasDao.seleccionarFolioReal("no_cuenta");
//					objConsultaPersonasDao.actualizarFolioReal("no_cuenta");
//					piNoLinea = objConsultaPersonasDao.seleccionarFolioReal("no_linea");
//					objConsultaPersonasDao.actualizarFolioReal("no_linea");
					piNoCuenta = globalDao.obtenerFolioReal("no_cuenta");
					piNoLinea = globalDao.obtenerFolioReal("no_linea");
					
					objConsultaPersonasDto.setNoLinea(piNoLinea);
					objConsultaPersonasDto.setNoCuenta(piNoCuenta);
				}
				recibeDatoEntero = objConsultaPersonasDao.existePersona(objConsultaPersonasDto.getNoPersona(), objConsultaPersonasDto.getNoEmpresa(), registro.get(0).get("tipoPersona"));
				
				if (recibeDatoEntero > 0){
					mensaje = "El n\u00famero de persona ya existe";
				}
				else{				
					if (registro.get(0).get("tipoPersona").equals("E"))
						/////////////Revisar noPersona
						objConsultaPersonasDto.setNoEmpresa(noPersona);
					
					recibeDatoEntero = objConsultaPersonasDao.insertaDatos(objConsultaPersonasDto, piNoCuenta, piNoLinea, noEmpresaC);				
					if (recibeDatoEntero > 0)
						mensaje = "El registro se inserto correctamente";
					else
						mensaje = "Ocurrio un error al almacenar el registro";
					
					recibeCadena = objConsultaPersonasDao.configuraSet(268);
					
					if (recibeCadena.equals("SI"))
						objConsultaPersonasDao.actualizaInstFinanciera(noPersona, "NULL");
					
					if (registro.get(0).get("tipoPersona").equals("I") || registro.get(0).get("tipoPersona").equals("E")){
						noEmpresa = noPersona;
						GlobalSingleton globalSingleton = GlobalSingleton.getInstancia();
						System.out.println("<----------------------------->");
						System.out.println(globalSingleton.getUsuarioLoginDto().getIdUsuario());
						
						objConsultaPersonasDto.setNoEmpresa(noEmpresa);
						recibeDatoEntero = objConsultaPersonasDao.insertaEmpresa(objConsultaPersonasDto);
						
						recibeDatoEntero = objConsultaPersonasDao.insertaLinea(noEmpresa, piNoLinea, registro.get(0).
								get("fechaHoy"),globalSingleton.getUsuarioLoginDto().getIdUsuario() );
						
						recibeDatoEntero = objConsultaPersonasDao.insertaCuenta(noEmpresa, piNoLinea, noPersona, piNoCuenta,
																			   registro.get(0).get("fechaHoy"),globalSingleton.getUsuarioLoginDto().getIdUsuario() );
												
						
						
						recibeDatoEntero = objConsultaPersonasDao.insertaSaldos(noEmpresa, piNoLinea, piNoCuenta, 90);
						recibeDatoEntero = objConsultaPersonasDao.insertaSaldos(noEmpresa, piNoLinea, piNoCuenta, 91);
						recibeDatoEntero = objConsultaPersonasDao.insertaSaldos(noEmpresa, piNoLinea, piNoCuenta, 8);
						recibeDatoEntero = objConsultaPersonasDao.insertaSaldos(noEmpresa, piNoLinea, piNoCuenta, 9);						
					}
					else if (registro.get(0).get("tipoPersona").equals("P")){
						noEmpresa = Integer.parseInt(registro.get(0).get("noEmpresa"));
						recibeDatoEntero = objConsultaPersonasDao.obtieneCuentaEmpresa(noEmpresa);
						if (recibeDatoEntero > 0)
							noCuentaEmp = recibeDatoEntero;
						else
							noCuentaEmp = 0;
						
						objConsultaPersonasDao.insertaProveedor(noEmpresa, noPersona, Integer.parseInt(registro.get(0).get("usuarioModif")),
																registro.get(0).get("fechaHoy"), noCuentaEmp, false, "B");						
					}
					
					if (registro.get(0).get("tipoPersona").equals("I") || registro.get(0).get("tipoPersona").equals("E") 
						|| registro.get(0).get("tipoPersona").equals("P") || registro.get(0).get("tipoPersona").equals("C")){
						mensaje = "Datos registrados con n\u00famero de persona "  + " y N\u00famero de Empresa " + noEmpresa;
						
						
					}
					else{
						mensaje = "Datos registrados con n\u00famero de persona " + registro.get(0).get("noPersona");
					}					
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: guardarCasa");
		}return mensaje;
	}
	
	
	
	public String guardaNuevoMedioContacto (String no_persona,String mail,int empre){
		String mensaje="";
		try{
			objConsultaPersonasDao.guardaNuevoMedioContacto ( no_persona,mail, empre);
		
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: guardarCasa");
		}return mensaje;
	}
	
	/* ***************************************GUARDAR PERSONA FISICA**************************************************
	************************************************************************************************************ */
	
	
	public String guardarF (List<Map<String, String>> registro){

		String mensaje = "";
//		String sexo = "";
		String psProveedorBasico = "";
//		String psProveedor = "";
//		String psAsociacion = "";
//		String psContratoInversion = "";
//		String psConcentradora = "";
//		String psExporta = "";
//		String psDivision = "";
//		String psPagosCruzados = "";
//		int piContratoTEF = 0;
//		int piContratoPayment = 0;		
		int recibeDatoEntero = 0;
		int piNoCuenta = 0;
		int piNoLinea = 0;
		int noPersona = 0;
//		String psRazonSocial = "";
//		String psNombre = "";
//		String psPaterno = "";
//		String psMaterno = "";
		String recibeCadena = "";
		int noEmpresa = 0;
		int noCuentaEmp = 0;
		int noEmpresaC = 0;
		
		try{
			
			noPersona = registro.get(0).get("noPersona").equals("") ? 0 : Integer.parseInt(registro.get(0).get("noPersona"));
					
			//Valida sexo							
//			sexo = registro.get(0).get("sexo");
			
						
			//Si el tipo de persona es proveedor
			if (registro.get(0).get("tipoPersona").equals("P")){				
				if (registro.get(0).get("proveedorServiciosBasicos").equals("true")){					
					psProveedorBasico = "S";
				}
				else{					
					psProveedorBasico = "N";
				}
			}
			
//				psProveedor = "N";
//				psAsociacion = "N";
//				psContratoInversion = "N";
						
			//RadioButton de empresa concentradora			
//			psConcentradora = registro.get(0).get("concentradora");
						
			//RadioButton de exportacion
//			psExporta = registro.get(0).get("exporta");
									
//			if (registro.get(0).get("tipoPersona").equals("E")){
//			
//					psDivision = "N";
//					psPagosCruzados = "N";			
//			}			
			
//			psRazonSocial = registro.get(0).get("razonSocial");
			
			//objConsultaPersonasDto.setNoEmpresa(registro.get(0).get("noEmpresa").equals("") ? 0 : Integer.parseInt(registro.get(0).get("noEmpresa")));
			objConsultaPersonasDto.setNoPersona(registro.get(0).get("noPersona").equals("") ? 0 : Integer.parseInt(registro.get(0).get("noPersona")));
			objConsultaPersonasDto.setTipoPersona(registro.get(0).get("tipoPersona"));
			objConsultaPersonasDto.setRfc(registro.get(0).get("rfc"));	
			objConsultaPersonasDto.setPaterno(registro.get(0).get("paterno"));
			objConsultaPersonasDto.setMaterno(registro.get(0).get("materno"));
			objConsultaPersonasDto.setNombre(registro.get(0).get("nombre"));			
			objConsultaPersonasDto.setNombreCorto(registro.get(0).get("nombreCorto"));
			objConsultaPersonasDto.setSexo(registro.get(0).get("sexo"));
			
			System.out.println("4");
			//vector.tipoOperacion = "MODIFICAR"
			if (registro.get(0).get("tipoOperacion").equals("MODIFICAR")){
				System.out.println("5");
				//Se actualiza los datos en Persona
				recibeDatoEntero = objConsultaPersonasDao.actualizaPersonaFisica(objConsultaPersonasDto);
				System.out.println(recibeDatoEntero);
				if (recibeDatoEntero > 0)
					mensaje = "El registro se actualizo correctamente";
				else
					mensaje = "Ocurrio un error durante la actualizaci\u00f2n";
				System.out.println("10");
				if (registro.get(0).get("tipoPersona").equals("E"))
				{
					if (!registro.get(0).get("idCaja").equals("") && !registro.get(0).get("idCaja").equals(0)){
						recibeDatoEntero = objConsultaPersonasDao.actualizaCaja(Integer.parseInt(registro.get(0).get("idCaja")), Integer.parseInt(registro.get(0).get("noEmpresa")));						
					}
					else{
						recibeDatoEntero = objConsultaPersonasDao.actualizaCaja(-1, Integer.parseInt(registro.get(0).get("noPersona")));
					}					
				}
				
				if(registro.get(0).get("tipoPersona").equals("P")){					
					recibeDatoEntero = objConsultaPersonasDao.actualizaProveedor(Integer.parseInt(registro.get(0).get("noPersona")), 
																				 Integer.parseInt(registro.get(0).get("noEmpresa")), psProveedorBasico);					
				}
				
				if (registro.get(0).get("tipoPersona").equals("E") || registro.get(0).get("tipoPersona").equals("I")){
					recibeDatoEntero = objConsultaPersonasDao.actualizaEmpresa(objConsultaPersonasDto);
				}		
				
			}//Termina la funcion del update
			
			else if (registro.get(0).get("tipoOperacion").equals("INSERTAR")){// para hacer un insert de una nueva persona	
				System.out.println("6");
				if (registro.get(0).get("tipoPersona").equals("K") || registro.get(0).get("tipoPersona").equals("P"))
					objConsultaPersonasDto.setNoEmpresa(552);
//				else
//					objConsultaPersonasDto.setNoEmpresa(Integer.parseInt(registro.get(0).get("noEmpresa")));
							
				if (registro.get(0).get("tipoPersona").equals("R") || registro.get(0).get("tipoPersona").equals("F")){	
					System.out.println("7");
					//objConsultaPersonasDto.setNoPersona(objConsultaPersonasDao.seleccionarFolioReal("no_persona"));
					objConsultaPersonasDto.setNoPersona(globalDao.seleccionarFolio("no_persona"));
					//objConsultaPersonasDao.actualizarFolioReal("no_persona");ya estaba comentada
				}
				else{					
					objConsultaPersonasDto.setNoPersona(noPersona);
				}				

				if (!registro.get(0).get("tipoPersona").equals("E") && !registro.get(0).get("tipoPersona").equals("P") &&
					!registro.get(0).get("tipoPersona").equals("C") && !registro.get(0).get("tipoPersona").equals("I") &&
					!registro.get(0).get("tipoPersona").equals("R") && !registro.get(0).get("tipoPersona").equals("K")){
					System.out.println("8");
					objConsultaPersonasDto.setNoEmpresa(1);
//					objConsultaPersonasDto.setNoPersona(objConsultaPersonasDao.seleccionarFolioReal("no_persona"));
//					objConsultaPersonasDao.actualizarFolioReal("no_persona");
					objConsultaPersonasDto.setNoPersona(globalDao.obtenerFolioReal("no_persona"));
					
				}
				
				if (registro.get(0).get("tipoPersona").equals("I") || registro.get(0).get("tipoPersona").equals("E") ||
					registro.get(0).get("tipoPersona").equals("P") || registro.get(0).get("tipoPersona").equals("C") ||
					registro.get(0).get("tipoPersona").equals("R")){
					System.out.println("9");
//					piNoCuenta = objConsultaPersonasDao.seleccionarFolioReal("no_cuenta");
//					objConsultaPersonasDao.actualizarFolioReal("no_cuenta");
//					piNoLinea = objConsultaPersonasDao.seleccionarFolioReal("no_linea");
//					objConsultaPersonasDao.actualizarFolioReal("no_linea");
					piNoCuenta = globalDao.obtenerFolioReal("no_cuenta");
					piNoLinea = globalDao.obtenerFolioReal("no_linea");
					
					objConsultaPersonasDto.setNoLinea(piNoLinea);
					objConsultaPersonasDto.setNoCuenta(piNoCuenta);
				}
				recibeDatoEntero = objConsultaPersonasDao.existePersona(objConsultaPersonasDto.getNoPersona(), objConsultaPersonasDto.getNoEmpresa(), registro.get(0).get("tipoPersona"));
				
				if (recibeDatoEntero > 0){
					mensaje = "El n\u00famero de persona ya existe";
				}
				else{				
					if (registro.get(0).get("tipoPersona").equals("E"))
						/////////////Revisar noPersona
						objConsultaPersonasDto.setNoEmpresa(noPersona);
					
					recibeDatoEntero = objConsultaPersonasDao.insertaDatos(objConsultaPersonasDto, piNoCuenta, piNoLinea, noEmpresaC);				
					if (recibeDatoEntero > 0)
						mensaje = "El registro se inserto correctamente";
					else
						mensaje = "Ocurrio un error al almacenar el registro";
					
					recibeCadena = objConsultaPersonasDao.configuraSet(268);
					
					if (recibeCadena.equals("SI"))
						objConsultaPersonasDao.actualizaInstFinanciera(noPersona, "NULL");
					
					if (registro.get(0).get("tipoPersona").equals("I") || registro.get(0).get("tipoPersona").equals("E")){
						noEmpresa = noPersona;
						GlobalSingleton globalSingleton = GlobalSingleton.getInstancia();
						System.out.println("<----------------------------->");
						System.out.println(globalSingleton.getUsuarioLoginDto().getIdUsuario());
						
						objConsultaPersonasDto.setNoEmpresa(noEmpresa);
						recibeDatoEntero = objConsultaPersonasDao.insertaEmpresa(objConsultaPersonasDto);
						
						recibeDatoEntero = objConsultaPersonasDao.insertaLinea(noEmpresa, piNoLinea, registro.get(0).
								get("fechaHoy"),globalSingleton.getUsuarioLoginDto().getIdUsuario() );
						
						recibeDatoEntero = objConsultaPersonasDao.insertaCuenta(noEmpresa, piNoLinea, noPersona, piNoCuenta,
																			   registro.get(0).get("fechaHoy"),globalSingleton.getUsuarioLoginDto().getIdUsuario() );
												
						recibeDatoEntero = objConsultaPersonasDao.insertaSaldos(noEmpresa, piNoLinea, piNoCuenta, 90);
						recibeDatoEntero = objConsultaPersonasDao.insertaSaldos(noEmpresa, piNoLinea, piNoCuenta, 91);
						recibeDatoEntero = objConsultaPersonasDao.insertaSaldos(noEmpresa, piNoLinea, piNoCuenta, 8);
						recibeDatoEntero = objConsultaPersonasDao.insertaSaldos(noEmpresa, piNoLinea, piNoCuenta, 9);						
					}
					else if (registro.get(0).get("tipoPersona").equals("P")){
						noEmpresa = Integer.parseInt(registro.get(0).get("noEmpresa"));
						recibeDatoEntero = objConsultaPersonasDao.obtieneCuentaEmpresa(noEmpresa);
						if (recibeDatoEntero > 0)
							noCuentaEmp = recibeDatoEntero;
						else
							noCuentaEmp = 0;
						
						objConsultaPersonasDao.insertaProveedor(noEmpresa, noPersona, Integer.parseInt(registro.get(0).get("usuarioModif")),
																registro.get(0).get("fechaHoy"), noCuentaEmp, false, "B");						
					}
					
					if (registro.get(0).get("tipoPersona").equals("I") || registro.get(0).get("tipoPersona").equals("E") 
						|| registro.get(0).get("tipoPersona").equals("P") || registro.get(0).get("tipoPersona").equals("C")){
						mensaje = "Datos registrados con n\u00famero de persona "  + " y N\u00famero de Empresa " + noEmpresa;
						
						
					}
					else{
						mensaje = "Datos registrados con n\u00famero de persona " + registro.get(0).get("noPersona");
					}					
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: guardarF");
		}return mensaje;
	}
	
	
	public String guardarIB (List<Map<String, String>> registro){
		String mensaje = "";
//		String sexo = "";
		String psProveedorBasico = "";
//		String psProveedor = "";
//		String psAsociacion = "";
//		String psContratoInversion = "";
//		String psConcentradora = "";
//		String psExporta = "";
//		String psDivision = "";
//		String psPagosCruzados = "";
//		int piContratoTEF = 0;
//		int piContratoPayment = 0;		
		int recibeDatoEntero = 0;
		int piNoCuenta = 0;
		int piNoLinea = 0;
		int noPersona = 0;
		String psRazonSocial = "";
		String psNombre = "";
		String psPaterno = "";
		String psMaterno = "";
		String recibeCadena = "";
		int noEmpresa = 0;
		int noCuentaEmp = 0;
		int noEmpresaC = 0;
		
		try{
			
			noPersona = registro.get(0).get("noPersona").equals("") ? 0 : Integer.parseInt(registro.get(0).get("noPersona"));
					
			//Valida sexo							
//			sexo = registro.get(0).get("sexo");
			
						
			//Si el tipo de persona es proveedor
			if (registro.get(0).get("tipoPersona").equals("P")){				
				if (registro.get(0).get("proveedorServiciosBasicos").equals("true")){					
					psProveedorBasico = "S";
				}
				else{					
					psProveedorBasico = "N";
				}
			}
			

//				psProveedor = "N";
//				psContratoInversion = "N";
			//RadioButton de empresa concentradora			
//			psConcentradora = registro.get(0).get("concentradora");
			//RadioButton de exportacion
//			psExporta = registro.get(0).get("exporta");
									
			
			
//			if (registro.get(0).get("tipoPersona").equals("E")){
//				psDivision = "N";
//				psPagosCruzados = "N";			
//			}			
			
			psRazonSocial = registro.get(0).get("razonSocial");
			
			psNombre =  "NULL";
			psPaterno =  "NULL";
			psMaterno = "NULL";
			
			
			
			//Para actualizar o insertar la informacion
			
			objConsultaPersonasDto.setNoPersona(registro.get(0).get("noPersona").equals("") ? 0 : Integer.parseInt(registro.get(0).get("noPersona")));
			objConsultaPersonasDto.setTipoPersona(registro.get(0).get("tipoPersona"));
			objConsultaPersonasDto.setFechaIngreso(registro.get(0).get("fechaIngreso").equals("") ? registro.get(0).get("fechaHoy") : registro.get(0).get("fechaIngreso"));
			objConsultaPersonasDto.setFechaHoy(registro.get(0).get("fechaHoy").substring(0, 10));
			objConsultaPersonasDto.setRfc(registro.get(0).get("rfc"));	
			objConsultaPersonasDto.setRazonSocial(psRazonSocial);
			objConsultaPersonasDto.setPaterno(psPaterno);
			objConsultaPersonasDto.setMaterno(psMaterno);
			objConsultaPersonasDto.setNombre(psNombre);			
			objConsultaPersonasDto.setNombreCorto(registro.get(0).get("nombreCorto"));

	
			//vector.tipoOperacion = "MODIFICAR"
			if (registro.get(0).get("tipoOperacion").equals("MODIFICAR")){
			
				//Se actualiza los datos en Persona
				recibeDatoEntero = objConsultaPersonasDao.actualizaDatos(objConsultaPersonasDto);
				System.out.println(recibeDatoEntero);
				if (recibeDatoEntero > 0)
					mensaje = "El registro se actualizo correctamente";
				else
					mensaje = "Ocurrio un error durante la actualizaci\u00f2n";
				System.out.println("10");
				if (registro.get(0).get("tipoPersona").equals("E"))
				{
					if (!registro.get(0).get("idCaja").equals("") && !registro.get(0).get("idCaja").equals(0)){
						recibeDatoEntero = objConsultaPersonasDao.actualizaCaja(Integer.parseInt(registro.get(0).get("idCaja")), Integer.parseInt(registro.get(0).get("noEmpresa")));						
					}
					else{
						recibeDatoEntero = objConsultaPersonasDao.actualizaCaja(-1, Integer.parseInt(registro.get(0).get("noPersona")));
					}					
				}
				
				if(registro.get(0).get("tipoPersona").equals("P")){					
					recibeDatoEntero = objConsultaPersonasDao.actualizaProveedor(Integer.parseInt(registro.get(0).get("noPersona")), 
																				 Integer.parseInt(registro.get(0).get("noEmpresa")), psProveedorBasico);					
				}
				
				if (registro.get(0).get("tipoPersona").equals("E") || registro.get(0).get("tipoPersona").equals("I")){
					recibeDatoEntero = objConsultaPersonasDao.actualizaEmpresa(objConsultaPersonasDto);
				}		
				
			}//Termina la funcion del update
			
			else if (registro.get(0).get("tipoOperacion").equals("INSERTAR")){// para hacer un insert de una nueva persona	
					
				if (registro.get(0).get("tipoPersona").equals("K") || registro.get(0).get("tipoPersona").equals("P"))
					objConsultaPersonasDto.setNoEmpresa(552);
							
				if (registro.get(0).get("tipoPersona").equals("R") || registro.get(0).get("tipoPersona").equals("F")){					
//					objConsultaPersonasDto.setNoPersona(objConsultaPersonasDao.seleccionarFolioReal("no_persona"));
//					objConsultaPersonasDao.actualizarFolioReal("no_persona");
					objConsultaPersonasDto.setNoPersona(globalDao.obtenerFolioReal("no_persona"));

				}
				else{					
					objConsultaPersonasDto.setNoPersona(noPersona);
				}				

				if (!registro.get(0).get("tipoPersona").equals("E") && !registro.get(0).get("tipoPersona").equals("P") &&
					!registro.get(0).get("tipoPersona").equals("C") && !registro.get(0).get("tipoPersona").equals("I") &&
					!registro.get(0).get("tipoPersona").equals("R") && !registro.get(0).get("tipoPersona").equals("K")){
					
					objConsultaPersonasDto.setNoEmpresa(1);
//					objConsultaPersonasDto.setNoPersona(objConsultaPersonasDao.seleccionarFolioReal("no_persona"));
//					objConsultaPersonasDao.actualizarFolioReal("no_persona");
					objConsultaPersonasDto.setNoPersona(globalDao.obtenerFolioReal("no_persona"));

				}
				
				if (registro.get(0).get("tipoPersona").equals("I") || registro.get(0).get("tipoPersona").equals("E") ||
					registro.get(0).get("tipoPersona").equals("P") || registro.get(0).get("tipoPersona").equals("C") ||
					registro.get(0).get("tipoPersona").equals("R")){
//					piNoCuenta = objConsultaPersonasDao.seleccionarFolioReal("no_cuenta");
//					objConsultaPersonasDao.actualizarFolioReal("no_cuenta");
//					piNoLinea = objConsultaPersonasDao.seleccionarFolioReal("no_linea");
//					objConsultaPersonasDao.actualizarFolioReal("no_linea");
					piNoCuenta = globalDao.obtenerFolioReal("no_cuenta");
					piNoLinea = globalDao.obtenerFolioReal("no_linea");
					
					objConsultaPersonasDto.setNoLinea(piNoLinea);
					objConsultaPersonasDto.setNoCuenta(piNoCuenta);
				}
				recibeDatoEntero = objConsultaPersonasDao.existePersona(objConsultaPersonasDto.getNoPersona(), objConsultaPersonasDto.getNoEmpresa(), registro.get(0).get("tipoPersona"));
				
				if (recibeDatoEntero > 0){
					mensaje = "El n\u00famero de persona ya existe";
				}
				else{				
					if (registro.get(0).get("tipoPersona").equals("E"))
						/////////////Revisar noPersona
						objConsultaPersonasDto.setNoEmpresa(noPersona);
					
					recibeDatoEntero = objConsultaPersonasDao.insertaDatos(objConsultaPersonasDto, piNoCuenta, piNoLinea, noEmpresaC);				
					if (recibeDatoEntero > 0)
						mensaje = "El registro se inserto correctamente";
					else
						mensaje = "Ocurrio un error al almacenar el registro";
					
					recibeCadena = objConsultaPersonasDao.configuraSet(268);
					
					if (recibeCadena.equals("SI"))
						objConsultaPersonasDao.actualizaInstFinanciera(noPersona, "NULL");
					
					
					if (registro.get(0).get("tipoPersona").equals("I") || registro.get(0).get("tipoPersona").equals("E")){
						noEmpresa = noPersona;
						GlobalSingleton globalSingleton = GlobalSingleton.getInstancia();
						System.out.println("<----------------------------->");
						System.out.println(globalSingleton.getUsuarioLoginDto().getIdUsuario());
						
						objConsultaPersonasDto.setNoEmpresa(noEmpresa);
						recibeDatoEntero = objConsultaPersonasDao.insertaEmpresa(objConsultaPersonasDto);
						
						recibeDatoEntero = objConsultaPersonasDao.insertaLinea(noEmpresa, piNoLinea, registro.get(0).
								get("fechaHoy"),globalSingleton.getUsuarioLoginDto().getIdUsuario() );
						
						recibeDatoEntero = objConsultaPersonasDao.insertaCuenta(noEmpresa, piNoLinea, noPersona, piNoCuenta,
																			   registro.get(0).get("fechaHoy"),globalSingleton.getUsuarioLoginDto().getIdUsuario() );
												
						recibeDatoEntero = objConsultaPersonasDao.insertaSaldos(noEmpresa, piNoLinea, piNoCuenta, 90);
						recibeDatoEntero = objConsultaPersonasDao.insertaSaldos(noEmpresa, piNoLinea, piNoCuenta, 91);
						recibeDatoEntero = objConsultaPersonasDao.insertaSaldos(noEmpresa, piNoLinea, piNoCuenta, 8);
						recibeDatoEntero = objConsultaPersonasDao.insertaSaldos(noEmpresa, piNoLinea, piNoCuenta, 9);						
					}
					else if (registro.get(0).get("tipoPersona").equals("P")){
						noEmpresa = Integer.parseInt(registro.get(0).get("noEmpresa"));
						recibeDatoEntero = objConsultaPersonasDao.obtieneCuentaEmpresa(noEmpresa);
						if (recibeDatoEntero > 0)
							noCuentaEmp = recibeDatoEntero;
						else
							noCuentaEmp = 0;
						
						objConsultaPersonasDao.insertaProveedor(noEmpresa, noPersona, Integer.parseInt(registro.get(0).get("usuarioModif")),
																registro.get(0).get("fechaHoy"), noCuentaEmp, false, "B");						
					}
					
					if (registro.get(0).get("tipoPersona").equals("I") || registro.get(0).get("tipoPersona").equals("E") 
						|| registro.get(0).get("tipoPersona").equals("P") || registro.get(0).get("tipoPersona").equals("C")){
						mensaje = "Datos registrados con n\u00famero de persona "  + " y N\u00famero de Empresa " + noEmpresa;
						
						
					}
					else{
						mensaje = "Datos registrados con n\u00famero de persona " + registro.get(0).get("noPersona");
					}					
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: guardarIB");
		}return mensaje;
	}
	
	
	public String aceptar (List<Map<String, String>> registro){
		System.out.println(registro.get(0).get("concentradora"));
		String mensaje = "";
//		String sexo = "";
		String psProveedorBasico = "";
//		String psProveedor = "";
//		String psAsociacion = "";
//		String psContratoInversion = "";
		String psConcentradora = "";
//		String psExporta = "";
//		String psDivision = "";
//		String psPagosCruzados = "";
//		int piContratoTEF = 0;
//		int piContratoPayment = 0;		
		int recibeDatoEntero = 0;
		int piNoCuenta = 0;
		int piNoLinea = 0;
		int noPersona = 0;
		String psRazonSocial = "";
		String psNombre = "";
		String psPaterno = "";
		String psMaterno = "";
		String recibeCadena = "";
		int noEmpresa = 0;
		int noCuentaEmp = 0;
		int noEmpresaC = 0;
		
		try{
			System.out.println("Insertar Empresa");
			noPersona = registro.get(0).get("noEmpresa").equals("") ? 0 : Integer.parseInt(registro.get(0).get("noEmpresa"));
			//Valida sexo							
//			sexo = registro.get(0).get("sexo");		
			//Si el tipo de persona es proveedor
			if (registro.get(0).get("tipoPersona").equals("P")){				
				if (registro.get(0).get("proveedorServiciosBasicos").equals("true")){					
					psProveedorBasico = "S";
				}
				else{					
					psProveedorBasico = "N";
				}
			}
//				psProveedor = "N";
//				psAsociacion = "N";
//				psContratoInversion = "N";
						
			//RadioButton de empresa concentradora			
			psConcentradora = registro.get(0).get("concentradora");
						
			//RadioButton de exportacion
//			psExporta = registro.get(0).get("exporta");
//			if (registro.get(0).get("tipoPersona").equals("E")){
////					psDivision = "N";
//					psPagosCruzados = "N";			
//			}			
			
			psRazonSocial = registro.get(0).get("razonSocial");
			
			psNombre =  "NULL";
			psPaterno =  "NULL";
			psMaterno = "NULL";

			//Para actualizar o insertar la informacion
			noEmpresaC = registro.get(0).get("noEmpresa").equals("") ? 0 : Integer.parseInt(registro.get(0).get("noEmpresa"));
			
			objConsultaPersonasDto.setNoEmpresa(registro.get(0).get("noEmpresa").equals("") ? 0 : Integer.parseInt(registro.get(0).get("noEmpresa")));
			objConsultaPersonasDto.setTipoPersona(registro.get(0).get("tipoPersona"));
			objConsultaPersonasDto.setFechaIngreso(registro.get(0).get("fechaIngreso").equals("") ? registro.get(0).get("fechaHoy") : registro.get(0).get("fechaIngreso"));
			objConsultaPersonasDto.setFechaHoy(registro.get(0).get("fechaHoy").substring(0, 10));
			objConsultaPersonasDto.setRfc(registro.get(0).get("rfc"));	
			objConsultaPersonasDto.setRazonSocial(psRazonSocial);
			objConsultaPersonasDto.setPaterno(psPaterno);
			objConsultaPersonasDto.setMaterno(psMaterno);
			objConsultaPersonasDto.setNombre(psNombre);			
			objConsultaPersonasDto.setNombreCorto(registro.get(0).get("nombreCorto"));
			objConsultaPersonasDto.setIdCaja(registro.get(0).get("idCaja").equals("") ? 0 : Integer.parseInt(registro.get(0).get("idCaja")));
			objConsultaPersonasDto.setConcentradora(psConcentradora);

	
			//vector.tipoOperacion = "MODIFICAR"
			if (registro.get(0).get("tipoOperacion").equals("MODIFICAR")){
			
				//Se actualiza los datos en Persona
				recibeDatoEntero = objConsultaPersonasDao.actualizaDatos(objConsultaPersonasDto);
			
				if (recibeDatoEntero > 0)
					mensaje = "El registro se actualizo correctamente";
				else
					mensaje = "Ocurrio un error durante la actualizaci\u00f2n";
				System.out.println("10");
				if (registro.get(0).get("tipoPersona").equals("E"))
				{
					if (!registro.get(0).get("idCaja").equals("") && !registro.get(0).get("idCaja").equals(0)){
						recibeDatoEntero = objConsultaPersonasDao.actualizaCaja(Integer.parseInt(registro.get(0).get("idCaja")), Integer.parseInt(registro.get(0).get("noEmpresa")));						
					}
					else{
						recibeDatoEntero = objConsultaPersonasDao.actualizaCaja(-1, Integer.parseInt(registro.get(0).get("noPersona")));
					}					
				}
				
				if(registro.get(0).get("tipoPersona").equals("P")){					
					recibeDatoEntero = objConsultaPersonasDao.actualizaProveedor(Integer.parseInt(registro.get(0).get("noPersona")), 
																				 Integer.parseInt(registro.get(0).get("noEmpresa")), psProveedorBasico);					
				}
				
				if (registro.get(0).get("tipoPersona").equals("E") || registro.get(0).get("tipoPersona").equals("I")){
					recibeDatoEntero = objConsultaPersonasDao.actualizaEmpresa(objConsultaPersonasDto);
				}		
				
			}//Termina la funcion del update
			//inicia el proceso de insercion
			
			else if (registro.get(0).get("tipoOperacion").equals("INSERTAR")){// para hacer un insert de una nueva persona	
					
				if (registro.get(0).get("tipoPersona").equals("K") || registro.get(0).get("tipoPersona").equals("P"))
					objConsultaPersonasDto.setNoEmpresa(552);
				else
					objConsultaPersonasDto.setNoEmpresa(Integer.parseInt(registro.get(0).get("noEmpresa")));
							
				if (registro.get(0).get("tipoPersona").equals("R") || registro.get(0).get("tipoPersona").equals("F")){					
//					objConsultaPersonasDto.setNoPersona(objConsultaPersonasDao.seleccionarFolioReal("no_persona"));
//					objConsultaPersonasDao.actualizarFolioReal("no_persona");
					objConsultaPersonasDto.setNoPersona(globalDao.obtenerFolioReal("no_persona"));
				}
				else{					
					objConsultaPersonasDto.setNoPersona(noPersona);
				}				

				if (!registro.get(0).get("tipoPersona").equals("E") && !registro.get(0).get("tipoPersona").equals("P") &&
					!registro.get(0).get("tipoPersona").equals("C") && !registro.get(0).get("tipoPersona").equals("I") &&
					!registro.get(0).get("tipoPersona").equals("R") && !registro.get(0).get("tipoPersona").equals("K")){
					
					objConsultaPersonasDto.setNoEmpresa(1);
//					objConsultaPersonasDto.setNoPersona(objConsultaPersonasDao.seleccionarFolioReal("no_persona"));
//					objConsultaPersonasDao.actualizarFolioReal("no_persona");
					objConsultaPersonasDto.setNoPersona(globalDao.obtenerFolioReal("no_persona"));
				}
				
				if (registro.get(0).get("tipoPersona").equals("I") || registro.get(0).get("tipoPersona").equals("E") ||
					registro.get(0).get("tipoPersona").equals("P") || registro.get(0).get("tipoPersona").equals("C") ||
					registro.get(0).get("tipoPersona").equals("R")){
					//se deben invertir las lineas, primero se seleccionabaFolioReal y despues se actualizabaFolioReal
					
//					piNoCuenta = objConsultaPersonasDao.seleccionarFolioReal("no_cuenta");
//					objConsultaPersonasDao.actualizarFolioReal("no_cuenta");
					piNoCuenta=globalDao.obtenerFolioReal("no_cuenta");
					
				
//					piNoLinea = objConsultaPersonasDao.seleccionarFolioReal("no_linea");
//					objConsultaPersonasDao.actualizarFolioReal("no_linea");
					piNoLinea=globalDao.obtenerFolioReal("no_linea");
					
					objConsultaPersonasDto.setNoLinea(piNoLinea);
					objConsultaPersonasDto.setNoCuenta(piNoCuenta);
					
				}
				System.out.println("+++++++++++");
				recibeDatoEntero = objConsultaPersonasDao.existePersona(objConsultaPersonasDto.getNoPersona(), objConsultaPersonasDto.getNoEmpresa(), registro.get(0).get("tipoPersona"));
				System.out.println("+++++++++++");
				if (recibeDatoEntero > 0){
					mensaje = "El n\u00famero de persona ya existe";
				}
				else{				
					if (registro.get(0).get("tipoPersona").equals("E"))
						/////////////Revisar noPersona
						objConsultaPersonasDto.setNoEmpresa(noPersona);
					
					recibeDatoEntero = objConsultaPersonasDao.insertaDatos(objConsultaPersonasDto, piNoCuenta, piNoLinea, noEmpresaC);
					System.out.println("++++++++++++++++++++++");
					System.out.println(recibeDatoEntero);
					System.out.println("++++++++++++++++++++++");
					if (recibeDatoEntero > 0)
						mensaje = "El registro se inserto correctamente";
					else
						return "Ocurrio un error al almacenar el registro";
					
					recibeCadena = objConsultaPersonasDao.configuraSet(268);
					
					if (recibeCadena.equals("SI"))
						objConsultaPersonasDao.actualizaInstFinanciera(noPersona, "NULL");
					
					if (!registro.get(0).get("noEmpresa").equals(""))
						objConsultaPersonasDao.actualizaInstFinanciera(noPersona, registro.get(0).get("noEmpresa"));
					
					if (registro.get(0).get("tipoPersona").equals("I") || registro.get(0).get("tipoPersona").equals("E")){
						noEmpresa = noPersona;
						GlobalSingleton globalSingleton = GlobalSingleton.getInstancia();
						System.out.println("<----------------------------->");
						System.out.println(globalSingleton.getUsuarioLoginDto().getIdUsuario());
						
						
						//manda datos para insertar en empresa
						objConsultaPersonasDto.setNoEmpresa(noEmpresa);
						
						recibeDatoEntero = objConsultaPersonasDao.insertaEmpresa(objConsultaPersonasDto);
						
						
							
						
						
						System.out.println("ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½");
						System.out.println(recibeDatoEntero);
						System.out.println("ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½ï¿½");
						if(recibeDatoEntero > 0){
//						recibeDatoEntero = objConsultaPersonasDao.insertaLinea(noEmpresa, piNoLinea, registro.get(0).
//								get("fechaHoy"),globalSingleton.getUsuarioLoginDto().getIdUsuario() );
						}else{
							objConsultaPersonasDao.eliminaPersona(objConsultaPersonasDto);
							return "Ocurrio un error al almacenar el registro";
						}
						
						
						if(recibeDatoEntero > 0){
						recibeDatoEntero = objConsultaPersonasDao.insertaCuenta(noEmpresa, piNoLinea, noPersona, piNoCuenta,
																			   registro.get(0).get("fechaHoy"),globalSingleton.getUsuarioLoginDto().getIdUsuario() );
						}else{
							objConsultaPersonasDao.eliminaPersona(objConsultaPersonasDto);
							objConsultaPersonasDao.eliminaEmpresa(objConsultaPersonasDto);
							
							return "Ocurrio un error al almacenar el registro";
						}				
						
						if(recibeDatoEntero > 0){
							mensaje = "Exito al insertar";
						}else{
							objConsultaPersonasDao.eliminaPersona(objConsultaPersonasDto);
							objConsultaPersonasDao.eliminaEmpresa(objConsultaPersonasDto);
							objConsultaPersonasDao.eliminaLinea(objConsultaPersonasDto);
							return "Ocurrio un error al almacenar el registro";
						}
						
						recibeDatoEntero = objConsultaPersonasDao.insertaSaldos(noEmpresa, piNoLinea, piNoCuenta, 90);
						recibeDatoEntero = objConsultaPersonasDao.insertaSaldos(noEmpresa, piNoLinea, piNoCuenta, 91);
						recibeDatoEntero = objConsultaPersonasDao.insertaSaldos(noEmpresa, piNoLinea, piNoCuenta, 8);
						recibeDatoEntero = objConsultaPersonasDao.insertaSaldos(noEmpresa, piNoLinea, piNoCuenta, 9);						
					}
					else if (registro.get(0).get("tipoPersona").equals("P")){
						noEmpresa = Integer.parseInt(registro.get(0).get("noEmpresa"));
						recibeDatoEntero = objConsultaPersonasDao.obtieneCuentaEmpresa(noEmpresa);
						if (recibeDatoEntero > 0)
							noCuentaEmp = recibeDatoEntero;
						else
							noCuentaEmp = 0;
						
						objConsultaPersonasDao.insertaProveedor(noEmpresa, noPersona, Integer.parseInt(registro.get(0).get("usuarioModif")),
																registro.get(0).get("fechaHoy"), noCuentaEmp, false, "B");						
					}
					
					if (registro.get(0).get("tipoPersona").equals("I") || registro.get(0).get("tipoPersona").equals("E") 
						|| registro.get(0).get("tipoPersona").equals("P") || registro.get(0).get("tipoPersona").equals("C")){
						mensaje = "Datos registrados con n\u00famero de persona "  + " y N\u00famero de Empresa " + noEmpresa;
						
						
					}
					else{
						mensaje = "Datos registrados con n\u00famero de persona " + registro.get(0).get("noPersona");
					}					
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: aceptar");
		}return mensaje;
	}
	
	public String verificaEmpresa (int noEmpresa){
		try {
			return objConsultaPersonasDao.verificaEmpresa(noEmpresa);		
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: verificaEmpresa");
		}
		return null;
	}
	
	public int obtieneFolio (String tipoFolio){
		try {
//			return objConsultaPersonasDao.seleccionarFolioReal(tipoFolio);	
			return globalDao.seleccionarFolio(tipoFolio);	
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: obtieneFolio");
		}
		return 0;
	}
	public int actualizaFolio(String tipoFolio){		
		try {
			return objConsultaPersonasDao.actualizarFolioReal(tipoFolio);		
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: actualizaFolio");
		}
		return 0;
	}
	
	public String cambiaTipoPersona(int noPersona){
		String mensaje = "";
		int recibeDatoEntero = 0;
		try{
			recibeDatoEntero = objConsultaPersonasDao.cambiaTipoPersona(noPersona);
			
			if (recibeDatoEntero > 0)
				mensaje = "Se cambio el tipo de persona con exito";
			else
				mensaje = "Ocurrio un error durante el cambio de persona";
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: cambiaTipoPersona");
		}return mensaje;
	}
	
	public List<ConsultaPersonasDto> comboBenef (int noEmpresa, String noBenef, String descBenef){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.comboBenef(noEmpresa, noBenef, descBenef);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: comboBenef");
		}
		return result;
	}
	
	public List<ConsultaPersonasDto> verificaRegistro(int noPersona, String tipoPersona){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.verificaRegistro(noPersona, tipoPersona);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: verificaRegistro");
		}
		return result;
	}
	
	public String inhabilitaPersona (int noPersona, String tipoPersona){
		String mensaje = "";
		int recibeDatoEntero = 0;
		try{
			recibeDatoEntero = objConsultaPersonasDao.inhabilitaPersona(noPersona, tipoPersona);
			
			if (recibeDatoEntero > 0)
				mensaje = "Se inhabilito la persona con exito";
			else
				mensaje = "Ocurrio un error durante el proceso";
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Persona, C: ConsultaPersonasBusinessImpl, M: inhabilitaPersona");
		}return mensaje;
	}
	
	//*************************************************** MANTENIMIENTO CUENTAS PROVEEDOR *********************************************************
	public List<ConsultaPersonasDto> consultaCuentasProveedor(int noPersona, int noEmpresa, String tipoPersona){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.consultaCuentasProveedor(noPersona, noEmpresa, tipoPersona);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: consultaCuentasProveedor");
		}
		return result;
	}
	
	public List<ConsultaPersonasDto> comboDivisasCuentas(){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.comboDivisasCuentas();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: comboDivisasCuentas");
		}
		return result;
	}
	
	public List<ConsultaPersonasDto> comboBancoCuentas(String nacionalidad){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.comboBancoCuentas(nacionalidad);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: comboBancoCuentas");
		}
		return result;
	}
	
	public String buscaDescripcionBanco(int noBanco){
		try {
			return objConsultaPersonasDao.buscaDescripcionBanco(noBanco);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: comboBancoCuentas");
		}
		return null;
	}
	
	public List<ConsultaPersonasDto> validaMovimientos(int noPersona, String chequera){
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.validaMovimientos(noPersona, chequera);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: validaMovimientos");
		}
		return result;
	}
	
	public List<ConsultaPersonasDto> validaTransferencias(List<Map<String, String>> registro){	
		try{
			objConsultaPersonasDto.setNoPersona(Integer.parseInt(registro.get(0).get("noPersonas")));
			objConsultaPersonasDto.setIdBanco(Integer.parseInt(registro.get(0).get("idBanco")));
			objConsultaPersonasDto.setChequera(registro.get(0).get("chequera"));
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: validaTranferencias");
		}return objConsultaPersonasDao.validaTransferencias(objConsultaPersonasDto);
	}
	
	public String eliminaCuentaProveedor(List<Map<String, String>> registro){
		String mensaje = "";
		try{
			objConsultaPersonasDto.setNoEmpresa(Integer.parseInt(registro.get(0).get("noEmpresa")));
			objConsultaPersonasDto.setNoPersona(Integer.parseInt(registro.get(0).get("noPersona")));
			objConsultaPersonasDto.setIdBanco(Integer.parseInt(registro.get(0).get("idBanco")));
			objConsultaPersonasDto.setChequera(registro.get(0).get("chequera"));
			objConsultaPersonasDto.setTipoPersona(registro.get(0).get("tipoPersona"));
			
			recibeResultadoEntero = objConsultaPersonasDao.eliminaCuentaProveedor(objConsultaPersonasDto);
			
			if (recibeResultadoEntero > 0)
				mensaje = "Registro eliminado con exito";
			else
				mensaje = "Ocurrion un problema durante la eliminaci\u00f2n";
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: eliminaCuentaProveedor");
		}
		return mensaje;
	}
	
	public String eliminaMedioContacto(String no_persona,String mail){
		String mensaje = "";
		try{
			
			recibeResultadoEntero = objConsultaPersonasDao.eliminaCuentaProveedor(no_persona,mail);
			
			if (recibeResultadoEntero > 0)
				mensaje = "Registro eliminado con exito";
			else
				mensaje = "0";
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: eliminaCuentaProveedor");
		}
		return mensaje;
	}
	
	public String validaDatosCuentas(List<Map<String, String>> registro){
		String mensaje = "";
		try{			
			if (registro.get(0).get("idBanco").equals(""))
				mensaje = "Debe de seleccionar un banco";
			
			if (registro.get(0).get("divisa").equals(""))
				mensaje = "Debe de seleccionar una divisa";
			
			if (registro.get(0).get("chequera").equals(""))
				mensaje = "Debe capturar una chequera";
			
			if (!registro.get(0).get("bankTrue").equals("") && registro.get(0).get("swiftInter").equals("") && registro.get(0).get("abaInter").equals(""))
				mensaje = "Debe de escribir un ABA/SWIFT del Banco Intermediario";
			
			if (!registro.get(0).get("bankCorresponding").equals("") && registro.get(0).get("swiftCorresp").equals("") && registro.get(0).get("abaCorresp").equals(""))
				mensaje = "Debe escribir un ABA/SWIFT del Banco Corresponsal";
			
			if (!registro.get(0).get("idBanco").equals("")){
				if (Integer.parseInt(registro.get(0).get("idBanco")) < 1000){					
					if (registro.get(0).get("idBanco").equals("12")){ //validacion para cuentas Bancomer
						if (registro.get(0).get("chequera").substring(0, 3).equals("91")) {
							if (registro.get(0).get("chequera").length() != 13)
								mensaje = "Las Chequeras de Bancomer con prefijo 91 deben ser de 13 caracteres";
						}
						else{ //Si el prefijo no es 91
							if (registro.get(0).get("chequera").length() != 11) {
								if (registro.get(0).get("chequera").length() == 13)
									mensaje = "Las chequeras de Bancomer de 13 caracteres deben de iniciar con prefijo 91";
								else
									mensaje = "Las chequeras de Bancomer deben de ser de 11 caracteres";
							}
						}
					}	
					else{						
						if (registro.get(0).get("chequera").length() != 11 && registro.get(0).get("chequera").length() != 16)
							mensaje = "Las chequeras nacionales deben de ser de 11 caracteres \u00f2 16 para Tarjetas de Credito";
						
						if (registro.get(0).get("clabe").length() != 18 && registro.get(0).get("clabe").length() != 0)
							mensaje = "La CLABE debe ser de 18 caracteres";
						else if(registro.get(0).get("clabe").length() == 18 && registro.get(0).get("chequera").length() != 16)
						{
							if (!funciones.eliminarCerosAlaIzquierda(registro.get(0).get("clabe").substring(0, 3)).equals(registro.get(0).get("idBanco")))
								mensaje = "El Banco de la CLABE no corresponde con el banco seleccionado";
							
							System.out.println("banco :" + registro.get(0).get("clabe").substring(1, 3));
							System.out.println("banco:" + registro.get(0).get("idBanco"));
							System.out.println("banco:" + registro.get(0).get("clabe").substring(0, 3));
							
							
							if (!registro.get(0).get("clabe").substring(6, 17).equals(registro.get(0).get("chequera")))
								mensaje = "La cuenta de la CLABE no corresponde con la cuenta capturada";
							
						}
						
					}
				}
			}		
			System.out.println("llega 2");
			if (registro.get(0).get("nacionalidad").equals("N"))
			{
				if (registro.get(0).get("plaza").equals(""))
					mensaje = "Falta el dato de la Plaza";
				
				if (registro.get(0).get("sucursal").equals(""))
					mensaje = "Falta caputurar la sucursal";
				
				if (registro.get(0).get("clabe").equals(""))
					mensaje = "Falta caturar la CLABE";
			}		
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: validaDatosCuentas");
		}return mensaje;
	}
	
	public String insertaCuentaHistorico(List<Map<String, String>> registro){
		String mensaje = "";
		int noFolio = 0;
		try{
//			recibeResultadoEntero = objConsultaPersonasDao.seleccionarFolioReal("cambios_ctas_banco");
//			if (recibeResultadoEntero > 0){
//				noFolio = recibeResultadoEntero;
//				objConsultaPersonasDao.actualizarFolioReal("cambios_ctas_banco");
//			}
			noFolio = globalDao.obtenerFolioReal("cambios_ctas_banco");
			
			objConsultaPersonasDto.setFolioReal(noFolio);
			objConsultaPersonasDto.setNoEmpresa(registro.get(0).get("noEmpresa").equals("") ? 0 : Integer.parseInt(registro.get(0).get("noEmpresa")));
			objConsultaPersonasDto.setNoPersona(registro.get(0).get("noPersona").equals("") ? 0 : Integer.parseInt(registro.get(0).get("noPersona")));
			objConsultaPersonasDto.setIdBanco(registro.get(0).get("idBanco").equals("") ? 0 : Integer.parseInt(registro.get(0).get("idBanco")));			
			objConsultaPersonasDto.setDivisa(registro.get(0).get("divisa"));
			objConsultaPersonasDto.setChequera(registro.get(0).get("chequera"));
			objConsultaPersonasDto.setDescChequera(registro.get(0).get("descChequera"));
			objConsultaPersonasDto.setSucursal(registro.get(0).get("sucursal").equals("") ? 0 : Integer.parseInt(registro.get(0).get("sucursal")));
			objConsultaPersonasDto.setPlaza(registro.get(0).get("plaza").equals("") ? 0 : Integer.parseInt(registro.get(0).get("plaza")));
			objConsultaPersonasDto.setFechaHoy(registro.get(0).get("fecHoy"));
			objConsultaPersonasDto.setUsuarioAlta(Integer.parseInt(registro.get(0).get("usuarioAlta")));
			//objConsultaPersonasDto.setHoraAlta();
			objConsultaPersonasDto.setSwift(registro.get(0).get("swift"));
			objConsultaPersonasDto.setAba(registro.get(0).get("aba"));
			objConsultaPersonasDto.setTipoPersona(registro.get(0).get("tipoPersona"));
			objConsultaPersonasDto.setClabe(registro.get(0).get("clabe"));
			objConsultaPersonasDto.setSwiftInter(registro.get(0).get("swiftInter"));
			objConsultaPersonasDto.setAbaInter(registro.get(0).get("abaInter"));
			objConsultaPersonasDto.setSwiftCorresp(registro.get(0).get("swiftCorresp"));
			objConsultaPersonasDto.setAbaCorresp(registro.get(0).get("abaCorresp"));
			objConsultaPersonasDto.setConcepto("BAJA");
			
			recibeResultadoEntero = objConsultaPersonasDao.insertaCuentaHistorico(objConsultaPersonasDto);
			
			if (recibeResultadoEntero > 0)
				mensaje = "Se inserto el registro con exito";
			else
				mensaje = "Ocurrio un error durante el proceso";
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: insertaCuentaHistorico");
		}return mensaje;
	}
	
	
	public String insertaActualizaCuentasProveedor(List<Map<String, String>> registro){
		String mensaje = "";
		Map<String, String> mapaActualiza = new HashMap<String, String>();	
		try{
			System.out.println("llega al business de insertaActualiza");		
			objConsultaPersonasDto.setNoEmpresa(registro.get(0).get("noEmpresa").equals("") ? 0 : Integer.parseInt(registro.get(0).get("noEmpresa")));
			objConsultaPersonasDto.setNoPersona(registro.get(0).get("noPersona").equals("") ? 0 : Integer.parseInt(registro.get(0).get("noPersona")));
			objConsultaPersonasDto.setIdBanco(registro.get(0).get("idBanco").equals("") ? 0 : Integer.parseInt(registro.get(0).get("idBanco")));			
			objConsultaPersonasDto.setDivisa(registro.get(0).get("divisa"));
			System.out.println("llego aqui 0");
			objConsultaPersonasDto.setChequera(registro.get(0).get("chequera"));
			objConsultaPersonasDto.setDescChequera(registro.get(0).get("descChequera"));
			objConsultaPersonasDto.setSucursal(registro.get(0).get("sucursal").equals("") ? 0 : Integer.parseInt(registro.get(0).get("sucursal")));
			objConsultaPersonasDto.setPlaza(registro.get(0).get("plaza").equals("") ? 0 : Integer.parseInt(registro.get(0).get("plaza")));
			objConsultaPersonasDto.setFechaHoy(registro.get(0).get("fecHoy").substring(0, 10));
			objConsultaPersonasDto.setUsuarioAlta(Integer.parseInt(registro.get(0).get("usuarioAlta")));
			//objConsultaPersonasDto.setHoraAlta();
			System.out.println("llego aqui insert1");
			objConsultaPersonasDto.setSwift(registro.get(0).get("swift"));
			objConsultaPersonasDto.setAba(registro.get(0).get("aba"));
			objConsultaPersonasDto.setTipoPersona(registro.get(0).get("tipoPersona"));
			objConsultaPersonasDto.setClabe(registro.get(0).get("clabe"));
			objConsultaPersonasDto.setSwiftInter(registro.get(0).get("swiftInter"));
			objConsultaPersonasDto.setAbaInter(registro.get(0).get("abaInter"));
			objConsultaPersonasDto.setSwiftCorresp(registro.get(0).get("swiftCorresp"));
			
			objConsultaPersonasDto.setAbaCorresp(registro.get(0).get("abaCorresp"));
			objConsultaPersonasDto.setChequeraTrue(registro.get(0).get("chequeraTrue"));
			objConsultaPersonasDto.setBankCorresponding(registro.get(0).get("bankCorresponding").equals("") ? 0 : Integer.parseInt(registro.get(0).get("bankCorresponding")));
			objConsultaPersonasDto.setChequeraAnt(registro.get(0).get("chequeraAnt"));
			objConsultaPersonasDto.setIdBancoAnt(registro.get(0).get("idBancoAnt").equals("") ? 0 : Integer.parseInt(registro.get(0).get("idBancoAnt")));
			objConsultaPersonasDto.setChequeraBenef(registro.get(0).get("chequeraBenef"));
			objConsultaPersonasDto.setBankTrue(registro.get(0).get("bankTrue").equals("") ? 0 : Integer.parseInt(registro.get(0).get("bankTrue")));
			objConsultaPersonasDto.setChequeraTrue(registro.get(0).get("chequeraTrue"));
			objConsultaPersonasDto.setBancoAnterior(registro.get(0).get("bancoAnterior"));
			objConsultaPersonasDto.setChequeraAnterior(registro.get(0).get("chequeraAnterior"));
			objConsultaPersonasDto.setActualizaChequeraProv(registro.get(0).get("actualizaChequeraProv"));
	
			System.out.println("llego aqui insert2");
			if (registro.get(0).get("tipoOperacion").equals("INSERTAR")){
				//Se inserta la nueva chequera
				recibeResultadoEntero = objConsultaPersonasDao.insertaCuentaProveedor(objConsultaPersonasDto);
				if (recibeResultadoEntero > 0)
					mensaje = "Se almaceno el registro con exito";
				else
					mensaje = "Ocurrio un problema durante la insercion del registro";
				
				//Valida que la persona tenga dada de alta la forma de pago 3
				recibeDatos = objConsultaPersonasDao.buscaFormaPagoProv(Integer.parseInt(registro.get(0).get("noPersona")), Integer.parseInt(registro.get(0).get("noEmpresa")));
				if (recibeDatos.size() <= 0)
					recibeResultadoEntero = objConsultaPersonasDao.insertaFormaPago(Integer.parseInt(registro.get(0).get("noEmpresa")), Integer.parseInt(registro.get(0).get("noPersona")), registro.get(0).get("tipoPersona"));
					
			}
			else{
				//se actualiza la chequera
				// si esta prendida esta bandera se actualizan las chequeras del beneficiario en los movimientos pendientes de pago
				System.out.println("llego aqui insert3");
				if(registro.get(0).get("actualizaChequeraProv").equals("S")){
					mapaActualiza = objConsultaPersonasDao.actualizaChequerasProv(Integer.parseInt(registro.get(0).get("noPersona")));
					
					if (mapaActualiza.size() > 0)
						mensaje = "Ocurrio un error en la Actualizacion de las Chequeras";
				}
				System.out.println("llego al insert 4");
				recibeResultadoEntero = objConsultaPersonasDao.actualizaCuentaProveedor(objConsultaPersonasDto);
				if (recibeResultadoEntero > 0)
					mensaje = "Se actualizo la cuenta con exito";
				else
					mensaje = "Ocurrio un problema durante la actualizaci\u00f2n";
				
			}
			//Manda llamar el insert del historico
			insertaCuentaHistorico(registro);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: insertaActualizaCuentasProveedor");
		}return mensaje;
	}
	
	//*********************************************************************************************************************************************
	
	
	public List<ConsultaPersonasDto> obtenerPersonas(String condicion,boolean todos) {
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.obtenerPersonas(condicion, todos);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: obtenerPersonas");
		}
		return result;
	}
	
	public List<LlenaComboGralDto> obtenerRelaciones(LlenaComboGralDto obj) {
		List<LlenaComboGralDto> result = new ArrayList<LlenaComboGralDto>();
		try {
			result = objConsultaPersonasDao.obtenerRelaciones(obj);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: obtenerRelaciones");
		}
		return result;
	}
	
	/*public String altaRelaciones(List<Map<String, String>> registro) {
		System.out.println("2");
		String mensaje = "";
		int existeRel = 0;
		
		try {
			existeRel = objConsultaPersonasDao.existeRelacion(registro);
			if (existeRel > 0) 
				return "Ya existe una relaci\u00f2n con esta persona";
			
			existeRel = objConsultaPersonasDao.altaRelacion(registro);
			
			if(existeRel > 0)
				mensaje = "Registro almacenado";
			else
				mensaje = "Error al tratar de guadar el registro";
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: insertaCuentaHistorico");
		}return mensaje;
	}*/
	
	public String altaRelaciones (List<Map<String, String>> registro){
		String mensaje = "";
//		String sexo = "";
//		String psProveedorBasico = "";
//		String psProveedor = "";
//		String psAsociacion = "";
//		String psContratoInversion = "";
//		String psConcentradora = "";
//		String psExporta = "";
//		String psDivision = "";
//		String psPagosCruzados = "";
//		int piContratoTEF = 0;
//		int piContratoPayment = 0;		
		int recibeDatoEntero = 0;
//		int piNoCuenta = 0;
//		int piNoLinea = 0;
//		int noPersona = 0;
//		String psRazonSocial = "";
//		String psNombre = "";
//		String psPaterno = "";
//		String psMaterno = "";
//		String recibeCadena = "";
//		int noEmpresa = 0;
//		int noCuentaEmp = 0;
//		int noEmpresaC = 0;
		
		try{
			
			
			
			objConsultaPersonasDto.setNoPersona(registro.get(0).get("noPersona").equals("") ? 0 : Integer.parseInt(registro.get(0).get("noPersona")));
			//objConsultaPersonasDto.setTipoPersona(registro.get(0).get("tipoPersona"));
			objConsultaPersonasDto.setNoPersonaRel(registro.get(0).get("noPersonaRel").equals("") ? 0 : Integer.parseInt(registro.get(0).get("noPersonaRel")));	
			objConsultaPersonasDto.setTipoRelacion(registro.get(0).get("tipoRelacion").equals("") ? 0 : Integer.parseInt(registro.get(0).get("tipoRelacion")));
			objConsultaPersonasDto.setFecha(registro.get(0).get("fecha").equals("") ? registro.get(0).get("fechaHoy") : registro.get(0).get("fecha"));
			objConsultaPersonasDto.setFechaHoy(registro.get(0).get("fechaHoy").substring(0, 10));
			objConsultaPersonasDto.setCuenta(registro.get(0).get("cuenta"));
			//objConsultaPersonasDto.setCuenta(registro.get(0).get("cuenta").equals("") ? 0 : Integer.parseInt(registro.get(0).get("cuenta")));

					recibeDatoEntero = objConsultaPersonasDao.altaRelacion(objConsultaPersonasDto);				
					if (recibeDatoEntero > 0)
						mensaje = "El registro se inserto correctamente";
					else
						mensaje = "Ocurrio un error al almacenar el registro";
									
			
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: altaRelacion");
		}return mensaje;
	}
	
	public List<ConsultaPersonasDto> consultarRelaciones(int noPersona, int noEmpresa) {
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			result = objConsultaPersonasDao.consultarRelaciones(noPersona, noEmpresa);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: consultarRelaciones");
		}
		return result;
	}
	
	public String eliminarRelaciones(List<Map<String, String>> datos) {
		String mensaje = "";
		int existeRel = 0;
		int i = 0;
		
		try {
			for(i=0; i<datos.size(); i++) {
				existeRel = objConsultaPersonasDao.eliminarRelaciones(datos, i);
				if(existeRel == 0) break;
			}
			if(existeRel > 0)
				mensaje = "Registro eliminado";
			else
				mensaje = "Error al tratar de eliminar el registro " + datos.get(i).get("noPersonaRel");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: eliminarRelaciones");
		}return mensaje;
	}
	
	public String modificarReferencia(String tipoReferencia, int noPersona, String tipoPersona) {
		String mensaje = "";
		int cont = 0;
		
		try {
			cont = objConsultaPersonasDao.modificarReferencia(tipoReferencia, noPersona, tipoPersona);

			if(cont > 0)
				mensaje = "Referencia registrada con exito";
			else
				mensaje = "Error al tratar de modificar la referencia";
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: eliminarRelaciones");
		}return mensaje;
	}
	
	//******** GET y SET del DAO *****************************************************************
	public ConsultaPersonasDao getObjConsultaPersonasDao() {
		return objConsultaPersonasDao;
	}

	public void setObjConsultaPersonasDao(ConsultaPersonasDao objConsultaPersonasDao) {
		this.objConsultaPersonasDao = objConsultaPersonasDao;
	}

	@Override
	public HSSFWorkbook reportePersonas(String tipoPersona) {
		HSSFWorkbook hb=null;
		try {
			hb=Utilerias.generarExcel(new String[]{
					"Equivale Persona",
					"Nombre",
					"No. Empresa",
					"Tipo Persona",				
					"No.Persona",
					"Fisica Moral",
					"Estatus",
					"Tipo Direcciï¿½n",
					"Calle",
					"Colonia",
					"Cp",
					"Delegacion",
					"Ciudad",
					"Estado",
					"Pais"
						
			}, 
					objConsultaPersonasDao.reportePersonas(tipoPersona), 
					new String[]{
							"equivalePersona",
							"nombre",
							"noEmpresa",
							"idTipoPersona",				
							"noPersona",
							"fisicaMoral",
							"estatus",
							"idTipoD",
							"calle",
							"colonia",
							"cp",
							"delegacion",
							"ciudad",
							"estado",
							"pais"
							
					});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: reportePersonas");
		}return hb;
		
	}

	@Override
	public List<ConsultaPersonasDto> llenaGridMedios(String equivale) {
		List<ConsultaPersonasDto> result = new ArrayList<ConsultaPersonasDto>();
		try {
			System.out.println("eeeeeeeeeeeeee");
			result = objConsultaPersonasDao.llenaGridMedios(equivale);
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: llenaGridMedios");
		}
		return result;
	}

	public GlobalDao getGlobalDao() {
		return globalDao;
	}

	public void setGlobalDao(GlobalDao globalDao) {
		this.globalDao = globalDao;
	}

	@Override
	public String guardaNuevoMedioContacto(String no_persona, String mail) {
		// TODO Auto-generated method stub
		return null;
	}


	
}