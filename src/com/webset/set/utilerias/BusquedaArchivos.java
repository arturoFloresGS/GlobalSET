package com.webset.set.utilerias;

import java.io.File;
import java.util.Date;

import org.apache.log4j.Logger;


/**
 * 
 * @author Sergio Vaca
 * @since 27/Octubre/2010
 */
public class BusquedaArchivos {
	private static Logger logger = Logger.getLogger(BusquedaArchivos.class);
	private Bitacora bitacora = new Bitacora();

	/**
	 * Para la busqueda de los archivos segun el directorio actual
	 * @param extension del archivo a buscar
	 * @return File[]
	 * retorna un vector de archivos
	 */
	public File[] obtenerNombreArchivos(String extension){
		try{
			int cantidad=0;
			int i;
			int j=0;
			String dirActual=System.getProperty("user.dir");
			logger.info("Directorio"+dirActual);
			System.out.println("directorio actual "+dirActual);
			File file = new File(dirActual);
			
			if(!file.exists())
				file.mkdirs();
			
			if(file.isDirectory()){
				File []datos = file.listFiles();
				for(i=0; i<datos.length; i++)
					if(obtenerArchivo(datos[i].getName(),extension))
						cantidad++;
				File []archivo=new File[cantidad];
				for(i=0; i<datos.length; i++)
					if(obtenerArchivo(datos[i].getName(),extension)){
						archivo[j]=datos[i];
						j++;
					}
				return archivo;
			}
			else
				return null;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()
					+ " "
					+ e.toString()
					+ "P:Util, C:BusquedaArchivosDao, M:obtenerNombreArchivos");
       		return null;
       	}
	}
	/**
	 * Verifica que el archivo contenga la extension que se necesita
	 * @param arch el nombre del archivo
	 * @param bus la extension a comparar
	 * @return boolean
	 */
	private boolean obtenerArchivo(String arch, String bus){
		try{
			int i;
			int pos=0;
			for(i=0; i<arch.length(); i++){
				if(arch.charAt(i)=='.')
					pos=i;
			}
			if(arch.substring(pos).equals(bus)||arch.substring(pos).equals(bus.toUpperCase()))
				return true;
			else
				return false;
		}catch(Exception e){
       		bitacora.insertarRegistro(new Date().toString()
					+ " "
					+ e.toString()
					+ "P:Util, C:BusquedaArchivosDao, M:obtenerArchivo");
       		return false;
       	}
	}
	/**
	 * cambia la extension del archivo recibiendo la entension actual y la nueva 
	 * @param datos
	 * @param extensionActual
	 * @param extensionNueva
	 * @return
	 */
	public File[] cambiarExtension(File []datos, String extensionActual, String extensionNueva){
		try{
			int i;
			for(i=0; i<datos.length; i++){
				if(!datos[i].renameTo(new File(datos[i].getAbsolutePath().replace(extensionActual, extensionNueva))))
					bitacora.insertarRegistro("Fallo en renombrar el archivo: " + datos[i].getAbsolutePath());
				else
					datos[i]=new File(datos[i].getAbsolutePath().replace(extensionActual, extensionNueva));
			}
			return datos;
		}catch(Exception e){
       		bitacora.insertarRegistro(new Date().toString()
					+ " "
					+ e.toString()
					+ "P:Util, C:BusquedaArchivosDao, M:cambiarExtension");
       		return null;
       	}
	}
	
	public File cambiarExtensionSoloUno(File dato, String extensionActual, String extensionNueva){
		try{
			if(!dato.renameTo(new File(dato.getAbsolutePath().replace(extensionActual, extensionNueva))))
				logger.info("Fallo en renombrar el archivo: " + dato.getAbsolutePath());
			return dato;
		}catch(Exception e){
       		bitacora.insertarRegistro(new Date().toString()
					+ " "
					+ e.toString()
					+ "P:Util, C:BusquedaArchivosDao, M:cambiarExtensionSoloUno");
       		return null;
       	}
	}
	
/*	public static void main(String args[]){
		long ini = System.currentTimeMillis();
		int i;
		BusquedaArchivosDao a = new BusquedaArchivosDao();
		Scanner archivo;
		System.setProperty("user.dir","c:\\movimientos\\bital\\");
		File []nombres = a.obtenerNombreArchivos(".tmp");
		nombres=a.cambiarExtension(nombres,".tmp",".txt");
		for(i=0; i<nombres.length;i++)
			logger.info(nombres[i].getAbsolutePath());
		try {
			for(i=0; i<nombres.length;i++){
				logger.info("\n\nArchivo " + (i+1) + ": \n\n");
				archivo=new Scanner(nombres[i]);
				while(archivo.hasNext()){
					logger.info(archivo.nextLine());
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long fin = System.currentTimeMillis();
		logger.info(fin-ini);
	}*/
}
