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

import org.apache.axis.AxisFault;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.egresos.dao.CompensarPropuestasDao;
import com.webset.set.egresos.dao.impl.ConsultasGeneralesEgresosDao;
import com.webset.set.egresos.dto.BloqueoPagoCruzadoDto;
import com.webset.set.egresos.dto.PagosPropuestosDto;
import com.webset.set.egresos.service.CompensarPropuestasService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.ComunEgresosDto;
import com.webset.set.utilerias.dto.GrupoEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.SaldosChequerasDto;
import com.webset.set.utilerias.dto.SeleccionAutomaticaGrupoDto;
import com.webset.set.utilerias.dto.StoreParamsComunDto;
import com.webset.utils.tools.Utilerias;

import mx.com.gruposalinas.Pagos.DT_Pagos_OBPagos;
import mx.com.gruposalinas.Pagos.DT_Pagos_ResponsePagosResponse;
import mx.com.gruposalinas.Pagos.SOS_PagosBindingStub;
import mx.com.gruposalinas.Pagos.SOS_PagosServiceLocator;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

public class CompensarPropuestasBusinessImpl implements CompensarPropuestasService {
	private CompensarPropuestasDao compensarPropuestasDao;
	private ConsultasGeneralesEgresosDao consultasGeneralesEgresosDao;
	private Funciones funciones= new Funciones();
	private Bitacora bitacora = new Bitacora(); 
	//private GlobalSingleton globalSingleton;
	private String ubicacion = "";//esta variable se queda fija como "" despues se subir� a session
	private GlobalSingleton globalSingleton;
	private PagosEnSAPBusiness pagosEnSAPBusiness;
	public PagosEnSAPBusiness getPagosEnSAPBusiness() {
		return pagosEnSAPBusiness;
	}



	public void setPagosEnSAPBusiness(PagosEnSAPBusiness pagosEnSAPBusiness) {
		this.pagosEnSAPBusiness = pagosEnSAPBusiness;
	}



	@Override
	public List<LlenaComboGralDto> llenarComboGrupoEmpresa(GrupoEmpresasDto dto) {
		return compensarPropuestasDao.llenarComboGrupoEmpresa(dto);
	}
	
	
	
	public ConsultasGeneralesEgresosDao getConsultasGeneralesEgresosDao() {
		return consultasGeneralesEgresosDao;
	}



	public void setConsultasGeneralesEgresosDao(ConsultasGeneralesEgresosDao consultasGeneralesEgresosDao) {
		this.consultasGeneralesEgresosDao = consultasGeneralesEgresosDao;
	}



	@Override
	public List<LlenaComboGralDto> llenarComboDivXEmp(int IdUsuario) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public SaldosChequerasDto obtenerSaldosChequeras(SaldosChequerasDto dto){
		double saldoInicialMN = 0;
		double cargosMN = 0;
		double abonosMN = 0;
		double saldoFinalMN = 0;
	    
		double saldoInicialDLS = 0;
		double cargosDLS = 0;
		double abonosDLS = 0;
		double saldoFinalDLS = 0;
		
		SaldosChequerasDto dtoReturn= new SaldosChequerasDto();
		List<SaldosChequerasDto> listSalCheq= new ArrayList<SaldosChequerasDto>();
		try{
			dto.setFecha(compensarPropuestasDao.obtenerFechaHoy());
			listSalCheq=compensarPropuestasDao.obtenerSaldosChequeras(dto);
			
			if(listSalCheq.size() > 0)
			{
				for(int i=0; i<listSalCheq.size(); i++){
					if(listSalCheq.get(i).getIdDivisa() != null && !listSalCheq.get(i).getIdDivisa().equals("") 
							&& listSalCheq.get(i).getIdDivisa().equals(compensarPropuestasDao.consultarConfiguraSet(771)))
					{
		                saldoInicialMN = saldoInicialMN + (listSalCheq.get(i).getSaldoInicial()!= 0 ? listSalCheq.get(i).getSaldoInicial() : 0);
		                cargosMN = cargosMN + (listSalCheq.get(i).getCargo()!=0?listSalCheq.get(i).getCargo():0);
		                abonosMN = abonosMN + (listSalCheq.get(i).getAbono()!=0?listSalCheq.get(i).getAbono():0);
		                saldoFinalMN = saldoFinalMN + (listSalCheq.get(i).getSaldoFinal() != 0 ? listSalCheq.get(i).getSaldoFinal() : 0);
					}
	                else if (listSalCheq.get(i).getIdDivisa() != null && !listSalCheq.get(i).getIdDivisa().equals("") 
							&& listSalCheq.get(i).getIdDivisa().equals(compensarPropuestasDao.consultarConfiguraSet(772)))
	                {
	                	saldoInicialDLS = saldoInicialDLS + (listSalCheq.get(i).getSaldoInicial()!=0?listSalCheq.get(i).getSaldoInicial():0);
		                cargosDLS = cargosDLS + (listSalCheq.get(i).getCargo() != 0 ? listSalCheq.get(i).getCargo() : 0);
		                abonosDLS = abonosDLS + (listSalCheq.get(i).getAbono() != 0 ? listSalCheq.get(i).getAbono() : 0);
		                saldoFinalDLS = saldoFinalDLS + (listSalCheq.get(i).getSaldoFinal() != 0 ?listSalCheq.get(i).getSaldoFinal() : 0);
	                }
				}
			}
			dtoReturn.setSaldoInicialMn(saldoInicialMN);
			dtoReturn.setCargoMn(cargosMN);
			dtoReturn.setAbonoMn(abonosMN);
			dtoReturn.setSaldoFinalMn(saldoFinalMN);
			dtoReturn.setInversionesMn(0);
			
			dtoReturn.setSaldoInicialDls(saldoInicialDLS);
			dtoReturn.setCargoDls(cargosDLS);
			dtoReturn.setAbonoDls(abonosDLS);
			dtoReturn.setSaldoFinalDls(saldoFinalDLS);
			dtoReturn.setInversionesDls(0);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagoPropuestasBusiness, M:obtenerSaldosChequeras");
		}
		return dtoReturn;
	}
	
	public List<MovimientoDto>consultarDetallePropuestasNoPagadas(SeleccionAutomaticaGrupoDto dtoIn){
		PagosPropuestosDto consDetalleDto= new PagosPropuestosDto();
		List<MovimientoDto> listDetalle= new ArrayList<MovimientoDto>();
		
		try{ 
			if(dtoIn.getCveControl().trim().substring(0,1).equals("M")){
				consDetalleDto.setIdGrupoEmpresa(dtoIn.getIdGrupoFlujo());
				consDetalleDto.setIdGrupo(dtoIn.getIdGrupo());//Checar
				consDetalleDto.setCveControl(dtoIn.getCveControl());
				consDetalleDto.setFecha(compensarPropuestasDao.consultarFechaHoy());//Checar
				consDetalleDto.setPsDivision("");
				listDetalle=compensarPropuestasDao.consultarDetallePropuestasNoPagadas(consDetalleDto);
			}
						
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:consultarDetalle");
		}
		return listDetalle;
	}

	
	public List<MovimientoDto>consultarDetalle(SeleccionAutomaticaGrupoDto dtoIn){
		PagosPropuestosDto consDetalleDto= new PagosPropuestosDto();
		List<MovimientoDto> listDetalle= new ArrayList<MovimientoDto>();
		
		try{ 
			if(dtoIn.getCveControl().trim().substring(0,1).equals("M")){
				consDetalleDto.setIdGrupoEmpresa(dtoIn.getIdGrupoFlujo());
				consDetalleDto.setIdGrupo(dtoIn.getIdGrupo());//Checar
				consDetalleDto.setCveControl(dtoIn.getCveControl());
				consDetalleDto.setFecha(compensarPropuestasDao.consultarFechaHoy());//Checar
				consDetalleDto.setPsDivision("");
				listDetalle=compensarPropuestasDao.consultarDetalle(consDetalleDto);
			}
			
		
			int count = 0;
			List<BloqueoPagoCruzadoDto> listBloqueados = new ArrayList<>();

			if(listDetalle.size() > 0) {
				for(int i=0; i<listDetalle.size(); i++) {
					
					//Checa si ya trae el color desde la consulta, hasta el momento solo no_cobrador.
					if(listDetalle.get(i).getColor() != null && listDetalle.get(i).getColor().equals("NARANJA")){
						listDetalle.get(i).setColor("color:#FDA920"); //Naranja
						continue;
					}
					
					//Verifica que no este bloqueado el proveedor
					listBloqueados = compensarPropuestasDao.obtenerBloqueoProveedor(
													""+listDetalle.get(i).getNoEmpresa(),
													""+listDetalle.get(i).getEquivale(),
													dtoIn.getCveControl());
					
					if(listBloqueados.size()>0){
						
						//Hace corrimiento, si hay alg�n registro sin documento, es bloqueo proveedor, se PINTA el registro.
						//Si no, es bloqueo de documento, se compara con el documento de la propuesta, si no son iguales, no se pinta azul.
						for(int j=0; j<listBloqueados.size(); j++){
							if(listBloqueados.get(j).getNoDocumento() == null || listBloqueados.get(j).getNoDocumento().equals("")){
								listDetalle.get(i).setColor("color:#090DFA"); //Azul
								break;
							}else if(listBloqueados.get(j).getNoDocumento() != null && listBloqueados.get(j).getNoDocumento().equals(listDetalle.get(i).getNoDocto())){
								listDetalle.get(i).setColor("color:#090DFA");
								break;
							}
						}
						
						
					}else{
						//Si hay bloqueo de SAP
						listBloqueados = compensarPropuestasDao.obtenerBloqueoSAP(
								""+dtoIn.getCveControl(),
								""+listDetalle.get(i).getNoDocto());
						
						if(listBloqueados.size()>0){
							listDetalle.get(i).setColor("color:#090DFA"); //Azul
						}
						
					}
					
					//Valida que existan las chequeras benef
					count = compensarPropuestasDao.existeChequeraBenef(listDetalle.get(i));
					//Si no existe se pinta de color
					if(count == 0){
						listDetalle.get(i).setColor("color:#7030A0"); //morado
					}else{
						listDetalle.get(i).setColor("color:#000000");
					}
					
					
					
				}
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:consultarDetalle");
		}
		return listDetalle;
	}
	

	
	@Override
	public List<MovimientoDto> consultarPropuestas(PagosPropuestosDto dtoIn) {
		List<MovimientoDto>listCons = new ArrayList<MovimientoDto>();
		
		try {
			dtoIn.setFechaHoy(compensarPropuestasDao.obtenerFechaHoy());
			//System.out.print(compensarPropuestasDao.obtenerFechaHoy());
	        dtoIn.setAutorizaPropuesta(compensarPropuestasDao.consultarConfiguraSet(240));
	        dtoIn.setBSoloCheques(false);//valor fijado para pruebas
	        dtoIn.setBTambienIntercos(false);//este valor es fijo segun la llamada en VB
	        dtoIn.setCveControl("");
	        
	        listCons = compensarPropuestasDao.consultarPagProp(dtoIn);
	        
	        if (listCons.size()>0) {
				for (int i = 0; i < listCons.size(); i++) {
					if( (listCons.get(i).getColor() != null && listCons.get(i).getColor().equals("AZUL")) ||
							(listCons.get(i).getIdChequeraCte() != null && listCons.get(i).getIdChequeraCte().equals("ROJO"))){
						if (listCons.get(i).getIdChequeraCte().equals("ROJO")) {
							listCons.get(i).setColor("color:#FF4500"); //Azul
						} else if(listCons.get(i).getColor().equals("AZUL") ){
							listCons.get(i).setColor("color:#090DFA"); //Azul
						}
					
					}
				}
				
			}
	        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Egresos, C:PagoPropuestasBusiness, M:consultarPropuestas");
		}
		return listCons;
	}

	@Override
	public Map<String, Object> ejecutarPropuestas(List<ComunEgresosDto> listGrid, String fecHoy){
		Map<String, Object> mapRetorno= new HashMap<String, Object>();
		return mapRetorno;

	}


	public void acutalizarConfContablePag(String cveControl) {
		List<MovimientoDto> listDatPag = new ArrayList<MovimientoDto>();
		List<MovimientoDto> listFolioDetPag = new ArrayList<MovimientoDto>();
		
		try {
			listDatPag = compensarPropuestasDao.buscaDatosConfigContable(cveControl);
    		
    		if(listDatPag.size() > 0) {
    			for(int i=0; i<listDatPag.size(); i++) {
    				listFolioDetPag = compensarPropuestasDao.buscaFolioDatosPagados(cveControl, listDatPag, i);
    				
    				if(listFolioDetPag.size() > 0) {
    					compensarPropuestasDao.actFolioPagConfigCont(listFolioDetPag.get(0).getNoFolioDet(), 
    															listDatPag.get(i).getNoFolioDet());
    				}
    			}
    		}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Egresos, C:PagoPropuestasBusiness, M:acutalizarConfContablePag");
					e.printStackTrace();
		}
	}

	@Override
	public List<ComunEgresosDto> validarCpaVtaTransfer(List<ComunEgresosDto> listGrid){
		List<MovimientoDto>listPagCpaVtaTran= new ArrayList<MovimientoDto>();
		List<MovimientoDto>listPagCruzadosAut= new ArrayList<MovimientoDto>();
		ComunEgresosDto dtoParam = new  ComunEgresosDto();
		StringBuffer documentos = new StringBuffer();
		try{
			if(listGrid.size()>0)
			{
				for(int i=0; i<listGrid.size();i++)
				{
					if(listGrid.get(i).getCveControl()!=null && !listGrid.get(i).getCveControl().equals("") 
							&& listGrid.get(i).getCveControl().trim().substring(0,1).equals("M"))
					{
						listPagCpaVtaTran=new ArrayList<MovimientoDto>();
						listPagCpaVtaTran=compensarPropuestasDao.consultarPagosCpaVtaTransferPropManual(listGrid.get(i).getCveControl());
						if(listPagCpaVtaTran.size()>0)
						{
							for(int in=0;in<listPagCpaVtaTran.size();in++)
							{
								if((listPagCpaVtaTran.get(in).getIdFormaPago()==3 || listPagCpaVtaTran.get(in).getIdFormaPago()==5)
										&& ((listPagCpaVtaTran.get(in).getNumCheqCte()>=0 && listPagCpaVtaTran.get(in).getNumCheqEmp()==0)
												||(listPagCpaVtaTran.get(in).getBcoPagoCruzado()>0))){
									//listGrid.get(i).setMensaje("Para el Docto. "+listPagCpaVtaTran.get(in).getNoDocto()+
		                              //      " se realizara un pago cruzado de Transferencias.");
									documentos.append("Para el Docto. "+listPagCpaVtaTran.get(in).getNoDocto()+
		                                    " se realizara un pago cruzado de Transferencias. ");
								
								}else if((listPagCpaVtaTran.get(in).getIdFormaPago()==3 || listPagCpaVtaTran.get(in).getIdFormaPago()==5)
										&& ((listPagCpaVtaTran.get(in).getNumCheqCte()>=0 && listPagCpaVtaTran.get(in).getNumCheqEmp()==0)
												//||(listPagCpaVtaTran.get(in).getBcoPagoCruzado()>0)
										|| (listPagCpaVtaTran.get(in).getIdServicioBe()!=null && listPagCpaVtaTran.get(in).getIdServicioBe().equals("CVT"))))
									
			                            listGrid.get(i).setMensaje("Para el Docto. "+listPagCpaVtaTran.get(in).getNoDocto()+
			                                    " se hara una Compra-Venta de Transfer");
							}
						}
						
					}else if(listGrid.get(i).getCveControl()!=null && !listGrid.get(i).getCveControl().equals("") 
							&& !listGrid.get(i).getCveControl().trim().substring(0,1).equals("M")
							&& !listGrid.get(i).getCveControl().trim().substring(0,2).equals("MF")) {
						//' Movimientos de Compra Venta de Transfer
						
						 //If cmbDivision.ListIndex = -1 Then
						dtoParam= new ComunEgresosDto();
						dtoParam.setFechaHoy(compensarPropuestasDao.obtenerFechaHoy());
						dtoParam.setCveControl(listGrid.get(i).getCveControl());
						dtoParam.setPsDivision("");
						dtoParam.setCompraVtaTransfer(true);
		                /*Else
		                    Set rstDatos = gobjSQL.funSQLSelectPagosCruzadosAut(GT_FECHA_HOY, _
		                                            msfDatos.TextMatrix(i, MI_CVE_CONTROL), _
		                                            msfDatos.TextMatrix(i, MI_GRUPO_EMPRESA), True)
		                End If*/
						listPagCruzadosAut= new ArrayList<MovimientoDto>();
						listPagCruzadosAut=compensarPropuestasDao.consultarPagosCruzadosAut(dtoParam);
						
						if(listPagCruzadosAut.size()>0)
						{
							for(int in=0;in<listPagCruzadosAut.size();in++)
							{
								if((listPagCruzadosAut.get(in).getIdFormaPago()==3 || listPagCruzadosAut.get(in).getIdFormaPago()==5)
									&& listPagCruzadosAut.get(in).getNumCheqCte()>0 && listPagCruzadosAut.get(in).getNumCheqEmp()==0)
								{
									 listGrid.get(i).setMensaje("Para el Docto. " +listPagCruzadosAut.get(in).getNoDocto()+ 
		                            " se hara una Compra-Venta de Transfer");
								}
							}
						}
						
		                //'Movimientos de Pago Cruzado Automatico
		                dtoParam= new ComunEgresosDto();
						dtoParam.setFechaHoy(compensarPropuestasDao.obtenerFechaHoy());
						dtoParam.setCveControl(listGrid.get(i).getCveControl());
						dtoParam.setPsDivision("");
						dtoParam.setCompraVtaTransfer(true);   
		             
						listPagCruzadosAut= new ArrayList<MovimientoDto>();
						listPagCruzadosAut=compensarPropuestasDao.consultarPagosCruzadosAut(dtoParam);
						
						if(listPagCruzadosAut.size()>0)
						{
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
									listGrid.get(i).setMensaje2("Para el Docto. "+listPagCruzadosAut.get(j).getNoDocto()+" se le cambiara la divisa de pago de "+ 
									listPagCruzadosAut.get(j).getIdDivisa()+ " a " +listPagCruzadosAut.get(j).getDivPagoCruzado()+
                                    " por la cantidad final de " + listPagCruzadosAut.get(j).getImpPagoCruzado());
                                    
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
									listGrid.get(i).setMensaje2("Para el Docto. "+listPagCruzadosAut.get(j).getNoDocto()+" se le cambiara la divisa de pago de "
                                    +listPagCruzadosAut.get(j).getIdDivisa()+" a MN " + " por la cantidad final de " + listPagCruzadosAut.get(j).getImpPagoCruzado());
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
									listGrid.get(i).setMensaje2("Para el Docto. " + listPagCruzadosAut.get(j).getNoDocto() + " se le cambiara la divisa de pago de " +
									listPagCruzadosAut.get(j).getIdDivisa() + " a MN " + " por la cantidad final de " + listPagCruzadosAut.get(j).getImpPagoCruzado());
								}
							}
						}
					}//else
				}
				listGrid.get(listGrid.size()-1).setMensaje(documentos.toString());
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagoPropuestasBusiness, M:validarCpaVtaTransfer");
			e.printStackTrace();
		}
		return listGrid;
	}
	
		
	public boolean verificarNuevo(int noEmpresa, int noProveedor, String idDivisa, int idFormaPago, 
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
						
					
		    		/////////////
			    		if(listGrupos.get(index-1).getIdFormaPago()==1)
			    		{
			    			if(psCveControl.trim().substring(1,2).equals("G"))
			    			{
			    				verificaNuevo = false;
			    			}else{
			    				//Valida si las chequeras se agrupan...
			    				if(compensarPropuestasDao.consultarConfiguraSet(220).equals("NO"))
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
	
	public Map<String,Object> agruparMovimientos(List<ComunEgresosDto> listGrid){
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
		//int piNumFolioMassActual = 0;
        //int piNumArregloMassActual = 1;
        String sIdChequeraBenef = "";
        int idxGrupo = -1;
       // ComunDto paramBanCheqBenef = new ComunDto();
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
						listPagProApagar=compensarPropuestasDao.consultarPagPropApagar(dtoParam);
					
					}else if(listGrid.get(i).getCveControl()!=null && !listGrid.get(i).getCveControl().equals("") 
							&& listGrid.get(i).getCveControl().trim().substring(0,1).equals("M")){
						String origenMovimiento = compensarPropuestasDao.obtieneOrigenMov(listGrid.get(i).getCveControl());
						if (origenMovimiento.equals("CAJ") || origenMovimiento.equals("CVD")) {
							//no entra para origen sap
							rstDatos = compensarPropuestasDao.consultarPagosCpaVtaTransferPropManual(listGrid.get(i).getCveControl());
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
							listMovto=compensarPropuestasDao.consultarPagosCpaVtaTransferPropManual(listGrid.get(i).getCveControl());
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
								lAfec=compensarPropuestasDao.actualizarEstatusComVtaTransfer(psFoliosWL, true);
							if(!psFolios.equals(""))
								lAfec=compensarPropuestasDao.actualizarEstatusComVtaTransfer(psFolios,false);
							if (!psFoliosCruzado.equals("")) 
								lAfec = compensarPropuestasDao.actualizaMovtoTC(globalSingleton.getUsuarioLoginDto().getIdUsuario(),
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
		                listPagProApagar= compensarPropuestasDao.consultarPagPropApagar(dtoParam);
		                //ok
					}else{//if dentro del for 1
						//' Movimientos de Compra Venta de Transfer
						List<MovimientoDto>listPagCruzadosAut= new ArrayList<MovimientoDto>();
						dtoParam= new ComunEgresosDto();
						dtoParam.setFechaHoy(compensarPropuestasDao.obtenerFechaHoy());
						dtoParam.setCveControl(listGrid.get(i).getCveControl());
						dtoParam.setPsDivision("");
						dtoParam.setCompraVtaTransfer(true);
		               
						listPagCruzadosAut=compensarPropuestasDao.consultarPagosCruzadosAut(dtoParam);
						
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
							lAfec=compensarPropuestasDao.actualizarEstatusComVtaTransfer(psFolios,false);
							
						psFolios = "";
						psDocumentosCompensar  = "";
		                psDoctosCruzarAut = "";
		                //' Movimientos de Pago Cruzado Automatico
		                dtoParam= new ComunEgresosDto();
						dtoParam.setFechaHoy(compensarPropuestasDao.obtenerFechaHoy());
						dtoParam.setCveControl(listGrid.get(i).getCveControl());
						dtoParam.setPsDivision("");
						dtoParam.setCompraVtaTransfer(true);   
						
						listPagCruzadosAut=compensarPropuestasDao.consultarPagosCruzadosAut(dtoParam);
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
												lAfec=compensarPropuestasDao.actualizarBancoCheqBenef(listPagCruzadosAut.get(ji).getNoFolioDet(),
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
													mapCuadrante=compensarPropuestasDao.cambiarCuadrantes(cambiaCuadranteDto);
			                
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
	                                            listBanCheqBenef = compensarPropuestasDao.consultarBancoCheqBenef(paramBanCheqBenef);
	                                            
	                                            if(listBanCheqBenef.size()>0){
		                                            	 iIdBancoBenef = listBanCheqBenef.get(0).getIdBanco();
		                                                 sIdChequeraBenef = listBanCheqBenef.get(0).getIdChequera();
	                                            }
	                                            paramBanCheqBenef=new ComunDto();
	                                            paramBanCheqBenef.setNoFolioDet(listPagCruzadosAut.get(ji).getNoFolioDet());
	                                            paramBanCheqBenef.setIdBanco(iIdBancoBenef);
	                                            paramBanCheqBenef.setIdChequera(sIdChequeraBenef);
	                                            lAfec = compensarPropuestasDao.actualizarBancoCheqBenef(paramBanCheqBenef);
	                                             
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
														mapCuadrante=compensarPropuestasDao.cambiarCuadrantes(cambiaCuadranteDto);
				                
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
	                                            listBanCheqBenef = compensarPropuestasDao.consultarBancoCheqBenef(paramBanCheqBenef);
	                                            
	                                            if(listBanCheqBenef.size()>0)
	                                            {
	                                            	iIdBancoBenef = listBanCheqBenef.get(0).getIdBanco();
		                                            sIdChequeraBenef = listBanCheqBenef.get(0).getIdChequera();
	                                            }
	                                            
	                                            paramBanCheqBenef=new ComunDto();
	                                            paramBanCheqBenef.setNoFolioDet(listPagCruzadosAut.get(ji).getNoFolioDet());
	                                            paramBanCheqBenef.setIdBanco(iIdBancoBenef);
	                                            paramBanCheqBenef.setIdChequera(sIdChequeraBenef);
	                                            lAfec = compensarPropuestasDao.actualizarBancoCheqBenef(paramBanCheqBenef);
	                                            
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
												mapCuadrante=compensarPropuestasDao.cambiarCuadrantes(cambiaCuadranteDto);
			                
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
						
						listPagosBenefDatosAlt= compensarPropuestasDao.consultarPagosBenefDatosAlt(dtoParam);
						
						if(listPagosBenefDatosAlt.size()>0)
						{
							paramBanCheqBenef=new ComunDto();
			                paramBanCheqBenef.setNoFolioDet(listPagosBenefDatosAlt.get(0).getNoFolioDet());
			                paramBanCheqBenef.setIdBanco(listPagosBenefDatosAlt.get(0).getIdBancoBenef());
			                paramBanCheqBenef.setIdChequera(listPagosBenefDatosAlt.get(0).getIdChequeraBenef());
			                lAfec = compensarPropuestasDao.actualizarBancoCheqBenef(paramBanCheqBenef);
						}
						//' Hace los pagos normales de los documentos en la propuesta
						dtoParamGral= new ComunEgresosDto();
						listPagProApagar=new ArrayList<MovimientoDto>();
						dtoParamGral.setFecha(listGrid.get(i).getFechaPago());
						dtoParamGral.setCveControl(listGrid.get(i).getCveControl());
						dtoParamGral.setIdGrupoRubros(listGrid.get(i).getIdGrupoRubros());
						dtoParamGral.setIdGrupoEmpresas(listGrid.get(i).getIdGrupoEmpresas());
						listPagProApagar=compensarPropuestasDao.consultarPagPropAut1(dtoParamGral);//llama a consultarPagPropAut1
						
						dtoParam= new ComunEgresosDto();
						dtoParam.setCveControl(listGrid.get(i).getCveControl());
						ctaDatos=compensarPropuestasDao.contarMovsPropMAut(dtoParam);
						
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
									if(compensarPropuestasDao.consultarConfiguraSet(261).equals("SI")){
										List<String>listMassPay= new ArrayList<String>();
										
										listMassPay=compensarPropuestasDao.consultarPagoMassPayment(listPagProApagar.get(cont).getNoFolioDet(),
												compensarPropuestasDao.obtenerFechaHoy());
										
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
								if(compensarPropuestasDao.consultarConfiguraSet(261).equals("SI")){
									List<String>listMassPay= new ArrayList<String>();
									
									listMassPay=compensarPropuestasDao.consultarPagoMassPayment(listPagProApagar.get(cont).getNoFolioDet(),
											compensarPropuestasDao.obtenerFechaHoy());
									
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
								compensarPropuestasDao.actualizarEstatusMass(foliosMass);
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


	
	public JRDataSource reportePagoPropuestas(PagosPropuestosDto dtoIn){
		JRMapArrayDataSource jrDataSource = null;
		List<Map<String, Object>> listReport = new ArrayList<Map<String, Object>>();
		
		try {
			dtoIn.setFechaHoy(compensarPropuestasDao.obtenerFechaHoy());
	        dtoIn.setAutorizaPropuesta(compensarPropuestasDao.consultarConfiguraSet(240));
	        dtoIn.setBSoloCheques(false);//valor fijado para pruebas
	        dtoIn.setBTambienIntercos(false);//este valor es fijo segun la llamada en VB
	        System.out.println("busisbes reporte");
	        if(dtoIn.getTipoReporte() == 1)
	        	listReport = compensarPropuestasDao.reportePagoPropuestas(dtoIn); 
	        else if(dtoIn.getTipoReporte() == 2 || dtoIn.getTipoReporte() == 3)
	        	listReport = compensarPropuestasDao.reportePagoPropuestasDetalle(dtoIn);
	        	
	        jrDataSource = new JRMapArrayDataSource(listReport.toArray());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PagoPropuestasBusiness, M:reportePagoPropuestas");
		}
		return jrDataSource;
	}
	
	public CompensarPropuestasDao getcompensarPropuestasDao() {
		return compensarPropuestasDao;
	}

	public void setcompensarPropuestasDao(CompensarPropuestasDao compensarPropuestasDao) {
		this.compensarPropuestasDao = compensarPropuestasDao;
	}




	public Map<String, Object> insertarFoliosZexpFact(String datos, String fecHoy, int idUsuario) {
		Map<String, Object>mapRetorno = new HashMap<String, Object>();
		mapRetorno.put("msgError", "Error desconocido");
		mapRetorno.put("estatus", false);
		try {
			
			mapRetorno = pagosEnSAPBusiness.compensaPropuestasSAP(datos, fecHoy, idUsuario, false, true,new ArrayList<ComunEgresosDto>(),
					false, 0);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagoPropuestasBusiness, M:insertarFoliosZexpFact");
			e.printStackTrace();
		}
		return mapRetorno;
	}



	
	private Map<String, Object> pagarEnSAP(DT_Pagos_OBPagos pagoDto) {
		Map<String, Object>mapRetorno = new HashMap<String, Object>();
		mapRetorno.put("mensaje", "No se pudo acceder a SAP.");
		mapRetorno.put("estatus", false);
		try {
			SOS_PagosServiceLocator service = new SOS_PagosServiceLocator();
			SOS_PagosBindingStub inst = new SOS_PagosBindingStub(new URL(service.getHTTP_PortAddress()), service);
			inst.setUsername(compensarPropuestasDao.consultarConfiguraSet(ConstantesSet.USERNAME_WS_PAGOS));
			inst.setPassword(compensarPropuestasDao.consultarConfiguraSet(ConstantesSet.PASSWORD_WS_PAGOS));
			if (!compensarPropuestasDao.consultarConfiguraSet(4000).equals("local")) {
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
				dtoGrid.setPsAgrupaCheques(comunEgresosDto.get("agrupaCheques"));
				dtoGrid.setPsAgrupaTransfers(comunEgresosDto.get("agrupaTransfers"));				
				listGrid.add(dtoGrid);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:PagoPropuestasAction, M:convierteJsonToListComunEgresosDto");
		}
		return listGrid;
	}
	
	
	@Override
	public HSSFWorkbook consultaPagosExcel(String clave) {
		System.out.println("Clabe bis"+clave);
		HSSFWorkbook hb=null;
		try {
			
			hb=Utilerias.generarExcel(new String[]{
					"Clave Control",
					"Documento",					
					"Importe",
					"Divisa",
					"Nombre Empresa",
					"Nombre Beneficiario"	
			}, 
					compensarPropuestasDao.consultaPagosExcel(clave), new String[]{
					"cve_control",
					"no_docto",
					"importe",
					"id_divisa",
					"nom_empresa",
					"beneficiario"
			});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: reportePersonas");
		}
		return hb;
	}
	
	public String generarExcelMensajes(List<Map<String, String>> datos){
		String nombre = "";
		HSSFWorkbook hb=null;
		try {
			/*hb=Utilerias.generarExcel(new String[]{
					"Clave Control",
					"Mensaje",
					"estatus",
					"origen"
			},datos, new String[]{
					"Clave Control",
					"Mensaje",
					"estatus",
					"origen"
			});
			*/
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


}
