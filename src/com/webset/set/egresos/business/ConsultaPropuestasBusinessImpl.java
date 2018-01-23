package com.webset.set.egresos.business;
/**
 * @autor Cristian Garcia Garcia
 */
 
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.egresos.dao.ConsultaPropuestasDao;
import com.webset.set.egresos.dao.impl.ConsultaPropuestasDaoImpl;
import com.webset.set.egresos.dao.impl.ConsultasGeneralesEgresosDao;
import com.webset.set.egresos.dto.BitacoraPropuestasDto;
import com.webset.set.egresos.dto.BloqueoPagoCruzadoDto;
import com.webset.set.egresos.dto.ColoresBitacoraDto;
import com.webset.set.egresos.dto.ConsultaPropuestasDto;
import com.webset.set.egresos.dto.PagosPropuestosDto;
import com.webset.set.egresos.dto.ParamConsultaPropuestasDto;
import com.webset.set.egresos.service.ConsultaPropuestasService;
import com.webset.set.traspasos.dto.GrupoDTO;
import com.webset.set.traspasos.dto.RubroDTO;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.GrupoEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.SeleccionAutomaticaGrupoDto;
import com.webset.utils.tools.Utilerias;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

@SuppressWarnings("deprecation")
public class ConsultaPropuestasBusinessImpl implements ConsultaPropuestasService{

	private ConsultaPropuestasDao consultaPropuestasDao;
	Funciones funciones= new Funciones();
	Bitacora bitacora = new Bitacora(); 
	GlobalSingleton globalSingleton;
	private String autoDesAuto = "";
	private ConsultasGeneralesEgresosDao consultasGeneralesEgresosDao;
	private Logger logger = Logger.getLogger(ConsultaPropuestasBusinessImpl.class);
	
	public List<LlenaComboGralDto> LlenarComboGral(LlenaComboGralDto dto) {
		return consultaPropuestasDao.llenarComboGral(dto);
	}
	
	public Date obtenerFechaHoy(){
		return consultaPropuestasDao.obtenerFechaHoy();
	}

	public List<LlenaComboGralDto>llenarComboBeneficiario(LlenaComboGralDto dto){
		return consultasGeneralesEgresosDao.llenarComboBeneficiario(dto);	
	}
	
	public List<SeleccionAutomaticaGrupoDto> consultarSeleccionAutomatica(ConsultaPropuestasDto dtoIn){
		List<SeleccionAutomaticaGrupoDto>list= new ArrayList<SeleccionAutomaticaGrupoDto>();
		int count=0;
		//String rech="";
		
		try {
			list=consultaPropuestasDao.consultarSeleccionAutomatica(dtoIn);
			
			
			if(list.size()>0) {
				for(int i=0; i<list.size();i++) {
//					count = consultaPropuestasDao.consultaAutorizada(list.get(i).getCveControl());//checar
//					if(count > 0){
//						list.get(i).setEstatus("AUTORIZADA");
//						list.get(i).setColor("color:#01DF01");	
//						continue;
//					}
					count = list.get(i).getTieneAutorizacion();//checar
					if(count > 0){
						list.get(i).setEstatus("AUTORIZADA");
						list.get(i).setColor("color:#01DF01");	
						continue;
					}
										//Autorizadas
            		if(list.get(i).getNivelAutorizacion() == 1){
						if((list.get(i).getUser1()!= null && !list.get(i).getUser1().equals(""))){
								
//		            		list.get(i).setColor("color:#01DFD7");	//verde autorizadas
//		            		list.get(i).setEstatus("REVISADA. EN ESPERA DE AUTORIZACIï¿½N");
							list.get(i).setEstatus("AUTORIZADA");
							list.get(i).setColor("color:#01DF01");	
		            		continue;
						}
					}else if(list.get(i).getNivelAutorizacion() == 2){
						if((list.get(i).getUser1()!= null && !list.get(i).getUser1().equals("")) 
								&& (list.get(i).getUser2() != null && !list.get(i).getUser2().equals(""))){
								
//		            		list.get(i).setColor("color:#01DFD7");	//verde autorizadas
//		            		list.get(i).setEstatus("REVISADA. EN ESPERA DE AUTORIZACIï¿½N");
							list.get(i).setEstatus("AUTORIZADA");
							list.get(i).setColor("color:#01DF01");	
		            		continue;
						}
					}else if(list.get(i).getNivelAutorizacion() == 3){
						if((list.get(i).getUser1()!= null && !list.get(i).getUser1().equals("")) 
								&& (list.get(i).getUser2() != null && !list.get(i).getUser2().equals(""))
								&& (list.get(i).getUser3() != null && !list.get(i).getUser3().equals(""))
								){
								
		            		list.get(i).setColor("color:#04A861");	//verde autorizadas
		            		list.get(i).setEstatus("REVISADA. EN ESPERA DE AUTORIZACION");
		            		continue;
						}
					}
					/***********	chequera benef no existe en set  ***********/
		            
		           // count = consultaPropuestasDao.noExisteChequeraBenef(list.get(i).getCveControl());
					
		          //Existe alguna chequera benef que no existe en ctas_Banco, se sombrea.
            		count=list.get(i).getSinChequeraBenef();
					if(count > 0){
						list.get(i).setColor("color:#C5B8D6");	//MORADO para sombreado  
						list.get(i).setEstatus("NO EXISTE BANCO/CHEQUERA BENEFICIARIA");
						continue;
						//NS.grid.getView().getRow(i).style.backgroundColor = '#123fas'
					}
					
					/***********	chequera CIE  ***********/
		            
		           // count = consultaPropuestasDao.chequeraCIE(list.get(i).getCveControl());//CHECAR
					count=list.get(i).getSinBancoPagador();
		          //Existe alguna chequera benef que no existe en ctas_Banco, se sombrea.
					if(count > 0){
						list.get(i).setColor("color:#A901DB");	//MORADO para sombreado  
						list.get(i).setEstatus("LA PROPUESTAS CON CONVENIO DEBE SER UNA OPERACION MISMO BANCO ");
						continue;
						//NS.grid.getView().getRow(i).style.backgroundColor = '#123fas'
					}

					/***********	Pago Cruzado  ***********/
		            
		            /*count = consultaPropuestasDao.pagoCruzado(list.get(i).getCveControl());
					
					if(count > 0){
						list.get(i).setColor("color:#FF00BF");	
						list.get(i).setEstatus("PROPUESTA CON PAGO CRUZADO");
			            count = consultaPropuestasDao.confPagoCruzado(list.get(i).getCveControl());
						if(count <= 0){
							list.get(i).setColor("color:#FA58D0");	
							list.get(i).setEstatus("PROPUESTA SIN PAGO CRUZADO AUTORIZADO");
							continue;
						} else {
							if (consultaPropuestasDao.verificaCompraTransfer(list.get(i).getCveControl()) > 0) {
								list.get(i).setColor("color:#61380B"); 
								list.get(i).setEstatus("VIGENTE CON COMPRA DE TRANSFER");
								continue;
							}
						}
					}*/
					/***********	Pago Cruzado Conf ***********/
		            
					
		          //Existe alguna chequera benef que no existe en ctas_Banco, se sombrea.
					
					
					/***   BLOQUEADOS	***/
		           /* List<BloqueoPagoCruzadoDto> lstBloqueo = new ArrayList<>();
		            lstBloqueo = consultaPropuestasDao.consultaProveedoresBloqueados(""+list.get(i).getIdGrupoFlujo() ,"'"+list.get(i).getCveControl()+"'");
				    
		            if(lstBloqueo.size()>0) {
		            	//Color Azul
		            	list.get(i).setEstatus("BLOQUEO PROVEEDOR");
		            	for(int j=0;j<lstBloqueo.size();j++ ){
		            		if(lstBloqueo.get(j).getUsuario().equals("SAP")){
		            			list.get(i).setEstatus("BLOQUEO SAP");
		            			break;
		            		}
		            	}
		            	list.get(i).setColor("color:#090DFA");	//azul bloqueado
		            	continue;
		            }*/
		            
		            /***  RFC	***/
		           // count = consultaPropuestasDao.consultaRFC(list.get(i).getCveControl());//CHECAR
		            count=list.get(i).getSinRfc();
		            if(count > 0) {
		            	list.get(i).setEstatus("PROVEEDOR CON RFC INVï¿½LIDO");
		            	list.get(i).setColor("color:#090DF9");	//azul bloqueado
		            	continue;
		            }
		            /**************************/
		            
		            /***   INTERBANCARIO	***/
		            count = consultaPropuestasDao.consultaPropInterbancario(list.get(i).getCveControl());//CHECAR
		            
		            if(count > 0) {
		            	list.get(i).setEstatus("MOVIMIENTO INTERBANCARIO SIN CTA. CLABE");
		            	list.get(i).setColor("color:#090DF9");	//azul bloqueado
		            	continue;
		            }
		            
		            /***   MODIFICADOS POR MULTISOCIEDAD	***/
			           
		            count = consultaPropuestasDao.modificadoMultisociedad(list.get(i).getCveControl());//CHECAR
				    
		            if(count>0) {
		            	list.get(i).setEstatus("MODIFICADO POR MULTISOCIEDAD");
		            	list.get(i).setColor("color:#FDA920");	//Naranja
		            	continue;
		            }
		            /**************************/
		            
		            
					list.get(i).setEstatus("VIGENTE CON DOCTOS PROPUESTOS");
					list.get(i).setColor("color:#000000");	//negra vigente
					
					if (list.get(i).getMotivoRechazo() != null && !list.get(i).getMotivoRechazo().equals("")) {
						list.get(i).setEstatus("PROPUESTA NO AUTORIZADA. REVISAR EL MOTIVO DE RECHAZO");
						list.get(i).setColor("color:#FA5882");	//rosa rechazada						
					}
						
	            	if(funciones.ponerFechaDate(list.get(i).getFechaPropuesta()).compareTo(consultaPropuestasDao.obtenerFechaHoy()) < 0) { 
	            		list.get(i).setColor("color:#E81624");	//rojo vencidas
	            		list.get(i).setEstatus("VENCIDA");
	            	}
				}
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:consultarSeleccionAutomatica");
		}
		return list;
	}
	
	public List<SeleccionAutomaticaGrupoDto> consultarSeleccionAutomaticaStored(ConsultaPropuestasDto dtoIn){
		List<SeleccionAutomaticaGrupoDto>list= new ArrayList<SeleccionAutomaticaGrupoDto>();
		//int count=0;
		//String rech="";
		
		try {
			list=consultaPropuestasDao.consultarSeleccionAutomaticaStored(dtoIn);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:consultarSeleccionAutomatica");
		}
		return list;
	}
	
	//Modicicado EMS:/04/11/2015
	public List<MovimientoDto>consultarDetalle(SeleccionAutomaticaGrupoDto dtoIn){
		PagosPropuestosDto consDetalleDto= new PagosPropuestosDto();
		List<MovimientoDto> listDetalle= new ArrayList<MovimientoDto>();
		
		try{ 
			//EMS 16/02/2016 - Modificacion para cargar detalle por medio de stored
			
			consDetalleDto.setIdGrupoEmpresa(dtoIn.getIdGrupoFlujo());
			consDetalleDto.setCveControl(dtoIn.getCveControl());
			consDetalleDto.setFecha(consultaPropuestasDao.consultarFechaHoy());
			listDetalle=consultaPropuestasDao.consultarDetalleStored(consDetalleDto);
			
			
			/*if(dtoIn.getCveControl().trim().substring(0,1).equals("M")){
				consDetalleDto.setIdGrupoEmpresa(dtoIn.getIdGrupoFlujo());
				//consDetalleDto.setIdGrupo(dtoIn.getIdGrupo());//Checar
				consDetalleDto.setCveControl(dtoIn.getCveControl());
				consDetalleDto.setFecha(consultaPropuestasDao.consultarFechaHoy());
				//consDetalleDto.setPsDivision("");
				listDetalle=consultaPropuestasDao.consultarDetalle(consDetalleDto);
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
					listBloqueados = consultaPropuestasDao.obtenerBloqueoProveedor(
													""+listDetalle.get(i).getNoEmpresa(),
													""+listDetalle.get(i).getEquivale(),
													dtoIn.getCveControl(),
													listDetalle.get(i).getNoDocto());
					
					if(listBloqueados.size()>0){
						
						//Hace corrimiento, si hay algï¿½n registro sin documento, es bloqueo proveedor, se PINTA el registro.
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
						listBloqueados = consultaPropuestasDao.obtenerBloqueoSAP(
								""+dtoIn.getCveControl(),
								""+listDetalle.get(i).getNoDocto());
						
						if(listBloqueados.size()>0){
							listDetalle.get(i).setColor("color:#090DFA"); //Azul
						}
						
					}
					
					//Valida que existan las chequeras benef
					count = consultaPropuestasDao.existeChequeraBenef(listDetalle.get(i));
					//Si no existe se pinta de color
					if(count == 0){
						listDetalle.get(i).setColor("color:#7030A0"); //morado
					}
					
				}
			}
			*/
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:consultarDetalle");
		}
		return listDetalle;
	}
	
	
	public Map<String, Object>autorizarProp(List<SeleccionAutomaticaGrupoDto> listaDto, String detalle, String autoDes, int autCheq) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {
			System.out.println("entro a Biss autorizar");
			autoDesAuto = autoDes;
			if(autoDesAuto.equals("AUT"))
				
				result = autorizar(listaDto, detalle, autCheq);
			
			else
				result = quitarAutorizacion(listaDto, detalle);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasDao, M:autorizarProp");
		}
		return result;
	}
	
	public Map<String, Object>autorizar(List<SeleccionAutomaticaGrupoDto> listaDto, String detalle, int autCheq){
		Map<String, Object> result= new HashMap<String, Object>();
		List<SeleccionAutomaticaGrupoDto> listPropGral= new ArrayList<SeleccionAutomaticaGrupoDto>();
		boolean bAutorizada=false;
		boolean bUsuarioAutenticado=false;
		String psCampo="";
		int actAutori=0;
		//int numReg = 0;
		int noAutorizadas = 0;
	//	System.out.print("listaDto "+listaDto.size());
		System.out.print("detalle "+detalle);
		//Gson gson1 = new Gson();
		//List<Map<String, String>> objDetalle = null;
		
		result.put("bit","0");
		
		//if(!detalle.equals("")){
		//	objDetalle = gson1.fromJson(detalle, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		//}
		/*else{
			result.put("msgUsuario","No hay detalles");
			return result;
		}*/
		
		//List<Map<String, String>> objDetalle = gson1.fromJson(detalle, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
		try {
			
			if(listaDto.size() > 0){
				bUsuarioAutenticado = consultaPropuestasDao.validarUsuarioAutenticado(listaDto.get(0).getIdUsuario(), funciones.encriptador(listaDto.get(0).getPsw()));
			}else{
			//	System.out.println("prpuesta no valida");
//				result.put("msgUsuario","Propuestas no válidas: " + listaDto.get(0).getEstatus());
				result.put("msgUsuario","Propuestas no válidas");

				return result;
			}
			
			if(!bUsuarioAutenticado) {
				result.put("msgUsuario","Usuario No Válido");
				return result;
			}
			
			//inicia foreach, para multiples autorizaciones
			for(SeleccionAutomaticaGrupoDto dtoGMaster : listaDto){
			//	System.out.println("entro a foreach");
				listPropGral = consultaPropuestasDao.seleccionarPropGral(dtoGMaster.getIdGrupoFlujo(), dtoGMaster.getIdGrupo(),dtoGMaster.getCveControl());			
				
				if(listPropGral.size()>0)
				{
					if(listPropGral.get(0).getCveControl().trim().substring(0,1).equals("M"))
					{
						if(dtoGMaster.getCupoTotal()!=listPropGral.get(0).getCupoTotal())
						{
							if(listaDto.size()>1){
								 noAutorizadas++;
								 continue;
							 }else{
								 result.put("msgUsuario","Los documentos propuestos han cambiado por favor refresque la lista de propuestas y sus documentos");
								 return result;
							 }
							
						}
					}else{
						if(dtoGMaster.getCupoTotal()!=listPropGral.get(0).getCupoTotal()){
							if(listaDto.size()>1){
								 noAutorizadas++;
								 continue;
							 }else{
								 result.put("msgUsuario","Los documentos propuestos han cambiado por favor refresque la lista de propuestas y sus documentos");
								 return result;
							 }
							
						}
					}
				}
				if(dtoGMaster.getNivelAutorizacion() <= 0){// checar esta validación
					if(listaDto.size()>1){
						 noAutorizadas++;
						 continue;
					 }else{
						 result.put("msgUsuario","Esta propuesta no requiere Autorización");
						 return result;
					 }
					
				}
				bAutorizada = false;
				if(dtoGMaster.getNivelAutorizacion()==1) {
					 if(dtoGMaster.getUsuarioUno()>0)
	                        bAutorizada=true;
				 }else if(dtoGMaster.getNivelAutorizacion()==2) {
					 if((dtoGMaster.getUsuarioUno()>0) && (dtoGMaster.getUsuarioDos()>0))
	                        bAutorizada=true;
				 }else if(dtoGMaster.getNivelAutorizacion()==3) {
					 if((dtoGMaster.getUsuarioUno()>0) && (dtoGMaster.getUsuarioDos()>0) && (dtoGMaster.getUsuarioTres()>0))
	                        bAutorizada=true;
				 }
				 if(bAutorizada) {
					 if(listaDto.size()>1){
						 noAutorizadas++;
						 continue;
					 }else{
						 result.put("msgUsuario","La propuesta ya alcanzó el límite de autorizaciones");
						 return result;
					 }
					 
				 }
				 
				 if(dtoGMaster.getUsuarioUno() > 0 && dtoGMaster.getUsuarioUno() != dtoGMaster.getIdUsuario()) {
					 System.out.println("el usario uno es diferente del de la sesion "+dtoGMaster.getIdUsuario());
					 if(!buscarAutorizacion(dtoGMaster, false, true)) {
						 if(listaDto.size()>1){
							 noAutorizadas++;
							 continue;
						 }else{
							 result.put("msgUsuario","La factura requiere la autorizacion del nivel uno para continuar!!");
							 return result;
						 }
						 
					 }
				 }
				 
				 if(dtoGMaster.getNivelAutorizacion() == 1)
					 psCampo = "usuario_uno";
				 else if(dtoGMaster.getNivelAutorizacion() == 2) {
					 if(dtoGMaster.getUsuarioUno() == 0)
						 psCampo = "usuario_uno";
					 else if (dtoGMaster.getUsuarioDos() == 0)
						 psCampo = "usuario_dos";
				 }else if (dtoGMaster.getNivelAutorizacion() == 3) {
					 if(dtoGMaster.getUsuarioUno() == 0)
						 psCampo = "usuario_uno";
					 else if (dtoGMaster.getUsuarioDos() == 0)
						 psCampo = "usuario_dos";
					 else if (dtoGMaster.getUsuarioTres() == 0)
						 psCampo = "usuario_tres";
				 }
				 
				
				 /** MODIFICADO 30/03/2016 - SE QUITAN LAS MODIFICACIONES HACIA LOS DETALLES
				 if((dtoGMaster.getUsuarioUno()>0 && dtoGMaster.getUsuarioUno()==dtoGMaster.getIdUsuario())
					||(dtoGMaster.getUsuarioDos()>0 && dtoGMaster.getUsuarioDos()==dtoGMaster.getIdUsuario())
					||(dtoGMaster.getUsuarioTres()>0 && dtoGMaster.getUsuarioTres()==dtoGMaster.getIdUsuario()))
				 {
					 if(buscarDetalleAuto(detalle, dtoGMaster, true, false)) {
						 if(listaDto.size()>1){
							 noAutorizadas++;
							 continue;
						 }else{
							 result.put("msgUsuario","Usted ya ha autorizado esta propuesta, agregue nuevas facturas para volver autorizarla");
							 return result;
						 }
					 }
					 autorizaDetalle(detalle, dtoGMaster, psCampo);
					 
					 
					 if(listaDto.size()>1){
						result.put("msgUsuario","Se han autorizado " + (listaDto.size() - noAutorizadas) + " de un total de " + listaDto.size() + " propuestas");
					 }else{
						 result.put("msgUsuario","Su autorizaciï¿½n ha sido almacenda con exito");
					 }
					 return result;
				 }
				 
				 if(!detalle.equals("")){
					 actAutori = autorizaDetalle(detalle, dtoGMaster, psCampo);
				 }
				 
				 
				 if(dtoGMaster.getUsuarioUno() != 0 && dtoGMaster.getUsuarioUno() != dtoGMaster.getIdUsuario())
					 numReg = consultaPropuestasDao.buscarRegPend(dtoGMaster.getCveControl());
				 
				 if(numReg == 0)
				 **/
				 if((dtoGMaster.getUsuarioUno()>0 && dtoGMaster.getUsuarioUno()==dtoGMaster.getIdUsuario())
							||(dtoGMaster.getUsuarioDos()>0 && dtoGMaster.getUsuarioDos()==dtoGMaster.getIdUsuario())
							||(dtoGMaster.getUsuarioTres()>0 && dtoGMaster.getUsuarioTres()==dtoGMaster.getIdUsuario()))
						 {
					 result.put("msgUsuario","Usted ya ha autorizado esta propuesta, agregue nuevas facturas para volver a autorizar");
					 return result;
						 }else{
							 if(consultaPropuestasDao.verificaAutorizacion3801(dtoGMaster.getCveControl())) {	
							 	  actAutori = consultaPropuestasDao.actualizarAutorizacionProp3801(psCampo, ""+dtoGMaster.getIdUsuario(),			
							 			  dtoGMaster.getIdGrupoFlujo(),dtoGMaster.getCveControl());                                                			
						 } else {		                                                                                
							 actAutori = consultaPropuestasDao.actualizarAutorizacionPop(psCampo, ""+dtoGMaster.getIdUsuario(),
									 dtoGMaster.getIdGrupoFlujo(),dtoGMaster.getCveControl());
									 actAutori = consultaPropuestasDao.actualizarAutorizacionPop(psCampo, ""+dtoGMaster.getIdUsuario(),
								dtoGMaster.getIdGrupoFlujo(),dtoGMaster.getCveControl());
						}			
					actAutori = consultaPropuestasDao.actualizarAutorizacionPop(psCampo, ""+dtoGMaster.getIdUsuario(),
								 										dtoGMaster.getIdGrupoFlujo(),dtoGMaster.getCveControl());
						 
							 
						/**else{
							 if(listaDto.size()>1){
								 noAutorizadas++;
								 continue;
							 }else{
								  result.put("msgUsuario", "La propuesta tiene una factura no autorizada, favor de eliminarla!!");
								  return result;
							 }
						 }**/
							
						 
							if(actAutori>0){
								System.out.println("actAut mayora a 0");
								if(listaDto.size()>1){
									//System.out.println("entro a se han autorizado"+listaDto.size());
									result.put("msgUsuario","Se han autorizado " + (listaDto.size() - noAutorizadas) + " propuestas");
								 }else{
									 result.put("msgUsuario","Su autorización ha sido almacenda con exito");
								 }
								
								result.put("bit","1");
							}else{
								result.put("msgUsuario","No se pudo autorizar la propuesta");
							 } 
							
						 }
			
			}//fin foreach
			
			if(listaDto.size()>1){
				result.put("msgUsuario","Se han autorizado " + (listaDto.size() - noAutorizadas) + " propuestas");
			 }
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:autorizar");
		}
		return result;
	}
	
	public int autorizaDetalle(String detalle, SeleccionAutomaticaGrupoDto dtoGMaster, String psCampo) {
		Gson gson = new Gson();
		int res = 0;
		
		List<Map<String, String>> objDetalle = null; 
		if(!detalle.equals("")){
			objDetalle = gson.fromJson(detalle, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		}
		
		try {
			
			for(int i=0; i<objDetalle.size(); i++) {
				if(dtoGMaster.getUsuarioUno() == dtoGMaster.getIdUsuario() || dtoGMaster.getUsuarioUno() == 0)
					psCampo = "usuario_uno";
				else
					psCampo = "usuario_dos";
				
				if(!consultaPropuestasDao.existeReg(objDetalle.get(i).get("noFolioDet").toString(), dtoGMaster.getIdUsuario(), false, false, false) && autoDesAuto.equals("AUT"))
					res = consultaPropuestasDao.autorizarDetalle(Integer.parseInt(objDetalle.get(i).get("noFolioDet")), dtoGMaster.getIdUsuario());
				else //if(dtoGMaster.getUsuarioUno() != 0)
					res = consultaPropuestasDao.modificarDetalle(Integer.parseInt(objDetalle.get(i).get("noFolioDet")), psCampo,
																autoDesAuto.equals("AUT") ? dtoGMaster.getIdUsuario() : 0);
				
				//updateSaldoPropuesta(dtoGMaster.getCveControl()); nO ACTUALIZA SALDO
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:autorizaDetalle");
			return 0;
		}
		return res;
	}
	
	/*public int updateSaldoPropuesta(String cveControl) {
		int res = 1;
		double importe;
		List<MovimientoDto> list = new ArrayList<MovimientoDto>();
		
		try {
			list = consultaPropuestasDao.selectMonto("usuario_dos", cveControl);
			
			if(list.get(0).getImporte() == 0) {
				list = consultaPropuestasDao.selectMonto("usuario_uno", cveControl);
				
				if(list.get(0).getImporte() == 0)
					list = consultaPropuestasDao.selectMontoOriginal(cveControl);
			}
			importe = list.get(0).getImporte();
			
			//res = consultaPropuestasDao.updateMontoPropuesta(importe, cveControl); nO ACTUALIZA SALDO
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:updateSaldoPropuesta");
			return 0;
		}
		return res;
	}*/
	
	public boolean buscarDetalleAuto(String detalle, SeleccionAutomaticaGrupoDto dtoGMaster, boolean ban, boolean ban2) {
		Gson gson = new Gson();
		boolean existe = true;
		
		if("".equals(detalle)){
			return existe;
		}
		
		try {
			List<Map<String, String>> objDetalle = gson.fromJson(detalle, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			for(int i=0; i<objDetalle.size(); i++) {
				if(!consultaPropuestasDao.existeReg(objDetalle.get(i).get("noFolioDet").toString(), dtoGMaster.getIdUsuario(), ban, ban2, false)) return false;
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:buscarDetalleAuto");
		}
		return existe;
	}
	
	public Map<String, Object>quitarAutorizacion(List<SeleccionAutomaticaGrupoDto> listaDto, String detalle){
		Map<String, Object> result= new HashMap<String, Object>();
		//List<SeleccionAutomaticaGrupoDto> listPropGral= new ArrayList<SeleccionAutomaticaGrupoDto>();
		//boolean bAutorizada=false;
		boolean bUsuarioAutenticado=false;
		String psCampo="";
		int actAutori=0;
		boolean pagada = false;
		result.put("bit", "0");
		//int noDesautorizada = 0;
		System.out.println("quitar autorización detalle: "+detalle);
		try {
			//Se agrego forEach para autorizar multiples registros
			for(SeleccionAutomaticaGrupoDto dtoGMaster: listaDto){
				
//				pagada = consultaPropuestasDao.verificaEstatusProp(dtoGMaster.getCveControl());
//				
//				if(pagada) {
//					System.out.println("tamaño de la lista "+listaDto.size());
//					result.put("msgUsuario", "La propuesta esta pagada, ¿Desea quitar la autorización?");
//					return result;
////					if(listaDto.size()>1){
////						//noDesautorizada++;
////						continue;
////					}else{
////						result.put("msgUsuario", "La propuesta esta pagada, no puede quitar la autorización!!");
////						return result;
////					}
//				}
				if(dtoGMaster.getNivelAutorizacion() <= 0){
					if(listaDto.size()>1){
						//noDesautorizada++;
						continue;
					}else{
						result.put("msgUsuario","Esta propuesta no tiene Autorizaciones que eliminar");
						return result;
					}
					
				}
				 /*if(!((dtoGMaster.getUsuarioUno()>0 && dtoGMaster.getUsuarioUno()==dtoGMaster.getIdUsuario())
					||(dtoGMaster.getUsuarioDos()>0 && dtoGMaster.getUsuarioDos()==dtoGMaster.getIdUsuario())
					||(dtoGMaster.getUsuarioTres()>0 && dtoGMaster.getUsuarioTres()==dtoGMaster.getIdUsuario())))
				 {
					 result.put("msgUsuario","Usted no ha autorizado esta propuesta, no puede quitar una autorizaciï¿½n que no ha proporcionado");
					 return result;
				 }else */
				
				/** EMS 30/03/2016 - SE COMENTA PARA QUITAR LAS VALIDACIONES SOBRE LOS DETALLES 
				if((dtoGMaster.getUsuarioUno()>0 && dtoGMaster.getUsuarioUno()==dtoGMaster.getIdUsuario())
						 ||(dtoGMaster.getUsuarioDos()>0 && dtoGMaster.getUsuarioDos()==dtoGMaster.getIdUsuario())) {
					 
						if(!buscarDetalleAuto(detalle, dtoGMaster, true, false)) {
						
						 if(listaDto.size()>1){
								noDesautorizada++;
								continue;
							}else{
								 result.put("msgUsuario","Usted no ha autorizado esta propuesta, no puede quitar una autorizaciï¿½n que no ha proporcionado");
								 return result;
							}
						 
						
					 	}
				 }else{
					 if(!buscarDetalleAuto(detalle, dtoGMaster, true, false)) {
						 if(listaDto.size()>1){
								noDesautorizada++;
								continue;
							}else{
								result.put("msgUsuario","Usted no ha autorizado esta propuesta, no puede quitar una autorizaciï¿½n que no ha proporcionado");
								return result;
							}
					 }
				 }
				**/
				
				 bUsuarioAutenticado = consultaPropuestasDao.validarUsuarioAutenticado(dtoGMaster.getIdUsuario(), funciones.encriptador(dtoGMaster.getPsw()));
				 
				 if(!bUsuarioAutenticado) {
					 if(listaDto.size()>1){
						//noDesautorizada++;
						continue;
					}else{
						 result.put("msgUsuario","Usuario No Valido");
						 return result;
					}
					
				 }
				 System.out.println("nivel "+dtoGMaster.getNivelAutorizacion());
				 if(dtoGMaster.getNivelAutorizacion()==1)
					 psCampo = "usuario_uno";
				 else if(dtoGMaster.getNivelAutorizacion()==2) {
					 if(dtoGMaster.getUsuarioUno() != 0 && dtoGMaster .getIdUsuario() == dtoGMaster.getUsuarioUno())
						 psCampo = "usuario_uno";
					 else if (dtoGMaster.getUsuarioDos() != 0  && dtoGMaster .getIdUsuario() == dtoGMaster.getUsuarioDos())
						 psCampo = "usuario_dos";
				 }else if (dtoGMaster.getNivelAutorizacion()==3) {
					 if(dtoGMaster.getUsuarioUno() != 0 && dtoGMaster .getIdUsuario() == dtoGMaster.getUsuarioUno())
						 psCampo = "usuario_uno";
					 else if (dtoGMaster.getUsuarioDos()!=0  && dtoGMaster .getIdUsuario() == dtoGMaster.getUsuarioDos())
						 psCampo = "usuario_dos";
					 else if (dtoGMaster.getUsuarioTres()!=0 && dtoGMaster .getIdUsuario() == dtoGMaster.getUsuarioTres())
						 psCampo = "usuario_tres";
				 }
				 
				/** COMENTADO 30/03/2016 
				 * actAutori = autorizaDetalle(detalle, dtoGMaster, psCampo);
				 */
				 System.out.println("usuario: "+psCampo);
				 if(psCampo.equals("usuario_dos") || psCampo.equals("usuario_uno")) {
					 actAutori = consultaPropuestasDao.actualizarAutorizacionPop(psCampo, "NULL", dtoGMaster.getIdGrupoFlujo(),dtoGMaster.getCveControl());
					 System.out.println("actAutori "+actAutori);
				 }else{
					 result.put("msgUsuario","Usted no cuenta con la facultad para realizar la Eliminación");
					 //result.put("bit", "1");
				 }
				
				 if(actAutori > 0){
					 result.put("msgUsuario","Las autorizaciónes se han eliminado con éxito");
					 result.put("bit", "1");
				 }
				 
				 
				 
			}//fin foreach
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:quitarAutorizacion");
		}
		return result;
	}
	
	public List<LlenaComboGralDto> llenarComboGrupoEmpresa(GrupoEmpresasDto dto){
		return consultaPropuestasDao.llenarComboGrupoEmpresa(dto);
	}
	
	public String eliminarProp(String cveControl, int noEmpresa) {
		String resp = "";
		int resul = 0;
		Gson gson = new Gson();
		List<Map<String, String>> objGrid = gson.fromJson(cveControl, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		
		try {
			
			for(int i=0;i<objGrid.size();i++){
				if(consultaPropuestasDao.selectCveCtrl(objGrid.get(i).get("cveControl"),noEmpresa) > 0){
					resp = "Solo se pueden eliminar las propuestas que no han sido pagadas";
					return resp;
				}else {
					if(consultaPropuestasDao.confirmaEliminar(objGrid.get(i).get("cveControl"),noEmpresa) > 0){
						resul = consultaPropuestasDao.deletePropuesta(objGrid.get(i).get("cveControl"));
					}
				}
				System.out.println("i "+i);
			}
			
			if(resul > 0)
				resp = "Propuestas eliminadas";
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:eliminarProp");
		}
		return resp;
	}
	
	public String modificarProp(SeleccionAutomaticaGrupoDto dtoGridProp, List<MovimientoDto> listDetProp) {
		//int iEmpresa = 0;
		String resp = "";
		//String sNoBenef = "";
		
		try {
			if(consultaPropuestasDao.consultarConfiguraSet(204).equals("SI")) {
				if(dtoGridProp.getUsuarioUno() != 0 || dtoGridProp.getUsuarioDos() != 0 || dtoGridProp.getUsuarioTres() != 0)
					return "No se puede modificar esta propuesta, hasta que se eliminen las autorizaciones";
			}
			/* Se comenta validaciï¿½n, se valida en js las empresas y proveedores, dependiento el caso de que si va a modificar pagadora o beneficiaria.
			if(dtoGridProp.getNumIntercos() > 0) {
				for(int i=0; i<listDetProp.size(); i++) {
					if(iEmpresa == 0) {
						iEmpresa = listDetProp.get(i).getNoEmpresa();
						sNoBenef = listDetProp.get(i).getNoCliente();
					}else {
						if(iEmpresa != listDetProp.get(i).getNoEmpresa() || sNoBenef.equals(listDetProp.get(i).getNoCliente())) {
							return "Para cambiar datos de varios traspasos deben de ser de la misma empresa y mismo beneficiario";
						}
					}
				}
			}
			*/
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:eliminarProp");
		}
		return resp;
	}
	
	public String validaModPropMaestro(SeleccionAutomaticaGrupoDto dtoGridProp[]) {
		String resp = "";
				
		try {
			if(consultaPropuestasDao.consultarConfiguraSet(204).equals("SI")) {
				for(int i=0; i<dtoGridProp.length;i++){
					if(dtoGridProp[i].getUsuarioUno() != 0 || dtoGridProp[i].getUsuarioDos() != 0 || dtoGridProp[i].getUsuarioTres() != 0)
						return "No se puede modificar esta propuesta, hasta que se eliminen las autorizaciones";
				}
			}else{
				return "No tiene facultades para modificar propuestas";
			}
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:eliminarProp");
		}
		
		return resp;
	}
	
	public String nombreUsuarios(String usr1, String usr2, String usr3) {
		String result = "";
		List<ParamConsultaPropuestasDto> listNom = new ArrayList<ParamConsultaPropuestasDto>();
		
		try {
			listNom = consultaPropuestasDao.nombreUsuarios(usr1, usr2, usr3);
			
			for(int i=0; i<listNom.size(); i++) {
				result += listNom.get(i).getNomUsuarios() + ",";
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasDao, M:nombreUsuarios");
		}
		return result;
	}
	
	public JRDataSource reporteDetalleCupos(SeleccionAutomaticaGrupoDto dtoIn) {
		JRMapArrayDataSource jrDataSource = null;
		List<Map<String, Object>> listReport = new ArrayList<Map<String, Object>>();
		PagosPropuestosDto consDetalleDto= new PagosPropuestosDto();
		
		try {
			if(dtoIn.getCveControl().trim().substring(0,1).equals("M")){
				consDetalleDto.setIdGrupoEmpresa(dtoIn.getIdGrupoFlujo());
				consDetalleDto.setIdGrupo(dtoIn.getIdGrupo());//Checar
				consDetalleDto.setCveControl(dtoIn.getCveControl());
				consDetalleDto.setFecha(consultaPropuestasDao.consultarFechaHoy());//Checar
				consDetalleDto.setPsDivision("");
			}
			listReport = consultaPropuestasDao.reporteDetalleCupos(consDetalleDto);
	        jrDataSource = new JRMapArrayDataSource(listReport.toArray());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Egresos, C:PagoPropuestasBusiness, M:reportePagoPropuestas");
		}
		return jrDataSource;
	}

	public String consultarConfiguraSet(int indice){
		return consultaPropuestasDao.consultarConfiguraSet(indice);
	}
	
	@SuppressWarnings({ "unused" })
	public String eliminaDetallePropuesta(List<Map<String, String>> grid){
		System.out.println(grid);
		String mensaje = "";
		String foliosCancela = "";
		String fechaHoy = grid.get(0).get("fecHoy");
		String cveControl = "";
		String cadena = "";
		String psRevividor = "";
		String psTipoCancelacion = "";
		String psOrigen = "";
		String psEstatusMov = "";
		String recibeCadena = "";
		String psResultado = "";
		double pdImporte = 0.0;	
		double importeTotal = 0.0;
		double importeResta = 0.0;
		int respuestaEntera = 0;
		int numIntercos = 0; //No se usa, pero no se borra por si se habilita intercompaï¿½ias (Peticiï¿½n de Carmelo).
		int noUsuario = 0;
		int c = 0;
		int noCheque = 0;
		int noFolio = 0;
		int formaPago = 0;
		List<ParamConsultaPropuestasDto> recibeDatos = new ArrayList<ParamConsultaPropuestasDto>();
		ParamConsultaPropuestasDto objPropuestasDto = new ParamConsultaPropuestasDto();
		Map<String, Object> mapaRevividor = new HashMap<String, Object>();
		
		String estatusPropuesta = "";
		
		List<MovimientoDto> listImp = new ArrayList<MovimientoDto>();
		try{
			importeTotal = Double.parseDouble(grid.get(0).get("importeTotal"));
			
			for (int i = 0; i < grid.size(); i++){
			
				foliosCancela = foliosCancela + grid.get(i).get("noFolioDet") + ",";
				
				/*if (grid.get(i).get("cveControl").indexOf("M") > 0)
					pdImporte = pdImporte + Double.parseDouble(grid.get(i).get("importe"));
				else
					pdImporte = pdImporte + Double.parseDouble(grid.get(i).get("importeMN"));
				*/
				
				pdImporte = pdImporte + Double.parseDouble(grid.get(i).get("importe"));
				
				if (i == 0){
					//numIntercos = !grid.get(i).get("numIntercos").equals("") ? Integer.parseInt(grid.get(i).get("numIntercos")) : 0;
					fechaHoy = grid.get(i).get("fecHoy");
					noUsuario  = GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario();
					cveControl = grid.get(i).get("cveControl");
					noCheque = !grid.get(i).get("noCheque").equals("") ? Integer.parseInt(grid.get(i).get("noCheque")) : 0;
					estatusPropuesta = grid.get(i).get("estatusPropuesta");
				}
			}
			
			
			
			if (!foliosCancela.equals("")){
				foliosCancela = foliosCancela.substring(0, foliosCancela.length() - 1);
				
				
				mensaje =  consultaPropuestasDao.eliminarDetalleStored(cveControl, importeTotal, pdImporte,
						foliosCancela, noUsuario, noCheque, estatusPropuesta);
				
				//De aqui para abajo 
				
				/*if(consultaPropuestasDao.existeReg(foliosCancela, 0, false, true, true))
					return "No se puede eliminar una factura que ya esta completamente autorizada";
				*/
				
				/*if (numIntercos > 0) {
					respuestaEntera = consultaPropuestasDao.actualizaPropuesta(foliosCancela, fechaHoy, noUsuario, cveControl, 1);
				}else{*/
				
				/* SE COMENTO DE AQUI EN ADELANTE PARA PRUEBA DEL STORE	
					respuestaEntera = consultaPropuestasDao.actualizaPropuesta(foliosCancela, fechaHoy, noUsuario, cveControl, 0);
					recibeDatos = consultaPropuestasDao.buscaRegistroBitacora(foliosCancela);
					
					if (recibeDatos.size() > 0){
						c = 0;
						Date d = new Date();
						while (c < recibeDatos.size()) {
							consultaPropuestasDao.insertaLogPropuestas(recibeDatos.get(c).getNoEmpresa(), recibeDatos.get(c).getNoDocto(),									
							recibeDatos.get(c).getInvoice(), "SACA DE PROPUESTA", noUsuario, fechaHoy, Integer.toString(d.getHours()) + ":" + Integer.toString(d.getMinutes()));
							c++;
						}
					}
			
					if (noCheque > 0) {			
						recibeDatos = consultaPropuestasDao.buscaMovimiento(foliosCancela);
						
						if (recibeDatos.size() > 0) {
							c = 0;
							while (c <= recibeDatos.size()) {
								objPropuestasDto.setNoEmpresa(recibeDatos.get(c).getNoEmpresa());
								objPropuestasDto.setNoFolioDet(recibeDatos.get(c).getNoFolioDet());
								objPropuestasDto.setIdBanco(recibeDatos.get(c).getIdBanco());
								objPropuestasDto.setIdChequera(recibeDatos.get(c).getIdChequera());
								objPropuestasDto.setNoCheque(recibeDatos.get(c).getNoCheque());
								objPropuestasDto.setImporte(recibeDatos.get(c).getImporte());
								objPropuestasDto.setIdEstatusMov(recibeDatos.get(c).getIdEstatusMov());
								objPropuestasDto.setIdDivisa(recibeDatos.get(c).getIdDivisa());
								objPropuestasDto.setUsuarioModif(recibeDatos.get(c).getUsuarioModif());
								objPropuestasDto.setCausa(13);
								objPropuestasDto.setFecHoy(fechaHoy);
								objPropuestasDto.setIdCaja(recibeDatos.get(c).getIdCaja());
								objPropuestasDto.setBeneficiario(recibeDatos.get(c).getBeneficiario());
								objPropuestasDto.setConcepto(recibeDatos.get(c).getConcepto());
								objPropuestasDto.setNoDocto(recibeDatos.get(c).getNoDocto());
								objPropuestasDto.setFecCheque("1999-01-01");
								
								consultaPropuestasDao.insertaBitacoraCheque(objPropuestasDto);
								
								consultaPropuestasDao.actualizaNoCheque(objPropuestasDto);
								
								c++;
							}
						}
					}
					//Se buscan movimientos a cancelar
					recibeDatos = consultaPropuestasDao.buscaSolicitudACancelar(foliosCancela);
					importeResta=importeTotal - pdImporte;
					if (recibeDatos.size() > 0) {
						//if (cveControl.substring(0, 1).equals("M"))
						//	consultaPropuestasDao.updateMontosCupo(cveControl, importeResta);
						//else
						//	consultaPropuestasDao.updateMontosAut(true, pdImporte, cveControl,importeTotal);
						
						
						consultaPropuestasDao.updateMontosCupo(cveControl, importeResta);
						
						if(estatusPropuesta != null && estatusPropuesta.equals("RECHAZADA"))
							consultaPropuestasDao.updatePropSinRech(cveControl);
						
						
						mensaje = "Se elimino el detalle con ï¿½xito.";
					}
					*/
				}
			//}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasBusinessImpl, M: eliminaDetallePropuesta");
		}
		
		return mensaje;
	}
	
	public JRDataSource reporteConSelecAut(ParamConsultaPropuestasDto dtoIn) {
		JRMapArrayDataSource jrDataSource = null;
		List<Map<String, Object>> listReport = new ArrayList<Map<String, Object>>();
		
		try {
			listReport = consultaPropuestasDao.reporteConSelecAut(dtoIn);
	        jrDataSource = new JRMapArrayDataSource(listReport.toArray());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Egresos, C:PagoPropuestasBusiness, M:reportePagoPropuestas");
		}
		return jrDataSource;
	}

	public int validaFacultad(int idFacultad) {
		int resp = 0;
		
		try {
			//Aquï¿½ valida la facultad que viene desde el extJs y es la de autorizacion de transfers 
			resp = consultaPropuestasDao.validaFacultad(idFacultad);
			
			if(resp == 0) {
				//Aquï¿½ valida la facultad de autorizaciï¿½n de cheques
				resp = consultaPropuestasDao.validaFacultad(154);
				
				if(resp != 0) resp = 1000;
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PagoPropuestasBusiness, M:validaFacultad");
		}
		return resp;
	}

	public List<LlenaComboDivisaDto>llenarComboDivisa() {
		return consultaPropuestasDao.llenarComboDivisa();
	}
	
	public double obtenerTipoCambio(List<Map<String, String>> objDatos, String sDivisa, Date fecHoy) {
		double val = 0;
		String divisaGrid = "";
		
		try {
			for(int i=0; i<objDatos.size(); i++) {
				if(divisaGrid.equals("")) divisaGrid = objDatos.get(i).get("idDivisa");
				else {
					if(!divisaGrid.equals(objDatos.get(i).get("idDivisa"))) return 100;
				}
			}
			if(!sDivisa.equals("")) {
				if(!sDivisa.equals("MN"))
					val = consultaPropuestasDao.obtenerTipoCambio(sDivisa, fecHoy);
				else if(!divisaGrid.equals("MN"))
					val = consultaPropuestasDao.obtenerTipoCambio(divisaGrid, fecHoy);
				else
					return 101;
			}
			if(val == 0 && sDivisa.equals("MN")) val = 1;
			
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:obtenerTipoCambio");	
		}
		return val;
	}

	public List<LlenaComboGralDto>llenarComboBancoPag(String sDivisa, String noEmpresa) {
		return consultaPropuestasDao.llenarComboBancoPag(sDivisa, noEmpresa);
	}
	
	//Agregado EMS:04/11/2015
	public List<LlenaComboGralDto>llenarComboBancoPagBenef(String sDivisa, int noPersona, String descFormaPago) {
		
		List<LlenaComboGralDto> list = new ArrayList<>();
		
		try{
			list = consultaPropuestasDao.llenarComboBancoPagBenef(sDivisa, noPersona, descFormaPago);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:llenarComboBancoPagBenef");	
		}
		return list;
	}
	
	public List<LlenaComboGralDto>llenarComboChequeraPag(int idBanco, int noEmpresa, String sDivisa) {
		return consultaPropuestasDao.llenarComboChequeraPag(idBanco, noEmpresa, sDivisa);
	}
	//agregado EMS: 04/11/2015
	public List<LlenaComboGralDto>llenarComboChequeraPagBenef(int idBanco, int noEmpresa, String sDivisa, int idBenef, String descFormaPago) {
		return consultaPropuestasDao.llenarComboChequeraPagBenef(idBanco, noEmpresa, sDivisa, idBenef, descFormaPago);
	}
	
	public String aceptarModificar(List<Map<String, String>>mapGridProp, List<Map<String, String>>mapGridDetProp, int formaPago,
			 int idBanco, String idChequera, int idBancoBenef, String idChequeraBenef, boolean actualizaBeneficiario, String fecHoy, 
			 String idDivisa, double tipoCambio, String folios, String campoRef, String referencia1, String referencia2,String referencia3
			 ) {
		String resp = "";
		String sFolios = "";
		String tFolios = "";
		String bTraspaso = "";
		//String sCambioBitacora = "Nueva Fecha: ";
		//List<ComunEgresosDto> bancoCheq = new ArrayList<ComunEgresosDto>();
		//List<ComunEgresosDto> bancoCheqBenef = new ArrayList<ComunEgresosDto>();
		//boolean fPProv = false;
		///int afec = 0;
		//Map resultado = new HashMap();
		
		try {
			//System.out.println("entro a modificar business");
			if(consultaPropuestasDao.consultarConfiguraSet(204).equals("SI")) {
				if (consultaPropuestasDao.verificaFacultadUsuario("modificarPropAutorizadas") == "" )
					if((Integer.parseInt(mapGridProp.get(0).get("usr1")) != 0 ||
							Integer.parseInt(mapGridProp.get(0).get("usr2")) != 0 ||
							Integer.parseInt(mapGridProp.get(0).get("usr3")) != 0))
						return "No se puede modificar esta propuesta, no cuenta con la facultad correspondiente";
			}
			
			for(int i=0; i<mapGridDetProp.size(); i++) {
				/*if(formaPago != 0) {
					bancoCheq = consultaPropuestasDao.buscaBancoChequera(Integer.parseInt(mapGridDetProp.get(i).get("noEmpresa")), formaPago);
					if(bancoCheq.size() <= 0) return "No hay chequera asignada a la empresa " + mapGridDetProp.get(i).get("noEmpresa") + " con la forma de pago seleccionada";
					
					//fPProv = consultaPropuestasDao.buscaFPProv(Integer.parseInt(mapGridDetProp.get(i).get("noCliente")), formaPago);
					//if(!fPProv) return "El Proveedor No. " + mapGridDetProp.get(i).get("noCliente") + " no tiene autorizada la forma de pago seleccionada";
					
					switch(formaPago) {
					case 1:
					case 5:
						afec = consultaPropuestasDao.actualizarBCBenef(Integer.parseInt(mapGridDetProp.get(i).get("noEmpresa")),
																	   Integer.parseInt(mapGridDetProp.get(i).get("noFolioDet")), 0, "", formaPago);
						break;
					case 3:
						bancoCheqBenef = consultaPropuestasDao.buscaBancoChequeraBenef(Integer.parseInt(mapGridDetProp.get(i).get("noCliente")), mapGridDetProp.get(i).get("idDivisa"));
						
						if(bancoCheqBenef.size() != 0)
							afec = consultaPropuestasDao.actualizarBCBenef(Integer.parseInt(mapGridDetProp.get(i).get("noEmpresa")),
																			Integer.parseInt(mapGridDetProp.get(i).get("noFolioDet")),
																			bancoCheqBenef.get(0).getIdBanco(), bancoCheqBenef.get(0).getIdChequera(), formaPago);
						else
							return "No existen chequeras en la divisa para el proveedor No. " + mapGridDetProp.get(i).get("noCliente");
						break;
					//case 9:
						//afec = consultaPropuestasDao.actualizarConcepto(Integer.parseInt(mapGridDetProp.get(i).get("noEmpresa")),
						//		   										Integer.parseInt(mapGridDetProp.get(i).get("noFolioDet")), "");
						//break;
						//
					}
					if(afec == 0) return "No se actualizaron los datos";
				}*/
				tFolios += mapGridDetProp.get(i).get("noFolioDet") + ",";
			}
			if(campoRef.equals("") && !referencia1.equals("") && !referencia2.equals("") && !referencia3.equals("")){
				String noPersona = mapGridDetProp.get(0).get("noCliente");
				consultaPropuestasDao.actualizaReferenciaCte(noPersona);
			}
		
			tFolios = tFolios.substring(0, tFolios.length() - 1);
			bTraspaso = mapGridDetProp.get(0).get("formaPago");
			sFolios=tFolios;
		//	System.out.println("forma pago "+bTraspaso);
			if(idBanco != 0 || actualizaBeneficiario || formaPago != 0 || !referencia1.equals("") || !referencia2.equals("") || !referencia3.equals("")) consultaPropuestasDao.actualizarBanCheqPropuesta(sFolios, formaPago, idBanco, idChequera, idBancoBenef, idChequeraBenef, actualizaBeneficiario, idDivisa, tipoCambio,
					bTraspaso, campoRef, referencia1, referencia2, referencia3);

		} catch(Exception e){
			e.printStackTrace();
			logger.error(e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:aceptarModificar");	
		}
		return resp; 
	}
	
	public void BitacoraCambiosDocumentos(String sFolios, String sCambioBitacora, String fecHoy) {
		List<ParamConsultaPropuestasDto> datosProp = new ArrayList<ParamConsultaPropuestasDto>();
		globalSingleton = GlobalSingleton.getInstancia();
		Date d = new Date();
		
		datosProp = consultaPropuestasDao.buscaRegistroBitacora(sFolios);
		
		for(int i=0; i<datosProp.size(); i++) {
			consultaPropuestasDao.insertaLogPropuestas(datosProp.get(i).getNoEmpresa(), datosProp.get(i).getNoDocto(), datosProp.get(i).getInvoice(),
													sCambioBitacora, globalSingleton.getUsuarioLoginDto().getIdUsuario(),
													fecHoy, Integer.toString(d.getHours()) + ":" + Integer.toString(d.getMinutes()));
		}
	}
	
	public List<MovimientoDto> consultarDetPagos(int noCliente, String fecIni, String fecFin) {
		List<MovimientoDto> result = new ArrayList<MovimientoDto>();
		
		try {
			result = consultaPropuestasDao.consultarDetPagos(noCliente, fecIni, fecFin);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:consultarDetPagos");
		}
		return result;
	}
	
	@Override
	public List<GrupoDTO> llenarComboGrupo(String idTipoGrupo, int noEmpresa) {
		
		return null;
	}
	@Override
	public List<RubroDTO> llenarComboRubros(int idGrupo) {
		
		return null;
	}
	@Override
	public Map<String, Object> insertarFlujoMovtos(List<Map<String, String>> dtoMovto) {
		
		return null;
	}

	public ConsultaPropuestasDao getConsultaPropuestasDao() {
		return consultaPropuestasDao;
	}
	public void setConsultaPropuestasDao(
			ConsultaPropuestasDao consultaPropuestasDao) {
		this.consultaPropuestasDao = consultaPropuestasDao;
		consultasGeneralesEgresosDao = new ConsultasGeneralesEgresosDao(
				((ConsultaPropuestasDaoImpl)consultaPropuestasDao).getJdbcTemplate());
	}
	
	
	
	/**
	 * Agregado EMS 170915 
	 * Metodo que exporta a excel un grid del JS, sin hacer consulta sql
	 */
	/*public String exportaExcel(String datos) {
		String respuesta = "";
	    Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
	    try {	    	
			//Se crea el libro Excel
			HSSFWorkbook wb = Utilerias.generarExcel(new String[]{
													"Fecha Elaboraciï¿½n",
										            "Fecha Pago",
										            "Sociedad",
										            "Nombre Sociedad",
										            "Total",
										            "Modena",
										            "Concepto",
										            "Vï¿½a Pago",
										            "Id Propuesta",
										            "Origen Propuesta",
										            "Usuario 1",
										            "Usuario 2"
												}, 
												parameters, 
												new String[]{
														"fechaElaboracion", //usuario_uno, usuario_dos -> obtiene el numero de usuario
														"fechaPago",
														"sociedad",
														"nombreSociedad",
														"total",														
														"moneda", //Fecha captura - revisar
														"concepto", 
														"viaPago",
														"idPropuesta",
														"origenPropuesta",
														"usuario1",
														"usuario2"
												});			
            
            respuesta = ConstantesSet.RUTA_EXCEL + "propuestas " + Utilerias.indicadorFecha() +".xls";
            File outputFile = new File(respuesta);
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
        } catch (IOException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:exportaExcel");
        	respuesta = "";
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:exportaExcel");
            respuesta = "";
        }
        return respuesta;
	}*/
	public HSSFWorkbook obtenerExcel(String datos) {
		

		HSSFWorkbook wb = new HSSFWorkbook();
		
		/*String[] headers = new String[]{
			"Fecha Elaboraciï¿½n",
            "Fecha Pago",
            "Sociedad",
            "Nombre Sociedad",
            "Total",
            "Modena",
            "Concepto",
            "Vï¿½a Pago",
            "Id Propuesta",
            "Origen Propuesta",
            "Usuario 1",
            "Usuario 2"
		};
		
		String[] keys = new String[]{
			"fechaElaboracion", //usuario_uno, usuario_dos -> obtiene el numero de usuario
			"fechaPago",
			"sociedad",
			"nombreSociedad",
			"total",														
			"moneda", //Fecha captura - revisar
			"concepto", 
			"viaPago",
			"idPropuesta",
			"origenPropuesta",
			"usuario1",
			"usuario2"
		};*/
		
		String[] headersD = new String[]{
			"Nombre Regla",
			"No Empresa",
			"Nombre de Empresa",
            "No. Beneficiario",
            "Beneficiario",
            "No. Factura",
            "No. Documento",
            //"Clase Docto.",
            "Importe",
            "Divisa",
            "Forma Pago",
            "Concepto",
            "Fecha Propuesta",
            "Fecha Vencimiento",
            "Fecha Documento",
            "Banco Pago",
            "Chequera Pago",
            "Banco Beneficiario",
            "Chequera Beneficiaria",
            "Clabe",
            "Referencia",
            "Id. Rubro",
            

		};
		
		String[] keysD = new String[]{
			"nombreRegla",
			"noEmpresa", 
			"nomEmpresa",
			"equivale",
			"beneficiario",
			"noFactura",
			"noDocto",														
			//"idContable",
			"importe", 
			"idDivisa",
			"descFormaPago",
			"concepto",
			"fecPropuesta",
			"fecValor",
			"fecOperacion",
			"bancoPago",
			"idChequera",
			"idBancoBenef",
			"idChequeraBenef",
			"clabe",
			"referenciaCte",
			"idRubro",
			
			};
		
		try {
			
			Gson gson = new Gson();
			List<Map<String, String>> data = 
					gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			
			HSSFSheet sheet = wb.createSheet();

			int rowIdx = 0;
			int cellIdx = 0;
			int cellIdx2 = 0;
			
			for (Iterator<Map<String, String>> rows = data.iterator(); rows.hasNext();) {
				//Carta----------------------------------------------------------------
				cellIdx=0;
				//HSSFRow hssfHeader = sheet.createRow(rowIdx);
				HSSFFont font = wb.createFont();
				HSSFCellStyle cellStyle = wb.createCellStyle();
				font.setColor(new HSSFColor.WHITE().getIndex());
				//font.setFontHeight(new Short("18"));
				cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				cellStyle.setFillPattern(HSSFCellStyle.FINE_DOTS );
				cellStyle.setFont(font);
				cellStyle.setFillForegroundColor(new HSSFColor.DARK_GREEN().getIndex());
				
				
				/*HSSFCell celdaTitulo = hssfHeader.createCell(cellIdx);
				celdaTitulo.setCellStyle(cellStyle);
				celdaTitulo.setCellValue(new HSSFRichTextString("Propuesta"));*/
				
				//sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, headers.length - 1));
				
				//rowIdx++;
				// Header
				//hssfHeader = sheet.createRow(rowIdx);
				font = wb.createFont();
				cellStyle = wb.createCellStyle();
				font.setColor(new HSSFColor.WHITE().getIndex());
				cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				cellStyle.setFillPattern(HSSFCellStyle.FINE_DOTS );
				cellStyle.setFont(font);
				cellStyle.setFillForegroundColor(new HSSFColor.LIGHT_BLUE().getIndex());
				
				/*for (String string : headers) {
					HSSFCell hssfCell = hssfHeader.createCell(cellIdx++);
					hssfCell.setCellStyle(cellStyle);
					hssfCell.setCellValue(new HSSFRichTextString(string));
				}*/
				
				//Data
				//rowIdx++;
				
				Map<String, String> row = rows.next();
				//HSSFRow hssfRow = sheet.createRow(rowIdx++);
				cellIdx = 0;
				
				/*for (String string : keys) {
					HSSFCell hssfCell = hssfRow.createCell(cellIdx++);
					hssfCell.setCellValue(new HSSFRichTextString(row.get(string)));
				}*/
				//---------------------------------------------------------------------
				
				//Detalle----------------------------------------------------------------
				PagosPropuestosDto consDetalleDto = new PagosPropuestosDto();
				consDetalleDto.setIdGrupoEmpresa(Integer.parseInt(row.get("idGrupoEmpresa")));
				consDetalleDto.setCveControl(row.get("idPropuesta"));
				consDetalleDto.setFecha(consultaPropuestasDao.consultarFechaHoy());
				
				List<Map<String, String>> detalle = 
						consultaPropuestasDao.consultarDetalleStoredParaExcel(consDetalleDto);
				
				cellIdx = 0;
				
				//HSSFRow hssfHeaderD;
				//HSSFRow hssfHeaderD = sheet.createRow(rowIdx);
				//HSSFFont fontD = wb.createFont();
				//HSSFCellStyle cellStyleD = wb.createCellStyle();
				//fontD.setColor(new HSSFColor.WHITE().getIndex());
				//font.setFontHeight(new Short("18"));
				///cellStyleD.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				//cellStyleD.setFillPattern(HSSFCellStyle.FINE_DOTS );
				//cellStyleD.setFont(fontD);
				//cellStyleD.setFillForegroundColor(new HSSFColor.DARK_GREEN().getIndex());
				
				
				/*HSSFCell celdaTituloD = hssfHeaderD.createCell(cellIdx);
				celdaTituloD.setCellStyle(cellStyleD);
				celdaTituloD.setCellValue(new HSSFRichTextString("Detalle"));*/
				
				//sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, headersD.length - 1));
				
				//rowIdx++;
				// Header
				
				HSSFFont fontD = wb.createFont();
				HSSFCellStyle cellStyleD = wb.createCellStyle();
				fontD.setColor(new HSSFColor.WHITE().getIndex());
				cellStyleD.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				cellStyleD.setFillPattern(HSSFCellStyle.FINE_DOTS );
				cellStyleD.setFont(fontD);
				cellStyleD.setFillForegroundColor(new HSSFColor.LIGHT_BLUE().getIndex());
				
				if(cellIdx2<17){
					HSSFRow hssfHeaderD = sheet.createRow(rowIdx++);
					for (String stringD : headersD) {
						HSSFCell hssfCellD = hssfHeaderD.createCell(cellIdx2++);
						hssfCellD.setCellStyle(cellStyleD);
						hssfCellD.setCellValue(new HSSFRichTextString(stringD));
					}
				}
				
				
				//Data
				//rowIdx++;
				for (Iterator<Map<String, String>> rowsD = detalle.iterator(); rowsD.hasNext();) {
					Map<String, String> rowD = rowsD.next();
					HSSFRow hssfRowD = sheet.createRow(rowIdx++);
					cellIdx = 0;
					
					for (String stringD : keysD) {
						HSSFCell hssfCellD2 = hssfRowD.createCell(cellIdx++);
						hssfCellD2.setCellValue(new HSSFRichTextString(rowD.get(stringD)));
					}
				}
				//rowIdx+=5;
				//-----------------------------------------------------------------------	
			}
			
			wb.setSheetName(0, "Hoja 1");
			
			for (int i = 0; i < headersD.length; i++) {
				sheet.autoSizeColumn((short)i);
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(
					new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Impresion, C: CartasPorEntregarBusinessImpl, "
					+ "M: reporteCartasEmitidas");
		} return wb;
	}

	public String exportaExcelDetalles(String datos) {
		String respuesta = "";
	    Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
	    try {	    	
			//Se crea el libro Excel
			HSSFWorkbook wb = Utilerias.generarExcel(new String[]{
													"No Empresa",
										            "No. Beneficiario",
										            "Beneficiario",
										            "No. Factura",
										            "No. Documento",
										            "Clase Docto.",
										            "Importe",
										            "Divisa",
										            "Forma Pago",
										            "Concepto",
										            "Fecha Propuesta",
										            "Fecha Vencimiento",
										            "Fecha Contbilización",
										            "Fecha Documento",
										            "Banco Pago",
										            "Chequera Pago",
										            "Banco Beneficiario",
										            "Chequera Beneficiaria"
										            
												}, 
												parameters, 
												new String[]{
														"noEmpresa", 
														"equivale",
														"beneficiario",
														"noFactura",
														"noDocto",														
														"idContable",
														"importe", 
														"idDivisa",
														"descFormaPago",
														"concepto",
														"fecPropuesta",
														"fecValor",
														"fecContabilizacion",
														"fecOperacion",
														"bancoPago",
														"idChequera",
														"idBancoBenef",
														"idChequeraBenef"
												},
												"Detallado de propuestas"
												);			
			
            respuesta = ConstantesSet.RUTA_EXCEL + "propuestas " + Utilerias.indicadorFecha() +".xls";
            File outputFile = new File(respuesta);
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
        } catch (IOException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:exportaExcel");
        	respuesta = "";
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:exportaExcel");
            respuesta = "";
        }
        return respuesta;
	}
	
	public Map<String, Object>autorizarModificaciones(SeleccionAutomaticaGrupoDto dtoGMaster) {
		Map<String, Object> result = new HashMap<String, Object>();
		boolean bUsuarioAutenticado=false;
		
		try {
			bUsuarioAutenticado = consultaPropuestasDao.validarUsuarioAutenticado(dtoGMaster.getIdUsuario(), funciones.encriptador(dtoGMaster.getPsw()));
			
			if(!bUsuarioAutenticado) {
				result.put("msgUsuario","Usuario No Valido");
				return result;
			}else{
				result.put("msgUsuario","");
			}
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasDao, M:autorizarModificaciones");
		}
		return result;
	}

	public String updateFecProp(SeleccionAutomaticaGrupoDto matProp[], String fechaActualiza) {
		int afec = 0;
		String resp = "";
		String cadControl = "";
		try {
			for(int i=0; i<matProp.length;i++){
				cadControl+=" '"+matProp[i].getCveControl()+"',";
			}
			
			if(!cadControl.equals("")){
				cadControl = cadControl.substring(0, cadControl.length()-1);
				afec = consultaPropuestasDao.updateFecProp(cadControl, fechaActualiza);
				consultaPropuestasDao.updateFecDetProp(cadControl, fechaActualiza);
				
				resp = "" + afec;
			}
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:eliminarProp");
		}
		return resp;
	}


	public List<BloqueoPagoCruzadoDto> consultaPagosCruzados(String noPersona) {
		List<BloqueoPagoCruzadoDto> list = new ArrayList<BloqueoPagoCruzadoDto>();
		
		try {
			Funciones funcion = new Funciones();
			list = consultaPropuestasDao.consultaPagosCruzados(noPersona, funcion.isNumeric(noPersona));

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasBussinesImpl, M:consultaPagosCruzados");
		}
		return list;
	}


	public List<LlenaComboGralDto> llenarComboBenefPagoCruzado(String noPersona) {
		
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		
		try {
			Funciones funciones = new Funciones();
			list = consultaPropuestasDao.llenarComboBenefPagoCruzado(noPersona, funciones.isNumeric(noPersona));
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasBussinesImpl, M:llenarComboBenefPagoCruzado");
		}
		return list;
	}

	@Override
	public List<LlenaComboDivisaDto>obtenerDivisas(){
		return consultaPropuestasDao.obtenerDivisas();
	}

	//Agregado EMS: 25/09/15 - Valida los datos para insertar un nuevo registro de pago cruzado
	@Override
	public String insertaNuevoProvPagoCruzado(BloqueoPagoCruzadoDto pagoCruzado) {

		String result = "";
		int afec = 0;
		
		if(pagoCruzado.getProveedor().equals("")){
			return "Seleccione un proveedor";
		}
		
		if(pagoCruzado.getDivisaOrig().equals("")){
			return "Seleccione una divisa original";
		}
		
		if(pagoCruzado.getDivisaPago().equals("")){
			return "Seleccione una divisa de pago";
		}
		
		if(pagoCruzado.getDivisaOrig().equals(pagoCruzado.getDivisaPago())){
			return "Las divisas deben ser diferentes.";
		}
		
		//Si llego aquï¿½ es porque no hay nada vacio, valido que no exista ya ese proveedor con la misma divisa original.
		if(consultaPropuestasDao.existeProvPagoCruzado(pagoCruzado.getProveedor(), pagoCruzado.getDivisaOrig())){
			return "Error al registrar: Ya existe el proveedor con esta divisa.";
		}
		
		try {
			afec = consultaPropuestasDao.insertaNuevoProvPagoCruzado(pagoCruzado);
			if(afec == 0){
				return "Error al registrar el proveedor";
			}else{
				result = "Proveedor registrado con éxito!";
			}
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:insertaNuevoProvPagoCruzado");
		}

		return result;
	}

	@Override
	public String eliminarProvPagoCruzado(String noProv, String divOriginal) {
		
		int afec = 0;
		
		try {
			afec = consultaPropuestasDao.eliminarProvPagoCruzado(noProv, divOriginal);
			
			if(afec == 0){
				return "Error al eliminar el registro";
			}else{
				return "Registro eliminado con éxito!";
			}
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:eliminarProvPagoCruzado");
			return "";
		}
	}
	
	public Map<String, Object> buscarBloqueo(String noEmpresa, String noProv, String noDocto, String periodo){
	
		Funciones fun = new Funciones();
		List<BloqueoPagoCruzadoDto> list = new ArrayList<BloqueoPagoCruzadoDto>();
		Map<String, Object> datos = new HashMap<String, Object>();
		int existe = 0;
		
		datos.put("error", "");
		
		if(noEmpresa.equals("")){ datos.put("error", "Seleccione una empresa."); return datos;}
		if(noProv.equals("")){ datos.put("error", "Seleccione una proveedor."); return datos;}
		
		try {
			
			existe = consultaPropuestasDao.existeBloqueo(noEmpresa, noProv, noDocto);
			
			if(existe <= 0 ){
				//No existe - No esta bloqueado
				
				if(!noDocto.equals("")){
					//Busca los datos del documento en movimiento
					if(periodo.equals("")){ datos.put("error", "Seleccione un periodo."); return datos;}
					
					list = consultaPropuestasDao.buscarBloqueo(noEmpresa, noProv, noDocto, false, periodo);
					
					if(list.size() <= 0){
						datos.put("error", "No se encontró el documento");
					}else{
						datos.put("importe", list.get(0).getImporte());
						//El formato original tiene yyyy/mm/dd HH:mm:ss.n, por eso la convierto y corto la cadena
						datos.put("fechaPago", fun.cambiarFecha(list.get(0).getFechaPago()).substring(0, 10));  
						datos.put("concepto", list.get(0).getConcepto());
						datos.put("motivo", "");
						datos.put("error", "");
						datos.put("existe", "documento2");
					}
					
				}else{
					//Solo retorna vacio
					datos.put("error", "");
					datos.put("existe", "");
				}
				
			}else{
				//Si existe - Esta bloqueado
				
				if(!noDocto.equals("")){
					if(periodo.equals("")){ datos.put("error", "Seleccione un periodo."); return datos;}
					//Obtiene los datos del documento en movimiento con join a la tabla bloqueo_proveedor
					list = consultaPropuestasDao.buscarBloqueo(noEmpresa, noProv, noDocto, true, periodo);
					
					if(list.size() <= 0){
						datos.put("error", "No se encontró el documento bloqueado");
					}else{
						datos.put("importe", list.get(0).getImporte());
						//El formato original tiene yyyy/mm/dd HH:mm:ss.n, por eso la convierto y corto la cadena
						datos.put("fechaPago", fun.cambiarFecha(list.get(0).getFechaPago()).substring(0, 10));  
						datos.put("concepto", list.get(0).getConcepto());
						datos.put("motivo", list.get(0).getMotivo());
						datos.put("error", "");
						datos.put("existe", "documento");
					}
					
				}else{
					//Obtiene datos proveedor bloqueado
					list = consultaPropuestasDao.obtenerBloqueoProveedor(noEmpresa, noProv,"", "");
					if(list.size() <= 0){
						datos.put("error", "No se encontro el proveedor bloqueado");
					}else{
						datos.put("motivo", list.get(0).getMotivo());
						datos.put("existe", "proveedor");
						datos.put("error", "");
					}
				}
				
			}
			
			//list = consultaPropuestasDao.buscarBloqueo(noEmpresa, noProv, noDocto);
			
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:buscarBloqueo");
		}
		return datos;
	}

	//agregado EMS 30/09/15
	@Override
	public Map<String, Object> insertarBloqueo(String noEmpresa, String noProv, String noDocto, String motivo, int noUsuario) {
		
		List<BloqueoPagoCruzadoDto> list = new ArrayList<BloqueoPagoCruzadoDto>();
		Map<String, Object> datos = new HashMap<String, Object>();
		int afec = 0, existe = 0;
		
		datos.put("error", "");
		
		if(noEmpresa.equals("")){ datos.put("error", "Seleccione una empresa."); return datos;}
		if(noProv.equals("")){ datos.put("error", "Seleccione una proveedor."); return datos;}
		if(motivo.equals("")){ datos.put("error", "El campo motivo no debe estar vacio."); return datos; }
		if(noUsuario <= 0){ datos.put("error", "Usuario no válido."); return datos; }
		
		try {
			
			if(!noDocto.equals("")){
				
				existe = consultaPropuestasDao.existeBloqueo(noEmpresa, noProv, noDocto);
				
				if(existe <= 0 ){
					//Busca los datos del documento en movimiento
					list = consultaPropuestasDao.buscarBloqueo(noEmpresa, noProv, noDocto, false, "");
					
					if(list.size() <= 0){
						datos.put("error", "Error al guardar, no se encontró el documento.");
					}else{
						//Inserta documento
						afec = consultaPropuestasDao.insertarBloqueo(noEmpresa, noProv, noDocto, motivo, noUsuario);
						
						if(afec <= 0){
							datos.put("error", "Error al guardar el documento");
						}else{
							datos.put("resp", "Documento bloqueado con éxito.");
						}
					}
				}else{
					datos.put("error", "El documento ya se encuentra bloqueado.");
				}
				
			}else{
				//Inserta proveedor
				//Existe bloqueo proveedor
				existe = consultaPropuestasDao.existeBloqueo(noEmpresa, noProv, noDocto);
				
				if(existe <= 0 ){
					//si no existe se bloquea
					afec = consultaPropuestasDao.insertarBloqueo(noEmpresa, noProv, noDocto,motivo, noUsuario);
					
					if(afec <= 0){
						datos.put("error", "Error al guardar el proveedor.");
					}else{
						datos.put("resp", "Proveedor bloqueado con éxito.");
					}
				}else{
					datos.put("error", "El proveedor ya se encuentra bloqueado.");
				}
				
			}
				
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:insertarBloqueo");
		}
		return datos;
		
	}
	
	//Agregado EMS 30/09/15
	@Override
	public Map<String, Object> eliminarBloqueo(String noEmpresa, String noProv, String noDocto) {
		
		Map<String, Object> datos = new HashMap<String, Object>();
		int afec = 0;
		
		datos.put("error", "");
		datos.put("resp", "");
		
		if(noEmpresa.equals("")){ datos.put("error", "Seleccione una empresa."); }
		if(noProv.equals("")){ datos.put("error", "Seleccione una proveedor."); }
		
		try {		
				
				afec = consultaPropuestasDao.eliminarBloqueo(noEmpresa, noProv, noDocto);
				
				if(afec <= 0 ){
					datos.put("error", "Error al eliminar, no se encontró el registro bloqueado.");
				}else{
					datos.put("resp", "El registro se ha desbloqueado con ï¿½xito.");
				}
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:eliminarBloqueo");
		}
		
		return datos;
		
	}

	@SuppressWarnings("resource")
	@Override
	public HSSFWorkbook datosExcelBloqueados(String nombre, Map<String, String> params) {
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		String[] headers = {"Clave Control","No. Empresa","No. Proveedor","No. Documento","Fecha Pago","Importe","Concepto", "Fecha Bloqueo", "Usuario Bloqueo", "Motivo"};
		
		try{
			
			if(params.get("bloqSet").equals("true") && params.get("bloqSap").equals("true")){
				//Consulta de los dos bloqueos
				workbook = Utilerias.generarExcel("Proveedores bloqueados", headers, consultaPropuestasDao.datosExcelBloqueados(3));
			}else if(params.get("bloqSet").equals("true")){
				//consulta bloqueados set
				workbook = Utilerias.generarExcel("Proveedores bloqueados", headers, consultaPropuestasDao.datosExcelBloqueados(1));
			}else if(params.get("bloqSap").equals("true")){
				//consulta bloqueados sap
				workbook = Utilerias.generarExcel("Proveedores bloqueados", headers, consultaPropuestasDao.datosExcelBloqueados(2));
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:datosExcelBloqueados");
		}
		
		try {
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return workbook;
	}

	@Override
	public List<BloqueoPagoCruzadoDto> consultaProveedoresBloqueados(String empresas, String propuestas) {
		
		List<BloqueoPagoCruzadoDto>list= new ArrayList<BloqueoPagoCruzadoDto>();
		
		if(empresas.equals("") || propuestas.equals("")) return null;
		
		empresas = empresas.replace("[", "").replace("{", "").replace("}", "").replace("]", "");
		empresas = empresas.replace("\"noEmpresa\":", "");
		
		propuestas = propuestas.replace("[", "").replace("{", "").replace("}", "").replace("]", "");
		propuestas = propuestas.replace("\"cveControl\":", "");
		propuestas = propuestas.replace("\"", "\'");
		
		list = consultaPropuestasDao.consultaProveedoresBloqueados(empresas,propuestas);
		
		return list;
	}
	
	public List<LlenaComboGralDto> llenarComboPeriodos(LlenaComboGralDto dto) {
		
		List<LlenaComboGralDto> listRet= new ArrayList<LlenaComboGralDto>();
		
		try{
			listRet = consultaPropuestasDao.llenarComboPeriodos(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:llenaComboPeriodos");
		}
		
		return listRet;
	}
	
	public List<LlenaComboGralDto>llenarComboReglaNegocio(LlenaComboGralDto dto){
		
		List<LlenaComboGralDto> listRet= new ArrayList<LlenaComboGralDto>();
		
		try{
			listRet = consultaPropuestasDao.llenarComboReglaNegocio(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:llenaComboPeriodos");
		}
		
		return listRet;
	}

	@Override
	public Map<String, String> validaCvePropuesta(String cveControl) {
		
		Map<String, String> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		try{
			
			if(cveControl.equals("")){
				resultado.put("error", "El ID Propuesta esta vacio.");
				return resultado;
			}
			
			resultado = consultaPropuestasDao.validaCvePropuesta(cveControl);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasBusinessImpl, M:validaCvePropuesta");	
		}
		
		return resultado;
	}

	@Override
	public Map<String, String> insertaSubPropuesta(double montoMaximo, String nvaCveControl, String oldCveControl, int idUsuario, String fecha){
		
		Map<String, String> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		try{
			
			if(montoMaximo == 0){
				resultado.put("error", "El monto mï¿½ximo de los registros es 0.");
				return resultado;
			}
			
			if(nvaCveControl.equals("")){
				resultado.put("error", "La nueva ID Propuesta no puede estar vacia.");
				return resultado;
			}
			
			if(oldCveControl.equals("")){
				resultado.put("error", "El id de la propuesta original esta vacio.");
				return resultado;
			}
			
			if(idUsuario == 0){
				resultado.put("error", "Error de usuario.");
				return resultado;
			}
			
			resultado = consultaPropuestasDao.insertaSubPropuesta(montoMaximo, nvaCveControl,oldCveControl,idUsuario, fecha);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasBusinessImpl, M:validaCvePropuesta");	
		}
		
		return resultado;
	}

	@Override
	public Map<String, String> actualizaMontoPropuesta(double montoMaximo, String cveControl, String stencero) {
		Map<String, String> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		try{
			
			if(montoMaximo == 0){
				resultado.put("error", "El monto mï¿½ximo de los registros es 0.");
				return resultado;
			}
			
			if(cveControl.equals("")){
				resultado.put("error", "La nueva ID Propuesta no puede estar vacia.");
				return resultado;
			}
			
			resultado = consultaPropuestasDao.actualizaMontoPropuesta(montoMaximo, cveControl, stencero);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasBusinessImpl, M:validaCvePropuesta");	
		}
		
		return resultado;
	}

	@Override
	public Map<String, String> actualizaPropuesta(String nvaCveControl, String noDoctos, String oldCveControl, String fecha) {
		
		Map<String, String> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		try{
			
			if(nvaCveControl.equals("")){
				resultado.put("error", "La nueva ID Propuesta no puede estar vacia.");
				return resultado;
			}
			
			if(noDoctos.equals("")){
				resultado.put("error", "No. de documentos no puede estar vacio");
				return resultado;
			}
			
			if(oldCveControl.equals("")){
				resultado.put("error", "Id propuesta original no puede estar vacia.");
				return resultado;
			}
			
			if(fecha.equals("")){
				resultado.put("error", "La nueva fecha de pago no puede estar vacia.");
				return resultado;
			}
			
			resultado = consultaPropuestasDao.actualizaPropuesta(nvaCveControl, noDoctos, oldCveControl, fecha);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasBusinessImpl, M:validaCvePropuesta");	
		}
		
		return resultado;
		
	}
	
	public List<LlenaComboGralDto> llenarComboGrupoEmpresaUnica(GrupoEmpresasDto dto){
		return consultaPropuestasDao.llenarComboGrupoEmpresaUnica(dto);
	}

	@Override
	public Map<String, String> insertaBitacoraPropuesta(List<BitacoraPropuestasDto> listBitacora) {
		Map<String, String> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		try{
			resultado = consultaPropuestasDao.insertaBitacoraPropuesta(listBitacora);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasBusinessImpl, M:insertaBitacoraPropuesta");	
		}
		
		return resultado;
		
	}
	
	public String exportaExcel(String datos) {
		String respuesta = "";
	    
	    try {	    	
			//Se crea el libro Excel
			HSSFWorkbook wb = obtenerExcel(datos);			
            
            respuesta = ConstantesSet.RUTA_EXCEL + "propuestas " + Utilerias.indicadorFecha() +".xls";
            File outputFile = new File(respuesta);
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
        } catch (IOException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:exportaExcel");
        	respuesta = "";
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:exportaExcel");
            respuesta = "";
        }
        return respuesta;
	}
	
	public String exportaExcelPropuestas(String datos) {
		String respuesta = "";
	    Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
	    try {	    	
			//Se crea el libro Excel
			HSSFWorkbook wb = Utilerias.generarExcel(new String[]{
													"Fecha Elaboración",
										            "Fecha Pago",
										            "Sociedad",
										            "Nombre Sociedad",
										            "Total",
										            "Modena",
										            "Concepto",
										            "Vía Pago",
										            "Id Propuesta",
										            "Origen Propuesta",
										            "Usuario 1",
										            "Usuario 2"
												}, 
												parameters, 
												new String[]{
														"fechaElaboracion", //usuario_uno, usuario_dos -> obtiene el numero de usuario
														"fechaPago",
														"sociedad",
														"nombreSociedad",
														"total",														
														"moneda", //Fecha captura - revisar
														"concepto", 
														"viaPago",
														"idPropuesta",
														"origenPropuesta",
														"usuario1",
														"usuario2"
												});			
            
            respuesta = ConstantesSet.RUTA_EXCEL + "propuestas " + Utilerias.indicadorFecha() +".xls";
            File outputFile = new File(respuesta);
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
        } catch (IOException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:exportaExcel");
        	respuesta = "";
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:exportaExcel");
            respuesta = "";
        }
        return respuesta;
	}
	
	public String exportaExcelPropDet(String datos) {
		String respuesta = "";
	    
	    try {	    	
			//Se crea el libro Excel
			HSSFWorkbook wb = obtenerExcelPropDet(datos);			
            
            respuesta = ConstantesSet.RUTA_EXCEL + "propuestas " + Utilerias.indicadorFecha() +".xls";
            File outputFile = new File(respuesta);
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
        } catch (IOException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:exportaExcel");
        	respuesta = "";
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:exportaExcel");
            respuesta = "";
        }
        return respuesta;
	}

	//Agregado EMS 27/11/2015
	@Override
	public int validaFacultadUsuario(String facultad) {
		int resp = 0;
		
		try {
			System.out.println("Biss"+facultad);
			//Aquï¿½ valida la facultad que viene desde el extJs y es la de autorizacion de transfers 
			resp = consultaPropuestasDao.validaFacultadUsuario(facultad);
			
			/*if(resp == 0) {
				//Aquï¿½ valida la facultad de autorizaciï¿½n de cheques
				resp = consultaPropuestasDao.validaFacultad(154);
				
				if(resp != 0) resp = 1000;
			}*/
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PagoPropuestasBusiness, M:validaFacultadUsuario");
		}
		return resp;
	}
	
	@Override
	public List<MovimientoDto> existeChequeraBenef(List<MovimientoDto> movimientos) {
		
		int count = 0;
		
		try {
		
			for(int i=0; i<movimientos.size(); i++){
				//Envia el movimiento y revisa si existe o no su chequera.
				count = consultaPropuestasDao.existeChequeraBenef(movimientos.get(i));
				//Si no existe se pinta de color
				if(count == 0){
					movimientos.get(i).setColor("color:#B1A0C7"); 
					
				}else{
					movimientos.get(i).setColor("color:#000000");
				}
				
			}
			
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PagoPropuestasBusiness, M:existeChequeraBenef");
		}
		return movimientos;
	}
	
	public List<LlenaComboEmpresasDto>llenarComboEmpresa(int usuario){
		return consultaPropuestasDao.llenarComboEmpresa(usuario);
	}

	@Override
	public Map<String, Object> validarBancoChequera(List<SeleccionAutomaticaGrupoDto> listSeleccion) {
		
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("lista", null);
		
		StringBuffer claves = new StringBuffer();
		try{
			
			for(int i=0; i<listSeleccion.size();i++){
				claves.append("'" + listSeleccion.get(i).getCveControl()+"',");
			}
			
			String cad="";
			if(!claves.toString().equals("")){
				cad = claves.toString().substring(0, claves.length()-1);
				System.out.println("cadControl: " + cad);
			}
			
			List<String> clavesPagadoras = new ArrayList<>();
			clavesPagadoras = consultaPropuestasDao.validarBancoChequeraPagadoraBenef(cad, 1);
			if(clavesPagadoras.size() > 0){
				resultado.put("error", "1");
				resultado.put("lista", clavesPagadoras);
			}
				
			List<String> clavesBenef = new ArrayList<>();
			clavesBenef = consultaPropuestasDao.validarBancoChequeraPagadoraBenef(cad, 2);
			if(clavesBenef.size() > 0){
				resultado.put("error", "2");
				resultado.put("lista", clavesBenef);
			}

			//Eliminamos cveControl repetidas.
			clavesBenef.removeAll(clavesPagadoras);
			
			if(clavesPagadoras.size() > 0 && clavesBenef.size() > 0){
				resultado.put("error", "3");
				clavesPagadoras.addAll(clavesBenef);
				resultado.put("lista", clavesPagadoras);
			}else{ 
				//A claves benef se eliminaron todas las claves y quedo vacia, 
				//el mapa tiene referencia a benef, cambiarlo a pagadora que es 
				//la que contiene las claves si repetir.
				resultado.put("lista", clavesPagadoras);
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasBusiness, M:validarBancoChequera");
		}
		
		return resultado;
	}
	
	@Override
	public Map<String, String> actualizaMultisociedad(String cveControl, 
													  StringBuffer folios, 
													  MovimientoDto movDatos) {
		
		Map<String, String> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("msgUsuario", "");
		
		if(folios.equals("")){
			resultado.put("error", "No hay detalles seleccionados.");
			return resultado;
		}
		
		if(movDatos == null){
			resultado.put("error", "No hay datos para actualizar.");
			return resultado;
		}
		
		try{
			
			resultado = consultaPropuestasDao.actualizaMultisociedad(cveControl, folios, movDatos);
					
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:ConsultaPropuestasBusiness, M:actualizaMultisociedad");
		}
		
		return resultado;
	}
	
	@Override
	public List<ColoresBitacoraDto> obtenerColoresPropuesta() {
		
		List<ColoresBitacoraDto> result = new ArrayList<>();
		
		try{
			
			result = consultaPropuestasDao.obtenerColoresPropuesta();
					
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:ConsultaPropuestasBusiness, M:obtenerColoresPropuesta");
		}
		
		return result;
	}
	
	@Override
	public List<ColoresBitacoraDto> obtenerColoresDetalle() {
		
		List<ColoresBitacoraDto> result = new ArrayList<>();
		
		try{
			
			result = consultaPropuestasDao.obtenerColoresDetalle();
					
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:ConsultaPropuestasBusiness, M:obtenerColoresDetalle");
		}
		
		return result;
	}
	
	@Override
	public Map<String, String> actualizaCompraTransfer(List<SeleccionAutomaticaGrupoDto> listSag, String operacion) {
		
		Map<String, String> result = new HashMap<>();
		result.put("error", "");
		result.put("mensaje", "");
		
		try{
			/*
			  validamos que no este vacio ningun dato.
			 */
			if(listSag.size()<= 0){
				result.put("error", "Error: No hay propuestas para actualizar compra de transferencias.");
				return result;
			}
			
			for(int i=0; i<listSag.size(); i++){
				if(listSag.get(i).getCveControl().equals("")){
					result.put("error", "Error: ID propuesta vacï¿½a para actualizar compra de transferencias.");
					return result;
				}
			}
			
			if(operacion.equals("") || (!operacion.equals("M") && !operacion.equals("D")) ){
				result.put("error", "Error: Tipo de operacion (Marcar/Desmarcar) invï¿½lido");
				return result;
			}
			
			//Concateno todas las cve_control en una cadena para realizar una sola consulta.
			
			StringBuilder claves = new StringBuilder();
			
			for(int i=0; i<listSag.size(); i++){
				claves.append("'"+listSag.get(i).getCveControl()+"',");
			}
			
			if(claves.length()>0){
				claves.delete(claves.length()-1, claves.length()); //quita la ultima coma.
			}
			
			result = consultaPropuestasDao.actualizaCompraTransfer(claves.toString(), operacion);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:ConsultaPropuestasBusiness, M:actualizaCompraTransfer");
		}
		
		return result;
	}

	@Override
	public String sumaTotalDetalles(String cveControl) {
		
		String result = "";
		
		try{
			result = consultaPropuestasDao.sumaTotalDetalles(cveControl);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:ConsultaPropuestasBusiness, M:sumaTotalDetalles");
		}
		
		return result;
	}
	
	public HSSFWorkbook obtenerExcelPropDet(String datos) {
		
		HSSFWorkbook wb = new HSSFWorkbook();
		
		String[] headers = new String[]{
			"Fecha Elaboración",
            "Fecha Pago",
            "Sociedad",
            "Nombre Sociedad",
            "Total",
            "Moneda",
            "Concepto",
            "Vía Pago",
            "Id Propuesta",
            "Origen Propuesta",
            "Usuario 1",
            "Usuario 2",
            "Usuario 3"
		};
		
		String[] keys = new String[]{
			"fechaElaboracion", //usuario_uno, usuario_dos -> obtiene el numero de usuario
			"fechaPago",
			"sociedad",
			"nombreSociedad",
			"total",														
			"moneda", //Fecha captura - revisar
			"concepto", 
			"viaPago",
			"idPropuesta",
			"origenPropuesta",
			"usuario1",
			"usuario2",
			"usuario3"
		};
		
		String[] headersD = new String[]{
			"No Empresa",
			"Nombre Empresa",
            "No. Beneficiario",
            "Beneficiario",
            "No. Factura",
            "No. Documento",
            //"Clase Docto.",
            "Importe",
            "Divisa",
            "Forma Pago",
            "Concepto",
            "Fecha Propuesta",
            "Fecha Vencimiento",
            "Fecha Contabilización",
            "Fecha Documento",
            "Banco Pago",
            "Chequera Pago",
            "Banco Beneficiario",
            "Chequera Beneficiaria",
            "Clabe",
            "Referencia",
            "Id. Grupo",
            "Grupo de Tesorería",
            "Id. Rubro",
            "Descripción Rubro"
		};
		
		String[] keysD = new String[]{
			"noEmpresa",
			"nomEmpresa",
			"equivale",
			"beneficiario",
			"noFactura",
			"noDocto",														
			//"idContable",
			"importe", 
			"idDivisa",
			"descFormaPago",
			"concepto",
			"fecPropuesta",
			"fecValor",
			"fecContabilizacion",
			"fecOperacion",
			"bancoPago",
			"idChequera",
			"idBancoBenef",
			"idChequeraBenef",
			"clabe",
			"referenciaCte",
			"idGrupo",
			"descGrupo",
			"idRubro",
			"descRubro"
			};
		
		try {
			
			Gson gson = new Gson();
			List<Map<String, String>> data = 
					gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			
			HSSFSheet sheet = wb.createSheet();

			int rowIdx = 0;
			int cellIdx = 0;

			
			for (Iterator<Map<String, String>> rows = data.iterator(); rows.hasNext();) {
				//Carta----------------------------------------------------------------
				cellIdx=0;
				HSSFRow hssfHeader = sheet.createRow(rowIdx);
				HSSFFont font = wb.createFont();
				HSSFCellStyle cellStyle = wb.createCellStyle();
				font.setColor(new HSSFColor.WHITE().getIndex());
				//font.setFontHeight(new Short("18"));
				cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				cellStyle.setFillPattern(HSSFCellStyle.FINE_DOTS );
				cellStyle.setFont(font);
				cellStyle.setFillForegroundColor(new HSSFColor.DARK_GREEN().getIndex());
				
				
				HSSFCell celdaTitulo = hssfHeader.createCell(cellIdx);
				celdaTitulo.setCellStyle(cellStyle);
				celdaTitulo.setCellValue(new HSSFRichTextString("Propuesta"));
				
				sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, headers.length - 1));
				
				rowIdx++;
				// Header
				hssfHeader = sheet.createRow(rowIdx);
				font = wb.createFont();
				cellStyle = wb.createCellStyle();
				font.setColor(new HSSFColor.WHITE().getIndex());
				cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				cellStyle.setFillPattern(HSSFCellStyle.FINE_DOTS );
				cellStyle.setFont(font);
				cellStyle.setFillForegroundColor(new HSSFColor.LIGHT_BLUE().getIndex());
				
				for (String string : headers) {
					HSSFCell hssfCell = hssfHeader.createCell(cellIdx++);
					hssfCell.setCellStyle(cellStyle);
					hssfCell.setCellValue(new HSSFRichTextString(string));
				}
				
				//Data
				rowIdx++;
				
				Map<String, String> row = rows.next();
				HSSFRow hssfRow = sheet.createRow(rowIdx++);
				cellIdx = 0;
				
				for (String string : keys) {
					HSSFCell hssfCell = hssfRow.createCell(cellIdx++);
					hssfCell.setCellValue(new HSSFRichTextString(row.get(string)));
				}
				//---------------------------------------------------------------------
				
				//Detalle----------------------------------------------------------------
				PagosPropuestosDto consDetalleDto = new PagosPropuestosDto();
				consDetalleDto.setIdGrupoEmpresa(Integer.parseInt(row.get("idGrupoEmpresa")));
				consDetalleDto.setCveControl(row.get("idPropuesta"));
				consDetalleDto.setFecha(consultaPropuestasDao.consultarFechaHoy());
				
				List<Map<String, String>> detalle = 
						consultaPropuestasDao.consultarDetalleStoredParaExcel(consDetalleDto);
				
				cellIdx = 0;
				
				HSSFRow hssfHeaderD = sheet.createRow(rowIdx);
				HSSFFont fontD = wb.createFont();
				HSSFCellStyle cellStyleD = wb.createCellStyle();
				fontD.setColor(new HSSFColor.WHITE().getIndex());
				//font.setFontHeight(new Short("18"));
				cellStyleD.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				cellStyleD.setFillPattern(HSSFCellStyle.FINE_DOTS );
				cellStyleD.setFont(fontD);
				cellStyleD.setFillForegroundColor(new HSSFColor.DARK_GREEN().getIndex());
				
				
				HSSFCell celdaTituloD = hssfHeaderD.createCell(cellIdx);
				celdaTituloD.setCellStyle(cellStyleD);
				celdaTituloD.setCellValue(new HSSFRichTextString("Detalle"));
				
				sheet.addMergedRegion(new CellRangeAddress(rowIdx, rowIdx, 0, headersD.length - 1));
				
				rowIdx++;
				// Header
				hssfHeaderD = sheet.createRow(rowIdx);
				fontD = wb.createFont();
				cellStyleD = wb.createCellStyle();
				fontD.setColor(new HSSFColor.WHITE().getIndex());
				cellStyleD.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				cellStyleD.setFillPattern(HSSFCellStyle.FINE_DOTS );
				cellStyleD.setFont(fontD);
				cellStyleD.setFillForegroundColor(new HSSFColor.LIGHT_BLUE().getIndex());
				
				for (String stringD : headersD) {
					HSSFCell hssfCellD = hssfHeaderD.createCell(cellIdx++);
					hssfCellD.setCellStyle(cellStyleD);
					hssfCellD.setCellValue(new HSSFRichTextString(stringD));
				}
				
				//Data
				rowIdx++;
				for (Iterator<Map<String, String>> rowsD = detalle.iterator(); rowsD.hasNext();) {
					Map<String, String> rowD = rowsD.next();
					HSSFRow hssfRowD = sheet.createRow(rowIdx++);
					cellIdx = 0;
					
					for (String stringD : keysD) {
						HSSFCell hssfCellD = hssfRowD.createCell(cellIdx++);
						hssfCellD.setCellValue(new HSSFRichTextString(rowD.get(stringD)));
					}
				}
				rowIdx+=5;
				//-----------------------------------------------------------------------
				
				
			}
			
			wb.setSheetName(0, "Hoja 1");
			
			for (int i = 0; i < headersD.length; i++) {
				sheet.autoSizeColumn((short)i);
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(
					new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Impresion, C: CartasPorEntregarBusinessImpl, "
					+ "M: reporteCartasEmitidas");
		} return wb;
	}

	@Override
	public HSSFWorkbook obtenerDetallesReporte(int idUsuario) {
		
		HSSFWorkbook hb = null;
		
	    try {	    	
			//Se crea el libro Excel
			hb = Utilerias.generarExcel(new String[]{
													"No Empresa",
													"Nombre Empresa",
										            "No. Beneficiario",
										            "Beneficiario",
										            "No. Factura",
										            "No. Documento",
										            "Clase Docto.",
										            "Importe",
										            "Divisa",
										            "Forma Pago",
										            "Concepto",
										            "Fecha Propuesta",
										            "Fecha Vencimiento",
										            "Fecha Contbilizaciï¿½n",
										            "Fecha Documento",
										            "Banco Pago",
										            "Chequera Pago",
										            "Banco Beneficiario",
										            "Chequera Beneficiaria"
										            
												}, 
												consultaPropuestasDao.obtenerDetallesReporte(idUsuario),
												new String[]{
														"noEmpresa", 
														"nomEmpresa",
														"equivale",
														"beneficiario",
														"noFactura",
														"noDocto",														
														"idContable",
														"importe", 
														"idDivisa",
														"descFormaPago",
														"concepto",
														"fecPropuesta",
														"fecValor",
														"fecContabilizacion",
														"fecOperacion",
														"bancoPago",
														"idChequera",
														"idBancoBenef",
														"idChequeraBenef"
												},
												"Detallado de propuestas"
												);			
			
            //respuesta = ConstantesSet.RUTA_EXCEL + "propuestas " + Utilerias.indicadorFecha() +".xls";
            //File outputFile = new File(respuesta);
          
			//FileOutputStream outs = new FileOutputStream(outputFile);
			//wb.write(outs);
			//outs.close();            
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:obtenerDetallesReporte");
        }
	    
	    return hb;
	}
	
	public Map<String, String> actualizarChequeraNoAgrupa(String cveControl, String folios){
		
		Map<String, String> result = new HashMap<>();
		result.put("error", "");
		result.put("mensaje", "");
		
		try{
			result = consultaPropuestasDao.actualizarChequeraNoAgrupa(cveControl, folios);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasBusiness, M:actualizarChequeraNoAgrupa");	
		}
		
		return result;
	}

	public String verificaFacultadUsuario(String facultad){
		
		String result = "";
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 51))
			return result;
		
		try{
			
			result = consultaPropuestasDao.verificaFacultadUsuario(facultad);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasBusiness, M:verificaFacultadUsuario");	
		}
		
		return result;
	}

	@Override
	public List<LlenaComboGralDto> LlenarComboGralB(LlenaComboGralDto dto) {
		return consultaPropuestasDao.llenarComboGralB(dto);
	}

	@Override
	public double obtenerTipoCambio(String sDivisa, Date ponerFechaDate) {
		return consultaPropuestasDao.obtenerTipoCambio(sDivisa, ponerFechaDate);
	}

	@Override
	public Map<String, String> eliminarPropuesta(List<String> listaCveControl) {
		Map<String, String> respuesta = new HashMap<String, String>();
		respuesta.put("error", "0");
		respuesta.put("mensaje", "No se realizo ninguna acción.");
		
		for (String cveControl : listaCveControl) {
			if (consultaPropuestasDao.verificaPropuesta(cveControl)) {
				respuesta = consultaPropuestasDao.eliminaPropuesta(cveControl);
			}
		}
		return respuesta;
	}

	@Override
	public Map<String, Object> verificaChequerabenef(String idDivisa, String[] folios) {
		Map<String, Object> result = new HashMap<String, Object>();
		String documento = "";
		String foliosPermitidos = "";
		for (String folio : folios) {
			String doctoVerificado = consultaPropuestasDao.verificaChequerabenef(idDivisa, folio);
			if (!doctoVerificado.equals("")) {
				documento += doctoVerificado + ", ";
			} else {
				foliosPermitidos += folio + "|";
			}
		}
		
		result.put("folios", foliosPermitidos);
		if (!documento.equals("")) {
			result.put("mensage", 
					"Los siguientes documentos no tienen chequera beneficiaria con la divisa seleccionada:\n" + documento
					+ "\n Estos no se modificaran como pago cruzado.");
			result.put("error", 0);
		} else if (foliosPermitidos.equals("")) {
			result.put("mensage", 
					"No se modificara ningun pago no cuentan con chequera beneficiaria con la divisa seleccionada.");
			result.put("error", 3);
		} else {
			result.put("mensage", "");
			result.put("error", 0);			
		}
		return result;
	}
	
	public String obtenerDivisaChequera(String chequera){
		
		String result = "";
		
		if(!Utilerias.haveSession(WebContextManager.get()))
			return result;
		
		try{
			
			result = consultaPropuestasDao.obtenerDivisaChequera(chequera);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasBusiness, M:obtenerDivisaChequera");	
		}
		
		return result;
	}
	
	public List<SeleccionAutomaticaGrupoDto> obtenerNiveles(GrupoEmpresasDto dto){
		return consultaPropuestasDao.obtenerNiveles(dto);
	}

	public boolean buscarAutorizacion(SeleccionAutomaticaGrupoDto dtoGMaster, boolean ban, boolean ban2) {
		Gson gson = new Gson();
		boolean existe = true;
		try {
			//List<Map<String, String>> objDetalle = gson.fromJson(detalle, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		//	for(int i=0; i<objDetalle.size(); i++) {
				if(!consultaPropuestasDao.existeAutorizacion(dtoGMaster.getCveControl(),dtoGMaster.getIdUsuario(), ban, ban2, false)) return false;
			//}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:buscarDetalleAuto");
		}
		return existe;
	}
	public Map<String, Object>buscarPropuestasPagadas(List<SeleccionAutomaticaGrupoDto> listaDto){
		Map<String, Object> result= new HashMap<String, Object>();
		boolean pagada = false;
		result.put("bit", "0");
		try {
			System.out.println("entro a buscar propuestas pagadas");
			//Se agrego forEach para autorizar multiples registros
			for(SeleccionAutomaticaGrupoDto dtoGMaster: listaDto){
				pagada = consultaPropuestasDao.verificaEstatusProp(dtoGMaster.getCveControl());
				if(pagada) {
					System.out.println("tamaño de la lista "+listaDto.size());
					result.put("msgUsuario", "1");
					return result;
//					if(listaDto.size()>1){
//						//noDesautorizada++;
//						continue;
//					}else{
//						result.put("msgUsuario", "La propuesta esta pagada, no puede quitar la autorización!!");
//						return result;
//					}
				}
				result.put("msgUsuario", "0");
				 
			}//fin foreach
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:quitarAutorizacion");
		}
		System.out.println("result "+result);
		return result;
	}

	public JRDataSource obtenerReportePropuestasAutorizadas(SeleccionAutomaticaGrupoDto dtoIn){
		PagosPropuestosDto consDetalleDto= new PagosPropuestosDto();
//		List<MovimientoDto> listDetalle= new ArrayList<MovimientoDto>();
		JRMapArrayDataSource jrDataSource = null;
		List<Map<String, Object>> listReport = new ArrayList<Map<String, Object>>();
		
		try{ 
			consDetalleDto.setIdGrupoEmpresa(dtoIn.getIdGrupoFlujo());
			consDetalleDto.setCveControl(dtoIn.getCveControl());
			consDetalleDto.setFecha(consultaPropuestasDao.consultarFechaHoy());
			consDetalleDto.setTotal(dtoIn.getTotal().toString());
			listReport=consultaPropuestasDao.obtenerReportePropuestasAutorizadas(consDetalleDto);
//			Map<String, Object> map = new HashMap<String, Object>();
			
//			for(int i=0;i<listReport.size();i++){
//				importe+=Double.parseDouble(listReport.get(i).get("importe").toString());
//				map.put("importeTotal", importe);
//				
//			}
//			listReport.add(listReport.size(),map);
//			System.out.println("importe acumulado "+importe);
				
		    jrDataSource = new JRMapArrayDataSource(listReport.toArray());
		    
					}catch(Exception e){
						e.printStackTrace();
						
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:consultarDetalle");
		}
		return jrDataSource;
	}

	@Override
	public Map<String, String> actualizaCVT(String folios) {
		
		Map<String, String> result = new HashMap<>();
		result.put("error", "");
		result.put("mensaje", "");
		
		try{
			result = consultaPropuestasDao.actualizaCVT( folios);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasBusiness, M:actualizarChequeraNoAgrupa");	
		}
		
		return result;
	}	


	
}