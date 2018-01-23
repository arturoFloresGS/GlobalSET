package com.webset.set.egresos.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webset.set.egresos.dao.ConfirmacionTransferenciasDao;
import com.webset.set.egresos.dto.ConfirmacionTransferenciasDto;
import com.webset.set.egresos.service.ConfirmacionTransferenciasService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;

public class ConfirmacionTransferenciasBusinessImpl implements ConfirmacionTransferenciasService{
	private ConfirmacionTransferenciasDao confirmacionTransferenciasDao;
	Bitacora bitacora;
	Funciones funciones= new Funciones();
	ConsultasGenerales consultasGenerales;
	
	public List<LlenaComboEmpresasDto> obtenerEmpresas(int idUsuario) {
		return confirmacionTransferenciasDao.obtenerEmpresas(idUsuario);
	}
	public List<LlenaComboGralDto> llenaComboBanco(int noEmpresa) {
		return confirmacionTransferenciasDao.llenaComboBanco(noEmpresa);
	}
	public List<LlenaComboChequeraDto> llenaComboChequera(int idBanco, int noEmpresa, String idDivisa) {
		return confirmacionTransferenciasDao.llenaComboChequera(idBanco, noEmpresa, idDivisa);
	}
	public List<MovimientoDto> consultarMovimiento(int noEmpresa, int idBanco, String idChequera, int hayBanca, int idUsuario, String idDivisa) {
		return confirmacionTransferenciasDao.consultarMovimiento(noEmpresa, idBanco, idChequera, hayBanca, idUsuario, idDivisa);
	}
	
	public Map<String, Object> ejecutarConfirmacion(List<MovimientoDto> list, boolean bAutomatico, boolean chkAutomatico, int iTieneBanca) {
		Map<String, Object> mapRetornoMsg = new HashMap<String, Object>();
		Map<String, Object> respConfirmador = new HashMap<String, Object>();
		
		List<ConfirmacionTransferenciasDto> fechaAyerHoraAct = new ArrayList<ConfirmacionTransferenciasDto>();
		List<ConfirmacionTransferenciasDto> existeCargo = new ArrayList<ConfirmacionTransferenciasDto>();
		
		int horaActual = 0;
		int iSecuencia = 0;
		int piContRow = 0;
		
		boolean pasa = true;
		
		String sReferencia = "";
		String sFoliosDet = "";
		String psFolioBanco = "";
		
		try {
			if(list.size() <= 0) {
				if(!bAutomatico) {
					mapRetornoMsg.put("msgUsuario", "Debe seleccionar al menos un registro.");
					return mapRetornoMsg;
				}
			}
			if(chkAutomatico) {
				fechaAyerHoraAct = confirmacionTransferenciasDao.fechaAyerHoraActual();
				
				for(int i=0; i<fechaAyerHoraAct.size(); i++) {
					horaActual = Integer.parseInt(fechaAyerHoraAct.get(i).getHoraActual());
				}
				if(!bAutomatico) {
					if(confirmacionTransferenciasDao.validaConfirmacion() <= 0) {
						if(horaActual < 14) {
							mapRetornoMsg.put("msgUsuario", "Solo se puede confirmar automaticamente despues de las 2:00 PM.");
							return mapRetornoMsg;
						}
					}
				}
				if(confirmacionTransferenciasDao.consultarConfiguraSet(1).equals("CIE")) {
					for(int x=0; x<list.size(); x++) {
						sFoliosDet = "" + list.get(x).getNoFolioDet();
						respConfirmador = confirmacionTransferenciasDao.ejecutarConfirmador(Integer.parseInt(sFoliosDet), psFolioBanco, 
																							iSecuencia, list.get(x).getFecValor(), 0, "0");
						
						if (!respConfirmador.isEmpty() && Integer.parseInt(respConfirmador.get("result").toString())!= 0 && !bAutomatico) {
							mapRetornoMsg.put("msgUsuario", "Error, en confirmador" + respConfirmador.get("result") + ":");
							return mapRetornoMsg;
						}else
							sFoliosDet = "";
					}
					if(!bAutomatico)
						mapRetornoMsg.put("msgUsuario", "Registros Confirmados");
						return mapRetornoMsg;
				}//Termina procedimiento temporal para CIE
			}//Termina el procedimiento automatico
			
			for(int i=0; i<list.size(); i++) {
				if(chkAutomatico) {
					//Si es automatica la confirmacion, asignamos el folio_det a la referencia
					sReferencia = "" + list.get(i).getNoFolioDet();
					existeCargo = confirmacionTransferenciasDao.buscaCargoExiste(list.get(i).getFecValor(), list.get(i).getNoEmpresa(),
													list.get(i).getIdBanco(), list.get(i).getIdChequera(), list.get(i).getImporte(), "A");
					
					if(existeCargo.size() <= 0) {
						pasa = false;
						continue;
					}else {
						//Solo debe poder confirmar si hay un solo registro en banca y no esta rechazado y tambien si solo es un registro en set con ese importe, esa chequera en ese dia
						if(existeCargo.size() == 1 && (existeCargo.get(i).getIdEstatusCancela().equals("0") || existeCargo.get(i).getIdEstatusCancela() == null) && existeCargo.get(i).getRegistrosSet() == 1) {
							sFoliosDet = "" + existeCargo.get(0).getSecuencia();;
							
							if(list.get(i).getIdBanco() == 2 && existeCargo.get(0).getDescripcion().contains("AUT")) {
								psFolioBanco = existeCargo.get(0).getDescripcion().trim();
								psFolioBanco = psFolioBanco.substring(psFolioBanco.indexOf("AUT") + 4) + " ";
								psFolioBanco = psFolioBanco.substring(0, psFolioBanco.indexOf(" ") + 1).trim();
							}else {
								psFolioBanco = "";
							}
						}else {
							pasa = false;
							continue;
						}
					}
				}else {
					sReferencia = list.get(i).getReferencia();
					existeCargo = confirmacionTransferenciasDao.buscaCargoExiste(list.get(i).getFecValor(), list.get(i).getNoEmpresa(),
													list.get(i).getIdBanco(), list.get(i).getIdChequera(), list.get(i).getImporte(), "M");
					
					if(existeCargo.size() > 0) {
	                	iSecuencia = existeCargo.get(0).getSecuencia();
	                	if(existeCargo.size() != 1) sFoliosDet = "" + iSecuencia;
					}else {
			            if(/*gobjSeguridad.ValidaFacultad("TRFMAN", sMensaje) &&*/ !chkAutomatico) {
			                if(!bAutomatico) {
			                    /*If MsgBox("No se encontro el cargo correspondiente al movimiento con importe " & _
			                            mfgBancoExt.TextMatrix(i, li_c_importe) & " �Desea confirmar este movimiento?", vbExclamation + vbYesNo, _
			                            "SET") = vbNo Then
			                    
				                	pasa = false;
			                        continue;
		                        End If*/
			                }
			            }else {
			                pasa = false;
			                continue;
			            }
			        }
				}
				if(!list.get(i).getReferencia().equals("") || iTieneBanca != 0) {
					piContRow++;
					confirmacionTransferenciasDao.updateReferenciaFolBco(sReferencia, list.get(i).getNoFolioDet(),
																		 psFolioBanco, confirmacionTransferenciasDao.obtenerFechaHoy());
					
					sFoliosDet = "" + list.get(i).getNoFolioDet();
				}else {
					if(!bAutomatico) mapRetornoMsg.put("msgUsuario", "Debe ingresar una Referencia");
					return mapRetornoMsg;
				}
				respConfirmador = confirmacionTransferenciasDao.ejecutarConfirmador(Integer.parseInt(sFoliosDet), psFolioBanco, 
																					iSecuencia, list.get(i).getFecValor(), 0, "0");
				
				if (!respConfirmador.isEmpty() && Integer.parseInt(respConfirmador.get("result").toString())!= 0 && !bAutomatico) {
	    			mapRetornoMsg.put("msgUsuario", "Error, en confirmador" + respConfirmador.get("result") + ":");
	    			return mapRetornoMsg;
	    		}else
	    			sFoliosDet = "";
			}
			if(piContRow > 0)
				if(!bAutomatico) mapRetornoMsg.put("msgUsuario", "Datos Registrados");
	    	
			if(!pasa)
				if(!bAutomatico) mapRetornoMsg.put("msgUsuario", "Existen movimientos sin registrar!!");
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionTransferenciasBusiness, M:ejecutarConfirmacion");
			e.printStackTrace();
		}
		return mapRetornoMsg;
	}
	
	//get y set
	public ConfirmacionTransferenciasDao getConfirmacionTransferenciasDao() {
		return confirmacionTransferenciasDao;
	}
	public void setConfirmacionTransferenciasDao(ConfirmacionTransferenciasDao confirmacionTransferenciasDao) {
		this.confirmacionTransferenciasDao = confirmacionTransferenciasDao;
	}
}
