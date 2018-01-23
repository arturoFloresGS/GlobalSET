package com.webset.set.reportes.business;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.webset.set.reportes.dao.ReportesDao;
import com.webset.set.reportes.service.ReportesService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

@SuppressWarnings("unchecked")
public class ReportesBusinessImpl implements ReportesService{
	private Bitacora bitacora = new Bitacora();
	//private GlobalSingleton globalSingleton;
	private ReportesDao reportesDao;
	private Funciones funciones = new Funciones();
	//private static Logger logger = Logger.getLogger(ReportesBusinessImpl.class);

	public List<LlenaComboEmpresasDto> consultarEmpresas(int usuario){
		return reportesDao.consultarEmpresas(usuario);
	}
	
	public List<LlenaComboGralDto> consultarCajas(int usuario){
		return reportesDao.consultarCajas(usuario);
	}
	
	public List<LlenaComboChequeraDto> consultarOrigen(String tipoMovto){
		return reportesDao.consultarOrigen(tipoMovto);
	}
	
	public List<LlenaComboGralDto> consultarBancos(int iEmpresa, String sDivisa){
		return reportesDao.consultarBancos(iEmpresa, sDivisa);
	}
	
	public List<LlenaComboChequeraDto> consultarChequeras(int iBanco, int iEmpresa){
		return reportesDao.consultarChequeras(iBanco, iEmpresa);
	}
	
	public JRDataSource obtenerDatosReporteCheques(String nomReporte, Map parameters){
		JRMapArrayDataSource  jrDataSource = null; 
	    XStream xStream = new XStream(new DomDriver());
	    List<Map<String, Object>> resMap = null;
	    //String resXml = "";
	    String sCadEmpresas = "";
	    String sCadCajas = "";
	    int iUsuario = funciones.convertirCadenaInteger(parameters.get("usuario").toString());
		try{
			//globalSingleton = GlobalSingleton.getInstancia();
			//logger.info("usuario: "+globalSingleton.getUsuarioLoginDto().getIdUsuario());
			List<LlenaComboEmpresasDto> listEmpresas = new ArrayList<LlenaComboEmpresasDto>();
			List<LlenaComboGralDto> listCajas = new ArrayList<LlenaComboGralDto>();
					
			//validando empresas
			if(parameters.get("empresas").equals("")){
				listEmpresas = reportesDao.consultarEmpresas(iUsuario);
				if(listEmpresas.size() > 0){
					for(int i = 0; i < listEmpresas.size(); i++)
					{
						sCadEmpresas = sCadEmpresas + listEmpresas.get(i).getNoEmpresa() + ",";
					}
				}
				parameters.put("empresas", sCadEmpresas.substring(0, sCadEmpresas.length() - 1));
			}
			
			//validando cajas
			if(parameters.get("cajas").equals("")){
				listCajas = reportesDao.consultarCajas(iUsuario);
				if(listCajas.size() > 0){
					for(int j = 0; j < listCajas.size(); j++){
						sCadCajas = sCadCajas + listCajas.get(j).getId() + ",";
					}
				}
				parameters.put("cajas", sCadCajas.substring(0, sCadCajas.length() - 1));
			}
			
			resMap = reportesDao.consultarReporteCheques(parameters);
		
			if(resMap.size()==0)
				return null;

			// convert to XML 
			//xStream.alias("map", java.util.Map.class);
			xStream.alias("mapa cheque",  java.util.List.class);
			//resXml = xStream.toXML(resMap.toArray());
			//logger.info("***xml="+resXml);
			
			//String [] cadenas = new String[2];
            jrDataSource = new JRMapArrayDataSource(resMap.toArray());
			//xmlDataSource = new JRXmlDataSource(resXml, "/");

		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Reportes, C:ReportesBusinessImpl, M:obtenerDatosReporte");
		}
		return jrDataSource;
	}
	
	public int buscarDatosReportes(List<Map<String, String>> objParams) {
		List<Map<String, Object>> resp = null;
		resp = reportesDao.buscarDatosReporte(objParams);
		if(resp.size() > 0) return 1;
		else return 0;
	}
	
	public JRDataSource reporteTransConfirmadas(Map parameters) {
		JRDataSource jrDataSource = null;
		List<Map<String, Object>> resp = null;
		String operacionSup = "";
		
		try {
			if(parameters.get("tipoReporte").equals("transfer")) {
				if(Integer.parseInt(parameters.get("estatusReporte").toString()) == 0 || 
						Integer.parseInt(parameters.get("estatusReporte").toString()) == 2 || 
						Integer.parseInt(parameters.get("estatusReporte").toString()) == 3)
					resp = reportesDao.buscarDatosReportes(parameters);
				else if(Integer.parseInt(parameters.get("estatusReporte").toString()) == 1)
					resp = reportesDao.buscarTransferidas(parameters);
			}else if(parameters.get("tipoReporte").equals("traspasos")) {
				if(Integer.parseInt(parameters.get("estatusReporte").toString()) == 0) {	//Traspasos entre cuentas
					if(Integer.parseInt(parameters.get("tipoTrasp").toString()) == 0)
						operacionSup = "3800";
					else if(Integer.parseInt(parameters.get("tipoTrasp").toString()) == 1)
						operacionSup = "3700";
					else if(Integer.parseInt(parameters.get("tipoTrasp").toString()) == 2)
						operacionSup = "3800, 3700";
				}else if(Integer.parseInt(parameters.get("estatusReporte").toString()) == 1) { //Traspasos entre empresas
					if(Integer.parseInt(parameters.get("tipoTrasp").toString()) == 0)
						operacionSup = "3801,3805,3806,3808,3809,3814";
					else if(Integer.parseInt(parameters.get("tipoTrasp").toString()) == 1)
						operacionSup = "3701,3705,3706,3708,3709,3714";
					else if(Integer.parseInt(parameters.get("tipoTrasp").toString()) == 2)
						operacionSup = "3801, 3701";
				}else if(Integer.parseInt(parameters.get("estatusReporte").toString()) == 2) {	//Traspasos ambos
					if(Integer.parseInt(parameters.get("tipoTrasp").toString()) == 0)
						operacionSup = "3800, 3801";
					else if(Integer.parseInt(parameters.get("tipoTrasp").toString()) == 1)
						operacionSup = "3700, 3701";
					else if(Integer.parseInt(parameters.get("tipoTrasp").toString()) == 2)
						operacionSup = "3700, 3800, 3701, 3801";
				}
				resp = reportesDao.entreCuentasEmp(parameters, operacionSup);
			}
			jrDataSource = new JRMapArrayDataSource(resp.toArray());
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Reportes, C:ReportesBusinessImpl, M:reporteTransConfirmadas");
		}
		return jrDataSource;
	}
	
	@SuppressWarnings("deprecation")
	public String exportaExcel(String datos) {
		String respuesta = "";
		String sCadEmpresas = "";
	    String sCadCajas = "";
	    Gson gson = new Gson();
	    List<Map<String, Object>> resMap = null;
	    List<LlenaComboEmpresasDto> listEmpresas = new ArrayList<LlenaComboEmpresasDto>();
		List<LlenaComboGralDto> listCajas = new ArrayList<LlenaComboGralDto>();
		List<Map<String, String>> parameters = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
		Map params = new HashMap();
		int iUsuario = funciones.convertirCadenaInteger(parameters.get(0).get("usuario").toString());
		
	    try {
	    	System.out.println("datos para armar excel "+datos.toString());
			params.put("chequera", parameters.get(0).get("chequera"));
			params.put("empresas", parameters.get(0).get("empresas"));
			params.put("divisa", parameters.get(0).get("divisa"));
			params.put("cajas", parameters.get(0).get("cajas"));
			params.put("bancoInf", parameters.get(0).get("bancoInf"));
			params.put("bancoSup", parameters.get(0).get("bancoSup"));
			params.put("fechaIni", parameters.get(0).get("fechaIni"));
			params.put("fechaFin", parameters.get(0).get("fechaFin"));
			params.put("estatusMov", parameters.get(0).get("estatusMov"));
			params.put("estatusEntregado", parameters.get(0).get("estatusEntregado"));
			params.put("origen", parameters.get(0).get("origen"));
			params.put("agrupado", parameters.get(0).get("agrupado"));
			params.put("estatusCb", parameters.get(0).get("estatusCb"));
			params.put("ordenado", parameters.get(0).get("ordenado"));
			params.put("usuario", parameters.get(0).get("usuario"));
			params.put("nomEmpresa", parameters.get(0).get("nomEmpresa"));
			params.put("descEstatus", parameters.get(0).get("descEstatus"));
			params.put("subtitulo", parameters.get(0).get("subtitulo"));
			params.put("cheIni", parameters.get(0).get("cheIni"));
			params.put("cheFin", parameters.get(0).get("cheFin"));
			
			//validando empresas
			if(parameters.get(0).get("empresas").equals("")){
				listEmpresas = reportesDao.consultarEmpresas(iUsuario);
				if(listEmpresas.size() > 0){
					for(int i = 0; i < listEmpresas.size(); i++)
					{
						sCadEmpresas = sCadEmpresas + listEmpresas.get(i).getNoEmpresa() + ",";
					}
				}
				params.put("empresas", sCadEmpresas.substring(0, sCadEmpresas.length() - 1));
			}
			
			//validando cajas
			if(parameters.get(0).get("cajas").equals("")){
				listCajas = reportesDao.consultarCajas(iUsuario);
				if(listCajas.size() > 0){
					for(int j = 0; j < listCajas.size(); j++){
						sCadCajas = sCadCajas + listCajas.get(j).getId() + ",";
					}
				}
				params.put("cajas", sCadCajas.substring(0, sCadCajas.length() - 1));
			}
			
			resMap = reportesDao.consultarReporteCheques(params);
			
			if(resMap.size() <= 0)
				return "No existen datos para exportar a excel!!";
			
			//Se crea el libro Excel
			HSSFWorkbook wb = new HSSFWorkbook();
            //Se crea una nueva hoja dentro del libro
            HSSFSheet sheet = wb.createSheet("Cheques");
            //Se crea una fila dentro de la hoja
            //HSSFPatriarch patr = sheet.createDrawingPatriarch();
            
            HSSFDataFormat format = wb.createDataFormat();
            HSSFCellStyle cellStyle = wb.createCellStyle();
            cellStyle.setDataFormat(format.getFormat("$#,##0.00"));
            
            HSSFRow row1 = sheet.createRow((short)0);
            row1.createCell((short)0).setCellValue("Beneficiario");
            row1.createCell((short)1).setCellValue("Concepto");
            row1.createCell((short)2).setCellValue("No. Cheque");
            row1.createCell((short)3).setCellValue("Importe");
            row1.createCell((short)4).setCellValue("Fecha Cheque");
            row1.createCell((short)5).setCellValue("Estatus");
            
            for(int i=0; i<resMap.size(); i++) {
            	HSSFRow row2 = sheet.createRow((short)i+1);
                row2.createCell((short)0).setCellValue(resMap.get(i).get("beneficiario").toString());
                row2.createCell((short)1).setCellValue(resMap.get(i).get("concepto").toString());
                row2.createCell((short)2).setCellValue(resMap.get(i).get("no_cheque").toString());
                row2.createCell((short)3).setCellValue(resMap.get(i).get("importe").toString());
                row2.createCell((short)4).setCellValue(resMap.get(i).get("fecha_cheque").toString());
                row2.createCell((short)5).setCellValue(resMap.get(i).get("estatus").toString());
            }
          //  String nomArch = "\\\\svrwebset\\cheques\\" + "ReporteCheques" + ".xls";
            String nomArch = "\\\\C:\\cheques\\" + "ReporteCheques" + ".xls";
            
            //Escribimos los resultados a un fichero Excel
            FileOutputStream fileOut = new FileOutputStream(nomArch);
            wb.write(fileOut);
            fileOut.close();
            
            respuesta = "Cheques exportados a excel";
            
        }catch(IOException e){
        	System.out.println(e.toString());
            System.out.println("Error al escribir el fichero.");
            return "Error al imprimir el cheque";
        }
        return respuesta;
	}
	
	@SuppressWarnings("deprecation")
	public String exportaExcelTransfer(String datos) {
		List<Map<String, Object>> resMap = null;
		String respuesta = "";
		Gson gson = new Gson();
		List<Map<String, String>> param = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		Map parameters = new HashMap();
		
	    try {
	    	parameters.put("noEmpresa", param.get(0).get("noEmpresa"));
			parameters.put("nomEmpresa", param.get(0).get("nomEmpresa"));
			parameters.put("noBanco", param.get(0).get("noBanco"));
			parameters.put("idDivisa", param.get(0).get("idDivisa"));
			parameters.put("FECHA_INI", param.get(0).get("FECHA_INI"));
			parameters.put("FECHA_FIN", param.get(0).get("FECHA_FIN"));
			parameters.put("estatusReporte", param.get(0).get("estatusReporte"));
			parameters.put("chkSmart", param.get(0).get("chkSmart"));
			parameters.put("NOM_REP", param.get(0).get("NOM_REP"));
			parameters.put("tipoReporte", param.get(0).get("tipoReporte"));
			parameters.put("usuario", param.get(0).get("usuario"));
			
	    	resMap = buscaDatosExcelTransfer(parameters);
	    	
			if(resMap.size() <= 0) return "No existen datos para exportar a excel!!";
			
			//Se crea el libro Excel
			HSSFWorkbook wb = new HSSFWorkbook();
            //Se crea una nueva hoja dentro del libro
            HSSFSheet sheet = wb.createSheet("Cheques");
            //Se crea una fila dentro de la hoja
            //HSSFPatriarch patr = sheet.createDrawingPatriarch();
            
            HSSFDataFormat format = wb.createDataFormat();
            HSSFCellStyle cellStyle = wb.createCellStyle();
            cellStyle.setDataFormat(format.getFormat("$#,##0.00"));
            
            HSSFRow row1 = sheet.createRow((short)0);
            row1.createCell((short)0).setCellValue("Beneficiario");
            row1.createCell((short)1).setCellValue("Concepto");
            row1.createCell((short)2).setCellValue("Importe");
            row1.createCell((short)3).setCellValue("Banco Benef.");
            row1.createCell((short)4).setCellValue("Clabe Benef.");
            row1.createCell((short)5).setCellValue("No. Factura");
            row1.createCell((short)6).setCellValue("Divisa");
            row1.createCell((short)7).setCellValue("Fecha");
            row1.createCell((short)8).setCellValue("Estatus");
            row1.createCell((short)9).setCellValue("Archivo");
            
            for(int i=0; i<resMap.size(); i++) {
            	HSSFRow row2 = sheet.createRow((short)i+1);
                row2.createCell((short)0).setCellValue(resMap.get(i).get("nombre_benef").toString());
                row2.createCell((short)1).setCellValue(resMap.get(i).get("concepto").toString());
                row2.createCell((short)2).setCellValue(resMap.get(i).get("importe").toString());
                row2.createCell((short)3).setCellValue(resMap.get(i).get("banco_benef").toString());
                row2.createCell((short)4).setCellValue(resMap.get(i).get("clabe_benef").toString());
                row2.createCell((short)5).setCellValue(resMap.get(i).get("no_docto").toString());
                row2.createCell((short)6).setCellValue(resMap.get(i).get("divisa").toString());
                row2.createCell((short)7).setCellValue(resMap.get(i).get("fecha").toString());
                row2.createCell((short)8).setCellValue(resMap.get(i).get("estatus").toString());
                
                if(Integer.parseInt(param.get(0).get("estatusReporte").toString()) != 1)
                	row2.createCell((short)9).setCellValue(resMap.get(i).get("usr_modifico").toString());
                else
                	row2.createCell((short)9).setCellValue(resMap.get(i).get("rubro").toString());
            }
            String nomArch = "\\\\svrwebset\\reportesVarios\\" + "ReporteTransferencias" + ".xls";
            
            //Escribimos los resultados a un fichero Excel
            FileOutputStream fileOut = new FileOutputStream(nomArch);
            wb.write(fileOut);
            fileOut.close();
            
            respuesta = "Transferencias exportadas a excel";
            
        }catch(IOException e){
            return "Error al crear el archivo de excel de transferencias";
        }
        return respuesta;
	}
	
	public List<Map<String, Object>> buscaDatosExcelTransfer(Map parameters) {
		List<Map<String, Object>> resp = null;
		String operacionSup = "";
		
		try {
			if(parameters.get("tipoReporte").equals("transfer")) {
				if(Integer.parseInt(parameters.get("estatusReporte").toString()) == 0 || 
						Integer.parseInt(parameters.get("estatusReporte").toString()) == 2 || 
						Integer.parseInt(parameters.get("estatusReporte").toString()) == 3)
					resp = reportesDao.buscarDatosReportes(parameters);
				else if(Integer.parseInt(parameters.get("estatusReporte").toString()) == 1)
					resp = reportesDao.buscarTransferidas(parameters);
			}else if(parameters.get("tipoReporte").equals("traspasos")) {
				if(Integer.parseInt(parameters.get("estatusReporte").toString()) == 0) {	//Traspasos entre cuentas
					if(Integer.parseInt(parameters.get("tipoTrasp").toString()) == 0)
						operacionSup = "3800";
					else if(Integer.parseInt(parameters.get("tipoTrasp").toString()) == 1)
						operacionSup = "3700";
					else if(Integer.parseInt(parameters.get("tipoTrasp").toString()) == 2)
						operacionSup = "3800, 3700";
				}else if(Integer.parseInt(parameters.get("estatusReporte").toString()) == 1) { //Traspasos entre empresas
					if(Integer.parseInt(parameters.get("tipoTrasp").toString()) == 0)
						operacionSup = "3801";
					else if(Integer.parseInt(parameters.get("tipoTrasp").toString()) == 1)
						operacionSup = "3701";
					else if(Integer.parseInt(parameters.get("tipoTrasp").toString()) == 2)
						operacionSup = "3801, 3701";
				}else if(Integer.parseInt(parameters.get("estatusReporte").toString()) == 2) {	//Traspasos ambos
					if(Integer.parseInt(parameters.get("tipoTrasp").toString()) == 0)
						operacionSup = "3800, 3801";
					else if(Integer.parseInt(parameters.get("tipoTrasp").toString()) == 1)
						operacionSup = "3700, 3701";
					else if(Integer.parseInt(parameters.get("tipoTrasp").toString()) == 2)
						operacionSup = "3700, 3800, 3701, 3801";
				}
				resp = reportesDao.entreCuentasEmp(parameters, operacionSup);
			}
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Reportes, C:ReportesBusinessImpl, M:buscaDatosExcelTransfer");
		}
		return resp;
	}
	
	public ReportesDao getReportesDao() {
		return reportesDao;
	}
	
	public void setReportesDao(ReportesDao reportesDao) {
		this.reportesDao = reportesDao;
	}
}
