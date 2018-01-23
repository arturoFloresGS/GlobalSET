package com.webset.set.seguridad.dao;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.jdbc.core.JdbcTemplate;

public class GeneralDao {
	private JdbcTemplate jdbcTemplate;
	
	public GeneralDao(JdbcTemplate jdbcTemplate){
		this.jdbcTemplate=jdbcTemplate;
	}
	/***
	 * retorna el consecutivo siguiente de la tabla
	 * @param tabla 
	 * @param campo
	 * @return
	 */
	public int getSeq(String tabla, String campo){
		int res=jdbcTemplate.queryForInt("SELECT MAX(" + campo + ") + 1 FROM " + tabla);
		if(res<1){
			res=1;
		}
		return res;
	}
	/**
	 * Retorna el numero de veces que esta repetido un campo
	 * @param tabla nombre de la tabla a consultar
	 * @param campo el campo de la tabla a contar
	 * @param clave la String que entrara en la sentencia a conectar
	 * @return
	 */
	public int getCount(String tabla, String campo, String clave){
		int res=jdbcTemplate.queryForInt("SELECT COUNT(" + campo + ") FROM " + tabla + " WHERE " + campo + " = '" + clave.toUpperCase().trim() + "'");
		if(res<1){
			res=0;
		}
		return res;
	}
	
	public Date cambiarFecha(String fecha){
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		try
		{
			Date today = df.parse(fecha);            
			return today;
		} catch (ParseException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public void beginTran(){
		jdbcTemplate.update("BEGIN TRAN");
	}
	
	public void rollback(){
		jdbcTemplate.update("ROLLBACK");
	}
	
	public void commit(){
		jdbcTemplate.update("COMMIT TRAN");
	}
	
	
}
