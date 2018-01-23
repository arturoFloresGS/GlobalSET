package com.webset.set.egresos.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.egresos.dao.ConfirmacionTransferenciasDao;
import com.webset.set.egresos.dto.ConfirmacionTransferenciasDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.ConstantesSet;

public class ConfirmacionTransferenciasDaoImpl implements ConfirmacionTransferenciasDao {
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	Funciones funciones = new Funciones();
	ConsultasGenerales consultasGenerales;
	String gsDBM = ConstantesSet.gsDBM;
	
	public List<LlenaComboEmpresasDto> obtenerEmpresas(int idUsuario) {
		consultasGenerales = new ConsultasGenerales (jdbcTemplate);
		return consultasGenerales.llenarComboEmpresas(idUsuario);
	}
	
	 
	public List<LlenaComboGralDto> llenaComboBanco(int noEmpresa) {
		StringBuffer sql=new StringBuffer();
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		
		try{
		    sql.append(" SELECT id_banco as ID, desc_banco as DESCRIPCION ");
		    sql.append(" \n	FROM cat_banco ");
		    sql.append(" \n	WHERE id_banco in (SELECT distinct id_banco FROM cat_cta_banco ");
		    sql.append(" \n					   WHERE tipo_chequera not in (SELECT valor FROM configura_set WHERE indice = 202) ");
		    
		    if(noEmpresa == 0) {
		    	sql.append(" \n	) ");
		    }else{
		    	sql.append(" \n		AND no_empresa = " + noEmpresa + ") ");
		    }
			
			System.out.println("llenaComboBanco: " + sql.toString());
			
		    list = jdbcTemplate.query(sql.toString().toString(), new RowMapper<LlenaComboGralDto>(){
		    	public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}
		    });
		    
		    insertarBitacora("lleno combo Bancos");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:ConfirmacionTransferenciasDaoImpl, M:LlenaComboGralDto ");
		}
		return list;
	}
	
	
	private void insertarBitacora(String string) {
		StringBuffer sql=new StringBuffer();
		try {
			sql.append("Insert into BITACORA_PROCESOS values('Confirmaci�n', GETDATE(), 666, '" + string + "')");
			jdbcTemplate.execute(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
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
		    
		    if(!idDivisa.equals(new StringBuffer()))
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
		    insertarBitacora("lleno combo Chequera");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:ConfirmacionTransferenciasDaoImpl, M:LlenaComboChequeraDto ");
		}
		return list;
	}
	
	
	public List<MovimientoDto> consultarMovimiento(int noEmpresa, int idBanco, String idChequera, int hayBanca, int idUsuario, String idDivisa) {
		StringBuffer sql = new StringBuffer();
		List<MovimientoDto> list = new ArrayList<MovimientoDto>();
		
		try{
		    sql.append("  SELECT m.id_banco, m.id_chequera, m.id_banco_benef, m.no_docto, m.lote_entrada, m.id_chequera_benef, ");
		    sql.append(" \n	   m.referencia, m.id_estatus_mov, m.origen_mov, m.id_forma_pago, m.no_cuenta, m.id_divisa, ");
		    sql.append(" \n	   m.importe AS importe1, m.beneficiario, m.fec_valor, m.id_cve_operacion, m.no_folio_det, m.folio_ref, ");
		    sql.append(" \n	   m.agrupa1, m.agrupa2, m.id_rubro, m.agrupa3, cb.desc_banco, c.desc_banco as banco_benef, ");
		    sql.append(" \n	   cfp.desc_forma_pago, e.desc_estatus, m.no_empresa, em.nom_empresa ");
		    
		    if(gsDBM != null && (gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE") || gsDBM.equals("ORACLE"))) {
		    	sql.append(" \n	FROM movimiento m LEFT JOIN cat_estatus e ON(m.id_estatus_mov = e.id_estatus AND e.clasificacion = 'MOV'), cat_banco cb, cat_forma_pago cfp, cat_banco c,  empresa em ");
		    }else{
		    	if(gsDBM != null && (gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2"))) {
		    		sql.append(" \n	FROM movimiento m LEFT JOIN cat_estatus e ON(m.id_estatus_mov = e.id_estatus AND e.clasificacion = 'MOV'), ");
		    		sql.append(" \n		 cat_banco cb, cat_forma_pago cfp, cat_banco c ");	
		    	}
		    }
		    sql.append(" \n	WHERE  m.id_banco = cb.id_banco ");
		    sql.append(" \n		AND m.id_banco_benef = c.id_banco ");
		    sql.append(" \n		AND m.id_forma_pago = cfp.id_forma_pago ");
		    sql.append(" \n		AND ((m.id_estatus_mov = 'P' ");
		    sql.append(" \n			AND cb.b_banca_elect not in ('E','A')) ");
		    sql.append(" \n			OR m.id_estatus_mov = 'T') ");
		    sql.append(" \n		AND m.id_forma_pago = 3 ");
	        
		   /* if(gsDBM != null && (gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE") || gsDBM.equals("ORACLE"))) {
		    	if (gsDBM.equals("ORACLE"))
		    		sql.append(" \n	AND m.id_estatus_mov = e.id_estatus(+) ");
		    	else
		    		sql.append(" \n	AND m.id_estatus_mov *= e.id_estatus ");*/
	            
		    	//sql.append(" \n	AND e.clasificacion = 'MOV' ");
		    //}
		    if(noEmpresa != 0)
		    	sql.append(" \n	AND m.no_empresa = " + noEmpresa + new StringBuffer());
		    
		    if(idBanco != 0)
		    	sql.append(" \n	AND m.id_banco = " + idBanco + new StringBuffer());
		    
		    if(!idChequera.equals(""))
		    	sql.append(" \n	AND m.id_chequera = '" + idChequera + "' ");
		    
		    if(idUsuario !=0 )
		    	sql.append(" \n	AND m.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " + idUsuario + ") ");
		    
		    if(hayBanca == 0)
		    	sql.append(" \n	AND cb.b_banca_elect in ('E','A') ");
		    else
		    	sql.append(" \n	AND cb.b_banca_elect not in ('E','A') ");
		    
		    sql.append(" \n	AND m.no_empresa = em.no_empresa ");
		    sql.append(" \n	AND m.id_divisa = '" + idDivisa + "' ");
		    //Se comenta esta parte por que al parecer el generador o el pagador no estan arrastrando el area que se le asigna al pago, en caabsa no aplica adem�s 
		    /*
		    sql.append(" \n	AND (((select count(*) from usuario_area WHERE no_usuario = " + idUsuario + ") = 0) or  ");
		    sql.append(" \n		 (id_area in (select id_area from usuario_area where no_usuario = " + idUsuario + "))) ");
		    */
		    
		    sql.append(" \n	ORDER BY m.importe ");
			
			System.out.println("consultarMovimiento: " + sql.toString());
		    
		    list = jdbcTemplate.query(sql.toString(), new RowMapper<MovimientoDto>() {
		    	public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
		    		MovimientoDto cons = new MovimientoDto();
		    		
		    		cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setFecValor(rs.getDate("fec_valor"));
					cons.setBancoPago(rs.getString("desc_banco"));
					cons.setIdChequera(rs.getString("id_chequera"));
					cons.setImporte(rs.getDouble("importe1"));
					cons.setDescBancoBenef(rs.getString("banco_benef"));
					cons.setIdChequeraBenef(rs.getString("id_chequera_benef"));
					cons.setIdDivisa(rs.getString("id_divisa"));
					cons.setDescFormaPago(rs.getString("desc_forma_pago"));
					cons.setBeneficiario(rs.getString("beneficiario"));
					cons.setReferencia(rs.getString("referencia"));
					cons.setIdBanco(rs.getInt("id_banco"));
					cons.setIdBancoBenef(rs.getInt("id_banco_benef"));
					cons.setIdFormaPago(rs.getInt("id_forma_pago"));
					cons.setIdCveOperacion(rs.getInt("id_cve_operacion"));
					//cons.setTipoCambio(rs.getDouble("tipoCambio"));
					cons.setIdEstatusMov(rs.getString("id_estatus_mov"));
					cons.setDescEstatus(rs.getString("desc_estatus"));
					cons.setNoDocto(rs.getString("no_docto"));
					cons.setLoteEntrada(rs.getInt("lote_entrada"));
					cons.setFolioRef(rs.getInt("folio_ref"));
					cons.setNoCuenta(rs.getInt("no_cuenta"));
					cons.setOrigenMov(rs.getString("origen_mov"));
					cons.setAgrupa1(rs.getString("agrupa1"));
					cons.setAgrupa2(rs.getString("agrupa2"));
					cons.setIdRubro(rs.getDouble("id_rubro"));
					cons.setAgrupa3(rs.getString("agrupa3"));
					cons.setNoFolioDet(rs.getInt("no_folio_det"));
					cons.setNomEmpresa(rs.getString("nom_empresa"));
					return cons;
				}
		    });

		    insertarBitacora("consulta Movimientos");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:ConfirmacionTransferenciasDaoImpl, M:consultarMovimiento ");
		}
		return list;
	}
	
	
	public List<ConfirmacionTransferenciasDto> fechaAyerHoraActual() {
		StringBuffer sql = new StringBuffer();
		List<ConfirmacionTransferenciasDto> list = new ArrayList<ConfirmacionTransferenciasDto>();
		
		try{
			if(gsDBM != null) {
		    	if(gsDBM.equals("ORACLE"))
		    		sql.append(" SELECT datepart(hh,sysdate()) as hora, fec_ayer FROM fechas ");
		    	if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE"))
		    		sql.append(" SELECT datepart(hh,getdate()) as hora, fec_ayer FROM fechas ");
		    	if(gsDBM.equals("POSTGRESQL"))
		    		sql.append(" SELECT to_char(timestamp 'now','HH24') as hora, fec_ayer FROM fechas ");
		    	if(gsDBM.equals("DB2"))
		    		sql.append(" SELECT hour(CURRENT TIME) as hora, fec_ayer FROM fechas ");
		    }
			
			System.out.println("fechaAyerHoraActual: " + sql.toString());
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper<ConfirmacionTransferenciasDto>() {
				public ConfirmacionTransferenciasDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionTransferenciasDto cons = new ConfirmacionTransferenciasDto();
		    		
		    		cons.setFecAyer(rs.getDate("fec_ayer"));
		    		cons.setHoraActual(rs.getString("hora"));
					return cons;
				}
		    });

		    insertarBitacora("Selecciona fecha");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:ConfirmacionTransferenciasDaoImpl, M:fechaAyerHoraActual ");
		}
		return list;
	}
	
	 
	public List<ConfirmacionTransferenciasDto> buscaCargoExiste(Date pdFecha, int piEmpresa, int piBanco, String psChequera, Double pdimporte, String bandera) {
		StringBuffer sql = new StringBuffer();
		List<ConfirmacionTransferenciasDto> list = new ArrayList<ConfirmacionTransferenciasDto>();
		
		try{
			sql.append(" SELECT secuencia, id_estatus_cancela, (select count(*) from movimiento m1 where m1.id_tipo_operacion = 3200 ");
			sql.append(" \n		and m1.id_forma_pago in (3,5) and m1.id_estatus_mov in ('T', 'K', 'P', 'X') and m1.importe = e.importe ");
			
			if(gsDBM != null) {
		    	if(gsDBM.equals("SQL SERVER"))
		    		sql.append(" \n		and m1.fec_valor = convert(char(10), e.fec_valor, 103) ");
		    	if(gsDBM.equals("DB2"))
		    		sql.append(" \n		and m1.fec_valor = cast(e.fec_valor as date) ");
		    }
			sql.append(" \n		and m1.id_banco = e.id_banco and m1.id_chequera = e.id_chequera) as registros_set, descripcion ");
			sql.append(" \n	FROM movto_banca_e e ");
			sql.append(" \n	WHERE "); //AND no_empresa = " & piEmpresa
			sql.append(" \n		cargo_abono = 'E' ");
			sql.append(" \n		AND id_banco = " + piBanco + new StringBuffer());
			sql.append(" \n		AND id_chequera = '" + psChequera + "' ");		
			sql.append(" \n		AND importe = " + pdimporte + new StringBuffer());
			sql.append(" \n		AND (folio_det_conf is NULL OR folio_det_conf = 0) ");
			
			if(bandera.equals("A")) {
				sql.append(" \n		AND fec_valor between convert(datetime, '" + funciones.ponerFecha(pdFecha) + "', 103) and convert(datetime, '" + funciones.ponerFecha(pdFecha) + "', 103) ");
			}else {
				if(gsDBM.equals("DB2"))
					sql.append(" \n		AND fec_valor between '" + funciones.ponerFecha(pdFecha) + "' and '" + funciones.ponerFecha(funciones.modificarFecha("d", 2, pdFecha)) + "' ");
				else
					sql.append(" \n		AND fec_valor between '" + funciones.ponerFecha(pdFecha) + "' and '" + funciones.ponerFecha(funciones.modificarFecha("d", 2, pdFecha)) + "' ");
			}
			
			System.out.println("buscaCargoExiste: " + sql.toString());
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper<ConfirmacionTransferenciasDto>() {
				public ConfirmacionTransferenciasDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionTransferenciasDto cons = new ConfirmacionTransferenciasDto();
		    		
		    		cons.setIdEstatusCancela(rs.getString("idEstatusCancela"));
		    		
		    		cons.setHoraActual(rs.getString("id_estatus_cancela"));
		    		cons.setRegistrosSet(rs.getInt("registros_set"));
		    		cons.setSecuencia(rs.getInt("secuencia"));
		    		cons.setDescripcion(rs.getString("descripcion"));
		    		return cons;
				}
		    });

		    insertarBitacora("busca cargo");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:ConfirmacionTransferenciasDaoImpl, M:fechaAyerHoraActual ");
		}
		return list;
	}	
	
	 
	public Map<String, Object> ejecutarConfirmador(int sqlFolio, String sqlFolioBanco, int sqlSecuencia, Date sqlFecValor, int result, String msg)
	{
		Map<String, Object> retorno = new HashMap<String, Object>();
		try {			
			retorno.put("result", 0);
			retorno.put("mensaje", "OK");
			
			Date sqlFecHoy = consultasGenerales.obtenerFechaHoy(); 
			
			Map<String, Object> actualizaMov =  actualizaMovimiento330(sqlFecHoy, sqlFolioBanco, sqlFecValor, sqlFolio);
			
			if ((Integer)actualizaMov.get("result") != 0){
				return actualizaMov;
			}

			actualizaMov =  actualizaMovimiento340(sqlFecHoy, sqlFolioBanco, sqlFecValor, sqlFolio);
			
			if ((Integer)actualizaMov.get("result") != 0){
				return actualizaMov;
			}
			
			if (sqlSecuencia != 0) {
				Map<String, Object> actualizaMovToBanca =  actualizaMovimiento350(sqlFolio, sqlSecuencia);
				
				if ((Integer)actualizaMovToBanca.get("result") != 0){
					return actualizaMovToBanca;
				}
			}
			
			return retorno;
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConfirmacionTransferenciasDao, M:confirmar");
			return retorno;
		}
	}

	private Map<String, Object> actualizaMovimiento350(int sqlFolio, int sqlSecuencia) {
		Map<String, Object> retorno = new HashMap<String, Object>();
		StringBuffer sql = new StringBuffer();
		try {
			sql.append(" UPDATE movto_banca_e \n");
			sql.append(" SET folio_det_conf = " + sqlFolio + ", \n");
			sql.append(" 	id_estatus_trasp = 'T' \n");
			sql.append(" WHERE secuencia = " + sqlSecuencia + "  \n");
			
			int result = jdbcTemplate.update(sql.toString());

			if (result > 0) retorno.put("result", 0);
		} catch (Exception e) {
			Map<String, Object> retornoInsertar =  new HashMap<String, Object>();
			retorno.put("mensaje", "Error al actualizar el movto_banca_e");
			retorno.put("result", 350);
			retornoInsertar = insertarBitacoraProcesos("sp_confirmador", 350, "Error al actualizar el movto_banca_e");
			if ((Integer)retornoInsertar.get("result") != 0) return retornoInsertar;
		}
		return retorno;
	}


	private Map<String, Object> actualizaMovimiento340(Date sqlFecHoy, String sqlFolioBanco, Date sqlFecValor,
			int sqlFolio) {
		Map<String, Object> retorno = new HashMap<String, Object>();
		StringBuffer sql = new StringBuffer();
		try {
			sql.append(" UPDATE movimiento \n");
			sql.append(" SET id_estatus_mov = 'K', \n");
			sql.append(" 	fec_conf_trans = convert(date, GETDATE(), 103), \n");
			sql.append(" 	referencia = '" + sqlFolioBanco + "', \n");
			sql.append(" 	fec_valor_original = convert(date, GETDATE(), 103) \n");
			sql.append(" WHERE no_folio_det = " + sqlFolio + " \n");
			
			System.out.println(sql.toString());
			int result = jdbcTemplate.update(sql.toString());

			if (result > 0) retorno.put("result", 0);
		} catch (Exception e) {
			Map<String, Object> retornoInsertar =  new HashMap<String, Object>();
			retorno.put("mensaje", "Error al actualizar el movimiento 'K'");
			retorno.put("result", 340);
			retornoInsertar = insertarBitacoraProcesos("sp_confirmador", 340, "Error al actualizar el movimiento 'K'");
			if ((Integer)retornoInsertar.get("result") != 0) return retornoInsertar;
		}
		return retorno;
	}


	private Map<String, Object> actualizaMovimiento330(Date sqlFecHoy, String sqlFolioBanco, Date sqlFecValor,
			int sqlFolio) {
		Map<String, Object> retorno = new HashMap<String, Object>();
		StringBuffer sql = new StringBuffer();
		try {
			sql.append(" UPDATE movimiento \n");
			sql.append(" SET fec_conf_trans = convert(date, GETDATE(), 103), \n");
			sql.append(" 	referencia = '" + sqlFolioBanco + "', \n");
			sql.append(" 	fec_valor_original = convert(date, GETDATE(), 103) \n");
			sql.append(" WHERE  id_estatus_mov = 'H' \n");
			sql.append(" AND id_tipo_operacion = 3201 \n");
			sql.append(" AND no_folio_mov IN (SELECT no_folio_mov \n");
			sql.append(" 			FROM movimiento  \n");
			sql.append(" 			WHERE no_folio_det = " + sqlFolio + ")  \n");
			
			int result = jdbcTemplate.update(sql.toString());

			if (result > 0) retorno.put("result", 0);
		} catch (Exception e) {
			Map<String, Object> retornoInsertar =  new HashMap<String, Object>();
			retorno.put("mensaje", "Error al actualizar el movimiento del grupo_pago de los hijos");
			retorno.put("result", 330);
			String desc = "Actualizar 330" + e.getCause().toString().replace("'", "''");
			retornoInsertar = insertarBitacoraProcesos("sp_confirmador", 330, desc);
			if ((Integer)retornoInsertar.get("result") != 0) return retornoInsertar;
		}
		return retorno;
	}

/*
	private List<Map<String, Object>> obtenerDatosMovimiento320(int sqlFolio) {
		List<Map<String, Object>> list =  new ArrayList<Map<String, Object>>();
		try {
			StringBuffer sql = new StringBuffer();
			
			sql.append(" SELECT  COALESCE(tipo_cambio,0) as sqlTipoCambio,  \n");
			sql.append(" 	COALESCE(id_estatus_mov,'') as sqlEstatusMov, \n");
			sql.append(" 	COALESCE(b_entregado,'N') as sqlBEntregado,  \n");
			sql.append(" 	COALESCE(grupo_pago,0) as sqlGrupoPago,  \n");
			sql.append(" 	COALESCE(no_folio_mov,0) as noFolioMov, \n");
			sql.append(" 	referencia \n");
			sql.append(" FROM movimiento \n");
			sql.append(" WHERE no_folio_det = " + sqlFolio + " \n");
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, Object>>() {
				public Map<String, Object> mapRow(ResultSet rs, int idx) throws SQLException {
					Map<String, Object> cons = new HashMap<String, Object>();
					cons.put("sqlTipoCambio", rs.getDouble("sqlTipoCambio"));
					cons.put("sqlEstatusMov", rs.getString("sqlEstatusMov"));
					cons.put("sqlBEntregado", rs.getString("sqlBEntregado"));
					cons.put("sqlGrupoPago", rs.getInt("sqlGrupoPago"));
					cons.put("noFolioMov", rs.getInt("noFolioMov"));
					cons.put("referencia", rs.getString("referencia"));
					cons.put("result", 0);
		    		return cons;
				}
		    });
		} catch (Exception e) {
			Map<String, Object> retorno =  new HashMap<String, Object>();
			Map<String, Object> retornoInsertar =  new HashMap<String, Object>();
			retorno.put("mensaje", "Error al obtener tipo_cambio, id_estatus_mov, b_entregado, grupo_pago, no_folio_mov");
			retorno.put("result", 320);
			retornoInsertar = insertarBitacoraProcesos("sp_confirmador", 320, "Error al obtener tipo_cambio, id_estatus_mov, b_entregado, grupo_pago, no_folio_mov");
			if ((Integer)retornoInsertar.get("result") != 0) list.set(0, retornoInsertar);
			list.set(0, retorno);
		}
		return list;
	}
*/

	private Map<String, Object> insertarBitacoraProcesos(String proceso, int numErr, String descripcion) {
		Map<String, Object> retorno =  new HashMap<String, Object>();
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" INSERT INTO bitacora_procesos \n");
			sql.append(" VALUES ('" + proceso + "', GETDATE(), " + numErr + ", '" + descripcion + "') \n");
			int result = jdbcTemplate.update(sql.toString());
			if (result > 0) retorno.put("result", 0);
		} catch (Exception e) {
			retorno.put("mensaje", "Error al insertar en bitacora: (P)" + proceso + " (E)" + numErr + " (D)" + descripcion);
			retorno.put("result", 310);
		}
		return retorno;
	}


	public int updateReferenciaFolBco(String referencia, int iFoliosDet, String psFolioBanco, Date FechaHoy){
		StringBuffer sql = new StringBuffer();
		
		try{
			sql.append("  UPDATE movimiento ");
			sql.append("  SET referencia = '" + referencia + "', folio_banco = '" + psFolioBanco + "' ");
			
			if(!FechaHoy.equals(new StringBuffer()) || !FechaHoy.equals(null))
				sql.append("  ,fec_conf_trans = '" + funciones.ponerFecha(FechaHoy) + "' ");
			
			sql.append("  WHERE no_folio_det = " + iFoliosDet + new StringBuffer());
			sql.append("  	OR folio_ref = " + iFoliosDet + new StringBuffer());
			
			System.out.println("updateReferenciaFolBco: " + sql.toString());

		    insertarBitacora("actualiza referencia");
			return jdbcTemplate.update(sql.toString());

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConfirmacionTransferenciasDao, M:updateReferenciaFolBco ");
			return 0;
		}
	}
	
	public String consultarConfiguraSet(int indice){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
	}
	
	public Date obtenerFechaHoy(){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.obtenerFechaHoy();
	}
	
	public void setDataSource(DataSource dataSource){
		try {
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConfirmacionTransferenciasDao, M:setDataSource ");
		}
	}
	@Override
	public int validaConfirmacion() {
		int res = 0;
		try {
			StringBuffer sql = new StringBuffer();
			
			sql.append(" SELECT count(*) \n");
			sql.append(" FROM BOTON_USUARIO b JOIN SEG_FAC_BOTON s \n");
			sql.append(" ON b.ID_BOTON = s.ID_BOTON \n");
			sql.append(" WHERE ID_MODULO = 49 \n");
			sql.append(" AND ID_USUARIO = " + GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario() + "\n");
			sql.append(" and b.ID_BOTON = 'filtroConfimacion' \n");
			
			res = jdbcTemplate.queryForInt(sql.toString());

		    insertarBitacora("valida confirmador");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConfirmacionTransferenciasDao, M:validaConfirmacion ");
		}		
		return res;		
	}
}
