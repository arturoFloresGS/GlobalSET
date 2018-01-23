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
import com.webset.set.seguridad.dto.SegCatTipoBitacoraDto;

public class SegCatTipoBitacoraDao {
	private JdbcTemplate jdbcTemplate;
	private GeneralDao gral;
	/**
	 * Metodo que consulta la tabla de seg_cat_tipo_bitacora de acuerdo a los parametros
	 * de id_tipo_bitacora o descripcion  
	 * 
	 * @Author Sergio Vaca
	 *  
	 * Retorna una lista de objetos de tipo o solamente una tupla 
	 */
	
	public List<SegCatTipoBitacoraDto> consultar(SegCatTipoBitacoraDto bitacora) throws Exception{
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT id_tipo_bitacora, descripcion ");
		sb.append(" FROM seg_cat_tipo_bitacora ");
		ArrayList<String>cond = new ArrayList<String>();
		if(bitacora!=null){
			if(bitacora.getIdTipoBitacora()>0){
				cond.add("id_tipo_bitacora = " + bitacora.getIdTipoBitacora());
			}
			if((bitacora.getDescripcion()!=null) || !bitacora.getDescripcion().equals("")){
				cond.add("descripcion = '" + bitacora.getDescripcion() + "'" );
			}
			if(cond.size() > 0){
				sb.append(" WHERE " + StringUtils.join(cond, " AND "));
			}
		}
		sql=sb.toString();
		List <SegCatTipoBitacoraDto> bitacoras = jdbcTemplate.query(sql, new RowMapper<SegCatTipoBitacoraDto>(){
			public SegCatTipoBitacoraDto mapRow(ResultSet rs, int idx) throws SQLException {
				SegCatTipoBitacoraDto bitacora = new SegCatTipoBitacoraDto();
				bitacora.setIdTipoBitacora(rs.getInt("id_tipo_bitacora"));
				bitacora.setDescripcion(rs.getString("descripcion"));
			return bitacora;
			}});
		return bitacoras;
	}
	
	/**
	 * Actualiza la tabla de seg_cat_tipo_bitacora al bitacora en base a la informacion proporcionada en el objeto bitacora
	 * segun el campo id_tipo_bitacora
	 * retorna un entero
	**/
	public int modificar(SegCatTipoBitacoraDto bitacora) throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append(" UPDATE seg_cat_tipo_bitacora SET descripcion = ? ");
		sb.append(" WHERE id_tipo_bitacora = ? ");
				int res = jdbcTemplate.update(sb.toString(),
				new Object[]{bitacora.getDescripcion().toUpperCase(), bitacora.getIdTipoBitacora()}
				);
		return res;
	}
	
	/**
	 * Elimina de la tabla de seg_cat_tipo_bitacora el bitacora identificado por el id_tipo_bitacora 
	**/
	public int eliminar(int idBita)throws Exception{
		int res = jdbcTemplate.update("DELETE seg_cat_tipo_bitacora FROM  WHERE id_tipo_bitacora = " + idBita);
		return res;
	}

	/**
	 * Inserta un nuevo registro en seg_cat_tipo_bitacora 
	**/
	public int insertar(SegCatTipoBitacoraDto bitacora) throws Exception{
		StringBuffer sb = new StringBuffer();
		gral = new GeneralDao(jdbcTemplate);
		bitacora.setIdTipoBitacora(gral.getSeq("seg_cat_tipo_bitacora", "id_tipo_bitacora"));
		sb.append("INSERT INTO seg_cat_tipo_bitacora ( id_tipo_bitacora, descripcion ) ");
		sb.append(" VALUES ( ?, ? ) ");
		int res = jdbcTemplate.update(sb.toString(),
				new Object[]{bitacora.getIdTipoBitacora(), bitacora.getDescripcion().toUpperCase()
				}
		);
		return res;
	}
	/**
	 * 
	 * @param bitacora
	 * @return false si falta algun campo para la insercion o modificacion 
	 * true sino falta ningun dato
	 */
	public boolean noNulo(SegCatTipoBitacoraDto bitacora){
		if (bitacora == null 
			 || bitacora.getDescripcion()==null 
			 || bitacora.getDescripcion().equals("")) 
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
