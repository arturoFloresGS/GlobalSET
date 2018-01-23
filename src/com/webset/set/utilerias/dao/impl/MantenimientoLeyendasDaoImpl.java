package com.webset.set.utilerias.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dao.MantenimientoLeyendasDao;
import com.webset.set.utileriasmod.dto.MantenimientoLeyendasDto;
import com.webset.utils.tools.Utilerias;

/**
 * Clase de catalogo de leyendas.
 * @author YEC
 * @since  26/11/2015
 */

public class MantenimientoLeyendasDaoImpl implements MantenimientoLeyendasDao{
	private JdbcTemplate jdbcTemplate = new JdbcTemplate();
	private Bitacora bitacora = new Bitacora();
	
	public List<MantenimientoLeyendasDto> llenaGridLeyendas(String descLeyenda){
		List<MantenimientoLeyendasDto> listaResultado = new ArrayList<MantenimientoLeyendasDto>();
		StringBuffer sql = new StringBuffer();
		try{
			/*****REALIZA UNA CONSULTA INDIVIDUAL, POR EL TIPO DE LEYENDA : PARA LLENAR EL GRID DE LEYENDAS CUANDO SE REALIZA UNA BUSQUEDA ****/
			if(!descLeyenda.equals("")&&!descLeyenda.equals(null)){
				sql.append("Select l.id_leyenda, l.desc_leyenda ,fec_alta ");
				sql.append( "\n from cat_leyenda l order by id_leyenda");
				sql.append( "\n WHERE l.desc_leyenda  like '%"+Utilerias.validarCadenaSQL(descLeyenda)+"%'");
				sql.append( "\n order by id_leyenda");
				System.out.println(sql.toString());
				listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MantenimientoLeyendasDto>(){
					public MantenimientoLeyendasDto mapRow(ResultSet rs, int idx) throws SQLException{
						MantenimientoLeyendasDto campos = new MantenimientoLeyendasDto();
						campos.setIdLeyenda(rs.getInt("id_leyenda"));
						campos.setDescLeyenda(rs.getString("DESC_LEYENDA"));
						campos.setFecAlta(""+ponerFecha(rs.getDate("fec_alta")));
						return campos;
					}
				});
			}
		/**** Realiza una consulta general, obteniendo todas las leyendas : para llenar el grid de leyendas ****/
			if(listaResultado.size() <= 0){
				sql.delete(0, sql.length());
				sql.append("Select id_leyenda, DESC_LEYENDA,  fec_alta ");
				sql.append("\n from CAT_LEYENDA ");
				sql.append("\n order by id_leyenda");
				listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MantenimientoLeyendasDto>(){
					public MantenimientoLeyendasDto mapRow(ResultSet rs, int idx) throws SQLException{
						MantenimientoLeyendasDto campos = new MantenimientoLeyendasDto();
						campos.setIdLeyenda(rs.getInt("id_leyenda"));
						campos.setDescLeyenda(rs.getString("DESC_LEYENDA"));
						campos.setFecAlta(""+ponerFecha(rs.getDate("fec_alta")));
						return campos;
					}
				});
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoLeyendasDaoImpl, M:llenaGridLeyendas");
		}return listaResultado;
	}
	
	public String insertaMantenimientoLeyendas(MantenimientoLeyendasDto dto){
		String resultado="Error";
		try{
			StringBuffer sql = new StringBuffer();
			int resp;
			sql.append("insert into cat_leyenda (id_leyenda,desc_leyenda,usuario_alta,fec_alta) values (");
			sql.append("\n (SELECT case when max(coalesce(id_leyenda,0)) + 1 is null then 1 else max(coalesce(id_leyenda,0)) +1 end ");
			sql.append("\n FROM cat_leyenda),");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getDescLeyenda()) + "',");
			sql.append("\n" + Utilerias.validarCadenaSQL(dto.getUsuarioAlta()) + ",");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getFecAlta()) + "')");
			System.out.println(sql.toString());
			resp=jdbcTemplate.update(sql.toString());
			if(resp==1){
				resultado="Exito";
			}
		}
		catch(CannotGetJdbcConnectionException e){
			resultado="Error al conectar con la base de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoLeyendasDaoImpl, M:insertaMantenimientoLeyendas");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoLeyendasDaoImpl, M:insertaMantenimientoLeyendas");
		}return resultado;
	}
	
	public String updateMantenimientoLeyendas(MantenimientoLeyendasDto dto){
		String resultado="Error";
		try{
			StringBuffer sql = new StringBuffer();
			int resp;
			sql.append("update cat_leyenda ");
			sql.append("\n set desc_leyenda='" + Utilerias.validarCadenaSQL(dto.getDescLeyenda()) + "' ");
		    sql.append("\n  where ");
			sql.append(" id_leyenda= " + Utilerias.validarCadenaSQL(dto.getIdLeyenda()) + "");
			resp=jdbcTemplate.update(sql.toString());
			if(resp==1){resultado="Exito";}
		}
		catch(CannotGetJdbcConnectionException e){
			resultado="Error al conectar con la base de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoLeyendasDaoImpl, M:updateMantenimientoLeyendas");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoLeyendasDaoImpl, M:updateMantenimientoLeyendas");
		}return resultado;
	}
	
	public String deleteMantenimientoLeyendas(MantenimientoLeyendasDto dto){
		String resultado="Error";
		try{
			
			StringBuffer sql = new StringBuffer();
			int resp;
			sql.append("delete cat_leyenda ");
			sql.append("\n  where ");
			sql.append(" id_leyenda= " + Utilerias.validarCadenaSQL(dto.getIdLeyenda()) + "");
			resp=jdbcTemplate.update(sql.toString());
			if(resp==1){
				resultado="Exito";
			}
		}
		catch(CannotGetJdbcConnectionException e){
			resultado="Error al conectar con la base de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoLeyendasDaoImpl, M:deleteMantenimientoLeyendas");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoLeyendasDaoImpl, M:deleteMantenimientoLeyendas");
		}return resultado;
	}
	
	private String ponerFecha(Date fecha) {
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat(
				"dd/MM/yyyy");
		formatoDeFecha.setLenient(false);
		return formatoDeFecha.format(fecha);
	}
	
	//**************************************************************		
	public void setDataSource(DataSource dataSource)
	{
		try
		{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoLeyendasDaoImpl, M:setDataSource");
		}
	}
	
}
