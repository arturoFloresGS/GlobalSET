/**
 * @author Jessica Arelly Cruz Cruz
 * @since 05/01/2011
 */
package com.webset.set.traspasos.dao;


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

import com.webset.set.bancaelectronica.dto.PersonasDto;
import com.webset.set.ingresos.dto.CatBancoDto;
import com.webset.set.ingresos.dto.CatCtaBancoDto;
import com.webset.set.ingresos.dto.CatGrupoRubroDto;
import com.webset.set.seguridad.dto.EmpresaDto;
import com.webset.set.traspasos.dto.BuscarSolicitudesTraspasosDto;
import com.webset.set.traspasos.dto.CatConceptoTraspDto;
import com.webset.set.traspasos.dto.GrupoDTO;
import com.webset.set.utilerias.dto.MovimientoDto;
//import com.webset.set.traspasos.dto.MovimientoDto;
import com.webset.set.traspasos.dto.ParametroTraspasosDto;
import com.webset.set.traspasos.dto.RubroDTO;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.StoreParamsComunDto;
import com.webset.set.utilerias.ConstantesSet;

@SuppressWarnings("unchecked")
public class TraspasosDao {
	private JdbcTemplate jdbcTemplate;
	private Bitacora bitacora = new Bitacora();
	private Funciones funciones;
	private ConsultasGenerales consultasGenerales;
	private static Logger logger = Logger.getLogger(TraspasosDao.class);
	
	GlobalSingleton globalSingleton;
	
	String gsDBM = ConstantesSet.gsDBM;
	
	/**
	 * concultar configuraSET
	 * */
	public String consultarConfiguraSet(int indice){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
		
	}
	
	/**
	 * cambiar la fecha a String en formato dd/mm/aaaa
	 * */
	public String obtenerFechaHoyS(){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		funciones = new Funciones();
		String fecha2 = "";
		try{
		Date fecha = consultasGenerales.obtenerFechaHoy();
		fecha2 = funciones.ponerFechaSola(fecha);
		}catch(Exception e){e.printStackTrace();}
		return fecha2;
	}
	
	public Date obtenerFechaHoy(){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.obtenerFechaHoy();
	}
	
	public int seleccionarFolioReal(String tipoFolio){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.seleccionarFolioReal(tipoFolio);
	} 
	
	public int actualizarFolioReal(String tipoFolio){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.actualizarFolioReal(tipoFolio);
	} 
	
	public int obtenerNumeroCuenta(int empresa){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.obtenerCuenta(empresa);
	}
	
	/**
	 * llamada al generador
	 * */
	public Map<String, Object> ejecutarGenerador(GeneradorDto dto){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.ejecutarGenerador(dto);
	}
	
	/**
	 * llamada al store ps_traspaso
	 * @param dto
	 * @param regreso
	 * @return
	 */
	public Map<String, Object> ejecutarTraspaso(StoreParamsComunDto dto, String regreso)
	{
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.ejecutarTraspasos(dto, regreso);
	}
	
	/**
	 * FunSQLComboConcepto()
	 * @return conceptos
	 * @throws Exception
	 */
	public List<CatConceptoTraspDto> llenarComboConcepto() throws Exception{
		
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append( " SELECT id_concepto as id, desc_concepto as describ ");
		sb.append( " FROM cat_concepto_trasp ");
	    sql=sb.toString();
		System.out.println(sql);
	    List <CatConceptoTraspDto> conceptos = null;
		
		try {
			conceptos = jdbcTemplate.query(sql, new RowMapper(){
			
			public CatConceptoTraspDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatConceptoTraspDto concepto = new CatConceptoTraspDto();
				concepto.setDescConcepto(rs.getString("describ"));
				concepto.setIdConcepto(rs.getInt("id"));
			return concepto;
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Traspasos, C:TraspasosDao, M:llenarComboConcepto");
			}
		return conceptos;
		
	}
	
	/**
	 * FunSQLComboEmpresaDe(GI_USUARIO)
	 * Metodo que consulta la tabla de empresa 
	 * @param empresa
	 * @throws Exception
	 */
	public List<EmpresaDto> obtenerEmpresa(int piUsuario) {
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append( " SELECT e.no_empresa as ID, e.nom_empresa as describ ");
		sb.append( " FROM empresa e ");
		sb.append( " WHERE e.no_empresa > 1 ");
		sb.append( " and e.no_empresa in ");
		sb.append( " ( ");
		sb.append( " SELECT no_empresa ");
		sb.append( " FROM usuario_empresa ");
		sb.append( " WHERE no_usuario = " + piUsuario);
		sb.append( " ) ");
		sb.append( " ORDER BY id ");
	    sql=sb.toString();
		List <EmpresaDto> empresas = null;
		System.out.println("<----------------obtenerEmpresa");
		System.out.println(sql);
		try{
			empresas = jdbcTemplate.query(sql, new RowMapper(){
			
			public EmpresaDto mapRow(ResultSet rs, int idx) throws SQLException {
				EmpresaDto empresa = new EmpresaDto();
				empresa.setNoEmpresa(rs.getInt("ID"));
				empresa.setNomEmpresa(rs.getString("describ"));
			return empresa;
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Traspasos, C:TraspasosDao, M:consultarEmpresa");
			}
		return empresas;
	}
	
	/**
	 * gobjSQL.FunSQLCombo386(Val(txtnoempresa2), bandera), Mat_bancoA
	 * Metodo que consulta las tablas cat_banco y cat_cta_banco
	 * @param emp
	 * @throws Exception
	 */
	public List<CatBancoDto> obtenerBanco(int emp, boolean pbInversion) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append( " SELECT distinct b.id_banco as ID, " );
		sb.append( " b.desc_banco as Descrip " );
		sb.append( " FROM cat_banco b, cat_cta_banco c " );
		sb.append( " WHERE c.id_banco = b.id_banco " );
		sb.append( " AND c.no_empresa = " + emp );
	    
	    if (pbInversion == true) 
	    	sb.append( "   AND c.tipo_chequera not in ('U') " );
	    else
	    	sb.append( "   AND c.tipo_chequera not in (" + consultarConfiguraSet(202) + ") " );
	   
		sql=sb.toString();
		//logger.info("banco de cta"+ sql);
		
		System.out.println("banco de cta"+ sql);
		
		List <CatBancoDto> bancos = null;
		try{
			bancos = jdbcTemplate.query(sql, new RowMapper(){
			public CatBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatBancoDto banco = new CatBancoDto();
				banco.setIdBanco(rs.getInt("ID"));
				banco.setDescBanco(rs.getString("Descrip"));
			return banco;
			}});
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Traspasos, C:TraspasosDao, M:consultarBanco");
			}
		return bancos;
	}
	
	/**
	 * FunSQLCombo380
	 * consulta las chequeras del banco y empresa seleccionados
	 * @param ban
	 * @param emp
	 * @param pbInversion
	 * @return banco
	 * @throws Exception
	 */
	public List<CatCtaBancoDto> obtenerChequera(int emp, int ban, boolean pbInversion) {
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append( "SELECT id_chequera as ID, ");
		sb.append( " id_chequera as Descrip ");
		sb.append( " FROM cat_cta_banco ");
		sb.append( " WHERE id_banco = " + ban);
		sb.append( " AND no_empresa =  " + emp);
	    
	    if(pbInversion == true)
	    	sb.append( "   AND tipo_chequera NOT IN ('U') " );
	    else							
	    	sb.append( "   AND tipo_chequera NOT IN (" + consultarConfiguraSet(202) + ") ");
	    
		sql=sb.toString();
		System.out.println(sql);
		List <CatCtaBancoDto> bancos = null;
		try{
			bancos = jdbcTemplate.query(sql, new RowMapper(){
			
			public CatCtaBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatCtaBancoDto banco = new CatCtaBancoDto();
				banco.setIdChequera(rs.getString("ID"));
			return banco;
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Traspasos, C:TraspasosDao, M:consultarChequera");
			}
		return bancos;
	}
	
	/**
	 * FunSQLSelect901
	 * @param emp, ban, chequera, bAnterior, fecha
	 * @return banco
	 */
	public double consultarSaldoFinal(int emp, int ban, String chequera, boolean bAnterior, String fecha) {
		String sql = "";
		StringBuffer sb = new StringBuffer();
		funciones = new Funciones();
		sb.append( " SELECT saldo_final ");
		    
		    if( bAnterior ){
		    	sb.append( " FROM hist_cat_cta_banco " );
	    		sb.append( " WHERE id_banco = " + ban );
		    
				if(!fecha.equals(""))
		        	sb.append( " AND fec_valor = '" + funciones.ponerFechaSola(fecha) + "'" );
		    }   
		    else{
		    	sb.append( " FROM cat_cta_banco " );
    			sb.append( " WHERE id_banco = " + ban );
		    }
		    
		    sb.append( "   AND id_chequera = '" + chequera + "'" );
    		sb.append( "   AND no_empresa = " + emp );
    		
		sql=sb.toString();
		System.out.println(sql);
		//logger.info("consultar saldo final " + sql);
		List <CatCtaBancoDto> bancos = null;
		try{
			bancos = jdbcTemplate.query(sql, new RowMapper(){
			
			public CatCtaBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatCtaBancoDto saldo = new CatCtaBancoDto();
				saldo.setSaldoFinal(rs.getDouble("saldo_final"));
			return saldo;
			}});
			
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Traspasos, C:TraspasosDao, M:consultarSaldoFinal");
			} 
		return bancos.isEmpty()? 0 : bancos.get(0).getSaldoFinal();
	}
	
	/**
	 * FunSQLSelect898
	 * @param ban
	 * @param chequera
	 * @param pbInversion
	 * @return divisas
	 * @throws Exception
	 */
	
	public List<Map<String,String>> verificarDivisa(int ban, String chequera, boolean pbInversion){
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append( " SELECT id_divisa ");
	    
	    if(pbInversion)
	    	sb.append( ", tipo_chequera as tipo " );
	    	    
	    sb.append( " FROM cat_cta_banco " );
		sb.append( " WHERE id_banco = " + ban );
		if(chequera != null && !chequera.equals(""))
			sb.append( " AND id_chequera = '" + chequera + "'" );
	    sql=sb.toString();
	    System.out.println(sql);
		List<Map<String,String>> divisas=null;
		try{
			divisas = jdbcTemplate.query(sql, new RowMapper(){
				public Map<String,String> mapRow(ResultSet rs, int idx){
					HashMap divisa = new HashMap();
					try {
						divisa.put("divisa", rs.getString("id_divisa"));
						divisa.put("tipo", rs.getString("tipo"));
					} catch (SQLException e) {}
				return divisa;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosDao, M:verificarDivisa");
		} 
		return divisas;
	}
	
	/**
	 * cheques por entregar
	 * FunSQLSelect900
	 * @param emp
	 * @param divisa
	 * @param chequera
	 * @param ban
	 * @return
	 * @throws Exception
	 */
	
	public double sumarImporte(int emp, String chequera, int ban, boolean pbInversion) {
		List<Map<String,String>> list = null;
		list = verificarDivisa(ban, chequera, pbInversion);
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append( " SELECT SUM(importe) AS suma ");
		sb.append( " FROM movimiento ");
		sb.append( " WHERE no_empresa = " + emp);
		sb.append( "   AND id_tipo_movto = 'E' ");
		sb.append( "   AND id_divisa = '" + (list.isEmpty()?"":list.get(0).get("divisa")) + "'");
		sb.append( "   AND id_chequera = '" + chequera + "'");
		sb.append( "   AND id_banco = " + ban);
		sb.append( "   AND ((id_estatus_mov IN ('I','R') AND b_entregado = 'N')");
		sb.append( "   OR (id_estatus_mov IN ('P','J','T')))");
	    
	    sql=sb.toString();
	    System.out.println(sql);
		List <MovimientoDto> importe = null;
		try{
			importe = jdbcTemplate.query(sql, new RowMapper(){
			
			public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
				MovimientoDto saldo = new MovimientoDto();
				saldo.setImporte(rs.getDouble("suma"));
			return saldo;
			}});
			
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Traspasos, C:TraspasosDao, M:sumarImporte");
			} 
		return importe.isEmpty()? 0 : importe.get(0).getImporte();
	}
	
	/**
	 * FunSQLSelectReferencia
	 * @param emp
	 * @param ban
	 * @param cheque
	 * @return
	 */
	
	public List<Map<String,String>> obtenerDatosReferencia(int emp, int ban, String cheque){
		StringBuffer sb = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
		String sql = "";
		sb.append( " SELECT no_linea, no_cuenta ");
		sb.append( " FROM ctas_contrato ");
		sb.append( " WHERE no_empresa = " + emp + "");
		sb.append( " AND id_chequera = '" + cheque + "'");
		sb.append( " AND id_banco = '" + ban + "'");
		sql=sb.toString();
		System.out.println(sql);
		List<Map<String,String>> datos = jdbcTemplate.query(sql, new RowMapper(){
			public Map<String,String> mapRow(ResultSet rs, int idx) throws SQLException{
				HashMap contrato = new HashMap();
				contrato.put("noLinea", rs.getInt("no_linea"));
				contrato.put("noCuenta", rs.getInt("no_cuenta"));
				return contrato;
			}
		});
			sql = "";
    
			if(datos.size()>0){
				for(int i = 0;i < datos.size();i ++){
		            sb2.append(  " SELECT COALESCE(c.persona_autoriza, '') AS referencia, p.razon_social AS institucion ");
		            sb2.append(  " FROM cuenta c, persona p ");
		            sb2.append(  " WHERE c.no_empresa = " + emp + "");
		            sb2.append(  " AND c.no_linea = '" + (datos.isEmpty()? 0 : datos.get(i).get("noLinea"))+ "'");
		            sb2.append(  " AND c.no_cuenta = '" + (datos.isEmpty()? 0 : datos.get(i).get("noCuenta")) + "'");
		            sb2.append(  " AND c.id_tipo_cuenta = 'C' ");
		            sb2.append(  " AND c.no_institucion = p.no_persona ");
		            sb2.append(  " AND p.id_tipo_persona = 'B' ");
				}
			}
			sql=sb2.toString();
			System.out.println(sql);
			List<Map<String,String>> datos2 = null;
			try{
				datos2 = jdbcTemplate.query(sql, new RowMapper(){
					public Map<String,String> mapRow(ResultSet rs, int idx) throws SQLException{
						HashMap resultado = new HashMap();
						resultado.put("referencia", rs.getString("referencia"));
						resultado.put("razonSocial", rs.getString("institucion"));
					return resultado;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosDao, M:consultarDatosReferencia");
		} 
		return datos2;
	}
	
	/**
	 * FunSQLCombo381
	 * @param emp2
	 * @param divisa
	 * @param pbInversion
	 * @param tipoChequera
	 * @return
	 * @throws Exception
	 */
	
	public List<CatBancoDto> obtenerBancoA(int emp2, String divisa, boolean pbInversion, String tipoChequera) {
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append( "SELECT distinct cat_banco.id_banco as ID, ");
		sb.append( "       cat_banco.desc_banco as Descrip ");
		sb.append( "  FROM cat_banco,cat_cta_banco");
		sb.append( " WHERE cat_cta_banco.id_banco = cat_banco.id_banco ");
		sb.append( "   AND cat_cta_banco.no_empresa = " + emp2);
		if(tipoChequera != null && !tipoChequera.equals(""))
    	{
		    if(pbInversion){
		    //ECOC 19/05/09 ---> Se modifica para que aparezcan las chequeras de inversion
		        if(tipoChequera.equals("M"))
		        	sb.append( "   AND cat_cta_banco.tipo_chequera NOT IN ('" + tipoChequera + "') ");
		        else 
		        	sb.append( "   AND cat_cta_banco.tipo_chequera IN ('I', 'M') ");
		    }
		    else															//fun_Valor_configura_set(202)
		    	sb.append( "   AND cat_cta_banco.tipo_chequera NOT IN (" + consultarConfiguraSet(202) + ") ");
    	}
    	sb.append( "   AND id_divisa='" + divisa + "'");
    	sql=sb.toString();
    	//logger.info("bancos acta"+sql);
    	
    	System.out.println("bancos acta"+sql);
		List <CatBancoDto> bancos = null;
		try{
			bancos = jdbcTemplate.query(sql, new RowMapper(){
			public CatBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatBancoDto banco = new CatBancoDto();
				banco.setIdBanco(rs.getInt("ID"));
				banco.setDescBanco(rs.getString("Descrip"));
			return banco;
			}});
		
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosDao, M:consultarBancoA");
		}
		return bancos;
	}
	
	
	/**
	 * FunSQLCombo379
	 * @param emp
	 * @param ban
	 * @param pbInversion
	 * @param cheqExcluye
	 * @return chequera
	 * @throws Exception
	 */
	
	public List<CatCtaBancoDto> obtenerChequeraA(int emp, int ban, boolean pbInversion, String cheqExcluye) {
		List<Map<String,String>> list = null;
		list = verificarDivisa(ban, cheqExcluye, pbInversion);
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append( " SELECT id_chequera as ID, ");
		sb.append( " id_chequera as Descrip ");
		sb.append( " FROM cat_cta_banco");
		sb.append( " WHERE id_banco = " + ban);
		sb.append( " AND no_empresa =  " + emp);
//	    sb.append( " AND tipo_chequera <>'U' ");
	    if(pbInversion)
	    {
//	    	ECOC 19/05/09  ---> Se modifica para poder seleccionar las chequeras de inversion
	        if(list.get(0).get("tipo").equals("M"))
	            sb.append( " AND tipo_chequera NOT IN ('" + list.get(0).get("tipo") + "') ");
	        else
	        	sb.append( " AND tipo_chequera IN ('I', 'M') ");
	    }
	    else{
//	        sb.append( "   AND tipo_chequera not in('U') ");
	    	sb.append( " AND tipo_chequera NOT IN (" + consultarConfiguraSet(202) + ") ");
	    }
	    sb.append( " AND id_divisa = '"+ list.get(0).get("divisa") + "'");
//	    'ECOC 19/05/09
//	    'No se incluye la chequera seleccionada en cmbChequeraDE
	    if(cheqExcluye != null && !cheqExcluye.equals(""))
	        sb.append( " AND id_chequera <> '" + cheqExcluye + "'");
	    sql=sb.toString();
	    System.out.println(sql);
		List <CatCtaBancoDto> chequera = null;
		try{
			chequera = jdbcTemplate.query(sql, new RowMapper(){
			
			public CatCtaBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatCtaBancoDto cheque = new CatCtaBancoDto();
				cheque.setIdChequera(rs.getString("ID"));
				
			return cheque;
			}});
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Traspasos, C:TraspasosDao, M:consultarChequeraA");
			}
		return chequera;
	}
	
	/**
	 * FunSQLCombo384
	 * @param usuario
	 * @return empresa
	 * @throws Exception
	 */
	
	public List<PersonasDto> llenarComboInterEmpresas(int usuario) {
		String sql = "";
		StringBuffer sb = new StringBuffer();
			sb.append( " SELECT persona.no_empresa as ID, ");
		    sb.append( " razon_social as Descrip ");
		    sb.append( " FROM persona, empresa");
		    sb.append( " WHERE (id_tipo_persona = 'E' or id_tipo_persona = 'I') ");
		    sb.append( " AND empresa.no_empresa = persona.no_empresa ");
		    sb.append( " AND empresa.b_control_bancario = 'S'");
		    sb.append( " AND empresa.no_empresa in (Select no_empresa ");
		    sb.append( " From usuario_empresa ");
		    sb.append( " Where no_usuario = " + usuario + ")");
		    sql=sb.toString();
		    //System.out.println("<-------------------llenarComboInterEmpresas");
		    System.out.println(sql);
		   // System.out.println("<------------------------------------------->");
			List <PersonasDto> empresa = null;
			try{
				empresa = jdbcTemplate.query(sql, new RowMapper(){
				public PersonasDto mapRow(ResultSet rs, int idx) throws SQLException {
					PersonasDto persona = new PersonasDto();
					persona.setNoEmpresa(rs.getInt("ID"));
					persona.setRazonSocial(rs.getString("Descrip"));
				return persona;
				}});
			
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Traspasos, C:TraspasosDao, M:llenarComboInterEmpresas");
			}
			return empresa;
		}
	
	
	/**
	 * FunSQLCombo383(ByVal pvvValor1 As Variant)
	 * @param empresa
	 * @return bancos
	 * @throws Exception
	 */
	
	public List<CatBancoDto> llenarComboBancoInter(int empresa){
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT distinct b.id_banco as ID, ");
	    sb.append("       b.desc_banco as Descrip ");
	    sb.append("  FROM cat_banco b,cat_cta_banco c");
	    sb.append(" WHERE c.id_banco = b.id_banco");
	    sb.append("   AND c.no_empresa =" +empresa);
	    sb.append("   AND c.tipo_chequera not in(" + consultarConfiguraSet(202) + ") ");
	    sql=sb.toString();
	    
	    System.out.println(sql);
	    
		List <CatBancoDto> bancos = null;
		try{
			bancos = jdbcTemplate.query(sql, new RowMapper(){
			public CatBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatBancoDto banco = new CatBancoDto();
				banco.setIdBanco(rs.getInt("ID"));
				banco.setDescBanco(rs.getString("Descrip"));
			return banco;
			}});
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Traspasos, C:TraspasosDao, M:llenarComboBancoInter");
			}
		return bancos;
	}
	
	/**
	 * FunSQLCombo385
	 * @param empresa
	 * @param usuario
	 * @return empresas
	 */
	
	public List<PersonasDto> llenarComboInterEmpresas2(int empresa, int usuario) {
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append( " SELECT persona.no_empresa as ID, ");
	    sb.append( " razon_social as Descrip ");
	    sb.append( " FROM persona, empresa");
	    sb.append( " WHERE (id_tipo_persona = 'E' or id_tipo_persona = 'I') ");
	    sb.append( " AND empresa.no_empresa=persona.no_empresa ");
	    sb.append( " AND empresa.b_control_bancario = 'S'");
	    sb.append( " AND empresa.no_empresa <> " + empresa);

	    if (usuario > 0)
	    {
	        sb.append( " and empresa.no_empresa in ");
	        sb.append( " (SELECT no_empresa ");
	        sb.append( " FROM usuario_empresa ");
	        sb.append( " WHERE no_usuario = " + usuario +")");
	    }
	        sql=sb.toString();
	        System.out.println("<-------------------llenarComboInterEmpresas2"+sql.toString());
			List <PersonasDto> empresas = null;
			try{
				empresas = jdbcTemplate.query(sql, new RowMapper(){
				public PersonasDto mapRow(ResultSet rs, int idx) throws SQLException {
					PersonasDto persona = new PersonasDto();
					persona.setNoEmpresa(rs.getInt("ID"));
					persona.setRazonSocial(rs.getString("Descrip"));
				return persona;
				}});
			
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Traspasos, C:TraspasosDao, M:llenarComboInterEmpresas2");
			}
			return empresas;
		}
	
	
	/**
	 * FunSQLCombo382
	 * @param empresa2
	 * @param pbInversion
	 * @param bancoDe
	 * @param chequeraDe
	 * @return lista bancos
	 */
	
	public List<CatBancoDto> llenarComboBancoInter2(int empresa2, boolean pbInversion, int bancoDe, String chequeraDe) {
		List<Map<String,String>> list = null;
		list = verificarDivisa(bancoDe, chequeraDe, pbInversion);
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append( " SELECT distinct cat_banco.id_banco as ID, ");
	    sb.append( " cat_banco.desc_banco as Descrip ");
	    sb.append( " FROM cat_banco,cat_cta_banco");
	    sb.append( " WHERE cat_cta_banco.id_banco = cat_banco.id_banco");
	    sb.append( " AND cat_cta_banco.no_empresa = " + empresa2);
	    sb.append( " AND cat_cta_banco.id_divisa='" + list.get(0).get("divisa") + "'");
	    
	    if (pbInversion)
	        sb.append( "   AND cat_cta_banco.tipo_chequera not in('" + list.get(0).get("tipo") + "') ");
	    else
	        sb.append( "   AND cat_cta_banco.tipo_chequera not in(" + consultarConfiguraSet(202) + ") ");
	    
	    sql=sb.toString();
	    
	    System.out.println("query para los banos de los traspasos: " + sql.toString());
	    
		List <CatBancoDto> bancos = null;
		try{
			bancos = jdbcTemplate.query(sql, new RowMapper(){
			public CatBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatBancoDto banco = new CatBancoDto();
				banco.setIdBanco(rs.getInt("ID"));
				banco.setDescBanco(rs.getString("Descrip"));
			return banco;
			}});
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Traspasos, C:TraspasosDao, M:llenarComboBancoInter2");
			}
		return bancos;
	}
	
	
	/**
	 * FunSQLSelect963
	 * @param empresa
	 * @return campo b_concentradora
	 */
	
	public String consultarConcentradora(int empresa) {
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append( "SELECT b_concentradora");
		sb.append( "  FROM empresa");
		sb.append( " WHERE no_empresa = " + empresa);
	
	    sql=sb.toString();
	    System.out.println(sql);
		List <EmpresaDto> result = null;
		try{
			result = jdbcTemplate.query(sql, new RowMapper(){
			
			public EmpresaDto mapRow(ResultSet rs, int idx) throws SQLException {
				EmpresaDto concentradora = new EmpresaDto();
				concentradora.setBConcentradora(rs.getString("b_concentradora"));
			return concentradora;
			}});
			
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Traspasos, C:TraspasosDao, M:consultarConcentradora");
			} 
		return result.isEmpty()? "" : result.get(0).getBConcentradora();
	}
	
	
	
	public List<Map<String,String>> obtenerClabe(int empresa, int banco, String chequera) {
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append( " SELECT id_clabe as CLABE, id_divisa ");
		sb.append( " FROM cat_cta_banco ");
		sb.append( " WHERE no_empresa =" + empresa);
		sb.append( " AND id_banco = " + banco);
		sb.append( " AND id_chequera =  '" + chequera + "'");
		sql=sb.toString();
		System.out.println(sql);
		List<Map<String,String>> clabe = null;
		try{
			clabe = jdbcTemplate.query(sql, new RowMapper(){
			
			public Map<String,String> mapRow(ResultSet rs, int idx)  {
				HashMap idClabe = new HashMap();
				try{
					idClabe.put("clabe", rs.getString("CLABE"));
					idClabe.put("divisa", rs.getString("id_divisa"));
				}catch(SQLException e){}
			return idClabe;
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Traspasos, C:TraspasosDao, M:obtenerClabe");
			}
		return clabe;
	}
	
	
	
	
	/**
	 * FunSQLInsert239
	 * metodo que captura en la tabla Parametro la solicitud de traspaso
	 * @param parametro
	 * @return 
	 */
	public int insertarSolicitudTraspaso(ParametroTraspasosDto parametro){
		int res = -1;
		StringBuffer sb = new StringBuffer();
		funciones = new Funciones();
		try{
			//Recibe los par�metros necesarios para ejecutar la sentencia de la forma frmTraspaso			
			sb.append( "INSERT INTO parametro " );
			sb.append( "(no_empresa, no_folio_param, aplica,secuencia, id_tipo_operacion, ");
			sb.append( "no_cuenta, id_estatus_mov, id_chequera, id_banco, importe, ");
			sb.append( "b_salvo_buen_cobro, fec_valor, fec_valor_original, ");
			sb.append( "id_estatus_reg, usuario_alta, fec_alta, id_divisa, ");
			sb.append( "id_forma_pago, importe_original, fec_operacion, id_banco_benef, ");
			sb.append( "id_chequera_benef, origen_mov, concepto, id_caja, no_folio_mov, ");
			sb.append( "folio_ref, grupo, beneficiario, no_cliente, referencia, id_grupo, id_rubro, no_docto)");
			
			sb.append( " VALUES");
			
			sb.append( " ( " + parametro.getNoEmpresa() + "," + parametro.getNoFolioParam() + ",1,1,");
			
			if(parametro.isBRegreso() == false)
				sb.append( parametro.getIdTipoOperacion() + "," + parametro.getNoCuenta() + ",'L', ");
			else
				sb.append( parametro.getIdTipoOperacion() + "," + parametro.getNoCuenta() + ",'A',");
			
			sb.append( "'" + parametro.getIdChequera() + "'," + parametro.getIdBanco() + "," + parametro.getImporte() + ", ");
			
			if(parametro.isBandera())
				sb.append( "'S', convert(datetime, '" + funciones.ponerFechaSola(parametro.getFecInversion()) + "', 103), convert(datetime, '" + funciones.ponerFechaSola(parametro.getFecInversion()) + "', 103), 'P',");
			else
				sb.append( "'S', convert(datetime, '" + funciones.ponerFechaSola(parametro.getFecValor()) + "', 103), convert(datetime, '" + funciones.ponerFechaSola(parametro.getFecValorOriginal()) + "', 103), 'P',");
			
			sb.append( parametro.getUsuarioAlta() + ", convert(datetime, '" + funciones.ponerFechaSola(parametro.getFecAlta()) + "', 103),'" + parametro.getIdDivisa() + "',3, ");
			sb.append( parametro.getImporteOriginal() + ", convert(datetime, '" + funciones.ponerFechaSola(parametro.getFecOperacion()) + "', 103)," + parametro.getIdBancoBenef() + ", ");
			
			if(parametro.isBandera())
				sb.append( "'" + parametro.getIdChequeraBenef() + "', 'IVC','" + parametro.getConcepto() + "'," + parametro.getIdCaja() + ", ");
			else
				sb.append( "'" + parametro.getIdChequeraBenef() + "', 'SET','" + parametro.getConcepto() + "'," + parametro.getIdCaja() + ", ");
			
			sb.append( "0,0," + parametro.getGrupo() + ",'" + parametro.getBeneficiario() + "' ,'" + parametro.getNoCliente() + "', '" + parametro.getPsReferencia() + "',");
			sb.append( parametro.getIdGrupoFlujoEgreso() + "," + parametro.getIdRubroFlujoEgreso() + "," + parametro.getNoDocto() + ")" );
			
			System.out.println(sb.toString());
			
			res = jdbcTemplate.update(sb.toString()
			);
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosDao, M:insertarSolicitudTraspaso");
			}
		return res;
	}
	
	/**
	 * 
	 * @param parametro
	 * @return
	 */
	
	public int insertarSolicitudTraspaso2(ParametroTraspasosDto parametro){
		int res = -1;
		StringBuffer sb = new StringBuffer();
		funciones = new Funciones();
		try{			
			sb.append( " INSERT INTO parametro ");
		    sb.append( " (no_empresa,no_folio_param,aplica,secuencia,id_tipo_operacion,");
		    sb.append( " no_cuenta,id_estatus_mov,id_chequera,id_banco,importe,");
		    sb.append( " b_salvo_buen_cobro,fec_valor,fec_valor_original,id_estatus_reg,");
		    sb.append( " usuario_alta,fec_alta,id_divisa,id_forma_pago,importe_original,");
		    sb.append( " fec_operacion,id_banco_benef, id_chequera_benef,origen_mov,");
		    sb.append( " concepto,id_caja,no_folio_mov,folio_ref,grupo,beneficiario,");
		    sb.append( " no_cliente, referencia, id_grupo, id_rubro)");
		
		    sb.append( " VALUES");
			
		    sb.append( "(" + parametro.getNoCliente() + "," + parametro.getNoFolioParam() + ",1,2,");
		    	
		    if(parametro.isBRegreso() == false)
				sb.append( parametro.getIdTipoOperacion() + "," + parametro.getNoCuenta() + ",'L', ");
			else
				sb.append( parametro.getIdTipoOperacion() + "," + parametro.getNoCuenta() + ",'A',");
		    	
		    sb.append( "'" + parametro.getIdChequeraBenef() + "'," + parametro.getIdBancoBenef() + "," + parametro.getImporte() + ",'S', ");
		    	
		    if(parametro.isBandera())
		        sb.append( " convert(datetime, '" + funciones.ponerFechaSola(parametro.getFecInversion()) + "', 103), convert(datetime, '" + funciones.ponerFechaSola(parametro.getFecInversion()) + "', 103),'P'," + parametro.getUsuarioAlta());
		    else
		        sb.append( " convert(datetime, '" + funciones.ponerFechaSola(parametro.getFecValor()) + "', 103), convert(datetime, '" + funciones.ponerFechaSola(parametro.getFecValorOriginal()) + "', 103),'P'," + parametro.getUsuarioAlta());
		    	
		    sb.append( ", convert(datetime, '" + funciones.ponerFechaSola(parametro.getFecAlta()) + "', 103),'" + parametro.getIdDivisa() + "',3,");
		    sb.append( parametro.getImporteOriginal() + ", convert(datetime, '" + funciones.ponerFechaSola(parametro.getFecOperacion()) + "', 103)," + parametro.getIdBanco());
		    	
		    if(parametro.isBandera())
		        sb.append( ", '" + parametro.getIdChequera() + "', 'IVC','" + parametro.getConcepto2() + "',");
		    else
		        sb.append( ", '" + parametro.getIdChequera() + "', 'SET','" + parametro.getConcepto2() + "',");
		    	
		    sb.append( parametro.getIdCaja() + ",1,1," + parametro.getGrupo() + " ,'" + parametro.getBeneficiarion() + "', ");
		    sb.append( "'" + parametro.getNoEmpresa() + "', '" + parametro.getPsReferencia() + "',");
		    sb.append( parametro.getIdGrupoFlujoIngreso() + "," + parametro.getIdRubroFlujoIngreso() + ")" );
		    	
		    System.out.println(sb.toString());
	    	res = jdbcTemplate.update(sb.toString()
			);
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosDao, M:insertarSolicitudTraspaso2");
			}
	return res;
	}
	
	/**de aqui en adelante se agregan las llamadas a las consultas referentes a ejecucion de traspasos*/
	
	/**
	 * llama al metodo de llenar combo general
	 * @param dto
	 * @return
	 */
	public List<LlenaComboGralDto>llenarComboGral(LlenaComboGralDto dto)
	{
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.llenarComboGral(dto);
	}
	
	
	public List<LlenaComboDivisaDto>llenarComboDivisa()
	{
		String sql="";
		List<LlenaComboDivisaDto> listDivisas= new ArrayList<LlenaComboDivisaDto>();
		try
		{
			sql+= " SELECT id_divisa as ID, desc_divisa as DESCRIPCION  ";
			sql+= " FROM cat_divisa ";
			sql+= " WHERE clasificacion = 'D'";
			sql+= " AND id_divisa <> 'CNT'";
			
			System.out.println(sql);
			/*
			 * If pbTrunca = True Then
			        sSQL = sSQL & "   AND id_divisa in('MN','DLS')"
			    End If
			 * */
    		listDivisas= jdbcTemplate.query(sql, new RowMapper(){
				public LlenaComboDivisaDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboDivisaDto cons = new LlenaComboDivisaDto();
					cons.setIdDivisa(rs.getString("ID"));
					cons.setDescDivisa(rs.getString("DESCRIPCION"));
					return cons;
				}});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosDao, M:llenarComboDivisa");
		}
		return listDivisas;
	    
	}
	
	/**
	 * FunSQLSolicitudesTraspaso
	 * consulta que obtiene las solicitudes de traspaso pendientes
	 * @param datos
	 * @return List<BuscarSolicitudesTraspasosDto>
	 */
	
	public List<BuscarSolicitudesTraspasosDto>consultarSolicitudesTraspasos(BuscarSolicitudesTraspasosDto datos){
		List<BuscarSolicitudesTraspasosDto> resultado = null;
		
		StringBuffer sb = new StringBuffer();
		String sql = "";
		funciones = new Funciones();
		
		try {
	/*
	 * Public Function FunSQLSolicitudesTraspaso(ByVal pvsDescOperacion As String, ByVal pviTipoOperacion As Integer, _
                                          ByVal pvsValor As String, ByVal pvbMismoBanco As Boolean, _
                                          ByVal pvbEmpresaActual As Boolean, ByVal psiEmpresa As Integer, _
                                          ByVal pvsDivisa As String, ByVal pbSPEUA As Boolean, _
                                          ByVal pvsUsuario As String, ByVal pdfechaini As String, ByVal pdfechafin As String, _
                                          ByVal pbInternacional As Boolean, Optional piBanco As Integer, _
                                          Optional pbInversion As Boolean, Optional optInter As Boolean) As ADODB.Recordset*/
    
    //Recibe los par�metros necesarios para ejecutar la sentencia de la forma frmTransInv
	boolean pbH2HBital = false;
	
    if(consultarConfiguraSet(232).equals("SI") && datos.getIdBanco() == 21)
    	pbH2HBital = true;
    //logger.info("conf set:"+consultarConfiguraSet(232));
    
            sb.append("SELECT distinct cb2.id_banco_city,m.observacion,cb.b_layout_comerica,m.id_tipo_operacion,");
            sb.append("\n CASE WHEN m.id_tipo_operacion = 3701 AND m.cve_control <> '' THEN (SELECT fecha_propuesta FROM  seleccion_automatica_grupo sa WHERE sa.cve_control = m.cve_control) ELSE convert(char(10),'01/01/1990', 103) END as fecha_propuesta ," );
            sb.append("\n CASE WHEN m.id_tipo_operacion = 3701 AND m.cve_control <> '' THEN (SELECT habilitado FROM  seleccion_automatica_grupo sa WHERE sa.cve_control = m.cve_control) ELSE 'S' END as habilitado,");
            sb.append("\n       c2.desc_plaza as plaza_benef,c.desc_plaza as plaza,");
            sb.append("\n       m.no_empresa , ");
            sb.append("\n       m.cve_control , ");
            sb.append("\n       m.lote_entrada,");
            sb.append("\n       m.no_empresa as no_empresaCXP,");
            sb.append("\n       m.origen_mov,");
            sb.append("\n       m.fec_valor,");
            sb.append("\n       m.fec_valor_original,");
            
            sb.append("\n       oi.id_banco as id_banco_inv,");
            sb.append("\n       oi.id_chequera as id_chequera_inv,");
            sb.append("\n       oi.fec_venc,");
            sb.append("\n       oi.importe_traspaso,");
            
            sb.append("\n       oi.traspaso_ejecutado,");
            sb.append("\n       oi.plazo,");
            sb.append("\n       oi.no_orden,");
            sb.append("\n       oi.no_cuenta,");
            sb.append("\n       oi.tasa,");
            
            sb.append("\n       oi.isr,");
            sb.append("\n       oi.interes,");
            sb.append("\n       m.no_docto ,");
            sb.append("\n       m.fec_operacion ,");
            sb.append("\n       o.desc_cve_operacion,");
            
            sb.append("\n       oi.importe as importe_inv, ");
            sb.append("\n       m.importe,m.importe_original,");
            sb.append("\n       (case when m.referencia = '' or m.referencia is null then '0070005' else m.referencia end) as referencia,");
            sb.append("\n       m.id_divisa ,");
            sb.append("\n       m.no_folio_mov,");
            
            sb.append("\n       m.id_chequera_benef ,");
            sb.append("\n       m.id_banco,");
            sb.append("\n       m.id_banco_benef,");
            sb.append("\n       m.id_chequera,");
            sb.append("\n       cb2.desc_banco as desc_banco_benef,  ");
            sb.append("\n       cb.desc_banco as desc_banco,  ");
            sb.append("\n       no_cliente as no_empresa_benef,");
            sb.append("\n       m.beneficiario,");
            sb.append("\n       m.concepto,");
            sb.append("\n       m.oper_may_esp,");
            sb.append("\n       m.no_folio_det,m.invoice_type,");
            sb.append("\n       e2.nom_empresa, ");
            sb.append("\n       c2.desc_sucursal as sucursal_destino,");
            sb.append("\n       c.desc_sucursal as sucursal_origen, cb2.inst_finan as inst_finan_benef, ");
            sb.append("\n       cb2.desc_banco_inbursa, c2.id_clabe as clabe_benef, ");
            sb.append("\n       c2.aba as aba_benef, ");
            sb.append("\n       c2.swift_code as swift_benef ");
            sb.append("\n       ,cb.valida_CLABE,e2.id_contrato_mass ");
            sb.append("\n       ,c2.tipo_chequera as  tipo_chequera_benef ");
            if(pbH2HBital)
                sb.append("\n,u.desc_usuario_bital,s.desc_servicio_bital ");
            
            sb.append("\n       ,pp.rfc ");
            sb.append("\n       ,pp2.rfc as rfc_benef" );
            
            
       
       
    	   if(datos.getIdBanco() == 12)
    	   {
            // Solo si es bancomer vamos por estos datos los demas no los ocupan
                sb.append("\n       , (SELECT contacto FROM medios_contacto mc where ");
                sb.append("\n         pp2.no_empresa = mc.no_persona And pp2.no_persona = mc.no_persona  ");
                sb.append("\n         and pp2.id_tipo_persona = mc.id_tipo_persona and mc.id_tipo_medio = 'TOF1' ");
                sb.append("\n         and mc.no_medio = 1) as complemento,  (select pa.desc_pais from cat_pais pa where pa.id_pais = 'MX')  as especiales");
    	   }
            else
                sb.append("\n         , '' as complemento,  ''  as especiales");
            
            sb.append("\n       ,d.ciudad + ', ' + d.id_estado as direccion_benef , (select pa.desc_pais from cat_pais pa where pa.id_pais = 'MX') as pais_benef ");
            sb.append("\n  FROM movimiento m LEFT JOIN persona pp2 ON(m.no_cliente = pp2.no_persona and m.no_cliente = pp2.no_empresa AND pp2.id_tipo_persona = 'E')");
            sb.append("\n       LEFT JOIN orden_inversion oi ON(m.no_docto = oi.no_orden and m.no_empresa = oi.no_empresa)");
            sb.append("\n       LEFT JOIN cat_cta_banco c2 ON(m.id_banco_benef = c2.id_banco and m.id_chequera_benef = c2.id_chequera) ");
            if(pbH2HBital)
            {
                    sb.append("\n INNER JOIN (empresa e2  ");
                    sb.append("\n     LEFT JOIN cat_usuario_bital u ON(e2.id_usuario_bital = u.id_usuario_bital)");
                    sb.append("\n     LEFT JOIN cat_servicio_bital s ON (e2.id_servicio_bital = s.id_servicio_bital) ");
                    sb.append("\n )   ON(m.no_empresa = e2.no_empresa) ");
            }
            else
                sb.append("\n    INNER JOIN empresa e2  ON(m.no_empresa = e2.no_empresa)");
            
            sb.append("\n    INNER JOIN persona pp ON(m.no_empresa = pp.no_empresa and m.no_empresa = pp.no_persona and pp.id_tipo_persona = 'E')");
            sb.append("\n    LEFT JOIN cat_cta_banco c ON(m.id_banco_benef = c.id_banco and m.id_chequera_benef = c.id_chequera AND m.no_empresa = c.no_empresa) ");
            sb.append("\n    LEFT JOIN cat_banco cb2 ON(m.id_banco_benef = cb2.id_banco) ");
            sb.append("\n    INNER JOIN empresa e3  ON(m.no_empresa = e3.no_empresa) ");
            sb.append("\n    INNER JOIN persona pp3 ON(m.no_empresa = pp3.no_empresa and m.no_empresa = pp3.no_persona and pp3.id_tipo_persona = 'E'), ");
            sb.append("\n    cat_cve_operacion o, cat_banco cb, direccion d ");
            sb.append("\n WHERE ");
            sb.append("\n   m.id_estatus_mov = 'L' ");
           // sb.append("\n   m.no_empresa = e2.no_empresa ");
           // sb.append("\n   AND  m.id_banco_benef = cb2.id_banco ");
            
            
            
            
            
            if(datos.isOpcionInversion())
                sb.append("\n   AND (m.origen_mov in ('IVC') or m.origen_mov is null)");
            
            sb.append("\n   AND m.id_cve_operacion = o.id_cve_operacion");
            //sb.append("\n   AND m.no_empresa = c.no_empresa");

          //  sb.append("\n   AND m.id_banco = c.id_banco");
          //  sb.append("\n   AND m.id_chequera = c.id_chequera");
            sb.append("\n   AND m.id_tipo_movto = 'E'");
            sb.append("\n   AND m.id_banco = cb.id_banco");
            sb.append("\n   AND m.id_tipo_operacion between 3800 and 3899");
            

            sb.append("\n   AND m.id_divisa = '" + datos.getIdDivisa() + "'");
            sb.append("\n   AND m.fec_valor between convert(datetime,'" + funciones.ponerFechaSola(datos.getFechaIni()) + "',103) and convert(datetime,'" + funciones.ponerFechaSola(datos.getFechaFin()) + "',103)");

            if(datos.isOpcionSpeua())
            {
                sb.append("\n AND m.id_banco <> m.id_banco_benef ");
            }
            else if(datos.isOpcionMismoBanco())
               sb.append("\n AND m.id_banco = m.id_banco_benef");

            else if(datos.isOpcionInternacional())
            {
               sb.append("\n AND m.id_banco <> m.id_banco_benef");
            }
            
            else if(datos.isOpcionInterbancaria())
            {
                   sb.append("\n AND m.id_banco <> m.id_banco_benef");
            }
            
            if(datos.getIdBanco() != 0)
                sb.append("\n AND cb.id_banco = " + datos.getIdBanco() + " ");

            //if(datos.isOpcionEmpresaActual())
            if(datos.getNoEmpresa() > 0)
                sb.append("\n AND m.no_empresa =  " + datos.getNoEmpresa());

            if(datos.getIdTipoOperacion() > 0)
            {
            	if(datos.getIdTipoOperacion() == 3801 && datos.getDescCveOperacion().equals("TRASPASO INTEREMPRESAS"))
                    sb.append("\n AND m.origen_mov = 'SET'");
                else if(datos.getIdTipoOperacion() == 3801 && datos.getDescCveOperacion().equals("PAGO INTEREMPRESAS"))
                    sb.append("\n AND (m.origen_mov is null or m.origen_mov = '')");
               
                sb.append("\n AND m.id_tipo_operacion = " + datos.getIdTipoOperacion());
            }
            
            
            sb.append("\n   AND pp.no_empresa = d.no_persona");
            sb.append("\n   and pp.no_persona = d.no_persona");
            sb.append("\n   and pp.id_tipo_persona = d.id_tipo_persona");
            sb.append("\n   and d.id_tipo_direccion = 'OFNA'");
            sb.append("\n   and d.no_direccion = 1");
            
            sb.append("\n AND (cve_control is not null and cve_control not in (");
            
            sb.append("\n select cve_control from (");
            sb.append("\n select nivel_autorizacion, case when usuario_tres is not null then 3 when usuario_dos is not null then 2");
            sb.append("\n when usuario_uno is not null then 1 else 0 end as nivel_prop, cve_control");
            sb.append("\n from seleccion_automatica_grupo s, cat_grupo_flujo c, grupo_empresa g");
            sb.append("\n Where");
            sb.append("\n s.id_grupo_flujo = c.id_grupo_flujo");
            sb.append("\n and c.nivel_autorizacion > 0");
            sb.append("\n and c.id_grupo_flujo = g.id_grupo_flujo");
            sb.append("\n and g.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " + datos.getIdUsuario() + ")");
            sb.append("\n ) a where nivel_autorizacion <> nivel_prop ))");
    
            sb.append("\n ORDER BY m.concepto" );
       
	 
			sql = sb.toString();			
			
			System.out.println("consulta de traspasos: " + sql);
			
	        //logger.info("solicitudes traspaso" + sql);
	        if(pbH2HBital)
	        {
		        resultado = (List<BuscarSolicitudesTraspasosDto>)jdbcTemplate.query(sql, new RowMapper(){
	        	public Object mapRow(ResultSet rs, int i)
				throws SQLException {
	        		BuscarSolicitudesTraspasosDto cons = new BuscarSolicitudesTraspasosDto();
	        		cons.setNoDocto(rs.getString("no_docto"));
	        		cons.setFecOperacion(rs.getDate("fec_operacion"));
	        		cons.setBeneficiario(rs.getString("beneficiario"));
	        		cons.setImporte(rs.getDouble("importe"));
	        		cons.setIdDivisa(rs.getString("id_divisa"));
	        		cons.setDescBancoBenef(rs.getString("desc_banco_benef"));
	        		cons.setIdChequeraBenef(rs.getString("id_chequera_benef"));
	        		cons.setClabeBenef(rs.getString("clabe_benef"));
	        		cons.setConcepto(rs.getString("concepto"));
	        		cons.setNoFolioDet(rs.getInt("no_folio_det"));
	        		cons.setImporteTraspaso(rs.getDouble("importe_traspaso"));
	        		cons.setNoFolioMov(rs.getInt("no_folio_mov"));
	        		cons.setIdBancoBenef(rs.getInt("id_banco_benef"));
	        		cons.setIdChequera(rs.getString("id_chequera"));
	        		cons.setIdBanco(rs.getInt("id_banco"));
//					cons.put("importeInv",rs.getString("27"));
	        		cons.setSucursalDestino(rs.getString("sucursal_destino"));
	        		cons.setFecValor(rs.getDate("fec_valor"));
	        		cons.setOrigenMov(rs.getString("origen_mov"));
	        		cons.setDescBanco(rs.getString("desc_banco"));
	        		cons.setNoEmpresa(rs.getInt("no_empresa"));
	        		cons.setLoteEntrada(rs.getInt("lote_entrada"));
	        		cons.setNoEmpresaBenef(rs.getInt("no_empresa_benef"));
	        		//cons.setPlaza(rs.getInt("plaza"));
	        		cons.setPlazaBenef(rs.getString("plaza_benef"));
	        		cons.setBLayoutComerica(rs.getString("b_layout_comerica"));
	        		cons.setInvoiceType(rs.getString("invoice_type"));
	        		cons.setNomEmpresa(rs.getString("nom_empresa"));
	        		cons.setNoEmpresaCXP(rs.getInt("no_empresaCXP"));
	        		cons.setInstFinan(rs.getString("inst_finan_benef"));
	        		cons.setSucursalOrigen(rs.getString("sucursal_origen"));
	        		cons.setObservacion(rs.getString("observacion"));
	        		cons.setImporteOriginal(rs.getDouble("importe_original"));
	        		cons.setDescBancoInbursa(rs.getString("desc_banco_inbursa"));
	        		cons.setAbaBenef(rs.getString("aba_benef"));
	        		cons.setSwiftBenef(rs.getString("swift_benef"));
	        		cons.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
	        		cons.setOperMayEsp(rs.getString("oper_may_esp"));
	        		cons.setValidaClabe(rs.getString("valida_clabe"));
	        		cons.setIdContratoMass(rs.getInt("id_contrato_mass"));
	        		cons.setTipoChequeraBenef(rs.getString("tipo_chequera_benef"));
	        		cons.setIdBancoCity(rs.getString("id_banco_city"));
	        		cons.setFecValorOriginal(rs.getDate("fec_valor_original"));
	        		cons.setDescUsuarioBital(rs.getString("desc_usuario_bital"));
	        		cons.setDescServicioBital(rs.getString("desc_servicio_bital"));
	        		cons.setRfc(rs.getString("rfc"));
	        		cons.setRfcBenef(rs.getString("rfc_benef"));
	        		cons.setFechaPropuesta(rs.getDate("fecha_propuesta"));
	        		cons.setHabilitado(rs.getString("habilitado"));
	        		cons.setCveControl(rs.getString("cve_control"));
	        		cons.setPaisBenef(rs.getString("pais_benef"));
	        		cons.setDireccionBenef(rs.getString("direccion_benef"));
	        		cons.setComplemento(rs.getString("complemento"));
	        		cons.setEspeciales(rs.getString("especiales"));
//	        		cons.setUsr1(rs.getString("usuario_uno"));
//	        		cons.setUsr2(rs.getString("usuario_dos"));
//	        		cons.setUsrUno(rs.getInt("usrUno"));
//	        		cons.setUsrDos(rs.getInt("usrDos"));
	        		return cons;
				}});
			}
	        else
	        {
	        	resultado = (List<BuscarSolicitudesTraspasosDto>)jdbcTemplate.query(sql, new RowMapper(){
	        	public Object mapRow(ResultSet rs, int i)
				throws SQLException {
	        		BuscarSolicitudesTraspasosDto cons = new BuscarSolicitudesTraspasosDto();
	        		cons.setNoDocto(rs.getString("no_docto"));
	        		cons.setFecOperacion(rs.getDate("fec_operacion"));
	        		cons.setBeneficiario(rs.getString("beneficiario"));
	        		cons.setImporte(rs.getDouble("importe"));
	        		cons.setIdDivisa(rs.getString("id_divisa"));
	        		cons.setDescBancoBenef(rs.getString("desc_banco_benef"));
	        		cons.setIdChequeraBenef(rs.getString("id_chequera_benef"));
	        		cons.setClabeBenef(rs.getString("clabe_benef"));
	        		cons.setConcepto(rs.getString("concepto"));
	        		cons.setNoFolioDet(rs.getInt("no_folio_det"));
	        		cons.setImporteTraspaso(rs.getDouble("importe_traspaso"));
	        		cons.setNoFolioMov(rs.getInt("no_folio_mov"));
	        		cons.setIdBancoBenef(rs.getInt("id_banco_benef"));
	        		cons.setIdChequera(rs.getString("id_chequera"));
	        		cons.setIdBanco(rs.getInt("id_banco"));
	        		cons.setSucursalDestino(rs.getString("sucursal_destino"));
	        		cons.setFecValor(rs.getDate("fec_valor"));
	        		cons.setOrigenMov(rs.getString("origen_mov"));
	        		cons.setDescBanco(rs.getString("desc_banco"));
	        		cons.setNoEmpresa(rs.getInt("no_empresa"));
	        		cons.setLoteEntrada(rs.getInt("lote_entrada"));
	        		cons.setNoEmpresaBenef(rs.getInt("no_empresa_benef"));
	        		//cons.setPlaza(rs.getInt("plaza"));
	        		cons.setPlazaBenef(rs.getString("plaza_benef"));
	        		cons.setBLayoutComerica(rs.getString("b_layout_comerica"));
	        		cons.setInvoiceType(rs.getString("invoice_type"));
	        		cons.setNomEmpresa(rs.getString("nom_empresa"));
	        		cons.setNoEmpresaCXP(rs.getInt("no_empresaCXP"));
	        		cons.setInstFinan(rs.getString("inst_finan_benef"));
	        		cons.setSucursalOrigen(rs.getString("sucursal_origen"));
	        		cons.setObservacion(rs.getString("observacion"));
	        		cons.setImporteOriginal(rs.getDouble("importe_original"));
	        		cons.setDescBancoInbursa(rs.getString("desc_banco_inbursa"));
	        		cons.setAbaBenef(rs.getString("aba_benef"));
	        		cons.setSwiftBenef(rs.getString("swift_benef"));
	        		cons.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
	        		cons.setOperMayEsp(rs.getString("oper_may_esp"));
	        		cons.setValidaClabe(rs.getString("valida_clabe"));
	        		cons.setIdContratoMass(rs.getInt("id_contrato_mass"));
	        		cons.setTipoChequeraBenef(rs.getString("tipo_chequera_benef"));
	        		cons.setIdBancoCity(rs.getString("id_banco_city"));
	        		cons.setFecValorOriginal(rs.getDate("fec_valor_original"));
	        		cons.setRfc(rs.getString("rfc"));
	        		cons.setRfcBenef(rs.getString("rfc_benef"));
	        		cons.setFechaPropuesta(rs.getDate("fecha_propuesta"));
	        		cons.setHabilitado(rs.getString("habilitado"));
	        		cons.setCveControl(rs.getString("cve_control"));
	        		cons.setPaisBenef(rs.getString("pais_benef"));
	        		cons.setDireccionBenef(rs.getString("direccion_benef"));
	        		cons.setComplemento(rs.getString("complemento"));
	        		cons.setEspeciales(rs.getString("especiales"));
//	        		cons.setNivelAutorizacion(rs.getInt("nivel_prop"));
//	        		cons.setUsr1(rs.getString("usuario_uno"));
//	        		cons.setUsr2(rs.getString("usuario_dos"));
//	        		cons.setUsrUno(rs.getInt("usrUno"));
//	        		cons.setUsrDos(rs.getInt("usrDos"));
	        		return cons;
				}});
	        }
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Trapasos, C:TraspasosDao, M:consultarSolicitudesTraspasos");
		}
		return resultado;
	}
	
	/**
	 * FunSQLComboCve
	 * consulta que llena el combo de claves
	 * @param datos
	 * @return List
	 */
	
	public List<BuscarSolicitudesTraspasosDto>llenarComboClave(BuscarSolicitudesTraspasosDto datos){
		List<BuscarSolicitudesTraspasosDto> resultado = null;
		String gsDBM = ConstantesSet.gsDBM;
		StringBuffer sb = new StringBuffer();
		String sql = "";
		funciones = new Funciones();
		try{
			/*
			Public Function FunSQLComboCve(ByVal pvsDescOperacion As String, ByVal pviTipoOperacion As Integer, _
                    ByVal pvsValor As String, ByVal pvbMismoBanco As Boolean, _
                    ByVal pvbEmpresaActual As Boolean, ByVal psiEmpresa As Integer, _
                    ByVal pvsDivisa As String, ByVal pbSPEUA As Boolean, _
                    ByVal pvsUsuario As String, ByVal pdfechaini As String, _
                    ByVal pdfechafin As String, Optional pbInversion As Boolean) As ADODB.Recordset*/
			
			boolean pbH2HBital = false;
		    if(consultarConfiguraSet(232).equals("SI") && datos.getIdBanco() == 21)
		    	pbH2HBital = true;
		    //logger.info("conf set:"+consultarConfiguraSet(232));
			
			sb.append("\n SELECT distinct m.cve_control ");
			sb.append("\n   FROM movimiento m LEFT JOIN persona p ON(m.no_cliente = p.no_persona and m.no_empresa = p.no_empresa)");
			sb.append("\n        LEFT JOIN orden_inversion oi ON(m.no_docto = oi.no_orden and m.no_empresa = oi.no_empresa)");
			sb.append("\n        LEFT JOIN cat_cta_banco c2 ON(m.id_banco_benef = c2.id_banco and m.id_chequera_benef = c2.id_chequera) ");
			if(pbH2HBital)
			{
				sb.append("\n  INNER JOIN (empresa e2  ");
				sb.append("\n      LEFT JOIN cat_usuario_bital u ON(e2.id_usuario_bital = u.id_usuario_bital)");
				sb.append("\n      LEFT JOIN cat_servicio_bital s ON (e2.id_servicio_bital = s.id_servicio_bital) ");
				sb.append("\n  )   ON(m.no_empresa = e2.no_empresa) ");
			}
			else
				sb.append("\n     INNER JOIN empresa e2  ON(m.no_empresa = e2.no_empresa)");
			
			sb.append("\n     INNER JOIN persona pp ON(m.no_empresa = pp.no_empresa and m.no_empresa = pp.no_persona and pp.id_tipo_persona = 'E')");
			sb.append("\n        ,cat_cve_operacion o, cat_cta_banco c, cat_banco cb,cat_banco cb2, empresa e");
			sb.append("\n  WHERE ");
			sb.append("\n    m.no_empresa = e2.no_empresa ");
			sb.append("\n    AND  m.id_banco_benef = cb2.id_banco ");
			
			if(gsDBM.equals("POSTGRESQL"))
				sb.append("\n    AND  int4(m.no_cliente) = e.no_empresa");
			else
				sb.append("\n    AND  m.no_cliente = e.no_empresa");
			
//			'para los propuestos
//			'se comenta temporalmente, hasta que se ejecute en pago de propuestas
//			'            sb.append("\n    AND (m.cve_control = '' or m.cve_control is null) ");
			
			sb.append("\n    AND m.id_estatus_mov = 'L' ");
			
			if(datos.isOpcionInversion())
				sb.append("\n    AND (origen_mov in ('IVC') or origen_mov is null)");
			else
				sb.append("\n    AND (origen_mov in ('PRE','SET','INV','','CRE','IVT') or origen_mov is null)");
			
			sb.append("\n    AND m.id_cve_operacion = o.id_cve_operacion");
			sb.append("\n    AND m.no_empresa = c.no_empresa");
			
			sb.append("\n    AND m.id_banco = c.id_banco");
			sb.append("\n    AND m.id_chequera = c.id_chequera");
			sb.append("\n    AND m.id_tipo_movto = 'E'");
			sb.append("\n    AND m.id_banco = cb.id_banco");
			sb.append("\n    AND m.id_tipo_operacion between 3800 and 3899");
			
			sb.append("\n    AND m.id_divisa = '" + datos.getIdDivisa() + "'");
			sb.append("\n    AND m.fec_valor between convert(datetime,'" + funciones.ponerFechaSola(datos.getFechaIni()) + "',103) and convert(datetime,'" + funciones.ponerFechaSola(datos.getFechaFin()) + "',103)");
			
			if(datos.isOpcionSpeua())
			{
				sb.append("\n  AND m.id_banco <> m.id_banco_benef ");
				sb.append("\n  AND m.importe >= 50000 ");
			}
			else if(datos.isOpcionMismoBanco())
			{
				sb.append("\n  AND m.id_banco = m.id_banco_benef");
			}
			else
			{
				sb.append("\n  AND m.id_banco <> m.id_banco_benef");
			}
			
			if(datos.getIdBanco() != 0)
			{
				sb.append("\n  AND cb.id_banco = '" + datos.getIdBanco() + "'");
			}
			
			if(datos.isOpcionEmpresaActual())
			{
				sb.append("\n  AND m.no_empresa =  " + datos.getNoEmpresa());
			}
			
			if(datos.getIdTipoOperacion() > 0)
			{
				if(datos.getIdTipoOperacion() == 3801 && datos.getDescCveOperacion().equals("TRASPASO INTEREMPRESAS"))
					sb.append("\n  AND m.origen_mov = 'SET'");
				else if (datos.getIdTipoOperacion() == 3801 && datos.getDescCveOperacion().equals("PAGO INTEREMPRESAS"))
					sb.append("\n  AND (m.origen_mov is null or m.origen_mov = '')");
				
				sb.append("\n  AND m.id_tipo_operacion = " + datos.getIdTipoOperacion());
			}
			
			sql = sb.toString();
			System.out.println(sql);
	        //logger.info("combo clave" + sql);
	        resultado = (List<BuscarSolicitudesTraspasosDto>)jdbcTemplate.query(sql, new RowMapper(){
	        	public Object mapRow(ResultSet rs, int i)
				throws SQLException {
	        		BuscarSolicitudesTraspasosDto cons = new BuscarSolicitudesTraspasosDto();
	        		cons.setCveControl(rs.getString("cve_control"));
	        		return cons;
				}});
			}
			catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Trapasos, C:TraspasosDao, M:llenarComboClave");
			}
			return resultado;
		}
	
	/**
	 * FunSQLSelect868
	 * @param banco
	 * @return
	 */
	
	public String consultarBancaElect(int banco) {
		String sql = "";
		StringBuffer sb = new StringBuffer();
		List <ComunDto> result = null;
		try{
			sb.append( "SELECT b_banca_elect  FROM cat_banco  WHERE id_banco = " + banco);
		    sql=sb.toString();
		    System.out.println(sql);
		    //logger.info("banca: " + sql);
			result = jdbcTemplate.query(sql, new RowMapper(){
			public ComunDto mapRow(ResultSet rs, int idx) throws SQLException {
				ComunDto banca = new ComunDto();
				banca.setBancaElect(rs.getString("b_banca_elect"));
				System.out.println("vamos a configura set..::"+banca);
				return banca;
		
			}});
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Traspasos, C:TraspasosDao, M:consultarBancaElect");
			} 
		return result.isEmpty()? "" : result.get(0).getBancaElect();
	}
	
	/**
	 * FunSQLSelect872
	 * @param empresa
	 * @param folioDet
	 * @return List
	 */
	
	public List<MovimientoDto>subSeleccionSolicitudes(int empresa, int folioDet,final boolean tipoEnvio){
		List<MovimientoDto> resultado = null;
		StringBuffer sb = new StringBuffer();
		String sql = "";
		try{
	System.out.println("Dentro del DAO metodo:subSeleccionSolicitudes..::angel");
//		Public Function FunSQLSelect872(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant) As ADODB.Recordset
//	    'Recibe los parametros necesarios para ejecutar la sentencia de la forma frmTransInv o ejecuta traspasos
		//MS: se debe revisar este query para agregar los pagos de inveersiones referenciadas 
		//MS: se debe agregar la empresa beneficiaria para conocerla aqui en traspasos
	    
	    sb.append(" SELECT m.cve_control,");
	    sb.append("\n        m.origen_mov,");
	    sb.append("\n        oper_may_esp,");
	    sb.append("\n        ind_iva,");
	    sb.append("\n        division,");
	    sb.append("\n        m.id_rubro,");
	    sb.append("\n        m.no_cuenta,");
	    sb.append("\n        m.no_empresa,");
	    sb.append("\n        m.valor_tasa,");
	    sb.append("\n        m.id_tipo_docto,");
	    sb.append("\n        m.no_docto,");
	    sb.append("\n        m.referencia,");
	    sb.append("\n        m.id_banco_benef,");
	    sb.append("\n        m.no_folio_mov,");
	    sb.append("\n        m.id_divisa,");
	    
	    sb.append("\n        m.id_chequera_benef,");
	    sb.append("\n        m.id_banco,");
	    sb.append("\n        b.inst_finan,");
	    sb.append("\n        m.id_chequera,");
	    sb.append("\n        m.id_tipo_operacion,");
	    
	    sb.append("\n        COALESCE(m.sucursal,0 )as sucursal,");
	    sb.append("\n        m.importe,");
	    sb.append("\n        m.no_cliente,");
	    sb.append("\n        m.beneficiario,");
	    sb.append("\n        m.lote_entrada,");
	    sb.append("\n        m.plaza,");
	    
	    sb.append("\n        m.no_folio_mov,");
	    sb.append("\n        m.no_folio_det,");
	    sb.append("\n        m.concepto,");
	    sb.append("\n        b.desc_banco,");
	    sb.append("\n        ccb.desc_sucursal,");
	    sb.append("\n        COALESCE(b.id_banco_city,0 )as id_banco_city, ");
	    
	    sb.append("\n        m.fec_valor, ccb.id_clabe, '0' as sucDestino,  Ccb.Aba, ccb.swift_code,pp2.rfc  as rfc_benef");
	    
	    sb.append("\n   FROM movimiento  m, cat_banco b, cat_cta_banco ccb, persona pp2  ");
	    
	    sb.append("\n  WHERE m.no_empresa = " + empresa);
	    sb.append("\n    AND m.no_folio_det = " + folioDet);
	    sb.append("\n    AND m.id_tipo_movto = 'E' and not folio_ref is null ");
	    sb.append("\n    AND m.id_banco_benef = b.id_banco And m.id_chequera_benef = ccb.id_chequera");
	    sb.append("\n    AND m.no_cliente = pp2.no_persona and m.no_cliente = pp2.no_empresa AND pp2.id_tipo_persona = 'E'");
	    //System.out.println(" consulta cambia mov"+sb);
	    sql = sb.toString();
	    System.out.println("subseleccionSolicitudes "+sql.toString());
        //logger.info("subseleccion: " + sql);
        resultado = (List<MovimientoDto>)jdbcTemplate.query(sql, new RowMapper(){
        	public Object mapRow(ResultSet rs, int i)
			throws SQLException {
        		MovimientoDto cons = new MovimientoDto();
        		cons.setCveControl(rs.getString("cve_control"));
        		cons.setOrigenMovAnt(rs.getString("origen_mov"));
        		cons.setOperMayEsp(rs.getString("oper_may_esp"));
        		cons.setIndIva(rs.getString("ind_iva"));
        		cons.setDivision(rs.getString("division"));
        		cons.setIdRubroInt(rs.getInt("id_rubro"));
        		cons.setNoCuenta(rs.getInt("no_cuenta"));
        		cons.setNoEmpresa(rs.getInt("no_empresa"));
        		cons.setValorTasa(rs.getDouble("valor_tasa"));
        		cons.setIdTipoDocto(rs.getInt("id_tipo_docto"));
        		cons.setNoDocto(rs.getString("no_docto"));
        		cons.setReferencia(rs.getString("referencia"));
        		cons.setIdBancoBenef(rs.getInt("id_banco_benef"));
        		cons.setNoFolioMov(rs.getInt("no_folio_mov"));
        		cons.setIdDivisa(rs.getString("id_divisa"));
        		cons.setIdChequeraBenef(rs.getString("id_chequera_benef"));
        		cons.setIdChequeraBenefReal(rs.getString("id_chequera_benef"));
        		cons.setIdBanco(rs.getInt("id_banco"));
        		cons.setInstFinan(rs.getString("inst_finan"));
        		cons.setIdChequera(rs.getString("id_chequera"));
        		cons.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
        		cons.setSucursal(rs.getInt("sucursal"));
        		cons.setImporte(rs.getDouble("importe"));
        		cons.setNoCliente(rs.getString("no_cliente"));
        		cons.setRfcBenef(rs.getString("rfc_benef"));
        		
        		if(rs.getString("beneficiario").length()<=35){
        			cons.setBeneficiario(rs.getString("beneficiario"));
				}else{
					String benef=rs.getString("beneficiario");
					int tama=benef.length();
					//System.out.print("tamano de benef"+tama);
					String cadena2=benef.substring(0,35);
					//System.out.println("benef final "+cadena2);
				//	System.out.println("tam final "+cadena2.length());
					cons.setBeneficiario(cadena2);
				}
        		
        		//cons.setBeneficiario(rs.getString("beneficiario"));
        		cons.setLoteEntrada(rs.getInt("lote_entrada"));
        		cons.setPlaza(rs.getInt("plaza"));
        		cons.setNoFolioMov(rs.getInt("no_folio_mov"));
        		cons.setNoFolioDet(rs.getInt("no_folio_det"));
        		cons.setConcepto(rs.getString("concepto"));
        		cons.setFecValor(rs.getDate("fec_valor"));
        		System.out.println("tipo envio "+tipoEnvio);
        		if(tipoEnvio==true){
        			System.out.println("chequera "+rs.getString("id_chequera_benef"));
        			cons.setClabeBenef(rs.getString("id_chequera_benef"));
        		}else{
        			cons.setClabeBenef(rs.getString("id_clabe"));
        		}
        		
        		cons.setSucDestino(rs.getString("sucDestino"));
        		cons.setAba(rs.getString("aba"));
        		cons.setAba(rs.getString("swift_code"));
        		cons.setNombreBancoBenef(rs.getString("desc_banco"));
        		cons.setIdBancoCityStr(rs.getString("id_banco_city"));
        		cons.setDescSucursal(rs.getString("desc_sucursal"));
        		return cons;
			}});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Trapasos, C:TraspasosDao, M:subSeleccionSolicitudes");
		}
		System.out.println("resultado del qry angel.:::"+resultado);
		return resultado;
	}
	
	/**
	 * FunSQLSelect869
	 * @param empresa
	 * @param cuenta
	 * @param divisa
	 * @return
	 */
	
	public List<ComunDto>checarMovimientosNegativos(int empresa, int cuenta, String divisa){
	//cuenta = no_empresa_benef
//		Public Function FunSQLSelect869(ByVal pvvValor1 As Integer, ByVal pvvValor2 As Integer, _
//	            ByVal pvvValor3 As String) As ADODB.Recordset
	
//		'Recibe los par�metros necesarios para ejecutar la sentencia de la forma frmTransInv
		List<ComunDto> resultado = null;
		StringBuffer sb = null;
		String gsDBM = ConstantesSet.gsDBM;
		String sql = "";
		try{
	
			if(gsDBM.equals("ORACLE"))
			{
				sb = new StringBuffer();
				sb.append("\n SELECT coalesce(sum(s.importe),0) - (Select coalesce(sum(a.importe),0) ");
				sb.append("\n                                   From saldo a ");
				sb.append("\n                                  Where a.no_empresa = " + empresa);
				sb.append("\n                                    And a.no_cuenta =" + cuenta);
				sb.append("\n                                    And a.no_linea = s.no_linea");
				sb.append("\n                                    And a.id_tipo_saldo = 7) as suma ");
				sb.append("\n   FROM saldo s ");
				sb.append("\n  WHERE s.no_empresa = " + empresa);
				sb.append("\n    AND s.no_cuenta = " + cuenta);
				sb.append("\n    AND s.id_tipo_saldo in (90,5) ");
				sb.append("\n    AND s.no_linea = convert(numeric,(Select id_divisa_soin ");
				sb.append("\n                                  From cat_divisa ");
				sb.append("\n                                 Where id_divisa = '" + divisa + "' ");
				sb.append("\n                                   And clasificacion='D'),(18))");
			}
			
			if(gsDBM.equals("SQL SERVER")||gsDBM.equals("SYBASE"))
			{
				sb = new StringBuffer();
				sb.append("\n SELECT isnull(sum(s.importe),0) - (Select isnull(sum(a.importe),0) ");
				sb.append("\n                                      From saldo a ");
				sb.append("\n                                     Where a.no_empresa = " + empresa);
				sb.append("\n                                       And a.no_cuenta =" + cuenta);
				sb.append("\n                                       And a.no_linea = s.no_linea");
				sb.append("\n                                       And a.id_tipo_saldo = 7) as suma ");
				sb.append("\n   FROM saldo s ");
				sb.append("\n  WHERE s.no_empresa = " + empresa);
				sb.append("\n    AND s.no_cuenta = " + cuenta);
				sb.append("\n    AND s.id_tipo_saldo in (90,5) ");
				sb.append("\n    AND s.no_linea = convert(int,(Select id_divisa_soin ");
				sb.append("\n                                    From cat_divisa ");
				sb.append("\n                                   Where id_divisa = '" + divisa + "' ");
				sb.append("\n                                     And clasificacion='D'))");
				sb.append("\n  GROUP BY s.no_linea");
			}
			
			if(gsDBM.equals("POSTGRESQL"))
			{
				sb = new StringBuffer();
				sb.append("\n SELECT coalesce(sum(s.importe),0) - (Select coalesce(sum(a.importe),0) ");
				sb.append("\n                                        From saldo a ");
				sb.append("\n                                       Where a.no_empresa = " + empresa);
				sb.append("\n                                         And a.no_cuenta = " + cuenta);
				sb.append("\n                                         And a.no_linea = s.no_linea");
				sb.append("\n                                         And a.id_tipo_saldo = 7) as suma ");
				sb.append("\n   FROM saldo s ");
				sb.append("\n  WHERE s.no_empresa =" + empresa);
				sb.append("\n    AND s.no_cuenta =" + cuenta);
				sb.append("\n    AND s.id_tipo_saldo in (90,5) ");
				sb.append("\n    AND s.no_linea = convert(numeric,(Select id_divisa_soin ");
				sb.append("\n                                From cat_divisa ");
				sb.append("\n                               Where id_divisa = '" + divisa + "' ");
				sb.append("\n                                 And clasificacion='D'),(18))");
				sb.append("\n  GROUP BY s.no_linea");
			}
			
			if(gsDBM.equals("DB2"))
			{
				sb = new StringBuffer();
				sb.append("\n SELECT coalesce(sum(s.importe),0) - (Select coalesce(sum(a.importe),0) ");
				sb.append("\n                                      From saldo a ");
				sb.append("\n                                     Where a.no_empresa = " + empresa);
				sb.append("\n                                       And a.no_cuenta =" + cuenta);
				sb.append("\n                                       And a.no_linea = s.no_linea");
				sb.append("\n                                       And a.id_tipo_saldo = 7) as suma ");
				sb.append("\n   FROM saldo s ");
				sb.append("\n  WHERE s.no_empresa = " + empresa);
				sb.append("\n    AND s.no_cuenta = " + cuenta);
				sb.append("\n    AND s.id_tipo_saldo in (90,5) ");
				sb.append("\n    AND s.no_linea = cast((Select id_divisa_soin ");
				sb.append("\n                                    From cat_divisa ");
				sb.append("\n                                   Where id_divisa = '" + divisa + "' ");
				sb.append("\n                                     And clasificacion='D') as integer)");
				sb.append("\n  GROUP BY s.no_linea");
			}
			
			sql = sb.toString();
			System.out.println(sql);
	        //logger.info("suma: " + sql);
	        resultado = (List<ComunDto>)jdbcTemplate.query(sql, new RowMapper(){
	        	public Object mapRow(ResultSet rs, int i)
				throws SQLException {
	        		ComunDto cons = new ComunDto();
	        		cons.setSuma(rs.getDouble("suma"));
	        		return cons;
				}});
			}
			catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Trapasos, C:TraspasosDao, M:checarMovimientosNegativos");
			}
			return resultado;
	}
	
	/**
	 * FunSQLSelect962
	 * @param rubro
	 * @return
	 */
	
	public List<CatGrupoRubroDto>validarRubro(int rubro){
		List<CatGrupoRubroDto> validaRubro = null;
		StringBuffer sb = new StringBuffer();
		try{
				sb.append("SELECT * FROM cat_rubro WHERE id_rubro = " + rubro);
				System.out.println(sb.toString());
				//logger.info("rubro " + sb.toString());
				validaRubro = (List<CatGrupoRubroDto>)jdbcTemplate.query(sb.toString(), new RowMapper(){
	        	public Object mapRow(ResultSet rs, int i)
				throws SQLException {
	        		CatGrupoRubroDto cons = new CatGrupoRubroDto();
	        		cons.setIdGrupoR(rs.getInt("id_grupo"));
	        		cons.setIdRubroR(rs.getInt("id_rubro"));
	        		cons.setDescRubroR(rs.getString("desc_rubro"));
	        		cons.setIngresoEgresoR(rs.getString("ingreso_egreso"));
	        		cons.setIdRubroEquivaleR(rs.getInt("id_rubro_equivale"));
	        		cons.setBContabilizaR(rs.getString("b_contabiliza"));
	        		return cons;
				}});
			}
			catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Trapasos, C:TraspasosDao, M:validarRubro");
			}
			return validaRubro;
	}
	
	/**
	 * FunSQLSelect876
	 * @param folios
	 * @return
	 */
	
	public List<MovimientoDto>seleccionarDetalleArchivo(String folios){
		List<MovimientoDto> resultado = null;
		StringBuffer sb = new StringBuffer();
		String sql = "";
		try{
	    
		    sb.append(" SELECT m.no_folio_det,");
		    sb.append("\n       m.no_docto,");
		    sb.append("\n       m.fec_valor,");
		    sb.append("\n       m.id_chequera,");
		    sb.append("\n       m.id_banco,");
		    
		    sb.append("\n       m.id_banco_benef,");
		    sb.append("\n       m.id_chequera_benef,");
		    sb.append("\n       b.inst_finan,");
		    sb.append("\n       m.importe,");
		    sb.append("\n       m.beneficiario,");
		    
		    sb.append("\n       m.sucursal,");
		    sb.append("\n       m.plaza,");
		    sb.append("\n       m.concepto");
		    
		    sb.append("\n  FROM movimiento m, cat_banco b");
		    sb.append("\n WHERE m.no_folio_det in (" + folios + ")");
		    sb.append("\n   AND m.id_banco_benef = b.id_banco");	    
		    
		    sql = sb.toString();
		    System.out.println(sql);
	        
	        resultado = jdbcTemplate.query(sql, new RowMapper<MovimientoDto>(){
	        	public MovimientoDto mapRow(ResultSet rs, int i) throws SQLException {
	        		MovimientoDto cons = new MovimientoDto();
	        		cons.setNoFolioDet(rs.getInt("no_folio_det"));
	        		cons.setNoDocto(rs.getString("no_docto"));
	        		cons.setFecValor(rs.getDate("fec_valor"));
	        		cons.setIdChequera(rs.getString("id_chequera"));
	        		cons.setIdBanco(rs.getInt("id_banco"));
	        		cons.setIdBancoBenef(rs.getInt("id_banco_benef"));
	        		cons.setIdChequeraBenef(rs.getString("id_chequera_benef"));
	        		cons.setInstFinan(rs.getString("inst_finan"));
	        		cons.setImporte(rs.getDouble("importe"));
	        		cons.setBeneficiario(rs.getString("beneficiario"));
	        		cons.setSucursal(rs.getInt("sucursal"));
	        		cons.setPlaza(rs.getInt("plaza"));
	        		cons.setConcepto(rs.getString("concepto"));
	        		return cons;
				}});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Trapasos, C:TraspasosDao, M:seleccionarDetalleArchivo");
			e.printStackTrace();
		}
		return resultado;
	}
	
	/**
	 * FunSQLInsert_det_arch_transfer
	 * @param parametro
	 * @param psNomArch
	 * @return res
	 */
	public int insertarDetArchTransfer(MovimientoDto parametro, String psNomArch){
		int res = -1;
		StringBuffer sb = new StringBuffer();
		funciones = new Funciones();
		try{
			sb.append("\n  INSERT INTO det_arch_transfer (nom_arch,no_folio_det,no_docto,id_estatus_arch,");
			sb.append("\n  fec_valor , id_chequera, id_banco, id_banco_benef, id_chequera_benef, ");
			sb.append("\n  prefijo_benef,importe,beneficiario,sucursal,plaza,concepto ) ");
			sb.append("\n  values('" + psNomArch + "'," + parametro.getNoFolioDet() + ",");
			sb.append("\n  '" + parametro.getNoDocto() + "','T',convert(date,'" + funciones.ponerFechaSola(parametro.getFecValor()) + "',103),");
			sb.append("\n  '" + parametro.getIdChequera() + "'," + parametro.getIdBanco() + ",");
			sb.append("\n   " + parametro.getIdBancoBenef() + ",'" + parametro.getIdChequeraBenef() + "',");
			sb.append("\n  '" + parametro.getInstFinan() + "'," + parametro.getImporte() + ",'" + parametro.getBeneficiario() + "',");
			sb.append("\n   " + parametro.getSucursal());
			sb.append("\n  ," + parametro.getPlaza() + ",'" + parametro.getConcepto() + "')");
			System.out.println(" insert arch dtalle"+sb);
			res = jdbcTemplate.update(sb.toString());
		} catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosDao, M:insertarDetArchTransfer");
		}
		return res;
	}
	
	/**
	 * FunSQLInsert232
	 * @param psNomArch
	 * @param banco
	 * @param fecha
	 * @param total
	 * @param registros
	 * @param usuario
	 * @return res
	 */
	public int insertarArchTransfer(String psNomArch, int banco, Date fecha, double total, int registros, int usuario){
		int res = -1;
		StringBuffer sb = new StringBuffer();
		funciones = new Funciones();
		try{
			sb.append("\n  INSERT INTO arch_transfer ");
			sb.append("\n  (nom_arch,id_banco,fec_trans, importe, registros, fec_retrans,no_usuario_alta,");
			sb.append("\n  no_usuario_modif, b_cheque_ocurre) ");
			sb.append("\n  VALUES ");
			sb.append("\n  ('" + psNomArch + "'," + banco + ",");
			sb.append("\n  convert(date,'" +  funciones.ponerFormatoDate(fecha) + "',103),");
			sb.append("\n  " + total + ",");
			sb.append("\n" +  registros + ",null,");
			sb.append("\n" +  usuario + ",null,null) ");
			
			res = jdbcTemplate.update(sb.toString());
		} catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosDao, M:insertarArchTransfer");
		}
		return res;
	}

	/**
	 * FunSQLUpdate_movimiento_tipoEnvio
	 * @param psTipoEnvio
	 * @param psNomArchivos
	 * @param pbMasspayment
	 * @return res
	 */
	public int actualizarMovimientoTipoEnvio(String psTipoEnvio, String psNomArchivos, boolean pbMasspayment)
	{
		int res = -1;
		StringBuffer sb = new StringBuffer();
		String psArchivos = "";
		try{
//			Public Function FunSQLUpdate_movimiento_tipoEnvio(ByVal psTipoEnvio As String, ByVal psNomArchivos As String, _
//			        Optional ByVal pbMasspayment As Boolean) As Long
			    
				psArchivos = psNomArchivos.replace(".txt", "");
				psArchivos = psArchivos.replace(".sha", "");
				if(funciones.cadenaRight(psArchivos, 1).equals(","))
				{
			        psArchivos = psArchivos.substring(0, psArchivos.length() - 1);
				}
				psArchivos = "'" + psArchivos.replace(",", "','") + "'";
				    
			    sb.append("\n UPDATE movimiento SET ");
			    if(pbMasspayment == true)
			        sb.append("\n id_estatus_mov = 'T', ");
			    sb.append("\n id_servicio_be = '" + psTipoEnvio + "' ");
			    sb.append("\n WHERE no_folio_det in(");
			    sb.append("\n select no_folio_det from det_arch_transfer where nom_arch in(" + psArchivos + ") ");
			    sb.append("\n ) ");
	
			    //logger.info("actualiza movimiento "+sb.toString());
				res = jdbcTemplate.update(sb.toString());
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Traspasos, C:TraspasosDao, M:actualizarMovimientoTipoEnvio");
			}
		return res;
	}
	
	public List<Map<String, Object>> reporteDetArchTraspInv(String archivo){
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
		
		System.out.println("reporte dao");
		try {
			archivo = archivo.substring(0, archivo.indexOf("."));
			sql.append(" SELECT c.no_empresa as empresa_orig,c2.no_empresa as empresa_dest, \n");
            sql.append(" d.no_folio_det,d.nom_arch, d.no_docto, d.id_estatus_arch, d.fec_valor, \n");
            sql.append(" d.id_banco, d.id_banco_benef, d.id_chequera, d.id_chequera_benef, \n");
            sql.append(" d.importe , d.prefijo_benef,  d.sucursal, d.plaza, d.beneficiario, \n");
            sql.append(" c2.id_clabe as clabe_benef, \n");
            sql.append(" e.nom_empresa as nom_empresa_orig, e2.nom_empresa as nom_empresa_dest, m.id_divisa \n");
			
			/*if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE")) {
				sql.append(" FROM det_arch_transfer d, cat_cta_banco c, cat_cta_banco c2 \n");
	            sql.append("       ,movimiento m,empresa e, empresa e2 \n");
	            sql.append(" WHERE \n");
	            sql.append("    d.no_folio_det = m.no_folio_det \n");
	            sql.append("   AND m.no_empresa = e.no_empresa \n");
	            sql.append("   AND convert(integer,m.no_cliente) = e2.no_empresa \n");
	            sql.append("   AND d.id_chequera *= c.id_chequera \n");
	            sql.append("   AND d.id_chequera_benef *= c2.id_chequera \n");
	            sql.append("   AND d.id_banco *= c.id_banco \n");
	            sql.append("   AND d.id_banco_benef *= c2.id_banco \n");
	            sql.append("   AND d.id_estatus_arch <> 'X' \n");
	            sql.append("   AND nom_arch = '" + archivo + "' \n");
			}else if(gsDBM.equals("POSTGRESQL")) {
				sql.append("   FROM det_arch_transfer d \n");
	            sql.append("        LEFT JOIN cat_cta_banco c ON (d.id_banco = c.id_banco and d.id_chequera = c.id_chequera) \n");
	            sql.append("        LEFT JOIN cat_cta_banco c2 ON (d.id_banco_benef = c2.id_banco and d.id_chequera_benef = c2.id_chequera) \n");
	            sql.append("        LEFT JOIN movimiento m ON (d.no_folio_det = m.no_folio_det) \n");
	            sql.append("        ,empresa e, empresa e2 \n");
	            sql.append("  WHERE \n");
	            sql.append("    m.no_empresa = e.no_empresa \n");
	            sql.append("    AND TO_NUMBER(m.no_cliente,'999999999') = e2.no_empresa \n");
	            sql.append("    AND d.id_estatus_arch <> 'X' \n");
	            sql.append("    AND nom_arch = '" + archivo + "' \n");
			}else if(gsDBM.equals("DB2")) {*/
            
			sql.append("  FROM det_arch_transfer d \n");
            sql.append("       LEFT JOIN cat_cta_banco c ON (d.id_banco = c.id_banco and d.id_chequera = c.id_chequera)\n");
            sql.append("       LEFT JOIN cat_cta_banco c2 ON (d.id_banco_benef = c2.id_banco and d.id_chequera_benef = c2.id_chequera)\n");
            sql.append("       LEFT JOIN movimiento m ON (d.no_folio_det = m.no_folio_det)\n");
            sql.append("       ,empresa e, empresa e2 \n");
            sql.append(" WHERE \n");
            sql.append("   m.no_empresa = e.no_empresa \n");
            sql.append("   AND cast(m.no_cliente as integer) = e2.no_empresa \n");
            sql.append("   AND d.id_estatus_arch <> 'X' \n");
            sql.append("   AND nom_arch = '" + archivo + "' \n");
				
			System.out.println("traspaso reporte         ------>"+sql.toString());

			listResult = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, Object>>(){
				public Map<String,Object> mapRow(ResultSet rs, int idx) throws SQLException {
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("noEmpresaO", rs.getString("empresa_orig"));
					map.put("noEmpresaD", rs.getString("empresa_dest"));
					map.put("noFolioDet", rs.getString("no_folio_det"));
					map.put("nomArch", rs.getString("nom_arch"));
					map.put("noDocto", rs.getString("no_docto"));
					map.put("idEstatusArch", rs.getString("id_estatus_arch"));
					//map.put("fecValor", rs.getDate("fec_valor"));
					map.put("idEstatusArch", rs.getString("id_estatus_arch"));
					map.put("idBanco", rs.getString("id_banco"));
					map.put("idBancoBenef", rs.getString("id_banco_benef"));
					map.put("idChequera", rs.getString("id_chequera"));
					map.put("idChequeraBenef", rs.getString("id_chequera_benef"));
					map.put("importe", rs.getDouble("importe"));
					map.put("prefijoBenef", rs.getString("prefijo_benef"));
					map.put("sucursal", rs.getString("sucursal"));
					map.put("plaza", rs.getString("plaza"));
					map.put("beneficiario", rs.getString("beneficiario"));
					map.put("clabeBenef", rs.getString("clabe_benef"));
					map.put("nomEmpresaO", rs.getString("nom_empresa_orig"));
					map.put("nomEmpresaD", rs.getString("nom_empresa_dest"));
					map.put("idDivisa", rs.getString("id_divisa"));
					return map;
				}
			});        
	        
	    }catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:TraspasosDao, M:reporteDetArchTraspInv");
			e.printStackTrace();
		}
		return listResult;
	}
	
	public List<LlenaComboEmpresasDto> llenaComboEmpresas(int noUsuario){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.llenarComboEmpresas(noUsuario);
	}	
	
	public int cancelaMovimientos(String foliosCancelados){
		int resultado = 0;
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("UPDATE movimiento ");
		    sql.append("\n SET id_estatus_mov = 'X' ");
		    sql.append("\n WHERE no_folio_mov in ("+ foliosCancelados +")" );		    
		    		    
		    resultado = jdbcTemplate.update(sql.toString());
		    		
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Traspasos, C: TraspasosDao, M: cancelaMovimientos");
		}return resultado;
	}
	
	public int validaFacultad(int idFacultad) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.validaFacultad(idFacultad);
	}
	
	public List<MovimientoDto> validaAuto(int noFolioDet) {
		StringBuffer sql = new StringBuffer();
		List<MovimientoDto> list = new ArrayList<MovimientoDto>();
		
		try {
			sql.append(" SELECT coalesce(usuario_uno, 0) as usuario_uno, coalesce(usuario_dos, 0) as usuario_dos \n");
			sql.append(" FROM autorizacionesMov \n");
			sql.append(" WHERE no_folio_det = "+ noFolioDet +" \n");
			System.out.println(sql);
			list = (List<MovimientoDto>)jdbcTemplate.query(sql.toString(), new RowMapper(){
	        	public Object mapRow(ResultSet rs, int i)
				throws SQLException {
	        		MovimientoDto cons = new MovimientoDto();
	        		cons.setUsuarioAlta(rs.getInt("usuario_uno"));
	        		cons.setUsuarioModif(rs.getInt("usuario_dos"));
	        		return cons;
				}});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Traspasos, C: TraspasosBusiness, M: validaAuto");
		}
		return list;
	}
	
	public int autoDesAuto(int noFolioDet, String usrAutori, int usr) {
		int res = 0;
		StringBuffer sql = new StringBuffer();
		globalSingleton = GlobalSingleton.getInstancia();
		
		try {
			sql.append(" UPDATE autorizacionesMov SET \n");
			sql.append(" "+ usrAutori +" = "+ usr +" \n");
			sql.append(" WHERE no_folio_det = "+ noFolioDet +" \n");
			
			res = jdbcTemplate.update(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Traspasos, C: TraspasosDao, M: autoDesAuto");
		}
		return res;
	}
	
	public int insertAutorizacionesMov(int noFolioDet, int idUsuario) {
		int res = 0;
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" INSERT INTO autorizacionesMov VALUES( "+ noFolioDet +", "+ idUsuario +", 0)");
			
			res = jdbcTemplate.update(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Traspasos, C: TraspasosDao, M: insertAutorizacionesMov");
		}
		return res;
	}
	
	public int validaPassword(int usr, String pass) {
		StringBuffer sql = new StringBuffer();
		int res = 0;
		
		try {
			sql.append(" SELECT count(*) \n");
			sql.append(" FROM seg_usuario \n");
			sql.append(" WHERE id_usuario = "+ usr +" \n");
			sql.append(" 	And contrasena = '"+ pass +"' \n");
			System.out.println(sql);
			res = jdbcTemplate.queryForInt(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Traspasos, C: TraspasosDao, M: validaPassword");
		}
		return res;
	}
	
	//getters && setters
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<GrupoDTO> llenarComboGrupo(String idTipoGrupo) {

		String sql = "";
		StringBuffer sb = new StringBuffer();
		System.out.println("entro"+idTipoGrupo.toString());
		if( !idTipoGrupo.equals("N") && !idTipoGrupo.equals("CC") && !idTipoGrupo.equals("CE") ){
			sb.append( " SELECT id_grupo as id, desc_grupo as describ\n");
			sb.append( " FROM cat_grupo\n");
			sb.append( " WHERE id_grupo in( \n");
			sb.append( "					select distinct id_grupo \n"); 
			sb.append( "					from cat_rubro \n");
			sb.append( "					where ingreso_egreso = '" +idTipoGrupo +"') \n");
		}else{
			sb.append( " SELECT id_grupo as id, desc_grupo as describ\n");
			sb.append( " FROM cat_grupo \n");
			
			if(idTipoGrupo.equals("CC"))
				sb.append( " WHERE id_grupo in (11500) \n");
			
			if(idTipoGrupo.equals("CE"))
				sb.append( " WHERE id_grupo = 70700 \n");
		}
				
	    sql=sb.toString();
	    
	    System.out.println(sb.toString());
	    
		List <GrupoDTO> grupos = null;

		grupos = jdbcTemplate.query(sql, new RowMapper(){			
			public GrupoDTO mapRow(ResultSet rs, int idx){				
				GrupoDTO grupo = new GrupoDTO();
					try {
						grupo.setDescGrupo( rs.getString("describ") );
						grupo.setIdGrupo( rs.getInt("id") );
					} catch (SQLException e) {
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
								+ "P:Traspasos, C:TraspasosDao, M:llenarComboGrupo");
					}
				return grupo;				
		}});
			
		return grupos;
		
	}
	
	
	public List<GrupoDTO> llenarComboGrupoVX(String idTipoGrupo, int noEmpresa) {
		String sql = "";
		StringBuffer sb = new StringBuffer();
		
		//if( !idTipoGrupo.equals("N") && !idTipoGrupo.equals("CC") ){
		sb.append( " SELECT id_grupo as id, desc_grupo as describ\n");
		sb.append( " FROM cat_grupo\n");
		sb.append( " WHERE id_grupo < 50000 ");
		
		
		/*
		sb.append( "					select distinct id_grupo \n"); 
		sb.append( "					from cat_rubro \n");
		sb.append( "					where ingreso_egreso in ('" +idTipoGrupo +"') \n");
		sb.append( "					and id_rubro in( \n");
		sb.append( "					               select distinct id_rubro \n");
		sb.append( "					               from guia_contable \n");
		sb.append( "					               where no_empresa = " + noEmpresa +"\n");
		
		if(idTipoGrupo.equals("I', 'E"))
			sb.append( "					               And (id_grupo = 5000 or (id_grupo = 10000))) \n");
		else
			sb.append( "					               ) \n");
		
		sb.append( "					               ) \n");
			*/
		/*}else{
			
			sb.append( " SELECT id_grupo as id, desc_grupo as describ\n");
			sb.append( " FROM cat_grupo \n");
			
			
			if(idTipoGrupo.equals("CC"))
				sb.append( " WHERE id_grupo = 10000 \n");
		}*/
				
	    sql=sb.toString();
	    
	    System.out.println("pts" + sql);
	    
		List <GrupoDTO> grupos = null;

		grupos = jdbcTemplate.query(sql, new RowMapper(){			
			public GrupoDTO mapRow(ResultSet rs, int idx){				
				GrupoDTO grupo = new GrupoDTO();
					try {
						grupo.setDescGrupo( rs.getString("describ") );
						grupo.setIdGrupo( rs.getInt("id") );
					} catch (SQLException e) {
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
								+ "P:Traspasos, C:TraspasosDao, M:llenarComboGrupo");
					}
				return grupo;				
		}});
			
		return grupos;
		
	}

    
	public List<RubroDTO> llenarComboRubro(int idGrupo, int noEmpresa) {
		StringBuffer sb = new StringBuffer();
		List <RubroDTO> rubros = null;
		System.out.println("entro rubros"+idGrupo+" "+noEmpresa);
		try {
		sb.append( " SELECT id_rubro as id, desc_rubro as describ\n");
		sb.append( " FROM cat_rubro\n");
		sb.append( " WHERE id_grupo = ");		
		sb.append(   idGrupo );
		
		System.out.println(sb.toString());
	    
		rubros = jdbcTemplate.query(sb.toString(), new RowMapper(){			
			public RubroDTO mapRow(ResultSet rs, int idx) throws SQLException {
				RubroDTO rubro = new RubroDTO();
				rubro.setDescRubro( rs.getString("describ") );
				rubro.setIdRubro( rs.getString("id") );
				return rubro;				
			}
		});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Traspasos, C:TraspasosDao, M:llenarComboGrupo");
		}
		return rubros;
	}


}//End class 