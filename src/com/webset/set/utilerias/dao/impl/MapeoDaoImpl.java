package com.webset.set.utilerias.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dao.MapeoDao;
import com.webset.set.utilerias.dto.MapeoDto;
import com.webset.utils.tools.Utilerias;

public class MapeoDaoImpl implements MapeoDao{
	String sql = "";
private static Logger logger = Logger.getLogger(MapeoDao.class);

	
	Funciones funciones = new Funciones();
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();


	public List<MapeoDto> llenaGrid(){

		List<MapeoDto> listaResultado = new ArrayList<MapeoDto>();
		StringBuffer sql= new StringBuffer();
		try{
			sql.append("Select * from mapeosav");
			
			System.out.println("-----------------");
			System.out.println("imprime query");
			System.out.println("-----------------");
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MapeoDto>(){
				public MapeoDto mapRow(ResultSet rs, int idx)throws SQLException{
					MapeoDto campos = new MapeoDto();
					
					campos.setSecuencia(rs.getInt("secuencia"));
					campos.setIdPoliza(rs.getString("poliza"));
					campos.setIdGrupo(rs.getString("grupo"));
					campos.setIdRubro(rs.getString("rubro"));
					campos.setIdBanco(rs.getString("banco"));
					campos.setReferencia(rs.getString("referencia"));
					campos.setConcepto(rs.getString("concepto"));
					campos.setDescripcion(rs.getString("descripcion"));
					campos.setObservacion(rs.getString("observacion"));
					campos.setTipo(rs.getString("tipo"));
					campos.setEspecial(rs.getString("especial"));
					campos.setActivo(rs.getString("activo"));
					
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: MapeoDaoImpl, M: llenaGrid");
		}return listaResultado;
	}
	
	
	public List<MapeoDto> llenaPoliza(){
		List<MapeoDto> listaResultado = new ArrayList<MapeoDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("select * from ZIMP_POLIZAMANUAL");
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MapeoDto>(){
				public MapeoDto mapRow(ResultSet rs, int idx)throws SQLException{
					MapeoDto campos = new MapeoDto();
					campos.setIdPoliza(rs.getString("id_poliza"));
					campos.setDescPoliza(rs.getString("nombre_poliza"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoDaoImpl, M: llenaPoliza");
		}return listaResultado;
	}
	
	
	
	public List<MapeoDto> llenaGrupo(String poliza){
		List<MapeoDto> listaResultado = new ArrayList<MapeoDto>();
		StringBuffer sql= new StringBuffer();
		
		try{
			sql.append("select * from CAT_GRUPO where id_grupo in");
			sql.append("(select id_grupo from asignacion_poliza_grupo where id_poliza="+Utilerias.validarCadenaSQL(poliza));
					
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MapeoDto>(){
				public MapeoDto mapRow(ResultSet rs, int idx)throws SQLException{
					MapeoDto campos = new MapeoDto();
					campos.setIdGrupo(rs.getString("id_grupo"));
					campos.setDescGrupo(rs.getString("desc_grupo"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoDaoImpl, M: llenaGrupo");
		}return listaResultado;
	}
	
	
	public List<MapeoDto> llenaRubro(String poliza){
		System.out.println("llego a daoimpl");
		List<MapeoDto> listaResultado = new ArrayList<MapeoDto>();
		StringBuffer sql = new StringBuffer();
	
		try{
			sql.append("select  * from cat_rubro cr join asignacion_poliza_grupo ");
			sql.append("pg on cr.id_rubro=pg.id_grupo join cat_grupo cg on cg.id_grupo = cr.id_grupo ");
			sql.append("where pg.id_poliza="+Utilerias.validarCadenaSQL(poliza));
					
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MapeoDto>(){
				public MapeoDto mapRow(ResultSet rs, int idx)throws SQLException{
					MapeoDto campos = new MapeoDto();
					campos.setIdRubro(rs.getString("id_rubro"));
					campos.setDescRubro(rs.getString("desc_rubro"));
					campos.setIdGrupo(rs.getString("id_grupo"));
					campos.setDescGrupo(rs.getString("desc_grupo"));
					
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoDaoImpl, M: llenaRubro");
		}
		System.out.println(listaResultado.toString());
		return listaResultado;
	}
	
	public List<MapeoDto> llenaBanco(){
		List<MapeoDto> listaResultado = new ArrayList<MapeoDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("SELECT distinct b.ID_BANCO, DESC_BANCO FROM CAT_BANCO b \n");
			sql.append("JOIN CAT_CTA_BANCO cb ON b.id_banco= cb.id_banco");
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MapeoDto>(){
				public MapeoDto mapRow(ResultSet rs, int idx)throws SQLException{
					MapeoDto campos = new MapeoDto();
					campos.setIdBanco(rs.getString("id_banco"));
					campos.setDescBanco(rs.getString("desc_banco"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoDaoImpl, M: llenaRubro");
		}return listaResultado;
	}
	
	
	public List<MapeoDto> obtenerChequeras(String idBanco,int secuencia) {
		StringBuffer sql= new StringBuffer();
		List<MapeoDto> listaResultado = new ArrayList<MapeoDto>();
		sql.append("select id_chequera, desc_chequera from cat_cta_banco where id_banco="+Utilerias.validarCadenaSQL(idBanco)+"");
		if(secuencia!=0){
		sql.append(" and id_chequera not in (select id_chequera from CHEQUERAS_AV where ID_MAPEO_AV="+Utilerias.validarCadenaSQL(secuencia)+")");
		}
		System.out.println(sql.toString());
		try{
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MapeoDto>(){
				public MapeoDto mapRow(ResultSet rs, int idx)throws SQLException{
					MapeoDto campos = new MapeoDto();
					campos.setReferencia(rs.getString("id_chequera"));
					campos.setDescripcion(rs.getString("desc_chequera"));
					return campos;
				}
			});
			
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoDaoImpl, M: obtenerChequeras");
		}
		return listaResultado;
	}
	
	
	
	public void setDataSource(DataSource dataSource){
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}


	@Override
	public int asignarChequeras(int idBanco, int idChequera, boolean todos) {
		int res=-1;
		try{
			if(!todos)
			res = jdbcTemplate.update("INSERT INTO USUARIO_POLIZA (id_usuario, id_poliza) values (?,?)",
					new Object[]{Utilerias.validarCadenaSQL(idBanco),Utilerias.validarCadenaSQL( idChequera)});
			else
			res = jdbcTemplate.update("INSERT INTO USUARIO_POLIZA (id_usuario, id_poliza) "
					+ "SELECT  "+Utilerias.validarCadenaSQL(idBanco)+",id_poliza FROM zimp_polizamanual "
					+ "WHERE NOT ID_POLIZA IN (SELECT distinct ID_POLIZA FROM USUARIO_POLIZA WHERE id_usuario="+Utilerias.validarCadenaSQL(idBanco)+") ");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		
			+ "P:Utilerias, C:MapeoDao, M:asignarChequeras");
		}
		return res;
	}


	@Override
	public String insertaMapeo(MapeoDto objMapeoDto, List<Map<String, String>> registro, String idSecuencia) {
		String resultado="";
		try {
			StringBuffer sqlInsert = new StringBuffer();
			sqlInsert.append("insert into mapeosav values(");
			sqlInsert.append(Utilerias.validarCadenaSQL(idSecuencia));
			sqlInsert.append(",");
			sqlInsert.append(Utilerias.validarCadenaSQL(objMapeoDto.getIdPoliza()));
			sqlInsert.append(",");
			sqlInsert.append(Utilerias.validarCadenaSQL(objMapeoDto.getIdGrupo()));
			sqlInsert.append(",");
			sqlInsert.append(Utilerias.validarCadenaSQL(objMapeoDto.getIdRubro()));
			sqlInsert.append(",");
			sqlInsert.append(Utilerias.validarCadenaSQL(objMapeoDto.getIdBanco()));
			sqlInsert.append(",");
			sqlInsert.append("''");
			sqlInsert.append(",'");
			sqlInsert.append(Utilerias.validarCadenaSQL(objMapeoDto.getReferencia()));
			sqlInsert.append("','");
			sqlInsert.append(Utilerias.validarCadenaSQL(objMapeoDto.getConcepto()));
			sqlInsert.append("','");
			sqlInsert.append(Utilerias.validarCadenaSQL(objMapeoDto.getDescripcion()));
			sqlInsert.append("','");
			sqlInsert.append(Utilerias.validarCadenaSQL(objMapeoDto.getObservacion()));
			sqlInsert.append("','");
			sqlInsert.append(Utilerias.validarCadenaSQL(objMapeoDto.getTipo()));
			sqlInsert.append("','");
			sqlInsert.append(Utilerias.validarCadenaSQL(objMapeoDto.getEspecial()));
			sqlInsert.append("','");
			sqlInsert.append(Utilerias.validarCadenaSQL(objMapeoDto.getActivo()));
			sqlInsert.append("',");
			sqlInsert.append("getdate()");
			sqlInsert.append(",");
			sqlInsert.append("getdate()");
			sqlInsert.append(",");
			GlobalSingleton globalSingleton = GlobalSingleton.getInstancia();
			sqlInsert.append(Utilerias.validarCadenaSQL(globalSingleton .getUsuarioLoginDto().getIdUsuario()));
			sqlInsert.append(")");
			System.out.println("cabecera");
			System.out.println(sqlInsert);
			if(jdbcTemplate.update(sqlInsert.toString())!=0){
				for (int i = 0; i < registro.size();i ++) {
					StringBuffer sqlInsertChequera = new StringBuffer();
					sqlInsertChequera.append("insert into chequeras_av values(");
					sqlInsertChequera.append(idSecuencia);
					sqlInsertChequera.append(",'");
					sqlInsertChequera.append(registro.get(i).get("referencia"));
					sqlInsertChequera.append("',");
					sqlInsertChequera.append(objMapeoDto.getIdBanco());
					sqlInsertChequera.append(")");
					
					System.out.println("chequera");
					System.out.println(sqlInsertChequera);
					if(jdbcTemplate.update(sqlInsertChequera.toString())!=0){
						resultado = "Exito al insertar la chequera";
					}else{
						resultado += "Error al insertar la chequera"+registro.get(i).get("referencia");
					}
				}
				
				
			}else{
				resultado = "Error al insertar la cabecera";
			}
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Utilerias, C:MapeoDao, M:insertarMapeo");
		}
		return resultado;
	}
	
	public List<MapeoDto> obtieneDatos(String idPoliza, String idGrupo, String idRubro, String idBanco){
		System.out.println("Entro a dao Obtiene Datos");
		List<MapeoDto> listaResultado = new ArrayList<MapeoDto>();
		sql = "";
		@SuppressWarnings("unused")
		SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
		
		try
		{
			System.out.println("Entra al obtiene datos dao");
			logger.debug("Entra: obtieneDatos");
			StringBuffer sql = new StringBuffer();
			
			sql.append("select mp.*, pm.nombre_poliza, cr.desc_rubro, desc_grupo, desc_banco from mapeosav mp join zimp_polizamanual pm on mp.poliza=pm.id_poliza join cat_rubro cr on mp.rubro=cr.id_rubro join cat_grupo cg on mp.grupo=cg.id_grupo join cat_banco cb on mp.banco= cb.id_banco where banco = "); //895 and rubro=985 and grupo=526 and poliza=200");
			sql.append(Utilerias.validarCadenaSQL(idBanco));
			sql.append(" and grupo = ");
			sql.append(Utilerias.validarCadenaSQL(idGrupo));
			sql.append(" and rubro = ");
			sql.append(Utilerias.validarCadenaSQL(idRubro));
			sql.append(" and poliza = ");
			sql.append(Utilerias.validarCadenaSQL(idPoliza));
			
			System.out.println(sql);
			logger.info("SQL: " + sql);
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MapeoDto>(){
				public MapeoDto mapRow(ResultSet rs, int idx)throws SQLException{
					MapeoDto campos = new MapeoDto();
					campos.setIdPoliza(rs.getString("poliza"));
					campos.setDescPoliza(rs.getString("nombre_poliza"));
					campos.setIdGrupo(rs.getString("grupo"));
					campos.setDescGrupo(rs.getString("desc_grupo"));
					campos.setIdRubro(rs.getString("rubro"));
					campos.setDescRubro(rs.getString("desc_rubro"));
					campos.setIdBanco(rs.getString("banco"));
					campos.setDescBanco(rs.getString("desc_banco"));
					campos.setReferencia(rs.getString("referencia"));
					campos.setConcepto(rs.getString("concepto"));
					campos.setDescripcion(rs.getString("descripcion"));
					campos.setObservacion(rs.getString("observacion"));
					campos.setTipo(rs.getString("tipo"));
					campos.setEspecial(rs.getString("especial"));
					campos.setActivo(rs.getString("activo"));
					

					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoDaoImpl, M: obtieneDatos");
			logger.error(e.toString());
			return null;
		}
		logger.debug("Sale: obtieneDatos");
	
		return listaResultado;
	}


	@Override
	public List<Map<String, String>> reporteMapeo() {
		List<Map<String, String>> listaResultado = new ArrayList<Map<String, String>>();
		sql = "";
		try{
			StringBuffer sql = new StringBuffer();
			sql.append("select ma.*, ch.*, desc_chequera, desc_banco from mapeosav ma join chequeras_av ch on secuencia=id_mapeo_av "); 
			sql.append("join cat_cta_banco chb on chb.id_chequera= ch.id_chequera join cat_banco cb on cb.id_banco=ch.id_banco order by secuencia, ch.id_banco");
			
			
			System.out.println("-----------------");
			System.out.println("imprime query");
			System.out.println("-----------------");
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>(){
				public Map<String, String> mapRow(ResultSet rs, int idx)throws SQLException{
					Map<String, String> campos = new HashMap<String, String>();
					campos.put("secuencia",rs.getString("secuencia"));
					campos.put("idPoliza",rs.getString("poliza"));
					campos.put("idGrupo",rs.getString("grupo"));
					campos.put("idRubro",rs.getString("rubro"));
					campos.put("idBanco",rs.getString("banco"));
					campos.put("idChequera",rs.getString("id_chequera"));
					campos.put("referencia",rs.getString("referencia"));
					campos.put("concepto",rs.getString("concepto"));
					campos.put("descripcion",rs.getString("descripcion"));
					campos.put("observacion",rs.getString("observacion"));
					campos.put("tipo",rs.getString("tipo"));
					campos.put("especial",rs.getString("especial"));
					campos.put("activo",rs.getString("activo"));
					campos.put("descBanco",rs.getString("desc_banco"));
					campos.put("descChequera",rs.getString("desc_chequera"));
				
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoDaoImpl, M: reporteMapeo");
		}return listaResultado;	
	
	}


	@Override
	public String retornaIdMapeo(MapeoDto objMapeoDto) {
		String resultado="";
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("Select COUNT(*) from mapeosav where banco = "); //895 and rubro=985 and grupo=526 and poliza=200");
			sql.append(Utilerias.validarCadenaSQL(objMapeoDto.getIdBanco()));
			sql.append(" and grupo = ");
			sql.append(Utilerias.validarCadenaSQL(objMapeoDto.getIdGrupo()));
			sql.append(" and rubro = ");
			sql.append(Utilerias.validarCadenaSQL(objMapeoDto.getIdRubro()));
			sql.append(" and poliza = ");
			sql.append(Utilerias.validarCadenaSQL(objMapeoDto.getIdPoliza()));
			System.out.println("Este es el sql");
			System.out.println(sql);
	
			if (jdbcTemplate.queryForInt(sql.toString())!= 0){
				resultado="Ya existe un mapeo con esos valores";
				System.out.println("verda del dao");
			} else {
				System.out.println("false del dao");
				StringBuffer sqlId = new StringBuffer();
				List<String> listaResultado= new ArrayList<String>();
				sqlId.append("SELECT CASE WHEN (SELECT MAX(secuencia) FROM mapeosav) IS NOT NULL ");
				sqlId.append("\n THEN (SELECT MAX(secuencia)+ 1  FROM mapeosav)");
				sqlId.append("\n ELSE 1 END as id FROM DUAL");
				
				System.out.println(sqlId);
				listaResultado = jdbcTemplate.query(sqlId.toString(), new RowMapper<String>(){
					public String mapRow(ResultSet rs, int idx)throws SQLException{
						String id = "";
						id=rs.getString("id");
						return id;
					}
				});
				if (listaResultado.size()>0) {
					resultado=listaResultado.get(0);
					System.out.println("----------;:---------");
					System.out.println(resultado);
					System.out.println("----------;:---------");
				} else {
					resultado="No se pudo generar secuencia";
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoDaoImpl, M: retornaIdMapeo");

		}
		return resultado;
	}


	@Override
	public List<MapeoDto> obtenerChequerasAV(String idBanco, int secuencia) {
		StringBuffer sql= new StringBuffer();
		List<MapeoDto> listaResultado = new ArrayList<MapeoDto>();	
		
		sql.append("select ct.id_chequera, ct.desc_chequera from cat_cta_banco ct join chequeras_av cv "
				+ "on ct.id_chequera=cv.id_chequera where cv.id_mapeo_av="+Utilerias.validarCadenaSQL(secuencia)+"");
		System.out.println("esta es la cnsulta"+sql);
		try{
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MapeoDto>(){
				public MapeoDto mapRow(ResultSet rs, int idx)throws SQLException{
					MapeoDto campos = new MapeoDto();
					campos.setReferencia(rs.getString("id_chequera"));
					campos.setDescripcion(rs.getString("desc_chequera"));
					return campos;
				}
			});
			
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoDaoImpl, M: obtenerChequeras");
		}
		return listaResultado;
	}


	@Override
	public List<MapeoDto> obtenerChequerasM(String idBanco, String secuencia) {
		StringBuffer sql= new StringBuffer();
		List<MapeoDto> listaResultado = new ArrayList<MapeoDto>();
		sql.append("select id_chequera, desc_chequera from cat_cta_banco where id_banco="+Utilerias.validarCadenaSQL(idBanco)+"");
		sql.append(" and id_chequera not in (select id_chequera from CHEQUERAS_AV where ID_MAPEO_AV="+Utilerias.validarCadenaSQL(secuencia)+"");
		try{
			System.out.println("exddefrffr");
			System.out.println(sql.toString());
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MapeoDto>(){
				public MapeoDto mapRow(ResultSet rs, int idx)throws SQLException{
					MapeoDto campos = new MapeoDto();
					campos.setReferencia(rs.getString("id_chequera"));
					campos.setDescripcion(rs.getString("desc_chequera"));
					return campos;
				}
			});
			
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoDaoImpl, M: obtenerChequerasM");
		}
		return listaResultado;
	}
	
	public List<MapeoDto> verificaRegistro(int secuencia){
		List<MapeoDto> listaResultado = new ArrayList<MapeoDto>();
		StringBuffer sql = new StringBuffer();
		try{			
			sql.append("Select * from mapeosav ");
			sql.append("\n where secuencia ="+Utilerias.validarCadenaSQL( secuencia));
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MapeoDto>(){
				public MapeoDto mapRow(ResultSet rs, int idx)throws SQLException{
					MapeoDto campos = new MapeoDto();
					campos.setSecuencia(rs.getInt("secuencia"));
					
					return campos;
				}
			});		
		}		
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoDaoImpl, M: verificaRegistro");
		}return listaResultado;
	}


	

	@Override
	public int inhabilitaMapeo(int secuencia) {
		StringBuffer sql = new StringBuffer();
		int resultado = 0;
		try{		
			sql.append("Delete from mapeosav");
			sql.append("\n where secuencia ="+Utilerias.validarCadenaSQL( secuencia));
			
			System.out.println(sql);
			resultado = jdbcTemplate.update(sql.toString());
			
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoDaoImpl, M: inhabilitaMapeo");
		}return resultado;
	}


	@Override
	public void agregarChequerasAV(int secuencia, String chequeras, String banco) {
		try {
			StringBuffer sqlInsertChequera = new StringBuffer();
			sqlInsertChequera.append("insert into chequeras_av values(");
			sqlInsertChequera.append(Utilerias.validarCadenaSQL(secuencia));
			sqlInsertChequera.append(",'");
			sqlInsertChequera.append(Utilerias.validarCadenaSQL(chequeras));
			sqlInsertChequera.append("',");
			sqlInsertChequera.append(Utilerias.validarCadenaSQL(banco));
			sqlInsertChequera.append(")");
			
			System.out.println("chequera");
			System.out.println(sqlInsertChequera);
			
			jdbcTemplate.update(sqlInsertChequera.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Utilerias, C:MapeoDao, M:agregarChequerasAV");
		}
	}


	@Override
	public void eliminarChequerasAV(int secuencia, String chequeras, String banco) {
		try {
			StringBuffer sqlEliminaChequera = new StringBuffer();
			sqlEliminaChequera.append("delete chequeras_av where id_mapeo_av = ");
			sqlEliminaChequera.append(Utilerias.validarCadenaSQL(secuencia));
			sqlEliminaChequera.append(" and id_chequera= '");
			sqlEliminaChequera.append(Utilerias.validarCadenaSQL(chequeras));
			sqlEliminaChequera.append("'");
			System.out.println("elimina chequera");
			System.out.println(sqlEliminaChequera);
			
			jdbcTemplate.update(sqlEliminaChequera.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Utilerias, C:MapeoDao, M:eliminaChequerasAV");
		}
	}


	


	@Override
	public int eliminaChequera(int secuencia) {
		StringBuffer sql = new StringBuffer();
		int resultado = 0;
	
		try{
			sql.append("Delete from chequeras_av ");
			sql.append("\n where id_mapeo_av = "+Utilerias.validarCadenaSQL( secuencia ));
			
			System.out.println(sql);
			resultado = jdbcTemplate.update(sql.toString());
			
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoDaoImpl, M: eliminaChequera");
		}return resultado;
	}


	@Override
	public String actualizaMapeo(MapeoDto objMapeoDto, String idSecuencia) {
		String resultado="";
		try {
			StringBuffer sqlUpdate = new StringBuffer();
	
			sqlUpdate.append("Update mapeosav Set");
			sqlUpdate.append("\n referencia='"+Utilerias.validarCadenaSQL(objMapeoDto.getReferencia()));
			sqlUpdate.append("',");
			sqlUpdate.append("\n concepto='"+Utilerias.validarCadenaSQL(objMapeoDto.getConcepto()));
			sqlUpdate.append("',");
			sqlUpdate.append("\n descripcion='"+Utilerias.validarCadenaSQL(objMapeoDto.getDescripcion()));
			sqlUpdate.append("',");
			sqlUpdate.append("\n observacion='"+Utilerias.validarCadenaSQL(objMapeoDto.getObservacion()));
			sqlUpdate.append("',");
			sqlUpdate.append("\n tipo='"+Utilerias.validarCadenaSQL(objMapeoDto.getTipo()));
			sqlUpdate.append("',");
			sqlUpdate.append("\n especial='"+Utilerias.validarCadenaSQL(objMapeoDto.getEspecial()));
			sqlUpdate.append("',");
			sqlUpdate.append("\n activo='"+Utilerias.validarCadenaSQL(objMapeoDto.getActivo()));
			sqlUpdate.append("',");
			sqlUpdate.append("\n fec_modif=sysDate");
			sqlUpdate.append(",");
			GlobalSingleton globalSingleton = GlobalSingleton.getInstancia();
			sqlUpdate.append("\n usuario="+globalSingleton .getUsuarioLoginDto().getIdUsuario());
			sqlUpdate.append("\n where secuencia=" + idSecuencia);
			System.out.println("cabecera");
			System.out.println(sqlUpdate);
			if(jdbcTemplate.update(sqlUpdate.toString())!=0){
		
				resultado = "Exito al modificar el mapeo";

			}else{
				resultado = "Error al insertar la cabecera";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Utilerias, C:MapeoDao, M:actualizaMapeo");
		}
		return resultado;
	}


	@Override
	public void agregarTodasM(String idBanco, String secuencia) {
		try {
			StringBuffer sqlInsertChequera = new StringBuffer();
			
			sqlInsertChequera.append("insert into chequeras_av");
			sqlInsertChequera.append(" select ");
			sqlInsertChequera.append(Utilerias.validarCadenaSQL(secuencia));
			sqlInsertChequera.append(", id_chequera,");
			sqlInsertChequera.append(Utilerias.validarCadenaSQL(idBanco));
			sqlInsertChequera.append(" from cat_cta_banco where id_banco=");
			sqlInsertChequera.append(Utilerias.validarCadenaSQL(idBanco));
			sqlInsertChequera.append(" and id_chequera not in");
			sqlInsertChequera.append("(select id_chequera from CHEQUERAS_AV where ID_MAPEO_AV=");
			sqlInsertChequera.append(Utilerias.validarCadenaSQL(secuencia));		
			sqlInsertChequera.append(")");
			
			System.out.println("chequera");
			System.out.println(sqlInsertChequera);
			
			jdbcTemplate.update(sqlInsertChequera.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Utilerias, C:MapeoDao, M:agregarTodasM");
		}
		
	}


	@Override
	public void quitarTodasM(String secuencia) {
		try {
			StringBuffer sqlDeleteChequera = new StringBuffer();
			
			sqlDeleteChequera.append(" delete from chequeras_av where id_mapeo_av=");
			sqlDeleteChequera.append(secuencia);
			System.out.println("Elimina chequera");
			System.out.println(sqlDeleteChequera);
			
			jdbcTemplate.update(sqlDeleteChequera.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Utilerias, C:MapeoDao, M:quitarTodasM");
		}
		
	}


	




	
}
