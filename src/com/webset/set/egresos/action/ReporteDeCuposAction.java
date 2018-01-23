package com.webset.set.egresos.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.egresos.business.ReporteDeCuposBusiness;
import com.webset.set.egresos.dto.DataReporteCupos;
import com.webset.set.egresos.dto.ProveedorDto;
import com.webset.set.graficas.CreacionGrafica;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.utils.tools.Utilerias;

import net.sf.jasperreports.engine.JRDataSource;

public class ReporteDeCuposAction {
	
	private Contexto contex;
	//private ReporteDeCuposBusiness reporteDeCuposBusiness;
	
	
	public ReporteDeCuposAction(){
		
		contex = new Contexto();
		
	}
	
	@DirectMethod
	public List<LlenaComboEmpresasDto> llenaComboEmpresas( int noUsuario )
	{		
		
		ReporteDeCuposBusiness reporteDeCuposBusiness = (ReporteDeCuposBusiness) contex.obtenerBean("reporteDeCuposBussines");
		return reporteDeCuposBusiness.llenaComboEmpresas( noUsuario );
	}
	
	
	@DirectMethod
	public List<ProveedorDto> llenaComboProveedor( int noPersona, String nomPersona )
	{		
		ReporteDeCuposBusiness reporteDeCuposBusiness = (ReporteDeCuposBusiness) contex.obtenerBean("reporteDeCuposBussines");
		return reporteDeCuposBusiness.llenaComboProveedor( noPersona, nomPersona );
	}
	
	@DirectMethod
	public List<DataReporteCupos> getReporteCupos(int tipoBusqueda, int noEmpresa,
												  int noProveedor, String fecIni,
												  String fecFin, int iUserId)
	{
		List<DataReporteCupos> listReporteCupos = new ArrayList<DataReporteCupos>();
		
		try
		{
			if (Utilerias.haveSession(WebContextManager.get())) {
			ReporteDeCuposBusiness reporteDeCuposBusiness = (ReporteDeCuposBusiness) contex.obtenerBean("reporteDeCuposBussines");
			listReporteCupos = reporteDeCuposBusiness.getReporteCupos(tipoBusqueda, noEmpresa,noProveedor, fecIni, fecFin);
			
			if(listReporteCupos!=null && listReporteCupos.size()>0)
			{
				//Nombre de la grafica.
				String sName=tipoBusqueda+""+noEmpresa+""+noProveedor;//+fecIni+fecFin;
				if(fecIni!=null && !fecIni.equals("")) {
					fecIni = fecIni.trim();
					fecFin = fecFin.trim();
					sName += (!fecIni.equals("")) ? fecIni.replaceAll("/", "") : "";
					sName += (!fecFin.equals("")) ? fecFin.replaceAll("/", "") : "";
				}
				
				//Grafica: FormaPago - NumeroMovimientosFormaPago.
				Map<String, Double> datosPie = new HashMap<String, Double>();
				//Grafica: Banco - Importe Total.
				Map<String, Double> datosBarras = new HashMap<String, Double>();
				//Cantidad movimientos por forma de pago.
				double dNumMovs=0;
				
				for(int i=0; i<listReporteCupos.size(); i++){
					DataReporteCupos dataFormaPago = (DataReporteCupos)listReporteCupos.get(i);
					
					if(datosPie.containsKey(dataFormaPago.getDescFormaPago())){
						dNumMovs = datosPie.get(dataFormaPago.getDescFormaPago());
						datosPie.remove(dataFormaPago.getDescFormaPago());
						datosPie.put(dataFormaPago.getDescFormaPago(), (dNumMovs+1));
					}
					else {
						dNumMovs=1;
						datosPie.put(dataFormaPago.getDescFormaPago(), dNumMovs);
					}
					
					if(datosBarras.containsKey(dataFormaPago.getDescBanco())){
						double dImporte = datosBarras.get(dataFormaPago.getDescBanco());
						datosBarras.remove(dataFormaPago.getDescBanco());
						datosBarras.put(dataFormaPago.getDescBanco(), (dataFormaPago.getImporte()+dImporte));
					}
					else {
						datosBarras.put(dataFormaPago.getDescBanco(), dataFormaPago.getImporte());
					}
				}
				
				CreacionGrafica cg = new CreacionGrafica();
				cg.crearGraficaPie   (iUserId+"", "PropuestasPagadas"+sName, "Movimientos por Forma Pago", datosPie);
				cg.crearGraficaBarras(iUserId+"", "PropuestasPagadas"+sName, "Importe por Banco", "Importe", datosBarras, true);
				cg.crearGraficaLineas(iUserId+"", "PropuestasPagadas"+sName, "Importe por Banco", "Importe", datosBarras, true);
				//Fin graficas
			}
			}
		}
		catch(Exception e){
			System.err.println(e);
		}
		return listReporteCupos;
	}
	
	@DirectMethod
	public JRDataSource obtenerReporte(Map<String, Object> datos, ServletContext context) {
		
		int tipoBusqueda; 
		int noEmpresa;
		int noProveedor; 
		String fecIni;
		String fecFin;
		
		ReporteDeCuposBusiness reporteDeCuposBusiness = (ReporteDeCuposBusiness) contex.obtenerBean("reporteDeCuposBussines",context);
		
		JRDataSource jrDataSource = null;
		
		tipoBusqueda=datos.get("tipoBusqueda") != null ? Integer.parseInt(datos.get("tipoBusqueda").toString()) : 0;	
		noEmpresa=datos.get("noEmpresa") != null ? Integer.parseInt(datos.get("noEmpresa").toString()) : 0;
		noProveedor=datos.get("noProveedor") != null ? Integer.parseInt(datos.get("noProveedor").toString()) : 0;
		fecIni= datos.get("fecIni") != null ? datos.get("fecIni").toString() : "";
		fecFin= datos.get("fecFin") != null ? datos.get("fecFin").toString() : "";
		
		jrDataSource = reporteDeCuposBusiness.getReporteCuposV2(tipoBusqueda, noEmpresa, noProveedor, fecIni, fecFin);
		
		return jrDataSource;
		
	}


}//End class
