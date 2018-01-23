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

import com.webset.set.seguridad.dto.SegPerfilDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;


/**
 * 
 * @author Sergio Vaca
 * @since 19/Octubre/2010
 * @see <p>Tabla seg_perfil</p>
 */

/**
 * Clase que contiene los metodos "consultar", "insertar", "modificar", "eliminar", "noNulo", "llenarCombo"
 * de componentes
 */
public class SegPerfilDao {
	private JdbcTemplate jdbcTemplate;
	private ConsultasGenerales gral;
	private Bitacora bitacoraDao = new Bitacora();
	private static Logger logger = Logger.getLogger(SegPerfilDao.class);
	/**
	 * hace la consulta segun los parametros recibidos
	 * @param perfil
	 * @return List<SegPerfilDto>
	 * @throws Exception
	 */
	
	public List<SegPerfilDto> consultar(SegPerfilDto perfil){
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT id_perfil, descripcion, estatus ");
		sb.append(" FROM seg_perfil");
		ArrayList<String>cond = new ArrayList<String>();
		if(perfil!=null){
			if(perfil.getIdPerfil()>0){
				cond.add(" id_perfil = " + perfil.getIdPerfil());
			}
			if((perfil.getDescripcion()!=null) && !(perfil.getDescripcion().equals(""))){
				cond.add(" descripcion = '" + perfil.getDescripcion().toUpperCase().trim() + "'" );
			}
			if(perfil.getEstatus()!=null && !(perfil.getEstatus().equals(""))){
				if(perfil.getEstatus().toUpperCase().trim().equals("A") || perfil.getEstatus().toUpperCase().trim().equals("I"))
					cond.add(" estatus = '" + perfil.getEstatus().toUpperCase().trim() + "'");
			}
			if(cond.size() > 0){
				sb.append(" WHERE " + StringUtils.join(cond, " AND "));
			}
		}
		sb.append(" ORDER BY id_perfil ");
		sql=sb.toString();
		logger.info(sql);

		try{
			
				/*  // EJEMPLO DE LLAMADA A UN STORED PROCEDURE POR MEDIO DE MAPAS
			 	StoredProcedure storedProcedure = new StoredProcedure(jdbcTemplate.getDataSource(), "_SomeStoredProc" ) {};
			    storedProcedure.declareParameter( new SqlReturnResultSet( "foo", new RowMapper() {
			        public Object mapRow( final ResultSet rs, final int rowNum ) throws SQLException {
			            return rs.getObject( 1 );
			        }
			    } ) );
			    
			    storedProcedure.declareParameter( new SqlParameter( "inParamName", Types.VARCHAR ) );

			    HashMap< Object, Object > inParams = new HashMap< Object, Object >();

			    inParams.put( "inParamName", "myparam" );
			    Map result = storedProcedure.execute( inParams );
			    logger.info( result.get( "varRet" ) );
			    */
			
			    List <SegPerfilDto> perfiles = jdbcTemplate.query(sql, new RowMapper<SegPerfilDto>(){
				public SegPerfilDto mapRow(ResultSet rs, int idx) throws SQLException {
					SegPerfilDto facul = new SegPerfilDto();
					facul.setIdPerfil(rs.getInt("id_perfil")); 
					facul.setDescripcion(rs.getString("descripcion"));
					facul.setEstatus(rs.getString("estatus"));
				return facul;
				}});
			return perfiles;
		} catch (Exception e) {
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:SEG, C:SegPerfilDao, M:consultar");
			return null;
		}
	}
	
	/**
	 * Actualiza el perfil
	 * @param perfil
	 * @return int
	 */
	public int modificar(SegPerfilDto perfil){
		logger.info("modificar");
		StringBuffer sb = new StringBuffer();
		int res=-1;
		gral = new ConsultasGenerales(jdbcTemplate);
		try{
			//if(gral.getCountActualizar("seg_perfil", "id_perfil", "clave_perfil", perfil.getIdPerfil(), perfil.getClavePerfil())==0){
				//int retorno = gral.existeCampoClave("seg_usuario", "id_perfil", perfil.getIdPerfil());
				//if(retorno==0){	
					sb.append(" UPDATE seg_perfil SET descripcion = ?, estatus = ? ");
					sb.append(" WHERE id_perfil = ? ");
					logger.info(sb.toString());

					res = jdbcTemplate.update(sb.toString(),
						new Object[]{perfil.getDescripcion().toUpperCase().trim(), perfil.getEstatus().toUpperCase().trim(), perfil.getIdPerfil()}
					);
				//}
			//}
		} catch (Exception e) {
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:SEG, C:SegPerfilDao, M:modificar");
		}
		return res;
	}
	
	/**
	 * elimina el perfil segun el id
	 * @param idPer
	 * @return int
	 */
	public int eliminar(int idPer){
		try{
			int res = jdbcTemplate.update("DELETE FROM seg_perfil WHERE id_perfil = " + idPer);
			return res;
		} catch (Exception e) {
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:SEG, C:SegPerfilDao, M:eliminar");
			return -1;
		}
	}

	/**
	 * Inserta un nuevo perfil
	 * @param perfil
	 * @return int
	 * @throws Exception
	 */
	public int insertar(SegPerfilDto perfil){
		StringBuffer sb = new StringBuffer();
		int res=-1;
		gral = new ConsultasGenerales(jdbcTemplate);
		try{
			//if(gral.getCount("seg_perfil", "clave_perfil", perfil.getClavePerfil())==0){
				perfil.setIdPerfil(gral.getSeq("seg_perfil", "id_perfil"));
				sb.append(" INSERT INTO seg_perfil( id_perfil, descripcion, estatus ) ");
				sb.append(" VALUES ( ?, ?, ?) ");
				res = jdbcTemplate.update(sb.toString(),
					new Object[]{perfil.getIdPerfil(), perfil.getDescripcion().toUpperCase().trim(), perfil.getEstatus().toUpperCase().trim()}
				);
			//}
		} catch (Exception e) {
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:SEG, C:SegPerfilDao, M:insertar");
		}
		return res;
	}
	/**
	 * para saber que vienen todos los datos
	 * @param perfil
	 * @return boolean
	 */
	public boolean noNulo(SegPerfilDto perfil){
		if(perfil==null 
			|| perfil.getDescripcion()==null 
			|| perfil.getEstatus()==null 
			|| perfil.getDescripcion().equals("") 
			|| perfil.getEstatus().equals("") )
			return false;
		else
			return true;
	}
	
	/**
	 * retorna una lista de perfiles id_perfil y clave_perfil
	 * @param id puede contener los valores de 0 mayor y menor a cero
	 * 0  -> muestra perfiles activos 
	 * <0 -> muestra todos los perfiles ordenados por id_perfil
	 * 0> -> muestra el perfil que corresponde al id
	 * @return
	 */
	
	public List<SegPerfilDto> llenarCombo(int id){
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT id_perfil, descripcion ");
		sb.append(" FROM seg_perfil");
		if(id==0){
			sb.append(" WHERE estatus = 'A'");
		}
		else if(id>0){
			sb.append(" WHERE id_perfil = " + id);
		}
		else{
			sb.append(" ORDER BY id_perfil");
		}
		try{
			logger.info(sb.toString());

			List <SegPerfilDto> perfiles = jdbcTemplate.query(sb.toString(), new RowMapper<SegPerfilDto>(){
				public SegPerfilDto mapRow(ResultSet rs, int idx) throws SQLException {
					SegPerfilDto perfil = new SegPerfilDto();
					perfil.setIdPerfil(rs.getInt("id_perfil")); 
					perfil.setDescripcion(rs.getString("descripcion"));
				return perfil;
				}});
			return perfiles;
		} catch (Exception e) {
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:SEG, C:SegPerfilDao, M:llenarCombo");
			return null;
		}
	}
	//getters && setters
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public Map<String, Object> verificarFacultad(String idComponente, int idUsuario) {
		boolean r = true;
		Map<String, Object> result = new HashMap<>(); 
		result.put("conn", "");
		result.put("excep", "");
		result.put("facultad", false);
		
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT COUNT(*) FROM BOTON_USUARIO BU\n"); 
			sql.append("JOIN SEG_FAC_BOTON B ON BU.ID_BOTON = B.ID_BOTON\n");
			sql.append("WHERE BU.ID_BOTON = '");
			sql.append(idComponente);
			sql.append("' AND BU.ID_USUARIO = ");
			sql.append(idUsuario);
			sql.append(" AND B.ESTATUS = 'A'");
			System.out.println(sql.toString());
			r = jdbcTemplate.queryForInt(sql.toString()) > 0;
			
			result.put("facultad", r);
			
		}catch(CannotGetJdbcConnectionException e){
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ImpresionDaoImpl, M:llenarComboEmpresa");
			
			result.put("conn", "Error de conexi�n");
		} catch (Exception e) {
			e.printStackTrace();
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegPerfilDao, M:verificarFacultad");
			result.put("excep", "Error al verificar la facultad");
		}
		return result;
	}
}
