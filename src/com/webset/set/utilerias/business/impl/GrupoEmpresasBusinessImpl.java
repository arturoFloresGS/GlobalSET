package com.webset.set.utilerias.business.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dao.GrupoEmpresasDao;
import com.webset.set.utilerias.service.GrupoEmpresasService;
import com.webset.set.utileriasmod.dto.GrupoEmpresasDto;
import com.webset.utils.tools.Utilerias;

public class GrupoEmpresasBusinessImpl implements GrupoEmpresasService{
	GrupoEmpresasDao objGrupoEmpresasDao;
	Bitacora bitacora;

	public List<GrupoEmpresasDto> llenaComboGrupo(){
		return objGrupoEmpresasDao.llenaComboGrupo();
	}
	
	public String configuraSet (int indice){
		return objGrupoEmpresasDao.configuraSet(indice);
	}
	
	public List<GrupoEmpresasDto> llenaComboEmpresa(){
		return objGrupoEmpresasDao.llenaComboEmpresa();
	}
	
	public List<GrupoEmpresasDto> llenaGrid(int noEmpresa, int idGrupo, int todo){
		return objGrupoEmpresasDao.llenaGrid(noEmpresa, idGrupo, todo);
	}
	
	public String insertaRegistro(List<Map<String, String>> registro){
		String mensaje = "";
		int recibeEntero = 0;
		int idGrupo = 0;
		int noEmpresa = 0;
		List<GrupoEmpresasDto> recibeDatos = new ArrayList<GrupoEmpresasDto>();
		
		try{
			idGrupo = Integer.parseInt(registro.get(0).get("idGrupo"));
			noEmpresa = Integer.parseInt(registro.get(0).get("noEmpresa"));
			
			//Busca si no existe ya el registro en la base de datos
			recibeDatos = objGrupoEmpresasDao.buscaRegistro(idGrupo, noEmpresa);
			
			if (recibeDatos.size() > 0)
				mensaje = "Una misma empresa no debe de estar en dos Grupos";
			else {
				recibeEntero = objGrupoEmpresasDao.insertaRegistro(idGrupo, noEmpresa);
				if (recibeEntero > 0)
					mensaje = "Datos Registrados";
				else
					mensaje = "Ocurrio un problema en el proceso";
			}
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: GrupoEmpresasBusinessImpl, M: insertaRegistro");
		}return mensaje;
	}
	
	public int eliminaRegistro (int idGrupo, int noEmpresa){
		return objGrupoEmpresasDao.eliminaRegistro(idGrupo, noEmpresa);
	}
	
	public GrupoEmpresasDto obtieneCorreo (int idGrupo){
		GrupoEmpresasDto recibeDatos = new GrupoEmpresasDto();
		List<GrupoEmpresasDto> lista = new ArrayList<GrupoEmpresasDto>();
		
		try{
			lista = objGrupoEmpresasDao.obtieneCorreo(idGrupo);
			if (lista.size() > 0)
			{
				recibeDatos.setCorreoEmpresa(lista.get(0).getCorreoEmpresa());
				recibeDatos.setRemitenteCorreo(lista.get(0).getRemitenteCorreo());
				recibeDatos.setNivelAutorizacion(lista.get(0).getNivelAutorizacion());
			}							
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: GrupoEmpresasBusinessImpl, M: obtieneCorreo");
		}return recibeDatos;		
	}
	
	public String cambiaNivel(int idGrupo, int nivel){
		String mensaje = "";
		int recibeEntero = 0;
		
		try{
			recibeEntero = objGrupoEmpresasDao.cambiaNivel(idGrupo, nivel);
			if (recibeEntero > 0)
				mensaje = "Nivel de Autorización registrado con Exito";
			else
				mensaje = "Ocurrion un error durante la actualización";
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: GrupoEmpresasBusinessImpl, M: cambiaNivel");
		}return mensaje;
	}
	
	//**********************************************************************************************************************
	public GrupoEmpresasDao getObjGrupoEmpresasDao() {
		return objGrupoEmpresasDao;
	}

	public void setObjGrupoEmpresasDao(GrupoEmpresasDao objGrupoEmpresasDao) {
		this.objGrupoEmpresasDao = objGrupoEmpresasDao;
	}

	@Override
	public HSSFWorkbook reporteGrupoEmpresas() {
		HSSFWorkbook hb=null;
		try {
			hb=Utilerias.generarExcel(new String[]{
					
					
					"No. Empresa",
					"Empresa",
					"Id. Grupo",
					"Grupo",
					"Correo Empresa",	
					"Remitente Correo",
							
			}, 
					objGrupoEmpresasDao.reporteGrupoEmpresas(), 
					new String[]{
							
								
							"noEmpresa",
							"nomEmpresa",
							"idGrupo",
							"descGrupo",
							"correoEmpresa",
							"remitenteCorreo",	
					});
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: GrupoEmpresasBusinessImpl, M: reporteGrupoEmpresas");
		}return hb;
	}
	
}
