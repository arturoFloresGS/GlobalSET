package com.webset.set.consultas.dao.impl;

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

import com.webset.set.consultas.dao.PosicionBancariaDao;
import com.webset.set.consultas.dto.PosicionBancariaDTO;
import com.webset.set.consultas.dto.UsuarioPlantilla;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.RubroRegNeg;
import com.webset.utils.tools.Utilerias;

public class PosicionBancariaDaoImpl implements PosicionBancariaDao{


	/*incio todo angel*/
	
	JdbcTemplate jdbcTemplate;
	private Bitacora bitacora = new Bitacora();
	private Funciones funciones = new Funciones();
	private ConsultasGenerales consultasGenerales;
	
	/**********************************************************************/
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaDaoImpl, M:setDataSource");
		}
	}
	/*************************************************************************/

	@Override
	public List<PosicionBancariaDTO> consultarCriterioSeleccion(final int idCriterio) {
		
		StringBuffer sql = new StringBuffer();
		List<PosicionBancariaDTO> listRes = new ArrayList<PosicionBancariaDTO>();
		
		try{
			
			switch(idCriterio){
				case 1:
					sql.append("\n SELECT DISTINCT cb.id_banco , cb.desc_banco , ccb.id_chequera, e.no_empresa, e.nom_empresa, ccb.desc_chequera ");
					sql.append("\n FROM CAT_BANCO cb JOIN CAT_CTA_BANCO ccb ON cb.id_banco = ccb.id_banco ");
					sql.append("\n 					 JOIN EMPRESA e ON ccb.no_empresa = e.no_empresa ");
					sql.append("\n  				 JOIN USUARIO_EMPRESA ue ON e.no_empresa = ue.no_empresa ");
					sql.append("\n WHERE ue.no_usuario = "+GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+" ");
					sql.append("\n ORDER BY 1");
					break;
				case 2:
					sql.append("\n SELECT DISTINCT cb.id_banco , cb.desc_banco , e.no_empresa, e.nom_empresa ");
					sql.append("\n FROM CAT_BANCO cb JOIN CAT_CTA_BANCO ccb ON cb.id_banco = ccb.id_banco ");
					sql.append("\n 					 JOIN EMPRESA e ON ccb.no_empresa = e.no_empresa ");
					sql.append("\n  				 JOIN USUARIO_EMPRESA ue ON e.no_empresa = ue.no_empresa ");
					sql.append("\n WHERE ue.no_usuario = "+GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+" ");
					sql.append("\n ORDER BY 1");
					break;
				case 3:
					sql.append("\n SELECT DISTINCT cb.id_banco , cb.desc_banco ");
					sql.append("\n FROM CAT_BANCO cb JOIN CAT_CTA_BANCO ccb ON cb.id_banco = ccb.id_banco ");
					sql.append("\n 					 JOIN EMPRESA e ON ccb.no_empresa = e.no_empresa ");
					sql.append("\n  				 JOIN USUARIO_EMPRESA ue ON e.no_empresa = ue.no_empresa ");
					sql.append("\n WHERE ue.no_usuario = "+GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+" ");
					sql.append("\n ORDER BY 1");
					break;
				case 4:
					sql.append("\n SELECT DISTINCT e.no_empresa, e.nom_empresa ");
					sql.append("\n FROM EMPRESA e JOIN USUARIO_EMPRESA ue ON e.no_empresa = ue.no_empresa ");
					sql.append("\n WHERE ue.no_usuario = "+GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+" ");
					sql.append("\n ORDER BY 2");
					break;	
			}
			
		//	System.out.println("query consulta grid criterios: " + sql.toString());
			
			listRes = jdbcTemplate.query(sql.toString(), new RowMapper<PosicionBancariaDTO>() {
				public PosicionBancariaDTO mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDTO cons = new PosicionBancariaDTO();
					switch (idCriterio){
						case 1:
							cons.setIdBanco(rs.getInt("id_banco"));
							cons.setDescBanco(rs.getString("desc_banco"));
							cons.setIdChequera(rs.getString("id_chequera"));
							cons.setDescChequera(rs.getString("desc_chequera"));
							cons.setNoEmpresa(rs.getInt("no_empresa"));
							cons.setNomEmpresa(rs.getString("nom_empresa"));
							break;
						case 2:
							cons.setIdBanco(rs.getInt("id_banco"));
							cons.setDescBanco(rs.getString("desc_banco"));
							cons.setNoEmpresa(rs.getInt("no_empresa"));
							cons.setNomEmpresa(rs.getString("nom_empresa"));
							break;
						case 3:
							cons.setIdBanco(rs.getInt("id_banco"));
							cons.setDescBanco(rs.getString("desc_banco"));							
							break;
						case 4:
							cons.setNoEmpresa(rs.getInt("no_empresa"));
							cons.setNomEmpresa(rs.getString("nom_empresa"));
							break;
					}
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:consultarCriterioSeleccion");
		}
		return listRes;
	}
	
	public List<LlenaComboDivisaDto>llenarComboDivisa() {
		String sql="";
		List<LlenaComboDivisaDto> listDivisas= new ArrayList<LlenaComboDivisaDto>();
		try
		{
			sql+= " SELECT id_divisa as ID, desc_divisa as Descrip ";
			sql+= " FROM cat_divisa ";
//			sql+= " WHERE clasificacion = 'D'";			
//			sql+= " Union";
//			sql+= " Select 'TDS' As Id, 'TODAS' As Descrip From Dual";
//			sql+= " order by 1";
		//	System.out.println("divisas "+sql);
			
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
	
	@Override
	public List<LlenaComboGralDto>llenarComboPlantillas(String pantalla) {
		StringBuffer sql = new StringBuffer();
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		
		try{
			
			sql.append("\n SELECT FOLIO_PLANTILLA as ID, NOMBRE_PLANTILLA as Descrip");
			sql.append("\n FROM USUARIO_PLANTILLA ");
			sql.append("\n WHERE PANTALLA = '"+pantalla+"' ");
			sql.append("\n ORDER BY NOMBRE_PLANTILLA ");
			
			////System.out.println("Divisa: " + sql);
			
    		list= jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("Descrip"));
					return cons;
				}});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Consultas, C:PosicionBancariaDaoImpl, M:llenarComboPlantillas");
		}
		return list;
	}
	
	@Override
	public List<RubroRegNeg>consultarRubros() {
		StringBuffer sql = new StringBuffer();
		List<RubroRegNeg> list= new ArrayList<RubroRegNeg>();
		
		try{
			
			sql.append("\n SELECT id_rubro as ID, desc_rubro as Descrip");
			sql.append("\n FROM cat_rubro ");
			sql.append("\n ORDER BY desc_rubro ");
			
    		list= jdbcTemplate.query(sql.toString(), new RowMapper<RubroRegNeg>(){
				public RubroRegNeg mapRow(ResultSet rs, int idx) throws SQLException {
					RubroRegNeg cons = new RubroRegNeg();
					cons.setIdRubro(rs.getString("ID"));
					cons.setDescRubro(rs.getString("Descrip"));
					return cons;
				}});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Consultas, C:PosicionBancariaDaoImpl, M:consultarRubros");
		}
		return list;
	}
	
	@Override
	public List<PosicionBancariaDTO> consultarDetallePosicion(String idRubros, double importeIni, double importeFin,
															  String idBancos, String idChequeras, 
															  String noEmpresas,String divisa,
															  String fechaIni, String fechaFin,
															  int optConsulta, boolean bSinRubros) {
		
		StringBuffer sql = new StringBuffer();
		List<PosicionBancariaDTO> list= new ArrayList<PosicionBancariaDTO>();
		
		try{
			
			sql.append("\n SELECT DISTINCT VM.no_folio_det  , ");
			sql.append("\n VM.id_Banco , ");
			sql.append("\n (SELECT desc_banco from CAT_BANCO WHERE VM.id_banco = id_banco) as banco   , ");
			sql.append("\n (SELECT id_chequera from CAT_CTA_BANCO WHERE VM.id_Chequera = id_Chequera) as chequera, ");
			sql.append("\n coalesce(CR.id_rubro, 0) as id_rubro, ");
			sql.append("\n (SELECT desc_rubro from CAT_RUBRO WHERE VM.id_rubro = id_rubro) as sub_rubro, ");
			sql.append("\n cr.desc_rubro as desc_rubro, ");
			sql.append("\n VM.importe, VM.concepto, VM.referencia, VM.fec_operacion, ");
			sql.append("\n VM.fec_valor, VM.id_divisa, VM.id_divisa_original ,");
			sql.append("\n VM.importe_original, VM.fec_valor_original, VM.id_tipo_movto, ");
			sql.append("\n VM.beneficiario, VM.descripcion, VM.no_docto ");
			sql.append("\n from MOVIMIENTO VM LEFT OUTER JOIN cat_rubro cr ON (VM.id_rubro = cr.id_rubro)");
			sql.append("\n Where fec_valor >= convert(date,'"+ fechaIni +"',103) AND fec_valor < convert(date,DATEADD(day,1,'"+fechaFin+"'),103) ");
			
			if(!idRubros.equals("")){
				sql.append("\n and cr.id_rubro in (" + idRubros.trim() + ") ");
			}else if (bSinRubros){
				sql.append("\n and COALESCE(VM.id_rubro, 0) not in (SELECT id_rubro FROM cat_rubro) ");
			}else{
				//Busqueda por importes
				sql.append("\n and VM.importe BETWEEN " + importeIni + " AND "+importeFin+" ");
			}
			
			  switch (optConsulta) {
				case 1:
					sql.append("\n and id_banco in (" + idBancos + ") ");
					sql.append("\n and id_chequera in (" + idChequeras + ") ");
					break;
				case 2:
					sql.append("\n and id_banco in (" + idBancos + ") ");
					sql.append("\n and no_empresa in (" + noEmpresas + ") ");
					break;
				case 3:
					sql.append("\n and id_banco in (" + idBancos + ") ");
					break;
				case 4:
					sql.append("\n and no_empresa in (" + noEmpresas + ") ");
					break;
			}
			
			if (divisa.trim().length() > 0 && !divisa.trim().equals("TDS"))
				sql.append("\n and rtrim(rtrim(VM.id_divisa)) = '" + divisa + "'");
			
			sql.append("\n and VM.id_estatus_mov not in ('X','Q','H') ");
			sql.append("\n and VM.id_tipo_operacion not in(1000,3000,3001,3201,4000,4002,4003,4004,3800,3801,3805,3806,3808,3809,3810,3811) ");
			/*--- MOVIMIENTO_BANK (inicia) ---*/
			sql.append("\n UNION ALL ");
			sql.append("\n SELECT DISTINCT VM.no_folio_det  , ");
			sql.append("\n VM.id_Banco , ");
			sql.append("\n (SELECT desc_banco from CAT_BANCO WHERE VM.id_banco = id_banco) as banco   , ");
			sql.append("\n (SELECT id_chequera from CAT_CTA_BANCO WHERE VM.id_Chequera = id_Chequera) as chequera, ");
			sql.append("\n coalesce(CR.id_rubro, 0) as id_rubro, ");
			sql.append("\n (SELECT desc_rubro from CAT_RUBRO WHERE VM.id_rubro = id_rubro) as sub_rubro, ");
			sql.append("\n cr.desc_rubro as desc_rubro, ");
			sql.append("\n VM.importe, VM.concepto, VM.referencia, VM.fec_operacion, ");
			sql.append("\n VM.fec_valor, VM.id_divisa, VM.id_divisa_original ,");
			sql.append("\n VM.importe importe_original, VM.fec_valor fec_valor_original, VM.id_tipo_movto, ");
			sql.append("\n ' ' AS beneficiario, VM.descripcion, VM.no_docto ");
			sql.append("\n from MOVIMIENTO_BANK VM LEFT OUTER JOIN cat_rubro cr ON (VM.id_rubro = cr.id_rubro)");
			sql.append("\n Where fec_valor >= convert(date,'"+ fechaIni +"',103) AND fec_valor < convert(date,DATEADD(day,1,'"+fechaFin+"'),103) ");
			
			if(!idRubros.equals("")){
				sql.append("\n and cr.id_rubro in (" + idRubros.trim() + ") ");
			}else if (bSinRubros){
				sql.append("\n and COALESCE(VM.id_rubro, 0) not in (SELECT id_rubro FROM cat_rubro) ");
			}else{
				//Busqueda por importes
				sql.append("\n and VM.importe BETWEEN " + importeIni + " AND "+importeFin+" ");
			}
			
			  switch (optConsulta) {
				case 1:
					sql.append("\n and id_banco in (" + idBancos + ") ");
					sql.append("\n and id_chequera in (" + idChequeras + ") ");
					break;
				case 2:
					sql.append("\n and id_banco in (" + idBancos + ") ");
					sql.append("\n and no_empresa in (" + noEmpresas + ") ");
					break;
				case 3:
					sql.append("\n and id_banco in (" + idBancos + ") ");
					break;
				case 4:
					sql.append("\n and no_empresa in (" + noEmpresas + ") ");
					break;
			}
			
			if (divisa.trim().length() > 0 && !divisa.trim().equals("TDS"))
				sql.append("\n and rtrim(rtrim(VM.id_divisa)) = '" + divisa + "'");
			
			sql.append("\n and VM.id_estatus_mov not in ('X','Q','H') ");
			sql.append("\n and VM.id_tipo_operacion not in(1000,3000,3001,3201,4000,4002,4003,4004,3800,3801,3805,3806,3808,3809,3810,3811) ");
			/*--- MOVIMIENTO_BANK (termina) ---*/
			sql.append("\n order by banco, chequera, id_rubro, desc_Rubro ");
			
			System.out.println("Obtiene posicion bancaria rubros/importe: \n" + sql.toString());
			
    		list= jdbcTemplate.query(sql.toString(), new RowMapper<PosicionBancariaDTO>(){
				public PosicionBancariaDTO mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDTO cons = new PosicionBancariaDTO();
					cons.setNoFolioDet(rs.getInt("no_folio_det"));
					cons.setIdBanco(rs.getInt("id_banco"));
					cons.setDescBanco(rs.getString("banco"));
					cons.setIdChequera(rs.getString("chequera"));
					cons.setIdRubro(rs.getString("id_rubro"));
					//Falta la dsc, de sub-rubro - (SELECT desc_rubro from CAT_RUBRO WHERE VM.id_rubro = id_rubro) as sub_rubro 
					cons.setDescRubro(rs.getString("desc_rubro"));
					cons.setImporte(rs.getDouble("importe"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setReferencia(rs.getString("referencia"));
					cons.setFecOperacion(funciones.ponerFechaSola(rs.getDate("fec_operacion")));
					cons.setFecValor(funciones.ponerFechaSola(rs.getDate("fec_valor")));
					cons.setIdDivisa(rs.getString("id_divisa"));
					//Falta id_divisa_original, importe_original, fec_valor_original
					cons.setIdTipoMovto(rs.getString("id_tipo_movto"));
					cons.setBeneficiario(rs.getString("beneficiario"));
					cons.setDescripcion(rs.getString("descripcion"));
					cons.setNoDocto(rs.getString("no_docto"));
					return cons;
				}});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Consultas, C:PosicionBancariaDaoImpl, M:consultarDetallePosicion");
		}
		return list;
	}

	@Override
	public List<PosicionBancariaDTO> consultarDetallePosicion2(String rubro, String idChequeras, 
															  String noEmpresas,String divisa,
															  String fechaIni, String fechaFin,
															  String consulta, String tipoMovto) {
		
		StringBuffer sql = new StringBuffer();
		List<PosicionBancariaDTO> list= new ArrayList<PosicionBancariaDTO>();
		
		try{
			
			sql.append("\n SELECT DISTINCT VM.no_folio_det  , ");
			sql.append("\n VM.id_Banco , ");
			sql.append("\n (SELECT desc_banco from CAT_BANCO WHERE VM.id_banco = id_banco AND ROWNUM <= 1) as banco   , ");
			sql.append("\n (SELECT id_chequera from CAT_CTA_BANCO WHERE VM.id_Chequera = id_Chequera AND ROWNUM <= 1) as chequera, ");
			sql.append("\n CR.id_rubro as id_rubro, ");
			sql.append("\n (SELECT desc_rubro from CAT_RUBRO WHERE VM.id_rubro = id_rubro AND ROWNUM <= 1) as sub_rubro, ");
			sql.append("\n cr.desc_rubro as desc_rubro, ");
			sql.append("\n VM.importe, VM.concepto, VM.referencia, VM.fec_operacion, ");
			sql.append("\n VM.fec_valor, VM.id_divisa, VM.id_divisa_original ,");
			sql.append("\n VM.importe_original, VM.fec_valor_original, VM.id_tipo_movto, ");
			sql.append("\n VM.beneficiario, VM.descripcion, VM.no_docto, ");
			sql.append("\n e.no_empresa, e.nom_empresa ");
			sql.append("\n from MOVIMIENTO VM , cat_rubro cr, cat_grupo cg, empresa e ");
			sql.append("\n Where VM.id_rubro = cr.id_rubro ");
			sql.append("\n and VM.id_grupo = cr.id_grupo ");
			sql.append("\n and cr.id_grupo = cg.id_grupo ");
			sql.append("\n and VM.no_empresa = e.no_empresa ");
			
			if(!rubro.equals("")){
				sql.append("\n and cg.id_grupo in (" + rubro + ") ");
			}
			
			if(!idChequeras.equals("")){
				sql.append("\n and id_chequera in (" + idChequeras + ") ");
			}
			
			if(!noEmpresas.equals("")){
				sql.append("\n and VM.no_empresa in (" + noEmpresas + ") ");
			}
			
			if(tipoMovto.equals("I")){
				sql.append("\n AND ((id_tipo_operacion in (3200, 3701, 3120, 3101,3116, 1016, 1017, 1018, 1019, 1020, 1023,3107, 3700, 3702, 3705,3706,3708,3709,3707,4001,4102,4103,4104,3500)and id_estatus_mov = 'A')");
				sql.append("\n OR (id_tipo_operacion in (3102,3103)and id_estatus_mov = 'P') OR (id_tipo_operacion in (3500)and id_estatus_mov = 'W')) ");
			}else{
				sql.append("\n AND ((id_tipo_operacion in (3200)and id_estatus_mov in ('T','K','I','P')) ");
				sql.append("\n OR (id_tipo_operacion in (3200, 3701, 3120, 3101,3116, 1016, 1017, 1018, 1019, 1020, 1023,3107, 3700, 3702, 3705,3706,3708,3709,3707,4001,4102,4103,4104)and id_estatus_mov = 'A')) ");
			}
			
			if (divisa.trim().length() > 0 && !divisa.trim().equals("TDS"))
				sql.append("\n and trim(VM.id_divisa) = '" + divisa + "'");
			
			sql.append("\n and fec_valor >= TO_DATE('"+ fechaIni +"','dd/mm/yyyy') AND fec_valor < TO_DATE('"+fechaFin+"','dd/mm/yyyy')+1 "); 
			sql.append("\n and VM.id_estatus_mov not in ('X','Q','H') ");
			sql.append("\n and VM.id_estatus_mov not in ('X','Q','H') ");
			sql.append("\n and VM.id_tipo_operacion not in(1000,3000,3001,3201,4000,4002,4003,4004,3800,3801,3805,3806,3808,3809,3810,3811) ");
			
			sql.append("\n order by banco, chequera, no_empresa, id_rubro, desc_Rubro ");
			
			//System.out.println("Obtiene posicion bancaria rubros/importe: \n" + sql.toString());
			
    		list= jdbcTemplate.query(sql.toString(), new RowMapper<PosicionBancariaDTO>(){
				public PosicionBancariaDTO mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDTO cons = new PosicionBancariaDTO();
					cons.setNoFolioDet(rs.getInt("no_folio_det"));
					cons.setIdBanco(rs.getInt("id_banco"));
					cons.setDescBanco(rs.getString("banco"));
					cons.setIdChequera(rs.getString("chequera"));
					cons.setIdRubro(rs.getString("id_rubro"));
					//Falta la dsc, de sub-rubro - (SELECT desc_rubro from CAT_RUBRO WHERE VM.id_rubro = id_rubro) as sub_rubro 
					cons.setDescRubro(rs.getString("desc_rubro"));
					cons.setImporte(rs.getDouble("importe"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setReferencia(rs.getString("referencia"));
					cons.setFecOperacion(funciones.ponerFechaSola(rs.getDate("fec_operacion")));
					cons.setFecValor(funciones.ponerFechaSola(rs.getDate("fec_valor")));
					cons.setIdDivisa(rs.getString("id_divisa"));
					//Falta id_divisa_original, importe_original, fec_valor_original
					cons.setIdTipoMovto(rs.getString("id_tipo_movto"));
					cons.setBeneficiario(rs.getString("beneficiario"));
					cons.setDescripcion(rs.getString("descripcion"));
					cons.setNoDocto(rs.getString("no_docto"));
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setNomEmpresa(rs.getString("nom_empresa"));
					return cons;
				}});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Consultas, C:PosicionBancariaDaoImpl, M:consultarDetallePosicion");
		}
		return list;
	}
	
	@Override
	public List<LlenaComboGralDto> obtenerGruposRubros(String idTipoMovto) {
		
		StringBuffer sql = new StringBuffer();
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		
		try{
			
			sql.append("\n SELECT DISTINCT id_grupo AS ID, desc_grupo As describ ");
			sql.append("\n from cat_grupo ");
			sql.append("\n where id_grupo in (select id_grupo ");
			sql.append("\n                 	  from cat_rubro ");
			sql.append("\n                    where ingreso_egreso = '" + Utilerias.validarCadenaSQL(idTipoMovto) + "' )");
						
			
    		list= jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cmb = new LlenaComboGralDto();
					cmb.setIdStr(rs.getString("ID"));
					cmb.setDescripcion(rs.getString("describ"));
					return cmb;
				}});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Consultas, C:PosicionBancariaDaoImpl, M:consultarDetallePosicion");
		}
		return list;
	}

	@Override
	public List<LlenaComboGralDto> llenarComboRubro(String idGrupoRubro) {
		
		StringBuffer sql = new StringBuffer();
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		
		try{
			
			sql.append("\n SELECT DISTINCT id_rubro AS ID, desc_rubro As describ ");
			sql.append("\n from cat_rubro ");
			
			if(!idGrupoRubro.equals("")){
				sql.append("\n where id_grupo = '"+idGrupoRubro+"' ");
			}
			
    		list= jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cmb = new LlenaComboGralDto();
					cmb.setIdStr(rs.getString("ID"));
					cmb.setDescripcion(rs.getString("describ"));
					return cmb;
				}});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Consultas, C:PosicionBancariaDaoImpl, M:llenarComboRubro");
		}
		return list;
	}
	
	@Override
	public Map<String, String> actualizaRubros(String noFolioDet,String idGrupo, String idRubro,
												double importe, String referencia, boolean actualizaTodo ){
		
		Map<String, String> result = new HashMap<>();
		result.put("msg", "");
		result.put("error", "");
		
		StringBuffer sql = new StringBuffer();
		int cont = 0;
		
		try{
			
			sql.append("\n UPDATE MOVIMIENTO ");
			sql.append("\n SET ID_RUBRO = '"+idRubro+"' ");
			
			if(actualizaTodo){
				//Faltaria la fecha valor.
				sql.append("\n , REFERENCIA = '"+referencia+"' ");
			}
			sql.append("\n , ID_GRUPO = '" + idGrupo + "' ");
			sql.append("\n , HORA_RECIBO = SYSDATE ");
			sql.append("\n , USUARIO_ALTA = "+GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+" ");
			sql.append("\n , USUARIO_MODIF = "+GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+" ");
			sql.append("\n WHERE NO_FOLIO_DET = '"+noFolioDet+"' ");
			sql.append("\n AND IMPORTE = "+importe+" ");
			
    		cont = jdbcTemplate.update(sql.toString());
    		
    		if(cont == 0){
    			result.put("error", "Error al actualizar el registro.");
    		}else{
    			result.put("msg", "Registro modificado con éxito.");
    		}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Consultas, C:PosicionBancariaDaoImpl, M:actualizaRubros");
			result.put("error", "Error al ejecutar la actualización.");
		}
		return result;
	}
	
	
	@Override
	public List<PosicionBancariaDTO> consultarMovtoTes(String idBancos, String idChequeras, 
													   String noEmpresas, String fechaIni, String fechaFin,
													   int seleccion, String divisa) {
		
		StringBuffer sql = new StringBuffer();
		List<PosicionBancariaDTO> list= new ArrayList<PosicionBancariaDTO>();
		
		try{
			
			sql.append("\n select ccb.no_empresa,  e.nom_empresa, ban.desc_banco, ban.id_banco, ccb.id_chequera, coalesce(cr.id_grupo, 0) id_grupo, coalesce(cr.id_rubro, 0) id_rubro, ");
			sql.append("\n m.fec_valor , m.fec_operacion, m.referencia,(substr(coalesce(m.beneficiario,' '),1,20) || ' ' || m.concepto) as concepto, ");
			if( !divisa.equals("TDS") ){
			sql.append("\n  case m.id_tipo_movto when 'I' then importe else 0 end as Ingresos, ");
			sql.append("\n case m.id_tipo_movto when 'E' then importe else 0 end as Egresos");
			}else{
				sql.append("\n Case M.Id_Tipo_Movto When 'I' Then Case Trim(M.Id_Divisa ) When 'DLS' Then  Importe * (Select Valor From Valor_Divisa Where Fec_Divisa = To_Date('"+fechaFin+"','dd/mm/yyyy') And Id_Divisa = 'DLS') Else Importe End Else 0 End As Ingresos, ");
				sql.append("\n Case M.Id_Tipo_Movto When 'E' Then Case Trim(M.Id_Divisa ) When 'DLS' Then  Importe * (Select Valor From Valor_Divisa Where Fec_Divisa = To_Date('"+fechaFin+"','dd/mm/yyyy') And Id_Divisa = 'DLS') Else Importe End Else 0 End as  Egresos ");
			}
			sql.append("\n ,m.id_tipo_movto ");
			sql.append("\n FROM movimiento m LEFT OUTER JOIN cat_rubro cr ON (m.id_grupo = cr.id_grupo AND m.id_rubro = cr.id_rubro), Cat_banco ban, empresa e, cat_cta_banco ccb ");
			sql.append("\n Where ccb.id_banco = Ban.id_banco ");
			sql.append("\n and ccb.id_banco = m.id_banco ");
			sql.append("\n and ccb.id_chequera = m.id_chequera ");
			sql.append("\n and m.id_estatus_mov <> 'X' ");
			sql.append("\n and m.id_tipo_operacion in( 3200, 3701, 3120, 3101,3116, 1016, 1017, 1018, 1019, 1020, 1023, 3107, 3700, 3702, 3705,3706,3708,3709,3707,4001,4102,4103,4104,3500) ");
			sql.append("\n and ccb.no_empresa = e.no_empresa ");
			 
			  switch (seleccion){
				case 1:
					sql.append("\n and ccb.id_banco in (" + idBancos + ") ");
					sql.append("\n and ccb.id_chequera in (" + idChequeras + ") ");
					break;
				case 2:
					sql.append("\n and ccb.id_banco in (" + idBancos + ") ");
					sql.append("\n and m.no_empresa in (" + noEmpresas + ") ");
					break;
				case 3:
					sql.append("\n and m.id_banco in (" + idBancos + ") ");
					break;
				case 4:
					sql.append("\n and m.no_empresa in (" + noEmpresas + ") ");
					break;
			}
			
			if( !divisa.equals("TDS") ){
				sql.append("\n and m.id_divisa = '" + divisa + "'");
			}
			
			sql.append("\n and m.fec_valor  BETWEEN TO_DATE('"+ fechaIni +"','dd/mm/yyyy') AND TO_DATE('"+fechaFin+"','dd/mm/yyyy') "); 
			
			sql.append("\n UNION ALL");
			
			/*--- JMRE: movimiento_bank  ---*/
			sql.append("\n select ccb.no_empresa, e.nom_empresa, ban.desc_banco, ban.id_banco, ccb.id_chequera, coalesce(cr.id_grupo, 0) id_grupo, coalesce(cr.id_rubro, 0) id_rubro, ");
			sql.append("\n m.fec_valor , m.fec_operacion, m.referencia, m.concepto, ");
			if( !divisa.equals("TDS") ){
			sql.append("\n  case m.id_tipo_movto when 'I' then importe else 0 end as Ingresos, ");
			sql.append("\n case m.id_tipo_movto when 'E' then importe else 0 end as Egresos");
			}else{
				sql.append("\n Case M.Id_Tipo_Movto When 'I' Then Case Trim(M.Id_Divisa ) When 'DLS' Then  Importe * (Select Valor From Valor_Divisa Where Fec_Divisa = To_Date('"+fechaFin+"','dd/mm/yyyy') And Id_Divisa = 'DLS') Else Importe End Else 0 End As Ingresos, ");
				sql.append("\n Case M.Id_Tipo_Movto When 'E' Then Case Trim(M.Id_Divisa ) When 'DLS' Then  Importe * (Select Valor From Valor_Divisa Where Fec_Divisa = To_Date('"+fechaFin+"','dd/mm/yyyy') And Id_Divisa = 'DLS') Else Importe End Else 0 End as  Egresos ");
			}
			sql.append("\n ,m.id_tipo_movto ");
			sql.append("\n FROM movimiento_bank m LEFT OUTER JOIN cat_rubro cr ON (m.id_grupo = cr.id_grupo AND m.id_rubro = cr.id_rubro), Cat_banco ban, empresa e, cat_cta_banco ccb ");
			sql.append("\n Where ccb.id_banco = Ban.id_banco ");
			sql.append("\n and ccb.id_banco = m.id_banco ");
			sql.append("\n and ccb.id_chequera = m.id_chequera ");
			sql.append("\n and m.id_estatus_mov <> 'X' ");
			sql.append("\n and ccb.no_empresa = e.no_empresa ");
			 
			  switch (seleccion){
				case 1:
					sql.append("\n and ccb.id_banco in (" + idBancos + ") ");
					sql.append("\n and ccb.id_chequera in (" + idChequeras + ") ");
					break;
				case 2:
					sql.append("\n and ccb.id_banco in (" + idBancos + ") ");
					sql.append("\n and m.no_empresa in (" + noEmpresas + ") ");
					break;
				case 3:
					sql.append("\n and m.id_banco in (" + idBancos + ") ");
					break;
				case 4:
					sql.append("\n and m.no_empresa in (" + noEmpresas + ") ");
					break;
			}
			
			if( !divisa.equals("TDS") ){
				sql.append("\n and m.id_divisa = '" + divisa + "'");
			}
			
			sql.append("\n and m.fec_valor  BETWEEN TO_DATE('"+ fechaIni +"','dd/mm/yyyy') AND TO_DATE('"+fechaFin+"','dd/mm/yyyy') "); 
			
			sql.append("\n UNION ALL");
			/*--- JMRE: Termina movimiento_bank  ---*/
			
			sql.append("\n select ccb.no_empresa, e.nom_empresa, ban.desc_banco, ban.id_banco, ccb.id_chequera, coalesce(cr.id_grupo, 0) id_grupo, coalesce(cr.id_rubro, 0) id_rubro, ");
			sql.append("\n m.fec_valor , m.fec_operacion, m.referencia,(substr(coalesce(m.beneficiario,' '),1,20) || ' ' || m.concepto) as concepto, ");
			if( ! divisa.equals("TDS") ){
				sql.append("\n  case m.id_tipo_movto when 'I' then importe else 0 end as Ingresos, ");
				sql.append("\n case m.id_tipo_movto when 'E' then importe else 0 end as Egresos");
			}else{
				sql.append("\n Case M.Id_Tipo_Movto When 'I' Then Case Trim(M.Id_Divisa ) When 'DLS' Then  Importe * (Select Valor From Valor_Divisa Where Fec_Divisa = To_Date('"+fechaFin+"','dd/mm/yyyy') And Id_Divisa = 'DLS') Else Importe End Else 0 End As Ingresos, ");
				sql.append("\n Case M.Id_Tipo_Movto When 'E' Then Case Trim(M.Id_Divisa ) When 'DLS' Then  Importe * (Select Valor From Valor_Divisa Where Fec_Divisa = To_Date('"+fechaFin+"','dd/mm/yyyy') And Id_Divisa = 'DLS') Else Importe End Else 0 End as  Egresos ");
			}
			sql.append("\n ,m.id_tipo_movto ");
			sql.append("from hist_movimiento m LEFT OUTER JOIN cat_rubro cr ON (m.id_grupo = cr.id_grupo AND m.id_rubro = cr.id_rubro), cat_cta_banco ccb, Cat_banco ban, empresa e ");
			sql.append("\n Where ccb.id_banco = Ban.id_banco ");
			sql.append("\n and ccb.id_banco = m.id_banco ");
			sql.append("\n and ccb.id_chequera = m.id_chequera ");
			sql.append("\n and m.id_estatus_mov <> 'X' ");
			sql.append("\n and m.id_tipo_operacion in( 3200, 3701, 3120, 3101,3116, 1016, 1017, 1018, 1019, 1020, 1023, 3107, 3700, 3702, 3705,3706,3708,3709,3707,4001,4102,4103,4104,3500) ");
			sql.append("\n and ccb.no_empresa = e.no_empresa ");
			 
			  switch (seleccion){
				case 1:
					sql.append("\n and ccb.id_banco in (" + idBancos + ") ");
					sql.append("\n and ccb.id_chequera in (" + idChequeras + ") ");
					break;
				case 2:
					sql.append("\n and ccb.id_banco in (" + idBancos + ") ");
					sql.append("\n and m.no_empresa in (" + noEmpresas + ") ");
					break;
				case 3:
					sql.append("\n and ccb.id_banco in (" + idBancos + ") ");
					break;
				case 4:
					sql.append("\n and m.no_empresa in (" + noEmpresas + ") ");
					break;
			}
			
			if( ! divisa.equals("TDS") ){
				sql.append("\n and m.id_divisa = '" + divisa + "'");
			}  
			  
			
			sql.append("\n and m.fec_valor  BETWEEN TO_DATE('"+ fechaIni +"','dd/mm/yyyy') AND TO_DATE('"+fechaFin+"','dd/mm/yyyy') ");
			
			sql.append("\n order by no_empresa, desc_banco, id_chequera, id_grupo, id_rubro, fec_valor asc ");
			
			//System.out.println("Obtiene movtos tesoreria: \n" + sql.toString());
			
    		list= jdbcTemplate.query(sql.toString(), new RowMapper<PosicionBancariaDTO>(){
				public PosicionBancariaDTO mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDTO cons = new PosicionBancariaDTO();
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setNomEmpresa(rs.getString("nom_empresa"));
					cons.setIdBanco(rs.getInt("id_banco"));
					cons.setDescBanco(rs.getString("desc_banco"));
					cons.setIdChequera(rs.getString("id_chequera"));
					cons.setIdRubro(rs.getString("id_rubro"));
					cons.setIdGrupo(rs.getString("id_grupo"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setReferencia(rs.getString("referencia"));
					cons.setFecOperacion(funciones.ponerFechaSola(rs.getDate("fec_operacion")));
					cons.setFecValor(funciones.ponerFechaSola(rs.getDate("fec_valor")));
					cons.setIngreso(rs.getDouble("ingresos"));
					cons.setEgreso(rs.getDouble("egresos"));
					cons.setIdTipoMovto(rs.getString("id_tipo_movto"));
					if (cons.getIdRubro().equals("0")){
						if (cons.getIdTipoMovto().equals("I")){
							cons.setIdGrupo("2");
							cons.setIdRubro("2");
						}else{
							cons.setIdGrupo("1");
							cons.setIdRubro("1");
						}
					}
					return cons;
				}});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Consultas, C:PosicionBancariaDaoImpl, M:consultarDetallePosicion");
		}
		return list;
	}
	
	@Override
	public List<PosicionBancariaDTO> consultarDiarioTes(String idBancos, String idChequeras, 
													   String noEmpresas, String fechaIni, String fechaFin,
													   int seleccion, String divisa) {
		
		StringBuffer sql = new StringBuffer();
		List<PosicionBancariaDTO> list= new ArrayList<PosicionBancariaDTO>();
		
		try{
			
			sql.append("\n select ban.desc_banco, ccb.id_chequera, coalesce(cr.id_grupo,0) id_grupo, coalesce(cr.id_rubro,0) id_rubro, ");
			sql.append("\n m.fec_valor , m.fec_operacion, m.referencia, m.concepto,m.id_tipo_movto, ");
			
			if( ! divisa.equals("TDS") ){
				sql.append("m.importe");
			}else{
				sql.append("\n Case Trim(M.Id_Divisa ) When 'DLS' Then  m.importe * (Select Valor From Valor_Divisa Where Fec_Divisa = To_Date('"+fechaFin+"','dd/mm/yyyy') And Id_Divisa = 'DLS') Else m.importe End importe ");
			}
			
			sql.append("\n from movimiento m LEFT OUTER JOIN cat_rubro cr ON (m.id_grupo = cr.id_grupo AND m.id_rubro = cr.id_rubro), cat_cta_banco ccb, cat_banco ban ");
			sql.append("\n Where ccb.id_banco = Ban.id_banco ");
			sql.append("\n and ccb.id_banco = m.id_banco ");
			sql.append("\n and ccb.id_chequera = m.id_chequera ");
			sql.append("\n and id_estatus_mov <> 'X' ");
			sql.append("\n and m.id_tipo_operacion in( 3200, 3701, 3120, 3101,3116, 1016, 1017, 1018, 1019, 1020, 1023, 3107, 3700, 3702, 3705,3706,3708,3709,3707,4001,4102,4103,4104,3500) ");
			
			  switch (seleccion){
				case 1:
					sql.append("\n and ccb.id_banco in (" + idBancos + ") ");
					sql.append("\n and ccb.id_chequera in (" + idChequeras + ") ");
					break;
				case 2:
					sql.append("\n and ccb.id_banco in (" + idBancos + ") ");
					sql.append("\n and m.no_empresa in (" + noEmpresas + ") ");
					break;
				case 3:
					sql.append("\n and m.id_banco in (" + idBancos + ") ");
					break;
				case 4:
					sql.append("\n and m.no_empresa in (" + noEmpresas + ") ");
					break;
			}
			  if( ! divisa.equals("TDS") ){
			sql.append("\n and m.id_divisa = '" + divisa + "'");
			  }
			sql.append("\n and m.fec_valor  BETWEEN TO_DATE('"+ fechaIni +"','dd/mm/yyyy') AND TO_DATE('"+fechaFin+"','dd/mm/yyyy') "); 
			
			sql.append("\n UNION ALL");
			
			/*--- Movimiento_bank ---*/
			sql.append("\n select ban.desc_banco, ccb.id_chequera, coalesce(cr.id_grupo,0) id_grupo, coalesce(cr.id_rubro,0) id_rubro, ");
			sql.append("\n m.fec_valor , m.fec_operacion, m.referencia, m.concepto, m.id_tipo_movto, ");
			
			if( ! divisa.equals("TDS") ){
				sql.append("m.importe");
			}else{
				sql.append("\n Case Trim(M.Id_Divisa ) When 'DLS' Then  m.importe * (Select Valor From Valor_Divisa Where Fec_Divisa = To_Date('"+fechaFin+"','dd/mm/yyyy') And Id_Divisa = 'DLS') Else m.importe End importe ");
			}
			
			sql.append("\n from movimiento m LEFT OUTER JOIN cat_rubro cr ON (m.id_grupo = cr.id_grupo AND m.id_rubro = cr.id_rubro), cat_cta_banco ccb, cat_banco ban ");
			sql.append("\n Where ccb.id_banco = Ban.id_banco ");
			sql.append("\n and ccb.id_banco = m.id_banco ");
			sql.append("\n and ccb.id_chequera = m.id_chequera ");
			sql.append("\n and id_estatus_mov <> 'X' ");

			switch (seleccion){
			case 1:
				sql.append("\n and ccb.id_banco in (" + idBancos + ") ");
				sql.append("\n and ccb.id_chequera in (" + idChequeras + ") ");
				break;
			case 2:
				sql.append("\n and ccb.id_banco in (" + idBancos + ") ");
				sql.append("\n and m.no_empresa in (" + noEmpresas + ") ");
				break;
			case 3:
				sql.append("\n and m.id_banco in (" + idBancos + ") ");
				break;
			case 4:
				sql.append("\n and m.no_empresa in (" + noEmpresas + ") ");
				break;
			}
			if( ! divisa.equals("TDS") ){
				sql.append("\n and m.id_divisa = '" + divisa + "'");
			}
			sql.append("\n and m.fec_valor  BETWEEN TO_DATE('"+ fechaIni +"','dd/mm/yyyy') AND TO_DATE('"+fechaFin+"','dd/mm/yyyy') "); 
			
			sql.append("\n UNION ALL");
			
			/*--- termina Movimiento_Bank ---*/
			sql.append("\n select ban.desc_banco, ccb.id_chequera, coalesce(cr.id_grupo,0) id_grupo, coalesce(cr.id_rubro,0) id_rubro, ");
			sql.append("\n m.fec_valor , m.fec_operacion,m.referencia, m.concepto,m.id_tipo_movto, ");
			if( ! divisa.equals("TDS") ){
				sql.append("m.importe");
			}else{
				sql.append("\n Case Trim(M.Id_Divisa ) When 'DLS' Then  m.importe * (Select Valor From Valor_Divisa Where Fec_Divisa = To_Date('"+fechaFin+"','dd/mm/yyyy') And Id_Divisa = 'DLS') Else m.importe End importe ");
			}
			sql.append("\n from hist_movimiento m LEFT OUTER JOIN cat_rubro cr ON (m.id_grupo = cr.id_grupo AND m.id_rubro = cr.id_rubro), cat_cta_banco ccb, cat_banco ban ");
			sql.append("\n Where ccb.id_banco = Ban.id_banco ");
			sql.append("\n and ccb.id_banco = m.id_banco ");
			sql.append("\n and ccb.id_chequera = m.id_chequera ");
			sql.append("\n and id_estatus_mov <> 'X' ");
			sql.append("\n and m.id_tipo_operacion in( 3200, 3701, 3120, 3101,3116, 1016, 1017, 1018, 1019, 1020, 1023, 3107, 3700, 3702, 3705,3706,3708,3709,3707,4001,4102,4103,4104,3500) ");
			
			 
			  switch (seleccion){
				case 1:
					sql.append("\n and ccb.id_banco in (" + idBancos + ") ");
					sql.append("\n and ccb.id_chequera in (" + idChequeras + ") ");
					break;
				case 2:
					sql.append("\n and ccb.id_banco in (" + idBancos + ") ");
					sql.append("\n and m.no_empresa in (" + noEmpresas + ") ");
					break;
				case 3:
					sql.append("\n and ccb.id_banco in (" + idBancos + ") ");
					break;
				case 4:
					sql.append("\n and m.no_empresa in (" + noEmpresas + ") ");
					break;
			}
			  if( ! divisa.equals("TDS") ){
			sql.append("\n and m.id_divisa = '" + divisa + "'");
			  }
			sql.append("\n and m.fec_valor  BETWEEN TO_DATE('"+ fechaIni +"','dd/mm/yyyy') AND TO_DATE('"+fechaFin+"','dd/mm/yyyy') ");
			
			sql.append("\n order by id_chequera  , id_grupo , id_rubro asc ");
			
			//System.out.println("Obtiene movtos diario tesoreria: \n" + sql.toString());
			
    		list= jdbcTemplate.query(sql.toString(), new RowMapper<PosicionBancariaDTO>(){
				public PosicionBancariaDTO mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDTO cons = new PosicionBancariaDTO();
					cons.setDescBanco(rs.getString("desc_banco"));
					cons.setIdChequera(rs.getString("id_chequera"));
					cons.setImporte(rs.getDouble("importe"));
					cons.setIdRubro(rs.getString("id_rubro"));
					cons.setIdGrupo(rs.getString("id_grupo"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setReferencia(rs.getString("referencia"));
					cons.setFecOperacion(funciones.ponerFechaSola(rs.getDate("fec_operacion")));
					cons.setFecValor(funciones.ponerFechaSola(rs.getDate("fec_valor")));
					cons.setIdTipoMovto(rs.getString("id_tipo_movto"));
					if (cons.getIdRubro().trim().equals("0")){
						if (cons.getIdTipoMovto().equals("I")){
							cons.setIdRubro("2");
							cons.setIdGrupo("2");
						}else{
							cons.setIdRubro("1");
							cons.setIdGrupo("1");
						}
						
					}
					return cons;
				}});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Consultas, C:PosicionBancariaDaoImpl, M:consultarDiarioTes");
		}
		return list;
	}

	@Override
	public List<PosicionBancariaDTO> obtieneCuentaEmpresa(String tipoMovto, String bancos, String chequeras, String empresas, int iTipoSelec, String fechaIni,
															String fechaFin, int seleccion, String divisa) {
		
		List<PosicionBancariaDTO> listFlujoEmp = new ArrayList<PosicionBancariaDTO>(); 
		StringBuffer sql = new StringBuffer();
		String sOcupaGrupo = "NO";
		
		try {
			if(iTipoSelec == 0) {
		        if(sOcupaGrupo.equals("NO")) {
		            sql.append(" SELECT '"+ tipoMovto +"' AS TIPO_MOVTO, E.NO_EMPRESA, E.NO_EMPRESA || ':' || E.NOM_EMPRESA AS NOM_EMPRESA, \n");
		            sql.append(" V.ID_DIVISA, V.FEC_VALOR, coalesce (CG.ID_GRUPO,0) ID_GRUPO, COALESCE (CG.DESC_GRUPO, ' ') DESC_GRUPO, \n");
		            if( ! divisa.equals("TDS") ){
						sql.append("SUM(V.IMPORTE) AS IMPORTE");
					}else{
						sql.append("\n Case Trim(v.Id_Divisa ) When 'DLS' Then  SUM(V.IMPORTE) * (Select Valor From Valor_Divisa Where Fec_Divisa = To_Date('"+fechaFin+"','dd/mm/yyyy') And Id_Divisa = 'DLS') Else SUM(V.IMPORTE) End importe ");
					}
		            sql.append(" FROM MOVIMIENTO V LEFT OUTER JOIN CAT_RUBRO CR ON (V.id_grupo = CR.id_grupo AND V.id_rubro = CR.id_rubro) ");
		            sql.append(" LEFT OUTER JOIN CAT_GRUPO CG ON (V.id_grupo = CG.id_grupo), EMPRESA E \n");
		            sql.append(" WHERE V.NO_EMPRESA=E.NO_EMPRESA \n");
		            sql.append(" AND ID_BANCO IN (" + bancos + ") \n");
		            sql.append(" AND ID_CHEQUERA IN (" + chequeras + ") \n");
		            sql.append(" AND V.ID_TIPO_MOVTO = '" + tipoMovto + "' \n");
		        }
		        
		        if(tipoMovto.equals("I")) {
		            //sql.append(" AND ID_TIPO_OPERACION IN (3100,3107,3101,3102,3103,3700,3701,3705,3706,3708,3709,1020,3120,4102,4103,3500) \n");
		        	sql.append("AND ((ID_TIPO_OPERACION IN (3100,3107,3101,3102,3103,3700,3701,3705,3706,3708,3709,1020,3120,4102,4103,3500) \n");
		        	sql.append("AND ID_ESTATUS_MOV = 'A') or (ID_TIPO_OPERACION IN (3102,3103) \n");
		        	sql.append("AND ID_ESTATUS_MOV = 'P') OR  (ID_TIPO_OPERACION IN (3500) AND ID_ESTATUS_MOV = 'W')) \n");
		                    
		        }else if(tipoMovto.equals("E")) {
			            sql.append(" AND ((ID_TIPO_OPERACION IN (3200) \n");
			            sql.append(" AND ID_ESTATUS_MOV IN ('T','K','I','P','Z')) OR \n");
			            sql.append(" (ID_TIPO_OPERACION IN (4001,1012,1016,1017,1018,3022,1019,1020,4104,3700,3701,3705,3706,3708,3709,1019) \n");
			            sql.append(" AND ID_ESTATUS_MOV = 'A')) \n");
		        }
		        
		        if( ! divisa.equals("TDS") ){
		        sql.append(" AND V.ID_DIVISA = '"+ divisa +"' \n");
		        }
		        
		        sql.append(" AND FEC_VALOR BETWEEN TO_DATE('"+ fechaIni +"','dd/mm/yyyy') AND TO_DATE('"+ fechaFin +"','dd/mm/yyyy') \n");
		        
		        /*--- movimiento_bank (inicia) ---*/
		        sql.append(" GROUP BY E.NO_EMPRESA, E.NO_EMPRESA || ':' || E.NOM_EMPRESA, V.ID_DIVISA, V.FEC_VALOR, CG.ID_GRUPO, CG.DESC_GRUPO \n");
		        sql.append(" UNION ALL ");
		        if(sOcupaGrupo.equals("NO")) {
		            sql.append(" SELECT '"+ tipoMovto +"' AS TIPO_MOVTO, E.NO_EMPRESA, E.NO_EMPRESA || ':' || E.NOM_EMPRESA AS NOM_EMPRESA, \n");
		            sql.append(" V.ID_DIVISA, V.FEC_VALOR, coalesce (CG.ID_GRUPO,0) ID_GRUPO, COALESCE (CG.DESC_GRUPO, ' ') DESC_GRUPO, \n");
		            if( ! divisa.equals("TDS") ){
						sql.append("SUM(V.IMPORTE) AS IMPORTE");
					}else{
						sql.append("\n Case Trim(v.Id_Divisa ) When 'DLS' Then  SUM(V.IMPORTE) * (Select Valor From Valor_Divisa Where Fec_Divisa = To_Date('"+fechaFin+"','dd/mm/yyyy') And Id_Divisa = 'DLS') Else SUM(V.IMPORTE) End importe ");
					}
		            sql.append(" FROM MOVIMIENTO_BANK V LEFT OUTER JOIN CAT_RUBRO CR ON (V.id_grupo = CR.id_grupo AND V.id_rubro = CR.id_rubro) ");
		            sql.append(" LEFT OUTER JOIN CAT_GRUPO CG ON (V.id_grupo = CG.id_grupo), EMPRESA E \n");
		            sql.append(" WHERE V.NO_EMPRESA=E.NO_EMPRESA \n");
		            sql.append(" AND ID_BANCO IN (" + bancos + ") \n");
		            sql.append(" AND ID_CHEQUERA IN (" + chequeras + ") \n");
		            sql.append(" AND V.ID_TIPO_MOVTO = '" + tipoMovto + "' \n");
		        }
		        
			    sql.append(" AND ID_ESTATUS_MOV = 'A' \n");
		        
		        if( ! divisa.equals("TDS") ){
		        sql.append(" AND V.ID_DIVISA = '"+ divisa +"' \n");
		        }
		        
		        sql.append(" AND FEC_VALOR BETWEEN TO_DATE('"+ fechaIni +"','dd/mm/yyyy') AND TO_DATE('"+ fechaFin +"','dd/mm/yyyy') \n");
		        
		        /*--- movimiento_bank (termina) ---*/
		        
		        if(sOcupaGrupo.equals("NO")){
		            sql.append(" GROUP BY E.NO_EMPRESA, E.NO_EMPRESA || ':' || E.NOM_EMPRESA, V.ID_DIVISA, V.FEC_VALOR, CG.ID_GRUPO, CG.DESC_GRUPO \n");
		            //sql.append(" ORDER BY E.NO_EMPRESA, CG.ID_GRUPO \n");
		            sql.append(" ORDER BY TIPO_MOVTO, ID_GRUPO, NO_EMPRESA, FEC_VALOR ");
		        }else{
		        	sql.append(" GROUP BY E.NOM_EMPRESA,V.ID_DIVISA, V.FEC_VALOR \n");
		        }
		            
		    }else{
	            if(sOcupaGrupo.equals("NO")) {
	                sql.append(" SELECT '"+ tipoMovto +"' AS TIPO_MOVTO, E.NO_EMPRESA,V.BENEFICIARIO, V.ID_DIVISA, V.FEC_VALOR, \n");
	                if( ! divisa.equals("TDS") ){
						sql.append("SUM(V.IMPORTE) AS IMPORTE");
					}else{
						sql.append("\n Case Trim(v.Id_Divisa ) When 'DLS' Then  SUM(V.IMPORTE) * (Select Valor From Valor_Divisa Where Fec_Divisa = To_Date('"+fechaFin+"','dd/mm/yyyy') And Id_Divisa = 'DLS') Else SUM(V.IMPORTE) End importe ");
					}
	                sql.append(" FROM MOVIMIENTO V, CAT_RUBRO CR, CAT_GRUPO CG,EMPRESA E \n");
	                sql.append(" WHERE V.ID_RUBRO = CR.ID_RUBRO \n");
	                sql.append(" 	AND CG.ID_GRUPO = CR.ID_GRUPO \n");
	                sql.append(" 	AND V.NO_EMPRESA=E.NO_EMPRESA \n");
	                sql.append(" 	AND  CR.ID_GRUPO IN (SELECT DISTINCT R.ID_GRUPO \n");
	                sql.append("                      		FROM CAT_RUBRO R \n");
	                sql.append("                      		WHERE (INGRESO_EGRESO = '"+ tipoMovto +"' \n");
	                sql.append(" 							OR INGRESO_EGRESO IS NULL) \n");
	            }
		           
		        if(tipoMovto.equals("I")) {
		            if(sOcupaGrupo.equals("NO"))
		                sql.append("                      AND R.ID_GRUPO IN (0)) \n");
		            else
		                sql.append("                      AND R.ID_GRUPO IN ('TCBC','TCIC','VTPB','ITPB','VTPP', 'ITPP', 'VTGU', 'ITGU','TCPI','TCFA','TCIE')) \n");
		        }else if(tipoMovto.equals("E")) {

			            if(sOcupaGrupo.equals("NO"))
			            	sql.append("                      AND R.ID_GRUPO IN (0)) \n");
			            else
			            	sql.append("                      AND R.ID_GRUPO IN ('TPBC','TPIC','INPB','INPP','INGU')) \n");
		        }
		        
	            if(!sOcupaGrupo.equals("NO"))
	                sql.append(" AND V.ID_GRUPO = CR.ID_GRUPO \n");
	            
                sql.append(" AND V.ID_RUBRO = CR.ID_RUBRO \n");
                sql.append(" AND V.ID_BANCO IN ("+ bancos +") \n");
                sql.append(" AND V.ID_CHEQUERA IN ("+ chequeras +") \n");
                sql.append(" AND V.ID_TIPO_MOVTO = '" + tipoMovto + "' \n");
                
                if(tipoMovto.equals("I")) {
		            sql.append(" AND V.ID_TIPO_OPERACION IN (1018,3700,3701,3022,1019,1020,3705,3706,3708,3709,3101,4102,4103,1020) \n"); //COMENTO ORR
                }else if(tipoMovto.equals("E")) {
		            sql.append(" AND V.ID_TIPO_OPERACION IN (3700,3701,4001,3101,3107,1019,1020,3705,3706,3708,3709,1018,4104,3200) \n");
                }
		        
                sql.append(" AND V.ID_ESTATUS_MOV IN( 'A','T', 'K') \n");
                		
		        //*******************after change divisa
                if( ! divisa.equals("TDS") ){
                sql.append(" AND V.ID_DIVISA = '"+ divisa +"' \n");
                }
		        
		        sql.append(" AND FEC_VALOR BETWEEN TO_DATE('"+ fechaIni +"','dd/mm/yyyy') AND TO_DATE('"+ fechaFin +"','dd/mm/yyyy') \n");
		        
		        /*--- movimiento_bank (inicia) ---*/
		        if(sOcupaGrupo.equals("NO")) {
		            sql.append(" GROUP BY E.NO_EMPRESA, E.NOM_EMPRESA,V.ID_DIVISA, V.FEC_VALOR ");
		        }else{
		            sql.append(" GROUP BY E.NOM_EMPRESA,V.ID_DIVISA, V.FEC_VALOR ");
		        }
		        sql.append(" UNION ALL ");
		        if(sOcupaGrupo.equals("NO")) {
	                sql.append(" SELECT '"+ tipoMovto +"' AS TIPO_MOVTO, E.NO_EMPRESA, ' ' AS BENEFICIARIO, V.ID_DIVISA, V.FEC_VALOR, \n");
	                if( ! divisa.equals("TDS") ){
						sql.append("SUM(V.IMPORTE) AS IMPORTE");
					}else{
						sql.append("\n Case Trim(v.Id_Divisa ) When 'DLS' Then  SUM(V.IMPORTE) * (Select Valor From Valor_Divisa Where Fec_Divisa = To_Date('"+fechaFin+"','dd/mm/yyyy') And Id_Divisa = 'DLS') Else SUM(V.IMPORTE) End importe ");
					}
	                sql.append(" FROM MOVIMIENTO_BANK V, CAT_RUBRO CR, CAT_GRUPO CG,EMPRESA E \n");
	                sql.append(" WHERE V.ID_RUBRO = CR.ID_RUBRO \n");
	                sql.append(" 	AND CG.ID_GRUPO = CR.ID_GRUPO \n");
	                sql.append(" 	AND V.NO_EMPRESA=E.NO_EMPRESA \n");
	                sql.append(" 	AND  CR.ID_GRUPO IN (SELECT DISTINCT R.ID_GRUPO \n");
	                sql.append("                      		FROM CAT_RUBRO R \n");
	                sql.append("                      		WHERE (INGRESO_EGRESO = '"+ tipoMovto +"' \n");
	                sql.append(" 							OR INGRESO_EGRESO IS NULL) \n");
	            }
		           
		        if(tipoMovto.equals("I")) {
		            if(sOcupaGrupo.equals("NO"))
		                sql.append("                      AND R.ID_GRUPO IN (0)) \n");
		            else
		                sql.append("                      AND R.ID_GRUPO IN ('TCBC','TCIC','VTPB','ITPB','VTPP', 'ITPP', 'VTGU', 'ITGU','TCPI','TCFA','TCIE')) \n");
		        }else if(tipoMovto.equals("E")) {

			            if(sOcupaGrupo.equals("NO"))
			            	sql.append("                      AND R.ID_GRUPO IN (0)) \n");
			            else
			            	sql.append("                      AND R.ID_GRUPO IN ('TPBC','TPIC','INPB','INPP','INGU')) \n");
		        }
		        
	            if(!sOcupaGrupo.equals("NO"))
	                sql.append(" AND V.ID_GRUPO = CR.ID_GRUPO \n");
	            
                sql.append(" AND V.ID_RUBRO = CR.ID_RUBRO \n");
                sql.append(" AND V.ID_BANCO IN ("+ bancos +") \n");
                sql.append(" AND V.ID_CHEQUERA IN ("+ chequeras +") \n");
                sql.append(" AND V.ID_TIPO_MOVTO = '" + tipoMovto + "' \n");
                sql.append(" AND V.ID_ESTATUS_MOV = 'A' \n");
                		
		        //*******************after change divisa
                if( ! divisa.equals("TDS") ){
                sql.append(" AND V.ID_DIVISA = '"+ divisa +"' \n");
                }
		        
		        sql.append(" AND FEC_VALOR BETWEEN TO_DATE('"+ fechaIni +"','dd/mm/yyyy') AND TO_DATE('"+ fechaFin +"','dd/mm/yyyy') \n");
		        
		        /*--- movimiento_bank (termina) ---*/
		        
		        if(sOcupaGrupo.equals("NO")) {
		            sql.append(" GROUP BY E.NO_EMPRESA, E.NOM_EMPRESA,V.ID_DIVISA, V.FEC_VALOR ");
		        }else{
		            sql.append(" GROUP BY E.NOM_EMPRESA,V.ID_DIVISA, V.FEC_VALOR ");
		        }
		    }
			
			//System.out.println("Consulta XEmp: " + sql.toString());
			
			listFlujoEmp = jdbcTemplate.query(sql.toString(), new RowMapper<PosicionBancariaDTO>(){
				public PosicionBancariaDTO mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDTO cons = new PosicionBancariaDTO();
					cons.setImporte(rs.getDouble("IMPORTE"));
					cons.setNoEmpresa(rs.getInt("NO_EMPRESA"));
					cons.setNomEmpresa(rs.getString("NOM_EMPRESA"));
					cons.setIdDivisa(rs.getString("ID_DIVISA"));
					cons.setFecValor(rs.getString("FEC_VALOR"));
					cons.setIdGrupo(rs.getString("ID_GRUPO"));
					cons.setDescGrupo(rs.getString("DESC_GRUPO"));
					cons.setIdTipoMovto(rs.getString("TIPO_MOVTO"));
					return cons;
				}});	
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Flujos, C:PosicionBancariaDaoImpl, M:obtieneCuentaEmpresa");
		}
		return listFlujoEmp;
	}
	
	public List<PosicionBancariaDTO> obtieneSaldodiario(String bancos, String ctas, String fecha, int noEmpresa, String sDivisa) {
		List<PosicionBancariaDTO> listFlujoEmp = new ArrayList<PosicionBancariaDTO>(); 
		StringBuffer sql = new StringBuffer();
		Date fechaDate = funciones.ponerFechaDate(fecha);
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy(); 
		
		try {
			
			
			
			if( ! sDivisa.equals("TDS") ){
				sql.append(" SELECT COALESCE(SUM(SALDO_INICIAL),0) AS SALDO \n");
			}else{
				sql.append("\n SELECT SUM(Case Trim(ccb.Id_Divisa ) When 'DLS' Then  ccb.SALDO_INICIAL * (Select Valor From Valor_Divisa Where Fec_Divisa = To_Date('"+fecha+"','dd/mm/yyyy') And Id_Divisa = 'DLS') Else ccb.SALDO_INICIAL End) SALDO");
			}
			
			if(fechaDate.compareTo(fechaHoy) == 0)
				sql.append(" FROM CAT_CTA_BANCO ccb\n");
			else
				sql.append(" FROM HIST_CAT_CTA_BANCO ccb\n");
			
			sql.append(" WHERE ID_BANCO IN (" + bancos + ") \n");
			
			if(!ctas.equals("")){
				sql.append(" 	AND ID_CHEQUERA = '"+ ctas +"' \n"); //Una cuenta
			}else if(noEmpresa != 0){
				sql.append(" 	AND NO_EMPRESA = "+ noEmpresa +" \n");
			}
			if( ! sDivisa.equals("TDS") ){
				sql.append(" 	AND ID_DIVISA = '" + sDivisa + "' \n");
			}
			
			if(fechaDate.compareTo(fechaHoy) != 0) {
				sql.append(" AND FEC_VALOR BETWEEN TO_DATE('"+ fecha +"','dd/mm/yyyy') and TO_DATE('"+ fecha +"','dd/mm/yyyy') \n");
			}
			
			////System.out.println(sql.toString());
			
			listFlujoEmp = jdbcTemplate.query(sql.toString(), new RowMapper<PosicionBancariaDTO>(){
				public PosicionBancariaDTO mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDTO cons = new PosicionBancariaDTO();
					cons.setImporte(rs.getDouble("SALDO"));
					return cons;
				}});	
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaDaoImpl, M:obtieneSaldodiario");
		}
		return listFlujoEmp;
	}
	
	@Override
	public List<PosicionBancariaDTO> obtieneSaldoFinal(String chequera, String fecha, String divisa) {
		List<PosicionBancariaDTO> listFlujoEmp = new ArrayList<PosicionBancariaDTO>(); 
		StringBuffer sql = new StringBuffer();
		Date fechaDate = funciones.ponerFechaDate(fecha);
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Date fechaHoy = consultasGenerales.obtenerFechaHoy(); 
		
		try {
			
			if(fechaDate.compareTo(fechaHoy) != 0){
				if( ! divisa.equals("TDS") ){
					sql.append("\n SELECT COALESCE(SUM(SALDO_FINAL),0) AS SALDO ");
				}else{
					sql.append("\n SELECT Case Trim(ccb.Id_Divisa ) When 'DLS' Then  ccb.saldo_final * (Select Valor From Valor_Divisa Where Fec_Divisa = To_Date('"+fecha+"','dd/mm/yyyy') And Id_Divisa = 'DLS') Else ccb.saldo_final End SALDO");
				}
				
				sql.append("\n FROM hist_cat_cta_banco ");
				sql.append("\n 	WHERE ID_CHEQUERA = '"+ chequera +"' ");
				sql.append("\n 	AND FEC_VALOR = TO_DATE('"+ fecha +"','dd/mm/yyyy') ");
				sql.append("\n 	AND ID_DIVISA = '" + divisa + "' ");
			}else{
				if( ! divisa.equals("TDS") ){
					sql.append("\n SELECT saldo_final AS SALDO ");
				}else{
					sql.append("\n SELECT Case Trim(ccb.Id_Divisa ) When 'DLS' Then  ccb.saldo_final * (Select Valor From Valor_Divisa Where Fec_Divisa = To_Date('"+fecha+"','dd/mm/yyyy') And Id_Divisa = 'DLS') Else ccb.saldo_final End SALDO");
				}
				sql.append("\n FROM cat_cta_banco ccb , empresa e, cat_banco cb ");
				sql.append("\n WHERE ccb.no_empresa = e.no_empresa ");
				sql.append("\n AND ccb.id_banco = cb.id_banco ");
				if( ! divisa.equals("TDS") ){
				sql.append("\n AND ccb.id_divisa = '" + divisa + "' ");
				}
				sql.append("\n AND ccb.id_chequera = '" + chequera + "' ");
			}
	
			//System.out.println("Obtiene saldo Final: "+ sql.toString());
			
			listFlujoEmp = jdbcTemplate.query(sql.toString(), new RowMapper<PosicionBancariaDTO>(){
				public PosicionBancariaDTO mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDTO cons = new PosicionBancariaDTO();
					cons.setImporte(rs.getDouble("SALDO"));
					return cons;
				}});	
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaDaoImpl, M:obtieneSaldodiario");
		}
		return listFlujoEmp;
	}
	
	@Override
	public List<PosicionBancariaDTO> obtieneFlujoEmp(String bancos, String chequeras) {
		List<PosicionBancariaDTO> listFlujoEmp = new ArrayList<PosicionBancariaDTO>(); 
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append("\n SELECT distinct e.no_empresa, e.nom_empresa ");
			sql.append("\n FROM empresa e, cat_cta_banco c ");
			sql.append(" WHERE e.no_empresa = c.no_empresa \n");
			sql.append(" 	And c.id_banco IN ("+ bancos +") \n");
			sql.append(" 	And c.id_chequera IN ("+ chequeras +") \n");
			sql.append(" ORDER BY e.no_empresa \n");
			
			//sql.append(" ORDER BY e.no_empresa \n");
			
			listFlujoEmp = jdbcTemplate.query(sql.toString(), new RowMapper<PosicionBancariaDTO>(){
				public PosicionBancariaDTO mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDTO cons = new PosicionBancariaDTO();
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setNomEmpresa(rs.getString("nom_empresa"));
					return cons;
				}});	
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaDaoImpl, M:obtieneFlujoEmp");
		}
		return listFlujoEmp;
	}
	
	@Override
	public List<PosicionBancariaDTO> obtenerCuentas(String bancos, String empresas, int iSelec, final boolean nomEmpresa) {
		List<PosicionBancariaDTO> listCtas = new ArrayList<PosicionBancariaDTO>(); 
		StringBuffer sql = new StringBuffer();
		
		try {
			if(nomEmpresa){
				sql.append(" SELECT DISTINCT ID_BANCO, ID_CHEQUERA, CCB.NO_EMPRESA, E.NOM_EMPRESA \n");
			}else{
				sql.append(" SELECT DISTINCT ID_BANCO, ID_CHEQUERA, CCB.NO_EMPRESA \n");
			}
			
			sql.append(" FROM CAT_CTA_BANCO CCB, EMPRESA E \n");
			sql.append(" WHERE CCB.NO_EMPRESA = E.NO_EMPRESA \n");
			
			switch (iSelec){
				/*case 1:
					sql.append("\n and c.id_banco in (" + bancos + ") ");
					sql.append("\n and c.id_chequera in (" + chequeras + ") ");
					break;*/
				case 2:
					sql.append("\n and CCB.id_banco in (" + bancos + ") ");
					sql.append("\n and CCB.no_empresa in (" + empresas + ") ");
					break;
				case 3:
					sql.append("\n and CCB.id_banco in (" + bancos + ") ");
					break;
				case 4:
					sql.append("\n and CCB.no_empresa in (" + empresas + ") ");
					break;
	        }
			
			sql.append(" ORDER BY CCB.NO_EMPRESA, ID_BANCO \n");
			
			listCtas = jdbcTemplate.query(sql.toString(), new RowMapper<PosicionBancariaDTO>(){
				public PosicionBancariaDTO mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDTO cons = new PosicionBancariaDTO();
					cons.setIdBanco(rs.getInt("ID_BANCO"));
					cons.setIdChequera(rs.getString("ID_CHEQUERA"));
					cons.setNoEmpresa(rs.getInt("NO_EMPRESA"));
					
					if(nomEmpresa){
						cons.setNomEmpresa(rs.getString("NOM_EMPRESA"));
					}
					
					return cons;
				}});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaDaoImp, M:obtenerCuentas");
		}
		return listCtas;
	}
	
	@Override
	public List<PosicionBancariaDTO> obtenerCuentas(String bancos, String chequeras, String empresas, int iSelec, final boolean nomEmpresa) {
		List<PosicionBancariaDTO> listCtas = new ArrayList<PosicionBancariaDTO>(); 
		StringBuffer sql = new StringBuffer();
		
		try {
			if(nomEmpresa){
				sql.append(" SELECT DISTINCT ID_BANCO, ID_CHEQUERA, CCB.NO_EMPRESA, E.NOM_EMPRESA \n");
			}else{
				sql.append(" SELECT DISTINCT ID_BANCO, ID_CHEQUERA, CCB.NO_EMPRESA \n");
			}
			
			sql.append(" FROM CAT_CTA_BANCO CCB, EMPRESA E \n");
			sql.append(" WHERE CCB.NO_EMPRESA = E.NO_EMPRESA \n");
			
			switch (iSelec){
				case 1:
					sql.append("\n and CCB.id_banco in (" + bancos + ") ");
					sql.append("\n and CCB.id_chequera in (" + chequeras + ") ");
					break;
				case 2:
					sql.append("\n and CCB.id_banco in (" + bancos + ") ");
					sql.append("\n and CCB.no_empresa in (" + empresas + ") ");
					break;
				case 3:
					sql.append("\n and CCB.id_banco in (" + bancos + ") ");
					break;
				case 4:
					sql.append("\n and CCB.no_empresa in (" + empresas + ") ");
					break;
	        }
			
			sql.append(" ORDER BY CCB.NO_EMPRESA, ID_BANCO \n");
			
			listCtas = jdbcTemplate.query(sql.toString(), new RowMapper<PosicionBancariaDTO>(){
				public PosicionBancariaDTO mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDTO cons = new PosicionBancariaDTO();
					cons.setIdBanco(rs.getInt("ID_BANCO"));
					cons.setIdChequera(rs.getString("ID_CHEQUERA"));
					cons.setNoEmpresa(rs.getInt("NO_EMPRESA"));
					
					if(nomEmpresa){
						cons.setNomEmpresa(rs.getString("NOM_EMPRESA"));
					}
					
					return cons;
				}});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaDaoImp, M:obtenerCuentas");
		}
		return listCtas;
	}
	
	@Override
	public List<PosicionBancariaDTO> obtenerNombreEmpresas(String empresas) {
		List<PosicionBancariaDTO> listCtas = new ArrayList<PosicionBancariaDTO>(); 
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT DISTINCT E.NO_EMPRESA, E.NOM_EMPRESA \n");
			sql.append(" FROM EMPRESA E \n");
			sql.append(" WHERE E.NO_EMPRESA in (" + empresas + ") \n");
			sql.append(" ORDER BY CCB.NO_EMPRESA \n");
			
			listCtas = jdbcTemplate.query(sql.toString(), new RowMapper<PosicionBancariaDTO>(){
				public PosicionBancariaDTO mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDTO cons = new PosicionBancariaDTO();
					cons.setNoEmpresa(rs.getInt("NO_EMPRESA"));
					cons.setNomEmpresa(rs.getString("NOM_EMPRESA"));
					return cons;
				}});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaDaoImp, M:obtenerNombreEmpresa");
		}
		return listCtas;
	}
	
	public List<PosicionBancariaDTO> obtieneFichaCuenta(String sTipoMovto, String sBancos, String ctas, int iTipoSelec, String fechaIni, 
														String fechaFin, int seleccion,String divisa) {
		StringBuffer sql = new StringBuffer();
		String psOcupaGrupo = "NO";
		//String ctasPreliminar = "";
		List<PosicionBancariaDTO> listFlujoEmp = new ArrayList<PosicionBancariaDTO> ();
		boolean chkSaldos = false;
		
		try {
			if(iTipoSelec == 0) {
		        if(psOcupaGrupo.equals("NO")) {
		            sql.append(" SELECT V.NO_EMPRESA, CR.ID_GRUPO, V.ID_BANCO, \n");
		            sql.append(" V.ID_CHEQUERA, CG.DESC_GRUPO, V.FEC_VALOR, \n");
		            if( ! divisa.equals("TDS") ){
						sql.append("SUM(V.IMPORTE) AS IMPORTE");
					}else{
						sql.append("\n SUM(Case Trim(v.Id_Divisa ) When 'DLS' Then  V.IMPORTE * (Select Valor From Valor_Divisa Where Fec_Divisa = To_Date('"+fechaFin+"','dd/mm/yyyy') And Id_Divisa = 'DLS') Else V.IMPORTE End) importe ");
					}
		            sql.append(" FROM MOVIMIENTO V, CAT_RUBRO CR, CAT_GRUPO CG \n");
		            sql.append(" WHERE V.ID_RUBRO = CR.ID_RUBRO \n");
		            sql.append(" AND V.ID_GRUPO = CR.ID_GRUPO \n");
		            sql.append(" AND CG.ID_GRUPO = CR.ID_GRUPO \n");
		            sql.append(" AND CG.ID_GRUPO IN (SELECT DISTINCT R.ID_GRUPO \n");
		            sql.append("                     FROM CAT_RUBRO R, CAT_GRUPO G \n");
		            sql.append("                     WHERE G.ID_GRUPO = R.ID_GRUPO \n");
		            //sql.append("                     AND R.ID_GRUPO NOT IN ('TCBC','TCIC') \n");
		            sql.append("                     AND R.ID_GRUPO NOT IN (0) \n");
		            sql.append("                     AND (INGRESO_EGRESO = '" + sTipoMovto + "' \n");
		            sql.append(" OR INGRESO_EGRESO IS NULL)) \n");
		            sql.append(" AND ID_BANCO IN (" + sBancos + ") \n");
		            sql.append(" AND ID_CHEQUERA IN (" + ctas + ") \n");
		            sql.append(" AND ID_TIPO_MOVTO = '" + sTipoMovto + "' \n");
		        }else {
		            sql.append(" SELECT V.NO_EMPRESA, V.ID_GRUPO, V.ID_BANCO, \n");
		            sql.append(" V.ID_CHEQUERA, CG.DESC_GRUPO, V.FEC_VALOR, \n");
		            if( ! divisa.equals("TDS") ){
						sql.append("SUM(V.IMPORTE) AS IMPORTE");
					}else{
						sql.append("\n SUM(Case Trim(v.Id_Divisa ) When 'DLS' Then  V.IMPORTE * (Select Valor From Valor_Divisa Where Fec_Divisa = To_Date('"+fechaFin+"','dd/mm/yyyy') And Id_Divisa = 'DLS') Else V.IMPORTE) End importe ");
					}
		            sql.append(" FROM MOVIMIENTO V, CAT_RUBRO CR, CAT_GRUPO CG \n");
		            sql.append(" WHERE V.ID_RUBRO = CR.ID_RUBRO \n");
		            sql.append(" AND CG.ID_GRUPO = CR.ID_GRUPO \n");
		            sql.append(" AND CG.ID_GRUPO IN (SELECT DISTINCT R.ID_GRUPO \n");
		            sql.append("                     FROM CAT_RUBRO R, CAT_GRUPO G \n");
		            sql.append("                     WHERE G.ID_GRUPO = R.ID_GRUPO \n");
		            //sql.append("                     AND R.ID_GRUPO NOT IN ('TCBC','TCIC') \n");
		            sql.append("                     AND R.ID_GRUPO NOT IN ('TCBC','TCIC','VTPB','ITPB','INPB','TPIC','INPP', 'INGU', 'ITGU','VTGU','TCPI', 'TPBC','TCFA','TCIE') \n");
		            sql.append("                     AND (INGRESO_EGRESO = '" + sTipoMovto + "' \n");
		            sql.append(" OR INGRESO_EGRESO IS NULL)) \n");
		            sql.append(" AND V.ID_GRUPO = CR.ID_GRUPO \n");
		            sql.append(" AND ID_BANCO IN (" + sBancos + ") \n");
		            sql.append(" AND ID_CHEQUERA IN (" + ctas + ") \n");
		            sql.append(" AND ID_TIPO_MOVTO = '" + sTipoMovto + "' \n");
		        }
		        
		        if(sTipoMovto.equals("I")) {
		        	
		        	//sql.append(" AND ID_TIPO_OPERACION IN (3101,3107,3115,3154,3500) \n");
		        	sql.append(" AND ((ID_TIPO_OPERACION IN (3100,3107,3101,3102,3103,3700,3701,3705,3706,3708,3709,1020,3120,4102,4103,3500) \n");
		        	sql.append(" AND (ID_ESTATUS_MOV = 'A' or ID_ESTATUS_MOV = 'W' )) or (ID_TIPO_OPERACION IN (3102,3103) \n");
		        	sql.append(" AND ID_ESTATUS_MOV = 'P')) \n");
		                    
		            if(chkSaldos) {
		                sql.append(" AND ORIGEN_MOV NOT IN ('BCE') \n");
		            }
		        }else if(sTipoMovto.equals("E")) {
		            sql.append(" AND ((ID_TIPO_OPERACION IN (3200) \n");
		            sql.append(" AND ID_ESTATUS_MOV IN ('T','K','I','P','Z')) OR \n");
		            //sql.append(" (ID_TIPO_OPERACION IN (4001,1012,1016,1017,1018,3022,1019,1020,4104,3700,3701,3705,3706,3708,3709) \n");
		            sql.append(" (ID_TIPO_OPERACION IN (4001,1012,1016,1017,1018,3022,1019,1020,4104,3700,3701,3705,3706,3708,3709,1019) \n");
		            		
		            //'CHANGE OSCAR 1
		            sql.append(" AND ID_ESTATUS_MOV = 'A')) \n");
		            
		            if(chkSaldos) {
		                sql.append(" AND ORIGEN_MOV NOT IN ('BCE') \n");
		            }
		            
		        }
		        /*
		        '*******************before change divisa
		        'sql.append(" AND LTRIM(RTRIM(ID_DIVISA)) IN (SELECT ID_DIVISA FROM CAT_CTA_BANCO \n");
		        'sql.append("                                 WHERE ID_BANCO IN (" & pqBancos & ") \n");
		        'sql.append("                                 AND ID_CHEQUERA IN (" & pqCuentas & ")) \n");
		        '*******************before change divisa
		        */
		        //'*******************after change divisa
		        if( ! divisa.equals("TDS") ){
		        sql.append(" AND ID_DIVISA = '" + divisa + "' \n");
		        }
		        //'*******************after change divisa
		        
		        sql.append(" AND FEC_VALOR BETWEEN TO_DATE('"+fechaIni+"', 'dd/mm/yyyy') AND TO_DATE('"+fechaFin+"', 'dd/mm/yyyy') \n");
		        
		        /*--- MOVIMIENTO_BANK (inicia) ---*/
		        if(psOcupaGrupo.equals("NO")) {
		            sql.append(" GROUP BY V.NO_EMPRESA, CR.ID_GRUPO, V.ID_BANCO, V.ID_CHEQUERA, CG.DESC_GRUPO, V.FEC_VALOR ");
		        }else {
		            sql.append(" GROUP BY V.NO_EMPRESA, V.ID_GRUPO, V.ID_BANCO, V.ID_CHEQUERA, CG.DESC_GRUPO, V.FEC_VALOR ");
		        }
		        sql.append(" UNION ALL ");
		        if(psOcupaGrupo.equals("NO")) {
		            sql.append(" SELECT V.NO_EMPRESA, CR.ID_GRUPO, V.ID_BANCO, \n");
		            sql.append(" V.ID_CHEQUERA, CG.DESC_GRUPO, V.FEC_VALOR, \n");
		            if( ! divisa.equals("TDS") ){
						sql.append("SUM(V.IMPORTE) AS IMPORTE");
					}else{
						sql.append("\n SUM(Case Trim(v.Id_Divisa ) When 'DLS' Then  V.IMPORTE * (Select Valor From Valor_Divisa Where Fec_Divisa = To_Date('"+fechaFin+"','dd/mm/yyyy') And Id_Divisa = 'DLS') Else V.IMPORTE End) importe ");
					}
		            sql.append(" FROM MOVIMIENTO_BANK V, CAT_RUBRO CR, CAT_GRUPO CG \n");
		            sql.append(" WHERE V.ID_RUBRO = CR.ID_RUBRO \n");
		            sql.append(" AND V.ID_GRUPO = CR.ID_GRUPO \n");
		            sql.append(" AND CG.ID_GRUPO = CR.ID_GRUPO \n");
		            sql.append(" AND CG.ID_GRUPO IN (SELECT DISTINCT R.ID_GRUPO \n");
		            sql.append("                     FROM CAT_RUBRO R, CAT_GRUPO G \n");
		            sql.append("                     WHERE G.ID_GRUPO = R.ID_GRUPO \n");
		            //sql.append("                     AND R.ID_GRUPO NOT IN ('TCBC','TCIC') \n");
		            sql.append("                     AND R.ID_GRUPO NOT IN (0) \n");
		            sql.append("                     AND (INGRESO_EGRESO = '" + sTipoMovto + "' \n");
		            sql.append(" 						  OR INGRESO_EGRESO IS NULL)) \n");
		            sql.append(" AND ID_BANCO IN (" + sBancos + ") \n");
		            sql.append(" AND ID_CHEQUERA IN (" + ctas + ") \n");
		            sql.append(" AND ID_TIPO_MOVTO = '" + sTipoMovto + "' \n");
		        }else {
		            sql.append(" SELECT V.NO_EMPRESA, V.ID_GRUPO, V.ID_BANCO, \n");
		            sql.append(" V.ID_CHEQUERA, CG.DESC_GRUPO, V.FEC_VALOR, \n");
		            if( ! divisa.equals("TDS") ){
						sql.append("SUM(V.IMPORTE) AS IMPORTE");
					}else{
						sql.append("\n SUM(Case Trim(v.Id_Divisa ) When 'DLS' Then  V.IMPORTE * (Select Valor From Valor_Divisa Where Fec_Divisa = To_Date('"+fechaFin+"','dd/mm/yyyy') And Id_Divisa = 'DLS') Else V.IMPORTE) End importe ");
					}
		            sql.append(" FROM MOVIMIENTO_BANK V, CAT_RUBRO CR, CAT_GRUPO CG \n");
		            sql.append(" WHERE V.ID_RUBRO = CR.ID_RUBRO \n");
		            sql.append(" AND CG.ID_GRUPO = CR.ID_GRUPO \n");
		            sql.append(" AND CG.ID_GRUPO IN (SELECT DISTINCT R.ID_GRUPO \n");
		            sql.append("                     FROM CAT_RUBRO R, CAT_GRUPO G \n");
		            sql.append("                     WHERE G.ID_GRUPO = R.ID_GRUPO \n");
		            sql.append("                     AND R.ID_GRUPO NOT IN ('TCBC','TCIC','VTPB','ITPB','INPB','TPIC','INPP', 'INGU', 'ITGU','VTGU','TCPI', 'TPBC','TCFA','TCIE') \n");
		            sql.append("                     AND (INGRESO_EGRESO = '" + sTipoMovto + "' \n");
		            sql.append(" OR INGRESO_EGRESO IS NULL)) \n");
		            sql.append(" AND V.ID_GRUPO = CR.ID_GRUPO \n");
		            sql.append(" AND V.ID_BANCO IN (" + sBancos + ") \n");
		            sql.append(" AND V.ID_CHEQUERA IN (" + ctas + ") \n");
		            sql.append(" AND V.ID_TIPO_MOVTO = '" + sTipoMovto + "' \n");
		        }
		        sql.append(" AND V.ID_ESTATUS_MOV = 'A' \n");

		        if( ! divisa.equals("TDS") ){
		        	sql.append(" AND V.ID_DIVISA = '" + divisa + "' \n");
		        }
		        //'*******************after change divisa
		        
		        sql.append(" AND FEC_VALOR BETWEEN TO_DATE('"+fechaIni+"', 'dd/mm/yyyy') AND TO_DATE('"+fechaFin+"', 'dd/mm/yyyy') \n");
		        /*--- MOVIMIENTO_BANK (termina) ---*/
		        
		        if(psOcupaGrupo.equals("NO")) {
		            sql.append(" GROUP BY V.NO_EMPRESA, CR.ID_GRUPO, V.ID_BANCO, V.ID_CHEQUERA, CG.DESC_GRUPO, V.FEC_VALOR ");
		        }else {
		            sql.append(" GROUP BY V.NO_EMPRESA, V.ID_GRUPO, V.ID_BANCO, V.ID_CHEQUERA, CG.DESC_GRUPO, V.FEC_VALOR ");
		        }
		    }else {
	            if(psOcupaGrupo.equals("NO")) {
	                sql.append(" SELECT V.NO_EMPRESA, CR.ID_GRUPO, \n");
	                sql.append(" V.ID_CHEQUERA, CG.DESC_GRUPO,V.FEC_VALOR, \n");
	                if( ! divisa.equals("TDS") ){
						sql.append("SUM(V.IMPORTE) AS IMPORTE");
					}else{
						sql.append("\n Case Trim(v.Id_Divisa ) When 'DLS' Then  SUM(V.IMPORTE) * (Select Valor From Valor_Divisa Where Fec_Divisa = To_Date('"+fechaFin+"','dd/mm/yyyy') And Id_Divisa = 'DLS') Else SUM(V.IMPORTE) End importe ");
					}
	                sql.append(" FROM MOVIMIENTO V, CAT_RUBRO CR, CAT_GRUPO CG \n");
	                sql.append(" WHERE CR.ID_RUBRO = CR.ID_RUBRO \n");
	                sql.append(" AND CG.ID_GRUPO = CR.ID_GRUPO \n");
	                sql.append(" AND  CR.ID_GRUPO IN (SELECT DISTINCT R.ID_GRUPO \n");
	                sql.append("                      FROM CAT_RUBRO R \n");
	                sql.append("                      WHERE (INGRESO_EGRESO = '" + sTipoMovto + "' \n");
	                sql.append(" OR INGRESO_EGRESO IS NULL) \n");
	            }else {
	                sql.append(" SELECT V.NO_EMPRESA, V.ID_GRUPO, \n");
	                sql.append(" V.ID_CHEQUERA, CG.DESC_GRUPO,V.FEC_VALOR, \n");
	                if( ! divisa.equals("TDS") ){
						sql.append("SUM(V.IMPORTE) AS IMPORTE");
					}else{
						sql.append("\n Case Trim(v.Id_Divisa ) When 'DLS' Then  SUM(V.IMPORTE) * (Select Valor From Valor_Divisa Where Fec_Divisa = To_Date('"+fechaFin+"','dd/mm/yyyy') And Id_Divisa = 'DLS') Else SUM(V.IMPORTE) End importe ");
					}
	                sql.append(" FROM MOVIMIENTO V, CAT_RUBRO CR, CAT_GRUPO CG \n");
	                sql.append(" WHERE V.ID_RUBRO = CR.ID_RUBRO \n");
	                sql.append(" AND V.ID_GRUPO = CR.ID_GRUPO \n");
	                sql.append(" AND CG.ID_GRUPO = CR.ID_GRUPO \n");
	                sql.append("  AND CG.ID_GRUPO = V.ID_GRUPO  \n");
	                sql.append(" AND  V.ID_GRUPO IN (SELECT DISTINCT R.ID_GRUPO \n");
	                sql.append("                      FROM CAT_RUBRO R \n");
	                sql.append("                      WHERE (INGRESO_EGRESO = '" + sTipoMovto + "' \n");
	                sql.append(" OR INGRESO_EGRESO IS NULL) \n");
	        
	            }
		        if(sTipoMovto.equals("I")) {
		            
		            if(psOcupaGrupo.equals("NO")) {
		            	sql.append("                      AND R.ID_GRUPO IN (0)) \n");
		            }else {
		                sql.append("                      AND R.ID_GRUPO IN ('TCBC','TCIC','VTPB','ITPB','VTPP', 'ITPP', 'VTGU', 'ITGU','TCPI','TCFA','TCIE')) \n");
		            }
		            //'CHANGE OSCAR 1
		        }else if(sTipoMovto.equals("E")) {
		           
		            if(psOcupaGrupo.equals("NO")) {
		            	sql.append("                      AND R.ID_GRUPO IN (0)) \n");
		            }else {
		            	sql.append("                      AND R.ID_GRUPO IN ('TPBC','TPIC','INPB','INPP','INGU')) \n");
		            }

		        }
		        
		            if(psOcupaGrupo.equals("NO")) {
		                sql.append("  ");
		            }else {
		                sql.append(" AND V.ID_GRUPO = CR.ID_GRUPO \n");
		            }
		                sql.append(" AND V.ID_RUBRO = CR.ID_RUBRO \n");
		                sql.append(" AND V.ID_BANCO IN (" + sBancos + ") \n");
		                sql.append(" AND V.ID_CHEQUERA IN (" + ctas + ") \n");
		                sql.append(" AND V.ID_TIPO_MOVTO = '" + sTipoMovto + "' \n");
		                            
		                            
		        if(sTipoMovto.equals("I")) {
		        	//sql.append(" AND V.ID_TIPO_OPERACION IN (3101,3107,3115,3154,3500) \n");
		        	sql.append(" AND V.ID_TIPO_OPERACION IN (1018,3700,3701,3022,1019,1020,3705,3706,3708,3709,3101,4102,4103,1020,3500) \n");
		        }else if(sTipoMovto.equals("E")) {
		            sql.append(" AND V.ID_TIPO_OPERACION IN (3700,3701,4001,3101,3107,1019,1020,3705,3706,3708,3709,1018,4104,3200) \n");
		        }
		        sql.append(" AND V.ID_ESTATUS_MOV IN( 'A','T', 'K','W') \n");
		        
		        //'*******************after change divisa
		        if( ! divisa.equals("TDS") ){
		        sql.append(" AND RTRIM(LTRIM(V.ID_DIVISA)) IN ('" + divisa + "') \n");
		        }
		        //'*******************after change divisa
		        
		        sql.append(" AND FEC_VALOR BETWEEN TO_DATE('"+fechaIni+"', 'dd/mm/yyyy') AND TO_DATE('"+fechaFin+"', 'dd/mm/yyyy') \n");
		        
		        /*--- MOVIMIENTO_BANK (inicia) ---*/
		        if(psOcupaGrupo.equals("NO")) {
		            sql.append(" GROUP BY V.NO_EMPRESA, CR.ID_GRUPO,  V.ID_CHEQUERA,  CG.DESC_GRUPO,V.FEC_VALOR ");
		        }else {
		            sql.append(" GROUP BY V.NO_EMPRESA, V.ID_GRUPO,  V.ID_CHEQUERA,  CG.DESC_GRUPO,V.FEC_VALOR ");
		        }
		        sql.append(" UNION ALL \n");
		        if(psOcupaGrupo.equals("NO")) {
	                sql.append(" SELECT V.NO_EMPRESA, CR.ID_GRUPO, \n");
	                sql.append(" V.ID_CHEQUERA, CG.DESC_GRUPO,V.FEC_VALOR, \n");
	                if( ! divisa.equals("TDS") ){
						sql.append("SUM(V.IMPORTE) AS IMPORTE");
					}else{
						sql.append("\n Case Trim(v.Id_Divisa) When 'DLS' Then  SUM(V.IMPORTE) * (Select Valor From Valor_Divisa Where Fec_Divisa = To_Date('"+fechaFin+"','dd/mm/yyyy') And Id_Divisa = 'DLS') Else SUM(V.IMPORTE) End importe ");
					}
	                sql.append(" FROM MOVIMIENTO_BANK V, CAT_RUBRO CR, CAT_GRUPO CG \n");
	                sql.append(" WHERE CR.ID_RUBRO = CR.ID_RUBRO \n");
	                sql.append(" AND CG.ID_GRUPO = CR.ID_GRUPO \n");
	                sql.append(" AND  CR.ID_GRUPO IN (SELECT DISTINCT R.ID_GRUPO \n");
	                sql.append("                      FROM CAT_RUBRO R \n");
	                sql.append("                      WHERE (INGRESO_EGRESO = '" + sTipoMovto + "' \n");
	                sql.append(" OR INGRESO_EGRESO IS NULL) \n");
	            }else {
	                sql.append(" SELECT V.NO_EMPRESA, V.ID_GRUPO, \n");
	                sql.append(" V.ID_CHEQUERA, CG.DESC_GRUPO, V.FEC_VALOR, \n");
	                if( ! divisa.equals("TDS") ){
						sql.append("SUM(V.IMPORTE) AS IMPORTE");
					}else{
						sql.append("\n Case Trim(v.Id_Divisa ) When 'DLS' Then  SUM(V.IMPORTE) * (Select Valor From Valor_Divisa Where Fec_Divisa = To_Date('"+fechaFin+"','dd/mm/yyyy') And Id_Divisa = 'DLS') Else SUM(V.IMPORTE) End importe ");
					}
	                sql.append(" FROM MOVIMIENTO_BANK V, CAT_RUBRO CR, CAT_GRUPO CG \n");
	                sql.append(" WHERE V.ID_RUBRO = CR.ID_RUBRO \n");
	                sql.append(" AND V.ID_GRUPO = CR.ID_GRUPO \n");
	                sql.append(" AND CG.ID_GRUPO = CR.ID_GRUPO \n");
	                sql.append("  AND CG.ID_GRUPO = V.ID_GRUPO  \n");
	                sql.append(" AND  V.ID_GRUPO IN (SELECT DISTINCT R.ID_GRUPO \n");
	                sql.append("                      FROM CAT_RUBRO R \n");
	                sql.append("                      WHERE (INGRESO_EGRESO = '" + sTipoMovto + "' \n");
	                sql.append(" OR INGRESO_EGRESO IS NULL) \n");
	        
	            }
		        if(sTipoMovto.equals("I")) {
		            
		            if(psOcupaGrupo.equals("NO")) {
		            	sql.append("                      AND R.ID_GRUPO IN (0)) \n");
		            }else {
		                sql.append("                      AND R.ID_GRUPO IN ('TCBC','TCIC','VTPB','ITPB','VTPP', 'ITPP', 'VTGU', 'ITGU','TCPI','TCFA','TCIE')) \n");
		            }
		            //'CHANGE OSCAR 1
		        }else if(sTipoMovto.equals("E")) {
		           
		            if(psOcupaGrupo.equals("NO")) {
		            	sql.append("                      AND R.ID_GRUPO IN (0)) \n");
		            }else {
		            	sql.append("                      AND R.ID_GRUPO IN ('TPBC','TPIC','INPB','INPP','INGU')) \n");
		            }

		        }
		        
	            if(psOcupaGrupo.equals("NO")) {
	                sql.append("  ");
	            }else {
	                sql.append(" AND V.ID_GRUPO = CR.ID_GRUPO \n");
	            }
                sql.append(" AND V.ID_RUBRO = CR.ID_RUBRO \n");
                sql.append(" AND V.ID_BANCO IN (" + sBancos + ") \n");
                sql.append(" AND V.ID_CHEQUERA IN (" + ctas + ") \n");
                sql.append(" AND V.ID_TIPO_MOVTO = '" + sTipoMovto + "' \n");
                sql.append(" AND V.ID_ESTATUS_MOV = 'A' \n");
		        
		        //'*******************after change divisa
		        if( ! divisa.equals("TDS") ){
		        sql.append(" AND RTRIM(LTRIM(V.ID_DIVISA)) IN ('" + divisa + "') \n");
		        }
		        //'*******************after change divisa
		        
		        sql.append(" AND FEC_VALOR BETWEEN TO_DATE('"+fechaIni+"', 'dd/mm/yyyy') AND TO_DATE('"+fechaFin+"', 'dd/mm/yyyy') \n");
		        /*--- MOVIMIENTO_BANK (termina) ---*/
		        
		        if(psOcupaGrupo.equals("NO")) {
		            sql.append(" GROUP BY V.NO_EMPRESA, CR.ID_GRUPO,  V.ID_CHEQUERA,  CG.DESC_GRUPO,V.FEC_VALOR ");
		        }else {
		            sql.append(" GROUP BY V.NO_EMPRESA, V.ID_GRUPO,  V.ID_CHEQUERA,  CG.DESC_GRUPO,V.FEC_VALOR ");
		        }

		    }

			//EMS 20/04/2016 - De momento siempre es falso
			
			/*if(chkSaldos) {
				if(sTipoMovto.equals("I")) {
					ctasPreliminar = obtieneFichaCuentaPreliminar("E", sBancos, ctas, 0, parametros);
					sql.append(" \n UNION ALL \n");
					sql.append(ctasPreliminar);
				}else if(sTipoMovto.equals("I")) {
					ctasPreliminar = obtieneFichaCuentaPreliminar("E", sBancos, ctas, 0, parametros);
					sql.append(" \n UNION ALL \n");
					sql.append(ctasPreliminar);
					ctasPreliminar = obtieneFichaCuentaPreliminarDLS("E", sBancos, ctas, 0);
					sql.append(" \n UNION ALL \n");
					sql.append(ctasPreliminar);
				}
			}*/
			
			sql.append(" ORDER BY ID_GRUPO, NO_EMPRESA, ID_CHEQUERA ");
			
			//System.out.println("sql valor x cta: " + sql.toString());
			
			listFlujoEmp = jdbcTemplate.query(sql.toString(), new RowMapper<PosicionBancariaDTO>(){
				public PosicionBancariaDTO mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDTO cons = new PosicionBancariaDTO();
					cons.setImporte(rs.getDouble("IMPORTE"));
					cons.setIdGrupo(rs.getString("ID_GRUPO"));
					cons.setIdBanco(rs.getInt("ID_BANCO"));
					cons.setIdChequera(rs.getString("ID_CHEQUERA"));
					cons.setDescGrupo(rs.getString("DESC_GRUPO"));
					cons.setFecValor(rs.getString("FEC_VALOR"));
					cons.setNoEmpresa(rs.getInt("NO_EMPRESA"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaDaoImpl, M:obtieneFichaCuenta");
		}
		return listFlujoEmp;
	}
	
	@Override
	public Map<String, String> insertarPlantilla(String bancos, String chequeras, String empresas, String orden,
												 String nomPlantilla, String consulta, 
												 String fechaIni, String fechaFin, 
												 int seleccion, String divisa, int folio){

		Map<String, String> result = new HashMap<>();
		result.put("error", "");
		result.put("msgUsuario", "");
		
		StringBuffer sql = new StringBuffer();
		int afec = 0;
		
		try {
			sql.append("\n INSERT INTO USUARIO_PLANTILLA");
			sql.append("\n (ID_USUARIO, NOMBRE_PLANTILLA, PANTALLA, BOTON, QUERY_USUARIO1, QUERY_USUARIO2, FOLIO_PLANTILLA, ");
			sql.append("\n FEC_INICIO,FEC_FIN,SELECCION,VER_EN,DATOS_SELECCIONADOS,DIVISA,DESPLAZAMIENTO,DATOS_SELECCIONADOS2,orden) ");
			sql.append("\n values ("+GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+" ");
			sql.append("\n 		   ,'" +nomPlantilla+ "','PosicionBancaria','" +consulta+ "','','',"+folio+",'" +fechaIni+ "','" +fechaFin+ "'," + seleccion + ",0, ");
			
			switch (seleccion) {
			case 1:
				sql.append("\n '"+ bancos + "','" + divisa+ "',0,'" +chequeras+ "','"+orden+"') ");
				break;
			case 2:
				sql.append("\n '"+ bancos + "','" + divisa+ "',0,'" +empresas+ "','"+orden+"') ");
				break;
			case 3:
				sql.append("\n '"+ bancos + "','" + divisa+ "',0,'','"+orden+"') ");
				break;
			case 4:
				sql.append("\n '"+ empresas + "','" + divisa+ "',0,'','"+orden+"') ");
				break;
			}
			
			afec = jdbcTemplate.update(sql.toString());
			
			if(afec == 0){
				result.put("error", "Error al guardar la plantilla");
			}else{
				result.put("msgUsuario", "Plantilla guardada con éxito.");
			}
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaDaoImp, M:insertarPlantilla");
			result.put("error", "Error al guardar la plantilla");
		}
		return result;
	}
		    
	@Override
	public int obtenerFolioPlantilla(){

		int folio = 0;
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append("\n SELECT COALESCE(MAX(FOLIO_PLANTILLA),0)+1 AS FOLIO FROM USUARIO_PLANTILLA ");
			
			folio = jdbcTemplate.queryForInt(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaDaoImp, M:obtenerFolioPlantilla");
		}
		return folio;
	}
	
	@Override
	public UsuarioPlantilla obtenerPlantilla(int idPlantilla) {
		
		StringBuffer sql = new StringBuffer();
		List<UsuarioPlantilla> listRes = new ArrayList<UsuarioPlantilla>();
		
		try{
			
			sql.append("\n SELECT DISTINCT ID_USUARIO, NOMBRE_PLANTILLA, BOTON, FOLIO_PLANTILLA, FEC_INICIO, ");
			sql.append("\n 		  FEC_FIN, SELECCION, VER_EN, DATOS_SELECCIONADOS, DIVISA, DATOS_SELECCIONADOS2, ORDEN ");
			sql.append("\n FROM USUARIO_PLANTILLA ");
			sql.append("\n WHERE PANTALLA = 'PosicionBancaria' ");
			sql.append("\n AND FOLIO_PLANTILLA = "+idPlantilla+" ");
			
			//System.out.println("query consulta OBTIENE PLANTILLA : " + sql.toString());
			
			listRes = jdbcTemplate.query(sql.toString(), new RowMapper<UsuarioPlantilla>() {
				public UsuarioPlantilla mapRow(ResultSet rs, int idx) throws SQLException {
					UsuarioPlantilla plantilla = new UsuarioPlantilla();
					plantilla.setIdUsuario(rs.getInt("id_usuario"));
					plantilla.setNombre(rs.getString("nombre_plantilla"));
					plantilla.setBoton(rs.getString("boton"));
					plantilla.setFolio(rs.getInt("folio_plantilla"));
					plantilla.setFechaInicio(rs.getString("fec_inicio"));
					plantilla.setFechaFin(rs.getString("fec_fin"));
					plantilla.setSeleccion(rs.getInt("seleccion"));
					plantilla.setDatos1(rs.getString("datos_seleccionados"));
					plantilla.setDivisa(rs.getString("divisa"));
					plantilla.setDatos2(rs.getString("datos_seleccionados2"));
					plantilla.setOrden(rs.getString("orden"));
					return plantilla;
				}
			});
			
			if(listRes.size()>0)
				return listRes.get(0);
				
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:consultarCriterioSeleccion");
		}
		return null;
	}
	
	@Override
	public List<PosicionBancariaDTO> obtenCuentasEmpresa(int seleccion, String bancos, 
														String chequeras, String empresas,
														String divisa) {
		
		StringBuffer sql = new StringBuffer();
		List<PosicionBancariaDTO> listRes = new ArrayList<PosicionBancariaDTO>();
		
		try{
			
			sql.append("\n SELECT DISTINCT ban.desc_banco, hist.id_chequera,  ");
			sql.append("\n 		  			(Select nom_empresa from empresa where hist.no_empresa = no_empresa) as empresa , ");
			sql.append("\n 					hist.id_divisa ");
			sql.append("\n from cat_cta_banco hist, Cat_banco ban ");
			sql.append("\n WHERE hist.id_banco = Ban.id_banco ");
			
			switch (seleccion){
			case 1:
				sql.append("\n and hist.id_banco in (" + bancos + ") ");
				sql.append("\n and hist.id_chequera in (" + chequeras + ") ");
				break;
			case 2:
				sql.append("\n and hist.id_banco in (" + bancos + ") ");
				sql.append("\n and hist.no_empresa in (" + empresas + ") ");
				break;
			case 3:
				sql.append("\n and hist.id_banco in (" + bancos + ") ");
				break;
			case 4:
				sql.append("\n and hist.no_empresa in (" + empresas + ") ");
				break;
			}
			if( ! divisa.equals("TDS") ){

			sql.append("\n and hist.id_divisa = '" + divisa + "' ");
			
			}
			sql.append("\n ORDER BY desc_banco, empresa");
		    
			//System.out.println("query consulta chequeras/empresa saldos : " + sql.toString());
			
			listRes = jdbcTemplate.query(sql.toString(), new RowMapper<PosicionBancariaDTO>() {
				public PosicionBancariaDTO mapRow(ResultSet rs, int idx) throws SQLException {
					PosicionBancariaDTO pos = new PosicionBancariaDTO();
					pos.setDescBanco(rs.getString("desc_banco"));
					pos.setIdChequera(rs.getString("id_chequera"));
					pos.setNomEmpresa(rs.getString("empresa"));
					pos.setIdDivisa(rs.getString("id_divisa"));
					return pos;
				}
			});
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:obtenCuentasEmpresa");
		}
		return listRes;
	}
	
	@Override
	public Map<String, String> eliminarPlantilla(int idPlantilla) {
		
		StringBuffer sql = new StringBuffer();
		int cont = 0;
		
		Map<String, String> result = new HashMap<>();
		result.put("error", "");
		result.put("msgUsuario", "");
		
		try{
			
			sql.append("\n DELETE FROM USUARIO_PLANTILLA ");
			sql.append("\n WHERE FOLIO_PLANTILLA = "+idPlantilla+" ");
		    
			cont = jdbcTemplate.update(sql.toString());
			
			if(cont > 0){
				result.put("msgUsuario", "Plantilla eliminada con éxito.");
			}else{
				result.put("error", "Error al eliminar la plantilla");
			}
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:PosicionBancariaDaoImp, M:eliminarPlantilla");
		}
		return result;
	}
	@Override
	public UsuarioPlantilla obtenerPlantilla(String nombrePlantilla) {
		
		StringBuffer sql = new StringBuffer();
		List<UsuarioPlantilla> listRes = new ArrayList<UsuarioPlantilla>();
		
		try{
			
			sql.append("\n SELECT DISTINCT ID_USUARIO, NOMBRE_PLANTILLA, BOTON, FOLIO_PLANTILLA, FEC_INICIO, ");
			sql.append("\n 		  FEC_FIN, SELECCION, VER_EN, DATOS_SELECCIONADOS, DIVISA, DATOS_SELECCIONADOS2, ORDEN ");
			sql.append("\n FROM USUARIO_PLANTILLA ");
			sql.append("\n WHERE PANTALLA = 'PosicionBancaria' ");
			sql.append("\n AND NOMBRE_PLANTILLA = '"+ nombrePlantilla + "'");
			
			//System.out.println("query consulta OBTIENE PLANTILLA : " + sql.toString());
			
			listRes = jdbcTemplate.query(sql.toString(), new RowMapper<UsuarioPlantilla>() {
				public UsuarioPlantilla mapRow(ResultSet rs, int idx) throws SQLException {
					UsuarioPlantilla plantilla = new UsuarioPlantilla();
					plantilla.setIdUsuario(rs.getInt("id_usuario"));
					plantilla.setNombre(rs.getString("nombre_plantilla"));
					plantilla.setBoton(rs.getString("boton"));
					plantilla.setFolio(rs.getInt("folio_plantilla"));
					plantilla.setFechaInicio(rs.getString("fec_inicio"));
					plantilla.setFechaFin(rs.getString("fec_fin"));
					plantilla.setSeleccion(rs.getInt("seleccion"));
					plantilla.setDatos1(rs.getString("datos_seleccionados"));
					plantilla.setDivisa(rs.getString("divisa"));
					plantilla.setDatos2(rs.getString("datos_seleccionados2"));
					plantilla.setOrden(rs.getString("orden"));
					return plantilla;
				}
			});
			
			if(listRes.size()>0)
				return listRes.get(0);
				
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:consultarCriterioSeleccion");
		}
		return null;
	}
	
	
	
	
	
}
