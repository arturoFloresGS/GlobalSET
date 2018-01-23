package com.webset.set.seguridad.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;

/**
 * 
 * @author Armando Rodriguez
 *
 */
public class SegUsuarioPerfilDao {
	private JdbcTemplate jdbcTemplate;
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
	public List<Map<String, Object>> obtenerPerfilesUsuario(int idUsuario){

		
		ConsultasGenerales consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		
		// CONSTRUCCION DE SUBQUERY (PERFILES INCLUIDOS)
		List<String> camposUsuPerf = new ArrayList<String>();
		camposUsuPerf.add("COUNT(1)");

		List<String> tablasUsuPerf = new ArrayList<String>();
		tablasUsuPerf.add("SEG_USUARIO_PERFIL UP");
		
		List<String> joinsUsuPerf = new ArrayList<String>();
		joinsUsuPerf.add("UP.ID_PERFIL = PER.ID_PERFIL");
		
		List<String> filterUsuPerf = new ArrayList<String>();
		filterUsuPerf.add("UP.ID_USUARIO = " + idUsuario);

		String queryUsuPerf = consultasGenerales.obtenerQuery(camposUsuPerf, tablasUsuPerf, joinsUsuPerf, filterUsuPerf, null);
		
		// CONSTRUCCION DE QUERY PRINCIPAL (TODOS LOS PERFILES)
		List<String> campos = new ArrayList<String>();
		campos.add("("+queryUsuPerf+") SELECCIONADO");
		campos.add("PER.ID_PERFIL");
		campos.add("PER.DESCRIPCION");
		campos.add("PER.ESTATUS");
		
		List<String> tablas = new ArrayList<String>();
		tablas.add("SEG_PERFIL PER");
		
		//List<String> joins = new ArrayList<String>();
		//joins.add("P.ID_PERFIL = UP.ID_PERFIL");
		
		List<String> condiciones = new ArrayList<String>();
		condiciones.add("ESTATUS = 'A'");

		List<String> orden = new ArrayList<String>();
		orden.add("ID_PERFIL");
		
		List<Map<String, Object>> listDatos = consultasGenerales.obtenerListaDatos(campos, tablas, null, condiciones, orden);
	
		
		
		return listDatos;
	}
	/**
	 * elimina todos las facultades correspondientes al perfil pero
	 * si el tambien contiene valor el id_facultad solo elimina la facultad
	 * y perfil correspondiente 
	 * @param facultad
	 * @return
	 * @throws Exception
	 */
	public int eliminar(int usuario, int perfil){
		
		StringBuffer sb = new StringBuffer();
		sb.append(" DELETE FROM seg_usuario_perfil");
		sb.append("\n WHERE id_usuario = " + usuario);
		sb.append("\n AND id_perfil = " + perfil);
		
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
	public int insertar(int usuario, int perfil){
		
		StringBuffer sb = new StringBuffer();
		sb.append("INSERT INTO seg_usuario_perfil ");
		sb.append("\n SELECT u.id_usuario, p.id_perfil ");
		sb.append("\n FROM seg_usuario u, seg_perfil p "); 
		sb.append("\n WHERE 1=1 "); 
		sb.append("\n AND u.estatus = 'A' "); 
		sb.append("\n AND u.id_usuario = "+ usuario);

		if(perfil>0){
			sb.append("\n AND p.estatus = 'A' "); 
			sb.append("\n AND p.id_perfil = "+ perfil);
		}
		
		sb.append("\n  AND NOT u.id_usuario IN (SELECT up.id_usuario "); 
		sb.append("\n FROM seg_usuario_perfil up ");
		sb.append("\n WHERE up.id_perfil = "+ perfil +" ) ");
		try{
			int res = jdbcTemplate.update(sb.toString());
			return res;
		} catch (Exception e) {
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:SEG, C:SegPerfilFacultadDao, M:insertar");
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
	public int insertar(int usuario, List<Map<String, String>> perfilesMapLst){
		StringBuffer listaPerfiles = new StringBuffer();
		int res = 0;
		
		try{
			StringBuffer sb = new StringBuffer();
			sb.append("DELETE seg_usuario_perfil ");
			sb.append("\n WHERE id_usuario = "+ usuario);
			
			res = jdbcTemplate.update(sb.toString());
			
			for(int i=0; i < perfilesMapLst.size(); i++)
				listaPerfiles.append((i>0 ? ", ":"")+perfilesMapLst.get(i).get("idPerfil"));
			
			sb = new StringBuffer();
			sb.append("INSERT INTO seg_usuario_perfil ");
			sb.append("\n SELECT u.id_usuario, p.id_perfil ");
			sb.append("\n FROM seg_usuario u, seg_perfil p "); 
			sb.append("\n WHERE 1=1 "); 
			sb.append("\n AND u.estatus = 'A' ");
			sb.append("\n AND u.id_usuario = "+ usuario);
			
			if(perfilesMapLst!=null){
				sb.append("\n AND p.estatus = 'A' "); 
				sb.append("\n AND p.id_perfil in ( "+ listaPerfiles + ")");
			}
			
			sb.append("\n  AND NOT u.id_usuario IN (SELECT up.id_usuario "); 
			sb.append("\n FROM seg_usuario_perfil up ");
			sb.append("\n WHERE p.id_perfil in ( "+ listaPerfiles + ") ) ");
		
			

			res = jdbcTemplate.update(sb.toString());
			
			return res;
		} catch (Exception e) {
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:SEG, C:SegPerfilFacultadDao, M:insertar");
			return -1;
		}
	}
	/**
	 * Valida si el perfil esta asignado a un usuario 
	 * Esto se deriva del objSeguridad de VB como gobjSeguridad.ValidaFacultad
	 * @param idUsuario
	 * @param idPerfil
	 * @return
	 */
	public boolean validarPerfilUsuario(int idUsuario, int idPerfil){
		StringBuffer sql = new StringBuffer();
		int res=0;
		try{
			sql.append("SELECT	p.id_perfil");
			sql.append("\n 		FROM 	seg_usuario_perfil sup,");
			sql.append("\n	seg_perfil p, seg_usuario su");
			sql.append("\n	WHERE");
			sql.append("\n	su.id_usuario="+idUsuario);
			sql.append("\n	and sup.id_perfil="+idPerfil);
			sql.append("\n	and	sup.id_usuario=su.id_usuario");
			sql.append("\n	and sup.id_perfil=p.id_perfil");
			sql.append("\n	and p.estatus='A'");
			
			res=jdbcTemplate.queryForInt(sql.toString());
			
			if(res>0)
				return true;
			else 
				return false;
		}catch(Exception e){
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:SEG, C:SegPerfilFacultadDao, M:validarPerfilUsuario");
			return false;
		}
	}
	
	//getters && setters
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
}
