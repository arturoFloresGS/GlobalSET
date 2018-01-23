/**
 *@author Cristian Garcia Garcia
 *@since 01/April/2011
 */
package com.webset.set.layout.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

import com.webset.set.layout.dao.LayoutsDao;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;

public class CreacionArchivosBusinessLocal {
	
	Funciones funciones = new Funciones();
	private File creaArchivo = null;
	Bitacora bitacora = new Bitacora();
	private LayoutsDao layoutsDao;

	public boolean crearDirectorioLayOut(String dirPrincipal,String subCarpetas){
		if(subCarpetas.equals(""))
			creaArchivo = new File(dirPrincipal);
		else
			creaArchivo = new File(dirPrincipal+subCarpetas);
		if(creaArchivo.isDirectory())
			return true;
		else
			return creaArchivo.mkdirs();
	}

	public File crearArchivoLayOut(String dirPrincipal,String subCarpetas,String nombreArchivo){
		if(subCarpetas.equals("")){
			//dirPrincipal+="\\";
			creaArchivo = new File(dirPrincipal + nombreArchivo);
		}
		else{
			dirPrincipal+=subCarpetas;
			creaArchivo = new File(dirPrincipal+ nombreArchivo);
		}
		try {
			if(creaArchivo.isFile())
				return creaArchivo;
			else{ 
				if(creaArchivo.createNewFile())
					return creaArchivo;
				else
					return null;
			}
		} catch (IOException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:com.webset.set.business, C:CreacionArchivosBusiness, M:crearArchivoLayOut" +"No se creo el Directorio de la Bitacora " );
			return null;
		}
	}
	
	
	public void insertarRegistro(String dirPrincipal,String subCarpetas,String nomArch, String arg){
		Scanner archivo = null;
		String sb = "";
		if(crearDirectorioLayOut(dirPrincipal,subCarpetas)){
			File fichero = crearArchivoLayOut(dirPrincipal,subCarpetas,nomArch);
			if(fichero!=null){
				try {
					archivo = new Scanner(fichero);
					while(archivo.hasNext()){
						sb += archivo.nextLine() + "\r\n";
					}
					archivo.close();
						FileWriter bita = new FileWriter(fichero);
						sb += arg;
						bita.write(sb);
						bita.close();
				}catch (FileNotFoundException e) {
					bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:com.webset.set.business, C:CreacionArchivosBusiness, M:insertarRegistro" +"No se encuentra el Archivo");
				}catch (IOException e) {
					bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:com.webset.set.business, C:CreacionArchivosBusiness, M:insertarRegistro" +"Error al escribir en la Bitacora");
				}
			}
			else
				bitacora.insertarRegistro(new Date().toString() + " "
				+"P:com.webset.set.business, C:CreacionArchivosBusiness, M:insertarRegistro" +"No se pudo crear el Archivo");
		}
		else{
			bitacora.insertarRegistro(new Date().toString() + " " 
			+"P:com.webset.set.business, C:CreacionArchivosBusiness, M:insertarRegistro" +"Error al obtener el directorio");
		}
	}
	
	public boolean eliminarArchivoLayout(String dirPrincipal,String subCarpetas, String archivo){
		try{
			File borrar = new File(dirPrincipal+subCarpetas+archivo);
			borrar.delete();
			return true;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " 
			+"P:com.webset.set.business, C:CreacionArchivosBusiness, M:eliminarArchivoLayout" );
			return false;
		}
	}
	
	
	
	


	public LayoutsDao getLayoutsDao() {
		return layoutsDao;
	}

	public void setLayoutsDao(LayoutsDao layoutsDao) {
		this.layoutsDao = layoutsDao;
	}
	}
