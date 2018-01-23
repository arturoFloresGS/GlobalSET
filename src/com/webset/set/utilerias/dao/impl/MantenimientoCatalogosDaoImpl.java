package com.webset.set.utilerias.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dao.MantenimientoCatalogosDao;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utileriasmod.dto.MantenimientoCatalogosCatDto;
import com.webset.set.utileriasmod.dto.MantenimientoCatalogosDto;
public class MantenimientoCatalogosDaoImpl implements MantenimientoCatalogosDao{
	
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	StringBuffer sql = new StringBuffer();
	Funciones dao = new Funciones();
	
	public List<MantenimientoCatalogosDto> llenaComboCatalogos(){
		List <MantenimientoCatalogosDto> listaResultado = new ArrayList<MantenimientoCatalogosDto>();
		try{
			sql.append("Select nombre_catalogo,upper(desc_catalogo) as desc_catalogo,titulo_columnas,campos, botones from cat_catalogo ");
			sql.append("order by desc_catalogo");
			
			System.out.println(sql);
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MantenimientoCatalogosDto>(){
				public MantenimientoCatalogosDto mapRow(ResultSet rs, int idx) throws SQLException{
					MantenimientoCatalogosDto campos = new MantenimientoCatalogosDto();
					campos.setNombreCatalogo(rs.getString("nombre_catalogo"));
					campos.setDescCatalogo(rs.getString("desc_catalogo"));
					campos.setCol(rs.getString("campos"));
					campos.setTitulos(rs.getString("titulo_columnas"));
					campos.setBotones(rs.getString("botones"));
					return campos;
				}
			});
			
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoCatalogosDaoImpl, M: llenaComboCatalogos");
		}return listaResultado;
	}
	

	public List<LlenaComboGralDto> llenarComboBancos(int noEmpresa){
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		try{
		      sb.append("\n Select distinct cat_banco.id_banco as ID, ");
		      sb.append("\n cat_banco.desc_banco as DESCRIPCION ");
		      sb.append("\n From cat_banco left outer join cat_cta_banco on (cat_cta_banco.id_banco = cat_banco.id_banco)");
		      if(noEmpresa!=0)
		      sb.append("\n where cat_cta_banco.no_empresa = " + noEmpresa);
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
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoCatalogosDaoImpl, M: llenarComboBancos");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoCatalogosDaoImpl, M: llenarComboBancos");
		}return listDatos;
	}
	
	public List<LlenaComboGralDto> llenarComboChequeras(int noBanco,int noEmpresa){
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		try{
		      sb.append("\n Select id_chequera as ID, ");
		      sb.append("\n id_chequera as DESCRIPCION ");
		      sb.append("\n From cat_cta_banco");
		      if(noBanco != 0 && noEmpresa !=0){
		    	  sb.append("\n Where id_banco = " + noBanco);
		          sb.append("\n and no_empresa = " + noEmpresa);
		      }else{
		    	  if(noBanco != 0 && noEmpresa ==0)
		    		  sb.append("\n Where id_banco = " + noBanco);  
		    	  if(noBanco == 0 && noEmpresa !=0)
		    		  sb.append("\n Where no_empresa = " + noEmpresa);  
		      }
		      System.out.println(sb.toString());

		     listDatos= jdbcTemplate.query(sb.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoCatalogosDaoImpl, M: llenarComboChequeras");
		}
		return listDatos;
	}

	@Override
	public List<Map<String, Object>> llenaGrid(String nombreCatalogo,String noEmpresa,String noBanco,String idChequera, String[] vecColumnas, String orden){
		List<Map<String, Object>> listaResultado = new ArrayList<Map<String, Object>>();		
		try{
			sql = new StringBuffer();
			sql.append("select * from "+nombreCatalogo);
			StringBuffer condicion = new StringBuffer("");
			if(nombreCatalogo.equals("cat_ctas_contables_erp")){
				if(noEmpresa!=null && !noEmpresa.equals("") && !noEmpresa.equals("0")){
					condicion.append(" where no_empresa='"+noEmpresa+"'");
					if(noBanco!=null && !noBanco.equals("")){
						condicion.append(" and  id_banco='"+noBanco+"'");	
						if(idChequera!=null && !idChequera.equals("")){
							condicion.append(" and  id_chequera='"+idChequera+"'");	
						}
					}
				}
			}
			
			if (orden != null &&
					!orden.equals("")) {
				sql.append("\n order by ").append(orden);
			}
			System.out.println(sql.toString());
			listaResultado = jdbcTemplate.query(
					!condicion.toString().equals("")?
							sql.toString()+condicion.toString():
								sql.toString(),new MapaGrid(vecColumnas));
			System.out.println(listaResultado.size());
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoCatalogosDaoImpl, M: llenaGrid");
		}return listaResultado;
	}
	
	public String addRecord(String catalogo, String key, String tipo) {
		int id=0;
		try{
			System.out.println(key+" dao addRecord");
			sql.append("select coalesce(max("+key+")+1,1) from "+catalogo);
			
			id=jdbcTemplate.queryForInt(sql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoCatalogosDaoImpl, M: addRecord");
		}
		
		return ""+id;
	}
	
	public int deleteRecord(String catalogo, String key,String valor) {
		int res=0;
		try{
			sql.append("delete from ");
			sql.append(catalogo);
			sql.append(" where ");
			sql.append(key);
			sql.append("=");
			sql.append("'"+valor+"'");
			System.out.println(sql.toString());
			res=jdbcTemplate.update(sql.toString());
			System.out.println("res="+res);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoCatalogosDaoImpl, M: addRecord");
		}
		return res;
	}
	public int existe(String catalogo,String key,String valor){
		int exist=0;
		sql=new StringBuffer();
		try{
			sql.append("select coalesce(count(*),0) from "+catalogo+" where "+key+"='"+valor+"'");
			System.out.println(sql.toString());
			exist = jdbcTemplate.queryForInt(sql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoCatalogosDaoImpl, M: existe");						
		}
		return exist;
	}
	public int saveRecord(String catalogo,List<Map<String, String>> matRegistros,List<Map<String, String>> defCols) {
		int exist=0;
		int cambios=0;
		System.out.println(defCols+" defCols");
		System.out.println(matRegistros+" matRegistro dao");
		System.out.println(matRegistros.size()+" Tama�o");
		System.out.println(catalogo+" Catalogo");
		
		
		try{
			for(int j=0;j<matRegistros.size();j++){
				exist = existe(catalogo,defCols.get(0).get("name"),matRegistros.get(j).get(defCols.get(0).get("name")));
				sql=new StringBuffer();
				if(exist>0){
					sql.append("update "+catalogo+" set ");
				}else{
					if(catalogo.equals("cat_divisa")){
						sql.append("insert into "+catalogo+" (clasificacion, id_divisa, desc_divisa) values ('D', ");
					}else if(catalogo.equals("cat_catalogo")){
						sql.append("insert into "+catalogo+" (nombre_catalogo, id_empresa, desc_catalogo, titulo_columnas, campos, botones) values (");
					}else if(catalogo.equals("cat_colores_bit")){
						sql.append("insert into "+catalogo+" (id_color,tipo, color, descripcion_uno, descripcion_dos) values(");
					} else if(catalogo.equals("cat_grupo_flujo") ||
							catalogo.equals("cat_forma_pago") ||
							catalogo.equals("cat_concepto_trasp") ||
							catalogo.equals("cat_caja")){
						sql.append("insert into "+catalogo+"  values (");
					} else{
						String columnas = obtenerColumnas(defCols);
						sql.append("insert into "+catalogo+" "
								+ columnas
								+ " values (");
					}	
				}
				for(int i=0;i<matRegistros.get(j).size();i++){
					if(exist > 0){
						
						sql.append(defCols.get(i).get("name")+"='"+matRegistros.get(j).get(defCols.get(i).get("name"))+"' ");
						
						if(i!=matRegistros.get(j).size()-1)
							sql.append(",");
						else
							sql.append("where "+defCols.get(0).get("name")+"='"+matRegistros.get(j).get(defCols.get(0).get("name"))+"'");
					}else{
						sql.append("'"+matRegistros.get(j).get(defCols.get(i).get("name"))+"'");
						if(i != matRegistros.get(j).size()-1)
							sql.append(",");
						else
							if(catalogo.equals("cat_grupo_flujo")){
								sql.append(", 0)");
							}else if(catalogo.equals("cat_forma_pago")){
								sql.append(", null)");
							}else if(catalogo.equals("cat_concepto_trasp")){
								sql.append(", 0)");
							}else if(catalogo.equals("cat_caja")){
								sql.append(", 'N')");
							}else{
								sql.append(")");
							}
					}
				}
				
				System.out.println(sql);
				cambios = cambios + jdbcTemplate.update(sql.toString());
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoCatalogosDaoImpl, M: saveRecord");
		}
		
		return cambios;
	}
	private String obtenerColumnas(List<Map<String, String>> defCols) {
		StringBuffer columnas = new StringBuffer("(");
		
		for (int i = 0; i < defCols.size() - 1; i++) {
			Map<String, String> columna = defCols.get(i);
			columnas.append(columna.get("name") + ",");
		}
		
		columnas.append(
				defCols.get(defCols.size() - 1).get("name") + ")");
		
		return columnas.toString();
	}

	//****************
	public void setDataSource(DataSource dataSource)
	{
		try
		{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoCatalogosDaoImpl, M:setDataSource");
		}
	}
	
	@Override
	public List<Map<String, String>> reporteCatalogo(String catalogo, String campos, String[] keys, String orden) {
		List<Map<String, String>> listaResultado = new ArrayList<Map<String, String>>();
		
		StringBuffer sql = new StringBuffer();
				
		try
		{		
		
			sql.append("select ");
			sql.append(campos);
			sql.append(" from ");
			sql.append(catalogo);
			

			if (orden != null &&
					!orden.equals("")) {
				sql.append("\n order by ").append(orden);
			}
			
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql.toString(), new MapaReporte(keys));			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoCatalogosDaoImpl, M:reporteCatalogo");
		}
		return listaResultado;
	}
	

	class MapaReporte implements RowMapper<Map<String, String>>{
		private String[] keys;
		
		public MapaReporte(String[] keys) {
			this.keys = keys;
		}

		@Override
		public Map<String, String> mapRow(ResultSet rs, int arg1) throws SQLException {
			Map<String, String> campos = new HashMap<String, String>();
			
			for (String columna : keys) {
				campos.put(columna, rs.getString(columna));
			}
			
			return campos;
		}
		
	}

	class MapaGrid implements RowMapper<Map<String, Object>>{
		private String[] keys;
		
		public MapaGrid(String[] keys) {
			this.keys = keys;
		}

		@Override
		public Map<String, Object> mapRow(ResultSet rs, int arg1) throws SQLException {
			Map<String, Object> campos = new HashMap<String, Object>();
			
			for (int i = 0; i < keys.length; i+=2) {
				String columna = keys[i + 1];
				String tipo = keys[i].toUpperCase();
				
				switch (tipo) {
				case "S":
					campos.put(columna, rs.getString(columna));
					break;
					
				case "N":
					campos.put(columna, rs.getString(columna));
					break;
					
				case "F":
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					
					try {
						Date aux = format.parse(rs.getDate(columna).toString());
						campos.put(columna, dao.ponerFechaSola(aux));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					break;
				}
			}
			
			return campos;
		}
		
	}

	@Override
	public Map<String, String> getColumnas(String catalogo) {
		List<Map<String, String>> listaResultado = new ArrayList<Map<String, String>>();
		try
		{		
		
			sql.append("select campos, botones from cat_catalogo where nombre_catalogo= '"+catalogo+ "'\n");
			
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>()
			{
				public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException
				{
					Map<String, String> res = new HashMap<String, String>();
					res.put("campos", rs.getString("campos"));
					res.put("botones", rs.getString("botones"));
					return res;
				}				
				
			});			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoCatalogosDaoImpl, M:reporteCatalogo");
		}
		return !listaResultado.isEmpty() ? listaResultado.get(0) : null;
	}
}
