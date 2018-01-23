package com.webset.set.seguridad.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.seguridad.dto.PersonaDto;
import com.webset.set.seguridad.dto.UsuarioProveedorDto;
import com.webset.set.utilerias.Bitacora;

/**
 * 
 * @author vtello
 *
 */
public class PersonaDao {
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora=new Bitacora();
	
	/**
	 * Metodo que consulta la tabla usuario_proveedor en base a los parametros.
	 * noUsuario y trae resultados de la tabla persona.
	 * @param "noUsuario" del que se traeran los resultados de la tabla usuario_proveedor y "existe" para saber si requiere 
	 * 		de los datos asignados o pendientes por asignar al usuario.
	 * @return lista con todos los proveedores que tiene o se le pueden asignar al usuario. 
	 * @throws Exception
	 */
	
	public List<PersonaDto> consultar(int noUsuario, boolean existe) throws Exception{
		List<PersonaDto>  personas=null;
		String sql = "SELECT distinct top 5 no_persona, case when razon_social <> '' then razon_social " +
							"else nombre + ' ' + paterno + ' ' + materno end as razon_social FROM PERSONA " +
							"WHERE id_tipo_persona = 'P' AND ";
		if (!existe) { sql = sql + "NOT "; }
		
		sql = sql + "no_persona IN (SELECT distinct  no_persona FROM USUARIO_PROVEEDOR WHERE no_usuario = " + noUsuario;
		sql = sql + " ) ORDER BY no_persona";
		
		
		try{
		personas = jdbcTemplate.query(sql, new RowMapper<PersonaDto>(){
			public PersonaDto mapRow(ResultSet rs, int idx) throws SQLException {
				PersonaDto persona = new PersonaDto();
				persona.setNoPersona(rs.getInt("no_persona"));
				persona.setRazonSocial(rs.getString("razon_social"));
				return persona;
			}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:PersonaDao, M:consultar");
		}
		return personas;
	}
	
	/**
	 * Inserta un registro en la tabla de usuario_proveedor. Los datos se obtienen del objeto proporcionado
	 * como parametro. 
	 * @param usuario objeto que sera insertado en la BD
	 * @return numero de registros afectados por la operacion
	 * @throws Exception
	 */
	public int insertar(UsuarioProveedorDto usuario, boolean todos) throws Exception{
		int res=0;
		try{
			if(!todos)
			res = jdbcTemplate.update("INSERT INTO USUARIO_PROVEEDOR (no_usuario, no_persona) values (?,?)",
					new Object[]{usuario.getNoUsuario(), usuario.getNoPersona()});
			else
			res = jdbcTemplate.update("INSERT INTO USUARIO_PROVEEDOR SELECT TOP 5 " +usuario.getNoUsuario() + ", no_persona FROM PERSONA " +
					  "WHERE id_tipo_persona = 'P' AND NOT no_persona IN (" +
					  "SELECT distinct no_persona FROM USUARIO_PROVEEDOR WHERE no_usuario = " + usuario.getNoUsuario() + ")");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:PersonaDao, M:insertar");
		}	
		return res;
	}
	
	
	/**
	 * Elimina al usuario junto con su proveedor que tiene asignado.  
	 * @param noUsuario idetificador del usuario y la noPersona identificador del proveedor que se va a eliminar.  
	 * @return numero de registros afectados. Generalmente 1 si el registro fue eliminado y 0 si no pudo realizarse la operacion
	 */
	public int eliminar(UsuarioProveedorDto dto,boolean todos) throws Exception{
		int res=0;
		try{
			String sql = "DELETE FROM USUARIO_PROVEEDOR WHERE no_usuario = " + dto.getNoUsuario();
			if (!todos)	 sql = sql + " AND no_persona = " +dto.getNoPersona();
		 res = jdbcTemplate.update(sql);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:PersonaDao, M:eliminar");
		}
		return res;
	}
	
	//getters && setters
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
}
