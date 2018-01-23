package com.webset.set.seguridad.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.seguridad.dto.SegConfiguracionesSETDto;
import com.webset.set.seguridad.dto.SegMantenimientoCatalogosDto;
import com.webset.set.utilerias.Bitacora;

public class SegConfiguracionesSETDao {
	private JdbcTemplate jdbcTemplate;
	private GeneralDao gral;
	Bitacora bitacora=new Bitacora();
	private static Logger logger = Logger.getLogger(SegConfiguracionesSETDao.class);


	public List<SegConfiguracionesSETDto> llenaConfiguraciones() {
		List<SegConfiguracionesSETDto> configuraciones = null;
		StringBuffer sb = new StringBuffer();
		sb.append("select * from configura_set");
		try {
			configuraciones = jdbcTemplate.query(sb.toString(), new RowMapper<SegConfiguracionesSETDto>(){
			public SegConfiguracionesSETDto mapRow(ResultSet rs, int idx) throws SQLException {
				SegConfiguracionesSETDto conf = new SegConfiguracionesSETDto();
				conf.setIndice(rs.getString("indice"));
				conf.setValor(rs.getString("valor"));
				conf.setDescripcion(rs.getString("descripcion"));
			return conf;
			}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegConfiguracionesSETDao, M:llenaConfiguraciones");
		}
		return configuraciones;
	}

	public int guardarConfiguracion(SegConfiguracionesSETDto datos) {
		int r = -1;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("insert into configura_set values('");
			sql.append(datos.getIndice());
			sql.append("', '");
			sql.append(datos.getValor());
			sql.append("', '");
			sql.append(datos.getDescripcion());
			sql.append("')");
			System.out.println(sql.toString());
			r = jdbcTemplate.update(sql.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegConfiguracionesSETDao, M:guardarConfiguracion");
		}
		return r;
	}

	public int modificarConfiguracion(SegConfiguracionesSETDto datos) {
		int r = -1;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("update configura_set set valor = ");
			sql.append("'");
			sql.append(datos.getValor());
			sql.append("', descripcion = '");
			sql.append(datos.getDescripcion());
			sql.append("' where indice = '");
			sql.append(datos.getIndice()+"'");
			r = jdbcTemplate.update(sql.toString());
			System.out.println(sql.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegConfiguracionesSETDao, M:modificarConfiguracion");
		}
		return r;
	}

	public int eliminarConfiguracion(String id) {
		int r = -1;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("delete from configura_set where indice ='"+id+"' ");
			r = jdbcTemplate.update(sql.toString());
			System.out.println(sql.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegConfiguracionesSETDao, M:eliminarConfiguracion");
		}
		return r;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
}
