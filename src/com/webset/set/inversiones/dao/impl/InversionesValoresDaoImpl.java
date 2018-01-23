package com.webset.set.inversiones.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.webset.set.inversiones.dao.InversionesValoresDao;
import com.webset.set.inversiones.dto.CtasValoresDto;
import com.webset.set.inversiones.dto.MantenimientoValoresDto;
import com.webset.set.inversiones.dto.LlenaComboValoresDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dto.CuentaDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public class InversionesValoresDaoImpl  implements InversionesValoresDao {
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<MantenimientoValoresDto> consultarValores(){
		StringBuffer sbSql = new StringBuffer();
		List<MantenimientoValoresDto> listConsValores = new ArrayList<MantenimientoValoresDto>();
		try{
			sbSql.append("Select "			 ).append("\n");
			sbSql.append("ID_TIPO_VALOR,"    ).append("\n");  
			sbSql.append("DESC_TIPO_VALOR,"  ).append("\n");
			sbSql.append("B_ISR,"            ).append("\n");
			sbSql.append("ID_DIVISA,"        ).append("\n");
			sbSql.append("ID_GRUPO_INV,"     ).append("\n");        
			sbSql.append("ID_RUBRO_INV,"     ).append("\n");        
			sbSql.append("ID_GRUPO_REGR,"    ).append("\n");        
			sbSql.append("ID_RUBRO_REGR,"    ).append("\n");        
			sbSql.append("ID_GRUPO_INT,"     ).append("\n");        
			sbSql.append("ID_RUBRO_INT,"     ).append("\n");        
			sbSql.append("ID_GRUPO_ISR,"     ).append("\n");        
			sbSql.append("ID_RUBRO_ISR"     ).append("\n");
	        sbSql.append("FROM CAT_TIPO_VALOR ");
	  
	        listConsValores = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
	        	public MantenimientoValoresDto mapRow(ResultSet rs, int idx) throws SQLException{
	        		MantenimientoValoresDto dtoCons = new MantenimientoValoresDto();
	        		dtoCons.setIdValor    	   ( rs.getString("ID_TIPO_VALOR"  ) );
	        		dtoCons.setDescripcion	   ( rs.getString("DESC_TIPO_VALOR") );
	        		dtoCons.setIsr        	   ( rs.getString("B_ISR"          ) );
	        		dtoCons.setIdDivisa   	   ( rs.getString("ID_DIVISA"      ) );
	        		dtoCons.setIdGrupoOrden    ( rs.getDouble("ID_GRUPO_INV"   ) );
	        		dtoCons.setIdRubroOrden    ( rs.getDouble("ID_RUBRO_INV"   ) );
	        		dtoCons.setIdGrupoRegreso  ( rs.getDouble("ID_GRUPO_REGR"  ) );
	        		dtoCons.setIdRubroRegreso  ( rs.getDouble("ID_RUBRO_REGR"  ) );
	        		dtoCons.setIdGrupoIntereses( rs.getDouble("ID_GRUPO_INT"   ) );
	        		dtoCons.setIdRubroIntereses( rs.getDouble("ID_RUBRO_INT"   ) );
	        		dtoCons.setIdGrupoISR      ( rs.getDouble("ID_GRUPO_ISR"   ) );
	        		dtoCons.setIdRubroISR      ( rs.getDouble("ID_RUBRO_ISR"   ) );
	        		
	        		return dtoCons;
	        	}
	        });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Inversiones, C:InversionesValoresDaoImpl, M:consultarValores");
		}
		return listConsValores;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<LlenaComboValoresDto>  consultarDivisa(){
		List<LlenaComboValoresDto> listDiv = new ArrayList<LlenaComboValoresDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("Select  id_divisa,");
			sbSql.append("\n desc_divisa");
			sbSql.append("\n from cat_divisa ");
		    listDiv = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public LlenaComboValoresDto mapRow(ResultSet rs, int idx) throws SQLException	{
	        		LlenaComboValoresDto dtoDivisa = new LlenaComboValoresDto();
	        		dtoDivisa.setIdDiv(rs.getString("id_divisa"));
	        		dtoDivisa.setDescripcion(rs.getString("desc_divisa"));
	        		return dtoDivisa;
	        	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+" "+ Bitacora.getStackTrace(e)+"P:Inversiones C:InversionesValoresDaoImpl M:ConsultarDivisa");
		}
		return listDiv;	}

	public int insertarValores(List<MantenimientoValoresDto> dtoInsVal){
		
		StringBuffer sbSql = new StringBuffer();
		Object[] params = 
		{
			dtoInsVal.get(0).getIdValor(),
			dtoInsVal.get(0).getDescripcion(),
			dtoInsVal.get(0).getIsr(),
			dtoInsVal.get(0).getIdDivisa(),
			dtoInsVal.get(0).getIdGrupoOrden(),
			dtoInsVal.get(0).getIdRubroOrden(), 
			dtoInsVal.get(0).getIdGrupoRegreso(), 
			dtoInsVal.get(0).getIdRubroRegreso(), 
			dtoInsVal.get(0).getIdGrupoIntereses(), 
			dtoInsVal.get(0).getIdRubroIntereses(), 
			dtoInsVal.get(0).getIdGrupoISR(), 
			dtoInsVal.get(0).getIdRubroISR()
		};
		
		int iAfec = 0;
		
		try{
		        
			sbSql.append("Insert Into cat_tipo_valor(" ).append("\n");
			sbSql.append( "ID_TIPO_VALOR  ,"		   ).append("\n");
			sbSql.append( "DESC_TIPO_VALOR,"	       ).append("\n");
			sbSql.append( "B_ISR          ,"		   ).append("\n");
			sbSql.append( "ID_DIVISA      ,"		   ).append("\n");
			sbSql.append( "ID_GRUPO_INV   ,"		   ).append("\n");
			sbSql.append( "ID_RUBRO_INV   ,"		   ).append("\n");
			sbSql.append( "ID_GRUPO_REGR  ,"		   ).append("\n");
			sbSql.append( "ID_RUBRO_REGR  ,"		   ).append("\n");
			sbSql.append( "ID_GRUPO_INT   ,"		   ).append("\n");
			sbSql.append( "ID_RUBRO_INT   ,"		   ).append("\n");
			sbSql.append( "ID_GRUPO_ISR   ,"		   ).append("\n");
			sbSql.append( "ID_RUBRO_ISR   "			   ).append("\n");           
			sbSql.append( ") VALUES(      "			   ).append("\n");
			sbSql.append( "?,?,?,?,?,?,?,?,?,?,?,?"	   ).append("\n");
			sbSql.append( ")"			   			   ).append("\n");
					        
		    iAfec = jdbcTemplate.update(sbSql.toString(), params );

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Inversiones, C:InversionesValoresDaoImpl, M:insertarValores");
		}
		
		return iAfec;
		
	}
	
	public int modificarValores(List<MantenimientoValoresDto> dtoInsVal){
		StringBuffer sbSql = new StringBuffer();
		int iAfec = 0;
		
		Object[] params = {
			dtoInsVal.get(0).getDescripcion(), 
    		dtoInsVal.get(0).getIsr(),
    		dtoInsVal.get(0).getIdDivisa(),
    		dtoInsVal.get(0).getIdGrupoOrden(), 
    		dtoInsVal.get(0).getIdRubroOrden(), 
    		dtoInsVal.get(0).getIdGrupoRegreso(), 
    		dtoInsVal.get(0).getIdRubroRegreso(), 
    		dtoInsVal.get(0).getIdGrupoIntereses(),
    		dtoInsVal.get(0).getIdRubroIntereses(), 
    		dtoInsVal.get(0).getIdGrupoISR(), 
    		dtoInsVal.get(0).getIdRubroISR(),
    		dtoInsVal.get(0).getIdValor()
		};
		
		try{
			
	        sbSql.append( "UPDATE cat_tipo_valor SET" ).append( "\n" );
	        sbSql.append( "DESC_TIPO_VALOR = ?," 	  ).append( "\n" );
	        sbSql.append( "B_ISR           = ?,"	  ).append( "\n" );
	        sbSql.append( "ID_DIVISA       = ?,"      ).append( "\n" );
	        sbSql.append( "ID_GRUPO_INV    = ?,"	  ).append( "\n" );
			sbSql.append( "ID_RUBRO_INV    = ?,"	  ).append( "\n" );
			sbSql.append( "ID_GRUPO_REGR   = ?,"	  ).append( "\n" );
			sbSql.append( "ID_RUBRO_REGR   = ?,"	  ).append( "\n" );
			sbSql.append( "ID_GRUPO_INT    = ?,"	  ).append( "\n" );
			sbSql.append( "ID_RUBRO_INT    = ?,"	  ).append( "\n" );
			sbSql.append( "ID_GRUPO_ISR    = ?,"	  ).append( "\n" );
			sbSql.append( "ID_RUBRO_ISR    = ? "	  ).append( "\n" );  
			sbSql.append( "WHERE ID_TIPO_VALOR = ? "  ).append( "\n" );
		        
	        iAfec = jdbcTemplate.update(sbSql.toString(), params);
		        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Inversiones, C:InversionesValoresDaoImpl, M:modificarValores");
		}
		return iAfec;
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int eliminarValores(String sIdValor){
		StringBuffer sbSql = new StringBuffer();
		int iAfec = 0;
		try{
			sbSql.append("Delete  from cat_tipo_valor  where  id_tipo_valor =" +"'"+sIdValor+"'");
			iAfec = jdbcTemplate.update(sbSql.toString());
		}catch(Exception e){
		   	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Inversiones, C:InversionesValoresDaoImpl, M:eliminarValores");
		}
		return iAfec;
	}
		
	
	public int  buscaValores(List<MantenimientoValoresDto> listValores){
		StringBuffer sbSql = new StringBuffer();
		int iAfec = 0;
		try{
			 sbSql.append(" select count(*) from cat_tipo_valor  where id_tipo_valor= " +"'" +listValores.get(0).getIdValor()+"'"+  "");
		     iAfec = jdbcTemplate.queryForInt(sbSql.toString()); 
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Flujos, C:MantenimientoConceptosDaoImpl, M:buscaConceptos");
		}
		return iAfec;
	}

	//set del data source
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Inversiones, C:InversionesDaoImpl, M:setDataSource");
		}
	}

	public JdbcTemplate getJdbcTemplate() {
	    	return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		    this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public int existeValorAsociadoAPapel(String idValor) {
		
		StringBuilder sb = new StringBuilder(); 
		String sql = null; 
		Object[] params = {idValor};
		
		sb.append( "SELECT COUNT(*)" 			).append("\n");
		sb.append( "FROM CAT_PAPEL"  			).append("\n");
		sb.append( "WHERE ID_TIPO_VALOR = ?" 	).append("\n");
		
		sql = sb.toString(); 
		
		return jdbcTemplate.queryForInt(sql,params);
		
	}
	
}
