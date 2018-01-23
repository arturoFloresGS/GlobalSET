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

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.egresos.dao.ConsultaPropuestasBDao;
import com.webset.set.egresos.dao.impl.ConsultaPropuestasBDaoImpl;
import com.webset.set.egresos.dao.impl.ConsultasGeneralesEgresosDao;
import com.webset.set.egresos.dto.BitacoraPropuestasDto;
import com.webset.set.egresos.dto.BloqueoPagoCruzadoDto;
import com.webset.set.egresos.dto.ColoresBitacoraDto;
import com.webset.set.egresos.dto.ComunEgresosDto;
import com.webset.set.egresos.dto.ConsultaPropuestasDto;
import com.webset.set.egresos.dto.PagosPropuestosDto;
import com.webset.set.egresos.dto.ParamConsultaPropuestasDto;
import com.webset.set.egresos.service.ConsultaPropuestasBService;
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

public class ConsultaPropuestasBBusinessImpl implements ConsultaPropuestasBService{

	private ConsultaPropuestasBDao consultaPropuestasBDao;
	Funciones funciones= new Funciones();
	Bitacora bitacora = new Bitacora(); 
	GlobalSingleton globalSingleton;
	private String autoDesAuto = "";
	private ConsultasGeneralesEgresosDao consultasGeneralesEgresosDao;
	
	
	public List<LlenaComboGralDto> LlenarComboGral(LlenaComboGralDto dto) {
		return consultaPropuestasBDao.llenarComboGral(dto);
	}
	
	public Date obtenerFechaHoy(){
		return consultaPropuestasBDao.obtenerFechaHoy();
	}

	public List<LlenaComboGralDto>llenarComboBeneficiario(LlenaComboGralDto dto){
		return consultasGeneralesEgresosDao.llenarComboBeneficiario(dto);	
	}
	
	public List<SeleccionAutomaticaGrupoDto> consultarSeleccionAutomatica(ConsultaPropuestasDto dtoIn){
		List<SeleccionAutomaticaGrupoDto>list= new ArrayList<SeleccionAutomaticaGrupoDto>();
		
		try {
			list=consultaPropuestasBDao.consultarSeleccionAutomatica(dtoIn);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:consultaPropuestasBDao, M:consultarSeleccionAutomatica");
		}
		return list;
	}
	
	public List<SeleccionAutomaticaGrupoDto> consultarSeleccionAutomaticaStored(ConsultaPropuestasDto dtoIn){
		List<SeleccionAutomaticaGrupoDto>list= new ArrayList<SeleccionAutomaticaGrupoDto>();
		int count=0;
		String rech="";
		
		try {
			list=consultaPropuestasBDao.consultarSeleccionAutomaticaStored(dtoIn);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:consultaPropuestasBDao, M:consultarSeleccionAutomatica");
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
			consDetalleDto.setFecha(consultaPropuestasBDao.consultarFechaHoy());
			listDetalle=consultaPropuestasBDao.consultarDetalleStored(consDetalleDto);
			
			
			/*if(dtoIn.getCveControl().trim().substring(0,1).equals("M")){
				consDetalleDto.setIdGrupoEmpresa(dtoIn.getIdGrupoFlujo());
				//consDetalleDto.setIdGrupo(dtoIn.getIdGrupo());//Checar
				consDetalleDto.setCveControl(dtoIn.getCveControl());
				consDetalleDto.setFecha(consultaPropuestasBDao.consultarFechaHoy());
				//consDetalleDto.setPsDivision("");
				listDetalle=consultaPropuestasBDao.consultarDetalle(consDetalleDto);
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
					listBloqueados = consultaPropuestasBDao.obtenerBloqueoProveedor(
													""+listDetalle.get(i).getNoEmpresa(),
													""+listDetalle.get(i).getEquivale(),
													dtoIn.getCveControl(),
													listDetalle.get(i).getNoDocto());
					
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
						listBloqueados = consultaPropuestasBDao.obtenerBloqueoSAP(
								""+dtoIn.getCveControl(),
								""+listDetalle.get(i).getNoDocto());
						
						if(listBloqueados.size()>0){
							listDetalle.get(i).setColor("color:#090DFA"); //Azul
						}
						
					}
					
					//Valida que existan las chequeras benef
					count = consultaPropuestasBDao.existeChequeraBenef(listDetalle.get(i));
					//Si no existe se pinta de color
					if(count == 0){
						listDetalle.get(i).setColor("color:#7030A0"); //morado
					}
					
				}
			}
			*/
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:consultaPropuestasBDao, M:consultarDetalle");
		}
		return listDetalle;
	}
	
	
	public Map<String, Object>autorizarProp(List<SeleccionAutomaticaGrupoDto> listaDto, String detalle, String autoDes, int autCheq) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {
			autoDesAuto = autoDes;
			
			if(autoDesAuto.equals("AUT"))
				
				result = autorizar(listaDto, detalle, autCheq);
			
			else
				result = quitarAutorizacion(listaDto, detalle);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:consultaPropuestasBDao, M:autorizarProp");
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
		int numReg = 0;
		int noAutorizadas = 0;
		
		Gson gson1 = new Gson();
		List<Map<String, String>> objDetalle = null;
		
		result.put("bit","0");
		
		if(!detalle.equals("")){
			objDetalle = gson1.fromJson(detalle, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		}/*else{
			result.put("msgUsuario","No hay detalles");
			return result;
		}*/
		
		//List<Map<String, String>> objDetalle = gson1.fromJson(detalle, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
		try {
			
			if(listaDto.size() > 0){
				bUsuarioAutenticado = consultaPropuestasBDao.validarUsuarioAutenticado(listaDto.get(0).getIdUsuario(), funciones.encriptador(listaDto.get(0).getPsw()));
				
			}else{
				result.put("msgUsuario","Propuestas no v�lidas.");
			
				return result;
			}
			
			if(!bUsuarioAutenticado) {
				result.put("msgUsuario","Usuario No V�lido");
				return result;
			}
			
			//inicia foreach, para multiples autorizaciones
			for(SeleccionAutomaticaGrupoDto dtoGMaster : listaDto){
				
				/*if(autCheq == 1000) {
					for(int i=0; i<objDetalle.size(); i++) {
						if(consultaPropuestasBDao.verificaFormaPago(Integer.parseInt(objDetalle.get(i).get("noFolioDet"))) == 0) {
							if(listaDto.size()>1){
								noAutorizadas++;
								continue;
							}else{
								result.put("msgUsuario", "Usted cuenta solamente con la facultad para autorizar propuestas de cheques!!");
								return result;
							}
							
						}
					}
				}*/
				
				/*SE COMENTA PORQUE LA AUTORIZACI�N SE HACE SOLAMENTE POR MAESTRO NO POR DETATLLES.
				if(dtoGMaster.getUsuarioUno() != 0 && dtoGMaster.getUsuarioUno() != dtoGMaster.getIdUsuario()) {
					numReg = consultaPropuestasBDao.verificaRegPend(dtoGMaster.getCveControl(), dtoGMaster.getIdGrupoFlujo());
				 
					 if(numReg != 0) {
						 if(listaDto.size()>1){
							 noAutorizadas++;
							 continue;
						 }else{
							 result.put("msgUsuario", "La propuesta tiene una factura no autorizada, favor de eliminarla!!");
							 return result;
						 }
						 
					 }
				}*/
				
				listPropGral = consultaPropuestasBDao.seleccionarPropGral(dtoGMaster.getIdGrupoFlujo(), dtoGMaster.getIdGrupo(),dtoGMaster.getCveControl());			
				
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
				if(dtoGMaster.getNivelAutorizacion() <= 0){
					if(listaDto.size()>1){
						 noAutorizadas++;
						 continue;
					 }else{
						 result.put("msgUsuario","Esta propuesta no requiere Autorizaci�n");
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
						 result.put("msgUsuario","La propuesta ya alcanz� el l�mite de autorizaciones");
						 return result;
					 }
					 
				 }
				 
				 if(dtoGMaster.getUsuarioUno() > 0 && dtoGMaster.getUsuarioUno() != dtoGMaster.getIdUsuario()) {
					 //if(consultaPropuestasBDao.validaTodasAutorizadas(dtoGMaster.getCveControl(), "<>") < 0) {
					 if(!buscarDetalleAuto(detalle, dtoGMaster, false, true)) {
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
						 result.put("msgUsuario","Su autorizaci�n ha sido almacenda con exito");
					 }
					 return result;
				 }
				 
				 // If Me.cmdAutorizar.Tag = "AUTMANPP" Then//checar
				 if(!detalle.equals("")){
					 actAutori = autorizaDetalle(detalle, dtoGMaster, psCampo);
				 }
				 
				 if(dtoGMaster.getUsuarioUno() != 0 && dtoGMaster.getUsuarioUno() != dtoGMaster.getIdUsuario())
					 numReg = consultaPropuestasBDao.buscarRegPend(dtoGMaster.getCveControl());
				 
				 if(numReg == 0)
					 actAutori = consultaPropuestasBDao.actualizarAutorizacionPop(psCampo, ""+dtoGMaster.getIdUsuario(),
						 										dtoGMaster.getIdGrupoFlujo(),dtoGMaster.getCveControl());
				 else{
					 if(listaDto.size()>1){
						 noAutorizadas++;
						 continue;
					 }else{
						  result.put("msgUsuario", "La propuesta tiene una factura no autorizada, favor de eliminarla!!");
						  return result;
					 }
				 }
					
				 
				if(actAutori>0){
					if(listaDto.size()>1){
						result.put("msgUsuario","Se han autorizado " + (listaDto.size() - noAutorizadas) + " propuestas");
					 }else{
						 result.put("msgUsuario","Su autorizaci�n ha sido almacenda con exito");
					 }
					
					result.put("bit","1");
				}
			}//fin foreach
			
			if(listaDto.size()>1){
				result.put("msgUsuario","Se han autorizado " + (listaDto.size() - noAutorizadas) + " propuestas");
			 }
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:consultaPropuestasBDao, M:autorizar");
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
				
				if(!consultaPropuestasBDao.existeReg(objDetalle.get(i).get("noFolioDet").toString(), dtoGMaster.getIdUsuario(), false, false, false) && autoDesAuto.equals("AUT"))
					res = consultaPropuestasBDao.autorizarDetalle(Integer.parseInt(objDetalle.get(i).get("noFolioDet")), dtoGMaster.getIdUsuario());
				else //if(dtoGMaster.getUsuarioUno() != 0)
					res = consultaPropuestasBDao.modificarDetalle(Integer.parseInt(objDetalle.get(i).get("noFolioDet")), psCampo,
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
			list = consultaPropuestasBDao.selectMonto("usuario_dos", cveControl);
			
			if(list.get(0).getImporte() == 0) {
				list = consultaPropuestasBDao.selectMonto("usuario_uno", cveControl);
				
				if(list.get(0).getImporte() == 0)
					list = consultaPropuestasBDao.selectMontoOriginal(cveControl);
			}
			importe = list.get(0).getImporte();
			
			//res = consultaPropuestasBDao.updateMontoPropuesta(importe, cveControl); nO ACTUALIZA SALDO
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
				if(!consultaPropuestasBDao.existeReg(objDetalle.get(i).get("noFolioDet").toString(), dtoGMaster.getIdUsuario(), ban, ban2, false)) return false;
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
		int noDesautorizada = 0;
		
		try {
			//Se agrego forEach para autorizar multiples registros
			for(SeleccionAutomaticaGrupoDto dtoGMaster: listaDto){
				
				pagada = consultaPropuestasBDao.verificaEstatusProp(dtoGMaster.getCveControl());
				
				if(pagada) {
					if(listaDto.size()>1){
						noDesautorizada++;
						continue;
					}else{
						result.put("msgUsuario", "La propuesta esta pagada, no puede quitar la autorizaci�n!!");
						return result;
					}
					
				}
				if(dtoGMaster.getNivelAutorizacion() <= 0){
					if(listaDto.size()>1){
						noDesautorizada++;
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
					 result.put("msgUsuario","Usted no ha autorizado esta propuesta, no puede quitar una autorizaci�n que no ha proporcionado");
					 return result;
				 }else */
				
				if((dtoGMaster.getUsuarioUno()>0 && dtoGMaster.getUsuarioUno()==dtoGMaster.getIdUsuario())
						 ||(dtoGMaster.getUsuarioDos()>0 && dtoGMaster.getUsuarioDos()==dtoGMaster.getIdUsuario())) {
					 if(!buscarDetalleAuto(detalle, dtoGMaster, true, false)) {
						
						 if(listaDto.size()>1){
								noDesautorizada++;
								continue;
							}else{
								 result.put("msgUsuario","Usted no ha autorizado esta propuesta, no puede quitar una autorizaci�n que no ha proporcionado");
								 return result;
							}
						 
						
					 }
				 }else{
					 if(!buscarDetalleAuto(detalle, dtoGMaster, true, false)) {
						 if(listaDto.size()>1){
								noDesautorizada++;
								continue;
							}else{
								result.put("msgUsuario","Usted no ha autorizado esta propuesta, no puede quitar una autorizaci�n que no ha proporcionado");
								return result;
							}
					 }
				 }
					 
				 bUsuarioAutenticado = consultaPropuestasBDao.validarUsuarioAutenticado(dtoGMaster.getIdUsuario(), funciones.encriptador(dtoGMaster.getPsw()));
				 
				 if(!bUsuarioAutenticado) {
					 if(listaDto.size()>1){
						noDesautorizada++;
						continue;
					}else{
						 result.put("msgUsuario","Usuario No Valido");
						 return result;
					}
					
				 }
				 // If Me.cmdAutorizar.Tag = "AUTMANPP" Then//checar
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
				 
				 actAutori = autorizaDetalle(detalle, dtoGMaster, psCampo);
				 
				 if(psCampo.equals("usuario_dos") || psCampo.equals("usuario_uno")) {
					 actAutori = consultaPropuestasBDao.actualizarAutorizacionPop(psCampo, "NULL", dtoGMaster.getIdGrupoFlujo(),dtoGMaster.getCveControl());
				 }
				
				 if(actAutori > 0){
					 result.put("msgUsuario","Las autorizaci�nes han sido eliminadas con exito");
					 result.put("bit", "1");
				 }
				 
			}//fin foreach
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:consultaPropuestasBDao, M:quitarAutorizacion");
		}
		return result;
	}
	
	public List<LlenaComboGralDto> llenarComboGrupoEmpresa(GrupoEmpresasDto dto){
		return consultaPropuestasBDao.llenarComboGrupoEmpresa(dto);
	}
	
	public String eliminarProp(String cveControl, int noEmpresa) {
		String resp = "";
		int resul = 0;
		Gson gson = new Gson();
		List<Map<String, String>> objGrid = gson.fromJson(cveControl, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		
		try {
			
			for(int i=0;i<objGrid.size();i++){
				if(consultaPropuestasBDao.selectCveCtrl(objGrid.get(i).get("cveControl"),noEmpresa) > 0){
					resp = "Solo se pueden eliminar las propuestas que no han sido pagadas";
					return resp;
				}else {
					if(consultaPropuestasBDao.confirmaEliminar(objGrid.get(i).get("cveControl"),noEmpresa) > 0){
						resul = consultaPropuestasBDao.deletePropuesta(objGrid.get(i).get("cveControl"));
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
		int iEmpresa = 0;
		String resp = "";
		String sNoBenef = "";
		
		try {
			if(consultaPropuestasBDao.consultarConfiguraSet(204).equals("SI")) {
				if(dtoGridProp.getUsuarioUno() != 0 || dtoGridProp.getUsuarioDos() != 0 || dtoGridProp.getUsuarioTres() != 0)
					return "No se puede modificar esta propuesta, hasta que se eliminen las autorizaciones";
			}
			/* Se comenta validaci�n, se valida en js las empresas y proveedores, dependiento el caso de que si va a modificar pagadora o beneficiaria.
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
			if(consultaPropuestasBDao.consultarConfiguraSet(204).equals("SI")) {
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
			listNom = consultaPropuestasBDao.nombreUsuarios(usr1, usr2, usr3);
			
			for(int i=0; i<listNom.size(); i++) {
				result += listNom.get(i).getNomUsuarios() + ",";
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:consultaPropuestasBDao, M:nombreUsuarios");
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
				consDetalleDto.setFecha(consultaPropuestasBDao.consultarFechaHoy());//Checar
				consDetalleDto.setPsDivision("");
			}
			listReport = consultaPropuestasBDao.reporteDetalleCupos(consDetalleDto);
	        jrDataSource = new JRMapArrayDataSource(listReport.toArray());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Egresos, C:PagoPropuestasBusiness, M:reportePagoPropuestas");
		}
		return jrDataSource;
	}

	public String consultarConfiguraSet(int indice){
		return consultaPropuestasBDao.consultarConfiguraSet(indice);
	}
	
	@SuppressWarnings({ "unused", "deprecation" })
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
		int numIntercos = 0; //No se usa, pero no se borra por si se habilita intercompa�ias (Petici�n de Carmelo).
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
				
				
				mensaje =  consultaPropuestasBDao.eliminarDetalleStored(cveControl, importeTotal, pdImporte,
						foliosCancela, noUsuario, noCheque, estatusPropuesta);
				
				//De aqui para abajo 
				
				/*if(consultaPropuestasBDao.existeReg(foliosCancela, 0, false, true, true))
					return "No se puede eliminar una factura que ya esta completamente autorizada";
				*/
				
				/*if (numIntercos > 0) {
					respuestaEntera = consultaPropuestasBDao.actualizaPropuesta(foliosCancela, fechaHoy, noUsuario, cveControl, 1);
				}else{*/
				
				/* SE COMENTO DE AQUI EN ADELANTE PARA PRUEBA DEL STORE	
					respuestaEntera = consultaPropuestasBDao.actualizaPropuesta(foliosCancela, fechaHoy, noUsuario, cveControl, 0);
					recibeDatos = consultaPropuestasBDao.buscaRegistroBitacora(foliosCancela);
					
					if (recibeDatos.size() > 0){
						c = 0;
						Date d = new Date();
						while (c < recibeDatos.size()) {
							consultaPropuestasBDao.insertaLogPropuestas(recibeDatos.get(c).getNoEmpresa(), recibeDatos.get(c).getNoDocto(),									
							recibeDatos.get(c).getInvoice(), "SACA DE PROPUESTA", noUsuario, fechaHoy, Integer.toString(d.getHours()) + ":" + Integer.toString(d.getMinutes()));
							c++;
						}
					}
			
					if (noCheque > 0) {			
						recibeDatos = consultaPropuestasBDao.buscaMovimiento(foliosCancela);
						
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
								
								consultaPropuestasBDao.insertaBitacoraCheque(objPropuestasDto);
								
								consultaPropuestasBDao.actualizaNoCheque(objPropuestasDto);
								
								c++;
							}
						}
					}
					//Se buscan movimientos a cancelar
					recibeDatos = consultaPropuestasBDao.buscaSolicitudACancelar(foliosCancela);
					importeResta=importeTotal - pdImporte;
					if (recibeDatos.size() > 0) {
						//if (cveControl.substring(0, 1).equals("M"))
						//	consultaPropuestasBDao.updateMontosCupo(cveControl, importeResta);
						//else
						//	consultaPropuestasBDao.updateMontosAut(true, pdImporte, cveControl,importeTotal);
						
						
						consultaPropuestasBDao.updateMontosCupo(cveControl, importeResta);
						
						if(estatusPropuesta != null && estatusPropuesta.equals("RECHAZADA"))
							consultaPropuestasBDao.updatePropSinRech(cveControl);
						
						
						mensaje = "Se elimino el detalle con �xito.";
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
			listReport = consultaPropuestasBDao.reporteConSelecAut(dtoIn);
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
			//Aqu� valida la facultad que viene desde el extJs y es la de autorizacion de transfers 
			resp = consultaPropuestasBDao.validaFacultad(idFacultad);
			
			if(resp == 0) {
				//Aqu� valida la facultad de autorizaci�n de cheques
				resp = consultaPropuestasBDao.validaFacultad(154);
				
				if(resp != 0) resp = 1000;
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PagoPropuestasBusiness, M:validaFacultad");
		}
		return resp;
	}

	public List<LlenaComboDivisaDto>llenarComboDivisa() {
		return consultaPropuestasBDao.llenarComboDivisa();
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
					val = consultaPropuestasBDao.obtenerTipoCambio(sDivisa, fecHoy);
				else if(!divisaGrid.equals("MN"))
					val = consultaPropuestasBDao.obtenerTipoCambio(divisaGrid, fecHoy);
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
		return consultaPropuestasBDao.llenarComboBancoPag(sDivisa, noEmpresa);
	}
	
	//Agregado EMS:04/11/2015
	public List<LlenaComboGralDto>llenarComboBancoPagBenef(String sDivisa, int noPersona) {
		
		List<LlenaComboGralDto> list = new ArrayList<>();
		
		try{
			list = consultaPropuestasBDao.llenarComboBancoPagBenef(sDivisa, noPersona);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:llenarComboBancoPagBenef");	
		}
		return list;
	}
	
	public List<LlenaComboGralDto>llenarComboChequeraPag(int idBanco, int noEmpresa, String sDivisa) {
		return consultaPropuestasBDao.llenarComboChequeraPag(idBanco, noEmpresa, sDivisa);
	}
	//agregado EMS: 04/11/2015
	public List<LlenaComboGralDto>llenarComboChequeraPagBenef(int idBanco, int noEmpresa, String sDivisa, int idBenef) {
		return consultaPropuestasBDao.llenarComboChequeraPagBenef(idBanco, noEmpresa, sDivisa, idBenef);
	}
	
	public String aceptarModificar(List<Map<String, String>>mapGridProp, List<Map<String, String>>mapGridDetProp, int formaPago,
			 int idBanco, String idChequera, int idBancoBenef, String idChequeraBenef, boolean actualizaBeneficiario, String fecHoy) {
		String resp = "";
		String sFolios = "";
		//String sCambioBitacora = "Nueva Fecha: ";
		List<ComunEgresosDto> bancoCheq = new ArrayList<ComunEgresosDto>();
		List<ComunEgresosDto> bancoCheqBenef = new ArrayList<ComunEgresosDto>();
		//boolean fPProv = false;
		int afec = 0;
		//Map resultado = new HashMap();
		
		try {
			if(consultaPropuestasBDao.consultarConfiguraSet(204).equals("SI")) {
				if(Integer.parseInt(mapGridProp.get(0).get("usr1")) != 0 ||
						Integer.parseInt(mapGridProp.get(0).get("usr2")) != 0 ||
						Integer.parseInt(mapGridProp.get(0).get("usr3")) != 0)
					return "No se puede modificar esta propuesta, hasta que se eliminen todas las autorizaciones";
			}
			
			for(int i=0; i<mapGridDetProp.size(); i++) {
				/*if(formaPago != 0) {
					bancoCheq = consultaPropuestasBDao.buscaBancoChequera(Integer.parseInt(mapGridDetProp.get(i).get("noEmpresa")), formaPago);
					if(bancoCheq.size() <= 0) return "No hay chequera asignada a la empresa " + mapGridDetProp.get(i).get("noEmpresa") + " con la forma de pago seleccionada";
					
					//fPProv = consultaPropuestasBDao.buscaFPProv(Integer.parseInt(mapGridDetProp.get(i).get("noCliente")), formaPago);
					//if(!fPProv) return "El Proveedor No. " + mapGridDetProp.get(i).get("noCliente") + " no tiene autorizada la forma de pago seleccionada";
					
					switch(formaPago) {
					case 1:
					case 5:
						afec = consultaPropuestasBDao.actualizarBCBenef(Integer.parseInt(mapGridDetProp.get(i).get("noEmpresa")),
																	   Integer.parseInt(mapGridDetProp.get(i).get("noFolioDet")), 0, "", formaPago);
						break;
					case 3:
						bancoCheqBenef = consultaPropuestasBDao.buscaBancoChequeraBenef(Integer.parseInt(mapGridDetProp.get(i).get("noCliente")), mapGridDetProp.get(i).get("idDivisa"));
						
						if(bancoCheqBenef.size() != 0)
							afec = consultaPropuestasBDao.actualizarBCBenef(Integer.parseInt(mapGridDetProp.get(i).get("noEmpresa")),
																			Integer.parseInt(mapGridDetProp.get(i).get("noFolioDet")),
																			bancoCheqBenef.get(0).getIdBanco(), bancoCheqBenef.get(0).getIdChequera(), formaPago);
						else
							return "No existen chequeras en la divisa para el proveedor No. " + mapGridDetProp.get(i).get("noCliente");
						break;
					//case 9:
						//afec = consultaPropuestasBDao.actualizarConcepto(Integer.parseInt(mapGridDetProp.get(i).get("noEmpresa")),
						//		   										Integer.parseInt(mapGridDetProp.get(i).get("noFolioDet")), "");
						//break;
						//
					}
					if(afec == 0) return "No se actualizaron los datos";
				}*/
				sFolios += mapGridDetProp.get(i).get("noFolioDet") + ",";
			}
			
			sFolios = sFolios.substring(0, sFolios.length() - 1);
			
			if(idBanco != 0 || actualizaBeneficiario || formaPago != 0) consultaPropuestasBDao.actualizarBanCheqPropuesta(sFolios, formaPago, idBanco, idChequera, idBancoBenef, idChequeraBenef, actualizaBeneficiario);

		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:aceptarModificar");	
		}
		return resp; 
	}
	
	@SuppressWarnings("deprecation")
	public void BitacoraCambiosDocumentos(String sFolios, String sCambioBitacora, String fecHoy) {
		List<ParamConsultaPropuestasDto> datosProp = new ArrayList<ParamConsultaPropuestasDto>();
		globalSingleton = GlobalSingleton.getInstancia();
		Date d = new Date();
		
		datosProp = consultaPropuestasBDao.buscaRegistroBitacora(sFolios);
		
		for(int i=0; i<datosProp.size(); i++) {
			consultaPropuestasBDao.insertaLogPropuestas(datosProp.get(i).getNoEmpresa(), datosProp.get(i).getNoDocto(), datosProp.get(i).getInvoice(),
													sCambioBitacora, globalSingleton.getUsuarioLoginDto().getIdUsuario(),
													fecHoy, Integer.toString(d.getHours()) + ":" + Integer.toString(d.getMinutes()));
		}
	}
	
	public List<MovimientoDto> consultarDetPagos(int noCliente, String fecIni, String fecFin) {
		List<MovimientoDto> result = new ArrayList<MovimientoDto>();
		
		try {
			result = consultaPropuestasBDao.consultarDetPagos(noCliente, fecIni, fecFin);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:consultarDetPagos");
		}
		return result;
	}
	
	@Override
	public List<GrupoDTO> llenarComboGrupo(String idTipoGrupo, int noEmpresa) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<RubroDTO> llenarComboRubros(int idGrupo) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Map<String, Object> insertarFlujoMovtos(List<Map<String, String>> dtoMovto) {
		// TODO Auto-generated method stub
		return null;
	}

	public ConsultaPropuestasBDao getConsultaPropuestasBDao() {
		return consultaPropuestasBDao;
	}
	public void setConsultaPropuestasBDao(
			ConsultaPropuestasBDao consultaPropuestasBDao) {
		this.consultaPropuestasBDao = consultaPropuestasBDao;
		consultasGeneralesEgresosDao = new ConsultasGeneralesEgresosDao(
				((ConsultaPropuestasBDaoImpl)consultaPropuestasBDao).getJdbcTemplate());
	}
	
	
	
	/**
	 * Agregado EMS 170915 
	 * Metodo que exporta a excel un grid del JS, sin hacer consulta sql
	 */
	public String exportaExcel(String datos) {
		String respuesta = "";
	    Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
	    try {	    	
			//Se crea el libro Excel
			HSSFWorkbook wb = Utilerias.generarExcel(new String[]{
													"Fecha Elaboraci�n",
										            "Fecha Pago",
										            "Sociedad",
										            "Nombre Sociedad",
										            "Total",
										            "Modena",
										            "Concepto",
										            "V�a Pago",
										            "Id Propuesta",
										            "Origen Propuesta",
										            "Usuario 1",
										            "Usuario 2",
										            "Doc. Compensaci�n"
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
														"usuario2",
														"po_headers"
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
										            "Forma Pago",
										            "Concepto",
										            "Fecha Propuesta",
										            "Fecha Vencimiento",
										            "Fecha Contbilizaci�n",
										            "Fecha Documento",
										            "Banco Pago",
										            "Chequera Pago",
										            "Banco Beneficiario",
										            "Chequera Beneficiaria",
										            "Doc. Compensaci�n"
										            
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
														"poHeaders"
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
			bUsuarioAutenticado = consultaPropuestasBDao.validarUsuarioAutenticado(dtoGMaster.getIdUsuario(), funciones.encriptador(dtoGMaster.getPsw()));
			
			if(!bUsuarioAutenticado) {
				result.put("msgUsuario","Usuario No Valido");
				return result;
			}else{
				result.put("msgUsuario","");
			}
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:consultaPropuestasBDao, M:autorizarModificaciones");
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
				afec = consultaPropuestasBDao.updateFecProp(cadControl, fechaActualiza);
				consultaPropuestasBDao.updateFecDetProp(cadControl, fechaActualiza);
				
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
			list = consultaPropuestasBDao.consultaPagosCruzados(noPersona, funcion.isNumeric(noPersona));

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
			list = consultaPropuestasBDao.llenarComboBenefPagoCruzado(noPersona, funciones.isNumeric(noPersona));
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasBussinesImpl, M:llenarComboBenefPagoCruzado");
		}
		return list;
	}

	@Override
	public List<LlenaComboDivisaDto>obtenerDivisas(){
		return consultaPropuestasBDao.obtenerDivisas();
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
		
		//Si llego aqu� es porque no hay nada vacio, valido que no exista ya ese proveedor con la misma divisa original.
		if(consultaPropuestasBDao.existeProvPagoCruzado(pagoCruzado.getProveedor(), pagoCruzado.getDivisaOrig())){
			return "Error al registrar: Ya existe el proveedor con esta divisa.";
		}
		
		try {
			afec = consultaPropuestasBDao.insertaNuevoProvPagoCruzado(pagoCruzado);
			if(afec == 0){
				return "Error al registrar el proveedor";
			}else{
				result = "Proveedor registrado con �xito!";
			}
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:consultaPropuestasBDao, M:insertaNuevoProvPagoCruzado");
		}

		return result;
	}

	@Override
	public String eliminarProvPagoCruzado(String noProv, String divOriginal) {
		
		int afec = 0;
		
		try {
			afec = consultaPropuestasBDao.eliminarProvPagoCruzado(noProv, divOriginal);
			
			if(afec == 0){
				return "Error al eliminar el registro";
			}else{
				return "Registro eliminado con �xito!";
			}
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:consultaPropuestasBDao, M:eliminarProvPagoCruzado");
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
			
			existe = consultaPropuestasBDao.existeBloqueo(noEmpresa, noProv, noDocto);
			
			if(existe <= 0 ){
				//No existe - No esta bloqueado
				
				if(!noDocto.equals("")){
					//Busca los datos del documento en movimiento
					if(periodo.equals("")){ datos.put("error", "Seleccione un periodo."); return datos;}
					
					list = consultaPropuestasBDao.buscarBloqueo(noEmpresa, noProv, noDocto, false, periodo);
					
					if(list.size() <= 0){
						datos.put("error", "No se encontr� el documento");
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
					list = consultaPropuestasBDao.buscarBloqueo(noEmpresa, noProv, noDocto, true, periodo);
					
					if(list.size() <= 0){
						datos.put("error", "No se encontr� el documento bloqueado");
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
					list = consultaPropuestasBDao.obtenerBloqueoProveedor(noEmpresa, noProv,"", "");
					if(list.size() <= 0){
						datos.put("error", "No se encontro el proveedor bloqueado");
					}else{
						datos.put("motivo", list.get(0).getMotivo());
						datos.put("existe", "proveedor");
						datos.put("error", "");
					}
				}
				
			}
			
			//list = consultaPropuestasBDao.buscarBloqueo(noEmpresa, noProv, noDocto);
			
			
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
		if(noUsuario <= 0){ datos.put("error", "Usuario no v�lido."); return datos; }
		
		try {
			
			if(!noDocto.equals("")){
				
				existe = consultaPropuestasBDao.existeBloqueo(noEmpresa, noProv, noDocto);
				
				if(existe <= 0 ){
					//Busca los datos del documento en movimiento
					list = consultaPropuestasBDao.buscarBloqueo(noEmpresa, noProv, noDocto, false, "");
					
					if(list.size() <= 0){
						datos.put("error", "Error al guardar, no se encontr� el documento.");
					}else{
						//Inserta documento
						afec = consultaPropuestasBDao.insertarBloqueo(noEmpresa, noProv, noDocto, motivo, noUsuario);
						
						if(afec <= 0){
							datos.put("error", "Error al guardar el documento");
						}else{
							datos.put("resp", "Documento bloqueado con �xito.");
						}
					}
				}else{
					datos.put("error", "El documento ya se encuentra bloqueado.");
				}
				
			}else{
				//Inserta proveedor
				//Existe bloqueo proveedor
				existe = consultaPropuestasBDao.existeBloqueo(noEmpresa, noProv, noDocto);
				
				if(existe <= 0 ){
					//si no existe se bloquea
					afec = consultaPropuestasBDao.insertarBloqueo(noEmpresa, noProv, noDocto,motivo, noUsuario);
					
					if(afec <= 0){
						datos.put("error", "Error al guardar el proveedor.");
					}else{
						datos.put("resp", "Proveedor bloqueado con �xito.");
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
				
				afec = consultaPropuestasBDao.eliminarBloqueo(noEmpresa, noProv, noDocto);
				
				if(afec <= 0 ){
					datos.put("error", "Error al eliminar, no se encontr� el registro bloqueado.");
				}else{
					datos.put("resp", "El registro se ha desbloqueado con �xito.");
				}
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:eliminarBloqueo");
		}
		
		return datos;
		
	}

	@Override
	public HSSFWorkbook datosExcelBloqueados(String nombre, Map<String, String> params) {
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		String[] headers = {"Clave Control","No. Empresa","No. Proveedor","No. Documento","Fecha Pago","Importe","Concepto", "Fecha Bloqueo", "Usuario Bloqueo", "Motivo"};
		
		try{
			
			if(params.get("bloqSet").equals("true") && params.get("bloqSap").equals("true")){
				//Consulta de los dos bloqueos
				workbook = Utilerias.generarExcel("Proveedores bloqueados", headers, consultaPropuestasBDao.datosExcelBloqueados(3));
			}else if(params.get("bloqSet").equals("true")){
				//consulta bloqueados set
				workbook = Utilerias.generarExcel("Proveedores bloqueados", headers, consultaPropuestasBDao.datosExcelBloqueados(1));
			}else if(params.get("bloqSap").equals("true")){
				//consulta bloqueados sap
				workbook = Utilerias.generarExcel("Proveedores bloqueados", headers, consultaPropuestasBDao.datosExcelBloqueados(2));
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
		
		list = consultaPropuestasBDao.consultaProveedoresBloqueados(empresas,propuestas);
		
		return list;
	}
	
	public List<LlenaComboGralDto> llenarComboPeriodos(LlenaComboGralDto dto) {
		
		List<LlenaComboGralDto> listRet= new ArrayList<LlenaComboGralDto>();
		
		try{
			listRet = consultaPropuestasBDao.llenarComboPeriodos(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasBusinessImpl, M:llenaComboPeriodos");
		}
		
		return listRet;
	}
	
	public List<LlenaComboGralDto>llenarComboReglaNegocio(LlenaComboGralDto dto){
		
		List<LlenaComboGralDto> listRet= new ArrayList<LlenaComboGralDto>();
		
		try{
			listRet = consultaPropuestasBDao.llenarComboReglaNegocio(dto);
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
			
			resultado = consultaPropuestasBDao.validaCvePropuesta(cveControl);
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
				resultado.put("error", "El monto m�ximo de los registros es 0.");
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
			
			resultado = consultaPropuestasBDao.insertaSubPropuesta(montoMaximo, nvaCveControl,oldCveControl,idUsuario, fecha);
			
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
				resultado.put("error", "El monto m�ximo de los registros es 0.");
				return resultado;
			}
			
			if(cveControl.equals("")){
				resultado.put("error", "La nueva ID Propuesta no puede estar vacia.");
				return resultado;
			}
			
			resultado = consultaPropuestasBDao.actualizaMontoPropuesta(montoMaximo, cveControl, stencero);
			
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
			
			resultado = consultaPropuestasBDao.actualizaPropuesta(nvaCveControl, noDoctos, oldCveControl, fecha);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasBusinessImpl, M:validaCvePropuesta");	
		}
		
		return resultado;
		
	}
	
	public List<LlenaComboGralDto> llenarComboGrupoEmpresaUnica(GrupoEmpresasDto dto){
		return consultaPropuestasBDao.llenarComboGrupoEmpresaUnica(dto);
	}

	@Override
	public Map<String, String> insertaBitacoraPropuesta(List<BitacoraPropuestasDto> listBitacora) {
		Map<String, String> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		try{
		
			resultado = consultaPropuestasBDao.insertaBitacoraPropuesta(listBitacora);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasBusinessImpl, M:insertaBitacoraPropuesta");	
		}
		
		return resultado;
		
	}

	//Agregado EMS 27/11/2015
	@Override
	public int validaFacultadUsuario(String facultad) {
		int resp = 0;
		
		try {
			//Aqu� valida la facultad que viene desde el extJs y es la de autorizacion de transfers 
			resp = consultaPropuestasBDao.validaFacultadUsuario(facultad);
			
			/*if(resp == 0) {
				//Aqu� valida la facultad de autorizaci�n de cheques
				resp = consultaPropuestasBDao.validaFacultad(154);
				
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
				count = consultaPropuestasBDao.existeChequeraBenef(movimientos.get(i));
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
		return consultaPropuestasBDao.llenarComboEmpresa(usuario);
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
			clavesPagadoras = consultaPropuestasBDao.validarBancoChequeraPagadoraBenef(cad, 1);
			if(clavesPagadoras.size() > 0){
				resultado.put("error", "1");
				resultado.put("lista", clavesPagadoras);
			}
				
			List<String> clavesBenef = new ArrayList<>();
			clavesBenef = consultaPropuestasBDao.validarBancoChequeraPagadoraBenef(cad, 2);
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
			
			resultado = consultaPropuestasBDao.actualizaMultisociedad(cveControl, folios, movDatos);
					
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
			
			result = consultaPropuestasBDao.obtenerColoresPropuesta();
					
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
			
			result = consultaPropuestasBDao.obtenerColoresDetalle();
					
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:ConsultaPropuestasBusiness, M:obtenerColoresDetalle");
		}
		
		return result;
	}
	
	
}
