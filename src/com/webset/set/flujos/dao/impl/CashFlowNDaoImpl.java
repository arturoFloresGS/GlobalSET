package com.webset.set.flujos.dao.impl;

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

import com.webset.set.flujos.dao.CashFlowNDao;
import com.webset.set.flujos.dto.PosicionBancariaDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;

@SuppressWarnings("unchecked")
public class CashFlowNDaoImpl implements CashFlowNDao {
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	Funciones funciones = new Funciones();
	ConsultasGenerales consultasGenerales;
	
	public List<PosicionBancariaDto> cashFlowDatos(List<Map<String, String>> parametros, String tipoMovto) {
		StringBuffer sql = new StringBuffer();
		List<PosicionBancariaDto> listDatos = new ArrayList<PosicionBancariaDto>();
		
		try {
			sql.append(" SELECT ctc.id_tipo_concepto, ctc.descripcion, ccf.CVE_CONCEPTO, ccf.DESC_CORTA, coalesce(sum(v.importe), 0) as importe, month(v.fec_valor) as mes, year(v.fec_valor) as anio \n");
			sql.append(" FROM vista_movimiento v, cat_tipo_concepto ctc, cat_rubro_concepto crc, cat_rubro cr , CAT_CONCEPTO_FLUJO ccf\n");
			sql.append(" WHERE ctc.ID_TIPO_CONCEPTO = crc.ID_TIPO_CONCEPTO \n");
			sql.append(" 	And ccf.CVE_CONCEPTO = cr.id_grupo \n");
			sql.append(" 	And v.id_grupo = crc.RUBRO \n");
			sql.append(" 	And v.id_rubro = crc.SUBRUBRO \n");
			sql.append(" 	And v.id_rubro = cr.id_rubro \n");
			sql.append(" 	And v.id_tipo_movto = ctc.INGRESOEGRESO \n");
			sql.append(" 	And ctc.INGRESOEGRESO = '"+ tipoMovto +"' \n");
			sql.append(" 	And v.fec_valor between DATEADD(s, 0, DATEADD(mm, DATEDIFF(m, 0, '"+ parametros.get(0).get("fecIni") +"'), 0)) and ");
			sql.append(" 		DATEADD(s, -1, DATEADD(mm, DATEDIFF(m, 0, '"+ parametros.get(0).get("fecFin") +"') + 1, 0)) \n");
			sql.append(" 	And v.id_divisa = '"+ parametros.get(0).get("idDivisa") +"' \n");
//			sql.append(" 	And v.id_tipo_operacion not in (3000, 3001, 3201) \n");
			sql.append(" 	And (v.id_tipo_operacion in (3101,3115,3154, 1016,1017,1018,1023,1026,4104) or \n");
			sql.append(" 		((v.grupo_pago != 0 and not v.grupo_pago is null) and v.id_tipo_operacion in (3201)) or \n");
			sql.append(" 		((v.grupo_pago = 0 or v.grupo_pago is null) and v.id_tipo_operacion in (3200))) \n");
			
			if(Integer.parseInt(parametros.get(0).get("noEmpresa")) != 0)
				sql.append(" 	And v.no_empresa = "+ parametros.get(0).get("noEmpresa") +" \n");
			
			sql.append(" 	group by ctc.ID_TIPO_CONCEPTO, ctc.DESCRIPCION,ccf.CVE_CONCEPTO, ccf.DESC_CORTA, month(v.fec_valor), year(v.fec_valor) \n");
			sql.append(" order by ctc.ID_TIPO_CONCEPTO,ccf.CVE_CONCEPTO, month(v.fec_valor), year(v.fec_valor) \n");
			
			System.out.println("Query cashflowdatos: " + sql.toString());
			
			listDatos = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public PosicionBancariaDto mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDto cons = new PosicionBancariaDto();
					cons.setIdTipoConcepto(rs.getInt("id_tipo_concepto"));
					cons.setDescripcion(rs.getString("descripcion"));
					cons.setIdRubro(rs.getString("CVE_CONCEPTO"));
					cons.setDescRubro(rs.getString("DESC_CORTA"));
					cons.setImporte(rs.getDouble("importe"));
					cons.setMes(rs.getInt("mes"));
					cons.setAnio(rs.getInt("anio"));
					return cons;
				}});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:CashFlowNDaoImpl, M:cashFlowDatos");
		}
		return listDatos;
	}
	
	public List<PosicionBancariaDto> totalIngresosEgresos(List<Map<String, String>> parametros, String tipoMovto, int tipoRep) {
		StringBuffer sql = new StringBuffer();
		List<PosicionBancariaDto> listDatos = new ArrayList<PosicionBancariaDto>();
		
		try {
			if(tipoRep == 0)
				sql.append(" SELECT coalesce(sum(v.importe), 0) as importe, 'col' + right( '00' + convert(varchar(15), day(fec_valor)), 2 ) + '/' + right( '00' + convert(varchar(15), month(fec_valor)), 2 ) + '/'  + convert(varchar(15), year(v.fec_valor)) as mesAnio  \n");
			if(tipoRep == 2)
				sql.append(" SELECT coalesce(sum(v.importe), 0) as importe, 'col' + convert(varchar(15), month(v.fec_valor)) + convert(varchar(15), year(v.fec_valor)) as mesAnio \n");
			
			sql.append(" FROM vista_movimiento v, cat_tipo_concepto ctc, cat_rubro_concepto crc, cat_rubro cr \n");
			sql.append(" WHERE ctc.ID_TIPO_CONCEPTO = crc.ID_TIPO_CONCEPTO \n");
			sql.append(" 	And v.id_grupo = crc.RUBRO \n");
			sql.append(" 	And v.id_rubro = crc.SUBRUBRO \n");
			sql.append(" 	And v.id_tipo_movto = ctc.INGRESOEGRESO \n");
			sql.append(" 	And ctc.INGRESOEGRESO = '"+ tipoMovto +"' \n");
			sql.append(" 	And v.id_rubro = cr.id_rubro \n");
			sql.append(" 	And v.fec_valor between DATEADD(s, 0, DATEADD(mm, DATEDIFF(m, 0, '"+ parametros.get(0).get("fecIni") +"'), 0)) and ");
			sql.append(" 		DATEADD(s, -1, DATEADD(mm, DATEDIFF(m, 0, '"+ parametros.get(0).get("fecFin") +"') + 1, 0)) \n");
			sql.append(" 	And v.id_divisa = '"+ parametros.get(0).get("idDivisa") +"' \n");
			sql.append(" 	And (v.id_tipo_operacion in (3101,3115,3154, 1016,1017,1018,1023,1026,4104) or \n");
			sql.append(" 		((v.grupo_pago != 0 and not v.grupo_pago is null) and v.id_tipo_operacion in (3201)) or \n");
			sql.append(" 		((v.grupo_pago = 0 or v.grupo_pago is null) and v.id_tipo_operacion in (3200))) \n");
			
			if(Integer.parseInt(parametros.get(0).get("noEmpresa")) != 0)
				sql.append(" 	And v.no_empresa = "+ parametros.get(0).get("noEmpresa") +" \n");
			
			if(tipoRep == 0)
				sql.append(" 	group by day(v.fec_valor), month(v.fec_valor), year(v.fec_valor) \n");
			if(tipoRep == 2)
				sql.append(" 	group by month(v.fec_valor), year(v.fec_valor) \n");
			
			System.out.println("Query de total ingresos y egresos mensual: " + sql.toString());
			
			listDatos = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public PosicionBancariaDto mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDto cons = new PosicionBancariaDto();
					cons.setImporte(rs.getDouble("importe"));
					cons.setNomCol(rs.getString("mesAnio"));
					return cons;
				}});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:CashFlowNDaoImpl, M:totalIngresosEgresos");
		}
		return listDatos;
	}
	
	public String configuraSET(int indice) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
	}
	
	public List<PosicionBancariaDto> consultarDetalleFlujo(List<Map<String, String>> parametros, String idRubro) {
		StringBuffer sql = new StringBuffer();
		List<PosicionBancariaDto> listDatos = new ArrayList<PosicionBancariaDto>();
		
		try {
			System.out.println("fecha  "+parametros.get(0).get("fecIni"));
			sql.append(" SELECT e.nom_empresa, v.Concepto, v.importe, v.fec_valor, c.desc_rubro, v.no_folio_det, v.id_tipo_movto \n");
			sql.append(" FROM vista_movimiento v, empresa e, cat_rubro c, CAT_CONCEPTO_FLUJO ccf  \n");
			sql.append(" WHERE v.no_empresa = e.no_empresa \n");
			sql.append(" 	And v.id_rubro = c.id_rubro \n");
			
			if(Integer.parseInt(parametros.get(0).get("tipoReporte").toString()) == 0) { //Diario
				sql.append(" 	And v.fec_valor = '"+ retornaFecha(parametros.get(0).get("dataIndex")) +"' \n");
			}else if(Integer.parseInt(parametros.get(0).get("tipoReporte").toString()) == 1) { //Semanal
				sql.append(" 	And datepart(WEEK,v.fec_valor) =  '"+ retornaFechaSemana(parametros.get(0).get("dataIndex")) +"'  \n");
				sql.append(" 	And YEAR(v.fec_valor) =  '"+ parametros.get(0).get("fecIni").substring(0,4) +"'  \n");
			}else if(Integer.parseInt(parametros.get(0).get("tipoReporte").toString()) == 2) { //Mensual
				sql.append(" 	And MONTH(v.fec_valor) =  '"+ retornaFechaMes(parametros.get(0).get("dataIndex")) +"'  \n");
				sql.append(" 	And YEAR(v.fec_valor) =  '"+ retornaFechaAnio(parametros.get(0).get("dataIndex")) +"'  \n");
			}
			
			sql.append(" 	And v.id_divisa = '"+ parametros.get(0).get("idDivisa") +"' \n");
			sql.append(" 	And c.id_grupo = ccf.CVE_CONCEPTO \n");
			sql.append(" 	And ccf.CVE_CONCEPTO = "+ idRubro +" \n");
			sql.append(" 	And v.id_tipo_operacion not in (3000, 3001, 3201) \n");
			
			if(Integer.parseInt(parametros.get(0).get("noEmpresa")) != 0)
				sql.append(" 	And v.no_empresa = "+ parametros.get(0).get("noEmpresa") +" \n");
			
			sql.append(" order by e.no_empresa, v.fec_valor \n");
			
			System.out.println("Query detalle de flujo mensual: " + sql.toString());
			
			listDatos = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public PosicionBancariaDto mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDto cons = new PosicionBancariaDto();
					cons.setNomEmpresa(rs.getString("nom_empresa"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setImporte(rs.getDouble("importe"));
					cons.setFecValor(rs.getString("fec_valor"));
					cons.setDescRubro(rs.getString("desc_rubro"));
					cons.setIdTipoMovto(rs.getString("id_tipo_movto"));
					cons.setNoFolioDet(rs.getInt("no_folio_det"));
					return cons;
				}});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:CashFlowNDaoImpl, M:cashFlowDatos");
		}
		return listDatos;
	}
	
	public List<PosicionBancariaDto> cashFlowDatosDiarios(List<Map<String, String>> parametros, String tipoMovto) {
		StringBuffer sql = new StringBuffer();
		List<PosicionBancariaDto> listDatos = new ArrayList<PosicionBancariaDto>();
		
		try {
			sql.append(" SELECT ctc.id_tipo_concepto, ctc.descripcion,ccf.CVE_CONCEPTO, ccf.DESC_CORTA, coalesce(sum(v.importe), 0) as importe, v.fec_valor, day(v.fec_valor) as dia \n");
			sql.append(" FROM vista_movimiento v, cat_tipo_concepto ctc, cat_rubro_concepto crc, cat_rubro cr, CAT_CONCEPTO_FLUJO ccf \n");
			sql.append(" WHERE ctc.ID_TIPO_CONCEPTO = crc.ID_TIPO_CONCEPTO \n");
			sql.append(" 	And ccf.CVE_CONCEPTO = cr.id_grupo \n");
			sql.append(" 	And v.id_grupo = crc.RUBRO \n");
			sql.append(" 	And v.id_rubro = crc.SUBRUBRO \n");
			sql.append(" 	And v.id_rubro = cr.id_rubro \n");
			sql.append(" 	And v.id_tipo_movto = ctc.INGRESOEGRESO \n");
			sql.append(" 	And ctc.INGRESOEGRESO = '"+ tipoMovto +"' \n");
			sql.append(" 	And v.fec_valor between '"+ parametros.get(0).get("fecIni") +"' and ");
			sql.append(" 		'"+ parametros.get(0).get("fecFin") +"' \n");
			sql.append(" 	And v.id_divisa = '"+ parametros.get(0).get("idDivisa") +"' \n");
			sql.append(" 	And (v.id_tipo_operacion in (3101,3115,3154, 1016,1017,1018,1023,1026,4104) or \n");
			sql.append(" 		((v.grupo_pago != 0 and not v.grupo_pago is null) and v.id_tipo_operacion in (3201)) or \n");
			sql.append(" 		((v.grupo_pago = 0 or v.grupo_pago is null) and v.id_tipo_operacion in (3200))) \n");
			
			if(Integer.parseInt(parametros.get(0).get("noEmpresa")) != 0)
				sql.append(" 	And v.no_empresa = "+ parametros.get(0).get("noEmpresa") +" \n");
			
			sql.append(" 	group by ctc.ID_TIPO_CONCEPTO, ctc.DESCRIPCION,ccf.CVE_CONCEPTO, ccf.DESC_CORTA, v.fec_valor, day(v.fec_valor) \n");
			sql.append(" order by ctc.ID_TIPO_CONCEPTO,ccf.CVE_CONCEPTO, v.fec_valor \n");
			
			System.out.println("Query cashflowdatos Diario: " + sql.toString());
			
			listDatos = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public PosicionBancariaDto mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDto cons = new PosicionBancariaDto();
					cons.setIdTipoConcepto(rs.getInt("id_tipo_concepto"));
					cons.setDescripcion(rs.getString("descripcion"));
					cons.setIdRubro(rs.getString("CVE_CONCEPTO"));
					cons.setDescRubro(rs.getString("DESC_CORTA"));
					cons.setImporte(rs.getDouble("importe"));
					cons.setFecValor(rs.getString("fec_valor"));
					return cons;
				}});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:CashFlowNDaoImpl, M:cashFlowDatosDiarios");
		}
		return listDatos;
	}
	
	/**
	 * Este metodo es para hacer el enlace con el bean de conexion en el aplicationContext
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource){
		try {
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:CashFlowNDaoImpl, M:setDataSource");
		}
	}
	
	public List<Map<String, Object>> reporteDetalleFlujo(PosicionBancariaDto dtoIn){
		List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
		String sql = "";
		try {
			
			sql+= " 	SELECT e.nom_empresa, v.concepto, v.importe, v.fec_valor, c.desc_rubro \n";
			sql+= " 	FROM vista_movimiento v, empresa e, cat_rubro c \n";
			sql+= " 	WHERE v.no_empresa = e.no_empresa \n";
			sql+= " 	And v.id_rubro = c.id_rubro \n";
			
			if(dtoIn.getTipoReporte() == 0) {
				sql+= "		And v.fec_valor between DATEADD(s, 0, DATEADD(mm, DATEDIFF(m, 0, '"+ dtoIn.getFecIni() +"'), 0)) and\n";
				sql+= " 	DATEADD(s, -1, DATEADD(mm, DATEDIFF(m, 0, '"+ dtoIn.getFecFin() +"') + 1, 0)) \n";
			}
			if(dtoIn.getTipoReporte() == 1) {
				sql+= "		And v.fec_valor between DATEADD(s, 0, DATEADD(mm, DATEDIFF(m, 0, '"+ dtoIn.getFecIni() +"'), 0)) and\n";
				sql+= " 	DATEADD(s, -1, DATEADD(mm, DATEDIFF(m, 0, '"+ dtoIn.getFecFin() +"') + 1, 0)) \n";
				}
			if(dtoIn.getTipoReporte() == 2) {
				sql+= "		And v.fec_valor between DATEADD(s, 0, DATEADD(mm, DATEDIFF(m, 0, '"+ retornaFecha(dtoIn.getDataIndex()) +"'), 0)) and\n";
				sql+= " 	DATEADD(s, -1, DATEADD(mm, DATEDIFF(m, 0, '"+ retornaFecha(dtoIn.getDataIndex()) +"') + 1, 0)) \n";
				}
			sql+= " 	And v.id_divisa = '"+ dtoIn.getIdDivisa()+"' \n";
			sql+= " 	And v.id_rubro = "+ dtoIn.getIdRubro() +" \n";
			sql+= " 	And v.id_tipo_operacion not in (3000, 3001, 3201) \n";
			
			if(dtoIn.getNoEmpresa() != 0)
				sql+= " 	And v.no_empresa = "+ dtoIn.getNoEmpresa() +" \n";
			
			sql+= "		order by e.no_empresa, v.fec_valor";
			
			System.out.println("Query detalle de flujo mensual: " + sql.toString());
			
			
			listResult = jdbcTemplate.query(sql, new RowMapper() {
				public Map mapRow(ResultSet rs, int idx) throws SQLException {
					Map cons = new HashMap();
					cons.put("nomEmpresa", rs.getString("nom_empresa"));
					cons.put("concepto", rs.getString("concepto"));
					cons.put("importe", rs.getDouble("importe"));
					cons.put("fecValor", rs.getString("fec_valor"));
					cons.put("descRubro", rs.getString("desc_rubro"));
					return cons;
				}});
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:CashFlowNDaoImpl, M:cashFlowDatos");
		}
		return listResult;
	}
	
	public String retornaFecha(String dataInd) {
		return dataInd.substring(3);
	}
	
	public String retornaFechaMes(String dataInd) {
			return dataInd.substring(3,4);
	}
	
	public String retornaFechaSemana(String dataInd) {
		return  dataInd.substring(9);
	}
	
	public String retornaFechaAnio(String dataInd) {
		return dataInd.substring(4,8);
	}
	
	public List<PosicionBancariaDto> comboGrupoRubro(String idTipoMovto) {
		List<PosicionBancariaDto> listResult = new ArrayList<PosicionBancariaDto>();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT * \n");
			sql.append(" FROM cat_grupo cg, cat_rubro cr \n");
			sql.append(" WHERE cg.id_grupo = cr.id_grupo \n");
			sql.append(" 	And cr.ingreso_egreso = '"+ idTipoMovto +"' \n");
			sql.append(" ORDER BY desc_grupo \n");
			
			System.out.println("Query grupo rubro: " + sql.toString());
			
			listResult = jdbcTemplate.query(sql.toString(), new RowMapper() {
				public PosicionBancariaDto mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDto cons = new PosicionBancariaDto();
					cons.setIdGrupo(rs.getString("id_grupo"));
					cons.setDescGrupo(rs.getString("desc_grupo"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:CashFlowNDaoImpl, M:comboGrupoRubro");
		}
		return listResult;
	}
	
	public List<PosicionBancariaDto> comboRubro(int idGrupo){
		List<PosicionBancariaDto> listResult = new ArrayList<PosicionBancariaDto>();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT * \n");
			sql.append(" FROM cat_rubro \n");
			sql.append(" WHERE id_grupo = "+ idGrupo +" \n");
			sql.append(" ORDER BY desc_rubro \n");
			
			System.out.println("Query rubro: " + sql.toString());
			
			listResult = jdbcTemplate.query(sql.toString(), new RowMapper() {
				public PosicionBancariaDto mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDto cons = new PosicionBancariaDto();
					cons.setIdRubro(rs.getString("id_rubro"));
					cons.setDescRubro(rs.getString("desc_rubro"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:CashFlowNDaoImpl, M:comboRubro");
		}
		return listResult;
	}
	
	public int reclasificaMovtos(int noFolioDet, int idGrupo, int idRubro) {
		StringBuffer sql = new StringBuffer();
		int res = 0;
		
		try {
			sql.append(" UPDATE movimiento SET id_grupo = "+ idGrupo +", id_rubro = "+ idRubro +" \n");
			sql.append(" WHERE no_folio_det = "+ noFolioDet +" \n");
			
			System.out.println("Query update movimiento grupo y rubro: " + sql.toString());
			
			res = jdbcTemplate.update(sql.toString());
			
			if(res == 0) {
				sql.append(" UPDATE hist_movimiento SET id_grupo = "+ idGrupo +", id_rubro = "+ idRubro +" \n");
				sql.append(" WHERE no_folio_det = "+ noFolioDet +" \n");
				
				System.out.println("Query update hist_movimiento grupo y rubro: " + sql.toString());
				
				res = jdbcTemplate.update(sql.toString());
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:CashFlowNDaoImpl, M:reclasificaMovtos");
		}
		return res;
	}
	
	public int diaSemana(String dia){
		int res = 0;
		try{
			res = jdbcTemplate.queryForInt("select DATEPART(WEEKDAY,'"+ dia +"') as diaSemana");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:CashFlowNDaoImpl, M:diaSemana");
		}
		return res;
	}
	
	public List<PosicionBancariaDto> totalSdoChequera(List<Map<String, String>> parametros, Date fecIni, int tipoRep) {
		StringBuffer sql = new StringBuffer();
		List<PosicionBancariaDto> listDatos = new ArrayList<PosicionBancariaDto>();
		try {
			
			sql.append(" SELECT sum(saldo_inicial) as sdoIni, sum(saldo_final) as sdoFin, \n");
			if(tipoRep == 0)
				sql.append(" 		'col' + right( '00' + convert(varchar(15), day(fec_valor)), 2 ) + '/'  + right( '00' + convert(varchar(15), month(fec_valor)), 2 ) + '/' + convert(varchar(15), year(fec_valor)) as mesAnio  \n");
			if(tipoRep == 2)
				sql.append(" 		'col' + convert(varchar(15), month(fec_valor)) + convert(varchar(15), year(fec_valor)) as mesAnio  \n");
			sql.append(" FROM hist_cat_cta_banco \n");
			if(tipoRep == 0)
				sql.append("WHERE fec_valor between DATEADD(s, 0, DATEADD(mm, DATEDIFF(m, 0, '"+parametros.get(0).get("fecIni")+"'), 0)) and  		DATEADD(s, -1, DATEADD(mm, DATEDIFF(m, 0, '"+parametros.get(0).get("fecFin")+"') + 1, 0))\n");
			if(tipoRep == 2)
				sql.append(" WHERE fec_valor = '"+ funciones.ponerFecha(fecIni) +"' \n");
			sql.append(" 	And id_divisa = '"+ parametros.get(0).get("idDivisa") +"' \n");
			if(Integer.parseInt(parametros.get(0).get("noEmpresa")) != 0)
				sql.append(" 	And no_empresa = "+ parametros.get(0).get("noEmpresa") +" \n");
			sql.append(" GROUP BY day(fec_valor), month(fec_valor), year(fec_valor) \n");
				
			
			
			System.out.println("Query de total saldo de chequera: " + sql.toString());
			
			listDatos = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public PosicionBancariaDto mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDto cons = new PosicionBancariaDto();
					cons.setSdoIni(rs.getDouble("sdoIni"));
					cons.setSdoFin(rs.getDouble("sdoFin"));
					cons.setNomCol(rs.getString("mesAnio"));
					return cons;
				}});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:CashFlowNDaoImpl, M:totalSdoChequera");
		}
		return listDatos;
	}
	
	public int insertaInteres(int noFolioDet,int interes,String concepto,int idGrupo,int idRubro,String fecValor){
		StringBuffer sql = new StringBuffer();
		int res = 0;

		try{
			sql.append("insert into interes_fideicomiso (no_folio_det,importe,concepto,id_grupo,id_rubro,fec_valor) \n");
			sql.append("values("+noFolioDet+",\n");
			sql.append(""+interes+",\n");
			sql.append("'"+concepto+"',\n");
			sql.append("72500,\n");
			sql.append("72502,\n");
			sql.append("'"+ funciones.cambiarFecha(fecValor) +"')");
			System.out.println("inserta interes "+sql.toString());
			res = jdbcTemplate.update(sql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:CashFlowNDaoImpl, M:insertaInteres");
		}
		return res;
	}
	
}
