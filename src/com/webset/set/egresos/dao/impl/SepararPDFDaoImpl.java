package com.webset.set.egresos.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.webset.set.egresos.dao.SepararPDFDao;
import com.webset.set.utilerias.dto.ConfiguraSetDto;

public class SepararPDFDaoImpl implements SepararPDFDao {
	//Atributos de la clase
	private JdbcTemplate jdbcTemplate;
	private Logger logger = Logger.getLogger(SepararPDFDaoImpl.class);
	//private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	
	/**
	 * Devuelve el valor del indice indicado en configura_set,
	 * si no se encuentra o ocurre un error devuelve null.
	 */
	@SuppressWarnings("unchecked")
	public ConfiguraSetDto consultaConfiguraSet(int indice){
		ConfiguraSetDto result = null;
		List<ConfiguraSetDto> lConf = new ArrayList<ConfiguraSetDto>();
		StringBuilder sbSql = new StringBuilder();
		
		try{
			sbSql.append("SELECT INDICE, VALOR, DESCRIPCION FROM CONFIGURA_SET WHERE INDICE = "+indice+" ");
			
			lConf = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
                public ConfiguraSetDto mapRow(ResultSet rs, int idx) throws SQLException {
                        ConfiguraSetDto csd = new ConfiguraSetDto();
                        csd.setIndice(rs.getInt("INDICE"));
                        csd.setValor(rs.getString("VALOR"));
                        csd.setDescripcion(rs.getString("DESCRIPCION"));
                        return csd;
                }});
			
			if(lConf!=null && lConf.size()>0)
				result = lConf.get(0);
		}
		catch(Exception e){
			result = null;
			logger.error(e);
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Consulta en movimiento e hist_movimiento por noFolioDet, si existe pone la ruta del PDF en comentario2
	 * Si no existe, busca por beneficiario, fechaValor, importe y chequera, si existe se actualiza la ruta del PDF en comentario2
	 * Si existe devuelve 1
	 * Si no existe devuelve -1
	 */
	public int consultaActualizaMovimiento(String sNoFolio, String sBenef, String sFecValorFormato, String sImporte, String sChequera){
		int result=0;
		StringBuilder sbSql = new StringBuilder();
		
		try{
			String sAnyo = sFecValorFormato.substring(6, sFecValorFormato.length());
			String sMes  = sFecValorFormato.substring(3, 5);
			
			sbSql.append("select count(*) from movimiento where referencia like '%" + sNoFolio + "' and importe = "+sImporte+" ");
			logger.info("consultaActualizaMovimiento: " + sbSql);
			result = jdbcTemplate.queryForInt(sbSql.toString());

			//Si encuentra el movimiento se actualiza
			if(result>0){
				sbSql = new StringBuilder();
				sbSql.append("update movimiento set comentario2='citibank/"+sAnyo+"/"+sMes+"/" +sNoFolio+"_"+sBenef+"_"+sFecValorFormato.replaceAll("\\/", "")+"_"+sImporte.replaceAll("\\.", "")+"_"+sChequera+".pdf' where referencia like '%" + sNoFolio + "' and importe = "+sImporte+" ");
				logger.info("consultaActualizaMovimiento: " + sbSql);
				result = jdbcTemplate.update(sbSql.toString());
				return result;
			}
			else{
				sbSql = new StringBuilder();
				sbSql.append("select count(*) from hist_movimiento where referencia like '%" + sNoFolio + "' and importe = "+sImporte+" ");
				logger.info("consultaActualizaMovimiento: " + sbSql);
				result = jdbcTemplate.queryForInt(sbSql.toString());
				
				if(result>0){
					sbSql = new StringBuilder();
					sbSql.append("update hist_movimiento set comentario2='citibank/"+sAnyo+"/"+sMes+"/" +sNoFolio+"_"+sBenef+"_"+sFecValorFormato.replaceAll("\\/", "")+"_"+sImporte.replaceAll("\\.", "")+"_"+sChequera+".pdf' where referencia like '%" + sNoFolio + "' and importe = "+sImporte+" ");
					logger.info("consultaActualizaMovimiento: " + sbSql);
					result = jdbcTemplate.update(sbSql.toString());
					return result;
				}
			}
			
			//Si no se encuentra por noFolioDet se busca por benef, fecValor, importe y chequera
			sbSql = new StringBuilder();
			sbSql.append("select count(*) from movimiento where id_tipo_operacion=3200 and replace(beneficiario, ' ', '') = '"+sBenef+"' ");
			sbSql.append("and convert(varchar(10), fec_valor, 103) = '"+sFecValorFormato+"' ");
			sbSql.append("and importe = " + sImporte + " and id_chequera = '"+sChequera+"' ");
			logger.info("consultaActualizaMovimiento: " + sbSql);
			result = jdbcTemplate.queryForInt(sbSql.toString());
			
			//Si encuentra el movimiento se actualiza
			if(result>0){
				sbSql = new StringBuilder();
				sbSql.append("update movimiento set comentario2='citibank/"+sAnyo+"/"+sMes+"/" +sNoFolio+"_"+sBenef+"_"+sFecValorFormato.replaceAll("\\/", "")+"_"+sImporte.replaceAll("\\.", "")+"_"+sChequera+".pdf' where id_tipo_operacion=3200 and replace(beneficiario, ' ', '') = '"+sBenef+"' ");
				sbSql.append("and convert(varchar(10), fec_valor, 103) = '"+sFecValorFormato+"' ");
				sbSql.append("and importe = " + sImporte + " and id_chequera = '"+sChequera+"' ");
				logger.info("consultaActualizaMovimiento: " + sbSql);
				result = jdbcTemplate.update(sbSql.toString());
				return result;
			}
			else{
				sbSql = new StringBuilder();
				sbSql.append("select count(*) from hist_movimiento where id_tipo_operacion=3200 and replace(beneficiario, ' ', '') = '"+sBenef+"' ");
				sbSql.append("and convert(varchar(10), fec_valor, 103) = '"+sFecValorFormato+"' ");
				sbSql.append("and importe = " + sImporte + " and id_chequera = '"+sChequera+"' ");
				logger.info("consultaActualizaMovimiento: " + sbSql);
				result = jdbcTemplate.queryForInt(sbSql.toString());
				
				if(result>0){
					sbSql = new StringBuilder();
					sbSql.append("update hist_movimiento set comentario2='citibank/"+sAnyo+"/"+sMes+"/" +sNoFolio+"_"+sBenef+"_"+sFecValorFormato.replaceAll("\\/", "")+"_"+sImporte.replaceAll("\\.", "")+"_"+sChequera+".pdf' where id_tipo_operacion=3200 and replace(beneficiario, ' ', '') = '"+sBenef+"' ");
					sbSql.append("and convert(varchar(10), fec_valor, 103) = '"+sFecValorFormato+"' ");
					sbSql.append("and importe = " + sImporte + " and id_chequera = '"+sChequera+"' ");
					logger.info("consultaActualizaMovimiento: " + sbSql);
					result = jdbcTemplate.update(sbSql.toString());
					return result;
				}
			}
			result=-1;
		}
		catch(Exception e){
			e.printStackTrace();
			logger.error(e);
			return -1; //result = -1;
		}
		return result;
	}
	
	/****************************Getters and Setters **********************/
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
		catch(Exception e){
			e.printStackTrace();
			logger.error(e);
		}
	}
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
}
