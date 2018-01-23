package com.webset.set.consultas.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.consultas.dto.ChequeraGridDto;
import com.webset.set.consultas.dto.MovimientoDto;
import com.webset.set.consultas.dto.ParamBusSolicitudDto;
import com.webset.set.consultas.dto.ParamBusquedaMovimientoDto;
import com.webset.set.consultas.dto.ParamZimpFactDto;
import com.webset.set.consultas.dto.ParametroGridChequeraDto;
import com.webset.set.consultas.dto.ReportesChequeraDto;
import com.webset.set.consultas.dto.DatosChequeraDto;
import com.webset.set.consultas.dto.SaldoChequeraDto;
import com.webset.set.consultas.dto.SaldoDto;
import com.webset.set.utilerias.dto.BitacoraChequesDto;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.DetArchTransferDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.RetornoUnicoDto;
import com.webset.set.utilerias.dto.RevividorDto;
import com.webset.set.utilerias.dto.ZimpFactDto;
import com.webset.utils.tools.Utilerias;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.Funciones;
/**
 * IMplementacion de Consultas
 */
@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
public class ConsultasDao {
	private static Logger logger = Logger.getLogger(ConsultasDao.class);
	private ConsultasGenerales gral;
	private Bitacora bitacora = new Bitacora();
	private Funciones funciones = new Funciones();
	private JdbcTemplate jdbcTemplate = new JdbcTemplate();
	private StringBuffer sb;
	ConsultasGenerales consultasGenerales;
	GlobalSingleton globalSingleton;
	
	/**
	 * 
	 * @param idEmpresa
	 * @return List<ComunDto>
	 * 
	 * Hace llamada  las tablas cat_banco y cat_cta_banco y obtine el id y la descripcion del banco
	 * Public Function FunSQLCombo336(ByVal pvvValor1 As Variant) As ADODB.Recordset
	 * Public Function FunSQLCombo328(ByVal pvvValor1 As Variant) As ADODB.Recordset
	 */
	public List<LlenaComboGralDto> llenarComboCatCtaBanco(int idEmpresa){
		List<LlenaComboGralDto> datos = new ArrayList<LlenaComboGralDto>();
		sb = new StringBuffer();
		try{
			sb.append(" SELECT DISTINCT cat_banco.id_banco AS id, cat_banco.desc_banco AS descrip ");
		    sb.append("\n FROM cat_banco,cat_cta_banco ");
		    sb.append("\n WHERE cat_cta_banco.id_banco = cat_banco.id_banco ");
		    if(idEmpresa != 0)
		    sb.append("\n 	AND cat_cta_banco.no_empresa = " + idEmpresa );
		    
		    
		     datos = jdbcTemplate.query(sb.toString(), new RowMapper<LlenaComboGralDto>(){
		    	public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
		    		LlenaComboGralDto dato = new LlenaComboGralDto();
						dato.setId(rs.getInt("id"));
						dato.setDescripcion(rs.getString("descrip"));
					    return dato;
		    	}});
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modConsulta, C:ModConsultasDao, M:llenarComboCatCtaBanco");
		}
		return datos;
	}
	/**
	 * 
	 * @param idBanco
	 * @param idEmpresa
	 * @return List<ComunDto>
	 * 
	 * Retorna la lista de chequeras que correspondan al idBanco e idEmpresa ademas de que el tipo chequera 
	 * sea diferente U
	 * Public Function FunSQLCombo335(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant) As ADODB.Recordset
	 * Public Function FunSQLCombo326(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant) As ADODB.Recordset 
	 */
	public List<ComunDto> llenarComboChequera(int idBanco, int idEmpresa){
		sb = new StringBuffer();
		List<ComunDto> datos =null;
		try{
			sb.append(" SELECT id_chequera AS id, id_chequera AS descrip ");
			sb.append("\n FROM cat_cta_banco ");
			sb.append("\n WHERE tipo_chequera <> 'U' ");
			sb.append("\n 	AND id_banco = " + idBanco);
			sb.append("\n 	AND no_empresa = " + idEmpresa);
			System.out.println("chequera "+sb.toString());
			datos = jdbcTemplate.query(sb.toString(), new RowMapper<ComunDto>(){
		    	public ComunDto mapRow(ResultSet rs, int idx){
		    		ComunDto dato = new ComunDto();
		    		try {
						dato.setIdChequera(rs.getString("id"));
						dato.setDescripcion(rs.getString("descrip"));
					} catch (SQLException e) {
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
								+ " P:modConsulta, C:ModConsultasDao, M:llenarComboChequera");
					}
		    		return dato;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modConsulta, C:ModConsultasDao, M:llenarComboChequera");
		}
		return datos;
	}
	/**
	 * 
	 * @param dto
	 * @return SaldoChequeraDto
	 * 
	 * Public Function FunSQLSelect802(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant, ByVal pvvValor3 As Variant, ByVal pvvValor4 As Variant, ByVal pvvValor5 As Variant) As ADODB.Recordset
	 * 
	 * Seleciona los datos para llenar la pantalla de Saldo Historico de Chequeras
	 */
	public List<SaldoChequeraDto> seleccionarDatosChequeraHistorico(DatosChequeraDto dto){
		sb = new StringBuffer();
		List<SaldoChequeraDto> saldoChequeraDto = null;  
		sb.append(" SELECT saldo_inicial, saldo_final, cargo, abono, cargo_sbc, abono_sbc ");
		sb.append("\n FROM hist_cat_cta_banco c, cat_banco b  ");
		sb.append("\n WHERE b.desc_banco = '" + Utilerias.validarCadenaSQL(dto.getDescBanco()) + "'");
		sb.append("\n 	AND c.id_chequera= '" + Utilerias.validarCadenaSQL(dto.getIdChequera()) + "'");
		sb.append("\n 	AND c.id_banco = b.id_banco ");
		sb.append("\n 	AND c.no_empresa =  " + Utilerias.validarCadenaSQL(dto.getNoEmpresa()));
		sb.append("\n 	AND c.id_banco = " + dto.getIdBanco());
		sb.append("\n 	AND c.fec_valor= convert(datetime,'" + Utilerias.validarCadenaSQL(dto.getFecValor()) + "',103)");
		System.out.println("Query: "+sb.toString());
		try{
			saldoChequeraDto = jdbcTemplate.query(sb.toString(), new RowMapper<SaldoChequeraDto>(){
		    	public SaldoChequeraDto mapRow(ResultSet rs, int idx){
		    		SaldoChequeraDto dato = new SaldoChequeraDto();
		    		try {
						dato.setSaldoInicial(rs.getDouble("saldo_inicial"));
						dato.setSaldoFinal(rs.getDouble("saldo_final"));
						dato.setCargo(rs.getDouble("cargo"));
						dato.setAbono(rs.getDouble("abono"));
						dato.setCargoSbc(rs.getDouble("cargo_sbc"));
						dato.setAbonoSbc(rs.getDouble("abono_sbc"));
					} catch (SQLException e) {
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
								+ " P:modConsulta, C:ModConsultasDao, M:seleccionarDatosChequeraHistorico");
					}
		    		return dato;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modConsulta, C:ModConsultasDao, M:seleccionarDatosChequera");
		}
		return saldoChequeraDto;
	}
	/**
	 * 
	 * @param dto
	 * @return
	 * Public Function SELECTSaldoCheq(ByVal pvsDescbanco As String, ByVal pvsChequera As String, 
	 * 		ByVal pviEmpresa As Integer, ByVal pviBanco As Integer) As ADODB.Recordset
	 * 
	 * Seleciona los datos para llenar la pantalla de Saldo de Chequeras
	 */
	public List<SaldoChequeraDto> seleccionarDatosChequera(DatosChequeraDto dto){
		sb = new StringBuffer();
		
		List<SaldoChequeraDto> saldoChequeraDto = null;  
		sb.append(" SELECT saldo_inicial, saldo_final, cargo, abono, cargo_sbc, abono_sbc,saldo_banco_ini ");
		sb.append("\n FROM cat_cta_banco c, cat_banco b  ");
		sb.append("\n WHERE c.id_chequera= '" + Utilerias.validarCadenaSQL(dto.getIdChequera()) + "'");
		sb.append("\n 	AND c.id_banco = b.id_banco ");
		sb.append("\n 	AND c.no_empresa =  " + dto.getNoEmpresa());
		sb.append("\n 	AND c.id_banco = " + dto.getIdBanco());
		try{
			System.out.println("obtener saldos "+sb.toString());
			saldoChequeraDto = jdbcTemplate.query(sb.toString(), new RowMapper<SaldoChequeraDto>(){
		    	public SaldoChequeraDto mapRow(ResultSet rs, int idx){
		    		SaldoChequeraDto dato = new SaldoChequeraDto();
		    		try {
						dato.setSaldoInicial(rs.getDouble("saldo_inicial"));
						dato.setSaldoFinal(rs.getDouble("saldo_final"));
						dato.setCargo(rs.getDouble("cargo"));
						dato.setAbono(rs.getDouble("abono"));
						dato.setCargoSbc(rs.getDouble("cargo_sbc"));
						dato.setAbonoSbc(rs.getDouble("abono_sbc"));
						dato.setSaldoFinBanco(rs.getDouble("saldo_banco_ini"));	
						
					} catch (SQLException e) {
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
								+ " P:modConsulta, C:ModConsultasDao, M:seleccionarDatosChequera");
					}
		    		return dato;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modConsulta, C:ModConsultasDao, M:seleccionarDatosChequera");
		}
		return saldoChequeraDto;
	}
	/**
	 * 
	 * @param dto
	 * @return List<RetornoUnicoDto>
	 * 
	 * Public Function FunSQLSelect793(ByVal pvvValor1 As Variant, ByVal pvvValor2 
	 * 	As Variant, ByVal pvvValor3 As Variant, Optional psFECHA As String) As ADODB.Recordset
	 * 
	 * Hace la suma de los importes que tengan que ver los los parametros 
	 * idChequera, noEmpresa, idBanco, fecValor
	 */
	public List<RetornoUnicoDto> seleccionarImporteTotalMovimiento(DatosChequeraDto dto){
		List<RetornoUnicoDto> retorno = null;
		sb = new StringBuffer();
		sb.append(" SELECT coalesce(sum(m.importe),0) as cheques ");
		sb.append("\n FROM movimiento m ");
		sb.append("\n WHERE m.id_tipo_movto = 'E' and ((m.b_entregado = 'N' ");
		sb.append("\n 	and id_estatus_mov in ('I','R')) ");
		sb.append("\n 	or (id_estatus_mov in('T','P','J','K') AND id_estatus_cb in('N','P') ");
		sb.append("\n 			AND  id_estatus_mov <> 'X')) ");
		sb.append("\n 	and m.id_chequera= '" + Utilerias.validarCadenaSQL(dto.getIdChequera()) + "'");
		sb.append("\n 	and m.no_empresa =  " + dto.getNoEmpresa());
		sb.append("\n 	and m.id_banco = " + dto.getIdBanco());
		sb.append("\n 	and m.fec_valor <= convert(datetime,'" + Utilerias.validarCadenaSQL(dto.getFecValor().substring(0, 10)) + "', 103)");
		sb.append("\n 	and m.id_forma_pago = 1 ");
		sb.append("\n 	and m.no_cheque not in (select no_cheque from movimiento b ");
	    sb.append("\n 			where b.importe < 0 and b.id_tipo_operacion = 3200 ");
	    sb.append("\n 				and b.id_banco = m.id_banco and b.id_chequera = m.id_chequera)");
	    
	    System.out.println("Query saldos: " + sb.toString());
	    
	    try{
	    	retorno = jdbcTemplate.query(sb.toString(), new RowMapper<RetornoUnicoDto>(){
		    	public RetornoUnicoDto mapRow(ResultSet rs, int idx){
		    		RetornoUnicoDto dato = new RetornoUnicoDto();
		    		try {
		    			dato.setCheques(rs.getDouble("cheques"));
					} catch (SQLException e) {
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
								+ " P:modConsulta, C:ModConsultasDao, M:seleccionarImporteTotalMovimiento");
					}
		    		return dato;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modConsulta, C:ModConsultasDao, M:seleccionarImporteTotalMovimiento");
		}
		return retorno;
	}
	
	/**
	 * 
	 * @param dto
	 * @return List<ChequeraGridDto>
	 * 
	 * para seleccionar abonos
	 * Public Function FunSQLSelect803(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant, _
                                ByVal pvvValor3 As Variant, ByVal pvvValor4 As Variant, _
                                ByVal pvvValor5 As Variant, ByVal pvvValor6 As Variant, _
                                ByVal pvvValor7 As Variant) As ADODB.Recordset
	 */
	public List<ChequeraGridDto> seleccionarDatosGridAbonos(ParametroGridChequeraDto dto){
		sb = new StringBuffer();
		List<ChequeraGridDto> datos = null;
		sb.append(" SELECT REPLACE(COALESCE(m.no_docto,''),'null','') AS no_docto, concepto, CASE WHEN id_estatus_mov IN ('X','Z') ");
		sb.append("\n	 AND convert(datetime, fec_modif, 103) = '" + Utilerias.validarCadenaSQL(dto.getFecMod()) + "'");
		sb.append("\n	"+ Utilerias.validarCadenaSQL(dto.getSubQuery1()) +" '"+ Utilerias.validarCadenaSQL(dto.getFecMod()) + "'");
		sb.append("\n	"+ Utilerias.validarCadenaSQL(dto.getSubQuery2()));
		sb.append("\n	, m.no_folio_det, cfp.desc_forma_pago, m.observacion ");
		sb.append("\n FROM movimiento m, cat_forma_pago cfp ");
		sb.append("\n WHERE id_tipo_movto = 'I'");
		sb.append("\n	and (((id_estatus_mov in ('A','W')  And (convert(datetime, fec_valor, 103) = '" + Utilerias.validarCadenaSQL(dto.getFecValor()) +"' or convert(char, fec_modif, 103) = '" + Utilerias.validarCadenaSQL(dto.getFecValor()) + "')");
		sb.append("\n 	and (importe_desglosado is null or importe_desglosado = 0)) or ");
		sb.append("\n 	(id_estatus_mov='H' and id_tipo_operacion in (3102,3103,3152,3153, 3108, 3110, 3112)  ");
		sb.append("\n 		and b_salvo_buen_cobro='S' And convert(datetime, fec_modif, 103) = '" + Utilerias.validarCadenaSQL(dto.getFecValor()) + "'))");
		sb.append(")\n	and (b_salvo_buen_cobro = 'S' or b_salvo_buen_cobro is null)");
		sb.append("\n 	and no_empresa = " + dto.getIdEmpresa());
		sb.append("\n 	and id_banco=" + dto.getIdBanco() + " and id_chequera='" + Utilerias.validarCadenaSQL(dto.getIdChequera()) + "'");
		sb.append("\n 	and m.id_forma_pago = cfp.id_forma_pago ");
		sb.append("\n UNION ALL ");
		sb.append("\n SELECT no_docto, concepto, case when id_estatus_mov in ('X','Z') ");
		sb.append("\n	 AND convert(datetime, fec_modif, 103) = '" + Utilerias.validarCadenaSQL(dto.getFecMod()) + "'");
		sb.append("\n	"+ Utilerias.validarCadenaSQL(dto.getSubQuery1()) +"' "+ Utilerias.validarCadenaSQL(dto.getFecMod()) + "'");
		sb.append("\n	"+ Utilerias.validarCadenaSQL(dto.getSubQuery2()));
		sb.append("\n	, m.no_folio_det, cfp.desc_forma_pago, m.observacion ");
		sb.append("\n FROM hist_movimiento m, cat_forma_pago cfp");
		sb.append("\n WHERE id_tipo_movto = 'I' ");
		sb.append("\n	and (((id_estatus_mov in ('A','W') And (convert(datetime, fec_valor, 103) = '" + Utilerias.validarCadenaSQL(dto.getFecValor()) + "'");
		sb.append("\n 	or convert(datetime, fec_modif, 103) ='" + Utilerias.validarCadenaSQL(dto.getFecValor()) + "') and (importe_desglosado is null or importe_desglosado = 0)) or ");
		sb.append("\n 	(id_estatus_mov='H' and id_tipo_operacion in (3102,3103,3152,3153, 3108, 3110, 3112)  and b_salvo_buen_cobro='S' ");
		sb.append("\n 	And convert(datetime, fec_modif, 103) = '" + Utilerias.validarCadenaSQL(dto.getFecValor()) + "'))");
		sb.append(") \n 	 and (b_salvo_buen_cobro = 'S' or b_salvo_buen_cobro is null)");
		sb.append("\n 	and no_empresa = " + dto.getIdEmpresa());
		sb.append("\n 	and id_banco=" + dto.getIdBanco() + " and id_chequera='" + Utilerias.validarCadenaSQL(dto.getIdChequera()) + "'");
		sb.append("\n 	and m.id_forma_pago = cfp.id_forma_pago");
		sb.append("\n UNION ALL ");
		sb.append("\n SELECT no_docto, concepto, case when id_estatus_mov in ('X','Z') ");
		sb.append("\n 	and convert(char, fec_modif, 103) = '" + Utilerias.validarCadenaSQL(dto.getFecMod()) + "'" );
		sb.append("\n 	" + Utilerias.validarCadenaSQL(dto.getSubQuery1()) +"'"+ Utilerias.validarCadenaSQL(dto.getFecMod()) + "'");
		sb.append("\n 	" + Utilerias.validarCadenaSQL(dto.getSubQuery2()) + " , m.no_folio_det, cfp.desc_forma_pago, m.observacion ");
		sb.append("\n FROM hist_mov_det m, cat_forma_pago cfp");
		sb.append("\n WHERE id_tipo_movto = 'I'");
		sb.append("\n 	and (((id_estatus_mov in ('A','W') And (convert(datetime, fec_valor, 103) = '" + Utilerias.validarCadenaSQL(dto.getFecValor()) + "'"); 
		sb.append("\n 	or convert(datetime, fec_modif, 103) = '" + Utilerias.validarCadenaSQL(dto.getFecValor()) + "') and importe_desglosado is null) or ");
		sb.append("\n 	(id_estatus_mov='H' and id_tipo_operacion in (3102,3103,3152,3153, 3108, 3110, 3112)  and b_salvo_buen_cobro='S' And fec_modif = '" + Utilerias.validarCadenaSQL(dto.getFecValor()) + "'))");
		sb.append("\n 	or (id_estatus_mov in ('X','Z') and convert(datetime, fec_modif, 103) = '" + Utilerias.validarCadenaSQL(dto.getFecValor()) + "' and convert(datetime, fec_alta, 103) < '" + Utilerias.validarCadenaSQL(dto.getFecValor()) + "')");
		sb.append("\n 	or (id_estatus_mov in ('X','Z') and convert(datetime, fec_modif, 103) >'" + Utilerias.validarCadenaSQL(dto.getFecValor()) + "' and convert(datetime, fec_alta, 103) = '" + Utilerias.validarCadenaSQL(dto.getFecValor()) + "')) and (b_salvo_buen_cobro = 'S' or b_salvo_buen_cobro is null)");
		sb.append("\n 	and no_empresa = " + dto.getIdEmpresa());
		sb.append("\n 	and id_banco=" + dto.getIdBanco() + " and id_chequera='" + Utilerias.validarCadenaSQL(dto.getIdChequera()) + "'");
		sb.append("\n 	and m.id_forma_pago = cfp.id_forma_pago");
		
		System.out.println("Carga abonos hist: " + sb.toString());
		
		try{
			datos = jdbcTemplate.query(sb.toString(), new RowMapper<ChequeraGridDto>(){
		    	public ChequeraGridDto mapRow(ResultSet rs, int idx){
		    		ChequeraGridDto dato = new ChequeraGridDto();
		    		try {
		    			dato.setNoDocto(rs.getString("no_docto"));
		    			dato.setConcepto(rs.getString("concepto"));
		    			dato.setImporte(rs.getDouble("importe"));
		    			dato.setBeneficiario(rs.getString("beneficiario"));
		    			dato.setNoFolioDet(rs.getString("no_folio_det"));
		    			dato.setDescFormaPago(rs.getString("desc_forma_pago"));
		    			dato.setObservacion(rs.getString("observacion"));
					} catch (SQLException e) {
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
								+ " P:modConsulta, C:ModConsultasDao, M:seleccionarDatosGridAbonos");
					}
		    		return dato;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modConsulta, C:ModConsultasDao, M:seleccionarDatosGridAbonos");
		}
		
		return datos;
	}
	/**
	 * 
	 * @param dto
	 * @return List<ChequeraGridDto>
	 * 
	 * Public Function FunSQLSelect804(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant, ByVal pvvValor3 As Variant, ByVal pvvValor4 As Variant, ByVal pvvValor5 As Variant, ByVal pvvValor6 As Variant, ByVal pvvValor7 As Variant) As ADODB.Recordset
	 *  
	 * Retorna los datos de los cargos
	 */
	public List<ChequeraGridDto> seleccionarDatosGridCargos(ParametroGridChequeraDto dto){
		sb = new StringBuffer();
		List<ChequeraGridDto> datos = null;
		sb.append(" SELECT REPLACE(COALESCE(m.no_docto,''),'null','') AS no_docto, m.concepto, case when m.id_estatus_mov in ('X','Z') ");
		sb.append("\n 	and convert(char(10), m.fec_modif, 103) ='" + Utilerias.validarCadenaSQL(dto.getFecMod()) + "'" + Utilerias.validarCadenaSQL(dto.getSubQuery1()) +"'"+ Utilerias.validarCadenaSQL(dto.getFecMod()) + "'" + Utilerias.validarCadenaSQL(dto.getSubQuery2()));
		sb.append(",\n 	m.no_folio_det, cfp.desc_forma_pago, m.observacion ");
		sb.append("\n FROM movimiento m, cat_forma_pago cfp ");
		sb.append("\n WHERE m.id_tipo_movto = 'E' ");
		sb.append("\n 	and (((m.id_estatus_mov in('P','R','I','T','K','J','G') and m.id_tipo_operacion not in (3000))");
		sb.append("\n 	or (m.id_estatus_mov='A' and m.id_tipo_operacion not between 3000 and 3099))");
		sb.append("\n 	and m.no_empresa = " + dto.getIdEmpresa());
		sb.append("\n 	and (convert(datetime, m.fec_valor, 103) = '" + Utilerias.validarCadenaSQL(dto.getFecValor()) + "'");
		sb.append("\n 	and convert(datetime, m.fec_valor, 103) = '" + Utilerias.validarCadenaSQL(dto.getFecValor()) + "')");
		sb.append(")\n 	 and m.id_banco = " + dto.getIdBanco() + " and m.id_chequera = '" +Utilerias.validarCadenaSQL(dto.getIdChequera()) + "'");
		sb.append("\n 	and m.origen_mov<> 'MIG' ");
		sb.append("\n 	and m.id_forma_pago = cfp.id_forma_pago");
		sb.append("\n UNION ALL ");
		sb.append("\n 	SELECT m.no_docto, m.concepto, case when m.id_estatus_mov in ('X','Z') ");
		sb.append("\n 	and convert(char(10), m.fec_modif, 103) ='" + Utilerias.validarCadenaSQL(dto.getFecMod()) + "'" + Utilerias.validarCadenaSQL(dto.getSubQuery1()) +"'"+ Utilerias.validarCadenaSQL(dto.getFecMod()) + "'" + Utilerias.validarCadenaSQL(dto.getSubQuery2()));
		sb.append(",\n 	m.no_folio_det, cfp.desc_forma_pago, m.observacion ");
		sb.append("\n FROM hist_movimiento m, cat_forma_pago cfp ");
		sb.append("\n WHERE m.id_tipo_movto = 'E' ");
		sb.append("\n 	and (((m.id_estatus_mov in('P','R','I','T','K','J','G') and m.id_tipo_operacion not in (3000))");
		sb.append("\n 	or (m.id_estatus_mov='A' and m.id_tipo_operacion not between 3000 and 3099))");
		sb.append("\n 	and m.no_empresa = " + dto.getIdEmpresa());
		sb.append("\n 	and convert(datetime, m.fec_valor, 103) = '" + Utilerias.validarCadenaSQL(dto.getFecValor()) + "'");
		sb.append(")\n 	and m.id_banco = " + dto.getIdBanco() + " and m.id_chequera = '" + Utilerias.validarCadenaSQL(dto.getIdChequera()) + "' and m.origen_mov<>'MIG' ");
		sb.append("\n 	and m.id_forma_pago = cfp.id_forma_pago order by no_docto");
		
		System.out.println("Carga egresos hist: " + sb);
		
		try{
			datos = jdbcTemplate.query(sb.toString(), new RowMapper<ChequeraGridDto>(){
		    	public ChequeraGridDto mapRow(ResultSet rs, int idx){
		    		ChequeraGridDto dato = new ChequeraGridDto();
		    		try {
		    			dato.setNoDocto(rs.getString("no_docto"));
		    			dato.setConcepto(rs.getString("concepto"));
		    			dato.setImporte(rs.getDouble("importe"));
		    			dato.setBeneficiario(rs.getString("beneficiario"));
		    			dato.setNoFolioDet(rs.getString("no_folio_det"));
		    			dato.setDescFormaPago(rs.getString("desc_forma_pago"));
		    			dato.setObservacion(rs.getString("observacion"));
					} catch (SQLException e) {
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
								+ " P:modConsulta, C:ModConsultasDao, M:seleccionarDatosGridCargos");
					}
		    		return dato;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modConsulta, C:ModConsultasDao, M:seleccionarDatosGridCargos");
		}
		return datos;
	}
	
	/**
	 * 
	 * @param idUsuario
	 * @return List<ComunDto>
	 * 
	 * llenar el combo de saldo de chequeras
	 */
	public List<ComunDto> llenarComboEmpresa(int idUsuario){
		System.out.println("llenarcomboempresa");
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT ea.no_empresa, ea.nom_empresa ");
		sb.append("\n FROM empresa ea");
		sb.append("\n WHERE ea.no_empresa in ");
		sb.append("\n	 ( SELECT u.no_empresa ");
		sb.append("\n		 FROM usuario_empresa u ");
		sb.append("\n		 WHERE u.no_usuario = " + idUsuario + " )");
		sb.append("\n ORDER BY ea.nom_empresa ");
		System.out.println("<---------------------------------->");
		System.out.println(sb.toString());
		System.out.println("<---------------------------------->");
		List<ComunDto> empresas = null;
		try{
			empresas = jdbcTemplate.query(sb.toString(), new RowMapper<ComunDto>(){
			public ComunDto mapRow(ResultSet rs, int idx) throws SQLException {
				ComunDto empresa = new ComunDto();
				empresa.setIdEmpresa(rs.getInt("no_empresa"));
				empresa.setDescripcion(rs.getString("nom_empresa"));
				return empresa;
			}});
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Ingresos, C:ConsultasDao, M:llenarComboEmpresa");
		}
		return empresas;
	}

	/**
	 * 
	 * @param dto
	 * @return List<ChequeraGridDto>
	 * 
	 * Public Function SELECTMvtosAbonosCheq(ByVal pvsFecha As String, ByVal pviEmpresa As Integer, ByVal pviBanco As Integer, ByVal pvsChequera As String) As ADODB.Recordset
	 * 
	 * Selecciona los datos para el grid de saldo chequera abonos
	 */
	public List<ChequeraGridDto> seleccionarMvtosAbonosChequera(ParametroGridChequeraDto dto){
		List<ChequeraGridDto> datos = null;
		sb = new StringBuffer();
		sb.append("	SELECT REPLACE(COALESCE(m.no_docto,''),'null','') AS no_docto, m.concepto, ");
		sb.append("\n 	case when m.id_estatus_mov in ('X') then m.importe *-1 ");
		sb.append("\n 	    else m.importe end as importe,");
		sb.append("\n 	m.beneficiario, m.no_folio_det, cfp.desc_forma_pago as forma_pago, m.observacion");
		sb.append("\n FROM movimiento m, cat_forma_pago cfp");
		sb.append("\n WHERE m.id_tipo_movto = 'I'");
		sb.append("\n 	and (((m.id_estatus_mov in ('A','W') and m.fec_valor = convert(datetime,'" + Utilerias.validarCadenaSQL(dto.getFecValor()) + "',103)) ");
		sb.append("\n   and m.id_tipo_operacion not in(3102,3103,3108,3110,3112,3152,3153)) ");
		sb.append("\n 	or (m.id_estatus_mov='A' ");
		sb.append("\n 	and m.id_tipo_operacion in (3102,3103,3108,3110,3112,3152,3153) ");
		sb.append("\n 	and m.b_salvo_buen_cobro='S' ");
		sb.append("\n 	and m.fec_modif = convert(datetime,'" + Utilerias.validarCadenaSQL(dto.getFecValor()) + "',103))");
		sb.append("\n 	or (m.id_estatus_mov in ('X') and m.fec_modif = convert(datetime,'" + Utilerias.validarCadenaSQL(dto.getFecValor()) + "',103)");
		sb.append("\n 	and m.fec_valor < m.fec_modif and m.b_salvo_buen_cobro='S'))");
		sb.append("\n 	and m.no_empresa = " + dto.getIdEmpresa());
		sb.append("\n 	and m.id_banco=" + dto.getIdBanco());
		sb.append("\n 	AND m.id_estatus_mov <> 'X'");
		sb.append("\n 	and m.id_chequera='" + Utilerias.validarCadenaSQL(dto.getIdChequera()) + "'");
		sb.append("\n 	and m.id_tipo_operacion not in(1013,1014) ");
		sb.append("\n 	and m.id_forma_pago = cfp.id_forma_pago");
		
		System.out.println("Query para los abonos: " + sb.toString());
		
		try{
			datos = jdbcTemplate.query(sb.toString(), new RowMapper<ChequeraGridDto>(){
		    	public ChequeraGridDto mapRow(ResultSet rs, int idx){
		    		ChequeraGridDto dato = new ChequeraGridDto();
		    		try {
		    			dato.setNoDocto(rs.getString("no_docto"));
		    			dato.setConcepto(rs.getString("concepto"));
		    			dato.setImporte(rs.getDouble("importe"));
		    			dato.setBeneficiario(rs.getString("beneficiario"));
		    			dato.setNoFolioDet(rs.getString("no_folio_det"));
		    			dato.setDescFormaPago(rs.getString("forma_pago"));
		    			dato.setObservacion(rs.getString("observacion"));
					} catch (SQLException e) {
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
								+ " P:modConsulta, C:ModConsultasDao, M:seleccionarMvtosAbonosChequera");
					}
		    		return dato;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modConsulta, C:ModConsultasDao, M:seleccionarMvtosAbonosChequera");
		}
		return datos;
	}
	
	/**
	 * 
	 * @param dto
	 * @return List<ChequeraGridDto>
	 * 
	 *Public Function SELECTMvtosCargosCheq(ByVal pvsFecha As String, ByVal pviEmpresa As Integer, ByVal pviBanco As Integer, ByVal pvsChequera As String) As ADODB.Recordset 
	 * 
	 * Selecciona los datos para el grid de saldo chequera cargos
	 */
	public List<ChequeraGridDto> seleccionarMvtosCargosChequera(ParametroGridChequeraDto dto){
		List<ChequeraGridDto> datos = null;
		sb = new StringBuffer();
		sb.append(" SELECT REPLACE(COALESCE(m.no_docto,''),'null','') AS no_docto, m.concepto, cfp.desc_forma_pago as forma_pago, case when m.id_estatus_mov in ('X','Z') then m.importe *-1 ");
		sb.append("\n 	else m.importe end as importe, m.beneficiario, m.no_folio_det, m.observacion");
		sb.append("\n FROM movimiento m, cat_forma_pago cfp ");
		sb.append("\n WHERE m.id_tipo_movto = 'E'");
		sb.append("\n 	and (((m.id_estatus_mov in('P','R','I','T','K','J','G') and m.id_tipo_operacion not in (3000))");
		sb.append("\n 	or (m.id_estatus_mov='A' and m.id_tipo_operacion not between 3000 and 3099))");
		sb.append("\n 	and m.no_empresa = " +dto.getIdEmpresa());
		sb.append("\n 	and (m.fec_valor = convert(datetime,'" + Utilerias.validarCadenaSQL(dto.getFecValor()) + "',103))");
		sb.append(")\n	and m.id_banco = " + dto.getIdBanco());
		sb.append("\n 	and m.id_chequera = '" + Utilerias.validarCadenaSQL(dto.getIdChequera()) + "' ");
		sb.append("\n 	and ((m.origen_mov <> 'MIG') or (m.origen_mov is null))");
		sb.append("\n 	and m.id_forma_pago = cfp.id_forma_pago");
		sb.append("\n ORDER BY no_docto,importe ");
		logger.info("seleccionarMvtosCargosChequera="+sb.toString());
		
		System.out.println("Query para los cargos: " + sb.toString());
		
		try{
			datos = jdbcTemplate.query(sb.toString(), new RowMapper<ChequeraGridDto>(){
		    	public ChequeraGridDto mapRow(ResultSet rs, int idx){
		    		ChequeraGridDto dato = new ChequeraGridDto();
		    		try {
		    			dato.setNoDocto(rs.getString("no_docto"));
		    			dato.setConcepto(rs.getString("concepto"));
		    			dato.setImporte(rs.getDouble("importe"));
		    			dato.setBeneficiario(rs.getString("beneficiario"));
		    			dato.setNoFolioDet(rs.getString("no_folio_det"));
		    			dato.setDescFormaPago(rs.getString("forma_pago"));
		    			dato.setObservacion(rs.getString("observacion"));
					} catch (SQLException e) {
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
								+ " P:modConsulta, C:ModConsultasDao, M:seleccionarMvtosCargosChequera");
					}
		    		return dato;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modConsulta, C:ModConsultasDao, M:seleccionarMvtosCargosChequera");
		}
		return datos;
	}
	/**
	 * 
	 * @param dto
	 * @return List<RetornoUnicoDto>
	 * 
	 * Public Function FunSQLSelect805(ByVal pvvValor1 As Variant, 
	 * 		ByVal pvvValor2 As Variant, ByVal pvvValor3 As Variant) As ADODB.Recordset
	 * 
	 * Selecciona la ultima fecha de actualizacion  de la chequera
	 */
	public List<RetornoUnicoDto> seleccionarFechaActualizacion(ParametroGridChequeraDto dto){
		List<RetornoUnicoDto> datos = null;
		sb = new StringBuffer();
	    sb.append(" SELECT fecha_banca ");
	    sb.append("\n FROM cat_cta_banco ");
	    sb.append("\n WHERE no_empresa = " + Utilerias.validarCadenaSQL(dto.getIdEmpresa()));
	    sb.append("\n	and id_banco = " + Utilerias.validarCadenaSQL(dto.getIdBanco()));
	    sb.append("\n 	and id_chequera = '" + Utilerias.validarCadenaSQL(dto.getIdChequera()) + "'");
	    
		try{
			datos = jdbcTemplate.query(sb.toString(), new RowMapper<RetornoUnicoDto>(){
		    	public RetornoUnicoDto mapRow(ResultSet rs, int idx){
		    		RetornoUnicoDto dato = new RetornoUnicoDto();
		    		
		    		try {
		    			if(rs.getDate("fecha_banca").equals(""))
		    				dato.setFechaActualizacion("");
		    			else
		    				dato.setFechaActualizacion(funciones.ponerFecha(rs.getDate("fecha_banca")));
					} catch (SQLException e) {
						dato.setFechaActualizacion("");
						return dato;
					}
		    		return dato;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + " P:modConsulta, C:ModConsultasDao, M:seleccionarFechaActualizacion");
			
		}
		return datos;
	}
    
	/**
	 * 
	 * @param dto
	 * @return List<RetornoUnicoDto>
	 * 
	 * Public Function FunSQLSelect798(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant, ByVal pvvValor3 As Variant) As ADODB.Recordset
	 * 
	 * Selecciona el valor de chequeras por entregar para  SaldoChequera
	 */
	public List<ChequeraGridDto> seleccionarPorEnt(ParametroGridChequeraDto dto){
		List<ChequeraGridDto> datos = null;
		//MAFJ 12 MAR 2014 
		//Modificacion para grid
		sb = new StringBuffer();
		sb.append(" SELECT REPLACE(COALESCE(m.no_docto,''),'null','') AS no_docto, m.concepto, cfp.desc_forma_pago as forma_pago, ");
		sb.append("\n 	m.importe, m.beneficiario, m.no_folio_det, m.observacion");
		sb.append("\n FROM movimiento m, cat_cta_banco cta, cat_forma_pago cfp");
		sb.append("\n WHERE m.no_empresa = cta.no_empresa ");
		sb.append("\n 	and m.id_banco=cta.id_banco  ");
		sb.append("\n 	and m.id_chequera = cta.id_chequera ");
		sb.append("\n 	and m.id_tipo_movto = 'E' ");
		sb.append("\n 	and m.b_entregado = 'N'  and m.id_estatus_mov in ('I','R')");
		sb.append("\n 	and cta.id_chequera= '" + Utilerias.validarCadenaSQL(dto.getIdChequera()) + "'");
		sb.append("\n 	and cta.no_empresa =  " + Utilerias.validarCadenaSQL(dto.getIdEmpresa()));
		sb.append("\n 	and cta.id_banco = " + Utilerias.validarCadenaSQL(dto.getIdBanco()));
		sb.append("\n 	and m.id_forma_pago = cfp.id_forma_pago");
		sb.append("\n 	and m.id_forma_pago IN ('1')"); 
		sb.append("\n ORDER BY m.no_docto, m.importe "); 
		
		logger.info("seleccionarPorEnt="+sb.toString());
		
		System.out.println("Query para los cheques en transito: " + sb.toString());
		
		try{
			datos = jdbcTemplate.query(sb.toString(), new RowMapper<ChequeraGridDto>(){
		    	public ChequeraGridDto mapRow(ResultSet rs, int idx){
		    		ChequeraGridDto dato = new ChequeraGridDto();
		    		try {
		    			dato.setNoDocto(rs.getString("no_docto"));
		    			dato.setConcepto(rs.getString("concepto"));
		    			dato.setImporte(rs.getDouble("importe"));
		    			dato.setBeneficiario(rs.getString("beneficiario"));
		    			dato.setNoFolioDet(rs.getString("no_folio_det"));
		    			dato.setDescFormaPago(rs.getString("forma_pago"));
		    			dato.setObservacion(rs.getString("observacion"));
					} catch (SQLException e) {
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
								+ " P:modConsulta, C:ModConsultasDao, M:seleccionarPorEnt");
					}
		    		return dato;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modConsulta, C:ModConsultasDao, M:seleccionarPorEnt");
		}
		return datos;
	}
		
		/**
	 * 
	 * @param dto
	 * @return List<RetornoUnicoDto>
	 * 
	 * Public Function FunSQLSelect799(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant, ByVal pvvValor3 As Variant) As ADODB.Recordset
	 * 
	 * Selecciona el valor de chequeras por imprimir para  SaldoChequera
	 */
	public List<RetornoUnicoDto>  seleccionarPorImp(ParametroGridChequeraDto dto){
		List<RetornoUnicoDto> datos=null;
		sb = new StringBuffer();
		sb.append(" SELECT  sum(m.importe) as porimp ");
		sb.append("\n FROM movimiento m ");
		sb.append("\n LEFT JOIN cat_caja cc ON(m.id_caja = cc.id_caja), ");
		sb.append("\n 	cat_banco cb");
		sb.append("\n WHERE m.id_banco = cb.id_banco ");
		sb.append("\n 	and id_tipo_movto = 'E' ");
		sb.append("\n 	and id_forma_pago in (1,8,9) ");
		sb.append("\n 	and id_estatus_mov in ('J','P') ");
		sb.append("\n 	and m.id_chequera= '" + Utilerias.validarCadenaSQL(dto.getIdChequera()) + "'");
		sb.append("\n 	and m.no_empresa =  " + dto.getIdEmpresa());
		sb.append("\n 	and m.id_banco = " + dto.getIdBanco());
		try{
	    	datos = jdbcTemplate.query(sb.toString(), new RowMapper<RetornoUnicoDto> (){
		    	public RetornoUnicoDto mapRow(ResultSet rs, int idx){
		    		RetornoUnicoDto dato = new RetornoUnicoDto();
		    		try {
		    			dato.setCheques(rs.getDouble("porimp"));
					} catch (SQLException e) {
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
								+ " P:modConsulta, C:ModConsultasDao, M:seleccionarPorImp");
					}
		    		return dato;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modConsulta, C:ModConsultasDao, M:seleccionarPorImp");
		}
		return datos;
	}

	/**
	 * 
	 * @param dto
	 * @return List<RetornoUnicoDto>
	 * Public Function FunSQLSelect800(ByVal pvvValor1 As Variant, 
	 * 			ByVal pvvValor2 As Variant, ByVal pvvValor3 As Variant) As ADODB.Recordset
	 * 
	 * Selecciona el valor de chequeras por transferir para  SaldoChequera
	 */
	public List<RetornoUnicoDto>  seleccionarPorTrans(ParametroGridChequeraDto dto){
		List<RetornoUnicoDto> datos=null;
		sb = new StringBuffer();
		sb.append(" SELECT Coalesce(sum(m.importe),0) as portrans ");
		sb.append("\n FROM movimiento m, cat_banco cb, cat_estatus ce, cat_banco z ");
		sb.append("\n WHERE cb.id_banco = m.id_banco_benef ");
		sb.append("\n 	and m.id_banco = z.id_banco ");
		sb.append("\n 	and ce.clasificacion = 'MOV' ");
		sb.append("\n 	and ce.id_estatus = m.id_estatus_mov");
		sb.append("\n 	and m.id_tipo_movto = 'E' ");
		sb.append("\n 	and id_forma_pago = 3 ");
		sb.append("\n 	and id_estatus_mov in ('T','P') ");
		sb.append("\n 	and m.id_chequera= '" + Utilerias.validarCadenaSQL(dto.getIdChequera()) + "'");
		sb.append("\n 	and m.no_empresa =  " + dto.getIdEmpresa());
		sb.append("\n 	and m.id_banco = " + dto.getIdBanco());
		try{
	    	datos = jdbcTemplate.query(sb.toString(), new RowMapper<RetornoUnicoDto>(){
		    	public RetornoUnicoDto mapRow(ResultSet rs, int idx){
		    		RetornoUnicoDto dato = new RetornoUnicoDto();
		    		try {
		    			dato.setCheques(rs.getDouble("portrans"));
					} catch (SQLException e) {
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
								+ " P:modConsulta, C:ModConsultasDao, M:seleccionarPorTrans");
					}
		    		return dato;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modConsulta, C:ModConsultasDao, M:seleccionarPorTrans");
		}
		return datos;
	}
	
	/**
	 * 
	 * @param dto
	 * @return List<ChequeraGridDto>
	 * 
	 * Public Function FunSQLSelect795(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant, ByVal pvvValor3 As Variant) As ADODB.Recordset
	 * 
	 * selcciona los datos de grid para AbonoSBC de Saldo Chequera
	 */
	public List<ChequeraGridDto> seleccionarAbonoSBC(ParametroGridChequeraDto dto){
		List<ChequeraGridDto> datos = null;
		sb = new StringBuffer();
		sb.append(" SELECT no_docto, concepto, importe, beneficiario, no_folio_det ");
		sb.append("\n FROM movimiento ");
		sb.append("\n WHERE b_salvo_buen_cobro = 'N' and id_tipo_movto = 'I' ");
		sb.append("\n 	and id_estatus_mov = 'P' and id_tipo_operacion not between 4000 and 4200 ");
		sb.append("\n 	and no_empresa = " + dto.getIdEmpresa());
		sb.append("\n 	and id_banco = " + dto.getIdBanco() + " and id_chequera ='" + Utilerias.validarCadenaSQL(dto.getIdChequera()) + "'");
		System.out.println("<-----Abonos--------------->");
		System.out.println(sb.toString());
		System.out.println("<-------------------------->");
		try{
			datos = jdbcTemplate.query(sb.toString(), new RowMapper<ChequeraGridDto>(){
		    	public ChequeraGridDto mapRow(ResultSet rs, int idx){
		    		ChequeraGridDto dato = new ChequeraGridDto();
		    		try {
		    			dato.setNoDocto(rs.getString("no_docto"));
		    			dato.setConcepto(rs.getString("concepto"));
		    			dato.setImporte(rs.getDouble("importe"));
		    			dato.setBeneficiario(rs.getString("beneficiario"));
		    			dato.setNoFolioDet(rs.getString("no_folio_det"));
					} catch (SQLException e) {
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
								+ " P:modConsulta, C:ModConsultasDao, M:seleccionarAbonoSBC");
					}
		    		return dato;
		    	}
		    });
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modConsulta, C:ModConsultasDao, M:seleccionarAbonoSBC");
		}
		return datos;
	}
	
	/**
	 * 
	 * @param dto
	 * @return List<RetornoUnicoDto>
	 * 
	 * Public Function FunSQLSelect796(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant, ByVal pvvValor3 As Variant, ByVal pvvValor4 As Variant) As ADODB.Recordset
	 * 
	 * Selecciona la suma del abonoSBC en saldoChequera
	 */
	public List<RetornoUnicoDto>  seleccionarSumaAbonosSBC(ParametroGridChequeraDto dto){
		List<RetornoUnicoDto> datos=null;
		sb = new StringBuffer();
		sb.append(" SELECT sum(importe) as suma ");
		sb.append("\n FROM movimiento ");
		sb.append("\n WHERE b_salvo_buen_cobro = 'N' and id_tipo_movto = 'I'");
		sb.append("\n 	and id_estatus_mov = 'P' and id_tipo_operacion not between 4000 and 4200 ");
		sb.append("\n 	and no_empresa = " + dto.getIdEmpresa());
	    //sb.append("\n 	and fec_valor <= '" + dto.getFecValor() + "'");
		sb.append("\n 	and id_banco = " + dto.getIdBanco()); 
		sb.append("\n 	and id_chequera ='" + Utilerias.validarCadenaSQL(dto.getIdChequera()) + "'");
		try{
	    	datos = jdbcTemplate.query(sb.toString(), new RowMapper<RetornoUnicoDto>(){
		    	public RetornoUnicoDto mapRow(ResultSet rs, int idx){
		    		RetornoUnicoDto dato = new RetornoUnicoDto();
		    		try {
		    			dato.setCheques(rs.getDouble("suma"));
					} catch (SQLException e) {
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
								+ " P:modConsulta, C:ModConsultasDao, M:seleccionarSumaAbonosSBC");
					}
		    		return dato;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modConsulta, C:ModConsultasDao, M:seleccionarSumaAbonosSBC");
		}
		return datos;
	}
	
	/**
	 * 
	 * @return String
	 */
	public String obtenerFechaHoy() {
		gral = new ConsultasGenerales(jdbcTemplate);
		return funciones.ponerFechaSola(gral.obtenerFechaHoy());
	}
	/**
	 * 
	 * @return String
	 */
	public String obtenerFechaAyer() {
		gral = new ConsultasGenerales(jdbcTemplate);
		return funciones.ponerFechaSola(gral.obtenerFechaAyer());
	}
	/**
	 * M\u00e9todo para obtener las cajas asignadas a un usuario,
	 * utilizado en ConsultaDeMovimientos.js
	 * @param usuario
	 * @return lista con que contiene el id y la descripci\u00f2n de la caja
	 */
	public List<LlenaComboGralDto> consultarCajas(int usuario){
		StringBuffer sbSQL = new StringBuffer();
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try{
			sbSQL.append("SELECT id_caja as id,");
			sbSQL.append("\n desc_caja as describ");
			sbSQL.append("\n FROM cat_caja");
			sbSQL.append("\n WHERE id_caja in");
			sbSQL.append("\n (select id_caja from caja_usuario");
			sbSQL.append("\n where no_usuario = " + usuario + ")");
			sbSQL.append("\n ORDER BY desc_caja");
		
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException 
					{
						LlenaComboGralDto caja = new LlenaComboGralDto();
							caja.setId(rs.getInt("id"));
							caja.setDescripcion(rs.getString("describ"));
						return caja;
					}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:ConsultasDao, M:consultarCajas");
		}
		return list;
	}
	
	/**
	 * Este m\u00e9todo consulta las chequeras asignadas a un banco,
	 * es utilizado en ConsultaDeMovimientos
	 * @param iIdBanco
	 * @param iIdEmpresa
	 * @return
	 */
	public List<LlenaComboGralDto> consultarChequeras(int iIdBanco, int iIdEmpresa){
		List<LlenaComboGralDto> listCheq = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("SELECT id_chequera as ID, id_chequera as describ From cat_cta_banco");
			sbSql.append("\n	Where id_banco = " + iIdBanco );
			if(iIdEmpresa != 0)
			sbSql.append("\n	and no_empresa = " + iIdEmpresa);
			
			listCheq = jdbcTemplate.query(sbSql.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						dtoCons.setDescripcion(rs.getString("ID"));
					return dtoCons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasDao, M:consultarChequeras");
		}
		return listCheq;
	}
	
	/**
	 * Este m\u00e9todo consulta las divisiones de todas las empresas 
	 * o de una empresa en particular, es utilizado en ConsultaDeMovimientos
	 * FunSQLComboDivision
	 * @param iIdEmpresa
	 * @return
	 */
	public List<LlenaComboGralDto> consultarDivisiones(int iIdEmpresa){
		List<LlenaComboGralDto> listDiv = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("SELECT id_division, desc_division from cat_division ");

			if(iIdEmpresa > 0)
		       sbSql.append("\n	where no_empresa = " + iIdEmpresa);
		    
			sbSql.append("\n order by desc_division");
			
			listDiv = jdbcTemplate.query(sbSql.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						dtoCons.setId(rs.getInt("id_division"));
						dtoCons.setDescripcion(rs.getString("desc_division"));
					return dtoCons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasDao, M:consultarDivisiones");
		}
		return listDiv;
	}
	
	
	/**
	 * FunSQLCombo123()
	 * Este m\u00e9todo obtiene los estatus de cat_estatus con clasificacion MOV
	 * @return
	 */
	public List<LlenaComboGralDto> consultarEstatus(){
		List<LlenaComboGralDto> listEstatus = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("SELECT id_estatus, desc_estatus From cat_estatus");
			sbSql.append(" Where  clasificacion = 'MOV' ");
		 
			listEstatus = jdbcTemplate.query(sbSql.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						//Se setea en campoUno en lugar de id por que su id_estatus es String
						dtoCons.setCampoUno(rs.getString("id_estatus"));
						dtoCons.setDescripcion(rs.getString("desc_estatus"));
					return dtoCons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasDao, M:consultarEstatus");
		}
		return listEstatus;
	}
	
	/**
	 * Este m\u00e9todo obtiene los origenes de movimiento de la tabla
	 * cat_origen_mov, es utilizado en ConsultaDeMovimientos
	 * @return
	 */
	public List<LlenaComboGralDto> consultarOrigen(){
		List<LlenaComboGralDto> listEstatus = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("SELECT origen_mov , desc_origen_mov");
			sbSql.append("\n	FROM cat_origen_mov");
			
		 
			listEstatus = jdbcTemplate.query(sbSql.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						//Se setea en campoUno en lugar de id por que su origen_mov es String
						dtoCons.setCampoUno(rs.getString("origen_mov"));
						dtoCons.setDescripcion(rs.getString("desc_origen_mov"));
					return dtoCons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasDao, M:consultarOrigen");
		}
		return listEstatus;
	}
	
	/**
	 * FunSQLCombo121
	 * Este m\u00e9todo obtiene las claves de operaci\u00f2n de las tablas
	 * cat_cve_operacion, cve_tipo_operacion, es utilizado en ConsultaDeMoviemientos
	 * @return
	 */
	public List<LlenaComboGralDto> consultarTiposOperacion(){
		List<LlenaComboGralDto> listTipoOperacion = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("SELECT cco.id_cve_operacion,"); 
			sbSql.append("\n	cco.desc_cve_operacion");
			sbSql.append("\n	From cat_cve_operacion cco, cve_tipo_operacion cto");
			sbSql.append("\n	Where cco.id_cve_operacion = cto.id_cve_operacion");
			sbSql.append("\n	and cco.padre_hijo <> 'H'");
			sbSql.append("\n	and cto.secuencia = 1");
		 
			listTipoOperacion = jdbcTemplate.query(sbSql.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						dtoCons.setId(rs.getInt("id_cve_operacion"));
						dtoCons.setDescripcion(rs.getString("desc_cve_operacion"));
					return dtoCons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasDao, M:consultarTiposOperacion");
		}
		return listTipoOperacion;
	}
	
	/**
	 * Este m\u00e9todo consulta la chequera de la tabla de valor_custodia
	 * @param iIdCaja
	 * @param iIdEmpresa
	 * @return
	 */
	public List<LlenaComboGralDto> consultarValorCustodia(int iIdCaja, int iIdEmpresa)
	{
		List<LlenaComboGralDto> listTipoOperacion = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("SELECT");
			sbSql.append("\n	distinct");
			sbSql.append("\n	id_chequera");
			sbSql.append("\n	From");
			sbSql.append("\n	valor_custodia");
			sbSql.append("\n	Where ");
			sbSql.append("\n	id_caja = " + iIdCaja);
			if(iIdEmpresa > 0)
				sbSql.append("\n	and no_empresa = " + iIdEmpresa);
		 
			listTipoOperacion = jdbcTemplate.query(sbSql.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						dtoCons.setDescripcion(rs.getString("id_chequera"));
					return dtoCons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasDao, M:consultarValorCustodia");
		}
		return listTipoOperacion;
	}
	public StringBuffer armarConsReporteMovto(ParamBusquedaMovimientoDto dto){
		StringBuffer sbSql = new StringBuffer();
		ParamBusquedaMovimientoDto dtoCampos = new ParamBusquedaMovimientoDto();
		ParamBusquedaMovimientoDto dtoParams = new ParamBusquedaMovimientoDto();
		dtoParams = dto;		
		try{
			/*    If chkSubtotales.Value Then
			        Set rstdatos = gobjSQL.FunSQLConsultaMovimientos(optmov(0).Value, sEmpresas, Format(sFecha1, "dd/mm/yyyy"), _
			                                                         Format(sFecha2, "dd/mm/yyyy"), iClave, sBanco, sBenef, sNoBenef, _
			                                                         sMonto1, sMonto2, sChequera, _
			                                                         sConcepto, sDivisa, sDivision, _
			                                                         sEstatus, sFolio, sFormaPago, sLote, _
			                                                         sCaja, sDocto, sNoCheque, sFactura, _
			                                                         sOrigen, sValorCust, , "CHEQUERA")
			      Else
			        If chkOrigen_Mov = 0 Then
			            Set rstdatos = gobjSQL.FunSQLConsultaMovimientos(optmov(0).Value, sEmpresas, Format(sFecha1, "dd/mm/yyyy"), _
			                                                         Format(sFecha2, "dd/mm/yyyy"), iClave, sBanco, sBenef, sNoBenef, _
			                                                         sMonto1, sMonto2, sChequera, _
			                                                         sConcepto, sDivisa, sDivision, _
			                                                         sEstatus, sFolio, sFormaPago, sLote, _
			                                                         sCaja, sDocto, sNoCheque, sFactura, _
			                                                         sOrigen, sValorCust, , "BENEF", 1)
			        Else
			            Set rstdatos = gobjSQL.FunSQLConsultaMovimientos(optmov(0).Value, sEmpresas, Format(sFecha1, "dd/mm/yyyy"), _
			                                                             Format(sFecha2, "dd/mm/yyyy"), iClave, sBanco, sBenef, sNoBenef, _
			                                                             sMonto1, sMonto2, sChequera, _
			                                                             sConcepto, sDivisa, sDivision, _
			                                                             sEstatus, sFolio, sFormaPago, sLote, _
			                                                             sCaja, sDocto, sNoCheque, sFactura, _
			                                                             sOrigen, sValorCust, , "BENEF", 3, chkOrigen_Mov)
			        End If
			    End If*/
			sbSql.append("select * from\n(");
			if(ConstantesSet.gsDBM.equals("SYBASE"))
			{
				//Se modifican los valores iniciales de dto que viene del Action
					dtoParams.setBSoloFolioDet(false);
					dtoParams.setITalblaInicio(0);
					dtoParams.setITablaFin(0);
					dtoParams.setBReporte(true);
					dtoParams.setBEncReporte(true);
					dtoParams.setIHist(0);
				sbSql.append("\n" + this.armarConsultaMovimientos(dtoParams) + "\n" );
				sbSql.append("\n UNION ALL \n");
					dtoParams.setBSoloFolioDet(false);
					dtoParams.setITalblaInicio(1);
					dtoParams.setITablaFin(1);
					dtoParams.setBReporte(true);
					dtoParams.setBEncReporte(true);
					dtoParams.setIHist(0);
				sbSql.append("\n" + this.armarConsultaMovimientos(dtoParams) + "\n" );
			}
			else //if(dtoParams.getIHist() == 0)
			{
					dtoParams.setBSoloFolioDet(false);
					dtoParams.setITalblaInicio(0);
					dtoParams.setITablaFin(1); 
					dtoParams.setBReporte(false);
					dtoParams.setBEncReporte(true);
					dtoParams.setIHist(0);
				sbSql.append("\n" + this.armarConsultaMovimientos(dtoParams) + "\n" );
			}
			/*else if(dtoParams.getIHist() == 1)
			{
				
			}*/
			sbSql.append("\n UNION ALL \n");//se agrega en lugar del APPEND VB borrar despues de pruebas
			
			if(ConstantesSet.gsDBM.equals("SYBASE"))
			{
					dtoCampos.setOptMovto(true);
					dtoCampos.setFolioDet(-1);
					dtoCampos.setBSoloFolioDet(false);
					dtoCampos.setITalblaInicio(0);
					dtoCampos.setITablaFin(0);
					dtoCampos.setBReporte(true);
					dtoCampos.setBEncReporte(false);
					dtoCampos.setIHist(0);
				sbSql.append("\n" + this.armarConsultaMovimientos(dtoCampos) + "\n" );
				sbSql.append("\n AND m.grupo_pago in ( \n");
					dtoParams.setFolioDet(-1);
					dtoParams.setBSoloFolioDet(true);
					dtoParams.setITalblaInicio(1);
					dtoParams.setITablaFin(1);
					dtoParams.setBReporte(true);
					dtoParams.setBEncReporte(false);
					dtoParams.setIHist(0);
				sbSql.append("\n" + this.armarConsultaMovimientos(dtoParams) + "\n");
				
				sbSql.append("\n UNION ALL \n");
				
					dtoCampos.setOptMovto(true);
					dtoCampos.setFolioDet(-1);
					dtoCampos.setBSoloFolioDet(false);
					dtoCampos.setITalblaInicio(0);
					dtoCampos.setITablaFin(0);
					dtoCampos.setBReporte(true);
					dtoCampos.setBEncReporte(false);
					dtoCampos.setIHist(0);
				sbSql.append("\n" + this.armarConsultaMovimientos(dtoCampos) + "\n");
				sbSql.append("\n AND m.grupo_pago in ( \n");
					dtoParams.setFolioDet(-1);
					dtoParams.setBSoloFolioDet(true);
					dtoParams.setITalblaInicio(1);
					dtoParams.setITablaFin(1);
					dtoParams.setBReporte(true);
					dtoParams.setBEncReporte(false);
					dtoParams.setIHist(0);
				sbSql.append("\n" + this.armarConsultaMovimientos(dtoParams) + "\n" );
				
				sbSql.append("\n )");
			}
			else
			{
					dtoCampos.setOptMovto(true);
					dtoCampos.setFolioDet(-1);
					dtoCampos.setBSoloFolioDet(false);
					dtoCampos.setITalblaInicio(0);
					dtoCampos.setITablaFin(0);
					dtoCampos.setBReporte(true);
					dtoCampos.setBEncReporte(false);
					dtoCampos.setIHist(0);
				sbSql.append("\n" + this.armarConsultaMovimientos(dtoCampos) + "\n");
				sbSql.append("\n AND (m.id_tipo_operacion = 3201 or m.id_tipo_operacion = 3701)");
				sbSql.append("\n AND m.grupo_pago in (");
					dtoParams.setFolioDet(-1);
					dtoParams.setBSoloFolioDet(true);
					dtoParams.setITalblaInicio(0);
					dtoParams.setITablaFin(dto.getITablaFin());
					dtoParams.setBReporte(true);
					dtoParams.setBEncReporte(false);
					dtoParams.setIHist(0);
				sbSql.append("\n" + this.armarConsultaMovimientos(dtoParams) + "\n");
		        sbSql.append("\n )");
			}
			
			sbSql.append("\n UNION ALL \n");
			
			if(ConstantesSet.gsDBM.equals("SYBASE"))
			{
					dtoCampos.setOptMovto(true);
					dtoCampos.setFolioDet(-1);
					dtoCampos.setBSoloFolioDet(false);
					dtoCampos.setITalblaInicio(1);
					dtoCampos.setITablaFin(1);
					dtoCampos.setBReporte(true);
					dtoCampos.setBEncReporte(false);
					dtoCampos.setIHist(0);
				sbSql.append("\n" + this.armarConsultaMovimientos(dtoCampos) + "\n");
				sbSql.append("\n AND m.grupo_pago in ( \n");
					dtoParams.setFolioDet(-1);
					dtoParams.setBSoloFolioDet(true);
					dtoParams.setITalblaInicio(0);
					dtoParams.setITablaFin(0);
					dtoParams.setBReporte(true);
					dtoParams.setBEncReporte(false);
					dtoParams.setIHist(0);
				sbSql.append("\n" + this.armarConsultaMovimientos(dtoParams) + "\n");
				sbSql.append("\n and (m.id_tipo_operacion = 3201 or m.id_tipo_operacion = 3701)");
				sbSql.append("\n )");
				
				sbSql.append("\n UNION ALL \n");
				
					dtoCampos.setOptMovto(true);
					dtoCampos.setFolioDet(-1);
					dtoCampos.setBSoloFolioDet(false);
					dtoCampos.setITalblaInicio(1);
					dtoCampos.setITablaFin(1);
					dtoCampos.setBReporte(true);
					dtoCampos.setBEncReporte(false);
					dtoCampos.setIHist(0);
				sbSql.append("\n" + this.armarConsultaMovimientos(dtoCampos) + "\n");
				sbSql.append("\n AND m.grupo_pago in ( \n");
					dtoParams.setFolioDet(-1);
					dtoParams.setBSoloFolioDet(true);
					dtoParams.setITalblaInicio(1);
					dtoParams.setITablaFin(1);
					dtoParams.setBReporte(true);
					dtoParams.setBEncReporte(false);
					dtoParams.setIHist(0);
				sbSql.append("\n" + this.armarConsultaMovimientos(dtoParams) + "\n");
				sbSql.append("\n and (m.id_tipo_operacion = 3201 or m.id_tipo_operacion = 3701)");
				sbSql.append("\n )");
			}
			else
			{
					dtoCampos.setOptMovto(true);
					dtoCampos.setFolioDet(-1);
					dtoCampos.setBSoloFolioDet(false);
					dtoCampos.setITalblaInicio(dto.getITablaFin() -1);
					dtoCampos.setITablaFin(dto.getITablaFin() - 1);
					dtoCampos.setBReporte(true);
					dtoCampos.setBEncReporte(false);
					dtoCampos.setIHist(0);
				sbSql.append("\n" + this.armarConsultaMovimientos(dtoCampos) + "\n");
				sbSql.append("\n and (m.id_tipo_operacion = 3201 or m.id_tipo_operacion = 3701)");
				sbSql.append("\n AND m.grupo_pago in ( \n");
					dtoParams.setFolioDet(-1);
					dtoParams.setBSoloFolioDet(true);
					dtoParams.setITalblaInicio(0);
					dtoParams.setITablaFin(3);
					dtoParams.setBReporte(true);
					dtoParams.setBEncReporte(false);
					dtoParams.setIHist(0);
				sbSql.append("\n" + this.armarConsultaMovimientos(dtoParams) + "\n");
				sbSql.append("\n )");
			}
			
			sbSql.append("\n UNION ALL \n");
			
			if(ConstantesSet.gsDBM.equals("SYBASE"))
			{
				
			}
			else
			{
					dtoCampos.setOptMovto(true);
					dtoCampos.setFolioDet(-1);
					dtoCampos.setBSoloFolioDet(false);
					dtoCampos.setITalblaInicio(dto.getITablaFin());
					dtoCampos.setITablaFin(dto.getITablaFin());
					dtoCampos.setBReporte(true);
					dtoCampos.setBEncReporte(false);
					dtoCampos.setIHist(0);
				sbSql.append("\n" + this.armarConsultaMovimientos(dtoCampos) + "\n");
				sbSql.append("\n and (m.id_tipo_operacion = 3201 or m.id_tipo_operacion = 3701)");
				sbSql.append("\n AND m.grupo_pago in ( \n");
					dtoParams.setFolioDet(-1);
					dtoParams.setBSoloFolioDet(true);
					dtoParams.setITalblaInicio(0);
					dtoParams.setITablaFin(2);
					dtoParams.setBReporte(true);
					dtoParams.setBEncReporte(false);
					dtoParams.setIHist(0);
				sbSql.append("\n" + this.armarConsultaMovimientos(dtoParams) + "\n");
				sbSql.append("\n )");
			}
			sbSql.append("\n   ) m Order by m.no_cliente");
			
			System.out.println(" ------------------------------------- \n");
			System.out.print("Consulta Reporte: " + sbSql.toString());
			System.out.println(" -------------------------------------");
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasDao, M:armarConsReporteMovto");
		}
		return sbSql;
	}
	
	public StringBuffer armarConsultaMovimientos(ParamBusquedaMovimientoDto dto)
	{
		StringBuffer sbSql = new StringBuffer();
		try
		{
			for(int iTablas = dto.getITalblaInicio(); iTablas <= dto.getITablaFin(); iTablas ++)
			{
				if(dto.isBSoloFolioDet())
				{
					sbSql.append("SELECT no_folio_det");
				}
				else
				{
					if(dto.isBReporte() || dto.isBEncReporte())
					{
						sbSql.append("SELECT CASE WHEN m.importe_original<> m.importe then m.importe_original else 0 end as importe_original,");
						sbSql.append("\n	m.fec_valor,m.fec_valor_original,m.fec_alta, coalesce(m.no_cliente, '0') as no_cliente, m.beneficiario, m.concepto, ");
		                sbSql.append("\n    m.importe, fp.desc_forma_pago, b.desc_banco as desc_banco_benef, ");
		                sbSql.append("\n    m.id_chequera_benef, ");
		                sbSql.append("\n    CASE WHEN m.id_forma_pago = 2 THEN ' ' ");
		                sbSql.append("\n         ELSE CASE WHEN not m.id_tipo_operacion between 5000 and 6000 ");
		                sbSql.append("\n                    THEN a.desc_banco ELSE ' ' END ");
		                sbSql.append("\n    END as desc_banco, m.id_chequera, m.id_divisa, e.desc_estatus, ");
		                sbSql.append("\n    m.no_folio_det, cj.desc_caja, m.lote_entrada, m.no_docto, ");
		                sbSql.append("\n    CASE WHEN m.id_forma_pago = 1 THEN coalesce(m.b_entregado, '') ELSE '' END as b_entregado, ");
						//'sSQL = sSQL & CHR(10) &"     CASE WHEN m.id_estatus_mov in ('I', 'R') "
						//'sSQL = sSQL & CHR(10) &"          THEN m.b_entregado ELSE ' ' END as b_entregado, "
		                sbSql.append("\n    m.no_empresa, m.grupo_pago, ");
		                sbSql.append("\n    CASE WHEN m.id_divisa = 'DLS' ");
		                sbSql.append("\n         THEN importe ELSE 0 END as importe_dls, ");
		                sbSql.append("\n    CASE WHEN m.id_divisa = 'MN' ");
		                //sbSql.append("\n	THEN importe ELSE 0 END as importe_mn,m.cve_control, coalesce(to_char(m.fec_propuesta, 'yyyy'),'0') as c_periodo,m.po_headers");
		                
		                sbSql.append("\n         THEN importe ELSE 0 END as importe_mn,m.cve_control,m.c_periodo,m.po_headers ");
		                sbSql.append("\n ,convert(char(10),m.fec_propuesta,103) as fecPropuestaStr, coalesce(year(m.fec_propuesta),'0') as periodo_descompensar");
		                
		                sbSql.append("\n         ,m.id_grupo, cg.desc_grupo ,  m.id_rubro, cr.desc_rubro ,m.fec_exportacion ");
					}
					else
					{
						sbSql.append("SELECT ");
		                if((iTablas == dto.getITablaFin()) && (dto.getEstatus() != null && !dto.getEstatus().equals("")))
		                    sbSql.append("\n importe_original, ");
		                else
		                    sbSql.append("\n m.importe_original, ");
		                
		                sbSql.append("\n m.id_tipo_movto , m.id_estatus_cb, m.no_folio_det, ");
		                sbSql.append("\n m.tipo_cambio, m.no_cuenta, replace(m.no_docto,'null','') as no_docto, m.no_empresa, m.id_estatus_mov,");
		                sbSql.append("\n m.no_folio_mov, m.fec_valor,m.fec_valor_original,m.fec_alta, m.importe, m.id_divisa, m.division, ");
		                sbSql.append("\n m.referencia, m.id_forma_pago, ");
		                sbSql.append("\n CASE WHEN COALESCE(grupo_pago,0) != 0 ");
		                sbSql.append("\n      THEN 'AGRUPADO' ");
		                sbSql.append("\n      ELSE CASE WHEN COALESCE(grupo_pago,0) = 0");
		                sbSql.append("\n                    THEN 'NORMAL' ");
		                sbSql.append("\n                    ELSE '' ");
		                sbSql.append("\n               END ");
		                sbSql.append("\n END as tipopago, ");
		                sbSql.append("\n case when m.id_forma_pago in (1,2)");
		                
		                if(ConstantesSet.gsDBM.equals("SYBASE"))
		                	sbSql.append("\n     then convert(varchar,m.no_cheque) ");
		                else if (ConstantesSet.gsDBM.equals("DB2"))
		                    sbSql.append("\n     then cast(m.no_cheque as varchar(10)) ");
		                else//'Entra consulta movimiento
		                	sbSql.append("\n   then m.no_cheque");
		                     //sbSql.append("\n     then cast(m.no_cheque as varchar) ");
		                
		                //sbSql.append("\n     else  m.folio_banco end as no_cheque, ");
		                sbSql.append("\n     else 0 end as no_cheque, ");
		                sbSql.append("\n m.id_banco, m.id_banco_benef, m.id_chequera_benef, m.beneficiario,");
		                sbSql.append("\n m.id_chequera, m.importe_desglosado, m.folio_ref, m.valor_tasa, ");
		                sbSql.append("\n m.lote_entrada, m.grupo_pago, ");
		                sbSql.append("\n m.lote_salida, m.id_tipo_docto, m.concepto, m.id_caja, m.id_leyenda,");
		                sbSql.append("\n m.fec_recalculo, m.no_cliente, m.origen_mov, m.solicita, m.autoriza,");
		                sbSql.append("\n m.observacion, m.b_salvo_buen_cobro, m.fec_conf_trans, m.b_entregado,");
		                sbSql.append("\n m.fec_entregado,");
		                sbSql.append("\n case when m.id_forma_pago = 2 then ' ' else a.desc_banco end as desc_banco,");
		                sbSql.append("\n b.desc_banco as desc_banco_benef, co.desc_tipo_operacion, co.id_tipo_operacion,");
		                sbSql.append("\n cv.id_cve_operacion, cv.desc_cve_operacion, fp.desc_forma_pago, cj.desc_caja,");
		                sbSql.append("\n e.desc_estatus, no_factura,");
		                sbSql.append("\n case m.tipo_cancelacion when 'C' then 'CANCELACION'");
		                sbSql.append("\n      when 'R' then 'REGRESO' end as tipo_cancelacion, ");
		                sbSql.append("\n em.nom_empresa, m.cod_bloqueo,");

		                if(iTablas == 0)//'Entra consulta movimiento
		                    sbSql.append("\n 'M' as origen");
		                else
		                    sbSql.append("\n 'H' as origen");
		               
		                sbSql.append("\n	,m.cve_control");
		             //   sbSql.append("\n	,m.cve_control,m.c_periodo,m.po_headers");
		               // sbSql.append("\n	,m.cve_control, coalesce(to_char(m.fec_propuesta, 'yyyy'),'0') as c_periodo,m.po_headers");ya estaba comentado
//		                sbSql.append("\n  ,m.id_grupo, cg.desc_grupo ,  m.id_rubro, cr.desc_rubro, m.fec_exportacion ");
//		                sbSql.append("\n ,CONVERT(CHAR(10),m.fec_propuesta,103) as fecPropuestaStr, coalesce(year(m.fec_propuesta),'0') as periodo_descompensar");
//		                sbSql.append("\n , ( select  distinct top 1 id_clabe ");
//						sbSql.append("\n  from cat_cta_banco ccb ");
//						sbSql.append("\n where ccb.no_empresa= m.no_empresa ");
//						sbSql.append("\n and ccb.id_banco = m.id_banco ");
//						sbSql.append("\n and ccb.id_chequera= m.id_chequera");
//						sbSql.append("\n ) clabeBancaria ");
						//sbSql.append("\n  , p.equivale_persona,p.razon_social ");ya estaba comentado
					//	sbSql.append("\n  , p.equivale_persona,p.razon_social, m.fecha_contabilizacion ");
						
						//if((iTablas == 0) || (iTablas==1 && dto.getITalblaInicio()!=0) || (iTablas==2 && dto.isBEncReporte()) )ya estaba comentado
					//	sbSql.append(", m.comentario2 ");
					}
				}
				
				sbSql.append("\n FROM");
		        sbSql.append("\n cat_cve_operacion cv, empresa em, cat_tipo_operacion co, ");

		        if(iTablas == 0)//'Entra consulta movimiento
		            sbSql.append("\n movimiento m");
		        else if (iTablas == 1) 
		        {
		        	if ((dto.isBReporte() && !dto.isBSoloFolioDet()) || dto.getFolioDet() > 0 && dto.getIHist() == 0)
	                	sbSql.append("\n hist_mov_det m");
		            else if (dto.getIHist() == 1)
		                sbSql.append("\n hist_solicitud m");
		            else if (dto.getITalblaInicio() != 0)
		                sbSql.append("\n hist_movimiento m");
		            else
		                sbSql.append("\n hist_movimiento m");
		        }
		        else if (iTablas == 2 && dto.getITalblaInicio() == 1)
		            sbSql.append("\n hist_mov_det m");
		        else if (iTablas == 2 && dto.isBEncReporte())
		            sbSql.append("\n hist_movimiento m");
		        else if (iTablas == 3 && (dto.getEstatus() != null && !dto.getEstatus().equals("")))
		            sbSql.append("\n hist_mov_det m");
		        else
		            sbSql.append("\n hist_solicitud m");
		         

		        sbSql.append("\n LEFT JOIN cat_banco a  ON(m.id_banco=a.id_banco)  ");
		        sbSql.append("\n LEFT JOIN cat_banco b ON(m.id_banco_benef = b.id_banco)  ");
		        sbSql.append("\n LEFT JOIN cat_estatus e ON(m.id_estatus_mov=e.id_estatus and e.clasificacion='MOV')  ");
		        sbSql.append("\n LEFT JOIN cuenta cu ON(m.no_empresa= cu.no_empresa AND m.no_cuenta=cu.no_cuenta AND m.id_divisa=cu.id_divisa)");
		        sbSql.append("\n LEFT JOIN cat_caja cj ON(m.id_caja=cj.id_caja)");
		        sbSql.append("\n LEFT JOIN cat_forma_pago fp ON(m.id_forma_pago = fp.id_forma_pago) ");
//		        sbSql.append("\n LEFT JOIN cat_rubro cr on( m.id_rubro= cr.id_rubro)"); //no va hasta 
//                sbSql.append("\n LEFT JOIN cat_grupo cg on( m.id_grupo= cg.id_grupo)");
//                if(dto.isOptMovto())
//                sbSql.append("\n LEFT JOIN persona  p on( m.no_cliente=p.no_persona)");
//                else
//                sbSql.append("\n LEFT JOIN persona  p on( m.no_cliente=p.no_persona) and p.id_tipo_persona =  'C' ");//aqui
		        sbSql.append("\n WHERE  ");
		        sbSql.append("\n m.id_tipo_operacion = co.id_tipo_operacion");
		        sbSql.append("\n and m.id_cve_operacion = cv.id_cve_operacion");

		        if(dto.getFolioDet() <= 0 && !dto.isBReporte())
		        {
		        	if(dto.getValorCustodia() != null && dto.getValorCustodia().equals(""))
	                	sbSql.append("\n and m.id_estatus_mov not in ('H', '*')");
		            else
		            {
		            	//'Entra consulta movimiento, se repite en la dif opciones
		                if((dto.getEstatus() != null) && 
		                   (dto.getEstatus().equals("PENDIENTE") || dto.getEstatus().equals("TRANSFERIDO")
		                		   || dto.getEstatus().equals("CONF. TRANSFERIDO") || dto.getEstatus().equals("APLICADO")))
		                {
		                	sbSql.append("\n and m.id_estatus_mov in ('H', '*')");
		                }
		               /* Case "PENDIENTE"
		                    sbSql.append("\n and m.id_estatus_mov in ('H', '*')");
		                Case "TRANSFERIDO", "IMPRESO"
		                    sbSql.append("\n and m.id_estatus_mov in ('H', '*')");
		                Case "CONF. TRANSFERIDO"
		                    sbSql.append("\n and m.id_estatus_mov  in ('H', '*')");
		                Case "APLICADO"
		                    sbSql.append("\n and m.id_estatus_mov  in ('H', '*')");
		                //'FALTA IMPRESO
		                End Select*/
		            }
		            	
		            if(dto.isOptMovto()){
		            	//'Entra consulta movimiento
		                sbSql.append("\n and m.id_tipo_movto =  'E' ");
	                	//sbSql.append("\n and p.id_tipo_persona =  'P' ");
		            }else{
		                sbSql.append("\n and m.id_tipo_movto =  'I' ");
		            }
		        }
		        
		        sbSql.append("\n and em.no_empresa = m.no_empresa");
		        		        

		        if (dto.getNoEmpresa() != 0)
		        	sbSql.append("\n and m.no_empresa in (" + dto.getNoEmpresa() + ")");
		        /*if(dto.getSEmpresas() != null && !dto.getSEmpresas().equals(""))
		            sbSql.append("\n and m.no_empresa in (" + dto.getSEmpresas() + ")");*/

		        //' >>>>>>>>>>>> CRITERIOS
		        if(dto.getIdFormaPago() > 0)
		            sbSql.append("\n and m.id_forma_pago = " + dto.getIdFormaPago());

		        if(dto.getIdDivisa() != null && !dto.getIdDivisa().equals(""))
		            sbSql.append("\n and m.id_divisa = '" + Utilerias.validarCadenaSQL(dto.getIdDivisa()) + "'");

		        if(dto.getIdDivision() > 0)
		            sbSql.append("\n and m.division = '" + Utilerias.validarCadenaSQL(dto.getIdDivision()) + "'");
//		        
//		        if(dto.getFechaInicial() != null && !dto.getFechaInicial().equals("")){
//		            sbSql.append("\n And ((M.Fecha_Contabilizacion Is Not Null And  M.Fecha_Contabilizacion >= convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaInicial()) + "',103) )");
//		            sbSql.append("\n  Or (M.Fecha_Contabilizacion Is Null And M.Fec_Valor >= convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaInicial()) + "', 103) )");
//		            sbSql.append("\n )");
//		        }
//		        if(dto.getFechaFinal() != null && !dto.getFechaFinal().equals("")){
//		             sbSql.append("\n And ((M.Fecha_Contabilizacion Is Not Null And  M.Fecha_Contabilizacion <= convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFinal()) + "', 103) )");
//		             sbSql.append("\n  Or (M.Fecha_Contabilizacion Is Null And M.Fec_Valor <= convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFinal()) + "', 103) )");
//		             sbSql.append("\n )");
//		        }
		        
		        
		        if(dto.getFechaInicial() != null && !dto.getFechaInicial().equals("")){
		            sbSql.append("\n  And M.Fec_Valor >= convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaInicial()) + "', 103) ");
	
		        }
		        if(dto.getFechaFinal() != null && !dto.getFechaFinal().equals("")){
		             sbSql.append("\n And M.Fec_Valor <= convert(datetime,'" + funciones.ponerFechaSola(dto.getFechaFinal()) + "', 103) ");
		            
		        }
		        
		        if((dto.getEstatus() != null && !dto.getEstatus().equals("")) && (dto.getValorCustodia() != null && dto.getValorCustodia().equals("")))
		            sbSql.append("\n and m.id_estatus_mov = '" + Utilerias.validarCadenaSQL(dto.getEstatus()) + "'");
		      
		        if(dto.getTipoOperacion() > 0)
		        {
		        	switch(dto.getTipoOperacion())
		        	{
		        		case 31:
		        			sbSql.append("\n and m.id_cve_operacion in (31,34,50) and  m.id_estatus_mov <> 'S'  ");
		        		break;
		        		case 34:
		        			sbSql.append("\n and (m.id_cve_operacion = 34 or m.id_estatus_mov = 'S') ");
		        		break;
		        		default:
		        			sbSql.append("\n and m.id_cve_operacion = " + Utilerias.validarCadenaSQL(dto.getTipoOperacion()));
		        		break;
		        	}
		        }
		        
		        if(dto.getMontoInicial() > 0)
		        	sbSql.append("\n and m.importe >= " + dto.getMontoInicial());

		        if(dto.getMontoFinal() > 0)
		            sbSql.append("\n and m.importe <= " + dto.getMontoFinal());

		        if(dto.getIdBanco() > 0)
		            sbSql.append("\n and m.id_banco = " + dto.getIdBanco());

		        if(dto.getIdChequera() != null && !dto.getIdChequera().equals(""))
		            sbSql.append("\n and m.id_chequera = '" + Utilerias.validarCadenaSQL(dto.getIdChequera()) + "'");

		        if(dto.getFolioMovimiento() > 0)
		            sbSql.append("\n and m.no_folio_det = " + dto.getFolioMovimiento());

		        if(dto.getConcepto() != null && !dto.getConcepto().equals(""))
		            sbSql.append("\n and m.concepto like '%" + Utilerias.validarCadenaSQL(dto.getConcepto()) + "%'");

		        if(dto.getCvePropuesta() != null && !dto.getCvePropuesta().equals(""))
		            sbSql.append("\n and m.cve_control = '" + Utilerias.validarCadenaSQL(dto.getCvePropuesta()) + "'");

		        if(dto.getValorCustodia() != null && !dto.getValorCustodia().equals("") && funciones.isNumeric(dto.getValorCustodia()))//'Entra consulta movimiento
		            sbSql.append("\n and m.grupo_pago = " + Utilerias.validarCadenaSQL(dto.getValorCustodia()) + "");

		        if(dto.getNoDocto() != null && !dto.getNoDocto().equals(""))
		            sbSql.append("\n and m.no_docto like '" + Utilerias.validarCadenaSQL(dto.getNoDocto()) + "%'");

		        if(dto.getNoFactura() > 0)
		            sbSql.append("\n and m.no_factura  like '" + dto.getNoFactura() + "%'");

		        if(dto.getNoCheque() > 0) 
		            sbSql.append("\n and m.no_cheque  in (" + dto.getNoCheque() + ")");
		        
		        if(dto.getBeneficiario() != null && !dto.getBeneficiario().equals("") && dto.getIdProveedor() > 0)
		        {
		        	sbSql.append("\n and (m.beneficiario like '%" + Utilerias.validarCadenaSQL(dto.getBeneficiario()) + "%' ");
		            //'Se agrega numero de cliente de las funciones del query se debe declarar una variable
		            sbSql.append("\n or m.no_cliente_ant = '" + dto.getIdProveedor() + "')");
		        } 
		        else if(dto.getBeneficiario() != null && !dto.getBeneficiario().equals(""))
		        {
		        	sbSql.append("\n and m.beneficiario like '%" + Utilerias.validarCadenaSQL(dto.getBeneficiario()) + "%' ");
		           
		        }
		        else if(dto.getIdProveedor() > 0)
		        {
		        	sbSql.append("\n and m.no_cliente_ant = '" + dto.getIdProveedor() + "'");
		        }

		        if(dto.getIdCaja() > 0)
		            sbSql.append("\n and m.id_caja = " + dto.getIdCaja());

		        if(dto.getOrigen() != null && !dto.getOrigen().equals("") && dto.getOrigen().equals("CXP"))
                	sbSql.append("\n AND (m.origen_mov = '' or m.origen_mov is NULL)");
	        	else if(dto.getOrigen() != null && !dto.getOrigen().equals(""))
                	sbSql.append("\n AND m.origen_mov = '" + Utilerias.validarCadenaSQL(dto.getOrigen()) + "'");
		            
		        if(dto.getValorCustodia() != null && !dto.getValorCustodia().equals(""))
		        {
		        	if(dto.isOptMovto())
		        	{
		        		sbSql.append("\n  AND ( m.id_tipo_operacion = 3201 ");
		                sbSql.append("\n  or ( m.id_tipo_operacion = 3701 ");
		                sbSql.append("\n        and m.id_estatus_mov = '*' ");
		                sbSql.append("\n        and m.id_tipo_movto = 'E'  ");
	                //	sbSql.append("\n and p.id_tipo_persona =  'P' ) ) ");//no va
		        	}
		        	else
		        	{
		        		sbSql.append("\n  AND ( m.id_tipo_operacion = 3201 ");
		                sbSql.append("\n  or ( m.id_tipo_operacion = 3701 ");
		                sbSql.append("\n        and m.id_estatus_mov = '*' ");
		                sbSql.append("\n        and m.id_tipo_movto = 'I' ");
	                //	sbSql.append("\n and p.id_tipo_persona =  'C' ) ) "); //no va
		        	} 
		        }

		        if(dto.getFolioDet() > 0 && !dto.isBReporte() && dto.getValorCustodia() != null && !dto.getValorCustodia().equals("")) 
		        {
		        	sbSql.append("\n AND m.grupo_pago = " + dto.getFolioDet());
		            if(iTablas == 1)
		            {
		            	if(ConstantesSet.gsDBM.equals("SQL SERVER"))
		            		sbSql.append("\n ORDER BY co.id_tipo_operacion ");
		            	else
		            		sbSql.append("\n ORDER BY id_tipo_operacion ");
		            }
		        }
		            
		        if(dto.isBSoloFolioDet())
		            sbSql.append("\n AND COALESCE(grupo_pago,0) != 0 ");
		        
		        
		        if(dto.getPooHeaders() != null && !dto.getPooHeaders().equals("")){ //no va
		        	sbSql.append("\n AND po_headers = '" + dto.getPooHeaders() +"'"); //va arriba en el otro if y se le asigna 'A'
		        }
		        //'<<<<<<<<<<<< CRITERIOS

		        if(iTablas == 0)
		        {
		        	if(dto.getITalblaInicio() != dto.getITablaFin())
	                	sbSql.append("\n UNION ALL \n");
		        }
		        else if(iTablas == 1 && dto.getITalblaInicio() != dto.getITablaFin())
		        {
		        	if(dto.getITalblaInicio() != dto.getITablaFin() - 1)
		        		sbSql.append("\n UNION ALL \n");
		        	else if(iTablas != dto.getITablaFin())
	                	sbSql.append("\n UNION ALL \n");
		        }
		        else if(iTablas == 1 && dto.getITablaFin() == 2 && dto.isBReporte())
		            sbSql.append("\n UNION ALL \n");
		        else if(iTablas == 2 && dto.getITablaFin() > iTablas) 
		            sbSql.append("\n UNION ALL \n");
			}
		}
		catch(Exception e){
			logger.error(e);
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:ConsultasDao, M:armarConsultaMovimientos");
		}
		return sbSql;
	}
	
	/**
	 * Este m\u00e9todo consulta las tablas de, movimiento, hist_mov_det, hist_solicitud,
	 * es utilizado en 
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<Map<String, Object>> consultarMovimientos(StringBuffer sbSql, boolean bReporte){
		List<Map<String, Object>> listConsMovi  = new ArrayList<Map<String, Object>>();
		try
		{
			if(bReporte)
			{
				System.out.println("-----------------Reporte---------------------");
				System.out.println(sbSql);
				System.out.println("-----------------Reporte---------------------");
				logger.info("consultarMovimientos: " + sbSql);
				listConsMovi = jdbcTemplate.query(sbSql.toString(), new RowMapper<Map<String, Object>>(){
					public Map<String, Object> mapRow(ResultSet rs, int idx)throws SQLException{
						Map<String, Object> map = new HashMap<String, Object>();
							map.put("importeOriginal", rs.getDouble("importe_original"));
							map.put("fecValor", rs.getDate("fec_valor"));
							map.put("fecValorOriginal", rs.getDate("fec_valor_original"));
							map.put("fecAlta", rs.getDate("fec_alta"));
							map.put("noCliente", rs.getString("no_cliente"));
							map.put("beneficiario", rs.getString("beneficiario"));
							map.put("concepto", rs.getString("concepto"));
							map.put("importe", rs.getDouble("importe"));
							map.put("descFormaPago", rs.getString("desc_forma_pago"));
							map.put("idChequeraBenef", rs.getString("id_chequera_benef"));
							map.put("descBanco", rs.getString("desc_banco"));
							map.put("idChequera", rs.getString("id_chequera"));
							map.put("idDivisa", rs.getString("id_divisa"));
							map.put("descEstatus", rs.getString("desc_estatus"));
							map.put("noFolioDet", rs.getInt("no_folio_det"));
							map.put("descCaja", rs.getString("desc_caja"));
							map.put("loteEntrada", rs.getInt("lote_entrada"));
							map.put("noDocto", rs.getString("no_docto"));
							map.put("bEntregado", rs.getString("b_entregado"));
							map.put("noEmpresa", rs.getInt("no_empresa"));
							map.put("grupoPago", rs.getInt("grupo_pago"));
							map.put("importeDls", rs.getBigDecimal("importe_dls").doubleValue());
							map.put("importeMn", rs.getBigDecimal("importe_mn").doubleValue());
							map.put("cveControl", rs.getString("cve_control"));
						//	map.put("periodo", rs.getString("c_periodo"));
							
						return map;
						/*MovimientoDto dtoCons = new MovimientoDto();
							dtoCons.setImporteOriginal(rs.getDouble("importe_original"));
							dtoCons.setFecValor(rs.getDate("fec_valor"));
							dtoCons.setFecValorOriginal(rs.getDate("fec_valor_original"));
							dtoCons.setFecAlta(rs.getDate("fec_alta"));
							dtoCons.setNoCliente(rs.getString("no_cliente"));
							dtoCons.setBeneficiario(rs.getString("beneficiario"));
							dtoCons.setConcepto(rs.getString("concepto"));
							dtoCons.setImporte(rs.getDouble("importe"));
							dtoCons.setDescFormaPago(rs.getString("desc_forma_pago"));
							dtoCons.setDescBancoBenef(rs.getString("desc_banco_benef"));
							dtoCons.setIdChequeraBenef(rs.getString("id_chequera_benef"));
							dtoCons.setDescBanco(rs.getString("desc_banco"));
							dtoCons.setIdChequera(rs.getString("id_chequera"));
							dtoCons.setIdDivisa(rs.getString("id_divisa"));
							dtoCons.setDescEstatus(rs.getString("desc_estatus"));
							dtoCons.setNoFolioDet(rs.getInt("no_folio_det"));
							dtoCons.setDescCaja(rs.getString("desc_caja"));
							dtoCons.setLoteEntrada(rs.getInt("lote_entrada"));
							dtoCons.setNoDocto(rs.getString("no_docto"));
							dtoCons.setBEntregado(rs.getString("b_entregado"));
							dtoCons.setNoEmpresa(rs.getInt("no_empresa"));
							dtoCons.setGrupoPago(rs.getInt("grupo_pago"));
							dtoCons.setImporteDls(rs.getBigDecimal("importe_dls").doubleValue());
							dtoCons.setImporteMn(rs.getBigDecimal("importe_mn").doubleValue());
							dtoCons.setCveControl(rs.getString("cve_control"));
						return dtoCons;*/
					}
				});
			}
			else
			{
				System.out.println("no reporte");
				System.out.println(sbSql.toString());
				listConsMovi = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
					public MovimientoDto mapRow(ResultSet rs, int idx)throws SQLException{
						
						MovimientoDto dtoCons = new MovimientoDto();
							dtoCons.setImporteOriginal(rs.getDouble("importe_original"));
							dtoCons.setIdTipoMovto(rs.getString("id_tipo_movto"));
							dtoCons.setIdEstatusCb(rs.getString("id_estatus_cb"));
							dtoCons.setNoFolioDet(rs.getInt("no_folio_det"));
							dtoCons.setTipoCambio(rs.getDouble("tipo_cambio"));
							dtoCons.setNoCuenta(rs.getInt("no_cuenta"));
							dtoCons.setNoDocto(rs.getString("no_docto"));
							dtoCons.setNoEmpresa(rs.getInt("no_empresa"));
							dtoCons.setIdEstatusMov(rs.getString("id_estatus_mov"));
							dtoCons.setNoFolioMov(rs.getInt("no_folio_mov"));
							dtoCons.setFecValor(rs.getDate("fec_valor"));
							dtoCons.setFecValorOriginal(rs.getDate("fec_valor_original"));
							dtoCons.setFecAlta(rs.getDate("fec_alta"));
							dtoCons.setImporte(rs.getDouble("importe"));
							dtoCons.setIdDivisa(rs.getString("id_divisa"));
							dtoCons.setDivision(rs.getString("division"));
							dtoCons.setReferencia(rs.getString("referencia"));
							dtoCons.setIdFormaPago(rs.getInt("id_forma_pago"));
							dtoCons.setPoHeaders(rs.getString("tipopago"));
							dtoCons.setNoCheque(rs.getInt("no_cheque"));
							dtoCons.setIdBanco(rs.getInt("id_banco"));
							dtoCons.setIdBancoBenef(rs.getInt("id_banco_benef"));
							dtoCons.setIdChequeraBenef(rs.getString("id_chequera_benef"));
							dtoCons.setBeneficiario(rs.getString("beneficiario"));
							dtoCons.setIdChequera(rs.getString("id_chequera"));
							dtoCons.setImporteDesglosado(rs.getDouble("importe_desglosado"));
							dtoCons.setFolioRef(rs.getInt("folio_ref"));						
							dtoCons.setValorTasa(rs.getDouble("valor_tasa"));
							dtoCons.setLoteEntrada(rs.getInt("lote_entrada"));
							dtoCons.setGrupoPago(rs.getInt("grupo_pago"));
							dtoCons.setLoteSalida(rs.getInt("lote_salida"));
							dtoCons.setIdTipoDocto(rs.getInt("id_tipo_docto"));
							dtoCons.setConcepto(rs.getString("concepto"));
							dtoCons.setIdCaja(rs.getInt("id_caja"));
							dtoCons.setIdLeyenda(rs.getString("id_leyenda"));
							dtoCons.setFecRecalculo(rs.getDate("fec_recalculo"));
							dtoCons.setNoCliente(rs.getString("no_cliente"));
							dtoCons.setOrigenMov(rs.getString("origen_mov"));
							dtoCons.setSolicita(rs.getString("solicita"));
							dtoCons.setAutoriza(rs.getString("autoriza"));
							dtoCons.setObservacion(rs.getString("observacion"));
							dtoCons.setBSalvoBuenCobro(rs.getString("b_salvo_buen_cobro"));
							dtoCons.setFecConfTrans(rs.getDate("fec_conf_trans"));
							dtoCons.setBEntregado(rs.getString("b_entregado"));
							dtoCons.setFecEntregado(rs.getDate("fec_entregado"));
							//desc_banco
							dtoCons.setDescBancoBenef(rs.getString("desc_banco_benef"));
							//desc_tipo_operacion
							dtoCons.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
							dtoCons.setIdCveOperacion(rs.getInt("id_cve_operacion"));
							dtoCons.setDescCveOperacion(rs.getString("desc_cve_operacion"));
							dtoCons.setDescFormaPago(rs.getString("desc_forma_pago"));
							dtoCons.setDescCaja(rs.getString("desc_caja"));
							dtoCons.setDescEstatus(rs.getString("desc_estatus"));
							dtoCons.setNoFactura(rs.getString("no_factura"));
							dtoCons.setTipoCancelacion(rs.getString("tipo_cancelacion"));
							dtoCons.setNomEmpresa(rs.getString("nom_empresa"));
							dtoCons.setCodBloqueo(rs.getString("cod_bloqueo"));
							dtoCons.setOrigen(rs.getString("origen"));
							dtoCons.setCveControl(rs.getString("cve_control"));
							
							//dtoCons.setCPeriodo(rs.getInt("c_periodo"));
							//dtoCons.setPoHeaders(rs.getString("po_headers"));
							dtoCons.setDescBanco(rs.getString("desc_banco"));
							//29.01.2016 
							//dtoCons.setIdRubro(rs.getString("id_rubro"));
						//	dtoCons.setIdGrupo(rs.getString("id_grupo"));
							//dtoCons.setDescGrupo(rs.getString("desc_grupo"));
							//dtoCons.setDescRubro(rs.getString("desc_rubro"));
							//dtoCons.setFecExportacion(rs.getDate("fec_exportacion"));
							//24.02.2016
							//dtoCons.setEquivalePersona(rs.getString("equivale_persona"));
							//dtoCons.setNomProveedor(rs.getString("razon_social"));
							//26.03.2016
							//dtoCons.setClabe(rs.getString("clabeBancaria"));
							//29.03.2016
							//dtoCons.setPeridoDescompensar(rs.getString("periodo_descompensar"));
							//dtoCons.setFecPropuestaStr(rs.getString("fecPropuestaStr"));
							//Agregar fecha_conta
							//dtoCons.setFechaContabilizacion(rs.getDate("fecha_contabilizacion"));
							
							try{
								//dtoCons.setComentario2(rs.getString("comentario2"));
							}
							catch(Exception e){
								dtoCons.setComentario2("");
							}
						return dtoCons;
					}
				});
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasDao, M:consultarMovimientos");
			e.printStackTrace();
		}
		return listConsMovi;
	}
	
	/**
	 * Consulta todos los movimientos en la tabla movimiento, por un rango de fechas y empresa.
	 * No considera los movimientos que se utilizan para el inventario.
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<Map<String, Object>> graficarTotalMovimientos(int noEmpresa, String sFecIni, String sFecFin)
	{
		List<Map<String, Object>> listConsMovi  = new ArrayList<Map<String, Object>>();
		
		try
		{
			StringBuffer sbSql = new StringBuffer();
			sbSql.append("select no_folio_det, no_folio_mov, id_tipo_operacion, id_tipo_movto, id_chequera, importe, importe_original, fec_valor, fec_operacion, fec_valor_original ");
			sbSql.append("from movimiento ");
			sbSql.append("where fec_valor >= CONVERT(DATE, '"+Utilerias.validarCadenaSQL(sFecIni)+"', 103) ");
			sbSql.append("and fec_valor <= CONVERT(DATE, '"+Utilerias.validarCadenaSQL(sFecFin)+"', 103) ");
			sbSql.append("and id_tipo_operacion not in(2001, 2201, 3001, 3201) ");
			
			if(noEmpresa!=0)
				sbSql.append("and no_empresa = "+noEmpresa);
			
			listConsMovi = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
				public MovimientoDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					
					MovimientoDto dtoCons = new MovimientoDto();
						dtoCons.setNoFolioDet(rs.getInt("no_folio_det"));
						dtoCons.setNoFolioMov(rs.getInt("no_folio_mov"));
						dtoCons.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
						dtoCons.setIdTipoMovto(rs.getString("id_tipo_movto"));
						dtoCons.setIdChequera(rs.getString("id_chequera"));
						dtoCons.setImporteOriginal(rs.getDouble("importe_original"));
						dtoCons.setImporte(rs.getDouble("importe"));
						dtoCons.setFecValor(rs.getDate("fec_valor"));
						dtoCons.setFecOperacion(rs.getDate("fec_operacion"));
						dtoCons.setFecValorOriginal(rs.getDate("fec_valor_original"));
					return dtoCons;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasDao, M:consultarMovimientos");
			e.printStackTrace();
		}
		return listConsMovi;
	}
	
	/**
	 * M\u00e9todo para llamar al procedimiento almacenado revividor.
	 * @param dtoRev
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public Map<String,Object> ejecutarRevividor(RevividorDto dtoRev){
		//StringBuffer sbParametro = new StringBuffer();
		//String mensajeResp = "inicia revividor";
		String resultadoRevividor = "";		
		int result = 0;
		Map<String,Object> resultado = new HashMap<String,Object>();
		try{
			/*
			System.out.println(dtoRev.getNoFolioDet() + " folioDet");
			System.out.println(dtoRev.getPsTipoCancelacion() + " tipocancela");			
			System.out.println(dtoRev.getIdTipoOperacion() + " tipo operacion");
			System.out.println(dtoRev.getIdEstatusMov() + "estatusMov");
			System.out.println(dtoRev.getPsOrigenMov() + " origenMov");
			System.out.println(dtoRev.getIdFormaPago() + " formaPago");
			System.out.println(dtoRev.getBEntregado() + " bEntregado");
			System.out.println(dtoRev.getIdTipoMovto() + " idTipoMovto");
			System.out.println(dtoRev.getImporte() + " importe");
			System.out.println(dtoRev.getNoEmpresa() + " noEmpresa");			
			System.out.println(dtoRev.getNoCuenta() + " noCuenta");
			System.out.println(dtoRev.getIdChequera() + " idChequera");
			System.out.println(dtoRev.getIdBanco() + " idBanco");
			System.out.println(dtoRev.getIdUsuario() + "idUsuario");
			System.out.println(dtoRev.getNoDocto() + " noDocto");
			System.out.println(dtoRev.getLote() + " lote");
			System.out.println(dtoRev.getBSalvoBuenCobro() + " bSalvoBC");
			System.out.println(dtoRev.getFecConfTrans() + "fecConfi");
			System.out.println(dtoRev.getIdDivisa() + " idDivisa");*/
			/*sbParametro.append(dtoRev.getNoFolioDet() + "," + dtoRev.getIdTipoOperacion() + ",");
			sbParametro.append(dtoRev.getPsTipoCancelacion() + "," + dtoRev.getIdEstatusMov() + ",");
			sbParametro.append(dtoRev.getPsOrigenMov() + "," + dtoRev.getIdFormaPago() + ",");
			sbParametro.append(dtoRev.getBEntregado() + "," + dtoRev.getIdTipoMovto() + ",");
			sbParametro.append(dtoRev.getImporte() + "," + dtoRev.getNoEmpresa() + ",");
			sbParametro.append(dtoRev.getNoCuenta() + "," + dtoRev.getIdChequera() + ",");
			sbParametro.append(dtoRev.getIdBanco() + "," + dtoRev.getIdUsuario() + ",");
			sbParametro.append(dtoRev.getNoDocto() + "," + dtoRev.getLote() + ",");
			sbParametro.append(dtoRev.getBSalvoBuenCobro() + "," + dtoRev.getFecConfTrans() + ",");
			sbParametro.append(dtoRev.getIdDivisa());
			*/
		
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);			
			
			/*
			 *
			 *SE IMPLEMENTA NUEVA FORMA DE LLAMAR AL SP_REVIVIDOR ...
			 *COMENTO ORLANDO RMZ 19-11-13...
			 *
			resultado = consultasGenerales.ejecutarRevividor(dtoRev.getPsRevividor(), dtoRev.getNoFolioDet(),  dtoRev.getIdTipoOperacion(),
			dtoRev.getPsTipoCancelacion(), dtoRev.getIdEstatusMov(), dtoRev.getPsOrigenMov(), dtoRev.getIdFormaPago(), 
			dtoRev.getBEntregado(), dtoRev.getIdTipoMovto(), dtoRev.getImporte(), dtoRev.getNoEmpresa(), 
			dtoRev.getNoCuenta(), dtoRev.getIdChequera(), dtoRev.getIdBanco(), dtoRev.getIdUsuario(), 
			dtoRev.getNoDocto() + "", dtoRev.getLote(), dtoRev.getBSalvoBuenCobro(), dtoRev.getFecConfTrans(), 
			dtoRev.getIdDivisa(), resultadoRevividor, false);
			*/
		
			/*Antes de ejecutar el revividor se inserta el una observacion en el movimiento*/
			int motivo= insertarMotivo(dtoRev.getObservaciones(), dtoRev.getNoFolioDet(), dtoRev.getOrigen());
			if(motivo!=0){
				result = consultasGenerales.ejecutarRevividorOR(dtoRev.getPsRevividor(), dtoRev.getNoFolioDet(),  dtoRev.getIdTipoOperacion(),
						dtoRev.getPsTipoCancelacion(), dtoRev.getIdEstatusMov(), dtoRev.getPsOrigenMov(), dtoRev.getIdFormaPago(), 
						dtoRev.getBEntregado(), dtoRev.getIdTipoMovto(), dtoRev.getImporte(), dtoRev.getNoEmpresa(), 
						dtoRev.getNoCuenta(), dtoRev.getIdChequera(), dtoRev.getIdBanco(), dtoRev.getIdUsuario(), 
						dtoRev.getNoDocumento() + "", dtoRev.getLote(), dtoRev.getBSalvoBuenCobro(), dtoRev.getFecConfTrans(), 
						dtoRev.getIdDivisa(), resultadoRevividor, false);		
				
				if(result>0)				
					resultado.put("result","0");
				else				
					resultado.put("result","1");
			}else{
				resultado.put("result","0");
			}
								
			/*String psRevividor, int noFolioDet, int idTipoOperacion,String psTipoCancelacion,
			String idEstatusMov, String psOrigenMov, int idFormaPago, String bEntregado, String idTipoMovto, double importe,
			int noEmpresa, int noCuenta, String idChequera, int idBanco,int idUsuario, String noDocto, int lote,
			String bSalvoBuenCobro, String fecConfTrans, String idDivisa, String psResultado, boolean pbAutomatico
			*/
			
			//System.out.println("antes de  a ejecuta revividor"); ORR
			/*StoredProcedure storedProcedure = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_revividor") {};
			
			storedProcedure.declareParameter(new SqlParameter("parametro",Types.VARCHAR ));
			storedProcedure.declareParameter(new SqlInOutParameter("result",Types.INTEGER));
			storedProcedure.declareParameter(new SqlInOutParameter("mensaje",Types.VARCHAR ));
		
			HashMap< Object, Object > inParams = new HashMap< Object, Object >();

			inParams.put( "parametro",sbParametro.toString());
			inParams.put( "result",result);
			inParams.put( "mensaje",mensajeResp);
			resultado = storedProcedure.execute((Map)inParams);		
			
			System.out.println(resultado + " lo q se obtiene de resultado");
			 * 
			 */
			
		}
		
		catch (Exception e) {
			System.out.println("ERROR EN EL DAO");
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
			+  "P:Consultas, C:ConsultasDao, M:consultarMovimientos	U: " + dtoRev.getIdUsuario() + ", F: " + dtoRev.getNomForma());
			e.printStackTrace();
		}
		return resultado;
	}
	
	/**
	 * Este m\u00e9todo realiza un insert sobre la tabla bitacora_cheques, para realizar
	 * el regreso de la operaci\u00f2n, es utilizado en ConsultaDeMovimientos
	 * FunSQLInsert_bitacora_cheques
	 * @param dtoChe
	 * @return
	 */
	public int insertarBitacoraCheques(BitacoraChequesDto dtoChe){
		int iRegAfec = 0;
		StringBuffer sbSql = new StringBuffer();
		try{
			if(ConstantesSet.gsDBM.equals("SQL SERVER"))
			{
				sbSql.append("INSERT INTO bitacora_cheques ");
		        sbSql.append("\n       SELECT  no_empresa, " + dtoChe.getNoFolioDet() + ", id_banco,  id_chequera, ");
		        sbSql.append("\n               no_cheque, importe, '" + Utilerias.validarCadenaSQL(dtoChe.getIdEstatus()) + "', id_divisa, fec_alta, ");
		        sbSql.append("\n               usuario_modif, no_folio_det, " + Utilerias.validarCadenaSQL(dtoChe.getCausa()) + " , no_folio_mov, CURRENT DATE, ");
		        sbSql.append("\n               id_caja, beneficiario, concepto, no_docto, fec_cheque ");
		        sbSql.append("\n       FROM    movimiento ");
		        sbSql.append("\n       WHERE   (no_folio_det = " + Utilerias.validarCadenaSQL(dtoChe.getNoFolioDet()) + " OR no_folio_det = (select folio_ref ");
		        sbSql.append("\n                                                                       from movimiento ");
		        sbSql.append("\n                                                                       where no_folio_det = " + dtoChe.getNoFolioDet() + ")) ");
		        sbSql.append("\n               AND no_empresa = " + dtoChe.getNoEmpresa() + " AND id_banco = " + dtoChe.getIdBanco());
		        sbSql.append("\n               AND id_chequera = '" + Utilerias.validarCadenaSQL(dtoChe.getIdChequera()) + "' AND no_cheque = " + dtoChe.getNoCheque());
		        sbSql.append("\n       ORDER BY fec_valor DESC fetch first 1 rows only");
			}
			else
			{
				/*sbSql.append("INSERT INTO bitacora_cheques ");
				sbSql.append("\n       SELECT  no_empresa, " + dtoChe.getNoFolioDet() + ", id_banco,  id_chequera, ");
	            sbSql.append("\n               no_cheque, importe, '" +Utilerias.validarCadenaSQL( dtoChe.getIdEstatus()) + "', id_divisa, fec_alta, ");
	            sbSql.append("\n               usuario_modif, no_folio_det, " + Utilerias.validarCadenaSQL(dtoChe.getCausa()) + " , no_folio_mov, getdate(), ");
	            sbSql.append("\n               id_caja, beneficiario, concepto, no_docto, fec_cheque ");
	            sbSql.append("\n       FROM    movimiento ");
	            sbSql.append("\n       WHERE   (no_folio_det = " + dtoChe.getNoFolioDet() + " OR no_folio_det = (select top 1 folio_ref ");
	            sbSql.append("\n                                                                       from movimiento ");
	            sbSql.append("\n                                                                       where no_folio_det = " + dtoChe.getNoFolioDet() + ")) ");
	            sbSql.append("\n               AND no_empresa = " + dtoChe.getNoEmpresa() + " AND id_banco = " + dtoChe.getIdBanco());
	            sbSql.append("\n               AND id_chequera = '" + Utilerias.validarCadenaSQL(dtoChe.getIdChequera()) + "' AND no_cheque = " + dtoChe.getNoCheque());
	            sbSql.append("\n      		   ORDER BY fec_valor DESC ");*/
			}
			System.out.println("=================="+sbSql.toString());
			iRegAfec = jdbcTemplate.update(sbSql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasDao, M:insertarBitacoraCheques");
		}
		return iRegAfec;
	}
	
	public int modificarMovimientoArchivo(int iNoFolioDet){
		StringBuffer sbSql = new StringBuffer();
		List<DetArchTransferDto> listArchTrans = new ArrayList<DetArchTransferDto>(); 
		String sNomArch = "";
		double uImporte = 0;
		int iRegAfec = 0;
		try{
			sbSql.append("Select importe,nom_arch from det_arch_transfer ");
	        sbSql.append("\n WHERE (no_folio_det = " + iNoFolioDet + " or no_folio_det in (select folio_ref  from movimiento where no_folio_det = " + iNoFolioDet + " ))");
	        sbSql.append("\n and id_estatus_arch = 'T' ");
	        
	        listArchTrans = jdbcTemplate.query(sbSql.toString(), new RowMapper<DetArchTransferDto>(){
	        	public DetArchTransferDto mapRow(ResultSet rs, int idx)throws SQLException
	        	{
	        		DetArchTransferDto dtoCons = new DetArchTransferDto();
	        			dtoCons.setImporte(rs.getBigDecimal("importe").doubleValue());
	        			dtoCons.setNomArch(rs.getString("nom_arch"));
	        		return dtoCons;
	        	}
	        });
	        
	        if(listArchTrans.size() > 0)
	        {
	        	uImporte = listArchTrans.get(0).getImporte();
	        	sNomArch = listArchTrans.get(0).getNomArch();
	        }
	        if(sNomArch.equals(""))
	        	return 0;
	       
	        //'cancelar los movimientos en det_arch_transfer
	        sbSql = new StringBuffer();
	        sbSql.append(" UPDATE det_arch_transfer set id_estatus_arch = 'X' ");
	        sbSql.append("\n WHERE no_folio_det = " + iNoFolioDet);
	        
	        if(ConstantesSet.gsDBM.equals("DB2"))
	        {
	        	iRegAfec = jdbcTemplate.update(sbSql.toString());
	        	sbSql = new StringBuffer();
	        }
	        else
	        	sbSql.append(" ;");
	        
	        //'Actualizar el importe en arch_transfer
	        sbSql.append("\n UPDATE arch_transfer set  importe = importe - " + uImporte);
	        sbSql.append("\n ,registros = registros - 1 ");
	        sbSql.append("\n WHERE nom_arch = '" + sNomArch + "'");    
	        
	        if(ConstantesSet.gsDBM.equals("DB2"))
	        {
	        	iRegAfec = jdbcTemplate.update(sbSql.toString());
	        	sbSql = new StringBuffer();
	        }
	        else
	        	sbSql.append(" ;");
	        
	        //'Actualizar el estatus del movimiento
	        sbSql.append("\n UPDATE movimiento ");
	        sbSql.append("\n SET id_estatus_mov = 'P' ");
	        sbSql.append("\n WHERE id_tipo_operacion = 3200 ");
	        sbSql.append("\n and no_folio_det = " + iNoFolioDet);
	        
	        iRegAfec = jdbcTemplate.update(sbSql.toString());
	        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasDao, M:modificarMovimientoArchivo");
		}
		return iRegAfec;
	}
	
	/**
	 * FunSelectRegistro
	 * @param iNoFolioDet
	 * @param iIdBanco
	 * @return
	 */
	public int buscarRegDetArchTransfer(int iNoFolioDet, int iIdBanco)
	{
		StringBuffer sbSql = new StringBuffer();
		int iRegCount = 0;
		try{
		    
			sbSql.append("select count(*)");
			sbSql.append("\n from det_arch_transfer");
			sbSql.append("\nwhere no_folio_det = " + iNoFolioDet + " And id_banco = " + iIdBanco + "");
			sbSql.append("\n and id_estatus_arch <> 'X' ");
			
			iRegCount = jdbcTemplate.queryForInt(sbSql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasDao, M:buscarRegDetArchTransfer");
		}
		return iRegCount;
	}
	public List<LlenaComboGralDto>consultarProveedores(String texto)
	{
		StringBuffer sql = new StringBuffer();
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		try
		{
			
			sql.append( "SELECT p.equivale_persona as ID, ");
			sql.append( "       case when p.pers_juridica = 'F' then rtrim(ltrim(p.razon_social +' '+ p.nombre + ' ' + p.paterno  + ' ' + p.materno)) ");
			sql.append( "       else rtrim(ltrim(p.razon_social)) end as Descrip ");
			sql.append( "  FROM persona p");
			sql.append( " WHERE p.id_tipo_persona = 'P' ");
			sql.append( "   AND (p.razon_social like '" + Utilerias.validarCadenaSQL(texto) + "%'");
			sql.append( "    OR p.nombre + ' ' + p.paterno  + ' ' + p.materno like '" + Utilerias.validarCadenaSQL(texto) + "%')");
			
			/*if(ConstantesSet.gsDBM.equals("ORACLE") || ConstantesSet.gsDBM.equals("POSTGRESQL"))
			{
				sql.append( "SELECT p.no_persona as ID, ");
				sql.append( "       case when p.pers_juridica = 'F' then rtrim(ltrim(p.nombre || ' ' || p.paterno  || ' ' || p.materno)) ");
				sql.append( "       else rtrim(ltrim(p.razon_social)) end as Descrip ");
				sql.append( "  FROM persona p");
				sql.append( " WHERE p.id_tipo_persona = 'P' ");
				sql.append( "   AND (p.razon_social like '" + texto + "%'");
				sql.append( "    OR p.nombre || ' ' || p.paterno  || ' ' || p.materno like '" + texto + "%')");
			}
			
			if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
			{
				sql.append( "SELECT p.no_persona as ID, ");
				sql.append( "       case when p.pers_juridica = 'F' then rtrim(ltrim(p.nombre + ' ' + p.paterno  + ' ' + p.materno)) ");
				sql.append( "       else rtrim(ltrim(p.razon_social)) end as Descrip ");
				sql.append( "  FROM persona p");
				sql.append( " WHERE p.id_tipo_persona = 'P' ");
				sql.append( "   AND (p.razon_social like '" + texto + "%'");
				sql.append( "    OR p.nombre + ' ' + p.paterno  + ' ' + p.materno like '" + texto + "%')");
			}*/
			
			System.out.println("consultarProveedores: " + sql);
			
			list= jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					try {
						cons.setId(rs.getInt("ID"));
					} catch (Exception e) {
						cons.setIdStr(rs.getString("ID"));
					}
					cons.setDescripcion(rs.getString("Descrip"));
					return cons;
			}});
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ImpresionDaoImpl, M:consultarProveedores");
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ImpresionDaoImpl, M:consultarProveedores");
			System.err.println(e);
		}
		return list;
	    
	}
	/**
	 * 
	 * FunSQLComboPersonas
	 * @param prefijoProv
	 * @return
	 */
	public List<LlenaComboGralDto> consultarProveedores(String prefijoProv, int iNoProveedor, int noEmpresa){
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listProv = new ArrayList<LlenaComboGralDto>();
		String sEmpresa = String.valueOf(noEmpresa).length() == 1 ? 0 + String.valueOf(noEmpresa) : String.valueOf(noEmpresa);
		
		try {
			if(ConstantesSet.gsDBM.equals("ORACLE") || ConstantesSet.gsDBM.equals("POSTGRESQL") || ConstantesSet.gsDBM.equals("DB2"))
			{
	            sbSql.append("SELECT p.equivale_persona as ID, ");
	            sbSql.append("case when p.pers_juridica = 'F' then p.nombre + ' ' + p.paterno  + ' ' + p.materno else p.razon_social end as Descrip ");
	            sbSql.append("\n  FROM persona p");
	            if(prefijoProv != null && !prefijoProv.equals("") && iNoProveedor <= 0)
	            {
	            	sbSql.append("\n  where (p.razon_social like '" + Utilerias.validarCadenaSQL(prefijoProv) + "%'");
		            sbSql.append("\n    OR p.nombre + ' ' + p.paterno  + ' ' + p.materno like '" + Utilerias.validarCadenaSQL(prefijoProv) + "%')");
	            }
	            else if(iNoProveedor > 0)
	            	sbSql.append("\n where p.no_persona = " + iNoProveedor);
			}else if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE")) {
				sbSql.append("SELECT p.no_persona as ID, ");
				sbSql.append("\n  case when p.pers_juridica = 'F' then rtrim(ltrim(p.nombre + ' ' + p.paterno  + ' ' + p.materno)) else rtrim(ltrim(p.razon_social)) end as Descrip ");
	            sbSql.append("\n  FROM persona p");
	            
	            if(prefijoProv != null && !prefijoProv.equals("") && iNoProveedor <= 0) {
	            	sbSql.append("\n  where (p.razon_social like '" + Utilerias.validarCadenaSQL(prefijoProv) + "%'");
		            sbSql.append("\n    OR p.nombre + ' ' + p.paterno  + ' ' + p.materno like '" + Utilerias.validarCadenaSQL(prefijoProv) + "%')");
		            if(!sEmpresa.equals("00"))
		            	sbSql.append("\n 	And p.equivale_persona like '" + sEmpresa + "%'");
	            }
	            else if(iNoProveedor > 0)
	            	sbSql.append("\n where p.no_persona = " + iNoProveedor);
			}
			sbSql.append("\n ORDER BY Descrip ");
			
			System.out.println("Query consulta mov: " + sbSql.toString());
			
			listProv = jdbcTemplate.query(sbSql.toString(),  new RowMapper<LlenaComboGralDto> () {
				public	LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException {
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						dtoCons.setId(rs.getInt("ID"));
						dtoCons.setDescripcion(rs.getString("Descrip"));
					return dtoCons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:consultarProveedores");
		}
		return listProv;
	}
	
	//Inician metodos para BuscarMovimiento.js, aunque se pueden utilizar anteriores
	
	/**
	 * Este m\u00e9todo consulta la tabla zimp_fact
	 * FunSQLZIMP
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarZimpFact(ParamZimpFactDto dto)
	{
		List<Map<String, Object>> listCons = new ArrayList<Map<String, Object>>();
		StringBuffer sbSql = new StringBuffer();
		try
		{
			if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
			{
				sbSql.append("Select z.no_doc_sap,z.fec_fact,z.estatus_compensa,z.estatus,");
				sbSql.append("\n z.fecha_imp,z.causa_rech,z.secuencia,z.importe,z.id_divisa,");
				sbSql.append("\n z.no_benef,p.razon_social as nombre,z.forma_pago");
				sbSql.append("\n from zimp_fact z left join persona p ");
				sbSql.append("\n on (to_number(z.no_benef,'9999999999')=p.no_persona and p.id_tipo_persona='P')");
				sbSql.append("\n where z.no_empresa=(Select SOIEMP from SET006 where SETEMP=" + dto.getNoEmpresa() + " and SISCOD='CP')");
				sbSql.append("\n and z.fec_fact like '%" + Utilerias.validarCadenaSQL(dto.getPeriodo()) + "%'");
			}
			else if(ConstantesSet.gsDBM.equals("DB2"))
			{
				sbSql.append("\n select z.no_doc_sap,z.fec_fact,z.estatus_compensa,z.FECHA_EXP,");
				sbSql.append("\n z.estatus , z.causa_rech, z.secuencia, z.importe, z.id_divisa,z.forma_pago,");
				sbSql.append("\n z.no_benef,p.razon_social + ' ' + p.paterno + ' ' + p.materno + ' ' + p.nombre as nombre");
				sbSql.append("\n from zimp_fact z left join persona p on (z.no_benef=p.equivale_persona and p.id_tipo_persona='P')");
				sbSql.append("\n where z.no_empresa=(Select SOIEMP from SET006 where SETEMP=" + dto.getNoEmpresa() + " and SISCOD='CP')");
				sbSql.append("\n and z.fec_fact like '%" + Utilerias.validarCadenaSQL(dto.getPeriodo()) + "%'");
			}
			else
			{
				sbSql.append("\n select z.no_doc_sap,z.fec_fact,z.estatus_compensa,z.fecha_imp,z.FECHA_EXP,");
				sbSql.append("\n z.estatus , coalesce(z.causa_rech,'') as causa_rech, z.secuencia, z.importe, z.id_divisa,z.forma_pago,");
				sbSql.append("\n z.no_benef,p.razon_social + ' '  + p.paterno + ' ' + p.materno + ' ' + p.nombre as nombre");
				sbSql.append("\n from zimp_fact z left join persona p on (z.no_benef=p.equivale_persona and p.id_tipo_persona='P')");
				sbSql.append("\n where z.no_empresa=(Select SOIEMP from SET006 where SETEMP=" + dto.getNoEmpresa() + " and SISCOD='CP')");
				sbSql.append("\n and z.fec_fact like '%" + Utilerias.validarCadenaSQL(dto.getPeriodo()) + "%'");
			}
			
			if(dto.getNoDocto() != null && !dto.getNoDocto().equals(""))
				sbSql.append("\n and z.no_doc_sap='" + Utilerias.validarCadenaSQL(dto.getNoDocto()) + "'");
			if(dto.getNoProveedor() != null && !dto.getNoProveedor().equals(""))
				sbSql.append("\n and z.no_benef = '" + Utilerias.validarCadenaSQL(dto.getNoProveedor()) + "'");
			if(dto.getFactura() != null && !dto.getFactura().equals(""))
				sbSql.append("\n and z.no_factura like '" + Utilerias.validarCadenaSQL(dto.getFactura()) + "%'");
			
			System.out.println("Query que busca los movimientos en zimp_fact: " + sbSql);
			
			listCons = jdbcTemplate.query(sbSql.toString(), new RowMapper()
				{
					public ZimpFactDto mapRow (ResultSet rs, int idx)throws SQLException
					{
						ZimpFactDto dtoCons = new ZimpFactDto();
							dtoCons.setNoDocSap(rs.getString("no_doc_sap"));
							dtoCons.setFecFact(rs.getString("fec_fact"));
							dtoCons.setEstatusCompensa(rs.getString("estatus_compensa"));
							dtoCons.setFechaImp(rs.getString("fecha_exp"));
							dtoCons.setEstatus(rs.getString("estatus"));
							dtoCons.setCausaRech(rs.getString("causa_rech").trim());
							dtoCons.setSecuencia(rs.getString("secuencia"));
							dtoCons.setImporte(rs.getBigDecimal("importe").doubleValue());
							dtoCons.setIdDivisa(rs.getString("id_divisa"));
							dtoCons.setFormaPago(rs.getInt("forma_pago"));
							dtoCons.setNoBenef(rs.getString("no_benef"));
							dtoCons.setNombre(rs.getString("nombre"));
							dtoCons.setBZimpFact(true);
						return dtoCons;
					}
				}
			);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasDao, M:consultarZimpFact");
		}
		return listCons;
	}
	
	
	/**
	 * Este m\u00e9todo retorna el resultado de la consulta en un mapa
	 * debido a la funcionalidad, ya que un mismo m\u00e9todo obtiene dos tipos
	 * de consulta.
	 * FunSolicMovi
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarSolicitudMovto(ParamBusSolicitudDto dto)
	{
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		StringBuffer sbSql = new StringBuffer();
		try
		{
			for(int i = 0; i <= 1; i ++)
			{
				sbSql.append("SELECT m.id_estatus_mov, m.id_forma_pago, fp.desc_forma_pago, m.importe,");
	            sbSql.append("\n m.no_docto, m.fec_operacion, m.id_divisa,");
	            sbSql.append("\n m.fec_imprime, m.fec_valor, m.fec_conf_trans,");
	            sbSql.append("\n m.fec_reimprime, m.beneficiario,");
	            sbSql.append("\n m.grupo_pago, m.po_headers, m.invoice_type, m.cod_bloqueo, ");
	            sbSql.append("\n m.c_periodo, m.fec_propuesta, m.cve_control, cc.desc_caja, m.no_cliente");
	            if (i == 0) 
	                sbSql.append("\n FROM movimiento m LEFT JOIN cat_caja cc ON (m.id_caja = cc.id_caja)");
	            else
	                sbSql.append("\n FROM " + Utilerias.validarCadenaSQL(dto.getNomTabla()) + " m LEFT JOIN cat_caja cc ON (m.id_caja = cc.id_caja)");
	            
	            sbSql.append("\n ,cat_forma_pago fp");
	            sbSql.append("\n WHERE ");
	            sbSql.append("\n m.id_forma_pago = fp.id_forma_pago");
	            sbSql.append("\n AND m.no_empresa = " + dto.getNoEmpresa());
	            sbSql.append("\n AND m.id_tipo_operacion IN (" + dto.getIdTipoOperacion() + ")");
	            if(dto.getNoDocto() != null && !dto.getNoDocto().equals(""))
	            sbSql.append("\n AND m.no_docto = '" + Utilerias.validarCadenaSQL(dto.getNoDocto()) + "'");
	            
	            if(dto.getSecuencia() != null && !dto.getSecuencia().equals(""))
	                sbSql.append("\n AND m.invoice_type = '" + Utilerias.validarCadenaSQL(dto.getSecuencia()) + "'");
	           
	            if(dto.getFechaOperacion() != null && !dto.getFechaOperacion().equals(""))
	                sbSql.append("\n AND m.fec_operacion = '" + funciones.ponerFechaSola(dto.getFechaOperacion()) + "'");
	            
	            if(ConstantesSet.gsDBM.equals("DB2"))
	            	sbSql.append("\n AND m.fec_operacion  BETWEEN '" + Utilerias.validarCadenaSQL(dto.getPeriodo()) + "-01-01' AND '" + Utilerias.validarCadenaSQL(dto.getPeriodo()) + "-12-31'");
	            else
	            	sbSql.append("\n AND m.fec_operacion  BETWEEN '01/01/" + Utilerias.validarCadenaSQL(dto.getPeriodo()) + "' AND '31/12/" + Utilerias.validarCadenaSQL(dto.getPeriodo()) + "'");
	            	
	            if(dto.getIdProveedor() != null && !dto.getIdProveedor().equals(""))
	            {
	            	sbSql.append("\n AND m.no_cliente IN (SELECT no_persona FROM persona WHERE no_empresa = 552");
	            	sbSql.append("\n AND id_tipo_persona IN ('P', 'K', 'E') AND (equivale_persona = '" + Utilerias.validarCadenaSQL(dto.getIdProveedor()) + "' or no_persona = '" + Utilerias.validarCadenaSQL(dto.getIdProveedor()) + "') )");
	            }
	            
	            if(i == 0)
	                sbSql.append("\n UNION \n");
	            
			}
			
			System.out.println("Query que busca los movimientos: " + sbSql);
			
			listMap = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
					public MovimientoDto mapRow(ResultSet rs, int idx)throws SQLException
					{
						MovimientoDto dtoCons = new MovimientoDto();
						  	dtoCons.setIdEstatusMov(rs.getString("id_estatus_mov"));
							dtoCons.setIdFormaPago(rs.getInt("id_forma_pago"));
							dtoCons.setDescFormaPago(rs.getString("desc_forma_pago"));
							dtoCons.setImporte(rs.getBigDecimal("importe").doubleValue());
							dtoCons.setNoDocto(rs.getString("no_docto"));
							dtoCons.setFecOperacion(rs.getDate("fec_operacion"));
							dtoCons.setIdDivisa(rs.getString("id_divisa"));
							dtoCons.setFecImprime(rs.getDate("fec_imprime"));
							dtoCons.setFecValor(rs.getDate("fec_valor"));
							dtoCons.setFecConfTrans(rs.getDate("fec_conf_trans"));
							dtoCons.setFecReimprime(rs.getDate("fec_reimprime"));
							dtoCons.setBeneficiario(rs.getString("beneficiario"));
							dtoCons.setGrupoPago(rs.getInt("grupo_pago"));
							dtoCons.setPoHeaders(rs.getString("po_headers"));
							dtoCons.setInvoiceType(rs.getString("invoice_type"));
							dtoCons.setCodBloqueo(rs.getString("cod_bloqueo"));
							dtoCons.setCPeriodo(rs.getInt("c_periodo"));
							dtoCons.setFecPropuesta(rs.getDate("fec_propuesta"));
							dtoCons.setCveControl(rs.getString("cve_control"));
							dtoCons.setDescCaja(rs.getString("desc_caja"));
							dtoCons.setNoCliente(rs.getString("no_cliente"));
							dtoCons.setBZimpFact(false);
						return dtoCons;
					}
				}
			);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasDao, M:consultarSolicitudMovto");
		}
		return listMap;
	}
	
	/**
	 * sSQLSeguimiento
	 * @param dto
	 * @return
	 */
	public List<MovimientoDto> consultarSeguimientoMovtos(ParamBusSolicitudDto dto){
		List<MovimientoDto> listMovto = new ArrayList<MovimientoDto>();
		StringBuffer sbSql = new StringBuffer();
		try
		{
			for(int i = 0; i <= 3; i ++)
			{
				sbSql.append("SELECT a.no_folio_det, a.no_empresa, desc_tipo_operacion, desc_forma_pago, desc_estatus");
		        sbSql.append("\n ,a.importe, cc.desc_caja, a.importe_original, a.id_divisa, a.id_divisa_original,");
		        sbSql.append("\n CASE WHEN a.id_estatus_mov IN ('X','Y','Z') THEN a.fec_modif ELSE");
		        sbSql.append("\n CASE WHEN a.id_estatus_mov IN ('I') THEN a.fec_imprime ELSE");
		        sbSql.append("\n CASE WHEN a.id_estatus_mov IN ('R') THEN a.fec_reimprime ELSE");
		        sbSql.append("\n CASE WHEN a.id_estatus_mov IN ('T') THEN a.fec_trans ELSE");
		        sbSql.append("\n CASE WHEN a.id_estatus_mov IN ('K') THEN a.fec_conf_trans ELSE fec_valor");
		        sbSql.append("\n END END END END END AS fec_valor ,");
		        sbSql.append("\n CASE WHEN grupo_pago > 0 THEN grupo_pago ELSE 0 END AS grupo,");
		        sbSql.append("\n CASE WHEN cve_control <> 'NULL' THEN cve_control ELSE '' END AS propuesta");
		        sbSql.append("\n ,a.id_tipo_operacion, desc_divisa,");
		        
		        if(ConstantesSet.gsDBM.equals("POSTGRESQL") || ConstantesSet.gsDBM.equals("DB2")) 
		        {
		        	sbSql.append("\n cua.nombre || ' ' || cua.paterno || ' '  || cua.materno AS desc_usuario,cb.desc_banco as banco,a.id_chequera,");
		            sbSql.append("\n cum.nombre || ' ' || cum.paterno || ' '  || cum.materno AS desc_usuario_mod,");
		        }
		        else
		        {
		        	sbSql.append("\n RTRIM(LTRIM(coalesce(cua.nombre,'') + ' ' + coalesce(cua.paterno,'') + ' '  + coalesce(cua.materno,''))) AS desc_usuario,cb.desc_banco as banco,a.id_chequera,");
		            sbSql.append("\n RTRIM(LTRIM(coalesce(cum.nombre,'') + ' ' + coalesce(cum.paterno,'') + ' '  + coalesce(cum.materno,''))) AS desc_usuario_mod,");
		        }
		        
		        sbSql.append("\n cb2.desc_banco AS banco_benef, a.id_chequera_benef,a.origen_mov");
		        
		        switch (i)
		        {
		        	case 0:
		        		sbSql.append("\n FROM hist_solicitud a ");
		        	break;
		        	case 1:
		        		sbSql.append("\n FROM movimiento a ");
		        	break;
		        	case 2:
		        		sbSql.append("\n FROM hist_movimiento a ");
		        	break;
		        	case 3:
		        		sbSql.append("\n FROM hist_mov_det a ");
		        	break;
		        }
		        
		        sbSql.append("\n LEFT JOIN cat_banco cb ON (a.id_banco = cb.id_banco)");
		        sbSql.append("\n LEFT JOIN cat_banco cb2 ON (a.id_banco_benef = cb2.id_banco)");
		        //'/*** CONDICION PARA QUE SOLO LAS SOLICITUDES TOMEN EL USUARIO ALTA LOS DEMAS EL USUARIO MODIF**/
		        sbSql.append("\n LEFT JOIN cat_usuario cua ON (a.usuario_alta = cua.no_usuario)");
		        sbSql.append("\n LEFT JOIN cat_usuario cum ON (a.usuario_modif = cum.no_usuario)");
		        sbSql.append("\n LEFT JOIN cat_caja cc ON (a.id_caja = cc.id_caja),");
		        sbSql.append("\n cat_estatus b, cat_tipo_operacion c,cat_forma_pago d, ");
		        sbSql.append("\n cat_divisa e");
		        sbSql.append("\n WHERE");
		        sbSql.append("\n ((a.id_estatus_mov <> 'H') OR (a.id_estatus_mov = 'H' AND grupo_pago > 0))");
		        sbSql.append("\n AND a.id_tipo_operacion = c.id_tipo_operacion");
		        sbSql.append("\n AND a.id_forma_pago = d.id_forma_pago");
		        sbSql.append("\n AND a.id_divisa = e.id_divisa");
		        sbSql.append("\n AND a.id_estatus_mov = b.id_estatus");
		        sbSql.append("\n AND a.no_empresa = " + dto.getNoEmpresa());
		        if(dto.getNoDocto()!=null && !dto.getNoDocto().equals(""))
		        sbSql.append("\n AND no_docto = '" + Utilerias.validarCadenaSQL(dto.getNoDocto()) + "'");
		        sbSql.append("\n AND a.id_tipo_operacion <> 3001 ");
		        sbSql.append("\n AND b.clasificacion = 'MOV'");
		        sbSql.append("\n AND a.id_tipo_movto = 'E'");
		        
		        if(!ConstantesSet.gsDBM.equals("POSTGRESQL"))
		        	sbSql.append("\n AND a.fec_operacion  BETWEEN '01/01/" + Utilerias.validarCadenaSQL(dto.getPeriodo()) + "' AND '31/12/" + Utilerias.validarCadenaSQL(dto.getPeriodo()) + "'");
		            //sbSql.append("\n AND a.fec_operacion = '" + funciones.ponerFechaSola(dto.getFechaOperacion()) + "'");
		        
		        if(dto.getSecuencia() != null && !dto.getSecuencia().equals("")) 
		            sbSql.append("\n AND a.invoice_type = '" + Utilerias.validarCadenaSQL(dto.getSecuencia()) + "'");
		        if(dto.getIdProveedor() != null && !dto.getIdProveedor().equals(""))
		        	sbSql.append("\n AND a.no_cliente = '" + Utilerias.validarCadenaSQL(dto.getNoBenef()) + "'");
		        
		        if(i != 3)
		            sbSql.append("\n UNION ");
			}
	    
	        //'/***** SE ORDENA PRIMERO POR no_folio_det PARA QUE SIGAN LA SECUENCIA CRONOLOGICA ***/
	        sbSql.append("\n ORDER BY no_folio_det,fec_valor,");
	        if(ConstantesSet.gsDBM.equals("POSTGRESQL") || ConstantesSet.gsDBM.equals("DB2"))
	            sbSql.append("\n id_tipo_operacion ");
	        else
	            sbSql.append("\n a.id_tipo_operacion ");
	        
	        System.out.println("query Seguimiento : " + sbSql.toString());
	        
	        listMovto = jdbcTemplate.query(sbSql.toString(), new RowMapper<MovimientoDto>()
	        	{
	        		public MovimientoDto mapRow(ResultSet rs, int idx)throws SQLException
	        		{
	        			MovimientoDto dtoCons = new MovimientoDto();
	        				dtoCons.setNoFolioDet(rs.getInt("no_folio_det"));
	        				dtoCons.setNoEmpresa(rs.getInt("no_empresa"));
	        				dtoCons.setDescTipoOperacion(rs.getString("desc_tipo_operacion"));
	        				dtoCons.setDescFormaPago(rs.getString("desc_forma_pago"));
	        				dtoCons.setDescEstatus(rs.getString("desc_estatus"));
	        				dtoCons.setImporte(rs.getBigDecimal("importe").doubleValue());
	        				dtoCons.setDescCaja(rs.getString("desc_caja"));
	        				dtoCons.setImporteOriginal(rs.getBigDecimal("importe_original").doubleValue());
	        				dtoCons.setIdDivisa(rs.getString("id_divisa"));
	        				dtoCons.setIdDivisaOriginal(rs.getString("id_divisa_original"));
	        				dtoCons.setFecValor(rs.getDate("fec_valor"));
	        				dtoCons.setGrupoPago(rs.getInt("grupo"));
	        				dtoCons.setCveControl(rs.getString("propuesta"));
	        				dtoCons.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
	        				dtoCons.setDescDivisa(rs.getString("desc_divisa"));
	        				dtoCons.setDescUsuario(rs.getString("desc_usuario"));
	        				dtoCons.setDescBanco(rs.getString("banco"));
	        				dtoCons.setIdChequera(rs.getString("id_chequera"));
	        				dtoCons.setDescUsuarioMod(rs.getString("desc_usuario_mod"));
	        				dtoCons.setDescBancoBenef(rs.getString("banco_benef"));
	        				dtoCons.setIdChequeraBenef(rs.getString("id_chequera_benef"));
	        				dtoCons.setOrigenMov(rs.getString("origen_mov"));
	        			return dtoCons;
	        		}
	        	}
	        );
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasDao, M:consultarSeguimientoMovtos");
		}
		return listMovto;
	}
	
	/**
	 * SQLPapa
	 * @param iFolio
	 * @param sNoDocto
	 * @return
	 */
	public List<MovimientoDto> consultarRegistroPadre(int iFolio, String sNoDocto)
	{
		List<MovimientoDto> listMovto = new ArrayList<MovimientoDto>();
		StringBuffer sbSql = new StringBuffer();
		try
		{
			for(int i = 0; i <= 1; i ++)
			{
		        sbSql.append("Select a.no_empresa,desc_tipo_operacion,desc_forma_pago,desc_estatus");
		        sbSql.append("\n ,a.importe,cc.desc_caja,a.no_docto,a.usuario_modif,a.fec_modif,");
		        sbSql.append("\n case when a.id_estatus_mov in('X','Y','Z') then a.fec_modif else");
		        sbSql.append("\n case when a.id_estatus_mov in('I') then a.fec_imprime else");
		        sbSql.append("\n case when a.id_estatus_mov in('R') then a.fec_reimprime else");
		        sbSql.append("\n case when a.id_estatus_mov in('T') then a.fec_trans else");
		        sbSql.append("\n case when a.id_estatus_mov in('k') then a.fec_conf_trans else fec_valor");
		        sbSql.append("\n end end end end end  as fec_valor ,");
		        sbSql.append("\n case when grupo_pago>0 then grupo_pago else 0 end  as grupo,");
		        sbSql.append("\n case when cve_control<>'NULL'   then cve_control else '' end as propuesta");
		        sbSql.append("\n ,a.id_tipo_operacion,desc_divisa,");
		        
		        if(ConstantesSet.gsDBM.equals("POSTGRESQL") || ConstantesSet.gsDBM.equals("DB2"))
		            sbSql.append("\n cu.nombre || ' ' || cu.paterno || ' '  || cu.materno as desc_usuario,cb.desc_banco as banco,a.id_chequera,");
		        else
		            sbSql.append("\n cu.nombre + ' ' + cu.paterno + ' '  + cu.materno as desc_usuario,cb.desc_banco as banco,a.id_chequera,");
		        
		        sbSql.append("\n cb2.desc_banco as banco_benef, a.id_chequera_benef");
		        
		        switch (i)
		        {
		        	case 0:
		        		sbSql.append("\n from hist_movimiento a ");
		        	break;
		        	case 1:
		        		sbSql.append("\n from movimiento a ");
		        	break;
		        }
		        
		        sbSql.append("\n left join cat_banco cb on (a.id_banco=cb.id_banco)");
		        sbSql.append("\n left join cat_banco cb2 on (a.id_banco_benef=cb2.id_banco)");
		        sbSql.append("\n left join cat_usuario cu on (case when a.id_estatus_mov='A' then a.usuario_alta else a.usuario_modif end=cu.no_usuario)");
		        sbSql.append("\n left join cat_caja cc on (a.id_caja=cc.id_caja),");
		        sbSql.append("\n cat_estatus b, cat_tipo_operacion c,cat_forma_pago d, ");
		        sbSql.append("\n cat_divisa e");
		        sbSql.append("\n Where");
		        sbSql.append("\n ((a.id_estatus_mov <> 'H')  or (a.id_estatus_mov='H' and grupo_pago>0))");
		        sbSql.append("\n and a.id_tipo_operacion = c.id_tipo_operacion");
		        sbSql.append("\n and a.id_forma_pago = d.id_forma_pago");
		        sbSql.append("\n and a.id_divisa = e.id_divisa");
		        sbSql.append("\n and a.id_estatus_mov = b.id_estatus");
		         
		        sbSql.append("\n and no_folio_det = " + iFolio);
		        sbSql.append("\n and grupo_pago = " + iFolio);
	            if(sNoDocto != null && !sNoDocto.equals(""))
		        sbSql.append("\n and no_docto <> '" + Utilerias.validarCadenaSQL(sNoDocto) + "'");
		        sbSql.append("\n and a.id_tipo_operacion <>3001 ");
		        sbSql.append("\n and b.clasificacion ='MOV'");
		        sbSql.append("\n and a.id_estatus_mov <>'N'");
		                
		        if(i == 0) 
		            sbSql.append("\n UNION \n");
		         
			}
			sbSql.append("\n ORDER BY fec_valor");

			listMovto = jdbcTemplate.query(sbSql.toString(), new RowMapper<MovimientoDto>()
	        	{
	        		public MovimientoDto mapRow(ResultSet rs, int idx)throws SQLException
	        		{
	        			MovimientoDto dtoCons = new MovimientoDto();
	        				dtoCons.setNoEmpresa(rs.getInt("no_empresa"));
	        				dtoCons.setDescTipoOperacion(rs.getString("desc_tipo_operacion"));
	        				dtoCons.setDescFormaPago(rs.getString("desc_forma_pago"));
	        				dtoCons.setDescEstatus(rs.getString("desc_estatus"));
	        				dtoCons.setImporte(rs.getBigDecimal("importe").doubleValue());
	        				dtoCons.setDescCaja(rs.getString("desc_caja"));
	        				dtoCons.setNoDocto(rs.getString("no_docto"));
	        				dtoCons.setDescUsuarioMod(rs.getString("usuario_modif"));
	        				dtoCons.setFecModif(rs.getDate("fec_modif"));
	        				dtoCons.setFecValor(rs.getDate("fec_valor"));
	        				dtoCons.setGrupoPago(rs.getInt("grupo"));
	        				dtoCons.setCveControl(rs.getString("propuesta"));
	        				dtoCons.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
	        				dtoCons.setDescDivisa(rs.getString("desc_divisa"));
	        				dtoCons.setDescUsuario(rs.getString("desc_usuario"));
	        				dtoCons.setDescBanco(rs.getString("banco"));
	        				dtoCons.setIdChequera(rs.getString("id_chequera"));
	        				dtoCons.setDescBancoBenef(rs.getString("banco_benef"));
	        				dtoCons.setIdChequeraBenef(rs.getString("id_chequera_benef"));
	        			return dtoCons;
	        		}
	        	}
			);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasDao, M:consultarRegistroPadre");
		}
		return listMovto;
	}
	
	/**
	 * FunSQLCausaChequera
	 * @param iNoBenef
	 * @param iNoEmpresa
	 * @return
	 */
	public String consultarCausaChequera(int iNoBenef, int iNoEmpresa)
	{
		StringBuffer sbSql = new StringBuffer();
		String sCausa = "";
		try
		{
			sbSql.append("Select causa_rech ");
	        sbSql.append("\n from zimpcheqprov ");
	        sbSql.append("\n where no_empresa= (Select SOIEMP from SET006 where SETEMP=" + iNoEmpresa + " and SISCOD='CP')");
	        sbSql.append("\n and equiv_per='" + iNoBenef + "'");
	        
	        sCausa = jdbcTemplate.queryForObject(sbSql.toString(), String.class);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Consultas, C:ConsultasDao, M:consultarCausaChequera");
		}
		return sCausa;
	}
	
	public List<LlenaComboChequeraDto> llenaComboChequera(int idBanco, int noEmpresa, String idDivisa) {
		StringBuffer sql = new StringBuffer();
		List<LlenaComboChequeraDto> list = new ArrayList<LlenaComboChequeraDto>();
		
		try{
		    sql.append(" SELECT id_chequera as ID \n");
		    sql.append(" FROM cat_cta_banco \n");
		    sql.append(" WHERE id_banco = " + idBanco + " \n");
	    	
	    	if(noEmpresa != 0 )
	    		sql.append(" And no_empresa = " + noEmpresa + " ");
			
	    	list = jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboChequeraDto>(){
		    	public LlenaComboChequeraDto mapRow(ResultSet rs, int idx) throws SQLException {
		    		LlenaComboChequeraDto cons = new LlenaComboChequeraDto();
					cons.setId(rs.getString("ID"));
					//cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:ConfirmacionTransferenciasDaoImpl, M:LlenaComboChequeraDto");
		}
		return list;
	}
	
	public List<SaldoChequeraDto> consultarSaldosCuentas(int noEmpresa, int idBanco, String idChequera, int idUsuario, String idDivisa) {
		StringBuffer sql = new StringBuffer();
		List<SaldoChequeraDto> listResult = new ArrayList<SaldoChequeraDto>();
		globalSingleton = GlobalSingleton.getInstancia();
		
		try {
			sql.append(" SELECT ccb.no_empresa, e.nom_empresa, ccb.id_banco, cb.desc_banco, ccb.desc_chequera, ccb.id_chequera, \n");
			sql.append("	ccb.id_divisa, ccb.saldo_inicial, ccb.cargo, ccb.abono, ccb.saldo_final \n");
			sql.append(" FROM cat_cta_banco ccb, empresa e, cat_banco cb \n");
			sql.append(" WHERE ccb.no_empresa = e.no_empresa \n");
			sql.append("	And ccb.id_banco = cb.id_banco \n");
			sql.append("	And ccb.no_empresa in (select no_empresa from usuario_empresa \n");
			sql.append("		where no_usuario = "+ idUsuario + " ) \n");// globalSingleton.getUsuarioLoginDto().getIdUsuario() +" ) \n");
			sql.append(" And ccb.id_divisa = '"+ Utilerias.validarCadenaSQL(idDivisa) +"' ");
			
			if(noEmpresa != 0) sql.append("	And ccb.no_empresa = "+ noEmpresa +" \n");
			if(idBanco != 0) sql.append("	And ccb.id_banco = "+ idBanco +" \n");
			if(!idChequera.equals("")) sql.append("	And ccb.id_chequera = '"+ Utilerias.validarCadenaSQL(idChequera) +"' \n");
			
			sql.append(" ORDER BY e.nom_empresa, cb.desc_banco");
			
			System.out.println("Query posicion chequera: " + sql);
			
			listResult = jdbcTemplate.query(sql.toString(), new RowMapper<SaldoChequeraDto>() {
				public SaldoChequeraDto mapRow(ResultSet rs, int idx)throws SQLException {
					SaldoChequeraDto dtoCheq = new SaldoChequeraDto();
					dtoCheq.setNoEmpresa(rs.getInt("no_empresa"));
					dtoCheq.setNomEmpresa(rs.getString("nom_empresa"));
					dtoCheq.setIdBanco(rs.getInt("id_banco"));
					dtoCheq.setDescBanco(rs.getString("desc_banco"));
					dtoCheq.setDescChequera(rs.getString("desc_chequera"));
					dtoCheq.setIdChequera(rs.getString("id_chequera"));
					dtoCheq.setIdDivisa(rs.getString("id_divisa"));
					dtoCheq.setSaldoInicial(rs.getDouble("saldo_inicial"));
					dtoCheq.setCargo(rs.getDouble("cargo"));
					dtoCheq.setAbono(rs.getDouble("abono"));
					dtoCheq.setSaldoFinal(rs.getDouble("saldo_final"));
					return dtoCheq;
	        	}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:consultarSaldosCuentas");
			logger.error(e.toString());
		}
		return listResult;
	}
	
	public List<Map<String, Object>> reportePosicionChequeras(SaldoChequeraDto datos, int noUsuario) {
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
		
		try {
			sql.append(" SELECT ccb.no_empresa, e.nom_empresa, ccb.id_banco, cb.desc_banco, ccb.desc_chequera, ccb.id_chequera, \n");
			sql.append("	ccb.id_divisa, ccb.saldo_inicial, ccb.cargo, ccb.abono, ccb.saldo_final \n");
			sql.append(" FROM cat_cta_banco ccb, empresa e, cat_banco cb \n");
			sql.append(" WHERE ccb.no_empresa = e.no_empresa \n");
			sql.append("	And ccb.id_banco = cb.id_banco \n");
			sql.append("	And ccb.no_empresa in (select no_empresa from usuario_empresa \n");
			sql.append("		where no_usuario = "+ noUsuario +" ) \n");
			
			if(datos.getNoEmpresa() != 0) sql.append("	And ccb.no_empresa = "+ datos.getNoEmpresa() +" \n");
			if(datos.getIdBanco() != 0) sql.append("	And ccb.id_banco = "+ datos.getIdBanco() +" \n");
			if(!datos.getIdChequera().equals("")) sql.append("	And ccb.id_chequera = '"+ Utilerias.validarCadenaSQL(datos.getIdChequera()) +"' \n");
			if(!datos.getIdDivisa().equals("")) sql.append("	And ccb.id_divisa = '"+ Utilerias.validarCadenaSQL(datos.getIdDivisa()) +"' \n");
			
			sql.append(" ORDER BY e.nom_empresa, cb.desc_banco");
			
			System.out.println("Query reporte posicion chequera: " + sql);
			
			listResult = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String,Object>>() {
				public Map<String,Object> mapRow(ResultSet rs, int idx)throws SQLException {
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("nomEmpresa", rs.getString("nom_empresa"));
					map.put("descBanco", rs.getString("desc_banco"));
					map.put("descChequera", rs.getString("desc_chequera"));
					map.put("idChequera", rs.getString("id_chequera"));
					map.put("idDivisa", rs.getString("id_divisa"));
					map.put("saldoInicial", rs.getDouble("saldo_inicial"));
					map.put("cargo", rs.getDouble("cargo"));
					map.put("abono", rs.getDouble("abono"));
					map.put("saldoFinal", rs.getDouble("saldo_final"));
					return map;
	        	}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:reportePosicionChequeras");
		}
		return listResult;
	}
	
	//AUTOR: JRBM
	public List<SaldoDto> getTotalesIngresosEgresos( int noEmpresa, int idBanco, String idChequera) {
		
		StringBuffer sql = new StringBuffer();
		
		List<SaldoDto> list = new ArrayList<SaldoDto>();
		
		try{
			
		    sql.append(" SELECT m.no_empresa,m.id_banco,                           \n");
		    sql.append("        m.id_chequera,m.id_divisa,m.id_tipo_movto,         \n");
			sql.append("        m.fec_valor,sum(importe) as importe                \n");
			sql.append(" FROM  movimiento M 				                       \n");
			sql.append(" WHERE id_estatus_mov <> 'X'                               \n");
			sql.append(" and m.id_tipo_operacion in( 3200, 3701, 3120, 3101,3116,  \n"); 
			sql.append(" 							1016, 1017, 1018, 1019, 1020,  \n");
			sql.append(" 							1023, 3107, 3700, 3702, 3705,  \n");
			sql.append(" 							3706,3708,3709,3707,4001,4102, \n");
			sql.append(" 							4103,4104)                     \n");
			sql.append(" and m.no_empresa  = ?                                     \n");
			sql.append(" and m.id_banco    = ?                                     \n");
			sql.append(" and m.id_chequera = ?                                     \n");
			sql.append(" and convert(datetime, m.fec_valor, 103 )   = ?                \n");			
			sql.append(" group by m.no_empresa,m.id_banco,m.id_chequera,           \n");
			sql.append(" m.id_divisa,m.id_tipo_movto, m.fec_valor                  \n");
			
			Object[] params = new Object[]{ Integer.valueOf( noEmpresa ),
										    Integer.valueOf( idBanco   ),
										    idChequera,
										    obtenerFechaHoy()
										  };
			
	    	list = jdbcTemplate.query(sql.toString(), params ,new RowMapper<SaldoDto>(){
	    		
		    	public SaldoDto mapRow(ResultSet rs, int idx) throws SQLException {
		    		
		    		SaldoDto saldo = new SaldoDto();
		    		saldo.setIdDivisa   ( rs.getString( "id_divisa" )     );
					saldo.setIdTipoMovto( rs.getString( "id_tipo_movto" ) );
					saldo.setImporte    ( rs.getDouble( "importe" )       );
					return saldo;
					
				}
		    	
		    });
	    	
	    	
		}catch(Exception e){
			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Egresos, C:ConfirmacionTransferenciasDaoImpl, M:getSaldosIngresosEgresosHoy");
		}
		
		return list;
	
	}//End function getTotalesIngresosEgresos
	
	//AUTOR: JRBM
	public Double getTotalesIngresosEgresos( int noEmpresa, int idBanco, String idChequera, String fecValor, String idTipoMovto) {
		
		StringBuffer sql = new StringBuffer();
		
		Double list = 0.0;
		
		try{
			
		    
		    sql.append("select COALESCE(SUM(M.importe), 0) AS importe                 	\n");
		    sql.append("FROM 															\n");
		    sql.append("	(select *													\n");
			sql.append("	 from movimiento											\n"); 
			sql.append("	 WHERE id_estatus_mov <> 'X'								\n");                               
			sql.append("	 and id_tipo_operacion in( 3200, 3701, 3120, 3101,3116,		\n");   
			sql.append("	 1016, 1017, 1018, 1019, 1020,  							\n");
			sql.append("	 1023, 3107, 3700, 3702, 3705,  							\n");
			sql.append("	 3706,3708,3709,3707,4001,4102, 							\n");
			sql.append("	 4103,4104)                    								\n");
			sql.append("	 union 														\n");
			sql.append("	 select *													\n");
			sql.append("	 from hist_movimiento										\n"); 
			sql.append("	 WHERE id_estatus_mov <> 'X'								\n");                               
			sql.append("	 and id_tipo_operacion in( 3200, 3701, 3120, 3101,3116,		\n");   
			sql.append("	 1016, 1017, 1018, 1019, 1020, 							 	\n");
			sql.append("	 1023, 3107, 3700, 3702, 3705,  							\n");
			sql.append("	 3706,3708,3709,3707,4001,4102, 							\n");
			sql.append("	 4103,4104)                     							\n");
			sql.append("     ) M 	 													\n");
			sql.append("WHERE m.no_empresa  = ?											\n");                                     
			sql.append("and m.id_banco      = ?											\n");
			sql.append("and m.id_chequera   = ?											\n");
			sql.append("and convert(datetime, m.fec_valor, 103 )   = ?						\n");              
			sql.append("and m.id_tipo_movto = ? 										\n");
							
			Object[] params = new Object[]{ Integer.valueOf( noEmpresa ),
										    Integer.valueOf( idBanco   ),
										    idChequera,
										    fecValor,
										    idTipoMovto
										  };
			
	    	list = jdbcTemplate.queryForObject(sql.toString(), params , Double.class );
	    	
		}catch(Exception e){
			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Egresos, C:ConfirmacionTransferenciasDaoImpl, M:getSaldosIngresosEgresosHoy");
		}
		
		return list;
	
	}//End function getTotalesIngresosEgresos
	
	//AUTOR: JRBM
	public List<SaldoDto> getSaldosHistoricos( int noEmpresa, int idBanco, String idChequera, String strFecha ) {
		
		StringBuffer sql = new StringBuffer();
		
		List<SaldoDto> list = new ArrayList<SaldoDto>();
		
		try{
			
			sql.append( "SELECT *                \n" );
			sql.append( "FROM HIST_CAT_CTA_BANCO \n" );
			sql.append( "WHERE no_empresa = ?    \n" );
			sql.append( "AND id_banco     = ?    \n" ); 
			sql.append( "AND id_chequera  = ?    \n" );
			sql.append( "AND convert(datetime, fec_valor, 103 )   >= ?    \n" );
			sql.append( "ORDER by fec_valor asc  \n");
			
			System.out.println("Query:"+sql.toString());
			
			Object[] params = new Object[]{ Integer.valueOf( noEmpresa ),
										    Integer.valueOf( idBanco   ),
										    idChequera,
										    strFecha
										  };
			
	    	list = jdbcTemplate.query(sql.toString(), params ,new RowMapper<SaldoDto>(){
	    		
		    	public SaldoDto mapRow(ResultSet rs, int idx) throws SQLException {
		    		
		    		SaldoDto saldo = new SaldoDto();
		    		saldo.setSaldoInicial( rs.getDouble( "saldo_Inicial" ) );
					saldo.setFecValor( rs.getString( "fec_valor" ) );
					return saldo;
					
				}
		    	
		    });
	    	
	    	
		}catch(Exception e){
			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Egresos, C:ConfirmacionTransferenciasDaoImpl, M:getSaldosHistoricos");
		}
		
		return list;
	
	}//End function getSaldosHistoricos
	
	
	public int updateSaldo(int noEmpresa, int idBanco, String idChequera, double cargo, double abono){
		
		int iRegAfec = 0;
		StringBuffer sql = new StringBuffer();
		try{
			
			Object[] params = new Object[]{
					Double.valueOf( abono ),
					Double.valueOf( cargo ),
					Double.valueOf( abono ),
					Double.valueOf( cargo ),
					Integer.valueOf( noEmpresa ),
				    Integer.valueOf( idBanco   ),
				    idChequera			
				  };
			
			sql.append("UPDATE cat_cta_banco SET abono = ?, cargo = ?,");
			sql.append("saldo_final = saldo_inicial + ? -  ?  ");
			sql.append("Where no_empresa = ?                          ");
			sql.append("and id_banco     = ?                          ");
			sql.append("and id_chequera  = ?                          ");
		        System.out.println("Qry update ctaBanco"+sql.toString());
		    iRegAfec = jdbcTemplate.update(sql.toString() , params);
		        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:updateSaldoHoy");
		}
		return iRegAfec;
	}
	
    public int updateSaldoInicialHoy(int noEmpresa, int idBanco, String idChequera, double saldoInicial){
		
		int iRegAfec = 0;
		StringBuffer sql = new StringBuffer();
		try{
			
			Object[] params = new Object[]{
					Double.valueOf( saldoInicial ),
					Integer.valueOf( noEmpresa ),
				    Integer.valueOf( idBanco   ),
				    idChequera			
				  };
			
			sql.append("UPDATE cat_cta_banco SET saldo_inicial = ?");
			sql.append(" Where no_empresa = ?                          ");
			sql.append(" and id_banco     = ?                          ");
			sql.append(" and id_chequera  = ?                          ");
		        
		    iRegAfec = jdbcTemplate.update(sql.toString() , params);
		        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:updateSaldoHoy");
		}
		return iRegAfec;
	}
	
	
	public int updateSaldo(int noEmpresa, int idBanco, String idChequera, double saldoInicial, double cargo, double abono, double saldoFinal, String fecValor){
		
		int iRegAfec = 0;
		StringBuffer sql = new StringBuffer();
		try{
			
			Object[] params = new Object[]{
					Double.valueOf( saldoInicial ),
					Double.valueOf( abono ),
					Double.valueOf( cargo ),
					Double.valueOf( saldoFinal ),
					Integer.valueOf( noEmpresa ),
				    Integer.valueOf( idBanco   ),
				    idChequera,
				    fecValor
				  };
			
			sql.append("UPDATE hist_cat_cta_banco SET saldo_inicial = ?, abono = ?, cargo = ?,saldo_final = ?");			 
			sql.append(" Where no_empresa = ?                          ");
			sql.append(" and id_banco     = ?                          ");
			sql.append(" and id_chequera  = ?                          ");
			sql.append(" and fec_valor  =  convert(datetime, ?   ,103)                       ");
		        
		    iRegAfec = jdbcTemplate.update(sql.toString() , params);
		        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:updateSaldoHoy");
		}
		return iRegAfec;
	}
	
	public String configuraSet(int indice) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
	}
	
	public int updateSaldos(int noEmpresa, String chequera, double saldoInicial, double ingreso, double egreso, double saldoFinal) {
		int iRegAfec = 0;
		StringBuffer sql = new StringBuffer();
		System.out.println("update de las chequeras");
		try{
			sql.append(" UPDATE cat_cta_banco SET saldo_inicial = "+ saldoInicial +", abono = "+ ingreso +", cargo = "+ egreso +", saldo_final = "+ saldoFinal +" \n");			 
			sql.append(" Where no_empresa = "+ noEmpresa +" ");
			sql.append(" and id_chequera  = '"+ funciones.ajustarLongitudCampo(chequera, 11, "D", "", "0") +"' ");
			System.out.println("EL UPDATE  "+sql.toString());
		    iRegAfec = jdbcTemplate.update(sql.toString());
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:updateSaldos");
		}
		return iRegAfec;
	}
	
	public int buscaChequera(int noEmpresa, String chequera) {
		int iRegAfec = 0;
		StringBuffer sql = new StringBuffer();
		System.out.println("busca las chequeras");
		
		try{
			sql.append(" SELECT count(*) FROM cat_cta_banco \n");			 
			sql.append(" Where no_empresa = "+ noEmpresa +" ");
			sql.append(" and id_chequera  = '"+ funciones.ajustarLongitudCampo(chequera, 11, "D", "", "0") +"' ");
		    System.out.println("EL SELECT  "+sql.toString());
		    iRegAfec = jdbcTemplate.queryForInt(sql.toString());
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:buscaChequera");
		}
		return iRegAfec;
	}
	
	public int validaFacultad(int idFacultad) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.validaFacultad(idFacultad);
	}
	
	public List<SaldoChequeraDto> obtenerUsuarios() {
		StringBuffer sql = new StringBuffer();
		List<SaldoChequeraDto> list = new ArrayList<SaldoChequeraDto>();
		
		try	{
			sql.append(" SELECT coalesce(valor, '') as valor FROM configura_set WHERE indice = 3011 ");
		    
			list = jdbcTemplate.query(sql.toString(), new RowMapper<SaldoChequeraDto>() {
				public SaldoChequeraDto mapRow(ResultSet rs, int idx)throws SQLException {
					SaldoChequeraDto dtoCheq = new SaldoChequeraDto();
					dtoCheq.setUsers(rs.getString("valor"));
					return dtoCheq;
	        	}
			});
			
			if(list.size() > 0) {
				sql = new StringBuffer();
				
				sql.append(" SELECT id_usuario, nombre + ' ' + apellido_paterno + ' ' + apellido_materno as nombre \n");			 
				sql.append(" FROM SEG_USUARIO \n");
				sql.append(" WHERE id_usuario in ("+ list.get(0).getUsers() +") ");
			    
				list = jdbcTemplate.query(sql.toString(), new RowMapper<SaldoChequeraDto>() {
					public SaldoChequeraDto mapRow(ResultSet rs, int idx)throws SQLException {
						SaldoChequeraDto dtoCheq = new SaldoChequeraDto();
						dtoCheq.setIdUsuario(rs.getInt("id_usuario"));
						dtoCheq.setNomUsuario(rs.getString("nombre"));
						return dtoCheq;
		        	}
				});
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:obtenerUsuarios");
		}
		return list;
	}
	
	public List<SaldoChequeraDto> obtenerBancos(int idUsuario, String idDivisa) {
		StringBuffer sql = new StringBuffer();
		List<SaldoChequeraDto> list = new ArrayList<SaldoChequeraDto>();
		
		try	{
			sql.append(" SELECT cb.id_banco, cb.desc_banco \n");
			sql.append(" FROM cat_ctas_personales ccp, cat_banco cb \n");
			sql.append(" WHERE ccp.id_banco = cb.id_banco \n");
			sql.append(" 	And ccp.beneficiario = "+ idUsuario +" \n");
			sql.append(" 	And ccp.id_divisa = '"+ Utilerias.validarCadenaSQL(idDivisa) +"' \n");
			sql.append(" 	And ccp.id_estatus_cta = 'A' \n");
			sql.append(" ORDER BY cb.desc_banco ");
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper<SaldoChequeraDto>() {
				public SaldoChequeraDto mapRow(ResultSet rs, int idx)throws SQLException {
					SaldoChequeraDto dtoCheq = new SaldoChequeraDto();
					dtoCheq.setIdBanco(rs.getInt("id_banco"));
					dtoCheq.setDescBanco(rs.getString("desc_banco"));
					return dtoCheq;
	        	}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:obtenerBancos");
		}
		return list;
	}
	
	public List<SaldoChequeraDto> obternerChequeras(int idBanco, int idUsuario, String idDivisa) {
		StringBuffer sql = new StringBuffer();
		List<SaldoChequeraDto> list = new ArrayList<SaldoChequeraDto>();
		
		try	{
			sql.append(" SELECT * \n");
			sql.append(" FROM cat_ctas_personales \n");
			sql.append(" WHERE beneficiario = "+ idUsuario +" \n");
			
			if(idBanco != 0)
				sql.append(" 	And id_banco = "+ idBanco +" \n");
			
			if(!idDivisa.equals("") && !idDivisa.equals("0"))
				sql.append(" 	And id_divisa = '"+ Utilerias.validarCadenaSQL(idDivisa) +"' \n");
			
			sql.append(" 	And id_estatus_cta = 'A' ");
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper<SaldoChequeraDto>() {
				public SaldoChequeraDto mapRow(ResultSet rs, int idx)throws SQLException {
					SaldoChequeraDto dtoCheq = new SaldoChequeraDto();
					dtoCheq.setIdChequera(rs.getString("id_chequera"));
					return dtoCheq;
	        	}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:obternerChequeras");
		}
		return list;
	}
	
	

	public List<SaldoChequeraDto> obtenerNvoBancos() {
		StringBuffer sql = new StringBuffer();
		List<SaldoChequeraDto> list = new ArrayList<SaldoChequeraDto>();
		
		try	{
			sql.append(" SELECT id_banco, desc_banco \n");
			sql.append(" FROM cat_banco \n");
			sql.append(" WHERE nac_ext = 'N' \n");
			sql.append(" ORDER BY desc_banco ");
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper<SaldoChequeraDto>() {
				public SaldoChequeraDto mapRow(ResultSet rs, int idx)throws SQLException {
					SaldoChequeraDto dtoCheq = new SaldoChequeraDto();
					dtoCheq.setIdBanco(rs.getInt("id_banco"));
					dtoCheq.setDescBanco(rs.getString("desc_banco"));
					return dtoCheq;
	        	}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:obtenerNvoBancos");
		}
		return list;
	}
	
	public int insertaNuevo(String params) {
		int iRegAfec = 0;
		StringBuffer sql = new StringBuffer();
		Gson gson = new Gson();
		List<Map<String, String>> datos = gson.fromJson(params, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		globalSingleton = GlobalSingleton.getInstancia();
		
		try {
			sql.append(" insert into cat_contrato_proveedor \n");	
			sql.append(" (no_contrato,desc_contrato,monto_original,monto_pagado,monto_adeudo,no_persona,fecha_inicial,fecha_final,no_pagos,estatus) \n");
			sql.append(" VALUES('"+ Utilerias.validarCadenaSQL(datos.get(0).get("noContrato")) +"', '"+ Utilerias.validarCadenaSQL(datos.get(0).get("descripcion")) +"', "+ Utilerias.validarCadenaSQL(datos.get(0).get("montoOrig")) +", \n");
			sql.append(" 		"+ Utilerias.validarCadenaSQL(datos.get(0).get("montoPag")) +", ("+ Utilerias.validarCadenaSQL(datos.get(0).get("montoOrig")) +")-("+ Utilerias.validarCadenaSQL(datos.get(0).get("montoPag")) +"), "+ Utilerias.validarCadenaSQL(datos.get(0).get("persona")) +", \n");			 
			sql.append(" 		'"+ Utilerias.validarCadenaSQL(datos.get(0).get("fecIni"))+"','"+ Utilerias.validarCadenaSQL(datos.get(0).get("fecFin"))+"', "+ Utilerias.validarCadenaSQL(datos.get(0).get("noPagos")) +",'A')");
			
			System.out.println("insercion "+sql.toString());
			
			iRegAfec = jdbcTemplate.update(sql.toString());
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:updateSaldos");
		}
		return iRegAfec;
	}
	
	
	public int modificarContrato(String params) {
		int iRegAfec = 0;
		StringBuffer sql = new StringBuffer();
		Gson gson = new Gson();
		List<Map<String, String>> datos = gson.fromJson(params, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		globalSingleton = GlobalSingleton.getInstancia();
		
		try {
			sql.append(" update cat_contrato_proveedor set \n");	
			sql.append(" no_contrato = '"+ Utilerias.validarCadenaSQL(datos.get(0).get("noContrato")) +"' ,desc_contrato = '"+ Utilerias.validarCadenaSQL(datos.get(0).get("descripcion")) +"',monto_original = "+ Utilerias.validarCadenaSQL(datos.get(0).get("montoOrig")) +", \n");
			sql.append(" monto_pagado = "+ Utilerias.validarCadenaSQL(datos.get(0).get("montoPag")) +",monto_adeudo = ("+ Utilerias.validarCadenaSQL(datos.get(0).get("montoOrig")) +")-("+ Utilerias.validarCadenaSQL(datos.get(0).get("montoPag")) +"),");
			sql.append(" fecha_inicial='"+ Utilerias.validarCadenaSQL(datos.get(0).get("fecIni"))+"',fecha_final = '"+ Utilerias.validarCadenaSQL(datos.get(0).get("fecFin"))+"',no_pagos =  "+ Utilerias.validarCadenaSQL(datos.get(0).get("noPagos")) +" \n");			 
			sql.append(" where id_contrato = "+ Utilerias.validarCadenaSQL(datos.get(0).get("folio")) +"");
			
			System.out.println("update"+sql.toString());
			
			iRegAfec = jdbcTemplate.update(sql.toString());
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:updateSaldos");
		}
		return iRegAfec;
	}
	
	public int insertaNuevoCP(String params) {
		int iRegAfec = 0;
		StringBuffer sql = new StringBuffer();
		Gson gson = new Gson();
		List<Map<String, String>> datos = gson.fromJson(params, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		globalSingleton = GlobalSingleton.getInstancia();
		
		try {
			sql.append(" insert into cat_ctas_personales \n");	
			sql.append(" (beneficiario,id_banco,id_chequera,id_clabe,desc_chequera,id_divisa,saldo_inicial,ingresos,egresos,saldo_final,usuario_modif,fecha_modif,id_estatus_cta) \n");
			sql.append(" VALUES("+ Utilerias.validarCadenaSQL(datos.get(0).get("idUsuario")) +", "+ Utilerias.validarCadenaSQL(datos.get(0).get("idBanco")) +", '"+ Utilerias.validarCadenaSQL(datos.get(0).get("idChequera")) +"','', \n");
			sql.append(" 		'"+ Utilerias.validarCadenaSQL(datos.get(0).get("clabe")) +"', '"+ Utilerias.validarCadenaSQL(datos.get(0).get("idDivisa")) +"', "+ Utilerias.validarCadenaSQL(datos.get(0).get("saldoIni")) +", "+ Utilerias.validarCadenaSQL(datos.get(0).get("saldoIng")) +", \n");			 
			sql.append(" 		"+ Utilerias.validarCadenaSQL(datos.get(0).get("saldoEgr"))+","+ Utilerias.validarCadenaSQL(datos.get(0).get("saldoFin"))+", " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + ",'0','A')");
			
			System.out.println("insercion "+sql.toString());
			
			iRegAfec = jdbcTemplate.update(sql.toString());
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:updateSaldos");
		}
		return iRegAfec;
	}
	
	
	public int modificarContratoCP(String params) {
		int iRegAfec = 0;
		StringBuffer sql = new StringBuffer();
		Gson gson = new Gson();
		List<Map<String, String>> datos = gson.fromJson(params, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		globalSingleton = GlobalSingleton.getInstancia();
		gral = new ConsultasGenerales(jdbcTemplate);
		
		try {
			sql.append(" update cat_ctas_personales set  \n");	
			sql.append(" id_banco =  "+ Utilerias.validarCadenaSQL(datos.get(0).get("idBanco"))+",id_chequera = '"+ Utilerias.validarCadenaSQL(datos.get(0).get("idChequera")) +"',desc_chequera = '"+ Utilerias.validarCadenaSQL(datos.get(0).get("clabe")) +"', saldo_inicial = "+ Utilerias.validarCadenaSQL(datos.get(0).get("saldoIni")) +",ingresos = "+ Utilerias.validarCadenaSQL(datos.get(0).get("saldoIng")) +", \n");
			sql.append(" egresos = "+ Utilerias.validarCadenaSQL(datos.get(0).get("saldoEgr")) +", saldo_final = "+ Utilerias.validarCadenaSQL(datos.get(0).get("saldoFin"))+", usuario_modif = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + ",fecha_modif = '"+ funciones.ponerFechaSola(gral.obtenerFechaHoy())+"'\n");
			sql.append(" where beneficiario =  "+ Utilerias.validarCadenaSQL(datos.get(0).get("idUsuario") )+" \n");
			sql.append(" and id_divisa = '"+ Utilerias.validarCadenaSQL(datos.get(0).get("idDivisa")) +"' \n");
			sql.append(" and id_banco = "+ Utilerias.validarCadenaSQL(datos.get(0).get("bancoMod")) +" \n");
			sql.append(" and id_chequera = '"+ Utilerias.validarCadenaSQL(datos.get(0).get("chequeraMod")) +"'");
			
			System.out.println("update "+sql.toString());
			
			iRegAfec = jdbcTemplate.update(sql.toString());
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:updateSaldos");
		}
		return iRegAfec;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<SaldoChequeraDto> obtenerRegistros(String params) {
		StringBuffer sql = new StringBuffer();
		List<SaldoChequeraDto> list = new ArrayList<SaldoChequeraDto>();
		Gson gson = new Gson();
		List<Map<String, String>> datos = gson.fromJson(params, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
		try	{
			sql.append(" select ccp.id_contrato,p.razon_social,ccp.no_contrato,ccp.desc_contrato,ccp.monto_original, \n");
			sql.append(" ccp.monto_pagado,ccp.monto_adeudo,ccp.no_persona,ccp.fecha_inicial,ccp.fecha_final,ccp.no_pagos  \n");
			sql.append(" from cat_contrato_proveedor ccp, persona p  \n");
			sql.append(" where ccp.no_persona = p.no_persona \n");
			
			if(!datos.get(0).get("empresa").equals("") && !datos.get(0).get("empresa").equals("0"))
				sql.append(" 	and SUBSTRING(p.equivale_persona,1,3) = "+ Utilerias.validarCadenaSQL(datos.get(0).get("empresa")) +" \n");
			
			if(!datos.get(0).get("idUsuario").equals("") && !datos.get(0).get("idUsuario").equals("0"))
				sql.append(" 	and ccp.no_persona = "+ Utilerias.validarCadenaSQL(datos.get(0).get("idUsuario")) +" \n");
			
			if(!datos.get(0).get("contrato").equals("") && !datos.get(0).get("contrato").equals("0"))
				sql.append(" 	and ccp.id_contrato = "+ Utilerias.validarCadenaSQL(datos.get(0).get("contrato")) +" \n");
			
			sql.append(" 	and ccp.estatus = 'A' \n");
						
			list = jdbcTemplate.query(sql.toString(), new RowMapper() {
				public ParamBusquedaMovimientoDto mapRow(ResultSet rs, int idx)throws SQLException {
					ParamBusquedaMovimientoDto dtoCheq = new ParamBusquedaMovimientoDto();
					dtoCheq.setIdContrato(rs.getInt("id_contrato"));
					dtoCheq.setRazonSocial(rs.getString("razon_social"));
					dtoCheq.setNoContrato2(rs.getString("no_contrato"));
					dtoCheq.setDescContrato(rs.getString("desc_contrato"));
					dtoCheq.setMontoOriginal(rs.getDouble("monto_original"));
					dtoCheq.setMontoPagado(rs.getDouble("monto_pagado"));
					dtoCheq.setMontoAdeudo(rs.getDouble("monto_adeudo"));
					dtoCheq.setNoPersona(rs.getInt("no_persona"));
					dtoCheq.setFechaInicial(rs.getString("fecha_inicial"));
					dtoCheq.setFechaFinal(rs.getString("fecha_final"));
					dtoCheq.setNoPagos(rs.getInt("no_pagos"));
					return dtoCheq;
	        	}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:obtenerRegistros");
		}
		return list;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<SaldoChequeraDto> obtenerRegistrosCP(String params) {
		StringBuffer sql = new StringBuffer();
		List<SaldoChequeraDto> list = new ArrayList<SaldoChequeraDto>();
		Gson gson = new Gson();
		List<Map<String, String>> datos = gson.fromJson(params, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
		try	{
			sql.append(" select ccp.beneficiario,su.nombre + ' ' + su.apellido_materno + ' ' + su.apellido_paterno as nomUsuario,ccp.id_banco, \n");
			sql.append(" cb.desc_banco,ccp.id_chequera,ccp.desc_chequera,ccp.id_divisa,ccp.saldo_inicial,ccp.ingresos,ccp.egresos,ccp.saldo_final \n");
			sql.append(" from cat_ctas_personales ccp,SEG_USUARIO su,cat_banco cb  \n");
			sql.append(" where su.id_usuario = ccp.beneficiario  \n");
			sql.append(" and cb.id_banco = ccp.id_banco  \n");
			
			if(!datos.get(0).get("idUsuario").equals("") && !datos.get(0).get("idUsuario").equals("0"))
				sql.append(" 	and ccp.beneficiario = "+ Utilerias.validarCadenaSQL(datos.get(0).get("idUsuario")) +" \n");
			
			if(!datos.get(0).get("idDivisa").equals("") && !datos.get(0).get("idDivisa").equals("0"))
				sql.append(" 	and ccp.id_divisa = '"+Utilerias.validarCadenaSQL( datos.get(0).get("idDivisa")) +"' \n");
			
			if(!datos.get(0).get("idBanco").equals("") && !datos.get(0).get("idBanco").equals("0"))
				sql.append(" 	and ccp.id_banco = "+ Utilerias.validarCadenaSQL(datos.get(0).get("idBanco")) +" \n");
			
			if(!datos.get(0).get("idChequera").equals("") && !datos.get(0).get("idChequera").equals("0"))
				sql.append(" 	and ccp.id_chequera = '"+ Utilerias.validarCadenaSQL(datos.get(0).get("idChequera")) +"' \n");
			
			sql.append(" 	and ccp.id_estatus_cta = 'A'");
			System.out.println("busqueda "+sql.toString());
						
			list = jdbcTemplate.query(sql.toString(), new RowMapper() {
				public ParamBusquedaMovimientoDto mapRow(ResultSet rs, int idx)throws SQLException {
					ParamBusquedaMovimientoDto dtoCheq = new ParamBusquedaMovimientoDto();
					dtoCheq.setIdUsuario(rs.getInt("beneficiario"));
					dtoCheq.setNomUsuario(rs.getString("nomUsuario"));
					dtoCheq.setIdBanco(rs.getInt("id_banco"));
					dtoCheq.setDescBanco(rs.getString("desc_banco"));
					dtoCheq.setIdChequera(rs.getString("id_chequera"));
					dtoCheq.setDescChequera(rs.getString("desc_chequera"));
					dtoCheq.setIdDivisa(rs.getString("id_divisa"));
					dtoCheq.setSaldoInicial(rs.getDouble("saldo_inicial"));
					dtoCheq.setAbono(rs.getDouble("ingresos"));
					dtoCheq.setCargo(rs.getDouble("egresos"));
					dtoCheq.setSaldoFinal(rs.getDouble("saldo_final"));
					return dtoCheq;
	        	}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:obtenerRegistrosCP");
		}
		return list;
	}
	
	public List<Map<String, Object>> reporteCuentasPersonales(SaldoChequeraDto datos) {
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
		
		try {
			sql.append(" SELECT su.nombre + ' ' + su.apellido_paterno + ' ' +  su.apellido_materno as nombre, cb.desc_banco, ccp.id_chequera, \n");
			sql.append("	ccp.desc_chequera, cd.desc_divisa, ccp.saldo_inicial, ccp.ingresos, ccp.egresos, ccp.saldo_final, ce.desc_estatus \n");
			sql.append(" FROM cat_ctas_personales ccp, SEG_USUARIO su, cat_banco cb, cat_divisa cd, cat_estatus ce \n");
			sql.append(" WHERE ccp.beneficiario = su.id_usuario \n");
			sql.append("	And ccp.id_banco = cb.id_banco \n");
			sql.append("	And ccp.id_divisa = cd.id_divisa \n");
			sql.append("	And ccp.id_estatus_cta = ce.id_estatus \n");
			sql.append("	And ce.clasificacion = 'PER' \n");
			sql.append(" 	And ccp.beneficiario = "+ Utilerias.validarCadenaSQL(datos.getIdUsuario()) +" \n");
			
			if(!datos.getIdDivisa().equals("") && !datos.getIdDivisa().equals("0"))
				sql.append(" 	And ccp.id_divisa = '"+ Utilerias.validarCadenaSQL(datos.getIdDivisa()) +"' \n");
			
			if(datos.getIdBanco() != 0)
				sql.append(" 	And ccp.id_banco = "+ Utilerias.validarCadenaSQL(datos.getIdBanco()) +" \n");
			
			if(!datos.getIdChequera().equals("") && !datos.getIdChequera().equals("0"))
				sql.append(" 	And ccp.id_chequera = '"+ Utilerias.validarCadenaSQL(datos.getIdChequera()) +"' \n");
			
			System.out.println("Query reporte cuentas personales: " + sql);
			
			listResult = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, Object>>() {
				public Map<String, Object> mapRow(ResultSet rs, int idx)throws SQLException {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("nomUsuario", rs.getString("nombre"));
					map.put("descBanco", rs.getString("desc_banco"));
					map.put("descChequera", rs.getString("desc_chequera"));
					map.put("idChequera", rs.getString("id_chequera"));
					map.put("descDivisa", rs.getString("desc_divisa"));
					map.put("saldoInicial", rs.getDouble("saldo_inicial"));
					map.put("egreso", rs.getDouble("egresos"));
					map.put("ingreso", rs.getDouble("ingresos"));
					map.put("saldoFinal", rs.getDouble("saldo_final"));
					map.put("estatus", rs.getString("desc_estatus"));
					return map;
	        	}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:reporteCuentasPersonales");
		}
		return listResult;
	}
	
	public List<Map<String, Object>> reporteContratosProveedores(SaldoChequeraDto datos) {
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
		
			try {
			sql.append(" select e.nom_empresa,p.razon_social,ccp.no_contrato,ccp.desc_contrato,ccp.monto_original, \n");
			sql.append(" ccp.monto_pagado,ccp.monto_adeudo,ccp.fecha_inicial,ccp.fecha_final,ccp.no_pagos  \n");
			sql.append(" from cat_contrato_proveedor ccp, persona p,empresa e  \n");
			sql.append(" where ccp.no_persona = p.no_persona \n");
			sql.append(" and e.no_empresa = "+ datos.getNoEmpresa() +"\n");
			
			if(datos.getNoEmpresa()!=0)
				sql.append(" 	and SUBSTRING(p.equivale_persona,1,3) = "+ Utilerias.validarCadenaSQL(datos.getNoEmpresa()) +" \n");
			
			if(datos.getIdUsuario()!= 0)
				sql.append(" 	and ccp.no_persona = "+ Utilerias.validarCadenaSQL(datos.getIdUsuario()) +" \n");
			
			if(datos.getIdContrato()!=0)
				sql.append(" 	and ccp.id_contrato = "+ Utilerias.validarCadenaSQL(datos.getIdContrato()) +" \n");
			
			sql.append(" 	and ccp.estatus = 'A' \n");
			
			System.out.println("reporte contratos "+sql.toString());
			
			
			listResult = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, Object>>() {
				public Map<String, Object> mapRow(ResultSet rs, int idx)throws SQLException {

					Map<String, Object> map = new HashMap<String, Object>();
					map.put("empresa", rs.getString("nom_empresa"));
					map.put("razonSocial", rs.getString("razon_social"));
					map.put("noContrato", rs.getString("no_contrato"));
					map.put("descContrato", rs.getString("desc_contrato"));
					map.put("montoOriginal", rs.getDouble("monto_original"));
					map.put("montoPagado", rs.getDouble("monto_pagado"));
					map.put("montoAdeudo", rs.getDouble("monto_adeudo"));
					map.put("fecIni", rs.getString("fecha_inicial"));
					map.put("fecFin", rs.getString("fecha_final"));
					map.put("noPagos", rs.getInt("no_pagos"));
					return map;
	        	}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:reporteContratosProveedores");
		}
		return listResult;
	}
	
	public List<SaldoChequeraDto> obtenerProveedor(int noEmpresa) {
		StringBuffer sql = new StringBuffer();
		List<SaldoChequeraDto> list = new ArrayList<SaldoChequeraDto>();
		String noEmpresaStr;
		
		if(Integer.toString(noEmpresa).length() == 1)
			noEmpresaStr = '0' + Integer.toString(noEmpresa);
		else
			noEmpresaStr = Integer.toString(noEmpresa); 
		
		try	{
			sql.append(" SELECT * FROM persona WHERE substring(equivale_persona, 0, 3) = '"+ noEmpresaStr +"' and id_tipo_persona = 'P' ");
		    
			list = jdbcTemplate.query(sql.toString(), new RowMapper<SaldoChequeraDto>() {
				public SaldoChequeraDto mapRow(ResultSet rs, int idx)throws SQLException {
					SaldoChequeraDto dtoCheq = new SaldoChequeraDto();
					dtoCheq.setIdUsuario(rs.getInt("no_persona"));
					dtoCheq.setNomUsuario(rs.getString("razon_social"));
					return dtoCheq;
	        	}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:obtenerProveedor");
		}
		return list;
	}
	
	public List<LlenaComboGralDto>llenarComboBeneficiario(LlenaComboGralDto dto){
		
		String cond="";
		cond=dto.getCondicion();
		dto.setCampoDos("rTRIM(COALESCE(razon_social,'')) ");
		
		cond = Utilerias.validarCadenaSQL(cond);
		
		if(dto.isRegistroUnico()){
			dto.setCondicion("equivale_persona='"+cond+"'");
		}else{
			dto.setCondicion("id_tipo_persona='P'	"
					+"	AND no_empresa in(552,217)"
					+"	AND ((razon_social like '"+cond+"%'"     
					+"	or paterno like '"+cond+"%'" 
					+"	or materno like '"+cond+"%'"   
					+"	or nombre like '"+cond+"%' )" 
					+"	or (no_persona like '"+cond+"%'))");	
		}
		
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.llenarComboGral(dto);
	}
	
	public List<SaldoChequeraDto> obtenerContratos(int idUsuario) {
		StringBuffer sql = new StringBuffer();
		List<SaldoChequeraDto> list = new ArrayList<SaldoChequeraDto>();
		
		try	{
			sql.append(" SELECT id_contrato,no_contrato FROM cat_contrato_proveedor \n");
			sql.append(" WHERE no_persona = "+ idUsuario +" ");
			sql.append(" AND estatus = 'A' ");
		    
			list = jdbcTemplate.query(sql.toString(), new RowMapper<SaldoChequeraDto>() {
				public SaldoChequeraDto mapRow(ResultSet rs, int idx)throws SQLException {
					SaldoChequeraDto dtoCheq = new SaldoChequeraDto();
					dtoCheq.setIdContrato(rs.getInt("id_contrato"));
					dtoCheq.setNoContrato(rs.getString("no_contrato"));
					return dtoCheq;
	        	}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:obtenerContratos");
		}
		return list;
	}
	
	public int eliminarContrato(int reg){
		int iRegAfec = 0;
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("update cat_contrato_proveedor set estatus = 'X' \n");
			sbSql.append("WHERE id_contrato = "+ reg +"");
			
			iRegAfec = jdbcTemplate.update(sbSql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:eliminarContrato");
		}
		return iRegAfec;
	}
	
	public int insertarMotivo (String motivo, int noFolioDet, String origen){
		int iRegAfec = 0;
		StringBuffer sbSql = new StringBuffer();
		try{
			if(origen.equals("M")){
				sbSql.append("update movimiento set observacion = 'observacion/ "+ Utilerias.validarCadenaSQL(motivo) +"' \n");
			}else if(origen.equals("H")){
				sbSql.append("update hist_movimiento set observacion = 'observacion/ "+ Utilerias.validarCadenaSQL(motivo) +"' \n");
			}
			sbSql.append("WHERE NO_FOLIO_DET = "+ noFolioDet +"");
			//System.out.println("Motivo"+sbSql);
			iRegAfec = jdbcTemplate.update(sbSql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:insertarMotivo");
		}
		return iRegAfec;
	}
	
	public int eliminarCP(int banco, String chequera){
		int iRegAfec = 0;
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("update cat_ctas_personales set id_estatus_cta = 'X' \n");
			sbSql.append("where id_banco = "+ banco +" \n");
			sbSql.append("and id_chequera = '"+ Utilerias.validarCadenaSQL(chequera)+"'");
			
			iRegAfec = jdbcTemplate.update(sbSql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:eliminarContrato");
		}
		return iRegAfec;
	}
	
	/*
	 * Agregado por Yec para cancelar movimientos
	 */
	public String updateMovimiento(String noFolioDet , String poheaders , String noEmpresa, String fecPropuesta ){
		String resultado="Error";
		
		String fechaPropone= funciones.ponerFechaSola(fecPropuesta);
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("update movimiento");
			sbSql.append("\n set id_estatus_mov  = 'X'");
			sbSql.append("\n where ID_TIPO_OPERACION = 3000");
			if(poheaders == null || poheaders.trim().equals("")){
				sbSql.append("\n  and NO_FOLIO_DET = "+ noFolioDet +" ");
				sbSql.append("\n and (CVE_CONTROL is null or CVE_CONTROL ='' )");
			}else{
				sbSql.append("\n  and po_headers = '"+ poheaders +"' ");
				sbSql.append("\n  and fec_propuesta = '"+ fechaPropone +"' ");
				sbSql.append("\n  and no_empresa = "+ noEmpresa +" ");
			}
			sbSql.append("\n and ID_ESTATUS_MOV in ('N','C')");
			
			System.out.println("updateMovimiento \n"+sbSql.toString()+ " \n updateMovimiento");
			int res = jdbcTemplate.update(sbSql.toString());
			if(res!=0)
				resultado="Exito";
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:eliminarContrato");
		}return resultado;
	} 
	
	public void insertarBitacoraRentas(RevividorDto revividorDto, BitacoraChequesDto bitacoraDto){
		StringBuffer sb = new StringBuffer();
		try{
			sb.append(" select coalesce(max(id_causa),0) as id from cat_causa_regreso where desc_causa like '%" + revividorDto.getObservaciones() + "%'");
			int r= jdbcTemplate.queryForInt(sb.toString());
			if(r > 499){
				String fecha= globalSingleton.getInstancia().getFechaHoy()+"";
				String fec[] = fecha.split("-");
				sb.delete(0, sb.length());
				sb.append(" select max(equivale_persona) from persona  where razon_social like '"+ bitacoraDto.getBeneficiario()+"'");
				String noBenef = jdbcTemplate.queryForInt(sb.toString())+"";
				sb.delete(0, sb.length());
				sb.append(" insert into BITACORA_PAGO_RECHAZADO \n");
				sb.append(" values ( \n");
				sb.append(" "+ revividorDto.getNoEmpresa() +", \n");
				sb.append(" "+ bitacoraDto.getNoFolioDet() +", \n");
				sb.append(" '"+ revividorDto.getNoDocumento() +"', \n");
				sb.append(" "+noBenef+ ", \n"); //noBenef
				sb.append(" "+ revividorDto.getImporte()+", \n");
				sb.append("'"+ fec[2]+"-"+fec[1]+"-"+ fec[0]+"', \n"); //fecharechazo
				sb.append(""+ globalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+" ,\n"); //usuario
				sb.append(" "+revividorDto.getIdFormaPago()+",\n"); //tipogasto
				sb.append("'R' , \n"); //estatus
				sb.append("'"+ revividorDto.getObservaciones() +"')");
				System.out.println(sb.toString());
				jdbcTemplate.update(sb.toString());
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:insertarBitacoraRentas");
		}
	}
	
	
	/*
	 * Agregado por YEC 
	 * 09 de febrero del 2016
	 * Reporte de fondeos del dia 
	 */
	public List<Map<String,String>> reporteFondeoSubReporte(String fInicio,String fFin, String usuario){
		List<Map<String, String>> resultado = new ArrayList<Map<String,String>>();
		StringBuffer sql = new StringBuffer();
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			sql.append("\n select id_grupo_flujo,desc_grupo_flujo,nom_empresa,desc_banco,id_divisa,id_chequera,id_banco, sum(importe) as importe , tipo, cont from (");
			
			//Cheques
			sql.append("\n  select top 100 ge.id_grupo_flujo, cf.desc_grupo_flujo,m1.id_divisa,count(m1.id_chequera) as cont,m1.id_chequera,m1.id_banco ,cb.desc_banco,");
			sql.append("\n 'cheque' as tipo , sum(m1.importe) as importe");
			sql.append("\n  ,e.nom_empresa");
			sql.append("\n from movimiento m1,cat_banco cb , empresa e , grupo_empresa ge , cat_grupo_flujo cf");
			sql.append("\n where  m1.id_banco=cb.id_banco");
			sql.append("\n and m1.no_empresa = e.no_empresa");
			sql.append("\n and e.no_empresa = ge.no_empresa");
			sql.append("\n and ge.id_grupo_flujo= cf.id_grupo_flujo");
			sql.append("\n and m1.no_empresa in (select no_empresa from Usuario_Empresa where no_usuario = "+usuario+")");
			sql.append("\n and m1.fec_valor BETWEEN convert(datetime,'"+fInicio+"',103) AND convert(datetime,'"+ fFin +"',103)");
			sql.append("\n and m1.id_estatus_mov in('P','J','I','R','T')");
			sql.append("\n and m1.id_tipo_operacion = 3200");
			sql.append("\n and m1.id_forma_pago in(1,10)");
			sql.append("\n group by e.nom_empresa,ge.id_grupo_flujo, cf.desc_grupo_flujo,cb.desc_banco,m1.id_banco,m1.id_chequera,m1.id_divisa");
			
			sql.append("\n union all");
			
			//Transferencia
			sql.append("\n  select top 100 ge.id_grupo_flujo, cf.desc_grupo_flujo,m1.id_divisa,count(m1.id_chequera) as cont,m1.id_chequera,m1.id_banco ,cb.desc_banco,");
			sql.append("\n 'transferencia' as tipo , sum(m1.importe) as importe");
			sql.append("\n  ,e.nom_empresa");
			sql.append("\n from movimiento m1,cat_banco cb , empresa e , grupo_empresa ge , cat_grupo_flujo cf");
			sql.append("\n where  m1.id_banco=cb.id_banco");
			sql.append("\n and m1.no_empresa = e.no_empresa");
			sql.append("\n and e.no_empresa = ge.no_empresa");
			sql.append("\n and ge.id_grupo_flujo= cf.id_grupo_flujo");
			sql.append("\n and m1.no_empresa in (select no_empresa from Usuario_Empresa where no_usuario = "+usuario+")");
			sql.append("\n and m1.fec_valor BETWEEN convert(datetime,'"+fInicio+"',103) AND convert(datetime,'"+ fFin +"',103)");
			sql.append("\n and m1.id_estatus_mov in('T','P','K')");
			sql.append("\n and m1.id_tipo_operacion = 3200");
			sql.append("\n and m1.id_forma_pago in(3)");
			sql.append("\n group by e.nom_empresa,ge.id_grupo_flujo, cf.desc_grupo_flujo,cb.desc_banco,m1.id_banco,m1.id_chequera,m1.id_divisa");
			
			sql.append("\n union all");
			
			//Cargo cuenta
			sql.append("\n  select top 100 ge.id_grupo_flujo, cf.desc_grupo_flujo,m1.id_divisa,count(m1.id_chequera) as cont,m1.id_chequera,m1.id_banco ,cb.desc_banco,");
			sql.append("\n 'cargo cuenta' as tipo , sum(m1.importe) as importe");
			sql.append("\n  ,e.nom_empresa");
			sql.append("\n from movimiento m1,cat_banco cb , empresa e , grupo_empresa ge , cat_grupo_flujo cf");
			sql.append("\n where  m1.id_banco=cb.id_banco");
			sql.append("\n and m1.no_empresa = e.no_empresa");
			sql.append("\n and e.no_empresa = ge.no_empresa");
			sql.append("\n and ge.id_grupo_flujo= cf.id_grupo_flujo");
			sql.append("\n and m1.no_empresa in (select no_empresa from Usuario_Empresa where no_usuario = "+usuario+")");
			sql.append("\n and m1.fec_valor BETWEEN convert(datetime,'"+fInicio+"',103) AND convert(datetime,'"+ fFin +"',103)");
			sql.append("\n and m1.id_estatus_mov in('T','P','K')");
			sql.append("\n and m1.id_tipo_operacion = 3200");
			sql.append("\n and m1.id_forma_pago in(5)");
			sql.append("\n group by e.nom_empresa,ge.id_grupo_flujo, cf.desc_grupo_flujo,cb.desc_banco,m1.id_banco,m1.id_chequera,m1.id_divisa");
			
			sql.append("\n order by desc_banco,id_banco,id_chequera,tipo,id_divisa) consulta");
			sql.append("\n group by id_grupo_flujo,desc_grupo_flujo,nom_empresa,desc_banco,id_banco,id_chequera,id_divisa,tipo, cont");
			sql.append("\n order by id_divisa DESC,desc_grupo_flujo,nom_empresa,desc_banco,id_chequera");
			
			System.out.println("\n-----------Super consulta-----------------");
			System.out.println("\n "+sql.toString());
			System.out.println("\n ----------------------------");
			
			resultado = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>() {
				public Map<String, String> mapRow(ResultSet rs, int idx)throws SQLException {
					Map<String, String> map = new HashMap<String, String>();
					map.put("columna", rs.getString("id_grupo_flujo"));
					map.put("columna0", rs.getString("desc_grupo_flujo"));
					map.put("columna1", rs.getString("id_divisa"));
					map.put("columna2", rs.getString("desc_banco"));
					map.put("columna3", rs.getString("id_chequera"));
					map.put("columna4", "");
					map.put("columna5", "");
					map.put("columna6", "");
					map.put("columna7", ""+rs.getDouble("importe"));
					map.put("columna8", rs.getString("tipo"));
					map.put("columna9", rs.getString("cont"));
					map.put("columna10",rs.getString("nom_empresa"));
					return map;
	        	}
			});
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:reporteFondeoSubReporte");
		}return resultado;
	}
	
	/*
	 * OBJETIVO: Obtener el reporte de fondeo detallado
	 * de las fechas enviadas en los parametros.
	 */
	
	
	public List<Map<String,String>> reporteFondeo(String fInicio,String fFin, String usuario){
		List<Map<String, String>> resultado = new ArrayList<Map<String,String>>();
		StringBuffer sql = new StringBuffer();
		try {
			
			sql.append("\n select m.cve_control,CONVERT(char(10),m.fec_valor,103) as fecha ,cf.desc_grupo_flujo , ");
		    sql.append("\n e.nom_empresa ,cg.desc_grupo as centroCostos,");
			sql.append("\n cb.desc_banco , m.id_chequera  ,m.id_rubro, cr.desc_rubro , fp.desc_forma_pago , m.importe ");
	        sql.append("\n , ce.desc_estatus ,  m.id_divisa , p.equivale_persona as no_cliente ,p.razon_social ");
			sql.append("\n from  movimiento m , grupo_empresa ge , cat_grupo_flujo cf, empresa e ,cat_rubro cr , cat_grupo cg , cat_estatus ce, ");
			sql.append("\n cat_banco cb ,cat_forma_pago fp , persona p ");
		    sql.append("\n WHERE m.fec_valor BETWEEN convert(datetime,'"+fInicio+"',103) AND convert(datetime,'"+ fFin +"',103)");
			sql.append("\n  and m.id_banco=cb.id_banco ");
			sql.append("\n and m.no_empresa in (select no_empresa from Usuario_Empresa where no_usuario = "+usuario+")");
			sql.append("\n and m.id_forma_pago=fp.id_forma_pago ");
			sql.append("\n AND cr.id_grupo = cg.id_grupo ");
			sql.append("\n AND ce.clasificacion = 'MOV' ");
			sql.append("\n  AND ce.id_estatus = m.id_estatus_mov ");
			sql.append("\n  AND m.id_estatus_mov in('P','T','K','I','R','J') ");
			sql.append("\n  AND cr.id_rubro = m.id_rubro ");
			sql.append("\n AND m.id_tipo_operacion = 3200");
			sql.append("\n AND m.id_forma_pago in(1,3,10,5) ");
			sql.append("\n and e.no_empresa = ge.no_empresa");
			sql.append("\n  and m.no_cliente=p.no_persona");
			sql.append("\n and p.id_tipo_persona='P'");
			sql.append("\n and ge.id_grupo_flujo= cf.id_grupo_flujo ");
			sql.append("\n  AND m.no_empresa = e.no_empresa ");
			sql.append("\n  GROUP BY m.no_folio_det,m.cve_control,m.fec_valor, m.id_grupo ,m.no_empresa ,m.id_banco , ");
			sql.append("\n  m.id_chequera ,m.id_rubro ,m.id_forma_pago,  m.importe,m.ID_ESTATUS_MOV");
			sql.append("\n  , p.equivale_persona   ,m.id_divisa,ce.desc_estatus ,cr.desc_rubro  , e.nom_empresa ,ge.id_grupo_flujo, ");
			sql.append("\n   cf.desc_grupo_flujo,cg.desc_grupo ");
			sql.append("\n  , cb.desc_banco,fp.desc_forma_pago ,p.razon_social ");
			sql.append("\n  order by m.id_divisa desc, m.no_empresa , m.id_banco ,m.id_chequera");
			System.out.println(sql.toString());
	
			resultado = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>() {
				public Map<String, String> mapRow(ResultSet rs, int idx)throws SQLException {
					Map<String, String> map = new HashMap<String, String>();
					DecimalFormatSymbols symbols = new DecimalFormatSymbols();
					symbols.setDecimalSeparator('.');
					symbols.setGroupingSeparator(',');
					String pattern = "###,##0.00";
					DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
					Double importe=Double.parseDouble(rs.getString("IMPORTE"));
					map.put("columna1", rs.getString("CVE_CONTROL")); //idPropuesta
					map.put("columna2", rs.getString("FECHA")); //fecha
					map.put("columna3", rs.getString("DESC_GRUPO_FLUJO")); // grupo empresa
					map.put("columna4", rs.getString("NOM_EMPRESA")); //empresa
					map.put("columna5", rs.getString("CENTROCOSTOS")); //area centro costos 
					map.put("columna6", rs.getString("DESC_BANCO")); //banco desc
					map.put("columna7", rs.getString("ID_CHEQUERA")); //id chequera 
					map.put("columna8", rs.getString("ID_RUBRO")); //id rubro
					map.put("columna9", rs.getString("DESC_RUBRO")); //desc rubro
					map.put("columna10", rs.getString("DESC_FORMA_PAGO")); //forma pago desc
					map.put("columna11", "$"+decimalFormat.format(importe)+""); //importe
					map.put("columna12", rs.getString("desc_estatus")); //estatus
					map.put("columna13", rs.getString("NO_CLIENTE")); //no cliente
					map.put("columna14", rs.getString("RAZON_SOCIAL")); //razon social
					map.put("columna15", ""); //cuenta mayor
					map.put("columna16", rs.getString("ID_DIVISA")); //divisa
					
					return map;
	        	}
			});
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:reporteFondeo");
		}return resultado;
	}
	public String obtenEmpresaSet006(String noEmpresa) {
		StringBuffer sql = new StringBuffer();
		List<String> empresa = new ArrayList<String>();
		if(noEmpresa!=null && !noEmpresa.equals("0")){
			  sql.append("Select SOIEMP from set006 where SETEMP="+noEmpresa+" \n");
				 empresa = jdbcTemplate.query(sql.toString(), new RowMapper<String>() {
						public String mapRow(ResultSet rs, int idx) throws SQLException{
						return rs.getString("SOIEMP");
					}});

			}
		return empresa.get(0);
	}
	
	public List<Map<String, String>> armarReporteCXP(String noEmpresaR, String fecIni, String fecFin, String estatus) {
		List<Map<String, String>> resultado = new ArrayList<Map<String,String>>();
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("\n SELECT * FROM ZIMP_FACT WHERE DESC_PROPUESTA NOT LIKE 'MAN%'");
			if(noEmpresaR!=null && !noEmpresaR.equals("0")){
				sql.append("\n AND NO_EMPRESA='"+noEmpresaR+"'");
			}
			sql.append("\n AND FECHA_EXP BETWEEN convert(date,'"+fecIni+"',103) AND convert(date,'"+ fecFin +"',103)");
			if(estatus!= null && !estatus.equals("")){
				sql.append("\n AND ESTATUS='"+estatus+"'");
			}
			sql.append("\n ORDER BY NO_EMPRESA,NO_BENEF");
			System.out.println("REPORTE IMPORTACIONES CXP "+sql.toString());
			resultado = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>() {
				public Map<String, String> mapRow(ResultSet rs, int idx)throws SQLException {
					Map<String, String> map = new HashMap<String, String>();
					DecimalFormatSymbols symbols = new DecimalFormatSymbols();
					symbols.setDecimalSeparator('.');
					symbols.setGroupingSeparator(',');
					String pattern = "###,##0.00";
					DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
					Double importe=Double.parseDouble(rs.getString("IMPORTE"));
					Double importeSol=Double.parseDouble(rs.getString("IMP_SOLIC"));
					map.put("NO_EMPRESA", rs.getString("NO_EMPRESA")); 
					map.put("NO_DOC_SAP", rs.getString("NO_DOC_SAP"));
					map.put("SECUENCIA", rs.getString("SECUENCIA"));
					map.put("NO_FACTURA", rs.getString("NO_FACTURA"));
					map.put("FEC_FACT", rs.getString("FEC_FACT"));
					map.put("FEC_VALOR", rs.getString("FEC_VALOR"));
					map.put("NO_BENEF", rs.getString("NO_BENEF"));
					map.put("ID_DIVISA", rs.getString("ID_DIVISA"));
					map.put("CONCEPTO", rs.getString("CONCEPTO"));
					map.put("FORMA_PAGO", rs.getString("FORMA_PAGO"));
					map.put("CVE_LEYEN", rs.getString("CVE_LEYEN"));
					map.put("BENEF_ALT", rs.getString("BENEF_ALT"));
					map.put("ID_BCO_ALT", rs.getString("ID_BCO_ALT"));
					map.put("ID_CHQ_ALT", rs.getString("ID_CHQ_ALT"));
					map.put("ID_RUBRO", rs.getString("ID_RUBRO"));
					map.put("GPO_TESOR", rs.getString("GPO_TESOR"));
					map.put("COD_BLOQ", rs.getString("COD_BLOQ"));
					map.put("MANDANTE", rs.getString("MANDANTE"));
					map.put("IND_MAY_ES", rs.getString("IND_MAY_ES"));
					map.put("DIVISION", rs.getString("DIVISION"));
					map.put("IND_IVA", rs.getString("IND_IVA"));
					map.put("RFC", rs.getString("RFC"));
					map.put("TIPO_CAMB", rs.getString("TIPO_CAMB"));
					map.put("ID_CAJA", rs.getString("ID_CAJA"));
					map.put("FECHA_IMP", rs.getString("FECHA_IMP"));
					map.put("FECHA_EXP", rs.getString("FECHA_EXP"));
					map.put("ESTATUS", rs.getString("ESTATUS"));
					map.put("CAUSA_RECH", rs.getString("CAUSA_RECH"));
					map.put("ORIGEN", rs.getString("ORIGEN"));
					map.put("ESTATUS_COMPENSA", rs.getString("ESTATUS_COMPENSA"));
					map.put("ESTATUS_PROCESO", rs.getString("ESTATUS_PROCESO"));
					map.put("EMAIL_ALT", rs.getString("EMAIL_ALT"));
					map.put("CLABE", rs.getString("CLABE"));
					map.put("DESC_PROPUESTA", rs.getString("DESC_PROPUESTA"));
					map.put("BANCO_PAGADOR", rs.getString("BANCO_PAGADOR"));
					map.put("CHEQUERA_PAGADORA", rs.getString("CHEQUERA_PAGADORA"));
					map.put("FEC_PROPUESTA", rs.getString("FEC_PROPUESTA"));
					map.put("IND_RETENCION", rs.getString("IND_RETENCION"));
					map.put("CLASE_DOCTO", rs.getString("CLASE_DOCTO"));
					map.put("IMPORTE_ORIGINAL", rs.getString("IMPORTE_ORIGINAL"));
					map.put("IMPORTE_BASE", rs.getString("IMPORTE_BASE"));
					map.put("ID_AREA", rs.getString("ID_AREA"));
					map.put("DESC_AREA", rs.getString("DESC_AREA"));
					map.put("IMPORTE", "$"+decimalFormat.format(importe)+"");
					map.put("IMP_SOLIC", "$"+decimalFormat.format(importeSol)+"");
					map.put("centro_cto", rs.getString("centro_cto"));
					map.put("forma_pago_original", rs.getString("forma_pago_original"));
					map.put("chequera_benef", rs.getString("chequera_benef"));
					map.put("banco_benef", rs.getString("banco_benef"));
					map.put("no_cheque", rs.getString("no_cheque"));
					map.put("origen_mov", rs.getString("origen_mov"));
					map.put("posicion", rs.getString("posicion"));
					map.put("no_orden", rs.getString("no_orden"));
					map.put("clasif_factura", rs.getString("clasif_factura"));					
				return map;
	        	}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:reporteCXP");
		}return resultado;
	}
	
	
	public List<Map<String, String>> armarReporteDatosBancarios(String noEmpresaR, String fecIni, String fecFin, String estatus) {
		List<Map<String, String>> resultado = new ArrayList<Map<String,String>>();
		StringBuffer sql = new StringBuffer();
		Map<String, String> map2;
		try {
			sql.append("\n SELECT * FROM zimpcheqprov WHERE ");
			sql.append("\n FEC_MODIF BETWEEN convert(date,'"+fecIni+"',103) AND convert(date,'"+ fecFin +"',103)");
			if(noEmpresaR!=null && !noEmpresaR.equals("0")){
				sql.append("\n AND NO_EMPRESA='"+noEmpresaR+"'");
			}
			if(estatus!= null && !estatus.equals("")){
				sql.append("\n AND ESTATUS='"+estatus+"'");
			}
			sql.append("\n ORDER BY NO_EMPRESA,EQUIV_PER");
			System.out.println("REPORTE IMPORTACIONES dATOSbANCARIOS "+sql.toString());
			resultado = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>() {
				public Map<String, String> mapRow(ResultSet rs, int idx)throws SQLException {
					Map<String, String> map = new HashMap<String, String>();
					map.put("no_empresa", rs.getString("no_empresa")); 
					map.put("equiv_per", rs.getString("equiv_per"));
					map.put("id_tipo_persona", rs.getString("id_tipo_persona"));
					map.put("id_divisa", rs.getString("id_divisa"));
					map.put("id_banco", rs.getString("id_banco"));
					map.put("id_chequera", rs.getString("id_chequera"));
					map.put("plaza", rs.getString("plaza"));
					map.put("sucursal", rs.getString("sucursal"));
					map.put("swift_code", rs.getString("swift_code"));
					map.put("aba", rs.getString("aba"));
					map.put("especiales", rs.getString("especiales"));
					map.put("id_bank_true", rs.getString("id_bank_true"));
					map.put("id_bank_corresponding", rs.getString("id_bank_corresponding"));
					map.put("id_chequera_true", rs.getString("id_chequera_true"));
					map.put("id_clabe", rs.getString("id_clabe"));
					map.put("id_banco_anterior", rs.getString("id_banco_anterior"));
					map.put("id_chequera_anterior", rs.getString("id_chequera_anterior"));
					map.put("tipo_envio_layout", rs.getString("tipo_envio_layout"));
					map.put("fec_modif", rs.getString("fec_modif"));
					map.put("ESTATUS", rs.getString("estatus"));
					map.put("causa_rech", rs.getString("causa_rech"));
					map.put("fec_imp", rs.getString("fec_imp"));
					map.put("fec_exp", rs.getString("fec_exp"));
					map.put("desc_chequera", rs.getString("desc_chequera"));
					map.put("usuario_modif", rs.getString("usuario_modif"));
					map.put("aba_intermediario", rs.getString("aba_intermediario"));
					map.put("swift_intermediario", rs.getString("swift_intermediario"));
					map.put("swift_corresponsal", rs.getString("swift_corresponsal"));
					map.put("aba_corresponsal", rs.getString("aba_corresponsal"));
					map.put("referencia", rs.getString("referencia"));
					map.put("id_pais", rs.getString("id_pais"));
					map.put("id_region", rs.getString("id_region"));
					map.put("banco_corresponsal", rs.getString("banco_corresponsal"));
					map.put("id_pais_corresponsal", rs.getString("id_pais_corresponsal"));
					map.put("id_region_corresponsal", rs.getString("id_region_corresponsal"));
					map.put("IBAN", rs.getString("IBAN"));
					map.put("forfurthercred", rs.getString("forfurthercred"));
					map.put("CIE_Referencia", rs.getString("CIE_Referencia"));
					map.put("CIE_Concepto", rs.getString("CIE_Concepto"));
				
				return map;
	        	}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:reporteDatosBancarios");
		}
		
//		System.out.println("num de rechazados bancarios"+resultado.size());
		return resultado;
	}
	
	public List<Map<String, String>> armarReporteCXC(String noEmpresaR, String fecIni, String fecFin, String estatus) {
		List<Map<String, String>> resultado = new ArrayList<Map<String,String>>();
		StringBuffer sql = new StringBuffer();
		Map<String, String> map2=new HashMap<String, String>();
		try {
			sql.append("\n SELECT * FROM zimp_dep WHERE");
			sql.append("\n FECHA_EXP BETWEEN convert(date,'"+fecIni+"',103) AND convert(date,'"+ fecFin +"',103)");
			if(noEmpresaR!=null && !noEmpresaR.equals("0")){
				sql.append("\n AND NO_EMPRESA='"+noEmpresaR+"'");
			}	
			if(estatus!= null && !estatus.equals("")){
				sql.append("\n AND ESTATUS='"+estatus+"'");
			}
			sql.append("\n ORDER BY NO_EMPRESA,NO_BENEF");
			System.out.println("REPORTE IMPORTACIONES CXC "+sql.toString());
			resultado = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>() {
				public Map<String, String> mapRow(ResultSet rs, int idx)throws SQLException {
					Map<String, String> map = new HashMap<String, String>();
					DecimalFormatSymbols symbols = new DecimalFormatSymbols();
					symbols.setDecimalSeparator('.');
					symbols.setGroupingSeparator(',');
					String pattern = "###,##0.00";
					DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
					Double importe=Double.parseDouble(rs.getString("IMPORTE"));
					Double importeSol=Double.parseDouble(rs.getString("IMP_SOLIC"));
					map.put("NO_EMPRESA", rs.getString("NO_EMPRESA")); 
					map.put("NO_DOC_SAP", rs.getString("NO_DOC_SAP"));
					map.put("SECUENCIA", rs.getString("SECUENCIA"));
					map.put("NO_FACTURA", rs.getString("NO_FACTURA"));
					map.put("FEC_FACT", rs.getString("FEC_FACT"));
					map.put("FEC_VALOR", rs.getString("FEC_VALOR"));
					map.put("NO_BENEF", rs.getString("NO_BENEF"));
					map.put("IMPORTE", "$"+decimalFormat.format(importe)+"");
					map.put("IMP_SOLIC", "$"+decimalFormat.format(importeSol)+"");
					map.put("ID_DIVISA", rs.getString("ID_DIVISA"));
					map.put("CONCEPTO", rs.getString("CONCEPTO"));
					map.put("FORMA_PAGO", rs.getString("FORMA_PAGO"));
					map.put("ID_RUBRO", rs.getString("ID_RUBRO"));
					map.put("GPO_TESOR", rs.getString("GPO_TESOR"));
					map.put("ID_CAJA", rs.getString("ID_CAJA"));
					map.put("FECHA_IMP", rs.getString("FECHA_IMP"));
					map.put("FECHA_EXP", rs.getString("FECHA_EXP"));
					map.put("ESTATUS", rs.getString("ESTATUS"));
					map.put("CAUSA_RECH", rs.getString("CAUSA_RECH"));
					map.put("ORIGEN", rs.getString("ORIGEN"));
					map.put("REFERENCIA", rs.getString("REFERENCIA"));
					map.put("ESTATUS_COMPENSA", rs.getString("ESTATUS_COMPENSA"));				
				return map;
	        	}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:reporteCXP");
		}
//		map2.put("total_reg", String.valueOf(resultado.size()));
//		resultado.add(map2);
		return resultado;
	}
	
	
	public String validar3200(String movimiento[]){
		String resultado="";
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("select count(NOM_ARCH) from DET_ARCH_TRANSFER_AGRUP \n");
			sb.append("where NO_EMPRESA=" + movimiento [1] + "\n");
			sb.append(" and  ID_BANCO="+ movimiento [2] + "\n");
			sb.append(" and ID_CHEQUERA= '"+ movimiento [3] + "'\n");
			sb.append(" and PO_HEADERS = '"+ movimiento [0] + "'\n");
			System.out.println(sb.toString());
			
			int res= jdbcTemplate.queryForInt(sb.toString());
			if(res==0)
				resultado= "No se encuentra el resgistro " + movimiento [4];
		} catch (Exception e) {
			resultado= "Error al validar el registro " + movimiento [4];
		}return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> obtenerDatosExcelSaldosS(String idEmpresa, String nomEmpresa , String idBancoInf, String idBancoSup, String idChequera, String tipoChequera, String estatusTipoEmpresa, String usuario, String empresas) {
		List<Map<String, String>> resultado = null;
		StringBuffer sql = new StringBuffer();
		globalSingleton = GlobalSingleton.getInstancia();
		int iUsuario = funciones.convertirCadenaInteger(usuario);
		//parametros.put("fechaHoy", globalSingleton.getFechaHoy());
		//Date fechaHoy=funciones.ponerFechaDate(funciones.ponerFechaSola((globalSingleton.getFechaHoy())));
		sql.append("SELECT  ccb.id_banco, cb.desc_banco, ccb.no_empresa, coalesce(ccb.saldo_inicial,0) as saldo_inicial,\n");
		sql.append("coalesce(ccb.cargo,0) as cargo,e.nom_empresa,case when ccb.id_estatus_cta = 'X' then '*' else '' end as estatus, \n");
		sql.append("ccb.id_chequera, coalesce(ccb.abono,0) as abono, coalesce((ccb.saldo_inicial-ccb.cargo+ccb.abono),0) as saldo_final, coalesce(ccb.abono_sbc,0) as abono_sbc, coalesce(ccb.ult_cheq_impreso,0) as ult_cheq_impreso, ccb.id_divisa,  \n");
		sql.append("coalesce((SELECT coalesce(sum(m.importe),0) from movimiento m \n");
		sql.append("Where m.no_empresa = ccb.no_empresa \n");
		sql.append("and m.id_banco=ccb.id_banco \n");
		sql.append("and m.id_chequera = ccb.id_chequera \n");
		sql.append("and id_tipo_movto = 'E' \n");
		sql.append("and ((m.b_entregado = 'N' \n");
		sql.append("and id_estatus_mov in ('I','R')) or (id_estatus_mov in('T','P','J')))),0) as cheq_no_ent,\n");
		sql.append("coalesce((SELECT coalesce(sum(m.importe),0)   from movimiento m  \n");
		sql.append("Where m.no_empresa = ccb.no_empresa \n");
		sql.append("and m.id_banco=ccb.id_banco \n");
		sql.append("and m.id_chequera = ccb.id_chequera \n");
		sql.append("and id_tipo_movto = 'E' \n");
		sql.append("and ((m.b_entregado = 'N' \n");
		sql.append("and id_estatus_mov in ('I','R')) \n");
		sql.append("or (id_estatus_mov in('T','P','J')))) + ccb.saldo_final,0) as disp_en_chequera, \n");     
		sql.append("convert(date,ccb.fec_conciliacion,103)as fec_conciliacion , ccb.periodo_conciliacion, \n");
		sql.append("ctc.desc_chequera as desc_tipo_chequera,convert(date,ccb.fecha_banca,103) as fecha_banca, (select convert(date,fec_hoy,103) from fechas)as fechaHoy\n");
	
		sql.append(",(select coalesce(saldo_banco_ini,0) from fechas f,hist_cat_cta_banco hccb \n");
		sql.append("where hccb.id_chequera like ('"+funciones.validarCadena(idChequera)+"') AND hccb.no_empresa in("+funciones.validarCadena(empresas)+") \n");
		sql.append("AND hccb.id_banco between "+funciones.convertirCadenaInteger(idBancoInf)+" and " + funciones.convertirCadenaInteger(idBancoSup)+" \n"); 
		sql.append("AND saldo_banco_ini!=0 \n");
		sql.append("AND hccb.fec_valor<(select max(fec_ayer) as 'aye' from fechas) group by hccb.saldo_banco_ini) as saldo_inicial_banco, \n");
		sql.append("coalesce(ccb.saldo_banco_ini,0)as saldo_final_banco \n");
		
		sql.append("From  cat_cta_banco ccb,cat_banco cb, empresa e, cat_tipo_chequera ctc \n");
		sql.append("where ctc.tipo_chequera = ccb.tipo_chequera and ccb.id_banco = cb.id_banco and ccb.no_empresa = e.no_empresa \n");
		System.out.println(tipoChequera);
		if(tipoChequera.equals("C")){
			sql.append("and ccb.tipo_chequera = 'C' \n");
		}else if(tipoChequera.equals("M")){
			sql.append("and ccb.tipo_chequera = 'M' \n");
		}else if(tipoChequera.equals("P")){
			sql.append("and ccb.tipo_chequera = 'P' \n");
		}else if(tipoChequera.equals("I")){
			sql.append("and ccb.tipo_chequera = 'I' \n");
		}
		sql.append("and ccb.tipo_chequera <>'U' \n");
		if(empresas.equals("")){
			sql.append("and ccb.no_empresa in(SELECT u.no_empresa FROM usuario_empresa u WHERE u.no_usuario = " + iUsuario + " )\n");
			//sql.append("and ccb.no_empresa in(SELECT u.no_empresa FROM usuario_empresa u WHERE u.no_usuario = " + iUsuario + " and u.no_empresa in (32, 9,1208) )\n");
		}else{
			sql.append("and ccb.no_empresa in("+funciones.validarCadena(empresas)+")\n");
		}
		sql.append("and ccb.id_banco between " + funciones.convertirCadenaInteger(idBancoInf) + " and  " + funciones.convertirCadenaInteger(idBancoSup)+" \n");
		sql.append("and ccb.id_chequera like ('" + funciones.validarCadena(idChequera) + "') \n");
		sql.append("order by e.NO_EMPRESA,e.NOM_EMPRESA,ccb.id_banco,ccb.id_divisa\n");
		System.out.println("obtenerDatosExcelSaldosS: "+sql.toString());
		resultado = (List<Map<String, String>>)jdbcTemplate.query(sql.toString(), new RowMapper(){
			public Object mapRow(ResultSet rs, int i)
				throws SQLException {
				Map<String, Object> results = new HashMap<String, Object>();
	            results.put("id_banco", funciones.validarCadena(rs.getString("id_banco")));
	            results.put("desc_banco", funciones.validarCadena(rs.getString("desc_banco")));
	            results.put("no_empresa", funciones.validarCadena(rs.getString("no_empresa")));
	            results.put("saldo_inicial", funciones.validarCadena(rs.getString("saldo_inicial")));
	            results.put("cargo", funciones.validarCadena(rs.getString("cargo")));
	            results.put("nom_empresa", funciones.validarCadena(rs.getString("nom_empresa")));
	            results.put("estatus", funciones.validarCadena(rs.getString("estatus")));
	            results.put("fechaHoy", funciones.validarCadena(rs.getString("fechaHoy")));
	            results.put("id_chequera", funciones.validarCadena(rs.getString("id_chequera")));
	            results.put("abono", funciones.validarCadena(rs.getString("abono")));
	            results.put("saldo_final", funciones.validarCadena(rs.getString("saldo_final")));
	            
	            results.put("saldo_inicial_banco", funciones.validarCadena(rs.getString("saldo_inicial_banco")));
	            results.put("saldo_final_banco", funciones.validarCadena(rs.getString("saldo_final_banco")));

	            results.put("abono_sbc", funciones.validarCadena(rs.getString("abono_sbc")));
	            results.put("ult_cheq_impreso", funciones.validarCadena(rs.getString("ult_cheq_impreso")));
	            results.put("id_divisa", funciones.validarCadena(rs.getString("id_divisa")));
	            results.put("cheq_no_ent", funciones.validarCadena(rs.getString("cheq_no_ent")));
	            results.put("desc_tipo_chequera", funciones.validarCadena(rs.getString("desc_tipo_chequera")));
	            results.put("disp_en_chequera",funciones.validarCadena(rs.getString("disp_en_chequera")));
	            results.put("fecha_banca",funciones.validarCadena(rs.getString("fecha_banca")));
	            return results;
			}
		});
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> obtenerDatosReporteSaldosS(Map<String, Object> parametros) {
		List<Map<String, Object>> resultado = null;
		StringBuffer sql = new StringBuffer();
		globalSingleton = GlobalSingleton.getInstancia();
		int iUsuario = funciones.convertirCadenaInteger(parametros.get("usuario").toString());
		parametros.put("fechaHoy", globalSingleton.getFechaHoy());
		//Date fechaHoy=funciones.ponerFechaDate(funciones.ponerFechaSola((globalSingleton.getFechaHoy())));
		sql.append("SELECT  ccb.id_banco, cb.desc_banco, ccb.no_empresa, coalesce(ccb.saldo_inicial,0) as saldo_inicial,\n");
		sql.append("coalesce(ccb.cargo,0) as cargo,e.nom_empresa,case when ccb.id_estatus_cta = 'X' then '*' else '' end as estatus, \n");
		sql.append("ccb.id_chequera, coalesce(ccb.abono,0) as abono, coalesce((ccb.saldo_inicial-ccb.cargo+ccb.abono),0) as saldo_final, coalesce(ccb.abono_sbc,0) as abono_sbc, coalesce(ccb.ult_cheq_impreso,0) as ult_cheq_impreso, ccb.id_divisa,  \n");
		sql.append("coalesce((SELECT coalesce(sum(m.importe),0) from movimiento m \n");
		sql.append("Where m.no_empresa = ccb.no_empresa \n");
		sql.append("and m.id_banco=ccb.id_banco \n");
		sql.append("and m.id_chequera = ccb.id_chequera \n");
		sql.append("and id_tipo_movto = 'E' \n");
		sql.append("and ((m.b_entregado = 'N' \n");
		sql.append("and id_estatus_mov in ('I','R')) or (id_estatus_mov in('T','P','J')))),0) as cheq_no_ent,\n");
		sql.append("coalesce((SELECT coalesce(sum(m.importe),0)   from movimiento m  \n");
		sql.append("Where m.no_empresa = ccb.no_empresa \n");
		sql.append("and m.id_banco=ccb.id_banco \n");
		sql.append("and m.id_chequera = ccb.id_chequera \n");
		sql.append("and id_tipo_movto = 'E' \n");
		sql.append("and ((m.b_entregado = 'N' \n");
		sql.append("and id_estatus_mov in ('I','R')) \n");
		sql.append("or (id_estatus_mov in('T','P','J')))) + ccb.saldo_final,0) as disp_en_chequera, \n");     
		sql.append("ccb.fec_conciliacion , ccb.periodo_conciliacion, \n");
		sql.append("ctc.desc_chequera as desc_tipo_chequera,convert(date,ccb.fecha_banca,103) as fecha_banca, (select fec_hoy from fechas)as fechaHoy \n");
		
		sql.append(",(select coalesce(saldo_banco_ini,0) from fechas f,hist_cat_cta_banco hccb \n");
		sql.append("where hccb.id_chequera like ('"+funciones.validarCadena(parametros.get("idChequera").toString())+"') AND hccb.no_empresa in("+funciones.validarCadena(parametros.get("empresas").toString())+") \n");
		sql.append("AND hccb.id_banco between "+funciones.convertirCadenaInteger(parametros.get("idBancoInf").toString())+" and " + funciones.convertirCadenaInteger(parametros.get("idBancoSup").toString())+" \n"); 
		sql.append("AND saldo_banco_ini!=0 \n");
		sql.append("AND hccb.fec_valor<(select max(fec_ayer) as 'aye' from fechas) group by hccb.saldo_banco_ini) as saldo_inicial_banco, \n");
		sql.append("coalesce(ccb.saldo_banco_ini,0)as saldo_final_banco \n");
		
		
		sql.append("From  cat_cta_banco ccb,cat_banco cb, empresa e, cat_tipo_chequera ctc \n");
		sql.append("where ctc.tipo_chequera = ccb.tipo_chequera and ccb.id_banco = cb.id_banco and ccb.no_empresa = e.no_empresa \n");
		System.out.println(parametros.get("tipoChequera").toString());
		if(parametros.get("tipoChequera").toString().equals("C")){
			sql.append("and ccb.tipo_chequera = 'C' \n");
		}else if(parametros.get("tipoChequera").toString().equals("M")){
			sql.append("and ccb.tipo_chequera = 'M' \n");
		}else if(parametros.get("tipoChequera").toString().equals("P")){
			sql.append("and ccb.tipo_chequera = 'P' \n");
		}else if(parametros.get("tipoChequera").toString().equals("I")){
			sql.append("and ccb.tipo_chequera = 'I' \n");
		}
		sql.append("and ccb.tipo_chequera <>'U' \n");
		if(parametros.get("empresas").equals("")){
			sql.append("and ccb.no_empresa in(SELECT u.no_empresa FROM usuario_empresa u WHERE u.no_usuario = " + iUsuario + " )\n");
			//sql.append("and ccb.no_empresa in(SELECT u.no_empresa FROM usuario_empresa u WHERE u.no_usuario = " + iUsuario + " and u.no_empresa in (32, 9,1208) )\n");
		}else{
			sql.append("and ccb.no_empresa in("+funciones.validarCadena(parametros.get("empresas").toString())+")\n");
		}
		sql.append("and ccb.id_banco between " + funciones.convertirCadenaInteger(parametros.get("idBancoInf").toString()) + " and  " + funciones.convertirCadenaInteger(parametros.get("idBancoSup").toString())+" \n");
		sql.append("and ccb.id_chequera like ('" + funciones.validarCadena(parametros.get("idChequera").toString()) + "') \n");
		sql.append("order by e.no_empresa,nom_empresa,id_divisa \n");
		System.out.println( "reporte saldos chequerasS "+sql.toString());
		resultado = (List<Map<String, Object>>)jdbcTemplate.query(sql.toString(), new RowMapper(){
			public Object mapRow(ResultSet rs, int i)
				throws SQLException {
				Map<String, Object> results = new HashMap<String, Object>();
	            results.put("id_banco", funciones.validarCadena(rs.getString("id_banco")));
	            results.put("desc_banco", funciones.validarCadena(rs.getString("desc_banco")));
	            results.put("no_empresa", funciones.validarCadena(rs.getString("no_empresa")));
	            results.put("saldo_inicial", funciones.validarCadena(rs.getString("saldo_inicial")));
	            results.put("cargo", funciones.validarCadena(rs.getString("cargo")));
	            results.put("nom_empresa", funciones.validarCadena(rs.getString("nom_empresa")));
	            results.put("estatus", funciones.validarCadena(rs.getString("estatus")));
	            results.put("fechaHoy", funciones.validarCadena(rs.getString("fechaHoy")));
	            results.put("id_chequera", funciones.validarCadena(rs.getString("id_chequera")));
	            results.put("abono", funciones.validarCadena(rs.getString("abono")));
	            results.put("saldo_final", funciones.validarCadena(rs.getString("saldo_final")));
	            
	            results.put("saldo_inicial_banco", funciones.validarCadena(rs.getString("saldo_inicial_banco")));
	            results.put("saldo_final_banco", funciones.validarCadena(rs.getString("saldo_final_banco")));
	            
	            results.put("abono_sbc", funciones.validarCadena(rs.getString("abono_sbc")));
	            results.put("ult_cheq_impreso", funciones.validarCadena(rs.getString("ult_cheq_impreso")));
	            results.put("id_divisa", funciones.validarCadena(rs.getString("id_divisa")));
	            results.put("cheq_no_ent", funciones.validarCadena(rs.getString("cheq_no_ent")));
	            results.put("desc_tipo_chequera", funciones.validarCadena(rs.getString("desc_tipo_chequera")));
	            results.put("disp_en_chequera",funciones.validarCadena(rs.getString("disp_en_chequera")));
	            results.put("fecha_banca",funciones.validarCadena(rs.getString("fecha_banca")));
	            return results;
			}
		});
		return resultado;
	}

	public List<Map<String, Object>> reporteChequeraDetalleMov(ReportesChequeraDto datos){
		StringBuffer sbSql = new StringBuffer();
		
		sbSql.append("\nSELECT  ");
		sbSql.append("\n convert(date,m.fec_valor, 103) as fec_valor, desc_estatus as id_estatus_mov,  ");
		sbSql.append("\ncase when id_tipo_movto='E' then  ");
		sbSql.append("\n  case when id_estatus_mov = 'X'  ");
		sbSql.append("\n      and not id_tipo_operacion between 3700 and 3799 then ");
		sbSql.append("\n    -1 * importe  ");
		sbSql.append("\n  else  ");
		sbSql.append("\n    importe  ");
		sbSql.append("\n  end  ");
		sbSql.append("\nelse  ");
		sbSql.append("\n  0.0  ");
		sbSql.append("\nend as CARGO, ");
		sbSql.append("\ncase when id_tipo_movto='I' then  ");
		sbSql.append("\n  case when id_estatus_mov = 'X'  ");
		sbSql.append("\n      and not id_tipo_operacion between 3700 and 3799 then  ");
		sbSql.append("\n        -1 * importe  ");
		sbSql.append("\n      else  ");
		sbSql.append("\n        importe  ");
		sbSql.append("\n      end  ");
		sbSql.append("\n    else 0.0  ");
		sbSql.append("\nend as ABONO,  ");
		sbSql.append("\nsaldo_inicial, saldo_final, m.no_folio_det, m.concepto, m.observacion  ");
		sbSql.append("\nfrom hist_movimiento m, hist_cat_cta_banco b, cat_estatus e    ");
		sbSql.append("\nwhere m.no_empresa= "+ datos.getNoEmpresa() +" ");
		sbSql.append("\n    and m.id_chequera= '"+ datos.getIdChequera() +"' ");
		sbSql.append("\n    and m.id_banco= "+ datos.getIdBanco() +" ");
		sbSql.append("\n    and id_tipo_movto in ('I','E')  ");
		sbSql.append("\n    and m.fec_valor between convert(date,'"+ datos.getsFechaDesde() +"', 103) and convert(date,'"+ datos.getsFechaHasta() +"', 103) ");
		sbSql.append("\n    AND ((id_tipo_operacion between 3700 and 3799 ) or (id_tipo_operacion in (3200, 3500, 1016, 1017, 1018, 1023,4001,4102,4103,4104)) ");
		sbSql.append("\n        or (id_tipo_operacion between 3100 and 3199))  ");
		sbSql.append("\n    and m.id_banco = b.id_banco and m.id_chequera = b.id_chequera  ");
		sbSql.append("\n    and m.fec_valor = b.fec_valor  ");
		sbSql.append("\n    and not (m.id_forma_pago = 1 and id_tipo_movto = 'I')  ");
		sbSql.append("\nUNION  ");
		sbSql.append("\nSELECT  ");
		sbSql.append("\n convert(date,m.fec_valor, 103) as fec_valor, desc_estatus as  id_estatus_mov,  ");
		sbSql.append("\ncase when id_tipo_movto='E' then  ");
		sbSql.append("\n  case when id_estatus_mov = 'X'  ");
		sbSql.append("\n      and not id_tipo_operacion between 3700 and 3799 then  ");
		sbSql.append("\n    -1 * importe else importe  ");
		sbSql.append("\n  end else  ");
		sbSql.append("\n    0.0  ");
		sbSql.append("\nend as cargo, ");
		sbSql.append("\ncase when id_tipo_movto='I' then  ");
		sbSql.append("\n  case when id_estatus_mov = 'X'  ");
		sbSql.append("\n      and not id_tipo_operacion between 3700 and 3799 then  ");
		sbSql.append("\n    -1 * importe  ");
		sbSql.append("\n  else  ");
		sbSql.append("\n    importe  ");
		sbSql.append("\n  end  ");
		sbSql.append("\nelse  ");
		sbSql.append("\n  0.0  ");
		sbSql.append("\nend as abono,  ");
		sbSql.append("\nsaldo_inicial, saldo_final, m.no_folio_det, m.concepto, m.observacion  ");
		sbSql.append("\nfrom movimiento m, hist_cat_cta_banco b, cat_estatus e  ");
		sbSql.append("\nwhere m.no_empresa= "+ datos.getNoEmpresa() +" ");
		sbSql.append("\n    and m.id_chequera= '"+ datos.getIdChequera() +"' ");
		sbSql.append("\n    and m.id_banco= "+ datos.getIdBanco() +" ");
		sbSql.append("\n    and id_tipo_movto in ('I','E')  ");
		sbSql.append("\n    and m.fec_valor between convert(date,'"+ datos.getsFechaDesde() +"', 103) and convert(date,'"+ datos.getsFechaHasta() +"', 103) ");
		sbSql.append("\n    AND ((id_tipo_operacion between 3700 and 3799)  ");
		sbSql.append("\n        or (id_tipo_operacion in (3200, 3500, 1016, 1017, 1018, 1023,4001,4102,4103,4104))  ");
		sbSql.append("\n        or (id_tipo_operacion between 3100 and 3199))  ");
		sbSql.append("\n    and m.id_banco = b.id_banco and m.id_chequera = b.id_chequera  ");
		sbSql.append("\n    and m.fec_valor = b.fec_valor  ");
		sbSql.append("\n    and m.id_estatus_mov = e.id_estatus and e.clasificacion = 'MOV' and m.id_estatus_mov not in ('H', 'N','X','Q') ");
		sbSql.append("\n    and not (m.id_forma_pago = 1 and id_tipo_movto = 'I')  ");
		sbSql.append("\nUNION  ");
		sbSql.append("\nSELECT  ");
		sbSql.append("\n convert(date,m.fec_modif, 103) as fec_valor, desc_estatus as id_estatus_mov,  ");
		sbSql.append("\ncase when id_tipo_movto='E' then case when id_estatus_mov = 'X' then -1 * importe else importe end else 0.0 end as cargo, ");
		sbSql.append("\ncase when id_tipo_movto='I' then case when id_estatus_mov = 'X' then -1 * importe else importe end else 0.0 end as abono,  ");
		sbSql.append("\nsaldo_inicial, saldo_final, m.no_folio_det, m.concepto, m.observacion  ");
		sbSql.append("\nfrom hist_movimiento m, hist_cat_cta_banco b, cat_estatus e    ");
		sbSql.append("\nwhere m.no_empresa= "+ datos.getNoEmpresa() +" ");
		sbSql.append("\n    and m.id_chequera= '"+ datos.getIdChequera() +"' ");
		sbSql.append("\n    and m.id_banco= "+ datos.getIdBanco() +" ");
		sbSql.append("\n    and id_tipo_movto in ('I', 'E')  ");
		sbSql.append("\n    and m.fec_modif between convert(date,'"+ datos.getsFechaDesde() +"', 103) and convert(date,'"+ datos.getsFechaHasta() +"', 103) ");
		sbSql.append("\n    AND (id_tipo_operacion between 3100 and 3199 or id_tipo_operacion between 3700 and 3799) ");
		sbSql.append("\n    and m.id_banco = b.id_banco and m.id_chequera = b.id_chequera  ");
		sbSql.append("\n    and m.fec_modif = b.fec_valor  ");
		sbSql.append("\n    and m.id_estatus_mov = e.id_estatus and e.clasificacion = 'MOV'  ");
		sbSql.append("\n    and ((m.id_forma_pago = 1 and b_salvo_buen_cobro = 'S' and id_tipo_movto = 'I' and m.id_estatus_mov in ('A')) ");
		sbSql.append("\n    or (id_tipo_operacion between 3700 and 3799 and id_estatus_mov = 'X')) ");
		sbSql.append("\nUNION  ");
		sbSql.append("\nSELECT  ");
		sbSql.append("\n convert(date,m.fec_modif, 103) as fec_valor, desc_estatus as id_estatus_mov,  ");
		sbSql.append("\ncase when id_tipo_movto='E' then case when id_estatus_mov = 'X' then -1 * importe else importe end else 0.0 end as cargo, ");
		sbSql.append("\ncase when id_tipo_movto='I' then case when id_estatus_mov = 'X' then -1 * importe else importe end else 0.0 end as abono,  ");
		sbSql.append("\nsaldo_inicial, saldo_final, m.no_folio_det, m.concepto, m.observacion  ");
		sbSql.append("\nfrom movimiento m, hist_cat_cta_banco b, cat_estatus e    ");
		sbSql.append("\nwhere m.no_empresa= "+ datos.getNoEmpresa() +" ");
		sbSql.append("\n    and m.id_chequera= '"+ datos.getIdChequera() +"' ");
		sbSql.append("\n    and m.id_banco= "+ datos.getIdBanco() +" ");
		sbSql.append("\n    and id_tipo_movto in ('I', 'E')  ");
		sbSql.append("\n    and m.fec_modif between convert(date,'"+ datos.getsFechaDesde() +"', 103) and convert(date,'"+ datos.getsFechaHasta() +"', 103) ");
		sbSql.append("\n    AND (id_tipo_operacion between 3100 and 3199 or id_tipo_operacion between 3700 and 3799)  ");
		sbSql.append("\n    and m.id_banco = b.id_banco and m.id_chequera = b.id_chequera  ");
		sbSql.append("\n    and m.fec_modif = b.fec_valor  ");
		sbSql.append("\n    and m.id_estatus_mov = e.id_estatus and e.clasificacion = 'MOV'  ");
		sbSql.append("\n    and ((m.id_forma_pago = 1 and b_salvo_buen_cobro = 'S' and id_tipo_movto = 'I' and m.id_estatus_mov in ('A')) ");
		sbSql.append("\n    or (id_tipo_operacion between 3700 and 3799 and id_estatus_mov = 'X')) ");
		sbSql.append("\n    order by 1, 7 ");
		
		System.out.println("El query es "+ sbSql.toString());
		
		return this.jdbcTemplate.queryForList(sbSql.toString());
	}
	
	public List<Map<String, Object>> reporteChequeraMovimientos(ReportesChequeraDto datos){
		StringBuffer sbSql = new StringBuffer();
		
		sbSql.append("\n select NO_DOCTO, CONCEPTO, IMPORTE, BENEFICIARIO, NO_CHEQUE, ");
		sbSql.append("\n CASE WHEN ID_TIPO_MOVTO = 'I'  ");
		sbSql.append("\n THEN 'ABONO'  ELSE 'CARGO' END AS TIPO_MOVTO ");
		sbSql.append("\n from MOVIMIENTO ");
		sbSql.append("\n where FEC_VALOR = convert(date, '"+ datos.getsFechaDesde() +"', 103) ");
		sbSql.append("\n and ((id_estatus_mov in('P','R','I','T','K','J','G') ");
		sbSql.append("\n     or (id_estatus_mov='A' and id_tipo_operacion not between 3000 and 3099)) ");
		sbSql.append("\n   or (id_estatus_mov in ('X') and fec_modif = convert(date, '"+ datos.getsFechaDesde() +"', 103) ");
		sbSql.append("\n     and fec_valor < convert(date, '"+ datos.getsFechaDesde() +"', 103) ");
		sbSql.append("\n   ) ");
		sbSql.append("\n   or (id_estatus_mov in ('X') and fec_modif > convert(date, '"+ datos.getsFechaDesde() +"', 103) ");
		sbSql.append("\n     and fec_valor = convert(date,'"+ datos.getsFechaDesde() +"', 103)  ");
		sbSql.append("\n   ) ");
		sbSql.append("\n ) ");
		sbSql.append("\n and id_banco =  "+ datos.getIdBanco());
		sbSql.append("\n and id_chequera = '"+ datos.getIdChequera() +"'  ");
		sbSql.append("\n and origen_mov<>'MIG' ");
		sbSql.append("\n UNION ");
		sbSql.append("\n select NO_DOCTO, CONCEPTO, IMPORTE, BENEFICIARIO, NO_CHEQUE, ");
		sbSql.append("\n CASE WHEN ID_TIPO_MOVTO = 'I'  ");
		sbSql.append("\n THEN 'ABONO'  ELSE 'CARGO' END AS TIPO_MOVTO ");
		sbSql.append("\n from HIST_MOVIMIENTO ");
		sbSql.append("\n where FEC_VALOR = convert(date, '"+ datos.getsFechaDesde() +"', 103) ");
		sbSql.append("\n and ((id_estatus_mov in('P','R','I','T','K','J','G') ");
		sbSql.append("\n     or (id_estatus_mov='A' and id_tipo_operacion not between 3000 and 3099)) ");
		sbSql.append("\n   or (id_estatus_mov in ('X') and fec_modif = convert(date, '"+ datos.getsFechaDesde() +"', 103) ");
		sbSql.append("\n     and fec_valor < convert(date, '"+ datos.getsFechaDesde() +"', 103) ");
		sbSql.append("\n   ) ");
		sbSql.append("\n   or (id_estatus_mov in ('X') and fec_modif > convert(date, '"+ datos.getsFechaDesde() +"', 103) ");
		sbSql.append("\n     and fec_valor = convert(date, '"+ datos.getsFechaDesde() +"', 103)  ");
		sbSql.append("\n   ) ");
		sbSql.append("\n ) ");
		sbSql.append("\n and id_banco = "+ datos.getIdBanco());
		sbSql.append("\n and id_chequera = '"+ datos.getIdChequera() +"'  ");
		sbSql.append("\n and origen_mov<>'MIG' ");
		sbSql.append("\n order by TIPO_MOVTO ");
		
		System.out.println("El query es "+ sbSql.toString());
		
		return this.jdbcTemplate.queryForList(sbSql.toString());
	}
	
	public Double consultarSaldoFinal(ReportesChequeraDto datos){
		StringBuffer sbSql = new StringBuffer();

		sbSql.append("\n select saldo_inicial ");
		sbSql.append("\n from hist_cat_cta_banco  ");
		sbSql.append("\n WHERE no_empresa= "+ datos.getNoEmpresa());
		sbSql.append("\n AND id_chequera= '"+ datos.getIdChequera() +"'  ");
		sbSql.append("\n and id_banco= "+ datos.getIdBanco());
		sbSql.append("\n and fec_valor = convert(date, '"+ datos.getsFechaDesde() +"', 103) ");
		
		System.out.println("El query es "+ sbSql.toString());
		
		try{
			return this.jdbcTemplate.queryForObject(sbSql.toString(), Double.class);
		}catch(Exception e){
			return 0D;
		}
	}
	
	public String update3200(RevividorDto revividorDto){
		StringBuffer sb = new StringBuffer();
		String resultado = "Error al actualizar el estatus";
		try {
			sb.append("UPDATE DET_ARCH_TRANSFER_AGRUP \n");
			sb.append("SET ID_ESTATUS_ARCH = 'X' \n");
			sb.append("where NO_EMPRESA=" + revividorDto.getNoEmpresa() + "\n");
			sb.append(" and  ID_BANCO="+ revividorDto.getIdBanco() + "\n");
			sb.append(" and ID_CHEQUERA= '"+ revividorDto.getIdChequera() + "'\n");
			sb.append(" and PO_HEADERS = '"+ revividorDto.getPoHeaders() + "'\n");
			System.out.println("update 3200 "+sb.toString());
			
			int res= jdbcTemplate.update(sb.toString());
			if(res!=0)
				resultado= "Registro procesado con exito";
		} catch (Exception e) {
			
		}return resultado;
	}
	
	/**
	 * Set para el jdbc
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	public double getTotalMovtos(int ntEmpresa, int ntBanco, String strChequera, String strFecha, String string) {

		return 0;
		
	}
	public String obtenerNoCliente(String idProveedor) {
		StringBuffer sql = new StringBuffer();
		List<String> noCliente = new ArrayList<String>();
			 sql.append("Select no_persona from persona where equivale_persona='"+idProveedor+"' \n");
				noCliente = jdbcTemplate.query(sql.toString(), new RowMapper<String>() {
					public String mapRow(ResultSet rs, int idx) throws SQLException{
					return rs.getString("no_persona");
				}});

		return noCliente.get(0);
	}
	
	public String obtenerNoClienteInterempresas(String noEmpresa) {
		StringBuffer sql = new StringBuffer();
		List<String> empresa = new ArrayList<String>();
		if(noEmpresa!=null && !noEmpresa.equals("0")){
			  sql.append("Select SETEMP from set006 where SOIEMP='"+noEmpresa+"' \n");
				 empresa = jdbcTemplate.query(sql.toString(), new RowMapper<String>() {
						public String mapRow(ResultSet rs, int idx) throws SQLException{
						return rs.getString("SETEMP");
					}});

			}
		return empresa.get(0);
	}
	
	
	public List<SaldoChequeraDto> obtenerSaldoInicialBanco(DatosChequeraDto dto){
		sb = new StringBuffer();
		
		List<SaldoChequeraDto> saldoChequeraDto = null;  
		
		try{
			sb.append(" SELECT saldo_banco_ini ");
			sb.append("\n FROM hist_cat_cta_banco ");
			sb.append("\n WHERE id_chequera= '" + Utilerias.validarCadenaSQL(dto.getIdChequera()) + "'");
			sb.append("\n 	AND no_empresa =  " + dto.getNoEmpresa());
			sb.append("\n 	AND id_banco = " + dto.getIdBanco());
			sb.append("\n 	AND fec_valor=(select max(fec_valor) from hist_cat_cta_banco");
			sb.append("\n WHERE id_chequera= '" + Utilerias.validarCadenaSQL(dto.getIdChequera()) + "'");
			sb.append("\n 	AND no_empresa =  " + dto.getNoEmpresa());
			sb.append("\n 	AND id_banco = " + dto.getIdBanco());
			sb.append("\n 	AND fec_valor<(select max(fec_hoy) from fechas))");
			System.out.println("saldo inicialBanco: "+sb);
			saldoChequeraDto = jdbcTemplate.query(sb.toString(), new RowMapper<SaldoChequeraDto>(){
		    	public SaldoChequeraDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		SaldoChequeraDto dato = new SaldoChequeraDto();
						dato.setSaldoIniBanco(rs.getDouble("saldo_banco_ini"));	
						return dato;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ " P:modConsulta, C:ModConsultasDao, M:seleccionarDatosChequera");
		}
		return saldoChequeraDto;
	}
	@SuppressWarnings("unchecked")
	public String leerRutaConfSet(int res) {
		String resultado="";
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto> listListas = new ArrayList<LlenaComboGralDto>();
		
		try {
			sb.append("select valor from configura_set where indice="+res+" ");
 
			System.out.println("leerRutaConfSet: " + sb);
			logger.info("leerRutaConfSet: " + sb);

			listListas = jdbcTemplate.query(sb.toString(), new RowMapper(){
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
					dtoCons.setDescripcion(rs.getString("valor"));

					return dtoCons;
				}
			});
		}
		catch (Exception e) {
			logger.error(e);
			resultado= "Error al validar la ruta ";
		}
		
		for (LlenaComboGralDto l : listListas) {
			resultado=l.getDescripcion();
		}
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public String leerRutaComentario2(int folio) {
		String resultado="";
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto> listListas = new ArrayList<LlenaComboGralDto>();
		
		try {
			sb.append("select COMENTARIO2 from movimiento where no_folio_det="+folio+" ");
 
			System.out.println("leerRutaComentario2: " + sb);
			logger.info("leerRutaComentario2: "+ sb);
			
			listListas = jdbcTemplate.query(sb.toString(), new RowMapper(){
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
					dtoCons.setDescripcion(rs.getString("COMENTARIO2"));
					return dtoCons;
				}
			});
			
			if(listListas!=null && listListas.size()>0){
				for (LlenaComboGralDto l : listListas) {
					resultado=l.getDescripcion();
				}
			}
			else{ // Se busca en historico
				sb = new StringBuffer();
				sb.append("select COMENTARIO2 from hist_movimiento where no_folio_det="+folio+" ");
				
				System.out.println("leerRutaComentario2: " + sb);
				logger.info("leerRutaComentario2: "+ sb);
				
				listListas = jdbcTemplate.query(sb.toString(), new RowMapper(){
					@Override
					public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
					{
						LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						dtoCons.setDescripcion(rs.getString("COMENTARIO2"));
						return dtoCons;
					}
				});
				if(listListas!=null && listListas.size()>0){
					for (LlenaComboGralDto l : listListas) {
						resultado=l.getDescripcion();
					}
				}
			}
		}
		catch (Exception e) {
			logger.error(e);
			resultado= "";
		}
		return resultado;
	}

	public int verDetalleArch(int noFolioDet) {
		StringBuffer sbSql = new StringBuffer();
		int iRegCount = 0;
		
		try{
			sbSql.append("  select COUNT(no_folio_det) from det_arch_transfer where no_folio_det="+noFolioDet+"  and id_estatus_arch='T'  "); 
			System.out.println("ver detalle archivo "+sbSql.toString());
			iRegCount = jdbcTemplate.queryForInt(sbSql.toString());
		}
		catch(Exception e){
			logger.error(e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:ConsultasDao, M:buscarRegDetArchTransfer");
		}
		return iRegCount;
	}
}