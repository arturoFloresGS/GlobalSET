package com.webset.set.financiamiento.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.coinversion.dao.impl.CoinversionDaoImpl;
import com.webset.set.financiamiento.dao.AvalesGarantiasFCDao;
import com.webset.set.financiamiento.dto.AvalGarantiaDto;
import com.webset.set.global.dao.GlobalDao;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.ConstantesSet;

public class AvalesGarantiasFCDaoImpl implements AvalesGarantiasFCDao {
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
			+ "P:Financiamiento, C:AvalesGarantiasFCDaoImpl, M:llenarCmbEmpresa");
		}
		return list;
	}
	//Se agregó para que muestre tipos de persona E y F
	@Override
	public List<LlenaComboGralDto> llenarCmbEmpresaAvalista(int piNoUsuario, boolean pbMismaEmpresa, int plEmpresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n SELECT p.id_tipo_persona+'_'+convert(varchar,p.no_persona) as ID, e.nom_empresa as Descrip");
			sbSQL.append("\n FROM   empresa e, usuario_empresa ue , persona p");
			sbSQL.append("\n WHERE  e.no_empresa = ue.no_empresa ");
			sbSQL.append("\n 	and e.no_empresa=p.no_persona");
			sbSQL.append("\n 	and p.id_tipo_persona='E'");
			sbSQL.append("\n 	And ue.no_usuario = "+piNoUsuario);
			if (pbMismaEmpresa)
				sbSQL.append("\n   And ue.no_empresa <> " + plEmpresa + " ");
			sbSQL.append("\n UNION ALL");
			sbSQL.append("\n SELECT p.id_tipo_persona+'_'+convert(varchar,p.no_persona) as ID, p.nombre+' '+p.paterno+' '+p.materno as Descrip");
			sbSQL.append("\n FROM   persona p");
			sbSQL.append("\n WHERE ");
			sbSQL.append("\n 	p.id_tipo_persona='F'");
			sbSQL.append("\n ORDER BY 1");
			logger.info("llenarCmbEmpresaAvalista "+sbSQL);
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
			+ "P:Financiamiento, C:AvalesGarantiasFCDaoImpl, M:llenarCmbEmpresa");
		}
		return list;
	}
	@Override
	public List<LlenaComboGralDto> llenarCmbTipoGtia() {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n SELECT id_tipo_garantia as ID, descripcion as Descrip ");
			sbSQL.append("\n FROM   cat_tipo_garantia ");
			sbSQL.append("\n ORDER BY id_tipo_garantia ");
			logger.info("llenarCmbTipoGtia "+sbSQL);
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
			+ "P:Financiamiento, C:AvalesGarantiasFCDaoImpl, M:llenarCmbTipoGtia");
		}
		return list;
	}
	@Override
	public List<AvalGarantiaDto> buscarAvalGtia(String psTipo, int piEmpresa,String tipoPersona) {
		List<AvalGarantiaDto> list = new ArrayList<AvalGarantiaDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			if(piEmpresa!=0&&tipoPersona.equals("E")||piEmpresa==0){
				sbSQL.append("\n SELECT  ag.tipo_persona+'_'+convert(varchar,ag.no_empresa) as noEmpresa, e.nom_empresa as nomEmpresa, ag.aval_garantia as id_aval, cta.descripcion as aval_garantia, ");
				sbSQL.append("\n         ag.clave, ag.descripcion, coalesce(ag.valor_total, 0) as valor_total, coalesce(sum(agae.monto_avalado), 0) as monto_dispuesto, ");
				sbSQL.append("\n         	(coalesce(ag.valor_total, 0) - coalesce(sum(agae.monto_avalado), 0) + ");
				sbSQL.append("\n           	(select coalesce(sum(capital),0) ");
				sbSQL.append("\n            From cat_amortizacion_credito ");
				sbSQL.append("\n            where id_contrato in (select distinct id_financiamiento ");
				sbSQL.append("\n                                  from aval_garantia_asig_lin ");
				sbSQL.append("\n                                  where clave = ag.clave and estatus = 'A') ");
				sbSQL.append("\n           and capital <> 0 ");
				sbSQL.append("\n           and estatus = 'P')) as monto_disponible, ");
				sbSQL.append("\n         ag.id_divisa , ag.fec_inicial, ag.fec_final, ag.pje_garantia, ag.gtia_especial ");
				sbSQL.append("\n FROM    aval_garantia ag left join aval_garantia_asig_emp agae on (ag.no_empresa = agae.no_empresa_avalista ");
				sbSQL.append("\n         And ag.clave = agae.clave), empresa e, cat_tipo_garantia cta ");
				sbSQL.append("\n WHERE   ag.no_empresa = e.no_empresa ");
				sbSQL.append("\n       And ag.aval_garantia = cta.id_tipo_garantia ");
				sbSQL.append("\n       And e.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " +globalSingleton.getUsuarioLoginDto().getIdUsuario()  + ")");
				if (!psTipo.equals("0"))
					sbSQL.append("\n      And ag.aval_garantia = " + psTipo + " ");
				if (piEmpresa!= 0&&tipoPersona.equals("E")){
					sbSQL.append("\n       And ag.no_empresa = " + piEmpresa + " ");
				}
				sbSQL.append("\n   GROUP BY ag.no_empresa, e.nom_empresa, ag.aval_garantia, ag.clave, ag.descripcion, ag.valor_total, ");
				sbSQL.append("\n          ag.id_divisa , ag.fec_inicial, ag.fec_final, ag.pje_garantia, ag.gtia_especial, cta.descripcion ,ag.tipo_persona ");
			}

			//Personas Físicas

			if(piEmpresa==0)
				sbSQL.append("\n UNION ALL");


			if(piEmpresa!=0&&tipoPersona.equals("F")||piEmpresa==0){
				sbSQL.append("\n	SELECT  ag.tipo_persona+'_'+convert(varchar,ag.no_empresa) as noEmpresa, p.nombre+' '+p.paterno+' '+p.materno as nomEmpresa, ag.aval_garantia as id_aval, cta.descripcion as aval_garantia,"); 
				sbSQL.append("\n	ag.clave, ag.descripcion, coalesce(ag.valor_total, 0) as valor_total, coalesce(sum(agae.monto_avalado), 0) as monto_dispuesto, ");
				sbSQL.append("\n		(coalesce(ag.valor_total, 0) - coalesce(sum(agae.monto_avalado), 0) + ");
				sbSQL.append("\n		(select coalesce(sum(capital),0) ");
				sbSQL.append("\n		From cat_amortizacion_credito ");
				sbSQL.append("\n		where id_contrato in (select distinct id_financiamiento ");
				sbSQL.append("\n			from aval_garantia_asig_lin ");
				sbSQL.append("\n			where clave = ag.clave and estatus = 'A') ");
				sbSQL.append("\n				and capital <> 0 ");
				sbSQL.append("\n				and estatus = 'P')) as monto_disponible,"); 
				sbSQL.append("\n 		ag.id_divisa , ag.fec_inicial, ag.fec_final, ag.pje_garantia, ag.gtia_especial ");
				sbSQL.append("\n 	FROM aval_garantia ag left join aval_garantia_asig_emp agae on (ag.no_empresa = agae.no_empresa_avalista ");
				sbSQL.append("\n 		And ag.clave = agae.clave), cat_tipo_garantia cta,persona p");
				sbSQL.append("\n 		WHERE  ag.no_empresa = p.no_persona");
				sbSQL.append("\n 			And p.id_tipo_persona='F'");
				sbSQL.append("\n 			And ag.aval_garantia = cta.id_tipo_garantia");
				if (!psTipo.equals("0"))
					sbSQL.append("\n      And ag.aval_garantia = " + psTipo + " ");
				if (piEmpresa!= 0&&tipoPersona.equals("F")){
					sbSQL.append("\n       And ag.no_empresa = " + piEmpresa + " ");
				}
				sbSQL.append("\n 		GROUP BY ag.no_empresa, ag.aval_garantia, ag.clave, ag.descripcion, ag.valor_total,");
				sbSQL.append("\n 		ag.id_divisa , ag.fec_inicial, ag.fec_final, ag.pje_garantia, ag.gtia_especial, cta.descripcion ,ag.tipo_persona,");
				sbSQL.append("\n 		p.nombre, p.paterno, p.materno");
			}
			System.out.println(sbSQL.toString());
			logger.info("buscarAvalGtia "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public AvalGarantiaDto mapRow(ResultSet rs, int idx) throws SQLException {
					AvalGarantiaDto compon = new AvalGarantiaDto();
					compon.setNoEmpresaS(rs.getString("noEmpresa"));
					compon.setNomEmpresa(rs.getString("nomEmpresa"));
					compon.setClave(rs.getString("clave"));
					compon.setIdAvalGarantia(rs.getString("id_aval").charAt(0));
					compon.setAvalGarantia(rs.getString("aval_garantia"));
					compon.setDescripcion(rs.getString("descripcion"));
					compon.setPjeGarantia(rs.getDouble("pje_garantia"));
					String gtiaEspecial="";
					if(rs.getString("gtia_especial").equals("S"))
						gtiaEspecial="SI";
					else
						gtiaEspecial="NO";
					compon.setGarantiaEspecial(gtiaEspecial);
					compon.setValorTotal(rs.getDouble("valor_total"));
					compon.setMontoDispuesto(rs.getDouble("monto_dispuesto"));
					compon.setDisponible(rs.getDouble("monto_disponible"));	
					compon.setIdDivisa(rs.getString("id_divisa"));
					compon.setFecInicial(Funciones.cambiarFecha(rs.getString("fec_inicial"),true));
					compon.setFecFinal(Funciones.cambiarFecha(rs.getString("fec_final"),true));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AvalesGarantiasFCDaoImpl, M:buscarAvalGtia");
		}
		return list;
	}
	@Override
	public int updateAvalGarantia(int empresa, int lsTipo, String clave, String descripcion,
			double valor, String fecIni, String fecFin, double pje, String vsEspecial,char tipoPersona) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		StringBuffer sbSQL = new StringBuffer();
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n UPDATE aval_garantia ");
			sbSQL.append("\n SET descripcion = '" + descripcion + "', valor_total = " + valor + ", ");
			sbSQL.append("\n fec_inicial = '" + fecIni + "', fec_final = '" + fecFin + "', ");
			sbSQL.append("\n pje_garantia = " + pje + ", gtia_especial = '" + vsEspecial + "', ");
			sbSQL.append("\n usuario_modif = " +  globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", fec_modif = cast('" + fechaHoy + "' as date) ");
			sbSQL.append("\n WHERE no_empresa = " + empresa + " ");
			sbSQL.append("\n   And clave = '" + clave + "' ");
			sbSQL.append("\n   And tipo_persona = '" + tipoPersona + "' ");
			sbSQL.append("\n   And aval_garantia = '" + lsTipo + "' ");
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
	public int insertaAvalGtia(int empresa, int lsTipo, String clave, String descripcion, double valor,
			String idDivisa, String fecIni, String fecFin, double pje, String vsEspecial,char tipoPersona) {
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n INSERT INTO aval_garantia(no_empresa, aval_garantia, clave, descripcion, valor_total, ");
			sbSQL.append("\n                          id_divisa, fec_inicial, fec_final, pje_garantia, gtia_especial, estatus,tipo_persona) ");
			sbSQL.append("\n VALUES (" +empresa+ ", '" +lsTipo+ "', '" +clave+ "', '"+descripcion+ "', ");
			sbSQL.append("\n        "+valor+ ", '" +idDivisa+ "', '" +funciones.cambiarOrdenFecha(fecIni)+ "', '" +funciones.cambiarOrdenFecha(fecFin)+ "',");
			sbSQL.append("\n       "+pje+ ", '" +vsEspecial+ "', 'A','"+tipoPersona+"')");
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
	public List<AvalGarantiaDto> existeAvalGtia(int empresa,int psAvalGtia, String clave,char tipoPersona) {
		List<AvalGarantiaDto> list = new ArrayList<AvalGarantiaDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n SELECT * FROM aval_garantia ");
			sbSQL.append("\n WHERE no_empresa = " + empresa +" ");
			sbSQL.append("\n       And aval_garantia = '" + psAvalGtia + "' ");
			sbSQL.append("\n       And clave = '" + clave + "' ");
			sbSQL.append("\n       And tipo_persona = '" + tipoPersona + "' ");
			sbSQL.append("\n       And estatus = 'A' ");
			logger.info("existeAvalGtia "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public AvalGarantiaDto mapRow(ResultSet rs, int idx) throws SQLException {
					AvalGarantiaDto compon = new AvalGarantiaDto();
					compon.setClave(rs.getString("clave"));
					compon.setNoEmpresa(rs.getInt("no_empresa"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AvalesGarantiasFCDaoImpl, M:existeAvalGtia");
		}
		return list;
	}
	@Override
	public List<AvalGarantiaDto> selectAvaladas(int empresa, String clave,String tipoPersona) {
		List<AvalGarantiaDto> list = new ArrayList<AvalGarantiaDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n SELECT agae.clave, agae.no_empresa_avalada, e.nom_empresa, coalesce(agae.monto_avalado, 0) as monto_avalado, coalesce(sum(agal.monto_dispuesto), 0) as monto_dispuesto, ");
			sbSQL.append("\n       (coalesce(agae.monto_avalado, 0) - coalesce(sum(agal.monto_dispuesto), 0)) as monto_disponible ");
			sbSQL.append("\n FROM      aval_garantia_asig_emp agae left join aval_garantia_asig_lin agal on (agae.no_empresa_avalada = agal.no_empresa_avalada and agae.clave = agal.clave and agal.estatus = 'A'), empresa e ");
			sbSQL.append("\n WHERE     agae.no_empresa_avalada = e.no_empresa ");
			sbSQL.append("\n           And agae.no_empresa_avalista = " + empresa + " ");
			sbSQL.append("\n           And agae.clave = '" + clave + "' ");
			sbSQL.append("\n           And agae.estatus = 'A' ");
			if(tipoPersona.equals("E"))
				sbSQL.append("\n           And agae.tipo_persona='E'");
			if(tipoPersona.equals("F"))
				sbSQL.append("\n           And agae.tipo_persona='F'");
			sbSQL.append("\n GROUP BY  agae.no_empresa_avalada, e.nom_empresa, agae.monto_avalado, agae.clave ");
			logger.info("selectAvaladas "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public AvalGarantiaDto mapRow(ResultSet rs, int idx) throws SQLException {
					AvalGarantiaDto compon = new AvalGarantiaDto();
					compon.setNoEmpresa(rs.getInt("no_empresa_avalada"));
					compon.setNomEmpresa(rs.getString("nom_empresa"));
					compon.setMontoAvalado(rs.getDouble("monto_avalado"));
					compon.setMontoDispuesto(rs.getDouble("monto_dispuesto"));
					compon.setDisponible(rs.getDouble("monto_disponible"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AvalesGarantiasFCDaoImpl, M:selectAvaladas");
		}
		return list;
	}
	@Override
	public int insertaAsignacionEmp(int empresa, String clave, int empresaA, double montoAvalado,char tipoPersona) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		StringBuffer sbSQL = new StringBuffer();
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n INSERT INTO aval_garantia_asig_emp(no_empresa_avalista, clave, no_empresa_avalada, ");
			sbSQL.append("\n                                    monto_avalado, fec_alta, usuario_alta, estatus,tipo_persona)");
			sbSQL.append("\n VALUES ( " + empresa + ", '" + clave + "', " + empresaA + ", ");
			sbSQL.append("\n          " + montoAvalado + ",cast('"  +fechaHoy+  "' as date), " +globalSingleton.getUsuarioLoginDto().getIdUsuario()+", 'A','"+tipoPersona+"') ");
			System.out.println(sbSQL.toString());
			logger.info("updateDisposicion "+sbSQL);
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AvalesGarantiasFCDaoImpl, M:insertaAsignacionEmp");
			return 0;
		}
	}
	@Override
	public int existeAvalGtiaLinea(int empresa, String clave, int empresaA,char tipoPersona) {
		StringBuffer sbSQL = new StringBuffer();
		List<AvalGarantiaDto> list = new ArrayList<AvalGarantiaDto>();
		globalSingleton = GlobalSingleton.getInstancia();
		try {
			sbSQL.append("\n SELECT * FROM aval_garantia_asig_emp agae, aval_garantia_asig_lin agal ");
			sbSQL.append("\n WHERE agae.no_empresa_avalada = agal.no_empresa_avalada ");
			sbSQL.append("\n       And agae.no_empresa_avalista = " + empresa + " ");
			sbSQL.append("\n       And agae.clave = '" + clave + "' ");
			sbSQL.append("\n       And agae.no_empresa_avalada = "
					+ empresaA + " ");
			sbSQL.append("\n       And agae.tipo_persona = '"+tipoPersona+"'");
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
			+ "P:Financiamiento, C:AvalesGarantiasFCDaoImpl, M:existeAvalGtiaLinea");
			return -1;
		}
	}
	@Override
	public int deleteAvalada(int empresa, String clave, int empresaA, char tipoPersona) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		StringBuffer sbSQL = new StringBuffer();
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n  UPDATE aval_garantia_asig_emp SET estatus = 'X', usuario_modif = " +globalSingleton.getUsuarioLoginDto().getIdUsuario()+", fec_modif = cast('"  +fechaHoy+  "' as date) ");
			sbSQL.append("\n  WHERE no_empresa_avalista = " +empresa+" ");
			sbSQL.append("\n      And clave = '" +clave + "' ");
			sbSQL.append("\n      And tipo_persona = '" +tipoPersona + "' ");
			sbSQL.append("\n     And no_empresa_avalada = " +empresaA+ " ");
			logger.info("deleteAvalada "+sbSQL);
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AvalesGarantiasFCDaoImpl, M:deleteAvalada");
			return 0;
		}

	}

	@Override
	public List<AvalGarantiaDto> reporteAvalesGtiasAvalistas(int vsTipoGtia) {
		List<AvalGarantiaDto> registros= new ArrayList<AvalGarantiaDto>();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		StringBuffer sbSQL = new StringBuffer();
		int posicion=0;
		double tipoCambio=0;
		DecimalFormat formato = new DecimalFormat("###,###,###,##0.00");
		DecimalFormat formato2 = new DecimalFormat("###########0.00");
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			/*sbSQL.append("\n SELECT    0 as identifica, ag.id_divisa, coalesce(case when ag.id_divisa = 'MN' then 1 else vd.valor end, 0) as valor, ");
			sbSQL.append("\n            cd.desc_divisa, ag.tipo_persona+'_'+convert(varchar,ag.no_empresa) as noEmpresa, e.nom_empresa as nomEmpresa, ag.clave, ag.descripcion, coalesce(ag.valor_total, 0) as valor_total,  ");
			sbSQL.append("\n             coalesce(sum(agae.monto_avalado), 0) as dispuesto,  ");
			sbSQL.append("\n (coalesce(ag.valor_total, 0) - coalesce(sum(agae.monto_avalado), 0)) as disponible ");

			sbSQL.append("\n FROM      aval_garantia ag left join aval_garantia_asig_emp agae on (ag.no_empresa = agae.no_empresa_avalista And ag.clave = agae.clave) ");
			sbSQL.append("\n           left join cat_divisa cd on (ag.id_divisa = cd.id_divisa) ");
			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append("\n         left join valor_divisa vd on (vd.id_divisa = ag.id_divisa And cast(vd.fec_divisa as date) = '"  +fechaHoy+ "') ");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\n           left join valor_divisa vd on (vd.id_divisa = ag.id_divisa And cast(vd.fec_divisa as date) = '" +fechaHoy+ "') ");
			sbSQL.append("\n           , empresa e ");
			sbSQL.append("\n WHERE     ag.no_empresa = e.no_empresa ");
			sbSQL.append("\n          and e.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " +globalSingleton.getUsuarioLoginDto().getIdUsuario()+  ")");
			if(vsTipoGtia!=0)  
				sbSQL.append("\n       And ag.aval_garantia = "+vsTipoGtia+ " ");
			sbSQL.append("\n GROUP BY  ag.id_divisa, vd.valor, cd.desc_divisa, ag.no_empresa, e.nom_empresa, ag.clave, ag.descripcion, ag.valor_total ");
			sbSQL.append("\n Union All ");
			sbSQL.append("\n SELECT    1 as identifica, ag.id_divisa, coalesce(case when ag.id_divisa = 'MN' then 1 else vd.valor end, 0) as valor, cd.desc_divisa, agae.no_empresa_avalada, e1.nom_empresa as empresa_avalista, agae.clave, 'a' as x, coalesce(agae.monto_avalado, 0) as monto_avalado, ");
			sbSQL.append("\n           coalesce(sum(agal.monto_dispuesto), 0) as monto_dispuesto, ");
			sbSQL.append("\n           coalesce(agae.monto_avalado, 0) - coalesce(sum(agal.monto_dispuesto), 0) as monto_disponible ");
			sbSQL.append("\n FROM      aval_garantia_asig_emp agae left join aval_garantia_asig_lin agal on ");
			sbSQL.append("\n           (agae.no_empresa_avalada = agal.no_empresa_avalada And agae.clave = agal.clave And agae.no_empresa_avalista = agal.no_empresa_avalista) ");
			sbSQL.append("\n          left join aval_garantia ag on (agae.no_empresa_avalista = ag.no_empresa And agae.clave = ag.clave) ");
			sbSQL.append("\n          left join cat_divisa cd on (ag.id_divisa = cd.id_divisa) ");
			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append("\n          left join valor_divisa vd on (vd.id_divisa = ag.id_divisa And cast(vd.fec_divisa as date) = '" +fechaHoy+ "') ");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\n         left join valor_divisa vd on (vd.id_divisa = ag.id_divisa And cast(vd.fec_divisa as date) = '" +fechaHoy+ "') ");
			sbSQL.append("\n          , empresa e1 ");
			sbSQL.append("\n WHERE     agal.no_empresa_avalista = e1.no_empresa ");
			sbSQL.append("\n          and e1.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " +globalSingleton.getUsuarioLoginDto().getIdUsuario()+  ")");
			if(vsTipoGtia!=0)  
				sbSQL.append("\n       ag.aval_garantia = " +vsTipoGtia+ " ");
			sbSQL.append("\n GROUP BY  ag.id_divisa, vd.valor, cd.desc_divisa, agae.no_empresa_avalada, e1.nom_empresa, agae.clave, agae.monto_avalado ");
			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append("\n ORDER BY  ag.clave, identifica, ag.id_divisa, descripcion ");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\nORDER BY  clave, identifica, id_divisa, descripcion ");*/

			sbSQL.append("\n SELECT    0 as identifica, ag.id_divisa, coalesce(case when ag.id_divisa = 'MN' then 1 else vd.valor end, 0) as valor, ");
			sbSQL.append("\n 	cd.desc_divisa, ag.tipo_persona+'_'+convert(varchar,ag.no_empresa) as noEmpresa, e.nom_empresa as nomEmpresa, ag.clave, ag.descripcion, coalesce(ag.valor_total, 0) as valor_total, ");
			sbSQL.append("\n 	coalesce(sum(agae.monto_avalado), 0) as dispuesto, ");
			sbSQL.append("\n 	(coalesce(ag.valor_total, 0) - coalesce(sum(agae.monto_avalado), 0)) as disponible ");
			sbSQL.append("\n FROM      aval_garantia ag left join aval_garantia_asig_emp agae on (ag.no_empresa = agae.no_empresa_avalista And ag.clave = agae.clave)  ");
			sbSQL.append("\n 	left join cat_divisa cd on (ag.id_divisa = cd.id_divisa)  ");
			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append("\n 	left join valor_divisa vd on (vd.id_divisa = ag.id_divisa And cast(vd.fec_divisa as date) = '"  +fechaHoy+ "') ");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\n           left join valor_divisa vd on (vd.id_divisa = ag.id_divisa And cast(vd.fec_divisa as date) = '" +fechaHoy+ "') ");
			sbSQL.append("\n , empresa e  ");
			sbSQL.append("\n WHERE     ag.no_empresa = e.no_empresa  ");
			sbSQL.append("\n 		and ag.tipo_persona='E' ");
			if(vsTipoGtia!=0)  
				sbSQL.append("\n       And ag.aval_garantia = "+vsTipoGtia+ " ");
			sbSQL.append("\n GROUP BY  ag.id_divisa, vd.valor, cd.desc_divisa, ag.no_empresa, e.nom_empresa, ag.clave, ag.descripcion, ag.valor_total,ag.tipo_persona ");
			sbSQL.append("\n UNION ALL ");
			sbSQL.append("\n SELECT    1 as identifica, ag.id_divisa, coalesce(case when ag.id_divisa = 'MN' then 1 else vd.valor end, 0) as valor, ");
			sbSQL.append("\n 	cd.desc_divisa, convert(varchar,agae.no_empresa_avalada) as noEmpresa, e1.nom_empresa as nomEmpresa, agae.clave, 'a' as x,  ");
			sbSQL.append("\n 	coalesce(agae.monto_avalado, 0) as monto_avalado, ");
			sbSQL.append("\n 	coalesce(sum(agal.monto_dispuesto), 0) as monto_dispuesto, ");
			sbSQL.append("\n 	coalesce(agae.monto_avalado, 0) - coalesce(sum(agal.monto_dispuesto), 0) as monto_disponible ");
			sbSQL.append("\n FROM      aval_garantia_asig_emp agae left join aval_garantia_asig_lin agal on ");
			sbSQL.append("\n 	(agae.no_empresa_avalada = agal.no_empresa_avalada And agae.clave = agal.clave And agae.no_empresa_avalista = agal.no_empresa_avalista) ");
			sbSQL.append("\n 	left join aval_garantia ag on (agae.no_empresa_avalista = ag.no_empresa And agae.clave = ag.clave) ");
			sbSQL.append("\n 	left join cat_divisa cd on (ag.id_divisa = cd.id_divisa) ");
			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append("\n 	left join valor_divisa vd on (vd.id_divisa = ag.id_divisa And cast(vd.fec_divisa as date) ='"  +fechaHoy+ "') ");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\n           left join valor_divisa vd on (vd.id_divisa = ag.id_divisa And cast(vd.fec_divisa as date) = '" +fechaHoy+ "') ");
			sbSQL.append("\n 	, empresa e1 ");
			sbSQL.append("\n WHERE    agal.no_empresa_avalista = e1.no_empresa ");
			sbSQL.append("\n 		and e1.no_empresa in (select no_empresa from usuario_empresa where no_usuario =" +globalSingleton.getUsuarioLoginDto().getIdUsuario()+  ")");
			sbSQL.append("\n 		and ag.tipo_persona='E'");
			sbSQL.append("\n 		and ag.no_empresa=agae.no_empresa_avalista");
			if(vsTipoGtia!=0)  
				sbSQL.append("\n       And ag.aval_garantia = "+vsTipoGtia+ " ");
			sbSQL.append("\n GROUP BY  ag.id_divisa, vd.valor, cd.desc_divisa, agae.no_empresa_avalada, e1.nom_empresa, agae.clave, agae.monto_avalado, ag.clave , descripcion ");
			sbSQL.append("\n UNION ALL");
			sbSQL.append("\n SELECT    0 as identifica, ag.id_divisa, coalesce(case when ag.id_divisa = 'MN' then 1 else vd.valor end, 0) as valor, "); 
			sbSQL.append("\n 	cd.desc_divisa, ag.tipo_persona+'_'+convert(varchar,ag.no_empresa) as noEmpresa, p.nombre+' '+p.paterno+' '+p.materno as nomEmpresa, ag.clave, ag.descripcion, coalesce(ag.valor_total, 0) as valor_total, "); 
			sbSQL.append("\n 	coalesce(sum(agae.monto_avalado), 0) as dispuesto, "); 
			sbSQL.append("\n 	(coalesce(ag.valor_total, 0) - coalesce(sum(agae.monto_avalado), 0)) as disponible "); 
			sbSQL.append("\n FROM      aval_garantia ag left join aval_garantia_asig_emp agae on (ag.no_empresa = agae.no_empresa_avalista And ag.clave = agae.clave) "); 
			sbSQL.append("\n 	left join cat_divisa cd on (ag.id_divisa = cd.id_divisa) "); 
			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append("\n 	left join valor_divisa vd on (vd.id_divisa = ag.id_divisa And cast(vd.fec_divisa as date) ='"  +fechaHoy+ "') ");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\n           left join valor_divisa vd on (vd.id_divisa = ag.id_divisa And cast(vd.fec_divisa as date) = '" +fechaHoy+ "') ");
			sbSQL.append("\n 	, persona p"); 
			sbSQL.append("\n where "); 
			sbSQL.append("\n 		ag.no_empresa=p.no_persona"); 
			sbSQL.append("\n 		and ag.tipo_persona='F'"); 
			if(vsTipoGtia!=0)  
				sbSQL.append("\n       And ag.aval_garantia = "+vsTipoGtia+ " "); 
			sbSQL.append("\n GROUP BY  ag.id_divisa, vd.valor, cd.desc_divisa, ag.no_empresa, ag.clave, ag.descripcion, ag.valor_total, p.nombre,p.paterno,p.materno,ag.tipo_persona"); 
			sbSQL.append("\n UNION ALL"); 
			sbSQL.append("\n SELECT    1 as identifica, ag.id_divisa, coalesce(case when ag.id_divisa = 'MN' then 1 else vd.valor end, 0) as valor,"); 
			sbSQL.append("\n 	cd.desc_divisa, convert(varchar,agae.no_empresa_avalada) as noEmpresa, e1.nom_empresa as nomEmpresa, agae.clave, 'a' as x, "); 
			sbSQL.append("\n 	coalesce(agae.monto_avalado, 0) as monto_avalado, "); 
			sbSQL.append("\n 	coalesce(sum(agal.monto_dispuesto), 0) as monto_dispuesto, ");
			sbSQL.append("\n 	coalesce(agae.monto_avalado, 0) - coalesce(sum(agal.monto_dispuesto), 0) as monto_disponible "); 
			sbSQL.append("\n FROM      aval_garantia_asig_emp agae left join aval_garantia_asig_lin agal on "); 
			sbSQL.append("\n 	(agae.no_empresa_avalada = agal.no_empresa_avalada And agae.clave = agal.clave And agae.no_empresa_avalista = agal.no_empresa_avalista and agal.tipo_persona=agae.tipo_persona) "); 
			sbSQL.append("\n 	left join aval_garantia ag on (agae.no_empresa_avalista = ag.no_empresa And agae.clave = ag.clave and ag.tipo_persona=agae.tipo_persona) "); 
			sbSQL.append("\n 	left join cat_divisa cd on (ag.id_divisa = cd.id_divisa) "); 
			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append("\n 	left join valor_divisa vd on (vd.id_divisa = ag.id_divisa And cast(vd.fec_divisa as date) = '"  +fechaHoy+ "') ");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\n           left join valor_divisa vd on (vd.id_divisa = ag.id_divisa And cast(vd.fec_divisa as date) = '" +fechaHoy+ "') ");
			sbSQL.append("\n 	, empresa e1 , persona p"); 
			sbSQL.append("\n WHERE    agal.no_empresa_avalista = p.no_persona"); 
			sbSQL.append("\n 		and agal.no_empresa_avalada=e1.no_empresa"); 
			sbSQL.append("\n 		and e1.no_empresa in (select no_empresa from usuario_empresa where no_usuario = "+globalSingleton.getUsuarioLoginDto().getIdUsuario()+  ")");
			if(vsTipoGtia!=0)  
				sbSQL.append("\n       And ag.aval_garantia = "+vsTipoGtia+ " ");
			sbSQL.append("\n 		and ag.no_empresa=agae.no_empresa_avalista"); 
			sbSQL.append("\n 		and agal.tipo_persona='F'"); 
			sbSQL.append("\n GROUP BY  ag.id_divisa, vd.valor, cd.desc_divisa, agae.no_empresa_avalada, e1.nom_empresa, agae.clave, agae.monto_avalado, ag.clave , descripcion ,agae.tipo_persona,agae.no_empresa_avalista"); 
			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append("\n ORDER BY  ag.clave, identifica, ag.id_divisa, descripcion ");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\nORDER BY  clave, identifica, id_divisa, descripcion ");
			logger.info("reporteAvalesGtiasAvalistas "+sbSQL);
			//	System.out.println(sbSQL.toString());
			List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sbSQL.toString());
			String vsDivisa="",vlEmpresa="",vsLinea="",vsAval="";
			double vdTotalDivisa=0,totalAvalistaMA=0,totalAvalistaD=0,
					totalAvalistaDisp=0,totalDispuesto=0,totalDisponible=0,
					totalDivisaMA=0,totalDivisaD=0,totalDivisaDisp=0,gtpMA=0,gtpMD=0,gtpMDisp=0,gtpDR=0,gtpDispR=0,tipoCambioAux=0;
			boolean vdCambiaDiv=false, band=false;
			List<Integer> posiciones= new ArrayList<Integer>();
			//Cálculos del valor dispuesto real
			List<String> valores= new ArrayList<String>();
			List<Double> valoresAux= new ArrayList<Double>();
			//Cálculos del valor disponible real
			List<String> valores2= new ArrayList<String>();
			List<Double> valoresAux2= new ArrayList<Double>();
			//Array de divisas para los valores reales
			List<String> divisas= new ArrayList<String>();
			//Array de divisas para los valores reales
			List<Double> valorDivisas= new ArrayList<Double>();
			if(rows.size()>0){
				AvalGarantiaDto compon = new AvalGarantiaDto();
				vlEmpresa =rows.get(0).get("noEmpresa").toString(); 
				vsDivisa = rows.get(0).get("id_divisa").toString().trim();
				compon.setNoEmpresaS(rows.get(0).get("id_divisa").toString());
				compon.setNomEmpresa(rows.get(0).get("desc_divisa").toString());
				compon.setColor("color:#165ACB");
				registros.add(compon);
				divisas.add(vsDivisa);
				tipoCambioAux=Double.parseDouble(rows.get(0).get("valor").toString());
				valorDivisas.add(tipoCambioAux);
			}
			for (Map row : rows) {
				AvalGarantiaDto compon4 = new AvalGarantiaDto();
				compon4.setNoEmpresaS(row.get("noEmpresa").toString());
				compon4.setNomEmpresa((String)row.get("nomEmpresa"));
				compon4.setClave((String)row.get("clave"));
				if(row.get("identifica").toString().equals("0")){
					compon4.setDescripcion((String)row.get("descripcion"));
					compon4.setColor("color:#165ACB");
					posiciones.add(registros.size());
					if(posicion!=0){
						divisas.add(vsDivisa);
						tipoCambioAux=Double.parseDouble(row.get("valor").toString());
						valorDivisas.add(tipoCambioAux);
						valores.add(formato.format(totalDispuesto));
						valoresAux.add(totalDispuesto);
						valores2.add(formato.format(totalDisponible));
						valoresAux2.add(totalDisponible);
						totalDispuesto=0;
						totalDisponible=0;
					}
				}
				else{
					compon4.setDescripcion("");
					totalAvalistaMA+=Double.parseDouble(row.get("valor_total").toString());
					totalAvalistaD+=Double.parseDouble(row.get("dispuesto").toString());
					totalAvalistaDisp+=Double.parseDouble(row.get("disponible").toString());
					totalDispuesto+=Double.parseDouble(row.get("dispuesto").toString());
					totalDisponible+=Double.parseDouble(row.get("disponible").toString());
					totalDivisaMA+=Double.parseDouble(row.get("valor_total").toString());
					totalDivisaD+=Double.parseDouble(row.get("dispuesto").toString());
					totalDivisaDisp+=Double.parseDouble(row.get("disponible").toString());
				}
				compon4.setMontoAvaladoS(formato.format(Double.parseDouble(row.get("valor_total").toString())));
				compon4.setDispuesto(formato.format(Double.parseDouble(row.get("dispuesto").toString())));				
				compon4.setMontoDisponible(formato.format(Double.parseDouble(row.get("disponible").toString())));
				compon4.setIdentifica(row.get("identifica").toString());
				registros.add(compon4);
				if(rows.size()>(posicion+1)){
					if(!rows.get(posicion+1).get("id_divisa").toString().trim().equals(vsDivisa)&&rows.get(posicion+1).get("identifica").toString().trim().equals("0")){
						AvalGarantiaDto compon5 = new AvalGarantiaDto();
						if(vsDivisa.equals("MN")){
							gtpMA+=totalDivisaMA;
							gtpMD+=totalDivisaD;
							gtpMDisp+=totalDivisaDisp;
						}
						else{
							gtpMA+=(totalDivisaMA*tipoCambio);
							gtpMD+=(totalDivisaD*tipoCambio);
							gtpMDisp+=(totalDivisaDisp*tipoCambio);
						}
						vsDivisa=rows.get(posicion+1).get("id_divisa").toString().trim();
						compon5.setColor("color:#165ACB");
						compon5.setMontoAvaladoS(formato.format(totalAvalistaMA));
						compon5.setDispuesto(formato.format(totalAvalistaD));
						compon5.setMontoDisponible(formato.format(totalAvalistaDisp));
						compon5.setDescripcion("TOTAL AVALISTA");
						compon5.setIdentifica("");
						band=true;
						totalAvalistaMA=0;
						totalAvalistaD=0;
						totalAvalistaDisp=0;
						registros.add(compon5);
						vlEmpresa=rows.get(posicion+1).get("noEmpresa").toString().trim();
						compon5 = new AvalGarantiaDto();
						compon5.setColor("color:#165ACB");
						compon5.setIdentifica("");
						compon5.setDescripcion("TOTAL DIVISA");
						compon5.setMontoAvaladoS(formato.format(totalDivisaMA));
						compon5.setDispuesto(formato.format(totalDivisaD));
						compon5.setMontoDisponible(formato.format(totalDivisaDisp));
						totalDivisaMA=0;
						totalDivisaD=0;
						totalDivisaDisp=0;
						registros.add(compon5);
						vdCambiaDiv = true;
						compon5 = new AvalGarantiaDto();
						registros.add(compon5);
						compon5 = new AvalGarantiaDto();
						compon5.setNoEmpresaS(rows.get(posicion+1).get("id_divisa").toString());
						compon5.setNomEmpresa(rows.get(posicion+1).get("desc_divisa").toString());
						compon5.setIdentifica("");
						tipoCambio=Double.parseDouble(rows.get(posicion+1).get("valor").toString());
						if(vsDivisa.equals("DLS"))
							compon5.setDescripcion(rows.get(posicion+1).get("valor").toString());
						compon5.setColor("color:#165ACB");
						vsDivisa = rows.get(posicion+1).get("id_divisa").toString().trim();
						registros.add(compon5);
					}
					if(!rows.get(posicion+1).get("noEmpresa").toString().trim().equals(vlEmpresa)&&rows.get(posicion+1).get("identifica").toString().trim().equals("0")){	
						vsDivisa=rows.get(posicion+1).get("id_divisa").toString().trim();
						AvalGarantiaDto compon5 = new AvalGarantiaDto();
						compon5 = new AvalGarantiaDto();
						compon5.setColor("color:#165ACB");
						compon5.setMontoAvaladoS(formato.format(totalAvalistaMA));
						compon5.setDispuesto(formato.format(totalAvalistaD));
						compon5.setMontoDisponible(formato.format(totalAvalistaDisp));
						compon5.setIdentifica("");
						compon5.setDescripcion("TOTAL AVALISTA");
						band=true;
						totalAvalistaMA=0;
						totalAvalistaD=0;
						totalAvalistaDisp=0;
						registros.add(compon5);
					}
				}
				else{
					AvalGarantiaDto compon6 = new AvalGarantiaDto();
					compon6.setColor("color:#165ACB");
					compon6.setMontoAvaladoS(formato.format(totalAvalistaMA));
					compon6.setDispuesto(formato.format(totalAvalistaD));
					compon6.setMontoDisponible(formato.format(totalAvalistaDisp));
					compon6.setDescripcion("TOTAL AVALISTA");
					compon6.setIdentifica("");
					band=true;
					totalAvalistaMA=0;
					totalAvalistaD=0;
					totalAvalistaDisp=0;
					registros.add(compon6);
					compon6 = new AvalGarantiaDto();
					vsDivisa=rows.get(posicion).get("id_divisa").toString().trim();
					compon6.setColor("color:#165ACB");
					compon6.setIdentifica("");
					compon6.setMontoAvaladoS(formato.format(totalDivisaMA));
					compon6.setDispuesto(formato.format(totalDivisaD));
					compon6.setMontoDisponible(formato.format(totalDivisaDisp));
					if(vsDivisa.equals("MN")){
						gtpMA+=totalDivisaMA;
						gtpMD+=totalDivisaD;
						gtpMDisp+=totalDivisaDisp;
					}else{
						gtpMA+=(totalDivisaMA*tipoCambio);
						gtpMD+=(totalDivisaD*tipoCambio);
						gtpMDisp+=(totalDivisaDisp*tipoCambio);
					}
					totalDivisaMA=0;
					totalDivisaD=0;
					totalDivisaDisp=0;
					compon6.setDescripcion("TOTAL DIVISA");
					registros.add(compon6);
				}
				posicion=posicion+1;
			}	
			//Cálculo de los totales del dispuesto real y disponible real de la última empresa 
			int ultimo=posiciones.get(posiciones.size()-1);
			double ultimoDispReal=0,ultimoDispoReal=0;
			while(ultimo<registros.size()){
				if(registros.get(ultimo).getIdentifica().trim().equals("1")){
					ultimoDispReal+=Double.parseDouble(formato.parse(registros.get(ultimo).getDispuesto()).toString());
					ultimoDispoReal+=Double.parseDouble(formato.parse(registros.get(ultimo).getMontoDisponible()).toString());
				}
				ultimo++;
			}
			valorDivisas.add(tipoCambio);
			registros.get(posiciones.get(posiciones.size()-1)).setDispuestoReal(formato.format(ultimoDispReal));
			registros.get(posiciones.get(posiciones.size()-1)).setDisponibleReal(formato.format(ultimoDispoReal));
			//Se actualizan los valores del las columnas del dispuesto real y disponible real
			for(int i=0;i<posiciones.size()-1;i++){
				registros.get(posiciones.get(i)).setDispuestoReal(valores.get(i));
				registros.get(posiciones.get(i)).setDisponibleReal(valores2.get(i));

				if(divisas.get(i).equals("MN")){
					gtpDR+=valoresAux.get(i);
					gtpDispR+=gtpDispR+valoresAux2.get(i);
				}
				else{
					gtpDR+=(valoresAux.get(i)* valorDivisas.get(i));
					gtpDispR+=(valoresAux2.get(i)* valorDivisas.get(i));
				}
			}
			if(divisas.get(divisas.size()-1).equals("MN")){
				gtpDR+=ultimoDispReal;
				gtpDispR+=ultimoDispoReal;
			}
			else{
				gtpDR+=(ultimoDispReal*valorDivisas.get(valorDivisas.size()-1));
				gtpDispR+=(ultimoDispoReal*valorDivisas.get(valorDivisas.size()-1));
			}
			//Gran total pesos
			AvalGarantiaDto compon7 = new AvalGarantiaDto();
			compon7.setColor("color:#165ACB");
			compon7.setIdentifica("");
			compon7.setMontoAvaladoS(formato.format(gtpMA));
			compon7.setDispuesto(formato.format(gtpMD));
			compon7.setMontoDisponible(formato.format(gtpMDisp));
			compon7.setDispuestoReal(formato.format(gtpDR));
			compon7.setDisponibleReal(formato.format(gtpDispR));
			compon7.setDescripcion("GRAN TOTAL PESOS");
			registros.add(compon7);
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AvalesGarantiasFCDaoImpl, M:reporteAvalesGtiasAvalistas");
		}
		return registros;
	}

	@Override
	public List<AvalGarantiaDto> reporteAvalesGtiasAvaladas(int vsTipoGtia) {
		List<AvalGarantiaDto> registros= new ArrayList<AvalGarantiaDto>();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		StringBuffer sbSQL = new StringBuffer();
		int posicion=0;
		double tipoCambio=0;
		DecimalFormat formato = new DecimalFormat("###,###,###,##0.00");
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			/*	sbSQL.append("\n SELECT    agae.no_empresa_avalada, e.nom_empresa as empresa_avalada, agae.no_empresa_avalista, e1.nom_empresa as empresa_avalista, ag.clave, ag.descripcion, ");
			sbSQL.append("\n           ag.pje_garantia, coalesce(agal.monto_dispuesto, 0) as asignado, ");
			sbSQL.append("\n           coalesce((select cb.desc_banco from cat_contrato_credito ccc, cat_banco cb ");
			sbSQL.append("\n           where ccc.id_banco_prestamo = cb.id_banco And ccc.id_financiamiento = agal.id_financiamiento), '') as desc_banco, coalesce(agal.id_financiamiento, '') as id_financiamiento, ");
			sbSQL.append("\n           coalesce(agal.id_disposicion, 0) as id_disposicion, coalesce(agae.monto_avalado, 0) as monto_avalado, ");
			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append("\n coalesce((select valor from valor_divisa where ag.id_divisa = id_divisa And cast(fec_divisa as date) = '" + fechaHoy + "'), 0) as valor, ");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\n  coalesce((select valor from valor_divisa where ag.id_divisa = id_divisa And cast(fec_divisa as date) = '" +fechaHoy+ "'), 0) as valor, ");
			sbSQL.append("\n           case when ag.id_divisa = 'MN' then 0 else 1 end as identifica, ag.id_divisa, cd.desc_divisa ");
			sbSQL.append("\n FROM  aval_garantia_asig_emp agae left join aval_garantia_asig_lin agal on (agae.no_empresa_avalada = agal.no_empresa_avalada ");
			sbSQL.append("\n       And agae.no_empresa_avalista = agal.no_empresa_avalista ");
			sbSQL.append("\n       And agae.clave = agal.clave), empresa e1, ");
			sbSQL.append("\n       aval_garantia ag, empresa e, cat_divisa cd ");
			sbSQL.append("\n Where agae.clave = ag.clave");
			sbSQL.append("\n       And agae.no_empresa_avalada = e.no_empresa");
			sbSQL.append("\n       And agae.no_empresa_avalista = e1.no_empresa");
			sbSQL.append("\n       And ag.id_divisa = cd.id_divisa");
			sbSQL.append("\n       and e.no_empresa in (select no_empresa from usuario_empresa where no_usuario = "+globalSingleton.getUsuarioLoginDto().getIdUsuario()+ ")");
			if(vsTipoGtia!=0)  
				sbSQL.append("\n   And ag.aval_garantia = " + vsTipoGtia + " ");
			sbSQL.append("\n  ORDER BY  identifica, agae.no_empresa_avalada, ag.clave, agal.id_financiamiento, agal.id_disposicion ");
			 */
			sbSQL.append("\n SELECT    agae.no_empresa_avalada, e.nom_empresa as empresa_avalada, agae.tipo_persona+'_'+CONVERT(varchar,agae.no_empresa_avalista) as noEmpresa, e1.nom_empresa as nomEmpresa, ag.clave, ag.descripcion,");
			sbSQL.append("\n           ag.pje_garantia, coalesce(agal.monto_dispuesto, 0) as asignado, ");
			sbSQL.append("\n           coalesce((select cb.desc_banco from cat_contrato_credito ccc, cat_banco cb ");
			sbSQL.append("\n           where ccc.id_banco_prestamo = cb.id_banco And ccc.id_financiamiento = agal.id_financiamiento), '') as desc_banco, coalesce(agal.id_financiamiento, '') as id_financiamiento, ");
			sbSQL.append("\n           coalesce(agal.id_disposicion, 0) as id_disposicion, coalesce(agae.monto_avalado, 0) as monto_avalado, ");
			if(gsDBM.equals("SQL SERVER"))		
				sbSQL.append("\n coalesce((select valor from valor_divisa where ag.id_divisa = id_divisa And cast(fec_divisa as date) =  '" + fechaHoy + "'), 0) as valor, ");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\n  coalesce((select valor from valor_divisa where ag.id_divisa = id_divisa And cast(fec_divisa as date) = '" +fechaHoy+ "'), 0) as valor, ");
			sbSQL.append("\n            case when ag.id_divisa = 'MN' then 0 else 1 end as identifica, ag.id_divisa, cd.desc_divisa ");
			sbSQL.append("\n  FROM  aval_garantia_asig_emp agae left join aval_garantia_asig_lin agal on (agae.no_empresa_avalada = agal.no_empresa_avalada ");
			sbSQL.append("\n      And agae.no_empresa_avalista = agal.no_empresa_avalista ");
			sbSQL.append("\n      And agae.clave = agal.clave), empresa e1, ");
			sbSQL.append("\n      aval_garantia ag, empresa e, cat_divisa cd ");
			sbSQL.append("\n Where agae.clave = ag.clave");
			sbSQL.append("\n      And agae.no_empresa_avalada = e.no_empresa");
			sbSQL.append("\n      And agae.no_empresa_avalista = e1.no_empresa");
			sbSQL.append("\n      And ag.id_divisa = cd.id_divisa");
			sbSQL.append("\n      and e.no_empresa in (select no_empresa from usuario_empresa where no_usuario =  "+globalSingleton.getUsuarioLoginDto().getIdUsuario()+ ")");
			if(vsTipoGtia!=0)  
				sbSQL.append("\n   And ag.aval_garantia = " + vsTipoGtia + " ");
			sbSQL.append("\n   UNION ALL");
			sbSQL.append("\nSELECT    agae.no_empresa_avalada, e.nom_empresa as empresa_avalada, agae.tipo_persona+'_'+CONVERT(varchar,agae.no_empresa_avalista) as noEmpresa, p.nombre+' '+p.paterno+' '+p.materno as nomEmpresa, ag.clave, ag.descripcion, ");
			sbSQL.append("\n           ag.pje_garantia, coalesce(agal.monto_dispuesto, 0) as asignado, ");
			sbSQL.append("\n           coalesce((select cb.desc_banco from cat_contrato_credito ccc, cat_banco cb ");
			sbSQL.append("\n           where ccc.id_banco_prestamo = cb.id_banco And ccc.id_financiamiento = agal.id_financiamiento), '') as desc_banco, coalesce(agal.id_financiamiento, '') as id_financiamiento, ");
			sbSQL.append("\n           coalesce(agal.id_disposicion, 0) as id_disposicion, coalesce(agae.monto_avalado, 0) as monto_avalado, ");
			if(gsDBM.equals("SQL SERVER"))		
				sbSQL.append("\n coalesce((select valor from valor_divisa where ag.id_divisa = id_divisa And cast(fec_divisa as date) = '" + fechaHoy + "'), 0) as valor, ");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\n  coalesce((select valor from valor_divisa where ag.id_divisa = id_divisa And cast(fec_divisa as date) = '" +fechaHoy+ "'), 0) as valor, ");
			sbSQL.append("\n           case when ag.id_divisa = 'MN' then 0 else 1 end as identifica, ag.id_divisa, cd.desc_divisa ");
			sbSQL.append("\n FROM  aval_garantia_asig_emp agae left join aval_garantia_asig_lin agal on (agae.no_empresa_avalada = agal.no_empresa_avalada");
			sbSQL.append("\n       And agae.no_empresa_avalista = agal.no_empresa_avalista ");
			sbSQL.append("\n       And agae.clave = agal.clave), persona p, ");
			sbSQL.append("\n       aval_garantia ag, empresa e, cat_divisa cd ");
			sbSQL.append("\nWhere agae.clave = ag.clave");
			sbSQL.append("\n      And agae.no_empresa_avalada = e.no_empresa");
			sbSQL.append("\n       And agae.no_empresa_avalista = p.no_persona");
			sbSQL.append("\n       And ag.id_divisa = cd.id_divisa");
			sbSQL.append("\n	   and agae.tipo_persona='F'");
			sbSQL.append("\n      and e.no_empresa in (select no_empresa from usuario_empresa where no_usuario = "+globalSingleton.getUsuarioLoginDto().getIdUsuario()+ ")");
			if(vsTipoGtia!=0)  
				sbSQL.append("\n   And ag.aval_garantia = " + vsTipoGtia + " ");
			sbSQL.append("\n ORDER BY  identifica, no_empresa_avalada, clave, id_financiamiento, id_disposicion"); 
			System.out.println(sbSQL.toString());
			logger.info("reporteAvalesGtiasAvaladas "+sbSQL);
			List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sbSQL.toString());
			String vsDivisa="",vlEmpresa="",vsLinea="",vsAval="",noEmpresa="";
			double vdTotalDivisa=0,totalDispuesto=0,gtpDispuesto=0,totalDivisaD=0,montoAsigAux=0,gtpMA=0,gtpDisponible=0,totalDivisaDisp=0,totalDivisaMA=0,tipoCambioAux=0;
			boolean vdCambiaDiv=false;
			List<String> divisas= new ArrayList<String>();
			List<Integer> posiciones= new ArrayList<Integer>();
			List<Integer> posicionesTotDiv= new ArrayList<Integer>();
			List<Double> valores= new ArrayList<Double>();
			List<Double> totalDivisaAux=new ArrayList<Double>();
			List<Double> totalDivisaDispAux=new ArrayList<Double>();
			List<Double> totalDivisaMAaux=new ArrayList<Double>();
			List<Double> valoresDivisa=new ArrayList<Double>();
			if(rows.size()>0){
				AvalGarantiaDto compon = new AvalGarantiaDto();
				compon.setNoEmpresaS(rows.get(0).get("id_divisa").toString().trim());
				compon.setNomEmpresa(rows.get(0).get("desc_divisa").toString());
				compon.setColor("color:#165ACB");
				compon.setIdFinanciamiento("");
				compon.setClave("");
				compon.setMontoAsignado("");
				vsDivisa = rows.get(0).get("id_divisa").toString().trim();
				divisas.add(vsDivisa);
				tipoCambioAux=Double.parseDouble(rows.get(0).get("valor").toString().trim());
				valoresDivisa.add(tipoCambioAux);
				registros.add(compon);
			}
			for (Map row : rows) {
				if(!row.get("id_divisa").toString().trim().equals(vsDivisa)){
					AvalGarantiaDto compon = new AvalGarantiaDto();
					compon.setNoEmpresaS(rows.get(0).get("id_divisa").toString().trim());
					compon.setNomEmpresa(rows.get(0).get("desc_divisa").toString());
					compon.setColor("color:#165ACB");
					compon.setIdFinanciamiento("");
					compon.setClave("");
					compon.setMontoAsignado("");
					vsDivisa = rows.get(0).get("id_divisa").toString().trim();
					registros.add(compon);
				}
				if((!vlEmpresa.equals(row.get("no_empresa_avalada").toString()))||vdCambiaDiv){
					if(registros.size()>2&&!vdCambiaDiv){
						AvalGarantiaDto compon2 = new AvalGarantiaDto();
						compon2.setNoEmpresaS("");
						compon2.setMontoAsignado("");
						compon2.setIdFinanciamiento("");
						compon2.setClave("");
						registros.add(compon2);
					}
					AvalGarantiaDto compon3 = new AvalGarantiaDto();
					compon3.setNoEmpresaS(row.get("no_empresa_avalada").toString());
					compon3.setNomEmpresa((String)row.get("empresa_avalada"));
					compon3.setIdFinanciamiento("");
					compon3.setClave("");
					compon3.setColor("color:#165ACB");
					compon3.setMontoAsignado("");
					posiciones.add(registros.size());
					registros.add(compon3);
					vlEmpresa = row.get("no_empresa_avalada").toString();
					vdCambiaDiv = false;
					if(posicion!=0){
						valores.add(totalDispuesto);
						divisas.add(vsDivisa);
						tipoCambioAux=Double.parseDouble(row.get("valor").toString().trim());
						valoresDivisa.add(tipoCambioAux);
						totalDispuesto=0;
					}
				} 
				vdTotalDivisa=0;
				AvalGarantiaDto compon4 = new AvalGarantiaDto();
				compon4.setNoEmpresaS(row.get("noEmpresa").toString());
				compon4.setNomEmpresa((String)row.get("nomEmpresa"));
				compon4.setClave((String)row.get("clave"));
				compon4.setDescripcion((String)row.get("descripcion"));
				compon4.setGarantiaAsignada(formato.format(Double.parseDouble(row.get("pje_garantia").toString())));
				compon4.setMontoAsignado(formato.format(Double.parseDouble(row.get("monto_avalado").toString())));
				compon4.setBanco("");
				if(row.get("desc_banco").toString().equals(""))
					compon4.setDescBanco("");
				else
					compon4.setDescBanco(row.get("desc_banco").toString());
				if(row.get("id_financiamiento").toString().equals(""))
					compon4.setIdFinanciamiento("");
				else
					compon4.setIdFinanciamiento(row.get("id_financiamiento").toString());
				compon4.setCredito(row.get("id_disposicion").toString());
				if(Double.parseDouble(row.get("asignado").toString())!=0)
					compon4.setMontoAvaladoS(formato.format(Double.parseDouble(row.get("asignado").toString())));
				else
					compon4.setMontoAvaladoS("0.00");
				compon4.setIdentifica(row.get("identifica").toString());
				totalDispuesto+=Double.parseDouble(row.get("asignado").toString());
				totalDivisaD+=Double.parseDouble(row.get("asignado").toString());;
				//	compon4.setDispuesto("");
				//	compon4.setMontoDisponible("");
				registros.add(compon4);
				if(rows.size()>(posicion+1)){
					if(!rows.get(posicion+1).get("id_divisa").toString().trim().equals(vsDivisa)){
						AvalGarantiaDto compon5 = new AvalGarantiaDto();
						vsDivisa=rows.get(posicion+1).get("id_divisa").toString().trim();
						compon5.setColor("color:#165ACB");
						compon5.setNoEmpresaS("");
						compon5.setNomEmpresa("");
						compon5.setMontoAsignado("");
						compon5.setDispuesto(formato.format(totalDivisaD));
						totalDivisaD=0;
						compon5.setIdFinanciamiento("");
						compon5.setClave("");
						compon5.setDescripcion("TOTAL DIVISA");
						posicionesTotDiv.add(registros.size());
						registros.add(compon5);
						vdCambiaDiv = true;
						AvalGarantiaDto compon1 = new AvalGarantiaDto();
						compon1.setIdFinanciamiento("");
						compon1.setClave("");
						compon1.setNoEmpresaS("");
						compon1.setMontoAsignado("");
						registros.add(compon1);
						AvalGarantiaDto compon = new AvalGarantiaDto();
						compon.setIdFinanciamiento("");
						compon.setClave("");
						compon.setMontoAsignado("");
						compon.setNoEmpresaS(rows.get(posicion+1).get("id_divisa").toString().trim());
						compon.setNomEmpresa(rows.get(posicion+1).get("desc_divisa").toString());
						tipoCambio=Double.parseDouble(rows.get(posicion+1).get("valor").toString());
						if(vsDivisa.equals("DLS"))
							compon.setDescripcion(rows.get(posicion+1).get("valor").toString());
						compon.setColor("color:#165ACB");
						vsDivisa = rows.get(posicion+1).get("id_divisa").toString().trim();
						registros.add(compon);
					}
				}
				else{
					AvalGarantiaDto compon7 = new AvalGarantiaDto();
					vsDivisa=rows.get(posicion).get("id_divisa").toString().trim();
					compon7.setColor("color:#165ACB");
					compon7.setIdFinanciamiento("");
					compon7.setClave("");
					compon7.setDescripcion("TOTAL DIVISA");
					posicionesTotDiv.add(registros.size());
					compon7.setNoEmpresaS("");
					compon7.setNomEmpresa("");
					compon7.setMontoAsignado("");
					compon7.setDispuesto(formato.format(totalDivisaD));
					totalDivisaD=0;
					registros.add(compon7);
				}
				posicion=posicion+1;
			}	
			for(int i=0;i<posiciones.size()-1;i++){
				registros.get(posiciones.get(i)).setDispuesto(formato.format(valores.get(i)));
				if(divisas.get(i).equals("MN")){
					gtpDispuesto+=valores.get(i);
				}
				else{
					gtpDispuesto+=(valores.get(i)*valoresDivisa.get(i));
				}
			}
			int ultimo=posiciones.get(posiciones.size()-1)+1;
			double ultimoDispuesto=0;
			while(ultimo<registros.size()){
				if(!registros.get(ultimo).getNomEmpresa().equals("")){
					ultimoDispuesto+=Double.parseDouble(formato.parse(registros.get(ultimo).getMontoAvaladoS()).toString());
				}
				ultimo++;
			}
			//Gran total pesos (Dispuesto)
			if(divisas.get(divisas.size()-1).equals("MN")){
				gtpDispuesto+=ultimoDispuesto;
			}
			else{
				gtpDispuesto+=(ultimoDispuesto*valoresDivisa.get(valoresDivisa.size()-1));
			}
			valores.add(ultimoDispuesto);
			registros.get(posiciones.get(posiciones.size()-1)).setDispuesto(formato.format(ultimoDispuesto));
			//Monto asignado/avalado de cada empresa avalista-1
			int x=0;
			for(int i=0;i<registros.size();i++){
				if ((!vsLinea.equals(registros.get(i).getIdFinanciamiento().trim())&&!registros.get(i).getIdFinanciamiento().trim().equals(""))||!registros.get(i).getIdFinanciamiento().trim().equals("") ){
					if(i==0){
						vsAval="";
						noEmpresa="";}
					else{
						vsAval=registros.get(i-1).getClave().trim();
						noEmpresa=registros.get(i-1).getNoEmpresaS().trim();
					}
					if(!registros.get(i).getClave().trim().equals(vsAval)) {
						if(registros.get(i).getMontoAsignado().equals(""))
							montoAsigAux=0;
						else
							montoAsigAux=Double.parseDouble(formato.parse(registros.get(i).getMontoAsignado()).toString());
						vdTotalDivisa = vdTotalDivisa +montoAsigAux ;
					}
					vsLinea = registros.get(i).getIdFinanciamiento().trim();
				}
				if(x<posiciones.size()){
					if(posiciones.get(x)==i){
						if(x>0){
							registros.get(posiciones.get(x-1)).setMontoAvaladoS(formato.format(vdTotalDivisa));
							totalDivisaAux.add(vdTotalDivisa);
						}
						vdTotalDivisa=0;
						x++;
					}
				}
			}
			//Monto asignado/avalado de cada empresa (última)
			totalDivisaAux.add(vdTotalDivisa);
			registros.get(posiciones.get(posiciones.size()-1)).setMontoAvaladoS(formato.format(vdTotalDivisa));
			//cálculo de gtp y por cada divisa de la columna monto asignado/avalado
			String  divisaPosterior;
			double suma=0;
			for (int i = 0; i < totalDivisaAux.size(); i++) {
				//Gran Total Pesos (Dispuesto)
				if(divisas.get(i).trim().equals("MN")){
					gtpMA+=totalDivisaAux.get(i);
				}else{
					gtpMA+=(totalDivisaAux.get(i)*tipoCambio);
				}
				//Total divisa(Dispuesto)
				divisaPosterior="";
				if(i!=totalDivisaAux.size()-1)
					divisaPosterior=divisas.get(i+1).trim();
				if(divisas.get(i).trim().equals(divisaPosterior)){
					totalDivisaMA+=totalDivisaAux.get(i);
				}else{
					totalDivisaMA+=totalDivisaAux.get(i);
					totalDivisaMAaux.add(totalDivisaMA);
					totalDivisaMA=0;
				}
			}
			//Columna Disponible
			double disponible=0;
			String anterior="";
			for (int i = 0; i < valores.size(); i++) {
				disponible=totalDivisaAux.get(i)-valores.get(i);
				registros.get(posiciones.get(i)).setMontoDisponible(formato.format(disponible));
				//Gran Total pesos (disponible)
				if(divisas.get(i).trim().equals("MN"))
					gtpDisponible+=disponible;
				else
					gtpDisponible+=(disponible*valoresDivisa.get(i));
				//Calculo de totales por divisa(columna disponible)
				totalDivisaDisp=disponible;
				if(i>0)
					anterior=divisas.get(i-1).trim();
				if(divisas.get(i).trim().equals(anterior)){
					totalDivisaDisp+=disponible;
				}
				else{
					totalDivisaDispAux.add(totalDivisaDisp);
					totalDivisaDisp=0;
				}
				disponible=0;
			}
			//Actualización de totales por divisa(columnas: Monto Asignado/Avalado y disponible)
			for (int i = 0; i < totalDivisaDispAux.size(); i++) {
				registros.get(posicionesTotDiv.get(i)).setMontoAvaladoS(formato.format(totalDivisaMAaux.get(i)));
				registros.get(posicionesTotDiv.get(i)).setMontoDisponible(formato.format(totalDivisaDispAux.get(i)));
			}//Gran total pesos (fila)
			AvalGarantiaDto compon7 = new AvalGarantiaDto();
			compon7.setMontoDisponible(formato.format(gtpDisponible));
			compon7.setDispuesto(formato.format(gtpDispuesto));
			compon7.setMontoAvaladoS(formato.format(gtpMA));
			compon7.setColor("color:#165ACB");
			compon7.setIdFinanciamiento("");
			compon7.setClave("");
			compon7.setDescripcion("GRAN TOTAL PESOS");
			compon7.setNoEmpresaS("");
			compon7.setNomEmpresa("");
			registros.add(compon7);
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AvalesGarantiasFCDaoImpl, M:selectAvaladas");
		}
		return registros;
	}
}
