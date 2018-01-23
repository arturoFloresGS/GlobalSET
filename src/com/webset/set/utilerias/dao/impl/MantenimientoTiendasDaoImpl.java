package com.webset.set.utilerias.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dao.MantenimientoTiendasDao;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utileriasmod.dto.MantenimientoTiendasDto;
import com.webset.utils.tools.Utilerias;

/**
 * Clase de catalogo de tiendas.
 * @author YEC
 * @since  26/11/2015
 */

public class MantenimientoTiendasDaoImpl implements MantenimientoTiendasDao{
	private JdbcTemplate jdbcTemplate;
	private Bitacora bitacora = new Bitacora();
	
	
	public List<MantenimientoTiendasDto> llenaGridTiendas(String noAcredor){
		List<MantenimientoTiendasDto> listaResultado = new ArrayList<MantenimientoTiendasDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("SELECT t.ccid, t.desc_sucursal, t.no_acreedor, t.no_empresa ,e.nom_empresa ,t.razon_social ");
			sql.append( "\n FROM cat_tienda t , empresa e ");
			sql.append( "\n WHERE t.no_empresa=e.no_empresa");
			if(noAcredor!=null && !noAcredor.equals(""))
				sql.append( "\n AND t.no_acreedor = '"+Utilerias.validarCadenaSQL(noAcredor) +"' ");
			System.out.println("sql1"+sql.toString());
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MantenimientoTiendasDto>(){
				public MantenimientoTiendasDto mapRow(ResultSet rs, int idx) throws SQLException{
					MantenimientoTiendasDto campos = new MantenimientoTiendasDto();
					campos.setCcid(rs.getInt("CCID"));
					campos.setDescSucursal(rs.getString("DESC_SUCURSAL"));
					campos.setNoAcreedor(rs.getString("NO_ACREEDOR"));
					campos.setNoEmpresa(rs.getString("NO_EMPRESA"));
					campos.setNomEmpresa(rs.getString("NOM_EMPRESA"));
					campos.setRazonSocial(rs.getString("RAZON_SOCIAL"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoTiendasDaoImpl, M:llenaGridTiendas");
		}return listaResultado;
	}
	
	/*public List<LlenaComboGralDto>llenarComboBeneficiario(LlenaComboGralDto dto){
		String cond="";
		cond=dto.getCondicion();
		dto.setCampoDos("rTRIM(COALESCE(razon_social,'')) ||' '|| rTRIM(COALESCE(NOMBRE,'')) || ' ' || rTRIM(COALESCE(PATERNO,'')) " +
				"|| ' ' || rTRIM(COALESCE(MATERNO,''))");
		
		if(dto.isRegistroUnico())
		{
			dto.setCondicion("equivale_persona="+cond);
		}else{
			dto.setCondicion("id_tipo_persona='P'	"
					+"	AND no_empresa in(552,217)"
					+"	AND ((razon_social like '"+cond.toUpperCase()+"%'"     
					+"	or paterno like '"+cond.toUpperCase()+"%'" 
					+"	or materno like '"+cond.toUpperCase()+"%'"   
					+"	or nombre like '"+cond.toUpperCase()+"%' )" 
					+"	or (equivale_persona like '"+cond.toUpperCase()+"%'))");	
		}
		
		ConsultasGenerales consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.llenarComboGral(dto);
	}*/
	
	public List<LlenaComboGralDto> llenarComboBeneficiario(String nombre, String ePersona){
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		try{
		      sb.append("SELECT DISTINCT equivale_persona as ID , ");
		      sb.append("\n CASE WHEN (paterno is null or len(rtrim(ltrim(paterno)))=0 ) then razon_social");
		      sb.append("\n ELSE COALESCE( (nombre + ' ' + paterno + ' ' +materno), ' ') END as DESCRIPCION  ");
		      sb.append("\n FROM  persona");
		      sb.append("\n WHERE id_tipo_persona = 'P'");
		      sb.append("\n AND no_empresa in(552,217)");
		      if(!ePersona.equals(""))
		    	  sb.append("\n AND equivale_persona = "+ Utilerias.validarCadenaSQL(ePersona));
		      else {
		    	  if(!nombre.equals("")){
			    	  sb.append("\n AND (razon_social like  '"+Utilerias.validarCadenaSQL( nombre.toUpperCase()) + "%'");
			    	  sb.append("\n OR nombre like  '"+ Utilerias.validarCadenaSQL(nombre.toUpperCase()) + "%'");
				      sb.append("\n OR paterno like  '"+ Utilerias.validarCadenaSQL(nombre.toUpperCase()) + "%'");
				      sb.append("\n OR materno like  '"+ Utilerias.validarCadenaSQL(nombre.toUpperCase())+ "%')");
				      sb.append("\n ORDER BY DESCRIPCION,ID");
		    	  }
		      }
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
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoTiendasDaoImpl, M:llenarComboBeneficiarios");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoTiendasDaoImpl, M:llenarComboBeneficiarios");
		}return listDatos;
	}
	
	public String existe(MantenimientoTiendasDto dto){
		String resultado="Error";
		try{
			StringBuffer sql = new StringBuffer();
			int resp;
			sql.append("select count(*)  ");
			sql.append("\n FROM cat_tienda");
			sql.append("\n  WHERE ");
			sql.append("\n ccid= '" + Utilerias.validarCadenaSQL(dto.getCcid())+"' ");
			sql.append("\n AND no_acreedor= '" + Utilerias.validarCadenaSQL(dto.getNoAcreedor())+ "'");
			
			resp=jdbcTemplate.queryForInt(sql.toString());
			if(resp==1){
				resultado="La combinacion de CCID y el n�mero de acreedor ya existe.";
			}else{
				resultado="No existe";
			}
		}
		catch(CannotGetJdbcConnectionException e){
			resultado="Error al conectar con la base de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoTiendasDaoImpl, M:existe");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoTiendasDaoImpl, M:existe");
		}return resultado;
	}
	
	public String insertaMantenimientoTiendas(MantenimientoTiendasDto dto){
		String resultado="Error";
		StringBuffer sql = new StringBuffer();
		int resp=0;
		try{
			sql.delete(0, sql.length());
			sql.append("SELECT count(equivale_persona) ");
			sql.append("\n FROM persona ");
			sql.append("\n WHERE id_tipo_persona='P' ");
			sql.append("\n and equivale_persona= "+dto.getNoAcreedor());
			System.out.println(sql.toString());
			
			resp=jdbcTemplate.queryForInt(sql.toString());
			String mnsExiste=existe(dto);
			if(resp > 0){
				if(mnsExiste.equals("No existe")){
					sql.delete(0, sql.length());
					insertaBitacoraCatalogoTiendas(dto, 1);
					resp=0;
					sql.append("INSERT into cat_tienda values( ");
					sql.append("\n" + Utilerias.validarCadenaSQL(dto.getCcid()) + ",");
					sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getDescSucursal()) + "',");
					sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getRazonSocial()) + "',");
					sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getNoAcreedor()) + "',");
					sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getNoEmpresa()) + "',");
					sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getUsuarioAlta()) + "',");
					sql.append("\n null ,");
					sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getFecAlta()) + "',");
					sql.append("\n null )");
					System.out.println(sql.toString());
					
					resp=jdbcTemplate.update(sql.toString());
					if(resp==1){
						resultado="Exito";
					}
				}else{
					resultado=mnsExiste;
				}
			}else{
				resultado= "Error: El n�mero de acreedor no existe";
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoTiendasDaoImpl, M:insertaMantenimientoTiendas");
		}return resultado;
	}
	
	public String updateMantenimientoTiendas(MantenimientoTiendasDto dto){
		String resultado="Error";
		StringBuffer sql = new StringBuffer();
		int resp;
		try{
			sql.delete(0, sql.length());
			sql.append("SELECT count(equivale_persona) ");
			sql.append("\n FROM persona ");
			sql.append("\n WHERE id_tipo_persona='P' ");
			sql.append("\n and equivale_persona= "+Utilerias.validarCadenaSQL(dto.getNoAcreedor()));
			resp=jdbcTemplate.queryForInt(sql.toString());
			if(resp > 0){
				insertaBitacoraCatalogoTiendas(dto, 2);
				sql.delete(0, sql.length());
				sql.append("update cat_tienda ");
				sql.append("\n set desc_sucursal='" + Utilerias.validarCadenaSQL(dto.getDescSucursal()) + "',");
				sql.append("\n  no_acreedor='" + Utilerias.validarCadenaSQL(dto.getNoAcreedor()) + "',");
				sql.append("\n  fec_modif='" + Utilerias.validarCadenaSQL(dto.getFecMod()) + "',");
				sql.append("\n  usuario_modif=" + Utilerias.validarCadenaSQL(dto.getUsuarioMod()) + ",");
				sql.append("\n  razon_social='" + Utilerias.validarCadenaSQL(dto.getRazonSocial()) + "',");
				sql.append("\n  no_empresa=" + Utilerias.validarCadenaSQL(dto.getNoEmpresa()) );
				sql.append("\n  where ");
				sql.append(" ccid= " + Utilerias.validarCadenaSQL(dto.getCcid()) + "");
				resp=jdbcTemplate.update(sql.toString());
				if(resp==1){resultado="Exito";}
				
			}else{
				resultado= "Error: El n�mero de acreedor no existe";
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoTiendasDaoImpl, M:insertaMantenimientoTiendas");
		}
		return resultado;
	}
	
	public String deleteMantenimientoTiendas(MantenimientoTiendasDto dto){
		String resultado="Error";
		try{
			insertaBitacoraCatalogoTiendas(dto, 2);
			StringBuffer sql = new StringBuffer();
			int resp;
			sql.append("delete cat_tienda ");
			sql.append("\n  where ");
			sql.append(" ccid= " + Utilerias.validarCadenaSQL(dto.getCcid()) + "");
			resp=jdbcTemplate.update(sql.toString());
			if(resp==1){
				resultado="Exito";
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoTiendasDaoImpl, M:insertaMantenimientoTiendas");
		}return resultado;
	}
	
	
	public int insertaBitacoraCatalogoTiendas(MantenimientoTiendasDto dto,int op){
		int resultado=0;
		try{
			StringBuffer sql = new StringBuffer();
			
			if(op==1) {
				
				sql.append("insert into bitacora_cat_tienda values( ");
				sql.append("\n" + Utilerias.validarCadenaSQL(dto.getCcid()) + ",");
				sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getDescSucursal()) + "',");
				sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getRazonSocial()) + "',");
				sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getNoAcreedor()) + "',");
				sql.append("\n" + Utilerias.validarCadenaSQL(dto.getNoEmpresa()) + ",");
				sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getUsuarioAlta()) + "',");
				sql.append("\n null ,");
				sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getFecAlta()) + "',");
				sql.append("\n null ,");
				sql.append("\n null )");
			}
			
			if(op==2){
				sql.append("update bitacora_cat_tienda");
				sql.append("\n set desc_sucursal='" + Utilerias.validarCadenaSQL(dto.getDescSucursal()) + "',");
				sql.append("\n  no_acreedor='" + Utilerias.validarCadenaSQL(dto.getNoAcreedor()) + "',");
				sql.append("\n  no_empresa=" + Utilerias.validarCadenaSQL(dto.getNoEmpresa()) + ",");
				sql.append("\n  razon_social='" + Utilerias.validarCadenaSQL(dto.getRazonSocial()) + "',");
				sql.append("\n  fec_modif='" + Utilerias.validarCadenaSQL(dto.getFecMod()) + "',");
				sql.append("\n  usuario_modif=" + Utilerias.validarCadenaSQL(dto.getUsuarioMod()) + ",");
				sql.append("\n  motivo_modif='" + Utilerias.validarCadenaSQL(dto.getMotivoModif()) + "'");
				sql.append("\n  where ");
				sql.append(" ccid= " + Utilerias.validarCadenaSQL(dto.getCcid()) + "");
			}
			System.out.println(sql.toString());
			resultado=jdbcTemplate.update(sql.toString());	
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoTiendasDaoImpl, M:insertaBitacoraCatalogoTiendas");
		}return resultado;
	}
	
	//**************************************************************		
	public void setDataSource(DataSource dataSource)
	{
		try
		{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoTiendasDaoImpl, M:setDataSource");
		}
	}
	
}
