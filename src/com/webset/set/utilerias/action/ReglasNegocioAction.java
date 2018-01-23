package com.webset.set.utilerias.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.egresos.dto.ConsultaPropuestasDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.AcreedorDoctoDto;
import com.webset.set.utilerias.dto.CondicionPagoDto;
import com.webset.set.utilerias.dto.DoctoTesoreriaDto;
import com.webset.set.utilerias.dto.EmpresaRegNegDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.PlanPagosDto;
import com.webset.set.utilerias.dto.ReglasNegocioDto;
import com.webset.set.utilerias.dto.RubroRegNeg;
import com.webset.set.utilerias.dto.SeleccionAutomaticaGrupoDto;
import com.webset.set.utilerias.service.ReglasNegocioService;
import com.webset.utils.tools.Utilerias;

public class ReglasNegocioAction {

	private Bitacora bitacora = new Bitacora();
	private Contexto contexto = new  Contexto();
	private ReglasNegocioService reglasNegocioService;

	@DirectMethod
	public List<ReglasNegocioDto> obtenerRelacion(){
		
		List<ReglasNegocioDto>list= new ArrayList<ReglasNegocioDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 185))
			return list;
		
		try{
			reglasNegocioService =(ReglasNegocioService)contexto.obtenerBean("reglasNegocioBusinessImpl");
			list = reglasNegocioService.obtenerRelacion();
			 
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:ReglasNegocioAction, M:obtenerRelacion");	
		}
		return list;
	}
	
	//Metodo para consultar las empresas que tiene cada usuario
	@DirectMethod
	public List<EmpresaRegNegDto> obtenerEmpresas(boolean bExiste, int idRegla) {

		List<EmpresaRegNegDto> list = new ArrayList<EmpresaRegNegDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 185))
			return list;
		
		try{
			reglasNegocioService = (ReglasNegocioService) contexto.obtenerBean("reglasNegocioBusinessImpl");
			list = reglasNegocioService.obtenerEmpresas(GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()
														, bExiste, idRegla) ;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioAction, M:obtenerEmpresas");
		}
		
		return list;
	}
	
	//Obtiene Acreedor/Documentos Incluidos o Excluidos
	@DirectMethod
	public List<AcreedorDoctoDto> obtenerAcreedorDocumento(boolean bTipoAcre, int idRegla, String clasificacion) {

		List<AcreedorDoctoDto> list = new ArrayList<AcreedorDoctoDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 185))
			return list;
		
		try{
			reglasNegocioService = (ReglasNegocioService) contexto.obtenerBean("reglasNegocioBusinessImpl");
			list = reglasNegocioService.obtenerAcreedorDocumento( bTipoAcre, idRegla, clasificacion) ;

			if(list.size() > 0 && clasificacion.equals("D")){
				//Concatenamos a 10 digitos
				for(int i = 0; i < list.size(); i++){
					if(list.get(i).getDeAcre() != null && !list.get(i).getDeAcre().equals("")){
						if(list.get(i).getDeAcre().length()<10){
							StringBuffer concat = new StringBuffer();
							for(int j = list.get(i).getDeAcre().length();j<10;j++){
								concat.append("0");
							}
							
							list.get(i).setDeAcre(concat.toString() + list.get(i).getDeAcre());
						}
					}
					
					if(list.get(i).getaAcre() != null && !list.get(i).getaAcre().equals("")){
						if(list.get(i).getaAcre().length()<10){
							StringBuffer concat = new StringBuffer();
							for(int j = list.get(i).getaAcre().length();j<10;j++){
								concat.append("0");
							}
							
							list.get(i).setaAcre(concat.toString() + list.get(i).getaAcre());
						}
					}
					
				}
				
			}	
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioAction, M:obtenerAcreedorDocumento");
		}
		
		return list;
	}
	
	
	
	//Obtiene Clase documento/ Grupo tesoreria
	@DirectMethod
	public List<DoctoTesoreriaDto> obtenerDoctoTesoreria(int idRegla, String clasificacion) {

		List<DoctoTesoreriaDto> list = new ArrayList<DoctoTesoreriaDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 185))
			return list;
		
		try{
			reglasNegocioService = (ReglasNegocioService) contexto.obtenerBean("reglasNegocioBusinessImpl");
			list = reglasNegocioService.obtenerDoctoTesoreria(idRegla, clasificacion) ;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioAction, M:obtenerDoctoTesoreria");
		}
		
		return list;
	}
	

	@DirectMethod
	public CondicionPagoDto obtenerCondicionesPago(int idRegla) {

		CondicionPagoDto condicionPago = new CondicionPagoDto();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 185))
			return condicionPago;
		
		try{
			reglasNegocioService = (ReglasNegocioService) contexto.obtenerBean("reglasNegocioBusinessImpl");
			condicionPago = reglasNegocioService.obtenerCondicionesPago(idRegla) ;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioAction, M:obtenerCondicionesPago");
		}
		
		return condicionPago;
	}
	
	@DirectMethod
	public List<PlanPagosDto> obtenerPlanPago(int idCondicioPago) {

		List<PlanPagosDto> list = new ArrayList<PlanPagosDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 185))
			return list;
		
		try{
			reglasNegocioService = (ReglasNegocioService) contexto.obtenerBean("reglasNegocioBusinessImpl");
			list = reglasNegocioService.obtenerPlanPago(idCondicioPago) ;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioAction, M:obtenerPlanPago");
		}
		
		return list;
	}
	
	@DirectMethod
	public Map<String, Object> validarAcreedores(String jsonDatos, String tipoOper) {

		Map<String, Object> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 185))
			return resultado;
		
		Gson gson = new Gson();
		List<Map<String, String>> mAcreedores = null;
		
		List<AcreedorDoctoDto> list = new ArrayList<>();
		resultado.put("existe", list);
		resultado.put("listaOriginal",list);
		resultado.put("listaAcre",list);
		
		try{
			if(!jsonDatos.equals("")){
				mAcreedores = gson.fromJson(jsonDatos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				for(int i=0; i < mAcreedores.size(); i++){
					AcreedorDoctoDto acre = new AcreedorDoctoDto();
					acre.setDeAcre(mAcreedores.get(i).get("deAcre")!= null && !mAcreedores.get(i).get("deAcre").equals("") ? mAcreedores.get(i).get("deAcre"): "");
					acre.setaAcre(mAcreedores.get(i).get("aAcre")   != null && !mAcreedores.get(i).get("aAcre").equals("") ? mAcreedores.get(i).get("aAcre"): "" );
					//E = Excel, otro es manual
					if(!tipoOper.equals("E")){
						acre.setTipoAcre(mAcreedores.get(i).get("tipoAcre") != null && !mAcreedores.get(i).get("tipoAcre").equals("") ? mAcreedores.get(i).get("tipoAcre"): "" );
					}else{
						acre.setTipoAcre(mAcreedores.get(i).get("clave") != null && !mAcreedores.get(i).get("clave").equals("") ? mAcreedores.get(i).get("clave"): "" );
					}
					acre.setClasificacion("A");
					list.add(acre);
				}
			}
			
			reglasNegocioService = (ReglasNegocioService) contexto.obtenerBean("reglasNegocioBusinessImpl");
			resultado = reglasNegocioService.validarAcreedores(list, tipoOper) ;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioAction, M:validarAcreedores");
		}
		
		return resultado;
	}
	
	@DirectMethod
	public Map<String, String> validarDocumento(String jsonDatos) {

		Map<String, String> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 185))
			return resultado;
		
		Gson gson = new Gson();
		List<Map<String, String>> mDocumentos = null;
		
		List<AcreedorDoctoDto> list = new ArrayList<>();
		
		try{
			if(!jsonDatos.equals("")){
				mDocumentos = gson.fromJson(jsonDatos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				for(int i=0; i < mDocumentos.size(); i++){
					AcreedorDoctoDto docto = new AcreedorDoctoDto();
					docto.setDeAcre(mDocumentos.get(i).get("deAcre")   != null && !mDocumentos.get(i).get("deAcre").equals("") ? mDocumentos.get(i).get("deAcre"): "" );
					docto.setaAcre(mDocumentos.get(i).get("aAcre")   != null && !mDocumentos.get(i).get("aAcre").equals("") ? mDocumentos.get(i).get("aAcre"): "" );
					docto.setTipoAcre(mDocumentos.get(i).get("clave") != null && !mDocumentos.get(i).get("clave").equals("") ? mDocumentos.get(i).get("clave"): "" );
					docto.setClasificacion("D");
					list.add(docto);
				}
			}
			
			reglasNegocioService = (ReglasNegocioService) contexto.obtenerBean("reglasNegocioBusinessImpl");
			resultado = reglasNegocioService.validarDocumento(list) ;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioAction, M:validarDocumentos");
		}
		
		return resultado;
	}
	
	//Este metodo retorna los datos de documento una vez que fueron validados para cargarlos a un grid, ya no es necesario mandarlos a business
	@DirectMethod
	public List<AcreedorDoctoDto> obtenerDatosValidados(String jsonDatos, String tipo) {
		
		List<AcreedorDoctoDto> list = new ArrayList<>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 185))
			return list;
		
		Gson gson = new Gson();
		List<Map<String, String>> mDocumentos = null;
		
		try{
			
			if(!jsonDatos.equals("")){
				mDocumentos = gson.fromJson(jsonDatos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				for(int i=0; i < mDocumentos.size(); i++){
					AcreedorDoctoDto docto = new AcreedorDoctoDto();
					if(tipo.equals("A")){
						docto.setDeAcre(mDocumentos.get(i).get("deAcre") != null ? mDocumentos.get(i).get("deAcre"): "" );
						docto.setaAcre(mDocumentos.get(i).get("aAcre")   != null ? mDocumentos.get(i).get("aAcre"): "" );
						docto.setTipoAcre(mDocumentos.get(i).get("clave") != null ? mDocumentos.get(i).get("clave"): "" );
						docto.setClasificacion("A");
					}else if(tipo.equals("D")){
						
						//Concatenamos a 10 digitos
						if(mDocumentos.get(i).get("deAcre") != null && !mDocumentos.get(i).get("deAcre").equals("")){
							if(mDocumentos.get(i).get("deAcre").length()<10){
								StringBuffer concat = new StringBuffer();
								for(int j = mDocumentos.get(i).get("deAcre").length();j<10;j++){
									concat.append("0");
								}
								
								docto.setDeAcre(concat.toString() + mDocumentos.get(i).get("deAcre"));
							}else{
								docto.setDeAcre(mDocumentos.get(i).get("deAcre"));
							}
						}else{
							docto.setDeAcre(mDocumentos.get(i).get("deAcre") != null ? mDocumentos.get(i).get("deAcre"): "");
						}
						
						if(mDocumentos.get(i).get("aAcre") != null && !mDocumentos.get(i).get("aAcre").equals("")){
							if(mDocumentos.get(i).get("aAcre").length() < 10){
								StringBuffer concat = new StringBuffer();
								for(int j = mDocumentos.get(i).get("aAcre").length();j<10;j++){
									concat.append("0");
								}
								
								docto.setaAcre(concat.toString() + mDocumentos.get(i).get("aAcre"));
							}else{
								docto.setaAcre(mDocumentos.get(i).get("aAcre"));
							}
						}else{
							docto.setaAcre(mDocumentos.get(i).get("aAcre") != null ? mDocumentos.get(i).get("aAcre"): "");
						}
						
						docto.setTipoAcre(mDocumentos.get(i).get("clave") != null ? mDocumentos.get(i).get("clave"): "" );
						docto.setClasificacion("D");
					}
					
					list.add(docto);
				}
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioAction, M:obtieneDatosValidados");
		}
		
		return list;
	}
	
	@DirectMethod
	public List<AcreedorDoctoDto> obtenerDatosValidadosAcreedores(String jsonDatos,String jsonDatosEliminar) {
		
		List<AcreedorDoctoDto> nvaLista = new ArrayList<>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 185))
			return nvaLista;
		
		Gson gson = new Gson();
		List<Map<String, String>> mAcreedores = null;
		List<Map<String, String>> mAcreedoresEliminar = null;
		
		List<AcreedorDoctoDto> listOriginal = new ArrayList<>();
		List<AcreedorDoctoDto> listEliminar = new ArrayList<>();
		
		try{
			
			if(!jsonDatos.equals("")){
				
				mAcreedores = gson.fromJson(jsonDatos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				for(int i=0; i < mAcreedores.size(); i++){
					AcreedorDoctoDto acre = new AcreedorDoctoDto();
					acre.setDeAcre(mAcreedores.get(i).get("deAcre") != null ? mAcreedores.get(i).get("deAcre"): "" );
					acre.setaAcre(mAcreedores.get(i).get("aAcre")   != null ? mAcreedores.get(i).get("aAcre"): "" );
					acre.setTipoAcre(mAcreedores.get(i).get("clave") != null ? mAcreedores.get(i).get("clave"): "" );
					acre.setClasificacion("A");
					
					listOriginal.add(acre);
				}
				
				if(!jsonDatos.equals("")){
					//jsonEliminar compara con el json original y si coinciden los rangos no se agregan a la lista retornada.
					mAcreedoresEliminar = gson.fromJson(jsonDatosEliminar, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
					
					for(int i=0; i < mAcreedoresEliminar.size(); i++){
						AcreedorDoctoDto acre = new AcreedorDoctoDto();
						acre.setDeAcre(mAcreedoresEliminar.get(i).get("deAcre") != null ? mAcreedoresEliminar.get(i).get("deAcre"): "" );
						acre.setaAcre(mAcreedoresEliminar.get(i).get("aAcre")   != null ? mAcreedoresEliminar.get(i).get("aAcre"): "" );
						
						listEliminar.add(acre);
					}
					
					boolean encontrado = false;
					int idxEncontrado = 0;
					
					for(int i=0; i<listOriginal.size();i++){
						encontrado = false;
						
						if(listEliminar.size()> 0){
							for(int j=0;j<listEliminar.size(); j++){
								if((listOriginal.get(i).getDeAcre().equals(listEliminar.get(j).getDeAcre()))
									&& (listOriginal.get(i).getaAcre().equals(listEliminar.get(j).getaAcre()))){
									
									encontrado = true;
									idxEncontrado = j;
									break;
								}
							}
						}
						
						if(encontrado){
							listEliminar.remove(idxEncontrado);
						}else{
							nvaLista.add(listOriginal.get(i));
						}
						
						//Si ya no hay datos en eliminar, rompemos el ciclo, no tiene caso seguir buscando
						//if(listEliminar.size()== 0) break;
						
					}
					
				}
					
				
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioAction, M:obtenerDatosValidadosAcreedores");
		}	
		
		if(nvaLista.size()>0){
			return nvaLista;
		}else{
			return listOriginal;
		}
	}
	
	@DirectMethod
	public Map<String, String> validarEmpresas(String jsonDatos, int idRegla) {

		Map<String, String> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 185))
			return resultado;
		
		Gson gson = new Gson();
		List<Map<String, String>> mEmpresas = null;
		
		List<EmpresaRegNegDto> list = new ArrayList<>();
		
		try{
			if(!jsonDatos.equals("")){
				mEmpresas = gson.fromJson(jsonDatos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				for(int i=0; i < mEmpresas.size(); i++){
					EmpresaRegNegDto empresa = new EmpresaRegNegDto();
					empresa.setNoEmpresa(Integer.parseInt(mEmpresas.get(i).get("noEmpresa") != null && !mEmpresas.get(i).get("noEmpresa").equals("") ? mEmpresas.get(i).get("noEmpresa"): "0" ));
					empresa.setNombreEmpresa(mEmpresas.get(i).get("nombreEmpresa") != null && !mEmpresas.get(i).get("nombreEmpresa").equals("") ? mEmpresas.get(i).get("nombreEmpresa"): "" );
					empresa.setHoraLimiteOperacion(mEmpresas.get(i).get("horaLimite") != null && !mEmpresas.get(i).get("horaLimite").equals("") ? mEmpresas.get(i).get("horaLimite"): "" );
					list.add(empresa);
				}
			}
			
			reglasNegocioService = (ReglasNegocioService) contexto.obtenerBean("reglasNegocioBusinessImpl");
			resultado = reglasNegocioService.validarEmpresas(list, idRegla, GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioAction, M:validarEmpresas");
		}
		
		return resultado;
	}
	
	@DirectMethod
	public List<EmpresaRegNegDto> obtenerEmpresasValidadas(String jsonDatos) {
		
		
		List<EmpresaRegNegDto> list = new ArrayList<>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 185))
			return list;
		
		Gson gson = new Gson();
		List<Map<String, String>> mEmpresas = null;
		
		try{
			
			if(!jsonDatos.equals("")){
				mEmpresas = gson.fromJson(jsonDatos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				for(int i=0; i < mEmpresas.size(); i++){
					EmpresaRegNegDto empresa = new EmpresaRegNegDto();
					empresa.setNoEmpresa(Integer.parseInt(mEmpresas.get(i).get("noEmpresa") != null && !mEmpresas.get(i).get("noEmpresa").equals("") ? mEmpresas.get(i).get("noEmpresa"): "0" ));
					empresa.setNombreEmpresa(mEmpresas.get(i).get("nombreEmpresa") != null && !mEmpresas.get(i).get("nombreEmpresa").equals("") ? mEmpresas.get(i).get("nombreEmpresa"): "" );
					empresa.setHoraLimiteOperacion(mEmpresas.get(i).get("horaLimite") != null && !mEmpresas.get(i).get("horaLimite").equals("") ? mEmpresas.get(i).get("horaLimite"): "" );
					
					list.add(empresa);
				}
			}
			
			if(list.size()>0){
				//Convertimos el formato de hora a formato 24 horas.
				for(int i = 0; i<list.size(); i++){
					if(list.get(i).getHoraLimiteOperacion().indexOf("p")>0 || list.get(i).getHoraLimiteOperacion().indexOf("P")>0){
						//Si tiene p se incrementan 12 a horas
						String temp = list.get(i).getHoraLimiteOperacion().substring(0,2);
						String cad1 = "";
						if(temp.indexOf(':')>0){
							temp = temp.replace(':', ' ').trim(); //En caso de que sea solo un numero y agarre dos puntos
							cad1 = list.get(i).getHoraLimiteOperacion().substring(1,7);
						}else{
							cad1 = list.get(i).getHoraLimiteOperacion().substring(2,8);
						}
						System.out.println("temp: "+temp);
						System.out.println("cad1: " + cad1);
						//Si es 12 no se incrementa nada, e.j. 12:35:00 p.m se queda igual
						if(Integer.parseInt(temp) != 12){
							int iTemp = Integer.parseInt(temp) + 12;
							list.get(i).setHoraLimiteOperacion(iTemp+cad1);
							System.out.println("nueva cadena: " + (iTemp+cad1));
						}
						
					}else{
						String temp = list.get(i).getHoraLimiteOperacion().substring(0,2);
						if(temp.indexOf(':')>0){
							list.get(i).setHoraLimiteOperacion("0"+list.get(i).getHoraLimiteOperacion().substring(0,7));
						}else{
							list.get(i).setHoraLimiteOperacion(list.get(i).getHoraLimiteOperacion().substring(0,8));
						}
						
						System.out.println("substring: " + list.get(i).getHoraLimiteOperacion().substring(0,8));
					}
						
				}
				
				
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioAction, M:obtenerEmpresasValidadas");
		}
		
		return list;
	}
	
	@DirectMethod
	public Map<String, String> insertarActualizarReglaNegocio(String sReglaNegocio, String sEmpresas, String sAcreDocto, 
													String sDoctoTes, String sCondicionPago, String sDiasContabVen, 
													String sDiaEsp, String sPlanPagos, String sRubros,  
													String tipoOperacion,
													String sEliminarAcre, String sEliminarDoctos, String sEliminarClase,
													String sEliminarTes,String sEliminarFechas,
													boolean eliminaDiasContabVenc, boolean eliminaDiasAdicionales,
													boolean eliminaDiasespecificos, boolean eliminaPlanPagos,
													boolean simulador){
		
		Map<String,String> result= new HashMap<String,String>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 185))
			return result;
		
		Gson gson = new Gson();
		List<Map<String, String>> mReglaNegocio = null;
		List<Map<String, String>> mEmpresas = null;
		List<Map<String, String>> mAcreDocto = null;
		List<Map<String, String>> mDoctoTes = null;
		List<Map<String, String>> mCondicionPago = null;
		List<Map<String, String>> mDiasContabVen = null;
		List<Map<String, String>> mDiaEsp = null;
		List<Map<String, String>> mPlanPagos = null;
		List<Map<String, String>> mRubros = null;
		//ELIMINAR
		List<Map<String, String>> mEliminarAcre = null;
		List<Map<String, String>> mEliminarDoctos = null;
		List<Map<String, String>> mEliminarClase = null;
		List<Map<String, String>> mEliminarTes = null;
		List<Map<String, String>> mEliminarFechas = null;
		
		ReglasNegocioDto reglaNegocio = new ReglasNegocioDto();
		List<EmpresaRegNegDto> listEmpresas = new ArrayList<>();		
		List<AcreedorDoctoDto> listAcreDocto = new ArrayList<>();
		List<DoctoTesoreriaDto> listDoctoTes = new ArrayList<>();
		CondicionPagoDto condicionPago = new CondicionPagoDto();
		List<PlanPagosDto> listPlanPagos = new ArrayList<>();
		List<RubroRegNeg> listRubros = new ArrayList<>();
		
		//LISTAS PARA ELIMINACION
		List<AcreedorDoctoDto> lEliminarAcreDocto = new ArrayList<>();
		List<DoctoTesoreriaDto> lEliminarDoctoTes = new ArrayList<>();
		List<PlanPagosDto> lEliminarFechas = new ArrayList<>();
		
		
		//REGLA NEGOCIO (Solo un registro)
		if(!sReglaNegocio.equals("")){
			mReglaNegocio = gson.fromJson(sReglaNegocio, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			
			reglaNegocio.setIdRegla(Integer.parseInt(mReglaNegocio.get(0).get("idRegla") != null && !mReglaNegocio.get(0).get("idRegla").equals("") ? mReglaNegocio.get(0).get("idRegla"): "0") );
			reglaNegocio.setReglaNegocio(mReglaNegocio.get(0).get("reglaNegocio") != null ? mReglaNegocio.get(0).get("reglaNegocio"): "");
			reglaNegocio.setTipoRegla(mReglaNegocio.get(0).get("tipoRegla") != null ? mReglaNegocio.get(0).get("tipoRegla"): "");
			
			if(reglaNegocio.getTipoRegla().length() > 1){
				reglaNegocio.setTipoRegla(reglaNegocio.getTipoRegla().substring(0, 1));
			}
			
			reglaNegocio.setFechaCaptura(mReglaNegocio.get(0).get("fechaCaptura") != null ? mReglaNegocio.get(0).get("fechaCaptura"): "");
			
			//reglaNegocio.setUsuarioCaptura(Integer.parseInt(mReglaNegocio.get(0).get("usuarioCaptura") != null ? mReglaNegocio.get(0).get("usuarioCaptura"): "-1"));
			reglaNegocio.setUsuarioCaptura(GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
			reglaNegocio.setGeneraPropuestaAutomatica(mReglaNegocio.get(0).get("generaPropuestaAutomatica") != null ? mReglaNegocio.get(0).get("generaPropuestaAutomatica"): "");
			reglaNegocio.setUsuarios((mReglaNegocio.get(0).get("usuarios") != null ? mReglaNegocio.get(0).get("usuarios"): "").trim());
			reglaNegocio.setIndicador(mReglaNegocio.get(0).get("indicador") != null ? mReglaNegocio.get(0).get("indicador"): "");
			reglaNegocio.setDescuento(mReglaNegocio.get(0).get("descuento") != null ? mReglaNegocio.get(0).get("descuento"): "");
			reglaNegocio.setLongChequera(Integer.parseInt(mReglaNegocio.get(0).get("longChequera") != null && !mReglaNegocio.get(0).get("longChequera").equals("")?mReglaNegocio.get(0).get("longChequera"):"0"));
		}
		
		if(!sEmpresas.equals("")){
			mEmpresas = gson.fromJson(sEmpresas, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			for(int i=0; i<mEmpresas.size(); i++){
				EmpresaRegNegDto empresa = new EmpresaRegNegDto();
				empresa.setNoEmpresa(Integer.parseInt(mEmpresas.get(i).get("noEmpresa") != null ? mEmpresas.get(i).get("noEmpresa"): "0"));
				empresa.setNombreEmpresa(mEmpresas.get(i).get("nombreEmpresa") != null ? mEmpresas.get(i).get("nombreEmpresa"): "");
				empresa.setHoraLimiteOperacion(mEmpresas.get(i).get("horaLimiteOperacion") != null ? mEmpresas.get(i).get("horaLimiteOperacion"): "");
				listEmpresas.add(empresa);
			}
		}
		
		if(!sAcreDocto.equals("")){
			mAcreDocto = gson.fromJson(sAcreDocto, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			for(int i = 0; i < mAcreDocto.size(); i++){
				AcreedorDoctoDto acreDocto = new AcreedorDoctoDto();
				acreDocto.setIdAcreDocto(Integer.parseInt(mAcreDocto.get(i).get("idAcreDocto") != null && !mAcreDocto.get(i).get("idAcreDocto").equals("") ? mAcreDocto.get(i).get("idAcreDocto"): "0"));
				acreDocto.setDeAcre(((mAcreDocto.get(i).get("deAcre") != null && !mAcreDocto.get(i).get("deAcre").equals("")) ? mAcreDocto.get(i).get("deAcre"): "").trim());
				acreDocto.setaAcre(((mAcreDocto.get(i).get("aAcre") != null && !mAcreDocto.get(i).get("aAcre").equals("")) ? mAcreDocto.get(i).get("aAcre"): "").trim());
				acreDocto.setClasificacion(mAcreDocto.get(i).get("clasificacion") != null ? mAcreDocto.get(i).get("clasificacion"): "");
				acreDocto.setTipoAcre(mAcreDocto.get(i).get("tipoAcre") != null ? mAcreDocto.get(i).get("tipoAcre"): "");

				listAcreDocto.add(acreDocto);
			}
		}
		
		if(!sDoctoTes.equals("")){
			mDoctoTes = gson.fromJson(sDoctoTes, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			for(int i = 0; i < mDoctoTes.size(); i++){
				DoctoTesoreriaDto doctoTes = new DoctoTesoreriaDto();
				doctoTes.setIdDoctoTes(Integer.parseInt(mDoctoTes.get(i).get("idDoctoTes") != null && !mDoctoTes.get(i).get("idDoctoTes").equals("") ? mDoctoTes.get(i).get("idDoctoTes"): "0"));
				doctoTes.setIncluir((mDoctoTes.get(i).get("incluir") != null ? mDoctoTes.get(i).get("incluir"): "").trim());
				doctoTes.setExcluir((mDoctoTes.get(i).get("excluir") != null ? mDoctoTes.get(i).get("excluir"): "").trim());
				doctoTes.setClasificacion(mDoctoTes.get(i).get("clasificacion") != null ? mDoctoTes.get(i).get("clasificacion"): "");
				
				listDoctoTes.add(doctoTes);
			}
		}
		
		if(!sCondicionPago.equals("")){
			mCondicionPago = gson.fromJson(sCondicionPago, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			condicionPago.setIdCondicionPago(Integer.parseInt(mCondicionPago.get(0).get("idCondicionPago") != null && !mCondicionPago.get(0).get("idCondicionPago").equals("") ? mCondicionPago.get(0).get("idCondicionPago"): "0"));
			condicionPago.setFechaBase(mCondicionPago.get(0).get("fechaBase") != null ? mCondicionPago.get(0).get("fechaBase"): "");
			condicionPago.setClaseDia(mCondicionPago.get(0).get("claseDia") != null ? mCondicionPago.get(0).get("claseDia"): "");
			condicionPago.setAccionDia(mCondicionPago.get(0).get("accionDia") != null ? mCondicionPago.get(0).get("accionDia"): "");
			if(mCondicionPago.get(0).get("aplicaDiasAdicionales").equals("true")){
				condicionPago.setDiasAdicionales(Integer.parseInt(mCondicionPago.get(0).get("diasAdicionales") != null && !mCondicionPago.get(0).get("diasAdicionales").equals("")? mCondicionPago.get(0).get("diasAdicionales"): "0"));
				condicionPago.setAplicaDiasAdicionales(true);
			}else{
				condicionPago.setAplicaDiasAdicionales(false);
				condicionPago.setDiasAdicionales(0);
			}
			
			condicionPago.setFinMes(mCondicionPago.get(0).get("finMes") != null ? mCondicionPago.get(0).get("finMes"): "");	
			condicionPago.setBancoInterlocutor((mCondicionPago.get(0).get("bancoInterlocutor") != null ? mCondicionPago.get(0).get("bancoInterlocutor"): "").trim());
		}
		
		if(!sDiasContabVen.equals("")){
			mDiasContabVen = gson.fromJson(sDiasContabVen, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			condicionPago.setIdContab(Integer.parseInt(mDiasContabVen.get(0).get("idContab") != null && !mDiasContabVen.get(0).get("idContab").equals("") ? mDiasContabVen.get(0).get("idContab") : "0"));
			if(mDiasContabVen.get(0).get("aplicaContab").equals("true")){
				condicionPago.setAplicaContab(true);
				condicionPago.setDiaPago1(mDiasContabVen.get(0).get("diaPago1") != null ? mDiasContabVen.get(0).get("diaPago1") : "");
				condicionPago.setDiaPago1(nombreDiaPagoToLetra(condicionPago.getDiaPago1()));
				condicionPago.setDiaPago2(mDiasContabVen.get(0).get("diaPago2") != null ? mDiasContabVen.get(0).get("diaPago2") : "");
				condicionPago.setDiaPago2(nombreDiaPagoToLetra(condicionPago.getDiaPago2()));
				condicionPago.setDiaPago3(mDiasContabVen.get(0).get("diaPago3") != null ? mDiasContabVen.get(0).get("diaPago3") : "");
				condicionPago.setDiaPago3(nombreDiaPagoToLetra(condicionPago.getDiaPago3()));
				condicionPago.setDiaPago4(mDiasContabVen.get(0).get("diaPago4") != null ? mDiasContabVen.get(0).get("diaPago4") : "");
				condicionPago.setDiaPago4(nombreDiaPagoToLetra(condicionPago.getDiaPago4()));
				condicionPago.setDiaPago5(mDiasContabVen.get(0).get("diaPago5") != null ? mDiasContabVen.get(0).get("diaPago5") : "");
				condicionPago.setDiaPago5(nombreDiaPagoToLetra(condicionPago.getDiaPago5()));
				condicionPago.setDiaPago6(mDiasContabVen.get(0).get("diaPago6") != null ? mDiasContabVen.get(0).get("diaPago6") : "");
				condicionPago.setDiaPago6(nombreDiaPagoToLetra(condicionPago.getDiaPago6()));
				condicionPago.setDiaPago7(mDiasContabVen.get(0).get("diaPago7") != null ? mDiasContabVen.get(0).get("diaPago7") : "");
				condicionPago.setDiaPago7(nombreDiaPagoToLetra(condicionPago.getDiaPago7()));
				condicionPago.setSemana1(mDiasContabVen.get(0).get("semana1") != null ? mDiasContabVen.get(0).get("semana1") : "");
				condicionPago.setSemana1(semanaPagoToLetra(condicionPago.getSemana1()));
				condicionPago.setSemana2(mDiasContabVen.get(0).get("semana2") != null ? mDiasContabVen.get(0).get("semana2") : "");
				condicionPago.setSemana2(semanaPagoToLetra(condicionPago.getSemana2()));
				condicionPago.setSemana3(mDiasContabVen.get(0).get("semana3") != null ? mDiasContabVen.get(0).get("semana3") : "");
				condicionPago.setSemana3(semanaPagoToLetra(condicionPago.getSemana3()));
				condicionPago.setSemana4(mDiasContabVen.get(0).get("semana4") != null ? mDiasContabVen.get(0).get("semana4") : "");
				condicionPago.setSemana4(semanaPagoToLetra(condicionPago.getSemana4()));
				condicionPago.setSemana5(mDiasContabVen.get(0).get("semana5") != null ? mDiasContabVen.get(0).get("semana5") : "");
				condicionPago.setSemana5(semanaPagoToLetra(condicionPago.getSemana5()));
				condicionPago.setSemana6(mDiasContabVen.get(0).get("semana6") != null ? mDiasContabVen.get(0).get("semana6") : "");
				condicionPago.setSemana6(semanaPagoToLetra(condicionPago.getSemana6()));
				condicionPago.setSemana7(mDiasContabVen.get(0).get("semana7") != null ? mDiasContabVen.get(0).get("semana7") : "");
				condicionPago.setSemana7(semanaPagoToLetra(condicionPago.getSemana7()));
			}else{
				condicionPago.setAplicaContab(false);
			}
		}
		
		if(!sDiaEsp.equals("")){
			mDiaEsp = gson.fromJson(sDiaEsp, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			condicionPago.setIdDiasExcep(Integer.parseInt(mDiaEsp.get(0).get("idDiasExcep") != null && !mDiaEsp.get(0).get("idDiasExcep").equals("") ? mDiaEsp.get(0).get("idDiasExcep") : "0" ));
			if(mDiaEsp.get(0).get("aplicaDiasExcep").equals("true")){
				
				condicionPago.setDiaEspecifico1(Integer.parseInt(mDiaEsp.get(0).get("diaEspecifico1") != null && !mDiaEsp.get(0).get("diaEspecifico1").equals("")? mDiaEsp.get(0).get("diaEspecifico1") : "0" ));
				condicionPago.setDiaExcep1(mDiaEsp.get(0).get("diaExcep1") != null ? mDiaEsp.get(0).get("diaExcep1") : "" );
				condicionPago.setDiaPagoExcep1(mDiaEsp.get(0).get("diaPagoExcep1") != null ? mDiaEsp.get(0).get("diaPagoExcep1") : "" );
				condicionPago.setRangoDiaDesde1(Integer.parseInt(mDiaEsp.get(0).get("rangoDiaDesde1") != null && !mDiaEsp.get(0).get("rangoDiaDesde1").equals("") ? mDiaEsp.get(0).get("rangoDiaDesde1") : "0" ));
				condicionPago.setRangoDiaHasta1(Integer.parseInt(mDiaEsp.get(0).get("rangoDiaHasta1") != null && !mDiaEsp.get(0).get("rangoDiaHasta1").equals("")? mDiaEsp.get(0).get("rangoDiaHasta1") : "0" ));
				
				condicionPago.setDiaEspecifico2(Integer.parseInt(mDiaEsp.get(0).get("diaEspecifico2") != null && !mDiaEsp.get(0).get("diaEspecifico2").equals("")? mDiaEsp.get(0).get("diaEspecifico2") : "0" ));
				condicionPago.setDiaExcep2(mDiaEsp.get(0).get("diaExcep2") != null ? mDiaEsp.get(0).get("diaExcep2") : "" );
				condicionPago.setDiaPagoExcep2(mDiaEsp.get(0).get("diaPagoExcep2") != null ? mDiaEsp.get(0).get("diaPagoExcep2") : "" );
				condicionPago.setRangoDiaDesde2(Integer.parseInt(mDiaEsp.get(0).get("rangoDiaDesde2") != null && !mDiaEsp.get(0).get("rangoDiaDesde2").equals("")? mDiaEsp.get(0).get("rangoDiaDesde2") : "0" ));
				condicionPago.setRangoDiaHasta2(Integer.parseInt(mDiaEsp.get(0).get("rangoDiaHasta2") != null && !mDiaEsp.get(0).get("rangoDiaHasta2").equals("")? mDiaEsp.get(0).get("rangoDiaHasta2") : "0" ));
				condicionPago.setAplicaDiasExcep(true);
			}else{
				condicionPago.setAplicaDiasExcep(false);
			}
			
		}
		
		if(!sPlanPagos.equals("")){
			mPlanPagos = gson.fromJson(sPlanPagos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			for(int i=0; i<mPlanPagos.size(); i++){
				PlanPagosDto planPagos = new PlanPagosDto();
				planPagos.setIdPlan(Integer.parseInt(mPlanPagos.get(i).get("idPlan") != null && !mPlanPagos.get(i).get("idPlan").equals("") ? mPlanPagos.get(i).get("idPlan") : "0"  ));
				planPagos.setFecha(mPlanPagos.get(i).get("fecha") != null ? mPlanPagos.get(i).get("fecha") : "");
				listPlanPagos.add(planPagos);
			}
		}
		
		if(!sRubros.equals("")){
			mRubros = gson.fromJson(sRubros, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			for(int i=0; i<mRubros.size(); i++){
				RubroRegNeg rubro = new RubroRegNeg();
				rubro.setIdRubro(mRubros.get(i).get("idRubro") != null ? mRubros.get(i).get("idRubro"): "0");
				rubro.setDescRubro(mRubros.get(i).get("descRubro") != null ? mRubros.get(i).get("descRubro"): "");
				listRubros.add(rubro);
			}
		}
		
		//ELIMINACIONES
		if(!sEliminarAcre.equals("")){
			mEliminarAcre = gson.fromJson(sEliminarAcre, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			for(int i=0; i<mEliminarAcre.size(); i++){
				AcreedorDoctoDto acre = new AcreedorDoctoDto();
				acre.setIdAcreDocto(Integer.parseInt(mEliminarAcre.get(i).get("idAcreDocto") != null && !mEliminarAcre.get(i).get("idAcreDocto").equals("") ? mEliminarAcre.get(i).get("idAcreDocto") : "0"));
				lEliminarAcreDocto.add(acre);
			}
		}
		
		if(!sEliminarDoctos.equals("")){
			mEliminarDoctos = gson.fromJson(sEliminarDoctos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			for(int i=0; i<mEliminarDoctos.size(); i++){
				AcreedorDoctoDto docto = new AcreedorDoctoDto();
				docto.setIdAcreDocto(Integer.parseInt(mEliminarDoctos.get(i).get("idAcreDocto") != null && !mEliminarDoctos.get(i).get("idAcreDocto").equals("") ? mEliminarDoctos.get(i).get("idAcreDocto") : "0"));
				lEliminarAcreDocto.add(docto);
			}
		}
		
		if(!sEliminarClase.equals("")){
			mEliminarClase = gson.fromJson(sEliminarClase, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			for(int i=0; i<mEliminarClase.size(); i++){
				DoctoTesoreriaDto clase = new DoctoTesoreriaDto();
				clase.setIdDoctoTes(Integer.parseInt(mEliminarClase.get(i).get("idDoctoTes") != null && !mEliminarClase.get(i).get("idDoctoTes").equals("") ? mEliminarClase.get(i).get("idDoctoTes") : "0"));
				lEliminarDoctoTes.add(clase);
			}
		}
		
		if(!sEliminarTes.equals("")){
			mEliminarTes = gson.fromJson(sEliminarTes, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			for(int i=0; i<mEliminarTes.size(); i++){
				DoctoTesoreriaDto tes = new DoctoTesoreriaDto();
				tes.setIdDoctoTes(Integer.parseInt(mEliminarTes.get(i).get("idDoctoTes") != null && !mEliminarTes.get(i).get("idDoctoTes").equals("") ? mEliminarTes.get(i).get("idDoctoTes") : "0"));
				lEliminarDoctoTes.add(tes);
			}
		}
		
		if(!sEliminarFechas.equals("")){
			mEliminarFechas = gson.fromJson(sEliminarFechas, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			for(int i=0; i<mEliminarFechas.size(); i++){
				PlanPagosDto fecha = new PlanPagosDto();
				fecha.setIdPlan(Integer.parseInt(mEliminarFechas.get(i).get("idPlan") != null && !mEliminarFechas.get(i).get("idPlan").equals("") ? mEliminarFechas.get(i).get("idPlan") : "0"));
				lEliminarFechas.add(fecha);
			}
		}
		
		try{
			reglasNegocioService = (ReglasNegocioService) contexto.obtenerBean("reglasNegocioBusinessImpl");
			result = reglasNegocioService.insertarActualizarReglaNegocio(reglaNegocio, listEmpresas, listAcreDocto, 
					listDoctoTes, condicionPago, listPlanPagos, listRubros, tipoOperacion,
					lEliminarAcreDocto, lEliminarDoctoTes, lEliminarFechas, 
					eliminaDiasContabVenc, eliminaDiasAdicionales,
					eliminaDiasespecificos, eliminaPlanPagos, simulador);

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioAction, M:insertarActualizarReglaNegocio");
		}
		
		return result;
	}
	
	@DirectMethod
	public Map<String,String> eliminarReglaNegocio(int idRegla) {

		Map<String,String> resultado = new HashMap<>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 185))
			return resultado;
		
		try{
			reglasNegocioService = (ReglasNegocioService) contexto.obtenerBean("reglasNegocioBusinessImpl");
			resultado = reglasNegocioService.eliminarReglaNegocio(idRegla) ;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioAction, M:eliminarReglaNegocio");
		}
		
		return resultado;
	}
	
	private String nombreDiaPagoToLetra(String diaPago){
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 185))
			return "";
		 
		if(diaPago.equalsIgnoreCase("LUNES")){
			return "LU";
		}else if(diaPago.equalsIgnoreCase("MARTES")){
			return "MA";
		}else if(diaPago.equalsIgnoreCase("MI�RCOLES") || diaPago.equalsIgnoreCase("MIERCOLES")){
			return "MI";
		}else if(diaPago.equalsIgnoreCase("JUEVES")){
			return "JU";
		}else if(diaPago.equalsIgnoreCase("VIERNES")){
			return "VI";
		}else if(diaPago.equalsIgnoreCase("S�BADO") || diaPago.equalsIgnoreCase("SABADO")){
			return "SA";
		}else if(diaPago.equalsIgnoreCase("DOMINGO")){
			return "DO";
		}
		
		return diaPago;
	}
	
	private String semanaPagoToLetra(String semana){
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 185))
			return "";
		
		if(semana.equalsIgnoreCase("PR�XIMA SEMANA")){
			return "P";
		}else if(semana.equalsIgnoreCase("SEMANA ACTUAL")){
			return "A";
		}
		return semana;
	}

	
	@DirectMethod
	public List<RubroRegNeg> obtenerRubros(boolean bAsignados, int idRegla) {

		List<RubroRegNeg> list = new ArrayList<RubroRegNeg>();
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 185))
			return list;
		
		try{
			reglasNegocioService = (ReglasNegocioService) contexto.obtenerBean("reglasNegocioBusinessImpl");
			list = reglasNegocioService.obtenerRubros(bAsignados, idRegla) ;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioAction, M:obtenerRubros");
		}
		
		return list;
	}
	
	@DirectMethod
	public String exportaExcel(String datos) {
		String resp = "";
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 185))
			return resp;
		
		try {
			reglasNegocioService = (ReglasNegocioService) contexto.obtenerBean("reglasNegocioBusinessImpl");
			resp = reglasNegocioService.exportaExcel(datos);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Reportes, C:ReglasNegocioAction, M:exportaExcel");
		}
		return resp;
	}
	
	@DirectMethod
	public HSSFWorkbook obtenerExcel(String nombre) {
		FileInputStream file = null;
		HSSFWorkbook workbook = null;
		File arch = new File(nombre);
		
		try {
			file = new FileInputStream(arch);
			workbook = new HSSFWorkbook(file);
			file.close();
		} catch (FileNotFoundException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:ReglasNegociosAction, M:obtenerExcel");
		} catch (IOException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:ReglasNegociosAction, M:obtenerExcel");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:ReglasNegociosAction, M:obtenerExcel");
		}
		arch.delete();
		return workbook;
	}
	
	
	@DirectMethod 
	public String procesaSimulador(int idRegla){
		
		String folios = "";
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 185))
			return folios;
		
		try{
			reglasNegocioService = (ReglasNegocioService) contexto.obtenerBean("reglasNegocioBusinessImpl");
			folios = reglasNegocioService.procesaSimulador(idRegla);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioAction, M:procesaSimulador");
		}
		
		return folios;
	}
	
		//AGREGADO EMS 03/02/2016 - ES UNA COPIA DE BITACORA, SOLO QUE AQUI CARGA LA TABLAS DEL SIMULADOR
		@DirectMethod
		public List<SeleccionAutomaticaGrupoDto>consultarSeleccionAutomaticaSIM(int idGrupoEmpresa, String idProv,int idGrupoRubro, 
				String fecIni, String fecFin, String soloMisProp, String propVigentes, boolean todasPro,
				boolean divMN, boolean divDLS, boolean divEUR, boolean divOtras, String tipoRegla, String reglaNegocio){
			
			List<SeleccionAutomaticaGrupoDto>list= new ArrayList<SeleccionAutomaticaGrupoDto>();
			
			if(!Utilerias.haveSession(WebContextManager.get())
					|| !Utilerias.tienePermiso(WebContextManager.get(), 185))
				return list;
			
			
			//ParamConsultaPropuestasDto dtoIn= new ParamConsultaPropuestasDto();
			ConsultaPropuestasDto dtoIn = new ConsultaPropuestasDto();
			
			try{
				
				reglasNegocioService = (ReglasNegocioService) contexto.obtenerBean("reglasNegocioBusinessImpl");
				
				dtoIn.setGrupoEmpresa(idGrupoEmpresa);
				dtoIn.setIdCliente(idProv);
				dtoIn.setPvGrupoRubro(idGrupoRubro);
				dtoIn.setIdUsuario(GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
				dtoIn.setFechaIni(fecIni!=null && !fecIni.equals("")?fecIni:"");
				dtoIn.setFechaFin(fecFin!=null && !fecFin.equals("")?fecFin:"");
				dtoIn.setPvsDivision(""); //Vacio porque no se muestra en pantalla
				if(soloMisProp!=null && soloMisProp.equals("true"))
					dtoIn.setSoloMisProp(true);
				else
					dtoIn.setSoloMisProp(false);
				
				if(propVigentes!=null && propVigentes.equals("true"))
					dtoIn.setSoloPropAct(true);
				else
					dtoIn.setSoloPropAct(false);
				
				dtoIn.setTodasPropuestas(todasPro);
				
				dtoIn.setDivMN(divMN);
				dtoIn.setDivDLS(divDLS);
				dtoIn.setDivEUR(divEUR);
				dtoIn.setDivOtras(divOtras);
				dtoIn.setTipoRegla(tipoRegla);
				dtoIn.setReglaNegocio(reglaNegocio);
				
				list = reglasNegocioService.consultarSeleccionAutomaticaSIM(dtoIn);
				 
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:Egresos, C:ConsultaPropuestasAction, M:consultarSeleccionAutomatica");	
			}
			return list;
		}
		
		//Consulta los detalles de un propuesta del simulador
		@DirectMethod
		public List<MovimientoDto>consultarDetalle(int idGrupoEmpresa, int idGrupo, String cveControl, int usr1, int usr2, int usr3){
			SeleccionAutomaticaGrupoDto dtoIn= new SeleccionAutomaticaGrupoDto();
			List<MovimientoDto>listDetalle= new ArrayList<MovimientoDto>();
			
			if(!Utilerias.haveSession(WebContextManager.get())
					|| !Utilerias.tienePermiso(WebContextManager.get(), 185))
				return listDetalle;
			
			try{
				reglasNegocioService = (ReglasNegocioService) contexto.obtenerBean("reglasNegocioBusinessImpl");
				dtoIn.setIdGrupoFlujo(idGrupoEmpresa);
				dtoIn.setIdGrupo(idGrupo);
				dtoIn.setCveControl(cveControl);
				dtoIn.setUsuarioUno(usr1);
				dtoIn.setUsuarioDos(usr2);
				dtoIn.setUsuarioTres(usr3);
				listDetalle = reglasNegocioService.consultarDetalle(dtoIn);
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:Utilerias, C:ReglasNegociosAction, M:consultarDetalle");	
			}
			return listDetalle;
		}
		
		
		@DirectMethod 
		public Map<String, Object> actualizarMovtosSimulador(int idRegla){
			
			Map<String, Object> result = new HashMap<>();
			result.put("error", "");
			result.put("msgUsuario", "");
			
			if(!Utilerias.haveSession(WebContextManager.get())
					|| !Utilerias.tienePermiso(WebContextManager.get(), 185)){
				result.put("error", "Error en sesi�n de usuario.");
				return result;
			}
				
			
			try{
				reglasNegocioService = (ReglasNegocioService) contexto.obtenerBean("reglasNegocioBusinessImpl");
				result = reglasNegocioService.actualizarMovtosSimulador(idRegla);
				
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Utilerias, C:ReglasNegocioAction, M:actualizarMovtosSimulador");
			}
			
			return result;
		}
		
		@DirectMethod 
		public Map<String, Object> deshacerCambiosSimulador(){
			
			Map<String, Object> result = new HashMap<>();
			result.put("error", "");
			result.put("msgUsuario", "");
			
			if(!Utilerias.haveSession(WebContextManager.get())
					|| !Utilerias.tienePermiso(WebContextManager.get(), 185)){
				result.put("error", "Error en sesi�n de usuario.");
				return result;
			}
				
			
			try{
				reglasNegocioService = (ReglasNegocioService) contexto.obtenerBean("reglasNegocioBusinessImpl");
				result = reglasNegocioService.deshacerCambiosSimulador();
				
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Utilerias, C:ReglasNegocioAction, M:deshacerCambiosSimulador");
			}
			
			return result;
		}
		
		//EMS 14/02/2016 - Valida si un usuario esta en linea utilizando el simulador.
		//No se puede usar el simulador por dos usuarios al mismo tiempo ya que pueden 
		//chocar los movimientos que sean asignados  a una propuesta.
		@DirectMethod 
		public Map<String, Object> loginSimulador(){
			
			Map<String, Object> result = new HashMap<>();
			result.put("error", "");
			
			if(!Utilerias.haveSession(WebContextManager.get())
					|| !Utilerias.tienePermiso(WebContextManager.get(), 185)){
				result.put("error", "Error en sesi�n de usuario.");
				return result;
			}
				
			
			try{
				reglasNegocioService = (ReglasNegocioService) contexto.obtenerBean("reglasNegocioBusinessImpl");
				result = reglasNegocioService.loginSimulador();
				
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Utilerias, C:ReglasNegocioAction, M:loginSimulador");
			}
			
			return result;
		}
		
		
		@DirectMethod 
		public Map<String, Object> insertarLoginSimulador(){
			
			Map<String, Object> result = new HashMap<>();
			result.put("error", "");
			
			if(!Utilerias.haveSession(WebContextManager.get())
					|| !Utilerias.tienePermiso(WebContextManager.get(), 185)){
				result.put("error", "Error en sesi�n de usuario.");
				return result;
			}
				
			
			try{
				reglasNegocioService = (ReglasNegocioService) contexto.obtenerBean("reglasNegocioBusinessImpl");
				result = reglasNegocioService.insertarLoginSimulador();
				
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Utilerias, C:ReglasNegocioAction, M:insertarLoginSimulador");
			}
			
			return result;
		}
		
		@DirectMethod 
		public Map<String, Object> eliminaLogueoUsuario(){
			
			Map<String, Object> result = new HashMap<>();
			result.put("error", "");
			
			if(!Utilerias.haveSession(WebContextManager.get())
					|| !Utilerias.tienePermiso(WebContextManager.get(), 185)){
				result.put("error", "Error en sesi�n de usuario.");
				return result;
			}
				
			try{
				reglasNegocioService = (ReglasNegocioService) contexto.obtenerBean("reglasNegocioBusinessImpl");
				result = reglasNegocioService.eliminaLogueoUsuario();
				
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Utilerias, C:ReglasNegocioAction, M:eliminaLogueoUsuario");
			}
			
			return result;
		}
		
		
}
