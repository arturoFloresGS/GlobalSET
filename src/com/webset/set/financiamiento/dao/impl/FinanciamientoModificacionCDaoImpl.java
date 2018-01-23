package com.webset.set.financiamiento.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.financiamiento.dao.FinanciamientoModificacionCDao;
import com.webset.set.financiamiento.dto.AmortizacionCreditoDto;
import com.webset.set.financiamiento.dto.ContratoCreditoDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;

public class FinanciamientoModificacionCDaoImpl implements FinanciamientoModificacionCDao {

	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora=new Bitacora();
	ConsultasGenerales consultasGenerales;
	Funciones funciones= new Funciones();
	GlobalSingleton globalSingleton;
	String vsTipoMenu="";
	String gsDBM = ConstantesSet.gsDBM;
	private static Logger logger = Logger.getLogger(FinanciamientoModificacionCDaoImpl.class);
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Financiamiento, C: FinanciamientoModificacionCDaoImpl, M:setDataSource");
		}
	}
	public GlobalSingleton getGlobalSingleton() {
		return globalSingleton;
	}
	public void setGlobalSingleton(GlobalSingleton globalSingleton) {
		this.globalSingleton = globalSingleton;
	}
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	public List<LlenaComboGralDto> obtenerContratos(int idEmpresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		StringBuffer sbSQL = new StringBuffer();
		try {
			globalSingleton = GlobalSingleton.getInstancia();
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
			+ "P:Financiamiento, C:FinanciamientoModificacionCDaoImpl, M:obtenerContratos");
		}
		return list;
	}

	@Override
	public List<LlenaComboGralDto> obtenerDisposiciones(String psIdContrato, boolean pbEstatus) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n SELECT id_disposicion as ID, id_disposicion as Descrip ");
			sbSQL.append("\n FROM  cat_disposicion_credito ");
			if(pbEstatus){
				sbSQL.append("\n  WHERE id_financiamiento in (" + psIdContrato + ") ");
				sbSQL.append("\n   And estatus = 'A' ");
			}else{
				sbSQL.append("\n  WHERE id_financiamiento in ('" + psIdContrato + "') ");
				sbSQL.append("\n    And estatus = 'A' ");
			}
			logger.info("llenarCmbContratos "+sbSQL);
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
			+ "P:Financiamiento, C:GastosFinanciamientoCDaoImpl, M:obtenerDisposiciones");
		}
		return list;
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
	public List<AmortizacionCreditoDto> selectAmortizaciones(String psIdContrato, int psIdDisposicion,
			boolean pbCambioTasa, String psTipoMenu, String psProyecto, int piCapital) {
		List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		String nombreBanco="";
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
				if(psProyecto.equals("CIE")&&psTipoMenu.equals("A"))
					sbSQL.append("\n    AND cac.interes > 0 ");
				sbSQL.append("\n     AND cac.estatus = 'A' ");
			} else if (piCapital > 0) {
				sbSQL.append("\n   AND cac.capital < 0 ");
			}
			if (piCapital <= 0) {
				sbSQL.append("\n   AND cac.capital = 0 ");
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
			nombreBanco=selectNombreBanco(psIdContrato).get(0).getDescInstitucion();
			System.out.println(sbSQL.toString());
			logger.info("selectAmortizaciones "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public AmortizacionCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					String psTasa="";
					Double tasaVigenteAux,tasaFijaAux,valorTasaAux, puntosAux;
					if(idx==0){
						String psTasaFijaVariable = "",psPeriodo="";
						double pdTasaPuntos=0,pdTasaBase=0,pdTasaAnt=0;

						if(!rs.getString("tasa").equals("")||rs.getString("tasa")!=null)
							psTasaFijaVariable=rs.getString("tasa");
						if(rs.getDouble("puntos")!=0)
							pdTasaPuntos=rs.getDouble("puntos");
						if(rs.getDouble("valor_tasa")!=0)
							pdTasaPuntos=rs.getDouble("valor_tasa");
						if(!rs.getString("periodo").equals("")||rs.getString("periodo")!=null)
							psPeriodo=rs.getString("periodo");   
					}
					AmortizacionCreditoDto dto = new AmortizacionCreditoDto();
					dto.setIdContrato(rs.getString("id_contrato"));
					dto.setIdDisposicion(rs.getInt("id_disposicion"));
					dto.setIdAmortizacion(rs.getInt("id_amortizacion"));
					dto.setFecPago(funciones.ponerFechaSola(funciones.cambiarFecha(rs.getString("fec_pago"))));
					dto.setFecInicio(funciones.ponerFechaSola(funciones.cambiarFecha(rs.getString("fec_inicio"))));
					dto.setFecVencimiento(funciones.ponerFechaSola(funciones.cambiarFecha(rs.getString("fec_vencimiento"))));
					dto.setDias(funciones.diasEntreFechas(funciones.ponerFechaDate(funciones.cambiarFecha(rs.getString("fec_inicio"))),funciones.ponerFechaDate(funciones.cambiarFecha(rs.getString("fec_vencimiento")))));
					dto.setCapital(rs.getDouble("capital"));
					dto.setInteres(rs.getDouble("interes"));
					dto.setRenta(rs.getDouble("renta"));
					dto.setSaldoInsoluto(rs.getDouble("saldo_insoluto"));
					dto.setEstatus(rs.getString("estatus").charAt(0));
					double pdTasaAnt=0;
					if(rs.getString("tasa").trim().equals("V")){
						tasaVigenteAux=rs.getDouble("tasa_vigente");
						dto.setTasaVigente((tasaVigenteAux==null)?0:rs.getDouble("tasa_vigente"));
						pdTasaAnt = (tasaVigenteAux==null)?0:rs.getDouble("tasa_vigente");
					}
					else{
						tasaFijaAux=rs.getDouble("tasa_fija");
						dto.setTasaVigente((tasaFijaAux==null)?0:rs.getDouble("tasa_fija"));
						pdTasaAnt = (tasaFijaAux==null)?0:rs.getDouble("tasa_fija");
					}
					Integer diasPeriodoAux=rs.getInt("dias_periodo");
					dto.setPeriodo((diasPeriodoAux==null)?"":Integer.toString(rs.getInt("dias_periodo")));
					Integer noAmortizacionesAux=rs.getInt("no_amortizaciones");
					dto.setNoAmortizaciones((noAmortizacionesAux==null)?"":Integer.toString(rs.getInt("no_amortizaciones")));
					valorTasaAux=rs.getDouble("valor_tasa");
					dto.setValorTasa((valorTasaAux==null)?0:rs.getDouble("valor_tasa"));
					puntosAux=rs.getDouble("puntos");
					dto.setPuntos((puntosAux==null)?0:rs.getDouble("puntos"));
					dto.setTipoTasaBase(rs.getString("tasa"));
					dto.setIdTasaBase(rs.getString("tasa_base"));
					dto.setTasaBase(rs.getString("tasa_base"));
					dto.setIva(rs.getDouble("iva"));
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

	public List<AmortizacionCreditoDto> selectNombreBanco(String psIdContrato) {
		List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		String nombreBanco="";
		try {
			sbSQL.append("\n SELECT ccc.id_banco, cb.desc_banco ");
			sbSQL.append("\n FROM  cat_contrato_credito ccc, cat_banco cb ");
			sbSQL.append("\n WHERE ccc.id_financiamiento = '" + psIdContrato + "' ");
			sbSQL.append("\n       AND cb.id_banco = ccc.id_banco ");
			logger.info("selectNombreBanco "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public AmortizacionCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					AmortizacionCreditoDto dto= new AmortizacionCreditoDto();
					dto.setDescInstitucion(rs.getString("desc_banco"));
					return dto;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: FinanciamientoModificacionC, C: FinanciamientoModificacionCDaoImpl, M:selectNombreBanco");
		}
		if(list.isEmpty()){
			AmortizacionCreditoDto dto= new AmortizacionCreditoDto();
			dto.setDescInstitucion("NINGUNO");
		}
		return list;
	}	

	public List<AmortizacionCreditoDto> selectNombreTasa(String tasa) {
		List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\nSELECT * FROM cat_tasas");
			sbSQL.append("\n WHERE id_tasa = '"+ tasa+ "'");
			logger.info("selectNombreTasa "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public AmortizacionCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					AmortizacionCreditoDto dto= new AmortizacionCreditoDto();
					dto.setDescTasa(rs.getString("desc_tasa"));
					return dto;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: FinanciamientoModificacionC, C: FinanciamientoModificacionCDaoImpl, M:selectNombreTasa");
		}
		if(list.isEmpty()){
			AmortizacionCreditoDto dto= new AmortizacionCreditoDto();
			dto.setDescTasa("");
		}
		return list;
	}

	@Override
	public List<LlenaComboGralDto> consultarTasa() {
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listDisp= new ArrayList<LlenaComboGralDto>();
		try{
			sbSql.append("\n SELECT  id_tasa as ID, desc_tasa as Describ ");
			sbSql.append("\n FROM    cat_tasas ");
			sbSql.append("\n WHERE   NOT desc_tasa LIKE '%CIE%' ");
			listDisp = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
					dtoCons.setDescripcion(rs.getString("Describ"));
					dtoCons.setIdStr(rs.getString("ID"));
					return dtoCons;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: FinanciamientoModificacionC, C: FinanciamientoModificacionCDaoImpl, M:consultarTasa");
		}
		return listDisp;
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
			+ "P:Financiamiento, C:FinanciamientoModificacionCDaoImpl, M:funSQLTasa");
		}
		return list;
	}

	@Override
	public int updTasaVariableDisp(String vsLinea, int vlDisp, String vsTipoTasa, String vsIdTasaBasa,
			String vdValorTasa, String vdPuntos) {
		try {
			StringBuffer sbSQL = new StringBuffer();
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n UPDATE cat_disposicion_credito ");
			sbSQL.append("\n SET   tipo_tasa = '" + vsTipoTasa + "', tasa_base = '" + vsIdTasaBasa + "', ");
			sbSQL.append("\n      valor_tasa = "  +vdValorTasa+  ", puntos = " +vdPuntos +" ");
			sbSQL.append("\n WHERE id_financiamiento =  '" + vsLinea + "' ");
			sbSQL.append("\n      AND id_disposicion = " + vlDisp + " ");
			sbSQL.append("\n      AND estatus = 'A'");
			System.out.println(sbSQL.toString());
			logger.info("updTasaVariableDisp "+sbSQL);
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:FinanciamientoModificacionCDaoImpl, M:updTasaVariableDisp");
			return 0;
		}
	}

	@Override
	public int updateAmortizacion(String pvIdContrato, int pvIdDisposicion, int pvIdAmortizacion, double pvCapital, double pvInteres,
			String pvTasaVigente, String pvTasaFija, String pvTasaBase, String pvValorTasa, String pvPuntos,
			String pvTasa, Date pvFecVen, String pvStatus, boolean pbRenta, String pdRenta, String pdIva,
			Date psFechaBusca, boolean pbSegundo, Date pvFecIni) {
		try {
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			Date fechaHoy = consultasGenerales.obtenerFechaHoy();
			StringBuffer sbSQL = new StringBuffer();
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n UPDATE cat_amortizacion_credito" );
			if (!pvStatus.equals("")){
				sbSQL.append("\n SET estatus = '" + pvStatus + "', usuario_modif = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", fec_modif = '"+ fechaHoy+"' ");
				sbSQL.append("\n WHERE id_contrato = '" + pvIdContrato + "'");
				sbSQL.append("\n AND id_disposicion = " +pvIdDisposicion);
				sbSQL.append("\n AND id_amortizacion = "+pvIdAmortizacion);
				sbSQL.append("\n AND estatus = 'A'");
				if(!pvFecVen.equals("")){
					if(gsDBM.equals("SQL SERVER"))
						sbSQL.append("\n AND cast(fec_vencimiento as datetime) = '" +funciones.fechaString(pvFecVen) + "'");
					else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
						sbSQL.append("\n AND cast(fec_vencimiento as date) = '" +funciones.fechaString(pvFecVen) + "'");
				}
			}
			else{
				sbSQL.append("\n  SET ");
				if (pbSegundo) sbSQL.append("\n fec_inicio = '" + funciones.fechaString(pvFecIni) + "', ");
				sbSQL.append("\n capital = "+pvCapital+ "," );
				sbSQL.append("\n interes = "+pvInteres+",");
				sbSQL.append("\n tasa_vigente = " +pvTasaVigente+ "," );
				sbSQL.append("\n tasa_fija = " +pvTasaFija+"," );
				sbSQL.append("\n tasa_base = '" +pvTasaBase +"'," );
				sbSQL.append("\n valor_tasa = " +pvValorTasa+ "," );
				sbSQL.append("\n puntos = " +pvPuntos+ "," );
				sbSQL.append("\n tasa = '" + pvTasa + "', ");
				sbSQL.append("\n fec_vencimiento = '" + funciones.fechaString(pvFecVen) + "', ");
				sbSQL.append("\n fec_pago = '" + funciones.fechaString(pvFecVen) + "', " );
				sbSQL.append("\n usuario_modif = " +globalSingleton.getUsuarioLoginDto().getIdUsuario()+ ", fec_modif = '" + fechaHoy+ "', ");
				sbSQL.append("\n  iva = " + pdIva);
				if(pbRenta)
					sbSQL.append("\n , solo_renta = 1, renta = " +pdRenta+ " ");
				sbSQL.append("\n WHERE id_contrato = '" + pvIdContrato + "'");
				sbSQL.append("\n AND id_disposicion = " + pvIdDisposicion);
				sbSQL.append("\n AND estatus = 'A'");
				if(pvIdAmortizacion == 0)
					sbSQL.append("\n AND id_amortizacion = " + pvIdAmortizacion);
				else
					sbSQL.append("\n And capital <> 0 ");
				if(gsDBM.equals("SQL SERVER"))
					sbSQL.append("\n AND cast(fec_vencimiento as date) = '" + funciones.fechaString(psFechaBusca) + "'");
				else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
					sbSQL.append("\n AND cast(fec_vencimiento as date) = '" + funciones.fechaString(psFechaBusca) + "'");
			}
			System.out.println(sbSQL.toString());
			logger.info("updateAmortizacion "+sbSQL);
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:FinanciamientoModificacionCDaoImpl, M:updateAmortizacion");
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
			logger.info("obtieneFolioAmort "+sbSQL);
			System.out.println(sbSQL.toString());
			folioAmort = jdbcTemplate.queryForInt(sbSQL.toString());
			System.out.println(folioAmort);
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:FinanciamientoModificacionCDaoImpl, M:obtieneFolioAmort");
		}
		return folioAmort;
	}
}
