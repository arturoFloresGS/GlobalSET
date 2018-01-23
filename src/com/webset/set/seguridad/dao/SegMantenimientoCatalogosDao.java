package com.webset.set.seguridad.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.seguridad.dto.SegComponenteDto;
import com.webset.set.seguridad.dto.SegMantenimientoCatalogosDto;
import com.webset.set.utilerias.Bitacora;

public class SegMantenimientoCatalogosDao {
	private JdbcTemplate jdbcTemplate;
	private GeneralDao gral;
	Bitacora bitacora=new Bitacora();
	private static Logger logger = Logger.getLogger(SegMantenimientoCatalogosDao.class);

	public List<SegMantenimientoCatalogosDto> llenaCatalogos() {
		List<SegMantenimientoCatalogosDto> catalogos = null;
		StringBuffer sb = new StringBuffer();
		sb.append("select * from cat_catalogo");
		try {
			catalogos = jdbcTemplate.query(sb.toString(), new RowMapper<SegMantenimientoCatalogosDto>(){
			public SegMantenimientoCatalogosDto mapRow(ResultSet rs, int idx) throws SQLException {
				SegMantenimientoCatalogosDto cat = new SegMantenimientoCatalogosDto();
				cat.setNombreCatalogo(rs.getString("nombre_catalogo"));
				cat.setDescCatalogo(rs.getString("desc_catalogo"));
				cat.setTituloColumnas(rs.getString("titulo_columnas"));
				cat.setCampos(rs.getString("campos"));
				cat.setBotones(rs.getString("botones"));
			return cat;
			}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegMantenimientoCatalogoDao, M:llenaCatalogos");
		}
		return catalogos;
	}
	
	public int guardarCatalogo(SegMantenimientoCatalogosDto datos) {
		int r = -1;
		try {
			//ConsultasGenerales consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			//int folio = consultasGenerales.obtenFolio("seg_componente");
			StringBuffer sql = new StringBuffer();
			sql.append("insert into cat_catalogo values(");
			sql.append("'1'");
			sql.append(", '");
			sql.append(datos.getNombreCatalogo());
			sql.append("', '");
			sql.append(datos.getDescCatalogo());
			sql.append("', '");
			sql.append(datos.getTituloColumnas());
			sql.append("', '");
			sql.append(datos.getCampos());
			sql.append("', '");
			sql.append(datos.getBotones());
			sql.append("')");
			System.out.println(sql.toString());
			r = jdbcTemplate.update(sql.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegMantenimientoCatalogoDao, M:guardarCatalogo");
		}
		return r;
	}

	public int modificarCatalogo(SegMantenimientoCatalogosDto datos) {
		int r = -1;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("update cat_catalogo set desc_catalogo = ");
			sql.append("'");
			sql.append(datos.getDescCatalogo());
			sql.append("', titulo_columnas = '");
			sql.append(datos.getTituloColumnas());
			sql.append("', campos = '");
			sql.append(datos.getCampos());
			sql.append("', botones = '");
			sql.append(datos.getBotones());
			sql.append("' where nombre_catalogo = '");
			sql.append(datos.getNombreCatalogo()+"'");
			r = jdbcTemplate.update(sql.toString());
			System.out.println(sql.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegMantenimientoCatalogoDao, M:modificarCatalogos");
		}
		return r;
	}

	public int eliminarCatalogo(String id) {
		int r = -1;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("delete from cat_catalogo where nombre_catalogo ='"+id+"' ");
			r = jdbcTemplate.update(sql.toString());
			System.out.println(sql.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegMantenimientoCatalogoslofDao, M:eliminarCatalogo");
		}
		return r;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

}
