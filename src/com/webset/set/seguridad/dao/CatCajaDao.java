package com.webset.set.seguridad.dao;

import com.webset.set.seguridad.dto.CajaUsuarioDto;
import com.webset.set.seguridad.dto.CatCajaDto;
import com.webset.set.utilerias.Bitacora;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

/**
 * 
 * @author vtello
 * @Table Select, Insert, Delete a usuario_empresa 
 */
public class CatCajaDao {
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora =new Bitacora();
	/**
	 * Metodo que consulta la tabla caja_usuario en base a los parametros.
	 * noUsuario y trae resultados de la tabla de cat_caja.
	 * @param "noUsuario" del que se traeran los resultados de la tabla caja_usuario y "existe" para saber si requiere 
	 * 		de los datos asignados o pendientes por asignar al usuario.
	 * @return lista con todas las cajas que tiene o se le pueden asignar al usuario. 
	 * @throws Exception
	 */
	
	public List<CatCajaDto> consultar(int noUsuario, boolean existe) throws Exception{
		List<CatCajaDto> cajas=null;
		String sql = "SELECT id_caja, desc_caja FROM CAT_CAJA WHERE ";
		if (!existe) { sql = sql + "NOT "; }
		sql = sql + "id_caja IN (SELECT distinct id_caja FROM CAJA_USUARIO WHERE no_usuario = " + noUsuario;
		sql =sql + " ) ORDER BY id_caja";
		
		try{
			 cajas = jdbcTemplate.query(sql, new RowMapper<CatCajaDto>(){
			public CatCajaDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatCajaDto caja = new CatCajaDto();
				caja.setIdCaja(rs.getInt("id_caja"));
				caja.setDescCaja(rs.getString("desc_caja"));
				return caja;
			}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:CatCajaDao, M:consultar");
		}
		return cajas;
	}
	
	/**
	 * Inserta un registro en la tabla de caja_usuario. Los datos se obtienen del objeto proporcionado
	 * como parametro. 
	 * @param usuario objeto que sera insertado en la BD
	 * @return numero de registros afectados por la operacion
	 * @throws Exception
	 */
	public int insertar(CajaUsuarioDto usuario, boolean todos) throws Exception{
		int res=0;
		try{
			if(!todos)
			res = jdbcTemplate.update("INSERT INTO CAJA_USUARIO (no_usuario, id_caja) values (?,?)",
					new Object[]{usuario.getNoUsuario(), usuario.getIdCaja()});
			else
			res = jdbcTemplate.update("INSERT INTO CAJA_USUARIO SELECT " + usuario.getNoUsuario()+ " as no_usuario, id_caja FROM cat_caja " +
					  "WHERE NOT id_caja IN (SELECT distinct id_caja FROM caja_usuario WHERE no_usuario = " + usuario.getNoUsuario() + ")");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:CatCajaDao, M:insertar");
		}
		return res;
	}
	
	
	/**
	 * Elimina al usuario junto con la caja que tiene asignado.  
	 * @param noUsuario idetificador del usuario y la idCaja identificador de la caja que se va a eliminar.  
	 * @return numero de registros afectados. Generalmente 1 si el registro fue eliminado y 0 si no pudo realizarse la operacion
	 */
	public int eliminar(CajaUsuarioDto dto, boolean todos) throws Exception{
		int res=0;
		String sql = "DELETE FROM CAJA_USUARIO WHERE no_usuario = " + dto.getNoUsuario();
		try{
			if (!todos)	sql = sql + " AND id_caja = " + dto.getIdCaja();
			res = jdbcTemplate.update(sql);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:CatCajaDao, M:eliminar");
		}
		return res;
	}
	
	
	public List<CatCajaDto> obtenerTodasCajas() throws Exception{
		List<CatCajaDto> cajas=null;
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT id_caja, desc_caja FROM CAT_CAJA ");
			
			cajas = jdbcTemplate.query(sql.toString(), new RowMapper<CatCajaDto>(){
			public CatCajaDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatCajaDto caja = new CatCajaDto();
				caja.setIdCaja(rs.getInt("id_caja"));
				caja.setDescCaja(rs.getString("desc_caja"));
				return caja;
			}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:CatCajaDao, M:obtenerTodasCajas");
		}
		return cajas;
	}
	
	//getters && setters
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
}
