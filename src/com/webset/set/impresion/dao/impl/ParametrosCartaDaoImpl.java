package com.webset.set.impresion.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.impresion.dao.ParametrosCartaDao;
import com.webset.set.impresion.dto.ParametrosCartaDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.utils.tools.Utilerias;


public class ParametrosCartaDaoImpl implements ParametrosCartaDao{
	
	
	Funciones funciones = new Funciones();
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	String sql = "";
	
	public void setDataSource(DataSource dataSource){
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public List<ParametrosCartaDto> llenaBanco(){
		List<ParametrosCartaDto> listaResultado = new ArrayList<ParametrosCartaDto>();
		StringBuffer sql = new StringBuffer();
		try{
			
			//sql.append("select * from cat_banco order by id_banco");
			sql.append("SELECT distinct b.ID_BANCO, DESC_BANCO FROM CAT_BANCO b ");
			sql.append("JOIN CAT_CTA_BANCO cb ON b.id_banco= cb.id_banco");
			System.out.println(sql);
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<ParametrosCartaDto>(){
				public ParametrosCartaDto mapRow(ResultSet rs, int idx)throws SQLException{
					ParametrosCartaDto campos = new ParametrosCartaDto();
					campos.setIdBanco(rs.getInt("id_banco"));
					campos.setDescBanco(rs.getString("desc_banco"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: ParametrosCartaDaoImpl, M: llenaBanco");
		}return listaResultado;
	}
	
	@Override
	public int insertaCarta(ParametrosCartaDto objParametrosCartaDto) {
		System.out.println("Entra a dao Impl");
		int resultado= 0;
		try {
			StringBuffer sqlInsert = new StringBuffer();
			sqlInsert.append("insert into parametros_carta values(");
			sqlInsert.append(Utilerias.validarCadenaSQL(objParametrosCartaDto.getIdBanco()));
			sqlInsert.append(",'");
			sqlInsert.append(Utilerias.validarCadenaSQL(objParametrosCartaDto.getTipo()));
			sqlInsert.append("','");
			if (objParametrosCartaDto.getTipo().equals("ACERT") || 
					objParametrosCartaDto.getTipo().equals("ACHEQ") ) {
				sqlInsert.append(Utilerias.validarCadenaSQL(objParametrosCartaDto.getB1()));
				sqlInsert.append("','");
				sqlInsert.append(Utilerias.validarCadenaSQL(objParametrosCartaDto.getB2()));
				sqlInsert.append("','");
				sqlInsert.append(Utilerias.validarCadenaSQL(objParametrosCartaDto.getB3()));
				sqlInsert.append("','");
				sqlInsert.append(Utilerias.validarCadenaSQL(objParametrosCartaDto.getB4()));
				sqlInsert.append("','");
				sqlInsert.append(Utilerias.validarCadenaSQL(objParametrosCartaDto.getC1()));
				sqlInsert.append("','");
				sqlInsert.append(Utilerias.validarCadenaSQL(objParametrosCartaDto.getC2()));
				sqlInsert.append("','");
				sqlInsert.append(Utilerias.validarCadenaSQL(objParametrosCartaDto.getC3()));
				sqlInsert.append("','");
				sqlInsert.append(Utilerias.validarCadenaSQL(objParametrosCartaDto.getC4()));
				sqlInsert.append("','");
				sqlInsert.append(Utilerias.validarCadenaSQL(objParametrosCartaDto.getC5()));
				sqlInsert.append("','");
				sqlInsert.append(Utilerias.validarCadenaSQL(objParametrosCartaDto.getC6()));
				sqlInsert.append("',");
			} else {
				sqlInsert.append(Utilerias.validarCadenaSQL(objParametrosCartaDto.getC9()));
				sqlInsert.append("','");
				sqlInsert.append(Utilerias.validarCadenaSQL(objParametrosCartaDto.getC10()));
				sqlInsert.append("','");
				sqlInsert.append(Utilerias.validarCadenaSQL(objParametrosCartaDto.getC11()));
				sqlInsert.append("',");
				sqlInsert.append("null");
				sqlInsert.append(",'");
				sqlInsert.append(Utilerias.validarCadenaSQL(objParametrosCartaDto.getC7()));
				sqlInsert.append("','");
				sqlInsert.append(Utilerias.validarCadenaSQL(objParametrosCartaDto.getC8()));
				sqlInsert.append("',");
				sqlInsert.append("null");
				sqlInsert.append(",'");
				sqlInsert.append(Utilerias.validarCadenaSQL(objParametrosCartaDto.getC12()));
				sqlInsert.append("','");
				sqlInsert.append(Utilerias.validarCadenaSQL(objParametrosCartaDto.getC13()));
				sqlInsert.append("','");
				sqlInsert.append(Utilerias.validarCadenaSQL(objParametrosCartaDto.getC14()));
				sqlInsert.append("',");
			}
			
			sqlInsert.append("getdate()");
			sqlInsert.append(",");
			sqlInsert.append("getdate()");
			sqlInsert.append(",");
			GlobalSingleton globalSingleton = GlobalSingleton.getInstancia();
			sqlInsert.append(globalSingleton .getUsuarioLoginDto().getIdUsuario());
			sqlInsert.append(",'");
			sqlInsert.append(Utilerias.validarCadenaSQL(objParametrosCartaDto.getC15()));
			sqlInsert.append("')");
			
			System.out.println(sqlInsert);
			resultado= jdbcTemplate.update(sqlInsert.toString()); 
				
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Impresion, C:ParametrosCartaDaoImp, M:insertarCarta");
		}
		return resultado;
	}

	@Override
	public List<ParametrosCartaDto> llenaGrid() {
		

			List<ParametrosCartaDto> listaResultado = new ArrayList<ParametrosCartaDto>();
			StringBuffer sql = new StringBuffer();
			try{
				
				sql.append("select * from parametros_carta pc join seg_usuario sg \n");
				sql.append("on pc.usuario=sg.id_usuario join cat_banco cb on pc.id_banco=cb.id_banco");
				
				System.out.println("-----------------");
				System.out.println("imprime query");
				System.out.println("-----------------");
				System.out.println(sql);
				listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<ParametrosCartaDto>(){
					
					public ParametrosCartaDto mapRow(ResultSet rs, int idx)throws SQLException{
						ParametrosCartaDto campos = new ParametrosCartaDto();
						
						campos.setIdBanco(rs.getInt("id_banco"));
						campos.setDescBanco(rs.getString("desc_banco"));
						campos.setTipo(rs.getString("tipo_carta"));
						campos.setUsuario(rs.getInt("usuario"));
						campos.setNombre(rs.getString("nombre"));
						campos.setaPaterno(rs.getString("apellido_paterno"));
						campos.setaMaterno(rs.getString("apellido_materno"));
						campos.setFecha(rs.getString("fec_modif"));
				
						return campos;
					}
				});
			}
			catch(Exception e){
				e.printStackTrace();
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: ParametrosCartaDaoImpl, M: llenaGrid");
			}return listaResultado;
		}
	
	public List<ParametrosCartaDto> verificaRegistro(int idBanco, String tipo){
		List<ParametrosCartaDto> listaResultado = new ArrayList<ParametrosCartaDto>();
		StringBuffer sql = new StringBuffer();
		try{	
			System.out.println("daoimpl");
			sql.append("Select * from parametros_carta");
			sql.append(" where id_banco ="+ Utilerias.validarCadenaSQL(idBanco) +" and tipo_carta ='"+Utilerias.validarCadenaSQL(tipo)+"'");
			
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<ParametrosCartaDto>(){
				public ParametrosCartaDto mapRow(ResultSet rs, int idx)throws SQLException{
					ParametrosCartaDto campos = new ParametrosCartaDto();
					campos.setIdBanco(rs.getInt("id_banco"));
					campos.setTipo(rs.getString("tipo_carta"));
					return campos;
				}
			});		
		}		
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MapeoDaoImpl, M: verificaRegistro");
		}return listaResultado;
	}
	
	@Override
	public int eliminaCarta(int idBanco, String tipo) {
		int resultado = 0;
		StringBuffer sql = new StringBuffer();
		
		try{	
			sql.append("Delete from parametros_carta \n");
			sql.append("where id_banco ="+ Utilerias.validarCadenaSQL(idBanco) +"and tipo_carta ='"+Utilerias.validarCadenaSQL(tipo)+"' ");
			
			System.out.println(sql);
			resultado = jdbcTemplate.update(sql.toString());
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: ParametrosCartaDaoImpl, M: eliminaCarta");
		}return resultado;
	}

	@Override
	public int existeCarta(int idBanco, String tipo, ParametrosCartaDto objParametrosCartaDto) {
		int resultado = 0;
		StringBuffer sql= new StringBuffer();
		
		try{
			sql.append("Select count(*) from parametros_carta where id_banco ="+Utilerias.validarCadenaSQL( idBanco)+"\n");
			sql.append("and tipo_carta = '"+Utilerias.validarCadenaSQL( tipo)+"'");
			
			
			System.out.println(sql);
			resultado = jdbcTemplate.queryForInt(sql.toString());
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: ParametrosCartaDaoImpl, M: existeCarta");
		}return resultado;
	}

	@Override
	public List<ParametrosCartaDto> obtieneCarta(int idBanco, String tipo) {
		System.out.println("Entro a dao Obtiene Datos");
		List<ParametrosCartaDto> listaResultado = new ArrayList<ParametrosCartaDto>();
		sql = "";
		@SuppressWarnings("unused")
		SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
		
		try
		{
			System.out.println("Entra al obtiene datos dao");
		
			StringBuffer sql = new StringBuffer();
			
			
			
			sql.append("select * from parametros_carta pc join cat_banco cb on pc.id_banco=cb.id_banco where pc.id_banco = "+Utilerias.validarCadenaSQL(idBanco)+" "); 
			sql.append("and tipo_carta = '"+ Utilerias.validarCadenaSQL(tipo) +"'");
			
			
			
			System.out.println(sql);
		
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<ParametrosCartaDto>(){
				public ParametrosCartaDto mapRow(ResultSet rs, int idx)throws SQLException{
					ParametrosCartaDto campos = new ParametrosCartaDto();
					campos.setIdBanco(rs.getInt("id_banco"));
					campos.setDescBanco(rs.getString("desc_banco"));
					campos.setTipo(rs.getString("tipo_carta"));
					campos.setB1(rs.getString("textob1"));
					campos.setB2(rs.getString("textob2"));
					campos.setB3(rs.getString("textob3"));
					campos.setB4(rs.getString("textob4"));
					campos.setC1(rs.getString("textoc1"));
					campos.setC2(rs.getString("textoc2"));
					campos.setC3(rs.getString("textoc3"));
					campos.setC4(rs.getString("textoc4"));
					campos.setC5(rs.getString("textoc5"));
					campos.setC6(rs.getString("textoc6"));
					
					campos.setC9(rs.getString("textob1"));
					campos.setC10(rs.getString("textob2"));
					campos.setC11(rs.getString("textob3"));
					campos.setC7(rs.getString("textoc1"));
					campos.setC8(rs.getString("textoc2"));
					campos.setC12(rs.getString("textoc4"));
					campos.setC13(rs.getString("textoc5"));
					campos.setC14(rs.getString("textoc6"));
					campos.setC15(rs.getString("lugar_fecha"));
					System.out.println(rs.getString("lugar_fecha"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: ParametrosCartaDaoImpl, M: obtieneCarta");
			return null;
		}
	
		return listaResultado;
	}

	@Override
	public String actualizaCarta(ParametrosCartaDto objParametrosCartaDto, String idBanco, String tipo) {
		String resultado="";
		try {
			StringBuffer sqlUpdate = new StringBuffer();
	
			sqlUpdate.append("Update parametros_carta Set");
			System.out.println(objParametrosCartaDto.getTipo());
			if (objParametrosCartaDto.getTipo().equals("ACERT") || 
					objParametrosCartaDto.getTipo().equals("ACHEQ") ) {
				sqlUpdate.append("\n textob1='"+Utilerias.validarCadenaSQL(objParametrosCartaDto.getB1()));
				sqlUpdate.append("',");
				sqlUpdate.append("\n textob2='"+Utilerias.validarCadenaSQL(objParametrosCartaDto.getB2()));
				sqlUpdate.append("',");
				sqlUpdate.append("\n textob3='"+Utilerias.validarCadenaSQL(objParametrosCartaDto.getB3()));
				sqlUpdate.append("',");
				sqlUpdate.append("\n textob4='"+Utilerias.validarCadenaSQL(objParametrosCartaDto.getB4()));
				sqlUpdate.append("',");
				sqlUpdate.append("\n textoc1='"+Utilerias.validarCadenaSQL(objParametrosCartaDto.getC1()));
				sqlUpdate.append("',");
				sqlUpdate.append("\n textoc2='"+Utilerias.validarCadenaSQL(objParametrosCartaDto.getC2()));
				sqlUpdate.append("',");
				sqlUpdate.append("\n textoc3='"+Utilerias.validarCadenaSQL(objParametrosCartaDto.getC3()));
				sqlUpdate.append("',");
				sqlUpdate.append("\n textoc4='"+Utilerias.validarCadenaSQL(objParametrosCartaDto.getC4()));
				sqlUpdate.append("',");
				sqlUpdate.append("\n textoc5='"+Utilerias.validarCadenaSQL(objParametrosCartaDto.getC5()));
				sqlUpdate.append("',");
				sqlUpdate.append("\n textoc6='"+Utilerias.validarCadenaSQL(objParametrosCartaDto.getC6()));
				sqlUpdate.append("',");
			}else{
				sqlUpdate.append("\n textob1='"+Utilerias.validarCadenaSQL(objParametrosCartaDto.getC9()));
				sqlUpdate.append("',");
				sqlUpdate.append("\n textob2='"+Utilerias.validarCadenaSQL(objParametrosCartaDto.getC10()));
				sqlUpdate.append("',");
				sqlUpdate.append("\n textob3='"+Utilerias.validarCadenaSQL(objParametrosCartaDto.getC11()));
				sqlUpdate.append("',");
				//sqlUpdate.append("\n textob4='"+objParametrosCartaDto.getB4());
				sqlUpdate.append("\n textoc1='"+Utilerias.validarCadenaSQL(objParametrosCartaDto.getC7()));
				sqlUpdate.append("',");
				sqlUpdate.append("\n textoc2='"+Utilerias.validarCadenaSQL(objParametrosCartaDto.getC8()));
				sqlUpdate.append("',");
//				sqlUpdate.append("\n textoc3='"+objParametrosCartaDto.getC3());
//				sqlUpdate.append("',");
				sqlUpdate.append("\n textoc4='"+Utilerias.validarCadenaSQL(objParametrosCartaDto.getC12()));
				sqlUpdate.append("',");
				sqlUpdate.append("\n textoc5='"+Utilerias.validarCadenaSQL(objParametrosCartaDto.getC13()));
				sqlUpdate.append("',");
				sqlUpdate.append("\n textoc6='"+Utilerias.validarCadenaSQL(objParametrosCartaDto.getC14()));
				sqlUpdate.append("',");
				
			}
			sqlUpdate.append("\n fec_modif=getdate()");
			sqlUpdate.append(",");
			GlobalSingleton globalSingleton = GlobalSingleton.getInstancia();
			sqlUpdate.append("\n usuario="+globalSingleton .getUsuarioLoginDto().getIdUsuario());
			sqlUpdate.append(",");
			sqlUpdate.append("\n lugar_fecha='"+Utilerias.validarCadenaSQL(objParametrosCartaDto.getC15()));
			sqlUpdate.append("'\n where id_banco=" + Utilerias.validarCadenaSQL(idBanco)+ " and tipo_carta='"+Utilerias.validarCadenaSQL(tipo)+"'");
			System.out.println("cabecera");
			System.out.println(sqlUpdate);
			if(jdbcTemplate.update(sqlUpdate.toString())!=0){
		
				resultado = "Exito al modificar el mapeo";

			}else{
				resultado = "Error al insertar la cabecera";
			}
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Utilerias, C:MapeoDao, M:actualizaMapeo");
		}
		return resultado;
	}


	
}
