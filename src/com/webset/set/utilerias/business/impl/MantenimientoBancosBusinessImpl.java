package com.webset.set.utilerias.business.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dao.MantenimientoBancosDao;
import com.webset.set.utilerias.service.MantenimientoBancosService;
import com.webset.set.utileriasmod.dto.MantenimientoBancosDto;
import com.webset.utils.tools.Utilerias;

public class MantenimientoBancosBusinessImpl implements MantenimientoBancosService{
	Bitacora bitacora;
	
	Funciones funciones;
	MantenimientoBancosDao objMantenimientoBancosDao;
	MantenimientoBancosDto objMantenimientoBancosDto = new MantenimientoBancosDto();
	List<MantenimientoBancosDto> listaResultado = new ArrayList<MantenimientoBancosDto>();
	String resultadoString = "";

		
	public List<MantenimientoBancosDto> llenaComboBancos(int bancoNacional){	
		return objMantenimientoBancosDao.llenaComboBancos(bancoNacional);
	}	
	
	public List <MantenimientoBancosDto> llenaGridBancos(String descBanco, String aba, int nacionalidad){
		String psBanco = "";
		
		List<MantenimientoBancosDto> recibeDatos = new ArrayList<MantenimientoBancosDto>();
		
		try{
			if (!aba.equals("")){
				resultadoString = objMantenimientoBancosDao.buscaAba(aba);
				if (!resultadoString.equals(""))
					psBanco = recibeDatos.get(0).getDescripcion();
				
				if (psBanco.equals(""))
					return recibeDatos;
			
			}				
			else{
				psBanco = descBanco;
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + "" + e.toString() + "P:Utilerias, C:MantenimientoBancosBusinessImpl, M:llenaGridBancos");
		}return  objMantenimientoBancosDao.llenaGridBancos(descBanco, aba, nacionalidad);
	}
	
	public int obtieneIdBancoMax(){
		return objMantenimientoBancosDao.obtieneIdBancoMax();
	}
	
	public List<MantenimientoBancosDto> validaChequerasAsignadas(int idBanco){
		return objMantenimientoBancosDao.validaChequerasAsignadas(idBanco);
	}
	
	public int eliminaBanco(int idBanco){
		return objMantenimientoBancosDao.eliminaBanco(idBanco);
	}
	
	public String aceptar(List<Map<String, String>> registro){
		String resultado = "";
		int piBanco = 0;
		int bancoAMB = 0;
		String descBanco = "";
		String prefijo = "";
		String clavePlaza = "";
		String pathArchivo = "";
		String codigoSeguridad = "";
		String claveArchivo = "";
		String fechaArchivo = "";
		String consecutivo = "";
		String estatusBancaE = "";
		String liberacionAutomatica = "";
		String horaLiberacion = "";
		String layoutComerica = "";
		String validaClabe = "";
		String chequeOcurre = "";
		String tratarPorBanco = "";
		String saldoBanco = "";
		String descripcionInbursa = "";
		String bChequeElect = "";
		String chequeProtegido = "";
		String nacionalidad = "";
		String fecHoy = "";
		String plazaExtranjera = "";
		int idUsuario = 0;
		
		List<MantenimientoBancosDto> recibeDatos = new ArrayList<MantenimientoBancosDto>();
						
		try{
			//Se validan los campos para saber si vienen completos
			//Banco			
			if (registro.get(0).get("idBancoInserta").equals("")){
				if (registro.get(0).get("modificar").equals("modificar")){
					resultado = "El ID del banco no puede ser modificado";
					return resultado;
					}
				resultado = "Falta escribir el n�mero de Banco";
			}else
				piBanco = Integer.parseInt(registro.get(0).get("idBancoInserta"));
				
			//System.out.println(piBanco + "piBanco");
			//Descripcion del banco
			if (registro.get(0).get("descBancoInserta").equals("")){
				if (registro.get(0).get("modificar").equals("modificar")){
					resultado = "La descripci�n del banco no puede ser modificada";
					return resultado;
					}
				resultado = "Falta la descripcion para el Banco";
			}else{
				descBanco = registro.get(0).get("descBancoInserta");
			}
				
			
			//System.out.println(descBanco + "descBanco");
			//System.out.println(clavePlaza.trim() + "clavePlaza antes de if" );
			
			if (registro.get(0).get("chequeElectronico").equals("S")){
				bChequeElect = registro.get(0).get("chequeElectronico");
				if (registro.get(0).get("clavePlaza")!= null)
				{
					if (registro.get(0).get("clavePlaza").equals("") || registro.get(0).get("clavePlaza").equals("0"))
						resultado = "Debe de escribir una Plaza";
					else{
						clavePlaza = registro.get(0).get("clavePlaza").trim();
						if (clavePlaza.length() > 3)
							resultado = "La longitud de la Plaza debe de ser de 3 d�gitos";
						//System.out.println(clavePlaza + "clavePlaza");
					}
				}
				else
					resultado = "Debe de escribir una Plaza";
			}
			else
				bChequeElect = "N";
			
			//System.out.println(bChequeElect + "bChequeElect");
			
			if (!registro.get(0).get("idBancoAMB").equals(""))
				bancoAMB = Integer.parseInt(registro.get(0).get("idBancoAMB"));
			else
				bancoAMB = 0;
			
			//System.out.println(bancoAMB + "bancoAMB");
			
			if (!registro.get(0).get("prefijo").equals("")){
				prefijo = registro.get(0).get("prefijo");
				if (prefijo.length() > 5)
					resultado = "La longitud del Prefijo debe de ser de 5 d�gitos";
			}
			else
				prefijo = "";
			
			//System.out.println(prefijo + "prefijo");
			
			if (registro.get(0).get("chequeProtegido").equals("N")){
				chequeProtegido = "N";
				pathArchivo = "";
				codigoSeguridad = "";
				claveArchivo = "";
				fechaArchivo = "";
				consecutivo = "";
			}
			else{
				chequeProtegido = registro.get(0).get("chequeProtegido");
				if (registro.get(0).get("pathArchivo").equals("") || registro.get(0).get("pathArchivo").equals("0"))
					resultado = "Debe escribir un Path del Archivo";
				else{
					pathArchivo = registro.get(0).get("pathArchivo");
					if (pathArchivo.length() > 30 )
						resultado = "La longitud del Path del Archivo debe de ser m�ximo de 30 d�gitos";
				}
				//System.out.println(pathArchivo + "path");
				
				if (registro.get(0).get("codigoSeguridad").equals("") || registro.get(0).get("codigoSeguridad").equals("0"))
				    return resultado = "Falta escribir un Codigo de Seguridad";
				else{
					codigoSeguridad = registro.get(0).get("codigoSeguridad");
					if (codigoSeguridad.length() > 3)
						resultado = "La longitud del Codigo de Seguridad debe de ser de 3 d�gitos";
				}
				
				//System.out.println(codigoSeguridad + "codigoSeg");
				if (registro.get(0).get("claveArchivo").equals("") || registro.get(0).get("claveArchivo").equals("0"))
					resultado = "Falta escribir una Clave del Archivo";
				else{
					claveArchivo = registro.get(0).get("claveArchivo");
					if (claveArchivo.length() > 2)
						resultado = "La longitud de la Clave del Archivo debe de ser de 2 d�gitos";
				}
				//System.out.println(claveArchivo + "claveArch");
				if (registro.get(0).get("fechaArchivo").equals("") || registro.get(0).get("fechaArchivo").equals("0"))
					resultado = "Falta digital la Fecha del Archivo";
				else{
					fechaArchivo = registro.get(0).get("fechaArchivo");
					
					if (fechaArchivo.length() > 4)
						resultado = "La longitud del D�a y Mes del Archivo debe de ser de 4 d�gitos";
				}
				//System.out.println(fechaArchivo +"fechaArch");
				
				if (registro.get(0).get("consecutivo").equals("") || registro.get(0).get("consecutivo").equals("0"))
					resultado = "Falta escribir un N�mero Consecutivo";
				else{
					consecutivo = registro.get(0).get("consecutivo");
					if (consecutivo.length() > 1)
						resultado = "La longitud del Consecutivo debe de ser de 1 d�gito";
				}
				//System.out.println(consecutivo + "consecutivo");
			}
			
			horaLiberacion = registro.get(0).get("hora");
			//System.out.println(horaLiberacion + "horaLiberacion");
			//System.out.println(registro.get(0).get("bancaElectronica") + "EstatusBE antes");
			if (registro.get(0).get("bancaElectronica").equals("IMPORTACI�N") ||registro.get(0).get("bancaElectronica").equals("I"))
				estatusBancaE = "I";
			else if (registro.get(0).get("bancaElectronica").equals("EXPORTACI�N") || registro.get(0).get("bancaElectronica").equals("E"))
				estatusBancaE = "E";
			else if (registro.get(0).get("bancaElectronica").equals("AMBAS") || registro.get(0).get("bancaElectronica").equals("A"))
				estatusBancaE = "A";
			else if (registro.get(0).get("bancaElectronica").equals("NINGUNA") || registro.get(0).get("bancaElectronica").equals("N"))
				estatusBancaE = "N";
			else
				estatusBancaE = "N";
							
			
			//System.out.println(estatusBancaE + "Estatus BE");
			liberacionAutomatica = registro.get(0).get("liberacionAutomatica");
			
			if (registro.get(0).get("layoutComerica").equals("S") && registro.get(0).get("tipoBanco").equals("1"))
				layoutComerica = "S";
			else
				layoutComerica = "";
			//System.out.println(layoutComerica + "layoutComerica");	
			validaClabe = registro.get(0).get("validaClabe");
			//System.out.println(validaClabe + "validaClabe");	
			chequeOcurre = registro.get(0).get("chequeOcurre");
			//System.out.println(chequeOcurre + "chequeOcurre");
			tratarPorBanco = registro.get(0).get("tratarPorBanco");
			//System.out.println(tratarPorBanco + "tratarPorBanco");
			//System.out.println(registro.get(0).get("saldoBanco") + "saldoBanco");
			if (registro.get(0).get("saldoBanco").trim()!= null)
			{
				if (!registro.get(0).get("saldoBanco").equals("")){
					saldoBanco = registro.get(0).get("saldoBanco");								
				}
				else
					saldoBanco = "N";
			}
			else
				saldoBanco = "N";
			
			//System.out.println(saldoBanco + "saldoBanco");
			if(registro.get(0).get("descripcionInbursa").equals(""))
				descripcionInbursa = "";
			else{
				descripcionInbursa = registro.get(0).get("descripcionInbursa");
				if (descripcionInbursa.length() > 20)
					resultado = "La longitud de la Descripci�n Inbursa debe de ser m�ximo de 20 d�gitos";
			}
			//System.out.println(descripcionInbursa + "descripcionInbursa");
			fecHoy = registro.get(0).get("fecHoy");
			idUsuario = Integer.parseInt(registro.get(0).get("idUsuario"));
			
			if (registro.get(0).get("tipoBanco").equals("0"))
				nacionalidad = "N";
			else
			{
				nacionalidad = "E";
				plazaExtranjera = registro.get(0).get("plazaExtranjera");
				if (plazaExtranjera.length() > 60)
					resultado = "La longitud de la Plaza Extranjera debe de ser de 60 d�gitos";
			}
			//System.out.println(nacionalidad + "nacionalidad");
			//System.out.println(plazaExtranjera + "plazaExtranjera");
			if (!resultado.equals(""))
			{
				return resultado;
			}
			else{
				
				objMantenimientoBancosDto.setIdBanco(piBanco);
				objMantenimientoBancosDto.setDescripcion(descBanco);				
				objMantenimientoBancosDto.setIdBancoCecoban(bancoAMB);				
				objMantenimientoBancosDto.setBChequeElect(bChequeElect);				
				objMantenimientoBancosDto.setBProtegido(chequeProtegido);				
				objMantenimientoBancosDto.setPrefInstitucion(prefijo);				
				objMantenimientoBancosDto.setBancaElect(estatusBancaE);				
				objMantenimientoBancosDto.setClavePlaza(clavePlaza);				
				objMantenimientoBancosDto.setHoraLiberacion(horaLiberacion);				
				objMantenimientoBancosDto.setSaldoBanco(saldoBanco);				
				objMantenimientoBancosDto.setDescripcionInbursa(descripcionInbursa);				
				objMantenimientoBancosDto.setPathArchivo(pathArchivo);				
				objMantenimientoBancosDto.setCodigoSeguridad(codigoSeguridad);				
				objMantenimientoBancosDto.setArchivoProtegido(claveArchivo);				
				objMantenimientoBancosDto.setFechaProtegido(fechaArchivo);				
				objMantenimientoBancosDto.setNoProtegido(Integer.parseInt(consecutivo.equals("") ? consecutivo = "0" : consecutivo));				
				objMantenimientoBancosDto.setBancaElect(estatusBancaE);				
				objMantenimientoBancosDto.setLiberacionAutomatica(liberacionAutomatica);				
				objMantenimientoBancosDto.setBLayoutComerica(layoutComerica);				
				objMantenimientoBancosDto.setValidacionClabe(validaClabe);				
				objMantenimientoBancosDto.setChequeOcurre(chequeOcurre);				
				objMantenimientoBancosDto.setTransferPorBanco(tratarPorBanco);				
				objMantenimientoBancosDto.setPlazaExtranjera(plazaExtranjera);
				objMantenimientoBancosDto.setFecAlta(fecHoy);				
				objMantenimientoBancosDto.setUsuarioAlta(idUsuario);				
				objMantenimientoBancosDto.setNacionalidad(nacionalidad);
																						
				int recibeDatosEnteros = 0;
				
				if (registro.get(0).get("modificar").equals("modificar")){
					//System.out.println("antes del update");
					recibeDatosEnteros = objMantenimientoBancosDao.actualizaBanco(objMantenimientoBancosDto);
					if (recibeDatosEnteros > 0)
						resultado = "Se Actualizo correctamente el Banco";
					else
						resultado = "Ocurrio un error durante la actualizaci�n";					
				}
				else{
					//System.out.println("antes del buscar");
					recibeDatos = objMantenimientoBancosDao.buscaBancoExistente(piBanco);
					
					if (recibeDatos.size() > 0)
						resultado = "El Banco ya existe";						
					else{
						//insertamos el nuevo banco
						//System.out.println("antes del insert");
						recibeDatosEnteros = objMantenimientoBancosDao.insertaBanco(objMantenimientoBancosDto);
						
						if (recibeDatosEnteros > 0)
							resultado = "Se inserto el Banco correctamente";
						else
							resultado = "No se inserto el registro";
					}
						
				}
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoBancosBusinessImpl, M:aceptar");
		}return resultado;
	}
	
	
	
	public MantenimientoBancosDao getObjMantenimientoBancosDao() {
		return objMantenimientoBancosDao;
	}	
	public void setObjMantenimientoBancosDao(MantenimientoBancosDao objMantenimientoBancosDao) {
		this.objMantenimientoBancosDao = objMantenimientoBancosDao;
	}

	

	@Override
	public HSSFWorkbook reporteBancos(String tipoBanco) {
		HSSFWorkbook hb=null;
		try {
			hb=Utilerias.generarExcel(new String[]{
					
					         "Id Banco", 
					         "Desc Banco",
					         "Id Banco CECOBAN", 		
					         "Imprime BE", 
					         "Cheq. Protegido",
					         "Clave Plaza",
					         "Codigo Seguridad",
					         "Path De Archivo", 
					         "Clave De Archivo",
					         "Fecha De Archivo",
					         "Consecutivo",
					         "Nacionalidad",
					         "Banca Elect",
					         "Pref. Intituci�n",
					         "Liberaci�n Autom�tica",
					         "Hora Liberaci�n", 
					         "Saldo Banco",
					         "Direcci�n Extranjera",
					         "Layout Comerica",
					         "Descripci�n Inbursa",
					         "Valida Clabe",
					         "Cheque Ocurre",
					         "Transfer Por Banco",
					/////////////////
					
						
			}, 
					objMantenimientoBancosDao.reporteBancos(tipoBanco), 
					new String[]{
					
							"idBanco",
							"descripcion",
							"idBancoCecoban",
							"bChequeElect",				
							"bProtegido",
							"clavePlaza",
							"codigoSeguridad",
							"pathArchivo",
							"archivoProtegido",   
							"fechaProtegido",
							"noProtegido",
							"nacionalidad",
							"bancaElect",
							"prefInstitucion",
                    		"liberacionAutomatica",
                    		"horaLiberacion",
                    		"saldoBanco",
                    		"plazaExtranjera",
                    		"layoutComerica",
                    		"descripcionInbursa",
							"validacionClabe",
                    		"chequeOcurre",
                    		"transferPorBanco"
					});
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: reportePersonas");
		}return hb;
	}		
}
