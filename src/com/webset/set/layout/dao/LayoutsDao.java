package com.webset.set.layout.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.bancaelectronica.dto.CriterioBusquedaDto;
import com.webset.set.bancaelectronica.dto.DetArchTransferDto;
import com.webset.set.layout.dto.ParametrosLayoutDto;
import com.webset.set.traspasos.business.TraspasosBusiness;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.ConstantesSet;

public class LayoutsDao {
	
	private JdbcTemplate jdbcTemplate;
	ConsultasGenerales consultasGenerales;
	Bitacora bitacora = new Bitacora();
	Funciones funciones = new Funciones();
	GlobalSingleton globalSingleton;
	private static Logger logger = Logger.getLogger(LayoutsDao.class);
	
	String gsDBM = ConstantesSet.gsDBM;
	boolean pbMasspay = false;
	
	public String consultarConfiguraSet(int indice){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
	}
	
	public int seleccionarFolioReal(String tipoFolio){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.seleccionarFolioReal(tipoFolio);
	}
	
	public int actualizarFolioReal(String tipoFolio){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.actualizarFolioReal(tipoFolio);
	}
	
	public int seleccionarbanco(int idBanco){
		StringBuffer sql = new StringBuffer();
		try{
			sql.append(" SELECT id_banco_bancomer ");
			sql.append(" FROM cat_banco ");
			sql.append(" WHERE id_banco = "+idBanco);
			
			return jdbcTemplate.queryForInt(sql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Layout, C:LayoutsDao, M:seleccionarbanco");
			return 0;
		}
	}
	
	public String obtenerInstiticionFinanciera(String folioDet){
		List<String> resultList = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("Select Inst_Finan From Cat_Banco where id_banco in (select id_banco from movimiento where no_folio_det = ");
			sql.append(folioDet);
			sql.append(" )");
			resultList = jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
	 			public String mapRow(ResultSet rs, int idx) throws SQLException {
	 				return rs.getString("Inst_Finan");
	 			}
	 		});
     		
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Layout, C:LayoutsDao, M:obtenerInstiticionFinanciera");
		}
		return resultList != null && !resultList.isEmpty()? resultList.get(0): "";
	}
	
	/*public List<String> obtenerPaisTelefonoBeneficiarioAgrupados(int idBanco, int noEmpresa
						String poHeaders, id){
		
	}
	*/
	public String obtenerReferenciaAgrupada(int noEmpresa, int idBanco, String chequera, String poHeaders){
		StringBuffer sql = new StringBuffer();
		List<String> resultList = new ArrayList<String>();
		try {
			sql.append("");
			sql.append("Select Referencia From Movimiento Where No_Empresa =");
			sql.append(noEmpresa);
			sql.append(" and Id_Banco = "); 
			sql.append(idBanco);
			sql.append("and id_chequera = '");
			sql.append(chequera);
			sql.append("' and Po_Headers = '");
			sql.append(poHeaders);
			sql.append("'");
			sql.append("and referencia is not null and Rownum = 1");
			System.out.println("Consulta de referencia agrupada : "+sql);
			resultList = jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
	 			public String mapRow(ResultSet rs, int idx) throws SQLException {
	 				return rs.getString("Referencia");
	 			}
	 		});
			System.out.println("referencia agrupada "+resultList);
			if (resultList == null || resultList.isEmpty() || resultList.size() == 0 || (resultList.size()>0 && resultList.get(0).equals(""))) {
				resultList.add(poHeaders);
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Layout, C:LayoutsDao, M:obtenerReferenciaAgrupada");
		}
		return resultList != null && !resultList.isEmpty()? resultList.get(0): "";
	}
	
	public String obtieneDatosMovComplementarios(String folioDet){
		List<String>  retorno = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append("Select (Case When (Referencia Is Null or Referencia = '') Then Po_Headers Else referencia end ) as Po_Headers From Movimiento where no_folio_det = ");
			sql.append(folioDet);
			retorno= jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
	 			public String mapRow(ResultSet rs, int idx) throws SQLException {
	 				return (rs.getString("Po_Headers") != null && !rs.getString("Po_Headers").equals("") ? rs.getString("Po_Headers"):"0000000000");
	 			}
	 		});
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Layout, C:LayoutsDao, M:obtieneDatosMovComplementarios");
		}
		return retorno != null && !retorno.isEmpty() ?retorno.get(0) : "";
		
	}
	
	public String obtienePolizaConpensa(String folioDet){
		List<String>  retorno = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append("Select  Po_Headers  as Po_Headers From Movimiento where no_folio_det = ");
			sql.append(folioDet);
			retorno= jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
	 			public String mapRow(ResultSet rs, int idx) throws SQLException {
	 				return (rs.getString("Po_Headers") != null && !rs.getString("Po_Headers").equals("") ? rs.getString("Po_Headers"):"  ");
	 			}
	 		});
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Layout, C:LayoutsDao, M:obtieneDatosMovComplementarios");
		}
		return retorno != null && !retorno.isEmpty() ?retorno.get(0) : "";
		
	}
	
	public String obtieneComplementosBancoOrigen(String chequera,  int idBanco ){
		List<String>  retorno = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append("select * from Cat_Cta_Banco where  ");
			sql.append("\n  id_banco = ");
			sql.append(idBanco);
			sql.append("\n   and Id_Chequera = '");
			sql.append(chequera);
			sql.append("'");
			retorno= jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
	 			public String mapRow(ResultSet rs, int idx) throws SQLException {
	 				return (rs.getString("id_clabe") != null  ? rs.getString("id_clabe"):"");
	 			}
	 		});
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Layout, C:LayoutsDao, M:obtieneComplementosBancoOrigen");
		}
		return retorno != null && !retorno.isEmpty() ?retorno.get(0) : "";		
	}

	public String obtieneReferenciaMovimiento(String folioDet){
		List<String>  retorno = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append("Select  cd.ref_alfanumerica, m.Referencia, * ");
			sql.append(" FROM  movimiento M JOIN PERSONA P ON m.NO_CLIENTE = p.NO_PERSONA JOIN CAT_FORMA_PAGO f ON m.ID_FORMA_PAGO = f.ID_FORMA_PAGO, chequera_default cd  where no_folio_det =");
			sql.append(folioDet);
			sql.append("  and p.no_persona= cd.no_persona");
			retorno= jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
	 			public String mapRow(ResultSet rs, int idx) throws SQLException {
	 				return (rs.getString("ref_alfanumerica") != null  ? rs.getString("ref_alfanumerica") : rs.getString("ref_numerica"));
	 			}
	 		});
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Layout, C:LayoutsDao, M:obtieneReferenciaMovimiento");
		}
		return retorno != null && !retorno.isEmpty() ? retorno.get(0) : "";		
	}

	
	public String obtieneRfcEmpresa(String empresa){
		List<String>  retorno = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append("select rfc from persona where no_persona = ");
			sql.append(empresa);
			sql.append(" and Id_Tipo_Persona = 'E'");
			retorno= jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
	 			public String mapRow(ResultSet rs, int idx) throws SQLException {
	 				return (rs.getString("rfc"));
	 			}
	 		});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Layout, C:LayoutsDao, M:obtieneRfcEmpresa");
		}
		return retorno != null && !retorno.isEmpty() ?retorno.get(0) : "";
		
	}

	public Map<String,Object> obtenerValorPagoReferenciado(int noCliente, int noFolioDet, String psRef){
		StringBuffer sql = new StringBuffer();
		List<String> listCons = new ArrayList<String>();
		Map<String,Object> mapRet = new HashMap<String,Object>();
		String psValor="";
		String psConDocto="";
		System.out.println("dentro del metodo:..obtenerValorPagoReferenciado-- ");
	 
		try {
			mapRet.put("psRef2", "");
			
//			sql.append(" SELECT coalesce( 'no_folio_det', referencia_cte) as Campo ");
			sql.append(" SELECT referencia_cte as Campo ");
			sql.append(" FROM persona ");
			sql.append(" WHERE no_persona = " + noCliente+" and id_tipo_persona in('P','K')");
			System.out.println("valor del qry..:"+sql.toString());
		     listCons= jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
					public String mapRow(ResultSet rs, int idx) throws SQLException {
						return rs.getString("Campo");
					}});
		     if(listCons.size()>0) {
		    	 psValor = listCons.get(0);
		    	 System.out.println("valor "+psValor);
		    	 if(psValor==null){
		    		 psValor="";
		    	 }
		    	 if(psValor.equals("")){
		    		 psValor="no_folio_det";
		    	 }
		    	// System.out.println("valor2 "+psValor);
		    	 sql = new StringBuffer();
		    	 listCons = new ArrayList<String>();
		    	 
		    	 if(psValor.equals("no_folio_det")) {
		    	 	 psConDocto = consultarConfiguraSet(354);
		    	 	 
		    	 	 if(psConDocto != null && psConDocto.equals("SI")) {
		    	 		sql.append(" SELECT no_docto ");
    	                sql.append(" FROM movimiento ");
                 		sql.append(" WHERE no_folio_det = " + noFolioDet);
                 		System.out.println("no_docto "+sql.toString());
                 		listCons= jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
        		 			public String mapRow(ResultSet rs, int idx) throws SQLException {
        		 				return rs.getString("no_docto");
        		 			}
        		 		});
                 		
                 		if(listCons.size()>0) {
                 			if(!listCons.get(0).equals(""))
                 				mapRet.put("valorPagRef", listCons.get(0));
                 			else
                 				mapRet.put("valorPagRef", noFolioDet);
                 		}else
                 			mapRet.put("valorPagRef", noFolioDet);
		    	 	 }else
		    	 		 mapRet.put("valorPagRef", noFolioDet);
		    	 }else if(psValor.equals("no_factura") || psValor.equals("no_docto") 
		    	 		 || psValor.equals("referencia")|| psValor.equals("concepto")) {
		    	            sql.append(" SELECT " + psValor +" as Valor ");
		            		sql.append(" FROM movimiento ");
		    	 			sql.append(" WHERE no_folio_det = " +noFolioDet);
		    	 			System.out.println("valor"+sql.toString());
		    	 			listCons= jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
		    	 				public String mapRow(ResultSet rs, int idx) throws SQLException {
		    	 					return rs.getString("Valor");
		    	 				}});
		    	            if(listCons.size()>0)
		    	            	mapRet.put("valorPagRef",listCons.get(0));
		    	            else
		    	            	mapRet.put("valorPagRef", "");
		    	 }else if(psValor.equals("concepto y no_factura")){
		             sql.append( " SELECT coalesce(no_factura, '' ) as no_factura, ");
		             sql.append( "     coalesce(concepto, 'SIN CONCEPTO') as concepto ");
		             sql.append( " FROM movimiento ");
		             sql.append( " WHERE no_folio_det = " +noFolioDet);
		             System.out.println("concepto y no factura "+sql.toString());
		             List<Map<String,Object>> map = new ArrayList<Map<String,Object>>();
		             map= jdbcTemplate.query(sql.toString(), new RowMapper<Map<String,Object>>(){
	    		 			public Map<String,Object> mapRow(ResultSet rs, int idx) throws SQLException {
	    		 					Map<String,Object> mapi = new HashMap<String,Object>();
	    		 					mapi.put("no_factura",rs.getString("no_factura"));
	    		 					mapi.put("concepto",rs.getString("concepto"));
	    		 				return  mapi;
	    		 			}});
		             
		            	 if(!map.isEmpty() && map.get(0).get("no_factura").equals("")) 
		            		 mapRet.put("psRef2", "0000");
			             else
			            	 mapRet.put("psRef2",map.get(0).get("no_factura"));
			             
			             if(!map.isEmpty() && map.get(0).get("concepto").equals("")) 
			            	 mapRet.put("valorPagRef", "SIN CONCEPTO");
			             else
			            	 mapRet.put("valorPagRef",map.get(0).get("concepto"));
		    	 }else{
		    	 	 mapRet.put("valorPagRef",psValor);
		     	 }
		     }
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Layout, C:LayoutsDao, M:obtenerValorPagoReferenciado");
		}
		 return mapRet;
	}
	
	
	public String concepto(int noCliente, int noFolioDet) {
		List<String> listCons = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		String valor = "";
		try{
			sql.append(" SELECT coalesce(referencia_cte, 'no_folio_det') as Campo");
			sql.append(" FROM persona");
			sql.append(" WHERE no_persona = " + noCliente);
			
			listCons = jdbcTemplate.query(sql.toString(), new RowMapper<String>() {
				public String mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getString("Campo");
			}});
			
			if(listCons.size() > 0) valor = listCons.get(0);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Layout, C:LayoutsDao, M:concepto");
		}
		return valor;
	}
	
	public String conceptoBancomer(int noCliente) {
		List<String> listCons = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		String valor = "";
		try{
			sql.append(" SELECT coalesce(referencia_cte, 'no_folio_det') as Campo");
			sql.append(" FROM persona");
			sql.append(" WHERE no_persona = " + noCliente);
			
			listCons = jdbcTemplate.query(sql.toString(), new RowMapper<String>() {
				public String mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getString("Campo");
			}});
			
			if(listCons.size() > 0) valor = listCons.get(0);
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Layout, C:LayoutsDao, M:concepto");
		}
		return valor;
	}
	
	//revisado A.A.G
	public List<CriterioBusquedaDto> envioBEParametrizado(int idBanco, int eTipoEnv) {
		List<CriterioBusquedaDto> resList = new ArrayList<CriterioBusquedaDto>();
		StringBuffer sql = new StringBuffer();
		try {
			System.out.println("angel:.. envio parametrizado");
			sql.append("SELECT * FROM envio_be");
			sql.append("\n	WHERE id_banco ="+idBanco );
			//sql.append(idBanco);
			sql.append("\n		And id_tipo_envio ="+eTipoEnv);
			//sql.append(eTipoEnv);
			sql.append("\n	ORDER BY secuencia");
			System.out.println("Angel::envioParametrizado valor qry..::"+sql.toString());
			resList = jdbcTemplate.query(sql.toString(), new RowMapper<CriterioBusquedaDto>(){
				public CriterioBusquedaDto mapRow(ResultSet rs, int idx) throws SQLException {
					CriterioBusquedaDto cons = new CriterioBusquedaDto();
					cons.setIdBanco(rs.getInt("id_banco"));
					cons.setIdTipoEnvio(rs.getInt("id_tipo_envio"));
					cons.setSecuencia(rs.getInt("secuencia"));
					cons.setCampo(rs.getString("campo"));
					cons.setPosIni(rs.getInt("pos_ini"));
					cons.setPosFin(rs.getInt("pos_fin"));
					cons.setLongitud(rs.getInt("longitud"));
					cons.setBRequerido(rs.getString("b_requerido"));
					cons.setValorDefault(rs.getString("valor_default"));
					cons.setIdTipoEquivale(rs.getInt("id_tipo_equivale"));
					cons.setFormato(rs.getString("formato") == null ? "":rs.getString("formato"));
					cons.setJustifica(rs.getString("justifica"));
					cons.setSeparador(rs.getString("separador"));
					cons.setComplemento(rs.getString("complemento"));
					cons.setTabla(rs.getString("tabla"));
					cons.setCondicionTabla(rs.getString("condicion_tabla"));
					cons.setTipoDato(rs.getString("tipo_dato"));
					cons.setEquivaleCampo(rs.getString("equivale_campo"));
					cons.setValidaCampo(rs.getString("valida_campo"));
					cons.setValidacionEspecial(rs.getInt("validacion_especial"));
					cons.setFuncionEspecial(rs.getString("funcion_especial"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Layout, C:LayoutsDao, M:envioBEParametrizado");
		}
		return resList;
	}
	
	public List<CriterioBusquedaDto> envioBEParametrizadoAgrupado(int idBanco, int eTipoEnv) {
		List<CriterioBusquedaDto> resList = new ArrayList<CriterioBusquedaDto>();
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("SELECT * FROM envio_be");
			sql.append("\n	WHERE id_banco = ");
			sql.append(idBanco);
			sql.append("\n		And id_tipo_envio = ");
			sql.append(eTipoEnv);
			sql.append("\n	ORDER BY secuencia");
			
			resList = jdbcTemplate.query(sql.toString(), new RowMapper<CriterioBusquedaDto>(){
				public CriterioBusquedaDto mapRow(ResultSet rs, int idx) throws SQLException {
					CriterioBusquedaDto cons = new CriterioBusquedaDto();
					cons.setIdBanco(rs.getInt("id_banco"));
					cons.setIdTipoEnvio(rs.getInt("id_tipo_envio"));
					cons.setSecuencia(rs.getInt("secuencia"));
					cons.setCampo(rs.getString("campo"));
					cons.setPosIni(rs.getInt("pos_ini"));
					cons.setPosFin(rs.getInt("pos_fin"));
					cons.setLongitud(rs.getInt("longitud"));
					cons.setBRequerido(rs.getString("b_requerido"));
					cons.setValorDefault(rs.getString("valor_default") != null 
							&& !rs.getString("valor_default").equals("") ? rs.getString("valor_default"):"");
					cons.setIdTipoEquivale(rs.getInt("id_tipo_equivale"));
					cons.setFormato(rs.getString("formato") == null ? "":rs.getString("formato"));
					cons.setJustifica(rs.getString("justifica"));
					cons.setSeparador(rs.getString("separador"));
					cons.setComplemento(rs.getString("complemento"));
					cons.setTabla(rs.getString("tabla"));
					cons.setCondicionTabla(rs.getString("condicion_tabla"));
					cons.setTipoDato(rs.getString("tipo_dato"));
					cons.setEquivaleCampo(rs.getString("equivale_campo"));
					cons.setValidaCampo(rs.getString("valida_campo"));
					cons.setValidacionEspecial(rs.getInt("validacion_especial"));
					cons.setFuncionEspecial(rs.getString("funcion_especial"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Layout, C:LayoutsDao, M:envioBEParametrizado");
		}
		return resList;
	}
	
	public List<CriterioBusquedaDto> selectEquivaleBE(int idBanco) {
		List<CriterioBusquedaDto> resList = new ArrayList<CriterioBusquedaDto>();
		String sql = "";
		
		try {
			sql += "SELECT * FROM equivale_be";
			sql += "\n	WHERE id_banco = " + idBanco + "";
			
			resList = jdbcTemplate.query(sql.toString(), new RowMapper<CriterioBusquedaDto>(){
				public CriterioBusquedaDto mapRow(ResultSet rs, int idx) throws SQLException {
					CriterioBusquedaDto cons = new CriterioBusquedaDto();
					cons.setIdBanco(rs.getInt("id_banco"));
					cons.setIdTipoEquivale(rs.getInt("id_tipo_equivale"));
					cons.setValorCampo(rs.getString("valor_campo"));
					cons.setValorEquivale(rs.getString("valor_equivale"));
					return cons;
				}});

		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Layout, C:LayoutsDao, M:envioBEParametrizado");
		}
		return resList;
	}
	

	//Revisado A.A.G
	public String obtenValorPagoReferenciado(String noCliente, int noFolioDet, String sRef2) {
		String sql = "";
		String sValor = "";
		String psConDocto = "";
		List<String> listCons = new ArrayList<String>();
		List<CriterioBusquedaDto> resList = new ArrayList<CriterioBusquedaDto>();
		
		try {
			sql = "SELECT coalesce(referencia_cte, 'no_folio_det') as Campo";
			sql += "\n	FROM persona";
			sql += "\n	WHERE no_persona = " + noCliente + "";
			
			listCons = jdbcTemplate.query(sql.toString(), new RowMapper<String>() {
				public String mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getString("Campo");
				}
			});
			if(listCons.size() > 0) sValor = listCons.get(0).toString().equals("") ? "no_folio_det" : listCons.get(0).toString();
			
			if(sValor.equals("no_folio_det")) {
				listCons.clear();
				//se va a verificar el configura_set para ver si mandamos el no docto o bien el folio det
				psConDocto = consultarConfiguraSet(354);
				
				if(psConDocto.equals("SI")) {
					sql = "";
					sql += "SELECT no_docto FROM movimiento ";
					sql += "\n	WHERE no_folio_det = " + noFolioDet + "";
					
					listCons = jdbcTemplate.query(sql.toString(), new RowMapper<String>() {
						public String mapRow(ResultSet rs, int idx) throws SQLException {
							return rs.getString("no_docto");
						}
					});
					if(listCons.size() > 0) {
						if(!listCons.get(0).toString().equals(""))
							return listCons.get(0).toString();
					}
				}
				return "" + noFolioDet;
			}else if(sValor.equals("no_factura") || sValor.equals("no_docto") || sValor.equals("referencia") || sValor.equals("concepto")) {
				listCons.clear();
				sql = "";
				sql += "SELECT " + sValor + " as Valor FROM movimiento ";
				sql += "\n	WHERE no_folio_det = " + noFolioDet + "";
				
				listCons = jdbcTemplate.query(sql.toString(), new RowMapper<String>() {
					public String mapRow(ResultSet rs, int idx) throws SQLException {
						return rs.getString("no_docto");
					}
				});
				if(listCons.size() > 0)
					return listCons.get(0).toString();
			}else if(sValor.equals("concepto y no_factura")) {
				listCons.clear();
				sql = "";
				sql += "SELECT coalesce(no_factura, '') as no_factura, ";
				sql += "\n		coalesce(concepto, 'SIN CONCEPTO') as concepto";
				sql += "\n	WHERE no_folio_det = " + noFolioDet + "";
				
				resList = jdbcTemplate.query(sql.toString(), new RowMapper<CriterioBusquedaDto>() {
					public CriterioBusquedaDto mapRow(ResultSet rs, int idx) throws SQLException {
						CriterioBusquedaDto cons = new CriterioBusquedaDto();
						cons.setNoFactura(rs.getString("no_factura"));
						cons.setConcepto(rs.getString("concepto"));
						return cons;
					}
				});
				if(resList.size() > 0) {
					if(resList.get(0).getNoFactura().trim().equals(""))
						sRef2 = "0000";
					else
						sRef2 = resList.get(0).getNoFactura().trim();
					
					if(resList.get(0).getConcepto().trim().equals(""))
						return "SIN CONCEPTO";
					else
						return resList.get(0).getConcepto().trim();
				}
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Layout, C:LayoutsDao, M:obtenValorPagoReferenciado");
			return "";
		}
		return sValor;
	}
	
	public String fechaHoy() {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return "" + consultasGenerales.obtenerFechaHoy();
	}
	
	public String obtenerFechaHoyDMY() {
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT to_char(fec_hoy,'dd/mm/yyyy') as fec_hoy FROM FECHAS ");
		try{
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			 List<String> fechas = jdbcTemplate.query(sb.toString(), new RowMapper<String>() {
				public String mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getString("fec_hoy");
				}
			});
			System.out.println("fechas"+ fechas.get(0));
			 return fechas.get(0);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Utilerias, C:ConsultasDao, M:obtenerFechaHoy");
			return null;
		}
	}
	
	public String obtenerFechaMananaDMY() {
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT to_char(fec_manana,'dd/mm/yyyy') as fec_hoy FROM FECHAS ");
		try{
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			 List<String> fechas = jdbcTemplate.query(sb.toString(), new RowMapper<String>() {
				public String mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getString("fec_hoy");
				}
			});
			System.out.println("fechas"+ fechas.get(0));
			 return fechas.get(0);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Utilerias, C:ConsultasDao, M:obtenerFechaHoy");
			return null;
		}
	}
	
	public String obtenerFechaHoyDMYADD() {
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT to_char(fec_hoy + 30,'dd/mm/yyyy') as fec_hoy FROM FECHAS ");
		try{
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			 List<String> fechas = jdbcTemplate.query(sb.toString(), new RowMapper<String>() {
				public String mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getString("fec_hoy");
				}
			});
			System.out.println("fechas"+ fechas.get(0));
			 return fechas.get(0);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Utilerias, C:ConsultasDao, M:obtenerFechaHoy");
			return null;
		}
	}

	//revisado A.A.G 
	public List<MovimientoDto> encabezadoBitalEnvio(String sFolios, boolean pbMasspayment, boolean pbControlArchivo) {
		List<MovimientoDto> resList = new ArrayList<MovimientoDto>();
		StringBuffer sql = new StringBuffer();
		sql.append("");
		pbMasspay = pbMasspayment;
		try {
			sql.append(queryEncabezadoBitalEnvio(sFolios, "movimiento", pbMasspayment));
			
			if(pbControlArchivo) {
				sql.append("\n UNION \n");
				sql.append( queryEncabezadoBitalEnvio(sFolios, "hist_solicitud", pbMasspayment));
				sql.append("\n UNION \n");
				sql.append( queryEncabezadoBitalEnvio(sFolios, "hist_movimiento", pbMasspayment));
			}
			
			if(pbMasspayment)
				sql.append(" ORDER BY e.id_contrato_mass ");
			else
				sql.append( " ORDER BY m.id_chequera ");
			
			
			resList = jdbcTemplate.query(sql.toString(), new RowMapper<MovimientoDto>(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons = new MovimientoDto();
					
					if(pbMasspay) {
						cons.setImporte(rs.getDouble("suma"));
						cons.setIdDivisa(rs.getString("id_divisa"));
						cons.setNoContratoMass(rs.getString("no_contrato_mass"));
						cons.setIdContratoMass(rs.getInt("id_contrato_mass"));
					}else {
						cons.setIdBanco(rs.getInt("id_banco"));
						cons.setIdChequera(rs.getString("id_chequera"));
						cons.setDescPlaza(rs.getString("plaza"));
						cons.setImporte(rs.getDouble("importe_encabezado"));
						cons.setIdDivisa(rs.getString("id_divisa"));
						cons.setUsuarioBE(rs.getInt("usuario_BE"));
						cons.setRfc(rs.getString("RFC_originador"));
						cons.setIdContrato(rs.getInt("id_contrato"));
						cons.setNoContrato(rs.getDouble("no_contrato"));
						cons.setNomEmpresa(rs.getString("nom_empresa"));
					}
					return cons;
				}});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Layout, C:LayoutsDao, M:encabezadoBitalEnvio");
		}
		return resList;
	}
	
	public List<MovimientoDto> encabezadoBitalEnvioAgrupado(String poHeaders,boolean pbMasspayment, boolean pbControlArchivo) {
		List<MovimientoDto> resList = new ArrayList<MovimientoDto>();
		StringBuffer sql = new StringBuffer();
		sql.append("");
		pbMasspay = pbMasspayment;
		try {
			//sql.append(queryEncabezadoBitalEnvioAgrupado(noEmpresa, idBanco, chequera,poHeaders, "movimiento", pbMasspayment));
			sql.append(queryEncabezadoBitalEnvioAgrupado2(poHeaders, "movimiento", pbMasspayment));
			if(pbControlArchivo) {
				sql.append("\n UNION \n");
				//sql.append(queryEncabezadoBitalEnvioAgrupado(noEmpresa, idBanco, chequera,poHeaders, "hist_solicitud", pbMasspayment));
				sql.append(queryEncabezadoBitalEnvioAgrupado2(poHeaders, "hist_solicitud", pbMasspayment));
				sql.append("\n UNION \n");
				sql.append(queryEncabezadoBitalEnvioAgrupado2(poHeaders, "hist_movimiento", pbMasspayment));
				
			}
			
			if(pbMasspayment)
				sql.append(" ORDER BY e.id_contrato_mass ");
			else
				sql.append( " ORDER BY id_chequera ");
			
			
			resList = jdbcTemplate.query(sql.toString(), new RowMapper<MovimientoDto>(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons = new MovimientoDto();
					
					if(pbMasspay) {
						cons.setImporte(rs.getDouble("suma"));
						cons.setIdDivisa(rs.getString("id_divisa"));
						cons.setNoContratoMass(rs.getString("no_contrato_mass"));
						cons.setIdContratoMass(rs.getInt("id_contrato_mass"));
					}else {
						cons.setIdBanco(rs.getInt("id_banco"));
						cons.setIdChequera(rs.getString("id_chequera"));
						cons.setDescPlaza(rs.getString("plaza"));
						cons.setImporte(rs.getDouble("importe_encabezado"));
						cons.setIdDivisa(rs.getString("id_divisa"));
						cons.setUsuarioBE(rs.getInt("usuario_BE"));
						cons.setRfc(rs.getString("RFC_originador"));
						cons.setIdContrato(rs.getInt("id_contrato"));
						cons.setNoContrato(rs.getDouble("no_contrato"));
						cons.setNomEmpresa(rs.getString("nom_empresa"));
						cons.setNoEmpresa(rs.getInt("no_empresa"));
					}
					return cons;
				}});
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Layout, C:LayoutsDao, M:encabezadoBitalEnvio");
		}
		return resList;
	}
	
	
	//revisado A.A.G
	public String queryEncabezadoBitalEnvio(String sFolios, String sTabla, boolean pbMasspayment) {
		StringBuffer sql = new StringBuffer();
		sql.append("");
		try {
				if(pbMasspayment) {
					sql.append(" SELECT distinct sum(m.importe) as suma, m.id_divisa, p.no_contrato_mass, e.id_contrato_mass");
					sql.append(" FROM ");
					sql.append( sTabla );
					sql.append(" m,empresa e, cat_contrato_mass_payment p");
					sql.append("\n	WHERE m.no_empresa = e.no_empresa");
					sql.append("\n		And e.id_contrato_mass = p.id_contrato_mass");
					
					if(sFolios.equals("TABLA_TMP")){
						sql.append("\n	");
						sql.append(tmpFoliosBE());
					}else{
						sql.append("\n		And no_folio_det in (");  
						sql.append( sFolios);
						sql.append(")");
						sql.append("\n	GROUP BY m.id_divisa, p.no_contrato_mass, e.id_contrato_mass");
					}
				}else {
					sql.append(" SELECT distinct m.id_banco, m.id_chequera, ccb.desc_plaza as plaza, sum(m.importe) as importe_encabezado,");
					sql.append("\n	m.id_divisa, b.usuario_BE, p.rfc as RFC_originador, t.id_contrato, t.no_contrato, e.nom_empresa");
					sql.append("\n FROM Persona P  Join Empresa E");
					sql.append("\n On P.No_Empresa = E.No_Empresa" );
					sql.append("\n Join " );
					sql.append( sTabla );
					sql.append("\n M On M.No_Empresa = E.No_Empresa" );
					sql.append("\n  Join Cat_Contrato_Tef t On E.Id_Contrato_Tef = t.Id_Contrato" );
					sql.append("\n  Join Cat_Banco B On B.Id_Banco = M.Id_Banco" );
					sql.append("\n   	Join Cat_Cta_Banco Ccb On (Ccb.Id_Banco = B.Id_Banco" );
					sql.append("\n 		and ccb.id_chequera = m.id_chequera) ");
					sql.append("\n WHERE ");
					sql.append("\n	 p.id_tipo_persona = 'E' and");
					
					if(sFolios.equals("TABLA_TMP"))
						sql.append( tmpFoliosBE());
					else{
						sql.append("\n	m.no_folio_det in (");
						sql.append( sFolios );
						sql.append(") ");
					}
					sql.append("\n GROUP BY m.id_banco, m.id_chequera, ccb.desc_plaza, m.id_divisa, b.usuario_BE, p.rfc, t.id_contrato, ");
					sql.append("\n	t.no_contrato, e.nom_empresa ");
				}
				System.out.println("consulta contrato treft"+sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Layout, C:LayoutsDao, M:queryEncabezadoBitalEnvio");
			return "";
		}
		return sql.toString();
	}
	
	public String queryEncabezadoBitalEnvioAgrupado(int noEmpresa, int idBanco, String chequera,
			String poHeaders, String sTabla, boolean pbMasspayment) {
		StringBuffer sql = new StringBuffer();
		sql.append("");
		try {
				if(pbMasspayment) {
					sql.append(" SELECT distinct sum(m.importe) as suma, m.id_divisa, p.no_contrato_mass, e.id_contrato_mass");
					sql.append(" FROM ");
					sql.append( sTabla );
					sql.append(" m,empresa e, cat_contrato_mass_payment p");
					sql.append("\n	WHERE m.no_empresa = e.no_empresa");
					sql.append("\n		And e.id_contrato_mass = p.id_contrato_mass");
					sql.append("\n  And M.No_Empresa =");
					sql.append(noEmpresa);
					sql.append("\n And M.Id_Banco =");
					sql.append(idBanco);
					sql.append("\n  And M.Id_Chequera = '"+chequera+"'");
					sql.append("\n   and m.po_headers = '"+poHeaders+"'");
					sql.append("\n	GROUP BY m.id_divisa, p.no_contrato_mass, e.id_contrato_mass");
					
				}else {
					sql.append(" SELECT distinct m.id_banco, m.id_chequera, ccb.desc_plaza as plaza, sum(m.importe) as importe_encabezado,");
					sql.append("\n	m.id_divisa, b.usuario_BE, p.rfc as RFC_originador, t.id_contrato, t.no_contrato, e.nom_empresa");
					sql.append("\n FROM Persona P  Join Empresa E");
					sql.append("\n On P.No_Empresa = E.No_Empresa" );
					sql.append("\n Join " );
					sql.append( sTabla );
					sql.append("\n M On M.No_Empresa = E.No_Empresa" );
					sql.append("\n  	Join Cat_Contrato_Tef t On E.Id_Contrato_Tef = t.Id_Contrato" );
					sql.append("\n  	Join Cat_Banco B On B.Id_Banco = M.Id_Banco" );
					sql.append("\n   	Join Cat_Cta_Banco Ccb On (Ccb.Id_Banco = B.Id_Banco" );
					sql.append("\n 		and ccb.id_chequera = m.id_chequera) ");
					sql.append("\n WHERE ");
					sql.append("\n	 p.id_tipo_persona = 'E' ");
					sql.append("\n  And M.No_Empresa =");
					sql.append(noEmpresa);
					sql.append("\n And M.Id_Banco =");
					sql.append(idBanco);
					sql.append("\n  And M.Id_Chequera = '"+chequera+"'");
					sql.append("\n   and m.po_headers = '"+poHeaders+"'");
					
					sql.append("\n GROUP BY m.id_banco, m.id_chequera, ccb.desc_plaza, m.id_divisa, b.usuario_BE, p.rfc, t.id_contrato, ");
					sql.append("\n	t.no_contrato, e.nom_empresa ");
				}
				System.out.println(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Layout, C:LayoutsDao, M:queryEncabezadoBitalEnvio");
			return "";
		}
		return sql.toString();
	}
	
	public String queryEncabezadoBitalEnvioAgrupado2(String poHeaders, String sTabla, boolean pbMasspayment) {
		StringBuffer sql = new StringBuffer();
		sql.append("");
		try {
				if(pbMasspayment) {
					sql.append(" SELECT distinct sum(m.importe) as suma, m.id_divisa, p.no_contrato_mass, e.id_contrato_mass");
					sql.append(" FROM ");
					sql.append( sTabla );
					sql.append(" m,empresa e, cat_contrato_mass_payment p");
					sql.append("\n	WHERE m.no_empresa = e.no_empresa");
					sql.append("\n		And e.id_contrato_mass = p.id_contrato_mass");
					
					sql.append("\n   and m.po_headers in( '"+poHeaders+"')");
					sql.append("\n	GROUP BY m.id_divisa, p.no_contrato_mass, e.id_contrato_mass");
					
				}else {
					sql.append(" SELECT distinct m.id_banco, m.id_chequera, ccb.desc_plaza as plaza, sum(m.importe) as importe_encabezado,");
					sql.append("\n	m.id_divisa, b.usuario_BE, p.rfc as RFC_originador, t.id_contrato, t.no_contrato, e.nom_empresa, M.No_Empresa");
					sql.append("\n FROM Persona P  Join Empresa E");
					sql.append("\n On P.No_Empresa = E.No_Empresa" );
					sql.append("\n Join " );
					sql.append( sTabla );
					sql.append("\n M On M.No_Empresa = E.No_Empresa" );
					sql.append("\n  	Join Cat_Contrato_Tef t On E.Id_Contrato_Tef = t.Id_Contrato" );
					sql.append("\n  	Join Cat_Banco B On B.Id_Banco = M.Id_Banco" );
					sql.append("\n   	Join Cat_Cta_Banco Ccb On (Ccb.Id_Banco = B.Id_Banco" );
					sql.append("\n 		and ccb.id_chequera = m.id_chequera) ");
					sql.append("\n WHERE ");
					sql.append("\n	 p.id_tipo_persona = 'E' ");
					
					sql.append("\n   and m.po_headers in ('"+poHeaders+"')");
					
					sql.append("\n GROUP BY m.id_banco, m.id_chequera, ccb.desc_plaza, m.id_divisa, b.usuario_BE, p.rfc, t.id_contrato, ");
					sql.append("\n	t.no_contrato, e.nom_empresa, M.No_Empresa ");
				}
				System.out.println(sql.toString());
			
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Layout, C:LayoutsDao, M:queryEncabezadoBitalEnvio");
			return "";
		}
		return sql.toString();
	}
	
	
	public int folioRealTEF(int idContrato) {
		String sql = "";
		int res = 0;
		
		try {
			sql = " UPDATE cat_contrato_tef SET tef_dia = tef_dia + 1 WHERE id_contrato = "+ idContrato +"";
			jdbcTemplate.update(sql.toString());
			
			sql = " SELECT coalesce(tef_dia, -1) as tef_dia FROM cat_contrato_tef WHERE id_contrato = "+ idContrato +"";
			res = jdbcTemplate.queryForInt(sql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Layout, C:LayoutsDao, M:folioRealTEF");
			return 0;
		}
		return res;
	}
	
	//modificado por A.A.G 5:59
	public List<MovimientoDto> detalleBitalEnvio(String sFolios, String idChequera, int idContratoMassPay, boolean pbControlArchivo) {
		List<MovimientoDto> resList = new ArrayList<MovimientoDto>();
		StringBuffer sql = new StringBuffer();
		try {
			// *** ORIGEN MOV <> FIL ***
			sql.append(queryDetalleBitalEnvio("movimiento", sFolios, idChequera, idContratoMassPay));
			if(pbControlArchivo) {
				sql.append("\n UNION \n");
				sql.append(queryDetalleBitalEnvio("hist_solicitud", sFolios, idChequera, idContratoMassPay));
				sql.append("\n UNION \n");
				sql.append(queryDetalleBitalEnvio("hist_movimiento", sFolios, idChequera, idContratoMassPay));
			}
			
		
			resList = jdbcTemplate.query(sql.toString(), new RowMapper<MovimientoDto>(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons = new MovimientoDto();
					
					cons.setFecValorOriginal(rs.getDate("fec_valor_original"));
					cons.setIdDivisa(rs.getString("id_divisa"));
					cons.setClabe(rs.getString("id_clabe"));
					cons.setIdChequera(rs.getString("id_chequera"));
					cons.setNoCliente(rs.getString("no_cliente"));
					cons.setIdBanco(rs.getInt("id_banco"));
					cons.setImporte(rs.getDouble("importe"));
					cons.setIdBancoBenef(rs.getInt("id_banco_benef"));
					cons.setPlazaBenef(rs.getString("plaza_benef"));
					cons.setIdChequeraBenef(rs.getString("id_chequera_benef"));
					cons.setSucDestino(rs.getString("sucursal_benef"));
					cons.setBeneficiario(rs.getString("beneficiario"));
					cons.setNoFolioDet(rs.getInt("no_folio_det"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setFecValor(rs.getDate("fec_valor"));
					cons.setNoDocto(rs.getString("no_docto"));
					cons.setInstFinan(rs.getString("inst_finan"));
					cons.setRfcBenef(rs.getString("RFC_Beneficiario"));
					cons.setObservacion(rs.getString("observacion"));
					cons.setNoFactura(rs.getString("no_factura"));
					cons.setNomEmpresa(rs.getString("nom_empresa"));
					cons.setIdBancoCity(rs.getInt("id_banco_city"));
					cons.setClabeBenef(rs.getString("clabe_benef"));
					cons.setSwiftCode(rs.getString("swift_benef"));
					cons.setAba(rs.getString("aba_benef"));
					cons.setClave(rs.getString("clabe_origen"));
					cons.setRfc(rs.getString("rfc_origen"));
					cons.setTipoMovto(rs.getString("tipo_movto"));
					return cons;
				}});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Layout, C:LayoutsDao, M:detalleBitalEnvio");
		}
		return resList;
	}
	
	public List<MovimientoDto> detalleBitalEnvioAgrupado(String sFolios, String idChequera, int idContratoMassPay, boolean pbControlArchivo) {
		List<MovimientoDto> resList = new ArrayList<MovimientoDto>();
		StringBuffer sql = new StringBuffer();
		try {
			// *** ORIGEN MOV <> FIL ***
			sql.append(queryDetalleBitalEnvioAgrupado("movimiento", sFolios, idChequera, idContratoMassPay));
			if(pbControlArchivo) {
				sql.append("\n UNION \n");
				sql.append(queryDetalleBitalEnvioAgrupado("hist_solicitud", sFolios, idChequera, idContratoMassPay));
				sql.append("\n UNION \n");
				sql.append(queryDetalleBitalEnvioAgrupado("hist_movimiento", sFolios, idChequera, idContratoMassPay));
			}
			
		
			resList = jdbcTemplate.query(sql.toString(), new RowMapper<MovimientoDto>(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons = new MovimientoDto();
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setFecValorOriginal(rs.getDate("fec_valor_original"));
					cons.setIdDivisa(rs.getString("id_divisa"));
					cons.setClabe(rs.getString("id_clabe"));
					cons.setIdChequera(rs.getString("id_chequera"));
					cons.setNoCliente(rs.getString("no_cliente"));
					cons.setIdBanco(rs.getInt("id_banco"));
					cons.setImporte(rs.getDouble("importe"));
					cons.setIdBancoBenef(rs.getInt("id_banco_benef"));
					cons.setPlazaBenef(rs.getString("plaza_benef"));
					cons.setIdChequeraBenef(rs.getString("id_chequera_benef"));
					cons.setSucDestino(rs.getString("sucursal_benef"));
					cons.setBeneficiario(rs.getString("beneficiario"));
					//cons.setNoFolioDet(rs.getInt("no_folio_det"));
					cons.setPoHeaders(rs.getString("po_headers"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setFecValor(rs.getDate("Fec_Propuesta"));
					cons.setNoDocto(rs.getString("no_docto"));
					cons.setInstFinan(rs.getString("inst_finan"));
					cons.setRfcBenef(rs.getString("RFC_Beneficiario"));
					cons.setObservacion(rs.getString("observacion"));
					cons.setNoFactura(rs.getString("no_factura"));
					cons.setNomEmpresa(rs.getString("nom_empresa"));
					cons.setIdBancoCity(rs.getInt("id_banco_city"));
					cons.setClabeBenef(rs.getString("clabe_benef"));
					cons.setSwiftCode(rs.getString("swift_benef"));
					cons.setAba(rs.getString("aba_benef"));
					cons.setClave(rs.getString("clabe_origen"));
					cons.setRfc(rs.getString("rfc_origen"));
					cons.setTipoMovto(rs.getString("tipo_movto"));
					return cons;
				}});
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Layout, C:LayoutsDao, M:detalleBitalEnvio");
		}
		return resList;
	}
	
	//Modificado por A.A.G
		public String queryDetalleBitalEnvioAgrupado(String sTabla, String sFolios, String idChequera, int idContratoMassPay) {
			StringBuffer sql = new StringBuffer();
			try {
				
				// *** Transferencias ***
				sql.append("\n		Select Po_Headers,Fec_Valor_Original,Id_Divisa,Id_Clabe,Id_Chequera,No_Cliente,");
				sql.append("\n		Id_Banco,sum(Importe) as importe,Id_Banco_Benef,Plaza_Benef,Id_Chequera_Benef,");
				sql.append("\n		Sucursal_Benef,Beneficiario,No_Folio_Det,Fec_Propuesta,No_Docto,");
				sql.append("\n		Inst_Finan,Rfc_Beneficiario,Concepto,Observacion,No_Factura,");
				sql.append("\n		Nom_Empresa,Id_Banco_City,Clabe_Benef,Rfc_Benef,");
				sql.append("\n		Swift_Benef,Aba_Benef,Clabe_Origen,Rfc_Origen,Tipo_Movto, no_empresa");
				sql.append("\n		 from (");
				sql.append("\n		");
				sql.append("\n		Select  M.Po_Headers,M.Fec_Valor_Original,M.Id_Divisa,C.Id_Clabe, M.Id_Chequera,M.No_Cliente,");
				sql.append("\n		m.id_banco,m.importe,m.id_banco_benef,Cast(C.Plaza As Varchar2 (10)) As Plaza_Benef,");
				sql.append("\n		m.id_chequera_benef,cast(c.sucursal as varchar2(10)) as sucursal_benef,m.beneficiario,");
				sql.append("\n		m.po_headers as no_folio_det, M.Fec_Propuesta, M.po_headers as No_Docto,Cb.Inst_Finan,");
				sql.append("\n		p.rfc as RFC_Beneficiario, 'pago' as concepto, m.observacion,'' as no_factura,");
				sql.append("\n		e.nom_empresa,cb.id_banco_city, c.id_clabe as clabe_benef, p.rfc as rfc_benef,");
				sql.append("\n		c.swift_code as swift_benef, c.aba as aba_benef,ch.id_clabe as clabe_origen,");
				sql.append("\n		p2.rfc as rfc_origen, 'TRANSFERENCIA' as tipo_movto, m.no_empresa");
			
				sql.append("\n 		FROM ");
				sql.append(sTabla);
				sql.append(" m Left Join  Cat_Banco Cb On Cb.Id_Banco = M.Id_Banco");
				sql.append("\n		Left Join Ctas_Banco C On (Cast(M.No_Cliente As Integer) = C.No_Persona");
				sql.append("\n									And  M.Id_Chequera_Benef = C.Id_Chequera");
				sql.append("\n									and  m.id_banco_benef = c.id_banco),");
				sql.append("\n									persona p,empresa e,cat_cta_banco ch, persona p2");
				sql.append("\n WHERE m.id_chequera = ch.id_chequera");
				sql.append("\n   	and m.id_banco = ch.id_banco");
				sql.append("\n   	and m.no_empresa = p2.no_empresa");
				sql.append("\n   	and p2.id_tipo_persona =  'E'");
				//sql.append("\n   	and m.no_empresa = e.no_empresa");
				sql.append("\n   	And Cast(m.No_Cliente As Integer) = P.No_Persona  ");
				sql.append("\n 		and p.id_tipo_persona in('P','K')  ");
			
				if(sFolios.equals("TABLA_TMP"))
					sql.append( tmpFoliosBE());
				else{
					sql.append("\n		and m.po_headers in ('");
					sql.append( sFolios );
					sql.append("')");
				}
				if(!idChequera.equals("")){ 
					sql.append("\n   and m.id_chequera = '"); 
					sql.append(idChequera);
					sql.append("'");
				}
				sql.append("\n		and coalesce(m.origen_mov,'') <> 'FIL'");
				
				if(idContratoMassPay > 0) {
					sql.append("\n	and e.id_contrato_mass = ");
					sql.append(idContratoMassPay);
				}
				sql.append("\n		and m.no_cliente not in(");
				sql.append(consultarConfiguraSet(206));
				sql.append(")");
				sql.append("\n		and m.id_tipo_operacion not between 3700 and 3899");
				
				sql.append("\n		 Group By M.Po_Headers,M.Fec_Valor_Original,M.Id_Divisa,C.Id_Clabe, M.Id_Chequera,M.No_Cliente,");
				sql.append("\n		m.id_banco,m.importe,m.id_banco_benef,C.Plaza ,");
				sql.append("\n		M.Id_Chequera_Benef,C.Sucursal,M.Beneficiario,");
				sql.append("\n		m.po_headers, M.Fec_Propuesta, m.no_docto,cb.inst_finan,");
				sql.append("\n		p.rfc ,'', m.observacion,'',");
				sql.append("\n		e.nom_empresa,cb.id_banco_city, c.id_clabe , p.rfc ,");
				sql.append("\n		C.Swift_Code , C.Aba ,Ch.Id_Clabe ,");
				sql.append("\n		P2.Rfc , '',m.no_empresa");
				sql.append("\n		");
				sql.append("\n		) X");
				sql.append("\n		group by");
				sql.append("\n		Po_Headers,Fec_Valor_Original,Id_Divisa,Id_Clabe,Id_Chequera,No_Cliente,");
				sql.append("\n		Id_Banco,Id_Banco_Benef,Plaza_Benef,Id_Chequera_Benef,");
				sql.append("\n		Sucursal_Benef,Beneficiario,No_Folio_Det,Fec_Propuesta,No_Docto,");
				sql.append("\n		Inst_Finan,Rfc_Beneficiario,Concepto,Observacion,No_Factura,");
				sql.append("\n		Nom_Empresa,Id_Banco_City,Clabe_Benef,Rfc_Benef,");
				sql.append("\n		Swift_Benef,Aba_Benef,Clabe_Origen,Rfc_Origen,Tipo_Movto, no_empresa");
				sql.append("\n		");
					
				System.out.println("\n");
			System.out.println("\nesta es la consulta que falla y se va arreglar"+sql+"\n \n");
			}catch(Exception e) {
				e.printStackTrace();
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Layout, C:LayoutsDao, M:queryDetalleBitalEnvio");
				return "";
			}
			return sql.toString();
		}
	//Modificado por A.A.G
	public String queryDetalleBitalEnvio(String sTabla, String sFolios, String idChequera, int idContratoMassPay) {
		StringBuffer sql = new StringBuffer();
		try {
			
			// *** Transferencias ***
			sql.append("");
			sql.append(" SELECT distinct m.fec_valor_original,m.id_divisa,c.id_clabe, m.id_chequera,m.no_cliente,");
			sql.append("\n		m.id_banco,m.importe,m.id_banco_benef,Cast(C.Plaza As Varchar2 (10)) As Plaza_Benef,");
			sql.append("\n  	m.id_chequera_benef,cast(c.sucursal as varchar2(10)) as sucursal_benef,m.beneficiario,");
			sql.append("\n  	m.no_folio_det , m.concepto,m.fec_valor, m.no_docto,cb.inst_finan,");
			sql.append("\n  	p.rfc as RFC_Beneficiario, m.concepto, m.observacion,m.no_factura,");
			sql.append("\n  	e.nom_empresa,cb.id_banco_city, c.id_clabe as clabe_benef, p.rfc as rfc_benef,");
			sql.append("\n  	c.swift_code as swift_benef, c.aba as aba_benef,ch.id_clabe as clabe_origen,");
			sql.append("\n  	p2.rfc as rfc_origen, 'TRANSFERENCIA' as tipo_movto");
			sql.append("\n 		FROM ");
			sql.append(sTabla);
			sql.append(" m Left Join  Cat_Banco Cb On Cb.Id_Banco = M.Id_Banco");
			sql.append("\n		Left Join Ctas_Banco C On (Cast(M.No_Cliente As Integer) = C.No_Persona");
			sql.append("\n									And  M.Id_Chequera_Benef = C.Id_Chequera");
			sql.append("\n									and  m.id_banco_benef = c.id_banco),");
			sql.append("\n									persona p,empresa e,cat_cta_banco ch, persona p2");
			sql.append("\n WHERE m.id_chequera = ch.id_chequera");
			sql.append("\n   	and m.id_banco = ch.id_banco");
			sql.append("\n   	and m.no_empresa = p2.no_empresa");
			sql.append("\n   	and p2.id_tipo_persona =  'E'");
			sql.append("\n   	and m.no_empresa = e.no_empresa");
			sql.append("\n   	And Cast(m.No_Cliente As Integer) = P.No_Persona  ");
			sql.append("\n 		and p.id_tipo_persona in('P','K')  ");
		
			if(sFolios.equals("TABLA_TMP"))
				sql.append( tmpFoliosBE());
			else{
				sql.append("\n		and m.no_folio_det in (");
				sql.append( sFolios );
				sql.append(")");
			}
			if(!idChequera.equals("")){ 
				sql.append("\n   and m.id_chequera = '"); 
				sql.append(idChequera);
				sql.append("'");
			}
			sql.append("\n		and coalesce(m.origen_mov,'') <> 'FIL'");
			
			if(idContratoMassPay > 0) {
				sql.append("\n	and e.id_contrato_mass = ");
				sql.append(idContratoMassPay);
			}
			sql.append("\n		and m.no_cliente not in(");
			sql.append(consultarConfiguraSet(206));
			sql.append(")");
			sql.append("\n		and m.id_tipo_operacion not between 3700 and 3899");
			
			// *** Traspasos ***
			sql.append("\n  UNION");
			sql.append("\n  Select Distinct M.Fec_Valor_Original,M.Id_Divisa,C.Id_Clabe,M.Id_Chequera,M.No_Cliente,");
			sql.append("\n		M.Id_Banco,M.Importe,M.Id_Banco_Benef,Ch.Desc_Plaza As Plaza_Benef,M.Id_Chequera_Benef,");
			sql.append("\n  	ch.desc_sucursal as sucursal_benef,m.beneficiario,m.no_folio_det, m.concepto,m.fec_valor,");
			sql.append("\n  	m.no_docto,cb.inst_finan,p.rfc as RFC_Beneficiario, m.concepto, m.observacion,m.no_factura,");
			sql.append("\n  	e.nom_empresa,cb.id_banco_city,c.id_clabe as clabe_benef,'AAA999999AAA' as rfc_benef,");  //Todas son Morales
			sql.append("\n  	C.Swift_Code As Swift_Benef, C.Aba As Aba_Benef,Ch.Id_Clabe As Clabe_Origen, P2.Rfc As Rfc_Origen, 'TRASPASO' As Tipo_Movto");
			sql.append("\n 		FROM ");
			sql.append(sTabla);
			sql.append(	" M Left Join  Cat_Banco Cb On Cb.Id_Banco = M.Id_Banco");
			sql.append("\n		Left Join Ctas_Banco C On (Cast(M.No_Cliente As Integer) = C.No_Persona");
			sql.append("\n      And  M.Id_Chequera_Benef = C.Id_Chequera ");
			sql.append("\n      And  M.Id_Banco_Benef = C.Id_Banco),");
			sql.append("\n      persona p,empresa e,cat_cta_banco ch, persona p2");
			
			sql.append("\n  WHERE m.id_chequera = ch.id_chequera");
			sql.append("\n   	and m.id_banco = ch.id_banco");
			sql.append("\n   	and m.no_empresa = p2.no_empresa");
			sql.append("\n   	and p2.id_tipo_persona =  'E'");
			sql.append("\n   	and m.no_empresa = e.no_empresa");
			sql.append("\n  	and p.no_persona = m.no_empresa");
			sql.append("\n  	and p.id_tipo_persona = 'E'");
		
			
			if(sFolios.equals("TABLA_TMP")){
				sql.append("\n	");
				sql.append(tmpFoliosBE());
			}
			else{
				sql.append("\n   and m.no_folio_det in (");
				sql.append(sFolios);
				sql.append(")");
			}
			if(!idChequera.equals("")){
				sql.append("\n   and m.id_chequera = '"); 
				sql.append(idChequera);
				sql.append("'");
			}
			sql.append("\n   and coalesce(m.origen_mov,'') <> 'FIL'");
			
			if(idContratoMassPay > 0){
				sql.append("\n   and e.id_contrato_mass = " );
			    sql.append(idContratoMassPay);
			    sql.append(" ");
			}
			sql.append("\n   and m.no_cliente not in("); 
			sql.append(consultarConfiguraSet(206));
			sql.append(")");
			sql.append("\n   and m.id_tipo_operacion between 3700 and 3899");
			// *** ORIGEN MOV = FIL ***
			
			sql.append("\n   UNION");
			sql.append("\n   SELECT distinct m.fec_valor_original,m.id_divisa,'' as id_clabe,");
			sql.append("\n M.Id_Chequera,M.No_Cliente,M.Id_Banco,M.Importe,M.Id_Banco_Benef,Cast(M.Plaza As Varchar(10)) As Plaza_Benef,");
			sql.append("\n  m.id_chequera_benef,cast(m.sucursal as varchar(10)) as sucursal_benef, m.beneficiario,");
			sql.append("\n  m.no_folio_det , m.concepto,m.fec_valor, m.no_docto,cb.inst_finan,");
			sql.append("\n  '' as RFC_Beneficiario, m.concepto, m.observacion ,m.no_factura");
			sql.append("\n  ,e.nom_empresa,cb.id_banco_city");
			sql.append("\n  ,m.clabe as clabe_benef");
			sql.append("\n  ,'AAAA999999AAA' as rfc_benef");  //Todas son Fisicas
			sql.append("\n  ,'' as swift_benef");
			sql.append("\n  ,'' as aba_benef");
			sql.append("\n  ,ch.id_clabe as clabe_origen");
			sql.append("\n  ,p2.rfc as rfc_origen, 'TRASPASO' as tipo_movto");
			sql.append("\n  FROM ");
			sql.append(sTabla);
			sql.append(" M left join Cat_Banco Cb on  m.id_banco_benef = cb.id_banco,");
			sql.append("\n            empresa e, cat_cta_banco ch, persona p2");
			sql.append("\n  WHERE");
			sql.append("\n   m.id_chequera = ch.id_chequera");
			sql.append("\n   and m.id_banco = ch.id_banco");
			sql.append("\n   and m.no_empresa = p2.no_empresa");
			sql.append("\n   and p2.id_tipo_persona =  'E'");
			sql.append("\n   and m.no_empresa = e.no_empresa");
			
			
			if(sFolios.equals("TABLA_TMP")){
				sql.append("\n	");
				sql.append(tmpFoliosBE());
			}
			else{
				sql.append("\n  and m.no_folio_det in (");
				sql.append(sFolios);
				sql.append(")");
			}
			if(!idChequera.equals("")){
			    sql.append("\n   and m.id_chequera = '"); 
				sql.append(idChequera);
				sql.append("'");
			}
			
			if(idContratoMassPay > 0){
			    sql.append("\n   and e.id_contrato_mass = " );
			    sql.append(idContratoMassPay);
			    sql.append(" ");
			}
			sql.append("\n   and ( m.origen_mov = 'FIL'");
			sql.append("\n   or m.no_cliente in(" );
			sql.append(consultarConfiguraSet(206));
			sql.append(")");
			sql.append("\n   )");
			sql.append("\n   and m.id_tipo_operacion NOT BETWEEN 3700 and 3899");
			System.out.println("\n");
		System.out.println("\nesta es la consulta que falla y se va arreglar"+sql+"\n \n");
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Layout, C:LayoutsDao, M:queryDetalleBitalEnvio");
			return "";
		}
		return sql.toString();
	}

	public String tmpFoliosBE() {
		globalSingleton = GlobalSingleton.getInstancia();
		return " and m.no_folio_det in (Select t.no_folio_det From tmp_folios_be t WHERE t.no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + ")";
	}
	//Actualizado A.A.G
	public int insertDetArchTransfer(DetArchTransferDto dto) {
		StringBuffer sql = new StringBuffer();
		int res = 0;
		try {
			sql.append(" INSERT INTO det_arch_transfer(nom_arch, no_folio_det, no_docto, id_estatus_arch,");
			sql.append("\n		fec_valor, id_chequera, id_banco, id_banco_benef, id_chequera_benef,");
			sql.append("\n		prefijo_benef, importe, beneficiario, sucursal, plaza, concepto)");
			sql.append("\n VALUES('" + dto.getNomArch().substring(0,8) + "', " + dto.getNoFolioDet() + ", " + dto.getNoDocto() + ",");
			sql.append("\n		'" + dto.getIdEstatusArch() + "', '" + funciones.ponerFormatoDate(dto.getFecValorDate()) + "',");
			sql.append("\n		'" + dto.getIdChequera() + "', " + dto.getIdBanco() + ", " + dto.getIdBancoBenef() + ",");
			sql.append("\n		'" + dto.getIdChequeraBenef() + "', '" + dto.getPrefijoBenef() + "', " + dto.getImporte() + ",");
			sql.append("\n		'" + dto.getBeneficiario().replace("'", "") + "', " + dto.getSucursal() + "," + dto.getPlaza() + ",");
			sql.append("\n		'" + dto.getConcepto() + "')");
			
		    res = jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectr�nica, C:EnvioTransferenciasDao, M:insertDetArchTransfer");
		}
		return res;
	}
	//Actualizado A.A.G
	public int insertArchTransfer(String nomArchivo, int idBanco, String fecTrans, double dImporte, int piRegistros) {
		StringBuffer sql = new StringBuffer();
		int res = 0;
		globalSingleton = GlobalSingleton.getInstancia();
		try {
			sql.append(" INSERT INTO arch_transfer(nom_arch, id_banco, fec_trans, importe, registros, fec_retrans,");
			sql.append("\n 		no_usuario_alta, no_usuario_modif, b_cheque_ocurre)");
			sql.append("\n VALUES('" + nomArchivo.substring(0,8) + "', " + idBanco + ", '" + fecTrans + "',");
			sql.append("\n		" + dImporte + ", " + piRegistros + ", null, " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", null, null)");
		    
		    res = jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectr�nica, C:EnvioTransferenciasDao, M:insertDetArchTransfer");
		}
		return res;
	}
	
	//Actualizado A.A.G
	public List<ParametrosLayoutDto> obtieneChequeraBenef(int noCliente) {
		List<ParametrosLayoutDto> lista = new ArrayList<ParametrosLayoutDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("Select * from ctas_banco ");
			sql.append("\n where no_persona = " + noCliente + " ");
			lista = jdbcTemplate.query(sql.toString(), new RowMapper<ParametrosLayoutDto>()	{
				public ParametrosLayoutDto mapRow(ResultSet rs, int idx) throws SQLException {
					ParametrosLayoutDto campos = new ParametrosLayoutDto();					
					campos.setIdChequeraBenef(rs.getString("id_chequera"));										
					return campos;			
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Layout, C:LayoutsDao, M:obtieneChequeraBenef");
		}
		return lista;
	}
	
	//Actualizado A.A.G
	public List<ParametrosLayoutDto> obtieneReferencia(int noCliente) {
		List<ParametrosLayoutDto> lista = new ArrayList<ParametrosLayoutDto>();
		StringBuffer sql = new StringBuffer();
		try
		{
			
			sql.append("Select coalesce(referencia_cte, '') as referencia_cte, equivale_persona from persona ");
			sql.append("\n where no_persona = ");
			sql.append(noCliente);
			sql.append(" ");
			sql.append("\n and id_tipo_persona = 'P' ");
			lista = jdbcTemplate.query(sql.toString(), new RowMapper<ParametrosLayoutDto>(){
				public ParametrosLayoutDto mapRow(ResultSet rs, int idx) throws SQLException 
				{
					ParametrosLayoutDto campos = new ParametrosLayoutDto();						
					campos.setReferenciaCte(rs.getString("referencia_cte"));
					campos.setEquivalePersona(rs.getString("equivale_persona"));
					return campos;
				}
			});	
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Layout, C:LayoutsDao, M:obtieneReferencia");
		}
		return lista;
	}
	
	//Actualizado A.A.G
	public List<ParametrosLayoutDto> obtieneFechaHoy() {
		List<ParametrosLayoutDto> lista = new ArrayList<ParametrosLayoutDto>();		
		String sql = "";		
		try{
			sql = "Select * from fechas ";
			lista = jdbcTemplate.query(sql, new RowMapper<ParametrosLayoutDto>(){
				public ParametrosLayoutDto mapRow(ResultSet rs, int idx) throws SQLException {
					ParametrosLayoutDto campos = new ParametrosLayoutDto();					
					campos.setFecHoy(rs.getDate("fec_hoy"));										
					return campos;			
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Layout, C:LayoutsDao, M:obtieneFechaHoy");
		}
		return lista;
	}
	
	
	//Actualizado A.A.G
	public List<ParametrosLayoutDto> obtieneDivisa(String psDivisa, String tipo){
		List<ParametrosLayoutDto> lista = new ArrayList<ParametrosLayoutDto>();		
		StringBuffer sql = new StringBuffer();		
		try	{
			sql.append("Select codDivisaERP as divisa ");
			sql.append("\n from desc_refcr_divisas ");
			sql.append("\n where codDivisaSET = '");
			sql.append(psDivisa);
			sql.append("' ");
			sql.append("\n and codEMP = '" + tipo + "'");
								
			lista = jdbcTemplate.query(sql.toString(), new RowMapper<ParametrosLayoutDto>() 
			{
				public ParametrosLayoutDto mapRow(ResultSet rs, int idx) throws SQLException 
				{
					ParametrosLayoutDto campos = new ParametrosLayoutDto();					
					campos.setIdDivisa(rs.getString("divisa"));										
					return campos;			
				}
			});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Layout, C:LayoutsDao, M:concepto");
		}
		return lista;
	}
	
	//Actualizado A.A.G
	public List<ParametrosLayoutDto> obtieneTipoDePago(int pbFolio) 
	{
		List<ParametrosLayoutDto> lista = new ArrayList<ParametrosLayoutDto>();		
		String sql = "";		
		try
		{
			sql = "Select * from movimiento ";
			sql += "\n where no_folio_det in ('" + pbFolio + "') ";
			sql += "\n union ";
			sql += "\n Select * from hist_movimiento ";
			sql += "\n where no_folio_det in ('" + pbFolio + "') ";
								
			lista = jdbcTemplate.query(sql, new RowMapper<ParametrosLayoutDto>() 
			{
				public ParametrosLayoutDto mapRow(ResultSet rs, int idx) throws SQLException 
				{
					ParametrosLayoutDto campos = new ParametrosLayoutDto();					
					campos.setIdFormaPago(rs.getInt("id_forma_pago"));										
					return campos;			
				}
			});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Layout, C:LayoutsDao, M:concepto");
		}
		return lista;
	}
	
	public List<String> obtieneDatosBancoMovimientoFolioDet(int noFolioDet){
		List<List<String>> obtieneDatosBanco = new ArrayList<>();
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("\n	Select (case when Ccb.Aba is null or Ccb.Aba = '' then ccb.swift_code else Ccb.Aba end) as aba,cb.desc_banco descripcion_banco,M.Concepto,");
			sql.append("\n	(Case When M.Referencia = '' Or M.Referencia Is Null Then M.Concepto Else M.Referencia End) As Referencia, M.Beneficiario, D.Calle_No,");
			sql.append("\n	(select mc.contacto from Medios_Contacto mc where");
			sql.append("\n	Mc.No_Persona = M.No_Cliente ");
			sql.append("\n	And Mc.Id_Tipo_Persona = 'P' ");
			sql.append("\n	and mc.id_tipo_medio = 'TOF1') as numero");
			sql.append("\n	From Movimiento M");
			sql.append("\n	Join Ctas_Banco Ccb On M.Id_Chequera_Benef = Ccb.Id_Chequera");
			sql.append("\n	join cat_banco cb on m.id_banco_benef = cb.id_banco, direccion d");
			sql.append("\n	Where M.No_Folio_Det = ");
			sql.append(noFolioDet);
			sql.append("\n	And M.No_Cliente = D.No_Persona");
			sql.append("\n	And Ccb.Id_Tipo_Persona = 'P'");
			
			System.out.println("obtiene desc banco movimiento"+sql);
			obtieneDatosBanco = jdbcTemplate.query(sql.toString(), new RowMapper<List<String>>() {
				public List<String> mapRow(ResultSet rs, int idx) throws SQLException {
					List<String> obtieneDatosBanco = new ArrayList<>();
					obtieneDatosBanco.add(rs.getString("aba"));
					obtieneDatosBanco.add(rs.getString("descripcion_banco"));
					obtieneDatosBanco.add(rs.getString("Concepto"));
					obtieneDatosBanco.add(rs.getString("Referencia"));
					obtieneDatosBanco.add(rs.getString("beneficiario"));
					obtieneDatosBanco.add(rs.getString("Calle_No"));
					obtieneDatosBanco.add(rs.getString("numero"));
					return obtieneDatosBanco; 			
				}
			});
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Layout, C:LayoutsDao, M:obtieneDatosBancoMovimientoFolioDet");
			e.printStackTrace();
		}
		return !obtieneDatosBanco.isEmpty()  ? obtieneDatosBanco.get(0): new ArrayList<String>();
	}
	
	
	public List<String> obtieneDatosBancoMovimientoFolioDet2(int noFolioDet){
		List<List<String>> obtieneDatosBanco = new ArrayList<>();
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("\n	Select (case when Ccb.Aba is null or Ccb.Aba = '' then ccb.swift_code else Ccb.Aba end) as aba,cb.desc_banco descripcion_banco,M.Concepto,");
			sql.append("\n	(Case When M.Referencia = '' Or M.Referencia Is Null Then M.Concepto Else M.Referencia End) As Referencia");
			sql.append("\n	From Movimiento M");
			sql.append("\n	Join cat_cta_banco Ccb On M.Id_Chequera_Benef = Ccb.Id_Chequera");
			sql.append("\n	join cat_banco cb on m.id_banco_benef = cb.id_banco");
			sql.append("\n	Where M.No_Folio_Det = ");
			sql.append(noFolioDet);
			System.out.println("obtiene desc banco movimiento"+sql);
			obtieneDatosBanco = jdbcTemplate.query(sql.toString(), new RowMapper<List<String>>() {
				public List<String> mapRow(ResultSet rs, int idx) throws SQLException {
					List<String> obtieneDatosBanco = new ArrayList<>();
					obtieneDatosBanco.add(rs.getString("aba"));
					obtieneDatosBanco.add(rs.getString("descripcion_banco"));
					obtieneDatosBanco.add(rs.getString("Concepto"));
					obtieneDatosBanco.add(rs.getString("Referencia"));
					return obtieneDatosBanco; 			
				}
			});
			/*
			sql.append("\n	Select (case when Ccb.Aba is null or Ccb.Aba = '' then ccb.swift_code else Ccb.Aba end) as aba,cb.desc_banco descripcion_banco,M.Concepto,");
			sql.append("\n	(Case When M.Referencia = '' Or M.Referencia Is Null Then M.Concepto Else M.Referencia End) As Referencia, M.Beneficiario, D.Calle_No,");
			sql.append("\n	(select mc.contacto from Medios_Contacto mc where");
			sql.append("\n	Mc.No_Persona = M.No_Cliente ");
			sql.append("\n	And Mc.Id_Tipo_Persona = 'P' ");
			sql.append("\n	and mc.id_tipo_medio = 'TOF1') as numero");
			sql.append("\n	From Movimiento M");
			sql.append("\n	Join cat_cta_banco Ccb On M.Id_Chequera_Benef = Ccb.Id_Chequera");
			sql.append("\n	join cat_banco cb on m.id_banco_benef = cb.id_banco, direccion d");
			sql.append("\n	Where M.No_Folio_Det = ");
			sql.append(noFolioDet);
			sql.append("\n	And M.No_Cliente = D.No_Persona");
			sql.append("\n	And Ccb.Id_Tipo_Persona = 'P'");
			*/
			System.out.println("obtiene desc banco movimiento"+sql);
			obtieneDatosBanco = jdbcTemplate.query(sql.toString(), new RowMapper<List<String>>() {
				public List<String> mapRow(ResultSet rs, int idx) throws SQLException {
					List<String> obtieneDatosBanco = new ArrayList<>();
					obtieneDatosBanco.add(rs.getString("aba"));
					obtieneDatosBanco.add(rs.getString("descripcion_banco"));
					obtieneDatosBanco.add(rs.getString("Concepto"));
					obtieneDatosBanco.add(rs.getString("Referencia"));
					obtieneDatosBanco.add(rs.getString("beneficiario"));
					obtieneDatosBanco.add(rs.getString("Calle_No"));
					obtieneDatosBanco.add(rs.getString("numero"));
					return obtieneDatosBanco; 			
				}
			});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Layout, C:LayoutsDao, M:obtieneDatosBancoMovimientoFolioDet");
			e.printStackTrace();
		}
		return !obtieneDatosBanco.isEmpty()  ? obtieneDatosBanco.get(0): new ArrayList<String>();
	}
	public List<String> obtieneDatosBanco (int idBanco){
		List<String> obtieneDatosBanco = new ArrayList<>();
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("Select Desc_Banco From Cat_Banco Where Id_Banco = ");
			sql.append(idBanco);
			
			obtieneDatosBanco = jdbcTemplate.query(sql.toString(), new RowMapper<String>() {
				public String mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getString("Desc_Banco"); 			
				}
			});
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Layout, C:LayoutsDao, M:obtieneDatosBanco");
		}
		return obtieneDatosBanco;
	}
	
	//Actualizado A.A.G
	public List<ParametrosLayoutDto> obtieneDatosPersona(int piNoPersona) {
		String sql = "";
		List<ParametrosLayoutDto> lista = new ArrayList<ParametrosLayoutDto>();
		
		try
		{
			sql = "Select coalesce(rfc, '') as rfc_c, coalesce(curp, '') as curp_c, * from persona ";
			sql += " where no_persona = " + piNoPersona + " or equivale_persona like '%" + piNoPersona + "%' ";
			sql += " and id_tipo_persona = 'P' ";
			
			lista = jdbcTemplate.query(sql, new RowMapper<ParametrosLayoutDto>()
			{
				public ParametrosLayoutDto mapRow(ResultSet rs, int idx) throws SQLException
				{
					ParametrosLayoutDto campos = new ParametrosLayoutDto();
					campos.setRfc(rs.getString("rfc_c"));
					campos.setCurp(rs.getString("curp_c"));
					return campos;
				}
			});		
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Layout, C:LayoutsDao, M:obtieneDatosPersona");
		}return lista;
	}
	
	public int deleteTMPFoliosBE() {
		String sql = "";
		int res = 0;
		globalSingleton = GlobalSingleton.getInstancia();
		
		try {
			sql = "DELETE FROM tmp_folios_be WHERE no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + "";
			
			res = jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Layout, C:LayoutsDao, M:deleteTMPFoliosBE");
		}
		return res;
	}
	
	public int insertTMPFoliosBE(String sFolioDet) {
		String sql = "";
		int res = 0;
		globalSingleton = GlobalSingleton.getInstancia();
		
		try {
			sql = "INSERT INTO tmp_folios_be (no_usuario, no_folio_det)";
			sql += "\n VALUES(" + globalSingleton.getUsuarioLoginDto().getIdUsuario() + ", " + sFolioDet + ")";
			
			res = jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Layout, C:LayoutsDao, M:insertTMPFoliosBE");
		}
		return res;
	}
	
	//Actualizado A.A.G
	public String selectCorreo(String noCliente) {
		String res = "";
		String sql = "";
		List<ParametrosLayoutDto> lista = new ArrayList<ParametrosLayoutDto>();
		
		try {
			sql = "SELECT coalesce(contacto, '') as contacto FROM medios_contacto";
			sql += "\n WHERE no_persona = " + noCliente + "";
			sql += "\n	And id_tipo_medio = 'MAIL'";
			sql += "\n	And id_tipo_persona = 'P'";
			
			lista = jdbcTemplate.query(sql, new RowMapper<ParametrosLayoutDto>() {
				public ParametrosLayoutDto mapRow(ResultSet rs, int idx) throws SQLException {
					ParametrosLayoutDto campos = new ParametrosLayoutDto();
					campos.setConcepto(rs.getString("contacto"));
					return campos;
				}
			});
			
			if(lista.size() > 0) res = lista.get(0).getConcepto().trim();
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Layout, C:LayoutsDao, M:selectCorreo");
			return "";
		}
		return res;
	}
	
	
	public List<ParametrosLayoutDto> buscaNomEmpresa(int noFolioDet){
		StringBuffer sql = new StringBuffer();
		List<ParametrosLayoutDto> lista = new ArrayList<ParametrosLayoutDto>();
		
		try {
			sql.append(" SELECT case when LEN(razon_social) > 40 then SUBSTRING(razon_social, 1, 41) else razon_social end as razon_social, rfc \n");
			sql.append(" FROM persona \n");
			sql.append(" WHERE no_empresa in (SELECT no_empresa FROM movimiento WHERE no_folio_det = "+ noFolioDet +" ) \n");
			
			lista = jdbcTemplate.query(sql.toString(), new RowMapper<ParametrosLayoutDto>() {
				public ParametrosLayoutDto mapRow(ResultSet rs, int idx) throws SQLException {
					ParametrosLayoutDto campos = new ParametrosLayoutDto();
					campos.setNomEmpresa(rs.getString("razon_social"));
					campos.setRfc(rs.getString("rfc"));
					return campos;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectr�nica, C:EnvioTransferenciasDao, M:setDataSource");
			return lista;
		}
		return lista;
	}
	
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectr�nica, C:EnvioTransferenciasDao, M:setDataSource");
		}
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<String> obtineDatosProveedor(int noEmpresa, int idBanco, String chequera, String poHeaders) {
		StringBuffer sql = new StringBuffer();
		List<List<String>> obtieneDatosBanco = new ArrayList<>();
		try {

			sql.append("\n   Select Cp.Desc_Pais as pais,");
			sql.append("\n	(select mc.contacto from Medios_Contacto mc where");
			sql.append("\n	Mc.No_Persona = M.No_Cliente ");
			sql.append("\n	And Mc.Id_Tipo_Persona = 'P' ");
			sql.append("\n	and mc.id_tipo_medio = 'TOF1') as numero");
			sql.append("\n	 From Movimiento M Left Join Direccion d On (M.No_Cliente = D.No_Persona)");
			sql.append("\n	              Left Join Cat_Pais Cp On (D.Id_Pais = Cp.Id_Pais)");
  
			sql.append("\n   Where M.No_Empresa =");
			sql.append(noEmpresa);
			sql.append("\n and M.Id_Banco = "); 
			sql.append(idBanco);
			sql.append("and M.id_chequera = '");
			sql.append(chequera);
			sql.append("' \n  and M.Po_Headers = '");
			sql.append(poHeaders);
			sql.append("'");
			sql.append("\n and Rownum = 1");
			obtieneDatosBanco = jdbcTemplate.query(sql.toString(), new RowMapper<List<String>>() {
				public List<String> mapRow(ResultSet rs, int idx) throws SQLException {
					List<String> obtieneDatosBanco = new ArrayList<>();
					obtieneDatosBanco.add(rs.getString("pais"));
					obtieneDatosBanco.add(rs.getString("numero"));
					return obtieneDatosBanco; 			
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectr�nica, C:EnvioTransferenciasDao, M:obtineDatosProveedor");
		}
		return !obtieneDatosBanco.isEmpty()  ? obtieneDatosBanco.get(0): new ArrayList<String>();
	}

	public String obtieneReferenciaNumerica(String string) {
		List<String>  retorno = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append("Select  cd.ref_numerica ");
			sql.append(" FROM  movimiento M JOIN PERSONA P ON m.NO_CLIENTE = p.NO_PERSONA JOIN CAT_FORMA_PAGO f ON m.ID_FORMA_PAGO = f.ID_FORMA_PAGO, chequera_default cd  where no_folio_det =");
			sql.append(string);
			sql.append("  and p.no_persona= cd.no_persona");
			retorno = jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
	 			public String mapRow(ResultSet rs, int idx) throws SQLException {
	 				return (rs.getString("ref_numerica") != null  ? rs.getString("ref_numerica") : "");
	 			}
	 		});
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Layout, C:LayoutsDao, M:obtieneReferenciaMovimiento");
		}
		return retorno.get(0);
	}
	
	
	public boolean diaFeriado(String fecha){
		StringBuffer sql = new StringBuffer();
		try{
			sql.append(" SELECT count(fec_inhabil) ");
			sql.append(" FROM dia_inhabil ");
			sql.append(" WHERE fec_inhabil = '"+fecha+"'");
			System.out.println(sql.toString());
			if(jdbcTemplate.queryForInt(sql.toString())>=1){
				return true;
			}
			else{
				return false;
			}
			//return jdbcTemplate.queryForInt(sql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Layout, C:LayoutsDao, M:seleccionarbanco");
			return true;
		}
	}

	public String obtenerCampoReferencia(int noPersona) {
		StringBuffer sql = new StringBuffer();
		try{
			sql.append(" SELECT COALESCE(LTRIM(RTRIM(referencia_cte)), '') as referencia_cte\n");
			sql.append(" FROM persona \n");
			sql.append(" WHERE no_persona = " + noPersona + " \n");
			sql.append(" AND id_tipo_persona in ('K' ,'P')\n");
			sql.append(" ORDER BY referencia_cte DESC \n");
			
			List<String> campo = jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
				public String mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getString("referencia_cte");					
				}
			});
			
			return campo.get(0);
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Layout, C:LayoutsDao, M:verificaDefault");
			return "";
		}
	}

	public boolean verificaDefault(int noPersona) {
		StringBuffer sql = new StringBuffer();
		try{
			sql.append(" SELECT COUNT(*) \n");
			sql.append(" FROM chequera_default \n");
			sql.append(" WHERE no_persona = " + noPersona + " \n");
			sql.append(" 	AND ((ref_numerica <> '' AND ref_numerica IS NOT NULL) \n");
			sql.append(" 	OR (ref_alfanumerica <> '' AND ref_alfanumerica IS NOT NULL)) \n");
			
			return jdbcTemplate.queryForInt(sql.toString()) > 0 ? true : false;
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Layout, C:LayoutsDao, M:verificaDefault");
			return false;
		}
	}

	public Map<String, Object> obtieneReferenciasDefault(int noPersona) {
		StringBuffer sql = new StringBuffer();
		try{
			sql.append(" SELECT coalesce(ref_numerica, '') as refN, coalesce(ref_alfanumerica, '') as refA \n");
			sql.append(" FROM chequera_default \n");
			sql.append(" WHERE no_persona = " + noPersona + " \n");
			
			List<Map<String,Object>> referencias = (List<Map<String, Object>>) jdbcTemplate.query(sql.toString(), new RowMapper<Map<String,Object>>(){
				public Map<String,Object> mapRow(ResultSet rs, int idx) throws SQLException {
					Map<String,Object> ret = new HashMap<>();
					ret.put("refA", rs.getString("refA"));
					ret.put("refN", rs.getString("refN"));
					return ret;					
				}
			});
			
			return referencias.get(0);
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Layout, C:LayoutsDao, M:verificaDefault");
			return null;
		}
	}

	public Map<String, Object> obtieneReferenciaMov(String campo, int noFolioDet) {
		StringBuffer sql = new StringBuffer();
		try{
			if (campo.equals("concepto y no_factura")) sql.append(" SELECT coalesce(concepto, '') as refN, coalesce(no_factura, '') as refA \n");
			else sql.append(" SELECT coalesce(" + campo + ", '') as refN, coalesce('', '') as refA \n");
			
			sql.append(" FROM movimiento \n");
			sql.append(" WHERE no_folio_det = " + noFolioDet + " \n");
			
			List<Map<String,Object>> referencias = (List<Map<String, Object>>) jdbcTemplate.query(sql.toString(), new RowMapper<Map<String,Object>>(){
				public Map<String,Object> mapRow(ResultSet rs, int idx) throws SQLException {
					Map<String,Object> ret = new HashMap<>();
					ret.put("refA", rs.getString("refA"));
					ret.put("refN", rs.getString("refN"));
					return ret;					
				}
			});
			
			return referencias.get(0);
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Layout, C:LayoutsDao, M:verificaDefault");
			return null;
		}
	}
	public Map<String,Object> obtenerReferenciaHonda(int noCliente, int noFolioDet, String concepto){
		StringBuffer sql = new StringBuffer();
		List<String> listCons = new ArrayList<String>();
		Map<String,Object> mapRet = new HashMap<String,Object>();
		String psValor="";
		String psConDocto="";
		String ref="";
		String val1="",val2="",val3="";
	//	System.out.println("dentro del metodo:..obtenerReferencia-- ");
	 
		try {
			mapRet.put("psRef2", "");
			
//			sql.append(" SELECT coalesce( 'no_folio_det', referencia_cte) as Campo ");
			sql.append(" SELECT referencia_cte as Campo ");
			sql.append(" FROM persona ");
			sql.append(" WHERE no_persona = " + noCliente+" and id_tipo_persona in('P','K')");
			System.out.println("valor del qry..:"+sql.toString());
		     listCons= jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
					public String mapRow(ResultSet rs, int idx) throws SQLException {
						return rs.getString("Campo");
					}});
		     if(listCons.size()>0) {
		    	 
		    	 psValor = listCons.get(0);
		    	// System.out.println("valor "+psValor);
		    	 if(psValor==null){
		    		 psValor="";
		    	 }
		    	 if(psValor.equals("")){
		    		 psValor="no_folio_det";
		    	 }
		 //   	 System.out.println("valor2 "+psValor);
		    	 sql = new StringBuffer();
		    	 listCons = new ArrayList<String>();
		    	 
		    	 if(psValor.equals("no_folio_det")) {
		    	 	 psConDocto = consultarConfiguraSet(354);
		    	 	 
		    	 	 if(psConDocto != null && psConDocto.equals("SI")) {
		    	 		sql.append(" SELECT no_docto ");
    	                sql.append(" FROM movimiento ");
                 		sql.append(" WHERE no_folio_det = " + noFolioDet);
                 		System.out.println("no_docto "+sql.toString());
                 		listCons= jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
        		 			public String mapRow(ResultSet rs, int idx) throws SQLException {
        		 				return rs.getString("no_docto");
        		 			}
        		 		});
                 		
                 		if(listCons.size()>0) {
                 			if(!listCons.get(0).equals(""))
                 				mapRet.put("valorPagRef", listCons.get(0));
                 			else
                 				mapRet.put("valorPagRef", noFolioDet);
                 		}else
                 			mapRet.put("valorPagRef", noFolioDet);
		    	 	 }else
		    	 		 mapRet.put("valorPagRef", noFolioDet);
		    	// 	 System.out.println("con "+concepto);
		    	 	mapRet.put("valorPagRef1",concepto);
		    	 	mapRet.put("valorPagRef2","");
		    	 	mapRet.put("valorPagRef3","");
		    	 }else if(psValor.equals("no_factura") || psValor.equals("no_docto") 
		    	 		 || psValor.equals("referencia")|| psValor.equals("concepto")) {
		    	            sql.append(" SELECT " + psValor +" as Valor ");
		            		sql.append(" FROM movimiento ");
		    	 			sql.append(" WHERE no_folio_det = " +noFolioDet);
		    	 			System.out.println("valor"+sql.toString());
		    	 			listCons= jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
		    	 				public String mapRow(ResultSet rs, int idx) throws SQLException {
		    	 					return rs.getString("Valor");
		    	 				}});
		    	            if(listCons.size()>0)
		    	            	mapRet.put("valorPagRef",listCons.get(0));
		    	            else
		    	            	mapRet.put("valorPagRef", "");
		    	            mapRet.put("valorPagRef1",listCons.get(0));
		    	         	mapRet.put("valorPagRef2",listCons.get(0));
				    	 	mapRet.put("valorPagRef3","");
		    	 }else if(psValor.equals("concepto y no_factura")){
		             sql.append( " SELECT coalesce(no_factura, '' ) as no_factura, ");
		             sql.append( "     coalesce(concepto, 'SIN CONCEPTO') as concepto ");
		             sql.append( " FROM movimiento ");
		             sql.append( " WHERE no_folio_det = " +noFolioDet);
		             System.out.println("concepto y no factura "+sql.toString());
		             List<Map<String,Object>> map = new ArrayList<Map<String,Object>>();
		             map= jdbcTemplate.query(sql.toString(), new RowMapper<Map<String,Object>>(){
	    		 			public Map<String,Object> mapRow(ResultSet rs, int idx) throws SQLException {
	    		 					Map<String,Object> mapi = new HashMap<String,Object>();
	    		 					mapi.put("no_factura",rs.getString("no_factura"));
	    		 					mapi.put("concepto",rs.getString("concepto"));
	    		 				return  mapi;
	    		 			}});
		        //     System.out.println(map.get(0).get("no_factura")+", "+map.get(0).get("concepto"));
		            	 if(!map.isEmpty() && map.get(0).get("no_factura").equals("") && map.get(0).get("concepto").equals("")) 
		            		// mapRet.put("psRef2", "0000");
		            		 mapRet.put("valorPagRef",funciones.ajustarLongitudCampo("SIN FACTURA SIN CONCEPTO", 15, "I", "", ""));
			             else
			            	// mapRet.put("psRef2",map.get(0).get("no_factura"));
			            	 if(!map.isEmpty() && map.get(0).get("no_factura").equals("") && !map.get(0).get("concepto").equals("")){
			            		 mapRet.put("valorPagRef",funciones.ajustarLongitudCampo("SIN FACTURA"+map.get(0).get("concepto").toString(), 15, "I", "", ""));
			            	 }else{
			            		 if(!map.isEmpty() && !map.get(0).get("no_factura").equals("") && map.get(0).get("concepto").equals("")){
				            		 mapRet.put("valorPagRef",funciones.ajustarLongitudCampo(map.get(0).get("no_factura").toString()+"SIN CONCEPTO", 15, "I", "", ""));
				            	 }else{
						             mapRet.put("valorPagRef",funciones.ajustarLongitudCampo(map.get(0).get("no_factura").toString()+map.get(0).get("concepto").toString(), 15, "I", "", ""));
				            	 }
			            	 }

			             
//			             if(!map.isEmpty() && map.get(0).get("concepto").equals("")) 
//			            	 mapRet.put("valorPagRef", "SIN CONCEPTO");
//			             else
//			            	 mapRet.put("valorPagRef",map.get(0).get("concepto"));
			             
			             mapRet.put("valorPagRef1",funciones.ajustarLongitudCampo(map.get(0).get("no_factura").toString()+map.get(0).get("concepto").toString(), 15, "I", "", ""));
			          	mapRet.put("valorPagRef2",funciones.ajustarLongitudCampo(map.get(0).get("no_factura").toString()+map.get(0).get("concepto").toString(), 15, "I", "", ""));
			    	 	mapRet.put("valorPagRef3","");
		    	 }else{
		    		 if(psValor.equals("referencia_dalton")){
		    	         sql.append( " SELECT no_folio_det as no_folio, ");
			             sql.append( "     observacion as referencias ");
			             sql.append( " FROM movimiento ");
			             sql.append( " WHERE no_folio_det = " +noFolioDet);
			             System.out.println("no_folio_det y referencias "+sql.toString());
			             List<Map<String,Object>> map = new ArrayList<Map<String,Object>>();
			             map= jdbcTemplate.query(sql.toString(), new RowMapper<Map<String,Object>>(){
		    		 			public Map<String,Object> mapRow(ResultSet rs, int idx) throws SQLException {
		    		 					Map<String,Object> mapi = new HashMap<String,Object>();
		    		 					mapi.put("no_folio_det",rs.getString("no_folio"));
		    		 					mapi.put("referencias",rs.getString("referencias"));
		    		 				return  mapi;
		    		 			}});
			             
			            	 if(!map.isEmpty() && map.get(0).get("no_folio_det").equals("")) 
			            		 mapRet.put("valorPagRef","");
				             else
				            	 mapRet.put("valorPagRef","PROVV"+funciones.ajustarLongitudCampo(map.get(0).get("no_folio_det").toString(), 10, "D", "", "0"));
				             
				             if(!map.isEmpty() && map.get(0).get("referencias").equals("")){
				            	 mapRet.put("valorPagRef1", "");
				            	 mapRet.put("valorPagRef2", "");
				            	 mapRet.put("valorPagRef3", "");
				             }else{
				            	 ref=map.get(0).get("referencias").toString();
				            	 System.out.println("cad original "+ref.toString());
					             if(ref.charAt(0)=='|'){
					            	 val1="";
					            	 val2=ref.substring(1,ref.indexOf('|',1));
					            	// System.out.println("detalle2 "+val2);
					            	 val3=ref.substring(val2.length()+1,ref.length());
					             }else{
					            	 val1=ref.substring(0,ref.indexOf('|',1));
							           System.out.println("tam1 "+val1.length());
							           val2=ref.substring(val1.length(),ref.indexOf('|',val1.length()+1));
							           System.out.println("tam2 "+val2.length());
							           val3=ref.substring(val1.length()+val2.length()+1,ref.length());
					             }
					          
					            	System.out.println("detalle1 "+val1);
					            	System.out.println("detalle2 "+val2);
					            	System.out.println("detalle3 "+val3);
					            	mapRet.put("valorPagRef1",val1.replace("|",""));
					            	mapRet.put("valorPagRef2",val2.replace("|",""));
					            	mapRet.put("valorPagRef3",val3.replace("|","")); 
				             }
				            	
		    		 }else{
		    			 mapRet.put("valorPagRef",psValor);
		    			 mapRet.put("valorPagRef1",concepto);
		    			 	mapRet.put("valorPagRef2","");
				    	 	mapRet.put("valorPagRef3","");
		    		 }
		    	 	
		     	 }
		     }
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.toString());
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Layout, C:LayoutsDao, M:obtenerValorPagoReferenciado");
		}
		 return mapRet;
	}
	
	public boolean consultarBancoExtranjero(String idBancoBenef) {
		StringBuffer sql = new StringBuffer();
		try{
			sql.append(" select COUNT(*) from cat_banco where id_banco='"+idBancoBenef+"' and nac_ext='N'  ");
			System.out.println("bancoBeneficiarioConsulta..::::::...Angel..::"+sql.toString());
			if(jdbcTemplate.queryForInt(sql.toString())>=1){
				return true;
			}
			else{
				return false;
			}
			//return jdbcTemplate.queryForInt(sql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Layout, C:LayoutsDao, M:seleccionarbanco");
			return true;
		}
	}


	public String consultarAba(String idChequera, String idDivisa, String psClabe) {
		StringBuffer sql = new StringBuffer();
		try{
			   
			sql.append("\n  select aba from cat_cta_banco ");
			sql.append("\n where id_chequera='"+idChequera+"'  "); 
			sql.append("\n and id_divisa='"+idDivisa+"' ");
			sql.append("\n  and id_clabe='"+psClabe+"' ");


			
			
			
			List<String> campo = jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
				public String mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getString("aba");					
				}
			});
			
			return campo.get(0);
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Layout, C:LayoutsDao, M:verificaDefault");
			return "";
		}
	}

	public String consultarSwCode(String idChequera, String idDivisa, String psClabe) {
		StringBuffer sql = new StringBuffer();
		try{
			   
			sql.append("\n  select swift_code cat_cta_banco ");
			sql.append("\n where id_chequera='"+idChequera+"'  "); 
			sql.append("\n and id_divisa='"+idDivisa+"' ");
			sql.append("\n  and id_clabe='"+psClabe+"' ");
			List<String> campo = jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
				public String mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getString("swift_code");					
				}
			});
			
			return campo.get(0);
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Layout, C:LayoutsDao, M:verificaDefault");
			return "";
		}
	}

}
