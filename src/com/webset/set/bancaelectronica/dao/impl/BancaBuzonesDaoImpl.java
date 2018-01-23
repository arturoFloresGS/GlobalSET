package com.webset.set.bancaelectronica.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.bancaelectronica.dao.BancaBuzonesDao;
import com.webset.set.bancaelectronica.dto.BSaldoBancoDTO;
import com.webset.set.bancaelectronica.dto.EmpresaDto;
import com.webset.set.bancaelectronica.dto.FolioDto;
import com.webset.set.bancaelectronica.dto.MovtoBancaEDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.utils.tools.StoredProcedure;
import com.webset.utils.tools.Utilerias;

public class BancaBuzonesDaoImpl implements BancaBuzonesDao{

	JdbcTemplate jdbcTemplate;
	ConsultasGenerales consultasGenerales;
	private Bitacora bitacora = new Bitacora();
	private GlobalSingleton globalSingleton;
	private Funciones funciones = new Funciones(); 
	
	/**********************************************************************/
	public void setDataSource(DataSource dataSource){
		try
		{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:BancaBuzonesDaoImpl, M:setDataSource");
		}
	}
	/*************************************************************************/

	public String consultarConfiguraSet(int indice){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
	}
	
	public List<String>armaCadenaConexion(){
		List<String> listDatos = new ArrayList<String>(); 
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("select * from Configura_Set where Indice In("+ConstantesSet.USUARIO_SERVIDOR+","+
		ConstantesSet.PASSWORD_SERVIDOR+","+ConstantesSet.IP_CARPETA_DESTINO+","+ConstantesSet.CARPETA_RAIZ+")");
			 listDatos= jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
					public String  mapRow(ResultSet rs, int idx) throws SQLException {
						return rs.getString("VALOR");
					}});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:armaCadenaConexion");
		}
		return listDatos;
	}
	@Override
	public List<EmpresaDto> buscarEmpresa(String chequera, int idBanco, boolean swiftMT940_MN) {
		
		List<EmpresaDto> listEmpresas = new ArrayList<EmpresaDto>(); 
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append("\n Select no_empresa,id_banco,id_chequera " );
			sql.append("\n from cat_cta_banco ");

			if(swiftMT940_MN){
				sql.append("\n Where id_chequera like '%" + Utilerias.validarCadenaSQL(chequera) + "' ");
			}else{
				sql.append("\n Where id_chequera = '" + Utilerias.validarCadenaSQL(chequera) + "' ");
			}
				    
			if(idBanco != 0){
				sql.append("\n And id_banco = " + idBanco );
			}
			
			listEmpresas = jdbcTemplate.query(sql.toString(), new RowMapper<EmpresaDto>(){
					public EmpresaDto mapRow(ResultSet rs, int idx) throws SQLException {
						EmpresaDto empresa = new EmpresaDto();
						empresa.setNoEmpresa(rs.getInt("no_empresa"));
						empresa.setIdBanco(rs.getInt("id_banco"));
						empresa.setIdChequera(rs.getString("id_chequera"));
						return empresa;
					}});
			
			/*if(listEmpresas.size() > 0){
				return listEmpresas.get(0);
			}*/
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:EnvioTransferenciasDao, M:buscarEmpresa");
		}
		
		return listEmpresas;
	}
	@Override
	public Map<String, Object> selectMD(String folioBanco, String concepto, String referencia, int noEmpresa, String chequera,
			String fecha, double importe, String descripcion) {
		
		StringBuffer sql = new StringBuffer();
		Map<String, Object> mapa = new HashMap<String, Object>();
		List<MovtoBancaEDto> listMovimientos = new ArrayList<>();
		
		mapa.put("estatus", "");
		mapa.put("listaMovto", null);
		
		try {
			sql.append("\n SELECT movto_arch from movto_banca_diario " );
			sql.append("\n WHERE folio_banco = '" + Utilerias.validarCadenaSQL(folioBanco) + "' ");
			sql.append("\n And concepto = '" + Utilerias.validarCadenaSQL(concepto) + "' ");
			sql.append("\n And movto_arch = 1 And no_empresa = '" + noEmpresa + "' ");
			sql.append("\n And id_chequera = '" + Utilerias.validarCadenaSQL(chequera) + "' ");
			sql.append("\n And fec_valor = convert(datetime,'" + Utilerias.validarCadenaSQL(fecha) + "',103) ");
			sql.append("\n And importe = " + importe + " ");
			
			//System.out.println("Query selectMD: \n " + sql.toString());
			
			listMovimientos = jdbcTemplate.query(sql.toString(), new RowMapper<MovtoBancaEDto>(){
					public MovtoBancaEDto mapRow(ResultSet rs, int idx) throws SQLException {
						MovtoBancaEDto movto = new MovtoBancaEDto();
						movto.setMovtoArch(rs.getInt("movto_arch"));
						return movto;
					}});
			
			mapa.put("listaMovto", listMovimientos);
		
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:selectMD");
			mapa.put("estatus", "Error de conexión");
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:selectMD");
			
			mapa.put("estatus", "Error al ejecutar SQL");
		}
		
		return mapa;
	}
	@Override
	public Map<String, Object> selectMovtoBancaE(String folioBanco, String concepto, String referencia, int noEmpresa, String chequera,
			String fecha, double importe, double saldo, long noLineaArchivo, String cargoAbono, int idBanco) {
		
		
		StringBuffer sql = new StringBuffer();
		Map<String, Object> mapa = new HashMap<String, Object>();
		List<MovtoBancaEDto> listMovimientos = new ArrayList<>();
		
		mapa.put("estatus", "");
		mapa.put("listaMovto", null);
		
		try {
			sql.append("\n Select movto_arch, secuencia, archivo " );
			sql.append("\n from movto_banca_e ");
			sql.append("\n where movto_arch = 1 And no_empresa = " + noEmpresa );
			
			if(concepto.equals("MISMO BANCO BANCOMER")){
				if(folioBanco.substring(0,1).equals("'")){ //1,1
					sql.append("\n and folio_banco in (" + folioBanco + ") and (archivo like 'md%' or archivo like 'SH%') ");
				}else{
					sql.append("\n and folio_banco = '" + Utilerias.validarCadenaSQL(folioBanco) + "' and (archivo like 'md%' or archivo like 'SH%') ");
				}
			}
			
			if(!concepto.equals("MISMO BANCO BANCOMER") && !concepto.equals("MISMO BANCO BANCOMER2") && idBanco == 12){
				sql.append("\n and concepto = '" + Utilerias.validarCadenaSQL(concepto) + "' AND referencia = '" + Utilerias.validarCadenaSQL(referencia) + "' ");
			}else if(concepto.equals("MISMO BANCO BANCOMER2")){
				sql.append("\n And concepto = '" +Utilerias.validarCadenaSQL(concepto) + "' and (archivo like 'md%' or archivo like 'SH%') ");
			}else{
				if(folioBanco.substring(0,1).equals("'")){ //1,1
					sql.append("\n and folio_banco in (" + folioBanco + ") ");
				}else{
					sql.append("\n and folio_banco = '" + Utilerias.validarCadenaSQL(folioBanco) + "' ");
				}
				
				sql.append("\n And referencia = '" + Utilerias.validarCadenaSQL(referencia) + "' ");
			}
			
			sql.append("\n And id_chequera = '" + Utilerias.validarCadenaSQL(chequera) + "' ");
			//checar la comparacion de fecha en formato dd/mm/yyyy
			sql.append("\n And fec_valor = '" + Utilerias.validarCadenaSQL(fecha) + "' ");
			sql.append("\n And importe = " + importe + " ");
			
		    if(saldo != 0){
		    	sql.append("\n AND saldo_bancario = "+saldo+" ");
		    }
		    
		    if(noLineaArchivo > 0 && !concepto.equals("MISMO BANCO BANCOMER") && !concepto.equals("MISMO BANCO BANCOMER2")){
		    	sql.append("\n AND no_linea_arch = " + noLineaArchivo +" ");
		    }
		    
		    if(!cargoAbono.equals("")){
		    	sql.append("\n  AND cargo_abono = '" + Utilerias.validarCadenaSQL(cargoAbono) + "' ");
		    }
		    
		    sql.append("\n Union all ");
		    sql.append("\n  Select movto_arch, secuencia, archivo from hist_movto_banca_e ");
		    sql.append("\n  Where movto_arch = 1 And no_empresa = " + noEmpresa +" ");
		    
		    if(concepto.equals("MISMO BANCO BANCOMER")){
				if(folioBanco.substring(0,1).equals("'")){ //1,1
					sql.append("\n and folio_banco in (" + folioBanco + ") and (archivo like 'md%' or archivo like 'SH%') ");
				}else{
					sql.append("\n and folio_banco = '" + Utilerias.validarCadenaSQL(folioBanco) + "' and (archivo like 'md%' or archivo like 'SH%') ");
				}
			}
		     
		    if(!concepto.equals("MISMO BANCO BANCOMER") && !concepto.equals("MISMO BANCO BANCOMER2") && idBanco == 12){
				sql.append("\n and concepto = '" + Utilerias.validarCadenaSQL(concepto) + "' AND referencia = '" + Utilerias.validarCadenaSQL(referencia) + "' ");
			}else if(concepto.equals("MISMO BANCO BANCOMER2")){
				sql.append("\n And concepto = '" + Utilerias.validarCadenaSQL(concepto) + "' and (archivo like 'md%' or archivo like 'SH%') ");
			}else{
				if(folioBanco.substring(0,1).equals("'")){ //1,1
					sql.append("\n and folio_banco in (" + folioBanco + ") ");
				}else{
					sql.append("\n and folio_banco = '" + Utilerias.validarCadenaSQL(folioBanco) + "' ");
				}
				
				sql.append("\n And referencia = '" + Utilerias.validarCadenaSQL(referencia) + "' ");
			}
		    
		    sql.append("\n And id_chequera = '" + Utilerias.validarCadenaSQL(chequera) + "' ");
			//checar la comparacion de fecha en formato dd/mm/yyyy
			sql.append("\n And fec_valor = '" + Utilerias.validarCadenaSQL(fecha) + "' ");
			sql.append("\n And importe = " + importe + " ");
			
			if(saldo != 0){
		    	sql.append("\n AND saldo_bancario = "+saldo+" ");
		    }
		    
		    if(noLineaArchivo > 0 && !concepto.equals("MISMO BANCO BANCOMER") && !concepto.equals("MISMO BANCO BANCOMER2")){
		    	sql.append("\n AND no_linea_arch = " + noLineaArchivo +" ");
		    }
		    
		    if(!cargoAbono.equals("")){
		    	sql.append("\n  AND cargo_abono = '" + Utilerias.validarCadenaSQL(cargoAbono) + "' ");
		    }
    
		    sql.append("\n Order by archivo ");
		    
		    //System.out.println("Query selectMovtoBancaE: \n " + sql.toString());
		    
			listMovimientos = jdbcTemplate.query(sql.toString(), new RowMapper<MovtoBancaEDto>(){
					public MovtoBancaEDto mapRow(ResultSet rs, int idx) throws SQLException {
						MovtoBancaEDto movto = new MovtoBancaEDto();
						movto.setMovtoArch(rs.getInt("movto_arch"));
						movto.setSecuencia(rs.getInt("secuencia"));
						movto.setArchivo(rs.getString("archivo"));
						return movto;
					}});
			
			mapa.put("listaMovto", listMovimientos);
		
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:selectMovtoBancaE");
			mapa.put("estatus", "Error de conexión");
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:selectMovtoBancaE");
			
			mapa.put("estatus", "Error al ejecutar SQL");
		}
		
		return mapa;
	}
	
	@Override
	public Map<String, Object> updateFolioReal(String idFolio) {
		
		StringBuffer sql = new StringBuffer();
		Map<String, Object> mapa = new HashMap<String, Object>();
		
		mapa.put("estatus", "");
		mapa.put("afec", 0);
		int afec = 0;
		
		try {
			sql.append("\n UPDATE folio " );
			sql.append("\n SET num_folio = num_folio + 1 ");
			sql.append("\n WHERE tipo_folio = '" + Utilerias.validarCadenaSQL(idFolio) + "' ");
				    
		    //System.out.println("Query update folioReal: \n " + sql.toString());
		    
		    afec = jdbcTemplate.update(sql.toString()); 
			
			mapa.put("afec", afec);
		
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:updateFolioReal");
			mapa.put("estatus", "Error de conexión");
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:updateFolioReal");
			mapa.put("estatus", "Error al ejecutar SQL");
		}
		
		return mapa;
	}
	
	@Override
	public Map<String, Object> selectFolioReal(String idFolio) {
		
		StringBuffer sql = new StringBuffer();
		Map<String, Object> mapa = new HashMap<String, Object>();
		List<FolioDto> listFolios = new ArrayList<>();
		
		mapa.put("estatus", "");
		mapa.put("folio", "");
		
		try {
			sql.append("\n SELECT num_folio " );
			sql.append("\n FROM folio ");
			sql.append("\n WHERE tipo_folio = '" + Utilerias.validarCadenaSQL(idFolio) + "' ");
				    
		    //System.out.println("Query selectFolioReal: \n " + sql.toString());
			
		    listFolios = jdbcTemplate.query(sql.toString(), new RowMapper<FolioDto>(){
				public FolioDto mapRow(ResultSet rs, int idx) throws SQLException {
					FolioDto folio = new FolioDto();
					folio.setNumero(rs.getLong("num_folio"));
					return folio;
				}});
		
		    if(listFolios.size() > 0){
		    	mapa.put("folio", listFolios.get(0).getNumero());
		    }
				    
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:selectFolioReal");
			mapa.put("estatus", "Error de conexión");
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:selectFolioReal");
			mapa.put("estatus", "Error al ejecutar SQL");
		}
		
		return mapa;
	}
	@Override
	public Map<String, Object> insertaBancaMD(int noEmpresa, int idBanco, String chequera, long folio, String fecha,
			String sucursal, String folioBanco, String referencia, String cargoAbono, double importe, String sbCobro,
			String concepto, int idUsuario, String descripcion, String archivo, String fechaHoy,
			String observacion, String cveConcepto, double saldo, String estatus, int noLineaArchivo) {
		
		StringBuffer sql = new StringBuffer();
		Map<String, Object> mapa = new HashMap<String, Object>();
		
		mapa.put("estatus", "");
		mapa.put("afec", 0);
		int afec = 0;
		
		try {
			
			if(estatus.equals("")){
				estatus = "N";
			}
			
			sql.append("\n insert into movto_banca_diario( " );
			sql.append("\n no_empresa,id_banco,id_chequera,secuencia,fec_valor, ");
			sql.append("\n sucursal,folio_banco,referencia,cargo_abono,importe,b_salvo_buen_cobro, ");
			sql.append("\n concepto,id_estatus_trasp,fec_alta,b_trasp_banco,b_trasp_conta,movto_arch, ");
			sql.append("\n usuario_alta,descripcion,archivo ");
			
			if(!observacion.equals("")){
				sql.append("\n ,observacion ");
			}
			if(!cveConcepto.equals("")){
				sql.append("\n ,id_cve_concepto ");
			}
			if(saldo != 0){
				sql.append("\n ,saldo_bancario ");
			}
			if(noLineaArchivo > 0){
				sql.append("\n ,no_linea_arch ");
			}
			
			sql.append("\n ,estatus)");
			sql.append("\n values(" + Utilerias.validarCadenaSQL(noEmpresa) +" ");
			sql.append("\n , " + Utilerias.validarCadenaSQL(idBanco) 
						+ ",'" + Utilerias.validarCadenaSQL(chequera) 
						+ "'," + Utilerias.validarCadenaSQL(folio) 
						+ ",'" + Utilerias.validarCadenaSQL(fecha) + "' ");
			sql.append("\n , '" + Utilerias.validarCadenaSQL(sucursal) 
						+ "','" + Utilerias.validarCadenaSQL(folioBanco)+ "' ");
			sql.append("\n , '" + Utilerias.validarCadenaSQL(referencia) 
						+ "','" + Utilerias.validarCadenaSQL(cargoAbono) + "' ");
			sql.append("\n , "+ Utilerias.validarCadenaSQL(importe) +" ");
			sql.append("\n , '" + Utilerias.validarCadenaSQL(sbCobro) 
						+ "','" + Utilerias.validarCadenaSQL(concepto) + "',NULL ");
			sql.append("\n , '" + Utilerias.validarCadenaSQL(fechaHoy)
						+ "','N','N',1," + Utilerias.validarCadenaSQL(idUsuario) + " ");
			sql.append("\n , '" + Utilerias.validarCadenaSQL(descripcion) 
						+ "','" + Utilerias.validarCadenaSQL(archivo)+ "' ");
			
			if(!observacion.equals("")){
				sql.append("\n ,'" + Utilerias.validarCadenaSQL(observacion) + "' ");
			}
			if(!cveConcepto.equals("")){
				sql.append("\n ,'" + Utilerias.validarCadenaSQL(cveConcepto) + "' ");
			}
			if(saldo != 0){
				sql.append("\n , " + saldo + " ");
			}
			if(noLineaArchivo > 0){
				sql.append("\n , " + noLineaArchivo + " ");
			}
			
			sql.append("\n ,'B') ");
			    
		    //System.out.println("Query insert movto_banca_diario: \n " + sql.toString());
			
		   afec = jdbcTemplate.update(sql.toString());
		   mapa.put("afec", afec);
		   
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:insertaBancaMD");
			mapa.put("estatus", "Error de conexiï¿½n");
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:insertaBancaMD");
			mapa.put("estatus", "Error al ejecutar SQL");
		}
		
		return mapa;
		
	}
	@Override
	public Map<String, Object> insertaMovtoBancaE(int noEmpresa, int idBanco, String chequera, long folio, String fecha,
			String sucursal, String folioBanco, String referencia, String cargoAbono, double importe, String sbCobro,
			String concepto, int idUsuario, String descripcion, String archivo, String fechaHoy,
			String observacion, String cveConcepto, double saldo, String estatus, int noLineaArchivo) {
		
		StringBuffer sql = new StringBuffer();
		Map<String, Object> mapa = new HashMap<String, Object>();
		
		mapa.put("estatus", "");
		mapa.put("afec", 0);
		int afec = 0;
		
		try {
			
			if(estatus.equals("")){
				estatus = "N";
			}
			
			sql.append("\n insert into movto_banca_e( " );
			sql.append("\n no_empresa,id_banco,id_chequera,secuencia,fec_valor, ");
			sql.append("\n sucursal,folio_banco,referencia,cargo_abono,importe,b_salvo_buen_cobro, ");
			sql.append("\n concepto,id_estatus_trasp,fec_alta,b_trasp_banco,b_trasp_conta,movto_arch, ");
			sql.append("\n usuario_alta,descripcion,archivo ");
			
			if(!observacion.equals("")){
				sql.append("\n ,observacion ");
			}
			if(!cveConcepto.equals("")){
				sql.append("\n ,id_cve_concepto ");
			}
			if(saldo != 0){
				sql.append("\n ,saldo_bancario ");
			}
			if(noLineaArchivo > 0){
				sql.append("\n ,no_linea_arch ");
			}
			
			sql.append("\n ,estatus )");
			
			sql.append("\n values(" + noEmpresa +" ");
			sql.append("\n , " + idBanco + ",'" + Utilerias.validarCadenaSQL(chequera) 
							+ "'," + Utilerias.validarCadenaSQL(folio) 
							+ ",'" + Utilerias.validarCadenaSQL(fecha) + "' ");
			sql.append("\n , '" + Utilerias.validarCadenaSQL(sucursal) 
							+ "','" + Utilerias.validarCadenaSQL(folioBanco)+ "' ");
			sql.append("\n , '" + Utilerias.validarCadenaSQL(referencia) 
							+ "','" + Utilerias.validarCadenaSQL(cargoAbono) + "' ");
			sql.append("\n , "+ importe +" ");
			sql.append("\n , '" + Utilerias.validarCadenaSQL(sbCobro) 
							+ "','" + Utilerias.validarCadenaSQL(concepto) 
							+ "','" +Utilerias.validarCadenaSQL(estatus)  + "' ");
			sql.append("\n , '" + Utilerias.validarCadenaSQL(fechaHoy)
							+ "','N','N',1," + idUsuario + " ");
			sql.append("\n , '" + Utilerias.validarCadenaSQL(descripcion) 
							+ "','" + Utilerias.validarCadenaSQL(archivo)+ "' ");
			
			if(!observacion.equals("")){
				sql.append("\n ,'" + Utilerias.validarCadenaSQL(observacion) + "' ");
			}
			if(!cveConcepto.equals("")){
				sql.append("\n ,'" + Utilerias.validarCadenaSQL(cveConcepto) + "' ");
			}
			if(saldo != 0){
				sql.append("\n , " + saldo + " ");
			}
			if(noLineaArchivo > 0){
				sql.append("\n , " + noLineaArchivo + " ");
			}
			
			sql.append("\n ,'S') ");
				    
		    //System.out.println("Query insert movto_banca_e: \n " + sql.toString());
			
		   afec = jdbcTemplate.update(sql.toString());
		   mapa.put("afec", afec);
				    
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:insertMovtoBancaE");
			mapa.put("estatus", "Error de conexiï¿½n");
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:insertMovtoBancaE");
			mapa.put("estatus", "Error al ejecutar SQL");
		}
		
		return mapa;
		
	}
	@Override
	public Map<String, Object> updateCatCtaBanco(String fecha, String archivos, int idBanco) {
		
		StringBuffer sql = new StringBuffer();
		Map<String, Object> mapa = new HashMap<String, Object>();
		
		mapa.put("estatus", "");
		mapa.put("afec", 0);
		int afec = 0;
		
		try {
			
			sql.append("\n UPDATE cat_cta_banco " );
			
			if(fecha.equals("")){
				sql.append("\n SET fecha_banca = 'GetDate()' ");
			}else{
				sql.append("\n SET fecha_banca = '" + Utilerias.validarCadenaSQL(fecha) + "' "); 
			}
			
			sql.append("\n WHERE ");
			sql.append("\n id_chequera in ");
			sql.append("\n 			   (select distinct id_chequera from movto_banca_e  ");
			sql.append("\n             where  archivo in(" + archivos + ") )");
			sql.append("\n and id_banco = " + idBanco + " ");
				        
		    //System.out.println("Query update updateCatCtaBanco: \n " + sql.toString());
			
		   afec = jdbcTemplate.update(sql.toString());
		   mapa.put("afec", afec);
				    
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:updateCatCtaBanco");
			mapa.put("estatus", "Error de conexiï¿½n");
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:updateCatCtaBanco");
			mapa.put("estatus", "Error al ejecutar SQL");
		}
		
		return mapa;
		
	}
	
	
	@Override
	public Map<String, Object> ejecutaConfOperaciones() {
		globalSingleton = GlobalSingleton.getInstancia();
		Map<String, Object> resul = new HashMap<>();
		//HashMap< Object, Object > inParams = new HashMap< Object, Object >();
		
		resul.put("estatus", "");
		
		try {
			
			StoredProcedure stored = new StoredProcedure(jdbcTemplate, "sp_ConfOperacionesAS400");
			//stored.declareParameter("usuario", globalSingleton.getUsuarioLoginDto().getIdUsuario(), StoredProcedure.IN);
			//stored.declareParameter("V_result", Types.INTEGER, StoredProcedure.OUT);
			
			resul = stored.execute();
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:ejecutaConfOperaciones");
			resul.put("estatus", "Error de conexiï¿½n");
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
			"P:BancaElectronica, C:BancaBuzonesDaoImpl, M:ejecutaConfOperaciones");
		}
		return resul;
	}
	
	
	public void beginTran(){
		jdbcTemplate.update("BEGIN TRAN");
	}
	
	public void rollback(){
		jdbcTemplate.update("ROLLBACK");
	}
	
	public void commit(){
		jdbcTemplate.update("COMMIT TRAN");
	}
	
	@Override
	public double llamaProcConfirmaTrans() {
		
		//globalSingleton = GlobalSingleton.getInstancia();
		Map<String, Object> resul = new HashMap<>();
		//HashMap< Object, Object > inParams = new HashMap< Object, Object >();
		resul.put("estatus", "");
		
		try {

			StoredProcedure stored = new StoredProcedure(jdbcTemplate, "sp_llamaConfirmador");
			//parametros de entrada: null.
			//parametros de salida: result, mensaje.
			stored.declareParameter("result", Types.INTEGER, StoredProcedure.OUT);
			stored.declareParameter("mensaje", Types.VARCHAR, StoredProcedure.OUT);
			
			resul = stored.execute();
			
			//System.out.println("Resultado: " + resul.get("result"));
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:llamaProcConfirmaTrans");
			resul.put("estatus", "Error de conexiï¿½n");
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
			"P:BancaElectronica, C:BancaBuzonesDaoImpl, M:llamaProcConfirmaTrans");
			resul.put("estatus", "Error de ejecucion store");
		}
		return (int) resul.get("result");
	}
	
	@Override
	public String selectControlStored(String nombreStored, int idStored) {
		
		StringBuffer sql = new StringBuffer();
		List<MovtoBancaEDto> movtos = new ArrayList<>();
		
		String result = "";
		
		try {
			sql.append("\n SELECT estatus from control_stored " );
			sql.append("\n where id_stored=" + idStored + " ");
			sql.append("\n and desc_stored='" + Utilerias.validarCadenaSQL(nombreStored) + "' ");
				   
		    //System.out.println("Query selectFolioReal: \n " + sql.toString());
			
		    movtos = jdbcTemplate.query(sql.toString(), new RowMapper<MovtoBancaEDto>(){
				public MovtoBancaEDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovtoBancaEDto e = new MovtoBancaEDto();
					e.setEstatusExp(rs.getString("estatus"));
					return e;
				}});
		
		   result = movtos.get(0).getEstatusExp();
				    
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:selectControlStored");
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:selectControlStored");
		}
		
		return result;		
	}
	
	@Override
	public Map<String, Object> ejecutaClasificadorMovtos(String fecha) {
		
		Map<String, Object> resul = new HashMap<>();
		//HashMap< Object, Object > inParams = new HashMap< Object, Object >();
		resul.put("estatus", "");
		
		try {
			StoredProcedure stored = new StoredProcedure(jdbcTemplate, "SP_CLASIFICADORMOVTOS");
			
			stored.declareParameter("V_fecha", fecha, StoredProcedure.IN);
			resul = stored.execute();
			
		}catch(CannotGetJdbcConnectionException e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:ejecutaClasificadorMovtos");
			resul.put("estatus", "Error de conexiï¿½n");
		}catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
			"P:BancaElectronica, C:BancaBuzonesDaoImpl, M:ejecutaClasificadorMovtos");
			resul.put("estatus", "Error de ejecucion store");
		}
		
		return resul;
	}
	
	@Override
	public Map<String, Object>  ejecutaBuzonesMovimiento() {
		
		Map<String, Object> resul = new HashMap<>();
		resul.put("estatus", "");
		
		try {
			StoredProcedure stored = new StoredProcedure(jdbcTemplate, "SP_BUZONES_MOVIMIENTO");
			//stored.declareParameter("V_result", Types.INTEGER, StoredProcedure.OUT);
			resul = stored.execute();
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:ejecutaBuszonesMovimiento");
			resul.put("estatus", "Error de conexiï¿½n");
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
			"P:BancaElectronica, C:BancaBuzonesDaoImpl, M:ejecutaBuzonesMovimiento");
			resul.put("estatus", "Error de ejecucion store");
		}
		return resul;
	}
	
	@Override
	public Map<String, Object> ejecutaConciliaSbcDev() {
		
		Map<String, Object> resul = new HashMap<>();
		resul.put("estatus", "");
		
		try {
			StoredProcedure stored = new StoredProcedure(jdbcTemplate, "sp_concilia_sbc_dev");
			resul = stored.execute();
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:ejecutaConciliaSbcDev");
			resul.put("estatus", "Error de conexiï¿½n");
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
			"P:BancaElectronica, C:BancaBuzonesDaoImpl, M:ejecutaConciliaSbcDev");
			resul.put("estatus", "Error de ejecucion store");
		}
		return resul;
	}
	
	@Override
	public Map<String, Object> ejecutaDatosDevolucionCheque() {
		
		Map<String, Object> resul = new HashMap<>();
		resul.put("estatus", "");
		
		try {
			StoredProcedure stored = new StoredProcedure(jdbcTemplate, "sp_Datos_Devolucion_Cheque");
			resul = stored.execute();
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:ejecutaDatosDevolucionCheque");
			resul.put("estatus", "Error de conexiï¿½n");
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
			"P:BancaElectronica, C:BancaBuzonesDaoImpl, M:ejecutaDatosDevolucionCheque");
			resul.put("estatus", "Error de ejecucion store");
		}
		return resul;
	}
	
	@Override
	public Map<String, Object> ejecutaFactorajeBancomer() {
		
		Map<String, Object> resul = new HashMap<>();
		resul.put("estatus", "");
		
		try {
			StoredProcedure stored = new StoredProcedure(jdbcTemplate, "sp_Factoraje_Bancomer");
			resul = stored.execute();
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:ejecutaFactorajeBancomer");
			resul.put("estatus", "Error de conexiï¿½n");
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
			"P:BancaElectronica, C:BancaBuzonesDaoImpl, M:ejecutaFactorajeBancomer");
			resul.put("estatus", "Error de ejecucion store");
		}
		return resul;
	}
	@Override
	public Map<String, Object> ejecutaStoreSPC(int idUsuario) {
		
		Map<String, Object> resul = new HashMap<>();
		//HashMap< Object, Object > inParams = new HashMap< Object, Object >();
		resul.put("estatus", "");
		
		try {
			StoredProcedure stored = new StoredProcedure(jdbcTemplate, "SP_CONCILIA_MOVTO_BANCO");
			stored.declareParameter("V_usuario", idUsuario, StoredProcedure.IN);
			resul = stored.execute();
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:ejecutaStoreSPC");
			resul.put("estatus", "Error de conexiï¿½n");
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
			"P:BancaElectronica, C:BancaBuzonesDaoImpl, M:ejecutaStoreSPC");
			resul.put("estatus", "Error de ejecucion store");
		}
		
		return resul;
		
	}
	@Override
	public Map<String, Object> verificaSaldoBanco(int idBanco) {
		
		StringBuffer sql = new StringBuffer();
		Map<String, Object> mapa = new HashMap<String, Object>();
		List<BSaldoBancoDTO> listSaldoBanco = new ArrayList<>();
		
		mapa.put("estatus", "");
		mapa.put("saldo", "");
		
		try {
			sql.append("\n SELECT id_banco, b_saldo_banco " );
			sql.append("\n FROM cat_banco ");
			sql.append("\n WHERE b_banca_elect in ('I','A') ");
			sql.append("\n and id_banco in("+idBanco+") ");
			sql.append("\n ORDER BY id_banco ");
			
		    //System.out.println("Query verificaSaldoBanco: \n " + sql.toString());
		            
		    listSaldoBanco = jdbcTemplate.query(sql.toString(), new RowMapper<BSaldoBancoDTO>(){
				public BSaldoBancoDTO mapRow(ResultSet rs, int idx) throws SQLException {
					BSaldoBancoDTO banco = new BSaldoBancoDTO();
					banco.setIdBanco(rs.getInt("id_banco"));
					banco.setbSaldoBanco(rs.getString("b_saldo_banco"));
					return banco;
				}});
		
		    if(listSaldoBanco.size() > 0){
		    	mapa.put("saldo", listSaldoBanco.get(0).getbSaldoBanco());
		    }
				    
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:verificaSaloBanco");
			mapa.put("estatus", "Error de conexiï¿½n");
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:verificaSaldoBanco");
			mapa.put("estatus", "Error al ejecutar SQL");
		}
		
		return mapa;
	}
	
	/*	SE SUSTITUYE POR INSERTBATCH().
	 * 
	 * @Override
	public String insertaMovtoBancaEStr(int noEmpresa, int idBanco, String chequera, long folio, String fecha,
			String sucursal, String folioBanco, String referencia, String cargoAbono, double importe, String sbCobro,
			String concepto, int idUsuario, String descripcion, String archivo, String fechaHoy, String observacion,
			String cveConcepto, double saldo, String estatus, int noLineaArchivo, String hora, String fechaOperacion,
			String estatus1) {
		
		
		StringBuffer sql = new StringBuffer();
		
		try {
			
			if(estatus.equals(""))
				estatus = "N";
			
			sql.append("\n insert into movto_banca_e( " );
			sql.append("\n no_empresa,id_banco,id_chequera,secuencia,fec_valor, " );
			sql.append("\n sucursal,folio_banco,referencia,cargo_abono,importe,b_salvo_buen_cobro, " );
			sql.append("\n concepto,id_estatus_trasp,fec_alta,b_trasp_banco,b_trasp_conta,movto_arch," );
			sql.append("\n usuario_alta,descripcion,archivo" );
			
			if(!observacion.equals("")){
				sql.append("\n ,observacion" );
			}

			if(!cveConcepto.equals("")){
				sql.append("\n ,id_cve_concepto" );
			}
			
			if(saldo != 0){
				sql.append("\n ,saldo_bancario" );
			}
			
			if(noLineaArchivo > 0){
				sql.append("\n ,no_linea_arch " );
			}
			
			if(fechaOperacion.length() > 0){
				sql.append("\n ,fec_operacion " );
			}
			
			if(!estatus.equals("")){
				sql.append("\n ,estatus " );
			}
			
			sql.append("\n )" );
			sql.append("\n values(" +noEmpresa +" ");
			sql.append("\n ," + idBanco + ",'" + chequera + "'," + folio + ",'" + fecha + "' " );
			sql.append("\n ,'" + sucursal + "','" + folioBanco + "' " );
			sql.append("\n ,'" + referencia + "','" + cargoAbono + "' " );
			sql.append("\n ," + importe + " " );
			sql.append("\n ,'" + sbCobro + "','" + concepto + "','" + estatus + "' " );
			sql.append("\n ,'" + fechaHoy + "','N','N',1," + idUsuario + " " );
			sql.append("\n ,'" + descripcion + "','" + archivo + "' " );
			
			if(!observacion.equals("")){
				sql.append("\n ,'" + observacion + "' " );
			}

			if(!cveConcepto.equals("")){
				sql.append("\n ,'" + cveConcepto + "' " );
			}
			
			if(saldo != 0){
				sql.append("\n ," + saldo + " " );
			}
			
			if(noLineaArchivo > 0){
				sql.append("\n ," + noLineaArchivo + " " );
			}
			
			if(fechaOperacion.length() > 0){
				if(new Funciones().isDate(fechaOperacion, false)){
					sql.append("\n ,'" + fechaOperacion + "' " );
				}else{
					sql.append("\n ,'" + fecha + "' " ); //concatenar hs:mm:ss o poner sysdate.
				}
			}
			
			if(!estatus.equals("")){
				sql.append("\n ,'" + estatus + "' " );
			}
			
			sql.append("\n );" );
			
		    System.out.println("cadena movtoBancaEStr: \n " + sql.toString());

		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:selectMovtoBancaE");
		}
		
		return sql.toString();
	}
	*/
	
	@Override
	public Map<String, Object> ejecutaInsertsMovtoBancaE(List<MovtoBancaEDto> lMovimientos) {
		
		int afec = 0; 
		Map<String,Object> mapa = new HashMap<>();
		
		try {
			
			//afec = jdbcTemplate.update(inserts.toString());
			afec = insertBatch(lMovimientos); //Este metodo insertara todos los movimientos pasados en 
			
			mapa.put("afec", afec);
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:selectMovtoBancaE");
			mapa.put("estatus", "Error de conexiï¿½n");
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:selectMovtoBancaE");
			mapa.put("estatus", "Error al ejecutar SQL");
		}
		
		return mapa;
		
	}
	
	public int insertBatch(final List<MovtoBancaEDto> listMovtos){
		
		StringBuffer sql = new StringBuffer();  
				
		sql.append("\n insert into movto_banca_e( " );
		sql.append("\n no_empresa,id_banco,id_chequera,secuencia,fec_valor, " );
		sql.append("\n sucursal,folio_banco,referencia,cargo_abono,importe,b_salvo_buen_cobro, " );
		sql.append("\n concepto,id_estatus_trasp,fec_alta,b_trasp_banco,b_trasp_conta,movto_arch," );
		sql.append("\n usuario_alta,descripcion,archivo" );
		
		sql.append("\n ,observacion" );
		sql.append("\n ,id_cve_concepto" );
		sql.append("\n ,saldo_bancario" );	
		sql.append("\n ,no_linea_arch " );
		sql.append("\n ,fec_operacion " );
		sql.append("\n ,estatus " );
		sql.append("\n ) " );
		
		sql.append("\n values(? "); //noEmpresa = 1
		sql.append("\n ,?,?,?,? " ); //banco = 2, id_chequera = 3, secuencia = 4, fec_valor = 5
		sql.append("\n ,?,? " ); //sucursal = 6, folio_banco = 7
		sql.append("\n ,?,? " ); //referencia = 8, cargo_abono = 9
		sql.append("\n ,? " );	//importe = 10
		sql.append("\n ,?,?,? " ); //sbCobro = 11, concepto = 12, estatus = 13
		sql.append("\n ,?,?,?,?,? " ); //fecha_alta = 14, b_trans_banco = 15, b_transp_conta = 16, movto_arch = 17, usuario_alta = 18
		sql.append("\n ,?,? " ); //descripcion = 19, archivo = 20
		
		sql.append("\n ,? " ); //observacion = 21
		sql.append("\n ,? " ); //cveConcepto = 22
		sql.append("\n ,? " ); //Saldo = 23
		
		sql.append("\n ,? " ); //noLineaArchivo = 24
		sql.append("\n ,? " ); //fechaOperacion = 25
		sql.append("\n ,? " ); //Estatus  = 26
		sql.append("\n )" );
		
		int resp[];
		
		resp = jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				
				MovtoBancaEDto movto = listMovtos.get(i);
				ps.setInt(1, movto.getNoEmpresa());
				ps.setInt(2, movto.getIdBanco());
				ps.setString(3, movto.getIdChequera());
				ps.setLong(4, movto.getSecuencia());
			    ps.setString(5, funciones.ponerFechaSola(movto.getFecValor())); //checar la fecha.    
				ps.setString(6,movto.getSucursal());
				ps.setString(7,movto.getFolioBanco());
				ps.setString(8,movto.getReferencia());
				ps.setString(9,movto.getCargoAbono());
				ps.setDouble(10,movto.getImporte());
				ps.setString(11,movto.getBSalvoBuenCobro());
				ps.setString(12,movto.getConcepto());
				ps.setString(13,movto.getIdEstatusTrasp());
				ps.setString(14, movto.getFechaAlta());
				ps.setString(15,"N");
				ps.setString(16,"N");
				ps.setInt(17,1);
				ps.setInt(18,movto.getUsuarioAlta());
				ps.setString(19,movto.getDescripcion());
				ps.setString(20,movto.getArchivo());
				ps.setString(21,movto.getObservacion());
				ps.setString(22,movto.getIdCveConcepto());
				ps.setDouble(23,movto.getSaldoBancario());
				ps.setInt(24,movto.getNoLineaArch());
				ps.setString(25,movto.getFechaOperacion());
				ps.setString(26,movto.getEstatus());
				
			}
			
			@Override
			public int getBatchSize() {
				return listMovtos.size();
				//return 0;
			}
		});
		
		//Obtiene el total de afectados
		int afec = 0;
		for(int i=0; i<resp.length; i++){
			afec += resp[i];
		}
		return afec;
	}
	
	@Override
	public Map<String, Object> ejecutaInsertsMovtoBancaD(List<MovtoBancaEDto> lMovimientos) {
		
		int afec = 0; 
		Map<String,Object> mapa = new HashMap<>();
		
		try {
			
			afec = insertBatchDiario(lMovimientos); //Este metodo insertara todos los movimientos pasados en la lista
			
			mapa.put("afec", afec);
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:selectMovtoBancaE");
			mapa.put("estatus", "Error de conexiï¿½n");
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:selectMovtoBancaE");
			mapa.put("estatus", "Error al ejecutar SQL");
		}
		
		return mapa;
		
	}
	
	public int insertBatchDiario(final List<MovtoBancaEDto> listMovtos){
		
		StringBuffer sql = new StringBuffer();  
				
		sql.append("\n insert into movto_banca_diario( " );
		sql.append("\n  no_empresa,id_banco,id_chequera,secuencia,fec_valor, ");
		
		sql.append("\n sucursal,folio_banco,referencia,cargo_abono,importe, " );
		sql.append("\n concepto,fec_alta,b_trasp_banco,b_trasp_conta,movto_arch," );
		sql.append("\n usuario_alta,descripcion,archivo" );
		sql.append("\n ,id_cve_concepto" );
		sql.append("\n ,saldo_bancario" );	
		sql.append("\n ,no_linea_arch " );
		sql.append("\n ,estatus " );
		sql.append("\n ) " );

		sql.append("\n values(? "); //noEmpresa = 1
		sql.append("\n ,?,?,?,? " ); //banco = 2, id_chequera = 3, secuencia = 4, fec_valor = 5
		sql.append("\n ,?,? " ); //sucursal = 6, folio_banco = 7
		sql.append("\n ,?,? " ); //referencia = 8, cargo_abono = 9
		sql.append("\n ,? " );	//importe = 10
		sql.append("\n ,?,?,? " ); //concepto = 11, //fecha_alta = 12, b_trans_banco = 13 
		sql.append("\n ,?,?,? " ); //b_transp_conta = 14, movto_arch = 15, usuario_alta = 16
		sql.append("\n ,?,? " ); //descripcion = 17, archivo = 18
		sql.append("\n ,? " ); //cveConcepto = 19
		sql.append("\n ,? " ); //Saldo = 20
		sql.append("\n ,? " ); //noLineaArchivo = 21
		sql.append("\n ,? " ); //Estatus  = 22
		sql.append("\n )" );
		
		int resp[];
		
		resp = jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				
				MovtoBancaEDto movto = listMovtos.get(i);
				ps.setInt(1, movto.getNoEmpresa());
				ps.setInt(2, movto.getIdBanco());
				ps.setString(3, movto.getIdChequera());
				ps.setLong(4, movto.getSecuencia());
			    ps.setString(5, funciones.ponerFechaSola(movto.getFecValor())); 
				ps.setString(6,movto.getSucursal());
				ps.setString(7,movto.getFolioBanco());
				ps.setString(8,movto.getReferencia());
				ps.setString(9,movto.getCargoAbono());
				ps.setDouble(10,movto.getImporte());
				ps.setString(11,movto.getConcepto());
				ps.setString(12,movto.getFechaAlta());
				ps.setString(13,"N");
				ps.setString(14,"N");
				ps.setInt(15,1);
				ps.setInt(16,movto.getUsuarioAlta());
				ps.setString(17,movto.getDescripcion());
				ps.setString(18,movto.getArchivo());
				ps.setString(19,movto.getIdCveConcepto());
				ps.setDouble(20,movto.getSaldoBancario());
				ps.setInt(21,movto.getNoLineaArch());
				ps.setString(22,movto.getEstatus());
				
			}
			
			@Override
			public int getBatchSize() {
				return listMovtos.size();
				//return 0;
			}
		});
		
		//Obtiene el total de afectados
		int afec = 0;
		for(int i=0; i<resp.length; i++){
			afec += resp[i];
		}
		return afec;
	}
	
	@Override
	public Map<String, Object> selectMovtosBancomer(String archivo, int idBanco, String chequera) {
		
		StringBuffer sql = new StringBuffer();
		Map<String,Object> mapa = new HashMap<>();
		List<MovtoBancaEDto> listaMovimientos = new ArrayList<>();
		
		mapa.put("estatus", "");
		mapa.put("lista", listaMovimientos);
		
		try{
			
			sql.append("\n SELECT secuencia, importe, cargo_abono ");		
			sql.append("\n FROM  movto_banca_e m ");
			sql.append("\n WHERE archivo = '" + Utilerias.validarCadenaSQL(archivo) + "' ");
			sql.append("\n and id_banco = " + idBanco + " ");
			sql.append("\n and id_chequera = '" + Utilerias.validarCadenaSQL(chequera) + "' ");
			sql.append("\n and cargo_abono in('I','E') ");
			sql.append("\n ORDER BY secuencia DESC ");
			        
			
			 listaMovimientos = jdbcTemplate.query(sql.toString(), new RowMapper<MovtoBancaEDto>(){
					public MovtoBancaEDto mapRow(ResultSet rs, int idx) throws SQLException {
						MovtoBancaEDto movto = new MovtoBancaEDto();
						movto.setSecuencia(rs.getInt("secuencia"));
						movto.setImporte(rs.getDouble("importe"));
						movto.setCargoAbono(rs.getString("cargo_abono"));
						return movto;
					}});
			
			    if(listaMovimientos.size() > 0){
			    	mapa.put("lista", listaMovimientos);
			    }
			    
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:selectMovtoBancomer");
			mapa.put("estatus", "Error de conexiï¿½n");
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:selectMovtoBancomer");
			mapa.put("estatus", "Error al ejecutar SQL");
		}
		
		return mapa;
        
	}
	@Override
	public int updateSaldoBancomer(double saldoChequera, String archivo, int idBanco, String chequera, long secuencia) {
		
		int afec = 0;
		StringBuffer sql = new StringBuffer();
		
		try{
			
			sql.append("\n UPDATE  movto_banca_e ");		
			sql.append("\n SET saldo_bancario = " + saldoChequera + " ");
			sql.append("\n WHERE archivo = '" + Utilerias.validarCadenaSQL(archivo) + "' ");
			sql.append("\n and id_banco = " + idBanco + " ");
			sql.append("\n and id_chequera = '" + Utilerias.validarCadenaSQL(chequera) + "' ");
			sql.append("\n and cargo_abono in('I','E') ");
			sql.append("\n and secuencia = "+ secuencia +" ");
			        
			afec = jdbcTemplate.update(sql.toString());
			    
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:updateSaldoBancomer");
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:updateSaldoBancomer");
		}
		
		return afec;
		
	}
	@Override
	public int updateSaldosCatCtaBanco(double saldoChequera, int noEmpresa, int idBanco, String chequera,
			String fechaHoy) {
		
		int afec = 0;
		StringBuffer sql = new StringBuffer();
		
		try{
			
			sql.append("\n UPDATE cat_cta_banco ");		
			sql.append("\n SET saldo_banco_ini = " + saldoChequera + " ");
			sql.append("\n ,fecha_banca = '" + Utilerias.validarCadenaSQL(fechaHoy) + "' ");
			sql.append("\n WHERE no_empresa = " + noEmpresa + " ");
			sql.append("\n and id_banco = " + idBanco + " ");
			sql.append("\n and id_chequera = '" + Utilerias.validarCadenaSQL(chequera) + "' ");
			        
			afec = jdbcTemplate.update(sql.toString());
			    
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:updateSaldosCatCtaBanco");
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:updateSaldosCatCtaBanco");
		}
		return afec;
	}
	@Override
	public int insertaArchMovtoBancaE(String archivoOriginal, String chequera, String fechaHoy,
			int idBanco) {
		
		int afec = 0;
		StringBuffer sql = new StringBuffer();
		
		try{
			
			sql.append("\n INSERT INTO arch_movto_banca_e ");		
			sql.append("\n            (archivo, id_chequera, fec_alta, id_banco) ");
			sql.append("\n VALUES( ");
			sql.append("\n '" + Utilerias.validarCadenaSQL(archivoOriginal) + "', ");
			sql.append("\n '" + Utilerias.validarCadenaSQL(chequera) + "', ");
			sql.append("\n '" + Utilerias.validarCadenaSQL(fechaHoy) + "', ");
			sql.append("\n " + idBanco + ") ");
				    
			afec = jdbcTemplate.update(sql.toString());
			    
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:insertaArchMovtoBancaE");
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:insertaArchMovtoBancaE");
		}
		return afec;
				    
	}
	
	@Override
	public List<EmpresaDto> buscarEmpresaClabe(String clabe, int idBanco) {
		
		List<EmpresaDto> listEmpresas = new ArrayList<EmpresaDto>(); 
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append("\n Select no_empresa,id_banco,id_chequera " );
			sql.append("\n from cat_cta_banco ");
			sql.append("\n Where id_clabe = '" + Utilerias.validarCadenaSQL(clabe) + "' ");
			
					
			if(idBanco != 0){
				sql.append("\n And id_banco = " + idBanco );
			}
			  
			listEmpresas = jdbcTemplate.query(sql.toString(), new RowMapper<EmpresaDto>(){
					public EmpresaDto mapRow(ResultSet rs, int idx) throws SQLException {
						EmpresaDto empresa = new EmpresaDto();
						empresa.setNoEmpresa(rs.getInt("no_empresa"));
						empresa.setIdBanco(rs.getInt("id_banco"));
						empresa.setIdChequera(rs.getString("id_chequera"));
						return empresa;
					}});
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:buscarEmpresaClabe");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:buscarEmpresaClabe");
		}
		
		return listEmpresas;
	}

	@Override
	public boolean existeMovDia2(int noEmpresa, int idBanco, String chequera, String fechaValor, String referencia,
								 String cargoAbono, double importe, String concepto){
		
		List<EmpresaDto> listEmpresas = new ArrayList<EmpresaDto>(); 
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append("\n SELECT no_empresa from movto_banca_diario " );
			sql.append("\n  where no_empresa = " + noEmpresa + " ");
			sql.append("\n  and id_banco=" + idBanco + " and id_chequera='" + Utilerias.validarCadenaSQL(chequera) + "' ");
			sql.append("\n  and fec_valor= convert(datetime,'" + Utilerias.validarCadenaSQL(fechaValor) + "',103) and referencia='" + Utilerias.validarCadenaSQL(referencia) + "' ");
			sql.append("\n  and cargo_abono='" + Utilerias.validarCadenaSQL(cargoAbono) + "' and importe=" + importe + " ");
			sql.append("\n  and concepto='" + Utilerias.validarCadenaSQL(concepto) + "' ");
			
			listEmpresas = jdbcTemplate.query(sql.toString(), new RowMapper<EmpresaDto>(){
					public EmpresaDto mapRow(ResultSet rs, int idx) throws SQLException {
						EmpresaDto empresa = new EmpresaDto();
						empresa.setNoEmpresa(rs.getInt("no_empresa"));
						return empresa;
					}});
			
			if(listEmpresas.size() > 0){
				return true;
			}
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:existeMovDia2");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:existeMovDia2");
		}
		
		return false;
		
	}
	
	@Override
	public boolean existeMovBancaE(int noEmpresa, int idBanco, String chequera, String fechaValor, String referencia,
								 String cargoAbono, double importe, String concepto){
		
		List<EmpresaDto> listEmpresas = new ArrayList<EmpresaDto>(); 
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append("\n SELECT no_empresa from movto_banca_e " );
			sql.append("\n  where no_empresa = " + noEmpresa + " ");
			sql.append("\n  and id_banco=" + idBanco + " and id_chequera='" + Utilerias.validarCadenaSQL(chequera) + "' ");
			sql.append("\n  and fec_valor='" + Utilerias.validarCadenaSQL(fechaValor) + "' and referencia='" + Utilerias.validarCadenaSQL(referencia) + "' ");
			sql.append("\n  and cargo_abono='" + Utilerias.validarCadenaSQL(cargoAbono) + "' and importe=" + importe + " ");
			sql.append("\n  and concepto='" + Utilerias.validarCadenaSQL(concepto) + "' ");
			
			listEmpresas = jdbcTemplate.query(sql.toString(), new RowMapper<EmpresaDto>(){
					public EmpresaDto mapRow(ResultSet rs, int idx) throws SQLException {
						EmpresaDto empresa = new EmpresaDto();
						empresa.setNoEmpresa(rs.getInt("no_empresa"));
						return empresa;
					}});
			
			if(listEmpresas.size() > 0){
				return true;
			}
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:existeMovBancaE");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:existeMovBancaE");
		}
		
		return false;
		
	}
	
	@Override
	public int insertaMovtoBancaD(int noEmpresa, int idBanco, String chequera, long folio, String fechaValor,
			String sucursal, String folioBanco, String referencia, String cargoAbono, double importe, String concepto,
			int idUsuario, String descripcion, String archivo, int lineaArch, double saldoChequera, String cveConcepto, String fechaHoy) {
		
		StringBuffer sql = new StringBuffer();
		int afec = 0;
		
		try {
			sql.append("\n insert into movto_banca_diario( " );
			sql.append("\n  no_empresa,id_banco,id_chequera,secuencia,fec_valor, ");
			
			if(!sucursal.equals("")){
				sql.append("\n sucursal, ");
			}	
			if(!folioBanco.equals("")){
				sql.append("\n folio_banco, ");
			}
			
			sql.append("\n  referencia,cargo_abono,importe, ");
			sql.append("\n  concepto, fec_alta, ");
			
			sql.append("\n b_trasp_banco, ");
			sql.append("\n b_trasp_conta,movto_arch, ");
			sql.append("\n usuario_alta, ");
			
			if(!descripcion.equals("")){
				sql.append("\n descripcion, ");
			}
			
			sql.append("\n archivo, ");
			
			if(!cveConcepto.equals("")){
				sql.append("\n id_cve_concepto, ");
			}
			
			if(saldoChequera > 0){
				sql.append("\n saldo_bancario, ");
			}
			
			sql.append("\n no_linea_arch,estatus ");
			sql.append("\n ) ");
			
			sql.append("\n values(" + noEmpresa +" ");
			sql.append("\n ," + idBanco + ",'" + Utilerias.validarCadenaSQL(chequera) 
							  + "','" + Utilerias.validarCadenaSQL(folio) 
							  + "','" + Utilerias.validarCadenaSQL(fechaValor) + "',");
			
			if(!sucursal.equals("")){
				sql.append("\n '" + Utilerias.validarCadenaSQL(sucursal) + "', ");
			}	
			if(!folioBanco.equals("")){
				sql.append("\n '" + Utilerias.validarCadenaSQL(folioBanco) + "', ");
			}
			
			sql.append("\n '" + Utilerias.validarCadenaSQL(referencia) + "','" + Utilerias.validarCadenaSQL(cargoAbono) + "'");
			sql.append("\n ," + importe + ",");
			sql.append("\n '" + Utilerias.validarCadenaSQL(concepto) + "'");
			sql.append("\n ,'" + Utilerias.validarCadenaSQL(fechaHoy) + "' ");
			sql.append("\n ,'N','N',1," + idUsuario + ", ");
			
			if(!descripcion.equals("")){
				sql.append("\n '" + Utilerias.validarCadenaSQL(descripcion) + "', ");
			}
			
			sql.append("\n '" + Utilerias.validarCadenaSQL(archivo) + "',");
			
			if(!cveConcepto.equals("")){
				sql.append("\n '" + Utilerias.validarCadenaSQL(cveConcepto) + "', ");
			}
			
			if(saldoChequera > 0){
				sql.append("\n " + saldoChequera + ", ");
			}
 
			sql.append("\n " + lineaArch +  ",'B'");
			sql.append("\n ) ");

			afec = jdbcTemplate.update(sql.toString());
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:insertaMovtoBancaD");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:insertaMovtoBancaD");
		}
		
		return afec;
		
	}

}