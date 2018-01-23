package com.webset.set.seguridad.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.seguridad.dto.CatAreaDto;
import com.webset.set.seguridad.dto.UsuarioAreaDto;
import com.webset.set.utilerias.Bitacora;

/**
 * 
 * @author Cristian Garcia Garcia
 *
 */
public class CatAreaDao {
	private JdbcTemplate jdbcTemplate;
	private Bitacora bitacora=new Bitacora();
	
	
	/**
	 * Metodo que consulta la tabla usuario_area en base a los parametros.
	 * noUsuario y trae resultados de la tabla cat_area.
	 * @param "noUsuario" del que se traeran los resultados de la tabla usuario_area y "existe" para saber si requiere 
	 * 		de los datos asignados o pendientes por asignar al usuario.
	 * @return lista con todas las areas que tiene o se le pueden asignar al usuario. 
	 * @throws Exception
	 */
	
	public List<CatAreaDto> consultar(int noUsuario, boolean existe){
		String sql = "SELECT distinct id_area, desc_area FROM CAT_AREA WHERE ";
		
		if (!existe) { sql = sql + "NOT "; }
		
		sql = sql + "id_area IN (SELECT distinct id_area FROM USUARIO_AREA WHERE no_usuario = " + noUsuario;
		sql = sql + " ) ORDER BY id_area";
		
		
		
		List<CatAreaDto> areas=null;
		try{
			areas = jdbcTemplate.query(sql, new RowMapper<CatAreaDto>(){
			public CatAreaDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatAreaDto area = new CatAreaDto();
				area.setIdArea(rs.getInt("id_area"));
				area.setDescArea(rs.getString("desc_area"));
				return area;
			}});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:CatAreaDao, M:consultar");
		}

		return areas;
	}
	
	/**
	 * Inserta un registro en la tabla de usuario_area. Los datos se obtienen del objeto proporcionado
	 * como parametro. 
	 * @param usuario, id_area datos del objeto que seran insertados en la BD
	 * @return numero de registros afectados por la operacion
	 * @throws Exception
	 */
	public int insertar(UsuarioAreaDto usuario, boolean todos){
		int res=-1;

		try{
			if(!todos)
				res = jdbcTemplate.update("INSERT INTO USUARIO_AREA (no_usuario, id_area) values (?,?)",
				new Object[]{usuario.getNoUsuario(), usuario.getIdArea()});
		
			else
				res = jdbcTemplate.update("INSERT INTO USUARIO_AREA SELECT " + usuario.getNoUsuario() + ", id_area " +
				  "FROM CAT_AREA WHERE NOT id_area IN (" +
				  "SELECT distinct id_area FROM USUARIO_AREA WHERE no_usuario = " + usuario.getNoUsuario() + ")");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:CatAreaDao, M:insertar");
		}
		return res;
	}
	
	
	/**
	 * Elimina al usuario junto con su origen que tiene asignado.  
	 * @param noUsuario idetificador del usuario y el origen identificador del proveedor que se va a eliminar.  
	 * @return numero de registros afectados. Generalmente 1 si el registro fue eliminado y 0 si no pudo realizarse la operacion
	 */
	public int eliminar(UsuarioAreaDto dto, boolean todos){
		int res=-1;
		String sql = "DELETE FROM USUARIO_AREA WHERE no_usuario = " + dto.getNoUsuario();
		if (!todos)	sql = sql + " AND id_area = " + dto.getIdArea();
		try{
			res = jdbcTemplate.update(sql);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
						+ "P:Seguridad, C:CatAreaDao, M:consultar");
		}
		
		return res;
	}

	//getters && setters
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:CatAreaDao, M:setDataSource");
		}
	}
}
