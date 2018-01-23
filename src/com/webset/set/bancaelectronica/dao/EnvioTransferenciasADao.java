package com.webset.set.bancaelectronica.dao;

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

import com.webset.set.bancaelectronica.dto.ArchTransferDto;
import com.webset.set.bancaelectronica.dto.CriterioBusquedaDto;
import com.webset.set.bancaelectronica.dto.DetArchTranferAgrup;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.utils.tools.Utilerias;

public class EnvioTransferenciasADao {

	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	ConsultasGenerales consultasGenerales;
	Funciones funciones = new Funciones();
	String gsDBM="SQL SERVER";
	public boolean pbH2HBital;
	private GlobalSingleton globalSingleton;
	public String consultarConfiguraSet(int indice){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
	}
	//Creado A.A.G
	public List<String>armaCadenaConexion(){
		List<String> listDatos = new ArrayList<String>(); 
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("select * from Configura_Set where Indice In("+ConstantesSet.USUARIO_SERVIDOR+","+
		ConstantesSet.PASSWORD_SERVIDOR+","+ConstantesSet.IP_CARPETA_DESTINO+","+ConstantesSet.CARPETA_RAIZ+")");
			 listDatos= jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
					public String  mapRow(ResultSet rs, int idx) throws SQLException {
						return rs.getString("VALOR");
					}});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:EnvioTransferenciasDao, M:armaCadenaConexion");
		}
		return listDatos;
	}
	
	public Date obtenerFechaHoy(){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.obtenerFechaHoy();
	}
	
	/*Public Function FunSQLCombo364(ByVal pbBENormal As Boolean, pbNAFIN As Boolean) As ADODB.Recordset*/
	public  List<LlenaComboGralDto> llenaComboCveControl (){
		List<LlenaComboGralDto> listDatos = new ArrayList<LlenaComboGralDto>();
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			StringBuffer sql = new StringBuffer();
			sql.append("Select cve_control as ID, cve_control as DESCRIPCION From Seleccion_Automatica_Grupo sa");
			sql.append("\n  where Sa.Concepto Like '%'+(Select Clave_Usuario From Seg_Usuario Where Id_Usuario = ");
			sql.append(globalSingleton.getUsuarioLoginDto().getIdUsuario());
			sql.append(")+'%' ");
			System.out.println("llena combo cves de control "+sql);
			
			listDatos= jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setIdStr(rs.getString("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:EnvioTransferenciasDao, M:llenaComboCveControl");
		}
		return listDatos;
	}
	
	
	public List<LlenaComboGralDto> llenarBancoEmisor(boolean pbBENormal, boolean pbNafin){
		String sql="";
		List<LlenaComboGralDto> listDatos = new ArrayList<LlenaComboGralDto>(); 
		try{
	        if(pbBENormal)
	        {
	        	sql = "Select id_banco as ID, desc_banco as Descrip ";
        		sql += "\n	From cat_banco";
        		sql += "\n	 Where ";
        		sql += "\n	id_banco in (Select distinct id_banco from cat_cta_banco) and b_banca_elect  in ('E','A') ";
	        }
	         
	        if(pbBENormal && pbNafin)
	            sql += "\n	 UNION ";
	       
	        if(pbNafin)
	        {
	        	sql += "\n	 SELECT id_banco as ID, desc_banco as Descrip ";
	            sql += "\n	 From cat_banco ";
	            sql += "\n	 Where ";
	            sql += "\n	 id_banco = 135 "; //Considerar NAFIN
	            sql += "\n	 and b_banca_elect  in ('E','A') ";
	        }
	        
	        listDatos= jdbcTemplate.query(sql, new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("Descrip"));
					return cons;
				}});
	        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:EnvioTransferenciasDao, M:llenarBancoEmisor");
		}
		
		return listDatos;
	}
	
	
	public List<LlenaComboDivisaDto>llenarComboDivisa()
	{
		String sql="";
		List<LlenaComboDivisaDto> listDivisas= new ArrayList<LlenaComboDivisaDto>();
		try
		{
			sql+= " SELECT id_divisa as ID, desc_divisa as Descrip ";
			sql+= "   FROM cat_divisa ";
			sql+= "  WHERE clasificacion = 'D'";
			sql+= "  and id_divisa <> 'CNT' order by 2";
    		listDivisas= jdbcTemplate.query(sql, new RowMapper<LlenaComboDivisaDto>(){
				public LlenaComboDivisaDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboDivisaDto cons = new LlenaComboDivisaDto();
					cons.setIdDivisa(rs.getString("ID"));
					cons.setDescDivisa(rs.getString("Descrip"));
					return cons;
				}});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:BancaElectronica, C:EnvioTransferenciasDao, M:llenarComboDivisa");
		}
		return listDivisas;
	    
	}
	
	public List<LlenaComboGralDto>llenarComboGral(LlenaComboGralDto dto){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.llenarComboGral(dto);
	}
	
	public List<MovimientoDto> consultarPagosMassPayment(CriterioBusquedaDto dtoBus){
		StringBuffer sql = new StringBuffer();
		List<MovimientoDto> list = new ArrayList<MovimientoDto>();
		try{
			if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE"))
			{
				sql = new StringBuffer();
				sql.append("Select distinct m.fec_valor_original,m.id_tipo_operacion,b2.id_banco_city,b2.inst_finan,m.observacion,c.desc_sucursal as suc_origen,");
	            sql.append("\n ctas.sucursal as suc_destino,m.id_banco,");
	            sql.append("\n b.b_layout_comerica, ");
	            sql.append("\n m.no_docto,m.fec_operacion,o.desc_cve_operacion,  ");
	            sql.append("\n m.importe,m.id_divisa,m.id_chequera_benef,m.origen_mov, m.division,");
	            sql.append("\n '' as nombre_banco_benef,b2.desc_banco as desc_banco_benef,");
	            sql.append("\n m.beneficiario,m.concepto,m.no_folio_det,m.no_empresa, ");
	            sql.append("\n m.id_chequera,m.fec_valor,");
	            sql.append("\n m.id_banco_benef,");
	            sql.append("\n ctas.plaza as plaza_benef,");
	            sql.append("\n c.desc_plaza as plaza,");
	            sql.append("\n m.no_cliente ,");
	            sql.append("\n  b2.desc_banco_inbursa, ");
	            sql.append("\n  ctas.id_clabe as clabe_benef  ");
	            
	            sql.append("\n  ,ctas.aba, ctas.swift_code ");
	            sql.append("\n  ,ctas.aba_intermediario,ctas.swift_intermediario ");
	            sql.append("\n  ,ctas.aba_corresponsal, ctas.swift_corresponsal ");
	            sql.append("\n  ,b3.desc_banco as nom_banco_intermediario ");
	            sql.append("\n  ,b4.desc_banco  as nom_banco_corresponsal ");
	            sql.append("\n  ,b.valida_CLABE,e.id_contrato_mass ");
	            sql.append("\n  ,p.rfc as rfc_benef ");
	            sql.append("\n  ,m.no_factura,e.nom_empresa ");
	            sql.append("\n  ,p.equivale_persona ");
	            sql.append("\n  ,ctas.especiales,  ctas.complemento");
	            
	            sql.append("\n FROM movimiento m, cat_cve_operacion o, ");
	            sql.append("\n  cat_banco b, cat_cta_banco c , ctas_banco ctas, cat_banco b2,cat_banco b3, cat_banco b4 ");
	            sql.append("\n  ,empresa e,persona p");
	            
	            sql.append("\n  WHERE ");
	            sql.append("\n  convert(int,m.no_cliente) = p.no_persona ");
	            sql.append("\n  and p.id_tipo_persona in('P','K') ");
	            sql.append("\n  and m.no_empresa = e.no_empresa ");
	            sql.append("\n  and ctas.id_bank_true *= b3.id_banco ");
	            sql.append("\n  and ctas.id_bank_corresponding *= b4.id_banco ");
	            
	            sql.append("\n  and m.id_banco_benef *= b2.id_banco ");
	            sql.append("\n  and m.id_banco_benef = ctas.id_banco ");
	            sql.append("\n  and m.id_chequera_benef = ctas.id_chequera ");
	            sql.append("\n  and convert(int, m.no_cliente) = ctas.no_persona ");
	            sql.append("\n  and m.id_estatus_mov in('M','P') ");   //'M--> Mass Payment
	            sql.append("\n  and m.id_tipo_operacion in(3000,3200) ");
	            sql.append("\n  and m.id_forma_pago = 3 ");
	            sql.append("\n  AND m.id_cve_operacion = o.id_cve_operacion ");
	            sql.append("\n  AND m.id_banco = b.id_banco");
	            sql.append("\n  AND m.no_empresa = c.no_empresa AND m.id_banco = c.id_banco");
	            sql.append("\n  AND m.id_chequera = c.id_chequera AND m.id_tipo_movto = 'E'");
	            sql.append("\n  AND b.b_banca_elect  in ('A','E') and ");
	            sql.append("\n  ((m.id_banco_benef<1000 and m.id_divisa = 'MN') or m.id_divisa <> 'MN') ");
	            sql.append("\n  and m.id_banco = " +Utilerias.validarCadenaSQL(dtoBus.getIdBanco()));
	            
	            if(dtoBus.isMassPorCheq())
	                sql.append("\n  AND c.pago_mass = 'S' ");
	            
	            if(dtoBus.isChkTodasEmpresas())
	                sql.append("\n  AND m.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " +Utilerias.validarCadenaSQL(dtoBus.getIdUsuario())+ ")");
	            else
	                sql.append("\n  AND m.no_empresa = " +dtoBus.getIdEmpresa());

	            if(dtoBus.getIdDivisa()!= null && !dtoBus.getIdDivisa().equals(""))
	                sql.append("\n  AND m.id_divisa = '" +Utilerias.validarCadenaSQL(dtoBus.getIdDivisa())+ "' ");
	            
	            if(dtoBus.isChkSoloLocales())
	                sql.append("\n  AND e.b_local = 'S'");
	         
	            sql.append(agregarCriteriosBusqueda(dtoBus.getMontoIni(),dtoBus.getMontoFin(),dtoBus.getPlBancoReceptor(),
	            		dtoBus.getPsFechaValor(),dtoBus.getPsFechaValorOrig(),dtoBus.getPsDivisa(),dtoBus.getPsDivision()));
	            
	            if(dtoBus.getOptTipoEnvio()!=null && dtoBus.getOptTipoEnvio().trim().equals("Mismo"))
	                    sql.append("\n  and m.id_banco = m.id_banco_benef");
	            else
	                    sql.append("\n  and m.id_banco <> m.id_banco_benef");
	            
	            if(dtoBus.getOptTipoEnvio()!=null && dtoBus.getOptTipoEnvio().trim().equals(""))
	            {
	            	//'Considerar solo los interbancarios que tengan fecha mayor a hoy de origen banamex
	                sql.append("\n UNION ");
	                sql.append("\n SELECT distinct m.fec_valor_original,m.id_tipo_operacion," +
	                		"b2.id_banco_city,b2.inst_finan,m.observacion,c.desc_sucursal as suc_origen,");
	                sql.append("\n ctas.sucursal as suc_destino,m.id_banco,");
	                sql.append("\n b.b_layout_comerica, ");
	                sql.append("\n m.no_docto,m.fec_operacion,o.desc_cve_operacion,  ");
	                sql.append("\n m.importe,m.id_divisa,m.id_chequera_benef,m.origen_mov, m.division,");
	                sql.append("\n '' as nombre_banco_benef,b2.desc_banco as desc_banco_benef,");
	                sql.append("\n m.beneficiario,m.concepto,m.no_folio_det,m.no_empresa, ");
	                sql.append("\n m.id_chequera,m.fec_valor,");
	                sql.append("\n m.id_banco_benef,");
	                sql.append("\n ctas.plaza as plaza_benef,");
	                sql.append("\n c.desc_plaza as plaza,");
	                sql.append("\n m.no_cliente ,");
	                sql.append("\n  b2.desc_banco_inbursa, ");
	                sql.append("\n  ctas.id_clabe as clabe_benef  ");
	                
	                sql.append("\n  ,ctas.aba, ctas.swift_code ");
	                sql.append("\n  ,ctas.aba_intermediario,ctas.swift_intermediario ");
	                sql.append("\n  ,ctas.aba_corresponsal, ctas.swift_corresponsal ");
	                sql.append("\n  ,b3.desc_banco as nom_banco_intermediario ");
	                sql.append("\n  ,b4.desc_banco  as nom_banco_corresponsal ");
	                sql.append("\n  ,b.valida_CLABE,e.id_contrato_mass ");
	                sql.append("\n  ,p.rfc as rfc_benef ");
	                sql.append("\n  ,m.no_factura,e.nom_empresa ");
	                sql.append("\n  ,p.equivale_persona ");
	                sql.append("\n  ,ctas.especiales,  ctas.complemento");
	                
	                sql.append("\n FROM movimiento m, cat_cve_operacion o, ");
	                sql.append("\n  cat_banco b, cat_cta_banco c , ctas_banco ctas, cat_banco b2,cat_banco b3, cat_banco b4 ");
	                sql.append("\n  ,empresa e,persona p ");
	                
	                sql.append("\n  WHERE ");
	                sql.append("\n  convert(int,m.no_cliente) = p.no_persona ");
	                sql.append("\n  and p.id_tipo_persona in ('P','K') ");
	                sql.append("\n  and m.no_empresa = e.no_empresa ");
	                sql.append("\n  and ctas.id_bank_true *= b3.id_banco ");
	                sql.append("\n  and ctas.id_bank_corresponding *= b4.id_banco ");
	                
	                sql.append("\n  and m.id_banco_benef *= b2.id_banco ");
	                sql.append("\n  and m.id_banco_benef = ctas.id_banco ");
	                sql.append("\n  and m.id_chequera_benef = ctas.id_chequera ");
	                sql.append("\n  and convert(int, m.no_cliente) = ctas.no_persona ");
	                sql.append("\n  and m.id_estatus_mov = 'P' ");
	                sql.append("\n  and m.id_tipo_operacion = 3200 ");
	                sql.append("\n  and m.id_forma_pago = 3 ");
	                sql.append("\n  AND m.id_cve_operacion = o.id_cve_operacion ");
	                sql.append("\n  AND m.id_banco = b.id_banco");
	                sql.append("\n  AND m.no_empresa = c.no_empresa AND m.id_banco = c.id_banco");
	                sql.append("\n  AND m.id_chequera = c.id_chequera ");
	                sql.append("\n  AND c.pago_mass = 'S' ");
	                sql.append("\n  AND m.id_tipo_movto = 'E'");
	                sql.append("\n  AND b.b_banca_elect  in ('A','E') and ");
	                sql.append("\n  ((m.id_banco_benef<1000 and m.id_divisa = 'MN') or m.id_divisa <> 'MN') ");
	                sql.append("\n  and m.id_banco = " +dtoBus.getIdBanco());
	                
	                if(dtoBus.isChkTodasEmpresas())
	                    sql.append("\n  AND m.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " +dtoBus.getIdUsuario()+ ")");
	                else
	                    sql.append("\n  AND m.no_empresa = " + dtoBus.getIdEmpresa());

	                if(dtoBus.getIdDivisa()!=null && !dtoBus.getIdDivisa().trim().equals(""))
	                    sql.append("\n  AND m.id_divisa = '" +dtoBus.getIdDivisa()+ "' ");
	                
	              
	                if(dtoBus.isChkSoloLocales())
	                    sql.append("\n  AND e.b_local = 'S'");
	                
	                sql.append(agregarCriteriosBusqueda(dtoBus.getMontoIni(),dtoBus.getMontoFin(),dtoBus.getPlBancoReceptor(),
		            		dtoBus.getPsFechaValor(),dtoBus.getPsFechaValorOrig(),dtoBus.getPsDivisa(),dtoBus.getPsDivision()));
	                                                   
	                sql.append("\n  and m.id_banco <> m.id_banco_benef ");
	                
	                if(!dtoBus.isMassPorCheq())
	                    sql.append("\n  and m.fec_valor_original > (Select fec_hoy from fechas) ");
	            	
	            }
	            
	            sql.append("\n  ORDER BY m.importe ");
	            if(gsDBM.equals("SYBASE"))
	              sql.append("\n AT ISOLATION READ UNCOMMITTED ");
				
			}else if(gsDBM.equals("POSTGRESQL")|| gsDBM.equals("DB2")){
				sql = new StringBuffer();
				sql.append("Select distinct m.fec_valor_original,m.id_tipo_operacion,b2.id_banco_city,b2.inst_finan,m.observacion,c.desc_sucursal as suc_origen,");
	            sql.append("\n ctas.sucursal as suc_destino,m.id_banco,");
	            sql.append("\n b.b_layout_comerica, ");
	            sql.append("\n m.no_docto,m.fec_operacion,o.desc_cve_operacion,  ");
	            sql.append("\n m.importe,m.id_divisa,m.id_chequera_benef,m.origen_mov, m.division,");
	            sql.append("\n '' || '' as nombre_banco_benef,b2.desc_banco as desc_banco_benef,");
	            sql.append("\n m.beneficiario,m.concepto,m.no_folio_det,m.no_empresa, ");
	            sql.append("\n m.id_chequera,m.fec_valor,");
	            sql.append("\n m.id_banco_benef,");
	            sql.append("\n ctas.plaza as plaza_benef,");
	            sql.append("\n c.desc_plaza as plaza,");
	            sql.append("\n m.no_cliente ,");
	            sql.append("\n  b2.desc_banco_inbursa, ");
	            sql.append("\n  ctas.id_clabe as clabe_benef,  ");
	            sql.append("\n  ctas.aba, ctas.swift_code ");
	            
	            sql.append("\n  ,ctas.aba_intermediario,ctas.Swift_intermediario ");
	            sql.append("\n  ,ctas.Aba_corresponsal, ctas.Swift_corresponsal ");
	            sql.append("\n  ,b3.desc_banco as nom_banco_intermediario ");
	            sql.append("\n  ,b4.desc_banco  as nom_banco_corresponsal ");
	            sql.append("\n  ,b.valida_CLABE, e.id_contrato_mass ");
	            sql.append("\n  ,p.rfc as rfc_benef ");
	            sql.append("\n  ,m.no_factura,e.nom_empresa ");
	            sql.append("\n  ,ctas.especiales,  ctas.complemento");
	            
	            sql.append("\n FROM movimiento m ");
	            
	            if(gsDBM.equals("DB2"))
	               sql.append("\n   LEFT JOIN persona p ON(cast(m.no_cliente as integer) = p.no_persona and p.id_tipo_persona = 'P' ) ");
	            else
	               sql.append("\n   LEFT JOIN persona p ON(convert(numeric,m.no_cliente,'999999999') = p.no_persona and p.id_tipo_persona = 'P' ) ");
	            
	            
	            sql.append("\n   LEFT JOIN cat_banco b2 ON(m.id_banco_benef = b2.id_banco),");
	            sql.append("\n   cat_cve_operacion o,cat_banco b,cat_cta_banco c,");
	            sql.append("\n   ctas_banco ctas LEFT JOIN cat_banco b3 ON(ctas.id_bank_true = b3.id_banco) ");
	            sql.append("\n   LEFT JOIN cat_banco b4 ON(ctas.id_bank_corresponding = b4.id_banco) ");
	            sql.append("\n ,empresa e ");
	            
	            sql.append("\n WHERE ");
	            sql.append("\n m.no_empresa = e.no_empresa ");
	            sql.append("\n and m.no_cliente = ctas.no_persona ");
	            sql.append("\n And m.id_banco_benef = ctas.id_banco ");
	            sql.append("\n and m.id_chequera_benef = ctas.id_chequera ");
	            sql.append("\n AND m.id_forma_pago = 3 ");
	            sql.append("\n AND m.id_estatus_mov = 'P' ");
	            sql.append("\n AND m.id_cve_operacion = o.id_cve_operacion ");
	            sql.append("\n AND m.id_banco = b.id_banco ");
	            sql.append("\n AND m.no_empresa = c.no_empresa ");
	            sql.append("\n AND m.id_banco = c.id_banco ");
	            sql.append("\n AND m.id_chequera = c.id_chequera ");
	            sql.append("\n AND m.id_tipo_movto = 'E' ");
	            sql.append("\n AND b.b_banca_elect  in ('A','E') ");
	            sql.append("\n and ((m.id_banco_benef<1000 and m.id_divisa = 'MN') or ");
	            sql.append("\n    m.id_divisa <> 'MN') ");
	            sql.append("\n  and m.id_banco = " + dtoBus.getIdBanco());
	           
	            if(dtoBus.isChkTodasEmpresas())
	                sql.append("\n  AND m.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " +dtoBus.getIdUsuario()+ ")");
	            else
	                sql.append("\n  AND m.no_empresa = " + dtoBus.getIdEmpresa());

	            if(dtoBus.getIdDivisa()!=null && !dtoBus.getIdDivisa().trim().equals(""))
	                sql.append("\n  AND m.id_divisa = '" +dtoBus.getIdDivisa()+ "' ");
	            
	            if(dtoBus.isChkSoloLocales())
	                sql.append("\n  AND e.b_local = 'S'");
	                      
	            sql.append(agregarCriteriosBusqueda(dtoBus.getMontoIni(),dtoBus.getMontoFin(),dtoBus.getPlBancoReceptor(),
	            		dtoBus.getPsFechaValor(),dtoBus.getPsFechaValorOrig(),dtoBus.getPsDivisa(),dtoBus.getPsDivision()));
	            
	            if(dtoBus.getOptTipoEnvio()!=null && dtoBus.getOptTipoEnvio().trim().equals("Mismo"))
	                    sql.append("\n  and m.id_banco = m.id_banco_benef");
	            else
	                    sql.append("\n  and m.id_banco <> m.id_banco_benef");

	            sql.append("\n  AND m.origen_mov <> 'FIL' ");
	            sql.append("\n  AND no_cliente not in(" +consultarConfiguraSet(206)+ ") ");
	            sql.append("\n  ORDER BY importe ");
			}
			
		     list= jdbcTemplate.query(sql.toString(), new RowMapper<MovimientoDto>(){
					public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
						MovimientoDto cons = new MovimientoDto();
						cons.setFecValorOriginal(rs.getDate("fec_valor_original"));
						cons.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
						cons.setIdBancoCityStr(rs.getString("id_banco_city"));
						cons.setInstFinan(rs.getString("inst_finan"));
						cons.setObservacion(rs.getString("observacion"));
						cons.setSucOrigen(rs.getString("suc_origen"));
						cons.setSucDestino(rs.getString("suc_destino"));
						cons.setIdBanco(rs.getInt("id_banco"));
						cons.setBLayoutComerica(rs.getString("b_layout_comerica"));
						cons.setNoDocto(rs.getString("no_docto"));
						cons.setFecOperacion(rs.getDate("fec_operacion"));
						cons.setDescCveOperacion(rs.getString("desc_cve_operacion"));
						cons.setImporte(rs.getDouble("importe")); 
						cons.setIdDivisa(rs.getString("id_divisa"));
						cons.setIdChequeraBenef(rs.getString("id_chequera_benef"));
						cons.setOrigenMov(rs.getString("origen_mov"));
						cons.setDivision(rs.getString("division"));
						cons.setNombreBancoBenef(rs.getString("nombre_banco_benef"));
						cons.setDescBancoBenef(rs.getString("desc_banco_benef"));
						cons.setBeneficiario(rs.getString("beneficiario"));
						cons.setConcepto(rs.getString("concepto"));
						cons.setNoFolioDet(rs.getInt("no_folio_det")); 
						cons.setNoEmpresa(rs.getInt("no_empresa"));
						cons.setIdChequera(rs.getString("id_chequera"));
						cons.setFecValor(rs.getDate("fec_valor")); 
						cons.setIdBancoBenef(rs.getInt("id_banco_benef"));
						cons.setPlazaBenef(rs.getString("plaza_benef"));
						cons.setPlaza(rs.getInt("plaza"));
						cons.setNoCliente(rs.getString("no_cliente"));
						cons.setDescBancoInbursa(rs.getString("desc_banco_inbursa"));
						cons.setClabeBenef(rs.getString("clabe_benef"));
						cons.setAba(rs.getString("aba"));
						cons.setSwiftCode(rs.getString("swift_code"));
						cons.setAbaIntermediario(rs.getString("aba_intermediario"));
						cons.setSwiftIntermediario(rs.getString("swift_intermediario"));
						cons.setAbaCorresponsal(rs.getString("aba_corresponsal"));
						cons.setSwiftCorresponsal(rs.getString("swift_corresponsal"));
						cons.setNomBancoIntermediario(rs.getString("nom_banco_intermediario"));
						cons.setNomBancoCorresponsal(rs.getString("nom_banco_corresponsal"));
						cons.setValidaCLABE(rs.getString("valida_clabe"));
						cons.setIdContratoMass(rs.getInt("id_contrato_mass"));
						cons.setRfcBenef(rs.getString("rfc_benef"));
						cons.setNoFactura(rs.getString("no_factura"));
						cons.setNomEmpresa(rs.getString("nom_empresa"));
						cons.setEspeciales(rs.getString("especiales")); 
						cons.setEquivalePersona(rs.getString("equivale_persona"));
						cons.setComplemento(rs.getString("complemento"));
						return cons;
					}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BancaElectronica, C:EnvioTransferenciasDao, M:consultarPagosMassPayment");
		}
		return list;
	}
	

	/*Public Function FunSQLSelect858(ByVal plNo_empresa As Long, ByVal pbOptMismo As Boolean, _
                                ByVal piNoUsuario As Integer, ByVal ps_divisa As String, _
                                ByVal pbSPEUA As Boolean, ByVal piBanco As Integer, _
                                ByVal pbConvenioCIE As Boolean, ByVal pbSoloComericaACH As Boolean, _
                                ByVal pdMontoIni As Double, ByVal pdMontoFin As Double, _
                                ByVal plBancoReceptor As Long, ByVal psfechaValor As String, _
                                ByVal psFechaValorOrig As String, ByVal psDivisa As String, _
                                ByVal psDivision As String, ByVal sCasaCambio As String, _
                                ByVal pbWorldLink As Boolean, ByVal pbinternacional As Boolean) As ADODB.Recordset
                                
                                */
	/**
	 *  los campos de los catalogos: sp_help cat_usuario_bital u, sp_help cat_servicio_bital no se muestran ya que no existen
	 *  con la bandera pbH2HBital
	 */
	public List<MovimientoDto> consultarPagos(CriterioBusquedaDto dtoBus){
		List<MovimientoDto> list = new ArrayList<MovimientoDto>();
		StringBuffer sql = new StringBuffer();
		pbH2HBital = false;
		try{
		   // if(consultarConfiguraSet(232).trim().equals("SI") && dtoBus.getIdBanco() == 21) 
		    //	pbH2HBital = true;
		    //'********************************* ORIGEN MOV <> FIL
	    	sql = new StringBuffer();
	    	sql.append("\n	Select Po_Headers,max(Fec_Valor_Original) as Fec_Valor_Original ,Id_Tipo_Operacion,Id_Banco_City,Inst_Finan,Observacion,Suc_Origen,Suc_Destino, ");
			sql.append("\n	  Id_Banco,B_Layout_Comerica,No_Docto,max(Fec_Operacion) as Fec_Operacion,Desc_Cve_Operacion,sum (Importe) as importe,Id_Divisa,Id_Chequera_Benef, ");
			sql.append("\n	  Origen_Mov,max(Division) as Division,Nombre_Banco_Benef,Desc_Banco_Benef,Beneficiario,Concepto,No_Folio_Det,No_Empresa, ");
			sql.append("\n	  Id_Chequera,max(Fec_Valor) as Fec_Valor,Id_Banco_Benef,Plaza_Benef,Plaza,No_Cliente,Desc_Banco_Inbursa,Clabe_Benef,Aba, ");
			sql.append("\n	  Swift_Code,Aba_Intermediario,Swift_Intermediario,Aba_Corresponsal,Swift_Corresponsal,Nom_Banco_Intermediario, ");
			sql.append("\n	  Nom_Banco_Corresponsal,Valida_Clabe,Id_Contrato_Mass,Id_Contrato_Wlink,Rfc_Benef,No_Factura,Nom_Empresa, ");
			sql.append("\n	  Existe_Chequera_Prov,Rfc,Especiales,Equivale_Persona,Complemento,Tipo_Envio_Layout,Divisa_Chequera,direccion_benef,pais_benef, ");
			sql.append("\n	  clave,NO_FOLIO_MOV from( ");

			sql.append("\n	Select m.po_headers,M.fec_propuesta as fec_valor_original, M.Id_Tipo_Operacion,B2.Id_Banco_City,B2.Inst_Finan,Coalesce(M.Observacion,'') As Observacion,C.Desc_Sucursal As Suc_Origen, ");
			sql.append("\n	Coalesce(Ctas.Sucursal, 0) As Suc_Destino, M.Id_Banco,Coalesce (B1.B_Layout_Comerica, '') As B_Layout_Comerica, ");
			sql.append("\n	Po_Headers As No_Docto, ");
			sql.append("\n	M.fec_propuesta as Fec_Operacion,O.Desc_Cve_Operacion, ");
			sql.append("\n	CASE WHEN coalesce(b1.transfer_casa_cambio, 'N') = 'S' and m.origen_mov='CVT' "); 
			sql.append("\n		THEN m.importe ");
			sql.append("\n		Else M.Importe End As Importe, "); 
			sql.append("\n	CASE WHEN coalesce(b1.transfer_casa_cambio, 'N') = 'S' and m.origen_mov='CVT' "); 
			sql.append("\n		 Then M.Id_Divisa_Original ");
			sql.append("\n       Else M.Id_Divisa End As Id_Divisa, "); 
			sql.append("\n 	M.Id_Chequera_Benef,M.Origen_Mov, M.Division, ");
			sql.append("\n  B2.Desc_Banco As Nombre_Banco_Benef,B2.Desc_Banco As Desc_Banco_Benef, ");
			sql.append("\n  Case When ");
			sql.append("\n		 ( SELECT coalesce(b.transfer_casa_cambio, 'N') "); 
			sql.append("\n		   FROM cat_banco b ");
			sql.append("\n		   WHERE b.id_banco = m.id_banco ) = 'S' and m.origen_mov='CVT' "); 
			sql.append("\n  THEN ( SELECT coalesce(pp.razon_social, '') ");
			sql.append("\n	      FROM persona pp ");
			sql.append("\n	      WHERE pp.no_persona = m.no_cliente "); 
			sql.append("\n	           And Pp.Id_Tipo_Persona = 'P' ) ");
			sql.append("\n	     Else M.Beneficiario End As Beneficiario, "); 
			sql.append("\n	'pago' as concepto,m.po_headers as no_folio_det,m.no_empresa, "); 
			sql.append("\n 	m.id_chequera,m.fec_propuesta as fec_valor,m.id_banco_benef, ");
			sql.append("\n 	Coalesce(Ctas.Plaza, 0) As Plaza_Benef, C.Desc_Plaza As Plaza, ");
			sql.append("\n 	M.No_Cliente, B2.Desc_Banco_Inbursa, Ctas.Id_Clabe As Clabe_Benef ");
			sql.append("\n 	,coalesce(ctas.aba, '') as aba, coalesce(ctas.swift_code, '') as swift_code "); 
			sql.append("\n 	,coalesce(ctas.aba_intermediario, '') as aba_intermediario, coalesce(ctas.swift_intermediario, '') as swift_intermediario "); 
			sql.append("\n 	,coalesce(ctas.aba_corresponsal, '') as aba_corresponsal, coalesce(ctas.swift_corresponsal, '') as swift_corresponsal ");
			sql.append("\n 	, ' ' As Nom_Banco_Intermediario, ' ' As Nom_Banco_Corresponsal ");
			sql.append("\n 	,B1.Valida_Clabe,E.Id_Contrato_Mass,E.Id_Contrato_Wlink, P.Rfc As Rfc_Benef "); 
			sql.append("\n  ,M.po_headers as No_Factura,E.Nom_Empresa, 'S' As Existe_Chequera_Prov, Pp.Rfc ");
			sql.append("\n 	,Ctas.Especiales, P.Equivale_Persona, Ctas.Complemento, c.id_divisa As Divisa_Chequera, ");
			sql.append("\n 	D.Ciudad +  ', ' +  D.Id_Estado As Direccion_Benef , 'MX' As Pais_Benef, M.Cve_Control As Clave, ");
			sql.append("\n 	m.po_headers as no_folio_mov, '' As Tipo_Envio_Layout ");



			sql.append("\n 	From Movimiento M Join Cat_Banco B2 On ( M.Id_Banco_Benef = B2.Id_Banco ) "); 
			sql.append("\n 	                  Join Cat_Banco B1 On ( M.Id_Banco = B1.Id_Banco ) ");
			sql.append("\n 	                  Join Cat_Cta_Banco C On (M.Id_Chequera = C.Id_Chequera And ");
			sql.append("\n 	                                          M.Id_Banco = C.Id_Banco ) ");
			//sql.append("\n 	                                          M.No_Empresa = C.No_Empresa) ");
			sql.append("\n 	                  Join Ctas_Banco Ctas On (M.Id_Banco_Benef = Ctas.Id_Banco And ");
			sql.append("\n 	                                          M.Id_Chequera_Benef = Ctas.Id_Chequera And ");
			sql.append("\n 	                                          M.No_Cliente = No_Persona) ");
			sql.append("\n 	                  Join Cat_Cve_Operacion O On (M.Id_Cve_Operacion = O.Id_Cve_Operacion) ");
			sql.append("\n 	                  Join Empresa E On (M.No_Empresa = E.No_Empresa) ");
			sql.append("\n 	                  Join Persona P On (Cast(M.No_Cliente As Integer) = P.No_Persona ) ");
			sql.append("\n 	                  Join Persona Pp On (M.No_Empresa = Pp.No_Empresa ");
			sql.append("\n 	                                      And M.No_Empresa = Pp.No_Persona) ");
			sql.append("\n 	                  Left Outer Join Direccion D On (Cast(M.No_Cliente As Integer) = D.No_Persona ");   
			sql.append("\n and d.id_tipo_direccion = 'OFNA' and d.no_direccion = 1 ");
			sql.append("\n and d.id_tipo_persona = 'P' and d.no_empresa = 552 )");
			
			sql.append("\n Where ");

			sql.append("\n m.id_banco = ");
			sql.append(Utilerias.validarCadenaSQL(dtoBus.getIdBanco()));
			sql.append("\n  AND m.no_empresa in (Select No_Empresa From Grupo_Empresa Where Id_Grupo_Flujo = ");
			sql.append(Utilerias.validarCadenaSQL(dtoBus.getIdEmpresa())+")");
			sql.append("\n  And M.Id_Tipo_Movto = 'E'");
			sql.append("\n  And M.Id_Forma_Pago = 3");
			sql.append("\n  AND m.no_cliente not in (" +consultarConfiguraSet(206)+")");
			sql.append("\n  And M.Origen_Mov <> 'FIL'");
			sql.append("\n  And M.Id_Estatus_Mov = 'N'");
			if (!dtoBus.getValidaCampo().equals("0")) {
        		sql.append("\n  and m.cve_control = '");
                sql.append(dtoBus.getValidaCampo());
                sql.append("'");
			}
			sql.append("\n  And (M.Po_Headers Is Not Null  Or M.Po_Headers <> '')");
            sql.append("\n  and p.id_tipo_persona in('P','K') ");
            sql.append("\n  AND b1.b_banca_elect  in ('A','E')  ");
            sql.append("\n  and pp.id_tipo_persona = 'E'");
           // sql.append("\n  and d.id_tipo_direccion = 'OFNA' ");
            //sql.append("\n  and d.no_direccion = 1 ");
           //sql.append("\n  and d.id_tipo_persona = 'P' ");
            //sql.append("\n  and d.no_empresa = 552 ");

            if(dtoBus.getIdDivisa()!=null && !dtoBus.getIdDivisa().equals("")){
                sql.append("\n  AND CASE WHEN coalesce(b1.transfer_casa_cambio, 'N') = 'S' and m.origen_mov='CVT'");
                sql.append("\n           THEN m.id_divisa_original ");
                sql.append("\n           ELSE m.id_divisa END = '" +Utilerias.validarCadenaSQL(dtoBus.getIdDivisa())+ "' ");
            }
            if(dtoBus.isChkConvenioCie())
                sql.append("\n  AND substring(m.id_chequera_benef,1,4) = 'CONV'");
            else if (dtoBus.getIdBanco()==12 && (dtoBus.getOptTipoEnvio()!=null && dtoBus.getOptTipoEnvio().trim().equals("Mismo")))
                sql.append("\n  AND substring(m.id_chequera_benef,1,4) <> 'CONV'");
            
            if(dtoBus.getOptComerica()!=null && dtoBus.getOptComerica().trim().equals("ACH"))
                sql.append("\n  AND m.id_servicio_be = 'ACH' ");
            
            if(dtoBus.isChkSoloLocales())
                sql.append("\n  AND e.b_local = 'S' ");
            
            sql.append(agregarCriteriosBusqueda(dtoBus.getMontoIni(),dtoBus.getMontoFin(),dtoBus.getPlBancoReceptor(),Utilerias.validarCadenaSQL(dtoBus.getPsFechaValor())
            		,dtoBus.getPsFechaValorOrig(),dtoBus.getIdDivisa(),dtoBus.getPsDivision()));
            
            if(dtoBus.getOptTipoEnvio()!=null && dtoBus.getOptTipoEnvio().trim().equals("SPEUA")){
            	sql.append("\n  and m.id_banco <> m.id_banco_benef ");
                sql.append("\n  and m.importe >= 50000 ");
            }else if (dtoBus.getOptTipoEnvio()!=null && dtoBus.getOptTipoEnvio().trim().equals("Mismo"))
                    sql.append("\n  and m.id_banco = m.id_banco_benef");
            else if (dtoBus.getOptTipoEnvio()!=null && dtoBus.getOptTipoEnvio().trim().equals("Internacional"))
               sql.append("\n AND m.id_banco <> m.id_banco_benef");
            else{
               if(dtoBus.getIdBanco()!=12 && (dtoBus.getIdDivisa()!=null && dtoBus.getIdDivisa().trim().equals(consultarConfiguraSet(771)))){
                    sql.append("\n AND ((b1.nac_ext = 'N' and b2.nac_ext = 'N')");
                    sql.append("\n or (b2.nac_ext = 'N' and b1.nac_ext = 'N'))");
               }
                sql.append("\n AND m.id_banco <> m.id_banco_benef");
            }
            
            sql.append("\n ) X");
            sql.append("\n Group By po_headers,Id_Tipo_Operacion,Id_Banco_City,Inst_Finan,Observacion,Suc_Origen,Suc_Destino,");
    		sql.append("\n Id_Banco,B_Layout_Comerica,No_Docto,Desc_Cve_Operacion,Id_Divisa,Id_Chequera_Benef,");
			sql.append("\n Origen_Mov,Nombre_Banco_Benef,Desc_Banco_Benef,Beneficiario,Concepto,No_Folio_Det,No_Empresa,");
			sql.append("\n Id_Chequera,Id_Banco_Benef,Plaza_Benef,Plaza,No_Cliente,Desc_Banco_Inbursa,Clabe_Benef,Aba,");
			sql.append("\n Swift_Code,Aba_Intermediario,Swift_Intermediario,Aba_Corresponsal,Swift_Corresponsal,Nom_Banco_Intermediario,");
			sql.append("\n Nom_Banco_Corresponsal,Valida_Clabe,Id_Contrato_Mass,Id_Contrato_Wlink,Rfc_Benef,No_Factura,Nom_Empresa,");
			sql.append("\n Existe_Chequera_Prov,Rfc,Especiales,Equivale_Persona,Complemento,Tipo_Envio_Layout,Divisa_Chequera,direccion_benef,pais_benef,");
			sql.append("\n clave,NO_FOLIO_MOV ");

            
            
            
            
		    System.out.println("\n consulta"+sql);
		    
		    	list= jdbcTemplate.query(sql.toString(), new RowMapper <MovimientoDto>(){
					public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
						MovimientoDto cons = new MovimientoDto();
						cons.setFecValorOriginal(rs.getDate("fec_valor_original"));
						cons.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
						cons.setIdBancoCityStr(rs.getString("id_banco_city"));
						cons.setInstFinan(rs.getString("inst_finan"));
						cons.setObservacion(rs.getString("observacion"));
						cons.setSucOrigen(rs.getString("suc_origen"));
						cons.setSucDestino(rs.getString("suc_destino"));
						cons.setIdBanco(rs.getInt("id_banco"));
						cons.setBLayoutComerica(rs.getString("b_layout_comerica"));
						cons.setNoDocto(rs.getString("no_docto"));
						cons.setFecOperacion(rs.getDate("fec_operacion"));
						cons.setDescCveOperacion(rs.getString("desc_cve_operacion"));
						cons.setImporte(rs.getDouble("importe")); 
						cons.setIdDivisa(rs.getString("id_divisa"));
						cons.setIdChequeraBenef(rs.getString("id_chequera_benef"));
						cons.setOrigenMov(rs.getString("origen_mov"));
						cons.setDivision(rs.getString("division"));
						cons.setNombreBancoBenef(rs.getString("nombre_banco_benef"));
						cons.setDescBancoBenef(rs.getString("desc_banco_benef"));
						cons.setBeneficiario(rs.getString("beneficiario"));
						cons.setConcepto(rs.getString("concepto"));
						cons.setPoHeaders(rs.getString("no_folio_det")); 
						cons.setNoEmpresa(rs.getInt("no_empresa"));
						cons.setIdChequera(rs.getString("id_chequera"));
						cons.setFecValor(rs.getDate("fec_valor")); 
						cons.setIdBancoBenef(rs.getInt("id_banco_benef"));
						cons.setPlazaBenef(rs.getString("plaza_benef"));
						cons.setPlaza(rs.getInt("plaza"));
						cons.setNoCliente(rs.getString("no_cliente"));
						cons.setDescBancoInbursa(rs.getString("desc_banco_inbursa"));
						cons.setClabeBenef(rs.getString("clabe_benef"));
						
						cons.setAba(rs.getString("aba"));
						cons.setSwiftCode(rs.getString("swift_code"));
						cons.setAbaIntermediario(rs.getString("aba_intermediario"));
						cons.setSwiftIntermediario(rs.getString("swift_intermediario"));
						cons.setAbaCorresponsal(rs.getString("aba_corresponsal"));
						cons.setSwiftCorresponsal(rs.getString("swift_corresponsal"));
						cons.setNomBancoIntermediario(rs.getString("nom_banco_intermediario"));
						cons.setNomBancoCorresponsal(rs.getString("nom_banco_corresponsal"));
						cons.setValidaCLABE(rs.getString("valida_clabe"));
						cons.setIdContratoMass(rs.getInt("id_contrato_mass"));
						cons.setRfcBenef(rs.getString("rfc_benef"));
						cons.setNoFactura(rs.getString("no_factura"));
						cons.setNomEmpresa(rs.getString("nom_empresa"));
						cons.setEspeciales(rs.getString("especiales")); 
						cons.setEquivalePersona(rs.getString("equivale_persona"));
						cons.setComplemento(rs.getString("complemento"));
						cons.setIdContratoWlink(rs.getString("id_contrato_wlink"));
						cons.setExisteChequeraProv(rs.getString("existe_chequera_prov"));
						cons.setRfc(rs.getString("rfc"));
						cons.setTipoEnvioLayout(rs.getInt("tipo_envio_layout"));
						cons.setDivisaChequera(rs.getString("divisa_chequera"));
						cons.setDireccionBenef(rs.getString("Direccion_Benef"));
						System.out.println(rs.getString("Direccion_Benef"));
						cons.setPaisBenef(rs.getString("pais_benef"));
						cons.setClave(rs.getString("clave"));
						cons.setIdChequeraBenefReal(obtenerChequeraBenefOficial(rs.getInt("id_banco_benef"), rs.getString("suc_destino"), 
								rs.getString("plaza_benef"), rs.getString("id_chequera_benef"), rs.getInt("id_banco")));
						cons.setHoraRecibo(funciones.obtenerHoraActual(false).substring(0, 5));
						//cons.setDescUsuarioBital(pbH2HBital ? rs.getString("desc_usuario_bital") : "");
						//cons.setDescServicioBital(pbH2HBital ? rs.getString("desc_servicio_bital") : "");
						cons.setPoHeaders(rs.getString("no_folio_mov"));
						return cons;
					}});
		     
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:BancaElectronica, C:EnvioTransferenciasDao, M:consultarPagos");
		}
		return list;
	}
	
	/*Public Function FunSQLSelectNAFIN(ByVal plNo_empresa As Long, ByVal piTodas As Integer, _
    ByVal plNoUsuario As Long, ByVal ps_divisa As String, _
    ByVal pdMontoIni As Double, ByVal pdMontoFin As Double, _
    ByVal plBancoReceptor As Long, ByVal psfechaValor As String, _
    ByVal psFechaValorOrig As String, ByVal psDivisa As String, _
    ByVal psDivision As String) As ADODB.Recordset*/
	public List<MovimientoDto> consultarPagosNafin(CriterioBusquedaDto dtoBus){
		StringBuffer sql = new StringBuffer();
		List<MovimientoDto> list = new ArrayList<MovimientoDto>();
		try{
			sql.append(" SELECT distinct '' as inst_finan,m.observacion,'' as suc_origen,");
		    sql.append("\n '' as suc_destino,m.id_banco, ");
		    sql.append("\n '' as b_layout_comerica, ");
		    sql.append("\n m.no_docto,m.fec_operacion,o.desc_cve_operacion,");
		    sql.append("\n m.importe,m.id_divisa,m.id_chequera_benef,m.origen_mov, m.division,");
		    sql.append("\n '' as nombre_banco_benef,'' as desc_banco_benef,");
		    sql.append("\n m.beneficiario,m.concepto,m.no_folio_det,m.no_empresa,");
		    sql.append("\n m.id_chequera,m.fec_valor,m.id_banco_benef,");
		    sql.append("\n '' as plaza_benef,");
		    sql.append("\n '' as plaza,m.no_cliente , ");
		    sql.append("\n '' desc_banco_inbursa, ");
		    sql.append("\n '' as clabe_benef ");
		    sql.append("\n ,'' as aba,'' as swift_code ");
		    sql.append("\n ,'' as aba_intermediario,'' as swift_intermediario ");
		    sql.append("\n ,'' as aba_corresponsal, '' as swift_corresponsal ");
		    sql.append("\n ,'' as nom_banco_intermediario ");
		    sql.append("\n ,'' as nom_banco_corresponsal");
		    sql.append("\n ,'' as valida_CLABE,'' as id_contrato_mass,");
		    sql.append("\n  coalesce(referencia_ban,'') + m.no_factura as no_factura , p.equivale_persona");
		    sql.append("\n ,'' as id_banco_city ");
		    sql.append("\n ,/*m.no_factura,*/'' as nom_empresa," +
		    		"case when coalesce(p.rfc, '') = '' then coalesce(m.rfc, '') else p.rfc  end as rfc_benef, '' as RFC" +
		    		", '' AS especiales, '' AS complemento, '' as tipo_envio_layout, '' as divisa_chequera, '' as pais_benef" +
		    		", '' as direccion_benef, '' as clave ");

		    sql.append("\n FROM movimiento m, cat_cve_operacion o, persona p ");
		    sql.append("\n WHERE m.no_cliente = p.no_persona ");
		    sql.append("\n and p.id_tipo_persona = 'P' ");
		    sql.append("\n AND m.id_cve_operacion = o.id_cve_operacion ");
		    sql.append("\n and m.id_forma_pago = 7 ");
		    sql.append("\n AND m.id_tipo_movto = 'E' ");
		    sql.append("\n and id_tipo_operacion = 3000 ");
		    sql.append("\n and id_estatus_mov in('C','N') ");
		    
		    if(dtoBus.isChkTodasEmpresas())
		    	 sql.append("\n  AND m.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " +Utilerias.validarCadenaSQL(dtoBus.getIdUsuario())+ ")");
		    else
		    	sql.append("\n  AND m.no_empresa = " + Utilerias.validarCadenaSQL(dtoBus.getIdEmpresa()));
		       		    
		    if(dtoBus.getIdDivisa()!=null && !dtoBus.getIdDivisa().trim().equals(""))
		    	sql.append("\n  AND m.id_divisa = '" +Utilerias.validarCadenaSQL(dtoBus.getIdDivisa())+ "' ");
		    
		    sql.append(agregarCriteriosBusqueda(dtoBus.getMontoIni(),dtoBus.getMontoFin(),
		    		dtoBus.getPlBancoReceptor(),Utilerias.validarCadenaSQL(Utilerias.validarCadenaSQL(dtoBus.getPsFechaValor())),Utilerias.validarCadenaSQL(dtoBus.getPsFechaValorOrig()),
		    		Utilerias.validarCadenaSQL(dtoBus.getIdDivisa()),Utilerias.validarCadenaSQL(dtoBus.getPsDivision())));
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:BancaElectronica, C:EnvioTransferenciasDao, M:consultarPagosNafin");
		}
		return list;
	}
	
	public String agregarCriteriosBusqueda(double montoIni, double montoFin, int idBancoReceptor, String fechaValor,
			String fechaValorOri, String idDivisa, String psDivision){
			StringBuffer sqlCriterios = new StringBuffer();
		try{
		    if(psDivision!=null && !psDivision.equals("")) 
		        sqlCriterios.append("\n AND m.division = '" +Utilerias.validarCadenaSQL( psDivision) + "'");
		            
		    if(montoIni>0 || montoFin>0)
		        sqlCriterios.append("\n AND m.importe between " + montoIni + " and " + montoFin);
		            
		    if(idBancoReceptor>0) 
		        sqlCriterios.append("\n AND m.id_banco_benef = " + idBancoReceptor);
		            
		    if(fechaValor!=null && !fechaValor.equals(""))
		        sqlCriterios.append("\n AND m.fec_valor = convert(datetime,'" +Utilerias.validarCadenaSQL(fechaValor)+ "',103)");
		            
		    if(fechaValorOri!=null && !fechaValorOri.equals(""))
		    	sqlCriterios.append("\n AND m.fec_valor_original = convert(datetime,'" +Utilerias.validarCadenaSQL(fechaValorOri)+ "',103)");
			
		    return sqlCriterios.toString();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:BancaElectronica, C:EnvioTransferenciasDao, M:agregarCriteriosBusqueda");
			return "";
		}
	}
	
	/*Public Function FunSQLYa_Ejecutado(ByVal pdFolioDet As Double) As Boolean*/
	public boolean consultarYaEjecutado(int folioDet){
		int res=0;
		try{
			StringBuffer sql = new StringBuffer();
			sql.append("Select coalesce(count(no_folio_det), 0) as no_folio_det from det_arch_transfer where no_folio_det = " + Utilerias.validarCadenaSQL(folioDet));
			sql.append("\n	and id_estatus_arch='T'");
			
			res = jdbcTemplate.queryForInt(sql.toString());
			
			if(res>0)
				return true;
			else
				return false;
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectr�nica, C:EnvioTransferenciasDao, M:consultarYaEjecutado");
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean consultarPagoEjecutado(String poHeaders, int noEmpresa, int banco, String idChequera){
		int res=0;
		try{
			StringBuffer sql = new StringBuffer();
			sql.append("Select count(*) from det_arch_transfer_agrup where Po_Headers = '" );
			sql.append(poHeaders);
			sql.append("'	And No_Empresa =");
			sql.append(noEmpresa);
			sql.append("\n  And Id_Banco =");
			sql.append(banco);
			sql.append("\n  And Id_chequera ='");
			sql.append(idChequera);
			sql.append("' and id_estatus_arch='T'");
			
			res = jdbcTemplate.queryForInt(sql.toString());
			
			if(res>0)
				return true;
			else
				return false;
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectr�nica, C:EnvioTransferenciasDao, M:consultarYaEjecutado");
			e.printStackTrace();
			return false;
		}
	}
	
	public int insertarDetArchTransferAgrup (DetArchTranferAgrup dto){
		int resul = 0;
		StringBuffer sql = new StringBuffer();
		String nomArchivo = "";
		try {
			nomArchivo = dto.getNomArch().replace(".txt", "");
			nomArchivo = funciones.elmiminarEspaciosAlaDerecha(nomArchivo.replace(".in", ""));
			
		    sql.append("Insert Into Det_Arch_Transfer_Agrup (");
		    sql.append("\n  Nom_Arch,Po_Headers,No_Empresa,Id_Banco,Id_Chequera,");
    		sql.append("\n  Id_Banco_Benef,Id_Chequera_Benef,Fec_Propuesta,");
    		sql.append("\n  Prefijo_Benef,Importe,No_Docto,Id_Estatus_Arch,Beneficiario,Sucursal,Plaza,Concepto)");
		    sql.append("\n   values( ");
		    
		    sql.append("'"+ Utilerias.validarCadenaSQL(nomArchivo) +"','"+dto.getPoHeaders()+"',");
		    sql.append("\n	"+dto.getNoEmpresa()+","+dto.getIdBanco()+",'"+Utilerias.validarCadenaSQL(dto.getIdChequera())+"',");
		    sql.append("\n	"+dto.getIdBancoBenef()+",'"+Utilerias.validarCadenaSQL(dto.getIdChequeraBenef())+"','");
		    sql.append("\n	"+Utilerias.validarCadenaSQL(funciones.ponerFormatoDate(dto.getFecPropuesta()))+"','");
		    sql.append(Utilerias.validarCadenaSQL(dto.getPrefijoBenef())+"',"+dto.getImporte()+",'");
		    sql.append("\n	"+Utilerias.validarCadenaSQL(dto.getNoDocto())+"', '"+dto.getIdEstatusArch()+"','");
		    sql.append("\n	"+Utilerias.validarCadenaSQL(dto.getBeneficiario())+"',"+dto.getSucursal()+",");
		    sql.append("\n	"+dto.getPlaza()+",'"+Utilerias.validarCadenaSQL(dto.getConcepto())+"')");
		    
		    System.out.println("indert det arch agrupado"+sql);
		    resul = jdbcTemplate.update(sql.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectr�nica, C:EnvioTransferenciasDao, M:insertarDetArchTransferAgrup");
			e.printStackTrace();
		}
		return resul;
	}
	
	

	//revisado A.A.G
	public int insertarArchTransfer(ArchTransferDto dto){
		StringBuffer sql = new StringBuffer();
		int res=0;
		try{
			sql.append(" INSERT INTO arch_transfer (nom_arch, id_banco, fec_trans, importe, registros, ");
	        sql.append(" \n	fec_retrans, no_usuario_alta, no_usuario_modif, b_cheque_ocurre, tipo_envio_layout) ");
	        sql.append(" \n	values(?,?,?,?,?,?,?,?,?,?)");
	        
	        System.out.println("Query "+sql.toString());
	        
	        res=jdbcTemplate.update(sql.toString(), new Object[]{dto.getNomArch(), dto.getIdBanco(), funciones.ponerFormatoDate(dto.getFecTrans()),
	        			dto.getImporte(), dto.getRegistros(), dto.getFecRetrans() != null ? funciones.ponerFormatoDate(dto.getFecRetrans()) : null, 
	        			dto.getNoUsuarioAlta(), dto.getNoUsuarioAlta() > 0 ? dto.getNoUsuarioAlta() : null,
	        			dto.getBChequeOcurre(), dto.getTipoEnvioLayout()});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BancaElectr�nica, C:EnvioTransferenciasDao, M:insertarArchTransfer");
		}
		return res;
	}

	//revisar a.a.a
	/*public int actualizarMovimientoTipoEnvio(String tipoEnvio, String nomArchivos){
		StringBuffer sql = new StringBuffer();
		int plResp=0;
		String psArchivos="";
		try{
			psArchivos = nomArchivos.replaceAll(".txt","");
			psArchivos = psArchivos.replaceAll(".in","");
			psArchivos = psArchivos.replace(".sha","");
			psArchivos = psArchivos.replace(".slk","");
			

			if(psArchivos.substring(0,psArchivos.length()-1).equals(","))
				psArchivos = psArchivos.substring(0,psArchivos.length()-1);
			psArchivos = "'" + psArchivos.replace(",","','") + "'";
			
			sql.append("UPDATE movimiento SET ");
			sql.append("\n	 id_estatus_mov = 'T', ");
			sql.append("\n   id_servicio_be = '" + Utilerias.validarCadenaSQL(tipoEnvio) + "', ");
			sql.append("\n   fec_trans = (select max(fec_valor) from det_arch_transfer d " +
					"where d.no_folio_det = movimiento.no_folio_det and id_estatus_arch <> 'X' ) ");
			sql.append("\n	 WHERE no_folio_det in(");
			sql.append("\n   select no_folio_det from det_arch_transfer where nom_arch in(" + psArchivos + ") ");
			sql.append(")");
			System.out.println("actuliza mov tipo envio"+sql);
			plResp = jdbcTemplate.update(sql.toString());
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BancaElectr�nica, C:EnvioTransferenciasDao, M:actualizarMovimientoTipoEnvio");
		}
		return plResp;
	}*/
	
	public int actualizarMovimientoTipoEnvioAgrupado(String tipoEnvio, String nomArchivos){
		StringBuffer sql = new StringBuffer();
		int plResp=0;
		String psArchivos="";
		try{
			psArchivos = nomArchivos.replaceAll(".txt","");
			psArchivos = psArchivos.replaceAll(".in","");
			psArchivos = psArchivos.replace(".sha","");
			psArchivos = psArchivos.replace(".slk","");
			

			if(psArchivos.substring(0,psArchivos.length()-1).equals(","))
				psArchivos = psArchivos.substring(0,psArchivos.length()-1);
			psArchivos = "'" + psArchivos.replace(",","','") + "'";
			
			sql.append("UPDATE movimiento SET ");
			sql.append("\n	 id_estatus_mov = 'T', ");
			sql.append("\n   id_servicio_be = '" + Utilerias.validarCadenaSQL(tipoEnvio) + "', ");
			sql.append("\n   fec_trans = (select max(Fec_Propuesta) from Det_Arch_Transfer_Agrup d " +
					"where d.no_folio_det = movimiento.no_folio_det and id_estatus_arch <> 'X' ) ");
			sql.append("\n	 WHERE no_folio_det in(");
			sql.append("\n   select no_folio_det from det_arch_transfer where nom_arch in(" + psArchivos + ") ");
			sql.append(")");
			System.out.println("actuliza mov tipo envio"+sql);
			plResp = jdbcTemplate.update(sql.toString());
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BancaElectr�nica, C:EnvioTransferenciasDao, M:actualizarMovimientoTipoEnvio");
		}
		return plResp;
	}
	
	public int actulizaMovimientosTefAgrupados(String nomArhc, String tipoEnvio){
		int result = 0;
		String psArchivos = "";
		StringBuffer sql = new StringBuffer();
		try {
			psArchivos = nomArhc.replaceAll(".txt","");
			psArchivos = psArchivos.replaceAll(".in","");
			psArchivos = psArchivos.replace(".sha","");
			psArchivos = psArchivos.replace(".slk","");
			
			sql.append("UPDATE movimiento SET ");
			sql.append("\n	 id_estatus_mov = 'T', ");
			sql.append("\n   id_servicio_be = '" + Utilerias.validarCadenaSQL(tipoEnvio) + "', ");
			sql.append("\n   Fec_Trans = (Select Max(Fec_Propuesta) From Det_Arch_Transfer_Agrup D ");
			sql.append("\n   Where D.po_headers = po_headers And Id_Estatus_Arch <> 'X' ) ");
			sql.append("\n   Where po_headers In");
			sql.append("\n   (Select Po_Headers From Det_Arch_Transfer_Agrup Where Nom_Arch In('"+psArchivos+"'))");
			sql.append("\n   And No_Empresa = (Select top 1 No_Empresa From Det_Arch_Transfer_Agrup Where Nom_Arch In('"+psArchivos+"') )");
			sql.append("\n   And Id_Banco = (Select top 1 Id_Banco From Det_Arch_Transfer_Agrup Where Nom_Arch In('"+psArchivos+"') )");
			sql.append("\n   And Id_Chequera = (Select top 1 id_chequera From Det_Arch_Transfer_Agrup Where Nom_Arch In('"+psArchivos+"') )");
			sql.append("\n   and fec_propuesta = (Select top 1 Fec_Propuesta From Det_Arch_Transfer_Agrup Where Nom_Arch In('"+psArchivos+"') )");

			System.out.println("actuliza mov tipo envio"+sql);
			result = jdbcTemplate.update(sql.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectr�nica, C:EnvioTransferenciasDao, M:llamarCuadrantesTransferenciasAgrupadas");
		}
		return result;
	}
	
	public int llamarCuadrantesTransferenciasAgrupadas(String nomArhc, String tipoEnvio){
		int result = 0;
		String psArchivos = "";
		StringBuffer sql = new StringBuffer();
		try {
			psArchivos = nomArhc.replaceAll(".txt","");
			psArchivos = psArchivos.replaceAll(".in","");
			psArchivos = psArchivos.replace(".sha","");
			psArchivos = psArchivos.replace(".slk","");
			
			sql.append("UPDATE movimiento SET ");
			sql.append("\n	 id_estatus_mov = 'T', ");
			sql.append("\n   id_servicio_be = '" + Utilerias.validarCadenaSQL(tipoEnvio) + "', ");
			sql.append("\n   Fec_Trans = (Select Max(Fec_Propuesta) From Det_Arch_Transfer_Agrup D ");
			sql.append("\n   Where D.po_headers = po_headers And Id_Estatus_Arch <> 'X' ) ");
			sql.append("\n   Where po_headers In");
			sql.append("\n   (Select Po_Headers From Det_Arch_Transfer_Agrup Where Nom_Arch In('"+psArchivos+"'))");
			sql.append("\n   And No_Empresa IN (Select No_Empresa From Det_Arch_Transfer_Agrup Where Nom_Arch In('"+psArchivos+"'))");
			sql.append("\n   And Id_Banco IN (Select Id_Banco From Det_Arch_Transfer_Agrup Where Nom_Arch In('"+psArchivos+"') )");
			sql.append("\n   And Id_Chequera IN (Select id_chequera From Det_Arch_Transfer_Agrup Where Nom_Arch In('"+psArchivos+"') )");
			sql.append("\n   and fec_propuesta IN (Select Fec_Propuesta From Det_Arch_Transfer_Agrup Where Nom_Arch In('"+psArchivos+"') )");

			System.out.println("actuliza mov tipo envio"+sql);
			result = jdbcTemplate.update(sql.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectr�nica, C:EnvioTransferenciasDao, M:llamarCuadrantesTransferenciasAgrupadas");
		}
		return result;
	}
	
	public int eliminarArchTransferAgrup(String nomArchivos){
		StringBuffer sql = new StringBuffer();
		int upDet=0;
		int upArch=0;
		int res=0;
		try{
			//BeginTransac
			sql.append("DELETE from Det_Arch_Transfer_Agrup where nom_arch in(" + nomArchivos + ")");
			upDet=jdbcTemplate.update(sql.toString());
			sql = new StringBuffer();
			sql.append("DELETE from arch_transfer where nom_arch in(" + nomArchivos + ")");
			upArch=jdbcTemplate.update(sql.toString());
			//CommitTransac
			if(upDet>0 && upArch>0)
				res=1;
			else 
				res=0;
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BancaElectr�nica, C:EnvioTransferenciasDao, M:eliminarArchTransfer");
		}
		return res;
	}
	
	
	public int eliminarArchTransfer(String nomArchivos){
		StringBuffer sql = new StringBuffer();
		int upDet=0;
		int upArch=0;
		int res=0;
		try{
			//BeginTransac
			sql.append("DELETE from det_arch_transfer where nom_arch in(" + nomArchivos + ")");
			upDet=jdbcTemplate.update(sql.toString());
			sql = new StringBuffer();
			sql.append("DELETE from arch_transfer where nom_arch in(" + nomArchivos + ")");
			upArch=jdbcTemplate.update(sql.toString());
			//CommitTransac
			if(upDet>0 && upArch>0)
				res=1;
			else 
				res=0;
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BancaElectr�nica, C:EnvioTransferenciasDao, M:eliminarArchTransfer");
		}
		return res;
	}
	
	public String obtenerChequeraBenefOficial(int idBancoBenef, String sucBenef, String plazaBenef, String idChequeraBenef, int idBanco) {
		String idChequeraBenefReal = "";
		
		try {
			if(sucBenef.length() > 4) sucBenef = sucBenef.substring(0, 3);
			if(plazaBenef.length() > 3) plazaBenef = plazaBenef.substring(0, 2);
			
			if(idBancoBenef == 2) {
				if(idBanco == 2)
					idChequeraBenefReal = funciones.ponerCeros(idChequeraBenef.substring(4), 11);
				else
					idChequeraBenefReal = idChequeraBenef;
			}else if(idBancoBenef == 12) {
				if(idChequeraBenef.length() == 11) {
					if(Integer.parseInt(idChequeraBenef.substring(0, 2)) == 0)
						idChequeraBenefReal = funciones.ponerCeros(plazaBenef, 3) + idChequeraBenef.substring(3);
					else
						idChequeraBenefReal = idChequeraBenef;
				}else
					idChequeraBenefReal = idChequeraBenef;
			}else
				idChequeraBenefReal = idChequeraBenef;
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectr�nica, C:EnvioTransferenciasDao, M:obtenerChequeraBenefOficial");
		}
		return idChequeraBenefReal;
	}
	
	public int updateMovimiento(String psNomArchivos, int folioConfirma)
	{
		int resultado = 0;
		String sql = "";
		String psArchivos = "";
		try
		{
			psArchivos = psNomArchivos.replace(".txt", "");
			psArchivos = psArchivos.replace(".sha", "");
			psArchivos = psArchivos.replace(".slk", "");
			
			if (psArchivos.substring(psArchivos.length() - 1, psArchivos.length()).equals(","))
			{
				psArchivos = psArchivos.substring(0, psArchivos.length() - 1);
			}
			psArchivos = "'" + psArchivos.replace(",", "','") + "'";
			
			sql = "";
			sql += "Update movimiento Set ";
			sql += "origen_mov_ant = " + folioConfirma + " ";
			sql += " where no_folio_det in ( ";
			sql += "select no_folio_det from det_arch_transfer where nom_arch in (" + psArchivos + ")";
			sql += ")";
			sql += "and id_estatus_mov = 'T'";
			
			resultado = jdbcTemplate.update(sql);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + toString() + "P:BancaElectronica, C:EnvioTransferenciasDao, M:updateMovimiento");
		}		
		return resultado;
	}
	//revvisar
	public int updateHistMovimiento(String psNomArchivos, int folioConfirma)
	{
		int resultado = 0;
		String sql = "";
		String psArchivos = "";
		try
		{
			psArchivos = psNomArchivos.replace(".txt", "");
			psArchivos = psArchivos.replace(".sha", "");
			psArchivos = psArchivos.replace(".slk", "");
			
			if (psArchivos.substring(psArchivos.length() - 1, psArchivos.length()).equals(","))
			{
				psArchivos = psArchivos.substring(0, psArchivos.length() - 1);
			}
			psArchivos = "'" + psArchivos.replace(",", "','") + "'";
			
			sql = "";
			sql += "Update movimiento Set ";
			sql += "origen_mov_ant = " + folioConfirma + "";
			sql += "where no_folio_det in ( ";
			sql += "select no_folio_det from det_arch_transfer where nom_arch in (" + psArchivos + ")";
			sql += ")";
			sql += "and id_estatus_mov = 'T'";
			
			resultado = jdbcTemplate.update(sql);
			System.out.println("resultado"+resultado);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + toString() + "P:BancaElectronica, C:EnvioTransferenciasDao, M:updateMovimiento");
		}		
		return resultado;
	}
	
	
	public List<Map<String, Object>> reporteDetArchivoTransf(CriterioBusquedaDto dtoBus){
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
		boolean pbControlArchivo = false;
		
		try {
			sql.append(reporteControlArchivo("movimiento", dtoBus.getCampo()));
			
			if(pbControlArchivo) {
				sql.append(" \n UNION \n ");
				sql.append(reporteControlArchivo("hist_movimiento", dtoBus.getCampo()));
				sql.append(" \n UNION \n ");
				sql.append(reporteControlArchivo("hist_solicitud", dtoBus.getCampo()));
			}
			System.out.println("Estra es la consulta del rporte------>  "+sql);
	        listResult = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, Object>>(){
				public Map<String, Object> mapRow(ResultSet rs, int idx) throws SQLException {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("fecValorOriginal", rs.getString("fec_valor_original"));
					map.put("nomArch", rs.getString("nom_arch"));
					map.put("noDocto", rs.getString("no_docto"));
					map.put("idEstatusArch", rs.getString("id_estatus_arch"));
					map.put("fecValor", rs.getDate("fec_valor"));
					map.put("idBanco", rs.getString("id_banco"));
					map.put("idBancoBenef", rs.getString("id_banco_benef"));
					map.put("idChequera", rs.getString("id_chequera"));
					map.put("idChequeraBenef", rs.getString("id_chequera_benef"));
					map.put("importe", rs.getDouble("importe"));
					map.put("prefijoBenef", rs.getString("prefijo_benef"));
					map.put("sucursal", rs.getString("sucursal"));
					map.put("plaza", rs.getString("plaza"));
					map.put("beneficiario", rs.getString("beneficiario").replace("\n", " ").replace("\b", " "));
					//System.out.println(rs.getString("beneficiario")+" longitu"+rs.getString("beneficiario").length());
					/*map.put("beneficiario", rs.getString("beneficiario") != null && 
								rs.getString("beneficiario").length()>45 ?
								rs.getString("beneficiario").substring(0, 30):
									rs.getString("beneficiario"));*/
					map.put("clabeBenef", rs.getString("clabe_benef") != null ? rs.getString("clabe_benef") : "");
					map.put("idDivisa", rs.getString("id_divisa"));
	    			return map;
				}
			});
	        System.out.println("Este es el reporte lista \n ---"+listResult.get(0).get("beneficiario")+"-----");
	    }catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:EnvioTransferenciasDao, M:reporteDetArchivoTransf");
			e.printStackTrace();
		}
		return listResult;
	}
	
	public String reporteControlArchivo(String psTabla, String psArchivo) {
		StringBuffer sql = new StringBuffer();
		psArchivo = psArchivo.substring(0, psArchivo.indexOf("."));
		
		try{
			//PAGO PROVEEDORES
		    sql.append("\n  SELECT distinct m.Fec_Propuesta as fec_valor_original,a.nom_arch, M.Po_Headers as no_docto, a.id_estatus_arch, ");
		    //AT PARA LOS DOCUMENTOS DE MASSPAYMENT <50000 INTERBANCARIOS LA FECHA VALOR ES MA�ANA
		    sql.append("\n  CASE WHEN nom_arch like 'DESC%' and m.id_banco in(2) ");
		    sql.append("\n        and m.importe<50000 and m.id_banco_benef not in(2,12) and m.id_divisa='MN' then ");
		    
		    if(psTabla.equals("movimiento"))
		        sql.append("\n    (select fec_manana from fechas) ");
		    else {
		      if(gsDBM.equals("DB2")) {
		          sql.append("\n    CASE WHEN dayname(a.fec_valor + 1 day)='S�bado' then  ");
		          sql.append("\n                a.fec_valor + 2 day");
		          sql.append("\n   ELSE a.fec_valor + 1 day END ");
		      }else {
		          sql.append("\n    CASE WHEN datename(dw,dateadd(day,1,a.fec_valor))='S�bado' then  ");
		          sql.append("\n                DateAdd(day, 2, a.fec_valor)");
		          sql.append("\n   ELSE dateadd(day, 1,a.fec_valor) END ");
		      }
		    }
		    sql.append("\n  ELSE a.fec_propuesta END as fec_valor ");
		    sql.append("\n       ,a.id_banco, a.id_banco_benef, ");
		    sql.append("\n      a.id_chequera, a.id_chequera_benef, a.importe, ");
		    sql.append("\n      a.prefijo_benef, a.sucursal, a.plaza, a.beneficiario, ");
		    sql.append("\n      c.id_clabe as clabe_benef, ");
		    sql.append("\n      CASE WHEN ");
		    sql.append("\n          ( SELECT coalesce(b.transfer_casa_cambio, 'N') ");
		    sql.append("\n            FROM cat_banco b ");
		    sql.append("\n            WHERE b.id_banco = m.id_banco ) = 'S' ");
		    sql.append("\n      THEN m.id_divisa_original ");
		    sql.append("\n      ELSE m.id_divisa END as id_divisa ");
		    sql.append("\n  FROM det_arch_transfer_agrup a," + psTabla + " m, ctas_banco c ");
		    //sql.append("\n  WHERE a.no_folio_det = m.no_folio_det ");
		    sql.append("\n  where");
		    sql.append("\n 	A.Po_Headers = M.Po_Headers	");
		    sql.append("\n 	And A.No_Empresa = M.No_Empresa	");
		    sql.append("\n 	And A.Id_Chequera = m.Id_Chequera	");
		    sql.append("\n 	And A.Id_Banco = M.Id_Banco	");
		    sql.append("\n 	and M.Fec_Propuesta = M.Fec_Propuesta");
		   	sql.append("\n  AND cast(m.no_cliente as integer) = c.no_persona ");
		    
		    sql.append("\n      AND m.id_banco_benef = c.id_banco ");
		    sql.append("\n      AND m.id_chequera_benef = c.id_chequera ");
		    sql.append("\n      AND id_estatus_arch <> 'X' ");
		    sql.append("\n      AND nom_arch = '" + Utilerias.validarCadenaSQL(psArchivo) + "' ");
		    sql.append("\n      AND m.origen_mov <> 'FIL'");
		    sql.append("\n      AND no_cliente not in (" + consultarConfiguraSet(206) + ")");
		    sql.append("\n      AND m.id_tipo_operacion NOT BETWEEN 3700 and 3799 ");
		    
		    sql.append("\n  UNION ALL ");  //PAGO FILIALES O INCIDENTALES
		    
		    sql.append("\n  SELECT distinct m.fec_valor_original,a.nom_arch, M.Po_Headers as no_docto, ");
		    sql.append("\n      a.id_estatus_arch, ");
		    //'AT PARA LOS DOCUMENTOS DE MASSPAYMENT <50000 INTERBANCARIOS LA FECHA VALOR ES MA�ANA
		    sql.append("\n  CASE WHEN nom_arch like 'DESC%' and m.id_banco in(2) ");
		    sql.append("\n        and m.importe<50000 and m.id_banco_benef not in(2,12) and m.id_divisa='MN' then ");
		    if(psTabla.equals("movimiento"))
		        sql.append("\n    (select fec_manana from fechas) ");
		    else {
		      if(gsDBM.equals("DB2")) {
		          sql.append("\n    CASE WHEN dayname(a.fec_valor + 1 day)='S�bado' then  ");
		          sql.append("\n                a.fec_valor + 2 day");
		          sql.append("\n   ELSE a.fec_valor + 1 day END ");
		      }else {
		          sql.append("\n    CASE WHEN datename(dw,dateadd(day,1,a.fec_valor))='S�bado' then  ");
		          sql.append("\n                DateAdd(day, 2, a.fec_valor)");
		          sql.append("\n   ELSE dateadd(day, 1,a.fec_valor) END ");
		      }
		   }
		    sql.append("\n  ELSE a.fec_propuesta END as fec_valor ");
		    sql.append("\n     ,a.id_banco, a.id_banco_benef, ");
		    sql.append("\n      a.id_chequera, a.id_chequera_benef, a.importe, ");
		    sql.append("\n      a.prefijo_benef, a.sucursal, a.plaza, a.beneficiario, ");
		    sql.append("\n      m.clabe as clabe_benef, ");
		    sql.append("\n      CASE WHEN ");
		    sql.append("\n          ( SELECT coalesce(b.transfer_casa_cambio, 'N') ");
		    sql.append("\n            FROM cat_banco b ");
		    sql.append("\n            WHERE b.id_banco = m.id_banco ) = 'S' ");
		    sql.append("\n      THEN m.id_divisa_original ");
		    sql.append("\n      ELSE m.id_divisa END as id_divisa ");
		    sql.append("\n  FROM det_arch_transfer_agrup a," + psTabla + " m ");
		    //sql.append("\n  WHERE a.no_folio_det = m.no_folio_det ");
		    sql.append("\n  where");
		    sql.append("\n 	A.Po_Headers = M.Po_Headers	");
		    sql.append("\n 	And A.No_Empresa = M.No_Empresa	");
		    sql.append("\n 	And A.Id_Chequera = m.Id_Chequera	");
		    sql.append("\n 	And A.Id_Banco = M.Id_Banco	");
		    sql.append("\n 	and M.Fec_Propuesta = M.Fec_Propuesta");
		    sql.append("\n      AND id_estatus_arch <> 'X' ");
		    sql.append("\n      AND nom_arch = '" + psArchivo + "' ");
		    sql.append("\n      AND (m.origen_mov = 'FIL' ");
		    sql.append("\n          OR no_cliente in (" + consultarConfiguraSet(206)+ ") )");
		    
		    sql.append("\n  UNION ALL ");  //'TRAPASOS
		    
		    sql.append("\n  SELECT distinct m.fec_valor_original,a.nom_arch, M.Po_Headers as no_docto, ");
		    sql.append("\n      a.id_estatus_arch, ");
		    //'AT PARA LOS DOCUMENTOS DE MASSPAYMENT <50000 INTERBANCARIOS LA FECHA VALOR ES MA�ANA
		    sql.append("\n  CASE WHEN nom_arch like 'DESC%' and m.id_banco in(2) ");
		    sql.append("\n        and m.importe<50000 and m.id_banco_benef not in(2,12) and m.id_divisa='MN' then ");
		    if(psTabla.equals("movimiento"))
		        sql.append("\n    (select fec_manana from fechas) ");
		    else {
		      if(gsDBM.equals("DB2")) {
		          sql.append("\n    CASE WHEN dayname(a.fec_valor + 1 day)='S�bado' then  ");
		          sql.append("\n                a.fec_valor + 2 day");
		          sql.append("\n   ELSE a.fec_valor + 1 day END ");
		      }else {
		          sql.append("\n    CASE WHEN datename(dw,dateadd(day,1,a.fec_valor))='S�bado' then  ");
		          sql.append("\n                DateAdd(day, 2, a.fec_valor)");
		          sql.append("\n   ELSE dateadd(day, 1,a.fec_valor) END ");
		      }
		    }
		    sql.append("\n  ELSE a.fec_propuesta END as fec_valor ");
		    sql.append("\n       , a.id_banco, a.id_banco_benef, ");
		    sql.append("\n      a.id_chequera, a.id_chequera_benef, a.importe, ");
		    sql.append("\n      a.prefijo_benef, a.sucursal, a.plaza, a.beneficiario, ");
		    sql.append("\n      c.id_clabe as clabe_benef, ");
		    sql.append("\n      CASE WHEN ");
		    sql.append("\n          ( SELECT coalesce(b.transfer_casa_cambio, 'N') ");
		    sql.append("\n            FROM cat_banco b ");
		    sql.append("\n            WHERE b.id_banco = m.id_banco ) = 'S' ");
		    sql.append("\n      THEN m.id_divisa_original ");
		    sql.append("\n      ELSE m.id_divisa END as id_divisa ");
		    sql.append("\n  FROM det_arch_transfer_agrup a," + psTabla + "  m,cat_cta_banco c ");
		    
		    //sql.append("\n  WHERE a.no_folio_det = m.no_folio_det ");
		    sql.append("\n  where");
		    sql.append("\n 	A.Po_Headers = M.Po_Headers	");
		    sql.append("\n 	And A.No_Empresa = M.No_Empresa	");
		    sql.append("\n 	And A.Id_Chequera = m.Id_Chequera	");
		    sql.append("\n 	And A.Id_Banco = M.Id_Banco	");
		    sql.append("\n 	and M.Fec_Propuesta = M.Fec_Propuesta");
		    
	    	sql.append("\n  AND cast(m.no_cliente as integer) = c.no_empresa ");
		    
		    sql.append("\n      AND m.id_banco_benef = c.id_banco ");
		    sql.append("\n      AND m.id_chequera_benef = c.id_chequera ");
		    sql.append("\n      AND a.id_estatus_arch <> 'X' ");
		    sql.append("\n      AND a.nom_arch = '" + Utilerias.validarCadenaSQL(psArchivo) + "' ");
		    sql.append("\n      AND coalesce(m.origen_mov,'') <> 'FIL' ");
		    sql.append("\n      AND no_cliente not in (" + consultarConfiguraSet(206) + ") ");
		    sql.append("\n      AND m.id_tipo_operacion BETWEEN 3700 and 3899 ");
		    sql.append("\n     order by no_docto");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectr�nica, C:EnvioTransferenciasDao, M:reporteControlArchivo");
		}
		return sql.toString();
	}
	
	public int buscaDatosBenef(List<Map<String, String>> objParams, int i) {
		StringBuffer sql = new StringBuffer();
		int res = 0;
		
		try {
			sql.append("SELECT count(*) FROM ctas_banco \n");
			sql.append("WHERE no_persona = "+ objParams.get(i).get("noCliente") +" \n");
			sql.append("	And id_banco = "+ objParams.get(i).get("idBancoBenef") +" \n");
			sql.append("	And id_chequera = '"+ Utilerias.validarCadenaSQL(objParams.get(i).get("idChequeraBenef")) +"'");
			
			res = jdbcTemplate.queryForInt(sql.toString());
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectr�nica, C:EnvioTransferenciasDao, M:buscaDatosBenef");
			e.printStackTrace();
			return 0;
		}
		return res;
	}
	
	
	public List<LlenaComboGralDto> obtenerBancosBenef(int noPersona) {
		List<LlenaComboGralDto> listBancos = new ArrayList<LlenaComboGralDto>();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT csb.id_banco as ID, cb.desc_banco as Descrip \n");
			sql.append(" FROM ctas_banco csb, cat_banco cb \n");
			sql.append(" WHERE csb.id_banco = cb.id_banco \n");
			sql.append(" 	And csb.no_persona = "+ noPersona +" \n");
			
			listBancos = jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("Descrip"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:BancaElectronica, C:EnvioTransferenciasDao, M:obtenerBancosBenef");
		}
		return listBancos;
	}
	
	
	public List<LlenaComboGralDto> obtenerChequerasBenef(int noPersona, int idBanco) {
		List<LlenaComboGralDto> listChequeras = new ArrayList<LlenaComboGralDto>();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT id_chequera as ID, id_chequera as Descrip \n");
			sql.append(" FROM ctas_banco \n");
			sql.append(" WHERE no_persona = "+ noPersona +" \n");
			sql.append(" 	And id_banco = "+ idBanco +" \n");
			
			listChequeras = jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setIdStr(rs.getString("ID"));
					cons.setDescripcion(rs.getString("Descrip"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:BancaElectronica, C:EnvioTransferenciasDao, M:obtenerChequerasBenef");
		}
		return listChequeras;
	}
	
	public int actualizaMovtoCheqBenef(String noFactura, int noEmpresa, int noCliente, int noFolioMov, int idBanco, String chequera) {
		StringBuffer sql = new StringBuffer();
		int res = 0;
		
		try {
			chequera = funciones.ajustarLongitudCampo(chequera, 11, "D", "", "0");
			
			sql.append("UPDATE movimiento SET id_banco_benef = "+ idBanco +", id_chequera_benef = '"+ Utilerias.validarCadenaSQL(chequera) +"' \n");
			sql.append("WHERE no_factura = '"+ Utilerias.validarCadenaSQL(noFactura) +"' \n");
			sql.append("	And no_cliente = "+ noCliente +" \n");
			sql.append("	And no_folio_mov = "+ noFolioMov +" \n");
			sql.append("	And no_empresa = "+ noEmpresa +" \n");
			
			res = jdbcTemplate.update(sql.toString());
			
			sql = new StringBuffer();
			sql.append("UPDATE hist_solicitud SET id_banco_benef = "+ idBanco +", id_chequera_benef = '"+ Utilerias.validarCadenaSQL(chequera) +"' \n");
			sql.append("WHERE no_factura = '"+ Utilerias.validarCadenaSQL(noFactura) +"' \n");
			sql.append("	And no_cliente = "+ noCliente +" \n");
			sql.append("	And no_empresa = "+ noEmpresa +" \n");
			
			jdbcTemplate.update(sql.toString());
			
			sql = new StringBuffer();
			sql.append("UPDATE hist_mov_det SET id_banco_benef = "+ idBanco +", id_chequera_benef = '"+ Utilerias.validarCadenaSQL(chequera) +"' \n");
			sql.append("WHERE no_factura = '"+ Utilerias.validarCadenaSQL(noFactura) +"' \n");
			sql.append("	And no_cliente = "+ noCliente +" \n");
			sql.append("	And no_empresa = "+ noEmpresa +" \n");
			
			jdbcTemplate.update(sql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectr�nica, C:EnvioTransferenciasDao, M:actualizaMovtoCheqBenef");
			e.printStackTrace();
			return 0;
		}
		return res;
	}
	
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectr�nica, C:EnvioTransferenciasDao, M:setDataSource");
		}
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
}

