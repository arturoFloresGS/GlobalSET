package com.webset.set.impresion.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis.AxisFault;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.impresion.dao.ChequesTransitoDao;
import com.webset.set.impresion.dto.ChequesTransitoDto;
import com.webset.set.impresion.service.ChequesTransitoService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.RevividorDto;
import com.webset.utils.tools.Utilerias;

import mx.com.gruposalinas.CancelacionCompensacion.DT_CancelacionCompensacion_OBCancelaciones;
import mx.com.gruposalinas.CancelacionCompensacion.DT_CancelacionCompensacion_ResponseCancelaciones;
import mx.com.gruposalinas.CancelacionCompensacion.SOS_CancelacionCompensacionBindingStub;
import mx.com.gruposalinas.CancelacionCompensacion.SOS_CancelacionCompensacionServiceLocator;

/*****
 * 
 * @author YEC
 * 04 DE ENERO DEL 2016
 */


public class ChequesTransitoBusinessImpl implements ChequesTransitoService {
	private Bitacora bitacora = new Bitacora();
	private ChequesTransitoDao objChequesTransitoDao;
	
	public List<LlenaComboGralDto> llenarComboMotivos(){
		List<LlenaComboGralDto> combo=  new ArrayList<LlenaComboGralDto>();
		try {
			combo= objChequesTransitoDao.llenarComboMotivos();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:ChequesTransitoBusinessImpl, M:llenarComboMotivos");
		}return combo;
	}
	
	public List<ChequesTransitoDto> llenaGrid(String noEmpresa, String noBanco, String idChequera,String noCheque, String fechaIni, String fechaFin, String dias) {
		List<ChequesTransitoDto> recibeDatos = new ArrayList<ChequesTransitoDto>();
		try {
			recibeDatos = objChequesTransitoDao.llenaGrid(noEmpresa,noBanco, idChequera,noCheque, fechaIni, fechaFin, dias);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:ChequesTransito, C:ChequesTransitoBusinessImpl, M: llenaGrid");	
		}return recibeDatos;
	}	
	
	public String cancelarCheque(ChequesTransitoDto dto){
		String mensaje="";
		Funciones funciones= new Funciones();
		Map<String, Object> mapRet = new HashMap<String, Object>();
		try {
			if(objChequesTransitoDao.validarCancelar(dto.getPwd(),dto.getUsuario())){
				if(objChequesTransitoDao.insertarMotivo(dto.getObservacion(), dto.getNoFolioDetalle())!=0){
					if(!dto.getFormaPago().equals("")){
						if(dto.getFormaPago().equals("1")&& dto.getDias().equals("0")){
										
										DT_CancelacionCompensacion_OBCancelaciones cancelacionDto = new DT_CancelacionCompensacion_OBCancelaciones(
												funciones.ajustarLongitudCampo(String.valueOf(dto.getNoEmpresa()), 4, "D", "", "0"),
												dto.getPoHeders(),
												String.valueOf(dto.getEjercicio()));
								
								if (dto.getOrigenMovimiento().equals("SAP")) {
									Map<String, Object> respuestaCancelacion = descompensar(cancelacionDto);
									
									if ((Boolean)respuestaCancelacion.get("estatus")) {
										DT_CancelacionCompensacion_ResponseCancelaciones[] respuestaCancelacionArreglo =
												(DT_CancelacionCompensacion_ResponseCancelaciones[])
												respuestaCancelacion.get("respuestaWS");
										if (respuestaCancelacionArreglo != null && respuestaCancelacionArreglo.length !=0) {
											DT_CancelacionCompensacion_ResponseCancelaciones respuesta=
													respuestaCancelacionArreglo[0];
											if (respuesta != null &&
													(cancelacionDto.getNO_DOC_SAP().equals(
															respuesta.getNO_DOC_SAP()))) {
												//Verificar la condicion para comprobar si el documento se cancelo correctamente
												if ((respuesta.getDOC_POLIZA_SAP()!=null &&!respuesta.getDOC_POLIZA_SAP().equals("")) ||
														(respuesta.getMSG_ERROR().indexOf("Se descompenso el documento pero no se anul� por")!=-1)) {
													mapRet= objChequesTransitoDao.ejecutarRevividor(dto);
													
													if(Integer.parseInt(mapRet.get("result").toString())!=0){
														mensaje="Error en el proceso";
									   
									     	        }else{
									     	        	mensaje="datos registrados";
									     	        }
													
												} else {
													mensaje = respuesta.getMSG_ERROR() != null && 
															!respuesta.getMSG_ERROR().equals("") ? 
																	respuesta.getMSG_ERROR() : 
																	"No se pudo descompensar el documento"; 
												}
											} else {
												mensaje="La respuesta no coincide con el documento enviado " ;
											}
										} else {
											mensaje=" No se obtuvo respuesta de SAP";
										}
												
									} else {
										mensaje=respuestaCancelacion.get("mensaje").toString();
									}
								} else {
									mapRet= objChequesTransitoDao.ejecutarRevividor(dto);
									if(Integer.parseInt(mapRet.get("result").toString())!=0){
										mensaje="Error en el proceso";
					   
					     	        }else{
					     	        	mensaje="datos registrados";
					     	        }
								}
								
						}
						if(dto.getFormaPago().equals("1")&& !dto.getDias().equals("0")){
							mapRet=objChequesTransitoDao.llenarParametro(dto);
							if(!mapRet.get("respuesta").equals(0)){
								String parametro=mapRet.get("folioParametro")+"";
								mapRet.clear();
								mapRet=objChequesTransitoDao.ejecutarGenerador(dto.getNoEmpresa(),parametro);
								
								if(Integer.parseInt(mapRet.get("result").toString())!=0){
									mensaje="error generador";
				   
				     	        }else{
				     	        	mensaje="datos registrados";
				     	        }
							}else{
								mensaje="Error al insertar en parametro";
							}
							
						}
						if(!dto.getFormaPago().equals("1"))
							mensaje=objChequesTransitoDao.conciliarChequeCajaOCertificado(dto.getNoFolioDetalle());
					}else{
						mensaje= "Error en los datos";
					}
				}else{
					mensaje= "Error al guardar el motivo";
				}
			}else{
				return "Contrase�a incorrecta";
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:ChequesTransito, C:ChequesTransitoBusinessImpl, M: cancelarCheque");		
		}return mensaje;
	}
	
	/*********Exporta excel************/
	public String exportaExcel(String datos) {
		String respuesta = "";
	    Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
	    try {	    	
			HSSFWorkbook wb = Utilerias.generarExcel(new String[]{
													"No.Docto",
													"No.Cheque",
										            "No.Proveedor",
										            "Beneficiario",
										            "Descripcion",
										            "Concepto",
										            "Importe",
										            "Divisa",
										            "Fec.Cheque",
										            "Empresa",
										            "Banco",
										            "D�as de Antig�edad"
												}, 
												parameters, 
												new String[]{
														"noDocumento",
														"noCheque",
														"noProveedor",
														"beneficiario",
														"descripcion",
														"concepto",
														"importe",
														"divisa",
														"fechaCheque",
														"nomEmpresa",
														"descBanco",
														"dias"
												});			
            
            respuesta = ConstantesSet.RUTA_EXCEL + "propuestas " + Utilerias.indicadorFecha() +".xls";
            File outputFile = new File(respuesta);
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
        } catch (IOException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:impresion.mantenimientos, C:ChequesTransitoBusinessImpl, M:exportaExcel");
        	respuesta = "";
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:impresion.mantenimientos, C:ChequesTransitoBusinessImpl, M:exportaExcel");
            respuesta = "";
        }
        return respuesta;
	}
	
	private Map<String, Object> descompensar(
			DT_CancelacionCompensacion_OBCancelaciones cancelacionDto) {
		Map<String, Object> resultado = new HashMap<String, Object>();
		resultado.put("estatus", false);
		resultado.put("mensaje", "Error al procesar el registro");
		try {
			SOS_CancelacionCompensacionServiceLocator service=
					new SOS_CancelacionCompensacionServiceLocator();
			SOS_CancelacionCompensacionBindingStub stub=
					new SOS_CancelacionCompensacionBindingStub(
							new URL(service.getHTTP_PortAddress()), service);
			stub.setUsername(
					objChequesTransitoDao.configuraSet(
							ConstantesSet.USERNAME_WS_CANCELACION));
			stub.setPassword(
					objChequesTransitoDao.configuraSet(
							ConstantesSet.PASSWORD_WS_CANCELACION));
			DT_CancelacionCompensacion_ResponseCancelaciones[] resp=
					stub.SOS_CancelacionCompensacion(
							new DT_CancelacionCompensacion_OBCancelaciones[]{
									cancelacionDto
							} );
			
			resultado.put("estatus", true);
			resultado.put("respuestaWS", resp);
			
		} catch ( MalformedURLException e1) {
			resultado.put("mensaje","No se pudo conectar a SAP.");
			e1.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + e1.toString() 
			+ "P:Impresion, C:ChequesTransitoBusinessImpl, M:descompensar");
		} catch (AxisFault e1) {
			resultado.put("mensaje","SAP ha tenido un problema al procesar los datos.");
			e1.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + e1.toString() 
			+ "P:Impresion, C:ChequesTransitoBusinessImpl, M:descompensar");
		} catch (Exception e) {
			resultado.put("mensaje","Error al procesar el registro");
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ChequesTransitoBusinessImpl, M:descompensar");
		} return resultado;
	}

	//*********************************************************************
			public ChequesTransitoDao getChequesTransitoDao() {
				return objChequesTransitoDao;
			}

			public void setObjChequesTransitoDao(ChequesTransitoDao objChequesTransitoDao) {
				this.objChequesTransitoDao = objChequesTransitoDao;
			}
		
}
