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

import com.webset.set.impresion.dao.MantenimientosDao;
import com.webset.set.impresion.dto.MantenimientosDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;

/*
 * Modificado el 07 de enero del 2016
 * Por Yoseline Espino C.
 */

public class MantenimientosDaoImpl implements MantenimientosDao {
	private Bitacora bitacora = new Bitacora();
	private JdbcTemplate jdbcTemplate = new JdbcTemplate();
	
	//Patanlla de Mantenimiento de Firmantes
	
	public List<LlenaComboGralDto> llenarComboNoFirmantes(){
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		try{
		      sb.append("\n SELECT DISTINCT id_usuario as ID , ");
		      sb.append("\n  COALESCE( (nombre + ' ' + apellido_paterno + ' ' +apellido_materno), ' ') as DESCRIPCION  ");
		      sb.append("\n FROM  seg_usuario");
		     sb.append("\n WHERE id_usuario not in ( ");
		      sb.append("\n SELECT DISTINCT no_persona ");
		      sb.append("\n FROM cat_persona )");
		      sb.append("\n ORDER BY DESCRIPCION,ID");
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
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosDaoImpl, M:llenarComboFirmantes");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosDaoImpl, M:llenarComboFirmantes");
		}return listDatos;
	}
	
	public List<MantenimientosDto> buscaFirmantes(int noPersona) {
		List<MantenimientosDto> result = new ArrayList<MantenimientosDto>();
		String sql = "";
		
		try{
			sql+= "SELECT * FROM cat_persona";
			
			if(noPersona > 0)
				sql+= " WHERE no_persona = "+ noPersona +"";

			result = jdbcTemplate.query(sql, new RowMapper<MantenimientosDto>() {
				public MantenimientosDto mapRow(ResultSet rs, int idx) throws SQLException {
					MantenimientosDto cons = new MantenimientosDto();
					cons.setNoPersona(rs.getInt("no_persona"));
					cons.setNombre(rs.getString("nombre"));
					cons.setPathFirma(rs.getString("path_firma"));
					
					return cons;
				}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosDaoImpl, M:obtenerBancos");
		}
		return result;
	}
	
	public List<MantenimientosDto> existeFirma(int noPersona) {
		List<MantenimientosDto> result = new ArrayList<MantenimientosDto>();
		StringBuffer sb = new StringBuffer();
		try{
			sb.append("SELECT * FROM cat_firma");
			sb.append("\n WHERE no_persona = " + noPersona );
			sb.append("\n AND b_deter ='1'");
			
			result = jdbcTemplate.query(sb.toString(), new RowMapper<MantenimientosDto>() {
				public MantenimientosDto mapRow(ResultSet rs, int idx) throws SQLException {
					MantenimientosDto cons = new MantenimientosDto();
					cons.setNoPersona(rs.getInt("no_persona"));
					
					return cons;
				}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosDaoImpl, M:existeFirma");
		}
		return result;
	}
	
	public int eliminarFirmante(int noPersona) {
		String sql = "";
	
		try{
		
			sql+= "DELETE FROM cat_persona WHERE no_persona = " + noPersona + "";
			System.out.println(sql.toString());
			if(jdbcTemplate.update(sql)!=0){
				sql="";
				//sql ="SELECT COUNT(*) FROM cat_firma WHERE no_persona = " + noPersona + "";
				int contador= jdbcTemplate.queryForInt(sql);
				if(contador!=0 ){
					sql+= "DELETE FROM cat_firma WHERE no_persona = " + noPersona + "";
					System.out.println(sql);
					return jdbcTemplate.update(sql.toString());
				}else
					return 1;
			}else{
				return 0;
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosDaoImpl, M:eliminarFirmante");
			return 0;
		}
	}
	
	public int modificarFirmante(int noPersona, String nombre, String pathFirma) {
		String sql = "";
		
		try {
			sql += "UPDATE cat_persona SET nombre = '" + Utilerias.validarCadenaSQL(nombre) + "', path_firma = '" + Utilerias.validarCadenaSQL(pathFirma) + "'";
			sql += "\n WHERE no_persona = " + noPersona + " ";
			
			return jdbcTemplate.update(sql);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosDaoImpl, M:modificarFirmante");
			return 0;
		}
	}
	
	public int insertarFirmante(int noPersona, String nombre, String pathFirma) {
		String sql = "";
		
		try {
			int existe = buscaFirmantes(noPersona).size();
			if(existe==0){
				sql += "INSERT INTO cat_persona (no_persona, nombre, path_firma)";
				sql += "\n VALUES(" + noPersona + ", '" + Utilerias.validarCadenaSQL(nombre) + "', '" + Utilerias.validarCadenaSQL(pathFirma) + "')";
				System.out.println(sql);
				return jdbcTemplate.update(sql);
			}else{
				return 1;
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosDaoImpl, M:insertarFirmante");
			return 0;
		}
	}
	
	//Patanlla de Mantenimiento de Firmas
	
	
	public List<LlenaComboGralDto> llenaComboBancos(){
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		try{
		      sb.append("\n SELECT DISTINCT id_banco as ID , ");
		      sb.append("\n  desc_banco as DESCRIPCION  ");
		      sb.append("\n FROM  cat_banco");
		      sb.append("\n WHERE b_cheque_elect = 'S' ");
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
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosDaoImpl, M:llenaComboBancos");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosDaoImpl, M:llenaComboBancos");
		}return listDatos;
	}
	
	public List<LlenaComboGralDto> llenaComboChequeras(int idBanco){
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		try{
		      sb.append("\n SELECT DISTINCT id_chequera as ID , ");
		      sb.append("\n  id_chequera as DESCRIPCION  ");
		      sb.append("\n FROM  cat_cta_banco");
		      sb.append("\n WHERE id_banco = " + Utilerias.validarCadenaSQL(idBanco) );
		     System.out.println(sb.toString());
		     
		    listDatos= jdbcTemplate.query(sb.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setIdStr(rs.getString("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}});
		}
		catch(CannotGetJdbcConnectionException e){
			LlenaComboGralDto cons = new LlenaComboGralDto();
			cons.setCampoUno("Error de conexion");
			listDatos.add(cons);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosDaoImpl, M:llenaComboChequeras");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosDaoImpl, M:llenaComboChequeras");
		}return listDatos;
	}
	
	public List<LlenaComboGralDto> llenaComboPersonas(String idBanco , String cuenta, boolean busqueda) {
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		//System.out.println("antes del select "+ busqueda);
		try{
		      sb.append("\n SELECT DISTINCT no_persona as ID , ");
		      sb.append("\n  nombre as DESCRIPCION  ");
		      sb.append("\n FROM  cat_persona");
		      if(busqueda)
		    	  sb.append("\n WHERE no_persona in ( ");
		      else
		    	  sb.append("\n WHERE no_persona not in ( ");
		      sb.append("\n 	SELECT DISTINCT no_persona ");
		      sb.append("\n 	FROM cat_firma ");
		      if(!idBanco.equals(null) && !idBanco.equals(""))
		    	  sb.append("\n 	WHERE bco_cve =" +Utilerias.validarCadenaSQL(idBanco));
		      if(!idBanco.equals(null) && !idBanco.equals("")&& !cuenta.equals(null) && !cuenta.equals(""))
		    	  sb.append("\n 	AND cta_no= '"+ Utilerias.validarCadenaSQL(cuenta) +"'");
		      sb.append("\n )");
		     System.out.println("Llena combo personas: \n" +sb.toString());
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
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosDaoImpl, M:llenarComboFirmantes");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosDaoImpl, M:llenarComboFirmantes");
		}return listDatos;
	}
	
	public List<MantenimientosDto> buscaFirmas(String idBanco, String idChequera, String noFirma) {
		List<MantenimientosDto> datos = new ArrayList<MantenimientosDto>();
		String sql = "";
		
		
		try {
			sql += "SELECT cf.no_persona,cf.tipo_firma, cf.b_deter , cf.cta_no, cp.nombre, cf.bco_cve, cb.desc_banco";
			sql += "\n FROM cat_firma cf , cat_persona cp , cat_banco cb";
			sql += "\n WHERE cf.no_persona= cp.no_persona";
			sql += "\n 		AND cf.bco_cve=cb.id_Banco";
			if(!idBanco.equals(null)&& !idBanco.equals(""))
				sql += "\n 		AND cf.bco_cve="+Utilerias.validarCadenaSQL(idBanco);
			if(!idChequera.equals(null)&& !idChequera.equals(""))
				sql += "\n 		AND cf.cta_no="+Utilerias.validarCadenaSQL(idChequera);
			if(!noFirma.equals(null)&& !noFirma.equals(""))
				sql += "\n 		AND cf.no_persona="+Utilerias.validarCadenaSQL(noFirma);
			
			sql += "\n ORDER BY cf.no_persona";
			
			System.out.println("Llena grid"+sql);
			
			datos = jdbcTemplate.query(sql, new RowMapper<MantenimientosDto>() {
				public MantenimientosDto mapRow(ResultSet rs, int idx) throws SQLException {
					MantenimientosDto cons = new MantenimientosDto();
					cons.setIdBanco(rs.getInt("bco_cve"));
					cons.setNoPersona(rs.getInt("no_persona"));
					cons.setIdChequera(rs.getString("cta_no"));
					cons.setBDeter(rs.getString("b_deter").equals("0") ? "NO" : "SI");
					cons.setTipoFirma(rs.getString("tipo_firma"));
					cons.setDescBanco(rs.getString("desc_banco"));
					cons.setNombre(rs.getString("nombre"));
					return cons;
				}
		    }); 
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosDaoImpl, M:buscaFirmas");
		}
		return datos;
	}
	
	public int existeFirma(String idBanco,String noCuenta,String noPersona){
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("SELECT count(no_persona) ");
			sql.append("\n FROM cat_firma ");
			sql.append("\n  WHERE bco_cve="+ Utilerias.validarCadenaSQL(idBanco));
			sql.append("\n AND cta_no='" + Utilerias.validarCadenaSQL(noCuenta)+ "'");
			sql.append("\n AND no_persona= "+ Utilerias.validarCadenaSQL(noPersona));
			System.out.println(sql.toString());
			return jdbcTemplate.queryForInt(sql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosDaoImpl, M:existeFirma");
			return 0;
		}
	}
	
	public int existePredeterminado(String idBanco,String noCuenta,String tipo){
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("SELECT count(no_persona) ");
			sql.append("\n FROM cat_firma ");
			sql.append("\n  WHERE bco_cve="+ Utilerias.validarCadenaSQL(idBanco));
			sql.append("\n AND cta_no='" + Utilerias.validarCadenaSQL(noCuenta)+ "'");
			sql.append("\n AND tipo_firma='"+ Utilerias.validarCadenaSQL(tipo)+"'");
			sql.append("\n AND b_deter ='"+ 1+"'");
			////System.out.println(sql.toString());
			return jdbcTemplate.queryForInt(sql.toString());

		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosDaoImpl, M:existePredeterminado");
			return 0;
		}
	}
	
	public void setDataSource(DataSource dataSource){
		try {
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:setDataSource");
		}
	}
	
	public String eliminarFirma(MantenimientosDto dto) {
		StringBuffer sql = new StringBuffer();
		String mensaje="Error";
		try{
			sql.append("DELETE FROM cat_firma");
			sql.append("\n where no_persona="+Utilerias.validarCadenaSQL(dto.getNoPersona()));
			sql.append("\n AND bco_cve="+Utilerias.validarCadenaSQL(dto.getIdBanco()));
			sql.append("\n AND cta_no='"+Utilerias.validarCadenaSQL(dto.getIdChequera())+"'");
			/*sql.append("\n AND B_DETE='"+dto.getBDeter());
			sql.append("\n AND TIPO_FIRMA='"+dto.getTipoFirma());*/
			System.out.println("rrubio"+sql.toString());
			if( (jdbcTemplate.update(sql.toString()))==1)
				mensaje="Exito";
		}
		
		catch(CannotGetJdbcConnectionException e){
			mensaje="Error al conectar con la base de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosDaoImpl, M:eliminarFirma");
		}

		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosDaoImpl, M:eliminarFirma");
		}return mensaje;
	}
	
	public String insetarFirma(MantenimientosDto dto) {
		StringBuffer sql = new StringBuffer();
		String mensaje="Error";
		try{
			if(existeFirma(dto.getIdBanco()+"", dto.getIdChequera(), dto.getNoPersona()+"")==0){
				int b=0;
				if(dto.getBDeter()=="1"){
					b=existePredeterminado(dto.getIdBanco()+"", dto.getIdChequera(), dto.getTipoFirma());
				}
				if(b==0){
					sql.append("INSERT INTO CAT_FIRMA VALUES (");
					sql.append(Utilerias.validarCadenaSQL(dto.getIdBanco())+",");
					sql.append(Utilerias.validarCadenaSQL(dto.getNoPersona())+",");
					sql.append("'"+Utilerias.validarCadenaSQL(dto.getIdChequera())+"',");
					sql.append("'0',");
					sql.append("'B')");
					////System.out.println(sql.toString());
					if( (jdbcTemplate.update(sql.toString()))==1)
						mensaje="Exito";
					else
						mensaje="Error al insertar el registro";
				}else{
					mensaje="Error: Ya existe un firmante predeterminado";
				}
			}else{
				mensaje="Error: La firma ya esta asignada a esta cuenta";
			}
		}
		
		catch(CannotGetJdbcConnectionException e){
			mensaje="Error al conectar con la base de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosDaoImpl, M:insetarFirma");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosDaoImpl, M:insetarFirma");
		}return mensaje;
	}
}
