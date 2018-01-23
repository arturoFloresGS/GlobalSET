package com.webset.set.financiamiento.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.gson.Gson;
import com.webset.set.financiamiento.dao.AlertaVencimientoDao;
import com.webset.set.financiamiento.dto.ControlPagosPasivos;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.LlenarConsultaFws;
import com.webset.utils.tools.Utilerias;

import net.sf.jasperreports.engine.JRDataSource;
public class AlertaVencimientoDaoImpl implements AlertaVencimientoDao {
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora=new Bitacora();
	ConsultasGenerales consultasGenerales;
	Funciones funciones= new Funciones();
	GlobalSingleton globalSingleton;
	
	
	public void setDataSource(DataSource dataSource)
	{
		try
		{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:financiamieno, C: AlertaVencimientoDaoImpl, M:setDataSource");
		}
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	public Bitacora getBitacora() {
		return bitacora;
	}
	public void setBitacora(Bitacora bitacora) {
		this.bitacora = bitacora;
	}
	public ConsultasGenerales getConsultasGenerales() {
		return consultasGenerales;
	}
	public void setConsultasGenerales(ConsultasGenerales consultasGenerales) {
		this.consultasGenerales = consultasGenerales;
	}
	public Funciones getFunciones() {
		return funciones;
	}
	public void setFunciones(Funciones funciones) {
		this.funciones = funciones;
	}
	public GlobalSingleton getGlobalSingleton() {
		return globalSingleton;
	}
	public void setGlobalSingleton(GlobalSingleton globalSingleton) {
		this.globalSingleton = globalSingleton;
	}

	@Override
	public List<LlenaComboGralDto> consultarBanco(int usuario) {
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listListas = new ArrayList<LlenaComboGralDto>();
		try
		{
			
					 sbSql.append("\n   SELECT ca.id_arrendadora as ID, ca.desc_arrendadora as Describ ");
					 sbSql.append("\n   	FROM  cat_contrato_credito ccc, cat_disposicion_credito cdc, cat_amortizacion_credito cac, cat_arrendadoras ca,"); 
					 sbSql.append("\n cat_tipo_contrato ctc ");
					  sbSql.append("\n    WHERE ccc.id_financiamiento = cdc.id_financiamiento"); 
					    sbSql.append("\n   And cdc.id_financiamiento = cac.id_contrato ");
					        sbSql.append("\n    And cdc.id_disposicion = cac.id_disposicion ");
					       sbSql.append("\n   And ccc.id_banco_prestamo = ca.id_arrendadora ");
					            sbSql.append("\n   And ccc.id_tipo_financiamiento = ctc.id_tipo_contrato ");
					        sbSql.append("\n   And ccc.no_empresa in (SELECT   e.no_empresa FROM empresa e, usuario_empresa ue"); 
					     sbSql.append("\n   WHERE    e.no_empresa = ue.no_empresa ");
					     sbSql.append("\n   And ue.no_usuario = "+usuario+") ");
					      sbSql.append("\n   And ccc.estatus = 'A' ");
					        sbSql.append("\n   And cac.estatus = 'A' ");
					 sbSql.append("\n   And ca.tipo_financiamiento = 'A' ");
					 sbSql.append("\n   And ca.tipo_financiamiento <> 'A'"); 
					  sbSql.append("\n   UNION ALL ");
					      sbSql.append("\n   SELECT distinct cb.id_banco as ID, cb.desc_banco as Describ ");
					     sbSql.append("\n   FROM  cat_contrato_credito ccc, cat_disposicion_credito cdc, cat_amortizacion_credito cac,cat_banco cb,"); 
					     sbSql.append("\n   cat_tipo_contrato ctc ");
					      sbSql.append("\n   WHERE ccc.id_financiamiento = cdc.id_financiamiento"); 
					     sbSql.append("\n   And cdc.id_financiamiento = cac.id_contrato ");
					    sbSql.append("\n   And cdc.id_disposicion = cac.id_disposicion"); 
					     sbSql.append("\n   And ccc.id_banco_prestamo = cb.id_banco ");
					    sbSql.append("\n   And ccc.id_tipo_financiamiento = ctc.id_tipo_contrato ");
					    sbSql.append("\n   And ccc.no_empresa in (SELECT   e.no_empresa FROM empresa e, usuario_empresa ue ");
					     sbSql.append("\n   WHERE    e.no_empresa = ue.no_empresa ");
					     sbSql.append("\n   And ue.no_usuario = "+usuario+")");
					     sbSql.append("\n   And ccc.estatus = 'A' ");
					     sbSql.append("\n   And cac.estatus = 'A' ");
					     sbSql.append("\n   And cb.nac_ext = 'N'");
					     sbSql.append("\n   ORDER BY Describ ");
			System.out.println("query"+sbSql.toString());
			listListas = jdbcTemplate.query(sbSql.toString(), new RowMapper()
			{
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						dtoCons.setId(rs.getInt("ID"));
						dtoCons.setDescripcion(rs.getString("Describ"));
					return dtoCons;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: FinanciamientoModificacionC, C: FinanciamientoModificacionCDaoImpl, consultarLineas");
		}

		return listListas;

	}

	@Override
	public List<LlenaComboGralDto> consultarLinea(int banco) {
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listListas = new ArrayList<LlenaComboGralDto>();
		try
		{
			
			if(banco!=0){
			
				sbSql.append("\n SELECT adc.id_disposicion as ID, adc.id_financiamiento as Describ  ");
			      sbSql.append("\n FROM  cat_disposicion_credito adc  join cat_amortizacion_credito amc on adc.id_disposicion=amc.id_disposicion  "); 
			       sbSql.append("\n WHERE adc.estatus = 'A' ");
			       sbSql.append("\n and amc.estatus = 'A' ");
			      sbSql.append("\n and CONVERT (date,amc.fec_vencimiento)<CONVERT (date, GETDATE()-15)");
			      sbSql.append("\n  and id_banco_disp="+banco+" ");
			      sbSql.append("\n   ORDER BY Describ ");
			
				
			}else{
				sbSql.append("\n SELECT adc.id_disposicion as ID, adc.id_financiamiento as Describ  ");
			      sbSql.append("\n FROM  cat_disposicion_credito adc  join cat_amortizacion_credito amc on adc.id_disposicion=amc.id_disposicion  "); 
			       sbSql.append("\n WHERE adc.estatus = 'A' ");
			       sbSql.append("\n and amc.estatus = 'A' ");
			       sbSql.append("\n and CONVERT (date,amc.fec_vencimiento)<CONVERT (date, GETDATE()-15)");
			       sbSql.append("\n   ORDER BY Describ ");
			
			}
		      
					 			     
	    System.out.println("query"+sbSql.toString());
			listListas = jdbcTemplate.query(sbSql.toString(), new RowMapper()
			{
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						dtoCons.setId(rs.getInt("ID"));
						dtoCons.setDescripcion(rs.getString("Describ"));
					return dtoCons;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: FinanciamientoModificacionC, C: FinanciamientoModificacionCDaoImpl, consultarLineas");
		}

		return listListas;
	}

	@Override
	public List<LlenaComboGralDto> consultarCredito(int banco) {
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listListas = new ArrayList<LlenaComboGralDto>();
		try
		{
			
			if(banco!=0){
			
				sbSql.append("\n SELECT adc.id_disposicion as ID, adc.id_disposicion as Describ  ");
			      sbSql.append("\n FROM  cat_disposicion_credito adc  join cat_amortizacion_credito amc on adc.id_disposicion=amc.id_disposicion  "); 
			       sbSql.append("\n WHERE adc.estatus = 'A' ");
			       sbSql.append("\n and amc.estatus = 'A' ");
			      sbSql.append("\n and CONVERT (date,amc.fec_vencimiento)<CONVERT (date, GETDATE()-15)");
			      sbSql.append("\n  and id_banco_disp="+banco+" ");
			      sbSql.append("\n   ORDER BY Describ ");
			
				
			}else{
				sbSql.append("\n SELECT adc.id_disposicion as ID, adc.id_disposicion as Describ  ");
			      sbSql.append("\n FROM  cat_disposicion_credito adc  join cat_amortizacion_credito amc on adc.id_disposicion=amc.id_disposicion  "); 
			       sbSql.append("\n WHERE adc.estatus = 'A' ");
			       sbSql.append("\n and amc.estatus = 'A' ");
			       sbSql.append("\n and CONVERT (date,amc.fec_vencimiento)<CONVERT (date, GETDATE()-15)");
			       sbSql.append("\n   ORDER BY Describ ");
			
			}
		      
					 			     
	    System.out.println("query"+sbSql.toString());
			listListas = jdbcTemplate.query(sbSql.toString(), new RowMapper()
			{
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						dtoCons.setId(rs.getInt("ID"));
						dtoCons.setDescripcion(rs.getString("Describ"));
					return dtoCons;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: FinanciamientoModificacionC, C: FinanciamientoModificacionCDaoImpl, consultarLineas");
		}

		return listListas;
	}

	@Override
	public List<ControlPagosPasivos> consultarDisp(int banco,String linea, int credito, String fecha, int conso) {
		String f=fecha;
	
		
		f=Funciones.cambiarFecha(f,true);
	List<ControlPagosPasivos> listDisp= new ArrayList<ControlPagosPasivos>();
	List<ControlPagosPasivos> list= new ArrayList<ControlPagosPasivos>();
	DecimalFormat formato = new DecimalFormat("###,###,###,##0.00");
	
	StringBuffer sbSql = new StringBuffer();
	try
	{
		
		String linea2=linea;
		
		if(linea.equalsIgnoreCase("****TODAS****")){
			linea="";
			
		}
		
		   sbSql.append("\n  SELECT  cac.fec_vencimiento as fecha, ccc.no_empresa, e.nom_empresa, ccc.id_banco_prestamo, ");
		   sbSql.append("\n   cac.id_disposicion, ccc.id_financiamiento, ccc.id_tipo_financiamiento, ctc.descripcion, ");
		    sbSql.append("\n   CONVERT (varchar(17), CAST(cdc.monto_disposicion AS money), 1)as monto_disposicion , ");
		     sbSql.append("\n  CONVERT (varchar(17), CAST((cdc.monto_disposicion - coalesce((SELECT sum(cac2.capital) ");
		    sbSql.append("\n   FROM cat_amortizacion_credito cac2 WHERE cac2.estatus = 'P' And cac2.id_contrato = cdc.id_financiamiento ");
		    sbSql.append("\n   And cac2.id_disposicion = cdc.id_disposicion),0)) AS money), 1)as saldo_actual, ");
		    sbSql.append("\n   CONVERT (varchar(17), CAST(cac.capital AS money), 1)as capital , ");
		 	 sbSql.append("\n CONVERT (varchar(17), CAST(cac.interes AS money), 1)as interes,  ");
		     sbSql.append("\n  CONVERT (varchar(17), CAST(cdc.renta AS money), 1)as renta, ");
		      sbSql.append("\n  CONVERT (varchar(17), CAST(cast(case when cdc.renta = 0 then coalesce((cac.interes * (cac.iva/100)),0) else coalesce((cdc.renta * (cac.iva/100)),0) end as numeric(15,2)) AS money), 1)as iva, "); 
		     sbSql.append("\n  CONVERT (varchar(17), CAST( cast(case when cac.id_amortizacion = 0 then ");
		     sbSql.append("\n  COALESCE((cac.capital + cac.interes + case when cdc.renta = 0 then ");
		     sbSql.append("\n  (cac.interes * (cac.iva/100)) else (cdc.renta * (cac.iva/100)) end), 0) ");
		     sbSql.append("\n   else coalesce((cac.capital + cac.interes), 0) end as numeric(15,2))AS money), 1) as pago_total, ");
		    sbSql.append("\n   cac.id_amortizacion, ctc.financiamiento, cb.desc_banco ");

			sbSql.append("\n  FROM    cat_amortizacion_credito cac, cat_contrato_credito ccc, ");
			sbSql.append("\n   empresa e, cat_tipo_contrato ctc, cat_disposicion_credito cdc , cat_banco cb ");
			sbSql.append("\n   WHERE    ");
			sbSql.append("\n  cac.estatus = 'A' ");
			sbSql.append("\n  and cac.id_contrato = ccc.id_financiamiento ");
			sbSql.append("\n  and cac.id_contrato = cdc.id_financiamiento ");
			sbSql.append("\n  and cac.id_disposicion = cdc.id_disposicion ");
			sbSql.append("\n  and ctc.id_tipo_contrato = ccc.id_tipo_financiamiento ");
			sbSql.append("\n  and ccc.no_empresa = e.no_empresa ");
			sbSql.append("\n  and ccc.id_banco_prestamo = cb.id_banco ");
			sbSql.append("\n  and ctc.financiamiento in ('')  "); 
			
			if(banco!=0){
					sbSql.append("\n     and ccc.id_banco_prestamo = "+banco+"   ");
			}else{
			sbSql.append("\n and ccc.no_empresa = ccc.no_empresa ");			
			}

			
			if(linea!=" "){
				if(linea.equals("")){
					sbSql.append("\n    and  cdc.id_financiamiento <> '"+linea+"' ");					
				}else{
					sbSql.append("\n    and  cdc.id_financiamiento = '"+linea+"' ");	
				}
							
			}
			
			if( credito==0){
					sbSql.append("\n and cdc.id_disposicion= cdc.id_disposicion  ");
			}else{
				sbSql.append("\n  and cdc.id_disposicion= "+credito+"   ");
			}
			
	//		 sbSql.append("\n and cac.fec_vencimiento  <= '" +f+"' ");
		     sbSql.append("\n ORDER BY cac.fec_vencimiento  ");
		    System.out.println("QrynVo..:::.."+sbSql.toString());
				listDisp = jdbcTemplate.query(sbSql.toString(), new RowMapper()	{
			@Override
			public ControlPagosPasivos mapRow(ResultSet rs, int idx)throws SQLException{
				ControlPagosPasivos reporte = new ControlPagosPasivos();

				reporte.setFecha(rs.getString("fecha"));
				reporte.setNo_empresa(rs.getInt("no_empresa"));
				reporte.setEmpresa(rs.getString("nom_empresa"));
				reporte.setBanco_prestamo(rs.getInt("id_banco_prestamo"));
				reporte.setInstitucion(rs.getString("desc_banco"));
				reporte.setDisposicion(rs.getInt("id_disposicion"));
				reporte.setId_financiamiento(rs.getString("id_financiamiento"));
				reporte.setId_tipo_financiamiento(rs.getInt("id_tipo_financiamiento"));
				reporte.setTipoFin( rs.getString("descripcion"));
				reporte.setMontoCred(rs.getString("monto_disposicion"));
				reporte.setSaldoActual(rs.getString("saldo_actual"));
				reporte.setPagoCap(rs.getString("capital"));
				reporte.setInteres(rs.getString("interes"));
				reporte.setRenta(rs.getString("renta"));
				reporte.setIva(rs.getString("iva"));
				reporte.setPagoTotal(rs.getString("pago_total"));
				reporte.setId_amortizacion(rs.getInt("id_amortizacion"));
				reporte.setFinanciamiento(rs.getString("id_financiamiento"));
				reporte.setDesc_banco1(rs.getString("desc_banco"));
				reporte.setDesc_banco2(rs.getString("desc_banco"));
				reporte.setDesc_banco3(rs.getString("desc_banco"));
				return reporte;
				
				
			}
		});
		
		
	}
	catch(Exception e)
	{
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P: FinanciamientoModificacionC, C: FinanciamientoModificacionCDaoImpl, consultarGrid");
		}

	return listDisp;
	}





	@Override
	public JRDataSource  obtenerReportePDF2(int banco, String linea, int credito, String fecha, int conso) {
		StringBuffer sbSql = new StringBuffer();


		
		sbSql.append("\n  SELECT  cac.fec_vencimiento as fecha, ccc.no_empresa, e.nom_empresa, ccc.id_banco_prestamo, ");
		sbSql.append("\n  cac.id_disposicion, ccc.id_financiamiento, ccc.id_tipo_financiamiento, ctc.descripcion, ");
		sbSql.append("\n  cdc.monto_disposicion, (cdc.monto_disposicion - coalesce((SELECT sum(cac2.capital) ");
		sbSql.append("\n  FROM cat_amortizacion_credito cac2 ");
		sbSql.append("\n  WHERE cac2.estatus = 'A' ");
		 sbSql.append("\n  And cac2.id_contrato = cdc.id_financiamiento ");  
		sbSql.append("\n  And cac2.id_disposicion = cdc.id_disposicion),0))  as saldo_actual, ");
		 sbSql.append("\n  cac.capital, cac.interes, cdc.renta, ");
		sbSql.append("\n  cast(case ");
		sbSql.append("\n  when cdc.renta = 0 then coalesce((cac.interes * (cac.iva/100)),0) "); 
		sbSql.append("\n else coalesce((cdc.renta * (cac.iva/100)),0) end as numeric(15,2)) as iva, ");
		sbSql.append("\n  cast(case ");
		sbSql.append("\n  when cac.id_amortizacion = 0 then "); 
		sbSql.append("\n  COALESCE((cac.capital + cac.interes + case when cdc.renta = 0 then "); 
		sbSql.append("\n  (cac.interes * (cac.iva/100)) else (cdc.renta * (cac.iva/100)) end), 0) "); 
		sbSql.append("\n  else coalesce((cac.capital + cac.interes), 0) end as numeric(15,2)) as pago_total, cac.id_amortizacion, ctc.financiamiento ");
	     
			sbSql.append("\n   , ca.desc_arrendadora as desc_banco ");  
			
		
		
		
				sbSql.append("\n   FROM    cat_amortizacion_credito cac, cat_contrato_credito ccc, "); 
				sbSql.append("\n   empresa e, cat_tipo_contrato ctc, cat_disposicion_credito cdc  , ");
				
					sbSql.append("\n 	cat_arrendadoras ca ");	

				

				
				sbSql.append("\n   	 WHERE  ");
			
				
				sbSql.append("\n    cac.estatus = 'A' ");
				sbSql.append("\n    and cac.id_contrato = ccc.id_financiamiento ");
				sbSql.append("\n     and cac.id_contrato = cdc.id_financiamiento ");
				sbSql.append("\n   and cac.id_disposicion = cdc.id_disposicion ");
				sbSql.append("\n   and ctc.id_tipo_contrato = ccc.id_tipo_financiamiento ");
				sbSql.append("\n    and ccc.no_empresa = e.no_empresa ");

					
		

		
		sbSql.append("\n      and cac.fec_vencimiento >='01/11/2017'"
				+ "   ORDER BY fecha ");
		
			System.out.println("query"+sbSql.toString());
			return  (JRDataSource) this.getJdbcTemplate().queryForList(sbSql.toString());
	}

	@Override
	public List<Map<String, Object>> obtenerReportePDF2(Map map) {
		
		List<Map<String, Object>> resultado = null;
		StringBuffer sbSql = new StringBuffer();
		try{

			sbSql.append("\n      SELECT  cac.fec_vencimiento as fecha, e.nom_empresa,");
					sbSql.append("\n       ccc.id_financiamiento,  ctc.descripcion, ");
							sbSql.append("\n       cdc.monto_disposicion, (cdc.monto_disposicion - coalesce((SELECT sum(cac2.capital) ");
									sbSql.append("\n       FROM cat_amortizacion_credito cac2 ");
											sbSql.append("\n       WHERE cac2.estatus = 'A' ");
													sbSql.append("\n      And cac2.id_contrato = cdc.id_financiamiento ");
															sbSql.append("\n       And cac2.id_disposicion = cdc.id_disposicion),0))  as saldo_actual, ");
																	sbSql.append("\n       cac.capital, cac.interes,"); 
																			sbSql.append("\n       cast(case ");
																					sbSql.append("\n       when cdc.renta = 0 then coalesce((cac.interes * (cac.iva/100)),0) ");
																							sbSql.append("\n      else coalesce((cdc.renta * (cac.iva/100)),0) end as numeric(15,2)) as iva, ");
																									sbSql.append("\n       cast(case ");
																											sbSql.append("\n       when cac.id_amortizacion = 0 then ");
																													sbSql.append("\n       COALESCE((cac.capital + cac.interes + case when cdc.renta = 0 then ");
																															sbSql.append("\n       (cac.interes * (cac.iva/100)) else (cdc.renta * (cac.iva/100)) end), 0) ");
																																	sbSql.append("\n      else coalesce((cac.capital + cac.interes), 0) end as numeric(15,2)) as pago_total, ca.desc_arrendadora as desc_banco"); 
																																			sbSql.append("\n        FROM    cat_amortizacion_credito cac, cat_contrato_credito ccc, ");
																																					sbSql.append("\n       empresa e, cat_tipo_contrato ctc, cat_disposicion_credito cdc  , ");
																																							sbSql.append("\n      	cat_arrendadoras ca"); 
																																									sbSql.append("\n      	 WHERE  ");
																																											sbSql.append("\n       cac.estatus = 'A' ");
			    		sbSql.append("\n       and cac.id_contrato = ccc.id_financiamiento ");
			    		sbSql.append("\n        and cac.id_contrato = cdc.id_financiamiento ");
			    		 sbSql.append("\n       and cac.id_disposicion = cdc.id_disposicion ");
					   sbSql.append("\n       and ctc.id_tipo_contrato = ccc.id_tipo_financiamiento ");
					   sbSql.append("\n       and ccc.no_empresa = e.no_empresa ");
			    		sbSql.append("\n        and cac.fec_vencimiento >='01/11/2017'   ORDER BY fecha ");
		    System.out.println("Query "+sbSql.toString());
		    resultado = (List<Map<String, Object>>)jdbcTemplate.query(sbSql.toString(), new RowMapper(){
				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            
			            results.put("fecha", rs.getString("fecha"));
			            results.put("nom_empresa", rs.getDate("nom_empresa"));
			            results.put("id_financiamiento", rs.getString("id_financiamiento"));
			            results.put("descripcion", rs.getDouble("descripcion"));
			            results.put("monto_disposicion", rs.getString("monto_disposcion"));
			            results.put("saldo_actual", rs.getString("saldo_actual"));
			            results.put("capital", rs.getString("capital"));
			            results.put("interes", rs.getString("interes"));
			            results.put("iva", rs.getDate("iva"));
			            results.put("pago_total", rs.getString("pago_total"));
			            results.put("desc_banco", rs.getString("desc_banco"));
			            return results;
					}
				});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarReporteMovsDuplicados");
		}
		return resultado;
		
	
	}	
	
	

}
