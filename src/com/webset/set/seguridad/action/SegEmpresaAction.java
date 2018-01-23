package com.webset.set.seguridad.action;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
/**
 * @author Cristian Garcia Garcia
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.seguridad.business.EmpresaBusiness;
import com.webset.set.seguridad.business.SegUsuarioBusiness;
import com.webset.set.seguridad.dto.ComboUsuario;
import com.webset.set.seguridad.dto.EmpresaDto;
import com.webset.set.seguridad.dto.UsuarioEmpresaDto;
import com.webset.set.seguridad.exceptions.BusinessException;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.service.MantenimientoLeyendasService;
import com.webset.utils.tools.Utilerias;
 
public class SegEmpresaAction {
	
	Contexto contexto=new Contexto();
	Bitacora bitacora=new Bitacora();
	
	/*
	 * Editado por Luis Serrato 04092015
	private static class ComboUsuario {
		public int idUsuario;
		public String nombre;
		public String claveUsuario;

		private ComboUsuario(int idUsuario,String claveUsuario, String nombre) {
			this.idUsuario = idUsuario;
			this.claveUsuario= claveUsuario;
			this.nombre = nombre;
		}
	}
	*/
	
	class EmpresaD{
		private int noEmpresa;
		private String nombreEmpresa;
		private EmpresaD(int noEmpresa, String nombreEmpresa)
		{
			this.noEmpresa= noEmpresa;
			this.nombreEmpresa= nombreEmpresa;
		}
		public int getNoEmpresa() {
			return noEmpresa;
		}
		public void setNoEmpresa(int noEmpresa) {
			this.noEmpresa = noEmpresa;
		}
		public String getNombreEmpresa() {
			return nombreEmpresa;
		}
		public void setNombreEmpresa(String nombreEmpresa) {
			this.nombreEmpresa = nombreEmpresa;
		}
		
		
	}
	/**
	 * Metodo para obtener usuarios y el llenado de los combos
	 * @return Lista de tipo comboUsuarios
	 * Editado por Luis Serrato 04092015
	 * Este metodo no se usa
	 */
	@DirectMethod
	public List<ComboUsuario> obtenerUsuarios() {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 20))
			return null;
		List<ComboUsuario> usuarios = new ArrayList<ComboUsuario>();
		
		try{
			SegUsuarioBusiness segUsuarioBusiness = (SegUsuarioBusiness)contexto.obtenerBean("segUsuarioBusiness");
			usuarios = segUsuarioBusiness.consultar();
	
		}catch(Exception e){
				System.out.println("Cayo en excepcion");
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Seguridad, C:SegPersonasAction, M:obtenerUsuarios");
		}
		return usuarios;
		
	}
	//Metodo para consultar las empresas que tiene cada usurio
	@DirectMethod
	public List<EmpresaD> obtenerEmpresas(int intIdUsuario, boolean bExiste) {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 20))
			return null;
		List<EmpresaDto> listEmpresa = new ArrayList<EmpresaDto>();
		List<EmpresaD> listEmpresaD = new ArrayList<EmpresaD>();
		try{
			EmpresaBusiness empresaBusiness = (EmpresaBusiness) contexto.obtenerBean("empresaBusiness");
			listEmpresa = empresaBusiness.obtenerListaEmpresas(intIdUsuario, bExiste) ;
			ListIterator<EmpresaDto> itr = listEmpresa.listIterator();
			
			while(itr.hasNext()){
				EmpresaDto empresaTmp = itr.next();
			    listEmpresaD.add(new EmpresaD(empresaTmp.getNoEmpresa(),empresaTmp.getNomEmpresa()));
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegEmpresaAction, M:obtenerEmpresas");
		}
		
		return listEmpresaD;
	}
	
	//Metodo  para asignar empresas a los usuarios
	@DirectMethod
	public boolean asignarEmpresas(String claves)
	{
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 20))
			return false;
		
		int ind=0,noUsuario=0, noEmpresa=0, res=0;
		boolean todos=false;
		UsuarioEmpresaDto dtoUsuarioEmpresa= new UsuarioEmpresaDto();
		try {
		EmpresaBusiness empresaBusiness = (EmpresaBusiness) contexto.obtenerBean("empresaBusiness");	
		ind=claves.indexOf(",");
		if(ind>0){
			noUsuario=Integer.parseInt(claves.substring(0,ind));
			noEmpresa=Integer.parseInt(claves.substring(ind+1));
			todos=false;
			}
		else{
			noUsuario=Integer.parseInt(claves.substring(0));
			todos=true;
		}
		
		dtoUsuarioEmpresa.setNoUsuario(noUsuario);
		dtoUsuarioEmpresa.setNoEmpresa(noEmpresa);
		
		
		res=empresaBusiness.insertar(dtoUsuarioEmpresa, todos);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegEmpresaAction, M:asignarEmpresas");
		}
		if(res>0)
			return true;
		else 
			return false;
	}
	
	/**
	 * Metodo para eliminar empresa de usuario_empresa
	 * @param claves
	 * @return true o false segun la respuesta de la consulta
	 * @throws BusinessException
	 * @throws Exception
	 */
	@DirectMethod
	public boolean eliminarEmpresas(String claves)  
	{
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 20))
			return false;
		int ind=0, noUsuario=0, noEmpresa=0, res=0;
		boolean todos=false;
		EmpresaBusiness empresaBusiness = (EmpresaBusiness) contexto.obtenerBean("empresaBusiness");
		UsuarioEmpresaDto dtoUsuarioEmpresa= new UsuarioEmpresaDto();
		try{
		ind=claves.indexOf(",");
		if(ind>0){
			noUsuario=Integer.parseInt(claves.substring(0,ind));
			noEmpresa=Integer.parseInt(claves.substring(ind+1));
			todos=false;
			}
		else{
			noUsuario=Integer.parseInt(claves.substring(0));
			todos=true;
		}
		dtoUsuarioEmpresa.setNoUsuario(noUsuario);
		dtoUsuarioEmpresa.setNoEmpresa(noEmpresa);
		
		res=empresaBusiness.eliminar(dtoUsuarioEmpresa,todos);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seguridad, C:SegEmpresaAction, M:eliminarEmpresas");
		}
		if(res>0)
			return true;
		else
			return false;
	}
	
	@DirectMethod
	public List<EmpresaDto> obtenerTodasEmpresas() {
		if(!Utilerias.haveSession(WebContextManager.get())
				|| (!Utilerias.tienePermiso(WebContextManager.get(), 20)&&
				!Utilerias.tienePermiso(WebContextManager.get(), 29)))
			return null;
		List<EmpresaDto> listEmpresa = new ArrayList<EmpresaDto>();
		
		try {
			EmpresaBusiness empresaBusiness = (EmpresaBusiness) contexto.obtenerBean("empresaBusiness");
			listEmpresa = empresaBusiness.obtenerTodasEmpresas() ;
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegEmpresaAction, M:obtenerTodasEmpresas");
		}
		
		return listEmpresa;
	}	
	
	/**********Excel***************/	
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
			+"P:Utilerias, C:SegEmpresaAction, M:obtenerExcel");
		} catch (IOException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:SegEmpresaAction, M:obtenerExcel");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:SegEmpresaAction, M:obtenerExcel");
		}
		arch.delete();
		return workbook;
	}
	
	@DirectMethod
	public String exportaExcel(String datos) {
		String resultado = "";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 189))
			return resultado;
		try {
			EmpresaBusiness empresaBusiness = (EmpresaBusiness) contexto.obtenerBean("empresaBusiness");
			resultado = empresaBusiness .exportaExcel(datos);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C:MantenimientoBitacoraRentasAction, M: exportaExcel");
		}
		return resultado;
	}
	
	
}
