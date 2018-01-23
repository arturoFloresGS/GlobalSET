package com.webset.set.financiamiento.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.apache.poi.ss.formula.functions.Rows;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.coinversion.dao.impl.CoinversionDaoImpl;
import com.webset.set.financiamiento.dao.ReporteAnalisisLineasCreditoDao;
import com.webset.set.financiamiento.dao.ReportePasivosFDao;
import com.webset.set.financiamiento.dto.AnalisisLineasCreditoDto;
import com.webset.set.financiamiento.dto.AvalGarantiaDto;
import com.webset.set.financiamiento.dto.ReportePasivosFDto;
import com.webset.set.global.dao.GlobalDao;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.ConstantesSet;

public class ReporteAnalisisLineasCDaoImpl implements ReporteAnalisisLineasCreditoDao {
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	GlobalDao globalDao;
	ConsultasGenerales consultasGenerales;
	Funciones funciones = new Funciones();
	GlobalSingleton globalSingleton;
	String gsDBM = ConstantesSet.gsDBM;
	String psTipoMenu="";
	private static Logger logger = Logger.getLogger(CoinversionDaoImpl.class);
	public void setDataSource(DataSource dataSource) {
		try {
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		} catch (Exception e) {
			logger.error("Error", e);
			e.printStackTrace();
		}
	}
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	@Override
	public List<LlenaComboGralDto> obtenerGruposEmpresa() {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append(" SELECT DISTINCT ID_SUPER_GRUPO, DESC_SUPER_GRUPO");
			sbSQL.append(" FROM CAT_SUPER_GRUPO");
			sbSQL.append(" ORDER BY ID_SUPER_GRUPO");
			logger.info("obtenerGruposEmpresa "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto compon = new LlenaComboGralDto();
					compon.setId(rs.getInt("ID_SUPER_GRUPO"));
					compon.setDescripcion(rs.getString("DESC_SUPER_GRUPO"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C: ReporteAnalisisLineasCDaoImpl, M: obtenerGruposEmpresa");
		}
		return list;
	}
	@Override
	public List<LlenaComboGralDto> obtenerTipoFinanciamiento(String vsTipoMenu) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {

			sbSQL.append("\n SELECT  id_tipo_contrato as ID, descripcion as Descrip ");
			sbSQL.append("\n FROM    cat_tipo_contrato ");
			if(!vsTipoMenu.equals("TODOS"))
				sbSQL.append("\n WHERE   financiamiento = '" +vsTipoMenu+"' ");
			logger.info("obtenerTipoFinanciamiento "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto compon = new LlenaComboGralDto();
					compon.setId(rs.getInt("ID"));
					compon.setDescripcion(rs.getString("Descrip"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: Financiamiento, C: ReporteAnalisisLineasCDaoImpl, M: obtenerTipoFinanciamiento");
		}
		return list;
	}
	@Override
	public List<LlenaComboGralDto> llenarCmbEmpresa(int piNoUsuario) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n  SELECT distinct e.no_empresa as ID, e.nom_empresa as Descrip ");
			sbSQL.append("\n  FROM   empresa e ");
			sbSQL.append("\n   WHERE  no_empresa in (select no_empresa from usuario_empresa where no_usuario  = " + piNoUsuario + ") ");
			sbSQL.append("\n   ORDER BY ID ");
			logger.info("llenarCmbEmpresa "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto compon = new LlenaComboGralDto();
					compon.setId(rs.getInt("ID"));
					compon.setDescripcion(rs.getString("Descrip"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: Financiamiento, C: ReporteAnalisisLineasCDaoImpl, M: llenarCmbEmpresa");
		}
		return list;
	}
	@Override
	public List<LlenaComboGralDto> llenarCmbEmpresa(int piNoUsuario,int noGrupo) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append(" SELECT e.no_empresa,e.nom_empresa" );
			sbSQL.append(" FROM cat_super_grupo_flujo csf," );
			sbSQL.append("      grupo_empresa ge,empresa e" );
			sbSQL.append(" WHERE csf.id_grupo_flujo = ge.id_grupo_flujo" );
			sbSQL.append(" AND ge.no_empresa = e.no_empresa" );
			sbSQL.append(" AND e.no_empresa IN(select no_empresa from usuario_empresa" );
			sbSQL.append("                     where no_usuario=" + piNoUsuario + ")" );
			sbSQL.append(" AND csf.id_super_grupo = " + noGrupo );
			System.out.println(sbSQL.toString());
			logger.info("llenarCmbEmpresa "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto compon = new LlenaComboGralDto();
					compon.setId(rs.getInt("no_empresa"));
					compon.setDescripcion(rs.getString("nom_empresa"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: Financiamiento, C: ReporteAnalisisLineasCDaoImpl, M: llenarCmbEmpresa");
		}
		return list;
	}

	@Override
	public List<AnalisisLineasCreditoDto> obtenerAnalisisLineas(int empresa, int tipoFinanciamiento,
			boolean vbTipoCambio, String vsMenu, String fechaInicio, String fechaFin) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		List<AnalisisLineasCreditoDto> list = new ArrayList<AnalisisLineasCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		DecimalFormat formato = new DecimalFormat("###,###,###,##0.00");
		DecimalFormat formato2 = new DecimalFormat("###,##0.00000");
		globalSingleton = GlobalSingleton.getInstancia();
		try {
			sbSQL.append("\n  SELECT  ccc.id_financiamiento, e.no_empresa, e.nom_empresa, coalesce(st.soiemp, '') as soiemp, ccc.id_banco_prestamo, ");
			sbSQL.append("\n    case when coalesce(vd.valor, 0) = 0 then ccc.monto_autorizado else (coalesce(vd.valor, 0) * ccc.monto_autorizado) end as monto_autorizado, ");
			sbSQL.append("\n    coalesce(case when coalesce(vd.valor, 0) = 0 then cdc.monto_disposicion else (coalesce(vd.valor, 0) * cdc.monto_disposicion) end, 0) as monto_disposicion, ");
			sbSQL.append("\n    coalesce(cdc.tasa_base, '') as tasa_base, coalesce(case when cdc.tipo_tasa = 'V' then cdc.puntos else cdc.valor_tasa end, 0) as puntos, ccc.fec_vencimiento, coalesce(ccc.tipo_operacion, '') as tipo_operacion, ");
			sbSQL.append("\n    coalesce(case when coalesce(vd.valor, 0) = 0 then (select sum(cac1.capital) from cat_amortizacion_credito cac1");
			sbSQL.append("\n    where cac1.id_contrato = ccc.id_financiamiento And cac1.estatus = 'A') else (coalesce(vd.valor, 0) *");
			sbSQL.append("\n    coalesce((select sum(cac1.capital) from cat_amortizacion_credito cac1");
			sbSQL.append("\n    where cac1.id_contrato = ccc.id_financiamiento And cac1.estatus = 'A'), 0)) end, 0) as monto_dispuesto,");
			sbSQL.append("\n    case when ccc.revolvencia = 'N' then 0 else");
			sbSQL.append("\n   case when coalesce(vd.valor, 0) = 0 then coalesce((ccc.monto_autorizado) - (coalesce((select sum(cac2.capital) from cat_amortizacion_credito cac2");
			sbSQL.append("\n   where cac2.id_contrato = cdc.id_financiamiento And cac2.estatus = 'A'), 0)), 0)");
			sbSQL.append("\n    else (coalesce(vd.valor, 0) *  ((ccc.monto_autorizado) - (select sum(cac2.capital) from cat_amortizacion_credito cac2");
			sbSQL.append("\n    where cac2.id_contrato = cdc.id_financiamiento And cac2.estatus = 'A'))) end end as monto_disponible,");
			sbSQL.append("\n    coalesce(vd.valor, 0) as valor, ccc.id_divisa, coalesce(cdc.tipo_tasa, '') as tipo_tasa, ctc.descripcion ");
			if (psTipoMenu.equals("A"))
				sbSQL.append("\n    ,ca.desc_arrendadora as desc_banco ");
			else
				sbSQL.append("\n    ,cb.desc_banco ");

			if(gsDBM.equals("SQL SERVER")){
				sbSQL.append("\n    , coalesce(case when coalesce(vd.valor, 0) = 0 then (select top 1 saldo_insoluto from cat_amortizacion_credito cac where cac.id_contrato = ccc.id_financiamiento ");
				sbSQL.append("\n    And cac.id_disposicion = cdc.id_disposicion and cac.estatus = 'A' order by fec_vencimiento asc) else");
				sbSQL.append("\n    ((select top 1 saldo_insoluto from cat_amortizacion_credito cac where cac.id_contrato = ccc.id_financiamiento");
				sbSQL.append("\n    And cac.id_disposicion = cdc.id_disposicion and cac.estatus = 'A' order by fec_vencimiento asc) * coalesce(vd.valor, 0)) end ,0) as saldo_insoluto");
			}
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE")){
				sbSQL.append("\n    , coalesce(case when coalesce(vd.valor, 0) = 0 then (select saldo_insoluto from cat_amortizacion_credito cac where cac.id_contrato = ccc.id_financiamiento ");
				sbSQL.append("\n    And cac.id_disposicion = cdc.id_disposicion and cac.estatus = 'A' order by fec_vencimiento asc limit 1) else");
				sbSQL.append("\n    ((select saldo_insoluto from cat_amortizacion_credito cac where cac.id_contrato = ccc.id_financiamiento");
				sbSQL.append("\n    And cac.id_disposicion = cdc.id_disposicion and cac.estatus = 'A' order by fec_vencimiento asc limit 1) * coalesce(vd.valor, 0)) end ,0) as saldo_insoluto");
			}
			sbSQL.append("\n  FROM  cat_contrato_credito ccc, cat_disposicion_credito cdc left join valor_divisa vd on (cdc.id_divisa = vd.id_divisa and ");
			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append("\n       cast(vd.fec_divisa as date) = '"+fechaHoy+ "'), ");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\n     cast(vd.fec_divisa as date) = '" +fechaHoy+ "'), ");

			sbSQL.append("\n         empresa e left join SET006 st on (e.no_empresa = setemp and siscod in ('CP', 'CO')), cat_tipo_contrato ctc ");
			if (psTipoMenu.equals("A")){
				sbSQL.append("\n   ,cat_arrendadoras ca ");
				sbSQL.append("\n WHERE ccc.id_banco_prestamo = ca.id_arrendadora ");
			}
			else{
				sbSQL.append("\n  ,cat_banco cb ");
				sbSQL.append("\n  WHERE ccc.id_banco_prestamo = cb.id_banco ");
			}
			sbSQL.append("\n        And ccc.id_financiamiento = cdc.id_financiamiento ");
			sbSQL.append("\n       And cdc.estatus = 'A'");
			sbSQL.append("\n       And ccc.id_tipo_financiamiento = ctc.id_tipo_contrato ");
			sbSQL.append("\n       And ccc.no_empresa = e.no_empresa ");
			sbSQL.append("\n        And ccc.estatus = 'A' ");
			sbSQL.append("\n       And ccc.no_empresa in (select distinct(no_empresa) from usuario_empresa where no_usuario = " +globalSingleton.getUsuarioLoginDto().getIdUsuario()+ ") ");
			if(empresa!=0)
				sbSQL.append("\n   And ccc.no_empresa = "+empresa+ " ");
			if (tipoFinanciamiento!=0) 
				sbSQL.append("\n   And ccc.id_tipo_financiamiento = " +tipoFinanciamiento+ " ");
			else
				sbSQL.append("\n And ctc.financiamiento in ('" + psTipoMenu + "') ");

			if(gsDBM.equals("SQL SERVER")){
				sbSQL.append("\n   and cast(ccc.fec_vencimiento as date) between '" +ponerFormatoFecha(fechaInicio)+ "' and '" +ponerFormatoFecha(fechaFin)+ "' ");
				sbSQL.append("\n ORDER BY st.soiemp, ccc.no_empresa, ccc.id_financiamiento, ccc.id_banco_prestamo, cast(ccc.fec_vencimiento as date) ");
			} 
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE")){
				sbSQL.append("\n   and cast(ccc.fec_vencimiento as date) between '" +ponerFormatoFecha(fechaInicio)+ "' and '" +ponerFormatoFecha(fechaFin)+ "' ");
				sbSQL.append("\n ORDER BY st.soiemp, ccc.no_empresa, ccc.id_financiamiento, ccc.id_banco_prestamo, cast(ccc.fec_vencimiento as date) ");
			}
			System.out.println(sbSQL.toString());
			logger.info("obtenerAnalisisLineas "+sbSQL);
			List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sbSQL.toString());
			String empresaAux=String.valueOf(empresa),vsGpoEmpresa="";
			int posicion=0;
			String vsFinanciamiento="";
			double montoDisponible=0;
			double vdMontoAutorizado=0,vdMontoDisposicion=0,vdMontoAnticipagos=0,vdMontoFactoraje=0,vdMontoDispues=0,vdMontoDisponible=0;
			double vdMontoAutorizadoAux=0,vdMontoDisposicionAux=0,vdMontoAnticipagosAux=0,vdMontoFactorajeAux=0,vdMontoDispuesAux=0,vdMontoDisponibleAux=0;
			if(rows.isEmpty())
				return list;
			for (Map row : rows) {
				if(row.get("monto_disponible")==null)
					montoDisponible=0;
				else
					montoDisponible=Double.parseDouble(row.get("monto_disponible").toString());
				if(!vsGpoEmpresa.equals(row.get("no_empresa").toString().trim())){
					if(posicion>1){
						AnalisisLineasCreditoDto dto = new AnalisisLineasCreditoDto();
						dto.setDescBanco("TOTAL: ");
						dto.setColor("color:#165ACB");
						dto.setLinea(formato.format(vdMontoAutorizado));
						dto.setPasivo(formato.format(vdMontoDisposicion));
						dto.setAnticipagos(formato.format(vdMontoAnticipagos));
						dto.setFactoraje(formato.format(vdMontoFactoraje));
						dto.setTotalLinea(formato.format(vdMontoDispues));
						dto.setTotalLineaDisp(formato.format(vdMontoDisponible));
						list.add(dto);
						posicion++;
						vdMontoAutorizadoAux+=vdMontoAutorizado;
						vdMontoDisposicionAux+=vdMontoDisposicion;
						vdMontoAnticipagosAux+=vdMontoAnticipagos;
						vdMontoFactorajeAux+=vdMontoFactoraje;
						vdMontoDispuesAux+=vdMontoDispues;
						vdMontoDisponibleAux+=vdMontoDisponible;
						vdMontoAutorizado=0;
						vdMontoDisposicion=0;
						vdMontoAnticipagos=0;
						vdMontoFactoraje=0;
						vdMontoDispues=0;
						vdMontoDisponible=0;
						dto = new AnalisisLineasCreditoDto();
						list.add(dto);
						posicion++;
					}
					AnalisisLineasCreditoDto dto = new AnalisisLineasCreditoDto();
					dto.setNomEmpresa(row.get("nom_empresa").toString());
					list.add(dto);
					posicion++;
					vsGpoEmpresa = row.get("no_empresa").toString().trim();
				}
				AnalisisLineasCreditoDto dto = new AnalisisLineasCreditoDto();
				dto.setNoEmpresa(row.get("no_empresa").toString());
				dto.setIdFinanciamiento(row.get("id_financiamiento").toString());
				dto.setTipoCredito(row.get("descripcion").toString());
				dto.setNoBanco(row.get("id_banco_prestamo").toString());
				dto.setDescBanco(row.get("desc_banco").toString());
				dto.setPasivo(formato.format(Double.parseDouble(row.get("saldo_insoluto").toString())));
				dto.setFecVencimiento(funciones.cambiarFecha(row.get("fec_vencimiento").toString()));
				dto.setCartas("");
				dto.setNamig("");
				dto.setAnticipagos("0.00");
				dto.setFactoraje("0.00");
				if(row.get("tipo_tasa").toString().trim().equals("F")||row.get("tipo_tasa").toString().trim().equals(""))
					dto.setTasa(formato2.format(Double.parseDouble(row.get("puntos").toString())));
				else
					dto.setTasa(row.get("tasa_base").toString()+"+"+formato2.format(Double.parseDouble(row.get("puntos").toString())));
				if(row.get("tipo_operacion").toString().trim().equals("A")) {
					dto.setAnticipagos(formato.format(Double.parseDouble(row.get("saldo_insoluto").toString())));
					vdMontoAnticipagos = vdMontoAnticipagos + Double.parseDouble(row.get("saldo_insoluto").toString());
				}
				else if (row.get("tipo_operacion").toString().trim().equals("C")||row.get("tipo_operacion").toString().trim().equals("S")) {
					dto.setFactoraje(formato.format(Double.parseDouble(row.get("saldo_insoluto").toString())));
					vdMontoFactoraje = vdMontoFactoraje + Double.parseDouble(row.get("saldo_insoluto").toString());
				}
				if (!vsFinanciamiento.equals(row.get("id_financiamiento").toString().trim())) {
					dto.setLinea(formato.format(Double.parseDouble(row.get("monto_autorizado").toString())));
					dto.setTotalLinea(formato.format(Double.parseDouble(row.get("monto_dispuesto").toString())));
					dto.setTotalLineaDisp(formato.format(montoDisponible));
					vdMontoDispues = vdMontoDispues + Double.parseDouble(row.get("monto_dispuesto").toString());
					vdMontoAutorizado = vdMontoAutorizado +Double.parseDouble(row.get("monto_autorizado").toString());
					vdMontoDisponible = vdMontoDisponible + montoDisponible;
					vsFinanciamiento =row.get("id_financiamiento").toString().trim();	
				}
				vdMontoDisposicion = vdMontoDisposicion + Double.parseDouble(row.get("saldo_insoluto").toString());
				list.add(dto);
			};
			AnalisisLineasCreditoDto dto = new AnalisisLineasCreditoDto();
			dto.setDescBanco("TOTAL: ");
			dto.setColor("color:#165ACB");
			dto.setLinea(formato.format(vdMontoAutorizado));
			dto.setPasivo(formato.format(vdMontoDisposicion));
			dto.setAnticipagos(formato.format(vdMontoAnticipagos));
			dto.setFactoraje(formato.format(vdMontoFactoraje));
			dto.setTotalLinea(formato.format(vdMontoDispues));
			dto.setTotalLineaDisp(formato.format(vdMontoDisponible));
			vdMontoAutorizadoAux+=vdMontoAutorizado;
			vdMontoDisposicionAux+=vdMontoDisposicion;
			vdMontoAnticipagosAux+=vdMontoAnticipagos;
			vdMontoFactorajeAux+=vdMontoFactoraje;
			vdMontoDispuesAux+=vdMontoDispues;
			vdMontoDisponibleAux+=vdMontoDisponible;
			vdMontoAutorizado=0;
			vdMontoDisposicion=0;
			vdMontoAnticipagos=0;
			vdMontoFactoraje=0;
			vdMontoDispues=0;
			vdMontoDisponible=0;
			list.add(dto);
			dto = new AnalisisLineasCreditoDto();
			dto.setDescBanco("TOTALES MULTILINEAS: ");
			dto.setLinea(formato.format(vdMontoAutorizadoAux));
			dto.setPasivo(formato.format(vdMontoDisposicionAux));
			dto.setAnticipagos(formato.format(vdMontoAnticipagosAux));
			dto.setFactoraje(formato.format(vdMontoFactorajeAux));
			dto.setTotalLinea(formato.format(vdMontoDispuesAux));
			dto.setTotalLineaDisp(formato.format(vdMontoDisponibleAux));
			dto.setColor("color:#165ACB");
			list.add(dto);
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: Financiamiento, C: ReporteAnalisisLineasCDaoImpl, M: obtenerAnalisisLineas");
		}
		return list;
	}
	@Override
	public List<AnalisisLineasCreditoDto> obtenerValoresDivisa() {
		List<AnalisisLineasCreditoDto> list = new ArrayList<AnalisisLineasCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		try {
			sbSQL.append("\n SELECT distinct cdc.id_divisa, coalesce(vd.valor, 0) as valor ");
			sbSQL.append("\n   FROM cat_disposicion_credito cdc left join valor_divisa vd on (cdc.id_divisa = vd.id_divisa) ");
			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append("\n   WHERE cast(vd.fec_divisa as date) = '" +fechaHoy+"' ");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\n  WHERE cast(vd.fec_divisa as date) = '"+fechaHoy+ "' ");
			System.out.println(sbSQL.toString());
			logger.info("obtenerValoresDivisa "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public AnalisisLineasCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					AnalisisLineasCreditoDto compon = new AnalisisLineasCreditoDto();
					compon.setDivisa(rs.getString("id_divisa"));
					compon.setValor(rs.getDouble("valor"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: Financiamiento, C: ReporteAnalisisLineasCDaoImpl, M: obtenerValoresDivisa");
		}
		return list;
	}

	@Override
	public List<AnalisisLineasCreditoDto> obtenerResumen(int empresa, int tipoFinanciamiento,String vsMenu) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		List<AnalisisLineasCreditoDto> list = new ArrayList<AnalisisLineasCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		DecimalFormat formato = new DecimalFormat("###,###,###,##0.00");
		DecimalFormat formato2 = new DecimalFormat("###,##0.00000");
		globalSingleton = GlobalSingleton.getInstancia();
		try {
			sbSQL.append("\n SELECT  ccc.id_financiamiento, coalesce(ccc.largo_plazo, '') as largo_plazo, cgf.id_grupo_flujo, cgf.desc_grupo_flujo, coalesce(ccc.tipo_operacion, '') as tipo_operacion,");
			sbSQL.append("\n   case when coalesce(vd.valor, 0) = 0 then ccc.monto_autorizado else (coalesce(vd.valor, 0) * ccc.monto_autorizado) end as monto_autorizado,");
			sbSQL.append("\n   coalesce(case when coalesce(vd.valor, 0) = 0 then (select sum(cac1.capital) from cat_amortizacion_credito cac1");
			sbSQL.append("\n   where cac1.id_contrato = ccc.id_financiamiento And cac1.estatus = 'A') else (coalesce(vd.valor, 0) *");
			sbSQL.append("\n   coalesce((select sum(cac1.capital) from cat_amortizacion_credito cac1");
			sbSQL.append("\n   where cac1.id_contrato = ccc.id_financiamiento And cac1.estatus = 'A'), 0)) end, 0) as monto_dispuesto,");

			sbSQL.append("\n   case when coalesce(vd.valor, 0) = 0 then coalesce((ccc.monto_autorizado) - (coalesce((select sum(cac2.capital) from cat_amortizacion_credito cac2");
			sbSQL.append("\n   where cac2.id_contrato = cdc.id_financiamiento And cac2.estatus = 'A'), 0)), 0)");
			sbSQL.append("\n   else (coalesce(vd.valor, 0) *  ((ccc.monto_autorizado) - (select sum(cac2.capital) from cat_amortizacion_credito cac2");
			sbSQL.append("\n   where cac2.id_contrato = cdc.id_financiamiento And cac2.estatus = 'A'))) end as monto_disponible");
			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append("\n FROM  cat_contrato_credito ccc left join cat_disposicion_credito cdc on (ccc.id_financiamiento = cdc.id_financiamiento And cdc.estatus = 'A' ) left join valor_divisa vd on (cdc.id_divisa = vd.id_divisa and cast(vd.fec_divisa as date) = '" +fechaHoy+ "'),");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\n FROM  cat_contrato_credito ccc left join cat_disposicion_credito cdc on (ccc.id_financiamiento = cdc.id_financiamiento And cdc.estatus = 'A' ) left join valor_divisa vd on (cdc.id_divisa = vd.id_divisa and cast(vd.fec_divisa as date) = '" +fechaHoy+ "'),");
			sbSQL.append("\n   grupo_empresa ge, cat_grupo_flujo cgf, cat_tipo_contrato ctc, empresa e, ");
			if (psTipoMenu.equals("A")){
				sbSQL.append("\n   cat_arrendadoras ca ");
				sbSQL.append("\n Where ccc.id_banco_prestamo = ca.id_arrendadora ");
			}else{
				sbSQL.append("\n   cat_banco cb ");
				sbSQL.append("\n Where ccc.id_banco_prestamo = cb.id_banco ");
			}
			sbSQL.append("\n   And ccc.no_empresa = ge.no_empresa ");
			sbSQL.append("\n   And ge.id_grupo_flujo = cgf.id_grupo_flujo ");
			sbSQL.append("\n   And ccc.id_tipo_financiamiento = ctc.id_tipo_contrato ");
			sbSQL.append("\n   And ccc.no_empresa = e.no_empresa ");
			sbSQL.append("\n  And ccc.estatus = 'A' ");
			sbSQL.append("\n   And ccc.no_empresa in (select distinct(no_empresa) from usuario_empresa where no_usuario = " +globalSingleton.getUsuarioLoginDto().getIdUsuario()+ ") ");
			if(empresa!=0)
				sbSQL.append("\n And ge.no_empresa = " +empresa +" ");
			if(tipoFinanciamiento!=0)
				sbSQL.append("\n   And ccc.id_tipo_financiamiento = " +tipoFinanciamiento+ " ");
			else{
				if(!vsMenu.equals(""))  
					sbSQL.append("\n   And ctc.financiamiento in ('" + vsMenu + "') ");
			}
			sbSQL.append("\n ORDER BY ccc.largo_plazo, cgf.id_grupo_flujo, ccc.tipo_operacion, ccc.id_financiamiento ");

			System.out.println(sbSQL.toString());

			logger.info("obtenerAnalisisLineas "+sbSQL);
			List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sbSQL.toString());
			String vlGrupo="",vsDesGrupo="",vsTipoOper="",vsLargoPlazo="",vsFinan="";
			boolean vbFinal=false;
			double vdMontoAut=0,vdMontoDis=0,vdMontoAutAcum=0,vdMontoDisAcum=0;

			vlGrupo = rows.get(0).get("id_grupo_flujo").toString().trim();
			vsDesGrupo = rows.get(0).get("desc_grupo_flujo").toString().trim(); 
			vsTipoOper = rows.get(0).get("tipo_operacion").toString().trim(); 
			vsLargoPlazo = rows.get(0).get("largo_plazo").toString().trim(); 
			AnalisisLineasCreditoDto dto=new AnalisisLineasCreditoDto();
			dto.setDescripcion("LINEAS DE CORTO PLAZO");
			dto.setColor("color:#165ACB");
			list.add(dto);
			dto=new AnalisisLineasCreditoDto();
			list.add(dto);

			for (int i = 0; i < rows.size(); i++) {

				if(vlGrupo!=rows.get(i).get("id_grupo_flujo").toString().trim()||!vsTipoOper.equals(rows.get(i).get("tipo_operacion").toString().trim())){
					dto=new AnalisisLineasCreditoDto();
					dto.setIdGrupo(vlGrupo);
					dto.setDescripcion(vsDesGrupo);
					dto.setLineasAut(formato.format(vdMontoAut));
					dto.setDispuestas(formato.format(vdMontoDis));
					dto.setDisponibles(formato.format(vdMontoAut - vdMontoDis));
					list.add(dto);
					vlGrupo = rows.get(i).get("id_grupo_flujo").toString().trim();
					vsDesGrupo = rows.get(i).get("desc_grupo_flujo").toString().trim();
					vsTipoOper =rows.get(i).get("tipo_operacion").toString().trim();
					vdMontoAutAcum = vdMontoAutAcum + vdMontoAut;
					vdMontoDisAcum = vdMontoDisAcum + vdMontoDis;
					vdMontoAut = 0;
					vdMontoDis = 0;
				}
				if(!vsLargoPlazo.equals(rows.get(i).get("largo_plazo").toString().trim())){
					dto=new AnalisisLineasCreditoDto();
					dto.setColor("color:#165ACB");	
					dto.setDescripcion("TOTAL");
					dto.setLineasAut(formato.format(vdMontoAutAcum));
					dto.setDispuestas(formato.format(vdMontoDisAcum));
					dto.setDisponibles(formato.format(vdMontoAutAcum - vdMontoDisAcum));
					list.add(dto);
					vdMontoAutAcum = 0;
					vdMontoDisAcum = 0;
					dto=new AnalisisLineasCreditoDto();
					list.add(dto);
					if(!vbFinal) {
						dto=new AnalisisLineasCreditoDto();
						dto.setDescripcion("LINEAS DE LARGO PLAZO");
						dto.setColor("color:#165ACB");
						list.add(dto);
						dto=new AnalisisLineasCreditoDto();
						list.add(dto);
					}
				}
				if(!vsFinan.equals(rows.get(i).get("id_financiamiento").toString().trim())) {
					vdMontoAut = vdMontoAut + Double.parseDouble(rows.get(i).get("monto_autorizado").toString());
					vdMontoDis = vdMontoDis +Double.parseDouble(rows.get(i).get("monto_dispuesto").toString()); 
					vsFinan = rows.get(i).get("id_financiamiento").toString().trim();
				}
				vsLargoPlazo = rows.get(i).get("largo_plazo").toString().trim();
				if(i==rows.size()-1){
					dto=new AnalisisLineasCreditoDto();
					dto.setIdGrupo(vlGrupo);
					dto.setDescripcion(vsDesGrupo);
					dto.setLineasAut(formato.format(vdMontoAut));
					dto.setDispuestas(formato.format(vdMontoDis));
					dto.setDisponibles(formato.format(vdMontoAut - vdMontoDis));
					list.add(dto);
					vdMontoAutAcum = vdMontoAutAcum + vdMontoAut;
					vdMontoDisAcum = vdMontoDisAcum + vdMontoDis;
					dto=new AnalisisLineasCreditoDto();
					dto.setDescripcion("TOTAL");
					dto.setColor("color:#165ACB");
					dto.setLineasAut(formato.format(vdMontoAutAcum));
					dto.setDispuestas(formato.format(vdMontoDisAcum));
					dto.setDisponibles(formato.format(vdMontoAutAcum - vdMontoDisAcum));
					list.add(dto);
					dto=new AnalisisLineasCreditoDto();
					list.add(dto);

				}
			}

		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: Financiamiento, C: ReporteAnalisisLineasCDaoImpl, M: obtenerAnalisisLineas");
		}
		return list;
	}

	public String ponerFormatoFecha(String fechaString){
		return fechaString.substring(6,10)+"-"+fechaString.substring(3,5)+"-"+fechaString.substring(0,2);

	}
}
