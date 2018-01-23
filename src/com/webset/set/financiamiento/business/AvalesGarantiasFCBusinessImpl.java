package com.webset.set.financiamiento.business;


import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.financiamiento.dao.AvalesGarantiasFCDao;
import com.webset.set.financiamiento.dto.AvalGarantiaDto;
import com.webset.set.financiamiento.service.AvalesGarantiasFCService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public class AvalesGarantiasFCBusinessImpl implements AvalesGarantiasFCService {
	private Bitacora bitacora = new Bitacora();
	private AvalesGarantiasFCDao avalesGarantiasFCDao;

	public Bitacora getBitacora() {
		return bitacora;
	}

	public void setBitacora(Bitacora bitacora) {
		this.bitacora = bitacora;
	}

	public AvalesGarantiasFCDao getAvalesGarantiasFCDao() {
		return avalesGarantiasFCDao;
	}

	public void setAvalesGarantiasFCDao(AvalesGarantiasFCDao avalesGarantiasFCDao) {
		this.avalesGarantiasFCDao = avalesGarantiasFCDao;
	}

	@Override
	public List<LlenaComboGralDto> llenarCmbEmpresa(int piNoUsuario, boolean pbMismaEmpresa, int plEmpresa) {
		return avalesGarantiasFCDao.llenarCmbEmpresa(piNoUsuario, pbMismaEmpresa,plEmpresa);
	}
	@Override
	public List<LlenaComboGralDto> llenarCmbEmpresaAvalista(int piNoUsuario, boolean pbMismaEmpresa, int plEmpresa) {
		return avalesGarantiasFCDao.llenarCmbEmpresaAvalista(piNoUsuario, pbMismaEmpresa,plEmpresa);
	}
	@Override
	public List<LlenaComboGralDto> llenarCmbTipoGtia() {
		return avalesGarantiasFCDao.llenarCmbTipoGtia();
	}
	@Override
	public List<AvalGarantiaDto> buscarAvalGtia(String psTipo, String piEmpresa) {
		String tipoPersona="";
		int cveEmpresa=0;
		if(!piEmpresa.equals("0")){
			cveEmpresa=Integer.parseInt(separarEmpresa(piEmpresa,'E'));
			tipoPersona=separarEmpresa(piEmpresa,'T');
		}
		return avalesGarantiasFCDao.buscarAvalGtia(psTipo,cveEmpresa,tipoPersona);
	}

	@Override
	public Map<String, Object> updateAvalGarantia(String empresa, int lsTipo, String clave, String descripcion,
			double valor, String fecIni, String fecFin, double pje, String vsEspecial) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		char tipoPersona;
		int cveEmpresa=0;
		try {
			int result = 0;
			cveEmpresa=Integer.parseInt(separarEmpresa(empresa,'E'));
			tipoPersona=separarEmpresa(empresa,'T').charAt(0);
			result = avalesGarantiasFCDao.updateAvalGarantia(cveEmpresa,  lsTipo, clave, descripcion, 
					valor,  fecIni, fecFin,  pje,  vsEspecial,tipoPersona);
			if (result > 0) {
				mensajes.add("Registro modificado.");
			} else {
				mensajes.add("El registro NO se modifico correctamente.");
			}
			mapResult.put("msgUsuario", mensajes);
			mapResult.put("result", result);

		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AvalesGarantiasFCAction, M:updateAvalGarantia");
		}
		return mapResult;
	}

	@Override
	public Map<String, Object> insertaAvalGtia(String empresa, int lsTipo, String clave, String descripcion, double valor,
			String idDivisa, String fecIni, String fecFin, double pje, String vsEspecial) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		List<AvalGarantiaDto> list = new ArrayList<AvalGarantiaDto>();
		int cveEmpresa;
		char tipoPersona;
		try {
			cveEmpresa=Integer.parseInt(separarEmpresa(empresa,'E'));
			tipoPersona=separarEmpresa(empresa,'T').charAt(0);
			list=avalesGarantiasFCDao.existeAvalGtia(cveEmpresa,lsTipo,clave,tipoPersona);
			if(!list.isEmpty()){
				mensajes.add("Ya existe un registro con esta clave: "+clave+ " y tipo de garantía "+lsTipo);
				mapResult.put("msgUsuario", mensajes);
				return mapResult;
			}
			else{
				int result = 0;
				result = avalesGarantiasFCDao.insertaAvalGtia( cveEmpresa,  lsTipo, clave, descripcion, 
						valor, idDivisa, fecIni, fecFin,  pje,  vsEspecial,tipoPersona);
				if (result > 0) {
					mensajes.add("Datos registrados.");
				} else {
					mensajes.add("El registro NO se guardó correctamente.");
				}
				mapResult.put("msgUsuario", mensajes);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AvalesGarantiasFCAction, M:insertaAvalGtia");
		}
		return mapResult;
	}

	@Override
	public List<AvalGarantiaDto> selectAvaladas(String empresa, String clave) {
		String tipoPersona="";
		int cveEmpresa=0;
		if(!empresa.equals("0")){
			cveEmpresa=Integer.parseInt(separarEmpresa(empresa,'E'));
			tipoPersona=separarEmpresa(empresa,'T');
		}
		return avalesGarantiasFCDao.selectAvaladas(cveEmpresa,clave,tipoPersona);
	}
	@Override
	public Map<String, Object> insertaAsignacionEmp(String empresa,String clave,int empresaA,
			double montoAvalado) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		char tipoPersona;
		int cveEmpresa=0;
		try {
			cveEmpresa=Integer.parseInt(separarEmpresa(empresa,'E'));
			tipoPersona=separarEmpresa(empresa,'T').charAt(0);
			int result = 0;
			result = avalesGarantiasFCDao.insertaAsignacionEmp(cveEmpresa,  clave, empresaA, montoAvalado,tipoPersona);
			if (result > 0) {
				mensajes.add("Asignación Realizada.");
			} else {
				mensajes.add("Error en la asignación consulte a sistemas.");
			}
			mapResult.put("msgUsuario", mensajes);
			mapResult.put("result", result);

		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AvalesGarantiasFCAction, M:insertaAsignacionEmp");
		}
		return mapResult;
	}
	@Override
	public Map<String, Object> existeAvalGtiaLinea(String empresa,String clave,int empresaA) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		char tipoPersona;
		int cveEmpresa=0;
		try {
			cveEmpresa=Integer.parseInt(separarEmpresa(empresa,'E'));
			tipoPersona=separarEmpresa(empresa,'T').charAt(0);
			int existe=avalesGarantiasFCDao.existeAvalGtiaLinea(cveEmpresa,clave,empresaA,tipoPersona);
			if(existe==1){
				int result = 0;
				result = avalesGarantiasFCDao.deleteAvalada(cveEmpresa,clave,empresaA,tipoPersona);
				if (result > 0) {
					mensajes.add("Registro eliminado correctamente.");
				} else {
					mensajes.add("El registro no se eliminó correctamente.");
				}
				mapResult.put("msgUsuario", mensajes);
				mapResult.put("result", result);
				return mapResult;
			}
			else{
				mapResult.put("result", 1);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AvalesGarantiasFCAction, M:insertaAsignacionEmp");
		}
		return mapResult;
	}
	@Override
	public Map<String, Object> deleteAvalada(String empresa,String clave,int empresaA) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		List<String> mensajes = new ArrayList<String>();
		char tipoPersona;
		int cveEmpresa=0;
		try {
			int result = 0;
			cveEmpresa=Integer.parseInt(separarEmpresa(empresa,'E'));
			tipoPersona=separarEmpresa(empresa,'T').charAt(0);
			result = avalesGarantiasFCDao.deleteAvalada(cveEmpresa,clave,empresaA,tipoPersona);
			if (result > 0) {
				mensajes.add("Registro eliminado correctamente.");
			} else {
				mensajes.add("El registro no se eliminó correctamente.");
			}
			mapResult.put("msgUsuario", mensajes);
			return mapResult;
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AvalesGarantiasFCAction, M:insertaAsignacionEmp");
		}
		return mapResult;
	}

	@Override
	public List<AvalGarantiaDto> reporteAvalesGtiasAvaladas(int vsTipoGtia) {
		return avalesGarantiasFCDao.reporteAvalesGtiasAvaladas(vsTipoGtia);
	}

	@Override
	public HSSFWorkbook excelAvaladas(String avaladas) {
		HSSFWorkbook hb=null;
		Gson gson = new Gson();
		try {
			List<Map<String, String>> paramAvaladas = gson.fromJson(avaladas,
					new TypeToken<ArrayList<Map<String, String>>>() {
			}.getType());
			hb=generarExcel(new String[]{
					"",
					"Empresa Avalada",
					"Aval/Garantía",
					"% Garantía Asignada",
					"Monto Asignado",
					"Banco",
					"Línea",
					"Crédito",
					"Monto Asignado/Avalado",
					"Dispuesto",
					"Disponible",
			}, paramAvaladas, new String[]{
					"color",
					"nomEmpresa",
					"descripcion",
					"garantiaAsignada",
					"montoAsignado",
					"descBanco",
					"idFinanciamiento",
					"credito",
					"montoAvaladoS",
					"dispuesto",
					"montoDisponible",


			});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: reportePersonas");
		}return hb;
	}	
	public static HSSFWorkbook generarExcel(String[] headers,
			List<Map<String, String>> data,
			String[] keys) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		int rowIdx = 0;
		int cellIdx = 0;
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(2);
		//Encabezado
		HSSFRow hssfHeader = sheet.createRow(rowIdx);
		HSSFFont font = wb.createFont();
		HSSFCellStyle cellStyle = wb.createCellStyle();
		font.setCharSet(Font.DEFAULT_CHARSET); 
		font.setBold(true);
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(CellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.BLUE_GREY().getIndex());
		hssfHeader = sheet.createRow(rowIdx);
		HSSFCell celdaTitulo = hssfHeader.createCell(cellIdx);
		celdaTitulo.setCellStyle(cellStyle);
		celdaTitulo.setCellValue(new HSSFRichTextString("EMPRESAS AVALADAS"));
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));
		//Renglón 3 
		rowIdx = 3;
		cellIdx = 0;
		hssfHeader = sheet.createRow(rowIdx);
		//Encabezado tabla
		cellIdx = 0;
		rowIdx = 3;
		hssfHeader = sheet.createRow(rowIdx);
		font = wb.createFont();
		cellStyle = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setFillPattern(CellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.LIGHT_BLUE().getIndex());
		for (String string : headers) {
			HSSFCell hssfCell = hssfHeader.createCell(cellIdx++);
			hssfCell.setCellStyle(cellStyle);
			hssfCell.setCellValue(new HSSFRichTextString(string));
		}
		//Datos tabla
		rowIdx = 4;
		cellStyle = wb.createCellStyle();
		font.setBold(true);
		cellStyle.setFillPattern(CellStyle.FINE_DOTS );
		cellStyle.setFont(font);
		cellStyle.setFillForegroundColor(new HSSFColor.GREY_25_PERCENT().getIndex());
		for (Iterator<Map<String, String>> rows = data.iterator(); rows.hasNext();) {
			Map<String, String> row = rows.next();
			HSSFRow hssfRow = sheet.createRow(rowIdx++);
			cellIdx = 0;
			boolean band=false;
			for (String string : keys) {
				HSSFCell hssfCell = hssfRow.createCell(cellIdx++);
				if(string.equals("color")){

					if(row.get(string)==null){
						band=false;
						hssfCell.setCellStyle(null);
					}
					else{
						band=true;
						hssfCell.setCellStyle(cellStyle);
					}
				}
				else{
					if(band){
						hssfCell.setCellStyle(cellStyle);
					}
					else{
						hssfCell.setCellStyle(null);
					}
					hssfCell.setCellValue(new HSSFRichTextString(row.get(string)));
				}
			}
		}
		wb.setSheetName(0, "Reporte");
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn((short)i);
		}	
		return wb;
	}		
	@Override
	public List<AvalGarantiaDto> reporteAvalesGtiasAvalistas(int vsTipoGtia) {
		return avalesGarantiasFCDao.reporteAvalesGtiasAvalistas(vsTipoGtia);
	}

	@Override
	public HSSFWorkbook excelAvalistas(String avaladas) {
		HSSFWorkbook hb=null;
		Gson gson = new Gson();
		try {
			List<Map<String, String>> paramAvaladas = gson.fromJson(avaladas,
					new TypeToken<ArrayList<Map<String, String>>>() {
			}.getType());
			hb=generarExcel(new String[]{
					"",
					"Empresa Avalada",
					"Aval/Garantía",
					"Monto avalado",
					"Dispuesto",
					"Dispuesto Real",
					"Disponible",
					"Disponible Real",
			}, paramAvaladas, new String[]{
					"color",
					"nomEmpresa",
					"descripcion",
					"montoAvaladoS",
					"dispuesto",
					"dispuestoReal",
					"montoDisponible",
					"disponibleReal",
			});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasBusinessImpl, M: reportePersonas");
		}return hb;
	}	

	/*Este método sirve para obtener la clave de la persona o el tipo de la persona, según se indique en el 
	 parámetro tipo 
	 E: clave de la persona
	 T: tipo de persona, retorna 'F' si es física y 'E' si es moral*/
	public String separarEmpresa(String empresaTipo,char tipo){
		String cadenaSeparada[];
		String cadena="";
		cadenaSeparada=empresaTipo.split("_");
		if(tipo=='T')
			cadena=cadenaSeparada[0];
		else
			cadena=cadenaSeparada[1];
		return cadena;
	}
}
