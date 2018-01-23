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
import com.webset.set.utilerias.dao.EquivalenciaEmpresasDao;
import com.webset.set.utileriasmod.dto.EquivalenciaEmpresasDto;

public class EquivalenciaEmpresasDaoImpl implements EquivalenciaEmpresasDao{
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora;
	String sql = "";
	
	@SuppressWarnings("unchecked")
	public List<EquivalenciaEmpresasDto> llenaComboEmpresas(){
		List<EquivalenciaEmpresasDto> listaResultado = new ArrayList<EquivalenciaEmpresasDto>();
		sql = "";
		
		try{
			sql = "Select * from empresa";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public EquivalenciaEmpresasDto mapRow(ResultSet rs, int idx) throws SQLException{
					EquivalenciaEmpresasDto campos = new EquivalenciaEmpresasDto();
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setNomEmpresa(rs.getString("nom_empresa"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: EquivalenciaEmpresasDaoImpl, M: llenaComboEmpresas");
		}return listaResultado;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<EquivalenciaEmpresasDto> llenaGrid(String nomEmpresa){
		List<EquivalenciaEmpresasDto> listaResultado = new ArrayList<EquivalenciaEmpresasDto>();
		sql = "";
		
		try{
			
			sql = "Select s.siscod as codigo, s.setemp as empresaSet, s.soiemp as empresaInterface, e.nom_empresa as descripcion ";
			sql += "\n from SET006 S, empresa e ";
			sql += "\n where s.setemp = e.no_empresa ";
			
			if (!nomEmpresa.equals(""))
				sql += " and e.nom_empresa like '%"+ nomEmpresa +"%' ";
			
			sql += "order by s.setemp ";
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public EquivalenciaEmpresasDto mapRow(ResultSet rs, int idx) throws SQLException{
					EquivalenciaEmpresasDto campos = new EquivalenciaEmpresasDto();
					campos.setCodigo(rs.getString("codigo"));
					campos.setEmpresaSet(rs.getString("empresaSet"));
					campos.setEmpresaInterface(rs.getString("empresaInterface"));
					campos.setDescripcion(rs.getString("descripcion"));
					return campos;
				}
			});

		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: EquivalenciaEmpresasDaoImpl, M: llenaGrid");
		}return listaResultado;
	}
	
	
	public int eliminaRegistro(String codigo, String empresaSet, String empresaInterface){
		int resultado = 0;
		sql = ""; 
		
		try{
			sql = " Delete from SET006 ";
			sql += "\n where siscod = '"+ codigo +"' and setemp = '"+ empresaSet +"' ";
			sql += "\n and soiemp = '"+ empresaInterface +"' ";
			System.out.println(sql);	
			resultado = jdbcTemplate.update(sql);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: EquivalenciaEmpresasDaoImpl, M: eliminaRegistro");
		}return resultado;
	}
	
	public int actualizaEmpresa(List<Map<String, String>> registro){
		int resultadoEntero = 0;
		sql = "";
		
		try{
			sql = "Update SET006 Set ";
			sql += "\n siscod = '"+ registro.get(0).get("codigo") +"', ";
			sql += "\n setemp = "+ registro.get(0).get("empresaSet") +", ";
			sql += "\n soiemp = '"+ registro.get(0).get("empresaInterface") +"' ";
			sql += "\n where ";
			sql += "\n siscod = '"+ registro.get(0).get("codigoOri") +"' ";
			sql += "\n and setemp = "+ registro.get(0).get("empresaSetOri") +" ";
			sql += "\n and soiemp = '"+ registro.get(0).get("empresaInterfaceOri") +"'";
			
			System.out.println(sql);
			resultadoEntero = jdbcTemplate.update(sql);
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: EquivalenciaEmpresasDaoImpl, M: actualizaEmpresa");
		}return resultadoEntero;		
	}
	
	@SuppressWarnings("unchecked")
	public List<EquivalenciaEmpresasDto> buscaRegistro(List<Map<String, String>> registro){
		List<EquivalenciaEmpresasDto> listaResultado = new ArrayList<EquivalenciaEmpresasDto>();
		sql = "";
		try{
			sql = "Select * from SET006 ";
			sql += "\n where ";
			sql += "\n siscod = '"+ registro.get(0).get("codigo") +"' ";
			sql += "\n and setemp = "+ registro.get(0).get("empresaSet") +" ";
			sql += "\n and soiemp = '"+ registro.get(0).get("empresaInterface") +"'";	
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public EquivalenciaEmpresasDto mapRow(ResultSet rs, int idx) throws SQLException{
					EquivalenciaEmpresasDto campos = new EquivalenciaEmpresasDto();
					campos.setCodigo(rs.getString("siscod"));
					campos.setEmpresaSet(rs.getString("setemp"));
					campos.setEmpresaInterface(rs.getString("soiemp"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: EquivalenciaEmpresasDaoImpl, M: buscaRegistro");
		}return listaResultado;
	}
	
	
	public int insertaEmpresa(List<Map<String, String>> registro){
		int resultadoEntero = 0;
		sql = "";
		try{
			sql = "Insert into SET006 (siscod, setemp, soiemp) ";
			sql += " values ( ";
			sql += "'"+ registro.get(0).get("codigo") +"',  "+ registro.get(0).get("empresaSet") +", ";
			sql += "'"+ registro.get(0).get("empresaInterface") +"')";
			
			resultadoEntero = jdbcTemplate.update(sql);
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: EquivalenciaEmpresasDaoImpl, M: insertaEmpresa");
		}return resultadoEntero;
	}
	
	//******************************************************************************************************************************
	public void setDataSource(DataSource dataSource)
	{
		try
		{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C: EquivalenciaEmpresasDaoImpl, M:setDataSource");
		}
	}
	
}
