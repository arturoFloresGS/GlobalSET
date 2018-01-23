package com.webset.set.seguridad.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.seguridad.dto.SegFacultadComponenteDto;
import com.webset.set.utilerias.Bitacora;

/**
 * 
 * @author Sergio Vaca
 * @since 19/Octubre/2010
 * @see <p>Tabla seg_facultada_componente</p>
 *
 */

/**
 * Clase que contiene los metodos "consultar", "insertar", "eliminar"
 * de todas las areas asignadas o por asignar a un usuario.
 */
public class SegFacultadComponenteDao {
	private JdbcTemplate jdbcTemplate;
	private Bitacora bitacoraDao = new Bitacora();
	/**
	 * Metodo que consulta la tabla de los componentes asignados o no asignados a facultad
	 * componente de acuerdo a los parametros
	 * de id_facultad y un valor boleano 
	 * true -> para los asignados  
	 * false -> para los no asignados
	 *  
	 * Retorna una lista de objetos SegFacultadComponenteDto con valores solamente de id_componente y clave_componente 
	 */
	
	public List<SegFacultadComponenteDto> consultar(SegFacultadComponenteDto facultad, boolean ban){
		StringBuffer sb = new StringBuffer();
		List <SegFacultadComponenteDto> componentes = null;
		sb.append(" SELECT id_componente, clave_componente, descripcion "); 
		sb.append("\n FROM seg_componente ");
		sb.append("\n WHERE estatus='A' "); 
		sb.append("\n AND ");
		if(!ban)
			sb.append(" NOT "); 
		sb.append("\n id_componente IN (SELECT id_componente "); 
		sb.append("\n 			FROM seg_facultad_componente "); 
		sb.append("\n 			WHERE id_facultad =  "+ facultad.getIdFacultad() +" ) ");
		try{
				componentes = jdbcTemplate.query(sb.toString(), new RowMapper<SegFacultadComponenteDto>(){
				public SegFacultadComponenteDto mapRow(ResultSet rs, int idx) throws SQLException {
					SegFacultadComponenteDto comp = new SegFacultadComponenteDto();
					comp.setIdComponente(rs.getInt("id_componente"));
					comp.setClaveComponente(rs.getString("clave_componente"));
					comp.setDescripcion(rs.getString("descripcion"));
				return comp;
				}});
			return componentes;
		} catch (Exception e) {
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:SEG, C:SegFacultadComponenteDao, M:consultar");
			return null;
		}
	}
	/**
	 * elimina todos los componentes correspondientes a la facultad pero
	 * si el tambien contiene valor el id_componente solo elimina el componente
	 * y faculta correspondiente 
	 * @param facultad
	 * @return
	 * @throws Exception
	 */
	public int eliminar(SegFacultadComponenteDto facultad){
		StringBuffer sb = new StringBuffer();
		sb.append(" DELETE FROM seg_facultad_componente");
		sb.append("\n WHERE id_facultad = " + facultad.getIdFacultad());
		if (facultad.getIdComponente()>0)
			sb.append("\n AND id_componente = " + facultad.getIdComponente());
		try{
			int res = jdbcTemplate.update(sb.toString());
			return res;
		} catch (Exception e) {
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:SEG, C:SegFacultadComponenteDao, M:eliminar");
			return -1;
		}
	}

	/**
	 * insertar todos los componentes o solamente uno 
	 * @param facultad 
	 * @return
	 * @throws Exception
	 */
	public int insertar(SegFacultadComponenteDto facultad){
		StringBuffer sb = new StringBuffer();
		sb.append("	INSERT INTO seg_facultad_componente");
		sb.append("\n SELECT f.id_facultad, c.id_componente");
		sb.append("\n FROM seg_componente c, seg_facultad f WHERE f.id_facultad = "+ facultad.getIdFacultad() );
		sb.append("\n AND c.estatus='A' ");
		if(facultad.getIdComponente()>0)
			sb.append("\n AND c.id_componente = "+ facultad.getIdComponente());
		sb.append("\n AND NOT c.id_componente IN (SELECT fc.id_componente ");
		sb.append("\n FROM seg_facultad_componente fc "); 
		sb.append("\n WHERE fc.id_facultad = "+ facultad.getIdFacultad()+" )");
		try{
			int res = jdbcTemplate.update(sb.toString());
			return res;
		} catch (Exception e) {
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:SEG, C:SegFacultadComponenteDao, M:insertar");
			return -1;
		}
	}
	
	//getters && setters
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
}
