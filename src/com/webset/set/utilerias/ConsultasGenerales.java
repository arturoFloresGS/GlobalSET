package com.webset.set.utilerias;
/**
 * @autor Serio Omar Vaca, Cristian Garcia Garcia, Jessica Arelly Cruz Cruz
 * @since 23/Sep/2010
 */
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.webset.set.utilerias.dto.CajaUsuarioDto;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.ComunEgresosDto;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.GrupoEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.LlenaFormaPagoDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.ParametroDto;
import com.webset.set.utilerias.dto.ParametroFactorajeDto;
import com.webset.set.utilerias.dto.Retorno;
import com.webset.set.utilerias.dto.SaldosChequerasDto;
import com.webset.set.utilerias.dto.StoreParamsComunDto;

/**
 * Clase que contiene los metodos "getSeq", "getCount", "getCountActualizar",
 * "cambiarFecha", "existeCampoClave"
 * 
 */
//@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
public class ConsultasGenerales {
	StringBuffer sb = null;
	private JdbcTemplate jdbcTemplate;
	private TransactionTemplate transactionTemplate;
	private Bitacora bitacora = new Bitacora();
	private Funciones funciones= new Funciones();
    //private UserTransaction utx;
    //private TransactionManager tm;
	String gsDBM="SQL SERVER";
	private static Logger logger = Logger.getLogger(ConsultasGenerales.class);
	Exception e;
	GlobalSingleton globalSingleton;
	
	//private static Logger log = Logger.getLogger(ConsultasGeneralesDao.class);

	public ConsultasGenerales(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	public ConsultasGenerales(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.transactionTemplate = transactionTemplate;
	}

	/***************************************************************************
	 * retorna el consecutivo siguiente de la tabla
	 * 
	 * @param tabla
	 * @param campo
	 * @return
	 */
	public int getSeq(String tabla, String campo) {
		try {
			int res = jdbcTemplate.queryForInt("SELECT MAX(" + campo
					+ ") + 1 FROM " + tabla);
			if (res < 1) {
				res = 1;
			}
			return res;
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Utilerias, C:ConsultasDao, M:getSeq");
			return -1;
		}
	}

	/**
	 * Retorna el numero de veces que esta repetido un campo
	 * 
	 * @param tabla
	 *            nombre de la tabla a consultar
	 * @param campo
	 *            el campo de la tabla a contar
	 * @param clave
	 *            la String que entrara en la sentencia a conectar
	 * @return int
	 */
	public int getCount(String tabla, String campo, String clave) {
		try {
			int res = jdbcTemplate.queryForInt("SELECT COUNT(" + campo
					+ ") FROM " + tabla + " WHERE " + campo + " = '"
					+ clave.toUpperCase().trim() + "'");
			if (res < 1) {
				res = 0;
			}
			return res;
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Utilerias, C:ConsultasDao, M:getCount");
			return -1;
		}
	}

	/**
	 * Retorna el numero de veces que esta repetida la clave y que no sea el
	 * mismo id
	 * 
	 * @param tabla
	 * @param campoId
	 * @param campoClave
	 * @param id
	 * @param clave
	 * @return int
	 */
	public int getCountActualizar(String tabla, String campoId,
			String campoClave, int id, String clave) {
		try {
			sb = new StringBuffer();
			sb.append(" SELECT COUNT(" + campoClave + ") ");
			sb.append(" FROM " + tabla);
			sb.append(" WHERE " + campoClave + " = '"
					+ clave.toUpperCase().trim() + "' ");
			if (id > 0)
				sb.append(" AND " + campoId + " <> " + id);
			int res = jdbcTemplate.queryForInt(sb.toString());
			if (res < 1) {
				res = 0;
			}
			return res;
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Utilerias, C:ConsultasDao, M:getCountActualizar");
			return -1;
		}
	}

	/**
	 * Cambiar un String a una fecha
	 * 
	 * @param fecha
	 * @return Date
	 */
	public Date cambiarFecha(String fecha) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date today = df.parse(fecha);
			return today;
		} catch (ParseException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Utilerias, C:ConsultasDao, M:cambiarFecha");
			return null;
		}
	}

	/**
	 * consulta la tabla configura_set y obtiene el valor segun el indice
	 * 
	 * @param indice
	 * @return
	 */
	
	public String consultarConfiguraSet(int indice) {
		try {
			sb = new StringBuffer();
			sb.append(" SELECT valor ");
			sb.append("\n FROM configura_set");
			sb.append("\n WHERE indice = " + indice);
			System.out.println("configura set "+sb);
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
					+ "P:Utilerias, C:ConsultasGenerales, M:ConsultaConfiguraSet");
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * consulta generica "fast track" 
	 * 
	 * @param String camposRet, String tablas, String joins, String filter
	 * @return
	 */
	//@SuppressWarnings("unchecked")
	public String obtenerQuery(String campos, String tablas, String joins, String condiciones, String orden) {

			if(campos == null || campos.equals("") || tablas == null || tablas.equals(""))
				return null;

		
			sb = new StringBuffer();
			sb.append(" SELECT " + campos);
			sb.append("\n FROM "+ tablas);
			sb.append("\n WHERE 1=1");
			
			if(joins != null && !joins.equals(""))
				sb.append("\n AND " + joins);
			
			if(condiciones != null && !condiciones.equals(""))
				sb.append("\n AND " + condiciones);

			if(orden != null && !orden.equals(""))
				sb.append("\n ORDER BY " + orden);
			
			return sb.toString();
	}
	
	/**
	 * consulta generica "fast track" 
	 * 
	 * @param String camposRet, String tablas, String joins, String filter
	 * @return
	 */
	
	public List<Map<String, Object>> ejecutarQueryGenerico(String query) {
		
		try {
			logger.info("SQL=>"+query+"<");
			
			List<Map<String, Object>> listRegs = jdbcTemplate.query(
						query,
						new RowMapper<Map<String, Object>>() {
							public Map<String, Object> mapRow(ResultSet rs, int idx) throws SQLException {
								ResultSetMetaData rsmd = rs.getMetaData();
								int numCols = rsmd.getColumnCount();
								logger.info("numCols="+numCols);
								
								String nombreCol = null; 
								Map<String, Object> reg = new HashMap<String, Object>();
									
								for(int i=1; i <= numCols; i++){
									nombreCol = rsmd.getColumnName(i);
									//logger.info("nombreCol["+i+"]=>"+nombreCol+"<"); //" Data = "+ rs.getString(nombreCol));
									reg.put(nombreCol, rs.getString(nombreCol));
								}
	
								return reg;
							}
					});
			
			return listRegs;
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Utilerias, C:ConsultasDao, M:ConsultaConfiguraSet");
			return null;
		}
	}
	
	
	/**
	 * consulta generica "fast track" 
	 * 
	 * @param String camposRet, String tablas, String joins, String filter
	 * @return
	 */
	//@SuppressWarnings("unchecked")
	public List<Map<String, Object>> obtenerListaDatos(String campos, String tablas, String joins, String condiciones, String orden) {
		String query = null;
		
			query = obtenerQuery(campos, tablas, joins, condiciones, orden);
			List<Map<String, Object>> listRegs = ejecutarQueryGenerico(query);
			
			return listRegs;
	}
	
	
	public String obtenerQuery(List<String> campos, List<String> tablas, List<String> joins, List<String> condiciones, List<String> orden) {
		boolean first = true; 
		Iterator<String> it = null;
		
		// PRO
		String sCampos = "";
		if(campos==null || tablas==null)
			return "";
		
		it = campos.iterator();
		for(first = true; it.hasNext();){
			sCampos += (first?" ":", ") + it.next();  // correo nadia: nena.nadi@hotmail.com
			first = false;
		}
		
		String sTablas = "";
		if(tablas!=null){
			it = tablas.iterator();
			for(first = true; it.hasNext();){
				sTablas += (first?" ":", ") + it.next();
				first = false;
			}
		}
		
		String sJoins = "";
		if(joins!=null){
			it = joins.iterator();
			for(first = true; it.hasNext();){
				sJoins += (first?" ":" AND ") + it.next();
				first = false;
			}
		}
		
		String sCondiciones = "";
		if(condiciones!=null){
			it = condiciones.iterator();
			for(first = true; it.hasNext();){
				sCondiciones += (first?" ":" AND ") + it.next();
				first = false;
			}
		}

		String sOrden = "";
		if(orden!=null){
			it = orden.iterator();
			for(first = true; it.hasNext();){
				sOrden += (first?" ":", ") + it.next();
				first = false;
			}
		}
	
		
		return obtenerQuery(sCampos, sTablas, sJoins, sCondiciones, sOrden);
	}
	
	public List<Map<String, Object>> obtenerListaDatos(List<String> campos, List<String> tablas, List<String> joins, List<String> condiciones, List<String> orden) {
		
		String query = obtenerQuery(campos, tablas, joins, condiciones, orden);
		List<Map<String, Object>> listRegs = ejecutarQueryGenerico(query);
		return listRegs;
	}
	
	
	public Date obtenerFechaHoy() {
		sb = new StringBuffer();
		sb.append(" SELECT fec_hoy FROM FECHAS ");
		try{
			 List<Date> fechas = jdbcTemplate.query(sb.toString(), new RowMapper<Date>() {
				public Date mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getDate("fec_hoy");
				}
			});
			return fechas.get(0);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Utilerias, C:ConsultasDao, M:obtenerFechaHoy");
			return null;
		}
	}
	
	/**
	 * obtiene la fecha de ma�ana
	 * @return
	 */
	public Date obtenerFechaAyer() {
		sb = new StringBuffer();
		sb.append(" SELECT fec_ayer FROM FECHAS ");
		try{
			 List<Date> fechas = jdbcTemplate.query(sb.toString(), new RowMapper<Date>() {
				public Date mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getDate("fec_ayer");
				}
			});
			return fechas.get(0);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Utilerias, C:ConsultasDao, M:obtenerFechaAyer");
			return null;
		}
	}

	
	/**
	 * obtiene la fecha de ma�ana
	 * @return
	 */
	
	public Date obtenerFechaManana() {
		sb = new StringBuffer();
		sb.append(" SELECT fec_manana FROM FECHAS ");
		try{
			 List<Date> fechas = jdbcTemplate.query(sb.toString(), new RowMapper<Date>() {
				public Date mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getDate("fec_manana");
				}
			});
			return fechas.get(0);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Utilerias, C:ConsultasDao, M:obtenerFechaManana");
			return null;
		}
	}
	
	/**
	 * 
	 * @param tabla
	 * @param campoRelacion
	 * @param dato
	 * @return int 
	 * 
	 * Metodo que sirve para verificar si existe una llave primaria en otra tabla
	 * Ejemplo que si existe el id_perfil 1 en la tabla seg_usuario
	 * 
	 * tabla 			-> es la tabla donde va a buscar
	 * campoRelacion 	-> es el parametro de la consulta para la busqueda en el where
	 * dato				-> es parametro a buscar
	 */
	public int existeCampoClave(String tabla, String campoRelacion, int dato){
		int res = -1;
		sb = new StringBuffer();
		sb.append("SELECT COUNT(*) FROM " + tabla + " WHERE " + campoRelacion + " = " + dato);
		try{
			res = jdbcTemplate.queryForInt(sb.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Utilerias, C:ConsultasDao, M:existeCampoClave");
		}
		return res;
	}
	
	/**
	 * 
	 * @param tabla
	 * @param campoRelacion
	 * @param dato
	 * @return int 
	 * 
	 * Metodo que sirve para verificar si existe una llave primaria en otra tabla
	 * Ejemplo que si existe el id_perfil 1 en la tabla seg_usuario
	 * 
	 * tabla 			-> es la tabla donde va a buscar
	 * campoRelacion 	-> es el parametro de la consulta para la busqueda en el where
	 * dato				-> es parametro a buscar
	 */
	public int existeCampoClave(String campo, String tabla, String campoRelacion, String dato){
		int res = -1;
		sb = new StringBuffer();
		sb.append("SELECT COUNT(" + campo + ") FROM " + tabla + " WHERE " + campoRelacion + " = '" + dato + "'");
		try{
			res = jdbcTemplate.queryForInt(sb.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Utilerias, C:ConsultasDao, M:existeCampoClave");
		}
		return res;
	}
	
	/**
	*
	* @param tipoFolio
	* @return int
	*
	* Selecciona el numero de folio de la tabla folio
	*/
	public int seleccionarFolioReal(String tipoFolio) {
		sb = new StringBuffer();
		int res = 0;
		try {
	
			sb.append(" SELECT num_folio ");
			sb.append("\n FROM folio ");
			sb.append("\n WHERE tipo_folio = '" + tipoFolio + "' ");
			System.out.println("seleccionarFolioReal "+sb.toString());
			res=jdbcTemplate.queryForInt(sb.toString());
		} catch (Exception e) {
		 System.out.println(e.toString());
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()+ "P:BE, C:ConsultasDao, M:seleccionarFolioReal");
			return -1;
		}
		return res;
	}

	/**
	 * 
	 * @param tipoFolio
	 * @return int
	 * 
	 * Actualiza el numero de folio de la tabla folio
	 */
	public int actualizarFolioReal(String tipoFolio) {
		sb = new StringBuffer();
		int res = 0;
		
		try {
			sb.append(" UPDATE folio ");
			sb.append("\n SET num_folio = num_folio + 1 ");
			sb.append("\n WHERE tipo_folio = '" + tipoFolio + "'");
			
			res = jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BE, C:ConsultasGenerales, M:actualizarFolioReal");
			return -1;
		}
		return res;
	}
	
	
/**
 * 
 * @param emp numero de la empresa
 * @param folParam numero de folio de parametro
 * @param folMovi folio de movimiento este lo obtiene en el store asi que se manda un cero
 * @param folDeta este parametro es igual al anterior
 * @param result se envia el numero 1 por default
 * @param mensajeResp es una variable que maneja el store se puede enviar como: ''
 * @return res para verificar si hizo o no el commit
 */
	
	public Map<String, Object> ejecutarGenerador(GeneradorDto dto)	 {
		Map<String, Object> resultado= new HashMap<String, Object>();	 
		  
		try {	   
		   	com.webset.utils.tools.StoredProcedure storedProcedure = 
					new com.webset.utils.tools.StoredProcedure(jdbcTemplate, "sp_generador");
			
			storedProcedure.declareParameter("emp", dto.getEmpresa(), com.webset.utils.tools.StoredProcedure.IN);
			storedProcedure.declareParameter("fol_para", dto.getFolParam(), com.webset.utils.tools.StoredProcedure.IN);
			storedProcedure.declareParameter("movi", Types.INTEGER, com.webset.utils.tools.StoredProcedure.OUT);
			storedProcedure.declareParameter("deta", Types.INTEGER, com.webset.utils.tools.StoredProcedure.OUT);
			storedProcedure.declareParameter("result", Types.VARCHAR, com.webset.utils.tools.StoredProcedure.OUT);
			storedProcedure.declareParameter("mensajeResp", Types.VARCHAR, com.webset.utils.tools.StoredProcedure.OUT);
			
			resultado = storedProcedure.execute();
		   	System.out.println("Query" + resultado);
		   	System.out.println("resp almacenado "+resultado.get("mensajeResp").toString());
		}catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
			+ "P:BE, C:ConsultasGenerales, M:ejecutarGenerador");
		}
		return resultado; 
	 }

	public Map<String, Object> obtieneCheqPagadora(GeneradorDto dto)	 {
		Map<String, Object> resultado= new HashMap<String, Object>();
//		ParametroDto datos=new ParametroDto();
//		Map<String, Object> resultado2= new HashMap<String, Object>();
		  
		try {	   
		   	com.webset.utils.tools.StoredProcedure storedProcedure = 
					new com.webset.utils.tools.StoredProcedure(jdbcTemplate, "sp_obtieneDatosCheqPagadora");
			storedProcedure.declareParameter("noEmpresa", dto.getEmpresa(), com.webset.utils.tools.StoredProcedure.IN);
			storedProcedure.declareParameter("divisa", dto.getDivisa(), com.webset.utils.tools.StoredProcedure.IN);
			storedProcedure.declareParameter("id_forma_pago", dto.getId_forma_pago(), com.webset.utils.tools.StoredProcedure.IN);
			storedProcedure.declareParameter("id_banco", Types.INTEGER, com.webset.utils.tools.StoredProcedure.OUT);
			storedProcedure.declareParameter("id_chequera", Types.VARCHAR, com.webset.utils.tools.StoredProcedure.OUT);
			storedProcedure.declareParameter("result", Types.INTEGER, com.webset.utils.tools.StoredProcedure.OUT);
			

			resultado = storedProcedure.execute();
//			if(!resultado.isEmpty()){
//		 		datos.setIdChequera("id_chequera");
//		 		datos.setIdBanco(Integer.parseInt("id_banco"));
//		 		resultado2=(Map<String, Object>) datos;
//		 	}
		   	System.out.println("Query" + resultado);
		   //	System.out.println("Resultado2" + resultado2);
		}catch (Exception e) {
			e.printStackTrace();
//			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
//			+ "P:BE, C:ConsultasGenerales, M:ejecutarGenerador");
		}
		return resultado; 
	 }
	
	
	/*
	 * 
	 * Se deshabilita proceso para lanzar revividor...
	 * COMENTA POR ORLANDO RMZ 19-11-13
	 * 
	@SuppressWarnings("unchecked")
	public Map ejecutarRevividor(String psRevividor, int noFolioDet, int idTipoOperacion,String psTipoCancelacion,
			String idEstatusMov, String psOrigenMov, int idFormaPago, String bEntregado, String idTipoMovto, double importe,
			int noEmpresa, int noCuenta, String idChequera, int idBanco,int idUsuario, String noDocto, int lote,
			String bSalvoBuenCobro, String fecConfTrans, String idDivisa, String psResultado, boolean pbAutomatico)
	{		
		String parametro="";
		String mensajeResp="";
		int result=0;
		Map resultado=new HashMap();
		
		try{
			parametro= noFolioDet+","+psTipoCancelacion+","
			+idTipoOperacion+","+idEstatusMov+","
			+psOrigenMov+","+idFormaPago+","
			+bEntregado+","+idTipoMovto+","
			+importe+","+noEmpresa+","
			+noCuenta+","+idChequera+","
			+idBanco+","+idUsuario+","
			+noDocto+","+lote+","
			+bSalvoBuenCobro+","+fecConfTrans+","
			+idDivisa;
			
			StoredProcedure storedProcedure = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_revividor") {};
			
			storedProcedure.declareParameter(new SqlParameter("parametro",Types.VARCHAR ));
			storedProcedure.declareParameter(new SqlInOutParameter("result",Types.INTEGER));
			storedProcedure.declareParameter(new SqlInOutParameter("mensaje",Types.VARCHAR ));
		
			HashMap< Object, Object > inParams = new HashMap< Object, Object >();

			inParams.put( "parametro",parametro);
			inParams.put( "result",result);
			inParams.put( "mensaje",mensajeResp);
			resultado = storedProcedure.execute((Map)inParams);		
			
			//try{
			//sql += " EXEC sp_revividor " + parametro + ", " + result + ", " + mensajeResp;
    
			//result = jdbcTemplate.update(sql);
			//}catch(Exception e){			
			
		}
		catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
			+ "P:BE, C:ConsultasGenerales, M:ejecutarRevividor");
		}
		return resultado;
	}
	*/
	
	@SuppressWarnings("unused")
	public int ejecutarRevividorOR(String psRevividor, int noFolioDet, int idTipoOperacion,String psTipoCancelacion,
			String idEstatusMov, String psOrigenMov, int idFormaPago, String bEntregado, String idTipoMovto, double importe,
			int noEmpresa, int noCuenta, String idChequera, int idBanco,int idUsuario, String noDocto, int lote,
			String bSalvoBuenCobro, String fecConfTrans, String idDivisa, String psResultado, boolean pbAutomatico)
	{		
		String sql = "";
		String parametro="";
		String mensajeResp="";
		int result=0;
		int resultado=0;
		
		try{
			parametro= noFolioDet+","+psTipoCancelacion+","
			+idTipoOperacion+","+idEstatusMov+","
			+psOrigenMov/*"SOI"*/+","+idFormaPago+","
			+bEntregado+","+idTipoMovto+","
			+importe+","+noEmpresa+","
			+noCuenta+","+idChequera+","
			+idBanco+","+idUsuario+","
			+noDocto+","+lote+","
			+bSalvoBuenCobro+","+fecConfTrans+","
			+idDivisa;
			
			//sql += " EXEC sp_revividor '" + parametro + "', " + result + ", '" + mensajeResp + "'";
			//System.out.println("ejecutarRevividorOR");
			//resultado = jdbcTemplate.update(sql);
		System.out.println("parametro: "+ parametro);
			com.webset.utils.tools.StoredProcedure storedProcedure = 
					new com.webset.utils.tools.StoredProcedure(jdbcTemplate, "sp_revividor");
			
			storedProcedure.declareParameter("parametro", 
					parametro, 
					com.webset.utils.tools.StoredProcedure.IN);
			
			storedProcedure.declareParameter("V_Result", 
					Types.INTEGER, 
					com.webset.utils.tools.StoredProcedure.OUT);
			
			storedProcedure.declareParameter("mensaje", 
					Types.VARCHAR, 
					com.webset.utils.tools.StoredProcedure.OUT);
			
			resultado = (Integer)storedProcedure.execute().get("V_Result");
			
		}
		catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
			+ "P:BE, C:ConsultasGenerales, M:ejecutarRevividor");
		}
		return resultado;
	}	
	
	public Map<String, Object> ejecutarPagoParcial(final String parametro, double interes, double iva) {
		System.out.println("ejecutarPagoParcial consultas");
	    		Map<String, Object> resultado= new HashMap<String, Object>();
	    		try {System.out.println("ejecutarPagoParcial 2");
	    			com.webset.utils.tools.StoredProcedure storedProcedure = 
	    					new com.webset.utils.tools.StoredProcedure(jdbcTemplate, "sp_Confirmacion_parcial");
	    			storedProcedure.declareParameter("parametro", parametro, com.webset.utils.tools.StoredProcedure.IN);
	    			storedProcedure.declareParameter("interesNuevo", interes, com.webset.utils.tools.StoredProcedure.IN);
	    			storedProcedure.declareParameter("ivaNuevo", iva, com.webset.utils.tools.StoredProcedure.IN);
	    			storedProcedure.declareParameter("mensaje",Types.VARCHAR,com.webset.utils.tools.StoredProcedure.OUT);
	    			storedProcedure.declareParameter("result",Types.VARCHAR, com.webset.utils.tools.StoredProcedure.OUT);
	    			resultado = storedProcedure.execute();				
	    			System.out.println("ejecutarPagoParcial "+resultado);		
			    } catch (Exception e) {
			    	bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:BE, C:ConsultasGenerales, M: ejecutarPagoParcial");
			    }
			    return resultado;
    }
	
	/*public Map<String, Object> ejecutarPagador(StoreParamsComunDto dto) {
		Map<String, Object> resultado= new HashMap<String, Object>();
		try{
			com.webset.utils.tools.StoredProcedure storedProcedure = 
					new com.webset.utils.tools.StoredProcedure(jdbcTemplate, "sp_sql_Pagador");
			
			storedProcedure.declareParameter("parametro", dto.getParametro(), com.webset.utils.tools.StoredProcedure.IN);
			storedProcedure.declareParameter("mensaje", Types.VARCHAR, 	com.webset.utils.tools.StoredProcedure.OUT);
			storedProcedure.declareParameter("result", Types.INTEGER, com.webset.utils.tools.StoredProcedure.OUT);

			resultado = storedProcedure.execute();			
			//if((int) resultado.get("result") != 0) throw new Exception("error");			
			System.out.println(resultado);
		}catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:GRAL, C:ConsultasGenerales, M:ejecutarPagador");
			return null;			
		}
		return resultado;
	}*/
	
	
	public Map<String, Object> cambiarCuadrantes(StoreParamsComunDto dto)
	{
		Map<String, Object> resultado= new HashMap<String, Object>();
		try{
			StoredProcedure storedProcedure = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_ajustes") {};
			storedProcedure.declareParameter(new SqlParameter("cadena",Types.VARCHAR ));
			storedProcedure.declareParameter(new SqlInOutParameter("mensaje",Types.VARCHAR));
			storedProcedure.declareParameter(new SqlInOutParameter("result",Types.INTEGER));
			
			HashMap<String, Object> inParams = new HashMap<String, Object>();
			inParams.put("cadena",dto.getParametro());
			inParams.put("mensaje",dto.getMensaje());
			inParams.put("result",dto.getResult());
			
			resultado = storedProcedure.execute(inParams);
			
			}catch (Exception e) {
				bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
				+ "P:GRAL, C:ConsultasGenerales, M:cambiarCuadrantes");
				
		}
		return resultado;
	}
	
	
	public Map<String, Object> ejecutarTraspasos(StoreParamsComunDto dto, String regreso)
	{
		Map<String, Object> resultado= new HashMap<String, Object>();
		
		try {
			com.webset.utils.tools.StoredProcedure storedProcedure = 
					new com.webset.utils.tools.StoredProcedure(jdbcTemplate, "sp_traspasos");
			
			storedProcedure.declareParameter("V_PARAMETRO", 
					dto.getParametro(), 
					com.webset.utils.tools.StoredProcedure.IN);
			
			storedProcedure.declareParameter("V_REGRESO", 
					Types.VARCHAR, 
					com.webset.utils.tools.StoredProcedure.OUT);
			
			storedProcedure.declareParameter("V_MENSAJE", 
					Types.VARCHAR, 
					com.webset.utils.tools.StoredProcedure.OUT);
			
			storedProcedure.declareParameter("result", 
					Types.INTEGER, 
					com.webset.utils.tools.StoredProcedure.OUT);
			
			resultado = storedProcedure.execute();			
		
			}catch (Exception e) {
				e.printStackTrace();
				bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
				+ "P:BE, C:ConsultasGenerales, M:ejecutarTraspasos");
		}
		return resultado;
	}
	
	
	public Map<String, Object> ejecutarConfirmador(int folioDet, String folioBanco, int secuencia, Date fecValor, int result, String msg) {
		Map<String, Object> resultado= new HashMap<String, Object>();
		try {
			StoredProcedure storedProcedure = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_confirmador") {};
			storedProcedure.declareParameter(new SqlParameter("vFolio",Types.INTEGER ));
			storedProcedure.declareParameter(new SqlParameter("vFolBanco",Types.VARCHAR));
			storedProcedure.declareParameter(new SqlParameter("vSecuencia",Types.INTEGER ));
			storedProcedure.declareParameter(new SqlParameter("vFecValor",Types.DATE));
			storedProcedure.declareParameter(new SqlInOutParameter("result",Types.INTEGER ));
			storedProcedure.declareParameter(new SqlInOutParameter("mensaje",Types.VARCHAR));
			
			HashMap<String, Object> inParams = new HashMap<String, Object>();
			inParams.put("vFolio", folioDet);
			inParams.put("vFolBanco", (folioBanco.equals("") ? "0" : folioBanco));
			inParams.put("vSecuencia", secuencia);
			inParams.put("vFecValor", funciones.ponerFechaDate("" + funciones.ponerFecha(fecValor)));
			inParams.put("result", result);
			inParams.put("mensaje", "'" + msg + "'");
			
			System.out.println(inParams);
			resultado = storedProcedure.execute(inParams);
			
			logger.info("result = " + resultado.get("result"));
			logger.info("mensaje = " + resultado.get("mensaje"));
		}catch (Exception e) {
				bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString() + "P:BE, C:ConsultasGenerales, M:ejecutarConfirmador");
		}
		return resultado;
	}
	
	/**
	* consulta la tabla configura_set y obtiene el valor segun el indice
	*
	* @param indice
	* @return
	*/
	
	public List<Retorno> consultarConfiguraSet() {
	List<Retorno> datos=null;
	try {
		sb = new StringBuffer();
		sb.append(" SELECT indice, valor, descripcion ");
		sb.append("\n FROM configura_set ");
		datos = jdbcTemplate.query(sb.toString(), new RowMapper<Retorno>() {
	public Retorno mapRow(ResultSet rs, int idx){
		Retorno dato = new Retorno();
	try {
			dato.setId(rs.getInt("indice"));
			dato.setValorConfiguraSet(rs.getString("valor"));
			} catch (SQLException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ConsultasDao, M:ConsultaConfiguraSet");
		}
		return dato;
	}
	});
	} catch (Exception e) {
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P:Utilerias, C:ConsultasGenerales, M:ConsultaConfiguraSet");
	}
	return datos;
	}
	
	
	/**
	 * Este metodo obtiene las empresas asignadas a un usuario
	 * @param idUsuario se envia el usuario para saber la empresas asignadas
	 * @return una lista que contiene el id y nombre de las empresas 
	 */
	
	public List<LlenaComboEmpresasDto> llenarComboEmpresas(int idUsuario)
	{
		String sql="";
		List<LlenaComboEmpresasDto> list= new ArrayList<LlenaComboEmpresasDto>();
		try{
			sql+= " SELECT no_empresa as ID, nom_empresa as describ ";
		    sql+= "\n	FROM empresa	";
		    sql+= "\n	WHERE no_empresa > 1	";
		    sql+= "\n	AND no_empresa in (SELECT no_empresa	";
		    sql+= "\n	FROM usuario_empresa	";
		    sql+= "\n	WHERE no_usuario = " + idUsuario + ")	";
		    sql+= "\n	ORDER BY describ	";
		    
		    list= jdbcTemplate.query(sql, new RowMapper<LlenaComboEmpresasDto>(){
				public LlenaComboEmpresasDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboEmpresasDto cons = new LlenaComboEmpresasDto();
					cons.setNoEmpresa(rs.getInt("ID"));
					cons.setNomEmpresa(rs.getString("describ"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Utilerias, C:ConsultasGenerales, M:llenarComboEmpresas");
		}
		
		return list;
	}
	
	/*Public Function FunSQLCombo398() As ADODB.Recordset*/
	
	public List<LlenaComboDivisaDto>llenarComboDivisa()
	{
		String sql="";
		List<LlenaComboDivisaDto> listDivisas= new ArrayList<LlenaComboDivisaDto>();
		try
		{
			sql+= " SELECT id_divisa as ID, desc_divisa as Descrip ";
			sql+= "   FROM cat_divisa ";
			sql+= "  WHERE clasificacion = 'D'";
			
			//System.out.println("Divisa: " + sql);
			
    		listDivisas= jdbcTemplate.query(sql, new RowMapper<LlenaComboDivisaDto>(){
				public LlenaComboDivisaDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboDivisaDto cons = new LlenaComboDivisaDto();
					cons.setIdDivisa(rs.getString("ID"));
					cons.setDescDivisa(rs.getString("Descrip"));
					return cons;
				}});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Utilerias, C:ConsultasGenerales, M:llenarComboDivisa");
		}
		return listDivisas;
	    
	}
	
	
	public List<LlenaFormaPagoDto> llenarComboFormaPago(){
		String sql="";
		List<LlenaFormaPagoDto> listFormPag= new ArrayList<LlenaFormaPagoDto>();
		try{
			sql+="	select * from cat_forma_pago where desc_forma_pago!='TRASPASO'";
			listFormPag= jdbcTemplate.query(sql, new RowMapper<LlenaFormaPagoDto>(){
				public LlenaFormaPagoDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaFormaPagoDto cons = new LlenaFormaPagoDto();
					cons.setIdFormaPago(rs.getInt("id_forma_pago"));
					cons.setDescFormaPago(rs.getString("desc_forma_pago"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Utilerias, C:ConsultasGenerales, M:llenarComboFormaPago");
		}
		return listFormPag;
	}
	
	
	
	/*Public Function funSQLSelectCajaUsuario(ByVal pvi_noUsuario As Integer) As ADODB.Recordset*/
	
	public List<CajaUsuarioDto> obtenerCajaUsuario(int idUsuario)
	{
		String sql="";
		List<CajaUsuarioDto> listCajaUsuario= new ArrayList<CajaUsuarioDto>();
		try{
			sql+= "	SELECT id_caja as ID, desc_caja as Descripcion ";
		    sql+= "  FROM cat_caja";
		    sql+= " WHERE id_caja in (select id_caja from caja_usuario where no_usuario = "	+idUsuario+	") ";
		    listCajaUsuario= jdbcTemplate.query(sql, new RowMapper<CajaUsuarioDto>(){
				public CajaUsuarioDto mapRow(ResultSet rs, int idx) throws SQLException {
					CajaUsuarioDto cons = new CajaUsuarioDto();
					cons.setIdCaja(rs.getInt("ID"));
					cons.setDesCaja(rs.getString("Descripcion"));					
					return cons;
				}});
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Utilerias, C:ConsultasGenerales, M:obtenerCajaUsuario");
		}
		return listCajaUsuario;
	}
	
	
	public List<LlenaComboGralDto>llenarComboGral(LlenaComboGralDto dto){
		String sql="";
		List<LlenaComboGralDto> listDatos= new ArrayList<LlenaComboGralDto>();
		try{
			sql+= "	SELECT	";
			if(dto.getCampoUno()!=null && !dto.getCampoUno().trim().equals(""))
				sql+=""+dto.getCampoUno()+"	as ID";
			if(dto.getCampoDos()!=null && !dto.getCampoDos().trim().equals(""));
				sql+=",	"+dto.getCampoDos()+"	as DESCRIPCION";
		    sql+= "  FROM	";
		    if(dto.getTabla()!=null && !dto.getTabla().trim().equals(""))
		    	sql+=""+dto.getTabla();
		    if(dto.getCondicion()!=null && !dto.getCondicion().trim().equals(""))
		    	sql+="	WHERE	"+dto.getCondicion();
		    if(dto.getOrden()!=null && !dto.getOrden().trim().equals(""))
		    	sql+="	ORDER BY	"+dto.getOrden();
		 
		    System.out.print("llena combo beneficiario: " + sql);
		    
		    listDatos= jdbcTemplate.query(sql, new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					try {
						System.out.println("ENTRO A ID");
						cons.setId(rs.getInt("ID"));
					} catch (Exception e) {
						System.out.println("entro a idsrt");
						cons.setIdStr(rs.getString("ID"));
					}
					cons.setDescripcion(rs.getString("DESCRIPCION").trim());
					
					return cons;
				}});
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Utilerias, C:ConsultasGenerales, M:llenarComboGral");
		}
		System.out.println("::..ID"+listDatos.get(0).getId());

		System.out.println("::..IDSTR"+listDatos.get(0).getIdStr());
		return listDatos;
	}
	
	public List<LlenaComboGralDto>llenarComboGralB(LlenaComboGralDto dto){
		String sql="";
		List<LlenaComboGralDto> listDatos= new ArrayList<LlenaComboGralDto>();
		try{
			sql+= "	SELECT	";
			if(dto.getCampoUno()!=null && !dto.getCampoUno().trim().equals(""))
				sql+=""+dto.getCampoUno()+"	as ID";
			if(dto.getCampoDos()!=null && !dto.getCampoDos().trim().equals(""));
				sql+=",	"+dto.getCampoDos()+"	as DESCRIPCION";
		    sql+= "  FROM	";
		    if(dto.getTabla()!=null && !dto.getTabla().trim().equals(""))
		    	sql+=""+dto.getTabla();
		    if(dto.getCondicion()!=null && !dto.getCondicion().trim().equals(""))
		    	sql+="	WHERE	"+dto.getCondicion();
		    if(dto.getOrden()!=null && !dto.getOrden().trim().equals(""))
		    	sql+="	ORDER BY	"+dto.getOrden();
		 
		    System.out.print("llena combo : " + sql);
		    //System.out.print("condicion: " + dto.getCondicion());
		    listDatos= jdbcTemplate.query(sql, new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setIdStr(rs.getString("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}});
		    System.out.println(listDatos);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Utilerias, C:ConsultasGenerales, M:llenarComboGral");
		}
		return listDatos;
	}
	
	/**
	* FunSQLSelect903
	* @param empresa
	* @return
	*/
	//@SuppressWarnings("unchecked")
	public int obtenerCuenta(int empresa) {
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT no_cuenta ");
		sb.append(" FROM cuenta ");
		sb.append(" WHERE no_empresa=" + empresa);
		sb.append(" AND id_tipo_cuenta='P' ");
		try {
			return jdbcTemplate.queryForInt(sb.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
			+ "P:Utilerias, C:ConsultasGenerales, M:obtenerCuenta");
		return -1;
		}
	}
	/****
	 * Metodo para obtener el valor de cada divisa
	 * @param idDivisa id de la divisa
	 * @param fecha de la que se desea conocer el valor
	 * @return retorna el valor de la divisa como double
	 */
	
	public double obtenerTipoCambio(String idDivisa, Date fecha){
		StringBuffer sb = new StringBuffer();
		List<Double> list = new ArrayList<Double>();
		try{ 
			sb.append("SELECT coalesce(valor, 0) as valor");
			sb.append("	from valor_divisa");
			sb.append("	where id_divisa='"+idDivisa+"'");
			sb.append("	and fec_divisa=convert(date,'"+funciones.ponerFechaSola(fecha) + "',103)");
			System.out.println("tipo de cambio"+sb);
			list= jdbcTemplate.query(sb.toString(), new RowMapper<Double>(){
				public Double mapRow(ResultSet rs, int idx) throws SQLException {
				return rs.getDouble("valor");
			}});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
			+ "P:Utilerias, C:ConsultasGenerales, M:obtenerTipoCambio");
		}
		System.out.println("valor de la divisa: " + list.size());
		if(list.size() <= 0)
			return 1;
		else
			return list.get(0) > 0 ? list.get(0) : 1;
	}
	/**
	 * La consulta sirve para validar que el usuario exista 
	 * y su contrase�a sea correcta
	 * @param idUsr identificador del usuario
	 * @param psw posible contrase�a valida
	 * @return true si existe y es valido de lo contrario false
	 */
	public boolean validarUsuarioAutenticado(int idUsr,String psw){
		String sql="";
		int res=0;
		boolean valido=false;
		try{
			sql+="SELECT count(*) FROM seg_usuario";
			sql+="\n	where id_usuario="+idUsr;
			sql+="\n	and contrasena='"+psw+"'";
			
			res=jdbcTemplate.queryForInt(sql);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
			+ "P:Utilerias, C:ConsultasGenerales, M:validarUsuarioAutenticado");
			e.printStackTrace();
		}
		if(res>0)
			valido=true;
		else
			valido=false;
		
		return valido;
	}
	
	/**
	 * Public Function funSQLUpdateAutorizacionProp
	 * @param campo
	 * @param valor
	 * @param idGrupo
	 * @param cveControl
	 * @return
	 */
	public int actualizarAutorizacionPop(String campo, String valor, int idGrupo, String cveControl){
		String sql="";
		int res=0;
		System.out.println("campo "+campo);
		try{
			sql+= "UPDATE seleccion_automatica_grupo ";
		    sql+= "		SET " +campo+ " = "+valor;
		    sql+= "		,motivo_rechazo  = null";
		    sql+= " 	WHERE id_grupo_flujo = " +idGrupo;
		    sql+= "   	and cve_control = '" +cveControl+ "'";
		    System.out.println("actualizaAutorizacionPop "+sql);
		    res=jdbcTemplate.update(sql);
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
			+ "P:Utilerias, C:ConsultasGenerales, M:actualizarAutorizacionPop");
		}
		return res;
	}
	
	/**
	* consulta que obtiene los dias de diferencia entre dos fechas
	* @param fecha1 debe ser menor que fecha2
	* @param fecha2
	* @return int
	*/
	public int obtenerDiasDiferencia(Date fecha1, Date fecha2){
		StringBuffer sb = new StringBuffer();
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		try{
		sb.append(" SELECT DATEDIFF(DAY, '" + formato.format(fecha1) + "','" );
		sb.append( formato.format(fecha2) + "') ");
			return jdbcTemplate.queryForInt(sb.toString());
		}catch (Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
			+ "P:Utilerias, C:ConsultasGenerales, M:obtenerDiasDiferencia");
		return -1;
		}
	}
	
	
	public List<LlenaComboGralDto> llenarComboGrupoEmpresa(GrupoEmpresasDto dto){
		dto.setPagoEmpresa("");//Se inicializa asi de acuerdo a la migracion
		String sql="";
		List<LlenaComboGralDto>listDatos=new ArrayList<LlenaComboGralDto>();
			try{
				if(dto.getPagoEmpresa().trim().equals(""))
				{
			        sql="";
			        sql+="SELECT distinct c.id_grupo_flujo, desc_grupo_flujo ";
			        sql+="\n	FROM cat_grupo_flujo c, grupo_empresa g, usuario_empresa  u";
			        sql+="\n	WHERE c.id_grupo_flujo = g.id_grupo_flujo";
			        sql+="\n	AND g.no_empresa = u.no_empresa";
			        sql+="\n	AND u.no_usuario = "+dto.getIdUsuario();
			        sql+="\n	ORDER BY desc_grupo_flujo";
				}
			    else
			    {
			    	sql+=" Select e.no_empresa,e.nom_empresa from empresa e, usuario_empresa ue";
			        sql+=" Where e.no_empresa = ue.no_empresa";
			        sql+=" and ue.no_usuario = "+dto.getIdUsuario();
			        if (dto.getIdEmpresa()>0)
			            sql+=" and e.no_empresa = "+ dto.getIdEmpresa();
			        else
			            sql+=" and (pagadora = 'S' or (e.no_empresa = "+dto.getPagoEmpresa()+" and pagadora = 'A' ))";
		
			        sql+=" order by e.no_empresa";
			    }
				
				 listDatos= jdbcTemplate.query(sql, new RowMapper<LlenaComboGralDto>(){
						public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
							LlenaComboGralDto cons = new LlenaComboGralDto();
							cons.setId(rs.getInt("id_grupo_flujo"));
							cons.setDescripcion(rs.getString("desc_grupo_flujo"));
							return cons;
						}});
			       
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Egresos, C:ConsultasGenerales, M:obtenerDiasDiferencia");
			}
		    return listDatos;
	}
	
	/*Public Function funSQLSelectDivXEmp(ByVal plUsuario As Long) As ADODB.Recordset*/
	
	public List<LlenaComboGralDto> llenarComboDivXEmp(int idUsuario){
		String sql="";
		List<LlenaComboGralDto> listDatos= new ArrayList<LlenaComboGralDto>();
		try{
		    sql+= " SELECT cd.id_division as id, ";
		    
		    if(gsDBM.equals("SQL SERVER")) 
		        sql+= " cast(e.no_empresa as varchar) + ' - ' + cd.desc_division as descrip ";
		    else if(gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2"))
		        sql+= " cast(e.no_empresa as varchar) || ' - ' || cd.desc_division as descrip ";
		    else if(gsDBM.equals("SYBASE"))
		        sql+= " convert(varchar, e.no_empresa) + ' - ' + cd.desc_division as descrip ";
		    
		    sql+= " FROM empresa e, cat_division cd ";
		    sql+= " WHERE e.b_division = 'S' ";
		    sql+= "     and cd.no_empresa = e.no_empresa ";
		    sql+= "     and cd.no_empresa in ( SELECT no_empresa ";
		    sql+= "                            FROM usuario_empresa ";
		    sql+= "                            WHERE no_usuario = "+idUsuario+" ) ";
		    sql+= "     and cd.id_division in ( SELECT ud.id_division ";
		    sql+= "                             FROM usuario_division ud ";
		    sql+= "                             WHERE ud.no_usuario = " +idUsuario;
		    sql+= "                                 and ud.no_empresa = cd.no_empresa ) ";
		    sql+= " ORDER BY e.no_empresa, cd.desc_division ";
		    
		    listDatos= jdbcTemplate.query(sql, new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("id"));
					cons.setDescripcion(rs.getString("descrip"));
					return cons;
				}});
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultasGenerales, M:llenarComboDivXEmp");
		}
		
		return listDatos;
	}
	/*Public Function funSQLSelectSaldosChequera(ByVal pvb_porEmpresa As Boolean, _
                                           pvi_NoEmpresa As Integer, _
                                           ByVal pvb_porGrupo As Boolean, _
                                           ByVal pvi_id_grupo As Integer, _
                                           ByVal pvi_noUsuario As Integer, _
                                           ByVal pvb_porDivisa As Boolean, _
                                           ByVal pvs_idDivisa As String, _
                                           ByVal pvb_porFormaPago As Boolean, _
                                           ByVal pvi_FormaPago As Integer, _
                                           ByVal pvt_fecha As Date, _
                                           Optional psDivision As String = "") _
                                           As ADODB.Recordset*/
	
	public List<SaldosChequerasDto> obtenerSaldosChequeras(SaldosChequerasDto dto){
		String sql="";
		List<SaldosChequerasDto> listDatos=new ArrayList<SaldosChequerasDto>();
		try{
		    sql+= "SELECT DISTINCT ccb.saldo_inicial, ccb.cargo, ";
		    sql+= "\n       ccb.abono, ccb.saldo_final, ";
		    sql+= "\n       case when ccb.id_divisa = '"+consultarConfiguraSet(771)+"' then 1 else ";
		    sql+= "\n      (select v.valor from valor_divisa v where v.id_divisa = ccb.id_divisa ";
		    if(dto.getFecha()!=null && !dto.getFecha().equals(""))
		    	sql+= "\n       and v.fec_divisa = convert(datetime,'"+funciones.ponerFecha(dto.getFecha())+"', 103) ) end as tipo_cambio, ";
		    else
		    	sql+= "\n       and v.fec_divisa = CURRENT_DATE ) end as tipo_cambio, "; //pruebas A.A.G		    
		    	//sql+= "\n       and v.fec_divisa = '"+funciones.ponerFecha(dto.getFecha())+"' ) end as tipo_cambio, ";
		    sql+= "\n       ccb.id_divisa, ccb.id_chequera ";
		    
		    if(dto.getPsDivision()!=null && dto.getPsDivision()!="")
	        	sql+= "\n	  FROM cat_cta_banco ccb, cat_cta_banco_division ccbd ";
		    else
		        sql+= "\n	  FROM cat_cta_banco ccb ";
		    
		    sql+= "\n	WHERE ccb.tipo_chequera in ('P','M') "; //Pagadoras y Mixtas
		    
		    if(dto.getIdEmpresa()!=0)
		    	sql+= "\n	AND ccb.no_empresa = "+dto.getIdEmpresa();
	        else if(dto.getIdGrupo()!=0){
		        sql+= "\n	 AND ccb.no_empresa in (select no_empresa ";
		        sql+= "\n                         from grupo_empresa ";
		        sql+= "\n                         where id_grupo_flujo = "+dto.getIdGrupo()+") ";
		        //sql+= "\n                         where id_grupo_flujo = 22) ";
	        }
		    else if(dto.getPsDivision()!=null && !dto.getPsDivision().equals("")){
		        sql+= "\n	 AND ccb.no_empresa = ccbd.no_empresa ";
		        sql+= "\n	 AND ccb.id_banco = ccbd.id_banco ";
		        sql+= "\n	 AND ccb.id_chequera = ccbd.id_chequera ";
			    if(dto.getPsDivision()!=null && dto.getPsDivision().equals("TODAS LAS DIVISIONES")){
		            sql+= "\n	 AND ccbd.id_division in ";
		            sql+= "\n	     ( SELECT id_division ";
		            sql+= "\n	       FROM usuario_division ";
		            sql+= "\n	       WHERE no_usuario = "+dto.getIdUsuario()+" ) ";
			    }
			    else{
			    	 sql+= "\n	 AND ccbd.id_division = '"+dto.getPsDivision()+"' ";
			    }
		    }
		    else{
	        	sql+= "\n	 AND ccb.no_empresa in (select no_empresa from  ";
	        		sql+= "                        usuario_empresa where no_usuario = " +dto.getIdUsuario()+") ";
		    }
		    
		    if(dto.getIdDivisa()!=null && !dto.getIdDivisa().equals("")) 
		    	sql+= " AND ccb.id_divisa ='"+dto.getIdDivisa()+"' ";
	    
		    if(dto.getFormaPago()>0)
		    {
		    	switch(dto.getFormaPago())
		    	{
		    	case 1:
		    		sql+= " AND ccb.b_cheque = 'S' ";
		    	break;
		    	case 3:
		    		sql+= " AND ccb.b_transferencia = 'S' ";
		    	break;
			    case 9:
			    	sql+= " AND ccb.b_cheque_ocurre = 'S' ";
		    	break;
		    	}
		    }
		
			System.out.println("saldos chequeras"+sql);
		    listDatos= jdbcTemplate.query(sql, new RowMapper<SaldosChequerasDto>(){
				public SaldosChequerasDto mapRow(ResultSet rs, int idx) throws SQLException {
					SaldosChequerasDto cons = new SaldosChequerasDto();
						cons.setSaldoInicial(rs.getDouble("saldo_inicial"));
						cons.setCargo(rs.getDouble("cargo"));
						cons.setAbono(rs.getDouble("abono"));
						cons.setSaldoFinal(rs.getDouble("saldo_final"));
						cons.setTipoCambio(rs.getDouble("tipo_cambio"));
						cons.setIdDivisa(rs.getString("id_divisa"));
						cons.setIdChequera(rs.getString("id_chequera"));
						
					return cons;
				}});
		   
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultasGenerales, M:obtenerSaldosChequeras");
		}
		return listDatos;
	}

	/*Public Function funSQLUpdateBancoCheqBenef(ByVal pvl_noFolioDet As Long, _
                                           ByVal pvi_idBanco As Integer, _
                                           ByVal pvs_idChequera As String) As Long*/
	public int actualizarBancoCheqBenef(int noFolioDet, int idBanco, String idChequera){
		String sql="";
		try{
			sql+= "UPDATE movimiento ";
		    sql+= "   SET id_banco_benef = " +idBanco+ ",";
		    sql+= "       id_chequera_benef = '" +idChequera+ "',";
		    sql+= "       id_leyenda = '1' ";
		    sql+= " WHERE no_folio_det = " + noFolioDet;
		    sql+= "    OR folio_ref = " + noFolioDet;
		    
		    return jdbcTemplate.queryForInt(sql);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultasGenerales, M:actualizarBancoCheqBenef");
			return 0;
		}
	}
	
	/*Public Function FunSQLSelectBancoCheqBenef(ByVal pvl_noProveedor As Long, ByVal pvs_id_divisa As String, _
                                           Optional ByVal pvi_NoEmpresa As Integer) As ADODB.Recordset
                                           '              id_banco tercer parametro)*/
	
	public List<ComunDto> consultarBancoCheqBenef(ComunDto dto){
		String sql="";
		List<ComunDto> listRet= new ArrayList<ComunDto>();
		try{
			sql+= "SELECT id_banco, id_chequera ";
		    sql+= "  FROM ctas_banco ";
		    sql+= " WHERE no_persona = "+ dto.getIdProveedor();
		    sql+= "   AND id_divisa = '" + dto.getIdDivisa() + "' ";
		    sql+= "   AND id_tipo_persona in ('P','K') ";
		    
		    if(dto.getIdBanco()>0)
		        sql+= " AND id_banco = " + dto.getIdBanco();
		    listRet=jdbcTemplate.query(sql, new RowMapper<ComunDto>(){
		    	public ComunDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		ComunDto cons= new ComunDto();
		    		cons.setIdBanco(rs.getInt("idBanco"));
		    		cons.setIdChequera(rs.getString("id_chequera"));
		    		return cons;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultasGenerales, M:consultarBancoCheqBenef");
		}
		return listRet;
	}
	
	/*Public Function funSQLUpdateBancoCheqBenef(ByVal pvl_noFolioDet As Long, _
                                           ByVal pvi_idBanco As Integer, _
                                           ByVal pvs_idChequera As String) As Long*/
	
	public int actualizarBancoCheqBenef(ComunDto dto){
		String sql="";
		try{
			sql+= "UPDATE movimiento ";
		    sql+= "   SET id_banco_benef = " +dto.getIdBanco()+ ",";
		    sql+= "       id_chequera_benef = '" +dto.getIdChequera()+ "',";
		    sql+= "       id_leyenda = '1' ";
		    sql+= " WHERE no_folio_det = " + dto.getNoFolioDet();
		    sql+= "    OR folio_ref = " + dto.getNoFolioDet();
		    
		    return jdbcTemplate.queryForInt(sql);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultasGenerales, M:actualizarBancoCheqBenef");
			return 0;
		}
	}
	
	/*Public Function FunSQLSelectPagPropApagarAut1(ByVal pvi_noGrupoEmpresa As Integer, _
    ByVal pvs_FecPropuesta As String, _
    ByVal cve_control As String, _
    ByVal pvi_noGrupoRubro As Integer, _
    Optional psDivision As String = "") _
    As ADODB.Recordset*/
	
	public List<MovimientoDto>consultarPagPropAut1(ComunEgresosDto dtoIn){
		List<MovimientoDto> listDatos= new ArrayList<MovimientoDto>();
		String sql="";
		try{
			sql+="\n SELECT m.id_chequera_benef, m.beneficiario, m.no_empresa, m.no_cliente as no_proveedor, ";
		    sql+="\n     m.id_divisa, m.id_forma_pago, ccb.id_banco as id_banco_pag, ";
		    sql+="\n     ccb.id_chequera as id_chequera_pago, m.no_folio_det, m.no_docto ";
		            
		    if(dtoIn.getPsDivision()== null || dtoIn.getPsDivision().equals(""))
		    {
		        sql+="\n FROM movimiento m ";
		        sql+="\n     INNER JOIN grupo_empresa ge ";
		        sql+="\n       ON (m.no_empresa = ge.no_empresa) ";
		        sql+="\n     INNER JOIN empresa e ";
		        sql+="\n       ON (ge.no_empresa = e.no_empresa) ";
		        sql+="\n     INNER JOIN cat_grupo_flujo cgf ";
		        sql+="\n       ON (ge.id_grupo_flujo = cgf.id_grupo_flujo) ";
		        sql+="\n     INNER JOIN grupo_flujo_cupo gfc ";
		        sql+="\n       ON (m.id_rubro = gfc.id_rubro) ";
		        sql+="\n     INNER JOIN cat_grupo_cupo cgc ";
		        sql+="\n       ON (gfc.id_grupo_cupo = cgc.id_grupo_cupo) ";
		        sql+="\n     INNER JOIN cat_cta_banco ccb ";
		        sql+="\n       ON (m.id_divisa = ccb.id_divisa ";
		        sql+="\n           AND m.no_empresa = ccb.no_empresa) ";
		    }
		    else{
		    	sql+="\n FROM movimiento m ";
		        sql+="\n     INNER JOIN empresa ";
		        sql+="\n       ON (m.no_empresa = empresa.no_empresa) ";
		        sql+="\n     INNER JOIN grupo_flujo_cupo gfc ";
		        sql+="\n       ON (m.id_rubro = gfc.id_rubro) ";
		        sql+="\n     INNER JOIN cat_grupo_cupo cgc ";
		        sql+="\n       ON (gfc.id_grupo_cupo = cgc.id_grupo_cupo) ";
		        sql+="\n     INNER JOIN cat_cta_banco ccb ";
		        sql+="\n       ON (m.id_divisa = ccb.id_divisa ";
		        sql+="\n           AND m.no_empresa = ccb.no_empresa) ";
		        sql+="\n     INNER JOIN cat_cta_banco_division ccbd ";
		        sql+="\n       ON (ccb.no_empresa = ccbd.no_empresa ";
		        sql+="\n           AND ccb.id_banco = ccbd.id_banco ";
		        sql+="\n           AND ccb.id_chequera = ccbd.id_chequera ";
		        sql+="\n           AND ccbd.id_division = '" +dtoIn.getPsDivision()+ "') ";
		    }
		    
		    sql+="\n  WHERE ((m.id_forma_pago = 1 and ccb.b_cheque ='S')";
	        sql+="\n     OR  (m.id_forma_pago = 3 and ccb.b_transferencia = 'S')";
	        sql+="\n     OR  (m.id_forma_pago = 5 and ccb.b_cargo_en_cuenta = 'S')";
	        sql+="\n     OR  (m.id_forma_pago = 9 and ccb.b_cheque_ocurre = 'S'))";
	        sql+="\n    AND m.id_tipo_movto = 'E'";
	        sql+="\n    AND m.id_estatus_mov in ('N', 'C', 'F')";
	        sql+="\n    AND ((m.id_forma_pago = 3 and m.id_leyenda <> '*') ";
	        sql+="\n     OR (m.id_forma_pago in (1, 5, 9)))";
	        sql+="\n    AND m.fec_propuesta = '" +funciones.ponerFecha(dtoIn.getFecha())+ "'";
	        sql+="\n    AND m.cve_control = '" +dtoIn.getCveControl()+ "'";
	        sql+="\n    AND gfc.id_grupo_cupo = " +dtoIn.getIdGrupoRubros();
	        sql+="\n    AND not m.id_chequera_benef like 'CONV_______' ";
	        
	        if(gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2")) 
	            sql+="\n    AND length(m.id_chequera_benef) <> 16 ";
	        else
	            sql+="\n    AND len(m.id_chequera_benef) <> 16 ";
	        
	        sql+="\n UNION ALL ";
	        
	        sql+="\n SELECT m.id_chequera_benef, m.beneficiario, m.no_empresa, m.no_cliente as no_proveedor, ";
            sql+="\n     m.id_divisa, m.id_forma_pago, ";
            sql+="\n     m.id_banco_benef as id_banco_pag, ";
            
            if(gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2")) 
                sql+="\n ( SELECT coalesce(ccbv.id_chequera, ''), m.id_chequera_benef  ";
            else
                sql+="\n ( SELECT TOP 1 coalesce(ccbv.id_chequera, ''), m.id_chequera_benef ";
           
            
            sql+="\n       FROM cat_cta_banco ccbv";
            
            if(dtoIn.getPsDivision()!=null && !dtoIn.getPsDivision().equals(""))
                sql+="\n       ,cat_cta_banco_division ccbd";
            
            
            sql+="\n       Where ccbv.no_empresa = m.no_empresa";
            
            if(dtoIn.getPsDivision()!=null && !dtoIn.getPsDivision().equals(""))
            {
                sql+="\n       AND ccbv.no_empresa = ccbd.no_empresa";
                sql+="\n       AND ccbv.id_chequera = ccbd.id_chequera";
                sql+="\n       AND ccbv.id_banco = ccbd.id_banco";
                sql+="\n       AND ccbv.id_division = ccbd.id_division";
            }
        
            
            sql+="\n           AND ccbv.id_divisa = m.id_divisa ";
            sql+="\n           AND ccbv.id_banco = m.id_banco_benef ";
            sql+="\n           AND ccbv.tipo_chequera in ('P', 'M') ";
            
            if(gsDBM.equals("POSTGRESQL"))
                sql+="\n   LIMIT 1 ";
            
            sql+="\n     ) as id_chequera_pago, m.no_folio_det ";
            
            //'''''''''''''duda si esta bian aqui ''''''''''''
            if(gsDBM.equals("DB2"))
                sql+="\n   fetch first 1 row only ";
            
            if(dtoIn.getPsDivision()==null || dtoIn.getPsDivision().equals("")) 
            {
	            sql+="\n FROM movimiento m ";
	            sql+="\n     INNER JOIN grupo_empresa ge ";
	            sql+="\n       ON (m.no_empresa = ge.no_empresa) ";
	            sql+="\n     INNER JOIN empresa e ";
	            sql+="\n       ON (ge.no_empresa = e.no_empresa) ";
	            sql+="\n     INNER JOIN cat_grupo_flujo cgf ";
	            sql+="\n       ON (ge.id_grupo_flujo = cgf.id_grupo_flujo) ";
	            sql+="\n     INNER JOIN grupo_flujo_cupo gfc ";
	            sql+="\n       ON (m.id_rubro = gfc.id_rubro) ";
	            sql+="\n     INNER JOIN cat_grupo_cupo cgc ";
	            sql+="\n       ON (gfc.id_grupo_cupo = cgc.id_grupo_cupo) ";
            }
	        else{
	            sql+="\n FROM movimiento m ";
	            sql+="\n     INNER JOIN empresa ";
	            sql+="\n       ON (m.no_empresa = empresa.no_empresa) ";
	            sql+="\n     INNER JOIN grupo_flujo_cupo gfc ";
	            sql+="\n       ON (m.id_rubro = gfc.id_rubro) ";
	            sql+="\n     INNER JOIN cat_grupo_cupo cgc ";
	            sql+="\n       ON (gfc.id_grupo_cupo = cgc.id_grupo_cupo) ";
	        }
	        
	        sql+="\n  WHERE m.id_tipo_movto = 'E'";
	        sql+="\n    AND m.id_estatus_mov in ('N', 'C', 'F')";
	        sql+="\n    AND (( m.id_forma_pago = 3 and m.id_leyenda <> '*' ) ";
	        sql+="\n     OR  ( m.id_forma_pago in (1, 5, 9) ) ) ";
	        sql+="\n    AND m.fec_propuesta = '" +funciones.ponerFecha(dtoIn.getFecha())+ "'";
	        sql+="\n    AND m.cve_control = '" +dtoIn.getCveControl()+ "'";
	        sql+="\n    AND gfc.id_grupo_cupo = " +dtoIn.getIdGrupoRubros();
		    
	        if(gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2")) 
	        	sql+="\n    AND length(m.id_chequera_benef) = 16 ";
		    else
		        sql+="\n    AND len(m.id_chequera_benef) = 16 ";
	        
	        if(consultarConfiguraSet(205)!=null && consultarConfiguraSet(205).equals("SI"))
	        {
	        
		        sql+="\n UNION ALL ";
		        
		        sql+="\n SELECT m.id_chequera_benef, m.beneficiario, m.no_empresa, m.no_cliente as no_proveedor, ";
		        sql+="\n     m.id_divisa, m.id_forma_pago, ";
		        sql+="\n     m.id_banco_benef as id_banco_pag, ";
		        
		        if(gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2")) 
		            sql+="\n ( SELECT coalesce(ccbv.id_chequera, ''), m.id_chequera_benef ";
		        else
		            sql+="\n ( SELECT TOP 1 coalesce(ccbv.id_chequera, '', m.id_chequera_benef";
		        
		        sql+="\n       FROM cat_cta_banco ccbv";
		        
		        if(dtoIn.getPsDivision()!=null && !dtoIn.getPsDivision().equals(""))
		            sql+="\n       ,cat_cta_banco_division ccbd";
		        
		        sql+="\n       Where ccbv.no_empresa = m.no_empresa";
		        
		        if(dtoIn.getPsDivision()!=null && !dtoIn.getPsDivision().equals(""))
		        {
		            sql+="\n       AND ccbv.no_empresa = ccbd.no_empresa";
		            sql+="\n       AND ccbv.id_chequera = ccbd.id_chequera";
		            sql+="\n       AND ccbv.id_banco = ccbd.id_banco";
		            sql+="\n       AND ccbv.id_division = ccbd.id_division";
		        }
		        
		        sql+="\n           AND ccbv.id_divisa = m.id_divisa ";
		        sql+="\n           AND ccbv.id_banco = m.id_banco_benef ";
		        sql+="\n           AND ccbv.tipo_chequera in ('P', 'M') ";
		        
		        if(gsDBM.equals("POSTGRESQL"))
		            sql+="\n   LIMIT 1 ";
		            
		        sql+="\n     ) as id_chequera_pago, m.no_folio_det ";
		        
		        //'''''''duda si esta bien''''''''''
		        if(gsDBM.equals("POSTGRESQL")) 
		            sql+="\n   fetch first 1 row only ";
		        
		                
		        if(dtoIn.getPsDivision()==null || dtoIn.getPsDivision().equals(""))
		        {
		            sql+="\n FROM movimiento m ";
		            sql+="\n     INNER JOIN grupo_empresa ge ";
		            sql+="\n       ON (m.no_empresa = ge.no_empresa) ";
		            sql+="\n     INNER JOIN empresa e ";
		            sql+="\n       ON (ge.no_empresa = e.no_empresa) ";
		            sql+="\n     INNER JOIN cat_grupo_flujo cgf ";
		            sql+="\n       ON (ge.id_grupo_flujo = cgf.id_grupo_flujo) ";
		            sql+="\n     INNER JOIN grupo_flujo_cupo gfc ";
		            sql+="\n       ON (m.id_rubro = gfc.id_rubro) ";
		            sql+="\n     INNER JOIN cat_grupo_cupo cgc ";
		            sql+="\n       ON (gfc.id_grupo_cupo = cgc.id_grupo_cupo) ";
		        }else{
		        	sql+="\n FROM movimiento m ";
		            sql+="\n     INNER JOIN empresa ";
		            sql+="\n       ON (m.no_empresa = empresa.no_empresa) ";
		            sql+="\n     INNER JOIN grupo_flujo_cupo gfc ";
		            sql+="\n       ON (m.id_rubro = gfc.id_rubro) ";
		            sql+="\n     INNER JOIN cat_grupo_cupo cgc ";
		            sql+="\n       ON (gfc.id_grupo_cupo = cgc.id_grupo_cupo) ";
		        }
		        
		        sql+="\n  WHERE m.id_tipo_movto = 'E'";
		        sql+="\n    AND m.id_estatus_mov in ('N', 'C', 'F')";
		        sql+="\n    AND (( m.id_forma_pago = 3 and m.id_leyenda <> '*' ) ";
		        sql+="\n     OR  ( m.id_forma_pago in (1, 5, 9) ) ) ";
		        sql+="\n    AND m.fec_propuesta = '" +funciones.ponerFecha(dtoIn.getFecha())+ "'";
		        sql+="\n    AND m.cve_control = '" +dtoIn.getCveControl()+ "'";
		        sql+="\n    AND gfc.id_grupo_cupo = " +dtoIn.getIdGrupoRubros();
		        sql+="\n    AND m.id_chequera_benef like 'CONV_______' ";
	        }
		    
		    if(gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2")) 
		    {
		        sql+="\n ORDER BY no_empresa, no_proveedor, id_divisa, id_forma_pago, ";
		        sql+="\n          id_banco_pag, id_chequera_pago,no_folio_det ";
		    }else
		    {
		    	sql+="\n ORDER BY m.no_empresa, no_cliente, m.id_divisa, id_forma_pago,";
		        sql+="\n          ccb.id_banco, ccb.id_chequera,no_folio_det ";
		    }
		    System.out.println("consultarPagPropAut1 "+sql);
		    listDatos= jdbcTemplate.query(sql, new RowMapper<MovimientoDto>(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons= new MovimientoDto();
					cons.setBeneficiario(rs.getString("beneficiario"));
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setIdProveedor(rs.getInt("no_proveedor"));
					cons.setIdDivisa(rs.getString("id_divisa"));
					cons.setIdFormaPago(rs.getInt("id_forma_pago"));
					cons.setIdBancoPago(rs.getInt("id_banco_pag"));
					cons.setIdChequeraPago(rs.getString("id_chequera_pago"));
					cons.setNoFolioDet(rs.getInt("no_folio_det"));
					cons.setNoDocto(rs.getString("no_docto"));
					cons.setIdChequeraBenef("id_chequera_benef");
					return cons;
				}});
	        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultasGenerales, M:consultarPagPropAut1");
		}
		return listDatos;
	}
	
	/*Public Function InsertaParametrofactoraje(ByVal NoUsuario As Integer, ByVal sFolios As String, ByVal pl_folio As Long, _
    ByVal pl_folio2 As Long, ByVal fecha_hoy As String) As Long*/
	public int insertarParametroFactoraje(ParametroFactorajeDto dto){
		String sql="",sql1="",sql2="",sqlI="";
		try{
			sqlI = " ";
		    sqlI +="\n INSERT INTO parametro (";;
		    sqlI +="\n     no_empresa, no_folio_param, id_banco, id_chequera, id_tipo_saldo, id_tipo_docto, ";
		    sqlI +="\n     id_tipo_operacion, dias_inv, usuario_imprime, usuario_alta, no_folio_mov, id_inv_cbolsa, no_cuenta, ";
		    sqlI +="\n     no_cheque, no_docto, folio_ref, no_cuenta_ref, lote, folio_seg, centro_costo, fec_valor,  ";
		    sqlI +="\n     fec_valor_original, fec_recalculo, fec_operacion, fec_exporta, fec_imprime, fec_reimprime, fec_cheque, fec_alta, importe, valor_tasa, importe_original, ";
		    sqlI +="\n     tipo_cambio, id_divisa, id_forma_pago, id_divisa_original, referencia, folio_banco, beneficiario, ";
		    sqlI +="\n     id_caja, concepto, id_banco_benef, id_chequera_benef, aplica, id_estatus_mov, b_salvo_buen_cobro, ";
		    sqlI +="\n     id_estatus_reg, secuencia, id_leyenda, arch_protegido, origen_reg, sucursal, plaza, no_cliente, ";
		    sqlI +="\n     no_cobrador, observacion, no_recibo, origen_mov, no_docto_cus, solicita, autoriza, b_entregado, grupo, ";
		    sqlI +="\n     tipo_cancelacion, id_codigo, id_subcodigo, fisica_moral, nac_ext, id_banco_reg, id_chequera_reg, ";
		    sqlI +="\n     importe_desglosado, id_query, no_factura, id_rubro, agrupa1, agrupa2, agrupa3, rfc, deudor, descripcion, ";
		    sqlI +="\n     rango, no_pedido, referencia_ban, division, ind_iva, oper_may_esp, hora_recibo, fec_concilia_caja, ";
		    sqlI +="\n     fec_propuesta, cod_bloqueo, id_area, monto_sobregiro,invoice_type)";
		    
		    sql1 = "";
	        sql1 +="\n(SELECT no_empresa, " +dto.getPlFolio()+ ", id_banco, id_chequera, id_tipo_saldo, id_tipo_docto, ";
	        sql1 +="\n   3200, dias_inv, usuario_imprime, " + dto.getNoUsuario() + ", 1, 0, no_cuenta, ";
	        sql1 +="\n   no_cheque, no_docto, no_folio_det, no_cuenta_ref, lote_entrada, folio_seg, null, '" +funciones.ponerFecha(dto.getFechaHoy())+ "', ";
	        sql1 +="\n   fec_valor_original, fec_recalculo, fec_operacion, fec_exportacion, fec_imprime, fec_reimprime, ";
	        sql1 +="\n   fec_cheque, '" +funciones.ponerFecha(dto.getFechaHoy())+ "', importe, valor_tasa, importe_original, tipo_cambio, id_divisa, ";
	        sql1 +="\n   id_forma_pago, id_divisa_original, referencia, folio_banco, beneficiario, id_caja, concepto, ";
	        sql1 +="\n   id_banco_benef, id_chequera_benef, 2, 'K', b_salvo_buen_cobro, ";
	        sql1 +="\n   'P', NULL, id_leyenda, arch_protegido, '', sucursal, plaza, no_cliente, no_cobrador, ";
	        sql1 +="\n   observacion, no_recibo, origen_mov, no_docto_cus, solicita, autoriza, b_entregado, " +dto.getPlFolio()+  ", ";
	        sql1 +="\n   tipo_cancelacion, id_codigo, id_subcodigo, fisica_moral, nac_ext, NULL, ";
	        sql1 +="\n   NULL, importe_desglosado, NULL, no_factura, id_rubro, agrupa1, agrupa2, ";
	        sql1 +="\n   agrupa3, rfc, deudor, descripcion, rango, no_pedido, referencia_ban, division, ind_iva, oper_may_esp, hora_recibo,";
	        sql1 +="\n   fec_concilia_caja , fec_propuesta, cod_bloqueo, id_area, monto_sobregiro,invoice_type ";
	        sql1 +="\n FROM movimiento ";
	        sql1 +="\n WHERE id_tipo_operacion = 3000 and id_estatus_mov = 'N'";
	        sql1 +="\n   and id_forma_pago = 7 and no_folio_det = " +dto.getSFolios()+ ")";
	        
	        sql2 = "";
            sql2 +="\n(SELECT no_empresa, " + dto.getPlFolio2()+ ", id_banco, id_chequera, id_tipo_saldo, id_tipo_docto, ";
            sql2 +="\n   3201, dias_inv, usuario_imprime, " + dto.getNoUsuario()+ " , 1, 0, no_cuenta, ";
            sql2 +="\n   no_cheque, no_docto, folio_ref, no_cuenta_ref, lote_entrada, folio_seg, null, '" +funciones.ponerFecha(dto.getFechaHoy())+ "', ";
            sql2 +="\n   fec_valor_original, fec_recalculo, '" +funciones.ponerFecha(dto.getFechaHoy())+ "', fec_exportacion, fec_imprime, fec_reimprime, ";
            sql2 +="\n   fec_cheque, '" +funciones.ponerFecha(dto.getFechaHoy())+ "', importe, valor_tasa, importe_original, tipo_cambio, id_divisa, ";
            sql2 +="\n   id_forma_pago, id_divisa_original, referencia, folio_banco, beneficiario, id_caja, concepto, ";
            sql2 +="\n   id_banco_benef, id_chequera_benef, 2, id_estatus_mov, b_salvo_buen_cobro, ";
            sql2 +="\n   'P', NULL, id_leyenda, arch_protegido, '', sucursal, plaza, no_cliente, no_cobrador, ";
            sql2 +="\n   observacion, no_recibo, origen_mov, no_docto_cus, solicita, autoriza, b_entregado, " +dto.getPlFolio()+ ", ";
            sql2 +="\n   tipo_cancelacion, id_codigo, id_subcodigo, fisica_moral, nac_ext, NULL, ";
            sql2 +="\n   NULL, importe_desglosado, NULL, no_factura, id_rubro, agrupa1, agrupa2, ";
            sql2 +="\n   agrupa3, rfc, deudor, descripcion, rango, no_pedido, referencia_ban, division, ind_iva, oper_may_esp, hora_recibo,";
            sql2 +="\n   fec_concilia_caja , fec_propuesta, cod_bloqueo, id_area, monto_sobregiro, invoice_type ";
            sql2 +="\n FROM movimiento ";
            sql2 +="\n WHERE id_tipo_operacion = 3001 and id_estatus_mov = 'H'";
            sql2 +="\n   and id_forma_pago = 7 and folio_ref = " +dto.getSFolios()+ ")";
            
            sql = "";
            sql += sqlI + " " + sql1 + ";" + sqlI + " " + sql2;
	        
	        return jdbcTemplate.update(sql);

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultasGenerales, M:insertarParametroFactoraje");
			return 0;
		}
	}
	
	/*Public Function FunSQLUpdate_solicitud_factoraje(ByVal plNoFolioDet As Long) As Long*/
	public int actualizarSolicitudFactoraje(int noFolioDet){
		String sql="";
		try{
			sql += " UPDATE movimiento set id_estatus_mov = 'A' ";
	        sql += "\n WHERE id_tipo_operacion = 3000 ";
	        sql += "\n and no_folio_det = " + noFolioDet;
	        
	        return jdbcTemplate.update(sql);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultasGenerales, M:actualizarSolicitudFactoraje");
			return 0;
		}
	}
	
	//Modificaion realizada por america para la importacion de proveedores del host to host
	
	public Map<String, Object> importaProveedor(int usuario, int noEmpresa)
	{
		Map<String, Object> resultado = new HashMap<String, Object>();
		int result = 0;
		try
		{
			StoredProcedure store = new StoredProcedure(jdbcTemplate.getDataSource(), "WS001_ImportaProv") {};
			store.declareParameter(new SqlParameter("vUsuario", Types.INTEGER));
			store.declareParameter(new SqlParameter("vEmpresa", Types.INTEGER));
			store.declareParameter(new SqlParameter("vResult", Types.INTEGER));
			
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("vUsuario", usuario);
			parametros.put("vEmpresa", noEmpresa);
			parametros.put("vResult", result);
			
			resultado = store.execute(parametros);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:ConsultasGenerales, M:importaProveedor");
		}
		return resultado;
	}
	
	//Modificaion realizada por america para la generacion de propuestas automaticas en la importacion de interfaz egresos
	
	public Map<String, Object> importaMovimientos(int usuario, String noEmpresa, int valor)
	{
		logger.debug("Entra importaMovimientos");
		Map<String, Object> resultado = new HashMap<String, Object>();
		int result = 0;
		
		try
		{
			StoredProcedure store = new StoredProcedure (jdbcTemplate.getDataSource(), "WS002_ImportacxpProc") {};
			store.declareParameter(new SqlParameter("vUsuario", Types.INTEGER));
			store.declareParameter(new SqlParameter("vEmpresa", Types.INTEGER));
			store.declareParameter(new SqlParameter("vValor", Types.INTEGER));
			store.declareParameter(new SqlParameter("vResult", Types.INTEGER));
			
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("vUsuario", usuario);
			parametros.put("vEmpresa", noEmpresa);
			parametros.put("vValor", valor);
			parametros.put("vResult", result);
			
			logger.info("Antes de ejecutar el store: WS002_ImportacxpProc");
			resultado = store.execute(parametros);
			logger.info("Store ejecutado: WS002_ImportacxpProc");
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:ConsultasGenerales, M:importaMovimientos");
		}
		logger.debug("Sale importaMovimientos [resultado = " + resultado + "]");
		return resultado;
	}
	
	/*public Map<String, Object> ejecutarPagador(final StoreParamsComunDto dto) {
	    return transactionTemplate.execute(new TransactionCallback<Map<String, Object>>() {
	    	@Override
	    	public Map<String, Object> doInTransaction(TransactionStatus transactionStatus) {
	    		Map<String, Object> resultado= new HashMap<String, Object>();
	    		try {
	    			com.webset.utils.tools.StoredProcedure storedProcedure = 
	    					new com.webset.utils.tools.StoredProcedure(jdbcTemplate, "sp_sql_Pagador");
	    			
	    			storedProcedure.declareParameter("parametro", dto.getParametro(), com.webset.utils.tools.StoredProcedure.IN);
	    			storedProcedure.declareParameter("mensaje", Types.VARCHAR, 	com.webset.utils.tools.StoredProcedure.OUT);
	    			storedProcedure.declareParameter("result", Types.INTEGER, com.webset.utils.tools.StoredProcedure.OUT);
	    			
	    			resultado = storedProcedure.execute();				
	    			System.out.println(resultado);		
	    			if((int) resultado.get("result") != 0) throw new Exception("error");
			    } catch (Exception e) {
			    	transactionStatus.setRollbackOnly();
			    }
			    return resultado;
	    	}
	    });
    }*/
	public Map<String, Object> correHistorico() {
		return transactionTemplate.execute(new TransactionCallback<Map<String, Object>>() {
    	@Override
	    	public Map<String, Object> doInTransaction(TransactionStatus transactionStatus) {
	    		Map<String, Object> resultado= new HashMap<String, Object>();
	    		try {
					logger.debug("Entra: correHistorico");
					int result = 0;
					String mensaje = "";
			
					logger.info("Antes Store sp_historico");
					StoredProcedure store = new StoredProcedure (jdbcTemplate.getDataSource(), "sp_historico") {};
					store.declareParameter(new SqlInOutParameter("vResult", Types.INTEGER));
					store.declareParameter(new SqlInOutParameter("vMensaje",Types.VARCHAR));
							
					HashMap<String, Object> parametros = new HashMap<String, Object>();						
					parametros.put("vResult", result);
					parametros.put("vMensaje", mensaje);
					
					resultado = store.execute(parametros);
					logger.info("Despues Store sp_historico. Resultado: ["+resultado+"]");

				 	bitacora.insertarRegistro(new Date().toString() + 
				 			" Despues Store sp_historico. Resultado: [" + resultado.get("vResult") + "]");
				 	
					if(Integer.parseInt(resultado.get("vResult") + "") > 0) throw new Exception("error");
	    		 } catch (Exception e) {
	    			e.printStackTrace();
				 	transactionStatus.setRollbackOnly();
				 	bitacora.insertarRegistro(new Date().toString() + "[Error]: " + resultado.get("vResult"));
				 }
				 return resultado;
			}
    	});
	}
	
	
	public Map<String, Object> actualizaChequerasProv(int noPersona)
	{
		Map<String, Object> resultado = new HashMap<String, Object>();
		int result = 0;
		String desc_result = "";
		
		try
		{			
			StoredProcedure store = new StoredProcedure (jdbcTemplate.getDataSource(), "sp_ActualizaChequera") {};
			store.declareParameter(new SqlParameter("vNoPersona", Types.INTEGER));
			store.declareParameter(new SqlParameter("vResult", Types.INTEGER));
			store.declareParameter(new SqlParameter("vDescResult",Types.VARCHAR));	
			
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("vNoPersona", noPersona);
			parametros.put("vResult", result);	
			parametros.put("vDescResult",desc_result);
			
			resultado = store.execute(parametros);
			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConsultasGenerales, M:actualizaChequerasProv");
		}return resultado;
	}
	
	
	public Map<String, Object> confirmacionEdosCta(String fecHoy){
		Map<String, Object> resultado = new HashMap<String, Object>();
		int result = 0;		
		try{
			StoredProcedure store = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_confirmador_edos_cta") {};
			store.declareParameter(new SqlParameter("vFecHoy", Types.VARCHAR));
			//store.declareParameter(new SqlParameter("vResult", Types.INTEGER));
			
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("vFecHoy", fecHoy);
			parametros.put("vResult", result);
			
			resultado = store.execute(parametros);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ConsultasGenerales, M: confirmacionEdosCta");
		}return resultado;		
	}
	
	
	public Map<String, Object> llamaExportador(String folios, String prefijo){
		logger.debug("Entra a llamaExportador");
		Map<String, Object> resultadoExportador = new HashMap<String, Object>();
		int result = 0;
		String mensaje = "";
		
		try{
//			StoredProcedure store = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_exportador") {};
			StoredProcedure store = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_exportadorAgrup") {};
			store.declareParameter(new SqlParameter("vFolios", Types.VARCHAR));
			store.declareParameter(new SqlParameter("vPrefijo", Types.VARCHAR));
			store.declareParameter(new SqlParameter("vResult", Types.INTEGER));
			store.declareParameter(new SqlParameter("vMensaje", Types.VARCHAR));
			
			HashMap<Object, Object> parametros = new  HashMap<Object, Object>();
			parametros.put("vFolios", folios);
			parametros.put("vPrefijo", prefijo);
			parametros.put("vResult", result);
			parametros.put("vMensaje", mensaje);
			
			logger.info("Antes de llamar store: sp_exportador");
			resultadoExportador = store.execute(parametros);
			logger.info("Despues de llamar store: sp_exportador");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Interfaz, C: ConsultasGenerales, M: llamaExportador");
		}
		logger.debug("Sale de llamaExportador");
		return resultadoExportador;
	}
	
	public int validaFacultad(int idFacultad) {
		StringBuffer sql = new StringBuffer();
		globalSingleton = GlobalSingleton.getInstancia();
		int res = 0;
		
		try {
			sql.append(" SELECT count(*) \n");
			sql.append(" FROM SEG_COMPONENTE sc, SEG_PER_FAC_COM spfc, SEG_USUARIO_PERFIL sup, SEG_USUARIO su \n");
			sql.append(" WHERE sc.id_componente = spfc.id_componente \n");
			sql.append(" 	And spfc.id_perfil = sup.id_perfil \n");
			sql.append(" 	And sup.id_usuario = su.id_usuario \n");
			sql.append(" 	And su.id_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + " \n");
			sql.append(" 	And sc.id_componente = "+ idFacultad +" \n");
			sql.append(" 	And spfc.id_facultad = 2 \n");
			
			res = jdbcTemplate.queryForInt(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Interfaz, C: ConsultasGenerales, M: validaFacultad");
		}
		return res;
	}
	
	
	public Date fechaHoy() {
		sb = new StringBuffer();
		sb.append(" SELECT fec_hoy FROM FECHAS ");
		
		try {
			 List<String> fechas = jdbcTemplate.query(sb.toString(), new RowMapper<String>() {
				public String mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getString("fec_hoy");
				}
			});
			
			return funciones.ponerFechaDate(fechas.get(0));
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Utilerias, C:ConsultasDao, M:obtenerFechaHoy");
			return null;
		}
	}
	
	public StringBuffer getSb() {
		return sb;
	}

	public void setSb(StringBuffer sb) {
		this.sb = sb;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Bitacora getBitacora() {
		return bitacora;
	}

	public void setBitacora(Bitacora bitacora) {
		this.bitacora = bitacora;
	}

	public Funciones getFunciones() {
		return funciones;
	}

	public void setFunciones(Funciones funciones) {
		this.funciones = funciones;
	}

	public String getGsDBM() {
		return gsDBM;
	}

	public void setGsDBM(String gsDBM) {
		this.gsDBM = gsDBM;
	}

	public int obtenFolio(String secuencia) {
		Map<String, Object> resultado = new HashMap<String, Object>();
		try {
			
			com.webset.utils.tools.StoredProcedure storedProcedure = 
					new com.webset.utils.tools.StoredProcedure(jdbcTemplate, "sp_obtenFolio");
			
			storedProcedure.declareParameter("tipo_folio", 
					secuencia, 
					com.webset.utils.tools.StoredProcedure.IN);
			
			storedProcedure.declareParameter("folio", 
					Types.INTEGER, 
					com.webset.utils.tools.StoredProcedure.OUT);
			
			storedProcedure.declareParameter("result", 
					Types.INTEGER, 
					com.webset.utils.tools.StoredProcedure.OUT);
			
			storedProcedure.declareParameter("mensaje", 
					Types.VARCHAR, 
					com.webset.utils.tools.StoredProcedure.OUT);
			
			resultado = storedProcedure.execute();	
			/*
			StoredProcedure store = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_obtenFolio") {};
			store.declareParameter(new SqlParameter("tipo_folio", Types.VARCHAR));
			store.declareParameter(new SqlOutParameter("folio", Types.INTEGER));
			store.declareParameter(new SqlOutParameter("result", Types.INTEGER));
			store.declareParameter(new SqlOutParameter("mensaje",Types.VARCHAR));
			
			store.compile();
			
			HashMap<Object, Object> parametros = new  HashMap<Object, Object>();
			parametros.put("tipo_folio", secuencia);
			/*parametros.put("folio", folio);
			parametros.put("result", result);
			parametros.put("mensaje", mensaje);
			
			resultado = store.execute(parametros);*/
			//System.out.println(resultado == null);
			System.out.println(resultado);
			/*Collection<Object> collection = resultado.values();
			for (Object object : collection) {
				System.out.println(object.toString());
			}*/
			return (Integer)resultado.get("folio");
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Utilerias, C: ConsultasGenerales, M: obtenFolio");
		}
		return -1;
	}
	                                                 
	/*
	static public void main(String []args){
		//logger.info(System.getProperty("user.dir"));
		//XmlBeanFactory beanFactory = new XmlBeanFactory(new FileSystemResource("C:\\proyectos\\webset\\workspace\\SETX\\WebRoot\\WEB-INF\\applicationContext.xml"));
		//AdministradorArchivosBusiness obj = (AdministradorArchivosBusiness) beanFactory.getBean("administradorArchivosBusiness");
		//ParametroLayoutDto dto = new ParametroLayoutDto();
		ConsultasGenerales consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		
		int idUsuario = 1;

		// CONSTRUCCION DE SUBQUERY
		List<String> camposUsuPerf = new ArrayList<String>();
		camposUsuPerf.add("COUNT(1)");

		List<String> tablasUsuPerf = new ArrayList<String>();
		tablasUsuPerf.add("SEG_USUARIO_PERFIL UP");
		
		List<String> joinsUsuPerf = new ArrayList<String>();
		joinsUsuPerf.add("UP.ID_PERFIL = PER.ID_PERFIL");
		
		List<String> filterUsuPerf = new ArrayList<String>();
		filterUsuPerf.add("UP.ID_USUARIO = "+idUsuario);

		String queryUsuPerf = consultasGenerales.obtenerQuery(camposUsuPerf, tablasUsuPerf, joinsUsuPerf, filterUsuPerf, null);
		
		// CONSTRUCCION DE QUERY PRINCIPAL
		List<String> campos = new ArrayList<String>();
		campos.add("ID_PERFIL");
		campos.add("DESCRIPCION");
		campos.add("ESTATUS");
		campos.add("("+queryUsuPerf+") INCLUIDO");
		
		List<String> tablas = new ArrayList<String>();
		tablas.add("SEG_PERFIL PER");
		
		//List<String> joins = new ArrayList<String>();
		//joins.add("P.ID_PERFIL = UP.ID_PERFIL");
		
		List<String> condiciones = new ArrayList<String>();
		condiciones.add("ESTATUS = 'A'");

		List<String> orden = new ArrayList<String>();
		orden.add("ID_PERFIL");
		
		List<Map<String, Object>> listDatos = consultasGenerales.obtenerListaDatos(campos, tablas, null, condiciones, orden);
		
		logger.info("//////////////////////////////////" + listDatos);
		//logger.info(mensajes.toString());
		//logger.info(mensajesUsuario.toString());}}
		
		// EJEMPLO DE CONDICIONES COMPLEJAS PARA UN WHERE DE UN SQL EN JS
		NS.storeDivision.baseParams.condicionesX = {
			listaCondiciones:[
				{
					tipoSentencia: 'AND', 
					listaComparaciones: [
						{nombreCampo: 'campo1',operador: 'IGUAL', valor: 'campo1'},
						{nombreCampo: 'no_empresa',operador: 'igual',valor: combo.getValue()},
						{nombreCampo: 'campo2',operador: 'menorque',valor: combo.getValue()},
					]
				},
				{
					tipoSentencia: 'OR', 
					listaComparaciones: [
						{nombreCampo: 'no_empresa',operador: 'igual',valor: combo.getValue()},
						{nombreCampo: 'campo2',operador: 'menorque',valor: combo.getValue()},
					]
				},
			]
		};*/


	public  Map<String, Object> ejecutarBuscaMovRegla(int idRegla) {
		Map<String, Object> resultado= new HashMap<String, Object>();
		
		try {
			com.webset.utils.tools.StoredProcedure storedProcedure = 
					new com.webset.utils.tools.StoredProcedure(jdbcTemplate, "SP_BUSCA_MOV_REGLA");
			
			storedProcedure.declareParameter("V_id_regla", 
					idRegla, 
					com.webset.utils.tools.StoredProcedure.IN);
			
			storedProcedure.declareParameter("V_folios", 
					Types.VARCHAR, 
					com.webset.utils.tools.StoredProcedure.OUT);
			
			storedProcedure.declareParameter("V_no_usuario", 
					GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario(), 
					com.webset.utils.tools.StoredProcedure.IN);
			
			
			resultado = storedProcedure.execute();			
		
			}catch (Exception e) {
			
		}
		return resultado; 
	}

	public Map<String, Object> importaProv() {
		Map<String, Object> resultado = new HashMap<String, Object>();
		resultado.put("Error", true);
		
		try {
			
			com.webset.utils.tools.StoredProcedure store = 
					new com.webset.utils.tools.StoredProcedure(jdbcTemplate, 
					"SP_LLAMA_PO");
			
			resultado = store.execute();
			
		} catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P:Utilerias, C:ConsultasGenerales, M:importaProv");
			
		}
		
		return resultado;
	}

	public Map<String, Object> importaPasivos() {
		Map<String, Object> resultado = new HashMap<String, Object>();
		resultado.put("Error", true);
		
		try {
			
			com.webset.utils.tools.StoredProcedure store = 
					new com.webset.utils.tools.StoredProcedure(jdbcTemplate, 
					"SP_IMPORTARCXP_ALL");
			
			resultado = store.execute();
			
		} catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P:Utilerias, C:ConsultasGenerales, M:importaPasivos");
			
		}
		
		return resultado;
	}	
	
	public List<LlenaComboGralDto> obtenerGrupo(){
		String sql="";
		List<LlenaComboGralDto> listDatos= new ArrayList<LlenaComboGralDto>();
		try{
			sql+= "	SELECT	*from cat_grupo g join cat_rubro r on r.id_grupo=g.id_grupo and r.ingreso_egreso='E'";
		    System.out.print("llena grupo: " + sql);
		   
		    listDatos= jdbcTemplate.query(sql, new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setIdStr(rs.getString("id_grupo"));
					cons.setDescripcion(rs.getString("desc_grupo"));
					return cons;
				}});
		    System.out.println(listDatos);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Utilerias, C:ConsultasGenerales, M:llenarComboGral");
		}
		return listDatos;
	}
	
	public List<LlenaComboGralDto>obtenerRubro(LlenaComboGralDto dto){
		String sql="";
		List<LlenaComboGralDto> listDatos= new ArrayList<LlenaComboGralDto>();
		try{
			sql+= "	SELECT	";
			if(dto.getCampoUno()!=null && !dto.getCampoUno().trim().equals(""))
				sql+=""+dto.getCampoUno()+"	as ID";
			if(dto.getCampoDos()!=null && !dto.getCampoDos().trim().equals(""));
				sql+=",	"+dto.getCampoDos()+"	as DESCRIPCION";
		    sql+= "  FROM	";
		    if(dto.getTabla()!=null && !dto.getTabla().trim().equals(""))
		    	sql+=""+dto.getTabla();
		    if(dto.getCondicion()!=null && !dto.getCondicion().trim().equals(""))
		    	sql+="	WHERE	"+dto.getCondicion()+" AND ingreso_egreso='E'";
		    if(dto.getOrden()!=null && !dto.getOrden().trim().equals(""))
		    	sql+="	ORDER BY	"+dto.getOrden();
		 
		    System.out.print("llena combo rubro: " + sql);
		    //System.out.print("condicion: " + dto.getCondicion());
		    listDatos= jdbcTemplate.query(sql, new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setIdStr(rs.getString("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}});
		    System.out.println(listDatos);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Utilerias, C:ConsultasGenerales, M:llenarComboGral");
		}
		return listDatos;
	}
	
	
	public String consultarRutaUno(int indice) {
		try {
			sb = new StringBuffer();
			sb.append(" SELECT ruta ");
			sb.append("\n FROM ruta_origen");
			sb.append("\n WHERE secuencia = " + indice);
			List<String> valores = jdbcTemplate.query(sb.toString(),
					new RowMapper<String>() {
						public String mapRow(ResultSet rs, int idx)
								throws SQLException {
							String valor = rs.getString("ruta");
							return valor;
							
							
						}
					});
			return valores.isEmpty()?"":valores.get(0);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Utilerias, C:ConsultasGenerales, M:consultarRutaUno");
			e.printStackTrace();
			return "";
		}
	}
	
	public String consultarRutaDos(int indice) {
		try {
			sb = new StringBuffer();
			sb.append(" SELECT ruta ");
			sb.append("\n FROM ruta_destino");
			sb.append("\n WHERE secuencia = " + indice);
			List<String> valores = jdbcTemplate.query(sb.toString(),
					new RowMapper<String>() {
						public String mapRow(ResultSet rs, int idx)
								throws SQLException {
							String valor = rs.getString("ruta");
							return valor;
						}
					});
			return valores.isEmpty()?"":valores.get(0);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Utilerias, C:ConsultasGenerales, M:consultarRutaDos");
			e.printStackTrace();
			return "";
		}
	}

	public Map<String, Object> ejecutarPagador(final StoreParamsComunDto dto) {
	    return transactionTemplate.execute(new TransactionCallback<Map<String, Object>>() {
	    	@Override
	    	public Map<String, Object> doInTransaction(TransactionStatus transactionStatus) {
	    		Map<String, Object> resultado= new HashMap<String, Object>();
	    		try {
	    			com.webset.utils.tools.StoredProcedure storedProcedure = 
	    					new com.webset.utils.tools.StoredProcedure(jdbcTemplate, "sp_sql_Pagador");
	    			System.out.println("entra");
	    			storedProcedure.declareParameter("parametro", dto.getParametro(), com.webset.utils.tools.StoredProcedure.IN);
	    			storedProcedure.declareParameter("mensaje", Types.VARCHAR, 	com.webset.utils.tools.StoredProcedure.OUT);
	    			storedProcedure.declareParameter("result", Types.INTEGER, com.webset.utils.tools.StoredProcedure.OUT);
	    			
	    			resultado = storedProcedure.execute();				
	    			System.out.println("ejecutar Pagador "+resultado);		
	    			if((int) resultado.get("result") != 0) throw new Exception("error");
			    } catch (Exception e) {
			    	transactionStatus.setRollbackOnly();
			    }
			    return resultado;
	    	}
	    });
    }
	public Map<String, Object> crearInteres(int folio, double interes, int secuencia, String cveControl) {
		System.out.println("ejecutar crear interes");
	    		Map<String, Object> resultado= new HashMap<String, Object>();
	    		try {
	    			com.webset.utils.tools.StoredProcedure storedProcedure = 
	    					new com.webset.utils.tools.StoredProcedure(jdbcTemplate, "sp_crearMovimientoInteres");
	    			storedProcedure.declareParameter("AFfolio_det", folio, com.webset.utils.tools.StoredProcedure.IN);
	    			storedProcedure.declareParameter("interesNuevo", interes, com.webset.utils.tools.StoredProcedure.IN);
	    			storedProcedure.declareParameter("secuencia", secuencia, com.webset.utils.tools.StoredProcedure.IN);
	    			storedProcedure.declareParameter("cveControl",cveControl, com.webset.utils.tools.StoredProcedure.IN);
	    			storedProcedure.declareParameter("mensaje",Types.VARCHAR,com.webset.utils.tools.StoredProcedure.OUT);
	    			storedProcedure.declareParameter("result",Types.VARCHAR, com.webset.utils.tools.StoredProcedure.OUT);
	    			resultado = storedProcedure.execute();				
	    			System.out.println("ejecutar crearMovimientoInteres "+resultado);		
			    } catch (Exception e) {
			    	bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:BE, C:ConsultasGenerales, M: ejecutarPagoParcial");
			    }
			    return resultado;
    }
		
	}


