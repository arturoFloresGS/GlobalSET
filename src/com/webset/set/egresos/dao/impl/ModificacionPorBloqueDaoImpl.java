package com.webset.set.egresos.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.conciliacionbancoset.dto.CriteriosBusquedaDto;
import com.webset.set.egresos.dao.ModificacionPorBloqueDao;
import com.webset.set.egresos.dto.SolicitudPagoDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.LlenaFormaPagoDto;
import com.webset.set.utilerias.dto.StoreParamsComunDto;
import com.webset.set.utilerias.ConstantesSet;

public class ModificacionPorBloqueDaoImpl implements ModificacionPorBloqueDao {
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	ConsultasGenerales consultasGenerales;
	Funciones funciones = new Funciones();
	GlobalSingleton globalSingleton;
	String gsDBM = ConstantesSet.gsDBM;

	public void setDataSource(DataSource dataSource) {
		try {
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public String consultarConfiguraSet(int indice) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
	}

	public List<LlenaComboEmpresasDto> obtenerEmpresas(int idUsuario) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.llenarComboEmpresas(idUsuario);
	}

	@Override
	public List<LlenaComboDivisaDto> obtenerDivisas() {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.llenarComboDivisa();
	}

	@Override
	public List<LlenaComboGralDto> obtenerBancos(int noEmpresa) {
		return llenarComboBancos(noEmpresa);
	}

	public List<LlenaFormaPagoDto> obtenerFormaPago() {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.llenarComboFormaPago();
	}

	@Override
	public List<LlenaComboGralDto> llenarComboGral(LlenaComboGralDto dto) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.llenarComboGralB(dto);
	}

	/**
	 * FunSQLCombo336
	 */

	public List<LlenaComboGralDto> llenarComboBancos(int noEmpresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			globalSingleton = GlobalSingleton.getInstancia();

			sbSQL.append("\n SELECT distinct cat_banco.id_banco as ID, cat_banco.desc_banco as Descrip");
			sbSQL.append("\n FROM cat_banco,cat_cta_banco ");
			sbSQL.append("\n WHERE cat_cta_banco.id_banco = cat_banco.id_banco ");
			sbSQL.append("\n AND cat_cta_banco.no_empresa = " + noEmpresa);
			sbSQL.append(
					"\n AND cat_cta_banco.tipo_chequera not in(" + globalSingleton.obtenerValorConfiguraSet(202) + ")");
			sbSQL.append("\n Order By desc_banco ");
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto bancos = new LlenaComboGralDto();
					bancos.setId(rs.getInt("ID"));
					bancos.setDescripcion(rs.getString("Descrip"));
					return bancos;
				}
			});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ModificacionPorBloqueDaoImpl, M:llenarComboBancos");
		}
		return list;
	}

	@Override
	public List<LlenaComboGralDto> obtenerDivision(int noEmpresa) {
		return llenarComboDivision(noEmpresa);
	}

	/**
	 * FunSQLComboDivision
	 */
	public List<LlenaComboGralDto> llenarComboDivision(int noEmpresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n  SELECT id_division as ID, desc_division as Descrip");
			sbSQL.append("\n  FROM cat_division ");
			if (noEmpresa != 0)
				sbSQL.append("\n  WHERE no_empresa=" + noEmpresa + " ");
			sbSQL.append("\n  ORDER BY desc_division");
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto divs = new LlenaComboGralDto();
					divs.setCampoUno(rs.getString("ID"));
					divs.setDescripcion(rs.getString("Descrip"));
					return divs;
				}
			});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ModificacionPorBloqueDaoImpl, M:llenarComboDivision");
		}

		return list;
	}

	// FunSQLSelect595
	@Override
	public List<SolicitudPagoDto> obtenerSolicitudes(CriteriosBusquedaDto dto) {
		List<SolicitudPagoDto> listSolicitud = new ArrayList<SolicitudPagoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append(
					"\n SELECT r.nom_empresa, m.no_folio_det, m.tipo_cambio, m.no_cuenta, m.no_docto, m.id_estatus_mov,");
			sbSQL.append("\n  m.no_folio_mov, m.importe, m.id_divisa, m.id_forma_pago, m.id_banco_benef,");
			sbSQL.append("\n  m.id_chequera_benef, m.beneficiario, m.id_chequera, m.no_cheque, m.folio_ref,");
			sbSQL.append("\n  m.valor_tasa, m.id_tipo_docto, m.fec_valor, m.concepto, m.id_caja,m.id_leyenda,");
			sbSQL.append("\n  m.fec_recalculo, m.no_cliente, b.id_banco, b.desc_banco, co.desc_tipo_operacion,");
			sbSQL.append("\n  co.id_tipo_operacion, cv.id_cve_operacion, cv.desc_cve_operacion, fp.desc_forma_pago,");
			sbSQL.append("\n  e.desc_estatus, m.division, m.cod_bloqueo");

			if(gsDBM.equals("ORACLE")){
				sbSQL.append("\n   FROM movimiento m, cat_banco b, cat_tipo_operacion co, cat_cve_operacion  cv, ");
				sbSQL.append("\n         cat_forma_pago fp, cat_estatus e, persona p, empresa r");
				sbSQL.append("\n  WHERE m.id_banco_benef = b.id_banco(+) ");
				sbSQL.append("\n    AND m.id_estatus_mov = e.id_estatus(+) ");
				sbSQL.append("\n    AND m.id_forma_pago = fp.id_forma_pago ");
				sbSQL.append("\n    AND m.id_tipo_operacion = co.id_tipo_operacion");
				sbSQL.append("\n    AND m.id_cve_operacion = cv.id_cve_operacion ");
				sbSQL.append("\n   AND m.no_empresa = r.no_empresa ");
				sbSQL.append("\n    AND m.id_tipo_movto = 'E'");
				sbSQL.append("\n   AND e.clasificacion='MOV' ");
				sbSQL.append("\n   AND m.id_tipo_operacion = 3000 ");
				sbSQL.append("\n    AND m.id_estatus_mov in ('N','C','F') ");
				sbSQL.append("\n    AND m.no_cliente = p.no_persona ");
				sbSQL.append("\n    AND p.id_tipo_persona = 'P' ");
				if(!dto.getBeneficiario().equals("")) 
					sbSQL.append("\n    AND p.equivale_persona = '" + dto.getBeneficiario() + "'");
			}
			else if(gsDBM.equals("SQL SERVER")||gsDBM.equals("SYBASE")){
				//			sbSQL.append("\n  FROM movimiento m , cat_banco b ,cat_tipo_operacion co, cat_cve_operacion  cv,");
				sbSQL.append("\n  FROM movimiento m LEFT JOIN cat_banco b ON (m.id_banco_benef = b.id_banco)");
				sbSQL.append("\n  LEFT JOIN cat_estatus e ON(m.id_estatus_mov = e.id_estatus),");
				sbSQL.append("\n  cat_tipo_operacion co, cat_cve_operacion  cv,");
				sbSQL.append("\n  cat_forma_pago fp, persona p, empresa r");
				//			sbSQL.append("\n  WHERE m.id_banco_benef *= b.id_banco ");
				//			sbSQL.append("\n  AND m.id_estatus_mov *= e.id_estatus ");
				//			sbSQL.append("\n  AND m.id_forma_pago = fp.id_forma_pago ");
				sbSQL.append("\n  WHERE m.id_forma_pago = fp.id_forma_pago ");
				sbSQL.append("\n  AND m.id_tipo_operacion = co.id_tipo_operacion ");
				sbSQL.append("\n  AND m.id_cve_operacion = cv.id_cve_operacion  ");
				sbSQL.append("\n  AND m.no_empresa = r.no_empresa  ");
				sbSQL.append("\n  AND m.id_tipo_movto = 'E'  ");
				sbSQL.append("\n  AND e.clasificacion='MOV'  ");
				sbSQL.append("\n  AND m.id_tipo_operacion = 3000  ");
				sbSQL.append("\n  AND m.id_estatus_mov in ('N','C','F')  ");
				if(gsDBM.equals("SYBASE"))
					sbSQL.append("\n    AND m.no_cliente = p.no_persona ");
				else
					sbSQL.append("\n  AND m.no_cliente = p.no_persona  ");
				sbSQL.append("\n  AND p.id_tipo_persona = 'P' ");
				
				if (!dto.getBeneficiario().equals(null) && !dto.getBeneficiario().equals("")) {
					sbSQL.append("\n      AND p.equivale_persona = '" + dto.getBeneficiario() + "'");
				}
			}
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("DB2")){
				sbSQL.append("\n   FROM movimiento m ");
				sbSQL.append("\n        LEFT JOIN cat_banco b On (m.id_banco_benef = b.id_banco)");
				sbSQL.append("\n        LEFT JOIN cat_estatus e On (m.id_estatus_mov = e.id_estatus and e.clasificacion='MOV') ");
				if(!dto.getBeneficiario().equals(""))
					sbSQL.append("\n        INNER JOIN persona p On (m.no_cliente = p.no_persona and p.id_tipo_persona = 'P' and p.equivale_persona = '" + dto.getBeneficiario() + "'" + " ) ");
				sbSQL.append("\n        ,cat_tipo_operacion co, cat_cve_operacion cv, ");
				sbSQL.append("\n        cat_forma_pago fp, empresa r ");
				sbSQL.append("\n  WHERE m.id_forma_pago = fp.id_forma_pago ");
				sbSQL.append("\n    AND m.no_empresa = r.no_empresa ");
				sbSQL.append("\n    AND co.no_empresa = '1'");
				sbSQL.append("\n    AND m.id_tipo_operacion = co.id_tipo_operacion");
				sbSQL.append("\n    AND cv.no_empresa = '1'");
				sbSQL.append("\n    AND m.id_cve_operacion = cv.id_cve_operacion ");
				sbSQL.append("\n    AND m.id_tipo_movto = 'E'");
				sbSQL.append("\n    AND m.id_tipo_operacion = 3000 ");
				sbSQL.append("\n    AND m.id_estatus_mov in ('N','C','F') ");
			}
			if (dto.getNoEmpresa() > 0)
				sbSQL.append("\n  AND m.no_empresa = " + dto.getNoEmpresa());
			else {
				sbSQL.append("\n  AND m.no_empresa in (Select no_empresa");
				sbSQL.append("\n  From usuario_empresa");
				sbSQL.append("\n  Where no_usuario= " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + ")");
			}
			// obtenerUsuario
			sbSQL.append("\n  AND (((select count(*) from usuario_area WHERE no_usuario = "
					+ globalSingleton.getUsuarioLoginDto().getIdUsuario() + " ) = 0) or ");
			sbSQL.append("\n  (id_area in (select id_area from usuario_area where no_usuario ="
					+ globalSingleton.getUsuarioLoginDto().getIdUsuario() + ")))");
			// FORMA DE PAGO
			if (dto.getFormaPago() > 0)
				sbSQL.append("\n  And m.id_forma_pago = " + dto.getFormaPago());
			// DIVISA
			if (!dto.getIdDivisa().equals(null) && !dto.getIdDivisa().equals("")) {
				sbSQL.append("\n AND m.id_divisa = '" + dto.getIdDivisa() + "'");
			}
			// DIVISION
			if (!dto.getDivision().equals(null) && !dto.getDivision().equals("")) {
				sbSQL.append("\n AND m.division= '" + dto.getDivision() + "'");
			}
			// FECHA
			if (dto.getFechaIni() != null && !dto.getFechaIni().equals("")) {
				if (!dto.getFechaFin().equals("") && dto.getFechaFin() != null) {
					sbSQL.append("\n  and m.fec_valor  >= CONVERT(date, '" + funciones.ponerFechaSola(dto.getFechaIni())
					+ "', 103)");
					sbSQL.append("\n  and m.fec_valor  <= CONVERT(date, '" + funciones.ponerFechaSola(dto.getFechaFin())
					+ "', 103)");
				} else {
					sbSQL.append("\n  and m.fec_valor  = CONVERT(date, '" + funciones.ponerFechaSola(dto.getFechaIni())
					+ "', 103");
				}
			}
			// ESTATUS
			if (!dto.getEstatus().equals(null) && !dto.getEstatus().equals("")) {
				switch (dto.getEstatus()) {
				case "0":
					sbSQL.append("\n   AND m.id_estatus_mov =  'C' ");
					break;
				case "1":
					sbSQL.append("\n   AND m.id_estatus_mov =  'F' ");
					break;
				case "2":
					sbSQL.append("\n   AND m.id_estatus_mov =  'N' ");
					break;
				}
			}
			if (dto.getMontoIni() != 0) {
				if (dto.getMontoFin() != 0) {
					sbSQL.append("\n  AND m.importe >=  " + dto.getMontoIni());
					sbSQL.append("\n  AND m.importe <= " + dto.getMontoFin());
				} else {
					sbSQL.append("\n  AND m.importe = " + dto.getMontoIni());
				}
			}
			// BANCO RECEPTOR
			if (dto.getIdBanco() > 0)
				sbSQL.append("\n  AND m.id_banco_benef = " + dto.getIdBanco());
			sbSQL.append("\n  ORDER BY m.id_estatus_mov ");
			System.out.println("Solicitudes" + sbSQL.toString());
			listSolicitud = jdbcTemplate.query(sbSQL.toString(), new RowMapper<SolicitudPagoDto>() {
				@Override
				public SolicitudPagoDto mapRow(ResultSet rs, int idx) throws SQLException {
					SolicitudPagoDto solicitud = new SolicitudPagoDto();
					solicitud.setNomEmpresa(rs.getString("nom_empresa"));
					solicitud.setNoFolioDet(rs.getInt("no_folio_det"));
					solicitud.setTipoCambio(rs.getDouble("tipo_cambio"));
					solicitud.setNoCuenta(rs.getInt("no_cuenta"));
					solicitud.setNoDocto(rs.getString("no_docto"));
					solicitud.setIdEstatusMov(rs.getString("id_estatus_mov").charAt(0));
					solicitud.setNoFolioMov(rs.getInt("no_folio_mov"));
					solicitud.setImporte(rs.getFloat("importe"));
					solicitud.setIdDivisa(rs.getString("id_divisa"));
					solicitud.setFormaPago(rs.getInt("id_forma_pago"));
					solicitud.setIdBancoBenef(rs.getInt("id_banco_benef"));
					solicitud.setIdChequeraBenef(rs.getString("id_chequera_benef"));
					solicitud.setBeneficiario(rs.getString("beneficiario"));
					solicitud.setIdChequera(rs.getString("id_chequera"));
					solicitud.setNoCheque(rs.getInt("no_cheque"));
					solicitud.setFolioRef(rs.getInt("folio_ref"));
					solicitud.setValorTasa(rs.getString("valor_tasa"));
					solicitud.setIdTipoDocto(rs.getInt("id_tipo_docto"));
					solicitud.setFecValor(rs.getDate("fec_valor"));
					solicitud.setConcepto(rs.getString("concepto"));
					solicitud.setIdCaja(rs.getInt("id_caja"));
					solicitud.setIdLeyenda(rs.getString("id_leyenda"));
					solicitud.setFecRecalculo(rs.getDate("fec_recalculo"));
					solicitud.setNoCliente(rs.getInt("no_cliente"));
					solicitud.setIdBanco(rs.getInt("id_banco"));
					solicitud.setDescBanco(rs.getString("desc_banco"));
					solicitud.setDescTipoOperacion(rs.getString("desc_tipo_operacion"));
					solicitud.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
					solicitud.setIdCveOperacion(rs.getInt("id_cve_operacion"));
					solicitud.setDescCveOperacion(rs.getString("desc_cve_operacion"));
					solicitud.setDescFormaPago(rs.getString("desc_forma_pago"));
					solicitud.setDescStatus(rs.getString("desc_estatus"));
					solicitud.setDivision(rs.getString("division"));
					solicitud.setDescCveOperacion(rs.getString("desc_cve_operacion"));
					solicitud.setCodBloqueo(rs.getString("cod_bloqueo").charAt(0));
					return solicitud;
				}
			});
		} catch (Exception e) {
			System.out.println("Error " + e);
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ModificacionPorBloqueDaoImpl, M:obtenerSolicitudes");
		}
		return listSolicitud;
	}

	/*
	 * Function FunSQLCombo397
	 */
	@Override
	public List<LlenaComboGralDto> obtenerProveedores(String texto, int noEmpresa) {
		StringBuffer sql = new StringBuffer();
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			if(gsDBM.equals("DB2"))
				sql.append("SELECT distinct p.equivale_persona as ID, coalesce(trim(p.razon_social), '') || coalesce(TRIM(NOMBRE), '') || ' ' || coalesce(TRIM(PATERNO), '') || ' ' || coalesce(TRIM(MATERNO), '')   as Descrip ");
			else
				sql.append(
						"SELECT p.equivale_persona as ID, rtrim(p.razon_social) + rTRIM(NOMBRE) + ' ' + rTRIM(PATERNO) + ' ' + rTRIM(MATERNO)   as Descrip");

			sql.append("    FROM persona p, proveedor pr");
			sql.append("    WHERE p.id_tipo_persona = 'P'");
			sql.append("    AND p.no_persona = pr.no_proveedor and id_estatus_per <> 'I' ");
			sql.append("    AND p.no_empresa = pr.no_empresa  ");
			sql.append("    AND p.no_empresa in (552," + noEmpresa + ")");

			sql.append("   AND ((p.razon_social like '" + texto + "%'");
			sql.append("   or p.paterno like '" + texto + "%'");
			sql.append("   or p.materno like '" + texto + "%'");
			sql.append("   or p.nombre like '" + texto + "%' ) or (p.equivale_persona like '" + texto + "%'))");
			list = jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>() {
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					try {
						cons.setId(rs.getInt("ID"));
					} catch (Exception e) {
						cons.setIdStr(rs.getString("ID"));
					}
					cons.setDescripcion(rs.getString("Descrip"));
					return cons;
				}
			});
		} catch (Exception e) {
			System.out.println("Error " + e);
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ModificacionPorBloqueDaoImpl, M:obtenerProveedores");
		}
		return list;
	}

	@Override
	public List<LlenaComboGralDto> obtenerBeneficiario(int noEmpresa, String texto) {
		StringBuffer sql = new StringBuffer();
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			sql.append("SELECT p.equivale_persona as ID, p.razon_social as Descrip ");
			sql.append("  FROM persona p, proveedor pr");
			sql.append("  WHERE p.id_tipo_persona = 'P' ");
			sql.append("  AND p.no_persona = pr.no_proveedor ");
			sql.append("  AND p.no_empresa = pr.no_empresa ");
			sql.append("  AND pr.no_empresa in (552," + noEmpresa + ")");
			sql.append("  AND p.equivale_persona = '" + texto + "'");
			list = jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>() {
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					try {
						cons.setId(rs.getInt("ID"));
					} catch (Exception e) {
						cons.setIdStr(rs.getString("ID"));
					}
					cons.setDescripcion(rs.getString("Descrip"));
					return cons;
				}
			});
		} catch (Exception e) {
			System.out.println("Error " + e);
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ModificacionPorBloqueDaoImpl, M:obtenerBeneficiario");
		}
		return list;
	}

	@Override
	public List<LlenaComboGralDto> obtenerBeneficiario2(int noEmpresa, String texto) {
		StringBuffer sql = new StringBuffer();
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			
			if(gsDBM.equals("ORACLE")||gsDBM.equals("POSTGRESQL")||gsDBM.equals("DB2")){
				sql.append("SELECT p.equivale_persona as ID, p.nombre || ' ' || p.paterno  || ' ' || p.materno as Descrip ");
				sql.append("  FROM persona p, proveedor pr");
				sql.append(" WHERE p.id_tipo_persona = 'P' ");
				sql.append("   AND p.no_persona = pr.no_proveedor ");
				sql.append("   AND p.no_empresa = pr.no_empresa ");
				sql.append("   AND pr.no_empresa in (552," +noEmpresa+ ") ");
				sql.append("   AND p.equivale_persona = '" +texto+ "'");
			}
			else if(gsDBM.equals("SQL SERVER")||gsDBM.equals("SYBASE")){
				sql.append("SELECT p.no_persona as ID, p.nombre + ' ' + p.paterno  + ' ' + p.materno as Descrip ");
				sql.append("  FROM persona p, proveedor pr");
				sql.append("  WHERE p.id_tipo_persona = 'P' ");
				sql.append("  AND p.no_persona = pr.no_proveedor ");
				sql.append("  AND p.no_empresa = pr.no_empresa ");
				sql.append("  AND pr.no_empresa in (552," + noEmpresa + ")");
				sql.append("   AND p.equivale_persona = '" + texto + "'");}
			list = jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>() {
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();

					try {
						cons.setId(rs.getInt("ID"));
					} catch (Exception e) {
						cons.setIdStr(rs.getString("ID"));
					}
					cons.setDescripcion(rs.getString("Descrip"));
					return cons;
				}
			});
		} catch (Exception e) {
			System.out.println("Error " + e);
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ModificacionPorBloqueDaoImpl, M:obtenerBeneficiario2");
		}
		return list;
	}

	/// FunSQLFPBeneficiario
	@Override
	public boolean validarFormaPagoBenef(int noEmpresa, int formaPago) {
		StringBuffer sql = new StringBuffer();
		boolean fp = false;
		List<SolicitudPagoDto> list = new ArrayList<SolicitudPagoDto>();
		try {
			sql.append("SELECT distinct no_empresa, no_persona, id_tipo_persona, id_forma_pago");
			sql.append("   FROM forma_pago_prov");
			sql.append("  WHERE no_empresa = 552 ");
			sql.append("    AND no_persona = " + noEmpresa);
			sql.append("    AND id_forma_pago = " + formaPago);
			sql.append("    AND id_tipo_persona = 'P'");
			System.out.println(sql.toString());
			list = jdbcTemplate.query(sql.toString(), new RowMapper<SolicitudPagoDto>() {
				@Override
				public SolicitudPagoDto mapRow(ResultSet rs, int idx) throws SQLException {
					SolicitudPagoDto cons = new SolicitudPagoDto();
					try {
						cons.setNoEmpresa(rs.getInt("no_empresa"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					return cons;
				}
			});
			if (list.isEmpty())
				fp = false;
			else
				fp = true;
		} catch (Exception e) {
			System.out.println("Error " + e);
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ModificacionPorBloqueDaoImpl, M:validarFormaPagoBenef");
		}
		return fp;

	}
	// FunSQLSelectBancoCheqBenef

	@Override
	public List<SolicitudPagoDto> validarBancoCheqBenef(int noProveedor, String divisa, int noEmpresa) {
		StringBuffer sql = new StringBuffer();

		List<SolicitudPagoDto> list = new ArrayList<SolicitudPagoDto>();
		try {

			sql.append("SELECT id_banco, id_chequera ");
			sql.append("  FROM ctas_banco ");
			sql.append(" WHERE no_persona = " + noProveedor);
			sql.append("   AND id_divisa = '" + divisa + "' ");
			sql.append("   AND id_tipo_persona in ('P','K') ");

			if (noEmpresa > 0)
				sql.append(" AND id_banco = " + noEmpresa);
			System.out.println(sql.toString());
			list = jdbcTemplate.query(sql.toString(), new RowMapper<SolicitudPagoDto>() {
				@Override
				public SolicitudPagoDto mapRow(ResultSet rs, int idx) throws SQLException {
					SolicitudPagoDto cons = new SolicitudPagoDto();
					try {
						cons.setIdBanco(rs.getInt("id_banco"));
						cons.setIdChequera(rs.getString("id_chequera"));
					} catch (Exception e) {
					}
					return cons;
				}
			});

		} catch (Exception e) {
			System.out.println("Error " + e);
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ModificacionPorBloqueDaoImpl, M:validarBancoCheqBenef");
		}
		return list;

	}

	// funSQLUpdateBancoCheqBenef
	@Override
	public int actualizaBancoCheqBenef(int noFolioDet, int iBancoBenef, String sChequeraBenef) {

		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n  UPDATE movimiento");
			sbSQL.append("\n     SET id_banco_benef = " + iBancoBenef + ",");
			sbSQL.append("\n       id_chequera_benef = '" + sChequeraBenef + "',");
			sbSQL.append("\n       id_leyenda = '1' ");
			sbSQL.append("\n   WHERE no_folio_det = " + noFolioDet);
			sbSQL.append("\n    OR folio_ref = " + noFolioDet);
			System.out.println(sbSQL.toString());
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ModificacionPorBloqueDaoImpl, M:actualizaBancoCheqBenef");
			return 0;
		}
	}

	// funSQLUpdateLimpiaBancoCheqBenef
	@Override
	public int actualizaLimpiaBancoCheqBenef(int noFolioDet) {
		StringBuffer sbSQL = new StringBuffer();
		try {

			sbSQL.append("UPDATE movimiento ");
			sbSQL.append("   SET id_banco_benef = null, ");
			sbSQL.append("       id_chequera_benef = null, ");
			sbSQL.append("       id_leyenda = '1' ");
			sbSQL.append(" WHERE no_folio_det = " + noFolioDet);
			sbSQL.append("    OR folio_ref = " + noFolioDet);
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ModificacionPorBloqueDaoImpl, M:actualizaLimpiaBancoCheqBenef");
			return 0;
		}
	}

	@Override
	public Map<String, Object> cambiarCuadrantes(StoreParamsComunDto dto) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.cambiarCuadrantes(dto);
	}

	// funSQLUpdateBloqueo
	@Override
	public int modificarBloqueo(int noFolioDet, String psBloqueo) {

		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n  UPDATE movimiento");
			sbSQL.append("    SET cod_bloqueo = '" + psBloqueo + "' ");
			sbSQL.append("  WHERE no_folio_det = " + noFolioDet);

			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ModificacionPorBloqueDaoImpl, M:modificarBloqueo");
			return 0;
		}
	}
}
