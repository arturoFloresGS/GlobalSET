package com.webset.set.inversiones.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.utilerias.Bitacora;
import com.webset.set.inversiones.dao.RepInversionesDao;
import com.webset.utils.tools.Utilerias;
import com.webset.set.utilerias.ConstantesSet;

public class RepInversionesDaoImpl implements RepInversionesDao {
	private JdbcTemplate jdbcTemplate;
	private Bitacora bitacora = new Bitacora();
	private static Logger logger = Logger.getLogger(RepInversionesDaoImpl.class);

	

	
	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.dao.RepInversionesDao#consultarReporteInversiones(int, java.lang.String, java.lang.String, int, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> consultarReporteInversiones(int noEmpresa,
			String fechaInicial, String fechaFinal, String anio, int noInstitucion, String idDivisa) {
		StringBuffer sbSql = new StringBuffer();
		List <Map<String, Object>> lista = null;

		logger.debug("Entra consultarReporteInversiones");
		
		sbSql.append("SELECT a.importe, a.plazo, a.tasa, a.interes, a.isr, (a.interes - a.isr) as it, ");
		sbSql.append("(a.interes - a.isr + a.importe) as mt ");
		sbSql.append("a.id_papel, to_char(a.fec_alta, 'dd/mm/yyyy') fec_alta, ");
		sbSql.append("to_char(a.fec_venc, 'dd/mm/yyyy') fec_venc,  ");
		sbSql.append("b.nombre_corto, d.desc_tipo_valor,  ");
		sbSql.append("FROM orden_inversion a, persona b, persona c, cat_tipo_valor d ");
		sbSql.append("WHERE a.no_institucion = b.no_persona ");
		sbSql.append("AND a.no_contacto = c.no_persona ");
		sbSql.append("AND a.id_tipo_valor = d.id_tipo_valor ");
		sbSql.append("AND b.no_empresa = 1 ");
		sbSql.append("AND c.no_empresa = 1 ");
		sbSql.append("AND A.id_estatus_ord IN('C','A','V') ");
		if (noEmpresa != 0)
			sbSql.append("AND a.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresa) + " ");
		if (fechaInicial.length() == 0 || fechaFinal.length() == 0)
			sbSql.append("AND to_char (a.fec_alta, 'yyyy') = '" + anio + "' ");
		else{
			sbSql.append("AND a.fec_alta >= to_date('" + Utilerias.validarCadenaSQL(fechaInicial) + "', 'dd/mm/yyyy') ");
			sbSql.append("AND a.fec_alta < to_date('" + Utilerias.validarCadenaSQL(fechaInicial) + "', 'dd/mm/yyyy') + 1 ");
		}
		if (idDivisa.length() > 0){
			sbSql.append("AND d.id_divisa = '" + Utilerias.validarCadenaSQL(idDivisa) + "' ");
		}
		if (noInstitucion > 0){
			sbSql.append("AND a.no_institucion = " + Utilerias.validarCadenaSQL(noInstitucion) + " ");
		}

		sbSql.append("ORDER BY a.fec_alta, b.nombre_corto ");
		
		try{
			lista = jdbcTemplate.queryForList(sbSql.toString());
		}catch (Exception e){
			logger.error(e.getLocalizedMessage());
		}
		
		return lista;
	}
	
	

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.dao.RepInversionesDao#consultarReporteInvEstablecidas(int, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> consultarReporteInvEstablecidas(int noEmpresa, String idDivisa, String fechaInicial, 
			String fechaFinal, int idUsuario) {
		StringBuffer sbSql = new StringBuffer();
		List <Map<String, Object>> lista = null;

		logger.info("Entra consultarReporteInvEstablecidas");
		
		sbSql.append("SELECT distinct oi.fec_alta, oi.fec_venc, e.nom_empresa, oi.no_orden, oi.no_cuenta, ");
		sbSql.append("id_papel, ce.desc_estatus, c.id_divisa, id_tipo_valor, p.razon_social, ");
		sbSql.append("oi.importe + coalesce (m1.importe, oi.interes) - coalesce (m2.importe, oi.isr) as total,cast(oi.plazo as INT) as plazo, oi.tasa, ");
		sbSql.append("case c.valor_salida when 3 then 'Transferencia' when 5 then 'Cargo en cuenta' end as forma_pago, ");
		sbSql.append("id_estatus_ord , oi.importe, coalesce (m1.importe, oi.interes) AS interes, coalesce (m2.importe, oi.isr) AS isr ");
		sbSql.append("FROM orden_inversion oi LEFT JOIN movimiento m1 ON m1.no_empresa = oi.no_empresa AND m1.no_docto = oi.no_orden AND m1.id_tipo_operacion = 4103 ");
		sbSql.append("LEFT JOIN movimiento m2 ON m2.no_empresa = oi.no_empresa AND m2.no_docto = oi.no_orden AND m2.id_tipo_operacion = 4104, ");
		sbSql.append("persona p, cat_estatus ce, cuenta c, empresa e, ctas_contrato cc ");
		sbSql.append("WHERE oi.no_institucion = p.no_persona ");
		sbSql.append("AND oi.no_empresa = e.no_empresa ");
		sbSql.append("AND oi.no_empresa = c.no_empresa ");
		sbSql.append("AND oi.no_empresa = cc.no_empresa ");
		sbSql.append("AND oi.no_empresa = c.no_persona ");
		sbSql.append("AND oi.no_cuenta = c.no_cuenta ");
		sbSql.append("AND oi.id_estatus_ord = ce.id_estatus ");
		sbSql.append("AND ce.clasificacion = 'ORD' ");
		sbSql.append("AND oi.id_estatus_ord <> 'X' ");
		sbSql.append("AND oi.no_cuenta = cc.no_cuenta ");
		sbSql.append("AND oi.id_banco = cc.id_banco ");
		sbSql.append("AND oi.id_chequera = cc.id_chequera ");
		
		if (noEmpresa != 0){
			sbSql.append("AND oi.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresa) + " ");
		}
		else{
			sbSql.append("AND oi.no_empresa IN (SELECT e.no_empresa  FROM usuario_empresa e ");
			sbSql.append("							WHERE no_usuario =" + Utilerias.validarCadenaSQL(idUsuario) + ") ");
		}
		sbSql.append("AND oi.fec_alta >= " + Utilerias.validarCadenaSQL(fechaInicial) + "  ");
		//sbSql.append("AND oi.fec_alta < to_date ('" + Utilerias.validarCadenaSQL(fechaFinal) + "', 'dd/mm/yyyy') + 1 ");
		sbSql.append("AND c.id_divisa = '" + Utilerias.validarCadenaSQL(idDivisa) + "' ");
		sbSql.append("ORDER BY oi.fec_alta ");
System.out.println("ggg:..."+sbSql.toString());
		try{
			lista = jdbcTemplate.queryForList(sbSql.toString());
		}catch (Exception e){
			logger.error(e.getLocalizedMessage());
		}
		
		return lista;
	}



	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.dao.RepInversionesDao#consultarReporteVencimiento(int, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	public List<Map<String, Object>> consultarReporteVencimiento(int noEmpresa, String idDivisa, String fechaInicial, String fechaFinal,
			int idUsuario) {
		StringBuffer sbSql = new StringBuffer();
		List <Map<String, Object>> lista = null;

		logger.info("Entra consultarReporteVencimiento");
		
		sbSql.append("SELECT distinct oi.fec_alta, oi.fec_venc ,e.nom_empresa, no_orden, oi.no_cuenta, ");
		sbSql.append("id_papel, ce.desc_estatus, coalesce(c.id_divisa,'') as id_divisa, ");
		sbSql.append("oi.fec_venc, id_tipo_valor,  coalesce(oi.id_chequera,'') as id_chequera, ");
		sbSql.append("oi.importe+interes-isr as total, ");
		if(ConstantesSet.gsDBM.equals("ORACLE"))
			sbSql.append("cast (oi.plazo as INT)as plazo, oi.tasa,  to_char(oi.hora,'99') || ':' || to_char(oi.minuto,'99') as hora, ");
		else
		sbSql.append("cast (oi.plazo as INT)as plazo, oi.tasa, RIGHT( CONVERT(DATETIME, oi.hora, 108),8) AS hora, ");
		sbSql.append("id_estatus_ord, coalesce(cb.desc_banco,'') as desc_banco, oi.importe, ");
		sbSql.append("coalesce(m1.importe,0) as interes, coalesce(m2.importe,0) as isr, ");
		sbSql.append("coalesce(oi.id_chequera_reg,'') as id_chequera_reg,cast (id_banco_reg as INT)as id_banco_reg, p.razon_social ");
		sbSql.append("FROM orden_inversion oi LEFT JOIN cat_banco cb on (oi.id_banco = cb.id_banco), ");
		sbSql.append("persona p, cat_estatus ce, cuenta c,empresa e, movimiento m1, movimiento m2 ");
		sbSql.append("WHERE oi.no_empresa = c.no_empresa ");
		sbSql.append("AND oi.no_empresa = e.no_empresa ");
		sbSql.append("AND oi.no_cuenta = c.no_cuenta ");
		sbSql.append("AND oi.id_estatus_ord = ce.id_estatus ");
		sbSql.append("AND ce.clasificacion = 'ORD' ");
		sbSql.append("AND oi.id_estatus_ord <> 'X' ");
		sbSql.append("AND m1.no_empresa = oi.no_empresa ");
		sbSql.append("AND m1.no_docto = oi.no_orden ");
		sbSql.append("AND m1.id_tipo_operacion = 4103 "); // INTERES
		sbSql.append("AND m2.no_empresa = oi.no_empresa ");
		sbSql.append("AND m2.no_docto = oi.no_orden ");
		sbSql.append("AND m2.id_tipo_operacion = 4104 "); // ISR
		
		sbSql.append("AND oi.no_institucion = p.no_persona ");
		if (noEmpresa != 0){
			sbSql.append("AND oi.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresa) + " ");
		}
		else{
			sbSql.append("AND oi.no_empresa IN (SELECT e.no_empresa  FROM usuario_empresa e ");
			sbSql.append("							WHERE no_usuario =" + Utilerias.validarCadenaSQL(idUsuario) + ") ");
		}
		
		if(ConstantesSet.gsDBM.equals("ORACLE"))
			sbSql.append("AND oi.fec_venc < to_date ('" + Utilerias.validarCadenaSQL(fechaFinal) + "', 'dd/mm/yyyy') + 1 ");
		else
		sbSql.append("AND oi.fec_venc >=  " + Utilerias.validarCadenaSQL(fechaInicial) + " ");
		sbSql.append("AND c.id_divisa = '" + Utilerias.validarCadenaSQL(idDivisa) + "' ");
		sbSql.append("ORDER BY oi.id_estatus_ord, oi.fec_alta ");
		System.out.println(sbSql.toString()+"55**");
		try{
			lista = jdbcTemplate.queryForList(sbSql.toString());
		}catch (Exception e){
			logger.error(e.getLocalizedMessage());
		}
		
 		return lista;
	}

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.dao.RepInversionesDao#eliminarTmpPosicion(int)
	 */
	@Override
	public void eliminarTmpPosicion(int idUsuario) {
		StringBuffer sbSql = new StringBuffer("delete from tmp_posicion where usuario_firma = ");
		
		sbSql.append(Utilerias.validarCadenaSQL(idUsuario));
		
		this.getJdbcTemplate().execute(sbSql.toString());
		
	}

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.dao.RepInversionesDao#insertarTmpPosicion(int, java.lang.String, java.lang.String, java.lang.String, int, java.lang.String)
	 */
	@Override
	public void insertarTmpPosicion(int idUsuario, String fechaInicial, String fechaFinal, int noEmpresa, String idDivisa) {
		StringBuffer sbSql = new StringBuffer();
		
		sbSql.append("INSERT INTO tmp_posicion (no_empresa, no_institucion,usuario_firma, id_divisa, tasa_prom_pond,interes_diario,fec_valor) ");
		sbSql.append("SELECT oi.no_empresa, oi.no_institucion, " + idUsuario + ", c.id_divisa, ");
		sbSql.append("round(Sum(oi.importe * tasa) / Sum(importe), 6), round(round(((Sum(oi.importe * tasa) / Sum(importe))/36000), 6) *sum(importe),2), ");
		sbSql.append("max(to_date ('" + Utilerias.validarCadenaSQL(fechaInicial) + "', 'dd/mm/yyyy')) ");
		sbSql.append("FROM orden_inversion oi, cuenta c ");
		sbSql.append("WHERE oi.id_estatus_ord in ('A','C') ");
		sbSql.append("AND oi.no_empresa = c.no_empresa ");
		sbSql.append("AND oi.no_cuenta = c.no_cuenta ");
		sbSql.append("AND oi.fec_venc >= to_date ('" + Utilerias.validarCadenaSQL(fechaInicial) + "', 'dd/mm/yyyy') ");
		sbSql.append("AND oi.fec_venc < (SELECT fec_hoy + 1 FROM fechas) ");
		sbSql.append("AND oi.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresa));
		sbSql.append(" AND c.id_divisa = '" + Utilerias.validarCadenaSQL(idDivisa) + "' ");
		sbSql.append("GROUP BY oi.no_empresa, oi.no_institucion, " + idUsuario + ", c.id_divisa ");
		sbSql.append("ORDER BY oi.no_empresa,c.id_divisa");

		this.jdbcTemplate.execute(sbSql.toString());
		
	}

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.dao.RepInversionesDao#consultarReportePosicionInv(int, int)
	 */
	@Override
	public List<Map<String, Object>> consultarReportePosicionInv(int noEmpresa, int idUsuario) {
		StringBuffer sbSql = new StringBuffer();
		List <Map<String, Object>> lista = null;

		logger.debug("Entra consultarReporteInvEstablecidas");
		
		sbSql.append("SELECT distinct oi.fec_venc, oi.fec_alta, tp.fec_valor, oi.no_orden, ");
		sbSql.append("id_papel, oi.id_estatus_ord, tp.tasa_prom_pond, ");
		sbSql.append("coalesce(tp.interes_diario,0) as interes_diario, ");
		sbSql.append("c.id_divisa, to_char(oi.hora,'09') || ':' || to_char(oi.minuto,'09') as hora, ");
		sbSql.append("id_tipo_valor, oi.id_chequera, p.razon_social, oi.importe * ((tasa/360)*28)/100 as ponder, ");
		sbSql.append("oi.importe, interes, isr, tasa, plazo, cb.desc_banco, oi.importe + interes - isr as total, ");
		sbSql.append("to_number(to_char(oi.fec_alta - tp.fec_valor,'dd')) * (interes / plazo) as intereses, ");
		sbSql.append("interes - to_number(to_char(oi.fec_alta - tp.fec_valor,'dd')) * (interes / plazo) as int_por_dev, ");
		sbSql.append("oi.tasa_curva28 as tasa_eq, ");
		sbSql.append("to_number(to_char(oi.fec_alta - tp.fec_valor, 'dd')) * (isr / plazo) as impuestos ");
		sbSql.append("FROM  orden_inversion oi LEFT JOIN cat_banco cb on (oi.id_banco = cb.id_banco ) ");
		sbSql.append("		LEFT JOIN persona p on (to_char(oi.no_institucion) = p.equivale_persona ),  ");
		sbSql.append("		tmp_posicion tp, cuenta c ");
		sbSql.append("WHERE oi.no_empresa = tp.no_empresa ");
		sbSql.append("AND tp.no_empresa = c.no_empresa ");
		sbSql.append("AND c.id_divisa = tp.id_divisa ");
		sbSql.append("AND oi.no_empresa = c.no_empresa ");
		sbSql.append("AND oi.no_empresa = c.no_persona ");
		sbSql.append("AND oi.no_cuenta = c.no_cuenta ");
		sbSql.append("AND id_estatus_ord in ('A','C') ");
		sbSql.append("AND oi.fec_venc >= tp.fec_valor ");
		sbSql.append("AND oi.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresa) + " ");
		sbSql.append("AND tp.usuario_firma = " + Utilerias.validarCadenaSQL(idUsuario) + " ");
		sbSql.append("ORDER BY oi.id_estatus_ord");
		
		try{
			lista = jdbcTemplate.queryForList(sbSql.toString());
		}catch (Exception e){
			logger.error(e.getLocalizedMessage());
		}
	
		return lista;
	}

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.dao.RepInversionesDao#consultarReporteSaldosInv(int, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	public List<Map<String, Object>> consultarReporteSaldosInv(int noEmpresa,
			String idDivisa, String fechaInicial, String fechaFinal, int idUsuario) {
		StringBuffer sbSql = new StringBuffer();
		List <Map<String, Object>> lista = null;

		logger.debug("Entra consultarReporteSaldosInv");
		
		sbSql.append("SELECT oi.no_empresa, e.nom_empresa, sum(oi.importe) importe, sum(oi.interes) interes, sum(oi.isr) isr, ");
		sbSql.append("sum(oi.importe+oi.interes-oi.isr) As total ");
		sbSql.append("FROM orden_inversion oi left join cat_banco cb on (oi.id_banco = cb.id_banco), ");
		sbSql.append("	   persona p, cat_estatus ce, cuenta c, Empresa e ");
		sbSql.append("WHERE to_char(oi.no_institucion) = p.equivale_persona ");
		sbSql.append("AND oi.no_empresa = c.no_empresa ");
		sbSql.append("AND oi.no_empresa = c.no_persona ");
		sbSql.append("AND oi.NO_EMPRESA = e.NO_EMPRESA ");
		sbSql.append("AND oi.no_cuenta = c.no_cuenta ");
		sbSql.append("AND oi.id_estatus_ord = ce.id_estatus ");
		sbSql.append("AND ce.clasificacion = 'ORD' ");
		sbSql.append("AND oi.id_estatus_ord <> 'X' ");
		sbSql.append("AND oi.TIPO_INVERSION = 'N' ");
		if (noEmpresa != 0){
			sbSql.append("AND oi.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresa) + " ");
		}
		else{
			sbSql.append("AND oi.no_empresa IN (SELECT eInt.no_empresa  FROM usuario_empresa eInt ");
			sbSql.append("							WHERE eInt.no_usuario =" + Utilerias.validarCadenaSQL(idUsuario) + ") ");
		}
		sbSql.append("AND oi.fec_alta >= to_date ('" + Utilerias.validarCadenaSQL(fechaInicial) + "', 'dd/mm/yyyy') ");
		sbSql.append("AND oi.fec_alta < to_date ('" + Utilerias.validarCadenaSQL(fechaFinal) + "', 'dd/mm/yyyy') + 1 ");
		sbSql.append("AND c.id_divisa = '" + Utilerias.validarCadenaSQL(idDivisa) + "' ");
		sbSql.append("GROUP BY oi.no_empresa, e.nom_empresa ");
		
		try{
			lista = jdbcTemplate.queryForList(sbSql.toString());
		}catch (Exception e){
			logger.error(e.getLocalizedMessage());
		}
	
		return lista;
	}

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.dao.RepInversionesDao#consultarExcelInvEstablecidas(int, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	public List<Map<String, String>> consultarExcelInvEstablecidas(int noEmpresa, String idDivisa, String fechaInicial,
			String fechaFinal, int idUsuario) {
		StringBuffer sbSql = new StringBuffer();
		List <Map<String, String>> lista = null;

		logger.debug("Entra consultarExcelInvEstablecidas");
		
		sbSql.append("SELECT distinct oi.fec_alta, oi.fec_venc, e.nom_empresa, oi.no_orden, oi.no_cuenta, ");
		sbSql.append("id_papel, ce.desc_estatus, c.id_divisa, id_tipo_valor, p.razon_social, ");
		sbSql.append("oi.importe + coalesce (m1.importe, oi.interes) - coalesce (m2.importe, oi.isr) as total, oi.plazo, oi.tasa, ");
		sbSql.append("case c.valor_salida when 3 then 'Transferencia' when 5 then 'Cargo en cuenta' end as forma_pago, ");
		sbSql.append("id_estatus_ord , oi.importe, coalesce (m1.importe, oi.interes) AS interes, coalesce (m2.importe, oi.isr) AS isr ");
		sbSql.append("FROM orden_inversion oi LEFT JOIN movimiento m1 ON m1.no_empresa = oi.no_empresa AND m1.no_docto = oi.no_orden AND m1.id_tipo_operacion = 4103 ");
		sbSql.append("LEFT JOIN movimiento m2 ON m2.no_empresa = oi.no_empresa AND m2.no_docto = oi.no_orden AND m2.id_tipo_operacion = 4104, ");
		sbSql.append("persona p, cat_estatus ce, cuenta c, empresa e, ctas_contrato cc ");
		sbSql.append("WHERE oi.no_institucion = p.no_persona ");
		sbSql.append(" AND oi.no_empresa = e.no_empresa ");
		sbSql.append("AND oi.no_empresa = c.no_empresa ");
		sbSql.append("AND oi.no_empresa = cc.no_empresa ");
		sbSql.append("AND oi.no_empresa = c.no_persona ");
		sbSql.append("AND oi.no_cuenta = c.no_cuenta ");
		sbSql.append("AND oi.id_estatus_ord = ce.id_estatus ");
		sbSql.append("AND ce.clasificacion = 'ORD' ");
		sbSql.append("AND oi.id_estatus_ord <> 'X' ");
		sbSql.append("AND oi.no_cuenta = cc.no_cuenta ");
		sbSql.append("AND oi.id_banco = cc.id_banco ");
		sbSql.append("AND oi.id_chequera = cc.id_chequera ");
		if (noEmpresa != 0){
			sbSql.append("AND oi.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresa) + " ");
		}
		else{
			sbSql.append("AND oi.no_empresa IN (SELECT e.no_empresa  FROM usuario_empresa e ");
			sbSql.append("							WHERE no_usuario =" + Utilerias.validarCadenaSQL(idUsuario) + ") ");
		}
		sbSql.append("AND oi.fec_alta >= " + Utilerias.validarCadenaSQL(fechaInicial) + " ");
		//sbSql.append("AND oi.fec_alta < to_date ('" + Utilerias.validarCadenaSQL(fechaFinal) + "', 'dd/mm/yyyy') + 1 ");
		sbSql.append("AND c.id_divisa = '" + Utilerias.validarCadenaSQL(idDivisa) + "' ");
		sbSql.append("ORDER BY 1 ");
System.out.println(".::"+sbSql.toString());
		try{
			lista = this.getJdbcTemplate().query(sbSql.toString(), new RowMapper<Map<String, String>>()
					{
				public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException
				{
					Map<String, String> campos = new HashMap<String, String>();
					campos.put("fec_alta", rs.getString("fec_alta"));
					campos.put("fec_venc", rs.getString("fec_venc"));
					campos.put("nom_empresa", rs.getString("nom_empresa"));
					campos.put("no_orden", rs.getString("no_orden"));
					campos.put("no_cuenta", rs.getString("no_cuenta"));
					campos.put("id_papel", rs.getString("id_papel"));
					campos.put("desc_estatus", rs.getString("desc_estatus"));
					campos.put("desc_estatus", rs.getString("desc_estatus"));
					campos.put("id_divisa", rs.getString("id_divisa"));
					campos.put("id_tipo_valor", rs.getString("id_tipo_valor"));
					campos.put("razon_social", rs.getString("razon_social"));
					campos.put("total", rs.getString("total"));
					campos.put("plazo", rs.getString("plazo"));
					campos.put("tasa", rs.getString("tasa"));
					campos.put("forma_pago", rs.getString("forma_pago"));
					campos.put("id_estatus_ord", rs.getString("id_estatus_ord"));
					campos.put("importe", rs.getString("importe"));
					campos.put("interes", rs.getString("interes"));
					campos.put("isr", rs.getString("isr"));
					
					return campos;
				}				
				
			});
		}catch (Exception e){
			logger.error(e.getLocalizedMessage());
		}
		
		return lista;
	}



	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.dao.RepInversionesDao#consultarExcelVencimiento(int, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	public List<Map<String, String>> consultarExcelVencimiento(int noEmpresa,
			String idDivisa, String fechaInicial, String fechaFinal,
			int idUsuario) {
		StringBuffer sbSql = new StringBuffer();
		List <Map<String, String>> lista = null;

		logger.debug("Entra consultarExcelVencimiento");
		
		sbSql.append("SELECT distinct oi.fec_alta, oi.fec_venc ,e.nom_empresa, no_orden, oi.no_cuenta, ");
		sbSql.append("id_papel, ce.desc_estatus, coalesce(c.id_divisa,'') as id_divisa, ");
		sbSql.append("oi.fec_venc, id_tipo_valor, coalesce(oi.id_chequera,'') as id_chequera, ");
		sbSql.append("oi.importe+interes-isr as total, ");
		sbSql.append("oi.plazo,oi.tasa,(oi.hora)as hora, ");
		sbSql.append("id_estatus_ord, coalesce(cb.desc_banco,'') as desc_banco, oi.importe, ");
		sbSql.append("coalesce(m1.importe,oi.interes) as interes, coalesce(m2.importe,oi.isr) as isr, ");
		sbSql.append("coalesce(oi.id_chequera_reg,'') as id_chequera_reg,id_banco_reg ");
		sbSql.append("FROM orden_inversion oi LEFT JOIN cat_banco cb on (oi.id_banco = cb.id_banco), ");
		sbSql.append("persona p, cat_estatus ce, cuenta c,empresa e, movimiento m1, movimiento m2 ");
		sbSql.append("WHERE oi.no_empresa = c.no_empresa ");
		sbSql.append("AND oi.no_empresa = e.no_empresa ");
		sbSql.append("AND oi.no_cuenta = c.no_cuenta ");
		sbSql.append("AND oi.id_estatus_ord = ce.id_estatus ");
		sbSql.append("AND ce.clasificacion = 'ORD' ");
		sbSql.append("AND oi.id_estatus_ord <> 'X' ");
		sbSql.append("AND m1.no_empresa = oi.no_empresa ");
		sbSql.append("AND m1.no_docto = oi.no_orden ");
		sbSql.append("AND m1.id_tipo_operacion = 4103 "); // INTERES
		sbSql.append("AND m2.no_empresa = oi.no_empresa ");
		sbSql.append("AND m2.no_docto = oi.no_orden ");
		sbSql.append("AND m2.id_tipo_operacion = 4104 ");
		sbSql.append("AND oi.no_institucion = p.no_persona ");
		if (noEmpresa != 0){
			sbSql.append("AND oi.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresa) + " ");
		}
		else{
			sbSql.append("AND oi.no_empresa IN (SELECT e.no_empresa  FROM usuario_empresa e ");
			sbSql.append("							WHERE no_usuario =" + Utilerias.validarCadenaSQL(idUsuario) + ") ");
		}
		
		sbSql.append("AND oi.fec_venc >= " + Utilerias.validarCadenaSQL(fechaInicial) + " ");
		//sbSql.append("AND oi.fec_venc < to_date ('" + Utilerias.validarCadenaSQL(fechaFinal) + "', 'dd/mm/yyyy') + 1 ");
		sbSql.append("AND c.id_divisa = '" + Utilerias.validarCadenaSQL(idDivisa) + "' ");
		sbSql.append("ORDER BY oi.id_estatus_ord, 1 ");
		System.out.println("qry:..\n"+sbSql.toString());
		try{
			lista = this.getJdbcTemplate().query(sbSql.toString(), new RowMapper<Map<String, String>>()
					{
				public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException
				{
					Map<String, String> campos = new HashMap<String, String>();
					campos.put("fec_alta", rs.getString("fec_alta"));
					campos.put("fec_venc", rs.getString("fec_venc"));
					campos.put("nom_empresa", rs.getString("nom_empresa"));
					campos.put("no_orden", rs.getString("no_orden"));
					campos.put("no_cuenta", rs.getString("no_cuenta"));
					campos.put("id_papel", rs.getString("id_papel"));
					campos.put("desc_estatus", rs.getString("desc_estatus"));
					campos.put("id_divisa", rs.getString("id_divisa"));
					campos.put("id_tipo_valor", rs.getString("id_tipo_valor"));
					campos.put("id_chequera", rs.getString("id_chequera"));
					campos.put("total", rs.getString("total"));
					campos.put("plazo", rs.getString("plazo"));
					campos.put("tasa", rs.getString("tasa"));
					campos.put("hora", rs.getString("hora"));
					campos.put("desc_banco", rs.getString("desc_banco"));
					campos.put("id_estatus_ord", rs.getString("id_estatus_ord"));
					campos.put("importe", rs.getString("importe"));
					campos.put("interes", rs.getString("interes"));
					campos.put("isr", rs.getString("isr"));
					campos.put("id_chequera_reg", rs.getString("id_chequera_reg"));
					campos.put("id_banco_reg", rs.getString("id_banco_reg"));
					
					return campos;
				}				
				
			});
		}catch (Exception e){
			logger.error(e.getLocalizedMessage());
		}
		
 		return lista;
	}



	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.dao.RepInversionesDao#consultarExcelPosicionInv(int, int)
	 */
	@Override
	public List<Map<String, String>> consultarExcelPosicionInv(int noEmpresa,
			int idUsuario) {
		StringBuffer sbSql = new StringBuffer();
		List <Map<String, String>> lista = null;

		logger.debug("Entra consultarReporteInvEstablecidas");
		
		sbSql.append("SELECT distinct to_char(oi.fec_venc, 'dd/mm/yyyy') fec_venc, to_char(oi.fec_alta, 'dd/mm/yyyy') fec_alta, ");
		sbSql.append("to_char(tp.fec_valor, 'dd/mm/yyyy') fec_valor, oi.no_orden, ");
		sbSql.append("id_papel, oi.id_estatus_ord, tp.tasa_prom_pond, ");
		sbSql.append("coalesce(tp.interes_diario,0) as interes_diario, ");
		sbSql.append("c.id_divisa, to_char(oi.hora,'09') || ':' || to_char(oi.minuto,'09') as hora, ");
		sbSql.append("id_tipo_valor, oi.id_chequera, p.razon_social, oi.importe * ((tasa/360)*28)/100 as ponder, ");
		sbSql.append("oi.importe, interes, isr, tasa, plazo, cb.desc_banco, oi.importe + interes - isr as total, ");
		sbSql.append("to_number(to_char(oi.fec_alta - tp.fec_valor,'dd')) * (interes / plazo) as intereses, ");
		sbSql.append("interes - to_number(to_char(oi.fec_alta - tp.fec_valor,'dd')) * (interes / plazo) as int_por_dev, ");
		sbSql.append("oi.tasa_curva28 as tasa_eq, ");
		sbSql.append("to_number(to_char(oi.fec_alta - tp.fec_valor, 'dd')) * (isr / plazo) as impuestos ");
		sbSql.append("FROM  orden_inversion oi LEFT JOIN cat_banco cb on (oi.id_banco = cb.id_banco ) ");
		sbSql.append("		LEFT JOIN persona p on (to_char(oi.no_institucion) = p.equivale_persona ),  ");
		sbSql.append("		tmp_posicion tp, cuenta c ");
		sbSql.append("WHERE oi.no_empresa = tp.no_empresa ");
		sbSql.append("AND tp.no_empresa = c.no_empresa ");
		sbSql.append("AND c.id_divisa = tp.id_divisa ");
		sbSql.append("AND oi.no_empresa = c.no_empresa ");
		sbSql.append("AND oi.no_empresa = c.no_persona ");
		sbSql.append("AND oi.no_cuenta = c.no_cuenta ");
		sbSql.append("AND id_estatus_ord in ('A','C') ");
		sbSql.append("AND oi.fec_venc >= tp.fec_valor ");
		sbSql.append("AND oi.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresa) + " ");
		sbSql.append("AND tp.usuario_firma = " + Utilerias.validarCadenaSQL(idUsuario) + " ");
		sbSql.append("ORDER BY oi.id_estatus_ord");
		
		try{
			lista = this.getJdbcTemplate().query(sbSql.toString(), new RowMapper<Map<String, String>>()
					{
				public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException
				{
					Map<String, String> campos = new HashMap<String, String>();
					campos.put("fec_alta", rs.getString("fec_alta"));
					campos.put("fec_venc", rs.getString("fec_venc"));
					campos.put("fec_valor", rs.getString("fec_valor"));
					campos.put("no_orden", rs.getString("no_orden"));
					campos.put("id_papel", rs.getString("id_papel"));
					campos.put("id_estatus_ord", rs.getString("id_estatus_ord"));
					campos.put("tasa_prom_pond", rs.getString("tasa_prom_pond"));
					campos.put("interes_diario", rs.getString("interes_diario"));
					campos.put("id_divisa", rs.getString("id_divisa"));
					campos.put("hora", rs.getString("hora"));
					campos.put("id_tipo_valor", rs.getString("id_tipo_valor"));
					campos.put("id_chequera", rs.getString("id_chequera"));
					campos.put("razon_social", rs.getString("razon_social"));
					campos.put("ponder", rs.getString("ponder"));
					campos.put("importe", rs.getString("importe"));
					campos.put("interes", rs.getString("interes"));
					campos.put("isr", rs.getString("isr"));
					campos.put("total", rs.getString("total"));
					campos.put("tasa", rs.getString("tasa"));
					campos.put("plazo", rs.getString("plazo"));
					campos.put("desc_banco", rs.getString("desc_banco"));
					campos.put("int_por_dev", rs.getString("int_por_dev"));
					campos.put("tasa_eq", rs.getString("tasa_eq"));
					campos.put("impuestos", rs.getString("impuestos"));
					
					return campos;
				}				
				
			});
		}catch (Exception e){
			logger.error(e.getLocalizedMessage());
		}
	
		return lista;
	}

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.dao.RepInversionesDao#consultarExcelSaldosInv(int, java.lang.String, java.lang.String, java.lang.String, int)
	 */
	@Override
	public List<Map<String, String>> consultarExcelSaldosInv(int noEmpresa,
			String idDivisa, String fechaInicial, String fechaFinal,
			int idUsuario) {
		StringBuffer sbSql = new StringBuffer();
		List <Map<String, String>> lista = null;

		logger.debug("Entra consultarExcelSaldosInv");
		
		sbSql.append("SELECT oi.no_empresa, e.nom_empresa, sum(oi.importe) importe, sum(oi.interes) interes, sum(oi.isr) isr, ");
		sbSql.append("sum(oi.importe+oi.interes-oi.isr) As total ");
		sbSql.append("FROM orden_inversion oi left join cat_banco cb on (oi.id_banco = cb.id_banco), ");
		sbSql.append("	   persona p, cat_estatus ce, cuenta c, Empresa e ");
		sbSql.append("WHERE to_char(oi.no_institucion) = p.equivale_persona ");
		sbSql.append("AND oi.no_empresa = c.no_empresa ");
		sbSql.append("AND oi.no_empresa = c.no_persona ");
		sbSql.append("AND oi.NO_EMPRESA = e.NO_EMPRESA ");
		sbSql.append("AND oi.no_cuenta = c.no_cuenta ");
		sbSql.append("AND oi.id_estatus_ord = ce.id_estatus ");
		sbSql.append("AND ce.clasificacion = 'ORD' ");
		sbSql.append("AND oi.id_estatus_ord <> 'X' ");
		sbSql.append("AND oi.TIPO_INVERSION = 'N' ");
		if (noEmpresa != 0){
			sbSql.append("AND oi.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresa) + " ");
		}
		else{
			sbSql.append("AND oi.no_empresa IN (SELECT eInt.no_empresa  FROM usuario_empresa eInt ");
			sbSql.append("							WHERE eInt.no_usuario =" + Utilerias.validarCadenaSQL(idUsuario) + ") ");
		}
		sbSql.append("AND oi.fec_alta >= to_date ('" + Utilerias.validarCadenaSQL(fechaInicial) + "', 'dd/mm/yyyy') ");
		sbSql.append("AND oi.fec_alta < to_date ('" + Utilerias.validarCadenaSQL(fechaFinal) + "', 'dd/mm/yyyy') + 1 ");
		sbSql.append("AND c.id_divisa = '" + Utilerias.validarCadenaSQL(idDivisa) + "' ");
		sbSql.append("GROUP BY oi.no_empresa, e.nom_empresa ");
		
		try{
			lista = this.getJdbcTemplate().query(sbSql.toString(), new RowMapper<Map<String, String>>()
					{
				public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException
				{
					Map<String, String> campos = new HashMap<String, String>();
					campos.put("no_empresa", rs.getString("no_empresa"));
					campos.put("nom_empresa", rs.getString("nom_empresa"));
					campos.put("total", rs.getString("total"));
					campos.put("importe", rs.getString("importe"));
					campos.put("interes", rs.getString("interes"));
					campos.put("isr", rs.getString("isr"));
					
					return campos;
				}				
				
			});
		}catch (Exception e){
			logger.error(e.getLocalizedMessage());
		}
	
		return lista;
	}

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.dao.RepInversionesDao#consultarReporteLiqInv(int, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> consultarReporteLiqInv(int noEmpresa, String tipoInversion) {
		StringBuffer sbSql = new StringBuffer();
		List <Map<String, Object>> lista = null;

		logger.debug("Entra consultarReporteSaldosInv");
		
	    sbSql.append(" SELECT a.no_orden, a.id_tipo_valor,  ");
	    if(ConstantesSet.gsDBM.equals("ORACLE")) {
	    	sbSql.append(" to_char(a.no_cuenta,'009') As no_cuenta,  ");
	    	sbSql.append(" to_char(a.no_institucion,'009') as no_institucion, ");
	    	sbSql.append(" a.importe, to_char(a.plazo, '009') plazo,  ");
	    }else {
	    	sbSql.append(" a.no_cuenta,  ");
	    	sbSql.append(" a.no_institucion, ");
	    	sbSql.append(" a.importe, cast (a.plazo as INT)as plazo,  ");
	    }
	    
	    sbSql.append(" a.tasa, a.interes, a.isr, a.id_papel,  ");
	    sbSql.append(" a.fec_alta, a.fec_venc, b.nombre_corto,  ");
	    sbSql.append(" d.desc_tipo_valor, m.no_folio_det, m.no_empresa,  ");
	    sbSql.append(" e.nom_empresa  ");
	    sbSql.append(" FROM persona B, cat_tipo_valor D,Empresa E,  ");
	    sbSql.append(" orden_inversion A LEFT JOIN movimiento M ON  ");
	    if(ConstantesSet.gsDBM.equals("ORACLE")) {
	    	sbSql.append("             (TRIM(A.no_orden) = TRIM(M.no_docto) 	 ");
	    } else{
	    	sbSql.append("             (RTRIM(A.no_orden) = RTRIM(M.no_docto) 	 ");
	    }
	    sbSql.append("             and a.no_empresa = m.no_empresa  ");
	    sbSql.append("             and m.id_tipo_operacion = 4000  ");
	    sbSql.append("             and m.id_estatus_mov = 'O')  ");
	    sbSql.append(" WHERE A.id_estatus_ord = 'C'  ");
	    sbSql.append(" AND a.b_autoriza = 'S'  ");
	    if(ConstantesSet.gsDBM.equals("ORACLE"))
	    	sbSql.append(" AND A.no_institucion = B.EQUIVALE_PERSONA  ");
	    else
	    	sbSql.append(" AND cast(A.no_institucion as varchar(15)) = B.no_persona  ");
	    
	    sbSql.append(" AND A.NO_EMPRESA = E.NO_EMPRESA  ");
	    sbSql.append(" AND D.id_tipo_valor = A.id_tipo_valor  ");
	    sbSql.append(" AND A.no_empresa = "+ noEmpresa);
	    sbSql.append(" AND A.tipo_orden = 'E'  ");
	    sbSql.append(" ORDER BY B.nombre_corto  ");
	    
	    
	    System.out.println("qry Reporte inv ..::"+sbSql.toString());
		
		
		try{
			lista = jdbcTemplate.queryForList(sbSql.toString());
		}catch (Exception e){
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}
		
		return lista;
	}

	//set del data source
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:MantenimientoDePapelDaoImpl, M:setDataSource");
		}
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
