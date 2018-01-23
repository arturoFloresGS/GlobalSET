package com.webset.set.egresos.business;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.axis.AxisFault;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.egresos.dao.PagosEnSAPDao;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.ComunEgresosDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.ParamMovto3000Dto;
import com.webset.set.utilerias.dto.ParametroFactorajeDto;
import com.webset.set.utilerias.dto.StoreParamsComunDto;
import com.webset.utils.tools.Utilerias;

import mx.com.gruposalinas.Pagos.DT_Pagos_OBPagos;
import mx.com.gruposalinas.Pagos.DT_Pagos_ResponsePagosResponse;
import mx.com.gruposalinas.Pagos.SOS_PagosBindingStub;
import mx.com.gruposalinas.Pagos.SOS_PagosServiceLocator;



public class PagosEnSAPBusiness {
	private PagosEnSAPDao pagosEnSAPDao;
	private GlobalSingleton globalSingleton;
	private Bitacora bitacora = new Bitacora();
	private Funciones funciones= new Funciones();
	private String ubicacion = "";
	
	private Map<String,Object> agruparMovimientos(List<ComunEgresosDto> listGrid){
		System.out.println("bisunas generico de egresos");
		System.out.println("bisunas generico de egresos");
		Map<String, Object>mapRetorno = new HashMap<String, Object>();
		List<MovimientoDto> listPagProApagar=new ArrayList<MovimientoDto>();
		List<MovimientoDto> rstDatos = new ArrayList<MovimientoDto>();
		List<MovimientoDto>listPagosBenefDatosAlt = new ArrayList<MovimientoDto>();
		List<MovimientoDto>listGrupos = new ArrayList<MovimientoDto>();
		Map<String,Object> mapCuadrante = new HashMap<String,Object>();
		StoreParamsComunDto cambiaCuadranteDto= new StoreParamsComunDto();
		ComunEgresosDto dtoParam = new  ComunEgresosDto();
		ArrayList<String> mensajes = new ArrayList<String>();
		String psDoctosCruzarAut = "";
		String psFoliosCruzado = "";
		String psFolios = "";
		String psDocumentosCompensar = "";
		String psFoliosWL = "";
		String psEmpresa = "";
		String psNoDocto = "";
		String psDivisa = "";
		int iIdBancoBenef = 0;
		int ctaDatos = 0;
        String sIdChequeraBenef = "";
        int idxGrupo = -1;
		ComunEgresosDto dtoParamGral = new ComunEgresosDto();
	    List<ComunDto> listBanCheqBenef = new ArrayList<ComunDto>();
        ComunDto paramBanCheqBenef=new ComunDto();
        listBanCheqBenef=new ArrayList<ComunDto>();
		double lAfec = 0;
		double pdTipoCambio = 0;
		mapRetorno.put("msjUsuario", "Error desconocido.");
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			if (listGrid.size()>0) {
				for (int i = 0; i < listGrid.size(); i++) {
					if(listGrid.get(i).getCveControl()!=null && !listGrid.get(i).getCveControl().equals("") 
							&& listGrid.get(i).getCveControl().trim().substring(0,2).equals("MF")){
						
						listPagProApagar=new ArrayList<MovimientoDto>();
						dtoParam= new ComunEgresosDto();
						dtoParam.setIdGrupoEmpresas(listGrid.get(i).getIdGrupoEmpresas()>0?listGrid.get(i).getIdGrupoEmpresas():0);
						dtoParam.setFechaPago(listGrid.get(i).getFechaPago());
						dtoParam.setCveControl(listGrid.get(i).getCveControl());
						dtoParam.setIdGrupoRubros(listGrid.get(i).getIdGrupoRubros());
						dtoParam.setPsDivision(listGrid.get(i).getPsDivision()!=null?listGrid.get(i).getPsDivision():"");
						dtoParam.setPsAgrupaCheques(listGrid.get(i).getPsAgrupaCheques());
						dtoParam.setPsAgrupaTransfers(listGrid.get(i).getPsAgrupaTransfers());
						listPagProApagar=pagosEnSAPDao.consultarPagPropApagar(dtoParam);
					
					}else if(listGrid.get(i).getCveControl()!=null && !listGrid.get(i).getCveControl().equals("") 
							&& listGrid.get(i).getCveControl().trim().substring(0,1).equals("M")){
						String origenMovimiento = pagosEnSAPDao.obtieneOrigenMov(listGrid.get(i).getCveControl());
						if (origenMovimiento.equals("CAJ") || origenMovimiento.equals("CVD")) {
							//no entra para origen sap
							rstDatos = pagosEnSAPDao.consultarPagosCpaVtaTransferPropManual(listGrid.get(i).getCveControl());
							if (rstDatos.size() > 0) {
								psFolios = "";
								psDocumentosCompensar = "";
		                        psDoctosCruzarAut = "";
		                        psFoliosWL = "";
		                        for (int j = 0; j < rstDatos.size(); j++) {
									if ((rstDatos.get(j).getIdFormaPago()== 3 || rstDatos.get(j).getIdFormaPago()== 5) &&
											(rstDatos.get(j).getIdServicioBe().equals("CVD")||rstDatos.get(j).getIdServicioBe().equals("CAJ"))) {
										if (psFolios.equals("")) {
											psFolios = String.valueOf(rstDatos.get(j).getNoFolioDet());
											psDocumentosCompensar = String.valueOf(rstDatos.get(j).getNoDocto());
										} else {
											psFolios += "," + rstDatos.get(j).getNoFolioDet();
											psDocumentosCompensar  += "," +  rstDatos.get(j).getNoDocto();
										}
									}//if for1
								}//for
		                        //termina validacion de compra de divisas y metales
							} //if rstDatos
						} else {
							//origen sap
							List<MovimientoDto> listMovto=new ArrayList<MovimientoDto>();
							listMovto=pagosEnSAPDao.consultarPagosCpaVtaTransferPropManual(listGrid.get(i).getCveControl());
							if (listMovto.size()>0) {
								psFolios = "";
								psDocumentosCompensar  ="";
								psDoctosCruzarAut = "";
								psFoliosWL = "";
								psFoliosCruzado = "";
								psEmpresa = "";
		                        psNoDocto = "";
		                        for (int j = 0; j < listMovto.size(); j++) {									
										if (listMovto.get(j).getBcoPagoCruzado()>0) {
											if (psFoliosCruzado.equals("")) {
												//psFoliosCruzado = String.valueOf(listMovto.get(j).getBcoPagoCruzado());
												psFoliosCruzado = String.valueOf(listMovto.get(j).getNoFolioDet());
											} else {
												psFoliosCruzado += "," +String.valueOf(listMovto.get(j).getNoFolioDet());
											}
											if (psEmpresa.equals("")) {
												psEmpresa = String.valueOf(listMovto.get(j).getNoEmpresa());
											} else {
												psEmpresa += "," +String.valueOf(listMovto.get(j).getNoEmpresa());
											}
											if (psNoDocto.equals("")) {
												psNoDocto = String.valueOf(listMovto.get(j).getNoDocto());
											} else {
												psNoDocto += "','" +String.valueOf(listMovto.get(j).getNoDocto());
											}
											pdTipoCambio = listMovto.get(j).getTipoCambio();
											psDoctosCruzarAut += "Para el Docto. "+listMovto.get(j).getNoDocto()
													+" Con Importe "+listMovto.get(j).getImporte()
													+" se le cambiara la divisa de pago de " + listMovto.get(j).getIdDivisa()
													+" a MN "
													+" por la cantidad final de "
													+(listMovto.get(j).getImporte()*listMovto.get(j).getTipoCambio())
													;
										}else{
											if ((listMovto.get(j).getIdFormaPago() == 3 || listMovto.get(j).getIdFormaPago() == 3)
													&& (listMovto.get(j).getNumCheqEmp() >= 0 && listMovto.get(j).getNumCheqEmp() == 0) 
													//||(listMovto.get(j).getBcoPagoCruzado()>0)
													|| (listMovto.get(j).getIdServicioBe()!= null && listMovto.get(j).getIdServicioBe().equals("CVT"))) {
												
												if (listMovto.get(j).getIdBancoPago() == ConstantesSet.CITIBANK_DLS
														&& listMovto.get(j).getIdFormaPago() == 3
														&& !listMovto.get(j).getIdDivisa().equals("MN") && !listMovto.get(j).getIdDivisa().equals("DLS")) {
													if (psFoliosWL.equals("")) {
														psFoliosWL = String.valueOf(listMovto.get(j).getNoFolioDet());
													} else {
														psFoliosWL += "," + String.valueOf(listMovto.get(j).getNoFolioDet());
													}
												}
												
												if (psFoliosCruzado.equals("")) {
													psFoliosCruzado = String.valueOf(listMovto.get(j).getNoFolioDet());
													psDocumentosCompensar  =  String.valueOf(listMovto.get(j).getNoDocto());
												} else {
													psFoliosCruzado += "," + listMovto.get(j).getNoFolioDet();
													psDocumentosCompensar  += "','" +  listMovto.get(j).getNoDocto();
												}
												psDoctosCruzarAut += "Para el Docto. " + listMovto.get(j).getNoDocto()+
														" se hara una Compra-Venta de Transfer";
											}
									}//if
								}//for
							}//if numero uno//' termina validacion de compra de transfer
						}
						//ok
						if (!psDoctosCruzarAut.equals("")) {
							if(!psFoliosWL.equals(""))
								lAfec=pagosEnSAPDao.actualizarEstatusComVtaTransfer(psFoliosWL, true);
							if(!psFolios.equals(""))
								lAfec=pagosEnSAPDao.actualizarEstatusComVtaTransfer(psFolios,false);
							if (!psFoliosCruzado.equals("")) 
								lAfec = pagosEnSAPDao.actualizaMovtoTC(globalSingleton.getUsuarioLoginDto().getIdUsuario(),
										"",pdTipoCambio, psFoliosCruzado,  psEmpresa,  psNoDocto);
						}
						psFolios = "";
						psDocumentosCompensar  = "";
		                psDoctosCruzarAut = "";
		                psFoliosWL = "";
		                listPagProApagar=new ArrayList<MovimientoDto>();
		                dtoParam= new ComunEgresosDto(); 
		                psDocumentosCompensar = "";
						psFolios = "";
		                psDoctosCruzarAut = "";
		                psFoliosWL = "";
				        //setters para una nueva llamada        
	
		                dtoParam.setIdGrupoEmpresas(listGrid.get(i).getIdGrupoEmpresas()>0?listGrid.get(i).getIdGrupoEmpresas():0);
		                dtoParam.setFechaPago(listGrid.get(i).getFechaPago());
		                dtoParam.setCveControl(listGrid.get(i).getCveControl());
		                dtoParam.setIdGrupoRubros(listGrid.get(i).getIdGrupoRubros());
		                dtoParam.setPsDivision(listGrid.get(i).getPsDivision()!=null?listGrid.get(i).getPsDivision():"");
		                dtoParam.setPsAgrupaCheques(listGrid.get(i).getPsAgrupaCheques());
		                dtoParam.setPsAgrupaTransfers(listGrid.get(i).getPsAgrupaTransfers());
		                listPagProApagar= pagosEnSAPDao.consultarPagPropApagar(dtoParam);
		                //ok
					}else{//if dentro del for 1
						//' Movimientos de Compra Venta de Transfer
						List<MovimientoDto>listPagCruzadosAut= new ArrayList<MovimientoDto>();
						dtoParam= new ComunEgresosDto();
						dtoParam.setFechaHoy(pagosEnSAPDao.obtenerFechaHoy());
						dtoParam.setCveControl(listGrid.get(i).getCveControl());
						dtoParam.setPsDivision("");
						dtoParam.setCompraVtaTransfer(true);
		               
						listPagCruzadosAut=pagosEnSAPDao.consultarPagosCruzadosAut(dtoParam);
						
						if(listPagCruzadosAut.size()>0){
							psFolios="";
							psDocumentosCompensar  ="";
							for(int in=0;in<listPagCruzadosAut.size();in++){
								if((listPagCruzadosAut.get(in).getIdFormaPago()==3 || listPagCruzadosAut.get(in).getIdFormaPago()==5)
									&& listPagCruzadosAut.get(in).getNumCheqCte()>0 && listPagCruzadosAut.get(in).getNumCheqEmp()==0){
									
									if(psFolios.equals("")){
		                                psFolios = ""+listPagCruzadosAut.get(in).getNoFolioDet();
		                                psDocumentosCompensar  =""+ listPagCruzadosAut.get(in).getNoDocto();
									}else{
		                                psFolios +=","+listPagCruzadosAut.get(in).getNoFolioDet();
		                                psDocumentosCompensar  += "," + listPagCruzadosAut.get(in).getNoDocto();
									}
									psDoctosCruzarAut +="Para el Docto. "+listPagCruzadosAut.get(in).getNoDocto()+
	                                " se hara una Compra-Venta de Transfer";
								}
							}
						}
						// ok 1
						listPagCruzadosAut = new ArrayList<MovimientoDto>();
						if (psDoctosCruzarAut.equals("")) 
							lAfec=pagosEnSAPDao.actualizarEstatusComVtaTransfer(psFolios,false);
							
						psFolios = "";
						psDocumentosCompensar  = "";
		                psDoctosCruzarAut = "";
		                //' Movimientos de Pago Cruzado Automatico
		                dtoParam= new ComunEgresosDto();
						dtoParam.setFechaHoy(pagosEnSAPDao.obtenerFechaHoy());
						dtoParam.setCveControl(listGrid.get(i).getCveControl());
						dtoParam.setPsDivision("");
						dtoParam.setCompraVtaTransfer(true);   
						
						listPagCruzadosAut=pagosEnSAPDao.consultarPagosCruzadosAut(dtoParam);
						// ok
						//' Realiza los pagos cruzados automaticos determinados
						if(listPagCruzadosAut.size()>0){
								for(int j=0; j<listPagCruzadosAut.size();j++){
									if(listPagCruzadosAut.get(j).getIdBancoCte()<=0
										&& (listPagCruzadosAut.get(j).getIdChequeraCte()==null || listPagCruzadosAut.get(j).getIdChequeraCte().equals(""))
										&& listPagCruzadosAut.get(j).getBcoPagoCruzado()>0
										&& (listPagCruzadosAut.get(j).getCheqPagoCruzado()!=null && !listPagCruzadosAut.get(j).getCheqPagoCruzado().equals(""))
										&& (listPagCruzadosAut.get(j).getDivPagoCruzado()!=null && !listPagCruzadosAut.get(j).getDivPagoCruzado().equals(""))
										&& (listPagCruzadosAut.get(j).getDivPagoCruzado()!=null && listPagCruzadosAut.get(j).getDivPagoCruzado().equals("MN"))
										&& listPagCruzadosAut.get(j).getNumCheqCruceDLS()==0
										&& listPagCruzadosAut.get(j).getNumCheqCruceMN()>0
										&& listPagCruzadosAut.get(j).getNumCheqPagoMN()>0
										&& (listPagCruzadosAut.get(j).getIdDivisa()!=null && listPagCruzadosAut.get(j).getIdDivisa().equals("DLS")))
									{
										psDoctosCruzarAut +="Para el Docto. "+listPagCruzadosAut.get(j).getNoDocto()+" se le cambiara la divisa de pago de "+ 
										listPagCruzadosAut.get(j).getIdDivisa()+ " a " +listPagCruzadosAut.get(j).getDivPagoCruzado()+
	                                    " por la cantidad final de " + listPagCruzadosAut.get(j).getImpPagoCruzado();
	                                    
									}
									else if(listPagCruzadosAut.get(j).getIdBancoCte()<=0
											&& (listPagCruzadosAut.get(j).getIdChequeraCte()==null || listPagCruzadosAut.get(j).getIdChequeraCte().equals(""))
											&& listPagCruzadosAut.get(j).getBcoPagoCruzado()<=0
											&& (listPagCruzadosAut.get(j).getCheqPagoCruzado()==null || listPagCruzadosAut.get(j).getCheqPagoCruzado().equals(""))
											&& (listPagCruzadosAut.get(j).getDivPagoCruzado()==null && listPagCruzadosAut.get(j).getDivPagoCruzado().equals(""))
											&& listPagCruzadosAut.get(j).getNumCheqCruceMN()>0
											&& listPagCruzadosAut.get(j).getNumCheqPagoDLS()==0
											&& listPagCruzadosAut.get(j).getNumCheqPagoMN()>0
											&& (listPagCruzadosAut.get(j).getIdDivisa()!=null && listPagCruzadosAut.get(j).getIdDivisa().equals("DLS")))
									{
										psDoctosCruzarAut +="Para el Docto. "+listPagCruzadosAut.get(j).getNoDocto()+" se le cambiara la divisa de pago de "
	                                    +listPagCruzadosAut.get(j).getIdDivisa()+" a MN " + " por la cantidad final de " + listPagCruzadosAut.get(j).getImpPagoCruzado();
									}
									else if(listPagCruzadosAut.get(j).getIdBancoCte()<=0
											&& (listPagCruzadosAut.get(j).getIdChequeraCte()==null || listPagCruzadosAut.get(j).getIdChequeraCte().equals(""))
											&& listPagCruzadosAut.get(j).getBcoPagoCruzado()>0
											&& (listPagCruzadosAut.get(j).getCheqPagoCruzado()!=null || !listPagCruzadosAut.get(j).getCheqPagoCruzado().equals(""))
											&& (listPagCruzadosAut.get(j).getDivPagoCruzado()!=null && listPagCruzadosAut.get(j).getDivPagoCruzado().equals("DLS"))
											&& listPagCruzadosAut.get(j).getNumCheqCruceMN()>0
											&& listPagCruzadosAut.get(j).getNumCheqPagoDLS()==0
											&& listPagCruzadosAut.get(j).getNumCheqPagoMN()>0
											&& (listPagCruzadosAut.get(j).getIdDivisa()!=null && listPagCruzadosAut.get(j).getIdDivisa().equals("DLS")))
									{
										psDoctosCruzarAut +="Para el Docto. " + listPagCruzadosAut.get(j).getNoDocto() + " se le cambiara la divisa de pago de " +
										listPagCruzadosAut.get(j).getIdDivisa() + " a MN " + " por la cantidad final de " + listPagCruzadosAut.get(j).getImpPagoCruzado();
									}
								}//for
								// ok
								//' Realiza los pagos cruzados automaticos determinados
								if(!psDoctosCruzarAut.equals("")){
									for(int ji=0; ji<listPagCruzadosAut.size();ji++){
										if(listPagCruzadosAut.get(ji).getIdBancoCte()<=0
												&& (listPagCruzadosAut.get(ji).getIdChequeraCte()==null || listPagCruzadosAut.get(ji).getIdChequeraCte().equals(""))
												&& listPagCruzadosAut.get(ji).getBcoPagoCruzado()>0
												&& (listPagCruzadosAut.get(ji).getCheqPagoCruzado()!=null && !listPagCruzadosAut.get(ji).getCheqPagoCruzado().equals(""))
												&& (listPagCruzadosAut.get(ji).getDivPagoCruzado()!=null && !listPagCruzadosAut.get(ji).getDivPagoCruzado().equals(""))
												&& (listPagCruzadosAut.get(ji).getDivPagoCruzado()!=null && listPagCruzadosAut.get(ji).getDivPagoCruzado().equals("MN"))
												&& listPagCruzadosAut.get(ji).getNumCheqCruceDLS()==0
												&& listPagCruzadosAut.get(ji).getNumCheqCruceMN()>0
												&& listPagCruzadosAut.get(ji).getNumCheqPagoMN()>0
												&& (listPagCruzadosAut.get(ji).getIdDivisa()!=null && listPagCruzadosAut.get(ji).getIdDivisa().equals("DLS")))
										{
											psFolios = "";
											psDocumentosCompensar  = "";
											if (listPagCruzadosAut.get(ji).getIdFormaPago() == 3 || listPagCruzadosAut.get(ji).getIdFormaPago() == 5) {
												lAfec=pagosEnSAPDao.actualizarBancoCheqBenef(listPagCruzadosAut.get(ji).getNoFolioDet(),
			                                    		listPagCruzadosAut.get(ji).getBcoPagoCruzado(),listPagCruzadosAut.get(ji).getCheqPagoCruzado()); 
		                                        if(lAfec>0){ 
		                                            psFolios = psFolios + listPagCruzadosAut.get(ji).getNoFolioDet()+",";
		                                            psDocumentosCompensar = psDocumentosCompensar + listPagCruzadosAut.get(ji).getNoDocto()+",";
		                                        }else{
		                                        	mensajes.add("No se pudo cambiar el Banco y Chequera Beneficiarios del Movto.: "+
		                                        			listPagCruzadosAut.get(ji).getNoFolioDet());
	                                        	}
											} else if(listPagCruzadosAut.get(ji).getIdFormaPago() == 1){
												if (!listPagCruzadosAut.get(ji).getDivPagoCruzado().equals(listPagCruzadosAut.get(ji).getIdDivisa())) {
													psFolios += listPagCruzadosAut.get(ji).getNoFolioDet()+",";
												}
											}
											// ok
											if(!psFolios.equals("")){
													mapCuadrante= new HashMap<String,Object>();
													cambiaCuadranteDto= new StoreParamsComunDto();
													
													cambiaCuadranteDto.setParametro(globalSingleton.getUsuarioLoginDto().getIdUsuario()
															+","+listPagCruzadosAut.get(ji).getIdFormaPago()+","
															+funciones.ponerFecha(listPagCruzadosAut.get(ji).getFecValor())+","+listPagCruzadosAut.get(ji).getConcepto()+","
															+listPagCruzadosAut.get(ji).getDivPagoCruzado()+","+listPagCruzadosAut.get(ji).getTipoCambioCruzado()+","+psFolios);
													mapCuadrante=pagosEnSAPDao.cambiarCuadrantes(cambiaCuadranteDto);
			                
			                                        if(Integer.parseInt(mapCuadrante.get("result").toString())>0){
			                                        	mensajes.add("No se pudo cambiar la Divisa de Pago, Folios: "+ psFolios);
			                                        	bitacora.insertarRegistro(new Date().toString()+ "P:Egresos, C:PagoPropuestasBusiness, M:agruparMovimientos "
			                                        			+globalSingleton.getUsuarioLoginDto().getIdUsuario()
			                                        			+"Error al cambiar la Divisa de Pago Llamada a ajusteNETROSET "+"Divisa de Pago: "
			                                        			+listPagCruzadosAut.get(ji).getDivPagoCruzado()+" Tipo de Cambio: "+listPagCruzadosAut.get(ji).getTipoCambioCruzado()
			                                        			+ " Folios: "+ psFolios);
			                                        }
			                                    
											}
											psDivisa = listPagCruzadosAut.get(ji).getDivPagoCruzado();//tentativo
										}else if(listPagCruzadosAut.get(ji).getIdBancoCte()<=0
												&& (listPagCruzadosAut.get(ji).getIdChequeraCte()==null || listPagCruzadosAut.get(ji).getIdChequeraCte().equals(""))
												&& listPagCruzadosAut.get(ji).getBcoPagoCruzado()<=0
												&& (listPagCruzadosAut.get(ji).getCheqPagoCruzado()==null || listPagCruzadosAut.get(ji).getCheqPagoCruzado().equals(""))
												&& (listPagCruzadosAut.get(ji).getDivPagoCruzado()==null || listPagCruzadosAut.get(ji).getDivPagoCruzado().equals(""))
												&& listPagCruzadosAut.get(ji).getNumCheqCruceMN()>0
												&& listPagCruzadosAut.get(ji).getNumCheqPagoDLS()==0
												&& listPagCruzadosAut.get(ji).getNumCheqPagoMN()>0
												&& (listPagCruzadosAut.get(ji).getIdDivisa()!=null && listPagCruzadosAut.get(ji).getIdDivisa().equals("DLS")))
										{
		                                     psFolios = "";
		                                     psDocumentosCompensar = "";
	                                         if(listPagCruzadosAut.get(ji).getIdFormaPago()==3
	                                        		 || listPagCruzadosAut.get(ji).getIdFormaPago()==5){
	                                         
	                                            iIdBancoBenef = 0;
	                                            sIdChequeraBenef = "";
	                                             
	                                            paramBanCheqBenef=new ComunDto();
	                                            listBanCheqBenef=new ArrayList<ComunDto>();
	                                            paramBanCheqBenef.setIdProveedor(listPagCruzadosAut.get(ji).getNoCliente()!=null?Integer.parseInt(listPagCruzadosAut.get(ji).getNoCliente()):0);
	                                            paramBanCheqBenef.setIdDivisa(listPagCruzadosAut.get(ji).getIdDivisa());
	                                            paramBanCheqBenef.setIdBanco(0);
	                                            listBanCheqBenef = pagosEnSAPDao.consultarBancoCheqBenef(paramBanCheqBenef);
	                                            
	                                            if(listBanCheqBenef.size()>0){
		                                            	 iIdBancoBenef = listBanCheqBenef.get(0).getIdBanco();
		                                                 sIdChequeraBenef = listBanCheqBenef.get(0).getIdChequera();
	                                            }
	                                            paramBanCheqBenef=new ComunDto();
	                                            paramBanCheqBenef.setNoFolioDet(listPagCruzadosAut.get(ji).getNoFolioDet());
	                                            paramBanCheqBenef.setIdBanco(iIdBancoBenef);
	                                            paramBanCheqBenef.setIdChequera(sIdChequeraBenef);
	                                            lAfec = pagosEnSAPDao.actualizarBancoCheqBenef(paramBanCheqBenef);
	                                             
		                                        if(lAfec>0){
		                                             psFolios = psFolios + listPagCruzadosAut.get(ji).getNoFolioDet() + ",";
		                                             psDocumentosCompensar  = psDocumentosCompensar +  listPagCruzadosAut.get(ji).getNoDocto()+ ",";
		                                        }else{
		                                        	 mensajes.add("No se pudo cambiar el Banco y Chequera Beneficiarios del Movto.:"+
		                                        			 listPagCruzadosAut.get(ji).getNoFolioDet());
		                                        }
		                                        psDivisa = "MN";
		                                        //ok
	                                         }else if (listPagCruzadosAut.get(ji).getIdFormaPago()==1){
	                                    	     
	                                        	 if((listPagCruzadosAut.get(ji).getDivPagoCruzado()!=null 
	                                        			 && listPagCruzadosAut.get(ji).getIdDivisa()!=null)
	                            	    		 &&(listPagCruzadosAut.get(ji).getDivPagoCruzado().equals(listPagCruzadosAut.get(ji).getIdDivisa())))
	                                        	 { 
	                                        		 psFolios = psFolios + listPagCruzadosAut.get(ji).getNoFolioDet() + ",";
	                                        	 	psDocumentosCompensar  = psDocumentosCompensar +  listPagCruzadosAut.get(ji).getNoDocto()+ ",";
	                                        	 }
	                                         }
	                                         if(!psFolios.equals("")){
		                                        	 mapCuadrante= new HashMap<String, Object>();
														cambiaCuadranteDto= new StoreParamsComunDto();
														cambiaCuadranteDto.setParametro(globalSingleton.getUsuarioLoginDto().getIdUsuario()
																+","+listPagCruzadosAut.get(ji).getIdFormaPago()+","
																+funciones.ponerFecha(listPagCruzadosAut.get(ji).getFecValor())+","+listPagCruzadosAut.get(ji).getConcepto()+","
																+psDivisa+","+listPagCruzadosAut.get(ji).getTipoCambioCruzado()+","+psFolios);
														mapCuadrante=pagosEnSAPDao.cambiarCuadrantes(cambiaCuadranteDto);
				                
			                                        if(Integer.parseInt(mapCuadrante.get("result").toString())>0){
		                                        		 mensajes.add(" No se pudo cambiar la Divisa de Pago, Folios: "+psFolios);
		                                        		 bitacora.insertarRegistro(new Date().toString()+ "P:Egresos, C:PagoPropuestasBusiness, M:agruparMovimientos "
		                                        				 	+globalSingleton.getUsuarioLoginDto().getIdUsuario()
				                                        			+"Error al cambiar la Divisa de Pago Llamada a ajusteNETROSET "+"Divisa de Pago: "
				                                        			+listPagCruzadosAut.get(ji).getDivPagoCruzado()+" Tipo de Cambio: "+listPagCruzadosAut.get(ji).getTipoCambioCruzado()
				                                        			+ " Folios: "+ psFolios);
		                                        	 }
	                                         }
											 //ok falta
	                                         
										}
										/////nuevo
										else if(listPagCruzadosAut.get(ji).getIdBancoCte()<=0
												&& (listPagCruzadosAut.get(ji).getIdChequeraCte()==null || listPagCruzadosAut.get(ji).getIdChequeraCte().equals(""))
												&& listPagCruzadosAut.get(ji).getBcoPagoCruzado()>0
												&& (listPagCruzadosAut.get(ji).getCheqPagoCruzado()!=null && !listPagCruzadosAut.get(ji).getCheqPagoCruzado().equals(""))
												&& (listPagCruzadosAut.get(ji).getDivPagoCruzado()!=null || listPagCruzadosAut.get(ji).getDivPagoCruzado().equals("DLS"))
												&& listPagCruzadosAut.get(ji).getNumCheqCruceMN()>0
												&& listPagCruzadosAut.get(ji).getNumCheqPagoDLS()==0
												&& listPagCruzadosAut.get(ji).getNumCheqPagoMN()>0
												&& (listPagCruzadosAut.get(ji).getIdDivisa()!=null && listPagCruzadosAut.get(ji).getIdDivisa().equals("DLS")))
										{
											psFolios = "";
											psDocumentosCompensar  = "";
											if(listPagCruzadosAut.get(ji).getIdFormaPago()==3 
													|| listPagCruzadosAut.get(ji).getIdFormaPago()==5)
											{
		                                        iIdBancoBenef = 0;
		                                        sIdChequeraBenef = "";
	
		                                        paramBanCheqBenef=new ComunDto();
	                                            listBanCheqBenef=new ArrayList<ComunDto>();
	                                            paramBanCheqBenef.setIdProveedor(listPagCruzadosAut.get(ji).getNoCliente()!=null?Integer.parseInt(listPagCruzadosAut.get(ji).getNoCliente()):0);
	                                            paramBanCheqBenef.setIdDivisa("MN");
	                                            paramBanCheqBenef.setIdBanco(0);
	                                            listBanCheqBenef = pagosEnSAPDao.consultarBancoCheqBenef(paramBanCheqBenef);
	                                            
	                                            if(listBanCheqBenef.size()>0)
	                                            {
	                                            	iIdBancoBenef = listBanCheqBenef.get(0).getIdBanco();
		                                            sIdChequeraBenef = listBanCheqBenef.get(0).getIdChequera();
	                                            }
	                                            
	                                            paramBanCheqBenef=new ComunDto();
	                                            paramBanCheqBenef.setNoFolioDet(listPagCruzadosAut.get(ji).getNoFolioDet());
	                                            paramBanCheqBenef.setIdBanco(iIdBancoBenef);
	                                            paramBanCheqBenef.setIdChequera(sIdChequeraBenef);
	                                            lAfec = pagosEnSAPDao.actualizarBancoCheqBenef(paramBanCheqBenef);
	                                            
		                                        if(lAfec>0){
		                                            psFolios = psFolios +listPagCruzadosAut.get(ji).getNoFolioDet()+ ",";
		                                            psDocumentosCompensar  = psDocumentosCompensar +  listPagCruzadosAut.get(ji).getNoDocto()+ ",";
		                                        }else{
		                                        	mensajes.add("No se pudo cambiar el Banco y Chequera Beneficiarios del Movto.:"
		                                        			+listPagCruzadosAut.get(ji).getNoFolioDet());
		                                        }
		                                        psDivisa = "MN";
											}  
		                                    else if(listPagCruzadosAut.get(ji).getIdFormaPago()==1){
		                                        if(listPagCruzadosAut.get(ji).getDivPagoCruzado()!=listPagCruzadosAut.get(ji).getIdDivisa()){
		                                            psFolios = psFolios + listPagCruzadosAut.get(ji).getNoFolioDet()+ ",";
		                                            psDocumentosCompensar  = psDocumentosCompensar +  listPagCruzadosAut.get(ji).getNoDocto()+ ",";
		                                        }
											}
											
											 if(!psFolios.equals(""))
											 {
												mapCuadrante= new HashMap<String,Object>();
												cambiaCuadranteDto= new StoreParamsComunDto();
												cambiaCuadranteDto.setParametro(globalSingleton.getUsuarioLoginDto().getIdUsuario()
														+","+listPagCruzadosAut.get(ji).getIdFormaPago()+","
														+funciones.ponerFecha(listPagCruzadosAut.get(ji).getFecValor())+","+listPagCruzadosAut.get(ji).getConcepto()+","
														+psDivisa+","+listPagCruzadosAut.get(ji).getTipoCambioCruzado()+","+psFolios);
												mapCuadrante=pagosEnSAPDao.cambiarCuadrantes(cambiaCuadranteDto);
			                
		                                        if(Integer.parseInt(mapCuadrante.get("result").toString())>0)
		                                        {
	                                        		 mensajes.add(" No se pudo cambiar la Divisa de Pago, Folios: "+psFolios);
	                                        		 bitacora.insertarRegistro(new Date().toString()+ "P:Egresos, C:PagoPropuestasBusiness, M:agruparMovimientos "
	                                        				 	+globalSingleton.getUsuarioLoginDto().getIdUsuario()
			                                        			+"Error al cambiar la Divisa de Pago Llamada a ajusteNETROSET "+"Divisa de Pago: "
			                                        			+listPagCruzadosAut.get(ji).getDivPagoCruzado()+" Tipo de Cambio: "+listPagCruzadosAut.get(ji).getTipoCambioCruzado()
			                                        			+ " Folios: "+ psFolios);
	                                        	 }
											 }
										}
									}//for
							}//if if(!psDoctosCruzarAut.equals("")) //' Realiza los pagos cruzados automaticos determinados
													
						}//if
		                
					
						//' Determina aquellos movimientos con id_leyenda con *, y aquellos
			            //' que no tienen bien definida una chequera beneficiaria adecuada
						dtoParam= new ComunEgresosDto();
						listPagosBenefDatosAlt=new ArrayList<MovimientoDto>();
						dtoParam.setCveControl(listGrid.get(i).getCveControl());
						
						listPagosBenefDatosAlt= pagosEnSAPDao.consultarPagosBenefDatosAlt(dtoParam);
						
						if(listPagosBenefDatosAlt.size()>0)
						{
							paramBanCheqBenef=new ComunDto();
			                paramBanCheqBenef.setNoFolioDet(listPagosBenefDatosAlt.get(0).getNoFolioDet());
			                paramBanCheqBenef.setIdBanco(listPagosBenefDatosAlt.get(0).getIdBancoBenef());
			                paramBanCheqBenef.setIdChequera(listPagosBenefDatosAlt.get(0).getIdChequeraBenef());
			                lAfec = pagosEnSAPDao.actualizarBancoCheqBenef(paramBanCheqBenef);
						}
						//' Hace los pagos normales de los documentos en la propuesta
						dtoParamGral= new ComunEgresosDto();
						listPagProApagar=new ArrayList<MovimientoDto>();
						dtoParamGral.setFecha(listGrid.get(i).getFechaPago());
						dtoParamGral.setCveControl(listGrid.get(i).getCveControl());
						dtoParamGral.setIdGrupoRubros(listGrid.get(i).getIdGrupoRubros());
						dtoParamGral.setIdGrupoEmpresas(listGrid.get(i).getIdGrupoEmpresas());
						listPagProApagar=pagosEnSAPDao.consultarPagPropAut1(dtoParamGral);//llama a consultarPagPropAut1
						
						dtoParam= new ComunEgresosDto();
						dtoParam.setCveControl(listGrid.get(i).getCveControl());
						ctaDatos=pagosEnSAPDao.contarMovsPropMAut(dtoParam);
						
						if(listPagProApagar.size()!=ctaDatos){
								mensajes.add("La propuesta "+dtoParam.getCveControl() +" no podra pagarse completa, si desea ver " +
			                      "las razones, por favor abra la pantalla de consulta de " +
			                      "propuestas y seleccionela dentro de la lista. ");
						}
						
					}
					////////
					
					//String foliosDet="";
					String foliosMass="";
					//piNumFolioMassActual = 0;
		            //piNumArregloMassActual = 1;
					MovimientoDto dtoGrupos= new MovimientoDto();
					if(listPagProApagar.size()>0){
						for(int cont=0; cont<listPagProApagar.size();cont++){
							dtoGrupos= new MovimientoDto();
							if(listGrupos.size()==0 
								|| verificarNuevo(listPagProApagar.get(cont).getNoEmpresa(), listPagProApagar.get(cont).getIdProveedor(),
										listPagProApagar.get(cont).getIdDivisa(), listPagProApagar.get(cont).getIdFormaPago(),
										listPagProApagar.get(cont).getIdBancoPago(), listPagProApagar.get(cont).getIdChequeraPago(),
										listPagProApagar.get(cont).getBeneficiario(), ""+listGrid.get(i).getAgrupaEmpChe(),
										listGrupos.size()>0 ? listGrupos.get(listGrupos.size()-1).getCveControl() : "",listGrupos,
												listPagProApagar.get(cont).getCLote(),
												listPagProApagar.get(cont).getCveControl(),
												listPagProApagar.get(cont).getIdChequeraBenef())
								|| listPagProApagar.get(cont).getIdFormaPago()==5
								|| ubicacion.trim().equals("CCM"))
							{
								idxGrupo++;
									if(pagosEnSAPDao.consultarConfiguraSet(261).equals("SI")){
										List<String>listMassPay= new ArrayList<String>();
										
										listMassPay=pagosEnSAPDao.consultarPagoMassPayment(listPagProApagar.get(cont).getNoFolioDet(),
												pagosEnSAPDao.obtenerFechaHoy());
										
										if(listMassPay.size()>0){
											if(foliosMass.equals("")){
						//						piNumFolioMassActual = 1;
							//					piNumArregloMassActual ++;
												foliosMass=listMassPay.get(0);
											}else{
												foliosMass+=", "+listMassPay.get(0);
											}
										}else{
											//'PARA LA COMER SI LA FEC_VALOR > HOY Y EL BANCO <> BANAMEX NO SE PAGA.
				                            //'If Not (gobjVarGlobal.Ubicacion = "CCM" And msfDatos.TextMatrix(i, MI_FECHA_PAGO) > GT_FECHA_HOY) Then
				                           // if(ubicacion.equals("CCM") && listPagProApagar.get(cont).getIdBancoPago()==2
				                            //		&& listGrid.get(i).getFechaPago()<=compensarPropuestasDao.obtenerFechaHoy()){
												dtoGrupos.setNoEmpresa(listPagProApagar.get(cont).getNoEmpresa());
				                            	dtoGrupos.setIdProveedor(listPagProApagar.get(cont).getIdProveedor());
				                            	dtoGrupos.setBeneficiario(listPagProApagar.get(cont).getBeneficiario());
				                            	dtoGrupos.setIdDivisa(listPagProApagar.get(cont).getIdDivisa());
				                            	dtoGrupos.setIdFormaPago(listPagProApagar.get(cont).getIdFormaPago());
				                            	dtoGrupos.setIdBancoPago(listPagProApagar.get(cont).getIdBancoPago());
				                            	dtoGrupos.setIdChequeraPago(listPagProApagar.get(cont).getIdChequeraPago());
				                            	dtoGrupos.setNoMovimientos(1);
				                            	dtoGrupos.setNoFolioDet(listPagProApagar.get(cont).getNoFolioDet());
				                            	dtoGrupos.setPsFolios(""+listPagProApagar.get(cont).getNoFolioDet());
				                            	dtoGrupos.setNoDocto(listPagProApagar.get(cont).getNoDocto());
				                            	dtoGrupos.setBGenContable(listPagProApagar.get(cont).getDeudor()+
				                            			listPagProApagar.get(cont).getCPeriodo()+
				                            			listPagProApagar.get(cont).getNoDocto()+
				                            			listPagProApagar.get(cont).getInvoiceType());
				                            	dtoGrupos.setCveControl(listGrid.get(i).getCveControl());
				                            	dtoGrupos.setIdChequeraBenef(listPagProApagar.get(cont).getIdChequeraBenef());
				                            	dtoGrupos.setFecPropuesta(listGrid.get(i).getFechaPago());
				                            	dtoGrupos.setCLote(listPagProApagar.get(cont).getCLote());
				                            	dtoGrupos.setDeudor(listPagProApagar.get(cont).getDeudor());
				                            	listGrupos.add(idxGrupo, dtoGrupos);
				                            	System.out.println("-------------------1");
				                            //}
										}
									}else{
										System.out.println("-------------------2");
			                            	dtoGrupos.setNoEmpresa(listPagProApagar.get(cont).getNoEmpresa());
			                            	dtoGrupos.setIdProveedor(listPagProApagar.get(cont).getIdProveedor());
			                            	dtoGrupos.setBeneficiario(listPagProApagar.get(cont).getBeneficiario());
			                            	dtoGrupos.setIdDivisa(listPagProApagar.get(cont).getIdDivisa());
			                            	dtoGrupos.setIdFormaPago(listPagProApagar.get(cont).getIdFormaPago());
			                            	dtoGrupos.setIdBancoPago(listPagProApagar.get(cont).getIdBancoPago());
			                            	dtoGrupos.setIdChequeraPago(listPagProApagar.get(cont).getIdChequeraPago());
			                            	dtoGrupos.setNoMovimientos(1);
			                            	dtoGrupos.setNoFolioDet(listPagProApagar.get(cont).getNoFolioDet());
			                            	dtoGrupos.setPsFolios(""+listPagProApagar.get(cont).getNoFolioDet());
			                            	dtoGrupos.setNoDocto(listPagProApagar.get(cont).getNoDocto());
			                            	dtoGrupos.setBGenContable(listPagProApagar.get(cont).getDeudor()+
			                            			listPagProApagar.get(cont).getCPeriodo()+
			                            			listPagProApagar.get(cont).getNoDocto()+
			                            			listPagProApagar.get(cont).getInvoiceType());
			                            	dtoGrupos.setCveControl(listGrid.get(i).getCveControl());//herebug
			                            	dtoGrupos.setIdChequeraBenef(listPagProApagar.get(cont).getIdChequeraBenef());
			                            	dtoGrupos.setFecPropuesta(listGrid.get(i).getFechaPago());
			                            	dtoGrupos.setDeudor(listPagProApagar.get(cont).getDeudor());
			                            	dtoGrupos.setCLote(listPagProApagar.get(cont).getCLote());
			                            	listGrupos.add(idxGrupo, dtoGrupos);
									}
							}else{
								if(pagosEnSAPDao.consultarConfiguraSet(261).equals("SI")){
									List<String>listMassPay= new ArrayList<String>();
									
									listMassPay=pagosEnSAPDao.consultarPagoMassPayment(listPagProApagar.get(cont).getNoFolioDet(),
											pagosEnSAPDao.obtenerFechaHoy());
									
									if(listMassPay.size()>0){
										if(foliosMass.equals(""))
											foliosMass=listMassPay.get(0);
										else
											foliosMass+=", "+listMassPay.get(0);
									}else{
		                            	listGrupos.get(idxGrupo).setNoMovimientos(listGrupos.get(idxGrupo).getNoMovimientos()+1);
		                            	listGrupos.get(idxGrupo).setPsFolios(listGrupos.get(idxGrupo).getPsFolios()+","+listPagProApagar.get(cont).getNoFolioDet());
		                            	listGrupos.get(idxGrupo).setBGenContable(listGrupos.get(idxGrupo).getBGenContable()+","+
		                            					listPagProApagar.get(cont).getDeudor()+
				                            			listPagProApagar.get(cont).getCPeriodo()+
				                            			listPagProApagar.get(cont).getNoDocto()+
				                            			listPagProApagar.get(cont).getInvoiceType());
									}
								}else{
									listGrupos.get(idxGrupo).setNoMovimientos(listGrupos.get(idxGrupo).getNoMovimientos()+1);
	                            	listGrupos.get(idxGrupo).setPsFolios(listGrupos.get(idxGrupo).getPsFolios()+","+listPagProApagar.get(cont).getNoFolioDet());
	                            	listGrupos.get(idxGrupo).setBGenContable(listGrupos.get(idxGrupo).getBGenContable()+","+
	                            					listPagProApagar.get(cont).getDeudor()+
			                            			listPagProApagar.get(cont).getCPeriodo()+
			                            			listPagProApagar.get(cont).getNoDocto()+
			                            			listPagProApagar.get(cont).getInvoiceType());
								}
							}
							//prbDetalle ++; pendiente
						}//for Propuestas
						
						if(!foliosMass.equals("")){
							pagosEnSAPDao.actualizarEstatusMass(foliosMass);
						}
					}
				}//for
			mapRetorno.put("listGrupos", listGrupos);
			mapRetorno.put("mensajes", mensajes);
			
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagoPropuestasBusiness, M:agruparMovimientos");
			e.printStackTrace();
		}
		return mapRetorno;
	}
	
	private boolean verificarNuevo(int noEmpresa, int noProveedor, String idDivisa, int idFormaPago, 
			int idBancoPag, String idChequeraPago, String psBeneficiario, String psAgrupaCheEmp, String psCveControl, 
			List<MovimientoDto> listGrupos, int cLote, String cveControl, String idChequeraBenef){
		boolean verificaNuevo=false;
		try{
			int index=listGrupos.size();
			if (listGrupos.size()==0 || psCveControl.trim().substring(0,2).equals("MF"))
	        	verificaNuevo = true;
		    else
		    {
		    	if(listGrupos.get(index-1).getNoEmpresa()==noEmpresa
		    			&& listGrupos.get(index-1).getIdProveedor()==noProveedor
		    			&& listGrupos.get(index-1).getIdDivisa().equals(idDivisa.trim())
		    			&& listGrupos.get(index-1).getIdFormaPago()==idFormaPago
		    			&& listGrupos.get(index-1).getIdBancoPago()==idBancoPag
    					&& listGrupos.get(index-1).getCveControl().equals(cveControl.trim())
    					&& listGrupos.get(index-1).getIdChequeraBenef().equals(idChequeraBenef.trim())
		    			&& listGrupos.get(index-1).getIdChequeraPago().equals(idChequeraPago)
		    			&& listGrupos.get(index-1).getBeneficiario().equals(psBeneficiario)
		    			)
		    	{
		    		if (!(idFormaPago == 1 || idFormaPago == 10)) {
						
			    		if(listGrupos.get(index-1).getIdFormaPago()==1)
			    		{
			    			if(psCveControl.trim().substring(1,2).equals("G"))
			    			{
			    				verificaNuevo = false;
			    			}else{
			    				//Valida si las chequeras se agrupan...
			    				if(pagosEnSAPDao.consultarConfiguraSet(220).equals("NO"))
			    				{
			    					if(psAgrupaCheEmp.trim().equals("S"))
		                                verificaNuevo = false;
		                            else
		                                verificaNuevo = true;
		                            
			    				}else{
			    					if(psAgrupaCheEmp.trim().equals("N"))
		                               verificaNuevo = false;
		                            else
		                                verificaNuevo = true;
		                            
		                            verificaNuevo = false;
			    				}
			    			}
			    		}
		    		}else{
		    			if(listGrupos.get(index-1).getCLote() == 1 ||   cLote == 1)
		    				verificaNuevo = true;
		    		}
		    	}else{
		    		verificaNuevo = true;
		    	}
		    }
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagoPropuestasBusiness, M:verificarNuevo");
		}
		
		return verificaNuevo;
	}
	
	public Map<String, Object> compensaPropuestasSAP(String datos, String fecHoy, int idUsuario, boolean ejecutaPagador, 
			boolean combierteJson, List<ComunEgresosDto> listPropuestas, boolean bfactoraje, int factoraje) {
		Map<String, Object>mapRetorno = new HashMap<String, Object>();
		List<ComunEgresosDto> listGrid;
		mapRetorno.put("msgError", "Error desconocido");
		mapRetorno.put("estatus", false);
		mapRetorno.put("reporteCVD",false);
		try {
			if (combierteJson) {
				listGrid = convierteJsonToListComunEgresosDto(datos);
				if(listGrid != null && listGrid.size() > 0)
					pagosEnSAPDao.actualizaMovimientoCompensar(listGrid); 
			}else{
				
				if(bfactoraje){
					String folios = getListaFoliosDetDePropuestas(datos);
					listGrid = getListaCvesControlDePropuesta(datos);
					pagosEnSAPDao.actualizaMovimientoANoCompensar(listGrid);
					pagosEnSAPDao.updateCancelSesionOperacionesCompraDeTransferParaPago(folios);
				}else{
					listGrid = listPropuestas;
				}
					
					
			}
			
			StringBuffer mensajesParaExcel = new StringBuffer("[");
			List<Map<String, String>>listarespuestaExportar = new ArrayList<Map<String, String>>();
			if(listGrid.size()>0){
				Map<String, Object> resAgrupaMov = agruparMovimientos(listGrid);
				
				List<MovimientoDto> listGrupos = 
						(List<MovimientoDto>)resAgrupaMov.get("listGrupos");
				
				if (listGrupos != null && listGrupos.size() > 0) {
					
					StringBuffer respuestas = new StringBuffer();
					
					for (int i = 0; i < listGrupos.size(); i++) {
						StringBuffer excelCompleto = new StringBuffer();
						
						Map<String, String>respuestaExportar = new HashMap<String, String>();
						respuestas.append("La propuesta con clave de control: ");
						respuestas.append(listGrupos.get(i).getCveControl());
						respuestaExportar.put("CLAVE_CONTROL", listGrupos.get(i).getCveControl());
						DT_Pagos_OBPagos pagoDto = pagosEnSAPDao.pagosParaWebservice (listGrupos.get(i), bfactoraje, factoraje);
						
						if (pagoDto != null) {
							
							Map<String, Object> mapaSAP = pagarEnSAP(pagoDto);
							if ((Boolean)mapaSAP.get("estatus")) {
								DT_Pagos_ResponsePagosResponse [] respuestaSAP;
								if (!pagosEnSAPDao.consultarConfiguraSet(4000).equals("local")) {
									System.out.println("no local");
								respuestaSAP = 
										(DT_Pagos_ResponsePagosResponse [])mapaSAP.get("pagosRespuesta");
								}else{
									System.out.println("local");
									respuestaSAP = new DT_Pagos_ResponsePagosResponse[1];
									respuestaSAP[0] = new DT_Pagos_ResponsePagosResponse("15", "99473", "0", "{\"TIPO\": \"E\", "
											+ "\"MENSAJE\": \""
											+ " se compenso con la poliza: "
											+ "\", "
											+ "\"ORIGEN\": \"SAP\"},",
											""+listGrupos.get(i).getCveControl()+"-"+i);
								}
								if (respuestaSAP != null && respuestaSAP.length != 0) {
									if (respuestaSAP[0].getDOC_POLIZA_SAP() != null && 
											!respuestaSAP[0].getDOC_POLIZA_SAP().equals("")) {
										
										pagosEnSAPDao.inserPagosZexpFact (listGrupos.get(i), idUsuario);
										pagosEnSAPDao.actualizaMovimientoCompensado(listGrupos.get(i), 
												respuestaSAP[0], fecHoy);
										respuestas.append(" se compenso con la poliza: ");
										respuestas.append(respuestaSAP[0].getDOC_POLIZA_SAP());
										respuestas.append("<br /><br />");
										
										excelCompleto.append("{\"TIPO\": \"S\", "
												+ "\"MENSAJE\": \""
												+ "Se compenso con la poliza: "+
													respuestaSAP[0].getDOC_POLIZA_SAP()
												+ "\", "
												+ "\"ORIGEN\": \"SAP\", \"ESTATUS\": \"Se compenso\", \"DOCUMENTO\": \""
													+ (listGrupos.get(i).getBGenContable().length() > 54 ?
															listGrupos.get(i).getBGenContable():
																listGrupos.get(i).getBGenContable())+"\"},");
										
										respuestaExportar.put("MENSAJE","Se compenso con la poliza: "+
												respuestaSAP[0].getDOC_POLIZA_SAP());
										respuestaExportar.put("ESTATUS","Se compenso");
										respuestaExportar.put("ORIGEN","SAP");
										respuestaExportar.put("DOCUMENTO",listGrupos.get(i).getBGenContable());
										mapRetorno.put("reporteCVD",true);
										/////////
										if(ejecutaPagador){
											
											Map<String, Object> respuestaPagador = ejecutarPagador( listGrupos.get(i) );
											if (!(Boolean)respuestaPagador.get("estatus")) {
												
												
												respuestas.append("(SET) ");
												respuestas.append(respuestaPagador.get("mensaje").toString());
												respuestas.append("<br /><br />");
												
												respuestaExportar.put("MENSAJE",respuestaPagador.get("mensaje").toString());
												respuestaExportar.put("ESTATUS","Se compenso");
												respuestaExportar.put("ORIGEN","SET");
												
												excelCompleto.append("{\"TIPO\": \"S\", "
														+ "\"MENSAJE\": \""
														+ respuestaPagador.get("mensaje").toString()
														+ "\", "
														+ "\"ORIGEN\": \"SET\", \"ESTATUS\": \"Se compenso\", \"DOCUMENTO\": \""
														+ (listGrupos.get(i).getBGenContable().length() > 54 ?
																listGrupos.get(i).getBGenContable():
																	listGrupos.get(i).getBGenContable())+"\"},");
											}
											
										}
										/////////
										pagosEnSAPDao.actualizarFecPago(listGrupos.get(i).getCveControl(), fecHoy);
										
									}else{
										
										pagosEnSAPDao.insertaBitacoraPagos(respuestaSAP[0]);
										pagosEnSAPDao.actualizaPagosCruzados(listGrupos.get(i));
										respuestas.append(" no fue compensada.<br />");
										respuestas.append("(SAP) ");
										respuestas.append(respuestaSAP[0].getMensaje() != null &&
												!respuestaSAP[0].getMensaje().equals("") ? 
														respuestaSAP[0].getMensaje() :
														"No se obtuvo respuesta de SAP.");
										respuestas.append("<br /><br />");
										
										respuestaExportar.put("MENSAJE",respuestaSAP[0].getMensaje() != null &&
												!respuestaSAP[0].getMensaje().equals("") ? 
														respuestaSAP[0].getMensaje() :
														"No se obtuvo respuesta de SAP.");
										respuestaExportar.put("ESTATUS","No se compenso");
										respuestaExportar.put("ORIGEN","SAP");
										respuestaExportar.put("DOCUMENTO",listGrupos.get(i).getBGenContable());
										
										if (respuestaSAP[0].getMensaje() != null &&
												!respuestaSAP[0].getMensaje().equals("")) {
											excelCompleto.append(respuestaSAP[0].getMensaje().replace("\"ORIGEN\": \"SAP\"",
													"\"ORIGEN\": \"SAP\", \"ESTATUS\": \"No se compenso\", \"DOCUMENTO\": \""
													+ (listGrupos.get(i).getBGenContable().length() > 54 ?
															listGrupos.get(i).getBGenContable():
																listGrupos.get(i).getBGenContable())+"\""));
										} else {
											excelCompleto.append("{\"TIPO\": \"E\", "
													+ "\"MENSAJE\": \""
													+ "No se obtuvo respuesta de SAP."
													+ "\", "
													+ "\"ORIGEN\": \"SET\", \"ESTATUS\": \"No se compenso\", \"DOCUMENTO\": \""
													+ (listGrupos.get(i).getBGenContable().length() > 54 ?
															listGrupos.get(i).getBGenContable():
																listGrupos.get(i).getBGenContable())+"\"},");
										}
										
									}
								} else {
									respuestas.append(" no fue compensada.<br />");
									respuestas.append("(SAP) No se obtuvo respuesta de SAP.");
									respuestas.append("<br /><br />");
									
									respuestaExportar.put("MENSAJE","No se obtuvo respuesta de SAP.");
									respuestaExportar.put("ESTATUS","No se compenso");
									respuestaExportar.put("ORIGEN","SAP");
									respuestaExportar.put("DOCUMENTO",listGrupos.get(i).getBGenContable());
									
									excelCompleto.append("{\"TIPO\": \"E\", "
											+ "\"MENSAJE\": \""
											+ "No se obtuvo respuesta de SAP."
											+ "\", "
											+ "\"ORIGEN\": \"SET\", \"ESTATUS\": \"No se compenso\", \"DOCUMENTO\": \""
													+ (listGrupos.get(i).getBGenContable().length() > 54 ?
															listGrupos.get(i).getBGenContable():
																listGrupos.get(i).getBGenContable())+"\"},");
								}
							} else {
								respuestas.append(" no fue compensada.<br />");
								respuestas.append("(SAP) ");
								respuestas.append(mapaSAP.get("mensaje"));
								respuestas.append("<br /><br />");
								
								respuestaExportar.put("MENSAJE",""+mapaSAP.get("mensaje"));
								respuestaExportar.put("ESTATUS","No se compenso");
								respuestaExportar.put("ORIGEN","SAP");
								respuestaExportar.put("DOCUMENTO",listGrupos.get(i).getBGenContable());
								
								excelCompleto.append("{\"TIPO\": \"E\", "
										+ "\"MENSAJE\": \""
										+ mapaSAP.get("mensaje").toString()
										+ "\", "
										+ "\"ORIGEN\": \"SET\", \"ESTATUS\": \"No se compenso\", \"DOCUMENTO\": \""
													+ (listGrupos.get(i).getBGenContable().length() > 54 ?
															listGrupos.get(i).getBGenContable():
																listGrupos.get(i).getBGenContable())+"\"},");
							}
						} else {
							respuestas.append(" no fue compensada.<br />");
							respuestas.append("(SET) Falta configurar cuentas contables.");
							respuestas.append("<br /><br />");
							
							respuestaExportar.put("MENSAJE","Falta configurar cuentas contables.");
							respuestaExportar.put("ESTATUS","No se compenso");
							respuestaExportar.put("ORIGEN","SET");
							respuestaExportar.put("DOCUMENTO",listGrupos.get(i).getBGenContable());
							
							excelCompleto.append("{\"TIPO\": \"E\", "
									+ "\"MENSAJE\": \""
									+ "Falta configurar cuentas contables."
									+ "\", "
									+ "\"ORIGEN\": \"SET\", \"ESTATUS\": \"No se compenso\", \"DOCUMENTO\": \""
													+ (listGrupos.get(i).getBGenContable().length() > 54 ?
															listGrupos.get(i).getBGenContable():
																listGrupos.get(i).getBGenContable())+"\"},");
						}
						String json = 
								excelCompleto.toString().replace("\"TIPO\":", "\"CLAVE_CONTROL\": \""
										+ listGrupos.get(i).getCveControl()
										+ "\", \"TIPO\":");
								mensajesParaExcel.append(json);
						listarespuestaExportar.add(respuestaExportar);
					}//for
					String cadenaExcel = mensajesParaExcel.toString().substring(0, mensajesParaExcel.toString().length()-1) + "]";
					if(Utilerias.parseaJsonExcel(cadenaExcel)){
						Gson gson = new Gson();
						System.out.println(mensajesParaExcel.toString().substring(0, mensajesParaExcel.toString().length()-1) + "]");
						listarespuestaExportar = gson.fromJson(cadenaExcel, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
					}
					
					mapRetorno.put("estatus", true);
					mapRetorno.put("salida", respuestas.toString());
					mapRetorno.put("excel", generarExcelMensajes(listarespuestaExportar));
					
				} else {
					mapRetorno.put("msgError", "(SET) Los datos no se pudieron agrupar.");
				}
			}else{
				mapRetorno.put("msgError", "(SET) Los datos son incorrectos.");
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagoPropuestasBusiness, M:insertarFoliosZexpFact");
			e.printStackTrace();
		}
		return mapRetorno;
	}
	
	private Map<String, Object> ejecutarPagador(MovimientoDto movimientoDto) {
		Map<String, Object>mapRetorno = new HashMap<String, Object>();
		mapRetorno.put("mensaje", "Error desconocido.");
		mapRetorno.put("estatus", false);
		String sCadenaPagador="";
		String sBandera="";
		String sChequera="";
		String sFolios="";
		String psMensaje="";
		int movto3=-1;//se inicializa asi para despues asegurarnos que entro al update*in
		int iGrupoNoMovs=0;
		int iBanco=0;
		int iFPago=0;
		
		StoreParamsComunDto dtoPagador= new StoreParamsComunDto();
		Map<String, Object> lResp= new HashMap<String, Object>();
		Map<String, Integer> mapPagFact = new HashMap<String, Integer>();		
		
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			dtoPagador= new StoreParamsComunDto();
            iGrupoNoMovs = movimientoDto.getNoMovimientos();		            
            
            iFPago = movimientoDto.getIdFormaPago();
            
            //Valor a bandera para agrupar...
            if(iFPago==1) {
            	sBandera =  "A";
            } else {
            	sBandera = iGrupoNoMovs==1 ? "N" : iGrupoNoMovs > 1 ? "A":"N";
            }
                       
            iBanco = movimientoDto.getIdBancoPago();
            sChequera = movimientoDto.getIdChequeraPago();
            sFolios = movimientoDto.getPsFolios();			            
            
            if((iBanco==0 || sChequera.equals(""))
        		&& (!movimientoDto.getCveControl().trim().substring(0,2).equals("MF"))) {
            	psMensaje = "Falta chequera pagadora para la empresa = " +movimientoDto.getNoEmpresa() +
                " y forma de pago " + movimientoDto.getIdFormaPago();
            }else{
            	if(pagosEnSAPDao.consultarConfiguraSet(1).equals("CCM")) {
            		sCadenaPagador="";
            		//sCadenaPagador="'"+sBandera+",0,"+Integer.parseInt(globalSingleton.getUsuarioLoginDto().getIdUsuario());
            		sCadenaPagador="'"+sBandera+",0,"+globalSingleton.getUsuarioLoginDto().getIdUsuario()+ "," 
            		+","+iBanco+","+sChequera+","+sFolios+"'";
            		pagosEnSAPDao.insertarDato(movimientoDto.getNoEmpresa(), sCadenaPagador);                   
            	}else{
            		if(pagosEnSAPDao.consultarConfiguraSet(286).equals("SI")
            				&& movimientoDto.getIdFormaPago()==1) {
            			ParamMovto3000Dto dtoMov3= new ParamMovto3000Dto();
            			dtoMov3.setNoEmpresa(movimientoDto.getNoEmpresa());
            			dtoMov3.setIdBanco(iBanco);
            			dtoMov3.setSChequera(sChequera);
            			dtoMov3.setSFolios(sFolios);
            			dtoMov3.setIdUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
            			//dtoMov3.setIdCaja(Integer.parseInt(globalSingleton.obtenerPropiedadUsuario("idCaja").toString()));//Agregar no_caja
            			dtoMov3.setIdCaja(globalSingleton.getUsuarioLoginDto().getIdCaja());//Agregar no_caja
            			movto3=pagosEnSAPDao.actualizarMovto3000(dtoMov3);//*in
            			
            		}else if(movimientoDto.getCveControl().substring(0,2).equals("MF")){
            			mapPagFact=llamarPagadorFactoraje((globalSingleton.getUsuarioLoginDto().getIdUsuario())
            					,sFolios,0,"",movimientoDto.getNoEmpresa());
            		}else{
            			dtoPagador.setParametro(sBandera + "," + 0 + "," 
            					//+ Integer.parseInt(globalSingleton.obtenerPropiedadUsuario("idUsuario").toString()) + ","
            					+ (globalSingleton.getUsuarioLoginDto().getIdUsuario()) + ","
            					+ iBanco + "," + sChequera + ","+ sFolios + "," + "#");
            			//System.out.println();
            			dtoPagador.setMensaje("iniciar pagador");
            			dtoPagador.setResult(0);
            			System.out.println("cadena delol pagador ----> "+dtoPagador.getParametro());
            			
            			lResp=pagosEnSAPDao.ejecutarPagador(dtoPagador);
            			
            		}
            	}
            	//{result=0, mensaje=null}
            	if((!pagosEnSAPDao.consultarConfiguraSet(286).equals("SI"))
            			|| (pagosEnSAPDao.consultarConfiguraSet(286).equals("SI") && movimientoDto.getIdFormaPago()!=1))
            	{
            		if((!lResp.isEmpty() && Integer.parseInt(lResp.get("result").toString())==0) 
	            			|| (!mapPagFact.isEmpty() && mapPagFact.get("pagadorFactoraje")!=null && mapPagFact.get("pagadorFactoraje")==0) 
	            			|| movto3==0){
            			pagosEnSAPDao.actualizarCveControl(movimientoDto.getNoEmpresa(), movimientoDto.getPsFolios(), //sFolios
            					movimientoDto.getCveControl(), movimientoDto.getFecPropuesta());
            			
            			pagosEnSAPDao.actualizaEjecucionOperacionesDivisas(globalSingleton.getUsuarioLoginDto().getIdUsuario(), sFolios);
            			
            			
            		}
            		//exito
            	}
            	
            	if((!lResp.isEmpty() && Integer.parseInt(lResp.get("result").toString())!=0) 
            			|| (!mapPagFact.isEmpty() && mapPagFact.get("iFoliosRech")!=null && mapPagFact.get("iFoliosRech")!=0) 
            			|| movto3>0)
            	{
            		if(mapPagFact.get("error")!=null && mapPagFact.get("error")!=0)
            		{
            			psMensaje = "� Error en generador al llamar pagador Factoraje #"+mapPagFact.get("error")+":";
            			
            		}
        			else if (movto3>0)
        			{
            			psMensaje = "� Error en  Movto3000 #"+movto3+":";
        			}
            		else if (!lResp.isEmpty() && Integer.parseInt(lResp.get("result").toString())!=0) 
            		{
            			psMensaje = "� Error en pagador #"+lResp.get("result")+":";
            			
            		}
            	}
            	
			}
            mapRetorno.put("estatus", psMensaje.equals(""));
            mapRetorno.put("mensaje", psMensaje);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagoPropuestasBusiness, M:ejecutarPagador");
			e.printStackTrace();
		}
		return mapRetorno;
	}

	
	private String generarExcelMensajes(List<Map<String, String>> datos){
		String nombre = "";
		HSSFWorkbook hb=null;
		try {
			hb=Utilerias.generarExcel(new String[]{
					"CLAVE_CONTROL",
					"MENSAJE",
					"ESTATUS",
					"ORIGEN",
					"DOCUMENTO"
			},datos, new String[]{
					"CLAVE_CONTROL",
					"MENSAJE",
					"ESTATUS",
					"ORIGEN",
					"DOCUMENTO"
			});
			nombre = ConstantesSet.RUTA_EXCEL + "mensajes " + Utilerias.indicadorFecha() +".xls";
            File outputFile = new File(nombre);
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			hb.write(outs);
			outs.close();   
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +
					"P: Personas, C: ConsultaPersonasBusinessImpl, M: generarExcelMensajes");
		}
		return nombre;
	}
	
	private List<ComunEgresosDto> convierteJsonToListComunEgresosDto(String datos) {
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<ComunEgresosDto>listGrid= new ArrayList<ComunEgresosDto>();
		try{
			for (Map<String, String> comunEgresosDto : objParams) {
				ComunEgresosDto dtoGrid= new ComunEgresosDto(); 
				dtoGrid.setAgrupaEmpChe(
						comunEgresosDto.get("agrupaCheEmp")!=null ? 
								Integer.parseInt(comunEgresosDto.get("agrupaCheEmp")) : 
								0);
				dtoGrid.setCveControl(comunEgresosDto.get("cveControl"));
				dtoGrid.setFechaPago(funciones.ponerFechaDate(comunEgresosDto.get("fecPropuesta")));
				dtoGrid.setIdGrupoEmpresas(comunEgresosDto.get("idGrupoEmpresa")!=null ? Integer.parseInt(comunEgresosDto.get("idGrupoEmpresa")) : 0);
				dtoGrid.setIdGrupoRubros(comunEgresosDto.get("idGrupoRubro")!=null ? Integer.parseInt(comunEgresosDto.get("idGrupoRubro")) : 0);				
				dtoGrid.setPsAgrupaCheques(comunEgresosDto.get("agrupaCheques") != null ? comunEgresosDto.get("agrupaCheques"): "" );
				dtoGrid.setPsAgrupaTransfers(comunEgresosDto.get("agrupaTransfers")!= null ? comunEgresosDto.get("agrupaTransfers") :"");				
				listGrid.add(dtoGrid);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:PagoPropuestasAction, M:convierteJsonToListComunEgresosDto");
		}
		return listGrid;
	}
	
	private Map<String, Object> pagarEnSAP(DT_Pagos_OBPagos pagoDto) {
		Map<String, Object>mapRetorno = new HashMap<String, Object>();
		mapRetorno.put("mensaje", "No se pudo acceder a SAP.");
		mapRetorno.put("estatus", false);
		try {
			SOS_PagosServiceLocator service = new SOS_PagosServiceLocator();
			SOS_PagosBindingStub inst = new SOS_PagosBindingStub(new URL(service.getHTTP_PortAddress()), service);
			inst.setUsername(pagosEnSAPDao.consultarConfiguraSet(ConstantesSet.USERNAME_WS_PAGOS));
			inst.setPassword(pagosEnSAPDao.consultarConfiguraSet(ConstantesSet.PASSWORD_WS_PAGOS));
			if (!pagosEnSAPDao.consultarConfiguraSet(4000).equals("local")) {
				DT_Pagos_ResponsePagosResponse[] pagosRespuesta = inst.SOS_Pagos(new DT_Pagos_OBPagos []{pagoDto});
				mapRetorno.put("pagosRespuesta", pagosRespuesta);
			}
			mapRetorno.put("estatus", true);
		} catch ( MalformedURLException e1) {
			mapRetorno.put("mensaje", "Error al conectarse ha SAP.");
			e1.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + e1.toString() +
					"P:Egresos, C:PagoPropuestasBusiness, M:pagarEnSAP");
		} catch (AxisFault e1) {
			mapRetorno.put("mensaje", "Error al procesar los datos dentro de SAP.");
			e1.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + e1.toString() + 
					"P:Egresos, C:PagoPropuestasBusiness, M:pagarEnSAP");			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagoPropuestasBusiness, M:pagarEnSAP");
			e.printStackTrace();
		}
		return mapRetorno;
	}
	
	public Map<String,Integer> llamarPagadorFactoraje(int noUsuario, String sFolios, int iFoliosRech, String psMensaje, int noEmpresa){
		int afecFactoraje=0;
		int plFolio=0;
		int plFolio2=0;
		Map<String,Object>  resp= new HashMap<String,Object>();
		Map<String,Integer>  retorno= new HashMap<String,Integer>(); 
		int pdFolioMov=0;
		int pdFolioDet=0;
	 
		ParametroFactorajeDto dtoFactor= new ParametroFactorajeDto();
		try{
		    plFolio = pagosEnSAPDao.seleccionarFolioReal("no_folio_param");
		    plFolio2 = pagosEnSAPDao.seleccionarFolioReal("no_folio_param");
		    dtoFactor.setNoUsuario(noUsuario);
		    dtoFactor.setSFolios(sFolios);
		    dtoFactor.setPlFolio(plFolio);
		    dtoFactor.setPlFolio2(plFolio2);
		    dtoFactor.setFechaHoy(pagosEnSAPDao.obtenerFechaHoy());
		    
		    afecFactoraje=pagosEnSAPDao.insertarParametroFactoraje(dtoFactor);
		    if(afecFactoraje>0)
		    {
		    	resp = pagosEnSAPDao.ejecutarGenerador(noEmpresa, plFolio, pdFolioMov, pdFolioDet, 1, "inicia generador");
		    	
		    	if(Integer.parseInt(resp.get("result").toString())!=0)
		    	{
		    		if(Integer.parseInt(resp.get("result").toString())!=1053)
		    		{
		    			retorno.put("iFoliosRech", 1);
		    			retorno.put("pagadorFactoraje", 1);
		    			retorno.put("error", Integer.parseInt(resp.get("result").toString()));
		    			return retorno;
		    		}
		    	}
		    	retorno.put("iFoliosRech", 0);
		    }else{
		    	retorno.put("iFoliosRech", 1);
		    }
		    retorno.put("pagadorFactoraje", 0);
		}catch(Exception e){
			retorno.put("pagadorFactoraje", 99);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagoPropuestasBusiness, M:llamarPagadorFactoraje");
		}
		return retorno;
	}

	public List<ComunEgresosDto> getListaCvesControlDePropuesta( String registros ){
		
		Gson gson = new Gson();
		List< Map< String, String> > registrosList = gson.fromJson(registros, new TypeToken<ArrayList<Map<String,String>>>() {}.getType());
		Map<String, String> nuevoMapa = new HashMap<String,String>();
		Map<String, String> nuevoMapaCvesFecha = new HashMap<String,String>();
		
		List<ComunEgresosDto> listaPropuestas = new ArrayList<ComunEgresosDto>();
		
		try {
			
			for(Map< String, String> registro: registrosList ){
				
				nuevoMapa         .put(registro.get("cveControl"), registro.get("cveControl"));	
				
				nuevoMapaCvesFecha.put(registro.get("cveControl"), registro.get("fecPropuesta") != null ? registro.get("fecPropuesta"):null);
				
			}	
			
			Set<String> nuevaLista = nuevoMapa.keySet();			
			
			for( String cveControl : nuevaLista){
				
				ComunEgresosDto ced = new ComunEgresosDto();
				
				ced.setCveControl(cveControl);				
				ced.setFechaPago( nuevoMapaCvesFecha.get(cveControl) != null ?funciones.ponerFechaDate(nuevoMapaCvesFecha.get(cveControl)): null );
				
				listaPropuestas.add( ced ) ;
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Egresos, C:PagosEnSAPBussines, M:getListaCvesControlDePropuesta");
		}
		return listaPropuestas;
	}
	
	public String getListaFoliosDetDePropuestas( String registros ){
		Gson gson = new Gson();
		List< Map< String, String> > registrosList = gson.fromJson(registros, new TypeToken<ArrayList<Map<String,String>>>() {}.getType());
		StringBuilder sb = new StringBuilder();
		String folios = null;
		try {
			for(Map< String, String> registro: registrosList ){
				
				 sb.append( registro.get("folio") ).append(",");		
			}	
			
			folios = sb.toString();
			folios = folios.substring(0, folios.length()-1);
			
			return folios;
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Egresos, C:PagosEnSAPBussines, M:getListaFoliosDetDePropuestas");
			return "";
		}
		
	}



	public PagosEnSAPDao getPagosEnSAPDao() {
		return pagosEnSAPDao;
	}

	public void setPagosEnSAPDao(PagosEnSAPDao pagosEnSAPDao) {
		this.pagosEnSAPDao = pagosEnSAPDao;
	}
}
