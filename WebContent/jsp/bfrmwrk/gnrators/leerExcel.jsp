<%@page import="com.webset.set.utilerias.Funciones"%>
<%@page import="org.apache.poi.xssf.usermodel.XSSFWorkbook"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="java.io.IOException"%>
<%@page import="java.util.Date"%>
<%@page import="org.apache.poi.hssf.usermodel.HSSFWorkbook"%>
<%@page import="org.apache.commons.fileupload.FileItem"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="org.apache.commons.fileupload.servlet.ServletFileUpload"%>
<%@page import="org.apache.commons.fileupload.disk.DiskFileItemFactory"%>
<%@page import="com.webset.set.utilerias.Bitacora"%>
<%@page import="com.webset.utils.tools.Utilerias"%>
<%@page import="org.apache.commons.fileupload.FileUploadException"%>
<%@page import="com.webset.set.utilerias.Contexto"%>
<%
Bitacora bitacora = new Bitacora();
try{
	DiskFileItemFactory factory = new DiskFileItemFactory();

	ServletFileUpload upload = new ServletFileUpload(factory);

	List<Map<String, String>> hojaDatos = new ArrayList<Map<String, String>>();
	
	// Primero hacer el upload antes de cualquier metodo del request
	
	List<FileItem> partes =  upload.parseRequest(request);
	
	//***** AGREGADO EMS 26/10/2015: Para separar diferentes lecturas de archivos excel de distintas pantallas
		ServletContext context = request.getSession().getServletContext();
		String sOpcionLectura = request.getParameter("nombreArchivoLeer")==null?"":request.getParameter("nombreArchivoLeer").toString();
	//*****
	
	String sNomVarParm = "nomParam1";
	String sNomValParm = "valParam1";
	
	Map<String, String> params = new HashMap<String, String>();
	
	String json = null;
	
	for (int i = 1; request.getParameter(sNomVarParm) != null 
			&& !request.getParameter(sNomVarParm).toString().equals(""); 
			i++, sNomVarParm = "nomParam" + i, sNomValParm = "valParam" + i) {
		
		params.put(request.getParameter(sNomVarParm).toString(), 
				request.getParameter(sNomValParm).toString());
	} 
	
	Funciones fn = new Funciones();
	

	
	
	for(FileItem item : partes){
    		try {
    			
    			String keys[] = {""};
    			
				if(sOpcionLectura.equals("leerExcelEmpresas")){
        			keys = new String[]{
    						"No_Empresa",
    						"Nombre",
    						"Hora_limite"
    				};	
    			}else if(sOpcionLectura.equals("leerExcelAcreedores")){
    				
        			keys = new String[]{
    						"Inicio",
    						"Fin",
    						"Clave"
    				};	
    			}else if(sOpcionLectura.equals("leerExcelDocumento")){
    				
        			keys = new String[]{
    						"Inicio",
    						"Fin",
    						"Clave"
    				};
        			
    			}else if(sOpcionLectura.equals("leerExcelRentas")){
    				keys = new String[]{
    						"ccid",
    						"sociedad",
    						"numeroAcredor",
    						"nombreAcredor",
    						"docSap",
    						"viaPago",
    						"banco",
    						"cuenta",
    						"moneda",
    						"fechaContabilizacion",
    						"total",
							"referencia"
    				};      
    			}else if(sOpcionLectura.equals("leerExcelForwards")){    				    			 	
    				keys = new String[]{
    						"folio",
    	    			 	"unidad_negocio",
    	    			 	"chequera_cargo",
    	    			 	"chequera_abono",
    	    			 	"forma_pago",
    	    			 	"importe_pago",
    	    			 	"importe_compra",
    	    			 	"tc",
    	    			 	"fec_vto",
    	    			 	"institucion",
    	    			 	"rubro_abono",
    	    			 	"subrubro_abono",
    	    			 	"rubro_cargo",
    	    			 	"subrubro_cargo",
    	    			 	"fec_compra",
    	    			 	"spot",
    	    			 	"puntos_forward",
    	    			 	"referencia",
    	    			 	"concepto"
    				};        			
    			} else if(sOpcionLectura.equals("leerExcelBeneficiarios")){
    				keys = new String[]{
    						"Beneficiarios"
    				};        			
    			}else if(sOpcionLectura.equals("leerExcelRangos")){
    				keys = new String[]{
    						"de",
    						"a"
    				};        			
    			}

    			
    			String extensionArchivo = fn.obtenerExtensionArchivo(item.getName()); 
    			
    			if(extensionArchivo.equals("xls") ){
    				HSSFWorkbook wb = new HSSFWorkbook(item.getInputStream());
    				hojaDatos = Utilerias.leerExcel(wb, keys);
    			}else if(extensionArchivo.equals("xlsx")){
    				XSSFWorkbook wb = new XSSFWorkbook(item.getInputStream());
    				hojaDatos = Utilerias.leerExcel(wb, keys);
    			}
    			
    			//hojaDatos = Utilerias.leerExcel(wb, keys);
    	
    		} catch (IOException e) {
    			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
    			+"P:jsp/gnrators, C:leerExcel.jsp, M:leerExcel");
    		} 
	}
	json = new Gson().toJson(hojaDatos);
	out.write("{'success': 'true', 'json': '"+json+"'}");
}
catch(FileUploadException ex){
	
	bitacora.insertarRegistro(new Date().toString() + " " + ex.toString()
	+"P:jsp/gnrators, C:leerExcel.jsp, M:leerExcel");
	out.write("{'success': 'false'}");
}


%>

