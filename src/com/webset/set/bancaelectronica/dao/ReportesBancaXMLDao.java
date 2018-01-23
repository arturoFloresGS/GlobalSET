package com.webset.set.bancaelectronica.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;
/**
 * clase que contiene las consultas para generar los reportes
 * @author Jessica Arelly Cruz Cruz
 * @since 11/03/2011
 *
 */
public class ReportesBancaXMLDao {
	private Funciones funciones = new Funciones();
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	private static Logger logger = Logger.getLogger(ReportesBancaXMLDao.class);

	/**
	 * FunSQLReporteConcepto
	 * @param datos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> ejecutarReporteConcepto(Map<String, Object> datos){
		List<Map<String, Object>> resultado = null;
		String gsDBM = "SQL SERVER";
		StringBuffer sb = new StringBuffer();
		String sql = "";
		/*
		 * Public Function FunSQLReporteConcepto(ByVal pi_empresainf As Integer, ByVal pi_empresasup As Integer, _
		ByVal pi_bancoinf As Integer, ByVal pi_bancosup As Integer, ByVal ps_Chequera As String, _
		ByVal pd_fechainf As Date, ByVal pd_fechasup As Date, ByVal pbdetalle As Boolean) As ADODB.Recordset
		'Recibe los par�metros necesarios para ejecutar la sentencia de la forma frmCritSelebancae
		Dim sSQL As String
		
	        sSQL = "SHAPE {"
	        sSQL = sSQL & Chr(10) & ""*/
		try{
	        if (datos.get("pbDetalle".toString()) != null && datos.get("pbDetalle").toString().trim().equals("true"))
	        {
	        	logger.info("Reporte detalle");
	        	if(datos.get("piBancoInf").toString() != null && datos.get("piBancoInf").toString().equals("12"))
	        	{
	        		logger.info("Reporte bancomer");
	                  sb.append("\n select m.descripcion as desc_observacion, ");
	                  if(gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2"))
	                  {
	                      sb.append("\n e.nom_empresa,m.no_empresa,m.id_banco,m.secuencia, cb.desc_banco,m.id_chequera ,m.fec_valor,m.sucursal,");
	                      sb.append("\n m.folio_banco,m.referencia,ccb.id_divisa,m.importe as importe,");
	                      sb.append("\n m.id_cve_concepto as concepto_set, m.observacion, m.cargo_abono, case when m.cargo_abono = 'E'");
	                      sb.append("\n then importe* -1 else importe end as importe_ca ,case when m.referencia = 1");
	                      sb.append("\n and m.cargo_abono = 'E'         then m.referencia         Else '' end as no_folio_det,  case when m.id_estatus_trasp='T'");
	                      sb.append("\n then 'S' else 'N' end as ejecutado,m.fec_alta,m.b_trasp_banco,m.b_trasp_conta");
	                  }
	                  else
	                  {
	                      sb.append("\n e.nom_empresa,m.no_empresa,m.id_banco,m.secuencia, cb.desc_banco,m.id_chequera ,m.fec_valor,m.sucursal,");
	                      sb.append("\n m.folio_banco,m.referencia,ccb.id_divisa,convert(float,m.importe) as importe, ");
	                      sb.append("\n m.id_cve_concepto as concepto_set, m.observacion, m.cargo_abono, case when m.cargo_abono = 'E'");
	                      sb.append("\n then convert(float,importe)* -1 else convert(float,importe) end as importe_ca ");
	                      if(gsDBM.equals("SYBASE"))
	                          sb.append("\n,case when PATINDEX( '%[^0-9]%', m.referencia) = 0 and m.cargo_abono = 'E' ");
	                      else
	                          sb.append("\n,case when ISNUMERIC(m.referencia) = 1 and m.cargo_abono = 'E' ");
	
	                      sb.append("\n        then m.referencia ");
	                      sb.append("\n        Else '' end as no_folio_det, ");
	                      sb.append("\n case when m.id_estatus_trasp='T' then 'S' else 'N' end as ejecutado,m.fec_alta,m.b_trasp_banco,m.b_trasp_conta ");
	                  }
	                  
	                  sb.append("\n from movto_banca_e m left join equivale_concepto c on (m.concepto = c.desc_concepto),cat_banco  cb,empresa e,");
	                  sb.append("\n cat_cta_banco ccb");
	                  sb.append("\n Where m.id_cve_concepto Not in (select distinct e.id_cve_leyenda from equivale_concepto e where e.id_cve_leyenda in");
	                  sb.append("\n (select n.id_cve_leyenda from equivale_concepto n) and e.id_banco = m.id_banco");
	                  sb.append("\n and e.cargo_abono=m.cargo_abono)");
	
	
	                  sb.append("\n and m.no_empresa = ccb.no_empresa and m.id_banco = ccb.id_banco and m.id_chequera = ccb.id_chequera");
	                  sb.append("\n and m.id_estatus_trasp='N' and movto_arch=1 and cb.id_banco = m.id_banco ");
	                  sb.append("\n And e.no_empresa = m.no_empresa ");
	                  sb.append("\n and m.no_empresa between " + datos.get("piEmpresaInf") + " and " + datos.get("piEmpresaSup") + " ");
	                  sb.append("\n and m.id_banco = " + datos.get("piBancoSup") + " ");
	                  sb.append("\n and m.id_chequera like '" + datos.get("psChequera") + "' ");
	                  sb.append("\n and m.fec_alta between '" + datos.get("pdFechaInf") + "' and '" + datos.get("pdFechaSup") + "' ");
	                  sb.append("\n group by e.nom_empresa,m.no_empresa,m.id_banco,m.secuencia, cb.desc_banco,m.id_chequera ,m.fec_valor,m.sucursal,");
	                  sb.append("\n m.folio_banco,m.referencia,ccb.id_divisa,importe, m.observacion, m.cargo_abono,");
	                  sb.append("\n m.fec_alta , m.b_trasp_banco, m.b_trasp_conta, m.id_cve_concepto, m.id_estatus_trasp, m.descripcion ");
	                  if(gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2"))
	                      sb.append("\n ,m.importe");
	                  if(gsDBM.equals("SYBASE"))
	                	  sb.append("\n AT ISOLATION READ UNCOMMITTED ");
	                  
	                 // sb.append("\n }  AS CmdBancae COMPUTE CmdBancae,SUM(CmdBancae.'importe_ca') AS total_ca BY 'nom_empresa','desc_banco','id_chequera'");
	        	}
	            else
	            {
	            	logger.info("Reporte diferente a bancomer");
	               sb.append("\n select m.descripcion as desc_observacion, case when c.concepto_set is null");
	               if(gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2"))
	               {
	                      sb.append("\n then m.concepto else c.concepto_set end as concepto,e.nom_empresa,m.no_empresa,m.id_banco,m.secuencia, cb.desc_banco,m.id_chequera ,m.fec_valor,m.sucursal,");
	                      sb.append("\n m.folio_banco,m.referencia,ccb.id_divisa,m.importe as importe, case when c.concepto_set is null");
	                      sb.append("\n then m.concepto else c.concepto_set end as concepto_set, m.observacion, m.cargo_abono, case when m.cargo_abono = 'E'");
	                      sb.append("\n then importe* -1 else importe end as importe_ca ,case when m.referencia = 1");
	                      sb.append("\n and m.cargo_abono = 'E'         then m.referencia         Else '' end as no_folio_det,  case when m.id_estatus_trasp='T'");
	                      sb.append("\n then 'S' else 'N' end as ejecutado,m.fec_alta,m.b_trasp_banco,m.b_trasp_conta");
	               }
	               else
	               {
	                      sb.append("\n then m.concepto else c.concepto_set end as concepto,e.nom_empresa,m.no_empresa,m.id_banco,m.secuencia, cb.desc_banco,m.id_chequera ,m.fec_valor,m.sucursal,");
	                      sb.append("\n m.folio_banco,m.referencia,ccb.id_divisa,convert(float,m.importe) as importe, case when c.concepto_set is null");
	                      sb.append("\n then m.concepto else c.concepto_set end as concepto_set, m.observacion, m.cargo_abono, case when m.cargo_abono = 'E'");
	                      sb.append("\n then convert(float,importe)* -1 else convert(float,importe) end as importe_ca ");
	                      if(gsDBM.equals("SYBASE"))
	                          sb.append("\n ,case when PATINDEX( '%[^0-9]%', m.referencia) = 0 and m.cargo_abono = 'E' ");
	                      else
	                          sb.append("\n ,case when ISNUMERIC(m.referencia) = 1 and m.cargo_abono = 'E' ");
	
	                      sb.append("\n        then m.referencia ");
	                      sb.append("\n        Else '' end as no_folio_det, ");
	                      sb.append("\n case when m.id_estatus_trasp='T' then 'S' else 'N' end as ejecutado,m.fec_alta,m.b_trasp_banco,m.b_trasp_conta ");
	               }
	                  
	                  sb.append("\n from movto_banca_e m left join equivale_concepto c on (m.concepto = c.desc_concepto and m.id_cve_concepto=c.id_cve_concepto),cat_banco  cb,empresa e,");
	                  sb.append("\n cat_cta_banco ccb");
	                  sb.append("\n Where Not concepto in (select desc_concepto from equivale_concepto e where  e.id_banco = m.id_banco");
	                  sb.append("\n and e.cargo_abono=m.cargo_abono)");
	                  sb.append("\n and m.no_empresa = ccb.no_empresa and m.id_banco = ccb.id_banco and m.id_chequera = ccb.id_chequera");
	                  sb.append("\n and m.id_estatus_trasp='N' and movto_arch=1 and cb.id_banco = m.id_banco ");
	                  sb.append("\n And e.no_empresa = m.no_empresa ");
	                  sb.append("\n and m.no_empresa between " + datos.get("piEmpresaInf") + " and " + datos.get("piEmpresaSup") + " ");
	                  sb.append("\n and m.id_banco = " + datos.get("piBancoSup") + " ");
	                  sb.append("\n and m.id_chequera like '" + datos.get("psChequera") + "' ");
	                  sb.append("\n and m.fec_alta between '" + datos.get("pdFechaInf") + "' and '" + datos.get("pdFechaSup") + "' ");
	                  sb.append("\n group by e.nom_empresa,m.no_empresa,m.id_banco,m.secuencia, cb.desc_banco,m.id_chequera ,m.fec_valor,m.sucursal,");
	                  sb.append("\n m.folio_banco,m.referencia,ccb.id_divisa,importe,concepto_set, m.observacion, m.cargo_abono,");
	                  sb.append("\n m.fec_alta , m.b_trasp_banco, m.b_trasp_conta, m.concepto, m.id_estatus_trasp, m.descripcion ");
	                  if(gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2"))
	                      sb.append("\n ,c.concepto_set,m.importe");
	                  if(gsDBM.equals("SYBASE"))
	                	  sb.append("\n AT ISOLATION READ UNCOMMITTED ");
	                 // sb.append("\n }  AS CmdBancae COMPUTE CmdBancae,SUM(CmdBancae.'importe_ca') AS total_ca BY 'nom_empresa','desc_banco','id_chequera'");
	            }
	
	        }
	        else
	        {
	        	logger.info("Reporte sin detalle");
	        	if(datos.get("piBancoInf").toString() != null && datos.get("piBancoInf").toString().equals("12"))
	             {
	        		logger.info("Reporte bancomer");
	        	 	sb.append("\n select count(m.no_empresa) as cuenta,e.nom_empresa,m.no_empresa,");
	        	 	sb.append("\n m.id_banco , m.id_cve_concepto as concepto, cb.desc_banco, m.cargo_abono");
	        	 	sb.append("\n from movto_banca_e m,cat_banco  cb,empresa e, equivale_concepto c ");
		            sb.append("\n Where m.id_cve_concepto Not in ");
		            sb.append("\n (select distinct e.id_cve_leyenda from equivale_concepto e ");
		            sb.append("\n where e.id_cve_leyenda in(select n.id_cve_leyenda from equivale_concepto n) and");
		            sb.append("\n e.id_banco = m.id_banco and e.cargo_abono=m.cargo_abono) ");
		            sb.append("\n and m.id_estatus_trasp='N' and movto_arch=1 and cb.id_banco = m.id_banco ");
		            sb.append("\n And e.no_empresa = m.no_empresa ");
		            sb.append("\n and m.no_empresa between " + datos.get("piEmpresaInf") + " and " + datos.get("piEmpresaSup") + " ");
		            sb.append("\n and m.id_banco = " + datos.get("piBancoInf"));
		            sb.append("\n and m.id_chequera like '" + datos.get("psChequera") + "' ");
		            sb.append("\n and m.fec_alta between '" + datos.get("pdFechaInf") + "' and '" + datos.get("pdFechaSup") + "' ");
		            sb.append("\n group by e.nom_empresa,m.id_banco,m.id_cve_concepto,cb.desc_banco,m.no_empresa,m.cargo_abono");
		            sb.append("\n order by e.nom_empresa");
		            if(gsDBM.equals("SYBASE"))
		            	sb.append("\n AT ISOLATION READ UNCOMMITTED ");
		             //  sb.append("\n }  AS concepto COMPUTE concepto BY 'desc_banco','nom_empresa'");
	             }
               else
               {
            	   logger.info("Reporte diferente a bancomer");
	               sb.append("\n select count(m.no_empresa) as cuenta,e.nom_empresa,m.no_empresa,");
	               sb.append("\n m.id_banco , m.concepto , cb.desc_banco, m.cargo_abono");
	               sb.append("\n from movto_banca_e m,cat_banco  cb,empresa e, equivale_concepto c ");
	               sb.append("\n Where Not concepto in ");
	               sb.append("\n (select desc_concepto from equivale_concepto e ");
	               sb.append("\n where  e.id_banco = m.id_banco and e.cargo_abono=m.cargo_abono) ");
	               sb.append("\n and m.id_estatus_trasp='N' and movto_arch=1 and cb.id_banco = m.id_banco ");
	               sb.append("\n And e.no_empresa = m.no_empresa ");
	               sb.append("\n and m.no_empresa between " + datos.get("piEmpresaInf") + " and " + datos.get("piEmpresaSup"));
	               sb.append("\n and m.id_banco between " + datos.get("piBancoInf") + " and " + datos.get("piBancoSup"));
	               sb.append("\n and m.id_chequera like '" + datos.get("psChequera") + "' ");
	               sb.append("\n and m.fec_alta between '" + datos.get("pdFechaInf") + "' and '" + datos.get("pdFechaSup") + "' ");
	               sb.append("\n group by concepto,m.id_banco,cb.desc_banco,e.nom_empresa,m.no_empresa,m.cargo_abono");
	               sb.append("\n order by e.nom_empresa");
	               if(gsDBM.equals("SYBASE"))
	                     sb.append("\n AT ISOLATION READ UNCOMMITTED ");
	              // sb.append("\n }  AS concepto COMPUTE concepto BY 'desc_banco','nom_empresa'");
	             }
	        }
	        sql = sb.toString();
	        //logger.info("Reporte conceptos" + sql);
	        resultado = (List<Map<String, Object>>)jdbcTemplate.query(sql, new RowMapper(){
        	public Object mapRow(ResultSet rs, int i)
			throws SQLException {

	            Map<String, Object> cons = new HashMap<String, Object>();
				cons.put("nom_empresa", 	rs.getString("nom_empresa"));
				cons.put("no_empresa", 		rs.getString("no_empresa"));
				cons.put("id_banco", 		rs.getString("id_banco"));
				cons.put("desc_banco", 		rs.getString("desc_banco"));
				cons.put("cargo_abono", 	rs.getString("cargo_abono"));
				cons.put("concepto", 		rs.getString("concepto"));
				cons.put("cuenta", 			rs.getString("cuenta"));
				return cons;
			}});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:ReportesBancaXMLDao, M:ejecutarReporteConcepto");
		}
        logger.info("resultado: " + resultado);

		return resultado;
	}
	
	/**
	 * FunSQL_ReporteBE
	 * consulta para obtener el reporte de movimientos del banco
	 * @param datos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> ejecutarReporteBE(Map<String, Object> datos){
		List<Map<String, Object>> resultado = null;
		String gsDBM = "SQL SERVER";
		String squery = "";
		try{
	/*
	 * 	Public Function FunSQL_ReporteBE(ByVal pi_empresainf As Integer, ByVal pi_empresasup As Integer, _
		ByVal pi_bancoinf As Integer, ByVal pi_bancosup As Integer, ByVal ps_Chequera As String, _
		ByVal pd_fechainf As Date, ByVal pd_fechasup As Date, ByVal pb_Mov As Boolean, _
		ByVal ps_tipomov As String, ByVal pv_OrigenMov As Variant, ByVal ps_concepto As String, _
		ByVal pb_opcion1 As Boolean, ByVal ps_movtosTEF As Boolean, ByVal ps_movtosDia As Boolean, _
		ByVal ps_movtoAutomata As Boolean, ByVal pvi_flagQuery As Integer) As ADODB.Recordset
		'Recibe los par�metros necesarios para ejecutar la sentencia de la forma frmCritSelebancae
		*/
		    squery += consultarReporteBE(1, datos);
		    squery += "\n UNION \n";
		    squery += consultarReporteBE(2, datos);
		
		    if(gsDBM.equals("ORACLE") || gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2"))
	            squery += "\n order by cargo_abono, fec_valor ";
		    
            if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE"))
	            squery += "\n order by be.id_chequera, be.fec_valor,be.importe,be.cargo_abono";
		        
		    if(gsDBM.equals("SYBASE"))
		          squery += "\n AT ISOLATION READ UNCOMMITTED ";
		   // logger.info("reporte BE \n"+squery);
		    
		    System.out.println(squery);
		    resultado = (List<Map<String, Object>>)jdbcTemplate.query(squery, new RowMapper(){

				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("desc_observacion", rs.getString("desc_observacion"));
			            results.put("nom_empresa", rs.getString("nom_empresa"));
			            results.put("no_empresa", rs.getString("no_empresa"));
			            results.put("id_banco", rs.getString("id_banco"));
			            results.put("secuencia", rs.getString("secuencia"));
			            results.put("desc_banco", rs.getString("desc_banco"));
			            results.put("id_chequera", rs.getString("id_chequera"));
			            results.put("fec_valor", rs.getString("fec_valor"));
			            results.put("sucursal", rs.getString("sucursal"));
			            results.put("folio_banco", rs.getString("folio_banco"));
			            results.put("referencia", rs.getString("referencia"));
			            results.put("id_divisa", rs.getString("id_divisa"));
			            results.put("importe", rs.getString("importe"));
			            results.put("concepto_set", rs.getString("concepto_set"));
			            results.put("observacion", rs.getString("observacion"));
			            results.put("cargo_abono", rs.getString("cargo_abono"));
			            results.put("importe_ca", rs.getString("importe_ca"));
			            results.put("no_folio_det", rs.getString("no_folio_det"));
			            results.put("ejecutado", rs.getString("ejecutado"));
			            results.put("fec_alta", rs.getString("fec_alta"));
			            results.put("b_trasp_banco", rs.getString("b_trasp_banco"));
			            results.put("b_trasp_conta", rs.getString("b_trasp_conta"));
			            return results;
					}
				});
	 
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:ReportesBancaXMLDao, M:ejecutarReporteBE");
		}
		return resultado;
	}

	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> ejecutarReporteTest(Map<String, Object> datos){
		List<Map<String, Object>> resultado = null;
		String squery = "";
		try{
		    
		    squery = "SELECT id_usuario, clave_usuario, contrasena, estatus FROM seg_usuario WHERE estatus = '"+datos.get("p_estatus")+"'";
		
		    logger.info("reporte: \n"+squery);

		    resultado = (List<Map<String, Object>>)jdbcTemplate.query(squery, new RowMapper(){

				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            
			            results.put("id_usuario", rs.getString("id_usuario"));
			            results.put("clave_usuario", rs.getString("clave_usuario"));
			            results.put("contrasena", rs.getString("contrasena"));
			            results.put("estatus", rs.getString("estatus"));
			            
			            return results;
					}
				});
	 
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:ReportesBancaXMLDao, M:ejecutarReporteBE");
		}
		return resultado;
	}
	
	
	/**
	 * FunSQLQueryReporte
	 * @param contador
	 * @param datos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String consultarReporteBE(int contador, Map<String, Object> datos){
		String gsDBM = "SQL SERVER";
		String queryreporte = "";
		String squery = "";
		String squeryizq = "";
		String squeryder = "";
		
		try{
	  		if (datos.get("pbOpcion1") != null && datos.get("pbOpcion1").equals("true"))
	  		{
	            queryreporte += " SELECT be.descripcion AS desc_observacion, " ;
	            queryreporte += "\n e.nom_empresa, be.no_empresa, be.id_banco, be.secuencia, ";
	            queryreporte += "\n b.desc_banco, be.id_chequera, be.fec_valor, be.concepto AS sucursal, ";
	            queryreporte += "\n be.folio_banco, be.referencia, ccb.id_divisa,";
	            queryreporte += "\n be.importe AS importe, ";
	            queryreporte += "\n CASE WHEN c.concepto_set IS NULL THEN be.concepto ELSE c.concepto_set END AS concepto_set, ";
	            queryreporte += "\n be.observacion, be.cargo_abono, ";
	            
	            if (gsDBM.equals("DB2") || gsDBM.equals("POSTGRESQL"))
	                queryreporte += "\n COALESCE(be.descripcion,'') || '/' || COALESCE(be.observacion,'') AS desc_observacion, ";
	            else
	                queryreporte += "\n COALESCE(be.descripcion,'') + '/' + COALESCE(be.observacion,'') AS desc_observacion, ";
	            
	            queryreporte += "\n CASE WHEN be.cargo_abono = 'E' THEN be.importe * -1 ELSE be.importe END AS importe_ca ";
	            
	            queryreporte += "\n ,CASE WHEN be.referencia = '1' and be.cargo_abono = 'E' ";
	            queryreporte += "\n         THEN be.referencia ";
	            queryreporte += "\n         ELSE '' END AS no_folio_det, ";
	            queryreporte += "\n  CASE WHEN be.id_estatus_trasp = 'T' THEN 'S' ELSE 'N' END AS ejecutado, be.fec_alta, be.b_trasp_banco, be.b_trasp_conta ";
	            
	            if(contador == 1)
	                queryreporte += "\n  FROM movto_banca_e be LEFT JOIN equivale_concepto c ON (be.id_banco = c.id_banco AND";
	            else
	                queryreporte += "\n  FROM hist_movto_banca_e be LEFT JOIN equivale_concepto c ON (be.id_banco = c.id_banco AND";
	            
	            queryreporte += "\n  be.concepto = c.desc_concepto AND be.cargo_abono = c.cargo_abono)";
	            queryreporte += "\n ,empresa e, cat_banco b, cat_cta_banco ccb ";
	            queryreporte += "\n  WHERE ";
	                
	            queryreporte += "\n be.cargo_abono IN ('I','E') ";
	            queryreporte += "\n AND be.no_empresa = ccb.no_empresa ";
	            queryreporte += "\n AND be.id_banco = ccb.id_banco ";
	            queryreporte += "\n AND be.id_chequera = ccb.id_chequera ";
	            queryreporte += "\n AND be.no_empresa = e.no_empresa ";
	            queryreporte += "\n AND be.no_empresa BETWEEN " + datos.get("piEmpresaInf")  + " AND " + datos.get("piEmpresaSup");
	            queryreporte += "\n AND be.id_banco = b.id_banco ";
	            queryreporte += "\n AND b.b_banca_elect IN ('A','I') ";
	            queryreporte += "\n AND be.id_banco BETWEEN " + datos.get("piBancoInf") + " AND " + datos.get("piBancoSup");
	            queryreporte += "\n AND be.id_chequera LIKE '" + datos.get("psChequera") + "' ";
	            queryreporte += "\n AND be.fec_valor BETWEEN '" + datos.get("pdFechaInf") + "' AND '" + datos.get("pdFechaSup") + "' ";
	            
	            if (datos.get("pbMov") != null && datos.get("psConcepto")!= null && 
            		datos.get("pbMov").equals("true") && !datos.get("psConcepto").equals("") || (!datos.get("psConcepto").equals("")))
	                queryreporte += "\n AND (c.concepto_set LIKE '" + datos.get("psConcepto") + "' OR be.concepto LIKE '" + datos.get("psConcepto") + "')";
	            
	            queryreporte += "\n AND be.cargo_abono LIKE '" + datos.get("psTipoMov") + "' ";
	            queryreporte += "\n AND COALESCE(be.movto_arch,0) = " + datos.get("pvOrigenMov");
	            
	            //Filtra el resultado por movimientos TEF (banamex) o por movimientos del dia (bancomer) solo si es requerido
	            if (datos.get("psMovtoAutomata") != null && datos.get("psMovtoAutomata").equals("true"))
	            {
	                if (datos.get("psMovtosTEF") != null && datos.get("psMovtosTEF").equals("true"))
	                    queryreporte += "\n AND be.archivo LIKE 'BT%'";
	                
	                if (datos.get("psMovtosDia") != null && datos.get("psMovtosDia").equals("true"))
	                    queryreporte += "\n AND be.archivo LIKE 'MM%'";
	            }else
	            {
	            	if (datos.get("psMovtosTEF") != null && datos.get("psMovtosTEF").equals("true"))
	                    queryreporte += "\n AND be.archivo NOT LIKE 'BT%'";
	                
	            	if (datos.get("psMovtosDia") != null && datos.get("psMovtosDia").equals("true"))
	                    queryreporte += "\n AND NOT (be.archivo LIKE 'MM%' OR be.archivo LIKE 'MD%')";
	            	
	            }
	            squery = squery + queryreporte;
	  		}
	    	else
	    	{
	    		 
		        //squery = "" & "SHAPE {"
		        squeryizq += "SELECT be.descripcion AS desc_observacion, ";
		        squeryizq +=  "\n '' AS no_folio_det,'' as ejecutado, be.fec_alta, be.b_trasp_banco, be.b_trasp_conta, e.nom_empresa, be.no_empresa, be.id_banco, be.secuencia, ";
		        squeryizq +=  "\n b.desc_banco, be.id_chequera, be.fec_valor, be.sucursal, ";
		        squeryizq +=  "\n be.folio_banco, be.referencia, ccb.id_divisa,";
		        
		        if(gsDBM.equals("ORACLE"))
		        	squeryizq += "\n to_number(be.importe) AS importe, ";
		        if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE"))
	                squeryizq += "\n convert(float,be.importe) AS importe, ";
		        if(gsDBM.equals("POSTGRESQL"))
	                squeryizq += "\n to_number(be.importe,'999999') AS importe, ";
		        if(gsDBM.equals("DB2"))
	                squeryizq += "\n  be.importe AS importe, ";
		        
		        squeryizq += "\n CASE WHEN c.concepto_set IS NULL THEN be.concepto ELSE c.concepto_set END AS concepto_set, ";
		        squeryizq += "\n be.observacion, be.cargo_abono, ";
		        
		        if(gsDBM.equals("ORACLE"))
	                squeryizq += "\n CASE WHEN be.cargo_abono = 'E' THEN to_number(importe)* -1 ELSE to_number(importe) END AS importe_ca ";
		        if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE"))
		            squeryizq += "\n CASE WHEN be.cargo_abono = 'E' THEN convert(float,importe)* -1 ELSE convert(float,importe) END AS importe_ca ";
		        if(gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2"))
	                squeryizq += "\n CASE WHEN be.cargo_abono = 'E' THEN to_number(importe,'999999')* -1 ELSE to_number(importe,'999999') END AS importe_ca ";
		        
		        
		        squeryizq += "\n FROM empresa e, movto_banca_e be, cat_banco b, equivale_concepto c, cat_cta_banco ccb ";
		        squeryizq += "\n WHERE ";
		        
		        squeryizq += "\n be.cargo_abono IN ('I','E') ";
		        squeryizq += "\n AND be.no_empresa = ccb.no_empresa ";
		        squeryizq += "\n AND be.id_banco = ccb.id_banco ";
		        squeryizq += "\n AND be.id_chequera = ccb.id_chequera ";
		        
		        squeryizq += "\n AND be.no_empresa = e.no_empresa ";
		        squeryizq += "\n AND be.no_empresa BETWEEN " + datos.get("piEmpresaInf")  + " AND " + datos.get("piEmpresaSup") ;
		        squeryizq += "\n AND be.id_banco = b.id_banco ";
		        
		        squery = squery + squeryizq;
		        
		        squery += "\n AND b.b_banca_elect = 'S' ";   // Diferencia
		        squery += "\n AND c.id_banco = be.id_banco "; // Diferencia
		        
		        squeryder += "\n AND be.id_banco BETWEEN " + datos.get("piBancoInf") + " AND " + datos.get("piBancoSup") ;
		        squeryder += "\n AND be.id_chequera LIKE '" + datos.get("psChequera") + "' ";
		        squeryder += "\n AND fec_valor between convert(datetime,'" + datos.get("pdFechaInf") + "',103) AND convert(dateime,'" + datos.get("pdFechaSup") + "',103) ";
		        squeryder += "\n AND  be.concepto = c.desc_concepto "; // Diferencia
		        
		        if(datos.get("psConcepto") != null && !datos.get("psConcepto").equals("%"))  // Diferencia
		            squeryder += "\n AND concepto_set LIKE '" + datos.get("psConcepto") + "' "; // Diferencia
		        else
		            squeryder += "\n AND concepto LIKE '" + datos.get("psConcepto") + "' "; // Diferencia
		        
		        squeryder += "\n AND be.cargo_abono = c.cargo_abono ";    // Diferencia
		        squeryder += "\n AND be.cargo_abono LIKE '" + datos.get("psTipoMov") == null ? "" : datos.get("psTipoMov") + "' ";
		        
		        if(gsDBM.equals("ORACLE"))
	                squeryder += "\n AND coalesce(movto_arch,0) = " + datos.get("pvOrigenMov") == null ? 0 : datos.get("pvOrigenMov");
		        if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE"))
	                squeryder += "\n AND isnull(movto_arch,0) = " + datos.get("pvOrigenMov") == null ? 0 : datos.get("pvOrigenMov") + " ";
		        if(gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2"))
	                squeryder += "\n AND COALESCE(movto_arch,0) = " + datos.get("pvOrigenMov") == null ? 0 : datos.get("pvOrigenMov");
	    	}
	  		
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:ReportesBancaXMLDao, M:consultarReporteBE");
		}
		return squery;
	}
	
	//getters && setters
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
}
