<%@page import="com.webset.set.utilerias.ConstantesSet"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.io.FileInputStream"%>
<%
	ServletContext context = request.getSession().getServletContext();
	String sNombreRep = request.getParameter("nomReporte") == null ?
			"" : request.getParameter("nomReporte").toString();
	
	if (sNombreRep.equals("")) {
	  return;
	}
	
	String reportFile = null;
	
	byte[] datos = null;
	FileInputStream archivo = null;
	Map<String, String> params = new HashMap<String, String>();
	
	try {
		int i = 1;
		String sNomVarParm = "nomParam" + i;
	    String sNomValParm = "valParam" + i;
	    
	    for (; request.getParameter(sNomVarParm) != null 
	    		&& !request.getParameter(sNomVarParm).toString().equals(""); 
	    		i++, sNomVarParm = "nomParam" + i, sNomValParm = "valParam" + i)
	    	params.put(request.getParameter(sNomVarParm).toString(), 
	    			request.getParameter(sNomValParm).toString());
		
	    reportFile = params.get("rutaArchivo");
    	archivo = new FileInputStream(reportFile); 
        int longitud = archivo.available();
        datos = new byte[longitud];
        
        archivo.read(datos);
	    archivo.close();
	    if (sNombreRep.equals("layouts")) {		    
		    response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition","attachment;filename=" + 
		    	params.get("nombreArchivo")); 
		} else if (sNombreRep.equals("cartas")) {		    
			response.setContentType("application/pdf");
		}	       
	    
	    ServletOutputStream ouputStream = response.getOutputStream();
	    ouputStream.write(datos);
	    ouputStream.flush();
	    ouputStream.close();
	} catch (Exception e) {
		e.printStackTrace();
		System.out.println("Error al generar el Excel: "+e.getMessage());
	}	 
%>