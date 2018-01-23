package com.webset.set.egresos.business;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.webset.set.egresos.dao.ReporteDeCuposDao;
import com.webset.set.egresos.dto.DataReporteCupos;
import com.webset.set.egresos.dto.ProveedorDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

public class ReporteDeCuposBusiness {
	
	private ReporteDeCuposDao reporteDeCuposDao;
//	private Funciones funciones;
//	private Bitacora bitacora;
	
	public ReporteDeCuposDao getReporteDeCuposDao() {
		return reporteDeCuposDao;
	}

	public void setReporteDeCuposDao(ReporteDeCuposDao reporteDeCuposDao) {
		this.reporteDeCuposDao = reporteDeCuposDao;
	}	
//	public ReporteDeCuposBusiness(){
//		
//		funciones= new Funciones();
//		bitacora = new Bitacora();		
//	}
	
	public List<LlenaComboEmpresasDto> llenaComboEmpresas( int noUsuario )
	{		
		return reporteDeCuposDao.llenaComboEmpresas(noUsuario);
	}

	public List<ProveedorDto> llenaComboProveedor(int noPersona, String nomPersona) {

		List<ProveedorDto> resp = null; 
		
		if( noPersona == 0 ){
			resp =  reporteDeCuposDao.llenaComboProveedor( nomPersona );	
		}
		
		if( nomPersona.equals("") ){
			resp =  reporteDeCuposDao.llenaComboProveedor( noPersona );
		}
		
		return resp;
		
		
		
		
	}

	public List<DataReporteCupos> getReporteCupos(int tipoBusqueda,
			int noEmpresa, int noProveedor, String fecIni, String fecFin) {

		List<DataReporteCupos> data = null;
		
		data = this.reporteDeCuposDao.getReporteCupos(tipoBusqueda, noEmpresa, noProveedor, fecIni, fecFin);

		return data;
		 
	}

	public JRDataSource getReporteCuposV2(int tipoBusqueda, int noEmpresa,			
			int noProveedor, String fecIni, String fecFin) {
		
		JRMapArrayDataSource jrDataSource = null;
	
		List<Map<String, Object>> listReport = new ArrayList<Map<String, Object>>();
        
    	listReport = this.reporteDeCuposDao.getReporteCuposV2(tipoBusqueda, noEmpresa, noProveedor, fecIni, fecFin);
    	
        jrDataSource = new JRMapArrayDataSource( listReport.toArray() );
            
        return jrDataSource;

	}

}//End class ReporteDeCuposBusiness
