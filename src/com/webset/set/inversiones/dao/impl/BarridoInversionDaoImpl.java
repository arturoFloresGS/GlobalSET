package com.webset.set.inversiones.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

import com.webset.set.inversiones.dao.BarridoInversionDao;
import com.webset.set.inversiones.dto.CanastaInversionesDto;
import com.webset.set.traspasos.dao.TraspasosDao;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.MovimientoDto;

@SuppressWarnings("unchecked")
public class BarridoInversionDaoImpl implements BarridoInversionDao {
	private Bitacora bitacora = new Bitacora();
	private JdbcTemplate jdbcTemplate;
	GlobalSingleton globalSingleton;
	ConsultasGenerales consultasGenerales;
	Funciones funciones = new Funciones();
	TraspasosDao traspasosDao = new TraspasosDao();
	
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:BarridoInversionDaoImpl, M:setDataSource");
		}
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	@Override
	public List<CanastaInversionesDto> empresasConcentradoras() {
		List<CanastaInversionesDto> listEmpresas = new ArrayList<CanastaInversionesDto>();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT no_empresa, nom_empresa \n");
			sql.append(" FROM empresa \n");
			sql.append(" WHERE b_concentradora = 'S' \n");
			
			listEmpresas = jdbcTemplate.query(sql.toString(), new RowMapper(){
		    	public CanastaInversionesDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		CanastaInversionesDto dtoEmp = new CanastaInversionesDto();
		    		dtoEmp.setNoEmpresa(rs.getInt("no_empresa"));
		    		dtoEmp.setNomEmpresa(rs.getString("nom_empresa"));
			    	return dtoEmp;
		    	}
		    });
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:empresasConcentradoras");
		}
		return listEmpresas;
	}

	@Override
	public List<CanastaInversionesDto> todosLosBancos(String idDivisa) {
		List<CanastaInversionesDto> listBancos = new ArrayList<CanastaInversionesDto>();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT distinct ccb.id_banco, cb.desc_banco \n");
			sql.append(" FROM cat_banco cb, cat_cta_banco ccb \n");
			sql.append(" WHERE cb.id_banco = ccb.id_banco \n");
			sql.append(" 	And ccb.id_divisa = '"+ idDivisa +"' \n");
			
			listBancos = jdbcTemplate.query(sql.toString(), new RowMapper(){
		    	public CanastaInversionesDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		CanastaInversionesDto dtoBco = new CanastaInversionesDto();
		    		dtoBco.setNoBanco(rs.getInt("id_banco"));
		    		dtoBco.setNomBanco(rs.getString("desc_banco"));
			    	return dtoBco;
		    	}
		    });
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:todosLosBancos");
		}
		return listBancos;
	}

	@Override
	public List<CanastaInversionesDto> obtenerRegistros(String idDivisa, String noBanco) {
		List<CanastaInversionesDto> listRegistros = new ArrayList<CanastaInversionesDto>();
		globalSingleton = GlobalSingleton.getInstancia();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT e.no_empresa, e.nom_empresa, cb.id_banco, cb.desc_banco, \n");
			sql.append(" 		ccb.id_chequera, ccb.desc_chequera, ccb.id_divisa, ccb.saldo_final, \n");
			sql.append(" 		coalesce((select SUM(m.importe) from movimiento m where m.b_entregado = 'N' \n");
			sql.append(" 		and m.b_gen_contable is null and m.no_empresa = ccb.no_empresa and m.id_chequera = ccb.id_chequera \n");
			sql.append(" 		and m.id_tipo_operacion = 3200 and id_estatus_mov = 'I'), 0) as saldoTransito, \n");
			sql.append(" 		ccb.saldo_final + coalesce((select SUM(m.importe) from movimiento m where m.b_entregado = 'N' \n");
			sql.append(" 		and m.b_gen_contable is null and m.no_empresa = ccb.no_empresa and m.id_chequera = ccb.id_chequera \n");
			sql.append(" 		and m.id_tipo_operacion = 3200 and id_estatus_mov = 'I'), 0) as saldoBancario, \n");
			sql.append(" 		'' as saldoInvertir, '' as dias \n");
			sql.append(" FROM empresa e, cat_cta_banco ccb, cat_banco cb \n");
			sql.append(" WHERE e.no_empresa = ccb.no_empresa \n");
			sql.append(" 	And ccb.id_banco = cb.id_banco \n");
			sql.append(" 	And ccb.id_divisa = '"+ idDivisa +"' \n");
			sql.append(" 	And ccb.no_empresa in (select no_empresa from usuario_empresa \n");
			sql.append(" 		where no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + ") \n");
			
			if(!noBanco.equals(""))
				sql.append(" And ccb.id_banco = "+ noBanco +" \n");
			
			sql.append(" ORDER BY e.nom_empresa \n");
			
			listRegistros = jdbcTemplate.query(sql.toString(), new RowMapper(){
		    	public CanastaInversionesDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		CanastaInversionesDto dtoBco = new CanastaInversionesDto();
	    			dtoBco.setNoEmpresa(rs.getInt("no_empresa"));
	    			dtoBco.setNomEmpresa(rs.getString("nom_empresa"));
	    			dtoBco.setNoBanco(rs.getInt("id_banco"));
		    		dtoBco.setNomBanco(rs.getString("desc_banco"));
		    		dtoBco.setIdChequera(rs.getString("id_chequera"));
		    		dtoBco.setDescChequera(rs.getString("desc_chequera"));
		    		dtoBco.setIdDivisa(rs.getString("id_divisa"));
		    		dtoBco.setSaldoFinal(rs.getDouble("saldo_final"));
		    		dtoBco.setSaldoTransito(rs.getDouble("saldoTransito"));
		    		dtoBco.setSaldoBancario(rs.getDouble("saldoBancario"));
		    		dtoBco.setSaldoInvertir(rs.getString("saldoInvertir"));
		    		dtoBco.setDias(rs.getString("dias"));
			    	return dtoBco;
		    	}
		    });
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:obtenerRegistros");
		}
		return listRegistros;
	}

	@Override
	public Date obtenerFechaHoy() {
		consultasGenerales = new ConsultasGenerales (jdbcTemplate);
		return consultasGenerales.obtenerFechaHoy();
	}

	@Override
	public int buscaRegistro(List<Map<String, String>> datosGrid, int i, Date fecHoy) {
		StringBuffer sql = new StringBuffer();
		int result = 0;
		
		try {
			sql.append(" SELECT count(*) \n");
			sql.append(" FROM control_barrido \n");
			sql.append(" WHERE id_chequera = '"+ datosGrid.get(i).get("idChequera").toString() +"' \n");
			sql.append(" 	And importe = "+ datosGrid.get(i).get("saldoInvertir") +" \n");
			sql.append(" 	And plazo = "+ datosGrid.get(i).get("noDias") +" \n");
			sql.append(" 	And fecha_solicitud = '"+ funciones.ponerFecha(fecHoy) +"' \n");
			sql.append(" 	And id_estatus = 'P' \n");
			
			result = jdbcTemplate.queryForInt(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:buscaRegistro");
		}
		return result;
	}

	@Override
	public int insertarBarridos(List<Map<String, String>> datosGrid, int i, Date fecHoy) {
		StringBuffer sql = new StringBuffer();
		int result = 0;
		
		try {
			sql.append(" INSERT INTO control_barrido \n");
			sql.append(" 	(id_chequera, importe, concepto, plazo, no_concentradora, \n");
			sql.append("	fecha_solicitud, id_estatus, id_usuario_autoriza) \n");
			sql.append(" VALUES('"+ datosGrid.get(i).get("idChequera").toString() +"', \n");
			sql.append(" 		"+ Double.parseDouble(datosGrid.get(i).get("saldoInvertir")) +", \n");
			sql.append(" 		'TRASPASO DE INVERSION', "+ datosGrid.get(i).get("noDias") +", \n");
			sql.append(" 		"+ datosGrid.get(i).get("noEmpresaCon") +", '"+ funciones.ponerFecha(fecHoy) +"', \n");
			sql.append(" 		'P', 0) \n");
			
			result = jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:insertarBarridos");
		}
		return result;
	}
	
	
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++ PROCESO DE EJECUCION DE BARRIDO INVERSION +++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	
	
	@Override
	public List<CanastaInversionesDto> obtenerSolicitudesBarrido(String idDivisa, String noBanco) {
		List<CanastaInversionesDto> listRegistros = new ArrayList<CanastaInversionesDto>();
		globalSingleton = GlobalSingleton.getInstancia();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT crb.no_solicitud, ccb.no_empresa, e.nom_empresa, ccb.id_banco, cb.desc_banco, \n");
			sql.append(" 		crb.id_chequera, crb.importe, crb.no_concentradora, eb.nom_empresa as nom_empresa_benef, \n");
			sql.append(" 		ccbb.id_banco as id_banco_benef, cbb.desc_banco as desc_banco_benef, \n");
			sql.append(" 		ccbb.id_chequera as id_chequera_benef, crb.fecha_solicitud, \n");
			sql.append(" 		cd.desc_divisa, crb.concepto, coalesce(su.clave_usuario, '') as clave_usuario, \n");
			sql.append(" 		case when crb.id_usuario_autoriza = 0 then 'color:#000000' else 'color:#04A861' end as color \n");
			sql.append(" FROM control_barrido crb left join seg_usuario su on (crb.id_usuario_autoriza = su.id_usuario ), \n");
			sql.append(" 		cat_cta_banco ccb, empresa e, cat_banco cb, empresa eb, cat_cta_banco ccbb, cat_banco cbb, \n");
			sql.append(" 		cat_divisa cd \n");
			sql.append(" WHERE crb.id_chequera = ccb.id_chequera \n");
			sql.append(" 	And ccb.no_empresa = e.no_empresa \n");
			sql.append(" 	And ccb.id_banco = cb.id_banco \n");
			sql.append(" 	And crb.no_concentradora = eb.no_empresa \n");
			sql.append(" 	And crb.no_concentradora = ccbb.no_empresa \n");
			sql.append(" 	And ccbb.id_banco = cbb.id_banco \n");
			sql.append(" 	And ccb.id_divisa = cd.id_divisa \n");
			sql.append(" 	And crb.id_estatus = 'P' \n");
			sql.append(" 	And ccb.id_divisa = '"+ idDivisa +"' \n");
			sql.append(" 	And ccb.no_empresa in (select no_empresa from usuario_empresa \n");
			sql.append(" 		where no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + ") \n");
			
			if(!noBanco.equals(""))
				sql.append(" And ccb.id_banco = "+ noBanco +" \n");
			
			sql.append(" ORDER BY e.nom_empresa \n");
			
			listRegistros = jdbcTemplate.query(sql.toString(), new RowMapper(){
		    	public CanastaInversionesDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		CanastaInversionesDto dtoBco = new CanastaInversionesDto();
	    			dtoBco.setNoEmpresa(rs.getInt("no_empresa"));
	    			dtoBco.setNomEmpresa(rs.getString("nom_empresa"));
	    			dtoBco.setNoBanco(rs.getInt("id_banco"));
		    		dtoBco.setNomBanco(rs.getString("desc_banco"));
		    		dtoBco.setIdChequera(rs.getString("id_chequera"));
		    		dtoBco.setImporteTraspaso(rs.getDouble("importe"));
		    		dtoBco.setNoEmpresaBenef(rs.getInt("no_concentradora"));
	    			dtoBco.setNomEmpresaBenef(rs.getString("nom_empresa_benef"));
	    			dtoBco.setNoBancoBenef(rs.getInt("id_banco_benef"));
		    		dtoBco.setNomBancoBenef(rs.getString("desc_banco_benef"));
		    		dtoBco.setIdChequeraBenef(rs.getString("id_chequera_benef"));
		    		dtoBco.setFechaSolicitud(rs.getDate("fecha_solicitud"));
		    		dtoBco.setDescDivisa(rs.getString("desc_divisa"));
		    		dtoBco.setConcepto(rs.getString("concepto"));
		    		dtoBco.setClaveUsuario(rs.getString("clave_usuario"));
		    		dtoBco.setNoSolicitud(rs.getInt("no_solicitud"));
		    		dtoBco.setColor(rs.getString("color"));
			    	return dtoBco;
		    	}
		    });
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:obtenerSolicitudesBarrido");
		}
		return listRegistros;
	}

	@Override
	public int validaFacultad(int idFacultad) {
		consultasGenerales = new ConsultasGenerales (jdbcTemplate);
		return consultasGenerales.validaFacultad(idFacultad);
	}

	@Override
	public boolean validaPassword(String pass) {
		globalSingleton = GlobalSingleton.getInstancia();
		
		consultasGenerales = new ConsultasGenerales (jdbcTemplate);
		return consultasGenerales.validarUsuarioAutenticado(globalSingleton.getUsuarioLoginDto().getIdUsuario(), pass);
	}

	@Override
	public int verificaBarridoAut(List<Map<String, String>> datosGrid, int i) {
		StringBuffer sql = new StringBuffer();
		int result = 0;
		
		try {
			sql.append(" SELECT count(*) \n");
			sql.append(" FROM control_barrido \n");
			sql.append(" WHERE no_solicitud = "+ datosGrid.get(i).get("noSolicitud") +" \n");
			sql.append(" 	And id_estatus = 'P' \n");
			sql.append(" 	And id_usuario_autoriza != 0 \n");
			
			result = jdbcTemplate.queryForInt(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:verificaBarridoAut");
		}
		return result;
	}

	@Override
	public int verificaBarridoDes(List<Map<String, String>> datosGrid, int i) {
		globalSingleton = GlobalSingleton.getInstancia();
		StringBuffer sql = new StringBuffer();
		int result = 0;
		
		try {
			sql.append(" SELECT count(*) \n");
			sql.append(" FROM control_barrido \n");
			sql.append(" WHERE no_solicitud = "+ datosGrid.get(i).get("noSolicitud") +" \n");
			sql.append(" 	And id_estatus = 'P' \n");
			sql.append(" 	And id_usuario_autoriza = "+ globalSingleton.getUsuarioLoginDto().getIdUsuario() +" \n");
			
			result = jdbcTemplate.queryForInt(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:verificaBarridoDes");
		}
		return result;
	}

	@Override
	public int autorizaBarrido(List<Map<String, String>> datosGrid, int i) {
		globalSingleton = GlobalSingleton.getInstancia();
		StringBuffer sql = new StringBuffer();
		int result = 0;
		
		try {
			sql.append(" UPDATE control_barrido \n");
			sql.append(" 	SET id_usuario_autoriza = "+ globalSingleton.getUsuarioLoginDto().getIdUsuario() +" \n");
			sql.append(" WHERE no_solicitud = "+ datosGrid.get(i).get("noSolicitud") +" \n");
			sql.append(" 	And id_estatus = 'P' \n");
			
			result = jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:autorizaBarrido");
		}
		return result;
	}

	@Override
	public int desAutorizaBarrido(List<Map<String, String>> datosGrid, int i) {
		globalSingleton = GlobalSingleton.getInstancia();
		StringBuffer sql = new StringBuffer();
		int result = 0;
		
		try {
			sql.append(" UPDATE control_barrido \n");
			sql.append(" 	SET id_usuario_autoriza = 0 \n");
			sql.append(" WHERE no_solicitud = "+ datosGrid.get(i).get("noSolicitud") +" \n");
			sql.append(" 	And id_estatus = 'P' \n");
			sql.append(" 	And id_usuario_autoriza = "+ globalSingleton.getUsuarioLoginDto().getIdUsuario() +" \n");
			
			result = jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:autorizaBarrido");
		}
		return result;
	}

	@Override
	public int regresarBarrido(List<Map<String, String>> datosGrid, int i) {
		globalSingleton = GlobalSingleton.getInstancia();
		StringBuffer sql = new StringBuffer();
		int result = 0;
		
		try {
			sql.append(" UPDATE control_barrido \n");
			sql.append(" 	SET id_estatus = 'X' \n");
			sql.append(" WHERE no_solicitud = "+ datosGrid.get(i).get("noSolicitud") +" \n");
			sql.append(" 	And id_estatus = 'P' \n");
			sql.append(" 	And id_usuario_autoriza = 0 \n");
			
			result = jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:regresarBarrido");
		}
		return result;
	}
	
	@Override
	public Map ejecutarBarridos(String noSolicitudes) {
		globalSingleton = GlobalSingleton.getInstancia();
		Map resul = new HashMap();
		HashMap< Object, Object > inParams = new HashMap< Object, Object >();
		
		try {
			StoredProcedure stored = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_ci_traspasos_barridos") {};
			stored.declareParameter(new SqlParameter("usuario", Types.INTEGER));
			stored.declareParameter(new SqlParameter("folSolic", Types.VARCHAR));
			stored.declareParameter(new SqlInOutParameter("result", Types.INTEGER));
			stored.declareParameter(new SqlInOutParameter("mensaje", Types.VARCHAR));
			
			inParams.put("usuario", globalSingleton.getUsuarioLoginDto().getIdUsuario());
			inParams.put("folSolic", noSolicitudes);
			inParams.put("result", 0);
			inParams.put("mensaje", "");
			
			resul = stored.execute((Map)inParams);
			
			}catch (Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
				"P:Inversiones, C:BarridoInversionDaoImpl, M:ejecutarBarridos");
		}
		return resul;
	}
	
	@Override
	public Map armaCanastas() {
		globalSingleton = GlobalSingleton.getInstancia();
		Map resul = new HashMap();
		HashMap< Object, Object > inParams = new HashMap< Object, Object >();
		
		try {
			StoredProcedure stored = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_ci_inicializa_canastas") {};
			stored.declareParameter(new SqlParameter("usuario", Types.INTEGER));
			stored.declareParameter(new SqlInOutParameter("result", Types.INTEGER));
			stored.declareParameter(new SqlInOutParameter("mensaje", Types.VARCHAR));
			
			inParams.put("usuario", globalSingleton.getUsuarioLoginDto().getIdUsuario());
			inParams.put("result", "");
			inParams.put("mensaje", 0);
			
			resul = stored.execute((Map)inParams);
			
			}catch (Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
				"P:Inversiones, C:BarridoInversionDaoImpl, M:armaCanastas");
		}
		return resul;
	}
	
	
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++        MONITOR DE INVERSION        ++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	
	
	@Override
	public List<CanastaInversionesDto> obtenerCanastasInv(String noEmpresa, String idDivisa) {
		List<CanastaInversionesDto> listCanastas = new ArrayList<CanastaInversionesDto>();
		globalSingleton = GlobalSingleton.getInstancia();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT distinct ec.no_empresa, ec.nom_empresa, 'CANASTA ' + cast(ci.no_canasta as varchar(50)) as canasta, \n");
			sql.append(" 		ci.importe, ci.interes, ci.isr, i.tasa, i.fecha_alta, ci.plazo, ci.fecha_vence, ci.no_canasta, coalesce(ci.folio_banco, '') as folio_banco \n");
			sql.append(" FROM canasta_inversiones ci, inversion i, control_barrido cb, cat_cta_banco ccb, empresa ec \n");
			sql.append(" WHERE ci.no_canasta = i.no_canasta \n");
			sql.append(" 	And i.no_solicitud = cb.no_solicitud \n");
			sql.append(" 	And cb.id_chequera = ccb.id_chequera \n");
			sql.append(" 	And cb.no_concentradora = ec.no_empresa \n");
			sql.append(" 	And ci.id_estatus = 'K' \n");
			sql.append(" 	And ccb.no_empresa in (select no_empresa from usuario_empresa \n");
			sql.append(" 		where no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + ") \n");
			sql.append(" 	And ccb.id_divisa = '"+ idDivisa +"' \n");
			
			if(!noEmpresa.equals(""))
				sql.append(" 	And cb.no_concentradora = "+ noEmpresa +" \n");
			
			sql.append(" ORDER BY canasta \n");
			
			listCanastas = jdbcTemplate.query(sql.toString(), new RowMapper(){
		    	public CanastaInversionesDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		CanastaInversionesDto dtoBco = new CanastaInversionesDto();
	    			dtoBco.setNoEmpresa(rs.getInt("no_empresa"));
	    			dtoBco.setNomEmpresa(rs.getString("nom_empresa"));
	    			dtoBco.setCanasta(rs.getString("canasta"));
	    			dtoBco.setImporte(rs.getDouble("importe"));
	    			dtoBco.setInteres(rs.getDouble("interes"));
	    			dtoBco.setIsr(rs.getDouble("isr"));
	    			dtoBco.setTasa(rs.getDouble("tasa"));
	    			dtoBco.setFechaAlta(rs.getDate("fecha_alta"));
	    			dtoBco.setDias(rs.getString("plazo"));
	    			dtoBco.setFechaVence(rs.getDate("fecha_vence"));
	    			dtoBco.setNoCanasta(rs.getInt("no_canasta"));
	    			dtoBco.setFolioBanco(rs.getString("folio_banco"));
			    	return dtoBco;
		    	}
		    });
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:obtenerCanastasInv");
		}
		return listCanastas;
	}

	@Override
	public List<CanastaInversionesDto> crearNodosRaizArbol(String noEmpresa, String idDivisa) {
		List<CanastaInversionesDto> listCanastas = new ArrayList<CanastaInversionesDto>();
		globalSingleton = GlobalSingleton.getInstancia();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql = new StringBuffer();
			sql.append(" INSERT INTO tmp_ci \n");
			sql.append(" SELECT distinct e.nom_empresa, ci.importe, ci.interes, ci.isr \n");
			sql.append(" FROM canasta_inversiones ci, inversion i, control_barrido cb, cat_cta_banco ccb, empresa e \n");
			sql.append(" WHERE ci.no_canasta = i.no_canasta \n");
			sql.append(" 	And i.no_solicitud = cb.no_solicitud \n");
			sql.append(" 	And cb.id_chequera = ccb.id_chequera \n");
			sql.append(" 	And cb.no_concentradora = e.no_empresa \n");
			sql.append(" 	And ci.id_estatus = 'K' \n");
			sql.append(" 	And ccb.no_empresa in (select no_empresa from usuario_empresa \n");
			sql.append(" 		where no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + ") \n");
			sql.append(" 	And ccb.id_divisa = '"+ idDivisa +"' \n");
			
			if(!noEmpresa.equals(""))
				sql.append(" 	And cb.no_concentradora = "+ noEmpresa +" \n");
			
			jdbcTemplate.update(sql.toString());
			
			sql = new StringBuffer();
			sql.append(" SELECT nom_empresa, sum(importe) as importe, sum(interes) as interes, sum(isr) as isr \n");
			sql.append(" FROM tmp_ci \n");
			sql.append(" GROUP BY nom_empresa \n");
			
			listCanastas = jdbcTemplate.query(sql.toString(), new RowMapper(){
		    	public CanastaInversionesDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		CanastaInversionesDto dtoBco = new CanastaInversionesDto();
	    			dtoBco.setNomEmpresa(rs.getString("nom_empresa"));
	    			dtoBco.setImporte(rs.getDouble("importe"));
	    			dtoBco.setInteres(rs.getDouble("interes"));
	    			dtoBco.setIsr(rs.getDouble("isr"));
			    	return dtoBco;
		    	}
		    });
			
			sql = new StringBuffer();
			sql.append(" DELETE FROM tmp_ci \n");
			
			jdbcTemplate.update(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:crearNodosRaizArbol");
		}
		return listCanastas;
	}

	@Override
	public List<CanastaInversionesDto> crearNodosArbol(String noEmpresa, String idDivisa) {
		List<CanastaInversionesDto> listCanastas = new ArrayList<CanastaInversionesDto>();
		globalSingleton = GlobalSingleton.getInstancia();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT distinct ec.no_empresa, ec.nom_empresa, 'CANASTA ' + cast(ci.no_canasta as varchar(50)) as canasta, \n");
			sql.append(" 		ci.importe, ci.interes, ci.isr, i.tasa, i.fecha_alta, ci.plazo, ci.fecha_vence, ci.no_canasta \n");
			sql.append(" FROM canasta_inversiones ci, inversion i, control_barrido cb, cat_cta_banco ccb, cat_banco cbc, empresa ec \n");
			sql.append(" WHERE ci.no_canasta = i.no_canasta \n");
			sql.append(" 	And i.no_solicitud = cb.no_solicitud \n");
			sql.append(" 	And cb.id_chequera = ccb.id_chequera \n");
			sql.append(" 	And cb.no_concentradora = ec.no_empresa \n");
			sql.append(" 	And ccb.id_banco = cbc.id_banco \n");
			sql.append(" 	And ci.id_estatus = 'K' \n");
			sql.append(" 	And ccb.no_empresa in (select no_empresa from usuario_empresa \n");
			sql.append(" 		where no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + ") \n");
			sql.append(" 	And ccb.id_divisa = '"+ idDivisa +"' \n");
			
			if(!noEmpresa.equals(""))
				sql.append(" 	And cb.no_concentradora = "+ noEmpresa +" \n");
			
			sql.append(" ORDER BY canasta \n");
			
			listCanastas = jdbcTemplate.query(sql.toString(), new RowMapper(){
		    	public CanastaInversionesDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		CanastaInversionesDto dtoBco = new CanastaInversionesDto();
	    			dtoBco.setNoEmpresa(rs.getInt("no_empresa"));
	    			dtoBco.setNomEmpresa(rs.getString("nom_empresa"));
	    			dtoBco.setCanasta(rs.getString("canasta"));
	    			dtoBco.setImporte(rs.getDouble("importe"));
	    			dtoBco.setInteres(rs.getDouble("interes"));
	    			dtoBco.setIsr(rs.getDouble("isr"));
	    			dtoBco.setTasa(rs.getDouble("tasa"));
	    			dtoBco.setFechaAlta(rs.getDate("fecha_alta"));
	    			dtoBco.setDias(rs.getString("plazo"));
	    			dtoBco.setFechaVence(rs.getDate("fecha_vence"));
	    			dtoBco.setNoCanasta(rs.getInt("no_canasta"));
			    	return dtoBco;
		    	}
		    });
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:crearNodosArbol");
		}
		return listCanastas;
	}

	@Override
	public List<CanastaInversionesDto> crearNodosHijosArbol(int noCanasta) {
		List<CanastaInversionesDto> listCanastas = new ArrayList<CanastaInversionesDto>();
		globalSingleton = GlobalSingleton.getInstancia();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT i.no_orden, cgf.desc_grupo_flujo, cb.importe, i.interes, i.isr, \n");
			sql.append(" 		i.fecha_vence, cbc.desc_banco, cb.id_chequera \n");
			sql.append(" FROM inversion i, control_barrido cb, cat_cta_banco ccb, empresa e, \n");
			sql.append(" 		cat_banco cbc, cat_grupo_flujo cgf \n");
			sql.append(" WHERE i.no_solicitud = cb.no_solicitud \n");
			sql.append(" 	And cb.id_chequera = ccb.id_chequera \n");
			sql.append(" 	And ccb.no_empresa = e.no_empresa \n");
			sql.append(" 	And ccb.id_banco = cbc.id_banco \n");
			sql.append(" 	And e.no_empresa = cgf.id_grupo_flujo \n");
			sql.append(" 	And no_canasta = "+ noCanasta +" \n");
			sql.append(" 	And i.id_estatus = 'K' \n");
			sql.append(" ORDER BY cgf.desc_grupo_flujo \n");
			
			listCanastas = jdbcTemplate.query(sql.toString(), new RowMapper(){
		    	public CanastaInversionesDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		CanastaInversionesDto dtoBco = new CanastaInversionesDto();
	    			dtoBco.setNoOrden(rs.getInt("no_orden"));
	    			dtoBco.setNomEmpresa(rs.getString("desc_grupo_flujo"));
	    			dtoBco.setImporte(rs.getDouble("importe"));
	    			dtoBco.setInteres(rs.getDouble("interes"));
	    			dtoBco.setIsr(rs.getDouble("isr"));
	    			dtoBco.setFechaVence(rs.getDate("fecha_vence"));
	    			dtoBco.setNomBanco(rs.getString("desc_banco"));
	    			dtoBco.setIdChequera(rs.getString("id_chequera"));
			    	return dtoBco;
		    	}
		    });
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:crearNodosHijosArbol");
		}
		return listCanastas;
	}

	@Override
	public int actualizaTasaReal(int noCanasta, String tasaReal) {
		StringBuffer sql = new StringBuffer();
		int result = 0;
		
		try {
			sql.append(" UPDATE inversion \n");
			sql.append(" 	SET interes = ROUND((((a.importe / 360) * ("+ tasaReal +"/100)) * a.plazo), 2), \n");
			sql.append(" 	isr = ROUND((a.importe * (SELECT (((tasa_isr/365) * 360) / 100) / 360 FROM retencion) * a.plazo), 2), \n");
			sql.append(" 	tasa = "+ tasaReal +" \n");
			sql.append(" FROM inversion i, control_barrido a \n");
			sql.append(" WHERE i.no_solicitud = a.no_solicitud \n");
			sql.append(" 	And i.no_canasta = "+ noCanasta +" \n");
						
			result = jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:actualizaTasaReal");
		}
		return result;
	}

	@Override
	public int actualizaCanastaIntIsr(int noCanasta) {
		StringBuffer sql = new StringBuffer();
		int result = 0;
		
		try {
			sql.append(" UPDATE canasta_inversiones \n");
			sql.append(" 	SET interes = (SELECT SUM(i.interes) FROM inversion i WHERE i.no_canasta = ci.no_canasta), \n");
			sql.append(" 		isr = (SELECT SUM(i.isr) FROM inversion i WHERE i.no_canasta = ci.no_canasta) \n");
			sql.append(" FROM canasta_inversiones ci \n");
			sql.append(" WHERE ci.no_canasta = "+ noCanasta +" \n");
						
			result = jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:actualizaCanastaIntIsr");
		}
		return result;
	}

	@Override
	public Map vencimientoCanastas(int noCanasta, int noEmpresa) {
		globalSingleton = GlobalSingleton.getInstancia();
		Map resul = new HashMap();
		HashMap< Object, Object > inParams = new HashMap< Object, Object >();
		
		try {
			StoredProcedure stored = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_ci_vencimiento_canastas") {};
			stored.declareParameter(new SqlParameter("no_empresa", Types.INTEGER));
			stored.declareParameter(new SqlParameter("no_canasta", Types.INTEGER));
			stored.declareParameter(new SqlParameter("usuario", Types.INTEGER));
			stored.declareParameter(new SqlInOutParameter("result", Types.INTEGER));
			stored.declareParameter(new SqlInOutParameter("mensaje", Types.VARCHAR));
			
			inParams.put("no_empresa", noEmpresa);
			inParams.put("no_canasta", noCanasta);
			inParams.put("usuario", globalSingleton.getUsuarioLoginDto().getIdUsuario());
			inParams.put("result", 0);
			inParams.put("mensaje", "");
			
			resul = stored.execute((Map)inParams);
			
			}catch (Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
				"P:Inversiones, C:BarridoInversionDaoImpl, M:armaCanastas");
		}
		return resul;
	}

	@Override
	public Map<String, Object> regresoCanastas(int noCanasta, int noEmpresa) {
		globalSingleton = GlobalSingleton.getInstancia();
		Map resul = new HashMap();
		HashMap< Object, Object > inParams = new HashMap< Object, Object >();
		
		try {
			StoredProcedure stored = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_ci_fondeo_automatico") {};
			stored.declareParameter(new SqlParameter("no_canasta", Types.INTEGER));
			stored.declareParameter(new SqlParameter("usuario", Types.INTEGER));
			stored.declareParameter(new SqlInOutParameter("result", Types.INTEGER));
			stored.declareParameter(new SqlInOutParameter("mensaje", Types.VARCHAR));
			
			inParams.put("no_canasta", noCanasta);
			inParams.put("usuario", globalSingleton.getUsuarioLoginDto().getIdUsuario());
			inParams.put("result", 0);
			inParams.put("mensaje", "");
			
			resul = stored.execute((Map)inParams);
			
			}catch (Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
				"P:Inversiones, C:BarridoInversionDaoImpl, M:regresoCanastas");
		}
		return resul;
	}

	@Override
	public int actualizaCanastaCodigo(int noCanasta, String codigo) {
		StringBuffer sql = new StringBuffer();
		int result = 0;
		
		try {
			sql.append(" UPDATE canasta_inversiones \n");
			sql.append(" 	SET folio_banco = '"+ codigo +"' \n");
			sql.append(" WHERE no_canasta = "+ noCanasta +" \n");
			sql.append(" 	And id_estatus = 'K' \n");
			
			result = jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:actualizaCanastaCodigo");
		}
		return result;
	}

	@Override
	public List<CanastaInversionesDto> detalleEmpresas(int noCanasta) {
		List<CanastaInversionesDto> listCanastas = new ArrayList<CanastaInversionesDto>();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT ccb.no_empresa, i.no_orden \n");
			sql.append(" FROM cat_cta_banco ccb, control_barrido cb, inversion i \n");
			sql.append(" WHERE ccb.id_chequera = cb.id_chequera \n");
			sql.append("	And cb.no_solicitud = i.no_solicitud \n");
			sql.append(" 	And i.no_canasta = "+ noCanasta +" \n");
			sql.append(" ORDER BY ccb.no_empresa \n");
			
			listCanastas = jdbcTemplate.query(sql.toString(), new RowMapper(){
		    	public CanastaInversionesDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		CanastaInversionesDto dtoBco = new CanastaInversionesDto();
	    			dtoBco.setNoEmpresa(rs.getInt("no_empresa"));
	    			dtoBco.setNoOrden(rs.getInt("no_orden"));
	    			return dtoBco;
		    	}
		    });
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:detalleEmpresas");
		}
		return listCanastas;
	}

	@Override
	public int actualizaDetalleCanastacodigo(int noOrden, String codigoDet) {
		StringBuffer sql = new StringBuffer();
		int result = 0;
		
		try {
			sql.append(" UPDATE inversion \n");
			sql.append(" 	SET folio_banco = '"+ codigoDet +"' \n");
			sql.append(" WHERE no_orden = "+ noOrden +" \n");
			sql.append(" 	And id_estatus = 'K' \n");
			
			result = jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:actualizaDetalleCanastacodigo");
		}
		return result;
	}

	@Override
	public String configuraSet(int indice) {
		consultasGenerales = new ConsultasGenerales (jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
	}

	@Override
	public List<MovimientoDto> buscaMovimientosTraspasos() {
		List<MovimientoDto> resultado = null;
		StringBuffer sb = new StringBuffer();
		
		try {
		    sb.append(" SELECT m.cve_control, m.origen_mov, oper_may_esp, ind_iva, division, m.id_rubro, m.no_cuenta,");
		    sb.append("\n        m.no_empresa, m.valor_tasa, m.id_tipo_docto, m.no_docto, m.referencia, m.id_banco_benef,");
		    sb.append("\n        m.no_folio_mov, m.id_divisa, m.id_chequera_benef, m.id_banco, b.inst_finan, m.id_chequera,");
		    sb.append("\n        m.id_tipo_operacion, m.sucursal, m.importe, m.beneficiario, m.lote_entrada, m.plaza,");
		    sb.append("\n        m.no_folio_mov, m.no_folio_det, m.concepto, m.fec_valor, ccb.id_clabe, '0' as sucDestino, m.no_cliente ");
		    sb.append("\n  FROM movimiento  m, cat_banco b, cat_cta_banco ccb, control_barrido cb ");
		    sb.append("\n  WHERE m.id_tipo_movto = 'E' and not folio_ref is null ");
		    sb.append("\n    AND m.id_banco_benef = b.id_banco And m.id_chequera_benef = ccb.id_chequera");
		    sb.append("\n    And id_tipo_operacion = 3701 ");
    		sb.append("\n    And m.no_docto = cb.no_solicitud ");
    		sb.append("\n    And m.no_cliente = 1001 ");
    		sb.append("\n    And cb.id_estatus = 'A' ");
    		sb.append("\n  ORDER BY m.id_banco ");
    			
    		resultado = (List<MovimientoDto>)jdbcTemplate.query(sb.toString(), new RowMapper(){
    			public Object mapRow(ResultSet rs, int i) throws SQLException {
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
	        		cons.setBeneficiario(rs.getString("beneficiario"));
	        		cons.setLoteEntrada(rs.getInt("lote_entrada"));
	        		cons.setPlaza(rs.getInt("plaza"));
	        		cons.setNoFolioMov(rs.getInt("no_folio_mov"));
	        		cons.setNoFolioDet(rs.getInt("no_folio_det"));
	        		cons.setConcepto(rs.getString("concepto"));
	        		cons.setFecValor(rs.getDate("fec_valor"));
	        		cons.setClabeBenef(rs.getString("id_clabe"));
	        		cons.setSucDestino(rs.getString("sucDestino"));
	        		cons.setNoCliente(rs.getString("no_cliente"));
	        		return cons;
			}});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:buscaMovimientosTraspasos");
		}
		return resultado;
	}

	public List<MovimientoDto>seleccionarDetalleArchivo(String folios){
		return traspasosDao.seleccionarDetalleArchivo(folios);
	}
	
	public int insertarDetArchTransfer(MovimientoDto parametro, String psNomArch) {
		return traspasosDao.insertarDetArchTransfer(parametro, psNomArch);
	}
	
	public int insertarArchTransfer(String psNomArch, int banco, Date fecha, double total, int registros, int usuario) {
		return traspasosDao.insertarArchTransfer(psNomArch, banco, fecha, total, registros, usuario);
	}
	
	public int actualizarMovimientoTipoEnvio(String psTipoEnvio, String psNomArchivos, boolean pbMasspayment) {
		return traspasosDao.actualizarMovimientoTipoEnvio(psTipoEnvio, psNomArchivos, pbMasspayment);
	}

	@Override
	public int cambiaEstCtrlBarrido(String noSolicitudes) {
		StringBuffer sql = new StringBuffer();
		int result = 0;
		
		try {
			sql.append(" UPDATE control_barrido \n");
			sql.append(" 	SET id_estatus = 'T' \n");
			sql.append(" WHERE id_estatus = 'A' \n");
//			sql.append(" 	And no_solicitud in ("+ noSolicitudes +") \n");
			
			result = jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:cambiaEstCtrlBarrido");
		}
		return result;
	}
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++       REPORTES DE INVERSION        ++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	
	
	@Override
	public List<Map<String, Object>> reportesBarridos(Map datos) {
		List<Map<String, Object>> listRegistros = new ArrayList<Map<String, Object>>();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT crb.no_solicitud, ccb.no_empresa, e.nom_empresa, ccb.id_banco, cb.desc_banco, \n");
			sql.append(" 		crb.id_chequera, crb.importe, crb.no_concentradora, eb.nom_empresa as nom_empresa_benef, \n");
			sql.append(" 		ccbb.id_banco as id_banco_benef, cbb.desc_banco as desc_banco_benef, \n");
			sql.append(" 		ccbb.id_chequera as id_chequera_benef, crb.fecha_solicitud, \n");
			sql.append(" 		cd.desc_divisa, crb.concepto, coalesce(su.clave_usuario, '') as clave_usuario, \n");
			sql.append(" 		desc_estatus \n");
			sql.append(" FROM control_barrido crb left join seg_usuario su on (crb.id_usuario_autoriza = su.id_usuario ), \n");
			sql.append(" 		cat_cta_banco ccb, empresa e, cat_banco cb, empresa eb, cat_cta_banco ccbb, cat_banco cbb, \n");
			sql.append(" 		cat_divisa cd, cat_estatus ce \n");
			sql.append(" WHERE crb.id_chequera = ccb.id_chequera \n");
			sql.append(" 	And ccb.no_empresa = e.no_empresa \n");
			sql.append(" 	And ccb.id_banco = cb.id_banco \n");
			sql.append(" 	And crb.no_concentradora = eb.no_empresa \n");
			sql.append(" 	And crb.no_concentradora = ccbb.no_empresa \n");
			sql.append(" 	And ccbb.id_banco = cbb.id_banco \n");
			sql.append(" 	And ccb.id_divisa = cd.id_divisa \n");
			sql.append(" 	And crb.id_estatus = ce.id_estatus \n");
			sql.append(" 	And ce.clasificacion = 'INV' \n");
			
			if(!datos.get("estatus").toString().equals(""))
				sql.append(" 	And crb.id_estatus = '"+ datos.get("estatus").toString() +"' \n");
			else
				sql.append(" 	And crb.id_estatus not in ('X', 'P') \n");
			sql.append(" 	And ccb.id_divisa = '"+ datos.get("idDivisa").toString() +"' \n");
			sql.append(" 	And ccb.no_empresa in (select no_empresa from usuario_empresa \n");
			sql.append(" 		where no_usuario = " + Integer.parseInt(datos.get("usuario").toString()) + ") \n");
			
			if(!datos.get("tipoRep").toString().equals("1"))
				sql.append(" 	And crb.fecha_solicitud between '"+ datos.get("pFecIni").toString() +"' and '"+ datos.get("pFecFin").toString() +"' \n");
			else if(datos.get("tipoRep").toString().equals("1")) {
				sql.append(" 	And crb.id_estatus = 'K' \n");
				sql.append(" 	And crb.no_solicitud in ("+ datos.get("solic").toString() +") \n");
			}
				
			if(!datos.get("noEmpresa").toString().equals(""))
				sql.append(" And crb.no_concentradora = "+ datos.get("noEmpresa").toString() +" \n");
			
			sql.append(" ORDER BY e.nom_empresa \n");
			
			//System.out.println("query de reportes: " + sql.toString());
			
			listRegistros = jdbcTemplate.query(sql.toString(), new RowMapper(){
		    	public Map mapRow(ResultSet rs, int idx) throws SQLException{
		    		Map map = new HashMap();
	    			map.put("nom_empresa", rs.getString("nom_empresa"));
	    			map.put("desc_banco", rs.getString("desc_banco"));
	    			map.put("id_chequera", rs.getString("id_chequera"));
	    			map.put("importe", rs.getDouble("importe"));
	    			map.put("nom_empresa_benef", rs.getString("nom_empresa_benef"));
	    			map.put("desc_banco_benef", rs.getString("desc_banco_benef"));
	    			map.put("id_chequera_benef", rs.getString("id_chequera_benef"));
	    			map.put("fecha_solicitud", rs.getDate("fecha_solicitud"));
	    			map.put("desc_divisa", rs.getString("desc_divisa"));
	    			map.put("concepto", rs.getString("concepto"));
	    			map.put("clave_usuario", rs.getString("clave_usuario"));
		    		return map;
		    	}
		    });
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:reportesBarridos");
		}
		return listRegistros;
	}

	@Override
	public List<Map<String, Object>> reporteCanastasPendientes(Map datos) {
		List<Map<String, Object>> listRegistros = new ArrayList<Map<String, Object>>();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT (SELECT TOP 1 m.nom_empresa \n");
			sql.append("		   FROM empresa m \n");
			sql.append("		  WHERE m.no_empresa = c.no_concentradora) AS nomConcentradora, \n");
			sql.append("		(SELECT TOP 1 o.desc_banco \n");
			sql.append("		   FROM cat_banco o \n");
			sql.append("		  WHERE o.id_banco IN (SELECT TOP 1 n.id_banco \n");
			sql.append("							     FROM cat_cta_banco n \n");
			sql.append("								WHERE n.no_empresa = c.no_concentradora \n");
			sql.append("								  AND n.tipo_chequera = 'C' \n");
			sql.append("								  AND n.id_estatus_cta = 'A')) AS descBanco, \n");
			sql.append("		(SELECT TOP 1 p.id_chequera \n");
			sql.append("		   FROM cat_cta_banco p \n");
			sql.append("		  WHERE p.no_empresa = c.no_concentradora \n");
			sql.append("		    AND p.tipo_chequera = 'C' \n");
			sql.append("			AND p.id_estatus_cta = 'A') AS idChequera, \n");			
			sql.append("		a.no_canasta, SUBSTRING(RTRIM(LTRIM(CONVERT(CHAR, c.fecha_solicitud, 3))), 1, 6) + CAST(YEAR(c.fecha_solicitud) AS VARCHAR) AS fecha_solicitud, \n");
			sql.append("	    a.plazo, (CAST(CAST(b.tasa AS DECIMAL(4,2)) AS VARCHAR) + '%') AS tasa, \n");
			sql.append("	    SUBSTRING(RTRIM(LTRIM(CONVERT(CHAR, a.fecha_vence, 3))), 1, 6) + CAST(YEAR(a.fecha_vence) AS VARCHAR) AS fecha_vence, \n");
			sql.append(" 	   (SELECT h.no_empresa \n");
			sql.append(" 		 FROM cat_cta_banco h \n");
			sql.append(" 		 WHERE h.id_chequera = c.id_chequera) AS no_empresa, \n");
			sql.append(" 	   (SELECT TOP 1 j.nom_empresa \n");
			sql.append(" 		 FROM empresa j \n");
			sql.append(" 		 WHERE j.no_empresa IN (SELECT TOP 1 i.no_empresa \n");
			sql.append(" 								  FROM cat_cta_banco i \n");
			sql.append(" 								 WHERE i.id_chequera = c.id_chequera)) AS nom_empresa, \n");	   
			sql.append(" 	   (SELECT TOP 1 l.desc_banco \n");
			sql.append(" 		 FROM cat_banco l \n");
			sql.append(" 		 WHERE l.id_banco IN (SELECT TOP 1 k.id_banco \n");
			sql.append(" 								FROM cat_cta_banco k \n");
			sql.append(" 							   WHERE k.id_chequera = c.id_chequera)) AS nom_banco, \n");
			sql.append(" 	   c.id_chequera, c.importe, b.interes, b.isr, (b.interes - b.isr) as ganancia \n");
			sql.append(" FROM canasta_inversiones a, inversion b, control_barrido c, empresa d \n");
			sql.append(" WHERE c.no_concentradora = " + datos.get("noConcentradora") + " \n");
			sql.append(" AND a.id_estatus = 'K' \n");
			sql.append(" AND a.id_estatus = 'K' \n");
			sql.append(" AND a.id_estatus = 'K' \n");
			
			if(datos.get("solic").toString().equals(""))
				sql.append(" AND c.fecha_solicitud BETWEEN '"+ datos.get("pFecIni").toString() +"' and '"+ datos.get("pFecFin").toString() +"' \n");
			else
				sql.append(" And a.no_canasta in (select no_canasta from inversion where no_solicitud in ("+ datos.get("solic").toString() +") ) \n");
			
			sql.append(" AND a.no_canasta = b.no_canasta \n");
			sql.append(" AND b.no_solicitud = c.no_solicitud \n");
			sql.append(" AND d.no_empresa = c.no_concentradora \n");
			sql.append(" ORDER BY a.no_canasta, 9 \n");
			
			listRegistros = jdbcTemplate.query(sql.toString(), new RowMapper(){
		    	public Map mapRow(ResultSet rs, int idx) throws SQLException{
		    		
		    		Map map = new HashMap();
		    		map.put("nomConcentradora", rs.getString("nomConcentradora"));		    		
		    		map.put("descBanco", rs.getString("descBanco"));
		    		map.put("idChequera", rs.getString("idChequera"));		    		
	    			map.put("no_canasta", rs.getString("no_canasta"));
	    			map.put("fecha_solicitud", rs.getString("fecha_solicitud"));
	    			map.put("plazo", rs.getString("plazo"));
	    			map.put("tasa", rs.getString("tasa"));
	    			map.put("fecha_vence", rs.getString("fecha_vence"));
	    			map.put("no_empresa", rs.getInt("no_empresa"));
	    			map.put("nom_empresa", rs.getString("nom_empresa"));
	    			map.put("nom_banco", rs.getString("nom_banco"));
	    			map.put("id_chequera", rs.getString("id_chequera"));
	    			map.put("importe", rs.getDouble("importe"));
	    			map.put("interes", rs.getDouble("interes"));
	    			map.put("isr", rs.getDouble("isr"));
	    			map.put("ganancia", rs.getDouble("ganancia"));
		    		return map;
		    	}
		    });
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:reporteCanastasVencimientos");
		}
		return listRegistros;
	}	
	
	@Override
	public List<Map<String, Object>> reporteCanastasVencenHoy(Map datos) {
		List<Map<String, Object>> listRegistros = new ArrayList<Map<String, Object>>();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT (SELECT TOP 1 m.nom_empresa \n");
			sql.append("		   FROM empresa m \n");
			sql.append("		  WHERE m.no_empresa = c.no_concentradora) AS nomConcentradora, \n");
			sql.append("		(SELECT TOP 1 o.desc_banco \n");
			sql.append("		   FROM cat_banco o \n");
			sql.append("		  WHERE o.id_banco IN (SELECT TOP 1 n.id_banco \n");
			sql.append("							     FROM cat_cta_banco n \n");
			sql.append("								WHERE n.no_empresa = c.no_concentradora \n");
			sql.append("								  AND n.tipo_chequera = 'C' \n");
			sql.append("								  AND n.id_estatus_cta = 'A')) AS descBanco, \n");
			sql.append("		(SELECT TOP 1 p.id_chequera \n");
			sql.append("		   FROM cat_cta_banco p \n");
			sql.append("		  WHERE p.no_empresa = c.no_concentradora \n");
			sql.append("		    AND p.tipo_chequera = 'C' \n");
			sql.append("			AND p.id_estatus_cta = 'A') AS idChequera, \n");			
			sql.append("		a.no_canasta, SUBSTRING(RTRIM(LTRIM(CONVERT(CHAR, c.fecha_solicitud, 3))), 1, 6) + CAST(YEAR(c.fecha_solicitud) AS VARCHAR) AS fecha_solicitud, \n");
			sql.append("	    a.plazo, (CAST(CAST(b.tasa AS DECIMAL(4,2)) AS VARCHAR) + '%') AS tasa, \n");
			sql.append("	    SUBSTRING(RTRIM(LTRIM(CONVERT(CHAR, a.fecha_vence, 3))), 1, 6) + CAST(YEAR(a.fecha_vence) AS VARCHAR) AS fecha_vence, \n");
			sql.append(" 	   (SELECT h.no_empresa \n");
			sql.append(" 		 FROM cat_cta_banco h \n");
			sql.append(" 		 WHERE h.id_chequera = c.id_chequera) AS no_empresa, \n");
			sql.append(" 	   (SELECT TOP 1 j.nom_empresa \n");
			sql.append(" 		 FROM empresa j \n");
			sql.append(" 		 WHERE j.no_empresa IN (SELECT TOP 1 i.no_empresa \n");
			sql.append(" 								  FROM cat_cta_banco i \n");
			sql.append(" 								 WHERE i.id_chequera = c.id_chequera)) AS nom_empresa, \n");	   
			sql.append(" 	   (SELECT TOP 1 l.desc_banco \n");
			sql.append(" 		 FROM cat_banco l \n");
			sql.append(" 		 WHERE l.id_banco IN (SELECT TOP 1 k.id_banco \n");
			sql.append(" 								FROM cat_cta_banco k \n");
			sql.append(" 							   WHERE k.id_chequera = c.id_chequera)) AS nom_banco, \n");
			sql.append(" 	   c.id_chequera, c.importe, b.interes, b.isr, (b.interes - b.isr) as ganancia, \n");
			sql.append(" 	   coalesce(a.folio_banco, '') as folio_banco, \n");
			sql.append(" 	   coalesce(b.folio_banco, '') as folio_banco_inv \n");
			sql.append(" FROM canasta_inversiones a, inversion b, control_barrido c, empresa d \n");
			sql.append(" WHERE c.no_concentradora = " + datos.get("noConcentradora") + " \n");
			sql.append(" AND a.id_estatus = 'K' \n");
			sql.append(" AND a.id_estatus = 'K' \n");
			sql.append(" AND a.id_estatus = 'K' \n");
			SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
			datos.remove("pFecFin");
			datos.put("pFecFin", formateador.format(obtenerFechaHoy()));
			datos.remove("pFecIni");
			datos.put("pFecIni", formateador.format(obtenerFechaHoy()));
			sql.append(" AND a.fecha_vence BETWEEN '"+formateador.format(obtenerFechaHoy())+"' and '"+formateador.format(obtenerFechaHoy())+"' \n");
			sql.append(" AND a.no_canasta = b.no_canasta \n");
			sql.append(" AND b.no_solicitud = c.no_solicitud \n");
			sql.append(" AND d.no_empresa = c.no_concentradora \n");
			sql.append(" ORDER BY a.no_canasta, 9 \n");
			
			listRegistros = jdbcTemplate.query(sql.toString(), new RowMapper(){
		    	public Map mapRow(ResultSet rs, int idx) throws SQLException{
		    		
		    		Map map = new HashMap();
		    		map.put("nomConcentradora", rs.getString("nomConcentradora"));		    		
		    		map.put("descBanco", rs.getString("descBanco"));
		    		map.put("idChequera", rs.getString("idChequera"));		    		
	    			map.put("no_canasta", rs.getString("no_canasta"));
	    			map.put("fecha_solicitud", rs.getString("fecha_solicitud"));
	    			map.put("plazo", rs.getString("plazo"));
	    			map.put("tasa", rs.getString("tasa"));
	    			map.put("fecha_vence", rs.getString("fecha_vence"));
	    			map.put("no_empresa", rs.getInt("no_empresa"));
	    			map.put("nom_empresa", rs.getString("nom_empresa"));
	    			map.put("nom_banco", rs.getString("nom_banco"));
	    			map.put("id_chequera", rs.getString("id_chequera"));
	    			map.put("importe", rs.getDouble("importe"));
	    			map.put("interes", rs.getDouble("interes"));
	    			map.put("isr", rs.getDouble("isr"));
	    			map.put("ganancia", rs.getDouble("ganancia"));
	    			map.put("folio_banco", rs.getString("folio_banco"));
	    			map.put("folio_banco_inv", rs.getString("folio_banco_inv"));
		    		return map;
		    	}
		    });
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:reporteCanastasVencimientos");
		}
		return listRegistros;
	}

	@Override
	public List<Map<String, Object>> reporteCanastasVencimientos(Map datos) {
		List<Map<String, Object>> listRegistros = new ArrayList<Map<String, Object>>();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT (SELECT TOP 1 m.nom_empresa \n");
			sql.append("		   FROM empresa m \n");
			sql.append("		  WHERE m.no_empresa = c.no_concentradora) AS nomConcentradora, \n");
			sql.append("		(SELECT TOP 1 o.desc_banco \n");
			sql.append("		   FROM cat_banco o \n");
			sql.append("		  WHERE o.id_banco IN (SELECT TOP 1 n.id_banco \n");
			sql.append("							     FROM cat_cta_banco n \n");
			sql.append("								WHERE n.no_empresa = c.no_concentradora \n");
			sql.append("								  AND n.tipo_chequera = 'C' \n");
			sql.append("								  AND n.id_estatus_cta = 'A')) AS descBanco, \n");
			sql.append("		(SELECT TOP 1 p.id_chequera \n");
			sql.append("		   FROM cat_cta_banco p \n");
			sql.append("		  WHERE p.no_empresa = c.no_concentradora \n");
			sql.append("		    AND p.tipo_chequera = 'C' \n");
			sql.append("			AND p.id_estatus_cta = 'A') AS idChequera, \n");			
			sql.append("		a.no_canasta, SUBSTRING(RTRIM(LTRIM(CONVERT(CHAR, c.fecha_solicitud, 3))), 1, 6) + CAST(YEAR(c.fecha_solicitud) AS VARCHAR) AS fecha_solicitud, \n");
			sql.append("	    a.plazo, (CAST(CAST(b.tasa AS DECIMAL(4,2)) AS VARCHAR) + '%') AS tasa, \n");
			sql.append("	    SUBSTRING(RTRIM(LTRIM(CONVERT(CHAR, a.fecha_vence, 3))), 1, 6) + CAST(YEAR(a.fecha_vence) AS VARCHAR) AS fecha_vence, \n");
			sql.append(" 	   (SELECT h.no_empresa \n");
			sql.append(" 		 FROM cat_cta_banco h \n");
			sql.append(" 		 WHERE h.id_chequera = c.id_chequera) AS no_empresa, \n");
			sql.append(" 	   (SELECT TOP 1 j.nom_empresa \n");
			sql.append(" 		 FROM empresa j \n");
			sql.append(" 		 WHERE j.no_empresa IN (SELECT TOP 1 i.no_empresa \n");
			sql.append(" 								  FROM cat_cta_banco i \n");
			sql.append(" 								 WHERE i.id_chequera = c.id_chequera)) AS nom_empresa, \n");	   
			sql.append(" 	   (SELECT TOP 1 l.desc_banco \n");
			sql.append(" 		 FROM cat_banco l \n");
			sql.append(" 		 WHERE l.id_banco IN (SELECT TOP 1 k.id_banco \n");
			sql.append(" 								FROM cat_cta_banco k \n");
			sql.append(" 							   WHERE k.id_chequera = c.id_chequera)) AS nom_banco, \n");
			sql.append(" 	   c.id_chequera, c.importe, b.interes, b.isr, (b.interes - b.isr) as ganancia \n");
			sql.append(" FROM canasta_inversiones a, inversion b, control_barrido c, empresa d \n");
			sql.append(" WHERE c.no_concentradora = " + datos.get("noConcentradora") + " \n");
			sql.append(" AND a.id_estatus = 'V' \n");
			sql.append(" AND a.id_estatus = 'V' \n");
			sql.append(" AND a.id_estatus = 'V' \n");
			sql.append(" AND a.fecha_vence BETWEEN '"+ datos.get("FECHA_INI").toString() +"' and '"+ datos.get("FECHA_FIN").toString() +"' \n");
			sql.append(" AND a.no_canasta = b.no_canasta \n");
			sql.append(" AND b.no_solicitud = c.no_solicitud \n");
			sql.append(" AND d.no_empresa = c.no_concentradora \n");
			sql.append(" ORDER BY a.no_canasta, 9 \n");
			
			listRegistros = jdbcTemplate.query(sql.toString(), new RowMapper(){
		    	public Map mapRow(ResultSet rs, int idx) throws SQLException{
		    		
		    		Map map = new HashMap();
		    		map.put("nomConcentradora", rs.getString("nomConcentradora"));		    		
		    		map.put("descBanco", rs.getString("descBanco"));
		    		map.put("idChequera", rs.getString("idChequera"));		    		
	    			map.put("no_canasta", rs.getString("no_canasta"));
	    			map.put("fecha_solicitud", rs.getString("fecha_solicitud"));
	    			map.put("plazo", rs.getString("plazo"));
	    			map.put("tasa", rs.getString("tasa"));
	    			map.put("fecha_vence", rs.getString("fecha_vence"));
	    			map.put("no_empresa", rs.getInt("no_empresa"));
	    			map.put("nom_empresa", rs.getString("nom_empresa"));
	    			map.put("nom_banco", rs.getString("nom_banco"));
	    			map.put("id_chequera", rs.getString("id_chequera"));
	    			map.put("importe", rs.getDouble("importe"));
	    			map.put("interes", rs.getDouble("interes"));
	    			map.put("isr", rs.getDouble("isr"));
	    			map.put("ganancia", rs.getDouble("ganancia"));
		    		return map;
		    	}
		    });
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:reporteCanastasPendientes");
		}
		return listRegistros;
	}	
	
	@Override
	public List<Map<String, Object>> InversionesDetalladas(Map datos) {
		List<Map<String, Object>> listRegistros = new ArrayList<Map<String, Object>>();
		globalSingleton = GlobalSingleton.getInstancia();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT crb.no_solicitud, ccb.no_empresa, e.nom_empresa, ccb.id_banco, cb.desc_banco, \n");
			sql.append(" 		crb.id_chequera, crb.importe, crb.no_concentradora, eb.nom_empresa as nom_empresa_benef, \n");
			sql.append(" 		ccbb.id_banco as id_banco_benef, cbb.desc_banco as desc_banco_benef, \n");
			sql.append(" 		ccbb.id_chequera as id_chequera_benef, crb.fecha_solicitud, \n");
			sql.append(" 		cd.desc_divisa, crb.concepto, coalesce(su.clave_usuario, '') as clave_usuario, \n");
			sql.append(" 		case when crb.id_usuario_autoriza = 0 then 'color:#000000' else 'color:#04A861' end as color \n");
			sql.append(" FROM control_barrido crb left join seg_usuario su on (crb.id_usuario_autoriza = su.id_usuario ), \n");
			sql.append(" 		cat_cta_banco ccb, empresa e, cat_banco cb, empresa eb, cat_cta_banco ccbb, cat_banco cbb, \n");
			sql.append(" 		cat_divisa cd \n");
			sql.append(" WHERE crb.id_chequera = ccb.id_chequera \n");
			sql.append(" 	And ccb.no_empresa = e.no_empresa \n");
			sql.append(" 	And ccb.id_banco = cb.id_banco \n");
			sql.append(" 	And crb.no_concentradora = eb.no_empresa \n");
			sql.append(" 	And crb.no_concentradora = ccbb.no_empresa \n");
			sql.append(" 	And ccbb.id_banco = cbb.id_banco \n");
			sql.append(" 	And ccb.id_divisa = cd.id_divisa \n");
			sql.append(" 	And crb.id_estatus = 'P' \n");
			sql.append(" 	And ccb.id_divisa = '"+ datos.get("idDivisa").toString() +"' \n");
			sql.append(" 	And ccb.no_empresa in (select no_empresa from usuario_empresa \n");
			sql.append(" 		where no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + ") \n");
			
			if(!datos.get("noEmpresa").toString().equals(""))
				sql.append(" And ccb.no_empresa = "+ datos.get("noEmpresa").toString() +" \n");
			
			sql.append(" ORDER BY e.nom_empresa \n");
			
			listRegistros = jdbcTemplate.query(sql.toString(), new RowMapper(){
		    	public Map mapRow(ResultSet rs, int idx) throws SQLException{
		    		Map map = new HashMap();
	    			map.put("", rs.getString("nom_empresa"));
	    			map.put("", rs.getString("desc_banco"));
	    			map.put("", rs.getString("id_chequera"));
	    			map.put("", rs.getDouble("importe"));
	    			map.put("", rs.getString("nom_empresa_benef"));
	    			map.put("", rs.getString("desc_banco_benef"));
	    			map.put("", rs.getString("id_chequera_benef"));
	    			map.put("", rs.getDate("fecha_solicitud"));
	    			map.put("", rs.getString("desc_divisa"));
	    			map.put("", rs.getString("concepto"));
	    			map.put("", rs.getString("clave_usuario"));
		    		return map;
		    	}
		    });
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:InversionesDetalladas");
		}
		return listRegistros;
	}

	@Override
	public List<CanastaInversionesDto> consultarDetalleCanasta(int canasta) {
		List<CanastaInversionesDto> resultado = null;
		StringBuffer sb = new StringBuffer();
		
		try {
		    sb.append(" SELECT e.nom_empresa, c.desc_banco, cb.id_chequera, cb.importe, i.interes, i.isr, coalesce(i.folio_banco, '') as folio_banco \n");
		    sb.append(" FROM control_barrido cb, inversion i, canasta_inversiones ci, cat_cta_banco ccb, empresa e, cat_banco c \n");
		    sb.append(" WHERE cb.no_solicitud = i.no_solicitud \n");
		    sb.append(" 	And i.no_canasta = ci.no_canasta ");
		    sb.append(" 	And cb.id_chequera = ccb.id_chequera ");
		    sb.append(" 	And ccb.no_empresa = e.no_empresa ");
		    sb.append(" 	And ccb.id_banco = c.id_banco ");
		    sb.append(" 	And i.no_canasta = "+ canasta +" ");
		    
    		resultado = jdbcTemplate.query(sb.toString(), new RowMapper(){
    			public CanastaInversionesDto mapRow(ResultSet rs, int i) throws SQLException {
    				CanastaInversionesDto cons = new CanastaInversionesDto();
    				cons.setNomEmpresa(rs.getString("nom_empresa"));
    				cons.setNomBanco(rs.getString("desc_banco"));
	        		cons.setIdChequera(rs.getString("id_chequera"));
	        		cons.setImporte(rs.getDouble("importe"));
	        		cons.setInteres(rs.getDouble("interes"));
	        		cons.setIsr(rs.getDouble("isr"));
	        		cons.setFolioBanco(rs.getString("folio_banco"));
	        		return cons;
			}});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:consultarDetalleCanasta");
		}
		return resultado;
	}
	
	
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++       TASAS Y COMISION DE INVERSION        ++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	
	
	@Override
	public List<CanastaInversionesDto> obtenerTasaComision() {
		List<CanastaInversionesDto> resultado = null;
		StringBuffer sb = new StringBuffer();
		
		try {
		    sb.append(" SELECT dias, valor_tasa, '' as fecha, 0 as valor_comision FROM tasas_inversiones \n");
		    sb.append(" UNION \n");
		    sb.append(" SELECT top 1 0 as dias, 0 as valor_tasa, fecha, valor_comision FROM comision_inversiones \n");
		    sb.append(" ORDER BY dias, fecha desc ");
    		
    		resultado = jdbcTemplate.query(sb.toString(), new RowMapper(){
    			public CanastaInversionesDto mapRow(ResultSet rs, int i) throws SQLException {
    				CanastaInversionesDto cons = new CanastaInversionesDto();
	        		cons.setDias(rs.getString("dias"));
	        		cons.setTasa(rs.getDouble("valor_tasa"));
	        		cons.setComision(rs.getDouble("valor_comision"));
	        		return cons;
			}});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:obtenerTasaComision");
		}
		return resultado;
	}

	@Override
	public int existeTasa(int dias) {
		StringBuffer sb = new StringBuffer();
		int resultado = 0;
		
		try {
		    sb.append(" SELECT count(*) FROM tasas_inversiones ");
		    sb.append("\n  WHERE dias = "+ dias +" ");
    			
    		resultado = jdbcTemplate.queryForInt(sb.toString());
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:existeTasaComision");
		}
		return resultado;
	}

	@Override
	public int insertarTasa(int dias, double valorTasa) {
		StringBuffer sql = new StringBuffer();
		int result = 0;
		
		try {
			sql.append(" INSERT INTO tasas_inversiones \n");
			sql.append(" 	(dias, valor_tasa) \n");
			sql.append(" VALUES("+ dias +", "+ valorTasa +") \n");
			
			result = jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:insertarTasaComision");
		}
		return result;
	}

	@Override
	public int modificarTasa(int dias, double valorTasa) {
		StringBuffer sql = new StringBuffer();
		int result = 0;
		
		try {
			sql.append(" UPDATE tasas_inversiones SET valor_tasa = "+ valorTasa +" \n");
			sql.append(" WHERE dias = "+ dias +" \n");
			
			result = jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:modificarTasa");
		}
		return result;
	}

	@Override
	public int existeComision(double comision, Date fecHoy) {
		StringBuffer sb = new StringBuffer();
		int resultado = 0;
		
		try {
		    sb.append(" SELECT count(*) FROM comision_inversiones ");
		    sb.append("\n  WHERE valor_comision = "+ comision +" ");
		    sb.append("\n  	And 0 = datediff(month, fecha, '"+ funciones.ponerFecha(fecHoy) +"') ");
		    
    		resultado = jdbcTemplate.queryForInt(sb.toString());
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:existeTasaComision");
		}
		return resultado;
	}

	@Override
	public int insertarComision(double comision, Date fecHoy) {
		StringBuffer sql = new StringBuffer();
		int result = 0;
		
		try {
			sql.append(" INSERT INTO comision_inversiones \n");
			sql.append(" 	(fecha, valor_comision) \n");
			sql.append(" VALUES('"+ funciones.ponerFecha(fecHoy) +"', "+ comision +") \n");
			
			result = jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:insertarComision");
		}
		return result;
	}

	@Override
	public List<CanastaInversionesDto> obtenerCanastasVencidasHoy(
			String noEmpresa, String idDivisa) {
		List<CanastaInversionesDto> listCanastas = new ArrayList<CanastaInversionesDto>();
		globalSingleton = GlobalSingleton.getInstancia();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT distinct ec.no_empresa, ec.nom_empresa, 'CANASTA ' + cast(ci.no_canasta as varchar(50)) as canasta, \n");
			sql.append(" 		ci.importe, ci.interes, ci.isr, i.tasa, i.fecha_alta, ci.plazo, ci.fecha_vence, ci.no_canasta, coalesce(ci.folio_banco, '') as folio_banco \n");
			sql.append(" FROM canasta_inversiones ci, inversion i, control_barrido cb, cat_cta_banco ccb, empresa ec \n");
			sql.append(" WHERE ci.no_canasta = i.no_canasta \n");
			sql.append(" 	And i.no_solicitud = cb.no_solicitud \n");
			sql.append(" 	And cb.id_chequera = ccb.id_chequera \n");
			sql.append(" 	And cb.no_concentradora = ec.no_empresa \n");
			sql.append(" 	And ci.id_estatus = 'K' \n");
			sql.append(" 	And ccb.no_empresa in (select no_empresa from usuario_empresa \n");
			sql.append(" 		where no_usuario = " + globalSingleton.getUsuarioLoginDto().getIdUsuario() + ") \n");
			sql.append(" 	And ccb.id_divisa = '"+ idDivisa +"' \n");
			SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
			sql.append(" 	And ci.fecha_vence = '" + formateador.format(obtenerFechaHoy()) + "' \n");
			
			if(!noEmpresa.equals(""))
				sql.append(" 	And cb.no_concentradora = "+ noEmpresa +" \n");
			
			sql.append(" ORDER BY canasta \n");
			
			listCanastas = jdbcTemplate.query(sql.toString(), new RowMapper(){
		    	public CanastaInversionesDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		CanastaInversionesDto dtoBco = new CanastaInversionesDto();
	    			dtoBco.setNoEmpresa(rs.getInt("no_empresa"));
	    			dtoBco.setNomEmpresa(rs.getString("nom_empresa"));
	    			dtoBco.setCanasta(rs.getString("canasta"));
	    			dtoBco.setImporte(rs.getDouble("importe"));
	    			dtoBco.setInteres(rs.getDouble("interes"));
	    			dtoBco.setIsr(rs.getDouble("isr"));
	    			dtoBco.setTasa(rs.getDouble("tasa"));
	    			dtoBco.setFechaAlta(rs.getDate("fecha_alta"));
	    			dtoBco.setDias(rs.getString("plazo"));
	    			dtoBco.setFechaVence(rs.getDate("fecha_vence"));
	    			dtoBco.setNoCanasta(rs.getInt("no_canasta"));
	    			dtoBco.setFolioBanco(rs.getString("folio_banco"));
			    	return dtoBco;
		    	}
		    });
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
			"P:Inversiones, C:BarridoInversionDaoImpl, M:obtenerCanastasVencidasHoy");
		}
		return listCanastas;
	}
}