package com.webset.set.bancaelectronica.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.egresos.dto.PropuestaPagoManualDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
/**
 * @author Jessica Arelly Cruz Cruz
 * @since 01/03/2011
 */
public class MovimientosBancaEDao {
	private ConsultasGenerales consultasGenerales;
	private Funciones funciones = new Funciones();
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	private static Logger logger = Logger.getLogger(MovimientosBancaEDao.class);
	
	/**
	 * llamada a concultas generales para obtener la fecha de hoy
	 * @return
	 */
	public String obtenerFechaHoy(){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		funciones = new Funciones();
		String fecha2 = "";
		try{
		Date fecha = consultasGenerales.obtenerFechaHoy();
		fecha2 = funciones.ponerFechaSola(fecha);
		}catch(Exception e){e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:MovimientosBancaEDao, M:obtenerFechaHoy");
		}
		return fecha2;
	}
	
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> llenarComboBancos(int noEmpresa){
		String sql="";
		String gsDBM = "SQL SERVER";
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		try{
		    if(gsDBM.equals("ORACLE"))
		    {
		      sb.append("\n Select distinct cat_banco.id_banco as ID, ");
		      sb.append("\n cat_banco.desc_banco as DESCRIPCION ");
		      sb.append("\n From cat_banco left outer join cat_cta_banco on (cat_cta_banco.id_banco = cat_banco.id_banco)");
		      sb.append("\n Where cat_cta_banco.no_empresa = " + noEmpresa);
		    
		    }
		    
		    if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE"))
		    {
		      sb.append("\n Select distinct cat_banco.id_banco as ID, ");
		      sb.append("\n cat_banco.desc_banco as DESCRIPCION ");
		      sb.append("\n From cat_banco,cat_cta_banco");
		      sb.append("\n Where cat_cta_banco.id_banco *= cat_banco.id_banco ");
		      if (noEmpresa!=0){
		      sb.append("\n and cat_cta_banco.no_empresa = " + noEmpresa);
		      }
		      
		    }
		   
		    if(gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2"))
		    {
		      sb.append("\n Select distinct cb.id_banco as ID, ");
		      sb.append("\n cb.desc_banco as DESCRIPCION ");
		      sb.append("\n From empresa e, cat_cta_banco ccb");
		      sb.append("\n       LEFT JOIN cat_banco cb ON(ccb.id_banco = cb.id_banco)");
		      
		      sb.append("\n Where  e.no_empresa = ccb.no_empresa");
    		  //sb.append("\n ccb.no_empresa = " & pvvValor1 And e.no_empresa = ccb.no_empresa);
		    }
  
		      sql = sb.toString();
		     System.out.println(sql);
		    //bitacora.insertarRegistro(sql);
		    listDatos= jdbcTemplate.query(sql, new RowMapper(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:BancaElectronica, C:MovimientosBancaEDao, M:llenarComboBancos");
		}
		return listDatos;
	}
	
	/**
	 * FunSQLSelect309
	 * @param noBanco
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String seleccionarBancaElect(int noBanco) {
		String sql = "";
		StringBuffer sb = new StringBuffer();
		List <ComunDto> result = null;
		try{
			sb.append( " Select b_banca_elect ");
			sb.append( " FROM cat_banco ");
			sb.append( " WHERE id_banco = " + noBanco);
		
		    sql=sb.toString();
		    //System.out.println("banca: "+sql);
				result = jdbcTemplate.query(sql, new RowMapper(){
				public ComunDto mapRow(ResultSet rs, int idx) throws SQLException {
					ComunDto control = new ComunDto();
					control.setDescripcion(rs.getString("b_banca_elect"));
				return control;
				}});
				
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:BancaElectronica, C:MovimientosBancaEDao, M:seleccionarBancaElect");
			} 
		return result.isEmpty()? "" : result.get(0).getDescripcion();
	}
	
	
	/**
	 * FunSQLSelect310
	 * @param lbGenerico
	 * @param banco
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ComunDto>obtenerConceptos(boolean lbGenerico, int noBanco)
	{
		String sql="";
		StringBuffer sb = new StringBuffer();
		List<ComunDto> listConceptos= new ArrayList<ComunDto>();
		try
		{
			sb.append( " Select distinct concepto_set as desc_concepto ");
			sb.append( "\n  FROM equivale_concepto ");
			sb.append( "\n  WHERE ");
			if (lbGenerico)
				sb.append( "\n  id_banco = " + noBanco);
		    else
		    	sb.append( "\n id_banco = 0");
		   
			sql = sb.toString();
			//System.out.println("conceptos: "+sql);
			listConceptos= jdbcTemplate.query(sql, new RowMapper(){
			public ComunDto mapRow(ResultSet rs, int idx) throws SQLException {
				ComunDto cons = new ComunDto();
				cons.setDescripcion(rs.getString("desc_concepto"));
				return cons;
			}});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:BancaElectronica, C:MovimientosBancaEDao, M:obtenerConceptos");
		}
		return listConceptos;
	}
	
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> llenarComboChequeras(int noBanco,int noEmpresa){
		String sql="";
		String gsDBM = "SQL SERVER";
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		try{
		      sb.append("\n Select id_chequera as ID, ");
		      sb.append("\n id_chequera as DESCRIPCION ");
		      sb.append("\n From cat_cta_banco");
		      sb.append("\n Where id_banco = " + noBanco);
		      
		      if( noEmpresa!= 0 ){
		      sb.append("\n and no_empresa = " + noEmpresa);
		      }
    		  
		      if(gsDBM.equals("SYBASE"))
		    	  sb.append("\n AT ISOLATION READ UNCOMMITTED ");
		      sql = sb.toString();
		      //logger.info(sql);
		    //bitacora.insertarRegistro(sql);
		    listDatos= jdbcTemplate.query(sql, new RowMapper(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setDescripcion(rs.getString("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:BancaElectronica, C:MovimientosBancaEDao, M:llenarComboChequeras");
		}
		return listDatos;
	}
	
	/**
	 * FunSQLReporteConcepto
	 * @param datos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<PropuestaPagoManualDto> ejecutarReporteConcepto(Map<String, Object> datos){
		List <PropuestaPagoManualDto> paramRetorno = new ArrayList<PropuestaPagoManualDto>();
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
	        if (datos.get("pbdetalle").equals("true"))
	        {
	        	if(datos.get("piBancoInf").equals("12"))
	        	{
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
	                  sb.append("\n and m.no_empresa between " + datos.get("piEmpresaInf") + " and " + datos.get("piEmpresaEup") + " ");
	                  sb.append("\n and m.id_banco = " + datos.get("piBancoSup") + " ");
	                  sb.append("\n and m.id_chequera like '" + datos.get("psChequera") + "' ");
	                  sb.append("\n and m.fec_alta between '" + (datos.get("pdFechaInf")) + "' and '" + (datos.get("pdFechaSup")) + "' ");
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
	                  sb.append("\n and m.fec_alta between '" + (datos.get("pdFechaInf")) + "' and '" + (datos.get("pdFechaSup")) + "' ");
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
	        	
	        	if(datos.get("piBancoInf").equals("12"))
	             {
	            	 
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
		            sb.append("\n and m.id_banco = " + datos.get("piBancoInf") + " ");
		            sb.append("\n and m.id_chequera like '" + datos.get("psChequera") + "' ");
		            sb.append("\n and m.fec_alta between '" + (datos.get("pdFechaInf")) + "' and '" + (datos.get("pdFechaSup")) + "' ");
		            sb.append("\n group by m.id_cve_concepto,m.id_banco,cb.desc_banco,e.nom_empresa,m.no_empresa,m.cargo_abono");
		            if(gsDBM.equals("SYBASE"))
		            	sb.append("\n AT ISOLATION READ UNCOMMITTED ");
		             //  sb.append("\n }  AS concepto COMPUTE concepto BY 'desc_banco','nom_empresa'");
	             }
               else
               {
	               sb.append("\n select count(m.no_empresa) as cuenta,e.nom_empresa,m.no_empresa,");
	               sb.append("\n m.id_banco , m.concepto , cb.desc_banco, m.cargo_abono");
	               sb.append("\n from movto_banca_e m,cat_banco  cb,empresa e, equivale_concepto c ");
	               sb.append("\n Where Not concepto in ");
	               sb.append("\n (select desc_concepto from equivale_concepto e ");
	               sb.append("\n where  e.id_banco = m.id_banco and e.cargo_abono=m.cargo_abono) ");
	               sb.append("\n and m.id_estatus_trasp='N' and movto_arch=1 and cb.id_banco = m.id_banco ");
	               sb.append("\n And e.no_empresa = m.no_empresa ");
	               sb.append("\n and m.no_empresa between " + datos.get("piEmpresaInf") + " and " + datos.get("piEmpresaSup") + " ");
	               sb.append("\n and m.id_banco between " + datos.get("piBancoInf") + " and " + datos.get("piBancoSup") + " ");
	               sb.append("\n and m.id_chequera like '" + datos.get("psChequera") + "' ");
	               sb.append("\n and m.fec_alta between '" + (datos.get("pdFechaInf")) + "' and '" + (datos.get("pdFechaSup")) + "' ");
	               sb.append("\n group by concepto,m.id_banco,cb.desc_banco,e.nom_empresa,m.no_empresa,m.cargo_abono");
	               if(gsDBM.equals("SYBASE"))
	                     sb.append("\n AT ISOLATION READ UNCOMMITTED ");
	              // sb.append("\n }  AS concepto COMPUTE concepto BY 'desc_banco','nom_empresa'");
	             }
	        }
	        sql = sb.toString();
	        paramRetorno = jdbcTemplate.query(sql, new RowMapper(){
	        	public PropuestaPagoManualDto mapRow(ResultSet rs, int idx) throws SQLException {
					return null;
	        	}});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:MovimientosBancaEDao, M:ejecutarReporteConcepto");
		}
		return paramRetorno;
	}
	
	/**
	 * FunSQL_ReporteBE
	 * @param datos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<PropuestaPagoManualDto> ejecutarReporteBE(Map<String, Object> datos){
		List <PropuestaPagoManualDto> paramRetorno = new ArrayList<PropuestaPagoManualDto>();
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
		Dim squery As String
		Dim squeryizq As String
		Dim squeryder As String
		Dim iConta As Integer
		
		On Error GoTo HayError
		    squery = ""
		    if (pvi_flagQuery <> 1)
		        squery = "SHAPE{"
		    End If*/
		    squery += consultarReporteBE(1, datos);
		    squery += "\n UNION \n";
		    squery += consultarReporteBE(2, datos);
		
		    if(gsDBM.equals("ORACLE") || gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2"))
	            squery += "\n order by cargo_abono, fec_valor ";
            if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE"))
	            squery += "\n order by be.fec_valor,be.importe,be.cargo_abono";
		        
		    if(gsDBM.equals("SYBASE"))
		          squery += "\n AT ISOLATION READ UNCOMMITTED ";
		 
//		    If pvi_flagQuery <> 1 Then
//		        squery = squery & "}  AS CmdBancae COMPUTE CmdBancae,SUM(CmdBancae.'importe_ca') AS total_ca BY 'nom_empresa','desc_banco','id_chequera'"
//		    End If
	        paramRetorno = jdbcTemplate.query(squery, new RowMapper(){
	        	public PropuestaPagoManualDto mapRow(ResultSet rs, int idx) throws SQLException {
					return null;
	        	}});
	 
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:MovimientosBancaEDao, M:ejecutarReporteBE");
		}
		return paramRetorno;
	}
	
	
	@SuppressWarnings("unchecked")
	public String consultarReporteBE(int contador, Map<String, Object> datos){
		String gsDBM = "SQL SERVER";
		String reporte = "";
		String squery = "";
		String squeryizq = "";
		String squeryder = "";
		try{
				//pb_opcion1 = ?
	  		if (datos.get("pb_opcion1").equals("true"))
	  		{
	            reporte += " SELECT be.descripcion AS desc_observacion, "; 
	            reporte += "\n e.nom_empresa, be.no_empresa, be.id_banco, be.secuencia, ";
	            reporte += "\n b.desc_banco, be.id_chequera, be.fec_valor, be.concepto AS sucursal, ";;
	            reporte += "\n be.folio_banco, be.referencia, ccb.id_divisa,";
	            reporte += "\n be.importe AS importe, ";
	            
	            reporte += "\n CASE WHEN c.concepto_set IS NULL THEN be.concepto ELSE c.concepto_set END AS concepto_set, ";
	            reporte += "\n be.observacion, be.cargo_abono, ";
	            
	            if (gsDBM.equals("DB2") || gsDBM.equals("POSTGRESQL"))
	                reporte += "\n COALESCE(be.descripcion,'') || '/' || COALESCE(be.observacion,'') AS desc_observacion, ";
	            else
	                reporte += "\n COALESCE(be.descripcion,'') + '/' + COALESCE(be.observacion,'') AS desc_observacion, ";
	            
	            reporte += "\n CASE WHEN be.cargo_abono = 'E' THEN be.importe * -1 ELSE be.importe END AS importe_ca ";
	            
	            reporte += "\n ,CASE WHEN be.referencia = '1' and be.cargo_abono = 'E' ";
	            reporte += "\n         THEN be.referencia ";
	            reporte += "\n         ELSE '' END AS no_folio_det, ";
	            reporte += "\n  CASE WHEN be.id_estatus_trasp = 'T' THEN 'S' ELSE 'N' END AS ejecutado, be.fec_alta, be.b_trasp_banco, be.b_trasp_conta ";
	            
	            if(contador == 1)
	                reporte += "\n  FROM movto_banca_e be LEFT JOIN equivale_concepto c ON (be.id_banco = c.id_banco AND";
	            else
	                reporte += "\n  FROM hist_movto_banca_e be LEFT JOIN equivale_concepto c ON (be.id_banco = c.id_banco AND";
	            
	            reporte += "\n  be.concepto = c.desc_concepto AND be.cargo_abono = c.cargo_abono)";
	            reporte += "\n ,empresa e, cat_banco b, cat_cta_banco ccb ";
	            reporte += "\n  WHERE ";
	                
	            reporte += "\n be.cargo_abono IN ('I','E') ";
	            reporte += "\n AND be.no_empresa = ccb.no_empresa ";
	            reporte += "\n AND be.id_banco = ccb.id_banco ";
	            reporte += "\n AND be.id_chequera = ccb.id_chequera ";
	            reporte += "\n AND be.no_empresa = e.no_empresa ";
	            reporte += "\n AND be.no_empresa BETWEEN " + datos.get("piEmpresaInf") + " AND " + datos.get("piEmpresaSup") + " ";
	            reporte += "\n AND be.id_banco = b.id_banco ";
	            reporte += "\n AND b.b_banca_elect IN ('A','I') ";
	            reporte += "\n AND be.id_banco BETWEEN " + datos.get("piBancoInf") + " AND " + datos.get("piBancoSup") + " ";
	            reporte += "\n AND be.id_chequera LIKE '" + datos.get("psChequera") + "' ";
	            reporte += "\n AND be.fec_valor BETWEEN '" + funciones.cambiarFecha(""+datos.get("pdFechaInf"), true) + "' AND '" + funciones.cambiarFecha(""+datos.get("pdFechaSup"),true) + "' ";
	            
	            if (datos.get("pbMov").equals("true") &&  !datos.get("psConcepto").equals("") || (!datos.get("psConcepto").equals("")))
	                reporte += "\n AND (c.concepto_set LIKE '" + datos.get("psConcepto") + "%' OR be.concepto LIKE '" + datos.get("psConcepto") + "%')";
	            
	            reporte += "\n AND be.cargo_abono LIKE '" + datos.get("psTipoMov") + "' ";
	            reporte += "\n AND COALESCE(be.movto_arch,0) = " + datos.get("pvOrigenMov") + " ";
	            
	            //Filtra el resultado por movimientos TEF (banamex) o por movimientos del dia (bancomer) solo si es requerido
	            if (datos.get("psMovtoAutomata").equals("true"))
	            {
	                if (datos.get("psMovtosTEF").equals("true"))
	                    reporte += "\n AND be.archivo LIKE 'BT%'";
	                
	                if (datos.get("psMovtosDia").equals("true"))
	                    reporte += "\n AND be.archivo LIKE 'MM%'";
	            }else
	            {
	            	if (datos.get("psMovtosTEF").equals("true"))
	                    reporte += "\n AND be.archivo NOT LIKE 'BT%'";
	                
	            	if (datos.get("psMovtosDia").equals("true"))
	                    reporte += "\n AND NOT (be.archivo LIKE 'MM%' OR be.archivo LIKE 'MD%')";
	            }
	            squery = squery + reporte;
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
		        squeryizq += "\n AND be.no_empresa BETWEEN " + datos.get("piEmpresaInf") + " AND " + datos.get("piEmpresaSup") + " ";
		        squeryizq += "\n AND be.id_banco = b.id_banco ";
		        
		        squery = squery + squeryizq;
		        squery += "\n AND b.b_banca_elect = 'S' ";   // Diferencia
		        squery += "\n AND c.id_banco = be.id_banco "; // Diferencia
		        
		        squeryder += "\n AND be.id_banco BETWEEN " + datos.get("piBancoInf") + " AND " + datos.get("piBancoSup") + " ";
		        squeryder += "\n AND be.id_chequera LIKE '" + datos.get("psChequera") + "' ";
		        squeryder += "\n AND fec_valor between '" + funciones.cambiarFecha(""+datos.get("pdFechaInf"), true) + "' AND '" + funciones.cambiarFecha(""+datos.get("pdFechaSup"), true) + "' ";
		        squeryder += "\n AND  be.concepto = c.desc_concepto "; // Diferencia
		        
		        if(!datos.get("psConcepto").equals("%"))  // Diferencia
		            squeryder += "\n AND concepto_set LIKE '" + datos.get("psConcepto") + "' "; // Diferencia
		        else
		            squeryder += "\n AND concepto LIKE '" + datos.get("psConcepto") + "' "; // Diferencia
		        
		        squeryder += "\n AND be.cargo_abono = c.cargo_abono ";    // Diferencia
		        squeryder += "\n AND be.cargo_abono LIKE '" + datos.get("psTipoMov") + "' ";
		        
		        if(gsDBM.equals("ORACLE"))
	                squeryder += "\n AND coalesce(movto_arch,0) = " + datos.get("pvOrigenMov") + " ";
		        if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE"))
	                squeryder += "\n AND isnull(movto_arch,0) = " + datos.get("pvOrigenMov") + " ";
		        if(gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2"))
	                squeryder += "\n AND COALESCE(movto_arch,0) = " + datos.get("pvOrigenMov") + " ";
	    	}
	 
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:MovimientosBancaEDao, M:consultarReporteBE");
		}
		return squery;
	}
			
			
	//getters && setters
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
}
