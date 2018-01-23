package com.webset.set.seguridad.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.seguridad.dto.CatOrigenMovDto;
import com.webset.set.seguridad.dto.OrigenUsuarioDto;
import com.webset.set.utilerias.Bitacora;

/**
 * 
 * @author vtello
 *
 */
public class CatOrigenMovDao {
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora=new Bitacora();
	
	/**
	 * Metodo que consulta la tabla origen_usuario en base a los parametros.
	 * noUsuario y trae resultados de la tabla cat_origen_mov.
	 * @param "noUsuario" del que se traeran los resultados de la tabla origen_usuario y "existe" para saber si requiere 
	 * 		de los datos asignados o pendientes por asignar al usuario.
	 * @return lista con todos los origenes de movimientos que tiene o se le pueden asignar al usuario. 
	 * @throws Exception
	 */
	
	public List<CatOrigenMovDto> consultar(int noUsuario, boolean existe) throws Exception{
		String sql = "SELECT distinct origen_mov, desc_origen_mov FROM CAT_ORIGEN_MOV WHERE ";
		List<CatOrigenMovDto> origenes=null;
		if (!existe) { sql = sql + "NOT "; }
		try{
			sql = sql + "origen_mov IN (SELECT distinct origen_mov FROM ORIGEN_USUARIO WHERE no_usuario = " + noUsuario;
			sql = sql + " ) ORDER BY desc_origen_mov";
			
			 System.out.println(sql);
			 origenes = jdbcTemplate.query(sql, new RowMapper<CatOrigenMovDto>(){
				public CatOrigenMovDto mapRow(ResultSet rs, int idx) throws SQLException {
					CatOrigenMovDto origen= new CatOrigenMovDto();
					origen.setOrigenMov(rs.getString("origen_mov"));
					origen.setDescOrigenMov(rs.getString("desc_origen_mov"));
					return origen;
				}});
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Seguridad, C:catOrigenMovDao, M:consultar");
			}
		return origenes;
	}
	
	/**
	 * Inserta un registro en la tabla de origen_usuario. Los datos se obtienen del objeto proporcionado
	 * como parametro. 
	 * @param usuario, persona objetos que seran insertados en la BD
	 * @return numero de registros afectados por la operacion
	 * @throws Exception
	 */
	public int insertar(OrigenUsuarioDto dto, boolean todos) throws Exception{
		int res=0;
		try{
			if(!todos)
			res = jdbcTemplate.update("INSERT INTO ORIGEN_USUARIO (no_usuario, origen_mov) values (?,?)",
					new Object[]{dto.getNoUsuario(), dto.getOrigenMov()});
			else
			 res = jdbcTemplate.update("INSERT INTO ORIGEN_USUARIO SELECT " + dto.getNoUsuario() + ", origen_mov " +
					  "FROM CAT_ORIGEN_MOV WHERE NOT origen_mov IN (" +
					  "SELECT distinct origen_mov FROM ORIGEN_USUARIO WHERE no_usuario = " + dto.getNoUsuario()+ ")");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:catOrigenMovDao, M:insertar");
		}
		return res;
	}
	
	
	/**
	 * Elimina al usuario junto con su origen que tiene asignado.  
	 * @param noUsuario idetificador del usuario y el origen identificador del proveedor que se va a eliminar.  
	 * @return numero de registros afectados. Generalmente 1 si el registro fue eliminado y 0 si no pudo realizarse la operacion
	 */
	public int eliminar(OrigenUsuarioDto dto, boolean todos) throws Exception{
		int res=0;
		String sql = "DELETE FROM ORIGEN_USUARIO WHERE no_usuario = " + dto.getNoUsuario();
		try{
		if (!todos)	sql = sql + " AND origen_mov = '"+dto.getOrigenMov()+"'";
		res = jdbcTemplate.update(sql);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:catOrigenMovDao, M:eliminar");
		}
		return res;
	}
	//getters && setters
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
}
