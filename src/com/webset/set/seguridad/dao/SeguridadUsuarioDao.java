package com.webset.set.seguridad.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.webset.set.utilerias.ConsultasDao;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public class SeguridadUsuarioDao {
	private JdbcTemplate jdbcTemplate;
	private Funciones funciones = new Funciones();
	private ConsultasDao consultasDao;
	private Bitacora bitacora = new Bitacora();
    private static Logger logger = Logger.getLogger(SegUsuarioDao.class);
    
    /*
     * LLena el combo de perfiles de la ventana emergente
     *  Tabla : seg_perfil
     */
    
    public List<LlenaComboGralDto> comboPerfilUsuario(){
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		try{
		      sb.append("SELECT PER.ID_PERFIL as id, PER.DESCRIPCION as descripcion");
		      sb.append("\n  FROM  SEG_PERFIL PER");
		      sb.append("\n WHERE 1=1");
		      sb.append("\n AND  ESTATUS = 'A'");
		      sb.append("\n ORDER BY  ID_PERFIL");
		      
		    listDatos= jdbcTemplate.query(sb.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}});
		}
		catch(CannotGetJdbcConnectionException e){
			LlenaComboGralDto cons = new LlenaComboGralDto();
			cons.setCampoUno("Error de conexion");
			listDatos.add(cons);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Seguridad, C:SeguridadUsuarioDao, M:comboPerfilUsuario");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Seguridad, C:SeguridadUsuarioDao, M:comboPerfilUsuario");
		}return listDatos;
	}
    
    
    public List<LlenaComboGralDto> obtenerTodasCajas(){
		List<LlenaComboGralDto> cajas=new ArrayList<LlenaComboGralDto>();
		StringBuffer sql = new StringBuffer();
		try {
			sql.append(" SELECT id_caja as ID , desc_caja as DESCRIPCION FROM CAT_CAJA ");
			
			cajas= jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>(){
					public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
						LlenaComboGralDto cons = new LlenaComboGralDto();
						cons.setId(rs.getInt("ID"));
						cons.setDescripcion(rs.getString("DESCRIPCION"));
						return cons;
					}});
			}
			catch(CannotGetJdbcConnectionException e){
				LlenaComboGralDto cons = new LlenaComboGralDto();
				cons.setCampoUno("Error de conexion");
				cajas.add(cons);
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Seguridad, C:SeguridadUsuarioDao, M:comboPerfilUsuario");
			}
			catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Seguridad, C:SeguridadUsuarioDao, M:comboPerfilUsuario");
			}return cajas;
	}

	
	 public List<ComboUsuario> consultar() throws Exception{
			StringBuffer sb = new StringBuffer();		
			/*sb.append(" SELECT su.id_usuario, clave_usuario, (su.nombre || ' ' || su.apellido_paterno || ' ' || su.apellido_materno) as nombre  \n");
			sb.append("\n FROM seg_usuario su, cat_caja cc, empresa e , seg_usuario_perfil sup , seg_perfil sp");
			sb.append("\n WHERE su.id_caja = cc.id_caja ");
			sb.append("\n AND su.no_empresa = e.no_empresa ");
			sb.append("\n AND sup.id_usuario=su.id_usuario ");
			sb.append("\n AND sup.id_perfil=sp.id_perfil ");
			sb.append("\n order by clave_usuario");*/
			
			//Consulta modificada 16-03-16
			
			sb.append("\n SELECT su.id_usuario, clave_usuario, (su.nombre + ' ' + su.apellido_paterno + ' ' + su.apellido_materno) as nombre "); 
			sb.append("\n  FROM seg_usuario su ");
			sb.append("\n  LEFT JOIN  cat_caja cc ON su.id_caja = cc.id_caja ");
			sb.append("\n LEFT JOIN empresa e  ON su.no_empresa = e.no_empresa ");
			sb.append("\n  LEFT JOIN seg_usuario_perfil sup ON sup.id_usuario=su.id_usuario");
			sb.append("\n  LEFT JOIN seg_perfil sp ON sup.id_perfil=sp.id_perfil");
			sb.append("\n order by clave_usuario");
					
			logger.info(sb.toString());
			 
			List <ComboUsuario> usuarios = null;
			System.out.println(sb.toString());
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
				+ "P:Seguridad, C:SeguridadUsuarioDao, M:consultar");
			}
			return usuarios;
		}
	 
	 public List<GridUsuario> consultarGrid(SegUsuarioDto usuario) throws Exception{
			String sql = "";
			StringBuffer sb = new StringBuffer();
			ArrayList<String> cond = new ArrayList<String>();
			
			sb.append(" SELECT su.id_usuario, clave_usuario, nombre, apellido_paterno, apellido_materno, contrasena, su.estatus, \n");
			sb.append(" intentos, correo_electronico, convert(char(10),fecha_acceso,103) as fecha_acceso ,  convert(char(10),fecha_vencimiento,103) as  fecha_vencimiento, cc.desc_caja, e.nom_empresa \n");
			sb.append(" , sp.descripcion as perfil , e.no_empresa ,sup.id_perfil , su.id_Caja \n"); //YEC
			/*sb.append(" FROM seg_usuario su, cat_caja cc, empresa e ,seg_usuario_perfil sup , seg_perfil sp \n");
			sb.append(" WHERE su.id_caja = cc.id_caja \n");
			sb.append(" And su.no_empresa = e.no_empresa \n");
			sb.append(" and sup.id_usuario=su.id_usuario \n"); //YEC
			sb.append(" and sup.id_perfil=sp.id_perfil \n"); //YEC*/
			
			sb.append("\n  FROM seg_usuario su ");
			sb.append("\n  LEFT JOIN  cat_caja cc ON su.id_caja = cc.id_caja ");
			sb.append("\n LEFT JOIN empresa e  ON su.no_empresa = e.no_empresa ");
			sb.append("\n  LEFT JOIN seg_usuario_perfil sup ON sup.id_usuario=su.id_usuario");
			sb.append("\n  LEFT JOIN seg_perfil sp ON sup.id_perfil=sp.id_perfil");
			
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
					sb.append(" where " + StringUtils.join(cond, " AND "));
				}
			}
			sb.append(" ORDER BY su.id_usuario ");
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
					usuario.setAcceso(rs.getString("fecha_acceso"));
					usuario.setVencimiento(rs.getString("fecha_vencimiento"));
					usuario.setNomCaja(rs.getString("desc_caja"));
					usuario.setNomEmpresa(rs.getString("nom_empresa"));
					usuario.setPerfil(rs.getString("perfil"));
					usuario.setIdPerfil(rs.getString("id_perfil"));
					usuario.setNoEmpresa(rs.getString("no_empresa"));
					usuario.setIdCaja(rs.getString("id_Caja"));
					
					return usuario;
				}});
			
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Seguridad, C:SeguridadUsuarioDao, M:consultar");
			}
			return usuarios;
		}
	 
	 
	 /******************
	  *  Bloque para eliminar usuario de cat_usuario ,seg_usuario
	  ******************/
	    public int eliminarCatUsuario(int idUsu){
			int res = -1;
			try{
				res = jdbcTemplate.update("DELETE FROM cat_usuario WHERE no_usuario = " + idUsu);
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
						+ "P:Seguridad, C:SeguridadUsuarioDao, M:eliminarCatUsuario");
						}
			return res;
		}
	 
	    public int eliminar(int idUsu){
			int res =-1;
			try{
				res = jdbcTemplate.update("DELETE FROM seg_usuario WHERE id_usuario = " + idUsu);
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Seguridad, C:SeguridadUsuarioDao, M:eliminar");
			}
			return res;
		}
	    
	    /******************
		  *  Bloque para insertar usuario en seg_usuario, cat_usuario ,
		  ******************/
	    
	    public int getSecuencia(){
	    	StringBuffer sb = new StringBuffer();
	    	sb.append(" SELECT MAX (maximo) FROM (");
	    	sb.append("\n SELECT MAX(id_usuario)+1 AS maximo FROM seg_usuario");
	    	sb.append("\n UNION ALL");
	    	sb.append("\n SELECT MAX(no_usuario)+1 AS maximo FROM cat_usuario");
	    	sb.append("\n UNION ALL");
	    	sb.append("\n SELECT MAX(id_usuario)+1 AS maximo FROM seg_usuario_perfil");
	    	sb.append("\n ) idmaximo");
	    	int res=0;
	    	System.out.println(sb.toString());
	    	try {
	    	     res=jdbcTemplate.queryForInt(sb.toString());
	    	     if(res<1){
	 				res=1;
	 			}
			} catch (Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Seguridad, C:SeguridadUsuarioDao, M:getSecuencia");
			}return res;
		}
	    
	    public int insertarSegUsuario(SegUsuarioDto usuario) throws Exception{
			int res=-1;
			StringBuffer sb = new StringBuffer();
			consultasDao = new ConsultasDao(jdbcTemplate);
			if(consultasDao.getCount("seg_usuario", "clave_usuario", usuario.getClaveUsuario())==0){
				usuario.setFechaAcceso(funciones.ponerFechaSola(consultasDao.obtenerFechaHoy().get(0)));
				sb.append(" INSERT INTO seg_usuario( id_usuario, nombre, apellido_materno, apellido_paterno, contrasena, id_caja, no_empresa, "); 
				sb.append("\n intentos, estatus, fecha_acceso, fecha_vencimiento, correo_electronico, clave_usuario)");
				sb.append("\n VALUES (");
				sb.append("\n "+usuario.getIdUsuario()+",");
				sb.append("\n '"+ usuario.getNombre().toUpperCase().trim()+"',");
				sb.append("\n '"+ usuario.getApellidoMaterno().toUpperCase().trim()+"',");
				sb.append("\n '"+ usuario.getApellidoPaterno().toUpperCase().trim()+"',");
				sb.append("\n '"+ funciones.encriptador(usuario.getContrasena().trim())+"',");
				sb.append("\n "+usuario.getIdCaja()+",");
				sb.append("\n "+usuario.getNoEmpresa()+",");
				sb.append("\n "+usuario.getIntentos()+",");
				sb.append("\n '"+ usuario.getEstatus()+"',");
				sb.append("\n convert(datetime,'"+usuario.getFechaAcceso()+"', 103),");
				sb.append("\n convert(datetime,'"+usuario.getFechaVencimiento()+"', 103),");
				sb.append("\n '"+usuario.getCorreoElectronico()+"',");
				sb.append("\n '"+usuario.getClaveUsuario().toUpperCase().trim()+"'");
				sb.append("\n ) ");
				try{
					System.out.println(sb.toString());
					res = jdbcTemplate.update(sb.toString());
				} catch(Exception e){
					bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
							+ "P:Seguridad, C:SeguridadUsuarioDao, M:insertar");
				}
			}
			return res;
		}
	 
	    public int insertarCatUsuario(SegUsuarioDto usuario) throws Exception{
			int res=-1;
			StringBuffer sb = new StringBuffer();
			consultasDao = new ConsultasDao(jdbcTemplate);
			try{
				if(consultasDao.getCount("cat_usuario", "no_usuario", Integer.toString(usuario.getIdUsuario())) == 0){
					sb.append(" INSERT INTO cat_usuario(no_usuario, nombre, materno, paterno, id_caja, no_empresa, folio_control) \n "); 
					sb.append("\n VALUES (");
					sb.append("\n "+usuario.getIdUsuario()+",");
					sb.append("\n '"+ usuario.getNombre().toUpperCase().trim()+"',");
					sb.append("\n '"+ usuario.getApellidoMaterno().toUpperCase().trim()+"',");
					sb.append("\n '"+ usuario.getApellidoPaterno().toUpperCase().trim()+"',");
					sb.append("\n "+usuario.getIdCaja()+",");
					sb.append("\n "+usuario.getNoEmpresa()+",");
					sb.append("\n "+"1"+"");
					sb.append("\n ) ");
				
					res = jdbcTemplate.update(sb.toString());
				}
			} catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Seguridad, C:SeguridadUsuarioDao, M:insertarCatUsuario");
			}
			return res;
		}
	    
	    public int insertarPerfil(int usuario, String idPerfil){
			int res = -1;
			StringBuffer sb = new StringBuffer();
			try{
				sb.append("INSERT INTO seg_usuario_perfil ");
				sb.append("values (");
				sb.append(usuario+",");
				sb.append(idPerfil+")");
				System.out.println(sb.toString());
				res = jdbcTemplate.update(sb.toString());	
			} catch (Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Seguridad, C:SeguridadUsuarioDao, M:insertarPerfil");
			}return res;
		}
	    
	    /******************
		  *  Bloque para modificar usuario en seg_usuario, cat_usuario ,
		  ******************/
	   
	    public int updatePerfil(int usuario, String idPerfil){
			int res = 0;
			StringBuffer sb = new StringBuffer();
			try{
				sb.append("select count(*) from seg_usuario_perfil WHERE id_usuario = " + usuario);
				res=jdbcTemplate.queryForInt(sb.toString());
				if(res!=0){
					sb.delete(0, sb.length());
					sb.append("update seg_usuario_perfil ");
					sb.append("\n set id_perfil= "+ idPerfil);
					sb.append("\n WHERE id_usuario = "+ usuario);
					//System.out.println(sb.toString());
					res = jdbcTemplate.update(sb.toString());
				}else{
					res= insertarPerfil(usuario, idPerfil);
				}
				
			} catch (Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
						+ "P:Seguridad, C:SeguridadUsuarioDao, M:insertarPerfil");
			} return res;
		}
	    
	    public int modificarCatUsuarios(SegUsuarioDto usuario) throws Exception{
			StringBuffer sb = new StringBuffer();
			int res = 0;
			
			logger.info(sb.toString());
			
			try{
				sb.append(" select count(*) from cat_usuario WHERE no_usuario =" + usuario.getIdUsuario());
				res=jdbcTemplate.queryForInt(sb.toString());
				if(res!=0){
					sb.delete(0, sb.length());
					sb.append(" UPDATE cat_usuario  ");
					sb.append("\n SET nombre = '"+usuario.getNombre().toUpperCase() +"', ");
					sb.append("\n materno = '"+ usuario.getApellidoMaterno().toUpperCase() +"' ,  ");
					sb.append("\n paterno = '"+ usuario.getApellidoPaterno().toUpperCase()+"', ");
					sb.append("\n id_caja = "+ usuario.getIdCaja()+" ,");
					sb.append("\n no_empresa = "+usuario.getNoEmpresa() +" ");
					sb.append("\n WHERE no_usuario = "+ usuario.getIdUsuario());
					System.out.println(sb.toString());
					res = jdbcTemplate.update(sb.toString());
				}else{
					res= insertarCatUsuario(usuario);
				}
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Seguridad, C:SeguridadUsuarioDao, M:modificarCatUsuarios");
			}
			return res;
		}
	    
	    public int modificar(SegUsuarioDto usuario) throws Exception{
			int res = 0;
			StringBuffer sb = new StringBuffer();
			String estatus = usuario.getEstatus().toUpperCase().equals("A")? "A":"I";
			try {
				sb.append(" UPDATE seg_usuario ");
				sb.append("\n SET clave_usuario = '"+ usuario.getClaveUsuario().toUpperCase() +"', ");
				sb.append("\n nombre = '"+ usuario.getNombre().toUpperCase() +"',  ");
				sb.append("\n apellido_materno = '"+ usuario.getApellidoMaterno().toUpperCase() +"', ");
				sb.append("\n apellido_paterno = '"+ usuario.getApellidoPaterno().toUpperCase() +"',");
				sb.append("\n estatus = '"+ estatus +"', ");
				sb.append("\n intentos="+usuario.getIntentos()+ " ,");
				sb.append("\n correo_electronico = '"+ usuario.getCorreoElectronico() +"', ");
				sb.append("\n fecha_vencimiento = convert(datetime, '"+ usuario.getFechaVencimiento() +"', 103), ");
				sb.append("\n id_caja = "+ usuario.getIdCaja() +",");
				sb.append("\n no_empresa = "+ usuario.getNoEmpresa() +" ");
				
				if(!usuario.getContrasena().trim().equals(""))
					sb.append("\n , contrasena = '"+ funciones.encriptador(usuario.getContrasena().trim()) +"' ");
				
				sb.append("\n WHERE id_usuario = "+ usuario.getIdUsuario() +" ");
				
				System.out.println(sb.toString());
				logger.info(sb.toString());
				
				res = jdbcTemplate.update(sb.toString());
			}catch(Exception e){
				System.out.println("dao");
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Seguridad, C:SeguridadUsuarioDao, M:modificar");
			}
			return res;
		}
	    
	    /***********Modificar esta parte *********************/
		
		public String deleteUsuario(String idUsuario){
			String resultado ="Error al eliminar perfil";
			StringBuffer sb = new StringBuffer();
			int res=0;
			try {
				System.out.println("--------------------------");
				System.out.println("ELIMINANDO USUARIO");
				sb.append("DELETE seg_usuario_perfil ");
				sb.append("\n WHERE id_usuario = "+ idUsuario);
				System.out.println(sb.toString());
				res = jdbcTemplate.update(sb.toString());
				
				if(res==0)
					return resultado;
				else
					resultado="Error al eliminar el usuario.";
				
				sb.delete(0, sb.length());
				sb.append("DELETE FROM seg_usuario WHERE id_usuario = " + idUsuario);
				System.out.println(sb.toString());
				res = jdbcTemplate.update(sb.toString());
				
				if(res==0)
					return resultado;
				else
					resultado="Error al eliminar.";
				
				sb.delete(0, sb.length());
				sb.append("DELETE FROM cat_usuario WHERE no_usuario = " + idUsuario);
				System.out.println(sb.toString());
				
				res = jdbcTemplate.update(sb.toString());
				
				if(res!=0)
					resultado="Exito";
				
				System.out.println("--------------------------");
				
				
			}catch(CannotGetJdbcConnectionException e){
				resultado ="Error de conexion";
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Seguridad, C:SeguridadUsuarioDao, M:deleteUsuario");
			}catch (Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Seguridad, C:SeguridadUsuarioDao, M:deleteUsuario");
			}return resultado;
			
		}
		
		
		
		
		//getters && setters
		public void setDataSource(DataSource dataSource) {
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
	 
}
