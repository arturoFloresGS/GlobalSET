package com.webset.set.seguridad.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.seguridad.dto.SegCatTipoComponenteDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;

/**
 * 
 * @author Sergio Vaca
 * @since 19/Octubre/2010
 * @see <p>Tabla seg_cat_tipo_componente</p>
 *
 */

/**
 * Clase que contiene los metodos "consultar", "insertar", "modificar", "eliminar", "noNulo", "llenarCombo"
 * del catalogo de componentes
 */

//@Transactional
public class SegCatTipoComponenteDao {
	private JdbcTemplate jdbcTemplate;
	private ConsultasGenerales gral;
	private Bitacora bitacoraDao = new Bitacora();
	private static Logger logger = Logger.getLogger(SegCatTipoComponenteDao.class);
	/**
	 * Consultar datos segun los parametros recibidos
	 * @param componente 
	 * @return List<SegCatTipoComponenteDto>
	 * @throws Exception
	 */
	
	public List<SegCatTipoComponenteDto> consultar(SegCatTipoComponenteDto componente){
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT id_tipo_componente, descripcion, estatus ");
		sb.append(" FROM seg_cat_tipo_componente ");
		ArrayList<String>cond = new ArrayList<String>();
		if(componente!=null){
			if(componente.getIdTipoComponente()>0){
				cond.add(" id_tipo_componente = " + componente.getIdTipoComponente());
			}
			if((componente.getDescripcion()!=null) && !(componente.getDescripcion().equals(""))){
				cond.add(" descripcion = '" + componente.getDescripcion().toUpperCase().trim() + "'" );
			}
			if(componente.getEstatus()!=null && !(componente.getEstatus().equals(""))){
				if(componente.getEstatus().toUpperCase().trim().equals("A") || componente.getEstatus().toUpperCase().trim().equals("I"))
					cond.add(" estatus = '" + componente.getEstatus().toUpperCase().trim() + "'");
			}
			if(cond.size() > 0){
				sb.append(" WHERE " + StringUtils.join(cond, " AND "));
			}
		}
		sql=sb.toString();
		try{
			List <SegCatTipoComponenteDto> componentes = jdbcTemplate.query(sql, new RowMapper<SegCatTipoComponenteDto> (){
				public SegCatTipoComponenteDto mapRow(ResultSet rs, int idx) throws SQLException {
					SegCatTipoComponenteDto comp = new SegCatTipoComponenteDto();
					comp.setIdTipoComponente(rs.getInt("id_tipo_componente"));
					comp.setDescripcion(rs.getString("descripcion"));
					comp.setEstatus(rs.getString("estatus"));
				return comp;
				}});
			return componentes;
		} catch (Exception e) {
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:SEG, C:SegCatTipoComponenteDao, M:consultar");
			return null;
		}
	}
	
	/**
	 * Actualiza el componente  
	 * @param componente
	 * @return int
	 * @throws Exception
	 */
	public int modificar(SegCatTipoComponenteDto componente){

		//final TransactionManager tm = new TransactionManager() ;
		//tm.commit();


		 //tm.begin();
		 
		
		StringBuffer sb = new StringBuffer();
		gral=new ConsultasGenerales(jdbcTemplate);
		int res=-1;
		try{
			
			//tm.begin();
			
			//if(gral.getCountActualizar("seg_cat_tipo_componente", "id_tipo_componente", "clave_tipo_componente", componente.getIdTipoComponente(), componente.getClaveTipoComponente())==0){
				//int retorno = gral.existeCampoClave("seg_componente", "id_tipo_componente", componente.getIdTipoComponente());
				//if(retorno==0){
					sb.append(" UPDATE seg_cat_tipo_componente SET descripcion = ?, estatus = ? ");
					sb.append(" WHERE id_tipo_componente = ? ");
					
					logger.info("modificar SQL:"+sb);
					res = jdbcTemplate.update(sb.toString(),
						new Object[]{componente.getDescripcion().toUpperCase(), componente.getEstatus().toUpperCase(), componente.getIdTipoComponente()}
					    );
				//}
			//}
		} catch (Exception e) {
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:SEG, C:SegCatTipoComponenteDao, M:modificar");
		}
		return res;
	}
	
	/**
	 * eliminar el componente segun el id
	 * @param idComp
	 * @return int
	 * @throws Exception
	 */
	public int eliminar(int idComp){
		int res=-1;
		gral=new ConsultasGenerales(jdbcTemplate);
		try{
			int retorno = gral.existeCampoClave("seg_componente", "id_tipo_componente", idComp);
			if(retorno==0){
				res = jdbcTemplate.update("DELETE  FROM seg_cat_tipo_componente WHERE id_tipo_componente = " + idComp);
			}
			
		} catch (Exception e) {
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:SEG, C:SegCatTipoComponenteDao, M:eliminar");
		}
		return res;
	}

	/**
	 * Insercion de un tipo de componente
	 * @param componente
	 * @return int
	 * @throws Exception
	 */
	public int insertar(SegCatTipoComponenteDto componente) throws Exception{
		int res=-1;
		StringBuffer sb = new StringBuffer();
		gral = new ConsultasGenerales(jdbcTemplate);
		try{
			//if(gral.getCount("seg_cat_tipo_componente", "clave_tipo_componente", componente.getClaveTipoComponente())==0){
				componente.setIdTipoComponente(gral.getSeq("seg_cat_tipo_componente", "id_tipo_componente"));
				sb.append("INSERT INTO seg_cat_tipo_componente ( id_tipo_componente, descripcion, estatus ) ");
				sb.append(" VALUES ( ?, ?, ? ) ");
				res = jdbcTemplate.update(sb.toString(),
						new Object[]{componente.getIdTipoComponente(), componente.getDescripcion().toUpperCase().trim(), componente.getEstatus().toUpperCase().trim()}
				);
			//}
		} catch (Exception e) {
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:SEG, C:SegCatTipoComponenteDao, M:insertar");
		}
		return res;
	}
	/**
	 * 
	 * @param componente
	 * @return false si falta algun campo para la insercion o modificacion 
	 * true sino falta ningun dato
	 */
	public boolean noNulo(SegCatTipoComponenteDto componente){
		if (componente==null 
			 || componente.getDescripcion() == null 
			 || componente.getEstatus() == null  
			 || componente.getDescripcion().equals("")
			 || componente.getEstatus().equals(""))
			return false;
		else
			return true;
	}
	
	/**
	 * retorna una lista de perfiles id_tipo_componente
	 * @param id puede contener los valores de 0 mayor y menor a cero
	 * 0  -> muestra componente activos 
	 * <0 -> muestra todos los componentes ordenados por id_tipo_componente
	 * 0> -> muestra el componente que corresponde al id
	 * @return List<SegCatTipoComponenteDto>
	 */
	
	public List<SegCatTipoComponenteDto> llenarCombo(int id){
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT id_tipo_componente, descripcion ");
		sb.append(" FROM seg_cat_tipo_componente");
		if(id==0){
			sb.append(" WHERE estatus = 'A'");
		}
		else if(id>0){
			sb.append(" WHERE id_tipo_componente = " + id);
		}
		else{
			sb.append(" ORDER BY id_tipo_componente");
		}
		try{
			List <SegCatTipoComponenteDto> componentes = jdbcTemplate.query(sb.toString(), new RowMapper<SegCatTipoComponenteDto> (){
				public SegCatTipoComponenteDto mapRow(ResultSet rs, int idx) throws SQLException {
					SegCatTipoComponenteDto componente = new SegCatTipoComponenteDto();
					componente.setIdTipoComponente(rs.getInt("id_tipo_componente")); 
					componente.setDescripcion(rs.getString("descripcion"));
				return componente;
				}});
			return componentes;
		} catch (Exception e) {
			bitacoraDao.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:SEG, C:SegCatTipoComponenteDao, M:llenarCombo");
			return null;
		}
	}
		
	//getters && setters
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

}
