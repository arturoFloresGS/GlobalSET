package com.webset.set.utilerias;

//import org.apache.log4j.Level;
import org.apache.log4j.Logger;
/**
 *@author PAEE 
 *Interfaz modificada para uso de Logger sin mayor impacto en el sistema
 **/
public class Bitacora {

	private static Logger logger = Logger.getRootLogger();//Logger.getLogger(Bitacora.class);
	
	public Bitacora() {
		//Logger.getRootLogger().setLevel(Level.ALL);
		//logger.setLevel(Level.ALL);
		
	}

	public void insertarRegistro(String arg){
		logger.error(arg);
	}
	
	public static String getStackTrace(Exception e) {
        StringBuffer aux = new StringBuffer(e.toString()).append("\n");

        StackTraceElement[] vec = e.getStackTrace();

        for (StackTraceElement element : vec) {
            aux.append(element.toString()).append("\n");
        }

        return aux.toString();
    }

	public void registrar(String arg0) {
		logger.debug(arg0);
	}

}
