package com.webset.set.caja.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.webset.set.caja.dao.CajaDao;
import com.webset.set.impresion.dto.ConsultaChequesDto;
import com.webset.set.traspasos.dto.MovimientoDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.CuadrantesDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.StoreParamsComunDto;

/**
 * Consultas del Modulo Caja
 * @author Jessica Arelly Cruz Cruz
 * @since 27/07/2011
 */
public class CajaDaoImpl implements CajaDao{
	private JdbcTemplate jdbcTemplate; 
	private Bitacora bitacora = new Bitacora();
	private Funciones funciones = new Funciones();
	private GlobalSingleton globalSingleton;
	private static Logger logger = Logger.getLogger(CajaDaoImpl.class);
	private ConsultasGenerales consultasGenerales;
	
	/**
	 * Metodo que consulta las empresas asignadas al usuario activo
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboEmpresasDto> consultarEmpresas(int idUsuario)
	{
		StringBuffer sbSQL = new StringBuffer();
		List<LlenaComboEmpresasDto> list= new ArrayList<LlenaComboEmpresasDto>();
		try{
			sbSQL.append( " SELECT no_empresa as ID, nom_empresa as describ ");
		    sbSQL.append( "\n	FROM empresa	");
		    sbSQL.append( "\n	WHERE no_empresa > 1	");
		    sbSQL.append( "\n	AND no_empresa in (SELECT no_empresa	");
		    sbSQL.append( "\n	FROM usuario_empresa	");
		    sbSQL.append( "\n	WHERE no_usuario = " + idUsuario + ")	");
		    sbSQL.append( "\n	ORDER BY describ	");
		    
		    list= jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public LlenaComboEmpresasDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboEmpresasDto cons = new LlenaComboEmpresasDto();
					cons.setNoEmpresa(rs.getInt("ID"));
					cons.setNomEmpresa(rs.getString("describ"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Caja, C:CajaDaoImpl, M:consultarEmpresas");
		}
		
		return list;
	}
	
	/**
	 * Metodo que consulta los bancos asignados a la empresa seleccionada
	 * FunSQLCombo89
	 * @param iEmpresa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarBancos(int iEmpresa){
		List<LlenaComboGralDto>list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  SELECT id_banco as ID, desc_banco as describ ");
		    sbSQL.append("\n  FROM cat_banco");
		    sbSQL.append("\n  WHERE id_banco IN ");
		    sbSQL.append("\n      ( SELECT DISTINCT id_banco");
		    sbSQL.append("\n        FROM cat_cta_banco ");
		    sbSQL.append("\n        WHERE no_empresa = " + iEmpresa + ")");
			
		    list= jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("describ"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Caja, C:CajaDaoImpl, M:consultarBancos");
		}
		return list;
	}
	
	/**
	 * Metodo que consulta los bancos en el combo de la ventana para imprimir reportes
	 * FunSQLCombo88
	 * @param iEmpresa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarBancosVentana(int iEmpresa){
		List<LlenaComboGralDto>list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  SELECT DISTINCT cat_banco.id_banco as ID,cat_banco.desc_banco as describ ");
		    sbSQL.append("\n  FROM cat_banco,cat_cta_banco");
		    sbSQL.append("\n  WHERE cat_cta_banco.id_banco = cat_banco.id_banco");
		    sbSQL.append("\n      and cat_cta_banco.no_empresa = " + iEmpresa);
			
		    list= jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("describ"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Caja, C:CajaDaoImpl, M:consultarBancosVentana");
		}
		return list;
	}
	
	/**
	 * Metodo que consulta las divisas 
	 * FunSQLCombo177
	 * @param cnt
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboDivisaDto> consultarDivisas(int cnt){
		List<LlenaComboDivisaDto>list = new ArrayList<LlenaComboDivisaDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  SELECT id_divisa,desc_divisa  ");
		    sbSQL.append("\n  FROM cat_divisa");
		    
		    if(cnt == 0)
	            sbSQL.append("\n  WHERE id_divisa = 'CNT'");
		    else if(cnt == 1)
	            sbSQL.append("\n  WHERE clasificacion = 'D'");
		    else
            	sbSQL.append("\n  WHERE clasificacion = 'D' or id_divisa = 'CNT'");
			
		    list= jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public LlenaComboDivisaDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboDivisaDto cons = new LlenaComboDivisaDto();
					cons.setIdDivisa(rs.getString("id_divisa"));
					cons.setDescDivisa(rs.getString("desc_divisa"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Caja, C:CajaDaoImpl, M:consultarDivisas");
		}
		return list;
	}
	
	/**
	 * Metodo que consulta las chequeras asignadas al banco seleccionado
	 * @param iBanco
	 * @param iEmpresa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboChequeraDto> consultarChequeras(int iBanco, int iEmpresa){
		List<LlenaComboChequeraDto>list = new ArrayList<LlenaComboChequeraDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  SELECT id_chequera as ID, id_chequera as describ ");
		    sbSQL.append("\n  FROM cat_cta_banco");
		    sbSQL.append("\n  WHERE id_banco = " + iBanco);
		    sbSQL.append("\n      AND no_empresa = " + iEmpresa);
			
		    list= jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public LlenaComboChequeraDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboChequeraDto cons = new LlenaComboChequeraDto();
					cons.setId(rs.getString("ID"));
					cons.setDescripcion(rs.getString("describ"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Caja, C:CajaDaoImpl, M:consultarChequeras");
		}
		return list;
	}
	
	/**
	 * Metodo que consulta las chequeras del banco seleccionado en la ventana
	 * FunSQLCombo87
	 * @param iBanco
	 * @param iEmpresa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboChequeraDto> consultarChequerasVentana(int iBanco, int iEmpresa){
		List<LlenaComboChequeraDto>list = new ArrayList<LlenaComboChequeraDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  SELECT id_chequera as ID,id_chequera as describ ");
		    sbSQL.append("\n  FROM cat_cta_banco ");
		    sbSQL.append("\n  WHERE id_banco = " + iBanco);
		    sbSQL.append("\n      and tipo_chequera  in ('C','P','I','M','O')");
		    sbSQL.append("\n      and no_empresa =" + iEmpresa);
			
		    list= jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public LlenaComboChequeraDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboChequeraDto cons = new LlenaComboChequeraDto();
					cons.setId(rs.getString("ID"));
					cons.setDescripcion(rs.getString("describ"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Caja, C:CajaDaoImpl, M:consultarChequerasVentana");
		}
		return list;
	}
	
	/**
	 * Metodo que consulta los cheques que estan pendientes por entregar
	 * FunSQLSelect168
	 * @param dtoBus
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<MovimientoDto> consultarChequesPorEntregar(ConsultaChequesDto dtoBus){
		List<MovimientoDto> list = new ArrayList<MovimientoDto> ();
		StringBuffer sbSQL = new StringBuffer();
		try{
		    
		    if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("ORACLE") || ConstantesSet.gsDBM.equals("SYBASE"))
		    {
	           sbSQL.append("\n  SELECT a.no_empresa ,nom_empresa as empresa, desc_banco as banco,id_chequera,importe,id_divisa as divisa,no_cheque,");
	           sbSQL.append("\n      beneficiario,fec_valor,concepto,no_folio_det, id_tipo_operacion, ");
	           sbSQL.append("\n      coalesce((select no_folio_det from control_fondeo_cheques where no_folio_det = a.no_folio_det), 0) as folio_seg, ce.desc_estatus, a.id_estatus_mov, a.id_banco, a.cve_control ");
	           sbSQL.append("\n   FROM movimiento a   RIGHT JOIN cat_banco b  ON b.id_banco = a.id_banco  JOIN empresa c on a.no_empresa=c.no_empresa join  cat_estatus ce on a.id_estatus_mov=ce.id_estatus ");
	           sbSQL.append("\n  WHERE ");
	           
	           if(dtoBus.getNoEmpresa() != 0)
	                sbSQL.append("\n  c.no_empresa = " + dtoBus.getNoEmpresa() + " And  ");
	           
	           sbSQL.append("\n      b_entregado = 'N' ");
	      //     sbSQL.append("\n      and a.no_empresa = c.no_empresa ");//anterir remplazado por Rigthjoin
	           sbSQL.append("\n      And id_estatus_mov in ('I', 'G', 'V') "); //, 'R'
	           sbSQL.append("\n      and id_forma_pago = 1 ");
	          // sbSQL.append("\n      And b.id_banco =* a.id_banco ");//anterir remplazado por Rigthjoin
	           sbSQL.append("\n      And a.id_caja in (" + dtoBus.getIdCaja() + ")");
	           sbSQL.append("\n      and id_tipo_operacion in (3200, 3000) ");
	          // sbSQL.append("\n      and a.id_estatus_mov = ce.id_estatus ");//anterir remplazado por Rigthjoin
	           sbSQL.append("\n      and ce.clasificacion = 'CHE' ");
	        	   
	           //validacion de criterios de busqueda
               if(dtoBus.getCriterioBanco() != 0)
            	   sbSQL.append("\n      And a.id_banco = " + dtoBus.getCriterioBanco());
	           
               if(!dtoBus.getCriterioConcepto().equals(""))
	               sbSQL.append("\n      And concepto Like '%" + dtoBus.getCriterioConcepto() + "%'");
	           
               if(dtoBus.getCriterioFechaIni() != null && dtoBus.getCriterioFechaFin() != null)
               {
                    sbSQL.append("\n    And fec_valor between ");
                    sbSQL.append("\n '" + 	funciones.ponerFechaSola(dtoBus.getCriterioFechaIni()) + "' and ");
            		sbSQL.append("\n '" + 	funciones.ponerFechaSola(dtoBus.getCriterioFechaFin()) + "'");
               }
               else if(dtoBus.getCriterioFechaIni() != null)
               {
                    sbSQL.append("\n      And fec_valor = '" + funciones.ponerFechaSola(dtoBus.getCriterioFechaIni()) + "'");
               }
	           
               if(dtoBus.getCriterioImporteIni() != 0)
            	   sbSQL.append("\n      And importe >= " + dtoBus.getCriterioImporteIni());
               
               if(dtoBus.getCriterioImporteFin() != 0)
                   sbSQL.append("\n      And importe <=" + dtoBus.getCriterioImporteFin());
	           
               if(!dtoBus.getCriterioBeneficiario().equals(""))
	               sbSQL.append("\n      And beneficiario Like '%" + dtoBus.getCriterioBeneficiario() + "%'");
	           
               if(dtoBus.getCriterioSolicitudIni() != 0 && dtoBus.getCriterioSolicitudFin() != 0)
               {
                   sbSQL.append("\n      And no_cheque between ");
                   sbSQL.append("\n      " + dtoBus.getCriterioSolicitudIni());
                   sbSQL.append("\n      and " + dtoBus.getCriterioSolicitudFin());
               }
               else if(dtoBus.getCriterioSolicitudIni() != 0)
                   sbSQL.append("\n      And no_cheque in (" + dtoBus.getCriterioSolicitudIni() + ")");
	           
               if(!dtoBus.getCriterioChequera().equals(""))
	               sbSQL.append("\n      And a.id_chequera = '" + dtoBus.getCriterioChequera() + "'");
	       
               if(!dtoBus.getCriterioDivisa().equals(""))
	               sbSQL.append("\n      And a.id_divisa = '" + dtoBus.getCriterioDivisa() + "'");
		    }
		    
		    if(ConstantesSet.gsDBM.equals("POSTGRESQL") || ConstantesSet.gsDBM.equals("DB2"))
		    {
		           sbSQL.append("\n  SELECT a.no_empresa  ,nom_empresa as empresa ,desc_banco as banco,id_chequera,importe,id_divisa as divisa,no_cheque,");
		           sbSQL.append("\n      beneficiario,fec_valor,concepto,no_folio_det, id_tipo_operacion ");
		           sbSQL.append("\n  FROM cat_banco b ");
		           sbSQL.append("\n      RIGHT JOIN movimiento a ON(b.id_banco = a.id_banco)");
		           sbSQL.append("\n      RIGHT JOIN empresa c ON(a.no_empresa =c.no_empresa)");
		           
		           sbSQL.append("\n  WHERE ");
		           
		           if(dtoBus.getNoEmpresa() != 0)
		                sbSQL.append("\n  c.no_empresa = " + dtoBus.getNoEmpresa() + " And  ");
		           
		           sbSQL.append("\n      b_entregado =  'N' ");
		           sbSQL.append("\n      And id_estatus_mov in ('I', 'R', 'G', 'V') ");
		           sbSQL.append("\n      and id_forma_pago = 1 ");
		           sbSQL.append("\n          And a.id_caja in (" + dtoBus.getIdCaja() + ")");
		           sbSQL.append("\n  and id_tipo_operacion in (3200, 3000) ");
		           
		         //validacion de criterios de busqueda
	               if(dtoBus.getCriterioBanco() != 0)
	            	   sbSQL.append("\n      And a.id_banco = " + dtoBus.getCriterioBanco());
		           
	               if(!dtoBus.getCriterioConcepto().equals(""))
		               sbSQL.append("\n      And concepto Like '" + dtoBus.getCriterioConcepto() + "'");
		           
	               if(dtoBus.getCriterioFechaIni() != null && dtoBus.getCriterioFechaFin() != null)
	               {
	                    sbSQL.append("\n    And fec_valor between ");
	                    sbSQL.append("\n '" + 	funciones.ponerFechaSola(dtoBus.getCriterioFechaIni()) + "' and ");
	            		sbSQL.append("\n '" + 	funciones.ponerFechaSola(dtoBus.getCriterioFechaFin()) + "'");
	               }
	               else if(dtoBus.getCriterioFechaIni() != null)
	               {
	                    sbSQL.append("\n      And fec_valor = '" + funciones.ponerFechaSola(dtoBus.getCriterioFechaIni()) + "'");
	               }
		           
	               if(dtoBus.getCriterioImporteIni() != 0)
	            	   sbSQL.append("\n      And importe >= " + dtoBus.getCriterioImporteIni());
	               
	               if(dtoBus.getCriterioImporteFin() != 0)
	                   sbSQL.append("\n      And importe <=" + dtoBus.getCriterioImporteFin());
		           
	               if(!dtoBus.getCriterioBeneficiario().equals(""))
		               sbSQL.append("\n      And beneficiario Like '" + dtoBus.getCriterioBeneficiario() + "'");
		           
	               if(dtoBus.getCriterioSolicitudIni() != 0 && dtoBus.getCriterioSolicitudFin() != 0)
	               {
	                   sbSQL.append("\n      And no_cheque between ");
	                   sbSQL.append("\n      " + dtoBus.getCriterioSolicitudIni());
	                   sbSQL.append("\n      and " + dtoBus.getCriterioSolicitudFin());
	               }
	               else if(dtoBus.getCriterioSolicitudIni() != 0)
	                   sbSQL.append("\n      And no_cheque in (" + dtoBus.getCriterioSolicitudIni() + ")");
		           
	               if(!dtoBus.getCriterioChequera().equals(""))
		               sbSQL.append("\n      And a.id_chequera = '" + dtoBus.getCriterioChequera() + "'");
		       
	               if(!dtoBus.getCriterioDivisa().equals(""))
		               sbSQL.append("\n      And a.id_divisa = '" + dtoBus.getCriterioDivisa() + "'");
		    }
		    //logger.info("chequesXentregar: "+sbSQL.toString());
		    
		    System.out.println("query trae cheques a entregar: " + sbSQL);
		    
		    list= jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons = new MovimientoDto();
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setNomEmpresa(rs.getString("empresa"));
					cons.setDescBanco(rs.getString("banco"));
					cons.setIdChequera(rs.getString("id_chequera"));
					cons.setImporte(rs.getDouble("importe"));
					cons.setIdDivisa(rs.getString("divisa"));
					cons.setNoCheque(rs.getInt("no_cheque"));
					cons.setBeneficiario(rs.getString("beneficiario"));
					cons.setFecValor(rs.getDate("fec_valor"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setNoFolioDet(rs.getInt("no_folio_det"));
					cons.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
					cons.setFolioSeg(rs.getInt("folio_seg"));
					cons.setIdEstatusCheq(rs.getString("desc_estatus"));
					cons.setIdEstatusMov(rs.getString("id_estatus_mov"));
					cons.setIdBanco(rs.getInt("id_banco"));
					cons.setCveControl(rs.getString("cve_control"));
					return cons;
				}});
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Caja, C:CajaDaoImpl, M:consultarChequesPorEntregar");
		}
		return list;
	}
	
	/**
	 * Metodo que actualiza el estatus b_entregado en la tabla movimiento
	 * FunSQLUpdateEstatusEfvo
	 * @param sFolios
	 * @return
	 */
	public int actualizarEstatusEfvo(String sFolios){
		int actualiza = 0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  UPDATE movimiento ");
		    sbSQL.append("\n  SET b_entregado = 'S' ");
		    sbSQL.append("\n  WHERE no_folio_det in ( " + sFolios + ") or folio_ref in ( " + sFolios + ")");
		    actualiza = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Caja, C:CajaDaoImpl, M:actualizarEstatusEfvo");
		}
		return actualiza;
	}
	
	/**
	 * Metodo que actualiza el numero de recibo en la tabla movimiento
	 * FunSQLUpdateFolioCaja
	 * @param iNoRecibo
	 * @param iFolioDet
	 * @return
	 */
	public int actualizaFolioCaja(int iNoRecibo, int iFolioDet){
		int actualiza = 0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  UPDATE movimiento ");
		    sbSQL.append("\n  SET no_recibo = " + iNoRecibo);
		    sbSQL.append("\n  WHERE no_folio_det = " + iFolioDet);
		    
		    actualiza = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Caja, C:CajaDaoImpl, M:actualizaFolioCaja");
		}
		return actualiza;
	}
	
	/**
	 * Metodo que consulta el folio siguiente
	 * @param tipoFolio
	 * @return
	 */
	public int seleccionarFolioReal(String tipoFolio) {
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append(" SELECT num_folio ");
			sbSQL.append("\n FROM folio ");
			sbSQL.append("\n WHERE tipo_folio = '" + tipoFolio + "'");
		
			return jdbcTemplate.queryForInt(sbSQL.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " " + e.toString()
					+ "P:Caja, C:CajaDaoImpl, M:seleccionarFolioReal");
			return -1;
		}
	}
	
	/**
	 * Metodo que realiza la ejecusion del store sp_CuadranteNETROSET
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map ejecutaCuadranteNetroSet(CuadrantesDto dto){
		Map resultado= new HashMap();
		try{
			StoredProcedure storedProcedure = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_CuadranteNETROSET") {};
			
			storedProcedure.declareParameter(new SqlParameter("folios",Types.VARCHAR ));
			storedProcedure.declareParameter(new SqlInOutParameter("mensaje",Types.VARCHAR));
			storedProcedure.declareParameter(new SqlInOutParameter("result",Types.INTEGER));
			
			HashMap< Object, Object > inParams = new HashMap< Object, Object >();
			inParams.put("folios",dto.getFolios());
			inParams.put("mensaje",dto.getMensaje());
			inParams.put("result",dto.getResult());
			
			logger.info("mensaje= "+inParams.get("mensaje"));
			logger.info("result= "+inParams.get("result"));
			logger.info("folios= "+inParams.get("folios"));
			
			resultado = storedProcedure.execute((Map)inParams);
			
			}catch (Exception e) {
				bitacora.insertarRegistro(new Date().toString()+ " " + e.toString()
						+ "P:Caja, C:CajaDaoImpl, M:ejecutaCuadranteNetroSet");
		}
		return resultado;
	}
	
	//reportes
	/**
	 * Metodo que consulta los registros del reporte de cheques por entregar
	 * FunSQLReporteCehequeXent
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> consultarReporteChequesPorEntregar(Map<String, Object> datos){
		List<Map<String, Object>> resultado = null;
		StringBuffer sbSQL = new StringBuffer();
		try{
	        if(ConstantesSet.gsDBM.equals("ORACLE"))
	        {
	            sbSQL.append("\n      SELECT CASE WHEN m.folio_banco is null THEN '0' ");
	            sbSQL.append("\n                  ELSE m.folio_banco END as folio_banco,");
	            sbSQL.append("\n          m.importe as importe, m.beneficiario, m.concepto,");
	            sbSQL.append("\n          m.lote_entrada, m.id_divisa, m.id_banco, cb.desc_banco,");
	            sbSQL.append("\n          m.no_docto, m.fec_imprime, m.fec_valor,m.id_chequera,");
	            sbSQL.append("\n          m.id_caja, cc.desc_caja, e.no_empresa AS empresa,");
	            sbSQL.append("\n          e.nom_empresa,m.no_cheque,");
	            sbSQL.append("\n          CASE WHEN m.id_estatus_mov = 'I' then 'IMPRESO'");
	            sbSQL.append("\n               WHEN m.id_estatus_mov = 'R' then 'REIMPRESO'");
	            sbSQL.append("\n          END As id_estatus_mov");
	            sbSQL.append("\n      FROM movimiento m left outer join cat_banco cb on (m.id_banco = cb.id_banco), left outer join cat_caja cc on (m.id_caja = cc.id_caja), empresa e");
	            sbSQL.append("\n      WHERE m.no_empresa = e.no_empresa");
	            sbSQL.append("\n          and m.no_empresa between " + funciones.convertirCadenaInteger(datos.get("empresaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("empresaSup").toString()));
	            sbSQL.append("\n          and m.id_banco between " + funciones.convertirCadenaInteger(datos.get("bancoInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("bancoSup").toString()));
	            sbSQL.append("\n          and m.id_chequera like '" + funciones.validarCadena(datos.get("chequera").toString()) + "'");
	            sbSQL.append("\n          and id_tipo_movto = 'E'");
	            sbSQL.append("\n          and id_forma_pago in (1,7)");
	            sbSQL.append("\n          and id_estatus_mov in ('I','R')");
	            sbSQL.append("\n          and m.b_entregado = 'N'");
	            sbSQL.append("\n          and m.fec_imprime between '" + funciones.ponerFechaSola(datos.get("fechaInf").toString())+ "' and '" + funciones.ponerFechaSola(datos.get("fechaSup").toString())+ "'");
	            sbSQL.append("\n          and m.id_caja between " + funciones.convertirCadenaInteger(datos.get("cajaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("cajaSup").toString()));
	            
	            if(datos.get("divisa") != null && !datos.get("divisa").equals(""))
	            {
	            	sbSQL.append("\n          and m.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "'");
	            }
	            
	            sbSQL.append("\n     ORDER BY id_chequera, desc_banco, nom_empresa, empresa");
	        }
	        
	        if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
	        {
	            sbSQL.append("\n      SELECT CASE WHEN m.folio_banco is null THEN '0' ");
	            sbSQL.append("\n                  ELSE m.folio_banco END as folio_banco,");
	            sbSQL.append("\n          m.importe as importe, m.beneficiario, m.concepto,");
	            sbSQL.append("\n          m.lote_entrada, m.id_divisa, m.id_banco,cb.desc_banco,");
	            sbSQL.append("\n          m.no_docto, convert(char, m.fec_imprime, 103) as fec_imprime, convert(char, m.fec_valor, 103) as fec_valor,m.id_chequera,");
	            sbSQL.append("\n          m.id_caja, cc.desc_caja, e.no_empresa AS empresa,");
	            sbSQL.append("\n          e.nom_empresa,m.no_cheque,");
	            sbSQL.append("\n          CASE WHEN m.id_estatus_mov = 'I' then 'IMPRESO'");
	            sbSQL.append("\n               WHEN m.id_estatus_mov = 'R' then 'REIMPRESO'");
	            sbSQL.append("\n          END As id_estatus_mov");
	            sbSQL.append("\n         FROM movimiento m LEFT JOIN cat_banco cb ON m.id_banco=cb.id_banco left join cat_caja cc on m.id_caja=cc.id_caja  join  empresa e on m.no_empresa=e.no_empresa ");
	           // sbSQL.append("\n      WHERE m.id_banco *= cb.id_banco");
	          //  sbSQL.append("\n          and m.no_empresa = e.no_empresa");
	           // sbSQL.append("\n          and m.id_caja *= cc.id_caja");
	            sbSQL.append("\n  WHERE ");
	            sbSQL.append("\n          m.no_empresa between " + funciones.convertirCadenaInteger(datos.get("empresaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("empresaSup").toString()));
	            sbSQL.append("\n          and m.id_banco between " + funciones.convertirCadenaInteger(datos.get("bancoInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("bancoSup").toString()));
	            sbSQL.append("\n          and m.id_chequera like '" + funciones.validarCadena(datos.get("chequera").toString()) + "'");
	            sbSQL.append("\n          and id_tipo_movto = 'E'");
	            sbSQL.append("\n          and id_forma_pago in (1,7)");
	            sbSQL.append("\n          and id_estatus_mov in ('I','R')");
	            sbSQL.append("\n          and m.b_entregado = 'N'");
	            sbSQL.append("\n          and m.fec_imprime between '" + funciones.ponerFechaSola(datos.get("fechaInf").toString())+ "' and '" + funciones.ponerFechaSola(datos.get("fechaSup").toString())+ "'");
	            sbSQL.append("\n          and m.id_caja between " + funciones.convertirCadenaInteger(datos.get("cajaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("cajaSup").toString()));
	            
	            if(datos.get("divisa") != null && !datos.get("divisa").equals(""))
	            {
	            	sbSQL.append("\n          and m.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "'");
	            }
	            
	            sbSQL.append("\n   ORDER BY id_chequera, desc_banco, nom_empresa, empresa");
	        }
        
	        if(ConstantesSet.gsDBM.equals("POSTGRESQL") || ConstantesSet.gsDBM.equals("DB2"))
	        {
	            sbSQL.append("\n      SELECT CASE WHEN m.folio_banco is null THEN '0' ");
	            sbSQL.append("\n                  ELSE m.folio_banco END as folio_banco,");
	            sbSQL.append("\n          m.importe as importe, m.beneficiario, m.concepto,");
	            sbSQL.append("\n          m.lote_entrada, m.id_divisa, m.id_banco,cb.desc_banco,");
	            sbSQL.append("\n          m.no_docto, m.fec_imprime, m.fec_valor,m.id_chequera,");
	            sbSQL.append("\n          m.id_caja, cc.desc_caja, e.no_empresa AS empresa,");
	            sbSQL.append("\n          e.nom_empresa,m.no_cheque,");
	            sbSQL.append("\n          CASE WHEN m.id_estatus_mov = 'I' then 'IMPRESO'");
	            sbSQL.append("\n               WHEN m.id_estatus_mov = 'R' then 'REIMPRESO'");
	            sbSQL.append("\n          END As id_estatus_mov");
	            sbSQL.append("\n      FROM movimiento m ");
	            sbSQL.append("\n          LEFT JOIN cat_banco cb ON(m.id_banco = cb.id_banco)");
	            sbSQL.append("\n          LEFT JOIN cat_caja cc ON (m.id_caja = cc.id_caja),empresa e");
	            sbSQL.append("\n      WHERE m.no_empresa = e.no_empresa");
	            sbSQL.append("\n          and m.no_empresa between " + funciones.convertirCadenaInteger(datos.get("empresaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("empresaSup").toString()));
	            sbSQL.append("\n          and m.id_banco between " + funciones.convertirCadenaInteger(datos.get("bancoInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("bancoSup").toString()));
	            sbSQL.append("\n          and m.id_chequera like '" + funciones.validarCadena(datos.get("chequera").toString()) + "'");
	            sbSQL.append("\n          and id_tipo_movto = 'E'");
	            sbSQL.append("\n          and id_forma_pago in (1,7)");
	            sbSQL.append("\n          and id_estatus_mov in ('I','R')");
	            sbSQL.append("\n          and m.b_entregado = 'N'");
	            sbSQL.append("\n          and m.fec_imprime between '" + funciones.ponerFechaSola(datos.get("fechaInf").toString())+ "' and '" + funciones.ponerFechaSola(datos.get("fechaSup").toString())+ "'");
	            sbSQL.append("\n          and m.id_caja between " + funciones.convertirCadenaInteger(datos.get("cajaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("cajaSup").toString()));
	            
	            if(datos.get("divisa") != null && !datos.get("divisa").equals(""))
	            {
	            	sbSQL.append("\n          and m.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "'");
	            }
	            
	            sbSQL.append("\n   ORDER BY id_chequera, desc_banco, nom_empresa, empresa");
	        }
	        logger.info("consultarReporteChequesPorEntregar");
	        
	        System.out.println("Query de cheques por entregar: " + sbSQL);
	        
	        
	        resultado = (List<Map<String, Object>>)jdbcTemplate.query(sbSQL.toString(), new RowMapper(){

				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("folio_banco", rs.getString("folio_banco"));
			            results.put("importe", rs.getString("importe"));
			            results.put("beneficiario", rs.getString("beneficiario"));
			            results.put("concepto", rs.getString("concepto"));
			            results.put("lote_entrada", rs.getString("lote_entrada"));
			            results.put("id_divisa", rs.getString("id_divisa"));
			            results.put("id_banco", rs.getString("id_banco"));
			            results.put("desc_banco", rs.getString("desc_banco"));
			            results.put("no_docto", rs.getString("no_docto"));
			            results.put("fec_imprime", rs.getString("fec_imprime"));
			            results.put("fec_valor", rs.getString("fec_valor"));
			            results.put("id_chequera", rs.getString("id_chequera"));
			            results.put("id_caja", rs.getString("id_caja"));
			            results.put("desc_caja", rs.getString("desc_caja"));
			            results.put("empresa", rs.getString("empresa"));
			            results.put("nom_empresa", rs.getString("nom_empresa"));
			            results.put("no_cheque", rs.getString("no_cheque"));
			            results.put("id_estatus_mov", rs.getString("id_estatus_mov"));
			            
			            return results;
					}
				});
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " " + e.toString()
					+ "P:Caja, C:CajaDaoImpl, M:consultarReporteChequesPorEntregar");
		}
		return resultado;
	}
	
	/**
	 * Metodo que consulta los registros del reporte de cheques entregados a la fecha de hoy
	 * FunSQLReporteCehequeentrg
	 * @param datos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> consultarReporteChequesEntregados(Map<String, Object> datos){
		List<Map<String, Object>> resultado = null;
		StringBuffer sbSQL = new StringBuffer();
		try{
			if(ConstantesSet.gsDBM.equals("ORACLE"))
	        {
	            sbSQL.append("\n      SELECT CASE WHEN m.folio_banco is null THEN '0' ");
	            sbSQL.append("\n                  ELSE m.folio_banco END as folio_banco,m.importe, m.beneficiario,");
	            sbSQL.append("\n          m.concepto, m.id_banco,cb.desc_banco,m.no_docto,m.fec_imprime,");
	            sbSQL.append("\n          m.id_chequera,m.id_caja, cc.desc_caja, m.fec_entregado,");
	            sbSQL.append("\n          e.no_empresa AS empresa,e.nom_empresa, m.no_cheque,");
	            sbSQL.append("\n          CASE WHEN m.id_estatus_mov ='I' then 'IMPRESO'");
	            sbSQL.append("\n               WHEN m.id_estatus_mov = 'R' then 'REIMPRESO'");
	            sbSQL.append("\n          END as id_estatus_mov,m.fec_valor");
	            sbSQL.append("\n      FROM movimiento m left outer join cat_banco cb on (m.id_banco = cb.id_banco)left outer join cat_caja cc on (m.id_caja = cc.id_caja),empresa e");
	            sbSQL.append("\n      WHERE  m.no_empresa=e.no_empresa");
	            sbSQL.append("\n          and m.no_empresa between " + funciones.convertirCadenaInteger(datos.get("empresaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("empresaSup").toString()));
	            sbSQL.append("\n          and m.id_banco between " + funciones.convertirCadenaInteger(datos.get("bancoInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("bancoSup").toString()));
	            sbSQL.append("\n          and m.id_chequera like '" + funciones.validarCadena(datos.get("chequera").toString()) + "'");
	            sbSQL.append("\n          and id_tipo_movto = 'E'");
	            sbSQL.append("\n          and id_forma_pago in (1,7)");
	            sbSQL.append("\n          and id_estatus_mov in ('I','R')");
	            sbSQL.append("\n          and m.b_entregado = 'S'");
	            sbSQL.append("\n          and m.fec_entregado = '" + funciones.ponerFechaSola(datos.get("fechaHoy").toString()) + "'");
	            sbSQL.append("\n          and m.id_caja between " + funciones.convertirCadenaInteger(datos.get("cajaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("cajaSup").toString()));
	            sbSQL.append("\n          and m.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "'");
	            sbSQL.append("\n      ORDER BY desc_banco,id_chequera,nom_empresa,empresa");
	        }
	    
			if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
	        {
	            sbSQL.append("\n      SELECT CASE WHEN m.folio_banco is null THEN '0' ");
	            sbSQL.append("\n                  ELSE m.folio_banco END as folio_banco,m.importe, m.beneficiario,");
	            sbSQL.append("\n          m.concepto,m.id_banco,cb.desc_banco,m.no_docto,m.fec_imprime,");
	            sbSQL.append("\n          m.id_chequera,m.id_caja,cc.desc_caja,convert(char, m.fec_entregado, 103) as fec_entregado,");
	            sbSQL.append("\n          e.no_empresa AS empresa,e.nom_empresa, m.no_cheque,");
	            sbSQL.append("\n          CASE WHEN m.id_estatus_mov ='I' then 'IMPRESO'");
	            sbSQL.append("\n               WHEN m.id_estatus_mov = 'R' then 'REIMPRESO'");
	            sbSQL.append("\n          END as id_estatus_mov,m.fec_valor");
	            sbSQL.append("\n       FROM movimiento m LEFT JOIN cat_banco cb ON m.id_banco=cb.id_banco left join cat_caja cc on m.id_caja=cc.id_caja join empresa e on m.no_empresa=e.no_empresa ");
//	            sbSQL.append("\n      WHERE m.id_banco *= cb.id_banco");
//	            sbSQL.append("\n          and m.id_caja *= cc.id_caja");
//	            sbSQL.append("\n          and m.no_empresa = e.no_empresa");
	            sbSQL.append("\n      WHERE ");
	            sbSQL.append("\n        m.no_empresa between " + funciones.convertirCadenaInteger(datos.get("empresaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("empresaSup").toString()));
	            sbSQL.append("\n          and m.id_banco between " + funciones.convertirCadenaInteger(datos.get("bancoInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("bancoSup").toString()));
	            sbSQL.append("\n          and m.id_chequera like '" + funciones.validarCadena(datos.get("chequera").toString()) + "'");
	            sbSQL.append("\n          and id_tipo_movto = 'E'");
	            sbSQL.append("\n          and id_forma_pago in (1,7)");
	            sbSQL.append("\n          and id_estatus_mov in ('I','R')");
	            sbSQL.append("\n          and m.b_entregado = 'S'");
	            sbSQL.append("\n          and m.fec_entregado = '" + funciones.ponerFechaSola(datos.get("fechaHoy").toString()) + "'");
	            sbSQL.append("\n          and m.id_caja between " + funciones.convertirCadenaInteger(datos.get("cajaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("cajaSup").toString()));
	            sbSQL.append("\n          and m.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "'");
	            sbSQL.append("\n      ORDER BY desc_banco,id_chequera,nom_empresa,empresa");
	        }
	    
			if(ConstantesSet.gsDBM.equals("POSTGRESQL") || ConstantesSet.gsDBM.equals("DB2"))
	        {
	            sbSQL.append("\n      SELECT CASE WHEN m.folio_banco is null THEN '0' ");
	            sbSQL.append("\n                  ELSE m.folio_banco END as folio_banco,m.importe,m.beneficiario,");
	            sbSQL.append("\n          m.concepto, m.id_banco,cb.desc_banco,m.no_docto,m.fec_imprime,");
	            sbSQL.append("\n          m.id_chequera,m.id_caja, cc.desc_caja, m.fec_entregado,");
	            sbSQL.append("\n          e.no_empresa AS empresa,e.nom_empresa, m.no_cheque,");
	            sbSQL.append("\n          CASE WHEN m.id_estatus_mov ='I' then 'IMPRESO'");
	            sbSQL.append("\n               WHEN m.id_estatus_mov = 'R' then 'REIMPRESO'");
	            sbSQL.append("\n          END as id_estatus_mov,m.fec_valor");
	            sbSQL.append("\n      FROM movimiento m");
	            sbSQL.append("\n          LEFT JOIN cat_banco cb ON(m.id_banco = cb.id_banco)");
	            sbSQL.append("\n          LEFT JOIN cat_caja cc ON(m.id_caja = cc.id_caja),empresa e ");
	            sbSQL.append("\n      WHERE m.no_empresa=e.no_empresa");
	            sbSQL.append("\n          and m.no_empresa between " + funciones.convertirCadenaInteger(datos.get("empresaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("empresaSup").toString()));
	            sbSQL.append("\n          and m.id_banco between " + funciones.convertirCadenaInteger(datos.get("bancoInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("bancoSup").toString()));
	            sbSQL.append("\n          and m.id_chequera like '" + funciones.validarCadena(datos.get("chequera").toString()) + "'");
	            sbSQL.append("\n          and id_tipo_movto = 'E'");
	            sbSQL.append("\n          and id_forma_pago in (1,7)");
	            sbSQL.append("\n          and id_estatus_mov in ('I','R')");
	            sbSQL.append("\n          and m.b_entregado = 'S'");
	            sbSQL.append("\n          and m.fec_entregado = '" + funciones.ponerFechaSola(datos.get("fechaHoy").toString()) + "'");
	            sbSQL.append("\n          and m.id_caja between " + funciones.convertirCadenaInteger(datos.get("cajaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("cajaSup").toString()));
	            sbSQL.append("\n          and m.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "'");
	            sbSQL.append("\n      ORDER BY desc_banco,id_chequera,nom_empresa,empresa");
	        }
			logger.info("consultarReporteChequesEntregadosPorBanco");
			
			System.out.println("Query de cheques entregados: " + sbSQL);
			
			resultado = (List<Map<String, Object>>)jdbcTemplate.query(sbSQL.toString(), new RowMapper(){

				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("folio_banco", rs.getString("folio_banco"));
			            results.put("importe", rs.getString("importe"));
			            results.put("beneficiario", rs.getString("beneficiario"));
			            results.put("concepto", rs.getString("concepto"));
			            results.put("id_banco", rs.getString("id_banco"));
			            results.put("desc_banco", rs.getString("desc_banco"));
			            results.put("no_docto", rs.getString("no_docto"));
			            results.put("fec_imprime", rs.getString("fec_imprime"));
			            results.put("id_chequera", rs.getString("id_chequera"));
			            results.put("id_caja", rs.getString("id_caja"));
			            results.put("desc_caja", rs.getString("desc_caja"));
			            results.put("fec_entregado", rs.getString("fec_entregado"));
			            results.put("empresa", rs.getString("empresa"));
			            results.put("nom_empresa", rs.getString("nom_empresa"));
			            results.put("no_cheque", rs.getString("no_cheque"));
			            results.put("id_estatus_mov", rs.getString("id_estatus_mov"));
			            results.put("fec_valor", rs.getString("fec_valor"));
			            
			            return results;
					}
				});
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " " + e.toString()
					+ "P:Caja, C:CajaDaoImpl, M:consultarReporteChequesEntregados");
		}
		return resultado;
	}
	
	/**
	 * Metodo que consulta los registros del reporte historico de cheques entregados
	 * FunSQLReporteCehequeHst
	 * @param datos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> consultarReporteHistoricoChequesEntregados(Map<String, Object> datos){
		List<Map<String, Object>> resultado = null;
		StringBuffer sbSQL = new StringBuffer();
		try{
			if(ConstantesSet.gsDBM.equals("ORACLE"))
	        {
	           sbSQL.append("\n     SELECT CASE WHEN m.folio_banco is null THEN '0' ");
	           sbSQL.append("\n                 ELSE m.folio_banco END as folio_banco,");
	           sbSQL.append("\n         m.importe as importe, m.beneficiario, m.concepto,m.id_banco,");
	           sbSQL.append("\n         cb.desc_banco, m.no_docto,m.fec_imprime,m.id_chequera,m.id_caja,");
	           sbSQL.append("\n         cc.desc_caja,e.no_empresa as empresa,e.nom_empresa,m.no_cheque,");
	           sbSQL.append("\n         CASE WHEN m.id_estatus_mov = 'I' then 'IMPRESO'");
	           sbSQL.append("\n              WHEN m.id_estatus_mov = 'R' then 'REIMPRESO'");
	           sbSQL.append("\n         END as id_estatus_mov, m.fec_entregado");
	           sbSQL.append("\n     FROM movimiento m left outer join cat_banco cb on (m.id_banco = cb.id_banco),left outer join cat_caja cc on (m.id_caja = cc.id_caja), empresa e");
	           sbSQL.append("\n     WHERE m.no_empresa=e.no_empresa");
	           sbSQL.append("\n         and m.no_empresa  between " + funciones.convertirCadenaInteger(datos.get("empresaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("empresaSup").toString()));
	           sbSQL.append("\n         and m.id_banco between " + funciones.convertirCadenaInteger(datos.get("bancoInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("bancoSup").toString()));
	           sbSQL.append("\n         and m.id_chequera like '" + funciones.validarCadena(datos.get("chequera").toString()) + "'");
	           sbSQL.append("\n         and id_tipo_movto = 'E'");
	           sbSQL.append("\n         and id_forma_pago in (1,7)");
	           sbSQL.append("\n         and id_estatus_mov in ('I','R')");
	           sbSQL.append("\n         and m.fec_entregado between '" + funciones.ponerFechaSola(datos.get("fechaInf").toString())+ "' and '" + funciones.ponerFechaSola(datos.get("fechaSup").toString())+ "'");
	           sbSQL.append("\n         and m.b_entregado = 'S'");
	           sbSQL.append("\n         and m.id_caja between " + funciones.convertirCadenaInteger(datos.get("cajaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("cajaSup").toString()));
	           sbSQL.append("\n         and m.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "'");
	           sbSQL.append("\n     UNION ALL ");
	           sbSQL.append("\n     SELECT CASE WHEN m.folio_banco is null THEN '0' ");
	           sbSQL.append("\n                 ELSE m.folio_banco END as folio_banco,");
	           sbSQL.append("\n         m.importe as importe, m.beneficiario, m.concepto,m.id_banco,");
	           sbSQL.append("\n         cb.desc_banco, m.no_docto,m.fec_imprime,m.id_chequera,m.id_caja,");
	           sbSQL.append("\n         cc.desc_caja,e.no_empresa as empresa,e.nom_empresa,m.no_cheque,");
	           sbSQL.append("\n         CASE WHEN m.id_estatus_mov = 'I' then 'IMPRESO'");
	           sbSQL.append("\n              WHEN m.id_estatus_mov = 'R' then 'REIMPRESO'");
	           sbSQL.append("\n         END as id_estatus_mov, m.fec_entregado");
	           sbSQL.append("\n     FROM hist_movimiento m left outer join cat_banco cb on (m.id_banco = cb.id_banco),left outer join cat_caja cc on (m.id_caja = cc.id_caja) ,empresa e");
	           sbSQL.append("\n     WHERE m.no_empresa=e.no_empresa");
	           sbSQL.append("\n         and m.no_empresa  between " + funciones.convertirCadenaInteger(datos.get("empresaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("empresaSup").toString()));
	           sbSQL.append("\n         and m.id_banco between " + funciones.convertirCadenaInteger(datos.get("bancoInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("bancoSup").toString()));
	           sbSQL.append("\n         and m.id_chequera like '" + funciones.validarCadena(datos.get("chequera").toString()) + "'");
	           sbSQL.append("\n         and id_tipo_movto = 'E'");
	           sbSQL.append("\n         and id_forma_pago in (1,7)");
	           sbSQL.append("\n         and id_estatus_mov in ('I','R')");
	           sbSQL.append("\n         and m.fec_entregado between '" + funciones.ponerFechaSola(datos.get("fechaInf").toString())+ "' and '" + funciones.ponerFechaSola(datos.get("fechaSup").toString())+ "'");
	           sbSQL.append("\n         and m.b_entregado = 'S'");
	           sbSQL.append("\n         and m.id_caja between " + funciones.convertirCadenaInteger(datos.get("cajaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("cajaSup").toString()));
	           sbSQL.append("\n         and m.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "'");
	           sbSQL.append("\n     ORDER BY desc_banco,id_chequera,nom_empresa,empresa");
	        }
		       
			if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
	        {
	           sbSQL.append("\n     SELECT CASE WHEN m.folio_banco is null THEN '0' ");
	           sbSQL.append("\n                 ELSE m.folio_banco END as folio_banco,");
	           sbSQL.append("\n         m.importe as importe, m.beneficiario, m.concepto,m.id_banco,");
	           sbSQL.append("\n         cb.desc_banco, m.no_docto,m.fec_imprime,m.id_chequera,m.id_caja,");
	           sbSQL.append("\n         cc.desc_caja,e.no_empresa as empresa,e.nom_empresa,m.no_cheque,");
	           sbSQL.append("\n         CASE WHEN m.id_estatus_mov = 'I' then 'IMPRESO'");
	           sbSQL.append("\n              WHEN m.id_estatus_mov = 'R' then 'REIMPRESO'");
	           sbSQL.append("\n         END as id_estatus_mov, convert(char,m.fec_entregado,103) as fec_entregado");
	           sbSQL.append("\n     FROM movimiento m left join cat_banco cb on m.id_banco=cb.id_banco left join cat_caja cc on m.id_caja=cc.id_caja join  empresa e on m.no_empresa=e.no_empresa");
//	           sbSQL.append("\n     WHERE m.id_banco *= cb.id_banco");
//	           sbSQL.append("\n         and m.no_empresa=e.no_empresa");
//	           sbSQL.append("\n         and m.id_caja *= cc.id_caja");
	           sbSQL.append("\n         where");
	           sbSQL.append("\n         and m.no_empresa  between " + funciones.convertirCadenaInteger(datos.get("empresaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("empresaSup").toString()));
	           sbSQL.append("\n         and m.id_banco between " + funciones.convertirCadenaInteger(datos.get("bancoInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("bancoSup").toString()));
	           sbSQL.append("\n         and m.id_chequera like '" + funciones.validarCadena(datos.get("chequera").toString()) + "'");
	           sbSQL.append("\n         and id_tipo_movto = 'E'");
	           sbSQL.append("\n         and id_forma_pago in (1,7)");
	           sbSQL.append("\n         and id_estatus_mov in ('I','R')");
	           sbSQL.append("\n         and m.fec_entregado between '" + funciones.ponerFechaSola(datos.get("fechaInf").toString())+ "' and '" + funciones.ponerFechaSola(datos.get("fechaSup").toString())+ "'");
	           sbSQL.append("\n         and m.b_entregado = 'S'");
	           sbSQL.append("\n         and m.id_caja between " + funciones.convertirCadenaInteger(datos.get("cajaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("cajaSup").toString()));
	           sbSQL.append("\n         and m.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "'");
	           sbSQL.append("\n     UNION ALL");
	           sbSQL.append("\n     SELECT CASE WHEN m.folio_banco is null THEN '0' ");
	           sbSQL.append("\n                 ELSE m.folio_banco END as folio_banco,");
	           sbSQL.append("\n         m.importe as importe, m.beneficiario, m.concepto,m.id_banco,");
	           sbSQL.append("\n         cb.desc_banco, m.no_docto,m.fec_imprime,m.id_chequera,m.id_caja,");
	           sbSQL.append("\n         cc.desc_caja,e.no_empresa as empresa,e.nom_empresa, m.no_cheque,");
	           sbSQL.append("\n         CASE WHEN m.id_estatus_mov = 'I' then 'IMPRESO'");
	           sbSQL.append("\n              WHEN m.id_estatus_mov = 'R' then 'REIMPRESO'");
	           sbSQL.append("\n         END as id_estatus_mov, convert(char,m.fec_entregado,103) as fec_entregado");
	           sbSQL.append("\n     FROM hist_movimiento m, cat_banco cb, cat_caja cc ,empresa e");
	           sbSQL.append("\n     WHERE m.id_banco *= cb.id_banco");
	           sbSQL.append("\n         and m.no_empresa=e.no_empresa");
	           sbSQL.append("\n         and m.id_caja *= cc.id_caja");
	           sbSQL.append("\n         and m.no_empresa  between " + funciones.convertirCadenaInteger(datos.get("empresaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("empresaSup").toString()));
	           sbSQL.append("\n         and m.id_banco between " + funciones.convertirCadenaInteger(datos.get("bancoInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("bancoSup").toString()));
	           sbSQL.append("\n         and m.id_chequera like '" + funciones.validarCadena(datos.get("chequera").toString()) + "'");
	           sbSQL.append("\n         and id_tipo_movto = 'E'");
	           sbSQL.append("\n         and id_forma_pago in (1,7)");
	           sbSQL.append("\n         and id_estatus_mov in ('I','R')");
	           sbSQL.append("\n         and m.fec_entregado between '" + funciones.ponerFechaSola(datos.get("fechaInf").toString())+ "' and '" + funciones.ponerFechaSola(datos.get("fechaSup").toString())+ "'");
	           sbSQL.append("\n         and m.b_entregado = 'S'");
	           sbSQL.append("\n         and m.id_caja between " + funciones.convertirCadenaInteger(datos.get("cajaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("cajaSup").toString()));
	           sbSQL.append("\n         and m.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "'");
	           sbSQL.append("\n     ORDER BY desc_banco,id_chequera,nom_empresa,empresa");

	        }
		       
			if(ConstantesSet.gsDBM.equals("POSTGRESQL") || ConstantesSet.gsDBM.equals("DB2"))
	        {
	           sbSQL.append("\n     SELECT CASE WHEN m.folio_banco is null THEN '0' ");
	           sbSQL.append("\n                 ELSE m.folio_banco END as folio_banco,");
	           sbSQL.append("\n         m.importe as importe, m.beneficiario, m.concepto,m.id_banco,");
	           sbSQL.append("\n         cb.desc_banco, m.no_docto,m.fec_imprime,m.id_chequera,m.id_caja,");
	           sbSQL.append("\n         cc.desc_caja,e.no_empresa as empresa,e.nom_empresa,m.no_cheque,");
	           sbSQL.append("\n         CASE WHEN m.id_estatus_mov = 'I' then 'IMPRESO'");
	           sbSQL.append("\n              WHEN m.id_estatus_mov = 'R' then 'REIMPRESO'");
	           sbSQL.append("\n         END as id_estatus_mov, m.fec_entregado");
	           sbSQL.append("\n     FROM movimiento m");
	           sbSQL.append("\n         LEFT JOIN cat_banco cb ON(m.id_banco = cb.id_banco)");
	           sbSQL.append("\n         LEFT JOIN cat_caja cc ON(m.id_caja = cc.id_caja),empresa e  ");
	           sbSQL.append("\n     WHERE m.no_empresa=e.no_empresa");
	           sbSQL.append("\n         and m.no_empresa  between " + funciones.convertirCadenaInteger(datos.get("empresaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("empresaSup").toString()));
	           sbSQL.append("\n         and m.id_banco between " + funciones.convertirCadenaInteger(datos.get("bancoInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("bancoSup").toString()));
	           sbSQL.append("\n         and m.id_chequera like '" + funciones.validarCadena(datos.get("chequera").toString()) + "'");
	           sbSQL.append("\n         and id_tipo_movto = 'E'");
	           sbSQL.append("\n         and id_forma_pago in (1,7)");
	           sbSQL.append("\n         and id_estatus_mov in ('I','R')");
	           sbSQL.append("\n         and m.fec_entregado between '" + funciones.ponerFechaSola(datos.get("fechaInf").toString())+ "' and '" + funciones.ponerFechaSola(datos.get("fechaSup").toString())+ "'");
	           sbSQL.append("\n         and m.b_entregado = 'S'");
	           sbSQL.append("\n         and m.id_caja between " + funciones.convertirCadenaInteger(datos.get("cajaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("cajaSup").toString()));
	           sbSQL.append("\n         and m.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "'");
	           sbSQL.append("\n     UNION ALL ");
	           sbSQL.append("\n     SELECT CASE WHEN m.folio_banco is null");
	           sbSQL.append("\n                 THEN '0' else m.folio_banco END as folio_banco,");
	           sbSQL.append("\n         m.importe as importe, m.beneficiario, m.concepto,m.id_banco,");
	           sbSQL.append("\n         cb.desc_banco, m.no_docto,m.fec_imprime,m.id_chequera,m.id_caja,");
	           sbSQL.append("\n         cc.desc_caja,e.no_empresa as empresa,e.nom_empresa, m.no_cheque,");
	           sbSQL.append("\n         CASE WHEN m.id_estatus_mov = 'I' then 'IMPRESO'");
	           sbSQL.append("\n              WHEN m.id_estatus_mov = 'R' then 'REIMPRESO'");
	           sbSQL.append("\n         END as id_estatus_mov, m.fec_entregado");
	           sbSQL.append("\n     FROM hist_movimiento m");
	           sbSQL.append("\n         LEFT JOIN cat_banco cb ON(m.id_banco = cb.id_banco)");
	           sbSQL.append("\n         LEFT JOIN cat_caja cc ON(m.id_caja = cc.id_caja),empresa e  ");
	           sbSQL.append("\n     WHERE m.no_empresa=e.no_empresa");
	           sbSQL.append("\n         and m.no_empresa  between " + funciones.convertirCadenaInteger(datos.get("empresaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("empresaSup").toString()));
	           sbSQL.append("\n         and m.id_banco between " + funciones.convertirCadenaInteger(datos.get("bancoInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("bancoSup").toString()));
	           sbSQL.append("\n         and m.id_chequera like '" + funciones.validarCadena(datos.get("chequera").toString()) + "'");
	           sbSQL.append("\n         and id_tipo_movto = 'E'");
	           sbSQL.append("\n         and id_forma_pago in (1,7)");
	           sbSQL.append("\n         and id_estatus_mov in ('I','R')");
	           sbSQL.append("\n         and m.fec_entregado between '" + funciones.ponerFechaSola(datos.get("fechaInf").toString())+ "' and '" + funciones.ponerFechaSola(datos.get("fechaSup").toString())+ "'");
	           sbSQL.append("\n         and m.b_entregado = 'S'");
	           sbSQL.append("\n         and m.id_caja between " + funciones.convertirCadenaInteger(datos.get("cajaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("cajaSup").toString()));
	           sbSQL.append("\n         and m.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "'");
	           sbSQL.append("\n     ORDER BY desc_banco,id_chequera,nom_empresa,empresa");

	        }
			logger.info("consultarReporteHistoricoChequesEntregadosPorBanco");
			
			System.out.println("Query historico de cheques entregados: " + sbSQL);
			
			resultado = (List<Map<String, Object>>)jdbcTemplate.query(sbSQL.toString(), new RowMapper(){

				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("folio_banco", rs.getString("folio_banco"));
			            results.put("importe", rs.getString("importe"));
			            results.put("beneficiario", rs.getString("beneficiario"));
			            results.put("concepto", rs.getString("concepto"));
			            results.put("id_banco", rs.getString("id_banco"));
			            results.put("desc_banco", rs.getString("desc_banco"));
			            results.put("no_docto", rs.getString("no_docto"));
			            results.put("fec_imprime", rs.getString("fec_imprime"));
			            results.put("id_chequera", rs.getString("id_chequera"));
			            results.put("id_caja", rs.getString("id_caja"));
			            results.put("desc_caja", rs.getString("desc_caja"));
			            results.put("empresa", rs.getString("empresa"));
			            results.put("nom_empresa", rs.getString("nom_empresa"));
			            results.put("no_cheque", rs.getString("no_cheque"));
			            results.put("id_estatus_mov", rs.getString("id_estatus_mov"));
			            results.put("fec_entregado", rs.getString("fec_entregado"));
			            
			            return results;
					}
				});
		    
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " " + e.toString()
					+ "P:Caja, C:CajaDaoImpl, M:consultarReporteHistoricoChequesEntregados");
		}
		return resultado;
	}
	
	/**
	 * Metodo que consulta los registros del reporte de cheques entregados ordenados por caja
	 * FunSQLReporteentregCaja
	 * @param datos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> consultarReporteChequesEntregadosPorCaja(Map<String, Object> datos){
		List<Map<String, Object>> resultado = null;
		StringBuffer sbSQL = new StringBuffer();
		try{
			if(ConstantesSet.gsDBM.equals("ORACLE"))
	        {
	            sbSQL.append("\n      SELECT CASE WHEN m.folio_banco is null THEN '0' ");
	            sbSQL.append("\n                  ELSE m.folio_banco END as folio_banco,");
	            sbSQL.append("\n          importe, m.beneficiario, m.concepto,m.id_banco,cb.desc_banco,");
	            sbSQL.append("\n          m.no_docto,m.fec_imprime,m.id_chequera,m.id_caja,cc.desc_caja,");
	            sbSQL.append("\n          m.fec_entregado,e.no_empresa as empresa,e.nom_empresa,");
	            sbSQL.append("\n          lote_entrada, m.no_cheque,");
	            sbSQL.append("\n          CASE WHEN m.id_estatus_mov = 'I' then 'IMPRESO'");
	            sbSQL.append("\n               WHEN m.id_estatus_mov = 'R' then 'REIMPRESO'");
	            sbSQL.append("\n          END as id_estatus_mov, m.fec_valor , m.id_divisa");
	            sbSQL.append("\n      FROM movimiento m left outer join cat_banco cb on (m.id_banco = cb.id_banco),left outer join cat_caja cc on (m.id_caja = cc.id_caja),empresa e");
	            sbSQL.append("\n      WHERE m.no_empresa=e.no_empresa");
	            sbSQL.append("\n          and m.no_empresa between " + funciones.convertirCadenaInteger(datos.get("empresaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("empresaSup").toString()));
	            sbSQL.append("\n          and m.id_banco between " + funciones.convertirCadenaInteger(datos.get("bancoInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("bancoSup").toString()));
	            sbSQL.append("\n          and m.id_chequera like '" + funciones.validarCadena(datos.get("chequera").toString()) + "'");
	            sbSQL.append("\n          and id_tipo_movto = 'E' and id_forma_pago in )(1,7)");
	            sbSQL.append("\n          and id_estatus_mov in ('I','R')");
	            sbSQL.append("\n          and m.b_entregado = 'S'");
	            sbSQL.append("\n          and m.fec_entregado = '" + funciones.ponerFechaSola(datos.get("fechaHoy").toString()) + "'");
	            sbSQL.append("\n          and m.id_caja between " + funciones.convertirCadenaInteger(datos.get("cajaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("cajaSup").toString()));
	            sbSQL.append("\n          and m.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "'");
	            sbSQL.append("\n      ORDER BY nom_empresa,desc_caja,id_divisa,id_chequera,empresa");
	        }
		    
			if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
	        {
	            sbSQL.append("\n      SELECT CASE WHEN m.folio_banco is null THEN '0' ");
	            sbSQL.append("\n                  ELSE m.folio_banco END as folio_banco,");
	            sbSQL.append("\n          importe, m.beneficiario, m.concepto,m.id_banco,cb.desc_banco,");
	            sbSQL.append("\n          m.no_docto,m.fec_imprime,m.id_chequera,m.id_caja,cc.desc_caja,");
	            sbSQL.append("\n          convert(char, m.fec_entregado, 103) as fec_entregado,e.no_empresa as empresa,e.nom_empresa,");
	            sbSQL.append("\n          lote_entrada,m.no_cheque,");
	            sbSQL.append("\n          CASE WHEN m.id_estatus_mov = 'I' then 'IMPRESO'");
	            sbSQL.append("\n               WHEN m.id_estatus_mov = 'R' then 'REIMPRESO'");
	            sbSQL.append("\n          END as id_estatus_mov, m.fec_valor , m.id_divisa");
	            sbSQL.append("\n     FROM movimiento m left join  cat_banco cb on m.id_banco=cb.id_banco left join cat_caja cc on m.id_caja=cc.id_caja join empresa e on m.no_empresa=e.no_empresa ");
//	            sbSQL.append("\n      WHERE m.id_banco *= cb.id_banco");
//	            sbSQL.append("\n          and m.id_caja *= cc.id_caja");
//	            sbSQL.append("\n          and m.no_empresa=e.no_empresa");
	            sbSQL.append("\n          where ");
	            sbSQL.append("\n           m.no_empresa between " + funciones.convertirCadenaInteger(datos.get("empresaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("empresaSup").toString()));
	            sbSQL.append("\n          and m.id_banco between " + funciones.convertirCadenaInteger(datos.get("bancoInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("bancoSup").toString()));
	            sbSQL.append("\n          and m.id_chequera like '" + funciones.validarCadena(datos.get("chequera").toString()) + "'");
	            sbSQL.append("\n          and id_tipo_movto = 'E' and id_forma_pago in (1,7)");
	            sbSQL.append("\n          and id_estatus_mov in ('I','R')");
	            sbSQL.append("\n          and m.b_entregado = 'S'");
	            sbSQL.append("\n          and m.fec_entregado = '" + funciones.ponerFechaSola(datos.get("fechaHoy").toString()) + "'");
	            sbSQL.append("\n          and m.id_caja between " + funciones.convertirCadenaInteger(datos.get("cajaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("cajaSup").toString()));
	            sbSQL.append("\n          and m.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "'");
	            sbSQL.append("\n      ORDER BY nom_empresa,desc_caja,id_divisa,id_chequera,empresa");
	        }

		        
			if(ConstantesSet.gsDBM.equals("POSTGRESQL") || ConstantesSet.gsDBM.equals("DB2"))
	        {
	            sbSQL.append("\n      SELECT CASE WHEN m.folio_banco is null THEN '0' ");
	            sbSQL.append("\n                  ELSE m.folio_banco END as folio_banco,");
	            sbSQL.append("\n          importe, m.beneficiario, m.concepto,m.id_banco,cb.desc_banco,");
	            sbSQL.append("\n          m.no_docto,m.fec_imprime,m.id_chequera,m.id_caja,cc.desc_caja,");
	            sbSQL.append("\n          m.fec_entregado,e.no_empresa as empresa,e.nom_empresa,");
	            sbSQL.append("\n          lote_entrada, m.no_cheque,");
	            sbSQL.append("\n          CASE WHEN m.id_estatus_mov = 'I' then 'IMPRESO'");
	            sbSQL.append("\n               WHEN m.id_estatus_mov = 'R' then 'REIMPRESO'");
	            sbSQL.append("\n          END as id_estatus_mov, m.fec_valor , m.id_divisa");
	            sbSQL.append("\n      FROM movimiento m ");
	            sbSQL.append("\n          LEFT JOIN cat_banco cb ON(m.id_banco = cb.id_banco)");
	            sbSQL.append("\n          LEFT JOIN cat_caja cc ON(m.id_caja = cc.id_caja),empresa e ");
	            sbSQL.append("\n      WHERE m.no_empresa=e.no_empresa");
	            sbSQL.append("\n          and m.no_empresa between " + funciones.convertirCadenaInteger(datos.get("empresaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("empresaSup").toString()));
	            sbSQL.append("\n          and m.id_banco between " + funciones.convertirCadenaInteger(datos.get("bancoInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("bancoSup").toString()));
	            sbSQL.append("\n          and m.id_chequera like '" + funciones.validarCadena(datos.get("chequera").toString()) + "'");
	            sbSQL.append("\n          and id_tipo_movto = 'E' and id_forma_pago in (1,7)");
	            sbSQL.append("\n          and id_estatus_mov in ('I','R')");
	            sbSQL.append("\n          and m.b_entregado = 'S'");
	            sbSQL.append("\n          and m.fec_entregado = '" + funciones.ponerFechaSola(datos.get("fechaHoy").toString()) + "'");
	            sbSQL.append("\n          and m.id_caja between " + funciones.convertirCadenaInteger(datos.get("cajaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("cajaSup").toString()));
	            sbSQL.append("\n          and m.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "'");
	            sbSQL.append("\n      ORDER BY nom_empresa,desc_caja,id_divisa,id_chequera,empresa");
	        }
			logger.info("consultarReporteChequesEntregadosPorCaja");
			
			System.out.println("Query de cheques entregados por caja: " + sbSQL);
			
			resultado = (List<Map<String, Object>>)jdbcTemplate.query(sbSQL.toString(), new RowMapper(){

				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("folio_banco", rs.getString("folio_banco"));
			            results.put("importe", rs.getString("importe"));
			            results.put("beneficiario", rs.getString("beneficiario"));
			            results.put("concepto", rs.getString("concepto"));
			            results.put("id_banco", rs.getString("id_banco"));
			            results.put("no_docto", rs.getString("no_docto"));
			            results.put("fec_imprime", rs.getString("fec_imprime"));
			            results.put("id_chequera", rs.getString("id_chequera"));
			            results.put("id_caja", rs.getString("id_caja"));
			            results.put("desc_caja", rs.getString("desc_caja"));
			            results.put("fec_entregado", rs.getString("fec_entregado"));
			            results.put("empresa", rs.getString("empresa"));
			            results.put("nom_empresa", rs.getString("nom_empresa"));
			            results.put("lote_entrada", rs.getString("lote_entrada"));
			            results.put("no_cheque", rs.getString("no_cheque"));
			            results.put("id_estatus_mov", rs.getString("id_estatus_mov"));
			            results.put("fec_valor", rs.getString("fec_valor"));
			            results.put("id_divisa", rs.getString("id_divisa"));
			            
			            return results;
					}
				});
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " " + e.toString()
					+ "P:Caja, C:CajaDaoImpl, M:consultarReporteChequesEntregadosPorCaja");
		}
		return resultado;
	}
	
	/**
	 * Metodo que consulta los registros del reporte historico de cheques entregados ordenados por caja
	 * FunSQLReporteentregCajahst
	 * @param datos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> consultarReporteHistoricoChequesEntregadosPorCaja(Map<String, Object> datos){
		List<Map<String, Object>> resultado = null;
		StringBuffer sbSQL = new StringBuffer();
		try{
			if(ConstantesSet.gsDBM.equals("ORACLE"))
	        {
	            sbSQL.append("\n      SELECT CASE WHEN m.folio_banco is null THEN '0' ");
	            sbSQL.append("\n                  ELSE m.folio_banco END as folio_banco,");
	            sbSQL.append("\n          m.importe, m.beneficiario, m.concepto, m.id_banco,cb.desc_banco,");
	            sbSQL.append("\n          m.no_docto, m.fec_imprime, m.id_chequera,m.id_caja,cc.desc_caja,");
	            sbSQL.append("\n          e.no_empresa as empresa,e.nom_empresa,lote_entrada, m.no_cheque,");
	            sbSQL.append("\n          CASE WHEN m.id_estatus_mov = 'I' THEN 'IMPRESO'");
	            sbSQL.append("\n               WHEN hen m.id_estatus_mov = 'R' THEN 'REIMPRESO'");
	            sbSQL.append("\n          END as id_estatus_mov,m.fec_entregado,m.id_divisa");
	            sbSQL.append("\n      FROM movimiento m left outer join cat_banco cb on (m.id_banco = cb.id_banco) left outer join cat_caja cc on (m.id_caja = cc.id_caja), empresa e");
	            sbSQL.append("\n      WHERE m.no_empresa = e.no_empresa");
	            sbSQL.append("\n          and m.no_empresa  between " + funciones.convertirCadenaInteger(datos.get("empresaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("empresaSup").toString()));
	            sbSQL.append("\n          and m.id_banco between " + funciones.convertirCadenaInteger(datos.get("bancoInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("bancoSup").toString()));
	            sbSQL.append("\n          and m.id_chequera like '" + funciones.validarCadena(datos.get("chequera").toString()) + "'");
	            sbSQL.append("\n          and id_tipo_movto = 'E' and id_forma_pago in (1,7)");
	            sbSQL.append("\n          and id_estatus_mov in ('I','R')");
	            sbSQL.append("\n          and m.fec_entregado between '" + funciones.ponerFechaSola(datos.get("fechaInf").toString())+ "' and '" + funciones.ponerFechaSola(datos.get("fechaSup").toString())+ "'");
	            sbSQL.append("\n          and m.b_entregado = 'S'");
	            sbSQL.append("\n          and m.id_caja between " + funciones.convertirCadenaInteger(datos.get("cajaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("cajaSup").toString()));
	            sbSQL.append("\n          and m.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "'");
	            sbSQL.append("\n      UNION ALL ");
	            sbSQL.append("\n      SELECT CASE WHEN m.folio_banco is null THEN '0' ");
	            sbSQL.append("\n                  ELSE m.folio_banco END as folio_banco,");
	            sbSQL.append("\n          m.importe, m.beneficiario, m.concepto, m.id_banco,cb.desc_banco,");
	            sbSQL.append("\n          m.no_docto,m.fec_imprime,m.id_chequera,m.id_caja,cc.desc_caja,");
	            sbSQL.append("\n          e.no_empresa as empresa,e.nom_empresa, lote_entrada, m.no_cheque,");
	            sbSQL.append("\n          CASE WHEN m.id_estatus_mov = 'I' THEN 'IMPRESO'");
	            sbSQL.append("\n               WHEN m.id_estatus_mov = 'R' THEN 'REIMPRESO'");
	            sbSQL.append("\n          END as id_estatus_mov,m.fec_entregado,m.id_divisa");
	            sbSQL.append("\n      FROM hist_movimiento m left outer join cat_banco cb on (m.id_banco = cb.id_banco) left outer join cat_caja cc on (m.id_caja = cc.id_caja),empresa e");
	            sbSQL.append("\n      WHERE m.no_empresa=e.no_empresa");
	            sbSQL.append("\n          and m.no_empresa  between " + funciones.convertirCadenaInteger(datos.get("empresaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("empresaSup").toString()));
	            sbSQL.append("\n          and m.id_banco between " + funciones.convertirCadenaInteger(datos.get("bancoInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("bancoSup").toString()));
	            sbSQL.append("\n          and m.id_chequera like '" + funciones.validarCadena(datos.get("chequera").toString()) + "'");
	            sbSQL.append("\n          and id_tipo_movto = 'E' and id_forma_pago in (1,7)");
	            sbSQL.append("\n          and id_estatus_mov in ('I','R')");
	            sbSQL.append("\n          and m.fec_entregado between '" + funciones.ponerFechaSola(datos.get("fechaInf").toString())+ "' and '" + funciones.ponerFechaSola(datos.get("fechaSup").toString())+ "'");
	            sbSQL.append("\n          and m.b_entregado = 'S'");
	            sbSQL.append("\n          and m.id_caja between " + funciones.convertirCadenaInteger(datos.get("cajaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("cajaSup").toString()));
	            sbSQL.append("\n          and m.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "'");
	            sbSQL.append("\n      ORDER BY nom_empresa,desc_caja,id_divisa,id_chequera,empresa");
		    
	        }

			if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
	        {
	            sbSQL.append("\n      SELECT CASE WHEN m.folio_banco is null THEN '0' ");
	            sbSQL.append("\n                  ELSE m.folio_banco END as folio_banco,");
	            sbSQL.append("\n          m.importe, m.beneficiario, m.concepto, m.id_banco,cb.desc_banco,");
	            sbSQL.append("\n          m.no_docto, m.fec_imprime, m.id_chequera,m.id_caja,cc.desc_caja,");
	            sbSQL.append("\n          e.no_empresa as empresa,e.nom_empresa,lote_entrada, m.no_cheque,");
	            sbSQL.append("\n          CASE WHEN m.id_estatus_mov = 'I' THEN 'IMPRESO'");
	            sbSQL.append("\n               WHEN m.id_estatus_mov = 'R' THEN 'REIMPRESO'");
	            sbSQL.append("\n          END as id_estatus_mov,convert(char,m.fec_entregado,103) as fec_entregado,m.id_divisa");
	            sbSQL.append("\n       FROM movimiento m left join cat_banco cb on m.id_banco=cb.id_banco left join  cat_caja cc on m.id_caja=cc.id_caja join empresa e on m.no_empresa=e.no_empresa ");
//	            sbSQL.append("\n      WHERE m.id_banco *= cb.id_banco");
//	            sbSQL.append("\n          and m.no_empresa = e.no_empresa");
//	            sbSQL.append("\n          and m.id_caja *= cc.id_caja");
	            sbSQL.append("\n          where");
	            sbSQL.append("\n           m.no_empresa  between " + funciones.convertirCadenaInteger(datos.get("empresaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("empresaSup").toString()));
	            sbSQL.append("\n          and m.id_banco between " + funciones.convertirCadenaInteger(datos.get("bancoInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("bancoSup").toString()));
	            sbSQL.append("\n          and m.id_chequera like '" + funciones.validarCadena(datos.get("chequera").toString()) + "'");
	            sbSQL.append("\n          and id_tipo_movto = 'E' and id_forma_pago in (1,7)");
	            sbSQL.append("\n          and id_estatus_mov in ('I','R')");
	            sbSQL.append("\n          and m.fec_entregado between '" + funciones.ponerFechaSola(datos.get("fechaInf").toString())+ "' and '" + funciones.ponerFechaSola(datos.get("fechaSup").toString())+ "'");
	            sbSQL.append("\n          and m.b_entregado = 'S'");
	            sbSQL.append("\n          and m.id_caja between " + funciones.convertirCadenaInteger(datos.get("cajaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("cajaSup").toString()));
	            sbSQL.append("\n          and m.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "'");
	            sbSQL.append("\n      UNION ALL ");
	            sbSQL.append("\n      SELECT CASE WHEN m.folio_banco is null THEN '0' ");
	            sbSQL.append("\n                  ELSE m.folio_banco END as folio_banco,");
	            sbSQL.append("\n          m.importe, m.beneficiario, m.concepto, m.id_banco,cb.desc_banco,");
	            sbSQL.append("\n          m.no_docto,m.fec_imprime,m.id_chequera,m.id_caja,cc.desc_caja,");
	            sbSQL.append("\n          e.no_empresa as empresa,e.nom_empresa, lote_entrada,m.no_cheque,");
	            sbSQL.append("\n          CASE WHEN m.id_estatus_mov = 'I' THEN 'IMPRESO'");
	            sbSQL.append("\n               WHEN m.id_estatus_mov = 'R' then 'REIMPRESO'");
	            sbSQL.append("\n          END as id_estatus_mov,convert(char,m.fec_entregado,103) as fec_entregado,m.id_divisa");
	            sbSQL.append("\n      FROM hist_movimiento m, cat_banco cb, cat_caja cc ,empresa e");
	            sbSQL.append("\n      WHERE m.id_banco *= cb.id_banco");
	            sbSQL.append("\n          and m.no_empresa=e.no_empresa");
	            sbSQL.append("\n          and m.id_caja *= cc.id_caja");
	            sbSQL.append("\n          and m.no_empresa between " + funciones.convertirCadenaInteger(datos.get("empresaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("empresaSup").toString()));
	            sbSQL.append("\n          and m.id_banco between " + funciones.convertirCadenaInteger(datos.get("bancoInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("bancoSup").toString()));
	            sbSQL.append("\n          and m.id_chequera like '" + funciones.validarCadena(datos.get("chequera").toString()) + "'");
	            sbSQL.append("\n          and id_tipo_movto = 'E' and id_forma_pago in (1,7)");
	            sbSQL.append("\n          and id_estatus_mov in ('I','R')");
	            sbSQL.append("\n          and m.fec_entregado between '" + funciones.ponerFechaSola(datos.get("fechaInf").toString())+ "' and '" + funciones.ponerFechaSola(datos.get("fechaSup").toString())+ "'");
	            sbSQL.append("\n          and m.b_entregado = 'S'");
	            sbSQL.append("\n          and m.id_caja between " + funciones.convertirCadenaInteger(datos.get("cajaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("cajaSup").toString()));
	            sbSQL.append("\n          and m.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "'");
	            sbSQL.append("\n      ORDER BY desc_caja,id_chequera,nom_empresa,id_divisa,empresa");
	        }
	        
			if(ConstantesSet.gsDBM.equals("POSTGRESQL") || ConstantesSet.gsDBM.equals("DB2"))
	        {
	            sbSQL.append("\n      SELECT CASE WHEN m.folio_banco is null THEN '0' ");
	            sbSQL.append("\n                  ELSE m.folio_banco END as folio_banco,");
	            sbSQL.append("\n          m.importe, m.beneficiario, m.concepto, m.id_banco,cb.desc_banco,");
	            sbSQL.append("\n          m.no_docto, m.fec_imprime, m.id_chequera,m.id_caja,cc.desc_caja,");
	            sbSQL.append("\n          e.no_empresa as empresa,e.nom_empresa,lote_entrada, m.no_cheque,");
	            sbSQL.append("\n          CASE WHEN m.id_estatus_mov = 'I' THEN 'IMPRESO'");
	            sbSQL.append("\n               WHEN m.id_estatus_mov = 'R' THEN 'REIMPRESO'");
	            sbSQL.append("\n          END as id_estatus_mov,m.fec_entregado,m.id_divisa");
	            sbSQL.append("\n      FROM movimiento m ");
	            sbSQL.append("\n          LEFT JOIN cat_banco cb ON(m.id_banco = cb.id_banco)");
	            sbSQL.append("\n          LEFT JOIN cat_caja cc ON(m.id_caja = cc.id_caja),empresa e");
	            sbSQL.append("\n      WHERE m.no_empresa = e.no_empresa");
	            sbSQL.append("\n          and m.no_empresa  between " + funciones.convertirCadenaInteger(datos.get("empresaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("empresaSup").toString()));
	            sbSQL.append("\n          and m.id_banco between " + funciones.convertirCadenaInteger(datos.get("bancoInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("bancoSup").toString()));
	            sbSQL.append("\n          and m.id_chequera like '" + funciones.validarCadena(datos.get("chequera").toString()) + "'");
	            sbSQL.append("\n          and id_tipo_movto = 'E' and id_forma_pago in (1,7)");
	            sbSQL.append("\n          and id_estatus_mov in ('I','R')");
	            sbSQL.append("\n          and m.fec_entregado between '" + funciones.ponerFechaSola(datos.get("fechaInf").toString())+ "' and '" + funciones.ponerFechaSola(datos.get("fechaSup").toString())+ "'");
	            sbSQL.append("\n          and m.b_entregado = 'S'");
	            sbSQL.append("\n          and m.id_caja between " + funciones.convertirCadenaInteger(datos.get("cajaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("cajaSup").toString()));
	            sbSQL.append("\n          and m.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "'");
	            sbSQL.append("\n      UNION ALL");
	            sbSQL.append("\n      SELECT CASE WHEN m.folio_banco is null THEN '0' ");
	            sbSQL.append("\n                  ELSE m.folio_banco END as folio_banco,");
	            sbSQL.append("\n          m.importe, m.beneficiario, m.concepto, m.id_banco,cb.desc_banco,");
	            sbSQL.append("\n          m.no_docto,m.fec_imprime, m.id_chequera,m.id_caja,cc.desc_caja,");
	            sbSQL.append("\n          e.no_empresa as empresa,e.nom_empresa, lote_entrada, m.no_cheque,");
	            sbSQL.append("\n          CASE WHEN m.id_estatus_mov = 'I' THEN 'IMPRESO'");
	            sbSQL.append("\n               WHEN m.id_estatus_mov = 'R' THEN 'REIMPRESO'");
	            sbSQL.append("\n          END as id_estatus_mov,m.fec_entregado,m.id_divisa");
	            sbSQL.append("\n      FROM hist_movimiento m");
	            sbSQL.append("\n          LEFT JOIN cat_banco cb ON(m.id_banco = cb.id_banco)");
	            sbSQL.append("\n          LEFT JOIN cat_caja cc ON(m.id_caja = cc.id_caja),empresa e");
	            sbSQL.append("\n      WHERE m.no_empresa=e.no_empresa");
	            sbSQL.append("\n          and m.no_empresa  between " + funciones.convertirCadenaInteger(datos.get("empresaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("empresaSup").toString()));
	            sbSQL.append("\n          and m.id_banco between " + funciones.convertirCadenaInteger(datos.get("bancoInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("bancoSup").toString()));
	            sbSQL.append("\n          and m.id_chequera like '" + funciones.validarCadena(datos.get("chequera").toString()) + "'");
	            sbSQL.append("\n          and id_tipo_movto = 'E' and id_forma_pago in (1,7)");
	            sbSQL.append("\n          and id_estatus_mov in ('I','R')");
	            sbSQL.append("\n          and m.fec_entregado between '" + funciones.ponerFechaSola(datos.get("fechaInf").toString())+ "' and '" + funciones.ponerFechaSola(datos.get("fechaSup").toString())+ "'");
	            sbSQL.append("\n          and m.b_entregado = 'S'");
	            sbSQL.append("\n          and m.id_caja between " + funciones.convertirCadenaInteger(datos.get("cajaInf").toString()) + " and " + funciones.convertirCadenaInteger(datos.get("cajaSup").toString()));
	            sbSQL.append("\n          and m.id_divisa = '" + funciones.validarCadena(datos.get("divisa").toString()) + "'");
	            sbSQL.append("\n      ORDER BY nom_empresa,desc_caja,id_divisa,id_chequera,empresa");
	        }
			logger.info("consultarReporteHistoricoChequesEntregadosPorCaja");
			
			System.out.println("Consulta Historico Cheques Entregados PorCaja: " + sbSQL);
			
			resultado = (List<Map<String, Object>>)jdbcTemplate.query(sbSQL.toString(), new RowMapper(){

				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("folio_banco", rs.getString("folio_banco"));
			            results.put("importe", rs.getString("importe"));
			            results.put("beneficiario", rs.getString("beneficiario"));
			            results.put("concepto", rs.getString("concepto"));
			            results.put("id_banco", rs.getString("id_banco"));
			            results.put("desc_banco", rs.getString("desc_banco"));
			            results.put("no_docto", rs.getString("no_docto"));
			            results.put("fec_imprime", rs.getString("fec_imprime"));
			            results.put("id_chequera", rs.getString("id_chequera"));
			            results.put("id_caja", rs.getString("id_caja"));
			            results.put("desc_caja", rs.getString("desc_caja"));
			            results.put("empresa", rs.getString("empresa"));
			            results.put("nom_empresa", rs.getString("nom_empresa"));
			            results.put("lote_entrada", rs.getString("lote_entrada"));
			            results.put("no_cheque", rs.getString("no_cheque"));
			            results.put("id_estatus_mov", rs.getString("id_estatus_mov"));
			            results.put("fec_entregado", rs.getString("fec_entregado"));
			            results.put("id_divisa", rs.getString("id_divisa"));
			            
			            return results;
					}
				});

		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " " + e.toString()
					+ "P:Caja, C:CajaDaoImpl, M:consultarReporteHistoricoChequesEntregadosPorCaja");
		}
		return resultado;
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public Map<String, Object> ejecutarPagador(StoreParamsComunDto dto)
	{
		try {
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			return consultasGenerales.ejecutarPagador(dto);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	public void actualizarFecPago(String cveControl, String fecHoy) {
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" UPDATE seleccion_automatica_grupo SET fecha_pago = '" + fecHoy + "' \n");
			sql.append(" WHERE cve_control = '" + cveControl + "'");
			
			jdbcTemplate.update(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasDao, M:actualizarFecPago");
		}
	}
	
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Caja, C:CajaDaoImpl, M:setDataSource");
		}
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
