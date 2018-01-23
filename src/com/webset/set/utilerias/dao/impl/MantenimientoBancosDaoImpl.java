package com.webset.set.utilerias.dao.impl;

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

import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dao.MantenimientoBancosDao;
import com.webset.set.utileriasmod.dto.MantenimientoBancosDto;

public class MantenimientoBancosDaoImpl implements MantenimientoBancosDao{
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora;
	String sql = "";
	
	@SuppressWarnings("unchecked")
	public List<MantenimientoBancosDto> llenaComboBancos(int bancoNacional) 
	{
		String sql="";
		List<MantenimientoBancosDto> list = new ArrayList<MantenimientoBancosDto>();
		
		try
		{				
		    sql+= "SELECT id_banco as ID, desc_banco as DESCRIPCION";
		    sql+= "\n	FROM cat_banco ";
		  
		    if (bancoNacional == 0)		    	
		    	sql+= "\n	WHERE nac_ext = 'N'";
		    else
		    	sql+= "\n	WHERE nac_ext = 'E'";		     
		    		    
		    list = jdbcTemplate.query(sql, new RowMapper()
		    {
		    	public MantenimientoBancosDto mapRow(ResultSet rs, int idx) throws SQLException 
		    	{
		    		MantenimientoBancosDto cons = new MantenimientoBancosDto();
					cons.setIdBanco(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}
		    });
		}
		catch(Exception e)
		{
			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoBancosDaoImpl, M:llenaComboBancos");
		}
		return list;
	}
	@SuppressWarnings("unchecked")
	public String buscaAba(String aba){
		String resultado = "";
		sql = "";
		List<MantenimientoBancosDto> listaResultado = new ArrayList<MantenimientoBancosDto>();
		
		try{
			sql = "Select desc_banco ";
			sql += "\n from cat_banco_aba a, cat_banco b ";
			sql += "\n where a.aba = "+ aba +" ";
			sql += "\n and a.id_banco = b.id_banco ";
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql, new RowMapper()
			{
				public MantenimientoBancosDto mapRow(ResultSet rs, int idx) throws SQLException{
					MantenimientoBancosDto campos = new MantenimientoBancosDto();
					campos.setDescripcion(rs.getString("desc_banco"));
					return campos;
				};
			});			
			
			if (listaResultado.size() > 0)
				resultado = listaResultado.get(0).getDescripcion();
	
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + "" + e.toString() + "P:Utilerias, C:MantenimientoBancosDaoImpl, M:buscaAba");
		}return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<MantenimientoBancosDto> llenaGridBancos(String descBanco, String aba, int nacionalidad)
	{
		List<MantenimientoBancosDto> listaResultado = new ArrayList<MantenimientoBancosDto>();
		sql = "";
		
		try{			
			sql = "Select desc_banco_inbursa, b_layout_comerica, id_banco, desc_banco, cve_plaza, codigo_seguridad, ";
			sql += "\n id_banco_cecoban, path_protegido, arch_protegido, fec_protegido, no_protegido, ";
			sql += "\n b_operacion, fec_alta, usuario_alta, fec_modif, usuario_modif, b_cheque_elect, ";
			sql += "\n nac_ext, b_banca_elect, inst_finan, id_banco_city, b_protegido, libera_aut, hora_libera, ";
			sql += "\n b_saldo_banco, plaza_ext, valida_clabe, coalesce(b_cheque_ocurre, 'N') as cheque_ocurre, ";
			sql += "\n coalesce(transfer_casa_cambio, 'N') as transfer_por_banco ";
			sql += "\n from cat_banco ";
			sql += "\n where id_banco <> 1 ";
			
			if (nacionalidad == 0)
				sql += "\n and nac_ext = 'N' ";
			else
				sql += "\n and nac_ext = 'E' ";
			
			if (descBanco.length() > 0){
				if (aba.length() > 0)
					sql += "\n and desc_banco = '"+ descBanco +"' ";
				else
					sql += "\n and desc_banco like '%"+ descBanco +"%' ";
			}
			
			sql += "\n order by id_banco";
			System.out.println("Entro,Query");
			System.out.println(sql);
						
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public MantenimientoBancosDto mapRow(ResultSet rs, int idx) throws SQLException{
					MantenimientoBancosDto campos = new MantenimientoBancosDto();
					campos.setIdBanco(rs.getInt("id_banco"));
					campos.setDescripcion(rs.getString("desc_banco"));
					campos.setDescripcionInbursa(rs.getString("desc_banco_inbursa"));					
					campos.setClavePlaza(rs.getString("cve_plaza"));
					campos.setCodigoSeguridad(rs.getString("codigo_seguridad"));
					campos.setIdBancoCecoban(rs.getInt("id_banco_cecoban"));
					campos.setPathArchivo(rs.getString("path_protegido"));
					campos.setArchivoProtegido(rs.getString("arch_protegido"));
					campos.setFechaProtegido(rs.getString("fec_protegido"));
					campos.setNoProtegido(rs.getInt("no_protegido"));
					campos.setBProtegido(rs.getString("b_protegido"));					
					campos.setBChequeElect(rs.getString("b_cheque_elect"));
					campos.setNacionalidad(rs.getString("nac_ext"));
					campos.setBancaElect(rs.getString("b_banca_elect"));
					campos.setPrefInstitucion(rs.getString("inst_finan"));
					campos.setLiberacionAutomatica(rs.getString("libera_aut"));
					campos.setHoraLiberacion(rs.getString("hora_libera"));
					campos.setSaldoBanco(rs.getString("b_saldo_banco"));
					campos.setPlazaExtranjera(rs.getString("plaza_ext"));
					campos.setLayoutComerica(rs.getString("b_layout_comerica"));
					campos.setChequeOcurre(rs.getString("cheque_ocurre"));
					campos.setValidacionClabe(rs.getString("valida_clabe"));
					campos.setTransferPorBanco(rs.getString("transfer_por_banco"));
					
					campos.setBOperacion(rs.getString("b_operacion"));
					campos.setFecAlta(rs.getString("fec_alta"));
					campos.setUsuarioAlta(rs.getInt("usuario_alta"));
					campos.setFecModif(rs.getString("fec_modif"));
					campos.setUsuarioModif(rs.getInt("usuario_modif"));
					
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ManteninientoBancosDaoImpl, M:llenaGridBancos");
		}return listaResultado;
	}
	
	public int obtieneIdBancoMax(){
		int resultado = 0;
		sql = "";
		try{
			sql = "Select max(id_banco) + 1 as id_banco ";
			sql += "\n from cat_banco ";
			sql += "\n where id_banco < 20000 ";
			sql += "\n and nac_ext = 'E' ";
			
			resultado = jdbcTemplate.queryForInt(sql);
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ManteninientoBancosDaoImpl, M:obtieneIdBancoMax");
		}return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<MantenimientoBancosDto> validaChequerasAsignadas(int idBanco){
		List<MantenimientoBancosDto> listaResultado = new ArrayList<MantenimientoBancosDto>();
		sql = "";
		
		try{
			System.out.println("llega al dao");
			sql = "Select no_empresa, id_banco, id_chequera, desc_chequera, ";
			sql += "\n id_divisa, saldo_inicial, cargo, abono, saldo_final, ";
			sql += "\n ult_cheq_impreso, cargo_sbc, abono_sbc, desc_plaza, ";
			sql += "\n desc_sucursal, b_concentradora, tipo_chequera, saldo_banco_ini, ";
			sql += "\n usuario_alta, sub_cuenta, ssub_cuenta, fec_alta, usuario_modif, ";
			sql += "\n fec_modif, nac_ext, b_traspaso, saldo_minimo, saldo_conta_ini, fec_conciliacion, periodo_conciliacion ";
			sql += "\n from cat_cta_banco ";
			sql += "\n where id_banco = "+ idBanco +" ";
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public MantenimientoBancosDto mapRow(ResultSet rs, int idx) throws SQLException{
					MantenimientoBancosDto campos = new MantenimientoBancosDto();
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setIdBanco(rs.getInt("id_banco"));
					campos.setIdChequera(rs.getString("id_chequera"));
					campos.setDescChequera(rs.getString("desc_chequera"));
					campos.setIdDivisa(rs.getString("id_divisa"));
					campos.setSaldoInicial(rs.getDouble("saldo_inicial"));
					campos.setCargo(rs.getDouble("cargo"));
					campos.setAbono(rs.getDouble("abono"));
					campos.setSaldoFinal(rs.getDouble("saldo_final"));
					campos.setUltChequeImpreso(rs.getInt("ult_cheq_impreso"));
					campos.setCargoSBC(rs.getDouble("cargo_sbc"));
					campos.setAbonoSBC(rs.getDouble("abono_sbc"));
					campos.setDescPlaza(rs.getString("desc_plaza"));
					campos.setDescSucursal(rs.getString("desc_sucursal"));
					campos.setBConcentradora(rs.getString("b_concentradora"));
					campos.setTipoChequera(rs.getString("tipo_chequera"));
					
					return campos;
				}
			});			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C:MantenimientoBancosDaoImpl, M:validaChequerasAsignadas");
		}return listaResultado;
	}
	
	public int eliminaBanco(int idBanco){
		int resultado = 0;
		sql = "";
		
		try{
			sql = "Delete from cat_banco ";
			sql += "\n where id_banco = "+ idBanco +" ";
			
			resultado = jdbcTemplate.update(sql);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoBancosDaoImpl, M:eliminaBanco");
		}return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<MantenimientoBancosDto> buscaBancoExistente(int idBanco){
		List<MantenimientoBancosDto> listaResultado = new ArrayList<MantenimientoBancosDto>();
		sql = "";
		
		try{
			sql = "Select * from cat_banco ";
			sql += "where id_banco = "+ idBanco +" ";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public MantenimientoBancosDto mapRow(ResultSet rs, int idx) throws SQLException{
					MantenimientoBancosDto campos = new MantenimientoBancosDto();
					campos.setIdBanco(rs.getInt("id_banco"));
					campos.setDescripcion(rs.getString("desc_banco"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoBancosDaoImp, M:buscaBancoExistente");
		}return listaResultado;
	}
	
	public int insertaBanco(MantenimientoBancosDto objDto){
		int resultado = 0;
		sql = "";
		try{
			System.out.println("entra al sql");
			sql = "Insert into cat_banco ( ";
			sql += "\n id_banco, desc_banco, id_banco_cecoban, b_cheque_elect, ";
			sql += "\n b_protegido, cve_plaza, codigo_seguridad, path_protegido, ";
			sql += "\n arch_protegido, fec_protegido, no_protegido, nac_ext, "; 
			sql += "\n b_banca_elect, inst_finan, fec_alta, usuario_alta, ";
			sql += "\n libera_aut, hora_libera, b_saldo_banco, plaza_ext, ";
			sql += "\n b_layout_comerica, desc_banco_inbursa, valida_CLABE, b_cheque_ocurre, transfer_casa_cambio )";
			sql += "\n values ( ";
			sql += "\n "+ objDto.getIdBanco() +", '"+ objDto.getDescripcion() +"', "+ objDto.getIdBancoCecoban() +", '"+ objDto.getBChequeElect() +"', ";
			sql += "\n '"+ objDto.getBProtegido() +"', '"+ objDto.getClavePlaza() +"', '"+ objDto.getCodigoSeguridad() +"', '"+ objDto.getPathArchivo() +"', ";
			sql += "\n '"+ objDto.getArchivoProtegido() +"', '"+ objDto.getFechaProtegido() +"', "+ objDto.getNoProtegido() +", '"+ objDto.getNacionalidad() +"', ";
			sql += "\n '"+ objDto.getBancaElect() +"', '"+ objDto.getPrefInstitucion() +"', '"+ objDto.getFecAlta() +"', "+ objDto.getUsuarioAlta() +", ";
			sql += "\n '"+ objDto.getLiberacionAutomatica() +"', '"+ objDto.getHoraLiberacion() +"', '"+ objDto.getSaldoBanco() +"', '"+ objDto.getPlazaExtranjera() +"', ";
			sql += "\n '"+ objDto.getBLayoutComerica() +"', '"+ objDto.getDescripcionInbursa() +"', '"+ objDto.getValidacionClabe() +"', ";
			sql += "\n '"+ objDto.getChequeOcurre() +"', '"+ objDto.getTransferPorBanco() +"'";
			sql += "\n )";
			
			System.out.println(sql);
			resultado = jdbcTemplate.update(sql);		
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoBancosDaoImpl, M:insertaBanco");
		}return resultado;
	}
	
	public int actualizaBanco(MantenimientoBancosDto objDto){
		int resultado = 0;
		sql = "";
		System.out.println("Entra al Query");
		try{
			//System.out.println("Entra al update");
			//USAR STRING BUFERR.
			sql += "\n Update cat_banco Set ";
			//sql += "\n id_banco = '"+ objDto.getIdBanco() +",";
			sql += "\n desc_banco = '"+ objDto.getDescripcion() +"',"; //cve_plaza = '"+ objDto.getClavePlaza() +"', ";
			sql += "\n cve_plaza = '"+ objDto.getClavePlaza() +"', ";
			sql += "\n codigo_seguridad = '"+ objDto.getCodigoSeguridad() +"', id_banco_cecoban = "+ objDto.getIdBancoCecoban() +", ";
			sql += "\n usuario_modif = "+ objDto.getUsuarioAlta() +", fec_modif = '"+ objDto.getFecAlta() +"', ";
			sql += "\n path_protegido = '"+ objDto.getPathArchivo() +"', arch_protegido = '"+ objDto.getArchivoProtegido() +"', ";
			sql += "\n fec_protegido = '"+ objDto.getFechaProtegido() +"', no_protegido = "+ objDto.getNoProtegido() +", ";
			sql += "\n inst_finan = '"+ objDto.getPrefInstitucion() +"', b_cheque_elect = '"+ objDto.getBChequeElect() +"',";
			sql += "\n b_protegido = '"+ objDto.getBProtegido() +"', b_banca_elect = '"+ objDto.getBancaElect() +"', ";
			sql += "\n nac_ext = '"+ objDto.getNacionalidad() +"', libera_aut = '"+ objDto.getLiberacionAutomatica() +"', ";
			sql += "\n hora_libera = '"+ objDto.getHoraLiberacion() +"', b_saldo_banco = '"+ objDto.getSaldoBanco() +"', ";
			sql += "\n plaza_ext = '"+ objDto.getPlazaExtranjera() +"', b_layout_comerica = '"+ objDto.getBLayoutComerica() +"', ";
			sql += "\n desc_banco_inbursa = '"+ objDto.getDescripcionInbursa() +"', valida_CLABE = '"+ objDto.getValidacionClabe() +"', ";
			sql += "\n b_cheque_ocurre = '"+ objDto.getChequeOcurre() +"', transfer_casa_cambio = '"+ objDto.getTransferPorBanco() +"' ";
			sql += "\n where id_banco = "+ objDto.getIdBanco() +"  ";
			
			//System.out.println(sql);
			resultado = jdbcTemplate.update(sql);			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoBancosDaoImpl, M:actualizaBanco");
		}return resultado;
	}
	
	//*****************************************
	public void setDataSource(DataSource dataSource)
	{
		try
		{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoBancosDaoImpl, M:setDataSource");
		}
	}
	@Override
	public List<Map<String, String>> reporteBancos(String tipoBanco) {
		List<Map<String, String>> listaResultado = new ArrayList<Map<String, String>>();
	
		sql = "";
		
		try{			
			sql = "Select desc_banco_inbursa, b_layout_comerica, id_banco, desc_banco, cve_plaza, codigo_seguridad, ";
			sql += "\n id_banco_cecoban, path_protegido, arch_protegido, fec_protegido, no_protegido, ";
			sql += "\n b_operacion, fec_alta, usuario_alta, fec_modif, usuario_modif, b_cheque_elect, ";
			sql += "\n nac_ext, b_banca_elect, inst_finan, id_banco_city, b_protegido, libera_aut, hora_libera, ";
			sql += "\n b_saldo_banco, plaza_ext, valida_clabe, coalesce(b_cheque_ocurre, 'N') as cheque_ocurre, ";
			sql += "\n coalesce(transfer_casa_cambio, 'N') as transfer_por_banco ";
			sql += "\n from cat_banco ";
			sql += "\n where id_banco <> 1 ";
			sql += "\n and nac_ext ='"+tipoBanco+"'";
			
			
//			if (descBanco.length() > 0){
//				if (aba.length() > 0)
//					sql += "\n and desc_banco = '"+ descBanco +"' ";
//				else
//					sql += "\n and desc_banco like '%"+ descBanco +"%' ";
//			}
			
			sql += "\n order by id_banco";
			
			System.out.println(sql);
						
			listaResultado = jdbcTemplate.query(sql, new RowMapper<Map<String, String>>(){
				public Map<String, String> mapRow(ResultSet rs, int idx)throws SQLException{
					Map<String, String> campos = new HashMap<String, String>();
					campos.put("idBanco",rs.getString("id_banco"));
					campos.put("descripcion",rs.getString("desc_banco"));
					campos.put("descripcionInbursa",rs.getString("desc_banco_inbursa"));
					campos.put("clavePlaza",rs.getString("cve_plaza"));					
					campos.put("codigoSeguridad",rs.getString("codigo_seguridad"));
					campos.put("idBancoCecoban",rs.getString("id_banco_cecoban"));
					campos.put("pathArchivo",rs.getString("path_protegido"));
					campos.put("archivoProtegido",rs.getString("arch_protegido"));
					campos.put("fechaProtegido",rs.getString("fec_protegido"));					
					campos.put("noProtegido",rs.getString("no_protegido"));
					campos.put("bProtegido",rs.getString("b_protegido"));
					campos.put("bChequeElect",rs.getString("b_cheque_elect"));
					campos.put("nacionalidad",rs.getString("nac_ext"));
					campos.put("bancaElect",rs.getString("b_banca_elect"));
					campos.put("prefInstitucion",rs.getString("inst_finan"));					
					campos.put("liberacionAutomatica",rs.getString("libera_aut"));
					campos.put("horaLiberacion",rs.getString("hora_libera"));
					campos.put("saldoBanco",rs.getString("b_saldo_banco"));
					campos.put("plazaExtranjera",rs.getString("plaza_ext"));
					campos.put("layoutComerica",rs.getString("b_layout_comerica"));
					campos.put("chequeOcurre",rs.getString("cheque_ocurre"));
					campos.put("validacionClabe",rs.getString("valida_clabe"));
					campos.put("tranferPorBanco",rs.getString("transfer_por_banco"));
					campos.put("bOperacion",rs.getString("b_operacion"));
					campos.put("fec_alta",rs.getString("fec_alta"));
					campos.put("usuarioAlta",rs.getString("usuario_alta"));
					campos.put("fecModif",rs.getString("fec_modif"));
					campos.put("usuarioModif",rs.getString("usuario_modif"));
					
					
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ManteninientoBancosDaoImpl, M:llenaGridBancos");
		}return listaResultado;
	}

}
