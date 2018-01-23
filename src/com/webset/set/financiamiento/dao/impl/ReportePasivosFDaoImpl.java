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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.coinversion.dao.impl.CoinversionDaoImpl;
import com.webset.set.financiamiento.dao.ReportePasivosFDao;
import com.webset.set.financiamiento.dto.AvalGarantiaDto;
import com.webset.set.financiamiento.dto.ReportePasivosFDto;
import com.webset.set.global.dao.GlobalDao;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.ConstantesSet;

public class ReportePasivosFDaoImpl implements ReportePasivosFDao {
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
	public List<LlenaComboGralDto> obtenerEmpresas(int plUsuario,String psMenu) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n SELECT distinct e.no_empresa as ID, e.nom_empresa as Descrip ");
			sbSQL.append("\n FROM   empresa e, cat_contrato_credito ccc, cat_tipo_contrato ctc");
			sbSQL.append("\n WHERE  e.no_empresa = ccc.no_empresa ");
			sbSQL.append("\n       And ccc.id_tipo_financiamiento = ctc.id_tipo_contrato");
			sbSQL.append("\n       And ctc.financiamiento in ('" + psMenu + "')");
			sbSQL.append("\n       And ccc.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " + plUsuario + ") ");
			sbSQL.append("\n ORDER BY ID ");
			System.out.println(sbSQL.toString());
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
			+ "P:Financiamiento, C:ReportePasivosFDaoImpl, M:obtenerEmpresas");
		}
		return list;
	}
	
	public String ponerFormatoFecha(String fechaString){
		return fechaString.substring(6,10)+"-"+fechaString.substring(3,5)+"-"+fechaString.substring(0,2);
	
	}
	public String ponerFormatoFecha2(String fechaString){
		return fechaString.substring(8,10)+"/"+fechaString.substring(5,7)+"/"+fechaString.substring(0,4);
	}
	@Override
	public List<ReportePasivosFDto> obtenerDivisaCreditos(int plUsuario, String psMenu) {
		List<ReportePasivosFDto> list = new ArrayList<ReportePasivosFDto>();
		StringBuffer sbSQL = new StringBuffer();
		try {
			sbSQL.append("\n SELECT distinct cdc.id_divisa, cd.desc_divisa ");
			sbSQL.append("\n FROM  cat_disposicion_credito cdc, cat_divisa cd, cat_tipo_contrato ctc, ");
			sbSQL.append("\n       cat_contrato_credito ccc, empresa e");
			sbSQL.append("\n WHERE cdc.estatus = 'A' ");
			sbSQL.append("\n   And cdc.id_divisa = cd.id_divisa ");
			sbSQL.append("\n   And ccc.id_tipo_financiamiento = ctc.id_tipo_contrato ");
			sbSQL.append("\n   And cdc.id_financiamiento = ccc.id_financiamiento ");
			sbSQL.append("\n   And ccc.no_empresa = e.no_empresa ");
			sbSQL.append("\n   And cd.id_divisa <> 'MN' ");
			sbSQL.append("\n   And ctc.financiamiento in ('" + psMenu + "') ");
			sbSQL.append("\n   And e.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " + plUsuario + ") ");
			sbSQL.append("\n ORDER BY cdc.id_divisa ");
			logger.info("obtenerDivisaCreditos "+sbSQL);
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper() {
				@Override
				public ReportePasivosFDto mapRow(ResultSet rs, int idx) throws SQLException {
					ReportePasivosFDto compon = new ReportePasivosFDto();
					compon.setIdDivisa(rs.getString("id_divisa"));
					
					return compon;
				}
			});
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:ReportePasivosFDaoImpl, M:obtenerEmpresas");
		}
		return list;
	}
	@Override
	public List<ReportePasivosFDto> obtenerPasivosFinancieros(int noEmpresa,  String json) {
		List<ReportePasivosFDto> registros= new ArrayList<ReportePasivosFDto>();
		StringBuffer sbSQL = new StringBuffer();
		Gson gson = new Gson();
		List<Map<String, String>> param = gson.fromJson(json,
				new TypeToken<ArrayList<Map<String, String>>>() {
		}.getType());
		String vsFecFin;
		DecimalFormat formato = new DecimalFormat("###,###,###,##0.00");
		DecimalFormat formato2 = new DecimalFormat("###,##0.00000");
		double vsTipoCambio=0;
		String vsRevolvente = "",vsDivisa="",viGrupo="0";
		try {
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			Date fechaHoy = consultasGenerales.obtenerFechaHoy();
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(fechaHoy);
			int anio = calendar.get(Calendar.YEAR);
			calendar.add(Calendar.YEAR, 1);
			vsFecFin = df.format(calendar.getTime());
			globalSingleton = GlobalSingleton.getInstancia();
			sbSQL.append("\n SELECT cdc.comentarios, cdr.descripcion,ccc.revolvencia, ccc.id_pais, cp.desc_pais, e.no_empresa, e.nom_empresa, coalesce(st.soiemp, '') as soiemp, cgf.id_grupo_flujo, cgf.desc_grupo_flujo, ");
			sbSQL.append("\n   case when ccc.revolvencia = 'S' then 'REVOLVENTE' else 'NO REVOLVENTE' end as revolvencia, ");
			sbSQL.append("\n   case when ccc.largo_plazo = 'S' then 'LARGO PLAZO' else 'CORTO PLAZO' end as cl_plazo,");
			sbSQL.append("\n   ccc.id_divisa , cd.desc_divisa, ccc.id_financiamiento, coalesce(cdc.id_disposicion, 0) as id_disposicion, ccc.id_banco_prestamo, ");
			sbSQL.append("\n   coalesce(cast(sum(cac.capital) as numeric(15,0)), 0) as capital, ");
			sbSQL.append("\n d.desc_division,  ctc.descripcion as tipoUso,");
			if (psTipoMenu.equals("A"))
				sbSQL.append("\n  ca.desc_arrendadora, ");
			else
				sbSQL.append("\n   cb.desc_banco as desc_arrendadora, ");
			if(gsDBM.equals("SQL SERVER")){
				sbSQL.append("\n   coalesce(case when cdc.tipo_tasa = 'V' then cdc.tasa_base + ' + ' + convert(char, cast(cdc.puntos as numeric(15,5))) end, '') as tasa_ref, ");
				sbSQL.append("\n   coalesce(datediff(day, cast(cdc.fec_disposicion as date), cast(cdc.fec_vencimiento as date)), 0) as plazo,");
			}
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE")){
				sbSQL.append("\n   coalesce(case when cdc.tipo_tasa = 'V' then cdc.tasa_base || ' + ' || cdc.puntos end, '') as tasa_ref, ");
				sbSQL.append("\n   coalesce((cast(cdc.fec_vencimiento as date) - cast(cdc.fec_disposicion as date)), 0) as plazo,");
			}
			sbSQL.append("\n  coalesce(cdc.valor_tasa, 0) as valor_tasa, cdc.fec_disposicion, cdc.fec_vencimiento, ccc.monto_autorizado, ");
			sbSQL.append("\n (select cast(coalesce(sum(cac1.capital), 0) as numeric(15,0)) from cat_amortizacion_credito cac1 where cac1.id_contrato = ccc.id_financiamiento and cac1.id_disposicion = cdc.id_disposicion");
			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append("\n  and cast(fec_vencimiento as date) between '"  +fechaHoy+  "' and '" + ponerFormatoFecha(vsFecFin) + "' and estatus = 'A') as monto_cplazo,");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\n  and cast(fec_vencimiento as date) between '" + fechaHoy+  "' and '" + ponerFormatoFecha(vsFecFin) + "' and estatus = 'A') as monto_cplazo,");
			sbSQL.append("\n (select cast(coalesce(sum(cac1.capital), 0) as numeric(15,0)) from cat_amortizacion_credito cac1 where cac1.id_contrato = ccc.id_financiamiento and cac1.id_disposicion = cdc.id_disposicion");
			if(gsDBM.equals("SQL SERVER"))
				sbSQL.append("\n  and cast(fec_vencimiento as date) > '" + ponerFormatoFecha(vsFecFin) + "' and estatus = 'A') as monto_lplazo, ");
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE"))
				sbSQL.append("\n  and cast(fec_vencimiento as date) > '" + ponerFormatoFecha(vsFecFin) + "' and estatus = 'A') as monto_lplazo, ");
			sbSQL.append("\n   coalesce(case when (select distinct cac1.periodo from cat_amortizacion_credito cac1 where cac1.id_contrato = ccc.id_financiamiento and cac1.id_disposicion = cdc.id_disposicion ");
			sbSQL.append("\n    and estatus = 'A' and cac1.capital > 0) = 'UNIVERSAL' then 'AMORTIZACIONES' else 'UNICA' end, '') as periodicidad, ");
			if(gsDBM.equals("SQL SERVER")){
				sbSQL.append("\n   coalesce((select top 1 coalesce(cac1.interes, 0) from cat_amortizacion_credito cac1 where cac1.id_contrato = ccc.id_financiamiento and cac1.id_disposicion = cdc.id_disposicion ");
				sbSQL.append("\n    and interes > 0 and estatus = 'A' order by cast(fec_vencimiento as date)), 0) as interes ");
			}
			else if(gsDBM.equals("POSTGRESQL")||gsDBM.equals("ORACLE")){
				sbSQL.append("\n   coalesce((select coalesce(cac1.interes, 0) from cat_amortizacion_credito cac1 where cac1.id_contrato = ccc.id_financiamiento and cac1.id_disposicion = cdc.id_disposicion ");
				sbSQL.append("\n    and interes > 0 and estatus = 'A' order by cast(fec_vencimiento as date) limit 1), 0) as interes ");
			}
			sbSQL.append("\n FROM  cat_contrato_credito ccc, cat_disposicion_credito cdc left join cat_amortizacion_credito cac on ");
			sbSQL.append("\n       (cdc.id_financiamiento = cac.id_contrato And cdc.id_disposicion = cac.id_disposicion And cac.estatus = 'A'), ");
			sbSQL.append("\n       cat_pais cp, empresa e left join SET006 st on (e.no_empresa = setemp and siscod in ('CP', 'CO')), usuario_empresa ue, grupo_empresa ge, ");
			sbSQL.append("\n       cat_grupo_flujo cgf, cat_divisa cd, cat_tipo_contrato ctc,cat_destino_recurso cdr, ");
			sbSQL.append("\n cat_division d, ");
			if (psTipoMenu.equals("A"))
				sbSQL.append("\n   cat_arrendadoras ca ");
			else
				sbSQL.append("\n   cat_banco cb ");
			sbSQL.append("\n WHERE ccc.id_financiamiento = cdc.id_financiamiento And ccc.id_pais = cp.id_pais ");
			sbSQL.append("\n   and ccc.id_divisa = cd.id_divisa ");
			sbSQL.append("\n   and ccc.no_empresa = e.no_empresa ");
			sbSQL.append("\n  and e.no_empresa = ue.no_empresa ");
			sbSQL.append("\n   and e.no_empresa = ge.no_empresa ");
			sbSQL.append("\n   and ge.id_grupo_flujo = cgf.id_grupo_flujo ");
			if (psTipoMenu.equals("A"))
				sbSQL.append("\n   and ccc.id_banco_prestamo = ca.id_arrendadora ");
			else
				sbSQL.append("\n   and ccc.id_banco_prestamo = cb.id_banco ");
			sbSQL.append("\n   and ccc.id_tipo_financiamiento = ctc.id_tipo_contrato ");
			sbSQL.append("\n   and ctc.financiamiento in ('" + psTipoMenu + "') ");
			sbSQL.append("\n   and ue.no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + " ");
			sbSQL.append("\n   and ccc.estatus = 'A'");
			sbSQL.append("\n   and cdc.estatus = 'A'");
			sbSQL.append("\n   and cdc.id_destino_recursos=cdr.id_destino_recurso");
			sbSQL.append("\n   and d.no_empresa=ccc.no_empresa");
			if(noEmpresa!= 0) 
				sbSQL.append("\n   and ccc.no_empresa = " + noEmpresa + " ");
			sbSQL.append("\n GROUP BY ccc.id_pais, cp.desc_pais, e.no_empresa, e.nom_empresa, st.soiemp, cgf.id_grupo_flujo, cgf.desc_grupo_flujo, ccc.revolvencia, ");
			sbSQL.append("\n         ccc.largo_plazo, ccc.id_divisa, cd.desc_divisa, ccc.id_financiamiento, cdc.id_disposicion, ");
			sbSQL.append("\n         ccc.id_banco_prestamo, cdc.tipo_tasa, cdc.tasa_base, cdc.puntos, ");
			sbSQL.append("\n         cdc.valor_tasa, cdc.fec_disposicion , cdc.fec_vencimiento, ccc.monto_autorizado, cdr.descripcion,");
			sbSQL.append("\n 		 d.desc_division, ctc.descripcion,cdc.comentarios,");
			if (psTipoMenu.equals("A"))
				sbSQL.append("\n   ca.desc_arrendadora ");
			else
				sbSQL.append("\n   cb.desc_banco ");
			sbSQL.append("\n ORDER BY st.soiemp, ccc.largo_plazo, ccc.id_divisa, ccc.revolvencia, ccc.id_financiamiento");
			System.out.println(sbSQL.toString());
			logger.info("obtenerPasivosFinancieros "+sbSQL);
			int j=0;
			List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sbSQL.toString());
			int posicion=0;
			double totalDivisa=0,totalRevolvente=0,totalEmpresa=0,totalDivisaMontoCP=0,totalRevolventeMontoCP=0,totalEmpresaMontoCP=0,totalDivisaInteres=0,totalRevolventeInteres=0,totalEmpresaInteres=0,totalDivisaMontoLP=0,totalRevolventeMontoLP=0,totalEmpresaMontoLP=0,totalDivisaLA=0,totalDivisaLD=0,totalRevolventeLA=0,totalRevolventeLD=0,totalEmpresaLA=0,totalEmpresaLD=0;
			for (Map row : rows) {
				double lineaDisp=0,lineaAutorizada=0;
				String divisa=(String)row.get("id_divisa");
				if(!divisa.equals("MN")){
					for (int i = 0; i < param.size(); i++) {
						if(param.get(i).get("idDivisa").equals(row.get("id_Divisa"))){
							vsTipoCambio=Double.parseDouble(param.get(i).get("valorDivisa"));
						}
					}
				}
				else{
					vsTipoCambio = 1;
				}
				if (!viGrupo.equals(row.get("no_empresa").toString())|| !vsRevolvente.equals(row.get("cl_plazo")) || !vsDivisa.equals(row.get("id_divisa"))) {
					ReportePasivosFDto dto = new ReportePasivosFDto();
					dto.setNoBanco((String)row.get("id_pais"));
					dto.setBanco((String)row.get("desc_pais")+" - "+(String)row.get("nom_empresa")+" - "+row.get("desc_division").toString());
					dto.setGrupo(row.get("no_empresa").toString());
					dto.setImporte((String)row.get("cl_plazo"));
					dto.setTasaRef((String)row.get("desc_divisa"));
					dto.setDivisa((String)row.get("id_divisa"));
					dto.setLinea("");
					dto.setIdDisposicion("");
					dto.setColor("color:#165ACB");
					viGrupo =row.get("no_empresa").toString();
					vsRevolvente=(String)row.get("cl_plazo");
					vsDivisa=(String)row.get("id_divisa");
					registros.add(dto);
				}
				ReportePasivosFDto dto = new ReportePasivosFDto();
				dto.setDivisa((String)row.get("desc_divisa"));
				dto.setNoBanco(row.get("id_banco_prestamo").toString());
				dto.setBanco((String)row.get("desc_arrendadora"));
				dto.setImporte(formato.format(Double.parseDouble(row.get("capital").toString())));
				dto.setTasaRef(row.get("tasa_ref").toString());
				dto.setIdDivisa((String)row.get("id_divisa"));
				dto.setValorTasa(formato2.format(Double.parseDouble(row.get("valor_tasa").toString())));
				dto.setInicio(ponerFormatoFecha2((String)row.get("fec_disposicion")));     
				dto.setPlazo(row.get("plazo").toString());
				dto.setVencimiento(ponerFormatoFecha2((String)row.get("fec_vencimiento")));
				double cortoPlazo=(Double.parseDouble(row.get("monto_cplazo").toString())*vsTipoCambio);
				dto.setMontoCortoPlazo(Double.toString(cortoPlazo));
				dto.setInteres(formato.format(Double.parseDouble(row.get("interes").toString())));
				double largoPlazo=Double.parseDouble(row.get("monto_lplazo").toString())*vsTipoCambio;
				dto.setMontoLargoPlazo(Double.toString(largoPlazo));
				dto.setPeriodicidadPagos((String)row.get("periodicidad"));
				dto.setGarantias((String)row.get("comentarios"));
				if(registros.get(registros.size()-1).getLinea().equals(row.get("id_financiamiento").toString()))
					dto.setLineaAutorizada("0.00");
				else{
					lineaAutorizada=Double.parseDouble(row.get("monto_autorizado").toString())*vsTipoCambio;
					dto.setLineaAutorizada(Double.toString(lineaAutorizada));
				}
				if(registros.get(registros.size()-1).getLinea().equals(row.get("id_financiamiento").toString())){
					lineaDisp = Double.parseDouble(registros.get(registros.size()-1).getLineaDisponible())-Double.parseDouble(dto.getMontoCortoPlazo())+Double.parseDouble(dto.getMontoLargoPlazo());
					dto.setLineaDisponible(Double.toString(lineaDisp));
				}else{
					System.out.println("Entra"+row.get("id_financiamiento").toString());
					lineaDisp = Double.parseDouble(dto.getLineaAutorizada())-Double.parseDouble(dto.getMontoCortoPlazo())+Double.parseDouble(dto.getMontoLargoPlazo());
					System.out.println(formato.format(lineaDisp));
					dto.setLineaDisponible(Double.toString(lineaDisp));
				}
				dto.setLinea(row.get("id_financiamiento").toString());
				dto.setIdDisposicion(row.get("id_disposicion").toString());
				dto.setNoEmpresaAux(row.get("no_empresa").toString());
				dto.setDestinoRecurso(row.get("descripcion").toString());
				dto.setIdTipoFinanciamiento(row.get("tipoUso").toString());
				if (viGrupo.equals(row.get("no_empresa").toString())|| vsRevolvente.equals(row.get("cl_plazo")) || vsDivisa.equals(row.get("id_divisa"))) {
					if(row.get("id_divisa").toString().equals("MN")){
						totalDivisa=totalDivisa+Double.parseDouble(row.get("capital").toString());
						totalDivisaInteres=totalDivisaInteres+Double.parseDouble(row.get("interes").toString());
					}
					else{
						totalDivisa=totalDivisa+(Double.parseDouble(row.get("capital").toString())*vsTipoCambio);
						totalDivisaInteres=totalDivisaInteres+(Double.parseDouble(row.get("interes").toString())*vsTipoCambio);
					}
					totalDivisaMontoCP=totalDivisaMontoCP+cortoPlazo;
					totalDivisaMontoLP=totalDivisaMontoLP+largoPlazo;
					totalDivisaLA=totalDivisaLA+lineaAutorizada;
				totalDivisaLD=totalDivisaLD+lineaDisp;
				}
				if (viGrupo.equals(row.get("no_empresa").toString())|| vsRevolvente.equals(row.get("cl_plazo"))) {
					if(row.get("id_divisa").toString().equals("MN")){
						totalRevolvente=totalRevolvente+Double.parseDouble(row.get("capital").toString());
						totalRevolventeInteres=totalRevolventeInteres+(Double.parseDouble(row.get("interes").toString()));
					}
					else{
						totalRevolvente=totalRevolvente+(Double.parseDouble(row.get("capital").toString())*vsTipoCambio);
						totalRevolventeInteres=totalRevolventeInteres+(Double.parseDouble(row.get("interes").toString()));	
					}
					totalRevolventeMontoCP=totalRevolventeMontoCP+cortoPlazo;
					totalRevolventeMontoLP=totalRevolventeMontoLP+largoPlazo;
					totalRevolventeLA=totalRevolventeLA+lineaAutorizada;
					totalRevolventeLD=totalRevolventeLD+lineaDisp;
				}
				if (viGrupo.equals(row.get("no_empresa").toString())|| vsRevolvente.equals(row.get("cl_plazo"))) {
					if(row.get("id_divisa").toString().equals("MN")){
						totalEmpresa=totalEmpresa+Double.parseDouble(row.get("capital").toString());
						totalEmpresaInteres=totalEmpresaInteres+(Double.parseDouble(row.get("interes").toString()));
					}
					else{
						totalEmpresa=totalEmpresa+(Double.parseDouble(row.get("capital").toString())*vsTipoCambio);
						totalEmpresaInteres=totalEmpresaInteres+(Double.parseDouble(row.get("interes").toString()));
					}
					totalEmpresaMontoCP=totalEmpresaMontoCP+cortoPlazo;
					totalEmpresaMontoLP=totalEmpresaMontoLP+largoPlazo;
					totalEmpresaLA=totalEmpresaLA+lineaAutorizada;
					totalEmpresaLD=totalEmpresaLD+lineaDisp;
				}
				registros.add(dto);
				if(rows.size()>posicion+1){
					if (!vsDivisa.equals(rows.get(posicion+1).get("id_divisa").toString())||!vsRevolvente.equals(rows.get(posicion+1).get("cl_plazo").toString()) ||! viGrupo.equals(rows.get(posicion+1).get("no_empresa").toString())){
						ReportePasivosFDto dto2 = new ReportePasivosFDto();                
						dto2.setBanco("TOTAL DIVISA");
						dto2.setLinea("");
						dto2.setIdDisposicion("");
						dto2.setImporte(formato.format(totalDivisa));
						dto2.setMontoCortoPlazo(Double.toString(totalDivisaMontoCP));
						dto2.setInteres(formato.format(totalDivisaInteres));
						dto2.setMontoLargoPlazo(Double.toString(totalDivisaMontoLP));
						dto2.setLineaAutorizada(Double.toString(totalDivisaLA));
						dto2.setLineaDisponible(Double.toString(totalDivisaLD));
						dto2.setColor("color:#165ACB");
						registros.add(dto2);
						totalDivisa=0;
						totalDivisaMontoCP=0;
						totalDivisaInteres=0;
						totalDivisaMontoLP=0;
						totalDivisaLA=0;
						totalDivisaLD=0;
					}
					if(!vsRevolvente.equals(rows.get(posicion+1).get("cl_plazo").toString()) ||! viGrupo.equals(rows.get(posicion+1).get("no_empresa").toString())){
						ReportePasivosFDto	 dto3 = new ReportePasivosFDto();
						dto3.setBanco("TOTAL "+vsRevolvente);
						dto3.setImporte(formato.format(totalRevolvente));
						dto3.setMontoCortoPlazo(Double.toString(totalRevolventeMontoCP));
						dto3.setInteres(formato.format(totalRevolventeInteres));
						dto3.setMontoLargoPlazo(Double.toString(totalRevolventeMontoLP));
						dto3.setLineaAutorizada(Double.toString(totalRevolventeLA));
						dto3.setLineaDisponible(Double.toString(totalRevolventeLD));
						dto3.setColor("color:#165ACB");
						dto3.setLinea("");
						dto3.setIdDisposicion("");
						registros.add(dto3);
						totalRevolvente=0;
						totalRevolventeMontoCP=0;
						totalRevolventeInteres=0;
						totalRevolventeMontoLP=0;
						totalRevolventeLA=0;
						totalRevolventeLD=0;
					}
					if (!viGrupo.equals(rows.get(posicion+1).get("no_empresa").toString())){
						ReportePasivosFDto dto4 = new ReportePasivosFDto();
						dto4.setBanco("TOTAL CREDITOS EMPRESA");
						dto4.setImporte(formato.format(totalEmpresa));
						dto4.setMontoCortoPlazo(Double.toString(totalEmpresaMontoCP));
						dto4.setInteres(formato.format(totalEmpresaInteres));
						dto4.setMontoLargoPlazo(Double.toString(totalEmpresaMontoLP));
						dto4.setLineaAutorizada(Double.toString(totalEmpresaLA));
						dto4.setLineaDisponible(Double.toString(totalEmpresaLD));
						dto4.setLinea("");
						dto4.setIdDisposicion("");
						dto4.setColor("color:#165ACB");
						registros.add(dto4);
						totalEmpresa=0;
						totalEmpresaMontoCP=0;
						totalEmpresaInteres=0;
						totalEmpresaMontoLP=0;
						totalEmpresaLA=0;
						totalEmpresaLD=0;
						ReportePasivosFDto dto5 = new ReportePasivosFDto();
						dto5.setLinea("");
						dto5.setIdDisposicion("");
						registros.add(dto5);
					}
				}else{   
					ReportePasivosFDto dto2 = new ReportePasivosFDto();
					dto2.setLinea("");
					dto2.setIdDisposicion("");
					dto2.setBanco("TOTAL DIVISA");
					dto2.setImporte(formato.format(totalDivisa));
					dto2.setMontoCortoPlazo(Double.toString(totalDivisaMontoCP));
					dto2.setInteres(formato.format(totalDivisaInteres));
					dto2.setMontoLargoPlazo(Double.toString(totalDivisaMontoLP));
					dto2.setLineaAutorizada(Double.toString(totalDivisaLA));
					dto2.setLineaDisponible(Double.toString(totalDivisaLD));
					dto2.setColor("color:#165ACB");
					registros.add(dto2);
					totalDivisa=0;
					totalDivisaMontoCP=0;
					totalDivisaInteres=0;
					totalDivisaMontoLP=0;
					totalDivisaLA=0;
					totalDivisaLD=0;
					ReportePasivosFDto dto3 = new ReportePasivosFDto();
					dto3.setLinea("");
					dto3.setIdDisposicion("");
					dto3.setBanco("TOTAL "+vsRevolvente);
					dto3.setImporte(formato.format(totalRevolvente));
					dto3.setMontoCortoPlazo(Double.toString(totalRevolventeMontoCP));
					dto3.setInteres(formato.format(totalRevolventeInteres));
					dto3.setMontoLargoPlazo(Double.toString(totalRevolventeMontoLP));
					dto3.setLineaAutorizada(Double.toString(totalRevolventeLA));
					dto3.setLineaDisponible(Double.toString(totalRevolventeLD));
					dto3.setColor("color:#165ACB");
					registros.add(dto3);
					totalRevolvente=0;
					totalRevolventeMontoCP=0;
					totalRevolventeInteres=0;
					totalRevolventeMontoLP=0;
					totalRevolventeLA=0;
					totalRevolventeLD=0;
					ReportePasivosFDto	dto4 = new ReportePasivosFDto();
					dto4.setBanco("TOTAL CREDITOS EMPRESA");
					dto4.setLinea("");
					dto4.setIdDisposicion("");
					dto4.setImporte(formato.format(totalEmpresa));
					dto4.setMontoCortoPlazo(Double.toString(totalEmpresaMontoCP));
					dto4.setInteres(formato.format(totalEmpresaInteres));
					dto4.setMontoLargoPlazo(Double.toString(totalEmpresaMontoLP));
					dto4.setLineaAutorizada(Double.toString(totalEmpresaLA));
					dto4.setLineaDisponible(Double.toString(totalEmpresaLD));
					dto4.setColor("color:#165ACB");
					totalEmpresa=0;
					totalEmpresaMontoCP=0;
					totalEmpresaInteres=0;
					totalEmpresaMontoLP=0;
					totalEmpresaLA=0;
					totalEmpresaLD=0;
					registros.add(dto4);
				}
				posicion=posicion+1;
			}       
		} catch (Exception e) {
			logger.error("Error", e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:ReportePasivosFDaoImpl, M:obtenerPasivosFinancieros");
		}

		return registros;
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
			sbSQL.append("\n  order by descripcion");
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
}
