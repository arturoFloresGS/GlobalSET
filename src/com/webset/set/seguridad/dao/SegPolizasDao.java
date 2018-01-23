package com.webset.set.seguridad.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.seguridad.dto.ComboUsuario;
import com.webset.set.seguridad.dto.PolizaDto;
import com.webset.set.seguridad.dto.UsuarioPolizaDto;
import com.webset.set.utilerias.Bitacora;

public class SegPolizasDao {
	private JdbcTemplate jdbcTemplate;
	private Bitacora bitacora= new Bitacora();
	
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<ComboUsuario> obtenerUsuario() {
		StringBuffer sb = new StringBuffer();		
		
		sb.append(" SELECT id_usuario, clave_usuario, (nombre + ' ' + apellido_paterno + ' ' + apellido_materno)as nombre, apellido_paterno, apellido_materno \n");
		sb.append(" FROM seg_usuario su, cat_caja cc, empresa e \n");
		sb.append(" WHERE su.id_caja = cc.id_caja \n");
		sb.append(" 	And su.no_empresa = e.no_empresa \n");
		
		List <ComboUsuario> usuarios = null;
		try{
			usuarios = jdbcTemplate.query(sb.toString(), new RowMapper<ComboUsuario>(){
			
			public ComboUsuario mapRow(ResultSet rs, int idx) throws SQLException {
				ComboUsuario usuario = new ComboUsuario();
				
				usuario.setIdUsuario(rs.getInt("id_usuario"));
				usuario.setNombre(rs.getString("nombre"));
				usuario.setClaveUsuario(rs.getString("clave_usuario"));
				
				return usuario;
			}});
		
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegPolizasDao, M:obtenerUsuario");
		}
		return usuarios;
	}

	public List<PolizaDto> obtenerPolizas(int idUsuario, boolean existe) {
		String sql = "SELECT ID_POLIZA, NOMBRE_POLIZA FROM ZIMP_POLIZAMANUAL WHERE ";
		List<PolizaDto> polizas=new ArrayList<PolizaDto>();
		if (!existe) { sql = sql + "NOT ";}
		sql = sql + "ID_POLIZA IN (SELECT distinct ID_POLIZA FROM USUARIO_POLIZA WHERE ID_USUARIO = " + idUsuario;
		sql =sql + " ) ORDER BY ID_POLIZA";
		try{
			 polizas = jdbcTemplate.query(sql, new RowMapper<PolizaDto>(){
				public PolizaDto mapRow(ResultSet rs, int idx) throws SQLException {
					PolizaDto poliza = new PolizaDto();
					poliza.setIdPoliza(rs.getInt("ID_POLIZA"));
					poliza.setNombrePolizas(rs.getString("NOMBRE_POLIZA"));
					return poliza;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegPolizasDao, M:obtenerPolizas");
		}
		return polizas;
	}

	public int asignarPoliza(int noUsuario, int idPoliza, boolean todos) {
		int res=-1;
		try{
			if(!todos)
			res = jdbcTemplate.update("INSERT INTO USUARIO_POLIZA (id_usuario, id_poliza) values (?,?)",
					new Object[]{noUsuario, idPoliza});
			else
			res = jdbcTemplate.update("INSERT INTO USUARIO_POLIZA (id_usuario, id_poliza) "
					+ "SELECT  "+noUsuario+",id_poliza FROM zimp_polizamanual "
					+ "WHERE NOT ID_POLIZA IN (SELECT distinct ID_POLIZA FROM USUARIO_POLIZA WHERE id_usuario="+noUsuario+") ");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			//+ "P:Seguridad, C:EmpresaDao, M:insertar");
			+ "P:Seguridad, C:SegPolizasDao, M:asignarPoliza");
		}
		return res;
	}
	
	public int eliminar(UsuarioPolizaDto dto, boolean todos) throws Exception{
		int res=-1;
		String sql = "DELETE FROM USUARIO_POLIZA WHERE id_usuario = " + dto.getNoUsuario();
		try{
			if (!todos)	sql = sql + " AND ID_POLIZA = " + dto.getIdPoliza();
			res = jdbcTemplate.update(sql);
		}catch(Exception e){
			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegPolizasDao, M:eliminar");
		}
		return res;
	}
}
