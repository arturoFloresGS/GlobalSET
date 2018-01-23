package com.webset.set.flujos.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
//import java.util.Map;
import javax.sql.DataSource;
//import org.apache.log4j.Logger;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.flujos.action.MantenimientoDeRubrosAction;
import com.webset.set.flujos.dao.MantenimientoDeRubrosDao;
import com.webset.set.flujos.dto.MantenimientoDeRubrosDto;
import com.webset.set.utilerias.Bitacora;

@SuppressWarnings("unchecked")
public class MantenimientoDeRubrosDaoImpl implements MantenimientoDeRubrosDao{
	private JdbcTemplate jdbcTemplate;
	private Bitacora bitacora = new Bitacora();
	private static Logger logger = Logger.getLogger(MantenimientoDeRubrosAction.class);
	
	
	public List<MantenimientoDeRubrosDto> llenarComboGrupo(){
		StringBuffer sbSql = new StringBuffer();
		List<MantenimientoDeRubrosDto> listCr = new ArrayList<MantenimientoDeRubrosDto>();
		try{
			sbSql.append("Select ");
		    sbSql.append("\n	id_grupo,desc_grupo");
		    sbSql.append("\n	From ");
		    sbSql.append("\n	cat_grupo");
		    listCr = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
	        	public MantenimientoDeRubrosDto mapRow(ResultSet rs, int idx) throws SQLException	{
	        		MantenimientoDeRubrosDto dtoCtv = new MantenimientoDeRubrosDto();
	        			dtoCtv.setIdGrupo(rs.getInt("id_grupo"));
	        			dtoCtv.setDesripcion(rs.getString("desc_grupo"));
	        		return dtoCtv;
	        	}
	        });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Flujos, C:MantenimientoDeRubrosDaoImpl, M:llenarComboGrupo");
		}
		return listCr;
	}
	
	public List<MantenimientoDeRubrosDto> consultarRubro(int idGrupo){
		StringBuffer sbSql = new StringBuffer();
		List<MantenimientoDeRubrosDto> listConsRub = new ArrayList<MantenimientoDeRubrosDto>();
		try{
			sbSql.append("Select ");
		    sbSql.append("\n	id_grupo,id_rubro,desc_rubro,ingreso_egreso ");
		    sbSql.append("\n	From ");
		    sbSql.append("\n	cat_rubro ");
		    sbSql.append("\n	where id_grupo = "+ idGrupo +"");
		    listConsRub = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
	        	public MantenimientoDeRubrosDto mapRow(ResultSet rs, int idx) throws SQLException	{
	        		MantenimientoDeRubrosDto dtoCR = new MantenimientoDeRubrosDto();
		        		dtoCR.setIdGrupo(rs.getInt("id_grupo"));
		        		dtoCR.setIdRubro(rs.getInt("id_rubro"));
		        		dtoCR.setDescRubro(rs.getString("desc_rubro"));
		        		dtoCR.setIngresoEgreso(rs.getString("ingreso_egreso"));
	        		return dtoCR;
	        	}
	        });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Flujos, C:MantenimientoDeRubrosDaoImpl, M:consultarRubro");
		}
		return listConsRub;
	}
	
	public int accionRubro(List<Map<String, String>> gListRubro){
		StringBuffer sbSql = new StringBuffer();
		int iAfec = 0;
		try{
			
		        sbSql.append("insert into cat_rubro (id_grupo,id_rubro,desc_rubro,ingreso_egreso) values('" + gListRubro.get(0).get("idGrupo") + "','" + gListRubro.get(0).get("idRubro") + "','" + gListRubro.get(0).get("rubro") + "','" + gListRubro.get(0).get("ingesoEgreso") + "')");
		        iAfec = jdbcTemplate.update(sbSql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Flujos, C:MantenimientoDeRubrosDaoImpl, M:accionRubro");
		}
		return iAfec;
	}
	
	
	public int eliminarRubro(int grupo, int rubro){
		StringBuffer sbSql = new StringBuffer();
		int result = 0;
		try {
			sbSql.append(" DELETE FROM cat_rubro where id_grupo = " + grupo + " and id_rubro = " + rubro + "");
			result = jdbcTemplate.update(sbSql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:MantenimientoDeRubrosDaoImpl, M:eliminarRubro");	
			e.printStackTrace();
		}
		return result;
	}
	
	
	//set del data source
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Flujos, C:MantenimientoDeRubrosDaoImpl, M:setDataSource");
		}
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
