package com.webset.set.seguridad.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
//import java.util.Date;
import java.util.List;

import javax.sql.DataSource;
//import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.seguridad.dao.GeneralDao;
import com.webset.set.seguridad.dto.SegBitacoraDto;
/**
 * 
 * @author Jessica Arelly
 *
 */
public class SegBitacoraDao {
	private JdbcTemplate jdbcTemplate;
	private GeneralDao gral;

	/**
	 * 
	 * Metodo que consulta la tabla de seg_bitacora de acuerdo al parametro
	 * de id_bitacora (por el momento)  
	 * 
	 * Retorna una lista de objetos de tipo o solamente una tupla 
	 *
	 * @param bitacora
	 * @return
	 * @throws Exception
	 */
	
	
	public List<SegBitacoraDto> consultar(SegBitacoraDto bitacora) throws Exception{
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT id_bitacora, fecha, id_tipo_bitacora, id_error, descripcion, ");
		sb.append(" 		id_usuario, clave_usuario" );
		
		sb.append(" FROM seg_bitacora");
		ArrayList<String>cond = new ArrayList<String>();
		if(bitacora!=null){
			if(bitacora.getIdBitacora() > 0){
				cond.add(" id_bitacora = " + bitacora.getIdBitacora());
			}
			
		}
		sql=sb.toString();
		List <SegBitacoraDto> bitacoras = jdbcTemplate.query(sql, new RowMapper<SegBitacoraDto>(){
			public SegBitacoraDto mapRow(ResultSet rs, int idx) throws SQLException {
				SegBitacoraDto bitacora = new SegBitacoraDto();
				bitacora.setIdBitacora(rs.getInt("id_bitacora"));
				bitacora.setIdError(rs.getInt("id_error"));
				bitacora.setIdTipoBitacora(rs.getInt("id_tipo_bitacora"));
				bitacora.setFecha(rs.getDate("fecha"));
				bitacora.setDescripcion(rs.getString("descripcion"));
				bitacora.setIdUsuario(rs.getInt("id_usuario"));
				bitacora.setClaveUsuario(rs.getString("clave_usuario"));
				
			return bitacora;
			}});
		return bitacoras;
	}
	
	/**
	 * Actualiza la tabla de seg_bitacora en base a la informacion proporcionada en el objeto bitacora
	 * segun el campo id_bitacora
	 * retorna un entero
	 * @param bitacora
	 * @return
	 * @throws Exception
	 */
	public int modificar(SegBitacoraDto bitacora) throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append(" UPDATE seg_bitacora SET id_error = ?, id_tipo_bitacora = ?, fecha = ?, descripcion = ?, id_usuario = ?, ");
		sb.append(" clave_usuario = ? WHERE id_bitacora = ? ");
		int res = jdbcTemplate.update(sb.toString(),
			new Object[]{bitacora.getIdBitacora(), bitacora.getIdError(), bitacora.getIdTipoBitacora(), bitacora.getFecha(), bitacora.getDescripcion().toUpperCase(),
						bitacora.getIdUsuario(), bitacora.getClaveUsuario().toUpperCase()
						}
			);
		return res;
	}
	
	/**
	 * Elimina de la tabla de seg_bitacora el registro identificado por el id_bitacora 
	 * @param bitacora
	 * @return
	 * 	@throws Exception
	 */	
	public int eliminar(int bitacora)throws Exception{
		int res = jdbcTemplate.update("DELETE seg_bitacora FROM  WHERE id_bitacora = " + bitacora);
		return res;
	}
	
	/**
	 * Inserta un nuevo registro en seg_bitacora
	 * @param bitacora
	 * @return
	 * @throws Exception
	 */
	public int insertar(SegBitacoraDto bitacora) throws Exception{
		StringBuffer sb = new StringBuffer();
		gral = new GeneralDao(jdbcTemplate);
		bitacora.setIdBitacora(gral.getSeq("seg_bitacora", "id_bitacora"));
		sb.append(" INSERT INTO seg_bitacora( id_bitacora, id_error, id_tipo_bitacora, fecha, descripcion ");
		sb.append("			id_usuario, clave_usuario )");
		sb.append(" VALUES ( ?, ?, ?, ?, ?, ");
		sb.append(" 		?, ? ) ");
		int res = jdbcTemplate.update(sb.toString(),
			new Object[]{bitacora.getIdBitacora(), bitacora.getIdError(), bitacora.getIdTipoBitacora(), bitacora.getFecha(), bitacora.getDescripcion().toUpperCase(),
			bitacora.getIdUsuario(), bitacora.getClaveUsuario().toUpperCase(), 0
				}
		);
		return res;
	}
	
	/**
	 * para saber si no falta algun parametro
	 * @param usuario
	 * @return true o false si false faltan datos si true todo bien
	 */
	public boolean noNulo(SegBitacoraDto bitacora){
		if(bitacora==null 
			|| bitacora.getIdError()<1
			|| bitacora.getIdTipoBitacora()<1
			|| bitacora.getIdUsuario()<1
			|| bitacora.getDescripcion()==null	
			|| bitacora.getDescripcion().equals("")
			|| bitacora.getClaveUsuario()==null
			|| bitacora.getClaveUsuario().equals(""))
			return false;
		else
			return true;
	}
	
	//getters && setters
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}


}
