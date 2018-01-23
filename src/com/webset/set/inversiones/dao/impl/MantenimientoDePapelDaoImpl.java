package com.webset.set.inversiones.dao.impl;

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

import com.webset.set.utilerias.Bitacora;
import com.webset.set.inversiones.dao.MantenimientoDePapelDao;
import com.webset.set.inversiones.dto.MantenimientoDePapelDto;


@SuppressWarnings("unchecked")
public class MantenimientoDePapelDaoImpl implements MantenimientoDePapelDao {
	private JdbcTemplate jdbcTemplate;
	private Bitacora bitacora = new Bitacora();
	private static Logger logger = Logger.getLogger(MantenimientoDePapelDaoImpl.class);
	
	/**
	 * FunSQLSelect650
	 * Este método consulta los contratos, es utilizado en OrdenDeInversion.js
	 * @param iNoEmpresa
	 * @param bInternas
	 * @return
	 */
	
	public List<MantenimientoDePapelDto> consultarPapel(){
		StringBuffer sbSql = new StringBuffer();
		List<MantenimientoDePapelDto> listConsPap = new ArrayList<MantenimientoDePapelDto>();
		try{
		    sbSql.append("Select ");
		    sbSql.append("\n	cp.id_papel , ");
		    sbSql.append("\n	cp.id_tipo_valor, ");
		    sbSql.append("\n	tv.desc_tipo_valor ");
		    sbSql.append("\n	From ");
		    sbSql.append("\n	cat_papel cp,cat_tipo_valor tv  ");
		    sbSql.append("\n	Where ");
		    sbSql.append("\n	cp.id_tipo_valor = tv.id_tipo_valor");
		    listConsPap = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
	        	public MantenimientoDePapelDto mapRow(ResultSet rs, int idx) throws SQLException	{
	        		MantenimientoDePapelDto dtoCosP = new MantenimientoDePapelDto();
	        			dtoCosP.setIdPapel(rs.getString("id_papel"));
	        			dtoCosP.setIdTipoValor(rs.getString("id_tipo_valor"));
	        			dtoCosP.setDescripcion(rs.getString("desc_tipo_valor"));
	        		return dtoCosP;
	        	}
	        });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:MantenimientoDePapelDaoImpl, M:consultarPapel");
		}
		return listConsPap;
	}
	
	public List<MantenimientoDePapelDto> llenarComboTipoValor(){
		StringBuffer sbSql = new StringBuffer();
		List<MantenimientoDePapelDto> listCtv = new ArrayList<MantenimientoDePapelDto>();
		try{
			sbSql.append("Select ");
		    sbSql.append("\n	id_tipo_valor,desc_tipo_valor ");
		    sbSql.append("\n	From ");
		    sbSql.append("\n	cat_tipo_valor");
		    listCtv = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
	        	public MantenimientoDePapelDto mapRow(ResultSet rs, int idx) throws SQLException	{
	        		MantenimientoDePapelDto dtoCtv = new MantenimientoDePapelDto();
	        			dtoCtv.setIdTipoValor(rs.getString("id_tipo_valor"));
	        			dtoCtv.setDescripcion(rs.getString("desc_tipo_valor"));
	        		return dtoCtv;
	        	}
	        });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:MantenimientoDePapelDaoImpl, M:llenarComboTipoValor");
		}
		return listCtv;
	}
	
	public int modificarPapel(List<Map<String, String>> gListPapel, String confirm){
		StringBuffer sbSql = new StringBuffer();
		int iAfe = 0;
		try{
			sbSql.append("UPDATE cat_papel SET id_papel = '" + gListPapel.get(0).get("idPapel") + "', id_tipo_valor = '" + gListPapel.get(0).get("idTipoValor") + "'");
			sbSql.append("WHERE id_tipo_valor = '" + confirm + "'\n");
		    iAfe = jdbcTemplate.update(sbSql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:MantenimientoDePapelDaoImpl, M:modificarPapel");
		}
		return iAfe;
	}
	
	public int validaNuevoPapel(List<Map<String, String>> gListPapel){
		
		StringBuffer sbSql = new StringBuffer();
		Object[] params = { gListPapel.get(0).get("idPapel") };
		
		try{
			
			sbSql.append( "SELECT COUNT(*)"      ).append("\n");
			sbSql.append( "FROM   CAT_PAPEL"     ).append("\n");
			sbSql.append( "WHERE  ID_PAPEL = ?"  ).append("\n");
			
		    return jdbcTemplate.queryForInt( sbSql.toString(), params);
		        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:MantenimientoDePapelDaoImpl, M:nuevoPapel");
		}
		
		return 0;
		
	}
	
	public int nuevoPapel(List<Map<String, String>> gListPapel){
		StringBuffer sbSql = new StringBuffer();
		int iAfec = 0;
		try{
		        sbSql.append("insert into cat_papel values('" + gListPapel.get(0).get("idPapel") + "','" + gListPapel.get(0).get("idTipoValor") + "')");
		        iAfec = jdbcTemplate.update(sbSql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:MantenimientoDePapelDaoImpl, M:nuevoPapel");
		}
		return iAfec;
	}
	
	public int eliminarPapel(String papel) {
		StringBuffer sbSql = new StringBuffer();
		int result = 0;
		try {
			sbSql.append(" DELETE FROM cat_papel WHERE id_papel ='"+ papel +"'");
			result = jdbcTemplate.update(sbSql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientoDePapelDaoImpl, M:eliminarPapel");	
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
