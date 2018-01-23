package com.webset.set.utilerias.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dao.MantenimientoValorDivisaDao;
import com.webset.set.utileriasmod.dto.MantenimientoValorDivisaDto;
import com.webset.utils.tools.Utilerias;

public class MantenimientoValorDivisaDaoImpl implements MantenimientoValorDivisaDao{
	private JdbcTemplate jdbcTemplate = new JdbcTemplate();
	private Bitacora bitacora = new Bitacora();
	
	public List<MantenimientoValorDivisaDto> llenaComboTasas(){
		List<MantenimientoValorDivisaDto> listaResultado = new ArrayList<MantenimientoValorDivisaDto>();
		StringBuffer sql = new StringBuffer();
		try{			
			sql.append("SELECT * FROM cat_tasas");
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MantenimientoValorDivisaDto>(){
				public MantenimientoValorDivisaDto mapRow(ResultSet rs, int idx) throws SQLException{
					MantenimientoValorDivisaDto campos = new MantenimientoValorDivisaDto();
					campos.setIdTasa(rs.getString("id_tasa"));
					campos.setDescTasa(rs.getString("desc_tasa"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoValorDivisaDaoImpl, M: llenaComboTasas");
		}return listaResultado;
	}
	
	public List<MantenimientoValorDivisaDto> llenaComboDivisas(){
		List<MantenimientoValorDivisaDto> listaResultado = new ArrayList<MantenimientoValorDivisaDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("Select * from cat_divisa");
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MantenimientoValorDivisaDto>(){
				public MantenimientoValorDivisaDto mapRow(ResultSet rs, int idx) throws SQLException{
					MantenimientoValorDivisaDto campos = new MantenimientoValorDivisaDto();
					campos.setIdDivisa(rs.getString("id_divisa"));
					campos.setDescDivisa(rs.getString("desc_divisa"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoValorDivisaDaoImp, M:llenaComboDivisas");
		}return listaResultado;
	}
	
	public List<MantenimientoValorDivisaDto> llenaGridTasas(String fecha){
		List<MantenimientoValorDivisaDto> listaResultado = new ArrayList<MantenimientoValorDivisaDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("SELECT t.valor, t.id_tasa, ct.desc_tasa ");
			sql.append( "\n FROM tasa t, cat_tasas ct ");
			sql.append( "\n WHERE t.id_tasa = ct.id_tasa ");
			sql.append("\n AND t.fecha = convert(datetime,'"+ Utilerias.validarCadenaSQL(fecha) +"',103) ");
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MantenimientoValorDivisaDto>(){
				public MantenimientoValorDivisaDto mapRow(ResultSet rs, int idx) throws SQLException{
					MantenimientoValorDivisaDto campos = new MantenimientoValorDivisaDto();
					campos.setIdTasa(rs.getString("id_tasa"));
					campos.setDescTasa(rs.getString("desc_tasa"));
					campos.setValorTasa(rs.getString("valor"));
					System.out.println(campos);
					return campos;
					
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoValorDivisaDaoImpl, M:llenaComboTasas");
		}return listaResultado;
	}
		
	public List<MantenimientoValorDivisaDto> llenaGridDivisas(String fecha){
		List<MantenimientoValorDivisaDto> listaResultado = new ArrayList<MantenimientoValorDivisaDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append( "Select v.valor, v.id_divisa, c.desc_divisa ");
			sql.append("\n from valor_divisa v, cat_divisa c ");
			sql.append("\n where v.id_divisa = c.id_divisa ");
			sql.append("\n and v.fec_divisa = convert(datetime,'"+ Utilerias.validarCadenaSQL(fecha) +"',103) ");
			sql.append("\n and c.id_divisa <> 'CNT' ");	
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MantenimientoValorDivisaDto>(){
				public MantenimientoValorDivisaDto mapRow(ResultSet rs, int idx) throws SQLException{
					MantenimientoValorDivisaDto campos = new MantenimientoValorDivisaDto();
					campos.setIdDivisa(rs.getString("id_divisa"));
					campos.setDescDivisa(rs.getString("desc_divisa"));
					campos.setValorDivisa(rs.getString("valor"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoValorDivisaDaoImpl, M:llenaComboTasas");
		}return listaResultado;
	}
	
	public List<MantenimientoValorDivisaDto> llenaGridTasasDefault(){
		List<MantenimientoValorDivisaDto> listaResultado = new ArrayList<MantenimientoValorDivisaDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append( "Select 0 as valor, id_tasa, desc_tasa ");
			sql.append("\n from cat_tasas ");						
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MantenimientoValorDivisaDto>(){
				public MantenimientoValorDivisaDto mapRow(ResultSet rs, int idx) throws SQLException{
					MantenimientoValorDivisaDto campos = new MantenimientoValorDivisaDto();
					campos.setIdTasa(rs.getString("id_tasa"));
					campos.setDescTasa(rs.getString("desc_tasa"));
					campos.setValorTasa(rs.getString("valor"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoValorDivisaDaoImpl, M:llenaGridTasasDefault");
		}return listaResultado;
	}
	
	
	public List<MantenimientoValorDivisaDto> llenaGridDivisasDefault(){
		List<MantenimientoValorDivisaDto> listaResultado = new ArrayList<MantenimientoValorDivisaDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("Select 0 as valor, id_divisa, desc_divisa ");
			sql.append( "\n from cat_divisa ");
			sql.append("\n where ");
			sql.append("\n id_divisa <> 'CNT' ");			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MantenimientoValorDivisaDto>(){
				public MantenimientoValorDivisaDto mapRow(ResultSet rs, int idx) throws SQLException{
					MantenimientoValorDivisaDto campos = new MantenimientoValorDivisaDto();
					campos.setIdDivisa(rs.getString("id_divisa"));
					campos.setDescDivisa(rs.getString("desc_divisa"));
					campos.setValorDivisa(rs.getString("valor"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoValorDivisaDaoImpl, M:llenaGridDivisasDefault");
		}return listaResultado;
	}
	
	public List<MantenimientoValorDivisaDto> buscaRegistroTasa(String fecha, String idTasa){
		List<MantenimientoValorDivisaDto> listaResultado = new ArrayList<MantenimientoValorDivisaDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("Select * from tasa " );
			sql.append( "\n where id_tasa = '"+ Utilerias.validarCadenaSQL(idTasa) +"' " );
			sql.append( "\n and fecha = convert(datetime,'"+ Utilerias.validarCadenaSQL(fecha) +"',103) " );
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MantenimientoValorDivisaDto>(){
				public MantenimientoValorDivisaDto mapRow(ResultSet rs, int idx) throws SQLException{
					MantenimientoValorDivisaDto campos = new MantenimientoValorDivisaDto();
					campos.setIdTasa(rs.getString("id_tasa"));									
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoValorDivisaDaoImpl, M: buscaRegistroTasa");
		}return listaResultado;
	}
	
	public List<MantenimientoValorDivisaDto> buscaRegistroDivisa(String fecha, String idDivisa){
		List<MantenimientoValorDivisaDto> listaResultado = new ArrayList<MantenimientoValorDivisaDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append( "Select * from valor_divisa " );
			sql.append( "\n where id_divisa = '"+ Utilerias.validarCadenaSQL(idDivisa) +"' " );
			sql.append( "\n and fec_divisa = '"+ Utilerias.validarCadenaSQL(fecha) +"' " );	
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MantenimientoValorDivisaDto>(){
				public MantenimientoValorDivisaDto mapRow(ResultSet rs, int idx) throws SQLException{
					MantenimientoValorDivisaDto campos = new MantenimientoValorDivisaDto();
					campos.setIdDivisa(rs.getString("id_divisa"));										
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoValorDivisaDaoImpl, M: buscaRegistroDivisa");
		}return listaResultado;
	}
	
	public int actualizaTasa(List<Map<String, String>> gridTasa, int posicion, String fecha){
		int resultado = 0;
		StringBuffer sql = new StringBuffer(); 
		try{
			sql.append( "UPDATE tasa ");
			sql.append( "\n SET valor = "+ Utilerias.validarCadenaSQL(Double.parseDouble(gridTasa.get(posicion).get("valorTasa"))) +" ");
			sql.append( "\n where id_tasa = '"+ Utilerias.validarCadenaSQL(gridTasa.get(posicion).get("idTasa")) +"' " );
			sql.append("\n and fecha = convert(datetime,'"+ Utilerias.validarCadenaSQL(fecha) +"',103)" );
			resultado = jdbcTemplate.update(sql.toString());
			
		}	
		catch(Exception e){
			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoValorDivisaDaoImpl, M: actualizaTasa");
		}return resultado;
	}
	
	public int insertaTasa (List<Map<String, String>> gridTasa, int posicion, String fecha){
		int resultado = 0;
		StringBuffer sql = new StringBuffer(); 
		try{
			sql.append("Insert into tasa (id_tasa, fecha, valor) values( " );
			sql.append( "\n  '"+ Utilerias.validarCadenaSQL(gridTasa.get(posicion).get("idTasa")) +"', " );
			sql.append("\n '"+ Utilerias.validarCadenaSQL(fecha) +"', "+ Utilerias.validarCadenaSQL(Double.parseDouble(gridTasa.get(posicion).get("valorTasa"))) +"");
			sql.append( "\n  )" );
			resultado = jdbcTemplate.update(sql.toString());		
		}
		catch(Exception e){	
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoValorDivisaDaoImpl, M: insertaTasa");
		}return resultado;
	}
	
	public int actualizaDivisa(List<Map<String, String>> gridDivisa, int posicion, String fecha){
		int resultado = 0;
		StringBuffer sql = new StringBuffer(); 
		try{ 
			sql.append( "Update valor_divisa set ");
			sql.append( "\n valor = "+ Double.parseDouble(gridDivisa.get(posicion).get("valorDivisa")) +" ");
			sql.append( "\n where id_divisa = '"+ Utilerias.validarCadenaSQL(gridDivisa.get(posicion).get("idDivisa")) +"' ");
			sql.append( "\n and fec_divisa = convert(datetime,'"+ Utilerias.validarCadenaSQL(fecha) +"',103)");
			resultado = jdbcTemplate.update(sql.toString());
		}	
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoValorDivisaDaoImpl, M: actualizaDivisa");
		}return resultado;
	}
	
	public int insertaDivisa (List<Map<String, String>> gridDivisa, int posicion, String fecha){
		int resultado = 0;
		StringBuffer sql = new StringBuffer(); 
		try{
			sql.append("Insert into valor_divisa (id_divisa, fec_divisa, valor) values( " );
			sql.append( "\n  '"+ Utilerias.validarCadenaSQL(gridDivisa.get(posicion).get("idDivisa")) +"', " );
			sql.append( "\n '"+ Utilerias.validarCadenaSQL(fecha) +"', "+Double.parseDouble( gridDivisa.get(posicion).get("valorDivisa")) +"");
			sql.append( "\n )");
			resultado = jdbcTemplate.update(sql.toString());		
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoValorDivisaDaoImpl, M: insertaDivisa");
		}return resultado;
	}
	
	public int actualizaEstatusFecha(){
		int resultado = 0;
		StringBuffer sql = new StringBuffer(); 
		try{			
			sql.append("Update fechas SET estatus_sist = 1");
			resultado = jdbcTemplate.update(sql.toString());			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoValorDivisaDaoImpl, M: actualizaEstatusFecha");
		}return resultado;
	}
	
	//**************************************************************		
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoValorDivisaDaoImpl, M:setDataSource");
		}
	}
	
}
