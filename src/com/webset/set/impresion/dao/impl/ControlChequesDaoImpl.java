package com.webset.set.impresion.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.impresion.dao.ControlChequesDao;
import com.webset.set.impresion.dto.ControlPapelDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.SeleccionAutomaticaGrupoDto;
import com.webset.utils.tools.Utilerias;

public class ControlChequesDaoImpl implements ControlChequesDao {

	private JdbcTemplate jdbcTemplate;
	//private Funciones funciones = new Funciones();
	Bitacora bitacora=new Bitacora();
	ConsultasGenerales consultasGenerales;
	Funciones funciones = new Funciones();
	
	/***********************************************************************************/
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ControlChequesDaoImpl, M:setDataSource");
		}
	}
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	/*******************************************************************************************/
	
	public List<LlenaComboEmpresasDto>llenarComboEmpresa(){
		
		StringBuffer sql = new StringBuffer();
		List<LlenaComboEmpresasDto> listEmpresa = new ArrayList<LlenaComboEmpresasDto>();
		try{
			
			sql.append( " SELECT no_empresa as ID, nom_empresa as describ ");
			sql.append( "\n   FROM empresa ");
			sql.append( "\n  WHERE no_empresa > 1");
			sql.append( "\n  AND no_empresa in (SELECT no_empresa");
			sql.append( "\n   FROM usuario_empresa ");
			sql.append( "\n   WHERE no_usuario = "+ GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario() + ")");
			sql.append( "\n   ORDER BY  nom_empresa");
			listEmpresa= jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboEmpresasDto>(){
			public LlenaComboEmpresasDto mapRow(ResultSet rs, int idx) throws SQLException {
				LlenaComboEmpresasDto cons = new LlenaComboEmpresasDto();
				cons.setNoEmpresa(rs.getInt("ID"));
				cons.setNomEmpresa(rs.getString("describ"));
				return cons;
			}});
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ControlChequesDaoImpl, M:llenarComboEmpresa");
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ControlChequesDaoImpl, M:llenarComboEmpresa");
		}
		return listEmpresa;
	    
	}
	
	@Override
	public List<LlenaComboGralDto> llenarComboBanco() {
		StringBuffer sql = new StringBuffer();
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		
		try {
			
			/*sql.append(" SELECT distinct ccb.id_banco as ID, cb.desc_banco as descrip ");
			sql.append("\n FROM ctas_banco ccb, cat_banco cb ");
			sql.append("\n WHERE ccb.id_banco = cb.id_banco ");
			sql.append("\n 		And ccb.id_tipo_persona = 'P' ");
			*/
			
			sql.append( "\n select distinct ccb.id_banco as ID, cb.desc_banco as descrip ");
			sql.append( "\n FROM CAT_CTA_BANCO ccb JOIN CAT_BANCO cb ON ccb.id_banco = CB.ID_BANCO ");
			sql.append( "\n where tipo_chequera = 'P' ");
			sql.append( "\n ORDER by cb.desc_banco ");
			
			System.out.println("query banco: " + sql.toString());
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>() {
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto con = new LlenaComboGralDto();
					con.setId(rs.getInt("ID"));
					con.setDescripcion(rs.getString("descrip"));
					return con;
				}
			});
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)  
			+ "P:Impresion, C:ControlChequesDaoImpl, M:llenarComboBanco");
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)  
					+ "P:Impresion, C:ControlChequesDaoImpl, M:llenarComboBanco");
		}return list;
	}

	@Override
	public List<LlenaComboGralDto> llenarComboChequera(int idBanco, int noEmpresa) {
		StringBuffer sql = new StringBuffer();
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		
		try {
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			
			sql.append(" SELECT id_chequera as ID, id_chequera as descrip ");
			sql.append("\n FROM cat_cta_banco ");
			sql.append("\n WHERE id_banco = " + idBanco +" ");
			//sql.append("\n 		And id_divisa like '" + sDivisa + "'");
			
			if(noEmpresa != 0)	sql.append("\n 		And no_empresa = "+ noEmpresa +"");
			
			sql.append("\n 	ORDER BY descrip ");
			System.out.println("query cheque pago: " + sql);
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>() {
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto con = new LlenaComboGralDto();
					con.setDescripcion(rs.getString("descrip"));
					return con;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ControlChequesDaoImpl, M:llenarComboChequera");
		}return list;
	}

	/**
	 * @param controlPapel: DTO contiene todos los datos
	 * 		  tipoEstatus: 1.- Activo ('A') cuando se registra por primera vez, 2.- Inactivo('I') cuando ya existe algun registro con los mismos valores (empresa, banco, chequera). 
	 */
	@Override
	public Map<String, Object> guardarControlCheque(ControlPapelDto controlPapel, int tipoEstatus) {
		
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		StringBuffer sql = new StringBuffer();
		int count = 0;
		
		try{
	    	
			sql.append("\n INSERT INTO CONTROL_PAPEL ");
			sql.append("\n VALUES("+controlPapel.getCajaClave()+", "+controlPapel.getIdBanco()+", "); 
			sql.append("\n getdate(), ");
			sql.append("\n "+controlPapel.getFolioInvIni()+", ");
			sql.append("\n "+controlPapel.getFolioInvFin()+", ");
			sql.append("\n "+controlPapel.getFolioUltImpreso()+", ");
			sql.append("\n '"+Utilerias.validarCadenaSQL(controlPapel.getIdChequera())+"', ");
			sql.append("\n "+controlPapel.getNoEmpresa()+", ");
			sql.append("\n '"+Utilerias.validarCadenaSQL(controlPapel.getTipoFolio())+"', ");
			sql.append("\n "+controlPapel.getStock()+", ");
			
			sql.append("\n CASE WHEN (SELECT MAX(ID_CONTROL_CHEQUE) FROM CONTROL_PAPEL) IS NOT NULL ");
			sql.append("\n THEN (SELECT MAX(ID_CONTROL_CHEQUE) + 1 FROM CONTROL_PAPEL) ");
			sql.append("\n ELSE 1 END, ");
			
			if(tipoEstatus == 1){
				sql.append("\n 'A' )");
			}else if(tipoEstatus == 2){
				sql.append("\n 'I' )");
			}
			
			count = jdbcTemplate.update(sql.toString());
		
			if(count > 0){
				resultado.put("mensaje", "Registro guardado con éxito.");
			}
			
		}catch(CannotGetJdbcConnectionException e){	
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ControlChequesDaoImpl, M:guardarControlCheque");
			
			resultado.put("error", "Error al guardar control cheque: Conexión perdida.");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ControlChequesDaoImpl, M:guardarControlCheque");
			
			resultado.put("error", "Error al guardar control cheque.");
		 }
		
		return resultado;
		
	}

	@Override
	public List<ControlPapelDto> consultarCheques(ControlPapelDto controlPapel, boolean folioPapel, boolean estatusChequeras, boolean estatusChequerasT) {
		StringBuffer sql= new StringBuffer();
		List<ControlPapelDto> list= new ArrayList<ControlPapelDto>();
		try{
            sql.append("SELECT cp.bco_cve, cp.fec_alta, cp.folio_inv_ini, cp.folio_inv_fin, ");
            sql.append("\n cp.folio_ult_impreso, cp.id_chequera, cp.no_empresa, cp.tipo_folio, cp.stock, e.nom_empresa, cp.id_control_cheque, cp.estatus ");
            sql.append("\n FROM CONTROL_PAPEL cp JOIN EMPRESA e on CP.no_empresa = e.no_empresa ");
            
            if(controlPapel.getNoEmpresa() != 0){
            	sql.append("\n WHERE cp.no_empresa = "+controlPapel.getNoEmpresa()+" ");
            	
            	if(controlPapel.getIdBanco() != 0){
            		sql.append("\n AND cp.bco_cve = "+controlPapel.getIdBanco()+" ");
            	}
            	
            	if(!controlPapel.getIdChequera().equals("")){
            		sql.append("\n AND cp.id_chequera = '"+Utilerias.validarCadenaSQL(controlPapel.getIdChequera())+"' ");
            	}
            }else{
            	
            	if(controlPapel.getIdBanco() != 0){
            		sql.append("\n WHERE cp.bco_cve = "+controlPapel.getIdBanco()+" ");
            		
            		if(!controlPapel.getIdChequera().equals("")){
                		sql.append("\n AND cp.id_chequera = '"+Utilerias.validarCadenaSQL(controlPapel.getIdChequera())+"' ");
                	}
            		
            	}
            	
            }
            if (estatusChequeras) {
            	if(estatusChequeras && estatusChequerasT){
                	sql.append("\n AND cp.estatus IN('I','T')");
    			}else{
    				sql.append("\n AND cp.estatus = 'I' ");
    			}
            }else if (estatusChequerasT) {
            	if(estatusChequeras && estatusChequerasT){
                	sql.append("\n AND cp.estatus IN('I','T')");
    			}else{
    				sql.append("\n AND cp.estatus = 'T' ");
    			}	
            }else{
				sql.append("\n AND cp.estatus = 'A' ");
			}
            //sql.append("\n AND cp.estatus IN ('A','I') ");
            if(folioPapel){
            	sql.append("\n AND cp.tipo_folio = 'P' ");
            }else{
            	sql.append("\n AND cp.tipo_folio = 'C' ");
            }
            
	        System.out.println("Obtiene control cheques: \n" + sql);
	        
	        list= jdbcTemplate.query(sql.toString(), new RowMapper<ControlPapelDto>(){
				public ControlPapelDto mapRow(ResultSet rs, int idx) throws SQLException {
					ControlPapelDto cons = new ControlPapelDto();
					cons.setIdBanco(rs.getInt("bco_cve"));
					cons.setFechaAlta( funciones.ponerFechaSola(rs.getDate("fec_alta")));
					cons.setFolioInvIni(rs.getInt("folio_inv_ini"));
					cons.setFolioInvFin(rs.getInt("folio_inv_fin"));
					cons.setFolioUltImpreso(rs.getInt("folio_ult_impreso"));
					cons.setIdChequera(rs.getString("id_chequera"));
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setNomEmpresa(rs.getString("nom_empresa"));
					cons.setTipoFolio(rs.getString("tipo_folio"));
					cons.setStock(rs.getInt("stock"));
					cons.setIdControlCheque(rs.getInt("id_control_cheque"));
					cons.setEstatus(rs.getString("estatus"));
					return cons;
				}});
            
		}catch(CannotGetJdbcConnectionException e){	
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ControlChequesDaoImpl, M:consultarCheques");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ControlChequesDaoImpl, M:consultarCheques");
		 }
		
		return list;
	}

	@Override
	public Map<String, Object> eliminarControlCheque(ControlPapelDto controlPapel) {
		
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		StringBuffer sql = new StringBuffer();
		int count = 0;
		
		try{
	    	
			sql.append("\n UPDATE CONTROL_PAPEL ");
			sql.append("\n SET ESTATUS = 'E' "); 
			sql.append("\n WHERE id_control_cheque = '"+ controlPapel.getIdControlCheque() +"' ");
			/*sql.append("\n WHERE BCO_CVE = "+controlPapel.getIdBanco()+" "); 
			sql.append("\n AND id_chequera = '"+ controlPapel.getIdChequera() +"' ");
			sql.append("\n AND no_empresa = "+controlPapel.getNoEmpresa()+" ");
			sql.append("\n AND tipo_folio = '"+ controlPapel.getTipoFolio() +"' ");
			sql.append("\n AND id_control_cheque = '"+ controlPapel.getIdControlCheque() +"' ");
			*/
			count = jdbcTemplate.update(sql.toString());
		
			if(count > 0){
				resultado.put("mensaje", "Registro eliminado con éxito.");
			}else{
				resultado.put("error", "Error al eliminar el registro.");
			}
			
		}catch(CannotGetJdbcConnectionException e){	
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ControlChequesDaoImpl, M:elimminarControlCheque");
			
			resultado.put("error", "Error al eliminar control cheque: Conexión perdida.");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ControlChequesDaoImpl, M:eliminarControlCheque");
			
			resultado.put("error", "Error al eliminar control cheque.");
		 }
		
		return resultado;
	}
	
	@Override
	public Map<String, Object> modificarControlCheque(ControlPapelDto controlPapelOrig, ControlPapelDto controlPapel) {
		
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		StringBuffer sql = new StringBuffer();
		int count = 0;
		
		try{
	    	
			sql.append("\n UPDATE CONTROL_PAPEL ");
			sql.append("\n SET BCO_CVE = "+controlPapel.getIdBanco()+" ");
			sql.append("\n , no_empresa = "+ controlPapel.getNoEmpresa() +" ");
			sql.append("\n , id_chequera = '"+ Utilerias.validarCadenaSQL(controlPapel.getIdChequera()) +"' ");
			sql.append("\n , folio_inv_ini = "+ controlPapel.getFolioInvIni()+" ");
			sql.append("\n , folio_inv_fin = "+ controlPapel.getFolioInvFin()+" ");
			sql.append("\n , folio_ult_impreso = "+ controlPapel.getFolioUltImpreso()+" ");
			sql.append("\n , tipo_folio = '"+ Utilerias.validarCadenaSQL(controlPapel.getTipoFolio()) +"' ");
			sql.append("\n , stock = "+ controlPapel.getStock() +" ");
			
			sql.append("\n WHERE ID_CONTROL_CHEQUE = "+controlPapelOrig.getIdControlCheque()+" ");
			
			/*sql.append("\n WHERE BCO_CVE = "+controlPapelOrig.getIdBanco()+" "); 
			sql.append("\n AND id_chequera = '"+ controlPapelOrig.getIdChequera() +"' ");
			sql.append("\n AND no_empresa = "+controlPapelOrig.getNoEmpresa()+" ");
			sql.append("\n AND tipo_folio = '"+ controlPapelOrig.getTipoFolio() +"' ");
			*/
			
			System.out.println("Update de control_papel: \n" + sql.toString());
			
			count = jdbcTemplate.update(sql.toString());
		
			if(count > 0){
				resultado.put("mensaje", "Registro modificado con éxito.");				
			}
			
		}catch(CannotGetJdbcConnectionException e){	
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ControlChequesDaoImpl, M:modificarControlCheque");
			
			resultado.put("error", "Error al modificar control cheque: Conexión perdida.");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ControlChequesDaoImpl, M:modificarControlCheque");
			
			resultado.put("error", "Error al modificar control cheque.");
		 }
		
		return resultado;
	}
	
	//Busca si ya existe una chequera con empresa, banco, chequera en control_papel 
	@Override
	public List<ControlPapelDto> existeChequeraControlPapel(ControlPapelDto controlPapel, int tipoEstatus) {
		StringBuffer sql= new StringBuffer();
		List<ControlPapelDto> list= new ArrayList<ControlPapelDto>();
		
		try{
            sql.append("SELECT cp.folio_inv_fin, cp.id_control_cheque ");
            sql.append("\n FROM CONTROL_PAPEL cp ");
            
            if(controlPapel.getNoEmpresa() != 0){
            	sql.append("\n WHERE cp.no_empresa = "+controlPapel.getNoEmpresa()+" ");
            	
            	if(controlPapel.getIdBanco() != 0){
            		sql.append("\n AND cp.bco_cve = "+controlPapel.getIdBanco()+" ");
            	}
            	
            	if(!controlPapel.getIdChequera().equals("")){
            		sql.append("\n AND cp.id_chequera = '"+Utilerias.validarCadenaSQL(controlPapel.getIdChequera())+"' ");
            	}
            }else{
            	
            	if(controlPapel.getIdBanco() != 0){
            		sql.append("\n WHERE cp.bco_cve = "+controlPapel.getIdBanco()+" ");
            		
            		if(!controlPapel.getIdChequera().equals("")){
                		sql.append("\n AND cp.id_chequera = '"+Utilerias.validarCadenaSQL(controlPapel.getIdChequera())+"' ");
                	}
            		
            	}
            	
            }
            
            sql.append("\n AND cp.estatus = 'A' ");
            if(tipoEstatus == 1)
            	sql.append("\n AND cp.tipo_folio = 'P' ");
            else if(tipoEstatus == 2)
            	sql.append("\n AND cp.tipo_folio = 'C' ");
            
	        System.out.println("Existe control cheques: \n" + sql);
	        
	        list= jdbcTemplate.query(sql.toString(), new RowMapper<ControlPapelDto>(){
				public ControlPapelDto mapRow(ResultSet rs, int idx) throws SQLException {
					ControlPapelDto cons = new ControlPapelDto();
					//cons.setIdBanco(rs.getInt("bco_cve"));
					//cons.setFechaAlta( funciones.ponerFechaSola(rs.getDate("fec_alta")));
					//cons.setFolioInvIni(rs.getInt("folio_inv_ini"));
					cons.setFolioInvFin(rs.getInt("folio_inv_fin"));
					//cons.setFolioUltImpreso(rs.getInt("folio_ult_impreso"));
					//cons.setIdChequera(rs.getString("id_chequera"));
					//cons.setNoEmpresa(rs.getInt("no_empresa"));
					//cons.setNomEmpresa(rs.getString("nom_empresa"));
					//cons.setTipoFolio(rs.getString("tipo_folio"));
					//cons.setStock(rs.getInt("stock"));
					cons.setIdControlCheque(rs.getInt("id_control_cheque"));
					return cons;
				}});
            
		}catch(CannotGetJdbcConnectionException e){	
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ControlChequesDaoImpl, M:existeChequeraControlPapel");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ControlChequesDaoImpl, M:existeChequeraControlPapel");
		 }
		
		return list;
	}
	
	@Override
	public int cambiarInactivoControlCheque(int idControlCheque) {
		
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		StringBuffer sql = new StringBuffer();
		int count = 0;
		
		try{
	    	
			sql.append("\n UPDATE CONTROL_PAPEL ");
			sql.append("\n SET ESTATUS = 'I' "); 
			sql.append("\n WHERE id_control_cheque = "+ idControlCheque +" ");
			
			count = jdbcTemplate.update(sql.toString());
			
		}catch(CannotGetJdbcConnectionException e){	
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ControlChequesDaoImpl, M:cambiarInactivoControlCheque");
			
			return -1;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ControlChequesDaoImpl, M:cambiarInactivoControlCheque");
		 }
		
		return count;
	}
	
	//Metodo busca algun registro que se encuentre como inactivo entre un rango de cheques
	@Override
	public int existeChequeraControlPapelInactivo(ControlPapelDto controlPapel, int tipoEstatus) {
		StringBuffer sql= new StringBuffer();
		//List<ControlPapelDto> list= new ArrayList<ControlPapelDto>();
		int cont = 0;
		
		try{
            sql.append("SELECT COUNT(cp.id_chequera) ");
            sql.append("\n FROM CONTROL_PAPEL cp ");
            
            if(controlPapel.getNoEmpresa() != 0){
            	sql.append("\n WHERE cp.no_empresa = "+controlPapel.getNoEmpresa()+" ");
            	
            	if(controlPapel.getIdBanco() != 0){
            		sql.append("\n AND cp.bco_cve = "+controlPapel.getIdBanco()+" ");
            	}
            	
            	if(!controlPapel.getIdChequera().equals("")){
            		sql.append("\n AND cp.id_chequera = '"+Utilerias.validarCadenaSQL(controlPapel.getIdChequera())+"' ");
            	}
            }else{
            	
            	if(controlPapel.getIdBanco() != 0){
            		sql.append("\n WHERE cp.bco_cve = "+controlPapel.getIdBanco()+" ");
            		
            		if(!controlPapel.getIdChequera().equals("")){
                		sql.append("\n AND cp.id_chequera = '"+Utilerias.validarCadenaSQL(controlPapel.getIdChequera())+"' ");
                	}
            		
            	}
            	
            }
            
            sql.append("\n AND cp.estatus = 'I' ");
            if(tipoEstatus == 1)
            	sql.append("\n AND cp.tipo_folio = 'P' ");
            else
            	sql.append("\n AND cp.tipo_folio = 'C' ");
            
            sql.append("\n AND (cp.folio_inv_ini between "+controlPapel.getFolioInvIni()+" AND "+controlPapel.getFolioInvFin()+" ");
            sql.append("\n  	OR cp.folio_inv_fin between "+controlPapel.getFolioInvIni()+" AND "+controlPapel.getFolioInvFin()+" ) ");
            
	        System.out.println("Existe control cheques inactivo: \n" + sql);
	        
	        cont = jdbcTemplate.queryForInt(sql.toString());
            
		}catch(CannotGetJdbcConnectionException e){	
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ControlChequesDaoImpl, M:existeChequeraControlPapel");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ControlChequesDaoImpl, M:existeChequeraControlPapel");
		 }
		
		return cont;
	}
	
}
