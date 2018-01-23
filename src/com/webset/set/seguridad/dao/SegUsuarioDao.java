package com.webset.set.seguridad.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.seguridad.dto.ComboUsuario;
import com.webset.set.seguridad.dto.GridUsuario;
import com.webset.set.seguridad.dto.SegUsuarioDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.ConsultasDao;
import com.webset.set.utilerias.Funciones;
import com.webset.utils.tools.Utilerias;
/**
 * 
 * @author Sergio Vaca 
 *
 */
public class SegUsuarioDao {
	private JdbcTemplate jdbcTemplate;
	private Funciones funciones = new Funciones();
	private ConsultasDao consultasDao;
	private Bitacora bitacora = new Bitacora();
    private static Logger logger = Logger.getLogger(SegUsuarioDao.class);
	private String gsDBM = ConstantesSet.gsDBM;

	/**
	 * Editado por Luis Serrato 04092015
	 * Metodo que consulta la tabla de seg_usuario de acuerdo a los parametros
	 * de id_usuario, cve_usuario, estatus   
	 * 
	 * Retorna una lista de objetos de tipo o solamente una tupla 
	 *
	 * @param usuario
	 * @return
	 * @throws Exception
	 */
    public List<ComboUsuario> consultar() throws Exception{
		StringBuffer sb = new StringBuffer();		
		
		sb.append(" SELECT id_usuario, clave_usuario, (nombre + ' ' + apellido_paterno + ' ' + apellido_materno)as nombre, apellido_paterno, apellido_materno \n");
		sb.append(" FROM seg_usuario su, cat_caja cc, empresa e \n");
		sb.append(" WHERE su.id_caja = cc.id_caja \n");
		sb.append(" 	And su.no_empresa = e.no_empresa \n");
				
		logger.info(sb.toString());
		
		List <ComboUsuario> usuarios = null;
		try{
			usuarios = jdbcTemplate.query(sb.toString(), new RowMapper<ComboUsuario>(){
			
			public ComboUsuario mapRow(ResultSet rs, int idx) throws SQLException {
				ComboUsuario usuario = new ComboUsuario();
				
				usuario.setIdUsuario(rs.getInt("id_usuario"));
				usuario.setNombre(rs.getString("nombre"));
				usuario.setClaveUsuario(rs.getString("clave_usuario"));
				
				return usuario;
			}});
		
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegUsuarioDao, M:consultar");
		}
		return usuarios;
	}
    
    /*
     * Creado por Luis Serrato 04092015
     */
    public List<GridUsuario> consultarGrid(SegUsuarioDto usuario) throws Exception{
		String sql = "";
		StringBuffer sb = new StringBuffer();
		ArrayList<String> cond = new ArrayList<String>();
		
		sb.append(" SELECT id_usuario, clave_usuario, nombre, apellido_paterno, apellido_materno, contrasena, estatus, \n");
		sb.append(" 		intentos, correo_electronico, fecha_acceso, fecha_vencimiento, cc.desc_caja, e.nom_empresa \n");
		sb.append(" FROM seg_usuario su, cat_caja cc, empresa e \n");
		sb.append(" WHERE su.id_caja = cc.id_caja \n");
		sb.append(" 	And su.no_empresa = e.no_empresa \n");
		
		if(usuario!=null){
			if(usuario.getIdUsuario() > 0){
				cond.add(" su.id_usuario = " + usuario.getIdUsuario());
			}
			if((usuario.getClaveUsuario()!=null) && !usuario.getClaveUsuario().equals("")){
				cond.add(" su.clave_usuario = '" + usuario.getClaveUsuario().toUpperCase() + "'" );
			}
			if(usuario.getEstatus()!=null && !usuario.getEstatus().equals("")){
				if(usuario.getEstatus().equals("A") || usuario.getEstatus().equals("I"))
					cond.add(" su.estatus = '" + usuario.getEstatus() + "'");
			}
			if(cond.size() > 0){
				sb.append(" And " + StringUtils.join(cond, " AND "));
			}
		}
		sb.append(" ORDER BY id_usuario ");
		sql=sb.toString();
		
		logger.info(sql);
		System.out.println(sql);
		List <GridUsuario> usuarios = null;
		try{
			usuarios = jdbcTemplate.query(sql, new RowMapper<GridUsuario>(){
			
			public GridUsuario mapRow(ResultSet rs, int idx) throws SQLException {
				GridUsuario usuario = new GridUsuario();
				usuario.setIdUsuario(rs.getInt("id_usuario"));
				usuario.setCveUsuario(rs.getString("clave_usuario"));
				usuario.setPaterno(rs.getString("apellido_paterno"));
				usuario.setMaterno(rs.getString("apellido_materno"));
				usuario.setNombreU(rs.getString("nombre"));
				usuario.setPsw(rs.getString("contrasena"));
				usuario.setEstatus(rs.getString("estatus"));
				usuario.setIntentos(rs.getInt("intentos"));
				usuario.setMail(rs.getString("correo_electronico"));
				usuario.setVencimiento(rs.getDate("fecha_vencimiento").toString());
				usuario.setNomCaja(rs.getString("desc_caja"));
				usuario.setNomEmpresa(rs.getString("nom_empresa"));
				
				return usuario;
			}});
		
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegUsuarioDao, M:consultar");
		}
		return usuarios;
	}


	/**
	 * 
	 * Metodo que valida la tabla de seg_usuario de acuerdo a los parametros
	 * de id_usuario, cve_usuario, estatus   
	 * 
	 * Retorna true of false segun la contrase\u00f1a es valida o no 
	 *
	 * @param usuario
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> validarContrasena(SegUsuarioDto usuario) throws Exception{
		//logger.info("SegUsuarioDao.validarContrasena()=" +usuario.getClaveUsuario()+","+usuario.getContrasena());

		StringBuffer sb = new StringBuffer();
		
		sb.append(" SELECT id_usuario ");
		sb.append(" FROM seg_usuario ");
		sb.append(" WHERE clave_usuario = '" + usuario.getClaveUsuario()+ "'");
		sb.append(" AND contrasena = '" + usuario.getContrasena() + "'" );
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {
			List <Integer> resList = jdbcTemplate.query(sb.toString(), new RowMapper<Integer>(){
				public Integer mapRow(ResultSet rs, int idx) throws SQLException {
					return new Integer(rs.getInt("id_usuario"));
				}});
			
			if (resList != null && !resList.isEmpty()) {
				result.put("id_usuario", resList.get(0));
				result.put("msg", "Login Correcto");
			} else {
				cambioIntento(usuario.getClaveUsuario());
				result.put("id_usuario", 0);
				result.put("msg", "Usuario y/o Contrase\u00f1a incorrectos");
			}
		}catch(Exception e) {
			result.put("id_usuario", 0);
			result.put("msg", "Error al autenticar");
		}
		return result;
	}
	
	
	
	public void cambioIntento (String claveUsuario){
		
		try{
			jdbcTemplate.update("UPDATE seg_usuario SET intentos = intentos + 1 WHERE clave_usuario = '" + claveUsuario + "'");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegUsuarioDao, M:cambioIntento");
		}
	}

	/**
	 * Actualiza la tabla de seg_usuario en base a la informacion proporcionada en el objeto usuario
	 * segun el campo id_usuario
	 * retorna un entero
	 * @param usuario
	 * @return
	 * @throws Exception
	 */
	public int modificar(SegUsuarioDto usuario) throws Exception{
		int res = 0;
		StringBuffer sb = new StringBuffer();
		String estatus = usuario.getEstatus().toUpperCase().equals("ACTIVO")? "A":"I";
		
		try {
			sb.append(" UPDATE seg_usuario SET clave_usuario = '"+ usuario.getClaveUsuario().toUpperCase() +"', nombre = '"+ usuario.getNombre().toUpperCase() +"', apellido_materno = '"+ usuario.getApellidoMaterno().toUpperCase() +"', apellido_paterno = '"+ usuario.getApellidoPaterno().toUpperCase() +"', estatus = '"+ estatus +"', ");
			sb.append( "intentos="+usuario.getIntentos()+ " ,correo_electronico = '"+ usuario.getCorreoElectronico() +"', fecha_vencimiento = TO_DATE('"+ usuario.getFechaVencimiento() +"','dd/mm/yyyy'), id_caja = "+ usuario.getIdCaja() +", no_empresa = "+ usuario.getNoEmpresa() +" ");
			
			if(!usuario.getContrasena().trim().equals(""))
				sb.append(" , contrasena = '"+ funciones.encriptador(usuario.getContrasena().trim()) +"' ");
			
			sb.append(" WHERE id_usuario = "+ usuario.getIdUsuario() +" ");
			System.out.println(sb.toString());
			logger.info(sb.toString());
			
			res = jdbcTemplate.update(sb.toString());
		}catch(Exception e){
			System.out.println("dao");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Seguridad, C:SegUsuarioDao, M:modificar");
		}
		return res;
	}
	
	/**
	 * Elimina de la tabla de seg_usuario el usuario identificado por el id_tipo_usuario 
	 * @param idUsu
	 * @return
	 * 	@throws Exception
	 */	
	public int eliminar(int idUsu){
		int res =-1;
		try{
			res = jdbcTemplate.update("DELETE FROM seg_usuario WHERE id_usuario = " + idUsu);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seguridad, C:SegUsuarioDao, M:eliminar");
					}
		return res;
	}

	/**
	 * Inserta un nuevo registro en seg_usuario
	 * @param usuario
	 * @return
	 * @throws Exception
	 */
	public int insertar(SegUsuarioDto usuario) throws Exception{
			int res=0;
			StringBuffer sb = new StringBuffer();
			consultasDao = new ConsultasDao(jdbcTemplate);
			
			if(consultasDao.getCount("seg_usuario", "clave_usuario", usuario.getClaveUsuario())==0){
				usuario.setIntentos(0);
				usuario.setFechaAcceso(funciones.ponerFechaSola(consultasDao.obtenerFechaHoy().get(0)));
				usuario.setIdUsuario(consultasDao.getSeq("seg_usuario", "id_usuario"));
				sb.append(" INSERT INTO seg_usuario( id_usuario, nombre, apellido_materno, apellido_paterno, contrasena, id_caja, no_empresa, "); 
				sb.append(" 						intentos, estatus, fecha_acceso, fecha_vencimiento, correo_electronico, clave_usuario)");
				sb.append(" VALUES("+usuario.getIdUsuario()+", '"+Utilerias.validarCadenaSQL(usuario.getNombre().toUpperCase().trim())+"' ");
				sb.append(" 		,'"+Utilerias.validarCadenaSQL(usuario.getApellidoMaterno().toUpperCase().trim())+"','"+Utilerias.validarCadenaSQL(usuario.getApellidoPaterno().toUpperCase().trim())+"' ");
				sb.append(" 		,'"+funciones.encriptador(usuario.getContrasena().trim())+"',"+usuario.getIdCaja()+" ");
				sb.append(" 		, "+usuario.getNoEmpresa()+", "+usuario.getIntentos()+", '"+Utilerias.validarCadenaSQL(usuario.getEstatus().toUpperCase().equals("ACTIVO")? "A":"I")+"' ");
				sb.append(" 		, convert(datetime,'"+Utilerias.validarCadenaSQL(usuario.getFechaAcceso())+"',103)");
				sb.append(" 		, convert(datetime,'"+Utilerias.validarCadenaSQL(usuario.getFechaVencimiento())+"',103)");
				sb.append(" 		, '"+Utilerias.validarCadenaSQL(usuario.getCorreoElectronico())+"' ");
				sb.append(" 		, '"+Utilerias.validarCadenaSQL( usuario.getClaveUsuario().toUpperCase().trim())+"'");
				sb.append(" 		) ");
				
				
				/*sb.append(" VALUES ( ?, ?, ?, ?, ?, ?, ?, ");
				sb.append(" 		?, ?, ?, ?, ?, ?) ");
				*/
				/*System.out.println("Fecha 1: " + usuario.getFechaAcceso());
				System.out.println("Fecha 2: " + usuario.getFechaVencimiento());
				*/
				
				
				try{
					res = jdbcTemplate.update(sb.toString());
					
					/*res = jdbcTemplate.update(sb.toString(),
					new Object[]{usuario.getIdUsuario(), usuario.getNombre().toUpperCase().trim(), usuario.getApellidoMaterno().toUpperCase().trim(), usuario.getApellidoPaterno().toUpperCase().trim(),funciones.encriptador(usuario.getContrasena().trim()), 
						usuario.getIdCaja(), usuario.getNoEmpresa(), usuario.getIntentos(), usuario.getEstatus().toUpperCase().equals("ACTIVO")? "A":"I", funciones.ponerFechaDate(usuario.getFechaAcceso()) , funciones.ponerFechaDate(usuario.getFechaVencimiento()),
						usuario.getCorreoElectronico(), usuario.getClaveUsuario().toUpperCase().trim()}
					);*/
				} catch(Exception e){
					bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
							+ "P:Seguridad, C:SegUsuarioDao, M:insertar");
				}
			}
			return res;
		}
	/**
	 * Para cambiar fecha de acceso
	 * @param idUsuario
	 * @return res 
	 */
	public int cambioAcceso (int idUsuario){
		int res=0;
		try{
		if(idUsuario>0){
			res = jdbcTemplate.update("UPDATE seg_usuario SET fecha_acceso = (select fec_hoy from fechas) WHERE id_usuario = " + idUsuario);
			return res;
		}
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegUsuarioDao, M:cambioAcceso");
		}
		return res;
	}
	/** 
	 * para cambiar fecha de Vencimiento
	 * @param idUsuario
	 * @param fecVen
	 * @return
	 */
	public int cambioVencimiento (int idUsuario, Date fecVen){
		int res=0;
		try{
		if(idUsuario>0){
			res = jdbcTemplate.update("UPDATE seg_usuario SET fecha_acceso = '" + new Date() + "' WHERE id_usuario = " + idUsuario);
		}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegUsuarioDao, M:cambioVencimiento");
		}
		return res;
	}
	/**
	 * para actualizar intentos 
	 * @param idUsuario
	 * @param edo
	 * @return res 
	 * si parametro edo el true suma 1 a los intentos si es falso regresa a cero los intentos
	 */
	public int cambioIntento (int idUsuario, boolean edo){
		int res=0;
		try{
		if(idUsuario>0){
			if(edo)
				res = jdbcTemplate.update("UPDATE seg_usuario SET intentos = intentos + 1 WHERE id_usuario = " + idUsuario);
			else
				res = jdbcTemplate.update("UPDATE seg_usuario SET intentos = 0 WHERE id_usuario = " + idUsuario);
		}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegUsuarioDao, M:cambioIntento");
		}
		return res;
	}
	/**
	 * para saber cuantos intentos lleva el usuario
	 * @param idUsuario
	 * @return
	 */
	public int intentos(String claveUsuario){
		int res=0;
		try{
			res=jdbcTemplate.queryForInt("SELECT intentos FROM seg_usuario WHERE clave_usuario = '" + claveUsuario + "'" );
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegUsuarioDao, M:intentos");
		}
		return res;
	}
	/**
	 * Cambio de contrase\u00f1a del usuario
	 * @param idUsuario
	 * @param contra
	 */
	public int cambiaContrasena(String cveUsr, String sNewPass){
		int res=0;
		
		try {
			res = insertaHisContrasena(cveUsr);
			
			if(res != 0) res = jdbcTemplate.update("UPDATE seg_usuario SET contrasena = '" + sNewPass + "' WHERE clave_usuario = '"+ cveUsr +"'");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegUsuarioDao, M:cambiaContrsena");
		}
		return res;
	}
	/**
	 * insertar en seg_his_contrasena segun el id_usuario recibido
	 * @param idUsuario
	 * @return
	 */
	public int insertaHisContrasena(String cveUsr){
		int res = 0;
		
		try{
			StringBuffer sb = new StringBuffer();
			StringBuffer sql = new StringBuffer();
			sb.append(" SELECT id_usuario, clave_usuario, getdate(), contrasena ");
			sb.append(" FROM seg_usuario ");
			sb.append(" WHERE clave_usuario = '"+ cveUsr +"'");
			
			/*List <SegHisContrasenaDto> contras = jdbcTemplate.query(sb.toString(), new RowMapper(){
				public SegHisContrasenaDto mapRow(ResultSet rs, int idx) throws SQLException {
					SegHisContrasenaDto contra = new SegHisContrasenaDto();
						contra.setIdUsuario(rs.getInt("id_usuario"));
						contra.setClaveUsuario(rs.getString("clave_usuario"));
						contra.setContrasena(rs.getString("contrasena"));
						contra.setFecha(new Date());
					return contra;
				}});*/
			
			//if(contras.size()==1){
				sql.append(" INSERT INTO seg_his_contrasena(id_usuario, clave_usuario, fecha, contrasena) ");
				//sql.append(" VALUES ( ?, ?, ?, ? ) ");
				sql.append(sb.toString());
				
				res = jdbcTemplate.update(sql.toString());
				/*res = jdbcTemplate.update(sql.toString(),
					new Object[]{contras.get(0).getIdUsuario(), contras.get(0).getClaveUsuario(), contras.get(0).getFecha(), contras.get(0).getContrasena()}
				  );*/
			//}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegUsuarioDao, M:insertaHisContrsena");
		}
		return res;
	}
	
	/**
	 * 
	 * @param id recibe el id de usuario
	 * @return y retorna el conjunto de id_usuario y nombreCompleto
	 */
	public List<SegUsuarioDto> llenarComboNombreBusqueda(int id){
		StringBuffer sb = new StringBuffer();
		List <SegUsuarioDto> usuarios = null;
		try{
		sb.append(" SELECT id_usuario, nombre, apellido_paterno, apellido_materno ");
		sb.append(" FROM seg_usuario ");
		if(id>0){
			sb.append(" WHERE id_usuario = " + id);
		}
		sb.append(" ORDER BY nombre ");
		 usuarios = jdbcTemplate.query(sb.toString(), new RowMapper<SegUsuarioDto>(){
			public SegUsuarioDto mapRow(ResultSet rs, int idx) throws SQLException {
				SegUsuarioDto usuario = new SegUsuarioDto();
					usuario.setIdUsuarioC(rs.getInt("id_usuario"));
					usuario.setNombreCompletoC(rs.getString("nombre").trim() + " " +
							rs.getString("apellido_paterno").trim() + " " + rs.getString("apellido_materno").trim());
				return usuario;
			}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegUsuarioDao, M:llenarComboNombreBusqueda");
		}
		return usuarios;
	}
	
	/**
	 * 
	 * @param id recibe el id de usuario
	 * @return y retorna el conjunto de id_usuario y clave_usuario
	 */
	public List<SegUsuarioDto> llenarComboClaveBusqueda(int id){
		StringBuffer sb = new StringBuffer();
		List <SegUsuarioDto> usuarios = null;
		try{
		sb.append(" SELECT id_usuario, clave_usuario ");
		sb.append(" FROM seg_usuario ");
		if(id>0){
			sb.append(" WHERE id_usuario = " + id);
		}
		sb.append(" ORDER BY nombre ");
		 usuarios = jdbcTemplate.query(sb.toString(), new RowMapper<SegUsuarioDto>(){
			public SegUsuarioDto mapRow(ResultSet rs, int idx) throws SQLException {
				SegUsuarioDto usuario = new SegUsuarioDto();
					usuario.setIdUsuarioCve(rs.getInt("id_usuario"));
					usuario.setClaveUsuarioCve(rs.getString("clave_usuario"));
				return usuario;
			}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegUsuarioDao, M:llenarComboClaveBusqueda");
		}
		return usuarios;
	}
	/**
	 * para saber si no falta algun parametro
	 * @param usuario
	 * @return true o false si false faltan datos si true todo bien
	 */
	public boolean noNulo(SegUsuarioDto usuario){
		if(usuario==null 
			|| usuario.getClaveUsuario()==null 
			|| usuario.getNombre()==null
			|| usuario.getApellidoPaterno()==null
			|| usuario.getContrasena()==null
			|| usuario.getEstatus()==null
			|| usuario.getClavePerfil()==null
			|| usuario.getClaveUsuario().equals("")
			|| usuario.getEstatus().equals("")
			|| usuario.getIdPerfil()<1
			|| usuario.getClaveUsuario().equals("") 
			|| usuario.getNombre().equals("")
			|| usuario.getApellidoPaterno().equals("")
			|| usuario.getContrasena().equals("")
			|| usuario.getEstatus().equals("")
			|| usuario.getClavePerfil().equals(""))
			return false;
		else
			return true;
	}
	
	public int buscaEmpresa(String nomEmpresa){
		StringBuffer sql = new StringBuffer();
		int res = 0;
		
		try {
			sql.append(" SELECT no_empresa FROM empresa ");
			sql.append(" WHERE nom_empresa = '"+ nomEmpresa +"' ");
			
			res = jdbcTemplate.queryForInt(sql.toString());
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegUsuarioDao, M:buscaEmpresa");
		}
		return res;
	}
	
	public int buscaCaja(String descCaja){
		StringBuffer sql = new StringBuffer();
		int res = 0;
		
		try {
			sql.append(" SELECT id_caja FROM cat_caja ");
			sql.append(" WHERE desc_caja = '"+ descCaja +"' ");
			
			res = jdbcTemplate.queryForInt(sql.toString());
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegUsuarioDao, M:buscaCaja");
		}
		return res;
	}
	
	public int insertarCatUsuario(SegUsuarioDto usuario) throws Exception{
		int res=0;
		StringBuffer sb = new StringBuffer();
		consultasDao = new ConsultasDao(jdbcTemplate);
		usuario.setIdUsuario(consultasDao.getUsuario(usuario.getClaveUsuario().toUpperCase().trim()));
		
		if(consultasDao.getCount("cat_usuario", "no_usuario", Integer.toString(usuario.getIdUsuario())) == 0){
			sb.append(" INSERT INTO cat_usuario(no_usuario, nombre, materno, paterno, id_caja, no_empresa, folio_control) \n"); 
			sb.append(" VALUES ( ?, ?, ?, ?, ?, ?, ? )");
			
			try{
				res = jdbcTemplate.update(sb.toString(),
				new Object[]{usuario.getIdUsuario(), usuario.getNombre().toUpperCase().trim(), usuario.getApellidoMaterno().toUpperCase().trim(), usuario.getApellidoPaterno().toUpperCase().trim(), 
					usuario.getIdCaja(), usuario.getNoEmpresa(), 1}
				);
			} catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
						+ "P:Seguridad, C:SegUsuarioDao, M:insertarCatUsuario");
			}
		}
		return res;
	}
	
	public int eliminarCatUsuario(int idUsu){
		int res = -1;
		
		try{
			res = jdbcTemplate.update("DELETE FROM cat_usuario WHERE no_usuario = " + idUsu);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seguridad, C:SegUsuarioDao, M:eliminarCatUsuario");
					}
		return res;
	}
	
	public int modificarCatUsuarios(SegUsuarioDto usuario) throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append(" UPDATE cat_usuario SET nombre = '").append(usuario.getNombre().toUpperCase()).append("',\n");
		sb.append("materno = '").append(usuario.getApellidoMaterno().toUpperCase()).append("',\n");
		sb.append("paterno = '").append(usuario.getApellidoPaterno().toUpperCase()).append("',\n");
		sb.append("id_caja = '").append(usuario.getIdCaja()).append("',\n");
		sb.append("no_empresa = '").append(usuario.getNoEmpresa()).append("'\n");
		sb.append(" WHERE no_usuario = ").append(usuario.getIdUsuario());
		
		int res = 0;
		
		logger.info(sb.toString());
		
		try{
			res = jdbcTemplate.update(sb.toString(),
			new Object[]{usuario.getNombre().toUpperCase(), usuario.getApellidoMaterno().toUpperCase(), usuario.getApellidoPaterno().toUpperCase(), 
				usuario.getIdCaja(), usuario.getNoEmpresa(), usuario.getIdUsuario()}
			);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Seguridad, C:SegUsuarioDao, M:modificarCatUsuarios");
		}
		return res;
	}
	
	//getters && setters
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public boolean validarVigencia(String usuario) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT COUNT(*) FROM SEG_USUARIO WHERE CLAVE_USUARIO = '");
		sb.append(usuario);
		sb.append("'\n");
		
		if(gsDBM.equals("ORACLE") || gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2"))
			sb.append("AND (SELECT to_date(FEC_HOY, 'dd/mm/yyyy') FROM FECHAS) <= to_date(FECHA_VENCIMIENTO, 'dd/mm/yyyy')");
		if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE"))
			sb.append("AND (SELECT CONVERT(datetime,FEC_HOY, 103) FROM FECHAS) <= convert(datetime,FECHA_VENCIMIENTO, 103)");
		
		logger.info("validarVigencia: " + sb);
		int r = 0;
		try{
			r = jdbcTemplate.queryForInt(sb.toString());
		}
		catch (CannotGetJdbcConnectionException e){
			e.printStackTrace();
			logger.error(e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seguridad, C:SegUsuarioDao, M:validarVigencia");
		} catch(Exception e){
			e.printStackTrace();
			logger.error(e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seguridad, C:SegUsuarioDao, M:validarVigencia");
		}
		return r > 0;
	}

	public int intentos(int idUsuario) {
		int res=0;
		try{
			res=jdbcTemplate.queryForInt("SELECT intentos FROM seg_usuario WHERE id_usuario = " + idUsuario );
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegUsuarioDao, M:intentos");
		}
		return res;
	}

	public boolean validarBloqueo(String claveUsuario) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT COUNT(*) FROM SEG_USUARIO WHERE CLAVE_USUARIO = '");
		sb.append(claveUsuario);
		sb.append("'\n");
		sb.append("AND ESTATUS = 'A'");
		System.out.println(sb.toString());
		int r = 0;
		try{
			r = jdbcTemplate.queryForInt(sb.toString());
		} catch (CannotGetJdbcConnectionException e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seguridad, C:SegUsuarioDao, M:validarBloqueo");
		} catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seguridad, C:SegUsuarioDao, M:validarBloqueo");
		}
		return r > 0;
	}

	public boolean validarUsuario(String claveUsuario) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT COUNT(*) FROM SEG_USUARIO WHERE CLAVE_USUARIO = '");
		sb.append(claveUsuario);
		sb.append("'\n");
		System.out.println(sb.toString());
		int r = 0;
		try{
			r = jdbcTemplate.queryForInt(sb.toString());
		} catch (CannotGetJdbcConnectionException e){
			e.printStackTrace();
			bitacora.insertarRegistro("---- Se Perdio la conexi�n " + new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seguridad, C:SegUsuarioDao, M:validarUsuario");
		} catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seguridad, C:SegUsuarioDao, M:validarUsuario");
		}
		return r > 0;
	}
}
