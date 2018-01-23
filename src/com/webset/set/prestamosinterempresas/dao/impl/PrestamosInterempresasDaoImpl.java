package com.webset.set.prestamosinterempresas.dao.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.webset.set.prestamosinterempresas.dao.PrestamosInterempresasDao;
import com.webset.set.prestamosinterempresas.dto.ArbolEmpresaDto;
import com.webset.set.prestamosinterempresas.dto.CuentaDto;
import com.webset.set.prestamosinterempresas.dto.HistSaldoDto;
import com.webset.set.prestamosinterempresas.dto.InteresPrestamoDto;
import com.webset.set.prestamosinterempresas.dto.ParamComunDto;
import com.webset.set.prestamosinterempresas.dto.ParamInteresPresNoDoc;
import com.webset.set.prestamosinterempresas.dto.ParamRepIntNetoDto;
import com.webset.set.prestamosinterempresas.dto.PersonaDto;
import com.webset.set.prestamosinterempresas.dto.RetInteresPresNoDoc;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.ParametroDto;
import com.webset.set.utilerias.dto.TmpEdoCuentaDto;

@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
public class PrestamosInterempresasDaoImpl implements PrestamosInterempresasDao{

	private JdbcTemplate jdbcTemplate;
	private Bitacora bitacora =  new Bitacora();
	private StringBuffer sbSql;
	private Funciones funciones = new Funciones();
	
	/**
	 * FunSQLComboEmpRaiz
	 * @param bExistentes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarEmpresasRaiz(boolean bExistentes)
	{
		sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpresaRaiz = new ArrayList<LlenaComboGralDto>();
		try
		{
			if(bExistentes)
			{
				sbSql.append("Select distinct e.no_empresa as ID, e.nom_empresa as describ ");
		        sbSql.append("\n From empresa e, arbol_empresa a ");
		        sbSql.append("\n WHERE e.no_empresa = a.empresa_raiz ");
			}
			else
			{
				sbSql.append("Select distinct e.no_empresa as ID, e.nom_empresa as describ ");
		        sbSql.append("\n From empresa e");
		        sbSql.append("\n WHERE e.no_empresa not in");
		        sbSql.append("\n     (select distinct empresa_raiz");
		        sbSql.append("\n     from arbol_empresa)");
			}
		
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
	
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarEmpresasHijo(int iEmpRaiz)
	{
		List<LlenaComboGralDto> listEmpH = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSql = new StringBuffer();
		try
		{
			sbSql.append("SELECT no_empresa, nom_empresa From empresa");
		    sbSql.append("\n WHERE no_empresa not in");
		    sbSql.append("\n (Select no_empresa from arbol_empresa");
		    sbSql.append("\n where ");
		    sbSql.append("\n empresa_raiz=" + iEmpRaiz + ")");
		    
		    listEmpH = jdbcTemplate.query(sbSql.toString(), new RowMapper()
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
	
	/**
	 * FunSQLPadre
	 * @param iEmpresaRaiz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarEmpresaPadre(int iEmpresaRaiz)
	{
		sbSql = new StringBuffer();
		List<LlenaComboGralDto> listPadre = new ArrayList<LlenaComboGralDto>();
		try
		{
			if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
			{
				if(ConstantesSet.gsDBM.equals("DB2"))
					sbSql.append("SELECT DISTINCT A.padre, CAST(A.padre AS VARCHAR(80)) || ':' ||");
				else
					sbSql.append("SELECT DISTINCT A.padre, CAST(A.padre AS VARCHAR) || ':' ||");
			}
			else if(ConstantesSet.gsDBM.equals("SQL SERVER"))
				sbSql.append("SELECT DISTINCT A.padre, CAST(A.padre AS VARCHAR) + ':' +");
		        
            sbSql.append("\n E.nom_empresa ");
            sbSql.append("\n AS nom_empresa");
            sbSql.append("\n FROM arbol_empresa A,empresa E");
            sbSql.append("\n WHERE  A.padre = E.no_empresa");
            sbSql.append("\n AND A.padre IS NOT NULL");
            sbSql.append("\n AND a.empresa_raiz = " + iEmpresaRaiz);
		    
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
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresaPadre");
		}
		return listPadre;
	}
	
	/**
	 * FunSQLHijos
	 * @param iEmpresaRaiz
	 * @param iEmpPadre
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarEmpresasHijos(int iEmpresaRaiz, int iEmpPadre)
	{
		sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpHijo = new ArrayList<LlenaComboGralDto>();
		try
		{
			if(ConstantesSet.gsDBM.equals("SQL SERVER"))
			{
				sbSql.append("SELECT DISTINCT A.hijo, CAST(A.hijo AS VARCHAR) + ':' + ");
	            sbSql.append("\n E.nom_empresa + ':' + CAST(COALESCE(A.monto_maximo,0.0) AS VARCHAR)");
			}
			else if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
			{
				sbSql.append("SELECT DISTINCT A.hijo, CAST(A.hijo AS VARCHAR) || ':' || ");
				sbSql.append("\n E.nom_empresa || ':' || CAST(COALESCE(A.monto_maximo,0.0) AS VARCHAR)");
			}
			else if(ConstantesSet.gsDBM.equals("DB2"))
			{
				sbSql.append("SELECT DISTINCT A.hijo, CAST(A.hijo AS VARCHAR(80)) || ':' || ");
	            sbSql.append("\n E.nom_empresa || ':' || CAST(COALESCE(A.monto_maximo,0.0) AS VARCHAR(18))");
			}
			
	    
            sbSql.append("\n AS nom_empresa ");
            sbSql.append("\n FROM arbol_empresa A,empresa E ");
            sbSql.append("\n WHERE  A.hijo = E.no_empresa ");
            sbSql.append("\n AND A.padre = " + iEmpPadre);
            sbSql.append("\n AND A.hijo IS NOT NULL");
            sbSql.append("\n AND A.empresa_raiz = " + iEmpresaRaiz);
            sbSql.append("\n AND A.nieto IS NULL"); 
            
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
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasHijos");
		}
		return listEmpHijo;
	}
	
	/**
	 * FunSQLNietos
	 * @param iEmpresaRaiz
	 * @param iEmpHijo
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarEmpresasNietos(int iEmpresaRaiz, int iEmpHijo)
	{
		sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpNieto = new ArrayList<LlenaComboGralDto>();
		try
		{
			if(ConstantesSet.gsDBM.equals("SQL SERVER"))
			{
				sbSql.append("SELECT DISTINCT A.nieto, CAST(A.nieto AS VARCHAR) + ':' + ");
				sbSql.append("\n E.nom_empresa + ':' + CAST(COALESCE(A.monto_maximo,0.0) AS VARCHAR)");
			}
			else if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
			{
				sbSql.append("SELECT DISTINCT A.nieto, CAST(A.nieto AS VARCHAR) || ':' || ");
		        sbSql.append("\n E.nom_empresa || ':' || CAST(COALESCE(A.monto_maximo,0.0) AS VARCHAR)");
			}
			else if(ConstantesSet.gsDBM.equals("DB2"))
			{
				sbSql.append("SELECT DISTINCT A.nieto, CAST(A.nieto AS VARCHAR(80)) || ':' || ");
	            sbSql.append("\n E.nom_empresa || ':' || CAST(COALESCE(A.monto_maximo,0.0) AS VARCHAR(18))");
			}
			
			sbSql.append("\n AS nom_empresa");
            sbSql.append("\n FROM arbol_empresa A,empresa E");
            sbSql.append("\n WHERE  A.nieto = E.no_empresa ");
            sbSql.append("\n AND A.hijo = " + iEmpHijo);
            sbSql.append("\n AND A.nieto IS NOT NULL");
            sbSql.append("\n AND A.empresa_raiz = " + iEmpresaRaiz);
            sbSql.append("\n AND A.bisnieto IS NULL");
            
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
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasNietos");
		}
		return listEmpNieto;
	}
	
	/**
	 * FunSQLBisnieto
	 * @param iEmpresaRaiz
	 * @param iEmpNieto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarEmpresasBisnietos(int iEmpresaRaiz, int iEmpNieto)
	{
		sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpBisnieto = new ArrayList<LlenaComboGralDto>();
		try
		{
			if(ConstantesSet.gsDBM.equals("SQL SERVER"))
			{
				sbSql.append("SELECT DISTINCT A.bisnieto, CAST(A.bisnieto AS VARCHAR) + ':' +");
	            sbSql.append("\n E.nom_empresa + ':' + CAST(COALESCE(A.monto_maximo,0.0) AS VARCHAR)");
			}
			else if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
			{
				sbSql.append("SELECT DISTINCT A.bisnieto, CAST(A.bisnieto AS VARCHAR) || ':' ||");
	            sbSql.append("\n E.nom_empresa || ':' || CAST(COALESCE(A.monto_maximo,0.0) AS VARCHAR)");
			}
			else if(ConstantesSet.gsDBM.equals("DB2"))
			{
				sbSql.append("SELECT DISTINCT A.bisnieto, CAST(A.bisnieto AS VARCHAR(80)) || ':' ||");
	            sbSql.append("\n E.nom_empresa || ':' || CAST(COALESCE(A.monto_maximo,0.0) AS VARCHAR(18))");
			}
			
            sbSql.append("\n AS nom_empresa");
            sbSql.append("\n FROM arbol_empresa A,empresa E");
            sbSql.append("\n WHERE  A.bisnieto = E.no_empresa");
            sbSql.append("\n AND A.nieto = " + iEmpNieto);
            sbSql.append("\n AND A.bisnieto IS NOT NULL");
            sbSql.append("\n AND A.empresa_raiz = " + iEmpresaRaiz);
            sbSql.append("\n AND A.tataranieto IS NULL");
            
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
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasBisnietos");
		}
		return listEmpBisnieto;
	}
	
	/**
	 * FunSQLTataranieto
	 * @param iEmpresaRaiz
	 * @param iEmpBisnieto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarEmpresasTataranietos(int iEmpresaRaiz, int iEmpBisnieto)
	{
		sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpTataranieto = new ArrayList<LlenaComboGralDto>();
		try
		{
			if(ConstantesSet.gsDBM.equals("SQL SERVER"))
			{
				sbSql.append("SELECT DISTINCT A.tataranieto, CAST(A.tataranieto AS VARCHAR) + ':' +");
	            sbSql.append("\n E.nom_empresa + ':' + CAST(COALESCE(A.monto_maximo,0.0) AS VARCHAR)");
			}
			else if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
			{
				sbSql.append("SELECT DISTINCT A.tataranieto, CAST(A.tataranieto AS VARCHAR) || ':' ||");
	            sbSql.append("\n E.nom_empresa || ':' || CAST(COALESCE(A.monto_maximo,0.0) AS VARCHAR)");
			}
			else if(ConstantesSet.gsDBM.equals("DB2"))
			{
				sbSql.append("SELECT DISTINCT A.tataranieto, CAST(A.tataranieto AS VARCHAR(80)) || ':' ||");
	            sbSql.append("\n E.nom_empresa || ':' || CAST(COALESCE(A.monto_maximo,0.0) AS VARCHAR(18))");
			}
			
			sbSql.append("\n AS nom_empresa");
            sbSql.append("\n FROM arbol_empresa A,empresa E");
            sbSql.append("\n WHERE  A.tataranieto = E.no_empresa");
            sbSql.append("\n AND A.bisnieto = " + iEmpBisnieto);
            sbSql.append("\n AND A.tataranieto IS NOT NULL");
            sbSql.append("\n AND A.empresa_raiz = " + iEmpresaRaiz);
            sbSql.append("\n AND A.chosno IS NULL");
            
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
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasTataranietos");
		}
		return listEmpTataranieto;
	}
	
	
	/**
	 * FunSQLChosno
	 * @param iEmpresaRaiz
	 * @param iEmpTataranieto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarEmpresasChosnoUno(int iEmpresaRaiz, int iEmpTataranieto)
	{
		sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpChosnoUno = new ArrayList<LlenaComboGralDto>();
		try
		{
			if(ConstantesSet.gsDBM.equals("SQL SERVER"))
			{
				sbSql.append("SELECT DISTINCT A.chosno, CAST(A.chosno AS VARCHAR) + ':' +");
                sbSql.append("\n E.nom_empresa + ':' + CAST(COALESCE(A.monto_maximo,0.0) AS VARCHAR)");
			}
			else if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
			{
				sbSql.append("SELECT DISTINCT A.chosno, CAST(A.chosno AS VARCHAR) || ':' ||");
                sbSql.append("\n E.nom_empresa || ':' || CAST(COALESCE(A.monto_maximo,0.0) AS VARCHAR)");
			}
			else if(ConstantesSet.gsDBM.equals("DB2"))
			{
				sbSql.append("SELECT DISTINCT A.chosno, CAST(A.chosno AS VARCHAR(80)) || ':' ||");
                sbSql.append("\n E.nom_empresa || ':' || CAST(COALESCE(A.monto_maximo,0.0) AS VARCHAR(18))");
			}
			
			sbSql.append("\n AS nom_empresa ");
            sbSql.append("\n FROM arbol_empresa A,empresa E");
            sbSql.append("\n WHERE  A.chosno = E.no_empresa");
            sbSql.append("\n AND A.tataranieto = " + iEmpTataranieto);
            sbSql.append("\n AND A.chosno IS NOT NULL");
            sbSql.append("\n AND A.empresa_raiz = " + iEmpresaRaiz);
            sbSql.append("\n AND A.chosno2 IS NULL");
            
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
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasChosnoUno");
		}
		return listEmpChosnoUno;
	}
	
	/**
	 * FunSQLChosno2
	 * @param iEmpresaRaiz
	 * @param iEmpChosno1
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarEmpresasChosnoDos(int iEmpresaRaiz, int iEmpChosno1)
	{
		sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpChosnoDos = new ArrayList<LlenaComboGralDto>();
		try
		{
			if(ConstantesSet.gsDBM.equals("SQL SERVER"))
			{
				sbSql.append("SELECT DISTINCT A.chosno2, CAST(A.chosno2 AS VARCHAR) + ':' +");
	            sbSql.append("\n E.nom_empresa + ':' + CAST(COALESCE(A.monto_maximo,0.0) AS VARCHAR)");
			}
			else if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
			{
				sbSql.append("SELECT DISTINCT A.chosno2, CAST(A.chosno2 AS VARCHAR) || ':' ||");
	            sbSql.append("\n E.nom_empresa || ':' || CAST(COALESCE(A.monto_maximo,0.0) AS VARCHAR)");
			}
			else if(ConstantesSet.gsDBM.equals("DB2"))
			{
				sbSql.append("SELECT DISTINCT A.chosno2, CAST(A.chosno2 AS VARCHAR(80)) || ':' ||");
	            sbSql.append("\n E.nom_empresa || ':' || CAST(COALESCE(A.monto_maximo,0.0) AS VARCHAR(18))");
			}
			
			sbSql.append("\n AS nom_empresa");
            sbSql.append("\n FROM arbol_empresa A,empresa E");
            sbSql.append("\n WHERE  A.chosno2 = E.no_empresa ");
            sbSql.append("\n AND A.chosno = " + iEmpChosno1);
            sbSql.append("\n AND A.chosno2 IS NOT NULL");
            sbSql.append("\n AND A.empresa_raiz = " + iEmpresaRaiz);
            sbSql.append("\n AND A.chosno3 IS NULL");
            
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
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasChosnoDos");
		}
		return listEmpChosnoDos;
	}
	
	/**
	 * FunSQLChosno3
	 * @param iEmpresaRaiz
	 * @param iEmpChosno2
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarEmpresasChosnoTres(int iEmpresaRaiz, int iEmpChosno2)
	{
		sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpChosnoTres = new ArrayList<LlenaComboGralDto>();
		try
		{
			if(ConstantesSet.gsDBM.equals("SQL SERVER"))
			{
				sbSql.append("Select distinct A.chosno3, cast(A.chosno3 as varchar) + ':' +");
	            sbSql.append("\n E.nom_empresa + ':' + CAST(COALESCE(A.monto_maximo,0.0) AS VARCHAR)");
			}
			else if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
			{
				sbSql.append("SELECT DISTINCT A.chosno3, CAST(A.chosno3 AS VARCHAR) || ':' ||");
	            sbSql.append("\n E.nom_empresa || ':' || CAST(COALESCE(A.monto_maximo,0.0) AS VARCHAR)");
			}
			else if(ConstantesSet.gsDBM.equals("DB2"))
			{
				sbSql.append("SELECT DISTINCT A.chosno3, CAST(A.chosno3 AS VARCHAR(80)) || ':' ||");
	            sbSql.append("\n E.nom_empresa || ':' || CAST(COALESCE(A.monto_maximo,0.0) AS VARCHAR(18))");
			}
			
			sbSql.append("\n AS nom_empresa");
            sbSql.append("\n FROM arbol_empresa A,empresa E");
            sbSql.append("\n WHERE  A.chosno3 = E.no_empresa");
            sbSql.append("\n AND A.chosno2 = " + iEmpChosno2);
            sbSql.append("\n AND A.chosno3 IS NOT NULL");
            sbSql.append("\n AND A.empresa_raiz = " + iEmpresaRaiz);
            sbSql.append("\n AND A.chosno4 IS NULL");
            
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
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasChosnoTres");
		}
		return listEmpChosnoTres;
	}
	
	
	/**
	 * FunSQLChosno4
	 * @param iEmpresaRaiz
	 * @param iEmpChosno3
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarEmpresasChosnoCuatro(int iEmpresaRaiz, int iEmpChosno3)
	{
		sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmpChosnoCuatro = new ArrayList<LlenaComboGralDto>();
		try
		{
			if(ConstantesSet.gsDBM.equals("SQL SERVER"))
			{
				sbSql.append("SELECT DISTINCT A.chosno4, CAST(A.chosno4 AS VARCHAR) + ':' +");
	            sbSql.append("\n E.nom_empresa + ':' + CAST(COALESCE(A.monto_maximo,0.0) AS VARCHAR)");
			}
			else if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
			{
				sbSql.append("SELECT DISTINCT A.chosno4, CAST(A.chosno4 AS VARCHAR)|| ':' ||");
	            sbSql.append("\n E.nom_empresa || ':' || CAST(COALESCE(A.monto_maximo,0.0) AS VARCHAR)");
			}
			else if(ConstantesSet.gsDBM.equals("DB2"))
			{
				sbSql.append("SELECT DISTINCT A.chosno4, CAST(A.chosno4 AS VARCHAR(80))|| ':' ||");
	            sbSql.append("\n E.nom_empresa || ':' || CAST(COALESCE(A.monto_maximo,0.0) AS VARCHAR(18))");
			}
			
			sbSql.append("\n AS nom_empresa");
            sbSql.append("\n FROM arbol_empresa A,empresa E");
            sbSql.append("\n WHERE  A.chosno4 = E.no_empresa ");
            sbSql.append("\n AND A.chosno3 = " + iEmpChosno3);
            sbSql.append("\n AND A.chosno4 IS NOT NULL");
            sbSql.append("\n AND A.empresa_raiz = " + iEmpresaRaiz);
            
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
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarEmpresasChosnoCuatro");
		}
		return listEmpChosnoCuatro;
	}
	
	public void eliminarTmpArbol(int iIdUsuario)
	{
		sbSql = new StringBuffer();
		try
		{
			sbSql.append(" Delete from tmp_arbol");
			sbSql.append("\n where usuario_alta=" + iIdUsuario);
			
			jdbcTemplate.update(sbSql.toString());
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:eliminarTmpArbol");
		}
	}
	
	/**
	 * M�todo para insertar en tmp_arbol
	 * @param iIdUsuario, usuario actual en session
	 * @param iSecuencia, orden de descendencia
	 * @param sNomEmpresa, nombre de la empresa 
	 * @param sTipo, tipo de descendencia ejem(padre, hijo, nieto, etc)
	 */
	public void insertarTmpArbol(int iIdUsuario, int iSecuencia, String sNomEmpresa, String sTipo)
	{
		sbSql = new StringBuffer();
		try
		{
			sbSql.append("insert into tmp_arbol ");
		    sbSql.append("\n (usuario_alta,secuencia," + sTipo + ")");
		    sbSql.append("\n values (" + iIdUsuario + "," + iSecuencia + ",'" + sNomEmpresa + "')");
		    
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
	@SuppressWarnings("unchecked")
	public List<Integer> consultarEmpArbolExis(int iIdEmpRaiz, int iIdEmpresa)
	{
		List<Integer> listEmpresa = new ArrayList<Integer>();
		sbSql = new StringBuffer();
		try
		{
		    sbSql.append("SELECT no_empresa ");
		    sbSql.append("\n FROM arbol_empresa ");
		    sbSql.append("\n WHERE no_empresa = " + iIdEmpresa);
		    sbSql.append("\n and empresa_raiz=" + iIdEmpRaiz);
		    
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
	
	/**
	 * FunSQLReporte1
	 * @param iIdUsuario
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarTmpArbol(int iIdUsuario)
	{
		sbSql = new StringBuffer();
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
	public void insertarArbolEmpresa(String sCampos, String sValoresCampos)
	{
		sbSql = new StringBuffer();
		try
		{
		    sbSql.append("Insert into arbol_empresa");
		    sbSql.append("\n (" + sCampos + " )");
		    sbSql.append("\n values (" + sValoresCampos + ")");
		    
		    jdbcTemplate.update(sbSql.toString());
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
		sbSql = new StringBuffer();
		try
		{
		    sbSql.append("UPDATE ");
		    sbSql.append("\n arbol_empresa ");
		    sbSql.append("\n SET b_padre = '" + sEstatus + "'");
		    sbSql.append("\n WHERE no_empresa = " + iIdEmpresa);
		    sbSql.append("\n and empresa_raiz=" + iIdEmpresaRaiz);
		    
		    jdbcTemplate.update(sbSql.toString());
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:actualizarArbEmpresaEstatusPadre");
		}
	}
	
	/**
	 * FunSQLSelectValidaPadre
	 * @param iIdEmpresaRaiz
	 * @param iIdEmpresa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ArbolEmpresaDto> consultarValidaPadre(int iIdEmpresaRaiz, int iIdEmpresa)
	{
		sbSql = new StringBuffer();
		List<ArbolEmpresaDto> listArbol = new ArrayList<ArbolEmpresaDto>();
		try
		{
			sbSql.append("SELECT b_padre ");
			sbSql.append("\n FROM arbol_empresa");
			sbSql.append("\n WHERE no_empresa = " + iIdEmpresa);
			sbSql.append("\n and empresa_raiz = " + iIdEmpresaRaiz);
			
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
	
	/**
	 * FunSQLSelect_Saldo_Empresa_Arbol
	 * @param iIdEmpresa
	 * @return
	 */
	public double consultarSaldoEmpresaArbol(int iIdEmpresa)
	{
		sbSql = new StringBuffer();
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
		sbSql = new StringBuffer();
		int iRegAfec = 0;
		try
		{
		    sbSql.append("DELETE from arbol_empresa");
		    sbSql.append("\n where no_empresa = " + iIdEmpresa);
		    sbSql.append("\n and empresa_raiz=" + iIdEmpresaRaiz);
		    
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
	@SuppressWarnings("unchecked")
	public List<String> consultarCampoArbolEmpresa(int iIdEmpresaRaiz, int iIdEmpresa)
	{
		sbSql = new StringBuffer();
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
	
	/**
	 * FunSQLSelectCuentaNodos
	 * @param iIdEmpresaRaiz
	 * @param iIdEmpresa
	 * @return
	 */
	public int consultarNumeroEmpresasArbol(int iIdEmpresaRaiz, int iIdEmpresa)
	{
		sbSql = new StringBuffer();
		int iNumEmp = 0;
		try
		{
		    sbSql.append("SELECT count(no_empresa) as contador");
		    sbSql.append("\n FROM arbol_empresa");
		    sbSql.append("\n WHERE  no_empresa = " + iIdEmpresa);
		    sbSql.append("\n and empresa_raiz=" + iIdEmpresaRaiz);
		    
		    iNumEmp = jdbcTemplate.queryForInt(sbSql.toString());
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarNumeroEmpresasArbol");
		}
		return iNumEmp;
	}
	
	//Inician m�todos de consulta para la forma SolicitudDeTraspasosDeCredito,
	//sin embargo se puede hacer uso de m�todos anteriores para pantallas posteriores y viceversa.
	/**
	 * FunSQLCombo7
	 * Este m�todo consulta la empresa concetradora para SolicitudeDeTraspasoDeCredito
	 * @param iIdUsuario
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarEmpresaConcentradora(int iIdUsuario)
	{
		sbSql = new StringBuffer();
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
	
	/**
	 * FunSQLCombo5
	 * FunSQLCombo26
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarBancosEmpresa(int iIdEmpresa, String sIdDivisa)
	{
		sbSql = new StringBuffer();
		List<LlenaComboGralDto> listBanc = new ArrayList<LlenaComboGralDto>();
		try
		{
		    sbSql.append("SELECT distinct cat_banco.id_banco, ");
		    sbSql.append("\n     cat_banco.desc_banco");
		    sbSql.append("\n FROM cat_banco,cat_cta_banco ");
		    sbSql.append("\n WHERE cat_cta_banco.id_banco = cat_banco.id_banco ");
		    sbSql.append("\n     and cat_cta_banco.no_empresa = " + iIdEmpresa);
		    sbSql.append("\n     and tipo_chequera <> 'U' ");
		    
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
	
	/**
	 * FunSQLCombo6
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarEmpresasArbol(int iIdEmpresa, String sCondicion)
	{
		sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmp = new ArrayList<LlenaComboGralDto>();
		try
		{
		    sbSql.append("Select ae.no_empresa, e.nom_empresa");
		    sbSql.append("\n From arbol_empresa ae, empresa e");
		    sbSql.append("\n Where e.no_empresa=ae.no_empresa ");
		    
		    if(!sCondicion.equals(""))
		        sbSql.append("\n and " + sCondicion + " = " + iIdEmpresa);
		    
		    sbSql.append("\n and ae.no_empresa <> " + iIdEmpresa);
		    
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
	
	/**
	 * FunSQLCombo1
	 * FunSQLCombo2
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarChequeras(int iIdEmpresa, int iIdBanco, String sIdDivisa)
	{
		sbSql = new StringBuffer();
		List<LlenaComboGralDto> listChe = new ArrayList<LlenaComboGralDto>();
		try
		{
		    sbSql.append("SELECT id_chequera");
		    sbSql.append("\n FROM cat_cta_banco");
		    sbSql.append("\n WHERE id_banco = " + iIdBanco);
		    sbSql.append("\n     and no_empresa =  " + iIdEmpresa);
		    sbSql.append("\n     and tipo_chequera <> 'U' ");
		    
		    if(!sIdDivisa.equals(""))
		    	 sbSql.append("\n     and id_divisa = '" + sIdDivisa + "'");
		    
		    listChe = jdbcTemplate.query(sbSql.toString(), new RowMapper()
	            {
            		public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
            		{
            			LlenaComboGralDto dtoCons = new LlenaComboGralDto();
            				dtoCons.setDescripcion(rs.getString("id_chequera"));
            			return dtoCons;
            		}
	            }
            );
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarChequeras");
		}
		return listChe;
	}
	
	public String consultarTipoDivisa(int iIdBanco, String sIdChequera)
	{
		String sIdDivisa = "";
		StringBuffer sbSql = new StringBuffer();
		try
		{
		    sbSql.append("Select id_divisa");
		    sbSql.append("\n from cat_cta_banco");
		    sbSql.append("\n where id_banco = " + iIdBanco);
		    sbSql.append("\n and id_chequera = '" + sIdChequera + "'");
		    System.out.println("Borrar divisa " + sbSql.toString());
		    sIdDivisa = jdbcTemplate.queryForObject(sbSql.toString(), String.class);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarTipoDivisa");
			e.printStackTrace();
		}
		return sIdDivisa;
	}
	
	/**
	 * FunSQLSelect32, FunSQLSelect34,FunSQLSelect23,FunSQLSelect21
	 * Este m�todo consulta el saldo final de cat_cta_banco de cada chequera
	 * @param iIdEmpresa
	 * @param iIdBanco
	 * @param sIdChequera
	 * @return
	 */
	public double consultarSaldoFinal(int iIdEmpresa, int iIdBanco, String sIdChequera)
	{
		sbSql = new StringBuffer();
		double uSaldoFinal = 0;
		BigDecimal bdSaldoFinal = new BigDecimal(0);
		try
		{
		    sbSql.append("Select saldo_final");
		    sbSql.append("\n from cat_cta_banco ");
		    sbSql.append("\n Where id_banco = " + iIdBanco);
		    sbSql.append("\n And id_chequera = '" + sIdChequera + "'");
		    sbSql.append("\n And no_empresa = " + iIdEmpresa);
		    
		    bdSaldoFinal = jdbcTemplate.queryForObject(sbSql.toString(), BigDecimal.class);
		    uSaldoFinal = bdSaldoFinal.doubleValue();
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarSaldoFinal");
		}
		return uSaldoFinal;
	}
	
	public double consultarImporteChequesXEntregar(int iIdEmpresa, int iIdBanco, String sIdChequera, String sIdDivisa)
	{
		double uImporte = 0;
		BigDecimal bdImporte = new BigDecimal(0);
		sbSql = new StringBuffer();
		try
		{
		    sbSql.append("Select coalesce(sum(importe), 0) as suma");
		    sbSql.append("\n from movimiento");
		    sbSql.append("\n where no_empresa=" + iIdEmpresa);
		    sbSql.append("\n and id_tipo_movto='E'");
		    sbSql.append("\n and id_divisa='" + sIdDivisa + "'");
		    sbSql.append("\n and id_chequera='" + sIdChequera + "' ");
		    sbSql.append("\n and id_banco=" + iIdBanco+ "");
		    sbSql.append("\n and ((id_estatus_mov in('I','R') and b_entregado='N')");
		    sbSql.append("\n     or (id_estatus_mov in ('P','J','T')))");
		    
		    bdImporte = jdbcTemplate.queryForObject(sbSql.toString(), BigDecimal.class);
		    uImporte = bdImporte.doubleValue();
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarImporteChequesXEntregar");
		}
		return uImporte;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarSaldoFinalYCoinversion(int iIdBanco, String sIdChequera, int iNoEmpresa,
																		int iNoEmpInv, String sIdDivisa)
	{
		List<Map<String, Object>> listSaldos = new ArrayList<Map<String, Object>>();
		sbSql = new StringBuffer();
		try
		{
		    sbSql.append("Select saldo_final ");
		    sbSql.append("\n, coalesce((Select coalesce(sum(importe),0) from saldo where no_empresa= " + iNoEmpInv);
		    sbSql.append("\n and id_tipo_saldo in (90,5) and no_cuenta=" + iNoEmpresa);
		    
		    if(ConstantesSet.gsDBM.equals("SYBASE"))
		        sbSql.append("\n and convert(varchar, no_linea) = (Select id_divisa_soin from cat_divisa where id_divisa='" + sIdDivisa + "'");
		    else if(ConstantesSet.gsDBM.equals("DB2"))
		        sbSql.append("\n and cast(no_linea as varchar(5))= (Select id_divisa_soin from cat_divisa where id_divisa='" + sIdDivisa + "'");
		    else
		        sbSql.append("\n and no_linea= (Select id_divisa_soin from cat_divisa where id_divisa='" + sIdDivisa + "'");
		    
		    sbSql.append("\n)) - (Select coalesce(importe,0)  from saldo where no_empresa=" + iNoEmpInv);
		    sbSql.append("\n and id_tipo_saldo=7 and no_cuenta=" + iNoEmpresa);
		    
		    if(ConstantesSet.gsDBM.equals("SYBASE"))
		        sbSql.append("\n and convert(varchar, no_linea) = (Select id_divisa_soin from cat_divisa where id_divisa='" + sIdDivisa + "'");
		    else if(ConstantesSet.gsDBM.equals("DB2"))
		        sbSql.append("\n and cast(no_linea as varchar(5))= (Select id_divisa_soin from cat_divisa where id_divisa='" + sIdDivisa + "'");
		    else
		        sbSql.append("\n and no_linea= (Select id_divisa_soin from cat_divisa where id_divisa='" + sIdDivisa + "'");
		    
		    sbSql.append("\n)), 0) as Saldo_coinv");
		    sbSql.append("\n From cat_cta_banco ");
		    sbSql.append("\n Where id_banco = " + iIdBanco);
		    sbSql.append("\n And id_chequera = '" + sIdChequera + "'");
		    sbSql.append("\n And no_empresa = " + iNoEmpresa);
		    
		    System.out.println("Saldo Final " + sbSql.toString());
		    
		    listSaldos = jdbcTemplate.query(sbSql.toString(), new RowMapper()
		    {
		    	public Map mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		Map<String, Object> mapCons = new HashMap<String, Object>();
		    			mapCons.put("saldoFinal", rs.getBigDecimal("saldo_final").doubleValue());
		    			mapCons.put("saldoCoinversion", rs.getBigDecimal("Saldo_coinv").doubleValue());
		    		return mapCons;
		    	}
		    });
			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarSaldoFinalYCoinversion");
			e.printStackTrace();
		}
		return listSaldos;
	}
	
	@SuppressWarnings("unchecked")
	public List<ArbolEmpresaDto> consultarParentesco(int iIdEmpresa)
	{
		sbSql = new StringBuffer();
		List<ArbolEmpresaDto> listParentesco = new ArrayList<ArbolEmpresaDto>();
		try
		{
		    sbSql.append("Select padre, hijo, nieto, bisnieto, tataranieto,");
		    sbSql.append("\n chosno, chosno2, chosno3, chosno4");
		    sbSql.append("\n from arbol_empresa");
		    sbSql.append("\n where no_empresa= " + iIdEmpresa);
		    System.out.println("Borrar parentesco : " + sbSql.toString());
		    listParentesco = jdbcTemplate.query(sbSql.toString(), new RowMapper()
		    {
		    	public ArbolEmpresaDto mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		ArbolEmpresaDto dtoCons = new ArbolEmpresaDto();
		    			dtoCons.setPadre(rs.getInt("padre"));
		    			dtoCons.setHijo(rs.getInt("hijo"));
		    			dtoCons.setNieto(rs.getInt("nieto"));
		    			dtoCons.setBisnieto(rs.getInt("bisnieto"));
		    			dtoCons.setTataranieto(rs.getInt("tataranieto"));
		    			dtoCons.setChosno(rs.getInt("chosno"));
		    			dtoCons.setChosno2(rs.getInt("chosno2"));
		    			dtoCons.setChosno3(rs.getInt("chosno3"));
		    			dtoCons.setChosno4(rs.getInt("chosno4"));
		    		return dtoCons;
		    	}
		    });
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarParentesco");
		}
		return listParentesco;
	}
	
	/**
	 * FunSQLSelect40
	 * @param iIdEmpresa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarBancoChequera(int iIdEmpresa)
	{
		sbSql = new StringBuffer();
		List<LlenaComboGralDto> listBancChe = new ArrayList<LlenaComboGralDto>();
		try
		{
		    sbSql.append("Select id_chequera,id_banco");
		    sbSql.append("\n from cat_cta_banco");
		    sbSql.append("\n where no_empresa=" + iIdEmpresa);
		    sbSql.append("\n and b_traspaso = 'S'");
		    
		    listBancChe = jdbcTemplate.query(sbSql.toString(), new RowMapper()
	            {
            		public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
            		{
            			LlenaComboGralDto dtoCons = new LlenaComboGralDto();
            				dtoCons.setId(rs.getInt("id_banco"));
            				dtoCons.setDescripcion(rs.getString("id_chequera"));
            			return dtoCons;
            		}
	            }
            );
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarBancoChequera");
		}
		return listBancChe;
	}
	
	/**
	 * FunSQLSelect107
	 * @param iIdEmpresa
	 * @return
	 */
	public int consultarNumCta(int iIdEmpresa)
	{
		sbSql = new StringBuffer();
		int iNoCta = 0;
		try
		{
		    sbSql.append("Select no_cuenta_emp  ");
		    sbSql.append("\n From empresa ");
		    sbSql.append("\n Where no_empresa = " + iIdEmpresa);
		    
		    iNoCta = jdbcTemplate.queryForObject(sbSql.toString(), Integer.class);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarNumCta");
		}
		return iNoCta;
	}
	
	/**
	 * FunObtenNoPersona
	 * @param iIdEmpresa
	 * @return
	 */
	public int consultarNumPersona(int iIdEmpresa)
	{
		sbSql = new StringBuffer();
		int iNoPersona = 0;
		try
		{
		    sbSql.append("SELECT no_persona");
		    sbSql.append("\n FROM persona ");
		    sbSql.append("\n WHERE no_empresa = " + iIdEmpresa);
		    sbSql.append("\n and id_tipo_persona = 'E'");
		    
		    iNoPersona = jdbcTemplate.queryForObject(sbSql.toString(), Integer.class);
		    
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarNumPersona");
		}
		return iNoPersona;
	}
	
	/**
	 * FunObtenDivisaSoin
	 * @param sIdDivisa
	 * @return
	 */
	public int consultarDivisaSoin(String sIdDivisa)
	{
		sbSql = new StringBuffer();
		int iDivisaSoin = 0;
		try
		{
		    sbSql.append("SELECT id_divisa_soin");
		    sbSql.append("\n FROM cat_divisa ");
		    sbSql.append("\n WHERE id_divisa = '" + sIdDivisa + "'");
		    sbSql.append("\n and clasificacion = 'D'");
		    
		    iDivisaSoin = jdbcTemplate.queryForObject(sbSql.toString(), Integer.class);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarDivisaSoin");
		}
		return iDivisaSoin;
	}
	
	public int consultarCtaCredInter(int iEmpresa, int iLinea, int iPersona, int iCuenta)
	{
		sbSql = new StringBuffer();
		int iNoEmpresa = 0;
		try
		{
		    sbSql.append("SELECT no_empresa");
		    sbSql.append("\n FROM cuenta");
		    sbSql.append("\n WHERE no_empresa = " + iEmpresa);
		    //sbSql.append("\n and no_linea = " + iLinea);iLinea trae (1) divisa_soin 
		    //sbSql.append("\n and no_persona = " + iPersona);
		    sbSql.append("\n and no_cuenta = " + iCuenta);
		    
		    iNoEmpresa = jdbcTemplate.queryForObject(sbSql.toString(), Integer.class);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarCtaCredInter");
		}
		return iNoEmpresa;
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int insertarCtaCredInter(CuentaDto dto)
	{
		sbSql = new StringBuffer();
		int iRegAfec = 0;
		try
		{
		    sbSql.append("INSERT INTO cuenta (no_empresa ,no_linea ,no_persona ,no_cuenta, ");
		    sbSql.append("\n fec_alta ,usuario_alta ,id_tipo_cuenta ) ");
		    sbSql.append("\n VALUES ");
		    sbSql.append("\n (" + dto.getNoEmpresa() + "," + dto.getNoLinea() + "," + dto.getNoPersona());
		    sbSql.append("\n  ," + dto.getNoCuenta() + ",'" + funciones.ponerFecha(dto.getFecAlta()) + "'," + dto.getUsuarioAlta() + ",'P' ) ");
		    
		    iRegAfec = jdbcTemplate.update(sbSql.toString());
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:insertarCtaCredInter");
		}
		return iRegAfec;
	}
	
	/**
	* Selecciona el n�mero de folio de la tabla folio
	* @param tipoFolio
	* @return int
	*/
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int seleccionarFolioReal(String tipoFolio) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT num_folio ");
		sbSql.append("\n FROM folio ");
		sbSql.append("\n WHERE tipo_folio = '" + tipoFolio + "'");
		try {
			return jdbcTemplate.queryForInt(sbSql.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ e.toString() + 
					"P:Inversiones, C:InversionesDaoImpl, M:seleccionarFolioReal");
			return -1;
		}
	}

	/**
	 * Actualiza el n�mero de folio de la tabla folio
	 * @param tipoFolio
	 * @return int
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int actualizarFolioReal(String tipoFolio){
		StringBuffer sbSQL = new StringBuffer();
		sbSQL.append(" UPDATE folio ");
		sbSQL.append("\n SET num_folio = num_folio + 1 ");
		sbSQL.append("\n WHERE tipo_folio = '" + tipoFolio + "'");
		try {
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "
					+ e.toString() + "P:Inversiones, C:InversionesDaoImpl, M:actualizarFolioReal");
			return -1;
		}
	}
	
	/**
	 * M�todo para insertar en la tabla parametro, utilizado
	 * en SolicitudDeTraspasosDeCredito.
	 * @param dto
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int insertarParametroTrasCred(ParametroDto dto)
	{
		sbSql = new StringBuffer();
		int iRegAfec = 0;
		try
		{
		    sbSql.append("INSERT INTO parametro (no_empresa,no_folio_param,aplica,secuencia,id_tipo_operacion,");
		    sbSql.append("\n no_cuenta,id_estatus_mov,id_chequera,id_banco,importe,b_salvo_buen_cobro,fec_valor,");
		    sbSql.append("\n fec_valor_original,id_estatus_reg,usuario_alta,fec_alta,id_divisa,id_forma_pago,");
		    sbSql.append("\n importe_original,fec_operacion,id_banco_benef, id_chequera_benef,origen_mov,");
		    sbSql.append("\n concepto,id_caja,no_folio_mov,folio_ref,grupo,no_docto,no_cliente,beneficiario,");
		    sbSql.append("\n valor_tasa,dias_inv )");
		    
		    sbSql.append("\n Values(");
		    sbSql.append("\n " + dto.getNoEmpresa() + "," + dto.getNoFolioParam() + "," + dto.getAplica() + ",");
		    sbSql.append("\n " + dto.getSecuencia() + "," + dto.getIdTipoOperacion() + "," + dto.getNoCuenta() + ",");
		    sbSql.append("\n '" + dto.getIdEstatusMov() + "','" + dto.getIdChequera() + "'," + dto.getIdBanco() + ",");
		    sbSql.append("\n " + dto.getImporte() + ",'" + dto.getBSalvoBuenCobro() + "','" + funciones.ponerFechaSola(dto.getFecValor()) + "',");
		    sbSql.append("\n '" + funciones.ponerFechaSola(dto.getFecValorOriginal()) + "','" + dto.getIdEstatusReg() + "'," + dto.getUsuarioAlta() + ",");
		    sbSql.append("\n '" + funciones.ponerFechaSola(dto.getFecAlta()) + "','" + dto.getIdDivisa() + "'," + dto.getIdFormaPago() + ",");
		    sbSql.append("\n " + dto.getImporteOriginal() + ",'" + funciones.ponerFechaSola(dto.getFecOperacion()) + "'," + dto.getIdBancoBenef() + ",");
		    sbSql.append("\n '" + dto.getIdChequeraBenef() + "','" + dto.getOrigenMov() + "','" + dto.getConcepto() + "',");
		    sbSql.append("\n " + dto.getIdCaja() + "," + dto.getNoFolioMov() + "," + dto.getFolioRef() + ",");
		    sbSql.append("\n " + dto.getGrupo() + ",'" + dto.getNoDocto() + "','" + dto.getNoCliente() + "',");
		    sbSql.append("\n '" + dto.getBeneficiario() + "'," + dto.getValorTasa() + "," + dto.getDiasInv());
		    sbSql.append("\n )");
		    
		    iRegAfec = jdbcTemplate.update(sbSql.toString());
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " "
					+ e.toString() + "P:Inversiones, C:InversionesDaoImpl, M:insertarParametroTrasCred");
		}
		return iRegAfec;
	}
	
	/**
	 * FunSQLSelect38 
	 * @param iEmpresa
	 * @return
	 */
	public int consultarNumCtaPagadora(int iEmpresa)
	{
		sbSql = new StringBuffer();
		int iNoEmpresa = 0;
		try
		{
		    sbSql.append("Select no_cuenta");
		    sbSql.append("\n from cuenta");
		    sbSql.append("\n WHERE no_empresa = " + iEmpresa);
		    sbSql.append("\n and id_tipo_cuenta='P'"); 
		    
		    iNoEmpresa = jdbcTemplate.queryForObject(sbSql.toString(), Integer.class);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarNumCtaPagadora");
		}
		return iNoEmpresa;
	}
	
	/**
	 * FunSQLSelect39
	 * @param iNoEmpresa
	 * @return
	 */
	public String consultarNomEmpresa(int iNoEmpresa)
	{
		StringBuffer sbSql = new StringBuffer();
		String sNomEmpresa = "";
		try
		{
		    sbSql.append("Select nom_empresa");
		    sbSql.append("\n from empresa");
		    sbSql.append("\n where no_empresa=" + iNoEmpresa);
		    sNomEmpresa = jdbcTemplate.queryForObject(sbSql.toString(), String.class);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarNomEmpresa");
		}
		return sNomEmpresa;
	}
	 
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public Map<String, Object> ejecutarGenerador(GeneradorDto dto)
	{
		Map<String, Object> resultado= new HashMap<String, Object>();
		try{
			ConsultasGenerales consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			
			resultado = consultasGenerales.ejecutarGenerador(dto);
			
			}catch (Exception e) {
				bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
				+ "P:PrestamosInterempresas, C:PrestamosInterempresasDaoImpl, M:consultarNomEmpresa U:"+dto.getIdUsuario()
				+ "	F:"+dto.getNomForma());
		}
		return resultado; 
	}
	
	/**
	 * Este m�todo consulta las empresas de arbol_empresa,
	 * excepto la empresa padre.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarEmpresasArbol()
	{
		List<LlenaComboGralDto> listEmp = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSql = new StringBuffer();
		try
		{
			sbSql.append("Select ae.no_empresa as ID, e.nom_empresa as describ");
		    sbSql.append("\n From arbol_empresa ae,empresa e");
		    sbSql.append("\n Where e.no_empresa = ae.no_empresa");
		    sbSql.append("\n and ae.no_empresa <> ae.padre");
		    
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
	
	/**
	 * Inician m�todos para la forma CalculoDeInteres
	 */
	@SuppressWarnings("unchecked")
	public List<RetInteresPresNoDoc> consultarInteresPresNoDoc(ParamInteresPresNoDoc dto)
	{
		List<RetInteresPresNoDoc> listCons = new ArrayList<RetInteresPresNoDoc>();
		sbSql = new StringBuffer();
		StringBuffer sbSqlSdoProm = new StringBuffer();
		StringBuffer sbArbol = new StringBuffer();
		try
		{
			sbSqlSdoProm.append("\n ( SELECT sum(importe) ");
		    sbSqlSdoProm.append("\n   FROM hist_saldo hs ");
		    sbSqlSdoProm.append("\n   WHERE hs.id_tipo_saldo = 91 ");
		    sbSqlSdoProm.append("\n       and hs.no_empresa = e.no_empresa ");
		    sbSqlSdoProm.append("\n       and hs.no_linea = d.id_divisa_soin ");
		    sbSqlSdoProm.append("\n       and hs.fec_valor between '" + funciones.ponerFechaSola(dto.getFecIni()) + "' ");
		    sbSqlSdoProm.append("\n       and '" + funciones.ponerFechaSola(dto.getFecFin()) + "'  ");
		    //'Se agrego la cuenta ya que las mamas que tiene otra mama tomaban en cuenta los importes de las hijas de su
		    sbSqlSdoProm.append("\n       and no_cuenta = (select max(no_cuenta_emp) from empresa e1 where e1.no_empresa = e.no_empresa ) ) ");
		    
		    //Se hace la consulta de la empresa padre para pasarsela a iArbol ya que 
		    //en la migraci�n solo se tenia la cadena 'padre' 
		    sbArbol.append(" (SELECT ar.empresa_raiz ");
		    sbArbol.append("\n     FROM arbol_empresa ar  ");
		    sbArbol.append("\n     WHERE ar.no_empresa = e.no_empresa)");
		    
		    sbSql.append("\n SELECT ");
		    sbSql.append("\n     ( SELECT coalesce(e1.porcentaje_iva, 0) ");
		    sbSql.append("\n       FROM valor_empresa e1 ");
		    sbSql.append("\n       WHERE e1.no_empresa = " + sbArbol + " and e1.id_divisa = d.id_divisa) * ");
		    sbSql.append("\n     ( " + sbSqlSdoProm + " / " + dto.getPlazo() + " ) as Iva, ");
		    //' sbSql.append("\n     / 100.0) / 360.0 ) * + dto.getPlazo() + & " ) as Iva, ");
		    sbSql.append("\n     e.no_empresa, e.nom_empresa, e.no_cuenta_emp, e.no_linea_emp, ");
		    sbSql.append("\n     d.id_divisa_soin, d.id_divisa, ");
		    sbSql.append("\n     ( " + sbSqlSdoProm + " / " + dto.getPlazo() + " ) as Sdo_Promedio, ");
		    sbSql.append("\n     ( " + sbSqlSdoProm + " / " + dto.getPlazo() + " ) as Int_Por_Pagar, ");
		    //'sbSql.append("\n     / 100.0) / 360.0 ) * " + dto.getPlazo() + " ) as Int_Por_Pagar, ");
		    sbSql.append("\n     " + sbArbol + "as Emp_Benef, ");
		    sbSql.append("\n     ( SELECT max(em.nom_empresa) ");
		    sbSql.append("\n       FROM empresa em ");
		    sbSql.append("\n       WHERE no_empresa = " + sbArbol + " ) as Nom_Emp_Benef, ");
		    sbSql.append("\n     ( SELECT max(ccb.id_chequera) ");
		    sbSql.append("\n       FROM cat_cta_banco ccb ");
		    sbSql.append("\n       WHERE ccb.no_empresa = " + sbArbol);
		    sbSql.append("\n           and b_traspaso = 'S' ");
		    sbSql.append("\n           and id_divisa = d.id_divisa) as Cheq_Benef, ");
		    sbSql.append("\n     ( SELECT max(ccb.id_banco) ");
		    sbSql.append("\n       FROM cat_cta_banco ccb ");
		    sbSql.append("\n       WHERE ccb.no_empresa = " + sbArbol);
		    sbSql.append("\n           and b_traspaso = 'S' ");
		    sbSql.append("\n           and id_divisa = d.id_divisa) as Banco_Benef, ");
		    sbSql.append("\n     ( SELECT max(cb.desc_banco) ");
		    sbSql.append("\n       FROM cat_banco cb ");
		    sbSql.append("\n       WHERE cb.id_banco = ( SELECT max(ccb.id_banco) ");
		    sbSql.append("\n                             FROM cat_cta_banco ccb ");
		    sbSql.append("\n                             WHERE ccb.no_empresa = " + sbArbol);
		    sbSql.append("\n                                 and b_traspaso = 'S' ");
		    sbSql.append("\n                                 and id_divisa = d.id_divisa) ");
		    sbSql.append("\n     ) as Nom_Banco_Benef, ");
		    
		    sbSql.append("\n     ( SELECT max(ccb.id_chequera) ");
		    sbSql.append("\n       FROM cat_cta_banco ccb ");
		    sbSql.append("\n       WHERE ccb.no_empresa = e.no_empresa ");
		    sbSql.append("\n           and b_traspaso = 'S' ");
		    sbSql.append("\n           and id_divisa = d.id_divisa) as Cheq_Origen, ");
		    sbSql.append("\n     ( SELECT max(ccb.id_banco) ");
		    sbSql.append("\n       FROM cat_cta_banco ccb ");
		    sbSql.append("\n       WHERE ccb.no_empresa = e.no_empresa ");
		    sbSql.append("\n           and b_traspaso = 'S' ");
		    sbSql.append("\n           and id_divisa = d.id_divisa) as Banco_Origen ");
		    
		    sbSql.append("\n FROM empresa e, cat_divisa d, arbol_empresa r "); //se cambio relacion_arbol_empresa por arbol_empresa
		    sbSql.append("\n WHERE " + sbSqlSdoProm + " > 0 ");
		    sbSql.append("\n   and  e.no_empresa = r.no_empresa ");
		    sbSql.append("\n   and e.no_empresa not in (select distinct empresa_raiz");
		    sbSql.append("\n                            from arbol_empresa )");
		    sbSql.append("\n     and d.id_divisa_soin in ( ");
		    sbSql.append("\n           SELECT no_linea ");
		    sbSql.append("\n           FROM hist_saldo hs ");
		    sbSql.append("\n           WHERE hs.id_tipo_saldo = 91 ");
		    sbSql.append("\n               and hs.no_empresa = e.no_empresa ");
		    sbSql.append("\n               and hs.fec_valor between '" + funciones.ponerFechaSola(dto.getFecIni()) + "' ");
		    sbSql.append("\n                   and '" + funciones.ponerFechaSola(dto.getFecFin()) + "' ) ");
		    sbSql.append("\n     and d.id_divisa = '" + dto.getIdDivisa() + "' ");
		    //' DESC 28/03/2005
		    sbSql.append("  and e.no_empresa not in ( ");
		    sbSql.append("      select no_empresa from  empresa where no_empresa in ( ");
		    sbSql.append("       select setemp from set006 where soiemp in (");
		    sbSql.append("           select soiemp from set006 where setemp in (");
		    sbSql.append("               select no_empresa from empresa where b_concentradora = 'S')");
		    sbSql.append("       and siscod = 'CO') and siscod = 'CP')");
		    sbSql.append("       )");
		    
		    sbSql.append("\n and e.no_empresa in(select no_empresa from usuario_empresa where no_usuario=" + dto.getIdUsuario());
		    sbSql.append("\n )");
		    //'    'AT
		    //' DESC 28/03/2005
		    sbSql.append("\n ORDER BY e.no_empresa ");
		    
		    System.out.println("CalculoDeInteres: " + sbSql.toString());
		    listCons = jdbcTemplate.query(sbSql.toString(), new RowMapper()
		    {
		    	public RetInteresPresNoDoc mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		RetInteresPresNoDoc dtoCons = new RetInteresPresNoDoc();
		    		
		    			dtoCons.setIva(rs.getDouble("Iva"));
		    			dtoCons.setNoEmpresa(rs.getInt("no_empresa"));
		    			dtoCons.setNomEmpresa(rs.getString("nom_empresa"));
		    			dtoCons.setNoCuentaEmp(rs.getInt("no_cuenta_emp"));
		    			dtoCons.setNoLineaEmp(rs.getInt("no_linea_emp"));
		    			dtoCons.setIdDivisaSoin(rs.getString("id_divisa_soin"));
		    			dtoCons.setIdDivisa(rs.getString("id_divisa"));
		    			dtoCons.setSaldoPromedio(rs.getBigDecimal("Sdo_Promedio").doubleValue());
		    			dtoCons.setInteresesPorPagar(rs.getBigDecimal("Int_Por_Pagar").doubleValue());
		    			dtoCons.setNoEmpBenef(rs.getInt("Emp_Benef"));
		    			dtoCons.setNomEmpBenef(rs.getString("Nom_Emp_Benef"));
		    			dtoCons.setCheqBenef(rs.getString("Cheq_Benef"));
		    			dtoCons.setBancoBenef(rs.getInt("Banco_Benef"));
		    			dtoCons.setNomBancoBenef(rs.getString("Nom_Banco_Benef"));
		    			dtoCons.setCheqOrigen(rs.getString("Cheq_Origen"));
		    			dtoCons.setBancoOrigen(rs.getInt("Banco_Origen"));
		    		return dtoCons;
		    	}
		    });
			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " +  Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarInteresPresNoDoc");
		}
		return listCons;
	}
	
	public int insertarParametroCalInt(ParametroDto dto)
	{
		int iRegAfec = 0;
	    sbSql = new StringBuffer();
		try
		{
			sbSql.append("\n INSERT INTO ");
		    sbSql.append("\n parametro ");
		    sbSql.append("\n (");
		    
		    sbSql.append("\n no_empresa,");
		    sbSql.append("\n no_folio_param,");
		    sbSql.append("\n aplica,");
		    sbSql.append("\n secuencia,");
		    sbSql.append("\n id_tipo_operacion,");
		    
		    sbSql.append("\n  no_cuenta,");
		    sbSql.append("\n  id_estatus_mov,");
		    sbSql.append("\n  id_chequera,");
		    sbSql.append("\n  id_banco,");
		    sbSql.append("\n  importe,");
		    
		    sbSql.append("\n  b_salvo_buen_cobro,");
		    sbSql.append("\n  fec_valor,");
		    sbSql.append("\n  fec_valor_original,");
		    sbSql.append("\n  id_estatus_reg,");
		    sbSql.append("\n  usuario_alta,");
		    
		    sbSql.append("\n  fec_alta,");
		    sbSql.append("\n  id_divisa,");
		    sbSql.append("\n  id_forma_pago,");
		    sbSql.append("\n  importe_original,");
		    sbSql.append("\n  fec_operacion,");
		    
		    sbSql.append("\n  id_banco_benef,");
		    sbSql.append("\n  id_chequera_benef,");
		    sbSql.append("\n  origen_mov,");
		    sbSql.append("\n  concepto,");
		    sbSql.append("\n  id_caja,");
		    
		    sbSql.append("\n no_folio_mov,");
		    sbSql.append("\n folio_ref,");
		    sbSql.append("\n grupo,");
		    sbSql.append("\n no_docto,");
		    sbSql.append("\n beneficiario,");
		    
		    sbSql.append("\n no_cliente,");
		    sbSql.append("\n no_recibo,");
		    sbSql.append("\n valor_tasa,");
		    sbSql.append("\n dias_inv");
		    
		    sbSql.append("\n )");
		    sbSql.append("\n VALUES");
		    
		    sbSql.append("\n ( " + dto.getNoEmpresa());
		    sbSql.append("\n," + dto.getNoFolioParam());
		    sbSql.append("\n," + dto.getAplica());         //'default 1
		    sbSql.append("\n," + dto.getSecuencia());        //'tenia 2
		    sbSql.append("\n," + dto.getIdTipoOperacion());    //'tenia 3806
		    
		    sbSql.append("\n," + dto.getNoCuenta());
		    sbSql.append("\n,'" + dto.getIdEstatusMov() + "'");        //' tenia L
		    sbSql.append("\n,'" + dto.getIdChequera() + "'");
		    sbSql.append("\n," + dto.getIdBanco());
		    sbSql.append("\n," + dto.getImporte());
		    
		    sbSql.append("\n,'" + dto.getBSalvoBuenCobro() + "'");             //'S
		    sbSql.append("\n,'" + funciones.ponerFecha(dto.getFecValor()) + "'");
		    sbSql.append("\n,'" + funciones.ponerFecha(dto.getFecValorOriginal()) + "'");
		    sbSql.append("\n,'" + dto.getIdEstatusReg() + "'");                //'P
		    sbSql.append("\n," + dto.getUsuarioAlta());
		    
		    sbSql.append("\n,'" + funciones.ponerFecha(dto.getFecAlta()) + "'");
		    sbSql.append("\n,'" + dto.getIdDivisa() + "'");
		    sbSql.append("\n," + dto.getIdFormaPago());                        //'3 transfer
		    sbSql.append("\n," + dto.getImporteOriginal());
		    sbSql.append("\n,'" + funciones.ponerFecha(dto.getFecOperacion()) + "'");
		    
		    sbSql.append("\n," + dto.getIdBancoBenef());
		    sbSql.append("\n, '" + dto.getIdChequeraBenef() + "'");
		    sbSql.append("\n,'" + dto.getOrigenMov() + "'");
		    sbSql.append("\n,'" + dto.getConcepto() + "'");
		    sbSql.append("\n," + dto.getIdCaja());
		    
		    sbSql.append("\n," + dto.getNoFolioMov());
		    sbSql.append("\n," + dto.getFolioRef());
		    sbSql.append("\n," + dto.getGrupo());
		    sbSql.append("\n," + dto.getNoDocto());
		    sbSql.append("\n,'" + dto.getBeneficiario() + "'");
		    
		    sbSql.append("\n," + dto.getNoCliente());
		    sbSql.append("\n," + dto.getNoRecibo());
		    sbSql.append("\n," + dto.getValorTasa());
		    sbSql.append("\n," + dto.getDiasInv());
		    sbSql.append("\n)");
		    
		    iRegAfec = jdbcTemplate.update(sbSql.toString());
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:insertarParametroCalInt");
		}
		return iRegAfec;
	}
	
	/**
	 * FunSQLSelectChequeraTraspEmp
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarCheqTraspaso(int iEmpresa, String sIdDivisa)
	{
		sbSql = new StringBuffer();
		List<LlenaComboGralDto> listBanChe = new ArrayList<LlenaComboGralDto>();
		try
		{
		   sbSql.append(" SELECT * ");
		   sbSql.append("\n FROM cat_cta_banco ");
		   sbSql.append("\n WHERE no_empresa = " + iEmpresa);
		   sbSql.append("\n     and tipo_chequera in ('P', 'M') ");
		   sbSql.append("\n     and id_divisa = '" + sIdDivisa + "' ");
		   sbSql.append("\n     and b_traspaso = 'S' ");
		   
		   listBanChe = jdbcTemplate.query(sbSql.toString(), new RowMapper()
		   {
			   public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		LlenaComboGralDto dtoCons = new LlenaComboGralDto();
		    			dtoCons.setId(rs.getInt("id_banco"));
		    			dtoCons.setDescripcion(rs.getString("id_chequera"));
		    		return dtoCons;
		    	}
		   });
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarCheqTraspaso");

		}
		return listBanChe;
	}
	
	/**
	 * FunSQLSelectCheqMadreMismoBanco
	 * @param iEmpresa
	 * @param sIdDivisa
	 * @param iIdBanco
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarCheqMadreMismoBan(int iEmpresa, String sIdDivisa, int iIdBanco)
	{
		sbSql = new StringBuffer();
		List<LlenaComboGralDto> listBanChe = new ArrayList<LlenaComboGralDto>();
		try
		{
		    sbSql.append("\n SELECT cc.* ");
		    sbSql.append("\n FROM cat_cta_banco cc ");
		    sbSql.append("\n WHERE cc.no_empresa = " + iEmpresa);
		    sbSql.append("\n     and cc.tipo_chequera in ('C', 'M') ");
		    sbSql.append("\n     and cc.id_divisa = '" + sIdDivisa + "' ");
		    sbSql.append("\n     and cc.id_chequera = ");
		    sbSql.append("\n         ( SELECT ccb.id_chequera ");
		    sbSql.append("\n           FROM cat_cta_banco ccb ");
		    sbSql.append("\n           WHERE ccb.id_banco = " + iIdBanco);
		    sbSql.append("\n               and ccb.no_empresa = cc.no_empresa ");
		    sbSql.append("\n               and ccb.tipo_chequera = cc.tipo_chequera ");
		    sbSql.append("\n               and ccb.id_divisa = cc.id_divisa ");
		    sbSql.append("\n               and ccb.mismo_banco = 'S' ) ");
		   
		   listBanChe = jdbcTemplate.query(sbSql.toString(), new RowMapper()
		   {
			   public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		LlenaComboGralDto dtoCons = new LlenaComboGralDto();
		    			dtoCons.setId(rs.getInt("id_banco"));
		    			dtoCons.setDescripcion(rs.getString("id_chequera"));
		    		return dtoCons;
		    	}
		   });
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarCheqTraspaso");

		}
		return listBanChe;
	}
	
	/**
	 * FunSQLSelectCuentaCoinv
	 * @param iNoEmpresa
	 * @return
	 */
	public int consultarCtaCoinversora(int iNoEmpresa)
	{
		sbSql = new StringBuffer();
		int iNoCtaCoin = 0;
		try
		{
			sbSql.append("Select no_cuenta ");
	        sbSql.append("\n From cuenta ");
	        sbSql.append("\n Where no_empresa = " + iNoEmpresa);
	        sbSql.append("\n and id_tipo_cuenta='P'");
	        
	        iNoCtaCoin = jdbcTemplate.queryForObject(sbSql.toString(), Integer.class);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarCtaCoinversora");
		}
		return iNoCtaCoin;
	}
	
	/**
	 * FunSQLSelectSaldoCoinversion
	 * @param iEmpCoinv
	 * @param iEmpresa
	 * @param sDivisa
	 * @return
	 */
	public double consultarSaldoCoinversion(int iEmpCoinv, int iEmpresa, String sDivisa)
	{
		sbSql = new StringBuffer();
		double uSaldo = 0;
		BigDecimal bdSaldo = new BigDecimal(0.0);
		try
		{
		    sbSql.append("SELECT coalesce(sum(s.importe), 0) as SdoCoinversion ");
		    sbSql.append("\n FROM saldo s ");
		    sbSql.append("\n WHERE s.no_empresa = " + iEmpCoinv);
		    sbSql.append("\n     AND s.no_cuenta = " + iEmpresa);
		    sbSql.append("\n     AND s.id_tipo_saldo = 91 ");
		    sbSql.append("\n     AND s.no_linea = ");
		    sbSql.append("\n         ( SELECT cd.id_divisa_soin ");
		    sbSql.append("\n           FROM cat_divisa cd ");
		    sbSql.append("\n           WHERE cd.id_divisa = '" + sDivisa + "' ) ");
		    
		    bdSaldo = jdbcTemplate.queryForObject(sbSql.toString(), BigDecimal.class);
		    uSaldo = bdSaldo.doubleValue();
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarSaldoCoinversion");

		}
		return uSaldo;
	}
	
	public int insertarInteresPrestamo(InteresPrestamoDto dto)
	{
		int iRegAfec = 0;
		sbSql = new StringBuffer();
		try
		{
		    sbSql.append("INSERT INTO Interes_Prestamo");
		    sbSql.append("\n     (no_empresa, fec_inicio_calculo, fec_fin_calculo, id_divisa, ");
		    sbSql.append("\n     tasa, saldo_promedio, interes, iva) ");
		    sbSql.append("\n VALUES ");
		    sbSql.append("\n     (" + dto.getNoEmpresa() + ",'" + funciones.ponerFecha(dto.getFecInicioCalculo()) + "',");
		    sbSql.append("\n     '" + funciones.ponerFecha(dto.getFecFinCalculo()) + "'," + "'" + dto.getIdDivisa()+ "',");
		    sbSql.append("\n     " + dto.getTasa() + "," + dto.getSaldoPromedio() + "," + dto.getInteres() + "," + dto.getIva() + ")");
		    
		    iRegAfec = jdbcTemplate.update(sbSql.toString());
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:insertarInteresPrestamo");
		}
		return iRegAfec;
	}
	
	/**
	 * FunObtenPeriodosIntPrestNoDoc
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarPeriodosPrestamos()
	{
		sbSql = new StringBuffer();
		List<LlenaComboGralDto> listPer = new ArrayList<LlenaComboGralDto>();
		try
		{
		    if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
		    {
		    	sbSql.append("SELECT DISTINCT rtrim(ltrim(convert(datetime,fec_inicio_calculo,103))) + ' - ' + ");
				sbSql.append("\n     rtrim(ltrim(convert(datetime,fec_fin_calculo,103)) + ' - ' + rtrim(ltrim(id_divisa)) as Periodo ");
		    }
		    else if(ConstantesSet.gsDBM.equals("DB2"))
		    {
		    	sbSql.append("SELECT DISTINCT rtrim(ltrim(cast(fec_inicio_calculo as varchar(10)))) + ' - ' + ");
		        sbSql.append("\n     rtrim(ltrim(cast(fec_fin_calculo as varchar(10)))) + ' - ' + rtrim(ltrim(id_divisa)) as Periodo ");

		    }
		    else
		    {
		    	sbSql.append("SELECT DISTINCT rtrim(ltrim(convert(char,fec_inicio_calculo,106))) + ' - ' + ");
		        sbSql.append("\n     rtrim(ltrim(convert(char,fec_fin_calculo,106))) + ' - ' + rtrim(ltrim(id_divisa)) as Periodo ");
		    }
		    	
		    sbSql.append("\n FROM interes_prestamo ");
		    sbSql.append("\n ORDER BY Periodo ");
		    
		    listPer = jdbcTemplate.query(sbSql.toString(), new RowMapper()
		    {
		    	public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		LlenaComboGralDto dtoCons = new LlenaComboGralDto();
		    			dtoCons.setDescripcion(rs.getString("Periodo"));
		    		return dtoCons;
		    	}
		    });
			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:LlenaComboGralDto");
		}
		return listPer;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarReportePresNoDoc(ParamComunDto dto)
	{
		sbSql = new StringBuffer();
		List<Map<String, Object>> listCons = new ArrayList<Map<String, Object>>();
		try
		{
			sbSql.append("SELECT ip.no_empresa, ip.fec_inicio_calculo, ip.fec_fin_calculo, ");
		    sbSql.append("\n         ip.id_divisa, ip.tasa, ip.saldo_promedio, ip.interes, ");
		    sbSql.append("\n         coalesce(ip.iva, 0) as iva, e.nom_empresa, d.desc_divisa ");
		    sbSql.append("\n     FROM interes_prestamo ip, empresa e, cat_divisa d ");
		    sbSql.append("\n     WHERE ip.fec_inicio_calculo = '" + funciones.ponerFecha(dto.getFecIni()) + "' ");
		    sbSql.append("\n         and ip.fec_fin_calculo = '" + funciones.ponerFecha(dto.getFecFin()) + "' ");
		    sbSql.append("\n         and ip.id_divisa = '" + dto.getDivisa() + "' ");
		    sbSql.append("\n         and ip.no_empresa = e.no_empresa ");
		    sbSql.append("\n         and d.id_divisa = ip.id_divisa ");
		    sbSql.append("\n     ORDER BY ip.no_empresa, ip.fec_inicio_calculo ");
		    
		    listCons = jdbcTemplate.query(sbSql.toString(), new RowMapper()
		    {
		    	public Map mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		Map<String, Object> map = new HashMap<String, Object>();
		    			map.put("noEmpresa", rs.getInt("no_empresa"));
		    			map.put("fecInicioCalculo", rs.getDate("fec_inicio_calculo"));
		    			map.put("fecFinCalculo", rs.getDate("fec_fin_calculo"));
		    			map.put("idDivisa", rs.getString("id_divisa"));
		    			map.put("tasa", rs.getBigDecimal("tasa").doubleValue());
		    			map.put("saldoPromedio", rs.getBigDecimal("saldo_promedio").doubleValue());
		    			map.put("interes", rs.getBigDecimal("interes").doubleValue());
		    			map.put("iva", rs.getBigDecimal("iva").doubleValue());
		    			map.put("nomEmpresa", rs.getString("nom_empresa"));
		    			map.put("descDivisa", rs.getString("desc_divisa"));
		    		return map;
		    	}
		    });
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarReportePresNoDoc");
		}
		return listCons;
	}
	
	//Inician m�todos de ReporteDeInteresNeto
	/**
	 * FunSQLObtenAnho
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarAnioInteresPres()
	{
		sbSql = new StringBuffer();
		List<LlenaComboGralDto> listAnio = new ArrayList<LlenaComboGralDto>();
		try
		{
		    sbSql.append("Select Distinct");
		    
		    if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
		    {
		    	sbSql.append("\n   case when date_part('year',ip.fec_inicio_calculo) > date_part('year',c.fec_desde) then date_part('year',ip.fec_inicio_calculo)");
		        sbSql.append("\n       else date_part('year',c.fec_desde) end as anho ");
		    }
		    else
		    {
		    	sbSql.append("\n   case when year(ip.fec_inicio_calculo) > year(c.fec_desde) then year(ip.fec_inicio_calculo)");
		        sbSql.append("\n       else year(c.fec_desde) end as anho");
		    }
		        
		    sbSql.append("\n from interes_prestamo ip, vencimiento_inversion c");
		    
		    listAnio = jdbcTemplate.query(sbSql.toString(), new RowMapper()
		    {
		    	public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		LlenaComboGralDto dto = new LlenaComboGralDto();
		    			dto.setId(rs.getInt("anho"));
		    		return dto;
		    	}
		    });
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarAnioInteresPres");
		}
		return listAnio;
	}
	
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarMes(int iAnio)
	{
		sbSql = new StringBuffer();
		List<LlenaComboGralDto> listMes = new ArrayList<LlenaComboGralDto>();
		try
		{
		    sbSql.append("Select distinct");
		    sbSql.append("\n  case when month(ip.fec_inicio_calculo) > month(c.fec_desde) then month(ip.fec_inicio_calculo)");
		    sbSql.append("\n       else month(c.fec_desde) end as num,");
		    
		    if(ConstantesSet.gsDBM.equals("DB2"))
		    {
		      sbSql.append("\n  case when month(ip.fec_inicio_calculo) > month(c.fec_desde) then upper(monthname(ip.fec_inicio_calculo))");
		      sbSql.append("\n       else upper(monthname(c.fec_desde)) end as mes");
		    }
		    else
		    {
		      sbSql.append("\n  case when month(ip.fec_inicio_calculo) > month(c.fec_desde) then upper(datename(m,ip.fec_inicio_calculo))");
		      sbSql.append("\n       else upper(datename(m,c.fec_desde)) end as mes");
		    }
		    
		    sbSql.append("\n from interes_prestamo ip, vencimiento_inversion c");
		    sbSql.append("\n where year(ip.fec_inicio_calculo) = " + iAnio);
		    sbSql.append("\n       and year(c.fec_desde) = " + iAnio);
		    
		    listMes = jdbcTemplate.query(sbSql.toString(), new RowMapper()
		    {
		    	public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		LlenaComboGralDto dto = new LlenaComboGralDto();
		    			dto.setId(rs.getInt("num"));
		    			dto.setDescripcion(rs.getString("mes"));
		    		return dto;
		    	}
		    });
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarMes");
		}
		return listMes;
	}
	
	/**
	 * HERE
	 * FunSQLRepInteresNeto
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarRepInteresNeto(ParamRepIntNetoDto dto)
	{
		sbSql = new StringBuffer();
		StringBuffer sbCoinv = new StringBuffer(); 
		StringBuffer sbCred = new StringBuffer(); 
		List<Map<String, Object>> listCons = new ArrayList<Map<String, Object>>(); 
		try
		{
		    sbCoinv.append("\n(  ");
	        sbCoinv.append("\n      (SELECT coalesce(sum(s.importe), 0)");
	        sbCoinv.append("\n       FROM hist_saldo s");
	        sbCoinv.append("\n       Where s.no_empresa = " + dto.getEmpresaCon());
	        sbCoinv.append("\n           AND s.no_cuenta = a.no_empresa");
	        sbCoinv.append("\n           AND id_tipo_saldo = 91");
	        sbCoinv.append("\n           AND s.no_linea =");
	        sbCoinv.append("\n                ( SELECT id_divisa_soin");
	        sbCoinv.append("\n                    From cat_divisa");
	        sbCoinv.append("\n                    WHERE id_divisa = '" + dto.getIdDivisa() + "')");
	        sbCoinv.append("\n           AND s.fec_valor = (select max(fec_valor) ");
	        sbCoinv.append("\n                               From hist_saldo");
	        sbCoinv.append("\n                               Where id_tipo_saldo = 91");
	        sbCoinv.append("\n                                   and month(fec_valor)= " + dto.getMes());
	        sbCoinv.append("\n                                   and year(fec_valor)= " + dto.getAnio() + " )");
	        sbCoinv.append("\n        )");
	        sbCoinv.append("\n  ) ");
	     
	        sbCred.append("\n  ( SELECT coalesce(sum(s.importe), 0)");
	        sbCred.append("\n    FROM hist_saldo s");
	        sbCred.append("\n    Where s.no_empresa = a.no_empresa");
	        sbCred.append("\n        AND s.no_cuenta = e.no_cuenta_emp");
	        sbCred.append("\n        AND id_tipo_saldo = 91");
	        sbCred.append("\n        AND s.no_linea =");
	        sbCred.append("\n            ( SELECT id_divisa_soin");
	        sbCred.append("\n              From cat_divisa");
	        sbCred.append("\n              WHERE id_divisa = '" + dto.getIdDivisa() + "')");
	        sbCred.append("\n    AND s.fec_valor = (select max(fec_valor) ");
	        sbCred.append("\n                               From hist_saldo");
	        sbCred.append("\n                               Where id_tipo_saldo = 91");
	        sbCred.append("\n                                   and month(fec_valor)= " + dto.getMes());
	        sbCred.append("\n                                   and year(fec_valor)=" + dto.getAnio() + ")");
	        sbCred.append("\n  ) ");
	     
	        sbSql.append("\n  select e.no_empresa,e.nom_empresa, ");
	        sbSql.append("\n  " + sbCred + " as saldo_cred, ");
	        sbSql.append("\n   coalesce(ip.interes,0.0) as ip_interes,");
	        sbSql.append("\n   coalesce(ip.iva,0.0) as ip_iva,");
	        sbSql.append("\n   (" + sbCred + " + coalesce(ip.interes,0.0) + coalesce(ip.iva,0.0)) as ip_suma,");
	        
	        sbSql.append("\n   " + sbCoinv + " as saldo_coinv, ");
	        sbSql.append("\n   coalesce(c.interes,0.0) as c_interes,");
	        sbSql.append("\n   coalesce(c.iva, 0.0) As c_iva,");
	        sbSql.append("\n (" + sbCoinv + " + coalesce(c.interes,0.0) + coalesce(c.iva, 0.0)) as c_suma, ");
	        
	        
	        sbSql.append("\n ((" + sbCoinv + " + coalesce(c.interes,0.0) + coalesce(c.iva, 0.0))-(" + sbCred + " + coalesce(ip.interes,0.0) + coalesce(ip.iva,0.0))) as total,");


	        sbSql.append("\n   coalesce(ip.tasa,0.0) as ip_tasa,");
	        sbSql.append("\n   coalesce(c.tasa_rend, 0.0) As c_tasa");
	        
	        if(ConstantesSet.gsDBM.equals("DB2"))
	        {
	        	sbSql.append("\n from   empresa e left join interes_prestamo ip on(e.no_empresa = ip.no_empresa)");
	        	sbSql.append("\n       left join vencimiento_inversion c on (e.no_empresa = c.no_empresa),");
	        	sbSql.append("\n       arbol_empresa a");
	        	sbSql.append("\n where ");// '''ip.no_empresa =* e.no_empresa and"
	        	//'''sbSql.append("\n       c.no_empresa =* e.no_empresa and");
	        }
	        else
	        {
	        	sbSql.append("\n from  interes_prestamo ip, empresa e,");
	        	sbSql.append("\n       vencimiento_inversion c,");
		        sbSql.append("\n       arbol_empresa a");
		        sbSql.append("\n where ip.no_empresa =* e.no_empresa and");
		        sbSql.append("\n       c.no_empresa =* e.no_empresa and");
	        }
	          
	        sbSql.append("\n       ip.id_divisa = '" + dto.getIdDivisa() + "'");
	        sbSql.append("\n       and c.id_divisa = '" + dto.getIdDivisa() + "'");
	        sbSql.append("\n       and Month(fec_inicio_calculo) = " + dto.getMes());
	        sbSql.append("\n       and Month(fec_desde) = " + dto.getMes());
	        sbSql.append("\n       and year(fec_inicio_calculo) = " + dto.getAnio());
	        sbSql.append("\n       and year(fec_desde) = " + dto.getAnio());

	        sbSql.append("\n       and a.no_empresa = e.no_empresa");
	        sbSql.append("\n       and e.no_empresa in");
	        sbSql.append("\n           ( SELECT no_cuenta");
	        sbSql.append("\n             FROM cuenta l");
	        sbSql.append("\n             WHERE l.id_tipo_cuenta='T'");
	        sbSql.append("\n                 and l.no_empresa = " + dto.getEmpresaCon() + " )");
	        
	        //'FunSQLAsignadaCoinversora(lConcentradora, piUsuario) = False
	        
	        if(!dto.isFacTodas())
	        {
	        	sbSql.append("\n       and a.no_empresa in");
	            sbSql.append("\n           ( SELECT no_empresa");
	            sbSql.append("\n             From usuario_empresa");
	            sbSql.append("\n             WHERE no_usuario =  " + dto.getIdUsuario() + " )");
	        }
	 
	        listCons = jdbcTemplate.query(sbSql.toString(), new RowMapper()
	        {
	        	public Map mapRow(ResultSet rs, int idx)throws SQLException
	        	{
	        		Map<String, Object> map = new HashMap<String, Object>();
	        			map.put("noEmpresa", rs.getInt("no_empresa"));
	        			map.put("nomEmpresa", rs.getString("nom_empresa"));
	        			map.put("saldoCred", rs.getBigDecimal("saldo_cred").doubleValue());
	        			map.put("ipInteres", rs.getBigDecimal("ip_interes").doubleValue());
	        			map.put("ipIva", rs.getBigDecimal("ip_iva").doubleValue());
	        			map.put("ipSuma", rs.getBigDecimal("ip_suma").doubleValue());
	        			map.put("saldoCoinv", rs.getBigDecimal("saldo_coinv").doubleValue());
	        			map.put("cInteres", rs.getBigDecimal("c_interes").doubleValue());
	        			map.put("cIva", rs.getBigDecimal("c_iva").doubleValue());
	        			map.put("cSuma", rs.getBigDecimal("c_suma").doubleValue());
	        			map.put("total", rs.getBigDecimal("total").doubleValue());
	        			map.put("ipTasa", rs.getBigDecimal("ip_tasa").doubleValue());
	        			map.put("cTasa", rs.getBigDecimal("c_tasa").doubleValue());
	        		return map;
	        	}
	        });
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarRepInteresNeto");
		}
		return listCons;
	}
	
	//Inician m�todos para FlujoNeto
	/**
	 * M�todo para obtener datos del catalogo de cat_act_economica
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarSectores()
	{
		sbSql = new StringBuffer();
		List<LlenaComboGralDto> listSectores = new ArrayList<LlenaComboGralDto>();
		try
		{
		    sbSql.append("SELECT * ");
		    sbSql.append("\n FROM cat_act_economica ");
		    sbSql.append("\n ORDER BY desc_act_economica ");
		    
		    listSectores = jdbcTemplate.query(sbSql.toString(), new RowMapper()
		    {
		    	public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		LlenaComboGralDto dto = new LlenaComboGralDto();
		    			dto.setId(rs.getInt(1));
		    			dto.setDescripcion(rs.getString(2));
		    		return dto;
		    	}
		    });
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarSectores");
		}
		return listSectores;
	}
	
	/**
	 * FunSQLSelect101
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarFlujoNetoSinSectores(ParamComunDto dto)
	{
		boolean bHoy = false;
		sbSql = new StringBuffer();
		List<Map<String, Object>> listMapFlujo = new ArrayList<Map<String, Object>>();
		try
		{
			if(dto.getFecValor().compareTo(dto.getFecHoy()) == 0)
				bHoy = true;
			
		    sbSql.append("SELECT a.no_empresa, e.nom_empresa, ");
		    sbSql.append("\n     ( SELECT coalesce(sum(s.importe), 0) ");
		    sbSql.append("\n       FROM " + (bHoy ? "saldo" : "hist_saldo") + " s ");
		    sbSql.append("\n       WHERE s.no_empresa = " + dto.getIdEmpresa());
		    sbSql.append("\n           AND s.no_cuenta = a.no_empresa ");
		    sbSql.append("\n           AND id_tipo_saldo = 90 ");
		    sbSql.append("\n           AND s.no_linea = ");
		    sbSql.append("\n               ( SELECT id_divisa_soin ");
		    sbSql.append("\n                 FROM cat_divisa ");
		    sbSql.append("\n                 WHERE id_divisa = '" + dto.getDivisa() + "') ");
		    
		    if(!bHoy)
		        sbSql.append("\n       AND s.fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "'");
		   
		    sbSql.append("\n     )");
		    sbSql.append("\n     - ");
		    sbSql.append("\n     ( SELECT coalesce(sum(s.importe), 0) ");
		    sbSql.append("\n       FROM " + (bHoy ? "saldo" : "hist_saldo") + " s ");
		    sbSql.append("\n       WHERE s.no_empresa = a.no_empresa ");
		    sbSql.append("\n           AND s.no_cuenta = e.no_cuenta_emp ");
		    sbSql.append("\n           AND id_tipo_saldo = 90 ");
		    sbSql.append("\n           AND s.no_linea = ");
		    sbSql.append("\n               ( SELECT id_divisa_soin ");
		    sbSql.append("\n                 FROM cat_divisa ");
		    sbSql.append("\n                 WHERE id_divisa = '" + dto.getDivisa() + "') ");
		    
		    if(!bHoy)
		    	sbSql.append("\n       AND s.fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "'");
		    
		    
		    sbSql.append("\n     )");
		    sbSql.append("\n     as SdoInicialNeto, ");
		    sbSql.append("\n     ( SELECT coalesce(sum(s.importe), 0) ");
		    sbSql.append("\n       FROM " + (bHoy ? "saldo" : "hist_saldo") + " s ");
		    sbSql.append("\n       WHERE s.no_empresa = " + dto.getIdEmpresa());
		    sbSql.append("\n           AND s.no_cuenta = a.no_empresa ");
		    sbSql.append("\n           AND id_tipo_saldo = 90 ");
		    sbSql.append("\n           AND s.no_linea = ");
		    sbSql.append("\n               ( SELECT id_divisa_soin ");
		    sbSql.append("\n                 FROM cat_divisa ");
		    sbSql.append("\n                 WHERE id_divisa = '" + dto.getDivisa() + "') ");
		    
		    if(!bHoy)
		        sbSql.append("\n       AND s.fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "'");
		    
		    sbSql.append("\n     )");
		    sbSql.append("\n     as si, ");
		    sbSql.append("\n     ( SELECT coalesce(sum(s.importe), 0) ");
		    sbSql.append("\n       FROM " + (bHoy ? "saldo" : "hist_saldo") + " s ");
		    sbSql.append("\n       WHERE s.no_empresa = " + dto.getIdEmpresa());
		    sbSql.append("\n           AND s.no_cuenta = a.no_empresa ");
		    sbSql.append("\n           AND id_tipo_saldo = 5 ");
		    sbSql.append("\n           AND s.no_linea = ");
		    sbSql.append("\n               ( SELECT id_divisa_soin ");
		    sbSql.append("\n                 FROM cat_divisa ");
		    sbSql.append("\n                 WHERE id_divisa = '" + dto.getDivisa() + "') ");
		    
		    if(!bHoy)
		        sbSql.append("\n       AND s.fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "'");
		    
		    
		    sbSql.append("\n     ) as ibm, ");
		    sbSql.append("\n     ( SELECT coalesce(sum(s.importe), 0) ");
		    sbSql.append("\n       FROM " + (bHoy ? "saldo" : "hist_saldo") + " s ");
		    sbSql.append("\n       WHERE s.no_empresa = " + dto.getIdEmpresa());
		    sbSql.append("\n           AND s.no_cuenta = a.no_empresa ");
		    sbSql.append("\n           AND id_tipo_saldo = 7 ");
		    sbSql.append("\n           AND s.no_linea = ");
		    sbSql.append("\n               ( SELECT id_divisa_soin ");
		    sbSql.append("\n                 FROM cat_divisa ");
		    sbSql.append("\n                 WHERE id_divisa = '" + dto.getDivisa() + "') ");
		    
		    if(!bHoy)
		        sbSql.append("\n       AND s.fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "'");
		    
		    sbSql.append("\n     ) as efm, ");
		    sbSql.append("\n     ( SELECT coalesce(sum(s.importe), 0) ");
		    sbSql.append("\n       FROM " + (bHoy ? "saldo" : "hist_saldo") + " s ");
		    sbSql.append("\n       WHERE s.no_empresa = " + dto.getIdEmpresa());
		    sbSql.append("\n           AND s.no_cuenta = a.no_empresa ");
		    sbSql.append("\n           AND id_tipo_saldo = 91 ");
		    sbSql.append("\n           AND s.no_linea = ");
		    sbSql.append("\n               ( SELECT id_divisa_soin ");
		    sbSql.append("\n                 FROM cat_divisa ");
		    sbSql.append("\n                 WHERE id_divisa = '" + dto.getDivisa() + "') ");
		    
		    if(!bHoy)
		        sbSql.append("\n       AND s.fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "'");
		    
		    sbSql.append("\n     ) as totalmn, ");
	
		    sbSql.append("\n(SELECT coalesce(sum(s.importe), 0)");
		    sbSql.append("\n   FROM " + (bHoy ? "saldo" : "hist_saldo") + " s ");
		    sbSql.append("\n   Where s.no_empresa = a.no_empresa");
		    sbSql.append("\n       AND s.no_cuenta = e.no_cuenta_emp");
		    sbSql.append("\n       AND id_tipo_saldo = 9");
		    sbSql.append("\n       AND s.no_linea =");
		    sbSql.append("\n           ( SELECT id_divisa_soin");
		    sbSql.append("\n               From cat_divisa");
		    sbSql.append("\n               WHERE id_divisa = 'MN')");
		    
		    if(!bHoy)
		        sbSql.append("\n       AND s.fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "'");
		    
		    sbSql.append("\n   ) as pcm,");
		    
		    sbSql.append("\n   (SELECT coalesce(sum(s.importe), 0)");
		    sbSql.append("\n   FROM " + (bHoy ? "saldo" : "hist_saldo") + " s ");
		    sbSql.append("\n   Where s.no_empresa = a.no_empresa");
		    sbSql.append("\n       AND s.no_cuenta = e.no_cuenta_emp");
		    sbSql.append("\n       AND id_tipo_saldo = 8");
		    sbSql.append("\n       AND s.no_linea =");
		    sbSql.append("\n           ( SELECT id_divisa_soin");
		    sbSql.append("\n               From cat_divisa");
		    sbSql.append("\n               WHERE id_divisa = 'MN')");
		    
		    if(!bHoy)
		        sbSql.append("\n       AND s.fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "'");
		    
		    sbSql.append("\n) as sc,");
		        
		    sbSql.append("\n     ( SELECT coalesce(sum(s.importe), 0) ");
		    sbSql.append("\n       FROM " + (bHoy ? "saldo" : "hist_saldo") + " s ");
		    sbSql.append("\n       WHERE s.no_empresa = a.no_empresa ");
		    sbSql.append("\n           AND s.no_cuenta = e.no_cuenta_emp ");
		    sbSql.append("\n           AND id_tipo_saldo = 91 ");
		    sbSql.append("\n           AND s.no_linea = ");
		    sbSql.append("\n               ( SELECT id_divisa_soin ");
		    sbSql.append("\n                 FROM cat_divisa ");
		    sbSql.append("\n                 WHERE id_divisa = '" + dto.getDivisa() + "') ");
		    
		    if(!bHoy)
		        sbSql.append("\n       AND s.fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "'");
		    
		    sbSql.append("\n     ) as saldo_c, ");
		    sbSql.append("\n     ( SELECT coalesce(sum(s.importe), 0) ");
		    sbSql.append("\n       FROM " + (bHoy ? "saldo" : "hist_saldo") + " s ");
		    sbSql.append("\n       WHERE s.no_empresa = " + dto.getIdEmpresa());
		    sbSql.append("\n           AND s.no_cuenta = a.no_empresa ");
		    sbSql.append("\n           AND id_tipo_saldo = 91 ");
		    sbSql.append("\n           AND s.no_linea = ");
		    sbSql.append("\n               ( SELECT id_divisa_soin ");
		    sbSql.append("\n                 FROM cat_divisa ");
		    sbSql.append("\n                 WHERE id_divisa = '" + dto.getDivisa() + "') ");
		    
		    if(!bHoy)
		        sbSql.append("\n       AND s.fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "'");
		    
		    sbSql.append("\n     )");
		    sbSql.append("\n     - ");
		    sbSql.append("\n     ( SELECT coalesce(sum(s.importe), 0) ");
		    sbSql.append("\n       FROM " + (bHoy ? "saldo" : "hist_saldo") + " s ");
		    sbSql.append("\n       WHERE s.no_empresa = a.no_empresa ");
		    sbSql.append("\n           AND s.no_cuenta = e.no_cuenta_emp ");
		    sbSql.append("\n           AND id_tipo_saldo = 91 ");
		    sbSql.append("\n           AND s.no_linea = ");
		    sbSql.append("\n               ( SELECT id_divisa_soin ");
		    sbSql.append("\n                 FROM cat_divisa ");
		    sbSql.append("\n                 WHERE id_divisa = '" + dto.getDivisa() + "') ");
		    
		    if(!bHoy)
		        sbSql.append("\n       AND s.fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "'");
		    
		    sbSql.append("\n     ) as SdoFinal ");
	
		    sbSql.append("\n FROM arbol_empresa a ,empresa e ");
		    sbSql.append("\n WHERE a.no_empresa = e.no_empresa ");
		    sbSql.append("\n     and e.no_empresa in ");
		    sbSql.append("\n         ( SELECT no_cuenta ");
		    sbSql.append("\n           FROM cuenta l ");
		    sbSql.append("\n           WHERE l.id_tipo_cuenta='T' ");
		    sbSql.append("\n               and l.no_empresa = " + dto.getIdEmpresa() + " ) ");
		    sbSql.append("\n     and a.no_empresa in ");
		    sbSql.append("\n         ( SELECT no_empresa ");
		    sbSql.append("\n           FROM usuario_empresa ");
		    sbSql.append("\n           WHERE no_usuario = " + dto.getIdUsuario() + " ) ");
		    sbSql.append("\n ORDER BY chosno4 ,chosno3,chosno2,chosno,tataranieto, ");
		    sbSql.append("\n          bisnieto,nieto,hijo,padre,e.no_empresa ASC ");
		    
		    listMapFlujo = jdbcTemplate.query(sbSql.toString(), new RowMapper()
		    {
		    	public Map mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		Map<String, Object> map = new HashMap<String, Object>();
		    			map.put("noEmpresa", rs.getInt("no_empresa"));
		    			map.put("nomEmpresa", rs.getString("nom_empresa"));
		    			map.put("saldoInicialNeto",rs.getBigDecimal("SdoInicialNeto").doubleValue());
		    			map.put("si", rs.getBigDecimal("si").doubleValue());
		    			map.put("ibm", rs.getBigDecimal("ibm").doubleValue());
		    			map.put("efm", rs.getBigDecimal("efm").doubleValue());
		    			map.put("totalmn",rs.getBigDecimal("totalmn").doubleValue());
		    			map.put("pcm",rs.getBigDecimal("pcm").doubleValue());
		    			map.put("sc", rs.getBigDecimal("sc").doubleValue());
		    			map.put("saldoC", rs.getBigDecimal("saldo_c").doubleValue());
		    			map.put("sdoFinal", rs.getBigDecimal("SdoFinal").doubleValue());
		    		return map;
		    	}
		    });
		    
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarFlujoNetoSinSectores");
		}
		return listMapFlujo;
	}
	
	/**
	 * FunSQLSelectSectores
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarFlujoNetoSectores(ParamComunDto dto)
	{
		int iRegAfec =0;
		sbSql = new StringBuffer();
		List<Map<String, Object>> listMapFlujo = new ArrayList<Map<String, Object>>();
		boolean bHoy = false;
		boolean bPrimero = true;
		boolean bSegundo = false;
		try
		{
			if(dto.getFecValor().compareTo(dto.getFecHoy()) == 0)
				bHoy = true;
			
			if(bSegundo)
		        sbSql.append("INSERT INTO #FlujoNeto ");
		    
		    if(ConstantesSet.gsDBM.equals("DB2") && bPrimero)
		      sbSql.append("\n create table #FlujoNeto as (");
		    
		    sbSql.append("\n SELECT a.no_empresa, e.nom_empresa, ");
		    sbSql.append("\n     ( SELECT coalesce(sum(s.importe), 0) ");
		    sbSql.append("\n       FROM " + (bHoy ? "saldo" : "hist_saldo") + " s ");
		    sbSql.append("\n       WHERE s.no_empresa = " + dto.getIdEmpresa());
		    sbSql.append("\n           AND s.no_cuenta = a.no_empresa ");
		    sbSql.append("\n           AND id_tipo_saldo = 90 ");
		    sbSql.append("\n           AND s.no_linea = ");
		    sbSql.append("\n               ( SELECT id_divisa_soin ");
		    sbSql.append("\n                 FROM cat_divisa ");
		    sbSql.append("\n                 WHERE id_divisa = '" + dto.getDivisa() + "') ");
		    
		    if(!bHoy)
		        sbSql.append("\n       AND s.fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "'");
		    
		    sbSql.append("\n     )");
		    sbSql.append("\n     - ");
		    sbSql.append("\n     ( SELECT coalesce(sum(s.importe), 0) ");
		    sbSql.append("\n       FROM " + (bHoy ? "saldo" : "hist_saldo") + " s ");
		    sbSql.append("\n       WHERE s.no_empresa = a.no_empresa ");
		    sbSql.append("\n           AND s.no_cuenta = e.no_cuenta_emp ");
		    sbSql.append("\n           AND id_tipo_saldo = 90 ");
		    sbSql.append("\n           AND s.no_linea = ");
		    sbSql.append("\n               ( SELECT id_divisa_soin ");
		    sbSql.append("\n                 FROM cat_divisa ");
		    sbSql.append("\n                 WHERE id_divisa = '" + dto.getDivisa() + "') ");
		    
		    if(!bHoy)
		        sbSql.append("\n       AND s.fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "'");
		    
		    sbSql.append("\n     )");
		    sbSql.append("\n     as SdoInicialNeto, ");
		    sbSql.append("\n     ( SELECT coalesce(sum(s.importe), 0) ");
		    sbSql.append("\n       FROM " + (bHoy ? "saldo" : "hist_saldo") + " s ");
		    sbSql.append("\n       WHERE s.no_empresa = " + dto.getIdEmpresa());
		    sbSql.append("\n           AND s.no_cuenta = a.no_empresa ");
		    sbSql.append("\n           AND id_tipo_saldo = 90 ");
		    sbSql.append("\n           AND s.no_linea = ");
		    sbSql.append("\n               ( SELECT id_divisa_soin ");
		    sbSql.append("\n                 FROM cat_divisa ");
		    sbSql.append("\n                 WHERE id_divisa = '" + dto.getIdEmpresa() + "') ");
		    
		    if(!bHoy)
		        sbSql.append("\n       AND s.fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "'");
		    
		    sbSql.append("\n     )");
		    sbSql.append("\n     as si, ");
		    sbSql.append("\n     ( SELECT coalesce(sum(s.importe), 0) ");
		    sbSql.append("\n       FROM " + (bHoy ? "saldo" : "hist_saldo") + " s ");
		    sbSql.append("\n       WHERE s.no_empresa = " + dto.getIdEmpresa());
		    sbSql.append("\n           AND s.no_cuenta = a.no_empresa ");
		    sbSql.append("\n           AND id_tipo_saldo = 5 ");
		    sbSql.append("\n           AND s.no_linea = ");
		    sbSql.append("\n               ( SELECT id_divisa_soin ");
		    sbSql.append("\n                 FROM cat_divisa ");
		    sbSql.append("\n                 WHERE id_divisa = '" + dto.getDivisa() + "') ");
		    
		    if(!bHoy)
		        sbSql.append("\n       AND s.fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "'");
		    
		    sbSql.append("\n     ) as ibm, ");
		    sbSql.append("\n     ( SELECT coalesce(sum(s.importe), 0) ");
		    sbSql.append("\n       FROM " + (bHoy ? "saldo" : "hist_saldo") + " s ");
		    sbSql.append("\n       WHERE s.no_empresa = " + dto.getIdEmpresa());
		    sbSql.append("\n           AND s.no_cuenta = a.no_empresa ");
		    sbSql.append("\n           AND id_tipo_saldo = 7 ");
		    sbSql.append("\n           AND s.no_linea = ");
		    sbSql.append("\n               ( SELECT id_divisa_soin ");
		    sbSql.append("\n                 FROM cat_divisa ");
		    sbSql.append("\n                 WHERE id_divisa = '" + dto.getDivisa() + "') ");
		    
		    if(!bHoy)
		        sbSql.append("\n       AND s.fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "'");
		    
		    sbSql.append("\n     ) as efm, ");
		    sbSql.append("\n     ( SELECT coalesce(sum(s.importe), 0) ");
		    sbSql.append("\n       FROM " + (bHoy ? "saldo" : "hist_saldo") + " s ");
		    sbSql.append("\n       WHERE s.no_empresa = " + dto.getIdEmpresa());
		    sbSql.append("\n           AND s.no_cuenta = a.no_empresa ");
		    sbSql.append("\n           AND id_tipo_saldo = 91 ");
		    sbSql.append("\n           AND s.no_linea = ");
		    sbSql.append("\n               ( SELECT id_divisa_soin ");
		    sbSql.append("\n                 FROM cat_divisa ");
		    sbSql.append("\n                 WHERE id_divisa = '" + dto.getDivisa() + "') ");
		    
		    if(!bHoy)
		        sbSql.append("\n       AND s.fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "'");
		    
		    sbSql.append("\n     ) as totalmn, ");
	
		    sbSql.append("\n(SELECT coalesce(sum(s.importe), 0)");
		    sbSql.append("\n   FROM " + (bHoy ? "saldo" : "hist_saldo") + " s ");
		    sbSql.append("\n   Where s.no_empresa = a.no_empresa");
		    sbSql.append("\n       AND s.no_cuenta = e.no_cuenta_emp");
		    sbSql.append("\n       AND id_tipo_saldo = 9");
		    sbSql.append("\n       AND s.no_linea =");
		    sbSql.append("\n           ( SELECT id_divisa_soin");
		    sbSql.append("\n               From cat_divisa");
		    sbSql.append("\n               WHERE id_divisa = 'MN')");
		    
		    if(!bHoy)
		        sbSql.append("\n       AND s.fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "'");
		    
		    sbSql.append("\n   ) as pcm,");
		    
		    sbSql.append("\n   (SELECT coalesce(sum(s.importe), 0)");
		    sbSql.append("\n   FROM " + (bHoy ? "saldo" : "hist_saldo") + " s ");
		    sbSql.append("\n   Where s.no_empresa = a.no_empresa");
		    sbSql.append("\n       AND s.no_cuenta = e.no_cuenta_emp");
		    sbSql.append("\n       AND id_tipo_saldo = 8");
		    sbSql.append("\n       AND s.no_linea =");
		    sbSql.append("\n           ( SELECT id_divisa_soin");
		    sbSql.append("\n               From cat_divisa");
		    sbSql.append("\n               WHERE id_divisa = 'MN')");
		    
		    if(!bHoy)
		        sbSql.append("\n       AND s.fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "'");
		    
		    sbSql.append("\n) as sc,");
		        
		    sbSql.append("\n     ( SELECT coalesce(sum(s.importe), 0) ");
		    sbSql.append("\n       FROM " + (bHoy ? "saldo" : "hist_saldo") + " s ");
		    sbSql.append("\n       WHERE s.no_empresa = a.no_empresa ");
		    sbSql.append("\n           AND s.no_cuenta = e.no_cuenta_emp ");
		    sbSql.append("\n           AND id_tipo_saldo = 91 ");
		    sbSql.append("\n           AND s.no_linea = ");
		    sbSql.append("\n               ( SELECT id_divisa_soin ");
		    sbSql.append("\n                 FROM cat_divisa ");
		    sbSql.append("\n                 WHERE id_divisa = '" + dto.getDivisa() + "') ");
		    
		    if(!bHoy)
		        sbSql.append("\n       AND s.fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "'");
		    
		    sbSql.append("\n     ) as saldo_c, ");
		    sbSql.append("\n     ( SELECT coalesce(sum(s.importe), 0) ");
		    sbSql.append("\n       FROM " + (bHoy ? "saldo" : "hist_saldo") + " s ");
		    sbSql.append("\n       WHERE s.no_empresa = " + dto.getIdEmpresa());
		    sbSql.append("\n           AND s.no_cuenta = a.no_empresa ");
		    sbSql.append("\n           AND id_tipo_saldo = 91 ");
		    sbSql.append("\n           AND s.no_linea = ");
		    sbSql.append("\n               ( SELECT id_divisa_soin ");
		    sbSql.append("\n                 FROM cat_divisa ");
		    sbSql.append("\n                 WHERE id_divisa = '" + dto.getDivisa() + "') ");
		    
		    if(!bHoy)
		        sbSql.append("\n       AND s.fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "'");
		    
		    sbSql.append("\n     )");
		    sbSql.append("\n     - ");
		    sbSql.append("\n     ( SELECT coalesce(sum(s.importe), 0) ");
		    sbSql.append("\n       FROM " + (bHoy ? "saldo" : "hist_saldo") + " s ");
		    sbSql.append("\n       WHERE s.no_empresa = a.no_empresa ");
		    sbSql.append("\n           AND s.no_cuenta = e.no_cuenta_emp ");
		    sbSql.append("\n           AND id_tipo_saldo = 91 ");
		    sbSql.append("\n           AND s.no_linea = ");
		    sbSql.append("\n               ( SELECT id_divisa_soin ");
		    sbSql.append("\n                 FROM cat_divisa ");
		    sbSql.append("\n                 WHERE id_divisa = '" + dto.getDivisa() + "') ");
		    
		    if(!bHoy)
		        sbSql.append("\n       AND s.fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "'");
		    
		    sbSql.append("\n     ) as SdoFinal ");
		    
		    if(bPrimero && !ConstantesSet.gsDBM.equals("DB2"))
		    {
		    	sbSql.append("\n INTO #FlujoNeto ");
		        bPrimero = false;
		        bSegundo = true;
		    }
		       
		    sbSql.append("\n FROM arbol_empresa a ,empresa e ");
		    sbSql.append("\n WHERE a.no_empresa = e.no_empresa ");
		    sbSql.append("\n     and e.no_empresa in ");
		    sbSql.append("\n         ( SELECT no_cuenta ");
		    sbSql.append("\n           FROM cuenta l ");
		    sbSql.append("\n           WHERE l.id_tipo_cuenta='T' ");
		    sbSql.append("\n               and l.no_empresa = " + dto.getIdEmpresa() + " ) ");
		    sbSql.append("\n     and a.no_empresa in ");
		    sbSql.append("\n         ( SELECT no_empresa ");
		    sbSql.append("\n           FROM usuario_empresa ");
		    sbSql.append("\n           WHERE no_usuario = " + dto.getIdUsuario() + " ) ");
		    sbSql.append("\n ORDER BY chosno4 ,chosno3,chosno2,chosno,tataranieto, ");
		    sbSql.append("\n          bisnieto,nieto,hijo,padre,e.no_empresa ASC ");
		    
		    if(ConstantesSet.gsDBM.equals("DB2") && bPrimero)
		    { 
		    	sbSql.append("\n ) definition only");
		    	bPrimero = false;
		    	bSegundo = true;
		    }
		    
		    iRegAfec = jdbcTemplate.update(sbSql.toString());
		    
		    sbSql = new StringBuffer();
		    sbSql.append("SELECT f.no_empresa, f.nom_empresa, f. SdoInicialNeto, f.si, f.ibm, f.efm,");
		    sbSql.append("\n f.totalmn, f.pcm, f.sc, f.saldo_c, f.SdoFinal, c.desc_act_economica");
		    sbSql.append("\n FROM #FlujoNeto f, cat_act_economica c, persona p");
		    sbSql.append("\n WHERE ");
		    
		    if(dto.getSector() != 0)
		        sbSql.append("\n c.id_act_economica = " + dto.getSector() + " AND");
		    
		    sbSql.append("\n p.id_act_economica = c.id_act_economica AND");
		    sbSql.append("\n p.no_empresa = f.no_empresa AND");
		    sbSql.append("\n p.id_tipo_persona = 'E'");
		    sbSql.append("\n ORDER BY c.desc_act_economica, f.no_empresa");
		    
		    listMapFlujo = jdbcTemplate.query(sbSql.toString(), new RowMapper()
		    {
		    	public Map mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		Map<String, Object> map = new HashMap<String, Object>();
			    		map.put("noEmpresa", rs.getInt("no_empresa"));
		    			map.put("nomEmpresa", rs.getString("nom_empresa"));
		    			map.put("saldoInicialNeto",rs.getBigDecimal("SdoInicialNeto").doubleValue());
		    			map.put("si", rs.getBigDecimal("si").doubleValue());
		    			map.put("ibm", rs.getBigDecimal("ibm").doubleValue());
		    			map.put("efm", rs.getBigDecimal("efm").doubleValue());
		    			map.put("totalmn",rs.getBigDecimal("totalmn").doubleValue());
		    			map.put("pcm",rs.getBigDecimal("pcm").doubleValue());
		    			map.put("sc", rs.getBigDecimal("sc").doubleValue());
		    			map.put("saldoC", rs.getBigDecimal("saldo_c").doubleValue());
		    			map.put("sdoFinal", rs.getBigDecimal("SdoFinal").doubleValue());	
		    			map.put("descActEconomica", rs.getString("desc_act_economica"));
		    		return map;
		    	}
		    });
		    
		    sbSql = new StringBuffer();
		    sbSql.append("DROP TABLE #FlujoNeto");
		    iRegAfec = jdbcTemplate.update(sbSql.toString());
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarFlujoNetoSectores");
		}
		return listMapFlujo;
	}
	
	//Inician m�todos para EstadoDeCuentaCredito
	/**
	 * Este m�todo obtiene las empresas de arbol_empresa
	 * asignadas a un usuario
	 * @param iIdUsuario
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarArbolEmpresaUsuario(int iIdUsuario)
	{
		sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmp = new ArrayList<LlenaComboGralDto>();
		try
		{
			sbSql.append("SELECT a.no_empresa as ID, e.nom_empresa as describ ");
		    sbSql.append("\n FROM arbol_empresa a, empresa e, usuario_empresa ue ");
		    sbSql.append("\n WHERE a.no_empresa=e.no_empresa  and ue.no_empresa = e.no_empresa ");
		    sbSql.append("\n and ue.no_usuario = " + iIdUsuario + " ORDER BY e.nom_empresa");
		    
		    listEmp = jdbcTemplate.query(sbSql.toString(), new RowMapper()
		    {
		    	public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		LlenaComboGralDto dto = new LlenaComboGralDto();
		    			dto.setId(rs.getInt(1));
		    			dto.setDescripcion(rs.getString(2));
		    		return dto;
		    	}
		    });
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarArbolEmpresaUsuario");
		}
		return listEmp;
	}
	
	/**
	 * FunSQLSelect1
	 */
	public double consultarSaldoInicial(ParamComunDto dto)
	{
		sbSql = new StringBuffer();
		double uSaldoInicial = 0;
		BigDecimal bdSaldoInicial = new BigDecimal(0);
		try
		{
		    sbSql.append("\n SELECT coalesce(s.importe,0) as importe ");
		    sbSql.append("\n FROM hist_saldo s, empresa e ");
		    sbSql.append("\n WHERE s.id_tipo_saldo in (90) and s.no_empresa = " + dto.getIdEmpresa());
		    sbSql.append("\n     and s.fec_valor = '" + funciones.ponerFecha(dto.getFecIni()) + "'");
		    sbSql.append("\n     and s.no_empresa = e.no_empresa ");
		    //'    sSQL = sSQL &  "     and s.no_linea = e.no_linea_emp "
		    sbSql.append("\n     and s.no_linea = ( ");
		    if(ConstantesSet.gsDBM.equals("SYBASE")) 
		        sbSql.append("\n        SELECT convert(int,id_divisa_soin) ");
		    else
		        sbSql.append("\n        SELECT id_divisa_soin ");
		    
		    sbSql.append("\n        FROM cat_divisa ");
		    sbSql.append("\n        WHERE id_divisa = '" + dto.getDivisa() + "')");
		    sbSql.append("\n     and s.no_cuenta = e.no_cuenta_emp ");
		    
		    bdSaldoInicial = jdbcTemplate.queryForObject(sbSql.toString(), BigDecimal.class);
		    uSaldoInicial = bdSaldoInicial.doubleValue();
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarArbolEmpresaUsuario");
			
			uSaldoInicial = 0D;
		}
		return uSaldoInicial;
	}
	
	
	public double consultarSaldoFinal(ParamComunDto dto)
	{
		BigDecimal bdSaldoFinal = new BigDecimal(0);
		double uSaldoFinal = 0;
		sbSql = new StringBuffer();
		try
		{
		    sbSql.append("SELECT coalesce( ");
		    sbSql.append("\n         ( SELECT coalesce(hs.importe,0)");
		    sbSql.append("\n           FROM hist_saldo hs ");
		    sbSql.append("\n           WHERE hs.id_tipo_saldo = 90 ");
		    sbSql.append("\n               and hs.no_empresa = e.no_empresa ");
		    sbSql.append("\n               and hs.fec_valor = '" + funciones.ponerFecha(dto.getFecIni()) + "'");
		    sbSql.append("\n               and hs.no_cuenta = e.no_cuenta_emp ");
		    sbSql.append("\n               and hs.no_linea = ( ");
		    
		    if(ConstantesSet.gsDBM.equals("SYBASE"))
		        sbSql.append("\n        SELECT convert(int,id_divisa_soin) ");
		    else
		        sbSql.append("\n        SELECT id_divisa_soin ");
		    
		    sbSql.append("\n                   FROM cat_divisa ");
		    sbSql.append("\n                   WHERE id_divisa = '" + dto.getDivisa() + "'))");
		    
		    sbSql.append("\n       + ( SELECT coalesce(sum(hs.importe),0) ");
		    sbSql.append("\n           FROM hist_saldo hs ");
		    sbSql.append("\n           WHERE hs.id_tipo_saldo in (8) ");
		    sbSql.append("\n               and hs.no_empresa = e.no_empresa ");
		    sbSql.append("\n               and fec_valor between '" + funciones.ponerFecha(dto.getFecIni()) + "' ");
		    sbSql.append("\n                 and '" + funciones.ponerFecha(dto.getFecFin()) + "'");
		    sbSql.append("\n               and hs.no_linea = ( ");
		    
		    if(ConstantesSet.gsDBM.equals("SYBASE")) 
		        sbSql.append("\n        SELECT convert(int,id_divisa_soin)");
		    else
		        sbSql.append("\n        SELECT id_divisa_soin ");
		    
		    
		    sbSql.append("\n                   FROM cat_divisa ");
		    sbSql.append("\n                   WHERE id_divisa = '" + dto.getDivisa() + "') ");
		    sbSql.append("\n         ) ");
		    
		    sbSql.append("\n       - ( SELECT coalesce(sum(hs.importe),0) ");
		    sbSql.append("\n           FROM hist_saldo hs ");
		    sbSql.append("\n           WHERE hs.id_tipo_saldo in (9) ");
		    sbSql.append("\n               and hs.no_empresa = e.no_empresa ");
		    sbSql.append("\n               and fec_valor between '" + funciones.ponerFecha(dto.getFecIni()) + "' ");
		    sbSql.append("\n                 and '" + funciones.ponerFecha(dto.getFecFin()) + "'");
		    sbSql.append("\n               and hs.no_linea = ( ");
		    
		    if(ConstantesSet.gsDBM.equals("SYBASE")) 
		        sbSql.append("\n        SELECT convert(int,id_divisa_soin) ");
		    else
		        sbSql.append("\n        SELECT id_divisa_soin ");
		   
		    sbSql.append("\n                   FROM cat_divisa ");
		    sbSql.append("\n                   WHERE id_divisa = '" + dto.getDivisa() + "')    ");
		    sbSql.append("\n         )  ");
		    
		    sbSql.append("\n , 0) as importe");
		    sbSql.append("\n FROM empresa e ");
		    sbSql.append("\n WHERE e.no_empresa = " + dto.getIdEmpresa());
		    
		    bdSaldoFinal = jdbcTemplate.queryForObject(sbSql.toString(), BigDecimal.class);
		    
		    uSaldoFinal = bdSaldoFinal.doubleValue();
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarSaldoFinal");
		}
		return uSaldoFinal;
	}
	
	/**
	 * FunObtenValoresCredito
	 * @param iIdEmpresa
	 * @param iMes
	 * @param iAnio
	 * @param sIdDivisa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<InteresPrestamoDto> consultarValoresDeCredito(int iIdEmpresa, int iMes, 
														int iAnio, String sIdDivisa)
	{
		sbSql = new StringBuffer();
		List<InteresPrestamoDto> listValCred = new ArrayList<InteresPrestamoDto>();
		try
		{
		    sbSql.append("SELECT * ");
		    sbSql.append("\n FROM interes_prestamo ");
		    
		    if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
		    {
		    	sbSql.append("\n WHERE (Date_Part('month', fec_inicio_calculo) = " + iMes);
		        sbSql.append("\n       or Date_Part('month', fec_fin_calculo) = " + iMes + ")");
		        sbSql.append("\n     and Date_Part('year', fec_fin_calculo) = " + iAnio);
		    }
		    else if(ConstantesSet.gsDBM.equals("DB2"))
		    {
		    	sbSql.append("\n WHERE (month(fec_inicio_calculo) = " + iMes);
		        sbSql.append("\n       or month(fec_fin_calculo) = " + iMes + ")");
		        sbSql.append("\n     and year(fec_fin_calculo) = " + iAnio);
		    }
		    else
		    {
		    	sbSql.append("\n WHERE (DatePart(m, fec_inicio_calculo) = " + iMes);
		        sbSql.append("\n       or DatePart(m, fec_fin_calculo) = " + iMes + ")");
		        sbSql.append("\n     and DatePart(year, fec_fin_calculo) = " + iAnio);
		    }
		    
		    sbSql.append("\n     and id_divisa = '" + sIdDivisa + "'");
		    sbSql.append("\n     and no_empresa = " + iIdEmpresa);
		    
		    listValCred = jdbcTemplate.query(sbSql.toString(), new RowMapper()
		    {
		    	public InteresPrestamoDto mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		InteresPrestamoDto dto = new InteresPrestamoDto();
		    			dto.setSaldoPromedio(rs.getBigDecimal("saldo_promedio").doubleValue());
		    			dto.setInteres(rs.getBigDecimal("interes").doubleValue());
		    			dto.setIva(rs.getBigDecimal("iva").doubleValue());
		    			dto.setTasa(rs.getBigDecimal("tasa").doubleValue());
		    		return dto;
		    	}
		    });
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarValoresDeCredito");
		}
		return listValCred;
	}
	
	/**
	 *
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarReporteEstadoDeCuenta(ParamComunDto dto)
	{
		sbSql = new StringBuffer();
		List<Map<String, Object>> listMapDat = new ArrayList<Map<String, Object>>();
		try
		{
			sbSql.append("\n   SELECT hs.importe, ");
		    sbSql.append("\n       CASE WHEN hs.id_tipo_saldo = 8 ");
		    sbSql.append("\n            THEN 'CREDITO' ");
		    sbSql.append("\n            ELSE 'PAGO CREDITO' ");
		    sbSql.append("\n       END as tipo, ");
		    sbSql.append("\n       hs.fec_valor ");
		    sbSql.append("\n   FROM hist_saldo hs ");
		    sbSql.append("\n   WHERE hs.id_tipo_saldo in (8,9) ");
		    sbSql.append("\n       and hs.no_empresa = " + dto.getIdEmpresa());
		    sbSql.append("\n       and hs.fec_valor between '" + funciones.ponerFecha(dto.getFecIni()) + "' ");
		    sbSql.append("\n           and '" + funciones.ponerFecha(dto.getFecFin()) + "' ");
		    sbSql.append("\n       and hs.no_linea = ( ");
		    sbSql.append("\n          SELECT id_divisa_soin ");
		    sbSql.append("\n          FROM cat_divisa ");
		    sbSql.append("\n          WHERE id_divisa = '" + dto.getDivisa() + "') ");
		    sbSql.append("\n       and ( SELECT coalesce(sum(hs1.importe), 0) ");
		    sbSql.append("\n             FROM hist_saldo hs1 ");
		    sbSql.append("\n             WHERE hs1.id_tipo_saldo in (8,9) ");
		    sbSql.append("\n                 and hs1.no_empresa = hs.no_empresa ");
		    sbSql.append("\n                 and hs1.fec_valor = hs.fec_valor ");
		    sbSql.append("\n                 and hs1.no_linea = hs.no_linea ) > 0 ");
		    
		    if((dto.getFecFin() == dto.getFecHoy())
		    		|| dto.getFecIni() == dto.getFecHoy())
		    {
		    	sbSql.append("\n   UNION ");
		        sbSql.append("\n   SELECT s.importe, ");
		        sbSql.append("\n       CASE WHEN s.id_tipo_saldo = 8 ");
		        sbSql.append("\n            THEN 'CREDITO' ");
		        sbSql.append("\n            ELSE 'PAGO CREDITO' ");
		        sbSql.append("\n       END as tipo, ");
		        sbSql.append("\n       ( SELECT fec_hoy ");
		        sbSql.append("\n         FROM fechas ) as fec_valor");
		        sbSql.append("\n   FROM saldo s ");
		        sbSql.append("\n   WHERE s.id_tipo_saldo in (8,9) ");
		        sbSql.append("\n       and s.no_empresa = " + dto.getIdEmpresa());
		        sbSql.append("\n       and s.no_linea = ( ");
		        sbSql.append("\n          SELECT id_divisa_soin ");
		        sbSql.append("\n          FROM cat_divisa ");
		        sbSql.append("\n          WHERE id_divisa = '" + dto.getDivisa() + "') ");
		        sbSql.append("\n       and ( SELECT coalesce(sum(s1.importe), 0) ");
		        sbSql.append("\n             FROM saldo s1 ");
		        sbSql.append("\n             WHERE s1.id_tipo_saldo in (8,9) ");
		        sbSql.append("\n                 and s1.no_empresa = s.no_empresa ");
		        sbSql.append("\n                 and s1.no_linea = s.no_linea ) > 0 ");
		        sbSql.append("\n   ORDER BY fec_valor");
		    }
		    
		    listMapDat = jdbcTemplate.query(sbSql.toString(), new RowMapper()
		    {
		    	public Map mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		Map<String,Object> map = new HashMap<String, Object>();
		    			map.put("importe", rs.getBigDecimal("importe").doubleValue());
		    			map.put("tipoSaldo", rs.getString("tipo"));
		    			map.put("fecValor", rs.getDate("fec_valor"));
		    		return map;
		    	}
		    });
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarReporteEstadoDeCuenta");
		}
		return listMapDat;
	}
	
	/**
	 * FunSQLSelectMINMAXFecha
	 * @param sTipo
	 * @param iNoEmpresa
	 * @param iNoCuenta
	 * @param iNoLinea
	 * @param dFecIni
	 * @param dFecFin
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Date consultarFechaMinMax(String sTipo, int iNoEmpresa, int iNoCuenta, 
										int iNoLinea, Date dFecIni, Date dFecFin)
	{
		sbSql = new StringBuffer();
		Date dFecha = null;
		try
		{
		    sbSql.append("\n select ");
		    
		    if(sTipo.trim().equals("MIN"))
		        sbSql.append("\n min(fec_valor) as fecha");
		    else
		        sbSql.append("\n max(fec_valor) as fecha");
		    
		    sbSql.append("\n From hist_saldo");
		    sbSql.append("\n Where no_empresa = " + iNoEmpresa);
		    sbSql.append("\n and id_tipo_saldo = 90");
		    sbSql.append("\n and no_cuenta = " + iNoCuenta);
		    sbSql.append("\n and no_linea = " + iNoLinea);
		    sbSql.append("\n and fec_valor >= '" + funciones.ponerFecha(dFecIni) + "'");
		    sbSql.append("\n and fec_valor <= '" + funciones.ponerFecha(dFecFin) + "'");
		    
			List<Date> fechas = jdbcTemplate.query(sbSql.toString(), new RowMapper() {
				public Date mapRow(ResultSet rs, int idx) throws SQLException 
					{
						return rs.getDate("fecha");
					}
				}); 
		    dFecha = fechas.get(0);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarFechaMinMax");
		}
		return dFecha;
	}
	
	/**
	 * M�todo para borrar los datos relacionados con el usuario, de la tabla
	 * tmp_edocuenta
	 * @param iIdUsuario
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int borrarTmpEdoCuenta(int iIdUsuario)
	{
		int iRegAfec = 0;
		StringBuffer sbSql = new StringBuffer();
		try
		{
		    sbSql.append("Delete from");
		    sbSql.append("\n tmp_edocuenta");
		    sbSql.append("\n where ");
		    sbSql.append("\n usuario_alta = " + iIdUsuario);
		    
		    iRegAfec = jdbcTemplate.update(sbSql.toString());
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:borrarTmpEdoCuenta");
		}
		return iRegAfec;
	}
	
	/**
	 * FunSQLSelectSaldoDepRet
	 * @param iNoEmpresa
	 * @param iNoCuenta
	 * @param iNoLinea
	 * @param dFecha
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<HistSaldoDto> consultarSaldoDepRet(int iNoEmpresa, int iNoCuenta, int iNoLinea, Date dFecha)
	{
		sbSql = new StringBuffer();
		List<HistSaldoDto> listCons = new ArrayList<HistSaldoDto>();
		try
		{
			if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
			{
				sbSql.append("Select ");
	            sbSql.append("\n fec_valor");
	            sbSql.append("\n,'SALDO INICIAL' as concepto");
	            sbSql.append("\n,convert(char,importe) as sdo_ini");
	            sbSql.append("\n,0.0 as depositos");
	            sbSql.append("\n,0.0 as retiros");
	            sbSql.append("\n,0.0 as sdo_fin");
	            sbSql.append("\n,1 as etiqueta");
	            sbSql.append("\n From ");
	            sbSql.append("\n hist_saldo");
	            sbSql.append("\n Where ");
	            sbSql.append("\n no_empresa = " + iNoEmpresa);
	            sbSql.append("\n and no_cuenta = " + iNoCuenta);
	            sbSql.append("\n and no_linea = " + iNoLinea);
	            sbSql.append("\n and fec_valor = '" + funciones.ponerFecha(dFecha) + "'");
	            sbSql.append("\n and id_tipo_saldo = 90");
			}
			else if(ConstantesSet.gsDBM.equals("POSTGRESQL") || ConstantesSet.gsDBM.equals("DB2"))
			{
				sbSql.append("Select ");
	            sbSql.append("\n fec_valor");
	            sbSql.append("\n,'SALDO INICIAL' as concepto");
	            
	            if(ConstantesSet.gsDBM.equals("DB2"))
	               sbSql.append("\n,cast(importe as varchar(18)) as sdo_ini");
	            else
	               sbSql.append("\n,to_char(importe,'999999999990D99') as sdo_ini");
	            
	            sbSql.append("\n,0.0 as depositos");
	            sbSql.append("\n,0.0 as retiros");
	            sbSql.append("\n,0.0 as sdo_fin");
	            sbSql.append("\n,1 as etiqueta");
	            sbSql.append("\n From ");
	            sbSql.append("\n hist_saldo");
	            sbSql.append("\n Where ");
	            sbSql.append("\n no_empresa = " + iNoEmpresa);
	            sbSql.append("\n and no_cuenta = " + iNoCuenta);
	            sbSql.append("\n and no_linea = " + iNoLinea);
	            sbSql.append("\n and fec_valor = '" + funciones.ponerFecha(dFecha) + "'");
	            sbSql.append("\n and id_tipo_saldo = 90");
			}
			
			listCons = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
				public HistSaldoDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					HistSaldoDto dto = new HistSaldoDto();
						dto.setFecValor(rs.getDate("fec_valor"));
						dto.setConcepto(rs.getString("concepto"));
						dto.setSdoIni(rs.getBigDecimal("sdo_ini").doubleValue());
						dto.setDepositos(rs.getBigDecimal("depositos").doubleValue());
						dto.setRetiros(rs.getBigDecimal("retiros").doubleValue());
						dto.setSdoFin(rs.getBigDecimal("sdo_fin").doubleValue());
						dto.setEtiqueta(rs.getInt("etiqueta"));
					return dto;
				}
			});
       
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarSaldoDepRet");
		}
		return listCons;
	}
	
	/**
	 * FunSQLInsertTmpEdoCuenta
	 * @param dto
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int insertarTmpEdoCuenta(TmpEdoCuentaDto dto)
	{
		sbSql = new StringBuffer();
		int iRegAfec = 0;
		try
		{
		    sbSql.append("Insert ");
		    sbSql.append("\n into ");
		    sbSql.append("\n tmp_edocuenta ");
		    sbSql.append("\n ( ");
		    sbSql.append("\n usuario_alta");
		    sbSql.append("\n,secuencia");
		    sbSql.append("\n,fecha");
		    sbSql.append("\n,concepto");
		    sbSql.append("\n,saldo_inicial");
		    sbSql.append("\n,depositos");
		    sbSql.append("\n,retiros");
		    sbSql.append("\n,saldo_final");
		    sbSql.append("\n,etiqueta");
		    sbSql.append("\n )");
		    sbSql.append("\n values ");
		    sbSql.append("\n ( ");
		    sbSql.append("\n" + dto.getUsuarioAlta());
		    sbSql.append("\n," + dto.getSecuencia());
		    sbSql.append("\n,'" + funciones.ponerFecha(dto.getFecha()) + "'");
		    sbSql.append("\n,'" + dto.getConcepto() + "'");
		    sbSql.append("\n," + dto.getSaldoInicial());
		    sbSql.append("\n," + dto.getDepositos());
		    sbSql.append("\n," + dto.getRetiros());
		    sbSql.append("\n," + dto.getSaldoFinal());
		    sbSql.append("\n," + dto.getEtiqueta());
		    sbSql.append("\n )");
		    
		    iRegAfec = jdbcTemplate.update(sbSql.toString());
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:insertarTmpEdoCuenta");
		}
		return iRegAfec;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarSaldoDepRetDetalle(boolean bFecIgualHoy, int iNoEmpresa, int iNoCuenta,
															String sIdDivisa, Date dFecIni, Date dFecFin)
	{
		sbSql = new StringBuffer();
		List<Map<String, Object>> listSaldo = new ArrayList<Map<String, Object>>();
		try
		{
		    sbSql.append("SELECT no_folio_det, fec_valor_original as fec_valor, m.concepto, ");
		    sbSql.append("\n     0.0 as sdo_ini, ");
		    
		    if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
		    {
		    	sbSql.append("\n to_char(CASE WHEN m.id_tipo_movto = 'I' ");
		        sbSql.append("\n                    THEN m.importe END, '999999999990D99') as depositos, ");
		        sbSql.append("\n to_char(CASE WHEN m.id_tipo_movto = 'E' ");
		        sbSql.append("\n                    THEN m.importe end, '999999999990D99') as retiros, ");
		    }
		    else if(ConstantesSet.gsDBM.equals("DB2"))
		    {
		    	sbSql.append("\n cast(CASE WHEN m.id_tipo_movto = 'I' ");
		        sbSql.append("\n                    THEN m.importe END as varchar(18)) as depositos, ");
		        sbSql.append("\n cast(CASE WHEN m.id_tipo_movto = 'E' ");
		        sbSql.append("\n                    THEN m.importe end as varchar(18)) as retiros, ");
		    }
		    else
		    {
		    	sbSql.append("\n convert(char, CASE WHEN m.id_tipo_movto = 'I' ");
		        sbSql.append("\n                    THEN m.importe END) as depositos, ");
		        sbSql.append("\n convert(char, CASE WHEN m.id_tipo_movto = 'E' ");
		        sbSql.append("\n                    THEN m.importe end) as retiros, ");
		    }
		    
		    sbSql.append("\n     0.0 as sdo_fin, 2 as etiqueta ");
		    sbSql.append("\n FROM movimiento m ");
		    sbSql.append("\n WHERE ( ( m.id_tipo_operacion = 3708 and id_tipo_movto = 'I') ");
		    sbSql.append("\n     or  ( m.id_tipo_operacion = 3709 and id_tipo_movto = 'E')) ");
		    sbSql.append("\n     and no_empresa = " + iNoEmpresa);
		    sbSql.append("\n     and no_cuenta =  " + iNoCuenta);
		    sbSql.append("\n     and id_divisa like '" + sIdDivisa + "'");
		    sbSql.append("\n     and fec_valor_original between '" + funciones.ponerFecha(dFecIni) + "' ");
		    sbSql.append("\n         and '" + funciones.ponerFecha(dFecFin) + "'");
		    sbSql.append("\n     and id_estatus_mov = 'A' ");
		    
		    sbSql.append("\n UNION ALL ");
		    
		    sbSql.append("\n SELECT no_folio_det, fec_valor_original as fec_valor, m.concepto, ");
		    sbSql.append("\n     0.0 as sdo_ini, ");
		    
		    if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
		    {
		    	sbSql.append("\n to_char(CASE WHEN m.id_tipo_movto = 'I' ");
		        sbSql.append("\n                    THEN m.importe END, '999999999990D99') as depositos, ");
		        sbSql.append("\n to_char(CASE WHEN m.id_tipo_movto = 'E' ");
		        sbSql.append("\n                    THEN m.importe end, '999999999990D99') as retiros, ");
		    }
		    else if(ConstantesSet.gsDBM.equals("DB2"))  
		    {
		    	sbSql.append("\n cast(CASE WHEN m.id_tipo_movto = 'I' ");
		        sbSql.append("\n                    THEN m.importe END as varchar(15)) as depositos, ");
		        sbSql.append("\n cast(CASE WHEN m.id_tipo_movto = 'E' ");
		        sbSql.append("\n                    THEN m.importe end as varchar(15)) as retiros, ");
		    }
		    else
		    {
		    	sbSql.append("\n convert(char, CASE WHEN m.id_tipo_movto = 'I' ");
		        sbSql.append("\n                    THEN m.importe END) as depositos, ");
		        sbSql.append("\n convert(char, CASE WHEN m.id_tipo_movto = 'E' ");
		        sbSql.append("\n                    THEN m.importe end) as retiros, ");
		    }
		        
		    sbSql.append("\n     0.0 as sdo_fin, 2 as etiqueta ");
		    sbSql.append("\n FROM hist_movimiento m ");
		    sbSql.append("\n WHERE ( ( m.id_tipo_operacion = 3708 and id_tipo_movto = 'I') ");
		    sbSql.append("\n     or  ( m.id_tipo_operacion = 3709 and id_tipo_movto = 'E')) ");
		    sbSql.append("\n     and no_empresa = " + iNoEmpresa);
		    sbSql.append("\n     and no_cuenta =  " + iNoCuenta);
		    sbSql.append("\n     and id_divisa like '" + sIdDivisa+ "'");
		    sbSql.append("\n     and fec_valor_original between '" + funciones.ponerFecha(dFecIni) + "' ");
		    sbSql.append("\n         and '" + funciones.ponerFecha(dFecFin) + "'");
		    sbSql.append("\n     and id_estatus_mov = 'A' ");
		    
		    //' Intereses que afectanm el saldo
		    
		    sbSql.append("\n UNION ALL ");
		    
		    sbSql.append("\n select no_folio_det, fec_valor_original as fec_valor, m.concepto");
		    sbSql.append("\n ,0.0 as sdo_ini");
		    
		    if(ConstantesSet.gsDBM.equals("SYBASE"))
		    {
		    	sbSql.append("\n ,convert(varchar, case when m.id_tipo_movto = 'E'  then m.importe end) as depositos ");
		        sbSql.append("\n ,convert(varchar, case when m.id_tipo_movto = 'I'  then m.importe end) as retiros");
		    }
		    else if(ConstantesSet.gsDBM.equals("DB2"))
		    {
		    	sbSql.append("\n ,cast(case when m.id_tipo_movto = 'E'  then m.importe end as varchar(18)) as depositos ");
		        sbSql.append("\n ,cast(case when m.id_tipo_movto = 'I'  then m.importe end as varchar(18)) as retiros");
		    }
		    else
		    {
		    	 sbSql.append("\n ,cast(case when m.id_tipo_movto = 'E'  then m.importe end as varchar) as depositos ");
			     sbSql.append("\n ,cast(case when m.id_tipo_movto = 'I'  then m.importe end as varchar) as retiros");
		    }
		    
		    sbSql.append("\n ,0.0 as sdo_fin");
		    sbSql.append("\n ,2 as etiqueta");
		    sbSql.append("\n  from movimiento m ");
		    
		    sbSql.append("\n  Where m.id_tipo_operacion = 8100 ");
		    sbSql.append("\n  and id_tipo_movto = 'E' ");
		    sbSql.append("\n  and no_empresa = " + iNoEmpresa);
		    sbSql.append("\n  and no_cuenta =  " + iNoCuenta);
		    sbSql.append("\n  and id_divisa like '" + sIdDivisa + "'");
		    sbSql.append("\n  and fec_valor_original between '" + funciones.ponerFecha(dFecIni) + "' and  '" + funciones.ponerFecha(dFecFin) + "'");
		    sbSql.append("\n  and id_estatus_mov = 'A'");
		    
		    sbSql.append("\n  UNION ALL ");
		    
		    sbSql.append("\n select no_folio_det, fec_valor_original as fec_valor ");
		    sbSql.append("\n ,m.concepto, 0.0 as sdo_ini");
		    
		    if(ConstantesSet.gsDBM.equals("SYBASE"))
		    {
		    	sbSql.append("\n ,convert(varchar, case when m.id_tipo_movto = 'E'  then m.importe end) as depositos ");
		        sbSql.append("\n ,convert(varchar, case when m.id_tipo_movto = 'I'  then m.importe end) as retiros");

		    }
		    else if(ConstantesSet.gsDBM.equals("DB2"))
		    {
		    	sbSql.append("\n ,cast(case when m.id_tipo_movto = 'E'  then m.importe end as varchar(18)) as depositos ");
		        sbSql.append("\n ,cast(case when m.id_tipo_movto = 'I'  then m.importe end as varchar(18)) as retiros");
		    }
		    else
		    {
		    	sbSql.append("\n ,cast(case when m.id_tipo_movto = 'E'  then m.importe end as varchar) as depositos ");
		        sbSql.append("\n ,cast(case when m.id_tipo_movto = 'I'  then m.importe end as varchar) as retiros");
		    }
		        
		    sbSql.append("\n ,0.0 as sdo_fin");
		    sbSql.append("\n ,2 as etiqueta");
		    sbSql.append("\n  from ");
		    sbSql.append("\n  hist_movimiento m");
		    
		    sbSql.append("\n  Where m.id_tipo_operacion = 8100 ");
		    sbSql.append("\n  and id_tipo_movto = 'E' ");
		    sbSql.append("\n  and no_empresa = " + iNoEmpresa);
		    sbSql.append("\n  and no_cuenta =  " + iNoCuenta);
		    sbSql.append("\n  and id_divisa like '" + sIdDivisa + "'");
		    sbSql.append("\n  and fec_valor_original between '" + funciones.ponerFecha(dFecIni) + "' and  '" + funciones.ponerFecha(dFecFin) + "'");
		    sbSql.append("\n  and id_estatus_mov = 'A'");
		    
		    sbSql.append("\n ORDER BY fec_valor ,no_folio_det");
		    
		    listSaldo = jdbcTemplate.query(sbSql.toString(), new RowMapper()
		    {
				public Map mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		Map<String, Object> map = new HashMap<String, Object>();
		    			map.put("noFolioDet", rs.getInt("no_folio_det"));
		    			map.put("fecValor", rs.getDate("fec_valor"));
		    			map.put("concepto", rs.getString("concepto"));
		    			if(rs.getBigDecimal("depositos") != null)
		    				map.put("depositos", rs.getBigDecimal("depositos").doubleValue());
		    			else
		    				map.put("depositos", 0D);
		    			
		    			if(rs.getBigDecimal("retiros") != null)
			    			map.put("retiros", rs.getBigDecimal("retiros").doubleValue());
		    			else
		    				map.put("retiros", 0D);
			    			
		    			if(rs.getBigDecimal("sdo_fin") != null)
			    			map.put("saldofin", rs.getBigDecimal("sdo_fin").doubleValue());
		    			else
		    				map.put("saldofin", 0D);
		    			
		    			map.put("etiqueta", rs.getInt("etiqueta"));
		    		return map;
		    	}
		    });
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarSaldoDepRetDetalle");
		}
		return listSaldo;
	}
	
	/**
	 * FunSQLSelectSaldoFin
	 * Este m�todo obtiene el saldo final de la tabla saldo
	 * @param bFecFinIgualHoy
	 * @param iNoEmpresa
	 * @param iNoLinea
	 * @param iNoCuenta
	 * @param dFecHoy
	 * @param dFecFin
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarSaldoFin(boolean bFecFinIgualHoy, int iNoEmpresa, int iNoLinea,
																int iNoCuenta, Date dFecHoy, Date dFecFin)
	{
		sbSql = new StringBuffer();
		List<Map<String, Object>> listSaldo = new ArrayList<Map<String, Object>>();
		try
		{
			if(ConstantesSet.gsDBM.equals("SYBASE"))
			{
				if(bFecFinIgualHoy)
				{
					sbSql.append("Select ");
		            sbSql.append("\n fec_valor = '" + funciones.ponerFecha(dFecHoy)+ "'  ");
		            sbSql.append("\n,'SALDO FINAL' as concepto");
		            sbSql.append("\n,0.0 as sdo_ini");
		            sbSql.append("\n,0.0 as depositos");
		            sbSql.append("\n,0.0 as retiros");
		            sbSql.append("\n,convert(varchar, sum(case when id_tipo_saldo = 9 then  importe *-1 else importe end)) as sdo_fin ");
		            sbSql.append("\n, 3 as etiqueta");
		            sbSql.append("\n From saldo");
		            sbSql.append("\n Where no_empresa = " + iNoEmpresa);
		            sbSql.append("\n and no_linea =  " + iNoLinea);
		            sbSql.append("\n and no_cuenta = " + iNoCuenta);
		            sbSql.append("\n and id_tipo_saldo in (90,8,9)");
				}
				else
				{
					sbSql.append("Select ");
		            sbSql.append("\n fec_valor");
		            sbSql.append("\n,'SALDO FINAL' as concepto");
		            sbSql.append("\n,0.0 as sdo_ini");
		            sbSql.append("\n,0.0 as depositos");
		            sbSql.append("\n,0.0 as retiros");
		            sbSql.append("\n,convert(varchar,importe) as sdo_fin ");
		            sbSql.append("\n, 3 as etiqueta");
		            sbSql.append("\n From hist_saldo");
		            sbSql.append("\n Where no_empresa = " + iNoEmpresa);
		            sbSql.append("\n and no_linea =  " + iNoLinea);
		            sbSql.append("\n and no_cuenta = " + iNoCuenta);
		            sbSql.append("\n and fec_valor = '" + funciones.ponerFecha(dFecFin)+ "'");
		            sbSql.append("\n and id_tip-o_saldo = 91");
				}
			}
			else if(ConstantesSet.gsDBM.equals("SQL SERVER"))
			{
				if(bFecFinIgualHoy)
				{
					sbSql.append("Select ");
		            sbSql.append("\n fec_valor = '" + funciones.ponerFecha(dFecHoy) + "'  ");
		            sbSql.append("\n,'SALDO FINAL' as concepto");
		            sbSql.append("\n,0.0 as sdo_ini");
		            sbSql.append("\n,0.0 as depositos");
		            sbSql.append("\n,0.0 as retiros");
		            sbSql.append("\n,cast(sum(case when id_tipo_saldo = 9 then  importe *-1 else importe end) as varchar) as sdo_fin ");
		            sbSql.append("\n, 3 as etiqueta");
		            sbSql.append("\n From saldo");
		            sbSql.append("\n Where no_empresa = " + iNoEmpresa);
		            sbSql.append("\n and no_linea =  " + iNoLinea);
		            sbSql.append("\n and no_cuenta = " + iNoCuenta);
		            sbSql.append("\n and id_tipo_saldo in (90,8,9)");
				}
				else
				{
		            sbSql.append("Select ");
		            sbSql.append("\n fec_valor");
		            sbSql.append("\n,'SALDO FINAL' as concepto");
		            sbSql.append("\n,0.0 as sdo_ini");
		            sbSql.append("\n,0.0 as depositos");
		            sbSql.append("\n,0.0 as retiros");
		            sbSql.append("\n,cast(importe as varchar) as sdo_fin ");
		            sbSql.append("\n, 3 as etiqueta");
		            sbSql.append("\n From hist_saldo");
		            sbSql.append("\n Where no_empresa = " + iNoEmpresa);
		            sbSql.append("\n and no_linea =  " + iNoLinea);
		            sbSql.append("\n and no_cuenta = " + iNoCuenta);
		            sbSql.append("\n and fec_valor = '" + funciones.ponerFecha(dFecFin)+ "'");
		            sbSql.append("\n and id_tipo_saldo = 91");
				}
			}
			else if(ConstantesSet.gsDBM.equals("POSTGRESQL") || ConstantesSet.gsDBM.equals("DB2"))
			{
				if(bFecFinIgualHoy)
				{
					sbSql.append("Select ");
		            sbSql.append("\n '" + funciones.ponerFecha(dFecHoy)+ "' as fec_valor ");
		            sbSql.append("\n,'SALDO FINAL' as concepto");
		            sbSql.append("\n,0.0 as sdo_ini");
		            sbSql.append("\n,0.0 as depositos");
		            sbSql.append("\n,0.0 as retiros");
		            
		            if(ConstantesSet.gsDBM.equals("DB2"))
		               sbSql.append("\n,cast(sum(case when id_tipo_saldo = 9 then  importe *-1 else importe end) as varchar(18)) as sdo_fin ");
		            else
		               sbSql.append("\n,cast(sum(case when id_tipo_saldo = 9 then  importe *-1 else importe end) as varchar) as sdo_fin ");
		            
		            sbSql.append("\n, 3 as etiqueta");
		            sbSql.append("\n From saldo");
		            sbSql.append("\n Where no_empresa = " + iNoEmpresa);
		            sbSql.append("\n and no_linea =  " + iNoLinea);
		            sbSql.append("\n and no_cuenta = " + iNoCuenta);
		            sbSql.append("\n and id_tipo_saldo in (90,8,9)");
				}
				else
				{
					sbSql.append("Select ");
		            sbSql.append("\n fec_valor");
		            sbSql.append("\n,'SALDO FINAL' as concepto");
		            sbSql.append("\n,0.0 as sdo_ini");
		            sbSql.append("\n,0.0 as depositos");
		            sbSql.append("\n,0.0 as retiros");
		            
		            
		            if(ConstantesSet.gsDBM.equals("DB2"))
		               sbSql.append("\n,cast(importe as varchar(18)) as sdo_fin ");
		            else
		               sbSql.append("\n,cast(importe as varchar) as sdo_fin ");
		                                    
		            sbSql.append("\n, 3 as etiqueta");
		            sbSql.append("\n From hist_saldo");
		            sbSql.append("\n Where no_empresa = " + iNoEmpresa);
		            sbSql.append("\n and no_linea =  " + iNoLinea);
		            sbSql.append("\n and no_cuenta = " + iNoCuenta);
		            sbSql.append("\n and fec_valor = '" + funciones.ponerFecha(dFecFin)+ "'");
		            sbSql.append("\n and id_tipo_saldo = 91");
				}
			}
			
			listSaldo = jdbcTemplate.query(sbSql.toString(), new RowMapper()
		    {
				public Map mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		Map<String, Object> map = new HashMap<String, Object>();
		    			map.put("fecValor", rs.getDate("fec_valor"));
		    			map.put("concepto", rs.getString("concepto"));
		    			map.put("saldoIni", rs.getBigDecimal("sdo_ini").doubleValue());
		    			map.put("depositos", rs.getBigDecimal("depositos").doubleValue());
		    			map.put("retiros", rs.getBigDecimal("retiros").doubleValue());
		    			map.put("saldofin", rs.getBigDecimal("sdo_fin").doubleValue());
		    			map.put("etiqueta", rs.getInt("etiqueta"));
		    		return map;
		    	}
		    });
			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarSaldoFinSaldo");
		}
		return listSaldo;
	}
	
	/**
	 * FunSQLSelectDatosEmpresa
	 * @param iNoEmpresa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<PersonaDto> consultarDatosEmpresa(int iNoEmpresa)
	{
		sbSql = new StringBuffer();
		List<PersonaDto> listDatos = new ArrayList<PersonaDto>();
		try
		{
			if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
			{
				sbSql.append("Select ");
		        sbSql.append("\n rfc ");
		        sbSql.append("\n,calle_no " + "  +");
		        sbSql.append("\n colonia " + "  +");
		        sbSql.append("\n ciudad  ");
		        sbSql.append("\n as direccion ");
		        sbSql.append("\n From ");
		        sbSql.append("\n persona p left join direccion d on (p.no_empresa = d.no_empresa and p.no_persona = d.no_persona)");
		        sbSql.append("\n Where  ");
		        sbSql.append("\n p.no_persona = " + iNoEmpresa);
		        sbSql.append("\n And p.id_tipo_persona  in ('E','I')");
			}
			else if(ConstantesSet.gsDBM.equals("POSTGRESQL") || ConstantesSet.gsDBM.equals("DB2"))
			{
				sbSql.append("\n Select ");
		        sbSql.append("\n rfc ");
		        sbSql.append("\n,calle_no || ' ' ||");
		        sbSql.append("\n colonia || ' ' ||");
		        sbSql.append("\n ciudad  ");
		        sbSql.append("\n as direccion ");
		        sbSql.append("\n From ");
		        sbSql.append("\n persona p left join direccion d on (p.no_empresa = d.no_empresa and p.no_persona = d.no_persona)");
		        sbSql.append("\n Where  ");
		        sbSql.append("\n p.no_persona = " + iNoEmpresa);
		        sbSql.append("\n And p.id_tipo_persona  in ('E','I')");
			}
	        
			listDatos = jdbcTemplate.query(sbSql.toString(), new RowMapper()
			{
				public PersonaDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					PersonaDto dto = new PersonaDto();
						dto.setRfc(rs.getString("rfc"));
						dto.setDireccion(rs.getString("direccion"));
					return dto;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarDatosEmpresa");
		}
		return listDatos;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarReporteEdoCuentaDetallado(int iIdUsuario)
	{
		sbSql = new StringBuffer();
		List<Map<String, Object>> listRep = new ArrayList<Map<String, Object>>();
		try
		{
			sbSql.append("Select ");
		    sbSql.append("\n	fecha as fec_valor, concepto, saldo_inicial as sdo_ini,");
		    sbSql.append("\n 	depositos as depositos,retiros as retiros, ");
		    sbSql.append("\n 	saldo_final as sdo_fin, etiqueta ");
		    sbSql.append("\n 	From tmp_edocuenta ");
		    sbSql.append("\n 	Where usuario_alta = " + iIdUsuario);
		    
		    listRep = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
				public Map mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		Map<String, Object> map = new HashMap<String, Object>();
		    			map.put("fecValor", rs.getDate("fec_valor"));
		    			map.put("concepto", rs.getString("concepto"));
		    			map.put("saldoInicial", rs.getBigDecimal("sdo_ini").doubleValue());
		    			map.put("depositos", rs.getBigDecimal("depositos").doubleValue());
		    			map.put("retiros", rs.getBigDecimal("retiros").doubleValue());
		    			map.put("saldoFinal", rs.getBigDecimal("sdo_fin").doubleValue());
		    			map.put("etiqueta", rs.getInt("etiqueta"));
		    		return map;
		    	}
		    });
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarReporteEdoCuentaDetallado");
		}
		return listRep;
	}
	
	//Inician m�todos para ReporteDeSolicitudes
	
	/**
	 * FunSQLReporte4, FunSQLReporte3
	 * @param iEmpresaInf
	 * @param iEmpresaSup
	 * @param sIdDivisa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarReporteSolicitudesDeCredito(int iEmpresaInf, int iEmpresaSup, 
																			String sIdDivisa, boolean bCredito)
	{
		sbSql = new StringBuffer();
		List<Map<String, Object>> listSolCred = new ArrayList<Map<String, Object>>();
		try
		{
			sbSql.append("Select a.no_empresa,a.id_banco,b.desc_banco,a.id_chequera,a.importe,");
		    sbSql.append("\n a.id_caja,a.concepto,a.beneficiario,f.nom_empresa as originaria,id_tipo_movto, ");
		    sbSql.append("\n a.id_divisa,a.referencia, a.id_banco_benef,g.nom_empresa as beneficiaria,");
		    sbSql.append("\n d.desc_banco as desc_banco_benef,a.id_chequera_benef,cto.desc_tipo_operacion ");
		    sbSql.append("\n FROM movimiento a");
		    sbSql.append("\n LEFT JOIN cat_banco d ON (a.id_banco_benef = d.id_banco),");
		    sbSql.append("\n cat_banco b, empresa f, empresa g, ");
		    sbSql.append("\n cat_cta_banco z, cat_tipo_operacion cto");
		    sbSql.append("\n Where a.id_banco = b.id_banco ");
		    sbSql.append("\n and a.id_tipo_operacion = cto.id_tipo_operacion ");
		    sbSql.append("\n and a.id_chequera_benef = z.id_chequera");
		    sbSql.append("\n and a.no_empresa = f.no_empresa ");
		    sbSql.append("\n and z.no_empresa = g.no_empresa");
		    
		    if(bCredito)
		    {
		    	sbSql.append("\n and a.id_tipo_movto = 'I' ");
			    sbSql.append("\n and a.id_tipo_operacion in(3808,3708) ");
		    }
		    else
		    {
		    	sbSql.append("\n and a.id_tipo_movto='E' ");
				sbSql.append("\n and a.id_tipo_operacion in(3809,3709) ");
		    }
		    
		    sbSql.append("\n and a.no_empresa between " + iEmpresaInf + " and " + iEmpresaSup);
		    sbSql.append("\n and a.id_divisa = '" + sIdDivisa + "' ");
		    sbSql.append("\n and a.fec_valor=(select fec_hoy from fechas) ");
		    sbSql.append("\n order by 1,3");
		    
		    listSolCred = jdbcTemplate.query(sbSql.toString(), new RowMapper()
		    {
		    	public Map<String, Object> mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		Map<String, Object> map = new HashMap<String, Object>();
		    			map.put("noEmpresa", rs.getInt("no_empresa"));
		    			map.put("idBanco", rs.getInt("id_banco"));
		    			map.put("descBanco", rs.getString("desc_banco"));
		    			map.put("idChequera", rs.getString("id_chequera"));
		    			map.put("importe", rs.getBigDecimal("importe").doubleValue());
		    			map.put("idCaja", rs.getInt("id_caja"));
		    			map.put("concepto", rs.getString("concepto"));
		    			map.put("beneficiario", rs.getString("beneficiario"));
		    			map.put("nomEmpOrig", rs.getString("originaria"));
		    			map.put("idTipoMovto", rs.getString("id_tipo_movto"));
		    			map.put("idDivisa", rs.getString("idDivisa"));
		    			map.put("referencia", rs.getString("referencia"));
		    			map.put("idBancoBenef", rs.getString("id_banco_benef"));
		    			map.put("nomEmpBenef", rs.getString("beneficiaria"));
		    			map.put("descBancoBenef", rs.getString("desc_banco_benef"));
		    			map.put("idChequeraBenef", rs.getString("id_chequera_benef"));
		    			map.put("descTipoOperacion", rs.getString("desc_tipo_operacion"));
		    		return map;
		    	}
		    });
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: PrestamosInterempresas, C: PrestamosInterempresasDaoImpl, M:consultarReporteSolicitudesDeCredito");
		}
		return listSolCred;
	}
	

	//public void setDataSource2(DataSource dataSource){
	public void setDataSource(DataSource dataSource){
		try
		{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:PrestamosInterempresas, C:PrestamosInterempresasDaoImpl, M:setDataSource");
		}
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
}
