package com.webset.set.cashflow.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.derby.tools.sysinfo;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.cashflow.dto.ConceptoFlujo;
import com.webset.set.cashflow.dto.EmpresaDto;
import com.webset.set.cashflow.dto.FechaInabilDto;
import com.webset.set.cashflow.dto.FechasDto;
import com.webset.set.cashflow.dto.GrupoEmpresasDto;
import com.webset.set.cashflow.dto.ParamSpFlujoDto;
import com.webset.set.cashflow.dto.ReporteFlujoDto;
import com.webset.set.cashflow.dto.Saldo;
import com.webset.set.cashflow.dto.TipoConcepto;
import com.webset.set.cashflow.dto.TipoMovto;
import com.webset.set.cashflow.dto.TotalConcepto;
import com.webset.set.cashflow.dto.TotalDiario;
import com.webset.set.cashflow.dto.TotalIngresoEgreso;
import com.webset.set.cashflow.dto.TotalSemanaDto;
import com.webset.set.cashflow.dto.TotalTipoConcepto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.SqlQuery;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unchecked")
public class CashFlowDaoImplements implements CashFlowDaoInterface{
	
	private JdbcTemplate jdbcTemplate;
	private Bitacora bitacora = new Bitacora();
	 private static Logger logger = Logger.getLogger(CashFlowDaoImplements.class);
	private Funciones funciones= new Funciones();
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public List<GrupoEmpresasDto> getGrupoEmpresasDao() {			
		
		String sql = "";
		StringBuffer sb = new StringBuffer();
		
		sb.append(" SELECT DISTINCT ID_SUPER_GRUPO, DESC_SUPER_GRUPO");
		sb.append(" FROM CAT_SUPER_GRUPO");
		sb.append(" ORDER BY ID_SUPER_GRUPO");
		
		sql=sb.toString();		
		System.out.println(sql+"\n"+"\n");
		List <GrupoEmpresasDto> grupoEmpresas = null;
		
		try{
			
			grupoEmpresas = jdbcTemplate.query(sql, new RowMapper(){	
				
			public GrupoEmpresasDto mapRow(ResultSet rs, int idx) throws SQLException {
				
				GrupoEmpresasDto grupoEmpresa = new GrupoEmpresasDto();
				
				grupoEmpresa.setId_super_grupo(rs.getInt("ID_SUPER_GRUPO"));
				grupoEmpresa.setDesc_super_grupo(rs.getString("DESC_SUPER_GRUPO"));
				
				return grupoEmpresa;
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:CashFlow, C:GrupoEmpresas, M:getGrupoEmpresas");
			}		
		
		return grupoEmpresas;
		
	}

	public List<ReporteFlujoDto> getReportesFlujoDao() {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();		
		
		sb.append(" SELECT ID, DESCRIPCION");
		sb.append(" FROM CAT_REPORTES_FLUJO");
		sb.append(" ORDER BY 1");
		
		sql=sb.toString();		
		System.out.println("getReportesFlujoDao "+sql +"\n"+"\n");
		List <ReporteFlujoDto> reportesFlujo = null;
		
		try{
			
			reportesFlujo = jdbcTemplate.query(sql, new RowMapper(){	
				
			public ReporteFlujoDto mapRow(ResultSet rs, int idx) throws SQLException {
				
				ReporteFlujoDto reporteFlujo = new ReporteFlujoDto();
				
				reporteFlujo.setId(rs.getInt("ID"));
				reporteFlujo.setDescripcion(rs.getString("DESCRIPCION"));
				
				return reporteFlujo;
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return reportesFlujo;

		
	}

	public List<EmpresaDto> getEmpresasDao(int idGrupo, int noUsuario) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();	 	
		
		sb.append(" SELECT 0 AS NO_EMPRESA,'------------------------------TODAS------------------------------' AS NOM_EMPRESA" );
		sb.append(" UNION ALL" );
		sb.append(" SELECT e.no_empresa,e.nom_empresa" );
		sb.append(" FROM cat_super_grupo_flujo csf," );
		sb.append("      grupo_empresa ge,empresa e" );
		sb.append(" WHERE csf.id_grupo_flujo = ge.id_grupo_flujo" );
		sb.append(" AND ge.no_empresa = e.no_empresa" );
		sb.append(" AND e.no_empresa IN(select no_empresa from usuario_empresa" );
		sb.append("                     where no_usuario=" + noUsuario + ")" );
		sb.append(" AND csf.id_super_grupo = " + idGrupo );
		
		sql = sb.toString();	
		System.out.println("getEmpresasDao "+sql+"\n"+"\n");
		logger.info(sql);
		
		List <EmpresaDto> listaEmpresas = null;
		
		try{
			
			listaEmpresas = jdbcTemplate.query(sql, new RowMapper(){	
				
			public EmpresaDto mapRow(ResultSet rs, int idx) throws SQLException {
				
				EmpresaDto empresa = new EmpresaDto();
				
				empresa.setNoEmpresa (rs.getInt( "no_empresa"  ) );
				empresa.setNomEmpresa(rs.getString( "nom_empresa" ) );
				
				return empresa;
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return listaEmpresas;

	}

	public List <EmpresaDto> getEmpresaDao(int noEmpresa) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();	
		
		sb.append(" SELECT e.no_empresa,e.nom_empresa" );
		sb.append(" FROM EMPRESA e" );		
		sb.append(" WHERE no_empresa = " + noEmpresa );		
		
		sql = sb.toString();	
		System.out.println("getEmpresaDao "+sql +"\n"+"\n");
		List <EmpresaDto> listaEmpresas = null;
		
		try{
								
			listaEmpresas = jdbcTemplate.query(sql, new RowMapper(){	
				
			public EmpresaDto mapRow(ResultSet rs, int idx) throws SQLException {
				
				EmpresaDto empresa = new EmpresaDto();
				
				empresa.setNoEmpresa (rs.getInt( "no_empresa"  ) );
				empresa.setNomEmpresa(rs.getString( "nom_empresa" ) );
				
				return empresa;
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return listaEmpresas;		
	}

	public List<GrupoEmpresasDto> getGrupoEmpresaDao(int noGrupo) {
		String sql = "";
		StringBuffer sb = new StringBuffer();
		
		sb.append(" SELECT DISTINCT ID_SUPER_GRUPO, DESC_SUPER_GRUPO");
		sb.append(" FROM CAT_SUPER_GRUPO");
		sb.append(" WHERE ID_SUPER_GRUPO = " + noGrupo );
		sb.append(" ORDER BY ID_SUPER_GRUPO");
		
		sql=sb.toString();		
	    System.out.println("getGrupoEmpresaDao "+sql+"\n"+"\n");
		List <GrupoEmpresasDto> grupoEmpresas = null;
		
		try{
			
			grupoEmpresas = jdbcTemplate.query(sql, new RowMapper(){	
				
			public GrupoEmpresasDto mapRow(ResultSet rs, int idx) throws SQLException {
				
				GrupoEmpresasDto grupoEmpresa = new GrupoEmpresasDto();
				
				grupoEmpresa.setId_super_grupo(rs.getInt("ID_SUPER_GRUPO"));
				grupoEmpresa.setDesc_super_grupo(rs.getString("DESC_SUPER_GRUPO"));
				
				return grupoEmpresa;
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:CashFlow, C:GrupoEmpresas, M:getGrupoEmpresas");
			}		
		
		return grupoEmpresas;

	}

	
	public List<ReporteFlujoDto> getReporteFlujoDao( int idReporte )
	{		
		String sql = "";
		StringBuffer sb = new StringBuffer();		
		
		sb.append(" SELECT ID, DESCRIPCION");
		sb.append(" FROM CAT_REPORTES_FLUJO");
		sb.append(" WHERE ID = " + idReporte);
		sb.append(" ORDER BY 1");
		
		sql=sb.toString();		
		System.out.println("getReporteFlujoDao "+sql+"\n"+"\n");
		List <ReporteFlujoDto> reportesFlujo = null;
		
		try{
			
			reportesFlujo = jdbcTemplate.query(sql, new RowMapper(){	
				
			public ReporteFlujoDto mapRow(ResultSet rs, int idx) throws SQLException {
				
				ReporteFlujoDto reporteFlujo = new ReporteFlujoDto();
				
				reporteFlujo.setId(rs.getInt("ID"));
				reporteFlujo.setDescripcion(rs.getString("DESCRIPCION"));
				
				return reporteFlujo;
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return reportesFlujo;

	}

	
	public List<FechaInabilDto> getFecInhabilDao( String fecha ) {
		
		String sql = "";
		List <FechaInabilDto> fechasInhabil = null;
		StringBuffer sb = new StringBuffer();		
		
		sb.append(" SELECT FEC_INHABIL");
		sb.append(" FROM DIA_INHABIL");
		sb.append(" WHERE FEC_INHABIL = convert(date, " + "'" + fecha + "', 103) ");
		
		sql=sb.toString();		
		//System.out.println("getFecInhabilDao "+sql+"\n"+"\n");
		try{
			
			fechasInhabil = jdbcTemplate.query(sql, new RowMapper(){	
				
			public FechaInabilDto mapRow(ResultSet rs, int idx) throws SQLException {
				
				FechaInabilDto fechaInhabil = new FechaInabilDto();
				
				fechaInhabil.setFecha(rs.getString("fec_inhabil"));
								
				return fechaInhabil;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		

		return fechasInhabil;
		
	}

	
	public List<FechasDto> getFechasSistemaDao() {

		String sql = "";
		StringBuffer sb = new StringBuffer();		
		
		sb.append(" SELECT CONVERT(DATETIME,FEC_AYER) as fec_ayer, CONVERT(DATETIME,FEC_HOY) as fec_hoy, CONVERT(DATETIME,FEC_MANANA) as fec_manana");
		sb.append(" FROM FECHAS");	
		
		sql=sb.toString();		
		//System.out.println("getFechasSistemaDao "+sql+"\n"+"\n");
		List <FechasDto> fechas = null;		
		try{
			
			fechas = jdbcTemplate.query(sql, new RowMapper(){	
				
			public FechasDto mapRow(ResultSet rs, int idx) throws SQLException {
				
				FechasDto fecha = new FechasDto();
				
				fecha.setFec_ayer( rs.getString( "fec_ayer" ) );
				fecha.setFec_hoy( rs.getString( "fec_hoy" ) );
				fecha.setFec_manana( rs.getString( "fec_manana" ) );
								
				return fecha;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}				
		return fechas;

	}

	
	public List<TipoMovto> FunSQLGetIngresoEgreso() {		
		String sql = "";
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append("select 'I' AS idTipoMovto, 'INGRESO' AS descTipoMovto");
		sb.append("\n"); sb.append("Union");
		sb.append("\n"); sb.append("select 'E' AS idTipoMovto, 'EGRESO' AS descTipoMovto");
		sb.append("\n"); sb.append("ORDER BY 1 DESC");	
		
		sql=sb.toString();		
		System.out.println("FunSQLGetIngresoEgreso "+sql+"\n"+"\n");
		List <TipoMovto> tiposMovto = null;
		try{
			
			tiposMovto = jdbcTemplate.query(sql, new RowMapper(){	
				
			public TipoMovto mapRow(ResultSet rs, int idx) throws SQLException {
				
				TipoMovto tipoMovto = new TipoMovto();
				
				tipoMovto.setIdTipoMovto( rs.getString( "idTipoMovto" ) );
				tipoMovto.setDescTipoMovto( rs.getString( "descTipoMovto" ) );

								
				return tipoMovto;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return tiposMovto;		
		
	}

	public List<TipoConcepto> FunSQLGetTiposConceptos(int paramNoUsuario,
		            	                              String paramTipoMovto) {
				
		String sql = "";
		StringBuffer sb = new StringBuffer();
			
		sb.append("\n"); sb.append("SELECT DISTINCT ID_TIPO_CONCEPTO, DESCRIPCION");
	    sb.append("\n"); sb.append("From cat_tipo_concepto");
	    //sb.append("\n"); sb.append("WHERE NO_USUARIO = " + paramNoUsuario);
	    sb.append("\n"); sb.append("where INGRESOEGRESO = '"  + paramTipoMovto + "'");
	    sb.append("\n"); sb.append("ORDER BY id_tipo_concepto");
		
		sql=sb.toString();		
		System.out.println("FunSQLGetTiposConceptos "+sql+"\n"+"\n");
		List <TipoConcepto> tiposConcepto = null;
		
		try{
			
			tiposConcepto = jdbcTemplate.query(sql, new RowMapper(){	
				
			public TipoConcepto mapRow(ResultSet rs, int idx) throws SQLException {
				
				TipoConcepto tipoConcepto = new TipoConcepto();
				
				tipoConcepto.setIdTipoConcepto( rs.getInt( "ID_TIPO_CONCEPTO" ) );
				tipoConcepto.setDescripcion( rs.getString( "DESCRIPCION" ) );
								
				return tipoConcepto;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return tiposConcepto;		

	}

	public List<ConceptoFlujo> FunSQLGetConceptosFlujo(int paramNoUsuario,
													   int paramTipoConcepto,int reporte,
													   int empresa,int grupo,
													   String fechaini,String fechfin,
													   String fechahoy,String idTipoMovto,String divisa) {
		
		System.out.println("ESTA EN EL DAO");
		Date  fecha_iniajustado; 
		//System.out.println("asta aqi "+fechahoy);
		String fecha2=funciones.cambiarFecha(fechahoy,true);
		System.out.println("cambia dma "+fecha2);
		Date  fecha=funciones.ponerFechaDate(fecha2);
		System.out.println("fecha sin hota "+fecha);
		fecha_iniajustado = funciones.modificarFecha("d",1,fecha);
		//System.out.println("FECHA MAS 1 "+fecha_iniajustado);
		String fecha3=funciones.ponerFecha(fecha_iniajustado);
		//System.out.println("cambia "+fecha3); 
		String finals=funciones.ponerFechaSola(fecha3);
		//System.out.println("quedo final "+finals); 
		String tipoReporte="";
		int transferencia=0;
		switch (reporte) {
		case 1:
			tipoReporte="O";
			
			break;
		case 2:
			tipoReporte="A";
			break;
		case 3:
			tipoReporte="C";
			break;
		case 4:
			tipoReporte="D";
			break;
		case 5:
			tipoReporte="RA";
			break;
		case 6:
			tipoReporte="OAR";
			break;
		case 7:
			tipoReporte="RA";
			
			  HashMap< Object, Object > inParams = new HashMap< Object, Object >();
              // Nombre del SP a ejecutar
              StoredProcedure storedProcedure = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_mensual_anual_proyectado") {};
              // PARAMETROS DEL SP
              storedProcedure.declareParameter(new SqlParameter("usuario", Types.INTEGER)); 
              inParams.put("usuario", paramNoUsuario);
              storedProcedure.declareParameter(new SqlParameter("Fecha_ini", Types.VARCHAR)); 
              inParams.put("Fecha_ini", fechaini);
              storedProcedure.declareParameter(new SqlParameter("Fecha_fin", Types.VARCHAR)); 
              inParams.put("Fecha_fin", fechfin);
              storedProcedure.declareParameter(new SqlParameter("cuadrante_in", Types.INTEGER)); 
              inParams.put("cuadrante_in", 7);
              storedProcedure.declareParameter(new SqlParameter("cuadrante_eg", Types.INTEGER)); 
              inParams.put("cuadrante_eg", 8);
              storedProcedure.declareParameter(new SqlParameter("idgrupo_flujo", Types.INTEGER)); 
              inParams.put("idgrupo_flujo", grupo);
              storedProcedure.declareParameter(new SqlParameter("no_empresa", Types.INTEGER)); 
              inParams.put("no_empresa", empresa);
              storedProcedure.declareParameter(new SqlParameter("divisa", Types.VARCHAR)); 
              inParams.put("divisa", divisa);
              System.out.println("almacenado "+inParams  );
              // EJECUCION DEL SP
              storedProcedure.execute((Map)inParams);
			break; 
		case 8:
			tipoReporte="RA";
			
			  HashMap< Object, Object > inParams1 = new HashMap< Object, Object >();
              // Nombre del SP a ejecutar
              StoredProcedure storedProcedure1 = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_mensual_anual_proyectado") {};
              // PARAMETROS DEL SP
              storedProcedure1.declareParameter(new SqlParameter("usuario", Types.INTEGER)); 
              inParams1.put("usuario", paramNoUsuario);
              storedProcedure1.declareParameter(new SqlParameter("Fecha_ini", Types.VARCHAR)); 
              inParams1.put("Fecha_ini", fechaini);
              storedProcedure1.declareParameter(new SqlParameter("Fecha_fin", Types.VARCHAR)); 
              inParams1.put("Fecha_fin", fechfin);
              storedProcedure1.declareParameter(new SqlParameter("cuadrante_in", Types.INTEGER)); 
              inParams1.put("cuadrante_in", 7);
              storedProcedure1.declareParameter(new SqlParameter("cuadrante_eg", Types.INTEGER)); 
              inParams1.put("cuadrante_eg", 8);
              storedProcedure1.declareParameter(new SqlParameter("idgrupo_flujo", Types.INTEGER)); 
              inParams1.put("idgrupo_flujo", grupo);
              storedProcedure1.declareParameter(new SqlParameter("no_empresa", Types.INTEGER)); 
              inParams1.put("no_empresa", empresa);
              storedProcedure1.declareParameter(new SqlParameter("divisa", Types.VARCHAR)); 
              inParams1.put("divisa", divisa);
              System.out.println("almacenado "+inParams1  );
              // EJECUCION DEL SP
              storedProcedure1.execute((Map)inParams1);
			break; 
		}
		System.out.println("entro a "+tipoReporte);
		String sql = "";
StringBuffer sb = new StringBuffer();
if(tipoReporte.equals("A") || tipoReporte.equals("O") || tipoReporte.equals("C")){
	if(idTipoMovto.equals("I") && transferencia != 4){ 
												
		sb.append("\n"); sb.append("select 'INTERESES GANADOS' AS 'desc_larga','+' AS 'naturaleza',31000 AS 'id_tipo_concepto', 'INTERESES' AS 'descripcion',31000 AS 'cve_concepto'");
		sb.append("\n"); sb.append("UNION"); 
	}
	 sb.append("\n"); sb.append("SELECT DISTINCT d.desc_larga, c.naturaleza, c.id_tipo_concepto, e.descripcion, d.cve_concepto");	
	 sb.append("\n"); sb.append("FROM  saldo_cuadrante a, cat_rubro b, cat_rubro_concepto c, cat_concepto_flujo d,");	
	 sb.append("\n"); sb.append("cat_tipo_concepto e, cat_super_grupo_flujo f, cat_super_grupo g ");	
	 sb.append("\n"); sb.append("WHERE a.id_rubro = b.id_rubro");	
	 sb.append("\n"); sb.append("And a.id_rubro = b.id_rubro  And a.id_grupo = c.rubro   ");
	 sb.append("\n"); sb.append(" And a.id_rubro = c.subrubro   And c.cve_concepto = d.cve_concepto  ");
	 sb.append("\n"); sb.append("  And c.id_tipo_concepto = e.id_tipo_concepto");
	 /*Todas las empresas*/
	 if(empresa == 0){
		 sb.append("\n"); sb.append("And a.no_empresa in (select id_grupo_flujo from cat_super_grupo_flujo");
		 sb.append("\n"); sb.append(" where id_grupo_flujo in(select no_empresa from usuario_empresa ");
		 sb.append("\n"); sb.append("where no_usuario=" + paramNoUsuario + ")");
		 sb.append("\n"); sb.append("and id_super_grupo in(select id_super_grupo from cat_super_grupo");
		 sb.append("\n"); sb.append(" where id_super_grupo in(" + grupo + "))");
		 sb.append("\n"); sb.append(" ) ");
	 }else{
		 sb.append("\n"); sb.append("And a.no_empresa =(");
		 sb.append("\n"); sb.append("select id_grupo_flujo from cat_super_grupo_flujo");
		 sb.append("\n"); sb.append("where id_grupo_flujo="  + empresa +"");	
		 sb.append("\n"); sb.append(" and id_super_grupo =" + grupo + ")");
	 }		  
	 sb.append("\n"); sb.append("AND  C.ID_TIPO_CONCEPTO = " + paramTipoConcepto);
	 //sb.append("\n"); sb.append(" And b.ingreso_egreso = '" + idTipoMovto + "'");
		  /*PARA LA INVERSION*/
	 if(transferencia == 2 || transferencia == 3){ 		 
		 //sb.append("\n"); sb.append("And a.id_grupo like '98%'");
	 }else if(transferencia == 4 || transferencia == 5){
		 sb.append("\n"); sb.append("And a.id_grupo like '99%'"); 
	 }else{ 
		 sb.append("\n"); sb.append("And (a.id_grupo not like '98%' and a.id_grupo not like '99%' and a.id_grupo not in (47200,97200))"); 
	 }
	 if(tipoReporte.equals("A")){
		 if(idTipoMovto.equals("I")){
			 sb.append("\n"); sb.append("And a.id_cuadrante = 7");
		 }else if(idTipoMovto.equals("E")){
			 sb.append("\n"); sb.append("And a.id_cuadrante = 8");
		 }
	 }  
	 if(tipoReporte.equals("O")){
		 if(idTipoMovto.equals("I")){
			 sb.append("\n"); sb.append("And a.id_cuadrante = 5");
		 }else if(idTipoMovto.equals("E")){
			 sb.append("\n"); sb.append(" And a.id_cuadrante = 6");
		 }
	 }  
	 if(tipoReporte.equals("C")){
		 if(idTipoMovto.equals("I")){
			 sb.append("\n"); sb.append("And a.id_cuadrante in(5,7)");
		 }else if(idTipoMovto.equals("E")){
			 sb.append("\n"); sb.append("And a.id_cuadrante in(6,8)");
		 }
	 }  
	 sb.append("\n"); sb.append(" And a.id_divisa = '" + divisa + "'"); 
	 sb.append("\n"); sb.append("AND a.fec_valor BETWEEN '" +fechaini +"' AND '"+ fechfin +"'");
	
}else if(tipoReporte.equals("RA")){
	if(idTipoMovto.equals("I") && transferencia != 4){
		sb.append("\n"); sb.append("select 'INTERESES GANADOS' AS 'desc_larga','+' AS 'naturaleza',31000 AS 'id_tipo_concepto', 'INTERESES' AS 'descripcion',31000 AS 'cve_concepto'");
		sb.append("\n"); sb.append("UNION"); 
	}
	 sb.append("\n"); sb.append("SELECT DISTINCT d.desc_larga, c.naturaleza, c.id_tipo_concepto, e.descripcion, d.cve_concepto");	
	 sb.append("\n"); sb.append("FROM  VISTA_MOVIMIENTO a, cat_rubro b, cat_rubro_concepto c, cat_concepto_flujo d,");	
	 sb.append("\n"); sb.append("cat_tipo_concepto e, cat_super_grupo_flujo f, cat_super_grupo g ");	
	 sb.append("\n"); sb.append("WHERE a.id_rubro = b.id_rubro");	
	 sb.append("\n"); sb.append("And a.id_rubro = b.id_rubro  And a.id_grupo = c.rubro   ");											 
	 sb.append("\n"); sb.append(" And a.id_rubro = c.subrubro   And c.cve_concepto = d.cve_concepto  ");
	 sb.append("\n"); sb.append("  And c.id_tipo_concepto = e.id_tipo_concepto");
	 /*Todas las empresas*/
	 if(empresa==0){
		 sb.append("\n"); sb.append("And a.no_empresa in (select id_grupo_flujo from cat_super_grupo_flujo");
		 sb.append("\n"); sb.append(" where id_grupo_flujo in(select no_empresa from usuario_empresa ");
		 sb.append("\n"); sb.append("where no_usuario=" + paramNoUsuario + ")");
		 sb.append("\n"); sb.append("and id_super_grupo in(select id_super_grupo from cat_super_grupo");
		 sb.append("\n"); sb.append(" where id_super_grupo in(" + grupo + "))");
		 sb.append("\n"); sb.append(" ) ");
	 }else{
		 sb.append("\n"); sb.append("And a.no_empresa =(");
		 sb.append("\n"); sb.append("select id_grupo_flujo from cat_super_grupo_flujo");
		 sb.append("\n"); sb.append("where id_grupo_flujo="  + empresa +"");	
		 sb.append("\n"); sb.append(" and id_super_grupo =" + grupo + ")");
	 }
	 sb.append("\n"); sb.append("AND  C.ID_TIPO_CONCEPTO = " + paramTipoConcepto);
	 //sb.append("\n"); sb.append(" AND a.id_tipo_movto = '" + idTipoMovto + "'");
	 /*PARA LA INVERSION*/
	 if(transferencia == 2 || transferencia == 3){
		 //sb.append("\n"); sb.append("And a.id_grupo like '98%'");
	 }else if(transferencia == 4 || transferencia == 5){
		 sb.append("\n"); sb.append("And a.id_grupo like '99%'"); 
	 }else{ 																														
		 sb.append("\n"); sb.append("And (a.id_grupo not like '98%' and a.id_grupo not like '99%' and a.id_grupo not in (47200,97200))"); 
	 }
	 sb.append("\n"); sb.append(" And a.id_divisa = '" + divisa + "'"); 
	 sb.append("\n"); sb.append("AND a.fec_valor BETWEEN '" +fechaini +"' AND '"+ fecha2 +"'");
	 sb.append("\n"); sb.append("UNION"); 
	/************AJUSTADO**************/
	 if(idTipoMovto.equals("I") && transferencia != 4){ 
			sb.append("\n"); sb.append("select 'INTERESES GANADOS' AS 'desc_larga','+' AS 'naturaleza',31000 AS 'id_tipo_concepto', 'INTERESES' AS 'descripcion',31000 AS 'cve_concepto'");
			sb.append("\n"); sb.append("UNION"); 
		} 
	 sb.append("\n"); sb.append("SELECT DISTINCT d.desc_larga, c.naturaleza, c.id_tipo_concepto, e.descripcion, d.cve_concepto");
	 sb.append("\n"); sb.append("FROM  saldo_cuadrante a, cat_rubro b, cat_rubro_concepto c, cat_concepto_flujo d,");
	 sb.append("\n"); sb.append("cat_tipo_concepto e, cat_super_grupo_flujo f, cat_super_grupo g");
	 sb.append("\n"); sb.append("WHERE a.id_rubro = b.id_rubro  And a.id_rubro = b.id_rubro");
																 
	 sb.append("\n"); sb.append("And a.id_grupo = c.rubro");
	 sb.append("\n"); sb.append("And a.id_rubro = c.subrubro");
																		 
	 sb.append("\n"); sb.append("And c.cve_concepto = d.cve_concepto And c.id_tipo_concepto = e.id_tipo_concepto");
	 if(empresa==0){
		 sb.append("\n"); sb.append("And a.no_empresa in (select id_grupo_flujo from cat_super_grupo_flujo");
		 sb.append("\n"); sb.append(" where id_grupo_flujo in(select no_empresa from usuario_empresa ");
		 sb.append("\n"); sb.append("where no_usuario=" + paramNoUsuario + ")");
		 sb.append("\n"); sb.append("and id_super_grupo in(select id_super_grupo from cat_super_grupo");
		 sb.append("\n"); sb.append(" where id_super_grupo in(" + grupo + "))");
		 sb.append("\n"); sb.append(" ) ");
	}
	 sb.append("\n"); sb.append("AND  C.ID_TIPO_CONCEPTO = " + paramTipoConcepto);
    // sb.append("\n"); sb.append(" And b.ingreso_egreso = '" + idTipoMovto + "'");
		 /*PARA LA INVERSION*/
	 if(transferencia == 2 || transferencia == 3){ 
		 //sb.append("\n"); sb.append("And a.id_grupo like '98%'");
	 }else if(transferencia == 4 || transferencia == 5){
		 sb.append("\n"); sb.append("And a.id_grupo like '99%'"); 
	 }else{ 
		 sb.append("\n"); sb.append("And (a.id_grupo not like '98%' and a.id_grupo not like '99%' and a.id_grupo not in (47200,97200))"); 
	 }
	 if(tipoReporte.equals("RA")){
		 if(idTipoMovto.equals("I")){ 
			 sb.append("\n"); sb.append("And a.id_cuadrante = 5");
		 }else if(idTipoMovto.equals("E")){
			 sb.append("\n"); sb.append("And a.id_cuadrante = 6");
		 }
	 }
	 sb.append("\n"); sb.append(" And a.id_divisa = '" + divisa + "'"); 
	 if(empresa == 0){}else{
		 sb.append("\n"); sb.append(" And a.no_empresa =(");
												  
		 sb.append("\n"); sb.append("select id_grupo_flujo from cat_super_grupo_flujo");
		 sb.append("\n"); sb.append(" where id_grupo_flujo=" + empresa +"");
		 sb.append("\n"); sb.append("and id_super_grupo =" + grupo +  ")");
	 }
	 //aqi fue
	 sb.append("\n"); sb.append("AND a.fec_valor BETWEEN '" +finals +"' AND '"+ fechfin +"'");
	 
	 
}else if(tipoReporte.equals("OAR")){
	if(idTipoMovto == "I" && transferencia != 4){ 
												
		sb.append("\n"); sb.append("select 'INTERESES GANADOS' AS 'desc_larga','+' AS 'naturaleza',31000 AS 'id_tipo_concepto', 'INTERESES' AS 'descripcion',31000 AS 'cve_concepto'");
		sb.append("\n"); sb.append("UNION"); 
	}
	
	 sb.append("\n"); sb.append("SELECT DISTINCT d.desc_larga, c.naturaleza, c.id_tipo_concepto, e.descripcion, d.cve_concepto");	
	 sb.append("\n"); sb.append("FROM  VISTA_MOVIMIENTO a, cat_rubro b, cat_rubro_concepto c, cat_concepto_flujo d,");	
	 sb.append("\n"); sb.append("cat_tipo_concepto e, cat_super_grupo_flujo f, cat_super_grupo g ");	
	 sb.append("\n"); sb.append("WHERE a.id_rubro = b.id_rubro");	
	 sb.append("\n"); sb.append("And a.id_rubro = b.id_rubro  And a.id_grupo = c.rubro   "); 
	 sb.append("\n"); sb.append(" And a.id_rubro = c.subrubro   And c.cve_concepto = d.cve_concepto  ");
	 sb.append("\n"); sb.append("  And c.id_tipo_concepto = e.id_tipo_concepto");
	 /*Todas las empresas*/
	 if(empresa == 0){
		 sb.append("\n"); sb.append("And a.no_empresa in (select id_grupo_flujo from cat_super_grupo_flujo");
		 sb.append("\n"); sb.append(" where id_grupo_flujo in(select no_empresa from usuario_empresa ");
		 sb.append("\n"); sb.append("where no_usuario=" + paramNoUsuario + ")");
		 sb.append("\n"); sb.append("and id_super_grupo in(select id_super_grupo from cat_super_grupo");
		 sb.append("\n"); sb.append(" where id_super_grupo in(" + grupo + "))");
		 sb.append("\n"); sb.append(" ) ");
	 }else{
		 sb.append("\n"); sb.append("And a.no_empresa =(");
		 sb.append("\n"); sb.append("select id_grupo_flujo from cat_super_grupo_flujo");
		 sb.append("\n"); sb.append("where id_grupo_flujo="  + empresa +"");	
		 sb.append("\n"); sb.append(" and id_super_grupo =" + grupo + ")");
	 }	
	 sb.append("\n"); sb.append(" AND C.ID_TIPO_CONCEPTO = " + paramTipoConcepto);
	 //sb.append("\n"); sb.append(" And a.id_tipo_movto = '" + idTipoMovto + "'");
	 if(transferencia == 2 || transferencia == 3){
		 //sb.append("\n"); sb.append("And a.id_grupo like '98%'");
	 }else if(transferencia == 4 || transferencia == 5){
		 sb.append("\n"); sb.append("And a.id_grupo like '99%'"); 
	 }else{ 
																																	
		 sb.append("\n"); sb.append("And (a.id_grupo not like '98%' and a.id_grupo not like '99%' and a.id_grupo not in (47200,97200))"); 
	 }
	 sb.append("\n"); sb.append(" And a.id_divisa = '" + divisa + "'"); 
	 sb.append("\n"); sb.append("AND a.fec_valor BETWEEN '" +fechaini +"' AND '"+ fecha2 +"'");
	 
	 /****************************************UNION CON ORIGINAL Y AJUSTADO *********************************/
	 sb.append("\n"); sb.append("UNION");
	 if(idTipoMovto.equals( "I") && transferencia != 4){ 
			sb.append("\n"); sb.append("select 'INTERESES GANADOS' AS 'desc_larga','+' AS 'naturaleza',31000 AS 'id_tipo_concepto', 'INTERESES' AS 'descripcion',31000 AS 'cve_concepto'");
			sb.append("\n"); sb.append("UNION"); 
		}
		
		 sb.append("\n"); sb.append("SELECT DISTINCT d.desc_larga, c.naturaleza, c.id_tipo_concepto, e.descripcion, d.cve_concepto");	
		 sb.append("\n"); sb.append("FROM  saldo_cuadrante a, cat_rubro b, cat_rubro_concepto c, cat_concepto_flujo d,");	
		 sb.append("\n"); sb.append("cat_tipo_concepto e, cat_super_grupo_flujo f, cat_super_grupo g ");	
		 sb.append("\n"); sb.append("WHERE a.id_rubro = b.id_rubro");	
		 sb.append("\n"); sb.append("And a.id_rubro = b.id_rubro  And a.id_grupo = c.rubro   ");					 
		 sb.append("\n"); sb.append(" And a.id_rubro = c.subrubro   And c.cve_concepto = d.cve_concepto  ");
		 sb.append("\n"); sb.append("  And c.id_tipo_concepto = e.id_tipo_concepto");
		 /*Todas las empresas*/
		 if(empresa == 0){
			 sb.append("\n"); sb.append("And a.no_empresa in (select id_grupo_flujo from cat_super_grupo_flujo");
			 sb.append("\n"); sb.append("where id_grupo_flujo in(select no_empresa from usuario_empresa ");
			 sb.append("\n"); sb.append("where no_usuario=" + paramNoUsuario + ")");
			 sb.append("\n"); sb.append("and id_super_grupo in(select id_super_grupo from cat_super_grupo");
			 sb.append("\n"); sb.append("where id_super_grupo in(" + grupo + "))");
			 sb.append("\n"); sb.append(")");
		 }
	 sb.append("\n"); sb.append("AND  C.ID_TIPO_CONCEPTO = " + paramTipoConcepto);
	 //sb.append("\n"); sb.append(" And b.ingreso_egreso = '" + idTipoMovto + "'");
		  /*PARA LA INVERSION*/
		 if(transferencia == 2 || transferencia == 3){
												  
			 //sb.append("\n"); sb.append("And a.id_grupo like '98%'");
		 }else if(transferencia == 4 || transferencia == 5){
			 sb.append("\n"); sb.append("And a.id_grupo like '99%'"); 
		 }else{ 
			 sb.append("\n"); sb.append("And (a.id_grupo not like '98%' and a.id_grupo not like '99%' and a.id_grupo not in (47200,97200))"); 
		 }  
	 
	 if(tipoReporte.equals("RA")){
		 if(idTipoMovto.equals("I")){
							 
			 sb.append("\n"); sb.append("And a.id_cuadrante = 5,7");
		 }else if(idTipoMovto.equals("E")){
			 sb.append("\n"); sb.append("And a.id_cuadrante = 6,8");
		 }
		  
		 sb.append("\n"); sb.append(" And a.id_divisa = '" + divisa + "'"); 
	
		 if(empresa == 0){}else{
			 sb.append("\n"); sb.append(" And a.no_empresa =(");
												  
			 sb.append("\n"); sb.append("select id_grupo_flujo from cat_super_grupo_flujo");
			 sb.append("\n"); sb.append(" where id_grupo_flujo=" + empresa +"");
			 sb.append("\n"); sb.append("and id_super_grupo =" + grupo +  ")");
		 } 
		 sb.append("\n"); sb.append("AND a.fec_valor BETWEEN '" +fechaini +"' AND '"+ fechfin +"'"); 
		 
	 }
}else if(tipoReporte.equals("D")){
	if(idTipoMovto.equals("I") && transferencia != 4){
	
												
		sb.append("\n"); sb.append("select 'INTERESES GANADOS' AS 'desc_larga','+' AS 'naturaleza',31000 AS 'id_tipo_concepto', 'INTERESES' AS 'descripcion',31000 AS 'cve_concepto'");
		sb.append("\n"); sb.append("UNION"); 
	}
	
	 sb.append("\n"); sb.append("SELECT DISTINCT d.desc_larga, c.naturaleza, c.id_tipo_concepto, e.descripcion, d.cve_concepto");	
	 sb.append("\n"); sb.append("FROM  VISTA_MOVIMIENTO a, cat_rubro b, cat_rubro_concepto c, cat_concepto_flujo d,");	
	 sb.append("\n"); sb.append("cat_tipo_concepto e, cat_super_grupo_flujo f, cat_super_grupo g ");	
	 sb.append("\n"); sb.append("WHERE a.id_rubro = b.id_rubro");	
	 sb.append("\n"); sb.append("And a.id_rubro = b.id_rubro  And a.id_grupo = c.rubro   ");
													  
														 
	 sb.append("\n"); sb.append(" And a.id_rubro = c.subrubro   And c.cve_concepto = d.cve_concepto  ");
	 sb.append("\n"); sb.append("  And c.id_tipo_concepto = e.id_tipo_concepto");
	 /*Todas las empresas*/
	 if(empresa==0){
		 sb.append("\n"); sb.append("And a.no_empresa in (select id_grupo_flujo from cat_super_grupo_flujo");
		 sb.append("\n"); sb.append(" where id_grupo_flujo in(select no_empresa from usuario_empresa ");
		 sb.append("\n"); sb.append("where no_usuario=" + paramNoUsuario + ")");
		 sb.append("\n"); sb.append("and id_super_grupo in(select id_super_grupo from cat_super_grupo");
		 sb.append("\n"); sb.append(" where id_super_grupo in(" + grupo + "))");
		 sb.append("\n"); sb.append(" ) ");
	 }else{
		 sb.append("\n"); sb.append("And a.no_empresa =(");
		 sb.append("\n"); sb.append("select id_grupo_flujo from cat_super_grupo_flujo");
		 sb.append("\n"); sb.append("where id_grupo_flujo="  + empresa +"");	
		 sb.append("\n"); sb.append(" and id_super_grupo =" + grupo + ")");
	 }		  
	 sb.append("\n"); sb.append("AND  C.ID_TIPO_CONCEPTO = " + paramTipoConcepto);
     //sb.append("\n"); sb.append(" And a.id_tipo_movto = '" + idTipoMovto + "'");
		 
	 if(transferencia == 2 || transferencia == 3){
		 sb.append("\n"); sb.append("And a.id_grupo like '98%'");
	 }else if(transferencia == 4 || transferencia == 5){
		 sb.append("\n"); sb.append("And a.id_grupo like '99%'"); 
	 }else{ 
																								
		 sb.append("\n"); sb.append("And (a.id_grupo not like '98%' and a.id_grupo not like '99%' and a.id_grupo not in (47200,97200))"); 
	 }
	 sb.append("\n"); sb.append(" And a.id_divisa = '" + divisa + "'"); 
	 sb.append("\n"); sb.append("AND a.fec_valor BETWEEN '" +fechaini +"' AND '"+ fecha2 +"'");
	 /********************************UNION CON ORIGINAL Y AJUSTADO************************************************/
	 sb.append("\n"); sb.append("UNION");
	 if(idTipoMovto.equals("I") && transferencia != 4){ 
			sb.append("\n"); sb.append("select 'INTERESES GANADOS' AS 'desc_larga','+' AS 'naturaleza',31000 AS 'id_tipo_concepto', 'INTERESES' AS 'descripcion',31000 AS 'cve_concepto'");
			sb.append("\n"); sb.append("UNION"); 
		}
		 sb.append("\n"); sb.append("SELECT DISTINCT d.desc_larga, c.naturaleza, c.id_tipo_concepto, e.descripcion, d.cve_concepto");	
		 sb.append("\n"); sb.append("FROM  saldo_cuadrante a, cat_rubro b, cat_rubro_concepto c, cat_concepto_flujo d,");	
		 sb.append("\n"); sb.append("cat_tipo_concepto e, cat_super_grupo_flujo f, cat_super_grupo g ");	
		 sb.append("\n"); sb.append("WHERE a.id_rubro = b.id_rubro");	
		 sb.append("\n"); sb.append("And a.id_rubro = b.id_rubro  And a.id_grupo = c.rubro   ");
															  
																 
		 sb.append("\n"); sb.append(" And a.id_rubro = c.subrubro   And c.cve_concepto = d.cve_concepto  ");
		 sb.append("\n"); sb.append("  And c.id_tipo_concepto = e.id_tipo_concepto");
		 /*Todas las empresas*/
		 if(empresa==0){
			 sb.append("\n"); sb.append("And a.no_empresa in (select id_grupo_flujo from cat_super_grupo_flujo");
			 sb.append("\n"); sb.append(" where id_grupo_flujo in(select no_empresa from usuario_empresa ");
			 sb.append("\n"); sb.append("where no_usuario=" + paramNoUsuario + ")");
			 sb.append("\n"); sb.append("and id_super_grupo in(select id_super_grupo from cat_super_grupo");
			 sb.append("\n"); sb.append(" where id_super_grupo in(" + grupo + "))");
			 sb.append("\n"); sb.append(" ) ");
		 }else{
			 sb.append("\n"); sb.append("And a.no_empresa =(");
			 sb.append("\n"); sb.append("select id_grupo_flujo from cat_super_grupo_flujo");
			 sb.append("\n"); sb.append("where id_grupo_flujo="  + empresa +"");	
			 sb.append("\n"); sb.append(" and id_super_grupo =" + grupo + ")");
		 }		  
		sb.append("\n"); sb.append("AND  C.ID_TIPO_CONCEPTO = " + paramTipoConcepto);
		//sb.append("\n"); sb.append(" And b.ingreso_egreso = '" + idTipoMovto + "'");
		 /*PARA LA INVERSION*/
		 if(transferencia == 2 || transferencia == 3){
			 sb.append("\n"); sb.append("And a.id_grupo like '98%'");
		 }else if(transferencia == 4 || transferencia == 5){
			 sb.append("\n"); sb.append("And a.id_grupo like '99%'"); 
		 }else{ 
			 sb.append("\n"); sb.append("And (a.id_grupo not like '98%' and a.id_grupo not like '99%' and a.id_grupo not in (47200,97200))"); 
		 }
		 if(tipoReporte.equals("D")){
			 if(idTipoMovto.equals("I")){
				 sb.append("\n"); sb.append("And a.id_cuadrante = 5");
			 }else if(idTipoMovto.equals("E")){
				 sb.append("\n"); sb.append("And a.id_cuadrante = 6");
			 }
		 }
		 sb.append("\n"); sb.append(" And a.id_divisa = '" + divisa + "'"); 
	
		 if(empresa == 0){}else{
			 sb.append("\n"); sb.append(" And a.no_empresa =(");
												  
			 sb.append("\n"); sb.append("select id_grupo_flujo from cat_super_grupo_flujo");
			 sb.append("\n"); sb.append(" where id_grupo_flujo=" + empresa +"");
			 sb.append("\n"); sb.append("and id_super_grupo =" + grupo +  ")");
		 } 
		 sb.append("\n"); sb.append("AND a.fec_valor BETWEEN '" +fechaini +"' AND '"+ fechfin +"'"); 
	 
} 

 sb.append("\n"); sb.append("ORDER BY  c.id_tipo_concepto, d.desc_larga");

																	  

	 
 
												  
																																	 
				
		/*
		String sql = "";
		StringBuffer sb = new StringBuffer();
		
		
	    sb.append("\n"); sb.append("SELECT DISTINCT C.CVE_CONCEPTO, DESC_LARGA FROM");	    
	    sb.append("\n"); sb.append("saldo_cuadrante a, cat_rubro b, cat_rubro_concepto c, cat_concepto_flujo d, cat_tipo_concepto e, cat_super_grupo_flujo f, cat_super_grupo g ");	    
	    sb.append("\n"); sb.append("  WHERE a.id_rubro = b.id_rubro And a.id_rubro = b.id_rubro  And a.id_grupo = c.rubro  And a.id_rubro = c.subrubro   And c.cve_concepto = d.cve_concepto    And c.id_tipo_concepto = e.id_tipo_concepto   AND");	    
	    sb.append("\n"); sb.append("C.ID_TIPO_CONCEPTO = " + paramTipoConcepto);	  
		 */
	   /* sb.append("\n"); sb.append("SELECT DISTINCT CVE_CONCEPTO, DESC_LARGA");	    
	    sb.append("\n"); sb.append("From DIARIO_REAL_AJUSTADO");	    
	    sb.append("\n"); sb.append("WHERE NO_USUARIO = " + paramNoUsuario);	    
	    sb.append("\n"); sb.append("AND ID_TIPO_CONCEPTO = " + paramTipoConcepto);	*/ 
		sql=sb.toString();		
		System.out.println("FunSQLGetConceptosFlujo "+sql+"\n"+"\n");
		List <ConceptoFlujo> conceptos = null;
		
		try{
			
			conceptos = jdbcTemplate.query(sql, new RowMapper(){	
				
			public ConceptoFlujo mapRow(ResultSet rs, int idx) throws SQLException {
				
				ConceptoFlujo concepto = new ConceptoFlujo();
				
				concepto.setCveConcepto( rs.getInt( "CVE_CONCEPTO" ) );
				concepto.setDescLarga( rs.getString( "DESC_LARGA" ) );
								
				return concepto;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return conceptos;		
	
	}

	public int funSQLUpdateRowForTotalIngresoEgreso(int paramNoUsuario,
			String paramTipoMovto, int paramRow) {
		
			int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update semanal_real_ajustado" );
	    sb.append("\n"); sb.append( "SET row_ingresoegreso = " + paramRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + paramNoUsuario);
	    sb.append("\n"); sb.append( "AND ingreso_egreso = '" + paramTipoMovto + "'");
	    System.out.println("funSQLUpdateRowForTotalIngresoEgreso "+sb+"\n"+"\n");
	    resp = jdbcTemplate.update(sb.toString());
	 
		return resp;
	}
	
	public int funSQLUpdateRowForTotalIngresoEgresoOAR(int paramNoUsuario,
			String paramTipoMovto, int paramRow) {
		
			int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update depura_original_ajustado_real" );
	    sb.append("\n"); sb.append( "SET row_ingresoegreso = " + paramRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + paramNoUsuario);
	    sb.append("\n"); sb.append( "AND ingeg = '" + paramTipoMovto + "'");
	    System.out.println("funSQLUpdateRowForTotalIngresoEgresoOAR "+sb+"\n"+"\n");
	    resp = jdbcTemplate.update(sb.toString());
	 
		return resp;
	}
	public int funSQLUpdateRowForTotalIngresoEgresoOriginalAjustado(int paramNoUsuario,
			String paramTipoMovto, int paramRow) {
		
			int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update depura_mensual_comparativo_proyajust" );
	    sb.append("\n"); sb.append( "SET row_ingresoegreso = " + paramRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + paramNoUsuario);
	    sb.append("\n"); sb.append( "AND ingeg = '" + paramTipoMovto + "'");
	    System.out.println("funSQLUpdateRowForTotalIngresoEgresoOriginalAjustado "+sb+"\n"+"\n");
	    resp = jdbcTemplate.update(sb.toString());
	 
		return resp;
	}
	public int funSQLUpdateRowForTotalIngresoEgresoOriginal(int paramNoUsuario,
			String paramTipoMovto, int paramRow) {
		
			int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update temp_saldo_cuadrante_diario" );
	    sb.append("\n"); sb.append( "SET row_ingresoegreso = " + paramRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + paramNoUsuario);
	    sb.append("\n"); sb.append( "AND ingreso_egreso = '" + paramTipoMovto + "'");
	    System.out.println("funSQLUpdateRowForTotalIngresoEgresoDiario "+sb+"\n"+"\n");
	    resp = jdbcTemplate.update(sb.toString());
	 
		return resp;
	}
	
	public int funSQLUpdateRowForTotalIngresoEgresoAjustado(int paramNoUsuario,
			String paramTipoMovto, int paramRow) {
		
			int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update temp_saldo_cuadrante_nuevo" );
	    sb.append("\n"); sb.append( "SET row_ingresoegreso = " + paramRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + paramNoUsuario);
	    sb.append("\n"); sb.append( "AND ingreso_egreso = '" + paramTipoMovto + "'");
	    System.out.println("funSQLUpdateRowForTotalIngresoEgresoAjustado "+sb+"\n"+"\n");
	    resp = jdbcTemplate.update(sb.toString());
	 
		return resp;
	}
	/*Hector*/
	public int funSQLUpdateRowForTotalIngresoEgresoMensual(int paramNoUsuario,
			String paramTipoMovto, int paramRow) {
		
			int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update depura_mensual_real_ajustado" );
	    sb.append("\n"); sb.append( "SET row_ingresoegreso = " + paramRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + paramNoUsuario);
	    sb.append("\n"); sb.append( "AND ingreso_egreso = '" + paramTipoMovto + "'");
	    System.out.println("funSQLUpdateRowForTotalIngresoEgresoMensual "+sb+"\n"+"\n");
	    resp = jdbcTemplate.update(sb.toString());
	 
		return resp;
	}	/*Hector*/
	
	public int funSQLUpdateRowForTotalIngresoEgresoComparativorealajustado(int paramNoUsuario,
			String paramTipoMovto, int paramRow) {
		
			int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update depura_mensual_comparativo_realajust" );
	    sb.append("\n"); sb.append( "SET row_ingresoegreso = " + paramRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + paramNoUsuario);
	    sb.append("\n"); sb.append( "AND ingeg = '" + paramTipoMovto + "'");
	    System.out.println("funSQLUpdateRowForTotalIngresoEgresoComparativorealajustado "+sb+"\n"+"\n");
	    resp = jdbcTemplate.update(sb.toString());
	 
		return resp;
	}
	public int funSQLUpdateRowForTipoConcepto(int paramNoUsuario,
			int lngIdTipoConcepto, int paramRow) {

		int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update semanal_real_ajustado" );
	    sb.append("\n"); sb.append( "SET row_tipo_concepto = " + paramRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + paramNoUsuario);
	    sb.append("\n"); sb.append( "AND id_tipo_concepto = " + lngIdTipoConcepto);
	    System.out.println("funSQLUpdateRowForTipoConcepto "+sb+"\n"+"\n");
	    resp = jdbcTemplate.update(sb.toString());
		
		return resp;

	}
	
	public int funSQLUpdateRowForTipoConceptoOAR(int paramNoUsuario,
			int lngIdTipoConcepto, int paramRow) {

		int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update depura_original_ajustado_real" );
	    sb.append("\n"); sb.append( "SET row_tipo_concepto = " + paramRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + paramNoUsuario);
	    sb.append("\n"); sb.append( "AND id_tipo_concepto = " + lngIdTipoConcepto);
	    System.out.println("funSQLUpdateRowForTipoConceptoOAR "+sb+"\n"+"\n");
	    resp = jdbcTemplate.update(sb.toString());
		
		return resp;

	}
	public int funSQLUpdateRowForTipoConceptoOriginalAjustado(int paramNoUsuario,
			int lngIdTipoConcepto, int paramRow) {

		int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update depura_mensual_comparativo_proyajust" );
	    sb.append("\n"); sb.append( "SET row_tipo_concepto = " + paramRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + paramNoUsuario);
	    sb.append("\n"); sb.append( "AND id_tipo_concepto = " + lngIdTipoConcepto);
	    System.out.println("funSQLUpdateRowForTipoConceptoOriginalAjustado "+sb+"\n"+"\n");
	    resp = jdbcTemplate.update(sb.toString());
		
		return resp;

	}
	
	public int funSQLUpdateRowForTipoConceptoOriginal(int paramNoUsuario,
			int lngIdTipoConcepto, int paramRow) {

		int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update temp_saldo_cuadrante_diario" );
	    sb.append("\n"); sb.append( "SET row_tipo_concepto = " + paramRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + paramNoUsuario);
	    sb.append("\n"); sb.append( "AND id_tipo_concepto = " + lngIdTipoConcepto);
	    System.out.println("funSQLUpdateRowForTipoConceptoDiario "+sb+"\n"+"\n");
	    resp = jdbcTemplate.update(sb.toString());
		
		return resp;

	}
	
	public int funSQLUpdateRowForTipoConceptoAjustado(int paramNoUsuario,
			int lngIdTipoConcepto, int paramRow) {

		int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update temp_saldo_cuadrante_nuevo" );
	    sb.append("\n"); sb.append( "SET row_tipo_concepto = " + paramRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + paramNoUsuario);
	    sb.append("\n"); sb.append( "AND id_tipo_concepto = " + lngIdTipoConcepto);
	    System.out.println("funSQLUpdateRowForTipoConceptoAjustado "+sb+"\n"+"\n");
	    resp = jdbcTemplate.update(sb.toString());
		
		return resp;

	}
	/*Hector*/
	public int funSQLUpdateRowForTipoConceptoMesual(int paramNoUsuario,
			int lngIdTipoConcepto, int paramRow) {

		int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update depura_mensual_real_ajustado" );
	    sb.append("\n"); sb.append( "SET row_tipo_concepto = " + paramRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + paramNoUsuario);
	    sb.append("\n"); sb.append( "AND id_tipo_concepto = " + lngIdTipoConcepto);
	    System.out.println("funSQLUpdateRowForTipoConceptoMesual "+sb+"\n"+"\n");
	    resp = jdbcTemplate.update(sb.toString());
		
		return resp;

	}/*Hector*/
	
	public int funSQLUpdateRowForTipoConceptocomparativoRealajustado(int paramNoUsuario,
			int lngIdTipoConcepto, int paramRow) {

		int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update depura_mensual_comparativo_realajust" );
	    sb.append("\n"); sb.append( "SET row_tipo_concepto = " + paramRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + paramNoUsuario);
	    sb.append("\n"); sb.append( "AND id_tipo_concepto = " + lngIdTipoConcepto);
	    System.out.println("funSQLUpdateRowForTipoConceptocomparativoRealajustado "+sb+"\n"+"\n");
	    resp = jdbcTemplate.update(sb.toString());
		
		return resp;

	}

	public int funSQLUpdateRowForConcepto(int paramNoUsuario, int lngCveConcepto, 
										  int paramRow) {
		
		int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update semanal_real_ajustado" );
	    sb.append("\n"); sb.append( "SET row_concepto = " + paramRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + paramNoUsuario);
	    sb.append("\n"); sb.append( "AND cve_concepto = " + lngCveConcepto);
		System.out.println("funSQLUpdateRowForConcepto "+sb+"\n"+"\n");
	    resp = jdbcTemplate.update(sb.toString());
		
		return resp;
	}
	
	public int funSQLUpdateRowForConceptoOAR(int paramNoUsuario, int lngCveConcepto, 
			  int paramRow) {

		int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update depura_original_ajustado_real" );
		sb.append("\n"); sb.append( "SET row_concepto = " + paramRow);
		sb.append("\n"); sb.append( "WHERE usuario = " + paramNoUsuario);
		sb.append("\n"); sb.append( "AND cve_concepto = " + lngCveConcepto);
		System.out.println("funSQLUpdateRowForConceptoOAR "+sb+"\n"+"\n");
		resp = jdbcTemplate.update(sb.toString());
		
		return resp;
}
	public int funSQLUpdateRowForConceptoOriginalAjustado(int paramNoUsuario, int lngCveConcepto, 
			  int paramRow) {
			
			int resp;
			
			StringBuffer sb = new StringBuffer();
			
			sb.append("\n"); sb.append( "Update depura_mensual_comparativo_proyajust" );
			sb.append("\n"); sb.append( "SET row_concepto = " + paramRow);
			sb.append("\n"); sb.append( "WHERE usuario = " + paramNoUsuario);
			sb.append("\n"); sb.append( "AND cve_concepto = " + lngCveConcepto);
			System.out.println("funSQLUpdateRowForConcepto "+sb+"\n"+"\n");
			resp = jdbcTemplate.update(sb.toString());
			
			return resp;
			}

	
	public int funSQLUpdateRowForConceptoOriginal(int paramNoUsuario, int lngCveConcepto, 
			  int paramRow) {
				
				int resp;
				
				StringBuffer sb = new StringBuffer();
				
				sb.append("\n"); sb.append( "Update temp_saldo_cuadrante_diario" );
				sb.append("\n"); sb.append( "SET row_concepto = " + paramRow);
				sb.append("\n"); sb.append( "WHERE usuario = " + paramNoUsuario);
				sb.append("\n"); sb.append( "AND cve_concepto = " + lngCveConcepto);
				System.out.println("funSQLUpdateRowForConceptoDiario "+sb+"\n"+"\n");
				resp = jdbcTemplate.update(sb.toString());
				
				return resp;
				}

	
	public int funSQLUpdateRowForConceptoAjustado(int paramNoUsuario,int lngCveConcepto, 
			  int paramRow) {

		int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update temp_saldo_cuadrante_nuevo" );
		sb.append("\n"); sb.append( "SET row_concepto = " + paramRow);
		sb.append("\n"); sb.append( "WHERE usuario = " + paramNoUsuario);
		sb.append("\n"); sb.append( "AND cve_concepto = " + lngCveConcepto);
		System.out.println("funSQLUpdateRowForConceptoAjustado "+sb+"\n"+"\n");
		resp = jdbcTemplate.update(sb.toString());
		
		return resp;
		}
	
	/*hector*/
	public int funSQLUpdateRowForConceptoMensual(int paramNoUsuario,
			  int lngCveConcepto, 
			  int paramRow) {
				
				int resp;
				
				StringBuffer sb = new StringBuffer();
				
				sb.append("\n"); sb.append( "Update depura_mensual_real_ajustado" );
				sb.append("\n"); sb.append( "SET row_concepto = " + paramRow);
				sb.append("\n"); sb.append( "WHERE usuario = " + paramNoUsuario);
				sb.append("\n"); sb.append( "AND cve_concepto = " + lngCveConcepto);
				System.out.println("funSQLUpdateRowForConceptoMensual  "+sb+"\n"+"\n");
				resp = jdbcTemplate.update(sb.toString());
				
				return resp;
	}/*Hector*/
	
	public int funSQLUpdateRowForConceptoMensualcomparativorealajustado(int paramNoUsuario,
			  int lngCveConcepto, 
			  int paramRow) {
				
				int resp;
				
				StringBuffer sb = new StringBuffer();
				
				sb.append("\n"); sb.append( "Update depura_mensual_comparativo_realajust" );
				sb.append("\n"); sb.append( "SET row_concepto = " + paramRow);
				sb.append("\n"); sb.append( "WHERE usuario = " + paramNoUsuario);
				sb.append("\n"); sb.append( "AND cve_concepto = " + lngCveConcepto);
				System.out.println("funSQLUpdateRowForConceptoMensualcomparativorealajustado  "+sb+"\n"+"\n");
				resp = jdbcTemplate.update(sb.toString());
				
				return resp;
	}
	public int funSQLUpdateRowSOA(int noUsuario, int ntLastRow) {
		int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update semanal_real_ajustado" );
	    sb.append("\n"); sb.append( "SET row_total_soa = " + ntLastRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + noUsuario);
	    
	    resp = jdbcTemplate.update(sb.toString());
	    System.out.println("funSQLUpdateRowSOA "+sb+"\n"+"\n");
		return resp;

	}
	
	public int funSQLUpdateRowSOAOAR(int noUsuario, int ntLastRow) {
		int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update depura_original_ajustado_real" );
	    sb.append("\n"); sb.append( "SET row_total_soa = " + ntLastRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + noUsuario);
	    
	    resp = jdbcTemplate.update(sb.toString());
	    System.out.println("funSQLUpdateRowSOAOAR "+sb+"\n"+"\n");
		return resp;

	}
	
	public int funSQLUpdateRowSOARealAjustado(int noUsuario, int ntLastRow) {
		int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update depura_mensual_comparativo_realajust" );
	    sb.append("\n"); sb.append( "SET row_total_soa = " + ntLastRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + noUsuario);
	    
	    resp = jdbcTemplate.update(sb.toString());
	    System.out.println("funSQLUpdateRowSOARealAjustado "+sb+"\n"+"\n");
		return resp;

	}
	public int funSQLUpdateRowSOAOriginalAjustado(int noUsuario, int ntLastRow) {
		int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update depura_mensual_comparativo_proyajust" );
	    sb.append("\n"); sb.append( "SET row_total_soa = " + ntLastRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + noUsuario);
	    
	    resp = jdbcTemplate.update(sb.toString());
	    System.out.println("funSQLUpdateRowSOA "+sb+"\n"+"\n");
		return resp;

	}
	public int funSQLUpdateRowSOAOriginal(int noUsuario, int ntLastRow) {
		int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update temp_saldo_cuadrante_diario" );
	    sb.append("\n"); sb.append( "SET row_total_soa = " + ntLastRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + noUsuario);
	    
	    resp = jdbcTemplate.update(sb.toString());
	    System.out.println("funSQLUpdateRowSOA "+sb+"\n"+"\n");
		return resp;

	}
	
	public int funSQLUpdateRowSOAAjustado(int noUsuario, int ntLastRow) {
		int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update temp_saldo_cuadrante_nuevo" );
	    sb.append("\n"); sb.append( "SET row_total_soa = " + ntLastRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + noUsuario);
	    
	    resp = jdbcTemplate.update(sb.toString());
	    System.out.println("funSQLUpdateRowSOAAjustado "+sb+"\n"+"\n");
		return resp;

	}
	/*HECTOR*/
	public int funSQLUpdateRowSOAMENSUAL(int noUsuario, int ntLastRow) {
		int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update depura_mensual_real_ajustado" );
	    sb.append("\n"); sb.append( "SET row_total_soa = " + ntLastRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + noUsuario);
	    
	    resp = jdbcTemplate.update(sb.toString());
	    System.out.println("funSQLUpdateRowSOAMENSUAL "+sb+"\n"+"\n");
		return resp;

	}
	/*HECTOR*/
	public int funSQLUpdateRowSDMENSUAL(int noUsuario, int ntLastRow) {

		int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update depura_mensual_real_ajustado" );
	    sb.append("\n"); sb.append( "SET row_total_sd = " + ntLastRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + noUsuario);
		System.out.println("funSQLUpdateRowSDMENSUAL " +sb+"\n"+"\n");
	    resp = jdbcTemplate.update(sb.toString());
		
		return resp;

	}
	
	public int funSQLUpdateRowSD(int noUsuario, int ntLastRow) {

		int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update semanal_real_ajustado" );
	    sb.append("\n"); sb.append( "SET row_total_sd = " + ntLastRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + noUsuario);
		System.out.println("funSQLUpdateRowSD " +sb+"\n"+"\n");
	    resp = jdbcTemplate.update(sb.toString());
		
		return resp;

	}
	
	public int funSQLUpdateRowSDOAR(int noUsuario, int ntLastRow) {

		int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update depura_original_ajustado_real" );
	    sb.append("\n"); sb.append( "SET row_total_sd = " + ntLastRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + noUsuario);
		System.out.println("funSQLUpdateRowSDOAR " +sb+"\n"+"\n");
	    resp = jdbcTemplate.update(sb.toString());
		
		return resp;

	}
	
	public int funSQLUpdateRowSDRealAjustado(int noUsuario, int ntLastRow) {

		int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update depura_mensual_comparativo_realajust" );
	    sb.append("\n"); sb.append( "SET row_total_sd = " + ntLastRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + noUsuario);
		System.out.println("funSQLUpdateRowSDRealAjustado " +sb+"\n"+"\n");
	    resp = jdbcTemplate.update(sb.toString());
		
		return resp;

	}
	public int funSQLUpdateRowSDOriginalAjustado(int noUsuario, int ntLastRow) {

		int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update depura_mensual_comparativo_proyajust" );
	    sb.append("\n"); sb.append( "SET row_total_sd = " + ntLastRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + noUsuario);
		System.out.println("funSQLUpdateRowSDOriginalAjustado " +sb+"\n"+"\n");
	    resp = jdbcTemplate.update(sb.toString());
		
		return resp;

	}
	public int funSQLUpdateRowSDOriginal(int noUsuario, int ntLastRow) {

		int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update temp_saldo_cuadrante_diario" );
	    sb.append("\n"); sb.append( "SET row_total_sd = " + ntLastRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + noUsuario);
		System.out.println("funSQLUpdateRowSDDiario " +sb+"\n"+"\n");
	    resp = jdbcTemplate.update(sb.toString());
		
		return resp;

	}
	
	public int funSQLUpdateRowSDAjustado(int noUsuario, int ntLastRow) {

		int resp;
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n"); sb.append( "Update temp_saldo_cuadrante_nuevo" );
	    sb.append("\n"); sb.append( "SET row_total_sd = " + ntLastRow);
	    sb.append("\n"); sb.append( "WHERE usuario = " + noUsuario);
		System.out.println("funSQLUpdateRowSDAjustado " +sb+"\n"+"\n");
	    resp = jdbcTemplate.update(sb.toString());
		
		return resp;

	}

	public List<Saldo> funSqlGetSaldosForFlujo(int paramUsuario,
											   String paramFecha,											  
											   String paramTabla, 
											   String paramDivisa,
											   int paramIdGrupo,
											   int paramNoEmpresa) {
		System.out.println("tipo"+ paramFecha+"\n"+"\n");
		
		String sql = "";
		StringBuffer sb = new StringBuffer();
		
		if( paramTabla.equals( "I-C" ) )
		{			
			//sb.append("\n"); sb.append(" select dbo.func_CALCULA_SALDO('saldo_inicial', " +paramUsuario+",'" + paramDivisa + "') as Saldo" );
			sb.append("\n");sb.append("select coalesce(sum(saldo_inicial),0)as Saldo from hist_cat_cta_banco where fec_valor = '"+paramFecha+"' and id_divisa = '"+paramDivisa+"'");
			 
			if( paramNoEmpresa == 0){
				sb.append("\n");sb.append("And no_empresa in (select id_grupo_flujo from cat_super_grupo_flujo where id_grupo_flujo");
				sb.append("\n");sb.append("in(select no_empresa from usuario_empresa where no_usuario="+paramUsuario+"");
				sb.append("\n");sb.append("and id_super_grupo in(select id_super_grupo from cat_super_grupo where id_super_grupo in("+paramIdGrupo+"))))");
			}else{
				sb.append("\n");sb.append("And no_empresa =(select id_grupo_flujo from cat_super_grupo_flujo where id_grupo_flujo="+paramNoEmpresa+"");
				sb.append("\n");sb.append("and id_super_grupo ="+paramIdGrupo+")"); 
			}
			
		}
		
		if( paramTabla.equals( "F-C" ) )
		{
			//sb.append("\n"); sb.append(" select dbo.func_CALCULA_SALDO('saldo_final', " +paramUsuario+",'" + paramDivisa + "') as Saldo" );
			sb.append("\n");sb.append("select coalesce(sum(saldo_inicial),0)as Saldo from hist_cat_cta_banco where fec_valor = '"+paramFecha+"' and id_divisa = '"+paramDivisa+"'");
			 
			if( paramNoEmpresa == 0){
				sb.append("\n");sb.append("And no_empresa in (select id_grupo_flujo from cat_super_grupo_flujo where id_grupo_flujo");
				sb.append("\n");sb.append("in(select no_empresa from usuario_empresa where no_usuario="+paramUsuario+"");
				sb.append("\n");sb.append("and id_super_grupo in(select id_super_grupo from cat_super_grupo where id_super_grupo in("+paramIdGrupo+"))))");
			}else{
				sb.append("\n");sb.append("And no_empresa =(select id_grupo_flujo from cat_super_grupo_flujo where id_grupo_flujo="+paramNoEmpresa+"");
				sb.append("\n");sb.append("and id_super_grupo ="+paramIdGrupo+")"); 
			}
		}
			
		if( paramTabla.equals( "I-H" ) )
		{
			//sb.append("\n"); sb.append(" select dbo.func_CALCULA_SALDO('saldo_inicial', " +paramUsuario+",'" + paramDivisa + "') as Saldo" );
			sb.append("\n");sb.append("select coalesce(sum(saldo_inicial),0)as Saldo from hist_cat_cta_banco where fec_valor = '"+paramFecha+"' and id_divisa = '"+paramDivisa+"'");
		 
			if( paramNoEmpresa == 0){
				sb.append("\n");sb.append("And no_empresa in (select id_grupo_flujo from cat_super_grupo_flujo where id_grupo_flujo");
				sb.append("\n");sb.append("in(select no_empresa from usuario_empresa where no_usuario="+paramUsuario+"");
				sb.append("\n");sb.append("and id_super_grupo in(select id_super_grupo from cat_super_grupo where id_super_grupo in("+paramIdGrupo+"))))");
			}else{
				sb.append("\n");sb.append("And no_empresa =(select id_grupo_flujo from cat_super_grupo_flujo where id_grupo_flujo="+paramNoEmpresa+"");
				sb.append("\n");sb.append("and id_super_grupo ="+paramIdGrupo+")"); 
			}
		}
		if( paramTabla.equals( "F-H" ) )
		{
			 sb.append("\n"); sb.append(" select dbo.func_CALCULA_SALDO('saldo_inicial', " +paramUsuario+",'" + paramDivisa + "') as Saldo" );
		 
		}  
		System.out.println("grupo "+paramIdGrupo+"\n"+"\n");
		System.out.println("Empresa "+paramNoEmpresa+"\n"+"\n");
		sql=sb.toString();	
		System.out.println("paramTabla sql"+paramTabla +sql+"\n"+"\n");
		List <Saldo> saldos = null;
		
		try{
			
			saldos = jdbcTemplate.query(sql, new RowMapper(){
				
			public Saldo mapRow(ResultSet rs, int idx) throws SQLException 
			{				
				Saldo saldo = new Saldo();				
				saldo.setSaldo( rs.getDouble( "Saldo" ) );								
				return saldo;				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return saldos;		

	}
	
	public List<Saldo> FunSQLSaldo_Inversion(int paramUsuario,
			   String paramFecha,											  
			   String paramTabla, 
			   String paramDivisa,
			   int paramIdGrupo,
			   int paramNoEmpresa){
		
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append("\n"); sb.append(" SELECT  coalesce(sum(importe), 0) as Saldo");
		sb.append("\n"); sb.append(" From orden_inversion");
		sb.append("\n"); sb.append(" where fec_alta <'"+paramFecha+"'");
		sb.append("\n"); sb.append(" AND fec_venc >='"+paramFecha+"'");
		sb.append("\n"); sb.append(" and id_estatus_ord <> 'X'");
		sb.append("\n"); sb.append(" and tipo_inversion <>'P'");
		if(paramNoEmpresa==0){
			sb.append("\n"); sb.append(" And no_empresa in (select id_grupo_flujo from cat_super_grupo_flujo");
			sb.append("\n"); sb.append(" where id_grupo_flujo in(select no_empresa from usuario_empresa");
			sb.append("\n"); sb.append(" where no_usuario= "+paramUsuario+" )");
			sb.append("\n"); sb.append(" and id_super_grupo in(select id_super_grupo from cat_super_grupo");
			sb.append("\n"); sb.append(" where id_super_grupo in( "+paramIdGrupo+" )))");
		}else{
			sb.append("\n"); sb.append("And no_empresa =(select id_grupo_flujo from cat_super_grupo_flujo");
			sb.append("\n"); sb.append("where id_grupo_flujo="+paramNoEmpresa+"");
			sb.append("\n"); sb.append(" and id_super_grupo =  "+paramIdGrupo+"  )"); 
		}
		
		
		sb.append("\n"); sb.append(" UNION ALL ");
		sb.append("\n"); sb.append(" SELECT  coalesce(sum(importe), 0) as Saldo");
		sb.append("\n"); sb.append(" From ESTADO_CTA_INV");
		sb.append("\n"); sb.append(" where fec_alta <'"+paramFecha+"'");
		sb.append("\n"); sb.append(" AND fec_venc >='"+paramFecha+"'");
		
		if(paramNoEmpresa == 0){
			sb.append("\n"); sb.append("And no_empresa in (select distinct no_empresa  from orden_inversion");
			sb.append("\n"); sb.append(" where tipo_inversion = 'P'");
			sb.append("\n"); sb.append(" and id_estatus_ord <> 'X'");
			sb.append("\n"); sb.append(" And no_empresa in (select id_grupo_flujo from cat_super_grupo_flujo");
			sb.append("\n"); sb.append(" where id_grupo_flujo in(select no_empresa from usuario_empresa");
			sb.append("\n"); sb.append("where no_usuario= "+paramUsuario+")");
			sb.append("\n"); sb.append(" and id_super_grupo in(select id_super_grupo from cat_super_grupo");
			sb.append("\n"); sb.append(" where id_super_grupo in("+paramIdGrupo+"))))");
		}else{
			sb.append("\n"); sb.append(" And no_empresa in (select distinct no_empresa  from orden_inversion");
			sb.append("\n"); sb.append(" where tipo_inversion = 'P'");
			sb.append("\n"); sb.append(" and id_estatus_ord <> 'X'");
			sb.append("\n"); sb.append(" And no_empresa =(select id_grupo_flujo from cat_super_grupo_flujo");
			sb.append("\n"); sb.append(" where id_grupo_flujo= "+paramNoEmpresa+"");
			sb.append("\n"); sb.append(" and id_super_grupo =  "+paramIdGrupo+" ))");
			sb.append("\n"); sb.append("");
			sb.append("\n"); sb.append("");
		}
		
	
		sql=sb.toString();	
		System.out.println("FunSQLSaldo_Inversion" +sql+"\n"+"\n");
		List <Saldo> saldos = null;
		
		try{
			
			saldos = jdbcTemplate.query(sql, new RowMapper(){
				
			public Saldo mapRow(ResultSet rs, int idx) throws SQLException 
			{				
				Saldo saldo = new Saldo();				
				saldo.setSaldo( rs.getDouble( "saldo" ) );								
				return saldo;				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return saldos;	
	}

	public List<TotalDiario> funSqlGetTotalesDiario(String paramIdDivisa,
			int paramUsuario) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();
					               		
	    sb.append("\n"); sb.append("SELECT CONVERT(VARCHAR(11), FEC_VALOR, 103)  AS FEC_VALOR,");
	    sb.append("\n"); sb.append("ID_ORIGEN , ");
	    sb.append("\n"); sb.append("dbo.func_TOTAL ('INGRESOEGRESO','NATURALEZA') as TOTAL");
	    sb.append("\n"); sb.append(",row_total_soa");
	    sb.append("\n"); sb.append(",row_total_sd");
	    sb.append("\n"); sb.append("From DIARIO_REAL_AJUSTADO");
	    sb.append("\n"); sb.append("Where no_usuario = " + paramUsuario);
	    sb.append("\n"); sb.append("AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append("GROUP BY FEC_VALOR,ID_ORIGEN,row_total_soa, row_total_sd,INGRESOEGRESO,NATURALEZA");
	    sb.append("\n"); sb.append("ORDER BY 1");

	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesDiario "+sql+"\n"+"\n");
		logger.info("SQL : " + sql);
		List<TotalDiario> totalesDiario = null;
		
		try{
			
			totalesDiario = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalDiario mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalDiario totalDiario = new TotalDiario();
								
				totalDiario.setFec_valor( rs.getString( "fec_valor" ) );
				totalDiario.setOrigen( rs.getString("id_origen") );
				totalDiario.setTotal( rs.getDouble( "total" ) );
				totalDiario.setRow_total_sd( rs.getInt("row_total_sd"));
				totalDiario.setRow_total_soa(rs.getInt("row_total_soa"));
				
				return totalDiario;
				
			}});
		
			}catch(Exception e){
				e.printStackTrace();
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesDiario;
	}

	public List<TotalIngresoEgreso> funSqlGetTotalesIngresoEgreso(
			String paramIdDivisa, int paramUsuario) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
	    sb.append("\n"); sb.append( "SELECT CONVERT(VARCHAR(11), FEC_VALOR, 103)  AS FEC_VALOR,");
	    sb.append("\n"); sb.append( "    ID_ORIGEN , ");
	    sb.append("\n"); sb.append( "INGRESOEGRESO,");
	    sb.append("\n"); sb.append("dbo.func_TOTAL ('INGRESOEGRESO','NATURALEZA') as TOTAL"); //Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ",ROW_INGRESOEGRESO");
	    sb.append("\n"); sb.append( "From DIARIO_REAL_AJUSTADO");
	    sb.append("\n"); sb.append( "Where no_usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "GROUP BY FEC_VALOR,ID_ORIGEN,INGRESOEGRESO, ROW_INGRESOEGRESO,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 1, 3 DESC");
	    
	    sql=sb.toString();
		System.out.println("funSqlGetTotalesIngresoEgreso "+sql+"\n"+"\n");
		List<TotalIngresoEgreso> totalesIngresoEgreso = null;
		
		try{
			
			totalesIngresoEgreso = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalIngresoEgreso mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalIngresoEgreso totalIngresoEgreso = new TotalIngresoEgreso();
								
				totalIngresoEgreso.setFec_valor( rs.getString( "fec_valor" ) );
				totalIngresoEgreso.setOrigen( rs.getString("id_origen") );
				totalIngresoEgreso.setIngresoegreso(rs.getString("ingresoegreso"));
				totalIngresoEgreso.setTotal( rs.getDouble( "total" ) );
				totalIngresoEgreso.setRow_ingresoegreso(rs.getInt("row_ingresoegreso"));
								
				return totalIngresoEgreso;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesIngresoEgreso;	    
	    
	}

	public List<TotalTipoConcepto> funSqlGetTotalesForTypeConcepto(
			String paramIdDivisa, int paramUsuario) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
		sb.append("\n"); sb.append("SELECT CONVERT(VARCHAR(11), FEC_VALOR, 103)  AS FEC_VALOR,");
	    sb.append("\n"); sb.append("ID_ORIGEN,");
	    sb.append("\n"); sb.append("INGRESOEGRESO,");
	    sb.append("\n"); sb.append("id_tipo_concepto,");
	    sb.append("\n"); sb.append("descripcion,");
	    sb.append("\n"); sb.append("dbo.func_TOTAL ('INGRESOEGRESO','NATURALEZA') as TOTAL,"); //Funcion SQL TOTAL
	    sb.append("\n"); sb.append(" row_tipo_concepto");
	    sb.append("\n"); sb.append("From DIARIO_REAL_AJUSTADO");
	    sb.append("\n"); sb.append("Where no_usuario = " + paramUsuario);
	    sb.append("\n"); sb.append("AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append("GROUP BY FEC_VALOR,ID_ORIGEN,INGRESOEGRESO, id_tipo_concepto, descripcion,row_tipo_concepto,NATURALEZA");
	    sb.append("\n"); sb.append("ORDER BY 1, 5");
	    
	    sql=sb.toString();
		System.out.println("funSqlGetTotalesForTypeConcepto "+sql+"\n"+"\n");
	    List<TotalTipoConcepto> totalesTipoConcepto = null;
		
		try{
			
			totalesTipoConcepto = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalTipoConcepto mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalTipoConcepto totalTipoConcepto = new TotalTipoConcepto();
								
				totalTipoConcepto.setFec_valor( rs.getString( "fec_valor" ) );
				totalTipoConcepto.setOrigen( rs.getString("id_origen") );
				totalTipoConcepto.setIngresoegreso(rs.getString("ingresoegreso"));
				totalTipoConcepto.setTotal( rs.getDouble( "total" ) );
				totalTipoConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
				totalTipoConcepto.setDescripcion(rs.getString("descripcion"));
				totalTipoConcepto.setRow_tipo_concepto(rs.getInt("row_tipo_concepto"));				
								
				return totalTipoConcepto;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForTypeConcepto" + ":," + sql + ":, M:");
			}		
		
		return totalesTipoConcepto;
	    	    
	}

	public List<TotalConcepto> funSqlGetTotalesForConcepto(
			String paramIdDivisa, int paramUsuario) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();
		
			    sb.append("\n"); sb.append("SELECT CONVERT(VARCHAR(11), FEC_VALOR, 103)  AS FEC_VALOR,");
			    
			    sb.append("\n"); sb.append("INGRESO_EGRESO,");
			    sb.append("\n"); sb.append("id_tipo_concepto,");
			    sb.append("\n"); sb.append("descripcion,");
			    sb.append("\n"); sb.append("cve_concepto,");
			    sb.append("\n"); sb.append("desc_larga,");
			    sb.append("\n"); sb.append("IMPORTE as TOTAL,"); //Funcion SQL TOTAL
			    sb.append("\n"); sb.append("row_concepto");
			    sb.append("\n"); sb.append("From semanal_real_ajustado");
			    sb.append("\n"); sb.append("Where usuario = " + paramUsuario);
			    sb.append("\n"); sb.append("AND ID_DIVISA = '" + paramIdDivisa + "'");
			    sb.append("\n"); sb.append("GROUP BY FEC_VALOR,INGRESO_EGRESO, id_tipo_concepto, descripcion,cve_concepto, desc_larga, row_concepto,NATURALEZA,IMPORTE");
			    sb.append("\n"); sb.append("ORDER BY 1, 7");
			    
			    sql=sb.toString();
			    System.out.println("funSqlGetTotalesForConcepto "+ sql+"\n"+"\n");
			    List<TotalConcepto> totalesConcepto = null;
				
				try{
					
					totalesConcepto = jdbcTemplate.query(sql, new RowMapper(){
						
					public TotalConcepto mapRow(ResultSet rs, int idx) throws SQLException {
						
						TotalConcepto totalConcepto = new TotalConcepto();
										
						totalConcepto.setFec_valor( rs.getString( "fec_valor" ) );
						//totalConcepto.setOrigen( rs.getString("id_origen") );
						totalConcepto.setIngresoegreso(rs.getString("ingreso_egreso"));
						totalConcepto.setTotal( rs.getDouble( "total" ) );
						totalConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
						totalConcepto.setDescripcion(rs.getString("descripcion"));
						totalConcepto.setCve_concepto(rs.getInt("cve_concepto"));
						totalConcepto.setDesc_larga(rs.getString("desc_larga"));
						totalConcepto.setRow_concepto(rs.getInt("row_concepto"));
						
						return totalConcepto;
						
					}});
				
					}catch(Exception e){
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForConcepto" + ":," + sql + ":, M:");
					}		
				
				return totalesConcepto;
				
	}

	public List<TotalDiario> funSqlGetTotalesXWeek(String paramIdDivisa,
			int paramUsuario, String paramFecIni, String paramFecFin) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();
					               		
	    
		sb.append("\n"); sb.append("SELECT NO_USUARIO,");
	    sb.append("\n"); sb.append("dbo.func_TOTAL ('INGRESOEGRESO','NATURALEZA') as TOTAL"); //Funcion SQL TOTAL
	    sb.append("\n"); sb.append(",row_total_soa");
	    sb.append("\n"); sb.append(",row_total_sd");
	    sb.append("\n"); sb.append("From DIARIO_REAL_AJUSTADO");
	    sb.append("\n"); sb.append("Where no_usuario = " + paramUsuario);
	    sb.append("\n"); sb.append("AND FEC_VALOR BETWEEN convert(date, '" + paramFecIni + "', 103) AND convert(date, '" + paramFecFin + "', 103) ");
	    sb.append("\n"); sb.append("AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append("GROUP BY NO_USUARIO,row_total_soa, row_total_sd,INGRESOEGRESO,NATURALEZA");
	    sb.append("\n"); sb.append("ORDER BY 1");

	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXWeek "+sql+"\n"+"\n");
		List<TotalDiario> totalesDiario = null;
		
		try{
			
			totalesDiario = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalDiario mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalDiario totalDiario = new TotalDiario();

				totalDiario.setTotal( rs.getDouble( "total" ) );
				totalDiario.setRow_total_sd( rs.getInt("row_total_sd"));
				totalDiario.setRow_total_soa(rs.getInt("row_total_soa"));
				
				return totalDiario;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesDiario;

	}

	public List<TotalIngresoEgreso> funSqlGetTotalesXSemanaIngresoEgreso(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
		sb.append("\n"); sb.append( "SELECT USUARIO,INGRESO_EGRESO,");
	    sb.append("\n"); sb.append("SUM(IMPORTE) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ",row_ingresoegreso");
	    sb.append("\n"); sb.append( "From SEMANAL_REAL_AJUSTADO");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "and fec_valor between convert(date, '" + paramFecIni + "', 103) and convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,INGRESO_EGRESO,row_ingresoegreso,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 2 desc");
	    
	    sql=sb.toString();
		System.out.println("funSqlGetTotalesXSemanaIngresoEgreso "+sql+"\n"+"\n");
		List<TotalIngresoEgreso> totalesIngresoEgreso = null;
		
		try{
			
			totalesIngresoEgreso = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalIngresoEgreso mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalIngresoEgreso totalIngresoEgreso = new TotalIngresoEgreso();								
				totalIngresoEgreso.setIngresoegreso(rs.getString("ingreso_egreso"));
				totalIngresoEgreso.setTotal( rs.getDouble( "total" ) );
				totalIngresoEgreso.setRow_ingresoegreso(rs.getInt("row_ingresoegreso"));
								
				return totalIngresoEgreso;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesIngresoEgreso;

	}

	public List<TotalTipoConcepto> funSqlGetTotalesXSemanaForTypeConcepto(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin,int numero) {

		String sql = "";
		StringBuffer sb = new StringBuffer();	
		StringBuffer sb1 = new StringBuffer();	
	    
		sb.append("\n"); sb.append("SELECT USUARIO, INGRESO_EGRESO, id_tipo_concepto, descripcion,");
	    sb.append("\n"); sb.append("SUM(IMPORTE) as TOTAL , ROW_TIPO_CONCEPTO From semanal_real_ajustado");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "AND FEC_VALOR BETWEEN convert(date, '" + paramFecIni + "', 103) and convert(date, '" + paramFecFin + "', 103) ");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,INGRESO_EGRESO, id_tipo_concepto, descripcion, ROW_TIPO_CONCEPTO,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 1, 5");
	    
	    sb1.append("\n"); sb1.append("insert into temp_excel_semanal ");
	    sb1.append("\n"); sb1.append("SELECT USUARIO, INGRESO_EGRESO, id_tipo_concepto, descripcion,");
	    sb1.append("\n"); sb1.append("SUM(IMPORTE) as TOTAL ,ROW_CONCEPTO,identificador,desc_larga,mes,"+numero+",id_divisa From semanal_real_ajustado");
	    sb1.append("\n"); sb1.append( "Where usuario = " + paramUsuario);
	    sb1.append("\n"); sb1.append( "AND FEC_VALOR BETWEEN convert(date, '" + paramFecIni + "', 103) and convert(date, '" + paramFecFin + "', 103) ");
	    sb1.append("\n"); sb1.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb1.append("\n"); sb1.append( "GROUP BY USUARIO,INGRESO_EGRESO, id_tipo_concepto, descripcion, ROW_CONCEPTO,NATURALEZA,identificador,desc_larga,mes,id_divisa");
	    sb1.append("\n"); sb1.append( "ORDER BY 1, 5");
	    
	    jdbcTemplate.execute(sb1.toString());
	    
	    sql=sb.toString();
		System.out.println("funSqlGetTotalesXSemanaForTypeConcepto "+sql+"\n"+"\n");
	    List<TotalTipoConcepto> totalesTipoConcepto = null;
		
		try{
			
			totalesTipoConcepto = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalTipoConcepto mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalTipoConcepto totalTipoConcepto = new TotalTipoConcepto();
								

				totalTipoConcepto.setIngresoegreso(rs.getString("ingreso_egreso"));
				totalTipoConcepto.setTotal( rs.getDouble( "total" ) );
				totalTipoConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
				totalTipoConcepto.setDescripcion(rs.getString("descripcion"));
				totalTipoConcepto.setRow_tipo_concepto(rs.getInt("row_tipo_concepto"));				
								
				return totalTipoConcepto;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForTypeConcepto" + ":," + sql + ":, M:");
			}		
		
		return totalesTipoConcepto;

	}

	public List<TotalConcepto> funSqlGetTotalesXWeekForConcepto(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
		
		sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append( "ingreso_egreso,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append( "cve_concepto,");
	    sb.append("\n"); sb.append( "desc_larga,");
	    sb.append("\n"); sb.append("sum(importe) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ", row_concepto");
	    sb.append("\n"); sb.append( "From SEMANAL_REAL_AJUSTADO");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "and fec_valor between convert(date, '" + paramFecIni + "', 103) and convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO, ingreso_egreso,id_tipo_concepto, descripcion,cve_concepto, desc_larga,row_concepto,INGRESO_EGRESO,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 2");
			    
			    sql=sb.toString();
			    System.out.println("funSqlGetTotalesXWeekForConcepto "+sql+"\n"+"\n");	    
			    List<TotalConcepto> totalesConcepto = null;
				
				try{
					
					totalesConcepto = jdbcTemplate.query(sql, new RowMapper(){
						
					public TotalConcepto mapRow(ResultSet rs, int idx) throws SQLException {
						
						TotalConcepto totalConcepto = new TotalConcepto();										
				
						totalConcepto.setIngresoegreso(rs.getString("ingreso_egreso"));
						totalConcepto.setTotal( rs.getDouble( "total" ) );
						totalConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
						totalConcepto.setDescripcion(rs.getString("descripcion"));
						totalConcepto.setCve_concepto(rs.getInt("cve_concepto"));
						totalConcepto.setDesc_larga(rs.getString("desc_larga"));
						totalConcepto.setRow_concepto(rs.getInt("row_concepto"));
						
						return totalConcepto;
						
					}});
				
					}catch(Exception e){
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForConcepto" + ":," + sql + ":, M:");
					}		
				
				return totalesConcepto;
		
	}

	public List<TotalDiario> funSqlGetTotalesXMes(String paramIdDivisa,
			int paramUsuario, String paramFecIni, String paramFecFin) {

		String sql = "";
		StringBuffer sb = new StringBuffer();
					               			    
		sb.append("\n"); sb.append("SELECT  USUARIO,");
	    sb.append("\n"); sb.append("SUM(importe)  as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append(",row_total_soa");
	    sb.append("\n"); sb.append(",row_total_sd");
	    sb.append("\n"); sb.append("From semanal_real_ajustado");
	    sb.append("\n"); sb.append("Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append("AND FEC_VALOR BETWEEN convert(date, '" + paramFecIni + "', 103) AND convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append("AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append("GROUP BY USUARIO,row_total_soa, row_total_sd,ingreso_egreso,NATURALEZA");
	    sb.append("\n"); sb.append("ORDER BY 1");

	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXMes "+sql+"\n"+"\n");
		List<TotalDiario> totalesDiario = null;
		
		try{
			
			totalesDiario = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalDiario mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalDiario totalDiario = new TotalDiario();

				totalDiario.setTotal( rs.getDouble( "total" ) );
				totalDiario.setRow_total_sd( rs.getInt("row_total_sd"));
				totalDiario.setRow_total_soa(rs.getInt("row_total_soa"));
				
				return totalDiario;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesDiario;

	}
	public List<TotalDiario> funSqlGetTotalesXMesAjustado(String paramIdDivisa,
			int paramUsuario, String paramFecIni, String paramFecFin) {

		String sql = "";
		StringBuffer sb = new StringBuffer();
					               			    
		sb.append("\n"); sb.append("SELECT  USUARIO,");
	    sb.append("\n"); sb.append("SUM(importe)  as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append(",row_total_soa");
	    sb.append("\n"); sb.append(",row_total_sd");
	    sb.append("\n"); sb.append("From temp_saldo_cuadrante_nuevo");
	    sb.append("\n"); sb.append("Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append("AND FEC_VALOR BETWEEN convert(date, '" + paramFecIni + "', 103) AND convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append("AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append("GROUP BY USUARIO,row_total_soa, row_total_sd,ingreso_egreso,NATURALEZA");
	    sb.append("\n"); sb.append("ORDER BY 1");

	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXMesAjustado "+sql+"\n"+"\n");
		List<TotalDiario> totalesDiario = null;
		
		try{
			
			totalesDiario = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalDiario mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalDiario totalDiario = new TotalDiario();

				totalDiario.setTotal( rs.getDouble( "total" ) );
				totalDiario.setRow_total_sd( rs.getInt("row_total_sd"));
				totalDiario.setRow_total_soa(rs.getInt("row_total_soa"));
				
				return totalDiario;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesDiario;

	}
	
	public List<TotalDiario> funSqlGetTotalesXMesOriginal(String paramIdDivisa,
			int paramUsuario, String paramFecIni, String paramFecFin) {

		String sql = "";
		StringBuffer sb = new StringBuffer();
					               			    
		sb.append("\n"); sb.append("SELECT  USUARIO,");
	    sb.append("\n"); sb.append("SUM(importe)  as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append(",row_total_soa");
	    sb.append("\n"); sb.append(",row_total_sd");
	    sb.append("\n"); sb.append("From temp_saldo_cuadrante_diario");
	    sb.append("\n"); sb.append("Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append("AND FEC_VALOR BETWEEN convert(date, '" + paramFecIni + "', 103) AND convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append("AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append("GROUP BY USUARIO,row_total_soa, row_total_sd,ingreso_egreso,NATURALEZA");
	    sb.append("\n"); sb.append("ORDER BY 1");

	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXMesOriginal "+sql+"\n"+"\n");
		List<TotalDiario> totalesDiario = null;
		
		try{
			
			totalesDiario = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalDiario mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalDiario totalDiario = new TotalDiario();

				totalDiario.setTotal( rs.getDouble( "total" ) );
				totalDiario.setRow_total_sd( rs.getInt("row_total_sd"));
				totalDiario.setRow_total_soa(rs.getInt("row_total_soa"));
				
				return totalDiario;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesDiario;

	}

	public List<TotalDiario> funSqlGetTotalesXMesMensual(String paramIdDivisa,
			int paramUsuario, String paramFecIni, String paramFecFin) {

		String sql = "";
		StringBuffer sb = new StringBuffer();
					               			    
		sb.append("\n"); sb.append("SELECT  USUARIO,");
	    sb.append("\n"); sb.append("SUM(importe)  as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append(",row_total_soa");
	    sb.append("\n"); sb.append(",row_total_sd");
	    sb.append("\n"); sb.append("From depura_mensual_real_ajustado");
	    sb.append("\n"); sb.append("Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append("AND FEC_VALOR BETWEEN convert(date, '" + paramFecIni + "', 103) AND convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append("AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append("GROUP BY mes,USUARIO,row_total_soa, row_total_sd,ingreso_egreso,NATURALEZA");
	    sb.append("\n"); sb.append("ORDER BY 1");

	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXMesMensual "+sql+"\n"+"\n");
		List<TotalDiario> totalesDiario = null;
		
		try{
			
			totalesDiario = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalDiario mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalDiario totalDiario = new TotalDiario();

				totalDiario.setTotal( rs.getDouble( "total" ) );
				totalDiario.setRow_total_sd( rs.getInt("row_total_sd"));
				totalDiario.setRow_total_soa(rs.getInt("row_total_soa"));
				
				return totalDiario;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesDiario;

	}
	public List<TotalDiario> funSqlGetTotalesXMes(String paramIdDivisa,
			int paramUsuario, String paramFecIni, String paramFecFin,String paramOrigen) {

		String sql = "";
		StringBuffer sb = new StringBuffer();		
		
		sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append("SUM(IMPORTE) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append(",row_total_soa");
	    sb.append("\n"); sb.append(",row_total_sd");
	    sb.append("\n"); sb.append("From depura_original_ajustado_real");
	    sb.append("\n"); sb.append("Where usuario = " + paramUsuario);
	   // sb.append("\n"); sb.append("AND FEC_VALOR BETWEEN convert(date, '" + paramFecIni + "', 103) AND convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "and mes between DATENAME(month, '" + paramFecIni + "') and DATENAME(month, '" + paramFecFin + "')");
	    sb.append("\n"); sb.append("AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "AND IDENTIFICADOR = '" + paramOrigen + "'");
	    sb.append("\n"); sb.append("GROUP BY USUARIO,row_total_soa, row_total_sd,ingeg,NATURALEZA");
	    sb.append("\n"); sb.append("ORDER BY 1");
	    
	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXMes "+sql+"\n"+"\n");
		List<TotalDiario> totalesDiario = null;
		
		try{
			
			totalesDiario = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalDiario mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalDiario totalDiario = new TotalDiario();

				totalDiario.setTotal( rs.getDouble( "total" ) );
				totalDiario.setRow_total_sd( rs.getInt("row_total_sd"));
				totalDiario.setRow_total_soa(rs.getInt("row_total_soa"));
				
				return totalDiario;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesDiario;

	}
	
	public List<TotalDiario> funSqlGetTotalesXMesOriginalAjustado2(String paramIdDivisa,
			int paramUsuario, String paramFecIni, String paramFecFin,String paramOrigen) {

		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
		sb.append("\n"); sb.append("SELECT  USUARIO,");
	    sb.append("\n"); sb.append("SUM(importe)  as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append(",row_total_soa");
	    sb.append("\n"); sb.append(",row_total_sd");
	    sb.append("\n"); sb.append("From depura_mensual_comparativo_proyajust");
	    sb.append("\n"); sb.append("Where usuario = " + paramUsuario);
	    //sb.append("\n"); sb.append("AND FEC_VALOR BETWEEN convert(date, '" + paramFecIni + "', 103) AND convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "and mes between DATENAME(month, '" + paramFecIni + "') and DATENAME(month, '" + paramFecFin + "')");
	    sb.append("\n"); sb.append("AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append("AND IDENTIFICADOR = '" + paramOrigen + "'");
	    sb.append("\n"); sb.append("GROUP BY mes,USUARIO,row_total_soa, row_total_sd,ingeg,NATURALEZA");
	    sb.append("\n"); sb.append("ORDER BY 1");

	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXMesOriginalAjustado2 "+sql+"\n"+"\n");
		List<TotalDiario> totalesDiario = null;
		
		try{
			
			totalesDiario = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalDiario mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalDiario totalDiario = new TotalDiario();

				totalDiario.setTotal( rs.getDouble( "total" ) );
				totalDiario.setRow_total_sd( rs.getInt("row_total_sd"));
				totalDiario.setRow_total_soa(rs.getInt("row_total_soa"));
				
				return totalDiario;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesDiario;

	}
	
	public List<TotalDiario> funSqlGetTotalesXMesRealAjustado2(String paramIdDivisa,
			int paramUsuario, String paramFecIni, String paramFecFin,String paramOrigen) {

		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
		sb.append("\n"); sb.append("SELECT  USUARIO,");
	    sb.append("\n"); sb.append("SUM(importe)  as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append(",row_total_soa");
	    sb.append("\n"); sb.append(",row_total_sd");
	    sb.append("\n"); sb.append("From depura_mensual_comparativo_realajust");
	    sb.append("\n"); sb.append("Where usuario = " + paramUsuario);
	    //sb.append("\n"); sb.append("AND FEC_VALOR BETWEEN convert(date, '" + paramFecIni + "', 103) AND convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "and mes between DATENAME(month, '" + paramFecIni + "') and DATENAME(month, '" + paramFecFin + "')");
	    sb.append("\n"); sb.append("AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append("AND IDENTIFICADOR = '" + paramOrigen + "'");
	    sb.append("\n"); sb.append("GROUP BY mes,USUARIO,row_total_soa, row_total_sd,ingeg,NATURALEZA");
	    sb.append("\n"); sb.append("ORDER BY 1");

	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXMesRealAjustado2 "+sql+"\n"+"\n");
		List<TotalDiario> totalesDiario = null;
		
		try{
			
			totalesDiario = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalDiario mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalDiario totalDiario = new TotalDiario();

				totalDiario.setTotal( rs.getDouble( "total" ) );
				totalDiario.setRow_total_sd( rs.getInt("row_total_sd"));
				totalDiario.setRow_total_soa(rs.getInt("row_total_soa"));
				
				return totalDiario;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesDiario;

	}
	
	public List<TotalDiario> funSqlGetTotalesXMesOriginalAjustado(String paramIdDivisa,
			int paramUsuario, String paramFecIni, String paramFecFin,String paramOrigen) {

		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
	 	sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append("SUM(IMPORTE) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append(",row_total_soa");
	    sb.append("\n"); sb.append(",row_total_sd");
	    sb.append("\n"); sb.append("From depura_mensual_comparativo_proyajust");
	    sb.append("\n"); sb.append("Where usuario = " + paramUsuario);
	   // sb.append("\n"); sb.append("AND FEC_VALOR BETWEEN convert(date, '" + paramFecIni + "', 103) AND convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "and mes between DATENAME(month, '" + paramFecIni + "') and DATENAME(month, '" + paramFecFin + "')");
	    sb.append("\n"); sb.append("AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "AND IDENTIFICADOR = '" + paramOrigen + "'");
	    sb.append("\n"); sb.append("GROUP BY USUARIO,row_total_soa, row_total_sd,ingeg,NATURALEZA");
	    sb.append("\n"); sb.append("ORDER BY 1");
		
	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXMesOriginalAjustado "+sql+"\n"+"\n");
		List<TotalDiario> totalesDiario = null;
		
		try{
			
			totalesDiario = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalDiario mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalDiario totalDiario = new TotalDiario();

				totalDiario.setTotal( rs.getDouble( "total" ) );
				totalDiario.setRow_total_sd( rs.getInt("row_total_sd"));
				totalDiario.setRow_total_soa(rs.getInt("row_total_soa"));
				
				return totalDiario;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesDiario;

	}
	
	public List<TotalDiario> funSqlGetTotalesXMesRealAjustado(String paramIdDivisa,
			int paramUsuario, String paramFecIni, String paramFecFin,String paramOrigen) {

		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
	 	sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append("SUM(IMPORTE) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append(",row_total_soa");
	    sb.append("\n"); sb.append(",row_total_sd");
	    sb.append("\n"); sb.append("From depura_mensual_comparativo_realajust");
	    sb.append("\n"); sb.append("Where usuario = " + paramUsuario);
	   // sb.append("\n"); sb.append("AND FEC_VALOR BETWEEN convert(date, '" + paramFecIni + "', 103) AND convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "and mes between DATENAME(month, '" + paramFecIni + "') and DATENAME(month, '" + paramFecFin + "')");
	    sb.append("\n"); sb.append("AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "AND IDENTIFICADOR = '" + paramOrigen + "'");
	    sb.append("\n"); sb.append("GROUP BY USUARIO,row_total_soa, row_total_sd,ingeg,NATURALEZA");
	    sb.append("\n"); sb.append("ORDER BY 1");
		
	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXMesRealAjustado "+sql+"\n"+"\n");
		List<TotalDiario> totalesDiario = null;
		
		try{
			
			totalesDiario = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalDiario mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalDiario totalDiario = new TotalDiario();

				totalDiario.setTotal( rs.getDouble( "total" ) );
				totalDiario.setRow_total_sd( rs.getInt("row_total_sd"));
				totalDiario.setRow_total_soa(rs.getInt("row_total_soa"));
				
				return totalDiario;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesDiario;

	}
	

	
	public List<TotalIngresoEgreso> funSqlGetTotalesXMesIngresoEgreso(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin) {

		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
	    sb.append("\n"); sb.append( "SELECT USUARIO,INGRESO_EGRESO,");
	    sb.append("\n"); sb.append("SUM (IMPORTE) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ",row_ingresoegreso");
	    sb.append("\n"); sb.append( "From SEMANAL_REAL_AJUSTADO");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "and fec_valor between convert(date, '" + paramFecIni + "',103) and convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,INGRESO_EGRESO,row_ingresoegreso,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 2 desc");
	    
	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXMesIngresoEgreso "+sql+"\n"+"\n");
		List<TotalIngresoEgreso> totalesIngresoEgreso = null;
		
		try{
			
			totalesIngresoEgreso = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalIngresoEgreso mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalIngresoEgreso totalIngresoEgreso = new TotalIngresoEgreso();								
				totalIngresoEgreso.setIngresoegreso(rs.getString("ingreso_egreso"));
				totalIngresoEgreso.setTotal( rs.getDouble( "total" ) );
				totalIngresoEgreso.setRow_ingresoegreso(rs.getInt("row_ingresoegreso"));
								
				return totalIngresoEgreso;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesIngresoEgreso;

	}
	
	public List<TotalIngresoEgreso> funSqlGetTotalesXMesIngresoEgresoAjustado(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin) {

		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
	    sb.append("\n"); sb.append( "SELECT USUARIO,INGRESO_EGRESO,");
	    sb.append("\n"); sb.append("SUM (IMPORTE) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ",row_ingresoegreso");
	    sb.append("\n"); sb.append( "From temp_saldo_cuadrante_nuevo");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "and fec_valor between convert(date, '" + paramFecIni + "',103) and convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,INGRESO_EGRESO,row_ingresoegreso,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 2 desc");
	    
	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXMesIngresoEgresoAjustado "+sql+"\n"+"\n");
		List<TotalIngresoEgreso> totalesIngresoEgreso = null;
		
		try{
			
			totalesIngresoEgreso = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalIngresoEgreso mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalIngresoEgreso totalIngresoEgreso = new TotalIngresoEgreso();								
				totalIngresoEgreso.setIngresoegreso(rs.getString("ingreso_egreso"));
				totalIngresoEgreso.setTotal( rs.getDouble( "total" ) );
				totalIngresoEgreso.setRow_ingresoegreso(rs.getInt("row_ingresoegreso"));
								
				return totalIngresoEgreso;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesIngresoEgreso;

	}
	public List<TotalIngresoEgreso> funSqlGetTotalesXMesIngresoEgresoOriginal(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin) {

		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
	    sb.append("\n"); sb.append( "SELECT USUARIO,INGRESO_EGRESO,");
	    sb.append("\n"); sb.append("SUM (IMPORTE) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ",row_ingresoegreso");
	    sb.append("\n"); sb.append( "From temp_saldo_cuadrante_diario");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "and fec_valor between convert(date, '" + paramFecIni + "',103) and convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,INGRESO_EGRESO,row_ingresoegreso,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 2 desc");
	    
	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXMesIngresoEgresoOriginal "+sql+"\n"+"\n");
		List<TotalIngresoEgreso> totalesIngresoEgreso = null;
		
		try{
			
			totalesIngresoEgreso = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalIngresoEgreso mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalIngresoEgreso totalIngresoEgreso = new TotalIngresoEgreso();								
				totalIngresoEgreso.setIngresoegreso(rs.getString("ingreso_egreso"));
				totalIngresoEgreso.setTotal( rs.getDouble( "total" ) );
				totalIngresoEgreso.setRow_ingresoegreso(rs.getInt("row_ingresoegreso"));
								
				return totalIngresoEgreso;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesIngresoEgreso;

	}
	public List<TotalIngresoEgreso> funSqlGetTotalesXMesIngresoEgresoMensual(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin) {

		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
	    sb.append("\n"); sb.append( "SELECT USUARIO,ingreso_egreso,");
	    sb.append("\n"); sb.append("SUM (IMPORTE) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ",row_ingresoegreso");
	    sb.append("\n"); sb.append( "From depura_mensual_real_ajustado");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "and fec_valor between convert(date, '" + paramFecIni + "',103) and convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "GROUP BY mes,USUARIO,ingreso_egreso,row_ingresoegreso,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 2 desc");
	    
	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXMesIngresoEgresoMensual "+sql+"\n"+"\n");
		List<TotalIngresoEgreso> totalesIngresoEgreso = null;
		
		try{
			
			totalesIngresoEgreso = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalIngresoEgreso mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalIngresoEgreso totalIngresoEgreso = new TotalIngresoEgreso();								
				totalIngresoEgreso.setIngresoegreso(rs.getString("ingreso_egreso"));
				totalIngresoEgreso.setTotal( rs.getDouble( "total" ) );
				totalIngresoEgreso.setRow_ingresoegreso(rs.getInt("row_ingresoegreso"));
								
				return totalIngresoEgreso;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesIngresoEgreso;

	}

	
	public List<TotalIngresoEgreso> funSqlGetTotalesXMesIngresoEgreso(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin,String paramOrigen) {

		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
	  
	    sb.append("\n"); sb.append( "SELECT USUARIO,ingeg,");
	    sb.append("\n"); sb.append("SUM(IMPORTE) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ",row_ingresoegreso");
	    sb.append("\n"); sb.append( "From depura_original_ajustado_real");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    //sb.append("\n"); sb.append( "and fec_valor between convert(date, '" + paramFecIni + "', 103) and convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "and mes between DATENAME(month, '" + paramFecIni + "') and DATENAME(month, '" + paramFecFin + "')");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "AND IDENTIFICADOR = '" + paramOrigen + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,ingeg,row_ingresoegreso,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 2 desc");
	    
	    
	    sql=sb.toString();
		System.out.println("funSqlGetTotalesXMesIngresoEgreso "+sql+"\n"+"\n");
		List<TotalIngresoEgreso> totalesIngresoEgreso = null;
		
		try{
			
			totalesIngresoEgreso = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalIngresoEgreso mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalIngresoEgreso totalIngresoEgreso = new TotalIngresoEgreso();								
				totalIngresoEgreso.setIngresoegreso(rs.getString("ingeg"));
				totalIngresoEgreso.setTotal( rs.getDouble( "total" ) );
				totalIngresoEgreso.setRow_ingresoegreso(rs.getInt("row_ingresoegreso"));
								
				return totalIngresoEgreso;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesIngresoEgreso;

	}

	public List<TotalIngresoEgreso> funSqlGetTotalesXMesIngresoEgresoOriginalAjustado(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin,String paramOrigen) {

		String sql = "";
		StringBuffer sb = new StringBuffer();	 
		
		  sb.append("\n"); sb.append( "SELECT USUARIO,ingeg,");
		    sb.append("\n"); sb.append("SUM(IMPORTE) as TOTAL"); // Funcion SQL TOTAL
		    sb.append("\n"); sb.append( ",row_ingresoegreso");
		    sb.append("\n"); sb.append( "From depura_mensual_comparativo_proyajust");
		    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
		    //sb.append("\n"); sb.append( "and fec_valor between convert(date, '" + paramFecIni + "', 103) and convert(date, '" + paramFecFin + "', 103)");
		    sb.append("\n"); sb.append( "and mes between DATENAME(month, '" + paramFecIni + "') and DATENAME(month, '" + paramFecFin + "')");
		    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
		    sb.append("\n"); sb.append( "AND IDENTIFICADOR = '" + paramOrigen + "'");
		    sb.append("\n"); sb.append( "GROUP BY USUARIO,ingeg,row_ingresoegreso,NATURALEZA");
		    sb.append("\n"); sb.append( "ORDER BY 2 desc");
	    sql=sb.toString();
		System.out.println("funSqlGetTotalesXMesIngresoEgresoOriginalAjustado "+sql+"\n"+"\n");
		List<TotalIngresoEgreso> totalesIngresoEgreso = null;
		
		try{
			
			totalesIngresoEgreso = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalIngresoEgreso mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalIngresoEgreso totalIngresoEgreso = new TotalIngresoEgreso();								
				totalIngresoEgreso.setIngresoegreso(rs.getString("ingeg"));
				totalIngresoEgreso.setTotal( rs.getDouble( "total" ) );
				totalIngresoEgreso.setRow_ingresoegreso(rs.getInt("row_ingresoegreso"));
								
				return totalIngresoEgreso;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesIngresoEgreso;

	}
	
	public List<TotalIngresoEgreso> funSqlGetTotalesXMesIngresoEgresoRealAjustado(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin,String paramOrigen) {

		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
	   /* sb.append("\n"); sb.append( "SELECT USUARIO,INGRESOEGRESO,");
	    sb.append("\n"); sb.append("dbo.func_TOTAL ('INGRESOEGRESO','NATURALEZA') as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ",row_ingresoegreso");
	    sb.append("\n"); sb.append( "From DIARIO_REAL_AJUSTADO");
	    sb.append("\n"); sb.append( "Where no_usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "and fec_valor between convert(date, '" + paramFecIni + "', 103) and convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "AND ID_ORIGEN = '" + paramOrigen + "'");
	    sb.append("\n"); sb.append( "GROUP BY NO_USUARIO,INGRESOEGRESO,row_ingresoegreso,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 2 desc");
	    */
		  sb.append("\n"); sb.append( "SELECT USUARIO,ingeg,");
		    sb.append("\n"); sb.append("SUM(IMPORTE) as TOTAL"); // Funcion SQL TOTAL
		    sb.append("\n"); sb.append( ",row_ingresoegreso");
		    sb.append("\n"); sb.append( "From depura_mensual_comparativo_realajust");
		    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
		    //sb.append("\n"); sb.append( "and fec_valor between convert(date, '" + paramFecIni + "', 103) and convert(date, '" + paramFecFin + "', 103)");
		    sb.append("\n"); sb.append( "and mes between DATENAME(month, '" + paramFecIni + "') and DATENAME(month, '" + paramFecFin + "')");
		    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
		    sb.append("\n"); sb.append( "AND IDENTIFICADOR = '" + paramOrigen + "'");
		    sb.append("\n"); sb.append( "GROUP BY USUARIO,ingeg,row_ingresoegreso,NATURALEZA");
		    sb.append("\n"); sb.append( "ORDER BY 2 desc");
	    sql=sb.toString();
		System.out.println("funSqlGetTotalesXMesIngresoEgresoRealAjustado "+sql+"\n"+"\n");
		List<TotalIngresoEgreso> totalesIngresoEgreso = null;
		
		try{
			
			totalesIngresoEgreso = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalIngresoEgreso mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalIngresoEgreso totalIngresoEgreso = new TotalIngresoEgreso();								
				totalIngresoEgreso.setIngresoegreso(rs.getString("ingeg"));
				totalIngresoEgreso.setTotal( rs.getDouble( "total" ) );
				totalIngresoEgreso.setRow_ingresoegreso(rs.getInt("row_ingresoegreso"));
								
				return totalIngresoEgreso;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesIngresoEgreso;

	}
	public List<TotalIngresoEgreso> funSqlGetTotalesXMesIngresoEgresoOriginalAjustado2(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin,String paramOrigen) {

		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
	    sb.append("\n"); sb.append( "SELECT USUARIO,ingeg,");
	    sb.append("\n"); sb.append("SUM(IMPORTE) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ",row_ingresoegreso");
	    sb.append("\n"); sb.append( "From depura_mensual_comparativo_proyajust");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    //sb.append("\n"); sb.append( "and fec_valor between convert(date, '" + paramFecIni + "', 103) and convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "and mes between DATENAME(month, '" + paramFecIni + "') and DATENAME(month, '" + paramFecFin + "')");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "AND IDENTIFICADOR = '" + paramOrigen + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,ingeg,row_ingresoegreso,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 2 desc");
	    
	    sql=sb.toString();
		System.out.println("funSqlGetTotalesXMesIngresoEgresoOriginalAjustado2 "+sql+"\n"+"\n");
		List<TotalIngresoEgreso> totalesIngresoEgreso = null;
		
		try{
			
			totalesIngresoEgreso = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalIngresoEgreso mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalIngresoEgreso totalIngresoEgreso = new TotalIngresoEgreso();								
				totalIngresoEgreso.setIngresoegreso(rs.getString("ingeg"));
				totalIngresoEgreso.setTotal( rs.getDouble( "total" ) );
				totalIngresoEgreso.setRow_ingresoegreso(rs.getInt("row_ingresoegreso"));
								
				return totalIngresoEgreso;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesIngresoEgreso;

	}
	
	public List<TotalIngresoEgreso> funSqlGetTotalesXMesIngresoEgresoRealAjustado2(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin,String paramOrigen) {

		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
	    sb.append("\n"); sb.append( "SELECT USUARIO,ingeg,");
	    sb.append("\n"); sb.append("SUM(IMPORTE) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ",row_ingresoegreso");
	    sb.append("\n"); sb.append( "From depura_mensual_comparativo_realajust");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    //sb.append("\n"); sb.append( "and fec_valor between convert(date, '" + paramFecIni + "', 103) and convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "and mes between DATENAME(month, '" + paramFecIni + "') and DATENAME(month, '" + paramFecFin + "')");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "AND IDENTIFICADOR = '" + paramOrigen + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,ingeg,row_ingresoegreso,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 2 desc");
	    
	    sql=sb.toString();
		System.out.println("funSqlGetTotalesXMesIngresoEgresoRealAjustado2 "+sql+"\n"+"\n");
		List<TotalIngresoEgreso> totalesIngresoEgreso = null;
		
		try{
			
			totalesIngresoEgreso = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalIngresoEgreso mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalIngresoEgreso totalIngresoEgreso = new TotalIngresoEgreso();								
				totalIngresoEgreso.setIngresoegreso(rs.getString("ingeg"));
				totalIngresoEgreso.setTotal( rs.getDouble( "total" ) );
				totalIngresoEgreso.setRow_ingresoegreso(rs.getInt("row_ingresoegreso"));
								
				return totalIngresoEgreso;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesIngresoEgreso;

	}

	
	public List<TotalTipoConcepto> funSqlGetTotalesXMESForTypeConcepto(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
		sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append( "INGRESO_EGRESO,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append("SUM(importe) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ", ROW_TIPO_CONCEPTO");
	    sb.append("\n"); sb.append( "From SEMANAL_REAL_AJUSTADO");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "AND FEC_VALOR BETWEEN convert(date, '" + paramFecIni + "', 103) and convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,INGRESO_EGRESO, id_tipo_concepto, descripcion, ROW_TIPO_CONCEPTO,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 1, 5");
	    
	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXMESForTypeConcepto "+sql+"\n"+"\n");
	    List<TotalTipoConcepto> totalesTipoConcepto = null;
		
		try{
			
			totalesTipoConcepto = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalTipoConcepto mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalTipoConcepto totalTipoConcepto = new TotalTipoConcepto();
								

				totalTipoConcepto.setIngresoegreso(rs.getString("ingreso_egreso"));
				totalTipoConcepto.setTotal( rs.getDouble( "total" ) );
				totalTipoConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
				totalTipoConcepto.setDescripcion(rs.getString("descripcion"));
				totalTipoConcepto.setRow_tipo_concepto(rs.getInt("row_tipo_concepto"));				
								
				return totalTipoConcepto;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForTypeConcepto" + ":," + sql + ":, M:");
			}		
		
		return totalesTipoConcepto;	
		
	}	
	public List<TotalTipoConcepto> funSqlGetTotalesXMESForTypeConceptoAjustado(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
		sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append( "INGRESO_EGRESO,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append("SUM(importe) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ", ROW_TIPO_CONCEPTO");
	    sb.append("\n"); sb.append( "From temp_saldo_cuadrante_nuevo");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "AND FEC_VALOR BETWEEN convert(date, '" + paramFecIni + "', 103) and convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,INGRESO_EGRESO, id_tipo_concepto, descripcion, ROW_TIPO_CONCEPTO,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 1, 5");
	    
	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXMESForTypeConceptoAjustado "+sql+"\n"+"\n");
	    List<TotalTipoConcepto> totalesTipoConcepto = null;
		
		try{
			
			totalesTipoConcepto = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalTipoConcepto mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalTipoConcepto totalTipoConcepto = new TotalTipoConcepto();
								

				totalTipoConcepto.setIngresoegreso(rs.getString("ingreso_egreso"));
				totalTipoConcepto.setTotal( rs.getDouble( "total" ) );
				totalTipoConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
				totalTipoConcepto.setDescripcion(rs.getString("descripcion"));
				totalTipoConcepto.setRow_tipo_concepto(rs.getInt("row_tipo_concepto"));				
								
				return totalTipoConcepto;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForTypeConcepto" + ":," + sql + ":, M:");
			}		
		
		return totalesTipoConcepto;	
		
	}	
	/*hector*/
	
	public List<TotalTipoConcepto> funSqlGetTotalesXMESForTypeConceptoOriginal(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
		sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append( "INGRESO_EGRESO,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append("SUM(importe) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ", ROW_TIPO_CONCEPTO");
	    sb.append("\n"); sb.append( "From temp_saldo_cuadrante_diario");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "AND FEC_VALOR BETWEEN convert(date, '" + paramFecIni + "', 103) and convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,INGRESO_EGRESO, id_tipo_concepto, descripcion, ROW_TIPO_CONCEPTO,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 1, 5");
	    
	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXMESForTypeConceptoOriginal "+sql+"\n"+"\n");
	    List<TotalTipoConcepto> totalesTipoConcepto = null;
		
		try{
			
			totalesTipoConcepto = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalTipoConcepto mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalTipoConcepto totalTipoConcepto = new TotalTipoConcepto();
								

				totalTipoConcepto.setIngresoegreso(rs.getString("ingreso_egreso"));
				totalTipoConcepto.setTotal( rs.getDouble( "total" ) );
				totalTipoConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
				totalTipoConcepto.setDescripcion(rs.getString("descripcion"));
				totalTipoConcepto.setRow_tipo_concepto(rs.getInt("row_tipo_concepto"));				
								
				return totalTipoConcepto;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForTypeConcepto" + ":," + sql + ":, M:");
			}		
		
		return totalesTipoConcepto;	
		
	}	
	
	public List<TotalTipoConcepto> funSqlGetTotalesXMESForTypeConceptoMensual(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
		sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append( "ingreso_egreso,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append("SUM(importe) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ", ROW_TIPO_CONCEPTO");
	    sb.append("\n"); sb.append( "From depura_mensual_real_ajustado");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "AND FEC_VALOR BETWEEN convert(date, '" + paramFecIni + "', 103) and convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "GROUP BY mes,USUARIO,ingreso_egreso, id_tipo_concepto, descripcion, ROW_TIPO_CONCEPTO,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 1, 5");
	    
	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXMESForTypeConceptoMensual "+sql+"\n"+"\n");
	    List<TotalTipoConcepto> totalesTipoConcepto = null;
		
		try{
			
			totalesTipoConcepto = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalTipoConcepto mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalTipoConcepto totalTipoConcepto = new TotalTipoConcepto();
								

				totalTipoConcepto.setIngresoegreso(rs.getString("ingreso_egreso"));
				totalTipoConcepto.setTotal( rs.getDouble( "total" ) );
				totalTipoConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
				totalTipoConcepto.setDescripcion(rs.getString("descripcion"));
				totalTipoConcepto.setRow_tipo_concepto(rs.getInt("row_tipo_concepto"));				
								
				return totalTipoConcepto;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForTypeConcepto" + ":," + sql + ":, M:");
			}		
		
		return totalesTipoConcepto;	
		
	}	
	/*hector*/
	
	public List<TotalTipoConcepto> funSqlGetTotalesXMESForTypeConcepto(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin,String paramOrigen) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();		 
		
		sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append( "ingeg,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append("sum(importe) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ", ROW_TIPO_CONCEPTO");
	    sb.append("\n"); sb.append( "From depura_original_ajustado_real");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "and mes between DATENAME(month, '" + paramFecIni + "') and DATENAME(month, '" + paramFecFin + "')");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "AND identificador = '" + paramOrigen + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,ingeg, id_tipo_concepto, descripcion, ROW_TIPO_CONCEPTO,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 1, 5");
	    
	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXMESForTypeConcepto "+sql+"\n"+"\n");
	    List<TotalTipoConcepto> totalesTipoConcepto = null;
		
		try{
			
			totalesTipoConcepto = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalTipoConcepto mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalTipoConcepto totalTipoConcepto = new TotalTipoConcepto();
								

				totalTipoConcepto.setIngresoegreso(rs.getString("ingeg"));
				totalTipoConcepto.setTotal( rs.getDouble( "total" ) );
				totalTipoConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
				totalTipoConcepto.setDescripcion(rs.getString("descripcion"));
				totalTipoConcepto.setRow_tipo_concepto(rs.getInt("row_tipo_concepto"));				
								
				return totalTipoConcepto;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForTypeConcepto" + ":," + sql + ":, M:");
			}		
		
		return totalesTipoConcepto;	
		
	}
	public List<TotalTipoConcepto> funSqlGetTotalesXMESForTypeConceptoOriginalAjustado2(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin,String paramOrigen) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
		/*sb.append("\n"); sb.append("SELECT NO_USUARIO,");
	    sb.append("\n"); sb.append( "INGRESOEGRESO,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append("dbo.func_TOTAL ('INGRESOEGRESO','NATURALEZA') as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ", ROW_TIPO_CONCEPTO");
	    sb.append("\n"); sb.append( "From DIARIO_REAL_AJUSTADO");
	    sb.append("\n"); sb.append( "Where no_usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "AND FEC_VALOR BETWEEN convert(date, '" + paramFecIni + "', 103) and convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "AND ID_ORIGEN = '" + paramOrigen + "'");
	    sb.append("\n"); sb.append( "GROUP BY NO_USUARIO,INGRESOEGRESO, id_tipo_concepto, descripcion, ROW_TIPO_CONCEPTO,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 1, 5");*/
		sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append( "ingeg,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append("sum(importe) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ", ROW_TIPO_CONCEPTO");
	    sb.append("\n"); sb.append( "From depura_mensual_comparativo_proyajust");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "and mes between DATENAME(month, '" + paramFecIni + "') and DATENAME(month, '" + paramFecFin + "')");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "AND identificador = '" + paramOrigen + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,ingeg, id_tipo_concepto, descripcion, ROW_TIPO_CONCEPTO,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 1, 5");
	    
	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXMESForTypeConceptoOriginalAjustado2 "+sql+"\n"+"\n");
	    List<TotalTipoConcepto> totalesTipoConcepto = null;
		
		try{
			
			totalesTipoConcepto = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalTipoConcepto mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalTipoConcepto totalTipoConcepto = new TotalTipoConcepto();
								

				totalTipoConcepto.setIngresoegreso(rs.getString("ingeg"));
				totalTipoConcepto.setTotal( rs.getDouble( "total" ) );
				totalTipoConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
				totalTipoConcepto.setDescripcion(rs.getString("descripcion"));
				totalTipoConcepto.setRow_tipo_concepto(rs.getInt("row_tipo_concepto"));				
								
				return totalTipoConcepto;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForTypeConcepto" + ":," + sql + ":, M:");
			}		
		
		return totalesTipoConcepto;	
		
	}
	
	
	public List<TotalTipoConcepto> funSqlGetTotalesXMESForTypeConceptoRealAjustado2(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin,String paramOrigen) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
		/*sb.append("\n"); sb.append("SELECT NO_USUARIO,");
	    sb.append("\n"); sb.append( "INGRESOEGRESO,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append("dbo.func_TOTAL ('INGRESOEGRESO','NATURALEZA') as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ", ROW_TIPO_CONCEPTO");
	    sb.append("\n"); sb.append( "From DIARIO_REAL_AJUSTADO");
	    sb.append("\n"); sb.append( "Where no_usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "AND FEC_VALOR BETWEEN convert(date, '" + paramFecIni + "', 103) and convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "AND ID_ORIGEN = '" + paramOrigen + "'");
	    sb.append("\n"); sb.append( "GROUP BY NO_USUARIO,INGRESOEGRESO, id_tipo_concepto, descripcion, ROW_TIPO_CONCEPTO,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 1, 5");*/
		sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append( "ingeg,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append("sum(importe) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ", ROW_TIPO_CONCEPTO");
	    sb.append("\n"); sb.append( "From depura_mensual_comparativo_realajust");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "and mes between DATENAME(month, '" + paramFecIni + "') and DATENAME(month, '" + paramFecFin + "')");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "AND identificador = '" + paramOrigen + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,ingeg, id_tipo_concepto, descripcion, ROW_TIPO_CONCEPTO,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 1, 5");
	    
	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXMESForTypeConceptoRealAjustado2 "+sql+"\n"+"\n");
	    List<TotalTipoConcepto> totalesTipoConcepto = null;
		
		try{
			
			totalesTipoConcepto = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalTipoConcepto mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalTipoConcepto totalTipoConcepto = new TotalTipoConcepto();
								

				totalTipoConcepto.setIngresoegreso(rs.getString("ingeg"));
				totalTipoConcepto.setTotal( rs.getDouble( "total" ) );
				totalTipoConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
				totalTipoConcepto.setDescripcion(rs.getString("descripcion"));
				totalTipoConcepto.setRow_tipo_concepto(rs.getInt("row_tipo_concepto"));				
								
				return totalTipoConcepto;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForTypeConcepto" + ":," + sql + ":, M:");
			}		
		
		return totalesTipoConcepto;	
		
	}
	public List<TotalTipoConcepto> funSqlGetTotalesXMESForTypeConceptoOriginalAjustado(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin,String paramOrigen) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
		sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append( "ingeg,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append("sum(importe) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ", ROW_TIPO_CONCEPTO");
	    sb.append("\n"); sb.append( "From depura_mensual_comparativo_proyajust");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "and mes between DATENAME(month, '" + paramFecIni + "') and DATENAME(month, '" + paramFecFin + "')");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "AND identificador = '" + paramOrigen + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,ingeg, id_tipo_concepto, descripcion, ROW_TIPO_CONCEPTO,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 1, 5");
	    
	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXMESForTypeConceptoOriginalAjustado "+sql+"\n"+"\n");
	    List<TotalTipoConcepto> totalesTipoConcepto = null;
		
		try{
			
			totalesTipoConcepto = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalTipoConcepto mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalTipoConcepto totalTipoConcepto = new TotalTipoConcepto();
								

				totalTipoConcepto.setIngresoegreso(rs.getString("ingeg"));
				totalTipoConcepto.setTotal( rs.getDouble( "total" ) );
				totalTipoConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
				totalTipoConcepto.setDescripcion(rs.getString("descripcion"));
				totalTipoConcepto.setRow_tipo_concepto(rs.getInt("row_tipo_concepto"));				
								
				return totalTipoConcepto;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForTypeConcepto" + ":," + sql + ":, M:");
			}		
		
		return totalesTipoConcepto;	
		
	}
	public List<TotalTipoConcepto> funSqlGetTotalesXMESForTypeConceptoRealAjustado(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin,String paramOrigen) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
		sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append( "ingeg,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append("sum(importe) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ", ROW_TIPO_CONCEPTO");
	    sb.append("\n"); sb.append( "From depura_mensual_comparativo_realajust");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "and mes between DATENAME(month, '" + paramFecIni + "') and DATENAME(month, '" + paramFecFin + "')");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "AND identificador = '" + paramOrigen + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,ingeg, id_tipo_concepto, descripcion, ROW_TIPO_CONCEPTO,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 1, 5");
	    
	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXMESForTypeConceptoRealAjustado "+sql+"\n"+"\n");
	    List<TotalTipoConcepto> totalesTipoConcepto = null;
		
		try{
			
			totalesTipoConcepto = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalTipoConcepto mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalTipoConcepto totalTipoConcepto = new TotalTipoConcepto();
								

				totalTipoConcepto.setIngresoegreso(rs.getString("ingeg"));
				totalTipoConcepto.setTotal( rs.getDouble( "total" ) );
				totalTipoConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
				totalTipoConcepto.setDescripcion(rs.getString("descripcion"));
				totalTipoConcepto.setRow_tipo_concepto(rs.getInt("row_tipo_concepto"));				
								
				return totalTipoConcepto;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForTypeConcepto" + ":," + sql + ":, M:");
			}		
		
		return totalesTipoConcepto;	
		
	}
	public List<TotalConcepto> funSqlGetTotalesXMesForConcepto(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin) {

		String sql = "";
		StringBuffer sb = new StringBuffer();	    
		
		sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append( "ingreso_egreso,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append( "cve_concepto,");
	    sb.append("\n"); sb.append( "desc_larga,");
	    sb.append("\n"); sb.append("SUM (importe) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ", row_concepto");
	    sb.append("\n"); sb.append( "From semanal_real_ajustado");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "and fec_valor between convert(date, '" + paramFecIni + "', 103) and convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO, ingreso_egreso,id_tipo_concepto, descripcion,cve_concepto, desc_larga,row_concepto,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 2");
			    
			    sql=sb.toString();
			    System.out.println("funSqlGetTotalesXMesForConcepto "+sql+"\n"+"\n");   
			    List<TotalConcepto> totalesConcepto = null;
				
				try{
					
					totalesConcepto = jdbcTemplate.query(sql, new RowMapper(){
						
					public TotalConcepto mapRow(ResultSet rs, int idx) throws SQLException {
						
						TotalConcepto totalConcepto = new TotalConcepto();										
				
						totalConcepto.setIngresoegreso(rs.getString("ingreso_egreso"));
						totalConcepto.setTotal( rs.getDouble( "total" ) );
						totalConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
						totalConcepto.setDescripcion(rs.getString("descripcion"));
						totalConcepto.setCve_concepto(rs.getInt("cve_concepto"));
						totalConcepto.setDesc_larga(rs.getString("desc_larga"));
						totalConcepto.setRow_concepto(rs.getInt("row_concepto"));
						
						return totalConcepto;
						
					}});
				
					}catch(Exception e){
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForConcepto" + ":," + sql + ":, M:");
					}		
				
				return totalesConcepto;
	}
	
	public List<TotalConcepto> funSqlGetTotalesXMesForConceptoAjustado(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin) {

		String sql = "";
		StringBuffer sb = new StringBuffer();	    
		
		sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append( "ingreso_egreso,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append( "cve_concepto,");
	    sb.append("\n"); sb.append( "desc_larga,");
	    sb.append("\n"); sb.append("SUM (importe) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ", row_concepto");
	    sb.append("\n"); sb.append( "From temp_saldo_cuadrante_nuevo");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "and fec_valor between convert(date, '" + paramFecIni + "', 103) and convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO, ingreso_egreso,id_tipo_concepto, descripcion,cve_concepto, desc_larga,row_concepto,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 2");
			    
			    sql=sb.toString();
			    System.out.println("funSqlGetTotalesXMesForConceptoAjustado "+sql+"\n"+"\n");   
			    List<TotalConcepto> totalesConcepto = null;
				
				try{
					
					totalesConcepto = jdbcTemplate.query(sql, new RowMapper(){
						
					public TotalConcepto mapRow(ResultSet rs, int idx) throws SQLException {
						
						TotalConcepto totalConcepto = new TotalConcepto();										
				
						totalConcepto.setIngresoegreso(rs.getString("ingreso_egreso"));
						totalConcepto.setTotal( rs.getDouble( "total" ) );
						totalConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
						totalConcepto.setDescripcion(rs.getString("descripcion"));
						totalConcepto.setCve_concepto(rs.getInt("cve_concepto"));
						totalConcepto.setDesc_larga(rs.getString("desc_larga"));
						totalConcepto.setRow_concepto(rs.getInt("row_concepto"));
						
						return totalConcepto;
						
					}});
				
					}catch(Exception e){
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForConcepto" + ":," + sql + ":, M:");
					}		
				
				return totalesConcepto;
	}
	
	
	public List<TotalConcepto> funSqlGetTotalesXMesForConceptoAjustadoExcel(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin) {

		String sql = "";
		StringBuffer sb = new StringBuffer();	    
		
		sb.append("\n"); sb.append("SELECT USUARIO,ingreso_egreso,id_tipo_concepto,descripcion,cve_concepto,");
	    sb.append("\n"); sb.append( "desc_larga,SUM (importe) as TOTAL, row_concepto,mes");
	    sb.append("\n"); sb.append( "From temp_saldo_cuadrante_nuevo");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    //sb.append("\n"); sb.append( "and fec_valor between convert(date, '" + paramFecIni + "', 103) and convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO, ingreso_egreso,id_tipo_concepto, descripcion,cve_concepto, desc_larga,row_concepto,NATURALEZA,mes");
	    sb.append("\n"); sb.append( "ORDER BY row_concepto");
			    
			    sql=sb.toString();
			    System.out.println("funSqlGetTotalesXMesForConceptoAjustado "+sql+"\n"+"\n");   
			    List<TotalConcepto> totalesConcepto = null;
				
				try{
					
					totalesConcepto = jdbcTemplate.query(sql, new RowMapper(){
						
					public TotalConcepto mapRow(ResultSet rs, int idx) throws SQLException {
						
						TotalConcepto totalConcepto = new TotalConcepto();										
				
						totalConcepto.setIngresoegreso(rs.getString("ingreso_egreso"));
						totalConcepto.setTotal( rs.getDouble( "total" ) );
						totalConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
						totalConcepto.setDescripcion(rs.getString("descripcion"));
						totalConcepto.setCve_concepto(rs.getInt("cve_concepto"));
						totalConcepto.setDesc_larga(rs.getString("desc_larga"));
						totalConcepto.setRow_concepto(rs.getInt("row_concepto"));
						totalConcepto.setMes(rs.getString("mes"));
						
						return totalConcepto;
						
					}});
				
					}catch(Exception e){
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForConcepto" + ":," + sql + ":, M:");
					}		
				
				return totalesConcepto;
	}
	
	
	/**
	 * 
	 */
	public List<TotalConcepto> funSqlGetTotalesXMesForConceptoOriginal(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin)
	{
		String sql = "";
		StringBuffer sb = new StringBuffer();	    
		
		sb.append("\nSELECT USUARIO, ingreso_egreso, id_tipo_concepto, descripcion, cve_concepto, desc_larga,");
	    sb.append("\nSUM (importe) as TOTAL, row_concepto");
	    sb.append("\nFrom temp_saldo_cuadrante_diario");
	    sb.append("\nWhere usuario = " + paramUsuario);
	    sb.append("\nand fec_valor between convert(date, '" + paramFecIni + "', 103) and convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\nAND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\nGROUP BY USUARIO, ingreso_egreso,id_tipo_concepto, descripcion,cve_concepto, desc_larga,row_concepto,NATURALEZA");
	    sb.append("\nORDER BY 2");
	    
	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXMesForConceptoOriginal: " + sql);
	    logger.info("funSqlGetTotalesXMesForConceptoOriginal: " + sql);
	    List<TotalConcepto> totalesConcepto = null;
	    
	    try{
	    	totalesConcepto = jdbcTemplate.query(sql, new RowMapper(){
	    		public TotalConcepto mapRow(ResultSet rs, int idx) throws SQLException {
	    			TotalConcepto totalConcepto = new TotalConcepto();										
	    			
	    			totalConcepto.setIngresoegreso(rs.getString("ingreso_egreso"));
	    			totalConcepto.setTotal( rs.getDouble( "total" ) );
	    			totalConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
	    			totalConcepto.setDescripcion(rs.getString("descripcion"));
	    			totalConcepto.setCve_concepto(rs.getInt("cve_concepto"));
	    			totalConcepto.setDesc_larga(rs.getString("desc_larga"));
	    			totalConcepto.setRow_concepto(rs.getInt("row_concepto")); 
	    			
	    			return totalConcepto;
	    		}});
	    }
	    catch(Exception e){
	    	e.printStackTrace();
	    	logger.error(e);
	    	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForConcepto" + ":," + sql + ":, M:");
	    }
	    return totalesConcepto;
	}
	
	public List<TotalConcepto> funSqlGetTotalesXMesForConceptoOriginalExcel(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin)
	{
		String sql = "";
		StringBuffer sb = new StringBuffer();	    
		
		sb.append("\nSELECT USUARIO, ingreso_egreso, id_tipo_concepto, descripcion, cve_concepto, desc_larga, mes,");
	    sb.append("\nSUM (importe) as TOTAL, row_concepto");
	    sb.append("\nFrom temp_saldo_cuadrante_diario");
	    sb.append("\nWhere usuario = " + paramUsuario);
	  //  sb.append("\nand mes='" + paramFecIni + "'");
	    sb.append("\nAND ID_DIVISA = '" + paramIdDivisa + "'");
	  //  sb.append("\nAND ingreso_egreso = 'I'");
	    
	    sb.append("\nGROUP BY USUARIO, ingreso_egreso,id_tipo_concepto, descripcion,cve_concepto, desc_larga,mes,row_concepto,NATURALEZA");
	    sb.append("\nORDER BY row_concepto");
	    
	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXMesForConceptoOriginal: " + sql);
	    logger.info("funSqlGetTotalesXMesForConceptoOriginal: " + sql);
	    List<TotalConcepto> totalesConcepto = null;
	    
	    try{
	    	totalesConcepto = jdbcTemplate.query(sql, new RowMapper(){
	    		public TotalConcepto mapRow(ResultSet rs, int idx) throws SQLException {
	    			TotalConcepto totalConcepto = new TotalConcepto();										
	    			
	    			totalConcepto.setIngresoegreso(rs.getString("ingreso_egreso"));
	    			totalConcepto.setTotal( rs.getDouble( "total" ) );
	    			totalConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
	    			totalConcepto.setDescripcion(rs.getString("descripcion"));
	    			totalConcepto.setCve_concepto(rs.getInt("cve_concepto"));
	    			totalConcepto.setDesc_larga(rs.getString("desc_larga"));
	    			totalConcepto.setRow_concepto(rs.getInt("row_concepto"));
	    			totalConcepto.setMes(rs.getString("mes"));
	    			totalConcepto.setAuxiliar(rs.getFloat("total")); 
	    			return totalConcepto;
	    		}});
	    }
	    catch(Exception e){
	    	e.printStackTrace();
	    	logger.error(e);
	    	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForConcepto" + ":," + sql + ":, M:");
	    }
	    return totalesConcepto;
	}
	
	
	public List<TotalSemanaDto> funSqlGetTotalesXMesForConceptoSemanalExcel(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin)
	{
		String sql = "";
		StringBuffer sb = new StringBuffer();	    
		
		sb.append("\n SELECT * "); 
	    sb.append("\n From temp_excel_semanal");
	    sb.append("\n Where usuario = " + paramUsuario); 
	    sb.append("\n AND DIVISA = '" + paramIdDivisa + "'"); 
	   // sb.append("\n AND ingreso_egreso = 'E'"); 
	    sb.append("\n ORDER BY 6,9");
	    
	    sql=sb.toString(); 
	    List<TotalSemanaDto> totalesConcepto = null;
	    
	    try{
	    	totalesConcepto = jdbcTemplate.query(sql, new RowMapper(){
	    		public TotalSemanaDto mapRow(ResultSet rs, int idx) throws SQLException {
	    			TotalSemanaDto totalConcepto = new TotalSemanaDto();										
	    			
	    			totalConcepto.setIngreso_egreso(rs.getString("ingreso_egreso"));
	    			totalConcepto.setTotal( rs.getDouble( "total" ) );
	    			totalConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
	    			totalConcepto.setDescripcion(rs.getString("descripcion"));
	    			totalConcepto.setColumna(rs.getInt("columna"));
	    			totalConcepto.setDesc_larga(rs.getString("desc_larga")); 
	    			totalConcepto.setMes(rs.getString("mes")); 
	    			totalConcepto.setIdentificador(rs.getString("identificardor"));
	    			return totalConcepto;
	    		}});
	    }
	    catch(Exception e){
	    	e.printStackTrace();
	    	logger.error(e);
	    	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesXMesForConceptoSemanalExcel" + ":," + sql + ":, M:");
	    }
	    return totalesConcepto;
	}
	
	
	/*hector*/
	
	public List<TotalConcepto> funSqlGetTotalesXMesForConceptoMensual(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin) {

		String sql = "";
		StringBuffer sb = new StringBuffer();	    
		sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append( "ingreso_egreso,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append( "cve_concepto,");
	    sb.append("\n"); sb.append( "desc_larga,");
	    sb.append("\n"); sb.append("SUM (importe) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ", row_concepto");
	    sb.append("\n"); sb.append( "From depura_mensual_real_ajustado");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "and fec_valor between convert(date, '" + paramFecIni + "', 103) and convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO, ingreso_egreso,id_tipo_concepto, descripcion,cve_concepto, desc_larga,row_concepto,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 2");
			    
			    sql=sb.toString();
			    System.out.println("funSqlGetTotalesXMesForConceptoMensual "+sql+"\n"+"\n");   
			    List<TotalConcepto> totalesConcepto = null;
				
				try{
					
					totalesConcepto = jdbcTemplate.query(sql, new RowMapper(){
						
					public TotalConcepto mapRow(ResultSet rs, int idx) throws SQLException {
						
						TotalConcepto totalConcepto = new TotalConcepto();										
				
						totalConcepto.setIngresoegreso(rs.getString("ingreso_egreso"));
						totalConcepto.setTotal( rs.getDouble( "total" ) );
						totalConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
						totalConcepto.setDescripcion(rs.getString("descripcion"));
						totalConcepto.setCve_concepto(rs.getInt("cve_concepto"));
						totalConcepto.setDesc_larga(rs.getString("desc_larga"));
						totalConcepto.setRow_concepto(rs.getInt("row_concepto"));
						
						return totalConcepto;
						
					}});
				
					}catch(Exception e){
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForConcepto" + ":," + sql + ":, M:");
					}		
				
				return totalesConcepto;
	}
	/*hector*/

	public List<TotalConcepto> funSqlGetTotalesXMesForConcepto(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin,String paramOrigen) {

		String sql = "";
		StringBuffer sb = new StringBuffer();	 
		
	    sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append( "ingeg,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append( "cve_concepto,");
	    sb.append("\n"); sb.append( "desc_larga,");
	    sb.append("\n"); sb.append("importe as total "); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ", row_concepto");
	    sb.append("\n"); sb.append( "From depura_original_ajustado_real");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "and mes between DATENAME(month, '" + paramFecIni + "') and DATENAME(month, '" + paramFecFin + "')");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "AND identificador = '" + paramOrigen + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO, ingeg,id_tipo_concepto, descripcion,cve_concepto, desc_larga,row_concepto,NATURALEZA,importe,identificador");
	    sb.append("\n"); sb.append( "ORDER BY 2");
			    
			    
			    sql=sb.toString();
			    System.out.println("funSqlGetTotalesXMesForConcepto "+sql+"\n"+"\n");
				  
			    List<TotalConcepto> totalesConcepto = null;
				
				try{
					
					totalesConcepto = jdbcTemplate.query(sql, new RowMapper(){
						
					public TotalConcepto mapRow(ResultSet rs, int idx) throws SQLException {
						
						TotalConcepto totalConcepto = new TotalConcepto();										
				
						totalConcepto.setIngresoegreso(rs.getString("ingeg"));
						totalConcepto.setTotal( rs.getDouble( "total" ) );
						totalConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
						totalConcepto.setDescripcion(rs.getString("descripcion"));
						totalConcepto.setCve_concepto(rs.getInt("cve_concepto"));
						totalConcepto.setDesc_larga(rs.getString("desc_larga"));
						totalConcepto.setRow_concepto(rs.getInt("row_concepto"));
						
						return totalConcepto;
						
					}});
				
					}catch(Exception e){
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForConcepto" + ":," + sql + ":, M:");
					}		
				
				return totalesConcepto;
	}	
	
	public List<TotalConcepto> funSqlGetTotalesXMesForConceptoOriginalAjustado2(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin,String paramOrigen) {

		String sql = "";
		StringBuffer sb = new StringBuffer();	    
		
		sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append( "ingeg,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append( "cve_concepto,");
	    sb.append("\n"); sb.append( "desc_larga,");
	    sb.append("\n"); sb.append("importe as total "); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ", row_concepto");
	    sb.append("\n"); sb.append( "From depura_mensual_comparativo_proyajust");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario); 
	   // sb.append("\n"); sb.append( "and fec_valor between convert(date, '" + paramFecIni + "', 103) and convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "and mes between DATENAME(month, '" + paramFecIni + "') and DATENAME(month, '" + paramFecFin + "')");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "AND identificador = '" + paramOrigen + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO, ingeg,id_tipo_concepto, descripcion,cve_concepto, desc_larga,row_concepto,NATURALEZA,importe,identificador");
	    sb.append("\n"); sb.append( "ORDER BY 2");
			    
			    sql=sb.toString();
			    System.out.println("funSqlGetTotalesXMesForConceptoOriginalAjustado2 "+sql+"\n"+"\n");
				  
			    List<TotalConcepto> totalesConcepto = null;
				
				try{
					
					totalesConcepto = jdbcTemplate.query(sql, new RowMapper(){
						
					public TotalConcepto mapRow(ResultSet rs, int idx) throws SQLException {
						
						TotalConcepto totalConcepto = new TotalConcepto();										
				
						totalConcepto.setIngresoegreso(rs.getString("ingeg"));
						totalConcepto.setTotal( rs.getDouble( "total" ) );
						totalConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
						totalConcepto.setDescripcion(rs.getString("descripcion"));
						totalConcepto.setCve_concepto(rs.getInt("cve_concepto"));
						totalConcepto.setDesc_larga(rs.getString("desc_larga"));
						totalConcepto.setRow_concepto(rs.getInt("row_concepto"));
						
						return totalConcepto;
						
					}});
				
					}catch(Exception e){
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForConcepto" + ":," + sql + ":, M:");
					}		
				
				return totalesConcepto;
	}	
	
	public List<TotalConcepto> funSqlGetTotalesXMesForConceptoOriginalAjustadoExcel(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin) {

		String sql = "";
		StringBuffer sb = new StringBuffer();	    
		
		sb.append("\n"); sb.append("SELECT USUARIO,ingeg,id_tipo_concepto,descripcion,");
	    sb.append("\n"); sb.append( "cve_concepto,desc_larga,importe as total "); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ", row_concepto,mes,identificador");
	    sb.append("\n"); sb.append( "From depura_mensual_comparativo_proyajust");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario); 
	   // sb.append("\n"); sb.append( "and fec_valor between convert(date, '" + paramFecIni + "', 103) and convert(date, '" + paramFecFin + "', 103)");
	  //  sb.append("\n"); sb.append( "and mes between DATENAME(month, '" + paramFecIni + "') and DATENAME(month, '" + paramFecFin + "')");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    
	    sb.append("\n"); sb.append( "GROUP BY USUARIO, ingeg,id_tipo_concepto, descripcion,cve_concepto, desc_larga,row_concepto,NATURALEZA,importe,identificador,mes");
	    sb.append("\n"); sb.append( "ORDER BY row_concepto,mes");
			    
			    sql=sb.toString();
			    System.out.println("funSqlGetTotalesXMesForConceptoOriginalAjustado2 "+sql+"\n"+"\n");
				  
			    List<TotalConcepto> totalesConcepto = null;
				
				try{
					
					totalesConcepto = jdbcTemplate.query(sql, new RowMapper(){
						
					public TotalConcepto mapRow(ResultSet rs, int idx) throws SQLException {
						
						TotalConcepto totalConcepto = new TotalConcepto();										
				
						totalConcepto.setIngresoegreso(rs.getString("ingeg"));
						totalConcepto.setTotal( rs.getDouble( "total" ) );
						totalConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
						totalConcepto.setDescripcion(rs.getString("descripcion"));
						totalConcepto.setCve_concepto(rs.getInt("cve_concepto"));
						totalConcepto.setDesc_larga(rs.getString("desc_larga"));
						totalConcepto.setRow_concepto(rs.getInt("row_concepto"));
						totalConcepto.setMes(rs.getString("mes"));
						totalConcepto.setIdentificador(rs.getString("identificador"));
						
						return totalConcepto;
						
					}});
				
					}catch(Exception e){
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesXMesForConceptoOriginalAjustadoExcel" + ":," + sql + ":, M:");
					}		
				
				return totalesConcepto;
	}	
	
	public List<TotalConcepto> funSqlGetTotalesXMesForConceptoRealAjustado2(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin,String paramOrigen) {

		String sql = "";
		StringBuffer sb = new StringBuffer();	    
		
		sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append( "ingeg,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append( "cve_concepto,");
	    sb.append("\n"); sb.append( "desc_larga,");
	    sb.append("\n"); sb.append("importe as total "); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ", row_concepto");
	    sb.append("\n"); sb.append( "From depura_mensual_comparativo_realajust");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario); 
	   // sb.append("\n"); sb.append( "and fec_valor between convert(date, '" + paramFecIni + "', 103) and convert(date, '" + paramFecFin + "', 103)");
	    sb.append("\n"); sb.append( "and mes between DATENAME(month, '" + paramFecIni + "') and DATENAME(month, '" + paramFecFin + "')");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "AND identificador = '" + paramOrigen + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO, ingeg,id_tipo_concepto, descripcion,cve_concepto, desc_larga,row_concepto,NATURALEZA,importe,identificador");
	    sb.append("\n"); sb.append( "ORDER BY 2");
			    
			    sql=sb.toString();
			    System.out.println("funSqlGetTotalesXMesForConceptoRealAjustado2 "+sql+"\n"+"\n");
				  
			    List<TotalConcepto> totalesConcepto = null;
				
				try{
					
					totalesConcepto = jdbcTemplate.query(sql, new RowMapper(){
						
					public TotalConcepto mapRow(ResultSet rs, int idx) throws SQLException {
						
						TotalConcepto totalConcepto = new TotalConcepto();										
				
						totalConcepto.setIngresoegreso(rs.getString("ingeg"));
						totalConcepto.setTotal( rs.getDouble( "total" ) );
						totalConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
						totalConcepto.setDescripcion(rs.getString("descripcion"));
						totalConcepto.setCve_concepto(rs.getInt("cve_concepto"));
						totalConcepto.setDesc_larga(rs.getString("desc_larga"));
						totalConcepto.setRow_concepto(rs.getInt("row_concepto"));
						
						return totalConcepto;
						
					}});
				
					}catch(Exception e){
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForConcepto" + ":," + sql + ":, M:");
					}		
				
				return totalesConcepto;
	}	
	
	
	public List<TotalConcepto> funSqlGetTotalesXMesForConceptoOriginalAjustado(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin,String paramOrigen) {

		String sql = "";
		StringBuffer sb = new StringBuffer();	    
		
		sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append( "ingeg,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append( "cve_concepto,");
	    sb.append("\n"); sb.append( "desc_larga,");
	    sb.append("\n"); sb.append("importe as total "); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ", row_concepto");
	    sb.append("\n"); sb.append( "From depura_mensual_comparativo_proyajust");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "and mes between DATENAME(month, '" + paramFecIni + "') and DATENAME(month, '" + paramFecFin + "')");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "AND identificador = '" + paramOrigen + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO, ingeg,id_tipo_concepto, descripcion,cve_concepto, desc_larga,row_concepto,NATURALEZA,importe,identificador");
	    sb.append("\n"); sb.append( "ORDER BY 2");
			    
			    sql=sb.toString();
			    System.out.println("funSqlGetTotalesXMesForConceptoOriginalAjustado "+sql+"\n"+"\n");
				  
			    List<TotalConcepto> totalesConcepto = null;
				
				try{
					
					totalesConcepto = jdbcTemplate.query(sql, new RowMapper(){
						
					public TotalConcepto mapRow(ResultSet rs, int idx) throws SQLException {
						
						TotalConcepto totalConcepto = new TotalConcepto();										
				
						totalConcepto.setIngresoegreso(rs.getString("ingeg"));
						totalConcepto.setTotal( rs.getDouble( "total" ) );
						totalConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
						totalConcepto.setDescripcion(rs.getString("descripcion"));
						totalConcepto.setCve_concepto(rs.getInt("cve_concepto"));
						totalConcepto.setDesc_larga(rs.getString("desc_larga"));
						totalConcepto.setRow_concepto(rs.getInt("row_concepto"));
						
						return totalConcepto;
						
					}});
				
					}catch(Exception e){
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForConcepto" + ":," + sql + ":, M:");
					}		
				
				return totalesConcepto;
	}	
	public List<TotalConcepto> funSqlGetTotalesXMesForConceptoRealAjustado(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin,String paramOrigen) {

		String sql = "";
		StringBuffer sb = new StringBuffer();	    
		
		sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append( "ingeg,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append( "cve_concepto,");
	    sb.append("\n"); sb.append( "desc_larga,");
	    sb.append("\n"); sb.append("importe as total "); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ", row_concepto");
	    sb.append("\n"); sb.append( "From depura_mensual_comparativo_realajust");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "and mes between DATENAME(month, '" + paramFecIni + "') and DATENAME(month, '" + paramFecFin + "')");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "AND identificador = '" + paramOrigen + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO, ingeg,id_tipo_concepto, descripcion,cve_concepto, desc_larga,row_concepto,NATURALEZA,importe,identificador");
	    sb.append("\n"); sb.append( "ORDER BY 2");
			    
			    sql=sb.toString();
			    System.out.println("funSqlGetTotalesXMesForConceptoRealAjustado "+sql+"\n"+"\n");
				  
			    List<TotalConcepto> totalesConcepto = null;
				
				try{
					
					totalesConcepto = jdbcTemplate.query(sql, new RowMapper(){
						
					public TotalConcepto mapRow(ResultSet rs, int idx) throws SQLException {
						
						TotalConcepto totalConcepto = new TotalConcepto();										
				
						totalConcepto.setIngresoegreso(rs.getString("ingeg"));
						totalConcepto.setTotal( rs.getDouble( "total" ) );
						totalConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
						totalConcepto.setDescripcion(rs.getString("descripcion"));
						totalConcepto.setCve_concepto(rs.getInt("cve_concepto"));
						totalConcepto.setDesc_larga(rs.getString("desc_larga"));
						totalConcepto.setRow_concepto(rs.getInt("row_concepto"));
						
						return totalConcepto;
						
					}});
				
					}catch(Exception e){
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForConcepto" + ":," + sql + ":, M:");
					}		
				
				return totalesConcepto;
	}	
	
	public List<TotalConcepto> funSqlGetTotalesXMesForConceptoRealAjustadoExcel(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin) {

		String sql = "";
		StringBuffer sb = new StringBuffer();	    
		
		sb.append("\n"); sb.append("SELECT USUARIO, ingeg, id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion, cve_concepto, desc_larga,mes,identificador,");
	    sb.append("\n"); sb.append("importe as total  , row_concepto");
	    sb.append("\n"); sb.append( "From depura_mensual_comparativo_realajust");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	 //   sb.append("\n"); sb.append( "and mes between DATENAME(month, '" + paramFecIni + "') and DATENAME(month, '" + paramFecFin + "')");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	  //  sb.append("\n"); sb.append( "AND identificador = '" + paramOrigen + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO, ingeg,id_tipo_concepto, descripcion,cve_concepto, desc_larga,row_concepto,NATURALEZA,importe,identificador,mes");
	    sb.append("\n"); sb.append( "ORDER BY row_concepto,mes");
			    
			    sql=sb.toString();
			    System.out.println("funSqlGetTotalesXMesForConceptoRealAjustadoExcel "+sql+"\n"+"\n");
				  
			    List<TotalConcepto> totalesConcepto = null;
				
				try{
					
					totalesConcepto = jdbcTemplate.query(sql, new RowMapper(){
						
					public TotalConcepto mapRow(ResultSet rs, int idx) throws SQLException {
						
						TotalConcepto totalConcepto = new TotalConcepto();										
				
						totalConcepto.setIngresoegreso(rs.getString("ingeg"));
						totalConcepto.setTotal( rs.getDouble( "total" ) );
						totalConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
						totalConcepto.setDescripcion(rs.getString("descripcion"));
						totalConcepto.setCve_concepto(rs.getInt("cve_concepto"));
						totalConcepto.setDesc_larga(rs.getString("desc_larga"));
						totalConcepto.setRow_concepto(rs.getInt("row_concepto"));
						totalConcepto.setMes(rs.getString("mes"));
						totalConcepto.setIdentificador(rs.getString("identificador"));
						return totalConcepto;
						
					}});
				
					}catch(Exception e){
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForConcepto" + ":," + sql + ":, M:");
					}		
				
				return totalesConcepto;
	}	
	
	public List<TotalConcepto> funSqlGetTotalesXMesForConceptoOARExcel(
			String paramIdDivisa, int paramUsuario, String paramFecIni,
			String paramFecFin) {

		String sql = "";
		StringBuffer sb = new StringBuffer();	    
		
		sb.append("\n"); sb.append("SELECT USUARIO, ingeg, id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion, cve_concepto, desc_larga,mes,identificador,");
	    sb.append("\n"); sb.append("importe as total  , row_concepto");
	    sb.append("\n"); sb.append( "From depura_original_ajustado_real");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	 //   sb.append("\n"); sb.append( "and mes between DATENAME(month, '" + paramFecIni + "') and DATENAME(month, '" + paramFecFin + "')");
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	  //  sb.append("\n"); sb.append( "AND identificador = '" + paramOrigen + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO, ingeg,id_tipo_concepto, descripcion,cve_concepto, desc_larga,row_concepto,NATURALEZA,importe,identificador,mes");
	    sb.append("\n"); sb.append( "ORDER BY row_concepto,mes");
			    
			    sql=sb.toString();
			    System.out.println("funSqlGetTotalesXMesForConceptoRealAjustadoExcel "+sql+"\n"+"\n");
				  
			    List<TotalConcepto> totalesConcepto = null;
				
				try{
					
					totalesConcepto = jdbcTemplate.query(sql, new RowMapper(){
						
					public TotalConcepto mapRow(ResultSet rs, int idx) throws SQLException {
						
						TotalConcepto totalConcepto = new TotalConcepto();										
				
						totalConcepto.setIngresoegreso(rs.getString("ingeg"));
						totalConcepto.setTotal( rs.getDouble( "total" ) );
						totalConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
						totalConcepto.setDescripcion(rs.getString("descripcion"));
						totalConcepto.setCve_concepto(rs.getInt("cve_concepto"));
						totalConcepto.setDesc_larga(rs.getString("desc_larga"));
						totalConcepto.setRow_concepto(rs.getInt("row_concepto"));
						totalConcepto.setMes(rs.getString("mes"));
						totalConcepto.setIdentificador(rs.getString("identificador"));
						return totalConcepto;
						
					}});
				
					}catch(Exception e){
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForConcepto" + ":," + sql + ":, M:");
					}		
				
				return totalesConcepto;
	}	
	
	
	/**
	 * graficarFlujoEfectivo DAO.
	 */
	public List<TotalConcepto> graficarFlujoEfectivo(int noEmpresa, String sFecIni, String sFecFin)
	{
		List<TotalConcepto> lTotalConcepto;
		StringBuffer sb = new StringBuffer();
		
		sb.append("select sum(importe) as total, convert(varchar(10), fec_valor, 103) as fec_valor, id_tipo_movto\n");
		sb.append("from movimiento\n");
		sb.append("where id_tipo_operacion in (3115, 3154, 3200)\n");
		sb.append("and no_empresa = " + noEmpresa + "\n");
		sb.append("and fec_valor between convert(date, '" + sFecIni +"', 103) and convert(date, '" +sFecFin +"', 103)\n");
		sb.append("group by fec_valor, id_tipo_movto\n");
		sb.append("UNION ALL\n");
		sb.append("select sum(importe) as total, convert(varchar(10), fec_valor, 103) as fec_valor, id_tipo_movto\n");
		sb.append("from hist_movimiento\n");
		sb.append("where id_tipo_operacion in (3115, 3154, 3200)\n");
		sb.append("and no_empresa = " + noEmpresa + "\n");
		sb.append("and fec_valor between convert(date, '" + sFecIni +"', 103) and convert(date, '" +sFecFin +"', 103)\n");
		sb.append("group by fec_valor, id_tipo_movto\n");
		sb.append("order by fec_valor, id_tipo_movto\n");
		
		System.out.println("SQL_FlujoEfectivo: " + sb.toString()+"\n"+"\n");
		
		lTotalConcepto = jdbcTemplate.query(sb.toString(), new RowMapper(){
			public TotalConcepto mapRow(ResultSet rs, int idx) throws SQLException {
				TotalConcepto totalConcepto = new TotalConcepto();
				totalConcepto.setTotal( rs.getDouble( "total" ) );
				totalConcepto.setFec_valor( rs.getString("fec_valor"));
				totalConcepto.setIngresoegreso(rs.getString("id_tipo_movto"));
				
				return totalConcepto;
			}});
		
		return lTotalConcepto;
	}
	
	public List<TotalDiario> funSqlGetTotalesXPeriodo(
			String paramIdDivisa, int paramUsuario) {

		String sql = "";
		StringBuffer sb = new StringBuffer();
					               		
	    sb.append("\n"); sb.append("SELECT NO_USUARIO,");
	    sb.append("\n"); sb.append("dbo.func_TOTAL ('INGRESOEGRESO','NATURALEZA') as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append(",row_total_soa");
	    sb.append("\n"); sb.append(",row_total_sd");
	    sb.append("\n"); sb.append("From DIARIO_REAL_AJUSTADO");
	    sb.append("\n"); sb.append("Where no_usuario = " + paramUsuario);
	    sb.append("\n"); sb.append("AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append("GROUP BY NO_USUARIO,row_total_soa,row_total_sd,INGRESOEGRESO,NATURALEZA");
	    sb.append("\n"); sb.append("ORDER BY 1");

	    sql=sb.toString();
		System.out.println("funSqlGetTotalesXPeriodo "+sql+"\n"+"\n");
		List<TotalDiario> totalesDiario = null;
		
		try{
			
			totalesDiario = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalDiario mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalDiario totalDiario = new TotalDiario();
								
				totalDiario.setTotal( rs.getDouble( "total" ) );
				totalDiario.setRow_total_sd( rs.getInt("row_total_sd"));
				totalDiario.setRow_total_soa(rs.getInt("row_total_soa"));
				
				return totalDiario;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesDiario;
	}
	public List<TotalDiario> funSqlGetTotalesXPeriodoMesual(
			String paramIdDivisa, int paramUsuario,String paramFecIni,
			String paramFecFin) {

		String sql = "";
		StringBuffer sb = new StringBuffer();
					               		
	    sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append("SUM(importe) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append(",row_total_soa");
	    sb.append("\n"); sb.append(",row_total_sd");
	    sb.append("\n"); sb.append("From depura_mensual_real_ajustado");
	    sb.append("\n"); sb.append("Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append("AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append("AND  fec_valor between  '" + paramFecIni + "' and '" + paramFecFin + "'");
	    sb.append("\n"); sb.append("GROUP BY USUARIO,row_total_soa,row_total_sd,INGRESO_EGRESO,NATURALEZA");
	    sb.append("\n"); sb.append("ORDER BY 1");

	    sql=sb.toString();
		System.out.println("funSqlGetTotalesXPeriodo "+sql+"\n"+"\n");
		List<TotalDiario> totalesDiario = null;
		
		try{
			
			totalesDiario = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalDiario mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalDiario totalDiario = new TotalDiario();
								
				totalDiario.setTotal( rs.getDouble( "total" ) );
				totalDiario.setRow_total_sd( rs.getInt("row_total_sd"));
				totalDiario.setRow_total_soa(rs.getInt("row_total_soa"));
				
				return totalDiario;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesDiario;
	}
	public List<TotalDiario> funSqlGetTotalesXPeriodoSemanal(
			String paramIdDivisa, int paramUsuario,String paramFecIni,
			String paramFecFin) {

		String sql = "";
		StringBuffer sb = new StringBuffer();
					               		
	    sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append("SUM(importe) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append(",row_total_soa");
	    sb.append("\n"); sb.append(",row_total_sd");
	    sb.append("\n"); sb.append("From semanal_real_ajustado");
	    sb.append("\n"); sb.append("Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append("AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append("AND  fec_valor between  '" + paramFecIni + "' and '" + paramFecFin + "'");
	    sb.append("\n"); sb.append("GROUP BY USUARIO,row_total_soa,row_total_sd,INGRESO_EGRESO,NATURALEZA");
	    sb.append("\n"); sb.append("ORDER BY 1");

	    sql=sb.toString();
		System.out.println("funSqlGetTotalesXPeriodoSemanal "+sql+"\n"+"\n");
		List<TotalDiario> totalesDiario = null;
		
		try{
			
			totalesDiario = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalDiario mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalDiario totalDiario = new TotalDiario();
								
				totalDiario.setTotal( rs.getDouble( "total" ) );
				totalDiario.setRow_total_sd( rs.getInt("row_total_sd"));
				totalDiario.setRow_total_soa(rs.getInt("row_total_soa"));
				
				return totalDiario;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesDiario;
	}
	public List<TotalDiario> funSqlGetTotalesXPeriodoAjustado(
			String paramIdDivisa, int paramUsuario) {

		String sql = "";
		StringBuffer sb = new StringBuffer();
					               		
	    sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append("SUM(IMPORTE) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append(",row_total_soa");
	    sb.append("\n"); sb.append(",row_total_sd");
	    sb.append("\n"); sb.append("From TEMP_SALDO_CUADRANTE_NUEVO");
	    sb.append("\n"); sb.append("Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append("AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append("GROUP BY USUARIO,row_total_soa,row_total_sd,INGRESO_EGRESO,NATURALEZA");
	    sb.append("\n"); sb.append("ORDER BY 1");

	    sql=sb.toString();
		System.out.println("funSqlGetTotalesXPeriodoAjustado "+sql+"\n"+"\n");
		List<TotalDiario> totalesDiario = null;
		
		try{
			
			totalesDiario = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalDiario mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalDiario totalDiario = new TotalDiario();
								
				totalDiario.setTotal( rs.getDouble( "total" ) );
				totalDiario.setRow_total_sd( rs.getInt("row_total_sd"));
				totalDiario.setRow_total_soa(rs.getInt("row_total_soa"));
				
				return totalDiario;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesDiario;
	}
	
	public List<TotalDiario> funSqlGetTotalesXPeriodoOriginal(
			String paramIdDivisa, int paramUsuario) {

		String sql = "";
		StringBuffer sb = new StringBuffer();
					               		
	    sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append("SUM(IMPORTE) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append(",row_total_soa");
	    sb.append("\n"); sb.append(",row_total_sd");
	    sb.append("\n"); sb.append("From TEMP_SALDO_CUADRANTE_DIARIO");
	    sb.append("\n"); sb.append("Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append("AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append("GROUP BY USUARIO,row_total_soa,row_total_sd,INGRESO_EGRESO,NATURALEZA");
	    sb.append("\n"); sb.append("ORDER BY 1");

	    sql=sb.toString();
		System.out.println("funSqlGetTotalesXPeriodoOriginal "+sql+"\n"+"\n");
		List<TotalDiario> totalesDiario = null;
		
		try{
			
			totalesDiario = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalDiario mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalDiario totalDiario = new TotalDiario();
								
				totalDiario.setTotal( rs.getDouble( "total" ) );
				totalDiario.setRow_total_sd( rs.getInt("row_total_sd"));
				totalDiario.setRow_total_soa(rs.getInt("row_total_soa"));
				
				return totalDiario;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesDiario;
	}
	public List<TotalDiario> funSqlGetTotalesXPeriodo(
			String paramIdDivisa, int paramUsuario, int band) {

		String sql = "";
		StringBuffer sb = new StringBuffer();
		
		String tipoInfo = "";
		
		if(band == 3)
			tipoInfo = "Original";
				
		if(band == 5)
			tipoInfo = "Real";		
					               		
	    sb.append("\n"); sb.append("SELECT NO_USUARIO,");
	    sb.append("\n"); sb.append("dbo.func_TOTAL_ID_ORIGEN ('INGRESOEGRESO','NATURALEZA','" + tipoInfo + "') as TOTAL"); // Funcionc SQL TOTAL c/ID_ORIGEN
	    sb.append("\n"); sb.append(",row_total_soa");
	    sb.append("\n"); sb.append(",row_total_sd");
	    sb.append("\n"); sb.append("From DIARIO_REAL_AJUSTADO");
	    sb.append("\n"); sb.append("Where no_usuario = " + paramUsuario);
	    sb.append("\n"); sb.append("AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append("GROUP BY NO_USUARIO,row_total_soa,row_total_sd,INGRESOEGRESO,NATURALEZA");
	    sb.append("\n"); sb.append("ORDER BY 1");

	    sql=sb.toString();
		System.out.println("funSqlGetTotalesXPeriodo "+sql+"\n"+"\n");
		List<TotalDiario> totalesDiario = null;
		
		try{
			
			totalesDiario = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalDiario mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalDiario totalDiario = new TotalDiario();
								
				totalDiario.setTotal( rs.getDouble( "total" ) );
				totalDiario.setRow_total_sd( rs.getInt("row_total_sd"));
				totalDiario.setRow_total_soa(rs.getInt("row_total_soa"));
				
				return totalDiario;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesDiario;
	}
	
	public List<TotalDiario> funSqlGetTotalesXPeriodoOriginalAjustado(
			String paramIdDivisa, int paramUsuario, int band) {

		String sql = "";
		StringBuffer sb = new StringBuffer();
		
		String tipoInfo = "";
		
		if(band == 3)
			tipoInfo = "Original";
				
		if(band == 5)
			tipoInfo = "Real";		
					               		
	    sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append("sum(importe) as TOTAL"); // Funcionc SQL TOTAL c/ID_ORIGEN
	    sb.append("\n"); sb.append(",row_total_soa");
	    sb.append("\n"); sb.append(",row_total_sd");
	    sb.append("\n"); sb.append("From depura_mensual_comparativo_proyajust");
	    sb.append("\n"); sb.append("Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append("AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append("GROUP BY USUARIO,row_total_soa,row_total_sd,ingeg,NATURALEZA");
	    sb.append("\n"); sb.append("ORDER BY 1");

	    sql=sb.toString();
		System.out.println("funSqlGetTotalesXPeriodoOriginalAjustado "+sql+"\n"+"\n");
		List<TotalDiario> totalesDiario = null;
		
		try{
			
			totalesDiario = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalDiario mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalDiario totalDiario = new TotalDiario();
								
				totalDiario.setTotal( rs.getDouble( "total" ) );
				totalDiario.setRow_total_sd( rs.getInt("row_total_sd"));
				totalDiario.setRow_total_soa(rs.getInt("row_total_soa"));
				
				return totalDiario;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesDiario;
	}


	public List<TotalIngresoEgreso> funSqlGetTotalesXPeriodoIngresoEgreso(
			String paramIdDivisa, int paramUsuario) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
	    sb.append("\n"); sb.append( "SELECT USUARIO,INGRESO_EGRESO,");
	    sb.append("\n"); sb.append("SUM (IMPORTE) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ", row_ingresoegreso");
	    sb.append("\n"); sb.append( "From SEMANAL_REAL_AJUSTADO");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,INGRESO_EGRESO,row_ingresoegreso,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 2 desc");
    		
	    sql=sb.toString();
		System.out.println("funSqlGetTotalesXPeriodoIngresoEgreso "+sql+"\n"+"\n");
		List<TotalIngresoEgreso> totalesIngresoEgreso = null;
		
		try{
			
			totalesIngresoEgreso = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalIngresoEgreso mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalIngresoEgreso totalIngresoEgreso = new TotalIngresoEgreso();

				totalIngresoEgreso.setIngresoegreso(rs.getString("ingreso_egreso"));
				totalIngresoEgreso.setTotal( rs.getDouble( "total" ) );
				totalIngresoEgreso.setRow_ingresoegreso(rs.getInt("row_ingresoegreso"));
								
				return totalIngresoEgreso;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesIngresoEgreso;
		
	}
	public List<TotalIngresoEgreso> funSqlGetTotalesXPeriodoIngresoEgresoMensual(
			String paramIdDivisa, int paramUsuario,String paramFecIni,
			String paramFecFin) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
	    sb.append("\n"); sb.append( "SELECT USUARIO,INGRESO_EGRESO,");
	    sb.append("\n"); sb.append("SUM (IMPORTE) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ", row_ingresoegreso");
	    sb.append("\n"); sb.append( "From depura_mensual_real_ajustado");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'"); 
	    sb.append("\n"); sb.append("AND    fec_valor between  '" + paramFecIni + "' and '" + paramFecFin + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,INGRESO_EGRESO,row_ingresoegreso,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 2 desc");
    		
	    sql=sb.toString();
		System.out.println("funSqlGetTotalesXPeriodoIngresoEgreso "+sql+"\n"+"\n");
		List<TotalIngresoEgreso> totalesIngresoEgreso = null;
		
		try{
			
			totalesIngresoEgreso = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalIngresoEgreso mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalIngresoEgreso totalIngresoEgreso = new TotalIngresoEgreso();

				totalIngresoEgreso.setIngresoegreso(rs.getString("ingreso_egreso"));
				totalIngresoEgreso.setTotal( rs.getDouble( "total" ) );
				totalIngresoEgreso.setRow_ingresoegreso(rs.getInt("row_ingresoegreso"));
								
				return totalIngresoEgreso;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesIngresoEgreso;
		
	}
	public List<TotalIngresoEgreso> funSqlGetTotalesXPeriodoIngresoEgresoSemanal(
			String paramIdDivisa, int paramUsuario,String paramFecIni,
			String paramFecFin) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
	    sb.append("\n"); sb.append( "SELECT USUARIO,INGRESO_EGRESO,");
	    sb.append("\n"); sb.append("SUM (IMPORTE) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ", row_ingresoegreso");
	    sb.append("\n"); sb.append( "From SEMANAL_REAL_AJUSTADO");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append("AND    fec_valor between  '" + paramFecIni + "' and '" + paramFecFin + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,INGRESO_EGRESO,row_ingresoegreso,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 2 desc");
    		
	    sql=sb.toString();
		System.out.println("funSqlGetTotalesXPeriodoIngresoEgresoSemanal "+sql+"\n"+"\n");
		List<TotalIngresoEgreso> totalesIngresoEgreso = null;
		
		try{
			
			totalesIngresoEgreso = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalIngresoEgreso mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalIngresoEgreso totalIngresoEgreso = new TotalIngresoEgreso();

				totalIngresoEgreso.setIngresoegreso(rs.getString("ingreso_egreso"));
				totalIngresoEgreso.setTotal( rs.getDouble( "total" ) );
				totalIngresoEgreso.setRow_ingresoegreso(rs.getInt("row_ingresoegreso"));
								
				return totalIngresoEgreso;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesIngresoEgreso;
		
	}
	public List<TotalIngresoEgreso> funSqlGetTotalesXPeriodoIngresoEgresoAjustado(
			String paramIdDivisa, int paramUsuario) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
	    sb.append("\n"); sb.append( "SELECT USUARIO,INGRESO_EGRESO,");
	    sb.append("\n"); sb.append("SUM (IMPORTE) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ", row_ingresoegreso");
	    sb.append("\n"); sb.append( "From temp_saldo_cuadrante_nuevo");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,INGRESO_EGRESO,row_ingresoegreso,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 2 desc");
    		
	    sql=sb.toString();
		System.out.println("funSqlGetTotalesXPeriodoIngresoEgresoAjustado "+sql+"\n"+"\n");
		List<TotalIngresoEgreso> totalesIngresoEgreso = null;
		
		try{
			
			totalesIngresoEgreso = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalIngresoEgreso mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalIngresoEgreso totalIngresoEgreso = new TotalIngresoEgreso();

				totalIngresoEgreso.setIngresoegreso(rs.getString("ingreso_egreso"));
				totalIngresoEgreso.setTotal( rs.getDouble( "total" ) );
				totalIngresoEgreso.setRow_ingresoegreso(rs.getInt("row_ingresoegreso"));
								
				return totalIngresoEgreso;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesIngresoEgreso;
		
	}
	
	public List<TotalIngresoEgreso> funSqlGetTotalesXPeriodoIngresoEgresoOriginal(
			String paramIdDivisa, int paramUsuario) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
	    sb.append("\n"); sb.append( "SELECT USUARIO,INGRESO_EGRESO,");
	    sb.append("\n"); sb.append("SUM (IMPORTE) as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ", row_ingresoegreso");
	    sb.append("\n"); sb.append( "From temp_saldo_cuadrante_diario");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,INGRESO_EGRESO,row_ingresoegreso,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 2 desc");
    		
	    sql=sb.toString();
		System.out.println("funSqlGetTotalesXPeriodoIngresoEgresoOriginal "+sql+"\n"+"\n");
		List<TotalIngresoEgreso> totalesIngresoEgreso = null;
		
		try{
			
			totalesIngresoEgreso = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalIngresoEgreso mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalIngresoEgreso totalIngresoEgreso = new TotalIngresoEgreso();

				totalIngresoEgreso.setIngresoegreso(rs.getString("ingreso_egreso"));
				totalIngresoEgreso.setTotal( rs.getDouble( "total" ) );
				totalIngresoEgreso.setRow_ingresoegreso(rs.getInt("row_ingresoegreso"));
								
				return totalIngresoEgreso;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesIngresoEgreso;
		
	}
	
	public List<TotalIngresoEgreso> funSqlGetTotalesXPeriodoIngresoEgreso(
			String paramIdDivisa, int paramUsuario, int band) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
		
		String tipoInfo = "";
		
		if(band == 3)
			tipoInfo = "Original";
				
		if(band == 5)
			tipoInfo = "Real";		
	    
	    sb.append("\n"); sb.append( "SELECT NO_USUARIO,INGRESOEGRESO,");
	    sb.append("\n"); sb.append("dbo.func_TOTAL_ID_ORIGEN ('INGRESOEGRESO','NATURALEZA','" + tipoInfo + "') as TOTAL"); // Funcion SQL TOTAL c/ID_ORIGEN
	    sb.append("\n"); sb.append( ", row_ingresoegreso");
	    sb.append("\n"); sb.append( "From DIARIO_REAL_AJUSTADO");
	    sb.append("\n"); sb.append( "Where no_usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "GROUP BY NO_USUARIO,INGRESOEGRESO,row_ingresoegreso,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 2 desc");
    		
	    sql=sb.toString();
		System.out.println("funSqlGetTotalesXPeriodoIngresoEgreso "+sql+"\n"+"\n");
		List<TotalIngresoEgreso> totalesIngresoEgreso = null;
		
		try{
			
			totalesIngresoEgreso = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalIngresoEgreso mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalIngresoEgreso totalIngresoEgreso = new TotalIngresoEgreso();

				totalIngresoEgreso.setIngresoegreso(rs.getString("ingresoegreso"));
				totalIngresoEgreso.setTotal( rs.getDouble( "total" ) );
				totalIngresoEgreso.setRow_ingresoegreso(rs.getInt("row_ingresoegreso"));
								
				return totalIngresoEgreso;				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesIngresoEgreso;
		
	}
	public List<TotalIngresoEgreso> funSqlGetTotalesXPeriodoIngresoEgresoOriginalAjustado(
			String paramIdDivisa, int paramUsuario, int band) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
		
		String tipoInfo = "";
		
		if(band == 3)
			tipoInfo = "Original";
				
		if(band == 5)
			tipoInfo = "Real";		
	    
	    sb.append("\n"); sb.append( "SELECT USUARIO,ingeg,");
	    sb.append("\n"); sb.append("sum(importe) as TOTAL"); // Funcion SQL TOTAL c/ID_ORIGEN
	    sb.append("\n"); sb.append( ", row_ingresoegreso");
	    sb.append("\n"); sb.append( "From depura_mensual_comparativo_proyajust");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,ingeg,row_ingresoegreso,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 2 desc");
    		
	    sql=sb.toString();
		System.out.println("funSqlGetTotalesXPeriodoIngresoEgresoOriginalAjustado "+sql+"\n"+"\n");
		List<TotalIngresoEgreso> totalesIngresoEgreso = null;
		
		try{
			
			totalesIngresoEgreso = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalIngresoEgreso mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalIngresoEgreso totalIngresoEgreso = new TotalIngresoEgreso();

				totalIngresoEgreso.setIngresoegreso(rs.getString("ingeg"));
				totalIngresoEgreso.setTotal( rs.getDouble( "total" ) );
				totalIngresoEgreso.setRow_ingresoegreso(rs.getInt("row_ingresoegreso"));
								
				return totalIngresoEgreso;				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:, C:, M:");
			}		
		
		return totalesIngresoEgreso;
		
	}
	
	public List<TotalTipoConcepto> funSqlGetTotalesXPeriodoForTypeConcepto(
			String paramIdDivisa, int paramUsuario) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
		sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append( "INGRESO_EGRESO,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append("SUM(importe)as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ",row_tipo_concepto");
	    sb.append("\n"); sb.append( "From semanal_REAL_AJUSTADO");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,INGRESO_EGRESO, id_tipo_concepto, descripcion,row_tipo_concepto,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 1, 5");
	    
	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXPeriodoForTypeConcepto "+sql+"\n"+"\n");
	    List<TotalTipoConcepto> totalesTipoConcepto = null;
		
		try{
			
			totalesTipoConcepto = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalTipoConcepto mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalTipoConcepto totalTipoConcepto = new TotalTipoConcepto();
								
				totalTipoConcepto.setIngresoegreso(rs.getString("ingreso_egreso"));
				totalTipoConcepto.setTotal( rs.getDouble( "total" ) );
				totalTipoConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
				totalTipoConcepto.setDescripcion(rs.getString("descripcion"));
				totalTipoConcepto.setRow_tipo_concepto(rs.getInt("row_tipo_concepto"));				
								
				return totalTipoConcepto;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForTypeConcepto" + ":," + sql + ":, M:");
			}		
		
		return totalesTipoConcepto;
	}
	public List<TotalTipoConcepto> funSqlGetTotalesXPeriodoForTypeConceptoMensual(
			String paramIdDivisa, int paramUsuario,String paramFecIni,
			String paramFecFin) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
		sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append( "INGRESO_EGRESO,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append("SUM(importe)as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ",row_tipo_concepto");
	    sb.append("\n"); sb.append( "From depura_mensual_real_ajustado");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'"); 
	    sb.append("\n"); sb.append("AND   fec_valor between  '" + paramFecIni + "' and '" + paramFecFin + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,INGRESO_EGRESO, id_tipo_concepto, descripcion,row_tipo_concepto,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 1, 5");
	    
	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXPeriodoForTypeConcepto "+sql+"\n"+"\n");
	    List<TotalTipoConcepto> totalesTipoConcepto = null;
		
		try{
			
			totalesTipoConcepto = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalTipoConcepto mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalTipoConcepto totalTipoConcepto = new TotalTipoConcepto();
								
				totalTipoConcepto.setIngresoegreso(rs.getString("ingreso_egreso"));
				totalTipoConcepto.setTotal( rs.getDouble( "total" ) );
				totalTipoConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
				totalTipoConcepto.setDescripcion(rs.getString("descripcion"));
				totalTipoConcepto.setRow_tipo_concepto(rs.getInt("row_tipo_concepto"));				
								
				return totalTipoConcepto;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForTypeConcepto" + ":," + sql + ":, M:");
			}		
		
		return totalesTipoConcepto;
	}
	public List<TotalTipoConcepto> funSqlGetTotalesXPeriodoForTypeConceptoSemanal(
			String paramIdDivisa, int paramUsuario,String paramFecIni,
			String paramFecFin) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
		sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append( "INGRESO_EGRESO,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append("SUM(importe)as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ",row_tipo_concepto");
	    sb.append("\n"); sb.append( "From semanal_REAL_AJUSTADO");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'"); 
	    sb.append("\n"); sb.append("AND   fec_valor between  '" + paramFecIni + "' and '" + paramFecFin + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,INGRESO_EGRESO, id_tipo_concepto, descripcion,row_tipo_concepto,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 1, 5");
	    
	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXPeriodoForTypeConceptoSemanal "+sql+"\n"+"\n");
	    List<TotalTipoConcepto> totalesTipoConcepto = null;
		
		try{
			
			totalesTipoConcepto = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalTipoConcepto mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalTipoConcepto totalTipoConcepto = new TotalTipoConcepto();
								
				totalTipoConcepto.setIngresoegreso(rs.getString("ingreso_egreso"));
				totalTipoConcepto.setTotal( rs.getDouble( "total" ) );
				totalTipoConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
				totalTipoConcepto.setDescripcion(rs.getString("descripcion"));
				totalTipoConcepto.setRow_tipo_concepto(rs.getInt("row_tipo_concepto"));				
								
				return totalTipoConcepto;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForTypeConcepto" + ":," + sql + ":, M:");
			}		
		
		return totalesTipoConcepto;
	}
	
	public List<TotalTipoConcepto> funSqlGetTotalesXPeriodoForTypeConceptoAjustado(
			String paramIdDivisa, int paramUsuario) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
		sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append( "INGRESO_EGRESO,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append("SUM(importe)as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ",row_tipo_concepto");
	    sb.append("\n"); sb.append( "From temp_saldo_cuadrante_nuevo");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,INGRESO_EGRESO, id_tipo_concepto, descripcion,row_tipo_concepto,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 1, 5");
	    
	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXPeriodoForTypeConceptoAjustado "+sql+"\n"+"\n");
	    List<TotalTipoConcepto> totalesTipoConcepto = null;
		
		try{
			
			totalesTipoConcepto = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalTipoConcepto mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalTipoConcepto totalTipoConcepto = new TotalTipoConcepto();
								
				totalTipoConcepto.setIngresoegreso(rs.getString("ingreso_egreso"));
				totalTipoConcepto.setTotal( rs.getDouble( "total" ) );
				totalTipoConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
				totalTipoConcepto.setDescripcion(rs.getString("descripcion"));
				totalTipoConcepto.setRow_tipo_concepto(rs.getInt("row_tipo_concepto"));				
								
				return totalTipoConcepto;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForTypeConcepto" + ":," + sql + ":, M:");
			}		
		
		return totalesTipoConcepto;
	}
	
	public List<TotalTipoConcepto> funSqlGetTotalesXPeriodoForTypeConceptoOriginal(
			String paramIdDivisa, int paramUsuario) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();					               		
	    
		sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append( "INGRESO_EGRESO,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append("SUM(importe)as TOTAL"); // Funcion SQL TOTAL
	    sb.append("\n"); sb.append( ",row_tipo_concepto");
	    sb.append("\n"); sb.append( "From temp_saldo_cuadrante_diario");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,INGRESO_EGRESO, id_tipo_concepto, descripcion,row_tipo_concepto,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 1, 5");
	    
	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXPeriodoForTypeConceptoOriginal "+sql+"\n"+"\n");
	    List<TotalTipoConcepto> totalesTipoConcepto = null;
		
		try{
			
			totalesTipoConcepto = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalTipoConcepto mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalTipoConcepto totalTipoConcepto = new TotalTipoConcepto();
								
				totalTipoConcepto.setIngresoegreso(rs.getString("ingreso_egreso"));
				totalTipoConcepto.setTotal( rs.getDouble( "total" ) );
				totalTipoConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
				totalTipoConcepto.setDescripcion(rs.getString("descripcion"));
				totalTipoConcepto.setRow_tipo_concepto(rs.getInt("row_tipo_concepto"));				
								
				return totalTipoConcepto;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForTypeConcepto" + ":," + sql + ":, M:");
			}		
		
		return totalesTipoConcepto;
	}
	
	public List<TotalTipoConcepto> funSqlGetTotalesXPeriodoForTypeConcepto(
			String paramIdDivisa, int paramUsuario, int band) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();
		
		String tipoInfo = "";
		
		if(band == 3)
			tipoInfo = "Original";
				
		if(band == 5)
			tipoInfo = "Real";		
	    
		sb.append("\n"); sb.append("SELECT NO_USUARIO,");
	    sb.append("\n"); sb.append( "INGRESOEGRESO,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append("dbo.func_TOTAL_ID_ORIGEN ('INGRESOEGRESO','NATURALEZA','" + tipoInfo + "') as TOTAL"); // Funcion SQL TOTAL c/ID_ORIGEN
		sb.append("\n"); sb.append( ",row_tipo_concepto");
	    sb.append("\n"); sb.append( "From DIARIO_REAL_AJUSTADO");
	    sb.append("\n"); sb.append( "Where no_usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "GROUP BY NO_USUARIO,INGRESOEGRESO, id_tipo_concepto, descripcion,row_tipo_concepto,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 1, 5");
	    
	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXPeriodoForTypeConcepto "+sql+"\n"+"\n");
	    List<TotalTipoConcepto> totalesTipoConcepto = null;
		
		try{
			
			totalesTipoConcepto = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalTipoConcepto mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalTipoConcepto totalTipoConcepto = new TotalTipoConcepto();
								
				totalTipoConcepto.setIngresoegreso(rs.getString("ingresoegreso"));
				totalTipoConcepto.setTotal( rs.getDouble( "total" ) );
				totalTipoConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
				totalTipoConcepto.setDescripcion(rs.getString("descripcion"));
				totalTipoConcepto.setRow_tipo_concepto(rs.getInt("row_tipo_concepto"));				
								
				return totalTipoConcepto;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForTypeConcepto" + ":," + sql + ":, M:");
			}		
		
		return totalesTipoConcepto;
	}
	public List<TotalTipoConcepto> funSqlGetTotalesXPeriodoForTypeConceptoOriginalAjustado(
			String paramIdDivisa, int paramUsuario, int band) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();
		
		String tipoInfo = "";
		
		if(band == 3)
			tipoInfo = "Original";
				
		if(band == 5)
			tipoInfo = "Real";		
	    
		sb.append("\n"); sb.append("SELECT USUARIO,");
	    sb.append("\n"); sb.append( "ingeg,");
	    sb.append("\n"); sb.append( "id_tipo_concepto,");
	    sb.append("\n"); sb.append( "descripcion,");
	    sb.append("\n"); sb.append("sum(importe) as TOTAL"); // Funcion SQL TOTAL c/ID_ORIGEN
		sb.append("\n"); sb.append( ",row_tipo_concepto");
	    sb.append("\n"); sb.append( "From depura_mensual_comparativo_proyajust");
	    sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
	    sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append( "GROUP BY USUARIO,ingeg, id_tipo_concepto, descripcion,row_tipo_concepto,NATURALEZA");
	    sb.append("\n"); sb.append( "ORDER BY 1, 5");
	    
	    sql=sb.toString();
	    System.out.println("funSqlGetTotalesXPeriodoForTypeConceptoOriginalAjustado "+sql+"\n"+"\n");
	    List<TotalTipoConcepto> totalesTipoConcepto = null;
		
		try{
			
			totalesTipoConcepto = jdbcTemplate.query(sql, new RowMapper(){
				
			public TotalTipoConcepto mapRow(ResultSet rs, int idx) throws SQLException {
				
				TotalTipoConcepto totalTipoConcepto = new TotalTipoConcepto();
								
				totalTipoConcepto.setIngresoegreso(rs.getString("ingeg"));
				totalTipoConcepto.setTotal( rs.getDouble( "total" ) );
				totalTipoConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
				totalTipoConcepto.setDescripcion(rs.getString("descripcion"));
				totalTipoConcepto.setRow_tipo_concepto(rs.getInt("row_tipo_concepto"));				
								
				return totalTipoConcepto;
				
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForTypeConcepto" + ":," + sql + ":, M:");
			}		
		
		return totalesTipoConcepto;
	}

	public List<TotalConcepto> funSqlGetTotalesXPeriodoForConcepto(
			String paramIdDivisa, int paramUsuario) {
		String sql = "";
		StringBuffer sb = new StringBuffer();	    
		
		sb.append("\n"); sb.append("SELECT USUARIO,");
		sb.append("\n"); sb.append( "id_tipo_concepto,");
		sb.append("\n"); sb.append( "descripcion,");
		sb.append("\n"); sb.append( "cve_concepto,");
		sb.append("\n"); sb.append( "desc_larga,");
	    sb.append("\n"); sb.append("sum(importe) as TOTAL"); // Funcion SQL TOTAL
		sb.append("\n"); sb.append( ", row_concepto");
		sb.append("\n"); sb.append( "From semanal_REAL_AJUSTADO");
		sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
		sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
		sb.append("\n"); sb.append( "GROUP BY USUARIO, id_tipo_concepto, descripcion,cve_concepto, desc_larga,row_concepto,INGRESO_EGRESO,NATURALEZA");
		sb.append("\n"); sb.append( "ORDER BY 2");
			    
			    sql=sb.toString();
			    System.out.println("funSqlGetTotalesXPeriodoForConcepto "+sql+"\n"+"\n");
			    List<TotalConcepto> totalesConcepto = null;
				
				try{
					
					totalesConcepto = jdbcTemplate.query(sql, new RowMapper(){
						
					public TotalConcepto mapRow(ResultSet rs, int idx) throws SQLException {
						
						TotalConcepto totalConcepto = new TotalConcepto();										
		
						totalConcepto.setTotal( rs.getDouble( "total" ) );
						totalConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
						totalConcepto.setDescripcion(rs.getString("descripcion"));
						totalConcepto.setCve_concepto(rs.getInt("cve_concepto"));
						totalConcepto.setDesc_larga(rs.getString("desc_larga"));
						totalConcepto.setRow_concepto(rs.getInt("row_concepto"));
						
						return totalConcepto;
						
					}});
				
					}catch(Exception e){
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForConcepto" + ":," + sql + ":, M:");
					}		
				
				return totalesConcepto;
	}	
	
	public List<TotalConcepto> funSqlGetTotalesXPeriodoForConceptoMensual(
			String paramIdDivisa, int paramUsuario,String paramFecIni,
			String paramFecFin) {
		String sql = "";
		StringBuffer sb = new StringBuffer();	    
		
		sb.append("\n"); sb.append("SELECT USUARIO,");
		sb.append("\n"); sb.append( "id_tipo_concepto,");
		sb.append("\n"); sb.append( "descripcion,");
		sb.append("\n"); sb.append( "cve_concepto,");
		sb.append("\n"); sb.append( "desc_larga,");
	    sb.append("\n"); sb.append("sum(importe) as TOTAL"); // Funcion SQL TOTAL
		sb.append("\n"); sb.append( ", row_concepto");
		sb.append("\n"); sb.append( "From depura_mensual_real_ajustado");
		sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
		sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append("AND   fec_valor between  '" + paramFecIni + "' and '" + paramFecFin + "'");
		sb.append("\n"); sb.append( "GROUP BY USUARIO, id_tipo_concepto, descripcion,cve_concepto, desc_larga,row_concepto,INGRESO_EGRESO,NATURALEZA");
		sb.append("\n"); sb.append( "ORDER BY 2");
			    
			    sql=sb.toString();
			    System.out.println("funSqlGetTotalesXPeriodoForConcepto "+sql+"\n"+"\n");
			    List<TotalConcepto> totalesConcepto = null;
				
				try{
					
					totalesConcepto = jdbcTemplate.query(sql, new RowMapper(){
						
					public TotalConcepto mapRow(ResultSet rs, int idx) throws SQLException {
						
						TotalConcepto totalConcepto = new TotalConcepto();										
		
						totalConcepto.setTotal( rs.getDouble( "total" ) );
						totalConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
						totalConcepto.setDescripcion(rs.getString("descripcion"));
						totalConcepto.setCve_concepto(rs.getInt("cve_concepto"));
						totalConcepto.setDesc_larga(rs.getString("desc_larga"));
						totalConcepto.setRow_concepto(rs.getInt("row_concepto"));
						
						return totalConcepto;
						
					}});
				
					}catch(Exception e){
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForConcepto" + ":," + sql + ":, M:");
					}		
				
				return totalesConcepto;
	}	
	
	public List<TotalConcepto> funSqlGetTotalesXPeriodoForConceptosemanal(
			String paramIdDivisa, int paramUsuario,String paramFecIni,
			String paramFecFin) {
		String sql = "";
		StringBuffer sb = new StringBuffer();	    
		
		sb.append("\n"); sb.append("SELECT USUARIO,");
		sb.append("\n"); sb.append( "id_tipo_concepto,");
		sb.append("\n"); sb.append( "descripcion,");
		sb.append("\n"); sb.append( "cve_concepto,");
		sb.append("\n"); sb.append( "desc_larga,");
	    sb.append("\n"); sb.append("sum(importe) as TOTAL"); // Funcion SQL TOTAL
		sb.append("\n"); sb.append( ", row_concepto");
		sb.append("\n"); sb.append( "From semanal_REAL_AJUSTADO");
		sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
		sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
	    sb.append("\n"); sb.append("AND   fec_valor between  '" + paramFecIni + "' and '" + paramFecFin + "'");
		sb.append("\n"); sb.append( "GROUP BY USUARIO, id_tipo_concepto, descripcion,cve_concepto, desc_larga,row_concepto,INGRESO_EGRESO,NATURALEZA");
		sb.append("\n"); sb.append( "ORDER BY 2");
			    
			    sql=sb.toString();
			    System.out.println("funSqlGetTotalesXPeriodoForConceptosemanal "+sql+"\n"+"\n");
			    List<TotalConcepto> totalesConcepto = null;
				
				try{
					
					totalesConcepto = jdbcTemplate.query(sql, new RowMapper(){
						
					public TotalConcepto mapRow(ResultSet rs, int idx) throws SQLException {
						
						TotalConcepto totalConcepto = new TotalConcepto();										
		
						totalConcepto.setTotal( rs.getDouble( "total" ) );
						totalConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
						totalConcepto.setDescripcion(rs.getString("descripcion"));
						totalConcepto.setCve_concepto(rs.getInt("cve_concepto"));
						totalConcepto.setDesc_larga(rs.getString("desc_larga"));
						totalConcepto.setRow_concepto(rs.getInt("row_concepto"));
						
						return totalConcepto;
						
					}});
				
					}catch(Exception e){
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForConcepto" + ":," + sql + ":, M:");
					}		
				
				return totalesConcepto;
	}	
	public List<TotalConcepto> funSqlGetTotalesXPeriodoForConceptoAjustado(
			String paramIdDivisa, int paramUsuario) {
		String sql = "";
		StringBuffer sb = new StringBuffer();	    
		
		sb.append("\n"); sb.append("SELECT USUARIO,");
		sb.append("\n"); sb.append( "id_tipo_concepto,");
		sb.append("\n"); sb.append( "descripcion,");
		sb.append("\n"); sb.append( "cve_concepto,");
		sb.append("\n"); sb.append( "desc_larga,");
	    sb.append("\n"); sb.append("sum(importe) as TOTAL"); // Funcion SQL TOTAL
		sb.append("\n"); sb.append( ", row_concepto");
		sb.append("\n"); sb.append( "From temp_saldo_cuadrante_nuevo");
		sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
		sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
		sb.append("\n"); sb.append( "GROUP BY USUARIO, id_tipo_concepto, descripcion,cve_concepto, desc_larga,row_concepto,INGRESO_EGRESO,NATURALEZA");
		sb.append("\n"); sb.append( "ORDER BY 2");
			    
			    sql=sb.toString();
			    System.out.println("funSqlGetTotalesXPeriodoForConceptoAjustado "+sql+"\n"+"\n");
			    List<TotalConcepto> totalesConcepto = null;
				
				try{
					
					totalesConcepto = jdbcTemplate.query(sql, new RowMapper(){
						
					public TotalConcepto mapRow(ResultSet rs, int idx) throws SQLException {
						
						TotalConcepto totalConcepto = new TotalConcepto();										
		
						totalConcepto.setTotal( rs.getDouble( "total" ) );
						totalConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
						totalConcepto.setDescripcion(rs.getString("descripcion"));
						totalConcepto.setCve_concepto(rs.getInt("cve_concepto"));
						totalConcepto.setDesc_larga(rs.getString("desc_larga"));
						totalConcepto.setRow_concepto(rs.getInt("row_concepto"));
						
						return totalConcepto;
						
					}});
				
					}catch(Exception e){
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForConcepto" + ":," + sql + ":, M:");
					}		
				
				return totalesConcepto;
	}	
	public List<TotalConcepto> funSqlGetTotalesXPeriodoForConceptoOriginal(
			String paramIdDivisa, int paramUsuario) {
		String sql = "";
		StringBuffer sb = new StringBuffer();	    
		
		sb.append("\n"); sb.append("SELECT USUARIO,");
		sb.append("\n"); sb.append( "id_tipo_concepto,");
		sb.append("\n"); sb.append( "descripcion,");
		sb.append("\n"); sb.append( "cve_concepto,");
		sb.append("\n"); sb.append( "desc_larga,");
	    sb.append("\n"); sb.append("sum(importe) as TOTAL"); // Funcion SQL TOTAL
		sb.append("\n"); sb.append( ", row_concepto");
		sb.append("\n"); sb.append( "From temp_saldo_cuadrante_diario");
		sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
		sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
		sb.append("\n"); sb.append( "GROUP BY USUARIO, id_tipo_concepto, descripcion,cve_concepto, desc_larga,row_concepto,INGRESO_EGRESO,NATURALEZA");
		sb.append("\n"); sb.append( "ORDER BY 2");
			    
			    sql=sb.toString();
			    System.out.println("funSqlGetTotalesXPeriodoForConceptoOriginal "+sql+"\n"+"\n");
			    List<TotalConcepto> totalesConcepto = null;
				
				try{
					
					totalesConcepto = jdbcTemplate.query(sql, new RowMapper(){
						
					public TotalConcepto mapRow(ResultSet rs, int idx) throws SQLException {
						
						TotalConcepto totalConcepto = new TotalConcepto();										
		
						totalConcepto.setTotal( rs.getDouble( "total" ) );
						totalConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
						totalConcepto.setDescripcion(rs.getString("descripcion"));
						totalConcepto.setCve_concepto(rs.getInt("cve_concepto"));
						totalConcepto.setDesc_larga(rs.getString("desc_larga"));
						totalConcepto.setRow_concepto(rs.getInt("row_concepto"));
						
						return totalConcepto;
						
					}});
				
					}catch(Exception e){
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForConcepto" + ":," + sql + ":, M:");
					}		
				
				return totalesConcepto;
	}	
	
	
	public List<TotalConcepto> funSqlGetTotalesXPeriodoForConcepto(
			String paramIdDivisa, int paramUsuario, int band) {
		String sql = "";
		StringBuffer sb = new StringBuffer();
		
		String tipoInfo = "";
		
		if(band == 3)
			tipoInfo = "Original";
				
		if(band == 5)
			tipoInfo = "Real";
		
		sb.append("\n"); sb.append("SELECT NO_USUARIO,");
		sb.append("\n"); sb.append( "id_tipo_concepto,");
		sb.append("\n"); sb.append( "descripcion,");
		sb.append("\n"); sb.append( "cve_concepto,");
		sb.append("\n"); sb.append( "desc_larga,");
	    sb.append("\n"); sb.append("dbo.func_TOTAL_ID_ORIGEN ('INGRESOEGRESO','NATURALEZA','" + tipoInfo + "') as TOTAL"); // Funcion SQL TOTAL c/ID_ORIGEN
		sb.append("\n"); sb.append( ", row_concepto");
		sb.append("\n"); sb.append( "From DIARIO_REAL_AJUSTADO");
		sb.append("\n"); sb.append( "Where no_usuario = " + paramUsuario);
		sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
		sb.append("\n"); sb.append( "GROUP BY NO_USUARIO, id_tipo_concepto, descripcion,cve_concepto, desc_larga,row_concepto,INGRESOEGRESO,NATURALEZA");
		sb.append("\n"); sb.append( "ORDER BY 2");
			    
			    sql=sb.toString();
			    System.out.println("funSqlGetTotalesXPeriodoForConcepto "+sql+"\n"+"\n");
			    List<TotalConcepto> totalesConcepto = null;
				
				try{
					
					totalesConcepto = jdbcTemplate.query(sql, new RowMapper(){
						
					public TotalConcepto mapRow(ResultSet rs, int idx) throws SQLException {
						
						TotalConcepto totalConcepto = new TotalConcepto();										
		
						totalConcepto.setTotal( rs.getDouble( "total" ) );
						totalConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
						totalConcepto.setDescripcion(rs.getString("descripcion"));
						totalConcepto.setCve_concepto(rs.getInt("cve_concepto"));
						totalConcepto.setDesc_larga(rs.getString("desc_larga"));
						totalConcepto.setRow_concepto(rs.getInt("row_concepto"));
						
						return totalConcepto;
						
					}});
				
					}catch(Exception e){
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForConcepto" + ":," + sql + ":, M:");
					}		
				
				return totalesConcepto;
	}	
	
	public List<TotalConcepto> funSqlGetTotalesXPeriodoForConceptoOriginalAjustado(
			String paramIdDivisa, int paramUsuario, int band) {
		String sql = "";
		StringBuffer sb = new StringBuffer();
		
		String tipoInfo = "";
		
		if(band == 3)
			tipoInfo = "Original";
				
		if(band == 5)
			tipoInfo = "Real";
		
		sb.append("\n"); sb.append("SELECT USUARIO,");
		sb.append("\n"); sb.append( "id_tipo_concepto,");
		sb.append("\n"); sb.append( "descripcion,");
		sb.append("\n"); sb.append( "cve_concepto,");
		sb.append("\n"); sb.append( "desc_larga,");
	    sb.append("\n"); sb.append("SUM(importe) as TOTAL"); // Funcion SQL TOTAL c/ID_ORIGEN
		sb.append("\n"); sb.append( ", row_concepto");
		sb.append("\n"); sb.append( "From depura_mensual_comparativo_proyajust");
		sb.append("\n"); sb.append( "Where usuario = " + paramUsuario);
		sb.append("\n"); sb.append( "AND ID_DIVISA = '" + paramIdDivisa + "'");
		sb.append("\n"); sb.append( "GROUP BY USUARIO, id_tipo_concepto, descripcion,cve_concepto, desc_larga,row_concepto,ingeg,NATURALEZA");
		sb.append("\n"); sb.append( "ORDER BY 2");
			    
			    sql=sb.toString();
			    System.out.println("funSqlGetTotalesXPeriodoForConceptoOriginalAjustado "+sql+"\n"+"\n");
			    List<TotalConcepto> totalesConcepto = null;
				
				try{
					
					totalesConcepto = jdbcTemplate.query(sql, new RowMapper(){
						
					public TotalConcepto mapRow(ResultSet rs, int idx) throws SQLException {
						
						TotalConcepto totalConcepto = new TotalConcepto();										
		
						totalConcepto.setTotal( rs.getDouble( "total" ) );
						totalConcepto.setId_tipo_concepto(rs.getInt("id_tipo_concepto"));
						totalConcepto.setDescripcion(rs.getString("descripcion"));
						totalConcepto.setCve_concepto(rs.getInt("cve_concepto"));
						totalConcepto.setDesc_larga(rs.getString("desc_larga"));
						totalConcepto.setRow_concepto(rs.getInt("row_concepto"));
						
						return totalConcepto;
						
					}});
				
					}catch(Exception e){
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "funSqlGetTotalesForConcepto" + ":," + sql + ":, M:");
					}		
				
				return totalesConcepto;
	}	
	
	 /*************************************************************************************************************************************************
	 * ************************************************************************************************************************************************
	 * ************************************************************************************************************************************************
	 * ************************************************************************************************************************************************
	 INIT PREPARA INFORMACION DIARIO REAL AJUSTADO*/
	/*lo modifique semanal*/
	public int spFlujoDiarioRealAjustadoDao( ParamSpFlujoDto obj ) {
		
		int resp = -1;
		StringBuffer sb = new StringBuffer();

		try
		{		
			 
				System.out.println("semanal");
				System.out.println("FECHA "+obj.getFechaHoy());
				String fecha2=funciones.cambiarFecha(obj.getFechaHoy(),true);
		 		System.out.println("fecha despues de funcion "+fecha2);		
		 		sb.append("\n"); sb.append( "execute sp_flujo_semanal_real_ajustado " + obj.getNoUsuario() + " , '"+obj.getFechaInicial()+"','"+obj.getFechaFinal()+"',"+9+","+10+","+5+","+6+","+obj.getIdGrupo()+","+obj.getNoEmpresa()+",'"+fecha2+"','"+obj.getIdDivisa()+"'");
		 		System.out.println("almacenado "+sb.toString());
				resp = jdbcTemplate.update(sb.toString());
			     
			 
			
			
		}
		catch (Exception e) 
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
                    + "P:CashFlow, C:CashFlowDaoImplement, M:spFlujoDiarioRealAjustadoDao");
		}
		return resp;
		
	}
	public int spFlujoDiarioRealDiarioDao( ParamSpFlujoDto obj ) {
		
		int resp = -1;
		StringBuffer sb = new StringBuffer();

		try
		{		
			 
				System.out.println("Diario");
				System.out.println("FECHA "+obj.getFechaHoy());
				String fecha2=funciones.cambiarFecha(obj.getFechaHoy(),true);
		 		System.out.println("fecha despues de funcion "+fecha2);		
		 		sb.append(" execute sp_flujo_diario " + obj.getNoUsuario() +" , '"+ obj.getFechaInicial() + "' , '" + obj.getFechaFinal() + "' ,"+5+","+6+", "+obj.getIdGrupo()+" , "+obj.getNoEmpresa()+",'"+obj.getIdDivisa()+"'");
				System.out.println("almacenado "+sb.toString());
				resp = jdbcTemplate.update(sb.toString());
			     
			 
			
			
		}
		catch (Exception e) 
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
                    + "P:CashFlow, C:CashFlowDaoImplement, M:spFlujoDiarioRealAjustadoDao");
		}
		return resp;
		
	}
	public int spFlujoMesualRealAjustadoDao( ParamSpFlujoDto obj ) {
		
		int resp = -1;
		StringBuffer sb = new StringBuffer();

		try
		{		
			 
				System.out.println("Mensual 4 ");
				System.out.println("FECHA "+obj.getFechaHoy());
				String fecha2=funciones.cambiarFecha(obj.getFechaHoy(),true);
		 		System.out.println("fecha despues de funcion "+fecha2);		
		 		sb.append("\n"); sb.append( "execute sp_flujo_mensual_real_ajustado " + obj.getNoUsuario() + " , '"+obj.getFechaInicial()+"','"+obj.getFechaFinal()+"',"+9+","+10+","+5+","+6+","+obj.getIdGrupo()+","+obj.getNoEmpresa()+",'"+fecha2+"','"+obj.getIdDivisa()+"'");
		 		System.out.println("almacenado en mensual "+sb.toString());
				resp = jdbcTemplate.update(sb.toString());
			     
			 
			
			
		}
		catch (Exception e) 
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
                    + "P:CashFlow, C:CashFlowDaoImplement, M:spFlujoDiarioRealAjustadoDao");
		}
		return resp;
		
	}
	 /*************************************************************************************************************************************************
	 * ************************************************************************************************************************************************
	 * ************************************************************************************************************************************************
	 * ************************************************************************************************************************************************
	 END PREPARA INFORMACION DIARIO REAL AJUSTADO*/
	

	/*************************************************************************************************************************************************
	 * ************************************************************************************************************************************************
	 * ************************************************************************************************************************************************
	 * ************************************************************************************************************************************************
	 INIT PREPARA INFORMACION MENSUAL AJUSTADO*/
	public int spFlujoMensualAjustadoDao(ParamSpFlujoDto obj) {
	
		int resp;		
		StringBuffer sb = new StringBuffer();		
		//sb.append(" DELETE FROM tmp_empresa_flujo WHERE no_usuario = " + obj.getNoUsuario() + "\n");	
		//sb.append(" delete from mensual_anual where usuario= " + obj.getNoUsuario() + "\n");	
		//resp = jdbcTemplate.update(sb.toString());		
		//sb.delete( 0, sb.length() );
		//sb.append(" delete from depura_mensual_anual where usuario = " + obj.getNoUsuario() + "\n");	
		//sb.append(" DELETE FROM tmp_movimientos_flujo WHERE no_usuario = " + obj.getNoUsuario() + "\n");		
		//resp = jdbcTemplate.update(sb.toString());
		//sb.delete( 0, sb.length() );		
		//sb.append(" DELETE FROM diario_real_ajustado  WHERE no_usuario = " + obj.getNoUsuario() + "\n");
		//sb.append(" DELETE FROM diario_real_ajustado  WHERE no_usuario = " + obj.getNoUsuario() + "\n");		
		//resp = jdbcTemplate.update(sb.toString());
		//sb.delete( 0, sb.length() );
				
		/*if ( obj.getNoEmpresa() == 0 )
		{						
		  /*sb.append("\n"); sb.append( "INSERT INTO tmp_empresa_flujo");                                                               			
			sb.append("\n"); sb.append( "SELECT NO_EMPRESA ," + obj.getNoUsuario() );
			sb.append("\n"); sb.append( "FROM GRUPO_EMPRESA" ); 
			sb.append("\n"); sb.append( "WHERE ID_GRUPO_FLUJO IN(" );
			sb.append("\n"); sb.append( "                        SELECT id_grupo_flujo" );
			sb.append("\n"); sb.append( "                        From cat_super_grupo_flujo" );
			sb.append("\n"); sb.append( "                        WHERE ID_GRUPO_FLUJO IN(SELECT no_empresa" );
			sb.append("\n"); sb.append( "                                                From usuario_empresa" );
			sb.append("\n"); sb.append( "                                                Where no_usuario = " + obj.getNoUsuario() );
			sb.append("\n"); sb.append( "                        )" );
			sb.append("\n"); sb.append( "AND id_super_grupo IN(SELECT ID_SUPER_GRUPO" );
			sb.append("\n"); sb.append( "                      From CAT_SUPER_GRUPO" );
			sb.append("\n"); sb.append( "                      WHERE ID_SUPER_GRUPO IN( " + obj.getIdGrupo() + ")" );
			sb.append("\n"); sb.append( "                      )" );
			sb.append("\n"); sb.append( ");" );
			sb.append("\n"); sb.append( "INSERT INTO mensual_anual");  
			sb.append("\n"); sb.append( "SELECT d.desc_larga, c.naturaleza, c.id_tipo_concepto,e.descripcion,");  
			sb.append("\n"); sb.append( "a.importe as importe, a.fec_valor, a.id_divisa, d.cve_concepto,a.fec_valor as fec_valor_original,a.id_rubro,");  
			sb.append("\n"); sb.append( "INSERT INTO tmp_empresa_flujo");  
			sb.append("\n"); sb.append( "INSERT INTO tmp_empresa_flujo");  
			sb.append("\n"); sb.append( "INSERT INTO tmp_empresa_flujo");  
			sb.append("\n"); sb.append( "INSERT INTO tmp_empresa_flujo");  
			sb.append("\n"); sb.append( "INSERT INTO tmp_empresa_flujo");  
			sb.append("\n"); sb.append( "INSERT INTO tmp_empresa_flujo");  
			sb.append("\n"); sb.append( "INSERT INTO tmp_empresa_flujo");  
			sb.append("\n"); sb.append( "INSERT INTO tmp_empresa_flujo");  
			
			resp = jdbcTemplate.update(sb.toString());			
			
		}else
		{			
			sb.append(" INSERT INTO tmp_empresa_flujo VALUES( " + obj.getNoEmpresa() + ", " + obj.getNoUsuario() + ");");		
			resp = jdbcTemplate.update(sb.toString());			
		}		
		sb.delete( 0, sb.length() );
				
		// SP-- NSERT INTO TMP_MOVIMIENTOS_FLUJO
		sb.append("\n"); sb.append(" execute sp_Insert_TMP_MOVIMIENTOS_FLUJO '" + obj.getFechaInicial() +"' , '"+ obj.getFechaFinal() + "' , '" + obj.getIdDivisa() + "' , '"+obj.getNoUsuario()+"'");
		
		resp = jdbcTemplate.update(sb.toString());
		sb.delete( 0, sb.length() );		 
        
		sb.append("\n"); sb.append( "execute sp_Insert_EN_DIARIO_REAL_AJUSTADO '" + obj.getNoUsuario() + "' , 'Ajustado'");
        
		resp = jdbcTemplate.update(sb.toString());
		sb.delete( 0, sb.length() );            
		
		sb.append( " DELETE FROM tmp_movimientos_flujo WHERE no_usuario =" + obj.getNoUsuario() );
		resp = jdbcTemplate.update(sb.toString());
		sb.delete( 0, sb.length() );  */
		System.out.println("Entra a spFlujoMensualAjustadoDao" );
		sb.append(" execute sp_flujo_ajustado " + obj.getNoUsuario() +" , '"+ obj.getFechaInicial() + "' , '" + obj.getFechaFinal() + "' ,"+7+","+8+", "+obj.getIdGrupo()+" , "+obj.getNoEmpresa()+",'"+obj.getIdDivisa()+"'");
		resp = jdbcTemplate.update(sb.toString());
		System.out.println(sb.toString());
		return resp;
	}
	
	public int spFlujoMensualOriginalDao(ParamSpFlujoDto obj) {
		
		int resp;		
		StringBuffer sb = new StringBuffer();		
		 
		System.out.println("Entra a spFlujoMensualOriginalDao" );
		sb.append(" execute sp_flujo_diario " + obj.getNoUsuario() +" , '"+ obj.getFechaInicial() + "' , '" + obj.getFechaFinal() + "' ,"+5+","+6+", "+obj.getIdGrupo()+" , "+obj.getNoEmpresa()+",'"+obj.getIdDivisa()+"'");
		resp = jdbcTemplate.update(sb.toString());
		System.out.println(sb.toString());
		return resp;
	}
	 /*************************************************************************************************************************************************
	 * ************************************************************************************************************************************************
	 * ************************************************************************************************************************************************
	 * ************************************************************************************************************************************************
	 END PREPARA INFORMACION MENSUAL AJUSTADO*/

	
	/*************************************************************************************************************************************************
	 * ************************************************************************************************************************************************
	 * ************************************************************************************************************************************************
	 * ************************************************************************************************************************************************
	 INIT PREPARA INFORMACION COMPARATIVO MENSUAL ORIGINAL AJUSTADO*/
	public int spFlujoMensualComparativoOriginalAjustadoDao(ParamSpFlujoDto obj) {

		int resp = 0;
		
		StringBuffer sb = new StringBuffer();
		
		/*sb.append(" DELETE FROM tmp_empresa_flujo WHERE no_usuario = " + obj.getNoUsuario() + "\n");		
		resp = jdbcTemplate.update(sb.toString());		
		sb.delete( 0, sb.length() );
		
		sb.append(" DELETE FROM tmp_movimientos_flujo WHERE no_usuario = " + obj.getNoUsuario() + "\n");		
		resp = jdbcTemplate.update(sb.toString());
		sb.delete( 0, sb.length() );
		
		sb.append(" DELETE FROM diario_real_ajustado  WHERE no_usuario = " + obj.getNoUsuario() + "\n");		
		resp = jdbcTemplate.update(sb.toString());
		sb.delete( 0, sb.length() );
		
		if ( obj.getNoEmpresa() == 0 )
		{						
			sb.append("\n"); sb.append( "INSERT INTO tmp_empresa_flujo");                                                               			
			sb.append("\n"); sb.append( "SELECT NO_EMPRESA ," + obj.getNoUsuario() );
			sb.append("\n"); sb.append( "FROM GRUPO_EMPRESA" ); 
			sb.append("\n"); sb.append( "WHERE ID_GRUPO_FLUJO IN(" );
			sb.append("\n"); sb.append( "                        SELECT id_grupo_flujo" );
			sb.append("\n"); sb.append( "                        From cat_super_grupo_flujo" );
			sb.append("\n"); sb.append( "                        WHERE ID_GRUPO_FLUJO IN(SELECT no_empresa" );
			sb.append("\n"); sb.append( "                                                From usuario_empresa" );
			sb.append("\n"); sb.append( "                                                Where no_usuario = " + obj.getNoUsuario() );
			sb.append("\n"); sb.append( "                        )" );
			sb.append("\n"); sb.append( "AND id_super_grupo IN(SELECT ID_SUPER_GRUPO" );
			sb.append("\n"); sb.append( "                      From CAT_SUPER_GRUPO" );
			sb.append("\n"); sb.append( "                      WHERE ID_SUPER_GRUPO IN( " + obj.getIdGrupo() + ")" );
			sb.append("\n"); sb.append( "                      )" );
			sb.append("\n"); sb.append( ");" );
			
			resp = jdbcTemplate.update(sb.toString());			
			
		}else
		{			
			sb.append(" INSERT INTO tmp_empresa_flujo VALUES( " + obj.getNoEmpresa() + ", " + obj.getNoUsuario() + ");");		
			resp = jdbcTemplate.update(sb.toString());			
		}		
		sb.delete( 0, sb.length() );
				
		// SP-- NSERT INTO TMP_MOVIMIENTOS_FLUJO
		sb.append("\n"); sb.append(" execute sp_Insert_TMP_MOVIMIENTOS_FLUJO '" + obj.getFechaInicial() +"' , '"+ obj.getFechaFinal() + "' , '" + obj.getIdDivisa() + "' , '"+obj.getNoUsuario()+"'");
		
		resp = jdbcTemplate.update(sb.toString());
		sb.delete( 0, sb.length() );		 
        
		sb.append("\n"); sb.append( "execute sp_Insert_EN_DIARIO_REAL_AJUSTADO '" + obj.getNoUsuario() + "' , 'Ajustado'");
        
		resp = jdbcTemplate.update(sb.toString());
		sb.delete( 0, sb.length() );            
		
		sb.append( " DELETE FROM tmp_movimientos_flujo WHERE no_usuario =" + obj.getNoUsuario() );
		resp = jdbcTemplate.update(sb.toString());
		sb.delete( 0, sb.length() );
		
		// SP-- NSERT INTO TMP_MOVIMIENTOS_FLUJO
		sb.append("\n"); sb.append(" execute sp_Insert_TMP_MOVIMIENTOS_FLUJO_IDCuard7y8 '" + obj.getFechaInicial() +"' , '"+ obj.getFechaFinal() + "' , '" + obj.getIdDivisa() + "' , '"+obj.getNoUsuario()+"'");
		
		resp = jdbcTemplate.update(sb.toString());
		sb.delete( 0, sb.length() );		 
        
		sb.append("\n"); sb.append( "execute sp_Insert_EN_DIARIO_REAL_AJUSTADO '" + obj.getNoUsuario()+ "' , 'Original'");
        
		resp = jdbcTemplate.update(sb.toString());
		sb.delete( 0, sb.length() );            
		
		sb.append( " DELETE FROM tmp_movimientos_flujo WHERE no_usuario =" + obj.getNoUsuario() );
		resp += jdbcTemplate.update(sb.toString());
		sb.delete( 0, sb.length() );  */
 
		System.out.println("Entra a spFlujoMensualComparativoOriginalAjustadoDao" );
		sb.append(" execute sp_flujo_comparativo_proy_ajus " + obj.getNoUsuario() +" , '"+ obj.getFechaInicial() + "' , '" + obj.getFechaFinal() + "' ,"+7+","+8+","+5+","+6+", "+obj.getIdGrupo()+" , "+obj.getNoEmpresa()+",'"+obj.getIdDivisa()+"'");
		resp = jdbcTemplate.update(sb.toString());
		

		return resp;
		
	}
	/*************************************************************************************************************************************************
	 * ************************************************************************************************************************************************
	 * ************************************************************************************************************************************************
	 * ************************************************************************************************************************************************
	 END PREPARA INFORMACION COMPARATIVO MENSUAL ORIGINAL AJUSTADO*/


/*************************************************************************************************************************************************
 * ************************************************************************************************************************************************
 * ************************************************************************************************************************************************
 * ************************************************************************************************************************************************
 INIT PREPARA INFORMACION COMPARATIVO MENSUAL REAL AJUSTADO*/
public int spFlujoMensualComparativoRealAjustadoDao(ParamSpFlujoDto obj) {

	int resp = 0;
	
	StringBuffer sb = new StringBuffer();
	System.out.println("Mensual 4 ");
	System.out.println("FECHA "+obj.getFechaHoy());
	String fecha2=funciones.cambiarFecha(obj.getFechaHoy(),true);
	sb.append(" execute sp_flujo_comparativo_real_ajus " + obj.getNoUsuario() +" , '"+ obj.getFechaInicial() + "' , '" + obj.getFechaFinal() + "' ,"+5+","+6+","+9+","+10+", "+obj.getIdGrupo()+" , "+obj.getNoEmpresa()+",'"+obj.getIdDivisa()+"','"+fecha2+"'");
	jdbcTemplate.update(sb.toString());
	
	/*
	sb.append(" DELETE FROM tmp_empresa_flujo WHERE no_usuario = " + obj.getNoUsuario() + "\n");		
	resp = jdbcTemplate.update(sb.toString());		
	sb.delete( 0, sb.length() );
	
	sb.append(" DELETE FROM tmp_movimientos_flujo WHERE no_usuario = " + obj.getNoUsuario() + "\n");		
	resp = jdbcTemplate.update(sb.toString());
	sb.delete( 0, sb.length() );
	
	sb.append(" DELETE FROM diario_real_ajustado  WHERE no_usuario = " + obj.getNoUsuario() + "\n");		
	resp = jdbcTemplate.update(sb.toString());
	sb.delete( 0, sb.length() );
		
	if ( obj.getNoEmpresa() == 0 )
	{					
		sb.append("\n"); sb.append( "INSERT INTO tmp_empresa_flujo");                                                               			
		sb.append("\n"); sb.append( "SELECT NO_EMPRESA ," + obj.getNoUsuario() );
		sb.append("\n"); sb.append( "FROM GRUPO_EMPRESA" ); 
		sb.append("\n"); sb.append( "WHERE ID_GRUPO_FLUJO IN(" );
		sb.append("\n"); sb.append( "                        SELECT id_grupo_flujo" );
		sb.append("\n"); sb.append( "                        From cat_super_grupo_flujo" );
		sb.append("\n"); sb.append( "                        WHERE ID_GRUPO_FLUJO IN(SELECT no_empresa" );
		sb.append("\n"); sb.append( "                                                From usuario_empresa" );
		sb.append("\n"); sb.append( "                                                Where no_usuario = " + obj.getNoUsuario() );
		sb.append("\n"); sb.append( "                        )" );
		sb.append("\n"); sb.append( "AND id_super_grupo IN(SELECT ID_SUPER_GRUPO" );
		sb.append("\n"); sb.append( "                      From CAT_SUPER_GRUPO" );
		sb.append("\n"); sb.append( "                      WHERE ID_SUPER_GRUPO IN( " + obj.getIdGrupo() + ")" );
		sb.append("\n"); sb.append( "                      )" );
		sb.append("\n"); sb.append( ");" );
		
		resp = jdbcTemplate.update(sb.toString());			
		
	}else
	{		
		sb.append(" INSERT INTO tmp_empresa_flujo VALUES( " + obj.getNoEmpresa() + ", " + obj.getNoUsuario() + ");");		
		resp = jdbcTemplate.update(sb.toString());		
	}		
	sb.delete( 0, sb.length() );

	// SP-- INSERT INTO TMP_MOVIMIENTOS_FLUJO- con los campos de MOVIMIENTO
	sb.append("\n"); sb.append(" execute sp_Insert_tmp_movimientos_flujo_TipoOperacion '" + obj.getFechaInicial() +"' , '"+ obj.getFechaFinal() + "' , '" + obj.getIdDivisa() + "' , '"+obj.getNoUsuario() + "' , 'MOVIMIENTO'");
	
    resp = jdbcTemplate.update(sb.toString());
	sb.delete( 0, sb.length() );

	// SP-- INSERT INTO TMP_MOVIMIENTOS_FLUJO- con los campos de HIST_MOVIMIENTO
	sb.append("\n"); sb.append(" execute sp_Insert_tmp_movimientos_flujo_TipoOperacion '" + obj.getFechaInicial() +"' , '"+ obj.getFechaFinal() + "' , '" + obj.getIdDivisa() + "' , '"+obj.getNoUsuario() + "' , 'HIST_MOVIMIENTO'");
	
    resp = jdbcTemplate.update(sb.toString());
	sb.delete( 0, sb.length() );			
	
	sb.append("\n"); sb.append(" INSERT INTO TMP_MOVIMIENTOS_FLUJO");
	sb.append("\n"); sb.append(" SELECT NO_EMPRESA, 999999999, '4102', 'I', FEC_VENC,INTERES,'MN',36061,'A'," + obj.getNoUsuario());
	sb.append("\n"); sb.append(" From ORDEN_INVERSION");
	sb.append("\n"); sb.append(" WHERE FEC_VENC BETWEEN CONVERT(DATETIME,'" + obj.getFechaInicial() + "')  AND CONVERT(DATETIME,'" + obj.getFechaFinal() + "')");
	sb.append("\n"); sb.append(" AND no_empresa IN(");
	sb.append("\n"); sb.append("            SELECT NO_EMPRESA");
	sb.append("\n"); sb.append("           From tmp_empresa_flujo");
	sb.append("\n"); sb.append("            Where no_usuario =" + obj.getNoUsuario());
	sb.append("\n"); sb.append("         )");
	sb.append("\n"); sb.append(" AND ID_ESTATUS_ORD IN('A');");			
	resp = jdbcTemplate.update(sb.toString());
	sb.delete( 0, sb.length() );
	
	sb.append("\n"); sb.append( "execute sp_Insert_EN_DIARIO_REAL_AJUSTADO '" + obj.getNoUsuario() + "' , 'Ajustado'");
    
	resp = jdbcTemplate.update(sb.toString());
	sb.delete( 0, sb.length() );            
	
	sb.append( " DELETE FROM tmp_movimientos_flujo WHERE no_usuario =" + obj.getNoUsuario() );
	resp = jdbcTemplate.update(sb.toString());
	sb.delete( 0, sb.length() );
	
	// SP-- NSERT INTO TMP_MOVIMIENTOS_FLUJO
	sb.append("\n"); sb.append(" execute sp_Insert_TMP_MOVIMIENTOS_FLUJO_IDCuard7y8 '" + obj.getFechaInicial() +"' , '"+ obj.getFechaFinal() + "' , '" + obj.getIdDivisa() + "' , '"+obj.getNoUsuario()+"'");
	
	resp = jdbcTemplate.update(sb.toString());
	sb.delete( 0, sb.length() );		 
    
	sb.append("\n"); sb.append( "execute sp_Insert_EN_DIARIO_REAL_AJUSTADO '" + obj.getNoUsuario() + "' , 'Ajustado'");
    
	resp = jdbcTemplate.update(sb.toString());
	sb.delete( 0, sb.length() );            
	
	sb.append( " DELETE FROM tmp_movimientos_flujo WHERE no_usuario =" + obj.getNoUsuario() );
	resp += jdbcTemplate.update(sb.toString());
	sb.delete( 0, sb.length() );  */

	return resp;
	
}
/*************************************************************************************************************************************************
 * ************************************************************************************************************************************************
 * ************************************************************************************************************************************************
 * ************************************************************************************************************************************************
 END PREPARA INFORMACION COMPARATIVO MENSUAL REAL AJUSTADO*/


/*************************************************************************************************************************************************
 * ************************************************************************************************************************************************
 * ************************************************************************************************************************************************
 * ********************************************************hector****************************************************************************************
 INIT PREPARA INFORMACION MENSUAL ORIGINAL AJUSTADO REAL*/ 
public int spFlujoMensuaOriginalAjustadoRealDao(ParamSpFlujoDto obj) {

	int resp = 0;
	
	StringBuffer sb = new StringBuffer();  
	String fecha2=funciones.cambiarFecha(obj.getFechaHoy(),true);
	sb.append(" execute sp_flujo_original_ajustado_real " + obj.getNoUsuario() +" , '"+ obj.getFechaInicial() + "' , '" + obj.getFechaFinal() + "' ,"+7+","+8+","+5+","+6+","+9+","+10+", "+obj.getIdGrupo()+" , "+obj.getNoEmpresa()+",'"+fecha2+"','"+obj.getIdDivisa()+"'");
	jdbcTemplate.update(sb.toString());

	return resp;
	
}
/*************************************************************************************************************************************************
 * ************************************************************************************************************************************************
 * ************************************************************************************************************************************************
 * ************************************************************************************************************************************************
 END PREPARA INFORMACION COMPARATIVO MENSUAL ORIGINAL AJUSTADO REAL*/

@Override
public void sp_mensual_anual_proyectado(String idDivisa, int noUsuario, String paramFechaInicial,
		String paramaFechaFinal, int i, int j, int paramIdGrupo, int paramNoEmpresa) {
 
	
	StringBuffer sb = new StringBuffer();
	sb.append("\n"); sb.append( "execute sp_mensual_anual_proyectado " + noUsuario + " , '"+paramFechaInicial+"','"+paramaFechaFinal+"',"+i+","+j+","+paramIdGrupo+","+paramNoEmpresa+",'"+idDivisa+"'");
	 jdbcTemplate.update(sb.toString());
	
	 System.out.println("SI ENTRO "+sb);
	
}

@Override
public List<Map<String, String>> conceptosTitulos(String movimiento) { 
	List<Map<String, String>> listaTitulosCOnceptos = new ArrayList<Map<String, String>>();
	String sql = ""; 
	try {
		
		sql+="\n  SELECT DISTINCT  ID_TIPO_CONCEPTO, DESCRIPCION"; 
	    sql+="\n From cat_tipo_concepto";
	    if(movimiento.equals("I")){
	    	sql+="\n WHERE INGRESOEGRESO='"+movimiento+"' ";	
	    }else{
	    	sql+="\n WHERE INGRESOEGRESO='"+movimiento+"' ";
	    } 
	    //sql+="\n ORDER BY id_tipo_concepto";
		 System.out.println("en conceptostitulos "+sql);
		listaTitulosCOnceptos = jdbcTemplate.query(sql, new RowMapper<Map<String, String>>() {
			public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException {
				Map<String, String> campos = new HashMap<String, String>(); 
				campos.put("descripcion", rs.getString("DESCRIPCION"));  
				return campos;
			}
		});
	} catch (Exception e) {
		logger.error("Error", e);
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P:Flujo, C:Conceptostitulos, M:reporteFlujo");
	}
	return listaTitulosCOnceptos;
	}
@Override
public List<Map<String, String>> conceptosTitulosID(String movimiento) { 
	List<Map<String, String>> listaTitulosCOnceptosID = new ArrayList<Map<String, String>>();
	String sql = ""; 
	try {
		
		sql+="\n  SELECT DISTINCT  ID_TIPO_CONCEPTO, DESCRIPCION"; 
	    sql+="\n From cat_tipo_concepto";
	    if(movimiento.equals("I")){
	    	sql+="\n WHERE INGRESOEGRESO='"+movimiento+"' ";	
	    }else{
	    	sql+="\n WHERE INGRESOEGRESO='"+movimiento+"' ";
	    } 
	    //sql+="\n ORDER BY id_tipo_concepto"; 
		 System.out.println("en conceptostitulos "+sql);
		listaTitulosCOnceptosID = jdbcTemplate.query(sql, new RowMapper<Map<String, String>>() {
			public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException {
				Map<String, String> campos = new HashMap<String, String>(); 
				campos.put("TIPO", rs.getString("ID_TIPO_CONCEPTO"));  
				return campos;
			}
		});
	} catch (Exception e) {
		logger.error("Error", e);
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P:Flujo, C:Conceptostitulos, M:reporteFlujo");
	}
	return listaTitulosCOnceptosID;
	}
public float saldoInicial(String grupo,String Noempresa,String empresa,String fechaIni,String fechaFin,String reporte,String noGrupo,int usuario,String divisa){
	StringBuffer sbSQL = new StringBuffer(); 
	int r;

	float saldos =0;
	r=Integer.parseInt(Noempresa);
	String fecha;
	fecha=fecha(fechaIni); 
	String fecha2=funciones.cambiarFecha(fecha,true);
	//System.out.println(fecha2);
	
	try{
		
		
		sbSQL.append("select coalesce(sum(saldo_inicial),0)as Saldo from hist_cat_cta_banco where fec_valor = '"+fecha2+"' and id_divisa = '"+divisa+"'");
		 
		if( r == 0){
			sbSQL.append(" And no_empresa in (select id_grupo_flujo from cat_super_grupo_flujo where id_grupo_flujo");
			sbSQL.append(" in(select no_empresa from usuario_empresa where no_usuario="+usuario+"");
			sbSQL.append(" and id_super_grupo in(select id_super_grupo from cat_super_grupo where id_super_grupo in("+noGrupo+"))))");
		}else{
			sbSQL.append("And no_empresa =(select id_grupo_flujo from cat_super_grupo_flujo where id_grupo_flujo="+r+"");
			sbSQL.append("and id_super_grupo ="+noGrupo+")");
			
		}
		//System.out.println("saldo"+sbSQL);
		saldos=jdbcTemplate.queryForInt(sbSQL.toString());
		
	}catch(Exception e){
		logger.error("Error", e);
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P:Flujo, C:SaldoIncial, M:reporteFlujo");
	}
	return saldos;
}

public String  fecha(String fechaIni) {
 
	StringBuffer sb = new StringBuffer();	
	String fechaa="" ;
	try{
	sb.append(" \n declare @dia varchar(20),	@fecha datetime");
	sb.append(" \n set @fecha = '"+fechaIni+"'");	
	sb.append(" \n set @dia=DATENAME(dw, @fecha)");	
	sb.append(" \n if (@dia = 'Sábado') begin");	
	sb.append(" \n  set @fecha= dateadd(day, 1, @fecha)");	
	sb.append(" \n end");	
	sb.append(" \n if(@dia = 'Domingo') begin");	
	sb.append(" \n set @fecha= dateadd(day, 1, @fecha)");	
	sb.append(" \n end");	
	sb.append(" \n select @fecha as fecha");
	
	fechaa=jdbcTemplate.queryForObject(sb.toString(), String.class);
	
	}catch(Exception e){
		bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
				+ "P:Inversiones, C:InversionesDaoImpl, M:consultarEstatusOrden");
	}
	
	return fechaa;	
	
}
}
	
	


