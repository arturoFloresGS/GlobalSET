package com.webset.set.inversiones.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.utilerias.Bitacora;
import com.webset.set.inversiones.dao.InversionesComunDao;
import com.webset.set.inversiones.dto.LlenaComboValoresDto;


public class InversionesComunDaoImpl implements InversionesComunDao {
	private JdbcTemplate jdbcTemplate;
	private Bitacora bitacora = new Bitacora();
	private static Logger logger = Logger.getLogger(InversionesComunDaoImpl.class);

	@Override
	public List<LlenaComboValoresDto> consultarInstitucion() {
		StringBuffer sbSql = new StringBuffer();
		List <LlenaComboValoresDto> lista = null;

		logger.debug("Entra consultarInstitucion");
		
		//sbSql.append("SELECT b.equivale_persona, b.razon_social ");
		sbSql.append("SELECT b.no_persona, b.razon_social ");
		sbSql.append("FROM CAT_INST_FIN a, persona b ");
		//sbSql.append("WHERE a.NO_PROV_INST = b.equivale_persona ");
		sbSql.append("WHERE a.NO_PROV_INST = b.no_persona ");
		sbSql.append("\n 	  AND b.ID_TIPO_PERSONA = 'P'");
		sbSql.append("AND b.id_estatus_per = 'A'");
		
		try{
			lista = jdbcTemplate.query(sbSql.toString(), new RowMapper<LlenaComboValoresDto>(){
				public LlenaComboValoresDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboValoresDto cons = new LlenaComboValoresDto();
					cons.setIdDiv(rs.getString("no_persona"));
					cons.setDescripcion(rs.getString("razon_social"));
					return cons;
				}});
		}catch (Exception e){
			logger.error(e.getLocalizedMessage());
		}
		
		return lista;
	}
	
	@Override
	public List<LlenaComboValoresDto> consultarTipoValor() {
		StringBuffer sbSql = new StringBuffer();
		List <LlenaComboValoresDto> lista = null;

		logger.debug("Entra consultarInstitucion");
		
		sbSql.append("SELECT id_tipo_valor, desc_tipo_valor ");
		sbSql.append("FROM cat_tipo_valor ");
		
		try{
			lista = jdbcTemplate.query(sbSql.toString(), new RowMapper<LlenaComboValoresDto>(){
				public LlenaComboValoresDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboValoresDto cons = new LlenaComboValoresDto();
					cons.setIdDiv(rs.getString("id_tipo_valor"));
					cons.setDescripcion(rs.getString("desc_tipo_valor"));
					return cons;
				}});
		}catch (Exception e){
			logger.error(e.getLocalizedMessage());
		}
		
		return lista;
	}

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.dao.InversionesComunDao#consultarDivisa()
	 */
	@Override
	public List<LlenaComboValoresDto> consultarDivisa() {
		StringBuffer sbSql = new StringBuffer();
		List <LlenaComboValoresDto> lista = null;

		logger.debug("Entra consultarDivisa");
		
		sbSql.append("SELECT id_divisa, desc_divisa ");
		sbSql.append("FROM cat_divisa ");
		
		try{
			lista = jdbcTemplate.query(sbSql.toString(), new RowMapper<LlenaComboValoresDto>(){
				public LlenaComboValoresDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboValoresDto cons = new LlenaComboValoresDto();
					cons.setIdDiv(rs.getString("id_divisa"));
					cons.setDescripcion(rs.getString("desc_divisa"));
					return cons;
				}});
		}catch (Exception e){
			logger.error(e.getLocalizedMessage());
		}
		
		return lista;
	}

	//set del data source
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:MantenimientoDePapelDaoImpl, M:setDataSource");
		}
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
