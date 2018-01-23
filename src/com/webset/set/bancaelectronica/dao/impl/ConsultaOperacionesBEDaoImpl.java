package com.webset.set.bancaelectronica.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.bancaelectronica.dao.ConsultaOperacionesBEDao;
import com.webset.set.bancaelectronica.dto.ConsultaOperacionesBEDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;
/**
 * @author YEC
 * @since 22 DE DICIEMBRE DEL 2015
 */
public class ConsultaOperacionesBEDaoImpl implements ConsultaOperacionesBEDao  {
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	
	public List<ConsultaOperacionesBEDto> llenaGrid(String folioBanco, String idChequera) {
		List<ConsultaOperacionesBEDto> listaResultado = new ArrayList<ConsultaOperacionesBEDto>();
		StringBuffer sql = new StringBuffer();
		StringBuffer sql1 = new StringBuffer();
		try{
			sql1.append("SELECT distinct");
			sql1.append("\n b.secuencia");
			sql1.append("\n ,convert(char,b.fec_valor,5) as fec_valor");
			sql1.append("\n ,b.sucursal");
			sql1.append("\n ,b.folio_banco");
			sql1.append("\n ,COALESCE(b.referencia, ' ') as referencia");
			sql1.append("\n ,b.cargo_abono");
			sql1.append("\n ,COALESCE(b.observacion,' ') as observacion");
			sql1.append("\n ,b.importe");
			sql1.append("\n ,b.b_salvo_buen_cobro");
			sql1.append("\n ,e.concepto_set as concepto");
			sql1.append("\n ,b.id_estatus_trasp");
			sql1.append("\n ,b.b_trasp_banco");
			sql1.append("\n ,b.b_trasp_conta");
			sql1.append("\n ,COALESCE(b.id_rubro, 0) as id_rubro");
			sql1.append("\n FROM");
			sql1.append("\n movto_banca_e b");
			sql1.append("\n ,equivale_concepto e");
			sql.append(sql1.toString()+"\n WHERE");
			//sql.append("\n b.no_empresa = " & pvvValor4");
			//sql.append("\n and b.id_estatus_trasp = 'N'");
			
			sql.append("\n b.id_estatus_trasp = 'N'");
			sql.append("\n and e.concepto_set like 'TRANSFERENCIA'");
			sql.append("\n and sucursal =6202");
			sql.append("\n and importe= 2576113.14");
			
			
			
			/*sql.append("\n and b.id_banco = e.id_banco ");
			sql.append("\n and b.concepto = e.concepto_set");
			sql.append("\n and b.cargo_abono = e.cargo_abono");*/
			//si banco no vacio
			//sql.append("\n AND b.id_banco = " & pvvValor5");
			//idChequera no vacio
			//sql.append("\n AND b.id_chequera = '" & pvvValor6 & "'");
			/*
			 *  If pvbValor3 Then
		            sSQL = sSQL & " AND b.fec_valor BETWEEN '" & PonFechaDMA(Format(pvvValor7, "dd/MM/yyyy") & " 00:00", True) & "' AND '" & PonFechaDMA(Format(pvvValor7, "dd/MM/yyyy") & " 23:59", True) & "' "
		        End If
			 */
			sql.append("\n UNION ");
			sql.append("\n "+ sql1.toString());
			sql.append("\n WHERE");
			//sql.append("\n b.no_empresa = " & pvvValor4);
			//sql.append("\n and b.id_estatus_trasp = 'N'");
			sql.append("\n  b.id_estatus_trasp = 'N'"); //MIENTRAS SE PONE LA EMPRESA
			sql.append("\n  and e.id_banco = 0");
			sql.append("\n  and b.concepto = e.concepto_set");
			sql.append("\n and b.cargo_abono = e.cargo_abono");
			 
			/********************* sigue 
        If pvbValor1 Then
            sSQL = sSQL & " AND b.id_banco = " & pvvValor5
        End If
        If pvbValor2 Then
            sSQL = sSQL & " AND b.id_chequera = '" & pvvValor6 & "'"
        End If
        If pvbValor3 Then
            sSQL = sSQL & " AND b.fec_valor BETWEEN '" & PonFechaDMA(Format(pvvValor7, "dd/MM/yyyy") & " 00:00", True) & "' AND '" & PonFechaDMA(Format(pvvValor7, "dd/MM/yyyy") & " 23:59", True) & "' "
        End If
        
        sSQL = sSQL + " a "
			 */
			sql.append("\n ORDER BY secuencia");
			System.out.println(sql.toString());
				
				listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<ConsultaOperacionesBEDto>(){
					public ConsultaOperacionesBEDto mapRow(ResultSet rs, int idx) throws SQLException{
						ConsultaOperacionesBEDto campos = new ConsultaOperacionesBEDto();
						campos.setSecuencia(rs.getLong("SECUENCIA"));
						campos.setObservacion(rs.getString("OBSERVACION"));
						campos.setCargoAbono(rs.getString("CARGO_ABONO"));
						campos.setSbc(rs.getString("B_SALVO_BUEN_COBRO"));
						campos.setFolioBanco(rs.getString("FOLIO_BANCO"));
						campos.setImporte(Double.parseDouble(rs.getString("IMPORTE")));
						campos.setReferencia(rs.getString("REFERENCIA"));
						campos.setConcepto(rs.getString("CONCEPTO"));
						campos.setSucursal(rs.getString("SUCURSAL"));
						campos.setIdEstatusTraspaso(rs.getString("ID_ESTATUS_TRASP"));
						campos.setFecValor(rs.getDate("FEC_VALOR"));
						//campos.setGrabados(rs.getString("SUCURSAL"));
						campos.setIdRubro(rs.getString("SUCURSAL"));
						campos.setbTraspBanco(rs.getString("B_TRASP_BANCO"));
						campos.setbTraspConta(rs.getString("B_TRASP_CONTA"));
						return campos;
					}
				});
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Bancaelectronica, C:ConsultaOperacionesBEDaoImpl, M:llenaGrid");
		}return listaResultado;
	}
	
	public List<LlenaComboGralDto> llenarComboBancos(int noEmpresa){
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		try{
		      sb.append("\n Select distinct cat_banco.id_banco as ID, ");
		      sb.append("\n cat_banco.desc_banco as DESCRIPCION ");
		      sb.append("\n From cat_banco left outer join cat_cta_banco on (cat_cta_banco.id_banco = cat_banco.id_banco)");
		      sb.append("\n ");
		      if(noEmpresa!=0)
		      sb.append("\n and cat_cta_banco.no_empresa = " + noEmpresa);
		    listDatos= jdbcTemplate.query(sb.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}});
		}
		catch(CannotGetJdbcConnectionException e){
			LlenaComboGralDto cons = new LlenaComboGralDto();
			cons.setCampoUno("Error de conexion");
			listDatos.add(cons);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectronica, C:ConsultaOperacionesBEDao, M:llenarComboBancos");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectronica, C:ConsultaOperacionesBEDao, M:llenarComboBancos");
		}return listDatos;
	}
	
	/**
	 * FunSQLSelect309
	 * @param noBanco
	 * @return
	 */
	public String seleccionarBancaElect(int noBanco) {
		StringBuffer sb = new StringBuffer();
		List <ComunDto> result = null;
		try{
			sb.append( " Select b_banca_elect ");
			sb.append( " FROM cat_banco ");
			sb.append( " WHERE id_banco = " + noBanco);
			result = jdbcTemplate.query(sb.toString(), new RowMapper<ComunDto>(){
				public ComunDto mapRow(ResultSet rs, int idx) throws SQLException {
					ComunDto control = new ComunDto();
					control.setDescripcion(rs.getString("b_banca_elect"));
					return control;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:BancaElectronica, C:MovimientosBEDao, M:seleccionarBancaElect");
		} return result.isEmpty()? "" : result.get(0).getDescripcion();
	}
	
	/**
	 * FunSQLSelect310
	 * @param lbGenerico
	 * @param banco
	 * @return
	 */
	public List<ComunDto>obtenerConceptos(boolean lbGenerico, int noBanco){
		StringBuffer sb = new StringBuffer();
		List<ComunDto> listConceptos= new ArrayList<ComunDto>();
		try{
			sb.append( " Select distinct concepto_set as desc_concepto ");
			sb.append( "\n  FROM equivale_concepto ");
			sb.append( "\n  WHERE ");
			if (lbGenerico)
				sb.append( "\n  id_banco = " + noBanco);
		    else
		    	sb.append( "\n id_banco = 0");
		   
			listConceptos= jdbcTemplate.query(sb.toString(), new RowMapper<ComunDto>(){
				public ComunDto mapRow(ResultSet rs, int idx) throws SQLException {
					ComunDto cons = new ComunDto();
					cons.setDescripcion(rs.getString("desc_concepto"));
					return cons;
			}});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:BancaElectronica, C:ConsultaOperacionesBEDao, M:obtenerConceptos");
		}
		return listConceptos;
	}
	
	public List<LlenaComboGralDto> llenarComboChequeras(int noBanco,int noEmpresa){
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		try{
		      sb.append("\n Select id_chequera as ID, ");
		      sb.append("\n id_chequera as DESCRIPCION ");
		      sb.append("\n From cat_cta_banco");
		      sb.append("\n Where id_banco = " + noBanco);
		      
		      if( noEmpresa!= 0 ){
		      sb.append("\n and no_empresa = " + noEmpresa);
		      }
		      listDatos= jdbcTemplate.query(sb.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setDescripcion(rs.getString("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:BancaElectronica, C:ConsultaOperacionesBEDao, M:llenarComboChequeras");
		}
		return listDatos;
	}
	
	public List<LlenaComboGralDto> llenaComboGrupo(){
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		try{
		      sb.append("\n Select ID_GRUPO as ID, ");
		      sb.append("\n DESC_GRUPO as DESCRIPCION ");
		      sb.append("\n From cat_grupo");
		      System.out.println(sb.toString());
		      listDatos= jdbcTemplate.query(sb.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(Integer.parseInt(rs.getString("ID")));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;	
					
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:BancaElectronica, C:ConsultaOperacionesBEDao, M:llenaComboGrupo");
		}
		return listDatos;
	}
	
	public List<LlenaComboGralDto> llenaComboRubro(String idGrupo){
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		try{
		      sb.append("\n Select ID_RUBRO as ID, ");
		      sb.append("\n DESC_RUBRO as DESCRIPCION ");
		      sb.append("\n From cat_rubro");
		      sb.append("\n Where ID_GRUPO = " + Utilerias.validarCadenaSQL(idGrupo));
		      System.out.println(sb.toString());
		      listDatos= jdbcTemplate.query(sb.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(Integer.parseInt(rs.getString("ID")));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:BancaElectronica, C:ConsultaOperacionesBEDao, M:llenarComboChequeras");
		}
		return listDatos;
	}
	
	public List<ConsultaOperacionesBEDto> contabiliza(String idRubro){
		List <ConsultaOperacionesBEDto> objResultado = new ArrayList<ConsultaOperacionesBEDto>();
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("SELECT coalesce(b_contabiliza,'N') as b_contabiliza ,coalesce(RTRIM(LTRIM(EQUIVALE_RP)),' ') AS equivale_rp");
			sql.append("\n FROM CAT_RUBRO");
			sql.append("\n WHERE id_rubro = " + Utilerias.validarCadenaSQL(idRubro));
			System.out.println(sql.toString());
			objResultado= jdbcTemplate.query(sql.toString(), new RowMapper<ConsultaOperacionesBEDto>(){
				public ConsultaOperacionesBEDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConsultaOperacionesBEDto dto = new ConsultaOperacionesBEDto();
					dto.setbContabiliza(rs.getString("b_contabiliza"));
					dto.setEquivaleRP(rs.getString("equivale_rp"));
					return dto;
				}});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectronica, C:ConsultaOperacionesBEDao, M:contabiliza");
			
		}return objResultado;
	}
	/**
	 * FunSQL_ReporteBE
	 * @param datos
	 * @return
	 */
	public List<ConsultaOperacionesBEDto> ejecutarReporteConceptoBE(Map<String, String> datos){
		List <ConsultaOperacionesBEDto> objResultado = new ArrayList<ConsultaOperacionesBEDto>();
		StringBuffer sb = new StringBuffer();
		try{
			 if (datos.get("pbdetalle").equals("true")){
	        	if(datos.get("piBancoInf").equals("12")){			
	        		  sb.append("\n select 1 as detalle,m.descripcion as desc_observacion, ");
	                  sb.append("\n e.nom_empresa,m.no_empresa,m.id_banco,m.secuencia, cb.desc_banco,m.id_chequera ,m.fec_valor,m.sucursal,");
	                  sb.append("\n m.folio_banco,m.referencia,ccb.id_divisa,importe, ");
	                  sb.append("\n m.id_cve_concepto as concepto_set, m.observacion, m.cargo_abono, case when m.cargo_abono = 'E'");
	                  sb.append("\n then importe* -1 else importe end as importe_ca ");
	                  sb.append("\n,case when m.referencia = '1' and m.cargo_abono = 'E' ");
	                  sb.append("\n        then m.referencia ");
	                  sb.append("\n        Else '' end as no_folio_det, ");
	                  sb.append("\n case when m.id_estatus_trasp='T' then 'S' else 'N' end as ejecutado,m.fec_alta,m.b_trasp_banco,m.b_trasp_conta ");
	                  sb.append("\n from movto_banca_e m left join equivale_concepto c on (m.concepto = c.desc_concepto),cat_banco  cb,empresa e,");
	                  sb.append("\n cat_cta_banco ccb");
	                  sb.append("\n Where m.id_cve_concepto Not in (select distinct e.id_cve_leyenda from equivale_concepto e where e.id_cve_leyenda in");
	                  sb.append("\n (select n.id_cve_leyenda from equivale_concepto n) and e.id_banco = m.id_banco");
	                  sb.append("\n and e.cargo_abono=m.cargo_abono)");
	                  sb.append("\n and m.no_empresa = ccb.no_empresa and m.id_banco = ccb.id_banco and m.id_chequera = ccb.id_chequera");
	                  sb.append("\n and m.id_estatus_trasp='N' and movto_arch=1 and cb.id_banco = m.id_banco ");
	                  sb.append("\n And e.no_empresa = m.no_empresa ");
	                  if(!datos.get("piEmpresaSup").equals("0"))
	                  sb.append("\n and m.no_empresa between " + Utilerias.validarCadenaSQL(datos.get("piEmpresaInf")) + " and " + Utilerias.validarCadenaSQL(datos.get("piEmpresaSup")) + " ");
	                  sb.append("\n and m.id_banco = " + Utilerias.validarCadenaSQL(datos.get("piBancoSup")) + " ");
	                  sb.append("\n and m.id_chequera like '" + Utilerias.validarCadenaSQL(datos.get("psChequera")) + "' ");
	                  sb.append("\n and m.fec_alta between '" + (Utilerias.validarCadenaSQL(datos.get("pdFechaInf"))) + "' and '" + (Utilerias.validarCadenaSQL(datos.get("pdFechaSup"))) + "' ");
	                  sb.append("\n group by e.nom_empresa,m.no_empresa,m.id_banco,m.secuencia, cb.desc_banco,m.id_chequera ,m.fec_valor,m.sucursal,");
	                  sb.append("\n m.folio_banco,m.referencia,ccb.id_divisa,importe, m.observacion, m.cargo_abono,");
	                  sb.append("\n m.fec_alta , m.b_trasp_banco, m.b_trasp_conta, m.id_cve_concepto, m.id_estatus_trasp, m.descripcion ");
			  }else{ // Banco if
	        		  sb.append("\n select 1 as detalle,m.descripcion as desc_observacion, case when c.concepto_set is null");
	                  sb.append("\n then m.concepto else c.concepto_set end as concepto,e.nom_empresa,m.no_empresa,m.id_banco,m.secuencia, cb.desc_banco,m.id_chequera ,m.fec_valor,m.sucursal,");
	                  sb.append("\n m.folio_banco,m.referencia,ccb.id_divisa,convert(float,m.importe) as importe, case when c.concepto_set is null");
	                  sb.append("\n then m.concepto else c.concepto_set end as concepto_set, m.observacion, m.cargo_abono, case when m.cargo_abono = 'E'");
	                  sb.append("\n then convert(float,importe)* -1 else convert(float,importe) end as importe_ca ");
	                  sb.append("\n ,case when m.referencia = '1' and m.cargo_abono = 'E' ");
	                  sb.append("\n        then m.referencia ");
	                  sb.append("\n        Else '' end as no_folio_det, ");
	                  sb.append("\n case when m.id_estatus_trasp='T' then 'S' else 'N' end as ejecutado,m.fec_alta,m.b_trasp_banco,m.b_trasp_conta ");
	                  sb.append("\n from movto_banca_e m left join equivale_concepto c on (m.concepto = c.desc_concepto and m.id_cve_concepto=c.id_cve_concepto),cat_banco  cb,empresa e,");
	                  sb.append("\n cat_cta_banco ccb");
	                  sb.append("\n Where Not concepto in (select desc_concepto from equivale_concepto e where  e.id_banco = m.id_banco");
	                  sb.append("\n and e.cargo_abono=m.cargo_abono)");
	                  sb.append("\n and m.no_empresa = ccb.no_empresa and m.id_banco = ccb.id_banco and m.id_chequera = ccb.id_chequera");
	                  sb.append("\n and m.id_estatus_trasp='N' and movto_arch=1 and cb.id_banco = m.id_banco ");
	                  sb.append("\n And e.no_empresa = m.no_empresa ");
	                  if(!datos.get("piEmpresaSup").equals("0"))
	                  sb.append("\n and m.no_empresa between " + Utilerias.validarCadenaSQL(datos.get("piEmpresaInf")) + " and " + Utilerias.validarCadenaSQL(datos.get("piEmpresaSup")) + " ");
	                  sb.append("\n and m.id_banco = " + Utilerias.validarCadenaSQL(datos.get("piBancoSup")) + " ");
	                  sb.append("\n and m.id_chequera like '" + Utilerias.validarCadenaSQL(datos.get("psChequera")) + "' ");
	                  sb.append("\n and m.fec_alta between '" + (Utilerias.validarCadenaSQL(datos.get("pdFechaInf"))) + "' and '" + (Utilerias.validarCadenaSQL(datos.get("pdFechaSup"))) + "' ");
	                  sb.append("\n group by e.nom_empresa,m.no_empresa,m.id_banco,m.secuencia, cb.desc_banco,m.id_chequera ,m.fec_valor,m.sucursal,");
	                  sb.append("\n m.folio_banco,m.referencia,ccb.id_divisa,importe,concepto_set, m.observacion, m.cargo_abono,");
	                  sb.append("\n m.fec_alta , m.b_trasp_banco, m.b_trasp_conta, m.concepto, m.id_estatus_trasp, m.descripcion ");
	          }
		}else{ //DETALLE
			 if(datos.get("piBancoInf").equals("12")){ 
	        	 	sb.append("\n select 2 as detalle,count(m.no_empresa) as cuenta,e.nom_empresa,m.no_empresa,");
	        	 	sb.append("\n m.id_banco , m.id_cve_concepto as concepto, cb.desc_banco, m.cargo_abono, m.id_chequera");
	        	 	sb.append("\n from movto_banca_e m,cat_banco  cb,empresa e, equivale_concepto c ");
		            sb.append("\n Where m.id_cve_concepto Not in ");
		            sb.append("\n (select distinct e.id_cve_leyenda from equivale_concepto e ");
		            sb.append("\n where e.id_cve_leyenda in(select n.id_cve_leyenda from equivale_concepto n) and");
		            sb.append("\n e.id_banco = m.id_banco and e.cargo_abono=m.cargo_abono) ");
		            sb.append("\n and m.id_estatus_trasp='N' and movto_arch=1 and cb.id_banco = m.id_banco ");
		            sb.append("\n And e.no_empresa = m.no_empresa ");
		            if(!datos.get("piEmpresaSup").equals("0"))
		            sb.append("\n and m.no_empresa between " + Utilerias.validarCadenaSQL(datos.get("piEmpresaInf")) + " and " + Utilerias.validarCadenaSQL(datos.get("piEmpresaSup")) + " ");
		            sb.append("\n and m.id_banco = " + Utilerias.validarCadenaSQL(datos.get("piBancoInf")) + " ");
		            sb.append("\n and m.id_chequera like '" + Utilerias.validarCadenaSQL(datos.get("psChequera")) + "' ");
		            sb.append("\n and m.fec_alta between '" + (Utilerias.validarCadenaSQL(datos.get("pdFechaInf"))) + "' and '" + Utilerias.validarCadenaSQL((datos.get("pdFechaSup"))) + "' ");
		            sb.append("\n group by m.id_cve_concepto,m.id_banco,cb.desc_banco,e.nom_empresa,m.no_empresa,m.cargo_abono, m.id_chequera");
			  }else{
	               sb.append("\n select 2 as detalle,count(m.no_empresa) as cuenta,e.nom_empresa,m.no_empresa,");
	               sb.append("\n m.id_banco , m.concepto , cb.desc_banco, m.cargo_abono, m.id_chequera");
	               sb.append("\n from movto_banca_e m,cat_banco  cb,empresa e, equivale_concepto c ");
	               sb.append("\n Where Not concepto in ");
	               sb.append("\n (select desc_concepto from equivale_concepto e ");
	               sb.append("\n where  e.id_banco = m.id_banco and e.cargo_abono=m.cargo_abono) ");
	               sb.append("\n and m.id_estatus_trasp='N' and movto_arch=1 and cb.id_banco = m.id_banco ");
	               sb.append("\n And e.no_empresa = m.no_empresa ");
	               if(!datos.get("piEmpresaSup").equals("0"))
	               sb.append("\n and m.no_empresa between " + Utilerias.validarCadenaSQL(datos.get("piEmpresaInf")) + " and " + Utilerias.validarCadenaSQL(datos.get("piEmpresaSup")) + " ");
	               sb.append("\n and m.id_banco between " + Utilerias.validarCadenaSQL(datos.get("piBancoInf")) + " and " + Utilerias.validarCadenaSQL(datos.get("piBancoSup")) + " ");
	               sb.append("\n and m.id_chequera like '" + Utilerias.validarCadenaSQL(datos.get("psChequera")) + "' ");
	               sb.append("\n and m.fec_alta between '" + (Utilerias.validarCadenaSQL(datos.get("pdFechaInf"))) + "' and '" + (Utilerias.validarCadenaSQL(datos.get("pdFechaSup"))) + "' ");
	               sb.append("\n group by concepto,m.id_banco,cb.desc_banco,e.nom_empresa,m.no_empresa,m.cargo_abono, m.id_chequera");
			  }
	  }
	 objResultado = jdbcTemplate.query(sb.toString(), new RowMapper<ConsultaOperacionesBEDto>(){
	   public ConsultaOperacionesBEDto mapRow(ResultSet rs, int idx) throws SQLException {
        		ConsultaOperacionesBEDto dto= new ConsultaOperacionesBEDto();
        		String detalle=rs.getString("detalle");
        		dto.setNomEmpresa(rs.getString("NOM_EMPRESA"));
    			dto.setNoEmpresa(Integer.parseInt(rs.getString("NO_EMPRESA")));
    			dto.setIdBanco(Integer.parseInt(rs.getString("ID_BANCO")));
    			dto.setCargoAbono(rs.getString("CARGO_ABONO"));
    			dto.setDescBanco(rs.getString("DESC_BANCO"));
    			dto.setIdChequera(rs.getString("ID_CHEQUERA"));
    			if(detalle.equals("1")){
    				dto.setTipoConsulta("rConceptoConDetalle");
        			dto.setDescObservacion(rs.getString("DESC_OBSERVACION"));
        			dto.setSecuencia(Integer.parseInt(rs.getString("SECUENCIA")));
        			dto.setFecValor(rs.getDate("FEC_VALOR"));
        			dto.setSucursal(rs.getString("SUCURSAL"));
        			dto.setReferencia(rs.getString("REFERENCIA"));
        			dto.setIdDivisa(rs.getString("ID_DIVISA"));
        			dto.setImporte(rs.getDouble("IMPORTE"));
        			dto.setObservacion(rs.getString("OBSERVACION"));
        			dto.setFolioDet(rs.getInt("NO_FOLIO_DET"));
        			dto.setFecAlta(rs.getDate("FEC_ALTA"));
        			dto.setbTraspBanco(rs.getString("B_TRASP_BANCO"));
        			dto.setbTraspConta(rs.getString("B_TRASP_CONTA"));
        			dto.setEjecutado(rs.getString("EJECUTADO"));
        			dto.setImporteCa(rs.getString("IMPORTE_CA"));
        			dto.setConceptoSet(rs.getString("concepto_set"));
        			dto.setFolioBanco(rs.getString("FOLIO_BANCO"));
        		}else{
        			dto.setTipoConsulta("rConceptoSinDetalle");
        			dto.setConcepto(rs.getString("concepto"));
        		}
				return dto;
        	}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:BancaElectronica, C:ConsultaOperacionesBEDao, M:ejecutarReporteConceptoBE");
		}return objResultado;
	}
	
	
			
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<ConsultaOperacionesBEDto> ejecutarReporteBE(Map<String, String> datos) {
		List <ConsultaOperacionesBEDto> paramRetorno = new ArrayList<ConsultaOperacionesBEDto>();
		String squery = "";
		try{
		    squery += consultarReporteBE(1, datos);
		    squery += "\n UNION \n";
		    squery += consultarReporteBE(2, datos);
		    squery += "\n order by cargo_abono, fec_valor ";
		    paramRetorno = jdbcTemplate.query(squery, new RowMapper<ConsultaOperacionesBEDto>(){
	        	public ConsultaOperacionesBEDto mapRow(ResultSet rs, int idx) throws SQLException {
	        		ConsultaOperacionesBEDto dto= new ConsultaOperacionesBEDto();
	        		dto.setNomEmpresa(rs.getString("nom_empresa"));
        			dto.setNoEmpresa(Integer.parseInt(rs.getString("no_empresa")));
        			dto.setIdBanco(Integer.parseInt(rs.getString("id_banco")));
        			dto.setDescBanco(rs.getString("desc_banco"));
        			dto.setIdChequera(rs.getString("id_chequera"));
    				dto.setTipoConsulta("rBE");
        			dto.setSecuencia(Integer.parseInt(rs.getString("secuencia")));
        			dto.setFecValor(rs.getDate("fec_valor"));
        			dto.setSucursal(rs.getString("sucursal"));
        			dto.setReferencia(rs.getString("referencia"));
        			dto.setIdDivisa(rs.getString("id_divisa"));
        			dto.setImporte(rs.getDouble("importe"));
        			dto.setObservacion(rs.getString("observacion"));
        			dto.setFolioDet(rs.getInt("no_folio_det"));
        			dto.setbTraspConta(rs.getString("b_trasp_conta"));
        			dto.setFolioBanco(rs.getString("FOLIO_BANCO"));
        			dto.setEjecutado(rs.getString("EJECUTADO"));
        			dto.setImporteCa(rs.getString("IMPORTE_CA"));
        			dto.setConceptoSet(rs.getString("concepto_set"));
        			dto.setDescObservacion(rs.getString("DESC_OBSERVACION"));
        			dto.setCargoAbono(rs.getString("CARGO_ABONO"));
        			dto.setFecAlta(rs.getDate("FEC_ALTA"));
        			dto.setbTraspBanco(rs.getString("B_TRASP_BANCO"));
					return dto;
	        	}});
	 
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:BancaElectronica, C:ConsultaOperacionesBEDao, M:ejecutarReporteBE");
		}return paramRetorno;
	}
	
	public String consultarReporteBE(int contador, Map<String, String> datos){
		String squery = "";
		//String squeryder = "";
		StringBuffer sb = new StringBuffer();
		try{
			sb.append(" SELECT be.descripcion AS desc_observacion, ");
  			sb.append("\n e.nom_empresa, be.no_empresa, be.id_banco, be.secuencia, ");
  			sb.append("\n b.desc_banco, be.id_chequera, be.fec_valor, be.concepto AS sucursal, ");
  			sb.append("\n be.folio_banco, be.referencia, ccb.id_divisa,");
  			sb.append("\n be.importe AS importe, ");
  			sb.append("\n CASE WHEN c.concepto_set IS NULL THEN be.concepto ELSE c.concepto_set END AS concepto_set, ");
  			sb.append("\n be.observacion, be.cargo_abono, ");    
            sb.append("\n COALESCE(be.descripcion,'') + '/' + COALESCE(be.observacion,'') AS desc_observacion, ");
            sb.append("\n CASE WHEN be.cargo_abono = 'E' THEN be.importe * -1 ELSE be.importe END AS importe_ca ");
            sb.append( "\n ,CASE WHEN be.referencia = '1' and be.cargo_abono = 'E' ");
            sb.append("\n         THEN be.referencia ");
            sb.append("\n         ELSE '' END AS no_folio_det, ");
            sb.append("\n  CASE WHEN be.id_estatus_trasp = 'T' THEN 'S' ELSE 'N' END AS ejecutado, be.fec_alta, be.b_trasp_banco, be.b_trasp_conta ");
            if(contador == 1)
	           	sb.append("\n  FROM movto_banca_e be LEFT JOIN equivale_concepto c ON (be.id_banco = c.id_banco AND");
            else
           	sb.append("\n  FROM hist_movto_banca_e be LEFT JOIN equivale_concepto c ON (be.id_banco = c.id_banco AND");
            sb.append("\n  be.concepto = c.desc_concepto AND be.cargo_abono = c.cargo_abono)");
            sb.append("\n ,empresa e, cat_banco b, cat_cta_banco ccb ");
            sb.append("\n  WHERE ");
            sb.append("\n be.cargo_abono IN ('I','E') ");
            sb.append("\n and be.no_empresa = e.no_empresa ");
            sb.append("\n AND be.no_empresa = ccb.no_empresa ");
            sb.append("\n AND be.id_banco = ccb.id_banco ");
            sb.append("\n AND be.id_chequera = ccb.id_chequera ");
            if(!datos.get("piEmpresaSup").equals("0"))
            sb.append("\n AND be.no_empresa BETWEEN " + Utilerias.validarCadenaSQL(datos.get("piEmpresaInf")) + " AND " + Utilerias.validarCadenaSQL(datos.get("piEmpresaSup")) + " ");
            sb.append("\n AND be.id_banco = b.id_banco ");
            sb.append("\n AND b.b_banca_elect IN ('A','I') ");
            sb.append("\n AND be.id_banco BETWEEN " + Utilerias.validarCadenaSQL(datos.get("piBancoInf")) + " AND " + Utilerias.validarCadenaSQL(datos.get("piBancoSup")) + " ");
            sb.append("\n AND be.id_chequera LIKE '" + Utilerias.validarCadenaSQL(datos.get("psChequera")) + "' ");
            sb.append("\n AND be.fec_valor BETWEEN convert(datetime,'" + Utilerias.validarCadenaSQL(datos.get("pdFechaInf")) + "',103) AND convert(datetime,'" + Utilerias.validarCadenaSQL(datos.get("pdFechaSup")) + "',103) ");
            sb.append("\n AND (c.concepto_set LIKE '" + Utilerias.validarCadenaSQL(datos.get("concepto")) + "%' OR be.concepto LIKE '" + Utilerias.validarCadenaSQL(datos.get("concepto")) + "%')");
            sb.append("\n AND be.cargo_abono LIKE '" + Utilerias.validarCadenaSQL(datos.get("tipoMov")) + "' ");
            sb.append("\n AND be.movto_arch LIKE '" + Utilerias.validarCadenaSQL(datos.get("origenMovimiento")) + "' ");
            squery = squery + sb.toString();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:BancaElectronica, C:ConsultaOperacionesBEDao, M:consultarReporteBE");
		}return squery;
	}

	
	/*
Public Function FunSQLLevantaRefEnc() As ADODB.Recordset
    'Se llama en la forma frmTransIng
    Dim sSQL As String

    On Error GoTo HayError
            
    sSQL = ""
    sSQL = sSQL & "SELECT * "
    sSQL = sSQL & "FROM referencia_enc "
    sSQL = sSQL & "ORDER BY  id_banco,no_empresa "
    
    Set FunSQLLevantaRefEnc = gobjBD.obtenRecordset(sSQL)
        
Exit Function
HayError:
    Set FunSQLLevantaRefEnc = Nothing
    BitacoraError "NetroBancaESQL", "ClsBancaESQL", "FunSQLLevantaRefEnc", Err.Number, Err.Description, sSQL
    Err.Raise Err.Number, "FunSQLLevantaRefEnc", Err.Description
End Function
	 * 
	 * 
	 */
}
