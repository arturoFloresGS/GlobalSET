package com.webset.set.seguridad.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.seguridad.dto.SegBotonDto;
import com.webset.set.seguridad.dto.SegCatTipoComponenteDto;
import com.webset.set.seguridad.dto.SegComponenteDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public class SegComponenteDao {
	private JdbcTemplate jdbcTemplate;
	private GeneralDao gral;
	Bitacora bitacora=new Bitacora();
	private static Logger logger = Logger.getLogger(SegComponenteDao.class);

	/**
	 * Metodo que consulta la tabla de seg_componente de acuerdo a los parametros
	 * de id_componente o descripcion o estatus  
	 * 
	 * @Author Sergio Vaca
	 *  
	 * Retorna una lista de objetos de tipo o solamente una tupla 
	 */
	public List<SegComponenteDto> consultar(SegComponenteDto componente){
		String sql = "";
		List <SegComponenteDto> componentes=null;
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT id_componente, clave_componente, descripcion, estatus, id_tipo_componente, ");
		sb.append(" ruta_imagen, id_componente_padre, clave_componente_padre ");
		sb.append(" FROM seg_componente ");
		ArrayList<String>cond = new ArrayList<String>();
		try{
			if(componente!=null){
				if(componente.getIdComponente() >0){
					cond.add(" id_componente = " + componente.getIdComponente());
				}
				if(componente.getClaveComponente()!=null && !(componente.getClaveComponente().equals(""))){
					cond.add(" clave_componente = '" + componente.getClaveComponente() + "'");
				}
				if((componente.getDescripcion()!=null) && !(componente.getDescripcion().equals(""))){
					cond.add(" descripcion = '" + componente.getDescripcion() + "'" );
				}
				if(componente.getEstatus()!=null && !(componente.getEstatus().equals(""))){
					if(componente.getEstatus().equals("A") || componente.getEstatus().equals("I"))
						cond.add(" estatus = '" + componente.getEstatus() + "'");
				} 
				if(componente.getIdTipoComponente()>0){
					cond.add(" id_tipo_componente = " + componente.getIdTipoComponente());
				}
				if(cond.size() > 0){
					sb.append(" WHERE " + StringUtils.join(cond, " AND "));
				}
			}
		sql=sb.toString();
		componentes = jdbcTemplate.query(sql, new RowMapper<SegComponenteDto>(){
			public SegComponenteDto mapRow(ResultSet rs, int idx) throws SQLException {
				SegComponenteDto comp = new SegComponenteDto();
				comp.setIdComponente(rs.getInt("id_componente"));
				comp.setClaveComponente(rs.getString("clave_componente"));
				comp.setDescripcion(rs.getString("descripcion"));
				comp.setEstatus(rs.getString("estatus"));
				comp.setIdTipoComponente(rs.getInt("id_tipo_componente"));
				comp.setRutaImagen(rs.getString("ruta_imagen"));
				comp.setIdComponentePadre(rs.getInt("id_componente_padre"));
				comp.setClaveComponentePadre(rs.getString("clave_componente_padre"));
			return comp;
			}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:consultar");
		}
		return componentes;

	}
	/**
	 * 
	 * @param componente
	 * @return lista de tipo SegCatTipoComponenteDto 
	 * @throws Exception
	 */
	
	public List<SegCatTipoComponenteDto> consultarTipoComponente(SegCatTipoComponenteDto componente){
		String sql = "";
		List <SegCatTipoComponenteDto> componentes=null;
		try{
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT id_tipo_componente,descripcion, estatus");
		sb.append(" FROM seg_cat_tipo_componente ");
		ArrayList<String>cond = new ArrayList<String>();
		if(componente!=null){
			if(componente.getIdTipoComponente() >0){
				cond.add(" id_tipo_componente = " + componente.getIdTipoComponente());
			}
			/*
			if(componente.getClaveTipoComponente()!=null && !(componente.getClaveTipoComponente().equals(""))){
				cond.add(" clave_tipo_componente = '" + componente.getClaveTipoComponente() + "'");
			}
			*/
			if((componente.getDescripcion()!=null) && !(componente.getDescripcion().equals(""))){
				cond.add(" descripcion = '" + componente.getDescripcion() + "'" );
			}
			if(componente.getEstatus()!=null && !(componente.getEstatus().equals(""))){
				if(componente.getEstatus().equals("A") || componente.getEstatus().equals("I"))
					cond.add(" estatus = '" + componente.getEstatus() + "'");
			} 
			
			if(cond.size() > 0){
				sb.append(" WHERE " + StringUtils.join(cond, " AND "));
			}
		}
		sql=sb.toString();
	
		 componentes = jdbcTemplate.query(sql, new RowMapper<SegCatTipoComponenteDto>(){
			public SegCatTipoComponenteDto mapRow(ResultSet rs, int idx) throws SQLException {
				SegCatTipoComponenteDto comp = new SegCatTipoComponenteDto();
				comp.setIdTipoComponente(rs.getInt("id_tipo_componente"));
				//comp.setClaveTipoComponente(rs.getString("clave_tipo_componente"));
				comp.setDescripcion(rs.getString("descripcion"));
				comp.setEstatus(rs.getString("estatus"));
			return comp;
			}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Seguridad, C:SegComponenteDao, M:consultarTipoComponente");
				}
		return componentes;
	}
	
	/**
	 * Actualiza la tabla de seg_componente al componente en base a la informacion proporcionada en el objeto componente
	 * segun el campo id_componente
	 * retorna un entero
	**/
	public int modificar(SegComponenteDto componente){
		int res=0;
		StringBuffer sb = new StringBuffer();
		sb.append(" UPDATE seg_componente SET clave_componente = ?, descripcion = ?, estatus = ?, id_tipo_componente = ?, ruta_imagen = ?, ");
		sb.append(" id_componente_padre = ?, clave_componente_padre = ? ");
		sb.append(" WHERE id_componente = ? ");
		try
		{
			 res = jdbcTemplate.update(sb.toString(),
				new Object[]{componente.getClaveComponente().toUpperCase(), componente.getDescripcion().toUpperCase(), componente.getEstatus().toUpperCase(), componente.getIdTipoComponente(), componente.getRutaImagen(),
					componente.getIdComponentePadre(),componente.getClaveComponentePadre(), componente.getIdComponente()
				}
			);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:modificar");
		}
		return res;
	}
	
	/**
	 * Elimina de la tabla de seg_componente el componente identificado por el id_tipo_componente 
	**/
	public int eliminar(int idComp){
		int res=0;
		try{
		res = jdbcTemplate.update("DELETE seg_componente  WHERE id_componente = " + idComp);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:eliminar");
		}
		return res;
	}

	/**
	 * Inserta un nuevo registro en seg_componente 
	**/
	public int insertar(SegComponenteDto componente) throws Exception{
		int res=0;
		StringBuffer sb = new StringBuffer();
		try
		{
			gral = new GeneralDao(jdbcTemplate);
			componente.setIdComponente(gral.getSeq("seg_componente", "id_componente"));
			sb.append("INSERT INTO seg_componente ( id_componente, descripcion,id_componente_padre,id_tipo_componente, estatus,ruta_imagen,clave_componente_padre,clave_componente)");
			sb.append(" VALUES(?, ?, ?, ?, ?, ?, ?, ?) ");
			res = jdbcTemplate.update(sb.toString(),
				new Object[]{componente.getIdComponente(),componente.getDescripcion().toUpperCase(), componente.getIdComponentePadre(),
				componente.getIdTipoComponente(), componente.getEstatus().toUpperCase(),componente.getRutaImagen(),
				componente.getClaveComponentePadre(),componente.getClaveComponente().toUpperCase()
							
				}
			);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponentesDao, M:insertar");
		}
		return res;
	}
	
	public boolean noNulo(SegComponenteDto componente){
		if(componente==null 
			|| componente.getClaveComponente()==null 
			|| componente.getDescripcion()==null 
			|| componente.getEstatus()==null 
			|| componente.getRutaImagen() == null
			|| componente.getIdComponentePadre()<0 
			|| componente.getIdTipoComponente()<0 
			|| componente.getClaveComponente().equals("") 
			|| componente.getDescripcion().equals("")
			|| componente.getEstatus().equals("") 
			|| componente.getRutaImagen().equals(""))
			return false;
		else
			return true;
	}
	/**
	 * Falta definirlo bien 
	 * @param idComp
	 * @return retorna la consulta segun el id_tipo_componente que escoja
	 * si es boton solo retorna pantallas, si es pantalla solo modulos y si son
	 * modulos solo modulos  
	 */
	
	public List<SegComponenteDto> obtenerComponentePadre(int idComp){
		String sql="";
		StringBuffer sb = new StringBuffer();
		List <SegComponenteDto> componentes=null;
		sb.append(" SELECT id_componente, clave_componente");
		sb.append(" FROM seg_componente");
		try
		{
			if(idComp==1)
				sb.append(" WHERE id_tipo_componente = " + idComp);
			else if(idComp > 1)
				sb.append(" WHERE id_tipo_componente = " + (idComp-1));
			else{
				sb.append(" WHERE estatus = 'A' ");
				sb.append(" ORDER BY id_componente ");
			}
			sql=sb.toString();
			componentes = jdbcTemplate.query(sql, new RowMapper<SegComponenteDto>(){
				public SegComponenteDto mapRow(ResultSet rs, int idx) throws SQLException {
					SegComponenteDto comp = new SegComponenteDto();
					comp.setIdComponente(rs.getInt("id_componente"));
					comp.setClaveComponente(rs.getString("clave_componente"));
					comp.setDescripcion(rs.getString("descripcion"));
				return comp;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:obtenerComponentePadre");
		}
		return componentes;
	}
	/**
	 * retorna una lista de perfiles id_tipo_componente y clave_tipo_componente
	 * @param id puede contener los valores de 0 mayor y menor a cero
	 * 0  -> muestra componente activos 
	 * <0 -> muestra todos los componentes ordenados por id_tipo_componente
	 * 0> -> muestra el componente que corresponde al id
	 * @return
	 */
	public List<SegComponenteDto> llenarCombo(int id){
		List <SegComponenteDto> componentes=null;
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT id_componente, clave_componente ");
		sb.append(" FROM seg_componente");
		try{
			if(id==0){
				sb.append(" WHERE estatus = 'A'");
			}
			else if(id>0){
				sb.append(" WHERE id_componente = " + id);
			}
			else{
				sb.append(" ORDER BY id_componente");
			}
		 componentes = jdbcTemplate.query(sb.toString(), new RowMapper<SegComponenteDto>(){
				public SegComponenteDto mapRow(ResultSet rs, int idx) throws SQLException {
					SegComponenteDto componente = new SegComponenteDto();
					componente.setIdComponente(rs.getInt("id_componente")); 
					componente.setClaveComponente(rs.getString("clave_componente")); 
				return componente;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:llenarCombo");
		}
		return componentes;
	}

	/**
	 * retorna una lista de componentes id_tipo_componente y clave_tipo_componente
	 * @param id puede contener los valores de 0 mayor y menor a cero
	 * 0  -> muestra componente activos 
	 * <0 -> muestra todos los componentes ordenados por id_tipo_componente
	 * 0> -> muestra el componente que corresponde al id
	 * @return
	 */
	public List<SegComponenteDto> obtenerComponentesUsuario(String usuarioLogin, int idTipoComponente, int idFacultad){
		List <SegComponenteDto> componentes = null;
		StringBuffer sb = new StringBuffer();
		try{
			sb.append(" select distinct c.orden as orden, pfc.id_componente as id_componente, c.etiqueta as etiqueta, c.ruta_imagen as ruta_imagen");
			sb.append(" from seg_usuario u, seg_usuario_perfil up, seg_per_fac_com pfc, seg_componente c ");
			sb.append(" where");
			sb.append(" up.id_usuario = u.id_usuario ");
			sb.append(" and pfc.id_perfil = up.id_perfil ");
			sb.append(" and c.id_componente = pfc.id_componente ");
			sb.append(" and u.estatus = 'A' ");
			sb.append(" and c.estatus = 'A' ");
			sb.append(" and c.id_tipo_componente = " + idTipoComponente);
			sb.append(" and pfc.id_facultad = "+ idFacultad);
			sb.append(" and u.clave_usuario = '" + usuarioLogin + "'");
			sb.append(" order by etiqueta ");
			
			logger.info(sb.toString());
			componentes = jdbcTemplate.query(sb.toString(), new RowMapper<SegComponenteDto>(){
				public SegComponenteDto mapRow(ResultSet rs, int idx) throws SQLException {
					SegComponenteDto componente = new SegComponenteDto();
					componente.setIdComponente(rs.getInt("id_componente")); 
					componente.setEtiqueta(rs.getString("etiqueta")); 
					componente.setRutaImagen(rs.getString("ruta_imagen")); 
				return componente;
				}
			});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:obtenerComponentesUsuario");
		}
		return componentes;
	}

	private String sMenuTree = "";
	
	/** 
	* M�todo para obtener una arbol de cada submenu
	* 
	* @return  ArrayList
	* @since   20/Dic/2010
	* @author  Armando Rodriguez Meneses
	*/
	
	
	public String obtenerArbolSubMenus(String usuarioLogin, int nPadre, Map<String, String> componenteHashRef, String sParmMenuTree){
		String sMenuTree = sParmMenuTree;
		List<Map<String, String>> listComponentes = new ArrayList<Map<String, String>>(); 
		boolean existieron = false;
	
		StringBuffer sb = new StringBuffer();

		try{
			sb.append(" select pfc.id_componente as id_componente, c.etiqueta as etiqueta, c.url as url, c.ruta_imagen as ruta_imagen, c.id_componente_padre as id_componente_padre");
			sb.append(" from seg_usuario u, seg_usuario_perfil up, seg_per_fac_com pfc, seg_componente c ");
			sb.append(" where ");
			sb.append(" up.id_usuario = u.id_usuario ");
			//sb.append(" and up.clave_usuario = u.clave_usuario ");
			sb.append(" and pfc.id_perfil = up.id_perfil ");
			sb.append(" and c.id_componente = pfc.id_componente ");
			sb.append(" and u.estatus = 'A' ");
			sb.append(" and c.estatus = 'A' ");
			sb.append(" and c.id_tipo_componente = 2");
			sb.append(" and pfc.id_facultad = 2");
			if(!usuarioLogin.equals(""))
				sb.append(" and u.clave_usuario = '" + usuarioLogin + "'");
			if(nPadre!=0)
				sb.append(" and c.id_componente_padre = " + nPadre); 
			sb.append(" order by orden ");

			logger.info("\nSQL "+nPadre+"="+sb);
			
			listComponentes = jdbcTemplate.query(sb.toString(), new RowMapper<Map<String, String>>(){
				public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException {
					//SegComponenteDto componente = new SegComponenteDto();
				
					logger.info("\nmapRow "+idx);
					HashMap<String, String> componenteHash = new HashMap<String, String>();
					
					componenteHash.put("id_componente", rs.getString("id_componente")); 
					componenteHash.put("etiqueta", rs.getString("etiqueta")); 
					componenteHash.put("url", rs.getString("url")); 
					componenteHash.put("ruta_imagen", rs.getString("ruta_imagen")); 
					componenteHash.put("id_componente_padre", rs.getString("id_componente_padre")); 
				return componenteHash;
				}
			});
			
			if(listComponentes.isEmpty() && componenteHashRef == null){  // is leaf
				
				logger.info("\nSaliendo "+nPadre);
				return "{}";
			}
	
			if(listComponentes.isEmpty()){  // is leaf
				logger.info("\ncomponenteHashRef="+componenteHashRef);

				// GENERAR EL NOMBRE DEL ITEM
				String sMenuImg = componenteHashRef.get("ruta_imagen")==null? "":componenteHashRef.get("ruta_imagen").toString().trim(); 
				logger.info("\nsMenuImg="+sMenuImg);

				sMenuTree += ",iconCls:'"+ (sMenuImg.equals("")? "user":sMenuImg) +"', leaf:true ";

				
				sMenuTree += ",attributes:{iconCls:'"+(sMenuImg.equals("")? "user":sMenuImg)+"'}";
				
				// GENERAR EL LINK DEL ITEM
				String sPagina = componenteHashRef.get("url")==null? "":componenteHashRef.get("url").toString().trim();
				String sID = componenteHashRef.get("id_componente")==null? "":componenteHashRef.get("id_componente").toString().trim();
				
				if(!sPagina.equals("")){
					sMenuTree += ",id:'"+sID+"-"+sPagina+"'";
					sMenuTree += ",href:'#"+sPagina+"'";
				}
	
				sMenuTree += "\n}\n";
	
			}else
				existieron = true;

	
		}
		catch(Exception e){e.printStackTrace();}
	
		if(existieron){   
			if(componenteHashRef!=null)
				sMenuTree +=  ",children:\n";
			
			sMenuTree +=  "[{\n";
	
			Iterator<Map<String, String>> iterComp = listComponentes.iterator();
			boolean primerItem = true;
				
			while( iterComp.hasNext() ) {
				HashMap<String, String> hashFunc = (HashMap<String, String>)iterComp.next();
	
				if(primerItem)
					primerItem = false;
				else
					sMenuTree += ",{";
	
				String sMenu = hashFunc.get("etiqueta")==null? "":hashFunc.get("etiqueta").toString().trim();
	
				// GENERAR EL NOMBRE DEL ITEM
				sMenuTree += "text:'"+sMenu+"', singleClickExpand:true ";
				// RECURSIVO
				sMenuTree = obtenerArbolSubMenus(usuarioLogin, Integer.parseInt(hashFunc.get("id_componente").toString()), hashFunc, sMenuTree);
				//sMenuTree += "}\n";
			}
	
			sMenuTree += "]\n";
	
			if(componenteHashRef!=null)
				sMenuTree +=  "}\n";
			
		} // else if(!existen)...
	
	
		return sMenuTree;
	}
	
	public String obtenerArbolComponentes(int nPadre, Map<String, String> componenteHashRef, String sParmMenuTree, int noPerfil){
		String sMenuTree = sParmMenuTree;
		List<Map<String, String>> listComponentes = new ArrayList<Map<String, String>>(); 
		
		boolean existieron = false;
		StringBuffer sb = new StringBuffer();
		int iNoPerfil = noPerfil;
		
		try{
			sb.append(" select id_componente, etiqueta, ruta_imagen, id_componente_padre ");
			sb.append(" from seg_componente ");
			sb.append(" where estatus = 'A' ");
			sb.append(" and id_componente_padre = " + nPadre);
			sb.append(" and ID_TIPO_COMPONENTE != 3");
			sb.append(" order by orden ");
			
			listComponentes = jdbcTemplate.query(sb.toString(), new RowMapper<Map<String, String>>(){
				public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException {
					HashMap<String, String> componenteHash = new HashMap<String, String>();
					
					componenteHash.put("id_componente", rs.getString("id_componente")); 
					componenteHash.put("etiqueta", rs.getString("etiqueta")); 
					componenteHash.put("ruta_imagen", rs.getString("ruta_imagen")); 
					componenteHash.put("id_componente_padre", rs.getString("id_componente_padre")); 
					return componenteHash;
				}
			});
			if(listComponentes.isEmpty() && componenteHashRef == null) { return "{}"; }
			
			if(listComponentes.isEmpty()){  // is leaf
				logger.info("\ncomponenteHashRef= ["+componenteHashRef+"]");
				
				// GENERAR EL NOMBRE DEL ITEM
				String sMenuImg = componenteHashRef.get("ruta_imagen")==null? "":componenteHashRef.get("ruta_imagen").toString().trim(); 
				//logger.info("\nsMenuImg="+sMenuImg);
				
				sMenuTree += ",iconCls:'"+ (sMenuImg.equals("")? "user":sMenuImg) +"', leaf:true";
				
				//Se agrega el ID
				//sMenuTree += ",id:"+ componenteHashRef.get("id_componente")+"";
				
				if(componentesAsigandos(iNoPerfil, componenteHashRef.get("id_componente") == null ? 0 : Integer.parseInt(componenteHashRef.get("id_componente")))) {
					sMenuTree += ", checked:true ";
				}
				sMenuTree += ",attributes:{iconCls:'"+(sMenuImg.equals("")? "user":sMenuImg)+"'}";
				
				// GENERAR EL LINK DEL ITEM
				String sPagina = componenteHashRef.get("url")==null? "":componenteHashRef.get("url").toString().trim();
				String sID = componenteHashRef.get("id_componente")==null? "":componenteHashRef.get("id_componente").toString().trim();
				
				if(!sPagina.equals("")){
					sMenuTree += ",id:'"+sID+"-"+sPagina+"'";
					//sMenuTree += ",href:'#"+sPagina+"'";
				}
				sMenuTree += "\n}\n";
			}else
				existieron = true;
		}catch(Exception e){e.printStackTrace();}
		
		if(existieron) {
			if(componenteHashRef != null) sMenuTree +=  ",children:\n";
			sMenuTree +=  "[{\n";
			Iterator<Map<String, String>> iterComp = listComponentes.iterator();
			boolean primerItem = true;
			
			while( iterComp.hasNext() ) {
				HashMap<String, String> hashFunc = (HashMap<String, String>)iterComp.next();
				
				if(primerItem)
					primerItem = false;
				else
					sMenuTree += ",{";
				String sMenu = hashFunc.get("etiqueta")==null? "":hashFunc.get("etiqueta").toString().trim();
				
				//Se agrega el ID
				sMenuTree += "id:"+ hashFunc.get("id_componente")+", ";
				// GENERAR EL NOMBRE DEL ITEM
				sMenuTree += "text:'"+sMenu+"', singleClickExpand:true ";
				
				if(componentesAsigandos(iNoPerfil, hashFunc.get("id_componente") == null ? 0 : Integer.parseInt(hashFunc.get("id_componente").toString()))) {
					sMenuTree += ", checked:true ";
				}
				// RECURSIVO
				sMenuTree = obtenerArbolComponentes(Integer.parseInt(hashFunc.get("id_componente").toString()), hashFunc, sMenuTree, iNoPerfil);
				//sMenuTree += "}\n";
			}
			sMenuTree += "]\n";
			
			if(componenteHashRef!=null)
				sMenuTree +=  "}\n";
		} // else if(!existen)...
		return sMenuTree;
	}
	
	public boolean componentesAsigandos(int noPerfil, int idComponente) {
		StringBuffer sql = new StringBuffer();
		boolean resp = false;
		int result = 0;
		
		try {
			sql.append(" SELECT count(*) FROM SEG_PER_FAC_COM");
			sql.append("\n WHERE id_perfil = " + noPerfil +"");
			sql.append("\n And id_componente = " + idComponente + "");
			sql.append("\n And id_facultad = 2");
			
			result = jdbcTemplate.queryForInt(sql.toString());
			
			if(result != 0) resp = true;
		}catch(Exception e) {e.printStackTrace();}
		return resp;
	}
	
	/**
	 * Actualiza los componentes de un Perfil.
	 * @param idPerfil
	 * @param sComponentes
	 * @return
	 */
	public boolean actualizarComponentesPerfil(int idPerfil, String sComponentes) {
		boolean resp = false;
		StringBuffer sql = new StringBuffer();
		
		try
		{
			// Se borran los componentes del Perfil
			sql.append(" DELETE FROM SEG_PER_FAC_COM ");
			sql.append(" WHERE id_perfil = " +idPerfil);
			
			jdbcTemplate.update(sql.toString());
			
			// Se insertan los nuevos componentes asignados al Perfil.
			StringTokenizer st = new StringTokenizer(sComponentes);
			while (st.hasMoreElements())
			{
				sql = new StringBuffer();
				sql.append(" INSERT INTO SEG_PER_FAC_COM (id_perfil, id_componente, id_facultad) ");
				sql.append(" VALUES ("+idPerfil+", "+st.nextToken()+", 2) ");
				
				jdbcTemplate.update(sql.toString());
			}
			resp=true;
		}
		catch(Exception e) {e.printStackTrace();}
		
		return resp;
	}
	
	//getters && setters
	public String getMenuTree(){
		return this.sMenuTree;
	}
	public void setMenuTree(String menu){
		this.sMenuTree = menu;
	}
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	public List<SegComponenteDto> llenaComponentes(int tipoComponente) {
		List<SegComponenteDto> componentes = null;
		StringBuffer sb = new StringBuffer();
		sb.append("select * from SEG_COMPONENTE where id_tipo_componente = " + tipoComponente + " order by orden");
		try {
		componentes = jdbcTemplate.query(sb.toString(), new RowMapper<SegComponenteDto>(){
			public SegComponenteDto mapRow(ResultSet rs, int idx) throws SQLException {
				SegComponenteDto comp = new SegComponenteDto();
				comp.setIdComponente(rs.getInt("id_componente"));
				comp.setDescripcion(rs.getString("descripcion"));
				comp.setEstatus(rs.getString("estatus"));
				comp.setIdTipoComponente(rs.getInt("id_tipo_componente"));
				comp.setIdComponentePadre(rs.getInt("id_componente_padre"));
				comp.setEtiqueta(rs.getString("etiqueta"));
				comp.setRutaImagen(rs.getString("ruta_imagen"));
				comp.setURL(rs.getString("url"));
			return comp;
			}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:llenaComponentes");
		}
		return componentes;
	}
	public int guardarModulo(SegComponenteDto datos) {
		int r = -1;
		try {
			//ConsultasGenerales consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			//int folio = consultasGenerales.obtenFolio("seg_componente");
			StringBuffer sql = new StringBuffer();
			sql.append("insert into seg_componente values(");
			sql.append("(select max(ID_COMPONENTE)+1 from seg_componente)");
			//sql.append(folio);
			sql.append(", '");
			sql.append(datos.getDescripcion());
			sql.append("', 1, '");
			sql.append(datos.getEstatus());
			sql.append("', '");
			sql.append(datos.getRutaImagen());
			sql.append("', 0, NULL, '");
			sql.append(datos.getEtiqueta());
			sql.append("', (select max(orden) + 1 from seg_componente where id_tipo_componente = 1), NULL, NULL)");
			r = jdbcTemplate.update(sql.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:guardarModulo");
		}
		return r;
	}
	public boolean tieneHijos(String id) {
		boolean r = true;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select count(ID_COMPONENTE) from seg_componente where ID_COMPONENTE_PADRE = ");
			sql.append(id);
			r = jdbcTemplate.queryForInt(sql.toString()) > 0;
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:eliminarModulo");
		}
		return r;
	}
	public int eliminarModulo(String id) {
		int r = -1;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("delete from seg_componente where ID_COMPONENTE = ");
			sql.append(id);
			r = jdbcTemplate.update(sql.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:eliminarModulo");
		}
		return r;
	}
	public int modificarModulo(SegComponenteDto datos) {
		int r = -1;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("update seg_componente set descripcion = ");
			sql.append("'");
			sql.append(datos.getDescripcion());
			sql.append("', estatus = '");
			sql.append(datos.getEstatus());
			sql.append("', ruta_imagen = '");
			sql.append(datos.getRutaImagen());
			sql.append("', etiqueta = '");
			sql.append(datos.getEtiqueta());
			sql.append("' where id_componente = ");
			sql.append(datos.getIdComponente());
			r = jdbcTemplate.update(sql.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:modificarModulo");
		}
		return r;
	}
	public String obtenerArbolModulo(int nPadre, Map<String, String> componenteHashRef, String sParmMenuTree){
		String sMenuTree = sParmMenuTree;
		List<Map<String, String>> listComponentes = new ArrayList<Map<String, String>>(); 
		
		boolean existieron = false;
		//int iNoPerfil = noPerfil;
		
		try{
			listComponentes = obtenerModulosHijo(nPadre);
			if(listComponentes.isEmpty() && componenteHashRef == null)
				return "{}";
			
			if(listComponentes.isEmpty()){  // is leaf
				
				// GENERAR EL NOMBRE DEL ITEM
				String sMenuImg = componenteHashRef.get("ruta_imagen") == null ? 
						"" :
						componenteHashRef.get("ruta_imagen").toString().trim(); 
				
				sMenuTree += ",iconCls:'"+ (sMenuImg.equals("") ? 
						"user":
						sMenuImg) +"', leaf:true";
				
				//Se agrega el ID
				//sMenuTree += ",id:"+ componenteHashRef.get("id_componente")+"";
				
//				if(componenteHashRef.get("estatus").toString().equals("A")) {
//					sMenuTree += ", checked:true ";
//				}
				sMenuTree += ",attributes:{iconCls:'"+(sMenuImg.equals("") ? 
						"user":
						sMenuImg)+"'}\n}\n";
			}else
				existieron = true;
		}catch(Exception e){e.printStackTrace();}
		
		if(existieron) {
			if(componenteHashRef != null) 
				sMenuTree +=  ",children:\n";
			
			sMenuTree +=  "[{\n";
			Iterator<Map<String, String>> iterComp = listComponentes.iterator();
			boolean primerItem = true;
			
			while( iterComp.hasNext() ) {
				HashMap<String, String> hashFunc = (HashMap<String, String>)iterComp.next();
				
				if(primerItem)
					primerItem = false;
				else
					sMenuTree += ",{";
				
				String sMenu = hashFunc.get("etiqueta")==null? 
						"":
						hashFunc.get("etiqueta").toString().trim();
				
				//Se agrega el ID
				sMenuTree += "id:"+ hashFunc.get("id_componente")+", ";
				// GENERAR EL NOMBRE DEL ITEM
				sMenuTree += "text:'"+sMenu+"', singleClickExpand:true ";
				
//				if(hashFunc.get("estatus").toString().equals("A")) {
//					sMenuTree += ", checked:true ";
//				}
				// RECURSIVO
				sMenuTree = obtenerArbolModulo(
						Integer.parseInt(hashFunc.get("id_componente").toString()), 
						hashFunc, sMenuTree);
			}
			sMenuTree += "]\n";
			
			if(componenteHashRef!=null)
				sMenuTree +=  "}\n";
		}
		return sMenuTree;
	}
	private List<Map<String, String>> obtenerModulosHijo(int nPadre) {
		List<Map<String, String>> listComponentes = new ArrayList<Map<String, String>>();
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(" select id_componente, etiqueta, id_componente_padre, estatus, url, ");
			sb.append(" case when (URL is null or url = '') and "); 
			sb.append(" (select Count(*) from SEG_COMPONENTE ");
			sb.append(" where id_componente_padre = ID_COMPONENTE) = 0 then 'menu_folder' ");
			sb.append(" else ruta_imagen ");
			sb.append(" end as ruta_imagen ");
			sb.append(" from seg_componente ");
			sb.append(" where ");
			sb.append(" id_componente_padre = " + nPadre);
			sb.append(" order by orden ");
			
			listComponentes = jdbcTemplate.query(sb.toString(), new RowMapper<Map<String, String>>(){
				public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException {
					HashMap<String, String> componenteHash = new HashMap<String, String>();
					
					componenteHash.put("id_componente", rs.getString("id_componente")); 
					componenteHash.put("etiqueta", rs.getString("etiqueta")); 
					componenteHash.put("ruta_imagen",rs.getString("ruta_imagen")); 
					componenteHash.put("id_componente_padre", rs.getString("id_componente_padre"));
					componenteHash.put("estatus", rs.getString("estatus"));
					return componenteHash;
				}
			});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:obtenerModulosHijo");
		}
		return listComponentes;
	}
	
	public List<SegComponenteDto> obtenerComponentesPadre(int idComponente) {
		List<SegComponenteDto> componentes = null;
		StringBuffer sb = new StringBuffer();
		sb.append("select * from SEG_COMPONENTE where id_tipo_componente in (1,2)");
		sb.append(" and (id_componente = ");
		sb.append(idComponente);
		sb.append(" or id_componente_padre = ");
		sb.append(idComponente);
		sb.append(" and (url = '' or url is null)) order by id_tipo_componente");
		System.out.println(sb.toString());
		try {
		componentes = jdbcTemplate.query(sb.toString(), new RowMapper<SegComponenteDto>(){
			public SegComponenteDto mapRow(ResultSet rs, int idx) throws SQLException {
				SegComponenteDto comp = new SegComponenteDto();
				comp.setIdComponente(rs.getInt("id_componente"));
				comp.setDescripcion(rs.getString("descripcion"));
				comp.setEstatus(rs.getString("estatus"));
				comp.setIdTipoComponente(rs.getInt("id_tipo_componente"));
				comp.setIdComponentePadre(rs.getInt("id_componente_padre"));
				comp.setEtiqueta(rs.getString("etiqueta"));
				comp.setRutaImagen(rs.getString("ruta_imagen"));
				comp.setURL(rs.getString("url"));
			return comp;
			}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:obtenerComponentesPadre");
		}
		return componentes;
	}
	public int guardarComponente(SegComponenteDto datos) {
		int r = -1;
		try {
			//ConsultasGenerales consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			//int folio = consultasGenerales.obtenFolio("seg_componente");
			StringBuffer sql = new StringBuffer();
			sql.append("insert into seg_componente values(");
			sql.append("(select max(ID_COMPONENTE)+1 from seg_componente)");
			//sql.append(folio);
			sql.append(", '");
			sql.append(datos.getDescripcion());
			sql.append("', 2, '");
			sql.append(datos.getEstatus());
			sql.append("', 'X', ");
			sql.append(datos.getIdComponentePadre());
			sql.append(", NULL, '");
			sql.append(datos.getEtiqueta());
			sql.append("', (select max(orden) + 1 from seg_componente where id_tipo_componente = 1), ");
			sql.append(datos.getURL().equals("") ? 
					"NULL" : "'" + datos.getURL() + "'");
			sql.append(", NULL)");
			r = jdbcTemplate.update(sql.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:guardarComponente");
		}
		return r;
	}
	public Map<String, Object> obtenerComponente(int idComponente) {
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("estatus", false);
		resultado.put("error", "Error desconocido.");
		try {
			StringBuffer sb = new StringBuffer();
			sb.append(" select id_componente, etiqueta, descripcion, id_componente_padre, estatus, url, ");
			sb.append(" case when (URL is null or url = '') then '0' ");
			sb.append(" else '1' ");
			sb.append(" end as tipo ");
			sb.append(" from seg_componente ");
			sb.append(" where ");
			sb.append(" id_componente = " + idComponente);
			
			List<SegComponenteDto> listComponentes = jdbcTemplate.query(sb.toString(), new RowMapper<SegComponenteDto>(){
				public SegComponenteDto mapRow(ResultSet rs, int idx) throws SQLException {
					SegComponenteDto segComponenteDto = new SegComponenteDto();
					
					segComponenteDto.setIdComponente(rs.getInt("id_componente")); 
					segComponenteDto.setEtiqueta(rs.getString("etiqueta"));  
					segComponenteDto.setIdComponentePadre(rs.getInt("id_componente_padre"));
					segComponenteDto.setEstatus(rs.getString("estatus"));
					segComponenteDto.setURL(rs.getString("url"));
					segComponenteDto.setDescripcion(rs.getString("descripcion"));
					segComponenteDto.setIdTipoComponente(rs.getInt("tipo"));
					
					return segComponenteDto;
				}
			});
			
			if (listComponentes != null && !listComponentes.isEmpty()) {
				resultado.put("estatus", true);
				resultado.put("informacion", listComponentes.get(0));
			} else {
				resultado.put("error", "No se encontro la informaci�n.");
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:obtenerComponente");
		}
		return resultado;
	}
	public int modificarComponente(SegComponenteDto datos) {
		int r = -1;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("update seg_componente set descripcion = ");
			sql.append("'");
			sql.append(datos.getDescripcion());
			sql.append("', estatus = '");
			sql.append(datos.getEstatus());
			sql.append("', url = ");
			sql.append(datos.getURL().equals("") ? 
					"NULL" : "'" + datos.getURL() + "'");
			sql.append(", etiqueta = '");
			sql.append(datos.getEtiqueta());
			sql.append("', id_componente_padre = ");
			sql.append(datos.getIdComponentePadre());
			sql.append(" where id_componente = ");
			sql.append(datos.getIdComponente());
			
			r = jdbcTemplate.update(sql.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:modificarComponente");
		}
		return r;
	}
	public List<SegBotonDto> llenaBotones(int idModulo) {
		List<SegBotonDto> componentes = null;
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM SEG_FAC_BOTON WHERE ID_MODULO = ");
		sb.append(idModulo);
		sb.append(" ORDER BY ID_BOTON");
		try {
		componentes = jdbcTemplate.query(sb.toString(), new RowMapper<SegBotonDto>(){
			public SegBotonDto mapRow(ResultSet rs, int idx) throws SQLException {
				SegBotonDto comp = new SegBotonDto();
				comp.setIdComponente(rs.getInt("id_componente"));
				comp.setDescripcion(rs.getString("descripcion"));
				comp.setEstatus(rs.getString("estatus"));
				comp.setIdModulo(rs.getInt("id_modulo"));
				comp.setIdBoton(rs.getString("id_boton"));
				return comp;
			}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:llenaBotones");
		}
		return componentes;
	}
	public List<SegComponenteDto> obtenerPantallas(int idModulo) {
		List<SegComponenteDto> componentes = null;
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM SEG_COMPONENTE WHERE ID_COMPONENTE_PADRE = ");
		sb.append(idModulo);
		sb.append("\n");
		sb.append("AND (NOT URL IS NULL OR URL != '')\n");
		sb.append("UNION\n");
		sb.append("SELECT P.* FROM\n");
		sb.append("((SELECT * FROM SEG_COMPONENTE WHERE ID_COMPONENTE_PADRE = ");
		sb.append(idModulo);
		sb.append("\n");
		sb.append("AND (URL IS NULL OR URL = '')) M\n");
		sb.append("JOIN\n");
		sb.append("(SELECT * FROM SEG_COMPONENTE WHERE NOT URL IS NULL OR URL != '') P\n");
		sb.append("ON M.ID_COMPONENTE = P.ID_COMPONENTE_PADRE)\n");
		System.out.println(sb.toString());
		try {
		componentes = jdbcTemplate.query(sb.toString(), new RowMapper<SegComponenteDto>(){
			public SegComponenteDto mapRow(ResultSet rs, int idx) throws SQLException {
				SegComponenteDto comp = new SegComponenteDto();
				comp.setIdComponente(rs.getInt("id_componente"));
				comp.setDescripcion(rs.getString("descripcion"));
				comp.setEstatus(rs.getString("estatus"));
				comp.setIdTipoComponente(rs.getInt("id_tipo_componente"));
				comp.setIdComponentePadre(rs.getInt("id_componente_padre"));
				comp.setEtiqueta(rs.getString("etiqueta"));
				comp.setRutaImagen(rs.getString("ruta_imagen"));
				comp.setURL(rs.getString("url"));
			return comp;
			}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:obtenerPantallas");
		}
		return componentes;
	}
	public int guardarBoton(SegBotonDto datos) {
		int r = -1;
		try {
			//ConsultasGenerales consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			//int folio = consultasGenerales.obtenFolio("seg_componente");
			StringBuffer sql = new StringBuffer();
			sql.append("insert into SEG_FAC_BOTON values(\n");
			sql.append("'");
			sql.append(datos.getIdBoton());
			sql.append("', \n'");
			sql.append(datos.getDescripcion());
			sql.append("', '");
			sql.append(datos.getEstatus());
			sql.append("', '");
			sql.append(datos.getIdComponente());
			sql.append("', '");
			sql.append(datos.getIdModulo());
			sql.append("')");
			System.out.println(sql.toString());
			r = jdbcTemplate.update(sql.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:guardarBoton");
		}
		return r;
	}
	public int modificarBoton(SegBotonDto datos) {
		int r = -1;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("update SEG_FAC_BOTON set DESCRIPCION = ");
			sql.append("'");
			sql.append(datos.getDescripcion());
			sql.append("', ESTATUS = '");
			sql.append(datos.getEstatus());
			sql.append("', ID_COMPONENTE = '");
			sql.append(datos.getIdComponente());
			sql.append("' where ID_BOTON = '");
			sql.append(datos.getIdBoton());
			sql.append("'");
			r = jdbcTemplate.update(sql.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:modificarBoton");
		}
		return r;
	}
	public boolean estaAsignadoElBoton(String id) {
		boolean r = true;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT COUNT(*) FROM BOTON_USUARIO WHERE ID_BOTON = '");
			sql.append(id);
			sql.append("'");
			r = jdbcTemplate.queryForInt(sql.toString()) > 0;
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:estaAsignadoElBoton");
		}
		return r;
	}
	public int eliminarBoton(String id) {
		int r = -1;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("delete from SEG_FAC_BOTON where ID_BOTON = '");
			sql.append(id);
			sql.append("'");
			r = jdbcTemplate.update(sql.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:eliminarBoton");
		}
		return r;
	}
	public List<SegComponenteDto> obtenerBotonesSinAsignar(int idUsario, int idModulo){
		List<SegComponenteDto> botones = new ArrayList<SegComponenteDto>();
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT ID_BOTON, B.DESCRIPCION, ETIQUETA FROM SEG_FAC_BOTON B\n");
		sb.append("JOIN SEG_COMPONENTE C ON B.ID_COMPONENTE = C.ID_COMPONENTE\n");
		sb.append("WHERE ID_MODULO = ");
		sb.append(idModulo);
		sb.append(" AND ID_BOTON NOT IN\n");
		sb.append("(SELECT ID_BOTON FROM BOTON_USUARIO WHERE ID_USUARIO = ");
		sb.append(idUsario);
		sb.append(")");
		
		System.out.println(sb.toString());
		
		try {
			botones =  jdbcTemplate.query(sb.toString(), new RowMapper<SegComponenteDto>() {
				public SegComponenteDto mapRow(ResultSet rs, int idx) throws SQLException{
					SegComponenteDto boton = new SegComponenteDto();
					
					boton.setClaveComponente(rs.getString("ID_BOTON"));
					boton.setDescripcion(rs.getString("DESCRIPCION"));
					boton.setEtiqueta(rs.getString("ETIQUETA"));
					
				return boton;
			}});
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:obtenerBotonesSinAsignar");
		} 
		
		return botones;
	}
	
	public int asignarBoton(String idBoton, int idUsuario){
		int resultado = 0;
		StringBuffer sb = new StringBuffer();
		sb.append("INSERT INTO BOTON_USUARIO \n");
		sb.append("VALUES('"+idBoton+"', "+idUsuario+" ) \n");
		System.out.println(sb.toString());
		
		try{
			resultado = jdbcTemplate.update(sb.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:asignarBoton");
		}
		
		
		return resultado;
	}
	
	public int desAsignarBoton(String clave, String idUsuario){
		System.out.println("por aca");
		int resultado = 0;
		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM BOTON_USUARIO \n");
		sb.append("WHERE ID_BOTON = '"+clave+"' and id_usuario = " + idUsuario);
		
		System.out.println(sb.toString());
		
		try{
			resultado = jdbcTemplate.update(sb.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:desAsignarBoton");
		}
		
		
		return resultado;
	}
	
	
	public int desAsignarTodos(int idUsario, int idModulo){
		int resultado = 0;
		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM BOTON_USUARIO WHERE ID_BOTON IN(SELECT bu.ID_BOTON FROM BOTON_USUARIO bu JOIN \n");
		sb.append("SEG_FAC_BOTON bs ON bs.ID_BOTON = bu.ID_BOTON WHERE ID_MODULO = "+idModulo+" AND ID_USUARIO = "+idUsario+") AND ID_USUARIO = "+idUsario);
		
		System.out.println(sb.toString());
		
		try{
			resultado = jdbcTemplate.update(sb.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:desAsignarBoton");
		}
		
		
		return resultado;
	}
	
	
	public List<SegComponenteDto> obtenerBotonesAsignados(int idUsario, int idModulo){
		List<SegComponenteDto> botones = new ArrayList<SegComponenteDto>();
		StringBuffer sb = new StringBuffer();
		
		sb.append("SELECT b.ID_BOTON, DESCRIPCION FROM BOTON_USUARIO b JOIN SEG_FAC_BOTON s \n");
		sb.append("ON b.ID_BOTON = s.ID_BOTON WHERE ID_MODULO = "+idModulo+" AND ID_USUARIO = "+idUsario+" \n");
		
		System.out.println(sb.toString());
		
		try {
			botones =  jdbcTemplate.query(sb.toString(), new RowMapper<SegComponenteDto>() {
				public SegComponenteDto mapRow(ResultSet rs, int idx) throws SQLException{
					SegComponenteDto boton = new SegComponenteDto();
					boton.setClaveComponente(rs.getString("ID_BOTON"));
					boton.setDescripcion(rs.getString("DESCRIPCION"));
				return boton;
			}});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:obtenerBotonesAsignados");
		} 
		
		return botones;
	}
	public boolean existeBoton(String idBoton) {
		boolean r = true;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select count(*) from seg_fac_boton where ID_BOTON = '");
			sql.append(idBoton);
			sql.append("'");
			r = jdbcTemplate.queryForInt(sql.toString()) > 0;
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Seguridad, C:SegComponenteDao, M:existeBoton");
		}
		return r;
	}
	
	
	public String obtenerPermiso(String usuarioLogin, int com_mod, int fac_hab) {
		String resultado="";
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto> listListas = new ArrayList<LlenaComboGralDto>();
		try {
			sb.append(" select distinct  etiqueta ");
			sb.append(" from seg_usuario u, seg_usuario_perfil up, seg_per_fac_com pfc, seg_componente c ");
			sb.append(" where");
			sb.append(" up.id_usuario = u.id_usuario ");
			sb.append(" and pfc.id_perfil = up.id_perfil ");
			sb.append(" and c.id_componente = pfc.id_componente ");
			sb.append(" and u.estatus = 'A' ");
			sb.append(" and c.estatus = 'A' ");
			sb.append(" and c.id_tipo_componente = " + com_mod);
			sb.append(" and pfc.id_facultad = "+ fac_hab);
			sb.append(" and u.clave_usuario = '" + usuarioLogin + "'");
		
			
 
			System.out.println(sb.toString());
			System.out.println("query"+sb.toString());
			listListas = jdbcTemplate.query(sb.toString(), new RowMapper(){
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
					dtoCons.setDescripcion(rs.getString("etiqueta"));

					return dtoCons;
					
				}
			});
	
		} catch (Exception e) {
			resultado= "Error al validar el componente ";
		}
		String c1="Financiamiento";
		String c2="financiamiento";
		String c3="Financiamientos";
		String c4="financiamientos";
	for (LlenaComboGralDto l : listListas) {
		
		if(l.getDescripcion().equals(c1)||(l.getDescripcion().equals(c2)) ||(l.getDescripcion().equals(c3)) || (l.getDescripcion().equals(c4))){
			resultado=l.getDescripcion();
			System.out.println(resultado);
			break;
		}
		
		
	}
		return resultado;
	}

}
