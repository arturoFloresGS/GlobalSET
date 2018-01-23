package com.webset.set.financiamiento.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.coinversion.dao.impl.CoinversionDaoImpl;
import com.webset.set.financiamiento.dao.ProvisionInteresesCDao;
import com.webset.set.financiamiento.dto.ProvisionCreditoDTO;
import com.webset.set.global.dao.GlobalDao;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public class ProvisionInteresesCDaoImpl implements ProvisionInteresesCDao {
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
	public List<LlenaComboGralDto> llenarCmbEmpresa(int piNoUsuario, boolean pbMismaEmpresa, int plEmpresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n  SELECT e.no_empresa as ID, e.nom_empresa as Descrip ");
			sbSQL.append("\n  FROM   empresa e, usuario_empresa ue ");
			sbSQL.append("\n  WHERE  e.no_empresa = ue.no_empresa ");
			sbSQL.append("\n    And ue.no_usuario = " + piNoUsuario + " ");
			if (pbMismaEmpresa)
				sbSQL.append("\n   And ue.no_empresa <> " + plEmpresa + " ");
			sbSQL.append("\n ORDER BY e.no_empresa ");
			System.out.println(sbSQL.toString());
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
			+ "P:Financiamiento, C:ProvisionInteresesCDaoImpl, M:llenarCmbEmpresa");
		}
		return list;
	}
	@Override
	public List<ProvisionCreditoDTO> selectProvisionHistorico(String psFecha, int plEmpresa, String psFechaIni,
			String psTipoFuncion, String psDivisa){
		List<ProvisionCreditoDTO> list = new ArrayList<ProvisionCreditoDTO>();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		StringBuffer sbSQL = new StringBuffer();
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n SELECT p.id_financiamiento, p.id_disposicion, p.no_empresa, e.nom_empresa, p.id_banco, ");
			sbSQL.append("\n   p.fec_ini_prov, p.fec_fin_prov, p.id_divisa, cd.desc_divisa, p.monto_saldo, p.monto_provision, ");
			sbSQL.append("\n   p.tasa , p.estatus, p.fec_calculo_prov, coalesce(vd.valor, 0) As valor ,cb.desc_banco ");
			sbSQL.append("\n FROM  provisiones p left join valor_divisa vd on (p.id_divisa = vd.id_divisa and vd.fec_divisa = '" +funciones.cambiarOrdenFecha(funciones.ponerFecha(fechaHoy))+ "'), ");
			sbSQL.append("\n       cat_divisa cd, empresa e ,cat_banco cb ");
			sbSQL.append("\n WHERE p.id_banco = cb.id_banco ");
			sbSQL.append("\n   And p.id_divisa = cd.id_divisa ");
			sbSQL.append("\n   And p.no_empresa = e.no_empresa ");
			if(plEmpresa!= 0)
				sbSQL.append("\n   And p.no_empresa = " + plEmpresa + " ");
			if (!psDivisa.equals("0"))
				sbSQL.append("\n   And p.id_divisa = '" +psDivisa+ "' ");
			sbSQL.append("\n   And e.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " +globalSingleton.getUsuarioLoginDto().getIdUsuario()+") ");
			if(gsDBM.equals("SQL SERVER")){
				if (psTipoFuncion.equals("H"))
					sbSQL.append("\n   And (cast(fec_fin_prov as date) >= '" + funciones.cambiarOrdenFecha(psFechaIni) + "' And cast(fec_fin_prov as date) <= '" + funciones.cambiarOrdenFecha(psFecha) + "') ");
				if (psTipoFuncion.equals("F")) 
					sbSQL.append("\n  And (cast(fec_ini_prov as date) <= '" + fechaHoy + "' and cast(fec_fin_prov as date) >= '" + fechaHoy + "') ");
				sbSQL.append("\n ORDER BY p.id_financiamiento, p.id_disposicion, cast(fec_ini_prov as date) ");
			}
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE")){
				if (psTipoFuncion.equals("H"))
					sbSQL.append("\n   And (cast(fec_fin_prov as date) >= '" + psFechaIni + "' And cast(fec_fin_prov as date) <= '" + psFecha + "') ");
				if (psTipoFuncion.equals("F")) 
					sbSQL.append("\n   And (cast(fec_ini_prov as date) <= '" + fechaHoy + "' and cast(fec_fin_prov as date) >= '" + fechaHoy + "') ");
				sbSQL.append("\n ORDER BY p.id_financiamiento, p.id_disposicion, cast(fec_ini_prov as date) ");
			}
			System.out.println(sbSQL.toString());
			logger.info("selectProvisionHistorico "+sbSQL);
			final String psTipoFuncionAux=psTipoFuncion;
			final Date fechaHoyAux=fechaHoy;
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public ProvisionCreditoDTO mapRow(ResultSet rs, int idx) throws SQLException {
					ProvisionCreditoDTO dto = new ProvisionCreditoDTO();
					int dias=0;
					dto.setNoEmpresa(rs.getInt("no_empresa"));
					dto.setDescEmpresa(rs.getString("nom_empresa"));
					dto.setIdBanco(rs.getInt("id_banco"));
					dto.setDescBanco(rs.getString("desc_banco"));
					dto.setIdFinanciamiento(rs.getString("id_financiamiento"));
					dto.setIdDisposicion(rs.getInt("id_disposicion"));
					dto.setiDivisa(rs.getString("id_divisa"));
					dto.setDescDivisa(rs.getString("desc_divisa"));
					dto.setValorTasa(rs.getDouble("Tasa"));
					dto.setValor(rs.getDouble("valor"));
					if(psTipoFuncionAux.equals("H")){
						System.out.println("Entra en H");
						dto.setFecInicial(funciones.ponerFechaSola(funciones.cambiarFecha(rs.getString("fec_ini_prov"))));
						dto.setFecFinal(funciones.ponerFechaSola(funciones.cambiarFecha(rs.getString("fec_fin_prov"))));
						dto.setDias(funciones.diasEntreFechas(funciones.ponerFechaDate3(funciones.cambiarFecha(rs.getString("fec_ini_prov"))),funciones.ponerFechaDate3(funciones.cambiarFecha(rs.getString("fec_fin_prov")))));
						dto.setCapital(rs.getDouble("monto_saldo"));
						dto.setMontoProvision(rs.getDouble("Monto_Provision"));
					}
					if(psTipoFuncionAux.equals("F")){
						System.out.println("Entra en F");
						dto.setFecInicial(funciones.ponerFechaSola(funciones.cambiarFecha(rs.getString("fec_ini_prov"))));
						dto.setFecFinal(funciones.ponerFechaSola(fechaHoyAux));
						dias=funciones.diasEntreFechas(funciones.ponerFechaDate3(funciones.cambiarFecha(rs.getString("fec_ini_prov"))),fechaHoyAux)+1;
						dto.setDias(dias);
						dto.setCapital(rs.getDouble("monto_saldo"));
						dto.setMontoProvision(((rs.getDouble("Tasa")/100/360)*rs.getDouble("monto_saldo"))*dias);
					}

					return dto;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:ProvisionInteresesCDaoImpl, M:selectProvisionHistorico");
		}
		return list;
	}
	@Override
	public List<ProvisionCreditoDTO> selectProvisionMesHoy(String psFecha, int plEmpresa, String psFechaIni,
			String psDivisa){
		List<ProvisionCreditoDTO> list = new ArrayList<ProvisionCreditoDTO>();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		StringBuffer sbSQL = new StringBuffer();
		try {
			globalSingleton = GlobalSingleton.getInstancia();

			sbSQL.append("\n SELECT p.id_financiamiento, p.id_disposicion, p.no_empresa, e.nom_empresa, p.id_banco, ");
			sbSQL.append("\n   p.fec_ini_prov, p.fec_fin_prov, p.id_divisa, cd.desc_divisa, p.monto_saldo, p.monto_provision, ");
			sbSQL.append("\n   p.tasa , p.estatus, p.fec_calculo_prov, ");

			if(gsDBM.equals("SQL SERVER")){
				sbSQL.append("\n   case when (cast(fec_ini_prov as date) <= '" + funciones.cambiarOrdenFecha(psFechaIni) + "' And cast(fec_fin_prov as date) >= '" + funciones.cambiarOrdenFecha(psFecha) + "') then 1 else ");
				sbSQL.append("\n   case when (cast(fec_ini_prov as date) <= '" + funciones.cambiarOrdenFecha(psFechaIni) + "' And ");
				sbSQL.append("\n             (cast(fec_fin_prov as date) >= '" + funciones.cambiarOrdenFecha(psFechaIni) + "' And cast(fec_fin_prov as date) <= '" + funciones.cambiarOrdenFecha(psFecha) + "')) then 2 else ");
				sbSQL.append("\n   case when ((cast(fec_ini_prov as date) >= '" + funciones.cambiarOrdenFecha(psFechaIni) + "' And cast(fec_ini_prov as date) <= '" + funciones.cambiarOrdenFecha(psFecha) + "') And ");
				sbSQL.append("\n             (cast(fec_fin_prov as date) >= '" + funciones.cambiarOrdenFecha(psFecha) + "')) then 3 else ");
				sbSQL.append("\n   case when ((cast(fec_ini_prov as date) >= '" + funciones.cambiarOrdenFecha(psFechaIni) + "' And cast(fec_ini_prov as date) <= '" + funciones.cambiarOrdenFecha(psFecha) + "') And ");
				sbSQL.append("\n             (cast(fec_fin_prov as date) >= '" + funciones.cambiarOrdenFecha(psFechaIni) + "' And cast(fec_fin_prov as date) <= '" + funciones.cambiarOrdenFecha(psFecha) + "')) then 4 end end end end as casos, ");
			}
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE")){
				sbSQL.append("\n   case when (cast(fec_ini_prov as date) <= '" + funciones.cambiarOrdenFecha(psFechaIni) + "' And cast(fec_fin_prov as date) >= '" + funciones.cambiarOrdenFecha(psFecha) + "') then 1 else ");
				sbSQL.append("\n   case when (cast(fec_ini_prov as date) <= '" + funciones.cambiarOrdenFecha(psFechaIni) + "' And ");
				sbSQL.append("\n             (cast(fec_fin_prov as date) >= '" + funciones.cambiarOrdenFecha(psFechaIni) + "' And cast(fec_fin_prov as date) <= '" + funciones.cambiarOrdenFecha(psFecha) + "')) then 2 else ");
				sbSQL.append("\n   case when ((cast(fec_ini_prov as date) >= '" + funciones.cambiarOrdenFecha(psFechaIni) + "' And cast(fec_ini_prov as date) <= '" + funciones.cambiarOrdenFecha(psFecha) + "') And ");
				sbSQL.append("\n             (cast(fec_fin_prov as date) >= '" + funciones.cambiarOrdenFecha(psFecha) + "')) then 3 else ");
				sbSQL.append("\n   case when ((cast(fec_ini_prov as date) >= '" + funciones.cambiarOrdenFecha(psFechaIni) + "' And cast(fec_ini_prov as date) <= '" + funciones.cambiarOrdenFecha(psFecha) + "') And ");
				sbSQL.append("\n             (cast(fec_fin_prov as date) >= '" + funciones.cambiarOrdenFecha(psFechaIni) + "' And cast(fec_fin_prov as date) <= '" +funciones.cambiarOrdenFecha(psFecha) + "')) then 4 end end end end as casos, ");
			}
			sbSQL.append("\n   coalesce(vd.valor, 0) As valor, cb.desc_banco ");
			sbSQL.append("\n FROM  provisiones p left join valor_divisa vd on (p.id_divisa = vd.id_divisa and vd.fec_divisa = '" +funciones.cambiarOrdenFecha(funciones.ponerFechaSola(fechaHoy))+ "'), ");
			sbSQL.append("\n       cat_divisa cd, empresa e, cat_banco cb ");
			sbSQL.append("\n WHERE p.id_banco = cb.id_banco ");
			sbSQL.append("\n   And p.id_divisa = cd.id_divisa ");
			sbSQL.append("\n   And p.no_empresa = e.no_empresa ");
			sbSQL.append("\n   And p.estatus = 'A' ");
			sbSQL.append("\n   And e.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() +") ");

			if(!psDivisa.trim().equals("0"))
				sbSQL.append("\n   And p.id_divisa = '" + psDivisa + "' ");
			if(plEmpresa!=0)
				sbSQL.append("\n And p.no_empresa = " + plEmpresa + " ");

			if(gsDBM.equals("SQL SERVER")){
				sbSQL.append("\n    And (cast(fec_fin_prov as date) >= '" + funciones.cambiarOrdenFecha(psFechaIni) + "' And cast(fec_fin_prov as date) <= '" + funciones.cambiarOrdenFecha(psFecha) + "') ");
				sbSQL.append("\n  ORDER BY p.id_financiamiento, p.id_disposicion, cast(fec_ini_prov as date) ");
			} 
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE")){
				sbSQL.append("\n    And (cast(fec_fin_prov as date) >= '" + psFechaIni + "' And cast(fec_fin_prov as date) <= '" + psFecha + "') ");
				sbSQL.append("\n  ORDER BY p.id_financiamiento, p.id_disposicion, cast(fec_ini_prov as date) ");
			}
			System.out.println(sbSQL.toString());
			logger.info("selectProvisionMesHoy "+sbSQL);
			final String vsFecIniOri=psFechaIni;
			final Date fechaHoyAux=fechaHoy;

			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public ProvisionCreditoDTO mapRow(ResultSet rs, int idx) throws SQLException {
					String vsFecha="";
					String vsFechaIni="";
					ProvisionCreditoDTO dto = new ProvisionCreditoDTO();
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					int dias=0;
					dto.setNoEmpresa(rs.getInt("no_empresa"));
					dto.setDescEmpresa(rs.getString("nom_empresa"));
					dto.setIdBanco(rs.getInt("id_banco"));
					dto.setDescBanco(rs.getString("desc_banco"));
					dto.setIdFinanciamiento(rs.getString("id_financiamiento"));
					dto.setIdDisposicion(rs.getInt("id_disposicion"));
					dto.setiDivisa(rs.getString("id_divisa"));
					dto.setDescDivisa(rs.getString("desc_divisa"));
					dto.setValorTasa(rs.getDouble("Tasa"));
					dto.setValor(rs.getDouble("valor"));
					vsFechaIni = vsFecIniOri;
					vsFecha =funciones.ponerFechaSola(fechaHoyAux);
					Date fecFinProvAux = null;
					String fecFinProv=funciones.ponerFechaSola(funciones.cambiarFecha(rs.getString("fec_fin_prov")));
					String fecIniProv=funciones.cambiarFecha(rs.getString("fec_ini_prov"));
					switch (rs.getInt("casos")) {
					case 2:
						try {
							fecFinProvAux = sdf.parse(fecFinProv);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						if(fecFinProvAux.compareTo(fechaHoyAux)<0)
							vsFecha = fecFinProv;
						break;
					case 3:
						vsFechaIni=fecIniProv;
						break;
					case 4:
						vsFechaIni=fecIniProv;
						try {
							fecFinProvAux = sdf.parse(fecFinProv);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						if(fecFinProvAux.compareTo(fechaHoyAux)<0)
							vsFecha = fecFinProv;
						break;
					}
					dto.setFecInicial(funciones.ponerFechaSola(vsFechaIni));
					dto.setFecFinal(vsFecha);
					dias=funciones.diasEntreFechas(funciones.ponerFechaDate(vsFechaIni),funciones.ponerFechaDate(vsFecha))+1;
					dto.setDias(dias);
					dto.setCapital(rs.getDouble("monto_saldo"));
					dto.setMontoProvision(((rs.getDouble("Tasa")/100/360)*rs.getDouble("monto_saldo"))*dias);
					return dto;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:ProvisionInteresesCDaoImpl, M:selectProvisionMesHoy");
		}
		return list;
	}
	@Override
	public List<ProvisionCreditoDTO> selectGeneraProvision(String psFecha, int plEmpresa,
			String psDivisa){
		List<ProvisionCreditoDTO> list = new ArrayList<ProvisionCreditoDTO>();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		StringBuffer sbSQL = new StringBuffer();
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n SELECT p.id_financiamiento, p.id_disposicion, p.no_empresa, e.nom_empresa, p.id_banco, ");
			sbSQL.append("\n   p.fec_ini_prov, p.fec_fin_prov, p.id_divisa, cd.desc_divisa, p.monto_saldo, p.monto_provision, ");
			sbSQL.append("\n   p.tasa , p.estatus, p.fec_calculo_prov, coalesce(vd.valor, 0) as valor, cb.desc_banco ");
			sbSQL.append("\n FROM  provisiones p left join valor_divisa vd on (p.id_divisa = vd.id_divisa and vd.fec_divisa = '" +funciones.cambiarOrdenFecha(funciones.ponerFechaSola(fechaHoy))+ "'), ");
			sbSQL.append("\n       cat_divisa cd, empresa e, cat_banco cb ");
			sbSQL.append("\n WHERE p.id_banco = cb.id_banco ");
			sbSQL.append("\n   And p.id_divisa = cd.id_divisa ");
			sbSQL.append("\n   And p.no_empresa = e.no_empresa ");
			sbSQL.append("\n   And p.estatus = 'A' ");
			sbSQL.append("\n   And e.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario()+ ") ");
			if(!psDivisa.trim().equals("0"))
				sbSQL.append("\n     And p.id_divisa= '" + psDivisa + "' ");
			if (plEmpresa!= 0)
				sbSQL.append("\n   And p.no_empresa = " + plEmpresa + " ");
			if(gsDBM.equals("SQL SERVER")){
				sbSQL.append("\n    And cast(fec_fin_prov as datetime) <= '" + funciones.cambiarOrdenFecha(psFecha) + "' ");
				sbSQL.append("\n  ORDER BY cast(fec_ini_prov as datetime), p.id_financiamiento, p.id_disposicion ");
			}
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE")){
				sbSQL.append("\n    And cast(fec_fin_prov as date) <= '" + psFecha +"' ");
				sbSQL.append("\n  ORDER BY cast(fec_ini_prov as date), p.id_financiamiento, p.id_disposicion ");
			}
			logger.info("selectGeneraProvision "+sbSQL);
			System.out.println(sbSQL.toString());
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public ProvisionCreditoDTO mapRow(ResultSet rs, int idx) throws SQLException {
					ProvisionCreditoDTO dto = new ProvisionCreditoDTO();
					int dias=0;
					dto.setNoEmpresa(rs.getInt("no_empresa"));
					dto.setDescEmpresa(rs.getString("nom_empresa"));
					dto.setIdBanco(rs.getInt("id_banco"));
					dto.setDescBanco(rs.getString("desc_banco"));
					dto.setIdFinanciamiento(rs.getString("id_financiamiento"));
					dto.setIdDisposicion(rs.getInt("id_disposicion"));
					dto.setiDivisa(rs.getString("id_divisa"));
					dto.setDescDivisa(rs.getString("desc_divisa"));
					dto.setValorTasa(rs.getDouble("Tasa"));
					dto.setValor(rs.getDouble("valor"));
					dto.setFecInicial(funciones.ponerFechaSola(funciones.cambiarFecha(rs.getString("fec_ini_prov"))));
					dto.setFecFinal(funciones.ponerFechaSola(funciones.cambiarFecha(rs.getString("fec_fin_prov"))));
					dto.setDias(funciones.diasEntreFechas(funciones.ponerFechaDate3(funciones.cambiarFecha(rs.getString("fec_ini_prov"))),funciones.ponerFechaDate3(funciones.cambiarFecha(rs.getString("fec_fin_prov")))));
					dto.setCapital(rs.getDouble("monto_saldo"));
					dto.setMontoProvision(rs.getDouble("Monto_Provision"));
					return dto;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:ProvisionInteresesCDaoImpl, M:selectProvisionHistorico");
		}
		return list;
	}

	@Override
	public List<ProvisionCreditoDTO> selectInhabil(String pvFechaInhabil){
		List<ProvisionCreditoDTO> list = new ArrayList<ProvisionCreditoDTO>();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		StringBuffer sbSQL = new StringBuffer();
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\nSELECT FEC_INHABIL");
			sbSQL.append("\n FROM DIA_INHABIL");
			if(gsDBM.equals("SQL SERVER")){
				sbSQL.append("\n WHERE cast(FEC_INHABIL as datetime) = '");
				sbSQL.append(funciones.cambiarOrdenFecha(pvFechaInhabil)+"'");
			}
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE")){
				sbSQL.append("\nWHERE cast(FEC_INHABIL as date) = '");
				sbSQL.append(pvFechaInhabil+"'");
			}
			System.out.println(sbSQL.toString());
			logger.info("selectInhabil "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public ProvisionCreditoDTO mapRow(ResultSet rs, int idx) throws SQLException {
					ProvisionCreditoDTO dto = new ProvisionCreditoDTO();
					dto.setFecInicial(rs.getString("FEC_INHABIL"));
					return dto;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:ProvisionInteresesCDaoImpl, M:selectInhabil");
		}
		return list;
	}
	@Override
	public int updateProvisionEstatus(ProvisionCreditoDTO dto) {
		int resultado=0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  UPDATE provisiones ");
			sbSQL.append("\n  SET estatus = 'P' ");
			sbSQL.append("\n WHERE id_financiamiento = '"+dto.getIdFinanciamiento()+ "' ");
			sbSQL.append("\n    And id_disposicion = " +dto.getIdDisposicion()+ "");
			sbSQL.append("\n    And fec_ini_prov = '"+dto.getFecInicial()+ "' ");
			sbSQL.append("\n    And fec_fin_prov = '" +dto.getFecFinal()+ "' ");
			logger.info("updateProvisionEstatus "+sbSQL);
			System.out.println(sbSQL.toString());
			resultado = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:ProvisionInteresesCDaoImpl, M:updateProvisionEstatus");
		}
		return resultado;
	}
	@Override
	public int updateProvisionX(ProvisionCreditoDTO dto) {
		int resultado=0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n delete provisiones ");
			sbSQL.append("\n WHERE id_financiamiento = '" +dto.getIdFinanciamiento()+"'");
			sbSQL.append("\n   And id_disposicion = " +dto.getIdDisposicion()+ "");
			sbSQL.append("\n   And estatus = 'A'");
			sbSQL.append("\n   And fec_fin_prov = '"+dto.getFecFinal()+ "'");
			System.out.println(sbSQL.toString());
			logger.info("updateProvisionX "+sbSQL);
			System.out.println(sbSQL.toString());
			resultado = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:ProvisionInteresesCDaoImpl, M:updateProvisionX");
		}
		return resultado;
	}

}
