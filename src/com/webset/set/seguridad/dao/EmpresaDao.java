package com.webset.set.seguridad.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.seguridad.dto.EmpresaDto;
import com.webset.set.seguridad.dto.UsuarioEmpresaDto;
import com.webset.set.utilerias.Bitacora;

/**
 * 
 * @author vtello
 *
 */
public class EmpresaDao {
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora=new Bitacora();
	/**
	 * Metodo que consulta la tabla usuario_empresa en base a los parametros.
	 * noUsuario y trae resultados de la tabla empresa.
	 * @param "noUsuario" del que se traeran los resultados de la tabla usuario_empresa y "existe" para saber si requiere 
	 * 		de los datos asignados o pendientes por asignar al usuario.
	 * @return lista con todas las empresas que tiene o se le pueden asignar al usuario. 
	 * @throws Exception
	 */
	
	public List<EmpresaDto> consultar(int noUsuario, boolean existe) throws Exception{
		String sql = "SELECT no_empresa, nom_empresa FROM EMPRESA WHERE ";
		List<EmpresaDto> empresas=new ArrayList<EmpresaDto>();
		if (!existe) { sql = sql + "NOT "; }
		sql = sql + "no_empresa IN (SELECT distinct no_empresa FROM USUARIO_EMPRESA WHERE no_usuario = " + noUsuario;
		sql =sql + " ) ORDER BY no_empresa";
		try{
			 empresas = jdbcTemplate.query(sql, new RowMapper<EmpresaDto>(){
				public EmpresaDto mapRow(ResultSet rs, int idx) throws SQLException {
					EmpresaDto empresa = new EmpresaDto();
					empresa.setNoEmpresa(rs.getInt("no_empresa"));
					empresa.setNomEmpresa(rs.getString("nom_empresa"));
					return empresa;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:EmpresaDao, M:consultar");
		}
		return empresas;
	}
	
	/**
	 * Inserta un registro en la tabla de usuario_empresa. Los datos se obtienen del objeto proporcionado
	 * como parametro. 
	 * @param usuario objeto que sera insertado en la BD
	 * @return numero de registros afectados por la operacion
	 * @throws Exception
	 */
	public int insertar(UsuarioEmpresaDto usuario, boolean todos) throws Exception{
		int res=-1;
		try{
			if(!todos)
			res = jdbcTemplate.update("INSERT INTO USUARIO_EMPRESA (no_usuario, no_empresa) values (?,?)",
					new Object[]{usuario.getNoUsuario(), usuario.getNoEmpresa()});
			else
			res = jdbcTemplate.update("INSERT INTO USUARIO_EMPRESA SELECT " + usuario.getNoUsuario() + ", no_empresa FROM EMPRESA " +
					  "WHERE NOT no_empresa IN (SELECT distinct no_empresa FROM usuario_empresa WHERE no_usuario = " + usuario.getNoUsuario() + ")");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:EmpresaDao, M:insertar");
		}
		return res;
	}
	
	
	/**
	 * Elimina al usuario junto con la empresa que tiene asignado.  
	 * @param noUsuario idetificador del usuario y la noEempresa identificador de la empresa que se va a eliminar.  
	 * @return numero de registros afectados. Generalmente 1 si el registro fue eliminado y 0 si no pudo realizarse la operacion
	 */
	public int eliminar(UsuarioEmpresaDto dto, boolean todos) throws Exception{
		int res=-1;
		String sql = "DELETE FROM USUARIO_EMPRESA WHERE no_usuario = " + dto.getNoUsuario();
		try{
			if (!todos)	sql = sql + " AND no_empresa = " + dto.getNoEmpresa();
			res = jdbcTemplate.update(sql);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:EmpresaDao, M:eliminar");
		}
		return res;
	}
	
	
	public List<EmpresaDto> obtenerTodasEmpresas() throws Exception{
		List<EmpresaDto> empresas=new ArrayList<EmpresaDto>();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT no_empresa, nom_empresa FROM EMPRESA ");
			empresas = jdbcTemplate.query(sql.toString(), new RowMapper<EmpresaDto>(){
				public EmpresaDto mapRow(ResultSet rs, int idx) throws SQLException {
					EmpresaDto empresa = new EmpresaDto();
					empresa.setNoEmpresa(rs.getInt("no_empresa"));
					empresa.setNomEmpresa(rs.getString("nom_empresa"));
					return empresa;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:EmpresaDao, M:obtenerTodasEmpresas");
		}
		return empresas;
	}
	
	//getters && setters
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
}
