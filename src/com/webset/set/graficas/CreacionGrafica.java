package com.webset.set.graficas;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleEdge;

import com.webset.set.utilerias.ConstantesSet;

public class CreacionGrafica {
	//Atributos de la clase.
	java.text.SimpleDateFormat sdf=new java.text.SimpleDateFormat("dd/MM/yyyy");
	
	public static void main(String[] args)
	{
		Map<String, Double> datos = new HashMap<String, Double>();
		Map<String, Double> datos2 = new HashMap<String, Double>();
		Map<String, Double> datos3 = new HashMap<String, Double>();
		
		datos.put("Cheques", new Double(230.4));
		datos.put("Transferencias MB", new Double(45.3));
		datos.put("Transferencias IB", new Double(120));
		datos.put("Compras de Transfer", new Double(195.3));
		
		datos2.put("Julio", new Double(55));
		datos2.put("Agosto", new Double(45));
		datos2.put("Septiembre", new Double(120));
		datos2.put("Octubre", new Double(80));
		datos2.put("Noviembre", new Double(22));
		datos2.put("Diciembre", new Double(195));
		
		datos3.put("Julio", new Double(95200));
		datos3.put("Agosto", new Double(60500));
		datos3.put("Septiembre", new Double(255460));
		datos3.put("Octubre", new Double(180500));
		datos3.put("Noviembre", new Double(22000));
		datos3.put("Diciembre", new Double(425000));
		
		CreacionGrafica instancia = new CreacionGrafica();
		//instancia.crearGraficaPie("lamodio", "FormasPago", "Formas de Pago", datos);
		instancia.crearGraficaBarrasFlujo("lamodio", "Tiempo", "Pagos por mes", "Importe", datos2, true, 1);
		instancia.crearGraficaLineasFlujo("lamodio", "Tiempo", "Importe por Ingresos", "Importe", datos3, true, 1);
	}
	public String crearGraficaPie(String usuario, String tipo, String titulo, Map<String, Double> datos)
	{
		String nombreArchivo = null;
		String dato = new String();
		DefaultPieDataset datosGrafica = new DefaultPieDataset();
		for (java.util.Iterator<String> iter = datos.keySet().iterator(); iter.hasNext();){
			dato = iter.next();
			datosGrafica.setValue(dato, datos.get(dato));
		}
		JFreeChart grafica = ChartFactory.createPieChart3D(titulo, datosGrafica, false, false, false);
		//grafica.setBackgroundPaint(new Color(250, 200, 100));
		
		PiePlot3D p = (PiePlot3D) grafica.getPlot();
        p.setForegroundAlpha(0.5f);
        p.setBackgroundAlpha(0.2f);
		
        PieSectionLabelGenerator generator = new StandardPieSectionLabelGenerator("{0} = {1} ({2})",Locale.ENGLISH);
        p.setLabelGenerator(generator);
        
		try{
			nombreArchivo = ConstantesSet.RUTA + usuario + tipo + "PC.jpg";
			File archivoGrafica = new File(nombreArchivo);
			System.out.println(archivoGrafica.getAbsolutePath());
			ChartUtilities.saveChartAsJPEG(archivoGrafica, grafica, 600, 500);
		}
		catch (IOException e){
			System.err.println("error al guardar la graficia: " + e.toString());
		}
		
		return nombreArchivo;
	}
	
	public String crearGraficaBarras(String usuario, String tipo, String titulo, String cantidades, Map<String, Double> datos, boolean vertical)
	{
		String nombreArchivo = null;
		String dato = new String();
		String sAux = new String();
		
		DefaultCategoryDataset datosGrafica = new DefaultCategoryDataset();
		for (java.util.Iterator<String> iter = datos.keySet().iterator(); iter.hasNext();){
			dato = iter.next();
			if(dato.substring(0, 3).equals("201")) {
				sAux = dato.substring(8,10)+"/"+dato.substring(5,7)+"/"+dato.substring(0,4);
				datosGrafica.setValue(datos.get(dato), tipo, sAux);
			}else
				datosGrafica.setValue(datos.get(dato), tipo, dato);
		}
		JFreeChart grafica = ChartFactory.createBarChart3D(titulo, "", cantidades, datosGrafica, (vertical)?PlotOrientation.VERTICAL:PlotOrientation.HORIZONTAL, false, false, false);
		//grafica.setBackgroundPaint(new Color(250, 200, 100));
		
		final CategoryPlot cPlot = grafica.getCategoryPlot();
		cPlot.setWeight(5);
		//cPlot.setBackgroundPaint(new Color(20, 200, 100));
		CategoryItemRenderer lineAndShapeRenderer;
		lineAndShapeRenderer= (CategoryItemRenderer) cPlot.getRenderer();
        lineAndShapeRenderer.setBaseItemLabelsVisible(Boolean.TRUE);
        lineAndShapeRenderer.setBaseItemLabelGenerator((CategoryItemLabelGenerator) new StandardCategoryItemLabelGenerator());
        
        //ItemLabelPosition itemLabelPosition = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.CENTER_LEFT, TextAnchor.CENTER_LEFT, -Math.PI / 2);
        //lineAndShapeRenderer.setBasePositiveItemLabelPosition(itemLabelPosition);
		CategoryAxis axis = cPlot.getDomainAxis();
		axis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        
        //lineAndShapeRenderer.setPaint(new Color(250, 200, 200));
		cPlot.setRenderer(lineAndShapeRenderer);
		
		try{
			nombreArchivo = ConstantesSet.RUTA + usuario + tipo + "BG.jpg";
			File archivoGrafica = new File(nombreArchivo);
			System.out.println(archivoGrafica.getAbsolutePath());
			ChartUtilities.saveChartAsJPEG(archivoGrafica, grafica, 600, 500);
		}
		catch (IOException e){
			System.err.println("error al guardar la graficia: " + e.toString());
		}
		
		return nombreArchivo;
	}
	public String crearGraficaBarrasFlujo(String usuario, String tipo, String titulo, String cantidades, Map<String, Double> datos, boolean vertical, int idReporte)
	{
		String nombreArchivo = null;
		String dato = new String();
		String sAux = new String();
		DefaultCategoryDataset datosGrafica = new DefaultCategoryDataset();
		
		System.out.println("Datos"+ datos);
		
		for(Iterator<String> iter = datos.keySet().iterator(); iter.hasNext();) {
			dato = iter.next();
			
			if(idReporte==1)
			{
				sAux = dato.substring(8,10)+"/"+dato.substring(5,7)+"/"+dato.substring(0,4);
				
				if(dato.substring(10).equals("I")){
					if(null==datos.get(dato.substring(0,10)+"E"))
						datosGrafica.setValue(0, "Egresos", sAux);
					
					datosGrafica.setValue(datos.get(dato), "Ingresos", sAux); //dato.substring(0,10)
				}
				else{
					if(null==datos.get(dato.substring(0,10)+"I"))
						datosGrafica.setValue(0, "Ingresos", sAux);
					
					datosGrafica.setValue(datos.get(dato), "Egresos", sAux);
				}
			}
			else if(idReporte==2)
			{
				sAux = dato.substring(2,8)+" "+dato.substring(0,2);
				
				if(dato.substring(8).equals("I")){
					if(null==datos.get(dato.substring(0,8)+"E"))
						datosGrafica.setValue(0, "Egresos", sAux);
					
					datosGrafica.setValue(datos.get(dato), "Ingresos", sAux);
				}
				else{
					if(null==datos.get(dato.substring(0,8)+"I"))
						datosGrafica.setValue(0, "Ingresos", sAux);
					
					datosGrafica.setValue(datos.get(dato), "Egresos", sAux);
				}
			}
			else if(idReporte==3)
			{
				sAux = dato.substring(2, dato.length()-1);
				
				if(dato.substring(dato.length()-1).equals("I")){
					if(null==datos.get(dato.substring(0,dato.length()-1)+"E"))
						datosGrafica.setValue(0, "Egresos", sAux);
					
					datosGrafica.setValue(datos.get(dato), "Ingresos", sAux);
				}
				else{
					if(null==datos.get(dato.substring(0,dato.length()-1)+"I"))
						datosGrafica.setValue(0, "Ingresos", sAux);
					
					datosGrafica.setValue(datos.get(dato), "Egresos", sAux);
				}
			}
		}
		
		JFreeChart grafica = ChartFactory.createBarChart3D(titulo, "", "cantidad", datosGrafica, (vertical)?PlotOrientation.VERTICAL:PlotOrientation.HORIZONTAL, false, false, false);
		//grafica.setBackgroundPaint(new Color(250, 200, 100));
		
		final CategoryPlot cPlot = grafica.getCategoryPlot();
		cPlot.setWeight(5);
		//cPlot.setBackgroundPaint(new Color(20, 200, 100));
		CategoryItemRenderer lineAndShapeRenderer;
		lineAndShapeRenderer= (CategoryItemRenderer) cPlot.getRenderer();
        lineAndShapeRenderer.setBaseItemLabelsVisible(Boolean.TRUE);
        lineAndShapeRenderer.setBaseItemLabelGenerator((CategoryItemLabelGenerator) new StandardCategoryItemLabelGenerator());
        
        LegendTitle legend = new LegendTitle(cPlot);
        legend.setPosition(RectangleEdge.BOTTOM);
        grafica.addSubtitle(legend);
        
        //ItemLabelPosition itemLabelPosition = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.CENTER_LEFT, TextAnchor.CENTER_LEFT, -Math.PI / 2);
        //lineAndShapeRenderer.setBasePositiveItemLabelPosition(itemLabelPosition);
		CategoryAxis axis = cPlot.getDomainAxis();
		axis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        
        //lineAndShapeRenderer.setPaint(new Color(250, 200, 200));
		cPlot.setRenderer(lineAndShapeRenderer);
		
		try{
			nombreArchivo = ConstantesSet.RUTA + usuario + tipo + "BG.jpg";
			File archivoGrafica = new File(nombreArchivo);
			System.out.println(archivoGrafica.getAbsolutePath());
			ChartUtilities.saveChartAsJPEG(archivoGrafica, grafica, 600, 500);
		}
		catch (IOException e){
			System.err.println("error al guardar la graficia: " + e.toString());
		}
		
		return nombreArchivo;
	}
	public String crearGraficaLineas(String usuario, String tipo, String titulo, String cantidades, Map<String, Double> datos, boolean vertical)
	{
		String nombreArchivo = null;
		String dato = new String();
		String sAux = new String();
		
		DefaultCategoryDataset datosGrafica = new DefaultCategoryDataset();
		for (java.util.Iterator<String> iter = datos.keySet().iterator(); iter.hasNext(); ){
			dato = iter.next();
			if(dato.substring(0, 3).equals("201")) {
				sAux = dato.substring(8,10)+"/"+dato.substring(5,7)+"/"+dato.substring(0,4);
				datosGrafica.setValue(datos.get(dato), tipo, sAux);
			}else
				datosGrafica.setValue(datos.get(dato), tipo, dato);
		}
		JFreeChart grafica = ChartFactory.createLineChart(titulo, "", cantidades, datosGrafica, (vertical)?PlotOrientation.VERTICAL:PlotOrientation.HORIZONTAL, false, true, true);
		//grafica.setBackgroundPaint(new Color(250, 200, 100));
		
		final CategoryPlot cPlot = grafica.getCategoryPlot();
		//cPlot.setBackgroundPaint(new Color(20, 200, 100));
		cPlot.setWeight(5);
		
		LineAndShapeRenderer lineAndShapeRenderer;
		lineAndShapeRenderer= (LineAndShapeRenderer) cPlot.getRenderer();
        lineAndShapeRenderer.setBaseItemLabelsVisible(Boolean.TRUE);
        
        //ItemLabelPosition itemLabelPosition = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.CENTER_LEFT, TextAnchor.CENTER_LEFT, -Math.PI / 2);
        //lineAndShapeRenderer.setBasePositiveItemLabelPosition(itemLabelPosition);
		CategoryAxis axis = cPlot.getDomainAxis();
		axis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        
        lineAndShapeRenderer.setBaseItemLabelGenerator((CategoryItemLabelGenerator) new StandardCategoryItemLabelGenerator());
		cPlot.setRenderer(lineAndShapeRenderer);
		
		try{
			nombreArchivo = ConstantesSet.RUTA + usuario + tipo + "LG.jpg";
			File archivoGrafica = new File(nombreArchivo);
			System.out.println(archivoGrafica.getAbsolutePath());
			ChartUtilities.saveChartAsJPEG(archivoGrafica, grafica, 600, 500);
		}
		catch (IOException e){
			System.err.println("error al guardar la graficia: " + e.toString());
		}
		
		return nombreArchivo;
	}
	
	public String crearGraficaLineasFlujo(String usuario, String tipo, String titulo, String cantidades, Map<String, Double> datos, boolean vertical, int idReporte)
	{
		String nombreArchivo = null;
		String dato = new String();
		String sAux = new String();
		
		DefaultCategoryDataset datosGrafica = new DefaultCategoryDataset();
		
		System.out.println("Datos"+ datos);
		for(Iterator<String> iter = datos.keySet().iterator(); iter.hasNext();) {
			dato = iter.next();
			
			if(idReporte==1)
			{
				sAux = dato.substring(8,10)+"/"+dato.substring(5,7)+"/"+dato.substring(0,4);
				
				if(dato.substring(10).equals("I")){
					if(null==datos.get(dato.substring(0,10)+"E"))
						datosGrafica.setValue(0, "Egresos", sAux);
					
					datosGrafica.setValue(datos.get(dato), "Ingresos", sAux);
				}
				else{
					if(null==datos.get(dato.substring(0,10)+"I"))
						datosGrafica.setValue(0, "Ingresos", sAux);
					
					datosGrafica.setValue(datos.get(dato), "Egresos", sAux);
				}
			}
			else if(idReporte==2)
			{
				sAux = dato.substring(2,8)+" "+dato.substring(0,2);
				
				if(dato.substring(8).equals("I")){
					if(null==datos.get(dato.substring(0,8)+"E"))
						datosGrafica.setValue(0, "Egresos", sAux);
					
					datosGrafica.setValue(datos.get(dato), "Ingresos", sAux);
				}
				else{
					if(null==datos.get(dato.substring(0,8)+"I"))
						datosGrafica.setValue(0, "Ingresos", sAux);
					
					datosGrafica.setValue(datos.get(dato), "Egresos", sAux);
				}
			}
			else if(idReporte==3)
			{
				sAux = dato.substring(2, dato.length()-1);
				
				if(dato.substring(dato.length()-1).equals("I")){
					if(null==datos.get(dato.substring(0,dato.length()-1)+"E"))
						datosGrafica.setValue(0, "Egresos", sAux);
					
					datosGrafica.setValue(datos.get(dato), "Ingresos", sAux);
				}
				else{
					if(null==datos.get(dato.substring(0,dato.length()-1)+"I"))
						datosGrafica.setValue(0, "Ingresos", sAux);
					
					datosGrafica.setValue(datos.get(dato), "Egresos", sAux);
				}
			}
		}
		
		JFreeChart grafica = ChartFactory.createLineChart(titulo, "", cantidades, datosGrafica, (vertical)?PlotOrientation.VERTICAL:PlotOrientation.HORIZONTAL, false, true, true);
		//grafica.setBackgroundPaint(new Color(250, 200, 100));
		
		final CategoryPlot cPlot = grafica.getCategoryPlot();
		//cPlot.setBackgroundPaint(new Color(20, 200, 100));
		cPlot.setWeight(5);
		
		LineAndShapeRenderer lineAndShapeRenderer;
		lineAndShapeRenderer= (LineAndShapeRenderer) cPlot.getRenderer();
        lineAndShapeRenderer.setBaseItemLabelsVisible(Boolean.TRUE);
        
        LegendTitle legend = new LegendTitle(cPlot);
        legend.setPosition(RectangleEdge.BOTTOM);
        grafica.addSubtitle(legend);
        //ChartUtilities.applyCurrentTheme(grafica);
        //ItemLabelPosition itemLabelPosition = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.CENTER_LEFT, TextAnchor.CENTER_LEFT, -Math.PI / 2);
        //lineAndShapeRenderer.setBasePositiveItemLabelPosition(itemLabelPosition);
		CategoryAxis axis = cPlot.getDomainAxis();
		axis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        lineAndShapeRenderer.setBaseItemLabelGenerator((CategoryItemLabelGenerator) new StandardCategoryItemLabelGenerator());
		cPlot.setRenderer(lineAndShapeRenderer);
		
		try{
			nombreArchivo = ConstantesSet.RUTA + usuario + tipo + "LG.jpg";
			File archivoGrafica = new File(nombreArchivo);
			System.out.println(archivoGrafica.getAbsolutePath());
			ChartUtilities.saveChartAsJPEG(archivoGrafica, grafica, 600, 500);
		}
		catch (IOException e){
			System.err.println("error al guardar la graficia: " + e.toString());
		}
		
		return nombreArchivo;
	}
}
