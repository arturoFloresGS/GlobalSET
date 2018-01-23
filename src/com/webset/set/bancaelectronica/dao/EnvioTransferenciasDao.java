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
import com.webset.set.bancaelectronica.dto.DetArchTransferDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.utils.tools.Utilerias;

public class EnvioTransferenciasDao {

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
			System.out.println("CONSULTA PAGOS Masspayment"+sql);
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
		    if(consultarConfiguraSet(232).trim().equals("SI") && dtoBus.getIdBanco() == 21) 
		    	pbH2HBital = true;
		    //'********************************* ORIGEN MOV <> FIL
	    	sql = new StringBuffer();
            sql.append("Select distinct convert(char,m.fec_valor_original,3) as fec_valor_original,m.id_tipo_operacion,coalesce(b2.id_banco_city,0) as banco_city,b2.inst_finan,m.observacion,c.desc_sucursal as suc_origen,");
            sql.append("\n coalesce(ctas.sucursal, 0) as suc_destino, m.id_banco,");
            sql.append("\n b.b_layout_comerica, ");
            sql.append("\n case when m.no_docto='null' then m.no_factura else m.no_docto end as no_docto,convert(char,m.fec_operacion,103) as fec_operacion,o.desc_cve_operacion,  ");
            sql.append("\n CASE WHEN coalesce(b.transfer_casa_cambio, 'N') = 'S' and m.origen_mov='CVT' ");
            sql.append("\n      THEN m.importe ");
            sql.append("\n      ELSE m.importe END as importe, ");
            sql.append("\n CASE WHEN coalesce(b.transfer_casa_cambio, 'N') = 'S' and m.origen_mov='CVT' ");
            sql.append("\n      THEN m.id_divisa_original ");
            sql.append("\n      ELSE m.id_divisa END as id_divisa, ");
            sql.append("\n m.id_chequera_benef,m.origen_mov, m.division,");
            sql.append("\n b2.desc_banco as nombre_banco_benef,b2.desc_banco as desc_banco_benef,");
            sql.append("\n CASE WHEN ");
            sql.append("\n          ( SELECT coalesce(b.transfer_casa_cambio, 'N') ");
            sql.append("\n            FROM cat_banco b ");
            sql.append("\n            WHERE b.id_banco = m.id_banco ) = 'S' and m.origen_mov='CVT' ");
            sql.append("\n      THEN ( SELECT coalesce(pp.razon_social, '') ");
            sql.append("\n             FROM persona pp ");
            sql.append("\n             WHERE pp.no_persona = m.no_cliente ");
            sql.append("\n                 and pp.id_tipo_persona = 'P' ) ");
            sql.append("\n      ELSE m.beneficiario END as beneficiario, ");
            sql.append("\n m.concepto,m.no_folio_det,m.no_empresa, ");
            sql.append("\n m.id_chequera,convert(char,m.fec_valor,103) as fec_valor,");
            sql.append("\n m.id_banco_benef,");
            sql.append("\n coalesce(ctas.plaza, 0) as plaza_benef,");
            sql.append("\n c.desc_plaza as plaza,");
            sql.append("\n m.no_cliente ,");
            sql.append("\n b2.desc_banco_inbursa, ");
            sql.append("\n CASE WHEN no_cliente IN (999999, 999998) THEN clabe ELSE ctas.id_clabe END AS clabe_benef ");
            
            sql.append("\n  ,coalesce(ctas.aba, '') as aba, coalesce(ctas.swift_code, '') as swift_code ");
            sql.append("\n  ,coalesce(ctas.aba_intermediario, '') as aba_intermediario, coalesce(ctas.swift_intermediario, '') as swift_intermediario ");
            sql.append("\n  ,coalesce(ctas.aba_corresponsal, '') as aba_corresponsal, coalesce(ctas.swift_corresponsal, '') as swift_corresponsal ");
            if(dtoBus.getIdDivisa()!=null && dtoBus.getIdDivisa().trim().equals(consultarConfiguraSet(772)))
            {
                sql.append("\n  ,(select b3.desc_banco from cat_banco b3 where b3.id_banco = ctas.id_bank_true) as  nom_banco_intermediario ");
                sql.append("\n  ,(select b4.desc_banco from cat_banco b4 where b4.id_banco = ctas.id_bank_corresponding) as nom_banco_corresponsal");
            }else{
            	sql.append("\n, ' ' as nom_banco_intermediario ");
                sql.append("\n, ' ' as nom_banco_corresponsal ");
            }
            
            sql.append("\n  ,b.valida_CLABE,e.id_contrato_mass,e.id_contrato_wlink ");
            sql.append("\n  ,p.rfc as rfc_benef ");
            sql.append("\n  ,m.no_factura,e.nom_empresa ");
            sql.append("\n  ,case when ctas.id_banco is null then 'N' else 'S' end as existe_chequera_prov, pp.rfc ");
            
            if(pbH2HBital)
            	sql.append("\n,u.desc_usuario_bital,s.desc_servicio_bital");
            
            sql.append("\n ,ctas.especiales, p.equivale_persona, ctas.complemento, "
            		+ "(select max(id_tipo_envio) from cat_tipo_envio_layout ctt where "
            		+ "ctt.id_banco = m.id_banco and tipo_envio_layout = ctas.tipo_envio_layout) as tipo_envio_layout ");
            
            //'MAS 04/09/02007 se agrega para validar la divisa de la chequera de pago contra la del docto
            sql.append("\n,(select id_divisa from cat_cta_banco ccb where ccb.no_empresa = m.no_empresa ");
            sql.append("\n   and ccb.id_banco = m.id_banco and ccb.id_chequera = m.id_chequera) as divisa_chequera");
            sql.append("\n   ,d.ciudad +  ', ' +  d.id_estado as direccion_benef , "
            		+ "(select pa.desc_pais from cat_pais pa where pa.id_pais = 'MX') as pais_benef,"
            		+ " m.cve_control as clave, m.no_folio_mov");
            ///
            sql.append(" From Movimiento M ");
            sql.append(" Left Join Cat_Banco B2 On ( m.Id_Banco_Benef = B2.Id_Banco )");
            sql.append(" Left Join Ctas_Banco Ctas On (M.Id_Banco_Benef = Ctas.Id_Banco"
                                           +" And  M.Id_Chequera_Benef = Ctas.Id_Chequera"
                                           +" And Cast(M.No_Cliente As Integer) = Ctas.No_Persona )");
            sql.append(" Left Outer Join Direccion D on (Cast(M.No_Cliente As Integer) = d.no_persona "
            		+"and d.id_tipo_direccion = 'OFNA'"
            		+"and d.no_direccion = 1 "
            		+"and d.id_tipo_persona = 'P'"
            		+"and d.no_empresa = 552"
            		+ "),");
            sql.append(" Cat_Cta_Banco C , Empresa E,Persona P,"
            		+ " Persona Pp,"
            		+ " cat_cve_operacion o, cat_banco b");
         
            //
                        
            if(pbH2HBital)
                sql.append("\n  ,cat_usuario_bital u, cat_servicio_bital s");
            
            sql.append("\n  WHERE ");
            sql.append("\n   Cast(M.No_Cliente As Integer) = p.no_persona ");
            sql.append("\n  and ((m.origen_mov='CVT' and  p.id_tipo_persona in ('K')) "
            		+ "or( m.origen_mov<>'CVT' and  p.id_tipo_persona in ('P')) ) ");
            
            if(pbH2HBital)
            {
                sql.append("\n  and e.id_usuario_bital *= u.id_usuario_bital ");
                sql.append("\n  and e.id_servicio_bital *= s.id_servicio_bital ");
            }
            
            sql.append("\n  and m.no_empresa = pp.no_empresa ");
            sql.append("\n  and m.no_empresa = pp.no_persona ");
            sql.append("\n  and pp.id_tipo_persona = 'E'");
            
            sql.append("\n  and m.no_empresa = e.no_empresa ");
            
            sql.append("\n  and ((b.transfer_casa_cambio = 'S' and m.id_tipo_operacion = 3000 and m.id_estatus_mov = 'V' And (m.id_divisa <> 'DLS' And m.id_divisa <> 'MN')) OR m.id_estatus_mov = 'P') ");
            sql.append("\n  and m.id_forma_pago = 3 ");
            sql.append("\n  AND m.id_cve_operacion = o.id_cve_operacion ");
            sql.append("\n  AND m.id_banco = b.id_banco");
            //'sql.append("\n  AND m.no_empresa = c.no_empresa ");
            sql.append("\n  AND m.id_banco = c.id_banco");
            sql.append("\n  AND m.id_chequera = c.id_chequera ");
            sql.append("\n  AND m.id_tipo_movto = 'E' ");
            sql.append("\n  AND b.b_banca_elect  in ('A','E')  ");
            sql.append("\n  and m.id_banco = " + Utilerias.validarCadenaSQL(dtoBus.getIdBanco()));
            
           // if(dtoBus.getIdEmpresa()==0)
            if(dtoBus.isChkTodasEmpresas())
                sql.append("\n  AND m.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " +Utilerias.validarCadenaSQL(dtoBus.getIdUsuario())+ ")");
            else
                //sql.append("\n  AND m.no_empresa = " +dtoBus.getIdEmpresa());
            	sql.append("\n  AND m.no_empresa in (Select No_Empresa From Grupo_Empresa Where Id_Grupo_Flujo = " +Utilerias.validarCadenaSQL(dtoBus.getIdEmpresa())+") ");
            
            if(dtoBus.getIdDivisa()!=null && !dtoBus.getIdDivisa().equals(""))
            {
                sql.append("\n  AND CASE WHEN coalesce(b.transfer_casa_cambio, 'N') = 'S' and m.origen_mov='CVT'");
                sql.append("\n           THEN m.id_divisa_original ");
                sql.append("\n           ELSE m.id_divisa END = '" +Utilerias.validarCadenaSQL(dtoBus.getIdDivisa())+ "' ");
            }
            
            if(dtoBus.isChkConvenioCie())
                sql.append("\n  AND substring(id_chequera_benef,1,4) = 'CONV'");
            else if (dtoBus.getIdBanco()==12 && (dtoBus.getOptTipoEnvio()!=null && dtoBus.getOptTipoEnvio().trim().equals("Mismo")))
                sql.append("\n  AND substring(id_chequera_benef,1,4) <> 'CONV'");
            
            if(dtoBus.getOptComerica()!=null && dtoBus.getOptComerica().trim().equals("ACH"))
                sql.append("\n  AND m.id_servicio_be = 'ACH' ");
            
            if(dtoBus.isChkSoloLocales())
                sql.append("\n  AND e.b_local = 'S' ");
            
            sql.append(agregarCriteriosBusqueda(dtoBus.getMontoIni(),dtoBus.getMontoFin(),dtoBus.getPlBancoReceptor(),Utilerias.validarCadenaSQL(dtoBus.getPsFechaValor())
            		,dtoBus.getPsFechaValorOrig(),dtoBus.getIdDivisa(),dtoBus.getPsDivision()));
            
            if(dtoBus.getOptTipoEnvio()!=null && dtoBus.getOptTipoEnvio().trim().equals("SPEUA"))
            {
            	sql.append("\n  and m.id_banco <> m.id_banco_benef ");
                sql.append("\n  and m.importe >= 50000 ");
            }
            else if (dtoBus.getOptTipoEnvio()!=null && dtoBus.getOptTipoEnvio().trim().equals("Mismo"))
                    sql.append("\n  and m.id_banco = m.id_banco_benef");
            else if (dtoBus.getOptTipoEnvio()!=null && dtoBus.getOptTipoEnvio().trim().equals("Internacional"))
               sql.append("\n AND m.id_banco <> m.id_banco_benef");
            else
            {
               if(dtoBus.getIdBanco()!=12 && (dtoBus.getIdDivisa()!=null && dtoBus.getIdDivisa().trim().equals(consultarConfiguraSet(771))))
               {
                    sql.append("\n AND ((b.nac_ext = 'N' and b2.nac_ext = 'N')");
                    sql.append("\n or (b2.nac_ext = 'N' and b.nac_ext = 'N'))");
               }
                sql.append("\n AND m.id_banco <> m.id_banco_benef");
            }
            sql.append("\n  AND m.origen_mov <> 'FIL' ");
            if (dtoBus.isNomina()){
                sql.append("\n  AND m.origen_mov = 'PRO' ");
            } else {

                sql.append("\n  AND m.origen_mov <> 'PRO' ");
            }
            //sql.append("\n  and Cast(M.No_Cliente As Integer) *= d.no_persona ");
            //sql.append("\n  and d.id_tipo_direccion = 'OFNA' ");
            //sql.append("\n  and d.no_direccion = 1 ");
            
           // sql.append("\n  and d.id_tipo_persona = 'P' ");
            //sql.append("\n  and d.no_empresa = 552 ");
            
            if (!dtoBus.getValidaCampo().equals("0")) {
            		sql.append("\n  and m.cve_control = '");
                    sql.append(dtoBus.getValidaCampo());
                    sql.append("'");
			}
            
            if (dtoBus.isChkConvenioSant()) {
            	sql.append("\n AND m.id_chequera_benef like 'SANT%' ");
            } else {
            	sql.append("\n AND m.id_chequera_benef not like 'SANT%' ");
            }
            
            if (dtoBus.isChkDebito()) {
            	sql.append("\n AND LEN(m.id_chequera_benef) = 16 ");
            } else {
            	sql.append("\n AND LEN(m.id_chequera_benef) <> 16 ");
            }
            //'********************************* ORIGEN MOV = FIL
            
            sql.append("\n  UNION ALL ");
            sql.append("\n  Select distinct convert(char,m.fec_valor_original,103) as fec_valor_original,m.id_tipo_operacion,b2.id_banco_city,b2.inst_finan,m.observacion,c.desc_sucursal as suc_origen,");
            sql.append("\n coalesce(m.sucursal, 0) as suc_destino ,m.id_banco,");
            sql.append("\n b.b_layout_comerica, ");
            sql.append("\n case when m.no_docto='null' then m.no_factura else m.no_docto end as no_docto, convert(char,m.fec_operacion,103) as fec_operacion,o.desc_cve_operacion,  ");
            sql.append("\n CASE WHEN coalesce(b.transfer_casa_cambio, 'N') = 'S' and m.origen_mov='CVT' ");
            sql.append("\n      THEN m.importe ");
            sql.append("\n      ELSE m.importe END as importe, ");
            sql.append("\n CASE WHEN coalesce(b.transfer_casa_cambio, 'N') = 'S' and m.origen_mov='CVT' ");
            sql.append("\n      THEN m.id_divisa_original ");
            sql.append("\n      ELSE m.id_divisa END as id_divisa, ");
            sql.append("\n m.id_chequera_benef,m.origen_mov, m.division,");
            sql.append("\n '' as nombre_banco_benef,b2.desc_banco as desc_banco_benef,");
            sql.append("\n CASE WHEN ");
            sql.append("\n          ( SELECT coalesce(b.transfer_casa_cambio, 'N') ");
            sql.append("\n            FROM cat_banco b ");
            sql.append("\n            WHERE b.id_banco = m.id_banco ) = 'S' and m.origen_mov='CVT' ");
            sql.append("\n      THEN ( SELECT coalesce(pp.razon_social, '') ");
            sql.append("\n             FROM persona pp ");
            sql.append("\n             WHERE pp.no_persona = m.no_cliente ");
            sql.append("\n                 and pp.id_tipo_persona = 'P' ) ");
            sql.append("\n      ELSE m.beneficiario END as beneficiario, ");
            sql.append("\n m.concepto,m.no_folio_det,m.no_empresa, ");
            sql.append("\n m.id_chequera,convert(char,m.fec_valor, 103) as fec_valor,");
            sql.append("\n m.id_banco_benef,");
            sql.append("\n coalesce(m.plaza, 0) as plaza_benef,");
            sql.append("\n c.desc_plaza as plaza,");
            sql.append("\n m.no_cliente ,");
            sql.append("\n  b2.desc_banco_inbursa, ");
            sql.append("\n  m.clabe as clabe_benef,  ");
            sql.append("\n  '' as aba,'' as swift_code ");
            
            sql.append("\n  ,'' as aba_intermediario, '' as Swift_intermediario ");
            sql.append("\n  ,'' as Aba_corresponsal, '' as Swift_corresponsal ");
            sql.append("\n  ,'' as nom_banco_intermediario ");
            sql.append("\n  ,'' as nom_banco_corresponsal ");
            sql.append("\n  ,b.valida_CLABE,e.id_contrato_mass,e.id_contrato_wlink ");
            sql.append("\n  ,case when m.origen_mov = 'FIL' then 'AAA999999AAA' ");  //'RFC Moral comodin
            sql.append("\n             else 'AAAA999999XXX' ");  //'Para origen FYE incidentales 'RFC Fisico comodin
            sql.append("\n  end as rfc_benef ");
            sql.append("\n  ,m.no_factura,e.nom_empresa ");
            sql.append("\n  ,'S' as existe_chequera_prov, pp.rfc ");
            
            if(pbH2HBital)
                sql.append("\n,u.desc_usuario_bital,s.desc_servicio_bital");
            
            sql.append("\n ,'' as especiales, '' as equivale_persona, ''  as complemento, 0 as tipo_envio_layout");
            
            //'MAS 04/09/02007 se agrega para validar la divisa de la chequera de pago contra la del docto
            sql.append("\n,(select id_divisa from cat_cta_banco ccb where ccb.no_empresa = m.no_empresa ");
            sql.append("\n   and ccb.id_banco = m.id_banco and ccb.id_chequera = m.id_chequera) as divisa_chequera");
            
            sql.append("\n   ,d.ciudad + ', ' + d.id_estado as direccion_benef , (select pa.desc_pais from cat_pais pa where pa.id_pais = 'MX') as pais_benef, m.cve_control as clave, m.no_folio_mov");
            
            
            ///
            sql.append(" From  Cat_Cve_Operacion O, Cat_Banco B,"
            		+ "movimiento M ");
            sql.append(" Left Join Cat_Banco B2 On ( M.Id_Banco_Benef = B2.Id_Banco )");
            sql.append(" Left Outer Join direccion D On (m.no_empresa = d.no_persona "
            		+ "and d.id_tipo_direccion = 'OFNA'"
            		+ "and d.no_direccion = 1"
            		+ "and d.id_tipo_persona = 'P'"
            		+ "and d.no_empresa = 552"
            		+ ")");		
            sql.append("Left Outer Join persona pp on (pp.no_persona = d.no_persona),"
            		+ "cat_cta_banco c , empresa e");
            ///
           
            if(pbH2HBital) 
                sql.append("\n  ,cat_usuario_bital u, cat_servicio_bital s");
            
            sql.append("\n  WHERE ");
            sql.append("\n  m.no_empresa = e.no_empresa ");
            
            if(pbH2HBital)
            {
                sql.append("\n  and e.id_usuario_bital *= u.id_usuario_bital ");
                sql.append("\n  and e.id_servicio_bital *= s.id_servicio_bital ");
            }
            //'de Luis
            sql.append("\n  and m.no_empresa = pp.no_empresa ");
            sql.append("\n  and m.no_empresa = pp.no_persona ");
            sql.append("\n  and pp.id_tipo_persona = 'E'");
            
            
            sql.append("\n  and ((b.transfer_casa_cambio = 'S' and m.id_tipo_operacion = 3000 and m.id_estatus_mov = 'V' And (m.id_divisa <> 'DLS' And m.id_divisa <> 'MN')) OR m.id_estatus_mov = 'P') ");
            sql.append("\n  and m.id_forma_pago = 3 ");
            sql.append("\n  AND m.id_cve_operacion = o.id_cve_operacion ");
            sql.append("\n  AND m.id_banco = b.id_banco");
            sql.append("\n  AND m.id_banco = c.id_banco");
            sql.append("\n  AND m.id_chequera = c.id_chequera AND m.id_tipo_movto = 'E'");
            sql.append("\n  AND b.b_banca_elect  in ('A','E')  ");
            sql.append("\n  and m.id_banco = " + Utilerias.validarCadenaSQL(dtoBus.getIdBanco()));
            
            if(dtoBus.isChkTodasEmpresas())
                sql.append("\n  AND m.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " +Utilerias.validarCadenaSQL(dtoBus.getIdUsuario())+ ")");
            else
                //sql.append("\n  AND m.no_empresa = " +dtoBus.getIdEmpresa());
            	sql.append("\n  AND m.no_empresa in (Select No_Empresa From Grupo_Empresa Where Id_Grupo_Flujo = " +Utilerias.validarCadenaSQL(dtoBus.getIdEmpresa())+") ");
            
            if(dtoBus.getIdDivisa()!=null && !dtoBus.getIdDivisa().equals(""))
            {
                sql.append("\n  AND CASE WHEN coalesce(b.transfer_casa_cambio, 'N') = 'S' and m.origen_mov='CVT' ");
                sql.append("\n           THEN m.id_divisa_original ");
                sql.append("\n           ELSE m.id_divisa END = '" +Utilerias.validarCadenaSQL(dtoBus.getIdDivisa())+ "' ");
            }
            
            if(dtoBus.getOptComerica()!=null && dtoBus.getOptComerica().trim().equals("ACH"))
                sql.append("\n  AND m.id_servicio_be = 'ACH' ");
            
            if(dtoBus.isChkSoloLocales())
                sql.append("\n  AND e.b_local = 'S' ");
            
                      
            sql.append(agregarCriteriosBusqueda(dtoBus.getMontoIni(),dtoBus.getMontoFin(),dtoBus.getPlBancoReceptor(),
            		dtoBus.getPsFechaValor(),dtoBus.getPsFechaValorOrig(),dtoBus.getIdDivisa(),dtoBus.getPsDivision()));
            
            if(dtoBus.getOptTipoEnvio()!=null && dtoBus.getOptTipoEnvio().trim().equals("SPEUA"))
            {
                sql.append("\n  and m.id_banco <> m.id_banco_benef ");
                sql.append("\n  and m.importe >= 50000 ");
            }
            else if(dtoBus.getOptTipoEnvio()!=null && dtoBus.getOptTipoEnvio().trim().equals("Mismo"))
                    sql.append("\n  and m.id_banco = m.id_banco_benef");
            else if (dtoBus.getOptTipoEnvio()!=null && dtoBus.getOptTipoEnvio().trim().equals("Internacional"))
            {
               sql.append("\n AND b.nac_ext = 'N' and b2.nac_ext = 'E'");
               sql.append("\n AND m.id_banco <> m.id_banco_benef");
            }else{
               sql.append("\n AND ((b.nac_ext = 'N' and b2.nac_ext = 'N')");
               sql.append("\n or (b2.nac_ext = 'N' and b.nac_ext = 'N'))");
               sql.append("\n AND m.id_banco <> m.id_banco_benef");
            }
            
            sql.append("\n  AND m.origen_mov = 'FIL' ");
            if (dtoBus.isNomina()){
                sql.append("\n  AND m.origen_mov = 'PRO' ");
            } else {

                sql.append("\n  AND m.origen_mov <> 'PRO' ");
            }
            
           // sql.append("\n   AND m.no_empresa = d.no_persona");
           // sql.append("\n   and pp.no_persona = d.no_persona");
            
           // sql.append("\n  and d.id_tipo_direccion = 'OFNA' ");
           // sql.append("\n  and d.no_direccion = 1 ");
            
           // sql.append("\n  and d.id_tipo_persona = 'P' ");
            //sql.append("\n  and d.no_empresa = 552 ");
            
            if (!dtoBus.getValidaCampo().equals("0")) {
        		sql.append("\n  and m.cve_control = '");
                sql.append(dtoBus.getValidaCampo());
                sql.append("'");
            }

            
            if (dtoBus.isChkConvenioSant()) {
            	sql.append("\n AND m.id_chequera_benef like 'SANT%' ");
            } else {
            	sql.append("\n AND m.id_chequera_benef not like 'SANT%' ");
            }

            if (dtoBus.isChkDebito()) {
            	sql.append("\n AND LEN(m.id_chequera_benef) = 16 ");
            } else {
            	sql.append("\n AND LEN(m.id_chequera_benef) <> 16 ");
            }
            sql.append("\n  ORDER BY importe ");

            System.out.println("\n consulta pagos"+sql);
		    
		    list= jdbcTemplate.query(sql.toString(), new RowMapper <MovimientoDto>(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons = new MovimientoDto();
					//cons.setFecValorOriginal(rs.getDate("fec_valor_original"));
					cons.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
					cons.setIdBancoCityStr(rs.getString("banco_city"));
					cons.setInstFinan(rs.getString("inst_finan"));
					cons.setObservacion(rs.getString("observacion"));
					cons.setSucOrigen(rs.getString("suc_origen"));
					cons.setSucDestino(rs.getString("suc_destino"));
					cons.setIdBanco(rs.getInt("id_banco"));
					cons.setBLayoutComerica(rs.getString("b_layout_comerica"));
					cons.setNoDocto(rs.getString("no_docto"));
					//cons.setFecOperacion(rs.getDate("fec_operacion"));
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
					//cons.setFecValor(rs.getDate("fec_valor")); 
					cons.setIdBancoBenef(rs.getInt("id_banco_benef"));
					cons.setPlazaBenef(rs.getString("plaza_benef"));
					//cons.setPlaza(rs.getInt("plaza"));
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
					cons.setDireccionBenef(rs.getString("direccion_benef"));
					cons.setPaisBenef(rs.getString("pais_benef"));
					cons.setClave(rs.getString("clave"));
					cons.setIdChequeraBenefReal(obtenerChequeraBenefOficial(rs.getInt("id_banco_benef"), rs.getString("suc_destino"), 
						rs.getString("plaza_benef"), rs.getString("id_chequera_benef"), rs.getInt("id_banco")));
					cons.setHoraRecibo(funciones.obtenerHoraActual(false).substring(0, 5));
					cons.setDescUsuarioBital(pbH2HBital ? rs.getString("desc_usuario_bital") : "");
					cons.setDescServicioBital(pbH2HBital ? rs.getString("desc_servicio_bital") : "");
					cons.setNoFolioMov(rs.getInt("no_folio_mov"));
					
					cons.setFecValorStr(rs.getString("fec_valor"));
					cons.setFecValorOriginalStr(rs.getString("fec_valor_original"));
					cons.setFecOperacionStr(rs.getString("fec_operacion"));
					return cons;
				}
		    });
		     
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
		System.out.println("consultar ya ejecutados "+sql);
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
	
	public int insertarDetArchTransfer(DetArchTransferDto dto){
		StringBuffer sql = new StringBuffer();
		String nomArchivo = "";
		int res=0;
		
		try{
			nomArchivo = dto.getNomArch();/*.replace("", "");
			*nomArchivo = funciones.elmiminarEspaciosAlaDerecha(nomArchivo.replace(".in", ""));
			
			if (nomArchivo.length() > 20) {
				nomArchivo = nomArchivo.substring(0, 19);
			}
			*/
		    sql.append("insert into det_arch_transfer (nom_arch,no_folio_det,no_docto,id_estatus_arch, fec_valor , ");
		    sql.append("id_chequera, id_banco, id_banco_benef, id_chequera_benef,prefijo_benef,");
		    sql.append("importe,beneficiario,sucursal,plaza,concepto)");
		    sql.append(" values( ");
		    
		    sql.append("'"+ Utilerias.validarCadenaSQL(nomArchivo) +"',"+dto.getNoFolioDet()+", '"+Utilerias.validarCadenaSQL(dto.getNoDocto())+"', '"+Utilerias.validarCadenaSQL(dto.getIdEstatusArch())+"', ");
    		sql.append("\n	"+"convert(datetime, '"+Utilerias.validarCadenaSQL(funciones.ponerFormatoDate(dto.getFecValorDate()))+"', 103), '"+Utilerias.validarCadenaSQL(dto.getIdChequera())+"',"+dto.getIdBanco()+","+dto.getIdBancoBenef()+",");
			sql.append("\n	"+"'"+Utilerias.validarCadenaSQL(dto.getIdChequeraBenef())+
							"', '"+Utilerias.validarCadenaSQL(dto.getPrefijoBenef())+
							"',"+ dto.getImporte()+
							",'"+Utilerias.validarCadenaSQL(dto.getBeneficiario())+
							"',"+dto.getSucursal()+"," );
			sql.append("\n	"+dto.getPlaza()+",'"+Utilerias.validarCadenaSQL(dto.getConcepto())+"')");
			
			System.out.println("insertarDetArchTransfer "+sql);
			
		    res = jdbcTemplate.update(sql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
						+ "P:BancaElectr�nica, C:EnvioTransferenciasDao, M:insertarDetArchTransfer");
		}
		return res;
	}

	//revisado A.A.G
	public int insertarArchTransfer(ArchTransferDto dto){
		StringBuffer sql = new StringBuffer();
		int res=0;
		try{
			sql.append(" INSERT INTO arch_transfer (nom_arch, id_banco, fec_trans, importe, registros, ");
	        sql.append(" \n	fec_retrans, no_usuario_alta, no_usuario_modif, b_cheque_ocurre, tipo_envio_layout) ");
	        sql.append(" \n	values(?,?,?,?,?,?,?,?,?,?)");
	        
//	        System.out.println("insertarArchTranfer"+sql.toString());
	        
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
	public int actualizarMovimientoTipoEnvio(String tipoEnvio, String nomArchivos){
		StringBuffer sql = new StringBuffer();
		int plResp=0;
		String psArchivos=nomArchivos;
		try{
			psArchivos = nomArchivos;/*.replaceAll(".txt","");
			psArchivos = psArchivos.replaceAll(".in","");
			psArchivos = psArchivos.replace(".sha","");
			psArchivos = psArchivos.replace(".slk","");*/
			

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
			sql.append(") and id_estatus_mov = 'P' ");
			
			
			plResp = jdbcTemplate.update(sql.toString());
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BancaElectr�nica, C:EnvioTransferenciasDao, M:actualizarMovimientoTipoEnvio");
		}
		return plResp;
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
			psArchivos = psNomArchivos;/*.replace(".txt", "");
			psArchivos = psArchivos.replace(".sha", "");
			psArchivos = psArchivos.replace(".slk", "");*/
			
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
			System.out.println("Esta es la consulta del reporte de transferencias------>  "+sql);
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
					map.put("beneficiario", rs.getString("beneficiario"));
					map.put("clabeBenef", rs.getString("clabe_benef") != null ? rs.getString("clabe_benef") : "");
					map.put("idDivisa", rs.getString("id_divisa"));
	    			return map;
				}
			});
	    }catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:EnvioTransferenciasDao, M:reporteDetArchivoTransf");
			e.printStackTrace();
		}
		return listResult;
	}
	
	public String reporteControlArchivo(String psTabla, String psArchivo) {
        StringBuffer sql = new StringBuffer();
        //psArchivo = psArchivo.substring(0, psArchivo.indexOf("."));

        try{
            //PAGO PROVEEDORES
            sql.append("  SELECT distinct a.no_folio_det,m.fec_valor_original,a.nom_arch, M.no_docto as no_docto, a.id_estatus_arch, ");
            //AT PARA LOS DOCUMENTOS DE MASSPAYMENT <50000 INTERBANCARIOS LA FECHA VALOR ES MA�ANA
            sql.append("  CASE WHEN nom_arch like 'DESC%' and m.id_banco in(2) ");
            sql.append("        and m.importe<50000 and m.id_banco_benef not in(2,12) and m.id_divisa='MN' then ");

            if(psTabla.equals("movimiento"))
                sql.append("    (select fec_manana from fechas) ");
            else {
              if(gsDBM.equals("DB2")) {
                  sql.append("    CASE WHEN dayname(a.fec_valor + 1 day)='S�bado' then  ");
                  sql.append("                a.fec_valor + 2 day");
                  sql.append("   ELSE a.fec_valor + 1 day END ");
              }else {
                  sql.append("    CASE WHEN datename(dw,dateadd(day,1,a.fec_valor))='S�bado' then  ");
                  sql.append("                DateAdd(day, 2, a.fec_valor)");
                  sql.append("   ELSE dateadd(day, 1,a.fec_valor) END ");
              }
            }
            sql.append("  ELSE a.fec_valor END as fec_valor ");
            sql.append("       ,a.id_banco, a.id_banco_benef, ");
            sql.append("      a.id_chequera, a.id_chequera_benef, a.importe, ");
            sql.append("      a.prefijo_benef, a.sucursal, a.plaza, a.beneficiario, ");
            sql.append("      c.id_clabe as clabe_benef, ");
            sql.append("      CASE WHEN ");
            sql.append("          ( SELECT coalesce(b.transfer_casa_cambio, 'N') ");
            sql.append("            FROM cat_banco b ");
            sql.append("            WHERE b.id_banco = m.id_banco ) = 'S' ");
            sql.append("      THEN m.id_divisa_original ");
            sql.append("      ELSE m.id_divisa END as id_divisa ");
            sql.append("  FROM det_arch_transfer a," + psTabla + " m, ctas_banco c ");
            sql.append("  WHERE a.no_folio_det = m.no_folio_det ");

            if(gsDBM.equals("POSTGRESQL"))
                sql.append("  AND TO_NUMBER(m.no_cliente,'99999999999') = c.no_persona ");
            else if(gsDBM.equals("DB2"))
                sql.append("  AND cast(m.no_cliente as integer) = c.no_persona ");
            else
                sql.append("  AND cast(m.no_cliente as integer) = c.no_persona ");


            sql.append("      AND m.id_banco_benef = c.id_banco ");
            sql.append("      AND m.id_chequera_benef = c.id_chequera ");
            sql.append("      AND id_estatus_arch <> 'X' ");
            sql.append("      AND nom_arch = '" + Utilerias.validarCadenaSQL(psArchivo) + "' ");
            sql.append("      AND m.origen_mov <> 'FIL'");
            sql.append("      AND no_cliente not in (" + consultarConfiguraSet(206) + ")");
            sql.append("      AND m.id_tipo_operacion NOT BETWEEN 3700 and 3799 ");

            sql.append("  UNION ALL ");  //PAGO FILIALES O INCIDENTALES

            sql.append("  SELECT distinct a.no_folio_det,m.fec_valor_original,a.nom_arch, M.Po_Headers as no_docto, ");
            sql.append("      a.id_estatus_arch, ");
            //'AT PARA LOS DOCUMENTOS DE MASSPAYMENT <50000 INTERBANCARIOS LA FECHA VALOR ES MA�ANA
            sql.append("  CASE WHEN nom_arch like 'DESC%' and m.id_banco in(2) ");
            sql.append("        and m.importe<50000 and m.id_banco_benef not in(2,12) and m.id_divisa='MN' then ");
            if(psTabla.equals("movimiento"))
                sql.append("    (select fec_manana from fechas) ");
            else {
              if(gsDBM.equals("DB2")) {
                  sql.append("    CASE WHEN dayname(a.fec_valor + 1 day)='S�bado' then  ");
                  sql.append("                a.fec_valor + 2 day");
                  sql.append("   ELSE a.fec_valor + 1 day END ");
              }else {
                  sql.append("    CASE WHEN datename(dw,dateadd(day,1,a.fec_valor))='S�bado' then  ");
                  sql.append("                DateAdd(day, 2, a.fec_valor)");
                  sql.append("   ELSE dateadd(day, 1,a.fec_valor) END ");
              }
           }
            sql.append("  ELSE a.fec_valor END as fec_valor ");
            sql.append("     ,a.id_banco, a.id_banco_benef, ");
            sql.append("      a.id_chequera, a.id_chequera_benef, a.importe, ");
            sql.append("      a.prefijo_benef, a.sucursal, a.plaza, a.beneficiario, ");
            sql.append("      m.clabe as clabe_benef, ");
            sql.append("      CASE WHEN ");
            sql.append("          ( SELECT coalesce(b.transfer_casa_cambio, 'N') ");
            sql.append("            FROM cat_banco b ");
            sql.append("            WHERE b.id_banco = m.id_banco ) = 'S' ");
            sql.append("      THEN m.id_divisa_original ");
            sql.append("      ELSE m.id_divisa END as id_divisa ");
            sql.append("  FROM det_arch_transfer a," + psTabla + " m ");
            sql.append("  WHERE a.no_folio_det = m.no_folio_det ");
            sql.append("      AND id_estatus_arch <> 'X' ");
            sql.append("      AND nom_arch = '" + psArchivo + "' ");
            sql.append("      AND (m.origen_mov = 'FIL' ");
            sql.append("          OR no_cliente in (" + consultarConfiguraSet(206)+ ") )");

            sql.append("  UNION ALL ");  //'TRAPASOS

            sql.append("  SELECT distinct a.no_folio_det,m.fec_valor_original,a.nom_arch, M.Po_Headers as no_docto, ");
            sql.append("      a.id_estatus_arch, ");
            //'AT PARA LOS DOCUMENTOS DE MASSPAYMENT <50000 INTERBANCARIOS LA FECHA VALOR ES MA�ANA
            sql.append("  CASE WHEN nom_arch like 'DESC%' and m.id_banco in(2) ");
            sql.append("        and m.importe<50000 and m.id_banco_benef not in(2,12) and m.id_divisa='MN' then ");
            if(psTabla.equals("movimiento"))
                sql.append("    (select fec_manana from fechas) ");
            else {
              if(gsDBM.equals("DB2")) {
                  sql.append("    CASE WHEN dayname(a.fec_valor + 1 day)='S�bado' then  ");
                  sql.append("                a.fec_valor + 2 day");
                  sql.append("   ELSE a.fec_valor + 1 day END ");
              }else {
                  sql.append("    CASE WHEN datename(dw,dateadd(day,1,a.fec_valor))='S�bado' then  ");
                  sql.append("                DateAdd(day, 2, a.fec_valor)");
                  sql.append("   ELSE dateadd(day, 1,a.fec_valor) END ");
              }
            }
            sql.append("  ELSE a.fec_valor END as fec_valor ");
            sql.append("       , a.id_banco, a.id_banco_benef, ");
            sql.append("      a.id_chequera, a.id_chequera_benef, a.importe, ");
            sql.append("      a.prefijo_benef, a.sucursal, a.plaza, a.beneficiario, ");
            sql.append("      c.id_clabe as clabe_benef, ");
            sql.append("      CASE WHEN ");
            sql.append("          ( SELECT coalesce(b.transfer_casa_cambio, 'N') ");
            sql.append("            FROM cat_banco b ");
            sql.append("            WHERE b.id_banco = m.id_banco ) = 'S' ");
            sql.append("      THEN m.id_divisa_original ");
            sql.append("      ELSE m.id_divisa END as id_divisa ");
            sql.append("  FROM det_arch_transfer a," + psTabla + "  m,cat_cta_banco c ");
            sql.append("  WHERE a.no_folio_det = m.no_folio_det ");

            if(gsDBM.equals("POSTGRESQL"))
                sql.append("  AND TO_NUMBER(m.no_cliente,'99999999999') = c.no_empresa ");
            else if(gsDBM.equals("DB2"))
                sql.append("  AND cast(m.no_cliente as integer) = c.no_empresa ");
            else
                sql.append("  AND cast(m.no_cliente as integer) = c.no_empresa ");

            sql.append("      AND m.id_banco_benef = c.id_banco ");
            sql.append("      AND m.id_chequera_benef = c.id_chequera ");
            sql.append("      AND a.id_estatus_arch <> 'X' ");
            sql.append("      AND a.nom_arch = '" + Utilerias.validarCadenaSQL(psArchivo) + "' ");
            sql.append("      AND coalesce(m.origen_mov,'') <> 'FIL' ");
            sql.append("      AND no_cliente not in (" + consultarConfiguraSet(206) + ") ");
            sql.append("      AND m.id_tipo_operacion BETWEEN 3700 and 3899 ");
            
            
            sql.append("  UNION ALL "); 
            
            sql.append("  SELECT distinct a.no_folio_det,m.fec_valor_original,a.nom_arch, M.Po_Headers as no_docto, a.id_estatus_arch, ");
            //AT PARA LOS DOCUMENTOS DE MASSPAYMENT <50000 INTERBANCARIOS LA FECHA VALOR ES MA�ANA
            sql.append("  CASE WHEN nom_arch like 'DESC%' and m.id_banco in(2) ");
            sql.append("        and m.importe<50000 and m.id_banco_benef not in(2,12) and m.id_divisa='MN' then ");

            if(psTabla.equals("movimiento"))
                sql.append("    (select fec_manana from fechas) ");
            else {
              if(gsDBM.equals("DB2")) {
                  sql.append("    CASE WHEN dayname(a.fec_valor + 1 day)='S�bado' then  ");
                  sql.append("                a.fec_valor + 2 day");
                  sql.append("   ELSE a.fec_valor + 1 day END ");
              }else {
                  sql.append("    CASE WHEN datename(dw,dateadd(day,1,a.fec_valor))='S�bado' then  ");
                  sql.append("                DateAdd(day, 2, a.fec_valor)");
                  sql.append("   ELSE dateadd(day, 1,a.fec_valor) END ");
              }
            }
            sql.append("  ELSE a.fec_valor END as fec_valor ");
            sql.append("       ,a.id_banco, a.id_banco_benef, ");
            sql.append("      a.id_chequera, a.id_chequera_benef, a.importe, ");
            sql.append("      a.prefijo_benef, a.sucursal, a.plaza, a.beneficiario, ");
            sql.append("      c.id_clabe as clabe_benef, ");
            sql.append("      CASE WHEN ");
            sql.append("          ( SELECT coalesce(b.transfer_casa_cambio, 'N') ");
            sql.append("            FROM cat_banco b ");
            sql.append("            WHERE b.id_banco = m.id_banco ) = 'S' ");
            sql.append("      THEN m.id_divisa_original ");
            sql.append("      ELSE m.id_divisa END as id_divisa ");
            sql.append("  FROM det_arch_transfer a, hist_movimiento m, ctas_banco c ");
            sql.append("  WHERE a.no_folio_det = m.no_folio_det ");

            if(gsDBM.equals("POSTGRESQL"))
                sql.append("  AND TO_NUMBER(m.no_cliente,'99999999999') = c.no_persona ");
            else if(gsDBM.equals("DB2"))
                sql.append("  AND cast(m.no_cliente as integer) = c.no_persona ");
            else
                sql.append("  AND cast(m.no_cliente as integer) = c.no_persona ");


            sql.append("      AND m.id_banco_benef = c.id_banco ");
            sql.append("      AND m.id_chequera_benef = c.id_chequera ");
            sql.append("      AND id_estatus_arch <> 'X' ");
            sql.append("      AND nom_arch = '" + Utilerias.validarCadenaSQL(psArchivo) + "' ");
            sql.append("      AND m.origen_mov <> 'FIL'");
            sql.append("      AND no_cliente not in (" + consultarConfiguraSet(206) + ")");
            sql.append("      AND m.id_tipo_operacion NOT BETWEEN 3700 and 3799 ");

            sql.append("  UNION ALL ");  //PAGO FILIALES O INCIDENTALES

            sql.append("  SELECT distinct a.no_folio_det,m.fec_valor_original,a.nom_arch, M.Po_Headers as no_docto, ");
            sql.append("      a.id_estatus_arch, ");
            //'AT PARA LOS DOCUMENTOS DE MASSPAYMENT <50000 INTERBANCARIOS LA FECHA VALOR ES MA�ANA
            sql.append("  CASE WHEN nom_arch like 'DESC%' and m.id_banco in(2) ");
            sql.append("        and m.importe<50000 and m.id_banco_benef not in(2,12) and m.id_divisa='MN' then ");
            if(psTabla.equals("movimiento"))
                sql.append("    (select fec_manana from fechas) ");
            else {
              if(gsDBM.equals("DB2")) {
                  sql.append("    CASE WHEN dayname(a.fec_valor + 1 day)='S�bado' then  ");
                  sql.append("                a.fec_valor + 2 day");
                  sql.append("   ELSE a.fec_valor + 1 day END ");
              }else {
                  sql.append("    CASE WHEN datename(dw,dateadd(day,1,a.fec_valor))='S�bado' then  ");
                  sql.append("                DateAdd(day, 2, a.fec_valor)");
                  sql.append("   ELSE dateadd(day, 1,a.fec_valor) END ");
              }
           }
            sql.append("  ELSE a.fec_valor END as fec_valor ");
            sql.append("     ,a.id_banco, a.id_banco_benef, ");
            sql.append("      a.id_chequera, a.id_chequera_benef, a.importe, ");
            sql.append("      a.prefijo_benef, a.sucursal, a.plaza, a.beneficiario, ");
            sql.append("      m.clabe as clabe_benef, ");
            sql.append("      CASE WHEN ");
            sql.append("          ( SELECT coalesce(b.transfer_casa_cambio, 'N') ");
            sql.append("            FROM cat_banco b ");
            sql.append("            WHERE b.id_banco = m.id_banco ) = 'S' ");
            sql.append("      THEN m.id_divisa_original ");
            sql.append("      ELSE m.id_divisa END as id_divisa ");
            sql.append("  FROM det_arch_transfer a, hist_movimiento m ");
            sql.append("  WHERE a.no_folio_det = m.no_folio_det ");
            sql.append("      AND id_estatus_arch <> 'X' ");
            sql.append("      AND nom_arch = '" + psArchivo + "' ");
            sql.append("      AND (m.origen_mov = 'FIL' ");
            sql.append("          OR no_cliente in (" + consultarConfiguraSet(206)+ ") )");

            sql.append("  UNION ALL ");  //'TRAPASOS

            sql.append("  SELECT distinct a.no_folio_det,m.fec_valor_original,a.nom_arch, M.Po_Headers as no_docto, ");
            sql.append("      a.id_estatus_arch, ");
            //'AT PARA LOS DOCUMENTOS DE MASSPAYMENT <50000 INTERBANCARIOS LA FECHA VALOR ES MA�ANA
            sql.append("  CASE WHEN nom_arch like 'DESC%' and m.id_banco in(2) ");
            sql.append("        and m.importe<50000 and m.id_banco_benef not in(2,12) and m.id_divisa='MN' then ");
            if(psTabla.equals("movimiento"))
                sql.append("    (select fec_manana from fechas) ");
            else {
              if(gsDBM.equals("DB2")) {
                  sql.append("    CASE WHEN dayname(a.fec_valor + 1 day)='S�bado' then  ");
                  sql.append("                a.fec_valor + 2 day");
                  sql.append("   ELSE a.fec_valor + 1 day END ");
              }else {
                  sql.append("    CASE WHEN datename(dw,dateadd(day,1,a.fec_valor))='S�bado' then  ");
                  sql.append("                DateAdd(day, 2, a.fec_valor)");
                  sql.append("   ELSE dateadd(day, 1,a.fec_valor) END ");
              }
            }
            sql.append("  ELSE a.fec_valor END as fec_valor ");
            sql.append("       , a.id_banco, a.id_banco_benef, ");
            sql.append("      a.id_chequera, a.id_chequera_benef, a.importe, ");
            sql.append("      a.prefijo_benef, a.sucursal, a.plaza, a.beneficiario, ");
            sql.append("      c.id_clabe as clabe_benef, ");
            sql.append("      CASE WHEN ");
            sql.append("          ( SELECT coalesce(b.transfer_casa_cambio, 'N') ");
            sql.append("            FROM cat_banco b ");
            sql.append("            WHERE b.id_banco = m.id_banco ) = 'S' ");
            sql.append("      THEN m.id_divisa_original ");
            sql.append("      ELSE m.id_divisa END as id_divisa ");
            sql.append("  FROM det_arch_transfer a,hist_solicitud  m,cat_cta_banco c ");
            sql.append("  WHERE a.no_folio_det = m.no_folio_det ");

            if(gsDBM.equals("POSTGRESQL"))
                sql.append("  AND TO_NUMBER(m.no_cliente,'99999999999') = c.no_empresa ");
            else if(gsDBM.equals("DB2"))
                sql.append("  AND cast(m.no_cliente as integer) = c.no_empresa ");
            else
                sql.append("  AND cast(m.no_cliente as integer) = c.no_empresa ");

            sql.append("      AND m.id_banco_benef = c.id_banco ");
            sql.append("      AND m.id_chequera_benef = c.id_chequera ");
            sql.append("      AND a.id_estatus_arch <> 'X' ");
            sql.append("      AND a.nom_arch = '" + Utilerias.validarCadenaSQL(psArchivo) + "' ");
            sql.append("      AND coalesce(m.origen_mov,'') <> 'FIL' ");
            sql.append("      AND no_cliente not in (" + consultarConfiguraSet(206) + ") ");
            sql.append("      AND m.id_tipo_operacion BETWEEN 3700 and 3899 ");
            
            
            sql.append("order by no_docto");
        }catch(Exception e){
            bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectr�nica, C:EnvioTransferenciasDao, M:reporteControlArchivo");
        }
        return sql.toString();
    }
	
	public int buscaDatosBenef(List<Map<String, String>> objParams, int i) {
		StringBuffer sql = new StringBuffer();
		int res = 0;
		
		try {
			if (!objParams.get(i).get("noCliente").equals("999999") && !objParams.get(i).get("noCliente").equals("999998")) {
				sql.append("SELECT count(*) FROM ctas_banco \n");
				sql.append("WHERE no_persona = "+ objParams.get(i).get("noCliente") +" \n");
				sql.append("	And id_banco = "+ objParams.get(i).get("idBancoBenef") +" \n");
				sql.append("	And id_chequera = '"+ Utilerias.validarCadenaSQL(objParams.get(i).get("idChequeraBenef")) +"'");
				
				res = jdbcTemplate.queryForInt(sql.toString());
			} else res = 1;
			
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
	
	public int numeroContrato(int idBanco, int idContrato) {
		StringBuffer sql = new StringBuffer();
		int res = 0;
		
		try {						
			sql.append(" select top 1 cod_cliente \n");
			sql.append(" from cat_contrato \n");
			sql.append(" where id_banco = " + idBanco + " \n");
			sql.append(" and no_contrato = " + idContrato + " \n");
			
			res = jdbcTemplate.queryForInt(sql.toString());
			return res;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectr�nica, C:EnvioTransferenciasDao, M:actualizaMovtoCheqBenef");
			e.printStackTrace();
			return res;
		}
	}
	public int consecutivoContrato(int idBanco, int idContrato) {
		StringBuffer sql = new StringBuffer();
		int res = 0;
		
		try {			
			sql.append("UPDATE cat_contrato SET consecutivo_dia = consecutivo_dia + 1 \n");
			sql.append(" where id_banco = " + idBanco + " \n");
			sql.append(" and no_contrato = " + idContrato + " \n");
			
			res = jdbcTemplate.update(sql.toString());			
			
			sql = new StringBuffer();
			
			sql.append(" select top 1 consecutivo_dia \n");
			sql.append(" from cat_contrato \n");
			sql.append(" where id_banco = " + idBanco + " \n");
			sql.append(" and no_contrato = " + idContrato + " \n");
			
			res = jdbcTemplate.queryForInt(sql.toString());
			return res;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectr�nica, C:EnvioTransferenciasDao, M:actualizaMovtoCheqBenef");
			e.printStackTrace();
			return res;
		}
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
	public String obtenerComando() {
		return consultasGenerales.consultarConfiguraSet(500);
	}
}
