package com.webset.set.egresos.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.sql.Types;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.webset.set.egresos.dao.ConfirmacionCargoCtaDao;
import com.webset.set.egresos.dto.ConfirmacionCargoCtaDto;
import com.webset.set.egresos.dto.ResultadoDto;
import com.webset.set.financiamiento.dao.impl.VencimientoFinanciamientoCDaoImpl;
import com.webset.set.financiamiento.dto.AmortizacionCreditoDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.StoreParamsComunDto;
import com.webset.set.utilerias.ConstantesSet;

public class ConfirmacionCargoCtaDaoImpl implements ConfirmacionCargoCtaDao{
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora;
	Funciones funciones;
	GlobalSingleton globalSingleton;
	ConsultasGenerales consultasGenerales;
	String fecHoy = "";
	private static Logger logger = Logger.getLogger(ConfirmacionCargoCtaDaoImpl.class);


	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	public List<LlenaComboGralDto> obtenerBancos(int noEmpresa) {
		String sql="";
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();

		try{
			sql+= "SELECT id_banco as ID, desc_banco as DESCRIPCION";
			sql+= "\n	FROM cat_banco";
			sql+= "\n	WHERE id_banco in (SELECT distinct id_banco FROM cat_cta_banco";
			sql+= "\n					   WHERE tipo_chequera in ('P', 'O', 'M')";
			if(noEmpresa != 0)
				sql+= "\n					   		And no_empresa = " + noEmpresa + "";
			sql+= "\n							And id_divisa like '%')";

			list = jdbcTemplate.query(sql, new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerBancos");
		}
		return list;
	}

	public List<LlenaComboChequeraDto> llenaComboChequera(int idBanco, int noEmpresa, String idDivisa) {
		StringBuffer sql=new StringBuffer();
		List<LlenaComboChequeraDto> list = new ArrayList<LlenaComboChequeraDto>();

		try{
			sql.append(" SELECT id_chequera as ID ");
			sql.append(" \n	FROM cat_cta_banco ");
			sql.append(" \n	WHERE id_banco = " + idBanco + "  ");

			if(noEmpresa != 0 )
				sql.append(" \n		AND no_empresa = " + noEmpresa + "  ");

			sql.append(" \n		AND	tipo_chequera not in (SELECT valor FROM configura_set WHERE indice = 202) ");

			if(!idDivisa.equals(""))
				sql.append(" \n	AND	id_divisa = '" + idDivisa + "' ");

			System.out.println("llenaComboChequera: " + sql.toString());

			list = jdbcTemplate.query(sql.toString().toString(), new RowMapper<LlenaComboChequeraDto>(){
				public LlenaComboChequeraDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboChequeraDto cons = new LlenaComboChequeraDto();
					cons.setId(rs.getString("ID"));
					//cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:ConfirmacionTransferenciasDaoImpl, M:LlenaComboChequeraDto ");
		}
		return list;
	}

	public List<MovimientoDto> llenarMovtosPagos(ConfirmacionCargoCtaDto dtoGrid) {
		String sql="";
		List<MovimientoDto> list = new ArrayList<MovimientoDto>();

		try{
			sql+= "SELECT origen_mov,concepto, importe, no_folio_det, id_divisa, beneficiario, no_docto, no_factura, no_cliente, fec_valor_original,";
			sql+= "\n	no_empresa, id_banco, id_chequera,cve_control";
			sql+= "\n	FROM movimiento";
			sql+= "\n	WHERE id_tipo_operacion = 3200";
			sql+= "\n		AND id_estatus_mov = 'T'";
			sql+= "\n		AND id_forma_pago = 5";

			if(!dtoGrid.getFechaIni().equals("") && !dtoGrid.getFechaFin().equals(""))
				sql+= "\n		AND fec_valor BETWEEN convert(datetime,'" + dtoGrid.getFechaIni() + "',103) AND convert(datetime,'" + dtoGrid.getFechaFin() + "',103)";
			if(dtoGrid.getNoEmpresa() != 0)
				sql+= "\n		AND no_empresa = " + dtoGrid.getNoEmpresa() + "";
			if(dtoGrid.getIdBanco() != 0)
				sql+= "\n		AND id_banco = " + dtoGrid.getIdBanco() + "";
			if(!dtoGrid.getIdChequera().equals(""))
				sql+= "\n		AND id_chequera = '" + dtoGrid.getIdChequera() + "'";
			System.out.println("obtenerPagos "+sql);
			list = jdbcTemplate.query(sql, new RowMapper<MovimientoDto>(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons = new MovimientoDto();
					cons.setOrigenMov(rs.getString("origen_mov"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setImporte(rs.getDouble("importe"));
					cons.setNoFolioDet(rs.getInt("no_folio_det"));
					cons.setIdDivisa(rs.getString("id_divisa"));
					cons.setBeneficiario(rs.getString("beneficiario"));
					cons.setNoDocto(rs.getString("no_docto"));
					cons.setNoCliente(rs.getString("no_cliente"));
					cons.setNoFactura(rs.getString("no_factura"));
					cons.setFecValorOriginal(rs.getDate("fec_valor_original"));
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setIdBanco(rs.getInt("id_banco"));
					cons.setIdChequera(rs.getString("id_chequera"));
					cons.setCveControl(rs.getString("cve_control"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:llenarMovtosPagos");
		}
		return list;
	}

	public List<ConfirmacionCargoCtaDto> llenarMovtosCargos(ConfirmacionCargoCtaDto dtoGrid) {
		String sql="";
		String gsDBM = ConstantesSet.gsDBM;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{
			if(!dtoGrid.isTodos()) {
				if(gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2"))
					sql+= "SELECT descripcion, importe, secuencia, fec_valor, concepto, be.no_empresa, be.id_banco, be.id_chequera";
				else if(gsDBM.equals("SQL SERVER"))
					sql+= "SELECT TOP 1 descripcion, importe, secuencia , fec_valor, concepto,be.id_cve_concepto, be.no_empresa, be.id_banco, be.id_chequera";
				else if(gsDBM.equals("SYBASE"))
					sql+= "SELECT SELECT max(secuencia) as secuencia, descripcion, importe, fec_valor, concepto, be.no_empresa, be.id_banco, be.id_chequera";
			}else
				sql+= "SELECT descripcion, importe, secuencia, fec_valor, concepto,be.id_cve_concepto, be.no_empresa as no_empresa, be.id_banco as id_banco,  be.id_chequera as id_chequera";

			sql+= "\n	FROM movto_banca_e be, equivale_concepto ec";
			sql+= "\n	WHERE be.cargo_abono = 'E'";
			sql+= "\n		AND ec.id_banco = be.id_banco";
			sql+= "\n		AND ec.b_cargo_cta = 'S'";
			sql+= "\n		AND ec.cargo_abono = be.cargo_abono";
			sql+= "\n		AND be.concepto LIKE ec.desc_concepto";
			sql+= "\n		AND id_estatus_trasp NOT IN ('T','X')";

			if(dtoGrid.getSecuencia().equals(""))
				sql+= "\n		AND secuencia NOT IN (0)";
			else
				sql+= "\n		AND secuencia NOT IN ('" + dtoGrid.getSecuencia().equals("") + "')";

			if(!dtoGrid.isTodos())
				sql+= "\n		AND importe = " + dtoGrid.getImporte() + "";
			if(!dtoGrid.getFechaIni().equals("") && !dtoGrid.getFechaFin().equals(""))
				sql+= "\n		AND fec_valor BETWEEN convert(datetime,'" + dtoGrid.getFechaIni() + "',103) AND convert(datetime,'" + dtoGrid.getFechaFin() + "',103)";
			if(dtoGrid.getNoEmpresa() != 0)
				sql+= "\n		AND be.no_empresa = " + dtoGrid.getNoEmpresa() + "";
			if(dtoGrid.getIdBanco() != 0)
				sql+= "\n		AND be.id_banco = " + dtoGrid.getIdBanco() + "";
			if(!dtoGrid.getIdChequera().equals(""))
				sql+= "\n		AND be.id_chequera = '" + dtoGrid.getIdChequera() + "'";

			if(gsDBM.equals("POSTGRESQL") && !dtoGrid.isTodos())
				sql+= "\n	LIMIT 1";
			else if(gsDBM.equals("DB2") && !dtoGrid.isTodos())
				sql+= "\n	fetch first 1 row only";
			else if(gsDBM.equals("SYBASE") && !dtoGrid.isTodos())
				sql+= "\n	GROUP BY descripcion, importe, fec_valor, concepto, be.no_empresa, be.id_banco, be.id_chequera";

			if(dtoGrid.isChkRechNom()) {
				sql+= " UNION ALL";
				sql+= "\n	SELECT descripcion, importe * -1 as importe, secuencia, fec_valor, concepto, no_empresa, id_banco, id_chequera";
				sql+= "\n	FROM movto_banca_e ";
				sql+= "\n	WHERE cargo_abono = 'I'";
				sql+= "\n		AND descripcion LIKE '%DEPOSITO POR REV%'";
				sql+= "\n		AND id_estatus_trasp NOT IN ('T','X')";
				sql+= "\n		AND fec_valor <= '" + dtoGrid.getFechaIni() + "'";

				if(dtoGrid.getNoEmpresa() != 0)
					sql+= "\n		AND no_empresa = " + dtoGrid.getNoEmpresa() + "";
				if(dtoGrid.getIdBanco() != 0)
					sql+= "\n		AND be.id_banco = " + dtoGrid.getIdBanco() + "";
				if(!dtoGrid.getIdChequera().equals(""))
					sql+= "\n		AND id_chequera = '" + dtoGrid.getIdChequera() + "'";
			}
			System.out.println(sql);
			list = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setDescripcion(rs.getString("descripcion"));
					cons.setImporte(rs.getDouble("importe"));
					cons.setSecuencia(rs.getString("secuencia"));
					cons.setFecValor(rs.getDate("fec_valor"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setId_cve_concepto(rs.getString("id_cve_concepto"));
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setIdBanco(rs.getInt("id_banco"));
					cons.setIdChequera(rs.getString("id_chequera"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:llenarMovtosCargos");
		}
		return list;
	}

	public Map<String, Object> ejecutarConfirmador(int folioDet, String folioBanco, int secuencia, Date fecValor, int result, String msg)
	{
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.ejecutarConfirmador(folioDet, folioBanco, secuencia, fecValor, result, msg);
	}

	//Para la pantalla de Consulta de mantenimiento de cupos
	public List<ConfirmacionCargoCtaDto> llenaComboGpoEmpresas() {
		globalSingleton = GlobalSingleton.getInstancia();
		String sql = "";
		List<ConfirmacionCargoCtaDto> query = new ArrayList<ConfirmacionCargoCtaDto>();

		try {
			sql += "SELECT distinct c.id_grupo_flujo, desc_grupo_flujo";
			sql += "\n	FROM cat_grupo_flujo c, grupo_empresa g, usuario_empresa  u";
			sql += "\n	WHERE c.id_grupo_flujo = g.id_grupo_flujo";
			sql += "\n		AND g.no_empresa = u.no_empresa";
			sql += "\n		AND u.no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + "";
			sql += "\n	ORDER BY desc_grupo_flujo";

			query = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();

					cons.setIdGrupoFlujo(rs.getInt("id_grupo_flujo"));
					cons.setDescGrupoFlujo(rs.getString("desc_grupo_flujo"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:llenaComboGpoEmpresas");
		}
		return query;
	}


	public List<ConfirmacionCargoCtaDto> llenaComboDivision() {
		String sql = "";
		String gsDBM = ConstantesSet.gsDBM;
		globalSingleton = GlobalSingleton.getInstancia();
		List<ConfirmacionCargoCtaDto> query = new ArrayList<ConfirmacionCargoCtaDto>();

		try {
			sql += "SELECT cd.id_division as id,";

			if(gsDBM.equals("SQL SERVER"))
				sql += "\n	cast(e.no_empresa as varchar) + ' - ' + cd.desc_division as descrip";
			else if(gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2"))
				sql += "\n	cast(e.no_empresa as varchar) || ' - ' || cd.desc_division as descrip";
			else if(gsDBM.equals("SYBASE"))
				sql += "\n	convert(varchar, e.no_empresa) + ' - ' + cd.desc_division as descrip";

			sql += "\n	FROM empresa e, cat_division cd";
			sql += "\n	WHERE e.b_division = 'S'";
			sql += "\n		And cd.no_empresa = e.no_empresa";
			sql += "\n		And cd.no_empresa in (SELECT no_empresa FROM usuario_empresa WHERE";
			sql += "\n			no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + " )";
			sql += "\n		And cd.id_division in (SELECT ud.id_division FROM usuario_division ud WHERE ";
			sql += "\n			ud.no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + "";
			sql += "\n			And ud.no_empresa = cd.no_empresa)";
			sql += "\n	ORDER BY e.no_empresa, cd.desc_division";

			query = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();

					cons.setIdDivision(rs.getString("id"));
					cons.setDescDivision(rs.getString("descrip"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:llenaComboDivision");
		}
		return query;
	}


	public List<ConfirmacionCargoCtaDto> llenaComboGpoCupo() {
		String sql = "";
		List<ConfirmacionCargoCtaDto> query = new ArrayList<ConfirmacionCargoCtaDto>();

		try {
			sql += "SELECT * FROM cat_grupo_cupo";

			query = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();

					cons.setIdGrupo(rs.getInt("id_grupo_cupo"));
					cons.setDescGrupoCupo(rs.getString("desc_grupo_cupo"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:llenaComboGpoCupo");
		}
		return query;
	}


	public List<ConfirmacionCargoCtaDto> buscarCupos(int optCupos, int grupoEmpresa, String fecIni, String fecFin, String idDivision) {
		String sql = "";
		List<ConfirmacionCargoCtaDto> query = new ArrayList<ConfirmacionCargoCtaDto>();

		try {
			sql += "SELECT sa.fec_limite_selecc, sa.cve_control, sa.id_grupo_flujo,";
			sql += "\n	monto_maximo, cupo_manual, cupo_automatico, desc_grupo_cupo,";

			if(!idDivision.equals(""))
				sql += "\n	fecha_propuesta, sa.id_grupo, f.desc_division as desc_grupo_flujo, sa.concepto";
			else
				sql += "\n	fecha_propuesta, sa.id_grupo, f.desc_grupo_flujo, sa.concepto";

			sql += "\n	FROM seleccion_automatica_grupo sa, cat_grupo_cupo c,";

			if(!idDivision.equals(""))
				sql += "\n	cat_division f";
			else
				sql += "\n	cat_grupo_flujo f";

			sql += "\n	WHERE sa.id_grupo = c.id_grupo_cupo";

			if(!idDivision.equals(""))
				sql += "\n	And sa.id_division = f.id_division";
			else
				sql += "\n	And sa.id_grupo_flujo = f.id_grupo_flujo";

			if(idDivision.equals("") && grupoEmpresa != 0)
				sql += "\n	And sa.id_grupo_flujo = " + grupoEmpresa + "";
			else {
				if(idDivision.equals("0"))
					sql += "\n	And (sa.id_division is not null And sa.id_division <> '')";
				else
					sql += "\n	And sa.id_division = " + idDivision + "";
			}
			if(optCupos == 2) {
				if(!fecIni.equals("") && !fecFin.equals(""))
					//sql += "\n	And sa.fecha_propuesta between '" + funciones.ponerFecha(fecIni) + "' And '" + funciones.ponerFecha(fecFin) + "'";
					sql += "\n	And sa.fecha_propuesta between '" + fecIni + "' And '" + fecFin + "'";
				else if(!fecIni.equals(""))
					//sql += "\n	And sa.fecha_propuesta = '" + funciones.ponerFecha(fecIni) + "'";
					sql += "\n	And sa.fecha_propuesta = '" + fecIni + "'";
				else if(!fecFin.equals(""))
					//sql += "\n	And sa.fecha_propuesta = '" + funciones.ponerFecha(fecFin) + "'";
					sql += "\n	And sa.fecha_propuesta = '" + fecFin + "'";
			}else if(optCupos == 0){
				sql += "\n	And sa.fecha_propuesta >= (Select fec_hoy from fechas)";
			}

			query = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();

					cons.setFecLimiteSelecc(rs.getString("fec_limite_selecc"));
					cons.setCveControl(rs.getString("cve_control"));
					cons.setIdGrupoFlujo(rs.getInt("id_grupo_flujo"));
					cons.setMontoMaximo(rs.getDouble("monto_maximo"));
					cons.setCupoManual(rs.getDouble("cupo_manual"));
					cons.setCupoAutomatico(rs.getDouble("cupo_automatico"));
					cons.setDescGrupoCupo(rs.getString("desc_grupo_cupo"));
					cons.setFechaPropuesta(rs.getString("fecha_propuesta"));
					cons.setIdGrupo(rs.getInt("id_grupo"));
					cons.setDescGrupoFlujo(rs.getString("desc_grupo_flujo"));
					cons.setConcepto(rs.getString("concepto"));

					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:buscarCupos");
		}
		return query;
	}

	public int actualizarFolioCupos() {
		String sql = "";
		globalSingleton = GlobalSingleton.getInstancia();
		int iResp = 0;

		try {
			sql += "UPDATE cat_usuario SET folio_control = folio_control + 1 ";
			sql += "WHERE no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + "";

			iResp = jdbcTemplate.update(sql);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:actualizarFolioCupos");
		}
		return iResp;
	}

	public int seleccionarFolioCupos(){
		String sql = "";
		globalSingleton = GlobalSingleton.getInstancia();
		int iResp = 0;

		try {
			sql += "SELECT folio_control FROM cat_usuario ";
			sql += "WHERE no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + "";

			iResp = jdbcTemplate.queryForInt(sql);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:seleccionarFolioCupos");
		}
		return iResp;
	}

	public Date obtenerFechaHoy(){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.obtenerFechaHoy();
	}

	public int ejecutarAltaRegistro(List<ConfirmacionCargoCtaDto> listDatos, boolean esDivision, String sCveControl) {
		String sql = "";
		double dCupoTotal = 0;
		dCupoTotal = listDatos.get(0).getCupoManual() + listDatos.get(0).getCupoAutomatico();
		int iResp = 0;

		try {
			sql += "INSERT INTO seleccion_automatica_grupo(";
			sql += "\n	id_grupo_flujo, monto_maximo, cupo_manual, cupo_automatico, id_grupo, fecha_propuesta,";
			sql += "\n	cupo_total, fec_limite_selecc, cve_control, concepto, id_division)";
			sql += "\n	VALUES( ";

			if(esDivision)
				sql += "-1, ";
			else
				sql += " " + listDatos.get(0).getIdGrupoFlujo() + ", ";

			sql += " " + listDatos.get(0).getMontoMaximo() + ", " + listDatos.get(0).getCupoManual() + ", " + listDatos.get(0).getCupoAutomatico() + ",";
			sql += "\n	" + listDatos.get(0).getIdGrupo() + ", '" + listDatos.get(0).getFechaPropuesta() + "', " + dCupoTotal + ",";
			sql += "\n  '" + listDatos.get(0).getFecLimiteSelecc() + "', '" + sCveControl + "', '" + listDatos.get(0).getConcepto() + "',";

			if(esDivision)
				sql += "\n	" + listDatos.get(0).getIdGrupoFlujo() + ")";
			else
				sql += "\n	null)";

			iResp = jdbcTemplate.update(sql);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:ejecutarAltaRegistro");
			return 0;
		}
		return iResp;
	}

	public int ejecutarUpdateRegistro(List<ConfirmacionCargoCtaDto> listDatos, boolean esDivision, int i) {
		String sql = "";
		int iResp = 0;
		double dCupoTotal = 0;
		dCupoTotal = listDatos.get(i).getCupoManual() + listDatos.get(i).getCupoAutomatico();

		try {
			sql += "UPDATE seleccion_automatica_grupo SET monto_maximo = " + listDatos.get(i).getMontoMaximo() + ",";
			sql += "\n	cupo_manual = " + listDatos.get(i).getCupoManual() + ", cupo_automatico = " + listDatos.get(i).getCupoAutomatico() + ",";
			sql += "\n	cupo_total = " + dCupoTotal + ", concepto = '" + listDatos.get(i).getConcepto() + "'";
			sql += "\n	WHERE ";

			if(esDivision)
				sql += "	id_division = " + listDatos.get(i).getIdGrupoFlujo() + "";
			else
				sql += "	id_grupo_flujo = " + listDatos.get(i).getIdGrupoFlujo() + "";

			sql += "\n		And id_grupo = " + listDatos.get(i).getIdGrupo() + "";
			//sql += "\n		And fecha_propuesta = '" + listDatos.get(i).getFechaPropuesta() + "'";
			sql += "\n		And cve_control = '" + listDatos.get(i).getCveControl() + "'";

			iResp = jdbcTemplate.update(sql);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:ejecutarUpdateRegistro");
			return 0;
		}
		return iResp;
	}

	public int existenMovtosSelAutoGrupo(List<ConfirmacionCargoCtaDto> listDatos, int i, String sTabla) {
		String sql = "";
		int iResp = 0;

		try {
			sql += "SELECT count(m.no_folio_det) as Movtos";
			sql += "\n	FROM " + sTabla + " m, grupo_empresa g ";
			sql += "\n	WHERE g.id_grupo_flujo = " + listDatos.get(i).getIdGrupoFlujo() + "";
			sql += "\n		And m.no_empresa = g.no_empresa";
			sql += "\n		And m.cve_control = '" + listDatos.get(i).getCveControl() + "' ";

			iResp = jdbcTemplate.queryForInt(sql);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:existenMovtosSelAutoGrupo");
			return 0;
		}
		return iResp;
	}

	public int deleteMovtosSelAutoGrupo(List<ConfirmacionCargoCtaDto> listDatos, int i) {
		String sql = "";
		int iResp = 0;

		try {
			sql += "DELETE FROM seleccion_automatica_grupo";
			sql += "\n	WHERE id_grupo_flujo = " + listDatos.get(i).getIdGrupoFlujo() + "";
			sql += "\n		And cve_control = '" + listDatos.get(i).getCveControl() + "' ";

			iResp = jdbcTemplate.update(sql);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:deleteMovtosSelAutoGrupo");
			return 0;
		}
		return iResp;
	}

	//Para la pantalla de pago de propuestas automatico
	public List<ConfirmacionCargoCtaDto> comboCveControl(int gpoEmpresa, int grupo, int idDivision) {
		String sql = "";
		String gsDBM = ConstantesSet.gsDBM;
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		funciones = new Funciones();
		List<ConfirmacionCargoCtaDto> query = new ArrayList<ConfirmacionCargoCtaDto>();

		try {
			if(gsDBM.equals("ORACLE"))
				sql += "SELECT cve_control as ID, cve_control + '   ' + convert(varchar,cupo_total) as DESCC";
			else if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE"))
				sql += "SELECT cve_control as ID, cve_control + '   ' + convert(varchar, cupo_total) as DESCC";
			else if(gsDBM.equals("POSTGRESQL"))
				sql += "SELECT cve_control as ID, cve_control + '   ' + convert(varchar,cupo_total,'99999999999999') as DESCC";

			sql += "\n	FROM seleccion_automatica_grupo";
			sql += "\n	WHERE";

			if(idDivision == 0)
				sql += "\n	id_grupo_flujo = " + gpoEmpresa + "";
			else
				sql += "\n	id_division = " + idDivision + "";

			sql += "\n	And id_grupo = " + grupo + "";
			sql += "\n	And cupo_total > 0";
			sql += "\n	And fec_limite_selecc >= '" + funciones.cambiarFecha("" + consultasGenerales.obtenerFechaHoy(), true) + "'";
			System.out.println(sql.toString());
			query = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();

					cons.setCveControl(rs.getString("ID"));
					cons.setDescCveControl(rs.getString("DESCC"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:comboCveControl");
		}
		return query;
	}

	public List<ConfirmacionCargoCtaDto> comboClaveOperacion() {
		String sql = "";
		List<ConfirmacionCargoCtaDto> query = new ArrayList<ConfirmacionCargoCtaDto>();

		try {
			sql += "SELECT id_tipo_operacion as ID, desc_tipo_operacion as DESCC";
			sql += "\n	FROM cat_tipo_operacion";
			sql += "\n	WHERE id_tipo_operacion >= 3000 And id_tipo_operacion <= 3099";

			query = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();

					cons.setIdValor(rs.getString("ID"));
					cons.setDescValor(rs.getString("DESCC"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:comboClaveOperacion");
		}
		return query;
	}

	public List<ConfirmacionCargoCtaDto> comboBancoReceptor() {
		String sql = "";
		List<ConfirmacionCargoCtaDto> query = new ArrayList<ConfirmacionCargoCtaDto>();

		try {
			sql += "SELECT id_banco as ID, desc_banco as DESCC";
			sql += "\n	FROM cat_banco";
			sql += "\n	WHERE id_banco in (select id_banco_benef from movimiento where id_tipo_operacion = 3000 and id_Estatus_mov in ('N', 'C'))";

			query = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();

					cons.setIdValor(rs.getString("ID"));
					cons.setDescValor(rs.getString("DESCC"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:comboBancoReceptor");
		}
		return query;
	}

	public List<ConfirmacionCargoCtaDto> comboBancoPagador(int gpoEmpresa) {
		String sql = "";
		List<ConfirmacionCargoCtaDto> query = new ArrayList<ConfirmacionCargoCtaDto>();

		try {
			sql += "SELECT id_banco as ID, desc_banco as DESCC";
			sql += "\n	FROM cat_banco";
			sql += "\n	WHERE id_banco in (select id_banco from cat_cta_banco where no_empresa in (";
			sql += "\n		select no_empresa from grupo_empresa where id_grupo_flujo = " + gpoEmpresa + "))";

			query = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();

					cons.setIdValor(rs.getString("ID"));
					cons.setDescValor(rs.getString("DESCC"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:comboBancoPagador");
		}
		return query;
	}

	public List<ConfirmacionCargoCtaDto> comboChequeraPagadora(int gpoEmpresa, int idBcoPag) {
		String sql = "";
		List<ConfirmacionCargoCtaDto> query = new ArrayList<ConfirmacionCargoCtaDto>();

		try {
			sql += "SELECT id_chequera as ID, id_chequera as DESCC";
			sql += "\n	FROM cat_cta_banco";
			sql += "\n	WHERE id_banco = " + idBcoPag + " And no_empresa in (";
			sql += "\n		select no_empresa from grupo_empresa where id_grupo_flujo = " + gpoEmpresa + ")";

			query = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>() {
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();

					cons.setIdValor(rs.getString("ID"));
					cons.setDescValor(rs.getString("DESCC"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:comboChequeraPagadora");
		}
		return query;
	}

	public List<ConfirmacionCargoCtaDto> comboCaja() {
		String sql = "";
		List<ConfirmacionCargoCtaDto> query = new ArrayList<ConfirmacionCargoCtaDto>();

		try {
			sql += "SELECT id_caja as ID, desc_caja as DESCC FROM cat_caja";

			query = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>() {
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();

					cons.setIdValor(rs.getString("ID"));
					cons.setDescValor(rs.getString("DESCC"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:comboCaja");
		}
		return query;
	}

	public List<ConfirmacionCargoCtaDto> comboOrigenMovto() {
		String sql = "";
		List<ConfirmacionCargoCtaDto> query = new ArrayList<ConfirmacionCargoCtaDto>();

		try {
			sql += " SELECT origen_mov as ID, desc_origen_mov as DESCC ";
			sql += "\n	FROM cat_origen_mov";
			sql += "\n	ORDER BY desc_origen_mov";

			query = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>() {
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();

					cons.setIdValor(rs.getString("ID"));
					cons.setDescValor(rs.getString("DESCC"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:comboOrigenMovto");
		}
		return query;
	}

	public List<ConfirmacionCargoCtaDto> comboDivision(int gpoEmpresa) {
		String sql = "";
		List<ConfirmacionCargoCtaDto> query = new ArrayList<ConfirmacionCargoCtaDto>();

		try {
			sql += " SELECT id_division as ID, desc_division as DESCC ";
			sql += "\n	FROM cat_division ";
			sql += "\n	WHERE no_empresa in (";
			sql += "\n		select distinct no_empresa from grupo_empresa where id_grupo_flujo = " + gpoEmpresa + ")";
			sql += "\n	ORDER BY desc_division";

			query = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>() {
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();

					cons.setIdValor(rs.getString("ID"));
					cons.setDescValor(rs.getString("DESCC"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:comboDivision");
		}
		return query;
	}


	public List<ConfirmacionCargoCtaDto> comboFormaPago() {
		String sql = "";
		List<ConfirmacionCargoCtaDto> query = new ArrayList<ConfirmacionCargoCtaDto>();

		try {
			sql += " SELECT id_forma_pago as ID, desc_forma_pago as DESCC ";
			sql += "\n	FROM cat_forma_pago";
			sql += "\n	WHERE id_forma_pago in (1,3,5,9)";

			query = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>() {
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();

					cons.setIdValor(rs.getString("ID"));
					cons.setDescValor(rs.getString("DESCC"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:comboFormaPago");
		}
		return query;
	}


	public List<ConfirmacionCargoCtaDto> comboDivisa() {
		String sql = "";
		List<ConfirmacionCargoCtaDto> query = new ArrayList<ConfirmacionCargoCtaDto>();

		try {
			sql += " SELECT id_divisa as ID, desc_divisa as DESCC ";
			sql += "\n	FROM cat_divisa";
			sql += "\n	WHERE clasificacion = 'D'";
			sql += "\n		AND id_divisa <> 'CNT'";

			query = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>() {
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();

					cons.setIdValor(rs.getString("ID"));
					cons.setDescValor(rs.getString("DESCC"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:comboDivisa");
		}
		return query;
	}


	public List<ConfirmacionCargoCtaDto> comboRubroMovto(int gpoEmpresa) {
		String sql = "";
		String gsDBM = ConstantesSet.gsDBM;
		globalSingleton = GlobalSingleton.getInstancia();
		List<ConfirmacionCargoCtaDto> query = new ArrayList<ConfirmacionCargoCtaDto>();

		try {
			sql += " SELECT DISTINCT m.id_rubro as ID,";

			if(gsDBM.equals("SQL SERVER"))
				sql += " (cast(m.id_rubro as varchar) + ' - ' + r.desc_rubro) as DESCC";
			else if(gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2"))
				sql += " (m.id_rubro || ' - ' || r.desc_rubro) as DESCC";
			else if(gsDBM.equals("SYBASE"))
				sql += " (convert(varchar, m.id_rubro) + ' - ' + r.desc_rubro) as DESCC";


			sql += "\n	FROM movimiento m, cat_rubro r, grupo_empresa ge";
			sql += "\n	WHERE m.id_tipo_operacion = 3000";
			sql += "\n		And m.id_rubro = r.id_rubro";
			sql += "\n		And m.id_grupo = r.id_grupo";
			sql += "\n		And (m.origen_mov not in ('INV','FIL') or m.origen_mov is null)";
			sql += "\n		And m.id_estatus_mov in ('N','C','F')";
			sql += "\n		And m.no_empresa in (SELECT no_empresa FROM usuario_empresa WHERE no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + ")";
			sql += "\n		And m.no_empresa = ge.no_empresa";
			sql += "\n		And (((select count(*) from usuario_area WHERE no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + ") = 0) or";
			sql += "\n			(id_area in (select id_area from usuario_area where no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + ")))";

			if(gpoEmpresa != 0)
				sql += "\n	And ge.id_grupo_flujo = " + gpoEmpresa + "";

			sql += "\n	ORDER BY m.id_rubro";

			query = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>() {
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();

					cons.setIdValor(rs.getString("ID"));
					cons.setDescValor(rs.getString("DESCC"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:comboRubroMovto");
		}
		return query;
	}


	public List<ConfirmacionCargoCtaDto> selectAutomaticaGpo(int gpoEmpresa, int grupo, String cveControl, int idDivision, String idDivisa) {
		String sql = "";
		double tipoCambio;
		String fecHoy = "";

		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Date fecHoyDate;

		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		List<ConfirmacionCargoCtaDto> query = new ArrayList<ConfirmacionCargoCtaDto>();

		try {
			fecHoy = "" + consultasGenerales.obtenerFechaHoy();
			fecHoy = fecHoy.substring(8,10) + "/" + fecHoy.substring(5,7) + "/" + fecHoy.substring(0,4);
			fecHoyDate = df.parse(fecHoy);

			tipoCambio = consultasGenerales.obtenerTipoCambio(idDivisa, fecHoyDate);

			sql += "SELECT id_grupo_flujo, cast((monto_maximo / "+ tipoCambio +") as numeric(15, 2)) as monto_maximo,";
			sql += "	cast(((cupo_manual + cupo_automatico) / "+ tipoCambio +") as numeric(15, 2)) as cupo_total,";
			sql += "	coalesce(fecha_propuesta, '') as fecha_propuesta,";
			sql += "	cast((cupo_automatico / "+ tipoCambio +") as numeric(15, 2)) as cupo_automatico,";
			sql += "	cast((cupo_manual / "+ tipoCambio +") as numeric(15, 2)) as cupo_manual";
			sql += "\n	FROM seleccion_automatica_grupo";
			sql += "\n	WHERE cve_control = '" + cveControl + "'";

			if(idDivision == 0)
				sql += "\n	And id_grupo_flujo = " + gpoEmpresa + "";
			else
				sql += "\n	And id_division = " + idDivision + "";

			sql += "\n	And id_grupo = " + grupo + "";

			query = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>() {
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();

					cons.setIdGrupoFlujo(rs.getInt("id_grupo_flujo"));
					cons.setMontoMaximo(rs.getDouble("monto_maximo"));
					cons.setCupoTotal(rs.getDouble("cupo_total"));
					cons.setCupoAutomatico(rs.getDouble("cupo_automatico"));
					cons.setFechaPropuesta(rs.getString("fecha_propuesta"));
					cons.setCupoManual(rs.getDouble("cupo_manual"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:selectAutomaticaGpo");
		}
		return query;
	}

	public String consultarConfiguraSet(int indice) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
	}


	public List<ConfirmacionCargoCtaDto> selectAutorizacionProp(ConfirmacionCargoCtaDto dto) {
		String sql = "";
		List<ConfirmacionCargoCtaDto> query = new ArrayList<ConfirmacionCargoCtaDto>();

		try {
			if(dto.getIdDivision().equals("0")) {
				sql += "SELECT coalesce(sag.usuario_uno, 0) as usuario_uno, coalesce(sag.usuario_dos, 0) as usuario_dos,";
				sql += "\n	coalesce(sag.usuario_tres, 0) as usuario_tres, coalesce(cgf.nivel_autorizacion, 0) as nivel_autorizacion";
				sql += "\n	FROM seleccion_automatica_grupo sag, cat_grupo_flujo cgf";
				sql += "\n	WHERE sag.id_grupo_flujo = " + dto.getIdGrupoFlujo() + "";
				sql += "\n		And sag.id_grupo = " + dto.getIdGrupo() + "";
				sql += "\n		And sag.cve_control = '" + dto.getCveControl() + "'";
				sql += "\n		And sag.id_grupo_flujo = cgf.id_grupo_flujo";
			}else {
				sql += "SELECT coalesce(sag.usuario_uno, 0) as usuario_uno, coalesce(sag.usuario_dos, 0) as usuario_dos,";
				sql += "\n	coalesce(sag.usuario_tres, 0) as usuario_tres, 0 as nivel_autorizacion";
				sql += "\n	FROM seleccion_automatica_grupo";
				sql += "\n	WHERE sag.id_grupo = " + dto.getIdGrupo() + "";
				sql += "\n		And sag.cve_control = '" + dto.getCveControl() + "'";
			}

			query = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>() {
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();

					cons.setUsuarioUno(rs.getInt("usuario_uno"));
					cons.setUsuarioDos(rs.getInt("usuario_dos"));
					cons.setUsuarioTres(rs.getInt("usuario_tres"));
					cons.setNivelAutorizacion(rs.getInt("nivel_autorizacion"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:selectAutorizacionProp");
		}
		return query;
	}


	public List<ConfirmacionCargoCtaDto> selectPagosAutomaticos(ConfirmacionCargoCtaDto dto, List<Map<String, String>> paramsGrid){
		String sql = "";
		String gsDBM = ConstantesSet.gsDBM;
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		globalSingleton = GlobalSingleton.getInstancia();
		List<ConfirmacionCargoCtaDto> query = new ArrayList<ConfirmacionCargoCtaDto>();

		try {
			fecHoy = "" + consultasGenerales.obtenerFechaHoy();
			fecHoy = fecHoy.substring(8,10) + "/" + fecHoy.substring(5,7) + "/" + fecHoy.substring(0,4);

			for(int i=1; i<=2; i++) {
				if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE")) {

					sql += "\n  SELECT s.nom_empresa, m.fec_valor, m.importe, m.no_cliente, m.beneficiario, m.id_rubro, m.id_divisa, ";
					sql += "\n      CASE WHEN m.id_divisa = 'MN' THEN m.importe ELSE m.importe * COALESCE( ";
					sql += "\n              ( SELECT DISTINCT vd.valor ";
					sql += "\n                FROM valor_divisa vd ";
					sql += "\n                WHERE vd.id_divisa = m.id_divisa ";
					sql += "\n                    and vd.fec_divisa = '" + fecHoy + "'), 1) ";
					sql += "\n      END as importe_mn, ";
					sql += "\n      m.fec_propuesta, m.id_forma_pago, fp.desc_forma_pago, ";
					sql += "\n      m.fec_modif, m.no_folio_det, m.id_chequera_benef, ";
					sql += "\n      m.no_docto, m.no_factura, m.concepto, " + i + " as Tipo, m.id_contable ";
					sql += "\n  FROM movimiento m ";

					if(dto.isBChequera())
						sql += "\n  , cat_banco b, ";
					else
						sql += "\n  LEFT JOIN cat_banco b ON (m.id_banco_benef = b.id_banco), ";

					if(!dto.getIdDivision().equals("0"))
						sql += "\n      cat_forma_pago fp, empresa s ";
					else
						sql += "\n      cat_forma_pago fp, grupo_empresa g, empresa s ";

					if(dto.isChkProvServ()) sql += "\n ,proveedor pr, persona p ";

					if(!dto.getIdDivision().equals("0")){
						sql += "\n  WHERE m.division = '" + dto.getIdDivision() + "' ";
						sql += "\n  and m.no_empresa = s.no_empresa ";
					}else {
						sql += "\n  WHERE g.id_grupo_flujo = '" + dto.getIdGrupoFlujo() + "'";
						sql += "\n  and m.no_empresa = g.no_empresa ";
						sql += "\n  and g.no_empresa = s.no_empresa ";

						if(consultarConfiguraSet(240).equals("SI")) sql += "\n  and ( m.division is null or division = '' )  ";
					}
					if(dto.isBChequera()) sql += "\n  and m.id_banco_benef = b.id_banco ";

					sql += "\n      and m.id_tipo_movto = 'E' ";
					sql += "\n      and (m.origen_mov not in ('INV','FIL') or m.origen_mov is null) ";
					sql += "\n      and m.id_estatus_mov in ('N','C','F') ";
					sql += "\n      and m.id_forma_pago = fp.id_forma_pago ";

					if(i == 1) {
						sql += "\n  and ((m.id_forma_pago = 3 and m.id_leyenda <> '*') or ";
						sql += "\n       (m.id_forma_pago in (1,5,9))) ";
					}else
						sql += "\n  and m.id_leyenda = '*' ";

					sql += "\n      and m.no_folio_det in ";
					sql += "\n          ( SELECT mm.folio_ref ";
					sql += "\n            FROM movimiento mm ";
					sql += "\n            WHERE mm.folio_ref = m.no_folio_det ";
					sql += "\n                and mm.id_rubro in ";
					sql += "\n                    ( SELECT id_rubro ";
					sql += "\n                      FROM grupo_flujo_cupo ";
					sql += "\n                      WHERE id_grupo_cupo = '" + dto.getIdGrupo() + "'))";

					if(dto.isChkProvServ()) {
						sql += "\n  and pr.no_empresa = p.no_empresa ";
						sql += "\n  and pr.no_proveedor = p.no_persona ";
						sql += "\n  and pr.b_servicios = 'S' ";
					}
					if(dto.isChkPropuestos())
						sql += "\n  and m.fec_propuesta is null ";
					else {
						sql += "\n  and m.fec_propuesta is not null ";
						sql += "\n  and m.cve_control = '" + dto.getCveControl() + "'";
					}
				}else if(gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2")) {
					sql += "\n  SELECT s.nom_empresa, m.fec_valor, m.importe, m.no_cliente, m.beneficiario, m.id_rubro, m.id_divisa, ";
					sql += "\n      CASE WHEN m.id_divisa = 'MN' THEN m.importe ELSE m.importe * COALESCE( ";
					sql += "\n              ( SELECT DISTINCT vd.valor ";
					sql += "\n                FROM valor_divisa vd ";
					sql += "\n                WHERE vd.id_divisa = m.id_divisa ";
					sql += "\n                    and vd.fec_divisa = '" + fecHoy + "'), 1) ";
					sql += "\n      END as importe_mn, ";
					sql += "\n      m.fec_propuesta, m.id_forma_pago, fp.desc_forma_pago, ";
					sql += "\n      m.fec_modif, m.no_folio_det, m.id_chequera_benef, ";
					sql += "\n      m.no_docto, m.no_factura, m.concepto, " + i + " as Tipo, m.id_contable ";
					sql += "\n  FROM movimiento m ";

					if(dto.isBChequera())
						sql += "\n  , cat_banco b, ";
					else
						sql += "\n  LEFT JOIN cat_banco b ON (m.id_banco_benef = b.id_banco), ";

					if(!dto.getIdDivision().equals("0"))
						sql += "\n      cat_forma_pago fp, empresa s ";
					else
						sql += "\n      cat_forma_pago fp, grupo_empresa g, empresa s ";

					if(dto.isChkProvServ()) sql += "\n ,proveedor pr, persona p ";

					if(!dto.getIdDivision().equals("0")){
						sql += "\n  WHERE m.division = '" + dto.getIdDivision() + "' ";
						sql += "\n  and m.no_empresa = s.no_empresa ";
					}else {
						sql += "\n  WHERE g.id_grupo_flujo = '" + dto.getIdGrupoFlujo() + "'";
						sql += "\n  and m.no_empresa = g.no_empresa ";
						sql += "\n  and g.no_empresa = s.no_empresa ";

						if(consultarConfiguraSet(240).equals("SI")) sql += "\n  and ( m.division is null or division = '' )  ";
					}
					if(dto.isBChequera()) sql += "\n  and m.id_banco_benef = b.id_banco ";

					sql += "\n      and m.id_tipo_movto = 'E' ";
					sql += "\n      and (m.origen_mov not in ('INV','FIL') or m.origen_mov is null) ";
					sql += "\n      and m.id_estatus_mov in ('N','C','F') ";
					sql += "\n      and m.id_forma_pago = fp.id_forma_pago ";

					if(i == 1) {
						sql += "\n  and ((m.id_forma_pago = 3 and m.id_leyenda <> '*') or ";
						sql += "\n       (m.id_forma_pago in (1,5,9))) ";
					}else
						sql += "\n  and m.id_leyenda = '*' ";

					sql += "\n      and m.no_folio_det in ";
					sql += "\n          ( SELECT mm.folio_ref ";
					sql += "\n            FROM movimiento mm ";
					sql += "\n            WHERE mm.folio_ref = m.no_folio_det ";
					sql += "\n                and mm.id_rubro in ";
					sql += "\n                    ( SELECT id_rubro ";
					sql += "\n                      FROM grupo_flujo_cupo ";
					sql += "\n                      WHERE id_grupo_cupo = '" + dto.getIdGrupo() + "'))";

					if(dto.isChkProvServ()) {
						sql += "\n  and pr.no_empresa = p.no_empresa ";
						sql += "\n  and pr.no_proveedor = p.no_persona ";
						sql += "\n  and pr.b_servicios = 'S' ";
					}
					if(dto.isChkPropuestos())
						sql += "\n  and m.fec_propuesta is null ";
					else {
						sql += "\n  and m.fec_propuesta is not null ";
						sql += "\n  and m.cve_control = '" + dto.getCveControl() + "'";
					}
				}
				sql += validaCriteriosGrid(paramsGrid, dto.getFechaPropuesta());

				if(i == 1) sql += "\n  UNION ALL "; 
			}
			if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE")) sql += "\n	ORDER BY m.fec_valor";
			System.out.println("같같같같같같같같같같같같같같같같같같같같같같같같같같같같같같�||||||||||");
			System.out.println(sql.toString());

			query = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>() {
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();

					cons.setImporteOriginal(rs.getDouble("importe_original"));
					cons.setNomEmpresa(rs.getString("nom_empresa"));
					cons.setDescEstatus(rs.getString("desc_estatus"));
					cons.setNoFactura(rs.getString("no_factura"));
					cons.setNoDocto(rs.getString("no_docto"));
					cons.setFecValor(rs.getDate("fec_valor"));
					cons.setDescCveOperacion(rs.getString("desc_cve_operacion"));
					cons.setIdDivisa(rs.getString("id_divisa"));
					cons.setIdFormaPago(rs.getInt("id_forma_pago"));
					cons.setImporte(rs.getDouble("importe"));
					cons.setDescFormaPago(rs.getString("desc_forma_pago"));
					cons.setDescBanco(rs.getString("desc_banco"));
					cons.setIdChequeraBenef(rs.getString("id_chequera_benef"));
					cons.setBeneficiario(rs.getString("beneficiario"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setNoFolioDet(rs.getInt("no_folio_det"));
					cons.setNoFolioMov(rs.getInt("no_folio_mov"));
					cons.setNoCuenta(rs.getString("no_cuenta"));
					cons.setIdChequera(rs.getString("id_chequera"));
					cons.setIdCveOperacion(rs.getString("id_cve_operacion"));
					cons.setIdFormaPago(rs.getInt("id_forma_pago"));
					cons.setIdBancoBenef(rs.getInt("id_banco_benef"));
					cons.setIdCaja(rs.getInt("id_caja"));
					cons.setIdLeyenda(rs.getString("id_leyenda"));
					cons.setIdEstatusMov(rs.getString("id_estatus_mov"));
					cons.setNoCliente(rs.getString("no_cliente"));
					cons.setTipoCambio(rs.getDouble("tipo_cambio"));
					cons.setOrigenMov(rs.getString("origen_mov"));
					cons.setSolicita(rs.getString("solicita"));
					cons.setAutoriza(rs.getString("autoriza"));
					cons.setObservacion(rs.getString("observacion"));
					cons.setLoteEntrada(rs.getInt("lote_entrada"));
					cons.setDescCaja(rs.getString("desc_caja"));
					cons.setEquivalePersona(rs.getString("equivale_persona"));
					cons.setAgrupa1(rs.getInt("agrupa1"));
					cons.setAgrupa2(rs.getInt("agrupa2"));
					cons.setAgrupa3(rs.getInt("agrupa3"));
					cons.setIdRubro(rs.getInt("id_rubro"));
					cons.setNoPedido(rs.getInt("no_pedido"));
					cons.setImporteMn(rs.getDouble("importe_mn"));
					cons.setNomProveedor(rs.getString("nom_proveedor"));
					cons.setFecPropuesta(rs.getString("fec_propuesta"));
					cons.setFecModif(rs.getString("fec_modif"));
					cons.setTipo(rs.getInt("tipo"));
					cons.setIdContable(rs.getString("id_contable"));

					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:selectPagosAutomaticos");
		}
		return query;
	}

	public String validaCriteriosGrid(List<Map<String, String>> paramsGrid, String fecPropuesta) {
		String sql = "";
		String docto1 = "";
		String docto2 = "";
		boolean pbFiltroFecha = false;

		try {
			if(paramsGrid.size() > 0) {
				docto1 = "";
				docto2 = "";
				//fecPropuesta = fecPropuesta.substring(8,10) + "/" + fecPropuesta.substring(5,7) + "/" + fecPropuesta.substring(0,4);

				if(!paramsGrid.get(0).get("chequeraBenef").equals("")) { 
					if(paramsGrid.get(0).get("chequeraBenef").equals("NACIONAL"))
						sql += "\n	And b.nac_ext = 'N'";
					else
						sql += "\n	And b.nac_ext = 'E'";
				}
				if(!paramsGrid.get(0).get("numDocto").equals("")) {
					docto1 = paramsGrid.get(0).get("numDocto").substring(0, paramsGrid.get(0).get("numDocto").indexOf(",")).trim();
					docto2 = paramsGrid.get(0).get("numDocto").substring(paramsGrid.get(0).get("numDocto").indexOf(",") + 1).trim();

					sql += "\n	And m.no_docto between '" + docto1 + "' and '" + docto2 + "'";
					pbFiltroFecha = true;
				}
				if(!paramsGrid.get(0).get("fechas").equals("")) {
					docto1 = paramsGrid.get(0).get("fechas").substring(0, paramsGrid.get(0).get("fechas").indexOf(",")).trim();
					docto2 = paramsGrid.get(0).get("fechas").substring(paramsGrid.get(0).get("fechas").indexOf(",") + 1).trim();

					if(!docto1.equals("") && !docto2.equals(""))
						sql += "\n	And m.fec_valor between '" + docto1 + "' and '" + docto2 + "'";
					else if(!fecPropuesta.equals(""))
						sql += "\n	And m.fec_valor <= '" + fecPropuesta + "'";
					pbFiltroFecha = true;
				}
				if(!paramsGrid.get(0).get("estatus").equals("")) {
					if(paramsGrid.get(0).get("estatus").equals("CAPTURADO"))
						sql += "\n	And m.id_estatus_mov = 'C'";
					else if(paramsGrid.get(0).get("estatus").equals("FILIAL"))
						sql += "\n	And m.id_estatus_mov = 'F'";
					else if(paramsGrid.get(0).get("estatus").equals("IMPORTADO"))
						sql += "\n	And m.id_estatus_mov = 'N'";
				}
				if(!paramsGrid.get(0).get("montos").equals("")) {
					docto1 = paramsGrid.get(0).get("montos").substring(0, paramsGrid.get(0).get("montos").indexOf(",")).trim();
					docto2 = paramsGrid.get(0).get("montos").substring(paramsGrid.get(0).get("montos").indexOf(",") + 1).trim();

					if(!docto1.equals("") && !docto2.equals(""))
						sql += "\n	And m.importe between " + docto1 + " and " + docto2 + "";
					else if(!docto1.equals("") && docto2.equals(""))
						sql += "\n	And m.importe <= " + docto1 + "";
					else if(docto1.equals("") && !docto2.equals(""))
						sql += "\n	And m.importe >= " + docto2 + "";
				}
				if(!paramsGrid.get(0).get("bancoRecep").equals(""))
					sql += "\n	And m.id_banco_benef = " + paramsGrid.get(0).get("bancoRecep") + "";
				if(!paramsGrid.get(0).get("bcoPagador").equals(""))
					sql += "\n	And m.id_banco = " + paramsGrid.get(0).get("bcoPagador") + "";
				if(!paramsGrid.get(0).get("chequeraPaga").equals(""))
					sql += "\n	And m.id_chequera = '" + paramsGrid.get(0).get("chequeraPaga") + "'";
				if(!paramsGrid.get(0).get("concepto").equals(""))
					sql += "\n	And m.concepto LIKE '%" + paramsGrid.get(0).get("concepto") + "%'";
				if(!paramsGrid.get(0).get("loteEntrada").equals(""))
					sql += "\n	And m.lote_entrada = '" + paramsGrid.get(0).get("loteEntrada") + "'";
				if(!paramsGrid.get(0).get("caja").equals(""))
					sql += "\n	And m.id_caja = " + paramsGrid.get(0).get("caja") + "";
				if(!paramsGrid.get(0).get("division").equals(""))
					sql += "\n	And m.division = " + paramsGrid.get(0).get("division") + "";
				if(!paramsGrid.get(0).get("formaPago").equals(""))
					sql += "\n	And m.id_forma_pago = " + paramsGrid.get(0).get("formaPago") + "";
				if(!paramsGrid.get(0).get("divisa").equals(""))
					sql += "\n	And m.id_divisa = " + paramsGrid.get(0).get("divisa") + "";
				if(!paramsGrid.get(0).get("claveOpe").equals(""))
					sql += "\n	And m.id_tipo_operacion = " + paramsGrid.get(0).get("claveOpe") + "";
				if(!paramsGrid.get(0).get("bloqueo").equals("")) {
					if(paramsGrid.get(0).get("bloqueo").equals("SI"))
						sql += "\n	And m.cod_bloqueo = 'S'";
					else
						sql += "\n	And (m.cod_bloqueo is null Or m.cod_bloqueo = 'N' Or m.cod_bloqueo = '')";
				}
				if(!paramsGrid.get(0).get("numProveedor").equals("")) {
					docto1 = paramsGrid.get(0).get("numProveedor").substring(0, paramsGrid.get(0).get("numProveedor").indexOf(",")).trim();
					docto2 = paramsGrid.get(0).get("numProveedor").substring(paramsGrid.get(0).get("numProveedor").indexOf(",") + 1).trim();

					sql += "\n	And m.no_cliente in (SELECT no_persona FROM persona WHERE equivale_persona between ";
					sql += "\n	'" + docto1 + "' And '" + docto2 + "' And id_tipo_persona = 'P')";
				}
				if(!paramsGrid.get(0).get("nomProveedor").equals(""))
					sql += "\n	And m.beneficiario LIKE '" + paramsGrid.get(0).get("nomProveedor") + "%'";
				if(!paramsGrid.get(0).get("factura").equals(""))
					sql += "\n	And m.no_factura LIKE '%" + paramsGrid.get(0).get("factura") + "%'";
				if(!paramsGrid.get(0).get("numPedido").equals(""))
					sql += "\n	And m.no_pedido = " + paramsGrid.get(0).get("numPedido") + "";
				if(!paramsGrid.get(0).get("origenMovto").equals("")) {
					if(paramsGrid.get(0).get("origenMovto").equals("CXP"))
						sql += "\n	And (m.origen_mov = '' Or m.origen_mov is null Or m.origen_mov = 'CXP'";
					else
						sql += "\n	And m.no_pedido = '" + paramsGrid.get(0).get("origenMovto") + "'";
				}
				if(!paramsGrid.get(0).get("rubroMovto").equals(""))
					sql += "\n	And m.id_rubro = '" + paramsGrid.get(0).get("rubroMovto") + "'";
				if(!paramsGrid.get(0).get("claseDocto").equals(""))
					sql += "\n	And m.id_contable in ('" + paramsGrid.get(0).get("claseDocto").replace(",", "','").replace(" ", "") + "')";
			}

			if(!pbFiltroFecha) {
				if(!fecPropuesta.equals(""))
					sql += "\n	And m.fec_valor <= convert(datetime,'" + (fecPropuesta) + "',103)";
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:validaCriteriosGrid");
		}
		return sql;
	}  


	public List<ConfirmacionCargoCtaDto> selectEquivaleP(String noPersona) {
		String sql = "";
		List<ConfirmacionCargoCtaDto> query = new ArrayList<ConfirmacionCargoCtaDto>();

		try {
			sql += "\n	SELECT equivale_persona";
			sql += "\n	FROM persona";
			sql += "\n	WHERE no_persona = " + noPersona + "";
			sql += "\n		AND id_tipo_persona in ('P', 'K')";
			sql += "\n		AND no_empresa = 552";

			query = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>() {
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();

					cons.setEquivalePersona(rs.getString("equivale_persona"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:selectEquivaleP");
		}
		return query;
	}

	public Map<String, Object> ejecutarCuadrante(String idUsuario, String fechaPago, String sFolios) {
		StoreParamsComunDto dto = new StoreParamsComunDto();

		dto.setParametro(sFolios);

		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.cambiarCuadrantes(dto);
	}

	public int updateFecPropuesta(String sFolios, String fechaPago, String cveControl) {
		String sql = "";
		int iResp = 0;

		try {
			sql += "UPDATE movimiento SET";
			sql += "\n		fec_propuesta = '" + fechaPago + "',";
			sql += "\n		fec_modif = fec_valor, cve_control = '" + cveControl + "'";
			sql += "\n	WHERE no_folio_det in ('" + sFolios + "')";
			sql += "\n		Or folio_ref in ('" + sFolios + "')";

			//System.out.println(sql);

			iResp = jdbcTemplate.update(sql);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:updateFecPropuesta");
			return 0;
		}
		return iResp;
	}

	public double tipoCambio(String idDivisa) {
		String fecHoy = "";
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Date fecHoyDate;
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);

		try {
			fecHoy = "" + consultasGenerales.obtenerFechaHoy();
			fecHoy = fecHoy.substring(8,10) + "/" + fecHoy.substring(5,7) + "/" + fecHoy.substring(0,4);
			fecHoyDate = df.parse(fecHoy);

			return consultasGenerales.obtenerTipoCambio(idDivisa, fecHoyDate);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:tipoCambio");
			return 0;
		}
	}

	public int updateCupoManualGrupo(boolean suma, double cupoAuto, double dImporteManual, int idGrupo, String fechaPago, String cveControl) {
		String sql = "";
		String sMasMenos = "";
		boolean pbTotales = false;
		String idDivision = "";
		int iResp = 0;

		try {
			if(suma)
				sMasMenos = "+";
			else
				sMasMenos = "-";

			sql += "\n	UPDATE seleccion_automatica_grupo ";
			sql += "\n	SET cupo_manual = cupo_manual " + sMasMenos + " " + dImporteManual + ",";
			sql += "\n		cupo_automatico = cupo_automatico " + sMasMenos + " " + cupoAuto + "";

			if(!pbTotales) {
				sql += "\n	,monto_maximo = monto_maximo " + sMasMenos + " " + dImporteManual + ",";
				sql += "\n	cupo_total = cupo_total " + sMasMenos + " " + dImporteManual + "";
			}
			if(idDivision.equals(""))
				sql += "\n	WHERE id_grupo = " + idGrupo + "";
			else
				sql += "\n	WHERE id_division = '" + idDivision + "'";

			sql += "\n		AND cve_control = '" + cveControl + "'";
			sql += "\n		AND fecha_propuesta = '" + fechaPago + "'";

			//System.out.println(sql);

			iResp = jdbcTemplate.update(sql); 

		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:updateCupoManualGrupo");
			return 0;
		}
		return iResp;
	}


	//Para la pantalla de compra venta de divisas
	public List<ConfirmacionCargoCtaDto> obtenerDivisaVta (int idUsuario) {
		String sql="";

		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{
			sql  += "\n SELECT id_divisa as ID, desc_divisa as Descrip ";
			sql  += "\n   FROM cat_divisa";
			sql  += "\n  WHERE clasificacion = 'D'";

			list = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdDivisa(rs.getString("ID"));
					cons.setDescDivisa(rs.getString("Descrip"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerDivisaVta");
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<ConfirmacionCargoCtaDto> obtenerDivisaVta (int idUsuario, int noEmpresa) {

		String sql = null;
		StringBuilder sbSQL = new StringBuilder();

		try{

			sbSQL.append( "SELECT id_divisa as ID, desc_divisa as Descrip" 	).append( "\n" );
			sbSQL.append( "FROM CAT_DIVISA" 							   	).append( "\n" );
			sbSQL.append( "WHERE clasificacion = 'D'"					   	).append( "\n" );
			sbSQL.append( "AND id_divisa IN("                              	).append( "\n" );
			sbSQL.append( "			SELECT DISTINCT id_divisa"	   		 	).append( "\n" );   
			sbSQL.append( "			FROM CAT_CTA_BANCO" 		   			).append( "\n" );
			sbSQL.append( "			WHERE no_empresa = ?"		  			).append( "\n" );			
			sbSQL.append( "			AND no_empresa in("            			).append( "\n" );
			sbSQL.append( "					 	SELECT no_empresa" 			).append( "\n" );
			sbSQL.append( "						FROM usuario_empresa" 		).append( "\n" );
			sbSQL.append( "						WHERE no_usuario = ?" 		).append( "\n" );
			sbSQL.append( "			)"	   									).append( "\n" );
			sbSQL.append( ")"	   										   	).append( "\n" );


			sql = sbSQL.toString(); 

			return jdbcTemplate.query(sql, new Object[] { noEmpresa, idUsuario }, new RowMapper(){

				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdDivisa(rs.getString("ID"));
					cons.setDescDivisa(rs.getString("Descrip"));
					return cons;
				}
			});

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerDivisaVta");
			return null; 
		}

	}//END METHOD: obtenerDivisaVta

	public List<ConfirmacionCargoCtaDto> obtenerBancoVta (int noEmpresa, String idDivisa) {
		String sql="";

		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{ 



			sql += "\n SELECT id_banco as ID, desc_banco as Descrip ";
			sql += "\n   FROM cat_banco";
			sql += "\n  WHERE id_banco in (SELECT distinct id_banco ";
			sql += "\n                       FROM cat_cta_banco ";
			sql += "\n                      WHERE tipo_chequera in ('P','M') ";
			sql += "\n                        AND no_empresa = " + noEmpresa;
			sql += "\n                        AND id_divisa = '" + idDivisa + "')";


			list = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdBanco(rs.getInt("ID"));
					cons.setDescBanco(rs.getString("Descrip"));
					return cons;
				}
			});   
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerBancoVta");
		}
		return list;
	}

	public List<ConfirmacionCargoCtaDto> obtenerChequeraVta (int noEmpresa, String idDivisa, int idBanco) {
		String sql="";

		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{ 


			sql += "\n SELECT id_chequera as id, id_chequera as descrip";
			sql += "\n   FROM cat_cta_banco ";
			sql += "\n  WHERE tipo_chequera in ('P','M')";
			sql += "\n	  AND no_empresa = " + noEmpresa;
			sql += "\n    AND id_divisa = '" + idDivisa + "'";
			sql += "\n    AND id_banco = " + idBanco;

			list = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdChequera(rs.getString("id"));   
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerChequeraVta");
		}
		return list;
	}


	public List<ConfirmacionCargoCtaDto> obtenerBancoAbo (int noEmpresa, String idDivisa, int radio, int custodia) {
		String sql="";


		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{
			if(radio == 2){
				sql += "\n SELECT id_caja as id, desc_caja as descrip ";
				sql += "\n   FROM cat_caja ";
				sql += "\n  WHERE id_caja in (SELECT v.id_caja ";
				sql += "\n                      FROM valor_custodia v, cat_valor_custodia c";
				sql += "\n                     WHERE c.id_valor_custodia = v.id_chequera ";
				sql += "\n                       AND v.no_empresa = " + noEmpresa;
				if (custodia == 1)
					sql += "\n    AND c.menu_cus = 'S'";
				else
					sql += "\n    AND c.menu_fij = 'S'";

				sql += "\n ) ";

			}else{
				sql +="\n SELECT id_banco as ID, desc_banco as Descrip ";
				sql +="\n   FROM cat_banco";
				sql +="\n  WHERE id_banco in (SELECT distinct id_banco ";
				sql +="\n                       FROM cat_cta_banco ";
				sql +="\n                      WHERE tipo_chequera in ('P','M') ";
				sql +="\n                        AND no_empresa = " + noEmpresa;
				sql +="\n                        AND id_divisa = '" + idDivisa + "')";
			}


			list = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdBanco(rs.getInt("ID"));
					cons.setDescBanco(rs.getString("Descrip"));
					return cons;
				}   
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerBancoAbo");
		}
		return list;
	}

	public List<ConfirmacionCargoCtaDto> obtenerChequeraAbo (int custodia, int radio, int noEmpresa, String idDivisa, int idBanco) {
		String sql="";
		String gsDBM = ConstantesSet.gsDBM;

		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{
			if(radio == 2){					
				if(idBanco >= 0){
					sql +="\n  SELECT DISTINCT c.id_valor_custodia as id, c.desc_valor as descrip ";
					sql +="\n    FROM valor_custodia v, cat_valor_custodia c ";
					sql +="\n   WHERE c.id_valor_custodia = v.id_chequera ";
					sql +="\n     AND v.no_empresa = " + noEmpresa;
					sql +="\n     AND v.id_caja = " + idBanco;

					if (custodia == 1) 
						sql +="\n     AND c.menu_cus = 'S'";
					else
						sql +="\n     AND c.menu_fij = 'S'";



				}

			}else {


				sql +="\n SELECT id_chequera as id, id_chequera as descrip";
				sql +="\n   FROM cat_cta_banco ";

				if (gsDBM.equals("SYBASE")) 
					sql +="\n  WHERE tipo_chequera in ('C','M','P') ";
				else
					sql +="\n  WHERE tipo_chequera in ('C','M') ";


				if ((noEmpresa == 11 || noEmpresa == 1011) && gsDBM.equals("SYBASE")) 
					sql +="\n    AND no_empresa IN (11,1011)";
				else
					sql +="\n    AND no_empresa = " + noEmpresa;


				sql +="\n    AND id_divisa = '" + idDivisa + "'";
				sql +="\n    AND id_banco = " + idBanco;

			}





			list = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdChequera(rs.getString("id"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerChequeraAbo");
		}
		return list;
	}


	public List<ConfirmacionCargoCtaDto> obtenerCasaCambioVta (int idUsuario) {

		String sql= "";
		List<ConfirmacionCargoCtaDto> list = null;

		try{ 
			/*sql +="\n SELECT CCC.NO_PROV_CASA_CAMB AS ID, CCC.NOMBRE AS DESCRIP";
			sql +="\n FROM CAT_CASA_CAMBIO CCC"								 				;
			sql +="\n 							INNER JOIN PERSONA P"			 				;
			sql +="\n 							ON CCC.NO_PROV_CASA_CAMB = P.EQUIVALE_PERSONA"	;
			sql +="\n 							AND P.ID_TIPO_PERSONA = 'K'"					;*/			
			sql += ""; // Sybase
			sql += " SELECT no_persona as ID , razon_social as Descrip ";
			sql += "FROM persona";
			sql += "  WHERE id_tipo_persona = 'K'";

			list = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdCasaCambio  ( rs.getInt("ID")          );   
					cons.setDescCasaCambio( rs.getString("DESCRIP")  );
					//cons.setContacto      ( rs.getString("CONTACTO") );

					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerCasaCambioVta");
		}
		return list;
	}


	public List<ConfirmacionCargoCtaDto> obtenerOperadorVta (int noCliente) {
		String sql="";
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{
			sql +="\n SELECT no_persona as Id, nombre_corto as Descrip ";
			sql +="\n FROM persona ";
			sql +="\n WHERE no_persona in ";
			sql +="\n     ( SELECT no_persona_rel ";
			sql +="\n       FROM relacion ";
			sql +="\n       WHERE no_persona = " + noCliente;
			sql +="\n     ) ";
			sql +="\n    and id_tipo_persona = 'F' ";


			list = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdOperador(rs.getInt("Id"));
					cons.setNomOperador(rs.getString("Descrip"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerOperadorVta");
		}
		return list;
	}

	public List<ConfirmacionCargoCtaDto> obtenerGrupoVta (int idUsuario) {
		String sql="";

		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{
			sql+= "\n select id_grupo as id, desc_grupo as descrip from cat_grupo";


			list = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdGrupo(rs.getInt("id"));
					cons.setDescGrupo(rs.getString("descrip"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerBancos");
		}
		return list;
	}

	public List<ConfirmacionCargoCtaDto> obtenerBanco (int noCliente, String idDivisa) {

		StringBuilder sb = new StringBuilder();
		String sql=null;

		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{ 

			sb.append("SELECT CB.ID_BANCO AS ID, B.DESC_BANCO AS DESCRIP"	).append("\n");
			sb.append("FROM CTAS_BANCO CB"								 	).append("\n");
			sb.append("                  LEFT JOIN CAT_BANCO B"			 	).append("\n");
			sb.append("                  ON CB.ID_BANCO = B.ID_BANCO"	 	).append("\n");
			sb.append("WHERE CB.ID_DIVISA = '" + idDivisa + "'"				).append("\n");
			sb.append("AND CB.NO_PERSONA IN("							 	).append("\n");
			sb.append("             		SELECT NO_PERSONA"			    ).append("\n");
			sb.append("                 	FROM PERSONA"                   ).append("\n");
			sb.append("                 	WHERE EQUIVALE_PERSONA = '" + noCliente + "'"     ).append("\n");
			sb.append("                 	AND ID_TIPO_PERSONA = 'K'"	    ).append("\n");
			sb.append(")"                                                  ).append("\n");
			System.out.println(sb.toString());
			sql = sb.toString();

			list = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdBanco(rs.getInt("ID"));
					cons.setDescBanco(rs.getString("Descrip"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerBanco");
		}
		return list;
	}


	public List<ConfirmacionCargoCtaDto> obtenerRubroVta(int idGrupo) {
		String sql="";

		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{

			sql +="\n SELECT id_rubro as ID, desc_rubro as Descrip";
			sql +="\n   FROM cat_rubro";
			sql +="\n  WHERE id_grupo = " + idGrupo;

			list = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdRubro(rs.getInt("ID"));
					cons.setDescRubro(rs.getString("Descrip"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerRubroVta");
		}
		return list;
	}


	public List<ConfirmacionCargoCtaDto> obtenerChequeraTotal (int noCliente, String idDivisa, int idBanco) {
		String sql="";
		StringBuilder sb = new StringBuilder();
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{

			Object[] params = {idDivisa, idBanco, noCliente};

			sb.append("SELECT CB.ID_CHEQUERA AS ID, CB.ID_CHEQUERA AS DESCRIP"  ).append("\n");
			sb.append("FROM CTAS_BANCO CB"									    ).append("\n");                          
			sb.append("WHERE CB.ID_DIVISA = ?"						    		).append("\n");
			sb.append("AND CB.ID_BANCO = ?"								    	).append("\n");
			sb.append("AND CB.NO_PERSONA IN("								    ).append("\n");
			sb.append("						SELECT NO_PERSONA"				    ).append("\n");
			sb.append("						FROM PERSONA"					    ).append("\n");
			sb.append("						WHERE EQUIVALE_PERSONA = ?"			).append("\n");
			sb.append("						AND ID_TIPO_PERSONA = 'K'"			).append("\n");
			sb.append(")"														).append("\n");

			sql = sb.toString();

			list = jdbcTemplate.query(sql,params,new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdChequera(rs.getString("id"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerChequeraAbo");
		}
		return list;
	}

	public int actualizarFolioReal(String tipoFolio) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.actualizarFolioReal(tipoFolio);
	}

	public int seleccionarFolioReal(String tipoFolio) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.seleccionarFolioReal(tipoFolio);
	}


	public List<ConfirmacionCargoCtaDto> obtenerCajaCuenta(int empresa) {
		String sql;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{
			sql = "select coalesce(id_caja, 0) as id_caja, coalesce(no_cuenta_emp, 0) as no_cuenta_emp from empresa where no_empresa = "+ empresa + " order by no_cuenta_emp"; 

			list = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdCaja(rs.getInt("id_caja"));
					cons.setNoCuenta(rs.getString("no_cuenta_emp"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerCajaCuenta");
		}

		return list;
	}

	public int InsertAceptado(int inst, ConfirmacionCargoCtaDto dto, int noFolioParam, int tipoMovto, int cuentaEmp, int noFolioDocto,
			int idCaja, String concepto, String leyenda, String obervacion, String solicita, String autoriza, int plaza,
			int sucursal, int grupo, int agrupa1, int agrupa2, int agrupa3, boolean ADU, int pedido, boolean CVDivisa, int area,
			String referencia, String sDivision, int piLote, String partida, boolean pbNomina, boolean pbProvAc){

		StringBuffer sql = new StringBuffer();
		String fecHoy = "";
		String fechaValor = "";
		String fechaLiquidacion = "";
		int idGrupo = 0;
		int res = 0;
		String estatus = "L";
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);

		fecHoy = "" + consultasGenerales.obtenerFechaHoy();
		fecHoy = fecHoy.substring(8,10)+"/"+fecHoy.substring(5,7)+"/"+fecHoy.substring(0,4);


		fechaValor = dto.getFechaValor();
		fechaValor = fechaValor.substring(8,10)+"/"+fechaValor.substring(5,7)+"/"+fechaValor.substring(0,4);


		fechaLiquidacion = dto.getFechaLiquidacion();
		fechaLiquidacion = fechaLiquidacion.substring(8,10)+"/"+fechaLiquidacion.substring(5,7)+"/"+fechaLiquidacion.substring(0,4);


		try{
			sql.append("\n INSERT INTO parametro (no_empresa, no_docto, no_folio_param, id_tipo_docto, id_forma_pago, ");
			sql.append("\n      usuario_alta, id_tipo_operacion, no_cuenta, no_factura, fec_valor, fec_valor_original, ");
			sql.append("\n      fec_operacion, fec_alta, id_divisa, id_divisa_original, importe, importe_original, ");
			sql.append("\n      tipo_cambio, id_caja, referencia, beneficiario,");
			sql.append("\n      concepto, id_banco_benef, id_chequera_benef, aplica,");
			sql.append("\n      id_estatus_mov, b_salvo_buen_cobro, id_estatus_reg, id_leyenda, folio_ref, ");
			sql.append("\n      id_chequera, observacion, origen_mov, no_cliente,");
			sql.append("\n      solicita, autoriza, plaza, sucursal, grupo, ");
			sql.append("\n      no_folio_mov, agrupa1, agrupa2, agrupa3, id_rubro, ");
			sql.append("		id_banco");

			if (ADU == true) sql.append("\n ,no_pedido ");

			if(inst != 247){
				if (area != -1) sql.append("\n ,id_area ");

				if (!sDivision.equals("")) sql.append("\n ,division ");

				if (pbNomina && piLote != 0)
					sql.append("\n ,lote ");
				else if ((pbProvAc && piLote == 0) || (pbProvAc && piLote == 1))
					sql.append("\n ,lote ");
				else if (piLote != 0) 
					sql.append("\n ,lote ");

				if(inst == 245 && idGrupo != 0) sql.append("\n ,id_grupo ");

				if(inst == 245) sql.append("\n ,invoice_type");
			}
			sql.append("\n)");

			sql.append("\n VALUES (");
			sql.append("\n " + dto.getNoEmpresa() + ", '" + noFolioDocto + "', " + noFolioParam + ", 41, " + dto.getIdFormaPago() + ",");
			sql.append("\n " + dto.getUsuario() + ", " + tipoMovto + ", " + cuentaEmp + ", '" + noFolioDocto + "', '" + fechaValor + "',");

			if (fechaValor != "")  
				sql.append("\n '" + fechaValor + "',");
			else
				sql.append("\n '" + fecHoy + "',");

			sql.append("\n '" + fecHoy + "', '" + fecHoy + "',");

			if(inst != 247)
				sql.append("'" + dto.getDivisaVenta() + "', '" + dto.getDivisaVenta() + "', " + dto.getImporteVenta() + "," + dto.getImporteVenta() + ",");
			else
				sql.append("'" + dto.getDivisaCompra() + "', '" + dto.getDivisaCompra() + "', " + dto.getImporteCompra() + "," + dto.getImporteCompra() + ",");

			sql.append("\n " + dto.getTipoCambio() + ", " + idCaja + ", '" + referencia + "', '" + dto.getDescCasaCambio() + "', '" + concepto + "', ");

			if(inst != 247)
				sql.append("" + dto.getIdBanco() + ", '" + dto.getIdChequera() + "'");
			else
				sql.append("" + dto.getBancoAbono() + ", '" + dto.getChequeraAbono() + "'");

			sql.append("\n ,1,");

			if(inst == 244) sql.append("\n 'C','S','P','" + leyenda + "', '', ");
			if(inst == 245) sql.append("\n 'H','S','P','" + leyenda + "', 1,");
			if(inst == 247) sql.append("'"+ estatus + "'"+ ",'S','P','" + leyenda + "', 1,");

			if(inst != 247) {
				if (dto.getChequeraCargo().equals(""))
					sql.append("\n NULL");
				else
					sql.append("\n '" + dto.getChequeraCargo()+ "'");
			}else {
				if (dto.getIdChequera().equals(""))  
					sql.append("\n NULL");
				else
					sql.append("\n '" + dto.getIdChequera() + "'");
			}
			sql.append("\n ,'" + dto.getObservacion() + "',");

			if(ADU)  
				sql.append("\n 'ADU'");
			else if(CVDivisa)  
				sql.append("\n 'CVD'");
			else
				sql.append("\n 'SET'");
			sql.append("\n ,'" + dto.getIdCasaCambio() + "',");

			//System.out.println(sql.toString());

			if(inst == 244){
				if (solicita.equals(""))
					sql.append("\n NULL, NULL, ");
				else
					sql.append("\n '" + solicita + "', '" + autoriza + "', ");
			}
			if(inst != 244){
				if(solicita.equals(""))
					sql.append("NULL, ");
				else 
					sql.append("'"+ solicita+ "', ");

				if (autoriza.equals(""))
					sql.append("\n NULL, ");
				else
					sql.append("\n '" + autoriza + "', ");
			}
			sql.append( "\n "+ plaza + "," + sucursal + ", " + grupo + ", 1, " + agrupa1 + ", " + agrupa2 + "," + agrupa3 + "");

			if (dto.getIdRubro() == 0)
				sql.append("\n ,NULL");
			else
				sql.append( ","+ dto.getIdRubro());

			if(inst != 247) 
				sql.append( "," + dto.getBancoCargo());
			else
				sql.append( "," + dto.getIdBanco());

			if (ADU) sql.append("\n ," + pedido);

			//System.out.println(sql.toString());

			if(inst != 247){
				if (area != -1)  
					sql.append("\n ," + area);

				if (sDivision != "")
					sql.append("\n ,'" + sDivision + "'");


				if (pbNomina == true && piLote != 0)  
					sql.append("\n ,3");
				else if ((pbProvAc == true && piLote == 0) || (pbProvAc == true && piLote == 1))  
					sql.append("\n ,4");
				else if (piLote != 0)  
					if(inst == 244)
						sql.append("\n ,2");
					else
						sql.append( "," + piLote);

				if(inst == 245){
					if(idGrupo != 0)
						sql.append( "," + idGrupo);
				}	
				if(inst == 244 && partida != "")      
					sql.append("\n ,'" + partida + "'");

				if (inst == 245)
					sql.append("\n ,'" + partida + "'");
			}
			sql.append("\n)");
			res = jdbcTemplate.update(sql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerBancos");
		}     
		return res;
	}

	public String fecHoy()
	{
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return "" + consultasGenerales.obtenerFechaHoy();
	}


	public Map<String, Object> ejecutarGenerador(GeneradorDto generadorDto)
	{
		System.out.println(" si entre al generador"+generadorDto);
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.ejecutarGenerador(generadorDto);

	}


	//Para la pantalla de compra venta de transferencias


	public List<ConfirmacionCargoCtaDto> claveControl(int idUsuario) {
		String sql="";

		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{
			sql+= "SELECT distinct cve_control as id, cve_control as descripcion";
			sql+= "\n	FROM movimiento";
			sql+= "\n	WHERE no_empresa in (SELECT no_empresa FROM usuario_empresa";
			sql+= "\n						  WHERE no_usuario = " + idUsuario + ")";
			sql+= "\n					   		And cve_control is not null";
			sql+= "\n							And id_estatus_mov = 'V' and (id_forma_pago in(3,5)or (id_forma_pago = 1 and ID_servicio_be = 'CVT'))";


			list = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setNoClaveControl(rs.getString("id"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerBancos");
		}
		return list;
	}

	public List<ConfirmacionCargoCtaDto> claveControl() {

		String sql =null ;
		StringBuilder sb = new StringBuilder();

		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		sb.append( "SELECT DISTINCT SAG.cve_control as id," 			).append( "\n" );
		sb.append( "	SAG.cve_control as descripcion" 	            ).append( "\n" );
		sb.append( "FROM SELECCION_AUTOMATICA_GRUPO SAG" 	            ).append( "\n" );
		sb.append( "	INNER JOIN MOVIMIENTO M"						).append( "\n" );
		sb.append( "		ON SAG.CVE_CONTROL = M.CVE_CONTROL" 		).append( "\n" );                               
		sb.append( "		AND M.ID_TIPO_OPERACION = 3000" 			).append( "\n" );
		sb.append( "		AND coalesce(M.ORIGEN_MOV, '')NOT IN('CVT')").append(" \n" );
		sb.append( "		AND M.ID_FORMA_PAGO = 3" 	        		).append( "\n" );
		sb.append( "		AND M.ID_ESTATUS_MOV = 'V'" 	        	).append( "\n" );
		//sb.append( "WHERE SAG.habilitado IS NOT NULL"       			).append( "\n" );
		sb.append( "	WHERE SAG.fecha_pago IS NULL"       			).append( "\n" );
		//sb.append( "AND SAG.USUARIO_UNO IS NOT NULL"     								    ).append( "\n" );
		System.out.println(sb.toString());
		sql = sb.toString(); 

		list = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
			public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
				ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
				cons.setNoClaveControl(rs.getString("id"));
				return cons;
			}
		});

		return list;
	}


	public List<ConfirmacionCargoCtaDto> DivisaLlenado (String noClaveControl) {
		String sql="";
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{
			sql+= "SELECT id_divisa as ID, desc_divisa as descrip";
			sql+= "\n	FROM cat_divisa";
			sql+= "\n	WHERE clasificacion = 'D'";
			sql+= "\n	 AND id_divisa <> 'CNT'";
			sql+= "\n	 And id_divisa in (select id_divisa from movimiento where cve_control = '" + noClaveControl + "'";
			sql+= "\n	 And id_estatus_mov = 'V')";


			list = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdDivisa(rs.getString("ID"));
					cons.setDescDivisa(rs.getString("descrip"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerBancos");
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<ConfirmacionCargoCtaDto> llenaGridMovimientos(ConfirmacionCargoCtaDto dtoGrid, boolean pbCpaVtaTransfer) {

		String sql = "";

		String gsDBM = ConstantesSet.gsDBM;

		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);

		String fecHoy = "" + consultasGenerales.obtenerFechaHoy();
		fecHoy = fecHoy.substring(8,10)+"/"+fecHoy.substring(5,7)+"/"+fecHoy.substring(0,4);

		StringBuffer sb = new StringBuffer();

		try{

			sb.append("SELECT m.no_empresa, e.nom_empresa, m.no_cliente,"									).append("\n");
			sb.append("p.nombre_corto as nom_proveedor, m.no_docto,m.importe,"								).append("\n");
			sb.append("CASE WHEN m.id_divisa <> 'MN' THEN"													).append("\n"); 
			sb.append("( m.importe * coalesce("																).append("\n");
			sb.append("( SELECT DISTINCT vd.valor"															).append("\n");
			sb.append("FROM valor_divisa vd"																).append("\n");
			sb.append("Where vd.id_divisa = m.id_divisa"													).append("\n");
			sb.append("and vd.fec_divisa = '"+ fecHoy + "'), 1 )"											).append("\n");
			sb.append(")"																					).append("\n");
			sb.append("END as importe_mn,m.id_divisa, m.id_forma_pago,"										).append("\n");
			sb.append("fp.desc_forma_pago, m.fec_valor, b.desc_banco, "										).append("\n");
			sb.append("m.id_banco as id_banco_pago, m.id_chequera as id_chequera_pago,"						).append("\n");
			sb.append("b2.desc_banco AS banco_pago, m.id_chequera_benef, m.concepto, m.id_rubro, 1 as dias,").append("\n");
			sb.append("cbp.id_banco as id_banco_cte, cbp.id_chequera as id_chequera_cte, "					).append("\n");
			sb.append("cbp.id_divisa as id_divisa_cte, importe_original,"									).append("\n");
			sb.append("'' as bco_pago_cruzado, '' as cheq_pago_cruzado,'' as div_pago_cruzado, '' as div_pago_cruzado, '' as imp_pago_cruzado,").append("\n"); 
			sb.append("m.id_divisa as id_divisa_pago,  m.id_contable, m.id_banco_benef,"					).append("\n");
			sb.append("m.beneficiario, m.no_folio_det,  m.observacion, m.id_divisa_original"				).append("\n");
			sb.append("FROM movimiento m"																	).append("\n");
			sb.append("left join empresa e on m.no_empresa = e.no_empresa"									).append("\n");
			sb.append("left join persona p on (m.no_cliente = p.no_persona and p.id_tipo_persona = 'P')"	).append("\n");
			sb.append("left join cat_forma_pago fp  on m.id_forma_pago = fp.id_forma_pago"					).append("\n");
			sb.append("left join cat_banco b on (m.id_banco_benef = b.id_banco)"							).append("\n");
			sb.append("left join cat_banco b2 on (m.id_banco = b2.id_banco)"								).append("\n");
			sb.append("left join ctas_banco cbp ON ( m.no_cliente = cbp.no_persona and m.id_banco_benef = cbp.id_banco and m.id_chequera_benef = cbp.id_chequera  )"				).append("\n");
			sb.append("WHERE m.id_divisa = ?"																).append("\n");
			sb.append("AND m.cve_control = ?"																).append("\n");
			sb.append("AND m.id_tipo_operacion = 3000"														).append("\n");
			sb.append("AND m.id_forma_pago IN( 3, 5)"														).append("\n");
			//sb.append("AND m.importe > 0"																	).append("\n");
			System.out.println(sb.toString());  
			sql = sb.toString(); 

			list = jdbcTemplate.query(sql, new Object[]{dtoGrid.getIdDivisa(), dtoGrid.getNoClaveControl() }, new RowMapper(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setNomEmpresa(rs.getString("nom_empresa"));		    		
					cons.setNoProveedor(rs.getString("no_cliente"));
					cons.setNomProveedor(rs.getString("nom_proveedor"));
					cons.setNoDocto(rs.getString("no_docto"));
					cons.setImporte(rs.getDouble("importe"));
					cons.setImporteMn(rs.getDouble("importe_mn"));   
					cons.setIdDivisa(rs.getString("id_divisa"));
					cons.setFormaPago(rs.getString("desc_forma_pago"));  
					cons.setNumFormaPago(rs.getString("id_forma_pago")); 
					cons.setFechaHoy(rs.getString("fec_valor"));
					cons.setIdDivisa(rs.getString("id_divisa"));
					cons.setDescBanco(rs.getString("desc_banco"));
					cons.setNoBancoPago(rs.getInt("id_banco_pago"));
					cons.setBancoPago(rs.getString("banco_pago"));
					cons.setIdChequera(rs.getString("id_chequera_pago"));   
					cons.setChequeraBenef(rs.getString("id_chequera_benef"));
					cons.setConcepto(rs.getString("concepto"));  
					cons.setIdRubro(rs.getDouble("id_rubro"));
					cons.setDias(rs.getInt("dias"));
					cons.setBancoCte(rs.getString("id_banco_cte"));
					cons.setChequeraCte(rs.getString("id_chequera_cte"));
					cons.setBcoPagoCruzado(rs.getString("bco_pago_cruzado"));
					cons.setCheqPagoCruzado(rs.getString("cheq_pago_cruzado"));
					cons.setDivPagoCruzado(rs.getString("div_pago_cruzado"));
					cons.setImpPagoCruzado(rs.getString("imp_pago_cruzado"));
					cons.setIdDivisaPago(rs.getString("id_divisa_pago"));
					cons.setIdDivisaCte(rs.getString("id_divisa_cte"));
					cons.setIdContable(rs.getString("id_contable"));
					cons.setIdBancoBenef(rs.getInt("id_banco_benef"));
					cons.setBeneficiario(rs.getString("beneficiario"));
					cons.setNoFolioDet(rs.getInt("no_folio_det"));
					cons.setObservaciones(rs.getString("observacion"));
					cons.setDivisaOriginal(rs.getString("id_divisa_original"));
					cons.setImporteOriginal(rs.getInt("importe_original"));



					return cons;
				}
			});

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:llenaGridMovimientos");
		}

		return list;
	}


	public List<ConfirmacionCargoCtaDto> obtenerDivisaEmpresa(int idUsuario) {
		String sql="";

		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{ 

			sql += "";  // Sybase
			sql +=" SELECT id_divisa as ID, desc_divisa as describ ";
			sql +="   FROM cat_divisa";
			sql +="  WHERE clasificacion = 'D' ";
			sql +="    AND id_divisa <> 'CNT' ";

			/*	
		    if (!Divisa.equals(""))
		       sql +=" AND id_divisa <> '" + idDivisa + "' ";
			 */



			list = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdDivisa(rs.getString("ID"));
					cons.setDescDivisa(rs.getString("describ"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerDivisaEmpresa");
		}
		return list;
	}


	public List<ConfirmacionCargoCtaDto> obtenerBancoEmpresa(String nomEmpresa, String idDivisa) {
		String sql="";
		String gsDBM = ConstantesSet.gsDBM;
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{
			sql += "";

			sql +="SELECT id_banco as ID, desc_banco as Descrip" + "\n";
			sql +="FROM cat_banco"+ "\n";
			sql +="WHERE  id_banco in (Select distinct id_banco"+ "\n";                       
			sql +="                    From cat_cta_banco"+ "\n";                     
			sql +="                    Where tipo_chequera in ('P','O','M')"+ "\n";                        
			sql +="                    And no_empresa in("+ "\n";
			sql +="                                        select distinct no_empresa"+ "\n";
			sql +="                                        from movimiento"+ "\n";
			sql +="                                        where cve_control = '"+nomEmpresa+"'"+ "\n";
			sql +="                                      )"+ "\n";
			sql +="                    And id_divisa = '"+ idDivisa +"')"+ "\n";
			System.out.println(sql.toString());
			list = jdbcTemplate.query(sql.toString(), new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdBanco(rs.getInt("ID"));
					cons.setDescBanco(rs.getString("Descrip"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerBancos");
		}
		return list;
	}


	public List<ConfirmacionCargoCtaDto> obtenerChequeraEmp(ConfirmacionCargoCtaDto dtoGrid) {
		String sql="";
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{
			sql +=" SELECT id_chequera as ID, id_chequera as Descrip ";
			sql +=" FROM cat_cta_banco";
			sql +=" WHERE id_banco = " + dtoGrid.getIdBancoCasa();
			sql +=" AND id_divisa = '" + dtoGrid.getIdDivisa()+ "'";		    
			sql +="                    And no_empresa in("+ "\n";
			sql +="                                        select distinct no_empresa"+ "\n";
			sql +="                                        from movimiento"+ "\n";
			sql +="                                        where cve_control = '"+dtoGrid.getNoClaveControl()+"'"+ "\n";
			sql +="                                      )"+ "\n";



			list = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdChequera(rs.getString("ID"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerBancos");
		}
		return list;
	}


	public List<ConfirmacionCargoCtaDto> obtenerDivisaPago(int noEmpresa) {
		String sql="";
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{
			sql+= "SELECT DISTINCT D.id_divisa as ID , d.desc_divisa as DESCRIPCION";
			sql+= "\n	FROM cat_cta_banco ccb, cat_divisa d";
			sql+= "\n	WHERE ccb.tipo_chequera in ('P','M')";
			sql+= "\n					   		And ccb.id_divisa =  d.id_divisa ";
			sql+= "\n							And ccb.no_empresa = " + noEmpresa + "";

			list = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdDivisa(rs.getString("ID"));
					cons.setDescDivisa(rs.getString("DESCRIPCION"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerBancos");
		}
		return list;
	}


	public List<ConfirmacionCargoCtaDto> obtenerEmpresa(int idUsuario) {
		String sql="";
		String letra="S";
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{ 
			if (letra.equals("")){ 
				sql += ""; // Sybase
				sql += " SELECT distinct c.id_grupo_flujo as ID, desc_grupo_flujo as DESCC";
				sql += "   FROM cat_grupo_flujo c, grupo_empresa g, usuario_empresa  u";
				sql += "  WHERE c.id_grupo_flujo = g.id_grupo_flujo";
				sql += "    AND g.no_empresa = u.no_empresa";
				sql += "    AND u.no_usuario = " + idUsuario ;
				sql += "  ORDER BY desc_grupo_flujo";
			}    else{
				sql += " Select e.no_empresa as ID,e.nom_empresa as DESCC from empresa e, usuario_empresa ue";
				sql += " 		Where e.no_empresa = ue.no_empresa";
				sql += " 			and ue.no_usuario = " + idUsuario;
				sql += " 			and pagadora = 'S'";
				sql += " 				order by e.no_empresa";
			}

			list = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setNoEmpresa(rs.getInt("ID"));
					cons.setNomEmpresa(rs.getString("DESCC"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerBancos");
		}
		return list;
	}

	@Override
	public ConfirmacionCargoCtaDto obtenerEmpresa(String idChequera) {

		String sql = "";		
		ConfirmacionCargoCtaDto list = null;

		try{ 

			sql += " Select distinct e.no_empresa as ID,e.nom_empresa as DESCC from empresa e, usuario_empresa ue";
			sql += " Where e.no_empresa = ue.no_empresa";
			sql += " and e.no_empresa in(";
			sql += "    		 select distinct no_empresa";
			sql += "      		 from cat_cta_banco";
			sql += "       		 where id_chequera = '" + idChequera + "'";
			sql += " )";




			list = jdbcTemplate.queryForObject(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setNoEmpresa(rs.getInt("ID"));
					cons.setNomEmpresa(rs.getString("DESCC"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerBancos");
		}
		return list;
	}


	public List<ConfirmacionCargoCtaDto> BancoLlenado(int noEmpresa, String idDivisa) {
		String sql="";

		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{ 
			sql += " SELECT id_banco as ID, desc_banco as DESCC ";
			sql += "   FROM cat_banco b ";
			sql += "  WHERE b.id_banco in ";
			sql += "         ( SELECT DISTINCT ccb.id_banco ";
			/*
			    if psDivision = "" Then*/
			sql += "           FROM cat_cta_banco ccb ";
			/*else
			        sql += "           FROM cat_cta_banco ccb, cat_cta_banco_division ccbd ";
			 */

			sql += "           WHERE ccb.tipo_chequera in ('P','M') ";
			sql += "               AND ccb.id_banco = b.id_banco ";
			if (idDivisa.length() < 4) 
				sql += "               AND ccb.id_divisa = '" + idDivisa + "' ";
			else
				sql += "               AND ccb.id_divisa = (Select id_divisa from cat_divisa where desc_divisa = '" + idDivisa + "')";

			sql += "               AND ccb.no_empresa = " + noEmpresa;
			/*
			    if psDivision <> "" 
			        sql += "           AND ccb.no_empresa = ccbd.no_empresa ";
			        sql += "           AND ccb.id_chequera = ccbd.id_chequera ";
			        sql += "           AND ccb.id_banco = ccbd.id_banco ";
			        sql += "           AND ccbd.id_division = '" + psDivision + "' ";*/

			/*
			    if plBanco > 0 Then
			        sql += "           AND ccb.id_banco = " + plBanco;
			 */  

			sql += ") ";


			list = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdBanco(rs.getInt("ID"));
					cons.setDescBanco(rs.getString("DESCC"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerBancos");
		}
		return list;
	}


	public List<ConfirmacionCargoCtaDto> obtenerChequera(int noEmpresa, String idDivisa, int idBanco) {
		String sql="";

		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{ 

			sql +=" SELECT ccb.id_chequera as ID, ccb.id_chequera as Descrip ";
			/*
		    if psDivision = "" Then*/
			sql +="   FROM cat_cta_banco ccb ";
			/*
		        sql +="   FROM cat_cta_banco ccb, cat_cta_banco_division ccbd ";
			 */
			sql +="  WHERE ccb.tipo_chequera in ('P','M')";
			sql +="    AND ccb.id_banco = " + idBanco;

			if (idDivisa.length() < 4) 
				sql +="    AND ccb.id_divisa = '" + idDivisa + "'";
			else
				sql +="    AND ccb.id_divisa = (Select id_divisa from cat_divisa where desc_divisa = '" + idDivisa + "')";


			sql +="    AND ccb.no_empresa = " + noEmpresa;
			/*
		    If psDivision <> "" Then
		        sql +=" AND ccb.no_empresa = ccbd.no_empresa ";
		        sql +=" AND ccb.id_chequera = ccbd.id_chequera ";
		        sql +=" AND ccb.id_banco = ccbd.id_banco ";
		        sql +=" AND ccbd.id_division = '" + psDivision + "' ";
			 */  

			list = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdChequera(rs.getString("Descrip"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerBancos");
		}
		return list;
	}


	public List<ConfirmacionCargoCtaDto> CasaCambio(int idUsuario) {
		String sql="";

		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{ 

			sql += ""; // Sybase
			sql += " SELECT no_persona as ID , razon_social as Descrip ";
			sql += "FROM persona";
			sql += "  WHERE id_tipo_persona = 'K'";



			list = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdCasaCambio(rs.getInt("ID"));
					cons.setDescCasaCambio(rs.getString("Descrip"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerBancos");
		}
		return list;
	}


	public List<ConfirmacionCargoCtaDto> BancoCasaCambio(String idDivisa, int idCasaCambio) {
		String sql="";

		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{
			sql += ""; // Sybase
			sql +=" SELECT id_banco as ID, desc_banco as Descrip ";
			sql +="\n   FROM cat_banco";
			sql +="\n WHERE id_banco in (Select distinct c.id_banco from ctas_banco c, persona p";
			sql +="\n                     Where p.no_persona = c.no_persona";
			sql +="\n                       And p.id_tipo_persona = 'K' ";
			sql +="\n                       And c.id_divisa = '" + idDivisa + "'";
			sql +="\n                       And p.no_persona = " + idCasaCambio;
			sql +="\n                       And c.no_empresa = 552)";

			System.out.println(sql.toString());

			list = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdBancoCasa(rs.getInt("ID"));
					cons.setDescBancoCasa(rs.getString("Descrip"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerBancos");
		}
		return list;
	}


	public List<ConfirmacionCargoCtaDto> obtenerChequeraCasa(int idCasaCambio, String idDivisa, int idBancoCasa) {
		String sql="";
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{
			sql +=" SELECT id_chequera as ID, id_chequera as descrip";
			sql +="   FROM ctas_banco ";
			sql +="  WHERE no_persona = " + idCasaCambio;
			sql +="    AND id_divisa = '" + idDivisa + "'";
			sql +="    AND id_banco = " + idBancoCasa;


			list = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdChequera(rs.getString("ID"));
					return cons; 
				} 
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerBancos");
		}
		return list;
	}


	public List<ConfirmacionCargoCtaDto> obtenerOperador(int idCasaCambio) {
		String sql="";
		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{
			sql += ""; // Sybase
			sql +=" SELECT no_persona as Id, nombre_corto as Descrip ";
			sql +=" FROM persona ";
			sql +=" WHERE no_persona in ";
			sql +="     ( SELECT no_persona_rel ";
			sql +="       FROM relacion ";
			sql +="       WHERE no_persona = " + idCasaCambio;
			sql +="     ) ";
			sql +="    and id_tipo_persona = 'F' ";

			list = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdOperador(rs.getInt("ID"));
					cons.setNomOperador(rs.getString("Descrip"));
					return cons; 
				} 
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerBancos");
		}
		return list;
	}

	public int Update(int psFoliosCli, String idCasaCambio, String idBancoCasa, String idChequera, String idDivisa, double tipoCambio, double importe) {
		String sql="";
		String gsDBM = ConstantesSet.gsDBM;
		int iRes = 0;

		try{

			sql +="\n UPDATE movimiento ";
			sql +="\n    SET ";

			/* 
		        if (pbModOrigen Then
		            sql +="\n    origen_mov_ant = origen_mov, "
		            sql +="\n    origen_mov = 'CVT', "
		        End If*/

			/*If psReferenciaCie <> "" Then
		            sql +="\n        referencia = '" + psReferenciaCie + "',"
		        End If*/

			sql +="\n        no_cliente_ant = '" + idCasaCambio + "',";
			sql +="\n        id_banco_ant = " +idBancoCasa + ", ";
			sql +="\n        id_chequera_ant = '" + idChequera+ "', ";
			sql +="\n        id_forma_pago = 3, ";

			if (gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2")) 
				sql +="\n    concepto = rtrim(ltrim(concepto)) + ' (COMPRA DE ' + id_divisa_original + ' PARA ' || rtrim(ltrim(beneficiario)) + ')' ";
			else
				sql +="\n    concepto = rtrim(ltrim(concepto)) + ' (COMPRA DE ' + id_divisa_original + ' PARA ' + rtrim(ltrim(beneficiario)) + ')' ";


			//'sql +="\n        id_divisa_original = '" + psDivisaCasaC + "', "
			//'sql +="\n        tipo_cambio = " + pdTipoCambioCasaC + ", "

			//if Right(psFolioDet, 1) <> "," Then
			//  'sql +="\n    importe_original = " + pdImporteCasaC
			sql +="\n WHERE no_folio_det in (" + psFoliosCli + ") ";
			/*else
		            'sql +="\n    importe_original = importe_original / " + pdTipoCambioCasaC
		            sql +="\n WHERE no_folio_mov in (" + Left(psFolioMov, Len(psFolioMov) - 1) + ") "*/

			iRes = jdbcTemplate.update(sql);

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerBancos");
			return 0;
		}
		return iRes;
	}

	@Transactional
	public Map<String, Object> ejecutaPagador(StoreParamsComunDto dto)
	{
		Map<String, Object> resultado= new HashMap<String, Object>();
		try{
			/*StoredProcedure storedProcedure = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_sql_Pagador") {};
			storedProcedure.declareParameter(new SqlParameter("parametro",Types.VARCHAR ));
			storedProcedure.declareParameter(new SqlInOutParameter("mensaje",Types.VARCHAR));
			storedProcedure.declareParameter(new SqlInOutParameter("result",Types.INTEGER));

			HashMap< Object, Object > inParams = new HashMap< Object, Object >();
			inParams.put("parametro",dto.getParametro());
			inParams.put("mensaje",dto.getMensaje());
			inParams.put("result",dto.getResult());

			logger.info(dto.getParametro());
			logger.info(dto.getMensaje());
			logger.info(dto.getResult());

			resultado = storedProcedure.execute((Map)inParams);
			 */
		}catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
			+ "P:GRAL, C:ConsultasGenerales, M:ejecutarPagador");
		}
		return resultado;
	}


	public void setDataSource(DataSource dataSource){
		try {
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:setDataSource");
		}
	}

	@SuppressWarnings("unchecked")
	public List<ConfirmacionCargoCtaDto> obtenerBancoAbo (int noEmpresa, String idDivisa) {
		String sql="";


		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{

			sql +="\n SELECT id_banco as ID, desc_banco as Descrip ";
			sql +="\n   FROM cat_banco";
			sql +="\n  WHERE id_banco in (SELECT distinct id_banco ";
			sql +="\n                       FROM cat_cta_banco ";
			sql +="\n                      WHERE tipo_chequera in ('P','M') ";
			sql +="\n                        AND no_empresa = " + noEmpresa;
			sql +="\n                        AND id_divisa = '" + idDivisa + "')";



			list = jdbcTemplate.query(sql, new RowMapper(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdBanco(rs.getInt("ID"));
					cons.setDescBanco(rs.getString("Descrip"));
					return cons;
				}   
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerBancoAbo");
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<ConfirmacionCargoCtaDto> obtenerChequeraAbo (int noEmpresa, String idDivisa, int idBanco) {
		String sql="";
		String gsDBM = ConstantesSet.gsDBM;

		List<ConfirmacionCargoCtaDto> list = new ArrayList<ConfirmacionCargoCtaDto>();

		try{

			sql +="\n SELECT id_chequera as id, id_chequera as descrip";
			sql +="\n   FROM cat_cta_banco ";
			sql +="\n  WHERE tipo_chequera in ('P','M') ";
			sql +="\n    AND no_empresa = " + noEmpresa;
			sql +="\n    AND id_divisa = '" + idDivisa + "'";
			sql +="\n    AND id_banco = " + idBanco;

			list = jdbcTemplate.query(sql, new RowMapper(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdChequera(rs.getString("id"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerChequeraAbo");
		}
		return list;
	}

	@Override
	public List<ConfirmacionCargoCtaDto> obtenerGruposPorTipoMovto( String idTipoMovto ) {

		String sql = null;
		StringBuilder sb = new StringBuilder();

		try{

			sb.append( "SELECT DISTINCT CR.ID_GRUPO AS ID_GRUPO, " 	).append( "\n" );
			sb.append( "CB.DESC_GRUPO" 								).append( "\n" ); 
			sb.append( "FROM CAT_RUBRO CR"							).append( "\n" );						
			sb.append( "			LEFT JOIN CAT_GRUPO CB"			).append( "\n" );
			sb.append( "			ON CR.ID_GRUPO = CB.ID_GRUPO"	).append( "\n" );
			sb.append( "WHERE CR.INGRESO_EGRESO = ?"				).append( "\n" );
			sb.append( "ORDER BY 2"									).append( "\n" );

			Object[] params = {idTipoMovto};

			sql = sb.toString(); 

			return 
					jdbcTemplate.query(sql, params, new RowMapper(){

						public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
							ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
							cons.setIdGrupo(rs.getInt("ID_GRUPO"));
							cons.setDescGrupo(rs.getString("DESC_GRUPO"));
							return cons;
						}
					});

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerBancos");
			return null; 
		}


	}//END METHOD: obtenerGruposPorTipoMovto


	@Override
	public List<ConfirmacionCargoCtaDto> llenaComboFirmas() {

		String sql = null;
		StringBuilder sb = new StringBuilder();

		List<ConfirmacionCargoCtaDto> listaComboFirmas = new ArrayList<ConfirmacionCargoCtaDto>();

		try{

			sb.append( "SELECT ID_FIRMANTE AS ID," 			).append( "\n" );
			sb.append( "	   NOMBRE_FIRMANTE AS DESCRIP"	).append( "\n" );
			sb.append( "FROM FIRMA_CVD"						).append( "\n" );
			sb.append( "ORDER BY 1"							).append( "\n" );

			sql = sb.toString();

			return jdbcTemplate.query(sql, new RowMapper(){

				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {

					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdFirmante    ( rs.getInt("ID")         );
					cons.setNombreFirmante( rs.getString("DESCRIP") );

					return cons;

				} 
			});

		}catch(Exception e){			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:llenaComboFirmas");
			return null; 
		}

	}//END METHOD: llenaComoboFirmas

	@Override
	public int guardarCartaCVD(ConfirmacionCargoCtaDto dto) {

		String sql = null;
		StringBuilder sb = new StringBuilder();
		int iResp = 0;

		sb.append( "INSERT INTO CARTAS_CVD(" 	).append("\n");
		sb.append( "id_registro,"				).append("\n");
		sb.append( "no_docto,"					).append("\n");
		sb.append( "no_empresa,"				).append("\n");
		sb.append( "nom_empresa,"				).append("\n");
		sb.append( "fecha_valor,"				).append("\n");
		sb.append( "fecha_liquidacion,"			).append("\n");
		sb.append( "id_divisa_venta,"			).append("\n");
		sb.append( "desc_divisa_pago,"			).append("\n");
		sb.append( "id_banco_cargo,"			).append("\n");
		sb.append( "nom_banco_cargo,"			).append("\n");
		sb.append( "id_chequera_cargo,"			).append("\n");
		sb.append( "importe_venta,"				).append("\n");
		sb.append( "id_divisa_abono,"			).append("\n");
		sb.append( "desc_divisa_abono,"			).append("\n");
		sb.append( "id_banco_abono,"			).append("\n");
		sb.append( "nom_banco_abono,"			).append("\n");
		sb.append( "id_chequera_abono,"			).append("\n");
		sb.append( "tipo_cambio,"				).append("\n");
		sb.append( "importe_compra,"			).append("\n");
		sb.append( "no_persona,"				).append("\n");
		sb.append( "nom_casa_cambio,"			).append("\n");
		sb.append( "direccion,"					).append("\n");
		sb.append( "colonia,"					).append("\n");
		sb.append( "cp,"						).append("\n");
		sb.append( "ciudad,"					).append("\n");
		sb.append( "id_banco_casa_cambio,"		).append("\n");
		sb.append( "desc_banco_casa_cambio,"	).append("\n");
		sb.append( "id_chequera_casa_cambio,"	).append("\n");
		sb.append( "operador,"					).append("\n");
		sb.append( "concepto,"					).append("\n");
		sb.append( "id_grupo,"					).append("\n");
		sb.append( "desc_grupo,"				).append("\n");
		sb.append( "id_rubro,"					).append("\n");
		sb.append( "desc_rubro,"				).append("\n");
		sb.append( "estatus)"					).append("\n");
		sb.append( "VALUES ((select coalesce((max(id_registro)+1),0) from CARTAS_CVD),?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"	).append("\n");

		String fechaValor 		= dto.getFechaValor().toString();
		String fechaLiquidacion = dto.getFechaLiquidacion().toString();

		fechaValor 			= fechaValor.substring(8,10)+"/"+fechaValor.substring(5,7)+"/"+fechaValor.substring(0,4);
		fechaLiquidacion	= fechaLiquidacion.substring(8,10)+"/"+fechaLiquidacion.substring(5,7)+"/"+fechaLiquidacion.substring(0,4);

		DecimalFormat dTCambio = new DecimalFormat("##.0000");
		DecimalFormat dTImporte = new DecimalFormat("#########.00");

		String importeVenta = dTImporte.format(dto.getImporteVenta());
		String tipoCambio = dTCambio.format(dto.getTipoCambio());
		String importeCompra = dTImporte.format(dto.getImporteCompra());

		Object[] params = new Object[] { 
				dto.getNoDocto(),
				String.valueOf(dto.getNoEmpresa()),
				dto.getNomEmpresa(),
				fechaValor,
				fechaLiquidacion,	    	
				dto.getDivisaVenta().toString(),
				dto.getDescDivisaVenta(), 	    	
				String.valueOf(dto.getBancoCargo()),
				dto.getDescBancoCargo(),	    	
				dto.getChequeraCargo().toString(),
				importeVenta	   , 	
				dto.getDivisaCompra().toString(),
				dto.getDescDivisaCompra(),
				String.valueOf(dto.getBancoAbono()),
				dto.getDescBancoAbono(),
				dto.getChequeraAbono().toString(),
				tipoCambio,    	
				importeCompra,
				String.valueOf(dto.getIdCasaCambio()),
				String.valueOf(dto.getDescCasaCambio()),
				dto.getReferencia(),
				"SIN VALOR",
				"SIN VALOR",
				"SIN VALOR",
				String.valueOf(dto.getIdBanco()),
				dto.getDescBancoCasa(),
				dto.getIdChequera(),
				dto.getNomOperador().toString(),
				dto.getConcepto().toString(),
				String.valueOf(dto.getIdGrupo()),
				dto.getDescGrupoEgreso(),
				String.valueOf(dto.getIdRubro()),
				dto.getDescRubroEgreso(),
				"A"
		};

		sql = sb.toString(); 

		iResp = jdbcTemplate.update(sql, params);

		return iResp;
	}


	@Override
	public int InsertAceptado2(int inst, ConfirmacionCargoCtaDto dto, int noFolioParam, int tipoMovto, int cuentaEmp, int noFolioDocto,
			int idCaja, String concepto, String leyenda, String obervacion, String solicita, String autoriza, int plaza,
			int sucursal, int grupo, int agrupa1, int agrupa2, int agrupa3, boolean ADU, int pedido, boolean CVDivisa, int area,
			String referencia, String sDivision, int piLote, String partida, boolean pbNomina, boolean pbProvAc){

		StringBuffer sql = new StringBuffer();
		String fecHoy = "";
		String fechaValor = "";
		String fechaLiquidacion = "";
		int idGrupo = 0;
		int res = 0;
		String estatus = "L";
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);

		fecHoy = "" + consultasGenerales.obtenerFechaHoy();
		fecHoy = fecHoy.substring(8,10)+"/"+fecHoy.substring(5,7)+"/"+fecHoy.substring(0,4);


		fechaValor = dto.getFechaValor();
		fechaValor = fechaValor.substring(8,10)+"/"+fechaValor.substring(5,7)+"/"+fechaValor.substring(0,4);


		fechaLiquidacion = dto.getFechaLiquidacion();
		fechaLiquidacion = fechaLiquidacion.substring(8,10)+"/"+fechaLiquidacion.substring(5,7)+"/"+fechaLiquidacion.substring(0,4);

		try{
			sql.append("\n INSERT INTO parametro (no_empresa, no_docto, no_folio_param, id_tipo_docto, id_forma_pago, ");
			sql.append("\n      usuario_alta, id_tipo_operacion, no_cuenta, no_factura, fec_valor, fec_valor_original, ");
			sql.append("\n      fec_operacion, fec_alta, id_divisa, id_divisa_original, importe, importe_original, ");
			sql.append("\n      tipo_cambio, id_caja, referencia, beneficiario,");
			sql.append("\n      concepto, id_banco_benef, id_chequera_benef, aplica,");
			sql.append("\n      id_estatus_mov, b_salvo_buen_cobro, id_estatus_reg, id_leyenda, folio_ref, ");
			sql.append("\n      id_chequera, observacion, origen_mov, no_cliente,");
			sql.append("\n      solicita, autoriza, plaza, sucursal, grupo, ");
			sql.append("\n      no_folio_mov, agrupa1, agrupa2, agrupa3, id_grupo, id_rubro, ");
			sql.append("		id_banco");

			if (ADU == true) sql.append("\n ,no_pedido ");

			if(inst != 247){
				if (area != -1) sql.append("\n ,id_area ");

				if (!sDivision.equals("")) sql.append("\n ,division ");

				if (pbNomina && piLote != 0)
					sql.append("\n ,lote ");
				else if ((pbProvAc && piLote == 0) || (pbProvAc && piLote == 1))
					sql.append("\n ,lote ");
				else if (piLote != 0) 
					sql.append("\n ,lote ");

				//if(inst == 245 && idGrupo != 0) sql.append("\n ,id_grupo ");

				if(inst == 245) sql.append("\n ,invoice_type");
			}
			sql.append("\n)");

			sql.append("\n VALUES (");
			sql.append("\n " + dto.getNoEmpresa() + ", '" + noFolioDocto + "', " + noFolioParam + ", 41, " + dto.getIdFormaPago() + ",");
			sql.append("\n " + dto.getUsuario() + ", " + tipoMovto + ", " + cuentaEmp + ", '" + noFolioDocto + "', '" + fechaValor + "',");

			if (fechaValor != "")  
				sql.append("\n '" + fechaValor + "',");
			else
				sql.append("\n '" + fecHoy + "',");

			sql.append("\n '" + fecHoy + "', '" + fecHoy + "',");

			if(inst != 247)
				sql.append("'" + dto.getDivisaVenta() + "', '" + dto.getDivisaVenta() + "', " + dto.getImporteVenta() + "," + dto.getImporteVenta() + ",");
			else
				sql.append("'" + dto.getDivisaCompra() + "', '" + dto.getDivisaCompra() + "', " + dto.getImporteCompra() + "," + dto.getImporteCompra() + ",");


			sql.append("\n " + dto.getTipoCambio() + ", " + idCaja + ", '" + referencia + "', '" + dto.getDescCasaCambio() + "', '" + concepto + "', ");		    

			sql.append("" + dto.getIdBancoBenef() + ", '" + dto.getIdChequeraBenef() + "'");


			sql.append("\n ,1,");

			if(inst == 244) sql.append("\n 'C','S','P','" + leyenda + "', '', ");
			if(inst == 245) sql.append("\n 'H','S','P','" + leyenda + "', 1,");
			if(inst == 247) sql.append("'"+ estatus + "'"+ ",'S','P','" + leyenda + "', 1,");

			if(inst != 247) {
				if (dto.getChequeraCargo().equals(""))
					sql.append("\n NULL");
				else
					sql.append("\n '" + dto.getChequeraCargo()+ "'");
			}else {
				if (dto.getIdChequera().equals(""))  
					sql.append("\n NULL");
				else
					sql.append("\n '" + dto.getIdChequera() + "'");
			}
			sql.append("\n ,'" + dto.getObservacion() + "',");

			if(ADU)  
				sql.append("\n 'ADU'");
			else if(CVDivisa)  
				sql.append("\n 'CVD'");
			else
				sql.append("\n 'SET'");
			sql.append("\n ,'" + dto.getIdCasaCambio() + "',");

			if(solicita.equals(""))
				sql.append("NULL, ");
			else 
				sql.append("'"+ solicita + "', ");

			if (autoriza.equals(""))
				sql.append("\n NULL, ");
			else
				sql.append("\n '" + autoriza + "', ");

			sql.append( "\n "+ plaza + "," + sucursal + ", " + grupo + ", 1, " + agrupa1 + ", " + agrupa2 + "," + agrupa3 + "");

			if (dto.getIdGrupo() == 0)
				sql.append("\n ,NULL");
			else
				sql.append( ","+ dto.getIdGrupo());


			if (dto.getIdRubro() == 0)
				sql.append("\n ,NULL");
			else
				sql.append( ","+ dto.getIdRubro());


			if(inst != 247) 
				sql.append( "," + dto.getBancoCargo());
			else
				sql.append( "," + dto.getIdBanco());

			if (ADU) sql.append("\n ," + pedido);

			//System.out.println(sql.toString());

			if(inst != 247){
				if (area != -1)  
					sql.append("\n ," + area);

				if (sDivision != "")
					sql.append("\n ,'" + sDivision + "'");


				if (pbNomina == true && piLote != 0)  
					sql.append("\n ,3");
				else if ((pbProvAc == true && piLote == 0) || (pbProvAc == true && piLote == 1))  
					sql.append("\n ,4");
				else if (piLote != 0)  
					if(inst == 244)
						sql.append("\n ,2");
					else
						sql.append( "," + piLote);

				//if(inst == 245){
				//if(idGrupo != 0)
				//sql.append( "," + idGrupo);
				//}

				if(inst == 244 && partida != "")      
					sql.append("\n ,'" + partida + "'");

				if (inst == 245)
					sql.append("\n ,'" + partida + "'");
			}
			sql.append("\n)");
			res = jdbcTemplate.update(sql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerBancos");
		}     
		return res;
	}

	@Override
	public Map<String, Object> getDatosReporteCVD(String folioRep) {

		String sql = null;
		StringBuilder sb = new StringBuilder();

		/*sb.append( "SELECT *" 									).append( "\n" );
		sb.append( "FROM CARTAS_CVD CVD"						).append( "\n" );
		sb.append( "WHERE CVD.NO_DOCTO = ?"						).append( "\n" );*/

		//sb.append( "select  extracnumber(cvd.importe_venta) as imp_venta," ).append( "\n" ); 
		sb.append( "select  cvd.importe_venta as imp_venta," ).append( "\n" );
		sb.append( "convert(numeric,cvd.importe_compra) as imp_compra," 		).append( "\n" );
		sb.append( "cvd.*" 												).append( "\n" );
		sb.append( "from cartas_cvd cvd" 								).append( "\n" );
		sb.append( "WHERE cvd.no_docto = ?"						        ).append( "\n" );

		Object[] params = {folioRep};

		sql = sb.toString(); 

		return jdbcTemplate.queryForMap(sql, params );

	}


	@Override
	public int traerNoPersona(String equivalePersona){

		String sql = "SELECT no_persona FROM PERSONA WHERE EQUIVALE_PERSONA = ?";

		return (Integer) jdbcTemplate.queryForObject( sql, new Object[] { equivalePersona }, Integer.class);

	}//END METHOD:findCustomerNameById 


	public ResultadoDto cambioDeDivisa( String cadena ){
		try{

			StoredProcedure storedProcedure = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_ajustes") {};
			storedProcedure.declareParameter(new SqlParameter     ("parametro",Types.VARCHAR ));
			storedProcedure.declareParameter(new SqlInOutParameter("result",Types.INTEGER)    );
			storedProcedure.declareParameter(new SqlInOutParameter("mensaje",Types.VARCHAR)    );

			Map< String, Object> inParams = new HashMap< String, Object >();

			inParams.put("parametro",cadena);
			inParams.put("result",0);
			inParams.put("mensaje","''");

			storedProcedure.execute( inParams );

			return new ResultadoDto(true, "Se ejecuto correctamente el cambio de divisas", null);

		}catch( Exception e ){
			return new ResultadoDto(false, "Error: En el SP AJUSTES. Reportarlo a su Administrador.", null);
		}

	}//END METHOD: ResultadoDto

	public ResultadoDto updateTransfer( String foliosHijo, String idCasaDeCambio, String idBancoCasaDeCambio, String idChequeraCasaDeCambio ){

		String sql = null;
		StringBuilder sb = new StringBuilder();

		sb.append( "UPDATE movimiento SET" 			).append("\n");
		sb.append( "origen_mov_ant  = origen_mov," 	).append("\n");
		sb.append( "origen_mov      = 'CVT'," 		).append("\n");
		sb.append( "no_cliente_ant  = ?," 			).append("\n");
		sb.append( "id_banco_ant    = ?," 			).append("\n");
		sb.append( "id_chequera_ant = ?," 			).append("\n");
		sb.append( "id_forma_pago   = 3," 			).append("\n");
		sb.append( "concepto = rtrim(ltrim(concepto)) + ' (COMPRA DE ' + id_divisa_original + ' PARA ' + rtrim(ltrim(beneficiario)) + ')'" ).append("\n");
		sb.append( "WHERE no_folio_det in (?)" 		).append("\n");

		Object[] params = new Object[] {idCasaDeCambio,Integer.parseInt(idBancoCasaDeCambio),idChequeraCasaDeCambio,foliosHijo};

		sql = sb.toString(); 

		try{
			jdbcTemplate.update(sql, params);
			return new ResultadoDto(true, "Se ejecuto correctamente la actualizacion de transfer", null);
		}catch(Exception e){
			return new ResultadoDto(false, "Error: En la actualizacion de la transfer. Reportarlo a su Administrador.", null);
		}


	}//END METHOD 

	@Override
	public int guardarCartaCVT(ConfirmacionCargoCtaDto dto) {

		String sql = null;
		StringBuilder sb = new StringBuilder();
		int iResp = 0;

		sb.append( "INSERT INTO CARTAS_CVD(" 	).append("\n");
		sb.append( "id_registro,"				).append("\n");
		sb.append( "no_docto,"					).append("\n");
		sb.append( "no_empresa,"				).append("\n");
		sb.append( "nom_empresa,"				).append("\n");
		sb.append( "fecha_valor,"				).append("\n");
		sb.append( "fecha_liquidacion,"			).append("\n");
		sb.append( "id_divisa_venta,"			).append("\n");
		sb.append( "desc_divisa_pago,"			).append("\n");
		sb.append( "id_banco_cargo,"			).append("\n");
		sb.append( "nom_banco_cargo,"			).append("\n");
		sb.append( "id_chequera_cargo,"			).append("\n");
		sb.append( "importe_venta,"				).append("\n");
		sb.append( "id_divisa_abono,"			).append("\n");
		sb.append( "desc_divisa_abono,"			).append("\n");
		sb.append( "id_banco_abono,"			).append("\n");
		sb.append( "nom_banco_abono,"			).append("\n");
		sb.append( "id_chequera_abono,"			).append("\n");
		sb.append( "tipo_cambio,"				).append("\n");
		sb.append( "importe_compra,"			).append("\n");
		sb.append( "no_persona,"				).append("\n");
		sb.append( "nom_casa_cambio,"			).append("\n");
		sb.append( "direccion,"					).append("\n");
		sb.append( "colonia,"					).append("\n");
		sb.append( "cp,"						).append("\n");
		sb.append( "ciudad,"					).append("\n");
		sb.append( "id_banco_casa_cambio,"		).append("\n");
		sb.append( "desc_banco_casa_cambio,"	).append("\n");
		sb.append( "id_chequera_casa_cambio,"	).append("\n");
		sb.append( "operador,"					).append("\n");
		sb.append( "concepto,"					).append("\n");
		sb.append( "id_grupo,"					).append("\n");
		sb.append( "desc_grupo,"				).append("\n");
		sb.append( "id_rubro,"					).append("\n");
		sb.append( "desc_rubro,"				).append("\n");
		sb.append( "estatus)"					).append("\n");
		sb.append( "VALUES ((select coalesce((max(id_registro)+1),0) from CARTAS_CVD),?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"	).append("\n");

		DecimalFormat dTCambio = new DecimalFormat("##.0000");
		DecimalFormat dTImporte = new DecimalFormat("#########.00");

		String importeVenta = dTImporte.format(dto.getImporteVenta());
		String tipoCambio = dTCambio.format(dto.getTipoCambio());
		String importeCompra = dTImporte.format(dto.getImporteCompra());

		Object[] params = new Object[] { 
				dto.getNoDocto(),
				dto.getNoEmpresa(),
				dto.getNomEmpresa(),
				"SIN VALOR",
				"SIN VALOR",	    	
				dto.getDivisaVenta().toString(),
				dto.getDescDivisaVenta(), 	    	
				String.valueOf(dto.getBancoCargo()),
				dto.getDescBancoCargo(),	    	
				dto.getChequeraCargo().toString(),
				importeVenta	   , 	
				dto.getDivisaCompra().toString(),
				dto.getDescDivisaCompra(),
				String.valueOf(dto.getBancoAbono()),
				dto.getDescBancoAbono(),
				dto.getChequeraAbono().toString(),
				tipoCambio,    	
				importeCompra,
				String.valueOf(dto.getIdCasaCambio()),
				String.valueOf(dto.getDescCasaCambio()),
				"SIN VALOR",
				"SIN VALOR",
				"SIN VALOR",
				"SIN VALOR",
				String.valueOf(dto.getIdBanco()),
				dto.getDescBancoCasa(),
				String.valueOf(dto.getIdChequera()),
				"SIN VALOR",
				dto.getConcepto(),
				"SIN VALOR",
				"SIN VALOR",
				"SIN VALOR",
				"SIN VALOR",
				"A"
		};

		sql = sb.toString(); 

		try{

			iResp = jdbcTemplate.update(sql, params);

		}catch( Exception e ){
			e.printStackTrace();

		}

		return iResp;
	}

	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public Map<String, Object> ejecutarPagador(StoreParamsComunDto dto)
	{
		//consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		//return consultasGenerales.ejecutarPagador(dto);

		Map<String, Object> resultado = null;

		try{
			StoredProcedure storedProcedure = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_sql_Pagador") {};
			storedProcedure.declareParameter(new SqlParameter("parametro",Types.VARCHAR ));
			storedProcedure.declareParameter(new SqlInOutParameter("mensaje",Types.VARCHAR));
			storedProcedure.declareParameter(new SqlInOutParameter("result",Types.INTEGER));

			Map< String, Object> inParams = new HashMap< String, Object >();

			System.out.println( "\n--PARAMETROS STORE--" );
			System.out.println( "PARAMETRO: "  + dto.getParametro());
			System.out.println( "MENSAJE  : "  + dto.getMensaje());
			System.out.println( "RESULTADO: "  + dto.getResult());

			inParams.put("parametro",dto.getParametro());
			inParams.put("mensaje",dto.getMensaje());
			inParams.put("result",dto.getResult());

			resultado = storedProcedure.execute( inParams );

		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString() + "P:GRAL, C:ConfirmacionCargoCtaDaoImpl, M:ejecutarPagador");				
		}

		return resultado;


	}//END METHOD: ejecutarPagador

	@Override
	public int cambioDeDivisa(int noUsuario, String idDivisaPago,String idBancoPagador,String idChequeraPagadora, String tipoCambio, String folio) {

		StringBuilder sb = new StringBuilder();
		String sql = null; 
		Object[] params = { noUsuario, idDivisaPago,idBancoPagador, idChequeraPagadora, tipoCambio, tipoCambio,folio};

		sb.append( "UPDATE Movimiento SET" 		).append("\n");
		sb.append( "usuario_modif      = ?,"    ).append("\n");
		sb.append( "id_divisa          = ?,"   	).append("\n");
		sb.append( "id_banco           = ?,"   	).append("\n");
		sb.append( "id_chequera        = ?,"   	).append("\n");
		sb.append( "tipo_cambio        = ?,"   	).append("\n");
		sb.append( "importe = importe  * ?"	  	).append("\n");
		sb.append( "WHERE no_folio_det = ?"	   	).append("\n");

		sql = sb.toString();

		return jdbcTemplate.update(sql,params);

	}

	@Override
	public Map<String, Object> getDatosReporteCVT(String folioRep) {

		String sql = null;
		StringBuilder sb = new StringBuilder();

		sb.append( "select  convert(numeric,cvd.importe_venta) as imp_venta," ).append( "\n" ); 
		sb.append( "convert(numeric,cvd.importe_compra) as imp_compra," 		).append( "\n" );
		sb.append( "cvd.*" 												).append( "\n" );
		sb.append( "from cartas_cvd cvd" 								).append( "\n" );
		sb.append( "WHERE cvd.no_docto = ?"						        ).append( "\n" );

		Object[] params = {folioRep};

		sql = sb.toString(); 

		return jdbcTemplate.queryForMap(sql, params );

	}
	@Override
	public Map<String, Object> generadorPagoParcial(String parametro,  double interes, double iva) {
		Map<String, Object> resultado = null;
		try{
			System.out.println("P:"+parametro);
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			resultado= consultasGenerales.ejecutarPagoParcial(parametro,interes,iva);
		}catch(Exception e){
			logger.error("Error", e);
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C: ConfirmacionCargoCtaDaoImpl, M: generadorPagoParcial");
		}
		return resultado;
	}
	public List<AmortizacionCreditoDto> obtenerImportes(int folio ) {
		String sql="";
		List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>();
		try{
			sql+= "SELECT concepto,importe from movimiento";
			sql+= " WHERE folio_ref="+folio;
			sql+= " AND origen_mov='CRD'";
			sql+= " AND id_estatus_mov='H'";
			int i=0;
			System.out.println("obtenerImporte "+sql);
			List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql);
			AmortizacionCreditoDto cons = new AmortizacionCreditoDto();
			for (Map row : rows) {
				if(rows.get(i).get("concepto").toString().toLowerCase().indexOf("interes") != -1){
					cons.setInteres(Double.parseDouble(rows.get(i).get("importe").toString()));
				}
				else if(rows.get(i).get("concepto").toString().toLowerCase().indexOf("iva") != -1){				
					cons.setIva(Double.parseDouble(rows.get(i).get("importe").toString()));
				}
				else {					
					cons.setCapital(Double.parseDouble(rows.get(i).get("importe").toString()));
				}
				i=i+1;
			}
			list.add(cons);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerImportes");
		}
		return list;
	}
	
	
	public Map<String, Object> crearInteres(int folio,double interes,int secuencia,String cveControl) {
		Map<String, Object> resultado = null;
		try{
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			resultado= consultasGenerales.crearInteres(folio,interes,secuencia,cveControl);
		}catch(Exception e){
			logger.error("Error", e);
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C: ConfirmacionCargoCtaDaoImpl, M: generadorPagoParcial");
		}
		return resultado;
	}
}
