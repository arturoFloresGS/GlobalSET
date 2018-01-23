package com.webset.set.seguridad.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;
//import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.seguridad.dto.SegFacultadDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;

/**
 * 
 * @author Sergio Vaca
 * @since 19/Octubre/2010
 * @see <p>Tabla seg_facultad</p>
 */

/**
 * Clase que contiene los metodos "consultar", "insertar", "modificar", "eliminar", "noNulo", "llenarCombo"
 * de componentes
 */
public class SegFacultadDao {
	private JdbcTemplate jdbcTemplate;
	private ConsultasGenerales gral;
	private Bitacora bitacoraDao = new Bitacora();
	//private static Logger log = Logger.getLogger(SegFacultadDao.class);
	
	/**
	 * Consultas los datos segun los parametros recibidos
	 * @param facultad
	 * @return List<SegFacultadDto>
	 * @throws Exception
	 */
	
	public List<SegFacultadDto> consultar(SegFacultadDto facultad){
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT id_facultad, descripcion, estatus ");
		sb.append(" FROM seg_facultad ");
		ArrayList<String>cond = new ArrayList<String>();
		if(facultad!=null){
			if(facultad.getIdFacultad() >0){
				cond.add(" id_facultad = " + facultad.getIdFacultad());
			}
			if((facultad.getDescripcion()!=null) && !(facultad.getDescripcion().equals(""))){
				cond.add(" descripcion = '" + facultad.getDescripcion() + "'" );
			}
			if(facultad.getEstatus()!=null && !(facultad.getEstatus().equals(""))){
				if(facultad.getEstatus().equals("A") || facultad.getEstatus().equals("I"))
					cond.add(" estatus = '" + facultad.getEstatus() + "'");
			}
			if(cond.size() > 0){
				sb.append(" WHERE " + StringUtils.join(cond, " AND "));
			}
		}
		sql=sb.toString();
		try{
			List <SegFacultadDto> facultades = jdbcTemplate.query(sql, new RowMapper<SegFacultadDto>(){
				public SegFacultadDto mapRow(ResultSet rs, int idx) throws SQLException {
					SegFacultadDto facul = new SegFacultadDto();
					facul.setIdFacultad(rs.getInt("id_facultad")); 
					facul.setDescripcion(rs.getString("descripcion"));
					facul.setEstatus(rs.getString("estatus"));
				return facul;
				}});
			return facultades;
		} catch (Exception e) {
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:SEG, C:SegFacultadDao, M:consultar");
			return null;
		}
	}
	
	/**
	 * Actualiza la facultad
	 * @param facultad
	 * @return int
	 * @throws Exception
	 */
	public int modificar(SegFacultadDto facultad) throws Exception{
		StringBuffer sb = new StringBuffer();
		int res=-1;
		gral=new ConsultasGenerales(jdbcTemplate);
		try{
			//if(gral.getCountActualizar("seg_facultad", "id_facultad", "clave_facultad", facultad.getIdFacultad(), facultad.getClaveFacultad())==0){
				sb.append(" UPDATE seg_facultad SET descripcion =?, estatus =? ");
				sb.append(" WHERE id_facultad = ? ");
				res = jdbcTemplate.update(sb.toString(),
					new Object[]{facultad.getDescripcion().toUpperCase(), facultad.getEstatus().toUpperCase(), facultad.getIdFacultad()}
				);
			//}
		} catch (Exception e) {
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:SEG, C:SegFacultadDao, M:modificar");
		}
		return res;
	}
	
	/**
	 * elimina la facultad
	 * @param idFac
	 * @return int
	 * @throws Exception
	 */
	public int eliminar(int idFac){
		try{
			int res = jdbcTemplate.update("DELETE FROM seg_facultad WHERE id_facultad = " + idFac);
			return res;
		} catch (Exception e) {
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:SEG, C:SegFacultadDao, M:eliminar");
			return -1;
		}
	}

	/**
	 * inserta una nueva facultad
	 * @param facultad
	 * @return int
	 * @throws Exception 
	 */
	public int insertar(SegFacultadDto facultad) throws Exception{
		StringBuffer sb = new StringBuffer();
		int res=-1;
		gral = new ConsultasGenerales(jdbcTemplate);
		try{
			//if(gral.getCount("seg_facultad", "clave_facultad", facultad.getClaveFacultad())==0){
				facultad.setIdFacultad(gral.getSeq("seg_facultad", "id_facultad"));
				sb.append(" INSERT INTO seg_facultad ( id_facultad, descripcion, estatus ) ");
				sb.append(" VALUES ( ?, ?, ? ) ");
				res = jdbcTemplate.update(sb.toString(),
					new Object[]{facultad.getIdFacultad(), facultad.getDescripcion().toUpperCase(), facultad.getEstatus().toUpperCase()}
				);
			//}
		} catch (Exception e) {
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:SEG, C:SegFacultadDao, M:insertar");
		}
		return res;
	}
	/**
	 * retorna una lista de facultades id_facultad y Decripcion
	 * @param id puede contener los valores de 0 mayor y menor a cero
	 * 0  -> muestra facultades activas 
	 * <0 -> muestra todos los facultades ordenados por id_facultad
	 * 0> -> muestra la facultad que corresponde al id
	 * @return
	 */
	
	public List<SegFacultadDto> llenarCombo(int id){
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT id_facultad, descripcion ");
		sb.append(" FROM seg_facultad");
		if(id==0){
			sb.append(" WHERE estatus = 'A'");
		}
		else if(id>0){
			sb.append(" WHERE id_facultad = " + id);
		}
		else{
			sb.append(" ORDER BY id_facultad");
		}
		try{
			List <SegFacultadDto> facultades = jdbcTemplate.query(sb.toString(), new RowMapper<SegFacultadDto>(){
				public SegFacultadDto mapRow(ResultSet rs, int idx) throws SQLException {
					SegFacultadDto facultad = new SegFacultadDto();
					facultad.setIdFacultad(rs.getInt("id_facultad")); 
					facultad.setDescripcion(rs.getString("descripcion"));
				return facultad;
				}});
			return facultades;
		} catch (Exception e) {
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:SEG, C:SegFacultadDao, M:llenarCombo");
			return null;
		}
	}
	/**
	 * Verifica que venga los datos requeridos para la insercion o la modificacion
	 * @param facultad
	 * @return boolean
	 */
	public boolean noNulo(SegFacultadDto facultad){
		if(facultad==null 
			|| facultad.getDescripcion()==null 
			|| facultad.getEstatus()==null 
			|| facultad.getDescripcion().equals("") 
			|| facultad.getEstatus().equals("") )
			return false;
		else
			return true;
	}
	
	//getters && setters
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

}
