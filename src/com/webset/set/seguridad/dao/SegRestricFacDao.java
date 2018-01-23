package com.webset.set.seguridad.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.seguridad.dto.SegRestricFacDto;

public class SegRestricFacDao {
	private JdbcTemplate jdbcTemplate; 
	
	
	public List<SegRestricFacDto>consultar(SegRestricFacDto datosRestric) throws Exception{
		String sql = "SELECT * FROM seg_restricfac";
		ArrayList<String>cond = new ArrayList<String>();
		
		if(datosRestric.getNRestriccion()!= 0 ){
			cond.add("c_empresa = '" + datosRestric.getNRestriccion() + "'");
		}
		if(cond.size() > 0){
			sql += " WHERE " + StringUtils.join(cond, " AND ");
		}
		
		
		List<SegRestricFacDto> datConsulta = jdbcTemplate.query(sql, new RowMapper<SegRestricFacDto>(){
			public SegRestricFacDto mapRow(ResultSet rs, int idx) throws SQLException {
				SegRestricFacDto usr = new SegRestricFacDto();
			 	return usr;
			}});
		
		return datConsulta;
		
	}
	
	/**
	 * Actualiza seg_restricFac en base a la informacion proporcionada en el objeto 
	 * @param datosRestriccion objeto que contiene la informacion a ser actualizada
	 * @return res numero de registros aceptados. Generalmente 1 si se pudo insertar el 
	 * registro y 0 si no pudo realizarse la operacion
	 * @throws Exception
	 */
	public int modificar(SegRestricFacDto datosRestriccion) throws Exception{
		int res = jdbcTemplate.update("UPDATE seg_restricfac SET n_restriccion = ?, " +
				"f_hora_inicial= ?,f_hora_final=?, f_vencimiento=? where n_restriccion= ?",
				new Object[]{datosRestriccion.getNRestriccion(),datosRestriccion.getFHoraInicial(),
				datosRestriccion.getFHoraFinal(),datosRestriccion.getFVencimiento()}
		);
		return res;
		
	}
	
	/**
	 * Elimina la restriccion idetificada por clave
	 * @param c_empresa idetificador de la restriccion a eliminar a eliminar.  
	 * @return numero de registros afectados. Generalmente 1 si el registro fue eliminado 
	 * y 0 si no pudo realizarse la operacion
	 */
	public int eliminar(String n_restriccion) throws Exception{
		int res = jdbcTemplate.update("DELETE FROM seg_restricfac WHERE ID = " + n_restriccion);
		return res;
	}
	
	/**
	 * Inserta un registro en la tabla seg_restricfac. 
	 * @param  datosRestriccion objeto que contiene los datos que seran insertados
	 * @return numero de registros afectados por la operacion. Generalmente 1 si se pudo realizar la insercion o otro numero en cualquier otro caso
	 * @throws Exception
	 */
	public int insertar(SegRestricFacDto datosRestriccion) throws Exception{
		int res = jdbcTemplate.update("INSERT INTO seg_restricfac(n_restriccion, f_hora_inicial, " +
				"f_hora_final,f_vencimiento) values(?,?,?,?)",
				new Object[]{datosRestriccion.getNRestriccion(),datosRestriccion.getFHoraInicial(),
				datosRestriccion.getFHoraFinal(),datosRestriccion.getFVencimiento()}
		);
		return res;
	}
	
	//getters && setters
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
}
