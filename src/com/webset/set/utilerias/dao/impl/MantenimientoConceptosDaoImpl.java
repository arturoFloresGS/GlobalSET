package com.webset.set.utilerias.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dao.MantenimientoConceptosDao;
import com.webset.set.utileriasmod.dto.MantenimientoConceptosDto;
import com.webset.utils.tools.Utilerias;

public class MantenimientoConceptosDaoImpl implements MantenimientoConceptosDao{
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora;
	String sql = "";
	
	@SuppressWarnings("unchecked")	
	public List<MantenimientoConceptosDto> llenaBancos(){
		List<MantenimientoConceptosDto> listaResultado = new ArrayList<MantenimientoConceptosDto>();
		sql = "";
		
		try{			
			sql = "Select id_banco, desc_banco from cat_banco ";
			sql += "\n where b_banca_elect in ('I','E','A')";
									
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public MantenimientoConceptosDto mapRow(ResultSet rs, int idx) throws SQLException{
					MantenimientoConceptosDto campos = new MantenimientoConceptosDto();
					campos.setIdBanco(rs.getInt("id_banco"));
					campos.setDescBanco(rs.getString("desc_banco"));
					return campos;
				}
			});			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoConceptosDaoImpl, M:llenaBancos");
		}return listaResultado;		
	}
	
	@SuppressWarnings("unchecked")
	public List<MantenimientoConceptosDto> llenaFormaPago (String ingresoEgreso){
		List<MantenimientoConceptosDto> listaResultado = new ArrayList<MantenimientoConceptosDto>();
		sql = "";
		
		try{
			sql = "Select id_forma_pago, desc_forma_pago ";
			sql += "\n from regla_concepto ";
			sql += "\n where ingreso_egreso = '"+ ingresoEgreso +"' ";
						
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public MantenimientoConceptosDto mapRow(ResultSet rs, int idx) throws SQLException{
					MantenimientoConceptosDto campos = new MantenimientoConceptosDto();
					campos.setIdFormaPago(rs.getInt("id_forma_pago"));
					campos.setDescFormaPago(rs.getString("desc_forma_pago"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoConceptosDaoImpl, M: llenaFormaPago");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<MantenimientoConceptosDto> llenaGrid(int idBanco, String conceptoBanco, String formaPago, String ingresoEgreso){
		
		List<MantenimientoConceptosDto> listaResultado = new ArrayList<MantenimientoConceptosDto>();
		sql = "";
		
		try{			
			sql = "Select ec.id_banco, cb.desc_banco, ec.desc_concepto, ec.concepto_set, coalesce(ec.clas_chequera, '') as clas_chequera, ";
			sql += "\n ec.b_salvo_buen_cobro, ec.cargo_abono, ec.despliega, ec.importa, ";
			sql += "\n coalesce(ec.b_rechazo, 'N') as b_rechazo, ec.b_cargo_cta, coalesce(id_cve_leyenda, '') as id_cve_leyenda ";
			sql += "\n from equivale_concepto ec, cat_banco cb";
			sql += "\n where ec.id_banco = "+ Utilerias.validarCadenaSQL(idBanco) +" ";
			sql += "\n and ec.cargo_abono = '"+ Utilerias.validarCadenaSQL(ingresoEgreso) +"' ";
						
			if (!conceptoBanco.equals("")){
				sql += "\n and ec.desc_concepto like '%"+ Utilerias.validarCadenaSQL(conceptoBanco) +"%' ";
			}
			
			if (!formaPago.equals("")){
				sql += "\n and ec.concepto_set in ( ";
				sql += "\n select distinct concepto_set from regla_concepto ";
				sql += "\n where desc_forma_pago = '"+ Utilerias.validarCadenaSQL(formaPago) +"' ";
				sql += "\n and ingreso_egreso = '"+ Utilerias.validarCadenaSQL(ingresoEgreso) +"')";
			}
			
			sql += "\n and ec.id_banco = cb.id_banco ";
			
			sql += "\n order by ec.desc_concepto";
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public MantenimientoConceptosDto mapRow(ResultSet rs, int idx)throws SQLException{
					MantenimientoConceptosDto campos = new MantenimientoConceptosDto();
					campos.setIdBanco(rs.getInt("id_banco"));
					campos.setDescBanco(rs.getString("desc_banco"));
					campos.setConceptoBanco(rs.getString("desc_concepto"));
					campos.setConceptoSet(rs.getString("concepto_set"));
					campos.setClasChequera(rs.getString("clas_chequera"));
					campos.setSbc(rs.getString("b_salvo_buen_cobro"));
					campos.setCargoAbono(rs.getString("cargo_abono"));
					campos.setDespliega(rs.getString("despliega"));
					campos.setImporta(rs.getString("importa"));
					campos.setRechazo(rs.getString("b_rechazo"));
					campos.setCargoCuenta(rs.getString("b_cargo_cta"));
					campos.setCveConcepto(rs.getString("id_cve_leyenda"));
					
					return campos;
				}
			});
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoConceptosDaoImpl, M: llenaGrid");
		}return listaResultado;
	}
	
	
	public int actualizaConcepto(MantenimientoConceptosDto obj){
		int resultado = 0;
		sql = "";
		
		try
		{			
			sql = "";
			sql += "Update equivale_concepto ";
			sql += "\n Set despliega = '"+ obj.getDespliega() +"', importa = '"+ obj.getImporta() +"', ";
			sql += "\n clas_chequera = '"+ obj.getClasChequera() +"', b_rechazo = '"+ obj.getRechazo() +"', ";
			sql += "\n b_cargo_cta = '"+ obj.getCargoCuenta() +"', id_cve_leyenda = '"+ obj.getCveConcepto() +"', ";
			sql += "\n desc_concepto = '"+ obj.getConceptoBanco() +"', cargo_abono = '"+ obj.getCargoAbono() +"', ";
			sql += "\n concepto_set = '"+ obj.getConceptoSet() +"' ";
			sql += "\n where ";
			sql += "\n id_banco = "+ obj.getIdBanco() +" ";
			sql += "\n and desc_concepto = '"+ obj.getConceptoBanco() +"' ";
			//sql += "\n and concepto_set = '"+ obj.getConceptoSet() +"' ";
			
			System.out.println("Query update:" + sql);
			
			resultado = jdbcTemplate.update(sql);			
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoConceptosDaoImpl, M: actualizaConcepto");
		}return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<MantenimientoConceptosDto> buscaConcepto(MantenimientoConceptosDto obj){
		List<MantenimientoConceptosDto> listaResultado = new ArrayList<MantenimientoConceptosDto>();		
		sql = "";
		
		try{			
			sql = "Select desc_concepto ";
			sql += "\n from equivale_concepto ";
			sql += "\n where desc_concepto = '"+ obj.getConceptoBanco() +"' ";
			sql += "\n and id_banco = "+ obj.getIdBanco() +" ";
			sql += "\n and cargo_abono = '"+ obj.getCargoAbono() +"' ";
						
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public MantenimientoConceptosDto mapRow(ResultSet rs, int idx) throws SQLException{
					MantenimientoConceptosDto campos = new MantenimientoConceptosDto ();
					campos.setConceptoBanco(rs.getString("desc_concepto"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoConceptosDaoImpl, M:buscaConcepto");
		}return listaResultado;		
	}
	
	@SuppressWarnings("unchecked")
	public List<MantenimientoConceptosDto> buscaFormaPago (MantenimientoConceptosDto obj){
		List<MantenimientoConceptosDto> listaResultado = new ArrayList<MantenimientoConceptosDto>();		
		sql = "";
		
		try{
			sql = "Select concepto_set, b_salvo_buen_cobro ";
			sql += "\n from regla_concepto ";
			sql += "\n where ingreso_egreso = '"+ obj.getCargoAbono() +"' ";
			//Se comenta esta linea por que el JS quita los ID repetidos por default
			//sql += "\n and id_forma_pago = "+ obj.getIdFormaPago() +" ";
			sql += "\n and desc_forma_pago = '"+ obj.getConceptoSet() +"' ";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public MantenimientoConceptosDto mapRow(ResultSet rs, int idx) throws SQLException{
					MantenimientoConceptosDto campos = new MantenimientoConceptosDto();
					campos.setConceptoSet(rs.getString("concepto_set"));
					campos.setSbc(rs.getString("b_salvo_buen_cobro"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoConceptosDaoImpl, M: buscaFormaPago");
		}return listaResultado;
	}
	
	public int insertaConcepto(MantenimientoConceptosDto obj){
		int resultado = 0;
		sql = "";
		
		try{			
			sql = "Insert into equivale_concepto ( ";
			sql += "\n id_banco, desc_concepto, concepto_set, ";
			sql += "\n b_salvo_buen_cobro, cargo_abono, despliega, ";
			sql += "\n importa, clas_chequera, b_rechazo, b_cargo_cta, id_cve_leyenda)";
			sql += "\n values (";
			sql += "\n "+ obj.getIdBanco() +", '"+ obj.getConceptoBanco() +"', '"+ obj.getConceptoSet() +"', ";
			sql += "\n '"+ obj.getSbc() +"', '"+ obj.getCargoAbono() +"', '"+ obj.getDespliega() +"', ";
			sql += "\n '"+ obj.getImporta() +"', '"+ obj.getClasChequera() +"', '"+ obj.getRechazo() +"', '"+ obj.getCargoCuenta() +"', '"+ obj.getCveConcepto() +"' )";
			
			System.out.println("insert " + sql);
			
			resultado = jdbcTemplate.update(sql);
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoConceptosDaoImpl, M: insertaConcepto");
		}return resultado;
	}
	
	public int eliminaConcepto (int idBanco, String conceptoBanco, String ingresoEgreso){
		int resultado = 0;
		sql = "";
		
		try{
			sql = "Delete from equivale_concepto ";
			sql += "\n where id_banco = "+ idBanco +" ";
			
			if (!conceptoBanco.equals(""))
				sql += "\n and desc_concepto = '"+ conceptoBanco +"' ";
			
			if (!ingresoEgreso.equals(""))
				sql += "\n and cargo_abono = '"+ ingresoEgreso +"' ";
			else
				sql += "\n and cargo_abono in ('E', 'I') ";
			
			resultado = jdbcTemplate.update(sql);
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoConceptosDaoImpl, M: eliminaConcepto");
		}return resultado;		
	}
	
	@SuppressWarnings("unchecked")
	public List<MantenimientoConceptosDto> llenaTipoOperacion(){
		List<MantenimientoConceptosDto> listaResultado = new ArrayList<MantenimientoConceptosDto>();
		sql = "";
		String ingresoEgreso = "I";
		try{			
			sql = "Select distinct cto.id_tipo_operacion, cto.desc_tipo_operacion as descripcion ";
			sql += "\n from cat_tipo_operacion cto, regla_concepto rc ";
			sql += "\n where cto.id_tipo_operacion = rc.id_tipo_operacion ";
			
			if (!ingresoEgreso.equals(""))
				sql += "\n and rc.ingreso_egreso = '"+ ingresoEgreso +"' ";
			
			sql += "\n order by cto.id_tipo_operacion";	
									
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public MantenimientoConceptosDto mapRow(ResultSet rs, int idx) throws SQLException{
					MantenimientoConceptosDto campos = new MantenimientoConceptosDto();
					campos.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
					campos.setDescripcion(rs.getString("descripcion"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoConceptosDaoImpl, M: llenaTipoOperacion");
		}return listaResultado;
	}
	
//**************
	public void setDataSource(DataSource dataSource)
	{
		try
		{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoConceptosDaoImpl, M:setDataSource");
		}
	}
//consulta a sql 
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> ejecutarReporteConcepto(Map<String, Object> parameters) {
		List<Map<String, Object>> resultado = null;
		String gsDBM = "SQL SERVER";
		String sql = "";
		try{
			sql = "Select ec.id_banco, cb.desc_banco, ec.desc_concepto, ec.concepto_set, coalesce(ec.clas_chequera, '') as clas_chequera, ";
			sql += "\n ec.b_salvo_buen_cobro, ec.cargo_abono, ec.despliega, ec.importa, ";
			sql += "\n coalesce(ec.b_rechazo, 'N') as b_rechazo, ec.b_cargo_cta, coalesce(id_cve_leyenda, '') as id_cve_leyenda ";
			sql += "\n from equivale_concepto ec, cat_banco cb";
			sql += "\n where ec.id_banco = cb.id_banco and ec.id_banco = "+ Utilerias.validarCadenaSQL(parameters.get("idBanco").toString()) +" ";
			sql += "\n and ec.cargo_abono = '"+ Utilerias.validarCadenaSQL(parameters.get("ingresoEgreso").toString()) +"' ";

			if (!parameters.get("conceptoBanco").equals("")){
				sql += "\n and ec.desc_concepto like '%"+ Utilerias.validarCadenaSQL(parameters.get("conceptoBanco").toString()) +"%' ";
			}
			if (!parameters.get("cmbFormaPago").equals("")){
				sql += "\n and ec.concepto_set in ( ";
				sql += "\n select distinct concepto_set from regla_concepto ";
				sql += "\n where desc_forma_pago = '"+ Utilerias.validarCadenaSQL(parameters.get("formaPago").toString()) +"' ";
				sql += "\n and ingreso_egreso = '"+ Utilerias.validarCadenaSQL(parameters.get("ingresoEgreso").toString()) +"')";
			}
			
			//sql += "\n and ec.id_banco = cb.id_banco ";
			
			sql += "\n order by ec.desc_concepto";
			System.out.println("ejecutarReporteConcepto: " + sql);
			
			resultado = (List<Map<String, Object>>)jdbcTemplate.query(sql.toString(), new RowMapper(){
				public Object mapRow(ResultSet rs, int i)
						throws SQLException {
					Map<String, Object> results = new HashMap<String, Object>();
					
					results.put("id_banco",rs.getString("id_banco"));
					results.put("desc_banco", rs.getString("desc_banco"));
					results.put("desc_concepto", rs.getString("desc_concepto"));
					results.put("concepto_set", rs.getString("concepto_set"));
					results.put("clas_chequera", rs.getString("clas_chequera"));
					results.put("b_salvo_buen_cobro", rs.getString("b_salvo_buen_cobro"));
					results.put("cargo_abono", rs.getString("cargo_abono"));
					results.put("despliega", rs.getString("despliega"));
					results.put("importa", rs.getString("importa"));
					results.put("b_rechazo", rs.getString("b_rechazo"));
					results.put("b_cargo_cta", rs.getString("b_cargo_cta"));
					results.put("id_cve_leyenda", rs.getString("id_cve_leyenda"));
					
					return results;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:ReportesBancaXMLDao, M:ejecutarReporteBE");
		}
		return resultado;
	}
	//fin 
}
