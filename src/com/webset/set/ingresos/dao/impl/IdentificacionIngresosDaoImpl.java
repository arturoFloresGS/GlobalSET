package com.webset.set.ingresos.dao.impl;

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

import com.webset.set.conciliacionbancoset.dto.ConciliaBancoDto;
import com.webset.set.conciliacionbancoset.dto.CriteriosBusquedaDto;
import com.webset.set.ingresos.dao.IdentificacionIngresosDao;
import com.webset.set.ingresos.dto.CuentaContableDto;
import com.webset.set.seguridad.dto.PersonaDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;

public class IdentificacionIngresosDaoImpl implements IdentificacionIngresosDao{

	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora=new Bitacora();
	ConsultasGenerales consultasGenerales;
	Funciones funciones= new Funciones();
	GlobalSingleton globalSingleton;
	
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:setDataSource");
		}
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<LlenaComboGralDto> llenarComboBancos(int noEmpresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			if(noEmpresa == 0){
				sbSQL.append("\n Select id_banco as ID, desc_banco as Descrip From cat_banco");
		        sbSQL.append("\n Where id_banco in (Select distinct id_banco ");
		        sbSQL.append("\n from cat_cta_banco where ");
		        sbSQL.append("\n tipo_chequera not in(" + globalSingleton.obtenerValorConfiguraSet(202) + ")");
		        sbSQL.append("\n  ) ");
		        sbSQL.append("\n Order By desc_banco ");
			}else{
				sbSQL.append("\n Select id_banco as ID, desc_banco as Descrip From cat_banco");
		        sbSQL.append("\n Where id_banco in (Select distinct id_banco ");
		        sbSQL.append("\n from cat_cta_banco where ");
		        sbSQL.append("\n tipo_chequera not in(" + globalSingleton.obtenerValorConfiguraSet(202) + ") ");
		        sbSQL.append("\n and no_empresa = " + noEmpresa + "  )");
		        sbSQL.append("\n Order By desc_banco ");
			}
			
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("Descrip"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Ingresos, C:IdentificacionIngresosDaoImpl, M:llenarComboBancos");
		}
		return list;
	}
	
	@Override
	public List<CuentaContableDto> getCuentaContable(int noEmpresa, String idGrupo, String idRubro) {

		List<CuentaContableDto> list = new ArrayList<CuentaContableDto>();
		
		StringBuffer sbSQL = new StringBuffer();
		
		try{
			
			sbSQL.append(" Select * From guia_contable where no_empresa = "+ noEmpresa +" And ");
			sbSQL.append(" id_grupo = " + idGrupo + " And id_rubro = " + idRubro );
			
		      list = jdbcTemplate.query(sbSQL.toString(), new RowMapper<CuentaContableDto>(){
					public CuentaContableDto mapRow(ResultSet rs, int idx) throws SQLException {
						CuentaContableDto cons = new CuentaContableDto();
						cons.setCuentaContable(rs.getString("cuenta_contable"));
						return cons;
					}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Ingresos, C:IdentificacionIngresosDaoImpl, M:getCuentaContable");
		}
		return list;
	}

	@Override
	public List<LlenaComboChequeraDto> llenarCmbChequeras(int iBanco, int iEmpresa, int iOpc) {
		List<LlenaComboChequeraDto> list = new ArrayList<LlenaComboChequeraDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			
			sbSQL.append("\n  Select");
	        sbSQL.append("\n  id_chequera as ID,");
	        sbSQL.append("\n  id_chequera as Descrip");
	        sbSQL.append("\n  From ");
	        sbSQL.append("\n  cat_cta_banco");
	        sbSQL.append("\n  Where ");
	        sbSQL.append("\n  id_banco = " + iBanco);
	        sbSQL.append("\n  and no_empresa = " + iEmpresa);
	        if(iOpc == 0)
	        	sbSQL.append("\n  AND tipo_chequera not in(" + globalSingleton.obtenerValorConfiguraSet(202) + ") ");
	        sbSQL.append("\n  Order By id_chequera ");   
	       
		      list = jdbcTemplate.query(sbSQL.toString(), new RowMapper<LlenaComboChequeraDto>(){
					public LlenaComboChequeraDto mapRow(ResultSet rs, int idx) throws SQLException {
						LlenaComboChequeraDto cons = new LlenaComboChequeraDto();
						cons.setId(rs.getString("ID"));
						cons.setDescripcion(rs.getString("Descrip"));
						return cons;
					}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Ingresos, C:IdentificacionIngresosDaoImpl, M:llenarCmbChequeras");
		}
		return list;
	}
	
	public List<ConciliaBancoDto> llenarMovsEmpresa(CriteriosBusquedaDto dto){
		List<ConciliaBancoDto> listCobranza = new ArrayList<ConciliaBancoDto>();
		StringBuffer sbSQL = new StringBuffer();
		
		try{
	            sbSQL.append("\n   SELECT c.no_empresa,nom_empresa, no_docto, no_factura, ");
	            sbSQL.append("\n   fec_factura, fec_valor, no_cliente, importe_solic, id_divisa,concepto,  ");
	            sbSQL.append("\n   referencia, estatus,  folio_det_mov,importe,Folio_Cob, Secuencia, ");
	            sbSQL.append("\n   (select case  when (razon_social ='') then nombre + ' ' + paterno + ' '+ materno");
	            sbSQL.append("\n   when (razon_social is null) then nombre + ' ' + paterno + ' ' + materno");
	            sbSQL.append("\n   Else razon_social ");
	            sbSQL.append("\n   End");
	            sbSQL.append("\n   from persona where equivale_persona=c.No_cliente and id_tipo_persona='C')as cliente ");
	            sbSQL.append("\n   FROM Cobranza_Esperada c INNER JOIN empresa e ON (c.No_Empresa=e.no_empresa)");
	            sbSQL.append("\n   WHERE (c.estatus is null or c.estatus='P')");
	            sbSQL.append("\n   AND (c.Folio_Det_Mov is null or c.Folio_Det_Mov='')");
	            sbSQL.append("\n   AND c.no_empresa = " + dto.getNoEmpresa());
	            
	               
               if(dto.getMontoIni() != 0){
            	   
            	   if(dto.getMontoFin() != 0){
            		   sbSQL.append("\n  and c.importe >= " + dto.getMontoIni() );
            		   sbSQL.append("\n  and c.importe <= " + dto.getMontoFin() );
            	   }else{
            		   sbSQL.append("\n  and c.importe = " + dto.getMontoIni() );
            	   }
               }
               
               if(!dto.getIdDivisa().equals(""))
	            	sbSQL.append("\n  and c.ID_divisa = '" + dto.getIdDivisa() +"' " );
	          
               
               /*sbSQL.append("\n  order by importe desc");*/
			
			System.out.println("/*************QUERY LLENAR GRID COBRANZA_ESPERADA*********/ " + sbSQL.toString() +"\n]");
			
			
			listCobranza = jdbcTemplate.query(sbSQL.toString(), new RowMapper<ConciliaBancoDto>(){
					public ConciliaBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
						ConciliaBancoDto cons = new ConciliaBancoDto();
						
						cons.setNoEmpresa(rs.getInt("no_empresa"));
						cons.setNomEmpresa(rs.getString("nom_empresa"));
						cons.setNoDocto2(rs.getString("no_docto"));
						cons.setNoFactura2(rs.getString("no_factura"));
						cons.setFecFactura(rs.getDate("fec_factura"));
						cons.setFecValor(rs.getDate("fec_valor"));
						cons.setNoClienteS(rs.getString("no_cliente"));
						cons.setImporteSol(rs.getDouble("importe_solic"));
						cons.setIdDivisa(rs.getString("id_divisa"));
						cons.setConcepto(rs.getString("concepto"));
						cons.setReferencia(rs.getString("referencia"));
						cons.setEstatus(rs.getString("estatus"));
						cons.setFolioDetMov(rs.getString("folio_det_mov"));
						cons.setImporte(rs.getDouble("importe"));
						cons.setFolioCob(rs.getInt("folio_cob"));
						cons.setSecuencia(rs.getInt("secuencia"));
						cons.setRazonSoc(rs.getString("cliente"));
						
						return cons;
					}
				});
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Ingresos, C:IdentificacionIngresos, M:llenarMovsEmpresa");
		}
		return listCobranza;
	}
	
	@Override
	public List<MovimientoDto> llenarMovsBancos(CriteriosBusquedaDto dto){
		List<MovimientoDto> listBanco = new ArrayList<MovimientoDto>();
		StringBuffer sbSQL = new StringBuffer();
		
		try{           
            sbSQL.append("\n   SELECT e.nom_empresa, m.no_empresa, m.id_banco, c.desc_banco, m.no_cuenta, \n");
            sbSQL.append(" m.importe - (SELECT COALESCE(SUM(importe), 0) AS importe \n"
					+ " FROM cobranza_esperada \n"
					+ " WHERE folio_mov = m.no_folio_det) AS importe \n");
            sbSQL.append("  , m.fec_operacion, ");
            sbSQL.append("\n   m.fec_valor, m.id_divisa, m.referencia, m.concepto, m.observacion, m.descripcion, m.id_tipo_operacion, ");
            sbSQL.append("\n   m.no_folio_det, m.no_folio_mov, m.id_chequera, m.no_docto, m.id_forma_pago, m.id_caja,m.id_estatus_mov, ");
            sbSQL.append("\n   m.no_cliente, m.id_rubro, m.division, m.id_banco_benef ");
            sbSQL.append("\n   FROM movimiento m, empresa e, cat_banco c ");
            sbSQL.append("\n   WHERE m.id_estatus_mov = 'A' ");
            if(dto.getIdBanco() > 0)
            	sbSQL.append("\n and m.id_banco = " + dto.getIdBanco());
            sbSQL.append("\n   AND m.id_banco = c.id_banco");
            sbSQL.append("\n   AND m.b_entregado <> 'C'");
            sbSQL.append("\n   AND m.no_empresa = e.no_empresa");
            sbSQL.append("\n   AND m.id_tipo_operacion BETWEEN 3100 AND 3199");
            sbSQL.append("\n   AND m.id_tipo_operacion <> 3154");
            sbSQL.append("\n   AND m.no_empresa = " + dto.getNoEmpresa());
            sbSQL.append("\n   AND ((m.concepto like '%DEP FIRME%') or (m.concepto like '%DEP S B COBRO%' and m.b_salvo_buen_cobro='S'))");
            sbSQL.append("\n   AND m.importe <> 0");
            if(dto.getIdChequera() != null && !dto.getIdChequera().equals(""))
            	sbSQL.append("\n and m.id_chequera = '" + dto.getIdChequera() + "'");
            if(dto.getFechaIni() != null && !dto.getFechaIni().equals("")){
            	 if(!dto.getFechaFin().equals("")){
	            	sbSQL.append("\n  and fec_valor_original >= CONVERT(date, '" + 
	            			funciones.ponerFechaSola(dto.getFechaIni()) + "', 103)" );
        			sbSQL.append("\n  and fec_valor_original <= CONVERT(date, '" + 
	            			funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)" );
	            }else{
	            	sbSQL.append("\n  and fec_valor_original = CONVERT(date, '" + 
	   						funciones.ponerFechaSola(dto.getFechaIni()) + "', 103");
	                 }
           }
            if(dto.getMontoIni() != 0){
        	   if(dto.getMontoFin() != 0){
        		   sbSQL.append("\n  and importe >= " + dto.getMontoIni() );
        		   sbSQL.append("\n  and importe <= " + dto.getMontoFin() );
        	   }else{
        		   sbSQL.append("\n  and importe = " + dto.getMontoIni() );
        	   }
           }
                    
            if(!dto.getIdDivisa().equals("")){
         	   sbSQL.append("\n  and m.id_divisa = '" + dto.getIdDivisa() +"' ");
            }
       
            sbSQL.append("\n   UNION ALL");
            sbSQL.append("\n   SELECT e.nom_empresa,h.no_empresa,h.id_banco,c.desc_banco,h.no_cuenta, ");
            sbSQL.append(" h.importe - (SELECT COALESCE(SUM(importe), 0) AS importe \n"
        					+ " FROM cobranza_esperada \n"
        					+ " WHERE folio_mov = h.no_folio_det) AS importe \n");
            sbSQL.append("\n   ,h.fec_operacion,");
            sbSQL.append("\n   h.fec_valor,h.id_divisa,h.referencia,h.concepto,h.observacion,h.descripcion, h.id_tipo_operacion,");
            sbSQL.append("\n   h.no_folio_det,h.no_folio_mov,h.id_chequera,h.no_docto,h.id_forma_pago,h.id_caja,h.id_estatus_mov,");
            sbSQL.append("\n   h.no_cliente,h.id_rubro,h.division,h.id_banco_benef");
            sbSQL.append("\n   FROM  hist_movimiento h, empresa e , cat_banco c");
            sbSQL.append("\n   WHERE h.id_estatus_mov = 'A'");
            sbSQL.append("\n   AND h.id_banco=c.id_banco");
            sbSQL.append("\n   AND h.b_entregado <> 'C'");
            sbSQL.append("\n   AND h.no_empresa=e.no_empresa");
            sbSQL.append("\n   AND h.id_tipo_operacion BETWEEN 3100 AND 3199");
            sbSQL.append("\n   AND h.no_empresa= " + dto.getNoEmpresa());
            sbSQL.append("\n   AND ((h.concepto like '%DEP FIRME%') or (h.concepto like '%DEP S B COBRO%' and h.b_salvo_buen_cobro='S'))");
            sbSQL.append("\n   AND h.importe <> 0");
            if(dto.getIdChequera() != null && !dto.getIdChequera().equals(""))
            	sbSQL.append("\n and h.id_chequera = '" + dto.getIdChequera() + "'");
            if(dto.getFechaIni() != null && !dto.getFechaIni().equals("")){
            	 if(!dto.getFechaFin().equals("")){
	            	sbSQL.append("\n  and fec_valor_original >= CONVERT(date, '" + 
	            			funciones.ponerFechaSola(dto.getFechaIni()) + "', 103)" );
        			sbSQL.append("\n  and fec_valor_original <= CONVERT(date, '" + 
	            			funciones.ponerFechaSola(dto.getFechaFin()) + "', 103)" );
	            }else{
	            	sbSQL.append("\n  and fec_valor_original = CONVERT(date, '" + 
	   						funciones.ponerFechaSola(dto.getFechaIni()) + "', 103");
	                 }
           }
            if(dto.getMontoIni() != 0){
        	   if(dto.getMontoFin() != 0){
        		   sbSQL.append("\n  and h.importe >= " + dto.getMontoIni() );
        		   sbSQL.append("\n  and h.importe <= " + dto.getMontoFin() );
        	   }else{
        		   sbSQL.append("\n  and h.importe = " + dto.getMontoIni() );
        	   }
           }
                    
            if(!dto.getIdDivisa().equals("")){
         	   sbSQL.append("\n  and h.id_divisa = '" + dto.getIdDivisa() +"' ");
            }
       
            
           // sbSQL.append("\n  order by importe asc");
            
            System.out.println("/*************QUERY LLENAR GRID MOVIMIENTOS**********/\n"+ sbSQL.toString());
	        
			listBanco = jdbcTemplate.query(sbSQL.toString(), new RowMapper<MovimientoDto>(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons = new MovimientoDto();
					
					
					
					cons.setNomEmpresa(rs.getString("nom_empresa"));					
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setIdBanco(rs.getInt("id_banco"));
					cons.setDescBanco(rs.getString("desc_banco"));
					cons.setNoCuenta(rs.getInt("no_cuenta")); 
					cons.setImporte(rs.getDouble("importe")); 
					cons.setFecOperacion(rs.getDate("fec_operacion")); 
					cons.setFecValor(rs.getDate("fec_valor"));
					cons.setIdDivisa(rs.getString("id_divisa"));
					cons.setReferencia(rs.getString("referencia"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setObservacion(rs.getString("observacion"));
					cons.setDescripcion(rs.getString("descripcion"));
					cons.setIdTipoOperacion(rs.getInt("id_tipo_operacion")); 
					cons.setNoFolioDet(rs.getInt("no_folio_det"));
					cons.setNoFolioMov(rs.getInt("no_folio_mov")); 
					cons.setIdChequera(rs.getString("id_chequera")); 
					cons.setNoDocto(rs.getString("no_docto")); 
					cons.setIdFormaPago(rs.getInt("id_forma_pago")); 
					cons.setIdCaja(rs.getInt("id_caja")); 
					cons.setIdEstatusMov(rs.getString("id_estatus_mov"));
					cons.setNoCliente(rs.getString("no_cliente"));
					cons.setIdBancoBenef(rs.getInt("id_banco_benef"));
					cons.setIdRubro(rs.getInt("id_rubro")); 
					cons.setDivision(rs.getString("division")); 
			       
					return cons;
				}});
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Ingresos, C:IdentificacionIngresosDaoImpl, M:llenarMovsBancos");
		}
		return listBanco;
	}
	
	public List<LlenaComboGralDto>llenarComboDivisa() {
		
		String sql="";
		List<LlenaComboGralDto> listDivisas= new ArrayList<LlenaComboGralDto>();
		try
		{
			sql+= " SELECT id_divisa as ID, desc_divisa as Descrip ";
			sql+= "   FROM cat_divisa ";
			sql+= "  WHERE clasificacion = 'D'";
			
    		listDivisas = jdbcTemplate.query(sql, new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setIdStr(rs.getString("ID"));
					cons.setDescripcion(rs.getString("Descrip"));
					return cons;
				}});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Ingresos, C:IdentificacionIngresos, M:llenarComboDivisa");
		}
		return listDivisas;
	}
	
	public List<MovimientoDto>llenaComboCuentaContable() {
		
		String sql="";
		List<MovimientoDto> listCuentas= new ArrayList<MovimientoDto>();
		try
		{
			sql+= " SELECT id_cta_contable,	desc_cta_contable,	perd_ganan ";
			sql+= "   FROM cat_ctas_contables";
			sql+= "  WHERE perd_ganan= 'G'";
			
			listCuentas = jdbcTemplate.query(sql, new RowMapper<MovimientoDto>(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons = new MovimientoDto();
					cons.setIdCtaContable(rs.getString("id_cta_contable"));
					cons.setDescCtaContable(rs.getString("desc_cta_contable"));
					return cons;
				}});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Ingresos, C:IdentificacionIngresos, M:llenaComboCuentaContable");
		}
		return listCuentas;
	}
	
    public List<ConciliaBancoDto>llenaComboCuentaContable2() {
		
		String sql="";
		List<ConciliaBancoDto> listCuentas= new ArrayList<ConciliaBancoDto>();
		try
		{
			sql+= " SELECT id_cta_contable,	desc_cta_contable,	perd_ganan ";
			sql+= "   FROM cat_ctas_contables";
			sql+= "  WHERE perd_ganan= 'P'";
			
			listCuentas = jdbcTemplate.query(sql, new RowMapper<ConciliaBancoDto>(){
				public ConciliaBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConciliaBancoDto cons = new ConciliaBancoDto();
					cons.setIdCtaContable(rs.getString("id_cta_contable"));
					cons.setDescCtaContable(rs.getString("desc_cta_contable"));
					
					
					return cons;
				}});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Ingresos, C:IdentificacionIngresos, M:llenaComboCuentaContable2");
		}
		return listCuentas;
	}

    public List<ConciliaBancoDto>llenaComboOrigen() {
	
	String sql="";
	List<ConciliaBancoDto> listCuentas= new ArrayList<ConciliaBancoDto>();
	try
	{
		sql+= " SELECT setemp, siscod, soiemp  FROM set006 ";
		
		
		listCuentas = jdbcTemplate.query(sql, new RowMapper<ConciliaBancoDto>(){
			public ConciliaBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
				ConciliaBancoDto cons = new ConciliaBancoDto();
				cons.setSetemp(rs.getInt("setemp"));
				cons.setSoiemp(rs.getString("soiemp"));
				
				
				return cons;
			}});
		
	}catch(Exception e){
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
		+ "P:Ingresos, C:IdentificacionIngresos, M:llenaComboOrigen");
	}
	return listCuentas;
}
	

  
    
	@Override
	public Map<String, String> conciliaBancosEmpresa(List<ConciliaBancoDto> listCobranza, List<MovimientoDto> listBanco) {
		StringBuffer sql = new StringBuffer();
		Map<String, String> resultado = new HashMap<>();
		
		resultado.put("error", "");
		resultado.put("mensaje", "");
		try{
			String facturas = "0";
			for (ConciliaBancoDto factura : listCobranza) {
				facturas += ", " + factura.getFolioCob();
			}
			
			if (!facturas.equals("0")) {
				sql.append(" UPDATE cobranza_esperada SET folio_mov = " + listBanco.get(0).getNoFolioDet() + " \n");
				sql.append(" , Estatus = 'C' \n");
				sql.append(" , Fec_Modif = CONVERT(DATE, GETDATE(), 103) \n");
				sql.append(" WHERE Folio_Cob in (" + facturas + ") \n");
				
				if (jdbcTemplate.update(sql.toString()) > 0) {					
					if (obtieneImporteDeposito(listBanco.get(0).getNoFolioDet()) == 0) {
						sql = new StringBuffer();
						sql.append(" UPDATE movimiento SET id_tipo_operacion = 3100 \n");
						sql.append(" , b_entregado = 'C' \n");
						sql.append(" , id_rubro = (select id_rubro from cobranza_esperada where folio_cob = " + listCobranza.get(0).getFolioCob() + ") \n");
						sql.append(" , id_grupo = (select top 1 id_grupo from cat_rubro where id_rubro in \n"
								+ "									(select id_rubro from cobranza_esperada where folio_cob = " + listCobranza.get(0).getFolioCob() + ")) \n");
						sql.append(" , no_cliente = (SELECT no_persona \n"
								+ "					FROM persona \n"
								+ "					WHERE equivale_persona in (Select No_cliente \n "
								+ "													from cobranza_esperada \n "
								+ "													where folio_cob = " + listCobranza.get(0).getFolioCob() + ") \n "
								+ "					AND id_tipo_persona = 'C') \n"); 
						sql.append(" WHERE no_folio_det = " + listBanco.get(0).getNoFolioDet());
						
						if (jdbcTemplate.update(sql.toString()) >= 0) {
							resultado.put("error", "");
							resultado.put("mensaje", "Datos actualizados con exito. ");
						} else {
							resultado.put("error", "No se pudo actualizar el deposito. ");
							resultado.put("mensaje", "");
						}
					} else {
						resultado.put("error", "");
						resultado.put("mensaje", "Datos actualizados con exito. ");
					}
				} else {				
					resultado.put("error", "No se pudieron actualizar las facturas");
					resultado.put("mensaje", "");
				}
			} else {
				resultado.put("error", "No se pudieron obtener las facturas");
				resultado.put("mensaje", "");
			}
		 }catch(Exception e){
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Ingresos, C:IdentificacionIngresos, M:conciliaBancoEmpresa");
			 resultado.put("error", "Error al actualizar bancos/cobranza");
		 }
		
		return resultado;
	}
	
	private double obtieneImporteDeposito(int noFolioDet) {
		double retorno = -1;
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT importe - (SELECT COALESCE(SUM(importe), 0) AS importe \n"
					+ " FROM cobranza_esperada \n"
					+ " WHERE folio_mov = " + noFolioDet + ") from movimiento where no_folio_det = " + noFolioDet);
			
			retorno = jdbcTemplate.queryForInt(sql.toString());
		} catch (Exception e) {
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Ingresos, C:IdentificacionIngresos, M:conciliaBancoEmpresa");
		}
		return retorno;
	}

	@Override
	public Map<String, String> parcializarBancos(List<ConciliaBancoDto> listCobranza,double diferencia2) {

		
		
		Map<String, String> resultado2 = new HashMap<>();
		resultado2.put("error", "");
		resultado2.put("mensaje", "");
		
		StringBuffer sql = new StringBuffer();
		int count2 = 0;
		
		try{
			
			
			
			sql.append("\n UPDATE cobranza_esperada ");
			sql.append("\n SET IMPORTE        =  IMPORTE-"+diferencia2+" ");
			sql.append("\n WHERE IMPORTE      =  "+listCobranza.get(0).getImporte()+" ");
			sql.append("\n AND id_DIVISA      = '"+listCobranza.get(0).getIdDivisa()+"' ");
			sql.append("\n AND SECUENCIA      = '"+listCobranza.get(0).getSecuencia()+"' ");
			sql.append("\n AND CONCEPTO       = '"+listCobranza.get(0).getConcepto()+"' ");
			sql.append("\n AND FOLIO_COB    = '"+listCobranza.get(0).getFolioCob()+"' ");
			
		//System.out.println("--query de UPDATE cobranza parcializarBancos : "+ sql.toString());
		
			
			count2 = jdbcTemplate.update(sql.toString());
			
			if(count2 > 0){
    			resultado2.put("mensaje", "Registros actualizados con �xito");
    	
    		}else{
    			resultado2.put("error", "Error al actualizar en cobranza.");
    		
    		}
				
			//Actualiza sobre la tabla Cobranza.
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			if(sql.length() > 0)
				sql.delete(0, sql.length());
			
			
			sql.append("\n INSERT INTO Cobranza_Esperada (Folio_Cob, No_Empresa, No_Docto, Secuencia, No_Factura, Fec_factura, Fec_Valor, ");
			sql.append("\n 	No_cliente, Importe, Importe_solic, Id_Divisa, Concepto, Forma_Pago, Id_rubro, Id_Grupo, Referencia, ");
			sql.append("\n 	Folio_Det_Mov, Estatus, Usuario_Alta, Fec_alta, Usuario_Modif, Fec_Modif, Fecha_exp, origen, causa_rech, ");
			sql.append("\n 	folio_mov, Importe_pendiente, usuario_concilia, fec_concilia, concilia_referenciado, concilia_tipo,"); 
			sql.append("\n 	conciliacion, estatus_proceso, codigo_res)");
			sql.append("\n SElECT "
					+ consultasGenerales.obtenFolio("Folio_Cob")
					+ ", No_Empresa, No_Docto, Secuencia, No_Factura, Fec_factura, Fec_Valor, No_cliente, " + diferencia2 + ", ");
			sql.append("\n 	Importe_solic, Id_Divisa, Concepto, Forma_Pago, Id_rubro, Id_Grupo, Referencia, Folio_Det_Mov, Estatus, ");
			sql.append("\n 	Usuario_Alta, Fec_alta, Usuario_Modif, Fec_Modif, Fecha_exp, origen, causa_rech, folio_mov, Importe_pendiente, ");
			sql.append("\n 	usuario_concilia, fec_concilia, concilia_referenciado, concilia_tipo, conciliacion, estatus_proceso, codigo_res"); 
			sql.append("\n FROM Cobranza_Esperada ");
			sql.append("\n WHERE Folio_Cob = " + listCobranza.get(0).getFolioCob());
			
			//System.out.println("--query de Insert nueva fila cobranza : "+ sql.toString());
			
			count2 = jdbcTemplate.update(sql.toString());
			if(count2 > 0){
    			resultado2.put("mensaje", "Registros parcializados con �xito");
    			
    		}else{
    			resultado2.put("error", "Error al parcializar la factura");
    		
    			return resultado2;
    		}
				
		 }catch(Exception e){
				
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Ingresos, C:IdentificacionIngresos, M:parcializarBancos");
			 resultado2.put("error", "Error al parcializar la factura.");
		 }
		
		return resultado2;
		
	
	}
	
	public Map<String, String> parcializarBancos2(List<MovimientoDto> listBanco,double diferencia) {
		
		
		Map<String, String> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		StringBuffer sql = new StringBuffer();
		int count = 0;
		try{
			
			sql.append("\n UPDATE movimiento ");
			sql.append("\n SET IMPORTE        =  IMPORTE-"+diferencia+" ");
			sql.append("\n WHERE IMPORTE      =  "+listBanco.get(0).getImporte()+" ");
			sql.append("\n AND NO_FOLIO_DET   =  "+listBanco.get(0).getNoFolioDet()+" ");
			
			//System.out.println("--query de UPDATE movimiento : "+ sql.toString());
			
			
			count = jdbcTemplate.update(sql.toString());
			
			if(count > 0){
    			resultado.put("mensaje", "Registros actualizados con �xito");
    	
    		}else{
    			resultado.put("error", "Error al actualizar en bancos.");
    		
    		}
		
			//Actualiza sobre la tabla Movimiento.

			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			if(sql.length() > 0)
				sql.delete(0, sql.length());
			
			
			
			
			sql.append("\n INSERT INTO movimiento (no_empresa, no_folio_det, no_folio_mov, id_tipo_operacion, id_cve_operacion, id_tipo_saldo, ");
			sql.append("\n 	no_linea, id_inv_cbolsa, no_cuenta, id_estatus_mov, no_cheque, id_chequera, id_banco, id_forma_pago, importe, ");
			sql.append("\n 	importe_original, b_salvo_buen_cobro, id_tipo_docto, no_docto, valor_tasa, fec_operacion, fec_valor, ");
			sql.append("\n 	fec_valor_original, fec_recalculo, folio_ref, dias_inv, id_tipo_movto, id_caja, id_divisa, id_divisa_original, ");
			sql.append("\n 	fec_exportacion, fec_reimprime, fec_imprime, fec_cheque, lote_entrada, lote_salida, referencia, folio_banco, ");
			sql.append("\n 	beneficiario, id_leyenda, id_banco_benef, id_chequera_benef, concepto, id_contable, folio_seg, arch_protegido, ");
			sql.append("\n 	usuario_imprime, fec_alta, usuario_alta, fec_modif, usuario_modif, no_cuenta_ref, id_estatus_cb, id_estatus_cc, ");
			sql.append("\n 	no_cliente, no_cobrador, sucursal, plaza, observacion, no_recibo, origen_mov, no_docto_cus, solicita, autoriza, ");
			sql.append("\n 	b_entregado, importe_desglosado, fec_entregado, fec_conf_trans, fec_rech_trans, tipo_cambio, tipo_cancelacion, ");
			sql.append("\n 	fec_exporta_flujo, fec_trans, id_codigo, id_subcodigo, fisica_moral, nac_ext, id_estatus_cheq, invoice_id, ");
			sql.append("\n 	vendor_id, vendor_site_id, invoice_type, no_factura, grupo_pago, id_rubro, agrupa1, agrupa2, agrupa3, po_headers, ");
			sql.append("\n 	alternate_vendor_id_eft, alternate_vendor_site_id_eft, invoice_type_lookup_code, b_autoriza_impre, firmante1, ");
			sql.append("\n 	firmante2, oper_may_esp, cod_bloqueo, rfc, no_poliza, c_poliza, c_lote, c_periodo, c_mes, b_gen_conta, ");
			sql.append("\n 	b_gen_contable, Division, Ind_IVA, contrarecibo, deudor, descripcion, rango, no_pedido, referencia_ban, ");
			sql.append("\n 	hora_recibo, fec_concilia_caja, fec_propuesta, importe_parcial, b_arch_seg, cve_control, id_banco_ant, ");
			sql.append("\n 	id_chequera_ant, origen_mov_ant, no_cliente_ant, id_servicio_be, e_mail, clabe, id_area, monto_sobregiro, ");
			sql.append("\n 	ID_GRUPO, TABLA_ORIGEN, FECHA_CONTABILIZACION, B_CERTIFICADO, FEC_CERTIFICACION, ABBA, swift, NOMBRE_PROV_DIVISAS, ");
			sql.append("\n 	BANCO_DIVISAS, DIRECCION_DIVISAS, CUENTA_DIVISAS, SORT_CODE, ABI, CAB, COMENTARIO1, COMENTARIO2, fec_propone_mov, ");
			sql.append("\n 	usu_propone_mov) ");
			sql.append("\n SElECT no_empresa, "
					+ consultasGenerales.obtenFolio("no_folio_det")
					+ ", no_folio_mov, id_tipo_operacion, id_cve_operacion, id_tipo_saldo, ");
			sql.append("\n 	no_linea, id_inv_cbolsa, no_cuenta, id_estatus_mov, no_cheque, id_chequera, id_banco, id_forma_pago, " + 
						diferencia + ", ");
			sql.append("\n 	importe_original, b_salvo_buen_cobro, id_tipo_docto, no_docto, valor_tasa, fec_operacion, fec_valor, ");
			sql.append("\n 	fec_valor_original, fec_recalculo, folio_ref, dias_inv, id_tipo_movto, id_caja, id_divisa, id_divisa_original, ");
			sql.append("\n 	fec_exportacion, fec_reimprime, fec_imprime, fec_cheque, lote_entrada, lote_salida, referencia, folio_banco, ");
			sql.append("\n 	beneficiario, id_leyenda, id_banco_benef, id_chequera_benef, concepto, id_contable, folio_seg, arch_protegido, ");
			sql.append("\n 	usuario_imprime, fec_alta, usuario_alta, fec_modif, usuario_modif, no_cuenta_ref, id_estatus_cb, id_estatus_cc, ");
			sql.append("\n 	no_cliente, no_cobrador, sucursal, plaza, observacion, no_recibo, origen_mov, no_docto_cus, solicita, autoriza, ");
			sql.append("\n 	b_entregado, importe_desglosado, fec_entregado, fec_conf_trans, fec_rech_trans, tipo_cambio, tipo_cancelacion, ");
			sql.append("\n 	fec_exporta_flujo, fec_trans, id_codigo, id_subcodigo, fisica_moral, nac_ext, id_estatus_cheq, invoice_id, ");
			sql.append("\n 	vendor_id, vendor_site_id, invoice_type, no_factura, grupo_pago, id_rubro, agrupa1, agrupa2, agrupa3, po_headers, ");
			sql.append("\n 	alternate_vendor_id_eft, alternate_vendor_site_id_eft, invoice_type_lookup_code, b_autoriza_impre, firmante1, ");
			sql.append("\n 	firmante2, oper_may_esp, cod_bloqueo, rfc, no_poliza, c_poliza, c_lote, c_periodo, c_mes, b_gen_conta, ");
			sql.append("\n 	b_gen_contable, Division, Ind_IVA, contrarecibo, deudor, descripcion, rango, no_pedido, referencia_ban, ");
			sql.append("\n 	hora_recibo, fec_concilia_caja, fec_propuesta, importe_parcial, b_arch_seg, cve_control, id_banco_ant, ");
			sql.append("\n 	id_chequera_ant, origen_mov_ant, no_cliente_ant, id_servicio_be, e_mail, clabe, id_area, monto_sobregiro, ");
			sql.append("\n 	ID_GRUPO, TABLA_ORIGEN, FECHA_CONTABILIZACION, B_CERTIFICADO, FEC_CERTIFICACION, ABBA, swift, NOMBRE_PROV_DIVISAS, ");
			sql.append("\n 	BANCO_DIVISAS, DIRECCION_DIVISAS, CUENTA_DIVISAS, SORT_CODE, ABI, CAB, COMENTARIO1, COMENTARIO2, fec_propone_mov, ");
			sql.append("\n 	usu_propone_mov ");
			sql.append("\n FROM movimiento ");
			sql.append("\n WHERE no_folio_det = " + listBanco.get(0).getNoFolioDet());
			
			
		//System.out.println("--query de Inser movimiento : "+ sql.toString());
			
			count = jdbcTemplate.update(sql.toString());
			if(count > 0){
    			resultado.put("mensaje", "Registros parcializados con �xito");
    			
    		}else{
    			resultado.put("error", "Error al parcializar en bancos");
    		
    			return resultado;
    		}
				
		 }catch(Exception e){
				
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Ingresos, C:IdentificacionIngresos, M:parcializarBancos2");
			 resultado.put("error", "Error al parcializar bancos.");
		 }
		
		return resultado;
		
	}

	public Map<String, String> concilVirSBanco(List<MovimientoDto> listBanco,String causa,String cuenta) {
		
		
		Map<String, String> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		StringBuffer sql = new StringBuffer();
		int count = 0;
		try{
			
			sql.append("\n UPDATE MOVIMIENTO ");
			sql.append("\n SET B_ENTREGADO = 'S' ");
			sql.append("\n WHERE IMPORTE      =  "+listBanco.get(0).getImporte()+" ");
			sql.append("\n AND NO_FOLIO_DET   =  "+listBanco.get(0).getNoFolioDet()+" ");
			
			
			
			
			//System.out.println("--query de UPDATE conciliacion movimiento : "+ sql.toString());
			
			
			count = jdbcTemplate.update(sql.toString());
			
			if(count > 0){
    			resultado.put("mensaje", "Registros actualizados con �xito");
    	
    		}else{
    			resultado.put("error", "Error al actualizar en bancos.");
    		
    		}
		
			//Actualiza sobre la tabla Movimiento.
			
			if(sql.length() > 0)
				sql.delete(0, sql.length());
			
			
			
			sql.append("\n 	   INSERT INTO movimiento_virtual ");
			sql.append("\n    		 (no_empresa,no_folio_det,id_banco,id_chequera,");
			sql.append("\n     		  id_divisa,no_docto,fec_creacion,importe,referencia,id_cta_contable,");
			sql.append("\n     		  estatus,causa_vir,	causa_rech,	usuario_alta,	fec_alta,no_folio_mov)");
			sql.append("\n     VALUES");
			sql.append("\n            ( '"+listBanco.get(0).getNoEmpresa()+"',  (select coalesce (MAX(no_folio_det),0)+1 from  movimiento_virtual), ");
			sql.append("\n              '"+listBanco.get(0).getIdBanco()+"'  , '"+listBanco.get(0).getIdChequera()+"', '"+listBanco.get(0).getIdDivisa()+"' ,  ");
			sql.append("\n              '"+listBanco.get(0).getNoDocto()+"'     ,  CURRENT_TIMESTAMP , "+listBanco.get(0).getImporte()+", '"+listBanco.get(0).getReferencia()+"', ");
			sql.append("\n               '"+cuenta+"','C','"+causa+"','',"+ GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+",CURRENT_TIMESTAMP,'"+listBanco.get(0).getNoFolioDet()+"')");
			
			
			
			//System.out.println("--query de Inser movimiento : "+ sql.toString());
			
			count = jdbcTemplate.update(sql.toString());
			if(count > 0){
    			resultado.put("mensaje", "Registros conciliados con �xito");
    			
    		}else{
    			resultado.put("error", "Error al conciliar en bancos");
    		
    			return resultado;
    		}
				
		 }catch(Exception e){
				
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Ingresos, C:IdentificacionIngresos, M:concilVirSBanco");
			 resultado.put("error", "Error al conciliar bancos.");
		 }
		
		return resultado;
		
	}

	public Map<String, String> concilVirSCobranza(List<ConciliaBancoDto> listCobranza,String causa,String cuenta) {

		
		
		Map<String, String> resultado2 = new HashMap<>();
		resultado2.put("error", "");
		resultado2.put("mensaje", "");
		
		StringBuffer sql = new StringBuffer();
		int count2 = 0;
		
		try{
			
			
			sql.append("\n UPDATE cobranza_esperada ");
			sql.append("\n SET ESTATUS= 'C' ");
			sql.append("\n WHERE IMPORTE = '"+listCobranza.get(0).getImporte()+"' ");
			sql.append("\n AND FOLIO_COB    = '"+listCobranza.get(0).getFolioCob()+"' ");
			
			//System.out.println("--query de UPDATE cobranza : "+ sql.toString());
		
			
			count2 = jdbcTemplate.update(sql.toString());
			
			if(count2 > 0){
    			resultado2.put("mensaje", "Registros actualizados con �xito");
    	
    		}else{
    			resultado2.put("error", "Error al actualizar en cobranza.");
    		
    		}
				
			//Actualiza sobre la tabla Cobranza.
			
			if(sql.length() > 0)
				sql.delete(0, sql.length());
			
			
			
			
			sql.append("\n 	   INSERT INTO movimiento_virtual ");
			sql.append("\n    		 (no_empresa,      no_folio_cob,  no_folio_det,  id_divisa, ");
			sql.append("\n     		  no_docto,        fec_creacion,  importe,       referencia,");
			sql.append("\n     		  id_cta_contable, estatus,        causa_vir,	 causa_rech,");
			sql.append("\n            usuario_alta,	   fec_alta)");
			sql.append("\n     VALUES");
			sql.append("\n            ( '"+listCobranza.get(0).getNoEmpresa()+"', "+listCobranza.get(0).getFolioCob()+"  , (select coalesce (MAX(no_folio_det),0)+1 from  movimiento_virtual), ");
			sql.append("\n               '"+listCobranza.get(0).getIdDivisa()+"' ,  ");
			sql.append("\n              '"+listCobranza.get(0).getNoDocto()+"'     ,  CURRENT_TIMESTAMP , "+listCobranza.get(0).getImporte()+", '"+listCobranza.get(0).getReferencia()+"', ");
			sql.append("\n               '"+cuenta+"','C','"+causa+"','',"+ GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+",CURRENT_TIMESTAMP)");
			
			
			
			//System.out.println("--query de Insert cobranza en movimiento_virtuaL : "+ sql.toString());
			
			count2 = jdbcTemplate.update(sql.toString());
			if(count2 > 0){
    			resultado2.put("mensaje", "Registros parcializados con �xito");
    			
    		}else{
    			resultado2.put("error", "Error al parcializar en cobranza");
    		
    			return resultado2;
    		}
				
		 }catch(Exception e){
				
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Ingresos, C:IdentificacionIngresos, M:parcializarBancos");
			 resultado2.put("error", "Error al parcializar bancos.");
		 }
		
		return resultado2;
		
	
	}
	
	@Override
	public Map<String, String> conciliaVirtualCobranza(List<ConciliaBancoDto> listCobranza, List<MovimientoDto> listBanco, String causa, String origen, String cuenta) {
		Map<String, String> resultado = new HashMap<>();


		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		StringBuffer sql = new StringBuffer();
		int count = 0;
		try{
	    	//actualiza sobre la tabla MOVTO_BANCO_CONCILIA
			for(int i=0; i<listCobranza.size(); i++){
				if(sql.length() > 0)
					sql.delete(0, sql.length());
				
				sql.append("\n UPDATE cobranza_esperada ");
				sql.append("\n SET ESTATUS = 'C' ");
			    sql.append("\n WHERE IMPORTE = '"+listCobranza.get(i).getImporte()+"' ");
				sql.append("\n AND FOLIO_COB    = '"+listCobranza.get(0).getFolioCob()+"' ");
				
				
				//System.out.println("--update cobranza_esperada conciliado: "+ sql.toString());
				
				count = jdbcTemplate.update(sql.toString());
				
				if(count > 0){
	    			resultado.put("mensaje", "Registros actualizados con �xito");
	    		}else{
	    			resultado.put("error", "Error al actualizar en cobranza.");
	    			return resultado;
	    		}
				
			}
    		
			//Actualiza sobre la tabla Movimiento.
			for(int i=0; i<listBanco.size(); i++){
				if(sql.length() > 0)
					sql.delete(0, sql.length());
				
				sql.append("\n UPDATE MOVIMIENTO ");
				sql.append("\n SET B_ENTREGADO = 'S'  ");
				sql.append("\n WHERE IMPORTE      =  "+listBanco.get(i).getImporte()+" ");
				sql.append("\n AND NO_FOLIO_DET   =  "+listBanco.get(i).getNoFolioDet()+" ");
				
				
				
				//System.out.println("--update movimiento: "+ sql.toString());
				
				count = jdbcTemplate.update(sql.toString());
				
				if(count > 0){
	    			resultado.put("mensaje", "Registros actualizados con �xito");
	    		}else{
	    			resultado.put("error", "Error al actualizar en Banco");
	    			return resultado;
	    		}
				
			}
			
		 
		
			//Actualiza sobre la tabla Movimiento Virtual.
			
			if(sql.length() > 0)
				sql.delete(0, sql.length());
			
			
			
			sql.append("\n 	   INSERT INTO movimiento_virtual ");
			sql.append("\n    		 (no_empresa,no_folio_cob,no_folio_det,id_banco,id_chequera,");
			sql.append("\n     		  id_divisa,no_docto,fec_creacion,importe,referencia,id_cta_contable,");
			sql.append("\n     		  origen,estatus,causa_vir,	causa_rech,	usuario_alta,	fec_alta,no_folio_mov)");
			sql.append("\n     VALUES");
			sql.append("\n            ( '"+listBanco.get(0).getNoEmpresa()+"', "+listCobranza.get(0).getFolioCob()+"  , (select coalesce (MAX(no_folio_det),0)+1 from  movimiento_virtual), ");
			sql.append("\n              '"+listBanco.get(0).getIdBanco()+"'  , '"+listBanco.get(0).getIdChequera()+"', '"+listCobranza.get(0).getIdDivisa()+"' ,  ");
			sql.append("\n              '"+listCobranza.get(0).getNoDocto()+"'     ,  CURRENT_TIMESTAMP , "+listCobranza.get(0).getImporte()+"-"+listBanco.get(0).getImporte()+", '"+listCobranza.get(0).getReferencia()+"', ");
			sql.append("\n               '"+cuenta+"',(SUBSTRING('"+origen+"',1,3)),'C','"+causa+"','',"+ GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+",CURRENT_TIMESTAMP,'"+listBanco.get(0).getNoFolioDet()+"')");
			
			
			
			
			//System.out.println("--query de Inser movimiento_Virtual : "+ sql.toString());
			
			count = jdbcTemplate.update(sql.toString());
			if(count > 0){
    			resultado.put("mensaje", "Registros conciliados con �xito");
    			
    		}else{
    			resultado.put("error", "Error al conciliar ");
    		
    			return resultado;
    		}
				
		 }catch(Exception e){
				
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Ingresos, C:IdentificacionIngresos, M:conciliaVirtualCobranza");
			 resultado.put("error", "Error al conciliar.");
		 }
		
		return resultado;
		}

	@Override
	public Map<String, String> conciliaVirtualBanco(List<ConciliaBancoDto> listCobranza, List<MovimientoDto> listBanco, String causa, String origen, String cuenta) {
		Map<String, String> resultado = new HashMap<>();

		
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		StringBuffer sql = new StringBuffer();
		int count = 0;
		try{
	    	//actualiza sobre la tabla MOVTO_BANCO_CONCILIA
			for(int i=0; i<listCobranza.size(); i++){
				if(sql.length() > 0)
					sql.delete(0, sql.length());
				
				sql.append("\n UPDATE cobranza_esperada ");
				sql.append("\n SET ESTATUS = 'C' ");
				sql.append("\n WHERE IMPORTE = '"+listCobranza.get(i).getImporte()+"' ");
			 	sql.append("\n AND FOLIO_COB    = '"+listCobranza.get(0).getFolioCob()+"' ");
				
				
				//System.out.println("--update cobranza_esperada: "+ sql.toString());
				
				count = jdbcTemplate.update(sql.toString());
				
				if(count > 0){
	    			resultado.put("mensaje", "Registros actualizados con �xito");
	    		}else{
	    			resultado.put("error", "Error al actualizar en cobranza.");
	    			return resultado;
	    		}
				
			}
    		
			//Actualiza sobre la tabla Movimiento.
			for(int i=0; i<listBanco.size(); i++){
				if(sql.length() > 0)
					sql.delete(0, sql.length());
				
				sql.append("\n UPDATE MOVIMIENTO ");
				sql.append("\n SET B_ENTREGADO = 'S' ");
				sql.append("\n WHERE IMPORTE      =  "+listBanco.get(i).getImporte()+" ");
				sql.append("\n AND id_DIVISA      = '"+listBanco.get(i).getIdDivisa()+"' ");
				sql.append("\n AND NO_DOCTO       = '"+listBanco.get(i).getNoDocto()+"' ");
				sql.append("\n AND NO_CLIENTE     = '"+listBanco.get(i).getNoCliente()+"' ");
				sql.append("\n AND NO_FOLIO_DET   =  "+listBanco.get(i).getNoFolioDet()+" ");
				
				
				//System.out.println("--update movimiento: "+ sql.toString());
				
				count = jdbcTemplate.update(sql.toString());
				
				if(count > 0){
	    			resultado.put("mensaje", "Registros actualizados con �xito");
	    		}else{
	    			resultado.put("error", "Error al actualizar en Banco");
	    			return resultado;
	    		}
				
			}
			
		 
		
			//Actualiza sobre la tabla Movimiento Virtual.
			
			if(sql.length() > 0)
				sql.delete(0, sql.length());
			
			

			sql.append("\n 	   INSERT INTO movimiento_virtual ");
			sql.append("\n    		 (no_empresa,no_folio_cob,no_folio_det,id_banco,id_chequera,");
			sql.append("\n     		  id_divisa,no_docto,fec_creacion,importe,referencia,id_cta_contable,");
			sql.append("\n     		  origen,estatus,causa_vir,	causa_rech,	usuario_alta,	fec_alta,no_folio_mov)");
			sql.append("\n     VALUES");
			sql.append("\n            ( '"+listBanco.get(0).getNoEmpresa()+"', "+listCobranza.get(0).getFolioCob()+"  ,  (select coalesce (MAX(no_folio_det),0)+1 from  movimiento_virtual), ");
			sql.append("\n              '"+listBanco.get(0).getIdBanco()+"'  , '"+listBanco.get(0).getIdChequera()+"', '"+listBanco.get(0).getIdDivisa()+"' ,  ");
			sql.append("\n              '"+listBanco.get(0).getNoDocto()+"'     ,  CURRENT_TIMESTAMP , "+listBanco.get(0).getImporte()+"-"+listCobranza.get(0).getImporte()+", '"+listBanco.get(0).getReferencia()+"', ");
			sql.append("\n               '"+cuenta+"',(SUBSTRING('"+origen+"',1,3)),'C','"+causa+"','',"+ GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+",CURRENT_TIMESTAMP,'"+listBanco.get(0).getNoFolioDet()+"')");
			
			
			
			
			//System.out.println("--query de Inser movimiento_Virtual : "+ sql.toString());
			
			count = jdbcTemplate.update(sql.toString());
			if(count > 0){
    			resultado.put("mensaje", "Registros conciliados con �xito");
    			
    		}else{
    			resultado.put("error", "Error al conciliar ");
    		
    			return resultado;
    		}
				
		 }catch(Exception e){
				
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Ingresos, C:IdentificacionIngresos, M:conciliaVirtualBanco");
			 resultado.put("error", "Error al conciliar.");
		 }
		
		return resultado;
		}

	@Override
	public List<ConciliaBancoDto> datosExcel(int empresa, String fechaIni, String fechaFin) {

		StringBuffer sql = new StringBuffer();
		List<ConciliaBancoDto> datosExcel= new ArrayList<ConciliaBancoDto>();
		try{
			sql.append("\n SELECT e.Nom_Empresa, ce.No_Factura, ce.Fec_factura, rtrim(p.razon_social) AS razon_social, "
					+ "\n 	ce.Importe, ce.Id_Divisa, COALESCE(ce.referencia, '') AS referencia "
					+ "\n FROM Cobranza_Esperada ce, empresa e, persona p "
					+ "\n WHERE ce.No_Empresa = " + empresa +" "
					+ "\n 	AND ce.no_empresa = e.no_empresa "
					+ "\n 	AND ce.no_cliente = p.equivale_persona "
					+ "\n 	AND ce.estatus = 'P' "
					+ "\n 	AND ce.Fec_factura BETWEEN '" + fechaIni + "' AND '" + fechaFin + "' "
					+ "\n ORDER BY CAST(ce.No_Factura AS INTEGER) ");
			
			System.out.println("Datos Excel Facturas: "+ sql.toString());
			
			datosExcel = jdbcTemplate.query(sql.toString(), new RowMapper<ConciliaBancoDto>(){
				public ConciliaBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConciliaBancoDto cons = new ConciliaBancoDto();

					cons.setNomEmpresa(rs.getString("Nom_Empresa"));
					cons.setNoFactura(rs.getString("No_Factura"));
					cons.setFecFactura(rs.getDate("Fec_factura"));
					cons.setNoClienteS(rs.getString("razon_social"));
					cons.setImporte(rs.getDouble("Importe"));
					cons.setIdDivisa(rs.getString("Id_Divisa"));
					cons.setReferencia(rs.getString("referencia"));
					return cons;
				}});			
		 }catch(Exception e){
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Ingresos, C:IdentificacionIngresos, M:datosExcel");
		 }
		return datosExcel;
	}

	@Override
	public List<Map<String, String>> obtieneFacturas(int empresa, String fecIni, String fecFin) {
		List<Map<String, String>> facturas = new ArrayList<Map<String, String>>();
		StringBuffer sql = new StringBuffer();
		try {
			sql.append(" SELECT nom_empresa, No_Factura, Fec_factura, No_cliente, razon_social, Importe, Importe_solic, Id_Divisa, \n"
					+ " 	'PENDIENTE' estatus  \n"
					+ " FROM cobranza_esperada ce, empresa e, persona p  \n"
					+ " WHERE ce.no_Empresa = e.no_empresa  \n"
					+ " 	AND ce.no_cliente = p.equivale_persona  \n"
					+ " 	AND estatus = 'P'  \n"
					+ " 	AND ce.no_empresa = " + empresa + "  \n"
					+ " 	AND Fec_factura BETWEEN '" + fecIni + "' \n"
					+ " 		AND '" + fecFin + "'  \n"
					+ " ORDER BY No_Factura \n");
			
			System.out.println("Query facturas excel: \n" + sql.toString());
			
			facturas = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>() {
				@Override
				public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException {
					Map<String, String> factura = new HashMap<String, String>();
					factura.put("empresa", rs.getString("nom_empresa"));
					factura.put("factura", rs.getString("No_Factura"));
					factura.put("fecha", rs.getString("Fec_factura"));
					factura.put("noCliente", rs.getString("No_cliente"));
					factura.put("cliente", rs.getString("razon_social"));
					factura.put("importe", rs.getString("Importe"));
					factura.put("saldo", rs.getString("Importe_solic"));
					factura.put("divisa", rs.getString("Id_Divisa"));
					factura.put("estatus", rs.getString("estatus"));
					return factura;
				}
			});
		} catch (Exception e) {
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Ingresos, C:IdentificacionIngresosDao, M:obtieneFacturas");
		}
		return facturas;
	}

	@Override
	public List<Map<String, String>> obtieneDepositos(int empresa, String fecIni, String fecFin) {
		List<Map<String, String>> depositos = new ArrayList<Map<String, String>>();
		StringBuffer sql = new StringBuffer();
		try {
			sql.append(" SELECT e.nom_empresa, desc_banco, m.id_chequera, m.fec_valor, \n"
					+ " 	m.importe - (SELECT COALESCE(SUM(importe), 0) AS importe \n"
					+ " 		FROM cobranza_esperada \n"
					+ " 		WHERE folio_mov = m.no_folio_det) AS importe, \n "
					+ " 	m.id_divisa, m.observacion, 'PENDIENTE' estatus \n"
					+ " FROM movimiento m, empresa e, cat_banco c \n"
					+ " WHERE m.id_estatus_mov = 'A' \n"
					+ " 	AND m.id_banco = c.id_banco \n"
					+ " 	AND m.b_entregado <> 'C' \n"
					+ " 	AND m.no_empresa = " + empresa + " \n"
					+ " 	AND m.no_empresa = e.no_empresa \n"
					+ " 	AND m.fec_valor BETWEEN '" + fecIni + "' \n"
					+ " 		AND '" + fecFin + "' \n"
					+ " 	AND m.id_tipo_operacion BETWEEN 3100 \n"
					+ " 		AND 3199 \n"
					+ " 	AND ((m.concepto LIKE '%DEP FIRME%') \n"
					+ " 		OR (m.concepto LIKE '%DEP S B COBRO%' \n"
					+ " 			AND m.b_salvo_buen_cobro='S')) \n"
					+ " 	AND m.importe <> 0 \n");
			
			System.out.println("Query depositos excel: \n" + sql.toString());
			
			depositos = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>() {
				@Override
				public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException {
					Map<String, String> deposito = new HashMap<String, String>();
					deposito.put("empresa", rs.getString("nom_empresa"));
					deposito.put("banco", rs.getString("desc_banco"));
					deposito.put("cuenta", rs.getString("id_chequera"));
					deposito.put("fecha", rs.getString("fec_valor"));
					deposito.put("importe", rs.getString("importe"));
					deposito.put("divisa", rs.getString("id_divisa"));
					deposito.put("concepto", rs.getString("observacion"));
					deposito.put("estatus", rs.getString("estatus"));
					return deposito;
				}
			});
		} catch (Exception e) {
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Ingresos, C:IdentificacionIngresosDao, M:obtieneDepositos");
		}
		return depositos;
	}

	@Override
	public List<Map<String, String>> obtieneConciliados(int empresa, String fecIni, String fecFin) {
		List<Map<String, String>> conciliados = new ArrayList<Map<String, String>>();
		StringBuffer sql = new StringBuffer();
		try {
			sql.append(" SELECT nom_empresa, ce.fec_modif, id_chequera, no_folio_det, ce.No_Factura, ce.Fec_factura, ce.no_cliente, \n"
					+ " 	p.razon_social, ce.importe, m.fec_valor, m.importe deposito, m.id_divisa, desc_banco, m.observacion, 'CONCILIADO' estatus \n"
					+ " FROM Cobranza_Esperada ce, empresa e, persona p, movimiento m, cat_banco cb \n"
					+ " WHERE ce.no_Empresa = e.no_empresa \n"
					+ " 	AND ce.no_cliente = p.equivale_persona \n"
					+ " 	AND ce.folio_mov = m.no_folio_det \n"
					+ " 	AND m.id_banco = cb.id_banco \n"
					+ " 	AND ce.estatus = 'C' \n"
					+ " 	AND ce.no_empresa = " + empresa + " \n"
					+ " 	AND ce.Fec_Modif BETWEEN '" + fecIni + "' \n"
					+ " 		AND '" + fecFin + "' \n"
					+ " UNION ALL		\n"
					+ " SELECT nom_empresa, ce.fec_modif, id_chequera, no_folio_det, ce.No_Factura, ce.Fec_factura, ce.no_cliente, \n"
					+ " 	p.razon_social, ce.importe, h.fec_valor, h.importe deposito, h.id_divisa, desc_banco, h.observacion, 'CONCILIADO' estatus \n"
					+ " FROM Cobranza_Esperada ce, empresa e, persona p, hist_movimiento h, cat_banco cb \n"
					+ " WHERE ce.no_Empresa = e.no_empresa \n"
					+ " 	AND ce.no_cliente = p.equivale_persona \n"
					+ " 	AND ce.folio_mov = h.no_folio_det \n"
					+ " 	AND h.id_banco = cb.id_banco \n"
					+ " 	AND ce.estatus = 'C' \n"
					+ " 	AND ce.no_empresa = " + empresa + " \n"
					+ " 	AND ce.Fec_Modif BETWEEN '" + fecIni + "' \n"
					+ " 		AND '" + fecFin + "' \n"
					+ " ORDER BY ce.No_Factura \n");
			
			System.out.println("Query conciliados excel: \n" + sql.toString());
			
			conciliados = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>() {
				@Override
				public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException {
					Map<String, String> conciliado = new HashMap<String, String>();
					conciliado.put("empresa", rs.getString("nom_empresa"));
					conciliado.put("fechaConcilia", rs.getString("fec_modif"));
					conciliado.put("cuenta", rs.getString("id_chequera"));
					conciliado.put("folioSet", rs.getString("no_folio_det"));
					conciliado.put("factura", rs.getString("No_Factura"));
					conciliado.put("fechaFactura", rs.getString("Fec_factura"));
					conciliado.put("noCliente", rs.getString("no_cliente"));
					conciliado.put("cliente", rs.getString("razon_social"));
					conciliado.put("importe", rs.getString("importe"));
					conciliado.put("fechaDeposito", rs.getString("fec_valor"));
					conciliado.put("deposito", rs.getString("deposito"));
					conciliado.put("idDivisa", rs.getString("id_divisa"));
					conciliado.put("banco", rs.getString("desc_banco"));
					conciliado.put("concepto", rs.getString("observacion"));
					conciliado.put("estatus", rs.getString("estatus"));
					return conciliado;
				}
			});
		} catch (Exception e) {
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Ingresos, C:IdentificacionIngresosDao, M:obtieneFacturas");
		}
		return conciliados;
	}

	@Override
	public List<PersonaDto> obtenerClientes(String cliente) {
		StringBuffer sql = new StringBuffer();
		List<PersonaDto> personas= new ArrayList<PersonaDto>();
		try{
			sql.append("\n SELECT equivale_persona, razon_social"
					+ "\n FROM persona "
					+ "\n WHERE (razon_social like '%" + cliente + "%' "
					+ "\n 	OR equivale_persona like '%" + cliente + "%') "
					+ "\n 	AND id_tipo_persona = 'C' ");
			
			System.out.println("Query Clientes: "+ sql.toString());
			
			personas = jdbcTemplate.query(sql.toString(), new RowMapper<PersonaDto>(){
				public PersonaDto mapRow(ResultSet rs, int idx) throws SQLException {
					PersonaDto cons = new PersonaDto();
					cons.setEquivalePersona(rs.getString("equivale_persona"));
					cons.setRazonSocial(rs.getString("razon_social"));
					return cons;
				}});			
		 }catch(Exception e){
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Ingresos, C:IdentificacionIngresos, M:datosExcel");
		 }
		return personas;
	}

	@Override
	public String enviarAnticipo(int folioDet, String cliente, double importe, int noEmpresa, String divisa) {
		String resultado = "";
		StringBuffer sql = new StringBuffer();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		try {
			sql.append("\n INSERT INTO Cobranza_Esperada (Folio_Cob, No_Empresa, No_Docto, Secuencia, No_Factura, Fec_factura, Fec_Valor, ");
			sql.append("\n 	No_cliente, Importe, Importe_solic, Id_Divisa, Concepto, Forma_Pago, Id_rubro, Id_Grupo, Referencia, ");
			sql.append("\n 	Folio_Det_Mov, Estatus, Usuario_Alta, Fec_alta, Usuario_Modif, Fec_Modif, Fecha_exp, origen, causa_rech, ");
			sql.append("\n 	folio_mov, Importe_pendiente, usuario_concilia, fec_concilia, concilia_referenciado, concilia_tipo,"); 
	 		sql.append("\n 	conciliacion, estatus_proceso, codigo_res)");
			sql.append("\n VALUES ("
					+ consultasGenerales.obtenFolio("Folio_Cob")
					+ ", "+ noEmpresa + ", 'VIR-" + folioDet + "', 1, 'VIR-" + folioDet + "', GETDATE(), GETDATE(), '" + cliente.trim() 
					+ "', " + importe + ", ");
			sql.append("\n 	" + importe + ", '" + divisa + "', 'ANTICIPO', 3, 51001, 0, NULL, " + folioDet + ", 'C', ");
			sql.append("\n 	NULL, GETDATE(), NULL, GETDATE(), GETDATE(), NULL, NULL, " + folioDet + ", " + importe + ", ");
			sql.append("\n 	NULL, NULL, NULL, NULL, NULL, NULL, NULL )");
			
			System.out.println("Insert nueva fila cobranza : "+ sql.toString());
			
			if (jdbcTemplate.update(sql.toString()) > 0) {
				sql = new StringBuffer();
				sql.append(" UPDATE movimiento SET id_tipo_operacion = 3100 \n");
				sql.append(" , b_entregado = 'C' \n");
				sql.append(" , no_cliente = (SELECT no_persona FROM persona WHERE equivale_persona = '" + cliente + "') \n ");
				sql.append(" WHERE no_folio_det = " + folioDet);

				if (jdbcTemplate.update(sql.toString()) > 0) {
					resultado = "Datos actualizados con exito. ";
				} else {
					resultado = "No se pudo actualizar el deposito. ";
				}
			} else {
				resultado = "No se pudo crear la factura virtual. ";
			}
		} catch (Exception e) {
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Ingresos, C:IdentificacionIngresosDao, M:enviaAnticipo");
		}
		return resultado;
	}

	@Override
	public List<MovimientoDto> llenarMovsBancos2(CriteriosBusquedaDto dto) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
