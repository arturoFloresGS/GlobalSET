package com.webset.set.interfaz.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.webset.set.interfaz.dao.CargaPagosDao;
import com.webset.set.interfaz.dto.CargaPagosDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;


public class GeneraLayoutH2H 
{		
	
	CargaPagosDao objCargaPagosDao;
	List<CargaPagosDto> recibeDatos = new ArrayList<CargaPagosDto>();	
	ConsultasGenerales consultasGenerales;
	Date fecha;
	Funciones funciones;
	Bitacora bitacora;
	
	public boolean layoutAltaProveedores(List<Map<String, String>> objGrid)
	{
		System.out.println("Entro a metodo");
		String psRegistro = "";	
		boolean respuesta = false;
		String psRuta = "";		
		String psArchivo = "";
		String psDirectorio = "";
		String caracterFijo = "ALTA"; //Esto solo se pone como prueba
		String psNomArchivo = "";
		int folioConsecutivo = 0;
		List<CargaPagosDto> recibeDatos = new ArrayList<CargaPagosDto>();
		List<CargaPagosDto> resultado = new ArrayList<CargaPagosDto>();
		String resultadoString = "";
		int totalCambios = 0;
		int totalAltas = 0;
		String noBenef = "";
		int bancoBenef = 0;
		String chequeraBenef = "";
		boolean actualizaCambio = false;
		boolean actualizaAlta = false;
				
		try
		{				
			System.out.println("antes del configura");
			System.out.println(objCargaPagosDao.consultarConfiguraSet(2401));
			psArchivo = objCargaPagosDao.consultarConfiguraSet(2401);
			psDirectorio = "SITPRE\\MAESTROTER\\ENVIO\\";
			System.out.println(psDirectorio);
						
			if (objGrid.size() > 0)
			{				
				//Se valida si aplican los registros como altas o cambios
				for (int i=0; i < objGrid.size(); i++) 
				{
					if (objGrid.get(i).get("idFormaPago").equals("3") || objGrid.get(i).get("idFormaPago").equals("5"))
					{
						if (objGrid.get(i).get("idDivisa").equals("MN")) //Por el momento el H2H solo aplica para MN
						{
							//Se valida que no sea nomina, ya que solo se mandan altas para los pagos a terceros y no en nomina
							if ((!objGrid.get(i).get("origen").equals("ADM")) || 
								(objGrid.get(i).get("idBancoBenef") != objGrid.get(i).get("bancoPagador") && objGrid.get(i).get("origen").equals("ADM") && !objGrid.get(i).get("idBancoBenef").equals("0")))
							{
								recibeDatos = objCargaPagosDao.buscaCtasBanco(objGrid.get(i).get("noBenef"));
								
								if (recibeDatos.size() > 1) //Si existe mas de un registro de la misma persona
								{
									for (int cam = 0; cam < recibeDatos.size(); cam ++)
									{										
										if (recibeDatos.get(cam).getEstatusAlta().equals("A"))
											actualizaCambio = true;
										else if (recibeDatos.get(cam).getEstatusAlta().equals("C"))
											actualizaCambio =true;
										else
											actualizaAlta = true;										
									}									
								}
								else //Es el unico registro de la persona
								{
									if (recibeDatos.get(0).getEstatusAlta().equals("A"))
									{
										actualizaCambio = true;
									}
									else if (recibeDatos.get(0).getEstatusAlta().equals("C"))
									{
										actualizaCambio = true;
									}
									else
										actualizaAlta = true;									
								}
								
								//Actualiza estatus en la tabla ctas_banco para las altas de personas H2H
								//resultadoString = objCargaPagosDao.sacaFechaModifMax(objGrid.get(i).get("noBenef"));
								
								if ((actualizaCambio == true && actualizaAlta == true) || actualizaCambio == true )
								{																			
									objCargaPagosDao.actualizaEstatusAlta(objGrid.get(i).get("noBenef"), "C", "");
									objCargaPagosDao.actualizaEstatusAlta(objGrid.get(i).get("noBenef"), "C", resultadoString);
									totalCambios = totalCambios + 1;
								}
								else
								{
									objCargaPagosDao.actualizaEstatusAlta(objGrid.get(i).get("noBenef"), "A", "");
									objCargaPagosDao.actualizaEstatusAlta(objGrid.get(i).get("noBenef"), "A", resultadoString);
									totalAltas = totalAltas + 1;
								}									
							}
						}
					}
				} //Termina validacion de estatus para la tabla ctas_banco *************************************************
				
				if (totalCambios != 0 || totalAltas != 0)
				{
					psNomArchivo = generaNombreArchivo(psArchivo, psDirectorio, "altas_H2H", caracterFijo, folioConsecutivo);
					
					if (!psNomArchivo.equals(""))
					{	
						abreArchivo(psArchivo + psDirectorio, psNomArchivo);
						
						psRegistro = armaLayout(objGrid, 0, 1, totalAltas, totalCambios, psNomArchivo, objGrid.get(0).get("fechaAlta")); //Genera encabezado del layout
						
						insertaRegistro(psArchivo + psDirectorio, psNomArchivo, psRegistro);
						
						/*
						 * public File crearArchivoLayOut(String dirPrincipal,String subCarpetas,String nombreArchivo){
		if(subCarpetas.equals(""))
			creaArchivo = new File(dirPrincipal + nombreArchivo);
		else
			creaArchivo = new File(dirPrincipal +subCarpetas+ nombreArchivo);
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
						 */
												
						/*for (int i = 0; i <= objGrid.size(); i++)
						{					
							psRegistro = armaLayout(objGrid, i, 2, 0, 0, psNomArchivo);
						}
						psRegistro = armaLayout(objGrid, 0, 3, totalAltas, totalCambios, "");			
						*/
						respuesta = true;
					}
					else
					{
						respuesta = false;
					}
		
				}				
			}
			else
				respuesta = false;
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:interfaz, C:GeneraLayoutH2H, M:layoutAltaProveedores");
			respuesta = false;
		}
		return respuesta;
	}
	
	public String armaLayout(List<Map<String, String>> objGrid, int posicion, int tipoRegistro, int totalAltas, int totalCambios, String nombreArchivo, String fechaAlta)
	{
		String psRegistro = "";
		String cadena = "";
		String contrato = "00000001";
		int totalRegistros = 0;
		int valorNumerico = 0;
		try
		{
			psRegistro = "";
			switch (tipoRegistro)
			{
				case 1: //Header H2H y Header Altas
					psRegistro = "HHEADER H2H";
					
					//Total de registros por archivo
					totalRegistros = totalAltas + totalCambios;
					cadena = Integer.toString(totalRegistros);
					cadena = funciones.ajustarLongitudCampo(cadena, 7, "D", "", "0");
					psRegistro = psRegistro + cadena;
					
					//Importe total para las altas no aplica, se rellena de 0
					cadena = "";
					cadena = funciones.ajustarLongitudCampo(cadena, 15, "I", "", "0");
					psRegistro = psRegistro + cadena;
					
					//Contrato H2H, este es asignado por el Banco 
					cadena = "";
					cadena = funciones.ajustarLongitudCampo(contrato, 20, "I", "", " ");
					psRegistro = psRegistro + cadena;
					
					//Subservicio para el H2H, asignado por el Banco
					psRegistro = psRegistro + "TG-0015 ";
					
					//Tipo de servicio asignado por el Banco
					psRegistro = psRegistro + "08";
					
					//Consecutivo o folio, debe de ser el mismo que va a traer en el nombre del archivo
					cadena = "";
					valorNumerico = nombreArchivo.indexOf(".");
					cadena = nombreArchivo.substring(valorNumerico + 1, nombreArchivo.length());
					cadena = cadena.substring(0, cadena.indexOf(".") - 1);
					cadena = funciones.ajustarLongitudCampo(cadena, 7, "D", "", "0");
					psRegistro = psRegistro + cadena;
					
					//Fecha de envio
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd"); 
					String formattedDate = formatter.format(objGrid.get(posicion).get("fecValor"));
				
					System.out.println(formattedDate);
					System.out.println(objGrid.get(posicion).get("fecValor"));
					
					cadena = objGrid.get(posicion).get("fechaAlta").substring(0, 4) + "-" + objGrid.get(posicion).get("fechaAlta").substring(5, 7) + "-" + objGrid.get(posicion).get("fechaAlta").substring(8, 10);
					psRegistro = psRegistro + cadena;
					
					//Campos en blanco
					cadena = "";
					cadena = funciones.ajustarLongitudCampo(cadena, 170, "I", " ", " ");
					psRegistro = psRegistro + cadena;
				
					break;
				case 2: //Detallado
					break;
				case 3: //Triller
					break;
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:GeneraLayoutH2H, M:armaLayout");
		} return psRegistro;
	}
	
	public String generaNombreArchivo(String psDirectorio, String psRutaArchivo, String psTipoFolio, 
									  String psPrefijoNombre, int folioConsecutivo)	
	{
		String nombreArchivo = "";
		String psRuta = "";
		boolean creaDirectorio = false;
		try
		{
			psRuta = psDirectorio + psRutaArchivo;
			folioConsecutivo = consultasGenerales.seleccionarFolioReal(psTipoFolio);
			
			if (folioConsecutivo < 0)
				nombreArchivo = "";
			else
				nombreArchivo = psPrefijoNombre + "." + folioConsecutivo + ".txt";
			
			//Se valida que exista el directorio para la colocacion del archivo
			creaDirectorio = generaDirectorio(psRuta);
			
			if (creaDirectorio == false)
				nombreArchivo = "";
			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:interfaz, C:GeneraLayoutH2H, M:generaNombreArchivo");
			nombreArchivo = "";
		}
		return nombreArchivo;
	}
		
	public boolean generaDirectorio (String psRuta)
	{
		File archivo = null;
		try
		{
			if (!psRuta.equals(""))
			{
				archivo = new File(psRuta);				
			
				if (archivo.isDirectory())
					return true;
				else
					return archivo.mkdirs();
			}
			else
				return false;
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:GeneraLayoutH2H, M:generaDirectorio");
			return false;
		}
	}
	
	public File abreArchivo(String psRuta, String nombreArchivo)
	{
		File creaArchivo = null;
		try
		{
			creaArchivo = new File(psRuta, nombreArchivo);
			if (creaArchivo.isFile())
			{
				return creaArchivo;				
			}
			else
			{
				if (creaArchivo.createNewFile())
					return creaArchivo;
				else return null;
			}		
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:GeneraLayoutH2H, M:abreArchivo");
			return null;
		}
	}
	
	public void insertaRegistro(String psRuta, String nombreArchivo, String registro)
	{
		Scanner archivo = null;
		String sb = "";
		File fichero = abreArchivo(psRuta, nombreArchivo);
		if (fichero != null)
		{
			try
			{
				archivo = new Scanner(fichero);
				while(archivo.hasNext())
				{
					sb += archivo.nextLine() + "\r\n";
				}
				archivo.close();
				FileWriter altaH2H = new FileWriter(fichero);
				sb += registro;
				altaH2H.write(sb);
				altaH2H.close();	
			}
			catch (FileNotFoundException e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:GeneraLayoutH2H, M:insertaRegistro" + "No se encuentra el Archivo");
			}
			catch (IOException e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:GeneraLayoutH2H, M:insertaRegistro" +"Error al escribir en la Bitacora");
			}
		}
		else
		{
			bitacora.insertarRegistro(new Date().toString() + " " 
					+"P:com.webset.set.business, C:CreacionArchivosBusiness, M:insertarRegistro" +"Error al obtener el directorio");
		}
	}
	
	public CargaPagosDao getObjCargaPagosDao() 
	{
		return objCargaPagosDao;
	}

	public void setObjCargaPagosDao(CargaPagosDao objCargaPagosDao) 
	{
		this.objCargaPagosDao = objCargaPagosDao;
	}

}
