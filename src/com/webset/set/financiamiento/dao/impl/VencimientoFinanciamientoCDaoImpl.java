package com.webset.set.financiamiento.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.swing.plaf.synth.SynthSpinnerUI;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.coinversion.dao.impl.CoinversionDaoImpl;
import com.webset.set.financiamiento.dao.VencimientoFinanciamientoCDao;
import com.webset.set.financiamiento.dto.AmortizacionCreditoDto;
import com.webset.set.financiamiento.dto.BeneficiarioDto;
import com.webset.set.financiamiento.dto.ContratoCreditoDto;
import com.webset.set.financiamiento.dto.ControlPagosPasivos;
import com.webset.set.financiamiento.dto.CorreoVencimientoDto;
import com.webset.set.financiamiento.dto.ProvisionCreditoDTO;
import com.webset.set.global.dao.GlobalDao;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.Retorno;

public class VencimientoFinanciamientoCDaoImpl implements VencimientoFinanciamientoCDao {
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	GlobalDao globalDao;
	ConsultasGenerales consultasGenerales;
	Funciones funciones = new Funciones();
	GlobalSingleton globalSingleton;
	String gsDBM = ConstantesSet.gsDBM;
	String psTipoMenu="";
	private static Logger logger = Logger.getLogger(VencimientoFinanciamientoCDaoImpl.class);
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
	public List<LlenaComboGralDto> obtenerPaisVenc() {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n SELECT distinct cp.id_pais as ID, cp.desc_pais as Descrip  ");
			sbSQL.append("\n FROM  cat_pais cp, cat_contrato_credito ccc, cat_disposicion_credito cdc ");
			sbSQL.append("\n WHERE ccc.id_pais = cp.id_pais ");
			sbSQL.append("\n   And ccc.id_financiamiento = cdc.id_financiamiento ");
			sbSQL.append("\n   And cdc.estatus = 'A' ");
			sbSQL.append("\n ORDER BY ID ");
			logger.info("obtenerPaisVenc "+sbSQL);
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
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:obtenerPaisVenc");
		}
		return list;
	}
	@Override
	public List<LlenaComboGralDto> obtenerContratos(int piEmpresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		StringBuffer sbSQL = new StringBuffer();
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n SELECT  ccc.id_financiamiento AS ID, ccc.id_financiamiento as Descrip");
			sbSQL.append("\n  FROM  cat_contrato_credito ccc, usuario_empresa ue");
			sbSQL.append("\n WHERE ccc.estatus = 'A'");
			sbSQL.append("\n and ccc.no_empresa="+piEmpresa);

			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append("\n And (cast(fec_vencimiento as date) > '"+ funciones.cambiarOrdenFecha(funciones.ponerFechaSola(fechaHoy)) +"')");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\n   And (cast(fec_vencimiento as date) > ' " +fechaHoy + "')");
			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append("\n ORDER BY substring(ccc.id_financiamiento, 5, 5) ");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\n ORDER BY substring(ccc.id_financiamiento from 5 for 8)");
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
	@Override
	public List<LlenaComboGralDto> obtenerEmpresas() {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		globalSingleton = GlobalSingleton.getInstancia();
		StringBuffer sbSQL = new StringBuffer();
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n SELECT e.no_empresa as ID, e.nom_empresa as Descrip ");
			sbSQL.append("\n FROM   empresa e, usuario_empresa ue ");
			sbSQL.append("\n WHERE  e.no_empresa = ue.no_empresa ");
			sbSQL.append("\n   And ue.no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario()+ " ");
			logger.info("obtenerEmpresas "+sbSQL);
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
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:obtenerEmpresas");
		}
		return list;
	}
	@Override
	public List<LlenaComboGralDto> obtenerDivisas(int noEmpresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		globalSingleton = GlobalSingleton.getInstancia();
		StringBuffer sbSQL = new StringBuffer();
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n SELECT id_divisa as ID, desc_divisa as Descrip ");
			sbSQL.append("\n From cat_divisa ");
			sbSQL.append("\n WHERE clasificacion = 'D' ");
			sbSQL.append("\n   And id_divisa in (select distinct cdc.id_divisa ");
			sbSQL.append("\n                    from  cat_disposicion_credito cdc, cat_tipo_contrato ctc, cat_contrato_credito ccc ");
			sbSQL.append("\n                     Where ");
			sbSQL.append("\n                          cdc.id_financiamiento = ccc.id_financiamiento ");
			sbSQL.append("\n                          and cdc.estatus = 'A' ");
			sbSQL.append("\n                          and ctc.financiamiento = '" + psTipoMenu + "' ");
			sbSQL.append("\n                          and ccc.no_empresa= " + noEmpresa+ ") ");

			logger.info("obtenerDivisas "+sbSQL);
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
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:obtenerDivisas");
		}
		return list;
	}
	@Override
	public List<AmortizacionCreditoDto> selectMovimientoAzt(String psFecIni,String psPais,
			int plEmpresa,int piBanco,String psLinea,int piTipoFinan,String psDivisa,int plCredito){
		List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>();

		globalSingleton = GlobalSingleton.getInstancia();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n  SELECT  ca.fec_vencimiento, cc.id_divisa, ca.id_disposicion, ca.id_amortizacion, coalesce(ccc.monto_disposicion, 0) as monto_disposicion, ");
			sbSQL.append("\n         coalesce(case when ca.id_amortizacion = 0 and ca.gasto = 0 and ca.comision = 0 then ca.interes else ");
			sbSQL.append("\n         case when ca.capital <> 0 then ca.capital when ca.gasto <> 0 then ");
			sbSQL.append("\n         ca.gasto when ca.comision <> 0 then ca.comision end end, 0) as importe, ");
			sbSQL.append("\n         coalesce(ccc.renta, 0) as renta, ");
			sbSQL.append("\n         cast(case when coalesce(ca.fact_capital, 0) = 0 then ");
			sbSQL.append("\n         coalesce(case when ca.id_amortizacion = '0' then ");
			sbSQL.append("\n         case when ccc.renta = 0 then (ca.interes * (ca.iva/100)) else (ccc.renta * (ca.iva/100)) end ");
			sbSQL.append("\n            else case when ca.iva <> 0 then " );
			sbSQL.append("\n         case when ca.gasto <> 0 then ");
			sbSQL.append("\n         	ca.gasto * (ca.iva/100) when ca.comision <> 0 then ca.comision * (ca.iva/100) end else 0 end end, 0) ");
			sbSQL.append("\n          else coalesce((ca.interes * (ca.iva/100)), 0) end as numeric (15,2)) as iva, ");
			sbSQL.append("\n          coalesce(cast(case when ca.fact_capital <> 0 then (ca.capital * (ca.fact_capital/100)) end as numeric(15,2)), 0) as iva_fact_capital, ");
			sbSQL.append("\n          cc.id_banco_prestamo, ca.id_contrato, cc.no_empresa, e.nom_empresa, ");
			if(psTipoMenu.equals("A"))
				sbSQL.append("\n     cas.desc_arrendadora as desc_banco, ");
			else
				sbSQL.append("\n     cb.desc_banco, ");
			sbSQL.append("\n         ccc.forma_pago, ccc.equivalente, cgf.id_grupo_flujo as gpo_empresa, ");
			sbSQL.append("\n         case when ca.id_amortizacion <> 0 and ca.tipo_gasto = 0 then 'Capital' ");
			sbSQL.append("\n        when ccc.renta <> 0 then 'Renta' else coalesce(ctg.descripcion, '') end as descripcion, ");
			sbSQL.append("\n         coalesce(ctg.valor, '') as valor, coalesce(ca.fact_capital, 0) as fact_capital, coalesce(ccc.id_banco_disp, 0) as id_banco_disp, coalesce(ccc.id_chequera_disp, '') as id_chequera_disp, ca.fec_inicio ");
			sbSQL.append("\n FROM    cat_amortizacion_credito ca left join cat_tipo_gasto ctg on (ca.tipo_gasto = ctg.id_gasto), cat_contrato_credito cc, empresa e, ");
			sbSQL.append("\n         cat_disposicion_credito ccc, grupo_empresa ge, cat_grupo_flujo cgf, cat_tipo_contrato ctc ");
			if(psTipoMenu.equals("A"))
				sbSQL.append("\n     , cat_arrendadoras cas ");
			else
				sbSQL.append("\n     , cat_banco cb ");
			sbSQL.append("\n WHERE   ca.id_contrato = cc.id_financiamiento ");
			sbSQL.append("\n       And cc.no_empresa = e.no_empresa ");
			if(psTipoMenu.equals("A"))
				sbSQL.append("\n   And cc.id_banco_prestamo = cas.id_arrendadora ");
			else
				sbSQL.append("\n   And cc.id_banco_prestamo = cb.id_banco ");
			sbSQL.append("\n       And ca.id_contrato = ccc.id_financiamiento ");
			sbSQL.append("\n       And ca.id_disposicion = ccc.id_disposicion ");
			sbSQL.append("\n       And cc.no_empresa = ge.no_empresa ");
			sbSQL.append("\n       And ge.id_grupo_flujo = cgf.id_grupo_flujo ");
			sbSQL.append("\n       And cc.id_tipo_financiamiento = ctc.id_tipo_contrato ");
			sbSQL.append("\n       And ctc.financiamiento = '" + psTipoMenu + "' ");
			sbSQL.append("\n       And ca.estatus = 'A' ");
			sbSQL.append("\n       And e.no_empresa in (select distinct no_empresa from usuario_empresa where no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario()+") ");
			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append("\n       And cast(ca.fec_vencimiento as date) <= '" + funciones.cambiarOrdenFecha(psFecIni) + "' ");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\n        And cast(ca.fec_vencimiento as date) <= '" + psFecIni + "' ");
			if (!psPais.equals("0") )
				sbSQL.append("\n        And cc.id_pais= '" + psPais + "' ");
			sbSQL.append("\n        And ca.pagar = 'S' ");
			if (plEmpresa!= 0)
				sbSQL.append("\n    And cc.no_empresa = " + plEmpresa + " ");
			if (piBanco != 0)
				sbSQL.append("\n    And cc.id_banco_prestamo = " + piBanco+" ");
			if (!psLinea.equals("0")&&!psLinea.equals("")) 
				sbSQL.append("\n    And cc.id_financiamiento = '" + psLinea + "' ");
			if (plCredito!=0) 
				sbSQL.append("\n    And ccc.id_disposicion = '" + plCredito + "' ");
			if (piTipoFinan !=0)
				sbSQL.append("\n    And cc.id_tipo_financiamiento = " + piTipoFinan + " ");
			if(!psDivisa.equals("0")) 
				sbSQL.append("\n And cc.id_divisa = '" + psDivisa + "' ");
			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append("\n ORDER BY cast(ca.fec_vencimiento as date), substring(ca.id_contrato, 5, 5), ca.id_disposicion, ca.id_amortizacion ");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\n ORDER BY cast(ca.fec_vencimiento as date), substring(ca.id_contrato from 5 for 10), ca.id_disposicion, ca.id_amortizacion ");
			logger.info("selectAmortizacionesCapital "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public AmortizacionCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					double vdCapital=0,vdInteres=0,  vdTotCap=0, vdTotInt=0,vdTotRenta=0,vdTotIva=0,vdPagTotal=0,vdTotPago=0;
					String vsValorG="";
					String vsTipoMenu;
					AmortizacionCreditoDto dto = new AmortizacionCreditoDto();
					dto.setIdContrato(rs.getString("id_contrato"));
					dto.setIdDisposicion(rs.getInt("id_disposicion"));
					dto.setIdAmortizacion(rs.getInt("id_amortizacion"));
					if(rs.getInt("id_amortizacion")==0)
						vdInteres=rs.getDouble("importe");
					if(rs.getInt("id_amortizacion")!=0)  
						vdCapital=rs.getDouble("importe");
					dto.setCapital(vdCapital);
					dto.setInteres(vdInteres);
					dto.setRenta(0);
					dto.setIva(rs.getDouble("iva"));
					dto.setFecVen(funciones.cambiarFecha(rs.getString("fec_vencimiento")));
					dto.setEmpresa(rs.getInt("no_empresa"));
					dto.setDescEmpresa(rs.getString("nom_empresa"));
					dto.setInstitucion(rs.getInt("id_banco_prestamo"));
					dto.setDescInstitucion(rs.getString("desc_banco"));
					dto.setSaldo(rs.getDouble("monto_disposicion"));
					dto.setFormaPago(rs.getInt("forma_pago"));
					dto.setEquivalente(rs.getString("equivalente"));
					dto.setGpoEmpresa(rs.getInt("gpo_empresa"));
					dto.setDescPago(rs.getString("descripcion"));
					dto.setValorPago(rs.getString("valor"));
					dto.setBancoBenef(rs.getInt("id_banco_disp"));
					dto.setChequeraBenef(rs.getString("id_chequera_disp"));
					dto.setDivisa(rs.getString("id_divisa"));
					dto.setFecIni(funciones.cambiarFecha(rs.getString("fec_inicio")));
					vdTotCap = vdTotCap + vdCapital;
					vdTotInt = vdTotInt + vdInteres;
					if (rs.getString("valor") == "")
						dto.setRenta(rs.getDouble("renta"));
					if (rs.getString("valor") == "") 
						vdTotRenta = vdTotRenta + rs.getDouble("renta");
					vdTotIva = vdTotIva + rs.getDouble("iva");
					vsValorG = rs.getString("valor");
					/*If vsTipoMenu = "A" Then
				                If Trim(rstMov!valor) = "" Then
				                    vdPagTotal = CDbl(rstMov!RENTA) + CDbl(rstMov!iva)
				                Else
				                    vdPagTotal = vdCapital + CDbl(rstMov!iva)
				                    .TextMatrix(.Rows - 1, LI_C_TOTAL_PAGO) = Format$(vdPagTotal, "#,##0.00")
				                End If

				            Else*/
					vdPagTotal = vdCapital + vdInteres + rs.getDouble("iva");
					dto.setTotalPago(vdPagTotal);
					//End If
					vdTotPago = vdTotPago + vdPagTotal;
					/*If vsTipoMenu = "A" Then
				                If Not rstMov.EOF Then
				                    If CDate(vsFecVen) = CDate(rstMov!fec_vencimiento) And Trim(vsValorG) = "" Then
				                        .TextMatrix(.Rows - 1, LI_C_AMORTIZACION) = rstMov!id_amortizacion
				                        .TextMatrix(.Rows - 1, LI_C_CAPITAL) = Format$(rstMov!Importe, "#,##0.00")

				                        If CDbl(rstMov!fact_capital) <> 0 Then
				                            .TextMatrix(.Rows - 1, LI_C_IVA) = Format$(CDbl(.TextMatrix(.Rows - 1, LI_C_IVA)) + (CDbl(rstMov!Importe) * CDbl(rstMov!fact_capital / 100)), "#,##0.00")
				                            vdPagTotal = vdPagTotal + (CDbl(rstMov!Importe) * CDbl(rstMov!fact_capital / 100))
				                        End If
				                        .TextMatrix(.Rows - 1, LI_C_TOTAL_PAGO) = Format$(vdPagTotal, "#,##0.00")
				                        vdTotCap = vdTotCap + CDbl(rstMov!Importe)
				                        rstMov.MoveNext
				                    End If
				                    If Not rstMov.EOF Then vsFecVen = rstMov!fec_vencimiento
				                End If
				            End If*/
					vdInteres = 0;
					vdCapital = 0;
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
	public List<LlenaComboGralDto> obtenerBancoVenci(String psNac, String psTipoMenu){
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		globalSingleton = GlobalSingleton.getInstancia();
		StringBuffer sbSQL = new StringBuffer();
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n SELECT distinct ca.id_arrendadora as ID, ca.desc_arrendadora as Descrip ");
			sbSQL.append("\n FROM  cat_contrato_credito ccc, cat_disposicion_credito cdc, cat_amortizacion_credito cac, cat_arrendadoras ca, cat_tipo_contrato ctc ");
			sbSQL.append("\n WHERE ccc.id_financiamiento = cdc.id_financiamiento ");
			sbSQL.append("\n       And cdc.id_financiamiento = cac.id_contrato ");
			sbSQL.append("\n       And cdc.id_disposicion = cac.id_disposicion ");
			sbSQL.append("\n       And ccc.id_banco_prestamo = ca.id_arrendadora ");
			sbSQL.append("\n       And ccc.id_tipo_financiamiento = ctc.id_tipo_contrato ");
			sbSQL.append("\n       And ctc.financiamiento in ('" + psTipoMenu + "') ");
			sbSQL.append("\n       And ccc.no_empresa in (SELECT   e.no_empresa FROM empresa e, usuario_empresa ue ");
			sbSQL.append("\n                              WHERE    e.no_empresa = ue.no_empresa ");
			sbSQL.append("\n                                       And ue.no_usuario = '" + globalSingleton.getUsuarioLoginDto().getIdUsuario() + "') ");
			sbSQL.append("\n       And ccc.estatus = 'A' ");
			sbSQL.append("\n       And cac.estatus = 'A' ");
			sbSQL.append("\n UNION ALL ");
			sbSQL.append("\n SELECT distinct cb.id_banco as ID, cb.desc_banco as Descrip ");
			sbSQL.append("\n FROM  cat_contrato_credito ccc, cat_disposicion_credito cdc, cat_amortizacion_credito cac, cat_banco cb, cat_tipo_contrato ctc ");
			sbSQL.append("\n 	 WHERE ccc.id_financiamiento = cdc.id_financiamiento ");
			sbSQL.append("\n       And cdc.id_financiamiento = cac.id_contrato ");
			sbSQL.append("\n       And cdc.id_disposicion = cac.id_disposicion ");
			sbSQL.append("\n       And ccc.id_banco_prestamo = cb.id_banco ");
			sbSQL.append("\n       And ccc.id_tipo_financiamiento = ctc.id_tipo_contrato ");
			sbSQL.append("\n       And ctc.financiamiento in ('" + psTipoMenu + "') ");
			sbSQL.append("\n       And ccc.no_empresa in (SELECT   e.no_empresa FROM empresa e, usuario_empresa ue ");
			sbSQL.append("\n                             WHERE    e.no_empresa = ue.no_empresa ");
			sbSQL.append("\n                                       And ue.no_usuario = '" + globalSingleton.getUsuarioLoginDto().getIdUsuario() + "') ");
			sbSQL.append("\n       And ccc.estatus = 'A' ");
			sbSQL.append("\n       And cac.estatus = 'A' ");
			if( !psNac.equals("") )
				sbSQL.append("\n And cb.nac_ext = '" + psNac + "' ");
			sbSQL.append("\n ORDER BY Descrip ");
			logger.info("obtenerBancoVenci "+sbSQL);
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
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:obtenerBancoVenci");
		}
		return list;
	}
	@Override
	public List<LlenaComboGralDto> obtenerBancoPago(int plEmpresa){
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		globalSingleton = GlobalSingleton.getInstancia();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n SELECT distinct ccb.id_banco as ID, cb.desc_banco as Descrip ");
			sbSQL.append("\n  FROM  cat_cta_banco ccb, cat_banco cb ");
			sbSQL.append("\n WHERE ccb.id_banco = cb.id_banco ");
			if (plEmpresa!=0) 
				sbSQL.append("\n And ccb.no_empresa = " + plEmpresa + " ");
			sbSQL.append("\n ORDER BY ID ");
			logger.info("obtenerBancoPago "+sbSQL);
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
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:obtenerBancoPago");
		}
		return list;
	}
	@Override
	public List<AmortizacionCreditoDto> storeSelectCapital(String psLinea,int piDisposicion){
		List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n SELECT coalesce(cast(sum(capital) as numeric(15,1)), 0) as capital ");
			sbSQL.append("\n FROM  cat_amortizacion_credito ");
			sbSQL.append("\n WHERE id_contrato = '" + psLinea + "' ");
			sbSQL.append("\n   And id_disposicion = " + piDisposicion + " ");
			sbSQL.append("\n  And estatus = 'A' ");
			logger.info("storeSelectCapital "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public AmortizacionCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					AmortizacionCreditoDto compon = new AmortizacionCreditoDto();
					compon.setCapital(rs.getDouble("capital"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:storeSelectCapital");
		}
		return list;
	}
	@Override
	public List<AmortizacionCreditoDto> selectPrimerAmortAct(String psLinea,int piDisposicion){
		List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n SELECT * FROM  cat_amortizacion_credito");
			sbSQL.append("\n WHERE id_contrato = '" + psLinea + "'");
			sbSQL.append("\n   And id_disposicion = " + piDisposicion + "");
			sbSQL.append("\n   And estatus = 'A'");
			sbSQL.append("\n   And capital <> 0");
			sbSQL.append("\n   And id_amortizacion <> 0");
			sbSQL.append("\n ORDER BY id_amortizacion asc");
			logger.info("selectPrimerAmortAct "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public AmortizacionCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					AmortizacionCreditoDto compon = new AmortizacionCreditoDto();
					compon.setIdAmortizacion(rs.getInt("id_amortizacion"));
					compon.setFecInicio(rs.getString("fec_inicio"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:selectPrimerAmortAct");
		}
		return list;
	}
	@Override
	public void cancelaTransaccion() {
		try {
			StringBuffer sbSQL = new StringBuffer();
			sbSQL.append("ROLLBACK");
			jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:cancelaTransaccion");

		}
	}
	@Override
	public void iniciaTransaccion() {
		try {
			StringBuffer sbSQL = new StringBuffer();
			sbSQL.append("BEGIN TRANSACTION");
			jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:iniciaTransaccion");
		}
	}
	@Override
	public void terminaTransaccion() {
		try {
			StringBuffer sbSQL = new StringBuffer();
			sbSQL.append("COMMIT");
			jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:terminaTransaccion");
		}
	}
	@Override
	public int insertPagoTotal(AmortizacionCreditoDto dto, int vlNoAmort) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		globalSingleton = GlobalSingleton.getInstancia();
		int vlNoAmortE=0,piIdAmortizacion,resultado=0;
		String psFecInicio,pdFecVenCap;
		Date fechaHoy = consultasGenerales.obtenerFechaHoy();
		try{
			StringBuffer sbSQL = new StringBuffer();
			List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>();
			sbSQL.append("\n select coalesce(max(id_amortizacion), 0) as id_amortizacion ");
			sbSQL.append("\n from cat_amortizacion_credito ");
			sbSQL.append("\n where id_contrato = '" +dto.getIdFinanciamiento()+ "' and id_disposicion = "+dto.getIdDisposicion());
			logger.info("insertPagoTotal "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public AmortizacionCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					AmortizacionCreditoDto compon = new AmortizacionCreditoDto();
					compon.setIdAmortizacion(rs.getInt("id_amortizacion"));
					return compon;
				}
			});
			if(list.size()>0){
				vlNoAmortE = list.get(0).getIdAmortizacion() + 1;
			}
			else{
				// Err.Raise 999, "FunSQLInsertPagoTotal", "No se encontro el dato de la amortizacion"
				return 0;
			}
			sbSQL= new StringBuffer();
			list=null;
			sbSQL.append("\n select ");
			if(gsDBM.equals("SQL SERVER")||gsDBM.equals("SYBASE"))
				sbSQL.append("\n  Top 1");
			sbSQL.append("\n fec_inicio, fec_vencimiento, id_amortizacion, no_folio_amort ");
			sbSQL.append("\n from cat_amortizacion_credito ");
			sbSQL.append("\n where id_contrato = '"+dto.getIdFinanciamiento()+ "' ");
			sbSQL.append("\n   and id_disposicion = " +dto.getIdDisposicion());
			sbSQL.append("\n   and capital <> 0 ");
			sbSQL.append("\n   and estatus = 'A' ");
			sbSQL.append("\n   and fec_vencimiento >=convert(date,  '" +dto.getFecVencimiento() +"',103)");
			sbSQL.append("\n   order by fec_vencimiento ");
			if(gsDBM.equals("POSTGRESQL") ||gsDBM.equals("ORACLE"))
				sbSQL.append("\n limit 1 ");
			logger.info("insertPagoTotal "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public AmortizacionCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					AmortizacionCreditoDto compon = new AmortizacionCreditoDto();
					compon.setIdAmortizacion(rs.getInt("id_amortizacion"));
					compon.setNoFolioAmort(rs.getInt("no_folio_amort"));
					compon.setFecInicio(rs.getString("fec_inicio"));
					compon.setFecVencimiento(rs.getString("fec_vencimiento"));
					return compon;
				}
			});
			if(list.size()>0){
				vlNoAmort =list.get(0).getNoFolioAmort();
				psFecInicio =list.get(0).getFecInicio();
				pdFecVenCap = list.get(0).getFecVencimiento();
				piIdAmortizacion = list.get(0).getIdAmortizacion();
			}
			else{
				// Err.Raise 999, "FunSQLInsertPagoTotal", "No se encontro el pago de capital"
				return 0;
			}
			sbSQL=new StringBuffer();
			list=null;
			sbSQL.append("\n  INSERT INTO cat_amortizacion_credito ");
			sbSQL.append("\n  SELECT ");
			if(gsDBM.equals("SQL SERVER") ||gsDBM.equals("SYBASE"))
				sbSQL.append("\n  Top 1 ");
			sbSQL.append("\n  id_contrato, id_disposicion, " + vlNoAmortE + ", cast('" +dto.getFecVencimiento()+"' as date), " + dto.getImporte() + ", ");
			sbSQL.append("\n        interes, gasto, comision, estatus, tasa_vigente, tasa_fija, periodo, ");
			sbSQL.append("\n        1, tasa_base, valor_tasa, puntos, tasa, saldo_insoluto, ");
			sbSQL.append("\n        pagar, '" + dto.getFecInicio()+ "', iva, cast('" +dto.getFecVencimiento() + "' as date), " +globalSingleton.getUsuarioLoginDto().getIdUsuario()+ ", 0, dia_corteint, dias, ");
			sbSQL.append("\n         cast('" +dto.getFecVencimiento()+ "' as date), 0, banco_gastcom, clabe_bancaria_gastcom, 'Pago anticipado de capital', ");
			sbSQL.append("\n        tipo_gasto, sobre_tasacb , fact_capital, solo_renta, renta, usuario_modif, fec_modif, no_folio_amort, cve_control ");
			sbSQL.append("\n  FROM  cat_amortizacion_credito ");
			sbSQL.append("\n  WHERE id_contrato = '" +dto.getIdFinanciamiento()+ "' ");
			sbSQL.append("\n    And id_disposicion = " +dto.getIdDisposicion()+" ");
			sbSQL.append("\n    And capital <> 0 ");
			sbSQL.append("\n    And id_amortizacion = "+dto.getIdAmortizacion());
			sbSQL.append("\n   And fec_inicio =cast('" +dto.getFecInicio()+"' as date) and estatus = 'A' ");
			sbSQL.append("\n  ORDER BY id_amortizacion desc ");
			if(gsDBM.equals("POSTGRESQL") ||gsDBM.equals("ORACLE"))
				sbSQL.append("\n limit 1 ");
			logger.info("insertPagoTotal "+sbSQL);
			resultado= jdbcTemplate.update(sbSQL.toString());
			if(resultado== 1){	
				resultado=0;
				sbSQL=new StringBuffer();
				sbSQL.append("\n UPDATE cat_amortizacion_credito ");
				sbSQL.append("\n SET fec_inicio = cast('" +dto.getFecVencimiento() +"' as date)");
				sbSQL.append("\n ,comentario = 'Recortado por pago anticipado de capital'");
				sbSQL.append("\n ,fec_modif = '" +dto.getFecVencimiento() +"'");
				sbSQL.append("\n ,usuario_modif = " +globalSingleton.getUsuarioLoginDto().getIdUsuario());
				sbSQL.append("\n WHERE id_contrato = '" +dto.getIdFinanciamiento()+"' ");
				sbSQL.append("\n   And id_disposicion = "+dto.getIdDisposicion()+" ");
				sbSQL.append("\n   And capital <> 0 ");
				sbSQL.append("\n   And id_amortizacion = "+dto.getIdAmortizacion());
				sbSQL.append("\n   And fec_inicio = cast( '" +dto.getFecInicio()+"'as date)  and estatus = 'A' ");

				resultado = jdbcTemplate.update(sbSQL.toString());
			}
			else{
				return 0;
			}
			logger.info("insertPagoTotal "+sbSQL);
			return resultado;
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:insertPagoTotal");
			return 0;
		}
	}
	@Override
	public int insertPagoInteresParcial(AmortizacionCreditoDto dto, int vlNoAmort) {
		int resultado=0,iDias;
		String vsFecIni,vsFecVen;
		globalSingleton = GlobalSingleton.getInstancia();
		try {
			StringBuffer sbSQL = new StringBuffer();
			List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>(); 
			sbSQL.append("\n SELECT fec_inicio, fec_vencimiento, dias FROM cat_amortizacion_credito");
			sbSQL.append("\n WHERE id_contrato = '"+dto.getIdFinanciamiento()+ "'");
			sbSQL.append("\n   And id_disposicion = " +dto.getIdDisposicion()+ " ");
			sbSQL.append("\n   And interes <> 0");
			sbSQL.append("\n   And estatus = 'A' ");
			sbSQL.append("\n   And fec_inicio <= '"+dto.getFecInicio()+ "' ");
			sbSQL.append("\n   And fec_vencimiento >= '" +dto.getFecVencimiento()+"'");
			sbSQL.append("\n ORDER BY fec_vencimiento ");
			logger.info("insertPagoTotal "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public AmortizacionCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					AmortizacionCreditoDto compon = new AmortizacionCreditoDto();
					compon.setDias(rs.getInt("dias"));
					compon.setFecInicio(rs.getString("fec_inicio"));
					compon.setFecVencimiento(rs.getString("fec_vencimiento"));
					return compon;
				}
			});
			if(list.size()>0){
				vsFecIni = list.get(0).getFecInicio();
				vsFecVen = list.get(0).getFecVencimiento();
				iDias = list.get(0).getDias();
			}else{
				return 0;
			}
			if(cambiarOrdenFecha(vsFecVen).compareTo(dto.getFecVencimiento())!=0){
				sbSQL = new StringBuffer();
				sbSQL.append("\n INSERT INTO cat_amortizacion_credito ");
				sbSQL.append("\n SELECT ");
				if(gsDBM.equals("SQL SERVER")||gsDBM.equals("SYBASE"))
					sbSQL.append("\n Top 1 ");
				sbSQL.append("\n id_contrato, id_disposicion, 0, '" + dto.getFecVencimiento() + "', 0, ");	
				sbSQL.append("\n       cast( ( (((CASE WHEN tasa = 'V' then tasa_vigente ");
				sbSQL.append("\n       else tasa_fija end) / 100 / " + iDias + ") * saldo_insoluto) *  ");
				if(gsDBM.equals("SQL SERVER")||gsDBM.equals("SYBASE"))
					sbSQL.append("\n       (datediff(dd,cast('" +vsFecIni+"' as datetime), cast('"+dto.getFecVencimiento()+ "' as datetime)))) as decimal(15,2)) as interes");
				if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
					sbSQL.append("\n       (cast('" +dto.getFecVencimiento()+ "' as date)- cast('" +vsFecIni+"' as date))) as decimal(15,2)) as interes");
				sbSQL.append("\n       , gasto, comision, estatus, tasa_vigente, tasa_fija, periodo, ");
				sbSQL.append("\n      1, tasa_base, valor_tasa, puntos, tasa, saldo_insoluto, ");
				sbSQL.append("\n       pagar, '" +vsFecIni+ "', iva, '" +dto.getFecVencimiento()+"', "+globalSingleton.getUsuarioLoginDto().getIdUsuario()+ ", 0, dia_corteint, ");
				sbSQL.append("\n       dias, '" +dto.getFecVencimiento()+"',");
				if(gsDBM.equals("SQL SERVER")||gsDBM.equals("SYBASE"))
					sbSQL.append("\n        (datediff(dd,cast('" +vsFecIni+"' as datetime), cast('"+ dto.getFecVencimiento()+ "' as datetime))) as dias_periodo,");
				if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
					sbSQL.append("\n       (cast('" + dto.getFecVencimiento()+ "' as date)- cast('" +vsFecIni+ "' as date)) as dias_periodo,");
				sbSQL.append("\n       banco_gastcom, clabe_bancaria_gastcom, 'Interes por pago anticipado de capital', ");
				sbSQL.append("\n      tipo_gasto, sobre_tasacb , fact_capital, solo_renta, renta, usuario_modif, fec_modif, no_folio_amort, cve_control ");
				sbSQL.append("\n FROM  cat_amortizacion_credito ");
				sbSQL.append("\n WHERE id_contrato = '" +dto.getIdFinanciamiento()+ "' ");
				sbSQL.append("\n   And id_disposicion = " +dto.getIdDisposicion()+" ");
				sbSQL.append("\n  And interes <> 0 ");
				sbSQL.append("\n  And fec_inicio = '" +vsFecIni+ "' ");
				sbSQL.append("\n   And fec_vencimiento = '" +dto.getFecVencimiento()+ "' and estatus = 'A'");
				sbSQL.append("\n ORDER BY id_amortizacion desc ");
				//	System.out.println(sbSQL.toString());
				if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
					sbSQL.append("\n limit 1 ");
				resultado = jdbcTemplate.update(sbSQL.toString());
				if(resultado==0){
					resultado=0;
					sbSQL = new StringBuffer();
					sbSQL.append("\n  update cat_amortizacion_credito ");
					sbSQL.append("\n  SET fec_inicio = convert(date, '" +dto.getFecVencimiento()+ "',103)");
					Date fechaInicial,fechaFinal;
					DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
					fechaInicial=df.parse(vsFecIni);
					fechaFinal=df.parse(dto.getFecVencimiento());
					sbSQL.append("\n ,dias_periodo = " +funciones.diasEntreFechas(fechaInicial, fechaFinal));
					sbSQL.append("\n ,usuario_modif = " +globalSingleton.getUsuarioLoginDto().getIdUsuario());
					sbSQL.append("\n ,fec_modif = convert(date, '" +dto.getFecVencimiento()+"',103)");
					sbSQL.append("\n WHERE id_contrato = '" +dto.getIdFinanciamiento()+ "'");
					sbSQL.append("\n   And id_disposicion = " +dto.getIdDisposicion()+" ");
					sbSQL.append("\n   And interes <> 0");
					sbSQL.append("\n    And estatus = 'A' ");
					sbSQL.append("\n    And fec_vencimiento =convert(date,  '"+dto.getFecVencimiento()+ "',103) ");
					resultado = jdbcTemplate.update(sbSQL.toString());
				}  else{
					return 0;
				}
			}
			else{
				resultado = 1;
			}
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:terminaTransaccion");
		}

		return resultado;
	}
	@Override
	public List<AmortizacionCreditoDto> selectCapitales(AmortizacionCreditoDto dto) {
		StringBuffer sbSQL = new StringBuffer();
		List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>(); 
		try{
			sbSQL.append("\n SELECT * FROM cat_amortizacion_credito");
			sbSQL.append("\n WHERE id_contrato  = '" +dto.getIdFinanciamiento()+"'");
			sbSQL.append("\n  and id_disposicion = " +dto.getIdDisposicion());
			sbSQL.append("\n  and estatus = 'A'");
			sbSQL.append("\n  and fec_vencimiento > '" +dto.getFecVencimiento()+ "' ");
			if(dto.isPrimeras()){
				sbSQL.append("\n ORDER BY fec_vencimiento, id_amortizacion  ");
			}else{
				if(gsDBM.equals("SQL SERVER")||gsDBM.equals("SYBASE"))
					sbSQL.append("\n ORDER BY fec_vencimiento, id_amortizacion desc ");
				else
					sbSQL.append("\n ORDER BY fec_vencimiento desc, id_amortizacion desc ");
			}
			logger.info("selectCapitales "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public AmortizacionCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					AmortizacionCreditoDto compon = new AmortizacionCreditoDto();
					compon.setNoFolioAmort(rs.getInt("no_folio_amort"));
					compon.setDias(rs.getInt("dias"));
					compon.setFecInicio(rs.getString("fec_inicio"));
					compon.setCapital(rs.getDouble("capital"));
					compon.setInteres(rs.getDouble("interes"));
					compon.setSaldoInsoluto(rs.getDouble("saldo_insoluto"));
					compon.setIdAmortizacion(rs.getInt("id_amortizacion"));
					compon.setFecVencimiento(rs.getString("fec_vencimiento"));
					compon.setTasaVigente(rs.getDouble("tasa_vigente"));
					compon.setTasaFija(rs.getDouble("tasa_fija"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:selectCapitales");
		}
		return list;
	}
	@Override
	public int actPagoAnt(String psContrato, int psDisposicion, String psFecVenc, int piIdAmortizacion,
			int piNoFolioAmort, String spEstatus, double pdImporteCap, String psFechaPago, int sCambiaInicio,
			double pdImpInteres, double pdSaldoInsoluto) {
		int resultado=0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n update cat_amortizacion_credito set ");
			sbSQL.append("\n saldo_insoluto = " + pdSaldoInsoluto);
			if (!spEstatus.equals(""))
				sbSQL.append("\n ,estatus='" + spEstatus + "'");
			if(pdImporteCap!= 0|| pdSaldoInsoluto == 0)
				sbSQL.append("\n , capital = " + pdImporteCap);
			if (pdImpInteres != 0 ||pdSaldoInsoluto == 0)
				sbSQL.append("\n , interes = " + pdImpInteres + " ");
			if(sCambiaInicio== 1)
				sbSQL.append("\n ,fec_inicio =  '" +psFechaPago + "' ");
			else if(sCambiaInicio == 2)
				sbSQL.append("\n , fec_vencimiento = " + psFechaPago + "', fec_pago ='" + psFechaPago + "' ");
			sbSQL.append("\n  WHERE  id_contrato = '" + psContrato + "' ");
			sbSQL.append("\n AND id_disposicion = " + psDisposicion);
			sbSQL.append("\n  AND fec_vencimiento =  '" +psFecVenc+"'");
			sbSQL.append("\n  AND id_amortizacion = " + piIdAmortizacion);
			sbSQL.append("\n  AND no_folio_amort = " + piNoFolioAmort);
			sbSQL.append("\n  AND estatus='A'");
			if (pdImporteCap!= 0)
				sbSQL.append("\n and capital > 0");
			if (pdImpInteres != 0)
				sbSQL.append("\n  and interes > 0");
			//System.out.println(sbSQL.toString());
			logger.info("actPagoAnt "+sbSQL);
			resultado = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:actPagoAnt");
		}
		return resultado;
	}
	public String cambiarOrdenFecha(String fecha) throws ParseException {
		DateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
		Date date = formato.parse(fecha);
		SimpleDateFormat formatoNuevo = new SimpleDateFormat("dd/MM/yyyy");
		String finalString = formatoNuevo.format(date);
		return finalString;
	}
	public String cambiarOrdenFecha2(String fecha) throws ParseException {
		DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		Date date = formato.parse(fecha);
		SimpleDateFormat formatoNuevo = new SimpleDateFormat("yyyy-MM-dd");
		String finalString = formatoNuevo.format(date);
		return finalString;
	}
	@Override
	public List<BeneficiarioDto> selectBeneficiario(String psNoPersona, String psDivisaPag) {
		StringBuffer sbSQL = new StringBuffer();
		List<BeneficiarioDto> list = new ArrayList<BeneficiarioDto>(); 
		try{
			sbSQL.append("\n SELECT coalesce(ctas.id_banco, 0) as id_banco, coalesce(ctas.id_chequera, '') as id_chequera, ");
			sbSQL.append("\n       p.equivale_persona, p.razon_social, p.no_persona ");
			sbSQL.append("\n FROM  persona p left join ctas_banco ctas on (p.no_persona = ctas.no_persona ");
			if(!psDivisaPag.equals(""))
				sbSQL.append("\n   And ctas.id_divisa = '" + psDivisaPag + "' ");
			sbSQL.append("\n ) ");
			sbSQL.append("\n WHERE p.equivale_persona = '" + psNoPersona + "' ");
			sbSQL.append("\n  AND p.id_tipo_persona = 'P' ");
		//	System.out.println(sbSQL.toString());
			logger.info("selectBeneficiario "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public BeneficiarioDto mapRow(ResultSet rs, int idx) throws SQLException {
					BeneficiarioDto compon = new BeneficiarioDto();
					compon.setBanco(rs.getString("id_banco"));
					compon.setChequera(rs.getString("id_chequera"));
					compon.setNoPersona(rs.getInt("no_persona"));
					compon.setProveedor(rs.getString("razon_social"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:selectBeneficiario");
		}
		return list;
	}
	@Override
	public Date obtenerFechaHoy() {
		Date fechaHoy;
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		fechaHoy = consultasGenerales.obtenerFechaHoy();
		return fechaHoy;
	}
	@Override
	public int updateFolioCupos(int usuario) {
		int resultado = 0;
		try {
			StringBuffer sbSQL = new StringBuffer();
			sbSQL.append("\n UPDATE cat_usuario ");
			sbSQL.append("\n    SET folio_control = folio_control + 1");
			sbSQL.append("\n  WHERE no_usuario = " + usuario);
			resultado = jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:updateFolioCuposl");
			e.printStackTrace();
		}
		return resultado;
	}
	@Override
	public int selectFolioCupos(int usuario) {
		StringBuffer sbSQL = new StringBuffer();
		int resultado=0;
		try {
			sbSQL.append("  SELECT folio_control ");
			sbSQL.append("  FROM cat_usuario ");
			sbSQL.append("  WHERE no_usuario = "+usuario);
			resultado=jdbcTemplate.queryForInt(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:selectFolioCupos");
			e.printStackTrace();
		}
		return resultado;
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
				sbSQL.append("\n                        left join valor_divisa vd on (cdc.id_divisa = vd.id_divisa and cast(vd.fec_divisa as date) = '" +fechaHoy+ "') ");
			sbSQL.append("\n  WHERE ccc.id_financiamiento = '" + psIdContrato + "' ");
		//	System.out.println(sbSQL.toString());
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
					compon.setIdBanco(rs.getInt("id_banco"));

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
	public List<AmortizacionCreditoDto> selectDivision(int piBanco, String psChequera) {
		StringBuffer sbSQL = new StringBuffer();
		List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>(); 
		try{
			sbSQL.append("\n SELECT coalesce(id_division, '') as id_division FROM cat_cta_banco ");
			sbSQL.append("\n WHERE id_banco = " + piBanco + " ");
			sbSQL.append("\n   And id_chequera = '" + psChequera + "' ");
			logger.info("selectDivision "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public AmortizacionCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					AmortizacionCreditoDto compon = new AmortizacionCreditoDto();
					compon.setDivision(rs.getString("id_division"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:selectDivision");
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
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:seleccionarFolioReal");
			e.printStackTrace();
		}
		return r;
	}
	@Override
	public int inserta1(String pvvEmpresa, int pvvFolio, int pvvTipoDocto, String pvvFormaPago, String pvvTipoOperacion,
			int pvvUsuario, String pvvNoCuenta, int pvvNoCliente, String pvvFecValor, String pvvFecOriginal,
			String pvvFecOperacion, String pvvFecAlta, double pvvImporte, double pvvImporteOriginal, int pvvIdCaja, String pvvIdDivisa,
			String pvvIdDivisaOriginal, String pvvOrigenReg, String pvvReferencia, String pvvConcepto, int pvvAplica,
			String pvvIdEstatusMov, String pvvBSalvoBC, String pvvIdEstatusReg, int pvvIdBanco, String pvvIdChequera,
			String pvvFolioBanco, String pvvOrigenMov, String pvvObservacion, int pvvIdInvCBolsa, int pvvNoFolioMov,
			int pvvFolioRef, int plFolioAnt, double pvvImporteDesglosado, int pvvLote, String pvvValor1, String pvvValor2,
			String sHoraRecibo, String plIdRubro, String psIdDivision, int psNoRecibo, int pvvGrupo, int pvvNoDocto,
			String pvvBeneficiario, int psCliente, int piBancoBenef, String psChequeraBenef, String psFecPropuesta, String psLinea) {
		int resultado = 0;
		try {
			StringBuffer sbSQL = new StringBuffer();
			sbSQL.append("\n INSERT INTO parametro(no_empresa, no_folio_param, id_tipo_docto, id_forma_pago, ");
			sbSQL.append("\n       id_tipo_operacion, usuario_alta, no_cuenta, fec_valor, fec_valor_original, ");
			sbSQL.append("\n       fec_operacion, fec_alta, importe, importe_original, id_caja, id_divisa, ");
			sbSQL.append("\n       id_divisa_original, origen_reg, referencia, concepto, aplica, id_estatus_mov, ");
			sbSQL.append("\n       b_salvo_buen_cobro, id_estatus_reg, id_banco, id_chequera, folio_banco, ");
			sbSQL.append("\n       origen_mov, observacion, id_inv_cbolsa, no_folio_mov, folio_ref, grupo, ");
			sbSQL.append("\n       importe_desglosado, lote, hora_recibo, division, id_rubro, no_recibo, ");
			sbSQL.append("\n       no_docto, beneficiario ");
			if(psCliente!=0||pvvNoCliente!=0)
				sbSQL.append("\n  , no_cliente ");
			//System.out.println(piBancoBenef+" "+psChequeraBenef);
			if(piBancoBenef!= 0&& !psChequeraBenef.equals(""))
				sbSQL.append("\n   , id_banco_benef, id_chequera_benef ");
			if(!psFecPropuesta.equals(""))
				sbSQL.append("\n   , fec_propuesta ");
			sbSQL.append("\n ) ");
			if(plIdRubro.equals(""))
				plIdRubro="0";
			sbSQL.append("\n VALUES(" + pvvEmpresa + "," + pvvFolio + "," + pvvTipoDocto + ", " + pvvFormaPago + " ");
			sbSQL.append("\n         , " + pvvTipoOperacion + ", " + pvvUsuario + ", " + pvvNoCuenta + ",convert(datetime,'"+pvvFecValor+"',103)");
			sbSQL.append("\n        ," + "convert(datetime,'"+pvvFecOriginal+"',103)" + "," + "convert(datetime,'"+ pvvFecOperacion+"',103)"+ "," +"convert(datetime,'"+pvvFecAlta+"',103)" + " ");
			sbSQL.append("\n         , " + pvvImporte + ", " + pvvImporteOriginal + ", " + pvvIdCaja + ", '" + pvvIdDivisa + "' ");
			sbSQL.append("\n       , '" + pvvIdDivisaOriginal + "', '" + pvvOrigenReg + "', '" + pvvReferencia + "', '" + pvvConcepto + "' ");
			sbSQL.append("\n         , " + pvvAplica + ", '" + pvvIdEstatusMov + "', '" + pvvBSalvoBC + "', '" + pvvIdEstatusReg + "' ");
			sbSQL.append("\n       , " + pvvIdBanco + ", '" + pvvIdChequera + "', '" + pvvFolioBanco + "', '" + pvvOrigenMov + "', '" + pvvObservacion + "' ");
			sbSQL.append("\n         , " + pvvIdInvCBolsa + ", " + pvvNoFolioMov + ", " + pvvFolioRef + ", " + pvvGrupo + ", " + pvvImporteDesglosado + " ");
			sbSQL.append("\n        ," + pvvLote + ", '" + sHoraRecibo + "', '" + psIdDivision + "', " + plIdRubro);
			sbSQL.append("\n        , " + psNoRecibo + ", '" + pvvNoDocto + "', '" + pvvBeneficiario + "' ");
			if(psCliente!=0)
				sbSQL.append("\n     , " + psCliente + " ");
			else if(pvvNoCliente!=0)
				sbSQL.append("\n     , " + pvvNoCliente + " ");
			if(piBancoBenef!=0&&!psChequeraBenef.equals(""))
				sbSQL.append("\n     , " + piBancoBenef + ", '" + psChequeraBenef + "' ");
			if(!psFecPropuesta.equals(""))
				sbSQL.append("\n     , '" +psFecPropuesta + "' ");
			sbSQL.append("\n ) ");
			logger.info("inserta1 "+sbSQL);
			//	System.out.println(sbSQL.toString());
			resultado = jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:inserta1");
			e.printStackTrace();
		}
		return resultado;
	}

	@Override
	public int inserta1Agrupado(String pvvEmpresa, int pvvFolio, int pvvTipoDocto, String pvvFormaPago, String pvvTipoOperacion,
			int pvvUsuario, String pvvNoCuenta, int pvvNoCliente, String pvvFecValor, String pvvFecOriginal,
			String pvvFecOperacion, String pvvFecAlta, double pvvImporte, double pvvImporteOriginal, int pvvIdCaja, String pvvIdDivisa,
			String pvvIdDivisaOriginal, String pvvOrigenReg, String pvvReferencia, String pvvConcepto, int pvvAplica,
			String pvvIdEstatusMov, String pvvBSalvoBC, String pvvIdEstatusReg, int pvvIdBanco, String pvvIdChequera,
			String pvvFolioBanco, String pvvOrigenMov, String pvvObservacion, int pvvIdInvCBolsa, int pvvNoFolioMov,
			String pvvFolioRef, int plFolioAnt, String pvvImporteDesglosado, int pvvLote, String pvvValor1, String pvvValor2,
			String sHoraRecibo, String plIdRubro, String psIdDivision, String psNoRecibo, int pvvGrupo, int pvvNoDocto,
			String pvvBeneficiario, int psCliente, int piBancoBenef, String psChequeraBenef, String psFecPropuesta, String psLinea,int noFactura
			,double txtTipoCambio,String idLeyenda, int grupo,int invoiceType,int idGrupo, int area, String division, int sucursal,int plaza) {
		int resultado = 0;
	//	System.out.println("pvvFolioRef"+ pvvFolioRef);
		try {
			StringBuffer sbSQL = new StringBuffer();
			sbSQL.append("\n INSERT INTO parametro(no_empresa, no_folio_param, id_tipo_docto, id_forma_pago, ");
			sbSQL.append("\n       id_tipo_operacion, usuario_alta, no_cuenta, fec_valor, fec_valor_original, ");
			sbSQL.append("\n       fec_operacion, fec_alta, importe, importe_original, id_caja, id_divisa, ");
			sbSQL.append("\n       id_divisa_original, referencia, concepto, aplica, id_estatus_mov, ");
			sbSQL.append("\n       b_salvo_buen_cobro, id_estatus_reg, id_banco, id_chequera,  ");
			sbSQL.append("\n       origen_mov, observacion, folio_ref, ");
			sbSQL.append("\n       importe_desglosado, lote, hora_recibo, id_rubro, no_recibo, ");
			sbSQL.append("\n       no_docto, beneficiario ");
			if(psCliente!=0||pvvNoCliente!=0)
				sbSQL.append("\n  , no_cliente ");
		//	System.out.println(piBancoBenef+" "+psChequeraBenef);
			sbSQL.append("\n   , id_banco_benef ");
			if(!psChequeraBenef.equals(""))
				sbSQL.append("\n    id_chequera_benef ");
			if(!psFecPropuesta.equals(""))
				sbSQL.append("\n   , fec_propuesta ");
			//Agrupado
			sbSQL.append("\n      , no_factura,tipo_cambio, id_leyenda,grupo, no_folio_mov, agrupa1, agrupa2, agrupa3,invoice_type, id_area");
			if(division!=null)
				sbSQL.append("\n  	,division");
			if(idGrupo!=0)
				sbSQL.append("\n  , id_grupo ");
			sbSQL.append("\n  , sucursal ");
			sbSQL.append("\n  , plaza ");
			sbSQL.append("\n ) ");
			if(plIdRubro.equals(""))
				plIdRubro="0";
			sbSQL.append("\n VALUES(" + pvvEmpresa + "," + pvvFolio + "," + pvvTipoDocto + ", " + pvvFormaPago + " ");
			sbSQL.append("\n         , " + pvvTipoOperacion + ", " + pvvUsuario + ", " + pvvNoCuenta + ", '" + funciones.cambiarOrdenFecha( pvvFecValor) + "'  ");
			sbSQL.append("\n        ,'" + funciones.cambiarOrdenFecha( pvvFecOriginal) + "','" + funciones.cambiarOrdenFecha( pvvFecOperacion) + "', '" +  funciones.cambiarOrdenFecha(pvvFecAlta) + "' ");
			sbSQL.append("\n         , " + pvvImporte + ", " + pvvImporteOriginal + ", " + pvvIdCaja + ", '" + pvvIdDivisa + "' ");
			sbSQL.append("\n       , '" + pvvIdDivisaOriginal + "', '" + pvvReferencia + "', '" + pvvConcepto + "' ");
			sbSQL.append("\n         , " + pvvAplica + ", '" + pvvIdEstatusMov + "', '" + pvvBSalvoBC + "', 'P' ");
			sbSQL.append("\n       , " + pvvIdBanco + ", '" + pvvIdChequera + "', '" + pvvOrigenMov + "', '" + pvvObservacion + "' ");
			sbSQL.append("\n         , " + pvvFolioRef + ", " + pvvImporteDesglosado + " ");
			sbSQL.append("\n        ," + pvvLote + ", '" + sHoraRecibo + "'," + plIdRubro);
			sbSQL.append("\n        , " + psNoRecibo + ", '" + pvvNoDocto + "', '" + pvvBeneficiario + "' ");
			if(psCliente!=0)
				sbSQL.append("\n     , " + psCliente + " ");
			else if(pvvNoCliente!=0)
				sbSQL.append("\n     , " + pvvNoCliente + " ");
			sbSQL.append("\n     , " + piBancoBenef );
			if(!psChequeraBenef.equals(""))
				sbSQL.append("\n     , " + piBancoBenef + ", '" + psChequeraBenef + "' ");
			if(!psFecPropuesta.equals(""))
				sbSQL.append("\n     , '" +psFecPropuesta + "' ");
			//Agrupado
			sbSQL.append("\n     , '" +noFactura + "' ,"+txtTipoCambio+",'"+idLeyenda+"'");
			sbSQL.append("\n     , " +grupo + ",1,0,0,0,'"+invoiceType+"',"+area);
			if(division!=null)
				sbSQL.append("\n  	,"+division+"");
			if(idGrupo!=0)
				sbSQL.append("\n     , " + idGrupo + " ");
			sbSQL.append("\n     , " + sucursal + " ");
			sbSQL.append("\n     , " + plaza + " ");
			sbSQL.append("\n ) ");
			logger.info("inserta1 "+sbSQL);
		//	System.out.println(sbSQL.toString());
			resultado = jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:inserta1");
			e.printStackTrace();
		}
		return resultado;
	}

	@Override
	public Map<String, Object> generador(GeneradorDto dto) {
		Map<String, Object> resultado = null;
		try{
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			resultado= consultasGenerales.ejecutarGenerador(dto);
		}catch(Exception e){
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:generador");
		}
		return resultado;
	}
	@Override
	public int updateMovCre(int pdFolioMov, int pdFolioDet, String clave, String fechaHoy, String contrato) {
		int resultado=0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  UPDATE  movimiento ");
			sbSQL.append("\n  SET     id_contable = 'KR', cve_control = '" + clave + "', ");
			sbSQL.append("\n          fec_propuesta = convert(date,'" + fechaHoy + "',103), no_factura = '" + contrato + "'  ");
			sbSQL.append("\n  WHERE   no_folio_mov = " + pdFolioMov + " ");
			logger.info("updateMovCre "+sbSQL);
		//	System.out.println(sbSQL.toString());
			resultado = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:updateMovCre");
		}
		return resultado;

	}
	@Override
	public int updateAmortizacion1(String idContrato, String idDisposicion, String idAmortizacion, String fecVen,
			String pvStatus, String clave, String vsTipoMenu) {
		int resultado=0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n UPDATE cat_amortizacion_credito");
			if(!pvStatus.equals("")){
				sbSQL.append("\n  SET estatus = '" + pvStatus + "', cve_control = '" + clave + "'");
				sbSQL.append("\n  WHERE id_contrato = '" + idContrato + "'");
				sbSQL.append("\n  AND id_disposicion = " +idDisposicion);
				sbSQL.append("\n AND estatus = 'A'");
				if (!vsTipoMenu.equals("A"))
					sbSQL.append("\n AND id_amortizacion = " +idAmortizacion);
				if(!fecVen.equals(""))
					if(gsDBM.equals("SQL SERVER"))
						sbSQL.append("\n  AND cast(fec_vencimiento as date) =convert(date, '" + fecVen + "',103)");
					else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
						sbSQL.append("\n  AND cast(fec_vencimiento as date) = '" + fecVen + "'");
			}
			logger.info("updateAmortizacion1 "+sbSQL);
			resultado = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:updateAmortizacion1");
		}
		return resultado;
	}
	@Override
	public List<AmortizacionCreditoDto> selectPagosVenc(String contrato, String disposicion) {
		StringBuffer sbSQL = new StringBuffer();
		List<AmortizacionCreditoDto> list = new ArrayList<AmortizacionCreditoDto>(); 
		try{
			sbSQL.append("\n  SELECT d.monto_disposicion, sum(c.capital) as total_capital ");
			sbSQL.append("\n  FROM   cat_amortizacion_credito c, cat_disposicion_credito d ");
			sbSQL.append("\n  WHERE c.id_contrato = '" +contrato+ "' ");
			sbSQL.append("\n    AND c.id_disposicion = "+disposicion+ " ");
			sbSQL.append("\n    AND c.id_contrato = d.id_financiamiento ");
			sbSQL.append("\n   AND c.id_disposicion = d.id_disposicion ");
			sbSQL.append("\n    AND c.estatus = 'P' ");
			sbSQL.append("\n   AND c.capital > 0 ");
			sbSQL.append("\n  GROUP BY d.monto_disposicion, c.capital ");
			logger.info("selectPagosVenc "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public AmortizacionCreditoDto mapRow(ResultSet rs, int idx) throws SQLException {
					AmortizacionCreditoDto compon = new AmortizacionCreditoDto();
					compon.setMontoDisposicion(rs.getDouble("monto_disposicion"));
					compon.setCapital(rs.getDouble("total_capital"));
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:selectPagosVenc");
		}
		return list;
	}
	@Override
	public void actualizaDisp(String pvIdContrato, String pvNoDisp, String pvEstatus) {
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  UPDATE cat_disposicion_credito ");
			sbSQL.append("\n  SET    estatus = '" + pvEstatus + "' ");
			sbSQL.append("\n  WHERE  id_financiamiento = '" + pvIdContrato + "' ");
			sbSQL.append("\n        AND id_disposicion = " + pvNoDisp + " ");
			jdbcTemplate.update(sbSQL.toString());
			logger.info("updateAmortizacion1 "+sbSQL);
		}catch(Exception e){
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:updateAmortizacion1");
		}

	}
	@Override
	public int insertaEncabezado(int dGrupo, String dEmpresa, String clave, String dFecha, double dTotal, String dFechaLim,
			int dUsuario) {
		int resultado=0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n INSERT INTO seleccion_automatica_grupo ");
			sbSQL.append("\n             (id_grupo, id_grupo_flujo, cve_control, fecha_propuesta, ");
			sbSQL.append("\n              monto_maximo, cupo_manual, cupo_automatico, cupo_total, ");
			sbSQL.append("\n              fec_limite_selecc");
			if(dUsuario>0)
				sbSQL.append("\n          , usuario_uno");
			sbSQL.append("\n          )");
			sbSQL.append("\n  VALUES (" + dGrupo + "," + dEmpresa + ",'" + clave + "',convert(date, '" + dFecha + "',103),");
			sbSQL.append("\n"+ dTotal + "," + dTotal + "," + dTotal + "," + dTotal + ",convert(date, '" + dFechaLim + "',103)");
			if(dUsuario!=0)
				sbSQL.append("\n, '" + dUsuario + "'");
			sbSQL.append("\n )");
			logger.info("insertaEncabezado "+sbSQL);
			resultado = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:insertaEncabezado");
		}
		return resultado;


	}
	@Override
	public void updateMovtosFecPropuesta(String vsFechaPropuesta, String clave,String concepto) {
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n UPDATE movimiento ");
			sbSQL.append("\n SET fec_propuesta = convert(date, '" + vsFechaPropuesta + "',103) ");
			sbSQL.append("\n WHERE cve_control = '" + clave + "' ");
			//System.out.println(sbSQL.toString());
			jdbcTemplate.update(sbSQL.toString());
			sbSQL = new StringBuffer();	
			sbSQL.append("\n UPDATE seleccion_automatica_grupo ");
			sbSQL.append("\n SET concepto = '" + concepto + "' ");
			sbSQL.append("\n WHERE cve_control = '" + clave + "' ");
			//System.out.println(sbSQL.toString());
			jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:updateMovtosFecPropuesta");
		}
	}

	@Override
	public int obtenerGrupoFlujo(int empresa) {
		int idGrupoFlujo = 0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("SELECT ID_GRUPO_FLUJO FROM GRUPO_EMPRESA WHERE NO_EMPRESA = " + empresa);
			idGrupoFlujo = jdbcTemplate.queryForInt(sbSQL.toString());
		}catch(Exception e){
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:obtenerGrupoFlujo");
		}

		return idGrupoFlujo;
	}

	@Override
	public int updateAmortizaCapital(AmortizacionCreditoDto dto){
		int resultado = 0;
		try {
			StringBuffer sbSQL = new StringBuffer();

			sbSQL.append("\nUPDATE cat_amortizacion_credito SET estatus = 'X' ");
			sbSQL.append("\n WHERE id_contrato = '"+dto.getIdFinanciamiento()+"' ");
			sbSQL.append("\n   And id_disposicion = " +dto.getIdDisposicion()+" ");
			sbSQL.append("\n  And (capital <> 0 or interes <> 0) ");
			sbSQL.append("\n   And (gasto = 0 and comision = 0) ");
			sbSQL.append("\n   And estatus = 'A' ");
			sbSQL.append("\n   And id_amortizacion <> " +dto.getIdAmortizacion()+ " ");
			resultado = jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:updateAmortizaCapital");
			e.printStackTrace();
		}
		return resultado;
	}
	public String consultarConfiguraSet(int indice) {
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT valor ");
			sb.append("\n FROM configura_set");
			sb.append("\n WHERE indice = " + indice);
			List<String> valores = jdbcTemplate.query(sb.toString(),
					new RowMapper<String>() {
				public String mapRow(ResultSet rs, int idx)
						throws SQLException {
					String valor = rs.getString("valor");
					return valor;
				}
			});
			return valores.isEmpty()?"":valores.get(0);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:consultarConfiguraSet");
			e.printStackTrace();
			return "";
		}
	}
	@Override
	public List<CorreoVencimientoDto> selectDestinatarios() {
		StringBuffer sbSQL = new StringBuffer();
		List<CorreoVencimientoDto> list = new ArrayList<CorreoVencimientoDto>(); 
		try{
			sbSQL.append("\n select * from control_correo");
			logger.info("selectDestinatarios "+sbSQL);
			System.out.println(sbSQL.toString());
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public CorreoVencimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					CorreoVencimientoDto dto = new CorreoVencimientoDto();
					dto.setUsuario(rs.getInt("usuario"));
					dto.setEmail(rs.getString("email"));
					dto.setGpoEmpresaVenc(rs.getInt("id_gpo_emp_venc"));
					dto.setPeriodo(rs.getInt("periodo"));
					return dto;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M: selectDestinatarios");
		}
		return list;
	}
	@Override
	public void updateControlCorreo(String asunto, int usuario, String email){
		int resultado = 0;
		try {
			StringBuffer sbSQL = new StringBuffer();
			sbSQL.append("\nUPDATE control_correo SET motivo_envio = '"+asunto+"' ");
			sbSQL.append("\n where usuario="+usuario);
			sbSQL.append("\n and email='"+email+"'");
			logger.info("updateControlCorreo "+sbSQL);
		//	System.out.println(sbSQL.toString());
			resultado = jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M: updateControlCorreo");
			e.printStackTrace();
		}
	}
	public List<Map<String, String>> funSQLDatosCorreo(int noCorreo) {
		StringBuffer sql= new StringBuffer();
		List<Map<String, String>> listConsMovi  = new ArrayList<Map<String, String>>();
		try{
			sql.append("\n SELECT parrafo_1,  parrafo_2,  parrafo_3,  parrafo_4,  parrafo_5,");
			sql.append("\n parrafo_6,  parrafo_7,  parrafo_8,  parrafo_9,  parrafo_10,");
			sql.append("\n parrafo_11, parrafo_12, parrafo_13, parrafo_14, parrafo_15,");
			sql.append("\n parrafo_16, parrafo_17, parrafo_18, parrafo_19, parrafo_20,");
			sql.append("\n parrafo_21, parrafo_22, parrafo_23, parrafo_24, parrafo_25,");
			sql.append("\n parrafo_26");
			sql.append("\n From configura_mail");
			sql.append("\n WHERE no_correo = "+noCorreo);
		//	System.out.println(sql.toString());
			listConsMovi = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>(){
				public Map<String, String> mapRow(ResultSet rs, int idx)throws SQLException{
					Map<String, String> map = new HashMap<String, String>();
					map.put("parrafo_1", rs.getString("parrafo_1"));
					map.put("parrafo_2", rs.getString("parrafo_2"));
					map.put("parrafo_3", rs.getString("parrafo_3"));
					map.put("parrafo_4", rs.getString("parrafo_4"));
					map.put("parrafo_5", rs.getString("parrafo_5"));
					map.put("parrafo_6", rs.getString("parrafo_6"));
					map.put("parrafo_7", rs.getString("parrafo_7"));
					map.put("parrafo_8", rs.getString("parrafo_8"));
					map.put("parrafo_9", rs.getString("parrafo_9"));
					map.put("parrafo_10", rs.getString("parrafo_10"));
					map.put("parrafo_11", rs.getString("parrafo_11"));
					map.put("parrafo_12", rs.getString("parrafo_12"));
					map.put("parrafo_13",rs.getString("parrafo_13"));
					map.put("parrafo_14", rs.getString("parrafo_14"));
					map.put("parrafo_15", rs.getString("parrafo_15"));
					map.put("parrafo_16", rs.getString("parrafo_16"));
					map.put("parrafo_17", rs.getString("parrafo_17"));
					map.put("parrafo_18", rs.getString("parrafo_18"));
					map.put("parrafo_19", rs.getString("parrafo_19"));
					map.put("parrafo_20", rs.getString("parrafo_20"));
					map.put("parrafo_21", rs.getString("parrafo_21"));
					map.put("parrafo_22", rs.getString("parrafo_22"));
					map.put("parrafo_23", rs.getString("parrafo_23"));
					map.put("parrafo_24", rs.getString("parrafo_24"));
					map.put("parrafo_25",rs.getString("parrafo_25"));
					map.put("parrafo_26", rs.getString("parrafo_26"));
					return map;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:CorreoDaoImpl, M:funSQLDatosCorreo");
			e.printStackTrace();
		}return listConsMovi;
	}
	@Override
	public List<ControlPagosPasivos> obtenerVencimientos(int empresa, int piBanco, String psLinea,String psContrato, String psAnexo) {
		List<ControlPagosPasivos> listDisp= new ArrayList<ControlPagosPasivos>();
		List<ControlPagosPasivos> list= new ArrayList<ControlPagosPasivos>();
		DecimalFormat formato = new DecimalFormat("###,###,###,##0.00");
		StringBuffer sbSQL = new StringBuffer();

		String psFecha="";
		try{
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			Date fechaHoy = consultasGenerales.obtenerFechaHoy();
			Calendar calendar = Calendar.getInstance();
			int plWeekend=0;
			calendar.setTime(fechaHoy);
			//La fecha actual mas 10 días
			calendar.add(Calendar.DAY_OF_YEAR, 10);
			if(Calendar.DAY_OF_WEEK==1)
				plWeekend=1;
			else if(Calendar.DAY_OF_WEEK==7)
				plWeekend=1;
			else
				plWeekend=0;
			calendar.add(Calendar.DAY_OF_YEAR, plWeekend);
			psFecha=fechaHabil(funciones.fechaString(calendar.getTime()));

			sbSQL.append("\n SELECT  cac.fec_vencimiento as fecha, cac.fec_inicio as fechaInicio, DATEDIFF(dd,cac.fec_inicio,cac.fec_pago) as plazo,cdc.valor_tasa,ccc.no_empresa, e.nom_empresa, ccc.id_banco_prestamo, ");
			sbSQL.append("\nCASE WHEN DATEDIFF(dd,'2017-07-25',cac.fec_vencimiento)<=5 then 'S'");
			sbSQL.append("\n		else 'N' end as color,");
			sbSQL.append("\n        cdc.id_divisa,  cac.saldo_insoluto, cac.id_disposicion, ccc.id_financiamiento, ccc.id_tipo_financiamiento, ctc.descripcion, ");
			sbSQL.append("\n        cdc.monto_disposicion, (cdc.monto_disposicion - coalesce((SELECT sum(cac2.capital) ");
			sbSQL.append("\n        FROM cat_amortizacion_credito cac2 WHERE cac2.estatus = 'P' And cac2.id_contrato = cdc.id_financiamiento ");
			sbSQL.append("\n       And cac2.id_disposicion = cdc.id_disposicion),0)) as saldo_actual, cac.capital, cac.interes, cdc.renta, ");
			sbSQL.append("\n        cast(case when cdc.renta = 0 then coalesce((cac.interes * (cac.iva/100)),0) else coalesce((cdc.renta * (cac.iva/100)),0) end as numeric(15,2)) as iva,");
			sbSQL.append("\n         cast(case when cac.id_amortizacion = 0 then ");
			sbSQL.append("\n        COALESCE((cac.capital + cac.interes + case when cdc.renta = 0 then ");
			sbSQL.append("\n         (cac.interes * (cac.iva/100)) else (cdc.renta * (cac.iva/100)) end), 0) ");
			sbSQL.append("\n         else coalesce((cac.capital + cac.interes+cast(case when cdc.renta = 0 then coalesce((cac.interes * (cac.iva/100)),0) else coalesce((cdc.renta * (cac.iva/100)),0) end as numeric(15,2))), 0) end as numeric(15,2)) as pago_total, cac.id_amortizacion, ctc.financiamiento ");
			if(psTipoMenu.equals("A")) 
				sbSQL.append("\n   , ca.desc_arrendadora as desc_banco ");
			else
				sbSQL.append("\n      , cb.desc_banco ");
			sbSQL.append("\n  FROM    cat_amortizacion_credito cac, cat_contrato_credito ccc, ");
			sbSQL.append("\n          empresa e, cat_tipo_contrato ctc, cat_disposicion_credito cdc ");
			if(psTipoMenu.equals("A")) 
				sbSQL.append("\n    , cat_arrendadoras ca ");
			else
				sbSQL.append("\n     , cat_banco cb ");
			sbSQL.append("\nWHERE   ccc.no_empresa="+empresa);
			sbSQL.append("\n     and cac.estatus = 'A' ");
			sbSQL.append("\n     and cac.id_contrato = ccc.id_financiamiento ");
			sbSQL.append("\n    and cac.id_contrato = cdc.id_financiamiento ");
			sbSQL.append("\n    and cac.id_disposicion = cdc.id_disposicion ");
			sbSQL.append("\n    and ctc.id_tipo_contrato = ccc.id_tipo_financiamiento ");
			sbSQL.append("\n    and ccc.no_empresa = e.no_empresa ");
			if(psTipoMenu.equals("A")) 
				sbSQL.append("\n    and ccc.id_banco_prestamo = ca.id_arrendadora ");
			else
				sbSQL.append("\n    and ccc.id_banco_prestamo = cb.id_banco ");
			if(psTipoMenu.equals("")) 
				sbSQL.append("\n    and ctc.financiamiento in ('"  +psTipoMenu + "') ");
			else
				sbSQL.append("\n    and ctc.financiamiento = '" + psTipoMenu + "' ");
			if (piBanco!= 0)
				sbSQL.append("\n and ccc.id_banco_prestamo = " + piBanco + " ");
			if(!psLinea.equals("TODOS")&&!psLinea.equals(""))
				sbSQL.append("\nand ccc.id_financiamiento = '" + psLinea + "' ");
			if(!psContrato.equals("TODOS")&&!psContrato.equals(""))
				sbSQL.append("\n and cdc.id_disposicion = '" + psContrato + "' ");
			if(!psAnexo.equals("")) 
				sbSQL.append("\nand cdc.anexo= '" + psAnexo + "' ");
			if(gsDBM.equals("SQL SERVER")){
				sbSQL.append("\n    and cast(cac.fec_vencimiento as date) >= '" + fechaHoy + "' ");
				sbSQL.append("\n    and cast(cac.fec_vencimiento as date) <= '" + psFecha + "' ");
				sbSQL.append("\n ORDER BY cast(cac.fec_vencimiento as date), pago_total DESC,cac.id_contrato ");}
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE")){
				sbSQL.append("\n    and cast(cac.fec_vencimiento as date) >= '" + fechaHoy + "' ");
				sbSQL.append("\n    and cast(cac.fec_vencimiento as date) <= '" + psFecha + "' ");
				sbSQL.append("\nORDER BY cast(cac.fec_vencimiento as date), ");
			}
			System.out.println(sbSQL.toString());
			listDisp = jdbcTemplate.query(sbSQL.toString(), new RowMapper()	{
				@Override
				public ControlPagosPasivos mapRow(ResultSet rs, int idx)throws SQLException{
					ControlPagosPasivos reporte = new ControlPagosPasivos();
					reporte.setFecha(rs.getString("fecha"));
					reporte.setFechaInicio(rs.getString("fechaInicio"));
					reporte.setPlazo(rs.getInt("plazo"));
					reporte.setValorTasa(rs.getDouble("valor_tasa"));
					reporte.setDivisa(rs.getString("id_divisa"));
					reporte.setSaldoInsoluto(rs.getDouble("saldo_insoluto"));
					reporte.setColor(rs.getString("color").charAt(0));
					reporte.setNo_empresa(rs.getInt("no_empresa"));
					reporte.setEmpresa(rs.getString("nom_empresa"));
					reporte.setBanco_prestamo(rs.getInt("id_banco_prestamo"));
					reporte.setInstitucion(rs.getString("desc_banco"));
					reporte.setDisposicion(rs.getInt("id_disposicion"));
					reporte.setId_financiamiento(rs.getString("id_financiamiento"));
					reporte.setId_tipo_financiamiento(rs.getInt("id_tipo_financiamiento"));
					reporte.setTipoFin( rs.getString("descripcion"));
					reporte.setMontoCred(rs.getString("monto_disposicion"));
					reporte.setSaldoActual(rs.getString("saldo_actual"));
					reporte.setPagoCap(rs.getString("capital"));
					reporte.setInteres(rs.getString("interes"));
					reporte.setRenta(rs.getString("renta"));
					reporte.setIva(rs.getString("iva"));
					reporte.setPagoTotal(rs.getString("pago_total"));
					reporte.setId_amortizacion(rs.getInt("id_amortizacion"));
					reporte.setFinanciamiento(rs.getString("id_financiamiento"));
					reporte.setDesc_banco1(rs.getString("desc_banco"));
					reporte.setDesc_banco2(rs.getString("desc_banco"));
					reporte.setDesc_banco3(rs.getString("desc_banco"));

					return reporte;


				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: FinanciamientoModificacionC, C: FinanciamientoModificacionCDaoImpl,M: consultarGrid");
		}

		return listDisp;
	}
	@Override
	public String fechaHabil(String fecha) {

		List <ProvisionCreditoDTO> result=new ArrayList<ProvisionCreditoDTO>();
		String diaHabil="";
		Calendar calendar;
		try {
			while (diaHabil.equals("")){
				result = selectInhabil(fecha);
				if(result.isEmpty()){
					diaHabil=fecha;
				}
				else{
					calendar=Calendar.getInstance();
					calendar.setTime(funciones.ponerFechaDate(fecha)); 
					calendar.add(Calendar.DAY_OF_YEAR, -1);
					fecha=funciones.ponerFecha(calendar.getTime());
					diaHabil="";
				}
			}

		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:ProvisionInteresesCBusinessImpl, M:diaHabilReg");
		}
		return diaHabil;
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
				sbSQL.append("\n WHERE cast(FEC_INHABIL as date) = '");
				sbSQL.append(pvFechaInhabil+"'");
			}
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE")){
				sbSQL.append("\nWHERE cast(FEC_INHABIL as date) = '");
				sbSQL.append(pvFechaInhabil+"'");
			}
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
	public List<CorreoVencimientoDto> obtenerEmpresasUsuario(int usuario) {
		StringBuffer sbSQL = new StringBuffer();
		List<CorreoVencimientoDto> list = new ArrayList<CorreoVencimientoDto>(); 
		try{
			sbSQL.append("\n select gev.no_empresa,razon_social as nomEmpresa from grupo_emp_venc gev join control_correo cc on");
			sbSQL.append("\n    gev.id_gpo_emp_venc=cc.id_gpo_emp_venc join persona p on gev.no_empresa=p.no_persona where p.id_tipo_persona='E' and cc.usuario="+usuario);
			logger.info("obtenerEmpresasUsuario "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public CorreoVencimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					CorreoVencimientoDto dto = new CorreoVencimientoDto();
					dto.setNoEmpresa(rs.getInt("no_empresa"));
					dto.setNomEmpresa(rs.getString("nomEmpresa"));
					return dto;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M: obtenerEmpresasUsuario");
		}
		return list;
	}
	@Override
	public int actualizaImporteParametro(double vdImporte, int grupo) {
		int resultado = 0;
		try {
			StringBuffer sbSQL = new StringBuffer();
			sbSQL.append("\n update parametro ");
			sbSQL.append("\n set importe="+vdImporte+", importe_original="+vdImporte);
			sbSQL.append("\n where id_estatus_mov='C' ");
			sbSQL.append("\n and no_folio_param="+grupo);
			sbSQL.append("\n and id_tipo_operacion=3000 and origen_mov='CRD'");
			resultado = jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCDaoImpl, M:updateAmortizaCapital");
			e.printStackTrace();
		}
		return resultado;
	}

}
