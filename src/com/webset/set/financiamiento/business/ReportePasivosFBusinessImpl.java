package com.webset.set.financiamiento.business;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.jdbc.core.JdbcTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.financiamiento.dao.ReportePasivosFDao;
import com.webset.set.financiamiento.dto.AvalGarantiaDto;
import com.webset.set.financiamiento.dto.ReportePasivosFDto;
import com.webset.set.financiamiento.service.ReportePasivosFService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public class ReportePasivosFBusinessImpl implements ReportePasivosFService {
	private Bitacora bitacora = new Bitacora();
	private ReportePasivosFDao reportePasivosFDao;
	private ConsultasGenerales consultasGenerales;
	private JdbcTemplate jdbcTemplate;

	public ReportePasivosFDao getReportePasivosFDao() {
		return reportePasivosFDao;
	}

	public void setReportePasivosFDao(ReportePasivosFDao reportePasivosFDao) {
		this.reportePasivosFDao = reportePasivosFDao;
	}

	@Override
	public List<LlenaComboGralDto> obtenerEmpresas(int plUsuario,String psMenu) {
		return reportePasivosFDao.obtenerEmpresas(plUsuario, psMenu);
	}

	@Override
	public List<ReportePasivosFDto> obtenerDivisaCreditos(int plUsuario, String psMenu) {
		return reportePasivosFDao.obtenerDivisaCreditos(plUsuario, psMenu);
	}

	@Override
	public List<ReportePasivosFDto> obtenerPasivosFinancieros(int noEmpresa, String json) {
		List<ReportePasivosFDto> lista= new ArrayList<ReportePasivosFDto>();
		List<AvalGarantiaDto> listaAvales;
		String avales="", descripcion="";
		lista=reportePasivosFDao.obtenerPasivosFinancieros(noEmpresa, json);
		for (int i = 0; i < lista.size(); i++) {
			avales="";descripcion="";
			if(!lista.get(i).getLinea().equals("")){
				System.out.println("no empresa "+lista.get(i).getNoEmpresaAux()+" disposicion"+lista.get(i).getIdDisposicion()+" linea"+lista.get(i).getLinea());
				listaAvales= new ArrayList<AvalGarantiaDto>();
				listaAvales=reportePasivosFDao.obtenerMontoDispuestoAvalada(lista.get(i).getLinea(),Integer.parseInt(lista.get(i).getIdDisposicion()), Integer.parseInt(lista.get(i).getNoEmpresaAux()));
				if(!listaAvales.isEmpty()){
					for (int j = 0; j < listaAvales.size(); j++) {
						if(j==listaAvales.size()-1){
							avales+=listaAvales.get(j).getNomEmpresa();
							descripcion+=listaAvales.get(j).getDescripcion();
						}
						else{
							avales+=listaAvales.get(j).getNomEmpresa()+"--";
							descripcion+=listaAvales.get(j).getDescripcion()+"--";
						}
					}
				}
				else{
					avales="NO TIENE AVALES / OBLIGADOS ASIGNADOS.";
					descripcion="";
				}
				lista.get(i).setAvales(avales);
				lista.get(i).setDescripcionAval(descripcion);
			}
			else{
				lista.get(i).setAvales("");
				lista.get(i).setDescripcionAval("");
			}
		}
		return lista;
	}

	@Override
	public HSSFWorkbook reportePasivosFinancieros(String pasivos) {
		HSSFWorkbook hb=null;
		Gson gson = new Gson();
		try {
			List<Map<String, String>> paramPasivos = gson.fromJson(pasivos,
					new TypeToken<ArrayList<Map<String, String>>>() {
			}.getType());
			hb=generarExcel(new String[]{
					"Banco",
					"Importe",
					"Tasa Ref.",
					"Valor Tasa",
					"Inicio",
					"Plazo",
					"Vencimiento",
					"Monto C/Plazo",
					"Monto L/Plazo",
					"Periodicidad pagos",
					"Línea Autorizada",
					"Línea Disponible",
					"Tipo Uso",
					"Uso Específico",
					"Garantías",
					"Nombre del Aval / Obligado",
					"Descripción Aval / Obligado"
			}, paramPasivos, new String[]{
					"banco",
					"importe",
					"tasaRef",
					"valorTasa",
					"inicio",
					"plazo",
					"vencimiento",
					"montoCortoPlazo",
					"montoLargoPlazo",
					"periodicidadPagos",
					"lineaAutorizada",
					"lineaDisponible",
					"idTipoFinanciamiento",
					"destinoRecurso",
					"garantias",
					"avales",
					//	"descripcionAval",
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
		String[] renglon;
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

		HSSFCellStyle cellStyle2 = wb.createCellStyle();
		font.setColor(new HSSFColor.WHITE().getIndex());
		cellStyle2.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle2.setFillPattern(CellStyle.FINE_DOTS );
		cellStyle2.setFont(font);
		cellStyle2.setFillForegroundColor(new HSSFColor.CORNFLOWER_BLUE().getIndex());


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
		for (Iterator<Map<String, String>> rows = data.iterator(); rows.hasNext();) {
			Map<String, String> row = rows.next();
			HSSFRow hssfRow = sheet.createRow(rowIdx++);
			cellIdx = 0;
			for (String string : keys) {
				HSSFRow hssfRow2;
				int posicion=cellIdx;
				if(string.equals("avales")){
					if(row.get(string)!=null){
						renglon = row.get(string).split("__");
						if(renglon.length>0){
							String[] avales=renglon[0].split("--");
							//if(renglon.length>1)
							String[] descripcionAval;

							for (int i = 0; i < avales.length; i++) {
								if(i==0){
									HSSFCell hssfCell = hssfRow.createCell(posicion);
									hssfCell.setCellValue(new HSSFRichTextString(avales[i]));
									if(renglon.length>1){
										 descripcionAval=renglon[1].split("--");
										HSSFCell hssfCell2 = hssfRow.createCell(posicion+1);
										hssfCell2.setCellValue(new HSSFRichTextString(descripcionAval[i]));
									}
								}
								else{
									hssfRow2 = sheet.createRow(rowIdx++);
									HSSFCell hssfCell= hssfRow2.createCell(posicion);
									hssfCell.setCellValue(new HSSFRichTextString(avales[i]));
									if(renglon.length>1){
									 descripcionAval=renglon[1].split("--");
										HSSFCell hssfCell2 = hssfRow2.createCell(posicion+1);
										hssfCell2.setCellValue(new HSSFRichTextString(descripcionAval[i]));
									}
								}
							}
						}
					}
					else{
						HSSFCell hssfCell = hssfRow.createCell(cellIdx++);
						hssfCell.setCellValue(new HSSFRichTextString(row.get(string)));
					}
				}
				else{
					HSSFCell hssfCell = hssfRow.createCell(cellIdx++);
					hssfCell.setCellValue(new HSSFRichTextString(row.get(string)));
				}

			}
		}
		font.setBold(true);
		wb.setSheetName(0, "Pasivos Financieros");
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn((short)i);
		}	
		return wb;
	}		

}
