package com.webset.set.utilerias;
 
 import java.io.*;

 import org.apache.poi.hssf.usermodel.HSSFCell;
 import org.apache.poi.hssf.usermodel.HSSFSheet;
 import org.apache.poi.hssf.usermodel.HSSFWorkbook;
 import org.apache.poi.hssf.usermodel.HSSFRow;
 import org.apache.poi.hssf.usermodel.HSSFCellStyle;
 import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author crgarcia
 * @since 09/September/2011
 */
public class ExportarDatos {
	Funciones funciones = new Funciones();
	
	/**
	 * Este método realiza la conversión de una cadena de datos en formato JSON
	 * a un archivo de tipo excel, el nombre de las columnas creadas en el js
	 * debe precedir de la letra "C" + el numero de columna, para poder realizar el recorrido
	 * de la lista de mapa, asi podemos identificar las columnas.
	 * (REFERENCIAS: ver ConsultaDeMovimiento.js, buscar : "NS.exportarDatosExcel")
	 * @param sDatos : contiene la estructura de datos de tipo JSON
	 * @param iNoColumn : tiene el número de columnas del grid
	 * @param sForma : este parametro lo utilizamos para el nombre de la hoja y el archivo
	 * @param sRuta : contiene la ruta para guardar el archivo en el servidor, es recibido del configura_set(3004)
	 * @return
	 * 
	 */
	@SuppressWarnings({ "static-access", "deprecation" })
	public String exportarDatosExcel(String sDatos, int iNoColumn, String sForma, String sRuta){
		Gson gSon = new Gson();
		List<Map<String, String>> gListDatos = gSon.fromJson(sDatos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		String sNomArch = "";
		try {
				sNomArch = sForma + funciones.generarCadenaClave() + ".xls" .trim();

				//Creamos el archivo de excel
			    HSSFWorkbook wb = new HSSFWorkbook();
			    FileOutputStream fileOut = new FileOutputStream(sRuta + sNomArch);
				//Creamos una hoja en el libro de excel
			    HSSFSheet sheet1 = wb.createSheet(sForma);
			
			    //Creamos el Estilo del Font a Utilizar en el nombre de las columnas
		        HSSFFont font = wb.createFont();
		        font.setFontHeightInPoints((short)10);
		        font.setFontName("Arial");
		        //font.setItalic(true);
		        //font.setStrikeout(true);
		        //font.setColor(new HSSFColor.BLUE_GREY().getIndex());
		        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		        
		        HSSFCellStyle style = wb.createCellStyle();
		        style.setFont(font);
		        style.setFillForegroundColor(new HSSFColor.BLUE_GREY().index);
		        style.setFillBackgroundColor(new HSSFColor.BLUE_GREY().index);
		        style.setFillPattern(style.SOLID_FOREGROUND);
		        
		        HSSFCellStyle style2 = wb.createCellStyle();
		        style2.setFont(font);
		        style2.setFillBackgroundColor(new HSSFColor.GREY_25_PERCENT().index);
		        style2.setFillForegroundColor(new HSSFColor.GREY_25_PERCENT().index);
		        style2.setFillPattern(style2.SOLID_FOREGROUND);
		        //style2
		        
		        HSSFRow row = null ;  //Se crea un mismo objeto para la fila y la celda
		        HSSFCell cell = null;
		        
		        row = sheet1.createRow((short)0);
			          
		        for (int m = 0; m < iNoColumn; m ++)// crea las celdas de acuerdo al numero de columnas
		        {
		            String nombreColumna = gListDatos.get(0).get("C" + m); //obtenemos el nombre de la columna
		            cell = row.createCell((short)m); 
		            cell.setCellValue(nombreColumna);//se agrega el nombre de la columna
		            cell.setCellStyle(style); // Asigna el estilo a la columna
		            sheet1.autoSizeColumn((short)m);
		        }
			                
		        int i = 1;
		        while (i < gListDatos.size()) 
		        {
		            row = sheet1.createRow((short)i); //Se crea la fila
		            for (int q = 0;  q < iNoColumn; q ++) //Se generan las columnas
		            {
		            	cell = row.createCell((short)q); 
		            	//Creamos la celda y asignamos un valor
		            	cell.setCellValue(gListDatos.get(i).get("C" + q));
		            	if(i % 2 == 0)
		            		cell.setCellStyle(style2); // Asigna el estilo a la columna
		            	sheet1.autoSizeColumn((short)q);
		            }
		            i++;
		        }    
				//Escribimos y cerramos el archivo de excel
				wb.write(fileOut);
				fileOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sNomArch;
	}
	       
}
