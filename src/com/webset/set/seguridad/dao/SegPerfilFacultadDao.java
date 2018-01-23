package com.webset.set.seguridad.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

//import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.seguridad.dto.SegPerfilFacultadDto;
import com.webset.set.utilerias.Bitacora;

/**
 * 
 * @author Sergio Vaca
 *
 */
public class SegPerfilFacultadDao {
	private JdbcTemplate jdbcTemplate;
	//private static Logger log = Logger.getLogger(SegFacultadDao.class);
	private Bitacora bitacoraDao = new Bitacora();
	/**
	 * 
	 * @param perfil
	 * @param ban
	 * @return List<SegPerfilFacultadDto>
	 * 
	 * Metodo que consulta la tabla de las facultades asignados o no asignados a perfil
	 * componente de acuerdo a los parametros
	 * de id_perfil y un valor boolean 
	 * true -> para los asignados  
	 * false -> para los no asignados
	 */
	
	public List<SegPerfilFacultadDto> consultar(SegPerfilFacultadDto perfil, boolean ban){
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT id_facultad, descripcion "); 
		sb.append("\n FROM seg_facultad ");
		sb.append("\n WHERE estatus='A' "); 
		sb.append("\n AND ");
		if(!ban)
			sb.append("\n NOT "); 
		sb.append("\n id_facultad IN (SELECT id_facultad "); 
		sb.append("\n 			FROM seg_perfil_facultad "); 
		sb.append("\n 			WHERE id_perfil =  " + perfil.getIdPerfil() + " ) ");
		try{
			List <SegPerfilFacultadDto> facultades = jdbcTemplate.query(sb.toString(), new RowMapper<SegPerfilFacultadDto>(){
				public SegPerfilFacultadDto mapRow(ResultSet rs, int idx) throws SQLException {
					SegPerfilFacultadDto facultad = new SegPerfilFacultadDto();
					facultad.setIdFacultad(rs.getInt("id_facultad"));
					facultad.setDescripcion(rs.getString("descripcion"));
				return facultad;
				}});
			return facultades;
		} catch (Exception e) {
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:SEG, C:SegPerfilFacultadDao, M:consultar");
			return null;
		}
	}
	/**
	 * elimina todos las facultades correspondientes al perfil pero
	 * si el tambien contiene valor el id_facultad solo elimina la facultad
	 * y perfil correspondiente 
	 * @param facultad
	 * @return
	 * @throws Exception
	 */
	public int eliminar(SegPerfilFacultadDto perfil){
		StringBuffer sb = new StringBuffer();
		sb.append(" DELETE FROM seg_perfil_facultad");
		sb.append("\n WHERE id_perfil = " + perfil.getIdPerfil());
		if (perfil.getIdFacultad()>0)
			sb.append("\n AND id_facultad = " + perfil.getIdFacultad());
		try{
			int res = jdbcTemplate.update(sb.toString());
			return res;
		} catch (Exception e) {
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:SEG, C:SegPerfilFacultadDao, M:eliminar");
			return -1;
		}
	}

	/**
	 * 
	 * @param perfil
	 * @return int
	 * 
	 * inserta todas las facultades activas o solamente una
	 */
	public int insertar(SegPerfilFacultadDto perfil){
		StringBuffer sb = new StringBuffer();
		sb.append(" INSERT INTO seg_perfil_facultad ");
		sb.append("\n SELECT p.id_perfil, f.id_facultad, p.clave_perfil ");
		sb.append("\n  FROM seg_facultad f, seg_perfil p WHERE p.id_perfil = "+ perfil.getIdPerfil() +" "); 
		sb.append("\n  AND f.estatus='A' "); 
		if(perfil.getIdFacultad()>0)
			sb.append("\n AND f.id_facultad = "+ perfil.getIdFacultad());
		sb.append("\n  AND NOT f.id_facultad IN ( SELECT DISTINCT pf.id_facultad "); 
		sb.append("\n FROM seg_perfil_facultad pf ");
		sb.append("\n WHERE pf.id_perfil = "+ perfil.getIdPerfil() +" ) ");
		try{
			int res = jdbcTemplate.update(sb.toString());
			return res;
		} catch (Exception e) {
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:SEG, C:SegPerfilFacultadDao, M:insertar");
			return -1;
		}
	}
	
	//getters && setters
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
}
