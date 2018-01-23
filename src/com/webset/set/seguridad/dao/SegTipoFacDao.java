package com.webset.set.seguridad.dao;
/**
 * Esta clase representa a la tabla seg_tipofac en el modulo Seguridad
 * @author Cristian Garcia Garcia
 */
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.seguridad.dto.SegTipoFacDto;


public class SegTipoFacDao {
	
	private JdbcTemplate jdbcTemplate;
	
	
	public List<SegTipoFacDto>consultar(SegTipoFacDto datTipoFac) throws Exception{
		String sql = "SELECT * FROM seg_tipofac";
		ArrayList<String>cond = new ArrayList<String>();
		
		if(datTipoFac.getTTipoFacultad() != null && !datTipoFac.getTTipoFacultad().trim().equals("")){
			cond.add("t_tipo_facultad = '" + datTipoFac.getTTipoFacultad() + "'");
		}
		if(cond.size() > 0){
			sql += " WHERE " + StringUtils.join(cond, " AND ");
		}	
		
		List<SegTipoFacDto> tipoFacultad = jdbcTemplate.query(sql, new RowMapper<SegTipoFacDto>(){
			public SegTipoFacDto mapRow(ResultSet rs, int idx) throws SQLException {
				SegTipoFacDto usr = new SegTipoFacDto();
				usr.setTTipoFacultad(rs.getString("t_tipo_facultad"));
				usr.setDDescripcion(rs.getString("d_descripcion"));
				usr.setBBloqueado(rs.getString("b_bloqueado"));
				return usr;
			}});
		
		return  tipoFacultad;
		
	}
	
	/**
	 * Actualiza seg_tipofac en base a la informacion proporcionada en el objeto datFacultad
	 * @param datTipoFac objeto que contiene la informacion a ser actualizada
	 * @return res numero de registros aceptados. Generalmente 1 si se pudo insertar el 
	 * registro y 0 si no pudo realizarse la operacion
	 * @throws Exception
	 */
	public int modificar(SegTipoFacDto datTipoFac) throws Exception{
		int res = jdbcTemplate.update("UPDATE seg_facultad SET t_tipo_facultad= ?,d_descripcion= ?, " +
				"b_bloqueado=?",
				new Object[]{datTipoFac.getTTipoFacultad(),datTipoFac.getDDescripcion(),
				datTipoFac.getBBloqueado()}
		);
		return res;
		
	}
	
	/**
	 * Elimina el registro en seg_facultad  idetificado por c_facultad
	 * @param c_facultad idetificador del registro a eliminar.  
	 * @return numero de registros afectados. Generalmente 1 si el registro fue eliminado 
	 * y 0 si no pudo realizarse la operacion
	 */
	public int eliminar(String c_facultad) throws Exception{
		int res = jdbcTemplate.update("DELETE FROM seg_facultad WHERE ID = " + c_facultad);
		return res;
	}
	
	/**
	 * Inserta un registro en la tabla seg_facultad. 
	 * @param datFacultad objeto que tiene los datos que seran insertados
	 * @return numero de registros afectados por la operacion. Generalmente 1 si se pudo realizar la insercion o otro numero en cualquier otro caso
	 * @throws Exception
	 */
	public int insertar(SegTipoFacDto datFacultad) throws Exception{
		int res = jdbcTemplate.update("INSERT INTO seg_tipofac(t_tipo_facultad,d_descripcion, " +
				"b_bloqueado values(?,?,?)",
				new Object[]{datFacultad.getTTipoFacultad(),datFacultad.getDDescripcion(),
				datFacultad.getBBloqueado()}
		);
		return res;
	}
	//getters && setters
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
}
