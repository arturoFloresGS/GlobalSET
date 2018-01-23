package com.webset.set.utilerias.bancaelectronica;

import java.util.Date;

import com.webset.set.layout.business.CreacionArchivosBusiness;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;

public class FuncionesBe {
	Bitacora bitacora = new Bitacora();
	Funciones funciones = new Funciones();
	CreacionArchivosBusiness creaArchivo= new CreacionArchivosBusiness();
	
	
	/*Function generar_nombre_archivo(ByVal ps_banco As String, ByVal ps_folio_banco, _
	    ByVal ps_indice_banco, ByRef ps_Archivo, _
	    ByRef ps_nom_archivo As String, _
	    ByVal pbMismoBanco As Boolean, ByVal pbInternacional As Boolean) As Boolean
		Exit Function*/
	public String generarNombreArchivo(String psBanco, int folioBanco, String psIndiceBanco, String psArchivo,
			String tipoEnvio){
		//"bancomer_cie", "arch_bancomer_cie", "be", ps_Archivo, ps_nom_archivo, optmismo.Value, False
		try{
			String psRuta="";
			String psNomArchivo="";
		
		
			psRuta = psArchivo + "\\" + psBanco + "\\";
	        //plFolio = gobjVarGlobal.Folio_Real("" & ps_folio_banco & "")
	        if(folioBanco < 0)
	        {
	        	return "No se encontr� el folio para " + psBanco;
	        }

	        if(psBanco!=null && psBanco.toUpperCase().equals("NAFIN"))
	        {
	            psNomArchivo = psIndiceBanco + "3" + funciones.ponerCeros(""+folioBanco,5)+ ".txt";
	            psArchivo = psArchivo + "\\" + psBanco + "\\" + psNomArchivo;
	        }
	        else{
	        	if(tipoEnvio!=null && tipoEnvio.equals("Mismo"))
	        	{
	                psNomArchivo = psIndiceBanco + "1" + funciones.ponerCeros(""+folioBanco,5) + ".txt";
	                psArchivo = psArchivo + "\\" + psBanco + "\\" + psNomArchivo;
	        	}
	            else if(tipoEnvio!=null && tipoEnvio.equals("Internacional"))
	            {
	                psNomArchivo = psIndiceBanco + "3" +funciones.ponerCeros(""+folioBanco,5) + ".txt";
	                psArchivo = psArchivo + "\\" + psBanco + "\\" + psNomArchivo;
	            }
	            else
	            {
	            	psNomArchivo = psIndiceBanco + "2" + funciones.ponerCeros(""+folioBanco,5) + ".txt";
	                psArchivo = psArchivo + "\\" + psBanco + "\\" + psNomArchivo;
	            }
	        }
	        return psNomArchivo;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:com.webset.set.utilerias.bancaelectronica, C:FuncionesBe, M:generarNombreArchivo");
			return "";
		}
		
	}
}
