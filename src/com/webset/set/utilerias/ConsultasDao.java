package com.webset.set.utilerias;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


public class ConsultasDao {
	private JdbcTemplate jdbcTemplate;
	String sql;
    StringBuffer sb;
	
	public ConsultasDao(JdbcTemplate jdbcTemplate){
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
		DateFormat df;
		 
		if(fecha.length()==10)
			 
			df=new SimpleDateFormat("dd/MM/yyyy"); 
		else
			 
			df=new SimpleDateFormat("dd/MM/yy"); 
		try
		{
			Date today = df.parse(fecha); 
		
			return today;
		} catch (ParseException e){
			e.printStackTrace();
			return null;
		}
	}
	
	
	public List<Date> obtenerFechaHoy()
	{
		sb=new StringBuffer();
		sb.append("SELECT FEC_HOY FROM FECHAS");
		List <Date> fecHoy=jdbcTemplate.query(sb.toString(), new RowMapper<Date>(){
			public Date mapRow(ResultSet rs, int idx) throws SQLException {
				return rs.getDate("fec_hoy");
			}});
	  return fecHoy;
	}
	
	public List<String> consultarConfiguraSet(int indice)
	{
		sql="";
		sb=new StringBuffer();
		sb.append(" SELECT valor from configura_set");
		ArrayList<String> cond=new ArrayList<String>();
		
			if(indice>0)
			{
				cond.add(" indice= '"+indice+"'");
			}
			 		
			if(cond.size()>0)
			{
				sb.append(" WHERE "+ StringUtils.join(cond," AND "));			
			}	
		 
							
	    sql=sb.toString();
	    List<String>valor=jdbcTemplate.query(sql, new RowMapper<String>(){
			public String mapRow(ResultSet rs, int idx) throws SQLException {
				return rs.getString("valor");
			}});
	  return valor;
	}
	
	
	public int actualizarCatCtaBanco(String Archivo, int idBanco, Date fecha, int psArchivos)
	{
		 StringBuffer sb=new StringBuffer();
	 
		 sb.append(" UPDATE cat_cta_banco ");
		 sb.append(" SET fecha_banca='"+fecha+"' ");
		 sb.append(" WHERE ");
		 sb.append(" id_chequera_in ");
		 sb.append(" (select distinct id_chequera from movto_banca_e ");
		 sb.append("  where  archivo in("+psArchivos +") ) ");
		 sb.append(" and id_banco ="+idBanco);
		  
		 int res=jdbcTemplate.update(sb.toString());
		 return res;
	}
	 
	public int getUsuario(String campo){
		int res = jdbcTemplate.queryForInt("SELECT id_usuario FROM seg_usuario WHERE clave_usuario = '"+ campo +"'");
		
		if(res < 1) res = 1;
		
		return res;
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
