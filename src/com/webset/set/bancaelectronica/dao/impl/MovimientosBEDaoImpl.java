package com.webset.set.bancaelectronica.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.bancaelectronica.dao.MovimientosBEDao;
import com.webset.set.bancaelectronica.dto.MovimientosBEDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;
/**
 * @author Jessica Arelly Cruz Cruz
 * @since 01/03/2011
 */
public class MovimientosBEDaoImpl implements MovimientosBEDao  {
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	private static Logger logger = Logger.getLogger(MovimientosBEDaoImpl.class);
	public List<LlenaComboGralDto> llenarComboBancos(int noEmpresa){
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		try{
		      sb.append("\n Select distinct cat_banco.id_banco as ID, ");
		      sb.append("\n cat_banco.desc_banco as DESCRIPCION ");
		      sb.append("\n From cat_banco left outer join cat_cta_banco on (cat_cta_banco.id_banco = cat_banco.id_banco)");
		      sb.append("\n Where ");
		      if(noEmpresa!=0)
		      sb.append("\n cat_cta_banco.no_empresa = " + noEmpresa);
		      
		      System.out.println("Query:"+sb.toString());
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
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectronica, C:MovimientosBEDao, M:llenarComboBancos");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectronica, C:MovimientosBEDao, M:llenarComboBancos");
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
			+ "P:BancaElectronica, C:MovimientosBEDao, M:obtenerConceptos");
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
		      if(noBanco != 0 && noEmpresa !=0){
		    	  sb.append("\n Where id_banco = " + noBanco);
		          sb.append("\n and no_empresa = " + noEmpresa);
		      }else{
		    	  if(noBanco != 0 && noEmpresa ==0)
		    		  sb.append("\n Where id_banco = " + noBanco);  
		    	  if(noBanco == 0 && noEmpresa !=0)
		    		  sb.append("\n Where no_empresa = " + noEmpresa);  
		      }
		      System.out.println(sb.toString());

		     listDatos= jdbcTemplate.query(sb.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setDescripcion(rs.getString("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:BancaElectronica, C:MovimientosBEDao, M:llenarComboChequeras");
		}
		return listDatos;
	}
	
	
	
	/**
	 * FunSQL_ReporteBE
	 * @param datos
	 * @return
	 */
	public List<MovimientosBEDto> ejecutarReporteConceptoBE(Map<String, String> datos){
		List <MovimientosBEDto> objResultado = new ArrayList<MovimientosBEDto>();
		StringBuffer sb = new StringBuffer();
		try{
			 if (datos.get("pbdetalle").equals("true")){
	        	if(datos.get("piBancoInf").equals("12")){			
	        		  sb.append("\n select 1 as detalle,m.descripcion as desc_observacion, ");
	                  sb.append("\n e.nom_empresa,m.no_empresa,m.id_banco,m.secuencia, cb.desc_banco,m.id_chequera ,convert(date,m.fec_valor,103) as fec_valor,m.sucursal,");
	                  sb.append("\n m.folio_banco,m.referencia,ccb.id_divisa,importe, ");
	                  sb.append("\n m.id_cve_concepto as concepto_set, m.observacion, m.cargo_abono, case when m.cargo_abono = 'E'");
	                  sb.append("\n then importe* -1 else importe end as importe_ca ");
	                  sb.append("\n,case when m.referencia = '1' and m.cargo_abono = 'E' ");
	                  sb.append("\n        then m.referencia ");
	                  sb.append("\n        Else '' end as no_folio_det, ");
	                  sb.append("\n case when m.id_estatus_trasp='T' then 'S' else 'N' end as ejecutado,convert(date,m.fec_alta,103) as fec_alta,m.b_trasp_banco,m.b_trasp_conta ");
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
	                  sb.append("\n and m.id_banco = " + Utilerias.validarCadenaSQL(datos.get("piBancoSup") + " "));
	                  sb.append("\n and m.id_chequera like '" + Utilerias.validarCadenaSQL(datos.get("psChequera")) + "' ");
	                  sb.append("\n and m.fec_alta between '" + (Utilerias.validarCadenaSQL(datos.get("pdFechaInf"))) + "' and '" + (Utilerias.validarCadenaSQL(datos.get("pdFechaSup"))) + "' ");
	                  sb.append("\n group by e.nom_empresa,m.no_empresa,m.id_banco,m.secuencia, cb.desc_banco,m.id_chequera ,m.fec_valor,m.sucursal,");
	                  sb.append("\n m.folio_banco,m.referencia,ccb.id_divisa,importe, m.observacion, m.cargo_abono,");
	                  sb.append("\n m.fec_alta , m.b_trasp_banco, m.b_trasp_conta, m.id_cve_concepto, m.id_estatus_trasp, m.descripcion ");
			  }else{ // Banco if
	        		  sb.append("\n select 1 as detalle,m.descripcion as desc_observacion, case when c.concepto_set is null");
	                  sb.append("\n then m.concepto else c.concepto_set end as concepto,e.nom_empresa,m.no_empresa,m.id_banco,m.secuencia, cb.desc_banco,m.id_chequera ,convert(date,m.fec_valor,103) as fec_valor,m.sucursal,");
	                  sb.append("\n m.folio_banco,m.referencia,ccb.id_divisa,convert(float,m.importe) as importe, case when c.concepto_set is null");
	                  sb.append("\n then m.concepto else c.concepto_set end as concepto_set, m.observacion, m.cargo_abono, case when m.cargo_abono = 'E'");
	                  sb.append("\n then convert(float,importe)* -1 else convert(float,importe) end as importe_ca ");
	                  sb.append("\n ,case when m.referencia = '1' and m.cargo_abono = 'E' ");
	                  sb.append("\n        then m.referencia ");
	                  sb.append("\n        Else '' end as no_folio_det, ");
	                  sb.append("\n case when m.id_estatus_trasp='T' then 'S' else 'N' end as ejecutado,convert(date,m.fec_alta,103) as fec_alta,m.b_trasp_banco,m.b_trasp_conta ");
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
	                  sb.append("\n and m.fec_alta between convert(datetime,'" + (Utilerias.validarCadenaSQL(datos.get("pdFechaInf"))) + "',103) and convert(datetime,'" + (Utilerias.validarCadenaSQL(datos.get("pdFechaSup"))) + "',103) ");
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
		            sb.append("\n and m.fec_alta between '" + (Utilerias.validarCadenaSQL(datos.get("pdFechaInf"))) + "' and '" + (Utilerias.validarCadenaSQL(datos.get("pdFechaSup"))) + "' ");
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
	               sb.append("\n and m.fec_alta between convert(datetime,'" + (Utilerias.validarCadenaSQL(datos.get("pdFechaInf"))) + "',103) and convert(datetime,'" + (Utilerias.validarCadenaSQL(datos.get("pdFechaSup"))) + "',103) ");
	               sb.append("\n group by concepto,m.id_banco,cb.desc_banco,e.nom_empresa,m.no_empresa,m.cargo_abono, m.id_chequera");
			  }
			 
	  }
			// System.out.println("ejecutarReporteConceptoBE "+sb);
	 objResultado = jdbcTemplate.query(sb.toString(), new RowMapper<MovimientosBEDto>(){
	   public MovimientosBEDto mapRow(ResultSet rs, int idx) throws SQLException {
        		MovimientosBEDto dto= new MovimientosBEDto();
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
        			dto.setFolioDet(rs.getString("NO_FOLIO_DET"));
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
        	}
	   });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:BancaElectronica, C:MovimientosBEDao, M:ejecutarReporteConceptoBE");
		}
		return objResultado;
	}
	
	
			
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<MovimientosBEDto> ejecutarReporteBE(Map<String, String> datos) {
		List <MovimientosBEDto> paramRetorno = new ArrayList<MovimientosBEDto>();
		String squery = "";
		try{
		    squery += consultarReporteBE(1, datos);
		    squery += "\n UNION \n";
		    squery += consultarReporteBE(2, datos);
		    squery += "\n order by be.cargo_abono, fec_valor ";
		    System.out.println("reporte movtos be "+squery);
		    paramRetorno = jdbcTemplate.query(squery, new RowMapper<MovimientosBEDto>(){
	        	public MovimientosBEDto mapRow(ResultSet rs, int idx) throws SQLException {
	        		MovimientosBEDto dto= new MovimientosBEDto();
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
        			dto.setFolioDet(rs.getString("no_folio_det"));
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
			logger.error(e);
        	e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:BancaElectronica, C:MovimientosBEDao, M:ejecutarReporteBE");
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
            sb.append("\n         ELSE '0' END AS no_folio_det, ");
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
            sb.append("\n AND be.no_empresa BETWEEN " + Utilerias.validarCadenaSQL(datos.get("piEmpresaInf")) + " AND " + datos.get("piEmpresaSup") + " ");
            sb.append("\n AND be.id_banco = b.id_banco ");
            sb.append("\n AND b.b_banca_elect IN ('A','I') ");
            sb.append("\n AND be.id_banco BETWEEN " + Utilerias.validarCadenaSQL(datos.get("piBancoInf")) + " AND " + Utilerias.validarCadenaSQL(datos.get("piBancoSup")) + " ");
            sb.append("\n AND be.id_chequera LIKE '" + Utilerias.validarCadenaSQL(datos.get("psChequera")) + "' ");
            sb.append("\n AND be.fec_valor BETWEEN convert(datetime,'" + Utilerias.validarCadenaSQL(datos.get("pdFechaInf")) + "',103) AND convert(datetime,'" + Utilerias.validarCadenaSQL(datos.get("pdFechaSup")) + "',103) ");
            sb.append("\n AND (c.concepto_set LIKE '" + Utilerias.validarCadenaSQL(datos.get("concepto")) + "%' OR be.concepto LIKE '" + Utilerias.validarCadenaSQL(datos.get("concepto")) + "%')");
            sb.append("\n AND be.cargo_abono LIKE '" + Utilerias.validarCadenaSQL(datos.get("tipoMov")) + "' ");
           // sb.append("\n AND be.movto_arch LIKE '" + Utilerias.validarCadenaSQL(datos.get("origenMovimiento")) + "' ");
            squery = squery + sb.toString();
           
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:BancaElectronica, C:MovimientosBEDao, M:consultarReporteBE");
		}return squery;
	}

}
