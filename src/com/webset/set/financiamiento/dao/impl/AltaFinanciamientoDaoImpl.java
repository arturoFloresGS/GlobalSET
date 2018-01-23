package com.webset.set.financiamiento.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.coinversion.dao.impl.CoinversionDaoImpl;
import com.webset.set.financiamiento.dao.AltaFinanciamientoDao;
import com.webset.set.financiamiento.dto.AmortizacionCreditoDto;
import com.webset.set.financiamiento.dto.AvalGarantiaDto;
import com.webset.set.financiamiento.dto.BitacoraCreditoBanDto;
import com.webset.set.financiamiento.dto.ContratoCreditoDto;
import com.webset.set.financiamiento.dto.DisposicionCreditoDto;
import com.webset.set.financiamiento.dto.ObligacionCreditoDto;
import com.webset.set.financiamiento.dto.Parametro;
import com.webset.set.financiamiento.dto.ProvisionCreditoDTO;
import com.webset.set.global.dao.GlobalDao;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.Retorno;
import com.webset.utils.tools.Utilerias;
import com.webset.set.utilerias.ConstantesSet;

public class AltaFinanciamientoDaoImpl implements AltaFinanciamientoDao {
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	GlobalDao globalDao;
	ConsultasGenerales consultasGenerales;
	Funciones funciones = new Funciones();
	GlobalSingleton globalSingleton;
	String vsTipoMenu="";
	String gsDBM = ConstantesSet.gsDBM;
	private static Logger logger = Logger.getLogger(AltaFinanciamientoDaoImpl.class);
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
	public List<Retorno> consultarConfiguraSet() {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet();
	}
	@Override
	public List<LlenaComboGralDto> obtenerContratos(int noEmpresa) {
		return llenarCmbContratos(noEmpresa);
	}
	@Override
	public List<LlenaComboGralDto>obtenerEmpresas(int idUsuario, boolean bMantenimiento){
		List<LlenaComboGralDto> lista = null;
		StringBuffer sbSQL = new StringBuffer();
		sbSQL.append("SELECT no_empresa, nom_empresa ");
		sbSQL.append("FROM empresa ");
		sbSQL.append("WHERE  ");
		if(idUsuario > 0){
			sbSQL.append("no_empresa in");
			sbSQL.append("	( SELECT no_empresa ");
			sbSQL.append("	FROM usuario_empresa ");
			sbSQL.append("	WHERE no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + " ) ");
			
			if(!bMantenimiento)
				sbSQL.append("	)");
		}
		lista = jdbcTemplate.query(sbSQL.toString(), new RowMapper<LlenaComboGralDto>(){
			@Override
			public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
				LlenaComboGralDto cons = new LlenaComboGralDto();
				cons.setId(rs.getInt("no_empresa"));
				cons.setDescripcion(rs.getString("nom_empresa"));
				return cons;
			}});
		return lista;
	}

	@Override
	public List<LlenaComboGralDto> obtenerPais() {
		return llenarCmbPais();
	}
	@Override
	public List<LlenaComboGralDto> obtenerTipoContratos(String psTipoFinan, boolean pbTipoContrato) {
		return llenarCmbTipoContratos(psTipoFinan, pbTipoContrato);
	}
	@Override
	public List<LlenaComboGralDto> obtenerBancos(String psNacionalidad, String psDivisa,int noEmpresa) {
		return llenarCmbBancos(psNacionalidad, psDivisa,noEmpresa);
	}
	@Override
	public List<LlenaComboGralDto> obtenerDivisas(boolean bRestringido) {
		return llenarCmbDivisas(bRestringido);
	}
	@Override
	public List<LlenaComboGralDto> obtenerTasa() {
		return llenarCmbTasa();
	}
	@Override
	public List<LlenaComboGralDto> obtenerArrendadoras() {
		return llenarCmbArrendadoras();
	}
	@Override
	public List<ContratoCreditoDto> obtenerContratoCredito(String clave) {
		return llenarDatosContratoCredito(clave);
	}
	@Override
	public List<LlenaComboGralDto> obtenerDisposiciones(String psIdContrato, boolean pbEstatus) {
		return llenarCmbDisposiciones(psIdContrato, pbEstatus);
	}
	@Override
	public List<ContratoCreditoDto> obtenerNoDisp(String psLinea) {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n SELECT id_disposicion, coalesce(sum(monto_disposicion), 0) as monto_disposicion   ");
			sbSQL.append("\n  	FROM cat_disposicion_credito ");
			sbSQL.append("\n  	WHERE id_financiamiento = '" + psLinea + "' ");
			sbSQL.append("\n  	GROUP BY id_disposicion");
			sbSQL.append("\n  	ORDER BY id_disposicion ASC  ");
			logger.info("obtenerNoDisp "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public ContratoCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					ContratoCreditoDto compon = new ContratoCreditoDto();
					compon.setIdDisposicion(rs.getInt("id_disposicion"));
					compon.setMontoDisposicion(rs.getDouble("monto_disposicion"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:obtenerNoDisp");
		}
		return list;
	}
	public List<LlenaComboGralDto> llenarCmbContratos(int idEmpresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		StringBuffer sbSQL = new StringBuffer();
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			/*	sbSQL.append("\n SELECT ccc.id_financiamiento AS ID, ccc.id_financiamiento as Descrip ");
			sbSQL.append("\n  FROM  cat_contrato_credito ccc, cat_tipo_contrato ctc, usuario_empresa ue");
			sbSQL.append("\n  WHERE ccc.id_tipo_financiamiento = ctc.id_tipo_contrato");
			sbSQL.append("\n And ccc.estatus = 'A'");
			sbSQL.append("\n And ctc.financiamiento = '' ");
			sbSQL.append("\n And ccc.no_empresa = ue.no_empresa ");
			sbSQL.append("\n  And (cast(fec_vencimiento as date) > '" + funciones.ponerFechaSola(fechaHoy) + "') or ");
			sbSQL.append("\n     ccc.id_financiamiento in (select id_financiamiento ");
			sbSQL.append("\n     from cat_disposicion_credito where estatus = 'A') ");
			if (globalSingleton.getUsuarioLoginDto().getNumeroEmpresa() != 0) {
				sbSQL.append("\nAnd ccc.no_empresa =" + idEmpresa);
			}
			sbSQL.append("\n ORDER BY substring(ccc.id_financiamiento, 5, 5)");*/
			//Se cambió para que aparecieran por empresa
			sbSQL.append("\n SELECT  ccc.id_financiamiento AS ID, ccc.id_financiamiento as Descrip");
			sbSQL.append("\n  FROM  cat_contrato_credito ccc, usuario_empresa ue");
			sbSQL.append("\n WHERE ccc.estatus = 'A'");
			if (idEmpresa != 0) {
				sbSQL.append("\nAnd ccc.no_empresa =" + idEmpresa);
			}
			sbSQL.append("\n  and ccc.id_tipo_financiamiento in(select id_tipo_financiamiento from cat_tipo_contrato where financiamiento='"+vsTipoMenu+"')");
			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append("\n And (cast(fec_vencimiento as date) > '"+ funciones.cambiarOrdenFecha(funciones.ponerFechaSola(fechaHoy)) +"')");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\n   And (cast(fec_vencimiento as date) > ' " +fechaHoy + "')");
			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append("\n ORDER BY substring(ccc.id_financiamiento, 5, 5) ");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\n ORDER BY substring(ccc.id_financiamiento from 5 for 8)");
System.out.println(sbSQL.toString());
			logger.info("llenarCmbContratos "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto compon = new LlenaComboGralDto();
					compon.setIdStr(rs.getString("ID"));
					compon.setDescripcion(rs.getString("Descrip"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:llenarCmbContratos");
		}
		return list;
	}
	public List<LlenaComboGralDto> llenarCmbTipoContratos(String psTipoFinan, boolean pbTipoContrato) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n SELECT  RTRIM(LTRIM(id_tipo_contrato)) AS ID, descripcion AS Descrip ");
			sbSQL.append("\n  FROM    cat_tipo_contrato ");
			if (!pbTipoContrato) {
				sbSQL.append("\n  WHERE   financiamiento in ('" + psTipoFinan + "') ");
			}
			logger.info("llenarCmbTipoContratos "+sbSQL);
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
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:llenarCmbTipoContratos");
		}
		return list;
	}
	public List<LlenaComboGralDto> llenarCmbPais() {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n SELECT id_pais as ID, desc_pais as Descrip ");
			sbSQL.append("\n  FROM  cat_pais");
			sbSQL.append("\n  ORDER BY desc_pais");
			logger.info("llenarCmbPais "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto compon = new LlenaComboGralDto();
					compon.setIdStr(rs.getString("ID"));
					compon.setDescripcion(rs.getString("Descrip"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:llenarCmbPais");
		}
		return list;
	}
	public List<LlenaComboGralDto> llenarCmbBancos(String psNacionalidad, String psDivisa,int noEmpresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n SELECT  DISTINCT cb.id_banco as ID, cb.desc_banco as Descrip ");
			sbSQL.append("\n  FROM    cat_banco cb, cat_cta_banco ccb, usuario_empresa ue ");
			sbSQL.append("\n  WHERE   ccb.id_banco = cb.id_banco ");
			sbSQL.append("\n   And ccb.no_empresa = ue.no_empresa ");
			sbSQL.append("\n  And ue.no_empresa = " + noEmpresa);
			if (!psNacionalidad.equals("") && !psNacionalidad.equals(null)) {
				sbSQL.append("\n  AND cb.nac_ext = '" + psNacionalidad + "'");
			}
			if (!psDivisa.equals("") && !psDivisa.equals(null)) {
				sbSQL.append("\n  And ccb.id_divisa = '" + psDivisa + "'");
			}
			logger.info("llenarCmbBancos "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto compon = new LlenaComboGralDto();
					compon.setIdStr(rs.getString("ID"));
					compon.setDescripcion(rs.getString("Descrip"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:llenarCmbBancos");
		}
		return list;
	}
	public List<LlenaComboGralDto> llenarCmbDivisas(boolean bRestringido) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n SELECT id_divisa as ID, desc_divisa as Descrip ");
			sbSQL.append("\n  FROM cat_divisa ");
			sbSQL.append("\n  WHERE clasificacion = 'D' ");
			if (bRestringido)
				sbSQL.append("\n  and id_divisa in( 'MN','DLS')");
			
			logger.info(""+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto compon = new LlenaComboGralDto();
					compon.setIdStr(rs.getString("ID"));
					compon.setDescripcion(rs.getString("Descrip"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:");
		}
		return list;
	}
	public List<LlenaComboGralDto> llenarCmbTasa() {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n SELECT  id_tasa as ID, desc_tasa as Descrip ");
			sbSQL.append("\n  FROM    cat_tasas ");
			sbSQL.append("\n  WHERE   NOT desc_tasa LIKE '%CIE%' ");
			logger.info("llenarCmbTasa"+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto compon = new LlenaComboGralDto();
					compon.setIdStr(rs.getString("ID"));
					compon.setDescripcion(rs.getString("Descrip"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:llenarCmbDivisas");
		}
		return list;
	}
	public List<LlenaComboGralDto> llenarCmbArrendadoras() {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n SELECT  DISTINCT id_arrendadora as ID, desc_arrendadora as Descrip  ");
			sbSQL.append("\n  FROM    cat_arrendadoras ");
			sbSQL.append("\n  UNION ALL ");
			sbSQL.append("\n  SELECT  DISTINCT cb.id_banco as ID, cb.desc_banco as Descrip ");
			sbSQL.append("\n  FROM  cat_banco cb ");
			sbSQL.append("\n  ORDER BY Descrip");
			logger.info("llenarCmbArrendadoras"+sbSQL);
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
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:llenarCmbArrendadoras");
		}
		return list;
	}
	public List<ContratoCreditoDto> llenarDatosContratoCredito(String psIdContrato) {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		try {
			sbSQL.append(
					"\n SELECT ccc.id_financiamiento, ccc.id_pais, ccc.id_banco, ccc.id_clabe, ccc.id_tipo_financiamiento,  ");
			sbSQL.append(
					"\n  	ccc.id_representante, ccc.fec_inicio, ccc.fec_vencimiento, ccc.id_divisa, ccc.monto_autorizado, ");
			sbSQL.append("\n  	coalesce((case when cdc.id_divisa = 'MN' then coalesce(cdc.monto_disposicion, 0) ");
			sbSQL.append(
					"\n  	when vd.valor >=1 And cdc.id_divisa <> ccc.id_divisa then vd.valor * coalesce(cdc.monto_disposicion, 0) ");
			sbSQL.append(
					"\n  	when vd.valor <1 And cdc.id_divisa <> ccc.id_divisa then vd.valor / coalesce(cdc.monto_disposicion, 0) ");
			sbSQL.append("\n  	when cdc.id_divisa = ccc.id_divisa then  coalesce(cdc.monto_disposicion, 0)");
			sbSQL.append(
					"\n  	end), 0) as monto_disposicion, ccc.tasa_linea, ccc.spreed, ccc.revolvencia, ccc.tipo_operacion,");
			sbSQL.append("\n  	ccc.id_banco_prestamo,");
			sbSQL.append("\n  	case when ccc.fecha_antpost ='E' or ccc.fecha_antpost ='E' then 'E'");
			sbSQL.append("\n  		when ccc.fecha_antpost ='A' or ccc.fecha_antpost ='A' then 'A'");
			sbSQL.append("\n  		when ccc.fecha_antpost='' or CCC.FECHA_ANTPOST='P' then 'P'");
			sbSQL.append("\n  		when ccc.fecha_antpost='P' or CCC.FECHA_ANTPOST='P' then 'P'");
			sbSQL.append("\n  		end as fecha_antpost, coalesce(ccc.largo_plazo, '') as largo_plazo,");
			sbSQL.append(
					"\n  	case when ccc.recfecha_antpost='' or CCC.RECFECHA_ANTPOST='P' then 'P' else 'A' end as recfecha_antpost,");
			sbSQL.append(
					"\n  		ccc.id_banco_fideicomiso, ccc.id_banco_agente, coalesce(cdc.id_banco_disp, 0) as id_banco_disp, coalesce(cdc.id_chequera_disp, '') as id_chequera_disp,");
			sbSQL.append(
					"\n  		coalesce(case when ccc.revolvencia = 'S' then (SELECT coalesce(sum(cac.capital), 0) FROM cat_amortizacion_credito cac ");
			sbSQL.append("\n  		WHERE  ccc.id_financiamiento = cdc.id_financiamiento ");
			sbSQL.append("\n   		And cdc.id_financiamiento = cac.id_contrato ");
			sbSQL.append("\n  		And cdc.id_disposicion = cac.id_disposicion");
			sbSQL.append("\n  		And cdc.estatus = 'A' ");
			sbSQL.append("\n  		And cac.estatus = 'P'  ");
			sbSQL.append("\n  		And ccc.id_financiamiento = '" + psIdContrato
					+ "') end, 0) as capital, ccc.reestructura ");
			sbSQL.append(
					"\n  FROM cat_contrato_credito ccc left join cat_disposicion_credito cdc on (ccc.id_financiamiento = cdc.id_financiamiento And cdc.estatus = 'A') ");
			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append(
						"\n  left join valor_divisa vd on (cdc.id_divisa = vd.id_divisa and cast(vd.fec_divisa as date) = '"
								+ fechaHoy + "')   ");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append(
						"\n  left join valor_divisa vd on (cdc.id_divisa = vd.id_divisa and cast(vd.fec_divisa as date) = '" + fechaHoy +  "') ");
			sbSQL.append("\n  WHERE ccc.id_financiamiento = '" + psIdContrato + "'  ");
			logger.info("llenarDatosContratoCredito "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public ContratoCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					ContratoCreditoDto compon = new ContratoCreditoDto();
					compon.setIdFinanciamiento(rs.getString("id_financiamiento"));
					compon.setIdPais(rs.getString("id_pais"));
					compon.setIdBanco(rs.getInt("id_banco"));
					compon.setIdClabe(rs.getString("id_clabe"));
					compon.setMontoDisposicion(rs.getDouble("monto_disposicion"));
					compon.setCapital(rs.getDouble("capital"));
					compon.setRevolvencia(rs.getString("revolvencia").charAt(0));
					compon.setIdBancoPrestamo(rs.getInt("id_banco_prestamo"));
					compon.setIdtipoFinanciamiento(rs.getInt("id_tipo_financiamiento"));
					compon.setIdRepresentante(rs.getInt("id_representante"));
					compon.setFecInicio(rs.getString("fec_inicio"));
					compon.setFecVencimiento(rs.getString("fec_vencimiento"));
					compon.setIdDivisa(rs.getString("id_divisa"));
					compon.setMontoAutorizado(rs.getDouble("monto_autorizado"));
					compon.setTasaLinea(rs.getString("tasa_linea"));
					compon.setSpreed(rs.getDouble("spreed"));
					compon.setReestructura(rs.getString("reestructura").charAt(0));
					compon.setLargoPlazo(rs.getString("largo_plazo").charAt(0));
					compon.setFechaAntpost(rs.getString("fecha_antpost").charAt(0));
					compon.setRecfechaAntpost(rs.getString("recfecha_antpost").charAt(0));
					compon.setIdBancoFideicomiso(rs.getInt("id_banco_fideicomiso"));
					compon.setAgente(rs.getInt("id_banco_agente"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:llenarDatosContratoCredito");
		}
		return list;
	}
	@Override
	public List<ContratoCreditoDto> obtenerTipoCambio(String psDivisa) {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		try {
			sbSQL.append("\n 	SELECT id_divisa, fec_divisa, coalesce(valor, 0) as valor   ");
			sbSQL.append("\n  	FROM  valor_divisa ");
			sbSQL.append("\n  	WHERE id_divisa = '" + psDivisa + "' ");
			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append("and cast(fec_divisa as date) = '" + fechaHoy + "'");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("       and cast(fec_divisa as date) = '" + fechaHoy + "' ");
			logger.info("obtenerTipoCambio "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public ContratoCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					ContratoCreditoDto compon = new ContratoCreditoDto();
					compon.setIdDivisa(rs.getString("id_divisa"));
					compon.setFecDivisa(rs.getString("fec_divisa"));
					compon.setValor(rs.getDouble("valor"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:obtenerTipoCambio");
		}
		return list;
	}

	public List<LlenaComboGralDto> llenarCmbDisposiciones(String psIdContrato, boolean pbEstatus) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n SELECT id_disposicion as ID, id_disposicion as Descrip ");
			sbSQL.append("\n  FROM  cat_disposicion_credito ccc");
			if (pbEstatus) {
				sbSQL.append("\n WHERE id_financiamiento in ('" + psIdContrato + "')");
				sbSQL.append("\n And ccc.estatus = 'A'");
			} else {
				sbSQL.append("\n WHERE id_financiamiento in ('" + psIdContrato + "') ");
				sbSQL.append("\n And ccc.estatus = 'A'");
			}
			logger.info("llenarCmbDisposiciones "+sbSQL);
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
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:llenarCmbDisposiciones");
		}
		return list;
	}

	@Override
	public List<ContratoCreditoDto> selectPrefijo(int piBanco) {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n 	SELECT distinct prefijo_banco  ");
			sbSQL.append("\n  	FROM   cat_prefijos_bancos ");
			sbSQL.append("\n  	 WHERE id_banco = " + piBanco + "");
			logger.info("selectPrefijo "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public ContratoCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					ContratoCreditoDto compon = new ContratoCreditoDto();
					compon.setPrefijoBanco(rs.getString("prefijo_banco"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:selectPrefijo");
		}
		return list;
	}
	@Override
	public List<ContratoCreditoDto> selectConsecutivoLinea() {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			if(gsDBM.equals("SQL SERVER")){
				sbSQL.append("\n 	SELECT coalesce(max(substring(id_financiamiento, 5,5)), '') AS id_financiamiento   ");
				sbSQL.append("\n  	FROM  cat_contrato_credito ");
			}
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE")){
				sbSQL.append("\n SELECT coalesce(max(substring(id_financiamiento from 5 for 4)), '') AS id_financiamiento ");
				sbSQL.append("\n FROM  cat_contrato_credito limit 1 ");
			}
			logger.info("selectConsecutivoLinea "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public ContratoCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					ContratoCreditoDto compon = new ContratoCreditoDto();
					compon.setIdFinanciamiento(rs.getString("id_financiamiento"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:selectConsecutivoLinea");
		}
		return list;
	}
	@Override
	public List<ContratoCreditoDto> selectInhabil(String pvFechaInhabil) {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n 	SELECT FEC_INHABIL   ");
			sbSQL.append("\n  	FROM DIA_INHABIL ");
			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append("\n  	WHERE cast(FEC_INHABIL as datetime) = '" + pvFechaInhabil + "' ");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE")){
				sbSQL.append("\nWHERE cast(FEC_INHABIL as date) = '");
				sbSQL.append("\n "+pvFechaInhabil+"'");}
			logger.info("selectInhabil "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public ContratoCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					ContratoCreditoDto compon = new ContratoCreditoDto();
					compon.setFecInhabil(rs.getString("FEC_INHABIL"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:selectInhabil");
		}
		return list;
	}
	@Override
	public List<ContratoCreditoDto> selectContratoCredito(String psIdContrato) {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		try {
			sbSQL.append(
					"\n SELECT ccc.id_financiamiento, ccc.id_pais, ccc.id_banco, ccc.id_clabe, ccc.id_tipo_financiamiento,   ");
			sbSQL.append(
					"\n        ccc.id_representante, ccc.fec_inicio, ccc.fec_vencimiento, ccc.id_divisa, ccc.monto_autorizado,  ");
			sbSQL.append("\n        coalesce((case when cdc.id_divisa = 'MN' then coalesce(cdc.monto_disposicion, 0) ");
			sbSQL.append(
					"\n              when vd.valor >=1 And cdc.id_divisa <> ccc.id_divisa then vd.valor * coalesce(cdc.monto_disposicion, 0) ");
			sbSQL.append(
					"\n               when vd.valor >=1 And cdc.id_divisa <> ccc.id_divisa then vd.valor * coalesce(cdc.monto_disposicion, 0)");
			sbSQL.append(
					"\n              when vd.valor <1 And cdc.id_divisa <> ccc.id_divisa then vd.valor / coalesce(cdc.monto_disposicion, 0)");
			sbSQL.append("\n              when cdc.id_divisa = ccc.id_divisa then  coalesce(cdc.monto_disposicion, 0)");
			sbSQL.append(
					"\n         end), 0) as monto_disposicion, ccc.tasa_linea, ccc.spreed, ccc.revolvencia, ccc.tipo_operacion,");
			sbSQL.append("\n        ccc.id_banco_prestamo,");
			sbSQL.append("\n        case when ccc.fecha_antpost ='E' or ccc.fecha_antpost ='E' then 'E'");
			sbSQL.append("\n             when ccc.fecha_antpost ='A' or ccc.fecha_antpost ='A' then 'A'");
			sbSQL.append("\n             when ccc.fecha_antpost='' or CCC.FECHA_ANTPOST='P' then 'P'");
			sbSQL.append("\n             when ccc.fecha_antpost='P' or CCC.FECHA_ANTPOST='P' then 'P'");
			sbSQL.append("\n        end as fecha_antpost, coalesce(ccc.largo_plazo, '') as largo_plazo,");
			sbSQL.append(
					"\n            case when ccc.recfecha_antpost='' or CCC.RECFECHA_ANTPOST='P' then 'P' else 'A' end as recfecha_antpost,");
			sbSQL.append(
					"\n        ccc.id_banco_fideicomiso, ccc.id_banco_agente, coalesce(cdc.id_banco_disp, 0) as id_banco_disp, coalesce(cdc.id_chequera_disp, '') as id_chequera_disp,");
			sbSQL.append(
					"\n        coalesce(case when ccc.revolvencia = 'S' then (SELECT coalesce(sum(cac.capital), 0) FROM cat_amortizacion_credito cac  ");
			sbSQL.append("\n        WHERE  ccc.id_financiamiento = cdc.id_financiamiento");
			sbSQL.append("\n           And cdc.id_financiamiento = cac.id_contrato");
			sbSQL.append("\n           And cdc.id_disposicion = cac.id_disposicion");
			sbSQL.append("\n           And cdc.estatus = 'A'");
			sbSQL.append("\n           And cac.estatus = 'P'");
			sbSQL.append("\n           And ccc.id_financiamiento = '" + psIdContrato
					+ "') end, 0) as capital, ccc.reestructura");
			sbSQL.append(
					"\n FROM cat_contrato_credito ccc left join cat_disposicion_credito cdc on (ccc.id_financiamiento = cdc.id_financiamiento And cdc.estatus = 'A')");
			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append(
						"\n left join valor_divisa vd on (cdc.id_divisa = vd.id_divisa and cast(vd.fec_divisa as date) = '"
								+ fechaHoy + "')");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append(
						"\n                        left join valor_divisa vd on (cdc.id_divisa = vd.id_divisa and cast(vd.fec_divisa as date) = '" + fechaHoy +"') ");
			sbSQL.append("\n  WHERE ccc.id_financiamiento = '" + psIdContrato + "' ");

			logger.info("selectContratoCredito "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public ContratoCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					ContratoCreditoDto compon = new ContratoCreditoDto();
					compon.setIdFinanciamiento(rs.getString("id_financiamiento"));
					compon.setIdPais(rs.getString("id_pais"));
					compon.setMontoDisposicion(rs.getDouble("monto_disposicion"));
					compon.setCapital(rs.getDouble("capital"));
					compon.setRevolvencia(rs.getString("revolvencia").charAt(0));
					compon.setIdBancoPrestamo(rs.getInt("id_banco_prestamo"));
					compon.setIdtipoFinanciamiento(rs.getInt("id_tipo_financiamiento"));
					compon.setIdRepresentante(rs.getInt("id_representante"));
					compon.setFecInicio(rs.getString("fec_inicio"));
					compon.setFecVencimiento(rs.getString("fec_vencimiento"));
					compon.setIdDivisa(rs.getString("id_divisa"));
					compon.setMontoAutorizado(rs.getDouble("monto_autorizado"));
					compon.setTasaLinea(rs.getString("tasa_linea"));
					compon.setSpreed(rs.getDouble("spreed"));
					compon.setReestructura(rs.getString("reestructura").charAt(0));
					compon.setLargoPlazo(rs.getString("largo_plazo").charAt(0));
					compon.setFechaAntpost(rs.getString("fecha_antpost").charAt(0));
					compon.setRecfechaAntpost(rs.getString("recfecha_antpost").charAt(0));
					compon.setIdBancoFideicomiso(rs.getInt("id_banco_fideicomiso"));
					compon.setAgente(rs.getInt("id_banco_agente"));
					compon.setIdClabe(rs.getString("id_chequera_disp"));
					compon.setIdBanco(rs.getInt("id_banco_disp"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:selectContratoCredito");
		}
		return list;
	}
	@Override
	public int insertAltaContrato(ContratoCreditoDto dto,int noEmpresa) {
		try {
			StringBuffer sbSQL = new StringBuffer();
			globalSingleton = GlobalSingleton.getInstancia();

			sbSQL.append(
					"\n INSERT INTO cat_contrato_credito (id_financiamiento, id_pais, no_empresa, id_banco, id_clabe, ");
			sbSQL.append(
					"\n                       id_tipo_financiamiento, id_representante, fec_inicio, fec_vencimiento, ");
			sbSQL.append("\n                       id_divisa, monto_autorizado, tasa_linea, tipo_operacion, spreed, ");
			sbSQL.append(
					"\n                       revolvencia, estatus, usuario_alta, id_banco_prestamo, fecha_antpost, ");
			sbSQL.append(
					"\n                       largo_plazo, recfecha_antpost, id_banco_fideicomiso, id_banco_agente, reestructura) ");
			sbSQL.append("\n VALUES ( '" + dto.getIdFinanciamiento() + "', '" + dto.getIdPais() + "', "
					+ noEmpresa + ", ");
			sbSQL.append("\n    " + dto.getIdBanco() + ", '" + dto.getNoCuenta() + "', " + dto.getIdtipoFinanciamiento()
			+ ", " + dto.getIdComun() + ", ");
			sbSQL.append("\n   '" +ponerFormatoFecha(dto.getFecInicio()) + "', '" + ponerFormatoFecha(dto.getFecVencimiento()) + "', '" + dto.getIdDivisa()
			+ "', " + dto.getMontoAutorizado() + ", ");
			sbSQL.append("\n   '" + dto.getTasaLinea() + "', '" + dto.getTipoOperacion() + "', " + dto.getSpreed()
			+ ", '" + dto.getRevolvencia() + "', ");
			sbSQL.append("\n   '" + dto.getEstatus() + "', " + globalSingleton.getUsuarioLoginDto().getIdUsuario()
					+ ", " + dto.getIdBancoPrestamo() + ", '" + dto.getFechaAntpost() + "', ");
			sbSQL.append("\n   '" + dto.getLargoPlazo() + "', '" + dto.getRecfechaAntpost() + "', "
					+ dto.getIdBancoFideicomiso() + ", " + dto.getAgente() + ", '" + dto.getReestructura() + "') ");

			logger.info("insertAltaContrato "+sbSQL);
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:insertAltaContrato");
			return 0;
		}
	}
	@Override
	public List<ContratoCreditoDto> selectExisteDispAmort(String psLinea) {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append(
					"\n SELECT * FROM cat_disposicion_credito cdc left join cat_amortizacion_credito cac on (cdc.id_financiamiento = cac.id_contrato And cac.estatus = 'A')");
			sbSQL.append("\n WHERE   cdc.estatus = 'A' ");
			sbSQL.append("\n     And cdc.id_financiamiento = '" + psLinea + "' ");
			logger.info("selectExisteDispAmort "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public ContratoCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					ContratoCreditoDto compon = new ContratoCreditoDto();
					compon.setIdFinanciamiento(rs.getString("id_financiamiento"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:selectExisteDispAmort");
		}
		return list;
	}
	@Override
	public int deleteDispAmortizacion(String psFinanciamiento) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		try {
			StringBuffer sbSQL = new StringBuffer();
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n UPDATE cat_contrato_credito SET estatus = 'X', usuario_modif = "
					+ globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", fec_modif = '" + fechaHoy + "' ");
			sbSQL.append("\n WHERE id_financiamiento = '" + psFinanciamiento + "' ");
			sbSQL.append("\n       AND estatus = 'A' ");
			sbSQL.append("\n       ; ");
			sbSQL.append("\n UPDATE cat_disposicion_credito SET estatus = 'X', usuario_modif = "
					+ globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", fec_modif = '" + fechaHoy + "' ");
			sbSQL.append("\n WHERE id_financiamiento = '" + psFinanciamiento + "' ");
			sbSQL.append("\n       AND estatus = 'A' ");
			sbSQL.append("\n       ; ");
			sbSQL.append("\n UPDATE cat_amortizacion_credito SET estatus = 'X', usuario_modif = "
					+ globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", fec_modif = '" + fechaHoy + "' ");
			sbSQL.append("\n WHERE id_contrato = '" + psFinanciamiento + "' ");
			sbSQL.append("\n       AND estatus = 'A' ");
			sbSQL.append("\n       ; ");
			sbSQL.append("\n UPDATE aval_garantia_asig_lin SET estatus = 'X', usuario_modif = "
					+ globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", fec_modif = '" + fechaHoy + "' ");
			sbSQL.append("\n WHERE id_financiamiento = '" + psFinanciamiento + "' ");
			sbSQL.append("\n       And estatus = 'A' ");
			sbSQL.append("\n       ; ");
			sbSQL.append("\n UPDATE fact_facturas SET estatus = 'X', usuario_modif = "
					+ globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", fec_modif = '" + fechaHoy + "' ");
			sbSQL.append("\n WHERE id_financiamiento = '" + psFinanciamiento + "' ");
			sbSQL.append("\n       And estatus = 'A' ");
			logger.info("deleteDispAmortizacion "+sbSQL);
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:deleteDispAmortizacion");
			return 0;
		}
	}

	@Override
	public List<DisposicionCreditoDto> selectDisposicionCred(String psFinanciamiento, int piDisposicion) {
		List<DisposicionCreditoDto> list = new ArrayList<DisposicionCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n SELECT * FROM cat_disposicion_credito ");
			sbSQL.append("\n WHERE   id_financiamiento = '" + psFinanciamiento + "' ");
			sbSQL.append("\n     And estatus = 'A' ");
			if (piDisposicion != 0)
				sbSQL.append("\n     And id_disposicion = " + piDisposicion + " ");
			logger.info("selectDisposicionCred "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public DisposicionCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					DisposicionCreditoDto dto = new DisposicionCreditoDto();
					dto.setIdFinanciamiento(rs.getString("id_financiamiento"));
					dto.setMontoDisposicion(rs.getDouble("monto_disposicion"));
					dto.setFecDisposicion(rs.getString("fec_disposicion"));
					dto.setFecVencimiento(rs.getString("fec_vencimiento"));
					dto.setTipoTasa(rs.getString("tipo_tasa").charAt(0));
					dto.setValorTasa(rs.getDouble("valor_tasa"));
					return dto;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:selectDisposicionCred");
		}
		return list;
	}

	@Override
	public List<ContratoCreditoDto> selectAltaAmortizaciones(String psIdContrato) {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n SELECT * FROM cat_amortizacion_credito ");
			sbSQL.append("\n WHERE id_contrato = '" + psIdContrato + "' ");
			sbSQL.append("\n ORDER BY id_amortizacion asc ");
			logger.info("selectAltaAmortizaciones "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public ContratoCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					ContratoCreditoDto compon = new ContratoCreditoDto();
					compon.setIdContrato(rs.getString("id_contrato"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:");
		}
		return list;
	}

	@Override
	public int updateLinea(ContratoCreditoDto dto) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		try {
			StringBuffer sbSQL = new StringBuffer();
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n UPDATE cat_contrato_credito SET ");
			sbSQL.append("\n       id_financiamiento = '" + dto.getIdFinanciamiento() + "', id_pais = '"
					+ dto.getIdPais() + "', ");
			sbSQL.append("\n      id_banco = " + dto.getIdBanco() + ", id_clabe = '" + dto.getNoCuenta()
			+ "', id_tipo_financiamiento = " + dto.getIdtipoFinanciamiento() + ", ");
			sbSQL.append("\n       id_representante = " + dto.getIdComun() + ", fec_inicio = '" + ponerFormatoFecha(dto.getFecInicio())
			+ "', fec_vencimiento = '" +ponerFormatoFecha(dto.getFecVencimiento())+ "', ");
			sbSQL.append("\n      id_divisa = '" + dto.getIdDivisa() + "', monto_autorizado = "
					+ dto.getMontoAutorizado() + ", tasa_linea = '" + dto.getTasaLinea() + "', ");
			sbSQL.append("\n      tipo_operacion = '" + dto.getTipoOperacion() + "', spreed = " + dto.getSpreed()
			+ ", revolvencia = '" + dto.getRevolvencia() + "', ");
			sbSQL.append("\n       id_banco_prestamo = " + dto.getIdBancoPrestamo() + ", fecha_antpost = '"
					+ dto.getFechaAntpost() + "', largo_plazo = '" + dto.getLargoPlazo() + "', ");
			sbSQL.append("\n       id_banco_fideicomiso = " + dto.getIdBancoFideicomiso() + ", id_banco_agente = "
					+ dto.getAgente() + ", recfecha_antpost = '" + dto.getRecfechaAntpost() + "', ");
			sbSQL.append("\n      reestructura = '" + dto.getReestructura() + "', usuario_modif = "
					+ globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", fec_modif = '" + fechaHoy + "' ");
			sbSQL.append("\n  WHERE id_financiamiento = '" + dto.getIdFinanciamiento() + "' ");
			logger.info("updateLinea "+sbSQL);
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:updateLinea");
			return 0;
		}
	}

	@Override
	public List<LlenaComboGralDto> obtenerEquivalencia(String psDesBanco, int piBanco) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n  SELECT  DISTINCT p.equivale_persona as ID, p.razon_social as Descrip ");
			sbSQL.append("\n FROM    cat_equivalencia ce, persona p ");
			sbSQL.append("\n WHERE ce.equivale_persona = p.equivale_persona ");
			sbSQL.append("\n And ce.id_arrendadora = " + piBanco + " ");
			sbSQL.append("\n And id_tipo_persona = 'P' ");
			logger.info("obtenerEquivalencia "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto compon = new LlenaComboGralDto();
					String cad = rs.getString("ID");
					compon.setIdStr(cad.trim());
					compon.setDescripcion(rs.getString("Descrip"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:obtenerEquivalencia");
		}

		return list;
	}

	@Override
	public List<ContratoCreditoDto> funSQLTasa(String psTasa) {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		try {
			sbSQL.append("\n  SELECT * FROM tasa ");
			sbSQL.append("\n  WHERE id_tasa = '" + psTasa + "' ");
			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append("\n   AND cast(fecha as date) = '" + fechaHoy + "' ");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\n    AND cast(fecha as date) = '" + fechaHoy + "' ");
			System.out.println(sbSQL.toString());
			logger.info("funSQLTasa "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public ContratoCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					ContratoCreditoDto compon = new ContratoCreditoDto();
					compon.setValor(rs.getDouble("valor"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:funSQLTasa");
		}
		return list;
	}

	@Override
	public List<LlenaComboGralDto> funSQLComboClabe(int pvvValor2, String psDivisa,int noEmpresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		globalSingleton = GlobalSingleton.getInstancia();
		try {
			sbSQL.append("\n SELECT id_chequera as ID, id_chequera as Descrip ");
			sbSQL.append("\n FROM  cat_cta_banco ");
			sbSQL.append("\n WHERE tipo_chequera in ('M') ");
			sbSQL.append("\n       AND no_empresa = " + noEmpresa + " ");
			sbSQL.append("\n       AND id_banco = " + pvvValor2 + " ");
			if (!psDivisa.equals(""))
				sbSQL.append("\n  And id_divisa = '" + psDivisa + "' ");
			logger.info("funSQLComboClabe "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto compon = new LlenaComboGralDto();
					compon.setIdStr(rs.getString("ID"));
					compon.setDescripcion(rs.getString("Descrip"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:funSQLComboClabe");
		}
		return list;
	}

	@Override
	public List<ContratoCreditoDto> selectBancoNacionalidad(int piBanco) {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n  SELECT coalesce(nac_ext, '') as nac_ext ");
			sbSQL.append("\n  FROM  cat_banco ");
			sbSQL.append("\n WHERE id_banco = " + piBanco + " ");
			logger.info("selectBancoNacionalidad "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public ContratoCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					ContratoCreditoDto compon = new ContratoCreditoDto();
					compon.setNacExt(rs.getString("nac_ext"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:selectBancoNacionalidad");
		}
		return list;
	}

	@Override
	public List<ContratoCreditoDto> selectNoDisp(String psLinea) {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n  SELECT id_disposicion, coalesce(sum(monto_disposicion), 0) as monto_disposicion ");
			sbSQL.append("\n  FROM cat_disposicion_credito ");
			sbSQL.append("\n WHERE id_financiamiento = '" + psLinea + "' ");
			sbSQL.append("\n GROUP BY id_disposicion ");
			sbSQL.append("\n ORDER BY id_disposicion ASC ");
			logger.info("selectNoDisp "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public ContratoCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					ContratoCreditoDto compon = new ContratoCreditoDto();
					compon.setIdDisposicion(rs.getInt("id_disposicion"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:selectNoDisp");
		}
		return list;
	}

	@Override
	public int updateLineaBancoCheq(ContratoCreditoDto dto) {
		try {
			StringBuffer sbSQL = new StringBuffer();
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n UPDATE cat_contrato_credito ");
			sbSQL.append("\n  SET id_banco = " + dto.getIdBanco() + ", id_clabe = '" + dto.getIdClabe() + "' ");
			sbSQL.append("\n WHERE id_financiamiento = '" + dto.getIdFinanciamiento() + "' ");
			logger.info("updateLineaBancoCheq "+sbSQL);
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:updateLineaBancoCheq");
			return 0;
		}
	}

	@Override
	public int insertDisposicion(DisposicionCreditoDto dto) {
		try {
			StringBuffer sbSQL = new StringBuffer();
			globalSingleton = GlobalSingleton.getInstancia();
			List<ContratoCreditoDto> dtoC = new ArrayList<ContratoCreditoDto>();
			dtoC = selectNoDisp(dto.getIdFinanciamiento());
			int plConsDisp, piDisp;
			if (dtoC.isEmpty())
				plConsDisp = 1;
			else
				plConsDisp = dtoC.get(dtoC.size() - 1).getIdDisposicion() + 1;
			piDisp = plConsDisp;
			sbSQL.append(
					"\n INSERT INTO cat_disposicion_credito (id_financiamiento, id_disposicion, emision, id_divisa, monto_disposicion, ");
			sbSQL.append(
					"\n       fec_disposicion, fec_vencimiento, sobre_tasa, aforo_porciento, aforo_importe, largo_plazo, calificadora_sp, ");
			sbSQL.append(
					"\n       calificadora_moody, calificadora_fitch, tipo_tasa, tasa_base, valor_tasa, puntos, tasa_ponderada, monto_postura, ");
			sbSQL.append(
					"\n       sobre_venta, sobre_tasacb, forma_pago, estatus, usuario_alta, id_banco_disp, id_chequera_disp, ");
			sbSQL.append(
					"\n       id_destino_recursos, equivalente, renta, id_disposicion_ref, anexo, comentarios, periodo_renta, wh_tax, ");
			sbSQL.append("\n       renta_dep, comision_apertura, monto_moratorio, tasa_moratoria, valor_facturas)");

			sbSQL.append(
					"\n VALUES ('" + dto.getIdFinanciamiento() + "', " + plConsDisp + ", '" + dto.getEmision() + "', ");
			sbSQL.append("\n       '" + dto.getIdDivisa() + "', " + dto.getMontoDisposicion() + ", '"
					+ ponerFormatoFecha(dto.getFecDisposicion()) + "', ");
			sbSQL.append("\n       '" + ponerFormatoFecha(dto.getFecVencimiento()) + "', " + dto.getSobreTasa() + ", "
					+ dto.getAforoPorciento() + ", ");
			sbSQL.append("\n        " + dto.getAforoImporte() + ", '" + dto.getLargoPlazo() + "', '"
					+ dto.getCalificadoraSp() + "', ");
			sbSQL.append("\n       '" + dto.getCalificadoraMoody() + "', '" + dto.getCalificadoraFitch() + "', '"
					+ dto.getTipoTasa() + "', ");
			sbSQL.append("\n      '" + dto.getTasaBase() + "', " + dto.getValorTasa() + ", " + dto.getPuntos() + ", ");
			sbSQL.append("\n       " + dto.getTasaPonderada() + ", " + dto.getMontoPostura() + ", "
					+ dto.getSobreVenta() + ", ");
			sbSQL.append("\n        " + dto.getSobreTasacb() + ", '" + dto.getFormaPago() + "', '" + dto.getEstatus()
			+ "', ");
			sbSQL.append("\n        " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", "
					+ dto.getIdBancoDisp() + ", '" + dto.getIdChequeraDisp() + "', ");
			sbSQL.append("\n       " + dto.getIdTipoFinanciamiento() + ", '" + dto.getEquivalente() + "', "
					+ dto.getRenta() + ", ");
			sbSQL.append("\n       '" + dto.getIdDisposicionRef() + "', '" + dto.getAnexo() + "', '"
					+ dto.getComentarios() + "', ");
			sbSQL.append("\n        " + dto.getPeriodoRenta() + ", " + dto.getWhTax() + ", " + dto.getRentaDep() + ", "
					+ dto.getComisionApertura() + ", ");
			sbSQL.append("\n        " + dto.getMontoMoratorio() + ", " + dto.getTasaMoratoria() + ", "
					+ dto.getValorFacturas() + ") ");
			logger.info("insertDisposicion "+sbSQL);
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:insertDisposicion");
			return 0;
		}
	}

	@Override
	public int updateAmortizacionReestructurada(String psLinea, int piDisposicion) {
		try {
			StringBuffer sbSQL = new StringBuffer();
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n UPDATE cat_amortizacion_credito SET estatus = 'R' ");
			sbSQL.append("\n WHERE id_contrato = '" + psLinea + "' ");
			sbSQL.append("\n      And id_disposicion = " + piDisposicion + " ");
			sbSQL.append("\n       And estatus = 'A' ");
			logger.info("updateAmortizacionReestructurada "+sbSQL);
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:updateAmortizacionReestructurada");
			return 0;
		}
	}

	@Override
	public List<ContratoCreditoDto> selectDivision(int piBanco, String psChequera) {
		List<ContratoCreditoDto> list = new ArrayList<ContratoCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n SELECT coalesce(id_division, '') as id_division FROM cat_cta_banco ");
			sbSQL.append("\n WHERE id_banco = " + piBanco + " ");
			sbSQL.append("\n   And id_chequera = '" + psChequera + "' ");
			logger.info("selectDivision "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public ContratoCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					ContratoCreditoDto compon = new ContratoCreditoDto();
					compon.setIdDivision(rs.getString("id_division").charAt(0));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:selectDivision");
		}
		return list;
	}
	@Override
	public int seleccionarFolioReal(String tipoFolio) {
		int r = 0;
		int f = 0;
		try {
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			f = consultasGenerales.seleccionarFolioReal(tipoFolio);
			if (f > 0) {
				r = f;
				consultasGenerales.actualizarFolioReal(tipoFolio);
			}
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:seleccionarFolioReal");
			e.printStackTrace();
		}
		return r;
	}
	@Override
	public int insertParametro(Parametro dto,int noEmpresa) {
		try {
			StringBuffer sbSQL = new StringBuffer();
			globalSingleton = GlobalSingleton.getInstancia();
			List<ContratoCreditoDto> dtoC = new ArrayList<ContratoCreditoDto>();
			String pvvNoCliente = dto.getNoCliente();
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			Date fechaHoy = consultasGenerales.obtenerFechaHoy();
			sbSQL.append("\n  INSERT INTO parametro(no_empresa, no_folio_param, id_tipo_docto, id_forma_pago, ");
			sbSQL.append("\n       id_tipo_operacion, usuario_alta, no_cuenta, fec_valor, fec_valor_original, ");
			sbSQL.append("\n       fec_operacion, fec_alta, importe, importe_original, id_caja, id_divisa, ");
			sbSQL.append("\n       id_divisa_original, origen_reg, referencia, concepto, aplica, id_estatus_mov, ");
			sbSQL.append("\n       b_salvo_buen_cobro, id_estatus_reg, id_banco, id_chequera, folio_banco, ");
			sbSQL.append("\n       origen_mov, observacion, id_inv_cbolsa, no_folio_mov, folio_ref, grupo, ");
			sbSQL.append("\n      importe_desglosado, lote, hora_recibo, division, id_rubro, no_recibo, ");
			sbSQL.append("\n         no_docto, beneficiario ");// ', no_factura
			// ");
			if (!dto.getIdCliente().equals("") || !dto.getNoCliente().equals(""))
				sbSQL.append("\n     , no_cliente ");
			if (dto.getIdBancoBenef() != 0 && !dto.getIdChequeraBenef().equals(""))
				sbSQL.append("\n     , id_banco_benef, id_chequera_benef ");
			if (!dto.getFecPropuesta().equals(""))
				sbSQL.append("\n     , fec_propuesta ");
			sbSQL.append("\n  ) ");
			sbSQL.append("\n   VALUES(" + noEmpresa + ","
					+ dto.getNoFolioParam() + "," + dto.getIdTipoDocto() + ", " + dto.getIdFormaPago() + " ");
			sbSQL.append("\n           , " + dto.getIdTipoOperacion() + ", "
					+ globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", " + dto.getNoCuenta()
					+ ",  convert(date,'" + dto.getFecValor() + "') ");
			sbSQL.append("\n           , convert(date,'" +ponerFormatoFecha(dto.getFecValorOriginal()) + "'),  convert(date,'" + fechaHoy
					+ "'),  convert(date,'" + fechaHoy + "') ");
			sbSQL.append("\n          , " + dto.getImporte() + ", " + dto.getImporteOriginal() + ", " + dto.getIdCaja()
			+ ", '" + dto.getIdDivisa() + "' ");
			sbSQL.append("\n        , '" + dto.getIdDivisaOriginal() + "', '" + dto.getOrigenReg() + "', '"
					+ dto.getReferencia() + "', '" + dto.getConcepto() + "' ");
			sbSQL.append("\n        , " + dto.getAplica() + ", '" + dto.getIdEstatusMov() + "', '"
					+ dto.getbSalvoBuenCobro() + "', '" + dto.getIdEstatusReg() + "' ");
			sbSQL.append("\n        , " + dto.getIdBanco() + ", '" + dto.getIdChequera() + "', '" + dto.getFolioBanco()
			+ "', '" + dto.getOrigenMov() + "', '" + dto.getObservacion() + "' ");
			sbSQL.append("\n        , " + dto.getIdInvCbolsa() + ", " + dto.getNoFolioMov() + ", " + dto.getFolioRef()
			+ ", " + dto.getGrupo() + ", " + dto.getImporteDesglosado() + " ");
			sbSQL.append("\n        ," + dto.getLote() + ", convert(time,'" + dto.getHoraRecibo() + "'), '"
					+ dto.getDivision() + "', ");
			if (dto.getIdRubro().equals(""))
				sbSQL.append("0");
			else
				sbSQL.append("" + dto.getIdRubro() + "");
			sbSQL.append("\n        , " + dto.getNoRecibo() + ", '" + dto.getNoDocto() + "', '" + dto.getBeneficiario()
			+ "' ");
			if (!dto.getIdCliente().equals("") || !dto.getNoCliente().equals(""))
				if (!dto.getNoCliente().equals(""))
					pvvNoCliente = "0";
			sbSQL.append("\n    , " + pvvNoCliente + " ");
			if (dto.getIdBancoBenef() != 0 && !dto.getIdChequeraBenef().equals(""))
				sbSQL.append("\n    , " + dto.getIdBancoBenef() + ", '" + dto.getIdChequeraBenef() + "' ");
			if (!dto.getFecPropuesta().equals(""))
				sbSQL.append("\n    , '" + ponerFormatoFecha(dto.getFecPropuesta()) + "' ");
			sbSQL.append("\n) ");
			logger.info("insertParametro "+sbSQL);
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:insertParametro");
			return 0;
		}
	}
	@Override
	public List<DisposicionCreditoDto> selectDisp(String psIdContrato, int psIdDisp) {
		List<DisposicionCreditoDto> list = new ArrayList<DisposicionCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n  SELECT * FROM cat_disposicion_credito ");
			sbSQL.append("\n  WHERE id_financiamiento = '" + psIdContrato + "' ");
			sbSQL.append("\n        AND id_disposicion = " + psIdDisp + " ");
			sbSQL.append("\n  ORDER BY id_disposicion ");
			logger.info("selectDisp "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public DisposicionCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					DisposicionCreditoDto dto = new DisposicionCreditoDto();
					dto.setIdFinanciamiento(rs.getString("id_financiamiento"));
					dto.setIdDisposicion(rs.getInt("id_disposicion"));
					dto.setEmision(rs.getString("emision"));
					dto.setIdDivisa(rs.getString("id_divisa"));
					dto.setMontoDisposicion(rs.getDouble("monto_disposicion"));
					dto.setFecDisposicion(rs.getString("fec_disposicion"));
					dto.setFecVencimiento(rs.getString("fec_vencimiento"));
					dto.setSobreTasa(rs.getDouble("sobre_tasa"));
					dto.setAforoPorciento(rs.getDouble("aforo_porciento"));
					dto.setAforoImporte(rs.getDouble("aforo_importe"));
					dto.setLargoPlazo(rs.getString("largo_plazo").charAt(0));
					dto.setCalificadoraSp(rs.getString("calificadora_sp"));
					dto.setCalificadoraMoody(rs.getString("calificadora_moody"));
					dto.setCalificadoraFitch(rs.getString("calificadora_fitch"));
					dto.setTipoTasa(rs.getString("tipo_tasa").charAt(0));
					dto.setTasaBase(rs.getString("tasa_base"));
					dto.setValorTasa(rs.getDouble("valor_tasa"));
					dto.setPuntos(rs.getDouble("puntos"));
					dto.setTasaPonderada(rs.getDouble("tasa_ponderada"));
					dto.setMontoPostura(rs.getDouble("monto_postura"));
					dto.setSobreVenta(rs.getDouble("sobre_venta"));
					dto.setSobreTasacb(rs.getDouble("sobre_tasacb"));
					dto.setFormaPago(rs.getString("forma_pago").charAt(0));
					dto.setEstatus(rs.getString("estatus").charAt(0));
					dto.setIdBancoDisp(rs.getInt("id_banco_disp"));
					dto.setIdChequeraDisp(rs.getString("id_chequera_disp"));
					dto.setIdTipoFinanciamiento(rs.getInt("id_destino_recursos"));
					dto.setEquivalente(rs.getString("equivalente"));
					dto.setRenta(rs.getDouble("renta"));
					dto.setIdDisposicionRef(rs.getString("id_disposicion_ref"));
					dto.setAnexo(rs.getString("anexo"));
					dto.setComentarios(rs.getString("comentarios"));
					dto.setPeriodoRenta(rs.getInt("periodo_renta"));
					dto.setWhTax(rs.getDouble("wh_tax"));
					dto.setRentaDep(rs.getDouble("renta_dep"));
					dto.setComisionApertura(rs.getInt("comision_apertura"));
					dto.setMontoMoratorio(rs.getDouble("monto_moratorio"));
					dto.setTasaMoratoria(rs.getDouble("tasa_moratoria"));
					dto.setValorFacturas(rs.getDouble("valor_facturas"));
					return dto;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:selectDisp");
		}
		return list;
	}

	@Override
	public List<DisposicionCreditoDto> buscaComisiones(String psLinea, int piDisp) {
		List<DisposicionCreditoDto> list = new ArrayList<DisposicionCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n SELECT * FROM cat_amortizacion_credito ");
			sbSQL.append("\n WHERE id_contrato = '" + psLinea + "' ");
			sbSQL.append("\n   And id_disposicion = " + piDisp + " ");
			sbSQL.append("\n  And comision <> 0 ");
			logger.info("buscaComisiones "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public DisposicionCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					DisposicionCreditoDto dto = new DisposicionCreditoDto();

					dto.setIdFinanciamiento(rs.getString("id_financiamiento"));
					dto.setIdDisposicion(rs.getInt("id_disposicion"));

					return dto;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:buscaComisiones");
		}
		return list;
	}

	@Override
	public List<AmortizacionCreditoDto> selectAmortizaciones(String psIdContrato, int psIdDisposicion,
			boolean pbCambioTasa, String psTipoMenu, String psProyecto, int piCapital) {
		List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n SELECT cac.id_contrato, cac.id_disposicion, cac.id_amortizacion, cac.fec_vencimiento, ");
			sbSQL.append("\n  cac.capital , cac.interes, cac.gasto, cac.comision, cac.estatus, cac.tasa_vigente, ");
			sbSQL.append("\n   cac.tasa_fija, cac.periodo, cac.no_amortizaciones, cac.tasa_base, cac.valor_tasa, ");
			sbSQL.append(
					"\n  cac.puntos, cac.tasa, cac.saldo_insoluto, cac.pagar, cac.fec_inicio, cac.iva, cac.fecha_calint, ");
			sbSQL.append(
					"\n   cac.usuario_alta , cac.dia_cortecap, cac.dia_corteint, cac.dias, cac.fec_pago, cac.dias_periodo, ");
			sbSQL.append("\n   cac.banco_gastcom, cac.clabe_bancaria_gastcom, cac.comentario, cac.tipo_gasto, ");
			sbSQL.append(
					"\n   cac.sobre_tasacb, coalesce(cac.fact_capital, 0) as fact_capital, coalesce(cac.solo_renta, 0) as solo_renta, ");
			sbSQL.append(
					"\n   case when coalesce(cac.solo_renta, 0) = 1 then coalesce(cac.renta, 0) else coalesce(cdc.renta, 0) end as renta, ");
			sbSQL.append("\n   cdc.fec_vencimiento as fec_ven_dis, cdc.monto_disposicion, ce.desc_estatus");
			sbSQL.append("\n FROM  cat_amortizacion_credito cac left join cat_disposicion_credito cdc on ");
			sbSQL.append(
					"\n       (id_contrato = cdc.id_financiamiento and cac.id_disposicion = cdc.id_disposicion ), ");
			sbSQL.append("\n      cat_estatus ce");
			sbSQL.append("\n WHERE cac.id_contrato = '" + psIdContrato + "' ");
			sbSQL.append("\n      AND cac.id_disposicion = " + psIdDisposicion + " ");
			sbSQL.append("\n       AND cac.estatus = ce.id_estatus ");
			sbSQL.append("\n       AND cac.estatus in ('A', 'P') ");
			sbSQL.append("\n       AND ce.clasificacion = 'CRD' ");
			if (pbCambioTasa) {
				sbSQL.append("\n    AND cac.interes > 0 ");
				sbSQL.append("\n     AND cac.estatus = 'A' ");
			} else if (piCapital > 0) {
				sbSQL.append("\n   AND cac.capital > 0 ");
			}
			sbSQL.append("\n        AND cac.gasto = 0 ");
			sbSQL.append("\n       AND cac.comision = 0 ");
			if (pbCambioTasa){
				if(gsDBM.equals("SQL SERVER"))
					sbSQL.append("\n ORDER BY cac.id_amortizacion, cast(cac.fec_vencimiento  as date), cac.interes desc ");
				else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
					sbSQL.append("\n ORDER BY cac.id_amortizacion, cast(cac.fec_vencimiento  as date), cac.interes desc ");
			}else{
				if(gsDBM.equals("SQL SERVER"))
					sbSQL.append("\n ORDER BY cac.id_amortizacion asc, cast(cac.fec_vencimiento as date) ");
				else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
					sbSQL.append("\n ORDER BY cac.id_amortizacion asc, cast(cac.fec_vencimiento as date) ");
			}
			logger.info("selectAmortizaciones "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public AmortizacionCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					AmortizacionCreditoDto dto = new AmortizacionCreditoDto();
					dto.setIdContrato(rs.getString("id_contrato"));
					dto.setIdDisposicion(rs.getInt("id_disposicion"));
					dto.setIdAmortizacion(rs.getInt("id_amortizacion"));
					return dto;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:selectAmortizaciones");
		}
		return list;
	}

	@Override
	public int updateDisposicion(DisposicionCreditoDto dto) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		try {
			StringBuffer sbSQL = new StringBuffer();
			globalSingleton = GlobalSingleton.getInstancia();

			sbSQL.append("\n UPDATE cat_disposicion_credito ");
			sbSQL.append("\n SET calificadora_sp = '" + dto.getCalificadoraSp() + "', calificadora_moody = '"
					+ dto.getCalificadoraMoody() + "', ");
			sbSQL.append("\n     calificadora_fitch = '" + dto.getCalificadoraFitch() + "', emision = '"
					+ dto.getEmision() + "', ");
			sbSQL.append("\n     id_divisa = '" + dto.getIdDivisa() + "', monto_disposicion = "
					+ dto.getMontoDisposicion() + ", ");
			sbSQL.append("\n     fec_disposicion = '" + ponerFormatoFecha(dto.getFecDisposicion()) + "', fec_vencimiento = '"
					+ponerFormatoFecha(dto.getFecVencimiento()) + "', ");
			sbSQL.append("\n    sobre_tasa = " + dto.getSobreTasa() + " , aforo_porciento = " + dto.getAforoPorciento()
			+ ", ");
			sbSQL.append("\n    aforo_importe = " + dto.getAforoImporte() + ", largo_plazo = '" + dto.getLargoPlazo()
			+ "', ");
			sbSQL.append("\n   forma_pago = '" + dto.getFormaPago() + "', tipo_tasa = '" + dto.getTipoTasa()
			+ "', tasa_base = '" + dto.getTasaBase() + "', ");
			sbSQL.append("\n     valor_tasa = " + dto.getValorTasa() + ", puntos = " + dto.getPuntos() + ", ");
			sbSQL.append("\n     tasa_ponderada = " + dto.getTasaPonderada() + ", monto_postura = "
					+ dto.getMontoPostura() + ", ");
			sbSQL.append(
					"\n     sobre_venta = " + dto.getSobreVenta() + ", sobre_tasacb = " + dto.getSobreTasacb() + ", ");
			sbSQL.append("\n     id_banco_disp = " + dto.getIdBancoDisp() + ", id_chequera_disp = '"
					+ dto.getIdChequeraDisp() + "', ");
			sbSQL.append("\n     id_destino_recursos = " + dto.getIdTipoFinanciamiento() + ", equivalente = '"
					+ dto.getEquivalente() + "', ");
			sbSQL.append("\n    renta = " + dto.getRenta() + ", anexo = '" + dto.getAnexo() + "', comentarios = '"
					+ dto.getComentarios() + "', ");
			sbSQL.append("\n     periodo_renta = '" + dto.getPeriodoRenta() + "', wh_tax = " + dto.getWhTax()
			+ ", renta_dep = " + dto.getRentaDep() + ", ");
			sbSQL.append("\n     comision_apertura = " + dto.getComisionApertura() + ", monto_moratorio = "
					+ dto.getMontoMoratorio() + ", tasa_moratoria = " + dto.getTasaMoratoria() + ", ");
			sbSQL.append("\n     valor_facturas = " + dto.getValorFacturas() + ", usuario_modif = "
					+ globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", fec_modif = '" + fechaHoy + "' ");
			sbSQL.append("\n WHERE id_financiamiento = '" + dto.getIdFinanciamiento() + "' ");
			sbSQL.append("\n      And id_disposicion = " + dto.getIdDisposicion() + " ");
			logger.info("updateDisposicion "+sbSQL);
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:updateDisposicion");
			return 0;
		}
	}

	@Override
	public List<AmortizacionCreditoDto> selectExisteAmort(String psLinea, String psCredito) {
		List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n  SELECT * FROM cat_amortizacion_credito");
			sbSQL.append("\n  WHERE   id_contrato = '" + psLinea + "'");
			sbSQL.append("\n      AND id_disposicion = '" + psCredito + "'");
			sbSQL.append("\n      AND estatus = 'A'");
			if(gsDBM.equals("SQL SERVER")||gsDBM.equals("SYBASE"))
				sbSQL.append("\n  ORDER BY cast(fec_vencimiento as date)");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\n ORDER BY cast(fec_vencimiento as date)");
			logger.info("selectExisteAmort "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public AmortizacionCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					AmortizacionCreditoDto dto = new AmortizacionCreditoDto();
					dto.setIdContrato(rs.getString("id_contrato"));
					dto.setIdDisposicion(rs.getInt("id_disposicion"));
					dto.setIdAmortizacion(rs.getInt("id_amortizacion"));
					return dto;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:selectExisteAmort");
		}
		return list;
	}

	@Override
	public int deleteAmortizacion(String psFinanciamiento, int piDisp, boolean piInteres, boolean pbDisposicion) {
		try {
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			Date fechaHoy = consultasGenerales.obtenerFechaHoy();
			StringBuffer sbSQL = new StringBuffer();
			globalSingleton = GlobalSingleton.getInstancia();
			if (pbDisposicion) {
				sbSQL.append("\n UPDATE cat_disposicion_credito SET estatus = 'X', usuario_modif = "
						+ globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", fec_modif = '" + fechaHoy + "' ");
				sbSQL.append("\n WHERE id_financiamiento = '" + psFinanciamiento + "' ");
				sbSQL.append("\n      AND id_disposicion = " + piDisp + " ");
				sbSQL.append("\n       ;");
			}
			sbSQL.append("\n UPDATE cat_amortizacion_credito SET estatus = 'X', usuario_modif = "
					+ globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", fec_modif = '" + fechaHoy + "' ");
			sbSQL.append("\n WHERE id_contrato = '" + psFinanciamiento + "' ");
			sbSQL.append("\n       AND id_disposicion = " + piDisp + " ");
			if (piInteres)
				sbSQL.append("\n        AND id_amortizacion = 0 ");
			logger.info("deleteAmortizacion "+sbSQL);
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:deleteAmortizacion");
			return 0;
		}
	}

	@Override
	public int deleteAGAsigLin(String psFinanciamiento, int piDisp) {
		try {
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			Date fechaHoy = consultasGenerales.obtenerFechaHoy();
			StringBuffer sbSQL = new StringBuffer();
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n UPDATE aval_garantia_asig_lin SET estatus = 'X', usuario_modif = "
					+ globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", fec_modif = '" + fechaHoy + "' ");
			sbSQL.append("\n WHERE id_financiamiento = '" + psFinanciamiento + "' ");
			sbSQL.append("\n       And id_disposicion = " + piDisp + " ");
			sbSQL.append("\n       And estatus = 'A' ");
			logger.info("deleteAGAsigLin "+sbSQL);
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:deleteAGAsigLin");
			return 0;
		}
	}

	@Override
	public int deleteFactFacturas(String psFinanciamiento, int piDisp) {
		try {
			StringBuffer sbSQL = new StringBuffer();
			globalSingleton = GlobalSingleton.getInstancia();
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			Date fechaHoy = consultasGenerales.obtenerFechaHoy();
			sbSQL.append("\n UPDATE fact_facturas SET estatus = 'X', usuario_modif = "
					+ globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", fec_modif = '" + fechaHoy + "' ");
			sbSQL.append("\n WHERE id_financiamiento = '" + psFinanciamiento + "' ");
			sbSQL.append("\n       And id_disposicion = " + piDisp + " ");
			sbSQL.append("\n       And estatus = 'A' ");
			logger.info("deleteFactFacturas "+sbSQL);
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:deleteFactFacturas");
			return 0;
		}
	}

	@Override
	public int cancelaMovimiento(String psFinanciamiento, int piDisp) {
		try {
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			Date fechaHoy = consultasGenerales.obtenerFechaHoy();
			StringBuffer sbSQL = new StringBuffer();
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n UPDATE movimiento SET id_estatus_mov = 'X', usuario_modif = "
					+ globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", fec_modif = '" + fechaHoy + "' ");
			sbSQL.append("\n  WHERE no_factura = '" + psFinanciamiento + "' ");
			sbSQL.append("\n        And no_docto = " + piDisp + " ");
			logger.info("cancelaMovimiento "+sbSQL);
			return jdbcTemplate.update(sbSQL.toString() + "-" + jdbcTemplate.update(sbSQL.toString()));
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:cancelaMovimiento");
			return 0;
		}
	}

	@Override
	public Map<String, Object> generador(GeneradorDto dto) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.ejecutarGenerador(dto);
	}

	@Override
	public List<LlenaComboGralDto> funSQLComboPeriodo(boolean pdAmort) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			if (!pdAmort) {
				sbSQL.append("\n  SELECT RTRIM(LTRIM(id_periodo)) AS ID, ");
				sbSQL.append("\n desc_periodo AS Descrip FROM cat_periodo ");
			} else {
				sbSQL.append("\n SELECT dias AS ID, ");
				sbSQL.append("\n desc_periodo AS Descrip FROM cat_periodo ");
			}
			logger.info("funSQLComboPeriodo "+sbSQL);
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
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:generador");
		}

		return list;
	}

	@Override
	public int funSQLDeleteProvisiones(String psFinanciamiento, int piDisp, boolean pbEstatus) {
		try {
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			Date fechaHoy = consultasGenerales.obtenerFechaHoy();
			StringBuffer sbSQL = new StringBuffer();
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n UPDATE provisiones SET estatus = 'X', usuario_modif = "
					+ globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", fec_modif = '" + fechaHoy + "' ");
			sbSQL.append("\n WHERE  id_financiamiento = '" + psFinanciamiento + "' ");
			sbSQL.append("\n      AND id_disposicion = " + piDisp + " ");
			if (pbEstatus)
				sbSQL.append("\n AND estatus = 'A' ");
			sbSQL.append("\n UPDATE cat_amortizacion_credito SET estatus = 'X', usuario_modif = "
					+ globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", fec_modif = '" + fechaHoy + "' ");
			sbSQL.append("\n WHERE id_contrato = '" + psFinanciamiento + "' ");
			sbSQL.append("\n       AND id_disposicion = " + piDisp + " ");
			logger.info("funSQLDeleteProvisiones "+sbSQL);
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:funSQLDeleteProvisiones");
			return 0;
		}
	}

	@Override
	public int obtieneFolioAmort(String psIdContrato, int psIdDisposicion) {
		int folioAmort = 0;
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n  SELECT max(no_folio_amort) as no_folio_amort FROM cat_amortizacion_credito ");
			sbSQL.append("\n  WHERE ");
			sbSQL.append("\n       id_contrato = '" + psIdContrato + "' ");
			sbSQL.append("\n        AND id_disposicion = " + psIdDisposicion + " ");
			logger.info("storeObtieneFolioAmort "+sbSQL);
			folioAmort = jdbcTemplate.queryForInt(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:obtieneFolioAmort");
		}
		return folioAmort;
	}

	@Override
	public int insertBitacora(String psFinanciamiento, int piDisposicion, String psNota, String psFinanciamientoHijo,int noEmpresa) {
		try {
			StringBuffer sbSQL = new StringBuffer();
			globalSingleton = GlobalSingleton.getInstancia();
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			Date fechaHoy = consultasGenerales.obtenerFechaHoy();
			sbSQL.append("\n  INSERT INTO bitacora_creditoban (no_empresa, id_financiamiento, id_disposicion, ");
			sbSQL.append("\n             fec_alta, nota, usuario_alta, id_financiamiento_hijo, id_disposicion_hijo) ");
			sbSQL.append("\n VALUES (" + noEmpresa + ", '"
					+ psFinanciamiento + "', " + piDisposicion + ", ");
			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append("\n        cast ('" + fechaHoy + "' as date), '" + psNota + "', "
						+ globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", ");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\n        '" + fechaHoy + "' , '" + psNota + "', "+ globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", ");

			if (piDisposicion == 0)
				sbSQL.append("\n     '" + psFinanciamientoHijo.trim() + "', '') ");
			else
				sbSQL.append("\n    '', '" + psFinanciamientoHijo.trim() + "') ");
			logger.info("insertBitacora "+sbSQL);
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:insertBitacora");
			return 0;
		}
	}

	@Override
	public List<BitacoraCreditoBanDto> selectBitacora(String psFinanciamiento, int piDisposicion,int noEmpresa) {
		List<BitacoraCreditoBanDto> list = new ArrayList<BitacoraCreditoBanDto>();
		StringBuffer sbSQL = new StringBuffer();
		globalSingleton = GlobalSingleton.getInstancia();
		try {
			sbSQL.append("\n SELECT bc.fec_alta, (cu.nombre + ' ' + cu.paterno + ' ' + cu.materno) As usuario, ");
			sbSQL.append("\n        bc.nota, bc.id_financiamiento_hijo, bc.id_disposicion_hijo ");
			sbSQL.append("\n FROM  bitacora_creditoban bc, cat_usuario cu ");
			sbSQL.append("\n WHERE bc.no_empresa = " + noEmpresa + " ");
			sbSQL.append("\n       and bc.usuario_alta = cu.no_usuario ");
			if (psFinanciamiento != "" && piDisposicion == 0) {
				sbSQL.append("\n   and bc.id_financiamiento = '" + psFinanciamiento + "' ");
				sbSQL.append("\n   and bc.id_disposicion = 0 ");
			} else if (psFinanciamiento != "" && piDisposicion != 0) {
				sbSQL.append("\n   and bc.id_financiamiento = '" + psFinanciamiento + "' ");
				sbSQL.append("\n   and bc.id_disposicion = " + piDisposicion + " ");
			}
			logger.info("selectBitacora "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public BitacoraCreditoBanDto mapRow(ResultSet rs, int idx) throws SQLException {
					BitacoraCreditoBanDto compon = new BitacoraCreditoBanDto();
					compon.setFecAlta(rs.getString("fec_alta"));
					compon.setUsuarioC(rs.getString("usuario"));
					compon.setNota(rs.getString("nota"));
					compon.setIdFinanciamientoHijo(rs.getString("id_financiamiento_hijo"));
					compon.setIdDisposicionHijo(rs.getString("id_disposicion_hijo"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:selectBitacora");
		}
		return list;
	}

	@Override
	public List<ObligacionCreditoDto> obtenerObligaciones(String idFinanciamiento,int noEmpresa) {
		List<ObligacionCreditoDto> list = new ArrayList<ObligacionCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		globalSingleton = GlobalSingleton.getInstancia();
		try {
			sbSQL.append("\n select * from obligacion where no_empresa = "
					+ noEmpresa + "");
			sbSQL.append("\n and id_financiamiento = '" + idFinanciamiento + "'");
			sbSQL.append("\n and no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + "");
			sbSQL.append("\n and estatus = 'A' ");
			sbSQL.append("\n order by id_clave");
			logger.info("obtenerObligaciones "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public ObligacionCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					ObligacionCreditoDto compon = new ObligacionCreditoDto();
					compon.setIdClave(rs.getInt("id_clave"));
					compon.setDescripcion(rs.getString("descripcion"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:obtenerObligaciones");
		}
		return list;
	}

	@Override
	public int insertObligacion(String psFinanciamiento, int piClave, String descripcion,int noEmpresa) {
		try {
			StringBuffer sbSQL = new StringBuffer();
			globalSingleton = GlobalSingleton.getInstancia();
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			Date fechaHoy = consultasGenerales.obtenerFechaHoy();
			sbSQL.append(
					"\n insert into obligacion(no_empresa,id_financiamiento,id_clave,descripcion,fec_alta,no_usuario,estatus) values("
							+noEmpresa + ", '" + psFinanciamiento
							+ "', ");
			sbSQL.append("\n " + piClave + ", '" + descripcion + "', ");
			sbSQL.append("\n cast ('" + fechaHoy + "' as date), " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", 'A')");
			logger.info("insertObligacion "+sbSQL);
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:insertObligacion");
			return 0;
		}
	}

	@Override
	public int deleteObligacion(int idClave, String psFinanciamiento,int noEmpresa) {
		try {
			StringBuffer sbSQL = new StringBuffer();
			globalSingleton = GlobalSingleton.getInstancia();
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			Date fechaHoy = consultasGenerales.obtenerFechaHoy();
			sbSQL.append("\n UPDATE obligacion SET estatus = 'X', usuario_modif = "
					+ globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", fec_modif = cast('" + fechaHoy + "' as date) ");
			sbSQL.append("\n WHERE no_empresa = " + noEmpresa + "");
			sbSQL.append("\n and no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + "    ");
			sbSQL.append("\n and id_clave in (" + idClave + ")");
			sbSQL.append("\n and id_financiamiento = '" + psFinanciamiento + "'");
			logger.info("deleteObligacion "+sbSQL);
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:deleteObligacion");
			return 0;
		}
	}

	@Override
	public List<ObligacionCreditoDto> obtenerObligacionesTotal(String idFinanciamiento, int noEmpresa) {
		List<ObligacionCreditoDto> list = new ArrayList<ObligacionCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		globalSingleton = GlobalSingleton.getInstancia();
		try {
			sbSQL.append("\n select * from obligacion where no_empresa = "
					+ noEmpresa + "");
			sbSQL.append("\n and id_financiamiento = '" + idFinanciamiento + "'");
			sbSQL.append("\n and no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + "");
			sbSQL.append("\n order by id_clave");
			logger.info("obtenerObligacionesTotal "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public ObligacionCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					ObligacionCreditoDto compon = new ObligacionCreditoDto();
					compon.setIdClave(rs.getInt("id_clave"));
					compon.setDescripcion(rs.getString("descripcion"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:obtenerObligacionesTotal");
		}
		return list;
	}

	@Override
	public List<Map<String, Object>> obtenerReporteContratos(String idFinanciamiento) {
		StringBuffer sbSql = new StringBuffer();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		try {
			sbSql.append("\n SELECT DISTINCT (sg.nombre+' '+sg.apellido_paterno+' '+sg.apellido_materno)  usuario,");
			sbSql.append(" \n sum(coalesce((case when cdc.id_divisa = 'MN' then coalesce(cdc.monto_disposicion, 0)");
			sbSql.append(
					"\n when vd.valor >=1 And cdc.id_divisa <> ccc.id_divisa then vd.valor * coalesce(cdc.monto_disposicion, 0) ");
			sbSql.append(
					"\n when vd.valor <1 And cdc.id_divisa <> ccc.id_divisa then vd.valor / coalesce(cdc.monto_disposicion, 0) ");
			sbSql.append("\n when cdc.id_divisa = ccc.id_divisa then  coalesce(cdc.monto_disposicion, 0)");
			sbSql.append("\n end), 0)) as montoDisposicion,");
			sbSql.append(
					"\n e.nom_empresa empresa,ccc.id_financiamiento contrato,cb.desc_banco banco,ctc.descripcion tipoFinanciamiento,");
			sbSql.append("\n fec_inicio fechaInicio,cd.desc_divisa divisa,");
			sbSql.append(
					"\n case when ccc.revolvencia = 'S' then 'SI REVOLVENTE' else 'NO REVOLVENTE' end revolvencia,");
			sbSql.append(
					"\n case when ccc.reestructura = '1' then 'SI REESTRUCTURA' else 'NO REESTRUCTURA' end reestructura,");
			sbSql.append("\n case when ccc.largo_plazo = 'S' then 'LARGO PLAZO' else 'CORTO PLAZO' end largoPlazo,");
			sbSql.append(
					"\n cp.desc_pais pais,ccc.id_clabe chequera, ccc.fec_vencimiento fechaVencimiento,ccc.monto_autorizado montoAutorizado,ct.desc_tasa tasa,ccc.spreed spreed,");
			sbSql.append("\n case when ccc.tipo_operacion = 'C' then 'Con Recurso' ");
			sbSql.append("\n when ccc.tipo_operacion = 'S' then 'Sin recurso' ");
			sbSql.append("\n when ccc.tipo_operacion = 'A' then 'Anticipados/Proveedores' ");
			sbSql.append("\n else 'NO APLICA' end tipoOperacion,ccc.id_banco_prestamo,");
			sbSql.append(
					"\n coalesce(case when ccc.revolvencia = 'S' then (SELECT coalesce(sum(cac.capital), 0) FROM cat_amortizacion_credito cac ");
			sbSql.append("\n WHERE  ccc.id_financiamiento = cdc.id_financiamiento ");
			sbSql.append("\n And cdc.id_financiamiento = cac.id_contrato ");
			sbSql.append("\n And cdc.estatus = 'A' ");
			sbSql.append("\n And cac.estatus = 'P'  ");
			sbSql.append("\n And ccc.id_financiamiento = '" + idFinanciamiento + "') end, 0) as capital,");
			sbSql.append(
					"\n (select cb2.desc_banco from cat_contrato_credito ccc2 left join cat_banco cb2 on ccc2.id_banco_prestamo=cb2.id_banco where  ccc2.id_financiamiento = '"
							+ idFinanciamiento + "' ");
			sbSql.append("\n group by ccc2.id_financiamiento,desc_banco)representanteComun  ");
			sbSql.append(
					"\n FROM cat_contrato_credito ccc left join cat_disposicion_credito cdc on (ccc.id_financiamiento = cdc.id_financiamiento And cdc.estatus = 'A') ");
			sbSql.append(
					"\n left join valor_divisa vd on (cdc.id_divisa = vd.id_divisa and cast(vd.fec_divisa as date) = '"
							+ fechaHoy + "') left join SEG_USUARIO sg  on sg.id_usuario=ccc.usuario_alta ");
			sbSql.append("\n left join cat_banco cb on ccc.id_banco=cb.id_banco ");
			sbSql.append(
					"\n left join cat_tipo_contrato ctc on ctc.id_tipo_contrato=ccc.id_tipo_financiamiento left join cat_divisa cd on cd.id_divisa=ccc.id_divisa  ");
			sbSql.append(
					"\n left join empresa e on ccc.no_empresa=e.no_empresa left join cat_pais cp on cp.id_pais=ccc.id_pais left join cat_tasas ct on ct.id_tasa=ccc.tasa_linea ");
			sbSql.append("\n WHERE ccc.id_financiamiento = '" + idFinanciamiento
					+ "'  group by  sg.nombre,sg.apellido_materno,sg.apellido_paterno, e.nom_empresa,ccc.id_financiamiento,cb.desc_banco,ctc.descripcion,ccc.fec_inicio,");
			sbSql.append(
					"\n cd.desc_divisa,ccc.revolvencia,ccc.reestructura,ccc.largo_plazo,cp.desc_pais,ccc.id_clabe,ccc.fec_vencimiento,ccc.monto_autorizado,ct.desc_tasa,ccc.spreed,ccc.tipo_operacion,");
			sbSql.append("\n ccc.id_banco_prestamo,cdc.id_financiamiento,cdc.estatus");
			logger.info("obtenerReporteContratos "+sbSql);
			return this.getJdbcTemplate().queryForList(sbSql.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:obtenerReporteContratos");
			return null;
		}
	}
	@Override
	public int obtenerDifMeses(String idFinanciamiento, String idDisp) {
		StringBuffer sbSql = new StringBuffer();
		int meses = 0;
		try {
			if(gsDBM.equals("SQL SERVER"))
				sbSql.append(
						"\n SELECT datediff(mm, cast(cdc.fec_disposicion as datetime), cast(cac.fec_vencimiento as datetime)) as meses ");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSql.append(
						"\n SELECT  date_part('month',cast(cac.fec_vencimiento as date)) - date_part('month', cast(cdc.fec_disposicion as date)) as meses ");	    
			sbSql.append(" \n FROM  cat_disposicion_credito cdc, cat_amortizacion_credito cac");
			sbSql.append(" \n WHERE cdc.id_financiamiento = cac.id_contrato");
			sbSql.append(" \n   and cdc.id_disposicion = cac.id_disposicion ");
			sbSql.append(" \n   and cdc.id_financiamiento = '" + idFinanciamiento + "'");
			sbSql.append(" \n    and cdc.id_disposicion = = '" + idDisp + "'");
			sbSql.append(" \n   and cac.id_amortizacion = 1 ");
			logger.info("obtenerDifMeses "+sbSql);
			meses = jdbcTemplate.queryForInt(sbSql.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:obtenerDifMeses");
		}
		return meses;
	}

	@Override
	public List<LlenaComboGralDto> selectComboEmpresaAval(int noEmpresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		globalSingleton = GlobalSingleton.getInstancia();
		try {
			sbSQL.append("\n SELECT distinct ag.tipo_persona+'_'+convert(varchar,ag.no_empresa) as ID, e.nom_empresa as Descrip  ");
			sbSQL.append("\n FROM   aval_garantia ag, aval_garantia_asig_emp agae, empresa e ");
			sbSQL.append("\n WHERE  ag.no_empresa = agae.no_empresa_avalista ");
			sbSQL.append("\n       And ag.no_empresa = e.no_empresa ");
			sbSQL.append("\n       And ag.no_empresa in (select no_empresa from usuario_empresa where no_usuario = "
					+ globalSingleton.getUsuarioLoginDto().getIdUsuario() + ") ");
			sbSQL.append("\n       And agae.no_empresa_avalada = "
					+ noEmpresa + " ");
			sbSQL.append("\n  UNION ");
			sbSQL.append("\n SELECT distinct ag.tipo_persona+'_'+convert(varchar,ag.no_empresa) as ID, p.nombre+' '+p.paterno+' '+p.materno as Descrip");
			sbSQL.append("\n FROM   aval_garantia ag, aval_garantia_asig_emp agae,persona p  ");
			sbSQL.append("\n WHERE  ag.no_empresa = agae.no_empresa_avalista  ");
			sbSQL.append("\n 	And ag.no_empresa = p.no_persona ");
			sbSQL.append("\n    And agae.no_empresa_avalada = "
					+ noEmpresa + " ");
			sbSQL.append("\n 	And ag.tipo_persona='F'  ");
			sbSQL.append("\n   ORDER BY ID,Descrip");
			logger.info("selectComboEmpresaAval "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto compon = new LlenaComboGralDto();
					compon.setIdStr(rs.getString("ID"));
					compon.setDescripcion(rs.getString("Descrip"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:selectComboEmpresaAval");
		}

		return list;
	}

	@Override
	public List<AvalGarantiaDto> obtenerMontoDispuestoAvalada(String idFinanciamiento, int idDisp, int noEmpresa) {
		List<AvalGarantiaDto> list = new ArrayList<AvalGarantiaDto>();
		StringBuffer sbSQL = new StringBuffer();
		globalSingleton = GlobalSingleton.getInstancia();
		try {
			sbSQL.append(
					"\n SELECT agal.clave, ag.aval_garantia as id_aval, case when ag.aval_garantia = 'A' then 'AVAL' else 'GARANTIA' end as aval_garantia, ");
			sbSQL.append(
					"\n         agal.tipo_persona+'_'+convert(varchar,agal.no_empresa_avalista) as noEmpresa, e.nom_empresa as nomEmpresa, ag.descripcion, ag.fec_final, agal.fec_alta, agal.monto_dispuesto  ");
			sbSQL.append(
					"\n FROM      aval_garantia_asig_lin agal, aval_garantia_asig_emp agae, aval_garantia ag, empresa e ");
			sbSQL.append("\n WHERE   agal.no_empresa_avalada = agae.no_empresa_avalada ");
			sbSQL.append("\n           And ");
			sbSQL.append("\n agal.no_empresa_avalista = agae.no_empresa_avalista ");
			sbSQL.append("\n           And agal.clave = agae.clave ");
			sbSQL.append("\n           And agae.no_empresa_avalista = ag.no_empresa ");
			sbSQL.append("\n           And agae.clave = ag.clave ");
			sbSQL.append("\n           And agal.no_empresa_avalada = "
					+ noEmpresa + " ");
			sbSQL.append("\n           And agal.id_financiamiento = '" + idFinanciamiento + "' ");
			sbSQL.append("\n           And agal.id_disposicion = " + idDisp + " ");
			sbSQL.append("\n           And agal.no_empresa_avalista = e.no_empresa ");
			sbSQL.append("\n           And agal.estatus = 'A' ");
			sbSQL.append("\n UNION");
			sbSQL.append("\n SELECT agal.clave, ag.aval_garantia as id_aval, case when ag.aval_garantia = 'A' then 'AVAL' else 'GARANTIA' end as aval_garantia, ");
			sbSQL.append("\n        agal.tipo_persona+'_'+convert(varchar,agal.no_empresa_avalista) as noEmpresa, p.nombre+' '+p.paterno+' '+p.materno as nomEmpresa, ");
			sbSQL.append("\n  ag.descripcion, ag.fec_final, agal.fec_alta, agal.monto_dispuesto ");
			sbSQL.append("\n FROM      aval_garantia_asig_lin agal, aval_garantia_asig_emp agae, aval_garantia ag,persona p");
			sbSQL.append("\n WHERE   agal.no_empresa_avalada = agae.no_empresa_avalada ");
			sbSQL.append("\n           And ");
			sbSQL.append("\n agal.no_empresa_avalista = agae.no_empresa_avalista ");
			sbSQL.append("\n           And agal.clave = agae.clave ");
			sbSQL.append("\n           And agae.no_empresa_avalista = ag.no_empresa ");
			sbSQL.append("\n           And agae.clave = ag.clave ");
			sbSQL.append("\n           And agal.no_empresa_avalada = "
					+ noEmpresa + " ");
			sbSQL.append("\n           And agal.id_financiamiento = '" + idFinanciamiento + "' ");
			sbSQL.append("\n           And agal.id_disposicion = " + idDisp + " ");
			sbSQL.append("\n           And agal.no_empresa_avalista = p.no_persona  ");
			sbSQL.append("\n           And agal.estatus = 'A' ");
			sbSQL.append("\n  And agal.tipo_persona='F'");
			logger.info("obtenerMontoDispuestoAvalada "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public AvalGarantiaDto mapRow(ResultSet rs, int idx) throws SQLException {
					AvalGarantiaDto compon = new AvalGarantiaDto();
					compon.setClave(rs.getString("clave"));
					compon.setAvalGarantia(rs.getString("aval_garantia"));
					compon.setIdAvalGarantia(rs.getString("id_aval").charAt(0));
					compon.setNoEmpresaAvalistaAux(rs.getString("noEmpresa"));
					compon.setNomEmpresa(rs.getString("nomEmpresa"));
					compon.setDescripcion(rs.getString("descripcion"));
					compon.setFecFinal(rs.getString("fec_final"));
					compon.setFecAlta(rs.getString("fec_alta"));
					compon.setMontoDispuesto(rs.getDouble("monto_dispuesto"));

					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:obtenerMontoDispuestoAvalada");
		}
		return list;
	}

	@Override
	public List<LlenaComboGralDto> selectComboAvalGtia(int piEmpresaAvalista, String psDivisa, int empresa, char tipoPersona) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		globalSingleton = GlobalSingleton.getInstancia();
		try {
			sbSQL.append("\n SELECT distinct ag.clave as ID, ag.descripcion as Descrip ");
			sbSQL.append("\n FROM   aval_garantia_asig_emp agae, aval_garantia ag ");
			sbSQL.append("\n WHERE  agae.no_empresa_avalista = ag.no_empresa ");
			sbSQL.append("\n      And agae.no_empresa_avalista = " + piEmpresaAvalista + " ");
			sbSQL.append("\n       And agae.no_empresa_avalada = "
					+ empresa + " ");
			sbSQL.append("\n      And ag.id_divisa = '" + psDivisa + "' ");
			sbSQL.append("\n      And ag.tipo_persona = '" + tipoPersona + "' ");
			sbSQL.append("\n ORDER BY ag.clave ");
			logger.info("selectComboAvalGtia "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto compon = new LlenaComboGralDto();
					compon.setIdStr(rs.getString("ID"));
					compon.setDescripcion(rs.getString("Descrip"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:selectComboAvalGtia");
		}
		return list;
	}

	@Override
	public int selectAvaladaGtia(int piEmpresa, String psClave,int noEmpresa) {
		StringBuffer sbSQL = new StringBuffer();
		globalSingleton = GlobalSingleton.getInstancia();
		try {
			sbSQL.append("\n SELECT (coalesce(agae.monto_avalado, 0) - coalesce(sum(agal.monto_dispuesto), 0) ");
			sbSQL.append("\n           ) as monto_avalado ");
			sbSQL.append("\n FROM aval_garantia_asig_emp agae ");
			sbSQL.append(
					"\n       left join aval_garantia_asig_lin agal on (agae.no_empresa_avalista = agal.no_empresa_avalista and agae.no_empresa_avalada = agal.no_empresa_avalada and agae.clave = agal.clave) ");
			sbSQL.append("\n WHERE agae.no_empresa_avalista = " + piEmpresa + " ");
			sbSQL.append("\n       And agae.clave = '" + psClave + "' ");
			sbSQL.append("\n       And agae.no_empresa_avalada = "
					+ noEmpresa + "");
			sbSQL.append("\n GROUP BY agae.monto_avalado, agal.id_financiamiento ");
			logger.info("selectAvaladaGtia "+sbSQL);
			return jdbcTemplate.queryForInt(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:selectAvaladaGtia");
			return 0;
		}
	}

	@Override
	public int insertAvalGtiaLin(String psFinanciamiento, int piDisposicion, double pdMontoDispuesto,
			int piEmpresaAvalista, String psClave, int noEmpresa, char tipoPersona) {
		try {
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			Date fechaHoy = consultasGenerales.obtenerFechaHoy();
			StringBuffer sbSQL = new StringBuffer();
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append(
					"\n INSERT INTO aval_garantia_asig_lin(no_empresa_avalada, id_financiamiento, id_disposicion, ");
			sbSQL.append(
					"\n                                    monto_dispuesto, no_empresa_avalista, clave, fec_alta, usuario_alta, estatus, tipo_persona) ");
			sbSQL.append("\n VALUES ( " + noEmpresa + ", '"
					+ psFinanciamiento + "', " + piDisposicion + ", ");
			sbSQL.append("\n          " + pdMontoDispuesto + ", " + piEmpresaAvalista + ", '" + psClave + "', cast('"
					+ fechaHoy + "' as date), " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", 'A','"+tipoPersona+ "')");
			logger.info("insertAvalGtiaLin "+sbSQL);
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:insertAvalGtiaLin");
			return 0;
		}
	}

	@Override
	public int existeAvalGtiaLinea(int piEmpresa, String psClave, int noEmpresa, char tipoPersona) {
		StringBuffer sbSQL = new StringBuffer();
		List<AvalGarantiaDto> list = new ArrayList<AvalGarantiaDto>();
		globalSingleton = GlobalSingleton.getInstancia();
		try {
			sbSQL.append("\n SELECT * FROM aval_garantia_asig_emp agae, aval_garantia_asig_lin agal ");
			sbSQL.append("\n WHERE agae.no_empresa_avalada = agal.no_empresa_avalada ");
			sbSQL.append("\n       And agae.no_empresa_avalista = " + piEmpresa + " ");
			sbSQL.append("\n       And agae.clave = '" + psClave + "' ");
			sbSQL.append("\n       And agae.no_empresa_avalada = "
					+ noEmpresa + " ");
			sbSQL.append("\n       And agae.tipo_persona='"+tipoPersona+"'");
			logger.info("existeAvalGtiaLinea "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public AvalGarantiaDto mapRow(ResultSet rs, int idx) throws SQLException {
					AvalGarantiaDto compon = new AvalGarantiaDto();
					compon.setClave(rs.getString("clave"));
					return compon;
				}
			});
			if (list.size() > 0)
				return 1;
			else
				return 0;
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:existeAvalGtiaLinea");
			return -1;
		}
	}

	@Override
	public int deleteLineaAvalada(int piEmpresa, String psClave, String psLinea, int piCredito,int noEmpresa, char tipoPersona) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		try {
			StringBuffer sbSQL = new StringBuffer();
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n UPDATE aval_garantia_asig_lin SET estatus = 'X', usuario_modif = "
					+ globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", fec_modif =cast('" + fechaHoy + "' as date) ");
			sbSQL.append("\n 	WHERE no_empresa_avalista = " + piEmpresa + " ");
			sbSQL.append("\n     And clave = '" + psClave + "' ");
			sbSQL.append(
					"\n     And no_empresa_avalada = " + noEmpresa + " ");
			sbSQL.append("\n     And id_financiamiento = '" + psLinea + "' ");
			sbSQL.append("\n    And id_disposicion = " + piCredito + " ");
			sbSQL.append("\n    And tipo_persona = '" + tipoPersona + "' ");
			logger.info("deleteLineaAvalada "+sbSQL);
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:deleteLineaAvalada");
			return 0;
		}
	}

	@Override
	public int insertAmort(AmortizacionCreditoDto amortizacionCreditoDto, String piBisiesto) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		int valor = 0, pvIdAmortizacion = 0;
		try {
			StringBuffer sbSQL = new StringBuffer();
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n INSERT INTO cat_amortizacion_credito (id_contrato, id_disposicion, id_amortizacion, ");
			sbSQL.append("\n           fec_vencimiento, capital, interes, gasto, comision, estatus, ");
			sbSQL.append("\n           tasa_vigente, tasa_fija, periodo, no_amortizaciones, tasa_base, ");
			sbSQL.append("\n           valor_tasa, puntos, tasa, saldo_insoluto, fecha_calint, iva, ");
			sbSQL.append("\n           usuario_alta, fec_inicio, dia_cortecap, dia_corteint, dias, ");
			sbSQL.append("\n           fec_pago, dias_periodo, banco_gastcom, clabe_bancaria_gastcom, ");
			sbSQL.append("\n           comentario, tipo_gasto, sobre_tasacb, pagar, fact_capital, ");
			sbSQL.append("\n           solo_renta, renta, no_folio_amort) ");
			sbSQL.append("\n VALUES (  '" + amortizacionCreditoDto.getIdFinanciamiento() + "', "
					+ amortizacionCreditoDto.getIdDisposicion() + ", " + amortizacionCreditoDto.getIdAmortizacion()
					+ ", ");
			sbSQL.append("\n           '" + ponerFormatoFecha(amortizacionCreditoDto.getFecVencimiento()) + "', "
					+ amortizacionCreditoDto.getCapital() + ", " + amortizacionCreditoDto.getInteresA() + ", ");
			sbSQL.append("\n           " + amortizacionCreditoDto.getGasto() + ", "
					+ amortizacionCreditoDto.getComision() + ", '" + amortizacionCreditoDto.getEstatus() + "', ");
			sbSQL.append("\n           " + amortizacionCreditoDto.getTasaVigente() + ", "
					+ amortizacionCreditoDto.getTasaFija() + ", '" + amortizacionCreditoDto.getPeriodo() + "', ");
			sbSQL.append("\n           " + amortizacionCreditoDto.getNoAmortizaciones() + ", '"
					+ amortizacionCreditoDto.getTasaBase() + "', " + amortizacionCreditoDto.getValorTasa() + ", ");
			sbSQL.append("\n           " + amortizacionCreditoDto.getPuntos() + ", '" + amortizacionCreditoDto.getTasa()
			+ "', " + amortizacionCreditoDto.getSaldo() + ", ");
			sbSQL.append("\n           '" + ponerFormatoFecha(amortizacionCreditoDto.getFecVencimiento()) + "', "
					+ amortizacionCreditoDto.getIva() + ", " + globalSingleton.getUsuarioLoginDto().getIdUsuario()
					+ ", '" +ponerFormatoFecha(amortizacionCreditoDto.getFecInicio()) + "', ");
			sbSQL.append("\n          '" + amortizacionCreditoDto.getDiaCortecap() + "', '"
					+ amortizacionCreditoDto.getDiaCorteint() + "', " + piBisiesto + ", ");
			sbSQL.append("\n           '" +ponerFormatoFecha(amortizacionCreditoDto.getFecPago()) + "', "
					+ amortizacionCreditoDto.getDiasPeriodo() + ", " + amortizacionCreditoDto.getBancoGastcom() + ", '"
					+ amortizacionCreditoDto.getClabeBancariaGastcom() + "', ");
			sbSQL.append("\n           '" + amortizacionCreditoDto.getComentario() + "' , "
					+ amortizacionCreditoDto.getTipoGasto() + ", " + amortizacionCreditoDto.getSobreTasacb() + ", '"
					+ amortizacionCreditoDto.getPagar() + "', ");
			sbSQL.append("\n           " + amortizacionCreditoDto.getFactCapital() + ", "
					+ amortizacionCreditoDto.getSoloRenta() + ", " + amortizacionCreditoDto.getRenta() + ", "
					+ amortizacionCreditoDto.getNoFolioAmort() + " )");
			System.out.println(sbSQL.toString());
			logger.info("insertAmort "+sbSQL);
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:insertAmort");
			return 0;
		}
	}

	@Override
	public int selectExisteAmortizacion(String contrato, int disposicion, int noAmortizacion) {
		StringBuffer sbSQL = new StringBuffer();
		List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>();
		try {
			sbSQL.append("\n SELECT * FROM cat_amortizacion_credito ");
			sbSQL.append("\n WHERE id_contrato = '" + contrato + "'");
			sbSQL.append("\n And id_disposicion = " + disposicion + " ");
			sbSQL.append("\n And id_amortizacion = " + noAmortizacion + " ");
			sbSQL.append("\n And (capital >0 or interes >0)");
			sbSQL.append("\n And estatus in ('A', 'P') ");
			System.out.println(sbSQL.toString());
			logger.info("selectExisteAmortizacion "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public AvalGarantiaDto mapRow(ResultSet rs, int idx) throws SQLException {
					AvalGarantiaDto compon = new AvalGarantiaDto();
					compon.setClave(rs.getString("id_amortizacion"));
					return compon;
				}
			});
			if (list.size() > 0)
				return 1;
			else
				return 0;
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:selectExisteAmortizacion");
			return 0;
		}
	}

	@Override
	public int existeIntProv(String psIdContrato, int psIdDisposicion, int iConsecutivo, Date dFecVencimiento, int noEmpresa) {
		StringBuffer sbSQL = new StringBuffer();
		List<ProvisionCreditoDTO> list = new ArrayList<ProvisionCreditoDTO>();
		try {
			sbSQL.append("\n SELECT no_folio_det FROM provisiones ");
			sbSQL.append("\n WHERE ");
			sbSQL.append("\n       id_financiamiento = '" + psIdContrato + "' ");
			sbSQL.append("\n       AND id_disposicion = " + psIdDisposicion + " ");
			sbSQL.append("\n       AND no_empresa = " + noEmpresa);
			sbSQL.append("\n       AND consecutivo = " + iConsecutivo);
			sbSQL.append("\n       AND fec_fin_prov = '" + funciones.ponerFechaSola(dFecVencimiento) + "'");
		
			logger.info("existeIntProv "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public ProvisionCreditoDTO mapRow(ResultSet rs, int idx) throws SQLException {
					ProvisionCreditoDTO compon = new ProvisionCreditoDTO();
					compon.setNoFolioDet(rs.getInt("no_folio_det"));
					return compon;
				}
			});
			if (list.size() > 0)
				return list.get(0).getNoFolioDet();
			else
				return 0;
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:existeIntProv");
			return 0;
		}
	}

	@Override
	public List<AmortizacionCreditoDto> funSQLSelectAmortizacionesIntProv(String psIdContrato, int psIdDisposicion) {
		StringBuffer sbSQL = new StringBuffer();
		List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>();
		globalSingleton = GlobalSingleton.getInstancia();
		try {
			sbSQL.append("\n SELECT * FROM cat_amortizacion_credito ");
			sbSQL.append("\n WHERE ");
			sbSQL.append("\n       id_contrato = '" + psIdContrato + "' ");
			sbSQL.append("\n       AND id_disposicion = " + psIdDisposicion + " ");
			sbSQL.append("\n       AND interes <>0 ");
			if(gsDBM.equals("SQL SERVER")||gsDBM.equals("SYBASE"))
				sbSQL.append("\n   AND convert(date,fec_vencimiento)>= (select fec_hoy -15 from fechas) ");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\n       AND fec_vencimiento >= (select fec_hoy -15 from fechas) ");
			sbSQL.append("\n       AND estatus in ('A', 'P')");
			sbSQL.append("\n       order by fec_vencimiento ");
			logger.info("funSQLSelectAmortizacionesIntProv "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public AmortizacionCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					AmortizacionCreditoDto dto = new AmortizacionCreditoDto();
					dto.setIdContrato(rs.getString("id_contrato"));
					dto.setIdDisposicion(rs.getInt("id_disposicion"));
					dto.setIdAmortizacion(rs.getInt("id_amortizacion"));
					dto.setFecVencimiento(rs.getString("fec_vencimiento"));
					dto.setSaldoInsoluto(rs.getDouble("saldo_insoluto"));
					dto.setTasaVigente(rs.getDouble("tasa_vigente"));
					dto.setFecInicio(rs.getString("fec_inicio"));
					dto.setPuntos(rs.getDouble("puntos"));
					dto.setTasaFija(rs.getDouble("tasa_fija"));
					return dto;
				}
			});
			return list;
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:funSQLSelectAmortizacionesIntProv");
			return null;
		}

	}

	@Override
	public int insertProvisionInteres(ProvisionCreditoDTO provision) {
		
		try {
			StringBuffer sbSQL = new StringBuffer();
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			Date fechaHoy = consultasGenerales.obtenerFechaHoy();
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\nINSERT INTO provisiones (id_financiamiento, id_disposicion, no_empresa, consecutivo, ");
			sbSQL.append("\n                         id_banco, fec_ini_prov, fec_fin_prov, id_divisa, ");
			sbSQL.append("\n                         monto_saldo, monto_provision, tasa, estatus, fec_calculo_prov, ");
			sbSQL.append("\n                         usuario, no_folio_amort, no_folio_det) ");
			sbSQL.append("\n VALUES ('" + provision.getIdFinanciamiento() + "', " + provision.getIdDisposicion() + ", "
					+ provision.getNoEmpresa() + ", " + provision.getConsecutivo()
					+ ", ");
			sbSQL.append("\n          " + provision.getIdBanco() + ", '" + funciones.cambiarOrdenFecha(funciones.ponerFechaSola(provision.getFecIniProv())) + "', '"
					+funciones.cambiarOrdenFecha(funciones.ponerFechaSola(provision.getFecFinProv())) + "', '" + provision.getiDivisa() + "', ");
			sbSQL.append("\n          " + provision.getMontoSaldo() + ", " + provision.getMontoProvision() + ", "
					+ provision.getTasa() + ", 'A', cast('" + fechaHoy + "' as date), ");
			sbSQL.append("\n          " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", "
					+ provision.getNoFolioAmort() + ", " + provision.getNoFolioDet() + ") ");
			logger.info("insertProvisionInteres "+sbSQL);
		System.out.println(sbSQL.toString());
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:insertProvisionInteres");
			return 0;
		}
	}

	@Override
	public int updateProvision(ProvisionCreditoDTO provision) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		try {
			StringBuffer sbSQL = new StringBuffer();
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n UPDATE provisiones SET monto_saldo = " + provision.getMontoSaldo() + ", monto_provision = "
					+ provision.getMontoProvision() + ",");
			sbSQL.append("\n tasa = " + provision.getTasa() + ", usuario_modif = "
					+ globalSingleton.getUsuarioLoginDto().getIdUsuario() );
			if(gsDBM.equals("SQL SERVER")||gsDBM.equals("SYBASE")){
				sbSQL.append("\n, fec_modif = convert(date,'" + fechaHoy + "')");
				sbSQL.append("\n , fec_ini_prov =convert(date, '" + funciones.fechaString(provision.getFecIniProv()) + "') ");
			}
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE")){
				sbSQL.append("\n, fec_modif = convert(date,'" + fechaHoy + "')");
				sbSQL.append("\n , fec_ini_prov = convert(date,'" + funciones.fechaString(provision.getFecIniProv()) + "') ");
			}
			sbSQL.append("\n  WHERE id_financiamiento = '" + provision.getIdFinanciamiento() + "'");
			sbSQL.append("\n    And id_disposicion = " + provision.getIdDisposicion() + "");
			sbSQL.append("\n    And no_folio_det = " + provision.getNoFolioDet() + "");
			sbSQL.append("\n    And estatus = 'A'");
		
			logger.info("updateProvision "+sbSQL);
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:updateProvision");
			return 0;
		}
	}

	@Override
	public List<Map<String, String>> reporteAmorizaciones(String contrato, String disposicion) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		List<Map<String, String>> listaResultado = new ArrayList<Map<String, String>>();
		String sql = "";
		try {
			sql += "\n SELECT *, DATENAME(MONTH,CONVERT(DATE,fec_pago)) AS mes,DATEDIFF(dd,fec_inicio,fec_vencimiento) AS diasC,";
			sql += " \n 	cast(ROUND((interes*(iva/100)), 2, 0)as numeric(36,2)) as ivaC,  ";
			sql += " \n  cast(ROUND(capital+interes+(interes* (iva / 100)), 2, 0)as numeric(36,2)) as pagoTotal,";
			sql += " \n 	case WHEN tasa='V' THEN tasa_vigente";
			sql += " \n 		WHEN tasa='F' THEN tasa_fija end as tasaC";
			sql += " \n	FROM cat_amortizacion_credito cac, cat_estatus ce";
			sql += "\n WHERE cac.estatus = ce.id_estatus";
			sql += "\n       AND cac.id_contrato = '" + contrato + "' ";
			sql += "\n       AND cac.id_disposicion = " + disposicion + " ";
			sql += "\n       AND cac.gasto = 0 ";
			sql += "\n       AND cac.comision = 0 ";
			sql += "\n       AND cac.estatus in ('A', 'P')";
			sql += "\n       AND ce.clasificacion = 'CRD'";
			// Select Case gsDBM
			// Case "SQL SERVER":
			sql += "\n  ORDER BY no_amortizaciones, cast(fec_vencimiento as date), id_amortizacion asc ";
			// Case "POSTGRESQL", "ORACLE":
			// sSQL = sSQL & Chr(10) & " ORDER BY no_amortizaciones,
			// cast(fec_vencimiento as date), id_amortizacion asc "
			// End Select
			logger.info("reporteAmorizaciones "+sql);
			listaResultado = jdbcTemplate.query(sql, new RowMapper<Map<String, String>>() {
				@Override
				public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException {
					Map<String, String> campos = new HashMap<String, String>();
					campos.put("id_amortizacion", rs.getString("id_amortizacion"));
					campos.put("fec_inicio", rs.getString("fec_inicio"));
					campos.put("fec_vencimiento", rs.getString("fec_vencimiento"));
					campos.put("fec_pago", rs.getString("fec_pago"));
					campos.put("mes", rs.getString("mes"));
					campos.put("dias", rs.getString("diasC"));
					campos.put("tasa", rs.getString("tasaC"));
					campos.put("capital", rs.getString("capital"));
					campos.put("interes", rs.getString("interes"));
					campos.put("iva", rs.getString("ivaC"));
					campos.put("pagoTotal", rs.getString("pagoTotal"));
					campos.put("saldo_insoluto", rs.getString("saldo_insoluto"));
					campos.put("desc_estatus", rs.getString("desc_estatus"));
					return campos;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:reporteAmorizaciones");
		}
		return listaResultado;
	}
	@Override
	public List<AmortizacionCreditoDto> selectAmortizacionesCapital(String psIdContrato, int psIdDisposicion,
			boolean pbCambioTasa, String psTipoMenu, String psProyecto, int piCapital) {
		List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n SELECT cac.id_contrato, cac.id_disposicion, cac.id_amortizacion, cac.fec_vencimiento, ");
			sbSQL.append("\n  cac.capital , cac.interes, cac.gasto, cac.comision, cac.estatus, cac.tasa_vigente, ");
			sbSQL.append("\n  cac.tasa_fija, cac.periodo, cac.no_amortizaciones, cac.tasa_base, cac.valor_tasa, ");
			sbSQL.append(
					"\n  cac.puntos, cac.tasa, cac.saldo_insoluto, cac.pagar, cac.fec_inicio, cac.iva, cac.fecha_calint, ");
			sbSQL.append(
					"\n  cac.usuario_alta , cac.dia_cortecap, cac.dia_corteint, cac.dias, cac.fec_pago, cac.dias_periodo, ");
			sbSQL.append("\n  cac.banco_gastcom, cac.clabe_bancaria_gastcom, cac.comentario, cac.tipo_gasto, ");
			sbSQL.append(
					"\n  cac.sobre_tasacb, coalesce(cac.fact_capital, 0) as fact_capital, coalesce(cac.solo_renta, 0) as solo_renta, ");
			sbSQL.append(
					"\n  case when coalesce(cac.solo_renta, 0) = 1 then coalesce(cac.renta, 0) else coalesce(cdc.renta, 0) end as renta, ");
			sbSQL.append("\n   	cdc.fec_vencimiento as fec_ven_dis, cdc.monto_disposicion, ce.desc_estatus,");
			sbSQL.append("\n    DATEDIFF(dd,fec_inicio,fec_pago) as diasC");
			sbSQL.append("\n FROM  cat_amortizacion_credito cac left join cat_disposicion_credito cdc on ");
			sbSQL.append(
					"\n      (id_contrato = cdc.id_financiamiento and cac.id_disposicion = cdc.id_disposicion ), ");
			sbSQL.append("\n       cat_estatus ce");
			sbSQL.append("\n WHERE cac.id_contrato = '" + psIdContrato + "' ");
			sbSQL.append("\n       AND cac.id_disposicion = " + psIdDisposicion + " ");
			sbSQL.append("\n       AND cac.id_amortizacion <> 0 ");
			sbSQL.append("\n       AND cac.estatus = ce.id_estatus ");
			sbSQL.append("\n       AND cac.estatus in ('A', 'P') ");
			sbSQL.append("\n       AND ce.clasificacion = 'CRD' ");
			if (pbCambioTasa) {
				sbSQL.append("\n   AND cac.interes > 0 ");
				sbSQL.append("\n   AND cac.estatus = 'A' ");
			} else if (piCapital > 0) {
				sbSQL.append("\n   AND cac.capital > 0 ");
			}
			sbSQL.append("\n       AND cac.gasto = 0 ");
			sbSQL.append("\n       AND cac.comision = 0 ");
			if (pbCambioTasa){
				if(gsDBM.equals("SQL SERVER"))
					sbSQL.append("\n ORDER BY cac.id_amortizacion, cast(cac.fec_vencimiento  as date), cac.interes desc ");
				else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
					sbSQL.append("\n ORDER BY cac.id_amortizacion, cast(cac.fec_vencimiento  as date), cac.interes desc ");
			}else{
				if(gsDBM.equals("SQL SERVER"))
					sbSQL.append("\n ORDER BY cac.id_amortizacion asc, cast(cac.fec_vencimiento as date) ");
				else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
					sbSQL.append("\n ORDER BY cac.id_amortizacion asc, cast(cac.fec_vencimiento as date) ");
			}
			logger.info("selectAmortizacionesCapital "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public AmortizacionCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					AmortizacionCreditoDto dto = new AmortizacionCreditoDto();
					dto.setNoAmort(rs.getInt("id_amortizacion"));
					dto.setFecIni(funciones.cambiarFecha(rs.getString("fec_inicio")));
					dto.setFecVen(funciones.cambiarFecha(rs.getString("fec_vencimiento")));
					dto.setFecPag(funciones.cambiarFecha(rs.getString("fec_pago")));
					dto.setDias(rs.getInt("diasC"));
					dto.setCapital(rs.getDouble("capital"));
					dto.setSaldo(rs.getDouble("saldo_insoluto"));
					dto.setEstatus(rs.getString("estatus").charAt(0));
					dto.setDescEstatus(rs.getString("desc_estatus"));
					dto.setDiaCortecap(rs.getInt("dia_cortecap"));
					dto.setDiaCorteint(rs.getInt("dia_corteInt"));
					dto.setDiasPeriodo(rs.getInt("dias_periodo"));
					dto.setPeriodo(rs.getString("periodo"));
					dto.setNoAmortizaciones(rs.getInt("no_amortizaciones"));
					return dto;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:selectAmortizacionesCapital");
		}
		return list;
	}

	@Override
	public List<AmortizacionCreditoDto> selectAmortizacionesInteres(String psIdContrato, int psIdDisposicion,
			boolean pbCambioTasa, String psTipoMenu, String psProyecto, int piCapital, int dias) {
		List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n SELECT cac.id_contrato, cac.id_disposicion, cac.id_amortizacion, cac.fec_vencimiento, ");
			sbSQL.append("\n  cac.capital , cac.interes, cac.gasto, cac.comision, cac.estatus, cac.tasa_vigente, ");
			sbSQL.append("\n  cac.tasa_fija, cac.periodo, cac.no_amortizaciones, cac.tasa_base, cac.valor_tasa, ");
			sbSQL.append(
					"\n  cac.puntos, cac.tasa, cac.saldo_insoluto, cac.pagar, cac.fec_inicio, cac.iva, cac.fecha_calint, ");
			sbSQL.append(
					"\n  cac.usuario_alta , cac.dia_cortecap, cac.dia_corteint, cac.dias, cac.fec_pago, cac.dias_periodo, ");
			sbSQL.append("\n  cac.banco_gastcom, cac.clabe_bancaria_gastcom, cac.comentario, cac.tipo_gasto, ");
			sbSQL.append(
					"\n  cac.sobre_tasacb, coalesce(cac.fact_capital, 0) as fact_capital, coalesce(cac.solo_renta, 0) as solo_renta, ");
			sbSQL.append(
					"\n  case when coalesce(cac.solo_renta, 0) = 1 then coalesce(cac.renta, 0) else coalesce(cdc.renta, 0) end as renta, ");
			sbSQL.append("\n  cdc.fec_vencimiento as fec_ven_dis, cdc.monto_disposicion, ce.desc_estatus,");
			if (dias == -1)
				sbSQL.append("\n  DATEDIFF(dd,fec_inicio,cac.fec_vencimiento) as diasC,");
			sbSQL.append("\n  	case WHEN tasa='V' THEN tasa_vigente");
			sbSQL.append("\n 		WHEN tasa='F' THEN tasa_fija end as tasaC");
			sbSQL.append("\n FROM  cat_amortizacion_credito cac left join cat_disposicion_credito cdc on ");
			sbSQL.append(
					"\n      (id_contrato = cdc.id_financiamiento and cac.id_disposicion = cdc.id_disposicion ), ");
			sbSQL.append("\n       cat_estatus ce");
			sbSQL.append("\n WHERE cac.id_contrato = '" + psIdContrato + "' ");
			sbSQL.append("\n       AND cac.id_disposicion = " + psIdDisposicion + " ");
			sbSQL.append("\n       AND cac.id_amortizacion = 0 ");
			sbSQL.append("\n       AND cac.estatus = ce.id_estatus ");
			sbSQL.append("\n       AND cac.estatus in ('A', 'P') ");
			sbSQL.append("\n       AND ce.clasificacion = 'CRD' ");
			if (pbCambioTasa) {
				sbSQL.append("\n   AND cac.interes > 0 ");
				sbSQL.append("\n   AND cac.estatus = 'A' ");
			} else if (piCapital > 0) {
				sbSQL.append("\n   AND cac.capital > 0 ");
			}
			sbSQL.append("\n       AND cac.gasto = 0 ");
			sbSQL.append("\n       AND cac.comision = 0 ");
			if (pbCambioTasa){
				if(gsDBM.equals("SQL SERVER"))
					sbSQL.append("\n ORDER BY cac.id_amortizacion, cast(cac.fec_vencimiento  as date), cac.interes desc ");
				else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
					sbSQL.append("\n ORDER BY cac.id_amortizacion, cast(cac.fec_vencimiento  as date), cac.interes desc ");
			}else{
				if(gsDBM.equals("SQL SERVER"))
					sbSQL.append("\n ORDER BY cac.id_amortizacion asc, cast(cac.fec_vencimiento as date) ");
				else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
					sbSQL.append("\n ORDER BY cac.id_amortizacion asc, cast(cac.fec_vencimiento as date) ");
			}
			logger.info("selectAmortizacionesIntereses: " + sbSQL);
			final int diasAux=dias;
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public AmortizacionCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {

					AmortizacionCreditoDto dto = new AmortizacionCreditoDto();
					dto.setFecInicio(funciones.cambiarFecha(rs.getString("fec_inicio")));
					dto.setFecVenc(funciones.cambiarFecha(rs.getString("fec_vencimiento")));
					dto.setFecPago(funciones.cambiarFecha(rs.getString("fec_pago")));
					if (diasAux == -1)
						dto.setDias(rs.getInt("diasC"));
					else
						dto.setDias(diasAux);
					dto.setSaldo(rs.getDouble("saldo_insoluto"));
					dto.setSaldoIns(rs.getDouble("tasaC"));
					dto.setInteres(rs.getDouble("interes"));
					dto.setIva(rs.getDouble("interes") * rs.getDouble("iva") / 100);
					dto.setEstatus(rs.getString("estatus").charAt(0));
					dto.setDescEstatus(rs.getString("desc_estatus"));
					dto.setDiasPeriodo(rs.getInt("dias_periodo"));
					dto.setPeriodo(rs.getString("periodo"));
					dto.setNoAmortizaciones(rs.getInt("no_amortizaciones"));
					dto.setComentario(rs.getString("comentario"));
					dto.setDiaCorteint(rs.getInt("dia_corteInt"));
					return dto;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:selectAmortizacionesInteres");
		}
		return list;
	}
	@Override
	public void deleteProvisionesFuturas(String idFinanciamiento, int iIdDisposicion, Date vsFechaFin) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		try {
			StringBuffer sbSQL = new StringBuffer();
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n UPDATE provisiones SET estatus = 'X', usuario_modif = " +globalSingleton.getUsuarioLoginDto().getIdUsuario()+", fec_modif = '" +fechaHoy+"' ");
			sbSQL.append("\n WHERE  id_financiamiento = '" + idFinanciamiento +"' ");
			sbSQL.append("\n       AND id_disposicion = " + iIdDisposicion + " ");
			sbSQL.append("\n AND estatus = 'A' ");
			if(vsFechaFin!=null)
				sbSQL.append("\n AND fec_fin_prov >= '" +funciones.ponerFechaSola(vsFechaFin) + "' ");
			else
				sbSQL.append("\n AND fec_fin_prov >= '' ");
			jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:deleteProvisionesFuturas");

		}
	}
	@Override
	public List<LlenaComboGralDto> obtenerDestinoRecursos() {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		StringBuffer sbSQL = new StringBuffer();
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n SELECT id_destino_recurso AS ID, descripcion as Descrip ");
			sbSQL.append("\n from cat_destino_recurso");
			logger.info("obtenerDestinoRecursos "+sbSQL);
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
			+ "P:Financiamiento, C:AltaFinanciamientoDaoImpl, M:obtenerDestinoRecursos");
		}
		return list;
	}
	public String ponerFormatoFecha(String fechaString){
		return fechaString.substring(6,10)+"-"+fechaString.substring(3,5)+"-"+fechaString.substring(0,2);

	}
}
