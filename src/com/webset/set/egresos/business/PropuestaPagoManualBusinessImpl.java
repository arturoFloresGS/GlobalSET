package com.webset.set.egresos.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webset.set.egresos.dao.PropuestaPagoManualDao;
import com.webset.set.egresos.dto.PropuestaPagoManualDto;
import com.webset.set.egresos.service.PropuestaPagoManualService;
import com.webset.set.global.business.GlobalBusiness;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.EstatusMovimientosDto;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.ParametroDto;
import com.webset.set.utilerias.dto.StoreParamsComunDto;
/**
 * 
 * @author Jessica Arelly Cruz Cruz
 * @since 14/02/2011
 *
 */
public class PropuestaPagoManualBusinessImpl implements PropuestaPagoManualService{
	PropuestaPagoManualDao propuestaPagoManualDao;
	private GlobalBusiness globalBusiness;
	Funciones funciones = new Funciones();
	SimpleDateFormat formato = new SimpleDateFormat("ddMMyyyy");
	private Bitacora bitacora = new Bitacora();
	 
	public Date obtenerFechaManana(){
		return propuestaPagoManualDao.obtenerFechaManana();
	}
	public Date obtenerFechaHoy(){
		return propuestaPagoManualDao.obtenerFechaHoy();
	}
	
	public List<LlenaComboGralDto>llenarComboGral(LlenaComboGralDto dto)
	{
		return propuestaPagoManualDao.llenarComboGral(dto);
	}
	
	public List<LlenaComboDivisaDto>llenarComboDivisa(String psDivisa){
		return propuestaPagoManualDao.llenarComboDivisa(psDivisa);
	}
	
	public String controlProyecto(int egreso, int bloqueo){
		return propuestaPagoManualDao.controlProyecto(egreso, bloqueo);
	}
	
	public List<LlenaComboGralDto>llenarComboGrupo(Map<String,Object> datos){
		return propuestaPagoManualDao.llenarComboGrupo(datos);
	}
	
	public List<LlenaComboGralDto>llenarComboBanco(Map<String,Object> datos){
		return propuestaPagoManualDao.llenarComboBanco(datos);
	}
	
	public List<LlenaComboGralDto>llenarComboChequera(Map<String,Object> datos){
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		String var = "";
		sb.append(datos.get("Persona"));
		var = sb.toString();
		
		
		try{
			if(var.equals("")){
				list = propuestaPagoManualDao.llenarComboChequera(datos);
			}else	
				list = propuestaPagoManualDao.llenarComboChequeraBenef(datos);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Egresos, C:PropuestaPagoManualBusinessImpl, M:llenarComboChequera");	
	
			
		}
		
		return list;
	}

	public List<LlenaComboGralDto>llenarCombo(LlenaComboGralDto dto){
		return propuestaPagoManualDao.llenarComboGral(dto);
	}
	
	public List<PropuestaPagoManualDto> consultarPropuestas(Map<String, String> datos){
		List<PropuestaPagoManualDto> list = new ArrayList<PropuestaPagoManualDto>();
		try{
			//globalSingleton = GlobalSingleton.getInstancia();
			//int iIdUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			//se agrego esta condicion para el caso en que se seleccionen todas las empresas
			//if(datos.get("noEmpresa").equals("0"))
			//	datos.put("noEmpresa", globalBusiness.obtenerCadenaEmpresasUsuario(iIdUsuario));
			
			list = propuestaPagoManualDao.consultarPropuestaPagoManual(datos);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Egresos, C:PropuestaPagoManualBusinessImpl, M:consultarPropuestas");	
					e.printStackTrace();
		}
		return list;
	}
	
	public double sumarImportePropuestas(Map<String, String> datos){
		try{
			//globalSingleton = GlobalSingleton.getInstancia();
			//int iIdUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			//se agrego esta condicion para el caso en que se seleccionen todas las empresas
			//if(datos.get("noEmpresa").equals("0"))
			//	datos.put("noEmpresa", globalBusiness.obtenerCadenaEmpresasUsuario(iIdUsuario));
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Egresos, C:PropuestaPagoManualBusinessImpl, M:sumarImportePropuestas");	
					e.printStackTrace();
		}
		return propuestaPagoManualDao.sumarImportePropuestas(datos);
	}
	
	public List<EstatusMovimientosDto> verificarEstatusMovtos(String psFolios){
		return propuestaPagoManualDao.verificarEstatusMovtos(psFolios);
	}
	
	
	/**
	 * metodo que define la clave de control de las propuestas,
	 * inserta en la tabla  seleccion_automatica_grupo
	 * y actualiza la tabla de movimiento, segun los folios que se
	 * hayan seleccionado en el grid.
	 * @param dto PropuestaPagoManualDto
	 * @return result Map<String,Object>
	 */
	public Map<String, Object> ejecutarPropuestas(List<PropuestaPagoManualDto> dto){
		Map<String,Object> result= new HashMap<String,Object>();
		List<EstatusMovimientosDto> estatus = new ArrayList<EstatusMovimientosDto>();
		StoreParamsComunDto cambiaCuadranteDto=new StoreParamsComunDto();
		boolean verificaEstatusMovto;
		int folio;
		int actualiza = 0;
		int inserta = 0;
		String psCveControl = "";
		String psDivision = "";
		String gdNuevaFechaPago = "";
		try{
			
			if(dto.get(0).getCmbPropuesta().equals(""))
			{
				//lb_transa = gobjSQL.BeginTransac()
            	propuestaPagoManualDao.actualizarFolioCupos(dto.get(0).getNoUsuario());
                folio = propuestaPagoManualDao.seleccionarFolioCupos(dto.get(0).getNoUsuario());
                	
                if(folio < 0)
				{
					result.put("msgUsuario", "En cat_usuario el folio para la Clave Control " +
							"es NULO para el usuario Num." + dto.get(0).getNoUsuario());
				     folio = -1;
				     return result;
				}
                
                psCveControl = "M"+folio;
                psCveControl = psCveControl + formato.format(propuestaPagoManualDao.obtenerFechaHoy());
                psCveControl = psCveControl + dto.get(0).getNoUsuario();
                
              //lb_transa = gobjSQL.CommitTransac()
			}
                
			//for(int i = 0; i < dto.size(); i++)
			//{	
				
				estatus = propuestaPagoManualDao.verificarEstatusMovtos(""+dto.get(0).getNoFolioDet());
				if (estatus.get(0).getMovMarcados() == estatus.get(0).getMovCorrectos())
				{
					verificaEstatusMovto = true;
				}
				else verificaEstatusMovto = false;
				
				
				if(dto.get(0).getIdBancoBenef() != 0 && !dto.get(0).getIdChequeraBenef().equals(""))
				{
					//If (giBanco <> 0 And gsChequera <> "") Or (giBanco <> 0 And chkFactoraje.Value = 1 And IsDate(gdNuevaFechaPago)) Then
					if(verificaEstatusMovto)
					{
						if(dto.get(0).getCmbPropuesta().equals(""))
						{
		                    PropuestaPagoManualDto dtoInsert = new PropuestaPagoManualDto();
		                    dtoInsert.setCveControl(psCveControl);
		                    dtoInsert.setFecPropuesta(dto.get(0).getFecPropuesta());
		                    dtoInsert.setIdGrupoFlujo(dto.get(0).getIdGrupoFlujo());
		                    dtoInsert.setMontoMaximo(dto.get(0).getMontoMaximo());
		                    dtoInsert.setImporteCupo(dto.get(0).getImporteCupo());
		                    
		                  //INSERTAMOS EN SELECCION_AUTOMATICA_GRUPO EL CUPO MANUAL
		                    inserta = propuestaPagoManualDao.crearCupoManual(dtoInsert, psDivision);
						}
						/* por el momento esto no es necesario ya que no se ha llenado el combo de propuestas
						 * Else
		                    psCveControl = psAdCveProp
		                    
		                    If cmbDivision.ListIndex = -1 Then
		                        Call gobjSQL.funSQLUpdateCupoManualGrupo(True, _
		                                        pdimporteCupo, pdimporteCupo, plCupoAct, _
		                                        psFecPropuestaAct, psCveControl, , True)
		                    Else
		                        Call gobjSQL.funSQLUpdateCupoManualGrupo(True, _
		                                        pdimporteCupo, pdimporteCupo, plCupoAct, _
		                                        psFecPropuestaAct, psCveControl, _
		                                        matDivision(cmbDivision.ItemData(cmbDivision.ListIndex)), _
		                                        True)
		                    End If
		                End If
						 * */
	                    
	                    //ACTUALIZAMOS FECHA PROPUESTA Y CVE_CONTROL DE LOS MOVIMIENTOS
						PropuestaPagoManualDto dtoUpdate = new PropuestaPagoManualDto();
	                    	dtoUpdate.setFecPago(dto.get(0).getFecPago());
	                    	dtoUpdate.setFecPropuesta(dto.get(0).getFecPropuesta());
	                    	dtoUpdate.setCveControl(psCveControl);
	                    	dtoUpdate.setNoFolioDet(dto.get(0).getNoFolioDet());
	                    	dtoUpdate.setIdBancoBenef(dto.get(0).getIdBancoBenef());
	                    	dtoUpdate.setIdChequeraBenef(dto.get(0).getIdChequeraBenef());
	                    	dtoUpdate.setTipoCambio(dto.get(0).getTipoCambio() <0 ? -1 : dto.get(0).getTipoCambio());
	                    	dtoUpdate.setGSIDCAJA(dto.get(0).getGSIDCAJA() <0 ? -1 : dto.get(0).getGSIDCAJA());
	                    	dtoUpdate.setCmbPropuesta(dto.get(0).getCmbPropuesta());
	                    	//mbCompraTranfer
	                    	//gdNuevaFechaPago
	                    	if(inserta > 0)
	                    		actualiza = propuestaPagoManualDao.actualizarFechaProp(dtoUpdate);
	                    	else{
	                    		result.put("msgUsuario", "El registro no se inserto, verifique.");
	                    		return result;
	                    	}
			                
			                /* esta es la condicion equivalenate a la de abajo
			                * if(dto.get(i).getIdDivisa().trim() == dto.get(i).getIdDivisaPago().trim() &&
	                    		dto.get(i).getIdDivisa().trim().equals("MN") && dto.get(i).getTipoCambio() > 1)
	                    		{}
	                    
			                If Trim(txtIdDivisa.Text) = Trim(txtIdDivisaPago.Text) And _
			                   Trim(txtIdDivisa.Text) <> "MN" And CDbl(txtTipoCambio.Text) <> 1 Then
			                    Call gobjSQL.funSQLUpdateFechaProp(txtFecPago.Text, psFecPropuesta, psCveControl, _
			                                                       psFolios, giBanco, gsChequera, txtTipoCambio.Text, _
			                                                       GI_ID_CAJA, mbCompraTranfer, gdNuevaFechaPago)
			                  Else
			                    If cmbPropuestasAct.ListIndex = 0 Then
			                        Call gobjSQL.funSQLUpdateFechaProp(txtFecPago.Text, psFecPropuesta, psCveControl, _
			                                                           psFolios, giBanco, gsChequera, , GI_ID_CAJA, mbCompraTranfer, gdNuevaFechaPago)
			                    End If
			                End If
			                
			                If cmbPropuestasAct.ListIndex <> 0 Then
			                    Call gobjSQL.funSQLUpdateFechaProp(txtFecPago.Text, txtFecPago.Text, psCveControl, _
			                                                       psFolios, giBanco, gsChequera, , GI_ID_CAJA, mbCompraTranfer, gdNuevaFechaPago)
			                End If
			                
			                //falta migrar de aqui en adelante, por ahora no es necesario
			                'Llama a ajuste aun que parezca que es cuadrantes*/
	                    	//Map<String,Object> plresp= new HashMap<String,Object>();
			                
			                String psFolios = dto.get(0).getNoFolioDet()+ ","; //**** Se agrega la coma al final de la cadena folios
			                                          //****porque asi la toma CambiaCuadrantes
			                
			                cambiaCuadranteDto= new StoreParamsComunDto();
							
			                
			                if(formato.format(dto.get(0).getFecPropuesta()) != formato.format(propuestaPagoManualDao.obtenerFechaHoy()))
			                {
			                    if(gdNuevaFechaPago.equals(""))
			                    {
			                    	cambiaCuadranteDto.setParametro(dto.get(0).getNoUsuario() + "," + dto.get(0).getIdFormaPago()+","
											+funciones.ponerFecha(dto.get(0).getFecPropuesta()) + "," + "" + ","
											+ "" + "," + 0 + "," + psFolios);
			                    }
			                    else
			                    {
			                    	cambiaCuadranteDto.setParametro(dto.get(0).getNoUsuario() + ","+ 7 +","
											+funciones.ponerFecha(dto.get(0).getFecPropuesta()) + "," + "" + ","
											+ "" + "," + 0 + "," + psFolios);
			                    }
			                        //plresp=propuestaPagoManualDao.cambiarCuadrantes(cambiaCuadranteDto);
			                }
//			                logger.info("cuadrantes " + plresp.get("result"));
//			                if(plresp.get("result") == null)
//			                {
//			                	result.put("msgUsuario", "� Error en CambiaCuadrantes #" + plresp.get("result") +  " error no catalogado! " +
//			                			"Datos registrados con la clave "+psCveControl);
//			                    return result;
//			                }
						 
					}else
					{
						result.put("msgUsuario", "� Error hay movimientos que cambiaron de estatus refresque la pantalla" +
								" Intente otra vez ");
						return result;
					}
				}
				else
				{
					return result;
				}
				
//				if(lbSeleccion)
//				{
//			        txtTotalPagar.Text = Format$(Me.txtTotalPagar.Text, "###,###,###,###,##0.00")
//			        txtTotalPago = Format$(Me.txtTotalPago.Text, "###,###,###,###,##0.00")
//			        Txttotalregistros = Format$(Me.Txttotalregistros, "###,##0")
//			        Screen.MousePointer = 0
//			        MsgBox "Datos Registrados", vbInformation, "SET"
//			    }else
//			    {
//			        return result.put("msgUsuario", "limpiar");
//			    }
				/*
			    If pb_ejecuto_pago = False Then
			        MsgBox "Datos registrados con la Cve. " & psCveControl, vbExclamation, "SET"
			    End If*/
				if(actualiza > 0)
					result.put("msgUsuario", "Datos registrados con la clave "+psCveControl);
				else{
            		result.put("msgUsuario", "Error al registrar la propuesta, verifique.");
            		return result;
            	}
			//}//for
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:PropuestaPagoManualBusinessImpl, M:ejecutarPropuestas");	
			e.printStackTrace();
		}
		return result;
	}
	
	public String muestraComponentes(int param){
		String var = "";
		var = propuestaPagoManualDao.muestraComponentes(param);
		
		return var;
	}
	public List<PropuestaPagoManualDto> obtenerDivision (int usuario){
		return propuestaPagoManualDao.obtenerDivision(usuario);
	}
		
	public List<PropuestaPagoManualDto> obtenerDivisaAct (int empresa, String division){
		 return propuestaPagoManualDao.obtenerDivisaAct(empresa, division); 
	}
	public List<PropuestaPagoManualDto> SelectBancoCheqBenef (String cliente, String Divisa, int banco){
		List<PropuestaPagoManualDto> list = new ArrayList<PropuestaPagoManualDto>();
		try{
			list = propuestaPagoManualDao.SelectBancoCheqBenef(cliente,Divisa,banco);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Egresos, C:PropuestaPagoManualBusinessImpl, M:SelectBancoCheqBenef");
		}	
		
		return list;  
	}	
	public int UpdateBancoCheqBenef(String folio, int Banco, String Chequera){
		int res = 0;
		try{
			res = propuestaPagoManualDao.UpdateBancoCheqBenef(folio, Banco, Chequera);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Egresos, C:PropuestaPagoManualBusinessImpl, M:UpdateBancoCheqBenef");
		}
		return res;
	}
	public String ConfiguraSet(int param, String cliente){
		String sino = "";
		int valor = 206;
		String msg = "";
		try{
			sino = propuestaPagoManualDao.muestraComponentes(param);
			if(sino == "NO"){
				sino = propuestaPagoManualDao.muestraComponentes(valor);
				if(sino.indexOf(cliente) > 0){
					msg = "No se puede hacer un Pago Parcial a un proveedor Incidental";
				}
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Egresos, C:PropuestaPagoManualBusinessImpl, M:ConfiguraSet");
		}
		return msg;
	}
	
	public List<PropuestaPagoManualDto> consultarPropuestasAgregar(int noGrupoEmpresa) {
		return propuestaPagoManualDao.consultarPropuestasAgregar(noGrupoEmpresa);
	}
	
	public String agregarPropuestas(int noGrupoEmpresa, String cveControl, String sFolios) {
		String resp = "";
		int resul = 0;
		List<PropuestaPagoManualDto> list = new ArrayList<PropuestaPagoManualDto>();
		List<PropuestaPagoManualDto> list2 = new ArrayList<PropuestaPagoManualDto>();
		List<PropuestaPagoManualDto> list3 = new ArrayList<PropuestaPagoManualDto>();
		
		try{
			list = propuestaPagoManualDao.selectPropuestaAcutal(noGrupoEmpresa, cveControl);
			
			if(list.size() > 0) {
				if(propuestaPagoManualDao.consultarConfiguraSet(240).equals("SI")) {
					if(list.get(0).getUsr1() != 0 || list.get(0).getUsr2() != 0 || list.get(0).getUsr3() != 0) {
						return "No pueden adicionarse mas movimientos a esta propuesta, " +
                               "hasta que no se eliminen las autorizaciones hasta ahora dadas";
					}
				}
			}else {
				return "Hubo un error, al tratar de recuperar los datos de la propuesta";
			}
			list3 = propuestaPagoManualDao.buscaBancoCheqPagadoras(cveControl);
			
			if(list3.size() == 0)
				return "No existen facturas en esta propuesta, no se puede agregar debido a que no se tiene el banco y chequera pagadora";
			
			list2 = propuestaPagoManualDao.buscaMovimientos(sFolios);
			
			if(list2.size() > 0) {
				for(int i=0; i<list2.size(); i++) {
					resul = propuestaPagoManualDao.actualizarPropuesta(cveControl, list2.get(i).getImporte(), noGrupoEmpresa);
					resul = propuestaPagoManualDao.actualizarMovimientos(cveControl, list2.get(i).getNoFolioMov(), list.get(0).getFecPropuesta(), Integer.parseInt(list3.get(0).getIdBanco()), list3.get(0).getIdChequera());
				}
			}else
				resp = "Error no encontro la informacion del registro agregar";
			
			if(resul == 0)
				resp = "No se puedieron agregar las facturas a la propuesta seleccionada";
			else
				resp = "Las facturas han sido agregadas correctamente a la propuesta seleccionada";
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Egresos, C:PropuestaPagoManualAction, M:agregarPropuestas");
		}
		return resp;
	}

	
	public Map<String,Object> ejecutarSolicitud(Map<String, String> datIn, List<Map<String, String>> detalle){
		Map<String, Object> mapReturn= new HashMap<String, Object>();
//		List<Map<String,String>> listDetalle = new ArrayList<Map<String, String>>();
//		String psBenef="";
		try{
				int  gICuentaEmp=0; 
				int folioNuevo=0;
				int folioParametro=0;
				int folioParametro1=0;
//				int insUno=0;
//				int insDet=0;
//				int grupo=0;
				Map<String, Object> pdResp;
				String  idEmpresa=datIn.get("idEmpresa").toString();
				String idBanco=datIn.get("idBancoBenef").toString();
				String noDocto=(String)datIn.get("noDocto");
				double parcial=Double.parseDouble((String)datIn.get("importePago"));
				//String sresta=String.valueOf(resta);
				mapReturn.put("terminado", "0");
				
			    if(datIn.get("chkProvUnico").equals("true") && datIn.get("idFormaPago").equals("3")) 
		        {
		            if(datIn.get("optNac").equals("true"))
		            {
		                if(Integer.parseInt(idBanco) < 1000 && !datIn.get("idDivOriginal").equals("MN"))
		                {
		                     if(datIn.get("chequera").toString().trim().length()>25)
		                     {
		                            mapReturn.put("msgUsuario", "La chequera debe ser menor de 25");
		                            return mapReturn;
		                     }
		                }
		                else
		                {
		                    if(idBanco.trim().equals("127"))
		                    {
		                        if(datIn.get("chequera").toString().trim().length()!=14) 
		                        {
		                        	mapReturn.put("msgUsuario", " Las chequeras de Banco Azteca deben ser de 14 caracteres");
		                            return mapReturn;
		                        }
		                    }
		                    if(idBanco.trim().equals("12") && !(datIn.get("observaciones").equals( "PAGO PARCIAL" )) )
		                    {
		                    	if(datIn.get("chequera").toString().trim().substring(0,2).equals("91"))
		                    	{
		                            if(datIn.get("chequera").toString().trim().length()!=13)
		                            {
		                            	mapReturn.put("msgUsuario", "Las chequeras de Bancomer deben ser de 13 caracteres");
			                            return mapReturn;
		                            }
		                    	}
		                        else
		                        {
		                        	if(datIn.get("chequera").toString().trim().length()!=11)
		                        	{
		                        		if(datIn.get("chequera").toString().trim().length()!=18)
		                        		{
		                        			if(datIn.get("chequera").toString().trim().length()!=25)
		                        			{
		                                        mapReturn.put("msgUsuario", "Las chequeras de Bancomer deben ser de 11,18 � 25 caracteres");
					                            return mapReturn;
		                        			}
		                        		}
		                        	}
		                        }
		                    }
		                    else
		                    {
		                    	
		                    	
		                    	if( ! datIn.get("observaciones").equals( "PAGO PARCIAL" ) ){
			                    	if(datIn.get("chequera").toString().trim().length()!=11 )
		                        	{
		                        		if(datIn.get("chequera").toString().trim().length()!=18)
		                        		{
		                        			if(datIn.get("chequera").toString().trim().length()!=25)
		                        			{
		                                        mapReturn.put("msgUsuario", "Las chequeras de Bancomer deben ser de 11,18 � 25 caracteres");
					                            return mapReturn;
		                        			}
		                        		}
		                        	}
		                    	}
		                    }
		                }
		            }
		        }
	        gICuentaEmp = propuestaPagoManualDao.obtenerNoCuenta(Integer.parseInt(idEmpresa));
	       
	        if(!noDocto.equals(""))
	        {
	        	if((datIn.get("cmbOrigenProv")!=null && !datIn.get("cmbOrigenProv").equals("NOMINA")) && propuestaPagoManualDao.consultarConfiguaraSet(1).equals("COSTCO"))
	        	{
		        	if(!propuestaPagoManualDao.verificarDocumento(Integer.parseInt(noDocto), Integer.parseInt(idEmpresa)))
		            {
		            	mapReturn.put("msgUsuario", "Este n�mero de documento ya existe");
	                    return mapReturn;
		            }
	        	}
	        }
	        else{
	        	mapReturn.put("msgUsuario", "Falta el n�mero de Documento");
                return mapReturn;
	        }
	        
	        if(!noDocto.equals("") && propuestaPagoManualDao.consultarConfiguaraSet(1).equals("COSTCO")) 
	        {
	        	if(datIn.get("cmbOrigenProv").equals("NOMINA") && propuestaPagoManualDao.consultarConfiguaraSet(1).equals("COSTCO"))
	               folioNuevo = Integer.parseInt(noDocto);
	            else
	               folioNuevo = Integer.parseInt(noDocto);
	        }    
	        else
	        {
	        	propuestaPagoManualDao.actualizarFolioReal("no_folio_docto");
	            folioNuevo = propuestaPagoManualDao.seleccionarFolioReal("no_folio_docto");
	            if((datIn.get("cmbOrigenProv")!=null && !datIn.get("cmbOrigenProv").equals("NOMINA")) && propuestaPagoManualDao.consultarConfiguaraSet(1).equals("COSTCO"))
	        	{
		        	if(!propuestaPagoManualDao.verificarDocumento(Integer.parseInt(noDocto), Integer.parseInt(idEmpresa)))
		            {
		            	mapReturn.put("msgUsuario", "Este n�mero de documento ya existe");
	                    return mapReturn;
		            }
	        	}
	        }	
	        
	        propuestaPagoManualDao.actualizarFolioReal("no_folio_param");
	    folioParametro= propuestaPagoManualDao.seleccionarFolioReal("no_folio_param");
        if(folioParametro<=0)
        {
        	mapReturn.put("msgUsuario", "Error al obtener: no_folio_param");
            return mapReturn;
        }
//        String cheqPag="";
        
//        if(datIn.get("idFormaPago").equals("2"))
//        {
//        	cheqPag=propuestaPagoManualDao.obtenerChequeraPagadora((String)datIn.get("idDivOriginal"));
//        }
        
//        if(datIn.get("idFormaPago").equals("3") )
//        {
//        	
//        	if( datIn.get("observaciones").equals( "PAGO PARCIAL" ) )
//        	{	        	
//        		cheqPag= (String)datIn.get("idChequera");
//        		
//        	
//        	}	        	
//        	
//        }
	        folioParametro1= folioParametro;
	        ParametroDto insertUno=new ParametroDto();
	        insertUno.setNoEmpresa(Integer.parseInt((String) datIn.get("idEmpresa")));
	        insertUno.setNoFolioParam(folioParametro);
	        insertUno.setIdFormaPago(Integer.parseInt((String)datIn.get("idFormaPago")));
	        insertUno.setUsuarioAlta(Integer.parseInt((String)datIn.get("idUsuario")));
	        insertUno.setNoCuenta(gICuentaEmp);
	        insertUno.setNoDocto( (String)datIn.get("noDocto") );
        	insertUno.setNoFactura((String)datIn.get("noFactura"));
	        insertUno.setIdBanco( Integer.parseInt(  datIn.get("idBanco") ) );
	        insertUno.setFecValor(funciones.ponerFechaDate((String)datIn.get("fechaPago")));
	        insertUno.setFecOperacion(funciones.ponerFechaDate((String)datIn.get("fechaFactura")));
	        insertUno.setFecAlta(propuestaPagoManualDao.obtenerFechaHoy());
	        insertUno.setFecValorOriginal(funciones.ponerFechaDate((String)datIn.get("fechaFactura")));//duda?verificar estas cuatro fechas
	        insertUno.setIdDivisa((String)datIn.get("idDivPago"));
	        insertUno.setImporte(Double.parseDouble((String)datIn.get("importePago")));//duda?importes
	        insertUno.setImporteOriginal(Double.parseDouble((String)datIn.get("importeOriginal")));
	        insertUno.setTipoCambio(Double.parseDouble((String)datIn.get("tipoCambio")));
	        insertUno.setIdCaja(datIn.get("idCaja")!=null && !datIn.get("idCaja").equals("") 
	        		? Integer.parseInt((String)datIn.get("idCaja")):0);
	        insertUno.setIdDivisaOriginal((String)datIn.get("idDivOriginal"));
	        insertUno.setBeneficiario((String)datIn.get("nomPersona"));
	        insertUno.setNoCliente(""+datIn.get("idPersona"));
	        insertUno.setConcepto((String)datIn.get("concepto"));
	        insertUno.setIdBancoBenef(Integer.parseInt((String)datIn.get("idBancoBenef")));
	        insertUno.setIdChequeraBenef(datIn.get("chequera")!=null && !datIn.get("chequera").equals("")
	        		? (String)datIn.get("chequera") : "0");
	        insertUno.setIdLeyenda(datIn.get("chkLeyenda").equals("true")?"1":"0");
	        insertUno.setIdChequera(datIn.get("idChequera")!=null && !datIn.get("idChequera").equals("")
	        		? (String)datIn.get("idChequera") : "0");
	        insertUno.setObservacion((String)datIn.get("observaciones"));
	        insertUno.setGrupo(folioParametro1);
	        insertUno.setAplica(1);
	        insertUno.setIdEstatusMov("C");
	        insertUno.setBSalvoBuenCobro("S");
	        insertUno.setIdEstatusReg("P");
	        insertUno.setIdTipoDocto(41);
	        insertUno.setIdTipoOperacion(3000);
	        insertUno.setFolioRef(0);
	        insertUno.setClabe(datIn.get("clabe") != null && !datIn.get("clabe").equals("")
	        		? datIn.get("clabe") : "0");
	        insertUno.setSolicita((String)datIn.get("solicita"));
	        insertUno.setAutoriza((String)datIn.get("autoriza"));
	        insertUno.setPlaza(datIn.get("plaza")!=null && !datIn.get("plaza").equals("") 
	        		? Integer.parseInt((String) datIn.get("plaza")) : 0);
	        insertUno.setSucursal(datIn.get("sucursal")!=null && !datIn.get("sucursal").equals("")
	        		? Integer.parseInt((String)datIn.get("sucursal")) : 0);
	        insertUno.setNoFolioMov(1);
	        insertUno.setAgrupa1(0);
	        insertUno.setAgrupa2(0);
	        insertUno.setAgrupa3(0);
	        insertUno.setAdu2((String)datIn.get("origenMov"));
	        insertUno.setIdArea(detalle.get(0).get("area") != null ? Integer.parseInt(detalle.get(0).get("area").toString()) : 0);
	        insertUno.setIdRubroInt(detalle.get(0).get("rubro")!=null && !detalle.get(0).get("rubro").equals("0")?detalle.get(0).get("rubro"):"0");
	        insertUno.setDivision(detalle.get(0).get("departamento")!=null && !detalle.get(0).get("departamento").equals("0")?(String)detalle.get(0).get("departamento"):"");       
	        insertUno.setIdGrupo(detalle.get(0).get("grupo")!=null && !detalle.get(0).get("grupo").equals("0")?detalle.get(0).get("grupo"):"0");
	        insertUno.setReferencia("");
	        insertUno.setNoPedido(0);
	        insertUno.setLote(2);
	        insertUno.setClabe(datIn.get("clabe") != null && !datIn.get("clabe").equals("")
            		? datIn.get("clabe") : "0");
	        propuestaPagoManualDao.insertarParametro(insertUno);
	        ParametroDto insertDet=new ParametroDto();
	        
	        for(int i=0;i<detalle.size();i++)
	        {
	        	propuestaPagoManualDao.actualizarFolioReal("no_folio_param");
	        	folioParametro= propuestaPagoManualDao.seleccionarFolioReal("no_folio_param");
	        	System.out.println(folioParametro);
	        	
	        	
	        	insertDet=new ParametroDto();
	        	insertDet.setNoEmpresa(Integer.parseInt((String) datIn.get("idEmpresa")));
	        	insertDet.setNoFolioParam(folioParametro);
		        insertDet.setIdFormaPago(Integer.parseInt((String)datIn.get("idFormaPago")));
		        insertDet.setUsuarioAlta(Integer.parseInt((String)datIn.get("idUsuario")));
		        insertDet.setNoCuenta(gICuentaEmp);
		        insertDet.setNoDocto( (String)datIn.get("noDocto") );
		        insertDet.setNoFactura((String)datIn.get("noFactura"));
	        	insertDet.setIdBanco( Integer.parseInt(  datIn.get("idBanco") ) );
		        insertDet.setFecValor(funciones.ponerFechaDate((String)datIn.get("fechaPago")));
		        insertDet.setFecOperacion(funciones.ponerFechaDate((String)datIn.get("fechaFactura")));
		        insertDet.setFecAlta(propuestaPagoManualDao.obtenerFechaHoy());
		        insertDet.setFecValorOriginal(funciones.ponerFechaDate((String)datIn.get("fechaFactura")));
		        insertDet.setIdDivisa((String)datIn.get("idDivPago"));
		        insertDet.setImporte(Double.parseDouble((String)datIn.get("importePago")));
		        insertDet.setImporteOriginal(Double.parseDouble((String)datIn.get("importeOriginal")));
		        insertDet.setTipoCambio(Double.parseDouble((String)datIn.get("tipoCambio")));
		        insertDet.setIdCaja(datIn.get("idCaja")!=null && !datIn.get("idCaja").equals("") 
		        		? Integer.parseInt((String)datIn.get("idCaja")):0);
		        insertDet.setIdDivisaOriginal((String)datIn.get("idDivOriginal"));
		        insertDet.setBeneficiario((String)datIn.get("nomPersona"));
		        insertDet.setNoCliente(""+datIn.get("idPersona"));
		        insertDet.setConcepto((String)datIn.get("concepto"));
		        insertDet.setIdBancoBenef(Integer.parseInt((String)datIn.get("idBancoBenef")));
		        insertDet.setIdChequeraBenef(datIn.get("chequera")!=null && !datIn.get("chequera").equals("")
		        		? (String)datIn.get("chequera") : "0");
		        insertDet.setIdLeyenda(datIn.get("chkLeyenda").equals("true")?"1":"0");
		        insertDet.setIdChequera(datIn.get("idChequera")!=null && !datIn.get("idChequera").equals("")
		        		? (String)datIn.get("idChequera") : "0");
		        insertDet.setObservacion((String)datIn.get("observaciones"));
		        insertDet.setGrupo(folioParametro1);
		        insertDet.setAplica(1);
		        insertDet.setIdEstatusMov("H");
		        insertDet.setBSalvoBuenCobro("S");
		        insertDet.setIdEstatusReg("P");
		        insertDet.setIdTipoDocto(41);
		        insertDet.setIdTipoOperacion(3001);
		        if(i>0)
	            	insertDet.setFolioRef(2);
	            else
	            	insertDet.setFolioRef(1);
		        insertDet.setClabe(datIn.get("clabe") != null && !datIn.get("clabe").equals("")
		        		? datIn.get("clabe") : "0");
		        insertDet.setSolicita((String)datIn.get("solicita"));
		        insertDet.setAutoriza((String)datIn.get("autoriza"));
		        insertDet.setPlaza(datIn.get("plaza")!=null && !datIn.get("plaza").equals("") 
		        		? Integer.parseInt((String) datIn.get("plaza")) : 0);
		        insertDet.setSucursal(datIn.get("sucursal")!=null && !datIn.get("sucursal").equals("")
		        		? Integer.parseInt((String)datIn.get("sucursal")) : 0);
		        insertDet.setNoFolioMov(1);
		        insertDet.setAgrupa1(0);//los campos agrupa estan fijos ya que en la interfaz por ahora no se muestran
		        insertDet.setAgrupa2(0);
		        insertDet.setAgrupa3(0);
		        insertDet.setAdu2((String)datIn.get("origenMov"));
		        insertDet.setIdArea(detalle.get(0).get("area") != null ? Integer.parseInt(detalle.get(0).get("area").toString()) : 0);
		        insertDet.setIdRubroInt(detalle.get(0).get("rubro")!=null && !detalle.get(0).get("rubro").equals("0")?detalle.get(0).get("rubro"):"0");
		        insertDet.setDivision(detalle.get(0).get("departamento")!=null && !detalle.get(0).get("departamento").equals("0")?(String)detalle.get(0).get("departamento"):"");       
		        insertDet.setIdGrupo(detalle.get(0).get("grupo")!=null && !detalle.get(0).get("grupo").equals("0")?detalle.get(0).get("grupo"):"0");
		        insertDet.setReferencia("");
		        insertDet.setNoPedido(0);
		        insertDet.setLote(2);
		        insertDet.setClabe(datIn.get("clabe") != null && !datIn.get("clabe").equals("")
	            		? datIn.get("clabe") : "0");
		        
	            propuestaPagoManualDao.insertarParametro(insertDet);
	        }
	        
	        ////
	        pdResp=null;
	        GeneradorDto generadorDto = new GeneradorDto();
	        generadorDto.setEmpresa(Integer.parseInt(datIn.get("idEmpresa")));
	        generadorDto.setFolParam(folioParametro1);
	        generadorDto.setFolMovi(0);
	        generadorDto.setFolDeta(0);
	        pdResp=propuestaPagoManualDao.ejecutarGenerador(generadorDto);
	        System.out.println("generador  "+pdResp.get("result"));
	        if(Integer.parseInt(pdResp.get("result").toString())!=0)
	        {
	        	mapReturn.put("msgUsuario","Error " +pdResp.get("result")+" enGenerador "+"no_folio_param="+folioParametro1);
	            return mapReturn;
	            
	        }
	        else
	        {
	        	
	        	if( datIn.get("observaciones").equals( "PAGO PARCIAL" ) ){	
	        		
	        		String strNoFolioDet = "";
	        		String strNoFolioMov = "";
	        		double ImporteParcial = 0.0;
	        		String strDescripcion = "";
	        		
	        		strNoFolioDet = ( (String) datIn.get("noFolioDet") ) ;
	        		strNoFolioMov = ( (String) datIn.get("noFolioMov") ) ;
	        		ImporteParcial = parcial;
	        		strDescripcion = "PAGO PARCIALIZADO" ;
	        		
	        		propuestaPagoManualDao.actualizaPagoCompleto(strNoFolioDet,ImporteParcial,strDescripcion);
	        		propuestaPagoManualDao.actualizaPagoCompleto3001(strNoFolioMov,ImporteParcial,strDescripcion);
	        		
	        	}
	        	
	        	propuestaPagoManualDao.actualizarClabe(pdResp.get("folDeta").toString(), insertUno.getClabe());
					mapReturn.put("msgUsuario", "Datos registrados para el docto: "+folioNuevo);
					mapReturn.put("terminado", "1");
				
	        }
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:CapturaSolicitudesPagoBussines, M:ejecutarSolicitud");
		}
		
		return mapReturn;
	}
	
	public List<PropuestaPagoManualDto> llenaComboGrupoConta(String idTipoMovto, String idSubGrupo) {
		return propuestaPagoManualDao.llenaComboGrupoConta(idTipoMovto, idSubGrupo);
	}
	
	public List<PropuestaPagoManualDto> llenaComboSubGrupo(int tipoGrupo) {
		return propuestaPagoManualDao.llenaComboSubGrupo(tipoGrupo);
	}
	
	public List<PropuestaPagoManualDto> llenaComboSubSubGrupo(int idRubroC) {
		return propuestaPagoManualDao.llenaComboSubSubGrupo(idRubroC);
	}
	
	public String buscaCtaContable(String datosConta) {
		return propuestaPagoManualDao.buscaCtaContable(datosConta);
	}
	
	public List<PropuestaPagoManualDto> llenarComboDepCCProy(int noEmpresa, int tipoCombo) {
		List<PropuestaPagoManualDto> list = new ArrayList<PropuestaPagoManualDto>();
		
		try {
			switch(tipoCombo) {
				case 1:
					list = propuestaPagoManualDao.llenarComboDepto(noEmpresa);
					break;
				case 2:
					list = propuestaPagoManualDao.llenarComboCCosto(noEmpresa);
					break;
				case 3:
					list = propuestaPagoManualDao.llenarComboProyecto(noEmpresa);
					break;
			}
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Egresos, C:CapturaSolicitudesPagoBussines, M:llenarComboDepCCProy");
		}
		return list;
	}
	
	public String modificaImporteApagar(int noFolioDet, double importe) {
		int resp = 0;
		String respuesta = "";
		
		resp = propuestaPagoManualDao.modificaImporteApagar(noFolioDet, importe);
		
		if(resp == 0)
			respuesta = "Error al modificar el importe";
		else
			respuesta = "Modificaci�n correcta";
		return respuesta;
	}
	
	public PropuestaPagoManualDao getPropuestaPagoManualDao() {
		return propuestaPagoManualDao;
	}

	public void setPropuestaPagoManualDao(
			PropuestaPagoManualDao propuestaPagoManualDao) {
		this.propuestaPagoManualDao = propuestaPagoManualDao;
	}

	public GlobalBusiness getGlobalBusiness() {
		return globalBusiness;
	}

	public void setGlobalBusiness(GlobalBusiness globalBusiness) {
		this.globalBusiness = globalBusiness;
	}
}
