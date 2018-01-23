package com.webset.set.conciliacionbancoset.dao.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.webset.set.conciliacionbancoset.dao.ConciliacionBancoSetDao;
import com.webset.set.conciliacionbancoset.dto.ConciliaBancoDto;
import com.webset.set.conciliacionbancoset.dto.CriteriosBusquedaDto;
import com.webset.set.conciliacionbancoset.dto.CriteriosReporteDto;
import com.webset.set.conciliacionbancoset.dto.CruceConciliaDto;
import com.webset.set.conciliacionbancoset.dto.ParamBusquedaDto;
import com.webset.set.conciliacionbancoset.dto.MonitorConciliaGenDTO;
import com.webset.set.conciliacionbancoset.dto.TmpBancoSetMonitorDto;
import com.webset.set.conciliacionbancoset.dto.TmpBcoSetResumenDto;
import com.webset.set.conciliacionbancoset.dto.TmpConcenConciliaDto;
import com.webset.set.ingresos.dto.CuentaContableDto;
import com.webset.set.utilerias.dto.CatCtaBancoDto;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.ParametroDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;

/**
 * Clase de consultas del modulo de Conciliacion Banco-Set
 * @author Jessica Arelly Cruz Cruz
 * @since 07/10/2011
 *
 */
@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
public class ConciliacionBancoSetDaoImpl implements ConciliacionBancoSetDao{
	
	private JdbcTemplate jdbcTemplate; 
	private Bitacora bitacora = new Bitacora();
	private Funciones funciones = new Funciones();
	private GlobalSingleton globalSingleton;
	ConsultasGenerales consultasGenerales;
	
	/**
	 * FunSQLCombo3
	 * FunSQLCombo4
	 * FunSQLCombo7
	 * FunSQLCombo10
	 * FunSQLCombo30
	 * FunSQLCombo26
	 * @param iEmpresa
	 * @param sDivisa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarBancos(int iEmpresa){
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			if(iEmpresa == 0){
				sbSQL.append("\n Select id_banco as ID, desc_banco as Descrip From cat_banco");
		        sbSQL.append("\n Where id_banco in (Select distinct id_banco ");
		        sbSQL.append("\n from cat_cta_banco where ");
		        sbSQL.append("\n tipo_chequera not in(" + globalSingleton.obtenerValorConfiguraSet(202) + ")");
		        sbSQL.append("\n  ) ");
		        sbSQL.append("\n Order By desc_banco ");
			}else{
				sbSQL.append("\n Select id_banco as ID, desc_banco as Descrip From cat_banco");
		        sbSQL.append("\n Where id_banco in (Select distinct id_banco ");
		        sbSQL.append("\n from cat_cta_banco where ");
		        sbSQL.append("\n tipo_chequera not in(" + globalSingleton.obtenerValorConfiguraSet(202) + ") ");
		        sbSQL.append("\n and no_empresa = " + iEmpresa + "  )");
		        sbSQL.append("\n Order By desc_banco ");
			}
			System.out.println("Query"+sbSQL.toString());
			
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("Descrip"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarBancos");
		}
		return list;
	}
	
	/**
	 * FunSQLFechaConciliacion
	 * @param iBanco
	 * @param bOptEmpresa si es true es una empresa, false si es global
	 * @param iEmpresa
	 * @param iUsuario
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<CatCtaBancoDto> consultarFechaConciliacion(int iBanco, int bOptEmpresa, int iEmpresa, int iUsuario){
		List<CatCtaBancoDto> listFecha = new ArrayList<CatCtaBancoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			
			globalSingleton = GlobalSingleton.getInstancia();
	        if(ConstantesSet.gsDBM.equals("DB2"))
	        {
	            //Si el reporte es global, se utiliza la fecha menor en que se concili�
	        	if(bOptEmpresa == 0)
	                sbSQL.append("\n  select coalesce(min(fec_conciliacion),'2000-01-01') ");
	        	else //Si la conciliacion es de una empresa, se utiliza la �lima fecha de conciliacion de esa empresa
	                sbSQL.append("\n  select coalesce(max(fec_conciliacion),'2000-01-01') ");
	        }
	        else
	        {
	        	//Si el reporte es global, se utiliza la fecha menor en que se concili�
	        	if(bOptEmpresa == 0)
	                sbSQL.append("\n  select coalesce(min(fec_conciliacion),'01/01/2000') ");
	            else  //Si la conciliacion es de una empresa, se utiliza la �lima fecha de conciliacion de esa empresa
	                sbSQL.append("\n  select coalesce(max(fec_conciliacion),'01/01/2000') ");
	        }
		        
	        sbSQL.append("\n  as fec_conciliacion ");
		    sbSQL.append("\n  from cat_cta_banco ");
		    sbSQL.append("\n  where ");
		    sbSQL.append("\n  id_banco = " + iBanco);
		    sbSQL.append("\n  and tipo_chequera not in(" + globalSingleton.obtenerValorConfiguraSet(202) + ") ");
	
		    //If piValorOption = -1 Then
		    if(bOptEmpresa != 0)
		        sbSQL.append("\n  and no_empresa= " + bOptEmpresa);
		    else
		    {
		        sbSQL.append("\n  and no_empresa in ");
		        sbSQL.append("\n  (select no_empresa from usuario_empresa where no_usuario =" + iUsuario + ") ");
		    }
		    
		    System.out.println("Query"+sbSQL.toString());
		    
		    listFecha = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public CatCtaBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
					CatCtaBancoDto cons = new CatCtaBancoDto();
					cons.setFecConciliacion(rs.getDate("fec_conciliacion"));
					return cons;
				}});
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarFechaConciliacion");
		}
		return listFecha;
	}
	
	/**
	 * FunSQLSelect7
	 * @param iBanco
	 * @param bOptEmpresa
	 * @param iEmpresa
	 * @param iUsuario
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<CatCtaBancoDto> consultarChequerasConciliar(int noBanco, int noEmpresa, int noUsuario){
		List<CatCtaBancoDto> listChequeras = new ArrayList<CatCtaBancoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			
			sbSQL.append("\n  SELECT ccb.id_chequera, ccb.no_empresa, e.nom_empresa ");
	        sbSQL.append("\n  FROM cat_cta_banco ccb, empresa e, usuario_empresa u ");
	        sbSQL.append("\n  WHERE ccb.no_empresa = u.no_empresa ");
	        sbSQL.append("\n  and u.no_usuario = " + noUsuario + " and ccb.id_banco = " + noBanco);
	        sbSQL.append("\n  and ccb.no_empresa = e.no_empresa ");
	        sbSQL.append("\n  AND ccb.tipo_chequera not in(" + globalSingleton.obtenerValorConfiguraSet(202) + ") ");
	        
	        if(noEmpresa != 0)
	            sbSQL.append("\n  and ccb.no_empresa = " + noEmpresa);
	        sbSQL.append("\n Order By e.nom_empresa ");
	        
	        System.out.println("Query"+sbSQL.toString());
	        
	        listChequeras = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public CatCtaBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
					CatCtaBancoDto cons = new CatCtaBancoDto();
					cons.setIdChequera(rs.getString("id_chequera"));
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setDescSucursal(rs.getString("nom_empresa"));
					return cons;
				}});
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarChequerasConciliar");
		}
		return listChequeras;
	}
	
	
	@Transactional
	@SuppressWarnings("unchecked")
	public Map ejecutaSPconciliaBancoSet(ParamBusquedaDto dto)
	{
		Map resultado= new HashMap();
		try{
			StoredProcedure storedProcedure = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_conciliaBancoSet") {};
			
			storedProcedure.declareParameter(new SqlInOutParameter("idUsuario",Types.VARCHAR ));
			storedProcedure.declareParameter(new SqlParameter("idEmpresa",Types.INTEGER ));
			storedProcedure.declareParameter(new SqlInOutParameter("idBanco",Types.INTEGER ));
			storedProcedure.declareParameter(new SqlInOutParameter("idChequera",Types.VARCHAR ));
			storedProcedure.declareParameter(new SqlInOutParameter("fechaIni",Types.DATE ));
			storedProcedure.declareParameter(new SqlInOutParameter("fechaFin",Types.DATE ));
			storedProcedure.declareParameter(new SqlInOutParameter("mensaje",Types.VARCHAR));
			storedProcedure.declareParameter(new SqlInOutParameter("result",Types.INTEGER));
			
			HashMap< Object, Object > inParams = new HashMap< Object, Object >();
			inParams.put("idUsuario",dto.getIdUsuario());
			inParams.put("idEmpresa",dto.getIdEmpresa());
			inParams.put("idBanco",dto.getIdBanco());
			inParams.put("idChequera",dto.getIdChequera());
			inParams.put("fechaIni",dto.getFechaIni());
			inParams.put("fechaFin",dto.getFechaFin());
			inParams.put("mensaje",dto.getMensaje());
			inParams.put("result",dto.getResultado());
			
			resultado = storedProcedure.execute((Map)inParams);
				
			}catch (Exception e) {
				e.printStackTrace();
				bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
						+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:ejecutaSPconciliaBancoSet");
		}
		return resultado;
	}
	
	@Transactional
	@SuppressWarnings("unchecked")
	public String ejecutaSPconciliaBancoMovto(ParamBusquedaDto dto)
	{
		Map resultado= new HashMap();
		String result = "Error al conciliar.";
		try{/*
			StoredProcedure storedProcedure = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_concilia_banco_movto") {};
			
			storedProcedure.declareParameter(new SqlParameter("idUsuario",Types.INTEGER ));
			storedProcedure.declareParameter(new SqlParameter("idEmpresa",Types.INTEGER ));
			storedProcedure.declareParameter(new SqlParameter("idBanco",Types.INTEGER ));
			storedProcedure.declareParameter(new SqlParameter("idChequera",Types.VARCHAR ));
			storedProcedure.declareParameter(new SqlInOutParameter("resultado",Types.INTEGER));
			
			HashMap< Object, Object > inParams = new HashMap< Object, Object >();
			inParams.put("idUsuario",dto.getIdUsuario());
			inParams.put("idEmpresa",dto.getIdEmpresa());
			inParams.put("idBanco",dto.getIdBanco());
			inParams.put("idChequera",dto.getIdChequera());
			inParams.put("resultado",dto.getResultado());
			
			resultado = storedProcedure.execute((Map)inParams);
			*/
			
			
			StoredProcedure storedProcedure = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_concilia_banco_movto_ORIGINAL") {};
			
			storedProcedure.declareParameter(new SqlParameter("idUsuario",Types.INTEGER ));
//			storedProcedure.declareParameter(new SqlInOutParameter("resultado",Types.INTEGER));
			
			HashMap< Object, Object > inParams = new HashMap< Object, Object >();
			inParams.put("idUsuario",dto.getIdUsuario());
			//inParams.put("resultado",dto.getResultado());
			
			resultado = storedProcedure.execute((Map)inParams);
			result = "exito";
				
			}catch (Exception e) {
				e.printStackTrace();
				bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
						+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:ejecutaSPconciliaBancoMovto");
		}
		return result;
	}
	
	/**
	 * FunSQLCombo5
	 * FunSQLCombo8
	 * FunSQLCombo11
	 * FunSQLCombo31
	 * FunSQLCombo27
	 * FunSQLCombo34
	 * @param iBanco
	 * @param iEmpresa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboChequeraDto> consultarChequeras(int iBanco, int iEmpresa, int iOpc){
		List<LlenaComboChequeraDto> list = new ArrayList<LlenaComboChequeraDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			
			sbSQL.append("\n  Select");
	        sbSQL.append("\n  id_chequera as ID,");
	        sbSQL.append("\n  id_chequera as Descrip");
	        sbSQL.append("\n  From ");
	        sbSQL.append("\n  cat_cta_banco");
	        sbSQL.append("\n  Where ");
	        sbSQL.append("\n  id_banco = " + iBanco);
	        sbSQL.append("\n  and no_empresa = " + iEmpresa);
	        if(iOpc == 0)
	        	sbSQL.append("\n  AND tipo_chequera not in(" + globalSingleton.obtenerValorConfiguraSet(202) + ") ");
	        sbSQL.append("\n  Order By id_chequera ");   
	       
	        System.out.println("Query"+sbSQL.toString());
	        
		      list = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
					public LlenaComboChequeraDto mapRow(ResultSet rs, int idx) throws SQLException {
						LlenaComboChequeraDto cons = new LlenaComboChequeraDto();
						cons.setId(rs.getString("ID"));
						cons.setDescripcion(rs.getString("Descrip"));
						return cons;
					}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarChequeras");
		}
		return list;
	}
	
	/**
	 * FunSQLCombo6
	 * FunSQLCombo9
	 * FunSQLCombo12
	 * FunSQLCombo32
	 * FunSQLCombo28
	 * @param iBanco
	 * @param iEmpresa
	 * @param iOpc
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboChequeraDto> consultarEstatus(boolean bCancelacion){
		
		List<LlenaComboChequeraDto> list = new ArrayList<LlenaComboChequeraDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("Select id_estatus as ID, desc_estatus as Descripcion From cat_estatus");
	        sbSQL.append("\n  Where clasificacion = 'CON'");
	        if(!bCancelacion)
	        	sbSQL.append("\n  and id_estatus in ('P','D')");

	        System.out.println("Query"+sbSQL.toString());
	        
		      list = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
					public LlenaComboChequeraDto mapRow(ResultSet rs, int idx) throws SQLException {
						LlenaComboChequeraDto cons = new LlenaComboChequeraDto();
						cons.setId(rs.getString("ID"));
						cons.setDescripcion(rs.getString("Descripcion"));
						return cons;
					}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarEstatus");
		}
		return list;
	}
	
	/**
	 * FunSQLDelete3
	 * @param iUsuario
	 * @return
	 */
	public int borrarTmpControlConcilia(int iUsuario){
		StringBuffer sbSQL = new StringBuffer();
		int iDelete = 0;
		try{
			sbSQL.append("\n Delete");
	        sbSQL.append("\n from ");
	        sbSQL.append("\n tmp_control_concilia");
	        sbSQL.append("\n Where ");
	        sbSQL.append("\n usuario_alta = " + iUsuario);
	        
	        System.out.println("Query"+sbSQL.toString());
	        
	        iDelete = jdbcTemplate.update(sbSQL.toString());
	        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:borrarTmpControlConcilia");
		}
		return iDelete;
	}
	
	/**
	 * FunSQLSelect10
	 * @param iBanco
	 * @param sIdChequera
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarTmpControlConcilia(int iBanco, String sIdChequera, boolean bCancelacion){
		StringBuffer sbSQL = new StringBuffer();
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		try{
            if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
    		{
		        sbSQL.append("\n Select ");
		        sbSQL.append("\n c.nombre as nombre, c.paterno as appaterno, c.materno as apmaterno");
		        sbSQL.append("\n ,t.id_banco");
		        sbSQL.append("\n ,t.id_chequera");
		        sbSQL.append("\n From ");
		        sbSQL.append("\n cat_usuario c");
		        sbSQL.append("\n ,tmp_control_concilia t");
		        sbSQL.append("\n Where ");
		        sbSQL.append("\n c.no_usuario = t.usuario_alta");
		        sbSQL.append("\n And t.id_banco = " + iBanco);
		        sbSQL.append("\n And t.id_chequera = '" + sIdChequera + "'");
		        if(bCancelacion)
		        	sbSQL.append("\n And t.id_tipo_concilia = 'S' ");
		        else
		        	sbSQL.append("\n And t.id_tipo_concilia = 'F' ");
		        
		        System.out.println("Query"+sbSQL.toString());
    		}
            
            if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
    		{
		        sbSQL.append("\n Select ");
		        sbSQL.append("\n c.nombre || ' ' || c.paterno || ' ' || c.materno as usuario");
		        sbSQL.append("\n ,t.id_banco");
		        sbSQL.append("\n ,t.id_chequera");
		        sbSQL.append("\n From ");
		        sbSQL.append("\n cat_usuario c");
		        sbSQL.append("\n ,tmp_control_concilia t");
		        sbSQL.append("\n Where ");
		        sbSQL.append("\n c.no_usuario = t.usuario_alta");
		        sbSQL.append("\n And t.id_banco = " + iBanco);
		        sbSQL.append("\n And t.id_chequera = '" + sIdChequera + "'");
		        if(bCancelacion)
		        	sbSQL.append("\n And t.id_tipo_concilia = 'S' ");
		        else
		        	sbSQL.append("\n And t.id_tipo_concilia = 'F' ");
    		}
            
            listMap = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
		    	public Map mapRow(ResultSet rs, int idx)throws SQLException {
		    		Map<String, Object> map = new HashMap<String, Object>();
		    		map.put("nombre", rs.getInt("nombre"));
		    		map.put("apaterno", rs.getString("appaterno"));
		    		map.put("amaterno", rs.getInt("apmaterno"));
		    		map.put("idBanco", rs.getString("id_banco"));
		    		map.put("idChequera", rs.getInt("id_chequera"));
		    		return map;
		    	} 
		    });
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarTmpControlConcilia");
		}
		return listMap;
	}
	
	/**
	 * FunSQLInsert7
	 * @param idBanco
	 * @param sIdChequera
	 * @param iUsuario
	 * @return
	 */
	public int insertarTmpControlConcilia(int idBanco, String sIdChequera, int iUsuario, boolean bCancelacion){
		StringBuffer sbSQL = new StringBuffer();
		int iInserta = -1;
		try{
			sbSQL.append("\n  Insert ");
	        sbSQL.append("\n  into ");
	        sbSQL.append("\n  tmp_control_concilia ");
	        sbSQL.append("\n  Values ");
	        sbSQL.append("\n  ( " + idBanco);
	        sbSQL.append("\n ,'" + sIdChequera + "'");
	        if(bCancelacion)
	        	 sbSQL.append("\n , 'S' ");
	        else
	        	sbSQL.append("\n , 'F' ");
	        sbSQL.append("\n ," + iUsuario + ")");
	        System.out.println("Query"+sbSQL.toString());
		    iInserta = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:insertarTmpControlConcilia");
		}
		return iInserta;
	}
	
	/**
	 * FunSQLSelect9
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ConciliaBancoDto> consultarMovsBanco(CriteriosBusquedaDto dto){
		List<ConciliaBancoDto> listBanco = new ArrayList<ConciliaBancoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
	            sbSQL.append("\n  Select id_estatus_cb, fec_operacion, cargo_abono, importe,");
	            sbSQL.append("\n  referencia, secuencia, no_cheque,");
	            //sbSQL.append("\n  concepto,");
	            sbSQL.append("\n  coalesce((select desc_concepto from cat_concepto_banco where id_banco=concilia_banco.id_banco and id_concepto=concepto),concepto) concepto,");
	            sbSQL.append("\n  no_empresa,id_banco,id_chequera");
	            sbSQL.append("\n  From concilia_banco");
	            sbSQL.append("\n  where id_estatus_cb in ('P')");
	            
	            if(dto.getNoEmpresa() > 0)
	            	sbSQL.append("\n  and no_empresa = " + dto.getNoEmpresa());
	            
	            if(dto.getIdBanco() > 0)
	            	sbSQL.append("\n  and id_banco = " + dto.getIdBanco());
	            
	            if(dto.getIdChequera() != null && !dto.getIdChequera().equals(""))
	            	sbSQL.append("\n  and id_chequera = '" + dto.getIdChequera() + "'");
	            
	            if(dto.getFechaIni() != null && !dto.getFechaIni().equals(""))
	            {
		            if(dto.getFechaFin() != null && !dto.getFechaFin().equals(""))
		            {
		            	sbSQL.append("\n  and fec_operacion >= convert(datetime,'" + 
		            			funciones.ponerFechaSola(dto.getFechaIni()) + " 00:00:00', 103)" );
	        			sbSQL.append("\n  and fec_operacion <= convert(datetime,'" + 
		            			funciones.ponerFechaSola(dto.getFechaFin()) + " 23:59:59', 103)" );
		            }
		            else
        				sbSQL.append("\n  and convert(datetime,fec_operacion,103) = '" + funciones.ponerFechaSola(dto.getFechaIni()) + "'");
	            }
	            
	            if(dto.getEstatus() != null && !dto.getEstatus().equals(""))
	            	if(!dto.getEstatus().equals("T"))
	            		sbSQL.append("\n  and id_estatus_cb = '" + dto.getEstatus() + "'");
	                
               if(dto.getMontoIni() != 0)
               {
            	   if(dto.getMontoFin() != 0)
            	   {
            		   sbSQL.append("\n  and importe >= " + dto.getMontoIni() );
            		   sbSQL.append("\n  and importe <= " + dto.getMontoFin() );
            	   }
            	   else 
            		   sbSQL.append("\n  and importe = " + dto.getMontoIni() );
               }
               
               if(dto.getCargoAbono() != null && !dto.getCargoAbono().equals(""))
            	   sbSQL.append("\n  and cargo_abono = '" + dto.getCargoAbono() + "'");
            			   
	            sbSQL.append("\n  order by importe desc");
			
//			sbSQL.append("select count(*) from concilia_banco");
			
			System.out.println("ConciliacionBancoSetDaoImpl::consultarMovsBanco consulta de ingresos: [" + sbSQL.toString() +"]");
			
			listBanco = jdbcTemplate.query(sbSQL.toString(), new RowMapper<ConciliaBancoDto>(){
					public ConciliaBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
						ConciliaBancoDto cons = new ConciliaBancoDto();
						cons.setIdEstatuscb(rs.getString("id_estatus_cb"));
						cons.setFecOperacion(rs.getDate("fec_operacion"));
						cons.setsFecOperacion(new Funciones().ponerFecha(cons.getFecOperacion()));
						
						cons.setCargoAbono(rs.getString("cargo_abono").equals("E") ? "C" : "A");
						cons.setImporte(rs.getDouble("importe"));
						cons.setReferencia(rs.getString("referencia"));
						cons.setSecuencia(rs.getInt("secuencia"));
						cons.setNoCheque(rs.getInt("no_cheque"));
						cons.setConcepto(rs.getString("concepto"));
						cons.setNoEmpresa(rs.getInt("no_empresa"));
						cons.setIdBanco(rs.getInt("id_banco"));
						cons.setIdChequera(rs.getString("id_chequera"));
						return cons;
					}
				});
			
		}catch(Exception e){
			System.out.println("Error "+e);
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarMovsBanco");
		}
		return listBanco;
	}
	
	/**
	 * FunSQLSelect8
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<MovimientoDto> consultarMovsEmpresa(CriteriosBusquedaDto dto){
		List<MovimientoDto> listEmpresa = new ArrayList<MovimientoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
            sbSQL.append("\n Select id_estatus_cb, fec_valor, id_tipo_movto, importe,");
            sbSQL.append("\n referencia, no_folio_det, no_cheque, no_cuenta,id_divisa,");
            sbSQL.append("\n no_docto, concepto, no_factura, beneficiario, 'H' AS tabla,id_estatus_mov ");
            sbSQL.append("\n From hist_movimiento");
            sbSQL.append("\n where id_estatus_cb in ('P','N')");
            sbSQL.append("\n and no_empresa= " + dto.getNoEmpresa());
            sbSQL.append("\n and id_estatus_mov in ('I','K','A','R')");
            sbSQL.append("\n and id_tipo_operacion in (1016,1017,3107,3200,3700,3701,3705,3706,3708,3709,1018,3100,3101,3154,4001,4102,4103,4104)");
            
            if(dto.getIdBanco() > 0)
            	sbSQL.append("\n and id_banco = " + dto.getIdBanco());
            
            if(dto.getIdChequera() != null && !dto.getIdChequera().equals(""))
            	sbSQL.append("\n and id_chequera = '" + dto.getIdChequera() + "'");
            
            if(dto.getFechaIni() != null && !dto.getFechaIni().equals(""))
            {
	            if(!dto.getFechaFin().equals(""))
	            {
	            	sbSQL.append("\n  and fec_valor_original >= convert(datetime,'" + 
	            			funciones.ponerFechaSola(dto.getFechaIni()) + "', 103)" );
        			sbSQL.append("\n  and fec_valor_original <= convert(datetime,'" + 
	            			funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)" );
	            }
	            else
   				sbSQL.append("\n  and fec_valor_original = convert(datetime,'" + 
   						funciones.ponerFechaSola(dto.getFechaIni()) + "', 103)");
            }
            
            if(dto.getEstatus() != null && !dto.getEstatus().equals(""))
            	sbSQL.append("\n and id_estatus_cb = '" + dto.getEstatus() + "'");
            	
            if(dto.getMontoIni() != 0)
           {
        	   if(dto.getMontoFin() != 0)
        	   {
        		   sbSQL.append("\n  and importe >= " + dto.getMontoIni() );
        		   sbSQL.append("\n  and importe <= " + dto.getMontoFin() );
        	   }
        	   else 
        		   sbSQL.append("\n  and importe = " + dto.getMontoIni() );
           }
                    
            if(dto.getCargoAbono() != null && !dto.getCargoAbono().equals(""))
            	   sbSQL.append("\n  and id_tipo_movto = '" + dto.getCargoAbono() + "'");
            
            if(dto.getFormaPago() != 0)
            	   sbSQL.append("\n  and id_forma_pago = '" + dto.getFormaPago() + "'");
                    
            sbSQL.append("\n UNION ALL");
       
            sbSQL.append("\n Select id_estatus_cb, fec_valor, id_tipo_movto, importe,");
            sbSQL.append("\n referencia, no_folio_det, no_cheque, no_cuenta,id_divisa,");
            sbSQL.append("\n no_docto, concepto, no_factura, beneficiario ,'M' AS tabla,id_estatus_mov");
            sbSQL.append("\n From movimiento");
            sbSQL.append("\n where id_estatus_cb in ('P','N')");
            sbSQL.append("\n and no_empresa=" + dto.getNoEmpresa() +  "");
            sbSQL.append("\n and id_estatus_mov in ('I','K','A','T','R','P')");
            sbSQL.append("\n and id_tipo_operacion in (1016,1017,3107,3200,3700,3701,3705,3706,3708,3709,1018,3100,3101,3154,4001,4102,4103,4104)");
    
            if(dto.getIdBanco() > 0)
            	sbSQL.append("\n and id_banco = " + dto.getIdBanco());
            
            if(dto.getIdChequera() != null && !dto.getIdChequera().equals(""))
            	sbSQL.append("\n and id_chequera = '" + dto.getIdChequera() + "'");
            
            if(dto.getFechaIni() != null && !dto.getFechaIni().equals(""))
            {
	            if(!dto.getFechaFin().equals(""))
	            {
	            	sbSQL.append("\n  and fec_valor_original >= convert(datetime,'" + 
	            			funciones.ponerFechaSola(dto.getFechaIni()) + "', 103)" );
        			sbSQL.append("\n  and fec_valor_original <= convert(datetime,'" + 
	            				funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)" );
	            }
	            else
   				sbSQL.append("\n  and fec_valor_original = convert(datetime,'" + 
   						funciones.ponerFechaSola(dto.getFechaIni()) + "', 103)");
            }
            
            if(dto.getEstatus() != null && !dto.getEstatus().equals(""))
            	sbSQL.append("\n and id_estatus_cb = '" + dto.getEstatus() + "'");
            	
            if(dto.getMontoIni() != 0)
           {
        	   if(dto.getMontoFin() != 0)
        	   {
        		   sbSQL.append("\n  and importe >= " + dto.getMontoIni() );
        		   sbSQL.append("\n  and importe <= " + dto.getMontoFin() );
        	   }
        	   else 
        		   sbSQL.append("\n  and importe = " + dto.getMontoIni() );
           }
                    
            if(dto.getCargoAbono() != null && !dto.getCargoAbono().equals(""))
            	   sbSQL.append("\n  and id_tipo_movto = '" + dto.getCargoAbono() + "'");
            
            if(dto.getFormaPago() != 0)
            	   sbSQL.append("\n  and id_forma_pago = '" + dto.getFormaPago() + "'");
                             
            sbSQL.append("\n order by importe desc");
            
            System.out.println("El query es "+ sbSQL.toString());
	        
			listEmpresa = jdbcTemplate.query(sbSQL.toString(), new RowMapper<MovimientoDto>(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons = new MovimientoDto();
					cons.setIdEstatusCb(rs.getString("id_estatus_cb"));
					cons.setFecValor(rs.getDate("fec_valor"));
					
					cons.setFecValorStr(new Funciones().ponerFecha(cons.getFecValor()));
					
					cons.setIdTipoMovto(rs.getString("id_tipo_movto").equals("E") ? "C" : "A");
					cons.setImporte(rs.getDouble("importe"));
					cons.setReferencia(rs.getString("referencia"));
					cons.setNoFolioDet(rs.getInt("no_folio_det"));
					cons.setNoCheque(rs.getInt("no_cheque"));
					cons.setNoCuenta(rs.getInt("no_cuenta"));
					cons.setIdDivisa(rs.getString("id_divisa"));
					cons.setNoDocto(rs.getString("no_docto"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setNoFactura(rs.getString("no_factura"));
					cons.setBeneficiario(rs.getString("beneficiario"));
					cons.setTablaOrigen(rs.getString("tabla"));
					cons.setIdEstatusMov(rs.getString("id_estatus_mov"));
					return cons;
				}});
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarMovsEmpresa");
		}
		return listEmpresa;
	}
	
	/**
	 * FunSQLContabiliza
	 * @param sRubro
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarContabiliza(String sRubro){
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n SELECT coalesce(b_contabiliza,'N') as b_contabiliza ,coalesce(RTRIM(LTRIM(EQUIVALE_RP)),'') AS equivale_rp" );
		    sbSQL.append("\n FROM CAT_RUBRO ");
		    sbSQL.append("\n WHERE id_rubro = " + sRubro);
		    
		    System.out.println("Query"+sbSQL.toString());
		    
		    listMap = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
		    	public Map mapRow(ResultSet rs, int idx)throws SQLException {
		    		Map<String, Object> map = new HashMap<String, Object>();
		    		map.put("bContabiliza", rs.getString("b_contabiliza"));
		    		map.put("equivaleRP", rs.getString("equivale_rp"));
		    		return map;
		    	} 
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarContabiliza");
		}
		return listMap;
	}
	
	/**
	 * FunSQLSelectEmpDiv
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<CatCtaBancoDto> consultarEmpDiv(CriteriosBusquedaDto dto){
		List<CatCtaBancoDto> list = new ArrayList<CatCtaBancoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  SELECT id_divisa, no_empresa, id_banco, id_chequera from cat_cta_banco ");
	           sbSQL.append("\n  WHERE ");
	           
	           if(dto.getIdBanco() != 0)
	        	   sbSQL.append("\n  id_banco = " + dto.getIdBanco());
	           
	           if(!dto.getIdChequera().equals(""))
	        	   sbSQL.append("\n  and id_chequera = '" + dto.getIdChequera() + "'");
	           
	           System.out.println("Query"+sbSQL.toString());
	           
	           list = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
					public CatCtaBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
						CatCtaBancoDto cons = new CatCtaBancoDto();
						cons.setIdDivisa(rs.getString("id_divisa"));
						cons.setNoEmpresa(rs.getInt("no_empresa"));
						cons.setIdBanco(rs.getInt("id_banco"));
						cons.setIdChequera(rs.getString("id_chequera"));
						return cons;
					}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarEmpDiv");
		}
		return list;
	}
	
	/**
	 * FunSQLObtieneT
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarIngresoEgreso(CriteriosBusquedaDto dto){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  SELECT  coalesce (ingreso_egreso, '') as ingreso_egreso ");
		    sbSQL.append("\n  FROM cat_rubro ");
		    sbSQL.append("\n  WHERE ");
		    sbSQL.append("\n  id_grupo  = '" + dto.getIdGrupo() + "'");
		    sbSQL.append("\n  and id_rubro = '" + dto.getIdRubro() + "'");
		    
		    System.out.println("Query"+sbSQL.toString());
		    
		    list = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
		    	public Map mapRow(ResultSet rs, int idx)throws SQLException {
		    		Map<String, Object> map = new HashMap<String, Object>();
		    		map.put("ingresoEgreso", rs.getString("ingreso_egreso"));
		    		return map;
		    	} 
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarIngresoEgreso");
		}
		return list;
	}
	
	/**
	*
	* @param tipoFolio
	* @return int
	*
	* Selecciona el numero de folio de la tabla folio
	*/
	public int seleccionarFolioReal(String tipoFolio) {
		StringBuffer sbSQL = new StringBuffer();
		sbSQL.append(" SELECT num_folio ");
		sbSQL.append("\n FROM folio ");
		sbSQL.append("\n WHERE tipo_folio = '" + tipoFolio + "'");
		try {
			return jdbcTemplate.queryForInt(sbSQL.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:seleccionarFolioReal");
			return -1;
		}
	}

	/**
	 * 
	 * @param tipoFolio
	 * @return int
	 * 
	 * Actualiza el numero de folio de la tabla folio
	 */
	public int actualizarFolioReal(String tipoFolio) {
		StringBuffer sbSQL = new StringBuffer();
		sbSQL.append(" UPDATE folio ");
		sbSQL.append("\n SET num_folio = num_folio + 1 ");
		sbSQL.append("\n WHERE tipo_folio = '" + tipoFolio + "'");
		try {
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:actualizarFolioReal");
			return -1;
		}
	}
	
	/**
	 * FunSQLInserta11
	 * @param paramDto
	 * @return
	 */
	public int insertarParametro(ParametroDto paramDto){
		int iInserta = 0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  INSERT INTO parametro(no_empresa, no_folio_param, id_tipo_docto, ");
	        sbSQL.append("\n  id_forma_pago, id_tipo_operacion, usuario_alta, no_cuenta, no_cliente, ");
	        sbSQL.append("\n  fec_valor, fec_valor_original, fec_operacion, fec_alta, importe, ");
	        sbSQL.append("\n  importe_original, id_caja, id_divisa, id_divisa_original, origen_reg, ");
	        sbSQL.append("\n  referencia, concepto, aplica, id_estatus_mov, b_salvo_buen_cobro, ");
	        sbSQL.append("\n  id_estatus_reg,id_banco,id_chequera,folio_banco,origen_mov,observacion, ");
	        sbSQL.append("\n  id_inv_cbolsa,no_folio_mov,folio_ref,grupo, id_grupo,importe_desglosado,lote,hora_recibo ");
	        sbSQL.append("\n  ,division");
	        sbSQL.append("\n  ,id_rubro ");
	        sbSQL.append("\n  ,no_recibo ");
	        
	        if(!paramDto.getNoDocto().equals(""))
	            sbSQL.append("\n  ,no_docto ");
	        
            sbSQL.append("\n )");
	                
	        sbSQL.append("\n VALUES(" + paramDto.getNoEmpresa() + "," + paramDto.getNoFolioParam());
	        sbSQL.append("\n ," + paramDto.getIdTipoDocto() + "," + paramDto.getIdFormaPago() + "," + paramDto.getIdTipoOperacion() + ",");
	        sbSQL.append("\n " + paramDto.getUsuarioAlta() + "," + paramDto.getNoCuenta() + ",'" + paramDto.getNoCliente() + "'");
	        sbSQL.append("\n ,convert(datetime,'" + funciones.ponerFechaSola(paramDto.getFecValor()) + "', 103),  convert(datetime,'" + funciones.ponerFechaSola(paramDto.getFecValorOriginal()) + "', 103)");
	        sbSQL.append("\n ,convert(datetime,'" + funciones.ponerFechaSola(paramDto.getFecOperacion()) + "',103), convert(datetime,'" + funciones.ponerFechaSola(paramDto.getFecAlta()) + "',103)," + paramDto.getImporte() + ",");
	        sbSQL.append("\n " + paramDto.getImporteOriginal() + "," + paramDto.getIdCaja() + ",'" + paramDto.getIdDivisa() + "','" + paramDto.getIdDivisaOriginal() + "','" + paramDto.getOrigenReg() + "',");
	        sbSQL.append("\n '" + paramDto.getReferencia() + "','" + paramDto.getConcepto() + "'," + paramDto.getAplica() + ",'" + paramDto.getIdEstatusMov() + "','" + paramDto.getBSalvoBuenCobro() + "',");
	        sbSQL.append("\n '" + paramDto.getIdEstatusReg() + "'," + paramDto.getIdBanco() + ",'" + paramDto.getIdChequera() + "','" + paramDto.getFolioBanco() + "','" + paramDto.getOrigenMov() + "','" + paramDto.getObservacion() + "',");
	        sbSQL.append("\n " + paramDto.getIdInvCbolsa() + "," + paramDto.getNoFolioMov() + "," + paramDto.getFolioRef() + "," + paramDto.getGrupo() + "," + paramDto.getGrupo() + "," + paramDto.getImporteDesglosado() + "," + paramDto.getLote());
	        sbSQL.append("\n ,'" + paramDto.getHoraRecibo() + "','" + paramDto.getDivision() + "' ");
	        sbSQL.append("\n , '" + paramDto.getIdRubroInt() + "'");
	        sbSQL.append("\n , " + paramDto.getNoRecibo());
	        
	        if(!paramDto.getNoDocto().equals(""))
	            sbSQL.append("\n , '" + paramDto.getNoDocto() + "'");
	        
            sbSQL.append("\n )");
            System.out.println("El query para insertar ["+ sbSQL.toString() +"]");
            iInserta = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:insertarParametro");
		}
		return iInserta;
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public Map<String, Object> ejecutarGenerador(GeneradorDto dto)
	{
		Map<String, Object> resultado= new HashMap<String, Object>();
		try{
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			
			resultado = consultasGenerales.ejecutarGenerador(dto);
			
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
			+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:ejecutarGenerador U:"+dto.getIdUsuario()
			+ "	F:"+dto.getNomForma());
		}
		return resultado; 
	}
	
	/**
	 * FusqlUpdateCb
	 * @param iEmpresa
	 * @param iBanco
	 * @param sChequera
	 * @param sSecuencia
	 * @return
	 */
	public int actualizarConciliaBanco(int iEmpresa, int iBanco, String sChequera, int iSecuencia){
		int iUpdate = 0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  Update concilia_banco set id_estatus_cb = 'M'");
	        sbSQL.append("\n  Where no_empresa = " + iEmpresa);
	        sbSQL.append("\n  and id_banco = " + iBanco);
	        sbSQL.append("\n  and id_chequera  = '" + sChequera + "'");
	        sbSQL.append("\n  and secuencia = " + iSecuencia);
	        
	        System.out.println("Query"+sbSQL.toString());
	        
	        iUpdate  = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:actualizarConciliaBanco");
		}
		return iUpdate;
	}
	
	/**
	 * FusqlUpdateMovtoIdentificado
	 * @param iEmpresa
	 * @param iBanco
	 * @param sChequera
	 * @param iSecuencia
	 * @return
	 */
	public int actualizarMovtoIdentificado(int iEmpresa, int iBanco, String sChequera, int iSecuencia){
		int iUpdate = 0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  Update movto_identificado set estatus = 'C'");
			sbSQL.append("\n  Where no_empresa = " + iEmpresa);
	        sbSQL.append("\n  and id_banco = " + iBanco);
	        sbSQL.append("\n  and id_chequera  = '" + sChequera + "'");
	        sbSQL.append("\n  and no_folio = " + iSecuencia);
        
	        System.out.println("Query"+sbSQL.toString());
	        
	        iUpdate  = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:actualizarMovtoIdentificado");
		}
		return iUpdate;
	}
	
	/**
	 * FusqlUpdateCb_movimiento
	 * @param iFolioDet
	 * @param sGenConta
	 * @param uImporte
	 * @param dFecha
	 * @param sNomComputadora
	 * @param iBanco
	 * @param sChequera
	 * @param iUsuario
	 * @param sHora
	 * @param sReferencia
	 * @return
	 */
	public int actualizarCbMovimiento(int iFolioDet, String sGenConta, double uImporte, Date dFecha, String sNomComputadora,
										int iBanco, String sChequera, int iUsuario, String sHora, String sReferencia){
		int iUpdate = 0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n Update movimiento set id_estatus_cb = 'M',b_gen_conta='" + sGenConta +  "',");
	        sbSQL.append("\n usuario_modif=" + iUsuario + ",descripcion='" + sNomComputadora + "',");
	        sbSQL.append("\n hora_recibo='" + sHora + "'");
	        
	        if(!sReferencia.equals(""))
	        	sbSQL.append("\n ,referencia='" + sReferencia + "'");
	        
	        sbSQL.append("\n Where no_folio_det = " + iFolioDet);
	        sbSQL.append("\n and importe=" + uImporte); 
	        sbSQL.append("\n and fec_valor= convert(datetime,'" + funciones.ponerFechaSola(dFecha) + "', 103) ");
	        sbSQL.append("\n and id_banco=" + iBanco + "");
	        sbSQL.append("\n and id_chequera='" + sChequera + "'");
        
	        System.out.println("Actualizar CB MOVIMIENTO "+ sbSQL.toString());
	        
	        iUpdate  = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:actualizarCbMovimiento");
		}
		return iUpdate;
	}
	
	/**
	 * FunSQLInsert_cruceConcilia
	 * @param dto
	 * @return
	 */
	public int insertarCruceConcilia(CruceConciliaDto dto){
		int iInsert = 0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  Insert into cruce_concilia(grupo,secuencia,no_folio_1,no_folio_2,");
		    sbSQL.append("\n  fuente_movto,tipo_concilia,fec_alta,usuario_alta,no_empresa,id_banco, ");
		    sbSQL.append("\n  id_chequera,fec_exporta )");
		    sbSQL.append("\n  Values ");
		    sbSQL.append("\n  (" + dto.getGrupo() + "," + dto.getSecuencia() + "," + dto.getNoFolio1() + ",");
		    sbSQL.append("\n  " + dto.getNoFolio2() + ",'" + dto.getFuenteMovto() + "','" + dto.getTipoConcilia() + "',");
		    sbSQL.append("\n  convert(date,'" + funciones.ponerFechaSola(dto.getFecAlta()) + "',103)," + dto.getUsuarioAlta() + "," + dto.getNoEmpresa() + "," + dto.getIdBanco() + ",");
		    sbSQL.append("\n  '" + dto.getIdChequera() + "',NULL)");
		    
		    System.out.println("Query "+sbSQL.toString());
		    iInsert  = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:insertarCruceConcilia");
		}
		return iInsert;
	}
	
	/**
	 * FunSQLSelect11
	 * @param iBanco
	 * @param iEmpresa
	 * @param sChequera
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<CatCtaBancoDto> obtenerCuenta(int iBanco, int iEmpresa, String sChequera){
		List<CatCtaBancoDto> list = new ArrayList<CatCtaBancoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n select b.id_divisa, c.no_cuenta");
	        sbSQL.append("\n from cat_cta_banco b, cuenta c ");
	        sbSQL.append("\n Where b.no_empresa = " + iEmpresa);
	        sbSQL.append("\n and b.no_empresa = c.no_empresa");
	        sbSQL.append("\n and b.id_banco = " + iBanco);
	        sbSQL.append("\n and b.id_chequera = '" + sChequera + "'");
	        sbSQL.append("\n and c.id_tipo_cuenta = 'P'");
	        
	        System.out.println("Query "+sbSQL.toString());
	        list = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public CatCtaBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
					CatCtaBancoDto cons = new CatCtaBancoDto();
					cons.setIdDivisa(rs.getString("id_divisa"));
					cons.setNoEmpresa(rs.getInt("no_cuenta"));
					return cons;
				}});
	        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:obtenerCuenta");
		}
		return list;
	}
	
	/**
	 * FunSQLInsert6
	 * @param paramDto
	 * @return
	 */
	public int insertarParametro1(ParametroDto paramDto){
		int iInserta = 0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n insert into parametro ");
		    sbSQL.append("\n (");
		    sbSQL.append("\n no_empresa, secuencia, no_folio_param, no_folio_mov,");
		    sbSQL.append("\n aplica, id_tipo_operacion, no_cuenta,");
		    sbSQL.append("\n id_estatus_mov, id_estatus_reg,");
		    sbSQL.append("\n id_chequera, id_banco, id_caja,");
		    sbSQL.append("\n importe, importe_original, id_divisa, id_divisa_original,");
		    sbSQL.append("\n usuario_alta, fec_operacion, fec_valor, fec_valor_original, fec_alta,");
		    sbSQL.append("\n concepto, origen_reg,");
		    sbSQL.append("\n id_forma_pago, origen_mov, grupo, id_grupo, no_docto");
		    sbSQL.append("\n )");
		    sbSQL.append("\n VALUES ");
		    sbSQL.append("\n (");
		    sbSQL.append("\n " + paramDto.getNoEmpresa() + "," + paramDto.getSecuencia() + "," + paramDto.getNoFolioParam() + "," + paramDto.getNoFolioMov() + ",");
		    sbSQL.append("\n " + paramDto.getAplica() + "," + paramDto.getIdTipoOperacion() + "," + paramDto.getNoCuenta() + ",");
		    sbSQL.append("\n '" + paramDto.getIdEstatusMov() + "',  'P',");
		    sbSQL.append("\n '" + paramDto.getIdChequera() + "'," + paramDto.getIdBanco() + "," + paramDto.getIdCaja() + ",");
		    sbSQL.append("\n " + paramDto.getImporte() + ", " + paramDto.getImporteOriginal() + ",  '" + paramDto.getIdDivisa() + "', '" + paramDto.getIdDivisaOriginal() + "', ");
		    sbSQL.append("\n " + paramDto.getUsuarioAlta() + " " );
//		    sbSQL.append("\n "+ paramDto.getFecOperacion() + "', '" + paramDto.getFecValor() + "',");
//		    sbSQL.append("\n '" + paramDto.getFecValorOriginal() + "', '" + paramDto.getFecAlta() + "',");
	        sbSQL.append("\n ,convert(datetime,'" + funciones.ponerFechaSola(paramDto.getFecOperacion()) + "', 103),  convert(datetime,'" + funciones.ponerFechaSola(paramDto.getFecValor()) + "', 103)");
	        sbSQL.append("\n ,convert(datetime,'" + funciones.ponerFechaSola(paramDto.getFecValorOriginal()) + "',103), convert(datetime,'" + funciones.ponerFechaSola(paramDto.getFecAlta()) + "',103),");
		    sbSQL.append("\n '" + paramDto.getConcepto() + "',  'CONSULTA DE MOVIMIENTO',");
		    sbSQL.append("\n "  +paramDto.getIdFormaPago() + ", '" + paramDto.getOrigenMov() + "'," + paramDto.getGrupo() + "," + paramDto.getGrupo() +", "+ paramDto.getNoDocto());
		    sbSQL.append("\n )");
		    
		    System.out.println("Query RRubio15"+sbSQL.toString());
		    
		    iInserta  = jdbcTemplate.update(sbSQL.toString());
//		    System.out.println("Termina la generación sin error "+ sbSQL.toString());
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:insertarParametro1");
		}
		return iInserta;
	}
	
	/**
	 * FunSQLUpdate5
	 * @param sEstatus
	 * @param iGrupo
	 * @param dFecha
	 * @return
	 */
	public int actualizarHistMovimiento(String sEstatus, int iGrupo, Date dFecha){
		int iUpdate = 0;
		StringBuffer sbSQL;
		try
		{
			sbSQL = new StringBuffer();
	        sbSQL.append("\n Update hist_movimiento set id_estatus_cb = '" + sEstatus + "'");
	        sbSQL.append("\n  Where no_folio_det in ( Select no_folio_1 from cruce_concilia ");
	        sbSQL.append("\n where grupo = " + iGrupo + ")");
	        System.out.println("Query "+sbSQL.toString());
	        iUpdate = jdbcTemplate.update(sbSQL.toString());

        	sbSQL = new StringBuffer();
	        sbSQL.append("\n Update movimiento set id_estatus_cb = '" + sEstatus + "'");
	        sbSQL.append("\n  , ID_ESTATUS_MOV = CASE WHEN ID_TIPO_OPERACION = 3200 AND id_forma_pago IN(3,5) and id_estatus_mov  IN('T') THEN 'K' ELSE id_estatus_mov end , fec_conf_trans = convert(date,'" + funciones.ponerFechaSola(dFecha) + "',103)");
	        sbSQL.append("\n  Where no_folio_det in ( Select no_folio_1 from cruce_concilia where grupo = " + iGrupo + ")");
	        System.out.println("Query "+sbSQL.toString());      
	        iUpdate = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:actualizarHistMovimiento");
		}
		return iUpdate;
	}
	
	/**
	 * FunSQLUpdate6
	 */
	public int actualizarConciliaBanco(String sEstatus, String sAclaracion, int iEmpresa, int iBanco, String sChequera, int iGrupo){
		int iUpdate = 0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n Update concilia_banco set id_estatus_cb = '"+sEstatus+"'");
	        sbSQL.append("\n , aclaracion = '" + sAclaracion + "'");
	        sbSQL.append("\n  Where no_empresa = " + iEmpresa);
	        sbSQL.append("\n    And id_banco = " + iBanco);
	        sbSQL.append("\n    And id_chequera = '" + sChequera + "'");
	        sbSQL.append("\n    and secuencia in (Select no_folio_2 from cruce_concilia where grupo = " + iGrupo + ")");
	        
	        System.out.println("Query "+sbSQL.toString());
	        
	        iUpdate = jdbcTemplate.update(sbSQL.toString());
	        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:actualizarConciliaBanco");
		}
		return iUpdate;
	}
	
	/**
	 * FunSqlActualizaEstatusMI
	 * @param iEmpresa
	 * @param iBanco
	 * @param sChequera
	 * @param iGrupo
	 * @return
	 */
	public int actualizarEstatusMI(int iEmpresa, int iBanco, String sChequera, int iGrupo){
		int iUpdate = 0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n Update movto_identificado set estatus = 'C'");
	        sbSQL.append("\n Where no_empresa = " + iEmpresa);
	        sbSQL.append("\n   And id_banco = " + iBanco);
	        sbSQL.append("\n   And id_chequera = '" + sChequera + "'");
	        sbSQL.append("\n   and no_folio in (Select no_folio_2 from cruce_concilia where grupo = " + iGrupo + ")");

	        
	        System.out.println("Query "+sbSQL.toString());
	        iUpdate = jdbcTemplate.update(sbSQL.toString());
	        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:actualizarEstatusMI");
		}
		return iUpdate;
	}
	
	/**
	 * FunSQLSelect26
	 * consulta del grid movimientos del banco de la pantalla de cancelacion de conciliaciones
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ConciliaBancoDto> consultarCancMovsBanco(CriteriosBusquedaDto dto){
		StringBuffer sbSQL = new StringBuffer();
		List<ConciliaBancoDto> listBanco = new ArrayList<ConciliaBancoDto>();
		try{
	        sbSQL.append("\n  Select ");
	        sbSQL.append("\n  Coalesce(c.grupo,0) as grupo");
	        sbSQL.append("\n ,b.id_estatus_cb");
	        sbSQL.append("\n ,b.fec_operacion");
	        sbSQL.append("\n ,b.cargo_abono");
	        sbSQL.append("\n ,b.importe");
	        sbSQL.append("\n ,b.referencia");
	        sbSQL.append("\n ,b.secuencia");
	        sbSQL.append("\n ,b.no_cheque ");
	        sbSQL.append("\n  From ");
	        sbSQL.append("\n  concilia_banco b Left Join cruce_concilia c  On (b.secuencia = c.no_folio_2) ");
	        sbSQL.append("\n  Where b.id_estatus_cb not in ('D','P') ");
	       
	        if(dto.getNoEmpresa() != 0)
            	sbSQL.append("\n  and b.no_empresa = " + dto.getNoEmpresa());
	        
	        if(dto.getIdBanco() != 0)
	        	sbSQL.append("\n  and b.id_banco = " + dto.getIdBanco());
	        
	        if(dto.getIdChequera() != null && !dto.getIdChequera().equals(""))
	        	sbSQL.append("\n  and b.id_chequera = '" + dto.getIdChequera() + "'");
	        
	        if(dto.getFechaIni() != null && !dto.getFechaIni().equals(""))
	        {
	        	if(dto.getFechaFin() != null && !dto.getFechaFin().equals(""))
	        	{
	        		sbSQL.append("\n  and b.fec_operacion >= convert(datetime,'"  + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103)");
	        		sbSQL.append("\n  and b.fec_operacion <= convert(datetime,'"  + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)");
	        	}
	        	else
	        		sbSQL.append("\n  and b.fec_operacion = '" + dto.getFechaIni() + "'");
	        }
	        
	        if(dto.getEstatus() != null && !dto.getEstatus().equals(""))
	        	sbSQL.append("\n  and b.id_estatus_cb = '" + dto.getEstatus() + "'");
	        
	        if(dto.getMontoIni() != 0)
	        {
        	   if(dto.getMontoFin() != 0)
        	   {
        		   sbSQL.append("\n  and float8(b.importe) >= " + dto.getMontoIni() );
        		   sbSQL.append("\n  and float8(b.importe) <= " + dto.getMontoFin() );
        	   }
        	   else 
        		   sbSQL.append("\n  and float8(b.importe) = " + dto.getMontoIni() );
	        }
	        
	        if(dto.getGrupoIni() != 0)
	        {
	        	if(dto.getGrupoFin() != 0)
	        	{
	        		sbSQL.append("\n  and c.grupo between " + dto.getGrupoIni());
	        		sbSQL.append("\n  and " + dto.getGrupoFin());
	        	}
	        	else
	        		sbSQL.append("\n  and c.grupo = " + dto.getGrupoIni());
	        }
               
               if(dto.getCargoAbono() != null && !dto.getCargoAbono().equals(""))
            	   sbSQL.append("\n  and cargo_abono = '" + dto.getCargoAbono() + "'");
	        
	        sbSQL.append("\n  order by b.importe desc");
			
	        System.out.println("Query "+sbSQL.toString());
	        
			listBanco = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public ConciliaBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConciliaBancoDto cons = new ConciliaBancoDto();
					cons.setGrupo(rs.getInt("grupo"));
					cons.setIdEstatuscb(rs.getString("id_estatus_cb"));
					cons.setFecOperacion(rs.getDate("fec_operacion"));
					
					cons.setsFecOperacion(new Funciones().ponerFecha(cons.getFecOperacion()));
					
					System.out.println("La fecha de operación en DATE es "+ cons.getFecOperacion());
					System.out.println("La fecha de operación es "+ cons.getsFecOperacion());
					
					cons.setCargoAbono(rs.getString("cargo_abono").equals("E") ? "C" : "A");
					cons.setImporte(rs.getDouble("importe"));
					cons.setReferencia(rs.getString("referencia"));
					cons.setSecuencia(rs.getInt("secuencia"));
					cons.setNoCheque(rs.getInt("no_cheque"));
					return cons;
				}});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarCancMovsBanco");
		}
		return listBanco;
	}
	
	/**
	 * FunSQLSelect27
	 * @param iGrupo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<CruceConciliaDto> consultarExportado(int iGrupo){
		List<CruceConciliaDto> list = new ArrayList<CruceConciliaDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  select");
		    sbSQL.append("\n  count(*) as grupo");
		    sbSQL.append("\n  from");
		    sbSQL.append("\n  cruce_concilia");
		    sbSQL.append("\n  where");
		    sbSQL.append("\n  grupo = " + iGrupo);
		    sbSQL.append("\n  and fec_exporta is not null");
		    
		    System.out.println("Query RRubio21"+sbSQL.toString());
		    
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public CruceConciliaDto mapRow(ResultSet rs, int idx) throws SQLException {
					CruceConciliaDto cons = new CruceConciliaDto();
					cons.setGrupo(rs.getInt("grupo"));
					return cons;
				}});
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarExportado");
		}
		return list;
	}
	
	/**
	 * FunSQLSelect24
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<MovimientoDto> consultarCancMovsEmpresa(CriteriosBusquedaDto dto){
		List<MovimientoDto> listEmpresa = new ArrayList<MovimientoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			/*if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
			{
	            sbSQL.append("\n  Select isnull(c.grupo,0) as grupo, b.id_estatus_cb, b.fec_valor, b.id_tipo_movto ");
	            sbSQL.append("\n  , b.importe,b.referencia, b.no_folio_det, b.no_cheque, b.id_divisa ");
	            sbSQL.append("\n  From ");
	            sbSQL.append("\n  hist_movimiento b, cruce_concilia c");
	            sbSQL.append("\n  where b.no_folio_det *= c.no_folio_1");
	            sbSQL.append("\n  and b.id_estatus_cb not in ('D','N','P') ");
	            
	            if(dto.getNoEmpresa() != 0)
	            	sbSQL.append("\n  and b.no_empresa = " + dto.getNoEmpresa());
	            
	            if(dto.getIdBanco() != 0)
		        	sbSQL.append("\n  and b.id_banco = " + dto.getIdBanco());
		        
		        if(dto.getIdChequera() != null && !dto.getIdChequera().equals(""))
		        	sbSQL.append("\n  and b.id_chequera = '" + dto.getIdChequera() + "'");
		        
		        if(dto.getFechaIni() != null && !dto.getFechaIni().equals(""))
		        {
		        	if(dto.getFechaFin() != null && !dto.getFechaFin().equals(""))
		        	{
		        		sbSQL.append("\n  and b.fec_operacion >= '"  + funciones.ponerFechaSola(dto.getFechaIni()) + "'");
		        		sbSQL.append("\n  and b.fec_operacion <= '"  + funciones.ponerFechaSola(dto.getFechaFin()) + "'");
		        	}
		        	else
		        		sbSQL.append("\n  and b.fec_operacion = '" + funciones.ponerFechaSola(dto.getFechaIni()) + "'");
		        }
		        
		        if(dto.getEstatus() != null && !dto.getEstatus().equals(""))
		        	sbSQL.append("\n  and b.id_estatus_cb = '" + dto.getEstatus() + "'");
		        
		        if(dto.getMontoIni() != 0)
		        {
		        	if(dto.getMontoFin() != 0)
		        	{
		        		sbSQL.append("\n  and b.importe_original >= " + dto.getMontoIni());
		        		sbSQL.append("\n  and b.importe_original <= " + dto.getMontoFin());
		        	}
		        	else
		        		sbSQL.append("\n  and b.importe_original = " + dto.getMontoIni());
		        }
		        	
		        if(dto.getGrupoIni() != 0)
		        {
		        	if(dto.getGrupoFin() != 0)
		        	{
		        		sbSQL.append("\n  and c.grupo between " + dto.getGrupoIni());
		        		sbSQL.append("\n  and " + dto.getGrupoFin());
		        	}
		        	else
		        		sbSQL.append("\n  and c.grupo = " + dto.getGrupoIni());
		        	sbSQL.replace(0, sbSQL.length(), sbSQL.toString().replace("*", ""));
		        }
		                    
		        if(dto.getCargoAbono() != null && !dto.getCargoAbono().equals(""))
	            	   sbSQL.append("\n  and id_tipo_movto = '" + dto.getCargoAbono() + "'");
	                
	            sbSQL.append("\n UNION");
	            sbSQL.append("\n  Select isnull(c.grupo,0) as grupo, b.id_estatus_cb, b.fec_valor, b.id_tipo_movto ");
	            sbSQL.append("\n  , b.importe,b.referencia, b.no_folio_det, b.no_cheque, b.id_divisa ");
	            sbSQL.append("\n  From ");
	            sbSQL.append("\n  movimiento b, cruce_concilia c");
	            sbSQL.append("\n  where b.no_folio_det *= c.no_folio_1");
	            sbSQL.append("\n  and b.id_estatus_cb not in ('D','N','P') ");
	            
	            if(dto.getNoEmpresa() != 0)
	            	sbSQL.append("\n  and b.no_empresa = " + dto.getNoEmpresa());
	            
	            if(dto.getIdBanco() != 0)
		        	sbSQL.append("\n  and b.id_banco = " + dto.getIdBanco());
		        
		        if(dto.getIdChequera() != null && !dto.getIdChequera().equals(""))
		        	sbSQL.append("\n  and b.id_chequera = '" + dto.getIdChequera() + "'");
		        
		        if(dto.getFechaIni() != null && !dto.getFechaIni().equals(""))
		        {
		        	if(dto.getFechaFin() != null && !dto.getFechaFin().equals(""))
		        	{
		        		sbSQL.append("\n  and b.fec_operacion >= '"  + funciones.ponerFechaSola(dto.getFechaIni()) + "'");
		        		sbSQL.append("\n  and b.fec_operacion <= '"  + funciones.ponerFechaSola(dto.getFechaFin()) + "'");
		        	}
		        	else
		        		sbSQL.append("\n  and b.fec_operacion = '" + funciones.ponerFechaSola(dto.getFechaIni()) + "'");
		        }
		        
		        if(dto.getEstatus() != null && !dto.getEstatus().equals(""))
		        	sbSQL.append("\n  and b.id_estatus_cb = '" + dto.getEstatus() + "'");
		        
		        if(dto.getMontoIni() != 0)
		        {
		        	if(dto.getMontoFin() != 0)
		        	{
		        		sbSQL.append("\n  and b.importe_original >= " + dto.getMontoIni());
		        		sbSQL.append("\n  and b.importe_original <= " + dto.getMontoFin());
		        	}
		        	else
		        		sbSQL.append("\n  and b.importe_original = " + dto.getMontoIni());
		        }
		        	
		        if(dto.getGrupoIni() != 0)
		        {
		        	if(dto.getGrupoFin() != 0)
		        	{
		        		sbSQL.append("\n  and c.grupo between " + dto.getGrupoIni());
		        		sbSQL.append("\n  and " + dto.getGrupoFin());
		        	}
		        	else
		        		sbSQL.append("\n  and c.grupo = " + dto.getGrupoIni());
		        	sbSQL.replace(0, sbSQL.length(), sbSQL.toString().replace("*", ""));
		        }
		                    
		        if(dto.getCargoAbono() != null && !dto.getCargoAbono().equals(""))
	            	   sbSQL.append("\n  and id_tipo_movto = '" + dto.getCargoAbono() + "'");
	                
	            sbSQL.append("\n  order by b.importe desc");
			}*/
            
            sbSQL.append("\n  Select c.grupo  as grupo, b.id_estatus_cb, b.fec_valor, b.id_tipo_movto ");
            sbSQL.append("\n  , b.importe,b.referencia, b.no_folio_det, b.no_cheque, b.id_divisa ");
            sbSQL.append("\n  From ");
            sbSQL.append("\n  hist_movimiento b LEFT JOIN cruce_concilia c");
            sbSQL.append("\n  ON(b.no_folio_det = c.no_folio_1 )");
            sbSQL.append("\n  WHERE ");
            sbSQL.append("\n  b.id_estatus_cb not in ('D','N','P') ");
            
            if(dto.getNoEmpresa() != 0)
            	sbSQL.append("\n  and b.no_empresa = " + dto.getNoEmpresa());
            
            if(dto.getIdBanco() != 0)
	        	sbSQL.append("\n  and b.id_banco = " + dto.getIdBanco());
	        
	        if(dto.getIdChequera() != null && !dto.getIdChequera().equals(""))
	        	sbSQL.append("\n  and b.id_chequera LIKE '" + dto.getIdChequera() + "'");
	        
	        if(dto.getFechaIni() != null && !dto.getFechaIni().equals(""))
	        {
	        	if(dto.getFechaFin() != null && !dto.getFechaFin().equals(""))
	        	{
	        		sbSQL.append("\n  and b.fec_valor >= convert(datetime,'"  + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103)");
	        		sbSQL.append("\n  and b.fec_valor <= convert(datetime,'"  + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)");
	        	}
	        	else
	        		sbSQL.append("\n  and b.fec_valor = convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103)");
	        }
	        
	        if(dto.getEstatus() != null && !dto.getEstatus().equals(""))
	        	sbSQL.append("\n  and b.id_estatus_cb = '" + dto.getEstatus() + "'");
	        
	        if(dto.getMontoIni() != 0)
	        {
	        	if(dto.getMontoFin() != 0)
	        	{
	        		sbSQL.append("\n  and b.importe_original >= " + dto.getMontoIni());
	        		sbSQL.append("\n  and b.importe_original <= " + dto.getMontoFin());
	        	}
	        	else
	        		sbSQL.append("\n  and b.importe_original = " + dto.getMontoIni());
	        }
	        	
	        if(dto.getGrupoIni() != 0)
	        {
	        	if(dto.getGrupoFin() != 0)
	        	{
	        		sbSQL.append("\n  and c.grupo between " + dto.getGrupoIni());
	        		sbSQL.append("\n  and " + dto.getGrupoFin());
	        	}
	        	else
	        		sbSQL.append("\n  and c.grupo = " + dto.getGrupoIni());
	        	sbSQL.replace(0, sbSQL.length(), sbSQL.toString().replace("*", ""));
	        }
	                    
	        if(dto.getCargoAbono() != null && !dto.getCargoAbono().equals(""))
            	   sbSQL.append("\n  and id_tipo_movto = '" + dto.getCargoAbono() + "'");
            
            sbSQL.append("\n UNION");
            sbSQL.append("\n  Select coalesce(c.grupo,0) as grupo, b.id_estatus_cb, b.fec_valor, b.id_tipo_movto ");
            sbSQL.append("\n  , b.importe,b.referencia, b.no_folio_det, b.no_cheque, b.id_divisa ");
            sbSQL.append("\n  From ");
            sbSQL.append("\n  movimiento b left join cruce_concilia c");
            sbSQL.append("\n  on (b.no_folio_det = c.no_folio_1)");
            sbSQL.append("\n  WHERE b.id_estatus_cb not in ('D','N','P') ");
            
            if(dto.getNoEmpresa() != 0)
            	sbSQL.append("\n  and b.no_empresa = " + dto.getNoEmpresa());
            
            if(dto.getIdBanco() != 0)
	        	sbSQL.append("\n  and b.id_banco = " + dto.getIdBanco());
	        
	        if(dto.getIdChequera() != null && !dto.getIdChequera().equals(""))
	        	sbSQL.append("\n  and b.id_chequera = '" + dto.getIdChequera() + "'");
	        
	        if(dto.getFechaIni() != null && !dto.getFechaIni().equals(""))
	        {
	        	if(dto.getFechaFin() != null && !dto.getFechaFin().equals(""))
	        	{
//	        		convert('"  + funciones.ponerFechaSola(dto.getFechaIni()) + "', 'dd/MM/yyyy')
	        		sbSQL.append("\n  and b.fec_valor >= convert(datetime,'"  + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103)");
	        		sbSQL.append("\n  and b.fec_valor <= convert(datetime,'"  + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)");
	        	}
	        	else
	        		sbSQL.append("\n  and b.fec_valor = convert(datetime,'"  + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103)");
	        }
	        
	        if(dto.getEstatus() != null && !dto.getEstatus().equals(""))
	        	sbSQL.append("\n  and b.id_estatus_cb = '" + dto.getEstatus() + "'");
	        
	        if(dto.getMontoIni() != 0)
	        {
	        	if(dto.getMontoFin() != 0)
	        	{
	        		sbSQL.append("\n  and b.importe_original >= " + dto.getMontoIni());
	        		sbSQL.append("\n  and b.importe_original <= " + dto.getMontoFin());
	        	}
	        	else
	        		sbSQL.append("\n  and b.importe_original = " + dto.getMontoIni());
	        }
	        	
	        if(dto.getGrupoIni() != 0)
	        {
	        	if(dto.getGrupoFin() != 0)
	        	{
	        		sbSQL.append("\n  and c.grupo between " + dto.getGrupoIni());
	        		sbSQL.append("\n  and " + dto.getGrupoFin());
	        	}
	        	else
	        		sbSQL.append("\n  and c.grupo = " + dto.getGrupoIni());
	        	sbSQL.replace(0, sbSQL.length(), sbSQL.toString().replace("*", ""));
	        }
	                    
	        if(dto.getCargoAbono() != null && !dto.getCargoAbono().equals(""))
            	   sbSQL.append("\n  and id_tipo_movto = '" + dto.getCargoAbono() + "'");
                
            sbSQL.append("\n  order by importe desc");

			System.out.println(sbSQL.toString());
			listEmpresa = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons = new MovimientoDto();
					cons.setGrupoPago(rs.getInt("grupo"));
					cons.setIdEstatusCb(rs.getString("id_estatus_cb"));
					cons.setFecValor(rs.getDate("fec_valor"));
					cons.setIdTipoMovto(rs.getString("id_tipo_movto").equals("E") ? "C" : "A");
					cons.setImporte(rs.getDouble("importe"));
					cons.setReferencia(rs.getString("referencia"));
					cons.setNoFolioDet(rs.getInt("no_folio_det"));
					cons.setNoCheque(rs.getInt("no_cheque"));
					cons.setIdDivisa(rs.getString("id_divisa"));
					return cons;
				}});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarCancMovsEmpresa");
		}
		return listEmpresa;
	}
	
	/**
	 * FunSQLUpdateDesconciliaHistorico
	 * @param iFolioDet
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int actualizarDesconciliaHistorico(int iFolioDet){
		int iUpdate = 0;
		StringBuffer sbSQL;
		try
		{
			sbSQL = new StringBuffer();
			sbSQL.append("\n update hist_movimiento ");
		    sbSQL.append("\n set id_estatus_cb = 'P' ");
		    sbSQL.append("\n where no_folio_det in (" + iFolioDet + ")");
		    System.out.println("Query"+sbSQL.toString());
	        iUpdate = jdbcTemplate.update(sbSQL.toString());

        	sbSQL = new StringBuffer();
        	sbSQL.append("\n update movimiento ");
    	    sbSQL.append("\n set id_estatus_cb = 'P' ");
    	    sbSQL.append("\n where no_folio_det in (" + iFolioDet + ")");
    	    System.out.println("Query"+sbSQL.toString());     
	        iUpdate = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:actualizarDesconciliaHistorico");
		}
		return iUpdate;
	}
	
	/**
	 * FunSQLInsertDetalleDesconciliacion
	 * @param iFolioDet
	 * @param iSecuencia
	 * @param iUsuario
	 * @param dFecCancela
	 * @param sObservaciones
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int insertarDetalleDesconciliacion(int iFolioDet, int iSecuencia, int iUsuario, String sFecCancela, String sObservaciones){
		StringBuffer sbSQL;
		int iCount = 0;
		int iInserta = 0;
		try{
			sbSQL = new StringBuffer();
		    sbSQL.append("\n  select count(*)  ");
		    sbSQL.append("\n  from detalle_desconciliacion ");
		    sbSQL.append("\n  where no_folio_det= " + iFolioDet);
		    sbSQL.append("\n  and secuencia = " + iSecuencia);
		    sbSQL.append("\n  and fecha_cancela='" + sFecCancela + "'");
		    
		    System.out.println("El query para desconciliar "+ sbSQL.toString());
		    
		    iCount = jdbcTemplate.queryForInt(sbSQL.toString());
		    
		    if(iCount <= 0)
		    {
		    	sbSQL = new StringBuffer();
		        sbSQL.append("\n  Insert into ");
		        sbSQL.append("\n  detalle_desconciliacion ");
		        sbSQL.append("\n  ( ");
		        sbSQL.append("\n no_folio_det, ");
		        sbSQL.append("\n secuencia, ");
		        sbSQL.append("\n fecha_cancela, ");
		        sbSQL.append("\n tipo_conciliacion, ");
		        sbSQL.append("\n usuario_cancela, ");
		        sbSQL.append("\n observaciones");
		        sbSQL.append("\n  ) ");
		        sbSQL.append("\n  Values ");
		        sbSQL.append("\n ( ");
		        sbSQL.append("\n" + iFolioDet);
		        sbSQL.append("\n ," + iSecuencia);
		        sbSQL.append("\n ,convert(datetime,'" + sFecCancela + "', 103)");
		        sbSQL.append("\n ,'B'");
		        sbSQL.append("\n ," + iUsuario);
		        sbSQL.append("\n ,'" + sObservaciones + "')");
		    }
		    System.out.println("Query"+sbSQL.toString());
		    iInserta  = jdbcTemplate.update(sbSQL.toString());
		    
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:insertarDetalleDesconciliacion");
		}
		return iInserta;
	}
	
	/**
	 * FunSQLUpdateDesconcilia
	 * @param sFolios
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int actualizarDesconciliacion(int iFolios){
		int iUpdate = 0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  UPDATE concilia_banco ");
		    sbSQL.append("\n  SET id_estatus_cb = 'P' ");
		    sbSQL.append("\n  WHERE secuencia in (" + iFolios + ")");
		    System.out.println("Query RRubio25"+sbSQL.toString());
		    iUpdate = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:actualizarDesconciliacion");
		}
		return iUpdate;
	}
	
	/**
	 * FunSQLUpdate12
	 * @param iGrupo
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int actualizarHistMovimiento(int iGrupo){
		int iUpdate = 0;
		StringBuffer sbSQL;
		try
		{
			sbSQL = new StringBuffer();
			sbSQL.append("\n update hist_movimiento set id_estatus_cb = 'P' where no_folio_det in (");
		    sbSQL.append("\n select distinct no_folio_1 from cruce_concilia cc where grupo ");
		    sbSQL.append("\n in (" + iGrupo + ")");
		    sbSQL.append("\n and (select count(*) from cruce_concilia where grupo=cc.grupo ");
		    sbSQL.append("\n and fec_exporta is not null)=0");
		    sbSQL.append("\n )");
		    System.out.println("Query"+sbSQL.toString());
		    iUpdate = jdbcTemplate.update(sbSQL.toString());
		    
		    sbSQL = new StringBuffer();
		    sbSQL.append("\n update movimiento set id_estatus_cb = 'P' where no_folio_det in (");
		    sbSQL.append("\n select distinct no_folio_1 from cruce_concilia cc where grupo ");
		    sbSQL.append("\n in (" + iGrupo + ")");
		    sbSQL.append("\n and (select count(*) from cruce_concilia where grupo=cc.grupo ");
		    sbSQL.append("\n and fec_exporta is not null)=0");
    		sbSQL.append("\n )");
    		System.out.println("Query"+sbSQL.toString());
	        iUpdate = jdbcTemplate.update(sbSQL.toString());
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:actualizarHistMovimiento");
		}
		return iUpdate;
	}
	
	/**
	 * FunSQLUpdate13
	 * @param iGrupo
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int actualizarConciliaBanco(int iGrupo){
		int iUpdate = 0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n update concilia_banco set id_estatus_cb = 'P' where secuencia in (");
		    sbSQL.append("\n  select distinct cc.no_folio_2 from cruce_concilia cc where cc.grupo ");
		    sbSQL.append("\n  in (" + iGrupo + ")");
		    sbSQL.append("\n  and (select count(*) from cruce_concilia where grupo=cc.grupo ");
		    sbSQL.append("\n  and fec_exporta is not null)=0");
		    sbSQL.append("\n )");
		    System.out.println("Query"+sbSQL.toString());
		    iUpdate = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:actualizarConciliaBanco");
		}
		return iUpdate;
	}
	
	/**
	 * FunSQLDelete7
	 * @param iGrupo
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int borrarCruceConcilia(int iGrupo){
		StringBuffer sbSQL = new StringBuffer();
		int iDelete = 0;
		try{
			sbSQL.append("\n  delete from cruce_concilia where grupo in (" + iGrupo + ")");
		    sbSQL.append("\n  and (select count(*) from cruce_concilia cc where cc.grupo=grupo ");
		    sbSQL.append("\n  and fec_exporta is not null)=0");
		    System.out.println("Query"+sbSQL.toString());
	        iDelete = jdbcTemplate.update(sbSQL.toString());
	        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:borrarCruceConcilia");
		}
		return iDelete;
	}
	
	/**
	 * FunSQLSelectCancelados
	 * consulta del reporte de cancelados
	 * @param iEmpresa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> consultarReporteCancelados(int iEmpresa){
		List<Map<String, Object>> resultado = null;
		StringBuffer sbSQL = new StringBuffer();
		try{
			/*sbSQL.append("\n  SELECT m.no_folio_det,");
		    sbSQL.append("\n  m.no_docto, ");
		    sbSQL.append("\n  d.usuario_cancela,");
		    sbSQL.append("\n  d.observaciones,");
		    sbSQL.append("\n  d.fecha_cancela");
		    sbSQL.append("\n  FROM hist_movimiento m, detalle_desconciliacion d");
		    sbSQL.append("\n  WHERE m.no_folio_det=d.no_folio_det");
		    sbSQL.append("\n  and m.no_empresa=" + iEmpresa);
		    sbSQL.append("\n  and d.tipo_conciliacion = 'B'");
		    sbSQL.append("\n  UNION");*/
		    
		    sbSQL.append("\n  SELECT m.no_folio_det,");
		    sbSQL.append("\n  m.no_docto, ");
		    sbSQL.append("\n  d.usuario_cancela,");
		    sbSQL.append("\n  d.observaciones,");
		    sbSQL.append("\n  d.fecha_cancela");
		    sbSQL.append("\n  FROM movimiento m, detalle_desconciliacion d");
		    sbSQL.append("\n  WHERE m.no_folio_det=d.no_folio_det");
		    sbSQL.append("\n  and m.no_empresa=" + iEmpresa);
		    sbSQL.append("\n  and d.tipo_conciliacion = 'B'");
		     
		     
		    sbSQL.append("\n  UNION");
		    sbSQL.append("\n  SELECT m.secuencia as no_folio_det,");
		    sbSQL.append("\n  convert(char,no_cheque) as no_docto,");
		    sbSQL.append("\n  d.usuario_cancela,");
		    sbSQL.append("\n  d.observaciones,");
		    sbSQL.append("\n  d.fecha_cancela");
		    sbSQL.append("\n  FROM concilia_banco m, detalle_desconciliacion d");
		    sbSQL.append("\n  WHERE m.secuencia=d.no_folio_det");
		    sbSQL.append("\n  and m.no_empresa=" + iEmpresa);
		    sbSQL.append("\n  and d.tipo_conciliacion = 'B'");
		    sbSQL.append("\n  ORDER BY 3");
		    System.out.println("Query"+sbSQL.toString());
		    resultado = (List<Map<String, Object>>)jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("no_folio_det", rs.getString("no_folio_det"));
			            results.put("no_docto", rs.getString("no_docto"));
			            results.put("usuario_cancela", rs.getString("usuario_cancela"));
			            results.put("observaciones", rs.getString("observaciones"));
			            results.put("fecha_cancela", rs.getDate("fecha_cancela"));
			            return results;
					}
				});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarReporteCancelados");
		}
		return resultado;
	}
	
	/**
	 * FunSQLSelectFicticios
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<MovimientoDto> consultarMovsFicticios(CriteriosBusquedaDto dto){
		List<MovimientoDto> listMov = new ArrayList<MovimientoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
			{
				sbSQL.append("\n  Select id_estatus_cb, fec_valor, id_tipo_movto, importe,id_tipo_operacion,");
	            sbSQL.append("\n  referencia, no_folio_det, no_cheque, no_cuenta,id_divisa,concepto ");
	            sbSQL.append("\n  From movimiento");
	            sbSQL.append("\n  where id_estatus_cb in ('P','D')");
	        
	            if(dto.getNoEmpresa() != 0)
	            {
	            	sbSQL.append("\n  and no_empresa = " + dto.getNoEmpresa());
	            }
	            
	            if(dto.getIdBanco() != 0)
	            {
	            	sbSQL.append("\n  and id_banco = " + dto.getIdBanco());
	            }
	            
	            if(dto.getIdChequera() != null && !dto.getIdChequera().equals(""))
	            {
	            	sbSQL.append("\n  and id_chequera = '" + dto.getIdChequera() + "'");
	            }
	            
	            if(dto.getFechaIni() != null && !dto.getFechaIni().equals(""))
	            {
	            	if(dto.getFechaFin() != null && !dto.getFechaFin().equals(""))
	            	{
	            		sbSQL.append("\n  and fec_valor_original >= convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "',103)");
	            		sbSQL.append("\n  and fec_valor_original <= convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "',103)");
	            	}
	            	else
	            		sbSQL.append("\n  and fec_valor_original = convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "',103)");
	            }
	            
	            if(dto.getEstatus() != null && !dto.getEstatus().equals(""))
	            {
	            	sbSQL.append("\n  and id_estatus_cb = '" + dto.getEstatus() + "'");
	            }
	            
	            if(dto.getMontoIni() != 0)
	            {
	            	if(dto.getMontoFin() != 0)
	            	{
	            		sbSQL.append("\n  and importe >= " + dto.getMontoIni() +" and importe <= " + dto.getMontoFin());
	            	}
	            	else
	            		sbSQL.append("\n  and importe = " + dto.getMontoIni());
	            }
	            
	            if(dto.getCargoAbono() != null && !dto.getCargoAbono().equals(""))
	            {
	            	sbSQL.append("\n  and id_tipo_movto = '" + dto.getCargoAbono() + "'");
	            }
	            
	            if(dto.getFormaPago() != 0)
	            {
	            	sbSQL.append("\n  and id_forma_pago = " + dto.getFormaPago());
	            }
	            
	            sbSQL.append("\n  order by importe desc");
			}
			System.out.println("Query "+sbSQL.toString());
			if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
			{
        
                sbSQL.append("\n  Select id_estatus_cb, fec_valor, id_tipo_movto, importe,id_tipo_operacion,");
                sbSQL.append("\n  referencia, no_folio_det, no_cheque, no_cuenta,id_divisa,concepto ");
                sbSQL.append("\n  From hist_movimiento");
                sbSQL.append("\n  where id_estatus_cb in ('P','D')");
        
                if(dto.getNoEmpresa() != 0)
	            {
	            	sbSQL.append("\n  and no_empresa = " + dto.getNoEmpresa());
	            }
                
                if(dto.getIdBanco() != 0)
	            {
	            	sbSQL.append("\n  and id_banco = " + dto.getIdBanco());
	            }
	            
	            if(dto.getIdChequera() != null && !dto.getIdChequera().equals(""))
	            {
	            	sbSQL.append("\n  and id_chequera = '" + dto.getIdChequera() + "'");
	            }
	            
	            if(dto.getFechaIni() != null && !dto.getFechaIni().equals(""))
	            {
	            	if(dto.getFechaFin() != null && !dto.getFechaFin().equals(""))
	            	{
	            		sbSQL.append("\n  and fec_valor_original >= convert(datetime,'" + dto.getFechaIni() + "',103)");
	            		sbSQL.append("\n  and fec_valor_original <= convert(datetime,'" +dto.getFechaFin() + "',103)");
	            	}
	            	else
	            		sbSQL.append("\n  and fec_valor_original = convert(datetime,'" + dto.getFechaIni() + "',103)");
	            }
	            
	            if(dto.getEstatus() != null && !dto.getEstatus().equals(""))
	            {
	            	sbSQL.append("\n  and id_estatus_cb = '" + dto.getEstatus() + "'");
	            }
	            
	            if(dto.getMontoIni() != 0)
	            {
	            	if(dto.getMontoFin() != 0)
	            	{
	            		sbSQL.append("\n  and float8(importe) >= " + dto.getMontoIni() +" and float8(importe) <= " + dto.getMontoFin());
	            	}
	            	else
	            		sbSQL.append("\n  and importe = " + dto.getMontoIni());
	            }
	            
	            if(dto.getCargoAbono() != null && !dto.getCargoAbono().equals(""))
	            {
	            	sbSQL.append("\n  and id_tipo_movto = '" + dto.getCargoAbono() + "'");
	            }
	            
	            if(dto.getFormaPago() != 0)
	            {
	            	sbSQL.append("\n  and id_forma_pago = " + dto.getFormaPago());
	            }

	            sbSQL.append("\n  order by importe desc");
			}
			
			//System.out.println(sbSQL.toString());
			listMov = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons = new MovimientoDto();
					cons.setIdEstatusCb(rs.getString("id_estatus_cb"));
					cons.setFecValor(rs.getDate("fec_valor"));
					cons.setIdTipoMovto(rs.getString("id_tipo_movto").equals("E") ? "C" : "A");
					cons.setImporte(rs.getDouble("importe"));
					cons.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
					cons.setReferencia(rs.getString("referencia"));
					cons.setNoFolioDet(rs.getInt("no_folio_det"));
					cons.setNoCheque(rs.getInt("no_cheque"));
					cons.setNoCuenta(rs.getInt("no_cuenta"));
					cons.setIdDivisa(rs.getString("id_divisa"));
					cons.setConcepto(rs.getString("concepto"));
					return cons;
				}});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarMovsFicticios");
		}
		return listMov;
	}
	
	/**
	 * FunSQLUpdateFicticio
	 * @param iFolio
	 * @param iEmpresa
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int actualizarFicticios(int iFolio, int iEmpresa){
		int iUpdate = 0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n update ");
		    sbSQL.append("\n movimiento ");
		    sbSQL.append("\n set ");
		    sbSQL.append("\n id_estatus_cb = 'F'");
		    sbSQL.append("\n Where ");
		    sbSQL.append("\n no_folio_det = " + iFolio);
		    sbSQL.append("\n And no_empresa = " + iEmpresa);
		    System.out.println("Query "+sbSQL.toString());
		    iUpdate = jdbcTemplate.update(sbSQL.toString());
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:actualizarFicticios");
		}
		return iUpdate;
	}
	
	/**
	 * FunSQLInsertDetalleFicticios
	 * @param iFolio
	 * @param sTipo
	 * @param iUsuario
	 * @param dFecha
	 * @param sObservacion
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int insertarDetalleFicticios(int iFolio, String sTipo, int iUsuario, Date dFecha, String sObservacion){
		int iInsert = 0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  Insert ");
		    sbSQL.append("\n  into ");
		    sbSQL.append("\n  detalle_ficticios ");
		    sbSQL.append("\n  Values ");
		    sbSQL.append("\n  (");
		    sbSQL.append("\n " + iFolio);
		    sbSQL.append("\n ,'" + sTipo + "'");
		    sbSQL.append("\n ," + iUsuario);
		    sbSQL.append("\n ,'" + funciones.ponerFechaSola(dFecha) + "'");
		    sbSQL.append("\n ,'" + sObservacion + "')");
		    System.out.println("Query "+sbSQL.toString());
		    iInsert = jdbcTemplate.update(sbSQL.toString());
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:insertarDetalleFicticios");
		}
		return iInsert;
	}
	
	/**
	 * FunSQLSelectDetalleFicticios
	 * @param iEmpresa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> consultarReporteMovsFicticios(int iEmpresa){
		List<Map<String, Object>> resultado = null;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n SELECT hm.id_estatus_cb, hm.fec_valor, hm.id_tipo_movto,");
		    sbSQL.append("\n hm.importe,hm.id_tipo_operacion, hm.referencia,");
		    sbSQL.append("\n hm.no_folio_det, hm.no_cheque, hm.no_cuenta, hm.id_divisa,concepto,");
		    sbSQL.append("\n df.fec_concilia, df.usuario_concilia, df.observaciones");
		    sbSQL.append("\n FROM detalle_ficticios df, hist_movimiento hm");
		    sbSQL.append("\n WHERE no_empresa = " + iEmpresa);
		    sbSQL.append("\n and df.no_folio_det = hm.no_folio_det ");
		    sbSQL.append("\n and tipo_conciliacion = 'B'");
		    System.out.println("Query"+sbSQL.toString());
		    resultado = (List<Map<String, Object>>)jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("id_estatus_cb", rs.getString("id_estatus_cb"));
			            results.put("fec_valor", rs.getDate("fec_valor"));
			            results.put("id_tipo_movto", rs.getString("id_tipo_movto"));
			            results.put("importe", rs.getDouble("importe"));
			            results.put("id_tipo_operacion", rs.getString("id_tipo_operacion"));
			            results.put("referencia", rs.getString("referencia"));
			            results.put("no_folio_det", rs.getString("no_folio_det"));
			            results.put("no_cheque", rs.getString("no_cheque"));
			            results.put("no_cuenta", rs.getString("no_cuenta"));
			            results.put("id_divisa", rs.getString("id_divisa"));
			            results.put("concepto", rs.getString("concepto"));
			            results.put("fec_concilia", rs.getDate("fec_concilia"));
			            results.put("usuario_concilia", rs.getString("usuario_concilia"));
			            results.put("observaciones", rs.getString("observaciones"));
			            return results;
					}
				});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarReporteMovsFicticios");
		}
		return resultado;
	}
	
	/**
	 * FunSQLSelectDuplicados
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ConciliaBancoDto> consultarMovsDuplicados(CriteriosBusquedaDto dto){
		List<ConciliaBancoDto> listMov = new ArrayList<ConciliaBancoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
			{
				sbSQL.append("\n  Select id_estatus_cb, fec_operacion, cargo_abono, importe, referencia,concepto, ");
	            sbSQL.append("\n  secuencia,no_cheque From concilia_banco where id_estatus_cb in ('P','D') ");
	        
	            if(dto.getNoEmpresa() != 0)
	            {
	            	sbSQL.append("\n  and no_empresa = " + dto.getNoEmpresa());
	            }
	            if(dto.getIdBanco() != 0)
	            {
	            	sbSQL.append("\n  and id_banco = " + dto.getIdBanco());
	            }
	            
	            if(dto.getIdChequera() != null && !dto.getIdChequera().equals(""))
	            {
	            	sbSQL.append("\n  and id_chequera = '" + dto.getIdChequera() + "'");
	            }
	            
	            if(dto.getFechaIni() != null && !dto.getFechaIni().equals(""))
	            {
	            	if(dto.getFechaFin() != null && !dto.getFechaFin().equals(""))
	            	{
	            		sbSQL.append("\n  and fec_operacion >= convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "',103)");
	            		sbSQL.append("\n  and fec_operacion <= convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "',103)");
	            	}
	            	else
	            		sbSQL.append("\n  and fec_operacion = convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "',103)");
	            }
	            
	            if(dto.getEstatus() != null && !dto.getEstatus().equals(""))
	            {
	            	sbSQL.append("\n  and id_estatus_cb = '" + dto.getEstatus() + "'");
	            }
	            
	            if(dto.getMontoIni() != 0)
	            {
	            	if(dto.getMontoFin() != 0)
	            	{
	            		sbSQL.append("\n  and importe >= " + dto.getMontoIni() +" and importe <= " + dto.getMontoFin());
	            	}
	            	else
	            		sbSQL.append("\n  and importe = " + dto.getMontoIni());
	            }
	            
	            if(dto.getCargoAbono() != null && !dto.getCargoAbono().equals(""))
	            {
	            	sbSQL.append("\n  and cargo_abono = '" + dto.getCargoAbono() + "'");
	            }
	            
	            sbSQL.append("\n  order by importe desc");
			}
			System.out.println("Query "+sbSQL.toString());
			if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
			{
        
                sbSQL.append("\n  Select id_estatus_cb, fec_operacion, cargo_abono, importe, referencia,concepto, ");
                sbSQL.append("\n  secuencia,no_cheque From concilia_banco where id_estatus_cb in ('P','D') ");
        
                if(dto.getNoEmpresa() != 0)
	            {
	            	sbSQL.append("\n  and no_empresa = " + dto.getNoEmpresa());
	            }
                
                if(dto.getIdBanco() != 0)
	            {
	            	sbSQL.append("\n  and id_banco = " + dto.getIdBanco());
	            }
	            
	            if(dto.getIdChequera() != null && !dto.getIdChequera().equals(""))
	            {
	            	sbSQL.append("\n  and id_chequera = '" + dto.getIdChequera() + "'");
	            }
	            
	            if(dto.getFechaIni() != null && !dto.getFechaIni().equals(""))
	            {
	            	if(dto.getFechaFin() != null && !dto.getFechaFin().equals(""))
	            	{
	            		sbSQL.append("\n  and fec_operacion >= '" + dto.getFechaIni() + "'");
	            		sbSQL.append("\n  and fec_operacion <= '" +dto.getFechaFin() + "'");
	            	}
	            	else
	            		sbSQL.append("\n  and fec_operacion = '" + dto.getFechaIni() + "'");
	            }
	            
	            if(dto.getEstatus() != null && !dto.getEstatus().equals(""))
	            {
	            	sbSQL.append("\n  and id_estatus_cb = '" + dto.getEstatus() + "'");
	            }
	            
	            if(dto.getMontoIni() != 0)
	            {
	            	if(dto.getMontoFin() != 0)
	            	{
	            		sbSQL.append("\n  and float8(importe) >= " + dto.getMontoIni() +" and float8(importe) <= " + dto.getMontoFin());
	            	}
	            	else
	            		sbSQL.append("\n  and importe = " + dto.getMontoIni());
	            }
	            
	            if(dto.getCargoAbono() != null && !dto.getCargoAbono().equals(""))
	            {
	            	sbSQL.append("\n  and cargo_abono = '" + dto.getCargoAbono() + "'");
	            }

	            sbSQL.append("\n  order by importe desc");
			}
			
			//System.out.println(sbSQL.toString());
			listMov = jdbcTemplate.query(sbSQL.toString(), new RowMapper<ConciliaBancoDto>(){
				public ConciliaBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConciliaBancoDto cons = new ConciliaBancoDto();
					cons.setIdEstatuscb(rs.getString("id_estatus_cb"));
					cons.setFecOperacion(rs.getDate("fec_operacion"));
					cons.setCargoAbono(rs.getString("cargo_abono").equals("E") ? "C" : "A");
					cons.setImporte(rs.getDouble("importe"));
					cons.setReferencia(rs.getString("referencia"));
					cons.setNoCheque(rs.getInt("no_cheque"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setSecuencia(rs.getInt("secuencia"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarMovsDuplicados");
		}
		return listMov;
	}
	
	/**
	 * FunSQLUpdateDuplicado
	 * @param iSecuencia
	 * @param iEmpresa
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int actualizarDuplicados(int iSecuencia, int iEmpresa){
		int iUpdate = 0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n update ");
		    sbSQL.append("\n concilia_banco ");
		    sbSQL.append("\n set ");
		    sbSQL.append("\n id_estatus_cb = 'A'");
		    sbSQL.append("\n Where ");
		    sbSQL.append("\n secuencia = " + iSecuencia);
		    sbSQL.append("\n And no_empresa = " + iEmpresa);
		    System.out.println("Query "+sbSQL.toString());
		    iUpdate = jdbcTemplate.update(sbSQL.toString());
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:actualizarDuplicados");
		}
		return iUpdate;
	}
	
	/**
	 * FunSQLInsertDetalleDuplicadosBanco
	 * @param iSecuencia
	 * @param iUsuario
	 * @param dFecha
	 * @param sObservacion
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int insertarDetalleDuplicadosBanco(int iSecuencia, int iUsuario, Date dFecha, String sObservacion){
		int iInsert = 0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  Insert into");
		    sbSQL.append("\n  detalle_duplicados_bancarios ");
		    sbSQL.append("\n  (secuencia, fecha_elimina, tipo_conciliacion, usuario_elimina, observaciones) ");
		    sbSQL.append("\n  Values (");
		    sbSQL.append("\n " + iSecuencia);
		    sbSQL.append("\n ,'" + funciones.ponerFechaSola(dFecha) + "'");
		    sbSQL.append("\n ,'B'");
		    sbSQL.append("\n ," + iUsuario);
		    sbSQL.append("\n ,'" + sObservacion + "')");
		    System.out.println("Query "+sbSQL.toString());
		    iInsert = jdbcTemplate.update(sbSQL.toString());
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:insertarDetalleDuplicadosBanco");
		}
		return iInsert;
	}
	
	/**
	 * FunSQLSelectDetalleDuplicados
	 * @param iEmpresa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> consultarReporteMovsDuplicados(int iEmpresa){
		List<Map<String, Object>> resultado = null;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n select cb.id_estatus_cb, cb.fec_operacion, cb.cargo_abono, ");
		    sbSQL.append("\n cb.importe , cb.referencia, cb.concepto, cb.secuencia, cb.no_cheque, ");
		    sbSQL.append("\n dd.fecha_elimina , dd.usuario_elimina, dd.observaciones ");
		    sbSQL.append("\n from detalle_duplicados_bancarios dd, concilia_banco cb ");
		    sbSQL.append("\n where no_empresa = " + iEmpresa + " ");
		    sbSQL.append("\n and cb.secuencia = dd.secuencia ");
		    sbSQL.append("\n and dd.tipo_conciliacion = 'B'");
		    System.out.println("Query "+sbSQL.toString());
		    resultado = (List<Map<String, Object>>)jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("id_estatus_cb", rs.getString("id_estatus_cb"));
			            results.put("fec_operacion", rs.getDate("fec_operacion"));
			            results.put("cargo_abono", rs.getString("cargo_abono"));
			            results.put("importe", rs.getDouble("importe"));
			            results.put("referencia", rs.getString("referencia"));
			            results.put("secuencia", rs.getString("secuencia"));
			            results.put("no_cheque", rs.getString("no_cheque"));
			            results.put("concepto", rs.getString("concepto"));
			            results.put("fecha_elimina", rs.getDate("fecha_elimina"));
			            results.put("usuario_elimina", rs.getString("usuario_elimina"));
			            results.put("observaciones", rs.getString("observaciones"));
			            return results;
					}
				});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarReporteMovsDuplicados");
		}
		return resultado;
	}
	
	/**
	 * FunSQLSelectBancosEmpresas
	 * @param sEmpresas
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarBancosEmpresas(String sEmpresas){
		StringBuffer sbSQL = new StringBuffer();
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n  SELECT distinct id_banco as ID, desc_banco as Descrip");
	        sbSQL.append("\n  FROM cat_banco");
	        sbSQL.append("\n  WHERE id_banco in ");
	        sbSQL.append("\n     (select distinct id_banco ");
	        sbSQL.append("\n     from cat_cta_banco ");
	        sbSQL.append("\n    where tipo_chequera not in(" + globalSingleton.obtenerValorConfiguraSet(202) + ") ");
	        sbSQL.append("\n     and no_empresa in (" + sEmpresas + "))");
	        System.out.println("Query "+sbSQL.toString());
	        list = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("Descrip"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarBancosEmpresas");
		}
		return list;
	}
	
	/**
	 * FunSQLSelectChequerasBanco
	 * @param sEmpresas
	 * @param iBanco
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<CatCtaBancoDto> consultarChequerasBanco(String sEmpresas, int iBanco){
		StringBuffer sbSQL = new StringBuffer();
		List<CatCtaBancoDto> listCheque = new ArrayList<CatCtaBancoDto>();
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n SELECT");
		    sbSQL.append("\n ccb.id_chequera,");
		    sbSQL.append("\n ccb.no_empresa,");
		    sbSQL.append("\n e.nom_empresa ");
		    sbSQL.append("\n FROM");
		    sbSQL.append("\n cat_cta_banco ccb,");
		    sbSQL.append("\n empresa e");
		    sbSQL.append("\n WHERE id_banco = " + iBanco);
		    sbSQL.append("\n and ccb.no_empresa = e.no_empresa");
		    sbSQL.append("\n AND ccb.tipo_chequera not in(" + globalSingleton.obtenerValorConfiguraSet(202) + ") ");
		    sbSQL.append("\n and ccb.no_empresa in (" + sEmpresas + ")");
		    sbSQL.append("\n ORDER BY ccb.id_chequera");
		    System.out.println("Query "+sbSQL.toString());
		    listCheque = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public CatCtaBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
					CatCtaBancoDto cons = new CatCtaBancoDto();
					cons.setIdChequera(rs.getString("id_chequera"));
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setDescPlaza(rs.getString("nom_empresa"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarChequerasBanco");
		}
		return listCheque;
	}
	
	/**
	 * FunSQLSelect21
	 * Consulta para los reportes: con aclaracion, conciliacion manual
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarReporteconAclaracion(CriteriosReporteDto dto){
		List<Map<String, Object>> mapReporte = new ArrayList<Map<String, Object>>();
		StringBuffer sbSQL = new StringBuffer();
		try{
		    sbSQL.append("\n  SELECT DISTINCT  ");
		    sbSQL.append("\n   c.no_empresa,h.concepto as concepto1, convert(char,null) as concepto,  ");
		    sbSQL.append("\n   c.id_chequera,   ");
		    sbSQL.append("\n   c.grupo,  ");
		    sbSQL.append("\n   c.no_folio_2 as secuencia,  ");
		    sbSQL.append("\n   c.no_folio_1 as no_folio_det,  ");
		    sbSQL.append("\n   convert(char,h.no_cheque) as chequemov,  ");
		    sbSQL.append("\n   convert(char,null) as chequecon,  ");
		    sbSQL.append("\n   h.referencia as refermov,  ");
		    sbSQL.append("\n   convert(char,null) as refercon,  ");
		    sbSQL.append("\n   h.fec_operacion as fec_operacion,  ");
		    sbSQL.append("\n   null as fec_operbanco,  ");
		    sbSQL.append("\n   h.importe as impm,  ");
		    sbSQL.append("\n   convert(numeric,null) as impb,  ");
		    sbSQL.append("\n   case when h.id_tipo_movto = 'E' then h.importe *-1 else h.importe  end as importemov ,  ");
		    sbSQL.append("\n   convert(numeric,null) as importecon,  ");
		    sbSQL.append("\n   case when h.id_tipo_movto = 'E' then 'C' when h.id_tipo_movto = 'I' then 'A' else convert(char,null) end as cam ,  ");
		    sbSQL.append("\n   convert(char,null) as cab,  ");
		    sbSQL.append("\n   case when h.id_tipo_movto = 'E' then h.importe end as impcargo,  ");
		    sbSQL.append("\n   convert(numeric,null) as impcargob ,  ");
		    sbSQL.append("\n   case when h.id_tipo_movto = 'I' then h.importe end as impabono,  ");
		    sbSQL.append("\n   convert(numeric,null) as impabonob    ");
		    sbSQL.append("\n  FROM cruce_concilia c,  hist_movimiento h");
		    sbSQL.append("\n  WHERE");
		    sbSQL.append("\n  c.no_folio_1 = no_folio_det");
		    sbSQL.append("\n  and c.no_folio_2 is null");
		    sbSQL.append("\n  and c.no_empresa = " + dto.getINoEmpresa());
		    sbSQL.append("\n  and c.no_empresa = h.no_empresa");
		    sbSQL.append("\n  and c.id_banco = " + dto.getIIdBanco());
		    sbSQL.append("\n  and c.id_banco = h.id_banco");
		    
		    if(dto.isBValChequera())
		        sbSQL.append("\n  and c.id_chequera = '" + dto.getSChequera() + "'");
		    
		    sbSQL.append("\n  and c.id_chequera = h.id_chequera");
		    
		    if(dto.getSTipoConcilia().equals("M"))
		        sbSQL.append("\n  and c.tipo_concilia in ('" + dto.getSTipoConcilia() + "','A')");
		    else
		        sbSQL.append("\n  and c.tipo_concilia = '" + dto.getSTipoConcilia() + "'");
		    
		    sbSQL.append("\n  and c.tipo_concilia = h.id_estatus_cb");
		    sbSQL.append("\n  and h.fec_valor between convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaIni()) + "', 103)  \n");
		    sbSQL.append("\n  and convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaFin()) + "', 103)");
		    //'''''''''' SE AGREGO UNION CON MOVIMIENTO''''''''''''
		    sbSQL.append("\n  UNION");
		    sbSQL.append("\n SELECT DISTINCT");
		    sbSQL.append("\n  c.no_empresa,h.concepto as concepto1, convert(char,null) as concepto,");
		    sbSQL.append("\n  c.id_chequera, ");
		    sbSQL.append("\n  c.grupo,");
		    sbSQL.append("\n  c.no_folio_2 as secuencia,");
		    sbSQL.append("\n  c.no_folio_1 as no_folio_det,");
		    sbSQL.append("\n  convert(char,h.no_cheque) as chequemov,");
		    sbSQL.append("\n  convert(char,null) as chequecon,");
		    sbSQL.append("\n  h.referencia as refermov,");
		    sbSQL.append("\n  convert(char,null) as refercon,");
		    sbSQL.append("\n  h.fec_operacion as fec_operacion,");
		    sbSQL.append("\n  null as fec_operbanco,");
		    sbSQL.append("\n  h.importe as impm,");
		    sbSQL.append("\n  convert(numeric,null) as impb,");
		    sbSQL.append("\n  case when h.id_tipo_movto = 'E' then h.importe *-1 else h.importe  end as importemov ,");
		    sbSQL.append("\n  convert(numeric,null) as importecon,");
		    sbSQL.append("\n  case when h.id_tipo_movto = 'E' then 'C' when h.id_tipo_movto = 'I' then 'A' else convert(char,null) end as cam ,");
		    sbSQL.append("\n  convert(char,null) as cab,");
		    sbSQL.append("\n  case when h.id_tipo_movto = 'E' then h.importe end as impcargo,");
		    sbSQL.append("\n  convert(numeric,null) as impcargob ,");
		    sbSQL.append("\n  case when h.id_tipo_movto = 'I' then h.importe end as impabono,");
		    sbSQL.append("\n  convert(numeric,null) as impabonob  ");
		    sbSQL.append("\n  FROM cruce_concilia c,movimiento h");
		    sbSQL.append("\n  WHERE");
		    sbSQL.append("\n  c.no_folio_1 = no_folio_det");
		    sbSQL.append("\n  and c.no_folio_2 is null");
		    sbSQL.append("\n  and c.no_empresa = " + dto.getINoEmpresa());
		    sbSQL.append("\n  and c.no_empresa = h.no_empresa");
		    sbSQL.append("\n  and c.id_banco = " + dto.getIIdBanco());
		    sbSQL.append("\n  and c.id_banco = h.id_banco");
		    
		    if(dto.isBValChequera())
		        sbSQL.append("\n  and c.id_chequera = '" + dto.getSChequera() + "'");
		    
		    sbSQL.append("\n  and c.id_chequera = h.id_chequera");
		    if(dto.getSTipoConcilia().equals("M"))
		        sbSQL.append("\n  and c.tipo_concilia IN ('" + dto.getSTipoConcilia() + "','A')");
		    else
		        sbSQL.append("\n  and c.tipo_concilia = '" + dto.getSTipoConcilia() + "'");
		   
		    sbSQL.append("\n  and c.tipo_concilia = h.id_estatus_cb");
		    sbSQL.append("\n  and h.fec_valor between convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaIni()) + "', 103) "
		    		+ "and convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaFin()) + "', 103)");
		    //''''''''''''''''''TERMINA UNION CON MOVIMIENTO''''''''''''''''''
		    
		    sbSQL.append("\n  UNION ALL");
		    sbSQL.append("\n  SELECT DISTINCT");
		    sbSQL.append("\n  c.no_empresa, convert(char,null) as concepto1, ");
		    sbSQL.append("\n  coalesce((select desc_concepto from cat_concepto_banco where id_banco=cb.id_banco and id_concepto=cb.concepto),cb.concepto) as concepto,");
		    sbSQL.append("\n  c.id_chequera,");
		    sbSQL.append("\n  c.grupo,");
		    sbSQL.append("\n  c.no_folio_2 as secuencia,");
		    sbSQL.append("\n  c.no_folio_1 as no_folio_det,");
		    sbSQL.append("\n  convert(char,null) as chequemov,");
		    sbSQL.append("\n  convert(char,cb.no_cheque)as chequecon,");
		    sbSQL.append("\n  convert(char,null) as refermov,");
		    sbSQL.append("\n  cb.referencia as refercon,");
		    sbSQL.append("\n  null as fec_operacion,");
		    sbSQL.append("\n  cb.fec_operacion as fec_operbanco,");
		    sbSQL.append("\n  convert(numeric,null) as impm,");
		    sbSQL.append("\n  cb.importe as impb,");
		    sbSQL.append("\n  convert(numeric,null) as importemov,");
		    sbSQL.append("\n  case when cb.cargo_abono = 'E' then cb.importe *-1 else cb.importe  end as importecon ,");
		    sbSQL.append("\n  convert(char,null) as cam,");
		    sbSQL.append("\n  case when cb.cargo_abono = 'E' then 'C' when cb.cargo_abono = 'I' then 'A' else convert(char,null) end as cab,");
		    sbSQL.append("\n  convert(numeric,null) as impcargo ,");
		    sbSQL.append("\n  case when cb.cargo_abono = 'E' then cb.importe end as impcargob ,");
		    sbSQL.append("\n  convert(numeric,null) as impabono ,");
		    sbSQL.append("\n  case when cb.cargo_abono = 'I' then cb.importe end as impabonob ");
		    sbSQL.append("\n  FROM concilia_banco cb, cruce_concilia c");
		    sbSQL.append("\n  WHERE");
		    sbSQL.append("\n  c.no_folio_1 is null");
		    sbSQL.append("\n  and c.no_folio_2 = cb.secuencia");
		    sbSQL.append("\n  and c.no_empresa = " + dto.getINoEmpresa());
		    sbSQL.append("\n  and c.no_empresa = cb.no_empresa");
		    sbSQL.append("\n  and c.id_banco = " + dto.getIIdBanco());
		    sbSQL.append("\n  and c.id_banco = cb.id_banco");

		    if(dto.isBValChequera())
		        sbSQL.append("\n  and c.id_chequera = '" + dto.getSChequera() + "'");

		    sbSQL.append("\n  and c.id_chequera = cb.id_chequera");
		    if(dto.getSTipoConcilia().equals("M"))
		        sbSQL.append("\n  and c.tipo_concilia IN ('" + dto.getSTipoConcilia() + "','A')");
		    else
		        sbSQL.append("\n  and c.tipo_concilia = '" + dto.getSTipoConcilia() + "'");
		   
		    sbSQL.append("\n  and c.tipo_concilia = cb.id_estatus_cb");
		    sbSQL.append("\n  and cb.fec_operacion between convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaIni()) + "', 103) "
		    		+ "and convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaFin()) + "', 103)");

		    sbSQL.append("\n  UNION ALL ");

		    sbSQL.append("\n  SELECT DISTINCT");
		    sbSQL.append("\n  c.no_empresa, h.concepto as concepto1, ");
		    sbSQL.append("\n  coalesce((select desc_concepto from cat_concepto_banco where id_banco=cb.id_banco and id_concepto=cb.concepto),cb.concepto) as concepto,");
		    sbSQL.append("\n  c.id_chequera,");
		    sbSQL.append("\n  c.grupo,");
		    sbSQL.append("\n  c.no_folio_2 as secuencia,");
		    sbSQL.append("\n  c.no_folio_1 as no_folio_det,");
		    sbSQL.append("\n  convert(char,h.no_cheque) as chequemov,");
		    sbSQL.append("\n  convert(char,cb.no_cheque) as chequecon,");
		    sbSQL.append("\n  h.referencia as refermov,");
		    sbSQL.append("\n  cb.referencia as refercon,");
		    sbSQL.append("\n  h.fec_operacion as fec_operacion,");
		    sbSQL.append("\n  cb.fec_operacion as fec_operbanco,");
		    sbSQL.append("\n  h.importe as impm,");
		    sbSQL.append("\n  cb.importe as impb,");
		    sbSQL.append("\n  case when h.id_tipo_movto = 'E' then h.importe *-1 else h.importe  end as importemov ,");
		    sbSQL.append("\n  case when cb.cargo_abono = 'E' then cb.importe *-1 else cb.importe  end as importecon ,");
		    sbSQL.append("\n  case when h.id_tipo_movto = 'E' then 'C' when h.id_tipo_movto = 'I' then 'A' else ' ' end as cam ,");
		    sbSQL.append("\n  case when cb.cargo_abono = 'E' then 'C' when cb.cargo_abono = 'I' then 'A' else ' ' end as cab,");
		    sbSQL.append("\n  case when h.id_tipo_movto = 'E' then h.importe end as impcargo ,");
		    sbSQL.append("\n  case when cb.cargo_abono = 'E' then cb.importe end as impcargob ,");
		    sbSQL.append("\n  case when h.id_tipo_movto = 'I' then h.importe end as impabono ,");
		    sbSQL.append("\n  case when cb.cargo_abono = 'I' then cb.importe end as impabonob  ");
		    sbSQL.append("\n  FROM concilia_banco cb, cruce_concilia c,  hist_movimiento h");
		    sbSQL.append("\n  WHERE");
		    sbSQL.append("\n  c.no_folio_1 = no_folio_det");
		    sbSQL.append("\n  and c.no_folio_2 = cb.secuencia");
		    sbSQL.append("\n  and c.no_empresa = " + dto.getINoEmpresa());
		    sbSQL.append("\n  and c.no_empresa = cb.no_empresa");
		    sbSQL.append("\n  and c.no_empresa = h.no_empresa");
		    sbSQL.append("\n  and c.id_banco = " + dto.getIIdBanco());
		    sbSQL.append("\n  and c.id_banco = cb.id_banco");
		    sbSQL.append("\n  and c.id_banco = h.id_banco");

		    if(dto.isBValChequera())
		        sbSQL.append("\n  and c.id_chequera = '" + dto.getSChequera() + "'");

		    sbSQL.append("\n  and c.id_chequera = cb.id_chequera");
		    sbSQL.append("\n  and c.id_chequera = h.id_chequera");
		    if(dto.getSTipoConcilia().equals("M"))
		        sbSQL.append("\n  and c.tipo_concilia IN ('" + dto.getSTipoConcilia() + "','A')");
		    else
		        sbSQL.append("\n  and c.tipo_concilia = '" + dto.getSTipoConcilia() + "'");
		   
		    sbSQL.append("\n  and c.tipo_concilia = cb.id_estatus_cb");
		    sbSQL.append("\n  and c.tipo_concilia = h.id_estatus_cb");
		    sbSQL.append("\n  and h.fec_valor between convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaIni()) + "', 103) "
		    		+ "and convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaFin()) + "', 103) ");
		    sbSQL.append("\n  and cb.fec_operacion between convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaIni()) + "', 103) "
		    		+ "and convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaFin()) + "', 103)");
		    
		    //'''''''''' SE AGREGO UNION CON MOVIMIENTO''''''''''''
		    sbSQL.append("\n UNION");

		    sbSQL.append("\n  SELECT DISTINCT");
		    sbSQL.append("\n  c.no_empresa, h.concepto as concepto1, ");
		    sbSQL.append("\n  coalesce((select desc_concepto from cat_concepto_banco where id_banco=cb.id_banco and id_concepto=cb.concepto),cb.concepto) as concepto,");
		    sbSQL.append("\n  c.id_chequera,");
		    sbSQL.append("\n  c.grupo,");
		    sbSQL.append("\n  c.no_folio_2 as secuencia,");
		    sbSQL.append("\n  c.no_folio_1 as no_folio_det,");
		    sbSQL.append("\n  convert(char,h.no_cheque) as chequemov,");
		    sbSQL.append("\n  convert(char,cb.no_cheque) as chequecon,");
		    sbSQL.append("\n  h.referencia as refermov,");
		    sbSQL.append("\n  cb.referencia as refercon,");
		    sbSQL.append("\n  h.fec_operacion as fec_operacion,");
		    sbSQL.append("\n  cb.fec_operacion as fec_operbanco,");
		    sbSQL.append("\n  h.importe as impm,");
		    sbSQL.append("\n  cb.importe as impb,");
		    sbSQL.append("\n  case when h.id_tipo_movto = 'E' then h.importe *-1 else h.importe  end as importemov ,");
		    sbSQL.append("\n  case when cb.cargo_abono = 'E' then cb.importe *-1 else cb.importe  end as importecon ,");
		    sbSQL.append("\n  case when h.id_tipo_movto = 'E' then 'C' when h.id_tipo_movto = 'I' then 'A' else ' ' end as cam ,");
		    sbSQL.append("\n  case when cb.cargo_abono = 'E' then 'C' when cb.cargo_abono = 'I' then 'A' else ' ' end as cab,");
		    sbSQL.append("\n  case when h.id_tipo_movto = 'E' then h.importe end as impcargo ,");
		    sbSQL.append("\n  case when cb.cargo_abono = 'E' then cb.importe end as impcargob ,");
		    sbSQL.append("\n  case when h.id_tipo_movto = 'I' then h.importe end as impabono ,");
		    sbSQL.append("\n  case when cb.cargo_abono = 'I' then cb.importe end as impabonob  ");
		    sbSQL.append("\n  FROM concilia_banco cb, cruce_concilia c,movimiento h");
		    sbSQL.append("\n  WHERE");
		    sbSQL.append("\n  c.no_folio_1 = no_folio_det");
		    sbSQL.append("\n  and c.no_folio_2 = cb.secuencia");
		    sbSQL.append("\n  and c.no_empresa = " + dto.getINoEmpresa());
		    sbSQL.append("\n  and c.no_empresa = cb.no_empresa");
		    sbSQL.append("\n  and c.no_empresa = h.no_empresa");
		    sbSQL.append("\n  and c.id_banco = " + dto.getIIdBanco());
		    sbSQL.append("\n  and c.id_banco = cb.id_banco");
		    sbSQL.append("\n  and c.id_banco = h.id_banco");

		    if(dto.isBValChequera())
		        sbSQL.append("\n  and c.id_chequera = '" + dto.getSChequera() + "'");

		    sbSQL.append("\n  and c.id_chequera = cb.id_chequera");
		    sbSQL.append("\n  and c.id_chequera = h.id_chequera");
		    if(dto.getSTipoConcilia().equals("M"))
		        sbSQL.append("\n  and c.tipo_concilia IN ('" + dto.getSTipoConcilia() + "','A')");
		    else
		        sbSQL.append("\n  and c.tipo_concilia = '" + dto.getSTipoConcilia() + "'");
		    
		    sbSQL.append("\n  and c.tipo_concilia = cb.id_estatus_cb");
		    sbSQL.append("\n  and c.tipo_concilia = h.id_estatus_cb");
		    sbSQL.append("\n  and h.fec_valor between convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaIni()) + "', 103) "
		    		+ "and convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaFin()) + "', 103)");
		    sbSQL.append("\n  and cb.fec_operacion between convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaIni()) + "', 103) "
		    		+ "and convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaFin()) + "', 103)");
		    
	    System.out.println("Consultar reporte aclaración \n"+ sbSQL.toString());
		    
		    mapReporte = (List<Map<String, Object>>)jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("no_empresa", rs.getString("no_empresa"));
			            results.put("concepto1", rs.getString("concepto1"));
			            results.put("concepto", rs.getString("concepto"));
			            results.put("id_chequera", rs.getString("id_chequera"));
			            results.put("grupo", rs.getString("grupo"));
			            results.put("secuencia", rs.getString("secuencia"));
			            results.put("no_folio_det", rs.getString("no_folio_det"));
			            results.put("chequemov", rs.getString("chequemov"));
			            results.put("chequecon", rs.getString("chequecon"));
			            results.put("refermov", rs.getString("refermov"));
			            results.put("refercon", rs.getString("refercon"));
			            results.put("fec_operacion", rs.getDate("fec_operacion"));
			            results.put("fec_operbanco", rs.getDate("fec_operbanco"));
			            results.put("impm", rs.getDouble("impm"));
			            results.put("impb", rs.getDouble("impb"));
			            results.put("importemov", rs.getDouble("importemov"));
			            results.put("importecon", rs.getDouble("importecon"));
			            results.put("cam", rs.getString("cam"));
			            results.put("cab", rs.getString("cab"));
			            results.put("impcargo", rs.getDouble("impcargo"));
			            results.put("impcargob", rs.getDouble("impcargob"));
			            results.put("impabono", rs.getDouble("impabono"));
			            results.put("impabonob", rs.getDouble("impabonob"));
			            
			            return results;
					}
				});
		    
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarReporteconAclaracion");
		}
		return mapReporte;
	}
	
	/**
	 * FunSQLSelect16
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarReporteConciliadoAutomatico(CriteriosReporteDto dto){
		List<Map<String, Object>> mapReporte = new ArrayList<Map<String, Object>>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  SELECT distinct ");
            sbSQL.append("\n  m.no_empresa, m.id_chequera,m.concepto as concepto1,");
            sbSQL.append("\n  coalesce((select desc_concepto from cat_concepto_banco where id_banco=b.id_banco and id_concepto=b.concepto),b.concepto) as concepto,  ");
            sbSQL.append("\n  no_folio_det, m.no_cheque as chequemov, m.referencia as refermov, ");
            sbSQL.append("\n  m.fec_operacion as fec_operacion1, m.importe as impm, ");
            sbSQL.append("\n  case when id_tipo_movto = 'E' then m.importe*-1 ");
            sbSQL.append("\n      else m.importe end as importemov, ");
            sbSQL.append("\n  case when id_tipo_movto = 'E' then 'C' else 'A' end as cam, ");
            sbSQL.append("\n  case when id_tipo_movto = 'E' then m.importe end as impcargo,");
            sbSQL.append("\n  case when id_tipo_movto = 'I' then m.importe end as impabono, ");
            sbSQL.append("\n  b.secuencia, b.no_cheque as chequecon, b.referencia as refercon, ");
            sbSQL.append("\n  b.fec_operacion, b.importe as impb, ");
            sbSQL.append("\n  case when id_tipo_movto = 'E' then b.importe *-1 else b.importe end as importecon, ");
            sbSQL.append("\n  case when cargo_abono = 'E' then 'C' else 'A' end as cab, ");
            sbSQL.append("\n  case when cargo_abono = 'E' then b.importe end as impcargob, ");
            sbSQL.append("\n  case when cargo_abono = 'I' then b.importe end as impabonob ");
            sbSQL.append("\n  FROM cruce_concilia c, hist_movimiento m , concilia_banco b ");
            sbSQL.append("\n  WHERE ");
            sbSQL.append("\n  m.no_empresa = " + dto.getINoEmpresa());
            sbSQL.append("\n  and m.no_empresa = b.no_empresa ");
            sbSQL.append("\n  and m.no_empresa = c.no_empresa ");
            sbSQL.append("\n  and m.no_folio_det = c.no_folio_1 ");
            sbSQL.append("\n  and b.secuencia = c.no_folio_2 ");
            sbSQL.append("\n  and m.id_estatus_cb = 'C' ");
            sbSQL.append("\n  and m.id_estatus_cb = b.id_estatus_cb ");
            sbSQL.append("\n  and m.id_estatus_cb = c.tipo_concilia ");
            sbSQL.append("\n  and m.id_banco = " + dto.getIIdBanco());
            sbSQL.append("\n  and m.id_banco = b.id_banco ");
            sbSQL.append("\n  and m.id_banco = c.id_banco ");
            
            if(dto.isBValChequera())
                sbSQL.append("\n  and m.id_chequera like '" + dto.getSChequera() + "' ");
            
            sbSQL.append("\n  and m.id_chequera = b.id_chequera ");
            sbSQL.append("\n  and m.id_chequera = c.id_chequera ");
            sbSQL.append("\n  and c.fuente_movto = '1' ");
            sbSQL.append("\n  and m.fec_valor between convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaIni()) + "', 103) and convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaFin()) + "', 103) ");
            sbSQL.append("\n  and b.fec_operacion between convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaIni()) + "', 103) and convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaFin()) + "', 103) ");
            sbSQL.append("\n UNION");
            sbSQL.append("\n  SELECT distinct ");
            sbSQL.append("\n  m.no_empresa, m.id_chequera,m.concepto as concepto1,");
            sbSQL.append("\n  coalesce((select desc_concepto from cat_concepto_banco where id_banco=b.id_banco and id_concepto=b.concepto),b.concepto) as concepto,  ");
            sbSQL.append("\n  no_folio_det, m.no_cheque as chequemov, m.referencia as refermov, ");
            sbSQL.append("\n  m.fec_operacion as fec_operacion1, m.importe as impm, ");
            sbSQL.append("\n  case when id_tipo_movto = 'E' then m.importe*-1 ");
            sbSQL.append("\n      else m.importe end as importemov, ");
            sbSQL.append("\n  case when id_tipo_movto = 'E' then 'C' else 'A' end as cam, ");
            sbSQL.append("\n  case when id_tipo_movto = 'E' then m.importe end as impcargo,");
            sbSQL.append("\n  case when id_tipo_movto = 'I' then m.importe end as impabono, ");
            sbSQL.append("\n  b.secuencia, b.no_cheque as chequecon, b.referencia as refercon, ");
            sbSQL.append("\n  b.fec_operacion, b.importe as impb, ");
            sbSQL.append("\n  case when id_tipo_movto = 'E' then b.importe *-1 else b.importe end as importecon, ");
            sbSQL.append("\n  case when cargo_abono = 'E' then 'C' else 'A' end as cab, ");
            sbSQL.append("\n  case when cargo_abono = 'E' then b.importe end as impcargob, ");
            sbSQL.append("\n  case when cargo_abono = 'I' then b.importe end as impabonob ");
            sbSQL.append("\n  FROM cruce_concilia c, movimiento m , concilia_banco b ");
            sbSQL.append("\n  WHERE ");
            sbSQL.append("\n  m.no_empresa = " + dto.getINoEmpresa());
            sbSQL.append("\n  and m.no_empresa = b.no_empresa ");
            sbSQL.append("\n  and m.no_empresa = c.no_empresa ");
            sbSQL.append("\n  and m.no_folio_det = c.no_folio_1 ");
            sbSQL.append("\n  and b.secuencia = c.no_folio_2 ");
            sbSQL.append("\n  and m.id_estatus_cb = 'C' ");
            sbSQL.append("\n  and m.id_estatus_cb = b.id_estatus_cb ");
            sbSQL.append("\n  and m.id_estatus_cb = c.tipo_concilia ");
            sbSQL.append("\n  and m.id_banco = " + dto.getIIdBanco());
            sbSQL.append("\n  and m.id_banco = b.id_banco ");
            sbSQL.append("\n  and m.id_banco = c.id_banco ");
            
            if(dto.isBValChequera())
                sbSQL.append("\n  and m.id_chequera like '" + dto.getSChequera() + "' ");
            
            sbSQL.append("\n  and m.id_chequera = b.id_chequera ");
            sbSQL.append("\n  and m.id_chequera = c.id_chequera ");
            sbSQL.append("\n  and c.fuente_movto = '1' ");
            sbSQL.append("\n  and m.fec_valor between convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaIni()) + "', 103) and convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaFin()) + "', 103) ");
            sbSQL.append("\n  and b.fec_operacion between convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaIni()) + "', 103) and convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaFin()) + "', 103) ");
            sbSQL.append("\n  order by b.fec_operacion");
            
            System.out.println("consultarReporteConciliadoAutomatico"+sbSQL.toString());
            mapReporte = (List<Map<String, Object>>)jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("no_empresa", rs.getString("no_empresa"));
			            results.put("concepto1", rs.getString("concepto1"));
			            results.put("concepto", rs.getString("concepto"));
			            results.put("id_chequera", rs.getString("id_chequera"));
			            results.put("secuencia", rs.getString("secuencia"));
			            results.put("no_folio_det", rs.getString("no_folio_det"));
			            results.put("chequemov", rs.getString("chequemov"));
			            results.put("chequecon", rs.getString("chequecon"));
			            results.put("refermov", rs.getString("refermov"));
			            results.put("refercon", rs.getString("refercon"));
			            results.put("fec_operacion", rs.getDate("fec_operacion"));
			            results.put("fec_operacion1", rs.getDate("fec_operacion1"));
			            results.put("impm", rs.getDouble("impm"));
			            results.put("impb", rs.getDouble("impb"));
			            results.put("importemov", rs.getDouble("importemov"));
			            results.put("importecon", rs.getDouble("importecon"));
			            results.put("cam", rs.getString("cam"));
			            results.put("cab", rs.getString("cab"));
			            results.put("impcargo", rs.getDouble("impcargo"));
			            results.put("impcargob", rs.getDouble("impcargob"));
			            results.put("impabono", rs.getDouble("impabono"));
			            results.put("impabonob", rs.getDouble("impabonob"));
			            
			            return results;
					}
				});

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarReporteConciliadoAutomatico");
		}
		return mapReporte;
	}
	
	/**
	 * FunSQLSelect20
	 * consulta de los reportes: detectados, pendientes por conciliar
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarReporteMovimientosDetectados(CriteriosReporteDto dto){
		List<Map<String, Object>> mapReporte = new ArrayList<Map<String, Object>>();
		StringBuffer sbSQL = new StringBuffer();
		StringBuffer sbSQLAdd = new StringBuffer();
		try{

			sbSQLAdd.append("\n (select distinct");
			sbSQLAdd.append("\n  id_divisa ");
		    sbSQLAdd.append("\n  FROM cat_cta_banco");
		    sbSQLAdd.append("\n  WHERE no_empresa = " + dto.getINoEmpresa());
		    sbSQLAdd.append("\n  and id_banco= " + dto.getIIdBanco());
		    
		    if(dto.isBValChequera())
		        sbSQLAdd.append("\n  and id_chequera = '" + dto.getSChequera() + "'");
		    sbSQLAdd.append("\n  ) as id_divisa_encabezado, ");
			
		    sbSQL.append("\n  SELECT ");
		    
		    sbSQL.append(sbSQLAdd.toString());
		    
		    sbSQL.append("\n  '1.- SALDO SEGUN ESTADO DE CUENTA BANCARIO:' as cargo_abono,");
		    sbSQL.append("\n  h.cargo as cargo,");
		    sbSQL.append("\n  h.abono as abono,");
		    sbSQL.append("\n (select importe from movto_banca_e m");
		    sbSQL.append("\n Where");
		    sbSQL.append("\n id_banco = " + dto.getIIdBanco());
		    sbSQL.append("\n and m.no_empresa =" + dto.getINoEmpresa());
		    sbSQL.append("\n and m.fec_valor = convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaFin()) + "',103)");
		    sbSQL.append("\n and cargo_abono='S'");
		    sbSQL.append("\n and h.id_banco = m.id_banco");
		    sbSQL.append("\n and h.id_chequera = m.id_chequera ) c_a,");
		    sbSQL.append("\n  h.fec_valor,");
		    sbSQL.append("\n  ' ' as marca,");
		    sbSQL.append("\n  0 as no_folio_det  ,");
		    sbSQL.append("\n  ' ' as referencia,");
		    sbSQL.append("\n  '0' as no_cheque,");
		    sbSQL.append("\n (select importe from movto_banca_e m");
		    sbSQL.append("\n Where");
		    sbSQL.append("\n id_banco = " + dto.getIIdBanco());
		    sbSQL.append("\n and m.no_empresa =" + dto.getINoEmpresa());
		    sbSQL.append("\n and m.fec_valor = convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaFin()) + "',103)");
		    sbSQL.append("\n and cargo_abono='S'");
		    sbSQL.append("\n and h.id_banco = m.id_banco");
		    sbSQL.append("\n and h.id_chequera = m.id_chequera ) importe,");
		    sbSQL.append("\n  ' ' as lista ,  ");
		    sbSQL.append("\n  h.no_empresa, h.id_banco, h.id_chequera,  ");
		    sbSQL.append("\n  h.saldo_final as saldo_final ,");
		    sbSQL.append("\n  (coalesce(h.saldo_banco_ini,0) + coalesce(h.saldo_final,0)*-1) as diferencia, c.id_divisa ");
		    sbSQL.append("\n  FROM  hist_cat_cta_banco h , cat_cta_banco c ");
		    sbSQL.append("\n  WHERE");
		    sbSQL.append("\n  c.id_banco = " + dto.getIIdBanco());
		    sbSQL.append("\n  and c.no_empresa = " + dto.getINoEmpresa());
		    sbSQL.append("\n  and h.fec_valor = convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaFin()) + "',103)");
		    sbSQL.append("\n  and h.no_empresa = c.no_empresa   ");
		    sbSQL.append("\n  and h.id_banco = c.id_banco   ");
		    sbSQL.append("\n  and h.id_chequera = c.id_chequera ");
	        sbSQL.append("\n  and h.id_chequera = '" + dto.getSChequera() + "'");
		    
		    sbSQL.append("\n  UNION");
		    sbSQL.append("\n  SELECT");
		    sbSQL.append(sbSQLAdd.toString());
		    sbSQL.append("\n  case when h.id_tipo_movto = 'I'");
		    sbSQL.append("\n      then '5.- MAS: ABONOS DEL SET NO CORRESPONDIDOS POR EL BANCO '");
		    sbSQL.append("\n      else '4.- MENOS: CARGOS DEL SET NO CORRESPONDIDOS POR EL BANCO '");
		    sbSQL.append("\n      end as cargo_abono,");
		    sbSQL.append("\n  case when h.id_tipo_movto = 'E' then h.importe else 0 end as cargo,");
		    sbSQL.append("\n  case when h.id_tipo_movto = 'I' then h.importe else 0 end as abono ,");
		    sbSQL.append("\n  case when h.id_tipo_movto = 'I' then h.importe else h.importe *-1 end as c_a,");
		    sbSQL.append("\n  h.fec_valor,");
		    sbSQL.append("\n  'H' as marca,");
		    sbSQL.append("\n  h.no_folio_det,");
		    sbSQL.append("\n  h.referencia,");
		    
		    /*if(ConstantesSet.gsDBM.equals("SYBASE") || ConstantesSet.gsDBM.equals("SQL SERVER"))
		        sbSQL.append("\n  convert(varchar, h.no_cheque) as no_cheque,");
		    else*/
		        sbSQL.append("\n  convert(char,h.no_cheque) as no_cheque,");

		    sbSQL.append("\n  h.importe as importe,");
		    sbSQL.append("\n  h.beneficiario as lista , ");
		    sbSQL.append("\n  h.no_empresa, h.id_banco, h.id_chequera,  ");
		    sbSQL.append("\n  0 as saldo_final  ,  ");
		    sbSQL.append("\n  case when h.id_tipo_movto = 'I' then h.importe else h.importe *-1 end as diferencia, c.id_divisa   ");
		    sbSQL.append("\n  FROM  hist_movimiento h , cat_cta_banco c  ");
		    sbSQL.append("\n  WHERE");
		    sbSQL.append("\n  h.id_estatus_cb in ( '" + dto.getIdEstatusCb1() + "','" + dto.getIdEstatusCb2() +"')");
		    sbSQL.append("\n  and h.id_forma_pago in (1,3,4,5,6,7,8)");
		    sbSQL.append("\n  and c.no_empresa = " + dto.getINoEmpresa());
		    sbSQL.append("\n  and h.id_estatus_mov <> 'H'");
		    sbSQL.append("\n  and ((h.id_tipo_movto IN ('E','I') and ((h.id_tipo_operacion = 3200" );
		    sbSQL.append("\n  and ((h.id_estatus_mov in ('I','R','K','A') and h.b_entregado = 'S') or  h.id_estatus_mov in ('K')))");
		    sbSQL.append("\n  or (h.id_tipo_operacion between 3700 and 3799 and h.id_estatus_mov IN ('A','K','I'))");
		    sbSQL.append("\n  or (h.id_tipo_operacion in (1016,1017,1018,1012,1015,1021,1023) and h.id_estatus_mov IN ('A','K','I'))");
		    sbSQL.append("\n  or (h.id_tipo_operacion in (4001,4104)  and h.id_estatus_mov IN ('A','K','I'))))");
		    sbSQL.append("\n  or (h.id_tipo_movto IN ('I','E')  and ((h.id_tipo_operacion between 3100 and 3199 and h.id_estatus_mov IN ('A','K','I'))");
		    sbSQL.append("\n  or (h.id_tipo_operacion between 3700 and 3799 and h.id_estatus_mov IN ('A','K','I'))");
		    sbSQL.append("\n  or (h.id_tipo_operacion in (3500,4102,4103) and  h.id_estatus_mov in ('A','W','K','I')))))");
		    sbSQL.append("\n  and c.id_banco = " + dto.getIIdBanco());
		    sbSQL.append("\n  and h.fec_valor >= convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaIni()) + "', 103)");
		    sbSQL.append("\n  and h.fec_valor <= convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaFin()) + "', 103)");
		    sbSQL.append("\n  and h.no_empresa = c.no_empresa   ");
		    sbSQL.append("\n  and h.id_banco = c.id_banco  ");
		    sbSQL.append("\n  and h.id_chequera = c.id_chequera  ");
	        sbSQL.append("\n  and h.id_chequera = '" + dto.getSChequera() + "'");

		    sbSQL.append("\n  UNION");

		    sbSQL.append("\n  SELECT");
		    sbSQL.append(sbSQLAdd.toString());
		    sbSQL.append("\n  case when h.id_tipo_movto = 'I'");
		    sbSQL.append("\n      then '5.- MAS: ABONOS DEL SET NO CORRESPONDIDOS POR EL BANCO '");
		    sbSQL.append("\n      else '4.- MENOS: CARGOS DEL SET NO CORRESPONDIDOS POR EL BANCO '");
		    sbSQL.append("\n      end as cargo_abono,");
		    sbSQL.append("\n  case when h.id_tipo_movto = 'E' then h.importe else 0 end as cargo,");
		    sbSQL.append("\n  case when h.id_tipo_movto = 'I' then h.importe else 0 end as abono,");
		    sbSQL.append("\n  case when h.id_tipo_movto = 'I' then h.importe else h.importe *-1 end as c_a,");
		    sbSQL.append("\n  h.fec_valor,");
		    sbSQL.append("\n  'H' as marca,");
		    sbSQL.append("\n  h.no_folio_det,");
		    sbSQL.append("\n  h.referencia,");
		    
//		    if(ConstantesSet.gsDBM.equals("SYBASE") || ConstantesSet.gsDBM.equals("SQL SERVER"))
//		        sbSQL.append("\n  convert(varchar, h.no_cheque) as no_cheque,");
//		    else
		        sbSQL.append("\n  convert(char,h.no_cheque) as no_cheque,");
		    
		    sbSQL.append("\n  h.importe as importe,");
		    sbSQL.append("\n  h.beneficiario as lista , ");
		    sbSQL.append("\n  h.no_empresa, h.id_banco, h.id_chequera,  ");
		    sbSQL.append("\n  0 as saldo_final,  ");
		    sbSQL.append("\n  case when h.id_tipo_movto = 'I' then h.importe else h.importe *-1 end as diferencia, c.id_divisa  ");
		    sbSQL.append("\n  FROM  movimiento h, cat_cta_banco c  ");
		    sbSQL.append("\n  WHERE");
		    //sbSQL.append("\n  h.id_tipo_movto IN ('E','I')");
		    //sbSQL.append("\n  and h.id_tipo_operacion =3000" );
		    //sbSQL.append("\n  and h.id_estatus_cb = '" + dto.getIdEstatusCb1() + "'");
		    sbSQL.append("\n  h.id_estatus_cb in ( '" + dto.getIdEstatusCb1() + "','" + dto.getIdEstatusCb2() +"')");
		    sbSQL.append("\n  and ((h.b_entregado = 'N' and h.id_estatus_mov in ('I','R'))");
		    sbSQL.append("\n      or (h.id_estatus_mov in('T','P','J')))");
		    sbSQL.append("\n  and c.no_empresa = " + dto.getINoEmpresa());
		    sbSQL.append("\n  and c.id_banco = " + dto.getIIdBanco());
		    sbSQL.append("\n  and h.fec_valor >= convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaIni()) + "', 103)");
		    sbSQL.append("\n  and h.fec_valor <= convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaFin()) + "', 103)");
		    sbSQL.append("\n  and h.no_empresa = c.no_empresa  ");
		    sbSQL.append("\n  and h.id_banco = c.id_banco ");
		    sbSQL.append("\n  and h.id_chequera = c.id_chequera  ");
	        sbSQL.append("\n  and h.id_chequera = '" + dto.getSChequera() + "'");

		    sbSQL.append("\n  UNION");

		    sbSQL.append("\n  SELECT");
		    sbSQL.append(sbSQLAdd.toString());
		    sbSQL.append("\n  case when h.id_tipo_movto = 'I'");
		    sbSQL.append("\n      then '5.- MAS: ABONOS DEL SET NO CORRESPONDIDOS POR EL BANCO '");
		    sbSQL.append("\n      else '4.- MENOS: CARGOS DEL SET NO CORRESPONDIDOS POR EL BANCO '");
		    sbSQL.append("\n      end as cargo_abono,");
		    sbSQL.append("\n  case when h.id_tipo_movto = 'E' then h.importe else 0 end as cargo,");
		    sbSQL.append("\n  case when h.id_tipo_movto = 'I' then h.importe else 0 end as abono ,");
		    sbSQL.append("\n  case when h.id_tipo_movto = 'I' then h.importe else h.importe *-1 end as c_a,");
		    sbSQL.append("\n  h.fec_valor,");
		    sbSQL.append("\n  case when id_tipo_operacion = 3200 and h.id_estatus_mov in ('P') and id_forma_pago = 1 then 'I' ");
		    sbSQL.append("\n  when id_tipo_operacion = 3200 and h.id_estatus_mov in ('I', 'R') and id_forma_pago = 1 and b_entregado = 'N' then 'E' ");
		    sbSQL.append("\n  when id_tipo_operacion = 3200 and h.id_estatus_mov in ('I', 'R') and id_forma_pago = 1 and b_entregado = 'S' then 'CH' ");
		    sbSQL.append("\n  when id_tipo_operacion = 3200 and h.id_estatus_mov in ('P') and id_forma_pago = 3 then 'T' ");
		    sbSQL.append("\n  when id_tipo_operacion = 3200 and h.id_estatus_mov in ('T') and id_forma_pago = 3 then 'C' ");
		    sbSQL.append("\n  when id_tipo_operacion = 3200 and h.id_estatus_mov in ('K') and id_forma_pago = 3 then 'TCH' ");
		    sbSQL.append("\n  when id_tipo_operacion in (3107, 3108) and h.id_estatus_mov in ('A', 'P') then 'R' ");
		    sbSQL.append("\n  when id_tipo_operacion in (3100, 3101) and h.id_estatus_mov in ('A', 'P') then 'DH' ");
		    sbSQL.append("\n  when id_tipo_operacion in (3102, 3103, 3152, 3153, 3120) and h.id_estatus_mov in ('A', 'P') and b_salvo_buen_cobro = 'N' then 'S' ");
		    sbSQL.append("\n  when id_tipo_operacion in (3102, 3103, 3152, 3153, 3120) and h.id_estatus_mov in ('A') and b_salvo_buen_cobro = 'S' then 'DH' ");
		    sbSQL.append("\n  when id_tipo_operacion between 3700 and 3799 and h.id_estatus_mov in ('A') then 'TH' ");
		    sbSQL.append("\n  when id_tipo_operacion in (1016,1017,1018,1012,1015,1021,1023) and h.id_estatus_mov = 'A' then 'HC' ");
		    sbSQL.append("\n  else '*' end as marca, ");
		    sbSQL.append("\n  h.no_folio_det,");
		    sbSQL.append("\n  h.referencia,");
		    
//		    if(ConstantesSet.gsDBM.equals("SYBASE") || ConstantesSet.gsDBM.equals("SQL SERVER"))
//		        sbSQL.append("\n  convert(varchar, h.no_cheque) as no_cheque,");
//		    else
		        sbSQL.append("\n  convert(char,h.no_cheque) as no_cheque,");
		    
		    sbSQL.append("\n  h.importe as importe,");
		    sbSQL.append("\n  h.beneficiario as lista , ");
		    sbSQL.append("\n  h.no_empresa, h.id_banco, h.id_chequera,  ");
		    sbSQL.append("\n  0 as saldo_final  ,  ");
		    sbSQL.append("\n  case when h.id_tipo_movto = 'I' then h.importe else h.importe *-1 end as diferencia, c.id_divisa   ");
		    sbSQL.append("\n  FROM  hist_movimiento h , cat_cta_banco c  ");
		    sbSQL.append("\n  WHERE");
		    sbSQL.append("\n h.id_estatus_cb in ( '" + dto.getIdEstatusCb1() + "','" + dto.getIdEstatusCb2() +"')");
		    sbSQL.append("\n  and h.id_forma_pago in (1,3,4,5,6,7,8)");
		    sbSQL.append("\n  and c.no_empresa = " + dto.getINoEmpresa());
		    sbSQL.append("\n  and h.id_estatus_mov <> 'H'");
		    sbSQL.append("\n  and ((h.id_tipo_movto IN ('E','I') and ((h.id_tipo_operacion=3200" );
		    sbSQL.append("\n  and (h.id_estatus_mov in ('K', 'T', 'P','I','A')))");
		    sbSQL.append("\n  or (h.id_tipo_operacion between 3700 and 3799 and h.id_estatus_mov IN ('A','K','I'))");
		    sbSQL.append("\n  or (h.id_tipo_operacion in (1016,1017,1018,1012,1015,1021,1023) and h.id_estatus_mov IN ('A','K','I'))");
		    sbSQL.append("\n  or (h.id_tipo_operacion in (4001,4104)  and h.id_estatus_mov IN ('A','K','I'))))");
		    sbSQL.append("\n  or (h.id_tipo_movto IN ('I','E')  and ((h.id_tipo_operacion between 3100 and 3199 and h.id_estatus_mov IN ('A','K','I'))");
		    sbSQL.append("\n  or (h.id_tipo_operacion between 3700 and 3799 and h.id_estatus_mov IN ('A','K','I'))");
		    sbSQL.append("\n  or (h.id_tipo_operacion in (3500,4102,4103) and  h.id_estatus_mov in ('A','W','K','I')))))");
		    sbSQL.append("\n  and c.id_banco = " + dto.getIIdBanco());
		    sbSQL.append("\n  and h.fec_valor >= convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaIni()) + "', 103)");
		    sbSQL.append("\n  and h.fec_valor <= convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaFin()) + "', 103)");
		    sbSQL.append("\n  and h.no_empresa = c.no_empresa   ");
		    sbSQL.append("\n  and h.id_banco = c.id_banco  ");
		    sbSQL.append("\n  and h.id_chequera = c.id_chequera  ");
	        sbSQL.append("\n  and h.id_chequera = '" + dto.getSChequera() + "'");

		    sbSQL.append("\n  UNION");

		    sbSQL.append("\n  SELECT");
		    sbSQL.append(sbSQLAdd.toString());
		    sbSQL.append("\n  case when m.id_tipo_movto = 'I'");
		    sbSQL.append("\n      then '5.- MAS: ABONOS DEL SET NO CORRESPONDIDOS POR EL BANCO '");
		    sbSQL.append("\n      else '4.- MENOS: CARGOS DEL SET NO CORRESPONDIDOS POR EL BANCO '");
		    sbSQL.append("\n      end as cargo_abono,");
		    sbSQL.append("\n  case when m.id_tipo_movto = 'E' then m.importe else 0 end as cargo,");
		    sbSQL.append("\n  case when m.id_tipo_movto = 'I' then m.importe else 0 end as abono,");
		    sbSQL.append("\n  case when m.b_entregado='N' then  m.importe *-1 when m.id_tipo_movto = 'I' then importe else m.importe *-1 end as c_a,");
		    sbSQL.append("\n  m.fec_valor,");
		    sbSQL.append("\n  case when m.b_entregado='N' then 'E' else 'CH' end as marca,");
		    sbSQL.append("\n  m.no_folio_det,");
		    sbSQL.append("\n  m.referencia,");
		    
//		    if(ConstantesSet.gsDBM.equals("SYBASE") || ConstantesSet.gsDBM.equals("SQL SERVER"))
//		        sbSQL.append("\n  convert(varchar, m.no_cheque) as no_cheque,");
//		    else
		        sbSQL.append("\n  convert(char,m.no_cheque) as no_cheque,");
		  
		    sbSQL.append("\n  m.importe as importe,");
		    sbSQL.append("\n  m.beneficiario as lista ,  ");
		    sbSQL.append("\n  m.no_empresa, m.id_banco, m.id_chequera,  ");
		    sbSQL.append("\n  0 as saldo_final ,  ");
		    sbSQL.append("\n  case when m.id_tipo_movto = 'I' then m.importe else m.importe *-1 end as diferencia, c.id_divisa  ");
		    sbSQL.append("\n  FROM movimiento m, cat_cta_banco c   ");
		    sbSQL.append("\n  WHERE");
		    sbSQL.append("\n  m.id_tipo_movto in ('E','I')");
		    sbSQL.append("\n  and m.id_tipo_operacion = 3200" );
		    sbSQL.append("\n  and m.id_estatus_cb = '" + dto.getIdEstatusCb1() + "'");
		    sbSQL.append("\n  and ((m.id_estatus_mov in ('I','R') and m.b_entregado in ('N', 'S')) or (m.id_estatus_mov in ('T','P','J')))");
		    sbSQL.append("\n  and c.no_empresa = " + dto.getINoEmpresa());
		    sbSQL.append("\n  and c.id_banco = " + dto.getIIdBanco());
		    sbSQL.append("\n  and m.fec_valor >= convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaIni()) + "', 103)");
		    sbSQL.append("\n  and m.fec_valor <= convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaFin()) + "', 103)");
		    sbSQL.append("\n  and m.no_empresa = c.no_empresa   ");
		    sbSQL.append("\n  and m.id_banco = c.id_banco   ");
		    sbSQL.append("\n  and m.id_chequera = c.id_chequera  ");
	        sbSQL.append("\n  and m.id_chequera = '" + dto.getSChequera() + "'");

		    sbSQL.append("\n  UNION");
		    
		    sbSQL.append("\n  SELECT");
		    sbSQL.append(sbSQLAdd.toString());
		    sbSQL.append("\n  case when b.cargo_abono = 'E'");
		    sbSQL.append("\n      then '2.- MAS: CARGOS DEL BANCO NO CORRESPONDIDOS POR EL SET '");
		    sbSQL.append("\n      else '3.- MENOS: ABONOS DEL BANCO NO CORRESPONDIDOS POR EL SET ' end");
		    sbSQL.append("\n      as cargo_abono  ,");
		    sbSQL.append("\n  case when b.cargo_abono = 'E' then b.importe else 0 end as cargo,");
		    sbSQL.append("\n  case when b.cargo_abono = 'I' then b.importe else 0 end as abono,");
		    sbSQL.append("\n  case when b.cargo_abono = 'E' then b.importe else b.importe *-1 end as c_a,");
		    sbSQL.append("\n  b.fec_operacion as fec_valor,");
		    sbSQL.append("\n  ' ' as marca,");
		    sbSQL.append("\n  b.secuencia,");
		    sbSQL.append("\n  b.referencia,");
		    
//		    if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
		        sbSQL.append("\n  convert(char,b.no_cheque) as no_cheque,");
//		    else if(ConstantesSet.gsDBM.equals("SYBASE") || ConstantesSet.gsDBM.equals("SQL SERVER"))
//		        sbSQL.append("\n  convert(varchar, b.no_cheque) as no_cheque,");
		    
		    sbSQL.append("\n  b.importe as importe,");
		    sbSQL.append("\n  coalesce((select desc_concepto from cat_concepto_banco where id_banco=b.id_banco and id_concepto=concepto),concepto) as lista,");
		    sbSQL.append("\n  b.no_empresa, b.id_banco, b.id_chequera,  ");
		    sbSQL.append("\n  0 as saldo_final ,  ");
		    sbSQL.append("\n  case when b.cargo_abono = 'E' then b.importe else b.importe *-1 end as diferencia, c.id_divisa   ");
		    sbSQL.append("\n  FROM concilia_banco b, cat_cta_banco c");
		    sbSQL.append("\n  WHERE");
		    sbSQL.append("\n  c.no_empresa  = " + dto.getINoEmpresa());
		    sbSQL.append("\n  and b.id_estatus_cb in ( '" + dto.getIdEstatusCb1() + "','" + dto.getIdEstatusCb2() +"')");
		    sbSQL.append("\n  and c.id_banco = " + dto.getIIdBanco());
		    sbSQL.append("\n  and b.fec_operacion >= convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaIni()) + " 00:00', 103)");
		    sbSQL.append("\n  and b.fec_operacion <= convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaFin()) + " 23:59', 103)");
		    sbSQL.append("\n  and b.no_empresa = c.no_empresa   ");
		    sbSQL.append("\n  and b.id_banco = c.id_banco   ");
		    sbSQL.append("\n  and b.id_chequera = c.id_chequera  ");
	        sbSQL.append("\n  and b.id_chequera = '" + dto.getSChequera() + "'");
		    sbSQL.append("\n  order by cargo_abono, fec_valor, importe desc");
		    
	    System.out.println("consultarReporteMovimientosDetectados \n"+sbSQL.toString());
		    mapReporte = (List<Map<String, Object>>)jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("id_divisa_encabezado", rs.getString("id_divisa_encabezado"));
			            results.put("cargo_abono", rs.getString("cargo_abono"));
			            results.put("cargo", rs.getString("cargo"));
			            results.put("abono", rs.getString("abono"));
			            results.put("c_a", rs.getDouble("c_a"));
			            results.put("fec_valor", rs.getDate("fec_valor"));
			            results.put("marca", rs.getString("marca"));
			            results.put("no_folio_det", rs.getString("no_folio_det"));
			            results.put("referencia", rs.getString("referencia"));
			            results.put("no_cheque", rs.getString("no_cheque"));
			            results.put("importe", rs.getDouble("importe"));
			            results.put("lista", rs.getString("lista"));
			            results.put("no_empresa", rs.getString("no_empresa"));
			            results.put("id_banco", rs.getString("id_banco"));
			            results.put("id_chequera", rs.getString("id_chequera"));
			            results.put("saldo_final", rs.getDouble("saldo_final"));
			            results.put("diferencia", rs.getDouble("diferencia"));
			            results.put("id_divisa", rs.getString("id_divisa"));
			            
			            return results;
					}
				});
			    
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarReporteMovimientosDetectados");
		}
		return mapReporte;
	}
	
	/**
	 * FunSQLSelect18
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarReporteAjustes(CriteriosReporteDto dto){
		List<Map<String, Object>> mapReporte = new ArrayList<Map<String, Object>>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n   SELECT ");
            sbSQL.append("\n   h.id_chequera, ");
            sbSQL.append("\n   case when  h.id_tipo_movto = 'E' then 'C' ");
            sbSQL.append("\n      when h.id_tipo_movto = 'I' then 'A' else ' ' end as cargo_abono, ");
            sbSQL.append("\n   h.fec_operacion , h.no_folio_det, h.importe ");
            sbSQL.append("\n   FROM hist_movimiento h");
            sbSQL.append("\n   WHERE id_tipo_operacion in (1019,1020) ");
            sbSQL.append("\n   and h.no_empresa = " + dto.getINoEmpresa());
            sbSQL.append("\n   and h.id_banco = " + dto.getIIdBanco());
		    sbSQL.append("\n  and h.fec_valor >= convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaIni()) + "', 103)");
		    sbSQL.append("\n  and h.fec_valor <= convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaFin()) + "', 103)");
            
            if(dto.isBValChequera())
                sbSQL.append("\n   and h.id_chequera = '" + dto.getSChequera() + "' ");
            
            sbSQL.append("\n   UNION ");
            sbSQL.append("\n   SELECT ");
            sbSQL.append("\n   h.id_chequera, ");
            sbSQL.append("\n   case when  h.id_tipo_movto = 'E' then 'C' ");
            sbSQL.append("\n      when h.id_tipo_movto = 'I' then 'A' else ' ' end as cargo_abono, ");
            sbSQL.append("\n   h.fec_operacion , h.no_folio_det, h.importe ");
            sbSQL.append("\n   FROM movimiento h");
            sbSQL.append("\n   WHERE id_tipo_operacion in (1019,1020) ");
            sbSQL.append("\n   and h.no_empresa = " + dto.getINoEmpresa());
            sbSQL.append("\n   and h.id_banco = " + dto.getIIdBanco());
		    sbSQL.append("\n  and h.fec_valor >= convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaIni()) + "', 103)");
		    sbSQL.append("\n  and h.fec_valor <= convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaFin()) + "', 103)");
            
            if(dto.isBValChequera())
                sbSQL.append("\n   and h.id_chequera = '" + dto.getSChequera() + "' ");
            
            System.out.println("sbSQL " + sbSQL.toString());
            mapReporte = (List<Map<String, Object>>)jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("id_chequera", rs.getString("id_chequera"));
			            results.put("cargo_abono", rs.getString("cargo_abono"));
			            results.put("fec_operacion", rs.getDate("fec_operacion"));
			            results.put("no_folio_det", rs.getString("no_folio_det"));
			            results.put("importe", rs.getDouble("importe"));
			            
			            return results;
					}
				});
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarReporteAjustes");
		}
		return mapReporte;
	}
	
	/**
	 * FunSQLSelect19
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarReporteMovimientosFicticios(CriteriosReporteDto dto){
		List<Map<String, Object>> mapReporte = new ArrayList<Map<String, Object>>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n SELECT");
		    sbSQL.append("\n  m.id_chequera, ");
		    sbSQL.append("\n  m.no_cheque as chequemov,");
		    sbSQL.append("\n  m.referencia as refermov,");
		    sbSQL.append("\n  m.fec_operacion,");
		    sbSQL.append("\n  m.importe as impm,");
		    sbSQL.append("\n  case when m.id_tipo_movto = 'E' then m.importe *-1 else m.importe end as importemov ,");
		    sbSQL.append("\n  case when m.id_tipo_movto = 'E' then 'C' when m.id_tipo_movto = 'I' then 'A' else ' ' end as cam ,");
		    sbSQL.append("\n  case when m.id_tipo_movto = 'E' then m.importe end as impcargo ,");
		    sbSQL.append("\n  case when m.id_tipo_movto = 'I' then m.importe end as impabono ");
		    sbSQL.append("\n  FROM");
		    sbSQL.append("\n  hist_movimiento m ");
		    sbSQL.append("\n  WHERE");
		    sbSQL.append("\n  m.no_empresa = " + dto.getINoEmpresa());
		    sbSQL.append("\n  and m.id_banco = " + dto.getIIdBanco());
		    //s
		    if(dto.isBValChequera())
		        sbSQL.append("\n  and m.id_chequera like '" + dto.getSChequera() + "'");
		    
		    sbSQL.append("\n  and m.id_estatus_cb = 'F'");
		    sbSQL.append("\n  and m.fec_valor between convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaIni()) + "',103) and convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaFin()) + "',103)");
		    
		    sbSQL.append("\n UNION");
		    sbSQL.append("\n SELECT");
		    sbSQL.append("\n  m.id_chequera, ");
		    sbSQL.append("\n  m.no_cheque as chequemov,");
		    sbSQL.append("\n  m.referencia as refermov,");
		    sbSQL.append("\n  m.fec_operacion,");
		    sbSQL.append("\n  m.importe as impm,");
		    sbSQL.append("\n  case when m.id_tipo_movto = 'E' then m.importe *-1 else m.importe end as importemov ,");
		    sbSQL.append("\n  case when m.id_tipo_movto = 'E' then 'C' when m.id_tipo_movto = 'I' then 'A' else ' ' end as cam ,");
		    sbSQL.append("\n  case when m.id_tipo_movto = 'E' then m.importe end as impcargo ,");
		    sbSQL.append("\n  case when m.id_tipo_movto = 'I' then m.importe end as impabono ");
		    sbSQL.append("\n  FROM");
		    sbSQL.append("\n  movimiento m ");
		    sbSQL.append("\n  WHERE");
		    sbSQL.append("\n  m.no_empresa = " + dto.getINoEmpresa());
		    sbSQL.append("\n  and m.id_banco = " + dto.getIIdBanco());
		    
		    if(dto.isBValChequera())
		        sbSQL.append("\n  and m.id_chequera like '" + dto.getSChequera() + "'");
		    
		    sbSQL.append("\n  and m.id_estatus_cb = 'F'");
		    sbSQL.append("\n  and m.fec_valor between convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaIni()) + "',103) and convert(datetime,'" + funciones.ponerFechaSola(dto.getDFechaFin()) + "',103)");
		    
		    System.out.println("sbSQL " + sbSQL.toString());
		    mapReporte = (List<Map<String, Object>>)jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("id_chequera", rs.getString("id_chequera"));
			            results.put("chequemov", rs.getString("chequemov"));
			            results.put("fec_operacion", rs.getDate("fec_operacion"));
			            results.put("refermov", rs.getString("refermov"));
			            results.put("impm", rs.getDouble("impm"));
			            results.put("importemov", rs.getDouble("importemov"));
			            results.put("cam", rs.getString("cam"));
			            results.put("impcargo", rs.getDouble("impcargo"));
			            results.put("impabono", rs.getDouble("impabono"));
			            
			            return results;
					}
				});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarReporteMovimientosFicticios");
		}
		return mapReporte;
	}
	
	/**
	 * FunSQLDelete4
	 * @param iUsuario
	 * @return
	 */
	public int borraTmpConciliaSetBanco(int iUsuario){
		int iDelete = 0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n DELETE ");
		    sbSQL.append("\n FROM ");
		    sbSQL.append("\n tmp_concilia_set_banco ");
		    sbSQL.append("\n WHERE ");
		    sbSQL.append("\n usuario_alta = " + iUsuario);
		    System.out.println("Query RRubio"+sbSQL.toString());
		    iDelete = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:borraTmpConciliaSetBanco");
		}
		return iDelete;
	}
	
	/**
	 * FunSQLFechaSaldo
	 * @param iEmpresa
	 * @param iBanco
	 * @param sChequera
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarFechaSaldo(int iEmpresa, int iBanco, String sChequera){
		StringBuffer sbSQL = new StringBuffer();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try{
			sbSQL.append("\n SELECT coalesce(max(fec_valor),'') as fecha_saldo");
		    sbSQL.append("\n   FROM movto_banca_e ");
		    sbSQL.append("\n  WHERE no_empresa=" + iEmpresa);
		    sbSQL.append("\n  and id_banco=" + iBanco);
		    sbSQL.append("\n  and id_chequera ='" + sChequera + "'");
		    System.out.println("Query "+sbSQL.toString());
		    list = (List<Map<String, Object>>)jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("fecha_saldo", rs.getString("fecha_saldo"));
			            
			            return results;
					}
				});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarFechaSaldo");
		}
		return list;
	}
	
	/**
	 * FunSQLDelete8
	 * @param iIdUsuario
	 * @return
	 */
	public int borraTmpConcenConcilia(int iIdUsuario){
		StringBuffer sbSQL = new StringBuffer();
		int iDelete = 0;
		try{
			sbSQL.append("\n  delete");
	        sbSQL.append("\n  from ");
	        sbSQL.append("\n  tmp_concen_concilia");
	        sbSQL.append("\n  where ");
	        sbSQL.append("\n  usuario_alta = " + iIdUsuario);
	        System.out.println("Query RRubio"+sbSQL.toString());
	        iDelete = jdbcTemplate.update(sbSQL.toString());
	        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:borraTmpConcenConcilia");
		}
		return iDelete;
	}
	
	/**
	 * FunSQLSelect35
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<TmpConcenConciliaDto> consultarTmpConcenConcilia(ParamBusquedaDto dto){
		List<TmpConcenConciliaDto> listLlenaTmp = new ArrayList<TmpConcenConciliaDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n  select");
		    sbSQL.append("\n  cb.no_empresa");
		    sbSQL.append("\n ,cb.id_banco");
		    sbSQL.append("\n ,desc_banco");
		    sbSQL.append("\n ,id_chequera");
		    sbSQL.append("\n ,( ");
		    sbSQL.append("\n  Select");
		    sbSQL.append("\n  count(*) ");
		    sbSQL.append("\n  From ");
		    sbSQL.append("\n  movimiento m");
		    sbSQL.append("\n  where");
		    sbSQL.append("\n  m.id_tipo_movto = 'E'");
		    sbSQL.append("\n  and m.id_tipo_operacion = 3200");
		    sbSQL.append("\n  and ((m.b_entregado = 'N'");
		    sbSQL.append("\n  and id_estatus_mov in ('I','R'))");
		    sbSQL.append("\n  or (id_estatus_mov in('T','P','J')))");
		    sbSQL.append("\n  And m.id_chequera= cb.id_chequera");
		    sbSQL.append("\n  AND m.id_banco = cb.id_banco");
		    sbSQL.append("\n  And m.fec_valor_original between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "',103) and convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "',103)");
		    sbSQL.append("\n  ) AS  pend_tot_cargo,");
		    sbSQL.append("\n  ( ");
		    sbSQL.append("\n  Select");
		    sbSQL.append("\n  sum(importe) ");
		    sbSQL.append("\n  From");
		    sbSQL.append("\n  movimiento m");
		    sbSQL.append("\n  where m.id_tipo_movto = 'E'");
		    sbSQL.append("\n  and m.id_tipo_operacion = 3200");
		    sbSQL.append("\n  and ((m.b_entregado = 'N'");
		    sbSQL.append("\n  and id_estatus_mov in ('I','R'))");
		    sbSQL.append("\n  or (id_estatus_mov in('T','P','J')))");
		    sbSQL.append("\n  And m.id_chequera= cb.id_chequera");
		    sbSQL.append("\n  AND m.id_banco = cb.id_banco");
		    sbSQL.append("\n  And m.fec_valor_original between '" + funciones.ponerFechaSola(dto.getFechaIni()) + "' and '" + funciones.ponerFechaSola(dto.getFechaFin()) + "'");
		    sbSQL.append("\n  ) AS  pend_imp_cargo,");
		    sbSQL.append("\n  ( ");
		    sbSQL.append("\n  Select");
		    sbSQL.append("\n  count(*)");
		    sbSQL.append("\n  From ");
		    sbSQL.append("\n  hist_movimiento m");
		    sbSQL.append("\n  Where ");
		    sbSQL.append("\n  m.no_empresa = cb.no_empresa");
		    sbSQL.append("\n  And id_estatus_cb in ('" + dto.getEstatus() + "','D')");
		    sbSQL.append("\n  And id_forma_pago in (1,3,4,5,6,7)");
		    sbSQL.append("\n  And m.id_divisa = cb.id_divisa");
		    sbSQL.append("\n  And m.id_banco = cb.id_banco");
		    sbSQL.append("\n  And m.id_chequera = cb.id_chequera");
		    sbSQL.append("\n  And m.fec_valor_original between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "',103) and convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "',103)");
		    sbSQL.append("\n  And ((id_tipo_movto = 'E'");
		    sbSQL.append("\n  And ((id_tipo_operacion = 3200  and ((id_estatus_mov in ('I','R')  And b_entregado = 'S') or  id_estatus_mov in ('K')))");
		    sbSQL.append("\n  Or (id_tipo_operacion between 3700  and 3799 And  id_estatus_mov = 'A')");
		    sbSQL.append("\n  Or (id_tipo_operacion in (1016,1017,1018)  And id_estatus_mov = 'A')");
		    sbSQL.append("\n  Or (id_tipo_operacion in (4001,4104)  And id_estatus_mov in ('A')))))");
		    sbSQL.append("\n  ) AS  movto_tot_cargo,");
		    sbSQL.append("\n  ( Select  sum (m.importe)");
		    sbSQL.append("\n  From  hist_movimiento m");
		    sbSQL.append("\n  Where m.no_empresa = cb.no_empresa");
		    sbSQL.append("\n  And id_estatus_cb in ('" + dto.getEstatus() + "','D') ");
		    sbSQL.append("\n  And id_forma_pago in (1,3,4,5,6,7)");
		    sbSQL.append("\n  And m.id_divisa = cb.id_divisa");
		    sbSQL.append("\n  And m.id_banco = cb.id_banco");
		    sbSQL.append("\n  And m.id_chequera = cb.id_chequera");
		    sbSQL.append("\n  And m.fec_valor_original between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "',103) and convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "',103)");
		    sbSQL.append("\n  And ((id_tipo_movto = 'E'");
		    sbSQL.append("\n  And ((id_tipo_operacion = 3200  and ((id_estatus_mov in ('I','R')   And b_entregado = 'S') or  id_estatus_mov in ('K')))");
		    sbSQL.append("\n  Or (id_tipo_operacion between 3700  and 3799 And   id_estatus_mov = 'A')");
		    sbSQL.append("\n  Or (id_tipo_operacion in (1016,1017,1018)  And id_estatus_mov = 'A')");
		    sbSQL.append("\n  Or (id_tipo_operacion in (4001,4104)  And id_estatus_mov in  ('A'))");
		    sbSQL.append("\n  )))");
		    sbSQL.append("\n  ) AS  movto_imp_cargo,");
		    sbSQL.append("\n  ( Select  count(*)");
		    sbSQL.append("\n  From  hist_movimiento m");
		    sbSQL.append("\n  Where m.no_empresa = cb.no_empresa");
		    sbSQL.append("\n  And id_estatus_cb in ('" + dto.getEstatus() + "','D')");
		    sbSQL.append("\n  And id_forma_pago in (1,3,4,5,6,7)");
		    sbSQL.append("\n  And m.id_divisa = cb.id_divisa");
		    sbSQL.append("\n  And m.id_banco = cb.id_banco");
		    sbSQL.append("\n  And m.id_chequera = cb.id_chequera");
		    sbSQL.append("\n  And m.fec_valor_original between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "',103) and convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "',103)");
		    sbSQL.append("\n  And  (id_tipo_movto = 'I'");
		    sbSQL.append("\n  And ((id_tipo_operacion between 3100 and 3199 ");
		    sbSQL.append("\n  And  id_estatus_mov = 'A')");
		    sbSQL.append("\n  Or (id_tipo_operacion between 3700 and 3799 ");
		    sbSQL.append("\n  And  id_estatus_mov = 'A')");
		    sbSQL.append("\n  Or (id_tipo_operacion in (4102,4103) ");
		    sbSQL.append("\n  And  id_estatus_mov in ('A'))))");
		    sbSQL.append("\n ) AS  movto_tot_abono,");
		    sbSQL.append("\n  (Select  sum (m.importe)");
		    sbSQL.append("\n  From  hist_movimiento m");
		    sbSQL.append("\n  Where m.no_empresa = cb.no_empresa");
		    sbSQL.append("\n  And id_estatus_cb in ('" + dto.getEstatus() + "','D') ");
		    sbSQL.append("\n  And id_forma_pago in (1,3,4,5,6,7)");
		    sbSQL.append("\n  And m.id_divisa = cb.id_divisa");
		    sbSQL.append("\n  And m.id_banco = cb.id_banco");
		    sbSQL.append("\n  And m.id_chequera = cb.id_chequera");
		    sbSQL.append("\n  And m.fec_valor_original between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "',103) and convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "',103)");
		    sbSQL.append("\n  And  (id_tipo_movto = 'I'");
		    sbSQL.append("\n  And ((id_tipo_operacion between 3100 and 3199 ");
		    sbSQL.append("\n  And  id_estatus_mov = 'A')");
		    sbSQL.append("\n  Or (id_tipo_operacion between 3700 and 3799 ");
		    sbSQL.append("\n  And  id_estatus_mov = 'A')");
		    sbSQL.append("\n  Or (id_tipo_operacion in (4102,4103 ) ");
		    sbSQL.append("\n  And  id_estatus_mov in ( 'A'))))");
		    sbSQL.append("\n ) AS  movto_imp_abono,");
		    sbSQL.append("\n  ( Select count(*)");
		    sbSQL.append("\n  From concilia_banco");
		    sbSQL.append("\n  Where no_empresa = cb.no_empresa");
		    sbSQL.append("\n  And id_estatus_cb in ('" + dto.getEstatus() + "','D')");
		    sbSQL.append("\n  And id_divisa = cb.id_divisa");
		    sbSQL.append("\n  And id_banco = cb.id_banco");
		    sbSQL.append("\n  And id_chequera = cb.id_chequera");
		    sbSQL.append("\n  And fec_operacion between '" + funciones.ponerFechaSola(dto.getFechaIni()) + "' and '" + funciones.ponerFechaSola(dto.getFechaFin()) + "'");
		    sbSQL.append("\n  And cargo_abono  = 'E'");
		    sbSQL.append("\n  ) as banco_tot_cargo,");
		    sbSQL.append("\n  ( Select sum(importe)");
		    sbSQL.append("\n  From concilia_banco");
		    sbSQL.append("\n  Where no_empresa = cb.no_empresa");
		    sbSQL.append("\n  And id_estatus_cb in ('" + dto.getEstatus() + "','D')");
		    sbSQL.append("\n  And id_divisa = cb.id_divisa");
		    sbSQL.append("\n  And id_banco = cb.id_banco");
		    sbSQL.append("\n  And id_chequera = cb.id_chequera");
		    sbSQL.append("\n  And fec_operacion between '" + funciones.ponerFechaSola(dto.getFechaIni()) + "' and '" + funciones.ponerFechaSola(dto.getFechaFin()) + "'");
		    sbSQL.append("\n  And cargo_abono  = 'E'");
		    sbSQL.append("\n  ) as banco_imp_cargo,");
		    sbSQL.append("\n  ( Select count(*)");
		    sbSQL.append("\n  From concilia_banco");
		    sbSQL.append("\n  Where no_empresa = cb.no_empresa");
		    sbSQL.append("\n  And id_estatus_cb in ('" + dto.getEstatus() + "','D')");
		    sbSQL.append("\n  And id_divisa = cb.id_divisa");
		    sbSQL.append("\n  And id_banco = cb.id_banco");
		    sbSQL.append("\n  And id_chequera = cb.id_chequera");
		    sbSQL.append("\n  And fec_operacion between '" + funciones.ponerFechaSola(dto.getFechaIni()) + "' and '" + funciones.ponerFechaSola(dto.getFechaFin()) + "'");
		    sbSQL.append("\n  And cargo_abono  = 'I'");
		    sbSQL.append("\n  ) as banco_tot_abono,");
		    sbSQL.append("\n  ( Select sum(importe)");
		    sbSQL.append("\n  From concilia_banco");
		    sbSQL.append("\n  Where no_empresa = cb.no_empresa");
		    sbSQL.append("\n  And id_estatus_cb in ('" + dto.getEstatus() + "','D')");
		    sbSQL.append("\n  And id_divisa = cb.id_divisa");
		    sbSQL.append("\n  And id_banco = cb.id_banco");
		    sbSQL.append("\n  And id_chequera = cb.id_chequera");
		    sbSQL.append("\n  And fec_operacion between '" + funciones.ponerFechaSola(dto.getFechaIni()) + "' and '" + funciones.ponerFechaSola(dto.getFechaFin()) + "'");
		    sbSQL.append("\n  And cargo_abono  = 'I'");
		    sbSQL.append("\n  ) as banco_imp_abono");
		    sbSQL.append("\n  from ");
		    sbSQL.append("\n  cat_cta_banco cb, cat_banco  b, usuario_empresa ue");
		    sbSQL.append("\n  where  cb.id_banco = b.id_banco");
		    if(dto.getIdEmpresa() != 0)
		        sbSQL.append("\n  and cb.no_empresa = " + dto.getIdEmpresa());
		  
		    sbSQL.append("\n  And cb.id_banco = b.id_banco");
		    sbSQL.append("\n  And cb.id_banco between " + dto.getIdBanco() + " and " + dto.getIdBanco2());
		    sbSQL.append("\n  And cb.id_chequera like '" + dto.getIdChequera() + "'");
		    sbSQL.append("\n  And cb.no_empresa=ue.no_empresa");
		    sbSQL.append("\n  And ue.no_usuario=" + dto.getIdUsuario());
		    sbSQL.append("\n   AND cb.tipo_chequera not in(" + globalSingleton.obtenerValorConfiguraSet(202) + ") ");
		    System.out.println("Query"+sbSQL.toString());
		    listLlenaTmp = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public TmpConcenConciliaDto mapRow(ResultSet rs, int idx) throws SQLException {
					TmpConcenConciliaDto cons = new TmpConcenConciliaDto();
						cons.setNoEmpresa(rs.getInt("no_empresa"));
			            cons.setIdBanco(rs.getInt("id_banco"));
			            cons.setDescBanco(rs.getString("desc_banco"));
			            cons.setIdChequera(rs.getString("id_chequera"));
			            cons.setPendTotCargo(rs.getInt("pend_tot_cargo"));
			            cons.setPendImpCargo(rs.getDouble("pend_imp_cargo"));
			            cons.setMovtoTotCargo(rs.getInt("movto_tot_cargo"));
			            cons.setMovtoImpCargo(rs.getDouble("movto_imp_cargo"));
			            cons.setMovtoTotAbono(rs.getInt("movto_tot_abono"));
			            cons.setMovtoImpAbono(rs.getDouble("movto_imp_abono"));
			            cons.setBancoTotCargo(rs.getInt("banco_tot_cargo"));
			            cons.setBancoImpCargo(rs.getDouble("banco_imp_cargo"));
			            cons.setBancoTotAbono(rs.getInt("banco_tot_abono"));
			            cons.setBancoImpAbono(rs.getDouble("banco_imp_abono"));
			            return cons;
					}
				});

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarTmpConcenConcilia");
		}
		return listLlenaTmp;
	}
	
	/**
	 * FunSQLInsert11
	 * @param dto
	 * @return
	 */
	public int insertaTmpConcenConcilia(TmpConcenConciliaDto dto){
		int iInsert = 0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  Insert");
		    sbSQL.append("\n  into");
		    sbSQL.append("\n  tmp_concen_concilia");
		    sbSQL.append("\n  (");
		    sbSQL.append("\n  usuario_alta");
		    sbSQL.append("\n ,no_empresa");
		    sbSQL.append("\n ,id_banco");
		    sbSQL.append("\n ,desc_banco");
		    sbSQL.append("\n ,id_chequera ");
		    sbSQL.append("\n ,pend_tot_cargo");
		    sbSQL.append("\n ,pend_imp_cargo");
		    sbSQL.append("\n ,movto_tot_cargo");
		    sbSQL.append("\n ,movto_imp_cargo");
		    sbSQL.append("\n ,movto_tot_abono");
		    sbSQL.append("\n ,movto_imp_abono");
		    sbSQL.append("\n ,banco_tot_cargo");
		    sbSQL.append("\n ,banco_imp_cargo");
		    sbSQL.append("\n ,banco_tot_abono");
		    sbSQL.append("\n ,banco_imp_abono");
		    sbSQL.append("\n  )");
		    sbSQL.append("\n  values");
		    sbSQL.append("\n  (");
		    sbSQL.append( dto.getUsuarioAlta());
		    sbSQL.append("\n ," + dto.getNoEmpresa());
		    sbSQL.append("\n ," + dto.getIdBanco());
		    sbSQL.append("\n ,'" + dto.getDescBanco() + "'");
		    sbSQL.append("\n ,'" + dto.getIdChequera() + "'");
		    sbSQL.append("\n ," + dto.getPendTotCargo());
		    sbSQL.append("\n ," + dto.getPendImpCargo());
		    sbSQL.append("\n ," + dto.getMovtoTotCargo());
		    sbSQL.append("\n ," + dto.getMovtoImpCargo());
		    sbSQL.append("\n ," + dto.getMovtoTotAbono());
		    sbSQL.append("\n ," + dto.getMovtoImpAbono());
		    sbSQL.append("\n ," + dto.getBancoTotCargo());
		    sbSQL.append("\n ," + dto.getBancoImpCargo());
		    sbSQL.append("\n ," + dto.getBancoTotAbono());
		    sbSQL.append("\n ," + dto.getBancoImpAbono());
		    sbSQL.append("\n  )");
		    System.out.println("Query"+sbSQL.toString());
		    iInsert = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:insertaTmpConcenConcilia");
		}
		return iInsert;
	}
	
	/**
	 * FunSQLResConPend
	 * metodo que hace la consulta repectiva para llenar los reportes Pendientes y Detectados 
	 * @param iIdUsuario
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarReportePendientes(int iIdUsuario){
		List<Map<String, Object>> listReporte = new ArrayList<Map<String, Object>>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  select");
		    sbSQL.append("\n  tc.usuario_alta");
		    sbSQL.append("\n  , tc.no_empresa");
		    sbSQL.append("\n  , tc.id_banco");
		    sbSQL.append("\n  , tc.desc_banco");
		    sbSQL.append("\n  , tc.id_chequera");
		    sbSQL.append("\n  , tc.pend_tot_cargo as pend_tot_cargo ");
		    sbSQL.append("\n  , tc.pend_imp_cargo as pend_imp_cargo ");
		    sbSQL.append("\n  , tc.movto_tot_cargo as movto_tot_cargo ");
		    sbSQL.append("\n  , tc.movto_imp_cargo as movto_imp_cargo ");
		    sbSQL.append("\n  , tc.movto_tot_abono as movto_tot_abono ");
		    sbSQL.append("\n  , tc.movto_imp_abono as movto_imp_abono ");
		    sbSQL.append("\n  , tc.banco_tot_cargo as banco_tot_cargo ");
		    sbSQL.append("\n  , tc.banco_imp_cargo as banco_imp_cargo ");
		    sbSQL.append("\n  , tc.banco_tot_abono as banco_tot_abono ");
		    sbSQL.append("\n  , tc.banco_imp_abono as banco_imp_abono ");
		    sbSQL.append("\n  ,nom_empresa");
		    sbSQL.append("\n  From");
		    sbSQL.append("\n  tmp_concen_concilia  tc, empresa e");
		    sbSQL.append("\n  Where");
		    sbSQL.append("\n  tc.usuario_alta = " + iIdUsuario);
		    sbSQL.append("\n  and tc.no_empresa = e.no_empresa");
		    sbSQL.append("\n  Order By tc.no_empresa, tc.id_banco");
		    
		    System.out.println("consultarReportePendientes "+sbSQL.toString());
		    listReporte = (List<Map<String, Object>>)jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("usuario_alta", rs.getString("usuario_alta"));
			            results.put("no_empresa", rs.getString("no_empresa"));
			            results.put("id_banco", rs.getString("id_banco"));
			            results.put("desc_banco", rs.getString("desc_banco"));
			            results.put("id_chequera", rs.getString("id_chequera"));
			            results.put("pend_tot_cargo", rs.getInt("pend_tot_cargo"));
			            results.put("pend_imp_cargo", rs.getDouble("pend_imp_cargo"));
			            results.put("movto_tot_cargo", rs.getInt("movto_tot_cargo"));
			            results.put("movto_imp_cargo", rs.getDouble("movto_imp_cargo"));
			            results.put("movto_tot_abono", rs.getInt("movto_tot_abono"));
			            results.put("movto_imp_abono", rs.getDouble("movto_imp_abono"));
			            results.put("banco_tot_cargo", rs.getInt("banco_tot_cargo"));
			            results.put("banco_imp_cargo", rs.getDouble("banco_imp_cargo"));
			            results.put("banco_tot_abono", rs.getInt("banco_tot_abono"));
			            results.put("banco_imp_abono", rs.getDouble("banco_imp_abono"));
			            results.put("nom_empresa", rs.getString("nom_empresa"));
			            return results;
					}
				});
		    		
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarReportePendientes");
		}
		return listReporte;
	}
	
	/**
	 * FunSQLDeleteResumenPend
	 * @param iIdUsuario
	 * @return
	 */
	public int borraTmpResumen(int iIdUsuario){
		StringBuffer sbSQL = new StringBuffer();
		int iDelete = 0;
		try{
			sbSQL.append("\n  delete");
	        sbSQL.append("\n  from ");
	        sbSQL.append("\n  tmp_bco_set_resumen");
	        sbSQL.append("\n  where ");
	        sbSQL.append("\n  usuario_alta = " + iIdUsuario);
	        
	        System.out.println("Query"+sbSQL.toString());
	        iDelete = jdbcTemplate.update(sbSQL.toString());
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:borraTmpResumen");
		}
		return iDelete;
	}
	
	/**
	 * FunSQLSelectResumenPend
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<TmpBcoSetResumenDto> consultarTmpBancoSetResumen(ParamBusquedaDto dto){
		List<TmpBcoSetResumenDto> listLlenaTmpResumen = new ArrayList<TmpBcoSetResumenDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  SELECT c.no_empresa,c.id_banco, c.id_chequera,");
		    
		    sbSQL.append("\n  ( Select h.saldo_banco_ini");
		    sbSQL.append("\n    From  hist_cat_cta_banco h");
		    sbSQL.append("\n    Where");
		    sbSQL.append("\n    h.fec_valor = convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "',103)");
		    sbSQL.append("\n    and h.no_empresa = c.no_empresa");
		    sbSQL.append("\n    and h.id_banco = c.id_banco");
		    sbSQL.append("\n    and h.id_chequera = c.id_chequera )as  sdo_edo_cuenta,");
		    
		    sbSQL.append("\n  ( Select Coalesce(h.saldo_final, 0) ");
		    sbSQL.append("\n    From  hist_cat_cta_banco h");
		    sbSQL.append("\n    Where");
		    sbSQL.append("\n    h.fec_valor = convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + " ',103)");
		    sbSQL.append("\n    and h.no_empresa = c.no_empresa");
		    sbSQL.append("\n    and h.id_banco = c.id_banco");
		    sbSQL.append("\n    and h.id_chequera = c.id_chequera )as  sdo_final,");
		    
		    sbSQL.append("\n  ( Select Sum(h.importe) ");
		    sbSQL.append("\n    From  hist_movimiento h");
		    sbSQL.append("\n    where    h.id_estatus_cb in ( 'P' , 'D' )");
		    sbSQL.append("\n    and h.id_forma_pago in (1,3,4,5,6,7,8)");
		    sbSQL.append("\n    and h.id_estatus_mov <> 'H'");
		    sbSQL.append("\n    and ((h.id_tipo_movto = 'E'");
		    sbSQL.append("\n    and ((h.id_tipo_operacion = 3200");
		    sbSQL.append("\n    and ((h.id_estatus_mov in ('I','R') and h.b_entregado in ('N','S'))");
		    sbSQL.append("\n    or  h.id_estatus_mov in ('T','P','J','K')))    or (h.id_tipo_operacion between 3700 and 3799 and h.id_estatus_mov = 'A')");
		    sbSQL.append("\n    or (h.id_tipo_operacion in (1016,1017,1018,1012,1015,1021,1023) and h.id_estatus_mov = 'A')");
		    sbSQL.append("\n    or (h.id_tipo_operacion in (4001,4104)  and h.id_estatus_mov = 'A'))))");
		    sbSQL.append("\n    and h.fec_valor_original >= convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "',103)");
		    sbSQL.append("\n    and h.fec_valor_original <= convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "',103)");
		    sbSQL.append("\n    and h.no_empresa = c.no_empresa");
		    sbSQL.append("\n    and h.id_banco = c.id_banco");
		    sbSQL.append("\n    and h.id_chequera = c.id_chequera  )as creditos_set ,");

		    sbSQL.append("\n  ( Select Sum(h.importe) ");
		    sbSQL.append("\n    From  hist_movimiento h");
		    sbSQL.append("\n    where    h.id_estatus_cb in ( 'P' , 'D' )");
		    sbSQL.append("\n    and h.id_forma_pago in (1,3,4,5,6,7,8)");
		    sbSQL.append("\n    and h.id_estatus_mov <> 'H'");
		    sbSQL.append("\n    and ((h.id_tipo_movto = 'I'  and ((h.id_tipo_operacion between 3100 and 3199 and h.id_estatus_mov = 'A')    or (h.id_tipo_operacion between 3700 and 3799 and h.id_estatus_mov = 'A')    or (h.id_tipo_operacion in (3500,4102,4103) and  h.id_estatus_mov in ('A','W')))))");
		    sbSQL.append("\n    and h.fec_valor_original >= convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "',103)");
		    sbSQL.append("\n    and h.fec_valor_original <= convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "',103)");
		    sbSQL.append("\n    and h.no_empresa = c.no_empresa");
		    sbSQL.append("\n    and h.id_banco = c.id_banco");
		    sbSQL.append("\n    and h.id_chequera = c.id_chequera) as  cargos_set ,");

		    sbSQL.append("\n  ( Select Sum(m.importe) ");
		    sbSQL.append("\n    From  movimiento m");
		    sbSQL.append("\n    where m.id_tipo_movto = 'E'");
		    sbSQL.append("\n    and m.id_tipo_operacion = 3200");
		    sbSQL.append("\n    and m.id_estatus_cb = 'P'");
		    sbSQL.append("\n    and ((m.id_estatus_mov in ('I','R') and b_entregado = 'N') or (id_estatus_mov in ('T','P','J')))");
		    sbSQL.append("\n    and m.fec_valor_original >= convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "',103)");
		    sbSQL.append("\n    and m.fec_valor_original <= convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "',103)");
		    sbSQL.append("\n    and m.no_empresa = c.no_empresa");
		    sbSQL.append("\n    and m.id_banco = c.id_banco");
		    sbSQL.append("\n    and m.id_chequera = c.id_chequera ) as creditos_set_pend,");

		    sbSQL.append("\n  ( Select Sum(b.importe)");
		    sbSQL.append("\n    From concilia_banco b");
		    sbSQL.append("\n    Where b.id_estatus_cb in ( 'P' , 'D' )");
		    sbSQL.append("\n    and b.cargo_abono = 'E'");
		    sbSQL.append("\n    and b.fec_operacion >= '" + funciones.ponerFechaSola(dto.getFechaIni()) + "'");
		    sbSQL.append("\n    and b.fec_operacion <= '" + funciones.ponerFechaSola(dto.getFechaFin()) + "'");
		    sbSQL.append("\n    and b.no_empresa = c.no_empresa");
		    sbSQL.append("\n    and b.id_banco = c.id_banco");
		    sbSQL.append("\n    and b.id_chequera = c.id_chequera ) as cargos_banco,");

		    sbSQL.append("\n  ( Select Sum(b.importe) ");
		    sbSQL.append("\n    From concilia_banco b");
		    sbSQL.append("\n    Where b.id_estatus_cb in ( 'P' , 'D' )");
		    sbSQL.append("\n    and  b.cargo_abono = 'I'");
		    sbSQL.append("\n    and b.fec_operacion >= '" + funciones.ponerFechaSola(dto.getFechaIni()) + "'");
		    sbSQL.append("\n    and b.fec_operacion <= '" + funciones.ponerFechaSola(dto.getFechaFin()) + "'");
		    sbSQL.append("\n    and b.no_empresa = c.no_empresa");
		    sbSQL.append("\n    and b.id_banco = c.id_banco");
		    sbSQL.append("\n    and b.id_chequera = c.id_chequera) as creditos_banco");

		    sbSQL.append("\n  FROM cat_cta_banco c,usuario_empresa ue");
		    sbSQL.append("\n  Where ");
		    sbSQL.append("\n  c.id_banco between  " + dto.getIdBanco() + " and " + dto.getIdBanco2());
		    if(dto.getIdEmpresa() != 0)
		        sbSQL.append("\n  and c.no_empresa =" + dto.getIdEmpresa());
		    
		        sbSQL.append("\n  And c.no_empresa=ue.no_empresa");
		        sbSQL.append("\n  And ue.no_usuario=" + dto.getIdUsuario());
		        sbSQL.append("\n  and c.id_chequera like  '" + dto.getIdChequera() + "'");
		 
		        
		        System.out.println("Query"+sbSQL.toString());
		        
		        listLlenaTmpResumen = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
					public TmpBcoSetResumenDto mapRow(ResultSet rs, int idx) throws SQLException {
						TmpBcoSetResumenDto cons = new TmpBcoSetResumenDto();
							cons.setNoEmpresa(rs.getInt("no_empresa"));
				            cons.setIdBanco(rs.getInt("id_banco"));
				            cons.setIdChequera(rs.getString("id_chequera"));
				            cons.setSdoEdoCuenta(rs.getDouble("sdo_edo_cuenta"));
				            cons.setSdoFinal(rs.getDouble("sdo_final"));
				            cons.setCreditosSet(rs.getDouble("creditos_set"));
				            cons.setCargosSet(rs.getDouble("cargos_set"));
				            cons.setCreditosSetPend(rs.getDouble("creditos_set_pend"));
				            cons.setCargosBanco(rs.getDouble("cargos_banco"));
				            cons.setCreditosBanco(rs.getDouble("creditos_banco"));
				            return cons;
						}
					});
		        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarTmpBancoSetResumen");
		}
		return listLlenaTmpResumen;
	}
	
	/**
	 * FunSQLInsertResumenPend
	 * @param dto
	 * @return
	 */
	public int insertaTmpBcoSetResumen(TmpBcoSetResumenDto dto){
		int iInsert = 0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  Insert");
		    sbSQL.append("\n  into");
		    sbSQL.append("\n  tmp_bco_set_resumen");
		    sbSQL.append("\n  (");
		    sbSQL.append("\n  usuario_alta");
		    sbSQL.append("\n ,no_empresa");
		    sbSQL.append("\n ,id_banco");
		    sbSQL.append("\n ,id_chequera ");
		    sbSQL.append("\n ,sdo_edo_cuenta");
		    sbSQL.append("\n ,cargos_banco");
		    sbSQL.append("\n ,creditos_banco");
		    sbSQL.append("\n ,cargos_set");
		    sbSQL.append("\n ,creditos_set");
		    sbSQL.append("\n ,sdo_disponible");
		    sbSQL.append("\n ,sdo_set");
		    sbSQL.append("\n ,diferencia");
		    sbSQL.append("\n  )");
		    sbSQL.append("\n  values");
		    sbSQL.append("\n  (");
		    sbSQL.append( dto.getUsuarioAlta());
		    sbSQL.append("\n ," + dto.getNoEmpresa());
		    sbSQL.append("\n ," + dto.getIdBanco());
	        sbSQL.append("\n ,'" + dto.getIdChequera() + "'");
		    sbSQL.append("\n ," + dto.getSdoEdoCuenta());
		    sbSQL.append("\n ," + dto.getCargosBanco());
		    sbSQL.append("\n ," + dto.getCreditosBanco());
		    sbSQL.append("\n ," + dto.getCargosSet());
		    sbSQL.append("\n ," + dto.getCreditosSet());
		    sbSQL.append("\n ," + dto.getSdoDisponible());
		    sbSQL.append("\n ," + dto.getSdoSet());
		    sbSQL.append("\n ," + dto.getDiferencia());
		    sbSQL.append("\n  )");
		    System.out.println("Query RRubio"+sbSQL.toString());
		    iInsert = jdbcTemplate.update(sbSQL.toString());

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:insertaTmpBcoSetResumen");
		}
		return iInsert;
	}
	
	/**
	 * FunSQLResumenPend
	 * @param iIdUsuario
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarReporteResumenPendientes(int iIdUsuario){
		List<Map<String, Object>> listReporte = new ArrayList<Map<String, Object>>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  Select");
		    sbSQL.append("\n  tc.usuario_alta");
		    sbSQL.append("\n  , tc.no_empresa");
		    sbSQL.append("\n  , tc.id_banco");
		    sbSQL.append("\n  , tc.id_chequera");
		    sbSQL.append("\n  , tc.sdo_edo_cuenta as sdo_edo_cuenta");
		    sbSQL.append("\n  , tc.cargos_banco as cargos_banco  ");
		    sbSQL.append("\n  , tc.creditos_banco as creditos_banco  ");
		    sbSQL.append("\n  , tc.cargos_set as cargos_set  ");
		    sbSQL.append("\n  , tc.creditos_set as creditos_set  ");
		    sbSQL.append("\n  , tc.sdo_disponible as sdo_disponible  ");
		    sbSQL.append("\n  , tc.sdo_set as sdo_set  ");
		    sbSQL.append("\n  , tc.diferencia as diferencia  ");
		    sbSQL.append("\n  , nom_empresa");
		    sbSQL.append("\n  , desc_banco");
		    
		    sbSQL.append("\n  From");
		    sbSQL.append("\n  tmp_bco_set_resumen tc, empresa e, cat_banco cb ");
		    sbSQL.append("\n  Where");
		    sbSQL.append("\n  tc.usuario_alta = " + iIdUsuario);
		    sbSQL.append("\n  and tc.no_empresa = e.no_empresa");
		    sbSQL.append("\n  and tc.id_banco = cb.id_banco");
		    sbSQL.append("\n  Order By tc.no_empresa, tc.id_banco");
		    
		    System.out.println("consultarReporteResumenPendientes "+sbSQL.toString());
		    
		    listReporte = (List<Map<String, Object>>)jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("usuario_alta", rs.getString("usuario_alta"));
			            results.put("no_empresa", rs.getString("no_empresa"));
			            results.put("id_banco", rs.getString("id_banco"));
			            results.put("id_chequera", rs.getString("id_chequera"));
			            results.put("sdo_edo_cuenta", rs.getDouble("sdo_edo_cuenta"));
			            results.put("cargos_banco", rs.getDouble("cargos_banco"));
			            results.put("creditos_banco", rs.getDouble("creditos_banco"));
			            results.put("cargos_set", rs.getDouble("cargos_set"));
			            results.put("creditos_set", rs.getDouble("creditos_set"));
			            results.put("sdo_disponible", rs.getDouble("sdo_disponible"));
			            results.put("sdo_set", rs.getDouble("sdo_set"));
			            results.put("diferencia", rs.getDouble("diferencia"));
			            results.put("nom_empresa", rs.getString("nom_empresa"));
			            results.put("desc_banco", rs.getString("desc_banco"));
			            return results;
					}
				});
		    		
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarReporteResumenPendientes");
		}
		return listReporte;
	}
	
	/**
	 * FunSQLDeleteMonitor
	 * @param iIdUsuario
	 * @return
	 */
	public int borraTmpMonitor(int iIdUsuario){
		StringBuffer sbSQL = new StringBuffer();
		int iDelete = 0;
		try{
			sbSQL.append("\n  delete");
	        sbSQL.append("\n  from ");
	        sbSQL.append("\n  tmp_bco_set_monitor");
	        sbSQL.append("\n  where ");
	        sbSQL.append("\n  usuario_alta = " + iIdUsuario);
	        
	        System.out.println("Query RRubio"+sbSQL.toString());
	        iDelete = jdbcTemplate.update(sbSQL.toString());
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:borraTmpMonitor");
		}
		return iDelete;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, String> consultarHeadersMonitor(CriteriosBusquedaDto dto){
		Map<String, String> headers = new HashMap<String, String>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append(" SELECT  \n");
			sbSQL.append(" coalesce(convert(char(10),FEC_CONCILIACION, 103),'') as FEC_CONCILIACION, \n");
			sbSQL.append(" coalesce(PERIODO_CONCILIACION,0), \n");
			sbSQL.append(" coalesce(convert(char(10),FEC_CONCILIACION + PERIODO_CONCILIACION, 103),'') as PROXIMA_CONCILIACION \n");
			sbSQL.append(" FROM \n");
			sbSQL.append(" CAT_CTA_BANCO \n");
			sbSQL.append(" WHERE NO_EMPRESA =  ? \n");
			sbSQL.append(" and ID_BANCO =  ? \n");
			sbSQL.append(" and ID_CHEQUERA = ? \n");
			System.out.println("Query"+sbSQL.toString());
			Object[] parametros = new Object[3];
			
			parametros[0] = dto.getNoEmpresa(); 
			parametros[1] = dto.getIdBanco(); 
			parametros[2] = dto.getIdChequera(); 
			
			System.out.println("Query "+ sbSQL.toString());
		    
			headers = (Map<String, String>)jdbcTemplate.queryForObject(sbSQL.toString(), parametros, new RowMapper(){
				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, String> results = new HashMap<String, String>();
			            results.put("UltimaConciliacion", rs.getString("FEC_CONCILIACION"));
			            results.put("PeriodoConciliacion", rs.getString("PERIODO_CONCILIACION"));
			            results.put("ProximaConciliacion", rs.getString("PROXIMA_CONCILIACION"));
			            
			            System.out.println(results);
			            return results;
					}
				});
		    		
		}catch(Exception e){
			headers.put("Error", e.getMessage());
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarReporteResumenPendientes");
		}
		return headers;
	}

	
	/**
	 * FunSQLSelectMonitor
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<TmpBancoSetMonitorDto> consultarTmpBcoSetMonitor(ParamBusquedaDto dto){
		List<TmpBancoSetMonitorDto> listTmp = new ArrayList<TmpBancoSetMonitorDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  SELECT hc.no_empresa,hc.id_banco, hc.id_chequera,");
	        
	        sbSQL.append("\n (Select  saldo_inicial as total From  hist_cat_cta_banco  Where  id_banco = hc.id_banco And id_chequera = hc.id_chequera And fec_valor = convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103)) as sdo_inicial,");
	        sbSQL.append("\n (Select  saldo_banco_ini as total From  hist_cat_cta_banco  Where  id_banco = hc.id_banco And id_chequera = hc.id_chequera And fec_valor = convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as sdo_banco_ini,");
	        
	        sbSQL.append("\n (Select  count(*) as total From  hist_movimiento m  Where  no_empresa = hc.no_empresa And id_forma_pago in (1,3,4,5,6,7)  And m.id_banco = hc.id_banco And m.id_chequera = hc.id_chequera And ((id_tipo_movto = 'E' And ((id_tipo_operacion = 3200  and id_estatus_mov in ('I','R','K','T','P','J')) Or (id_tipo_operacion between 3700  and 3799 And id_estatus_mov = 'A') Or (id_tipo_operacion in (1016,1017,1018,1021)  And id_estatus_mov = 'A')  Or (id_tipo_operacion in (4001,4104)  And id_estatus_mov in ('A')))))  And m.fec_valor_original between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103) ) as total_hist_mov_cargo, ");
	        sbSQL.append("\n (Select  count(*) as total from movimiento m  where m.id_tipo_movto = 'E' and m.id_tipo_operacion = 3200 and ((m.b_entregado = 'N' and id_estatus_mov in ('I','R')) or (id_estatus_mov in('T','P','J'))) And m.id_chequera= hc.id_chequera  And m.no_empresa = hc.no_empresa AND m.id_banco = hc.id_banco And m.fec_valor_original between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as total_pend_mov_cargo, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  hist_movimiento m  Where  no_empresa = hc.no_empresa And id_forma_pago in (1,3,4,5,6,7)  And m.id_banco = hc.id_banco And m.id_chequera = hc.id_chequera And ((id_tipo_movto = 'E' And ((id_tipo_operacion = 3200  and id_estatus_mov in ('I','R','K','T','P','J')) Or (id_tipo_operacion between 3700  and 3799 And id_estatus_mov = 'A') Or (id_tipo_operacion in (1016,1017,1018,1021)  And id_estatus_mov = 'A')  Or (id_tipo_operacion in (4001,4104)  And id_estatus_mov in ('A')))))  And m.fec_valor_original between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) and  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as imp_hist_mov_cargo, ");
	        sbSQL.append("\n (Select  sum(importe) as total from movimiento m  where m.id_tipo_movto = 'E' and m.id_tipo_operacion = 3200 and ((m.b_entregado = 'N' and id_estatus_mov in ('I','R')) or (id_estatus_mov in('T','P','J'))) And m.id_chequera= hc.id_chequera And m.no_empresa = hc.no_empresa AND m.id_banco = hc.id_banco And m.fec_valor_original between  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as imp_pend_mov_cargo, ");
	        sbSQL.append("\n (Select  count(*) as total From  hist_movimiento m  Where  no_empresa = hc.no_empresa And id_forma_pago in (1,3,4,5,6,7)  And m.id_banco = hc.id_banco And m.id_chequera = hc.id_chequera And ((id_tipo_movto = 'I' And ((id_tipo_operacion between 3100 and 3199  And id_estatus_mov = 'A') Or (id_tipo_operacion between 3700 and 3799  And id_estatus_mov = 'A') Or (id_tipo_operacion in (4102,4103)  And id_estatus_mov in ('A'))))) And m.fec_valor_original >= convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And m.fec_valor_original <= convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as total_hist_mov_abono, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  hist_movimiento m  Where  no_empresa = hc.no_empresa And id_forma_pago in (1,3,4,5,6,7)  And m.id_banco = hc.id_banco And m.id_chequera = hc.id_chequera And ((id_tipo_movto = 'I' And ((id_tipo_operacion between 3100 and 3199  And id_estatus_mov = 'A') Or (id_tipo_operacion between 3700 and 3799  And id_estatus_mov = 'A') Or (id_tipo_operacion in (4102,4103)  And id_estatus_mov in ('A'))))) And m.fec_valor_original between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as importe_hist_mov_abono, ");
	        sbSQL.append("\n (Select  count(*) as total From  movto_banca_e  Where  no_empresa = hc.no_empresa And id_banco = hc.id_banco And id_chequera = hc.id_chequera  And cargo_abono  = 'E' And movto_arch =  1  And fec_valor between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103) )as total_be_cargo1, ");
	        
	        sbSQL.append("\n (Select  count(*) as total From  hist_movto_banca_e  Where  no_empresa = hc.no_empresa And id_banco = hc.id_banco And id_chequera = hc.id_chequera  And cargo_abono  = 'E' And movto_arch =  1 And fec_valor between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103) ) as total_be_cargo2, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  movto_banca_e  Where  no_empresa = hc.no_empresa And id_banco = hc.id_banco And id_chequera = hc.id_chequera And cargo_abono  = 'E' And movto_arch =  1  And fec_valor between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103) ) as importe_be_cargo1, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  hist_movto_banca_e  Where  no_empresa = hc.no_empresa And id_banco = hc.id_banco And id_chequera = hc.id_chequera And cargo_abono  = 'E' And movto_arch =  1 And fec_valor between  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as importe_be_cargo2, ");
	        sbSQL.append("\n (Select  count(*) as total From  movto_banca_e  Where  no_empresa = hc.no_empresa And id_banco = hc.id_banco And id_chequera = hc.id_chequera And cargo_abono  = 'I' And movto_arch =  1  And fec_valor between  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103) ) as total_be_abono1, ");
	        sbSQL.append("\n (Select  count(*) as total From  hist_movto_banca_e  Where  no_empresa = hc.no_empresa And id_banco = hc.id_banco And id_chequera = hc.id_chequera And cargo_abono  = 'I' And movto_arch =  1 And fec_valor between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103) ) as total_be_abono2, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  movto_banca_e  Where  no_empresa = hc.no_empresa And id_banco = hc.id_banco And id_chequera = hc.id_chequera And cargo_abono  = 'I' And movto_arch =  1  And fec_valor between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103) ) as importe_be_abono1, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  hist_movto_banca_e  Where  no_empresa = hc.no_empresa And id_banco = hc.id_banco And id_chequera = hc.id_chequera And cargo_abono  = 'I' And movto_arch =  1  And fec_valor between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as importe_be_abono2, ");
	        
	        
	        sbSQL.append("\n (Select  count(*) as total From  hist_movimiento h  Where  h.id_estatus_cb in ( 'C') And h.no_empresa = hc.no_empresa And h.id_tipo_movto = 'E' And h.id_banco = hc.id_banco And h.id_chequera = hc.id_chequera And h.fec_valor_original between  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as mov_total_conciliados_cargo, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  hist_movimiento h  Where  h.id_estatus_cb in ( 'C') And h.no_empresa = hc.no_empresa And h.id_tipo_movto = 'E' And h.id_banco = hc.id_banco And h.id_chequera = hc.id_chequera And h.fec_valor_original between  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as mov_importe_conciliados_cargo, ");
	        sbSQL.append("\n (Select  count(*) as total From  hist_movimiento h  Where  h.id_estatus_cb in ( 'C') And h.no_empresa = hc.no_empresa And h.id_tipo_movto = 'I' And h.id_banco = hc.id_banco And h.id_chequera = hc.id_chequera And h.fec_valor_original between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103))as mov_total_conciliados_abono, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  hist_movimiento h  Where  h.id_estatus_cb in ( 'C') And h.no_empresa = hc.no_empresa And h.id_tipo_movto = 'I' And h.id_banco = hc.id_banco And h.id_chequera = hc.id_chequera And h.fec_valor_original between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as mov_importe_conciliados_abono, ");
	        sbSQL.append("\n (Select  count(*) as total From  concilia_banco cb Where   cb.id_estatus_cb  = 'C' And cb.cargo_abono = 'E' And cb.no_empresa = hc.no_empresa And cb.id_banco = hc.id_banco And cb.id_chequera = hc.id_chequera And cb.fec_operacion between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as be_total_conciliados_cargo, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  concilia_banco cb Where   cb.id_estatus_cb  = 'C' And cb.cargo_abono = 'E' And cb.no_empresa = hc.no_empresa And cb.id_banco = hc.id_banco And cb.id_chequera = hc.id_chequera And cb.fec_operacion between  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as be_importe_conciliados_cargo, ");
	        sbSQL.append("\n (Select  count(*) as total From  concilia_banco cb Where   cb.id_estatus_cb  = 'C' And cb.cargo_abono = 'I' And cb.no_empresa = hc.no_empresa And cb.id_banco = hc.id_banco And cb.id_chequera = hc.id_chequera And cb.fec_operacion between  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as be_total_conciliados_abono, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  concilia_banco cb Where   cb.id_estatus_cb  = 'C' And cb.cargo_abono = 'I' And cb.no_empresa = hc.no_empresa And cb.id_banco = hc.id_banco And cb.id_chequera = hc.id_chequera And cb.fec_operacion between  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as be_importe_conciliados_abono, ");
	        
	        sbSQL.append("\n (Select  count(*) as total From  hist_movimiento h  Where  h.id_estatus_cb in ( 'M') And h.no_empresa = hc.no_empresa And h.id_tipo_movto = 'E' And h.id_banco = hc.id_banco And h.id_chequera = hc.id_chequera And h.fec_valor_original between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as mov_total_manual_cargo, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  hist_movimiento h  Where  h.id_estatus_cb in ( 'M') And h.no_empresa = hc.no_empresa And h.id_tipo_movto = 'E' And h.id_banco = hc.id_banco And h.id_chequera = hc.id_chequera And h.fec_valor_original between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103))as mov_importe_manual_cargo, ");
	        sbSQL.append("\n (Select  count(*) as total From  hist_movimiento h  Where  h.id_estatus_cb in ( 'M') And h.no_empresa = hc.no_empresa And h.id_tipo_movto = 'I' And h.id_banco = hc.id_banco And h.id_chequera = hc.id_chequera And h.fec_valor_original between  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103))as mov_total_manual_abono, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  hist_movimiento h  Where  h.id_estatus_cb in ( 'M') And h.no_empresa = hc.no_empresa And h.id_tipo_movto = 'I' And h.id_banco = hc.id_banco And h.id_chequera = hc.id_chequera And h.fec_valor_original between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103))as mov_importe_manual_abono, ");
	        sbSQL.append("\n (Select  count(*) as total From  concilia_banco cb Where   cb.id_estatus_cb  = 'M' And cb.cargo_abono = 'E' And cb.no_empresa = hc.no_empresa And cb.id_banco = hc.id_banco And cb.id_chequera = hc.id_chequera And cb.fec_operacion between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as be_total_manual_cargo, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  concilia_banco cb Where   cb.id_estatus_cb  = 'M' And cb.cargo_abono = 'E' And cb.no_empresa = hc.no_empresa And cb.id_banco = hc.id_banco And cb.id_chequera = hc.id_chequera And cb.fec_operacion between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as be_importe_manual_cargo,");
	        sbSQL.append("\n (Select  count(*) as total From  concilia_banco cb Where   cb.id_estatus_cb  = 'M' And cb.cargo_abono = 'I' And cb.no_empresa = hc.no_empresa And cb.id_banco = hc.id_banco And cb.id_chequera = hc.id_chequera And cb.fec_operacion between  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as be_total_manual_abono, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  concilia_banco cb Where   cb.id_estatus_cb  = 'M' And cb.cargo_abono = 'I' And cb.no_empresa = hc.no_empresa And cb.id_banco = hc.id_banco And cb.id_chequera = hc.id_chequera And cb.fec_operacion between  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as be_importe_manual_abono,");
	        
	        
	        
	        sbSQL.append("\n (Select  count(*) as total From  hist_movimiento m  Where  no_empresa = hc.no_empresa And id_estatus_cb = 'A' And id_forma_pago in (1,3,4,5,6,7)  And m.id_banco = hc.id_banco And m.id_chequera = hc.id_chequera And ((id_tipo_movto = 'E' And ((id_tipo_operacion = 3200  and id_estatus_mov in ('I','R','K')) Or (id_tipo_operacion between 3700  and 3799 And id_estatus_mov = 'A') Or (id_tipo_operacion in (1016,1017)  And id_estatus_mov = 'A')  Or (id_tipo_operacion in (4001,4104)  And id_estatus_mov in ('A','S')))))  And m.fec_valor_original between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "',103)) as mov_total_aclarado_cargo, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  hist_movimiento m  Where  no_empresa = hc.no_empresa And id_estatus_cb = 'A' And id_forma_pago in (1,3,4,5,6,7)  And m.id_banco = hc.id_banco And m.id_chequera = hc.id_chequera And ((id_tipo_movto = 'E' And ((id_tipo_operacion = 3200  and id_estatus_mov in ('I','R','K')) Or (id_tipo_operacion between 3700  and 3799 And id_estatus_mov = 'A') Or (id_tipo_operacion in (1016,1017)  And id_estatus_mov = 'A')  Or (id_tipo_operacion in (4001,4104)  And id_estatus_mov in ('A','S')))))  And m.fec_valor_original between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as mov_importe_aclarado_cargo, ");
	        sbSQL.append("\n (Select  count(*) as total From  hist_movimiento m  Where  no_empresa = hc.no_empresa And id_estatus_cb = 'A' And id_forma_pago in (1,3,4,5,6,7)  And m.id_banco = hc.id_banco And m.id_chequera = hc.id_chequera And ((id_tipo_movto = 'I' And ((id_tipo_operacion between 3100 and 3199  And id_estatus_mov = 'A') Or (id_tipo_operacion between 3700 and 3799  And id_estatus_mov = 'A') Or (id_tipo_operacion in (4102,4103)  And id_estatus_mov in ('A','S'))))) And m.fec_valor_original between convert('" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as mov_total_aclarado_abono, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  hist_movimiento m  Where  no_empresa = hc.no_empresa And id_estatus_cb = 'A' And id_forma_pago in (1,3,4,5,6,7)  And m.id_banco = hc.id_banco And m.id_chequera = hc.id_chequera And ((id_tipo_movto = 'I' And ((id_tipo_operacion between 3100 and 3199  And id_estatus_mov = 'A') Or (id_tipo_operacion between 3700 and 3799  And id_estatus_mov = 'A') Or (id_tipo_operacion in (4102,4103)  And id_estatus_mov in ('A','S'))))) And m.fec_valor_original between convert('" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as mov_importe_aclarado_abono, ");
	        sbSQL.append("\n (Select  count(*) as total From  concilia_banco  Where  no_empresa = hc.no_empresa And id_estatus_cb = 'A' And id_banco = hc.id_banco And id_chequera = hc.id_chequera And cargo_abono  = 'E' And fec_operacion between convert('" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as be_total_aclarado_cargo, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  concilia_banco  Where  no_empresa = hc.no_empresa And id_estatus_cb = 'A' And id_banco = hc.id_banco And id_chequera = hc.id_chequera And cargo_abono  = 'E' And fec_operacion between convert('" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as be_importe_aclarado_cargo, ");
	        sbSQL.append("\n (Select  count(*) as total From  concilia_banco  Where  no_empresa = hc.no_empresa And id_estatus_cb = 'A' And id_banco = hc.id_banco And id_chequera = hc.id_chequera And cargo_abono  = 'I' And fec_operacion between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as be_total_aclarado_abono, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  concilia_banco  Where  no_empresa = hc.no_empresa And id_estatus_cb = 'A' And id_banco = hc.id_banco And id_chequera = hc.id_chequera And cargo_abono  = 'I' And fec_operacion between  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as be_importe_aclarado_abono, ");
	        
	        
	        sbSQL.append("\n (Select  count(*) as total From  hist_movimiento m  Where  no_empresa = hc.no_empresa And id_estatus_cb = 'P' And id_forma_pago in (1,3,4,5,6,7)  And m.id_banco = hc.id_banco And m.id_chequera = hc.id_chequera And ((id_tipo_movto = 'E' And ((id_tipo_operacion = 3200  And ((id_estatus_mov in ('I','R') and b_entregado = 'S') or  id_estatus_mov in ('K')))  Or (id_tipo_operacion between 3700  and 3799 And id_estatus_mov = 'A') Or (id_tipo_operacion in (1016,1017,1018,1021)  And id_estatus_mov = 'A')  Or (id_tipo_operacion in (4001,4104)  And id_estatus_mov in ('A')))))  And m.fec_valor_original between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as mov_total_pendiente_cargo, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  hist_movimiento m  Where  no_empresa = hc.no_empresa And id_estatus_cb = 'P' And id_forma_pago in (1,3,4,5,6,7)  And m.id_banco = hc.id_banco And m.id_chequera = hc.id_chequera And ((id_tipo_movto = 'E' And ((id_tipo_operacion = 3200  And ((id_estatus_mov in ('I','R') and b_entregado = 'S') or  id_estatus_mov in ('K')))  Or (id_tipo_operacion between 3700  and 3799 And id_estatus_mov = 'A') Or (id_tipo_operacion in (1016,1017,1018,1021)  And id_estatus_mov = 'A')  Or (id_tipo_operacion in (4001,4104)  And id_estatus_mov in ('A')))))  And m.fec_valor_original between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as mov_importe_pendiente_cargo, ");
	        sbSQL.append("\n (Select  count(*) as total From  hist_movimiento m  Where  no_empresa = hc.no_empresa And id_estatus_cb = 'P' And id_forma_pago in (1,3,4,5,6,7)  And m.id_banco = hc.id_banco And m.id_chequera = hc.id_chequera And ((id_tipo_movto = 'I' And ((id_tipo_operacion between 3100 and 3199  And id_estatus_mov = 'A') Or (id_tipo_operacion between 3700 and 3799  And id_estatus_mov = 'A') Or (id_tipo_operacion in (4102,4103)  And id_estatus_mov in ('A'))))) And m.fec_valor_original between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as mov_total_pendiente_abono, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  hist_movimiento m  Where  no_empresa = hc.no_empresa And id_estatus_cb = 'P' And id_forma_pago in (1,3,4,5,6,7)  And m.id_banco = hc.id_banco And m.id_chequera = hc.id_chequera And ((id_tipo_movto = 'I' And ((id_tipo_operacion between 3100 and 3199  And id_estatus_mov = 'A') Or (id_tipo_operacion between 3700 and 3799  And id_estatus_mov = 'A') Or (id_tipo_operacion in (4102,4103)  And id_estatus_mov in ('A'))))) And m.fec_valor_original between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as mov_importe_pendiente_abono, ");
	        sbSQL.append("\n (Select  count(*) as total From  concilia_banco  Where  no_empresa = hc.no_empresa And id_estatus_cb = 'P' And id_banco = hc.id_banco And id_chequera = hc.id_chequera And cargo_abono  = 'E' And fec_operacion between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103))as be_total_pendiente_cargo, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  concilia_banco  Where  no_empresa = hc.no_empresa And id_estatus_cb = 'P' And id_banco = hc.id_banco And id_chequera = hc.id_chequera And cargo_abono  = 'E' And fec_operacion between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103))as be_importe_pendiente_cargo,");
	        sbSQL.append("\n (Select  count(*) as total From  concilia_banco  Where  no_empresa = hc.no_empresa And id_estatus_cb = 'P' And id_banco = hc.id_banco And id_chequera = hc.id_chequera And cargo_abono  = 'I' And fec_operacion between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103))as be_total_pendiente_abono, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  concilia_banco  Where  no_empresa = hc.no_empresa And id_estatus_cb = 'P' And id_banco = hc.id_banco And id_chequera = hc.id_chequera And cargo_abono  = 'I' And fec_operacion between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as be_importe_pendiente_abono, ");
	    
	        sbSQL.append("\n (Select  count(*) as total From  hist_movimiento m  Where  no_empresa = hc.no_empresa And id_estatus_cb = 'D' And id_forma_pago in (1,3,4,5,6,7)  And m.id_banco = hc.id_banco And m.id_chequera = hc.id_chequera And ((id_tipo_movto = 'E' And ((id_tipo_operacion = 3200  and id_estatus_mov in ('I','R','K')) Or (id_tipo_operacion between 3700  and 3799 And id_estatus_mov = 'A') Or (id_tipo_operacion in (1016,1017)  And id_estatus_mov = 'A')  Or (id_tipo_operacion in (4001,4104)  And id_estatus_mov in ('A','S')))))  And m.fec_valor_original between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as mov_total_detectados_cargo, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  hist_movimiento m  Where  no_empresa = hc.no_empresa And id_estatus_cb = 'D' And id_forma_pago in (1,3,4,5,6,7)  And m.id_banco = hc.id_banco And m.id_chequera = hc.id_chequera And ((id_tipo_movto = 'E' And ((id_tipo_operacion = 3200  and id_estatus_mov in ('I','R','K')) Or (id_tipo_operacion between 3700  and 3799 And id_estatus_mov = 'A') Or (id_tipo_operacion in (1016,1017)  And id_estatus_mov = 'A')  Or (id_tipo_operacion in (4001,4104)  And id_estatus_mov in ('A','S')))))  And m.fec_valor_original between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103))as mov_importe_detectados_cargo, ");
	        sbSQL.append("\n (Select  count(*) as total From  hist_movimiento m  Where  no_empresa = hc.no_empresa And id_estatus_cb = 'D' And id_forma_pago in (1,3,4,5,6,7)  And m.id_banco = hc.id_banco And m.id_chequera = hc.id_chequera And ((id_tipo_movto = 'I' And ((id_tipo_operacion between 3100 and 3199  And id_estatus_mov = 'A') Or (id_tipo_operacion between 3700 and 3799  And id_estatus_mov = 'A') Or (id_tipo_operacion in (4102,4103)  And id_estatus_mov in ('A','S'))))) And m.fec_valor_original between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103))as mov_total_detectados_abono, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  hist_movimiento m  Where  no_empresa = hc.no_empresa And id_estatus_cb = 'D' And id_forma_pago in (1,3,4,5,6,7)  And m.id_banco = hc.id_banco And m.id_chequera = hc.id_chequera And ((id_tipo_movto = 'I' And ((id_tipo_operacion between 3100 and 3199  And id_estatus_mov = 'A') Or (id_tipo_operacion between 3700 and 3799  And id_estatus_mov = 'A') Or (id_tipo_operacion in (4102,4103)  And id_estatus_mov in ('A','S'))))) And m.fec_valor_original between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as mov_importe_detectados_abono, ");
	        sbSQL.append("\n (Select  count(*) as total From  concilia_banco  Where  no_empresa = hc.no_empresa And id_estatus_cb = 'D' And id_banco = hc.id_banco And id_chequera = hc.id_chequera And cargo_abono  = 'E' And fec_operacion between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as be_total_detectados_cargo, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  concilia_banco  Where  no_empresa = hc.no_empresa And id_estatus_cb = 'D' And id_banco = hc.id_banco And id_chequera = hc.id_chequera And cargo_abono  = 'E' And fec_operacion between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103))as be_importe_detectados_cargo, ");
	        sbSQL.append("\n (Select  count(*) as total From  concilia_banco  Where  no_empresa = hc.no_empresa And id_estatus_cb = 'D' And id_banco = hc.id_banco And id_chequera = hc.id_chequera And cargo_abono  = 'I' And fec_operacion between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as be_total_detectados_abono, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  concilia_banco  Where  no_empresa = hc.no_empresa And id_estatus_cb = 'D' And id_banco = hc.id_banco And id_chequera = hc.id_chequera And cargo_abono  = 'I' And fec_operacion between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103))as be_importe_detectados_abono, ");
	        
	        sbSQL.append("\n (Select  count(*) as total From  movimiento  Where  id_tipo_operacion  = 1019  And id_tipo_movto = 'E' And id_banco = 12 And id_chequera = '00059112573' And fec_valor_original between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)) as mov_total_ajuste_cargo, ");
	        sbSQL.append("\n (Select  sum(importe) as total From  movimiento  Where  id_tipo_operacion  = 1019  And id_tipo_movto = 'E' And id_banco = 12 And id_chequera = '00059112573' And fec_valor_original between convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "', 103) And  convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "', 103))as mov_importe_ajuste_cargo ");
	        
	        sbSQL.append("\n  From ");
	        sbSQL.append("\n  cat_cta_banco hc, usuario_empresa ue ");
	        sbSQL.append("\n  where  ");
	        sbSQL.append("\n  id_banco between " + dto.getIdBanco() + " AND " + dto.getIdBanco2());
	        if (dto.getIdEmpresa() != 0) 
	            sbSQL.append("\n  and hc.no_empresa = " + dto.getIdEmpresa());
	      
	        sbSQL.append("\n  AND hc.no_empresa=ue.no_empresa ");
	        sbSQL.append("\n  and ue.no_usuario= " + dto.getIdUsuario());
	        
	        sbSQL.append("\n  And id_chequera like  '" + dto.getIdChequera() + "'");
	        System.out.println("Query "+sbSQL.toString());
	        listTmp = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public TmpBancoSetMonitorDto mapRow(ResultSet rs, int idx) throws SQLException {
					TmpBancoSetMonitorDto cons = new TmpBancoSetMonitorDto();
						cons.setNoEmpresa(rs.getInt("no_empresa"));
			            cons.setIdBanco(rs.getInt("id_banco"));
			            cons.setIdChequera(rs.getString("id_chequera"));
			            cons.setSdoInicial(rs.getDouble("sdo_inicial"));
			            cons.setSdoBancoIni(rs.getDouble("sdo_banco_ini"));
			            cons.setTotalHistMovCargo(rs.getInt("total_hist_mov_cargo"));
			            cons.setImpHistMovCargo(rs.getDouble("imp_hist_mov_cargo"));
			            cons.setTotalPendMovCargo(rs.getInt("total_pend_mov_cargo"));
			            cons.setImpPendMovCargo(rs.getDouble("imp_pend_mov_cargo"));
			            cons.setTotalHistMovAbono(rs.getInt("total_hist_mov_abono"));
			            cons.setImporteHistMovAbono(rs.getDouble("importe_hist_mov_abono"));
			            cons.setTotalBeCargo1(rs.getInt("total_be_cargo1"));
			            cons.setTotalBeCargo2(rs.getInt("total_be_cargo2"));
			            cons.setImporteBeCargo1(rs.getDouble("importe_be_cargo1"));
			            cons.setImporteBeCargo2(rs.getDouble("importe_be_cargo2"));
			            cons.setTotalBeAbono1(rs.getInt("total_be_abono1"));
			            cons.setTotalBeAbono2(rs.getInt("total_be_abono2"));
			            cons.setImporteBeAbono1(rs.getDouble("importe_be_abono1"));
			            cons.setImporteBeAbono2(rs.getDouble("importe_be_abono2"));
			            cons.setMovTotalConciliadosCargo(rs.getInt("mov_total_conciliados_cargo"));
			            cons.setMovImporteConciliadosCargo(rs.getDouble("mov_importe_conciliados_cargo"));
			            cons.setMovTotalConciliadosAbono(rs.getInt("mov_total_conciliados_abono"));
			            cons.setMovImporteConciliadosAbono(rs.getDouble("mov_importe_conciliados_abono"));
			            cons.setBeTotalConciliadosCargo(rs.getInt("be_total_conciliados_cargo"));
			            cons.setBeImporteConciliadosCargo(rs.getDouble("be_importe_conciliados_cargo"));
			            cons.setBeTotalConciliadosAbono(rs.getInt("be_total_conciliados_abono"));
			            cons.setBeImporteConciliadosAbono(rs.getDouble("be_importe_conciliados_abono"));
			            cons.setMovTotalManualCargo(rs.getInt("mov_total_manual_cargo"));
			            cons.setMovImporteManualCargo(rs.getDouble("mov_importe_manual_cargo"));
			            cons.setMovTotalManualAbono(rs.getInt("mov_total_manual_abono"));
			            cons.setMovImporteManualAbono(rs.getDouble("mov_importe_manual_abono"));
			            cons.setBeTotalManualCargo(rs.getInt("be_total_manual_cargo"));
			            cons.setBeImporteManualCargo(rs.getDouble("be_importe_manual_cargo"));
			            cons.setBeTotalManualAbono(rs.getInt("be_total_manual_abono"));
			            cons.setBeImporteManualAbono(rs.getDouble("be_importe_manual_abono"));
			            cons.setMovTotalAclaradoCargo(rs.getInt("mov_total_aclarado_cargo"));
			            cons.setMovImporteAclaradoCargo(rs.getDouble("mov_importe_aclarado_cargo"));
			            cons.setMovTotalAclaradoAbono(rs.getInt("mov_total_aclarado_abono"));
			            cons.setMovImporteAclaradoAbono(rs.getDouble("mov_importe_aclarado_abono"));
			            cons.setBeTotalAclaradoCargo(rs.getInt("be_total_aclarado_cargo"));
			            cons.setBeImporteAclaradoCargo(rs.getDouble("be_importe_aclarado_cargo"));
			            cons.setBeTotalAclaradoAbono(rs.getInt("be_total_aclarado_abono"));
			            cons.setBeImporteAclaradoAbono(rs.getDouble("be_importe_aclarado_abono"));
			            cons.setMovTotalPendienteCargo(rs.getInt("mov_total_pendiente_cargo"));
			            cons.setMovImportePendienteCargo(rs.getDouble("mov_importe_pendiente_cargo"));
			            cons.setMovTotalPendienteAbono(rs.getInt("mov_total_pendiente_abono"));
			            cons.setMovImportePendienteAbono(rs.getDouble("mov_importe_pendiente_abono"));
			            cons.setBeTotalPendienteCargo(rs.getInt("be_total_pendiente_cargo"));
			            cons.setBeImportePendienteCargo(rs.getDouble("be_importe_pendiente_cargo"));
			            cons.setBeTotalPendienteAbono(rs.getInt("be_total_pendiente_abono"));
			            cons.setBeImportePendienteAbono(rs.getDouble("be_importe_pendiente_abono"));
			            cons.setMovTotalDetectadosCargo(rs.getInt("mov_total_detectados_cargo"));
			            cons.setMovImporteDetectadosCargo(rs.getDouble("mov_importe_detectados_cargo"));
			            cons.setMovTotalDetectadosAbono(rs.getInt("mov_total_detectados_abono"));
			            cons.setMovImporteDetectadosAbono(rs.getDouble("mov_importe_detectados_abono"));
			            cons.setBeTotalDetectadosCargo(rs.getInt("be_total_detectados_cargo"));
			            cons.setBeImporteDetectadosCargo(rs.getDouble("be_importe_detectados_cargo"));
			            cons.setBeTotalDetectadosAbono(rs.getInt("be_total_detectados_abono"));
			            cons.setBeImporteDetectadosAbono(rs.getDouble("be_importe_detectados_abono"));
			            cons.setMovTotalAjusteCargo(rs.getInt("mov_total_ajuste_cargo"));
			            cons.setMovImporteAjusteCargo(rs.getDouble("mov_importe_ajuste_cargo"));
			            return cons;
					}
				});
	        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarTmpBcoSetMonitor");
		}
		return listTmp;
	}
	
	/**
	 * FunSQLInsertMonitor
	 * @param dto
	 * @return
	 */
	public int insertarTmpBcoSetMonitor(TmpBancoSetMonitorDto dto){
		int iInsert = 0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  Insert");
		    sbSQL.append("\n  into");
		    sbSQL.append("\n  tmp_bco_set_monitor");
		    sbSQL.append("\n  (");
		    sbSQL.append("\n  usuario_alta");
		    sbSQL.append("\n ,no_empresa");
		    sbSQL.append("\n ,id_banco");
		    sbSQL.append("\n ,id_chequera ");
		    sbSQL.append("\n ,secuencia ");
		    sbSQL.append("\n ,concepto");
		    sbSQL.append("\n ,can_cargo_set");
		    sbSQL.append("\n ,imp_cargo_set");
		    sbSQL.append("\n ,can_abono_set");
		    sbSQL.append("\n ,imp_abono_set");
		    sbSQL.append("\n ,can_cargo_bco");
		    sbSQL.append("\n ,imp_cargo_bco");
		    sbSQL.append("\n ,can_abono_bco");
		    sbSQL.append("\n ,imp_abono_bco");
		    sbSQL.append("\n ,saldo");
		    sbSQL.append("\n  )");
		    sbSQL.append("\n  values");
		    sbSQL.append("\n  (");
    		sbSQL.append( dto.getUsuarioAlta());
		    sbSQL.append("\n ," + dto.getNoEmpresa());
		    sbSQL.append("\n ," + dto.getIdBanco());
		    sbSQL.append("\n ," + dto.getIdChequera());
		    sbSQL.append("\n ," + dto.getSecuencia());
		    sbSQL.append("\n ,'" + dto.getConcepto() + "'");
		    sbSQL.append("\n ," + dto.getCanCargoSet());
		    sbSQL.append("\n ," + dto.getImpCargoSet());
		    sbSQL.append("\n ," + dto.getCanAbonoSet());
		    sbSQL.append("\n ," + dto.getImpAbonoSet());
		    sbSQL.append("\n ," + dto.getCanCargoBco());
		    sbSQL.append("\n ," + dto.getImpCargoBco());
		    sbSQL.append("\n ," + dto.getCanAbonoBco());
		    sbSQL.append("\n ," + dto.getImpAbonoBco());
		    sbSQL.append("\n ," + dto.getSaldo());
		    sbSQL.append("\n  )");
		    System.out.println("Query RRubio"+sbSQL.toString());
		    iInsert = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:insertarTmpBcoSetMonitor");
		}
		return iInsert;
	}
	
	/**
	 * FunSQLMonitor
	 * @param iIdUsuario
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarReporteMonitor(int iIdUsuario){
		List<Map<String, Object>> listReporte = new ArrayList<Map<String, Object>>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  Select");
		    sbSQL.append("\n  tc.usuario_alta");
		    sbSQL.append("\n  , tc.no_empresa");
		    sbSQL.append("\n  , tc.id_banco");
		    sbSQL.append("\n  , tc.id_chequera");
		    
		    sbSQL.append("\n  ,concepto ");
		    sbSQL.append("\n  , tc.can_cargo_set as can_cargo_set  ");
		    sbSQL.append("\n  , tc.imp_cargo_set as imp_cargo_set  ");
		    sbSQL.append("\n  , tc.can_abono_set as can_abono_set  ");
		    sbSQL.append("\n  , tc.imp_abono_set as imp_abono_set  ");
		    sbSQL.append("\n  , tc.can_cargo_bco as can_cargo_bco  ");
		    sbSQL.append("\n  , tc.imp_cargo_bco as imp_cargo_bco  ");
		    sbSQL.append("\n  , tc.can_abono_bco as can_abono_bco  ");
		    sbSQL.append("\n  , tc.imp_abono_bco as imp_abono_bco  ");
		    sbSQL.append("\n  , tc.saldo as saldo  ");
		    
		    sbSQL.append("\n  , nom_empresa");
		    sbSQL.append("\n  , desc_banco");
		    
		    sbSQL.append("\n  From");
		    sbSQL.append("\n  tmp_bco_set_monitor tc, empresa e, cat_banco cb ");
		    sbSQL.append("\n  Where");
		    sbSQL.append("\n  tc.usuario_alta = " + iIdUsuario);
		    sbSQL.append("\n  and tc.no_empresa = e.no_empresa");
		    sbSQL.append("\n  and tc.id_banco = cb.id_banco order by secuencia");
		    
		    System.out.println("consultarReporteMonitor "+sbSQL.toString());
		    
		    listReporte = (List<Map<String, Object>>)jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("usuario_alta", rs.getString("usuario_alta"));
			            results.put("no_empresa", rs.getString("no_empresa"));
			            results.put("id_banco", rs.getString("id_banco"));
			            results.put("id_chequera", rs.getString("id_chequera"));
			            results.put("concepto", rs.getString("concepto"));
			            results.put("can_cargo_set", rs.getInt("can_cargo_set"));
			            results.put("imp_cargo_set", rs.getDouble("imp_cargo_set"));
			            results.put("can_abono_set", rs.getInt("can_abono_set"));
			            results.put("imp_abono_set", rs.getDouble("imp_abono_set"));
			            results.put("can_cargo_bco", rs.getInt("can_cargo_bco"));
			            results.put("imp_cargo_bco", rs.getDouble("imp_cargo_bco"));
			            results.put("can_abono_bco", rs.getInt("can_abono_bco"));
			            results.put("imp_abono_bco", rs.getDouble("imp_abono_bco"));
			            results.put("saldo", rs.getDouble("saldo"));
			            results.put("nom_empresa", rs.getString("nom_empresa"));
			            results.put("desc_banco", rs.getString("desc_banco"));
			            return results;
					}
				});
		    		
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarReporteMonitor");
		}
		return listReporte;
	}
	
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:setDataSource");
		}
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<MovimientoDto> consultarMovsEmpresaVIngresos(CriteriosBusquedaDto dto){
		
		
		List<MovimientoDto> listEmpresa = new ArrayList<MovimientoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
			{
	            sbSQL.append("\n Select id_estatus_cb, fec_valor, id_tipo_movto, importe,id_tipo_operacion,");
	            sbSQL.append("\n referencia, no_folio_det, no_cheque, no_cuenta,id_divisa,");
	            sbSQL.append("\n no_docto, concepto, no_factura, beneficiario, 'H' AS tabla,id_estatus_mov ");
	            sbSQL.append(",cg.id_grupo as id_grupo,cg.desc_grupo as desc_grupo, cr.id_rubro as id_rubro,desc_rubro as desc_rubro, ");
	            sbSQL.append("m.id_forma_pago, cfp.desc_forma_pago");
	            sbSQL.append("\n From hist_movimiento m ");	
	            sbSQL.append("\n 				inner join cat_grupo cg");
	    	    sbSQL.append("\n 				on m.ID_GRUPO = cg.id_grupo");
	    	    sbSQL.append("\n 				inner join cat_rubro cr "); 
	    	    sbSQL.append("\n 				on m.ID_GRUPO = cr.id_grupo");
	    	    sbSQL.append("\n 				and m.id_rubro = cr.id_rubro");
	    	    sbSQL.append("\n 				inner join cat_forma_pago cfp");
	    	    sbSQL.append("\n 				on m.id_forma_pago = cfp.id_forma_pago");
	            sbSQL.append("\n where no_empresa= " + dto.getNoEmpresa());
	            sbSQL.append("\n and id_estatus_mov not in ('X')");
	            sbSQL.append("\n and id_tipo_operacion in (3101, 3115, 3154, 3107, 3108)");
	            
	            if(dto.getIdBanco() > 0)
	            	sbSQL.append("\n and id_banco = " + dto.getIdBanco());
	            
	            if(dto.getIdChequera() != null && !dto.getIdChequera().equals(""))
	            	sbSQL.append("\n and id_chequera = '" + dto.getIdChequera() + "'");
	            
	            if(dto.getFechaIni() != null && !dto.getFechaIni().equals(""))
	            {
		            if(!dto.getFechaFin().equals(""))
		            {
		            	sbSQL.append("\n  and fec_valor_original >= convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "',103)" );
	        			sbSQL.append("\n  and fec_valor_original <= convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "',103)" );
		            }
		            else
       				sbSQL.append("\n  and fec_valor_original = convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "',103)");
	            }
	            
	            if(dto.getEstatus() != null && !dto.getEstatus().equals(""))
	            	sbSQL.append("\n and id_estatus_cb = '" + dto.getEstatus() + "'");
	            	
	            if(dto.getMontoIni() != 0)
               {
            	   if(dto.getMontoFin() != 0)
            	   {
            		   sbSQL.append("\n  and importe >= " + dto.getMontoIni() );
            		   sbSQL.append("\n  and importe <= " + dto.getMontoFin() );
            	   }
            	   else 
            		   sbSQL.append("\n  and importe = " + dto.getMontoIni() );
               }
	                    
	            if(dto.getCargoAbono() != null && !dto.getCargoAbono().equals(""))
	            	   sbSQL.append("\n  and id_tipo_movto = '" + dto.getCargoAbono() + "'");
	            
	            if(dto.getFormaPago() != 0)
	            	   sbSQL.append("\n  and id_forma_pago = '" + dto.getFormaPago() + "'");
	            
	            if(dto.getIdentificados() == 1)
	            	sbSQL.append("\n  and (m.concepto like '%/%' and m.id_grupo != 19990 and m.id_rubro != 19991) ");
	            else if(dto.getIdentificados() == 2)
	            	sbSQL.append("\n  and m.id_grupo = 19990 and m.id_rubro = 19991 ");
	            
	            sbSQL.append("\n  and m.fec_valor >= convert(datetime,'01/01/2014',103)" );
	            
	            sbSQL.append("\n UNION ALL");
	       
	            sbSQL.append("\n Select id_estatus_cb, fec_valor, id_tipo_movto, importe,id_tipo_operacion,");
	            sbSQL.append("\n referencia, no_folio_det, no_cheque, no_cuenta,id_divisa,");
	            sbSQL.append("\n no_docto, concepto, no_factura, beneficiario ,'M' AS tabla,id_estatus_mov");
	            sbSQL.append(",cg.id_grupo as id_grupo,cg.desc_grupo as desc_grupo, cr.id_rubro as id_rubro,desc_rubro as desc_rubro, ");
	            sbSQL.append("m.id_forma_pago, cfp.desc_forma_pago");
	            sbSQL.append("\n From movimiento m ");	
	            sbSQL.append("\n 				inner join cat_grupo cg");
	    	    sbSQL.append("\n 				on m.ID_GRUPO = cg.id_grupo");
	    	    sbSQL.append("\n 				inner join cat_rubro cr ");
	    	    sbSQL.append("\n 				on m.ID_GRUPO = cr.id_grupo");
	    	    sbSQL.append("\n 				and m.id_rubro = cr.id_rubro");
	    	    sbSQL.append("\n 				inner join cat_forma_pago cfp");
	    	    sbSQL.append("\n 				on m.id_forma_pago = cfp.id_forma_pago");
	            sbSQL.append("\n where no_empresa=" + dto.getNoEmpresa() +  "");
	            sbSQL.append("\n and id_estatus_mov not in ('X')");
	            sbSQL.append("\n and id_tipo_operacion in (3101, 3115, 3154, 3107, 3108)");
	            
	            if(dto.getIdBanco() > 0)
	            	sbSQL.append("\n and id_banco = " + dto.getIdBanco());
	            
	            if(dto.getIdChequera() != null && !dto.getIdChequera().equals(""))
	            	sbSQL.append("\n and id_chequera = '" + dto.getIdChequera() + "'");
	            
	            if(dto.getFechaIni() != null && !dto.getFechaIni().equals(""))
	            {
		            if(!dto.getFechaFin().equals(""))
		            {
		            	sbSQL.append("\n  and fec_valor_original >= convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "',103)" );
	        			sbSQL.append("\n  and fec_valor_original <= convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "',103)" );
		            }
		            else
       				sbSQL.append("\n  and fec_valor_original = convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "',103)");
	            }
	            
	            if(dto.getEstatus() != null && !dto.getEstatus().equals(""))
	            	sbSQL.append("\n and id_estatus_cb = '" + dto.getEstatus() + "'");
	            	
	            if(dto.getMontoIni() != 0)
               {
            	   if(dto.getMontoFin() != 0)
            	   {
            		   sbSQL.append("\n  and importe >= " + dto.getMontoIni() );
            		   sbSQL.append("\n  and importe <= " + dto.getMontoFin() );
            	   }
            	   else 
            		   sbSQL.append("\n  and importe = " + dto.getMontoIni() );
               }
	                    
	            if(dto.getCargoAbono() != null && !dto.getCargoAbono().equals(""))
	            	   sbSQL.append("\n  and id_tipo_movto = '" + dto.getCargoAbono() + "'");
	            
	            if(dto.getFormaPago() != 0)
	            	   sbSQL.append("\n  and id_forma_pago = '" + dto.getFormaPago() + "'");
	            
	            if(dto.getIdentificados() == 1)
	            	sbSQL.append("\n  and (m.concepto like '%/%' and m.id_grupo != 19990 and m.id_rubro != 19991) ");
	            else if(dto.getIdentificados() == 2)
	            	sbSQL.append("\n  and m.id_grupo = 19990 and m.id_rubro = 19991 ");
	            
	            sbSQL.append("\n  and m.fec_valor >= convert(datetime,'01/01/2014',103)" );
	            
	            sbSQL.append("\n order by importe desc");
			}
			System.out.println("Query"+sbSQL.toString());
			
			if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
			{
	            sbSQL.append("\n Select id_estatus_cb, fec_valor, id_tipo_movto, importe,id_tipo_operacion,");
	            sbSQL.append("\n referencia, no_folio_det, no_cheque, no_cuenta,id_divisa,");
	            sbSQL.append("\n no_docto, concepto, no_factura, beneficiario ");
	            sbSQL.append("\n From hist_movimiento");
	            sbSQL.append("\n where id_estatus_cb in ('P','D')");
	            
	            if(dto.getIdBanco() > 0)
	            	sbSQL.append("\n and id_banco = " + dto.getIdBanco());
	            
	            if(dto.getIdChequera() != null && !dto.getIdChequera().equals(""))
	            	sbSQL.append("\n and id_chequera = '" + dto.getIdChequera() + "'");
	            
	            if(dto.getFechaIni() != null && !dto.getFechaIni().equals(""))
	            {
		            if(!dto.getFechaFin().equals(""))
		            {
		            	sbSQL.append("\n  and fec_valor_original >= convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "',103)" );
	        			sbSQL.append("\n  and fec_valor_original <= convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFin()) + "',103)" );
		            }
		            else
       				sbSQL.append("\n  and fec_valor_original = convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaIni()) + "',103)");
	            }
	            
	            if(dto.getEstatus() != null && !dto.getEstatus().equals(""))
	            	sbSQL.append("\n and id_estatus_cb = '" + dto.getEstatus() + "'");
	            	
	            if(dto.getMontoIni() != 0)
               {
            	   if(dto.getMontoFin() != 0)
            	   {
            		   sbSQL.append("\n  and float8(importe) >= " + dto.getMontoIni() );
            		   sbSQL.append("\n  and float8(importe) <= " + dto.getMontoFin() );
            	   }
            	   else 
            		   sbSQL.append("\n  and importe = " + dto.getMontoIni() );
               }
	            
	            if(dto.getCargoAbono() != null && !dto.getCargoAbono().equals(""))
	            	   sbSQL.append("\n  and id_tipo_movto = '" + dto.getCargoAbono() + "'");
	            
	            if(dto.getFormaPago() != 0)
	            	   sbSQL.append("\n  and id_forma_pago = '" + dto.getFormaPago() + "'");
	            
	            if(dto.getIdentificados() == 1)
	            	sbSQL.append("\n  and (m.concepto like '%/%' and m.id_grupo != 19990 and m.id_rubro != 19991) ");
	            else if(dto.getIdentificados() == 2)
	            	sbSQL.append("\n  and m.id_grupo = 19990 and m.id_rubro = 19991 ");
	            
	            sbSQL.append("\n  and m.fec_valor >= convert(datetime,'01/01/2014',103)" );
	            
	            sbSQL.append("\n order by importe desc");
			}
			
//			System.out.println("Query para la consulta de depositos: " + sbSQL.toString());
			
			listEmpresa = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons = new MovimientoDto();
					cons.setIdEstatusCb(rs.getString("id_estatus_cb"));
					cons.setFecValor(rs.getDate("fec_valor"));
					cons.setIdTipoMovto(rs.getString("id_tipo_movto").equals("E") ? "C" : "A");
					cons.setImporte(rs.getDouble("importe"));
					cons.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
					cons.setReferencia(rs.getString("referencia"));
					cons.setNoFolioDet(rs.getInt("no_folio_det"));
					cons.setNoCheque(rs.getInt("no_cheque"));
					cons.setNoCuenta(rs.getInt("no_cuenta"));
					cons.setIdDivisa(rs.getString("id_divisa"));
					cons.setNoDocto(rs.getString("no_docto"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setNoFactura(rs.getString("no_factura"));
					cons.setBeneficiario(rs.getString("beneficiario"));
					cons.setTablaOrigen(rs.getString("tabla"));
					cons.setIdEstatusMov(rs.getString("id_estatus_mov"));
					cons.setIdGrupo(rs.getInt("id_grupo"));
					cons.setIdRubro(rs.getInt("id_rubro"));
					cons.setDescGrupo(rs.getString("desc_grupo"));
					cons.setDescRubro(rs.getString("desc_rubro"));
					cons.setIdFormaPago(rs.getInt("id_forma_pago"));
					cons.setDescFormaPago(rs.getString("desc_forma_pago"));
					
					return cons;
				}});
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarMovsEmpresa");
		}
		return listEmpresa;
	}
	
	
	public int updateMovimientoSETVIngresos(String listBanco, CriteriosBusquedaDto dto){
		
		StringBuffer sbSQL = new StringBuffer();
		
		if(  dto.getDescRubro().trim().equals("") )
		{
			sbSQL.append(" UPDATE MOVIMIENTO SET");
			sbSQL.append("\n ID_GRUPO = " + dto.getIdGrupo());
			sbSQL.append("\n ,ID_RUBRO = " + dto.getIdRubro());
			sbSQL.append("\n WHERE no_folio_det = " + listBanco + "");
			System.out.println("Query"+sbSQL.toString());
		}else{
			sbSQL.append(" UPDATE MOVIMIENTO SET");
			sbSQL.append("\n ID_GRUPO = " + dto.getIdGrupo());
			sbSQL.append("\n ,ID_RUBRO = " + dto.getIdRubro());
			sbSQL.append("\n ,CONCEPTO = '" + dto.getDescRubro() + "'");
//			sbSQL.append("\n ,DESCRIPCION = '" + dto.getDescRubro() + "'");
			sbSQL.append("\n WHERE no_folio_det = " + listBanco + "");	
			System.out.println("Query"+sbSQL.toString());
		}
//		System.out.println("update movimiento "+sbSQL.toString());
		try {
			
			return jdbcTemplate.update(sbSQL.toString());
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:updateMovimientoSETVIngresos");
			return -1;
		}

		
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<CuentaContableDto> getCuentaContable(int noEmpresa, String idGrupo, String idRubro) {

		List<CuentaContableDto> list = new ArrayList<CuentaContableDto>();
		
		StringBuffer sbSQL = new StringBuffer();
		
		try{
			
			sbSQL.append(" Select * From guia_contable where no_empresa = "+ noEmpresa +" And ");
			sbSQL.append(" id_grupo = " + idGrupo + " And id_rubro = " + idRubro );
			System.out.println("Query"+sbSQL.toString());
		      list = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
		    	  
					public CuentaContableDto mapRow(ResultSet rs, int idx) throws SQLException {
						CuentaContableDto cons = new CuentaContableDto();
						cons.setCuentaContable(rs.getString("cuenta_contable"));
						return cons;
						
					}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarEstatus");
		}
		
		return list;

	}
	
	public int existeFlujoMovtos(int noFolioDet) {
		StringBuffer sbSql = new StringBuffer();
		int iAfec = 0;
		
		try {
			sbSql.append(" SELECT count(*) FROM config_contable WHERE no_folio_det = "+ noFolioDet +" ");
			System.out.println("existeflujoMovTOS"+sbSql.toString());
			iAfec = jdbcTemplate.queryForInt(sbSql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P: Egresos, C: ConciliacionBancoSetDaoImpl, M: existeFlujoMovtos");
			return 0;
		}
		return iAfec;
	}
	
	public int insertaDatosConta(CriteriosBusquedaDto listMovtos, int i, int noFolioDet) {
		StringBuffer sbSql = new StringBuffer();
		int iAfec = 0;
		
		try {
			sbSql.append(" INSERT INTO config_contable VALUES("+ noFolioDet +", "+ listMovtos.getIdGrupo() +", ");
			sbSql.append("\n  "+ listMovtos.getIdRubro() +", "+ listMovtos.getIdSubGrupo() +", ");
			sbSql.append("\n  "+ listMovtos.getIdSubSubGrupo() +", "+ listMovtos.getIdRubroC() +", "+ noFolioDet +", '"+ listMovtos.getCuentaContable().replace("-", "") +"',");
			sbSql.append("\n  0,0,0)");
			System.out.println("insertaDatosConta "+sbSql.toString());
			iAfec = jdbcTemplate.update(sbSql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P: Egresos, C: ConciliacionBancoSetDaoImpl, M: insertaDatosConta");
			return 0;
		}
		return iAfec;
	}
	
	public int updateDatosConta(CriteriosBusquedaDto listMovtos, int i, int noFolioDet) {
		StringBuffer sbSql = new StringBuffer();
		int iAfec = 0;
		
		try {
			sbSql.append(" UPDATE config_contable SET id_grupo = "+ listMovtos.getIdGrupo() +", \n");
			sbSql.append(" 		id_rubro = "+ listMovtos.getIdRubro() +", id_sub_grupo = "+ listMovtos.getIdSubGrupo() +", \n");
			sbSql.append(" 		id_sub_sub_grupo = "+ listMovtos.getIdSubSubGrupo() +", id_rubro_c = "+ listMovtos.getIdRubroC() +", \n");
			sbSql.append(" 		cta_cargo = '"+ listMovtos.getCuentaContable().replace("-", "") +"' \n");
			sbSql.append(" WHERE no_folio_det = "+ noFolioDet +" ");
			System.out.println("Query"+sbSql.toString());
			iAfec = jdbcTemplate.update(sbSql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P: Egresos, C: ConciliacionBancoSetDaoImpl, M: updateDatosConta");
			return 0;
		}
		return iAfec;
	}
	
	@SuppressWarnings("unchecked")
	public List<CuentaContableDto> consultarFacturasCXC(String noCliente, int noEmpresa) {
		StringBuffer sbSql = new StringBuffer();
		List<CuentaContableDto> listFact = new ArrayList<CuentaContableDto>();
		
		try {
			sbSql.append(" SELECT c.no_factura, c.importe, c.id_divisa, cd.desc_divisa, c.fec_fact, c.concepto, c.no_benef, p.razon_social \n");
			sbSql.append(" FROM cobranza c, persona p, cat_divisa cd \n");
			sbSql.append(" WHERE c.no_benef = p.equivale_persona \n");
			sbSql.append(" 		And c.id_divisa = cd.id_divisa \n");
			sbSql.append(" 		And c.no_empresa = "+ noEmpresa +" \n");
			sbSql.append(" 		And c.estatus = 'P' \n");
			
			if(!noCliente.equals(""))
				sbSql.append(" 		And c.no_benef = '"+ noCliente +"' \n");
			
//			sbSql.append(" 		And p.id_tipo_persona = 'C' \n");
			
			sbSql.append(" ORDER BY c.no_benef \n");
			System.out.println("factura: "+sbSql.toString());
			
			listFact = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
				public CuentaContableDto mapRow(ResultSet rs, int idx) throws SQLException {
					CuentaContableDto cons = new CuentaContableDto();
					cons.setNoFactura(rs.getString("no_factura"));
					cons.setImporte(rs.getDouble("importe"));
					cons.setImporteA(rs.getDouble("importe"));
					cons.setIdDivisa(rs.getString("id_divisa"));
					cons.setDescDivisa(rs.getString("desc_divisa"));
					cons.setFecFact(rs.getString("fec_fact"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setNoBenef(rs.getString("no_benef"));
					cons.setRazonSocial(rs.getString("razon_social"));

					return cons;
				}
				}
			);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P: Egresos, C: ConciliacionBancoSetDaoImpl, M: updateDatosConta");
		}
		return listFact;
	}
	
	public int existeMov(int noFolioDet) {
		int res = 0;
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT count(*) FROM movimiento \n");
			sql.append(" WHERE no_folio_det = "+ noFolioDet +" \n");
			System.out.println("esisteMov :"+sql.toString());
			res = jdbcTemplate.queryForInt(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P: Egresos, C: ConciliacionBancoSetDaoImpl, M: existeMov");
		}
		return res;
	}
	
	public int pasaHistAMov(int noFolioDet) {
		int res = 0;
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" INSERT INTO movimiento \n");
			sql.append(" SELECT * FROM hist_movimiento \n");
			sql.append(" WHERE no_folio_det = "+ noFolioDet +" \n");
			System.out.println("pasaHistaMov "+sql.toString());
			res = jdbcTemplate.update(sql.toString());
			
			if(res == 1) {
				sql.append(" DELETE FROM hist_movimiento \n");
				sql.append(" WHERE no_folio_det = "+ noFolioDet +" \n");
				System.out.println("delete para a historico "+sql.toString());
				res = jdbcTemplate.update(sql.toString());
			}
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P: Egresos, C: ConciliacionBancoSetDaoImpl, M: pasaHistAMov");
		}
		return res;
	}
	
	public int clasificaIngresos(List<Map<String, String>> datosGrid, int i, boolean ietu, boolean iva) {
		int res = 0;	
		StringBuffer sql = new StringBuffer();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		int proyecto = datosGrid.get(i).get("proyec").toString().equals("") ? 0 : Integer.parseInt(datosGrid.get(i).get("proyec"));
		int depto = datosGrid.get(i).get("depto").toString().equals("") ? 0 : Integer.parseInt(datosGrid.get(i).get("depto"));
		int cCosto = datosGrid.get(i).get("cCosto").toString().equals("") ? 0 : Integer.parseInt(datosGrid.get(i).get("cCosto"));
		String sietu = ietu ? "S" : "N";
		String siva = iva ? "S" : "N";
		
		try {
			sql.append(" UPDATE cobranza SET fec_clasif = '"+ funciones.ponerFecha(consultasGenerales.obtenerFechaHoy()) +"', \n");
			sql.append(" 		folio_ref = "+ datosGrid.get(i).get("noFolioDet") +", id_proyecto = "+ proyecto +", \n");
			sql.append(" 		id_departamento = "+ depto +", id_centro_costo = "+ cCosto +", \n");
			sql.append(" 		cta_contable = '"+ datosGrid.get(i).get("ctaContable").replace("-", "") +"', estatus = 'A', \n");
			sql.append(" 		ietu = '"+ sietu +"', iva = '"+ siva +"', \n");
			sql.append(" 		importe = "+ Double.parseDouble(datosGrid.get(i).get("importeA")) +", \n");
			
			if(Double.parseDouble(datosGrid.get(i).get("importeA")) == Double.parseDouble(datosGrid.get(i).get("importe"))){
				sql.append(" 		id_tipo_operacion = "+ Integer.parseInt(datosGrid.get(i).get("idTipoOperacion")) +" \n");
			}else{
				sql.append(" 		id_tipo_operacion = 1235 \n");
			}
			
			sql.append(" WHERE no_empresa = "+ datosGrid.get(i).get("noEmpresa") +" \n");
			sql.append(" 	And no_factura = '"+ datosGrid.get(i).get("noFactura") +"' \n");
			sql.append(" 	And no_benef = '"+ datosGrid.get(i).get("noCliente") +"' \n");
			sql.append(" 	And importe = "+ datosGrid.get(i).get("importe") +"");
			System.out.println("Update de cobranza "+sql.toString());
			res = jdbcTemplate.update(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P: Egresos, C: ConciliacionBancoSetDaoImpl, M: clasificaIngresos");
		}
		return res;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> llenarComboDeptos(int noEmpresa) {
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		
		try {
			sbSql.append(" SELECT * \n");
			sbSql.append(" FROM cat_departamentos cd, equivale_empresa_SAE_COI e \n");
			sbSql.append(" WHERE cd.no_empresa = e.noEmpresaCOI \n");
			sbSql.append(" 		And e.noEmpresaSAE = "+ noEmpresa +" \n");
			sbSql.append(" ORDER BY cd.desc_departamento \n");
			System.out.println("Query"+sbSql.toString());
			list = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("id_departamento"));
					cons.setDescripcion(rs.getString("desc_departamento"));
					return cons;
				}}
			);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P: Egresos, C: ConciliacionBancoSetDaoImpl, M: llenarComboDeptos");
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> llenarComboEmpresa(int iUsuario){
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT ea.no_empresa, ea.nom_empresa ");
		sb.append("\n FROM empresa ea");
		sb.append("\n WHERE ea.no_empresa in ");
		sb.append("\n	 ( SELECT u.no_empresa ");
		sb.append("\n		 FROM usuario_empresa u ");
		sb.append("\n		 WHERE u.no_usuario = " + iUsuario + " )");
		sb.append("\n ORDER BY ea.nom_empresa ");
		System.out.println("Query"+sb.toString());
		try{
			list = jdbcTemplate.query(sb.toString(), new RowMapper(){
			public ComunDto mapRow(ResultSet rs, int idx) throws SQLException {
				ComunDto empresa = new ComunDto();
				empresa.setIdEmpresa(rs.getInt("no_empresa"));
				empresa.setDescripcion(rs.getString("nom_empresa"));
				return empresa;
			}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:consultarBancos");
		}
		return  list;
	}

	public Integer obtenerNoFolioDet(Integer noFolioParam){
		Integer res = 0;
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" select m.no_folio_det \n");
			sql.append(" from movimiento m, parametro p \n");
			sql.append(" where m.NO_EMPRESA = p.NO_EMPRESA \n");
			sql.append(" and m.ID_CHEQUERA = p.ID_CHEQUERA \n");
//			sql.append(" and m.REFERENCIA = p.REFERENCIA \n");
			sql.append(" and m.id_tipo_operacion = p.id_tipo_operacion \n");
			sql.append(" and m.USUARIO_ALTA = p.USUARIO_ALTA \n");
			sql.append(" and m.NO_DOCTO = p.NO_DOCTO \n");
			sql.append(" and p.NO_FOLIO_PARAM = ? \n");
			System.out.println("Query"+sql.toString());
			System.out.println("noFolioPAram"+ noFolioParam);
			
			res = jdbcTemplate.queryForInt(sql.toString(), noFolioParam);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P: Egresos, C: ConciliacionBancoSetDaoImpl, M: existeMov");
		}
		return res;
	}
	
	public MonitorConciliaGenDTO obtenerMovsSETMonitor(ParamBusquedaDto dto){
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT coalesce(mov.cantidad, 0) + coalesce(hist.cantidad, 0) as cantidad, coalesce(mov.total, 0) +  coalesce(hist.total, 0) as total \n");
			sql.append(" FROM  \n");
			sql.append(" (SELECT count(*) as cantidad, coalesce(sum(importe), 0) as total \n");
			sql.append(" From  \n");
			sql.append(" hist_movimiento m  \n");
			sql.append(" Where  \n");
			sql.append(" no_empresa = ? \n");
			sql.append(" And m.id_banco = ?  \n");
			sql.append(" And m.id_chequera = ? \n");
			sql.append(" And id_tipo_movto = ? \n");
			sql.append(" and id_estatus_mov in ('I','A','K') \n");
			sql.append(" And id_tipo_operacion in(3200,3700,3701,3705,3706,3708,3709,1018,3101,4001) \n");
			
			if(dto.getFechaIni()  != null && dto.getFechaFin() != null){
//				sql.append(" /*Opcionales*/ \n");
				sql.append(" And m.fec_valor >= convert(datetime,?, 103) \n");
				sql.append(" And m.fec_valor <= convert(datetime,?, 103) \n");
			}else if(dto.getFechaIni()  != null){
//				sql.append(" /*Puede ser también igual*/ \n");
				sql.append(" And m.fec_valor = convert(datetime,?, 103) \n");
			}
			sql.append(" ) hist,  \n");

			sql.append(" ( SELECT count(*) as cantidad, coalesce(sum(importe), 0) as total \n");
			sql.append(" From movimiento m  \n");
			sql.append(" Where no_empresa =  ? \n");
			sql.append(" And m.id_banco =  ? \n");
			sql.append(" And m.id_chequera = ? \n");
			sql.append(" And id_tipo_movto = ? \n");
			sql.append(" and id_estatus_mov in ('I','A','K') \n");
			sql.append(" And id_tipo_operacion in(3200,3700,3701,3705,3706,3708,3709,1018,3101,4001) \n");
//			sql.append(" /*Opcionales*/ \n");
			if(dto.getFechaIni()  != null && dto.getFechaFin() != null){
//				sql.append(" /*Opcionales*/ \n");
				sql.append(" And m.fec_valor >= convert(datetime,?, 103) \n");
				sql.append(" And m.fec_valor <= convert(datetime,?, 103) \n");
			}else if(dto.getFechaIni()  != null){
//				sql.append(" /*Puede ser también igual*/ \n");
				sql.append(" And m.fec_valor = convert(datetime,?, 103) \n");
			}
			sql.append(" ) mov  \n");
			System.out.println("Query"+sql.toString());
			@SuppressWarnings("rawtypes")
			Map mapeo = jdbcTemplate.queryForMap(sql.toString(), this.preparaParametrosMonitorConciliacion(dto));
			
			MonitorConciliaGenDTO retorno = new MonitorConciliaGenDTO();
			
			retorno.setCantidad(((Integer) mapeo.get("cantidad")).intValue());
			retorno.setImporte((BigDecimal)mapeo.get("total"));
			
			return retorno;
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P: Egresos, C: ConciliacionBancoSetDaoImpl, M: existeMov");
		}
		return null;
	}
	
	public MonitorConciliaGenDTO obtenerMovsBANCOMonitor(ParamBusquedaDto dto){
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT coalesce(banca.cantidad, 0) + coalesce(hist_banca.cantidad, 0) as cantidad, \n");
			sql.append(" banca.importe + hist_banca.importe as total \n");
			sql.append(" FROM (Select count(*) as cantidad, coalesce(sum(importe),0) as importe \n");
			sql.append("   From  \n");
			sql.append("   movto_banca_e  \n");
			sql.append("   Where  \n");
			sql.append("   no_empresa = ? \n");
			sql.append("   And id_banco = ? \n");
			sql.append("   And id_chequera = ? \n");
			sql.append("   And cargo_abono  = ? \n");
			sql.append("   And movto_arch =  1  \n");
			if(dto.getFechaIni()  != null && dto.getFechaFin() != null){
				sql.append("   And fec_valor >= convert(datetime,?, 103) \n");
				sql.append("   And fec_valor < convert(datetime,?, 103) +1 \n");
			}else if(dto.getFechaIni()  != null){
				sql.append("   And convert(datetime,fec_valor, 103) = ? \n");
			}
			sql.append("   )  banca \n");
			sql.append(" , \n");
			sql.append(" (  Select count(*) as cantidad, coalesce(sum(importe), 0) as importe \n");
			sql.append("   From  \n");
			sql.append("   hist_movto_banca_e  \n");
			sql.append("   Where  \n");
			sql.append("   no_empresa = ? \n");
			sql.append("   And id_banco = ? \n");
			sql.append("   And id_chequera = ? \n");
			sql.append("   And cargo_abono  = ? \n");
			sql.append("   And movto_arch =  1  \n");
			if(dto.getFechaIni()  != null && dto.getFechaFin() != null){
				sql.append("   And fec_valor >= convert(datetime,?, 103) \n");
				sql.append("   And fec_valor < convert(datetime,?, 103) +1 \n");
			}else if(dto.getFechaIni()  != null){
				sql.append("   And convert(datetime,fec_valor, 103) = ? \n");
			}
			sql.append(" ) hist_banca \n");
			System.out.println("Query "+sql.toString());
			@SuppressWarnings("rawtypes")
			Map mapeo = jdbcTemplate.queryForMap(sql.toString(), this.preparaParametrosMonitorConciliacion(dto));
			
			MonitorConciliaGenDTO retorno = new MonitorConciliaGenDTO();
			
			retorno.setCantidad(((Integer)mapeo.get("cantidad")).intValue());
			retorno.setImporte((BigDecimal)mapeo.get("total"));
			
			return retorno;
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P: Egresos, C: ConciliacionBancoSetDaoImpl, M: existeMov");
		}
		return null;
	}

	
	private Object[] preparaParametrosMonitorConciliacion(ParamBusquedaDto dto){
		Object []parametros = null;
		
		if(dto.getFechaIni()  != null && dto.getFechaFin() != null){
			parametros = new Object[12];
		}else if(dto.getFechaIni()  != null){
			parametros = new Object[10];
		}else
			parametros = new Object[8];
			
		parametros[0] = dto.getIdEmpresa();
		parametros[1] = dto.getIdBanco();
		parametros[2] = dto.getIdChequera();
		parametros[3] = dto.getTipoMovimiento();
		
		if(dto.getFechaIni()  != null && dto.getFechaFin() != null){
			parametros[4] = dto.getsFechaIni();
			parametros[5] = dto.getsFechaFin();
			parametros[6] = dto.getIdEmpresa();
			parametros[7] = dto.getIdBanco();
			parametros[8] = dto.getIdChequera();
			parametros[9] = dto.getTipoMovimiento();
			parametros[10] = dto.getsFechaIni();
			parametros[11] = dto.getsFechaFin();
		}else if(dto.getFechaIni()  != null){
			parametros[4] = dto.getsFechaIni();
			parametros[5] = dto.getIdEmpresa();
			parametros[6] = dto.getIdBanco();
			parametros[7] = dto.getIdChequera();
			parametros[8] = dto.getTipoMovimiento();
			parametros[9] = dto.getsFechaIni();
		}else{
			parametros[4] = dto.getIdEmpresa();
			parametros[5] = dto.getIdBanco();
			parametros[6] = dto.getIdChequera();
			parametros[7] = dto.getTipoMovimiento();
		}
		
//		for(Object o: parametros)
//			System.out.println("Parámetros "+ o.toString());
		
		return parametros;
	}
	
	public MonitorConciliaGenDTO obtenerMovsConcAutomYManualSET(ParamBusquedaDto dto){
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" select coalesce(count(no_folio_det), 0) as cantidad, coalesce(sum(importe), 0) as total \n");
			sql.append(" from (   \n");
			sql.append("   Select coalesce(no_folio_det, 0) as no_folio_det, coalesce(importe, 0) as importe \n");
			sql.append("   From   \n");
			sql.append("   hist_movimiento h   \n");
			sql.append("   Where h.id_estatus_cb in ( '"+ dto.getEstatus() +"')  \n"); /*M o C*/
			sql.append("   And h.no_empresa = "+ dto.getIdEmpresa() +" \n");
			sql.append("   And h.id_tipo_movto = '"+ dto.getTipoMovimiento() +"' \n"); /* E o I*/ 
			sql.append("   And h.id_banco = "+ dto.getIdBanco() +" \n");
			sql.append("   And h.id_chequera = '"+ dto.getIdChequera() +"' \n");
			sql.append("   AND h.id_estatus_mov in ('I','K','A')  \n");
			
			if(dto.getEstatus().equals("M"))
				sql.append("   AND id_tipo_operacion in (3200,3700,3701,3705,3706,3708,3709,1018,3101,4001,4102,4103,4104) \n");
			else
				sql.append("   AND id_tipo_operacion in (3200,3700,3701,3705,3706,3708,3709,1018,3101)  \n");
			
			/*OPCIONAL CUANDO LAS DOS FECHAS LLEGAN*/ 
			if(dto.getFechaIni()  != null && dto.getFechaFin() != null){
				sql.append("   And h.fec_valor >= convert(datetime,'"+ dto.getsFechaIni() +"', 103)  \n");
				sql.append("   And h.fec_valor < convert(datetime,'"+ dto.getsFechaFin() +"', 103) + 1 \n");
			}else if(dto.getFechaIni()  != null){
				/*OPCIONAL CUANDO LLEGA SOLO LA PRIMERA FECHA*/ 
				sql.append("   And convert(datetime,h.fec_valor,103) = '"+ dto.getsFechaIni() +"' \n");
			}
			sql.append("   UNION  \n");
			sql.append("   Select coalesce(no_folio_det, 0) as no_folio_det, coalesce(importe, 0) as importe  \n");
			sql.append("   From   \n");
			sql.append("   movimiento h   \n");
			sql.append("   Where h.id_estatus_cb in ( '"+ dto.getEstatus() +"')  \n"); /*M o C*/
			sql.append("   And h.no_empresa = "+ dto.getIdEmpresa() +" \n");
			sql.append("   And h.id_tipo_movto = '"+ dto.getTipoMovimiento() +"' \n"); /* E o I*/ 
			sql.append("   And h.id_banco = "+ dto.getIdBanco() +" \n");
			sql.append("   And h.id_chequera = '"+ dto.getIdChequera() +"' \n");
			sql.append("   AND h.id_estatus_mov in ('I','K','A')  \n");
			if(dto.getEstatus().equals("M"))
				sql.append("   AND id_tipo_operacion in (3200,3700,3701,3705,3706,3708,3709,1018,3101,4001,4102,4103,4104)  \n");
			else
				sql.append("   AND id_tipo_operacion in (3200,3700,3701,3705,3706,3708,3709,1018,3101)   \n");
			
			/*OPCIONAL CUANDO LAS DOS FECHAS LLEGAN*/ 
			if(dto.getFechaIni()  != null && dto.getFechaFin() != null){
				sql.append("   And h.fec_valor >= convert(datetime,'"+ dto.getsFechaIni() +"', 103)  \n");
				sql.append("   And h.fec_valor < convert(datetime,'"+ dto.getsFechaFin() +"', 103) + 1 \n");
			}else if(dto.getFechaIni()  != null){
				/*OPCIONAL CUANDO LLEGA SOLO LA PRIMERA FECHA*/ 
				sql.append("   And convert(datetime,h.fec_valor,103) = '"+ dto.getsFechaIni() +"' \n");
			}
			sql.append(" ) X\n");
			
	System.out.println("EL QUERY "+sql.toString());
			
			@SuppressWarnings("rawtypes")
			Map mapeo = jdbcTemplate.queryForMap(sql.toString());
			
			MonitorConciliaGenDTO retorno = new MonitorConciliaGenDTO();
			
			retorno.setCantidad(((Integer)mapeo.get("cantidad")).intValue());
			retorno.setImporte((BigDecimal)mapeo.get("total"));
			
			return retorno;
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P: Egresos, C: ConciliacionBancoSetDaoImpl, M: existeMov");
		}
		return null;
	}
	
	public MonitorConciliaGenDTO obtenerMovsConcAutomYManualBANCO(ParamBusquedaDto dto){
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" Select count(*) as cantidad, coalesce(sum(importe), 0) as total  \n");
			sql.append(" From concilia_banco cb  \n");
			sql.append(" Where cb.id_estatus_cb  = '"+ dto.getEstatus() +"'  \n");
			sql.append(" And cb.cargo_abono = '"+ dto.getTipoMovimiento() +"'  \n");
			sql.append(" And cb.no_empresa =  "+ dto.getIdEmpresa() +"  \n");
			sql.append(" And cb.id_banco =  "+ dto.getIdBanco() +"  \n");
			sql.append(" And cb.id_chequera = '"+ dto.getIdChequera() +"'  \n");
			
			/*OPCIONAL CUANDO LAS DOS FECHAS LLEGAN*/ 
			if(dto.getFechaIni()  != null && dto.getFechaFin() != null){
				sql.append(" And cb.fec_operacion >= convert(datetime,'"+ dto.getsFechaIni() +"', 103)  \n");
				sql.append(" And cb.fec_operacion <= convert(datetime,'"+ dto.getsFechaFin() +"', 103)  \n");
			}else if(dto.getFechaIni()  != null){
				/*OPCIONAL CUANDO LLEGA SOLO LA PRIMERA FECHA*/ 
				sql.append(" And cb.fec_operacion = convert(datetime,'"+ dto.getsFechaIni() +"', 103)  \n");
			}
			System.out.println("Query"+sql.toString());
			@SuppressWarnings("rawtypes")
			Map mapeo = jdbcTemplate.queryForMap(sql.toString());
			
			MonitorConciliaGenDTO retorno = new MonitorConciliaGenDTO();
			
			retorno.setCantidad(((Integer)mapeo.get("cantidad")).intValue());
			retorno.setImporte((BigDecimal)mapeo.get("total"));
			
			return retorno;
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P: Egresos, C: ConciliacionBancoSetDaoImpl, M: existeMov");
		}
		return null;
	}

	public MonitorConciliaGenDTO obtenerAclaradosYDetectadosSET(ParamBusquedaDto dto){
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT  \n");
			sql.append(" coalesce(count(no_folio_det), 0) as cantidad, coalesce(sum(importe), 0) as total  \n");
			sql.append(" FROM (  \n");
			sql.append(" Select coalesce(no_folio_det, 0) as no_folio_det, coalesce(importe, 0) as importe  \n");
			sql.append(" From hist_movimiento m   \n");
			sql.append(" Where   \n");
			sql.append(" no_empresa = "+ dto.getIdEmpresa() +"  \n");
			sql.append(" And id_estatus_cb = '"+ dto.getEstatus() +"'  \n");/*D detectado, A aclarado*/ 
			sql.append(" And id_forma_pago in (1,3,4,5,6,7)   \n");
			sql.append(" And m.id_banco = "+ dto.getIdBanco() +" \n");
			sql.append(" And m.id_chequera = '"+ dto.getIdChequera() +"'  \n");
			sql.append(" And id_tipo_movto ='"+ dto.getTipoMovimiento() +"'  \n");
			sql.append(" AND id_estatus_mov in ('I','A','K')  \n");
			sql.append(" AND id_tipo_operacion in (3200,3700,3701,3705,3706,3708,3709,1018,3101,4001,4102,4103,4104)  \n");

			/*OPCIONAL CUANDO LAS DOS FECHAS LLEGAN*/ 
			if(dto.getFechaIni()  != null && dto.getFechaFin() != null){
				sql.append(" And m.fec_valor >= convert(datetime,'"+ dto.getsFechaIni() +"', 103)  \n");
				sql.append(" And m.fec_valor < convert(datetime,'"+ dto.getsFechaFin() +"', 103) + 1  \n");
			}else if(dto.getFechaIni()  != null){
				/*OPCIONAL CUANDO LLEGA SOLO LA PRIMERA FECHA*/ 
				sql.append(" And m.fec_valor = convert(datetime,'"+ dto.getsFechaIni() +"', 103)  \n");
			}
			
			sql.append("UNION \n");
			sql.append(" Select coalesce(no_folio_det, 0) as no_folio_det, coalesce(importe, 0) as importe  \n");
			sql.append(" From movimiento m   \n");
			sql.append(" Where   \n");
			sql.append(" no_empresa = "+ dto.getIdEmpresa() +"  \n");
			sql.append(" And id_estatus_cb = '"+ dto.getEstatus() +"'  \n");/*D detectado, A aclarado*/ 
			sql.append(" And id_forma_pago in (1,3,4,5,6,7)   \n");
			sql.append(" And m.id_banco = "+ dto.getIdBanco() +" \n");
			sql.append(" And m.id_chequera = '"+ dto.getIdChequera() +"'  \n");
			sql.append(" And id_tipo_movto ='"+ dto.getTipoMovimiento() +"'  \n");
			sql.append(" AND id_estatus_mov in ('I','A','K')  \n");
			sql.append(" AND id_tipo_operacion in (3200,3700,3701,3705,3706,3708,3709,1018,3101,4001,4102,4103,4104)  \n");

			/*OPCIONAL CUANDO LAS DOS FECHAS LLEGAN*/ 
			if(dto.getFechaIni()  != null && dto.getFechaFin() != null){
				sql.append(" And m.fec_valor >= convert(datetime,'"+ dto.getsFechaIni() +"', 103)  \n");
				sql.append(" And m.fec_valor < convert(datetime,'"+ dto.getsFechaFin() +"', 103) + 1  \n");
			}else if(dto.getFechaIni()  != null){
				/*OPCIONAL CUANDO LLEGA SOLO LA PRIMERA FECHA*/ 
				sql.append(" And m.fec_valor = convert(datetime,'"+ dto.getsFechaIni() +"', 103)  \n");
			}
			sql.append(" ) X\n");
			System.out.println("Query "+sql.toString());
			
			@SuppressWarnings("rawtypes")
			Map mapeo = jdbcTemplate.queryForMap(sql.toString());
			
			MonitorConciliaGenDTO retorno = new MonitorConciliaGenDTO();
			
			retorno.setCantidad(((Integer)mapeo.get("cantidad")).intValue());
			retorno.setImporte((BigDecimal)mapeo.get("total"));
			
			return retorno;
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P: Egresos, C: ConciliacionBancoSetDaoImpl, M: existeMov");
		}
		return null;
	}

	public MonitorConciliaGenDTO obtenerAclaradosYDetectadosBANCO(ParamBusquedaDto dto){
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" Select count(*) as cantidad, coalesce(sum(importe), 0) as total  \n");
			sql.append(" From concilia_banco cb  \n");
			sql.append(" Where cb.id_estatus_cb  = '"+ dto.getEstatus() +"'  \n");
			sql.append(" And cb.cargo_abono = '"+ dto.getTipoMovimiento() +"'  \n");
			sql.append(" And cb.no_empresa =  "+ dto.getIdEmpresa() +"  \n");
			sql.append(" And cb.id_banco =  "+ dto.getIdBanco() +"  \n");
			sql.append(" And cb.id_chequera = '"+ dto.getIdChequera() +"'  \n");
			
			/*OPCIONAL CUANDO LAS DOS FECHAS LLEGAN*/ 
			if(dto.getFechaIni()  != null && dto.getFechaFin() != null){
				sql.append(" And cb.fec_operacion >= convert(datetime,'"+ dto.getsFechaIni() +"', 103)  \n");
				sql.append(" And cb.fec_operacion <= convert(datetime,'"+ dto.getsFechaFin() +"', 103)  \n");
			}else if(dto.getFechaIni()  != null){
				/*OPCIONAL CUANDO LLEGA SOLO LA PRIMERA FECHA*/ 
				sql.append(" And cb.fec_operacion = convert(datetime,'"+ dto.getsFechaIni() +"', 103)  \n");
			}
			System.out.println("Query "+sql.toString());
			@SuppressWarnings("rawtypes")
			Map mapeo = jdbcTemplate.queryForMap(sql.toString());
			
			MonitorConciliaGenDTO retorno = new MonitorConciliaGenDTO();
			
			retorno.setCantidad(((Integer)mapeo.get("cantidad")).intValue());
			retorno.setImporte((BigDecimal)mapeo.get("total"));
			
			return retorno;
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P: Egresos, C: ConciliacionBancoSetDaoImpl, M: existeMov");
		}
		return null;
	}

	
	public MonitorConciliaGenDTO obtenerPorConciliarSET(ParamBusquedaDto dto){
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" select coalesce(count(no_folio_det), 0)as cantidad, coalesce(sum(importe), 0) as total \n");
			sql.append(" from ( \n");
			sql.append("   Select coalesce(no_folio_det, 0) as no_folio_det, coalesce(importe, 0) as importe \n");
			sql.append("   From hist_movimiento m    \n");
			sql.append("   Where    \n");
			sql.append(" no_empresa = "+ dto.getIdEmpresa() +"  \n");
			sql.append("   And id_estatus_cb in ('P','N')   \n");
			sql.append(" And m.id_banco = "+ dto.getIdBanco() +" \n");
			sql.append(" And m.id_chequera = '"+ dto.getIdChequera() +"'  \n");
			sql.append(" And id_tipo_movto ='"+ dto.getTipoMovimiento() +"'  \n");
			sql.append(" and ((m.b_entregado = 'S'  \n");
			sql.append(" and id_estatus_mov in ('I','R'))  \n");
			sql.append(" or (id_estatus_mov in('K','T')))  \n");
			sql.append("   AND id_tipo_operacion in (3200,3700,3701,3705,3706,3708,3709,1018,3101,4102,4103,4104) \n");
			/*OPCIONAL CUANDO LAS DOS FECHAS LLEGAN*/ 
			if(dto.getFechaIni()  != null && dto.getFechaFin() != null){
				sql.append(" And m.fec_valor >= convert(datetime,'"+ dto.getsFechaIni() +"', 103)  \n");
				sql.append(" And m.fec_valor < convert(datetime,'"+ dto.getsFechaFin() +"', 103) + 1  \n");
			}else if(dto.getFechaIni()  != null){
				/*OPCIONAL CUANDO LLEGA SOLO LA PRIMERA FECHA*/ 
				sql.append(" And m.fec_valor = convert(datetime,'"+ dto.getsFechaIni() +"', 103)  \n");
			}
			sql.append("   union   \n");
			sql.append("   Select coalesce(no_folio_det, 0) as no_folio_det, coalesce(importe,0)  as importe \n");
			sql.append("   From movimiento m    \n");
			sql.append("   Where    \n");
			sql.append(" no_empresa = "+ dto.getIdEmpresa() +"  \n");
			sql.append("   And id_estatus_cb in ('P','N')   \n");
			sql.append(" And m.id_banco = "+ dto.getIdBanco() +" \n");
			sql.append(" And m.id_chequera = '"+ dto.getIdChequera() +"'  \n");
			sql.append(" And id_tipo_movto ='"+ dto.getTipoMovimiento() +"'  \n");
			sql.append(" and ((m.b_entregado = 'S'  \n");
			sql.append(" and id_estatus_mov in ('I','R'))  \n");
			sql.append(" or (id_estatus_mov in('K','T')))  \n");
			sql.append("   AND id_tipo_operacion in (3200,3700,3701,3705,3706,3708,3709,1018,3101,4102,4103,4104) \n");
			/*OPCIONAL CUANDO LAS DOS FECHAS LLEGAN*/ 
			if(dto.getFechaIni()  != null && dto.getFechaFin() != null){
				sql.append(" And m.fec_valor >= convert(datetime,'"+ dto.getsFechaIni() +"', 103)  \n");
				sql.append(" And m.fec_valor < convert(datetime,'"+ dto.getsFechaFin() +"', 103) + 1  \n");
			}else if(dto.getFechaIni()  != null){
				/*OPCIONAL CUANDO LLEGA SOLO LA PRIMERA FECHA*/ 
				sql.append(" And m.fec_valor = convert(datetime,'"+ dto.getsFechaIni() +"', 103)  \n");
			}
			sql.append(" ) X\n");
			
			System.out.println(sql.toString());
			@SuppressWarnings("rawtypes")
			Map mapeo = jdbcTemplate.queryForMap(sql.toString());
			
			MonitorConciliaGenDTO retorno = new MonitorConciliaGenDTO();
			
			retorno.setCantidad(((Integer)mapeo.get("cantidad")).intValue());
			retorno.setImporte((BigDecimal)mapeo.get("total"));
			
			return retorno;
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P: Egresos, C: ConciliacionBancoSetDaoImpl, M: existeMov");
		}
		return null;
	}

	public MonitorConciliaGenDTO obtenerPorConciliarBANCO(ParamBusquedaDto dto){
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" Select count(*) as cantidad, coalesce(sum(importe), 0) as total  \n");
			sql.append(" From concilia_banco cb  \n");
			sql.append(" Where cb.id_estatus_cb  = 'P'  \n");
			sql.append(" And cb.cargo_abono = '"+ dto.getTipoMovimiento() +"'  \n");
			sql.append(" And cb.no_empresa =  "+ dto.getIdEmpresa() +"  \n");
			sql.append(" And cb.id_banco =  "+ dto.getIdBanco() +"  \n");
			sql.append(" And cb.id_chequera = '"+ dto.getIdChequera() +"'  \n");
			
			/*OPCIONAL CUANDO LAS DOS FECHAS LLEGAN*/ 
			if(dto.getFechaIni()  != null && dto.getFechaFin() != null){
				sql.append(" And cb.fec_operacion >= convert(datetime,'"+ dto.getsFechaIni() +"', 103)  \n");
				sql.append(" And cb.fec_operacion <= convert(datetime,'"+ dto.getsFechaFin() +"', 103)  \n");
			}else if(dto.getFechaIni()  != null){
				/*OPCIONAL CUANDO LLEGA SOLO LA PRIMERA FECHA*/ 
				sql.append(" And cb.fec_operacion = convert(datetime,'"+ dto.getsFechaIni() +"', 103)  \n");
			}
			System.out.println("Query"+sql.toString());
			@SuppressWarnings("rawtypes")
			Map mapeo = jdbcTemplate.queryForMap(sql.toString());
			
			MonitorConciliaGenDTO retorno = new MonitorConciliaGenDTO();
			
			retorno.setCantidad(((Integer)mapeo.get("cantidad")).intValue());
			retorno.setImporte((BigDecimal)mapeo.get("total"));
			
			return retorno;
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P: Egresos, C: ConciliacionBancoSetDaoImpl, M: existeMov");
		}
		return null;
	}

	public MonitorConciliaGenDTO obtenerPendientesSET(ParamBusquedaDto dto){
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT   \n");
			sql.append(" coalesce(count(no_folio_det), 0) as cantidad, coalesce(sum(importe), 0) as total \n");
			sql.append(" FROM ( \n");
			sql.append(" Select coalesce(no_folio_det, 0) as no_folio_det, coalesce(importe, 0)  as importe \n");
			sql.append(" From hist_movimiento m    \n");
			sql.append(" Where    \n");
			sql.append(" no_empresa = "+ dto.getIdEmpresa() +"  \n");
			sql.append(" and ((coalesce(m.b_entregado,'') <> 'S'  \n");
			sql.append(" and id_estatus_mov in ('I','R'))  \n");
			sql.append(" or (id_estatus_mov in('P','J','A')))  \n");
			sql.append(" And m.id_banco = "+ dto.getIdBanco() +" \n");
			sql.append(" And m.id_chequera = '"+ dto.getIdChequera() +"'  \n");
			sql.append(" And id_tipo_movto ='"+ dto.getTipoMovimiento() +"'  \n");
			sql.append(" AND id_tipo_operacion in (3200,3700,3701,3705,3706,3708,3709,1018,3101,4001,4102,4103,4104)  \n");

			/*OPCIONAL CUANDO LAS DOS FECHAS LLEGAN*/ 
			if(dto.getFechaIni()  != null && dto.getFechaFin() != null){
				sql.append(" And m.fec_valor >= convert(datetime,'"+ dto.getsFechaIni() +"', 103)  \n");
				sql.append(" And m.fec_valor < convert(datetime,'"+ dto.getsFechaFin() +"', 103) + 1  \n");
			}else if(dto.getFechaIni()  != null){
				/*OPCIONAL CUANDO LLEGA SOLO LA PRIMERA FECHA*/ 
				sql.append(" And m.fec_valor = convert(datetime,'"+ dto.getsFechaIni() +"', 103)  \n");
			}
			sql.append(" union \n");
			sql.append(" Select coalesce(no_folio_det, 0) as no_folio_det, coalesce(importe, 0) as  importe \n");
			sql.append(" From movimiento m    \n");
			sql.append(" Where    \n");
			sql.append(" no_empresa = "+ dto.getIdEmpresa() +"  \n");
			sql.append(" and ((coalesce(m.b_entregado,'') <> 'S'  \n");
			sql.append(" and id_estatus_mov in ('I','R'))  \n");
			sql.append(" or (id_estatus_mov in('P','J','A')))  \n");
			sql.append(" And m.id_banco = "+ dto.getIdBanco() +" \n");
			sql.append(" And m.id_chequera = '"+ dto.getIdChequera() +"'  \n");
			sql.append(" And id_tipo_movto ='"+ dto.getTipoMovimiento() +"'  \n");
			//sql.append(" AND id_estatus_mov in ('I','A','K')  \n");
			sql.append(" AND id_tipo_operacion in (3200,3700,3701,3705,3706,3708,3709,1018,3101,4001,4102,4103,4104)  \n");

			/*OPCIONAL CUANDO LAS DOS FECHAS LLEGAN*/ 
			if(dto.getFechaIni()  != null && dto.getFechaFin() != null){
				sql.append(" And m.fec_valor >= convert(datetime,'"+ dto.getsFechaIni() +"', 103)  \n");
				sql.append(" And m.fec_valor < convert(datetime,'"+ dto.getsFechaFin() +"', 103) + 1  \n");
			}else if(dto.getFechaIni()  != null){
				/*OPCIONAL CUANDO LLEGA SOLO LA PRIMERA FECHA*/ 
				sql.append(" And m.fec_valor = convert(datetime,'"+ dto.getsFechaIni() +"', 103)  \n");
			}
			sql.append(" ) X\n");
			System.out.println("Query"+sql.toString());
			@SuppressWarnings("rawtypes")
			Map mapeo = jdbcTemplate.queryForMap(sql.toString());
			
			MonitorConciliaGenDTO retorno = new MonitorConciliaGenDTO();
			
			retorno.setCantidad(((Integer)mapeo.get("cantidad")).intValue());
			retorno.setImporte((BigDecimal)mapeo.get("total"));
			
			return retorno;
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P: Egresos, C: ConciliacionBancoSetDaoImpl, M: existeMov");
		}
		return null;
	}

	public MonitorConciliaGenDTO obtenerAjusteSET(ParamBusquedaDto dto){
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT    \n");
			sql.append("  coalesce(mov.cantidad, 0)+ coalesce(hist.cantidad,0) as cantidad,    \n");
			sql.append("  coalesce(mov.total, 0)+ coalesce(hist.total,0) as total    \n");
			sql.append("  FROM (  \n");
			sql.append(" Select count(*) as cantidad, coalesce(sum(importe), 0) as total  \n");
			sql.append(" FROM movimiento  \n");
			if(dto.getTipoMovimiento().equals('E'))
				sql.append(" WHERE id_tipo_operacion  = 1019  \n");
			else
				sql.append(" WHERE id_tipo_operacion  = 1020  \n");
			sql.append(" And id_tipo_movto = '"+ dto.getTipoMovimiento() +"'  \n");
			sql.append(" And id_banco = "+ dto.getIdBanco() +"  \n");
			sql.append(" And id_chequera = '"+ dto.getIdChequera() +"'  \n");
			sql.append(" And no_empresa = "+ dto.getIdEmpresa()  +"  \n");
			/*OPCIONAL CUANDO LAS DOS FECHAS LLEGAN*/ 
			if(dto.getFechaIni()  != null && dto.getFechaFin() != null){
				sql.append(" And fec_valor >= convert(datetime,'"+ dto.getsFechaIni() +"', 103)  \n");
				sql.append(" And fec_valor < convert(datetime,'"+ dto.getsFechaFin() +"', 103) + 1  \n");
			}else if(dto.getFechaIni()  != null){
				/*OPCIONAL CUANDO LLEGA SOLO LA PRIMERA FECHA*/ 
				sql.append(" And fec_valor = convert(datetime,'"+ dto.getsFechaIni() +"', 103)  \n");
			}
			sql.append(" ) mov, (  \n");
			sql.append(" Select count(*) as cantidad, coalesce(sum(importe), 0) as total  \n");
			sql.append(" FROM hist_movimiento  \n");
			if(dto.getTipoMovimiento().equals('E'))
				sql.append(" WHERE id_tipo_operacion  = 1019  \n");
			else
				sql.append(" WHERE id_tipo_operacion  = 1020  \n");
			sql.append(" And id_tipo_movto = '"+ dto.getTipoMovimiento() +"'  \n");
			sql.append(" And id_banco = "+ dto.getIdBanco() +"  \n");
			sql.append(" And id_chequera = '"+ dto.getIdChequera() +"'  \n");
			sql.append(" And no_empresa = "+ dto.getIdEmpresa()  +"  \n");
			/*OPCIONAL CUANDO LAS DOS FECHAS LLEGAN*/ 
			if(dto.getFechaIni()  != null && dto.getFechaFin() != null){
				sql.append(" And fec_valor >= convert(datetime,'"+ dto.getsFechaIni() +"', 103)  \n");
				sql.append(" And fec_valor < convert(datetime,'"+ dto.getsFechaFin() +"', 103) + 1  \n");
			}else if(dto.getFechaIni()  != null){
				/*OPCIONAL CUANDO LLEGA SOLO LA PRIMERA FECHA*/ 
				sql.append(" And fec_valor = convert(datetime,'"+ dto.getsFechaIni() +"', 103)  \n");
			}
			sql.append(" )hist  \n");
			System.out.println("Query"+sql.toString());
			@SuppressWarnings("rawtypes")
			Map mapeo = jdbcTemplate.queryForMap(sql.toString());
			
			MonitorConciliaGenDTO retorno = new MonitorConciliaGenDTO();
			
			retorno.setCantidad(((Integer)mapeo.get("cantidad")).intValue());
			retorno.setImporte((BigDecimal)mapeo.get("total"));
			
			return retorno;
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P: Egresos, C: ConciliacionBancoSetDaoImpl, M: existeMov");
		}
		return null;
	}

	public MonitorConciliaGenDTO obtenerSaldosInicialesMonitor(ParamBusquedaDto dto){
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" select coalesce(sum(saldo_inicial), 0) as saldo_inicial_set , coalesce(sum(saldo_banco_ini), 0) as saldo_inicial_banco  \n");
			sql.append(" from hist_cat_cta_banco  \n");
			sql.append(" where id_banco = "+ dto.getIdBanco() +"  \n");
			sql.append(" and id_chequera = '"+ dto.getIdChequera() +"'  \n");
			/*OPCIONAL CUANDO LAS DOS FECHAS LLEGAN*/ 
			if(dto.getFechaIni()  != null && dto.getFechaFin() != null){
				sql.append(" And fec_valor >= convert(datetime,'"+ dto.getsFechaIni() +"', 103)  \n");
				sql.append(" And fec_valor < convert(datetime,'"+ dto.getsFechaFin() +"', 103) + 1  \n");
			}else if(dto.getFechaIni()  != null){
				/*OPCIONAL CUANDO LLEGA SOLO LA PRIMERA FECHA*/ 
				sql.append(" And fec_valor = convert(datetime,'"+ dto.getsFechaIni() +"', 103)  \n");
			}
			System.out.println("Query "+sql.toString());
			@SuppressWarnings("rawtypes")
			Map mapeo = jdbcTemplate.queryForMap(sql.toString());
			
			MonitorConciliaGenDTO retorno = new MonitorConciliaGenDTO();
			
			retorno.setImporte((BigDecimal)mapeo.get("saldo_inicial_set"));
			retorno.setImporte2((BigDecimal)mapeo.get("saldo_inicial_banco"));
			
			return retorno;
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P: Egresos, C: ConciliacionBancoSetDaoImpl, M: existeMov");
		}
		return null;
	}

}

