package com.webset.set.egresos.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.egresos.dao.PropuestaPagoManualDao;
import com.webset.set.egresos.dto.ControlProyectoDto;
import com.webset.set.egresos.dto.PropuestaPagoManualDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.EstatusMovimientosDto;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.ParametroDto;
import com.webset.set.utilerias.dto.StoreParamsComunDto;
import com.webset.set.utilerias.ConstantesSet;

/**
 * 
 * @author Jessica Arelly Cruz Cruz
 * @since 14/02/2011
 *
 */
public class PropuestaPagoManualDaoImpl implements PropuestaPagoManualDao{
	private JdbcTemplate jdbcTemplate;
	private Bitacora bitacora = new Bitacora();
	private Funciones funciones;
	private ConsultasGenerales consultasGenerales;
	
	/**
	 * llamda al configuraSET
	 * @param indice
	 * @return
	 */
	public String consultarConfiguraSet(int indice) 
	{
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
	}
	
	
	/**
	 * llama al metodo de llenar combo general
	 * @param dto
	 * @return
	 */
	public List<LlenaComboGralDto>llenarComboGral(LlenaComboGralDto dto)
	{
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.llenarComboGral(dto);
	}
	
	/**
	 * obtener la fecha ma�ana
	 * @return
	 */
	public Date obtenerFechaManana(){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.obtenerFechaManana();
	}
	
	public Date obtenerFechaHoy(){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.obtenerFechaHoy();
	}
	
	public int obtenerDiasDiferencia(Date fecha1, Date fecha2){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.obtenerDiasDiferencia(fecha1, fecha2);
	}
	
	
	public Map<String, Object> cambiarCuadrantes(StoreParamsComunDto dto)
	{
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.cambiarCuadrantes(dto);
	}
	
	/**
	 * FunSQLCombo3()
	 * @return
	 */
	
	public List<LlenaComboDivisaDto>llenarComboDivisa(String psDivisa)
	{
		String sql="";
		StringBuffer sb = new StringBuffer();
		List<LlenaComboDivisaDto> listDivisas= new ArrayList<LlenaComboDivisaDto>();
		try
		{
			sb.append( " SELECT id_divisa as ID, desc_divisa as Descrip ");
			sb.append( "\n   FROM cat_divisa ");
			sb.append( "\n  WHERE clasificacion = 'D'");
			sb.append( "\n  AND id_divisa <> 'CNT'");
			if(!psDivisa.equals(""))
				sb.append( "\n AND id_divisa <> '" + psDivisa + "' ");
			sql = sb.toString();
			listDivisas= jdbcTemplate.query(sql, new RowMapper<LlenaComboDivisaDto>(){
			public LlenaComboDivisaDto mapRow(ResultSet rs, int idx) throws SQLException {
				LlenaComboDivisaDto cons = new LlenaComboDivisaDto();
				cons.setIdDivisa(rs.getString("ID"));
				cons.setDescDivisa(rs.getString("Descrip"));
				return cons;
			}});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:PropuestaPagoManualDaoImpl, M:llenarComboDivisa");
		}
		
		return listDivisas;
	    
	}
	
	/**
	 * FunSQLControl_Proyecto(4, 3)
	 * @param egreso
	 * @param bloqueo
	 * @return
	 */
	
	public String controlProyecto(int egreso, int bloqueo) {
		String sql = "";
		StringBuffer sb = new StringBuffer();
		List <ControlProyectoDto> result = null;
		try{
			sb.append( " SELECT b_habilita");
			sb.append( " FROM control_proyecto ");
			sb.append( " WHERE id_modulo = " + egreso);
			sb.append( " AND id_funcionalidad = " + bloqueo);
		
		    sql=sb.toString();
				result = jdbcTemplate.query(sql, new RowMapper<ControlProyectoDto>(){
				public ControlProyectoDto mapRow(ResultSet rs, int idx) throws SQLException {
					ControlProyectoDto control = new ControlProyectoDto();
					control.setBHabilita(rs.getString("b_habilita"));
				return control;
				}});
				
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Egresos, C:PropuestaPagoManualDaoImpl, M:controlProyecto");
			} 
		return result.isEmpty()? "" : result.get(0).getBHabilita();
	}
	
	/**
	 * FunSQLSelect23(GI_USUARIO)
	 * @param datos
	 * @return
	 */
	
	public List<LlenaComboGralDto> llenarComboGrupo(Map<String,Object> datos){
		String sql="";
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		try{
			if (datos.get("pagoEmpresa").equals(0)){
		        sb.append("    SELECT distinct c.id_grupo_flujo as ID, desc_grupo_flujo as DESCRIPCION");
		        sb.append("\n    FROM cat_grupo_flujo c, grupo_empresa g, usuario_empresa  u");
		        sb.append("\n   WHERE c.id_grupo_flujo = g.id_grupo_flujo");
		        sb.append("\n     AND g.no_empresa = u.no_empresa");
		        sb.append("\n     AND u.no_usuario = " + datos.get("noUsuario"));
		        sb.append("\n   ORDER BY desc_grupo_flujo");
			}else{
		        sb.append("\n  Select e.no_empresa as ID, e.nom_empresa as DESCRIPCION from empresa e, usuario_empresa ue");
		        sb.append("\n  Where e.no_empresa = ue.no_empresa");
		        sb.append("\n  and ue.no_usuario = " + datos.get("noUsuario"));
		        
		        if (!datos.get("noEmpresa").equals(0))
		            sb.append("\n  and e.no_empresa = " + datos.get("noEmpresa"));
		        else
		            sb.append("\n  and (pagadora = 'S' or (e.no_empresa = " + datos.get("pagoEmpresa") + " and pagadora = 'A' ))");
		        sb.append("\n  order by e.no_empresa");
			}
		    sql = sb.toString();
		    
		    System.out.println("grupo empresa "+sql);
		    //bitacora.insertarRegistro(sql);
		    
		    listDatos= jdbcTemplate.query(sql, new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:PropuestaPagoManualDaoImpl, M:llenarComboGrupo");
		}
		
		return listDatos;
	}
	
	/**
	 * FunSQLSelect21
	 * consulta que obtiene las propuestas generadas en la solicitud de pagos
	 * @param dto
	 * @param gsDBM
	 * @return
	 */
	
	public List<PropuestaPagoManualDto> consultarPropuestaPagoManual(Map<String, String> datos)
		{	
			String sql="";
			StringBuffer sb = new StringBuffer();
			String funFiltroCriterios = "";
			String gsDBM = ConstantesSet.gsDBM;
			funFiltroCriterios = filtroCriterios(datos);
			List <PropuestaPagoManualDto> paramRetorno = new ArrayList<PropuestaPagoManualDto>();
		try{
		     sb.append("\n  SELECT top(500) m.no_docto, m.division, ");
		     sb.append("\n        importe_original,");
		     sb.append("\n        e.desc_estatus ");
		     sb.append("\n        ,m.no_factura ");
		     sb.append("\n        ,m.fec_valor ");
		     sb.append("\n        ,cv.desc_cve_operacion ");

		     if(gsDBM.equals("SQL SERVER") )
		        sb.append("\n        ,convert(varchar,m.importe)AS importe ");
		     else if (gsDBM.equals("DB2") || gsDBM.equals("POSTGRESQL"))
		        sb.append("\n        ,m.importe ");
		     else
		        sb.append("\n        ,convert(varchar,m.importe)AS importe ");
		     
		     sb.append("\n        ,m.id_divisa ");
		     sb.append("\n        ,fp.desc_forma_pago ");
		     sb.append("\n        ,b.desc_banco ");
		     sb.append("\n        ,m.id_chequera_benef ");
		     sb.append("\n        ,m.beneficiario ");
		     sb.append("\n        ,m.concepto ");
		     sb.append("\n        ,m.no_folio_det ");
		     sb.append("\n        ,m.no_folio_mov ");
		     sb.append("\n        ,m.no_cuenta ");
		     sb.append("\n        ,m.id_chequera ");
		     sb.append("\n        ,cv.id_cve_operacion ");
		     sb.append("\n        ,m.id_forma_pago ");
		     sb.append("\n        ,coalesce(m.id_banco_benef,0) as id_banco_benef ");
		     sb.append("\n        ,m.id_caja ");
		     sb.append("\n        ,m.id_leyenda ");
		     sb.append("\n        ,m.id_estatus_mov ");
		     sb.append("\n        ,m.no_cliente");
		     sb.append("\n        ,m.tipo_cambio ");
		     sb.append("\n        ,m.origen_mov ");
		     sb.append("\n        ,m.solicita ");
		     sb.append("\n        ,m.autoriza ");
		     sb.append("\n        ,m.observacion ");
		     sb.append("\n        ,m.lote_entrada ");
		     sb.append("\n        ,cj.desc_caja ");
		     sb.append("\n        ,m.agrupa1, m.agrupa2, m.id_rubro, m.agrupa3");

		     if (!gsDBM.equals("DB2") && !gsDBM.equals("POSTGRESQL"))
		        sb.append("\n        ,p.b_servicios ");
		  
		     sb.append("\n        ,m.no_pedido ");
		     sb.append("\n        ,m.no_empresa, m.oper_may_esp ");
		     sb.append("\n        ,em.nom_empresa");
		     
		     if (datos.get("tipoChequeNE").equals("true"))
		     {
		         sb.append("\n        , coalesce((select id_clabe from ctas_banco cb where cb.no_persona = m.no_cliente");
		         sb.append("\n            and cb.id_banco = m.id_banco_benef and cb.id_chequera = m.id_chequera_benef), '')");
		         sb.append("\n        as clabe");
		     }else
		         sb.append("\n        , m.clabe");
		     
		     sb.append("\n        ,m.id_contable, m.id_grupo, m.id_rubro, m.id_banco, cg.desc_grupo, cr.desc_rubro  ");
		     
		//Select Case gsDBM
		 if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE") || gsDBM.equals("ORACLE"))
		 {
		 //Case "SQL SERVER", "SYBASE", "ORACLE":
			 
//		     sb.append("\n   FROM movimiento m ");
//		     sb.append("\n        ,cat_banco b ");
//		     sb.append("\n        ,cat_tipo_operacion co ");
//		     sb.append("\n        ,cat_cve_operacion  cv ");
//		     sb.append("\n        ,cat_forma_pago fp ");
//		     sb.append("\n        ,cat_estatus e ");
//		     sb.append("\n        ,cat_caja cj ");
//		     sb.append("\n        ,proveedor p ");
			 
			 sb.append("\n   FROM movimiento m");
		     sb.append("\n   LEFT JOIN cat_banco b ON (m.id_banco_benef = b.id_banco)");
		     sb.append("\n        ,cat_tipo_operacion co ");
		     sb.append("\n        ,cat_cve_operacion  cv ");
		     sb.append("\n        ,cat_forma_pago fp ");
		     sb.append("\n   LEFT JOIN cat_estatus e ON (m.id_estatus_mov = e.id_estatus)");
		     sb.append("\n  LEFT JOIN cat_caja cj ON(m.id_caja = cj.id_caja)");
		     sb.append("\n        ,proveedor p ");
		 }
		 else if(gsDBM.equals("POSTGRESQL"))
		 {
		//Case "DB2", "POSTGRESQL"
		     if (datos.get("tipoChequeNE").equals("true"))
		     {
		         sb.append("\n   FROM movimiento m ");
		         sb.append("\n        LEFT JOIN cat_estatus e ON (m.id_estatus_mov  = e.id_estatus And e.clasificacion = 'MOV') ");
		         sb.append("\n        LEFT JOIN cat_caja cj ON (m.id_caja = cj.id_caja) ");
		         sb.append("\n        ,cat_cve_operacion cv ");
		         sb.append("\n        ,cat_forma_pago fp, cat_banco b ");
		     }else{
		         sb.append("\n   FROM movimiento m LEFT JOIN cat_banco b ON(m.id_banco_benef = b.id_banco)");
		         sb.append("\n        LEFT JOIN cat_estatus e ON (m.id_estatus_mov  = e.id_estatus And e.clasificacion = 'MOV')");
		         sb.append("\n        LEFT JOIN cat_caja cj ON (m.id_caja = cj.id_caja)");
		         sb.append("\n        ,cat_cve_operacion  cv ");
		         sb.append("\n        ,cat_forma_pago fp ");
		     }
		  //End Select
		 }
		     
		     sb.append("\n        ,empresa em ");
		     
		     if (datos.get("psDivision").equals(""))
		     {
		         sb.append("\n        ,grupo_empresa g, cat_grupo cg, cat_rubro cr  ");
		         sb.append("\n  WHERE m.no_empresa = g.no_empresa");
		         sb.append("\n    AND m.no_empresa = em.no_empresa");
		     }else{
		         if (datos.get("psDivision").equals("TODAS LAS DIVISIONES"))
		         {
		             sb.append("\n  WHERE (m.division is not null and m.division <> '') ");
		             sb.append("\n    AND m.no_empresa = em.no_empresa");
		         }else{
		             sb.append("\n  WHERE m.division = '" + datos.get("psDivision") + "' ");
		             sb.append("\n    AND m.no_empresa = em.no_empresa");
		         }
		     }

		     if (!datos.get("grupoEmpresa").equals("0"))
		         sb.append("\n  AND g.id_grupo_flujo = " + datos.get("grupoEmpresa"));
		     //CHECAR SI ESTA CONDICION ES CORRECTA
	    	 //sb.append("\n  AND m.no_empresa in (" + datos.get("noEmpresa") + ")");
		     
		  //Select Case gsDBM
		     if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE") || gsDBM.equals("ORACLE"))
		     {
		     //Case "SQL SERVER", "SYBASE", "ORACLE"
		        if (gsDBM.equals("SYBASE"))
		            sb.append("\n    AND m.no_cliente = convert(varchar, p.no_proveedor) ");
		        else
		            sb.append("\n    AND m.no_cliente = p.no_proveedor ");
		                 
		        if (datos.get("tipoChequeNE").equals("true"))
		            sb.append("\n    AND m.id_banco_benef = b.id_banco");
		        else
		           // sb.append("\n    AND m.id_banco_benef *= b.id_banco");
		        
//	//	        sb.append("\n    AND coalesce(m.observacion, '') <> 'PAGO PARCIALIZADO'");
		      //  sb.append("\n    AND m.id_estatus_mov  *= e.id_estatus");
		        sb.append("\n    AND m.id_tipo_movto = 'E'  ");
		        sb.append("\n    AND (m.origen_mov not in ('INV','FIL') or m.origen_mov is null)");
		        sb.append("\n    AND e.clasificacion='MOV' ");
		        sb.append("\n    AND m.id_tipo_operacion = co.id_tipo_operacion ");
		        sb.append("\n    AND co.no_empresa = 1");
		        sb.append("\n    AND m.id_cve_operacion = cv.id_cve_operacion  ");
		        sb.append("\n    AND m.id_estatus_mov in ('N','C','F') ");
		        sb.append("\n    AND m.id_forma_pago = fp.id_forma_pago ");
		       // sb.append("\n    AND m.id_caja *= cj.id_caja ");
		        sb.append("\n    AND (m.fec_propuesta is null or m.fec_propuesta = '')");
		        //aqui
		        sb.append("\n    AND m.no_cliente not in(" + consultarConfiguraSet(206) + ")");
		        sb.append("\n    AND m.ID_GRUPO = cg.id_grupo \n");
		        sb.append("\n    AND m.id_rubro = cr.id_rubro \n");
		        sb.append("\n    AND cg.id_grupo = cr.id_grupo \n");
		     }

		     if(gsDBM.equals("POSTGRESQL"))
		     {
		     //Case "DB2", "POSTGRESQL"
		        if (datos.get("tipoChequeNE").equals("true"))
		            sb.append("\n    AND m.id_banco_benef = b.id_banco ");
		        
//		        sb.append("\n    AND coalesce(m.observacion, '') <> 'PAGO PARCIALIZADO'");
		        sb.append("\n    AND m.id_tipo_movto = 'E'");
		        sb.append("\n    AND (m.origen_mov not in ('INV','FIL') or m.origen_mov is null)");
		        sb.append("\n    AND m.id_estatus_mov in ('N','C','F')");
		        sb.append("\n    AND m.fec_propuesta is null");
		        sb.append("\n    AND m.id_forma_pago = fp.id_forma_pago");
		        sb.append("\n    AND cv.no_empresa = '1'");
		        sb.append("\n    AND m.id_cve_operacion = cv.id_cve_operacion  ");
		        sb.append("\n    AND m.no_cliente not in(" + consultarConfiguraSet(206) + ")");
		     }
		 
		     
		     if (datos.get("psDivision").equals(""))
		     {
		         sb.append("\n    AND m.no_empresa in ( SELECT no_empresa ");
		         sb.append("\n                          FROM usuario_empresa ");
		         sb.append("\n                          WHERE no_usuario = " + datos.get("usuario") + ")");
		     }else{
		         sb.append("\n    AND m.division in ( SELECT distinct id_division ");
		         sb.append("\n                        FROM usuario_division ");
		         sb.append("\n                        WHERE no_usuario = " + datos.get("usuario") + ")");
		     }
		     
		     /*sb.append("\n  AND (((select count(*) from usuario_area WHERE no_usuario = " + datos.get("usuario") + ") = 0) or ");
		     sb.append("\n      (id_area in (select id_area from usuario_area where no_usuario = " + datos.get("usuario") + ")))");
		     se comenta por que los movimientos no traen area */
		     
		     if(!funFiltroCriterios.equals(""))
	    	 {
		     	sb.append("\n"+funFiltroCriterios);
	    	 }
		     
		     //Buscar a los Proveedores Incidentales
		     sb.append("\n  UNION ");
		     sb.append("\n  SELECT m.no_docto, m.division, ");
		     sb.append("\n        importe_original,");
		     sb.append("\n        e.desc_estatus ");
		     sb.append("\n        ,m.no_factura ");
		     sb.append("\n        ,m.fec_valor ");
		     sb.append("\n        ,cv.desc_cve_operacion ");
		     
		     if (gsDBM.equals("SQL SERVER"))
		        sb.append("\n        ,convert(varchar,m.importe)AS importe ");
		     else if (gsDBM.equals("DB2") || gsDBM.equals("POSTGRESQL"))
		        sb.append("\n        ,m.importe ");
		     else
		        sb.append("\n        ,convert(varchar,m.importe)AS importe ");
		     
		     sb.append("\n        ,m.id_divisa ");
		     sb.append("\n        ,fp.desc_forma_pago ");
		     sb.append("\n        ,b.desc_banco ");
		     sb.append("\n        ,m.id_chequera_benef ");
		     sb.append("\n        ,m.beneficiario ");
		     sb.append("\n        ,m.concepto ");
		     sb.append("\n        ,m.no_folio_det ");
		     sb.append("\n        ,m.no_folio_mov ");
		     sb.append("\n        ,m.no_cuenta ");
		     sb.append("\n        ,m.id_chequera ");
		     sb.append("\n        ,cv.id_cve_operacion ");
		     sb.append("\n        ,m.id_forma_pago ");
		     sb.append("\n        ,coalesce(m.id_banco_benef,0) as id_banco_benef");
		     sb.append("\n        ,m.id_caja ");
		     sb.append("\n        ,m.id_leyenda ");
		     sb.append("\n        ,m.id_estatus_mov ");
		     sb.append("\n        ,m.no_cliente");
		     sb.append("\n        ,m.tipo_cambio ");
		     sb.append("\n        ,m.origen_mov ");
		     sb.append("\n        ,m.solicita ");
		     sb.append("\n        ,m.autoriza ");
		     sb.append("\n        ,m.observacion ");
		     sb.append("\n        ,m.lote_entrada ");
		     sb.append("\n        ,cj.desc_caja ");
		     sb.append("\n        ,m.agrupa1, m.agrupa2, m.id_rubro, m.agrupa3 ");
		     
		     if (!gsDBM.equals("DB2"))
		        sb.append("\n        ,'' as b_servicios ");
		     
		     sb.append("\n        ,m.no_pedido ");
		     sb.append("\n        ,m.no_empresa, m.oper_may_esp ");
		     sb.append("\n        ,em.nom_empresa");
		     sb.append("\n        ,m.clabe ");
		     sb.append("\n        ,m.id_contable, m.id_grupo, m.id_rubro, m.id_banco, cg.desc_grupo, cr.desc_rubro ");
		     
		     
		//Select Case gsDBM
	     if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE") || gsDBM.equals("ORACLE"))
	     {
		   //Case "SQL SERVER", "SYBASE", "ORACLE"
//	    	  sb.append("\n   FROM movimiento m ");
//			     sb.append("\n        ,cat_banco b ");
//			     sb.append("\n        ,cat_tipo_operacion co ");
//			     sb.append("\n        ,cat_cve_operacion  cv ");
//			     sb.append("\n        ,cat_forma_pago fp ");
//			     sb.append("\n        ,cat_estatus e ");
//			     sb.append("\n        ,cat_caja cj ");
	    	 
	    	 sb.append("\n   FROM movimiento m");
		     sb.append("\n   LEFT JOIN cat_banco b ON (m.id_banco_benef = b.id_banco)");
		     sb.append("\n        ,cat_tipo_operacion co ");
		     sb.append("\n        ,cat_cve_operacion  cv ");
		     sb.append("\n        ,cat_forma_pago fp ");
		     sb.append("\n   LEFT JOIN cat_estatus e ON (m.id_estatus_mov = e.id_estatus)");
		     sb.append("\n  LEFT JOIN cat_caja cj ON(m.id_caja = cj.id_caja)");
		  
	     }
	     else if(gsDBM.equals("POSTGRESQL"))
	     {
		   //Case "DB2", "POSTGRESQL"
		     if (datos.get("tipoChequeNE").equals("true"))
		     {
		         sb.append("\n   FROM movimiento m ");
		         sb.append("\n        LEFT JOIN cat_estatus e ON (m.id_estatus_mov  = e.id_estatus And e.clasificacion = 'MOV') ");
		         sb.append("\n        LEFT JOIN cat_caja cj ON (m.id_caja = cj.id_caja) ");
		         sb.append("\n        ,cat_cve_operacion cv ");
		         sb.append("\n        ,cat_forma_pago fp, cat_banco b ");
		     }else{
		         sb.append("\n   FROM movimiento m LEFT JOIN cat_banco b ON(m.id_banco_benef = b.id_banco)");
		         sb.append("\n        LEFT JOIN cat_estatus e ON (m.id_estatus_mov  = e.id_estatus And e.clasificacion = 'MOV')");
		         sb.append("\n        LEFT JOIN cat_caja cj ON (m.id_caja = cj.id_caja)");
		         sb.append("\n        ,cat_cve_operacion  cv ");
		         sb.append("\n        ,cat_forma_pago fp ");
		     }
	     }
		                 
		     sb.append("\n        ,empresa em ");
		     
		     if (datos.get("division").equals(""))
		     {
		         sb.append("\n        ,grupo_empresa g, cat_grupo cg, cat_rubro cr  ");
		         sb.append("\n  WHERE m.no_empresa = g.no_empresa");
		         sb.append("\n    AND m.no_empresa = em.no_empresa");
		     }else{
		         if (datos.get("division").equals("TODAS LAS DIVISIONES"))
		         {
		             sb.append("\n  WHERE (m.division is not null and m.division <> '') ");
		             sb.append("\n    AND m.no_empresa = em.no_empresa");
		         }else{
		             sb.append("\n  WHERE m.division = '" + datos.get("division") + "' ");
		             sb.append("\n    AND m.no_empresa = em.no_empresa");
		         }
		     }
		     
		     if (!datos.get("grupoEmpresa").equals("0"))
		         sb.append("\n  AND g.id_grupo_flujo = " + datos.get("grupoEmpresa"));
		     
		   //CHECAR SI ESTA CONDICION ES CORRECTA
		   //  sb.append("\n  AND m.no_empresa in (" + datos.get("noEmpresa") + ")");
		     
		  //Select Case gsDBM
	     if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE") || gsDBM.equals("ORACLE"))
	     {
		  //Case "SQL SERVER", "SYBASE", "ORACLE"
		     if (datos.get("tipoChequeNE").equals("true"))
		         sb.append("\n    AND m.id_banco_benef = b.id_banco");
		     else
			    // sb.append("\n    AND m.id_banco_benef *= b.id_banco");
		     
//	//	     sb.append("\n    AND coalesce(m.observacion, '') <> 'PAGO PARCIALIZADO'");
		  //   sb.append("\n    AND m.id_estatus_mov  *= e.id_estatus");
		     sb.append("\n    AND m.id_tipo_movto = 'E'  ");
		     sb.append("\n    AND (m.origen_mov not in ('INV','FIL') or m.origen_mov is null)");
		     sb.append("\n    AND e.clasificacion='MOV' ");
		     sb.append("\n    AND m.id_tipo_operacion = co.id_tipo_operacion ");
		     sb.append("\n    AND co.no_empresa = 1 ");
		     sb.append("\n    AND m.id_cve_operacion = cv.id_cve_operacion  ");
		     sb.append("\n    AND m.id_estatus_mov in ('N','C','F') ");
		     sb.append("\n    AND m.id_forma_pago = fp.id_forma_pago ");
		//     sb.append("\n    AND m.id_caja *= cj.id_caja ");
		     sb.append("\n    AND (m.fec_propuesta is null or m.fec_propuesta = '')");
		     sb.append("\n    AND m.no_cliente in(" + consultarConfiguraSet(206) + ")");
		     sb.append("\n    AND m.ID_GRUPO = cg.id_grupo \n");
		     sb.append("\n    AND m.id_rubro = cr.id_rubro \n");
		     sb.append("\n    AND cg.id_grupo = cr.id_grupo \n");
	     }
	     else if(gsDBM.equals("POSTGRESQL"))
	     {
		  //Case "DB2", "POSTGRESQL"
		     if (datos.get("tipoChequeNE").equals("true"))
		         sb.append("\n    AND m.id_banco_benef = b.id_banco ");
		     
//		     sb.append("\n    AND coalesce(m.observacion, '') <> 'PAGO PARCIALIZADO'");
		     sb.append("\n    AND m.id_tipo_movto = 'E'");
		     sb.append("\n    AND (m.origen_mov not in ('INV','FIL') or m.origen_mov is null)");
		     sb.append("\n    AND m.id_estatus_mov in ('N','C','F')");
		     sb.append("\n    AND m.fec_propuesta is null");
		     sb.append("\n    AND m.id_forma_pago = fp.id_forma_pago");
		     sb.append("\n    AND cv.no_empresa = '1'");
		     sb.append("\n    AND m.id_cve_operacion = cv.id_cve_operacion  ");
		     sb.append("\n    AND m.no_cliente in(" + consultarConfiguraSet(206) + ")");
	     }

	     if (datos.get("division").equals(""))
		     {
		         sb.append("\n    AND m.no_empresa in ( SELECT no_empresa ");
		         sb.append("\n                          FROM usuario_empresa ");
		         sb.append("\n                          WHERE no_usuario = " + datos.get("usuario") + ")");
		     }else{
		         sb.append("\n    AND m.division in ( SELECT distinct id_division ");
		         sb.append("\n                        FROM usuario_division ");
		         sb.append("\n                        WHERE no_usuario = " + datos.get("usuario") + ")");
		     }
		         
		     /*sb.append("\n  AND (((select count(*) from usuario_area WHERE no_usuario = " + datos.get("usuario") + ") = 0) or ");
		     sb.append("\n      (id_area in (select id_area from usuario_area where no_usuario = " + datos.get("usuario") + ")))");*/
		         
		     if(!funFiltroCriterios.equals(""))
		     {
		    	 sb.append("\n"+funFiltroCriterios);
		     }
     
		    sql = sb.toString();
		    
		    System.out.println("busqueda propuestas: " + sql);
			
		    paramRetorno = jdbcTemplate.query(sql, new RowMapper<PropuestaPagoManualDto>(){
			public PropuestaPagoManualDto mapRow(ResultSet rs, int idx) throws SQLException {
				PropuestaPagoManualDto cons = new PropuestaPagoManualDto();
					cons.setNoDocto(rs.getString("no_docto") == null ? "0" : rs.getString("no_docto"));
					cons.setDivision(rs.getString("division"));
					cons.setImporteOriginal(rs.getDouble("importe_original"));
					cons.setDescEstatus(rs.getString("desc_estatus"));
					cons.setNoFactura(rs.getString("no_factura"));
					cons.setFecValor(rs.getDate("fec_valor"));
					cons.setDescCveOperacion(rs.getString("desc_cve_operacion"));
					cons.setImporte(rs.getDouble("importe"));
					cons.setIdDivisa(rs.getString("id_divisa"));
					cons.setDescFormaPago(rs.getString("desc_forma_pago"));
					cons.setDescBanco(rs.getString("desc_banco"));
					cons.setIdChequeraBenef(rs.getString("id_chequera_benef"));
					cons.setBeneficiario(rs.getString("beneficiario"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setNoFolioDet(rs.getString("no_folio_det"));
					cons.setNoFolioMov(rs.getInt("no_folio_mov"));
					cons.setNoCuenta(rs.getInt("no_cuenta"));
					cons.setIdChequera(rs.getString("id_chequera"));
					cons.setIdCveOperacion(rs.getInt("id_cve_operacion"));
					cons.setIdFormaPago(rs.getInt("id_forma_pago"));
					cons.setIdBancoBenef(rs.getInt("id_banco_benef"));
					cons.setIdCaja(rs.getInt("id_caja"));
					cons.setIdLeyenda(rs.getString("id_leyenda"));
					cons.setIdEstatusMov(rs.getString("id_estatus_mov"));
					cons.setNoCliente(rs.getString("no_cliente"));
					cons.setTipoCambio(rs.getDouble("tipo_cambio"));	
					cons.setOrigenMov(rs.getString("origen_mov") == null ? "CXP" : rs.getString("origen_mov"));
					cons.setSolicita(rs.getString("solicita"));
					cons.setAutoriza(rs.getString("autoriza"));
					cons.setObservacion(rs.getString("observacion"));
					cons.setLoteEntrada(rs.getInt("lote_entrada"));
					cons.setDescCaja(rs.getString("desc_caja"));
					cons.setAgrupa1(rs.getInt("agrupa1"));
					cons.setAgrupa2(rs.getInt("agrupa2"));
					cons.setIdRubro(rs.getDouble("id_rubro"));
					cons.setAgrupa3(rs.getInt("agrupa3"));
					cons.setBServicios(rs.getString("b_servicios"));
					cons.setNoPedido(rs.getInt("no_pedido"));
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setOperMayEsp(rs.getString("oper_may_esp"));
					cons.setNomEmpresa(rs.getString("nom_empresa"));
					cons.setClabe(rs.getString("clabe"));
					cons.setIdContable(rs.getString("id_contable"));
					cons.setIdRubro(rs.getDouble("id_rubro"));
					cons.setIdGrupoFlujo(rs.getInt("id_grupo"));
					cons.setDifDias(obtenerDiasDiferencia(rs.getDate("fec_valor"),obtenerFechaHoy())
							 <0 ? 0 : obtenerDiasDiferencia(rs.getDate("fec_valor"),obtenerFechaHoy()));
					cons.setIdBanco(rs.getString("id_banco")== null ? "" : rs.getString("id_banco"));
					cons.setDescGrupo(rs.getString("desc_grupo"));
					cons.setDescRubro(rs.getString("desc_rubro"));
					
				return cons;
			}});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PropuestaPagoManualDaoImpl, M:consultarPropuestaPagoManual");
		} 
		return paramRetorno ;
	}
	
	
	/**
	 * FunSQLSelect21
	 * consulta que obtiene la suma total del importe de todas las propuestas 
	 * @param dto
	 * @param gsDBM
	 * @return
	 */
	
	public double sumarImportePropuestas(Map<String, String> datos)
		{	
			String sql="";
			StringBuffer sb = new StringBuffer();
			String funFiltroCriterios = "";
			String gsDBM = ConstantesSet.gsDBM;
			funFiltroCriterios = filtroCriterios(datos);
			List <PropuestaPagoManualDto> paramRetorno = new ArrayList<PropuestaPagoManualDto>();
		try{
		     sb.append("\n  SELECT sum(importe) as importe ");
		     
		//Select Case gsDBM
		 if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE") || gsDBM.equals("ORACLE"))
		 {
		 //Case "SQL SERVER", "SYBASE", "ORACLE":
		     sb.append("\n   FROM movimiento m ");
		     sb.append("\n   LEFT JOIN cat_banco b ON (m.id_banco_benef = b.id_banco)");
		     sb.append("\n        ,cat_tipo_operacion co ");
		     sb.append("\n        ,cat_cve_operacion  cv ");
		     sb.append("\n        ,cat_forma_pago fp ");
		     sb.append("\n   LEFT JOIN cat_estatus e ON (m.id_estatus_mov  = e.id_estatus)");
		     sb.append("\n   LEFT JOIN cat_caja cj ON (m.id_caja = cj.id_caja)");
		     sb.append("\n        ,proveedor p ");
		 }
		 else if(gsDBM.equals("POSTGRESQL"))
		 {
		//Case "DB2", "POSTGRESQL"
		     if (datos.get("tipoChequeNE").equals("true"))
		     {
		         sb.append("\n   FROM movimiento m ");
		         sb.append("\n        LEFT JOIN cat_estatus e ON (m.id_estatus_mov  = e.id_estatus And e.clasificacion = 'MOV') ");
		         sb.append("\n        LEFT JOIN cat_caja cj ON (m.id_caja = cj.id_caja) ");
		         sb.append("\n        ,cat_cve_operacion cv ");
		         sb.append("\n        ,cat_forma_pago fp, cat_banco b ");
		     }else{
		         sb.append("\n   FROM movimiento m LEFT JOIN cat_banco b ON(m.id_banco_benef = b.id_banco)");
		         sb.append("\n        LEFT JOIN cat_estatus e ON (m.id_estatus_mov  = e.id_estatus And e.clasificacion = 'MOV')");
		         sb.append("\n        LEFT JOIN cat_caja cj ON (m.id_caja = cj.id_caja)");
		         sb.append("\n        ,cat_cve_operacion  cv ");
		         sb.append("\n        ,cat_forma_pago fp ");
		     }
		  //End Select
		 }
		     
		     sb.append("\n        ,empresa em ");
		     
		     if (datos.get("psDivision").equals(""))
		     {
		         sb.append("\n        ,grupo_empresa g ");
		         sb.append("\n  WHERE m.no_empresa = g.no_empresa");
		         sb.append("\n    AND m.no_empresa = em.no_empresa");
		     }else{
		         if (datos.get("psDivision").equals("TODAS LAS DIVISIONES"))
		         {
		             sb.append("\n  WHERE (m.division is not null and m.division <> '') ");
		             sb.append("\n    AND m.no_empresa = em.no_empresa");
		         }else{
		             sb.append("\n  WHERE m.division = '" + datos.get("psDivision") + "' ");
		             sb.append("\n    AND m.no_empresa = em.no_empresa");
		         }
		     }

		     if (!datos.get("grupoEmpresa").equals("0"))
		         sb.append("\n  AND g.id_grupo_flujo = " + datos.get("grupoEmpresa"));
		   //CHECAR SI ESTA CONDICION ES CORRECTA
		     //sb.append("\n  AND m.no_empresa in (" + datos.get("noEmpresa") + ")");
		     
		  //Select Case gsDBM
		     if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE") || gsDBM.equals("ORACLE"))
		     {
		     //Case "SQL SERVER", "SYBASE", "ORACLE"
		        if (gsDBM.equals("SYBASE"))
		            sb.append("\n    AND m.no_cliente = convert(varchar, p.no_proveedor) ");
		        else
		            sb.append("\n    AND m.no_cliente = p.no_proveedor ");
		                 
		        if (datos.get("tipoChequeNE").equals("true"))
		            sb.append("\n    AND m.id_banco_benef = b.id_banco");
		        else
		           // sb.append("\n    AND m.id_banco_benef *= b.id_banco");
		        
		        //sb.append("\n    AND m.id_estatus_mov  *= e.id_estatus");
		        sb.append("\n    AND m.id_tipo_movto = 'E'  ");
		        sb.append("\n    AND (m.origen_mov not in ('INV','FIL') or m.origen_mov is null)");
		        sb.append("\n    AND e.clasificacion='MOV' ");
		        sb.append("\n    AND m.id_tipo_operacion = co.id_tipo_operacion ");
		        sb.append("\n    AND co.no_empresa = 1");
		        sb.append("\n    AND m.id_cve_operacion = cv.id_cve_operacion  ");
		        sb.append("\n    AND m.id_estatus_mov in ('N','C','F') ");
		        sb.append("\n    AND m.id_forma_pago = fp.id_forma_pago ");
		      //  sb.append("\n    AND m.id_caja *= cj.id_caja ");
		        sb.append("\n    AND (m.fec_propuesta is null or m.fec_propuesta = '')");
		        //aqui
		        sb.append("\n    AND m.no_cliente not in(" + consultarConfiguraSet(206) + ")");
		     }

		     if(gsDBM.equals("POSTGRESQL"))
		     {
		     //Case "DB2", "POSTGRESQL"
		        if (datos.get("tipoChequeNE").equals("true"))
		            sb.append("\n    AND m.id_banco_benef = b.id_banco ");
		        
		        sb.append("\n    AND m.id_tipo_movto = 'E'");
		        sb.append("\n    AND (m.origen_mov not in ('INV','FIL') or m.origen_mov is null)");
		        sb.append("\n    AND m.id_estatus_mov in ('N','C','F')");
		        sb.append("\n    AND m.fec_propuesta is null");
		        sb.append("\n    AND m.id_forma_pago = fp.id_forma_pago");
		        sb.append("\n    AND cv.no_empresa = '1'");
		        sb.append("\n    AND m.id_cve_operacion = cv.id_cve_operacion  ");
		        sb.append("\n    AND m.no_cliente not in(" + consultarConfiguraSet(206) + ")");
		     }
		 
		     
		     if (datos.get("psDivision").equals(""))
		     {
		         sb.append("\n    AND m.no_empresa in ( SELECT no_empresa ");
		         sb.append("\n                          FROM usuario_empresa ");
		         sb.append("\n                          WHERE no_usuario = " + datos.get("usuario") + ")");
		     }else{
		         sb.append("\n    AND m.division in ( SELECT distinct id_division ");
		         sb.append("\n                        FROM usuario_division ");
		         sb.append("\n                        WHERE no_usuario = " + datos.get("usuario") + ")");
		     }
		     
		     if(!funFiltroCriterios.equals(""))
	    	 {
		     	sb.append("\n"+funFiltroCriterios);
	    	 }
		    sql = sb.toString();
		    System.out.println("importe Total:  "+sql);
			paramRetorno = jdbcTemplate.query(sql, new RowMapper<PropuestaPagoManualDto>(){
			public PropuestaPagoManualDto mapRow(ResultSet rs, int idx) throws SQLException {
				PropuestaPagoManualDto cons = new PropuestaPagoManualDto();
					cons.setImporte(rs.getDouble("importe"));
				return cons;
			}});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PropuestaPagoManualDaoImpl, M:sumarImportePropuestas");
		} 
		return paramRetorno.isEmpty()? 0 : paramRetorno.get(0).getImporte();
	}
	
	/**
	 * pFunFiltroCriterios
	 * complemento de la consulta de propuestas
	 * @param dto
	 * @param gsDBM
	 * @return
	 */
	
	public String filtroCriterios(Map<String, String> datos)
	{
		String sql="";
		StringBuffer sb = new StringBuffer();
		boolean pbFiltroFecha = false;
		String pvtFechaPropuesta = "";
		funciones = new Funciones();
		try{
				if(datos.get("chequeraBenef") != null && !datos.get("chequeraBenef").trim().equals(""))
				{
				//Case Is = "CHEQUERA BENEF."
					if(datos.get("chequeraBenef").trim().equals("EXTRANJERA"))
						sb.append(" AND b.nac_ext = 'E' ");
					else
						sb.append(" AND b.nac_ext = 'N' ");
				}
				
				if(datos.get("noDocumentoIni") != null && !datos.get("noDocumentoIni").trim().equals("") &&
						datos.get("noDocumentoFin") != null && !datos.get("noDocumentoFin").trim().equals(""))
				{
				//Case Is = "NUMERO DE DOCUMENTO"
	                if(datos.get("noDocumentoFin").trim().equals(""))
	                	//datos.get("noDocumentoFin") = datos.get("noDocumentoIni");
	                    //dto.setNoDocumentoFin(dto.getNoDocumentoIni());
	                sb.append(" and m.no_docto between '" + datos.get("noDocumentoIni") + "' and '" + datos.get("noDocumentoFin") + "'");
		            pbFiltroFecha = true;
				}
	            if(datos.get("fecha1")!= null && datos.get("fecha2")!= null && 
	            		!datos.get("fecha1").equals("") && !datos.get("fecha2").equals(""))
	            {
	            //Case Is = "FECHAS"
	                sb.append(" and m.fec_valor between '" + funciones.ponerFechaSola(datos.get("fecha1")) + "' and '" + funciones.ponerFechaSola(datos.get("fecha2")) + "'");
	            	if(!pvtFechaPropuesta.equals(""))
	                    sb.append(" and m.fec_valor <= '" + funciones.ponerFechaSola(pvtFechaPropuesta) + "'");
	            	pbFiltroFecha = true;
	            }
	           
				if(datos.get("estatus") != null && !datos.get("estatus").trim().equals(""))
				{
				//Case Is = "ESTATUS"
		            if(datos.get("estatus").trim().equals("CAPTURADO"))
		            	sb.append(" AND m.id_estatus_mov = 'C' ");
		            else if(datos.get("estatus").trim().equals("FILIAL"))
		            	sb.append(" AND m.id_estatus_mov = 'F' ");
		            else if(datos.get("estatus").trim().equals("IMPORTADO"))
	                    sb.append(" AND m.id_estatus_mov = 'N' ");
				}
				if(datos.get("monto1") != null && datos.get("monto2") != null && 
						!datos.get("monto1").trim().equals("0") && !datos.get("monto2").trim().equals("0") &&
						!datos.get("monto1").trim().equals("") && !datos.get("monto2").trim().equals(""))
				{
				//Case Is = "MONTOS"
		            sb.append(" and m.importe between " + datos.get("monto1") + " and " + datos.get("monto2"));
		            if (!datos.get("monto1").equals("0") && datos.get("monto2").equals("0"))
		                sb.append(" and m.importe <= " + datos.get("monto1"));
		            if (!datos.get("monto2").equals("0") && datos.get("monto1").equals("0"))
		                sb.append(" and m.importe >= " + datos.get("monto2"));
				}
				if(datos.get("bancoReceptor") != null && !datos.get("bancoReceptor").trim().equals("")&&
						!datos.get("bancoReceptor").trim().equals(""))
				{
				//Case Is = "BANCO RECEPTOR"
		            sb.append(" and m.id_banco_benef = " + datos.get("bancoReceptor"));
				}
				if(datos.get("bancoPagador") != null && !datos.get("bancoPagador").trim().equals("0") && 
						!datos.get("bancoPagador").trim().equals(""))
				{
				//Case Is = "BANCO PAGADOR"
		            sb.append(" and m.id_banco = " + datos.get("bancoPagador"));
				}
				if(datos.get("chequeraPagadora") != null && !datos.get("chequeraPagadora").trim().equals(""))
				{
				//Case Is = "CHEQUERA PAGADORA"
	                sb.append(" and m.id_chequera = '" + datos.get("chequeraPagadora") + "'");
				}
				if(datos.get("concepto") != null && !datos.get("concepto").trim().equals(""))
				{
				//Case Is = "CONCEPTO"
					sb.append(" and m.concepto LIKE '%" + datos.get("concepto").trim() + "%'");
				}
				if(datos.get("loteEntrada") != null && !datos.get("loteEntrada").trim().equals(""))
				{
				//Case Is = "LOTE ENTRADA"
					sb.append(" and m.lote_entrada = '" + datos.get("loteEntrada").trim() + "'");
				}
				if(datos.get("caja") != null && !datos.get("caja").trim().equals("") && 
						!datos.get("caja").trim().equals(""))
				{
				//Case Is = "CAJA"
		            sb.append(" and m.id_caja = " + datos.get("caja"));
				}
				if(datos.get("division") != null && !datos.get("division").trim().equals(""))
				{
				//Case Is = "DIVISION"
					sb.append(" and m.division = '" + datos.get("division").trim() + "'");
				}
				if(datos.get("formaPago") != null && !datos.get("formaPago").trim().equals("") )
				{
				//Case Is = "FORMA DE PAGO"
	                sb.append(" and m.id_forma_pago = " + datos.get("formaPago"));
				}
				if(datos.get("divisa") != null && !datos.get("divisa").trim().equals(""))
				{
				//Case Is = "DIVISA"
		            sb.append(" and m.id_divisa = '" + datos.get("divisa").trim() + "'");
				}
				if(datos.get("claveOperacion") != null && !datos.get("claveOperacion").trim().equals("") )
				{
		        //Case Is = "CLAVE OPERACION"
		            sb.append(" and m.id_tipo_operacion = " + datos.get("claveOperacion"));
				}
				if(datos.get("bloqueo") != null && !datos.get("bloqueo").trim().equals(""))
				{
		        //Case Is = "BLOQUEO"
					if(datos.get("bloqueo").equals("SI"))
		                sb.append(" and m.cod_bloqueo = 'S' ");
		            else{
		                sb.append(" and ( m.cod_bloqueo is null ");
		                sb.append("     or m.cod_bloqueo = 'N' ");
		                sb.append("     or m.cod_bloqueo = '' ) ");
		            }
				}
				if(datos.get("noProveedor1") != null && datos.get("noProveedor2") != null &&
						!datos.get("noProveedor1").trim().equals("") && 
						!datos.get("noProveedor2").trim().equals(""))
				{
		        //Case Is = "NUMERO DE PROVEEDOR"
		            sb.append(" and m.no_cliente in ");
		            sb.append("     ( SELECT no_persona ");
		            sb.append("       FROM persona ");
		            sb.append("       WHERE equivale_persona between '" + datos.get("noProveedor1").trim() + "'");
		            sb.append("           And '" + datos.get("noProveedor2").trim() + "' AND id_tipo_persona = 'P')");
				}
				if(datos.get("nombreProveedor") != null && !datos.get("nombreProveedor").trim().equals(""))
				{
		        //Case Is = "NOMBRE PROVEEDOR"
		            sb.append(" and m.beneficiario LIKE '" + datos.get("nombreProveedor").trim() + "%'");
				}
				if(datos.get("factura") != null && !datos.get("factura").trim().equals(""))
				{
		        //Case Is = "FACTURA"
		            sb.append(" and m.no_factura LIKE '%" + datos.get("factura").trim() + "%'");
				}
				if(datos.get("noPedido") != null && !datos.get("noPedido").equals("") )
				{
		        //Case Is = "NUMERO DE PEDIDO"
		            sb.append(" and m.no_pedido = " + datos.get("noPedido"));
				}
				if(datos.get("origenMovimiento") != null && !datos.get("origenMovimiento").trim().equals(""))
				{
		        //Case Is = "ORIGEN DEL MOVTO"
					if(datos.get("origenMovimiento").trim().equals("CXP"))
		                sb.append(" and (m.origen_mov = '' or m.origen_mov is null or m.origen_mov = 'CXP') ");
		            else
		                sb.append(" and m.origen_mov = '" + datos.get("origenMovimiento").trim() + "'");
				}
				if(datos.get("rubroMovimiento") != null && !datos.get("rubroMovimiento").trim().equals(""))
				{
		        //Case Is = "RUBRO DEL MOVIMIENTO"
		            sb.append(" and m.id_rubro = '" + datos.get("rubroMovimiento").trim() + "'");
				}
//				if(dto.getClaseDocumento() != null && !dto.getClaseDocumento().equals(""))
//				{
//		        //Case Is = "CLASE DE DOCUMENTO"
//		            sb.append(" and m.id_contable in ('" & Replace(Replace(Trim(prs_Lst(piInd + 1)), ",", "','"), " ", "") & "')");
//				}
		
		if(pbFiltroFecha == false)
			if(!pvtFechaPropuesta.equals(""))
		        sb.append(" and m.fec_valor <= '" + funciones.ponerFechaSola(pvtFechaPropuesta) + "'");
		
			sql = sb.toString();
		   // System.out.println("query complementaria: "+sql);
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PropuestaPagoManualDaoImpl, M:filtroCriterios");
		} 
		return sql;
	}
	
	/**
	 * FunSQLComboBancoPagador
	 * @param datos
	 * @return
	 */
	
	public List<LlenaComboGralDto> llenarComboBanco(Map<String,Object> datos){
		String sql="";
		String psDivision = "";
        int plBanco = 0;
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		try{
				sb.append(" SELECT id_banco as ID, desc_banco as DESCRIPCION ");
			    sb.append("\n   FROM cat_banco b ");
			    sb.append("\n  WHERE b.id_banco in ");
			    sb.append("\n         ( SELECT DISTINCT ccb.id_banco ");
			    
			    if(psDivision.equals(""))
			        sb.append("\n           FROM cat_cta_banco ccb ");
			    else
			        sb.append("\n           FROM cat_cta_banco ccb, cat_cta_banco_division ccbd ");
			    
			    sb.append("\n           WHERE ccb.tipo_chequera in ('P','M') ");
			    sb.append("\n               AND ccb.id_banco = b.id_banco ");
			    
			      if ((datos.get("idDivisa").toString().length()) < 4)
			          sb.append("\n               AND ccb.id_divisa = '" + datos.get("idDivisa") + "' ");
			      else
			          sb.append("\n               AND ccb.id_divisa = (Select id_divisa from cat_divisa where desc_divisa = '" + datos.get("idDivisa") + "')");
			    
			    sb.append("\n               AND ccb.no_empresa = " + datos.get("noEmpresa"));
			    
			    if (!psDivision.equals(""))
			    {
			        sb.append("\n           AND ccb.no_empresa = ccbd.no_empresa ");
			        sb.append("\n           AND ccb.id_chequera = ccbd.id_chequera ");
			        sb.append("\n           AND ccb.id_banco = ccbd.id_banco ");
			        sb.append("\n           AND ccbd.id_division = '" + psDivision + "' ");
			    }
			    
			    if (plBanco > 0)
			        sb.append("\n           AND ccb.id_banco = " + plBanco);
			    
			    sb.append("\n ) ");
		sql = sb.toString();
		//System.out.println("bancos "+sql);
		    //bitacora.insertarRegistro(sql);
		    listDatos= jdbcTemplate.query(sql, new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:PropuestaPagoManualDaoImpl, M:llenarComboBanco");
		}
		
		return listDatos;
	}
	
	/**
	 * FunSQLComboChequeraPagadora
	 * @param datos
	 * @return
	 */
	
	public List<LlenaComboGralDto> llenarComboChequera(Map<String,Object> datos){
		String sql="";
		String psDivision = "";
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		
		try{
			sb.append(" SELECT ccb.id_chequera as ID, ccb.id_chequera as DESCRIPCION ");
		    
		    if (psDivision.equals(""))
		        sb.append("\n   FROM cat_cta_banco ccb ");
		    else
		        sb.append("\n   FROM cat_cta_banco ccb, cat_cta_banco_division ccbd ");
		    
		    sb.append("\n  WHERE ccb.tipo_chequera in ('P','M')");
		    sb.append("\n    AND ccb.id_banco = " + datos.get("idBanco"));
		    
		      if((datos.get("idDivisa").toString().length()) < 4)
		          sb.append("\n    AND ccb.id_divisa = '" + datos.get("idDivisa") + "'");
		      else
		          sb.append("\n    AND ccb.id_divisa = (Select id_divisa from cat_divisa where desc_divisa = '" + datos.get("idDivisa") + "')");
		       
		    sb.append("\n    AND ccb.no_empresa = " + datos.get("noEmpresa"));
		    
		    if(!psDivision.equals(""))
		    {
		        sb.append("\n AND ccb.no_empresa = ccbd.no_empresa ");
		        sb.append("\n AND ccb.id_chequera = ccbd.id_chequera ");
		        sb.append("\n AND ccb.id_banco = ccbd.id_banco ");
		        sb.append("\n AND ccbd.id_division = '" + psDivision + "' ");
		    }
		sql = sb.toString();
		    System.out.println("chequera "+sql);
		
		    listDatos= jdbcTemplate.query(sql, new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setDescripcion(rs.getString("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:PropuestaPagoManualDaoImpl, M:llenarComboChequera");
		}
		
		return listDatos;
	}
	
	/**
	 * funSQLVerificaEstatusMovtos
	 * @param psFolios
	 * @return
	 */
	
	public List<EstatusMovimientosDto> verificarEstatusMovtos(String psFolios)
	{
		String sql="";
		StringBuffer sb = new StringBuffer();
		List<EstatusMovimientosDto> estatus= new ArrayList<EstatusMovimientosDto>();
		try
		{
			sb.append(" select count(*) as movimientos_marcados, ");
		    sb.append("\n (select count(*) from movimiento where no_folio_det in (" + psFolios + ") ");
		    sb.append("\n  and id_tipo_operacion = 3000 and id_estatus_mov in ('N', 'C')  ");
		    sb.append("\n  and (cve_control is null or cve_control = '')) as movimientos_correctos ");
		    sb.append("\n from movimiento where no_folio_det in (" + psFolios + ") ");
		    sql = sb.toString();
		    //System.out.println("verifica estatus movtos "+sql);
		    estatus = jdbcTemplate.query(sql, new RowMapper<EstatusMovimientosDto>(){
			public EstatusMovimientosDto mapRow(ResultSet rs, int idx) throws SQLException {
				EstatusMovimientosDto cons = new EstatusMovimientosDto();
				cons.setMovCorrectos(rs.getInt("movimientos_correctos"));
				cons.setMovMarcados(rs.getInt("movimientos_marcados"));
				return cons;
			}});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:PropuestaPagoManualDaoImpl, M:verificarEstatusMovtos");
		}
		return estatus;
	    
	}
	
	/**
	 * FunSQLFolioCupos
	 * @param usuario
	 * @return
	 */
	
	public int seleccionarFolioCupos(int usuario){
		int res = -1;
		StringBuffer sb = new StringBuffer();
		try{
			sb.append("SELECT folio_control   FROM cat_usuario  WHERE no_usuario = " + usuario);
			res = jdbcTemplate.queryForInt(sb.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PropuestaPagoManualDaoImpl, M:seleccionarFolioCupos");
		}
		if(res < 1){
			res = 1;
		}
		return res;
	}
	
	/**
	 * FunSQLUpdateFolioCupos
	 * @param usuario
	 * @return
	 */
	public int actualizarFolioCupos(int usuario)
	{
		int res = -1;
		
		try{StringBuffer sb = new StringBuffer();
		sb.append(" UPDATE cat_usuario ");
		sb.append("\n SET folio_control = folio_control + 1");
		sb.append("\n WHERE no_usuario = " + usuario);
		//System.out.println("actualiza folio cupos "+sb.toString());
			res = jdbcTemplate.update(sb.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PropuestaPagoManualDaoImpl, M:actualizarFolioCupos");
		}
		return res;
	}
	
	/**
	 * funSQLCreaCupoManual
	 * insert a la tabla seleccion_automatica_grupo
	 * @param dto
	 * @param psDivision
	 * @return
	 */
	public int crearCupoManual(PropuestaPagoManualDto dtoInsert, String psDivision)
	{
	
	/*
	 * Public Function funSQLCreaCupoManual(ByVal pviIdGrupo As Integer, _
                                     ByVal pvsFecProp As String, _
                                     ByVal pviIdGrupoFlujo As Integer, _
                                     ByVal pvdMontoMaximo As Double, _
                                     ByVal pvdCupoManual As Double, _
                                     ByVal pvdCupoAutomatico As Double, _
                                     ByVal pvdCupoTotal As Double, _
                                     ByVal pvsFecLimite As String, _
                                     ByVal pvsCveControl As String, _
                                     Optional psDivision As String = "")
								    Dim sSQL As String*/
		int res=0;
        String sql = "";
        StringBuffer sb = new StringBuffer();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        
		try{
		    sb.append(" INSERT into seleccion_automatica_grupo ( id_grupo, fecha_propuesta, ");
		    sb.append("\n     id_grupo_flujo, monto_maximo, cupo_manual, cupo_automatico, ");
		    sb.append("\n     cupo_total, fec_limite_selecc, cve_control, id_division ) ");
		    sb.append("\n VALUES (" + 0 );
		    sb.append("\n ,'" + formato.format(dtoInsert.getFecPropuesta()) + "'");
		    sb.append("\n ," + dtoInsert.getIdGrupoFlujo());
		    sb.append("\n ," + dtoInsert.getMontoMaximo());
		    sb.append("\n ," + 0);
		    sb.append("\n ," + dtoInsert.getImporteCupo());
		    sb.append("\n ," + dtoInsert.getImporteCupo());
		    sb.append("\n ,'" + formato.format(obtenerFechaHoy()) + "'");
		    sb.append("\n ,'" + dtoInsert.getCveControl() + "'");
		    if (psDivision.equals(""))
		        sb.append("\n , null");
		    else
		        sb.append("\n ,'" + psDivision + "'");
		    
		    sb.append("\n )");
		    System.out.println(sb.toString());
		    sql = sb.toString();
			res=jdbcTemplate.update(sql);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PropuestaPagoManualDaoImpl, M:crearCupoManual");
			e.printStackTrace();
			
			
		}
	        
	        return res;
	}
	
	/**
	 * funSQLUpdateFechaProp
	 * @param dtoUpdate
	 * @return int
	 */
	public int actualizarFechaProp(PropuestaPagoManualDto dtoUpdate) {
		StringBuffer sb = new StringBuffer();
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		boolean bComa = false;
		//por el momento son constantes
		boolean pbCompraTransfer = false;
		String psNuevaFechaPago = "";
		String gsDBM = ConstantesSet.gsDBM;
		
		try {	
	/*
	 *Public Function funSQLUpdateFechaProp(ByVal pvFecPago As Variant, ByVal pvvValor1 As Variant, _
                                      ByVal pvvValor2 As Variant, ByVal pvvValor3 As Variant, _
                                      ByVal pvvBanco As Integer, ByVal pvvChequera As String, _
                                      Optional pdTipoCambio As Double = -1, _
                                      Optional piCaja As Integer = -1, _
                                      Optional pbCompraTransfer As Boolean = False, _ //manejar como constantes
                                      Optional psNuevaFechaPago As String = "") As Long

								    'Recibe los par�metros necesarios para ejecutar la sentencia de la forma frmSelbanco
								    Dim sSQL As String
								    Dim bComa As Boolean
								    
								    On Error GoTo HayError*/
								    
				    sb.append("\n UPDATE movimiento  ");
				    sb.append("\n   SET ");
				    if (!dtoUpdate.getFecPropuesta().equals(""))
				    {
				        sb.append("\n       fec_propuesta = convert(datetime,'" + formato.format(dtoUpdate.getFecPropuesta()) + "',103)");
				        bComa = true;
				    }
				    
				    if(dtoUpdate.getCveControl() != null && !dtoUpdate.getCveControl().trim().equals(""))
				    {
				    	//System.out.println(dtoUpdate.getCveControl());
				        if(bComa)
				            sb.append("\n     ,");
				        sb.append("       cve_control = '" + dtoUpdate.getCveControl() + "'");
				        bComa = true;
				    }
				    
				    //System.out.println("is date: "+funciones.isDate("01/03/2011", false));
				    if(psNuevaFechaPago.equals(""))
				    {
				    //If Not IsDate(psNuevaFechaPago) Then
				    	if(bComa)
				            sb.append("\n     ,");
				        
				        sb.append("       id_banco = " + dtoUpdate.getIdBancoBenef() + ",");
				        sb.append("      id_chequera = '" + dtoUpdate.getIdChequeraBenef() + "'");
				        bComa = true;
				    }
				    
				    if(dtoUpdate.getFecPago() != null && !dtoUpdate.getFecPago().equals(""))
				    {
				    //If pvFecPago <> "" Then
				        if(bComa)
				            sb.append("\n     ,");
				       
				        sb.append(" fec_valor = convert(datetime,'" + formato.format(dtoUpdate.getFecPago()) + "',103) ");
				        bComa = true;
				    }
				    if(dtoUpdate.getTipoCambio() > -1)
				    {
				    //If pdTipoCambio <> -1 Then
				        if(bComa)
				            sb.append("\n     ,");
				        
				        sb.append(" tipo_cambio = " + dtoUpdate.getTipoCambio() + "");
				        bComa = true;
				    }
				    
				    if (!gsDBM.equals("DB2"))
				    {
				    	if(dtoUpdate.getGSIDCAJA() > -1)
				    	{
				    		//If piCaja <> -1 Then
					          if(bComa)
					              sb.append("\n     ,");
					          
					          sb.append(" id_caja = " + dtoUpdate.getGSIDCAJA() + "");
					          bComa = true;
				    	}
				    }
				    
				    if (pbCompraTransfer)
				    {
				        if(bComa)
				            sb.append("\n     ,");
				       
				        sb.append(" id_servicio_be = 'CVT'");
				        bComa = true;
				    }
				    
				    if (!psNuevaFechaPago.equals("")) // quiere decir que es un factoraje y se debe enviar esta fecha a sap para que reprograme el pago
				    {	
				    	if (bComa)
				            sb.append("\n     ,");
				      
				        sb.append("   id_servicio_be = 'FAC', fec_recalculo = convert(datetime,'" + funciones.ponerFecha(psNuevaFechaPago) + "',103)");
				        sb.append("\n , id_forma_pago = 7, id_banco = 0, id_chequera = '', id_banco_benef = " + dtoUpdate.getIdBancoBenef() + ", id_chequera_benef = ''");
				    }
				    
				    
				    sb.append("\n  WHERE (no_folio_det in (" + dtoUpdate.getNoFolioDet() + ") and id_tipo_operacion = 3000 )");
				    sb.append("\n     OR (folio_ref in (" + dtoUpdate.getNoFolioDet() + ") ");
				    sb.append("\n    and id_tipo_operacion = 3001)");
				   // System.out.println(sb.toString());				    
			return jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PropuestaPagoManualDaoImpl, M:actualizarFechaProp");
			return -1;
		}
	}
	
	
	public String muestraComponentes(int param){		
		StringBuffer sql = new StringBuffer();
		List<String> listCons = new ArrayList<String>();
		String msj = "";
		
		try{
			sql.append("SELECT valor as Campo FROM configura_set  WHERE indice = " + param);
			
			listCons= jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
				public String mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getString("campo");
				
			}});
			
			 if(listCons.size() > 0) 
	            	msj = listCons.get(0);
		   
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:PropuestaPagoManualDaoImpl, M:muestraComponentes");
		}
		return msj;
	}
	
	public List<PropuestaPagoManualDto> obtenerDivision (int usuario){
		List<PropuestaPagoManualDto> list = new ArrayList<PropuestaPagoManualDto>();
		String sql = "";
		String gsDBM = ConstantesSet.gsDBM;
		try{
		    sql +=" SELECT cd.id_division as id, ";
		    
		    if (gsDBM.equals("SQL SERVER"))
		        sql +="cd.desc_division as descrip ";
		    /*ElseIf gsDBM = "POSTGRESQL" Or gsDBM = "DB2" Then
		        sql +=" cast(e.no_empresa as varchar) || ' - ' || cd.desc_division as descrip "
		    ElseIf gsDBM = "SYBASE" Then
		        sql +=" convert(varchar, e.no_empresa) + ' - ' + cd.desc_division as descrip "
		    End If*/
		    
		    sql +=" FROM empresa e, cat_division cd ";
		    sql +="\n WHERE e.b_division = 'S' ";
		    sql +="\n     and cd.no_empresa = e.no_empresa ";
		    sql +="\n     and cd.no_empresa in ( SELECT no_empresa ";
		    sql +="\n                            FROM usuario_empresa ";
		    sql +="\n                            WHERE no_usuario = " + usuario + " ) ";
		    sql +="\n     and cd.id_division in ( SELECT ud.id_division ";
		    sql +="\n                             FROM usuario_division ud ";
		    sql +="\n                             WHERE ud.no_usuario = " + usuario;
		    sql +="\n                                 and ud.no_empresa = cd.no_empresa ) ";
		    sql +="\n ORDER BY e.no_empresa, cd.desc_division ";			
			
		    list = jdbcTemplate.query(sql, new RowMapper<PropuestaPagoManualDto>(){
		    	public PropuestaPagoManualDto mapRow(ResultSet rs, int idx) throws SQLException {
		    		PropuestaPagoManualDto cons = new PropuestaPagoManualDto();
		    		cons.setDivision(rs.getString("descrip"));
		    		return cons;
				}
		    });
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:PropuestaPagoManualDaoImpl, M:obtenerDivision");
		}
		
		return list;	
		
	}
	
	public List<PropuestaPagoManualDto> obtenerDivisaAct(int empresa, String division){
		List<PropuestaPagoManualDto> list = new ArrayList<PropuestaPagoManualDto>();
		String sql = "";
		try{		
		     sql +="     SELECT DISTINCT d.id_divisa as id, d.desc_divisa as descripcion ";
		     
		     if (division.equals("")) 
		         sql +=" FROM cat_cta_banco ccb, cat_divisa d ";
		     else{
		         sql +=" FROM cat_cta_banco ccb, cat_divisa d, ";
		         sql +="     cat_cta_banco_division ccbd ";
		     }
		     
		     sql +="     WHERE ccb.tipo_chequera in ('P','M') ";
		     sql +="         AND ccb.id_divisa = d.id_divisa ";
		     //'sql +="         AND ccb.no_empresa = " & CStr(pvi_NoEmpresa)
		     
		     if (!division.equals("")){
		         sql +="     AND ccb.no_empresa = ccbd.no_empresa ";
		         sql +="     AND ccb.id_chequera = ccbd.id_chequera ";
		         sql +="     AND ccb.id_banco = ccbd.id_banco ";
		         sql +="     AND ccbd.id_division = '" + division + "' ";
		     }
		     
		System.out.println(sql);
		list = jdbcTemplate.query(sql, new RowMapper<PropuestaPagoManualDto>(){
		    	public PropuestaPagoManualDto mapRow(ResultSet rs, int idx) throws SQLException {
		    		PropuestaPagoManualDto cons = new PropuestaPagoManualDto();
		    		cons.setIdDivisa(rs.getString("id"));
		    		cons.setDescDivisa(rs.getString("descripcion")); 
		    		return cons;
				}
		    });
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:PropuestaPagoManualDaoImpl, M:obtenerDivisaAct");
		}
		
		return list;	
		
	}
	
	
	public List<PropuestaPagoManualDto> SelectBancoCheqBenef(String cliente, String divisa, int banco){
		String sql = "";
		List<PropuestaPagoManualDto> list = new ArrayList<PropuestaPagoManualDto>();
		try{
		    sql+="SELECT id_banco as Banco, id_chequera as Chequera";
		    sql+="  FROM ctas_banco ";
		    sql+=" WHERE no_persona = " + cliente;
		    sql+="   AND id_divisa = '" + divisa + "' ";
		    sql+="   AND id_tipo_persona in ('P','K') ";
		    
		    if (banco > 0)
		        sql+=" AND id_banco = " + banco;
			
		    list = jdbcTemplate.query(sql, new RowMapper<PropuestaPagoManualDto>(){
		    	public PropuestaPagoManualDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		PropuestaPagoManualDto cons = new PropuestaPagoManualDto();
		    		cons.setIdBancoBenef(rs.getInt("Banco"));
		    		cons.setIdChequera(rs.getString("Chequera"));
		    		return cons;
		    	}
		    	
		    });	    
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Egresos, C:PropuestaPagoManualDaoImpl, M:SelectBancoCheqBenef");
		}
		return list;
		
	}
	
	
	public int UpdateBancoCheqBenef(String folio, int Banco, String Chequera){
		String sql = "";
		int res = 0;
		try{
		    sql+="UPDATE movimiento ";
		    sql+="   SET id_banco_benef = " + Banco + ",";
		    sql+="       id_chequera_benef = '" + Chequera + "',";
		    sql+="       id_leyenda = '1' ";
		    sql+=" WHERE no_folio_det = " + folio;
		    sql+="    OR folio_ref = " + folio;
		    
		    
		    res = jdbcTemplate.update(sql);
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Egresos, C:PropuestaPagoManualDaoImpl, M:UpdateBancoCheqBenef");
		}
		return res;
	}
	
	public int UpdateBancosCheqsBenef(int banco,String chequera, String folio){ 
		int res = 0;
		String sql = "";
		try{
			  	
			
				sql+="UPDATE movimiento ";
			    sql+="   SET id_banco_benef = " + banco + ",";
			    sql+="       id_chequera_benef = '" + chequera + "',";
			    sql+="       id_leyenda = '1' ";
			    sql+=" WHERE no_folio_det in (" + folio + ")";
			    sql+="    OR folio_ref in (" + folio + ")";
			
	    
		   res = jdbcTemplate.update(sql);
		   
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:UpdateBancosCheqsBenef");
		}     
		return res;
	}
	
	
	public List<LlenaComboGralDto> llenarComboChequeraBenef(Map<String,Object> datos){
		String sql="";
		String divisa = "";
		StringBuffer sb = new StringBuffer();
		StringBuffer ab = new StringBuffer();
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		try{      
			sb.append("SELECT id_chequera as ID, id_chequera as Descrip ");
			sb.append("  FROM ctas_banco");
			sb.append(" WHERE id_tipo_persona = 'P'");
			sb.append("   AND id_banco = " + datos.get("idBanco"));
			sb.append("   AND no_persona = " + datos.get("Perdona"));
			ab.append(datos.get("idDivisa"));
			divisa = ab.toString();
			if(!divisa.equals(""))
				sb.append("   AND id_divisa = '" + datos.get("idDivisa") + "'");
		sql = sb.toString();
		    //System.out.println(sql);
		    listDatos= jdbcTemplate.query(sql, new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setDescripcion(rs.getString("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:PropuestaPagoManualDaoImpl, M:llenarComboChequera");
		}
		
		return listDatos;
	}
	
	
	public List<PropuestaPagoManualDto> consultarPropuestasAgregar(int noGrupoEmpresa) {
		List<PropuestaPagoManualDto> list = new ArrayList<PropuestaPagoManualDto>();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT distinct sag.cve_control, (sag.cve_control + ' ' + convert(char(10), sag.fecha_propuesta, 103) + \n");
			sql.append(" 		'  MN: ' + cast(case when m.id_divisa = 'MN' then sag.cupo_total else 0 end as varchar(20)) + \n");
			sql.append(" 		'  DLS: ' + cast(case when m.id_divisa = 'DLS' then sag.cupo_total else 0 end as varchar(20)) + \n");
			sql.append(" 		'  ' + (select top 1 mm.concepto from movimiento mm where sag.cve_control = mm.cve_control and id_tipo_operacion = 3000)) as valor, \n");
			sql.append(" 		m.id_banco, m.id_chequera \n");
			sql.append(" FROM seleccion_automatica_grupo sag, movimiento m \n");
			sql.append(" WHERE sag.cve_control = m.cve_control \n");
			sql.append(" 	And m.no_empresa = sag.id_grupo_flujo \n");
			sql.append(" 	And sag.fecha_pago is null \n");
			sql.append("  	And m.id_tipo_operacion = 3000 \n");
			sql.append("  	And sag.id_grupo_flujo = "+ noGrupoEmpresa +" \n");
			sql.append("  	And sag.usuario_dos is null ");
			
			System.out.println("Query para traer propuestas existentes: " + sql.toString());
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper<PropuestaPagoManualDto>(){
				public PropuestaPagoManualDto mapRow(ResultSet rs, int idx) throws SQLException {
					PropuestaPagoManualDto cons = new PropuestaPagoManualDto();
					cons.setCveControl(rs.getString("cve_control"));
					cons.setConcepto(rs.getString("valor"));
					cons.setIdBanco(rs.getString("id_banco"));
					cons.setIdChequera(rs.getString("id_chequera"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PropuestaPagoManualDaoImpl, M:consultarPropuestasAgregar");
		}
		return list;
	}
	
	
	public List<PropuestaPagoManualDto> selectPropuestaAcutal(int noGrupoEmpresa, String cveControl) {
		List<PropuestaPagoManualDto> list = new ArrayList<PropuestaPagoManualDto>();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT coalesce(usuario_uno, 0) as usuario_uno, coalesce(usuario_dos, 0) as usuario_dos, \n");
			sql.append(" 		coalesce(usuario_tres, 0) as usuario_tres, fecha_propuesta, id_grupo, cve_control \n");
			sql.append(" FROM seleccion_automatica_grupo \n");
			sql.append(" WHERE cve_control = '"+ cveControl +"' \n");
			sql.append("  	And id_grupo_flujo = "+ noGrupoEmpresa +" \n");
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper<PropuestaPagoManualDto>(){
				public PropuestaPagoManualDto mapRow(ResultSet rs, int idx) throws SQLException {
					PropuestaPagoManualDto cons = new PropuestaPagoManualDto();
					cons.setUsr1(rs.getInt("usuario_uno"));
					cons.setUsr2(rs.getInt("usuario_dos"));
					cons.setUsr3(rs.getInt("usuario_tres"));
					cons.setFecPropuesta(rs.getDate("fecha_propuesta"));
					cons.setIdGrupoFlujo(rs.getInt("id_grupo"));
					cons.setCveControl(rs.getString("cve_control"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PropuestaPagoManualDaoImpl, M:selectPropuestaAcutal");
		}
		return list;
	}
	
	public int actualizarPropuesta(String cveControl, double importe, int noGrupoEmpresa) {
		int res = 0;
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" UPDATE seleccion_automatica_grupo SET monto_maximo += "+ importe +", cupo_automatico += "+ importe +", \n");
			sql.append(" 		cupo_total += "+ importe +" \n");
			sql.append(" WHERE cve_control = '"+ cveControl +"' \n");
			sql.append(" 	And id_grupo_flujo = "+ noGrupoEmpresa +" \n");
			
			res = jdbcTemplate.update(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PropuestaPagoManualDaoImpl, M:actualizarPropuesta");
		}
		return res;
	}
	
	
	public List<PropuestaPagoManualDto> buscaMovimientos(String sFolios) {
		List<PropuestaPagoManualDto> list = new ArrayList<PropuestaPagoManualDto>();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT * FROM movimiento \n");
			sql.append(" WHERE no_folio_det in ("+ sFolios +") \n");
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper<PropuestaPagoManualDto>(){
				public PropuestaPagoManualDto mapRow(ResultSet rs, int idx) throws SQLException {
					PropuestaPagoManualDto cons = new PropuestaPagoManualDto();
					cons.setNoFolioMov(rs.getInt("no_folio_mov"));
					cons.setImporte(rs.getDouble("importe"));
					return cons;
				}
			});
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PropuestaPagoManualDaoImpl, M:buscaMovimientos");
		}
		return list;
	}
	
	public int actualizarMovimientos(String cveControl, int noFolioMov, Date fecPropuesta, int idBanco, String idChequera) {
		int res = 0;
		StringBuffer sql = new StringBuffer();
		funciones = new Funciones();
		
		try {
			sql.append(" UPDATE movimiento SET id_banco = "+ idBanco +", id_chequera = '"+ idChequera +"', \n");
			sql.append(" 		cve_control = '"+ cveControl +"', fec_propuesta = convert(datetime,'" + funciones.ponerFecha(fecPropuesta) +"',103) \n");
			sql.append(" WHERE no_folio_mov = "+ noFolioMov +" \n");
			
			res = jdbcTemplate.update(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PropuestaPagoManualDaoImpl, M:actualizarMovimientos");
		}
		return res;
	}
	
	
	public List<PropuestaPagoManualDto> buscaBancoCheqPagadoras(String cveControl) {
		List<PropuestaPagoManualDto> list = new ArrayList<PropuestaPagoManualDto>();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT top 1 * FROM movimiento \n");
			sql.append(" WHERE cve_control = '"+ cveControl +"' \n");
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper<PropuestaPagoManualDto>() {
				public PropuestaPagoManualDto mapRow(ResultSet rs, int idx) throws SQLException {
					PropuestaPagoManualDto cons = new PropuestaPagoManualDto();
					cons.setIdBanco(rs.getString("id_banco"));
					cons.setIdChequera(rs.getString("id_chequera"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PropuestaPagoManualDaoImpl, M:buscaBancoCheqPagadoras");
		}
		return list;
	}
	
	public int obtenerNoCuenta(int empresa){
		System.out.println("entra al daoimpl");
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.obtenerCuenta(empresa);
	}
	
	public String consultarConfiguaraSet(int indice){
		consultasGenerales=new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
	}
	public boolean verificarDocumento(int noDocto, int empresa){
		StringBuffer sb = new StringBuffer();
		int res=0;
		try{
			sb.append("	SELECT no_docto from movimiento ");
			sb.append("\n	WHERE no_docto = '"+noDocto+"'");
			sb.append("\n	AND id_tipo_operacion = 3000 ");
			sb.append("\n	AND no_empresa = "+empresa);
			sb.append("\n	AND id_estatus_mov <> 'B'");
			res=jdbcTemplate.queryForInt(sb.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:CapturaSolicitudesPagoDao, M:verificarDocumento");
		}
		if(res>0)
			return true;
		else
			return false;
	}
	public int actualizarFolioReal(String tipoFolio){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.actualizarFolioReal(tipoFolio);
	}
	public int seleccionarFolioReal(String tipoFolio){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.seleccionarFolioReal(tipoFolio);
	}
	
	
	public String obtenerChequeraPagadora(String idDivisa){
		String sql="";
		@SuppressWarnings("unused")
		String idChequera="";
		List<String> listCheq=new ArrayList<String>();
		try{
		    sql+= "SELECT id_chequera ";
		    sql+="  FROM chequera_custodia ";
		    sql+= " WHERE id_tipo_docto = 47 ";
		    sql+= "   AND id_divisa = '" +idDivisa+ "'";
		    listCheq= jdbcTemplate.query(sql, new RowMapper<String>(){
				public String mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getString("id_chequera");
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ImportaBancaElectronicaDao, M:obtenerChequeraPagadora");
		}
		return listCheq.get(0);
	}
	
	
	public Map<String, Object> ejecutarGenerador(GeneradorDto generadorDto){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.ejecutarGenerador(generadorDto);
	}
	@Override
	public int actualizaPagoCompleto(String strNoFolioDet,double ImporteParcial, String strDescripcion) {
		
		int res = 0;
		
		try{
			
			
			StringBuffer sql = new StringBuffer();
			
			sql = new StringBuffer();
			
			sql.append(" UPDATE movimiento ");
			sql.append("\n   SET importe = (importe - " + ImporteParcial + ")");
			sql.append("\n	,observacion = '" + strDescripcion + "'");
			sql.append("\n	 WHERE no_folio_det = " + strNoFolioDet + "");
			System.out.println(sql.toString());
			res =jdbcTemplate.update(sql.toString());
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Egresos, C:CapturaSolicitudesPago, M:actualizaPagoCompleto");
		}
		
		return res;
	}
	
	
	@Override
	public int actualizaPagoCompleto3001(String strNoFolioMov,double ImporteParcial, String strDescripcion) {
		
		int res = 0;
		
		try{
			
			
			StringBuffer sql = new StringBuffer();
			
			sql = new StringBuffer();
			
			sql.append(" UPDATE movimiento ");
			sql.append("\n   SET importe = (importe - " + ImporteParcial + ")");
			sql.append("\n	,observacion = '" + strDescripcion + "'");
			sql.append("\n	 WHERE no_folio_mov = " + strNoFolioMov + "");
			sql.append("\n	 AND id_tipo_operacion = 3001");
			System.out.println(sql.toString());
			res =jdbcTemplate.update(sql.toString());
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Egresos, C:CapturaSolicitudesPago, M:actualizaPagoCompleto3001");
		}
		
		return res;
	}
	

	public int insertarParametro(ParametroDto dto)
	{
			funciones = new Funciones();    
			int res=0;
	        String sql = "";
	        StringBuffer sb = new StringBuffer();
	   try{
		   sb.append("\n	INSERT INTO parametro(no_empresa,no_docto,no_folio_param,id_tipo_docto,id_forma_pago");
		   sb.append("\n	,usuario_alta,id_tipo_operacion,no_cuenta,no_factura");
		   sb.append("\n	,fec_valor,fec_valor_original,fec_operacion,id_divisa");
		   sb.append("\n	,fec_alta,importe,importe_original,tipo_cambio");
		   sb.append("\n	,id_caja,id_divisa_original");
		   sb.append("\n	,referencia");
		   sb.append("\n	,beneficiario,concepto,id_banco_benef,id_chequera_benef,aplica");
		   sb.append("\n	,id_estatus_mov,b_salvo_buen_cobro,id_estatus_reg,id_leyenda");
		   sb.append("\n	,folio_ref");
		   sb.append("\n	,id_chequera");
		   sb.append("\n	,observacion,origen_mov,no_cliente");
		   sb.append("\n	,solicita, autoriza, plaza, sucursal");
		   sb.append("\n	,grupo, no_folio_mov, id_rubro,agrupa1,agrupa2,agrupa3");
		   sb.append("\n	,no_pedido");
		   sb.append("\n	,id_area");
		   sb.append("\n	,division");
		   sb.append("\n	,lote");
		   sb.append("\n	,id_grupo");
		   sb.append("\n	,invoice_type");
		   sb.append("\n	,id_banco");
		   sb.append(") ");
	                  
		   sb.append( "VALUES("+dto.getNoEmpresa()+",'"+dto.getNoDocto()+"',"+dto.getNoFolioParam()+"");
	       sb.append(" ,"+dto.getIdTipoDocto()+","+dto.getIdFormaPago()+","+dto.getUsuarioAlta()+"");
	       sb.append(" ,"+dto.getIdTipoOperacion()+","+dto.getNoCuenta()+",'"+dto.getNoFactura()+"'");
	       sb.append(" ,'"+funciones.ponerFecha(dto.getFecValor())+"','"+funciones.ponerFechaSola(dto.getFecValorOriginal())+"'");
	       sb.append(" ,'"+funciones.ponerFecha(dto.getFecOperacion())+"','"+dto.getIdDivisa()+"','"+funciones.ponerFecha(dto.getFecAlta())+"',"+dto.getImporte()+"");
	       sb.append(" ,"+dto.getImporteOriginal()+","+dto.getTipoCambio()+","+dto.getIdCaja()+",'"+dto.getIdDivisaOriginal()+"'");
	       sb.append(" ,'"+dto.getReferencia()+"'");
	       sb.append(" ,'"+dto.getBeneficiario()+"','"+dto.getConcepto()+"',"+dto.getIdBancoBenef()+"");
	       sb.append(" ,'"+dto.getIdChequeraBenef()+"',"+dto.getAplica()+",'"+dto.getIdEstatusMov()+"','"+dto.getBSalvoBuenCobro()+"','"+dto.getIdEstatusReg()+"','"+dto.getIdLeyenda()+"'");
	       sb.append(" ,"+dto.getFolioRef()+"");
	       sb.append(" ,'"+dto.getIdChequera()+"'");
	       sb.append(" ,'"+dto.getObservacion()+"'");
	       sb.append(" ,'"+dto.getAdu2()+"'");
	       sb.append(" ,'"+dto.getNoCliente()+"'");
	       sb.append(" ,'"+dto.getSolicita()+"','"+dto.getAutoriza()+"',"+dto.getPlaza()+","+dto.getSucursal()+"");
	       sb.append(" ,"+dto.getGrupo()+","+dto.getNoFolioMov()+","+dto.getIdRubroInt()+","+dto.getAgrupa1()+","+dto.getAgrupa2()+","+dto.getAgrupa3()+"");
	       sb.append(" ,"+dto.getNoPedido()+"");
		   sb.append(" ,"+dto.getIdArea()+"");
		   sb.append(" ,'"+dto.getDivision()+"'");
		   sb.append(" ,"+dto.getLote()+"");
	       sb.append(" ,"+dto.getIdGrupo()+"");
		   sb.append(" ,'"+dto.getPartida()+"'");
		   sb.append(" ," + dto.getIdBanco()+"");
		   sb.append( " )");
		   sql = sb.toString();
		   System.out.println(sb.toString());
		   res=jdbcTemplate.update(sql);
		   System.out.println("ejecuta");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ImportaBancaElectronicaDao, M:insertarParametro");
			e.printStackTrace();
		}
	        
	        return res;
	}
	
	public int actualizarClabe(String folio, String clabe){
		int folioMov=0, res=0;
		try{
			StringBuffer sql = new StringBuffer();
			sql.append("select no_folio_mov from movimiento where no_folio_det = '" + folio + "' ");
			folioMov = jdbcTemplate.queryForInt(sql.toString());
			if(folioMov>0)
			{
				sql = new StringBuffer();
				sql.append(" UPDATE movimiento ");
				sql.append("\n   SET clabe = '" + clabe + "'");
				sql.append("\n	, folio_ref =" + folio);//Se hace update a folio_ref, por que en generador no se ponia con mas de 2 detalles
				sql.append("\n	 WHERE no_folio_mov = '" +folioMov + "'");
				res=jdbcTemplate.update(sql.toString());
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Egresos, C:ImportaBancaElectronicaDao, M:actualizarClabe");
		}
		return res;
	}
	
	
	public List<PropuestaPagoManualDto> llenaComboGrupoConta(String idTipoMovto, String idSubGrupo) {
		StringBuffer sql = new StringBuffer();
		List<PropuestaPagoManualDto> resp = new ArrayList<PropuestaPagoManualDto> ();
		
		try{
			sql.append(" SELECT * FROM cat_sub_grupo ");
			sql.append("\n WHERE tipo_movto = '"+ idTipoMovto +"' ");
//			sql.append("\n		And id_sub_grupo not in (5, 6, 7, 16) ");
			sql.append("\n		And id_sub_grupo in ("+ idSubGrupo +") ");
			
			resp = jdbcTemplate.query(sql.toString(), new RowMapper<PropuestaPagoManualDto>(){
				public PropuestaPagoManualDto mapRow(ResultSet rs, int idx) throws SQLException {
					PropuestaPagoManualDto list = new PropuestaPagoManualDto();
					list.setIdSubGrupo(rs.getInt("id_sub_grupo"));
					list.setDescSubGrupo(rs.getString("desc_sub_grupo"));
					return list;
				}
			});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PropuestaPagoManualDaoImpl, M:llenaComboGrupoConta");
		}
		return resp;
	}
	
	
	public List<PropuestaPagoManualDto> llenaComboSubGrupo(int tipoGrupo) {
		StringBuffer sql = new StringBuffer();
		List<PropuestaPagoManualDto> resp = new ArrayList<PropuestaPagoManualDto> ();
		
		try{
			sql.append("SELECT * FROM cat_sub_sub_grupo WHERE id_sub_grupo = "+ tipoGrupo +" ");
			
			resp = jdbcTemplate.query(sql.toString(), new RowMapper<PropuestaPagoManualDto>(){
				public PropuestaPagoManualDto mapRow(ResultSet rs, int idx) throws SQLException {
					PropuestaPagoManualDto list = new PropuestaPagoManualDto();
					list.setIdSubSubGrupo(rs.getInt("id_sub_sub_grupo"));
					list.setDescSubSubGrupo(rs.getString("desc_sub_sub_grupo"));
					return list;
				}
			});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PropuestaPagoManualDaoImpl, M:llenaComboSubGrupo");
		}
		return resp;
	}
	
	
	public List<PropuestaPagoManualDto> llenaComboSubSubGrupo(int idRubroC) {
		StringBuffer sql = new StringBuffer();
		List<PropuestaPagoManualDto> resp = new ArrayList<PropuestaPagoManualDto> ();
		
		try{
			sql.append("SELECT * FROM cat_rubro_conta WHERE id_sub_sub_grupo = "+ idRubroC +" ");
			
			resp = jdbcTemplate.query(sql.toString(), new RowMapper<PropuestaPagoManualDto>(){
				public PropuestaPagoManualDto mapRow(ResultSet rs, int idx) throws SQLException {
					PropuestaPagoManualDto list = new PropuestaPagoManualDto();
					list.setIdRubroC(rs.getInt("id_rubro_c"));
					list.setDescRubroC(rs.getString("desc_rubro_c"));
					return list;
				}
			});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PropuestaPagoManualDaoImpl, M:llenaComboSubSubGrupo");
		}
		return resp;
	}
	
	
	public String buscaCtaContable(String datosConta) {
		StringBuffer sql = new StringBuffer();
		List<String> listCheq=new ArrayList<String>();
		Gson gson = new Gson();
		List<Map<String, String>> datos = gson.fromJson(datosConta, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
		try {
			sql.append(" SELECT top 1 coalesce(cta_contable, '') as cta_contable \n");
			sql.append(" FROM cat_ctas_contables \n");
			sql.append(" WHERE id_sub_grupo = "+ datos.get(0).get("idSubGrupo") +" \n"); 
			sql.append(" 	And id_sub_sub_grupo = "+ datos.get(0).get("idSubSubGrupo") +" \n"); 
			sql.append(" 	And id_rubro_c = "+ datos.get(0).get("idRubroC") +" \n"); 
			
			if(existeRubroCtaContable(Integer.parseInt(datos.get(0).get("idGrupo").toString()), Integer.parseInt(datos.get(0).get("idRubro").toString()), 
					Integer.parseInt(datos.get(0).get("idSubGrupo")), Integer.parseInt(datos.get(0).get("idSubSubGrupo")), Integer.parseInt(datos.get(0).get("idRubroC"))) != 0) {
				sql.append(" 	And id_grupo = "+ datos.get(0).get("idGrupo") +" \n");
				sql.append(" 	And id_rubro = "+ datos.get(0).get("idRubro") +" \n");
			}else {
				sql.append(" 	And (id_grupo = "+ datos.get(0).get("idGrupo") +" Or id_grupo = 0) \n");
				sql.append(" 	And (id_rubro = "+ datos.get(0).get("idRubro") +" Or id_rubro = 0) \n");
			}
			
			if(existeEmpresaCtaContable(Integer.parseInt(datos.get(0).get("noEmpresa").toString()),
					Integer.parseInt(datos.get(0).get("idGrupo").toString()), Integer.parseInt(datos.get(0).get("idRubro").toString())) != 0)
				sql.append(" 	And no_empresa like '%"+ datos.get(0).get("noEmpresa") +"%' \n");
			else
				sql.append(" 	And (no_empresa like '%"+ datos.get(0).get("noEmpresa") +"%' Or no_empresa = '0') \n");
			
			sql.append(" ORDER BY id_grupo desc ");
			
		    listCheq= jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
				public String mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getString("cta_contable");
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ImportaBancaElectronicaDao, M:obtenerChequeraPagadora");
		}
		return listCheq.get(0);
	}
	
	public int existeRubroCtaContable(int idGrupo, int idRubro, int subGrupo, int subSubGrupo, int rubroC) {
		StringBuffer sql = new StringBuffer();
		int resp = 0;
		
		try {
			sql.append(" SELECT count(*) \n");
			sql.append(" FROM cat_ctas_contables \n");
			sql.append(" WHERE id_grupo = "+ idGrupo +" \n"); 
			sql.append(" 	And id_rubro = "+ idRubro +" \n"); 
			sql.append(" 	And id_sub_grupo = "+ subGrupo +" \n"); 
			sql.append(" 	And id_sub_sub_grupo = "+ subSubGrupo +" \n"); 
			sql.append(" 	And id_rubro_c = "+ rubroC +" \n"); 
			
		    resp = jdbcTemplate.queryForInt(sql.toString());
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ImportaBancaElectronicaDao, M:obtenerChequeraPagadora");
		}
		return resp;
	}
	
	public int existeEmpresaCtaContable(int noEmpresa, int idGrupo, int idRubro) {
		StringBuffer sql = new StringBuffer();
		int resp = 0;
		
		try {
			sql.append(" SELECT count(*) \n");
			sql.append(" FROM cat_ctas_contables \n");
			sql.append(" WHERE id_grupo = "+ idGrupo +" \n"); 
			sql.append(" 	And id_rubro = "+ idRubro +" \n");
			sql.append(" 	And no_empresa = "+ noEmpresa +" \n");
			
		    resp = jdbcTemplate.queryForInt(sql.toString());
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ImportaBancaElectronicaDao, M:obtenerChequeraPagadora");
		}
		return resp;
	}
	
	
	public List<PropuestaPagoManualDto> llenarComboDepto(int noEmpresa) {
		List<PropuestaPagoManualDto> list = new ArrayList<PropuestaPagoManualDto> ();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT id_departamento as id, desc_departamento as descripcion \n");
			sql.append(" FROM cat_departamentos \n");
			sql.append(" WHERE no_empresa = (select noEmpresaCOI from equivale_empresa_SAE_COI where noEmpresaSAE = "+ noEmpresa +" ) \n");
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper<PropuestaPagoManualDto>(){
				public PropuestaPagoManualDto mapRow(ResultSet rs, int idx) throws SQLException {
					PropuestaPagoManualDto list = new PropuestaPagoManualDto();
					list.setId(rs.getInt("id"));
					list.setDescripcion(rs.getString("descripcion"));
					return list;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ImportaBancaElectronicaDao, M:llenarComboDepto");
		}
		return list;
	}
	
	
	public List<PropuestaPagoManualDto> llenarComboCCosto(int noEmpresa) {
		List<PropuestaPagoManualDto> list = new ArrayList<PropuestaPagoManualDto> ();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT centro_costo as id, desc_centro_costo as descripcion \n");
			sql.append(" FROM cat_centro_costo \n");
			sql.append(" WHERE no_empresa = (select noEmpresaCOI from equivale_empresa_SAE_COI where noEmpresaSAE = "+ noEmpresa +" ) \n");
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper<PropuestaPagoManualDto>(){
				public PropuestaPagoManualDto mapRow(ResultSet rs, int idx) throws SQLException {
					PropuestaPagoManualDto list = new PropuestaPagoManualDto();
					list.setId(rs.getInt("id"));
					list.setDescripcion(rs.getString("descripcion"));
					return list;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ImportaBancaElectronicaDao, M:llenarComboCCosto");
		}
		return list;
	}
	
	
	public List<PropuestaPagoManualDto> llenarComboProyecto(int noEmpresa) {
		List<PropuestaPagoManualDto> list = new ArrayList<PropuestaPagoManualDto> ();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT id_proyecto as id, desc_proyecto as descripcion \n");
			sql.append(" FROM cat_proyectos \n");
			sql.append(" WHERE no_empresa = (select noEmpresaCOI from equivale_empresa_SAE_COI where noEmpresaSAE = "+ noEmpresa +" ) \n");
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper<PropuestaPagoManualDto>(){
				public PropuestaPagoManualDto mapRow(ResultSet rs, int idx) throws SQLException {
					PropuestaPagoManualDto list = new PropuestaPagoManualDto();
					list.setId(rs.getInt("id"));
					list.setDescripcion(rs.getString("descripcion"));
					return list;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ImportaBancaElectronicaDao, M:llenarComboProyecto");
		}
		return list;
	}
	
	public int modificaImporteApagar(int noFolioDet, double importe) {
		StringBuffer sql = new StringBuffer();
		int resp = 0;
		
		try	{
			sql.append(" UPDATE movimiento ");
			sql.append("\n SET importe = "+ importe +", importe_original = "+ importe +" ");
			sql.append("\n WHERE no_folio_mov in (select top 1 no_folio_mov from movimiento where no_folio_det = "+ noFolioDet +") ");
			sql.append("\n 	And origen_mov = 'SET' ");
			sql.append("\n 	And (cve_control is null or cve_control = '') ");
			
			System.out.println("Update modifica importe: " + sql.toString());
			
			resp = jdbcTemplate.update(sql.toString());
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PropuestaPagoManualDaoImpl, M:modificaImporteApagar");
		}
		return resp;
	}
	
	//getters && setters
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PropuestaPagoManualDaoImpl, M:setDataSource");
		}
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}

