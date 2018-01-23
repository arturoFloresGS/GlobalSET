package com.webset.set.utilerias.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
//import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.dao.MantenimientoChequerasDao;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utileriasmod.dto.MantenimientoChequerasDto;

import jcifs.dcerpc.msrpc.srvsvc;

public class MantenimientoChequerasDaoImpl implements MantenimientoChequerasDao
{
	/*
	 *Revisado por LASM 
	 *LE QUITE LOS WARNINGS 
	 */
	//Pendiente revisar si se va a insertar en cat_cta_banco_cancela las chequeras eliminadas
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora;
	String sql = "";
	ConsultasGenerales consultasGenerales;
	
	public String configuraSet(int indice){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
	}
	

	public List<LlenaComboEmpresasDto> obtenerEmpresas(int idUsuario) 
	{		
		consultasGenerales = new ConsultasGenerales (jdbcTemplate);
		return consultasGenerales.llenarComboEmpresas(idUsuario);
	}
	
	
	public List<MantenimientoChequerasDto> obtenerBancos(int noEmpresa) 
	{
		String sql="";
		List<MantenimientoChequerasDto> list = new ArrayList<MantenimientoChequerasDto>();
		
		try
		{
		    sql+= "SELECT id_banco as ID, desc_banco as DESCRIPCION";
		    sql+= "\n	FROM cat_banco ";
		    sql+= "\n	WHERE id_banco in (SELECT distinct id_banco FROM cat_cta_banco";
		    sql+= "\n					   WHERE tipo_chequera in ('P', 'O', 'M', 'C')";
		    sql+= "\n					   		And no_empresa = " + noEmpresa + ")";
		    		    
		    list = jdbcTemplate.query(sql, new RowMapper<MantenimientoChequerasDto>()
		    {
		    	public MantenimientoChequerasDto mapRow(ResultSet rs, int idx) throws SQLException 
		    	{
		    		MantenimientoChequerasDto cons = new MantenimientoChequerasDto();
					cons.setIdBanco(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}
		    });
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoChequerasDaoImpl, M:obtenerBancos");
		}
		return list;
	}
	
	
	
	public List<MantenimientoChequerasDto> llenaGrid(int noEmpresa, int idBanco)
	{
		List<MantenimientoChequerasDto> listaResultado = new ArrayList<MantenimientoChequerasDto>();
		sql = "";		
		try
		{			
			sql += "Select ccb.no_empresa,ccb.id_banco, ccb.b_cheque, ccb.b_transferencia, ccb.b_cheque_ocurre, ccb.b_cargo_en_cuenta, ";
			sql += "\n ccb.desc_plaza as plaza, ccb.desc_sucursal as sucursal, ccb.id_divisa, cd.desc_divisa, ccb.desc_chequera, ";
			sql += "\n ccb.id_chequera, cb.desc_banco, ccb.saldo_inicial, ccb.cargo, ccb.abono, ";
			sql += "\n ccb.ult_cheq_impreso, ccb.abono_sbc, ccb.tipo_chequera, ccb.saldo_minimo, ";
			sql += "\n ccb.saldo_banco_ini, ccb.saldo_final, ";
			sql += "\n ccb.b_concentradora, ccb.nac_ext, ccb.b_traspaso, ccb.saldo_conta_ini, ";
			sql += "\n case when ccb.aba = '' then ccb.swift_code else ccb.aba end as aba, ccb.swift_code, ccb.id_clabe, ccb.b_impre_continua, ";
			sql += "\n ccb.periodo_conciliacion, ccb.id_division, '' as desc_division, ccb.sobregiro, ";
			sql += "\n ccb.pago_mass, ccb.housebank, ctc.desc_chequera as desc__tipo_chequera ";
			sql += "\n from cat_cta_banco ccb, cat_banco cb , cat_divisa cd, cat_tipo_chequera ctc";
			sql += "\n where ccb.no_empresa = "+ noEmpresa +" ";
			if(idBanco != 0){
				sql += "\n and ccb.id_banco = "+ idBanco +" ";
			}
			sql += "\n and ccb.id_banco = cb.id_banco ";
			sql += "\n and ccb.id_divisa = cd.id_divisa ";
			sql += "\n and ccb.tipo_chequera = ctc.tipo_chequera ";
			
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql, new RowMapper<MantenimientoChequerasDto>()
			{
				public MantenimientoChequerasDto mapRow(ResultSet rs, int idx) throws SQLException
				{
					MantenimientoChequerasDto campos = new MantenimientoChequerasDto();
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setIdBanco(rs.getInt("id_banco"));					
					campos.setIdChequera(rs.getString("id_chequera"));
					campos.setPlaza(rs.getString("plaza"));
					campos.setSucursal(rs.getString("sucursal"));
					campos.setDescChequera(rs.getString("desc_chequera"));
					campos.setSaldoInicial(rs.getDouble("saldo_inicial"));
					campos.setCargo(rs.getDouble("cargo"));
					campos.setAbono(rs.getDouble("abono"));
					campos.setBCheques(rs.getString("b_cheque"));
					campos.setBTransferencias(rs.getString("b_transferencia"));
					campos.setChequeOcurre(rs.getString("b_cheque_ocurre"));
					campos.setCargoEnCuenta(rs.getString("b_cargo_en_cuenta"));					
					campos.setIdDivisa(rs.getString("id_divisa"));					
					campos.setDescDivisa(rs.getString("desc_divisa"));
					campos.setDescripcion(rs.getString("desc_banco"));					
					campos.setUltimoCheque(rs.getInt("ult_cheq_impreso"));
					campos.setAbonoSBC(rs.getDouble("abono_sbc"));
					campos.setTipoChequera(rs.getString("tipo_chequera"));
					campos.setDescTipoChequera(rs.getString("desc__tipo_chequera"));					
					campos.setSaldoMinimo(rs.getDouble("saldo_minimo"));
					campos.setSaldoBcoInicial(rs.getDouble("saldo_banco_ini"));
					campos.setSaldoFinal(rs.getDouble("saldo_final"));
					campos.setBConcentradora(rs.getString("b_concentradora"));
					campos.setNacionalidad(rs.getString("nac_ext"));
					campos.setBTraspaso(rs.getString("b_traspaso"));
					campos.setSaldoConta(rs.getDouble("saldo_conta_ini"));
					campos.setAba(rs.getString("aba"));
					//campos.setSwift(rs.getString("swift_code"));
					campos.setClabe(rs.getString("id_clabe"));
					campos.setImpresionContinua(rs.getString("b_impre_continua"));
					campos.setPeriodoConciliacion(rs.getString("periodo_conciliacion"));
					campos.setIdDivision(rs.getString("id_division"));
					campos.setDescDivision(rs.getString("desc_division"));
					campos.setSobregiro(rs.getDouble("sobregiro"));
					campos.setMassPayment(rs.getString("pago_mass"));
					campos.setHouseBank(rs.getString("housebank"));				
					
					return campos;
				}				
				
			});			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoChequerasDaoImpl, M:llenaGrid");
		}
		return listaResultado;
	}
	@Override
	public List<Map<String, String>> reporteChequeras(String tipoChequera, String empresa)
	{
		List<Map<String, String>> listaResultado = new ArrayList<Map<String, String>>();
		
		System.out.println("PPPPPPPPPPPPPPPPPPPPPPPPPPPPPP");
		System.out.println(empresa);
		System.out.println("PPPPPPPPPPPPPPPPPPPPPPPPPPPPPP");
		sql = "";		
		try
		{		
			if (tipoChequera.equals("T")) {
				
				sql += "Select ccb.no_empresa,ccb.id_banco, ccb.b_cheque, ccb.b_transferencia, ccb.b_cheque_ocurre, ccb.b_cargo_en_cuenta, ";
				sql += "\n ccb.desc_plaza as plaza, ccb.desc_sucursal as sucursal, ccb.id_divisa, cd.desc_divisa, ccb.desc_chequera, ";
				sql += "\n ccb.id_chequera, cb.desc_banco, ccb.saldo_inicial, ccb.cargo, ccb.abono, ";
				sql += "\n ccb.ult_cheq_impreso, ccb.abono_sbc, ccb.tipo_chequera, ccb.saldo_minimo, ";
				sql += "\n ccb.saldo_banco_ini, ccb.saldo_final, ";
				sql += "\n ccb.b_concentradora, ccb.nac_ext, ccb.b_traspaso, ccb.saldo_conta_ini, ";
				sql += "\n case when ccb.aba = '' then ccb.swift_code else ccb.aba end as aba, ccb.swift_code, ccb.id_clabe, ccb.b_impre_continua, ";
				sql += "\n ccb.periodo_conciliacion, ccb.id_division, '' as desc_division, ccb.sobregiro, ";
				sql += "\n ccb.pago_mass, ccb.housebank, ctc.desc_chequera as desc__tipo_chequera ";
				sql += "\n from cat_cta_banco ccb, cat_banco cb , cat_divisa cd, cat_tipo_chequera ctc";
				sql += "\n where ccb.id_banco = cb.id_banco ";
				if (!empresa.equals("0")) {
					sql += "\n and ccb.no_empresa = "+ empresa +" ";
				}
				sql += "\n and ccb.id_divisa = cd.id_divisa ";
				sql += "\n and ccb.tipo_chequera = ctc.tipo_chequera ";
				sql += "\n order by ccb.no_empresa, ccb.id_banco, ccb.id_divisa";
				
				
			}else{
				sql += "Select ccb.no_empresa,ccb.id_banco, ccb.b_cheque, ccb.b_transferencia, ccb.b_cheque_ocurre, ccb.b_cargo_en_cuenta, ";
				sql += "\n ccb.desc_plaza as plaza, ccb.desc_sucursal as sucursal, ccb.id_divisa, cd.desc_divisa, ccb.desc_chequera, ";
				sql += "\n ccb.id_chequera, cb.desc_banco, ccb.saldo_inicial, ccb.cargo, ccb.abono, ";
				sql += "\n ccb.ult_cheq_impreso, ccb.abono_sbc, ccb.tipo_chequera, ccb.saldo_minimo, ";
				sql += "\n ccb.saldo_banco_ini, ccb.saldo_final, ";
				sql += "\n ccb.b_concentradora, ccb.nac_ext, ccb.b_traspaso, ccb.saldo_conta_ini, ";
				sql += "\n case when ccb.aba = '' then ccb.swift_code else ccb.aba end as aba, ccb.swift_code, ccb.id_clabe, ccb.b_impre_continua, ";
				sql += "\n ccb.periodo_conciliacion, ccb.id_division, '' as desc_division, ccb.sobregiro, ";
				sql += "\n ccb.pago_mass, ccb.housebank, ctc.desc_chequera as desc__tipo_chequera ";
				sql += "\n from cat_cta_banco ccb, cat_banco cb , cat_divisa cd, cat_tipo_chequera ctc";
				sql += "\n where ccb.id_banco = cb.id_banco ";
				if (!empresa.equals("0")) {
					sql += "\n and ccb.no_empresa = "+ empresa +" ";
				}
				sql += "\n and ccb.tipo_chequera = '"+ tipoChequera +"' ";

				//sql += "\n and ccb.id_banco = cb.id_banco ";
				sql += "\n and ccb.id_divisa = cd.id_divisa ";
				sql += "\n and ccb.tipo_chequera = ctc.tipo_chequera ";
				sql += "\n order by ccb.no_empresa, ccb.id_banco, ccb.id_divisa";
			}
			
			
			
			
			
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql, new RowMapper<Map<String, String>>()
			{
				public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException
				{
					Map<String, String> campos = new HashMap<String, String>();
					campos.put("no_empresa", rs.getString("no_empresa"));
					campos.put("id_banco", rs.getString("id_banco"));					
					campos.put("id_chequera", rs.getString("id_chequera"));
					campos.put("plaza", rs.getString("plaza"));
					campos.put("sucursal", rs.getString("sucursal"));
					campos.put("desc_chequera", rs.getString("desc_chequera"));
					campos.put("saldo_inicial", rs.getString("saldo_inicial"));
					campos.put("cargo", rs.getString("cargo"));
					campos.put("abono", rs.getString("abono"));
					campos.put("b_cheque", rs.getString("b_cheque"));
					campos.put("b_transferencia", rs.getString("b_transferencia"));
					campos.put("b_cheque_ocurre", rs.getString("b_cheque_ocurre"));
					campos.put("b_cargo_en_cuenta", rs.getString("b_cargo_en_cuenta"));					
					campos.put("id_divisa", rs.getString("id_divisa"));					
					campos.put("desc_divisa", rs.getString("desc_divisa"));
					campos.put("desc_banco", rs.getString("desc_banco"));					
					campos.put("ult_cheq_impreso", rs.getString("ult_cheq_impreso"));
					campos.put("abono_sbc", rs.getString("abono_sbc"));
					campos.put("tipo_chequera", rs.getString("tipo_chequera"));
					campos.put("desc__tipo_chequera", rs.getString("desc__tipo_chequera"));					
					campos.put("saldo_minimo", rs.getString("saldo_minimo"));
					campos.put("saldo_banco_ini", rs.getString("saldo_banco_ini"));
					campos.put("saldo_final", rs.getString("saldo_final"));
					campos.put("b_concentradora", rs.getString("b_concentradora"));
					campos.put("nac_ext", rs.getString("nac_ext"));
					campos.put("b_traspaso", rs.getString("b_traspaso"));
					campos.put("saldo_conta_ini", rs.getString("saldo_conta_ini"));
					campos.put("aba", rs.getString("aba"));
					//campos.setSwift(", rs.getString("swift_code"));
					campos.put("id_clabe", rs.getString("id_clabe"));
					campos.put("b_impre_continua", rs.getString("b_impre_continua"));
					campos.put("periodo_conciliacion", rs.getString("periodo_conciliacion"));
					campos.put("id_division", rs.getString("id_division"));
					campos.put("desc_division", rs.getString("desc_division"));
					campos.put("sobregiro", rs.getString("sobregiro"));
					campos.put("pago_mass", rs.getString("pago_mass"));
					campos.put("housebank", rs.getString("housebank"));				
					
					return campos;
				}				
				
			});			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoChequerasDaoImpl, M:llenaGrid");
		}
		return listaResultado;
	}
	
	
	public List<MantenimientoChequerasDto> obtieneTipoChequeras()
	{
		List<MantenimientoChequerasDto> listaResultado = new ArrayList<MantenimientoChequerasDto>();
		sql = "";
		
		try
		{
			sql = "Select tipo_chequera as id, desc_chequera as descripcion ";
			sql += "\n from cat_tipo_chequera ";
			sql += "\n where tipo_chequera <> 'U'";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper<MantenimientoChequerasDto>()
			{
				public MantenimientoChequerasDto mapRow(ResultSet rs, int idx) throws SQLException
				{
					MantenimientoChequerasDto campos = new MantenimientoChequerasDto();
					campos.setIdString(rs.getString("id"));
					campos.setDescChequera(rs.getString("descripcion"));
					return campos;
				}
			});
			
			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + "" + e.toString() + "P:Utilerias, C:MantenimientoChequerasDaoImpl, M:obtieneTipoChequeras");
		}
		return listaResultado;
	}
	
	
	public List<MantenimientoChequerasDto> llenaComboGrupo()
	{
		List<MantenimientoChequerasDto> listaResultado = new ArrayList<MantenimientoChequerasDto>();
		sql = "";
		try
		{
			sql = "Select id_grupo_flujo as id,desc_grupo_flujo as descripcion";
			sql += "\n from cat_grupo_flujo";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper<MantenimientoChequerasDto>()
			{
				public MantenimientoChequerasDto mapRow(ResultSet rs, int idx) throws SQLException
				{
					MantenimientoChequerasDto campos = new MantenimientoChequerasDto();
					campos.setIdEntero(rs.getInt("id"));
					campos.setDescripcion(rs.getString("descripcion"));
					return campos;
				}
			});			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + "" + e.toString() + "P:Utilerias, C:MantenimientoChequerasDaoImpl, M:llenaComboGrupo");
		}return listaResultado;
	}
	
	
	public List<MantenimientoChequerasDto> obtenerBancosTodos(int bancoNacional) 
	{
		String sql="";
		List<MantenimientoChequerasDto> list = new ArrayList<MantenimientoChequerasDto>();
		
		try
		{			
		    sql+= "SELECT id_banco as ID, desc_banco as DESCRIPCION";
		    sql+= "\n	FROM cat_banco ";
		    
		    if (bancoNacional == 0)		    	
		    	sql+= "\n	WHERE nac_ext = 'N'";
		    else
		    	sql+= "\n	WHERE nac_ext = 'E'";		     
		    
		    list = jdbcTemplate.query(sql, new RowMapper<MantenimientoChequerasDto>()
		    {
		    	public MantenimientoChequerasDto mapRow(ResultSet rs, int idx) throws SQLException 
		    	{
		    		MantenimientoChequerasDto cons = new MantenimientoChequerasDto();
					cons.setIdBanco(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}
		    });
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoChequerasDaoImpl, M:obtenerBancosTodos");
		}
		return list;
	}
	
		
	public List<MantenimientoChequerasDto> llenaComboDivision (int noEmpresa)
	{
		List<MantenimientoChequerasDto> listaResultado = new ArrayList<MantenimientoChequerasDto>();
		sql = "";
		
		try{
			
			sql = "Select id_division , desc_division as descripcion ";
			sql += "\n from cat_division ";
			sql += "\n where no_empresa = "+ noEmpresa +"";

			listaResultado = jdbcTemplate.query(sql, new RowMapper<MantenimientoChequerasDto>()
			{
				public MantenimientoChequerasDto mapRow(ResultSet rs, int idx) throws SQLException
				{
					MantenimientoChequerasDto campos = new MantenimientoChequerasDto();
					campos.setIdDivision(rs.getString("id_division"));
					campos.setDescripcion(rs.getString("descripcion"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + "" + e.toString() + "P:Utilerias, C:MantenimientoChequerasDaoImpl, M:llenaComboDivision");
		}return listaResultado;		
	}
	
	
	public List<MantenimientoChequerasDto> llenaComboDivisa()
	{
		List<MantenimientoChequerasDto> listaResultado = new ArrayList<MantenimientoChequerasDto>();
		sql = "";
		try{
			sql = "Select id_divisa as id, ";
			sql += "\n desc_divisa as descripcion ";
			sql += "\n from cat_divisa ";
			sql += "\n where clasificacion = 'D' ";
						
			listaResultado = jdbcTemplate.query(sql, new RowMapper<MantenimientoChequerasDto>(){
				public MantenimientoChequerasDto mapRow(ResultSet rs, int idx)throws SQLException{
					MantenimientoChequerasDto campos = new MantenimientoChequerasDto();
					campos.setIdDivisa(rs.getString("id"));
					campos.setDescripcion(rs.getString("descripcion"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoChequerasDaoImpl, M:llenaComboDivisa");			
		}return listaResultado;
	}
	
	public int buscaRegPendientes(int noEmpresa, int idBanco, String idChequera) {
		int resultado = 0;
		StringBuffer sql = new StringBuffer();
		
		try{
			sql.append(" SELECT count(*) \n");
			sql.append(" FROM movimiento \n");
			sql.append(" WHERE no_empresa = "+ noEmpresa +" \n");
			sql.append(" 	And id_banco = "+ idBanco +" \n");
			sql.append(" 	And id_chequera = '"+ idChequera +"' \n");
			sql.append(" 	And ((id_tipo_operacion = 3200 and id_estatus_mov in ('P', 'T') and id_forma_pago in (3, 5)) or \n");
			sql.append(" 		(id_tipo_operacion = 3200 and id_estatus_mov in ('P') and id_forma_pago = 1))");
			
			resultado = jdbcTemplate.queryForInt(sql.toString());
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + "" + e.toString() + "P:Utilerias, C: MantenimientoChequerasDaoImpl, M:buscaRegPendientes");
		}return resultado;
	}
	
	public int eliminaChequeras (int noEmpresa, int idBanco, String idChequera, int noUsuario)
	{
		int resultado = 0;
		sql = "";
		try{
			/*
			 * sSql = sSql & Chr(10) & " INSERT INTO cat_cta_banco_cancela (no_empresa, id_banco, id_chequera, usuario_alta, fec_alta,id_divisa)"
    sSql = sSql & Chr(10) & " select no_empresa, id_banco, id_chequera, usuario_alta, '" & Format(Now, "dd/mm/yyyy") & "', id_divisa"
    sSql = sSql & Chr(10) & " from cat_cta_banco "
    sSql = sSql & Chr(10) & " where no_empresa = " & piNoEmpresa & " and id_banco = " & piIdBanco & ""
    sSql = sSql & Chr(10) & " and id_chequera = '" & psIdChequera & "';"
    
			 */
			
			sql = "Delete from cat_cta_banco ";
			sql += "\n where id_banco = " + idBanco + " ";
			sql += "\n and id_chequera = '" + idChequera + "'";
						
			resultado = jdbcTemplate.update(sql);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + "" + e.toString() + "P:Utilerias, C: MantenimientoChequerasDaoImpl, M:eliminaChequeras");
		}return resultado;
	}
	
	
	public List<MantenimientoChequerasDto> buscaChequera (int idBanco, String idChequera){
		List<MantenimientoChequerasDto> resultado = new ArrayList<MantenimientoChequerasDto>();
		sql = "";
		try{
			sql = "Select * from cat_cta_banco ";
			sql += "\n where id_banco = " + idBanco + " ";
			sql += "\n and id_chequera = '" + idChequera + "' ";
						
			resultado = jdbcTemplate.query(sql, new RowMapper<MantenimientoChequerasDto>(){
				public MantenimientoChequerasDto mapRow(ResultSet rs, int idx)throws SQLException{
					MantenimientoChequerasDto campos = new MantenimientoChequerasDto();
					campos.setIdBanco(rs.getInt("id_banco"));					
					campos.setIdChequera(rs.getString("id_chequera"));
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					return campos;
				}
			});			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoChequerasDaoImpl, M:buscaChequera");
		}return resultado;
	}
	
	/*
	 *Revisado por LASM 
	 */
	
	
	public int insertaChequera(MantenimientoChequerasDto objDto){
		int resultado = 0;
	//	SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
		
		sql = "";
		
		try{
			sql = "Insert into cat_cta_banco (";
			sql += "\n no_empresa, id_banco, id_chequera, desc_chequera, ";
			sql += "\n id_divisa, saldo_inicial, cargo, abono,";
			sql += "\n saldo_final, ult_cheq_impreso, cargo_sbc, abono_sbc, ";
			sql += "\n desc_plaza, desc_sucursal, tipo_chequera, ";
			sql += "\n b_concentradora, nac_ext, saldo_banco_ini, ";
			sql += "\n usuario_alta, fec_alta, b_traspaso, saldo_minimo, ";
			sql += "\n saldo_conta_ini, periodo_conciliacion, b_cheque, b_transferencia, ";
			sql += "\n b_cheque_ocurre, b_cargo_en_cuenta, id_clabe, b_impre_continua, ";
			sql += "\n aba, swift_code, id_division, sobregiro, ";
			sql += "\n pago_mass, housebank, id_estatus_cta";
			sql += " )";
			sql += " values ("+ objDto.getNoEmpresa() +", " + objDto.getIdBanco() + ", '" + objDto.getIdChequera() + "', '" + objDto.getDescChequera() + "', ";
			sql += "\n '" + objDto.getIdDivisa() + "',  " + objDto.getSaldoInicial() + ", 0, 0, ";
			sql += "\n " + objDto.getSaldoFinal() + ", " + objDto.getUltimoCheque() + ", 0, 0, ";
			sql += "\n '" + objDto.getPlaza() + "', '" + objDto.getSucursal() + "', '" + objDto.getTipoChequera() + "', ";
			sql += "\n '" + objDto.getBConcentradora() + "', '" + objDto.getNacionalidad() + "', " + objDto.getSaldoBcoInicial() + ", ";			
			sql += "\n " + objDto.getIdUsuario() + ", convert(datetime, '" + objDto.getFecHoy() + "', 103), '" + objDto.getBTraspaso() + "', " + objDto.getSaldoMinimo() + ", ";
			sql += "\n " + objDto.getSaldoConta() + ", '"+ objDto.getPeriodoConciliacion() +"', '"+ objDto.getBCheques() +"', '"+ objDto.getBTransferencias() +"', ";
			sql += "\n '"+ objDto.getChequeOcurre() +"', '"+ objDto.getCargoEnCuenta() +"', '"+ objDto.getClabe() +"', 'N', ";
			sql += "\n '"+ objDto.getAba() +"', '"+ objDto.getSwift() +"', '"+ objDto.getIdDivision() +"', "+ objDto.getSobregiro() +", ";
			sql += "\n '"+ objDto.getMassPayment() +"', '"+ objDto.getHouseBank() +"', 'A' ";
			sql += " )";
						
			System.out.println(sql);
			resultado = jdbcTemplate.update(sql);
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoChequerasDaoImpl, M:insertaChequera");
		}return resultado;		
	}
	
	/*
	 *Revisado por LASM 
	 */
	public int actualizaChequera(MantenimientoChequerasDto objDto){
		int resultado = 0;
		sql = "";
		try{
			sql = "Update cat_cta_banco ";
			sql += "\n Set desc_chequera = '"+ objDto.getDescChequera() +"', fec_modif = convert(datetime, '"+ objDto.getFecHoy() +"', 103), ";			
			sql += "\n id_divisa = '"+ objDto.getIdDivisa() +"', ";
			sql += "\n saldo_inicial = "+ objDto.getSaldoInicial() +", ";
			sql += "\n saldo_final = "+ objDto.getSaldoFinal() +", ";
			sql += "\n ult_cheq_impreso = "+ objDto.getUltimoCheque() +", ";
			sql += "\n abono_sbc = "+ objDto.getAbonoSBC() +", ";
			sql += "\n desc_plaza = '"+ objDto.getPlaza() +"', ";
			sql += "\n desc_sucursal = '"+ objDto.getSucursal() +"', ";
			sql += "\n tipo_chequera = '"+ objDto.getTipoChequera() +"', ";
			sql += "\n b_concentradora = '"+ objDto.getBConcentradora() +"', ";
			sql += "\n nac_ext = '"+ objDto.getNacionalidad() +"', ";
			sql += "\n saldo_banco_ini = "+ objDto.getSaldoBcoInicial() +", ";
			sql += "\n saldo_conta_ini = "+ objDto.getSaldoConta() +", ";
			sql += "\n saldo_minimo = "+ objDto.getSaldoMinimo() +", ";
			sql += "\n b_traspaso = '"+ objDto.getBTraspaso() +"', ";
			sql += "\n b_cheque = '"+ objDto.getBCheques() +"', ";
			sql += "\n b_transferencia = '"+ objDto.getBTransferencias() +"', ";
			sql += "\n b_cheque_ocurre = '"+ objDto.getChequeOcurre() +"', ";
			sql += "\n b_cargo_en_cuenta = '"+ objDto.getCargoEnCuenta() +"', ";
			sql += "\n periodo_conciliacion = '"+ objDto.getPeriodoConciliacion() +"', ";
			sql += "\n id_clabe = '"+ objDto.getClabe() +"', ";
			//sql += "\n b_impre_continua = 'N', ";
			sql += "\n aba = '"+ objDto.getAba() +"', ";
			sql += "\n swift_code = '"+ objDto.getSwift() +"', ";
			sql += "\n id_division = '"+ objDto.getIdDivision() +"', ";
			sql += "\n sobregiro = "+ objDto.getSobregiro() +", ";
			sql += "\n pago_mass = '"+ objDto.getMassPayment() +"', ";
			sql += "\n usuario_modif = "+ objDto.getIdUsuario() +", ";
			sql += "\n housebank = '"+ objDto.getHouseBank() +"' ";
			sql += "\n Where id_banco = "+ objDto.getIdBanco() +" and id_chequera = '"+ objDto.getIdChequeraWhere() +"'";
			
			resultado = jdbcTemplate.update(sql);
	
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoChequerasDaoImpl, M:actualizaChequera");
		}return resultado;
	}
	
	
	public List<MantenimientoChequerasDto> obtieneCajas(){
		List<MantenimientoChequerasDto> listaResultado = new ArrayList<MantenimientoChequerasDto>();
		sql = "";
		
		try{
			System.out.println("Entra al query de cajas");
			sql = "Select * from cat_caja";
			
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql, new RowMapper<MantenimientoChequerasDto>(){
				public MantenimientoChequerasDto mapRow(ResultSet rs, int idx) throws SQLException{
					MantenimientoChequerasDto campos = new MantenimientoChequerasDto();
					campos.setIdCaja(rs.getInt("id_caja"));
					campos.setDescCaja(rs.getString("desc_caja"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoChequerasDaoImpl, M:obtieneCajas");
		}return listaResultado;
	}
	
	public List<MantenimientoChequerasDto> facultadDeModificarChequera(int noUsuario, String indice){
		List<MantenimientoChequerasDto> listaResultado = new ArrayList<MantenimientoChequerasDto>();
		StringBuffer sql = new StringBuffer();
		try{			
			sql.append("select * from seg_per_fac_com "); 
			sql.append("\n where id_perfil in ( ");
			sql.append("\n select sp.id_perfil from seg_per_fac_com sp, seg_usuario_perfil up ");
			sql.append("\n where sp.id_perfil = up.id_perfil ");
			sql.append("\n and up.id_usuario = "+ noUsuario +" ");
			sql.append("\n ) ");
			sql.append("\n and id_componente in ("+ indice +") ");
			sql.append("\n and id_facultad = 2 ");
			
			System.out.println(sql.toString());
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MantenimientoChequerasDto>(){
				public MantenimientoChequerasDto mapRow(ResultSet rs, int idx)throws SQLException{
					MantenimientoChequerasDto campos = new MantenimientoChequerasDto();
					campos.setIdPerfil(rs.getInt("id_perfil"));
					campos.setIdComponente(rs.getInt("id_componente"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: MantenimientoChequerasDaoImpl, M: facultadDeModificarChequera");
		}return listaResultado;
	}

	//************************************************************************************************************************************
	public void setDataSource(DataSource dataSource) {
		try
		{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoChequerasDaoImpl, M:setDataSource");
		}
	}


	@Override
	public int guardarChequera(MantenimientoChequerasDto mantenimientoChequerasDto) {
		int r= -1;
		try {
			StringBuffer sql=  new StringBuffer();
			sql.append("insert into CAT_CTA_BANCO \n (");
			sql.append("ID_BANCO,");
			sql.append("DESC_CHEQUERA,");
			sql.append("ID_CHEQUERA,");
			sql.append("DESC_PLAZA,");
			sql.append("DESC_SUCURSAL,");
			sql.append("SALDO_INICIAL,");
			//sql.append("SALDO_FINAL,");
			sql.append("ULT_CHEQ_IMPRESO,");
			sql.append("ABONO_SBC,");
			sql.append("NAC_EXT,");
			sql.append("ID_DIVISA,");
			//sql.append("DESC_DIVISA,");
			sql.append("TIPO_CHEQUERA,");
			sql.append("SALDO_MINIMO,");
			sql.append("PERIODO_CONCILIACION,");
			sql.append("SALDO_CONTA_INI,");
			sql.append("B_CONCENTRADORA,");
			sql.append("B_TRASPASO,");
			sql.append("B_CHEQUE,");
			sql.append("B_TRANSFERENCIA,");
			sql.append("B_CHEQUE_OCURRE,");
			sql.append("B_CARGO_EN_CUENTA,");
			sql.append("ID_CLABE,");
			sql.append("B_IMPRE_CONTINUA,");
			sql.append("ABA,");
			sql.append("ID_DIVISION,");
			//sql.append("DESC_DIVISION,");
			sql.append("SOBREGIRO,");
			sql.append("PAGO_MASS,");
			sql.append("HOUSEBANK,");
			sql.append("USUARIO_ALTA,");
			sql.append("NO_EMPRESA,");
			sql.append("FEC_ALTA,");
			sql.append("FEC_MODIF) \n values ('");
			sql.append(mantenimientoChequerasDto.getIdBanco());
			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getDescChequera());
			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getIdChequera());
			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getPlaza());
			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getSucursal());
			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getSaldoInicial());
			sql.append("', '");
//			sql.append(mantenimientoChequerasDto.getSaldoFinal());
//			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getUltimoCheque());
			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getAbono());
			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getNacionalidad());
			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getIdDivisa());
			sql.append("', '");
//			sql.append(mantenimientoChequerasDto.getDescDivisa());
//			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getTipoChequera());
			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getSaldoMinimo());
			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getPeriodoConciliacion());
			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getSaldoConta());
			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getBConcentradora());
			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getBTraspaso());
			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getBCheques());
			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getBTransferencias());
			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getChequeOcurre());
			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getCargoEnCuenta());
			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getClabe());
			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getImpresionContinua());
			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getAba());
			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getIdDivision());
			sql.append("', '");
//			sql.append(mantenimientoChequerasDto.getDescDivision());
//			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getSobregiro());
			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getMassPayment());
			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getHouseBank());
			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getIdUsuario());
			sql.append("', '");
			sql.append(mantenimientoChequerasDto.getNoEmpresa());
			sql.append("', convert(datetime, '");
			sql.append(mantenimientoChequerasDto.getFecHoy());
			sql.append("', 103), convert(datetime, '");
			sql.append(mantenimientoChequerasDto.getFecHoy());
			sql.append("', 103))");
			return jdbcTemplate.update(sql.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoChequerasDaoImpl, M:guardarChequera");
		}
		return r;
	}

}