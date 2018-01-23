package com.webset.set.utilerias.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.dao.GrupoEmpresasDao;
import com.webset.set.utileriasmod.dto.GrupoEmpresasDto;

public class GrupoEmpresasDaoImpl implements GrupoEmpresasDao{
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora;
	String sql = "";
	ConsultasGenerales consultasGenerales;
	
	
	public String configuraSet(int indice){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
	}
	
	@SuppressWarnings("unchecked")
	public List<GrupoEmpresasDto> llenaComboGrupo(){
		List<GrupoEmpresasDto> listaResultado = new ArrayList<GrupoEmpresasDto>();
		sql = "";
		
		try{
			sql = "Select * from cat_grupo_flujo ";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public GrupoEmpresasDto mapRow(ResultSet rs, int idx) throws SQLException{
					GrupoEmpresasDto campos = new GrupoEmpresasDto();
					campos.setIdGrupo(rs.getInt("id_grupo_flujo"));
					campos.setDescGrupo(rs.getString("desc_grupo_flujo"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: GrupoEmpresasDaoImpl, M: llenaComboGrupo");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<GrupoEmpresasDto> llenaComboEmpresa(){
		List<GrupoEmpresasDto> listaResultado = new ArrayList<GrupoEmpresasDto>();
		sql = "";
		
		try{
			sql = "Select * from empresa ";
				
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public GrupoEmpresasDto mapRow(ResultSet rs, int idx) throws SQLException{
					GrupoEmpresasDto campos = new GrupoEmpresasDto();
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setNomEmpresa(rs.getString("nom_empresa"));
					return campos;
				}
			});
		}
		catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: GrupoEmpresasDaoImpl, M: llenaComboEmpresa");
		}	return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<GrupoEmpresasDto> llenaGrid(int noEmpresa, int idGrupo, int todo){
		List<GrupoEmpresasDto> listaResultado = new ArrayList<GrupoEmpresasDto>();
		sql = "";
		
		try{			
			sql = "Select cg.remitente_correo, cg.correo_empresa, g.id_grupo_flujo, ";
			sql += "\n g.no_empresa, e.nom_empresa, cg.desc_grupo_flujo ";
			sql += "\n from grupo_empresa g, empresa e, cat_grupo_flujo cg ";
			sql += "\n where g.no_empresa = e.no_empresa ";
			sql += "\n and cg.id_grupo_flujo = g.id_grupo_flujo ";
			
			if (todo == 0){
				if (idGrupo != 0)
					sql += "\n and g.id_grupo_flujo = "+ idGrupo +" ";
				if (noEmpresa != 0)
					sql += "\n and g.no_empresa = "+ noEmpresa +" ";
			}						
			
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public GrupoEmpresasDto mapRow(ResultSet rs, int idx) throws SQLException{
					GrupoEmpresasDto campos = new GrupoEmpresasDto();
					campos.setRemitenteCorreo(rs.getString("remitente_correo"));
					campos.setCorreoEmpresa(rs.getString("correo_empresa"));
					campos.setIdGrupo(rs.getInt("id_grupo_flujo"));
					campos.setDescGrupo(rs.getString("desc_grupo_flujo"));
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setNomEmpresa(rs.getString("nom_empresa"));
					return campos;
				}
			});
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: GrupoEmpresasDaoImpl, M: llenaGrid");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<GrupoEmpresasDto> buscaRegistro(int idGrupo, int noEmpresa){
		List<GrupoEmpresasDto> listaResultado = new ArrayList<GrupoEmpresasDto>();
		sql = "";
		
		try{
			sql = "Select * from grupo_empresa ";
			sql += "\n where no_empresa = "+ noEmpresa +" ";
			/*sql += "\n where id_grupo_flujo = "+ idGrupo +" ";
			sql += "\n and no_empresa = "+ noEmpresa +" ";*/
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public GrupoEmpresasDto mapRow(ResultSet rs, int idx)throws SQLException{
					GrupoEmpresasDto campos = new GrupoEmpresasDto();
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setIdGrupo(rs.getInt("id_grupo_flujo"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: GrupoEmpresasDaoImpl, M: buscaRegistro");
		}return listaResultado;
	}
	
	public int insertaRegistro(int idGrupo, int noEmpresa){
		int resultado = 0;
		sql = "";
		try{
			sql = "Insert into grupo_empresa (";
			sql += "\n id_grupo_flujo, no_empresa) ";
			sql += "Values (";
			sql += ""+ idGrupo +", "+ noEmpresa +") ";
		
			resultado = jdbcTemplate.update(sql);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: GrupoEmpresasDaoImpl, M: insertaRegistro");
		}return resultado;
	}
	
	public int eliminaRegistro(int idGrupo, int noEmpresa){
		int resultado = 0;
		sql = "";
		
		try{
			sql = "Delete from grupo_empresa ";
			sql += "\n where id_grupo_flujo = "+ idGrupo +" ";
			sql += "\n and no_empresa = "+ noEmpresa +" ";
			
			resultado = jdbcTemplate.update(sql);
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: GrupoEmpresasDaoImpl, M: eliminaRegistro");
		}return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<GrupoEmpresasDto> obtieneCorreo (int idGrupo){
		List<GrupoEmpresasDto> listaResultado = new ArrayList<GrupoEmpresasDto>();
		sql = "";
		try{
			sql = "Select coalesce(correo_empresa, '') as correo_empresa, coalesce(remitente_correo, '') as remitente_correo, ";
			sql += "\n coalesce(nivel_autorizacion, 0) as nivel_autorizacion";
			sql += "\n from cat_grupo_flujo ";
			sql += "\n where id_grupo_flujo = "+ idGrupo +" ";
						
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public GrupoEmpresasDto mapRow(ResultSet rs, int idx)throws SQLException{
					GrupoEmpresasDto campos = new GrupoEmpresasDto();
					campos.setCorreoEmpresa(rs.getString("correo_empresa"));
					campos.setRemitenteCorreo(rs.getString("remitente_correo"));
					campos.setNivelAutorizacion(rs.getInt("nivel_autorizacion"));
										
					return campos;
				}
			});
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: GrupoEmpresasDaoImpl, M: obtieneCorreo");
		}return listaResultado;
	}
	
	public int cambiaNivel(int idGrupo, int nivel){
		int resultado = 0;
		String sql = "";
		try{
			sql = "Update cat_grupo_flujo ";
			sql += "\n set nivel_autorizacion = "+ nivel +"";
			sql += "\n where id_grupo_flujo = "+ idGrupo +"";
		
			resultado = jdbcTemplate.update(sql);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: GrupoEmpresasDaoImpl, M: cambiaNivel");
		}return resultado;		
	}
	
	//******************************************************************************************************************************
	public void setDataSource(DataSource dataSource)
	{
		try
		{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: GrupoEmpresasDaoImpl, M:setDataSource");
		}
	}

	

	@Override
	public List<Map<String, String>> reporteGrupoEmpresas() {
		List<Map<String, String>> listaResultado = new ArrayList<Map<String, String>>();
		StringBuffer sql = new StringBuffer();
		try{
			
			sql.append("Select cg.remitente_correo, cg.correo_empresa, g.id_grupo_flujo, \n"); 
			sql.append("g.no_empresa, e.nom_empresa, cg.desc_grupo_flujo \n"); 
			sql.append("from grupo_empresa g, empresa e, cat_grupo_flujo cg  \n");
			sql.append("where g.no_empresa = e.no_empresa  \n");
			sql.append("and cg.id_grupo_flujo = g.id_grupo_flujo order by cg.id_grupo_flujo");
			
			
			System.out.println("-----------------");
			System.out.println("imprime query");
			System.out.println("-----------------");
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>(){
				public Map<String, String> mapRow(ResultSet rs, int idx)throws SQLException{
					Map<String, String> campos = new HashMap<String, String>();
					
				
					campos.put("noEmpresa",rs.getString("no_empresa"));
					campos.put("nomEmpresa",rs.getString("nom_empresa"));
					campos.put("idGrupo",rs.getString("id_grupo_flujo"));
					campos.put("descGrupo",rs.getString("desc_grupo_flujo"));
					campos.put("correoEmpresa",rs.getString("correo_empresa"));
					campos.put("remitenteCorreo",rs.getString("remitente_correo"));
					
				
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoDaoImpl, M: reporteMapeo");
		}return listaResultado;	
	}
}
