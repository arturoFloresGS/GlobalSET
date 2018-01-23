package com.webset.set.utilerias.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dao.CorreoDao;

/***********
 * 
 * @author YOSELINE E.C
 * Fecha: 04 de febrero del 2015
 */
public class CorreoDaoImpl implements CorreoDao {
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
		

	public int funSQLActEmailGenerado(String psFoli_set, String pdImp_pago, String psNoDocto, String psNoempresa, String psBenef,String psMail) {
		StringBuffer sb= new StringBuffer();
		int resultado=0;
		try{
			sb.append("UPDATE movimiento");
			sb.append("\n SET hora_recibo = 'OK'");
			sb.append("\n WHERE no_folio_det="+ psFoli_set);
			sb.append("\n AND importe ="+pdImp_pago);
			sb.append("\n and no_docto = '" + psNoDocto + "'");
			sb.append("\n and id_forma_pago <> 7 ");
			sb.append("\n and no_empresa = '" + psNoempresa + "'");
			sb.append("\n and no_cliente = '" + psBenef + "'");
			resultado=jdbcTemplate.update(sb.toString());
			System.out.println(sb.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:CorreoDaoImpl, M:funSQLActEmailGenerado");
			e.printStackTrace();
		}return resultado;
	}

	public List<Map<String, String>> funSQLDetallePagoAgrupado(String psNoDocto, String pdGrupo) { //ByVal psNoDocto As String, ByVal pdGrupo As Double
		StringBuffer sql= new StringBuffer();
		List<Map<String, String>> listConsMovi  = new ArrayList<Map<String, String>>();
		try{
			
			sql.append(" select  m.no_docto as no_doc_sap,m.no_empresa, p.equivale_persona as no_benef, m.importe as imp_solic , m.no_factura , '' as causa_rech , m.po_headers as contra_rec \n");
			sql.append(" from movimiento m join persona  p on p.no_persona = m.no_cliente  \n");
			sql.append(" where m.id_estatus_mov= 'A' \n");
			sql.append(" and m.po_headers is not null \n");
			sql.append(" and m.id_tipo_operacion = 3000 \n");
			sql.append(" and m.no_docto = '"+psNoDocto+"' \n");
			sql.append(" and m.grupo_pago = "+ pdGrupo +" \n");
			
			System.out.println(sql.toString());
			listConsMovi = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>(){
				public Map<String, String> mapRow(ResultSet rs, int idx)throws SQLException{
					Map<String, String> map = new HashMap<String, String>();
					map.put("no_doc_sap", rs.getString("no_doc_sap"));
					map.put("no_empresa",  rs.getString("no_empresa"));
					map.put("no_benef",  rs.getString("no_benef"));
					map.put("imp_solic",  rs.getString("imp_solic"));
					map.put("no_factura",  rs.getString("no_factura"));
					map.put("causa_rech", rs.getString("causa_rech"));
					map.put("contra_rec",  rs.getString("contra_rec"));
					return map;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:CorreoDaoImpl, M:funSQLDetallePagoAgrupado");
			e.printStackTrace();
		}return listConsMovi;
		
	}

	public List<Map<String, String>> funSQLDetalleNoPagoAgrupado(String psNoDocto, String pdGrupo) { //ByVal psNoDocto As String, ByVal pdGrupo As Double
		StringBuffer sql= new StringBuffer();
		List<Map<String, String>> listConsMovi  = new ArrayList<Map<String, String>>();
		try{
			
			sql.append(" select  m.no_docto as no_doc_sap,m.no_empresa, p.equivale_persona as no_benef, m.importe as imp_solic , m.no_factura , '' as causa_rech , m.po_headers as contra_rec \n");
			sql.append(" from movimiento m join persona  p on p.no_persona = m.no_cliente  \n");
			sql.append(" where id_estatus_mov='H' \n");
//			sql.append(" and m.po_headers is not null \n");
			sql.append(" and m.id_tipo_operacion = 3201 \n");
			sql.append(" and m.no_docto = '"+psNoDocto+"' \n");
			sql.append(" and m.grupo_pago = "+ pdGrupo +" \n");
			
			System.out.println(sql.toString());
			listConsMovi = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>(){
				public Map<String, String> mapRow(ResultSet rs, int idx)throws SQLException{
					Map<String, String> map = new HashMap<String, String>();
					map.put("no_doc_sap", rs.getString("no_doc_sap"));
					map.put("no_empresa",  rs.getString("no_empresa"));
					map.put("no_benef",  rs.getString("no_benef"));
					map.put("imp_solic",  rs.getString("imp_solic"));
					map.put("no_factura",  rs.getString("no_factura"));
					map.put("causa_rech", rs.getString("causa_rech"));
					map.put("contra_rec",  rs.getString("contra_rec"));
					return map;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:CorreoDaoImpl, M:funSQLDetallePagoAgrupado");
			e.printStackTrace();
		}return listConsMovi;
		
	}

	public List<Map<String, String>> funSQLDatosCorreo(String string) {
		StringBuffer sql= new StringBuffer();
		List<Map<String, String>> listConsMovi  = new ArrayList<Map<String, String>>();
		try{
			
			sql.append("\n SELECT parrafo_1,  parrafo_2,  parrafo_3,  parrafo_4,  parrafo_5,");
			sql.append("\n parrafo_6,  parrafo_7,  parrafo_8,  parrafo_9,  parrafo_10,");
			sql.append("\n parrafo_11, parrafo_12, parrafo_13, parrafo_14, parrafo_15,");
			sql.append("\n parrafo_16, parrafo_17, parrafo_18, parrafo_19, parrafo_20,");
			sql.append("\n parrafo_21, parrafo_22, parrafo_23, parrafo_24, parrafo_25,");
			sql.append("\n parrafo_26");
			sql.append("\n From configura_mail");
			sql.append("\n WHERE no_correo = 1");
			System.out.println(sql.toString());
			listConsMovi = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>(){
			public Map<String, String> mapRow(ResultSet rs, int idx)throws SQLException{
					Map<String, String> map = new HashMap<String, String>();
					map.put("parrafo_1", rs.getString("parrafo_1"));
					map.put("parrafo_2", rs.getString("parrafo_2"));
					map.put("parrafo_3", rs.getString("parrafo_3"));
					map.put("parrafo_4", rs.getString("parrafo_4"));
					map.put("parrafo_5", rs.getString("parrafo_5"));
					map.put("parrafo_6", rs.getString("parrafo_6"));
					map.put("parrafo_7", rs.getString("parrafo_7"));
					map.put("parrafo_8", rs.getString("parrafo_8"));
					map.put("parrafo_9", rs.getString("parrafo_9"));
					map.put("parrafo_10", rs.getString("parrafo_10"));
					map.put("parrafo_11", rs.getString("parrafo_11"));
					map.put("parrafo_12", rs.getString("parrafo_12"));
					map.put("parrafo_13",rs.getString("parrafo_13"));
					map.put("parrafo_14", rs.getString("parrafo_14"));
					map.put("parrafo_15", rs.getString("parrafo_15"));
					map.put("parrafo_16", rs.getString("parrafo_16"));
					map.put("parrafo_17", rs.getString("parrafo_17"));
					map.put("parrafo_18", rs.getString("parrafo_18"));
					map.put("parrafo_19", rs.getString("parrafo_19"));
					map.put("parrafo_20", rs.getString("parrafo_20"));
					map.put("parrafo_21", rs.getString("parrafo_21"));
					map.put("parrafo_22", rs.getString("parrafo_22"));
					map.put("parrafo_23", rs.getString("parrafo_23"));
					map.put("parrafo_24", rs.getString("parrafo_24"));
					map.put("parrafo_25",rs.getString("parrafo_25"));
					map.put("parrafo_26", rs.getString("parrafo_26"));
					return map;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:CorreoDaoImpl, M:funSQLDatosCorreo");
			e.printStackTrace();
		}return listConsMovi;
	}

	public List<Map<String, String>> enviarCorreo() {
		StringBuffer sql= new StringBuffer();
		List<Map<String, String>> listConsMovi  = new ArrayList<Map<String, String>>();
		try{
		   sql.append("SELECT m.no_docto, m.no_folio_det, m.no_cliente, m.no_empresa, m.importe, m.fec_valor, m.grupo_pago , m.id_chequera_benef");
			sql.append("\n  ,m.id_forma_pago,m.beneficiario,m.tipo_cambio,m.id_divisa,m.id_chequera, m.referencia ");
			sql.append("\n  ,(select desc_banco from cat_banco cb where cb.id_banco = m.id_banco) as desc_banco");
			sql.append("\n ,(select desc_banco from cat_banco cb2 where cb2.id_banco = m.id_banco_benef) as desc_banco_benef");
			sql.append("\n  ,(select nom_empresa from empresa e where e.no_empresa = m.no_empresa ) as nom_empresa");
			sql.append("\n  ,COALESCE(me.contacto,'') as email");
			sql.append("\n ,(select equivale_persona from persona p where p.no_persona = m.no_cliente and id_tipo_persona = 'P') AS equivale_persona ");
			sql.append("\n FROM movimiento m, medios_contacto me");
			sql.append("\n  where m.id_estatus_mov in ('I', 'R', 'K') ");
			sql.append("\n AND me.no_persona =* m.no_cliente ");
			sql.append("\n AND m.id_tipo_operacion = 3200");
			sql.append("\n AND fec_conf_trans <= (select fec_hoy from fechas) and hora_recibo = '' ");
			System.out.println(sql.toString());
			listConsMovi = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>(){
				public Map<String, String> mapRow(ResultSet rs, int idx)throws SQLException{
					Map<String, String> map = new HashMap<String, String>();
					map.put("email", rs.getString("email"));
					map.put("no_docto", rs.getString("no_docto"));
					map.put("no_folio_det", rs.getString("no_folio_det"));
					map.put("no_cliente", rs.getString("no_cliente"));
					map.put("no_Empresa", rs.getString("no_Empresa"));
					map.put("importe", rs.getString("importe"));
					map.put("nom_empresa", rs.getString("nom_empresa"));
					map.put("fec_valor", rs.getString("fec_valor"));
					map.put("grupo_pago", rs.getString("grupo_pago"));
					map.put("id_chequera_benef", rs.getString("id_chequera_benef"));
					map.put("desc_banco_benef", rs.getString("desc_banco_benef"));
					map.put("id_forma_pago", rs.getString("id_forma_pago"));
					map.put("beneficiario", rs.getString("beneficiario"));
					map.put("tipo_cambio", rs.getString("tipo_cambio"));
					map.put("id_divisa", rs.getString("id_divisa"));
					map.put("id_chequera", rs.getString("id_chequera"));
					map.put("referencia", rs.getString("referencia"));
					map.put("desc_banco", rs.getString("desc_banco"));
					map.put("equivale_persona", rs.getString("equivale_persona"));
					return map;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:CorreoDaoImpl, M:funSQLSelectPagosEnviarCorreo");
			e.printStackTrace();
		}return listConsMovi;
	}
	
	/**
	 * consulta la tabla configura_set y obtiene el valor segun el indice
	 * 
	 * @param indice
	 * @return
	 */
	
	public String consultarConfiguraSet(int indice) {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT valor ");
			sb.append("\n FROM configura_set");
			sb.append("\n WHERE indice = " + indice);
			List<String> valores = jdbcTemplate.query(sb.toString(),
					new RowMapper<String>() {
						public String mapRow(ResultSet rs, int idx)
								throws SQLException {
							String valor = rs.getString("valor");
							return valor;
						}
					});
			return valores.isEmpty()?"":valores.get(0);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Utilerias, C:ConsultasGenerales, M:ConsultaConfiguraSet");
			e.printStackTrace();
			return "";
		}
	}

	public String consultarCorreoPorNoCliente(int no_cliente) {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("\n select contacto");
			sb.append("\n from medios_contacto");
			sb.append("\n where no_persona = "+ no_cliente);
			sb.append("\n and id_tipo_persona = 'P'");
			sb.append("\n and id_tipo_medio = 'MAIL'");
			List<String> valores = jdbcTemplate.query(sb.toString(),
					new RowMapper<String>() {
						public String mapRow(ResultSet rs, int idx)
								throws SQLException {
							String valor = rs.getString("contacto");
							return valor;
						}
					});
			return valores.isEmpty()?"":valores.get(0);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Utilerias, C:ConsultasGenerales, M:ConsultaConfiguraSet");
			e.printStackTrace();
			return "";
		}
	}
	
	public List<String> correosAdicionalesPorNoCliente(Integer noCliente){
		List<String> correos = new ArrayList<String>();
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT CORREO FROM CORREOS_PERSONA WHERE NO_PERSONA "+ noCliente + " AND ESTATUS = 'A' ");
			
			correos = jdbcTemplate.query(sb.toString(),
					new RowMapper<String>() {
						public String mapRow(ResultSet rs, int idx)
								throws SQLException {
							String valor = rs.getString("contacto");
							return valor;
						}
					});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Utilerias, C:ConsultasGenerales, M:ConsultaConfiguraSet");
			e.printStackTrace();
		}
		
		return correos;
	}

	
	/***********************************************************/
	
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:CorreoDaoImpl, M:setDataSource");
		}
	}

	
}
