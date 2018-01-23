package com.webset.set.utilerias.dao.impl;

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

import com.webset.set.traspasos.dto.GrupoDTO;
import com.webset.set.traspasos.dto.RubroDTO;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dao.CodigosDao;
import com.webset.set.utileriasmod.dto.CodigosDto;

public class CodigosDaoImpl implements CodigosDao{
	Bitacora bitacora;
	JdbcTemplate jdbcTemplate;
	String sql = "";
	
	
	public List<CodigosDto> llenaComboEmpresas(){
		List<CodigosDto> listaResultado = new ArrayList<CodigosDto>();
		sql = "";
		
		try{
			sql = "Select * from empresa ";
			
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql, new RowMapper<CodigosDto>(){
				public CodigosDto mapRow(ResultSet rs, int idx)throws SQLException{
					CodigosDto campos = new CodigosDto();
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setNomEmpresa(rs.getString("nom_empresa"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosDaoImpl, M: llenaComboEmpresas");
		}return listaResultado;
	}
	
	
	public List<CodigosDto> llenaGrid(int noEmpresa){
		List<CodigosDto> listaResultado = new ArrayList<CodigosDto>();
		sql = "";
		
		try{
			sql = "Select * from cat_codigo ";
			if (noEmpresa != 0)
				sql += " \n where no_empresa = "+ noEmpresa +" ";
				
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql, new RowMapper<CodigosDto>(){
				public CodigosDto mapRow(ResultSet rs, int idx)throws SQLException{
					CodigosDto campos = new CodigosDto();
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setIdCodigo(rs.getString("id_codigo"));
					campos.setDescCodigo(rs.getString("desc_codigo"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosDaoImpl, M: llenaGrid");
		}return listaResultado;
	}
	
	
	public int eliminaCodigo(int idRenglon, int idRubro ,String codigo){
		int resultado = 0;
		StringBuffer sql = new StringBuffer();

		switch (codigo) {
		case "grupo":
			sql.append("DELETE FROM CAT_GRUPO \n");
			sql.append("WHERE ID_GRUPO = " + idRenglon +  "\n");
			
			//eliminarAsignacion(idRenglon, idRubro);
			
			break;
		case "rubro":
			sql.append("DELETE FROM CAT_RUBRO \n");
			sql.append("WHERE ID_GRUPO = "+idRenglon+" AND ID_RUBRO = "+idRubro+" \n");
			break;
			
		}
		
//		sql = "Delete from cat_codigo ";
//		sql += "\n where no_empresa = "+ noEmpresa +" ";
//		sql += "\n and id_codigo = '"+ idCodigo +"' ";
	
		try{
			System.out.println(sql);
			resultado = jdbcTemplate.update(sql.toString());
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosDaoImpl, M: eliminaCodigo");
		}
		return resultado;
	}
	
	
	public void eliminarAsignacion(int idRenglon, int idRubro){
		StringBuffer sb = new StringBuffer();
		
		sb.append("DELETE FROM ASIGNACION_POLIZA_GRUPO \n");
		sb.append("WHERE ID_POLIZA = "+ idRubro +" AND ID_GRUPO = "+idRenglon+ " \n");
		
		System.out.println(sb);
		try {
			jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Utilerias, C: CodigosDaoImpl, M: eliminarAsignacion");
		}
	}
	
	public List<CodigosDto> buscaCodigo (int noEmpresa, String idCodigo){
		List<CodigosDto> listaResultado = new ArrayList<CodigosDto>();
		sql = "";
		try{
			sql = "Select * from cat_codigo ";
			sql += "\n where no_empresa = "+ noEmpresa +" ";
			sql += "\n and id_codigo = '"+ idCodigo +"' ";
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql, new RowMapper<CodigosDto>(){
				public CodigosDto mapRow(ResultSet rs, int idx)throws SQLException{
					CodigosDto campos = new CodigosDto();
					campos.setIdCodigo(rs.getString("id_codigo"));
					campos.setDescCodigo(rs.getString("desc_codigo"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosDaoImpl, M: buscaCodigo");
		}return listaResultado;
	}
	
	
	public int insertaCodigo(int noEmpresa, String idCodigo, String descCodigo){
		int resultadoEntero = 0;
		sql = "";
		try{
			
			sql = "Insert into cat_codigo (no_empresa, id_codigo, desc_codigo) ";
			sql += "\n values ("+ noEmpresa +", '"+ idCodigo +"', '"+ descCodigo +"') ";
			
			System.out.println(sql);
			
			resultadoEntero = jdbcTemplate.update(sql);
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosDaoImpl, M:insertaCodigo");
		}return resultadoEntero;		
	}
	
	
	//******************************************************************************************************************************
	public void setDataSource(DataSource dataSource)
	{
		try
		{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C: EquivalenciaEmpresasDaoImpl, M:setDataSource");
		}
	}
	
	
	
	public int buscaGrupo(String idGrupo){
		
		List<CodigosDto> listaResultado = new ArrayList<CodigosDto>();
		int resultado = 0;
		
		sql = "";
		
		try{
			
			sql = "Select * from cat_grupo ";
			sql += "\n where id_grupo = "+ idGrupo +" ";			
			
			System.out.println(sql);
			System.out.println(sql);
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper<CodigosDto>(){
				
				public CodigosDto mapRow(ResultSet rs, int idx)throws SQLException{
					CodigosDto campos = new CodigosDto();
					campos.setIdCodigo(rs.getString("id_grupo"));
					campos.setDescCodigo(rs.getString("desc_grupo"));
					return campos;
				}
			});
		}
		
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosDaoImpl, M: buscaCodigo");
		}
		resultado = listaResultado.size();
		return resultado;
	}

	@Override
	public int modificarGrupo(String idGrupo, String descGrupo){
		StringBuffer sb = new StringBuffer();
		int resultadoEntero = 0;
		
		sb.append("UPDATE CAT_GRUPO SET \n");
		sb.append("DESC_GRUPO = '" + descGrupo + "' \n");
		sb.append("WHERE ID_GRUPO = " + idGrupo);
		//modificarAsignacion(idGrupo, idPoliza);
		System.out.println(sb);
		try {
			resultadoEntero = jdbcTemplate.update(sb.toString());
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P: Utilerias, C: CodigosDaoImpl, M: modificarGrupo");
		}
		
		return resultadoEntero;
	}
	
	public void modificarAsignacion(String idGrupo, String idPoliza){
		StringBuffer sb = new StringBuffer();
		
		sb.append("UPDATE ASIGNACION_POLIZA_GRUPO SET \n");
		sb.append("ID_POLIZA = "+idPoliza+" WHERE ID_GRUPO = "+ idGrupo +" \n");
		
		System.out.println(sb);
		try {
			
			jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P: Utilerias, C: CodigosDaoImpl, M: modificarAsignacion");
		}
	}
	
	
	@Override
	public int modificarRubro(String idGrupo, String idRubro, String descRubro, String ingresoEgreso) {
		StringBuffer sb = new StringBuffer();
		int resultadoEntero = 0;
		
		sb.append("UPDATE cat_rubro SET ID_GRUPO = "+idGrupo+", \n");
		sb.append("ID_RUBRO = "+idRubro+", DESC_RUBRO = '"+descRubro+"', \n");
		sb.append("INGRESO_EGRESO = '"+ingresoEgreso+"' \n");
		sb.append("WHERE ID_RUBRO = "+idRubro+" AND ID_GRUPO = "+idGrupo+" \n");
		System.out.println(sb.toString());
		try {
			
			resultadoEntero = jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P: Utilerias, C: CodigosDaoImpl, M: modificarRubro");
		}
		
		return resultadoEntero;
	}
	
	
	public int insertaGrupo(String idGrupo, String descGrupo){
		int resultadoEntero = 0;
		sql = "";
		try{
			
			sql = "Insert into cat_grupo (id_grupo, desc_grupo) ";
			sql += "\n values (" + idGrupo + ", '"+ descGrupo +"')";
			
			resultadoEntero = jdbcTemplate.update(sql);
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosDaoImpl, M:insertaCodigo");
		}return resultadoEntero;		
	}

	@Override
	public List<RubroDTO> getRubros( int idGrupo ) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();
				
		sb.append( " SELECT cr.id_rubro as id, cr.desc_rubro as describ, cg.id_grupo, cg.desc_grupo, cr.INGRESO_EGRESO as ie \n");
		sb.append( " FROM cat_rubro cr\n");
		sb.append( "                  left join cat_grupo cg\n");
		sb.append( "                  on  cg.id_grupo = cr.id_grupo\n");		
		if( idGrupo != 0 ){
			sb.append( " WHERE cr.id_grupo = ");		
			sb.append(   idGrupo );
		}
		
				
	    sql=sb.toString();
	    
	    //System.out.println(sql);
	    
		List <RubroDTO> rubros = null;
		
		System.out.println(sb);
		rubros = jdbcTemplate.query(sql, new RowMapper<RubroDTO>(){			
			public RubroDTO mapRow(ResultSet rs, int idx){				
				RubroDTO rubro = new RubroDTO();
					try {
						rubro.setDescRubro( rs.getString("describ") );
						rubro.setIdRubro( rs.getString("id") );
						rubro.setIdGrupo( rs.getString("id_grupo") );
						rubro.setDescGrupo(rs.getString("desc_grupo"));
						rubro.setIngresoEgreso(rs.getString("ie"));
					} catch (SQLException e) {
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
								+ "P:Traspasos, C:TraspasosDao, M:llenarComboRubro");
					}
				return rubro;				
		}});
			
		return rubros;

	}

	@Override
	public int buscaRubro(String idGrupo, String idRubro) {
		
		List<CodigosDto> listaResultado = new ArrayList<CodigosDto>();
		int resultado = 0;
		
		sql = "";
		
		try{
			
			sql = "Select * from cat_rubro ";
			sql += "\n where id_grupo = "+ idGrupo +" ";			
			sql += "\n and id_rubro = "+ idRubro +" ";
			
			System.out.println(sql);
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper<CodigosDto>(){
				
				public CodigosDto mapRow(ResultSet rs, int idx)throws SQLException{
					CodigosDto campos = new CodigosDto();
					campos.setIdCodigo(rs.getString("id_rubro"));
					campos.setDescCodigo(rs.getString("desc_rubro"));
					return campos;
				}
			});
		}
		
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosDaoImpl, M: buscaCodigo");
		}
		
		resultado = listaResultado.size();
		
		return resultado;

	}

	@Override
	public int insertaRubro(String idGrupo, String idRubro, String descRubro,
			String ingresoEgreso) {
		int resultadoEntero = 0;
		
		sql = "";
		try{
			
			sql = "Insert into cat_rubro (id_grupo, id_rubro, desc_rubro, ingreso_egreso, PRESENTAR) ";
			sql += "\n values (" + idGrupo + ", "+ idRubro + ", '" + descRubro +"', '" + ingresoEgreso +"', 'NO' )";
			
			resultadoEntero = jdbcTemplate.update(sql);
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosDaoImpl, M:insertaCodigo");
		}return resultadoEntero;		

	}

	@Override
	public List<RubroDTO> getGuiasContables(int noEmpresa) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();
				
		sb.append( " SELECT gc.no_empresa, gc.id_grupo, gc.id_rubro, gc.cuenta_contable,");     sb.append( "\n" );                                          
		sb.append( " e.nom_empresa, cg.desc_grupo, cr.desc_rubro, cr.ingreso_egreso");          sb.append( "\n" );																
		sb.append( " FROM guia_contable gc");                              						sb.append( "\n" ); 
		sb.append( "                  left join empresa e");               						sb.append( "\n" );
		sb.append( "                  on  gc.no_empresa = e.no_empresa");  						sb.append( "\n" );
		sb.append( "                  left join cat_grupo cg");            						sb.append( "\n" );
		sb.append( "                  on  gc.id_grupo = cg.id_grupo");     						sb.append( "\n" );
		sb.append( "                  left join cat_rubro cr");            						sb.append( "\n" );
		sb.append( "                  on  gc.id_rubro = cr.id_rubro");     						sb.append( "\n" );
		
		if( noEmpresa != 0 ){
			sb.append( " WHERE gc.no_Empresa = ");		
			sb.append(   noEmpresa );
		}
		
				
	    sql=sb.toString();
	    
	    //System.out.println(sql);
	    
		List <RubroDTO> rubros = null;

		rubros = jdbcTemplate.query(sql, new RowMapper<RubroDTO>(){			
			public RubroDTO mapRow(ResultSet rs, int idx){				
				RubroDTO rubro = new RubroDTO();
					try {
						rubro.setDescRubro( rs.getString("desc_rubro") );
						rubro.setIdRubro( rs.getString("id_rubro") );
						rubro.setIdGrupo( rs.getString("id_grupo") );
						rubro.setDescGrupo(rs.getString("desc_grupo"));
						rubro.setNoEmpresa( rs.getInt("no_empresa"));
						rubro.setNomEmpresa(rs.getString("nom_empresa"));
						rubro.setCuentaContable(rs.getString("cuenta_contable"));
						rubro.setIngresoEgreso(rs.getString("ingreso_egreso"));
					} catch (SQLException e) {
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
								+ "P:Traspasos, C:TraspasosDao, M:llenarComboRubro");
					}
				return rubro;				
		}});
			
		return rubros;

	}

	@Override
	public int buscaGuiaContable(String noEmpresa, String idGrupo,
			String idRubro, String cuentaContable) {
		
		List<CodigosDto> listaResultado = new ArrayList<CodigosDto>();
		int resultado = 0;
		
		sql = "";
		
		try{
			
			sql = "Select * from guia_contable ";
			sql += "\n where id_grupo = "+ idGrupo +" ";			
			sql += "\n and id_rubro = "+ idRubro +" ";
			sql += "\n and no_empresa = "+ noEmpresa +" ";
			sql += "\n and cuenta_contable = '"+ cuentaContable + "'";
			
			System.out.println(sql);
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper<CodigosDto>(){
				
				public CodigosDto mapRow(ResultSet rs, int idx)throws SQLException{
					CodigosDto campos = new CodigosDto();
					campos.setIdCodigo(rs.getString("id_rubro"));					
					return campos;
				}
			});
		}
		
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosDaoImpl, M: buscaCodigo");
		}
		
		resultado = listaResultado.size();
		
		return resultado;

	}

	@Override
	public int insertaGuiaContable(String noEmpresa, String idGrupo,
			String idRubro, String cuentaContable) {
		
		int resultadoEntero = 0;
		
		sql = "";
		try{
			
			sql = "Insert into guia_contable ";
			sql += "\n values (" + noEmpresa + ", "+ idGrupo + ", " + idRubro +", '" + cuentaContable +"')";
			
			resultadoEntero = jdbcTemplate.update(sql);
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosDaoImpl, M:insertaCodigo");
		}return resultadoEntero;		

	}


	/*
	 * Luis Alfredo Serrato Montes de Oca
	 */
	
	@Override
	public List<CodigosDto> getPolizas() {
		List<CodigosDto> listaPolizas = new ArrayList<CodigosDto>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM zimp_polizamanual");
		
		try {
			
			listaPolizas = jdbcTemplate.query(sb.toString(), new RowMapper<CodigosDto>(){
				public CodigosDto mapRow(ResultSet rs, int idx)throws SQLException{
					CodigosDto poliza = new CodigosDto();
					poliza.setIdCodigo(rs.getString("ID_POLIZA"));
					poliza.setDescCodigo(rs.getString("NOMBRE_POLIZA"));
					return poliza;
				}
			});
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Utilerias, C: CodigosDaoImpl, M:getPolizas");
		}
		
		return listaPolizas;
	}


	@Override
	public String agregarPoliza(int idPoliza, String nombrePoliza) {
		String mensaje = "";
		StringBuffer sb = new StringBuffer();
		sb.append("INSERT INTO zimp_polizamanual VALUES( \n");
		sb.append(idPoliza + ", '"+nombrePoliza+"') \n");
		
		try {
			jdbcTemplate.update(sb.toString());
			mensaje = "Poliza creada con exito";
		} catch (Exception e) {
			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Utilerias, C: CodigosDaoImpl, M:agregarPoliza");
		}
		
		return mensaje;
	}
	
	@Override
	public int existePoliza(int idPoliza, String descPoliza){
		StringBuffer sb = new StringBuffer();
		int res = 0;
		sb.append("SELECT COUNT(*) FROM zimp_polizamanual \n");
		sb.append("WHERE ID_POLIZA = " + idPoliza + " \n");
		
		try {
			res = jdbcTemplate.queryForInt(sb.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Utilerias, C: CodigosDaoImpl, M:existePoliza");
		}
		
		return res;
	}
	
	
	@Override
	public String actualizarPoliza(int idPoliza, String nombrePoliza) {
		String mensaje = "";
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE zimp_polizamanual SET \n");
		sb.append("NOMBRE_POLIZA = '" + nombrePoliza + "' WHERE ID_POLIZA = "+ idPoliza +" \n");
		
		try {
			jdbcTemplate.update(sb.toString());
			mensaje = "Poliza modificada con exito";
		} catch (Exception e) {
			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Utilerias, C: CodigosDaoImpl, M:actualizarPoliza");
		}
		
		return mensaje;
	}
	
	@Override
	public String eliminarPoliza(int idPoliza){
		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM zimp_polizamanual WHERE \n");
		sb.append("ID_POLIZA = "+ idPoliza +" \n");
		
		try {
			jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Utilerias, C: CodigosDaoImpl, M:eliminarPoliza");
		}
		
		return "";
	}
	
//	@Override
//	public int insertarGrupoPoliza(String idGrupo, String idPoliza){
//		int resultadoEntero = 0;
//		sql = "";
//		try{
//			
//			sql = "Insert into ASIGNACION_POLIZA_GRUPO ";
//			sql += "\n values (" + idPoliza + ", "+ idGrupo +")";
//			
//			resultadoEntero = jdbcTemplate.update(sql);
//			
//		}
//		catch(Exception e){
//			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosDaoImpl, M:insertaCodigo");
//		}return resultadoEntero;		
//	}
	
	@Override
	public List<GrupoDTO> llenarComboGrupo(String idTipoGrupo) {
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT g.ID_GRUPO, DESC_GRUPO from cat_grupo g \n");
		//sb.append("join ASIGNACION_POLIZA_GRUPO p on g.ID_GRUPO = p.ID_GRUPO \n");
		//sb.append("JOIN zimp_polizamanual z on P.ID_POLIZA = z.ID_POLIZA \n");
				
		List <GrupoDTO> grupos = null;

		grupos = jdbcTemplate.query(sb.toString(), new RowMapper<GrupoDTO>(){			
			public GrupoDTO mapRow(ResultSet rs, int idx){				
				GrupoDTO grupo = new GrupoDTO();
					try {
						grupo.setDescGrupo(rs.getString("DESC_GRUPO"));
						grupo.setIdGrupo(rs.getInt("ID_GRUPO"));
						
					} catch (SQLException e) {
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
								+ "P:Utilerias, C:CodigosDaoImpl, M:llenarComboGrupo");
					}
				return grupo;				
		}});
			
		return grupos;
		
	}


	@Override
	public List<CodigosDto> obtenerPolizasSinAsignar(String idRubro){
		List<CodigosDto> listaPolizas = new ArrayList<CodigosDto>();
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT * FROM zimp_polizamanual WHERE ID_POLIZA NOT IN \n");
		sb.append("(SELECT ID_POLIZA FROM asignacion_poliza_grupo WHERE ID_GRUPO = "+idRubro+") \n");
		
		
		System.out.println(sb);
		try {
			
			listaPolizas = jdbcTemplate.query(sb.toString(), new RowMapper<CodigosDto>(){
				public CodigosDto mapRow(ResultSet rs, int idx)throws SQLException{
					CodigosDto poliza = new CodigosDto();
					poliza.setIdCodigo(rs.getString("ID_POLIZA"));
					poliza.setDescCodigo(rs.getString("NOMBRE_POLIZA"));
					return poliza;
				}
			});
			
		} catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:CodigosDaoImpl, M:obtenerPolizasSinAsignar");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:CodigosDaoImpl, M:obtenerPolizasSinAsignar");
		}
		
		return listaPolizas;
	}
	
	@Override
	public String asignarPolizas(String idPoliza, String idRubro){
		String mensaje = "";
		StringBuffer sb = new StringBuffer();
		sb.append("INSERT INTO asignacion_poliza_grupo VALUES( \n");
		sb.append(""+idPoliza+", "+idRubro+" )\n");
		System.out.println(sb.toString());
		try {
			jdbcTemplate.update(sb.toString());
			
		} catch (CannotGetJdbcConnectionException e){			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:CodigosDaoImpl, M:obtenerPolizasSinAsignar");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:CodigosDaoImpl, M:obtenerPolizasSinAsignar");
		}
		
		
		return mensaje;
	}
	
	@Override
	public String eliminarPolizas(String idPoliza, String idRubro){
		String mensaje = "";
		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM asignacion_poliza_grupo WHERE \n");
		sb.append("ID_POLIZA = '"+idPoliza+"' AND ID_GRUPO = '"+idRubro+"' \n");
		System.out.println(sb.toString());
		try {
			jdbcTemplate.update(sb.toString());
			
		} catch (CannotGetJdbcConnectionException e){			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:CodigosDaoImpl, M:obtenerPolizasSinAsignar");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:CodigosDaoImpl, M:obtenerPolizasSinAsignar");
		}
		
		
		return mensaje;
	}
	
	@Override
	public List<CodigosDto> obtenerPolizasAsignadas(String idRubro){
		List<CodigosDto> listaPolizas = new ArrayList<CodigosDto>();
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT * FROM zimp_polizamanual WHERE ID_POLIZA IN \n");
		sb.append("(SELECT ID_POLIZA FROM asignacion_poliza_grupo WHERE ID_GRUPO = '"+idRubro+"') \n");
		
		System.out.println(sb);
		try {
			
			listaPolizas = jdbcTemplate.query(sb.toString(), new RowMapper<CodigosDto>(){
				public CodigosDto mapRow(ResultSet rs, int idx)throws SQLException{
					CodigosDto poliza = new CodigosDto();
					poliza.setIdCodigo(rs.getString("ID_POLIZA"));
					poliza.setDescCodigo(rs.getString("NOMBRE_POLIZA"));
					return poliza;
				}
			});
			
		} catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:CodigosDaoImpl, M:obtenerPolizasAsignadas");
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:CodigosDaoImpl, M:obtenerPolizasAsignadas");
		}
		
		return listaPolizas;
	}


	@Override
	public List<Map<String, String>> reporteGrupos() {
		
		List<Map<String, String>> listaResultado = new ArrayList<Map<String, String>>();
		StringBuffer sql = new StringBuffer();
		try{
			
			sql.append("SELECT g.ID_GRUPO, DESC_GRUPO from cat_grupo g order by g.id_grupo "); 
			
			System.out.println("-----------------");
			System.out.println("imprime query");
			System.out.println("-----------------");
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>(){
				public Map<String, String> mapRow(ResultSet rs, int idx)throws SQLException{
					Map<String, String> campos = new HashMap<String, String>();

					campos.put("idGrupo", rs.getString("id_grupo"));
					campos.put("descGrupo", rs.getString("desc_grupo"));
					
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosDaoImpl, M: reporteGrupos");
		}return listaResultado;	
	}


	@Override
	public List<Map<String, String>> reporteGrupoPolizas() {
		List<Map<String, String>> listaResultado = new ArrayList<Map<String, String>>();
		StringBuffer sql = new StringBuffer();
		try{
			
		sql.append("SELECT * FROM zimp_polizamanual order by id_poliza \n");

			
			System.out.println("-----------------");
			System.out.println("imprime query");
			System.out.println("-----------------");
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>(){
				public Map<String, String> mapRow(ResultSet rs, int idx)throws SQLException{
					Map<String, String> campos = new HashMap<String, String>();

					campos.put("idCodigo", rs.getString("id_poliza"));
					campos.put("descCodigo", rs.getString("nombre_poliza"));
					
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosDaoImpl, M: reporteGrupoPoliza");
		}return listaResultado;	
	}


	@Override
	public List<Map<String, String>> reporteGrupoRubros() {

		List<Map<String, String>> listaResultado = new ArrayList<Map<String, String>>();
		StringBuffer sql = new StringBuffer();
		try{
			
		sql.append("SELECT cr.id_rubro as id, cr.desc_rubro as describ, cg.id_grupo, cg.desc_grupo,  \n");
		sql.append("cr.INGRESO_EGRESO as ie FROM cat_rubro cr left join cat_grupo cg \n");
		sql.append(" on  cg.id_grupo = cr.id_grupo order by cg.id_grupo \n");
		

			
			System.out.println("-----------------");
			System.out.println("imprime query");
			System.out.println("-----------------");
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>(){
				public Map<String, String> mapRow(ResultSet rs, int idx)throws SQLException{
					Map<String, String> campos = new HashMap<String, String>();
					
					campos.put("idRubro", rs.getString("id"));
					campos.put("descRubro", rs.getString("describ"));
					campos.put("idGrupo", rs.getString("id_grupo"));
					campos.put("descGrupo", rs.getString("desc_grupo"));
					campos.put("ingresoEgreso", rs.getString("ie"));
					
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosDaoImpl, M: reporteGrupoRubros");
		}return listaResultado;	
		
	}






}
