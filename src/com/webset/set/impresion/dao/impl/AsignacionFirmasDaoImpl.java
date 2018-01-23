package com.webset.set.impresion.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.impresion.dao.AsignacionFirmasDao;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

/**
 * 
 * @author YEC
 * 06 DE ENERO DEL 2015
 */

public class AsignacionFirmasDaoImpl implements AsignacionFirmasDao  {
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	
	public List<LlenaComboGralDto> llenarComboBancos(){
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		try{
		      sb.append("\n SELECT DISTINCT id_banco as ID , ");
		      sb.append("\n desc_banco as DESCRIPCION ");
		      sb.append("\n FROM cat_banco");
		      sb.append("\n WHERE b_cheque_elect = 'S' ");
		      sb.append("\n ORDER BY desc_banco ");
		      System.out.println(sb.toString());
		      
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
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:AsignacionFirmasDaoImplDao, M:llenarComboFirmantes");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:AsignacionFirmasDaoImplDao, M:llenarComboFirmantes");
		}return listDatos;
	}
	
	public List<LlenaComboGralDto> llenarComboCuentas(String idBanco){
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		try{ 
			  sb.append("\n SELECT DISTINCT cta_no as ID , ");
		      sb.append("\n cta_no as DESCRIPCION ");
		      sb.append("\n FROM cat_firma");
		      sb.append("\n WHERE bco_cve = "+idBanco);
		      System.out.println(sb.toString());
		     
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
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:AsignacionFirmasDaoImplDao, M:llenarComboFirmantes");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:AsignacionFirmasDaoImplDao, M:llenarComboFirmantes");
		}return listDatos;
	}
	
	public List<LlenaComboGralDto> llenarComboFirmantes(String tipo,String idBanco, String cuenta ){
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		try{
		      sb.append("\n SELECT DISTINCT cf.no_persona as ID , ");
		      sb.append("\n COALESCE( p.nombre, ' ') as DESCRIPCION ");
		      sb.append("\n FROM cat_firma cf , cat_persona p");
		      sb.append("\n WHERE cf.no_persona=p.no_persona ");
		      sb.append("\n AND cf.bco_cve= "+idBanco);
		      sb.append("\n AND cf.cta_no= "+cuenta);
		      sb.append("\n AND tipo_firma= '"+tipo+"' ");
		      System.out.println("llenar combo firmantes"+sb.toString());
		     
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
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:AsignacionFirmasDaoImplDao, M:llenarComboFirmantes");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:AsignacionFirmasDaoImplDao, M:llenarComboFirmantes");
		}return listDatos;
	}
	
	public String updateFirmanteDeterminado(String tipo,String idBanco, String cuenta,String idPersona){
		StringBuffer sb = new StringBuffer();
		String resultado="Error";
		int r=0;
		try {
			sb.delete(0, sb.length());
		    sb.append("UPDATE cat_firma");
			sb.append("\n SET b_deter = '0'");
		    sb.append("\n WHERE bco_cve= "+idBanco);
		    sb.append("\n AND cta_no= "+cuenta);
		    sb.append("\n AND tipo_firma= '"+tipo+"' ");
		    System.out.println(sb.toString());
		   
		    r=jdbcTemplate.update(sb.toString());
		    System.out.println("aqui 2");
			System.out.println("fksdjdkfj"+r);
		    if(r!=0){
				sb.delete(0, sb.length());
			    sb.append("UPDATE cat_firma");
				sb.append("\n SET b_deter = '1'");
				sb.append("\n WHERE no_persona = " + idPersona);
			    sb.append("\n AND bco_cve= "+idBanco);
			    sb.append("\n AND cta_no= "+cuenta);
			    sb.append("\n AND tipo_firma= '"+tipo+"' ");
			    System.out.println(sb.toString());
			    r=jdbcTemplate.update(sb.toString());
				if(r!=0){
					resultado="Exito";
				}else{
					System.out.println("Error");
				}
			}else{
				System.out.println("Error al actualizar firma");
				resultado="Error al actualizar firma";
			} 	
		}
		catch(CannotGetJdbcConnectionException e){
			System.out.println("Error de conexion");
			resultado = "Error de conexion";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:AsignacionFirmasDaoImplDao, M:llenarComboFirmantes");
		}
		catch(Exception e){
			System.out.println("Excepcion kfksdn");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:AsignacionFirmasDaoImplDao, M:llenarComboFirmantes");
		}return resultado;
	}
	
	/***********************************************************/
	public void setDataSource(DataSource dataSource){
		try {
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:impresion.mantenimientos, C:AsignacionFirmasDaoImpl, M:setDataSource");
		}
	}
}
