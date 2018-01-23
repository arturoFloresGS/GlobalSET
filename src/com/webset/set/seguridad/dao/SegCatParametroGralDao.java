package com.webset.set.seguridad.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.seguridad.dao.GeneralDao;
import com.webset.set.seguridad.dto.SegCatParametroGralDto;

/**
 * 
 * * @Author Sergio Vaca
 *
 */
public class SegCatParametroGralDao {
	private JdbcTemplate jdbcTemplate;
	private GeneralDao gral;
	/**
	 * Metodo que consulta la tabla de seg_cat_parametro_gral de acuerdo a los parametros
	 * de id_parametro_general o descripcion  
	 * 
	 *  
	 * Retorna una lista de objetos de tipo o solamente una tupla 
	 */
	
	public List<SegCatParametroGralDto> consultar(SegCatParametroGralDto parametro) throws Exception{
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT id_parametro_general, descripcion, valor ");
		sb.append(" FROM seg_cat_parametro_gral ");
		ArrayList<String>cond = new ArrayList<String>();
		if(parametro!=null){
			if(parametro.getIdParametroGeneral()>0){
				cond.add("id_parametro_general = " + parametro.getIdParametroGeneral());
			}
			if((parametro.getDescripcion()!=null) || !parametro.getDescripcion().equals("")){
				cond.add("descripcion = '" + parametro.getDescripcion() + "'" );
			}
			if(cond.size() > 0){
				sb.append(" WHERE " + StringUtils.join(cond, " AND "));
			}
		}
		sql=sb.toString();
		List <SegCatParametroGralDto> parametros = jdbcTemplate.query(sql, new RowMapper<SegCatParametroGralDto>(){
			public SegCatParametroGralDto mapRow(ResultSet rs, int idx) throws SQLException {
				SegCatParametroGralDto para = new SegCatParametroGralDto();
				para.setIdParametroGeneral(rs.getInt("id_parametro_general"));
				para.setDescripcion(rs.getString("descripcion"));
				para.setValor(rs.getString("valor"));
			return para;
			}});
		return parametros;
	}
	
	/**
	 * Actualiza la tabla de seg_cat_parametro_gral al parametro en base a la informacion proporcionada en el objeto parametro
	 * segun el campo id_parametro_general
	 * retorna un entero
	**/
	public int modificar(SegCatParametroGralDto parametro) throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append(" UPDATE seg_cat_parametro_gral SET descripcion = ?, valor = ? ");
		sb.append(" WHERE id_parametro_general = ? ");
				int res = jdbcTemplate.update(sb.toString(),
				new Object[]{parametro.getDescripcion().toUpperCase(), parametro.getValor().toUpperCase(), parametro.getIdParametroGeneral()}
				);
		return res;
	}
	
	/**
	 * Elimina de la tabla de seg_cat_parametro_gral el parametro identificado por el id_parametro_general 
	**/
	public int eliminar(int idPara)throws Exception{
		int res = jdbcTemplate.update("DELETE seg_cat_parametro_gral FROM  WHERE id_parametro_general = " + idPara);
		return res;
	}

	/**
	 * Inserta un nuevo registro en seg_cat_parametro_gral 
	**/
	public int insertar(SegCatParametroGralDto parametro) throws Exception{
		StringBuffer sb = new StringBuffer();
		gral = new GeneralDao(jdbcTemplate);
		parametro.setIdParametroGeneral(gral.getSeq("seg_cat_parametro_gral", "id_parametro_general"));
		sb.append("INSERT INTO seg_cat_parametro_gral ( id_parametro_general, descripcion, valor ) ");
		sb.append(" VALUES ( ?, ?, ? ) ");
		int res = jdbcTemplate.update(sb.toString(),
				new Object[]{parametro.getIdParametroGeneral(), parametro.getDescripcion().toUpperCase(), parametro.getValor().toUpperCase()
				}
		);
		return res;
	}
	/**
	 * 
	 * @param parametro
	 * @return false si falta algun campo para la insercion o modificacion 
	 * true sino falta ningun dato
	 */
	public boolean noNulo(SegCatParametroGralDto parametro){
		if (parametro.getDescripcion() == null 
			 || parametro.getDescripcion()==null 
			 || parametro.getDescripcion().equals("") 
			 || parametro.getValor().equals(""))
			return false;
		else
			return true;
	}
	/**
	 * 
	 * @return
	 */
	//getters && setters
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

}
