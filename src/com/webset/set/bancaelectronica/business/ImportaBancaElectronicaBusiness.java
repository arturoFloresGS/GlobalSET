package com.webset.set.bancaelectronica.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.webset.set.bancaelectronica.dao.ImportaBancaElectronicaDao;
import com.webset.set.bancaelectronica.dto.CatAfiliacionIngresosDto;
import com.webset.set.bancaelectronica.dto.CatArmaIngresoDto;
import com.webset.set.bancaelectronica.dto.CatBancoDto;
import com.webset.set.bancaelectronica.dto.CatCtaBancoDto;
import com.webset.set.bancaelectronica.dto.CatDivisionDto;
import com.webset.set.bancaelectronica.dto.CatReferenciaChequeraDto;
import com.webset.set.bancaelectronica.dto.CatReferenciaIngresosDto;
import com.webset.set.bancaelectronica.dto.EquivaleConceptoDto;
import com.webset.set.bancaelectronica.dto.MovimientoDto;
import com.webset.set.bancaelectronica.dto.ParamBusImpBancaDto;
import com.webset.set.bancaelectronica.dto.ParamConceptoDescDto;
import com.webset.set.bancaelectronica.dto.ParamDeterminaCorrespondeDto;
import com.webset.set.bancaelectronica.dto.ParamImportarPendientesDto;
import com.webset.set.bancaelectronica.dto.ParamReglaConceptoDto;
import com.webset.set.bancaelectronica.dto.ParamRetImpBancaDto;
import com.webset.set.bancaelectronica.dto.ParamRevividorDto;
import com.webset.set.bancaelectronica.dto.ParamValidaRefCheqDto;
import com.webset.set.bancaelectronica.dto.ParametroDto;
import com.webset.set.bancaelectronica.dto.PersonasDto;
import com.webset.set.bancaelectronica.dto.ReferenciaDetDto;
import com.webset.set.bancaelectronica.dto.ReferenciaEncDto;
import com.webset.set.bancaelectronica.dto.SeleccionGeneralDto;
import com.webset.set.seguridad.dto.EmpresaDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.GeneradorDto;

public class ImportaBancaElectronicaBusiness {
	private ImportaBancaElectronicaDao importaBancaElectronicaDao;
	 private static Logger logger = Logger.getLogger(ImportaBancaElectronicaBusiness.class);
	Bitacora bitacora = new Bitacora();
	Funciones funciones = new Funciones();
	int miEmpresaReg = 0;
	boolean pbAutomatico = false;
	String psRefTraspaso = "000000077";
	String gsDBM = ConstantesSet.gsDBM;
	private GlobalSingleton globalSingleton;
	//int giUsuario=2;
	int miEmpresa=0;
	int liBanco=0;//valor del combo
	
	public List<CatBancoDto>obtenerBancosCombo(boolean pbInversion)
	{
		return importaBancaElectronicaDao.obtenerBancosCombo(pbInversion);
	}
	public List<CatCtaBancoDto>obtenerChequeras(CatCtaBancoDto dto,boolean pbTipoChequera, boolean pbInversion)
	{
		return importaBancaElectronicaDao.obtenerChequeras(dto, pbTipoChequera, pbInversion);
	}
	public List<EmpresaDto>obtenerEmpresas()
	{
		return importaBancaElectronicaDao.obtenerEmpresas();
	}
	public List<EquivaleConceptoDto>obtenerConcepto(int bancoUno, int bancoDos, String psTipoMovto)
	{
		return importaBancaElectronicaDao.obtenerConcepto(bancoUno, bancoDos, psTipoMovto);
	}
	public int cancelarMovimientos(int[]secuencia)
	{
		return importaBancaElectronicaDao.cancelarMovimientos(secuencia);
	}
	
	public List<ParamRetImpBancaDto>consultarMovimientoBancaE(ParamBusImpBancaDto dtoBus)
	{
	    String psQuery="";
	    int i=0;
	    boolean pbEncontrado=false;
	    String psSalvo="";          //sSalvo
	    //String psConcepto ="";     //sConcepto
	    boolean pbExisteBanco=false;
	    boolean bandera=false;
	    boolean lbReferenciaPorChequera=false;//entrada
	    int iEncabezado=0;
	    int iEmpresaAnt=0;
	    int iEmpresa=0;
	    int iIniciaEn=0;
	    int iTerminaEn=0;
	    int miEmpresa=0;
	    //Reemplazo de lista por variables en el paso de parametros
	    int plBanco=dtoBus.getPlBanco();
	    String psChequera=dtoBus.getPsChequera();
	    double pdMontoIni=dtoBus.getPdMontoIni();
	    double pdMontoFin=dtoBus.getPdMontoFin();
	    String psFechaValorIni=dtoBus.getPsFechaValorIni();
	    String psFechaValorFin=dtoBus.getPsFechaValorFin();
	    String psConcepto="";
	    Date psFecha;
	    String sImportaEspeciales="";
	    String sSalvo="";
	    String sReferencia="";
	    String sCorresponde="";
	    String sImporta="";
	    String sDescCorresponde="";
	    String sIdentificador="";
	    String sValores="";
	    int iRenglon=0;
	    String psTipoMovto="";
	    String sNoCliente="";
	    int plIdRubro=0;
	    String psDespliega="";
	    String psDivision="";
	    int plNoCliente=0;
	    String psBeneficiario="";
	    String psDescripcion="";
	    int piBanco=0;
	    String psConceptoFiliacion="";
	    String psRestoRef="";
	    boolean pbTarjetaCred=false;
	    int sCaja=0;
	    String sDivision="";
	    String sCliente="";
	    String sCodigo="";
	    String sSubCodigo="";
	    //Dim masreferencia As Variant
	    //Dim rstConcepto As ADODB.Recordset
	    int idRubro=0;
	    String scampos="";
	    //Dim sValor As Variant
	    int longitud=0;
	    String condicion="";
	    String concepto="";
	    CatAfiliacionIngresosDto dtoAfiliacionIngresos=new CatAfiliacionIngresosDto();
	    CatReferenciaIngresosDto dtoReferenciaIngresos=new CatReferenciaIngresosDto();
	    CatReferenciaChequeraDto dtoReferenciaChequera=new CatReferenciaChequeraDto();
		List<ParamRetImpBancaDto>listRetorno=new ArrayList<ParamRetImpBancaDto>();//lista que retorna los datos del dao
		ParamRetImpBancaDto dtoParamGrid=new ParamRetImpBancaDto();
		List<ParamRetImpBancaDto> listRetornoGrid=new ArrayList<ParamRetImpBancaDto>();//array para retornar los valores al action
		ParamRetImpBancaDto dtoRetornoGrid=new ParamRetImpBancaDto();
		
		try {
			System.out.println("bussines consultarMovimientoBancaE" );
			if((dtoBus.getPlBanco()==12) && (!dtoBus.isOptCapturados())) 
				listRetorno=importaBancaElectronicaDao.consultarMovimientoBancaImportaBancomer(dtoBus);
			else
				listRetorno=importaBancaElectronicaDao.consultarMovimientoBancaE(dtoBus, gsDBM);
		 
	//Inicia 
		if(listRetorno.size()>0)
		{
			 
	        while(i<listRetorno.size())
	        {
	        	System.out.println("descc "+listRetorno.get(0).getDescripcion());
	               if(dtoBus.isOptCapturados())
	               {
	                   if((listRetorno.get(i).getBBancaElect()!=null && listRetorno.get(i).getBBancaElect().equals("I"))||(listRetorno.get(i).getBBancaElect()!=null && listRetorno.get(i).getBBancaElect().equals("A")))
	                       psConcepto = listRetorno.get(i).getConceptoSet();
	                   else
	                       psConcepto = listRetorno.get(i).getConcepto();
	               }else{
	                     psConcepto = listRetorno.get(i).getConcepto();
	               }      
	               if(listRetorno.get(i).getBSalvoBuenCobro()!=null && !listRetorno.get(i).getBSalvoBuenCobro().equals(""))
	            	   psSalvo = listRetorno.get(i).getBSalvoBuenCobro();
	                   //Se agrega todo el cï¿½digo dentro del IF para nuevo mapeo de HSBC
	               if(listRetorno.get(i).getNoEmpresa()>0 
	            		   && (listRetorno.get(i).getIdChequera()!=null && !listRetorno.get(i).getIdChequera().equals("")) 
	            		   && (listRetorno.get(i).getReferencia()!=null && listRetorno.get(i).getReferencia().equals("")))//Validaciones
	               {
	     			   if(listRetorno.get(i).getNoEmpresa()==9 && listRetorno.get(i).getNoEmpresa()==21 && listRetorno.get(i).getIdChequera().equals("04100756759") && listRetorno.get(i).getReferencia().substring(0,1).equals("2"))
	                	   sReferencia= (listRetorno.get(i).getReferencia().substring(0,9))+(listRetorno.get(i).getReferencia().substring(11,listRetorno.get(i).getReferencia().length()-10))+(listRetorno.get(i).getReferencia().substring(10,1));
	                   else
	                   sReferencia = listRetorno.get(i).getReferencia();
	               }
	     			   
	               if(listRetorno.get(i).getNoEmpresa()>0)//validaciones
	               {
	                   iEmpresa = listRetorno.get(i).getNoEmpresa();
	                   miEmpresa = iEmpresa;
	               }
	                   sNoCliente = "";
	                   plIdRubro = 0;
	                   /*Funcionalidad agergada para conceptos que revuelven los bancos
	                   'HSBC 5423: traspasos -- pero hay algunos depositos de clientes
	                   'Banamex 7100: traspasos -- pero hay algunos depositos de clientes
	                   'Banamex 6100 pagos interbancarios  -- pero hay comisiones e ivas*/
	                   
	                   if(listRetorno.get(i).getDespliega()!=null && !listRetorno.get(i).getDespliega().trim().equals("N"))
	                       psDespliega = "S";
	                   else if((listRetorno.get(i).getDespliega()!=null && listRetorno.get(i).getDespliega().trim().equals("N"))
	                		   && (listRetorno.get(i).getDescConcepto()!=null && listRetorno.get(i).getDescConcepto().trim().equals("5423")))
	                   {
	                       psDespliega = "S";
	                       sImportaEspeciales = "S";
	                   }
	                   else if((listRetorno.get(i).getDespliega()!=null && listRetorno.get(i).getDespliega().trim().equals("N"))
	                		   && (listRetorno.get(i).getDescConcepto()!=null && listRetorno.get(i).getDescConcepto().trim().equals("6100")))
	                   {
	                       psDespliega = "S";
	                       sImportaEspeciales = "S";
	                   }
	                   else if ((listRetorno.get(i).getDespliega()!=null && listRetorno.get(i).getDespliega().trim().equals("N"))
	                		   && (listRetorno.get(i).getDescConcepto()!=null && listRetorno.get(i).getDescConcepto().trim().equals("7100")))
	                   {
	                	   psDespliega = "S";
	                       sImportaEspeciales = "S";
	                   }
	                   else
	                       psDespliega = "N";
	                   
	                   psDivision = "";
	                   sNoCliente = "";
	                   
	                   sCorresponde = "";
	                   sDescCorresponde = "";
	                   psConceptoFiliacion = "";
	                   pbTarjetaCred = false;
	                   psBeneficiario = "";
	                   plIdRubro = 0;
	                   plNoCliente = 0;
	                   psDivision = "";
	                   psRestoRef = "";
	                   psFecha=listRetorno.get(i).getFecValor();
	                 
	                   if((listRetorno.get(i).getCargoAbono()!=null && listRetorno.get(i).getCargoAbono().trim().equals("I"))
	                		   &&(listRetorno.get(i).getDespliega()!=null && listRetorno.get(i).getDespliega().trim().equals("S")))
	                   {  
	                       if((psConcepto!=null && psConcepto.trim().equals("INTERES"))
	                    		   ||(psConcepto!=null && psConcepto.trim().equals("DEVOLUCION ISR"))
	                    		   ||(psConcepto!=null && psConcepto.trim().equals("DEV NOMINA"))
	                    		   ||(psConcepto!=null && psConcepto.trim().equals("SOBRANTE")))
	                       {
	                    	   sCorresponde = "";
	                           sDescCorresponde = psConcepto;
	                       } 
	                       else if (lbReferenciaPorChequera)
	                       {
	                            //inicio cambios cie 
	                           /* Para american express y TC hay que cambiar estas descripciones que estan clavadas
	                           ' y poner una tabla nueva donde se guarde banco, descripcion, tipo (AMEX o Visa y Master card)
	                           ' y se mantiene la sucursal 0859 para amex
	                           ' y revisar la parte de anticipos por que solo tienen dos digitos en la referencias para identificarlos*/
	                           pbTarjetaCred = false;
	                           //American Express
	                           if(((plBanco==2) && (listRetorno.get(i).getSucursal()!=null && listRetorno.get(i).getSucursal().equals("0859")))
	                        		   ||(plBanco==2) && (listRetorno.get(i).getDescripcion()!=null && listRetorno.get(i).getDescripcion().indexOf("ORDEN DE ABONO")>0)
	                        		   || (plBanco==2) && (listRetorno.get(i).getDescripcion()!=null && listRetorno.get(i).getDescripcion().indexOf("TEF AMEXCO SE")>0)
	                        		   || (plBanco==21) && (listRetorno.get(i).getDescripcion()!=null && listRetorno.get(i).getDescripcion().indexOf("TEF AMEXCO SE")>0))
	                           {                   
	                               sCorresponde = "C";
	                               sDescCorresponde = "CTE AMEX";
	                               dtoAfiliacionIngresos.setNoEmpresa(listRetorno.get(i).getNoEmpresa()>0?listRetorno.get(i).getNoEmpresa():0);
	                               dtoAfiliacionIngresos.setIdBanco(listRetorno.get(i).getIdBanco()>0?listRetorno.get(i).getIdBanco():0);
	                               dtoAfiliacionIngresos.setIdChequera(listRetorno.get(i).getIdChequera()!=null?listRetorno.get(i).getIdChequera():"");
	                               dtoAfiliacionIngresos.setConcepto(listRetorno.get(i).getDescripcion()!=null?listRetorno.get(i).getDescripcion().substring(17,27):"");
	                               List<CatAfiliacionIngresosDto>listAfiliacion= importaBancaElectronicaDao.obtenerAfiliacionIngresos(dtoAfiliacionIngresos);
	                              if(listAfiliacion.size()>0)
	                              {
	                            	  sNoCliente = listAfiliacion.get(0).getCuentaPuente()!=null?listAfiliacion.get(0).getCuentaPuente():"";
	                                  psDivision = listAfiliacion.get(0).getIdDivision()!=null?listAfiliacion.get(0).getIdDivision():"";
	                                  psBeneficiario = "AMERICAN EXPRESS";
	                                  plIdRubro = 1100100;
	                                  if(dtoBus.getPlBanco()==21)
	                                      psConceptoFiliacion = "FILIACION" +listAfiliacion.get(0).getConcepto()!=null?listAfiliacion.get(0).getConcepto():""
	                                    	  +" - "+listRetorno.get(i).getDescripcion()!=null?listRetorno.get(i).getDescripcion().substring(17,27):"";
	                              }
	                           }else{
	                               
	                                   sNoCliente = "101000060";
	                                   psBeneficiario = "AMERICAN EXPRESS";
	                                   plIdRubro = 1100100;
	                                   if(plBanco==21)
	                                   psConceptoFiliacion = "FILIACION" + funciones.cadenaRight(listRetorno.get(i).getDescripcion()!=null?listRetorno.get(i).getDescripcion():"", 7);
	                               pbTarjetaCred = true;
	                           
	                           }
	                           logger.info("entra 3");
	                           // Visa y MasterCard
	                           if((plBanco==2 && listRetorno.get(i).getDescripcion().indexOf("DEPOSITO X INST TARJ")>0)
	                    		   ||(plBanco==2 && listRetorno.get(i).getDescripcion().indexOf("SUC.TARJETAS")>0)
	                    		   ||(plBanco==21 && listRetorno.get(i).getDescripcion().indexOf("ABONO POR TPV NUM. AFILIACION")>0)
	                    		   ||(plBanco==21 && listRetorno.get(i).getDescripcion().indexOf("ABONO TPV VTAS")>0)
	                    		   &&(!pbTarjetaCred))
	                           {
	                           
	                               sCorresponde = "P";
	                               sDescCorresponde = "TARJ CRED";
	                               sNoCliente = "104010234";
	                               plIdRubro = 1100100;
	                               if(plBanco==21)
	                                   psConceptoFiliacion = "FILIACION" + funciones.cadenaRight(listRetorno.get(i).getDescripcion(), 7);
	                               else if ( (plBanco==2) && (listRetorno.get(i).getDescripcion().indexOf("SUC. TARJETAS"))>0)
	                                   psConceptoFiliacion = "FILIACION" + listRetorno.get(i).getDescripcion().substring(listRetorno.get(i).getDescripcion().indexOf("SUC.TARJETAS")-8,+
	                                		   listRetorno.get(i).getDescripcion().indexOf("SUC.TARJETAS")-8+8);
	                                   //Mid(!descripcion, InStr(1, Trim(!descripcion & ""), " SUC.TARJETAS") - 8, 8);
	                               pbTarjetaCred = true;
	                           }
	                          
	                           //Anticipos
	                           String valorConfiguraSet=importaBancaElectronicaDao.consultarConfiguraSet(1);
	                           if(!valorConfiguraSet.equals("COSTCO"))
	                           {
	                        	   if(sReferencia.substring(0,2).equals("61")||sReferencia.substring(0,2).equals("71")
	                        			   || sReferencia.substring(0,2).equals("81"))
	                        	   {
	                                       sCorresponde = "P";
	                                       sDescCorresponde = "ANTICIPO";
	                                       sNoCliente = "104010240";
	                                       plIdRubro = 1100100;
	                                       pbTarjetaCred = true;
	                        	   }   
	                           }
	                           //Validacion de Ingresos por referencia
	                           if(!pbTarjetaCred) 
	                           {
	                        	   /*aqui cambiar esta reparacion por la que tenemos en ingresos para que ya jale
	                           y tomar de la referencias los XX digitos mas a la izquierda que indica el catalogo
	                           ' ordenar el query por chequera de tal manera que se obtenga la longitud desde un principio
	                           ' y luego ya solo validar la referencia, si la referencias es menor a la longitud requerida
	                           ' ya no se valida nada*/
	                    	  	   dtoReferenciaIngresos.setNoEmpresa(listRetorno.get(i).getNoEmpresa());
	                        	   dtoReferenciaIngresos.setIdBanco(listRetorno.get(i).getIdBanco());
	                        	   dtoReferenciaIngresos.setIdChequera(listRetorno.get(i).getIdChequera());
	                        	   dtoReferenciaIngresos.setReferencia(sReferencia);
	                        	   List<CatReferenciaIngresosDto>refIngreList=importaBancaElectronicaDao.obtenerReferenciaIngresos(dtoReferenciaIngresos);
	                        	   if(refIngreList.size()>0)
	                        	   {
	                        		   plIdRubro = refIngreList.get(0).getIdRubro();
	                                   plNoCliente =Integer.parseInt(refIngreList.get(0).getNoProveedor());
	                                   psDivision = refIngreList.get(0).getDivision();
	                                   psBeneficiario = refIngreList.get(0).getConcepto();
	                                   psRestoRef = "";
	                                   if(refIngreList.get(0).getBCliente().equals("S"))
	                                   {
	                                   	   sCorresponde = "C";
	                                       sDescCorresponde = "ING. CLIENTE";
	                                   }
	                                   else{
	                                	   sCorresponde = "P";
	                                       sDescCorresponde = "ING. PROV.";
	                                   }
	                                   psConceptoFiliacion = "INGRESOCH"+refIngreList.get(0).getConcepto();
	                                   sNoCliente = ""+plNoCliente;
	                                   pbTarjetaCred = true;
	                        	   }
	                               
	                           }
	                           // fin cambios cie *******************************************************
	                           //Analizar la referencia por chequera asignandole el rubro encontrado
	                          if(listRetorno.get(i).getValidaReferencia().trim().equals("S")&&(!pbTarjetaCred)) 
	                           { 
	                               if(listRetorno.get(i).getValidaCheqRef().trim().equals("S")) 
	                               {
	                            	   ParamValidaRefCheqDto dtoParamValidaRefCheq=new ParamValidaRefCheqDto();
	                            	   dtoParamValidaRefCheq.setPsReferencia(sReferencia);
	                            	   dtoParamValidaRefCheq.setPlNoEmpresa(dtoBus.getNoEmpresa());
	                            	   dtoParamValidaRefCheq.setPiBanco(dtoBus.getPlBanco());
	                            	   dtoParamValidaRefCheq.setPsChequera(dtoBus.getPsChequera());
	                            	   dtoParamValidaRefCheq.setPlIdRubro(plIdRubro);
	                            	   dtoParamValidaRefCheq.setPlNoCliente(plNoCliente);
	                            	   dtoParamValidaRefCheq.setPsDivision(psDivision);
	                            	   dtoParamValidaRefCheq.setPsBeneficiario(psBeneficiario);
	                            	   dtoParamValidaRefCheq.setPsRestoRef(psRestoRef);
	                            	   dtoParamValidaRefCheq.setPsCorresponde(sCorresponde);
	                            	   dtoParamValidaRefCheq.setPsFecValor(""+psFecha);
	                            	   //Checar esta funcion
	                            	   boolean validaReferencia=validarReferenciaPorChequera(dtoParamValidaRefCheq);
	                            	   if(validaReferencia)
	                                   {
	                                       if(sCorresponde.trim().equals("")) 
	                                       {
	                                           sCorresponde = "C";
	                                           sDescCorresponde = "CLIENTES";
	                                       }else{
	                                           if(sCorresponde.trim().equals("P"))
	                                               sDescCorresponde = "PROVEEDOR";
	                                           else if(sCorresponde.trim().equals("A")) 
	                                           {   
	                                               sCorresponde = "P";
	                                               sDescCorresponde = "ACREEDOR";
	                                           }
	                                           else if (sCorresponde.trim().equals("M")) 
	                                           {
	                                        	   sCorresponde = "M";
	                                               sDescCorresponde = "MORRALLA";
	                                           }
	                                       }
	                                       sNoCliente = ""+plNoCliente;
	                                       
	                                   }else
	                                   {
	                                   	   sCorresponde = "N";
	                                       sDescCorresponde = "No Identificado";
	                                       
	                               	   }
	                               }else{
	                                   sCorresponde = "P";
	                                   sDescCorresponde = "ACREEDOR";
	                                   
	                                   dtoReferenciaChequera.setNoEmpresa(listRetorno.get(i).getNoEmpresa());
	                                   dtoReferenciaChequera.setIdBanco(listRetorno.get(i).getIdBanco());
	                                   dtoReferenciaChequera.setIdChequera(listRetorno.get(i).getIdChequera());
	                                   List<CatReferenciaChequeraDto>refCheqList=importaBancaElectronicaDao.obtenerReferenciaChequera(dtoReferenciaChequera);
	                                  
	                                   if(refCheqList.size()>0)
	                                   {
	                                	   if(!refCheqList.get(0).getValorDefault().equals(""))
	                                		   sNoCliente="0";
	                                	   else 
	                                		   sNoCliente="0";
	                            	   plIdRubro=refCheqList.get(0).getIdRubro();
	                                   }else
	                                	   plIdRubro = 0;
	                                   
	                               }
	                           }else
	                           {
	                               if(!pbTarjetaCred)
	                               {
	                                   sCorresponde = "N";
	                                   sDescCorresponde = "No Identificado";
	                               }
	                       		}
	                          
	                       CatArmaIngresoDto dtoParamBanco =new CatArmaIngresoDto();
	                       dtoParamBanco.setIdBanco(listRetorno.get(i).getIdBanco());
	                       dtoParamBanco.setIdChequera(listRetorno.get(i).getIdChequera());
	                       dtoParamBanco.setNoEmpresa(listRetorno.get(i).getNoEmpresa());
	                       List<CatArmaIngresoDto>listLongitud=new ArrayList<CatArmaIngresoDto>();
	                       listLongitud=importaBancaElectronicaDao.obtenerLongitud(dtoParamBanco);
	                           //'If Not prTabla.EOF Then
	                          bandera = false;
	                          int cont=0;
	                          ParamDeterminaCorrespondeDto dtoDetCorresponde= new ParamDeterminaCorrespondeDto();
	                          while(cont<=listLongitud.size()&& !bandera)
	                           {
	                        	  
	                               longitud = listLongitud.get(cont).getLongReferencia();
	                               if(listRetorno.get(i).getIdBanco()==12 && (listRetorno.get(i).getReferencia().length()>longitud))
	                                   sReferencia =listRetorno.get(i).getReferencia().substring((listRetorno.get(i).getReferencia().length())-(longitud-1));
	                               else if (listRetorno.get(i).getReferencia().length()>longitud)
	                                   sReferencia = listRetorno.get(i).getReferencia().substring(0,longitud);
	                               else
	                                   sReferencia = listRetorno.get(i).getReferencia();
	                               
	                            
	                               if(listRetorno.get(i).getDescripcion().equals(""))
	                                   condicion = "";
	                               else if (listRetorno.get(i).getIdBanco()==12)
	                               {
	                                       
	                                       if (listRetorno.get(i).getDescripcion()!=null && listRetorno.get(i).getDescripcion().indexOf("")>0)
	                                           condicion = listRetorno.get(i).getDescripcion().substring(0,listRetorno.get(i).getDescripcion().indexOf(" ")-1);
	                                       else
	                                           condicion = listRetorno.get(i).getDescripcion();
	                               }
	                        
	                               if(sCorresponde.equals("N") && (funciones.isNumeric(listRetorno.get(i).getReferencia())==true))
	                               {
	                                   dtoDetCorresponde.setSReferencia(sReferencia);
	                                   dtoDetCorresponde.setNoEmpresa(listRetorno.get(i).getNoEmpresa());
	                                   dtoDetCorresponde.setIdBanco(listRetorno.get(i).getIdBanco());
	                                   dtoDetCorresponde.setIdChequera(listRetorno.get(i).getIdChequera());
	                                   //dtoDetCorresponde.setMasReferencia(masReferencia);es byRef no lleva nada
	                                   dtoDetCorresponde.setSCaja(""+sCaja);
	                                   dtoDetCorresponde.setSCodigo(sCodigo);
	                                   dtoDetCorresponde.setSSubCodigo(sSubCodigo);
	                              
	                                   sCorresponde=determinarCorresponde(dtoDetCorresponde);
	                               }
	                               else
	                               {
	                            	  
	                            	   dtoDetCorresponde.setSReferencia(concepto);//checar cooncepto y/o refernecia
	                                   dtoDetCorresponde.setNoEmpresa(listRetorno.get(i).getNoEmpresa());
	                                   dtoDetCorresponde.setIdBanco(listRetorno.get(i).getIdBanco());
	                                   dtoDetCorresponde.setIdChequera(listRetorno.get(i).getIdChequera());
	                                   //dtoDetCorresponde.setMasReferencia(masReferencia);
	                                   dtoDetCorresponde.setSCaja(""+sCaja);
	                                   dtoDetCorresponde.setSCodigo(sCodigo);
	                                   dtoDetCorresponde.setSSubCodigo(sSubCodigo);
	                                   concepto = listRetorno.get(i).getReferencia().substring(0,longitud);
	                                   if(sCorresponde.equals("N")&& funciones.isNumeric(concepto)==true)
	                                       sCorresponde = determinarCorresponde(dtoDetCorresponde);
	                                       
	                               }
	                               
	                               
	                             
	                               if(sCorresponde.equals("N") && (listRetorno.get(i).getDescripcion().length()>=longitud) )
	                               {
		                               concepto = listRetorno.get(i).getDescripcion().substring(listRetorno.get(i).getDescripcion().length()-(longitud-1));
	                                   //Mid(!descripcion, Len(!descripcion) - (longitud - 1))
		                               if(funciones.isNumeric(concepto)==true) 
		                               {
		                            	   dtoDetCorresponde.setSReferencia(concepto);//checar cooncepto y/o refernecia
		                                   dtoDetCorresponde.setNoEmpresa(listRetorno.get(i).getNoEmpresa());
		                                   dtoDetCorresponde.setIdBanco(listRetorno.get(i).getIdBanco());
		                                   dtoDetCorresponde.setIdChequera(listRetorno.get(i).getIdChequera());
		                                   //dtoDetCorresponde.setMasReferencia(masReferencia);
		                                   dtoDetCorresponde.setSCaja(""+sCaja);
		                                   dtoDetCorresponde.setSCodigo(sCodigo);
		                                   dtoDetCorresponde.setSSubCodigo(sSubCodigo);
		                                   
		                                   concepto = listRetorno.get(i).getReferencia().substring(0,longitud);
		                                   sCorresponde = determinarCorresponde(dtoDetCorresponde);
		                                    //Call gobjVarGlobal.cambia_combo_str(sCorresponde,cmbCorresponde,ls_mat_corresponde)
		                                    //sDescCorresponde = cmbCorresponde.Text
		                               	}
		                              
		                               concepto = listRetorno.get(i).getDescripcion().substring(0,longitud); //Mid(!descripcion, 1, longitud)
		                               if((funciones.isNumeric(concepto)) && (sCorresponde.equals("N"))) // IsNumeric(concepto) And sCorresponde = "N" Then
		                                {
		                            	   dtoDetCorresponde.setSReferencia(concepto);
		                                   dtoDetCorresponde.setNoEmpresa(listRetorno.get(i).getNoEmpresa());
		                                   dtoDetCorresponde.setIdBanco(listRetorno.get(i).getIdBanco());
		                                   dtoDetCorresponde.setIdChequera(listRetorno.get(i).getIdChequera());
		                                   //dtoDetCorresponde.setMasReferencia(masReferencia);
		                                   dtoDetCorresponde.setSCaja(""+sCaja);
		                                   dtoDetCorresponde.setSCodigo(sCodigo);
		                                   dtoDetCorresponde.setSSubCodigo(sSubCodigo);
		                            	   
		                            	   sCorresponde = determinarCorresponde(dtoDetCorresponde);
		                                   //Call gobjVarGlobal.cambia_combo_str(sCorresponde, cmbCorresponde, ls_mat_corresponde)
		                                   //sDescCorresponde = cmbCorresponde.Text
	                               		}
		                                                               
		                               if(sCorresponde.equals("N")) 
		                               {
		                            	   concepto = listRetorno.get(i).getDescripcion().substring(listRetorno.get(i).getDescripcion().length()-(longitud-1));// Mid(!descripcion, Len(!descripcion) - (longitud - 1))
		                            	   dtoDetCorresponde.setSReferencia(concepto);
		                                   dtoDetCorresponde.setNoEmpresa(listRetorno.get(i).getNoEmpresa());
		                                   dtoDetCorresponde.setIdBanco(listRetorno.get(i).getIdBanco());
		                                   dtoDetCorresponde.setIdChequera(listRetorno.get(i).getIdChequera());
		                                   //dtoDetCorresponde.setMasReferencia(masReferencia);
		                                   dtoDetCorresponde.setSCaja(""+sCaja);
		                                   dtoDetCorresponde.setSCodigo(sCodigo);
		                                   dtoDetCorresponde.setSSubCodigo(sSubCodigo);
		                            	   
		                            	   sCorresponde = determinarCorresponde(dtoDetCorresponde);
	                               		}
		                               
	                               }
	                                  
	                           if(sCorresponde.equals("N") && (listRetorno.get(0).getConcepto().length()>=longitud))
	                           	{
	                               concepto = listRetorno.get(0).getConcepto().substring(0,longitud);
	                               if(funciones.isNumeric(concepto)); 
	                               	   concepto=concepto.substring(0,longitud);
		                        	   dtoDetCorresponde.setSReferencia(concepto);
		                               dtoDetCorresponde.setNoEmpresa(listRetorno.get(i).getNoEmpresa());
		                               dtoDetCorresponde.setIdBanco(listRetorno.get(i).getIdBanco());
		                               dtoDetCorresponde.setIdChequera(listRetorno.get(i).getIdChequera());
		                               //dtoDetCorresponde.setMasReferencia(masReferencia);
		                               dtoDetCorresponde.setSCaja(""+sCaja);
		                               dtoDetCorresponde.setSCodigo(sCodigo);
		                               dtoDetCorresponde.setSSubCodigo(sSubCodigo);
		                        	   
		                        	   sCorresponde = determinarCorresponde(dtoDetCorresponde);
	                               
	                               
	                               concepto = listRetorno.get(0).getConcepto().substring(listRetorno.get(0).getConcepto().length()-(longitud-1)); 
	                               //Mid(!concepto, Len(!concepto) - (longitud - 1))
	                               if((funciones.isNumeric(concepto)) && (sCorresponde.equals("N")))
		                        	   dtoDetCorresponde.setSReferencia(concepto);
		                               dtoDetCorresponde.setNoEmpresa(listRetorno.get(i).getNoEmpresa());
		                               dtoDetCorresponde.setIdBanco(listRetorno.get(i).getIdBanco());
		                               dtoDetCorresponde.setIdChequera(listRetorno.get(i).getIdChequera());
		                               //dtoDetCorresponde.setMasReferencia(masReferencia);
		                               dtoDetCorresponde.setSCaja(""+sCaja);
		                               dtoDetCorresponde.setSCodigo(sCodigo);
		                               dtoDetCorresponde.setSSubCodigo(sSubCodigo);
		                        	   
		                        	   sCorresponde = determinarCorresponde(dtoDetCorresponde);
	                                                               
	                               if(sCorresponde.equals("N"))
	                               {
	                            	   if(!listRetorno.get(0).getDescripcion().equals(""))
	                            	   	{
	                            		   concepto = listRetorno.get(i).getDescripcion().substring(listRetorno.get(i).getDescripcion().length()-(longitud-1));//Mid(!descripcion, Len(!descripcion) - (longitud - 1))
	    	                        	   dtoDetCorresponde.setSReferencia(concepto);
	    	                               dtoDetCorresponde.setNoEmpresa(listRetorno.get(i).getNoEmpresa());
	    	                               dtoDetCorresponde.setIdBanco(listRetorno.get(i).getIdBanco());
	    	                               dtoDetCorresponde.setIdChequera(listRetorno.get(i).getIdChequera());
	    	                               //dtoDetCorresponde.setMasReferencia(masReferencia);
	    	                               dtoDetCorresponde.setSCaja(""+sCaja);
	    	                               dtoDetCorresponde.setSCodigo(sCodigo);
	    	                               dtoDetCorresponde.setSSubCodigo(sSubCodigo);
	    	                        	   
	    	                        	   sCorresponde = determinarCorresponde(dtoDetCorresponde);
	                               		}
	                           		}
	                               
	                               //Call gobjVarGlobal.cambia_combo_str(sCorresponde, cmbCorresponde, ls_mat_corresponde)
	                               //sDescCorresponde = cmbCorresponde.Text
	                       		}
	                                 
	                           if(!sCorresponde.equals("N")) 
	                           {   
	                               //Obtiene el rubro
	                        	   List<CatArmaIngresoDto>listArmaRef=new ArrayList<CatArmaIngresoDto>();
	                              listArmaRef=importaBancaElectronicaDao.ObtenerDatosArmaRef(listRetorno.get(i).getNoEmpresa(),
	                            		  listRetorno.get(i).getIdBanco(),listRetorno.get(i).getIdChequera(),sCorresponde);
	                               if(listArmaRef.size()>0)
	                                   plIdRubro = listArmaRef.get(0).getIdRubro();
	
	                               String arrayMasReferencia[][];
	                               arrayMasReferencia=dtoDetCorresponde.getMasReferencia();
	                               if(dtoDetCorresponde.getMasReferencia().length>0)
	                               {
	                            /*
	                               For i = 1 To UBound(masreferencia, 2)
	                                   Select Case UCase(masreferencia(1, i))
	                                       Case "CLIENTE"
	                                           sNo_Cliente = masreferencia(2, i)
	                                       Case "CODIGO"
	                                           sCodigo = masreferencia(2, i)
	                                       Case "SUBCODIGO"
	                                           sSubCodigo = masreferencia(2, i)
	                                       Case "DIVISION"
	                                           psDivision = masreferencia(2, i)
	                                       Case "CONST1", "CONST2", "CONST3", "VAR1", "VAR2", "VAR3"
	                                            Call asignavalores(scaja, sNo_Cliente, sCodigo, sSubCodigo, psDivision, scampos, sValor, masreferencia, i)
	                                   End Select
	                               Next*/
	                               }
	                               bandera = true;
	                       		}
	                               
	                               
	/*'''                                If sCorresponde = "N" Then
	'''                                    sReferencia = !referencia
	'''                                    'concepto = Mid(!descripcion, Len(!descripcion) - (longitud - 1))
	'''                                    sCorresponde = DeterminaCorresponde(sReferencia, !no_empresa, !id_banco, !id_chequera, masreferencia)
	'''
	'''
	'''                                Call gobjVarGlobal.cambia_combo_str(sCorresponde, cmbCorresponde, ls_mat_corresponde)
	'''                                sDescCorresponde = cmbCorresponde.Text
	'''                                End If*/
	                           cont++;
	                           }
	                       }    
	                       else
	                       {
	                    	   
	                    	   ParamDeterminaCorrespondeDto dtoDetCorresponde= new ParamDeterminaCorrespondeDto();
	                    	   if(iEmpresa!=iEmpresaAnt)
	                           iEncabezado = contarEmpresa(iEmpresa,listRetorno.get(i).getIdBanco(), iIniciaEn, iTerminaEn);
	                           //iRenglon = iRenglon;
	                           miEmpresaReg = iEmpresa;
	                           if(listRetorno.get(i).getBRechazo()!=null && listRetorno.get(i).getBRechazo().equals("S"))//checar el ""
	                           {
	                               sCorresponde = "";
	                               sDescCorresponde = "DEVOLUCION PAGO";
	                               sImporta = "S";
	                           }
	                           else
	                           {
	                               //evalua referencia
	                               //sCorresponde = evaluarReferencia(Replace(sReferencia, "-", ""), iEncabezado, iIniciaEn, iTerminaEn, sImporta, sIdentificador, sValores, sNoCliente);
	                        	   dtoDetCorresponde.setSReferencia(concepto);
		                           dtoDetCorresponde.setNoEmpresa(listRetorno.get(i).getNoEmpresa()>0?listRetorno.get(i).getNoEmpresa():0);
		                           dtoDetCorresponde.setIdBanco(listRetorno.get(i).getIdBanco()>0?listRetorno.get(i).getIdBanco():0);
		                           dtoDetCorresponde.setIdChequera(listRetorno.get(i).getIdChequera()!=null?listRetorno.get(i).getIdChequera():"");
		                           
		                           //dtoDetCorresponde.setMasReferencia(masReferencia);
		                           dtoDetCorresponde.setSCaja(""+sCaja);
		                           dtoDetCorresponde.setSCodigo(sCodigo);
		                           dtoDetCorresponde.setSSubCodigo(sSubCodigo);
	                               //Funcion de ingresos para validar la referencia
	                        	   if(listRetorno.get(i).getReferencia()!=null && funciones.isNumeric(listRetorno.get(i).getReferencia())==true 
	                            	 && (listRetorno.get(i).getReferencia()!=null && !listRetorno.get(i).getReferencia().equals(condicion))
	                        	   	 && (listRetorno.get(i).getReferencia()!=null && listRetorno.get(i).getReferencia().length()==longitud))
	                        	   sCorresponde = determinarCorresponde(dtoDetCorresponde);
	                               else
	                                   sCorresponde = "N";
	                               
	                               concepto = "";
	                               concepto = listRetorno.get(i).getConcepto()!=null?listRetorno.get(i).getConcepto().substring(0,longitud):"";
	                               
	                               if(funciones.isNumeric(concepto)==true)
	                               {
	                            	 
	                            	  // dtoDetCorresponde.setSReferencia(concepto);
	                            	   //sCorresponde = determinarCorresponde(dtoDetCorresponde);
	                               }
	                               else
	                               {
	                                   concepto =""+(listRetorno.get(i).getConcepto()!=null?listRetorno.get(i).getConcepto().length()-longitud-1:"");
	                                   dtoDetCorresponde.setSReferencia(concepto);
	                                   if(funciones.isNumeric(concepto))
	                                	   sCorresponde = determinarCorresponde(dtoDetCorresponde);
	                               }
	                            
	                              /* if(sCorresponde.equals("N"))
	                               {
	                                   concepto =""+(listRetorno.get(i).getConcepto().length()-longitud-1);
	                                   dtoDetCorresponde.setSReferencia(concepto);
	                                   sCorresponde = determinarCorresponde(dtoDetCorresponde);
	                               }*/
	                           
	                              /* if(sCorresponde.equals(""))
	                               {
	                                   if (!pbAutomatico) 
	                                	   logger.info("faltan datos en regla_repara , vbExclamation, SET");
	                                      //checar MsgBox "faltan datos en regla_repara ", vbExclamation, "SET"
	                                   //Exit Sub
	                               }*/
	           /*'                    Select Case UCase(sCorresponde)
	           '                        Case "C": sDescCorresponde = "Clientes"
	           '                        Case "V": sDescCorresponde = "Varios"
	           '                        Case "N": sDescCorresponde = "No Identificado"
	           '                        Case "J": sDescCorresponde = "Caja Clientes"
	           '                        Case "K": sDescCorresponde = "Caja Varios"
	           '                        Case "T": sDescCorresponde = "Traspasos"
	           '                        Case "F": sDescCorresponde = "Financiamientos"
	           '                    End Select*/
	                               if(sCorresponde.equals("C"))
	                               {
	                                   //Validar que el cliente pertenezca a la misma empresa donde se hizo el depï¿½sito
	                            	   if(!(importaBancaElectronicaDao.verificarEmpresaCobranzaCliente(sReferencia,listRetorno.get(i).getIdBanco(),listRetorno.get(i).getIdChequera())))
	                                       sCorresponde = "N";
	                               }
	                               //Call gobjVarGlobal.cambia_combo_str(sCorresponde, cmbCorresponde, ls_mat_corresponde)
	                               //sDescCorresponde = cmbCorresponde.Text
	                           }
	                   		}
	                   //'Else
	                       //'sImporta = "S"
	                       //'sCorresponde = ""
						}
	                   
	                   boolean repetido=false;
	                   dtoRetornoGrid=new ParamRetImpBancaDto();
	                   //Validar los repetidos
	                   
	                   for(int c=0;c<listRetornoGrid.size();c++)
	                   {
	                	   if(listRetorno.get(i).getSecuencia()==listRetornoGrid.get(c).getSecuencia())
	                	   {
	                		    repetido=true;
	                		    break;
	                	   }
	                		  
	                   }
	                   if(sCorresponde.equals(""))
	                   {
	                	   if(listRetorno.get(i).getConceptoSet().equals("DEP FIRME")){
	                		   sCorresponde = "N";
	                	   }
	                   }
	                  if(!repetido)
	                  {
	                	  if(psDespliega.trim().equals("S") && (!listRetorno.get(i).getReferencia().equals(psRefTraspaso)))
	                	  {
			                   if(listRetorno.get(i).getNoEmpresa()==9 && listRetorno.get(i).getIdBanco()==21 && (listRetorno.get(i).getIdChequera()!=null && listRetorno.get(i).getIdChequera().equals("04100756759"))
			                		   && (listRetorno.get(i).getReferencia()!=null && listRetorno.get(i).getReferencia().substring(0,1).equals("2")))
			                   	   dtoRetornoGrid.setReferencia(listRetorno.get(i).getReferencia()!=null?listRetorno.get(i).getReferencia().substring(0,20):"");//1-20
			                   else
			                	   dtoRetornoGrid.setReferencia(listRetorno.get(i).getReferencia()!=null?listRetorno.get(i).getReferencia():"");
			                  
			                   //if(psConcepto!=null && !psConcepto.equals(""))
			                    	//dtoRetornoGrid.setDescConcepto(psConceptoFiliacion);
                              // else la linea de abajo esta dentro del else
                               	//dtoRetornoGrid.setDescConcepto(listRetorno.get(i).getConcepto());
			                   dtoRetornoGrid.setDescConcepto(listRetorno.get(i).getDescConcepto());
			                   
			                   if(sImportaEspeciales.equals("S"))
			                	   dtoRetornoGrid.setImporta(sImportaEspeciales);
			                   else
			                	   dtoRetornoGrid.setImporta(listRetorno.get(i).getImporta());
			                   
			                    dtoRetornoGrid.setImporte(listRetorno.get(i).getImporte()>0?listRetorno.get(i).getImporte():0);
			                   	dtoRetornoGrid.setFecValorString(funciones.ponerFecha(psFecha));
			                    dtoRetornoGrid.setSucursal(listRetorno.get(i).getSucursal()!=null?listRetorno.get(i).getSucursal():"");
			                    dtoRetornoGrid.setFolioBanco(listRetorno.get(i).getFolioBanco()!=null?listRetorno.get(i).getFolioBanco():"");
			                    dtoRetornoGrid.setIdChequera(listRetorno.get(i).getIdChequera()!=null?listRetorno.get(i).getIdChequera():"");
			                    dtoRetornoGrid.setDescBanco(listRetorno.get(i).getDescBanco()!=null?listRetorno.get(i).getDescBanco():"");
			                    dtoRetornoGrid.setConcepto(listRetorno.get(i).getConcepto()!=null?listRetorno.get(i).getConcepto():"");
			                    dtoRetornoGrid.setNoEmpresa(listRetorno.get(i).getNoEmpresa()>0?listRetorno.get(i).getNoEmpresa():0);
			                    dtoRetornoGrid.setNomEmpresa(listRetorno.get(i).getNomEmpresa()!=null?listRetorno.get(i).getNomEmpresa():"");
			                    dtoRetornoGrid.setBBancaElect(listRetorno.get(i).getBBancaElect()!=null?listRetorno.get(i).getBBancaElect():"");
			                    dtoRetornoGrid.setSecuencia(listRetorno.get(i).getSecuencia());
			                    dtoRetornoGrid.setNoCuentaEmp(listRetorno.get(i).getNoCuentaEmp());
			                    dtoRetornoGrid.setTranspasoDeposito(listRetorno.get(i).getTranspasoDeposito());//campo para validacion en js
                                	
			                    dtoRetornoGrid.setConcepto2(listRetorno.get(i).getConcepto2()!=null?listRetorno.get(i).getConcepto2():"");
			                    dtoRetornoGrid.setCargoAbono(listRetorno.get(i).getCargoAbono()!=null?listRetorno.get(i).getCargoAbono():"");
			                    dtoRetornoGrid.setObservacion(listRetorno.get(i).getObservacion() != null ? listRetorno.get(i).getObservacion() : "");
			                    dtoRetornoGrid.setDescCorresponde(sDescCorresponde);
			                    dtoRetornoGrid.setCorresponde(sCorresponde);
			                    dtoRetornoGrid.setSIdentificador(sIdentificador);
			                    dtoRetornoGrid.setSValores(sValores);
			                    dtoRetornoGrid.setNoCliente(sNoCliente);
			                    dtoRetornoGrid.setBeneficiario(psBeneficiario);
			                    dtoRetornoGrid.setIdDivision(psDivision!=null && !psDivision.equals("")?Integer.parseInt(psDivision):0);
			                    dtoRetornoGrid.setIdBanco(listRetorno.get(i).getIdBanco());
			                    if(sImportaEspeciales!=null && sImportaEspeciales.equals("S"))
	                               dtoRetornoGrid.setImporta("S");
	                            else
	                               dtoRetornoGrid.setImporta(listRetorno.get(i).getImporta());
			                    
			                    dtoRetornoGrid.setIdCveConcepto(listRetorno.get(i).getIdCveConcepto());
			                    dtoRetornoGrid.setConceptoBancario(listRetorno.get(i).getConceptoBancario());
			                    dtoRetornoGrid.setConceptoSet(listRetorno.get(i).getConceptoSet());
			                    
			                    /*impresiones de prueba
			                    logger.info("beneficiario"+ psBeneficiario);
			                    logger.info("descConcepto"+ listRetorno.get(0).getDescConcepto());
			                    logger.info("desCorresponde"+sDescCorresponde);
			                    logger.info("corresponde"+sCorresponde);
			                    logger.info("identificador"+sIdentificador);
			                    logger.info("valores"+sValores);
			                    logger.info("noCliente"+sNoCliente);
			                    logger.info("noCuentaEmp"+listRetorno.get(0).getNoCuentaEmp());
			                    
			                    
			                    //Termina impresiones de prueba*/
	                            dtoRetornoGrid.setBRechazo(listRetorno.get(i).getBRechazo());
	                            dtoRetornoGrid.setDescripcion(listRetorno.get(i).getDescripcion());
	                            dtoRetornoGrid.setLiberaAut(listRetorno.get(i).getLiberaAut());
	                            dtoRetornoGrid.setIdRubro(plIdRubro);//Solo aplica para Depositos CM
	                            dtoRetornoGrid.setRestoReferencia(psRestoRef);
			                    
	                            dtoRetornoGrid.setIdCaja(sCaja);
		                        dtoRetornoGrid.setCodigo(sCodigo);
		                        dtoRetornoGrid.setSubCodigo(sSubCodigo);
		                        dtoRetornoGrid.setIdUsuario(dtoBus.getPlNoUsuario());
		                       
	                            
			                    //Solo para Banamex, si la descripcion del concepto tiene IVA entonces asegurarnos de que entre como IVA
		                           if((listRetorno.get(i).getDescBanco()!=null && listRetorno.get(i).getDescBanco().equals("BANAMEX"))
		                        		   && (listRetorno.get(i).getCargoAbono()!=null && listRetorno.get(i).getCargoAbono().trim().equals("E")))
		                           {
		                               psDescripcion = listRetorno.get(i).getDescripcion()!=null?listRetorno.get(i).getDescripcion():"";
		                               if(psDescripcion.indexOf("IVA")>0)
		                               {
		                            	   dtoRetornoGrid.setConcepto("IVA");
		                            	   dtoRetornoGrid.setDescConcepto("IVA");
		                               }
		                               // se agrega CUOTA TPV CELULAR
		                               //CUOTA MENSUAL ADQUIR
		                               //PAGO INT MDIA
		                               //GASTOS DE OPER
		                               //ya que son conceptos de comision presentes en costco verificar si aplica para los demas
		                               else if (psDescripcion.indexOf("COMISION")>0 ||
		                                       ((listRetorno.get(i).getConcepto2()!=null && listRetorno.get(i).getConcepto2().equals("500"))&& (listRetorno.get(i).getDescripcion()!=null && listRetorno.get(i).getDescripcion().equals("TARIFA TAR DEBIT")))
		                                       ||((listRetorno.get(i).getConcepto2()!=null && listRetorno.get(i).getConcepto2().equals("500"))&& (listRetorno.get(i).getDescripcion()!=null && listRetorno.get(i).getDescripcion().equals("CUOTA TPV CELULAR")))
		                                       ||((listRetorno.get(i).getConcepto2()!=null && listRetorno.get(i).getConcepto2().equals("500"))&& (listRetorno.get(i).getDescripcion()!=null && listRetorno.get(i).getDescripcion().equals("CUOTA MENSUAL ADQUIR")))
		                                       ||((listRetorno.get(i).getConcepto2()!=null && listRetorno.get(i).getConcepto2().equals("500"))&& (listRetorno.get(i).getDescripcion()!=null && listRetorno.get(i).getDescripcion().equals("PAGO INT MDIA")))
		                                       ||((listRetorno.get(i).getConcepto2()!=null && listRetorno.get(i).getConcepto2().equals("500"))&& (listRetorno.get(i).getDescripcion()!=null && listRetorno.get(i).getDescripcion().equals("GASTOS DE OPER"))))
		                               {
		                            	   dtoRetornoGrid.setDescConcepto("COMISIONES");
		                            	   dtoRetornoGrid.setConcepto("COMISIONES");
		                               }
		                           }
			                    listRetornoGrid.add(dtoRetornoGrid);
	                	  }
	                	  if(listRetorno.get(i).getCargoAbono().equals("I"))
	                		  iEmpresaAnt = iEmpresa;
	                  }
	                   //para validar los repetidos
	                   /*
	                   pbEncontrado = false;
	                   for(int inc=0;)For i = 1 To msfDatos.Rows - 1
	                       If !secuencia = Val(msfDatos.TextMatrix(i, LI_C_SECUENCIA)) Then
	                           pbEncontrado = True
	                       End If
	                   Next i
	                  
	                   If sCorresponde = "" Then
	                       sCorresponde = "N"
	                       sDescCorresponde = "No Identificado"
	                   End If
	                   
	                   If pbEncontrado = False Then
	                   
	                       If psDespliega = "S" And (!referencia & "") <> psRefDetTraspaso Then
	               
	           '                 msfDatos.AddItem "X" & Chr(9) & ps_salvo & Chr(9) & ps_concepto & Chr(9) & _
	                            !referencia & Chr(9) & Format$(CStr(!importe), "###,###,###,##0.00") & _
	                            Chr(9) & Format$(!fec_valor, "dd/mm/yyyy") & Chr(9) & !sucursal & Chr(9) & _
	                            !folio_banco & Chr(9) & !id_banco & Chr(9) & !id_chequera & Chr(9) & _
	                            !secuencia & Chr(9) & !desc_banco & Chr(9) & !observacion & Chr(9) & _
	                            !cargo_abono & Chr(9) & !concepto2 & Chr(9) & !no_empresa & Chr(9) _
	                            & !nom_empresa & Chr(9) & !desc_concepto & Chr(9) & !secuencia & Chr(9) _
	                            & !no_cuenta_emp & Chr(9) & !b_banca_elect & Chr(9) _
	                            & sDescCorresponde & Chr(9) & sCorresponde & Chr(9) & sIdentificador & Chr(9) & sValores
	                           msfDatos.AddItem ""
	                           iRenglon = msfDatos.Rows - 1
	                           msfDatos.TextMatrix(iRenglon, LI_C_SELECCION) = "X"
	                           msfDatos.TextMatrix(iRenglon, LI_C_SBC) = ps_salvo
	                           msfDatos.TextMatrix(iRenglon, LI_C_CONCEPTO) = ps_concepto
	                           'ACM 01/10/07 - Se agrega IF para nuevo mapeo de HSBC
	                           If !no_empresa = 9 And !id_banco = 21 And !id_chequera = "04100756759" And Mid(Trim(!referencia), 1, 1) = 2 Then
	                               'msfDatos.TextMatrix(iRenglon, LI_C_REFERENCIA) = Mid(Trim(sReferencia), 1, 20) & ""
	                               msfDatos.TextMatrix(iRenglon, LI_C_REFERENCIA) = Mid(Trim(!referencia), 1, 20) & ""
	                           Else
	                               'msfDatos.TextMatrix(iRenglon, LI_C_REFERENCIA) = sReferencia & ""
	                               msfDatos.TextMatrix(iRenglon, LI_C_REFERENCIA) = !referencia & ""
	                           End If
	                           'msfDatos.TextMatrix(iRenglon, LI_C_REFERENCIA) = !referencia & "" 'ACM 01/10/07
	                           msfDatos.TextMatrix(iRenglon, LI_C_IMPORTE) = Format$(CStr(!importe), "###,###,###,##0.00")
	                           
	                           If psFecha <> "" Then
	                               msfDatos.TextMatrix(iRenglon, LI_C_FEC_VALOR) = Format$(psFecha, "dd/mm/yyyy")
	                           Else
	                               msfDatos.TextMatrix(iRenglon, LI_C_FEC_VALOR) = Format$(!fec_valor, "dd/mm/yyyy")
	                           End If
	                           
	                           msfDatos.TextMatrix(iRenglon, LI_C_SUCURSAL) = !sucursal & ""
	                           msfDatos.TextMatrix(iRenglon, LI_C_FOLIO_BANCO) = !folio_banco & ""
	                           msfDatos.TextMatrix(iRenglon, LI_C_ID_BANCO) = !id_banco & ""
	                           msfDatos.TextMatrix(iRenglon, LI_C_ID_CHEQUERA) = !id_chequera & ""
	                           msfDatos.TextMatrix(iRenglon, LI_C_SECUENCIA) = !secuencia & ""
	                           msfDatos.TextMatrix(iRenglon, LI_C_DESC_BANCO) = !desc_banco & ""
	                           msfDatos.TextMatrix(iRenglon, LI_C_OBSERVACION) = !observacion & ""
	                           msfDatos.TextMatrix(iRenglon, LI_C_CARGO_ABONO) = !cargo_abono & ""
	                           msfDatos.TextMatrix(iRenglon, LI_C_CONCEPTO2) = !concepto2 & ""
	                           msfDatos.TextMatrix(iRenglon, LI_C_NO_EMPRESA) = !no_empresa & ""
	                           msfDatos.TextMatrix(iRenglon, LI_C_NOM_EMPRESA) = !nom_empresa & ""
	                           
	                           If ps_concepto_filiacion <> "" Then
	                               msfDatos.TextMatrix(iRenglon, LI_C_DESC_CONCEPTO) = ps_concepto_filiacion & ""
	                           Else
	                               msfDatos.TextMatrix(iRenglon, LI_C_DESC_CONCEPTO) = !concepto & ""
	                           End If
	                           
	                           msfDatos.TextMatrix(iRenglon, LI_C_SECUENCIA2) = !secuencia & ""
	                           msfDatos.TextMatrix(iRenglon, LI_C_NO_CUENTA_EMP) = !no_cuenta_emp & ""
	                           msfDatos.TextMatrix(iRenglon, LI_C_ID_BANCA_ELECT) = !b_banca_elect & ""
	                           msfDatos.TextMatrix(iRenglon, LI_C_DESCRIPCION_CORRESP) = sDescCorresponde
	                           msfDatos.TextMatrix(iRenglon, LI_C_CORRESPONDE) = sCorresponde
	                           msfDatos.TextMatrix(iRenglon, LI_C_IDENTIFICADOR) = sIdentificador
	                           msfDatos.TextMatrix(iRenglon, LI_C_VALORES) = sValores
	                           msfDatos.TextMatrix(iRenglon, LI_C_NO_CLIENTE) = sNo_Cliente
	                           msfDatos.TextMatrix(iRenglon, LI_C_BENEFICIARIO) = psBeneficiario
	                           msfDatos.TextMatrix(iRenglon, LI_C_ID_DIVISION) = psDivision
	                           If sImportaEspeciales = "S" Then
	                               msfDatos.TextMatrix(iRenglon, LI_C_IMPORTA_CONCEPTO) = "S"
	                           Else
	                               msfDatos.TextMatrix(iRenglon, LI_C_IMPORTA_CONCEPTO) = !importa & ""
	                           End If
	                           msfDatos.TextMatrix(iRenglon, LI_C_B_RECHAZO) = !b_rechazo & ""
	                           msfDatos.TextMatrix(iRenglon, LI_C_DESCRIPCION) = !descripcion & ""
	                           msfDatos.TextMatrix(iRenglon, LI_C_HORA_FEC_VALOR) = Format$(!fec_valor, "hh:mm")
	                           msfDatos.TextMatrix(iRenglon, LI_C_LIBERA_AUT) = !libera_aut & ""
	                           msfDatos.TextMatrix(iRenglon, LI_C_ID_RUBRO) = plIdRubro  'Solo aplica para Depositos CM
	                           msfDatos.TextMatrix(iRenglon, LI_C_RESTO_REF) = psRestoRef
	                           
	                           If psTipo_movto = "A" And !cargo_abono = "E" Then
	                               TxtMonto = Val(TxtMonto) - Format$(!importe, "###,###,###,##0.00")
	                           Else
	                               TxtMonto = Val(TxtMonto) + Format$(!importe, "###,###,###,##0.00")
	                           End If
	                           
	                           msfDatos.TextMatrix(iRenglon, LI_C_ID_CAJA) = scaja
	                           msfDatos.TextMatrix(iRenglon, LI_C_ID_CODIGO) = sCodigo
	                           msfDatos.TextMatrix(iRenglon, LI_C_ID_SUBCODIGO) = sSubCodigo
	                           
	                           'Solo para Banamex, si la descripcion del concepto tiene IVA entonces asegurarnos de que entre como IVA
	                           If msfDatos.TextMatrix(i, LI_C_ID_BANCO) = BANAMEX And Trim(!cargo_abono) = "E" Then
	                               psDescripcion = Trim(!descripcion & "")
	                               If InStr(1, psDescripcion, "IVA") > 0 Then
	                                   msfDatos.TextMatrix(i, LI_C_CONCEPTO) = "IVA"
	                                   msfDatos.TextMatrix(i, LI_C_DESC_CONCEPTO) = "IVA"
	                                   
	                               ' se agrega CUOTA TPV CELULAR
	                               ' CUOTA MENSUAL ADQUIR
	                               ' PAGO INT MDIA
	                               ' GASTOS DE OPER
	                               ' ya que son conceptos de comision presentes en costco verificar si aplica para los demas
	                               ElseIf InStr(1, psDescripcion, "COMISION") > 0 Or _
	                                       (msfDatos.TextMatrix(iRenglon, LI_C_CONCEPTO2) = "500" And _
	                                       msfDatos.TextMatrix(iRenglon, LI_C_DESCRIPCION) = "TARIFA TAR DEBIT") Or _
	                                       (msfDatos.TextMatrix(iRenglon, LI_C_CONCEPTO2) = "500" And _
	                                       msfDatos.TextMatrix(iRenglon, LI_C_DESCRIPCION) = "CUOTA TPV CELULAR") Or _
	                                       (msfDatos.TextMatrix(iRenglon, LI_C_CONCEPTO2) = "500" And _
	                                       msfDatos.TextMatrix(iRenglon, LI_C_DESCRIPCION) = "CUOTA MENSUAL ADQUIR") Or _
	                                       (msfDatos.TextMatrix(iRenglon, LI_C_CONCEPTO2) = "500" And _
	                                       (InStr(1, psDescripcion, "PAGO INT MDIA") > 0)) Or _
	                                       (msfDatos.TextMatrix(iRenglon, LI_C_CONCEPTO2) = "500" And _
	                                       (InStr(1, psDescripcion, "GASTOS DE OPER") > 0)) Then
	                                   msfDatos.TextMatrix(i, LI_C_CONCEPTO) = "COMISIONES"
	                                   msfDatos.TextMatrix(i, LI_C_DESC_CONCEPTO) = "COMISIONES"
	                               End If
	                           End If
	                       End If
	                       
	                       If !cargo_abono = "I" Then
	                           iEmpresaAnt = iempresa
	                       End If
	                       
	                   End If
	               
	               'End If MS
	               
	               .MoveNext
	               
			
		 		
	            TxtMonto = Format$(TxtMonto, "###,###,###,##0.00")
	            TxtRegistros = Format$(msfDatos.Rows - 1, "###,###,##0")
	            .Close*/
	           i++;
	        }
		}else{

		}
		//logger.info("Antes de la llamada");
		//listRetornoGrid=importaBancaElectronicaDao.obtenerTraspasoDeposito(listRetornoGrid);
		//logger.info("Despues");
	}
	catch(Exception e){
		//bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		//+ "P:Banca Electronica, C:ImportaBancaElectronicaBusiness, M:consultarMovimientoBancaE");
		e.printStackTrace();
	} 
			///Termina
		return listRetornoGrid;
}
//Boton Ejecutar	
	
public Map<String,Object> ejecutarImportaBancaElectronica(List<ParamRetImpBancaDto> dto)
{
	int  i=0;
	int resultadoInsertUno=-1;
	int resultadoInsertDos=-1;
	int contInsert=0;
	int piTipoDocto=0;
	int iNoEmpresa=0;
	String sChequera="";
	Map pdResp;
	int pdFolioMov=0;
	boolean sinRegistrar=false;
	int pdFolioDet=0;
	String psNoCliente="";
	String psEstatusMov="";
	String psReferencia="";
	String sReferActual="";
	String psDivisa="";
	String psOrigen="";
	String psSalvoBCobro="";
	int plFolio =0;
	int plFolio1 =0;
	int plEmpresa =0;
	int lAfectados =0;
	boolean pbBanca;
	boolean bEncontrado;
	boolean bNoEntra=false;
	String piTipoOperacion="";
	int piFormaPago=0;
	String psOrigenMov="";
	double pdImporteDesglosado=0;
	int piPosicionIni=0;
	int piPosicionFin=0;
	String psRango="";
	String psCod="";
	int piIdCaja=0;
	String psSubCod="";
	String psFecValorOriginal="";
	String psOperacionDev="";
	int plIdRubro=0;
	String psFecValor="";
	
	String psConceptoDocto="";
	String psDivision="";
	String psClaveOperOrig="";
	int liImporta=0;
	String sTraspDp=""; 
	String sMensaje="";
	 
	Map<String, Object> result = new HashMap<String,Object>();
	pbAutomatico = false;
	List<ParamReglaConceptoDto> listGrupoRubro = new ArrayList<ParamReglaConceptoDto>();
	int existe;
	
	try {
		//AQUI PRINCPIA EL BARIDO DEL GRID for
		
		for(i=0; i<dto.size(); i++) {
			existe = 0;
			existe = importaBancaElectronicaDao.existeMovto(dto.get(i).getSecuencia());
			
			if(existe == 0) {
			if (dto.get(i).getReferencia()!=null && !dto.get(i).getReferencia().equals(psRefTraspaso)) {
				if(dto.get(i).getImporta()!=null && dto.get(i).getImporta().equals("N")) {
					//'MsgBox "El Concepto " & Tri(MSFdatos.TextMatrix(i, LI_C_CONCEPTO2) & " Solo se muestra, no se importa", vbInformatio, "SET"
					sinRegistrar = true;
					//msfDatos.TextMatrix(i, 0) = "";
					//Checardto.get(i).getRferencia().equals("");
			        bNoEntra=true;
			        result.put("msgUsuario","Este registro solo se muestra, pero no se importa!!");
			        return result;
				}
				piTipoDocto = 0;
		        bEncontrado = false;
		        sReferActual = dto.get(i).getReferencia()!=null?dto.get(i).getReferencia():"";//Trim$(msfDatos.TextMatrix(i, LI_C_REFERENCIA))
			    
		        if(dto.get(i).getBBancaElect()!=null&& dto.get(i).getBBancaElect().equals("N"))
		        	pbBanca =false;
		        else
		        	pbBanca =true;
		        piIdCaja = dto.get(i).getIdCaja();//msfDatos.TextMatrix(i, I_C_ID_CAJA)
		        psCod = dto.get(i).getCodigo();//msfDatos.TextMatrix(i, LI_C_ID_CODGO)
		        psSubCod = dto.get(i).getSubCodigo();//msfDatos.TextMatrix(i, LI_C_I_SUBCODIGO)
		        psNoCliente = "";
			       
		        //pon el origen corecto
		        if((dto.get(i).getCorresponde()!=null) &&(dto.get(i).getCorresponde().equals("N") || dto.get(i).getCorresponde().equals(""))&& (!dto.get(i).getCorresponde().equals("P")) && !sReferActual.equals(""))
		        	if (sReferActual.length() > 4)
		        		psOrigen = sReferActual.substring(2,4).equals("1")?"":sReferActual.substring(2,4);//IIf(Mid$(sReferActul,3, 1) = "1", "0", Mid$(sReferActul, 3, 1))
		        	else 
			            psOrigen = "";
		        else
		            psOrigen = "";
		        
			    //asigna referencia
		        if(dto.get(i).getCorresponde()!=null && (dto.get(i).getCorresponde().equals("C")||dto.get(i).getCorresponde().equals("")|| dto.get(i).getCorresponde().equals("P"))) {
		        	piTipoDocto = 51;
		            psNoCliente = dto.get(i).getNoCliente();
		            psCod = "1";
		            psSubCod= "001";
		            psReferencia = "DEPOSITO BANCO CLIENTE";
		        }else if(dto.get(i).getCorresponde()!=null & dto.get(i).getCorresponde().trim().equals("M")) {
			        piTipoDocto = 51;
			        psNoCliente =dto.get(i).getNoCliente();
			        psReferencia = "DEPOSITO MORRALLAS";
			    }else if(dto.get(i).getCorresponde()!=null & (dto.get(i).getCorresponde().trim().equals("V")||dto.get(i).getCorresponde().trim().equals("K"))) {
			    	psReferencia ="VARIOS";
			        piTipoDocto = 52;
			        psNoCliente = "";
			    }else if(dto.get(i).getCorresponde()!=null && dto.get(i).getCorresponde().equals("N")) {
			    	psReferencia = "NI";
			        piTipoDocto = 52;
			        psNoCliente = "";
			    }
		        //'ingreso_egreso'
			    //'optimizaciï¿½ de cï¿½digo
			    //'VLIACION PARA CIE
		        listGrupoRubro = new ArrayList<ParamReglaConceptoDto>();
		        
		        List<ParamReglaConceptoDto>listReglaConcepto = new ArrayList<ParamReglaConceptoDto>();
		        ParamReglaConceptoDto dtoReglaConcepto = new ParamReglaConceptoDto();
		        dtoReglaConcepto.setIngresoEgreso(dto.get(i).getCargoAbono()); //obtenerReglaConcepto
		        dtoReglaConcepto.setConceptoSet(dto.get(i).getDescConcepto());
		        dtoReglaConcepto.setIdCorresponde(dto.get(i).getCorresponde());
		        
		        listReglaConcepto = importaBancaElectronicaDao.obtenerReglaConcepto(dtoReglaConcepto);
		        
		        if(listReglaConcepto.size()>0) {
		        	//System.out.println("listaRecglaConcepto mayoa  0");
		        	piTipoOperacion = ""+listReglaConcepto.get(0).getIdTipoOperacion();
		            piFormaPago = listReglaConcepto.get(0).getIdFormaPago(); 
		            psOrigenMov =listReglaConcepto.get(0).getOrigenMov();
		            bEncontrado = true;
		      //      System.out.println("origen mov "+psOrigenMov);
		            listGrupoRubro = importaBancaElectronicaDao.obtenerGrupoRubro(piTipoOperacion);
		            
		        }else {
		        	piTipoOperacion = "0";
			        piFormaPago= 0;
			        psOrigenMov = "";
			        bEncontrado =false;
			    }
		        
			//'            While  <= UBound(mareReglaCon) And bEnconrado = False
			//                If maeReglaCon(j).sIgrsoEgreso = msfDatos.TextMatrix(i, LI_C_CARGO_ABONO) And 
			//'                  mareReglaCon(j).sConceptoSet = msfDatos.TextMatrix(i, LI_C_COCEPTO) Then
			//'                   If Trim$(msfDatos.TextMatrix(i, LIC_CARGO_ABONO)) = "E" Then
			//'                       If marReglaCon(j).sOrigenMov= "set" Then
			//'                           bEncontrado = True
			//'                       Else
			//'                           j = j +1
			//'                        End If
			//'                  Else
			//'                       If Trim(mareReglaCon(j).sOrigenMv) = "" And _
			//'                         mareReglaCon(j).sIdCrresponde  msfDatos.TextMatrix(i, LI_C_CORRSPONDE) Then
			//'                          bEncontrado = True
			//'                       Else
			//'                           j =  + 1
			//'                       nd If
			//'                  End If
			//'               Else
			//'                    j = j + 1
			//'              End If
			//'            Wend
			       
			//'          If j > UBound(mareReglaCo) Then
			        
			    if(!bEncontrado) {
		        	if(dto.get(i).getCargoAbono()!=null && dto.get(i).getCargoAbono().trim().equals("E")) {
		        		if(!pbAutomatico)
		        			result.put("msgUsuario","No se encontro clave de Egresos Concepto_Set " + dto.get(i).getConcepto() + " Origen_Mov='' y Corresponde " + dto.get(i).getCorresponde());
		        	}else {
		        		if(!pbAutomatico)
		        			result.put("msgUsuario","No se encontro clave de Ingresos Concepto_Set " + dto.get(i).getConcepto() + " Origen_Mov='' y Corresponde " + dto.get(i).getCorresponde());
		        	}
		        	return result;
		        }
			
		       if(dto.get(i).getDescConcepto().trim().equals("DEP S B COBO")) 
		       	{
		           psSalvoBCobro = "N";
		           psEstatusMov ="P";
		        }
		        else
		        {
		            psEstatusMov = "A";       //siempre es "A" a menos que sea EP SB COBRO
		            psSalvoBCobro = "S";     //siempre es "S" amenos que sea DP S  COBRO
		        }
			     
			        //va por la divisa de la chequera si esdiferente
		        String consDivisa="";
		        if(iNoEmpresa!=dto.get(i).getNoEmpresa() || sChequera!=dto.get(i).getIdChequera()) {
		        	consDivisa=importaBancaElectronicaDao.obtenerDivisa(dto.get(i).getNoEmpresa(),dto.get(i).getIdBanco(),dto.get(i).getIdChequera().trim());
			        if(consDivisa!=null && !consDivisa.equals("")) {
			        	iNoEmpresa = dto.get(i).getNoEmpresa();
			        	sChequera =dto.get(i).getIdChequera();
			        	psDivisa = consDivisa;
			        }else{
			        	if (!pbAutomatico)
			            	result.put("msgUsuario","No existe la chequera: "+dto.get(i).getIdChequera());
			               	return result;
			        }
		        }
			        //'Validar el tipo de operacion para cuando es cheque devuelto
			        //'If mreReglaCon(j.iTpoOperacion = 1015 Then
			        if(piTipoOperacion.equals("1015"))
			       {
			            if(!dto.get(i).getLiberaAut().equals("S"))
			           {
			                 if(!pbAutomatico)
			                	 result.put("msgUsuario","No se puede importar CHEQUE DEVUELTO si el banco no maneja Liberación Automática");
			                //call CmdBuscar_Click
			                 return result;
			                
			            }
			        }
			        
			       //actualiza el folio y despues obtiene el ultimo
			        importaBancaElectronicaDao.actualizarFolioReal("no_folio_param");
			       plFolio = importaBancaElectronicaDao.seleccionarFolioReal("no_folio_param");
			      if(piFormaPago==1) 
			       pdImporteDesglosado = dto.get(i).getImporte();
			       else
			            pdImporteDesglosado = 0;
			       
			      plIdRubro = dto.get(i).getIdRubro();
			      //inerta en parametro
			      psFecValorOriginal = dto.get(i).getFecValorString();
			      //si la fec_valor es menor  hoy no debe afectar chequers hoy
			      psFecValor = psFecValorOriginal;
			      //INICO ************************************ Este codigo viene de cie hay que revisar
			      psClaveOperOrig = piTipoOperacion;

			      
			      List<CatAfiliacionIngresosDto>listAfiliacionIngreso=new ArrayList<CatAfiliacionIngresosDto>();
			      CatAfiliacionIngresosDto dtoParamAfi=new CatAfiliacionIngresosDto();
			      if((dto.get(i).getDescConcepto()!=null&& dto.get(i).getDescConcepto().indexOf("FILIACION")>0)
			    		  ||(dto.get(i).getDescConcepto()!=null & dto.get(i).getDescConcepto().indexOf("INGRESOCH")>0))
			      { 
			            psConceptoDocto = funciones.cadenaRight(dto.get(i).getDescConcepto(),dto.get(i).getDescConcepto().length()-9);
			            psDivision = ""+dto.get(i).getIdDivision();
			            dtoParamAfi=new CatAfiliacionIngresosDto();
			            dtoParamAfi.setNoEmpresa(dto.get(i).getNoEmpresa());
			            dtoParamAfi.setIdBanco(dto.get(i).getIdBanco());
			            dtoParamAfi.setIdChequera(dto.get(i).getIdChequera());
			            dtoParamAfi.setAfiliacion(psConceptoDocto);
			            listAfiliacionIngreso=importaBancaElectronicaDao.obtenerAfiliacionIngresos(dtoParamAfi);
			           if(listAfiliacionIngreso.size()>0)
			            {
			        	 //  System.out.println("afiliacionIngreso");
			           	psConceptoDocto = listAfiliacionIngreso.get(0).getAfiliacion()+"-"+listAfiliacionIngreso.get(0).getConcepto();
			               psNoCliente = listAfiliacionIngreso.get(0).getCuentaPuente();
			               psDivision = listAfiliacionIngreso.get(0).getIdDivision();
			               piTipoOperacion = ""+listAfiliacionIngreso.get(0).getIdTipoOperacion();
			                if(Integer.parseInt(piTipoOperacion.trim())<=0)
			                  piTipoOperacion = psClaveOperOrig;
			            }
			      }
			    
			   
			      else if(dto.get(i).getIdBanco()==2 && (dto.get(i).getDescripcion()!=null && dto.get(i).getDescripcion().indexOf("DEPOSITO X INST TARJ")>0)) //excepcion ya que la descripcion viene nula
			       {
			       //msfDatos.TextMatrix(i, LI_C_ID_ANCO) = 2 And InStr1, msDatos.TetMatrix(i, LIC_DESCRIPCION), "DEPOSITO X INST TARJ") hen 'LU 11072005
			          
			    	  psConceptoDocto = dto.get(i).getFolioBanco();
			            psDivision = ""+dto.get(i).getIdDivision();
			            
			            dtoParamAfi=new CatAfiliacionIngresosDto();
			            dtoParamAfi.setNoEmpresa(dto.get(i).getNoEmpresa());
			            dtoParamAfi.setIdBanco(dto.get(i).getIdBanco());
			            dtoParamAfi.setIdChequera(dto.get(i).getIdChequera());
			            dtoParamAfi.setAfiliacion(psConceptoDocto);
			            listAfiliacionIngreso=importaBancaElectronicaDao.obtenerAfiliacionIngresos(dtoParamAfi);
			           
			           if(listAfiliacionIngreso.size()>0)
			            {
			        	  // System.out.println("afiliacionIngreso 2");
			           	psConceptoDocto = listAfiliacionIngreso.get(0).getAfiliacion()+"-"+listAfiliacionIngreso.get(0).getConcepto();
			               psNoCliente = listAfiliacionIngreso.get(0).getCuentaPuente();
			                psDivision = listAfiliacionIngreso.get(0).getIdDivision();
			               piTipoOperacion = ""+listAfiliacionIngreso.get(0).getIdTipoOperacion();
			               if(Integer.parseInt(piTipoOperacion.trim())<=0)
			                   piTipoOperacion = psClaveOperOrig;
			            }
			       }
			       else if(dto.get(i).getCorresponde()!=null && dto.get(i).getCorresponde().indexOf("MORRALA")>0) 
			      {
			            psConceptoDocto = "DEPOSITO MORRALLAS";
			          psDivision= ""+dto.get(i).getIdDivision();
			      }
			       else
			       
			           psConceptoDocto = dto.get(i).getDescConcepto();
			          psDivision = ""+ dto.get(i).getIdDivision();
			      }
			      
				globalSingleton = GlobalSingleton.getInstancia();
				
				
				  plEmpresa=dto.get(i).getNoEmpresa();
			       //FN ************************************ Este codigo viene de ciehay que revisar
			      ParametroDto dtoInsertaParamUno= new ParametroDto();
			      dtoInsertaParamUno.setNoEmpresa(dto.get(i).getNoEmpresa());
			      
			     // System.out.println("folio param "+plFolio+" empresa---"+dto.get(i).getNoEmpresa());
			      dtoInsertaParamUno.setNoFolioParam(plFolio);
			      dtoInsertaParamUno.setNoDocto(""+piTipoDocto);
			      dtoInsertaParamUno.setPiFormaPago(piFormaPago);
			     // System.out.println("ipoOperacion "+Integer.parseInt(piTipoOperacion));
			      dtoInsertaParamUno.setIdTipoOperacion(Integer.parseInt(piTipoOperacion));
			      //dtoInsertaParamUno.setUsuarioAlta(globalSingleton.getUsuarioLoginDto().getIdUsuario());
			      dtoInsertaParamUno.setUsuarioAlta(2);
			      //dtoInsertaParamUno Val(msfaos.TextMatrix(i, 19))
			      dtoInsertaParamUno.setNoCliente(psNoCliente); 
			      dtoInsertaParamUno.setFecValor(funciones.ponerFechaDate(psFecValor));
			      dtoInsertaParamUno.setFecValorOriginal(funciones.ponerFechaDate(psFecValorOriginal)); 
			      dtoInsertaParamUno.setFecOperacion(importaBancaElectronicaDao.obtenerFechaHoy());
			      dtoInsertaParamUno.setFecAlta(importaBancaElectronicaDao.obtenerFechaHoy());
			      dtoInsertaParamUno.setImporte(dto.get(i).getImporte());
			      dtoInsertaParamUno.setImporteOriginal(dto.get(i).getImporte());
			 
			      dtoInsertaParamUno.setIdCaja(piIdCaja);
			      dtoInsertaParamUno.setIdDivisa(psDivisa);
			      dtoInsertaParamUno.setIdDivisaOriginal(psDivisa);
			      dtoInsertaParamUno.setOrigenReg("BCE");
			      dtoInsertaParamUno.setReferencia(dto.get(i).getReferencia());
			      dtoInsertaParamUno.setConcepto(psConceptoDocto);
			      dtoInsertaParamUno.setAplica(1);
			      dtoInsertaParamUno.setIdEstatusMov(psEstatusMov); 
			      dtoInsertaParamUno.setBSalvoBuenCobro(psSalvoBCobro); 
			      dtoInsertaParamUno.setIdEstatusReg("P");
			      dtoInsertaParamUno.setIdBanco(dto.get(i).getIdBanco());
			      dtoInsertaParamUno.setIdChequera(dto.get(i).getIdChequera());
//			      dtoInsertaParamUno.setIdGrupo(Integer.parseInt(listGrupoRubro.get(0).getIdGrupo()));
			      dtoInsertaParamUno.setIdGrupo(plFolio);
			      dtoInsertaParamUno.setIdRubro(Integer.parseInt(listGrupoRubro.get(0).getIdRubro()));
			      /*
			      if(dto.get(i).getIdCveConcepto().trim().equals("P14"))
			    	  dtoInsertaParamUno.setFolioBanco(!dto.get(i).getReferencia().trim().equals("") ? dto.get(i).getReferencia().trim().substring(dto.get(i).getReferencia().trim().length() - 4) : "");
			      else
			    	  dtoInsertaParamUno.setFolioBanco(!dto.get(i).getDescripcion().trim().equals("") ? dto.get(i).getDescripcion().trim().substring(dto.get(i).getDescripcion().trim().length() - 4) : "");
			      */
			      dtoInsertaParamUno.setFolioBanco(dto.get(i).getFolioBanco());
			      dtoInsertaParamUno.setOrigenMov("BCE");
			      dtoInsertaParamUno.setDescripcion(dto.get(i).getDescripcion());
			      dtoInsertaParamUno.setSecuencia(dto.get(i).getSecuencia());
			      dtoInsertaParamUno.setUsuarioAlta(dto.get(i).getIdUsuario());
			      dtoInsertaParamUno.setIdFormaPago(piFormaPago);
			      dtoInsertaParamUno.setImporteDesglosado(pdImporteDesglosado); 
			      dtoInsertaParamUno.setIdentificador(dto.get(i).getSIdentificador());
			      dtoInsertaParamUno.setValores(dto.get(i).getSValores());
			      //dtoInsertaParamUno.setIdRubro(plIdRubro);
			      dtoInsertaParamUno.setDivision(psDivision);
			      dtoInsertaParamUno.setIdCodigo(psCod);
			      dtoInsertaParamUno.setIdSubcodigo(psSubCod);
			      dtoInsertaParamUno.setBeneficiario(dto.get(i).getBeneficiario()); 
			    //  System.out.println("numero de cuenta"+dto.get(i).getNoCuentaEmp());
//			      dtoInsertaParamUno.setNoCuenta(dto.get(i).getNoCuentaEmp());
			      dtoInsertaParamUno.setNoCuenta(dto.get(i).getSecuencia());
			      dtoInsertaParamUno.setObservacion(dto.get(i).getConcepto());
			      dtoInsertaParamUno.setIdInvCbolsa(dto.get(i).getSecuencia());// checado
			      //dtoInsertaParamUno LI_C_RESTO_REF
			    
			      resultadoInsertUno = importaBancaElectronicaDao.insertarParametroUno(dtoInsertaParamUno);
			      contInsert ++;
			      
			      //Segundo insert
			      ParametroDto dtoInsertaParametroDos= new ParametroDto();
			      if(dto.get(i).getConcepto()!=null && dto.get(i).getConcepto().equals("DEP S B COBRO"))
			      {
			    	  if(dto.get(i).getIdFormaPago()==3 && (dto.get(i).getCargoAbono()!=null && dto.get(i).getCargoAbono().equals("I")))
			    	  {
			    		  importaBancaElectronicaDao.actualizarFolioReal("no_folio_param");
				    	  plFolio = importaBancaElectronicaDao.seleccionarFolioReal("no_folio_param");
				    	  
			    		  dtoInsertaParametroDos.setNoEmpresa(dto.get(i).getNoEmpresa());
			              dtoInsertaParametroDos.setNoFolioParam(plFolio);
			              dtoInsertaParametroDos.setNoDocto(""+piTipoDocto);
			              dtoInsertaParametroDos.setPiFormaPago(piFormaPago);
			              dtoInsertaParametroDos.setIdTipoOperacion(Integer.parseInt(piTipoOperacion));
			              dtoInsertaParametroDos.setUsuarioAlta(globalSingleton.getUsuarioLoginDto().getIdUsuario());
			              dtoInsertaParametroDos.setNoCliente(psNoCliente); 
			              dtoInsertaParametroDos.setFecValor(funciones.ponerFechaDate(psFecValor));
			              dtoInsertaParametroDos.setFecValorOriginal(funciones.ponerFechaDate(psFecValorOriginal)); 
			              dtoInsertaParametroDos.setFecOperacion(importaBancaElectronicaDao.obtenerFechaHoy());
			              dtoInsertaParametroDos.setFecAlta(importaBancaElectronicaDao.obtenerFechaHoy());
			              dtoInsertaParametroDos.setImporte(dto.get(i).getImporte());
			              dtoInsertaParametroDos.setImporteOriginal(dto.get(i).getImporte());
			         
			              dtoInsertaParametroDos.setIdCaja(piIdCaja);
			              dtoInsertaParametroDos.setIdDivisa(psDivisa);
			              dtoInsertaParametroDos.setIdDivisaOriginal(psDivisa);
			              dtoInsertaParametroDos.setOrigenReg("BCE");
			              dtoInsertaParametroDos.setReferencia(dto.get(i).getReferencia());
			              dtoInsertaParametroDos.setConcepto(psConceptoDocto);
			              dtoInsertaParametroDos.setAplica(2);
			              dtoInsertaParametroDos.setIdEstatusMov("H"); 
			              dtoInsertaParametroDos.setBSalvoBuenCobro(psSalvoBCobro); 
			              dtoInsertaParametroDos.setIdEstatusReg("P");
			              dtoInsertaParametroDos.setIdBanco(dto.get(i).getIdBanco());
			              dtoInsertaParametroDos.setIdChequera(dto.get(i).getIdChequera());
			              dtoInsertaParametroDos.setFolioBanco(dto.get(i).getFolioBanco());
			              dtoInsertaParametroDos.setIdGrupo(Integer.parseInt(listGrupoRubro.get(0).getIdGrupo()));
					      dtoInsertaParametroDos.setIdRubro(Integer.parseInt(listGrupoRubro.get(0).getIdRubro()));
			              /*
			              if(dto.get(i).getIdCveConcepto().trim().equals("P14"))
					    	  dtoInsertaParamUno.setFolioBanco(!dto.get(i).getReferencia().trim().equals("") ? dto.get(i).getReferencia().trim().substring(dto.get(i).getReferencia().trim().length() - 4) : "");
					      else
					    	  dtoInsertaParametroDos.setFolioBanco(!dto.get(i).getDescripcion().trim().equals("") ? dto.get(i).getDescripcion().trim().substring(dto.get(i).getDescripcion().trim().length() - 4) : "");
			              */
			              dtoInsertaParametroDos.setOrigenMov("BCE");
			              dtoInsertaParametroDos.setDescripcion(dto.get(i).getDescripcion());
			              dtoInsertaParametroDos.setIdInvCbolsa(dto.get(i).getSecuencia());// checado
			              dtoInsertaParametroDos.setGrupo(1);
			              dtoInsertaParametroDos.setNoFolioMov(1);
			              dtoInsertaParametroDos.setSecuencia(dto.get(i).getSecuencia());
			              dtoInsertaParametroDos.setUsuarioAlta(dto.get(i).getIdUsuario());
			              dtoInsertaParametroDos.setIdFormaPago(piFormaPago);
			              //dtoInsertaParametroDos 0
			              //dtoInsertaParametroDos 0
			              //dtoInsertaParametroDos.setfo plfolio
			              dtoInsertaParametroDos.setImporteDesglosado(pdImporteDesglosado); 
			              //dtoInsertaParametroDos 0
			              dtoInsertaParametroDos.setIdentificador(dto.get(i).getSIdentificador());
			              dtoInsertaParametroDos.setValores(dto.get(i).getSValores());
			              //dtoInsertaParametroD LI_C_HORA_FEC_VALOR
			              //dtoInsertaParametroDos.setIdRubro(plIdRubro);
			              dtoInsertaParametroDos.setDivision(psDivision);
			              dtoInsertaParametroDos.setIdCodigo(psCod);
			              dtoInsertaParametroDos.setIdSubcodigo(psSubCod);
			              dtoInsertaParametroDos.setBeneficiario(dto.get(i).getBeneficiario()); 
			              dtoInsertaParametroDos.setObservacion(psConceptoDocto);
			              
			              resultadoInsertDos=importaBancaElectronicaDao.insertarParametroDos(dtoInsertaParametroDos);
			              if(resultadoInsertDos>0)
			              {
			            	 contInsert++; 
			              	 //importaBancaElectronicaDao.actualizarFolioReal("no_folio_param");
			              }
			    	 }
			      }
			       /*  
			            sql+= " INSERT INTO parametro(1.no_empresa, 2.no_folio_param, 3.id_tipo_docto, ";
				        sql+= " 4.id_forma_pago, 5.id_tipo_operacion, 6.usuario_alta, 7.no_cuenta, 8.no_cliente, ";
				        sql+= " 9.fec_valor, 10.fec_valor_original, 11.fec_operacion, 12.fec_alta, 13.importe, ";
				        sql+= " 14.importe_original, 15.id_caja, 16.id_divisa, 17.id_divisa_original, 18.origen_reg, ";
				        sql+= " 19.referencia, 20.concepto, 21.aplica, 22.id_estatus_mov, 23.b_salvo_buen_cobro, ";
				        sql+= " 24.id_estatus_reg,25.id_banco,26.id_chequera,27.folio_banco,28.origen_mov,29observacion, ";
				        sql+= " 30.id_inv_cbolsa,31grupo,32no_folio_mov,33folio_ref,34id_banco_benef,id_chequera_benef,lote,";
				        sql+= " hora_recibo,division "; 
			            
			'                'inserta en paretro
			                pl_empresa = Val(msfDatos.TextMatrix(i, LI_C_N_EMPRESA))
			                If msfDatos.TextMatix(i, LI_C_ONCEPTO) = "DEP S B COBRO" Then
			                     f Not (piForma_pago = 3 And msfDatos.TextMatix(i, LI_C_CARGO_ABONO) = "I") Then
			                    plfolio1 = gobjVarGobal.Folio_Real("no_folio_param")
			                      lAfectados = gobjSQL.FunSQLInserta2(1.msfDatos.TextMatrix(i LI_C_NO_EMPRESA), _
			                                                          2.pl_folio1, 3.pi_tipo_docto, 4.piForma_pago, _
			                                                          5.pTipo_operacion, 6.CStr(GI_USUARIO), _
			                                                          7.Val(msfDatos.TextMatrix(i, LI_C_NO_CUENTA_EMP)), _
			                                                          8.ps_NoCliente, 9.GT_FECHA_HOY, 10.psFecValorOriginal, _
			                                                          11.GT_FECHA_HOY, 12.GT_FECHA_HOY, _
			                                                          13.Format$(msfDatos.TextMatrix(i, LI_C_IMPORTE), "########0.00"), _
			                                                          14.Format$(msfDatos.TextMatrix(i, LI_C_IMPORTE), "########0.00"), _
			                                                          15.piIdCaja, 16.ps_divisa, 17.ps_divisa, 18."BCE", _
			                                                          19.msfDatos.TextMatrix(i, LI_C_REFERENCIA), _
			                                                           20.psConceptoDocto, _
			                                                           21.2, 22."H", 23.ps_alvo_b_cobro, 24."P", _
			                                                           25.msfDatos.TextMatrix(, LI_C_ID_BANCO), _
			                                                           26.Trim$(msfDatos.TextMatrx(i, LI_C_ID_CHEQUERA)), _
			                                                          27.Trim$(msfDatos.TextMatrix(i, LI_C_FOLIO_BANCO)), _
			                                                          28.psOrigen_mov, Trim$(msfDatos.TextMatrix(i, LI_C_CONCEPTO2)), _
			                                                          29.Trim$(msfDatos.TextMatrix(i, LI_C_SECUENCIA2)), _
			                                                          30pl_folio, 31 1, 32 1, msfDatos.TextMatrix(i, LI_C_ID_BANCO), _
			                                                          Trim$(msfDatos.TextMatrix(i, LI_C_ID_CHEQUERA)), 0, _
			                                                          msfDatos.TextMatrix(i, LI_C_IDENTIFICADOR), _
			                                                          msfDatos.TextMatrix(i, LI_C_VALORES), _
			                                                          msfDatos.TextMatrix(i, LI_C_HORA_FEC_VALOR), _
			                                                          plIdRubro, Trim(msfDatos.TextMatrix(i, LI_C_ID_DIVISION)), _
			                                                          psCod, psSubCod, msfDatos.TextMatrix(i, LI_C_BENEFICIARIO), _
			                                                         msfDatos.TextMatix(i, LI_C_RESTO_REF))
			                       EndIf
			               End If
			                DEvents*/
			           //'llamada al generador
			            pdResp =null;
			            
			           //'**********Llamada STORED DE IMPORTA BANCA_ELECTRONICA PARA CCM
			            if (importaBancaElectronicaDao.consultarConfiguraSet(1).equals("CCM")) 
			           {
			                //Call gobjSQL.inserta_dato(CInt(l_empresa), pl_folio)
		            		importaBancaElectronicaDao.ejecutarStored("sp_datos_importa");//por el momento no se realiza
			           }
			           else
			           {
			        	   pdResp = null;
			        	   GeneradorDto generadorDto = new GeneradorDto();
			        	   generadorDto.setEmpresa(plEmpresa);
			        	   generadorDto.setFolDeta(pdFolioDet);
			        	   generadorDto.setFolMovi(pdFolioMov);
			        	   generadorDto.setFolParam(plFolio);
			        	 //  System.out.println("datos del generador "+plEmpresa+"-"+pdFolioDet+"-"+pdFolioMov+plFolio);
			        	   pdResp = importaBancaElectronicaDao.ejecutarGenerador(generadorDto);
			           }
			           
			            System.out.println("Mensaje de generador: " + pdResp.get("result").toString());
			            
			            if(pdResp.size() > 0) {
			            	if(pdResp.get("result") != null) {
					            if(Integer.parseInt(pdResp.get("result").toString()) != 0) {
					            	contInsert--;
					            	if(Integer.parseInt(pdResp.get("result").toString()) != 1053) {
					            		if (!pbAutomatico) {
					            			//logger.info("Error en generador");//MsgBox "Error #" & CStr(pd_resp) & " enGenerador" & Chr(13 & _" no_folio_param = " & Ctr(pl_folio), vbCritical, "SET"
					            			result.put("msgUsuario","Error " + pdResp.get("result") + " enGenerador " + " no_folio_param = " + plFolio);
					            		}
					            		return result;
					            	}
					            } else { 
					            	//importaBancaElectronicaDao.actualizaMovtoBancaE(dto.get(i).getSecuencia());
					            }
			            	}
			            }
			            
			           //'Actualizar estatus de si es Ingreso por Compra/venta de divisas
			           if(dto.get(i).getCorresponde()!=null && dto.get(i).getCorresponde().equals("D"))
			            {
			               importaBancaElectronicaDao.actualizarCompraVenta(dto.get(i).getIdBanco(),dto.get(i).getIdChequera(),dto.get(i).getImporte(),dto.get(i).getNoEmpresa(),pdFolioDet);
			            }
			           
			            //'Trato especial para RECHAZOS
			           ParamRevividorDto dtoRevividor = new ParamRevividorDto();
			           String mensaje="";
			           if(dto.get(i).getBRechazo()!=null && dto.get(i).getBRechazo().equals("S"))
			            {
			                  psOperacionDev =importaBancaElectronicaDao.consultarConfiguraSet(210);
			                  //'R-->Revivir solicitud  , C-->Cancelar pago
			                  dtoRevividor.setIdBanco(dto.get(i).getIdBanco());
			                  dtoRevividor.setIdChequera(dto.get(i).getIdChequera());
			                  dtoRevividor.setImporte(dto.get(i).getImporte());
			                  dtoRevividor.setPsOperacion(psOperacionDev);
			                  dtoRevividor.setPlFolioRechazado(pdFolioDet);//Este campo viene referenciado del store checar
			                  dtoRevividor.setReferencia(dto.get(i).getReferencia());
			                  
			                  if ((psOperacionDev!=null && psOperacionDev.equals("R"))|| (psOperacionDev!=null && psOperacionDev.equals("C")))
			                      	mensaje=revivirSolicitudPago(dtoRevividor);
			                  if(!mensaje.equals("1") && !mensaje.equals(""))
			                	  result.put("msgUsuario", mensaje);
			            }
			           if(sinRegistrar)
			           {
			        	   if(!pbAutomatico)
			        	   {
			        		   result.put("msgUsuario","Existen conceptos que no deben ser importados");
			        		   return result;
			        	   }
			           }
			}else {
				result.put("msgUsuario","Este registro que esta trando de importar ya fue importado: " + dto.get(i).getImporte());
     		   return result;
			}
		}//Terina FOR
	}catch(Exception e){
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P:Banca Electronica, C:ImportaBancaElectronicaBusiness, M:consultarMovimientoBancaE");
		e.printStackTrace();
	} 
		System.out.println("registros "+contInsert);
    result.put("regImportados",contInsert); 
   return result;
}
	
	
	
	/*Function valida_referencia_porChequera(ByVal psReferencia As String, ByVal plNoEmpresa =0;, _
    ByVal piBancoAs Integer, ByVal psChequera As String, _
    ByRef plIdRubro As Long, ByRf plNoCliete As Long, _
    ByRef psDivision As String, ByRef psBeneficiario As tring, _
    ByRef psRestoRef As Strig, _
    Optional ByRef piLongitud As Integer, _
    Optional Byef psCorrespondeAs String, _
    Optional ByRef psFecValor As String) As Boolean*/

public boolean validarReferenciaPorChequera(ParamValidaRefCheqDto dtoValidar)
{
	int piContador;
	int piDigAdicional=0;
	int piLongitud=0;
	int plIdRubro=0;
	int plNoCliente = 0;
	int plNoEmpresa=dtoValidar.getPlNoEmpresa();
	int i=0;
	boolean pbBandera=false;
	boolean pbBuscaNoCliente=false;;
	boolean pbBuscaDivision=false;
	boolean pbSinValidarCliente=false;
	boolean pbFecha=false;
	boolean pbPertenencia=false;
	boolean pbValidaRef=false;
	boolean pbProveedor=false;
	boolean pbParque=false;
	boolean pbExitWhile=false;
	String  psRestoRef=dtoValidar.getPsRestoRef();
	String  psAxiliar=dtoValidar.getPsReferencia().trim();
	String  psFormato;
	String  psCaracterFormato;
	String  psCaracterReferencia;
	String  psEquivalePersona="";
	String  psValorDefault="";
	String  psCorresponde=dtoValidar.getPsCorresponde();
	String  psBeneficiario = "";
	String  psFecValor=dtoValidar.getPsFecValor();
	dtoValidar.setPlNoCliente(0);
	dtoValidar.setPsDivision("");
	String psDivision=dtoValidar.getPsDivision();
	CatReferenciaChequeraDto dtoRefCheq=new CatReferenciaChequeraDto();
	String psReferencia = psAxiliar;
	dtoRefCheq.setNoEmpresa(dtoValidar.getPlNoEmpresa());
	dtoRefCheq.setIdBanco(dtoValidar.getPiBanco());
	dtoRefCheq.setIdChequera(dtoValidar.getPsChequera());
	//Buscar la chequera en la tabla de configuracion de referencias por chequera
	List<CatReferenciaChequeraDto>listRefCheq=importaBancaElectronicaDao.obtenerReferenciaChequera(dtoRefCheq);
	
	while(i<=listRefCheq.size()){
		//Comparar contra la referencia para determinar si trae el formato correcto
	            //checamos la bandera de fecha y pertenencia
	            
				if((listRefCheq.get(i).getBFecha()==null?"":listRefCheq.get(i).getBFecha()).equals("S"))
					pbFecha=true;
				else
					pbFecha=false;
	            if((listRefCheq.get(i).getBPertenencia()==null?"":listRefCheq.get(i).getBPertenencia()).equals("S"))
	            	 pbPertenencia = true;
	            else
	 	             pbPertenencia = false;
	            if((listRefCheq.get(i).getBProveedor()==null?"":listRefCheq.get(i).getBProveedor()).equals("S"))
	                pbProveedor = true;
	            else
	                pbProveedor = false;
	            if(!(listRefCheq.get(i).getValorDefault()==null?"":listRefCheq.get(i).getValorDefault()).equals(""))
	                psValorDefault = listRefCheq.get(i).getValorDefault();
	            else
	                psValorDefault = "";
	            if((listRefCheq.get(i).getBParque()==null?"":listRefCheq.get(i).getBParque()).equals("S"))
	                pbParque = true;
	            else
	                pbParque = false;
	            if(listRefCheq.get(i).getLongCliente()==-1)
	            {
            	  pbBuscaNoCliente = true;
	              pbSinValidarCliente = true;
	            }
	            else if(listRefCheq.get(i).getLongCliente()>0)
	            {
	                pbBuscaNoCliente = true;
	                if(psReferencia.length()>=listRefCheq.get(i).getLongCliente())
	                {
	                    psEquivalePersona=psReferencia.substring((listRefCheq.get(i).getInicioCliente()<=0?1:listRefCheq.get(i).getInicioCliente()),listRefCheq.get(i).getLongCliente());
	                    piDigAdicional=(listRefCheq.get(i).getInicioCliente()<=0?1:listRefCheq.get(i).getInicioCliente())-1;
	            	}
	                pbSinValidarCliente = false;
	            }
	            
	            if(listRefCheq.get(i).getBDivision().equals("S"))
	                pbBuscaDivision = true;
	            
	            piLongitud = listRefCheq.get(i).getLongitud();
	            if(listRefCheq.get(i).getFormato().equals("%"))//comodin
	            {
	                pbBandera = true;
	                plIdRubro =listRefCheq.get(i).getIdRubro();
	                break;
	            }
                else
                {
	                pbBandera = true;
	//                    If piBanco = BANAMEX Then
	//                       If Len(psReferencia) > Val(!Longitud & "") Then
	//                            psReferencia = Right(psReferencia, Val(!Longitud & ""))
	//                        End If
	//                    Else
	                psReferencia =psReferencia.trim(); //valida_longitud_maxima(psReferencia, Val(!Longitud & ""))
	//                   End If
	                if(psReferencia.length()==listRefCheq.get(i).getLongitud())
	                {
	                    //Validar el formato
	                    psFormato = listRefCheq.get(i).getFormato();
	                    for(piContador=0;i<=psFormato.length();piContador++)
	                    {
	                        psCaracterFormato = psFormato.substring(piContador,piContador+1);
	                        psCaracterReferencia = psReferencia.substring(piContador,piContador+1);
	                        if(psCaracterFormato.equals("0"))
	                        {
	                        	if((Integer.parseInt(psCaracterReferencia))!=0)
	                        		pbBandera=false;
	                        }
	                        else if(psCaracterFormato.equals("9"))
	                        {
	                        	if(!funciones.isNumeric(psCaracterReferencia))
	                        		pbBandera=false;
	                        }
	                        else if(psCaracterFormato.equals("A"))
	                        {
	                        	if(!funciones.isNumeric(psCaracterReferencia))
	                        	{
	                        		if(funciones.comparaMayusculas(psCaracterReferencia))
	                        			pbBandera=false;
	                        	}
	                        		
	                        }
	                        if(!pbBandera) 
	                            break;
	                        
	                    }
	                    plIdRubro = listRefCheq.get(i).getIdRubro(); 
	                    if(pbBandera)
	                        break;
	                }else
	                {
	                	pbBandera = false;
	                	break;
	                }
                }
      
	}
	 //si el formato de la referencia completa es correcto la pbBandera = true ejm. '9999999999'
	    if(pbBandera)
	    {
	        if(pbParque)
	        {
	            //Valida inmueble para sacar divisiï¿½n
	            if(pbBuscaDivision)
	            {    
	            	int idDivision=0;
	            	idDivision=importaBancaElectronicaDao.buscarInmuebleParque(dtoValidar.getPlNoEmpresa(),psReferencia.substring(0,2));
	                
	                if (idDivision>0)
	                {
	                   psDivision =""+idDivision;
	                }
	                else
	                {
	                   plNoCliente = 0;
	                   psBeneficiario = "";
	                   pbBandera = false;
	                }
	                
	            }
	            else
	            {
	                psDivision = "";
	            }
	            // Valida el cliente
	            if(!psEquivalePersona.equals(""))
	            {   
	                if(importaBancaElectronicaDao.consultarConfiguraSet(1).equals("CIE")) 
	                    psEquivalePersona = "0"+psEquivalePersona;
	            }
	            else
	                psEquivalePersona = psReferencia.substring(4,13);
	            if((!pbSinValidarCliente && !pbBuscaDivision && psDivision.equals(""))||
	               (!pbSinValidarCliente && pbBuscaDivision && !psDivision.equals(""))) 
	            {
	            	List<PersonasDto>listClientes=new ArrayList<PersonasDto>();
                	PersonasDto personasDto=new PersonasDto();
                	personasDto.setEquivalePersona(psEquivalePersona);
                	personasDto.setNoEmpresa(dtoValidar.getPlNoEmpresa());
	                if(pbProveedor)
	                {
	                    //Busca los proveedores
	                	listClientes=importaBancaElectronicaDao.buscarCliente(personasDto,true);
	                    if(listClientes.size()>0)
	                    {
	                        plNoCliente =listClientes.get(0).getNoPersona();
	                        psBeneficiario =listClientes.get(0).getRazonSocial();
	                        psCorresponde = "P";
	                    }//Otros artï¿½culos similares:
	                    else
	                    {
	                        plNoCliente = 0;
	                        psBeneficiario = "";
	                        psCorresponde = "";
	                    }
	                }
	                if(pbParque && (psReferencia.substring(2,4)).equals("09"))//Morrayas
	                {
	                	listClientes=importaBancaElectronicaDao.buscarCliente(personasDto,true);
	                    if(listClientes.size()>0)
	                    {
	                        plNoCliente = listClientes.get(0).getNoPersona();
	                        psBeneficiario = listClientes.get(0).getRazonSocial();
	                        psCorresponde = "M";
	                    }
	                    else
	                    {
	                        plNoCliente = 0;
	                        psBeneficiario = "";
	                        psCorresponde = "";
	                    }
	                }
	                if(plNoCliente==0 && psBeneficiario.equals(""))
	                {
	                    //Buscar el no_cliente y su division
	                	listClientes=importaBancaElectronicaDao.buscarCliente(personasDto,false);
	                    if(listClientes.size()>0)
	                    {
	                        plNoCliente =listClientes.get(0).getNoPersona();
	                        psBeneficiario = listClientes.get(0).getRazonSocial();
	                    }
	                    else //no encontrï¿½ al cliente en persona
	                    {
	                        pbBandera = false;
	                        plNoCliente = 0;
	                        psBeneficiario = "";
	                    }
	                }
	                
	            }
	            
	            
	            //Obtiene la fecha para aplicar el movimiento
	            if(pbFecha)
	            {   
	            	psFecValor=psReferencia.substring(14,16)+"/"+
	            	psReferencia.substring(16,18)+"/"+
	            	psReferencia.substring(18,20);
	            }
	            else	            
	                psFecValor = "";	            
	            if((pbParque) && (psReferencia.substring(2,4).equals("09")))
	            	psCorresponde="M";
	        }
	        else if (pbBuscaNoCliente) //Busca al cliente si la longitud del cliente es mayor a 0
	        {
	            //No valida el cliente ya que no existe en SET
	            if(pbSinValidarCliente)
	            {
	                plNoCliente = Integer.parseInt(psReferencia.substring(0,8));
	                if(pbProveedor)
	                    psCorresponde = "P";
	                else
	                    psCorresponde = "";
	                
	                if(pbBuscaDivision)
	                {
	                    //Set prTabla = gobjSQL.FunSQLSelect_cliente_division_sin_validar(plNoCliente)
	                    List<CatDivisionDto>listCliDivSinValidar=new ArrayList<CatDivisionDto>();
	                    listCliDivSinValidar=importaBancaElectronicaDao.obtenerClienteDivSinValidar(plNoCliente);
	                    if(listCliDivSinValidar.size()>0)
	                    {
	                        psDivision = listCliDivSinValidar.get(0).getIdDivision();
	                        psBeneficiario = listCliDivSinValidar.get(0).getDescDivision();
	                        psCorresponde = "A";
	                        if(!psValorDefault.equals(""))
	                            plNoCliente = Integer.parseInt(psValorDefault);
	                    }
	                    else
	                    {
	                         //aqui es en donde buscamos en imueble_division si
	                         // el cliente no esta en cliente_division
	                         if((psReferencia.trim().length())>=12)
	                         {
		                            //Set prInmueble = gobjSQL.FunSQLSelect_busca_inmueble(dtoValidar.getPlNoEmpresa(),psReferencia, piDigAdicional)
                        	 int idDivision=0;
                        	 idDivision=importaBancaElectronicaDao.buscarInmueble(dtoValidar.getPlNoEmpresa(), psReferencia, piDigAdicional);
	                            if(idDivision>0)
	                               psDivision = ""+idDivision;
	                            else
	                            {
	                               plNoCliente = 0;
	                               psBeneficiario = "";
	                               pbBandera = false;
	                            }
	                         }
	                         else
	                         {
	                            plNoCliente = 0;
	                            psBeneficiario = "";
	                            pbBandera = false;
	                         }
	                    }
	                }
	                else
	                {
	                    plNoCliente = 0;
	                    psBeneficiario = "";
	                    pbBandera = false;
	                }
	                
	            }
	              
	            //con validar cliente
	            else if(!psEquivalePersona.equals(""))
	            {
	                if(importaBancaElectronicaDao.consultarConfiguraSet(1).equals("CIE"))
	                	psEquivalePersona = "0" + psEquivalePersona;
	               if(pbProveedor) 
	               {
	            	   PersonasDto dtoPersonas=new PersonasDto();
	            	   List<PersonasDto> listPersonas=new ArrayList<PersonasDto>();
	                   dtoPersonas.setEquivalePersona(psEquivalePersona);
	                   dtoPersonas.setNoEmpresa(plNoEmpresa);
	                   //Busca los proveedores
	                   listPersonas=importaBancaElectronicaDao.buscarCliente(dtoPersonas,true);
	                   if(listPersonas.size()>0)
	                    {
	                        plNoCliente = listPersonas.get(0).getNoPersona();
	                        psBeneficiario = listPersonas.get(0).getRazonSocial();
	                        psCorresponde = "P";
	                    }
	                    else
	                    {
	                        plNoCliente = 0;
	                        psBeneficiario = "";
	                        psCorresponde = "";
	                    }
	                    
	                    if(pbBuscaDivision && plNoCliente>0)
	                    {
	                    	List <CatDivisionDto>listClienteDivSinValidar= new ArrayList<CatDivisionDto>();
	                    	listClienteDivSinValidar=importaBancaElectronicaDao.obtenerClienteDivSinValidar(plNoCliente);
	                    	if(listClienteDivSinValidar.size()>0)
	                            psDivision = listClienteDivSinValidar.get(0).getIdDivision();//aqui obtenemos la fecha y la pertenencia o no ??????????
	                        else
	                        {
	                            //aqui es en donde buscamos en inmueble_division si
	                            //el cliente no esta en cliente_division
	                            
	                        	if(psReferencia.length()>=12)
	                        	{
	                        		 int idDivision=0;
	                            	 idDivision=importaBancaElectronicaDao.buscarInmueble(dtoValidar.getPlNoEmpresa(), psReferencia, piDigAdicional);
	                                if(idDivision>0)
	                                   psDivision =""+idDivision;//aqui obtenemos la fecha y la pertenencia o no ???????????
	                                else
	                                {
	                                    pbBandera = false;  //lo pone en No identificado debido a que no se encontro el inmueble
	                                    plNoCliente = 0;
	                                    psBeneficiario = "";
	                                }
	                        	}
	                        	else
	                        	{
	                                pbBandera = false; //lo pone en No identificado debido a que la referencia es incorrecta
	                                plNoCliente = 0;
	                                psBeneficiario = "";
	                        	}
	                        }
	                    }          
	               }
	                
	                if(plNoCliente==0 && psBeneficiario.equals(""))
	                {
	                    //Buscar el no_cliente y su division
	                	PersonasDto dtoPersonas=new PersonasDto();
	            	    List<PersonasDto> listPersonas=new ArrayList<PersonasDto>();
	                    dtoPersonas.setEquivalePersona(psEquivalePersona);
	                    dtoPersonas.setNoEmpresa(plNoEmpresa);
	                    listPersonas=importaBancaElectronicaDao.buscarCliente(dtoPersonas,false);
	                    if(listPersonas.size()>0)
	                    {
	                        plNoCliente = listPersonas.get(0).getNoPersona();
	                        psBeneficiario = listPersonas.get(0).getRazonSocial();
	                        if(pbBuscaDivision)
	                        {
	                            List<CatDivisionDto>listCliDivSinValidar= new ArrayList<CatDivisionDto>();
	                            listCliDivSinValidar=importaBancaElectronicaDao.obtenerClienteDivSinValidar(plNoCliente);
	                            if(listCliDivSinValidar.size()>0)
	                                psDivision = listCliDivSinValidar.get(0).getIdDivision();//aqui obtenemos la fecha y la pertenencia o no ??????????
	                            else
	                            {
	                                // aqui es en donde buscamos en inmueble_division si
	                                //el cliente no esta en cliente_division
	                                if(psReferencia.length()>=12)
	                                {
	                                	int idDivision=0;
   	                            	    idDivision=importaBancaElectronicaDao.buscarInmueble(dtoValidar.getPlNoEmpresa(), psReferencia, piDigAdicional);
	                                    if(idDivision>0)
	                                       psDivision = ""+idDivision;//aqui obtenemos la fecha y la pertenencia o no ???????????
	                                       
	                                    else
	                                    {
	                                        pbBandera = false;  //lo pone en No identificado debido a que no se encontro el inmueble
	                                        plNoCliente = 0;
	                                        psBeneficiario = "";
	                                    }
	                                }
	                                else
	                                {
	                                    pbBandera = false; //lo pone en No identificado debido a que la referencia es incorrecta
	                                    plNoCliente = 0;
	                                    psBeneficiario = "";
	                                }
	                            }
	                        }
	                    }
	                    else //no encontrï¿½ al cliente en persona
	                    {
	                        pbBandera = false;
	                        plNoCliente = 0;
	                        psBeneficiario = "";
	                    }
	                }
	            }
	        
	            if(psReferencia.length()>12)
	                psRestoRef = psReferencia.substring(12+piDigAdicional);
	          
	        }
	    }
	    //validaReferenciaPorChequera = pbBandera;
	    
	    return pbBandera;
}

/*
 'Determina el corresponde al que pertenece la referencia
'Devuelve como valor el id_corresponde asociado a la referencia
'si no determina su valor regresa "N" = No identificado

Public Function DeterminaCorresponde(ByVal psReferencia As String, ByVal plNoEmpresa As Long, _
            ByVal plIdBanco As Long, ByVal psIdChequera As String, _
            ByRef pmasReferencia As Variant, ByRef iIdCajaEmp As Integer, ByRef sIdCodigo As String, _
            ByRef sIdSubCodigo As String) As String*/
	public String determinarCorresponde(ParamDeterminaCorrespondeDto dto)
	{
		//Dim rstTipoIngreso As ADODB.Recordset
	    //Dim rstValorBuscado As ADODB.Recordset  Para los valores de referencia buscados en DB
	    String strCorresponde="";
	    boolean bolCumpleRegla=false;
	    String strValor="";         //El valor a evaluar
	    String strPreCte="";        //Valor prefijo de todos los clientes en la tabla persona
	    String [][]arregloMasReferencia;
	    //Si la regla incluye codigo y subcodigo es necesario _
	    //conservar el codigo para validar el codigo y el subcodigo _
	    //para cuando se pide primero validar el subcodigo
	    String strCodigo="";
	    String strSubCodigo="";
	    //Determina si esta pendiente de validar el subcodigo
	    boolean bolPendienteSubCodigo=false;
	    String  psRefOriginal="";
	    //Si la regla incluye empresa es necesario evaluar al _
	    //cliente con la clave de la empresa contenida en la _
	    //referencia, sino con la clave de la empresa donde se _
	    //hizo el deposito
	    int piNoEmpresa=0;
	    String psNoCliente="";
	    String valorConfiguraSet="";
	    String retDeterminarCorresponde="";
	    //Determina si esta pendiente de evaluar el cliente
	    boolean bolPendienteCliente=false;
	    List<ParamDeterminaCorrespondeDto>listTipoIngreso=new ArrayList<ParamDeterminaCorrespondeDto>();
	    ParamDeterminaCorrespondeDto dtoParamTipoI=new ParamDeterminaCorrespondeDto();
	    valorConfiguraSet=importaBancaElectronicaDao.consultarConfiguraSet(363);
	    retDeterminarCorresponde="N";
	    psRefOriginal = dto.getSReferencia();
	    //Obtiene los corresponde asociados a la chequera
	    dtoParamTipoI.setLongReferencia(dto.getSReferencia().length());
	    dtoParamTipoI.setNoEmpresa(dto.getNoEmpresa());
	    dtoParamTipoI.setIdBanco(dto.getIdBanco());
	    dtoParamTipoI.setIdChequera(dto.getIdChequera());
	    listTipoIngreso=importaBancaElectronicaDao.obtenerTipoIngreso(dtoParamTipoI);
	                                                  
	    //Si no hay corresponde termina la validaciï¿½n
	    if(listTipoIngreso.size()>0){
	        //System.exit(0); //Exit function
	    //Valida el digito verificador. SE ASUME QUE LA CHEQUERA USA UNA SOLA BASE
	    //Cuanto se agreguen otras formas de calculo se debe direcionar a cada funcion
	    if(listTipoIngreso.get(0).getBaseCalculo()!=null && listTipoIngreso.get(0).getBaseCalculo().equals("b10"))
	    {
	    	if(dto.getSReferencia()!=null && funciones.isNumeric(dto.getSReferencia().trim())==true)
	    	{
	    		//Se agrega condicion para quitar el punto si es el final de la cadena y poder usar la funcion CByte
	    		if((dto.getSReferencia().substring(dto.getSReferencia().length(),dto.getSReferencia().length()+1).equals(".")) 
	    			|| (dto.getSReferencia().substring(dto.getSReferencia().length(),dto.getSReferencia().length()+1).equals(",")))
	    			dto.setSReferencia(dto.getSReferencia().substring(0,dto.getSReferencia().length()-1));
	    		if(!(dto.getSReferencia().substring(dto.getSReferencia().length(),dto.getSReferencia().length()+1))
	    			.equals((funciones.calcularDigito(dto.getSReferencia().substring(0,dto.getSReferencia().length()-1)))))
	    		{}//System.exit(0);
	    	}
	    	else{}
	    		//System.exit(0);
	    }
	   
	    //BUSCA EL CORRESPONDE
	    //Cadena que forma la Referencia. Una posiciï¿½n para cada elemento
	    //Empresa, Cliente, Codigo, SubCodigo, Division, Constante1, Constante2, Constante3
	    //Para cada elemento se graba el nombre del elemento
	    arregloMasReferencia=new String[3][11];
	    strCorresponde = ""; //Corresponde en revisiï¿½n
	    bolCumpleRegla = false;
	    int cont=0;
	    while (listTipoIngreso.size()>=cont)
	    {
	        //Para cada caso de Corresponde
	        if(!strCorresponde.equals(listTipoIngreso.get(cont).getIdCorresponde()!=null?listTipoIngreso.get(cont).getIdCorresponde():""))
	        {
	            if(bolCumpleRegla)
	                break;
	            dto.setSReferencia(psRefOriginal);
	            strCorresponde =listTipoIngreso.get(cont).getIdCorresponde();
	            strCodigo = "";
	            strSubCodigo = "";
	            bolPendienteSubCodigo = false;
	            if(listTipoIngreso.get(cont).getOrdenEmpresa()==0)
	                piNoEmpresa = dto.getNoEmpresa();
	            else
	            piNoEmpresa = 0;
	            psNoCliente = "";
	            bolPendienteCliente = false;
	            bolCumpleRegla = true;
	            //LimpiaReferencia pmasReferencia
	        }
	            
	        else
	        {
	            if(bolCumpleRegla) //Mientras no falle la regla se sigue revisando
	            {
	                //El valor a evaluar
	                strValor =dto.getSReferencia()!=null?dto.getSReferencia().substring(0,listTipoIngreso.get(cont).getLongitud()):"";
	                if(!strValor.equals(dto.getSReferencia()!=null?dto.getSReferencia():""))
	                    dto.setSReferencia(dto.getSReferencia().substring(listTipoIngreso.get(cont).getLongitud()+1));
	                arregloMasReferencia[0][listTipoIngreso.get(cont).getOrden()]=listTipoIngreso.get(cont).getCampo();//pmasreferencia cambie por arregloMasReferencia
	                arregloMasReferencia[1][listTipoIngreso.get(cont).getOrden()]=strValor;
	                arregloMasReferencia[2][listTipoIngreso.get(cont).getOrden()]=listTipoIngreso.get(cont).getDestinoEmpresa();
	                List<EmpresaDto>listObtenerCuentaEmpresa=new ArrayList<EmpresaDto>();
	                
	                if(listTipoIngreso.get(cont).getCampo().equals("EMPRESA"))
	                {
	                	piNoEmpresa=Integer.parseInt(strValor);
	                	listObtenerCuentaEmpresa=importaBancaElectronicaDao.obtenerCuentaEmpresa(piNoEmpresa);
	                	if(listObtenerCuentaEmpresa.size()<=0)
	                		bolCumpleRegla=false;
	                	else
	                		if(!bolPendienteCliente)
	                		{
	                			/* Set rstValorBuscado = gobjSQL.FunSQLComboDepositosPersonas(piNoEmpresa, _
	                                    psNoCliente, IIf(Mid(psNoCliente, 1, Len(strPreCte)) = strPreCte, "C", "P"), "I")
	                                If rstValorBuscado.EOF And gobjVarGlobal.valor_Configura_set(361) <> "SI" Then
	                                    bolCumpleRegla = False
	                                End If*/
	                		}
	                }
	                else if(listTipoIngreso.get(cont).getCampo().equals("CLIENTE"))
	                {
	                	if(piNoEmpresa==0)
	                	{
	                		psNoCliente = strValor;
                        	bolPendienteCliente = true;
	                	}
                        else
                        {
	                        /*Set rstValorBuscado = gobjSQL.FunSQLComboDepositosPersonas(piNoEmpresa, _
	                            strValor, IIf(Mid(strValor, 1, Len(strPreCte)) = strPreCte, "C", "P"), "I")
	                        If rstValorBuscado.EOF And gobjVarGlobal.valor_Configura_set(361) <> "SI" Then
	                            bolCumpleRegla = False
	                        End If*/
                        }
	                }
	                else if(listTipoIngreso.get(cont).getCampo().equals("CODIGO"))
	                {
	                	strCodigo = strValor;
                        /*Set rstValorBuscado = gobjSQL.FunSQLSelect866(strValor, 1)
                        if rstValorBuscado.EOF Then
                            bolCumpleRegla = False
                        else
                            If bolPendienteSubCodigo Then
                                Set rstValorBuscado = gobjSQL.FunSQLSelect865(strValor, strSubCodigo, 1)
                                If rstValorBuscado.EOF Then
                                    bolCumpleRegla = False
                                End If
                            End If
                        End If*/
	                }
	                else if(listTipoIngreso.get(cont).getCampo().equals("SUBCODIGO"))
	                {
	                	if(strCodigo.equals(""))
	                	{
                        	strSubCodigo = strValor;
                        	bolPendienteSubCodigo = true;
	                	}
	                    else
	                    {
	                        /*Set rstValorBuscado = gobjSQL.FunSQLSelect865(strCodigo, strValor, 1)
	                        If rstValorBuscado.EOF Then
	                            bolCumpleRegla = False
	                        End If*/
	                    }
	                }
	                else if(listTipoIngreso.get(cont).getCampo().equals("DIVISION"))
	                {
	                	/*Set rstValorBuscado = gobjSQL.FunSQLSelect867(plNoEmpresa, strValor)
                        If rstValorBuscado.EOF Then
                            bolCumpleRegla = False
                        End If*/
	                }
	                else if(listTipoIngreso.get(cont).getCampo().equals("VAR1")
	                		||listTipoIngreso.get(cont).getCampo().equals("VAR2")
	                		||listTipoIngreso.get(cont).getCampo().equals("VAR3"))
	                {
	                	 // MS Nada ya que son variables puede traer lo que sea
                         //  siempre y cuando se cumpla con el digito
	                }
	                else//Para el manejo de las 3 constantes
	                {
	                	if(Integer.parseInt(strValor)!=listTipoIngreso.get(cont).getValor())
	                		bolCumpleRegla = false;
	                }
	            }
	        }
	        cont++;
	    }
	   }
	   if(bolCumpleRegla)
	        //iIdCajaEmp = piNoEmpresa;
	        dto.setSCodigo(strCodigo);
	        dto.setSSubCodigo(strSubCodigo);
	        //DeterminaCorresponde = strCorresponde;
	   /*else
	        dto.setLimpiaReferencia pmasReferencia;*/
	        return strCorresponde;
	    
	}
	public boolean importarPendientes(List<ParamImportarPendientesDto> listBancos,String fecInicial,String fecFinal, String idUsuario, boolean lbImportaTodosBancos)
	{
		boolean importados=false;
	    //Dim prTabla As ADODB.Recordset
	    //Dim rstDatos As ADODB.Recordset
	    //Dim rstValidaImp As ADODB.Recordset
	    int i=0;
	    String psSalvo="";
	    String psConcepto="";
	    int iEncabezado=0;
	    int iEmpresaAnt=0;
	    int iEmpresa=0;
	    int iIniciaEn=0;
	    int iTerminaEn=0;
	    String sReferencia="";
	    String sCorresponde ="";
	    String sImporta ="";
	    String sDescCorresponde ="";
	    String sIdentificador ="";
	    String sValores ="";
	    int iRenglon =0;
	    int piRespueata =0;
	    String sNoCliente ="";
	    boolean pbEncontrado=false;
	    int piRenglon =0;
	    String lclSrutalogo ="";
	    boolean pbImporta=false;
	    int plIdRubro =0;
	    String psIdDivision ="";
	    int plNoCliente =0;
	    String psBeneficiario ="";
	    String psDescripcion ="";
	    int piContadorBanco =0;
	    int piBanco =0;
	    String psDescBanco ="";
	    String psRestoRef ="";
	    String validaImp="";
	    boolean lbReferenciaPorChequera=false;
	    ParamRetImpBancaDto dtoImportarMov=new ParamRetImpBancaDto();
    /*
    lbImportaTodosBancos = False
    frmRangoImpPend.Show vbModal
    
    If lbImportaTodosBancos = True Then
        piRespueata = MsgBox(" ï¿½ Deseas realizar la importaciï¿½n de todos los movimientos pendientes de TODAS las empresas de TODOS los Bancos ?", vbYesNo + vbDefaultButton2, "SET")
    Else
        piRespueata = MsgBox(" ï¿½ Deseas realizar la importaciï¿½n de todos los movimientos pendientes de TODAS las empresas ?", vbYesNo + vbDefaultButton2, "SET")
    End If
    
    If piRespueata = vbNo Then
        Exit Sub
    End If
    'Adecuar el grid para resultados
    Call inicializa_grid(True)
    
    CmdAceptar.Enabled = False
    cmdImprimir.Enabled = False
    cmdCancelar.Enabled = False
    CmdBuscar.Enabled = False
    fraBusqueda.Enabled = False
    cmbBancos.Clear
    If lbImportaTodosBancos = True Then
        'Buscar los Bancos que tengas movimientos pendientes
        Call gobjVarGlobal.LlenaComboRst(cmbBancos, gobjSQL.FunSQLSelectBancosPorImportar(ltFechaInicial, ltFechaFinal))
        If cmbBancos.ListCount = 0 Then
            Screen.MousePointer = 0
            If pbAutomatico = False Then
                MsgBox "No hay Movimientos Bancarios por Importar ", vbInformation, "SET"
            End If
            Exit Sub
        End If
    Else
        cmbBancos.AddItem lsBanco
        cmbBancos.ItemData(cmbBancos.NewIndex) = llBanco
    End If*/
    for(piContadorBanco=0;piContadorBanco<listBancos.size();piContadorBanco++)
    {
    
         
        piBanco = listBancos.get(piContadorBanco).getIdBanco();
        psDescBanco = listBancos.get(piContadorBanco).getNomBanco();
        List<ParamRetImpBancaDto> listaSelect863=new ArrayList<ParamRetImpBancaDto>();
        listaSelect863=importaBancaElectronicaDao.prepararImportaPendientes(fecInicial, fecFinal,listBancos.get(piContadorBanco).getIdBanco(), Integer.parseInt(idUsuario));
        
        
        if(listaSelect863.size()>0)
        {
                for(int cont=0;cont<listaSelect863.size();cont++)
                {
                    if(listaSelect863.get(cont).getImportado()!=null && listaSelect863.get(cont).getImportado().equals("S"))
                    {
                           if(listaSelect863.get(cont).getBBancaElect()!=null && (listaSelect863.get(cont).getBBancaElect().trim().equals("I") 
                        		   || listaSelect863.get(cont).getBBancaElect().trim().equals("A")))
                                psConcepto = listaSelect863.get(cont).getConceptoSet();
                           else
                                psConcepto = listaSelect863.get(cont).getConcepto();
                           
                    }
                    else
                           psConcepto = listaSelect863.get(cont).getConcepto();
                    
                    // Valida que el concepto actual pueda ser importado por la chequera correcta...
                    //El campo clas_chequera de la tabla equivale_concepto, me dice a que chequera es
                    //valida subirse el concepto como tal, de tal manera que ese campo char de 3 posiciones
                    //me indica lo siguiente:
                    //Si en la primera posicion trae una X aplica a las chequeras Concentradoras
                    //Si en la segunda posicion trae una X aplica a las chequeras Pagadoras
                    //Si en la tercera posicion trae una X aplica a las chequeras de la coinversora
                   
                    pbImporta = false;
                    validaImp=importaBancaElectronicaDao.verificarImportaMovSet(listaSelect863.get(cont).getIdChequera(),
                    		listaSelect863.get(cont).getIdBanco(),listaSelect863.get(cont).getNoEmpresa(),listaSelect863.get(cont).getConcepto2());
                   
                    if(validaImp!=null && validaImp.trim().equals("X"))
                        pbImporta = true;
                     
                    psSalvo = listaSelect863.get(cont).getBSalvoBuenCobro();
                    sReferencia = listaSelect863.get(cont).getReferencia();
                    iEmpresa = listaSelect863.get(cont).getNoEmpresa();
                    miEmpresa = iEmpresa;
                    sNoCliente = "";
                    psIdDivision = "";
                    plIdRubro = 0;
                    
                    psRestoRef = "";
                    	
                    if (pbImporta)
                    {
                        if(listaSelect863.get(cont).getCargoAbono()!=null && listaSelect863.get(cont).getCargoAbono().equals("I"))
                        {
                            if((psConcepto!=null && psConcepto.equals("INTERES")) || (psConcepto!=null && psConcepto.equals("DEVOLUCION ISR")))
                            {
                                sCorresponde = "";
                                sDescCorresponde = psConcepto;
                            }
                            else if(lbReferenciaPorChequera) 
                            {
                                if (listaSelect863.get(cont).getValidaReferencia()!=null 
                                		&& listaSelect863.get(cont).getValidaReferencia().trim().equals("S"))
                                {
                                	ParamValidaRefCheqDto dto=new ParamValidaRefCheqDto();
                                	dto.setPsReferencia(sReferencia);
                                	dto.setPlNoEmpresa(listaSelect863.get(cont).getNoEmpresa());
                                	dto.setPiBanco(piBanco);
                                	dto.setPsChequera(listaSelect863.get(cont).getIdChequera());
                                	dto.setPlIdRubro(plIdRubro);
                                	dto.setPlNoCliente(plNoCliente);
                                	dto.setPsDivision(psIdDivision);
                                	dto.setPsBeneficiario(psBeneficiario);
                                	dto.setPsRestoRef(psRestoRef);//checar el resto de la referencia
                                	boolean validarRefPorCheq=false;
                                	validarRefPorCheq=validarReferenciaPorChequera(dto);
                                    //Analizar la referencia por chequera asignandole el rubro encontrado
                                    if(validarRefPorCheq)
                                    {
                                                    
                                        sCorresponde = "C";
                                        sDescCorresponde = "CLIENTES";
                                        sNoCliente = ""+plNoCliente;
                                        sImporta = "S";
                                    }                                        
                                    else
                                    {
                                    
                                        sCorresponde = "N";
                                        sDescCorresponde = "No Identificado";
                                        sImporta = "S";
                                    }
                                }
                                else
                                {
                                    sCorresponde = "N";
                                    sDescCorresponde = "No Identificado";
                                    sImporta = "S";
                                }
                            }                            
                            else
                            {
                                if(iEmpresa!=iEmpresaAnt) 
                                    iEncabezado =contarEmpresa(iEmpresa, listaSelect863.get(cont).getIdBanco(), iIniciaEn, iTerminaEn);
                               
                                iRenglon = iRenglon;
                                miEmpresaReg = iEmpresa;
                                //evalua referencia
                                sNoCliente = "";
                                sCorresponde = evaluarReferencia(sReferencia.replace("-",""), iEncabezado, iIniciaEn, iTerminaEn, sImporta, sIdentificador, sValores, sNoCliente);
                                
                                if(sCorresponde!=null && sCorresponde.equals("")) 
                                {
                                    if(!pbAutomatico) 
                                       System.out.println("faltan datos en regla_repara ");//MsgBox "faltan datos en regla_repara ", vbExclamation, "SET"
                                    
                                    //Exit Sub
                                }
                                if(sCorresponde!=null && sCorresponde.equals("C")) 
                                {
                                    //Validar que el cliente pertenezca a la misma empresa donde se hizo el depï¿½sito
                                     if(importaBancaElectronicaDao.verificarEmpresaCobranzaCliente(sReferencia, listaSelect863.get(cont).getIdBanco(),listaSelect863.get(cont).getIdChequera()))
                                        sCorresponde = "N";
                                }
                                
                                if(sCorresponde!=null && sCorresponde.equals("C"))
                                	sDescCorresponde = "Clientes";
                                else if(sCorresponde!=null && sCorresponde.equals("V"))   
                                	sDescCorresponde = "Varios";
                                else if(sCorresponde!=null && sCorresponde.equals("N"))  
                                	sDescCorresponde = "No Identificado";
                                else if(sCorresponde!=null && sCorresponde.equals("J"))  
                                	sDescCorresponde = "Caja Clientes";
                                else if(sCorresponde!=null && sCorresponde.equals("K"))  
                                    sDescCorresponde = "Caja Varios";
                                else if(sCorresponde!=null && sCorresponde.equals("T"))  
                                    sDescCorresponde = "Traspasos";
                                else if(sCorresponde!=null && sCorresponde.equals("F"))  
                                    sDescCorresponde = "Financiamientos";
                                
                            }
                        }
                        else
                        {
                            sImporta = "S";
                            sCorresponde = "";
                        }
                       
                        //Solo para Banamex, si la descripcion del concepto tiene IVA entonces asegurarnos de que entre como IVA
                        if((listaSelect863.get(cont).getDescBanco()!=null && listaSelect863.get(cont).getDescBanco().trim().equals("BANAMEX")) 
                        		&& (listaSelect863.get(cont).getCargoAbono()!=null && listaSelect863.get(cont).getCargoAbono().trim().equals("E")))
                        {
                            psDescripcion = listaSelect863.get(cont).getDescripcion();
                            if(psDescripcion.trim().indexOf("IVA")>0)
                                psConcepto = "IVA";
                            
                        }
                        
                         boolean impMov=false;
                        if (sImporta!=null && sImporta.equals("S")) 
                        {
                        	 impMov=importarMovimientos(dtoImportarMov);
                        		
                        	 dtoImportarMov.setNoEmpresa(listaSelect863.get(cont).getNoEmpresa());
                        	 dtoImportarMov.setNomEmpresa(listaSelect863.get(cont).getNomEmpresa());
                        	 dtoImportarMov.setReferencia(listaSelect863.get(cont).getReferencia());
                        	 dtoImportarMov.setCorresponde(listaSelect863.get(cont).getCorresponde());
                        	 dtoImportarMov.setCargoAbono(listaSelect863.get(cont).getCargoAbono());
                        	 dtoImportarMov.setConcepto(psConcepto);
                        	 dtoImportarMov.setIdBanco(listaSelect863.get(cont).getIdBanco());
                        	 dtoImportarMov.setIdChequera(listaSelect863.get(cont).getIdChequera());
                        	 dtoImportarMov.setLiberaAut(listaSelect863.get(cont).getLiberaAut());
                        	 dtoImportarMov.setNoCuentaEmp(listaSelect863.get(cont).getNoCuentaEmp());
                        	 dtoImportarMov.setFecValorString(listaSelect863.get(cont).getFecValorString());
                        	 dtoImportarMov.setImporte(listaSelect863.get(cont).getImporte());
                        	 dtoImportarMov.setReferencia(listaSelect863.get(cont).getReferencia());
                        	 dtoImportarMov.setFolioBanco(listaSelect863.get(cont).getFolioBanco());
                        	 dtoImportarMov.setConcepto2(listaSelect863.get(cont).getConcepto2());
                        	 dtoImportarMov.setSecuencia(listaSelect863.get(cont).getSecuencia());
                        	 dtoImportarMov.setSIdentificador(sIdentificador);
                        	 dtoImportarMov.setSValores(sValores);
                        	 dtoImportarMov.setNoCliente(sNoCliente);
                        	 dtoImportarMov.setIdRubro(plIdRubro);
                        	 dtoImportarMov.setIdDivision(Integer.parseInt(psIdDivision));
                        	 dtoImportarMov.setDescBanco(psDescBanco);
                        	 //ChecarRestoRef
                            if (listaSelect863.get(cont).getCargoAbono()!=null && listaSelect863.get(cont).getCargoAbono().trim().equals("I"))
                                iEmpresaAnt = iEmpresa;
                        }
                    }
                    else
                    {
                    	//Call grid_resultado(psDescBanco, !no_empresa, !nom_empresa, False)
                    }
                        
                }
             List<ParamConceptoDescDto>listConDesconocido=new ArrayList<ParamConceptoDescDto>();
             listConDesconocido=importaBancaElectronicaDao.conceptoDesconocido(fecInicial, fecFinal, piBanco);
             //llamar al grid nuevamente
           /* Set rstDatos = gobjSQL.FunSQLConceptoDesconocido(ltFechaInicial, ltFechaFinal, _
                                                             piBanco)
            With msfDatos
                For i = 1 To .Rows - 1
                    pi_renglon = i
                    .TextMatrix(pi_renglon, LI_C_PEN_NO_IDENTIFICADOS) = 0#
                    pbEncontrado = False
                    While Not rstDatos.EOF And Not pbEncontrado
                        If rstDatos("no_empresa") = Val(.TextMatrix(pi_renglon, LI_C_PEN_NO_EMPRESA)) Then
                            .TextMatrix(pi_renglon, LI_C_PEN_NO_IDENTIFICADOS) = rstDatos("cuenta")
                            pbEncontrado = True
                        End If
                        rstDatos.MoveNext
                    Wend
                Next
            End With
            Set rstDatos = Nothing*/
        }
        else
        {
            if(!lbImportaTodosBancos) 
            {
                if(!pbAutomatico)   
                    System.out.println("! No existen datos del banco ");//MsgBox "! No existen datos del banco " & psDescBanco & "ï¿½ ", vbInformation, "SET"
                
            }
        }
    }//Next piContadorBanco  'Ciclo Principal
        
    
    /*
    If msfDatos.Rows > 1 Then
        If MsgBox("Desea imprimir el reporte de la cuadrilla...", vbOKCancel + vbDefaultButton1 + vbInformation, "SET") = vbOK Then
                Set rstDatos = New ADODB.Recordset
                
                ' define campos del recordset
                With rstDatos
                    .Fields.Append "Desc_Banco", adVarChar, 50
                    .Fields.Append "no_Empresa", adDouble
                    .Fields.Append "nom_empresa", adVarChar, 100
                    .Fields.Append "importados", adDouble
                    .Fields.Append "rechazados", adDouble
                    .Fields.Append "no_identificados", adDouble
                    .CursorType = adOpenKeyset
                    .LockType = adLockOptimistic
                    .Open
                End With
                        
                With msfDatos
                    ' carga registros
                    For i = 1 To .Rows - 1
                        .row = i
                        rstDatos.AddNew
                        rstDatos("desc_banco") = .TextMatrix(.row, LI_C_PEN_DESC_BANCO)
                        rstDatos("no_Empresa") = CDbl(.TextMatrix(.row, LI_C_PEN_NO_EMPRESA))
                        rstDatos("nom_empresa") = .TextMatrix(.row, LI_C_PEN_DESC_EMPRESA)
                        rstDatos("importados") = CDbl(.TextMatrix(.row, LI_C_PEN_IMPORTADOS))
                        rstDatos("rechazados") = CDbl(.TextMatrix(.row, LI_C_PEN_RECHAZADOS))
                        rstDatos("no_identificados") = CDbl(.TextMatrix(.row, LI_C_PEN_NO_IDENTIFICADOS))
                        rstDatos.Update
                    Next i
                End With
                
                With drpImportaBE
                    Set .DataSource = rstDatos
                    .RightMargin = 400
                    .LeftMargin = 400
                    .TopMargin = 400
                    .BottomMargin = 400
                    .Orientation = rptOrientPortrait
                    .Sections("Encabezado").Controls("Lblempresa").Caption = "INFORME GLOBAL DE " & lsBanco
                    .Sections("Encabezado").Controls("lblfecha").Caption = Format(GT_FECHA_HOY, "dd/mm/yyyy")
                    lcl_s_rutalogo = gobjVarGlobal.Logo(GI_ID_EMPRESA)
                    Set .Sections("Encabezado").Controls("imglogo").Picture = LoadPicture(lcl_s_rutalogo)
                    Screen.MousePointer = 0
                    .Show vbModal
                End With
        
        End If
    End If
     pgrAvance.Value = 0
    */
   
    if(!pbAutomatico)
        System.out.println("Registros Importados");//MsgBox "Registros Importados", vbInformation, "SET"

		return importados;//borrar
	}
	/**
	 * Function contarEmpresa(piEmpresa As Integer, piBanco As Integer, iIniciaEn As Integer, iTerminaEn As Integer) As Integer
	 * @param idEmpresa
	 * @param idBanco
	 * @param iniciaEn
	 * @param terminaEn
	 * @return
	 */
	public int contarEmpresa(int idEmpresa, int idBanco, int iniciaEn, int terminaEn)
	{
	    int i=0;
	    int contarEmpresa=0;
	    boolean bEncontrado=false;
	    boolean bBanco=false;
	    boolean bEmpresa=false;
	    int iEncabezado=0;
	    int iEmpresa=idEmpresa;
	    int iBanco=idBanco;
	    List<ReferenciaEncDto>mareRefEnc=new ArrayList<ReferenciaEncDto>();//Checar donde se llena
	    List<ReferenciaDetDto>mareRefDet=new ArrayList<ReferenciaDetDto>();//Checar donde se llena
	    while(!bEncontrado && i<mareRefEnc.size())
	    {
	        if(mareRefEnc.get(i).getIdBanco()==iBanco && mareRefEnc.get(i).getNoEmpresa()==iEmpresa)
	        {
	            bEncontrado = true;
	            iEncabezado = i;
	        }
	        else if (mareRefEnc.get(i).getIdBanco()==iBanco)
	        {
	            bBanco = true;
	            i++;
	        }
	        else if(mareRefEnc.get(i).getNoEmpresa()==iEmpresa)
	        {
	            bEmpresa = true;
	            i++;
	        }
	        else
	            i++;
	    }
	    
	    if(!bBanco && !bEmpresa)
	    {
	        i = 1;
	        iBanco = -1;
	        iEmpresa = -1;
	        while(!bEncontrado && i<mareRefEnc.size())
	        {
	            if(mareRefEnc.get(i).getIdBanco()==iBanco && mareRefEnc.get(i).getNoEmpresa()==iEmpresa)
	            {
	                bEncontrado = true;
	                iEncabezado = i;
	            }
	            else
	                i++;
	        }
	    }
	    else if(!bBanco)
	    {
	        i = 1;
	        iBanco = -1;
	        while(!bEncontrado && i<mareRefEnc.size())
	        {
	            if(mareRefEnc.get(i).getIdBanco()==iBanco && mareRefEnc.get(i).getNoEmpresa()==iEmpresa)
	            {
	                bEncontrado = true;
	                iEncabezado = i;
	            
	            }else
	                i++;
	        }
	    }
	    else if(!bEmpresa)
	    {
	        i = 1;
	        iEmpresa = -1;
	        while(!bEncontrado && i<mareRefEnc.size())
	        {
	            if(mareRefEnc.get(i).getIdBanco()==iBanco && mareRefEnc.get(i).getNoEmpresa()==iEmpresa)
	            {
	                bEncontrado = true;
	                iEncabezado = i;
	            }
	            else
	                i++; 
	        }
	    }
	    if(!bEncontrado)
	    {
	        i = 1;
	        iBanco = -1;
	        iEmpresa = -1;
	        while(!bEncontrado && i<mareRefEnc.size())  
	        {
	        	if(mareRefEnc.get(i).getIdBanco()==iBanco && mareRefEnc.get(i).getNoEmpresa()==iEmpresa)
	        	{
	                bEncontrado = true;
	                iEncabezado = i;
	        	}
	            else
	                i++;
	        }
	    }
	    i = 1;
	    while (i<mareRefDet.size()&&(mareRefDet.get(i).getIBanco()!=iBanco || mareRefDet.get(i).getIEmpresa()!=iEmpresa ))
	    {
	        i++;
	    }
	    if(i<mareRefDet.size() && mareRefDet.get(i).getIEmpresa()==iEmpresa && mareRefDet.get(i).getIBanco()==iBanco)
	    {
	        iniciaEn = i;
	        while(mareRefDet.get(i).getIBanco()==iBanco && mareRefDet.get(i).getIEmpresa()==iEmpresa)
	        {
	            i++;
	    	}
	        if(mareRefDet.get(i).getIBanco() == iBanco && mareRefDet.get(i).getIEmpresa()== iEmpresa && i<mareRefDet.size())
	           if(i==mareRefDet.size())
	            terminaEn = i;
	        else
	            terminaEn = i - 1;
	    }else{
	        iniciaEn = 0;
	        terminaEn = 0;
	    }
	    
	    contarEmpresa = iEncabezado;
	    
	    return contarEmpresa;
	}

	/*Function evaluarReferencia(psReferencia As String, piEncabezado As Integer, piIniciaEn As Integer, _
            piTerminaEn As Integer, ByRef psImporta As String, psIdentificador As String, _
            psValor As String, ByRef psNo_cliente As String) As String*/
	public String evaluarReferencia(String psReferencia, int piEncabezado,int  piIniciaEn,int piTerminaEn, String psImporta,String psIdentificador,String psValor,String psNoCliente)
	{
		boolean bEncontrado=false;
	    boolean bCondicion=true;
	    int iCasoAnt=1;
	    int iCasoActual=1;
	    int iPosIni=0;
	    int iPosFin=0;
	    int iPosGuion=0;
	    int iCodSubcod=0;
	    int i=0;
	    String sCorresponde="";
	    String sValor="";
	    String sQuery="";
	    String sEvalQuery="";
	    int lDigitoVerif=0;
	    //Dim rstEvalQry As ADODB.Recordset
	    
	    boolean temp=false;
	    int iTemp=0;
	    String stemp="";
	    int iIntentos=0;
	    String sQueryNoDocto="";
	    String evaluarRetRef="";
	    
	    i = piIniciaEn;
	    psIdentificador = "";
	    psValor = "";
	    List<ReferenciaEncDto>mareRefEnc=new ArrayList<ReferenciaEncDto>();//Checar donde se llena
	    List<ReferenciaDetDto>mareRefDet=new ArrayList<ReferenciaDetDto>();//Checar donde se llena
	    
	    if (!seImporta(psReferencia, piIniciaEn, piTerminaEn))
	    {
	    	evaluarRetRef = "N";
	       psImporta = "N";
	       psIdentificador = "";
	       psValor = "";
	       //Exit Function
	    }
	    if(!validarNumAlfa(psReferencia, piEncabezado) || Integer.parseInt(psReferencia)==0)
	    {
	        sCorresponde = "N";
	        psImporta = "S";
	        psIdentificador = "";
	        psValor = "";
	    }
	    else
	    {
		        if(mareRefEnc.get(piEncabezado).getBaseCalculo()!=null && mareRefEnc.get(piEncabezado).getBaseCalculo().equals("b10"))
		            lDigitoVerif =funciones.calcularDigito(psReferencia.substring(0,mareRefEnc.get(piEncabezado).getNoDigitos()-1)); //Para que el calculo se haga de acuerdo a la ref.
		              
		        if(psReferencia.length()!=mareRefEnc.get(piEncabezado).getNoDigitos())
		        {
		            sCorresponde = "N";
		            psImporta = "S";
		            psIdentificador = "";
		            psValor = "";
		        }
		            	
		        else
		        {
		          
		            if(lDigitoVerif==Integer.parseInt((psReferencia.substring(mareRefEnc.get(piEncabezado).getCampoVerificador(), mareRefEnc.get(piEncabezado).getCampoVerificador()+1))))
		            {
		                sCorresponde = "N";
		                psImporta = "S";
		                psIdentificador = "";
		                psValor = "";
		            }
		            else  
		            {
		           
		                iCasoActual = mareRefDet.get(i).getICaso();
		                while(!bEncontrado && i <= piTerminaEn)
		                {
		                    while (bCondicion && i <= piTerminaEn)
		                    {//'cambio M6  And (iCasoAnt = iCasoActual)
		                        if(mareRefDet.get(i).getICaso()!= iCasoActual)
		                            break;

		                        iPosIni = 0;
		                        iPosFin = 0;
		                        iPosFin = parteDerIzq(mareRefDet.get(i).getSVerifCampo(), iPosIni);
		                        if(iPosFin==0)
		                            sValor = psReferencia.substring(iPosIni,iPosIni+1);
		                        else
		                            sValor = psReferencia.substring(iPosIni, iPosIni+(iPosFin - iPosIni) + 1);
		                        
		                        if(mareRefDet.get(i).getSCondicion()!=null && mareRefDet.get(i).getSCondicion().equals("="))
		                        {
		                        	if(Integer.parseInt(sValor)==Integer.parseInt(mareRefDet.get(i).getSValor()))
		                        	{
		                                    sCorresponde = mareRefDet.get(i).getSCorresp();
		                                    psImporta = mareRefDet.get(i).getSBImporta();
		                                    {
		                                    if(mareRefDet.get(i).getSIdentificador()!=null && !mareRefDet.get(i).getSIdentificador().equals(""))
		                                           psIdentificador = psIdentificador + "," + mareRefDet.get(i).getSIdentificador();
		                                           if(mareRefDet.get(i).getSTipoId()!=null && mareRefDet.get(i).getSTipoId().equals("N"))
		                                               psValor = psValor + "," + sValor;
		                                           else
		                                               psValor = psValor + ",'" + sValor + "'";
		                                    }
		                                           
		                                   
		                        	}
		                        	else
		                                    bCondicion = false;
		                        }
		                        if(mareRefDet.get(i).getSCondicion()!=null && mareRefDet.get(i).getSCondicion().equals("!"))
		                        {
		                        	if(Integer.parseInt(sValor)!=Integer.parseInt(mareRefDet.get(i).getSValor()))
		                        		{
		                                    sCorresponde = mareRefDet.get(i).getSCorresp();
		                                    psImporta = mareRefDet.get(i).getSBImporta();
		                                    if(mareRefDet.get(i).getSIdentificador()!=null && mareRefDet.get(i).getSIdentificador().equals(""))
		                                    {
		                                        psIdentificador = psIdentificador + "," + mareRefDet.get(i).getSIdentificador();
		                                        if (mareRefDet.get(i).getSTipoId().equals("N"))
		                                            psValor = psValor + "," + sValor;
		                                        else
		                                            psValor = psValor + ",'" + sValor + "'";
		                                    }
	                        			}
		                        	else                                
		                                    bCondicion = false;
		                        }
		                        if(mareRefDet.get(i).getSCondicion()!=null && mareRefDet.get(i).getSCondicion().equals("<"))
		                        {
		                        	 if(Integer.parseInt(sValor)<Integer.parseInt(mareRefDet.get(i).getSValor()))
		                        	 {
		                                    sCorresponde = mareRefDet.get(i).getSCorresp();
		                                    psImporta = mareRefDet.get(i).getSBImporta();
		                                    if(mareRefDet.get(i).getSIdentificador()!=null && mareRefDet.get(i).getSIdentificador().equals(""))
		                                    {
		                                        psIdentificador = psIdentificador + "," + mareRefDet.get(i).getSIdentificador();
		                                        if(mareRefDet.get(i).getSTipoId()!=null && mareRefDet.get(i).getSTipoId().equals("N"))
		                                            psValor = psValor+ "," + sValor;
		                                        else
		                                            psValor = psValor + ",'" + sValor + "'";
		                                    }
		                        	 }else
		                                    bCondicion = false;
		                        }
		                        if(mareRefDet.get(i).getSCondicion()!=null && mareRefDet.get(i).getSCondicion().equals(">"))
		                        {
		                        	 if(Integer.parseInt(sValor)> Integer.parseInt(mareRefDet.get(i).getSValor()))
		                                    sCorresponde = mareRefDet.get(i).getSCorresp();
		                                    psImporta = mareRefDet.get(i).getSBImporta();
		                                    if(mareRefDet.get(i).getSIdentificador()!=null && !mareRefDet.get(i).getSIdentificador().equals(""))
		                                    {
		                                        psIdentificador = psIdentificador + "," + mareRefDet.get(i).getSIdentificador();
		                                        if(mareRefDet.get(i).getSTipoId()!=null && mareRefDet.get(i).getSTipoId().equals("N"))
		                                            psValor = psValor + "," + sValor;
		                                        else
		                                            psValor = psValor + ",'" + sValor + "'";
		                                    }
		                                else
		                                    bCondicion = false;
		                        }
		                        if(mareRefDet.get(i).getSCondicion()!=null && mareRefDet.get(i).getSCondicion().equals("I"))
		                        {
		                        	sCorresponde = mareRefDet.get(i).getSCorresp();
		                                psImporta = mareRefDet.get(i).getSBImporta();
		                        }
		                        if(mareRefDet.get(i).getSCondicion()!=null && mareRefDet.get(i).getSCondicion().equals("["))
		                        {
		                        	 iPosFin = parteDerIzq(mareRefDet.get(i).getSValor(),iPosIni);
		                                if(Integer.parseInt(sValor)>=iPosIni && Integer.parseInt(sValor)<=iPosFin)
		                                {
		                                    sCorresponde = mareRefDet.get(i).getSCorresp();
		                                    psImporta = mareRefDet.get(i).getSBImporta();
		                                    if(mareRefDet.get(i).getSIdentificador()!=null && !mareRefDet.get(i).getSIdentificador().equals(""))
		                                    {
		                                        psIdentificador = psIdentificador + "," + mareRefDet.get(i).getSIdentificador();
		                                        if(mareRefDet.get(i).getSTipoId()!=null && mareRefDet.get(i).getSTipoId().equals("N"))
		                                            psValor = psValor + "," + sValor;
		                                        else
		                                            psValor = psValor + ",'" + sValor + "'";
		                                    }
		                                }
		                                else
		                                    bCondicion = false;
		                        }
		                        if(mareRefDet.get(i).getSCondicion()!=null && mareRefDet.get(i).getSCondicion().equals("Q"))
		                        {
		                        	sEvalQuery = "";
		                                psNoCliente = "";
		                                sEvalQuery = evaluarQuery(mareRefDet.get(i).getSValor(), psReferencia);
		                                if (mareRefDet.get(i).getSCorresp()!=null && mareRefDet.get(i).getSCorresp().equals("C"))
		                                    sQueryNoDocto = "Select no_persona " +sEvalQuery.substring(sEvalQuery.toUpperCase().indexOf("FROM"));
		                                else
		                                    sQueryNoDocto = sEvalQuery;
		                                
		                                if(sEvalQuery!=null && !sEvalQuery.equals(""))
		                                {
		                                	List<SeleccionGeneralDto>list=new ArrayList<SeleccionGeneralDto>();
		                                	list=importaBancaElectronicaDao.seleccionarGeneral(sQueryNoDocto);
		                                    if(list.size()>0)
		                                    {
		                                    	if(mareRefDet.get(i).getSCorresp()!=null && mareRefDet.get(i).getSCorresp().equals("C"))
                                                   psNoCliente = list.get(0).getNoPersona();
		                                        else
                                                   iCodSubcod = list.get(0).getCuenta();
		                                    }
		                                }
		                                if(iCodSubcod > 0)
		                                {
		                                    sCorresponde = mareRefDet.get(i).getSCorresp();
		                                    psImporta = mareRefDet.get(i).getSBImporta();
		                                    if(mareRefDet.get(i).getSIdentificador()!=null && mareRefDet.get(i).getSIdentificador().equals(""))
		                                    {
		                                        psIdentificador = psIdentificador + "," + mareRefDet.get(i).getSIdentificador();
		                                        if (mareRefDet.get(i).getSTipoId()!=null && mareRefDet.get(i).getSTipoId().equals("N"))
		                                            psValor = psValor + "," + sValor;
		                                        else
		                                            psValor = psValor + ",'" + sValor + "'";
		                                    }
		                                }
		                                else
		                                {
		                                	boolean continua;
		                                    if (Integer.parseInt(psNoCliente)>0 && (mareRefDet.get(i).getSCorresp()!=null && mareRefDet.get(i).getSCorresp().equals("C")))
		                                        continua=true;//esta variable solo para que no marque error en el if
		                                    else
		                                        bCondicion = false;
		                                }
		                        }  
		                        if(mareRefDet.get(i).getSCondicion()!=null && mareRefDet.get(i).getSCondicion().equals("S"))
		                        {
		                        	sEvalQuery = "";
		                                sEvalQuery = evaluarQuery(mareRefDet.get(i).getSValor(), psReferencia);
		                                sValor = "";
		                                if(sEvalQuery!=null && !sEvalQuery.equals("")) 
		                                {
		                                    List<SeleccionGeneralDto>list=new ArrayList<SeleccionGeneralDto>();
		                                    list=importaBancaElectronicaDao.seleccionarGeneral(sEvalQuery);
		                                    int in=0;
		                                    while(in<list.size())
		                                    {
		                                    	sValor = list.get(in).getIdentificador();
		                                    	in++;
		                                    }
		                                              
		                                }
		                                if(sValor!=null && !sValor.equals(""))
		                                {
		                                    sCorresponde = mareRefDet.get(i).getSCorresp();
		                                    psImporta = mareRefDet.get(i).getSBImporta();
		                                    if(mareRefDet.get(i).getSIdentificador()!=null && !mareRefDet.get(i).getSIdentificador().equals(""))
		                                    {
		                                        psIdentificador = psIdentificador + "," + mareRefDet.get(i).getSIdentificador();
		                                        if(mareRefDet.get(i).getSTipoId()!=null && mareRefDet.get(i).getSTipoId().equals("N"))
		                                            psValor = psValor + "," + sValor;
		                                        else
		                                            psValor = psValor + ",'" + sValor + "'";
		                                    }
		                                }
		                                else
		                                    bCondicion = false;
		                        }
		                        i++;
		                        //'Cambio M6
		//'                        If i <= piTerminaEn Then
		//'                            iCasoActual = mareRefDet(i).iCaso
		//'                        Else
		//'                            iCasoActual = iCasoActual + 1
		//'                        End If
		                    }
		                    //'ir al siguiente caso
		                    if(i<=piTerminaEn)
		                        //'Cambio M6
		                        //'iCasoAnt = mareRefDet(i).iCaso
		                        iCasoAnt = mareRefDet.get(i-1).getICaso();
		                        //'iCasoActual = iCasoAnt + 1
		                    if(bCondicion)
		                        bEncontrado = true;
		                    else if (i <=mareRefDet.size())
		                    {
		                        //'Cambios M6
		//'                        While iCasoActual = iCasoAnt And i < UBound(mareRefDet)
		//'                            i = i + 1
		//'                            iCasoActual = mareRefDet(i).iCaso
		//'                        Wend
		                        do 
		                        {
		                            i++;
		                            if(i>=mareRefDet.size())
		                                break;
		                        }while(mareRefDet.get(i).getICaso()==iCasoAnt);
		                        iCasoActual = mareRefDet.get(i).getICaso();
		                        bCondicion = true;
		                        sCorresponde = "";
		                        psIdentificador = "";
		                        psValor = "";
		                        //'iCasoAnt = iCasoActual
		                    }
		                }
		            }
		        }
	    }
	    if((sCorresponde!=null && sCorresponde.equals(""))&& psReferencia.length()>0)
	    {
	        sCorresponde = "N";
	        psImporta = "S";
	        psIdentificador = "";
	        psValor = "";
	    }
    
   	return sCorresponde;
    
	}
	
	/*Function SeImporta(psReferencia As String, piIniciaEn As Integer, piTerminaEn As Integer) As Boolean*/
	public boolean seImporta(String psReferencia, int piIniciaEn, int piTerminaEn)
	{
	    int i=piIniciaEn;
	    int iPosIni=0;
	    int iPosFin=0;
	    int iCasoAnt=1;
	    int iCasoActual=1;
	    int iCodSubcod=0;
	    int iParche=0;
	    int iIntentos=0;
	    boolean bBandera=true;
	    boolean bCondicion=true;
	    String sValor="";
	    String sEvalQuery="";
	    
	    List<ReferenciaEncDto>mareRefEnc=new ArrayList<ReferenciaEncDto>();//Checar donde se llena
	    List<ReferenciaDetDto>mareRefDet=new ArrayList<ReferenciaDetDto>();//Checar donde se llena
	    
	    while (i <= piTerminaEn && bBandera)
	    {
	    	iCasoAnt = iCasoActual;
	        if(mareRefDet.get(i).getSBImporta()!=null && mareRefDet.get(i).getSBImporta().equals("N")) 
	        {
	            bCondicion = true;
	            while (bCondicion && iCasoAnt == iCasoActual)
	            {
	            	
	                iPosIni = 0;
	                iPosFin = 0;
	                iPosFin = parteDerIzq(mareRefDet.get(i).getSVerifCampo(),iPosIni);
	                if (iPosFin ==0 )
	                    sValor = psReferencia.substring(iPosIni,iPosIni+2);
	                else
	                    sValor = psReferencia.substring(iPosIni,iPosIni+(iPosFin - iPosIni+1));
	                
	                if(mareRefDet.get(i).getSCondicion()!=null && mareRefDet.get(i).getSCondicion().equals("="))
	                {
	                	 if(Integer.parseInt(sValor)==Integer.parseInt(mareRefDet.get(i).getSValor()))
	                            bCondicion = true;
	                     else
	                            bCondicion = false;
	                }
	                if(mareRefDet.get(i).getSCondicion()!=null && mareRefDet.get(i).getSCondicion().equals("!"))
	                {
	                	 if(Integer.parseInt(sValor)!=Integer.parseInt(mareRefDet.get(i).getSValor())) 
	                            bCondicion = true;
	                        else
	                            bCondicion = false;
	                }
	                if(mareRefDet.get(i).getSCondicion()!=null && mareRefDet.get(i).getSCondicion().equals("<"))
	                {
	                	 if(Integer.parseInt(sValor)<Integer.parseInt(mareRefDet.get(i).getSValor()))
	                		 bCondicion = true;
	                     else
	                         bCondicion = false;
	                }
	                if(mareRefDet.get(i).getSCondicion()!=null && mareRefDet.get(i).getSCondicion().equals(">"))
	                {
	                	if(Integer.parseInt(sValor)>Integer.parseInt(mareRefDet.get(i).getSValor()))
	                            bCondicion = true;
	                    else
	                            bCondicion = false;
	                }
	                if(mareRefDet.get(i).getSCondicion()!=null && mareRefDet.get(i).getSCondicion().equals("["))
	                {
	                	iPosFin = parteDerIzq(mareRefDet.get(i).getSValor(), iPosIni);
	                        if(Integer.parseInt(sValor)>=iPosIni && Integer.parseInt(sValor)<=iPosFin)
	                            bCondicion = true;
	                        else
	                            bCondicion = false;
	                        
	                }
	                if(mareRefDet.get(i).getSCondicion()!=null && mareRefDet.get(i).getSCondicion().equals("Q"))
	                {
	                	 sEvalQuery = "";
	                        sEvalQuery = evaluarQuery(mareRefDet.get(i).getSValor(), psReferencia);
	                        if(sEvalQuery!=null && !sEvalQuery.equals(""))
	                        {
	                            List<SeleccionGeneralDto>list=new ArrayList<SeleccionGeneralDto>();
	                        	list = importaBancaElectronicaDao.seleccionarGeneral(sEvalQuery);
	                            if(list!=null)
	                                iCodSubcod =  list.get(0).getCuenta();//checar
	                        }
	                        
	                        if(iCodSubcod > 0)
	                            bCondicion = true;
	                        else
	                            bCondicion = false;
	                       
	                }      
	              
	                iCasoAnt = mareRefDet.get(i).getICaso();
	                i++;
	                if(i<mareRefDet.size())
	                    iCasoActual = mareRefDet.get(i).getICaso();
	                else
	                    iCasoActual = i;
	            }
	            if(bCondicion)
	            	bBandera = false;
	        }
	        while (iCasoActual == iCasoAnt && i <= piTerminaEn)
	        {
	            iCasoAnt = mareRefDet.get(i).getICaso();
	            i++;
	            if(i <= piTerminaEn)
	               iCasoActual = mareRefDet.get(i).getICaso();
	        }
	    }
	    return bBandera;
	}
	
	/*Function evaluaQuery(psquery As String, psReferencia As String) As String*/
	public String evaluarQuery(String psQuery,String psReferencia)
	{
    String sRegreso="";
    String sEvalua="";
    String sCuantos="";
    String sCadEvaluar="";
    int iPosIni=0;
    int iPosFin=0;
    String tmp="";
    
    sCadEvaluar = psQuery;
	    while(sCadEvaluar.length()>0)
	    {
	        sRegreso = sRegreso + separarVal(sCadEvaluar, "@");
	        if(sRegreso!=null && !sRegreso.equals(""))  
	        {
	            sEvalua = sCadEvaluar.substring(0,1);
	            if(sEvalua!=null && sEvalua.toUpperCase().equals("E"))
	            {
	            	 sRegreso = sRegreso + miEmpresaReg;
	            	 tmp = separarVal(sCadEvaluar, "E");
	            }
	            if(sEvalua!=null && sEvalua.toUpperCase().equals("B"))
	            {
	            	sRegreso = sRegreso + liBanco;
                    tmp = separarVal(sCadEvaluar, "B");
	            }
	            if(sEvalua!=null && sEvalua.toUpperCase().equals("S"))
	            {
	            	tmp = separarVal(sCadEvaluar, "S");
                    sCuantos = separarVal(sCadEvaluar, "[");
                    sCuantos = separarVal(sCadEvaluar, "]");
                    iPosFin = parteDerIzq(sCuantos, iPosIni);
                    sRegreso = sRegreso + "'" + psReferencia.substring(iPosIni,iPosIni+(iPosFin - iPosIni) + 1)+"'";
	            }
	            if(sEvalua!=null && sEvalua.toUpperCase().equals("N"))
	            {
	            	tmp = separarVal(sCadEvaluar, "N");
                    sCuantos = separarVal(sCadEvaluar, "[");
                    sCuantos = separarVal(sCadEvaluar, "]");
                    iPosFin = parteDerIzq(sCuantos, iPosIni);
                    sRegreso = sRegreso + psReferencia.substring(iPosIni,iPosIni+(iPosFin - iPosIni) + 1); 
	            }
	        }
	        else
	        {
	            sRegreso = sCadEvaluar;
	            sCadEvaluar = "";
	        }
	    }
    return sRegreso;
	}
	
	/*Function separaVal(ByRef psCadena As String, psCaracter As String) As String*/
	public String separarVal(String psCadena, String psCaracter)
	{
		
		String psRegreso="";
	    int iPosicion=0;

	    iPosicion = psCadena.indexOf(psCaracter);
	    if(iPosicion>0)
	    {
	        psRegreso = psCadena.substring(0,iPosicion-1);
	        psCadena = psCadena.substring(iPosicion+1,iPosicion+1+psCadena.length());
	    }
	    else
	    {
	        if(psCadena!=null && !psCadena.equals(""))
	        {
	            psRegreso=psCadena;
	        }
	         
	    }
	    return psRegreso;	
	}
    
	/*Private Function parteDerIzq(psCadena As String, ByRef iPosIni) As Integer*/
	public int parteDerIzq(String psCadena, int iPosIni)
	{
		int iPosGuion=0;
	    int iPosFin=0;
	    
	    iPosGuion =psCadena.indexOf("-");
	    if(iPosGuion>0)
	    {
	        iPosIni = Integer.parseInt(psCadena.substring(0,iPosGuion));
	        iPosFin = Integer.parseInt(psCadena.substring(iPosGuion+1,iPosGuion+1+psCadena.length()));
	    }
	    else
	    {
	        iPosIni = Integer.parseInt(psCadena);
	        iPosFin = 0;
	    }
	    
	    return iPosFin;	
	}
   
	/*Function validaNumAlfa(psReferencia As String, piEncabezado As Integer) As Boolean*/
	public boolean validarNumAlfa(String psReferencia, int piEncabezado)
	{
		int i=0;
	    boolean bBand=true;
	    List<ReferenciaEncDto>mareRefEnc=new ArrayList<ReferenciaEncDto>();//Checar donde se llena
	    List<ReferenciaDetDto>mareRefDet=new ArrayList<ReferenciaDetDto>();//Checar donde se llena
	    
	    if(mareRefEnc.get(piEncabezado).getNumAlfa()!=null && mareRefEnc.get(piEncabezado).getNumAlfa().equals("N"))
	    {
	        while(i <= psReferencia.length() && bBand)
	        {
	            if(!funciones.isNumeric(psReferencia.substring(i,i+1)))
	                bBand = false;
	            
	            i++;
	        }
	    }
	    else if (mareRefEnc.get(piEncabezado).getNumAlfa()!=null && mareRefEnc.get(piEncabezado).getNumAlfa().equals("A"))
	    {
	        while(i <= psReferencia.length()&& bBand)
	        {
	            if(!funciones.comparaMayusculas(psReferencia.substring(i,i+1)) || funciones.isNumeric(psReferencia.substring(i,i+1))) 
	                bBand = false;
	            i++;
	        }
	    }
	    
	    return bBand;
	}
    
	/*Sub importar_movimientos_SET(ByVal plNo_empresa As Long, _
    ByVal psDesc_empresa As String, _
    ByVal sReferencia As String, ByVal ps_id_BE As String, _
    ByVal psCorresponde As String, ByVal psCargo_Abono As String, _
    ByVal psConcepto As String, ByVal plId_banco As Long, _
    ByVal psId_chequera As String, ByVal ps_Liberacion_aut As String, _
    ByVal plNo_CuentaEmp As Long, ByVal psFec_valor As String, _
    ByVal pdImporte As Double, ByVal psReferencia As String, _
    ByVal psFolioBanco As String, ByVal psObservacion As String, _
    ByVal psSecuencia As String, ByVal psIdentificador As String, _
    ByVal psValores As String, ByVal psHora_FecValor As String, _
    ByVal psNoCliente As String, ByVal plIdRubro As Long, _
    ByVal psIdDivision As String, ByVal psDescBanco As String, _
    ByVal psRestoRef As String)
    'Importar los movimientos al SET*/
	public boolean importarMovimientos(ParamRetImpBancaDto dto)
	{
		int  piTipoDocto=0;
		int  iNoEmpresa=0;
		String sChequera="";
		double pdResp=0; 
		double pdFolioMov=0;
		double pdFolioDet=0;
		String psNoCliente="";
		String psEstatusMov="";
		String psReferencia="";
		String sReferActual=dto.getReferencia();
		String psDivisa="";
		String psCodigo="";
		String psOrigen="";
		String psSalvoBCobro="";

		    
		int plFolio=0;
		int plFolio1=0;
		int plEmpresa=0;
		int lAfectados=0;

		boolean pbGenerico=false;
		boolean pbBanca=false;
		boolean bEncontrado=false;

		String piTipoOperacion="";
		int piFormaPago=0;
		String psOrigenMov="";
		String psFecValorOriginal="";
		List<ParamRetImpBancaDto>listReglaCon=new ArrayList<ParamRetImpBancaDto>();
		
		if(dto.getPsIdBe()!=null && dto.getPsIdBe().equals("N")) 
	         pbBanca = false;
	    else
	         pbBanca = true;
	    
	    psCodigo = "";
	    psNoCliente = "";
	    
	    //pone el origen correcto
        if(dto.getCorresponde()!=null && (dto.getCorresponde().equals("N") || !dto.getCorresponde().equals("N"))) 
            psOrigen = sReferActual.substring(2,3)=="1"?"0":sReferActual.substring(2,3);
        else
            psOrigen = "";

        //asigna referencia
        if(dto.getCorresponde()!=null && (dto.getCorresponde().trim().equals("C")|| dto.getCorresponde().trim().equals("J"))) 
        {
            piTipoDocto = 51;
            //'ps_NoCliente = sReferActual
            //'MSDESC
            psNoCliente = dto.getNoCliente();
            psReferencia = "DEPOSITO BANCO CLIENTE";
        }
        else if(dto.getCorresponde()!=null && (dto.getCorresponde().trim().equals("V") || dto.getCorresponde().trim().equals("K")))
        {
            psReferencia = "VARIOS";
            piTipoDocto = 52;
            psNoCliente = "";
        }
        else if (dto.getCorresponde()!=null && (dto.getCorresponde().trim().equals("N")))
        {
            psReferencia = "NI";
            piTipoDocto = 52;
            psNoCliente = "";
        }
        
        listReglaCon=importaBancaElectronicaDao.seleccionarReglaConcepto(dto.getIngresoEgreso(),dto.getConceptoSet(),dto.getIdCorresponde());
        
        if(listReglaCon.size()>0)
        {
        	 piTipoOperacion = ""+listReglaCon.get(0).getIdTipoOperacion();
             piFormaPago = listReglaCon.get(0).getIdFormaPago();
             psOrigenMov = listReglaCon.get(0).getOrigenMov();
             bEncontrado = true;
        }
        else
        {
        	piTipoOperacion = "0";
            piFormaPago = 0;
            psOrigenMov = "";
            bEncontrado = false;
        }
        
        if (!bEncontrado)
        {
           
//'                If psCargo_abono = "E" Then
//'                    MsgBox "No se encontrï¿½ clave de Egresos " & Chr(13) & _
//'                        " Concepto_Set " & psConcepto & _
//'                        " Origen_Mov = set ", vbCritical, "SET"
//'
//'                Else
//'                    MsgBox "No se encontrï¿½ clave de Ingresos " & Chr(13) & _
//'                        " Concepto_Set " & psConcepto & Chr(13) & _
//'                        " Origen_Mov = '' y Corresponde " & psCorresponde, vbCritical, "SET"
//'                End If
           //Call grid_resultado(psDescBanco, plNo_empresa, psDesc_empresa, False)
           //Exit Sub
        	
    	 if(dto.getConcepto()!=null && dto.getConcepto().equals("DEP S B COBRO")) 
    	 {
             psSalvoBCobro = "N";
             psEstatusMov = "P";
    	 }
         else
         {
             psEstatusMov = "A";        //'siempre es "A" a menos que sea DEP S B COBRO
             psSalvoBCobro = "S";     //'siempre es "S" a menos que sea DEP S B COBRO
         }
        }
       
        //'va por la divisa de la chequera si es diferente
        if(iNoEmpresa!=dto.getNoEmpresa()||sChequera!=dto.getIdChequera())
        {
        String idDivi=importaBancaElectronicaDao.obtenerDivisa(dto.getNoEmpresa(),dto.getIdBanco(),dto.getIdChequera());
        
	        if(idDivi!=null && !idDivi.equals(""))
	        {
	            iNoEmpresa = dto.getNoEmpresa();
	            sChequera = dto.getIdChequera();
	            psDivisa = idDivi;
	        }
	        else
	        {
	            if(!pbAutomatico)   
	               System.out.println("No existe la chequera: ");// MsgBox "No existe la chequera: " & psId_chequera, vbExclamation, "SET"
	             
	            //Call grid_resultado(psDescBanco, plNo_empresa, psDesc_empresa, False)
	            //Exit Sub
	        }
        }
        //'Validar el tipo de operacion para cuando es cheque devuelto
        if(piTipoOperacion.equals("1015"))  
        {
            if (dto.getLiberaAut()!=null && !dto.getLiberaAut().equals("S"))
            {
                if(!pbAutomatico)
                    logger.info("No se puede importar CHEQUE DEVUELTO si el banco ");//MsgBox "No se puede importar CHEQUE DEVUELTO si el banco " & plId_banco & " no maneja Liberaciï¿½n Automï¿½tica", vbExclamation, "SET"
                 
                //Call grid_resultado(psDescBanco, plNo_empresa, psDesc_empresa, False)
                //Exit Sub
            }
        }
                            
       //'obtiene el ultimo folio
        importaBancaElectronicaDao.actualizarFolioReal("no_folio_param");
       plFolio =importaBancaElectronicaDao.seleccionarFolioReal("no_folio_param");
        
       //'inserta en parametro
       //psFec_valor = Format(psFec_valor, "dd/MM/yyyy")
       psFecValorOriginal = dto.getFecValorString();
       
       return true; //Es de prueba
	}

	/*Sub revivir_solicitud_pago_rechazado(piRenglon As Integer, ByVal psOperacion_Dev As String, _
    ByVal plNoFolioRechazo As Long) */
	public String revivirSolicitudPago(ParamRevividorDto dtoRevividor){
		String psTipoCancelacion;
		String psRevividor="";
		int piSecuencia;
		String psOrigenMov;
		String psResultado="";
		String psEstatusMov;
		String mensaje="";
		Map plResp=new HashMap();
		List<MovimientoDto>listPagoRechazado= new ArrayList<MovimientoDto>();
		globalSingleton = GlobalSingleton.getInstancia();
		
		if(dtoRevividor.getReferencia()==null || dtoRevividor.getReferencia().equals("0"))
		{
			return "";//Se utiliza para salir de la funcion como el exit sub
		}
		listPagoRechazado=importaBancaElectronicaDao.consultarPagoRechazado(dtoRevividor.getReferencia(),dtoRevividor.getIdBanco(),
				dtoRevividor.getIdChequera(), dtoRevividor.getImporte());
		if(listPagoRechazado.size()>0)
		{
		    //'revivir la solicitud y al movimiento ponerle origen_mov = 'REC'
            //'Actualizar el no_docto del ingreso por Devolucion
			importaBancaElectronicaDao.actualizarDoctoRechazado(listPagoRechazado.get(0).getNoDocto(), dtoRevividor.getPlFolioRechazado());
			if(dtoRevividor.getPsOperacion()!=null && dtoRevividor.getPsOperacion().trim().equals("R"))
			{
				psTipoCancelacion="H";//'Para revivir la sol. sin tocar las chequeras
				psRevividor = "S";
			}
			else if(dtoRevividor.getPsOperacion()!=null && dtoRevividor.getPsOperacion().trim().equals("R"))
			{
				psTipoCancelacion="C";//'Cancela el movimiento
				psRevividor = "N";
			}
			else
			{
				return "";
			}
		
			psEstatusMov=listPagoRechazado!=null?listPagoRechazado.get(0).getIdEstatusMov():"";
			piSecuencia=1;
			psOrigenMov=listPagoRechazado!=null?listPagoRechazado.get(0).getOrigenMov():"";
			if(psOrigenMov!=null && psOrigenMov.trim().equals(""))
				psOrigenMov="SOI";
			
			   plResp =importaBancaElectronicaDao.ejecutarRevividor(psRevividor,listPagoRechazado.get(0).getNoFolioDet(),
					   listPagoRechazado.get(0).getIdTipoOperacion(),listPagoRechazado.get(0).getTipoCancelacion(), listPagoRechazado.get(0).getIdEstatusMov(),psOrigenMov,
					   listPagoRechazado.get(0).getIdFormaPago(),listPagoRechazado.get(0).getBEntregado(),listPagoRechazado.get(0).getIdTipoMovto(), 
					   listPagoRechazado.get(0).getImporte(), listPagoRechazado.get(0).getNoEmpresa(),listPagoRechazado.get(0).getNoCuenta(),
					   listPagoRechazado.get(0).getIdChequera(),listPagoRechazado.get(0).getIdBanco(),globalSingleton.getUsuarioLoginDto().getIdUsuario(),
					   listPagoRechazado.get(0).getNoDocto(),listPagoRechazado.get(0).getLoteEntrada(),
					   listPagoRechazado.get(0).getBSalvoBuenCobro(),""+ listPagoRechazado.get(0).getFecTrans(),listPagoRechazado.get(0).getIdDivisa(), 
					   psResultado, pbAutomatico);
			   
		    if(!plResp.get("result").equals("0"))
		    {
		    	if(plResp.get("result").equals("2000"))
		    	{
		    		if(!pbAutomatico)
                       mensaje="¿Este Movimiento no se puede Regresarse !" +"  El caso de cancelación no existe " + " De ser necesario consulte a su proveedor de sistemas SET";
		    	}
		    	else
		    	{
		    		psResultado = psResultado.replace("|","");
                    if(!pbAutomatico)
                        mensaje="Error en revividor  # " + plResp+ " SET";
		    	}
                  return mensaje;
		    }
		}
		
		return "1";  
	}
	
	public void importaBancaElectronica(){
		int res = importaBancaElectronicaDao.importaBancaElectronica();
		String result = "";
		
		System.out.println("entro a importar bancae");
		
		if(res==1)
			result = "Procedimiento ejecutado correctamente";
		else if(res==-1)
			result = "Ocurrio un error al ejecutar el procedimiento";
		
		
	}
	public ImportaBancaElectronicaDao getImportaBancaElectronicaDao() {
		return importaBancaElectronicaDao;
	}
	public void setImportaBancaElectronicaDao(
			ImportaBancaElectronicaDao importaBancaElectronicaDao) {
		this.importaBancaElectronicaDao = importaBancaElectronicaDao;
	}
	

}
