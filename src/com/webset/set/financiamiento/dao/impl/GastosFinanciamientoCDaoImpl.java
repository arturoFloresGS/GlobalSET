package com.webset.set.financiamiento.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.coinversion.dao.impl.CoinversionDaoImpl;
import com.webset.set.financiamiento.dao.GastosFinanciamientoCDao;
import com.webset.set.financiamiento.dto.AmortizacionCreditoDto;
import com.webset.set.financiamiento.dto.GastoComisionCreditoDto;
import com.webset.set.global.dao.GlobalDao;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.ConstantesSet;

public class GastosFinanciamientoCDaoImpl implements GastosFinanciamientoCDao {
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	GlobalDao globalDao;
	ConsultasGenerales consultasGenerales;
	Funciones funciones = new Funciones();
	GlobalSingleton globalSingleton;
	String gsDBM = ConstantesSet.gsDBM;
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
	public List<LlenaComboGralDto> obtenerContratos(String psTipoMenu,int iBanco,int piEmpresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		StringBuffer sbSQL = new StringBuffer();
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n SELECT  ccc.id_financiamiento AS ID, ccc.id_financiamiento as Descrip");
			sbSQL.append("\n  FROM  cat_contrato_credito ccc, usuario_empresa ue");
			sbSQL.append("\n WHERE ccc.estatus = 'A'");
			if (piEmpresa != 0) {
				sbSQL.append("\nAnd ccc.no_empresa =" + piEmpresa);
			}
			sbSQL.append("\n  and ccc.id_tipo_financiamiento in(select id_tipo_financiamiento from cat_tipo_contrato where financiamiento='"+psTipoMenu+"')");
			if(gsDBM.equals("SQL SERVER"))
			sbSQL.append("\nAnd (cast(fec_vencimiento as date) > '"+ funciones.cambiarOrdenFecha(funciones.ponerFechaSola(fechaHoy)) +"')");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\n   And (cast(fec_vencimiento as date) > ' " + fechaHoy +  "')");
			if(iBanco!=0)
				sbSQL.append("\n And ccc.id_banco_prestamo = "+iBanco);
			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append("\n ORDER BY substring(ccc.id_financiamiento, 5, 5)");
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
			
			System.out.println(list.size());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:GastosFinanciamientoCDaoImpl, M:obtenerContratos");
		}
		return list;
	}
	@Override
	public List<LlenaComboGralDto> obtenerGastos() {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n SELECT id_gasto AS ID, ");
			sbSQL.append("\n  descripcion as Descrip");
			sbSQL.append("\n  FROM cat_tipo_gasto");
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
			+ "P:Financiamiento, C:GastosFinanciamientoCDaoImpl, M:obtenerGastos");
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
	public List<GastoComisionCreditoDto> selectGastos(String psFinan, int piDisp){
		List<GastoComisionCreditoDto> list = new ArrayList<GastoComisionCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n  SELECT cac.id_contrato, cac.id_disposicion, cac.tipo_gasto, ctg.descripcion, cac.gasto, cac.comision, ");
			sbSQL.append("\n         cac.iva, cac.banco_gastcom , coalesce(cb.desc_banco, '') as desc_banco, ");
			sbSQL.append("\n         cac.clabe_bancaria_gastcom, cac.id_amortizacion, coalesce(cac.pagar, '') as pagar, cac.fec_pago ");
			sbSQL.append("\n  FROM  cat_amortizacion_credito cac left join cat_banco cb on ");
			sbSQL.append("\n        (cac.banco_gastcom = cb.id_banco And cac.banco_gastcom <> 0), cat_tipo_gasto ctg ");
			sbSQL.append("\n  WHERE cac.tipo_gasto = ctg.id_gasto ");
			sbSQL.append("\n        And id_contrato = '" + psFinan + "' ");
			sbSQL.append("\n        And id_disposicion = " + piDisp + " ");
			sbSQL.append("\n        And capital = 0 ");
			sbSQL.append("\n        And interes = 0 ");
			sbSQL.append("\n        And cac.estatus <> 'X' ");
			sbSQL.append("\n  ORDER BY id_contrato, id_disposicion ");
			logger.info("selectGastos "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public GastoComisionCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					GastoComisionCreditoDto compon = new GastoComisionCreditoDto();
					String vsTipoPago="";
					double  vdPorcentaje=0,vsImporte=0;
					vsTipoPago = rs.getString("descripcion").substring(0, 1);
					if(vsTipoPago == "N")
						vsTipoPago = "C";
					vdPorcentaje = 0;
					if (vsTipoPago == "G"){
						if(rs.getDouble("iva")!=0)
							vdPorcentaje=(rs.getDouble("iva")/100)*rs.getDouble("gasto");
						vsImporte=rs.getDouble("gasto");
					}else{
						vdPorcentaje = (rs.getDouble("iva")/100)*rs.getDouble("comision");
						vsImporte =rs.getDouble("comision");
					}
					compon.setIdContrato(rs.getString("id_contrato"));
					compon.setIdDisposicion(rs.getInt("id_disposicion"));
					compon.setTipoGasto(rs.getInt("tipo_gasto"));
					compon.setTipoPago(vsTipoPago.charAt(0));
					compon.setDescripcion(rs.getString("descripcion"));
					compon.setGasto(vsImporte);
					compon.setComision(rs.getDouble("iva"));
					compon.setIva(vdPorcentaje);
					compon.setBancoGastcom(rs.getInt("banco_gastcom"));
					compon.setDescBanco(rs.getString("desc_banco"));
					compon.setClabeBancariaGastcom(rs.getString("clabe_bancaria_gastcom"));
					compon.setTotal(vdPorcentaje+vsImporte);
					compon.setPagar(rs.getString("pagar").charAt(0));
					compon.setFecPago(funciones.cambiarFecha(rs.getString("fec_pago")));
					compon.setIdAmortizacion(rs.getInt("id_amortizacion"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:GastosFinanciamientoCDaoImpl, M:selectGastos");
		}
		return list;
	}
	@Override
	public  int obtenerIdAmortizacion(String linea, int idDisposicion){ 
		int valor = 0;
		try {
			StringBuffer sbSQL = new StringBuffer();
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append(
					"\n select coalesce(max(id_amortizacion), 0) as id_amortizacion from cat_amortizacion_credito where id_contrato = '"
							+linea+ "' and id_disposicion = "
							+ idDisposicion+" ");
			valor = jdbcTemplate.queryForInt(sbSQL.toString());
			valor=valor+1;
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:GastosFinanciamientoCDaoImpl, M:obtenerIdAmortizacion");
			return 0;
		}
		return valor;
	}
	@Override
	public int insertAmort(AmortizacionCreditoDto amortizacionCreditoDto, String piBisiesto) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		globalSingleton = GlobalSingleton.getInstancia();
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		try{
			StringBuffer sbSQL2 = new StringBuffer();
			sbSQL2.append("\n INSERT INTO cat_amortizacion_credito (id_contrato, id_disposicion, id_amortizacion, ");
			sbSQL2.append("\n           fec_vencimiento, capital, interes, gasto, comision, estatus, ");
			sbSQL2.append("\n           tasa_vigente, tasa_fija, periodo, no_amortizaciones, tasa_base, ");
			sbSQL2.append("\n           valor_tasa, puntos, tasa, saldo_insoluto, fecha_calint, iva, ");
			sbSQL2.append("\n           usuario_alta, fec_inicio, dia_cortecap, dia_corteint, dias, ");
			sbSQL2.append("\n           fec_pago, dias_periodo, banco_gastcom, clabe_bancaria_gastcom, ");
			sbSQL2.append("\n           comentario, tipo_gasto, sobre_tasacb, pagar, fact_capital, ");
			sbSQL2.append("\n           solo_renta, renta, no_folio_amort) ");
			sbSQL2.append("\n VALUES (  '" + amortizacionCreditoDto.getIdFinanciamiento() + "', "
					+ amortizacionCreditoDto.getIdDisposicion() + ", " + amortizacionCreditoDto.getIdAmortizacion()
					+ ", ");
			sbSQL2.append("\n           '" +ponerFormatoFecha(amortizacionCreditoDto.getFecVencimiento()) + "', "
					+ amortizacionCreditoDto.getCapital() + ", " + amortizacionCreditoDto.getInteresA() + ", ");
			sbSQL2.append("\n           " + amortizacionCreditoDto.getGasto() + ", "
					+ amortizacionCreditoDto.getComision() + ", '" + amortizacionCreditoDto.getEstatus() + "', ");
			sbSQL2.append("\n           " + amortizacionCreditoDto.getTasaVigente() + ", "
					+ amortizacionCreditoDto.getTasaFija() + ", '" + amortizacionCreditoDto.getPeriodo() + "', ");
			sbSQL2.append("\n           " + amortizacionCreditoDto.getNoAmortizaciones() + ", '"
					+ amortizacionCreditoDto.getTasaBase() + "', " + amortizacionCreditoDto.getValorTasa() + ", ");
			sbSQL2.append("\n           " + amortizacionCreditoDto.getPuntos() + ", '" + amortizacionCreditoDto.getTasa()
			+ "', " + amortizacionCreditoDto.getSaldo() + ", ");
			sbSQL2.append("\n           '" + ponerFormatoFecha(amortizacionCreditoDto.getFecVencimiento()) + "', "
					+ amortizacionCreditoDto.getPorcentaje() + ", " + globalSingleton.getUsuarioLoginDto().getIdUsuario()
					+ ", '" + fechaHoy + "', ");
			sbSQL2.append("\n          '" + amortizacionCreditoDto.getDiaCortecap() + "', '"
					+ amortizacionCreditoDto.getDiaCorteint() + "', " + piBisiesto + ", ");
			sbSQL2.append("\n           '" +ponerFormatoFecha(amortizacionCreditoDto.getFecPago()) + "', "
					+ amortizacionCreditoDto.getDiasPeriodo() + ", " + amortizacionCreditoDto.getBancoGastcom() + ", '"
					+ amortizacionCreditoDto.getClabeBancariaGastcom() + "', ");
			sbSQL2.append("\n           '" + amortizacionCreditoDto.getComentario() + "' , "
					+ amortizacionCreditoDto.getTipoGasto() + ", " + amortizacionCreditoDto.getSobreTasacb() + ", '"
					+ amortizacionCreditoDto.getPagar() + "', ");
			sbSQL2.append("\n           " + amortizacionCreditoDto.getFactCapital() + ", "
					+ amortizacionCreditoDto.getSoloRenta() + ", " + amortizacionCreditoDto.getRenta() + ", "
					+ amortizacionCreditoDto.getNoFolioAmort() + " )");
			logger.info("insertAmort "+sbSQL2);
			return jdbcTemplate.update(sbSQL2.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:GastosFinanciamientoCDaoImpl, M:insertAmort");
			return 0;
		}
	}
	@Override
	public int eliminarGastos(AmortizacionCreditoDto amortizacionCreditoDto) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		globalSingleton = GlobalSingleton.getInstancia();
		try{
			StringBuffer sbSQL2 = new StringBuffer();
			sbSQL2.append("\n UPDATE cat_amortizacion_credito SET estatus = 'X', usuario_modif = " + globalSingleton.getUsuarioLoginDto().getIdUsuario()+", fec_modif = '"+fechaHoy+ "' ");
			sbSQL2.append("\n WHERE  id_contrato = '" + amortizacionCreditoDto.getIdFinanciamiento() +  "' ");
			sbSQL2.append("\n       AND id_disposicion = "+amortizacionCreditoDto.getIdDisposicion()+" ");
			sbSQL2.append("\n       AND id_amortizacion = "+amortizacionCreditoDto.getIdAmortizacion()+ " ");
			sbSQL2.append("\n       AND capital = 0 ");
			sbSQL2.append("\n       AND interes = 0 ");
			logger.info("insertAmort "+sbSQL2);
			return jdbcTemplate.update(sbSQL2.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:GastosFinanciamientoCDaoImpl, M:insertAmort");
			return 0;
		}
	}
	@Override
	public List<Map<String, Object>> obtenerReporteGastos(String idLinea,int idDisposicion) {
		StringBuffer sbSql = new StringBuffer();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		try {
			sbSql.append("\n  SELECT cac.id_contrato, cac.id_disposicion, cac.tipo_gasto, ctg.descripcion, ");
			sbSql.append("\n        case when coalesce(cac.gasto, 0) <> 0 then cac.gasto else cac.comision end as gasto, ");
			sbSql.append("\n        cast(((cac.iva/100) * case when coalesce(cac.gasto, 0) <> 0 then cac.gasto else cac.comision end) as numeric(15,2)) as iva, ");
			sbSql.append("\n        cac.iva as porcentaje, cac.banco_gastcom , coalesce(cb.desc_banco, '') as desc_banco, ");
			sbSql.append("\n      coalesce(cac.clabe_bancaria_gastcom, '') as clabe_bancaria_gastcom, cac.id_amortizacion, cac.fec_pago as fecha_pago, ");
			sbSql.append("\n        cast(((case when coalesce(cac.gasto, 0) <> 0 then cac.gasto else cac.comision end) + ((cac.iva/100) * ");
			sbSql.append("\n        case when coalesce(cac.gasto, 0) <> 0 then cac.gasto else cac.comision end)) as numeric(15,2)) as total ");
			sbSql.append("\n FROM  cat_amortizacion_credito cac left join cat_banco cb on ");
			sbSql.append("\n       (cac.banco_gastcom = cb.id_banco And cac.banco_gastcom <> 0), cat_tipo_gasto ctg ");
			sbSql.append("\n WHERE cac.tipo_gasto = ctg.id_gasto ");
			sbSql.append("\n      And id_contrato = '" +idLinea+ "' ");
			sbSql.append("\n     And id_disposicion = "+idDisposicion+ " ");
			sbSql.append("\n      And capital = 0 ");
			sbSql.append("\n      And interes = 0 ");
			sbSql.append("\n ORDER BY id_contrato, id_disposicion ");
			logger.info("obtenerReporteGastos "+sbSql);
			return this.getJdbcTemplate().queryForList(sbSql.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:GastosFinanciamientoCDaoImpl, M:obtenerReporteGastos");
			return null;
		}
	}
	public String ponerFormatoFecha(String fechaString){
		return fechaString.substring(6,10)+"-"+fechaString.substring(3,5)+"-"+fechaString.substring(0,2);
	
	}
}
