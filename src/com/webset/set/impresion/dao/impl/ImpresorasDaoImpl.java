package com.webset.set.impresion.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.impresion.dao.ImpresorasDao;
import com.webset.set.impresion.dto.MantenimientosDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.CajaUsuarioDto;

@SuppressWarnings("unchecked")
public class ImpresorasDaoImpl implements ImpresorasDao{
	private JdbcTemplate jdbcTemplate;
	private Bitacora bitacora;
	private ConsultasGenerales consultasGenerales;
	private GlobalSingleton globalSingleton;
	
	public List<CajaUsuarioDto> llenaComboCajas() {
		globalSingleton = GlobalSingleton.getInstancia();
		
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.obtenerCajaUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
	}
	
	public List<MantenimientosDto> buscarImpresoras() {
		StringBuffer sql = new StringBuffer();
		List<MantenimientosDto> list = new ArrayList<MantenimientosDto>();
		
		try {
			sql.append(" SELECT i.no_impresora, c.desc_caja, i.num_charolas, i.cja_cve \n");
			sql.append(" FROM cat_impresora i, cat_caja c \n");
			sql.append(" WHERE i.cja_cve = c.id_caja \n");
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper() {
				public MantenimientosDto mapRow(ResultSet rs, int idx)  throws SQLException {
					MantenimientosDto res = new MantenimientosDto();
					res.setNoImpresora(rs.getInt("no_impresora"));
					res.setDescCaja(rs.getString("desc_caja"));
					res.setNoCharola(rs.getInt("num_charolas"));
					res.setCveCaja(rs.getInt("cja_cve"));
					return res;
				}
			});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:ImpresorasDaoImpl, M:buscarImpresoras");	
			e.printStackTrace();
		}
		return list;
	}
	
	public int eliminarImpre(int noImpresora) {
		StringBuffer sql = new StringBuffer();
		int result = 0;
		
		try {
			sql.append(" DELETE FROM cat_impresora \n");
			sql.append(" WHERE no_impresora = "+ noImpresora +" \n");
			
			result = jdbcTemplate.update(sql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:ImpresorasDaoImpl, M:eliminarImpre");	
			e.printStackTrace();
		}
		return result;
	}
	
	public int eliminarImpreCharola(int noImpresora) {
		StringBuffer sql = new StringBuffer();
		int result = 0;
		
		try {
			sql.append(" DELETE FROM charola \n");
			sql.append(" WHERE no_impresora = "+ noImpresora +" \n");
			
			result = jdbcTemplate.update(sql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:ImpresorasDaoImpl, M:eliminarImpreCharola");	
			e.printStackTrace();
		}
		return result;
	}
	
	public int buscarImpreEsp(int noImpre) {
		StringBuffer sql = new StringBuffer();
		int res = 0;
		
		try {
			sql.append(" SELECT count(*) \n");
			sql.append(" FROM cat_impresora \n");
			sql.append(" WHERE no_impresora = "+ noImpre +" \n");
			
			res = jdbcTemplate.queryForInt(sql.toString());
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:ImpresorasDaoImpl, M:buscarImpreEsp");	
			e.printStackTrace();
		}
		return res;
	}
	
	public int insertarImpre(int noImpresora, int noCaja, int noCharola) {
		StringBuffer sql = new StringBuffer();
		int result = 0;
		
		try {
			sql.append(" INSERT INTO cat_impresora (no_impresora, num_charolas, cja_cve)\n");
			sql.append("VALUES ("+ noImpresora +", "+ noCharola +", "+ noCaja +")\n");
			
			result = jdbcTemplate.update(sql.toString());
			
			//Insertar registros de acuerdo al numero de charolas en charola
			for(int i=1; i<=noCharola; i++) {
				sql = new StringBuffer();
				sql.append(" INSERT INTO charola (no_impresora, no_charola, bco_cve)\n");
				sql.append("VALUES ("+ noImpresora +", "+ i +", 0)\n");
				
				result = jdbcTemplate.update(sql.toString());
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:ImpresorasDaoImpl, M:eliminarImpreCharola");	
			e.printStackTrace();
		}
		return result;
	}
	
	
	
	public void setDataSource(DataSource dataSource){
		try {
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:ImpresorasDaoImpl, M:setDataSource");
		}
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
}
