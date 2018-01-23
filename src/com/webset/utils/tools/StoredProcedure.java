package com.webset.utils.tools;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;

public class StoredProcedure implements CallableStatementCreator, CallableStatementCallback<HashMap<String, Object>>{
	public static final int OUT = 0;
	public static final int IN = 1;
	private HashMap<String, Object> parametros;
	
	private ArrayList<Integer> iSalidas;
	private ArrayList<Integer> tipos;
	private ArrayList<String> keys;
	private JdbcTemplate jdbcTemplate;
	private String nombre;
	
	public StoredProcedure(JdbcTemplate jdbcTemplate, String nombre) {
		this.nombre = nombre;
		parametros = new HashMap<String, Object>();
		iSalidas = new ArrayList<Integer>();
		keys = new ArrayList<String>();
		tipos = new ArrayList<Integer>();
		this.jdbcTemplate = jdbcTemplate;
	}
	
	
	public void declareParameter(String key, Object value, int type) {
		parametros.put(key, value);
		keys.add(key);
		tipos.add(type);
		if (type == OUT) {
			iSalidas.add(keys.size()-1);
		}
	}
	
	public HashMap<String, Object> execute() {
		return jdbcTemplate.execute(this, this);
	}
	
	@Override
	public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String sentencia = "{CALL " + nombre;
		String parametrosStr = "";
		for (int i = 0; i < keys.size(); i++) {
			parametrosStr += "?,";
		}
		sentencia += (!parametrosStr.equals("") ? "(" + parametrosStr.substring(0, parametrosStr.length()-1) + ")}" : "}");
		
		CallableStatement callableStatement = con.prepareCall(sentencia);
		
		for (int i = 0; i < keys.size(); i++) {
			if (tipos.get(i) == OUT) {
				callableStatement.registerOutParameter(i + 1, (Integer) parametros.get(keys.get(i)));
			} else {
				callableStatement.setObject(i + 1, parametros.get(keys.get(i)));
			}
		}
		return callableStatement;
	}

	@Override
	public HashMap<String, Object> doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		cs.execute();
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		for (Integer integer : iSalidas) {
			result.put(keys.get(integer), cs.getObject(integer + 1));
		}
		
		return result;		
	}
	

}
