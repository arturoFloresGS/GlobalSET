package com.webset.set.barridosfondeos.dao.impl;
/**
 * @autor COINSIDE
 */

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.barridosfondeos.dao.BarridosFondeosDao;
import com.webset.set.barridosfondeos.dto.BusquedaFondeoDto;
import com.webset.set.barridosfondeos.dto.FilialDto;
import com.webset.set.barridosfondeos.dto.FondeoAutomaticoDto;
import com.webset.set.barridosfondeos.dto.ParametroDto;
import com.webset.set.barridosfondeos.dto.TmpTraspasoFondeoDto;
import com.webset.set.coinversion.dto.ArbolEmpresaDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.utils.tools.Utilerias;

public class BarridosFondeosDaoImpl implements BarridosFondeosDao{  
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora=new Bitacora();
	ConsultasGenerales consultasGenerales;
	Funciones funciones= new Funciones();
	GlobalSingleton globalSingleton;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public String consultarConfiguraSet(int indice){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
	}

	public List<LlenaComboEmpresasDto>obtenerEmpresas(int idUsuario, boolean bMantenimiento){
		List<LlenaComboEmpresasDto> lista = null;
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT no_empresa, nom_empresa ");
	    sql.append("FROM empresa ");
	    sql.append("WHERE b_concentradora = 'S' ");
		
	    if(idUsuario > 0){
	    	sql.append("AND no_empresa in");
	        
	        if(!bMantenimiento){
	        	sql.append("	(SELECT DISTINCT no_controladora ");
	            sql.append("	FROM empresa ");
	            sql.append("	WHERE no_empresa in ");
	        }
	            
	        sql.append("	( SELECT no_empresa ");
	        sql.append("	FROM usuario_empresa ");
	        sql.append("	WHERE no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + " ) ");
	        System.out.println("llena_combo"+sql.toString());
	        if(!bMantenimiento)
	            sql.append("	)");
	    }
	    
		lista = jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboEmpresasDto>(){
			public LlenaComboEmpresasDto mapRow(ResultSet rs, int idx) throws SQLException {
				LlenaComboEmpresasDto cons = new LlenaComboEmpresasDto();
				cons.setNoEmpresa(rs.getInt("no_empresa"));
				cons.setNomEmpresa(rs.getString("nom_empresa"));
				return cons;
			}});
		
		return lista;
	}
	
	@Override
	public List<FilialDto> obtenerEmpresasFiliales(int noEmpresa, int idUsuario) {
		List<FilialDto> lista = null;
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT distinct e.no_empresa, e.nom_empresa, e.no_cuenta_emp, ");
		sql.append("p.id_tipo_persona, c.no_cuenta, e.no_controladora ");
		sql.append("FROM empresa e ");
		sql.append("     LEFT JOIN cuenta c on(e.no_empresa = c.no_cuenta), persona p ");
		sql.append("WHERE e.no_empresa = p.no_empresa ");
		sql.append("and p.id_tipo_persona in ('I','E') ");
		sql.append("and (e.no_controladora is null or e.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresa) + ") ");
		sql.append("and e.no_empresa <> " + Utilerias.validarCadenaSQL(noEmpresa));
		sql.append("and e.no_empresa in ");
		sql.append("         ( SELECT no_empresa ");
		sql.append("           FROM usuario_empresa ");
		sql.append("           WHERE no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + " ) ");
		sql.append(" ORDER BY e.nom_empresa ");
	    System.out.println("query"+sql.toString());
		lista = jdbcTemplate.query(sql.toString(), new RowMapper<FilialDto>(){
			public FilialDto mapRow(ResultSet rs, int idx) throws SQLException {
				FilialDto cons = new FilialDto();
				cons.setNoEmpresa(rs.getInt("no_empresa"));
				cons.setNombreEmpresa(rs.getString("nom_empresa"));
				cons.setNoCuentaEmp(rs.getLong("no_cuenta_emp"));
				cons.setTipoPersona(rs.getString("id_tipo_persona"));
				cons.setNoCuenta(rs.getLong("no_cuenta"));
				cons.setNoControladora(rs.getInt("no_controladora"));
				return cons;
			}});
		return lista;
	}

	@Override
	public List<LlenaComboGralDto> obtenerTipoArbol(boolean bExistentes, String tipoOperacion) {
		List<LlenaComboGralDto> lista ;
		StringBuffer sbSql = new StringBuffer();
		
		if (bExistentes){
			sbSql.append("SELECT distinct tipo_arbol id, desc_arbol descripcion ");
			sbSql.append("FROM cat_arbol ");
		}else{
			sbSql.append("SELECT distinct e.no_empresa id, e.nom_empresa descripcion ");
	        sbSql.append("FROM empresa ");
	        sbSql.append("WHERE no_empresa not in ");
	        sbSql.append("    (select distinct empresa_raiz");
	        sbSql.append("    from arbol_empresa)");
		}
		System.out.println(sbSql.toString());
		lista = jdbcTemplate.query(sbSql.toString(), new RowMapper<LlenaComboGralDto>(){
			public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
				LlenaComboGralDto cons = new LlenaComboGralDto();
				cons.setId(rs.getInt("id"));
				cons.setDescripcion(rs.getString("descripcion"));
				
				return cons;
			}});
		return lista;
	}

	@Override
	public String obtenerIgualDif(int idArbol) {
		String valor = "";
		StringBuffer sbSql = new StringBuffer();
		
		sbSql.append("SELECT tipo_busqueda ");
		sbSql.append("FROM arbol_clase_docto ");
		sbSql.append("WHERE tipo_arbol = ?");
		System.out.println("query"+sbSql.toString());
		try {
			valor = (String) this.getJdbcTemplate().queryForObject(sbSql.toString(), new Object[] {idArbol}, String.class);
		}catch (Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BarridosFondeos, C:BarridosFondeosDao, M:obtenerIgualDif");
		}
		return valor;
	}

	public String consultarConfiguaraSet(int indice){
		consultasGenerales=new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
	}
	
	/**
	 * Este metodo es para hacer el enlace con el bean de conexion
	 * en el aplicationContext
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:CapturaSolicitudesPagoDao, M:setDataSource");
		}
	}	
	
	/*--- metodos config arboles ---*/
	/**
	 * FunSQLComboEmpRaiz
	 * @param bExistentes
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<LlenaComboGralDto> consultarArboles(boolean bExistentes){
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpresaRaiz = new ArrayList<LlenaComboGralDto>();

		try
		{
			if(bExistentes)
			{
				sbSql.append("\n Select distinct a.tipo_arbol as ID, a.desc_arbol as describ, aef.empresa_origen as empresa");
				sbSql.append("\n From cat_arbol a ");
				sbSql.append("\n left outer join arbol_empresa_fondeo aef ");
			    sbSql.append("\n on (aef.tipo_arbol = a.tipo_arbol) ");

			}
			else
			{
				sbSql.append("Select distinct e.no_empresa as ID, e.nom_empresa as describ ");
		        sbSql.append("\n From empresa e");
		        sbSql.append("\n WHERE e.no_empresa not in");
		        sbSql.append("\n     (select distinct empresa_raiz");
		        sbSql.append("\n     from arbol_empresa)");
			}
			System.out.println("query"+sbSql.toString());
			listEmpresaRaiz = jdbcTemplate.query(sbSql.toString(), new RowMapper()
			{
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						dtoCons.setId(rs.getInt("ID"));
						dtoCons.setDescripcion(rs.getString("describ"));
						dtoCons.setCampoUno(rs.getString("empresa"));
					return dtoCons;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos, C: BarridosFondeosDaoImpl, consultarArboles");
		}
		return listEmpresaRaiz;
	}


	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<LlenaComboGralDto> consultarArboles1(boolean bExistentes){
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpresaRaiz = new ArrayList<LlenaComboGralDto>();

		try
		{
			if(bExistentes)
			{
				
				
	
				 
				
				 
				
				sbSql.append("\n Select distinct a.tipo_arbol as ID, a.desc_arbol as describ, aef.empresa_origen as empresa");
				sbSql.append("\n From empresa e, arbol_empresa a  ");
				sbSql.append("\n  WHERE e.no_empresa = a.no_empresa ");
			    sbSql.append("\n and a.padre = 111 and a.hijo is not null  ");
			    sbSql.append("\n 	 order by   ");

			}
			else
			{
				sbSql.append("Select distinct e.no_empresa as ID, e.nom_empresa as describ ");
		        sbSql.append("\n From empresa e");
		        sbSql.append("\n WHERE e.no_empresa not in");
		        sbSql.append("\n     (select distinct empresa_raiz");
		        sbSql.append("\n     from arbol_empresa)");
			}
			System.out.println("query"+sbSql.toString());
			listEmpresaRaiz = jdbcTemplate.query(sbSql.toString(), new RowMapper()
			{
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						dtoCons.setId(rs.getInt("ID"));
						dtoCons.setDescripcion(rs.getString("describ"));
						dtoCons.setCampoUno(rs.getString("empresa"));
					return dtoCons;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos, C: BarridosFondeosDaoImpl, consultarArboles1");
		}
		return listEmpresaRaiz;
	}

	
	 

	 
	
	public List<LlenaComboGralDto> consultarArbolesInterempresas(boolean bExistentes){
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpresaRaiz = new ArrayList<LlenaComboGralDto>();

		try
		{
			if(bExistentes)
			{
				 


			/*	 & " SELECT e.no_empresa as ID, e.nom_empresa as describ"
				    sSql = sSql & " FROM empresa e, arbol_empresa a "
				    sSql = sSql & " WHERE e.no_empresa = a.no_empresa "
				    sSql = sSql & " and a.empresa_raiz=" & piNoEmpresaRaiz*/
				
				
				 	
				
				
				
				
				
				sbSql.append("\n select e.no_empresa as ID, e.nom_empresa as describ, e.arch_logo as empresa 	");
							sbSql.append("\n  from empresa e join arbol_empresa a on a.no_empresa=e.no_empresa  ");
							sbSql.append("\n  where a.b_padre='S' And e.b_concentradora='S'  ");
						    sbSql.append("\n order by 2");
						    
			}
			else
			{
				sbSql.append("Select distinct e.no_empresa as ID, e.nom_empresa as describ ");
		        sbSql.append("\n From empresa e");
		        sbSql.append("\n WHERE e.no_empresa not in");
		        sbSql.append("\n     (select distinct empresa_raiz");
		        sbSql.append("\n     from arbol_empresa)");
			}
			System.out.println("query"+sbSql.toString());
			listEmpresaRaiz = jdbcTemplate.query(sbSql.toString(), new RowMapper()
			{
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						dtoCons.setId(rs.getInt("ID"));
						dtoCons.setDescripcion(rs.getString("describ"));
						dtoCons.setCampoUno(rs.getString("empresa"));
					return dtoCons;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos, C: BarridosFondeosDaoImpl, consultarArbolesInterEmpresas");
		}
		return listEmpresaRaiz;
	}

	
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarArbolesHijos(boolean bExistentes, int idRaiz){
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpresaRaiz = new ArrayList<LlenaComboGralDto>();

		try
		{
			if(bExistentes)
			{
				
				                 
				sbSql.append("\n Select distinct e.no_empresa as ID ,e.nom_empresa as describ, e.arch_logo as empresa");
							sbSql.append("\n From empresa e, arbol_empresa a ");
							sbSql.append("\n  WHERE e.no_empresa = a.no_empresa");
							sbSql.append("\n and a.padre = "+idRaiz+" and a.hijo is not null ");		 
						    sbSql.append("\n order by 2");
						   
						    		
						    
			}
			else
			{
				sbSql.append("Select distinct e.no_empresa as ID, e.nom_empresa as describ ");
		        sbSql.append("\n From empresa e");
		        sbSql.append("\n WHERE e.no_empresa not in");
		        sbSql.append("\n     (select distinct empresa_raiz");
		        sbSql.append("\n     from arbol_empresa)");
			}
			System.out.println("query"+sbSql.toString());
			listEmpresaRaiz = jdbcTemplate.query(sbSql.toString(), new RowMapper()
			{
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						dtoCons.setId(rs.getInt("ID"));
						dtoCons.setDescripcion(rs.getString("describ"));
						dtoCons.setCampoUno(rs.getString("empresa"));
					return dtoCons;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos, C: BarridosFondeosDaoImpl, consultarArbolesHijos");
		}
		return listEmpresaRaiz;
	}

	

	//llenarCmbEmpresaRaizInterempresas aver si llega a este metodo
	
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarArbolesHijosInterempresas(boolean bExistentes, int idRaiz){
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpresaRaiz = new ArrayList<LlenaComboGralDto>();

		try
		{
			if(bExistentes)
			{ 
				

				sbSql.append("\n select e.no_empresa as ID, e.nom_empresa as describ, e.arch_logo as empresa 	");
							sbSql.append("\n from empresa e join arbol_empresa a on a.no_empresa=e.no_empresa ");
							sbSql.append("\n where a.b_padre='N'  ");	
							sbSql.append("\n and a.empresa_raiz="+idRaiz+"  ");
							sbSql.append("\n or a.padre="+idRaiz+"  ");
						    sbSql.append("\n order by 1");
			

				
				/*sbSql.append("\n select e.no_empresa as ID, e.nom_empresa as describ, e.arch_logo as empresa 	");
							sbSql.append("\n from empresa e join arbol_empresa a on a.no_empresa=e.no_empresa ");
							sbSql.append("\n where a.b_padre='N'  ");
							sbSql.append("\n and e.b_concentradora='N'  ");	
							sbSql.append("\n AND e.no_empresa>="+idRaiz+" OR  e.no_empresa>="+idRaiz+"");
						    sbSql.append("\n order by 1");*/
						    
			}
			else
			{
				sbSql.append("Select distinct e.no_empresa as ID, e.nom_empresa as describ ");
		        sbSql.append("\n From empresa e");
		        sbSql.append("\n WHERE e.no_empresa not in");
		        sbSql.append("\n     (select distinct empresa_raiz");
		        sbSql.append("\n     from arbol_empresa)");
			}
			System.out.println("query"+sbSql.toString());
			listEmpresaRaiz = jdbcTemplate.query(sbSql.toString(), new RowMapper()
			{
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						dtoCons.setId(rs.getInt("ID"));
						dtoCons.setDescripcion(rs.getString("describ"));
						dtoCons.setCampoUno(rs.getString("empresa"));
					return dtoCons;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos, C: BarridosFondeosDaoImpl, consultarArbolesHijosInterempresas");
		}
		return listEmpresaRaiz;
	}

	

	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<LlenaComboGralDto> consultarEmpresasRaiz(boolean bExistentes, int idArbol){
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpresaRaiz = new ArrayList<LlenaComboGralDto>();
		try
		{
			if(bExistentes)
			{
				sbSql.append("\n Select distinct e.no_empresa as ID, e.nom_empresa as describ ");
		         sbSql.append("\n From empresa e, arbol_empresa_fondeo aef, cat_arbol ca "); 
		         sbSql.append("\n WHERE e.no_empresa = aef.empresa_origen ");
		    //     sbSql.append("\n AND  ");
		         sbSql.append("\n AND aef.tipo_arbol = " + idArbol);
		         sbSql.append("\n AND orden = 0 ");
				/*
				sbSql.append("Select distinct e.no_empresa as ID, e.nom_empresa as describ ");
		        sbSql.append("\n From empresa e, arbol_empresa a ");
		        sbSql.append("\n WHERE e.no_empresa = a.empresa_raiz "); */
			}
			else
			{
				sbSql.append("Select distinct e.no_empresa as ID, e.nom_empresa as describ ");
		        sbSql.append("\n From empresa e");
		        sbSql.append("\n WHERE e.no_empresa not in");
		        sbSql.append("\n     (select distinct tipo_arbol ");
		        sbSql.append("\n     from arbol_empresa_fondeo)");
		        sbSql.append("\n AND e.no_empresa in (select no_empresa from cuenta)");
			}
System.out.println("Query"+sbSql.toString());
			listEmpresaRaiz = jdbcTemplate.query(sbSql.toString(), new RowMapper()
			{
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						dtoCons.setId(rs.getInt("ID"));
						dtoCons.setDescripcion(rs.getString("describ"));
					return dtoCons;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasRaiz");
		}
		return listEmpresaRaiz;
	}
	

	
	/*Medoto isrra query para llenar el combo*/
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<LlenaComboGralDto> consultarEmpresasRaizInterempresas(boolean bExistentes){
		
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpresaRaiz = new ArrayList<LlenaComboGralDto>();
		try
		{
			if(bExistentes)	{
				
				
				sbSql.append("Select distinct e.no_empresa as ID, e.nom_empresa as describ ");
		        sbSql.append("\n From empresa e, arbol_empresa a ");
		        sbSql.append("\n WHERE e.no_empresa = a.empresa_raiz ");
			  	 
/*
				sbSql.append("\n select e.no_empresa as ID, e.nom_empresa as describ ");
				sbSql.append("\n from empresa e join arbol_empresa a on a.no_empresa=e.no_empresa "); 
		        sbSql.append("\n where e.no_empresa not in ");
		        sbSql.append("\n (select distinct empresa_raiz ");
		        sbSql.append("\n   from arbol_empresa) ");
		        sbSql.append("\n   order by 1 ");*/
		      
				 
				/*
				sbSql.append("\n select e.no_empresa as ID, e.nom_empresa as describ ");
				sbSql.append("\n from empresa e join arbol_empresa a on a.no_empresa=e.no_empresa "); 
		        sbSql.append("\n where e.no_empresa not in ");
		        sbSql.append("\n (select distinct empresa_raiz ");
		        sbSql.append("\n   from arbol_empresa) ");
		        sbSql.append("\n   order by 1 ");*/
		        
				/*
				sbSql.append("Select distinct e.no_empresa as ID, e.nom_empresa as describ ");
		        sbSql.append("\n From empresa e, arbol_empresa a ");
		        sbSql.append("\n WHERE e.no_empresa = a.empresa_raiz "); */
			}
			else
			{
				sbSql.append(" 	 select p.no_empresa as ID, p.razon_social as describ from persona p join empresa e on p.no_persona=e.no_empresa  where id_tipo_persona='E' and e.b_concentradora='S' order by 1 ");
				   
			}
System.out.println("Query"+sbSql.toString());
			listEmpresaRaiz = jdbcTemplate.query(sbSql.toString(), new RowMapper()
			{
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						dtoCons.setId(rs.getInt("ID"));
						dtoCons.setDescripcion(rs.getString("describ"));
					return dtoCons;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasRaizInterempresas");
		}
		return listEmpresaRaiz;
	}
	
	
	
	
	
	
	/**
	 * FunSQLBisnieto
	 * @param iEmpresaRaiz
	 * @param iEmpNieto
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<LlenaComboGralDto> consultarEmpresasBisnietos(int iEmpresaRaiz, int iEmpNieto)
	{
		String tipo_arbol;
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpBisnieto = new ArrayList<LlenaComboGralDto>();
		try
		{
			sbSql.append("select distinct tipo_Arbol from arbol_empresa_fondeo where empresa_origen = " + iEmpresaRaiz);
			tipo_arbol = this.getJdbcTemplate().queryForObject(sbSql.toString(), String.class);
			
			sbSql.delete(0, sbSql.length());
			
			sbSql.append("\n SELECT DISTINCT aef.empresa_destino, convert(char,aef.empresa_destino) + ':' +  ");
			sbSql.append("\n  e.nom_empresa + ':' + convert(char,COALESCE(convert(integer,aef.valor),0.00)) ");
			sbSql.append("\n  AS nom_empresa ");
			sbSql.append("\n  FROM arbol_empresa_fondeo aef,empresa e  ");
			sbSql.append("\n  WHERE  aef.empresa_destino = E.no_empresa  ");
			sbSql.append("\n  AND aef.tipo_arbol = " + tipo_arbol );
			sbSql.append("\n  AND aef.orden = 0  ");
			sbSql.append("\n  AND aef.empresa_origen = " + iEmpNieto );
			
			/*
			sbSql.append("SELECT DISTINCT A.bisnieto, convert(char,A.bisnieto) + ':' +");
            sbSql.append("\n E.nom_empresa + ':' + convert(char,COALESCE(A.monto_maximo,0.00))");
            sbSql.append("\n AS nom_empresa");
            sbSql.append("\n FROM arbol_empresa A,empresa E");
            sbSql.append("\n WHERE  A.bisnieto = E.no_empresa");
            sbSql.append("\n AND A.nieto = " + iEmpNieto);
            sbSql.append("\n AND A.bisnieto IS NOT NULL");
            sbSql.append("\n AND A.empresa_raiz = " + iEmpresaRaiz);
            sbSql.append("\n AND A.tataranieto IS NULL"); */
            System.out.println("Query Bisnieto"+sbSql.toString());
            listEmpBisnieto = jdbcTemplate.query(sbSql.toString(), new RowMapper()
	            {
            		public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
            		{
            			LlenaComboGralDto dtoCons = new LlenaComboGralDto();
            				dtoCons.setId(rs.getInt("empresa_destino"));
            				dtoCons.setDescripcion(rs.getString("nom_empresa"));
            			return dtoCons;
            		}
	            }
            );
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasBisnietos");
		}
		return listEmpBisnieto;
	}

	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<LlenaComboGralDto> consultarEmpresasBisnietosIn(int iEmpresaRaiz, int iEmpNieto)
	{
		String tipo_arbol;
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpBisnieto = new ArrayList<LlenaComboGralDto>();
		try
		{
			/*sbSql.append("select distinct tipo_Arbol from arbol_empresa_fondeo where empresa_origen = " + iEmpresaRaiz);
			tipo_arbol = this.getJdbcTemplate().queryForObject(sbSql.toString(), String.class);*/
			
			sbSql.delete(0, sbSql.length());
			
		
			sbSql.append("SELECT DISTINCT A.bisnieto, convert(char,A.bisnieto) + ':' +");
            sbSql.append("\n E.nom_empresa + ':' + convert(char,COALESCE(A.monto_maximo,0.00))");
            sbSql.append("\n AS nom_empresa");
            sbSql.append("\n FROM arbol_empresa A,empresa E");
            sbSql.append("\n WHERE  A.bisnieto = E.no_empresa");
            sbSql.append("\n AND A.nieto = " + iEmpNieto);
            sbSql.append("\n AND A.bisnieto IS NOT NULL");
            sbSql.append("\n AND A.empresa_raiz = " + iEmpresaRaiz);
            sbSql.append("\n AND A.tataranieto IS NULL"); 
            System.out.println("Query Bisnieto"+sbSql.toString());
            listEmpBisnieto = jdbcTemplate.query(sbSql.toString(), new RowMapper()
	            {
            		public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
            		{
            			LlenaComboGralDto dtoCons = new LlenaComboGralDto();
            				dtoCons.setId(rs.getInt("bisnieto"));
            				dtoCons.setDescripcion(rs.getString("nom_empresa"));
            			return dtoCons;
            		}
	            }
            );
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasBisnietosIn");
		}
		return listEmpBisnieto;
	}


	
	
	
	
	
	
	
	
	/**
	 * FunSQLTataranieto
	 * @param iEmpresaRaiz
	 * @param iEmpBisnieto
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<LlenaComboGralDto> consultarEmpresasTataranietos(int iEmpresaRaiz, int iEmpBisnieto)
	{
		String tipo_arbol;
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpTataranieto = new ArrayList<LlenaComboGralDto>();
		try
		{
			sbSql.append("select distinct tipo_Arbol from arbol_empresa_fondeo where empresa_origen = " + iEmpresaRaiz);
			tipo_arbol = this.getJdbcTemplate().queryForObject(sbSql.toString(), String.class);
			
			sbSql.delete(0, sbSql.length());
			
			sbSql.append("\n  SELECT DISTINCT aef.empresa_destino, convert(char,aef.empresa_destino) + ':' + ");
			sbSql.append("\n   e.nom_empresa + ':' + convert(char,COALESCE(convert(integer,aef.valor),0.00)) ");
			sbSql.append("\n   AS nom_empresa ");
			sbSql.append("\n   FROM arbol_empresa_fondeo aef,empresa e ");
			sbSql.append("\n   WHERE  aef.empresa_destino = E.no_empresa ");
			sbSql.append("\n   AND aef.tipo_arbol = " + tipo_arbol );
			sbSql.append("\n   AND aef.orden = 0 ");
			sbSql.append("\n   AND aef.empresa_origen = " + iEmpBisnieto );
			
			/*
			sbSql.append("SELECT DISTINCT A.tataranieto, convert(char,A.tataranieto) + ':' +");
            sbSql.append("\n E.nom_empresa + ':' + convert(char,COALESCE(A.monto_maximo,0.00))");
			sbSql.append("\n AS nom_empresa");
            sbSql.append("\n FROM arbol_empresa A,empresa E");
            sbSql.append("\n WHERE  A.tataranieto = E.no_empresa");
            sbSql.append("\n AND A.bisnieto = " + iEmpBisnieto);
            sbSql.append("\n AND A.tataranieto IS NOT NULL");
            sbSql.append("\n AND A.empresa_raiz = " + iEmpresaRaiz);
            sbSql.append("\n AND A.chosno IS NULL"); */
            System.out.println("Query Tataranieto "+sbSql.toString());
            listEmpTataranieto = jdbcTemplate.query(sbSql.toString(), new RowMapper()
	            {
            		public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
            		{
            			LlenaComboGralDto dtoCons = new LlenaComboGralDto();
            				dtoCons.setId(rs.getInt("empresa_destino"));
            				dtoCons.setDescripcion(rs.getString("nom_empresa"));
            			return dtoCons;
            		}
	            }
            );
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasTataranietos");
		}
		return listEmpTataranieto;
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<LlenaComboGralDto> consultarEmpresasTataranietosIn(int iEmpresaRaiz, int iEmpBisnieto)
	{
		String tipo_arbol;
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpTataranieto = new ArrayList<LlenaComboGralDto>();
		try
		{
			/*sbSql.append("select distinct tipo_Arbol from arbol_empresa_fondeo where empresa_origen = " + iEmpresaRaiz);
			tipo_arbol = this.getJdbcTemplate().queryForObject(sbSql.toString(), String.class);*/
			
			sbSql.delete(0, sbSql.length());
			
			
			
			
			sbSql.append("SELECT DISTINCT A.tataranieto, convert(char,A.tataranieto) + ':' +");
            sbSql.append("\n E.nom_empresa + ':' + convert(char,COALESCE(A.monto_maximo,0.00))");
			sbSql.append("\n AS nom_empresa");
            sbSql.append("\n FROM arbol_empresa A,empresa E");
            sbSql.append("\n WHERE  A.tataranieto = E.no_empresa");
            sbSql.append("\n AND A.bisnieto = " + iEmpBisnieto);
            sbSql.append("\n AND A.tataranieto IS NOT NULL");
            sbSql.append("\n AND A.empresa_raiz = " + iEmpresaRaiz);
            sbSql.append("\n AND A.chosno IS NULL"); 
            System.out.println("Query Tataranieto "+sbSql.toString());
            listEmpTataranieto = jdbcTemplate.query(sbSql.toString(), new RowMapper()
	            {
            		public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
            		{
            			LlenaComboGralDto dtoCons = new LlenaComboGralDto();
            				dtoCons.setId(rs.getInt("tataranieto"));
            				dtoCons.setDescripcion(rs.getString("nom_empresa"));
            			return dtoCons;
            		}
	            }
            );
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasTataranietosIn");
		}
		return listEmpTataranieto;
	}

	
	
	
	/**
	 * FunSQLChosno
	 * @param iEmpresaRaiz
	 * @param iEmpTataranieto
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<LlenaComboGralDto> consultarEmpresasChosnoUno(int iEmpresaRaiz, int iEmpTataranieto)
	{
		String tipo_arbol;
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpChosnoUno = new ArrayList<LlenaComboGralDto>();
		try
		{
			sbSql.append("select distinct tipo_Arbol from arbol_empresa_fondeo where empresa_origen = " + iEmpresaRaiz);
			tipo_arbol = this.getJdbcTemplate().queryForObject(sbSql.toString(), String.class);
			
			sbSql.delete(0, sbSql.length());
			
			sbSql.append("\n SELECT DISTINCT aef.empresa_destino, convert(char,aef.empresa_destino) + ':' + ");
			sbSql.append("\n  e.nom_empresa + ':' + convert(char,COALESCE(convert(integer,aef.valor),0.00)) ");
			sbSql.append("\n  AS nom_empresa ");
			sbSql.append("\n  FROM arbol_empresa_fondeo aef,empresa e ");
			sbSql.append("\n  WHERE  aef.empresa_destino = E.no_empresa ");
			sbSql.append("\n  AND aef.tipo_arbol = " + tipo_arbol );
			sbSql.append("\n  AND aef.orden = 0 ");
			sbSql.append("\n  AND aef.empresa_origen = " + iEmpTataranieto);
			/*
			sbSql.append("SELECT DISTINCT A.chosno, convert(char,A.chosno) + ':' +");
            sbSql.append("\n E.nom_empresa + ':' + convert(char,COALESCE(A.monto_maximo,0.00))");
			sbSql.append("\n AS nom_empresa ");
            sbSql.append("\n FROM arbol_empresa A,empresa E");
            sbSql.append("\n WHERE  A.chosno = E.no_empresa");
            sbSql.append("\n AND A.tataranieto = " + iEmpTataranieto);
            sbSql.append("\n AND A.chosno IS NOT NULL");
            sbSql.append("\n AND A.empresa_raiz = " + iEmpresaRaiz);
            sbSql.append("\n AND A.chosno2 IS NULL");*/
System.out.println("Query Chosno_1"+sbSql.toString());
            listEmpChosnoUno = jdbcTemplate.query(sbSql.toString(), new RowMapper()
	            {
            		public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
            		{
            			LlenaComboGralDto dtoCons = new LlenaComboGralDto();
            				dtoCons.setId(rs.getInt("empresa_destino"));
            				dtoCons.setDescripcion(rs.getString("nom_empresa"));
            			return dtoCons;
            		}
	            }
            );
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasChosnoUno");
		}
		return listEmpChosnoUno;
	}

	
	
	
	
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<LlenaComboGralDto> consultarEmpresasChosnoUnoIn(int iEmpresaRaiz, int iEmpTataranieto)
	{
		String tipo_arbol;
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpChosnoUno = new ArrayList<LlenaComboGralDto>();
		try
		{
			/*sbSql.append("select distinct tipo_Arbol from arbol_empresa_fondeo where empresa_origen = " + iEmpresaRaiz);
			tipo_arbol = this.getJdbcTemplate().queryForObject(sbSql.toString(), String.class);*/
			
			sbSql.delete(0, sbSql.length());
			
			
			sbSql.append("SELECT DISTINCT A.chosno, convert(char,A.chosno) + ':' +");
            sbSql.append("\n E.nom_empresa + ':' + convert(char,COALESCE(A.monto_maximo,0.00))");
			sbSql.append("\n AS nom_empresa ");
            sbSql.append("\n FROM arbol_empresa A,empresa E");
            sbSql.append("\n WHERE  A.chosno = E.no_empresa");
            sbSql.append("\n AND A.tataranieto = " + iEmpTataranieto);
            sbSql.append("\n AND A.chosno IS NOT NULL");
            sbSql.append("\n AND A.empresa_raiz = " + iEmpresaRaiz);
            sbSql.append("\n AND A.chosno2 IS NULL");
            
System.out.println("Query Chosno_1"+sbSql.toString());
            listEmpChosnoUno = jdbcTemplate.query(sbSql.toString(), new RowMapper()
	            {
            		public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
            		{
            			LlenaComboGralDto dtoCons = new LlenaComboGralDto();
            				dtoCons.setId(rs.getInt("chosno"));
            				dtoCons.setDescripcion(rs.getString("nom_empresa"));
            			return dtoCons;
            		}
	            }
            );
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasChosnoUnoIn");
		}
		return listEmpChosnoUno;
	}


	
	
	
	
	
	
	
	/**
	 * FunSQLChosno2
	 * @param iEmpresaRaiz
	 * @param iEmpChosno1
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<LlenaComboGralDto> consultarEmpresasChosnoDos(int iEmpresaRaiz, int iEmpChosno1)
	{
		String tipo_arbol;
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpChosnoDos = new ArrayList<LlenaComboGralDto>();
		try
		{
			sbSql.append("select distinct tipo_Arbol from arbol_empresa_fondeo where empresa_origen = " + iEmpresaRaiz);
			tipo_arbol = this.getJdbcTemplate().queryForObject(sbSql.toString(), String.class);
			
			sbSql.delete(0, sbSql.length());
			
			sbSql.append("\n   SELECT DISTINCT aef.empresa_destino, convert(char,aef.empresa_destino) + ':' + ");
			sbSql.append("\n   e.nom_empresa + ':' + convert(char,COALESCE(convert(integer,aef.valor),0.00)) ");
			sbSql.append("\n   AS nom_empresa ");
			sbSql.append("\n   FROM arbol_empresa_fondeo aef,empresa e ");
			sbSql.append("\n    WHERE  aef.empresa_destino = E.no_empresa ");
			sbSql.append("\n   AND aef.tipo_arbol = " + tipo_arbol );
			sbSql.append("\n   AND aef.orden = 0 ");
			sbSql.append("\n   AND aef.empresa_origen = " + iEmpChosno1);
			 /*
			sbSql.append("SELECT DISTINCT A.chosno2, convert(char,A.chosno2) + ':' +");
            sbSql.append("\n E.nom_empresa + ':' + convert(char,COALESCE(A.monto_maximo,0.00))");
			sbSql.append("\n AS nom_empresa");
            sbSql.append("\n FROM arbol_empresa A,empresa E");
            sbSql.append("\n WHERE  A.chosno2 = E.no_empresa ");
            sbSql.append("\n AND A.chosno = " + iEmpChosno1);
            sbSql.append("\n AND A.chosno2 IS NOT NULL");
            sbSql.append("\n AND A.empresa_raiz = " + iEmpresaRaiz);
            sbSql.append("\n AND A.chosno3 IS NULL"); */
            System.out.println("Query Chsono_2"+sbSql.toString());
            listEmpChosnoDos = jdbcTemplate.query(sbSql.toString(), new RowMapper()
	            {
            		public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
            		{
            			LlenaComboGralDto dtoCons = new LlenaComboGralDto();
            				dtoCons.setId(rs.getInt("empresa_destino"));
            				dtoCons.setDescripcion(rs.getString("nom_empresa"));
            			return dtoCons;
            		}
	            }
            );
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasChosnoDos");
		}
		return listEmpChosnoDos;
	}


	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<LlenaComboGralDto> consultarEmpresasChosnoDosIn(int iEmpresaRaiz, int iEmpChosno1)
	{
		String tipo_arbol;
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpChosnoDos = new ArrayList<LlenaComboGralDto>();
		try
		{
			/*sbSql.append("select distinct tipo_Arbol from arbol_empresa_fondeo where empresa_origen = " + iEmpresaRaiz);
			tipo_arbol = this.getJdbcTemplate().queryForObject(sbSql.toString(), String.class);*/
			
			sbSql.delete(0, sbSql.length());
			
			
			sbSql.append("SELECT DISTINCT A.chosno2, convert(char,A.chosno2) + ':' +");
            sbSql.append("\n E.nom_empresa + ':' + convert(char,COALESCE(A.monto_maximo,0.00))");
			sbSql.append("\n AS nom_empresa");
            sbSql.append("\n FROM arbol_empresa A,empresa E");
            sbSql.append("\n WHERE  A.chosno2 = E.no_empresa ");
            sbSql.append("\n AND A.chosno = " + iEmpChosno1);
            sbSql.append("\n AND A.chosno2 IS NOT NULL");
            sbSql.append("\n AND A.empresa_raiz = " + iEmpresaRaiz);
            sbSql.append("\n AND A.chosno3 IS NULL"); 
            System.out.println("Query Chsono_2"+sbSql.toString());
            listEmpChosnoDos = jdbcTemplate.query(sbSql.toString(), new RowMapper()
	            {
            		public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
            		{
            			LlenaComboGralDto dtoCons = new LlenaComboGralDto();
            				dtoCons.setId(rs.getInt("chosno2"));
            				dtoCons.setDescripcion(rs.getString("nom_empresa"));
            			return dtoCons;
            		}
	            }
            );
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasChosnoDosIn");
		}
		return listEmpChosnoDos;
	}

	
	
	
	/**
	 * FunSQLChosno3
	 * @param iEmpresaRaiz
	 * @param iEmpChosno2
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<LlenaComboGralDto> consultarEmpresasChosnoTres(int iEmpresaRaiz, int iEmpChosno2)
	{
		String tipo_arbol;
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpChosnoTres = new ArrayList<LlenaComboGralDto>();
		try
		{
			sbSql.append("select distinct tipo_Arbol from arbol_empresa_fondeo where empresa_origen = " + iEmpresaRaiz);
			tipo_arbol = this.getJdbcTemplate().queryForObject(sbSql.toString(), String.class);
			
			sbSql.delete(0, sbSql.length());
			
			sbSql.append("\n   SELECT DISTINCT aef.empresa_destino, convert(char,aef.empresa_destino) + ':' + ");
			sbSql.append("\n   e.nom_empresa + ':' + convert(char,COALESCE(convert(integer,aef.valor),0.00)) ");
			sbSql.append("\n   AS nom_empresa ");
			sbSql.append("\n   FROM arbol_empresa_fondeo aef,empresa e ");
			sbSql.append("\n     WHERE  aef.empresa_destino = E.no_empresa ");
			sbSql.append("\n    AND aef.tipo_arbol = " + tipo_arbol );
			sbSql.append("\n   AND aef.orden = 0 ");
			sbSql.append("\n   AND aef.empresa_origen = " + iEmpChosno2);
			/*
			sbSql.append("SELECT DISTINCT A.chosno3, convert(char,A.chosno3) + ':' +");
            sbSql.append("\n E.nom_empresa + ':' + convert(char,COALESCE(A.monto_maximo,0.00))");
			sbSql.append("\n AS nom_empresa");
            sbSql.append("\n FROM arbol_empresa A,empresa E");
            sbSql.append("\n WHERE  A.chosno3 = E.no_empresa");
            sbSql.append("\n AND A.chosno2 = " + iEmpChosno2); 
            sbSql.append("\n AND A.chosno3 IS NOT NULL");
            sbSql.append("\n AND A.empresa_raiz = " + iEmpresaRaiz);
            sbSql.append("\n AND A.chosno4 IS NULL");*/
            System.out.println("Query Chosno_3"+sbSql.toString());
            listEmpChosnoTres = jdbcTemplate.query(sbSql.toString(), new RowMapper()
	            {
            		public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
            		{
            			LlenaComboGralDto dtoCons = new LlenaComboGralDto();
            				dtoCons.setId(rs.getInt("empresa_destino"));
            				dtoCons.setDescripcion(rs.getString("nom_empresa"));
            			return dtoCons;
            		}
	            }
            );
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasChosnoTres");
		}
		return listEmpChosnoTres;
	}


	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<LlenaComboGralDto> consultarEmpresasChosnoTresIn(int iEmpresaRaiz, int iEmpChosno2)
	{
		String tipo_arbol;
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpChosnoTres = new ArrayList<LlenaComboGralDto>();
		try
		{
			/*sbSql.append("select distinct tipo_Arbol from arbol_empresa_fondeo where empresa_origen = " + iEmpresaRaiz);
			tipo_arbol = this.getJdbcTemplate().queryForObject(sbSql.toString(), String.class);*/
			
			sbSql.delete(0, sbSql.length());
			
			
			sbSql.append("SELECT DISTINCT A.chosno3, convert(char,A.chosno3) + ':' +");
            sbSql.append("\n E.nom_empresa + ':' + convert(char,COALESCE(A.monto_maximo,0.00))");
			sbSql.append("\n AS nom_empresa");
            sbSql.append("\n FROM arbol_empresa A,empresa E");
            sbSql.append("\n WHERE  A.chosno3 = E.no_empresa");
            sbSql.append("\n AND A.chosno2 = " + iEmpChosno2); 
            sbSql.append("\n AND A.chosno3 IS NOT NULL");
            sbSql.append("\n AND A.empresa_raiz = " + iEmpresaRaiz);
            sbSql.append("\n AND A.chosno4 IS NULL");
            
            System.out.println("Query Chosno_3"+sbSql.toString());
            
            listEmpChosnoTres = jdbcTemplate.query(sbSql.toString(), new RowMapper()
	            {
            		public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
            		{
            			LlenaComboGralDto dtoCons = new LlenaComboGralDto();
            				dtoCons.setId(rs.getInt("chosno3"));
            				dtoCons.setDescripcion(rs.getString("nom_empresa"));
            			return dtoCons;
            		}
	            }
            );
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasChosnoTresIn");
		}
		return listEmpChosnoTres;
	}

	
	
	
	
	
	/**
	 * FunSQLChosno4
	 * @param iEmpresaRaiz
	 * @param iEmpChosno3
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<LlenaComboGralDto> consultarEmpresasChosnoCuatro(int iEmpresaRaiz, int iEmpChosno3)
	{
		String tipo_arbol;
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpChosnoCuatro = new ArrayList<LlenaComboGralDto>();
		try
		{
			sbSql.append("select distinct tipo_Arbol from arbol_empresa_fondeo where empresa_origen = " + iEmpresaRaiz);
			tipo_arbol = this.getJdbcTemplate().queryForObject(sbSql.toString(), String.class);
			
			sbSql.delete(0, sbSql.length());
			
			sbSql.append("\n  SELECT DISTINCT aef.empresa_destino, convert(char,aef.empresa_destino)+ ':' + ");
			sbSql.append("\n  e.nom_empresa + ':' + convert(char,COALESCE(convert(integer,aef.valor),0.00)) ");
			sbSql.append("\n  AS nom_empresa ");
			sbSql.append("\n  FROM arbol_empresa_fondeo aef,empresa e ");
			sbSql.append("\n     WHERE  aef.empresa_destino = E.no_empresa ");
			sbSql.append("\n  AND aef.tipo_arbol = " + tipo_arbol );
			sbSql.append("\n  AND aef.orden = 0 ");
			sbSql.append("\n  AND aef.empresa_origen = " + iEmpChosno3);
			/*
			sbSql.append("SELECT DISTINCT A.chosno4, convert(char,A.chosno4)+ ':' +");
            sbSql.append("\n E.nom_empresa + ':' + convert(char,COALESCE(A.monto_maximo,0.00))");
			sbSql.append("\n AS nom_empresa");
            sbSql.append("\n FROM arbol_empresa A,empresa E");
            sbSql.append("\n WHERE  A.chosno4 = E.no_empresa ");
            sbSql.append("\n AND A.chosno3 = " + iEmpChosno3);
            sbSql.append("\n AND A.chosno4 IS NOT NULL");
            sbSql.append("\n AND A.empresa_raiz = " + iEmpresaRaiz);*/
            System.out.println("Query Chosno_4"+sbSql.toString());
            listEmpChosnoCuatro = jdbcTemplate.query(sbSql.toString(), new RowMapper()
	            {
            		public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
            		{
            			LlenaComboGralDto dtoCons = new LlenaComboGralDto();
            				dtoCons.setId(rs.getInt("empresa_destino"));
            				dtoCons.setDescripcion(rs.getString("nom_empresa"));
            			return dtoCons;
            		}
	            }
            );
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasChosnoCuatro");
		}
		return listEmpChosnoCuatro;
	}

	
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<LlenaComboGralDto> consultarEmpresasChosnoCuatroIn(int iEmpresaRaiz, int iEmpChosno3)
	{
		String tipo_arbol;
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpChosnoCuatro = new ArrayList<LlenaComboGralDto>();
		try
		{
			/*sbSql.append("select distinct tipo_Arbol from arbol_empresa_fondeo where empresa_origen = " + iEmpresaRaiz);
			tipo_arbol = this.getJdbcTemplate().queryForObject(sbSql.toString(), String.class);*/
			
			sbSql.delete(0, sbSql.length());
			
		
			sbSql.append("SELECT DISTINCT A.chosno4, convert(char,A.chosno4)+ ':' +");
            sbSql.append("\n E.nom_empresa + ':' + convert(char,COALESCE(A.monto_maximo,0.00))");
			sbSql.append("\n AS nom_empresa");
            sbSql.append("\n FROM arbol_empresa A,empresa E");
            sbSql.append("\n WHERE  A.chosno4 = E.no_empresa ");
            sbSql.append("\n AND A.chosno3 = " + iEmpChosno3);
            sbSql.append("\n AND A.chosno4 IS NOT NULL");
            sbSql.append("\n AND A.empresa_raiz = " + iEmpresaRaiz);
            System.out.println("Query Chosno_4"+sbSql.toString());
            listEmpChosnoCuatro = jdbcTemplate.query(sbSql.toString(), new RowMapper()
	            {
            		public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
            		{
            			LlenaComboGralDto dtoCons = new LlenaComboGralDto();
            				dtoCons.setId(rs.getInt("chosno4"));
            				dtoCons.setDescripcion(rs.getString("nom_empresa"));
            			return dtoCons;
            		}
	            }
            );
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasChosnoCuatroIn");
		}
		return listEmpChosnoCuatro;
	}

	
	
	
	
	
	
	
	
	public void eliminarTmpArbol(int iIdUsuario)
	{
		StringBuffer sbSql = new StringBuffer();
		try
		{
			sbSql.append(" Delete from tmp_arbol");
			sbSql.append("\n where usuario_alta=" + iIdUsuario);
			System.out.println("query"+sbSql.toString());
			jdbcTemplate.update(sbSql.toString());
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:eliminarTmpArbol");
		}
	}

	/**
	 * Metodo para insertar en tmp_arbol
	 * @param iIdUsuario, usuario actual en session
	 * @param iSecuencia, orden de descendencia
	 * @param sNomEmpresa, nombre de la empresa
	 * @param sTipo, tipo de descendencia ejem(padre, hijo, nieto, etc)
	 */
	public void insertarTmpArbol(int iIdUsuario, int iSecuencia, String sNomEmpresa, String sTipo)
	{
		StringBuffer sbSql = new StringBuffer();
		try
		{
			sbSql.append("insert into tmp_arbol ");
		    sbSql.append("\n (usuario_alta,secuencia," + sTipo + ")");
		    sbSql.append("\n values (" + iIdUsuario + "," + iSecuencia + ",'" + sNomEmpresa + "')");
		    System.out.println("query"+sbSql.toString());
		    jdbcTemplate.update(sbSql.toString());
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:insertarTmpArbol");
		}
	}

	/**
	 * FunSQLSelect3
	 * @param iIdEmpRaiz
	 * @param iIdEmpresa
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Integer> consultarEmpArbolExis(int iIdEmpRaiz, int iIdEmpresa){
		List<Integer> listEmpresa = new ArrayList<Integer>();
		StringBuffer sbSql = new StringBuffer();
		try
		{
		    sbSql.append("SELECT no_empresa ");
		    sbSql.append("\n FROM arbol_empresa ");
		    sbSql.append("\n WHERE no_empresa = " + iIdEmpresa);
		    sbSql.append("\n and empresa_raiz=" + iIdEmpRaiz);
		    System.out.println("query"+sbSql.toString());
		    listEmpresa = jdbcTemplate.query(sbSql.toString(), new RowMapper()
		    {
		    	public Integer mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		return rs.getInt("no_empresa");
		    	}
		    });
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpArbolExis");
		}
		return listEmpresa;
	}

	
//angel
	

	@SuppressWarnings({ "unchecked", "rawtypes" })//METODO4 BNT NvoArbol->ejec 
	public List<Integer> consultarEmpArbolExisIn(int iIdEmpRaiz, int iIdEmpresa){
		
		List<Integer> listEmpresa = new ArrayList<Integer>();
		StringBuffer sbSql = new StringBuffer();
		try
		{
		    sbSql.append("SELECT no_empresa ");
		    sbSql.append("\n FROM arbol_empresa ");
		    sbSql.append("\n WHERE no_empresa = " + iIdEmpresa);
		    sbSql.append("\n and empresa_raiz=" + iIdEmpRaiz);
		    System.out.println("query"+sbSql.toString());
		    listEmpresa = jdbcTemplate.query(sbSql.toString(), new RowMapper()
		    {
		    	public Integer mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		return rs.getInt("no_empresa");
		    	}
		    });
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpArbolExisIn");
		}
		return listEmpresa;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * FunSQLReporte1
	 * @param iIdUsuario
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Map<String, Object>> consultarTmpArbol(int iIdUsuario)
	{
		StringBuffer sbSql = new StringBuffer();
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		try
		{
		    sbSql.append("SELECT secuencia,");
		    sbSql.append("\n coalesce(padre,'') as padre, coalesce(hijo,'') as hijo,");
		    sbSql.append("\n coalesce(nieto,'') as nieto, coalesce(bisnieto,'') as bisnieto, ");
		    sbSql.append("\n coalesce(tatara,'') as tatara, coalesce(chosno1,'') as chosno1,");
		    sbSql.append("\n coalesce(chosno2,'') as chosno2, coalesce(chosno3,'') as chosno3,");
		    sbSql.append("\n coalesce(chosno4,'') as chosno4");
		    sbSql.append("\n FROM tmp_arbol ");
		    sbSql.append("\n WHERE usuario_alta = '" + iIdUsuario + "'");
		    System.out.println("query"+sbSql.toString());
		    listMap = jdbcTemplate.query(sbSql.toString(), new RowMapper()
		    {
		    	public Map mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		Map<String, Object> map = new HashMap<String, Object>();
		    			map.put("secuencia", rs.getInt("secuencia"));
		    			map.put("padre", rs.getString("padre"));
		    			map.put("hijo", rs.getString("hijo"));
		    			map.put("nieto", rs.getString("nieto"));
		    			map.put("bisnieto", rs.getString("bisnieto"));
		    			map.put("tatara", rs.getString("tatara"));
		    			map.put("chosno1", rs.getString("chosno1"));
		    			map.put("chosno2", rs.getString("chosno2"));
		    			map.put("chosno3", rs.getString("chosno3"));
		    			map.put("chosno4", rs.getString("chosno4"));
		    		return map;
		    	}
		    });
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarTmpArbol");
		}
		return listMap;
	}

	/**
	 * FunSQLInsert1
	 * @param sCampos
	 * @param sValoresCampos
	 */
	
	
	//angel 
	public void insertarArbolEmpresaIn(String sCampos,//metodo5 btn NvoArbol->ejec insertar en el arbol
			String sValoresCampos,
			String nombreArbol,
			boolean insertaArbol){


String tipo_arbol;
StringBuffer sbSql = new StringBuffer();
try
{

sbSql.append(" select MAX(no_empresa)+1 from arbol_empresa ");
tipo_arbol = this.getJdbcTemplate().queryForObject(sbSql.toString(), String.class);

sbSql.delete(0, sbSql.length());
 sbSql.append("Insert into arbol_empresa");
sbSql.append("\n (" + sCampos + " )");
sbSql.append("\n values (" + sValoresCampos + ")");
System.out.println("querys"+sbSql.toString());
jdbcTemplate.update(sbSql.toString());



if(insertaArbol){
sbSql = new StringBuffer();
sbSql.append("Insert into cat_arbol");
sbSql.append("\n (tipo_arbol, desc_arbol )");
sbSql.append("\n values ("+tipo_arbol+",'" + nombreArbol + "')");

jdbcTemplate.update(sbSql.toString());
/*
sbSql.append("update arbol_empresa set b_padre='S' "); 
sbSql.append("where no-empresa="+empresaOrigen+"");
jdbcTemplate.update(sbSql.toString());
		  	*/


}
}
catch(Exception e)
{
bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:insertarArbolEmpresaIn");
}
}
	
	
	public void insertarArbolEmpresa(String sCampos,
									String sValoresCampos,
									String nombreArbol,
									boolean insertaArbol){
		
		
		String tipo_arbol;
		StringBuffer sbSql = new StringBuffer();
		try
		{
			
			sbSql.append("select MAX(tipo_arbol)+1 from cat_arbol");
			tipo_arbol = this.getJdbcTemplate().queryForObject(sbSql.toString(), String.class);
			
			sbSql.delete(0, sbSql.length());
		    sbSql.append("Insert into arbol_empresa");
		    sbSql.append("\n (" + sCampos + " )");
		    sbSql.append("\n values (" + sValoresCampos + ")");
		    System.out.println("query"+sbSql.toString());
		    jdbcTemplate.update(sbSql.toString());

		    if(insertaArbol){
				sbSql = new StringBuffer();
			    sbSql.append("Insert into cat_arbol");
			    sbSql.append("\n (tipo_arbol, desc_arbol )");
			    sbSql.append("\n values ("+tipo_arbol+",'" + nombreArbol + "')");

			    jdbcTemplate.update(sbSql.toString());
		    }
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:insertarArbolEmpresa");
		}
	}

	/**
	 * FunSQLUpdatePadre
	 * @param iIdEmpresaRaiz
	 * @param iIdEmpresa
	 * @param sEstatus
	 */
	public void actualizarArbEmpresaEstatusPadre(int iIdEmpresaRaiz, int iIdEmpresa, String sEstatus)
	{
		StringBuffer sbSql = new StringBuffer();
		try
		{
		    sbSql.append("UPDATE ");
		    sbSql.append("\n arbol_empresa ");
		    sbSql.append("\n SET b_padre = '" + sEstatus + "'");
		    sbSql.append("\n WHERE no_empresa = " + iIdEmpresa);
		    sbSql.append("\n and empresa_raiz=" + iIdEmpresaRaiz);
		    System.out.println("query"+sbSql.toString());
		    jdbcTemplate.update(sbSql.toString());
		    
		    
		    
		    
		    
		    
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:actualizarArbEmpresaEstatusPadre");
		}
	}
	
	public void actualizarArbEmpresaEstatusPadreIn(int iIdEmpresaRaiz, int iIdEmpresa, String sEstatus)//metodo2 actualiza el estado para que sean hiajas btn nuevArbol->Ejec
	{
		
		System.out.println("Angel para actualzar que hace"+iIdEmpresaRaiz+ iIdEmpresa+sEstatus);
		
		StringBuffer sbSql = new StringBuffer();

		StringBuffer sbSql2 = new StringBuffer();
		try
		{
			
	        /*  set b_padre='S' 
			  where no_empresa=926
	         and empresa_raiz=111*/
	
		    sbSql.append("UPDATE ");
		    sbSql.append("\n arbol_empresa ");
		    sbSql.append("\n SET b_padre = '" + sEstatus + "'");
		    sbSql.append("\n WHERE no_empresa = " + iIdEmpresa);
		    sbSql.append("\n and empresa_raiz=" + iIdEmpresaRaiz);
		    System.out.println("query"+sbSql.toString());
		    jdbcTemplate.update(sbSql.toString());
		    
		    
		    
		    sbSql2.append("UPDATE ");
		    sbSql2.append("\n empresa ");
		    sbSql2.append("\n SET b_concentradora = '" + sEstatus + "'");
		    sbSql2.append("\n WHERE no_empresa = " + iIdEmpresa);
		    sbSql2.append("\n and no_controladora=" + iIdEmpresaRaiz);
		    System.out.println("query"+sbSql2.toString());
		    jdbcTemplate.update(sbSql2.toString());
		    
		    
		    
		    
		    
		    
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:actualizarArbEmpresaEstatusPadreIn");
		}
	}
	

	/**
	 * FunSQLSelectValidaPadre
	 * @param iIdEmpresaRaiz
	 * @param iIdEmpresa
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<ArbolEmpresaDto> consultarValidaPadre(int iIdEmpresaRaiz, int iIdEmpresa)
	{
		StringBuffer sbSql = new StringBuffer();
		List<ArbolEmpresaDto> listArbol = new ArrayList<ArbolEmpresaDto>();
		try
		{
			sbSql.append("SELECT b_padre ");
			sbSql.append("\n FROM arbol_empresa");
			sbSql.append("\n WHERE no_empresa = " + iIdEmpresa);
			sbSql.append("\n and empresa_raiz = " + iIdEmpresaRaiz);
			System.out.println("query"+sbSql.toString());
			listArbol = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
				public ArbolEmpresaDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					ArbolEmpresaDto dto = new ArbolEmpresaDto();
						dto.setBPadre(rs.getString("b_padre"));
					return dto;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarValidaPadre");
		}
		return listArbol;
	}

	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<ArbolEmpresaDto> consultarValidaPadreIn(int iIdEmpresaRaiz, int iIdEmpresa)
	{
		StringBuffer sbSql = new StringBuffer();
		List<ArbolEmpresaDto> listArbol = new ArrayList<ArbolEmpresaDto>();
		try
		{
			sbSql.append("SELECT b_padre ");
			sbSql.append("\n FROM arbol_empresa");
			sbSql.append("\n WHERE no_empresa = " + iIdEmpresa);
			sbSql.append("\n and empresa_raiz = " + iIdEmpresaRaiz);
			System.out.println("query"+sbSql.toString());
			listArbol = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
				public ArbolEmpresaDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					ArbolEmpresaDto dto = new ArbolEmpresaDto();
						dto.setBPadre(rs.getString("b_padre"));
					return dto;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarValidaPadreIn");
		}
		return listArbol;
	}
	
	
	
	public double consultarSaldoEmpresaArbolIn(int iIdEmpresa)
	{
		StringBuffer sbSql = new StringBuffer();
		BigDecimal bdSaldo = new BigDecimal(0);
		double uSaldo = 0;
		try
		{
		    sbSql.append("SELECT isnull((");
		    sbSql.append("\n    ( SELECT coalesce(hs.importe,0) ");
		    sbSql.append("\n      FROM saldo hs ");
		    sbSql.append("\n      WHERE hs.id_tipo_saldo = 90 ");
		    sbSql.append("\n          and hs.no_empresa = e.no_empresa ");
		    sbSql.append("\n          and hs.no_cuenta = e.no_cuenta_emp ");
		    sbSql.append("\n          and hs.no_linea = e.no_linea_emp ");
		    sbSql.append("\n     ) ");
		    sbSql.append("\n  + ( SELECT coalesce(sum(hs.importe),0) ");
		    sbSql.append("\n      FROM saldo hs ");
		    sbSql.append("\n      WHERE hs.id_tipo_saldo in (8) ");
		    sbSql.append("\n          and hs.no_empresa = e.no_empresa ");
		    sbSql.append("\n     ) ");
		    sbSql.append("\n  - ( SELECT coalesce(sum(hs.importe),0) ");
		    sbSql.append("\n      FROM saldo hs ");
		    sbSql.append("\n      WHERE hs.id_tipo_saldo in (9) ");
		    sbSql.append("\n          and hs.no_empresa = e.no_empresa ");
		    sbSql.append("\n    ) ), 0)as importe ");
		    sbSql.append("\n FROM empresa e ");
		    sbSql.append("\n WHERE e.no_empresa = " + iIdEmpresa);
		    System.out.println("query"+sbSql.toString());
		    bdSaldo = jdbcTemplate.queryForObject(sbSql.toString(), BigDecimal.class);
		    uSaldo = bdSaldo.doubleValue();
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarSaldoEmpresaArbol");
		}
		return uSaldo;
	}
	/**
	 * FunSQLSelect_Saldo_Empresa_Arbol
	 * @param iIdEmpresa
	 * @return
	 */
	public double consultarSaldoEmpresaArbol(int iIdEmpresa)
	{
		StringBuffer sbSql = new StringBuffer();
		BigDecimal bdSaldo = new BigDecimal(0);
		double uSaldo = 0;
		try
		{
		    sbSql.append("SELECT isnull((");
		    sbSql.append("\n    ( SELECT coalesce(hs.importe,0) ");
		    sbSql.append("\n      FROM saldo hs ");
		    sbSql.append("\n      WHERE hs.id_tipo_saldo = 90 ");
		    sbSql.append("\n          and hs.no_empresa = e.no_empresa ");
		    sbSql.append("\n          and hs.no_cuenta = e.no_cuenta_emp ");
		    sbSql.append("\n          and hs.no_linea = e.no_linea_emp ");
		    sbSql.append("\n     ) ");
		    sbSql.append("\n  + ( SELECT coalesce(sum(hs.importe),0) ");
		    sbSql.append("\n      FROM saldo hs ");
		    sbSql.append("\n      WHERE hs.id_tipo_saldo in (8) ");
		    sbSql.append("\n          and hs.no_empresa = e.no_empresa ");
		    sbSql.append("\n     ) ");
		    sbSql.append("\n  - ( SELECT coalesce(sum(hs.importe),0) ");
		    sbSql.append("\n      FROM saldo hs ");
		    sbSql.append("\n      WHERE hs.id_tipo_saldo in (9) ");
		    sbSql.append("\n          and hs.no_empresa = e.no_empresa ");
		    sbSql.append("\n    ) ), 0)as importe ");
		    sbSql.append("\n FROM empresa e ");
		    sbSql.append("\n WHERE e.no_empresa = " + iIdEmpresa);
		    System.out.println("query"+sbSql.toString());
		    bdSaldo = jdbcTemplate.queryForObject(sbSql.toString(), BigDecimal.class);
		    uSaldo = bdSaldo.doubleValue();
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarSaldoEmpresaArbol");
		}
		return uSaldo;
	}

	/**
	 * FunSQLDeleteNodo
	 * @param iIdEmpresaRaiz
	 * @param iIdEmpresa
	 * @return
	 */
	public int eliminarNodoArbolEmpresa(int iIdEmpresaRaiz, int iIdEmpresa)
	{
		StringBuffer sbSql = new StringBuffer();
		int iRegAfec = 0;
		try
		{
		    sbSql.append("DELETE from arbol_empresa");
		    sbSql.append("\n where no_empresa = " + iIdEmpresa);
		    sbSql.append("\n and empresa_raiz=" + iIdEmpresaRaiz);

		    iRegAfec = jdbcTemplate.update(sbSql.toString());
		    
		    sbSql.delete(0, sbSql.length());
		    sbSql.append("DELETE from arbol_empresa_fondeo");
		    sbSql.append("\n whe re empresa_destino = " + iIdEmpresa);
		    sbSql.append("\n and empresa_raiz=" + iIdEmpresaRaiz);
		    System.out.println("query"+sbSql.toString());
		    iRegAfec = jdbcTemplate.update(sbSql.toString());
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:eliminarNodoArbolEmpresa");
		}
		return iRegAfec;
	}


	
	
	
	
	
	public int eliminarNodoArbolEmpresaIn(int iIdEmpresaRaiz, int iIdEmpresa)
	{
		StringBuffer sbSql = new StringBuffer();
		int iRegAfec = 0;
		try
		{
		    sbSql.append("DELETE from arbol_empresa");
		    sbSql.append("\n where no_empresa = " + iIdEmpresa);
		    sbSql.append("\n and empresa_raiz=" + iIdEmpresaRaiz);
		    System.out.println("query"+sbSql.toString());
		    iRegAfec = jdbcTemplate.update(sbSql.toString());
		    
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:eliminarNodoArbolEmpresa");
		}
		return iRegAfec;
	}

	
	
	
	
	/**
	 * FunSQLSelect5
	 * @param iIdEmpresaRaiz
	 * @param iIdEmpresa
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> consultarCampoArbolEmpresa(int iIdEmpresaRaiz, int iIdEmpresa)
	{
		StringBuffer sbSql = new StringBuffer();
		List<String> listCampo = new ArrayList<String>();
		try
		{
		    sbSql.append("SELECT case when  hijo is null then 'padre'");
		    sbSql.append("\n     when  nieto is null then 'hijo'");
		    sbSql.append("\n     when  bisnieto is null then 'nieto'");
		    sbSql.append("\n     when  tataranieto is null then 'bisnieto'");
		    sbSql.append("\n     when  chosno is null then 'tataranieto'");
		    sbSql.append("\n     when  chosno2 is null then 'chosno'");
		    sbSql.append("\n     when chosno3 is null then 'chosno2'");
		    sbSql.append("\n     when chosno4 is null then 'chosno3'");
		    sbSql.append("\n     end as campo,*");
		    sbSql.append("\n FROM arbol_empresa");
		    sbSql.append("\n WHERE no_empresa = " + iIdEmpresa);
		    sbSql.append("\n and empresa_raiz =" + iIdEmpresaRaiz);
		    System.out.println("query"+sbSql.toString());
		    listCampo = jdbcTemplate.query(sbSql.toString(), new RowMapper()
		    {
		    	public String mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		return rs.getString("campo");
		    	}
		    });
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarCampoArbolEmpresa");
		}
		return listCampo;
	}


	
	
	
	
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> consultarCampoArbolEmpresaIn(int iIdEmpresaRaiz, int iIdEmpresa)
	{
		StringBuffer sbSql = new StringBuffer();
		List<String> listCampo = new ArrayList<String>();
		try
		{
		    sbSql.append("SELECT case when  hijo is null then 'padre'");
		    sbSql.append("\n     when  nieto is null then 'hijo'");
		    sbSql.append("\n     when  bisnieto is null then 'nieto'");
		    sbSql.append("\n     when  tataranieto is null then 'bisnieto'");
		    sbSql.append("\n     when  chosno is null then 'tataranieto'");
		    sbSql.append("\n     when  chosno2 is null then 'chosno'");
		    sbSql.append("\n     when chosno3 is null then 'chosno2'");
		    sbSql.append("\n     when chosno4 is null then 'chosno3'");
		    sbSql.append("\n     end as campo,*");
		    sbSql.append("\n FROM arbol_empresa");
		    sbSql.append("\n WHERE no_empresa = " + iIdEmpresa);
		    sbSql.append("\n and empresa_raiz =" + iIdEmpresaRaiz);
		    System.out.println("query"+sbSql.toString());
		    listCampo = jdbcTemplate.query(sbSql.toString(), new RowMapper()
		    {
		    	public String mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		return rs.getString("campo");
		    	}
		    });
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarCampoArbolEmpresaIn");
		}
		return listCampo;
	}

	
	
	
	
	
	/**
	 * FunSQLSelectCuentaNodos
	 * @param iIdEmpresaRaiz
	 * @param iIdEmpresa
	 * @return
	 */
	public int consultarNumeroEmpresasArbol(int iIdEmpresaRaiz, int iIdEmpresa)
	{
		StringBuffer sbSql = new StringBuffer();
		int iNumEmp = 0;
		try
		{
		    sbSql.append("SELECT count(no_empresa) as contador");
		    sbSql.append("\n FROM arbol_empresa");
		    sbSql.append("\n WHERE  no_empresa = " + iIdEmpresa);
		    sbSql.append("\n and empresa_raiz=" + iIdEmpresaRaiz);
		    System.out.println("query"+sbSql.toString());
		    iNumEmp = jdbcTemplate.queryForInt(sbSql.toString());
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarNumeroEmpresasArbol");
		}
		return iNumEmp;
	}

	
	
	
	
	
	public int consultarNumeroEmpresasArbolIn(int iIdEmpresaRaiz, int iIdEmpresa)
	{
		StringBuffer sbSql = new StringBuffer();
		int iNumEmp = 0;
		try
		{
		    sbSql.append("SELECT count(no_empresa) as contador");
		    sbSql.append("\n FROM arbol_empresa");
		    sbSql.append("\n WHERE  no_empresa = " + iIdEmpresa);
		    sbSql.append("\n and empresa_raiz=" + iIdEmpresaRaiz);
		    System.out.println("query"+sbSql.toString());
		    iNumEmp = jdbcTemplate.queryForInt(sbSql.toString());
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarNumeroEmpresasArbolIn");
		}
		return iNumEmp;
	}
	
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<LlenaComboGralDto> consultarEmpresasHijo(int iEmpRaiz) {
		List<LlenaComboGralDto> listEmpH = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSql = new StringBuffer();
		try
		{
//			sbSql.append("SELECT no_empresa, nom_empresa From empresa");
//		    sbSql.append("\n WHERE no_empresa not in");
//		    sbSql.append("\n (Select no_empresa from arbol_empresa");
//		    sbSql.append("\n where ");
//		    sbSql.append("\n empresa_raiz=" + iEmpRaiz + ")");

			sbSql.append(" select e.no_empresa, e.nom_empresa ");
			sbSql.append(" from empresa e ");
			sbSql.append(" where e.no_empresa != ?");
		//	sbSql.append(" and c.no_empresa = ? ");
			System.out.println("query"+sbSql.toString());
			Integer [] parametro = new Integer[1];

			parametro[0] = iEmpRaiz;

		    listEmpH = jdbcTemplate.query(sbSql.toString(), parametro, new RowMapper()
			{
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						dtoCons.setId(rs.getInt("no_empresa"));
						dtoCons.setDescripcion(rs.getString("nom_empresa"));
					return dtoCons;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasHijo");
		}
		return listEmpH;
	}

	
	
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<LlenaComboGralDto> consultarEmpresasHijoIn(int iEmpRaiz) {
		List<LlenaComboGralDto> listEmpH = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSql = new StringBuffer();
		try
		{
			/*sbSql.append("SELECT no_empresa, nom_empresa From empresa");
		    sbSql.append("\n WHERE no_empresa not in");
		    sbSql.append("\n (Select no_empresa from arbol_empresa");
		    sbSql.append("\n where ");
	    sbSql.append("\n empresa_raiz=" + iEmpRaiz + ")");*/

			
			sbSql.append(" select e.no_empresa, e.nom_empresa ");
			sbSql.append(" from empresa e ");
			sbSql.append(" where e.no_empresa != ?");
		//	sbSql.append(" and c.no_empresa = ? ");
			System.out.println("query"+sbSql.toString());
			Integer [] parametro = new Integer[1];

			parametro[0] = iEmpRaiz;

		    listEmpH = jdbcTemplate.query(sbSql.toString(), parametro, new RowMapper()
			{
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						dtoCons.setId(rs.getInt("no_empresa"));
						dtoCons.setDescripcion(rs.getString("nom_empresa"));
					return dtoCons;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasHijo");
		}
		return listEmpH;
	}

	
	
	
	
	
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void consultarMontoAct (int monto, int idEmpresa,  int idRaiz) {
	//System.out.println("Angel para actualzar que hace"+iIdEmpresaRaiz+ iIdEmpresa+sEstatus);
		
		StringBuffer sbSql = new StringBuffer();

		StringBuffer sbSql2 = new StringBuffer();
		try
		{
			
	       
			
			
			
	          sbSql.append("UPDATE ");
		    sbSql.append("\n arbol_empresa ");
		    sbSql.append("\n  set monto_maximo = "+monto+" ");
		    sbSql.append("\n where no_empresa = " + idEmpresa );
		    sbSql.append("\n and empresa_raiz=" + idRaiz);
		    System.out.println("query"+sbSql.toString());
		    jdbcTemplate.update(sbSql.toString());
		    
		       
		    
		    
		    
		    
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:actualizarMonto");
		}
	}

	
	
	
	
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<LlenaComboGralDto> consultarEmpresaPadreIn(int iEmpresaRaiz) {
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listPadre = new ArrayList<LlenaComboGralDto>();
		try
		{
			

			sbSql.append("select DISTINCT A.padre, convert(char,A.padre) + ':' + E.nom_empresa");
            sbSql.append("\n AS nom_empresa");
            sbSql.append("\n FROM arbol_empresa A,empresa E");
            sbSql.append("\n WHERE  A.padre = E.no_empresa");
            sbSql.append("\n AND A.padre IS NOT NULL");
            sbSql.append("\n AND a.empresa_raiz = " + iEmpresaRaiz);
			
            System.out.println("Query Padre "+sbSql.toString());
			listPadre = jdbcTemplate.query(sbSql.toString(), new RowMapper()
			{
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						dtoCons.setId(rs.getInt("padre"));
						dtoCons.setDescripcion(rs.getString("nom_empresa"));
					return dtoCons;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresaPadreIn");
		}
		return listPadre;
	}

	
	
	
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<LlenaComboGralDto> consultarEmpresaPadre(int iEmpresaRaiz) {
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listPadre = new ArrayList<LlenaComboGralDto>();
		try
		{
			sbSql.append("\n select DISTINCT aef.empresa_origen, convert(char,aef.empresa_origen) + ':' + E.nom_empresa ");
			sbSql.append("\n  AS nom_empresa ");
			sbSql.append("\n  FROM arbol_empresa_fondeo aef,empresa e ");
			sbSql.append("\n  WHERE  aef.empresa_origen = E.no_empresa ");
	//		sbSql.append("\n  AND aef.empresa_destino = 0 ");
			sbSql.append("\n  AND aef.orden = 0 ");
			sbSql.append("\n  AND aef.empresa_origen = " + iEmpresaRaiz);
/*
			sbSql.append("select DISTINCT A.padre, convert(char,A.padre) + ':' + E.nom_empresa");
            sbSql.append("\n AS nom_empresa");
            sbSql.append("\n FROM arbol_empresa A,empresa E");
            sbSql.append("\n WHERE  A.padre = E.no_empresa");
            sbSql.append("\n AND A.padre IS NOT NULL");
            sbSql.append("\n AND a.empresa_raiz = " + iEmpresaRaiz);*/
			
            System.out.println("Query Padre "+sbSql.toString());
			listPadre = jdbcTemplate.query(sbSql.toString(), new RowMapper()
			{
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						dtoCons.setId(rs.getInt("empresa_origen"));
						dtoCons.setDescripcion(rs.getString("nom_empresa"));
					return dtoCons;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresaPadre");
		}
		return listPadre;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<LlenaComboGralDto> consultarEmpresasHijos(int iEmpresaRaiz,
			int iEmpPadre) {
		String tipo_arbol;
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpHijo = new ArrayList<LlenaComboGralDto>();
		try
		{
			sbSql.append("select distinct tipo_Arbol from arbol_empresa_fondeo where empresa_origen = " + iEmpresaRaiz);
			tipo_arbol = this.getJdbcTemplate().queryForObject(sbSql.toString(), String.class);
			
			sbSql.delete(0, sbSql.length());
			
			sbSql.append("\n SELECT DISTINCT aef.empresa_destino, convert(char,aef.empresa_destino) + ':' + ");
			sbSql.append("\n e.nom_empresa + ':' + convert(char,COALESCE(convert(integer,aef.valor),0.00)) ");
			sbSql.append("\n AS nom_empresa ");
			sbSql.append("\n  FROM arbol_empresa_fondeo aef,empresa e ");
			sbSql.append("\n  WHERE  aef.empresa_destino = E.no_empresa ");
			sbSql.append("\n  AND aef.tipo_arbol = " + tipo_arbol );
			sbSql.append("\n  AND aef.orden <> 0 ");
			sbSql.append("\n  AND aef.empresa_origen = " + iEmpPadre );
			
			/*
			sbSql.append("SELECT DISTINCT A.hijo, convert(char,A.hijo) + ':' + ");
            sbSql.append("\n E.nom_empresa + ':' + convert(char,COALESCE(A.monto_maximo,0.00))");
            sbSql.append("\n AS nom_empresa ");
            sbSql.append("\n FROM arbol_empresa A,empresa E ");
            sbSql.append("\n WHERE  A.hijo = E.no_empresa ");
            sbSql.append("\n AND A.padre = " + iEmpPadre);
            sbSql.append("\n AND A.hijo IS NOT NULL");
            sbSql.append("\n AND A.empresa_raiz = " + iEmpresaRaiz);
            sbSql.append("\n AND A.nieto IS NULL"); */
            System.out.println("Query Hijo "+sbSql.toString());
            listEmpHijo = jdbcTemplate.query(sbSql.toString(), new RowMapper()
	            {
            		public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
            		{
            			LlenaComboGralDto dtoCons = new LlenaComboGralDto();
            				dtoCons.setId(rs.getInt("empresa_destino"));
            				dtoCons.setDescripcion(rs.getString("nom_empresa"));
            			return dtoCons;
            		}
	            }
            );
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasHijos");
		}
		return listEmpHijo;
	}

	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<LlenaComboGralDto> consultarEmpresasHijosIn(int iEmpresaRaiz,
			int iEmpPadre) {
		String tipo_arbol;
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpHijo = new ArrayList<LlenaComboGralDto>();
		try
		{
			/*sbSql.append("select distinct tipo_Arbol from arbol_empresa_fondeo where empresa_origen = " + iEmpresaRaiz);
			tipo_arbol = this.getJdbcTemplate().queryForObject(sbSql.toString(), String.class);
			*/
			sbSql.delete(0, sbSql.length());
			
			
			
			
			sbSql.append("SELECT DISTINCT A.hijo, convert(char,A.hijo) + ':' + ");
            sbSql.append("\n E.nom_empresa + ':' + convert(char,COALESCE(A.monto_maximo,0.00))");
            sbSql.append("\n AS nom_empresa ");
            sbSql.append("\n FROM arbol_empresa A,empresa E ");
            sbSql.append("\n WHERE  A.hijo = E.no_empresa ");
            sbSql.append("\n AND A.padre = " + iEmpPadre);
            sbSql.append("\n AND A.hijo IS NOT NULL");
            sbSql.append("\n AND A.empresa_raiz = " + iEmpresaRaiz);
            sbSql.append("\n AND A.nieto IS NULL"); 
            System.out.println("Query Hijo "+sbSql.toString());
            listEmpHijo = jdbcTemplate.query(sbSql.toString(), new RowMapper()
	            {
            		public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
            		{
            			LlenaComboGralDto dtoCons = new LlenaComboGralDto();
            				dtoCons.setId(rs.getInt("hijo"));
            				dtoCons.setDescripcion(rs.getString("nom_empresa"));
            			return dtoCons;
            		}
	            }
            );
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasHijosIn");
		}
		return listEmpHijo;
	}

	
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<LlenaComboGralDto> consultarEmpresasNietos(int iEmpresaRaiz,
			int iEmpHijo) {
		String tipo_arbol;
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpNieto = new ArrayList<LlenaComboGralDto>();
		try
		{
			sbSql.append("select distinct tipo_Arbol from arbol_empresa_fondeo where empresa_origen = " + iEmpresaRaiz);
			tipo_arbol = this.getJdbcTemplate().queryForObject(sbSql.toString(), String.class);
			
			sbSql.delete(0, sbSql.length());
			
sbSql.append("\n SELECT DISTINCT aef.empresa_destino, convert(char,aef.empresa_destino) + ':' + ");
sbSql.append("\n  e.nom_empresa + ':' + convert(char,COALESCE(convert(integer,aef.valor),0.00)) ");
sbSql.append("\n  AS nom_empresa ");
sbSql.append("\n  FROM arbol_empresa_fondeo aef,empresa e ");
sbSql.append("\n  WHERE  aef.empresa_destino = E.no_empresa "); 
sbSql.append("\n  AND aef.tipo_arbol = " + tipo_arbol );
sbSql.append("\n  AND aef.orden <> 0 ");
sbSql.append("\n  AND aef.empresa_origen = " + iEmpHijo );
			
/*
			sbSql.append("SELECT DISTINCT A.nieto, convert(char,A.nieto) + ':' + ");
            sbSql.append("\n E.nom_empresa + ':' + convert(char,COALESCE(A.monto_maximo,0.00))");
			sbSql.append("\n AS nom_empresa");
            sbSql.append("\n FROM arbol_empresa A,empresa E");
            sbSql.append("\n WHERE  A.nieto = E.no_empresa ");
            sbSql.append("\n AND A.hijo = " + iEmpHijo);
            sbSql.append("\n AND A.nieto IS NOT NULL");
            sbSql.append("\n AND A.empresa_raiz = " + iEmpresaRaiz);
            sbSql.append("\n AND A.bisnieto IS NULL"); */
            System.out.println("Query Nieto"+sbSql.toString());
            listEmpNieto = jdbcTemplate.query(sbSql.toString(), new RowMapper()
	            {
            		public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
            		{
            			LlenaComboGralDto dtoCons = new LlenaComboGralDto();
            				dtoCons.setId(rs.getInt("empresa_destino"));
            				dtoCons.setDescripcion(rs.getString("nom_empresa"));
            			return dtoCons;
            		}
	            }
            );
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasNietos");
		}
		return listEmpNieto;
	}

	
	
	
	
	
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<LlenaComboGralDto> consultarEmpresasNietosIn(int iEmpresaRaiz,
			int iEmpHijo) {
		String tipo_arbol;
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpNieto = new ArrayList<LlenaComboGralDto>();
		try
		{
			/*sbSql.append("select distinct tipo_Arbol from arbol_empresa_fondeo where empresa_origen = " + iEmpresaRaiz);
			tipo_arbol = this.getJdbcTemplate().queryForObject(sbSql.toString(), String.class);
			*/
			sbSql.delete(0, sbSql.length());
		
			

			sbSql.append("SELECT DISTINCT A.nieto, convert(char,A.nieto) + ':' + ");
            sbSql.append("\n E.nom_empresa + ':' + convert(char,COALESCE(A.monto_maximo,0.00))");
			sbSql.append("\n AS nom_empresa");
            sbSql.append("\n FROM arbol_empresa A,empresa E");
            sbSql.append("\n WHERE  A.nieto = E.no_empresa ");
            sbSql.append("\n AND A.hijo = " + iEmpHijo);
            sbSql.append("\n AND A.nieto IS NOT NULL");
            sbSql.append("\n AND A.empresa_raiz = " + iEmpresaRaiz);
            sbSql.append("\n AND A.bisnieto IS NULL"); 
            System.out.println("Query Nieto"+sbSql.toString());
            listEmpNieto = jdbcTemplate.query(sbSql.toString(), new RowMapper()
	            {
            		public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
            		{
            			LlenaComboGralDto dtoCons = new LlenaComboGralDto();
            				dtoCons.setId(rs.getInt("nieto"));
            				dtoCons.setDescripcion(rs.getString("nom_empresa"));
            			return dtoCons;
            		}
	            }
            );
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasNietosIn");
		}
		return listEmpNieto;
	}

	
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<LlenaComboGralDto> consultarEmpresaConcentradora(int iIdUsuario) {
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpCon = new ArrayList<LlenaComboGralDto>();
		try
		{
		    sbSql.append("SELECT no_empresa, nom_empresa");
		    sbSql.append("\n FROM empresa ");
		    sbSql.append("\n WHERE b_concentradora = 'S' ");

		    if(iIdUsuario > 0)
		    {
		    	sbSql.append("\n AND no_empresa in ");
		        sbSql.append("\n     ( SELECT DISTINCT no_controladora ");
		        sbSql.append("\n       FROM empresa ");
		        sbSql.append("\n       WHERE no_empresa in ");
		        sbSql.append("\n           ( SELECT no_empresa ");
		        sbSql.append("\n             FROM usuario_empresa ");
		        sbSql.append("\n             WHERE no_usuario = " + iIdUsuario + " ) ) ");
		    }
		    System.out.println("query"+sbSql.toString());
			listEmpCon = jdbcTemplate.query(sbSql.toString(), new RowMapper()
	            {
            		public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
            		{
            			LlenaComboGralDto dtoCons = new LlenaComboGralDto();
            				dtoCons.setId(rs.getInt("no_empresa"));
            				dtoCons.setDescripcion(rs.getString("nom_empresa"));
            			return dtoCons;
            		}
	            }
            );
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresaConcentradora");
		}
		return listEmpCon;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<LlenaComboGralDto> consultarBancosEmpresa(int iIdEmpresa,
			String sIdDivisa) {
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listBanc = new ArrayList<LlenaComboGralDto>();
		try
		{
		    sbSql.append("SELECT distinct cat_banco.id_banco, ");
		    sbSql.append("\n     cat_banco.desc_banco");
		    sbSql.append("\n FROM cat_banco,cat_cta_banco ");
		    sbSql.append("\n WHERE cat_cta_banco.id_banco = cat_banco.id_banco ");
		    sbSql.append("\n     and cat_cta_banco.no_empresa = " + iIdEmpresa);
		    sbSql.append("\n     and tipo_chequera <> 'U' ");
		    System.out.println("query"+sbSql.toString());
		    if(!sIdDivisa.equals(""))
		    	sbSql.append("\n 	and cat_cta_banco.id_divisa='" + sIdDivisa + "'");

		    listBanc = jdbcTemplate.query(sbSql.toString(), new RowMapper()
	            {
            		public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
            		{
            			LlenaComboGralDto dtoCons = new LlenaComboGralDto();
            				dtoCons.setId(rs.getInt("id_banco"));
            				dtoCons.setDescripcion(rs.getString("desc_banco"));
            			return dtoCons;
            		}
	            }
            );
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarBancosEmpresa");
		}
		return listBanc;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<LlenaComboGralDto> consultarEmpresasArbol(int iIdEmpresa,
			String sCondicion) {
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmp = new ArrayList<LlenaComboGralDto>();
		try
		{
		    sbSql.append("Select ae.no_empresa, e.nom_empresa");
		    sbSql.append("\n From arbol_empresa ae, empresa e");
		    sbSql.append("\n Where e.no_empresa=ae.no_empresa ");

		    if(!sCondicion.equals(""))
		        sbSql.append("\n and " + sCondicion + " = " + iIdEmpresa);

		    sbSql.append("\n and ae.no_empresa <> " + iIdEmpresa);
		    System.out.println("query"+sbSql.toString());
			listEmp = jdbcTemplate.query(sbSql.toString(), new RowMapper()
	            {
            		public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
            		{
            			LlenaComboGralDto dtoCons = new LlenaComboGralDto();
            				dtoCons.setId(rs.getInt("no_empresa"));
            				dtoCons.setDescripcion(rs.getString("nom_empresa"));
            			return dtoCons;
            		}
	            }
            );
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasArbol");
		}
		return listEmp;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<LlenaComboGralDto> consultarEmpresasArbol() {
		List<LlenaComboGralDto> listEmp = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSql = new StringBuffer();
		try
		{
			sbSql.append("Select ae.no_empresa as ID, e.nom_empresa as describ");
		    sbSql.append("\n From arbol_empresa ae,empresa e");
		    sbSql.append("\n Where e.no_empresa = ae.no_empresa");
		    sbSql.append("\n and ae.no_empresa <> ae.padre");
		    System.out.println("query"+sbSql.toString());
		    listEmp = jdbcTemplate.query(sbSql.toString(), new RowMapper()
		    {
		    	public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		LlenaComboGralDto dtoCons = new LlenaComboGralDto();
		    			dtoCons.setId(rs.getInt("ID"));
		    			dtoCons.setDescripcion(rs.getString("describ"));
		    		return dtoCons;
		    	}
		    });
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasArbol");
		}
		return listEmp;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<LlenaComboGralDto> consultarTipoOperacion() {
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpNieto = new ArrayList<LlenaComboGralDto>();
		try
		{
			sbSql.append(" select distinct id_tipo_operacion as ID,  ");
			sbSql.append(" desc_tipo_operacion as describ ");
			sbSql.append("  from cat_tipo_operacion ");
			sbSql.append(" Where id_tipo_operacion in (3806,3805) ");
			System.out.println("query"+sbSql.toString());
            listEmpNieto = jdbcTemplate.query(sbSql.toString(), new RowMapper()
	            {
            		public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
            		{
            			LlenaComboGralDto dtoCons = new LlenaComboGralDto();
            				dtoCons.setId(rs.getInt("ID"));
            				dtoCons.setDescripcion(rs.getString("describ"));
            			return dtoCons;
            		}
	            }
            );
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasNietos");
		}
		return listEmpNieto;
	}

	public void insertarArbolEmpresaFondeo(int idEmpresaRaiz,
											int empresaOrigen,
											int empresaDestino,
											int tipoOperacion,
											String tipoValor, 
											double valor, 
											String continua, 
											int orden, 
											String descArbol){
		String tipo_arbol;
		StringBuffer sbSql = new StringBuffer();
		try
		{
			
			sbSql.append("select MAX(tipo_arbol)+1 from arbol_empresa_fondeo");
			tipo_arbol = this.getJdbcTemplate().queryForObject(sbSql.toString(), String.class);
			
			sbSql.delete(0, sbSql.length());
			
		    sbSql.append("Insert into arbol_empresa_fondeo");
		    sbSql.append("\n (TIPO_ARBOL, EMPRESA_ORIGEN, EMPRESA_DESTINO, TIPO_OPERACION, TIPO_VALOR, VALOR, CONTINUA, ORDEN, DESC_ARBOL )");
		    sbSql.append("\n values (" + tipo_arbol + ", "+ empresaOrigen +","+ empresaOrigen +", " + tipoOperacion +", '"+ tipoValor +"', ");
		    sbSql.append(valor + ", '"+continua +"' ," + orden +", '"+ descArbol +"')" );
		    System.out.println("query"+sbSql.toString());
		    System.out.println("El valor es "+ valor);

		    jdbcTemplate.update(sbSql.toString());

		}
		catch(Exception e){
			System.out.println("Error "+ e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:insertarArbolEmpresa");
		}

	}
	

	
	
	
	
	
	
	
	

	public void insertarArbolEmpresaIn(int idEmpresaRaiz, //metodo3 el insert bnt  nvoArbo->Ejec
											int empresaOrigen,
											int empresaDestino,
											int tipoOperacion,
											String tipoValor, 
											double valor, 
											String continua, 
											int orden, 
											String descArbol){
		String tipo_arbol;
		StringBuffer sbSql = new StringBuffer();
		try
		{
			
			sbSql.append(" select MAX(no_empresa)+1 from arbol_empresa ");
			tipo_arbol = this.getJdbcTemplate().queryForObject(sbSql.toString(), String.class);
			
			sbSql.delete(0, sbSql.length());
			


 
			
		    sbSql.append("Insert into arbol_empresa");
		    
		    sbSql.append("\n (no_empresa,"
		    			+ " empresa_raiz,"
		    			+ " padre,"
		    			+ " hijo, "
		    			+ " nieto, "
		    			+ " bisnieto,"
		    			+ " tataranieto, "
		    			+ "chosno,"
		    			+ " chosno2,"
		    			+ "chosno3,"
		    			+ "chosno4,"
		    			+ "b_padre,"
		    			+ "monto_maximo )");
		    //los datos que recibe son: tipo_arbol=noempresa, empresaOrigen=empresa_raiz,empresaOrigen=padre, 
		    sbSql.append("\n values"+ " (" + tipo_arbol + ", "+ empresaOrigen +","+ empresaOrigen +", " + 0+", "+ 0 +", " + 0+", " + 0+", " + 0+", " + 0+", " + 0+", " + 0+" ");
		    					sbSql.append(  ", '"+continua +"' ," + valor +")" );

		    jdbcTemplate.update(sbSql.toString());
		    
		    //regresa
		    
		    sbSql.delete(0, sbSql.length());
			
		     
		    sbSql.append("update arbol_empresa set b_padre='S' "); 
		    sbSql.append("where no-empresa="+empresaOrigen+"");
		    jdbcTemplate.update(sbSql.toString());
		    		  	
		    sbSql.delete(0, sbSql.length());
			
		    
		    
		    

		}
		catch(Exception e){
			System.out.println("Error "+ e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:insertarArbolEmpresaIn");
		}

	}
	

	
	
	
	
	
	//angel modificarlo para arbol_empresa
	public Integer consultarRutaEmpArbolExisIn(int iIdEmpRaiz, int iIdEmpresaPadre, int iIdEmpresa){ //metodo1 de la capa de bissnes solo consulta
		StringBuffer sbSql = new StringBuffer();

		Integer cantOcurrencias = 0;

		Integer[] parametros = new Integer[3];

		parametros[0] = iIdEmpresa;
		parametros[1] = iIdEmpresaPadre;
		parametros[2] = iIdEmpRaiz;
		
		System.out.println("Angel parametros"+parametros[0]+parametros[1]+parametros[2]+"\n"+"\n"+"\n");

		try{
			sbSql.append(" 	SELECT count(*)  ");
			sbSql.append(" FROM arbol_empresa ");
			sbSql.append(" WHERE hijo =  ? "); /**/
			sbSql.append("       AND empresa_raiz =  ? ");
			sbSql.append("       AND padre <> 0 ");
			
			
			
			
			System.out.println("consultarRutaEmpArbolExisIn: " + sbSql);
			
			cantOcurrencias = new Integer(jdbcTemplate.queryForInt(sbSql.toString(), (Object[]) parametros));
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasNietosIn");
		}
		return cantOcurrencias;
	}
	

	public Integer consultarRutaEmpArbolExis(int iIdEmpRaiz, int iIdEmpresaPadre, int iIdEmpresa){
		StringBuffer sbSql = new StringBuffer();

		Integer cantOcurrencias = 0;

		Integer[] parametros = new Integer[3];

		parametros[0] = iIdEmpresa;
		parametros[1] = iIdEmpresaPadre;
		parametros[2] = iIdEmpRaiz;
		
		System.out.println("Angel parametros"+parametros[0]+parametros[1]+parametros[2]+"\n"+"\n"+"\n");

		try{
			sbSql.append(" SELECT count(*)  ");
			sbSql.append(" FROM arbol_empresa_fondeo  ");
			sbSql.append(" WHERE empresa_destino =  ? "); /**/
			sbSql.append("       AND empresa_origen =  ? ");
			sbSql.append("       AND orden <> 0 ");
			sbSql.append("       AND tipo_arbol =  ? ");//
			
			System.out.println("consultarRutaEmpArbolExis: " + sbSql);
			
			cantOcurrencias = new Integer(jdbcTemplate.queryForInt(sbSql.toString(), (Object[]) parametros));
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasNietos");
		}
		return cantOcurrencias;
	}
	/*--- terminan metodos de config arboles ---*/

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.dao.BarridosFondeosDao#consultarBancosRaiz1(int, java.lang.String)
	 */
	@Override
	public List<LlenaComboGralDto> consultarBancosRaiz(int idEmpresa, String idDivisa) {
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listBanc = new ArrayList<LlenaComboGralDto>();
		try
		{
		    sbSql.append("SELECT distinct cb.id_banco, cb.desc_banco ");
		    sbSql.append("FROM cat_banco cb, cat_cta_banco ccb ");
		    sbSql.append("WHERE ccb.id_banco = cb.id_banco ");
		    sbSql.append("AND tipo_chequera in ('P','M') ");
		    sbSql.append("AND id_divisa = '" + Utilerias.validarCadenaSQL(idDivisa) + "' ");
		    sbSql.append("AND ccb.no_empresa  = " + idEmpresa + " ");
		    System.out.println("query"+sbSql.toString());
		    /*---
		      	sbSql.append("AND ccb.no_empresa in (SELECT empresa_origen FROM arbol_empresa_fondeo ");
		     
			    sbSql.append("WHERE tipo_arbol = " + idEmpresa + " ");
			    sbSql.append("AND orden = 0)");
		    ---*/
		    
		    listBanc = jdbcTemplate.query(sbSql.toString(), new RowMapper<LlenaComboGralDto>()
	            {
            		public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
            		{
            			LlenaComboGralDto dtoCons = new LlenaComboGralDto();
            				dtoCons.setId(rs.getInt("id_banco"));
            				dtoCons.setDescripcion(rs.getString("desc_banco"));
            			return dtoCons;
            		}
	            }
            );
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: FondeoAutomatico, C:BarridosFondeosDaoImpl, M:consultarBancosRaiz");
		}
		return listBanc;
	}
	@Override
	public Map<String,Object> obtenerDatosPadre(int noEmpresa, int idBanco,String idChequera, int idArbol) {
		StringBuffer sbSql = new StringBuffer();
		Map<String, Object> resultado;
	
		sbSql.append("\n SELECT top 2 a.empresa_origen no_empresa,b.orden, b.empresa_origen padre ");
		sbSql.append("\n  FROM arbol_empresa_fondeo a, arbol_empresa_fondeo b  ");
		sbSql.append("\n  WHERE ((b.empresa_destino = a.empresa_origen AND b.orden > 0) ");
		sbSql.append("\n  OR (a.empresa_origen = b.empresa_origen  ");
		sbSql.append("\n  AND b.orden = 0))   ");
		sbSql.append("\n  AND b.orden = a.orden - 1  ");
		sbSql.append("\n  AND a.empresa_destino = " + noEmpresa + "");
		sbSql.append("\n  AND a.empresa_origen = (select empresa_origen ");
		sbSql.append("\n								from arbol_empresa_fondeo ");
		sbSql.append("\n								where empresa_destino = " + noEmpresa + ") ");
		sbSql.append("\n AND a.tipo_arbol = 1 ");
		sbSql.append("\n  AND a.tipo_arbol = b.tipo_arbol  ");
		
		/*
		sbSql.append("SELECT top 2 a.empresa_origen no_empresa, b.id_banco, b.id_chequera, b.orden, b.empresa_origen padre ");
		sbSql.append("\n FROM arbol_empresa_fondeo a, arbol_empresa_fondeo b ");
		sbSql.append("\n WHERE ((b.empresa_destino = a.empresa_origen AND b.orden > 0)"); 
		sbSql.append("\n OR (a.empresa_origen = b.empresa_origen ");
		sbSql.append("\n AND b.orden = 0))  ");
		sbSql.append("\n AND b.orden = a.orden - 1 ");
		sbSql.append("\n AND a.empresa_destino = " + noEmpresa + "");
	  //sbSql.append("\n AND a.id_banco = ? ");
		sbSql.append("\n AND a.id_chequera = '" + Utilerias.validarCadenaSQL(idChequera) + "' ");
		sbSql.append("\n AND a.tipo_arbol = " + idArbol + " ");
		sbSql.append("\n AND a.tipo_arbol = b.tipo_arbol "); */
        System.out.println("Query Obtener Datos Padre: " + sbSql.toString());
		
		resultado = this.getJdbcTemplate().queryForMap(sbSql.toString());
		return resultado;
	}
	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.dao.BarridosFondeosDao#consultarFondeoArbol(com.webset.set.barridosfondeos.dto.BusquedaFondeoDto)
	 */
	@Override
	public List<FondeoAutomaticoDto> consultarFondeoArbol(
			BusquedaFondeoDto busquedaFondeoDto) {
		String fechaHoy = new String();
		StringBuffer sbSql = new StringBuffer();
		List<FondeoAutomaticoDto> lista = null;
		final String idDivisa = busquedaFondeoDto.getIdDivisa();
		
		sbSql.append("SELECT convert(char(10),fec_hoy, 103) FROM fechas");
		fechaHoy = this.getJdbcTemplate().queryForObject(sbSql.toString(), String.class);
		
		sbSql.delete(0, sbSql.length());
		sbSql.append("\n SELECT orden, ");
	    sbSql.append("\n      ( SELECT coalesce(sum(m.importe), 0) ");
	    sbSql.append("\n        FROM movimiento m ");
	    sbSql.append("\n        WHERE m.no_empresa = e.no_empresa ");
	    sbSql.append("\n        AND id_tipo_operacion = 3818 ");
	    sbSql.append("\n        AND id_estatus_mov = 'L' ) as Prestamos, ");
	    sbSql.append("\n      e1.no_empresa as no_empresa_origen ,e1.nom_empresa as nom_empresa_origen, ");
	    sbSql.append("\n      e.no_empresa as no_empresa_destino ,e.nom_empresa as nom_empresa_destino, ");
	    sbSql.append("\n      coalesce(cb.desc_banco, '') as desc_banco, ");
	    sbSql.append("\n      coalesce(cb.id_banco, 0) as id_banco, ");
	    sbSql.append("\n      coalesce(ccb.id_chequera, '') as id_chequera, ");
	    sbSql.append("\n      coalesce(ccb.saldo_final, 0) as saldo_chequera, ");
	    sbSql.append("\n      coalesce(ccb.saldo_minimo,0) as saldo_minimo_chequera, ");
		sbSql.append("\n      ( SELECT coalesce(sum(importe), 0) ");
		sbSql.append("\n        FROM movimiento m ");
		sbSql.append("\n        WHERE e.no_empresa = m.no_empresa ");
		sbSql.append("\n        AND m.id_tipo_movto = 'I' ");
		sbSql.append("\n        AND m.id_tipo_operacion in (3806, 3706, 3808, 3708,3814,3714) ");
	    sbSql.append("\n        AND m.fec_valor = convert(datetime,'" + fechaHoy +"', 103) ");
	    sbSql.append("\n        AND m.no_factura = '1' ");
	    //sbSql.append("\n        AND m.fec_valor < convert(datetime,'" + fechaHoy +"', 103) + 1 ");
	    if (busquedaFondeoDto.getIdBanco() != 0 && busquedaFondeoDto.isChkMismoBanco()){
	    	sbSql.append("       AND m.id_banco = " + busquedaFondeoDto.getIdBanco() + " ");
	    }
	    if (!busquedaFondeoDto.isBTodaslasChequeras())
	    	sbSql.append("\n        AND m.id_chequera = ccb.id_chequera ");
	    sbSql.append("\n        AND m.id_estatus_mov in ('L','A')) as importeF, ");
	    sbSql.append("\n      CASE WHEN '" + busquedaFondeoDto.getIdDivisa() + "' = 'MN' ");
	    sbSql.append("\n           THEN 1 ELSE ");
	    sbSql.append("\n               ( SELECT coalesce(valor,0) ");
	    sbSql.append("\n                 FROM valor_divisa ");
	    sbSql.append("\n                 WHERE fec_divisa = ");
	    sbSql.append("\n                    ( SELECT max(fec_divisa) ");
	    sbSql.append("\n                       FROM valor_divisa ");
	    sbSql.append("\n                       WHERE id_divisa = '" + busquedaFondeoDto.getIdDivisa() + "') ");
	    sbSql.append("\n                 AND id_divisa = '" + busquedaFondeoDto.getIdDivisa() + "' ) ");
	    sbSql.append("\n      END as TipoCambio, ");
	    sbSql.append("\n      ( SELECT coalesce(sum(m.importe),0) ");
	    sbSql.append("\n        FROM movimiento m ");
	    /*sbSql.append("   	 WHERE ( ( m.id_tipo_operacion = 3000 ");
        sbSql.append("       		 AND m.id_estatus_mov in ('N','C') ");
        sbSql.append("       		 AND m.fec_propuesta >= to_date('" + fechaHoy +"', 'dd/mm/yyyy') ");
	    sbSql.append("       		 AND m.fec_propuesta < to_date('" + fechaHoy +"', 'dd/mm/yyyy') + 1 ");
        sbSql.append("       		 AND m.cve_control is not null ");
        sbSql.append("       		 AND ccb.id_chequera = ");
        sbSql.append("             			CASE WHEN substr(m.cve_control, 1,1) = 'M' ");
        sbSql.append("                  		THEN m.id_chequera ");
        sbSql.append("                  		ELSE ");
        sbSql.append("                    			( SELECT coalesce(ccbv.id_chequera, '') ");
        sbSql.append("                      		  FROM cat_cta_banco ccbv ");
        sbSql.append("                      		  WHERE ccbv.no_empresa = m.no_empresa ");
        sbSql.append("                          	  AND ccbv.id_divisa = m.id_divisa ");
        sbSql.append("                          	  AND ( CASE WHEN m.id_forma_pago = 1 ");
        sbSql.append("                                      THEN ccbv.b_cheque ");
        sbSql.append("                                      WHEN m.id_forma_pago = 3 ");
        sbSql.append("                                      THEN ccbv.b_transferencia ");
        sbSql.append("                                      WHEN m.id_forma_pago = 9 ");
        sbSql.append("                                      THEN ccbv.b_cheque_ocurre ");
        sbSql.append("                                END ) = 'S' )");
        sbSql.append("                      END ");
        sbSql.append("               ) ");
        sbSql.append("               OR ( m.id_tipo_operacion = 3200 ");
        */
	    sbSql.append("\n    	 WHERE ( ( m.id_tipo_operacion = 3200 ");
        sbSql.append("\n        			  AND m.fec_valor >= convert(datetime,'" + fechaHoy +"', 103)) ");
        sbSql.append("\n        			  OR (m.id_tipo_operacion = 3000 ");
        sbSql.append("\n          			  AND m.id_estatus_mov in('I','R','V') ");
        sbSql.append("\n          			  AND m.no_folio_det in(select cch.no_folio_det ");
	    //sbSql.append("\n        			  AND m.fec_valor < convert(datetime,'" + fechaHoy +"', 103) + 1 ");
        sbSql.append("\n      				  from  control_fondeo_cheques cch where cch.id_chequera = m.id_chequera )) ");
        //sbSql.append("\n        			  AND m.fec_valor < convert(datetime,'" + fechaHoy +"', 103) + 1 ");
        sbSql.append("\n 					  or (m.id_tipo_operacion between 3701 and 3799 and   m.fec_valor >= convert(datetime,'" + fechaHoy +"', 103)))");
        sbSql.append("\n                      AND m.id_tipo_movto = 'E'");
        
        
        //'DESC
        sbSql.append("\n          			  AND m.id_estatus_mov not in ('A','X','Y','Z','W','G') ");
        sbSql.append("\n 					  and coalesce(m.id_contable,'') NOT in( select cd.clase_docto from arbol_clase_docto cd where  cd.tipo_busqueda = 'D' and cd.tipo_arbol = 1)");
        //'DESC
        
        sbSql.append("\n          			  AND m.id_chequera = ccb.id_chequera ) as pm, ");
        sbSql.append("\n          			  ( SELECT sum(m.importe) FROM movimiento m WHERE m.id_tipo_operacion = 3000 ");
        sbSql.append("\n 					  AND m.id_forma_pago = 1 ");
        sbSql.append("\n 					  AND m.no_folio_det not in(select cch.no_folio_det ");
        sbSql.append("\n 					  from  control_fondeo_cheques cch where cch.id_chequera = m.id_chequera )");
        sbSql.append("\n 					  and m.id_tipo_movto = 'E'");
        sbSql.append("\n 					  and m.id_estatus_mov  IN('I','R','V')");
        sbSql.append("\n 					  and coalesce(m.id_contable,'') NOT in( select cd.clase_docto from arbol_clase_docto cd where  cd.tipo_busqueda = 'I' and cd.tipo_arbol = 1)");
        sbSql.append("\n 					  and m.id_chequera = ccb.id_chequera  ) as pmCheques, coalesce(s.importe, 0) saldoCoinversion,e.id_caja");
        
        //sbSql.append("\n            	 OR ( m.id_tipo_operacion between 3700 and 3799 ");
		//sbSql.append("\n 					  AND id_tipo_operacion not in (3705, 3709) ");
        //sbSql.append("\n        			  AND m.fec_valor >= convert(datetime,'" + fechaHoy +"', 103) ");
	    //sbSql.append("\n        			  AND m.fec_valor < convert(datetime,'" + fechaHoy +"', 103) + 1 ");
        //'DESC
        //sbSql.append("\n                     AND m.id_estatus_mov = 'A' ");
        //'DESC
        //sbSql.append("\n           		  AND m.id_chequera = ccb.id_chequera ) ");
        //sbSql.append("\n                OR ( m.id_tipo_operacion between 3800 and 3899 ");
        //sbSql.append("\n                     AND m.id_tipo_operacion not in (3818, 3805, 3809) ");
        //'DESC
        //sbSql.append("\n        			  AND m.fec_valor >= convert(datetime,'" + fechaHoy +"', 103) ");
	    //sbSql.append("\n        			  AND m.fec_valor < convert(datetime,'" + fechaHoy +"', 103) + 1 ");
        //sbSql.append("\n                     AND m.id_estatus_mov = 'L' ");
        //sbSql.append("\n                     AND m.id_chequera = ccb.id_chequera ) ) ");
        //sbSql.append("\n        AND m.id_tipo_movto = 'E' ");
        //sbSql.append("\n        AND m.no_empresa = e.no_empresa ) as pm, ");
        //nuevas columnas para que tome ingresos y barridos
        //sbSql.append("\n      ( SELECT coalesce(sum(m.importe), 0) ");
        //sbSql.append("\n        FROM movimiento m ");
        //sbSql.append("\n        WHERE m.no_empresa = e.no_empresa ");
        //sbSql.append("\n        AND id_tipo_operacion in (3701,3101,3700,3120) ");
        //sbSql.append("\n        AND m.id_banco=cb.id_banco");
        //sbSql.append("\n        AND m.id_chequera=ccb.id_chequera ");
        //sbSql.append("\n        AND m.id_estatus_mov = 'A'");
        //sbSql.append("\n        AND m.fec_valor >= convert(datetime,'" + fechaHoy +"', 103) ");
	    //sbSql.append("\n        AND m.fec_valor < convert(datetime,'" + fechaHoy +"', 103) + 1 ");
        //sbSql.append("\n        AND id_tipo_movto = 'I' ) as Ingresos, ");
        //sbSql.append("\n      ( SELECT coalesce(sum(m.importe), 0) ");
        //sbSql.append("\n        FROM movimiento m ");
        //sbSql.append("\n        WHERE e.no_empresa = m.no_empresa ");
        //sbSql.append("\n        AND m.id_tipo_operacion in (3805, 3809,3705,3709)  ");
        //sbSql.append("\n        AND m.id_banco=cb.id_banco");
        //sbSql.append("\n        AND m.id_chequera=ccb.id_chequera ");
        //sbSql.append("\n        AND m.id_estatus_mov in('L','A')");
        //sbSql.append("\n        AND m.fec_valor >= convert(datetime,'" + fechaHoy +"', 103) ");
	    //sbSql.append("\n        AND m.fec_valor < convert(datetime,'" + fechaHoy +"', 103) + 1 ");
        //sbSql.append("\n        AND id_tipo_movto = 'E' ) as importeB ");
//        sbSql.append("\n FROM empresa e, empresa e1, arbol_empresa_fondeo c, cat_cta_banco ccb, cat_banco cb ");
	    sbSql.append("\n FROM empresa e ");
	    sbSql.append("\n join cat_cta_banco ccb on ccb.no_empresa = e.no_empresa ");
	    sbSql.append("\n join cat_banco cb on cb.id_banco = ccb.id_banco  ");
	    sbSql.append("\n join arbol_empresa_fondeo c on c.empresa_destino = e.no_empresa ");
	    sbSql.append("\n join empresa e1 on c.empresa_origen = e1.no_empresa ");
	    sbSql.append("\n join cat_divisa d on ccb.id_divisa = d.id_divisa");
	    sbSql.append("\n left join saldo s on e1.no_empresa = s.no_empresa and s.no_cuenta = e.no_empresa and s.id_tipo_saldo = 91 and d.id_divisa_soin = s.no_linea ");
        
        sbSql.append("\n WHERE c.tipo_arbol = 1 ");
        sbSql.append("\n AND c.tipo_valor = 'NECESIDAD' ");
//        sbSql.append("\n AND ccb.no_empresa = e.no_empresa ");
        sbSql.append("\n AND ccb.tipo_chequera in ('P','M') ");
        
   /*     if (!busquedaFondeoDto.isBTodaslasChequeras()){
        	sbSql.append("AND ccb.b_traspaso = 'S' ");
        } */

        sbSql.append("\n AND ccb.id_divisa = '" + busquedaFondeoDto.getIdDivisa() + "' ");

        //' se comento esta parte para que no tome encuenta el banco para la busqueda
        if (busquedaFondeoDto.getIdBanco() != 0 && busquedaFondeoDto.isChkMismoBanco()){
        	sbSql.append("\n AND ccb.id_banco = " + busquedaFondeoDto.getIdBanco() + " ");
        }
        
        if (busquedaFondeoDto.getIdEmpresa() != 0)
        	sbSql.append("\n AND e.no_empresa = " + busquedaFondeoDto.getIdEmpresa() + " ");

//	    sbSql.append("\n AND cb.id_banco = ccb.id_banco ");
	    sbSql.append("\n AND e.no_empresa in ");
	    sbSql.append("\n          ( SELECT no_empresa ");
	    sbSql.append("\n            FROM usuario_empresa ");
	    sbSql.append("\n            WHERE no_usuario = " + busquedaFondeoDto.getIdUsuario() + " ) ");
//	    sbSql.append("\n AND c.empresa_destino = e.no_empresa ");
//	    sbSql.append("\n AND c.empresa_origen = e1.no_empresa ");
	    sbSql.append("\n AND c.orden > 0 ");
	    /*sbSql.append("\n AND (SELECT sum (m.importe) ");
	    sbSql.append("\n        FROM movimiento m ");
	    sbSql.append("\n    	 WHERE ( ( m.id_tipo_operacion = 3200 ");
        //'DESC
        sbSql.append("\n          			  AND m.id_estatus_mov not in ('A','X','Y','Z','W','G') ");
        //'DESC
        sbSql.append("\n          			  AND m.id_chequera = ccb.id_chequera ) ");
        sbSql.append("\n            	 OR ( m.id_tipo_operacion between 3700 and 3799 ");
		sbSql.append("\n 					  AND id_tipo_operacion not in (3705, 3709) ");
        //'DESC
        sbSql.append("\n                     AND m.id_estatus_mov = 'A' ");
        //'DESC
        sbSql.append("\n           		  AND m.id_chequera = ccb.id_chequera ) ");
        sbSql.append("\n                OR ( m.id_tipo_operacion between 3800 and 3899 ");
        sbSql.append("\n                     AND m.id_tipo_operacion not in (3818, 3805, 3809) ");
        sbSql.append("\n                     AND m.id_estatus_mov = 'L' ");
        sbSql.append("\n                     AND m.id_chequera = ccb.id_chequera ) ) ");
        sbSql.append("\n        AND m.id_tipo_movto = 'E' ");
        sbSql.append("\n        AND m.no_empresa = e.no_empresa ) > 0 ");*/
	    sbSql.append("\n GROUP BY orden,e.no_empresa, e.nom_empresa, e1.no_empresa,e1.nom_empresa,cb.id_banco, ");
	    sbSql.append("\n 		   cb.desc_banco, ccb.id_chequera, ccb.saldo_final, ccb.saldo_minimo, s.importe,e.id_caja ");
	    sbSql.append("\n ORDER BY e.no_empresa ");
	    System.out.println("Query consultarFondeoArbol: "+sbSql.toString());
	    lista = jdbcTemplate.query(sbSql.toString(), new RowMapper<FondeoAutomaticoDto>()
	            {
            		public FondeoAutomaticoDto mapRow(ResultSet rs, int idx)throws SQLException
            		{
            			FondeoAutomaticoDto dtoCons = new FondeoAutomaticoDto();
	            			dtoCons.setOrden(rs.getInt("orden"));
	            			dtoCons.setPrestamos(rs.getDouble("Prestamos"));
	            			dtoCons.setNoEmpresaOrigen(rs.getInt("no_empresa_origen"));
	            			dtoCons.setNomEmpresaOrigen(rs.getString("nom_empresa_origen"));
	            			dtoCons.setNoEmpresaDestino(rs.getInt("no_empresa_destino"));
	            			dtoCons.setNomEmpresaDestino(rs.getString("nom_empresa_destino"));
	            			dtoCons.setDescBanco(rs.getString("desc_banco"));
	            			dtoCons.setIdBanco(rs.getInt("id_banco"));
	            			dtoCons.setIdChequera(rs.getString("id_chequera"));
	            			dtoCons.setSaldoChequera(rs.getDouble("saldo_chequera"));
	            			dtoCons.setSaldoMinimoChequera(rs.getDouble("saldo_minimo_chequera"));
	            			dtoCons.setImporteF(rs.getDouble("importeF"));
	            			dtoCons.setTipoCambio(rs.getDouble("TipoCambio"));
	            			dtoCons.setPm(rs.getDouble("pm"));
	            			dtoCons.setPmCheques(rs.getDouble("pmCheques"));
	            			dtoCons.setSaldoCoinversion(rs.getDouble("saldoCoinversion"));
	            			//dtoCons.setIngresos(rs.getDouble("Ingresos"));
	            			//dtoCons.setImporteB(rs.getDouble("importeB"));
	            			dtoCons.setIdDivisa(idDivisa);
	            			dtoCons.setIdCaja(rs.getInt("id_caja"));
            			return dtoCons;
            		}
	            }
            );
	    System.out.println("datos retornados "+lista.size());
		return lista;
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.dao.BarridosFondeosDao#consultarChequera(int, int, java.lang.String)
	 */
	@Override
	public String consultarChequera(int noEmpresa, int idBanco, String idDivisa) {
		String valor = "";
		StringBuffer sbSql = new StringBuffer();
		System.out.println(noEmpresa+" "+idBanco+" "+idDivisa);
		if (idBanco > 0){
			bitacora.insertarRegistro("noEmpresa: " + noEmpresa);
			bitacora.insertarRegistro("Banco: " + idBanco);
			bitacora.insertarRegistro("divisa" + idDivisa);
			
			 sbSql.append("\n SELECT id_chequera "); 
			 sbSql.append("\n FROM cat_cta_banco ");
			 sbSql.append("\n WHERE  no_empresa = ? ");
			 sbSql.append("\n AND id_banco = ? ");
			 sbSql.append("\n AND b_traspaso in ('S') ");
			 sbSql.append("\n AND id_divisa = ? ");
			 sbSql.append("\n ORDER BY id_chequera ");
		/*	
			sbSql.append("SELECT TOP 1 id_chequera ");
			sbSql.append("FROM cat_cta_banco ");
			sbSql.append("WHERE no_empresa = ? ");
			sbSql.append("AND id_banco = ? ");
		//	sbSql.append("AND mismo_banco = 'S' ");
			sbSql.append("AND id_divisa = ? ");
			sbSql.append(" ORDER BY id_chequera ");
			//sbSql.append("AND rownum < 2 "); */
			System.out.println("Query Chequeras: " + sbSql.toString());
			valor = (String) this.getJdbcTemplate().queryForObject(sbSql.toString(), new Object[] {new Integer(noEmpresa), new Integer(idBanco), Utilerias.validarCadenaSQL(idDivisa)}, String.class);
			return valor;
		
		}
		
		sbSql.append("\n SELECT a.id_chequera");
		sbSql.append("\n FROM cat_cta_banco a, cat_banco b ");
		sbSql.append("\n WHERE a.no_empresa = ? ");
		sbSql.append("\n AND a.id_banco = b.id_banco ");
		sbSql.append("\n AND A.b_traspaso in ('S')");
		sbSql.append("\n AND id_divisa = ? ");
		sbSql.append("\n ORDER BY a.id_chequera ");
		
		
		System.out.println("Query Chequeras: "+sbSql.toString());
		valor = (String) this.getJdbcTemplate().queryForObject(sbSql.toString(), new Object[] {new Integer(noEmpresa), Utilerias.validarCadenaSQL(idDivisa)}, String.class);
		return valor;
		
		
	}
	
	@Override
	public int consultarBanco(int noEmpresa, int idBanco, String idDivisa) {
		String valor = "";
		StringBuffer sbSql = new StringBuffer();
		
			bitacora.insertarRegistro("noEmpresa: " + noEmpresa);
			bitacora.insertarRegistro("Banco: " + idBanco);
			bitacora.insertarRegistro("divisa" + idDivisa);
			
			sbSql.append("\n SELECT a.id_banco");
			sbSql.append("\n FROM cat_cta_banco a, cat_banco b ");
			sbSql.append("\n WHERE a.no_empresa = ? ");
			sbSql.append("\n AND a.id_banco = b.id_banco ");
			sbSql.append("\n AND A.b_traspaso in ('S')");
			sbSql.append("\n AND id_divisa = ? ");
			sbSql.append("\n ORDER BY a.id_chequera ");
			
			System.out.println("Query Bancos: " + sbSql.toString());
			valor = (String) this.getJdbcTemplate().queryForObject(sbSql.toString(), new Object[] {new Integer(noEmpresa), Utilerias.validarCadenaSQL(idDivisa)}, String.class);
			return Integer.parseInt(valor);
	
		
	}

	public List<LlenaComboGralDto> consultarChequeraFondeo(int iIdEmpresa, String sIdDivisa, int iIdBanco){
		List<LlenaComboGralDto> listDatos = new ArrayList<LlenaComboGralDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("SELECT id_chequera as ID, id_chequera as DESCRIPCION ");
			sql.append("\n	FROM cat_cta_banco ");
			sql.append("\n	WHERE id_banco = " + iIdBanco);
			sql.append("\n	and no_empresa =  " + iIdEmpresa);
			sql.append("\n	and tipo_chequera in ('C', 'M', 'P') ");
			sql.append("\n	and id_divisa = '" + sIdDivisa + "'");
			
			listDatos = jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException{
					LlenaComboGralDto cons = new LlenaComboGralDto();
						cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarChequeraFondeo");
		}
		return listDatos;
	}
	
	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.dao.BarridosFondeosDao#consultarChequeraRaiz(int, int, java.lang.String)
	 */
	@Override
	public List<LlenaComboGralDto> consultarChequeraRaiz(int idEmpresa, int idBanco, String idDivisa) {
		StringBuffer sql = new StringBuffer();
		List<LlenaComboGralDto> listaDatos = null;
		
		sql.append("SELECT id_chequera as ID, id_chequera as DESCRIPCION ");
		sql.append("FROM cat_cta_banco ");
		sql.append("WHERE id_banco = " + Utilerias.validarCadenaSQL(idBanco));
		sql.append(" and no_empresa =  " + Utilerias.validarCadenaSQL(idEmpresa));
		sql.append(" and tipo_chequera in ('C', 'M', 'P') ");
		sql.append(" and id_divisa = '" + Utilerias.validarCadenaSQL(idDivisa) + "'");
		System.out.println("query"+sql.toString());
		listaDatos = jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>(){
			public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException{
				LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setDescripcion(rs.getString("DESCRIPCION"));
				return cons;
			}
		});
		
		return listaDatos;
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.dao.BarridosFondeosDao#consultarEmpresaArbolFondeo(int)
	 */
	@Override
	public List<LlenaComboGralDto> consultarEmpresaArbolFondeo(int idEmpresaRaiz) {
		StringBuffer sql = new StringBuffer();
		List<LlenaComboGralDto> listaDatos = null;
		
		sql.append("SELECT b.no_empresa as ID, b.nom_empresa as DESCRIPCION ");
		sql.append("FROM arbol_empresa_fondeo a, empresa b ");
		sql.append("WHERE a.empresa_destino = b.no_empresa ");
		sql.append("AND a.tipo_valor = 'NECESIDAD' ");
		sql.append("AND tipo_arbol = " + Utilerias.validarCadenaSQL(idEmpresaRaiz));
		System.out.println("query"+sql.toString());
		listaDatos = jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>(){
			public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException{
				LlenaComboGralDto cons = new LlenaComboGralDto();
				cons.setId(rs.getInt("ID"));
				cons.setDescripcion(rs.getString("DESCRIPCION"));
				return cons;
			}
		});
		
		return listaDatos;
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.dao.BarridosFondeosDao#insertarTmpTraspaso(com.webset.set.barridosfondeos.dto.TmpTraspasoFondeoDto)
	 */
	@Override
	public void insertarTmpTraspaso(TmpTraspasoFondeoDto tmpTraspasoFondeo) throws Exception {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("INSERT INTO tmp_traspaso_fondeo (empresa_origen,empresa_destino, ");
		sbSql.append("importe_traspaso, tipo_arbol,orden, id_banco,id_chequera,id_chequera_padre, ");
		sbSql.append("no_usuario, tipo_operacion, concepto, no_fondeo) ");
		sbSql.append(" VALUES (");
		sbSql.append(tmpTraspasoFondeo.getIdEmpresaOrigen());
		sbSql.append("," + tmpTraspasoFondeo.getIdEmpresaDestino());
		sbSql.append("," + tmpTraspasoFondeo.getImporteTraspaso());
		sbSql.append("," + tmpTraspasoFondeo.getTipoArbol());
		sbSql.append("," + tmpTraspasoFondeo.getNivel());
		sbSql.append("," + tmpTraspasoFondeo.getIdBanco());
		sbSql.append(",'" + tmpTraspasoFondeo.getIdChequeraHijo());
		sbSql.append("','" + tmpTraspasoFondeo.getIdChequeraPadre());
		sbSql.append("'," + tmpTraspasoFondeo.getIdUsuario());
		sbSql.append("," + tmpTraspasoFondeo.getTipoOperacion());
		sbSql.append(",'" + tmpTraspasoFondeo.getConcepto() + "'");
		sbSql.append("," + tmpTraspasoFondeo.getNoFondeo());
		sbSql.append(")");
		System.out.println("query"+sbSql.toString());
		try{
			this.getJdbcTemplate().execute(sbSql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BarridosFondeos, C:BarridosFondeosDao, M:insertarTmpTraspaso");
			throw new Exception ("Error al insertar tmp_traspaso_fondeo");
		}
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.dao.BarridosFondeosDao#consultarTmpArbolFondeo()
	 */
	@Override
	public List<Map<String, Object>> consultarTmpArbolFondeo() {
		List<Map<String, Object>> lista = null;
		StringBuffer sbSql = new StringBuffer();
		
		sbSql.append("SELECT sum(importe_traspaso) as col0, ");
		sbSql.append("empresa_origen as col1, empresa_destino as col2, tipo_arbol as col3, orden as col4, ");
		sbSql.append("e.nom_empresa  as col5 ,  e1.nom_empresa  as col6 ,tf.id_banco as col7, tf.id_chequera as col8, ");
		sbSql.append("tf.tipo_operacion as col9, tf.concepto as col10, MAX(tf.no_fondeo) as col11 ");
		sbSql.append("FROM tmp_traspaso_fondeo tf, empresa e, empresa e1 ");
		sbSql.append("WHERE empresa_origen = e.no_empresa ");
		sbSql.append("AND empresa_destino = e1.no_empresa ");
		sbSql.append("GROUP BY empresa_origen,empresa_destino, tipo_arbol, orden, e.nom_empresa, e1.nom_empresa, tf.id_banco, ");
		sbSql.append("tf.id_chequera,tf.tipo_operacion, tf.concepto ");
		sbSql.append("ORDER BY orden");
		System.out.println("query  consultarTmpArbolFondeo"+sbSql.toString());
		lista = jdbcTemplate.queryForList(sbSql.toString());
		
		return lista;
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.dao.BarridosFondeosDao#obtenerFolioReal(java.lang.String)
	 */
	@Override
	public Long obtenerFolioReal(String tipo) {
		StringBuffer sbSql = new StringBuffer();
		Long lFolio = new Long(0);
		
		sbSql.append("SELECT coalesce(num_folio,0) + 1 ");
		sbSql.append("FROM folio ");
		sbSql.append("WHERE tipo_folio = ?");
		System.out.println("query"+sbSql.toString());
		lFolio = this.getJdbcTemplate().queryForLong(sbSql.toString(), new Object[] {Utilerias.validarCadenaSQL(tipo)});
		
		sbSql.delete(0, sbSql.length());
		sbSql.append("UPDATE folio ");
		sbSql.append("SET num_folio = " + lFolio.toString());
		sbSql.append(" WHERE tipo_folio = '" + Utilerias.validarCadenaSQL(tipo) + "'");
		System.out.println("query"+sbSql.toString());
		this.getJdbcTemplate().execute(sbSql.toString());
		
		return lFolio;
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.dao.BarridosFondeosDao#consultarDatosChequera(int, java.lang.String)
	 */
	@Override
	public Map<String, Object> consultarDatosChequera(int noEmpresa, String idDivisa) {
		StringBuffer sbSql = new StringBuffer();
		Map<String, Object> resultado;
	
		sbSql.append("SELECT top 2 a.id_banco as id_banco, a.id_chequera as id_chequera ");
		sbSql.append("FROM cat_cta_banco a, cat_banco b ");
		sbSql.append("WHERE a.no_empresa = ? ");
		sbSql.append("AND a.id_banco = b.id_banco ");
		sbSql.append("AND a.mismo_banco = 'S' ");
		sbSql.append("AND id_divisa = ? ");
		sbSql.append("");
		System.out.println("query"+sbSql.toString());
		resultado = this.getJdbcTemplate().queryForMap(sbSql.toString(), new Object[] {noEmpresa, Utilerias.validarCadenaSQL(idDivisa)}, String.class);
		return resultado;
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.dao.BarridosFondeosDao#insertarParametro(com.webset.set.barridosfondeos.dto.ParametroDto)
	 */
	@Override
	public void insertarParametro(ParametroDto parametroDto) throws Exception {
		Date fechaHoy;
		String lsFechaHoy;
		StringBuffer sbSql = new StringBuffer();
		System.out.println("entro a insertar parametro");
		sbSql.append("SELECT fec_hoy FROM fechas");
		fechaHoy = this.getJdbcTemplate().queryForObject(sbSql.toString(), Date.class);
		
		parametroDto.setFecValor(fechaHoy);
		
		sbSql.delete(0, sbSql.length());
		
		sbSql.append("SELECT convert(char(10),fec_hoy, 103) FROM fechas");
		lsFechaHoy = this.getJdbcTemplate().queryForObject(sbSql.toString(), String.class);
		
		parametroDto.setFecValor(fechaHoy);
		
		sbSql.delete(0, sbSql.length());
		
		sbSql.append("INSERT INTO parametro (  no_empresa,no_folio_param,aplica,secuencia,id_tipo_operacion,");
		sbSql.append(" no_cuenta,id_estatus_mov,id_chequera,id_tipo_saldo,id_banco,importe,");
		sbSql.append(" b_salvo_buen_cobro,fec_valor,fec_valor_original,id_estatus_reg,usuario_alta,");
		sbSql.append(" fec_alta,id_divisa,no_factura,id_forma_pago,id_divisa_original,importe_original, ");
		sbSql.append(" fec_operacion,id_banco_benef, id_chequera_benef,origen_mov,concepto, ");
		sbSql.append(" id_caja,no_folio_mov,folio_ref,id_grupo,grupo,no_cliente,");
		sbSql.append(" beneficiario, no_docto) VALUES ( ");
		sbSql.append(parametroDto.getNoEmpresa() + ", ");
		sbSql.append(parametroDto.getNoFolioParam() + ", ");
		sbSql.append(parametroDto.getAplica() + ", ");
		sbSql.append(parametroDto.getSecuencia() + ", ");
		sbSql.append(parametroDto.getIdTipoOperacion() + ", ");
		sbSql.append(parametroDto.getCuenta() + ", '");
		sbSql.append(parametroDto.getIdEstatusMov() + "', '");
		sbSql.append(parametroDto.getIdChequera() + "', ");
		sbSql.append(parametroDto.getTipo_saldo() + ",");
		sbSql.append(parametroDto.getIdBanco() + ",");
		sbSql.append(parametroDto.getImporte() + ", '");
		sbSql.append(parametroDto.getbSBC() + "', convert(datetime,'");
		sbSql.append(lsFechaHoy + "', 103), convert(datetime,'");
		sbSql.append(lsFechaHoy + "', 103), '");
		sbSql.append(parametroDto.getIdEstatusReg() + "', ");
		sbSql.append(parametroDto.getNoUsuario() + ", convert(datetime,'");
		sbSql.append(lsFechaHoy + "', 103), '");
		sbSql.append(parametroDto.getIdDivisa() + "', '1', ");
		sbSql.append(parametroDto.getIdFormaPago() + ", '");
		sbSql.append(parametroDto.getIdDivisa() + "', ");
		sbSql.append(parametroDto.getImporte() + ", convert(datetime,'");
		sbSql.append(lsFechaHoy + "', 103), ");
		sbSql.append(parametroDto.getIdBancoBenef() + ", '");
		sbSql.append(parametroDto.getIdChequeraBenef() + "', '");
		sbSql.append(parametroDto.getOrigenMov() + "', '");
		sbSql.append(parametroDto.getConcepto() + "', ");
		sbSql.append(parametroDto.getIdCaja() + ", ");
		sbSql.append(parametroDto.getNoFolioMov() + ", ");
		sbSql.append(parametroDto.getFolioRef() + ", ");
		sbSql.append(parametroDto.getIdGrupo() + ", ");
		sbSql.append(parametroDto.getIdGrupo() + ", ");
		sbSql.append(parametroDto.getNoCliente() + ", '");
		sbSql.append(parametroDto.getBeneficiario() + "', '");
		sbSql.append(parametroDto.getNoDocto() + "')");
		System.out.println("query insertar parametro: "+sbSql.toString());
		try{
			this.getJdbcTemplate().execute(sbSql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BarridosFondeos, C:BarridosFondeosDao, M:insertarParametro");
			throw new Exception ("Error al insertar parametro");
		}
		
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.dao.BarridosFondeosDao#buscarCuentaEmpresa(int)
	 */
	@Override
	public int buscarCuentaEmpresa(int noEmpresa) {
		StringBuffer sbSql = new StringBuffer();
		int resultado;
		
		sbSql.append("SELECT no_cuenta ");
		sbSql.append("FROM cuenta ");
		sbSql.append("WHERE no_empresa = ? ");
		sbSql.append("AND id_tipo_cuenta='P' ");
		System.out.println("query"+sbSql.toString());
		resultado = this.getJdbcTemplate().queryForInt(sbSql.toString(), new Object[] {Utilerias.validarCadenaSQL(noEmpresa)});
		return resultado;
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.dao.BarridosFondeosDao#ejecutarGenerador(com.webset.set.utilerias.dto.GeneradorDto)
	 */
	@Override
	public Map<String, Object> ejecutarGenerador(GeneradorDto generadorDto) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.ejecutarGenerador(generadorDto);
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.dao.BarridosFondeosDao#borrarTmpTraspasoFondeo()
	 */
	@Override
	public void borrarTmpTraspasoFondeo() throws Exception {
		StringBuffer sbSql = new StringBuffer();
		
		sbSql.append("DELETE tmp_traspaso_fondeo");
		System.out.println("query"+sbSql.toString());
		try{
			this.getJdbcTemplate().execute(sbSql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BarridosFondeos, C:BarridosFondeosDao, M:borrarTmpTraspasoFondeo");
			throw new Exception ("Error al eliminar TMP_TRASPASO_FONDEO");
		}
		
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.dao.BarridosFondeosDao#consultarPagos(com.webset.set.barridosfondeos.dto.BusquedaFondeoDto)
	 */
	@Override
	public List<MovimientoDto> consultarPagos(BusquedaFondeoDto dtoBus) {
		StringBuffer sql = new StringBuffer();
		List<MovimientoDto> listConsPag = new ArrayList<MovimientoDto>();
		try{
			sql.append("SELECT convert(char(10),fec_hoy, 103) FROM fechas");
			String fechaHoy = this.getJdbcTemplate().queryForObject(sql.toString(), String.class);
			
			sql.delete(0, sql.length());
			
		    sql.append(" SELECT m.cve_control, sum(m.importe) as importe, ");
    	    sql.append(" coalesce((select sag.cupo_total from seleccion_automatica_grupo sag where sag.cve_control = m.cve_control ");
    	    sql.append(" ), 0)as importe_original");
    	    sql.append(" FROM movimiento m, empresa e, empresa e1, arbol_empresa_fondeo c, cat_cta_banco ccb, cat_banco cb, cat_forma_pago cfp");
    	    sql.append(" WHERE ((  m.id_tipo_operacion = 3200 ");
    	    sql.append("         AND m.fec_valor >= convert(datetime,'" + fechaHoy + "', 103) ");
    	    sql.append("         AND m.fec_valor < convert(datetime,'" + fechaHoy + "', 103) + 1 )");
    	    sql.append(" OR (  m.id_tipo_operacion = 3000 ");
    	    sql.append("       and  m.fec_propuesta >= convert(datetime,'" + fechaHoy + "',103) ");
    	    sql.append("       and  m.fec_propuesta < convert(datetime,'" + fechaHoy + "',103) + 1 ");
    	    sql.append("and id_estatus_mov in ('C','N') ) ");
    	    sql.append(" or ( m.id_tipo_operacion = 3000");
    	    if(globalSingleton.obtenerValorConfiguraSet(3003).equals("SI"))
    	    	sql.append(" and m.id_estatus_mov in('I','R','V')");
    	    sql.append(" AND m.no_folio_det in(select cch.no_folio_det");
    	    sql.append(" from  control_fondeo_cheques cch where cch.id_chequera = m.id_chequera ))");
    	    sql.append(" or (m.id_tipo_operacion = 3701 ");
    	    sql.append("     AND m.fec_valor >= convert(datetime,'" + fechaHoy + "', 103) ");
    	    sql.append("     AND m.fec_valor < convert(datetime,'" + fechaHoy + "', 103) + 1 ");
    	    sql.append(" and (origen_mov is null or  origen_mov <> 'SET') ))");
    	    sql.append(" and m.id_tipo_movto = 'E'");
    	    sql.append(" and m.id_estatus_mov not in ('X','Y','Z','W','G')");
    	    sql.append(" and m.no_empresa = e.no_empresa");
    	    sql.append(" and coalesce(m.id_contable,'') NOT in( select cd.clase_docto from arbol_clase_docto cd where  ");
    	    
    	    if(dtoBus.getSTipoBusqueda() != null  && dtoBus.getSTipoBusqueda().equals("I"))
    	        sql.append(" cd.tipo_busqueda = 'I' and cd.tipo_arbol = " + dtoBus.getIdEmpresaRaiz() + ")");
    	    else
    	        sql.append(" cd.tipo_busqueda = 'D' and cd.tipo_arbol = " + dtoBus.getIdEmpresaRaiz() + ")");
    	    
    	    
    	    sql.append(" and m.id_chequera = ccb.id_chequera");
    	    sql.append(" and c.tipo_arbol = " + dtoBus.getIdEmpresaRaiz() + "");
    	    sql.append(" and c.tipo_valor = 'NECESIDAD'");
    	     
    	    sql.append(" and ccb.tipo_chequera in ('P','M')");
    	    sql.append(" and ccb.id_divisa = '" + dtoBus.getIdDivisa() + "'");
    	    sql.append(" and cb.id_banco = ccb.id_banco");
    	    sql.append(" and e.no_empresa in");
    	    sql.append(" ( SELECT no_empresa From usuario_empresa WHERE no_usuario = " + dtoBus.getIdUsuario() + ")");
    	    sql.append(" and c.empresa_destino = e.no_empresa");
    	    sql.append(" and c.empresa_origen = e1.no_empresa");
    	    sql.append(" and c.orden > 0");
    	    sql.append(" and ccb.id_banco = " + dtoBus.getIdBanco()+ "");
    	    sql.append(" and ccb.id_chequera = '" + dtoBus.getIdChequera() + "'");
    	    sql.append(" and m.id_forma_pago = cfp.id_forma_pago");
    	    
    	    sql.append(" group by m.cve_control order by m.cve_control");
    	    System.out.println("query"+sql.toString());
    	    listConsPag = jdbcTemplate.query(sql.toString(), new RowMapper <MovimientoDto>(){
    	    	public MovimientoDto mapRow(ResultSet rs, int idx)throws SQLException{
    	    		MovimientoDto dtoRs = new MovimientoDto();
	    	    		dtoRs.setCveControl(rs.getString("cve_control"));
	    	    		dtoRs.setImporte(rs.getDouble("importe"));
	    	    		dtoRs.setImporteOriginal(rs.getDouble("importe_original"));
	    	    	return dtoRs;
    	    	}
    	    });
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BarridosFondeos, C:BarridosFondeosDao, M:consultarPagos");
		}
		return listConsPag;
	}
	

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.dao.BarridosFondeosDao#registrarControlFondeo(com.webset.set.barridosfondeos.dto.FondeoAutomaticoDto)
	 */
	@Override
	public int registrarControlFondeo(FondeoAutomaticoDto fondeoDto, BusquedaFondeoDto busquedaDto) {
		StringBuffer sbSql = new StringBuffer();
		
		sbSql.append("SELECT convert(char(10),fec_hoy, 103) FROM fechas");
		String fechaHoy = this.getJdbcTemplate().queryForObject(sbSql.toString(), String.class);
		
		sbSql.delete(0, sbSql.length());
		sbSql.append("SELECT coalesce(max(no_fondeo),0) + 1 ");
		sbSql.append("FROM control_fondeos ");
		sbSql.append("WHERE id_tipo_arbol = " + busquedaDto.getIdEmpresaRaiz());
		sbSql.append(" AND no_empresa_padre = " + fondeoDto.getNoEmpresaOrigen());
		sbSql.append(" AND no_empresa_hijo = " + fondeoDto.getNoEmpresaDestino());
		sbSql.append(" AND id_chequera_padre = '" + fondeoDto.getIdChequeraPadre() + "' ");
		sbSql.append(" AND id_chequera_hijo = '" + fondeoDto.getIdChequera() + "' ");
		sbSql.append(" AND fec_valor = convert(datetime,'" + fechaHoy + "', 103) ");
		System.out.println("query"+sbSql.toString());
		Long noFondeo = this.getJdbcTemplate().queryForLong(sbSql.toString());
		
		sbSql.delete(0, sbSql.length());
		sbSql.append("INSERT INTO control_fondeos (id_tipo_arbol, no_empresa_padre, no_empresa_hijo, ");
		sbSql.append("id_chequera_padre, id_chequera_hijo, id_banco_padre, id_banco_hijo, no_fondeo, ");
		sbSql.append("importe_requerido, importe_fondeo, fec_valor, id_usuario, id_divisa) VALUES (");
		sbSql.append(busquedaDto.getIdEmpresaRaiz() + ", " + fondeoDto.getNoEmpresaOrigen() + ", ");
		sbSql.append(fondeoDto.getNoEmpresaDestino() + ", '" + fondeoDto.getIdChequeraPadre() + "', '");
		sbSql.append(fondeoDto.getIdChequera() + "', " + busquedaDto.getIdBanco() + ", " + fondeoDto.getIdBanco() + ", ");
		sbSql.append(noFondeo + ", " + fondeoDto.getPm() + ", " + fondeoDto.getImporteTraspaso() + ", ");
		sbSql.append("convert(datetime,'" + fechaHoy + "', 103), " + busquedaDto.getIdUsuario());
		sbSql.append(", '" + busquedaDto.getIdDivisa() + "') ");
		System.out.println("query Fondeo:"+sbSql.toString());
		try{
			this.getJdbcTemplate().execute(sbSql.toString());
		}catch(Exception e)
		{
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BarridosFondeos, C:BarridosFondeosDao, M:registrarControlFondeo");
		}
		
		return noFondeo.intValue();
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.dao.BarridosFondeosDao#actualizarControlFondeos(int, int, int, java.lang.String, java.lang.String, int, int)
	 */
	@Override
	public void actualizarControlFondeos(int idTipoArbol, int noEmpresaPadre,
			int noEmpresaHijo, String idChequeraPadre, String idChequeraHijo,
			int noFondeo, int noDocto) {
		StringBuffer sbSql = new StringBuffer();
		
		sbSql.append("UPDATE control_fondeos ");
		sbSql.append("SET no_docto = '" + noDocto + "' ");
		sbSql.append("WHERE id_tipo_arbol = " + idTipoArbol);
		sbSql.append(" AND no_empresa_padre = " + noEmpresaPadre);
		sbSql.append(" AND no_empresa_hijo = " + noEmpresaHijo);
		sbSql.append(" AND id_chequera_padre = '" + idChequeraPadre + "' ");
		sbSql.append(" AND id_chequera_hijo = '" + idChequeraHijo + "' ");
		sbSql.append(" AND fec_valor = (SELECT fec_hoy FROM fechas) ");
		sbSql.append(" AND no_fondeo = " + noFondeo);
		System.out.println("query actualizar control fondeo"+sbSql.toString());
		try{
			this.getJdbcTemplate().execute(sbSql.toString());
		}catch(Exception e)
		{
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BarridosFondeos, C:BarridosFondeosDao, M:actualizarControlFondeos");
		}
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.dao.BarridosFondeosDao#obtenerReporteArbolEmpresa(int)
	 */
	@Override
	public List<Map<String, Object>> obtenerReporteArbolEmpresa(int noEmpresaRaiz) {
		StringBuffer sbSql = new StringBuffer();
		List<Map<String, Object>> resultado;
		
		sbSql.append("SELECT DISTINCT ltrim(rtrim(convert(char,A.empresa_origen))) + ':' + E.nom_empresa as empresaOrigen, ");
		sbSql.append(" ltrim(rtrim(convert(char,A.empresa_destino))) + ':' + E1.nom_empresa as empresaDestino, ");
		sbSql.append("tipo_operacion as tipoOperacion, tipo_valor as tipoValor, orden, ");
		sbSql.append("coalesce( (SELECT TOP 1 id_chequera ");
		sbSql.append("  FROM cat_cta_banco ");
		sbSql.append("  WHERE no_empresa = A.empresa_origen ");
		sbSql.append("  AND id_divisa = 'MN' ");
		sbSql.append("  AND b_traspaso = 'S' ");
		sbSql.append("  ), 'NA') + ':MN / ' +");
		sbSql.append("  coalesce( (SELECT TOP 1 id_chequera ");
		sbSql.append("  FROM cat_cta_banco ");
		sbSql.append("  WHERE no_empresa = A.empresa_origen ");
		sbSql.append("  AND id_divisa = 'DLS' ");
		sbSql.append("  AND b_traspaso = 'S' ");
		sbSql.append("  ), 'NA') + ':DLS' as cuentaOrigen, ");
		sbSql.append("coalesce( (SELECT TOP 1 id_chequera ");
		sbSql.append("  FROM cat_cta_banco ");
		sbSql.append("  WHERE no_empresa = A.empresa_destino ");
		sbSql.append("  AND id_divisa = 'MN' ");
		sbSql.append("  AND b_traspaso = 'S' ");
		sbSql.append("  ), 'NA') + ':MN / ' +");
		sbSql.append("  coalesce( (SELECT TOP 1 id_chequera ");
		sbSql.append("  FROM cat_cta_banco ");
		sbSql.append("  WHERE no_empresa = A.empresa_destino ");
		sbSql.append("  AND id_divisa = 'DLS' ");
		sbSql.append("  AND b_traspaso = 'S' ");
		sbSql.append("  ), 'NA') + ':DLS' as cuentaDestino ");
		sbSql.append("FROM arbol_empresa_fondeo A,empresa E,empresa E1 ");
		sbSql.append("WHERE A.empresa_origen = E.no_empresa ");
		sbSql.append("AND A.empresa_destino = E1.no_empresa ");
		sbSql.append("AND orden > 0 ");
		sbSql.append("AND tipo_arbol = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
		sbSql.append(" ORDER BY orden asc");
		System.out.println("query"+sbSql.toString());
		resultado = this.jdbcTemplate.queryForList(sbSql.toString());
		return resultado;
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.dao.BarridosFondeosDao#obtenerReporteFondeo(int)
	 */
	@Override
	public List<Map<String, Object>> obtenerReporteFondeo(int idUsuario, String tipoOperacion) {
		StringBuffer sbSql = new StringBuffer();
		List<Map<String, Object>> resultado;
		
		sbSql.append("SELECT sum(importe_traspaso) as fondeo,");
		sbSql.append(" empresa_origen as no_empresa_origen, empresa_destino no_empresa_destino, tipo_arbol tipoArbol,");
		sbSql.append(" orden, e.nom_empresa as empresaOrigen,  e1.nom_empresa as empresaDestino,");
		sbSql.append(" tf.id_chequera as cuentaDestino, tf.id_chequera_padre as cuentaOrigen, tipo_operacion as tipoOperacion, concepto as concepto");
		sbSql.append(" from tmp_traspaso_fondeo tf, empresa e, empresa e1");
		sbSql.append(" Where empresa_origen = e.no_empresa");
		sbSql.append(" and empresa_destino = e1.no_empresa");
		sbSql.append(" and no_usuario = " + Utilerias.validarCadenaSQL(idUsuario));
		sbSql.append(" group by empresa_origen,empresa_destino, tipo_arbol,orden, e.nom_empresa, ");
		sbSql.append(" e1.nom_empresa, tf.id_chequera, tf.id_chequera_padre,tipo_operacion,concepto");
		sbSql.append(" order by orden asc");
		System.out.println("query"+sbSql.toString());
		resultado = this.jdbcTemplate.queryForList(sbSql.toString());
		return resultado;
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.dao.BarridosFondeosDao#obtenerReporteArbolEmpresaEst(int)
	 */
	@Override
	public List<Map<String, Object>> obtenerReporteArbolEmpresaEst(int noEmpresaRaiz, int idUsuario) { 
		
		StringBuffer sbSql = new StringBuffer();

		// Empresa Hijo
		sbSql.append("\n SELECT padre, HIJO, NIETO, BISNIETO, TATARANIETO, CHOSNO, CHOSNO2, CHOSNO3, CHOSNO4, NO_EMPRESA, MAX(SHIJO) shijo, ");
		sbSql.append("\n MAX(SNIETO) snieto, MAX(SBISNIETO) sbisnieto, MAX(STATARANIETO) stataranieto, MAX(SCHOSNO) schosno, ");
		sbSql.append("\n MAX(SCHOSNO2) schosno2, MAX(SCHOSNO3) schosno3, MAX(SCHOSNO4) schosno4, MAX(SDIVISA) Divisa, MAX(SECUENCIA) secuencia ");
		sbSql.append("\n FROM ( ");
	    sbSql.append("\n  SELECT distinct coalesce(ae.padre, 0) as padre, coalesce(ae.hijo, 0) as hijo, ");
	    sbSql.append("\n      0 as nieto, 0 as bisnieto, 0 as tataranieto, 0 as chosno, 0 as chosno2, ");
	    sbSql.append("\n      0 as chosno3, 0 as chosno4, e.no_empresa, coalesce(e.nom_empresa, '') as shijo, ");
	    sbSql.append("\n      ' ' as snieto, ' ' as sbisnieto, ' ' as stataranieto, ");
	    sbSql.append("\n      ' ' as schosno, ' ' as schosno2, ' ' as schosno3, ");
	    sbSql.append("\n      ' ' as schosno4, ' ' as sDivisa, 1 as secuencia ");
	    sbSql.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
	    sbSql.append("\n      arbol_empresa ae ");
	    sbSql.append("\n  WHERE e.no_empresa = p.no_empresa ");
	    sbSql.append("\n      and p.id_tipo_persona in ('I','E') ");
	    sbSql.append("\n      and ( e.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_controladora in ");
	    sbSql.append("\n              ( SELECT e1.no_empresa ");
	    sbSql.append("\n                FROM empresa e1 ");
	    sbSql.append("\n                WHERE e1.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n                    and e1.b_concentradora = 'S' ");
	    sbSql.append("\n              ) ");
	    sbSql.append("\n          or e.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_empresa = ae.nieto ) ");
	    sbSql.append("\n      and ae.hijo = e.no_empresa ");
	    sbSql.append("\n      and e.no_empresa in ");
	    sbSql.append("\n          ( SELECT no_empresa ");
	    sbSql.append("\n            FROM usuario_empresa ");
	    sbSql.append("\n            WHERE no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + " ) ");
	    sbSql.append("\n  GROUP BY ae.padre, ae.hijo, e.no_empresa, e.nom_empresa ");
	    sbSql.append("\n  UNION ALL ");
	    // Divisa Empresa Hijo
	    sbSql.append("\n  SELECT distinct coalesce(ae.padre, 0) as padre, coalesce(ae.hijo, 0) as hijo, ");
	    sbSql.append("\n      0 as nieto, 0 as bisnieto, 0 as tataranieto, 0 as chosno, 0 as chosno2, ");
	    sbSql.append("\n      0 as chosno3, 0 as chosno4, e.no_empresa, ' ' as shijo, ");
	    sbSql.append("\n      ' ' as nieto, ' ' as bisnieto, ' ' as tataranieto, ");
	    sbSql.append("\n      ' ' as chosno, ' ' as chosno2, ' ' as chosno3, ");
	    sbSql.append("\n      ' ' as chosno4, cd.desc_divisa as Divisa, 2 as secuencia ");
	    sbSql.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
	    sbSql.append("\n      arbol_empresa ae, cat_divisa cd ");
	    sbSql.append("\n  WHERE e.no_empresa = p.no_empresa ");
	    sbSql.append("\n      and p.id_tipo_persona in ('I','E') ");
	    sbSql.append("\n      and ( e.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_controladora in ");
	    sbSql.append("\n              ( SELECT e1.no_empresa ");
	    sbSql.append("\n                FROM empresa e1 ");
	    sbSql.append("\n                WHERE e1.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n                    and e1.b_concentradora = 'S' ");
	    sbSql.append("\n              ) ");
	    sbSql.append("\n          or e.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_empresa = ae.nieto ) ");
	    sbSql.append("\n      and ae.hijo = e.no_empresa ");
	    sbSql.append("\n      and cd.id_divisa = c.id_divisa ");
	    sbSql.append("\n      and e.no_empresa in ");
	    sbSql.append("\n          ( SELECT no_empresa ");
	    sbSql.append("\n            FROM usuario_empresa ");
	    sbSql.append("\n            WHERE no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + " ) ");
	    sbSql.append("\n  GROUP BY ae.padre, ae.hijo, e.no_empresa, e.nom_empresa, cd.desc_divisa ");
	    sbSql.append("\n  UNION ALL ");
	    // Empresa Nieto
	    sbSql.append("\n  SELECT distinct coalesce(ae.padre, 0), coalesce(ae.hijo, 0), coalesce(ae.nieto, 0), ");
	    sbSql.append("\n      0, 0, 0, 0, 0, 0, e.no_empresa, ' ' as hijo, coalesce(e.nom_empresa, '') as nieto, ");
	    sbSql.append("\n      ' ' as bisnieto, ' ' as tataranieto, ' ' as chosno, ");
	    sbSql.append("\n      ' ' as chosno2, ' ' as chosno3, ");
	    sbSql.append("\n      ' ' as chosno4, ' ' as Divisa, 3 as secuencia ");
	    sbSql.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
	    sbSql.append("\n      arbol_empresa ae ");
	    sbSql.append("\n  WHERE e.no_empresa = p.no_empresa ");
	    sbSql.append("\n      and p.id_tipo_persona in ('I','E') ");
	    sbSql.append("\n      and ( e.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_controladora in ");
	    sbSql.append("\n              ( SELECT e1.no_empresa ");
	    sbSql.append("\n                FROM empresa e1 ");
	    sbSql.append("\n                WHERE e1.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n                    and e1.b_concentradora = 'S' ");
	    sbSql.append("\n              ) ");
	    sbSql.append("\n          or e.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_empresa = ae.bisnieto ) ");
	    sbSql.append("\n      and ae.nieto = e.no_empresa ");
	    sbSql.append("\n      and e.no_empresa in ");
	    sbSql.append("\n          ( SELECT no_empresa ");
	    sbSql.append("\n            FROM usuario_empresa ");
	    sbSql.append("\n            WHERE no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + " ) ");
	    sbSql.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, e.no_empresa, e.nom_empresa ");
	    sbSql.append("\n  UNION ALL ");
	    // Divisa Empresa Nieto
	    sbSql.append("\n  SELECT distinct coalesce(ae.padre, 0), coalesce(ae.hijo, 0), coalesce(ae.nieto, 0), ");
	    sbSql.append("\n      0, 0, 0, 0, 0, 0, e.no_empresa, ' ' as hijo, ' ' as nieto, ");
	    sbSql.append("\n      ' ' as bisnieto, ' ' as tataranieto, ' ' as chosno, ");
	    sbSql.append("\n      ' ' as chosno2, ' ' as chosno3, ");
	    sbSql.append("\n      ' ' as chosno4, cd.desc_divisa as Divisa, 4 as secuencia ");
	    sbSql.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
	    sbSql.append("\n      arbol_empresa ae, cat_divisa cd ");
	    sbSql.append("\n  WHERE e.no_empresa = p.no_empresa ");
	    sbSql.append("\n      and p.id_tipo_persona in ('I','E') ");
	    sbSql.append("\n      and ( e.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_controladora in ");
	    sbSql.append("\n              ( SELECT e1.no_empresa ");
	    sbSql.append("\n                FROM empresa e1 ");
	    sbSql.append("\n                WHERE e1.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n                    and e1.b_concentradora = 'S' ");
	    sbSql.append("\n              ) ");
	    sbSql.append("\n          or e.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_empresa = ae.bisnieto ) ");
	    sbSql.append("\n      and ae.nieto = e.no_empresa ");
	    sbSql.append("\n      and cd.id_divisa = c.id_divisa ");
	    sbSql.append("\n      and e.no_empresa in ");
	    sbSql.append("\n          ( SELECT no_empresa ");
	    sbSql.append("\n            FROM usuario_empresa ");
	    sbSql.append("\n            WHERE no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + " ) ");
	    sbSql.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, e.no_empresa, e.nom_empresa, cd.desc_divisa ");
	    sbSql.append("\n  UNION ALL ");
	    // Empresa bisnieto
	    sbSql.append("\n  SELECT distinct coalesce(ae.padre, 0), coalesce(ae.hijo, 0), coalesce(ae.nieto, 0),");
	    sbSql.append("\n      coalesce(ae.bisnieto, 0), 0, 0, 0, 0, 0, e.no_empresa, ' ' as hijo, ' ' as nieto, ");
	    sbSql.append("\n      coalesce(e.nom_empresa, '') as bisnieto, ' ' as tataranieto, ' ' as chosno, ");
	    sbSql.append("\n      ' ' as chosno2, ' ' as chosno3, ' ' as chosno4, ");
	    sbSql.append("\n      ' ' as Divisa, 5 as secuencia ");
	    sbSql.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
	    sbSql.append("\n      arbol_empresa ae ");
	    sbSql.append("\n  WHERE e.no_empresa = p.no_empresa ");
	    sbSql.append("\n      and p.id_tipo_persona in ('I','E') ");
	    sbSql.append("\n      and ( e.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_controladora in ");
	    sbSql.append("\n              ( SELECT e1.no_empresa ");
	    sbSql.append("\n                FROM empresa e1 ");
	    sbSql.append("\n               WHERE e1.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n                    and e1.b_concentradora = 'S' ");
	    sbSql.append("\n              ) ");
	    sbSql.append("\n          or e.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_empresa = ae.tataranieto ) ");
	    sbSql.append("\n      and ae.bisnieto = e.no_empresa ");
	    sbSql.append("\n      and e.no_empresa in ");
	    sbSql.append("\n          ( SELECT no_empresa ");
	    sbSql.append("\n            FROM usuario_empresa ");
	    sbSql.append("\n            WHERE no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + " ) ");
	    sbSql.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, ae.bisnieto, e.no_empresa, e.nom_empresa ");
	    sbSql.append("\n  UNION ALL ");
	    // Divisa Empresa bisnieto
	    sbSql.append("\n  SELECT distinct coalesce(ae.padre, 0), coalesce(ae.hijo, 0), coalesce(ae.nieto, 0),");
	    sbSql.append("\n      coalesce(ae.bisnieto, 0), 0, 0, 0, 0, 0, e.no_empresa, ' ' as hijo, ");
	    sbSql.append("\n      ' ' as nieto, ");
	    sbSql.append("\n      ' ' as bisnieto, ' ' as tataranieto, ' ' as chosno, ");
	    sbSql.append("\n      ' ' as chosno2, ' ' as chosno3, ' ' as chosno4, ");
	    sbSql.append("\n      cd.desc_divisa as Divisa, 6 as secuencia ");
	    sbSql.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
	    sbSql.append("\n      arbol_empresa ae, cat_divisa cd ");
	    sbSql.append("\n  WHERE e.no_empresa = p.no_empresa ");
	    sbSql.append("\n      and p.id_tipo_persona in ('I','E') ");
	    sbSql.append("\n      and ( e.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_controladora in ");
	    sbSql.append("\n              ( SELECT e1.no_empresa ");
	    sbSql.append("\n                FROM empresa e1 ");
	    sbSql.append("\n                WHERE e1.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n                    and e1.b_concentradora = 'S' ");
	    sbSql.append("\n              ) ");
	    sbSql.append("\n          or e.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_empresa = ae.tataranieto ) ");
	    sbSql.append("\n      and ae.bisnieto = e.no_empresa ");
	    sbSql.append("\n      and cd.id_divisa = c.id_divisa ");
	    sbSql.append("\n      and e.no_empresa in ");
	    sbSql.append("\n          ( SELECT no_empresa ");
	    sbSql.append("\n            FROM usuario_empresa ");
	    sbSql.append("\n            WHERE no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + " ) ");
	    sbSql.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, ae.bisnieto, e.no_empresa, e.nom_empresa, cd.desc_divisa ");
	    sbSql.append("\n  UNION ALL ");
	    // Empresa Tataranieto
	    sbSql.append("\n  SELECT distinct coalesce(ae.padre, 0), coalesce(ae.hijo, 0), coalesce(ae.nieto, 0), ");
	    sbSql.append("\n      coalesce(ae.bisnieto, 0),  coalesce(ae.tataranieto, 0), 0, 0, 0, 0, e.no_empresa, ");
	    sbSql.append("\n      ' ' as hijo, ' ' as nieto, ' ' as bisnieto, coalesce(e.nom_empresa, '') as tataranieto, ");
	    sbSql.append("\n      ' ' as chosno, ' ' as chosno2, ' ' as chosno3, ' ' as chosno4, ");
	    sbSql.append("\n      ' ' as Divisa, 7 as secuencia ");
	    sbSql.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
	    sbSql.append("\n      arbol_empresa ae ");
	    sbSql.append("\n  WHERE e.no_empresa = p.no_empresa ");
	    sbSql.append("\n      and p.id_tipo_persona in ('I','E') ");
	    sbSql.append("\n      and ( e.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_controladora in ");
	    sbSql.append("\n              ( SELECT e1.no_empresa ");
	    sbSql.append("\n                FROM empresa e1 ");
	    sbSql.append("\n                WHERE e1.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n                    and e1.b_concentradora = 'S' ");
	    sbSql.append("\n              ) ");
	    sbSql.append("\n          or e.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_empresa = ae.chosno ) ");
	    sbSql.append("\n     and ae.tataranieto = e.no_empresa ");
	    sbSql.append("\n      and e.no_empresa in ");
	    sbSql.append("\n          ( SELECT no_empresa ");
	    sbSql.append("\n            FROM usuario_empresa ");
	    sbSql.append("\n            WHERE no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + " ) ");
	    sbSql.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, ae.bisnieto, ae.tataranieto, e.no_empresa, ");
	    sbSql.append("\n      e.nom_empresa ");
	    sbSql.append("\n  UNION ALL ");
	    // Divisa Empresa Tataranieto
	    sbSql.append("\n  SELECT distinct coalesce(ae.padre, 0), coalesce(ae.hijo, 0), coalesce(ae.nieto, 0), ");
	    sbSql.append("\n      coalesce(ae.bisnieto, 0),  coalesce(ae.tataranieto, 0), 0, 0, 0, 0, e.no_empresa, ");
	    sbSql.append("\n      ' ' as hijo, ' ' as nieto, ' ' as bisnieto, ' ' as tataranieto, ");
	    sbSql.append("\n      ' ' as chosno, ' ' as chosno2, ' ' as chosno3, ");
	    sbSql.append("\n      ' ' as chosno4, cd.desc_divisa as Divisa, 8 as secuencia ");
	    sbSql.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta), persona p, ");
	    sbSql.append("\n      arbol_empresa ae, cat_divisa cd ");
	    sbSql.append("\n  WHERE e.no_empresa = p.no_empresa ");
	    sbSql.append("\n      and p.id_tipo_persona in ('I','E') ");
	    sbSql.append("\n      and ( e.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_controladora in ");
	    sbSql.append("\n              ( SELECT e1.no_empresa ");
	    sbSql.append("\n                FROM empresa e1 ");
	    sbSql.append("\n                WHERE e1.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n                    and e1.b_concentradora = 'S' ");
	    sbSql.append("\n              ) ");
	    sbSql.append("\n          or e.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_empresa = ae.chosno ) ");
	    sbSql.append("\n      and ae.tataranieto = e.no_empresa ");
	    sbSql.append("\n      and cd.id_divisa = c.id_divisa ");
	    sbSql.append("\n      and e.no_empresa in ");
	    sbSql.append("\n          ( SELECT no_empresa ");
	    sbSql.append("\n            FROM usuario_empresa ");
	    sbSql.append("\n            WHERE no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + " ) ");
	    sbSql.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, ae.bisnieto, ae.tataranieto, e.no_empresa, ");
	    sbSql.append("\n      e.nom_empresa, cd.desc_divisa ");
	    sbSql.append("\n  UNION ALL ");
	    // Empresa Chosno
	    sbSql.append("\n  SELECT distinct coalesce(ae.padre, 0), coalesce(ae.hijo, 0), coalesce(ae.nieto, 0), ");
	    sbSql.append("\n      coalesce(ae.bisnieto, 0), coalesce(ae.tataranieto, 0), coalesce(ae.chosno, 0), 0, ");
	    sbSql.append("\n      0, 0, e.no_empresa, ' ' as hijo, ' ' as nieto, ");
	    sbSql.append("\n      ' ' as bisnieto, ' ' as tataranieto, ");
	    sbSql.append("\n      coalesce(e.nom_empresa, '') as chosno, ' ' as chosno2, ");
	    sbSql.append("\n      ' ' as chosno3, ' ' as chosno4, ");
	    sbSql.append("\n      ' ' as Divisa, 9 as secuencia ");
	    sbSql.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
	    sbSql.append("\n      arbol_empresa ae ");
	    sbSql.append("\n  WHERE e.no_empresa = p.no_empresa ");
	    sbSql.append("\n      and p.id_tipo_persona in ('I','E') ");
	    sbSql.append("\n      and ( e.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_controladora in ");
	    sbSql.append("\n              ( SELECT e1.no_empresa ");
	    sbSql.append("\n                FROM empresa e1 ");
	    sbSql.append("\n                WHERE e1.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n                    and e1.b_concentradora = 'S' ");
	    sbSql.append("\n              ) ");
	    sbSql.append("\n          or e.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_empresa = ae.chosno2 ) ");
	    sbSql.append("\n      and ae.chosno = e.no_empresa ");
	    sbSql.append("\n      and e.no_empresa in ");
	    sbSql.append("\n          ( SELECT no_empresa ");
	    sbSql.append("\n            FROM usuario_empresa ");
	    sbSql.append("\n            WHERE no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + " ) ");
	    sbSql.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, ae.bisnieto, ae.tataranieto, ae.chosno, ");
	    sbSql.append("\n      e.no_empresa, e.nom_empresa ");
	    sbSql.append("\n  UNION ALL ");
	    // Divisa Empresa Chosno
	    sbSql.append("\n  SELECT distinct coalesce(ae.padre, 0), coalesce(ae.hijo, 0), coalesce(ae.nieto, 0), ");
	    sbSql.append("\n      coalesce(ae.bisnieto, 0), coalesce(ae.tataranieto, 0), coalesce(ae.chosno, 0), 0, ");
	    sbSql.append("\n      0, 0, e.no_empresa, ' ' as hijo, ' ' as nieto, ");
	    sbSql.append("\n      ' ' as bisnieto, ' ' as tataranieto, ");
	    sbSql.append("\n      ' ' as chosno, ' ' as chosno2, ' ' as chosno3, ");
	    sbSql.append("\n      ' ' as chosno4, cd.desc_divisa as Divisa, 10 as secuencia ");
	    sbSql.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
	    sbSql.append("\n      arbol_empresa ae, cat_divisa cd ");
	    sbSql.append("\n  WHERE e.no_empresa = p.no_empresa ");
	    sbSql.append("\n      and p.id_tipo_persona in ('I','E') ");
	    sbSql.append("\n      and ( e.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_controladora in ");
	    sbSql.append("\n              ( SELECT e1.no_empresa ");
	    sbSql.append("\n                FROM empresa e1 ");
	    sbSql.append("\n                WHERE e1.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n                    and e1.b_concentradora = 'S' ");
	    sbSql.append("\n              ) ");
	    sbSql.append("\n          or e.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n         or e.no_empresa = ae.chosno2 ) ");
	    sbSql.append("\n      and ae.chosno = e.no_empresa ");
	    sbSql.append("\n      and cd.id_divisa = c.id_divisa ");
	    sbSql.append("\n      and e.no_empresa in ");
	    sbSql.append("\n          ( SELECT no_empresa ");
	    sbSql.append("\n            FROM usuario_empresa ");
	    sbSql.append("\n            WHERE no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + " ) ");
	    sbSql.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, ae.bisnieto, ae.tataranieto, ae.chosno, ");
	    sbSql.append("\n      e.no_empresa, e.nom_empresa, cd.desc_divisa ");
	    sbSql.append("\n  UNION ALL ");
	    // Empresa Chosno2
	    sbSql.append("\n  SELECT distinct coalesce(ae.padre, 0), coalesce(ae.hijo, 0), coalesce(ae.nieto, 0), ");
	    sbSql.append("\n      coalesce(ae.bisnieto, 0),  coalesce(ae.tataranieto, 0), coalesce(ae.chosno, 0), ");
	    sbSql.append("\n      coalesce(ae.chosno2, 0), 0, 0, e.no_empresa, ' ' as hijo, ' ' as nieto, ");
	    sbSql.append("\n      ' ' as bisnieto, ' ' as tataranieto, ' ' as chosno, ");
	    sbSql.append("\n      coalesce(e.nom_empresa, '') as chosno2, ' ' as chosno3, ");
	    sbSql.append("\n      ' ' as chosno4, ' ' as Divisa, 11 as secuencia ");
	    sbSql.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
	    sbSql.append("\n      arbol_empresa ae ");
	    sbSql.append("\n  WHERE e.no_empresa = p.no_empresa ");
	    sbSql.append("\n      and p.id_tipo_persona in ('I','E') ");
	    sbSql.append("\n      and ( e.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_controladora in ");
	    sbSql.append("\n              ( SELECT e1.no_empresa ");
	    sbSql.append("\n                FROM empresa e1 ");
	    sbSql.append("\n                WHERE e1.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n                    and e1.b_concentradora = 'S' ");
	    sbSql.append("\n              ) ");
	    sbSql.append("\n          or e.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_empresa = ae.chosno3 ) ");
	    sbSql.append("\n      and ae.chosno2 = e.no_empresa ");
	    sbSql.append("\n      and e.no_empresa in ");
	    sbSql.append("\n          ( SELECT no_empresa ");
	    sbSql.append("\n            FROM usuario_empresa ");
	    sbSql.append("\n            WHERE no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + " ) ");
	    sbSql.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, ae.bisnieto, ae.tataranieto, ae.chosno, ");
	    sbSql.append("\n      ae.chosno2, e.no_empresa , e.nom_empresa ");
	    sbSql.append("\n  UNION ALL ");
	    // Divisa Empresa Chosno2
	    sbSql.append("\n  SELECT distinct coalesce(ae.padre, 0), coalesce(ae.hijo, 0), coalesce(ae.nieto, 0), ");
	    sbSql.append("\n      coalesce(ae.bisnieto, 0),  coalesce(ae.tataranieto, 0), coalesce(ae.chosno, 0), ");
	    sbSql.append("\n      coalesce(ae.chosno2, 0), 0, 0, e.no_empresa, ' ' as hijo, ' ' as nieto, ");
	    sbSql.append("\n     ' ' as bisnieto, ' ' as tataranieto, ' ' as chosno, ");
	    sbSql.append("\n      ' ' as chosno2, ' ' as chosno3, ' ' as chosno4, ");
	    sbSql.append("\n      cd.desc_divisa as Divisa, 12 as secuencia ");
	    sbSql.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
	    sbSql.append("\n      arbol_empresa ae, cat_divisa cd ");
	    sbSql.append("\n  WHERE e.no_empresa = p.no_empresa ");
	    sbSql.append("\n      and p.id_tipo_persona in ('I','E') ");
	    sbSql.append("\n      and ( e.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_controladora in ");
	    sbSql.append("\n              ( SELECT e1.no_empresa ");
	    sbSql.append("\n                FROM empresa e1 ");
	    sbSql.append("\n                WHERE e1.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n                    and e1.b_concentradora = 'S' ");
	    sbSql.append("\n              ) ");
	    sbSql.append("\n          or e.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_empresa = ae.chosno3 ) ");
	    sbSql.append("\n      and ae.chosno2 = e.no_empresa ");
	    sbSql.append("\n      and cd.id_divisa = c.id_divisa ");
	    sbSql.append("\n      and e.no_empresa in ");
	    sbSql.append("\n          ( SELECT no_empresa ");
	    sbSql.append("\n            FROM usuario_empresa ");
	    sbSql.append("\n            WHERE no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + " ) ");
	    sbSql.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, ae.bisnieto, ae.tataranieto, ae.chosno, ");
	    sbSql.append("\n      ae.chosno2, e.no_empresa , e.nom_empresa, cd.desc_divisa ");
	    sbSql.append("\n  UNION ALL ");
	    // Empresa Chosno3
	    sbSql.append("\n  SELECT distinct coalesce(ae.padre, 0), coalesce(ae.hijo, 0), coalesce(ae.nieto, 0), ");
	    sbSql.append("\n      coalesce(ae.bisnieto, 0), coalesce(ae.tataranieto, 0), coalesce(ae.chosno, 0), ");
	    sbSql.append("\n      coalesce(ae.chosno2, 0), coalesce(ae.chosno3, 0), 0, e.no_empresa, ' ' as hijo, ");
	    sbSql.append("\n      ' ' as nieto, ' ' as bisnieto, ' ' as tataranieto, ");
	    sbSql.append("\n      ' ' as chosno, ' ' as chosno2, ");
	    sbSql.append("\n      coalesce(e.nom_empresa, '') as chosno3, ' ' as chosno4, ' ' as Divisa, 13 as secuencia ");
	    sbSql.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
	    sbSql.append("\n      arbol_empresa ae ");
	    sbSql.append("\n  WHERE e.no_empresa = p.no_empresa ");
	    sbSql.append("\n      and p.id_tipo_persona in ('I','E') ");
	    sbSql.append("\n      and ( e.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_controladora in ");
	    sbSql.append("\n              ( SELECT e1.no_empresa ");
	    sbSql.append("\n                FROM empresa e1 ");
	    sbSql.append("\n                WHERE e1.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n                    and e1.b_concentradora = 'S' ");
	    sbSql.append("\n              ) ");
	    sbSql.append("\n          or e.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_empresa = ae.chosno4 ) ");
	    sbSql.append("\n      and ae.chosno3 = e.no_empresa ");
	    sbSql.append("\n      and e.no_empresa in ");
	    sbSql.append("\n          ( SELECT no_empresa ");
	    sbSql.append("\n            FROM usuario_empresa ");
	    sbSql.append("\n            WHERE no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + " ) ");
	    sbSql.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, ae.bisnieto, ae.tataranieto, ae.chosno, ");
	    sbSql.append("\n      ae.chosno2, ae.chosno3, e.no_empresa, e.nom_empresa ");
	    sbSql.append("\n  UNION ALL ");
	    // Divisa Empresa Chosno3
	    sbSql.append("\n  SELECT distinct coalesce(ae.padre, 0), coalesce(ae.hijo, 0), coalesce(ae.nieto, 0), ");
	    sbSql.append("\n      coalesce(ae.bisnieto, 0), coalesce(ae.tataranieto, 0), coalesce(ae.chosno, 0), ");
	    sbSql.append("\n      coalesce(ae.chosno2, 0), coalesce(ae.chosno3, 0), 0, e.no_empresa, ' ' as hijo, ");
	    sbSql.append("\n      ' ' as nieto, ' ' as bisnieto, ' ' as tataranieto, ");
	    sbSql.append("\n      ' ' as chosno, ' ' as chosno2, ");
	    sbSql.append("\n      ' ' as chosno3, ' ' as chosno4, cd.desc_divisa as Divisa, 14 as secuencia ");
	    sbSql.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
	    sbSql.append("\n      arbol_empresa ae, cat_divisa cd ");
	    sbSql.append("\n  WHERE e.no_empresa = p.no_empresa ");
	    sbSql.append("\n      and p.id_tipo_persona in ('I','E') ");
	    sbSql.append("\n      and ( e.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_controladora in ");
	    sbSql.append("\n              ( SELECT e1.no_empresa ");
	    sbSql.append("\n                FROM empresa e1 ");
	    sbSql.append("\n                WHERE e1.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n                    and e1.b_concentradora = 'S' ");
	    sbSql.append("\n              ) ");
	    sbSql.append("\n          or e.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_empresa = ae.chosno4 ) ");
	    sbSql.append("\n      and ae.chosno3 = e.no_empresa ");
	    sbSql.append("\n      and cd.id_divisa = c.id_divisa ");
	    sbSql.append("\n      and e.no_empresa in ");
	    sbSql.append("\n          ( SELECT no_empresa ");
	    sbSql.append("\n            FROM usuario_empresa ");
	    sbSql.append("\n            WHERE no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + " ) ");
	    sbSql.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, ae.bisnieto, ae.tataranieto, ae.chosno, ");
	    sbSql.append("\n      ae.chosno2, ae.chosno3, e.no_empresa, e.nom_empresa, cd.desc_divisa ");
	    sbSql.append("\n  UNION ALL ");
	    // Empresa Chosno4
	    sbSql.append("\n  SELECT distinct coalesce(ae.padre, 0), coalesce(ae.hijo, 0), coalesce(ae.nieto, 0), ");
	    sbSql.append("\n      coalesce(ae.bisnieto, 0), coalesce(ae.tataranieto, 0), coalesce(ae.chosno, 0), ");
	    sbSql.append("\n      coalesce(ae.chosno2, 0), coalesce(ae.chosno3, 0), coalesce(ae.chosno4, 0), ");
	    sbSql.append("\n      e.no_empresa, ' ' as hijo, ' ' as nieto, ");
	    sbSql.append("\n      ' ' as bisnieto, ' ' as tataranieto, ");
	    sbSql.append("\n      ' ' as chosno, ' ' as chosno2, ");
	    sbSql.append("\n      ' ' as chosno3, coalesce(e.nom_empresa, '') as chosno4, ");
	    sbSql.append("\n      ' ' as Divisa, 15 as secuencia ");
	    sbSql.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
	    sbSql.append("\n      arbol_empresa ae ");
	    sbSql.append("\n  WHERE e.no_empresa = p.no_empresa ");
	    sbSql.append("\n      and p.id_tipo_persona in ('I','E') ");
	    sbSql.append("\n      and ( e.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_controladora in ");
	    sbSql.append("\n              ( SELECT e1.no_empresa ");
	    sbSql.append("\n                FROM empresa e1 ");
	    sbSql.append("\n                WHERE e1.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n                    and e1.b_concentradora = 'S' ");
	    sbSql.append("\n              ) ");
	    sbSql.append("\n          or e.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresaRaiz) + " ) ");
	    sbSql.append("\n      and ae.chosno4 = e.no_empresa ");
	    sbSql.append("\n      and e.no_empresa in ");
	    sbSql.append("\n          ( SELECT no_empresa ");
	    sbSql.append("\n            FROM usuario_empresa ");
	    sbSql.append("\n            WHERE no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + " ) ");
	    sbSql.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, ae.bisnieto, ae.tataranieto, ae.chosno, ae.chosno2, ");
	    sbSql.append("\n      ae.chosno3, ae.chosno4, e.no_empresa, e.nom_empresa ");
	    sbSql.append("\n  UNION ALL ");
	    // Divisa Empresa Chosno4
	    sbSql.append("\n  SELECT distinct coalesce(ae.padre, 0), coalesce(ae.hijo, 0), coalesce(ae.nieto, 0), ");
	    sbSql.append("\n      coalesce(ae.bisnieto, 0), coalesce(ae.tataranieto, 0), coalesce(ae.chosno, 0), ");
	    sbSql.append("\n      coalesce(ae.chosno2, 0), coalesce(ae.chosno3, 0), coalesce(ae.chosno4, 0), ");
	    sbSql.append("\n      e.no_empresa, ' ' as hijo, ' ' as nieto, ");
	    sbSql.append("\n      ' ' as bisnieto, ' ' as tataranieto, ");
	    sbSql.append("\n      ' ' as chosno, ' ' as chosno2, ");
	    sbSql.append("\n      ' ' as chosno3, ' ' as chosno4, ");
	    sbSql.append("\n      cd.desc_divisa as Divisa, 16 as secuencia ");
	    sbSql.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
	    sbSql.append("\n      arbol_empresa ae, cat_divisa cd ");
	    sbSql.append("\n  WHERE e.no_empresa = p.no_empresa ");
	    sbSql.append("\n      and p.id_tipo_persona in ('I','E') ");
	    sbSql.append("\n      and ( e.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n          or e.no_controladora in ");
	    sbSql.append("\n              ( SELECT e1.no_empresa ");
	    sbSql.append("\n                FROM empresa e1 ");
	    sbSql.append("\n                WHERE e1.no_controladora = " + Utilerias.validarCadenaSQL(noEmpresaRaiz));
	    sbSql.append("\n                    and e1.b_concentradora = 'S' ");
	    sbSql.append("\n              ) ");
	    sbSql.append("\n          or e.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresaRaiz) + " ) ");
	    sbSql.append("\n      and ae.chosno4 = e.no_empresa ");
	    sbSql.append("\n      and cd.id_divisa = c.id_divisa ");
	    sbSql.append("\n      and e.no_empresa in ");
	    sbSql.append("\n          ( SELECT no_empresa ");
	    sbSql.append("\n            FROM usuario_empresa ");
	    sbSql.append("\n            WHERE no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + " ) ");
	    
	    sbSql.append(") TABLA ");
	    sbSql.append("\n GROUP BY padre, HIJO, NIETO, BISNIETO, TATARANIETO, CHOSNO, CHOSNO2, CHOSNO3, CHOSNO4, NO_EMPRESA ");
	    sbSql.append("\n ORDER BY PADRE, HIJO, NIETO, BISNIETO, TATARANIETO, CHOSNO, CHOSNO2, CHOSNO3, CHOSNO4, 20");
	    //sbSql.append(" GROUP BY ae.padre, ae.hijo, ae.nieto, ae.bisnieto, ae.tataranieto, ae.chosno, ae.chosno2, ");
	    //sbSql.append("     ae.chosno3, ae.chosno4, e.no_empresa, e.nom_empresa, cd.desc_divisa ");
	    //sbSql.append(" ORDER BY padre, hijo, nieto, bisnieto, tataranieto, chosno, chosno2, chosno3, chosno4, e.no_empresa, secuencia ");
	    System.out.println("query"+sbSql.toString());
	    return this.getJdbcTemplate().queryForList(sbSql.toString());

	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.dao.BarridosFondeosDao#obtenerReporteFiliales(int)
	 */
	@Override
	public List<Map<String, Object>> obtenerReporteFiliales(int noEmpresa) {
		StringBuffer sbSql = new StringBuffer();
		
		sbSql.append("SELECT a.no_empresa, a.no_cuenta, a.id_divisa, b.nom_empresa empresaPadre, c.nom_empresa empresaFilial, ");
		sbSql.append("d.desc_divisa divisa ");
		sbSql.append("FROM cuenta a, empresa b, empresa c, cat_divisa d ");
		sbSql.append("WHERE a.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresa) + " ");
		sbSql.append("AND a.no_empresa = b.no_empresa ");
		sbSql.append("AND a.no_cuenta = c.no_empresa ");
		sbSql.append("AND a.id_divisa = d.id_divisa");
		System.out.println("query"+sbSql.toString());
		return this.getJdbcTemplate().queryForList(sbSql.toString());
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.dao.BarridosFondeosDao#obtenerReporteBarridosFondeos(int, int, java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> obtenerReporteBarridosFondeos(int noEmpresa, int idUsuario, String fecha) {
		StringBuffer sbSql = new StringBuffer();
		
		sbSql.append(" SELECT m.id_divisa, no_docto, importe, ");
		sbSql.append(" m.no_empresa, concepto, beneficiario, fec_valor, e.nom_empresa, ee.nom_empresa as desc_empresa_benef,");
		sbSql.append(" m.id_banco, cbb.desc_banco, m.id_chequera, m.id_banco_benef, ccbb.desc_banco as desc_banco_benef,");
		sbSql.append(" id_chequera_benef, cb.no_empresa as no_empresa_benef, sucursal,");
		sbSql.append(" case when id_tipo_operacion = 3805 and id_estatus_mov = 'L' then 'BARRIDOS SOLICITUDES'");
		sbSql.append(" when id_tipo_operacion = 3705 and id_estatus_mov = 'A' then 'BARRIDOS EJECUTADOS'");
		sbSql.append(" when id_tipo_operacion = 3806 and id_estatus_mov = 'L' then 'FONDEOS SOLICITUDES'");
		sbSql.append(" when id_tipo_operacion = 3706 and id_estatus_mov = 'A' then 'FONDEOS EJECUTADOS' end as tipo");
		sbSql.append(" FROM movimiento m, empresa e, cat_cta_banco cb,empresa ee,cat_banco cbb,cat_banco ccbb ");
		sbSql.append(" WHERE (id_tipo_operacion in(3806,3706) and id_tipo_movto='E')");
		sbSql.append(" AND cb.id_chequera=m.id_chequera_benef ");
		sbSql.append(" AND cbb.id_banco=m.id_banco ");
		sbSql.append(" AND ccbb.id_banco=m.id_banco_benef ");
		sbSql.append(" AND cb.id_banco=m.id_banco_benef ");
		sbSql.append(" AND cb.no_empresa=ee.no_empresa ");
		sbSql.append(" AND m.no_empresa=e.no_empresa");
		sbSql.append(" AND id_estatus_mov in('L','A') ");
		if (noEmpresa > 0)
			sbSql.append(" AND m.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresa));
		sbSql.append(" AND m.fec_valor >= convert(datetime,'" + Utilerias.validarCadenaSQL(fecha) + "', 103) ");
		sbSql.append(" AND m.fec_valor < convert(datetime,'" + Utilerias.validarCadenaSQL(fecha) + "', 103) + 1");
		sbSql.append(" AND m.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + ")");
		sbSql.append(" Union All ");
		sbSql.append(" select m.id_divisa,no_docto, importe, m.no_empresa,");
		sbSql.append(" concepto,beneficiario,fec_valor, e.nom_empresa,ee.nom_empresa as desc_empresa_benef,");
		sbSql.append(" m.id_banco,cbb.desc_banco as desc_banco_benef,m.id_chequera as id_chequera_benef, ");
		sbSql.append(" m.id_banco_benef, ccbb.desc_banco as desc_banco,id_chequera_benef as id_chequera, ");
		sbSql.append(" cb.no_empresa as no_empresa_benef,sucursal,");
		sbSql.append(" case when id_tipo_operacion=3805 AND id_estatus_mov='L' then 'BARRIDOS SOLICITUDES' ");
		sbSql.append(" when id_tipo_operacion=3705 AND id_estatus_mov='A' then 'BARRIDOS EJECUTADOS'");
		sbSql.append(" when id_tipo_operacion=3806 AND id_estatus_mov='L' then 'FONDEOS SOLICITUDES'");
		sbSql.append(" when id_tipo_operacion=3706 AND id_estatus_mov='A' then 'FONDEOS EJECUTADOS' end as tipo");
		sbSql.append(" from movimiento m, empresa e, cat_cta_banco cb,empresa ee,cat_banco cbb,cat_banco ccbb ");
		sbSql.append(" where (id_tipo_operacion in (3805,3705) AND id_tipo_movto='I') ");
		sbSql.append(" AND cb.id_chequera=m.id_chequera_benef ");
		sbSql.append(" AND cbb.id_banco=m.id_banco ");
		sbSql.append(" AND ccbb.id_banco=m.id_banco_benef ");
		sbSql.append(" AND cb.id_banco=m.id_banco_benef ");
		sbSql.append(" AND cb.no_empresa=ee.no_empresa ");
		sbSql.append(" AND m.no_empresa=e.no_empresa ");
		sbSql.append(" AND id_estatus_mov in('L','A') ");
		if (noEmpresa > 0)
			sbSql.append(" AND m.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresa));
		sbSql.append(" AND m.fec_valor >= convert(datetime,'" + Utilerias.validarCadenaSQL(fecha) + "', 103) ");
		sbSql.append(" AND m.fec_valor < convert(datetime,'" + Utilerias.validarCadenaSQL(fecha) + "', 103) + 1");
		sbSql.append(" AND m.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + ")");
		sbSql.append(" Union All ");
		sbSql.append(" select m.id_divisa,no_docto, importe, m.no_empresa, ");
		sbSql.append(" concepto,beneficiario,fec_valor, e.nom_empresa,ee.nom_empresa as desc_empresa_benef, ");
		sbSql.append(" m.id_banco,cbb.desc_banco,m.id_chequera, m.id_banco_benef, ccbb.desc_banco as desc_banco_benef,");
		sbSql.append(" id_chequera_benef,cb.no_empresa as no_empresa_benef,sucursal,");
		sbSql.append(" case when id_tipo_operacion=3805 AND id_estatus_mov='L' then 'BARRIDOS SOLICITUDES' ");
		sbSql.append(" when id_tipo_operacion=3705 AND id_estatus_mov='A' then 'BARRIDOS EJECUTADOS' ");
		sbSql.append(" when id_tipo_operacion=3806 AND id_estatus_mov='L' then 'FONDEOS SOLICITUDES' ");
		sbSql.append(" when id_tipo_operacion=3706 AND id_estatus_mov='A' then 'FONDEOS EJECUTADOS' end as tipo");
		sbSql.append(" from hist_movimiento m, empresa e, cat_cta_banco cb,empresa ee,cat_banco cbb,cat_banco ccbb ");
		sbSql.append(" where (id_tipo_operacion in(3806,3706) AND id_tipo_movto='E') ");
		sbSql.append(" AND cb.id_chequera=m.id_chequera_benef ");
		sbSql.append(" AND cbb.id_banco=m.id_banco ");
		sbSql.append(" AND ccbb.id_banco=m.id_banco_benef ");
		sbSql.append(" AND cb.id_banco=m.id_banco_benef ");
		sbSql.append(" AND cb.no_empresa=ee.no_empresa ");
		sbSql.append(" AND m.no_empresa=e.no_empresa ");
		sbSql.append(" AND id_estatus_mov in('L','A') ");
		if (noEmpresa > 0)
			sbSql.append(" AND m.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresa));
		sbSql.append(" AND m.fec_valor >= convert(datetime,'" + Utilerias.validarCadenaSQL(fecha) + "', 103) ");
		sbSql.append(" AND m.fec_valor < convert(datetime,'" + Utilerias.validarCadenaSQL(fecha) + "', 103) + 1");
		sbSql.append(" AND m.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + ")");
		sbSql.append(" Union All ");
		sbSql.append(" select m.id_divisa,no_docto, importe, m.no_empresa, ");
		sbSql.append(" concepto,beneficiario,fec_valor, e.nom_empresa,ee.nom_empresa as desc_empresa_benef, ");
		sbSql.append(" m.id_banco,cbb.desc_banco as desc_banco_benef,m.id_chequera as id_chequera_benef, ");
		sbSql.append(" m.id_banco_benef, ccbb.desc_banco as desc_banco,id_chequera_benef as id_chequera,");
		sbSql.append(" cb.no_empresa as no_empresa_benef,sucursal,");
		sbSql.append(" case when id_tipo_operacion=3805 AND id_estatus_mov='L' then 'BARRIDOS SOLICITUDES'");
		sbSql.append(" when id_tipo_operacion=3705 AND id_estatus_mov='A' then 'BARRIDOS EJECUTADOS' ");
		sbSql.append(" when id_tipo_operacion=3806 AND id_estatus_mov='L' then 'FONDEOS SOLICITUDES'");
		sbSql.append(" when id_tipo_operacion=3706 AND id_estatus_mov='A' then 'FONDEOS EJECUTADOS' end as tipo");
		sbSql.append(" from hist_movimiento m, empresa e, cat_cta_banco cb,empresa ee,cat_banco cbb,cat_banco ccbb ");
		sbSql.append(" where (id_tipo_operacion in(3805,3705) AND id_tipo_movto='I')");
		sbSql.append(" AND cb.id_chequera=m.id_chequera_benef ");
		sbSql.append(" AND cbb.id_banco=m.id_banco ");
		sbSql.append(" AND ccbb.id_banco=m.id_banco_benef ");
		sbSql.append(" AND cb.id_banco=m.id_banco_benef ");
		sbSql.append(" AND cb.no_empresa=ee.no_empresa ");
		sbSql.append(" AND m.no_empresa=e.no_empresa ");
		sbSql.append(" AND id_estatus_mov in('L','A') ");
		if (noEmpresa > 0)
			sbSql.append(" AND m.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresa));
		sbSql.append(" AND m.fec_valor >= convert(datetime,'" + Utilerias.validarCadenaSQL(fecha) + "', 103) ");
		sbSql.append(" AND m.fec_valor < convert(datetime,'" + Utilerias.validarCadenaSQL(fecha) + "', 103) + 1");
		sbSql.append(" AND m.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + ")");
		System.out.println("Query"+sbSql.toString());
		return this.getJdbcTemplate().queryForList(sbSql.toString());
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.dao.BarridosFondeosDao#obtenerReporteBarridosFondeos(int, int, java.lang.String)
	 */
	@Override
	public List<Map<String, String>> obtenerReporteBarridosFondeosStr(int noEmpresa, int idUsuario, String fecha) {
		StringBuffer sbSql = new StringBuffer();
		
		sbSql.append(" SELECT m.id_divisa, no_docto, convert(char,importe) importe, ");
		sbSql.append(" concepto, beneficiario, e.nom_empresa, ee.nom_empresa as desc_empresa_benef,");
		sbSql.append(" cbb.desc_banco, m.id_chequera, ccbb.desc_banco as desc_banco_benef,");
		sbSql.append(" id_chequera_benef, ");
		sbSql.append(" case when id_tipo_operacion = 3805 and id_estatus_mov = 'L' then 'BARRIDOS SOLICITUDES'");
		sbSql.append(" when id_tipo_operacion = 3705 and id_estatus_mov = 'A' then 'BARRIDOS EJECUTADOS'");
		sbSql.append(" when id_tipo_operacion = 3806 and id_estatus_mov = 'L' then 'FONDEOS SOLICITUDES'");
		sbSql.append(" when id_tipo_operacion = 3706 and id_estatus_mov = 'A' then 'FONDEOS EJECUTADOS' end as tipo");
		sbSql.append(" FROM movimiento m, empresa e, cat_cta_banco cb,empresa ee,cat_banco cbb,cat_banco ccbb ");
		sbSql.append(" WHERE (id_tipo_operacion in(3806,3706) and id_tipo_movto='E')");
		sbSql.append(" AND cb.id_chequera=m.id_chequera_benef ");
		sbSql.append(" AND cbb.id_banco=m.id_banco ");
		sbSql.append(" AND ccbb.id_banco=m.id_banco_benef ");
		sbSql.append(" AND cb.id_banco=m.id_banco_benef ");
		sbSql.append(" AND cb.no_empresa=ee.no_empresa ");
		sbSql.append(" AND m.no_empresa=e.no_empresa");
		sbSql.append(" AND id_estatus_mov in('L','A') ");
		if (noEmpresa > 0)
			sbSql.append(" AND m.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresa));
		sbSql.append(" AND m.fec_valor >= convert(datetime,'" + Utilerias.validarCadenaSQL(fecha) + "', 103) ");
		sbSql.append(" AND m.fec_valor < convert(datetime,'" + Utilerias.validarCadenaSQL(fecha) + "', 103) + 1");
		sbSql.append(" AND m.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + ")");
		sbSql.append(" Union All ");
		sbSql.append(" select m.id_divisa,no_docto, convert(char,importe), ");
		sbSql.append(" concepto,beneficiario, e.nom_empresa,ee.nom_empresa as desc_empresa_benef,");
		sbSql.append(" cbb.desc_banco as desc_banco_benef,m.id_chequera as id_chequera_benef, ");
		sbSql.append(" ccbb.desc_banco as desc_banco,id_chequera_benef as id_chequera, ");
		sbSql.append(" case when id_tipo_operacion=3805 AND id_estatus_mov='L' then 'BARRIDOS SOLICITUDES' ");
		sbSql.append(" when id_tipo_operacion=3705 AND id_estatus_mov='A' then 'BARRIDOS EJECUTADOS'");
		sbSql.append(" when id_tipo_operacion=3806 AND id_estatus_mov='L' then 'FONDEOS SOLICITUDES'");
		sbSql.append(" when id_tipo_operacion=3706 AND id_estatus_mov='A' then 'FONDEOS EJECUTADOS' end as tipo");
		sbSql.append(" from movimiento m, empresa e, cat_cta_banco cb,empresa ee,cat_banco cbb,cat_banco ccbb ");
		sbSql.append(" where (id_tipo_operacion in (3805,3705) AND id_tipo_movto='I') ");
		sbSql.append(" AND cb.id_chequera=m.id_chequera_benef ");
		sbSql.append(" AND cbb.id_banco=m.id_banco ");
		sbSql.append(" AND ccbb.id_banco=m.id_banco_benef ");
		sbSql.append(" AND cb.id_banco=m.id_banco_benef ");
		sbSql.append(" AND cb.no_empresa=ee.no_empresa ");
		sbSql.append(" AND m.no_empresa=e.no_empresa ");
		sbSql.append(" AND id_estatus_mov in('L','A') ");
		if (noEmpresa > 0)
			sbSql.append(" AND m.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresa));
		sbSql.append(" AND m.fec_valor >= convert(datetime,'" + Utilerias.validarCadenaSQL(fecha) + "', 103) ");
		sbSql.append(" AND m.fec_valor < convert(datetime,'" + Utilerias.validarCadenaSQL(fecha) + "', 103) + 1");
		sbSql.append(" AND m.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + ")");
		sbSql.append(" Union All ");
		sbSql.append(" select m.id_divisa,no_docto, convert(char,importe), ");
		sbSql.append(" concepto,beneficiario,e.nom_empresa,ee.nom_empresa as desc_empresa_benef, ");
		sbSql.append(" cbb.desc_banco,m.id_chequera, ccbb.desc_banco as desc_banco_benef,");
		sbSql.append(" id_chequera_benef, ");
		sbSql.append(" case when id_tipo_operacion=3805 AND id_estatus_mov='L' then 'BARRIDOS SOLICITUDES' ");
		sbSql.append(" when id_tipo_operacion=3705 AND id_estatus_mov='A' then 'BARRIDOS EJECUTADOS' ");
		sbSql.append(" when id_tipo_operacion=3806 AND id_estatus_mov='L' then 'FONDEOS SOLICITUDES' ");
		sbSql.append(" when id_tipo_operacion=3706 AND id_estatus_mov='A' then 'FONDEOS EJECUTADOS' end as tipo");
		sbSql.append(" from hist_movimiento m, empresa e, cat_cta_banco cb,empresa ee,cat_banco cbb,cat_banco ccbb ");
		sbSql.append(" where (id_tipo_operacion in(3806,3706) AND id_tipo_movto='E') ");
		sbSql.append(" AND cb.id_chequera=m.id_chequera_benef ");
		sbSql.append(" AND cbb.id_banco=m.id_banco ");
		sbSql.append(" AND ccbb.id_banco=m.id_banco_benef ");
		sbSql.append(" AND cb.id_banco=m.id_banco_benef ");
		sbSql.append(" AND cb.no_empresa=ee.no_empresa ");
		sbSql.append(" AND m.no_empresa=e.no_empresa ");
		sbSql.append(" AND id_estatus_mov in('L','A') ");
		if (noEmpresa > 0)
			sbSql.append(" AND m.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresa));
		sbSql.append(" AND m.fec_valor >= convert(datetime,'" + Utilerias.validarCadenaSQL(fecha) + "', 103) ");
		sbSql.append(" AND m.fec_valor < convert(datetime,'" + Utilerias.validarCadenaSQL(fecha) + "', 103) + 1");
		sbSql.append(" AND m.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + ")");
		sbSql.append(" Union All ");
		sbSql.append(" select m.id_divisa,no_docto, convert(char,importe), ");
		sbSql.append(" concepto,beneficiario, e.nom_empresa,ee.nom_empresa as desc_empresa_benef, ");
		sbSql.append(" cbb.desc_banco as desc_banco_benef,m.id_chequera as id_chequera_benef, ");
		sbSql.append(" ccbb.desc_banco as desc_banco,id_chequera_benef as id_chequera,");
		sbSql.append(" case when id_tipo_operacion=3805 AND id_estatus_mov='L' then 'BARRIDOS SOLICITUDES'");
		sbSql.append(" when id_tipo_operacion=3705 AND id_estatus_mov='A' then 'BARRIDOS EJECUTADOS' ");
		sbSql.append(" when id_tipo_operacion=3806 AND id_estatus_mov='L' then 'FONDEOS SOLICITUDES'");
		sbSql.append(" when id_tipo_operacion=3706 AND id_estatus_mov='A' then 'FONDEOS EJECUTADOS' end as tipo");
		sbSql.append(" from hist_movimiento m, empresa e, cat_cta_banco cb,empresa ee,cat_banco cbb,cat_banco ccbb ");
		sbSql.append(" where (id_tipo_operacion in(3805,3705) AND id_tipo_movto='I')");
		sbSql.append(" AND cb.id_chequera=m.id_chequera_benef ");
		sbSql.append(" AND cbb.id_banco=m.id_banco ");
		sbSql.append(" AND ccbb.id_banco=m.id_banco_benef ");
		sbSql.append(" AND cb.id_banco=m.id_banco_benef ");
		sbSql.append(" AND cb.no_empresa=ee.no_empresa ");
		sbSql.append(" AND m.no_empresa=e.no_empresa ");
		sbSql.append(" AND id_estatus_mov in('L','A') ");
		if (noEmpresa > 0)
			sbSql.append(" AND m.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresa));
		sbSql.append(" AND m.fec_valor >= convert(datetime,'" + Utilerias.validarCadenaSQL(fecha) + "', 103) ");
		sbSql.append(" AND m.fec_valor < convert(datetime,'" + Utilerias.validarCadenaSQL(fecha) + "', 103) + 1");
		sbSql.append(" AND m.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + ")");
		System.out.println("query"+sbSql.toString());
		return this.getJdbcTemplate().query(sbSql.toString(), new RowMapper<Map<String, String>>()
				{
			public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException
			{
				Map<String, String> campos = new HashMap<String, String>();
				campos.put("id_divisa", rs.getString("id_divisa"));
				campos.put("no_docto", rs.getString("no_docto"));
				campos.put("importe", rs.getString("importe"));
				campos.put("concepto", rs.getString("concepto"));
				campos.put("nom_empresa", rs.getString("nom_empresa"));
				campos.put("desc_empresa_benef", rs.getString("desc_empresa_benef"));
				campos.put("desc_banco_benef", rs.getString("desc_banco_benef"));
				campos.put("id_chequera_benef", rs.getString("id_chequera_benef"));
				campos.put("desc_banco", rs.getString("desc_banco"));
				campos.put("id_chequera", rs.getString("id_chequera"));
				campos.put("tipo", rs.getString("tipo"));
				return campos;
			}				
			
		});
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.dao.BarridosFondeosDao#obtenerReporteCuadre(int, int, java.lang.String)
	 */
	@Override
	public List<Map<String, String>> obtenerReporteCuadre(int noEmpresa, int idUsuario, String fecha) {
		StringBuffer sbSql = new StringBuffer();
		
		/*sbSql.append("SELECT a.no_empresa_padre, a.id_chequera_padre, b.nom_empresa, ");
		sbSql.append("a.id_chequera_hijo, c.desc_banco, a.no_empresa_hijo, d.desc_divisa, a.importe_requerido, ");
		sbSql.append("a.importe_fondeo, no_fondeo ");
		sbSql.append("FROM control_fondeos a, empresa b, cat_banco c, cat_divisa d ");
		sbSql.append("WHERE a.no_empresa_hijo = b.no_empresa ");
		sbSql.append("AND a.id_banco_hijo = c.id_banco ");
		sbSql.append("AND a.id_divisa = d.id_divisa ");
		if (noEmpresa > 0)
			sbSql.append("AND a.no_empresa_padre = " + Utilerias.validarCadenaSQL(noEmpresa) + " ");
		sbSql.append("AND a.fec_valor >= to_date('" + Utilerias.validarCadenaSQL(fecha) + "', 'dd/mm/yyyy') ");
		sbSql.append("AND a.fec_valor < to_date('" + Utilerias.validarCadenaSQL(fecha) + "', 'dd/mm/yyyy') + 1 ");
		sbSql.append("AND a.no_empresa_padre in (SELECT no_empresa FROM usuario_empresa ");
		sbSql.append("WHERE no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + ") ");
		sbSql.append("ORDER BY 1,2,3,4,no_fondeo ");*/
		
		sbSql.append("SELECT distinct b.no_empresa_padre, b.id_chequera_padre, be.nom_empresa, ");
		sbSql.append("d.id_chequera, c.desc_banco, be.no_empresa, e.desc_divisa, b.importe_requerido, ");
		sbSql.append("b.importe_fondeo, b.no_fondeo, ' ' no_docto, ' ' beneficiario, 0 importe ");
		sbSql.append("FROM arbol_empresa_fondeo a, control_fondeos b, empresa be, cat_banco c, "); 
		sbSql.append("     cat_cta_banco d, cat_divisa e ");
		sbSql.append("WHERE a.empresa_destino = be.no_empresa ");
		sbSql.append("AND a.empresa_destino = d.no_empresa ");
		sbSql.append("AND d.tipo_chequera in ('P','M') ");
		sbSql.append("AND d.id_banco = c.id_banco ");
		sbSql.append("AND d.id_divisa = e.id_divisa ");
		sbSql.append("AND a.orden > 0 ");
	//	sbSql.append("AND d.b_traspaso = 'S' ");
		sbSql.append("AND a.tipo_arbol = " + Utilerias.validarCadenaSQL(noEmpresa) + " ");
		sbSql.append("AND d.no_empresa = b.no_empresa_hijo ");
		sbSql.append("AND d.id_chequera = b.id_chequera_hijo ");
		sbSql.append("AND b.fec_valor >= convert(datetime,'" + Utilerias.validarCadenaSQL(fecha) + "', 103) ");
		sbSql.append("AND b.fec_valor < convert(datetime,'" + Utilerias.validarCadenaSQL(fecha) + "', 103) + 1 ");
		sbSql.append("AND b.id_tipo_arbol = " + Utilerias.validarCadenaSQL(noEmpresa) + " "); 
		sbSql.append("UNION ALL ");
		sbSql.append("SELECT ae.empresa_origen no_empresa_padre, '' id_chequera_padre, e.nom_empresa, m.id_chequera, c.desc_banco, ");
		sbSql.append("m.no_empresa, d.desc_divisa, null importe_requerido, 0 importe_fondeo, null no_fondeo, ");
		sbSql.append("m.no_docto, m.beneficiario, m.importe ");
		sbSql.append("FROM movimiento m, cat_cta_banco ccb, empresa e, arbol_empresa_fondeo ae, cat_banco c, cat_divisa d ");
		sbSql.append("WHERE ( ( m.id_tipo_operacion = 3200 "); 
		sbSql.append("AND m.id_estatus_mov not in ('A','X','Y','Z','W','G') "); 
		sbSql.append("        ) "); 
		sbSql.append("       OR ( m.id_tipo_operacion between 3700 and 3799 "); 
		sbSql.append("            AND id_tipo_operacion not in (3705, 3709) "); 
		sbSql.append("            AND m.id_estatus_mov = 'A' "); 
		sbSql.append("             ) "); 
		sbSql.append("       OR ( m.id_tipo_operacion between 3800 and 3899 "); 
		sbSql.append("            AND m.id_tipo_operacion not in (3818, 3805, 3809) "); 
		sbSql.append("            AND m.id_estatus_mov = 'L' ");
		sbSql.append("          ) "); 
		sbSql.append("      ) "); 
		sbSql.append("AND m.id_tipo_movto = 'E' "); 
		sbSql.append("AND m.no_empresa = e.no_empresa "); 
		sbSql.append("AND m.no_empresa = ae.empresa_destino ");
		sbSql.append("AND m.id_banco = ccb.id_banco ");
		sbSql.append("AND m.id_chequera = ccb.id_chequera ");
		sbSql.append("AND m.fec_valor >= convert(datetime,'" + Utilerias.validarCadenaSQL(fecha) + "', 103) ");
		sbSql.append("AND m.fec_valor < convert(datetime,'" + Utilerias.validarCadenaSQL(fecha) + "', 103) + 1 ");
		sbSql.append("AND ccb.id_divisa = d.id_divisa ");
		sbSql.append("AND ccb.no_empresa = m.no_empresa ");
		sbSql.append("AND ccb.id_banco = c.id_banco ");
		sbSql.append("AND ae.tipo_arbol = " + Utilerias.validarCadenaSQL(noEmpresa) + " ");
		sbSql.append("ORDER BY 3,4,11,12,13,10 "); 
		System.out.println("query"+sbSql.toString());
		return this.getJdbcTemplate().query(sbSql.toString(), new RowMapper<Map<String, String>>()
				{
			public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException
			{
				Map<String, String> campos = new HashMap<String, String>();
				campos.put("no_empresa_padre", rs.getString("no_empresa_padre"));
				campos.put("id_chequera_padre", rs.getString("id_chequera_padre"));
				campos.put("nom_empresa", rs.getString("nom_empresa"));
				campos.put("id_chequera_hijo", rs.getString("id_chequera"));
				campos.put("desc_banco", rs.getString("desc_banco"));
				campos.put("desc_divisa", rs.getString("desc_divisa"));
				campos.put("importe_requerido", rs.getString("importe_requerido"));				
				campos.put("importe_fondeo", rs.getString("importe_fondeo"));
				campos.put("no_fondeo", rs.getString("no_fondeo"));
				campos.put("no_docto", rs.getString("no_docto"));
				campos.put("beneficiario", rs.getString("beneficiario"));
				campos.put("importe", rs.getString("importe"));
				return campos;
			}				
		});
	}

	@Override
	public String getIdChequeraHijo(int noEmpresaDestino, String idDivisa, int idBanco, int orden) {
		
		StringBuffer sbSql = new StringBuffer();
		
		List<String> chequera = new ArrayList<String>();
			
		sbSql.append("\n SELECT id_chequera "); 
		sbSql.append("\n FROM cat_cta_banco ");
		sbSql.append("\n WHERE  no_empresa = " + noEmpresaDestino + " ");
		sbSql.append("\n AND id_banco = " + idBanco + " ");
		if (orden < 2) {
			sbSql.append("\n AND b_traspaso in ('S') ");			
		} else {
			sbSql.append("\n  AND tipo_chequera in ('P', 'M')  ");
		}
		sbSql.append("\n AND id_divisa = '" + idDivisa + "' ");
		sbSql.append("\n ORDER BY id_chequera ");
		
		System.out.println("Query Chequeras hijo: " + sbSql.toString());
		chequera = jdbcTemplate.query(sbSql.toString(), new RowMapper<String>()
				{
			public String mapRow(ResultSet rs, int idx) throws SQLException
			{
				return rs.getString("id_chequera");
			}	});
		return chequera.get(0).toString();
	}

	@Override
	public int getIdBanco(String lsChequera) {
		StringBuffer sql = new StringBuffer();		
		try {
			sql.append(" SELECT id_banco ");
			sql.append(" FROM cat_cta_banco ");
			sql.append(" WHERE id_chequera = '" + lsChequera + "'");
			
			return jdbcTemplate.queryForInt(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public Double getSaldoEmpresa(Integer noEmpresa, Integer idTipoSaldo) {
		Double saldo = 0D;
		
		StringBuffer sbSql = new StringBuffer();		
		try {
		    sbSql.append("\n select importe ");
		    sbSql.append("\n from saldo ");
		    sbSql.append("\n where no_empresa = "+ noEmpresa);
		    sbSql.append("\n and id_tipo_saldo = "+ idTipoSaldo);
		    
		    
			return jdbcTemplate.queryForObject(sbSql.toString(), Double.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return saldo;
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.dao.BarridosFondeosDao#buscarCuentaEmpresa(int)
	 */
	@Override
	public Integer buscarCuentaEmpresa(Integer noEmpresa, String divisa) {
		StringBuffer sbSql = new StringBuffer();
		int resultado;
		
	    sbSql.append(" SELECT c.no_cuenta  ");
	    sbSql.append(" FROM cuenta c  ");
	    sbSql.append(" join cat_divisa d  ");
	    sbSql.append(" on c.no_linea = d.id_divisa_soin ");
	    sbSql.append(" WHERE c.no_empresa = "+ noEmpresa);
	    sbSql.append(" AND d.id_divisa = '"+ divisa +"'");
	    sbSql.append(" AND c.id_tipo_cuenta = 'P' ");
	    
		System.out.println("query"+sbSql.toString());
		resultado = this.getJdbcTemplate().queryForInt(sbSql.toString());
		return resultado;
	}

	/* (non-Javadoc)
	 * @see com.webset.set.barridosfondeos.dao.BarridosFondeosDao#buscarCuentaEmpresa(int)
	 */
	@Override
	public int consultarCuentaEmpresa(int noEmpresa) {
		StringBuffer sbSql = new StringBuffer();
		int resultado;
		
		sbSql.append("select no_cuenta_emp from empresa where no_empresa = ? ");
		System.out.println("query"+sbSql.toString());
		resultado = this.getJdbcTemplate().queryForInt(sbSql.toString(), new Object[] {Utilerias.validarCadenaSQL(noEmpresa)});
		return resultado;
	}
	
	public String consultarChequeraTraspaso(Integer noEmpresa, String divisa){
		StringBuffer sbSql = new StringBuffer();
		String chequera = null;
		
		sbSql.append("select id_chequera from cat_cta_banco where no_empresa = ? and id_divisa = ? and b_traspaso = 'S'");

		List<String> chequeras = this.getJdbcTemplate().queryForList(sbSql.toString(), new Object[] {Utilerias.validarCadenaSQL(noEmpresa), divisa}, String.class);

		if(chequeras != null)
			chequera = chequeras.get(0);
		return chequera;
	}

	
	@Override
	public int buscaAbono(int tipoSaldo) {
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("select cargos from clasificacion_tipo_saldo where tipo_saldo=" + tipoSaldo);
			System.out.println(sb.toString());
		} catch (Exception e) {
			System.out.println(e);
		}
		return jdbcTemplate.queryForInt(sb.toString());
	}




}
