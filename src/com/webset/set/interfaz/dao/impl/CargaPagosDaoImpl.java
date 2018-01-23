package com.webset.set.interfaz.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.interfaz.dao.CargaPagosDao;
import com.webset.set.interfaz.dto.CargaPagosDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.ConstantesSet;

@SuppressWarnings("unchecked")
public class CargaPagosDaoImpl implements CargaPagosDao 
{
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora;
	Funciones funciones;
	ConsultasGenerales objConsultasGenerales;
	int valorNumerico = 0;
	String sql = "";
	List<CargaPagosDto> listaResultado = new ArrayList<CargaPagosDto>();
	StringBuffer sqlInsert = null;
	String gsDBM = ConstantesSet.gsDBM;
	
	//Logger
	private Logger logger = Logger.getLogger(CargaPagosDaoImpl.class);
		
	public List<CargaPagosDto> llenaComboEmpresas(int noUsuario)
	{		
	//	List<CargaPagosDto> listaResultado = new ArrayList<CargaPagosDto>();
		
		try
		{
			sql = "";
			sql += "SELECT no_empresa, nom_empresa FROM empresa WHERE no_empresa in (select no_empresa from usuario_empresa where no_usuario = "+ noUsuario +")";
			
			listaResultado= jdbcTemplate.query(sql, new RowMapper(){
				public CargaPagosDto mapRow(ResultSet rs, int idx) throws SQLException {
					CargaPagosDto cons = new CargaPagosDto();
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setNomEmpresa(rs.getString("nom_empresa"));
					return cons;
			}});
		}
		catch(Exception e)
		{			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:llenaComboEmpresas");
		}
		return listaResultado;
	}
	
	public String consultarConfiguraSet(int indice)
	{		
		objConsultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return objConsultasGenerales.consultarConfiguraSet(indice);
	}	
	
	public List<CargaPagosDto> sacaHouseBank(String houseBank)
	{
		try
		{
			sql = "";
			sql += "Select id_banco, no_empresa, * from cat_cta_banco ";
			sql += "where housebank = '" + houseBank + "' ";			
			listaResultado = jdbcTemplate.query(sql, new RowMapper()
			{
				public CargaPagosDto mapRow(ResultSet rs, int idx) throws SQLException
				{
					CargaPagosDto campos = new CargaPagosDto();
					campos.setIdBanco(rs.getInt("id_banco"));
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					return campos;
				}
			});	
				
		}
		catch(Exception e)
		{				
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:sacaHouseBank");
		}
		return listaResultado;
	}
	
	public List<CargaPagosDto> obtieneNombreEmpresa(int noEmpresa)
	{		
		try
		{
			sql = "";
			sql += "Select nom_empresa, no_cuenta_emp from empresa ";
			sql += "where no_empresa = " + noEmpresa + " ";
									
			listaResultado = jdbcTemplate.query(sql, new RowMapper()
			{
				public CargaPagosDto mapRow(ResultSet rs, int idx) throws SQLException
				{
					CargaPagosDto objCampos = new CargaPagosDto();
					objCampos.setNomEmpresa(rs.getString("nom_empresa"));
					objCampos.setNoCuentaEmpresa(rs.getInt("no_cuenta_emp"));
					return objCampos;
				}
				
			});		
			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:obtieneNombreEmpresa");
		}
		return listaResultado;
	}
	
	public List<CargaPagosDto> obtieneFormaPago(String idFormaPago, boolean esNumero)
	{		
		try
		{
			sql = "";
			
			sql += "Select * from cat_forma_pago ";			
			if (esNumero)				
				sql += "where id_forma_pago = " + Integer.parseInt(idFormaPago) + " ";
			else
				sql += "where equiv_forma_pago = '" + idFormaPago + "' ";
					
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper()
			{
				public CargaPagosDto mapRow(ResultSet rs, int idx) throws SQLException
				{
					CargaPagosDto objCampos = new CargaPagosDto();
					objCampos.setIdFormaPago(rs.getString("id_forma_pago"));
					objCampos.setDescFormaPago(rs.getString("desc_forma_pago"));
					return objCampos;
				}
			});		    
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:obtieneFormaPago");
		}return listaResultado;
	}
	
	public List<CargaPagosDto> obtieneBancoBenef(int bancoBenef)
	{
		try
		{
			sql = "";
			sql += "Select id_banco, desc_banco from cat_banco ";
			sql += " where id_banco = " + bancoBenef + "";
						
			listaResultado = jdbcTemplate.query(sql, new RowMapper()
			{
				public CargaPagosDto mapRow(ResultSet rs, int idx) throws SQLException
				{
					CargaPagosDto objCampos = new CargaPagosDto();
					objCampos.setIdBanco(rs.getInt("id_banco"));
					objCampos.setDescBanco(rs.getString("desc_banco"));
					return objCampos;
				}
			});		
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:obtieneBancoBenef");
		}return listaResultado;
	}
	
	public int obtieneMaximoNumeroBanco()
	{		
		try
		{
			sql = "";
			sql += "Select max(id_banco) ";
			sql += "from cat_banco";
						
			valorNumerico = jdbcTemplate.queryForInt(sql);			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C: CargaPagosDaoImpl, M:obtieneMaximoNumeroBanco");
		}return valorNumerico;
	}
		
	public int insertaBanco(CargaPagosDto objCargaPagosDto)
	{
		sqlInsert = new StringBuffer();
		sqlInsert.append("Insert into cat_banco (id_banco, desc_banco, fec_alta, usuario_alta, ");
		sqlInsert.append("b_cheque_elect, nac_ext, b_banca_elect, validado_aba, desc_banco_inbursa) ");
		sqlInsert.append("values ( ");
		sqlInsert.append(" " + objCargaPagosDto.getNuevoBanco() + ", '" + objCargaPagosDto.getDescBanco() + "', ");
		sqlInsert.append("'" + objCargaPagosDto.getFechaAlta() + "', " + objCargaPagosDto.getUsuarioAlta() + ", 'N', ");
		sqlInsert.append("'E', 'N', 'S', " + objCargaPagosDto.getIdBanco() + " )");		
		try
		{
			return jdbcTemplate.update(sqlInsert.toString());
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:insertaBanco");
			return 0;
		}		
	}	
	
	public List<CargaPagosDto> obtieneEquivaleDivisa(String origen, String divisaERP, String divisaSET)
	{
		try
		{
			sql = "";
			sql += "Select codDivisaSET, codDivisaERP from desc_refcr_divisas ";
			sql += "where codEmp = '" + origen + "' ";
			
			if (!divisaERP.equals(""))
				sql += "and codDivisaERP = '" + divisaERP + "'";
			else if(!divisaSET.equals(""))
				sql += "and codDivisaSET = '" + divisaSET + "'";				
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper()
			{
				public CargaPagosDto mapRow(ResultSet rs, int idx) throws SQLException
				{
					CargaPagosDto objCampos = new CargaPagosDto();
					objCampos.setCodDivisaSET(rs.getString("codDivisaSET"));
					objCampos.setCodDivisaERP(rs.getString("codDivisaERP"));
					return objCampos;					
				}
			});			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDto, M:obtieneEquivaleDivisa");
		}return listaResultado;
	}
	
	public List<CargaPagosDto> obtieneRubroCorrecto(String noBenef)
	{
		try
		{
			sql = "";
			sql += "Select coalesce(id_rubro, 0) as id_rubro ";
			sql += "from persona ";
			sql += "where equivale_persona = '" + noBenef + "' ";
			sql += "and id_tipo_persona = 'P'";
			listaResultado = jdbcTemplate.query(sql, new RowMapper()
			{
				public CargaPagosDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					CargaPagosDto objCampos = new CargaPagosDto();
					objCampos.setIdRubro(rs.getString("id_rubro"));
					return objCampos;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDto, M:obtieneRubroCorrecto");
		}return listaResultado;
	}
	
	public int obtieneNumeroCaja(int idCaja)
	{
		try
		{
			sql = "";
			sql += "Select id_caja cat_caja ";
			sql += "where id_caja = " + idCaja + " ";
			valorNumerico = jdbcTemplate.queryForInt(sql);
			return valorNumerico;
			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:obtieneNumeroCaja");			
		}return valorNumerico;
	}
	
	public List<CargaPagosDto> obtenerBancoYChequera(CargaPagosDto objCargaPagosDto)
	{			
		try
		{
			sql = "";
			if (objCargaPagosDto.getHouseBank().equals(""))
			{
				sql += "Select * from cat_ctas_pagadoras ";
				sql += "where housebank = '" + objCargaPagosDto.getHouseBank() + "'";
			}
			else
			{
				sql += "Select * from cat_ctas_pagadoras ";
				sql += "where no_empresa = " + objCargaPagosDto.getNoEmpresa() + " ";
				sql += "and origen = '" + objCargaPagosDto.getOrigen() + "' ";
				sql += "and id_bco_benef = " + objCargaPagosDto.getIdBancoBenef() + " ";
				sql += "and id_forma_pago = " + objCargaPagosDto.getIdFormaPago() + " ";
				sql += "and id_divisa = '" + objCargaPagosDto.getIdDivisa() + "'";
				
				if (objCargaPagosDto.getBancoPagador() != 0)
				{
					sql += "and id_bco_pag = " + objCargaPagosDto.getBancoPagador() + "";
				}				
			}					
						
			listaResultado = jdbcTemplate.query(sql, new RowMapper()
			{
				public CargaPagosDto mapRow(ResultSet rs, int idx) throws SQLException
				{
					CargaPagosDto objCampos = new CargaPagosDto();
					objCampos.setBancoPagador(rs.getInt("id_bco_pag"));
					objCampos.setChequeraPagadora(rs.getString("id_cheq_pag"));
					return objCampos;
				}				
			});		
		}
		catch(Exception e)
		{			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:obtenerBancoYChequera");
		}return listaResultado;
	}
		
	public List<CargaPagosDto> buscaRubroPersona(String noBenef)
	{
		try
		{
			sql = "";
			sql += "Select * from persona";
			sql += " where (equivale_persona like '%" + noBenef + "%' or no_persona like '%" + noBenef + "%')";
			sql += " and id_tipo_persona = 'P'";
						
			listaResultado = jdbcTemplate.query(sql, new RowMapper()
			{
				public CargaPagosDto mapRow(ResultSet rs, int idx) throws SQLException
				{
					CargaPagosDto objCampos = new CargaPagosDto();
					objCampos.setIdRubro(Integer.toString(rs.getInt("id_rubro")));
					return objCampos;
				}				
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M: buscaPersona");
		}return listaResultado;
	}
	
	public List<CargaPagosDto> existePersonaZinterprov(int noEmpresa, String noBenef)
	{
		try
		{
			sql = "";
			sql += "Select * from zinterprov ";
			sql += "where no_empresa = " + noEmpresa + " ";
			sql += "and equiv_per like '%" + noBenef + "%' ";
			sql += "and tipo_pers = 'P'";
						
			listaResultado = jdbcTemplate.query(sql, new RowMapper()
			{
				public CargaPagosDto mapRow(ResultSet rs, int idx) throws SQLException
				{
					CargaPagosDto objCampos = new CargaPagosDto();
					objCampos.setNoEmpresa(rs.getInt("no_empresa"));
					return objCampos;
				}
			});			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " +  e.toString() + "P:Interfaz, C:CargaPagosDaoImpl, M:existePersona");
		}return listaResultado;
	}
		
	public int limpiaZinterprov(int noEmpresa, String noBenef)
	{		
		try
		{
			sql = "";
			sql += "Delete from zinterprov ";
			sql += "where no_empresa = " + noEmpresa + " ";
			sql += "and equiv_per like '%" + noBenef + "%' ";
			sql += "and tipo_pers = 'P'";
		
			return jdbcTemplate.update(sql);			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:limpiaZinterprov");
			return 0;
		}
	}
	
	public int insertaDatosPersona(List<Map<String, String>> objCamposGrid, int posicion, int idRubro)
	{
		try
		{
			sql = "";
			sql += "Insert into zinterprov (no_empresa, tipo_pers, no_persona, tipo_prov,nombre, paterno, materno, ";
			sql += "razon_soc, rfc, estat_pers, fis_moral, estatus, equiv_per, id_rubro, ciudad, curp, calle_no, id_pais, ";
			sql += "id_estado, id_cp, email, contacto_principal) ";
			//Se coloca con la empresa fija
			//sql += "values (" + objCamposGrid.get(posicion).get("noEmpresa") + ", 'P', "+ 0 +", 'P', ";
			sql += "values (" + 552 + ", 'P', "+ 0 +", 'P', ";
			sql += "'" + objCamposGrid.get(posicion).get("nombre") + "', '" + objCamposGrid.get(posicion).get("apellidoPaterno") + "', ";
			sql += "'" + objCamposGrid.get(posicion).get("apellidoMaterno") + "', '" + objCamposGrid.get(posicion).get("razonSocial") + "', ";
			sql += "'" + objCamposGrid.get(posicion).get("rfc") + "', 'A', '" + objCamposGrid.get(posicion).get("tipoPersona") + "', ";
			sql += "'P', '" + objCamposGrid.get(posicion).get("noBenef") + "', " + idRubro + ", '" + objCamposGrid.get(posicion).get("cdBenef") + "', ";
			sql += "'" + objCamposGrid.get(posicion).get("curp") + "', '" + objCamposGrid.get(posicion).get("direccionBenef") + "', ";
			sql += "'" + objCamposGrid.get(posicion).get("paisBenef") + "', '" + objCamposGrid.get(posicion).get("regionBenef") + "', ";
			sql += "'" + objCamposGrid.get(posicion).get("cpBenef") + "', '" + objCamposGrid.get(posicion).get("mail") + "',''";			
			sql += ")";
			
			return jdbcTemplate.update(sql);	
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:insertaDatosPersona");
			return 0;
		}
	}
	
	public int actualizaDatosPersona(List<Map<String, String>> objCamposGrid, int posicion, int idRubro)
	{
		try
		{
			sql = "";
			sql += "Update zinterprov Set estatus = 'M', causa_rech = '', ciudad = '" + objCamposGrid.get(posicion).get("cdBenef") + "', ";
			sql += "nombre = '" + objCamposGrid.get(posicion).get("nombre") + "', ";
			sql += "paterno = '" + objCamposGrid.get(posicion).get("apellidoPaterno") + "', ";
			sql += "materno = '" + objCamposGrid.get(posicion).get("apellidoMaterno") + "', ";
			sql += "razon_soc = '" + objCamposGrid.get(posicion).get("razonSocial") + "', ";
			sql += "rfc = '" + objCamposGrid.get(posicion).get("rfc") + "', ";
			sql += "curp = '" + objCamposGrid.get(posicion).get("curp") + "', ";
			sql += "calle_no = '" + objCamposGrid.get(posicion).get("direccionBenef") + "', ";
			sql += "id_pais = '" + objCamposGrid.get(posicion).get("paisBenef") + "', ";
			sql += "id_estado = '" + objCamposGrid.get(posicion).get("regionBenef") + "', ";
			sql += "id_cp = '" + objCamposGrid.get(posicion).get("cpBenef") + "', ";
			sql += "fis_moral = '" + objCamposGrid.get(posicion).get("tipoPersona") + "', ";
			sql += "email = '" + objCamposGrid.get(posicion).get("mail") + "', ";
			sql += "id_rubro = " + idRubro + " ";
			//Igual se coloca la empresa fija
			//sql += "where no_empresa = " + objCamposGrid.get(posicion).get("noEmpresa") + " ";
			sql += "where no_empresa = " + 552 + " ";
			sql += "and equiv_per = '" + objCamposGrid.get(posicion).get("noBenef") + "' ";
			sql += "and tipo_pers = 'P'";
			
			return jdbcTemplate.update(sql);			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:actualizaDatosPersona");
			return 0;
		}
	}
	
	public int borraFormaPago(String noBenef, int noEmpresa, String mandante)
	{
		try
		{
			sql = "";
			sql += "Delete from zformapago ";
			sql += "where mandante = '" + mandante + "' ";
			sql += "and no_empresa = " + noEmpresa + " ";
			sql += "and no_persona = '" + noBenef + "'";
				
			return jdbcTemplate.update(sql);				
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:borraFormaPago");
			return 0;
		}
	}
	
	public int insertaFormaPago(String noBenef, int noEmpresa, String mandante, int formaPago)
	{
		try
		{
			sql = "";
			sql += "Insert into zformapago ( ";
			sql += "no_empresa, no_persona, tipo_pers, forma_pago, mandante) ";
			sql += "values (";
			sql += "" + noEmpresa + ", '" + noBenef + "', 'P', " + formaPago + ", '" + mandante + "')";
			
			return jdbcTemplate.update(sql);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:insertaFormaPago");
			return 0;
		}		
	}
	
	public boolean buscaChequeraCtasBanco(List<Map<String, String>> objCamposGrid, int posicion)
	{
		int resultado = 0;
		
		try
		{
			sql = "";
			sql += "Select count(*) from ctas_banco cb, persona p ";
			sql += "where cb.no_persona = p.no_persona ";
			sql += "and p.equivale_persona = '" + objCamposGrid.get(posicion).get("noBenef") + "' ";
			sql += "and cb.id_chequera = '" + objCamposGrid.get(posicion).get("chequeraBenef") + "' ";
			sql += "and cb.id_banco = " + Integer.parseInt(objCamposGrid.get(posicion).get("idBancoBenef")) + " ";
			sql += "and cb.id_tipo_persona = 'P' ";
			
			resultado = jdbcTemplate.queryForInt(sql);
			
			if (resultado > 0)
				return true;
			else
				return false;
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:buscaChequeraCtasBanco");
			return false;
		}
	}
		
	public int buscaChequeraZimpcheqprov(List<Map<String, String>> objCamposGrid, int posicion)
	{
		int respuesta = 0;
		
		try
		{
			sql = "";
			sql += "Select count(*) from zimpcheqprov ";
			sql += "where no_empresa = " + Integer.parseInt(objCamposGrid.get(posicion).get("noEmpresa")) + " ";
			sql += "and equiv_per = '" + objCamposGrid.get(posicion).get("noBenef") + "' ";
			sql += "and id_banco = " + Integer.parseInt(objCamposGrid.get(posicion).get("idBancoBenef")) + " ";
			sql += "and id_chequera = '" + objCamposGrid.get(posicion).get("chequeraBenef") + "' ";
			
			respuesta = jdbcTemplate.queryForInt(sql);
			
			return respuesta;
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CarpaPagosDaoImpl, M:buscaChequeraZintercheqprov");
			return 0;
		}
	}
	
	public int actualizaZimpcheqprov(List<Map<String, String>> objGrid, int posicion, String psEstatus)
	{
		try
		{
			sql = "";
			sql += "Update zimpcheqprov Set estatus = '" + psEstatus + "', causa_rech = '" + objGrid.get(posicion).get("conceptoRechazo") + "', ";
			sql += "plaza = 0, sucursal = " + objGrid.get(posicion).get("sucursalBenef") + ", ";
			
			if (!objGrid.get(posicion).get("clabe").equals(""))
			{
				sql += "id_clabe = '" + objGrid.get(posicion).get("clabe") + "', ";
			}
			
			sql += "id_pais = '" + objGrid.get(posicion).get("paisBancoBenef") + "', ";
			sql += "id_region = '" + objGrid.get(posicion).get("cdBancoBenef") + "', ";
			sql += "swift_code = '" + objGrid.get(posicion).get("swiftBenef") + "', ";
			sql += "iban = '" + objGrid.get(posicion).get("iban") + "', ";
			sql += "forfurthercred = '" + objGrid.get(posicion).get("forFurtherCredit") + "', ";
			sql += "banco_corresponsal = '" + objGrid.get(posicion).get("nombreBancoIntermediario") + "', ";
			sql += "id_pais_corresponsal = '" + objGrid.get(posicion).get("paisBancoIntemediario") + "', ";
			sql += "id_region_corresponsal = '" + objGrid.get(posicion).get("cdBancoIntermediario") + "', ";
			sql += "aba = '" + objGrid.get(posicion).get("aba") + "', ";
			sql += "id_divisa = '" + objGrid.get(posicion).get("idDivisa") + "' ";
			sql += "where no_empresa = " + Integer.parseInt(objGrid.get(posicion).get("noEmpresa")) + " ";
			sql += "and equiv_per = '" + objGrid.get(posicion).get("noBenef") + "' ";
			sql += "and id_banco = " + objGrid.get(posicion).get("idBancoBenef") + " ";
			sql += "and id_chequera = '" + objGrid.get(posicion).get("chequeraBenef") + "' ";
			
			return jdbcTemplate.update(sql);
			
		}
		catch(Exception e)
		{			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:actualizaZimpcheqprov");
			return 0;
		}
	}
	
	public int insertaZimpcheqprov(List<Map<String, String>> objGrid, int posicion, String psEstatus)
	{
		try
		{
			sql = "";
			sql += "Insert into zimpcheqprov ( ";
			sql += "no_empresa, equiv_per, id_tipo_persona, id_divisa, id_banco, id_chequera, ";
			sql += "plaza, sucursal, id_clabe, estatus, causa_rech, fec_imp, id_pais, id_region, ";
			sql += "swift_code, iban, forfurthercred, banco_corresponsal, id_pais_corresponsal, ";
			sql += "id_region_corresponsal, aba) ";
			sql += "values (";
			sql += "" + 552 + ", ";
			sql += "'" + objGrid.get(posicion).get("noBenef") + "', ";
			sql += "'P', '" + objGrid.get(posicion).get("idDivisa") + "', ";
			sql += "" + Integer.parseInt(objGrid.get(posicion).get("idBancoBenef")) + ", ";
			sql += "'" + objGrid.get(posicion).get("chequeraBenef") + "', ";
			sql += "0, '" + objGrid.get(posicion).get("sucursalBenef") + "', ";
			sql += "'" + objGrid.get(posicion).get("clabe") + "', '" + psEstatus + "', ";
			sql += "'" + objGrid.get(posicion).get("conceptoRechazo") + "', ";			
			sql += "'" + objGrid.get(posicion).get("fechaAlta").substring(8, 10) + "/" + objGrid.get(posicion).get("fechaAlta").substring(5, 7) + "/" + objGrid.get(posicion).get("fechaAlta").substring(0, 4) + "', ";			
			sql += "'" + objGrid.get(posicion).get("paisBancoBenef") + "', ";
			sql += "'" + objGrid.get(posicion).get("cdBancoBenef") + "', ";
			sql += "'" + objGrid.get(posicion).get("swiftBenef") + "', ";
			sql += "'" + objGrid.get(posicion).get("iban") + "', ";
			sql += "'" + objGrid.get(posicion).get("forFurtherCredit") + "', ";
			sql += "'" + objGrid.get(posicion).get("nombreBancoIntermediario") + "', ";
			sql += "'" + objGrid.get(posicion).get("paisBancoIntermediario") + "', ";
			sql += "'" + objGrid.get(posicion).get("cdBancoIntermediario") + "', ";
			sql += "'" + objGrid.get(posicion).get("aba") + "') ";
			
			return jdbcTemplate.update(sql);
			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:insertaZimpcheqprov");
			return 0;
		}
	}
	
	public int buscaRegistroZimpFact(List<Map<String, String>> objGrid, int posicion)
	{		
		int resultado = 0;
		try
		{
			sql = "";
			sql += "Select * from zimp_fact ";
			sql += "where no_empresa = " + Integer.parseInt(objGrid.get(posicion).get("noEmpresa")) + " ";
			sql += "and no_doc_sap = '" + objGrid.get(posicion).get("noDocto") + "' ";
			sql += "and secuencia = '" + objGrid.get(posicion).get("secuencia") + "' ";
			sql += "and fec_fact = '" + objGrid.get(posicion).get("fecValor") + "' ";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper()
			{
				public CargaPagosDto mapRow(ResultSet rs, int idx) throws SQLException
				{
					CargaPagosDto objCampos = new CargaPagosDto();
					objCampos.setEstatusPropuesta(rs.getString("estatus"));					
					return objCampos;
				}
			});
			
			for (int i=0; i < listaResultado.size(); i++)
			{
				if (listaResultado.get(i).getEstatusPropuesta().equals("I"))
					resultado = 1;				
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:buscaRegistroZimpFact");
			return 0;			
		}return resultado;	
	}
	
	public int eliminaRegistroZimpFact(List<Map<String, String>> objGrid, int posicion)
	{
		try
		{
			sql = "";
			sql += "Delete zimp_fact ";
			sql += "where no_empresa = " + Integer.parseInt(objGrid.get(posicion).get("noEmpresa")) + " ";
			sql += "and no_doc_sap = '" + objGrid.get(posicion).get("noDocto") + "' ";
			sql += "and secuencia = '" + objGrid.get(posicion).get("secuencia") + "' ";
			sql += "and fec_fact = '" + objGrid.get(posicion).get("fecValor") + "' ";
			
			return jdbcTemplate.update(sql);				
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:eliminaRegistroZimpFact");
			return 0;
		}
	}
	
	public int actualizaRegistroZimpFact(List<Map<String, String>> objGrid, int posicion, String concepto, String referenciaCie)
	{
		try
		{
			sql = "";
			sql += "Update zimp_fact Set no_benef = '" + objGrid.get(posicion).get("noBenef") + "', ";
			sql += "id_divisa = '" + objGrid.get(posicion).get("idDivisa") + "', ";
			sql += "concepto = '" + concepto + "', forma_pago = " + Integer.parseInt(objGrid.get(posicion).get("idFormaPago")) + ", ";
			sql += "origen = '" + objGrid.get(posicion).get("origen") + "', ";
			sql += "id_rubro = '" + objGrid.get(posicion).get("idRubro") + "', ";
			sql += "rfc = '" + objGrid.get(posicion).get("rfc") + "', ";
			sql += "id_caja = " + Integer.parseInt(objGrid.get(posicion).get("idCaja")) + ", ";
			sql += "clabe = '" + objGrid.get(posicion).get("clabe") + "', ";
			sql += "desc_propuesta = '" + objGrid.get(posicion).get("descPropuesta") + "', ";
			sql += "clase_docto = '" + objGrid.get(posicion).get("tipoDocumento") + "', ";
			sql += "chequera_benef = '" + objGrid.get(posicion).get("chequeraBenef") + "', ";
			sql += "banco_benef = " + Integer.parseInt(objGrid.get(posicion).get("idBancoBenef")) + ", ";
			sql += "estatus = 'P', banco_pagador = " + Integer.parseInt(objGrid.get(posicion).get("bancoPagador")) + ", ";
			sql += "chequera_pagadora = '" + objGrid.get(posicion).get("chequeraPagadora") + "', ";
						
			if (objGrid.get(posicion).get("origen").equals("SAP"))
			{
				sql += "importe_original = importe_original + " + Double.parseDouble(objGrid.get(posicion).get("importe")) + ", ";
				sql += "importe = importe + " + Double.parseDouble(objGrid.get(posicion).get("importe")) + ", ";
			}
			else
			{
				sql += "importe_original = " + Double.parseDouble(objGrid.get(posicion).get("importe")) + ", ";
				sql += "importe = " + Double.parseDouble(objGrid.get(posicion).get("importe")) + ", ";
				sql += "imp_solic = " + Double.parseDouble(objGrid.get(posicion).get("importe")) + ", ";
			}
			
			sql += "fec_fact = '" + objGrid.get(posicion).get("fecValor") + "', ";
			sql += "contra_rec = 0, fec_propuesta = '" + objGrid.get(posicion).get("fecValor") + "', ";
			sql += "cod_bloq = 'N', causa_rech = '" + objGrid.get(posicion).get("conceptoRechazo") + "', ";
			sql += "fecha_imp = '" + objGrid.get(posicion).get("fechaAlta") + "', ";
			sql += "centro_cto = '" + objGrid.get(posicion).get("centroCostos") + "', ";
			sql += "no_cheque = " + Integer.parseInt(objGrid.get(posicion).get("noCheque")) + ", ";
			sql += "no_factura = '" + referenciaCie + "' ";
			sql += "where no_empresa = " + Integer.parseInt(objGrid.get(posicion).get("noEmpresa")) + " ";
			sql += "and no_doc_sap = '" + objGrid.get(posicion).get("noDocto") + "', ";
			sql += "and secuencia = '" + objGrid.get(posicion).get("secuencia") + "', ";
			sql += "and fec_valor = '" + objGrid.get(posicion).get("fecValor") + "' ";
			
			return jdbcTemplate.update(sql);				
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:actualizaRegistroZimpFact");
			return 0;
		}		
	}
		
	public int insertaRegistroZimpFact(List<Map<String, String>> objGrid, int posicion, String concepto, String referencia, String estatus)
	{
		String var = "";
		try
		{
			var = objGrid.get(posicion).get("centroCostos").equals("")? "0" : objGrid.get(posicion).get("centroCostos");
					
			sql = "";
			sql += "Insert into zimp_fact (";
			sql += "no_empresa, no_doc_sap, secuencia, fec_valor, no_benef, importe, ";
			sql += "id_divisa, concepto, forma_pago, origen, gpo_tesor, id_rubro, rfc, id_caja, ";
			sql += "clabe, desc_propuesta, clase_docto, chequera_benef, banco_benef, ";
			sql += "estatus, banco_pagador, chequera_pagadora, importe_original, ";
			sql += "imp_solic, fec_fact, fec_propuesta, ";
			sql += "cod_bloq, causa_rech, fecha_imp, centro_cto, no_cheque, no_factura) ";
			sql += "values (";
			sql += "" + Integer.parseInt(objGrid.get(posicion).get("noEmpresa")) + ", ";
			sql += "'" + objGrid.get(posicion).get("noDocto") + "', ";
			sql += "'" + objGrid.get(posicion).get("secuencia") + "', ";
			sql += "'" + objGrid.get(posicion).get("fecValor") + "', ";
			sql += "'" + objGrid.get(posicion).get("noBenef") + "', ";
			sql += "" + Double.parseDouble(objGrid.get(posicion).get("importe")) + ", ";
			sql += "'" + objGrid.get(posicion).get("idDivisa") + "', ";
			sql += "'" + concepto + "', ";
			sql += "" + Integer.parseInt(objGrid.get(posicion).get("idFormaPago")) + ", ";
			sql += "'" + objGrid.get(posicion).get("origen") + "', ";
			sql += "" + Integer.parseInt(objGrid.get(posicion).get("idGrupo")) + ", ";
			sql += "" + Integer.parseInt(objGrid.get(posicion).get("idRubro")) + ", ";
			sql += "'" + objGrid.get(posicion).get("rfc") + "', ";
			sql += "" + Integer.parseInt(objGrid.get(posicion).get("idCaja")) + ", ";
			sql += "'" + objGrid.get(posicion).get("clabe") + "', ";
			sql += "'" + objGrid.get(posicion).get("descPropuesta") + "', ";			
			sql += "'" + objGrid.get(posicion).get("tipoDocumento") + "', ";
			sql += "'" + objGrid.get(posicion).get("chequeraBenef") + "', ";
			sql += "" + Integer.parseInt(objGrid.get(posicion).get("idBancoBenef")) + ", ";
			sql += "'" + estatus + "', " + Integer.parseInt(objGrid.get(posicion).get("bancoPagador")) + ", ";
			sql += "'" + objGrid.get(posicion).get("chequeraPagadora") + "', ";
			sql += "" + Double.parseDouble(objGrid.get(posicion).get("importe")) + ", ";			
			sql += "" + Double.parseDouble(objGrid.get(posicion).get("importe")) + ", ";
			sql += "'" + objGrid.get(posicion).get("fecValor") + "', ";
			sql += "'" + objGrid.get(posicion).get("fecValor") + "', 'N', ";
			sql += "'" + objGrid.get(posicion).get("conceptoRechazo") + "', ";
			sql += "'" + objGrid.get(posicion).get("fechaAlta").substring(8, 10) + "/" + objGrid.get(posicion).get("fechaAlta").substring(5, 7) + "/" + objGrid.get(posicion).get("fechaAlta").substring(0, 4) + "', ";
			//sql += "'" + objGrid.get(posicion).get("fechaAlta") + "', ";
			sql += "" + Integer.parseInt(var) + ", ";
			sql += "" + Integer.parseInt(objGrid.get(posicion).get("noCheque")) + ", ";
			sql += "'" + referencia + "')";
			
			return jdbcTemplate.update(sql);
				
		}
		catch(Exception e)
		{			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:insertaRegistroZimpFact");
			return 0;
		}
	}
	
	public List<CargaPagosDto> buscaCheqPag() {
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT no_empresa, id_bco_pag, id_cheq_pag, id_grupo \n");
			sql.append(" FROM cat_ctas_pagadoras \n");
			sql.append(" WHERE no_empresa in (select no_empresa from ZIMP_FACT \n");
			sql.append("		where DESC_PROPUESTA is null and BANCO_PAGADOR is null and CHEQUERA_PAGADORA is null and FEC_PROPUESTA is null)");
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper() {
				public CargaPagosDto mapRow(ResultSet rs, int idx) throws SQLException {
					CargaPagosDto objCampos = new CargaPagosDto();
					objCampos.setNoEmpresa(rs.getInt("no_empresa"));
					objCampos.setBancoPagador(rs.getInt("id_bco_pag"));
					objCampos.setChequeraPagadora(rs.getString("id_cheq_pag"));
					objCampos.setIdRubro(rs.getString("id_grupo"));
					return objCampos;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:buscaCheqPag");
		}
		return listaResultado;
	}
	
	public int actualizaDatosBcoCheq(List<CargaPagosDto> result, int i) {
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append("UPDATE zimp_fact SET ");
			sql.append("	desc_propuesta = no_empresa,");
			sql.append("	banco_pagador = "+ result.get(i).getBancoPagador() +", \n");
			sql.append("	chequera_pagadora = '"+ result.get(i).getChequeraPagadora() +"', \n");
			sql.append("	fec_propuesta = fec_valor,");
			sql.append("	id_rubro = "+ result.get(i).getIdRubro() +" \n");
			sql.append("WHERE DESC_PROPUESTA is null \n");
			sql.append("	and BANCO_PAGADOR is null \n");
			sql.append("	and CHEQUERA_PAGADORA is null \n");
			sql.append("	and FEC_PROPUESTA is null \n");
			sql.append("	and no_empresa = "+ result.get(i).getNoEmpresa() +"");
			
			return jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:actualizaDatosBcoCheq");
			return 0;
		}
	}
	
	public Map importaProveedor(int usuario, int noEmpresa)
	{
		objConsultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return objConsultasGenerales.importaProveedor(usuario, noEmpresa);
	}
	
	public Map importaMovimientos(int usuario, String noEmpresa, int valor)
	{
		logger.debug("Entra importaMovimientos");
		objConsultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Map result = objConsultasGenerales.importaMovimientos(usuario, noEmpresa, valor);
		logger.debug("Sale importaMovimientos");
		return result;
	}
	
	public List<CargaPagosDto> buscaCtasBanco(String noBenef)
	{
		try
		{
			sql = "";
			sql += "Select coalesce(estatus_alta, '') as estatus_alta, * ";
			sql += "\n from ctas_banco ";
			sql += "\n where no_persona in (select no_persona from persona ";
			sql += "\n where equivale_persona like '%" + noBenef + "%' and id_tipo_persona = 'P') ";
			sql += "order by fec_modif";
						
			listaResultado = jdbcTemplate.query(sql, new RowMapper()
			{
				public CargaPagosDto mapRow(ResultSet rs, int idx) throws SQLException
				{
					CargaPagosDto objCampos = new CargaPagosDto();
					objCampos.setEstatusAlta(rs.getString("estatus_alta"));
					return objCampos;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:buscaCtasBanco");
		}
		return listaResultado;
	}
	
	public List<CargaPagosDto> sacaFechaModifMax(String noBenef)
	{	
		try
		{
			sql = "";
			sql += "Select max(fec_modif) as fec_modif ";
			sql += "\n from ctas_banco ";
			sql += "\n where no_persona in (select no_persona from persona ";
			sql += "\n where equivale_persona like '%" + noBenef + "%' and id_tipo_persona = 'P') ";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper()
			{
				public CargaPagosDto mapRow(ResultSet rs, int idx) throws SQLException
				{
					CargaPagosDto objCampos = new CargaPagosDto();
					objCampos.setFechaAlta(rs.getString("fec_modif"));
					return objCampos;
				}
			});			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:sacaFechaModifMax");
		}		
		return listaResultado;
	}
	
	
	public int actualizaEstatusAlta(String noBenef, String estatus, String fechaModif)
	{		
		try
		{			
			sql = "";
			if (fechaModif.equals(""))
			{				
				sql += "Update ctas_banco set estatus_alta = null ";
				sql += "where no_persona in (select no_persona from persona ";
				sql += "where equivale_persona like '%" + noBenef + "%' and id_tipo_persona = 'P') ";
			}
			else
			{											
				sql += "Update ctas_banco set estatus_alta = '" + estatus + "' ";
				sql += "where no_persona in (select no_persona from persona ";
				sql += "where equivale_persona like '%" + noBenef + "%' and id_tipo_persona = 'P') ";
				sql += "and fec_modif = '" + fechaModif.substring(8, 10) + "-" + fechaModif.substring(5, 7) + "-" + fechaModif.substring(0, 4) + "' ";

			}
			
			return jdbcTemplate.update(sql);
		}
		catch(Exception e)
		{			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:actualizaEstatusAlta");
			return 0;
		}
	}
	
	public int seleccionaFolio(String tipoFolio)
	{
		objConsultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return objConsultasGenerales.seleccionarFolioReal(tipoFolio);
	}
	
	public int actualizaFolio(String tipoFolio)
	{
		try
		{
			sql = "";
			sql += "Update folio set num_folio = num_folio + 1 ";
			sql += "where tipo_folio = '" + tipoFolio + "'";
			System.out.println(sql);
			return jdbcTemplate.update(sql);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:actualizaFolio");
			return 0;
		}
	}
	
	public List<CargaPagosDto> selectImportaCP(int iTipoPago, String fecHoy, int usuarioAlta, String noEmpresaParam, String estatus, String fecIni, String fecFin) {
		StringBuffer sql = new StringBuffer();
		
		try {
			if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE")) {
	            sql.append(" SELECT i.fec_fact,i.secuencia, i.no_doc_sap, i.no_empresa, i.no_factura, i.fec_fact, i.fec_valor, \n");
	            sql.append(" 	i.no_benef, p.razon_social, p.nombre ,p.paterno ,p.materno, i.imp_solic , i.importe, i.id_divisa, \n");
	            sql.append(" 	i.tipo_camb, i.concepto, forma_pago, i.cve_leyen, i.benef_alt, i.id_bco_alt, i.id_chq_alt, \n");
	            sql.append(" 	i.id_rubro, i.centro_cto, i.gpo_tesor, i.cod_bloq, i.ind_may_es, i.estatus, i.causa_rech, i.fecha_imp \n");
	            sql.append(" FROM zimp_fact i left join persona p on i.no_benef = p.equivale_persona left join SET006 s on i.no_empresa = s.SOIEMP \n");
	            sql.append(" WHERE ");
	            
	            if(noEmpresaParam.equals("0")) {
	                sql.append(" s.SETEMP in ");
	                sql.append("     (select no_empresa");
	                sql.append("     from usuario_empresa ");
	                sql.append("     where no_usuario = " + usuarioAlta + ")");
	            }else
	            	sql.append(" (s.SETEMP = " + noEmpresaParam + " or I.NO_EMPRESA = " + noEmpresaParam + ")");
	            //sql.append(" and i.no_empresa = s.SOIEMP \n");
	            sql.append(" and s.SISCOD = 'CP' \n");
	           // sql.append(" and i.no_benef = p.equivale_persona \n");
	            sql.append(" and id_tipo_persona= (case when i.forma_pago = 6 then 'E' else 'P' end) \n");
	            
	            if(!estatus.equals("")) {
	            	if(estatus.equals("P")) {
	                    sql.append(" and ( ");
	                    sql.append(" (i.estatus IN ('P') and i.cod_bloq IN('N','S')) ");
	                    sql.append(" OR (i.estatus = 'U' and i.cod_bloq IN('H','D') ");
	                    sql.append(" OR i.estatus = 'M')");
	                    sql.append("     )");
	                }else if(estatus.equals("C"))
	                	sql.append(" and i.estatus in ('C','A') \n");
	                else
	                	sql.append(" and i.estatus = '" + estatus + "' \n");
	            }
	            sql.append("and FECHA_IMP >= TO_DATE('" + fecIni + "', 'DD/MM/YYYY') AND FECHA_IMP < TO_DATE('" + fecFin + "', 'DD/MM/YYYY') + 1");
	           
	            //sql.append(" and i.desc_propuesta is NULL \n");
	            //sql.append(" and i.fec_propuesta is NULL \n");
	            /*
	            if(pvsFec1 <> "")
	            	sql.append(" and i.fec_valor >= '" + pvsFec1 + "' \n");
	            
	            if(pvsFec2 <> "")
	            	sql.append(" and i.fec_valor <= '" + pvsFec2 + "' \n");
	            */
			}else if(gsDBM.equals("POSTGRESQL")) {
	            sql.append(" SELECT i.secuencia, i.no_doc_sap, i.no_empresa, i.no_factura, i.fec_fact, i.fec_valor, \n");
	            sql.append(" i.no_benef, p.razon_social, p.nombre ,p.paterno ,p.materno,  i.imp_solic , i.importe, i.id_divisa, \n");
	            sql.append(" i.tipo_camb, i.concepto, forma_pago, i.cve_leyen, i.benef_alt, i.id_bco_alt, i.id_chq_alt, \n");
	            sql.append(" i.id_rubro, i.centro_cto, i.gpo_tesor, i.cod_bloq, i.ind_may_es, i.estatus, i.causa_rech, i.fecha_imp \n");
	            sql.append(" FROM zimp_fact i left join persona p on i.no_benef = p.equivale_persona left join SET006 s on i.no_empresa = s.SOIEMP \n");
	            sql.append(" WHERE ");
	            
	            if(noEmpresaParam.equals("0")) {
	                sql.append(" s.SETEMP in ");
	                sql.append("     (select no_empresa ");
	                sql.append("     from usuario_empresa ");
	                sql.append("     where no_usuario = " + usuarioAlta + ") ");
	            }else
	                sql.append(" (s.SETEMP = " + noEmpresaParam + " or I.NO_EMPRESA = " + noEmpresaParam + ")");
	            
	            //sql.append(" and i.no_empresa = s.SOIEMP \n");
	            sql.append(" and s.SISCOD = 'CP' \n");
	            //sql.append(" and i.no_benef = p.equivale_persona \n");
	            sql.append(" and id_tipo_persona= (case when i.forma_pago = 6 then 'E' else 'P' end) \n");
	        
	            if(!estatus.equals("")) {
	            	if(estatus.equals("P")) {
	                    sql.append(" and ( ");
	                    sql.append(" (i.estatus IN ('P') and i.cod_bloq IN('N','S')) ");
	                    sql.append(" OR (i.estatus = 'U' and i.cod_bloq IN('H','D') ");
	                    sql.append(" OR i.estatus = 'M')");
	                    sql.append("     )");
	                }else
	                	sql.append(" and i.estatus = '" + estatus + "' \n");
	            }
	            /*
	            if(pvsFec1 <> "")
	                sql.append(" and date(i.fec_valor) >= '" & pvsFec1 & "' \n");
	            
	            if(pvsFec2 <> "")
	                sql.append(" and date(i.fec_valor) <= '" & pvsFec2 & "' \n");
	            */
			}
			System.out.println("Query zimp_fact: " + sql);
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper() {
				public CargaPagosDto mapRow(ResultSet rs, int idx) throws SQLException {
					CargaPagosDto objCampos = new CargaPagosDto();
					objCampos.setSecuencia(rs.getString("secuencia"));
					objCampos.setNoDocto(rs.getString("no_doc_sap"));
					objCampos.setNoEmpresa(rs.getInt("no_empresa"));
					objCampos.setNoFactura(rs.getString("no_factura"));
					objCampos.setFecFact(rs.getString("fec_fact"));
					objCampos.setFecValor(rs.getString("fec_valor"));
					objCampos.setNoBenef(rs.getString("no_benef"));
					objCampos.setRazonSocial(rs.getString("razon_social"));
					objCampos.setNombre(rs.getString("nombre"));
					objCampos.setApellidoPaterno(rs.getString("paterno"));
					objCampos.setApellidoMaterno(rs.getString("materno"));
					objCampos.setImpSolic(rs.getDouble("imp_solic"));
					objCampos.setImporte(rs.getDouble("importe"));
					objCampos.setIdDivisa(rs.getString("id_divisa"));
					objCampos.setTipoCamb(rs.getDouble("tipo_camb"));
					objCampos.setConcepto(rs.getString("concepto"));
					objCampos.setIdFormaPago(rs.getString("forma_pago"));
					objCampos.setCveLeyen(rs.getString("cve_leyen"));
					objCampos.setBenefAlt(rs.getString("benef_alt"));
					objCampos.setIdBcoAlt(rs.getInt("id_bco_alt"));
					objCampos.setIdChqAlt(rs.getString("id_chq_alt"));
					objCampos.setIdRubro(rs.getString("id_rubro"));
					objCampos.setCentroCostos(rs.getString("centro_cto"));
					objCampos.setGpoTesor(rs.getString("gpo_tesor"));
					objCampos.setCodBloq(rs.getString("cod_bloq"));
					objCampos.setIndMayEs(rs.getString("ind_may_es"));
					objCampos.setEstatusPropuesta(rs.getString("estatus"));
					objCampos.setConceptoRechazo(rs.getString("causa_rech"));
					objCampos.setFechaImp(rs.getString("fecha_imp"));
					return objCampos;
				}
			});
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:selectImportaCP");
		}
		return listaResultado;
	}
	
	public int actDatosPropuesta(List<Map<String, String>> gridDatos, int i) {
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" UPDATE zimp_fact SET ");
			sql.append("	desc_propuesta = '"+ gridDatos.get(i).get("descPropuesta") +"',");
			sql.append("	fec_propuesta = '"+ gridDatos.get(i).get("fecPropuesta").toString() +"',");
			sql.append("	id_rubro = 6000 \n");
			sql.append(" WHERE desc_propuesta is null \n");
			sql.append("	and fec_propuesta is null \n");
			sql.append("	and estatus = 'P'");
			
			sql.append("	and no_empresa = "+ Integer.parseInt(gridDatos.get(i).get("noEmpresa")) +"");
			sql.append("	and no_factura = '"+ gridDatos.get(i).get("noDocto") +"'");
			sql.append("	and fec_valor = '"+ gridDatos.get(i).get("fecValor") +"'");
			sql.append("	and no_benef = '"+ gridDatos.get(i).get("noBenef") +"'");
			sql.append("	and importe = "+ Double.parseDouble(gridDatos.get(i).get("importe")) +"");
			sql.append("	and id_divisa = '"+ gridDatos.get(i).get("idDivisa") +"'");
			
			return jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:actDatosPropuesta");
			return 0;
		}
	}
	
	public int actEstatusPropuesta() {
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" UPDATE zimp_fact SET estatus = 'I' \n");
			sql.append(" WHERE not desc_propuesta is null \n");
			sql.append("	and not fec_propuesta is null \n");
			sql.append("	and not banco_pagador is null \n");
			sql.append("	and not chequera_pagadora is null \n");
			sql.append("	and estatus = 'P'");
			
			return jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:actEstatusPropuesta");
			return 0;
		}
	}
	
	public int buscaDatosBenef(List<Map<String, String>> gridDatos, int i) {
		StringBuffer sql = new StringBuffer();
		int res = 0;
		String noEmpresa;
		
		try {
			noEmpresa = gridDatos.get(i).get("noEmpresa").length() == 1 ? "0" + gridDatos.get(i).get("noEmpresa") : gridDatos.get(i).get("noEmpresa");
			
			sql.append(" SELECT count(*) FROM ctas_banco cb, persona p \n");
			sql.append(" WHERE cb.no_persona = p.no_persona \n");
			sql.append(" 	And cb.id_tipo_persona = p.id_tipo_persona \n");
			sql.append(" 	And p.id_tipo_persona = 'P' \n");
			sql.append(" 	And p.equivale_persona = '" + gridDatos.get(i).get("noBenef") + "' \n");
			sql.append(" 	And p.equivale_persona like '" + noEmpresa + "%' \n");
			
			System.out.println("Query que busca las ctas banco" + sql);
			
			res = jdbcTemplate.queryForInt(sql.toString());
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:buscaDatosBenef");
			return 0;
		}
		return res;
	}
	
	public int validaFacultad(int idFacultad) {
		objConsultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return objConsultasGenerales.validaFacultad(idFacultad);
	}
	
	public List<CargaPagosDto> llenaGridCXC(int noEmpresaParam, String estatus) {
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT z.no_empresa, e.nom_empresa, z.no_factura, z.fec_fact, coalesce(p.equivale_persona, '') as equivale_persona, \n");
			sql.append(" 		coalesce(p.razon_social, '') as razon_social, z.importe, c.desc_divisa, cf.desc_forma_pago, ce.desc_estatus, z.concepto, \n");
			sql.append(" 		case when coalesce(z.no_factura, '') = '' then 'NO CUENTA CON N�MERO DE FACTURA!!' \n");
			sql.append(" 			 when coalesce(z.no_benef, '') = '' or coalesce(p.razon_social, '') = '' then 'NO SE AH IMPORTADO EL CLIENTE!!' else coalesce(z.causa_rech, '') end as causa_rech \n");
			sql.append(" FROM zimp_dep z left join persona p on (z.no_benef = p.equivale_persona), empresa e, cat_divisa c, \n");
			sql.append(" 	  cat_forma_pago cf, cat_estatus ce \n");
			sql.append(" WHERE z.no_empresa = e.no_empresa \n");
			sql.append(" 	And z.id_divisa = c.id_divisa \n");
			sql.append(" 	And z.forma_pago = cf.id_forma_pago \n");
			sql.append(" 	And z.estatus = ce.id_estatus \n");
			sql.append(" 	And z.estatus = '"+ estatus +"' \n");
//			sql.append(" 	And p.id_tipo_persona = 'C' \n");
			sql.append(" 	And ce.clasificacion = 'CXC' \n");
			
			if(noEmpresaParam != 0)
				sql.append(" 	And z.no_empresa = "+ noEmpresaParam +" \n");
			
			System.out.println("Query zimp_dep: " + sql);
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper() {
				public CargaPagosDto mapRow(ResultSet rs, int idx) throws SQLException {
					CargaPagosDto objCampos = new CargaPagosDto();
					objCampos.setNoEmpresa(rs.getInt("no_empresa"));
					objCampos.setNomEmpresa(rs.getString("nom_empresa"));
					objCampos.setNoFactura(rs.getString("no_factura"));
					objCampos.setFecFact(rs.getString("fec_fact"));
					objCampos.setNoBenef(rs.getString("equivale_persona"));
					objCampos.setRazonSocial(rs.getString("razon_social"));
					objCampos.setImporte(rs.getDouble("importe"));
					objCampos.setIdDivisa(rs.getString("desc_divisa"));
					objCampos.setIdFormaPago(rs.getString("desc_forma_pago"));
					objCampos.setEstatusAlta(rs.getString("desc_estatus"));
					objCampos.setConcepto(rs.getString("concepto"));
					objCampos.setConceptoRechazo(rs.getString("causa_rech"));
					return objCampos;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:llenaGridCXC");
		}
		return listaResultado;
	}
	
	public int existeCXC(List<Map<String, String>> objCamposGrid, int i) {
		int res = 0;
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT count(*) \n");
			sql.append(" FROM cobranza \n");
			sql.append(" WHERE no_empresa = "+ objCamposGrid.get(i).get("noEmpresa") +" \n");
			sql.append(" 	And no_factura = '"+ objCamposGrid.get(i).get("noFactura") +"' \n");
			sql.append(" 	And no_benef = '"+ objCamposGrid.get(i).get("noBenef") +"' \n");
			sql.append(" 	And importe = "+ objCamposGrid.get(i).get("importe") +" \n");
			
			res = jdbcTemplate.queryForInt(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:llenaGridCXC");
		}
		return res;
	}
	
	public int insertaRegistrosCXC(List<Map<String, String>> objCamposGrid, int i) { 
		int res = 0;
		StringBuffer sql = new StringBuffer();
		objConsultasGenerales = new ConsultasGenerales(jdbcTemplate);
		funciones = new Funciones();
		
		try {
			sql.append(" insert into cobranza \n");
			sql.append(" SELECT no_empresa, no_factura, fec_fact, no_benef, importe, id_divisa, concepto, forma_pago, \n");
			sql.append(" 		0, 0, '', '"+ funciones.ponerFecha(objConsultasGenerales.obtenerFechaHoy()) +"', estatus, 0, 0, 0, 0, '', '', '', 0 \n");
			sql.append(" FROM zimp_dep \n");
			sql.append(" WHERE no_empresa = "+ objCamposGrid.get(i).get("noEmpresa") +" \n");
			sql.append(" 	And no_factura = '"+ objCamposGrid.get(i).get("noFactura") +"' \n");
			sql.append(" 	And no_benef = '"+ objCamposGrid.get(i).get("noBenef") +"' \n");
			sql.append(" 	And importe = "+ objCamposGrid.get(i).get("importe") +" \n");
			
			res = jdbcTemplate.update(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:insertaRegistrosCXC");
		}
		return res;
	}
	
	public int modificaRegistrosCXC(List<Map<String, String>> objCamposGrid, int i, String estatus, String cusaRech) { 
		int res = 0;
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" UPDATE zimp_dep SET estatus = '"+ estatus +"', causa_rech = '"+ cusaRech +"' \n");
			sql.append(" WHERE no_empresa = "+ objCamposGrid.get(i).get("noEmpresa") +" \n");
			sql.append(" 	And no_factura = '"+ objCamposGrid.get(i).get("noFactura") +"' \n");
			sql.append(" 	And no_benef = '"+ objCamposGrid.get(i).get("noBenef") +"' \n");
			sql.append(" 	And importe = "+ objCamposGrid.get(i).get("importe") +" \n");
			
			res = jdbcTemplate.update(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:modificaRegistrosCXC");
		}
		return res;
	}
	
	/**
	 * Este metodo es para hacer el enlace con el bean de conexion
	 * en el aplicationContext
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource)
	{
		try
		{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Interfaz, C:CargaPagosDaoImpl, M:setDataSource");
		}
	}
/*
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	*/

	@Override
	public List<Map<String, String>> reporteInterfaces(String tipoValor, String fecHoy, String usuarioAlta,
			String noEmpresaParam, String estatus, String fecIni, String fecFin) {
		StringBuffer sql = new StringBuffer();
		List<Map<String, String>> resultado = new ArrayList<Map<String, String>>();
		try {
			sql.append(" SELECT i.fec_fact,i.secuencia, i.no_doc_sap, i.no_empresa, i.no_factura, i.fec_fact, i.fec_valor, \n");
            sql.append(" 	i.no_benef, p.razon_social, p.nombre ,p.paterno ,p.materno, i.imp_solic , i.importe, i.id_divisa, \n");
            sql.append(" 	i.tipo_camb, i.concepto, forma_pago, i.cve_leyen, i.benef_alt, i.id_bco_alt, i.id_chq_alt, \n");
            sql.append(" 	i.id_rubro, i.centro_cto, i.gpo_tesor, i.cod_bloq, i.ind_may_es, i.estatus, i.causa_rech, i.fecha_imp \n");
            sql.append(" FROM zimp_fact i left join persona p on i.no_benef = p.equivale_persona left join SET006 s on i.no_empresa = s.SOIEMP \n");
            sql.append(" WHERE ");
            
            if(noEmpresaParam.equals("0")) {
                sql.append(" s.SETEMP in ");
                sql.append("     (select no_empresa");
                sql.append("     from usuario_empresa ");
                sql.append("     where no_usuario = " + usuarioAlta + ")");
            }else
            	sql.append(" (s.SETEMP = " + noEmpresaParam + " or I.NO_EMPRESA = " + noEmpresaParam + ")");
            //sql.append(" and i.no_empresa = s.SOIEMP \n");
            sql.append(" and s.SISCOD = 'CP' \n");
           // sql.append(" and i.no_benef = p.equivale_persona \n");
            sql.append(" and id_tipo_persona= (case when i.forma_pago = 6 then 'E' else 'P' end) \n");
            
            if(!estatus.equals("")) {
            	if(estatus.equals("P")) {
                    sql.append(" and ( ");
                    sql.append(" (i.estatus IN ('P') and i.cod_bloq IN('N','S')) ");
                    sql.append(" OR (i.estatus = 'U' and i.cod_bloq IN('H','D') ");
                    sql.append(" OR i.estatus = 'M')");
                    sql.append("     )");
                }else if(estatus.equals("C"))
                	sql.append(" and i.estatus in ('C','A') \n");
                else
                	sql.append(" and i.estatus = '" + estatus + "' \n");
            }
            sql.append("and FECHA_IMP >= TO_DATE('" + fecIni + "', 'DD/MM/YYYY') AND FECHA_IMP < TO_DATE('" + fecFin + "', 'DD/MM/YYYY') + 1");
           
            //sql.append(" and i.desc_propuesta is NULL \n");
            //sql.append(" and i.fec_propuesta is NULL \n");
            /*
            if(pvsFec1 <> "")
            	sql.append(" and i.fec_valor >= '" + pvsFec1 + "' \n");
            
            if(pvsFec2 <> "")
            	sql.append(" and i.fec_valor <= '" + pvsFec2 + "' \n");
            */
			System.out.println("Query zimp_fact: " + sql);
			
			resultado = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>() {
				public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException {
					Map<String, String> objCampos = new HashMap<String, String>();
					objCampos.put("Secuencia", rs.getString("secuencia"));
					objCampos.put("NoDocto", rs.getString("no_doc_sap"));
					objCampos.put("NoEmpresa", rs.getString("no_empresa"));
					objCampos.put("NoFactura", rs.getString("no_factura"));
					objCampos.put("FecFact", rs.getString("fec_fact"));
					objCampos.put("FecValor", rs.getString("fec_valor"));
					objCampos.put("NoBenef", rs.getString("no_benef"));
					objCampos.put("RazonSocial", rs.getString("razon_social"));
					objCampos.put("Nombre", rs.getString("nombre"));
					objCampos.put("ApellidoPaterno", rs.getString("paterno"));
					objCampos.put("ApellidoMaterno", rs.getString("materno"));
					objCampos.put("ImpSolic", rs.getString("imp_solic"));
					objCampos.put("Importe", rs.getString("importe"));
					objCampos.put("IdDivisa", rs.getString("id_divisa"));
					objCampos.put("TipoCamb", rs.getString("tipo_camb"));
					objCampos.put("Concepto", rs.getString("concepto"));
					objCampos.put("IdFormaPago", rs.getString("forma_pago"));
					objCampos.put("CveLeyen", rs.getString("cve_leyen"));
					objCampos.put("BenefAlt", rs.getString("benef_alt"));
					objCampos.put("IdBcoAlt", rs.getString("id_bco_alt"));
					objCampos.put("IdChqAlt", rs.getString("id_chq_alt"));
					objCampos.put("IdRubro", rs.getString("id_rubro"));
					objCampos.put("CentroCostos", rs.getString("centro_cto"));
					objCampos.put("GpoTesor", rs.getString("gpo_tesor"));
					objCampos.put("CodBloq", rs.getString("cod_bloq"));
					objCampos.put("IndMayEs", rs.getString("ind_may_es"));
					objCampos.put("EstatusPropuesta", rs.getString("estatus"));
					objCampos.put("ConceptoRechazo", rs.getString("causa_rech"));
					objCampos.put("FechaImp", rs.getString("fecha_imp"));
					return objCampos;
				}
			});
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Interfaz, C:CargaPagosDaoImpl, M:selectImportaCP");
		}
		return resultado;
	}
	
}
