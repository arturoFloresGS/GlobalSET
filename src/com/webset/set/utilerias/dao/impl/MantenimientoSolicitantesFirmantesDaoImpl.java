package com.webset.set.utilerias.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dao.MantenimientoSolicitantesFirmantesDao;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utileriasmod.dto.MantenimientoSolicitantesFirmantesDto;

/**
 * Clase de catalogo de solicitantes y firmantes .
 * @author YEC
 * @since  26/11/2015
 */

public class MantenimientoSolicitantesFirmantesDaoImpl implements MantenimientoSolicitantesFirmantesDao{
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora;
	
	public List<LlenaComboGralDto> llenarComboPersonas(){
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		try{
		      sb.append("\n SELECT DISTINCT id_usuario as ID , ");
		      sb.append("\n  COALESCE( (nombre + ' ' + apellido_paterno + ' ' +apellido_materno), ' ') as DESCRIPCION  ");
		      sb.append("\n FROM  seg_usuario");
		      sb.append("\n WHERE  id_usuario not in ( ");
		      sb.append("\n SELECT DISTINCT id_persona ");
		      sb.append("\n FROM cat_solicitante_firmante )");
		      sb.append("\n ORDER BY DESCRIPCION,ID");
	
		     System.out.println("LLENA COMBO NO EN  cat_solicitante_firmante"+ sb.toString());
		    listDatos= jdbcTemplate.query(sb.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}});
		}
		catch(CannotGetJdbcConnectionException e){
			LlenaComboGralDto cons = new LlenaComboGralDto();
			cons.setCampoUno("Error de conexion");
			listDatos.add(cons);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesFirmantesDaoImpl, M:llenarComboFirmantes");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesFirmantesDaoImpl, M:llenarComboFirmantes");
		}return listDatos;
	}
	
	public List<MantenimientoSolicitantesFirmantesDto> llenaGridSolicitantesFirmantes(String tipoPersona){
		List<MantenimientoSolicitantesFirmantesDto> listaResultado = new ArrayList<MantenimientoSolicitantesFirmantesDto>();
		StringBuffer sql = new StringBuffer();
		try{
			if(!tipoPersona.equals("")&&!tipoPersona.equals(null)){
				sql.append("Select sf.id_persona, sf.nombre_persona, sf.puesto_persona, sf.tipo_persona");
				sql.append( "\n from cat_solicitante_firmante sf");
				sql.append( "\n where sf.tipo_persona = '"+tipoPersona +"' ");
				listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MantenimientoSolicitantesFirmantesDto>(){
					public MantenimientoSolicitantesFirmantesDto mapRow(ResultSet rs, int idx) throws SQLException{
						MantenimientoSolicitantesFirmantesDto campos = new MantenimientoSolicitantesFirmantesDto();
						campos.setIdPersona(rs.getString("id_persona"));
						campos.setNombre(rs.getString("nombre_persona"));
						campos.setPuesto(rs.getString("puesto_persona"));
						switch (rs.getString("tipo_persona")) {
							case "S":
								campos.setTipoPersona("SOLICITANTE");
								break;
							case "L":
								campos.setTipoPersona("FIRMANTE 1");					
							break;				
							case "F":
								campos.setTipoPersona("FIRMANTE 2");
								break;
						}
						return campos;
	
					}
				});
			}
		
			if(tipoPersona.equals("")){
				sql.delete(0, sql.length());
				sql.append("Select id_persona, nombre_persona, puesto_persona , tipo_persona ");
				sql.append("\n from cat_solicitante_firmante");
				listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MantenimientoSolicitantesFirmantesDto>(){
					public MantenimientoSolicitantesFirmantesDto mapRow(ResultSet rs, int idx) throws SQLException{
						MantenimientoSolicitantesFirmantesDto campos = new MantenimientoSolicitantesFirmantesDto();
						campos.setIdPersona(rs.getString("id_persona"));
						campos.setNombre(rs.getString("nombre_persona"));
						campos.setPuesto(rs.getString("puesto_persona"));
						switch (rs.getString("tipo_persona")) {
							case "S":
								campos.setTipoPersona("SOLICITANTE");
								break;
							case "L":
								campos.setTipoPersona("FIRMANTE 1");					
								break;				
							case "F":
								campos.setTipoPersona("FIRMANTE 2");
								break;
						}
						return campos;
					}
				});
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesFirmantesDaoImpl, M:llenaGridSolicitantesFirmantes");
		}return listaResultado;
	}
	
	public String insertaMantenimientoSolicitantesFirmantes(MantenimientoSolicitantesFirmantesDto dto){
		String resultado="Error";
		try{
			StringBuffer sql = new StringBuffer();
			int resp;
			sql.append("insert into cat_solicitante_firmante values( ");
			sql.append("\n'" + dto.getIdPersona() + "',");
			sql.append("\n'" + dto.getNombre() + "',");
			sql.append("\n'" + dto.getPuesto() + "',");
			sql.append("\n'" + dto.getTipoPersona()+ "',");
			sql.append("\n" + dto.getUsuarioAlta() + ",");
			sql.append("\n'" + dto.getFecAlta() + "')");
			resp=jdbcTemplate.update(sql.toString());
			if(resp==1){
				resultado="Exito";
			}
		}
		catch(CannotGetJdbcConnectionException e){
			resultado="Error al conectar con la base de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesFirmantesDaoImpl, M:insertaMantenimientoSolicitantesFirmantes");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesFirmantesDaoImpl, M:insertaMantenimientoSolicitantesFirmantes");
		}return resultado;
	}
	
	public String updateMantenimientoSolicitantesFirmantes(MantenimientoSolicitantesFirmantesDto dto){
		String resultado="Error";
		try{
			StringBuffer sql = new StringBuffer();
			int resp;
			sql.append("update cat_solicitante_firmante ");
			sql.append("\n set nombre_persona='" + dto.getNombre() + "',");
			sql.append("\n  puesto_persona='" + dto.getPuesto() + "',");
			sql.append("\n  tipo_persona='" + dto.getTipoPersona() + "' ");
			sql.append("\n  where ");
			sql.append(" id_persona= '" + dto.getIdPersona() + "' ");
			resp=jdbcTemplate.update(sql.toString());
			if(resp==1){
				resultado="Exito";
			}
		}
		catch(CannotGetJdbcConnectionException e){
			resultado="Error al conectar con la base de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesFirmantesDaoImpl, M:updateMantenimientoSolicitantesFirmantes");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesFirmantesDaoImpl, M:updateMantenimientoSolicitantesFirmantes");
		}return resultado;
	}
	
	public String deleteMantenimientoSolicitantesFirmantes(MantenimientoSolicitantesFirmantesDto dto){
		String resultado="Error";
		try{
			StringBuffer sql = new StringBuffer();
			int resp;
			sql.append("delete cat_solicitante_firmante ");
			sql.append("\n  where ");
			sql.append(" id_persona= '" + dto.getIdPersona() + "'");
			resp=jdbcTemplate.update(sql.toString());
			if(resp==1){
				resultado="Exito";
			}
		}
		catch(CannotGetJdbcConnectionException e){
			resultado="Error al conectar con la base de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesFirmantesDaoImpl, M:deleteMantenimientoSolicitantesFirmantes");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesFirmantesDaoImpl, M:deleteMantenimientoSolicitantesFirmantes");
		}return resultado;
	}
	
	
	//**************************************************************		
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesFirmantesDaoImpl, M:setDataSource");
		}
	}
	
}
