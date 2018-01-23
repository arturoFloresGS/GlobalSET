package com.webset.set.egresos.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
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

import com.webset.set.bancaelectronica.dto.ParametroDto;
import com.webset.set.egresos.business.GestionDeOperacionesDivisasBoImpl;
import com.webset.set.egresos.dto.ConfirmacionCargoCtaDto;
import com.webset.set.egresos.dto.OperacionDivisaDTO;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.StoreParamsComunDto;

public class GestionDeOperacionesDivisasDAOImpl implements GestionDeOperacionesDivisasDAO {
	
	private JdbcTemplate jdbcTemplate;
	ConsultasGenerales consultasGenerales;
	private Bitacora bitacora = new Bitacora();
	private Funciones funciones = new Funciones();
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public void setDataSource(DataSource dataSource){
		
		this.jdbcTemplate = new JdbcTemplate(dataSource);		
			
	}
	
	@Override
	public List<LlenaComboEmpresasDto> obtenerEmpresas(int idUsuario) {
		
		String sql= null;
		StringBuilder sb = new StringBuilder();
	
		sb.append("SELECT no_empresa as ID, nom_empresa as describ"	).append( "\n" ); 
		sb.append("FROM EMPRESA"									).append( "\n" );	
		sb.append("WHERE no_empresa > 1"							).append( "\n" );	
		sb.append("AND no_empresa in ("								).append( "\n" );
		sb.append("						SELECT no_empresa"			).append( "\n" );	
		sb.append("						FROM usuario_empresa"		).append( "\n" );	
		sb.append("						WHERE no_usuario = ?"		).append( "\n" );
		sb.append(")"												).append( "\n" );
		sb.append("ORDER BY describ"								).append( "\n" );
		
		sql = sb.toString(); 
		    
	    return jdbcTemplate.query(sql, new Object[] {idUsuario},new RowMapper(){
			public LlenaComboEmpresasDto mapRow(ResultSet rs, int idx) throws SQLException {
				LlenaComboEmpresasDto cons = new LlenaComboEmpresasDto();
				cons.setNoEmpresa (rs.getInt   ("ID")      );
				cons.setNomEmpresa(rs.getString("describ") );
				return cons;
		}});
		
	}//END METHOD: obtenerEmpresas
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ConfirmacionCargoCtaDto> obtenerDivisa (int idUsuario, int noEmpresa) {
		
		String sql = null;
		StringBuilder sbSQL = new StringBuilder();
			
		sbSQL.append( "SELECT id_divisa as ID, desc_divisa as Descrip" 	).append( "\n" );
		sbSQL.append( "FROM CAT_DIVISA" 							   	).append( "\n" );
		sbSQL.append( "WHERE clasificacion = 'D'"					   	).append( "\n" );
		sbSQL.append( "AND id_divisa IN("                              	).append( "\n" );
		sbSQL.append( "			SELECT DISTINCT id_divisa"	   		 	).append( "\n" );   
		sbSQL.append( "			FROM CAT_CTA_BANCO" 		   			).append( "\n" );
		sbSQL.append( "			WHERE no_empresa = ?"		  			).append( "\n" );
		sbSQL.append( "			AND id_estatus_cta = 'A'"	   			).append( "\n" );
		sbSQL.append( "			AND no_empresa in("            			).append( "\n" );
		sbSQL.append( "					 	SELECT no_empresa" 			).append( "\n" );
		sbSQL.append( "						FROM USUARIO_EMPRESA" 		).append( "\n" );
		sbSQL.append( "						WHERE no_usuario = ?" 		).append( "\n" );
		sbSQL.append( "			)"	   									).append( "\n" );
		sbSQL.append( ")"	   										   	).append( "\n" );
		
		sql = sbSQL.toString(); 
		
	    return jdbcTemplate.query(sql, new Object[] { noEmpresa, idUsuario }, new RowMapper(){
	    	
	    	public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
	    		ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
				cons.setIdDivisa(rs.getString("ID"));
				cons.setDescDivisa(rs.getString("Descrip"));
				return cons;
			}
	    });
		
	}//END METHOD: obtenerDivisaVta
		
	@SuppressWarnings("unchecked")
	@Override
	public List<ConfirmacionCargoCtaDto> obtenerBanco(int noEmpresa, String idDivisa) {
		
		String sql= null;
		StringBuilder sb = new StringBuilder();

	    sb.append( "SELECT id_banco as ID, desc_banco as Descrip" 			).append( "\n" ); 
		sb.append( "FROM CAT_BANCO"								  			).append( "\n" );
		sb.append( "WHERE id_banco in (" 						  			).append( "\n" );
		sb.append( "					SELECT distinct id_banco" 			).append( "\n" ); 
		sb.append( "					FROM CAT_CTA_BANCO"		  			).append( "\n" );
		sb.append( "					WHERE tipo_chequera in ('P','M')"   ).append( "\n" );
		sb.append( "					AND no_empresa = ?"					).append( "\n" );
		sb.append( "					AND id_divisa = ?"					).append( "\n" );
		sb.append( ")"														).append( "\n" );
		
		sql = sb.toString(); 
			
		return jdbcTemplate.query(sql, new Object[]{noEmpresa, idDivisa}, new RowMapper(){
			public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
				ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
				cons.setIdBanco(rs.getInt("ID"));
				cons.setDescBanco(rs.getString("Descrip"));
				return cons;
			}
		});   
		
	}//END METHOD:obtenerBanco 
		
	@SuppressWarnings("unchecked")
	@Override
	public List<ConfirmacionCargoCtaDto> obtenerChequera(int noEmpresa, String idDivisa, int idBanco) {
		
		String sql = null;
		StringBuilder sb = new StringBuilder();
			
		sb.append("SELECT id_chequera as id, id_chequera as descrip").append("\n");
		sb.append("FROM CAT_CTA_BANCO"								).append("\n");
		sb.append("WHERE tipo_chequera in ('P','M')"				).append("\n");
		sb.append("AND no_empresa = ?"								).append("\n");
		sb.append("AND id_divisa  = ?"								).append("\n");
		sb.append("AND id_banco   = ?"								).append("\n");
		
		sql = sb.toString(); 
			
		return jdbcTemplate.query(sql, new Object[]{noEmpresa, idDivisa, idBanco}, new RowMapper(){
			public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
				ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
				cons.setIdChequera(rs.getString("id"));   
				return cons;
			}
		});
		
	}//END METHDO:obtenerChequera 
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ConfirmacionCargoCtaDto> obtenerCasaCambio(){
		
		String sql=null;
		StringBuilder sb = new StringBuilder();
			
		sb.append("SELECT no_persona as ID, razon_social as Descrip"	).append("\n");
		sb.append("FROM PERSONA"										).append("\n");
		sb.append("WHERE id_tipo_persona = 'K'"							).append("\n");
		
		sql = sb.toString(); 
			
		return jdbcTemplate.query(sql, new RowMapper(){
			public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
				ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
				cons.setIdCasaCambio(rs.getInt("ID"));   
				cons.setDescCasaCambio(rs.getString("Descrip"));
				return cons;
			}
		});
	
	}//END METHOD:obtenerCasaCambio 
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ConfirmacionCargoCtaDto> obtenerBancoTerceros(int noCliente, String idDivisa) {
		
		String sql=null;
		StringBuilder sb = new StringBuilder();
		
		sb.append("SELECT id_banco as ID, desc_banco as Descrip"			).append("\n");
		sb.append("FROM CAT_BANCO"							    			).append("\n");
		sb.append("WHERE id_banco in ("						    			).append("\n");
		sb.append("						SELECT DISTINCT c.id_banco "		).append("\n");
		sb.append("						FROM CTAS_BANCO c, PERSONA p"		).append("\n");
		sb.append("						WHERE p.no_persona = c.no_persona"	).append("\n");
		sb.append("						AND p.id_tipo_persona = 'K'"		).append("\n");
		sb.append("						AND c.id_divisa  = ?"				).append("\n");
		sb.append("						AND p.no_persona = ?"				).append("\n");
		sb.append("						AND c.no_empresa = 552"				).append("\n");
		sb.append(")"														).append("\n");
	    			
		return jdbcTemplate.query(sql, new Object[]{ idDivisa, noCliente }, new RowMapper(){
			public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
				ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
				cons.setIdBanco  (rs.getInt("ID")         );
				cons.setDescBanco(rs.getString("Descrip") );
				return cons;
			}
		});
		
	}//END METHOD: obtenerBancoTerceros
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ConfirmacionCargoCtaDto> obtenerChequeraTerceros(int noCliente, String idDivisa, int idBanco) {
		
		String sql = null;
		StringBuilder sb = new StringBuilder();
		
			
		sb.append("SELECT id_chequera as id, id_chequera as descrip").append("\n");
		sb.append("FROM CTAS_BANCO"									).append("\n");
		sb.append("WHERE no_persona = ?"							).append("\n");
		sb.append("AND id_divisa    = ?"							).append("\n");
		sb.append("AND id_banco     = ?"							).append("\n");
		
		sql = sb.toString(); 
			
		return jdbcTemplate.query(sql, new Object[]{noCliente, idDivisa, idBanco}, new RowMapper(){
			public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
				ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
				cons.setIdChequera(rs.getString("id"));
				return cons;
			}
		});
		
	}//END METHOD: obtenerChequeraTerceros
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ConfirmacionCargoCtaDto> obtenerOperador( int noCliente ) {
		
		String sql="";
		StringBuilder sb = new StringBuilder();
		
		sb.append("SELECT no_persona as Id, nombre_corto as Descrip").append("\n");
		sb.append("FROM PERSONA"									).append("\n");
		sb.append("WHERE no_persona IN("							).append("\n");
		sb.append("						SELECT no_persona_rel"		).append("\n");
		sb.append("						FROM RELACION"				).append("\n");
		sb.append("						WHERE no_persona ?"			).append("\n");
		sb.append(")"												).append("\n");
		sb.append("AND id_tipo_persona = 'F'"						).append("\n"); 
						
		return jdbcTemplate.query(sql, new Object[]{noCliente}, new RowMapper(){
			public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
				ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
				cons.setIdOperador(rs.getInt("Id"));
				cons.setNomOperador(rs.getString("Descrip"));
				return cons;
			}
		});
		
	}//END METHOD: obtenerOperadorVta
	
	@Override
	public List<ConfirmacionCargoCtaDto> obtenerGrupos( String idTipoMovto ) {

		String sql = null;
		StringBuilder sb = new StringBuilder();
			
		sb.append( "SELECT DISTINCT CR.ID_GRUPO AS ID_GRUPO, " 	).append( "\n" );
		sb.append( "CB.DESC_GRUPO" 								).append( "\n" ); 
		sb.append( "FROM CAT_RUBRO CR"							).append( "\n" );						
		sb.append( "			LEFT JOIN CAT_GRUPO CB"			).append( "\n" );
		sb.append( "			ON CR.ID_GRUPO = CB.ID_GRUPO"	).append( "\n" );
		sb.append( "WHERE CR.INGRESO_EGRESO = ?"				).append( "\n" );
		sb.append( "ORDER BY 2"									).append( "\n" );
		
		Object[] params = {idTipoMovto};
		
		sql = sb.toString(); 
		
		return 
		jdbcTemplate.query(sql, params, new RowMapper(){			
			public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
				ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
				cons.setIdGrupo(rs.getInt("ID_GRUPO"));
				cons.setDescGrupo(rs.getString("DESC_GRUPO"));
				return cons;
			}
		});
		
	}//END METHOD: obtenerGruposPorTipoMovto
	
	@Override
	public List<ConfirmacionCargoCtaDto> obtenerRubros(int idGrupo) {
		
		String sql = null; 
		StringBuilder sb = new StringBuilder();
			
		sb.append("SELECT id_rubro as ID, desc_rubro as Descrip").append("\n");
		sb.append("FROM cat_rubro"								).append("\n");
		sb.append("WHERE id_grupo = ?"							).append("\n");
		
		sql = sb.toString(); 
			    			
		return jdbcTemplate.query(sql, new RowMapper(){
			public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
				ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
				cons.setIdRubro(rs.getInt("ID"));
				cons.setDescRubro(rs.getString("Descrip"));
				return cons;
			}
		});
		
	}//END METHOD: obtenerRubros
	
	@Override
	public List<ConfirmacionCargoCtaDto> llenaComboFirmas() {

		String sql = null;
		StringBuilder sb = new StringBuilder();
		
			
		sb.append( "SELECT ID_FIRMANTE AS ID," 			).append( "\n" );
		sb.append( "	   NOMBRE_FIRMANTE AS DESCRIP"	).append( "\n" );
		sb.append( "FROM FIRMA_CVD"						).append( "\n" );
		sb.append( "ORDER BY 1"							).append( "\n" );
		
		sql = sb.toString();
	    
	    return jdbcTemplate.query(sql, new RowMapper(){
	    	
	    	public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
	    		
	    		ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
				cons.setIdFirmante    ( rs.getInt("ID")         );
				cons.setNombreFirmante( rs.getString("DESCRIP") );
				
				return cons;
				
			} 
	    });
		
	}//END METHOD: llenaComoboFirmas

	@Override
	public int actualizarFolioReal(String tipoFolio) {
		
		String sql = null; 
		StringBuilder sb = new StringBuilder();
		
		sb.append("UPDATE FOLIO "					).append("\n");
		sb.append("SET num_folio = num_folio + 1 "	).append("\n");
		sb.append("WHERE tipo_folio = ?"			).append("\n");
		
		sql = sb.toString();
			
		return jdbcTemplate.update(sql, new Object[]{ tipoFolio } );
		
	}//END METHOD: actualizarFolioReal

	@Override
	public int seleccionarFolioReal(String tipoFolio) {
		
		String sql = null; 
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT num_folio"		).append("\n");
		sb.append("FROM FOLIO"				).append("\n");
		sb.append("WHERE tipo_folio = ?"	).append("\n");
		
		sql = sb.toString(); 
		
		return jdbcTemplate.queryForInt(sql, new Object[]{ tipoFolio } );
		
	}//END METHOD:seleccionarFolioReal 

	@Override
	public List<ConfirmacionCargoCtaDto> obtenerCajaCuenta( int noEmpresa ) {
		
		String sql = null;
		StringBuilder sb = new StringBuilder();
		
		sb.append(  "SELECT coalesce(id_caja, 0) AS id_caja"		).append("\n"); 
		sb.append(  "COALESCE(no_cuenta_emp, 0) AS no_cuenta_emp" 	).append("\n");
		sb.append(  "FROM EMPRESA" 									).append("\n"); 
		sb.append(  "WHERE no_empresa = ?" 							).append("\n"); 
		sb.append(  "ORDER BY no_cuenta_emp"						).append("\n");
		
		sql = sb.toString(); 

		return jdbcTemplate.query(sql, new Object[]{ noEmpresa },new RowMapper(){
			public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
				ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
				cons.setIdCaja(rs.getInt("id_caja"));
				cons.setNoCuenta(rs.getString("no_cuenta_emp"));
				return cons;
			}
		});

	}//END METHOD: obtenerCajaCuenta

	@Override
	public int InsertAceptado(int inst, ConfirmacionCargoCtaDto dto, int noFolioParam, int tipoMovto, int cuentaEmp, int noFolioDocto,
			int idCaja, String concepto, String leyenda, String obervacion, String solicita, String autoriza, int plaza,
			int sucursal, int grupo, int agrupa1, int agrupa2, int agrupa3, boolean ADU, int pedido, boolean CVDivisa, int area,
			String referencia, String sDivision, int piLote, String partida, boolean pbNomina, boolean pbProvAc){
		
		StringBuffer sql = new StringBuffer();
		String fecHoy = "";
		String fechaValor = "";
		String fechaLiquidacion = "";
		int idGrupo = 0;
		int res = 0;
		String estatus = "L";
		   
        fecHoy = "" + obtenerFechaHoy();
		fecHoy = fecHoy.substring(8,10)+"/"+fecHoy.substring(5,7)+"/"+fecHoy.substring(0,4);
		
		fechaValor = dto.getFechaValor();
		fechaValor = fechaValor.substring(8,10)+"/"+fechaValor.substring(5,7)+"/"+fechaValor.substring(0,4);
		
		
		fechaLiquidacion = dto.getFechaLiquidacion();
		fechaLiquidacion = fechaLiquidacion.substring(8,10)+"/"+fechaLiquidacion.substring(5,7)+"/"+fechaLiquidacion.substring(0,4);
		
		try{
		    sql.append("\n INSERT INTO parametro (no_empresa, no_docto, no_folio_param, id_tipo_docto, id_forma_pago, ");
		    sql.append("\n      usuario_alta, id_tipo_operacion, no_cuenta, no_factura, fec_valor, fec_valor_original, ");
		    sql.append("\n      fec_operacion, fec_alta, id_divisa, id_divisa_original, importe, importe_original, ");
		    sql.append("\n      tipo_cambio, id_caja, referencia, beneficiario,");
		    sql.append("\n      concepto, id_banco_benef, id_chequera_benef, aplica,");
		    sql.append("\n      id_estatus_mov, b_salvo_buen_cobro, id_estatus_reg, id_leyenda, folio_ref, ");
		    sql.append("\n      id_chequera, observacion, origen_mov, no_cliente,");
		    sql.append("\n      solicita, autoriza, plaza, sucursal, grupo, ");
		    sql.append("\n      no_folio_mov, agrupa1, agrupa2, agrupa3, id_grupo, id_rubro, ");
		    sql.append("		id_banco");
		    
		    if (ADU == true) sql.append("\n ,no_pedido ");
		    
		    if(inst != 247){
		    	if (area != -1) sql.append("\n ,id_area ");
		        
		    	if (!sDivision.equals("")) sql.append("\n ,division ");
		        
		        if (pbNomina && piLote != 0)
		        	sql.append("\n ,lote ");
		    	else if ((pbProvAc && piLote == 0) || (pbProvAc && piLote == 1))
		    		sql.append("\n ,lote ");
		    	else if (piLote != 0) 
		    		sql.append("\n ,lote ");
		        
		    	if(inst == 245) sql.append("\n ,invoice_type");
		    }
		    sql.append("\n)");
		    
		    sql.append("\n VALUES (");
		    sql.append("\n " + dto.getNoEmpresa() + ", '" + noFolioDocto + "', " + noFolioParam + ", 41, " + dto.getIdFormaPago() + ",");
		    sql.append("\n " + dto.getUsuario() + ", " + tipoMovto + ", " + cuentaEmp + ", '" + noFolioDocto + "', '" + fechaValor + "',");
		    
		    if (fechaValor != "")  
		    	sql.append("\n '" + fechaValor + "',");
		      else
		    	  sql.append("\n '" + fecHoy + "',");
		    
		    sql.append("\n '" + fecHoy + "', '" + fecHoy + "',");
		    
		    if(inst != 247)
		    	sql.append("'" + dto.getDivisaVenta() + "', '" + dto.getDivisaVenta() + "', " + dto.getImporteVenta() + "," + dto.getImporteVenta() + ",");
		    else
		    	sql.append("'" + dto.getDivisaCompra() + "', '" + dto.getDivisaCompra() + "', " + dto.getImporteCompra() + "," + dto.getImporteCompra() + ",");
		    
		    
		    sql.append("\n " + dto.getTipoCambio() + ", " + idCaja + ", '" + referencia + "', '" + dto.getDescCasaCambio() + "', '" + concepto + "', ");		    
		    
		    sql.append("" + dto.getIdBancoBenef() + ", '" + dto.getIdChequeraBenef() + "'");
		    
		    
		    sql.append("\n ,1,");
		    
		    if(inst == 244) sql.append("\n 'C','S','P','" + leyenda + "', '', ");
		    if(inst == 245) sql.append("\n 'H','S','P','" + leyenda + "', 1,");
		    if(inst == 247) sql.append("'"+ estatus + "'"+ ",'S','P','" + leyenda + "', 1,");
		    
		    if(inst != 247) {
			    if (dto.getChequeraCargo().equals(""))
			    	sql.append("\n NULL");
			    else
			    	sql.append("\n '" + dto.getChequeraCargo()+ "'");
		    }else {
		    	if (dto.getIdChequera().equals(""))  
			    	sql.append("\n NULL");
			    else
			    	sql.append("\n '" + dto.getIdChequera() + "'");
		    }
		    sql.append("\n ,'" + dto.getObservacion() + "',");
		    
		    if(ADU)  
		    	sql.append("\n 'ADU'");
		    else if(CVDivisa)  
		    	sql.append("\n 'CVD'");
		    else
		    	sql.append("\n 'SET'");
		    sql.append("\n ,'" + dto.getIdCasaCambio() + "',");
		    
	    	if(solicita.equals(""))
	    		sql.append("NULL, ");
	    	else 
	    		sql.append("'"+ solicita + "', ");
	    	
	    	if (autoriza.equals(""))
	    		sql.append("\n NULL, ");
	    	else
	    		sql.append("\n '" + autoriza + "', ");
		    
		    sql.append( "\n "+ plaza + "," + sucursal + ", " + grupo + ", 1, " + agrupa1 + ", " + agrupa2 + "," + agrupa3 + "");
		    
		    if (dto.getIdGrupo() == 0)
		        sql.append("\n ,NULL");
		    else
		        sql.append( ","+ dto.getIdGrupo());
		    
		    
		    if (dto.getIdRubro() == 0)
		        sql.append("\n ,NULL");
		    else
		        sql.append( ","+ dto.getIdRubro());
		    
		    
		    if(inst != 247) 
		    	sql.append( "," + dto.getBancoCargo());
		    else
		    	sql.append( "," + dto.getIdBanco());
		    
		    if (ADU) sql.append("\n ," + pedido);
		    
		    if(inst != 247){
		    	if (area != -1)  
		    		sql.append("\n ," + area);
		    	
		    	if (sDivision != "")
		    		sql.append("\n ,'" + sDivision + "'");
		    
		    
		    	if (pbNomina == true && piLote != 0)  
		    		sql.append("\n ,3");
		    	else if ((pbProvAc == true && piLote == 0) || (pbProvAc == true && piLote == 1))  
		    		sql.append("\n ,4");
		    	else if (piLote != 0)  
		    		if(inst == 244)
		    			sql.append("\n ,2");
		    		else
		    			sql.append( "," + piLote);
		    	
		    	if(inst == 244 && partida != "")      
		    		sql.append("\n ,'" + partida + "'");
		    
		    	if (inst == 245)
		    		sql.append("\n ,'" + partida + "'");
		    }
		    sql.append("\n)");
		    res = jdbcTemplate.update(sql.toString());
		}catch(Exception e){
			System.out.println(e);
		}     
		return res;
	}
	
	@Override
	public Date obtenerFechaHoy() {
		
		String sql = null; 
		StringBuilder sb = new StringBuilder();
		
		sb.append("SELECT fec_hoy" ).append("\n");
		sb.append("FROM FECHAS"	   ).append("\n");
	
		sql = sb.toString(); 
		
		 List<Date> fechas = jdbcTemplate.query(sql , new RowMapper() {
			public Date mapRow(ResultSet rs, int idx) throws SQLException {
				return rs.getDate("fec_hoy");
			}
		});
		 
		return fechas.get(0);
		
	}//END METHOD: obtenerFechaHoy
	
	@Override
	public String obtenerFechaHoyV2() {
		
		String sql = null; 
		StringBuilder sb = new StringBuilder();
		
		sb.append("SELECT convert(datetime,fec_hoy,103) as fec_hoy" ).append("\n");
		sb.append("FROM FECHAS"	                         ).append("\n");
	
		sql = sb.toString(); 
		
		 List<String> fechas = jdbcTemplate.query(sql , new RowMapper<String>() {
			public String mapRow(ResultSet rs, int idx) throws SQLException {
				return rs.getString("fec_hoy");
			}
		});
		 
		return fechas.get(0);
		
	}//END METHOD: obtenerFechaHoy

	@Override
	public int guardarCartaCVD(ConfirmacionCargoCtaDto dto) {
		
		String sql = null;
		StringBuilder sb = new StringBuilder();
		int iResp = 0;
		
		sb.append( "INSERT INTO CARTAS_CVD(" 	).append("\n");
		sb.append( "no_docto,"					).append("\n");
		sb.append( "no_empresa,"				).append("\n");
		sb.append( "nom_empresa,"				).append("\n");
		sb.append( "fecha_valor,"				).append("\n");
		sb.append( "fecha_liquidacion,"			).append("\n");
		sb.append( "id_divisa_venta,"			).append("\n");
		sb.append( "desc_divisa_pago,"			).append("\n");
		sb.append( "id_banco_cargo,"			).append("\n");
		sb.append( "nom_banco_cargo,"			).append("\n");
		sb.append( "id_chequera_cargo,"			).append("\n");
		sb.append( "importe_venta,"				).append("\n");
		sb.append( "id_divisa_abono,"			).append("\n");
		sb.append( "desc_divisa_abono,"			).append("\n");
		sb.append( "id_banco_abono,"			).append("\n");
		sb.append( "nom_banco_abono,"			).append("\n");
		sb.append( "id_chequera_abono,"			).append("\n");
		sb.append( "tipo_cambio,"				).append("\n");
		sb.append( "importe_compra,"			).append("\n");
		sb.append( "no_persona,"				).append("\n");
		sb.append( "nom_casa_cambio,"			).append("\n");
		sb.append( "direccion,"					).append("\n");
		sb.append( "colonia,"					).append("\n");
		sb.append( "cp,"						).append("\n");
		sb.append( "ciudad,"					).append("\n");
		sb.append( "id_banco_casa_cambio,"		).append("\n");
		sb.append( "desc_banco_casa_cambio,"	).append("\n");
		sb.append( "id_chequera_casa_cambio,"	).append("\n");
		sb.append( "operador,"					).append("\n");
		sb.append( "concepto,"					).append("\n");
		sb.append( "id_grupo,"					).append("\n");
		sb.append( "desc_grupo,"				).append("\n");
		sb.append( "id_rubro,"					).append("\n");
		sb.append( "desc_rubro,"				).append("\n");
		sb.append( "estatus)"					).append("\n");
		sb.append( "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"	).append("\n");
		
	 	String fechaValor 		= dto.getFechaValor().toString();
	 	String fechaLiquidacion = dto.getFechaLiquidacion().toString();
	 	
	 	fechaValor 			= fechaValor.substring(8,10)+"/"+fechaValor.substring(5,7)+"/"+fechaValor.substring(0,4);
	 	fechaLiquidacion	= fechaLiquidacion.substring(8,10)+"/"+fechaLiquidacion.substring(5,7)+"/"+fechaLiquidacion.substring(0,4);
	 	
	 	DecimalFormat dTCambio = new DecimalFormat("##.0000");
	 	DecimalFormat dTImporte = new DecimalFormat("#########.00");
	 	
	 	String importeVenta = dTImporte.format(dto.getImporteVenta());
	 	String tipoCambio = dTCambio.format(dto.getTipoCambio());
	 	String importeCompra = dTImporte.format(dto.getImporteCompra());
	 	
		
		Object[] params = new Object[] { 
			dto.getNoDocto(),
			String.valueOf(dto.getNoEmpresa()),
			dto.getNomEmpresa(),
			fechaValor,
			fechaLiquidacion,	    	
	    	dto.getDivisaVenta().toString(),
	    	dto.getDescDivisaVenta(), 	    	
	    	String.valueOf(dto.getBancoCargo()),
	    	dto.getDescBancoCargo(),	    	
	    	dto.getChequeraCargo().toString(),
	    	importeVenta	   , 	
	    	dto.getDivisaCompra().toString(),
	    	dto.getDescDivisaCompra(),
	    	String.valueOf(dto.getBancoAbono()),
	    	dto.getDescBancoAbono(),
	    	dto.getChequeraAbono().toString(),
	    	tipoCambio,    	
	    	importeCompra,
	    	String.valueOf(dto.getIdCasaCambio()),
	    	String.valueOf(dto.getDescCasaCambio()),
	    	"SIN VALOR",
	    	"SIN VALOR",
	    	"SIN VALOR",
	    	"SIN VALOR",
	    	String.valueOf(dto.getIdBanco()),
	    	dto.getDescBancoCasa(),
	    	String.valueOf(dto.getIdChequera()),
	    	dto.getNomOperador().toString(),
	    	dto.getConcepto().toString(),
	    	String.valueOf(dto.getIdGrupo()),
	    	dto.getDescGrupoEgreso(),
	    	String.valueOf(dto.getIdRubro()),
	    	dto.getDescRubroEgreso(),
	    	"P"
		};
			
		sql = sb.toString(); 
			
		iResp = jdbcTemplate.update(sql, params);
			
		return iResp;
	}
	
	@Override
	public Map ejecutarGenerador(int emp, int folParam,int folMovi, int folDeta, int result, String mensajeResp)
	{

		Map resultado= new HashMap();
		
		try {
			
			StoredProcedure storedProcedure = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_generador") {};
			storedProcedure.declareParameter(new SqlParameter("emp",Types.INTEGER ));
			storedProcedure.declareParameter(new SqlInOutParameter("folParam",Types.INTEGER ));
			storedProcedure.declareParameter(new SqlParameter("folMovi",Types.INTEGER ));
			storedProcedure.declareParameter(new SqlInOutParameter("folDeta",Types.INTEGER ));
			storedProcedure.declareParameter(new SqlInOutParameter("result",Types.INTEGER ));
			storedProcedure.declareParameter(new SqlInOutParameter("mensajeResp",Types.VARCHAR));
			
			HashMap< Object, Object > inParams = new HashMap< Object, Object >();

			inParams.put( "emp",emp);
			inParams.put( "folParam",folParam);
			inParams.put( "folMovi",folMovi);
			inParams.put( "folDeta",folDeta);
			inParams.put( "result",result);
			inParams.put( "mensajeResp",mensajeResp);
			resultado = storedProcedure.execute((Map)inParams);
			
			
		}catch (Exception e) {
			System.out.println(e);
		}
		
		return resultado; 
	}//END METHOD: ejecutarGenerador
	
	@Override	
	public List<OperacionDivisaDTO> traerOperacionesCompraDeTransferParaPago() {

		StringBuilder sb = new StringBuilder();
		String sql = null; 
		
		sb.append("SELECT"												).append("\n"); 
		sb.append("'CVT' AS tipoOperacionDivTransf,"					).append("\n"); 
		sb.append("'SNV' AS noDocSap, "									).append("\n");
		sb.append("'SNV' AS Cliente, "									).append("\n");
		sb.append("'SNV' AS usuario,"									).append("\n");
		sb.append("CVD.NO_DOCTO AS folio,"								).append("\n");
		sb.append("CVD.NO_EMPRESA AS noEmpresa,"					    ).append("\n");
		sb.append("CVD.NOM_EMPRESA AS nomEmpresa, "						).append("\n");
		sb.append("CVD.ID_DIVISA_VENTA AS idDivisaVenta, "				).append("\n");
		sb.append("CVD.DESC_DIVISA_PAGO AS descDivisaVenta,"			).append("\n");
		sb.append("CVD.id_banco_cargo AS  idBancoCargo,"				).append("\n");
		sb.append("CVD.nom_banco_cargo AS descBancoCargo,"				).append("\n");
		sb.append("CVD.id_chequera_cargo AS chequeraCargo, "			).append("\n");
		sb.append("CVD.id_divisa_abono AS idDivisaCompra,"				).append("\n");
		sb.append("CVD.desc_divisa_abono AS descDivisaCompra,"			).append("\n");
		sb.append("CVD.id_banco_abono AS idBancoAbono, "				).append("\n");
		sb.append("CVD.nom_banco_abono AS descBancoAbono,"				).append("\n");
		sb.append("CVD.id_chequera_abono AS chequeraAbono, "			).append("\n");
		sb.append("CVD.no_persona AS idCasaDeCambio, "					).append("\n");
		sb.append("CVD.nom_casa_cambio AS nomCasaDeCambio,"				).append("\n");
		sb.append("CVD.id_banco_casa_cambio AS idBancoCasaCambio, "		).append("\n");
		sb.append("CVD.desc_banco_casa_cambio AS descBancoCasaCambio ,"	).append("\n");
		sb.append("CVD.id_chequera_casa_cambio AS chequeraCasaCambio,"	).append("\n");
		sb.append("0 AS idOperador, "									).append("\n");
		sb.append("CVD.operador AS nomOperador, "						).append("\n");
		sb.append("CVD.fecha_valor AS fechaValor, "						).append("\n");
		sb.append("CVD.fecha_liquidacion AS fechaLiquidacion,"			).append("\n"); 
		sb.append("CVD.importe_compra AS importeCompra, "				).append("\n");
		sb.append("CVD.tipo_cambio AS tipoDeCambio, "					).append("\n");
		sb.append("CVD.importe_venta AS importeVenta, "					).append("\n");
		sb.append("0 AS idFormaPago, "									).append("\n");
		sb.append("'SNV' AS descFormaPago,"								).append("\n");
		sb.append("CVD.concepto AS concepto,"							).append("\n");
		sb.append("'SNV'  AS referencia,"								).append("\n");
		sb.append("'SNV' AS firma1, "									).append("\n");
		sb.append("'SNV' AS firma2, "									).append("\n");
		sb.append("CVD.id_grupo AS idGrupoEgreso, "						).append("\n");
		sb.append("CVD.desc_grupo AS descGrupoEgreso, "					).append("\n");
		sb.append("CVD.id_rubro AS idRubroEgreso, "						).append("\n");
		sb.append("CVD.desc_rubro AS descRubroEgreso, "					).append("\n");
		sb.append("0 AS idGrupoIngreso, "								).append("\n");
		sb.append("'SNV' AS descGrupoIngreso, "							).append("\n");
		sb.append("0 AS idRubroIngreso, "								).append("\n");
		sb.append("'SNV' AS descRubroIngreso,"							).append("\n");
		sb.append("0 AS noProveedor, "									).append("\n");
		sb.append("CVD.concepto AS nomProveedor, "							).append("\n");
		sb.append("estatus AS estatusAutorizacion,"						).append("\n");
		sb.append("'SNV' AS abaOswift,"									).append("\n");
		sb.append("M.cve_control AS cveControl,"						    ).append("\n");
		sb.append("convert(datetime,M.fec_Propuesta,103) AS fecPropuesta"						).append("\n");
		sb.append("FROM CARTAS_CVD CVD"									).append("\n");
		sb.append("				INNER JOIN movimiento M ON CVD.no_docto = M.folio_ref").append("\n");
		sb.append("				and M.id_tipo_operacion = 3200"						     ).append("\n");
		sb.append("				and M.origen_mov = 'CVT'"								 ).append("\n");
		sb.append("				and M.id_estatus_mov = 'P'"								 ).append("\n");
		sb.append("WHERE CVD.ESTATUS IN( 'P', 'A')"						).append("\n");

		
		sql = sb.toString(); 
		
		
		return jdbcTemplate.query(sql, new RowMapper<OperacionDivisaDTO>(){
			public OperacionDivisaDTO mapRow(ResultSet rs, int idx) throws SQLException {
				
				OperacionDivisaDTO operacion = new OperacionDivisaDTO();
				
				operacion.setTipoOperacionDivTransf	(rs.getString("tipoOperacionDivTransf"));	
				operacion.setNoDocSap 				(rs.getString("noDocSap"));
				operacion.setCliente				(rs.getString("cliente"));
				operacion.setUsuario				(rs.getString("usuario"));
				operacion.setFolio					(rs.getString("folio"));
				operacion.setNoEmpresa				(rs.getInt("noEmpresa"));
				operacion.setNomEmpresa				(rs.getString("nomEmpresa"));
				operacion.setIdDivisaVenta			(rs.getString("idDivisaVenta"));
				operacion.setDescDivisaVenta		(rs.getString("descDivisaVenta"));
				operacion.setIdBancoCargo			(rs.getInt("idBancoCargo"));
				operacion.setDescBancoCargo			(rs.getString("descBancoCargo"));
				operacion.setChequeraCargo			(rs.getString("chequeraCargo"));
				operacion.setIdDivisaCompra			(rs.getString("idDivisaCompra"));
				operacion.setDescDivisaCompra 		(rs.getString("descDivisaCompra"));
				operacion.setIdBancoAbono			(rs.getInt("idBancoAbono"));
				operacion.setDescBancoAbono			(rs.getString("descBancoAbono"));
				operacion.setChequeraAbono			(rs.getString("chequeraAbono"));
				operacion.setIdCasaDeCambio			(rs.getInt("idCasaDeCambio"));
				operacion.setNomCasaDeCambio		(rs.getString("nomCasaDeCambio"));
				operacion.setIdBancoCasaCambio		(rs.getInt("idBancoCasaCambio"));
				operacion.setDescBancoCasaCambio	(rs.getString("descBancoCasaCambio"));
				operacion.setChequeraCasaCambio		(rs.getString("chequeraCasaCambio"));
				operacion.setIdOperador				(rs.getInt("idOperador"));
				operacion.setNomOperador			(rs.getString("nomOperador"));
				operacion.setFechaValor				(rs.getString("fechaValor"));
				operacion.setFechaLiquidacion		(rs.getString("fechaLiquidacion"));
				operacion.setImporteCompra			(rs.getDouble("importeCompra"));
				operacion.setTipoDeCambio			(rs.getDouble("tipoDeCambio"));
				operacion.setImporteVenta			(rs.getDouble("importeVenta"));
				operacion.setIdFormaPago			(rs.getInt("idFormaPago"));
				operacion.setDescFormaPago	 		(rs.getString("descFormaPago"));
				operacion.setConcepto				(rs.getString("concepto"));
				operacion.setReferencia				(rs.getString("referencia"));
				operacion.setFirma1					(rs.getString("firma1"));
				operacion.setFirma2					(rs.getString("firma2"));				
				operacion.setDescGrupoEgreso		(rs.getString("descGrupoEgreso"));				
				operacion.setDescRubroEgreso		(rs.getString("descRubroEgreso"));				
				operacion.setDescGrupoIngreso		(rs.getString("descGrupoIngreso"));				
				operacion.setDescRubroIngreso		(rs.getString("descRubroIngreso"));
				operacion.setNoProveedor 			(rs.getInt("noProveedor"));
				operacion.setNomProveedor			(rs.getString("nomProveedor"));
				operacion.setAbaOswift				(rs.getString("abaOswift"));
				operacion.setEstatusAutorizacion	(rs.getString("estatusAutorizacion"));
				operacion.setCveControl             (rs.getString( "cveControl" ));
				operacion.setFecPropuesta( rs.getString("fecPropuesta"));
				
				
				return operacion;
			}
		});
		
	}//END METHOD: traerOperacionesCompraDeTransferParaPago

	@Override
	public List<OperacionDivisaDTO> traerOperacionesCompraVentaDeDivisasParaPago() {
		
		StringBuilder sb = new StringBuilder();
		String sql = null; 
		
		sb.append("SELECT"												).append("\n"); 
		sb.append("'CVD' AS tipoOperacionDivTransf,"					).append("\n"); 
		sb.append("'SNV' AS noDocSap, "									).append("\n");
		sb.append("'SNV' AS Cliente, "									).append("\n");
		sb.append("'SNV' AS usuario,"									).append("\n");
		sb.append("CVD.NO_DOCTO AS folio,"								).append("\n");
		sb.append("E.NO_EMPRESA AS noEmpresa,"							).append("\n");
		sb.append("E.NOM_EMPRESA AS nomEmpresa, "						).append("\n");
		sb.append("CVD.ID_DIVISA_VENTA AS idDivisaVenta, "				).append("\n");
		sb.append("CVD.DESC_DIVISA_PAGO AS descDivisaVenta,"			).append("\n");
		sb.append("CVD.id_banco_cargo AS  idBancoCargo,"				).append("\n");
		sb.append("CVD.nom_banco_cargo AS descBancoCargo,"				).append("\n");
		sb.append("CVD.id_chequera_cargo AS chequeraCargo, "			).append("\n");
		sb.append("CVD.id_divisa_abono AS idDivisaCompra,"				).append("\n");
		sb.append("CVD.desc_divisa_abono AS descDivisaCompra,"			).append("\n");
		sb.append("CVD.id_banco_abono AS idBancoAbono, "				).append("\n");
		sb.append("CVD.nom_banco_abono AS descBancoAbono,"				).append("\n");
		sb.append("CVD.id_chequera_abono AS chequeraAbono, "			).append("\n");
		sb.append("CVD.no_persona AS idCasaDeCambio, "					).append("\n");
		sb.append("CVD.nom_casa_cambio AS nomCasaDeCambio,"				).append("\n");
		sb.append("CVD.id_banco_casa_cambio AS idBancoCasaCambio, "		).append("\n");
		sb.append("CVD.desc_banco_casa_cambio AS descBancoCasaCambio ,"	).append("\n");
		sb.append("CVD.id_chequera_casa_cambio AS chequeraCasaCambio,"	).append("\n");
		sb.append("0 AS idOperador, "									).append("\n");
		sb.append("CVD.operador AS nomOperador, "						).append("\n");
		sb.append("CVD.fecha_valor AS fechaValor, "						).append("\n");
		sb.append("CVD.fecha_liquidacion AS fechaLiquidacion,"			).append("\n"); 
		sb.append("CVD.importe_compra AS importeCompra, "				).append("\n");
		sb.append("CVD.tipo_cambio AS tipoDeCambio, "					).append("\n");
		sb.append("CVD.importe_venta AS importeVenta, "					).append("\n");
		sb.append("0 AS idFormaPago, "									).append("\n");
		sb.append("'SNV' AS descFormaPago,"								).append("\n");
		sb.append("CVD.concepto AS concepto,"							).append("\n");
		sb.append("'SNV'  AS referencia,"								).append("\n");
		sb.append("'SNV' AS firma1, "									).append("\n");
		sb.append("'SNV' AS firma2, "									).append("\n");
		sb.append("CVD.id_grupo AS idGrupoEgreso, "						).append("\n");
		sb.append("CVD.desc_grupo AS descGrupoEgreso, "					).append("\n");
		sb.append("CVD.id_rubro AS idRubroEgreso, "						).append("\n");
		sb.append("CVD.desc_rubro AS descRubroEgreso, "					).append("\n");
		sb.append("0 AS idGrupoIngreso, "								).append("\n");
		sb.append("'SNV' AS descGrupoIngreso, "							).append("\n");
		sb.append("0 AS idRubroIngreso, "								).append("\n");
		sb.append("'SNV' AS descRubroIngreso,"							).append("\n");
		sb.append("0 AS noProveedor, "									).append("\n");
		sb.append("'SNV' AS nomProveedor, "								).append("\n");
		sb.append("'SNV' AS abaOswift,"									).append("\n");
		sb.append("estatus AS estatusAutorizacion"						).append("\n");
		sb.append("FROM CARTAS_CVD CVD"									).append("\n");
		sb.append("				LEFT JOIN EMPRESA E "					).append("\n");
		sb.append("				ON CVD.no_empresa = E.NO_EMPRESA "		).append("\n");
		sb.append("WHERE no_docto in(  "								).append("\n");
		sb.append("						select no_docto ").append("\n");
		sb.append("						from movimiento "												 ).append("\n");
		sb.append("						where id_tipo_operacion = 3000"									 ).append("\n");
		sb.append("						and origen_mov = 'CVD'"											 ).append("\n");
		sb.append("						and id_estatus_mov in ('C', 'N')"										 ).append("\n");
		sb.append(")"													).append("\n");
		sb.append("AND ESTATUS IN( 'P', 'A')"							).append("\n");
System.out.println("Query \n"+sb.toString());
		
		sql = sb.toString(); 
		
		
		return jdbcTemplate.query(sql, new RowMapper(){
			public OperacionDivisaDTO mapRow(ResultSet rs, int idx) throws SQLException {
				
				OperacionDivisaDTO operacion = new OperacionDivisaDTO();
				
				operacion.setTipoOperacionDivTransf	(rs.getString("tipoOperacionDivTransf"));	
				operacion.setNoDocSap 				(rs.getString("noDocSap"));
				operacion.setCliente				(rs.getString("cliente"));
				operacion.setUsuario				(rs.getString("usuario"));
				operacion.setFolio					(rs.getString("folio"));
				operacion.setNoEmpresa				(rs.getInt("noEmpresa"));
				operacion.setNomEmpresa				(rs.getString("nomEmpresa"));
				operacion.setIdDivisaVenta			(rs.getString("idDivisaVenta"));
				operacion.setDescDivisaVenta		(rs.getString("descDivisaVenta"));
				operacion.setIdBancoCargo			(rs.getInt("idBancoCargo"));
				operacion.setDescBancoCargo			(rs.getString("descBancoCargo"));
				operacion.setChequeraCargo			(rs.getString("chequeraCargo"));
				operacion.setIdDivisaCompra			(rs.getString("idDivisaCompra"));
				operacion.setDescDivisaCompra 		(rs.getString("descDivisaCompra"));
				operacion.setIdBancoAbono			(rs.getInt("idBancoAbono"));
				operacion.setDescBancoAbono			(rs.getString("descBancoAbono"));
				operacion.setChequeraAbono			(rs.getString("chequeraAbono"));
				operacion.setIdCasaDeCambio			(rs.getInt("idCasaDeCambio"));
				operacion.setNomCasaDeCambio		(rs.getString("nomCasaDeCambio"));
				operacion.setIdBancoCasaCambio		(rs.getInt("idBancoCasaCambio"));
				operacion.setDescBancoCasaCambio	(rs.getString("descBancoCasaCambio"));
				operacion.setChequeraCasaCambio		(rs.getString("chequeraCasaCambio"));
				operacion.setIdOperador				(rs.getInt("idOperador"));
				operacion.setNomOperador			(rs.getString("nomOperador"));
				operacion.setFechaValor				(rs.getString("fechaValor"));
				operacion.setFechaLiquidacion		(rs.getString("fechaLiquidacion"));
				operacion.setImporteCompra			(rs.getDouble("importeCompra"));
				operacion.setTipoDeCambio			(rs.getDouble("tipoDeCambio"));
				operacion.setImporteVenta			(rs.getDouble("importeVenta"));
				operacion.setIdFormaPago			(rs.getInt("idFormaPago"));
				operacion.setDescFormaPago	 		(rs.getString("descFormaPago"));
				operacion.setConcepto				(rs.getString("concepto"));
				operacion.setReferencia				(rs.getString("referencia"));
				operacion.setFirma1					(rs.getString("firma1"));
				operacion.setFirma2					(rs.getString("firma2"));
				operacion.setIdGrupoEgreso			(rs.getInt("idGrupoEgreso"));
				operacion.setDescGrupoEgreso		(rs.getString("descGrupoEgreso"));
				operacion.setIdRubroEgreso			(rs.getInt("idRubroEgreso"));
				operacion.setDescRubroEgreso		(rs.getString("descRubroEgreso"));
				operacion.setIdGrupoIngreso			(rs.getInt("idGrupoIngreso"));
				operacion.setDescGrupoIngreso		(rs.getString("descGrupoIngreso"));
				operacion.setIdRubroIngreso			(rs.getInt("idRubroIngreso"));
				operacion.setDescRubroIngreso		(rs.getString("descRubroIngreso"));
				operacion.setNoProveedor 			(rs.getInt("noProveedor"));
				operacion.setNomProveedor			(rs.getString("nomProveedor"));
				operacion.setAbaOswift				(rs.getString("abaOswift"));
				operacion.setEstatusAutorizacion	(rs.getString("estatusAutorizacion"));				
				
				return operacion;
			}
		});
		
	}//END METHOD:traerOperacionesCompraVentaDeDivisasParaPago

	@Override
	public int autorizaOperacionesDivisas( int noUsuario, String folios){
		
		String sql = null; 
		StringBuilder sb = new StringBuilder();
		
		sb.append( "UPDATE CARTAS_CVD SET "	).append("\n");
		sb.append( "ESTATUS = 'A',"			).append("\n");
		sb.append( "NO_USUARIO_AUTORIZA = ?" 	).append("\n");
		sb.append( "WHERE no_docto in("		).append(folios).append(")").append("\n");
		
		sql = sb.toString(); 
		
		return jdbcTemplate.update(sql, new Object[]{ noUsuario } );
		
	} //END METHOD:autorizaOperacionesDivisas
	
	@Override
	public int traerFolioDetDivisas(String noDocto, int idTipoOperacion){
		
		String sql = "SELECT no_folio_det FROM MOVIMIENTO WHERE NO_DOCTO = ? and ID_TIPO_OPERACION = ? AND ORIGEN_MOV = 'CVD' AND ID_ESTATUS_MOV = 'C'";
			 
		Integer folio = (Integer)getJdbcTemplate().queryForObject(
				sql, new Object[] { noDocto, idTipoOperacion }, Integer.class);
		
		return folio;
			
	}//END METHOD:findCustomerNameById 
	
	@Override
	public int traerFolioDetDivisasIngreso(String noDocto, int idTipoOperacion){
		
		String sql = "SELECT no_folio_det FROM MOVIMIENTO WHERE NO_DOCTO = ? and ID_TIPO_OPERACION = ? AND ORIGEN_MOV = 'CVD' AND ID_ESTATUS_MOV = 'L'";
			 
		Integer folio = (Integer)getJdbcTemplate().queryForObject(
				sql, new Object[] { noDocto, idTipoOperacion }, Integer.class);
		
		return folio;
			
	}//END METHOD:findCustomerNameById 

	
	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public Map ejecutarPagador(StoreParamsComunDto dto)
	{
		
		Map<String, Object> resultado = null;
		
		try{
			StoredProcedure storedProcedure = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_sql_Pagador") {};
			storedProcedure.declareParameter(new SqlParameter("parametro",Types.VARCHAR ));
			storedProcedure.declareParameter(new SqlInOutParameter("mensaje",Types.VARCHAR));
			storedProcedure.declareParameter(new SqlInOutParameter("result",Types.INTEGER));
			
			Map< String, Object> inParams = new HashMap< String, Object >();
			
			System.out.println( "\n--PARAMETROS STORE--" );
			System.out.println( "PARAMETRO: "  + dto.getParametro());
			System.out.println( "MENSAJE  : "  + dto.getMensaje());
			System.out.println( "RESULTADO: "  + dto.getResult());
			
			inParams.put("parametro",dto.getParametro());
			inParams.put("mensaje",dto.getMensaje());
			inParams.put("result",dto.getResult());
			
			resultado = storedProcedure.execute( inParams );
				
		}catch (Exception e) {
			e.printStackTrace();
			return null; 
		}
		
		return resultado;
		
		
	}//END METHOD: ejecutarPagador
	
	@Override
	public int actualizaEjecucionOperacionesDivisas( int noUsuario, String folios){
		
		String sql = null; 
		StringBuilder sb = new StringBuilder();
		
		sb.append( "UPDATE CARTAS_CVD SET "	).append("\n");
		sb.append( "ESTATUS = 'E',"			).append("\n");
		sb.append( "NO_USUARIO_EJECUTA = "  ).append( noUsuario ).append("\n");
		sb.append( "WHERE no_docto in("	    ).append( folios    ).append(")").append("\n");
		
		sql = sb.toString(); 
		
		return jdbcTemplate.update(sql);
		
	} //END METHOD:autorizaOperacionesDivisas
	
	
	@Override
	public List<OperacionDivisaDTO> operacionesVentaDeDivisasParaImprimir( String folios ) {
		
		StringBuilder sb = new StringBuilder();
		String sql = null; 
		
		sb.append("SELECT"												).append("\n"); 
		sb.append("'CVT' AS tipoOperacionDivTransf,"					).append("\n"); 
		sb.append("'SNV' AS noDocSap, "									).append("\n");
		sb.append("'SNV' AS Cliente, "									).append("\n");
		sb.append("'SNV' AS usuario,"									).append("\n");
		sb.append("CVD.NO_DOCTO AS folio,"								).append("\n");
		sb.append("E.NO_EMPRESA AS noEmpresa,"							).append("\n");
		sb.append("E.NOM_EMPRESA AS nomEmpresa, "						).append("\n");
		sb.append("CVD.ID_DIVISA_VENTA AS idDivisaVenta, "				).append("\n");
		sb.append("CVD.DESC_DIVISA_PAGO AS descDivisaVenta,"			).append("\n");
		sb.append("CVD.id_banco_cargo AS  idBancoCargo,"				).append("\n");
		sb.append("CVD.nom_banco_cargo AS descBancoCargo,"				).append("\n");
		sb.append("CVD.id_chequera_cargo AS chequeraCargo, "			).append("\n");
		sb.append("CVD.id_divisa_abono AS idDivisaCompra,"				).append("\n");
		sb.append("CVD.desc_divisa_abono AS descDivisaCompra,"			).append("\n");
		sb.append("CVD.id_banco_abono AS idBancoAbono, "				).append("\n");
		sb.append("CVD.nom_banco_abono AS descBancoAbono,"				).append("\n");
		sb.append("CVD.id_chequera_abono AS chequeraAbono, "			).append("\n");
		sb.append("CVD.no_persona AS idCasaDeCambio, "					).append("\n");
		sb.append("CVD.nom_casa_cambio AS nomCasaDeCambio,"				).append("\n");
		sb.append("CVD.id_banco_casa_cambio AS idBancoCasaCambio, "		).append("\n");
		sb.append("CVD.desc_banco_casa_cambio AS descBancoCasaCambio ,"	).append("\n");
		sb.append("CVD.id_chequera_casa_cambio AS chequeraCasaCambio,"	).append("\n");
		sb.append("0 AS idOperador, "									).append("\n");
		sb.append("CVD.operador AS nomOperador, "						).append("\n");
		sb.append("CVD.fecha_valor AS fechaValor, "						).append("\n");
		sb.append("CVD.fecha_liquidacion AS fechaLiquidacion,"			).append("\n"); 
		sb.append("CVD.importe_compra AS importeCompra, "				).append("\n");
		sb.append("CVD.tipo_cambio AS tipoDeCambio, "					).append("\n");
		sb.append("CVD.importe_venta AS importeVenta, "					).append("\n");
		sb.append("0 AS idFormaPago, "									).append("\n");
		sb.append("'SNV' AS descFormaPago,"								).append("\n");
		sb.append("CVD.concepto AS concepto,"							).append("\n");
		sb.append("CVD.direccion  AS referencia,"						).append("\n");
		sb.append("'SNV' AS firma1, "									).append("\n");
		sb.append("'SNV' AS firma2, "									).append("\n");
		sb.append("CVD.id_grupo AS idGrupoEgreso, "						).append("\n");
		sb.append("CVD.desc_grupo AS descGrupoEgreso, "					).append("\n");
		sb.append("CVD.id_rubro AS idRubroEgreso, "						).append("\n");
		sb.append("CVD.desc_rubro AS descRubroEgreso, "					).append("\n");
		sb.append("0 AS idGrupoIngreso, "								).append("\n");
		sb.append("'SNV' AS descGrupoIngreso, "							).append("\n");
		sb.append("0 AS idRubroIngreso, "								).append("\n");
		sb.append("'SNV' AS descRubroIngreso,"							).append("\n");
		sb.append("0 AS noProveedor, "									).append("\n");
		sb.append("'SNV' AS nomProveedor, "								).append("\n");
		sb.append("'SNV' AS abaOswift,"									).append("\n");
		sb.append("estatus AS estatusAutorizacion"						).append("\n");
		sb.append("FROM CARTAS_CVD CVD"									).append("\n");
		sb.append("				LEFT JOIN EMPRESA E "					).append("\n");
		sb.append("				ON CVD.no_empresa = E.NO_EMPRESA "		).append("\n");
		sb.append("WHERE no_docto in(  "								).append("\n");
		sb.append(	folios	).append("\n");
		sb.append(")"													).append("\n");
		sb.append("AND ESTATUS IN( 'E')"								).append("\n");

		
		sql = sb.toString(); 
		
		
		return jdbcTemplate.query(sql, new RowMapper(){
			public OperacionDivisaDTO mapRow(ResultSet rs, int idx) throws SQLException {
				
				OperacionDivisaDTO operacion = new OperacionDivisaDTO();
				
				operacion.setTipoOperacionDivTransf	(rs.getString("tipoOperacionDivTransf"));	
				operacion.setNoDocSap 				(rs.getString("noDocSap"));
				operacion.setCliente				(rs.getString("cliente"));
				operacion.setUsuario				(rs.getString("usuario"));
				operacion.setFolio					(rs.getString("folio"));
				operacion.setNoEmpresa				(rs.getInt("noEmpresa"));
				operacion.setNomEmpresa				(rs.getString("nomEmpresa"));
				operacion.setIdDivisaVenta			(rs.getString("idDivisaVenta"));
				operacion.setDescDivisaVenta		(rs.getString("descDivisaVenta"));
				operacion.setIdBancoCargo			(rs.getInt("idBancoCargo"));
				operacion.setDescBancoCargo			(rs.getString("descBancoCargo"));
				operacion.setChequeraCargo			(rs.getString("chequeraCargo"));
				operacion.setIdDivisaCompra			(rs.getString("idDivisaCompra"));
				operacion.setDescDivisaCompra 		(rs.getString("descDivisaCompra"));
				operacion.setIdBancoAbono			(rs.getInt("idBancoAbono"));
				operacion.setDescBancoAbono			(rs.getString("descBancoAbono"));
				operacion.setChequeraAbono			(rs.getString("chequeraAbono"));
				operacion.setIdCasaDeCambio			(rs.getInt("idCasaDeCambio"));
				operacion.setNomCasaDeCambio		(rs.getString("nomCasaDeCambio"));
				operacion.setIdBancoCasaCambio		(rs.getInt("idBancoCasaCambio"));
				operacion.setDescBancoCasaCambio	(rs.getString("descBancoCasaCambio"));
				operacion.setChequeraCasaCambio		(rs.getString("chequeraCasaCambio"));
				operacion.setIdOperador				(rs.getInt("idOperador"));
				operacion.setNomOperador			(rs.getString("nomOperador"));
				operacion.setFechaValor				(rs.getString("fechaValor"));
				operacion.setFechaLiquidacion		(rs.getString("fechaLiquidacion"));
				operacion.setImporteCompra			(rs.getDouble("importeCompra"));
				operacion.setTipoDeCambio			(rs.getDouble("tipoDeCambio"));
				operacion.setImporteVenta			(rs.getDouble("importeVenta"));
				operacion.setIdFormaPago			(rs.getInt("idFormaPago"));
				operacion.setDescFormaPago	 		(rs.getString("descFormaPago"));
				operacion.setConcepto				(rs.getString("concepto"));
				operacion.setReferencia				(rs.getString("referencia"));
				operacion.setFirma1					(rs.getString("firma1"));
				operacion.setFirma2					(rs.getString("firma2"));
				operacion.setIdGrupoEgreso			(rs.getInt("idGrupoEgreso"));
				operacion.setDescGrupoEgreso		(rs.getString("descGrupoEgreso"));
				operacion.setIdRubroEgreso			(rs.getInt("idRubroEgreso"));
				operacion.setDescRubroEgreso		(rs.getString("descRubroEgreso"));
				operacion.setIdGrupoIngreso			(rs.getInt("idGrupoIngreso"));
				operacion.setDescGrupoIngreso		(rs.getString("descGrupoIngreso"));
				operacion.setIdRubroIngreso			(rs.getInt("idRubroIngreso"));
				operacion.setDescRubroIngreso		(rs.getString("descRubroIngreso"));
				operacion.setNoProveedor 			(rs.getInt("noProveedor"));
				operacion.setNomProveedor			(rs.getString("nomProveedor"));
				operacion.setAbaOswift				(rs.getString("abaOswift"));
				operacion.setEstatusAutorizacion	(rs.getString("estatusAutorizacion"));				
				
				return operacion;
			}
		});
		
	}//END METHOD:traerOperacionesCompraVentaDeDivisasParaPago

	@Override
	public ParametroDto obtenerIngresoOperacionDeDivisa(String folioMov, String noDocto) {
		
		StringBuilder sb = new StringBuilder();
		String sql = null; 
		
		sb.append("SELECT NO_EMPRESA AS NO_EMPRESA,"		).append("\n"); 
		sb.append("'folioParam'    AS FOLIO_PARAM,"			).append("\n");		
		sb.append("0               AS ID_TIPO_DOCTO,"		).append("\n");		
		sb.append("ID_FORMA_PAGO   AS ID_FORMA_PAGO,"		).append("\n");		
		sb.append("3101            AS ID_TIPO_OPERACION,"	).append("\n");		
		sb.append("'usuarioAlta'   AS USUARIO_ALTA,"		).append("\n");		
		sb.append("NO_EMPRESA      AS NO_CUENTA,"			).append("\n");		
		sb.append("0               AS NO_CLIENTE,"			).append("\n");		
		sb.append("'fechaHoy'      AS FECHA_VALOR, "		).append("\n");
		sb.append("'fechaHoy'      AS FECHA_VALOR_ORIGINAL,").append("\n");
		sb.append("'fechaHoy'      AS FECHA_OPERACION,"		).append("\n");
		sb.append("'fechaHoy'      AS FECHA_ALTA,"			).append("\n");		
		sb.append("IMPORTE         AS IMPORTE, "			).append("\n");
		sb.append("IMPORTE         AS IMPORTE_ORIGINAL,"	).append("\n");		
		sb.append("'noCaja'        AS NO_CAJA, "			).append("\n");		
		sb.append("ID_DIVISA       AS ID_DIVISA, "			).append("\n");
		sb.append("ID_DIVISA       AS ID_DIVISA_ORIGINAL,"	).append("\n");		
		sb.append("'CVD'           AS ORIGEN_MOV,"			).append("\n");
		sb.append("REFERENCIA      AS REFERENCIA,"			).append("\n");
		sb.append("CONCEPTO        AS CONCEPTO,"			).append("\n");		
		sb.append("1               AS APLICA,"				).append("\n");
	    sb.append("'A'             AS ID_ESTATUS_MOV,"		).append("\n");
		sb.append("'S'             AS SALVO_BUEN_COBRO,"	).append("\n");
		sb.append("'P'             AS ID_ESTATUS_REG,"		).append("\n");		
		sb.append("ID_BANCO        AS ID_BANCO,"			).append("\n");
		sb.append("ID_CHEQUERA     AS ID_CHEQUERA,"			).append("\n");		
		sb.append("0               AS FOLIO_BANCO,"			).append("\n");
		sb.append("'CVD'           AS ORIGEN_MOV,"			).append("\n");
		sb.append("CONCEPTO        AS OBSERVACION,"			).append("\n");		
		sb.append("1               AS IND_BOLSA,"			).append("\n");
		sb.append("0               AS FOLIO_MOV,"			).append("\n");
		sb.append("0               AS FOLIO_REF,"			).append("\n");
		sb.append("'folioParam'    AS GRUPO,"				).append("\n");
		sb.append("IMPORTE         AS IMPORTE_DESGLOZADO, "	).append("\n");
		sb.append("0               AS LOTE,"				).append("\n");
		sb.append("0               AS HORA_RECIBO,"			).append("\n");
		sb.append("0               AS DIVISION,"			).append("\n");
		sb.append("0               AS NO_RECIBO,"			).append("\n");
		sb.append("NO_DOCTO        AS NO_DOCTO,"			).append("\n");
		sb.append("ID_GRUPO        AS ID_GRUPO,"			).append("\n");
		sb.append("ID_RUBRO        AS ID_RUBRO"				).append("\n");
		sb.append("FROM MOVIMIENTO M"						).append("\n");
		sb.append("WHERE ORIGEN_MOV = 'CVD'"				).append("\n");
		sb.append("AND ID_TIPO_OPERACION = 1000"			).append("\n");
		sb.append("AND ID_ESTATUS_MOV = 'L'"				).append("\n");
		sb.append("AND NO_DOCTO = '" + noDocto + "'"		).append("\n");
												
		sql = sb.toString(); 
					
		return (ParametroDto) jdbcTemplate.queryForObject(sql, new RowMapper<ParametroDto>(){
			public ParametroDto mapRow(ResultSet rs, int idx) throws SQLException {
				
				ParametroDto paramDto = new ParametroDto();
				
	            paramDto.setNoEmpresa(rs.getInt("NO_EMPRESA"));
	            paramDto.setNoFolioParam(0);	            
	            paramDto.setIdTipoDocto(0);		            
	            paramDto.setIdFormaPago(rs.getInt("ID_FORMA_PAGO"));
	            paramDto.setIdTipoOperacion(rs.getInt("ID_TIPO_OPERACION"));
	            paramDto.setUsuarioAlta(0);	  
	            paramDto.setNoCuenta(rs.getInt("NO_CUENTA"));	            
	            paramDto.setNoCliente("0");
	            paramDto.setFecValor( new Date() );	            
	            paramDto.setFecValorOriginal(new Date());	            
	            paramDto.setFecOperacion(new Date());	            
	            paramDto.setFecAlta(new Date());	            
	            paramDto.setImporte(rs.getDouble("IMPORTE"));	            
	            paramDto.setImporteOriginal(rs.getDouble("IMPORTE_ORIGINAL"));	            
	            paramDto.setIdCaja(0);	    
	            paramDto.setIdDivisa(rs.getString("ID_DIVISA"));
	            paramDto.setIdDivisaOriginal(rs.getString("ID_DIVISA_ORIGINAL"));	            
	            paramDto.setOrigenReg(rs.getString("ORIGEN_MOV"));            
	            paramDto.setReferencia(rs.getString("REFERENCIA"));	            
	            paramDto.setConcepto(rs.getString("CONCEPTO"));	            
	            paramDto.setAplica(1);	       	            
	            paramDto.setIdEstatusMov("A");	            
	            paramDto.setBSalvoBuenCobro("S");	            
	            paramDto.setIdEstatusReg("P");	            	            
	            paramDto.setIdBanco(rs.getInt("ID_BANCO"));
	            paramDto.setIdChequera(rs.getString("ID_CHEQUERA"))	;	            
	            paramDto.setFolioBanco("0");	
	            paramDto.setOrigenMov(rs.getString("ORIGEN_MOV"));	            
	            paramDto.setObservacion(rs.getString("OBSERVACION"));	            
	            paramDto.setIdInvCbolsa(1);	            
	            paramDto.setNoFolioMov(0);
	            paramDto.setFolioRef(0);
	            paramDto.setGrupo(0);	            
	            paramDto.setImporteDesglosado(rs.getDouble("IMPORTE_ORIGINAL"));
	            paramDto.setLote(0);
	            paramDto.setHoraRecibo("0");
	            paramDto.setDivision("0");	           
	            paramDto.setNoRecibo(0);
	            paramDto.setNoDocto(rs.getString("NO_DOCTO"));
	            paramDto.setIdRubro(rs.getInt("ID_RUBRO"));
	            paramDto.setIdGrupo(rs.getInt("ID_GRUPO") );
				
				return paramDto;
				
			}
		});
				
		
		
	}//END METHOD: obtenerIngresoOperacionDeDivisa
	
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
	        sbSQL.append("\n  id_inv_cbolsa,no_folio_mov,folio_ref,grupo,importe_desglosado,lote,hora_recibo ");
	        sbSQL.append("\n  ,division");
	        sbSQL.append("\n  ,id_grupo ");
	        sbSQL.append("\n  ,id_rubro ");
	        sbSQL.append("\n  ,no_recibo ");
	        
	        if(!paramDto.getNoDocto().equals(""))
	            sbSQL.append("\n  ,no_docto ");
	        
	        
	        
            sbSQL.append("\n )");
	                
	        sbSQL.append("\n VALUES(" + paramDto.getNoEmpresa() + "," + paramDto.getNoFolioParam());
	        sbSQL.append("\n ," + paramDto.getIdTipoDocto() + "," + paramDto.getIdFormaPago() + "," + paramDto.getIdTipoOperacion() + ",");
	        sbSQL.append("\n " + paramDto.getUsuarioAlta() + "," + paramDto.getNoCuenta() + ",'" + paramDto.getNoCliente() + "'");
	        sbSQL.append("\n ,'" + funciones.ponerFechaSola(paramDto.getFecValor()) + "','" + funciones.ponerFechaSola(paramDto.getFecValorOriginal()) + "'");
	        sbSQL.append("\n ,'" + funciones.ponerFechaSola(paramDto.getFecOperacion()) + "','" + funciones.ponerFechaSola(paramDto.getFecAlta()) + "'," + paramDto.getImporte() + ",");
	        sbSQL.append("\n " + paramDto.getImporteOriginal() + "," + paramDto.getIdCaja() + ",'" + paramDto.getIdDivisa() + "','" + paramDto.getIdDivisaOriginal() + "','" + paramDto.getOrigenReg() + "',");
	        sbSQL.append("\n '" + paramDto.getReferencia() + "','" + paramDto.getConcepto() + "'," + paramDto.getAplica() + ",'" + paramDto.getIdEstatusMov() + "','" + paramDto.getBSalvoBuenCobro() + "',");
	        sbSQL.append("\n '" + paramDto.getIdEstatusReg() + "'," + paramDto.getIdBanco() + ",'" + paramDto.getIdChequera() + "','" + paramDto.getFolioBanco() + "','" + paramDto.getOrigenMov() + "','" + paramDto.getObservacion() + "',");
	        sbSQL.append("\n " + paramDto.getIdInvCbolsa() + "," + paramDto.getNoFolioMov() + "," + paramDto.getFolioRef() + "," + paramDto.getGrupo() + "," + paramDto.getImporteDesglosado() + "," + paramDto.getLote());
	        sbSQL.append("\n ,'" + paramDto.getHoraRecibo() + "','" + paramDto.getDivision() + "' ");
	        sbSQL.append("\n , '" + paramDto.getIdGrupo() + "'");
	        sbSQL.append("\n , '" + paramDto.getIdRubro() + "'");
	        sbSQL.append("\n , " + paramDto.getNoRecibo());
	        
	        if(!paramDto.getNoDocto().equals(""))
	            sbSQL.append("\n , '" + paramDto.getNoDocto() + "'");
	        
	            sbSQL.append("\n )");
	            
	            iInserta = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:insertarParametro");
		}
		return iInserta;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)	
	public Map<String, Object> ejecutarGenerador2(GeneradorDto dto)
	{
		Map<String, Object> resultado= new HashMap<String, Object>();
		try{
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			
			resultado = consultasGenerales.ejecutarGenerador(dto);
			
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
			+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:ejecutarGenerador U:"+dto.getIdUsuario()
			+ "	F:"+dto.getNomForma());
		}
		return resultado; 
	}
	
	
	
	@Override
	public int eliminaOperacionesDivisas( int noUsuario, Integer folios){
		
		String sql = null; 
		StringBuilder sb = new StringBuilder();
		
		sb.append( "UPDATE MOVIMIENTO SET "	).append("\n");
		sb.append( "ID_ESTATUS_MOV = 'X'"	).append("\n");
		sb.append( "WHERE NO_FOLIO_DET = ?" ).append("\n");
		
		sql = sb.toString(); 
		
		return jdbcTemplate.update(sql, new Object[]{ folios } );
		
	} //END METHOD:autorizaOperacionesDivisas
	
	@Override
	public int eliminaOperacionesTransfer( int noUsuario, Integer folios){
		
		String sql = null; 
		StringBuilder sb = new StringBuilder();
		
		sb.append( "UPDATE MOVIMIENTO SET "	).append("\n");
		sb.append( "CVE_CONTROL = NULL,"	).append("\n");
		sb.append( "FEC_PROPUESTA = NULL"	).append("\n");
		sb.append( "WHERE NO_FOLIO_DET = ?" ).append("\n");
		
		sql = sb.toString(); 
		
		return jdbcTemplate.update(sql, new Object[]{ folios } );
		
	} //END METHOD:autorizaOperacionesDivisas
	
	@Override
	public int eliminaEjecucionOperacionesDivisas( int noUsuario, String folios){
		
		String sql = null; 
		StringBuilder sb = new StringBuilder();
		
		sb.append( "UPDATE CARTAS_CVD SET "	).append("\n");
		sb.append( "ESTATUS = 'X',"			).append("\n");
		sb.append( "NO_USUARIO_EJECUTA = ?" 	).append("\n");
		sb.append( "WHERE no_docto in(?)"	).append("\n");
		
		sql = sb.toString(); 
		
		return jdbcTemplate.update(sql, new Object[]{ noUsuario, folios } );
		
	} //END METHOD:autorizaOperacionesDivisas
	
	public boolean validarUsuarioAutenticado(int idUsr,String psw){
		String sql="";
		int res=0;
		boolean valido=false;
		try{
			sql+="SELECT count(*) FROM seg_usuario";
			sql+="\n	where id_usuario="+idUsr;
			sql+="\n	and contrasena='"+psw+"'";
			
			res=jdbcTemplate.queryForInt(sql);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
			+ "P:Utilerias, C:ConsultasGenerales, M:validarUsuarioAutenticado");
			e.printStackTrace();
		}
		if(res>0)
			valido=true;
		else
			valido=false;
		
		return valido;
	}
	
	
	
	@Override
	public List<OperacionDivisaDTO> operacionesVentaDeTransferParaImprimir( String folios ) {
		
		StringBuilder sb = new StringBuilder();
		String sql = null; 
		
		sb.append("SELECT"												).append("\n");
		sb.append("E.NOM_EMPRESA AS nomEmpresa, "						).append("\n");
		sb.append("CVD.DESC_DIVISA_PAGO AS descDivisaVenta,"			).append("\n");
		sb.append("CVD.desc_divisa_abono AS descDivisaCompra,"			).append("\n");
		sb.append("CVD.nom_banco_abono AS descBancoAbono,"				).append("\n");
		sb.append("CVD.id_chequera_abono AS chequeraAbono, "			).append("\n");
		sb.append("CVD.importe_compra AS importeCompra, "				).append("\n");
		sb.append("CVD.tipo_cambio AS tipoDeCambio, "					).append("\n");
		sb.append("CVD.importe_venta AS importeVenta, "					).append("\n");
		sb.append("CVD.concepto AS nomProveedor,"						).append("\n");		
		sb.append("'SNV' AS abaOswift"									).append("\n");		
		sb.append("FROM CARTAS_CVD CVD"									).append("\n");
		sb.append("				LEFT JOIN EMPRESA E "					).append("\n");
		sb.append("				ON CVD.no_empresa = E.NO_EMPRESA "		).append("\n");
		sb.append("WHERE no_docto in(  "								).append("\n");
		sb.append(	folios	).append("\n");
		sb.append(")"													).append("\n");
		sb.append("AND ESTATUS IN( 'E')"								).append("\n");

		
		sql = sb.toString(); 
		
		
		return jdbcTemplate.query(sql, new RowMapper(){
			public OperacionDivisaDTO mapRow(ResultSet rs, int idx) throws SQLException {
				
				OperacionDivisaDTO operacion = new OperacionDivisaDTO();
				
				operacion.setNomEmpresa				(rs.getString("nomEmpresa"));
				operacion.setDescDivisaVenta		(rs.getString("descDivisaVenta"));				
				operacion.setDescDivisaCompra 		(rs.getString("descDivisaCompra"));				
				operacion.setDescBancoAbono			(rs.getString("descBancoAbono"));
				operacion.setChequeraAbono			(rs.getString("chequeraAbono"));
				operacion.setImporteCompra			(rs.getDouble("importeCompra"));
				operacion.setTipoDeCambio			(rs.getDouble("tipoDeCambio"));
				operacion.setImporteVenta			(rs.getDouble("importeVenta"));
				operacion.setNomProveedor			(rs.getString("nomProveedor"));
				operacion.setAbaOswift				(rs.getString("abaOswift"));				
				
				return operacion;
			}
		});
		
	}//END METHOD:traerOperacionesCompraVentaDeDivisasParaPago

	@Override
	public int updateSesionOperacionesCompraDeTransferParaPago() {
		
		String sql = null; 
		StringBuilder sb = new StringBuilder();
		
		sb.append( "UPDATE MOVIMIENTO SET "			).append("\n");
		sb.append( "COMENTARIO1 = 'N'"				).append("\n");
		sb.append( "WHERE id_tipo_operacion = 3000"	).append("\n");
		sb.append( "AND origen_mov = 'CVT'"			).append("\n");
		sb.append( "AND id_estatus_mov = 'N'"		).append("\n");
		
		sql = sb.toString(); 
		
		return jdbcTemplate.update( sql );
				
	}
	
	@Override
	public int updateCancelSesionOperacionesCompraDeTransferParaPago( String folios ) {
		
		String sql = null; 
		StringBuilder sb = new StringBuilder();
		
		
		sb.append( "UPDATE MOVIMIENTO SET "			).append("\n");
		sb.append( "COMENTARIO1 = null"				).append("\n");
		sb.append( "WHERE id_tipo_operacion = 3000"	).append("\n");
		sb.append( "AND origen_mov = 'CVT'"			).append("\n");
		sb.append( "AND id_estatus_mov = 'N'"		).append("\n");
		sb.append( "AND no_folio_det in(").append( folios ).append( ")" ).append("\n");
		
		sql = sb.toString(); 
		
		return jdbcTemplate.update( sql );
				
	}
	
	

	
}//END CLASS: GestionDeOperacionesDivisasDAOImpl
