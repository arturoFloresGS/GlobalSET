package com.webset.set.utilerias.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dao.EquivalenciaBancosDao;
import com.webset.set.utilerias.dto.EquivalenciaBancosDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utileriasmod.dto.MantenimientoLeyendasDto;
import com.webset.utils.tools.Utilerias;

public class EquivalenciaBancosDaoImpl implements EquivalenciaBancosDao{
	private JdbcTemplate jdbcTemplate;
	private Bitacora bitacora = new Bitacora();
	
	
	public List<EquivalenciaBancosDto> llenaGridBancos(String bankl, String banka,String idBanco,String descBanco) {
			List<EquivalenciaBancosDto> listaResultado = new ArrayList<EquivalenciaBancosDto>();
			//System.out.println("LLEGO DAO");
			StringBuffer sql = new StringBuffer();
			try{
				/*****REALIZA UNA CONSULTA INDIVIDUAL, POR EL TIPO DE LEYENDA : PARA LLENAR EL GRID DE LEYENDAS CUANDO SE REALIZA UNA BUSQUEDA ****/
					//System.out.println("Llego al query");
					sql.append("select z.BANKL,z.BANKA,cb.id_banco,cb.desc_banco ");
					sql.append("\n from zbancosext z");
					sql.append("\n LEFT JOIN equivale_banco eb ON z.BANKL=eb.id_banco_sap");
					sql.append("\n LEFT JOIN cat_banco cb ON eb.id_banco=cb.id_banco");
					System.out.println(sql.toString());
					listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<EquivalenciaBancosDto>(){
						public EquivalenciaBancosDto mapRow(ResultSet rs, int idx) throws SQLException{
							EquivalenciaBancosDto campos = new EquivalenciaBancosDto();
							campos.setBankl(rs.getString("bankl"));
							campos.setBanka(rs.getString("banka"));
							campos.setIdBanco(rs.getString("id_banco"));
							campos.setDescBanco(rs.getString("desc_banco"));
							return campos;
						}
					});
			}
			catch(Exception e){
				e.printStackTrace();
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:EquivalenciaBancosDaoImpl, M:llenaGridBancos");
			}
			return listaResultado;
		
	}
	
	@Override
	public List<EquivalenciaBancosDto> consultarBancos(String texto) {
		StringBuffer sql = new StringBuffer();
		List<EquivalenciaBancosDto> list = new ArrayList<EquivalenciaBancosDto>();
		try
		{
			
			sql.append("SELECT id_banco,desc_banco ");
			sql.append(" FROM cat_banco");
			sql.append(" WHERE desc_banco like '%"+Utilerias.validarCadenaSQL(texto)+"%'");
						
			System.out.println("consultarProveedores: " + sql);
			list= jdbcTemplate.query(sql.toString(), new RowMapper<EquivalenciaBancosDto>(){
			public EquivalenciaBancosDto mapRow(ResultSet rs, int idx) throws SQLException {
				EquivalenciaBancosDto cons = new EquivalenciaBancosDto();
				cons.setIdBanco(rs.getString("id_banco"));
				cons.setDescBanco(rs.getString("desc_banco"));
				return cons;
			}});
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Utilieras, C:EquivalenciaBancosDaoImpl, M:consultarBancos");
		}
		return list;
	 
	}
		
	@Override
	public List<EquivalenciaBancosDto> llenarComboBanco(EquivalenciaBancosDto dto) {
			String sql="";
			List<EquivalenciaBancosDto> listDatos= new ArrayList<EquivalenciaBancosDto>();
			try{
				sql+= "	SELECT	";
				if(dto.getCampoUno()!=null && !dto.getCampoUno().trim().equals(""))
					sql+=""+Utilerias.validarCadenaSQL(dto.getCampoUno())+", ";
				//aplica solo para combos que tengan el mismo campo como id y descripcion (String)
				//ejemplo: chequeras
				else
					sql+=""+2+"	as ID "+",";	
				if(dto.getCampoDos()!=null && !dto.getCampoDos().trim().equals(""));
					sql+=Utilerias.validarCadenaSQL(dto.getCampoDos());
			    sql+= "  FROM	";
			    if(dto.getTabla()!=null && !dto.getTabla().trim().equals(""))
			    	sql+=""+Utilerias.validarCadenaSQL(dto.getTabla());
			    if(dto.getCondicion()!=null && !dto.getCondicion().trim().equals(""))
			    	sql+="	WHERE	id_banco='"+Utilerias.validarCadenaSQL(dto.getCondicion())+"'";
			    /*if(dto.getOrden()!=null && !dto.getOrden().trim().equals(""))
			    	sql+="	ORDER BY	"+Utilerias.validarCadenaSQL(dto.getOrden());*/
			    System.out.println(sql);
			    
			    listDatos= jdbcTemplate.query(sql, new RowMapper<EquivalenciaBancosDto>(){
					public EquivalenciaBancosDto mapRow(ResultSet rs, int idx) throws SQLException {
						EquivalenciaBancosDto cons = new EquivalenciaBancosDto();
						cons.setIdBanco(rs.getString("id_banco"));
						cons.setDescBanco(rs.getString("desc_banco"));
						return cons;
					}});
			}catch(Exception e){
				e.printStackTrace();
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Utilerias, C:EquivalenciaBancosDaoImpl, M:llenarComboBanco");
			}
			return listDatos;
		
	}
	

	@Override
	public String actualizaEquivaleBanco(String bankl, String idBancoGrid, String idBancoText) {
		String mensaje = "";
		StringBuffer sql = new StringBuffer();
		System.out.println(bankl);
		System.out.println(idBancoGrid);
		System.out.println(idBancoText);
		try {
		if(idBancoGrid.equals("")){
			sql.append("insert into equivale_banco ");
			sql.append(" values('"+Utilerias.validarCadenaSQL(bankl)+"','"+Utilerias.validarCadenaSQL(idBancoText)+"')");
		}else{
			sql.append("update equivale_banco set id_banco='"+Utilerias.validarCadenaSQL(idBancoText)+"' ");
			sql.append(" where id_banco_sap='"+Utilerias.validarCadenaSQL(bankl)+"' ");
		}

		System.out.println(sql.toString());
		
		
			if(jdbcTemplate.update(sql.toString()) > 0){
				mensaje = "Banco agregado con exito";
			}else{
				mensaje = "Ocurrio un error al ingresar el banco";
			}
			
		}catch(CannotGetJdbcConnectionException e){
			e.printStackTrace();
			mensaje = "Se perdio la conexion contacte a su DBA";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Utilerias, C:EquivalenciaBancosDaoImpl, M:actualizaEquivaleBanco");
			
		}catch (Exception e) { 
			e.printStackTrace();
			mensaje = "Error durante el proceso de insercion";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:EquivalenciaBancosDaoImpl, M:actualizaEquivaleBanco");
		}
		
		return mensaje.toString(); 
	}
	
	@Override
	public List<Map<String, String>> reporteBancosExt() {
		List<Map<String, String>> listaResultado = new ArrayList<Map<String, String>>();
		try{
			StringBuffer sql = new StringBuffer();
			sql.append("select z.BANKL,z.BANKA,cb.id_banco,cb.desc_banco ");
			sql.append("\n from zbancosext z");
			sql.append("\n LEFT JOIN equivale_banco eb ON z.BANKL=eb.id_banco_sap");
			sql.append("\n LEFT JOIN cat_banco cb ON eb.id_banco=cb.id_banco");
	
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>(){
				public Map<String, String> mapRow(ResultSet rs, int idx)throws SQLException{
					Map<String, String> campos = new HashMap<String, String>();
					campos.put("bankl",rs.getString("bankl"));
					campos.put("banka",rs.getString("banka"));
					campos.put("idBanco",rs.getString("id_banco"));
					campos.put("descBanco",rs.getString("desc_banco"));

					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: EquivalenciaBancosExtDaoImpl, M: reporteBancosExt");
		}return listaResultado;	
	}
	
	public void setDataSource(DataSource dataSource)
	{
		try
		{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:EquivalenciaBancosDaoImpl, M:setDataSource");
		}
	}


}
