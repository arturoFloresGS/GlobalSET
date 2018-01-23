package com.webset.set.impresion.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.impresion.dao.ConfiguracionContinuaDao;
import com.webset.set.impresion.dto.ChequeContinuoDto;
import com.webset.set.impresion.dto.ConfiguracionChequeDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;

/*
 * Luis Alfredo Serrato Montes de Oca
 */
public class ConfiguracionContinuaDaoImpl implements ConfiguracionContinuaDao {
	
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	
	
	@Override
	public List<ChequeContinuoDto> obtenerConfiguraciones(){
		List<ChequeContinuoDto> listaConf = new ArrayList<ChequeContinuoDto>();
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT ID_CONF, DESC_CONF, DESC_BANCO, b.ID_BANCO, DESC_DIVISA, d.ID_DIVISA, DRIVER,  \n");
		sb.append("TIPO_IMPRESION, NOM_EMPRESA, e.NO_EMPRESA, ID_CHEQUERA  \n");
		sb.append("FROM CAT_CHEQUE ch JOIN CAT_BANCO b ON ch.ID_BANCO = b.ID_BANCO \n");
		sb.append("JOIN CAT_DIVISA d ON ch.ID_DIVISA = d.ID_DIVISA JOIN EMPRESA e ON ch. NO_EMPRESA = e.NO_EMPRESA \n");
		System.out.println("rrubio"+sb.toString());
		try {
			listaConf = jdbcTemplate.query(sb.toString(), new RowMapper<ChequeContinuoDto>() {
				public ChequeContinuoDto mapRow(ResultSet rs, int idx) throws SQLException{
					ChequeContinuoDto configuracion = new ChequeContinuoDto();
					
					configuracion.setIdConf(rs.getInt("ID_CONF"));
					configuracion.setDescConf(rs.getString("DESC_CONF"));
					configuracion.setDescBanco(rs.getString("DESC_BANCO"));
					configuracion.setIdBanco(rs.getInt("ID_BANCO"));
					configuracion.setDescDivisa(rs.getString("DESC_DIVISA"));
					configuracion.setIdDivisa(rs.getString("ID_DIVISA"));
					configuracion.setDriver(rs.getString("DRIVER"));
					configuracion.setTipoImpresion(rs.getString("TIPO_IMPRESION"));
					configuracion.setNomEmpresa(rs.getString("NOM_EMPRESA"));
					configuracion.setNumEmpresa(rs.getString("NO_EMPRESA"));
					configuracion.setIdChequera(rs.getString("ID_CHEQUERA"));
					
					return configuracion;
			}});
			
		
		} catch (CannotGetJdbcConnectionException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaDaoImpl, M:obtenerConfiguraciones");
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaDaoImpl, M:obtenerConfiguraciones");
		}
		
		
		return listaConf;
	}
	
	@Override
	public List<LlenaComboGralDto> obtenerBancos(){
		List<LlenaComboGralDto> listaBancos = new ArrayList<LlenaComboGralDto>();
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT distinct b.ID_BANCO, DESC_BANCO FROM CAT_BANCO b \n");
		sb.append("JOIN CAT_CTA_BANCO cb ON b.id_banco= cb.id_banco \n");
		
		try {
			listaBancos = jdbcTemplate.query(sb.toString(), new RowMapper<LlenaComboGralDto>() {
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException{
					LlenaComboGralDto banco = new LlenaComboGralDto();
						banco.setId(rs.getInt("ID_BANCO"));
						banco.setDescripcion(rs.getString("DESC_BANCO"));
	
					return banco;
			}});
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaDaoImpl, M:obtenerConfiguraciones");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaDaoImpl, M:obtenerConfiguraciones");
		}
		
		return listaBancos;
	}
	
	@Override
	public List<LlenaComboChequeraDto> obtenerChequera(int idBanco, String idEmpresa){
		List<LlenaComboChequeraDto> listaChequeras = new ArrayList<LlenaComboChequeraDto>();
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT ID_CHEQUERA, DESC_CHEQUERA \n");
		sb.append("FROM CAT_CTA_BANCO WHERE ID_BANCO = "+Utilerias.validarCadenaSQL(idBanco)+" AND NO_EMPRESA = '"+Utilerias.validarCadenaSQL(idEmpresa)+"' \n");
		System.out.println(sb.toString());
		
		try {
			listaChequeras = jdbcTemplate.query(sb.toString(), new RowMapper<LlenaComboChequeraDto>() {
				public LlenaComboChequeraDto mapRow(ResultSet rs, int idx) throws SQLException{
					LlenaComboChequeraDto chequera = new LlenaComboChequeraDto();
						chequera.setId(rs.getString("ID_CHEQUERA"));
						chequera.setDescripcion(rs.getString("DESC_CHEQUERA"));
	
					return chequera;
			}});
					
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaDaoImpl, M:obtenerChequera");
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaDaoImpl, M:obtenerChequera");
		}
		
		return listaChequeras;
	}
	
	@Override
	public String insertarConfiguracion(List<Map<String, String>> datos) {
		String mensaje = "";
		StringBuffer sb = new StringBuffer();
				
		sb.append("INSERT INTO CAT_CHEQUE VALUES(\n");
		sb.append("CASE WHEN (SELECT MAX(ID_CONF) FROM CAT_CHEQUE) \n");
		sb.append(" IS NOT NULL THEN (SELECT MAX(ID_CONF) + 1 FROM CAT_CHEQUE) ELSE 1 END, \n");
		sb.append("'"+ Utilerias.validarCadenaSQL(datos.get(0).get("configuracion")) +"', "+ Utilerias.validarCadenaSQL(datos.get(0).get("banco")) +",  \n");
		sb.append(" '"+ Utilerias.validarCadenaSQL(datos.get(0).get("divisa")) +"', '"+ Utilerias.validarCadenaSQL(datos.get(0).get("driver")) +"', \n");
		sb.append(" '"+ Utilerias.validarCadenaSQL(datos.get(0).get("tipo")) +"', '"+ Utilerias.validarCadenaSQL(datos.get(0).get("empresa")) +"', \n");
		sb.append(" '"+ Utilerias.validarCadenaSQL(datos.get(0).get("chequera"))+"' ) \n");
		
		System.out.println(sb.toString());
		
		try {
		
			jdbcTemplate.update(sb.toString());
			mensaje = "Configuracion Guardada con exito";
			
		}catch(CannotGetJdbcConnectionException e){
			mensaje = "No se pudo obtener conexion";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaDaoImpl, M:insertarConfiguracion");
			
			
		} catch (Exception e) {
			mensaje = "Error al guardar";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaDaoImpl, M:insertarConfiguracion");
		}
		
		return mensaje;
	}

	@Override
	public String eliminarConfiguracion(int idConfiguracion){
		String mensaje = "";
		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM CAT_CHEQUE WHERE ID_CONF = "+Utilerias.validarCadenaSQL(idConfiguracion)+" \n");
		
		try {
			
			jdbcTemplate.update(sb.toString());
			mensaje = "Registro eliminado con exito";
			eliminarCampos(idConfiguracion);
			
		} catch (CannotGetJdbcConnectionException e){
			mensaje = "No se pudo obtener conexion";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaDaoImpl, M:eliminarConfiguracion");
			
		} catch (Exception e) {
			mensaje = "Error al eliminar";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaDaoImpl, M:eliminarConfiguracion");
		}
		
		return mensaje;
	}
	
	@Override
	public List<ConfiguracionChequeDto> obtenerCampos(int idConf){
		List<ConfiguracionChequeDto> listaCampos = new ArrayList<ConfiguracionChequeDto>();
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT * FROM conf_cheque  WHERE ID_CONF = "+ Utilerias.validarCadenaSQL(idConf) +" \n");
		System.out.println(sb.toString());
		try {
			
			listaCampos = jdbcTemplate.query(sb.toString(), new RowMapper<ConfiguracionChequeDto>() {
				public ConfiguracionChequeDto mapRow(ResultSet rs, int idx) throws SQLException{
					ConfiguracionChequeDto campos = new ConfiguracionChequeDto();
						campos.setIdCampo(rs.getInt("ID_CAMPO"));
						campos.setCampo(rs.getString("CAMPO"));
						campos.setIdConf(rs.getInt("ID_CONF"));
						campos.setTipoLetra(rs.getString("TIPO_LETRA"));
						campos.setTamanoLetra(rs.getInt("TAMANO_LETRA"));
						campos.setCoordX(rs.getDouble("COORD_X"));
						campos.setCoordY(rs.getDouble("COORD_Y"));
						campos.setFormato(rs.getString("FORMATO"));
					return campos;
			}});
			
		} catch (CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaDaoImpl, M:obtenerCampos");	
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaDaoImpl, M:obtenerCampos");
		}
		
		return listaCampos;
	}
	
	public String insertarCampos(List<Map<String, String>> datos){
		String mensaje = "";
		StringBuffer sb = new StringBuffer();
		
		sb.append("INSERT INTO conf_cheque VALUES(  \n");
		sb.append("'"+ Utilerias.validarCadenaSQL(datos.get(0).get("campo")) +"', "+ Utilerias.validarCadenaSQL(datos.get(0).get("idConf")) +", \n");
		sb.append("'"+ Utilerias.validarCadenaSQL(datos.get(0).get("fuente")) +"', "+ Utilerias.validarCadenaSQL(datos.get(0).get("tamano")) +",  \n");
		sb.append("'"+ Utilerias.validarCadenaSQL(datos.get(0).get("x")) +"', '"+ Utilerias.validarCadenaSQL(datos.get(0).get("y")) +"', \n");
		sb.append("'"+ Utilerias.validarCadenaSQL(datos.get(0).get("formato")) + "', CASE WHEN (SELECT MAX(ID_CAMPO) FROM conf_cheque) \n");
		sb.append(" IS NOT NULL THEN (SELECT MAX(ID_CAMPO) + 1 FROM conf_cheque) ELSE 1 END ) \n");
		
		System.out.println(sb.toString());
		
		try {
			jdbcTemplate.update(sb.toString());
			mensaje = "Registros guardado con exito";
		
		} catch (CannotGetJdbcConnectionException e){
			mensaje = "Se perdio la conexion con la base de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaDaoImpl, M:insertarCampos");	
			
		} catch (Exception e) {
			mensaje = "Error al guardar los registros";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaDaoImpl, M:insertarCampos");	
		}
		return mensaje;
	}
	
	@Override
	public String eliminarCampo(int idCampo){
		String mensaje = "";
		StringBuffer sb = new StringBuffer();
			
		sb.append("DELETE FROM conf_cheque WHERE ID_CAMPO = "+Utilerias.validarCadenaSQL(idCampo)+" \n" );
		
		try {
			jdbcTemplate.update(sb.toString());
			mensaje = "Registro eliminado con exito";
			
		} catch (CannotGetJdbcConnectionException e){
			mensaje = "Se perdio la conexion con la base de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaDaoImpl, M:eliminarCampo");
		} catch (Exception e) {
			mensaje = "Error al borrar el registros";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaDaoImpl, M:eliminarCampo");	
		}
		
		return mensaje;
	}
	
	
	public String eliminarCampos(int idConf){
		String mensaje = "";
		StringBuffer sb = new StringBuffer();
			
		sb.append("DELETE FROM conf_cheque WHERE ID_CONF = "+Utilerias.validarCadenaSQL(idConf)+" \n" );
		
		try {
			jdbcTemplate.update(sb.toString());
			mensaje = "Registro eliminado con exito";
			
		} catch (CannotGetJdbcConnectionException e){
			mensaje = "Se perdio la conexion con la base de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaDaoImpl, M:eliminarCampo");
		} catch (Exception e) {
			mensaje = "Error al borrar el registros";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ConfiguracionContinuaDaoImpl, M:eliminarCampo");	
		}
		
		return mensaje;
	}
	/****************************Getters and Setters **********************/
	
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:RentasDaoImpl, M:setDataSource");
		}
	}
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}


}
