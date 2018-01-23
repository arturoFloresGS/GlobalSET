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
import com.webset.set.utilerias.dao.MantenimientoSolicitantesDao;
import com.webset.set.utileriasmod.dto.MantenimientoSolicitantesDto;
import com.webset.utils.tools.Utilerias;

/**
 * Clase de catalogo de solicitantes.
 * @author YEC
 * @since  26/11/2015
 */

public class MantenimientoSolicitantesDaoImpl implements MantenimientoSolicitantesDao{
	private JdbcTemplate jdbcTemplate = new JdbcTemplate();
	private Bitacora bitacora = new Bitacora();
	
	public List<MantenimientoSolicitantesDto> llenaGridSolicitantes(String nombre){
		List<MantenimientoSolicitantesDto> listaResultado = new ArrayList<MantenimientoSolicitantesDto>();
		StringBuffer sql = new StringBuffer();
		try{
			if(!nombre.equals("")&&!nombre.equals(null)){
				sql.append("SELECT s.id_sol,convert(char(10),s.fecha_alta,103) as fecha_alta,s.observacion_sol,");
				sql.append("\n s.identificacionc1_sol,s.nombrec1_sol,s.puestoc1_sol,s.telefonoc1_sol,s.correoc1_sol,  ");
				sql.append("\n s.identificacionc2_sol,s.nombrec2_sol,s.puestoc2_sol,s.telefonoc2_sol,s.correoc2_sol ");
				sql.append( "\n FROM cat_solicitantes s");
				sql.append("\n WHERE nombrec1_sol like '"+Utilerias.validarCadenaSQL(nombre)+"%'");
				sql.append("\n OR nombrec2_sol like '"+Utilerias.validarCadenaSQL(nombre)+"%'");
				sql.append("\n ORDER BY fecha_alta");
			}else{
				if(nombre.equals("")){
					sql.append("SELECT top 100 s.id_sol,convert(char(10),s.fecha_alta,103) as fecha_alta ,s.observacion_sol,");
					sql.append("\n s.identificacionc1_sol,s.nombrec1_sol,s.puestoc1_sol,s.telefonoc1_sol,s.correoc1_sol,  ");
					sql.append("\n s.identificacionc2_sol,s.nombrec2_sol,s.puestoc2_sol,s.telefonoc2_sol,s.correoc2_sol ");
					sql.append( "\n FROM cat_solicitantes s");
					sql.append("\n ");
					sql.append("\n ORDER BY fecha_alta");
				}
			}
				listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MantenimientoSolicitantesDto>(){
					public MantenimientoSolicitantesDto mapRow(ResultSet rs, int idx) throws SQLException{
						MantenimientoSolicitantesDto campos = new MantenimientoSolicitantesDto();
						campos.setIdentificacion1(rs.getString("identificacionc1_sol"));
						campos.setNombre1(rs.getString("nombrec1_sol"));
						campos.setPuesto1(rs.getString("puestoc1_sol"));
						campos.setCorreo1(rs.getString("correoc1_sol"));
						campos.setTelefono1(rs.getString("telefonoc1_sol"));
						campos.setIdentificacion2(rs.getString("identificacionc2_sol"));
						campos.setNombre2(rs.getString("nombrec2_sol"));
						campos.setPuesto2(rs.getString("puestoc2_sol"));
						campos.setCorreo2(rs.getString("correoc2_sol"));
						campos.setTelefono2(rs.getString("telefonoc2_sol"));
						campos.setIdSolicitante(rs.getString("id_sol"));
						campos.setObservacion(rs.getString("observacion_sol"));
						campos.setFecha(rs.getString("fecha_alta"));
						return campos;
	
					}
				});
	    }catch(Exception e){
	    	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesDaoImpl, M:llenaGridSolicitantes");
		}return listaResultado;
	}
	
	public String insertSolicitante(MantenimientoSolicitantesDto dto){
		String resultado="Error";
		try{
			StringBuffer sql = new StringBuffer();
			int resp;
			sql.append("insert into cat_solicitantes values( ");
			sql.append("\n (SELECT case when max(coalesce(id_sol,0)) + 1 is null then 1 else max(coalesce(id_sol,0)) +1 end ");
			sql.append("\n FROM cat_solicitantes),");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getIdentificacion1()) + "',");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getNombre1())+ "',");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getPuesto1())+ "',");
			sql.append("\n" + Utilerias.validarCadenaSQL(dto.getTelefono1()) + ",");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getCorreo1()) + "',");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getIdentificacion2()) + "',");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getNombre2())+ "',");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getPuesto2())+ "',");
			sql.append("\n" + Utilerias.validarCadenaSQL(dto.getTelefono2()) + ",");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getCorreo2()) + "',");
			sql.append("\n CONVERT(date,'" + Utilerias.validarCadenaSQL(dto.getFecha()) + "',103),");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getObservacion()) + "')");
			System.out.println(sql.toString());
			resp=jdbcTemplate.update(sql.toString());
			if(resp==1){
				resultado="Exito";
			}
		}
		catch(CannotGetJdbcConnectionException e){
			resultado="Error al conectar con la base de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesDaoImpl, M:insertSolicitante");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesDaoImpl, M:insertSolicitante");
		}return resultado;
	}
	
	public String updateSolicitante(MantenimientoSolicitantesDto dto){
		String resultado="Error";
		try{
			StringBuffer sql = new StringBuffer();
			int resp;
			sql.append("update CAT_SOLICITANTES ");
			sql.append("\n set identificacionc1_sol='" + Utilerias.validarCadenaSQL(dto.getIdentificacion1()) + "',");
			sql.append("\n  nombrec1_sol='" + Utilerias.validarCadenaSQL(dto.getNombre1()) + "',");
			sql.append("\n  puestoc1_sol='" + Utilerias.validarCadenaSQL(dto.getPuesto1()) + "' ,");
			sql.append("\n  correoc1_sol='" + Utilerias.validarCadenaSQL(dto.getCorreo1()) + "',");
			sql.append("\n  telefonoc1_sol=" + Utilerias.validarCadenaSQL(dto.getTelefono1()) + " ,");
			sql.append("\n  identificacionc2_sol='" + Utilerias.validarCadenaSQL(dto.getIdentificacion2()) + "',");
			sql.append("\n  nombrec2_sol='" + Utilerias.validarCadenaSQL(dto.getNombre2()) + "',");
			sql.append("\n  puestoc2_sol='" + Utilerias.validarCadenaSQL(dto.getPuesto2()) + "' ,");
			sql.append("\n  correoc2_sol='" + Utilerias.validarCadenaSQL(dto.getCorreo2()) + "',");
			sql.append("\n  telefonoc2_sol=" + Utilerias.validarCadenaSQL(dto.getTelefono2()) + " ,");
			sql.append("\n  fecha_alta = CONVERT(date,'" + Utilerias.validarCadenaSQL(dto.getFecha()) + "',103),");
			sql.append("\n  observacion_sol='" + Utilerias.validarCadenaSQL(dto.getObservacion()) + "' ");
			sql.append("\n  where ");
			sql.append(" id_sol= '" + dto.getIdSolicitante() + "' ");
			resp=jdbcTemplate.update(sql.toString());
			if(resp==1){
				resultado="Exito";
			}
		}
		catch(CannotGetJdbcConnectionException e){
			resultado="Error al conectar con la base de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesDaoImpl, M:updateMantenimientoSolicitantes");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesDaoImpl, M:updateMantenimientoSolicitantes");
		}return resultado;
	}
	
	public String deleteSolicitante(MantenimientoSolicitantesDto dto){
		String resultado="Error";
		try{
			StringBuffer sql = new StringBuffer();
			int resp;
			sql.append("delete cat_solicitantes ");
			sql.append("\n  where ");
			sql.append(" id_sol= '" + Utilerias.validarCadenaSQL(dto.getIdSolicitante()) + "'");
			resp=jdbcTemplate.update(sql.toString());
			if(resp==1){
				resultado="Exito";
			}
		}
		catch(CannotGetJdbcConnectionException e){
			resultado="Error al conectar con la base de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesDaoImpl, M:deleteMantenimientoSolicitantes");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesDaoImpl, M:deleteMantenimientoSolicitantes");
		}return resultado;
	}
	
	
	//**************************************************************		
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesDaoImpl, M:setDataSource");
		}
	}
	
}
