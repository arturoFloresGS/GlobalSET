package com.webset.set.coinversion.dao.impl;

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

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.webset.set.coinversion.dao.CoinversionDao;
import com.webset.set.coinversion.dto.ArbolEmpresaDto;
import com.webset.set.coinversion.dto.ArbolEmpresaFondeoDto;
import com.webset.set.coinversion.dto.BarridoChequerasDto;
import com.webset.set.coinversion.dto.ControlFondeoChequesDto;
import com.webset.set.coinversion.dto.DivisasEncontradasDto;
import com.webset.set.coinversion.dto.HistSaldoDto;
import com.webset.set.coinversion.dto.InteresesDto;
import com.webset.set.coinversion.dto.ParamBusquedaFondeoDto;
import com.webset.set.coinversion.dto.ParamRetornoFondeoAutDto;
import com.webset.set.coinversion.dto.ParametroDto;
import com.webset.set.coinversion.dto.SaldoChequeraDto;
import com.webset.set.coinversion.dto.SaldoDto;
import com.webset.set.coinversion.dto.TmpSdoPromDto;
import com.webset.set.coinversion.dto.TmpTraspasoFondeoDto;
import com.webset.set.coinversion.dto.VencimientoInversionDto;
import com.webset.set.seguridad.dto.EmpresaDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.CatCtaBancoDto;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.OrdenInversionDto;
import com.webset.set.utilerias.dto.Retorno;
import com.webset.set.utilerias.dto.StoreParamsComunDto;
import com.webset.utils.tools.Utilerias;

@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
public class CoinversionDaoImpl implements CoinversionDao{

	private JdbcTemplate jdbcTemplate; 
	private Bitacora bitacora = new Bitacora();
	private Funciones funciones = new Funciones();
	private GlobalSingleton globalSingleton;
	private static Logger logger = Logger.getLogger(CoinversionDaoImpl.class);
	ConsultasGenerales consultasGenerales;
	
	
	public List<LlenaComboGralDto> consultarDatosEmpresaRaiz(boolean bExistentes){
		StringBuffer sql = new StringBuffer();
		List<LlenaComboGralDto> listDatos = new ArrayList<LlenaComboGralDto>();
		try{
		    if(bExistentes)
		    {
		    	sql.append("\n Select distinct a.tipo_arbol as ID, a.desc_arbol as describ, aef.empresa_origen as empresa");
		    	sql.append("\n From cat_arbol a ");
		    	sql.append("\n left outer join arbol_empresa_fondeo aef ");
		    	sql.append("\n on (aef.tipo_arbol = a.tipo_arbol) ");
		    	sql.append("\n where orden=0");
		    }
		    else{
		    	sql.append("Select distinct e.no_empresa as ID, e.nom_empresa as describ ");
		    	sql.append("\n	From empresa e");
		        sql.append("\n	WHERE e.no_empresa not in");
		        sql.append("\n	(select distinct tipo_arbol");
		        sql.append("\n	from arbol_empresa_fondeo)");
		    }    
		    System.out.println("rrubio"+sql.toString());
		    listDatos = jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("describ"));
					cons.setCampoUno(rs.getString("empresa"));
					return cons;
				}});

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarDatosEmpresaRaiz");
		}
		return listDatos;
	}
	
	public List<LlenaComboGralDto> consultarDatosBancoConcentradora(boolean bConcentradora,
			int iIdEmpresa, String idDivisa){
		StringBuffer sql = new StringBuffer();
		List<LlenaComboGralDto> listDatos = new ArrayList<LlenaComboGralDto>();
		try{
			sql.append(" Select distinct cb.id_banco as ID, cb.desc_banco as DESCRIPCION ");
		    sql.append(" From cat_banco cb ,cat_cta_banco ccb ");
		    sql.append(" Where ccb.id_banco = cb.id_banco ");
		    
		    if(bConcentradora)
		    {
		    	sql.append(" and ccb.no_empresa = " + iIdEmpresa);
		        sql.append(" and  ccb.tipo_chequera in ('P','C','M') ");
		        sql.append(" and  ccb.id_divisa = '" + idDivisa + "'");
		    }
		    
		    listDatos = jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>(){
		    	public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		LlenaComboGralDto cons = new LlenaComboGralDto();
			    		cons.setId(rs.getInt("ID"));
			    		cons.setDescripcion(rs.getString("DESCRIPCION"));
		    		return cons;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarDatosBancoConcentradora");
		}
		return listDatos;
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
			System.out.println("Query Combo Chequeras: " + sql.toString());
			
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

	public List<ParamRetornoFondeoAutDto> consultarFondeoAutomatico(ParamBusquedaFondeoDto dto){
		StringBuffer sql = new StringBuffer();
		List<ParamRetornoFondeoAutDto> listDatos = new ArrayList<ParamRetornoFondeoAutDto>();
		try{
			sql.append(" SELECT ");
		    sql.append("\n     ( SELECT coalesce(sum(m.importe), 0) ");
		    sql.append("\n     FROM movimiento m ");
		    sql.append("\n     WHERE m.no_empresa = e.no_empresa ");
		    sql.append("\n     and id_tipo_operacion = 3818 ");
		    sql.append("\n     and id_estatus_mov = 'L' ) as Prestamos, ");
		    sql.append("\n     e.no_empresa,e.nom_empresa,");
		    sql.append("\n     coalesce(cb.desc_banco, '') as desc_banco, ");
		    sql.append("\n     coalesce(cb.id_banco, 0) as id_banco, ");
		    sql.append("\n     coalesce(ccb.id_chequera, '') as id_chequera, ");
		    sql.append("\n     coalesce(ccb.saldo_final, 0) as saldo_chequera, ");
		    sql.append("\n     coalesce(ccb.saldo_minimo, 0) as saldo_minimo_chequera, ");
		    sql.append("\n     ( SELECT coalesce(sum(importe), 0) ");
		    sql.append("\n     FROM movimiento m ");
		    sql.append("\n     WHERE e.no_empresa = m.no_empresa ");
		    sql.append("\n     and m.id_tipo_movto = 'I' ");
		    
		    sql.append("\n	and m.id_tipo_operacion in (3806, 3706, 3808, 3708) ");
		    
		    
		    if(ConstantesSet.gsDBM.equals("DB2")) 
		    	sql.append("\n	and m.fec_valor = '" + funciones.ponerFecha(dto.getFechaHoy())+ "' ");
		    else
		    	sql.append("\n	and m.fec_valor = '" + funciones.ponerFecha(dto.getFechaHoy()) + "' ");
		    
		    
		    if(dto.getIdBanco() > 0)
		        sql.append("\n	and m.id_banco = " + dto.getIdBanco());
	
		    if(dto.isBTodaslasChequeras())
		        sql.append("\n	and m.id_chequera = ccb.id_chequera ");
		    
		    sql.append("\n	and m.id_estatus_mov in ('L','A')) as importeF, ");
		   
		    sql.append("\n	CASE WHEN '" + dto.getIdDivisa() + "' = 'MN' ");
		    sql.append("\n	THEN 1 ELSE ");
		    sql.append("\n	( SELECT coalesce(valor,0) ");
		    sql.append("\n	FROM valor_divisa ");
		    sql.append("\n	WHERE fec_divisa = ");
		    sql.append("\n	( SELECT max(fec_divisa) ");
		    sql.append("\n	FROM valor_divisa ");
		    sql.append("\n	WHERE id_divisa = '" + dto.getIdDivisa() + "') ");
		    sql.append("\n	and id_divisa = '" + dto.getIdDivisa() + "' ) ");
		    sql.append("\n	END as TipoCambio, ");

		    sql.append("\n	( SELECT sum(m.importe) ");
		    sql.append("\n	FROM movimiento m ");
		    
		    if(dto.isBUbicacionCCM())
		    {
		        sql.append("\n	WHERE ( m.id_tipo_operacion = 3200 ");
		        sql.append("\n	or m.id_tipo_operacion between 3700 and 3799 ) ");
		        sql.append("\n	and m.id_tipo_movto = 'E' ");
		        sql.append("\n and m.id_estatus_mov not in ('X','Y','Z','W','G')");
		        sql.append("\n and m.no_empresa = e.no_empresa ");
		        
		        if (ConstantesSet.gsDBM.equals("DB2")) 
		            sql.append("\n	and m.fec_valor >= '" +funciones.ponerFecha(dto.getFechaHoy())+ "'");
		        else
		            sql.append("\n	and m.fec_valor >= '" + funciones.ponerFecha(dto.getFechaHoy()) + "'");
		        
		        sql.append("\n	and m.id_chequera = ccb.id_chequera ) as pm ");
		    }
	        else
	        {
		        sql.append("\n	WHERE ( ( m.id_tipo_operacion = 3000 ");
		        sql.append("\n	and m.id_estatus_mov in ('N','C') ");
		        
		        if(ConstantesSet.gsDBM.equals("DB2")) 
		            sql.append("\n	and m.fec_propuesta = '" + funciones.ponerFecha(dto.getFechaHoy())+  "' ");
		        else
		            sql.append("\n	and m.fec_propuesta = '" + funciones.ponerFecha(dto.getFechaHoy())+ "' ");
		        
		        sql.append("\n	and m.cve_control is not null ");
		        sql.append("\n	and ccb.id_chequera = ");
		        sql.append("\n	CASE WHEN left(m.cve_control, 1) = 'M' ");
		        sql.append("\n	THEN m.id_chequera ");
		        sql.append("\n	ELSE ");
		        sql.append("\n	( SELECT coalesce(ccbv.id_chequera, '') ");
		        sql.append("\n	FROM cat_cta_banco ccbv ");
		        sql.append("\n	WHERE ccbv.no_empresa = m.no_empresa ");
		        sql.append("\n	AND ccbv.id_divisa = m.id_divisa ");
		        sql.append("\n	AND ( CASE WHEN m.id_forma_pago = 1 ");
		        sql.append("\n	THEN ccbv.b_cheque ");
		        sql.append("\n	WHEN m.id_forma_pago = 3 ");
		        sql.append("\n	THEN ccbv.b_transferencia ");
		        sql.append("\n	WHEN m.id_forma_pago = 9 ");
		        sql.append("\n	THEN ccbv.b_cheque_ocurre ");
		        sql.append("\n	END ) = 'S' )");
		        sql.append("\n	END ");
		        sql.append("\n	) or ");
			    sql.append("\n	( m.id_tipo_operacion = 3200 ");
		        
		        if(ConstantesSet.gsDBM.equals("DB2"))
		             sql.append("\n	and m.fec_valor = '" + funciones.ponerFecha(dto.getFechaHoy()) + "' ");
		        else
		            sql.append("\n	and m.fec_valor = '" + funciones.ponerFecha(dto.getFechaHoy()) + "' ");
		        
		        sql.append("\n	and m.id_estatus_mov not in ('A','X','Y','Z','W','G') ");
		        
		        sql.append("\n	and m.id_chequera = ccb.id_chequera ) or ");
	
		        sql.append("\n	( m.id_tipo_operacion between 3700 and 3799 and id_tipo_operacion not in (3705, 3709) ");
		        
		        if(ConstantesSet.gsDBM.equals("DB2"))
		            sql.append("\n	and m.fec_valor = '" + funciones.ponerFecha(dto.getFechaHoy())+ "' ");
		        else
		            sql.append("\n	and m.fec_valor = '" + funciones.ponerFecha(dto.getFechaHoy()) + "' ");
		        
		        
		        sql.append("\n	and m.id_estatus_mov = 'A' ");
		        
		        sql.append("\n	and m.id_chequera = ccb.id_chequera ) or ");
		        sql.append("\n	( m.id_tipo_operacion between 3800 and 3899 and m.id_tipo_operacion not in (3818, 3805, 3809) ");
		        
		        if(ConstantesSet.gsDBM.equals("DB2")) 
		            sql.append("\n	and m.fec_valor = '" +funciones.ponerFecha(dto.getFechaHoy())+ "' ");
		        else
		            sql.append("\n	and m.fec_valor = '" + funciones.ponerFecha(dto.getFechaHoy()) + "' ");
		        
		        sql.append("\n	and m.id_estatus_mov = 'L' ");
		        sql.append("\n	and m.id_chequera = ccb.id_chequera ) ) ");
	
		        sql.append("\n	and m.id_tipo_movto = 'E' ");
		        sql.append("\n	and m.no_empresa = e.no_empresa ) as pm ");
	        }
		    
		    sql.append("\n	,0 as pmCHEQUES");

    	    sql.append("\n	FROM empresa e, cuenta c, cat_cta_banco ccb, cat_banco cb ");
    	    sql.append("\n	WHERE c.no_empresa = " + dto.getIdEmpresa());
    	    sql.append("\n	and ccb.no_empresa = e.no_empresa ");
    	    sql.append("\n	and ccb.tipo_chequera in ('P','M') ");
    	    
    	    if(!dto.isBTodaslasChequeras())
            	sql.append("\n	and ccb.b_traspaso = 'S' ");
    	    
	        sql.append("\n	and ccb.id_divisa = '" + dto.getIdDivisa()+ "' ");
	
	        if(dto.getIdBanco() != 0)
	            sql.append("\n	and ccb.id_banco = " + dto.getIdBanco());
	        
	        sql.append("\n	and cb.id_banco = ccb.id_banco ");
	        sql.append("\n	and e.no_empresa in ");
	        sql.append("\n	( SELECT no_empresa ");
	        sql.append("\n	FROM usuario_empresa ");
	        sql.append("\n	WHERE no_usuario = " +dto.getIdUsuario()+ " ) ");
	        sql.append("\n	and e.no_controladora = " + dto.getIdEmpresa());
	        sql.append("\n	and c.no_cuenta = e.no_empresa ");
	        sql.append("\n	and c.id_tipo_cuenta = 'T' ");
	        sql.append("\n	and c.no_linea = " + dto.getNoLinea());
	        sql.append("\n	GROUP BY e.no_empresa, e.nom_empresa, cb.id_banco, ");
	        sql.append("\n	cb.desc_banco, ccb.id_chequera, ccb.saldo_final, ");
	        sql.append("\n	ccb.saldo_minimo ");
	        sql.append("\n	ORDER BY e.no_empresa");
	        
	        listDatos = jdbcTemplate.query(sql.toString(), new RowMapper<ParamRetornoFondeoAutDto>(){
	        	public ParamRetornoFondeoAutDto mapRow(ResultSet rs, int idx)throws SQLException{
	        		ParamRetornoFondeoAutDto rsDto = new ParamRetornoFondeoAutDto();
			    		rsDto.setPrestamos(rs.getDouble("Prestamos"));
			    		rsDto.setNoEmpresaDestino(rs.getInt("no_empresa"));
			    		rsDto.setNomEmpresaDestino(rs.getString("nom_empresa"));
			    		rsDto.setDescBanco(rs.getString("desc_banco"));
			    		rsDto.setIdBanco(rs.getInt("id_banco"));
			    		rsDto.setIdChequera(rs.getString("id_chequera"));
			    		rsDto.setSaldoChequera(rs.getDouble("saldo_chequera"));
			    		rsDto.setSaldoMinimoChequera(rs.getDouble("saldo_minimo_chequera"));
			    		rsDto.setImporteF(rs.getDouble("importeF"));
			    		rsDto.setTipoCambio(rs.getDouble("TipoCambio"));
			    		rsDto.setPm(rs.getDouble("pm"));
			    		rsDto.setPmCheques(rs.getDouble("pmCheques"));
	        		return rsDto;
	        	}
	        });
	        
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarFondeoAutomatico");
		}
		return listDatos;
	}
	
	
	public List<ParamRetornoFondeoAutDto> consultarFondeoArbol(ParamBusquedaFondeoDto dto){
		StringBuffer sql = new StringBuffer();
		List<ParamRetornoFondeoAutDto> listDatos = new ArrayList<ParamRetornoFondeoAutDto>();
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			sql.append(" SELECT orden, ");
		    sql.append("\n	( SELECT coalesce(sum(m.importe), 0)");
		    sql.append("\n	FROM movimiento m ");
		    sql.append("\n	WHERE m.no_empresa = e.no_empresa");
		    sql.append("\n	and id_tipo_operacion = 3818 ");
		    sql.append("\n	and id_estatus_mov = 'L' ) as Prestamos,");
		    sql.append("\n	e1.no_empresa as no_empresa_origen ,e1.nom_empresa as nom_empresa_origen,");
		    sql.append("\n	e.no_empresa as no_empresa_destino ,e.nom_empresa as nom_empresa_destino,");
		    sql.append("\n	coalesce(cb.desc_banco, '') as desc_banco,");
		    sql.append("\n	coalesce(cb.id_banco, 0) as id_banco,");
		    sql.append("\n	coalesce(ccb.id_chequera, '') as id_chequera,");
		    sql.append("\n	coalesce(ccb.saldo_final, 0) as saldo_chequera,");
		    sql.append("\n	coalesce(ccb.saldo_minimo, 0) as saldo_minimo_chequera,");
		    sql.append("\n	( SELECT coalesce(sum(importe), 0)");
		    sql.append("\n	FROM movimiento m");
		    sql.append("\n	WHERE e.no_empresa = m.no_empresa");
		    sql.append("\n	and m.id_tipo_movto = 'I'");
		    
		    sql.append("\n	and m.id_tipo_operacion in (3806, 3706, 3808, 3708,3814,3714)");
		    	    
    	    if(ConstantesSet.gsDBM.equals("DB2"))
    	        sql.append("\n	and m.fec_valor = '" +funciones.ponerFecha(dto.getFechaHoy())+ "'");
    	    else
    	        sql.append("\n	and m.fec_valor = '" + funciones.ponerFecha(dto.getFechaHoy()) + "'");
    	    
    	    sql.append("\n	and m.no_factura = '" + dto.getIdEmpresa()+ "'");

    	    if(dto.getIdBanco() != 0)
    	        sql.append("\n	and m.id_banco = " + dto.getIdBanco());

    	    if(dto.isBTodaslasChequeras())
    	        sql.append("\n	and m.id_chequera = ccb.id_chequera");
    	    
    	    sql.append("\n	and m.id_estatus_mov in ('L','A')) as importeF,");
    	    
    	    sql.append("\n	CASE WHEN '" + dto.getIdDivisa() + "' = 'MN' ");
    	    sql.append("\n  THEN 1 ELSE");
    	    sql.append("\n  ( SELECT coalesce(valor,0) ");
    	    sql.append("\n  FROM valor_divisa ");
    	    sql.append("\n  WHERE fec_divisa = ");
    	    sql.append("\n  ( SELECT max(fec_divisa) ");
    	    sql.append("\n  FROM valor_divisa ");
    	    sql.append("\n  WHERE id_divisa = '" + dto.getIdDivisa() + "') ");
    	    sql.append("\n  and id_divisa = '" + dto.getIdDivisa() + "' ) ");
    	    sql.append("\n  END as TipoCambio,");

    	    sql.append("\n  ( SELECT coalesce(sum(m.importe),0)");
    	    sql.append("\n  FROM movimiento m");
    	    
    	    if(dto.isBUbicacionCCM())
    	    {
	            if(ConstantesSet.gsDBM.equals("DB2"))
	                sql.append("\n	WHERE ((  m.id_tipo_operacion = 3200 and  m.fec_valor >= '" + funciones.ponerFecha(dto.getFechaHoy()) + "')");
	            else
	                sql.append("\n	WHERE ((  m.id_tipo_operacion = 3200 and  m.fec_valor >= '" + funciones.ponerFecha(dto.getFechaHoy()) + "')");
	            
	            sql.append("\n	or ( m.id_tipo_operacion = 3000");
	            if(globalSingleton.obtenerValorConfiguraSet(3003).equals("SI"))
	            	sql.append("\n	and m.id_estatus_mov in('I','R','V','P')");
	            
	            sql.append("\n	AND m.no_folio_det in(select cch.no_folio_det");
	            sql.append("\n	from  control_fondeo_cheques cch where cch.id_chequera = m.id_chequera ))");
	            
	            if(ConstantesSet.gsDBM.equals("DB2"))
	                sql.append("\n	or (m.id_tipo_operacion = 3701 and   m.fec_valor >= '" + funciones.ponerFecha(dto.getFechaHoy())+ "' and (origen_mov is null or  origen_mov <> 'SET') ))");
	            else
	                sql.append("\n	or (m.id_tipo_operacion between 3701 and 3799 and   m.fec_valor >= '" + funciones.ponerFecha(dto.getFechaHoy())+ "' ))");
	            
	            sql.append("\n	and m.id_tipo_movto = 'E'");
	            sql.append("\n	and m.id_estatus_mov not in ('X','Y','Z','W','G')");
	            
	            if(dto.getSTipoBusqueda().equals("I"))
	                sql.append("\n	and coalesce(m.id_contable,'') in( select cd.clase_docto from arbol_clase_docto cd " +
	                		"where cd.tipo_busqueda = 'I' and cd.tipo_arbol = " + dto.getIdEmpresa() + ")");
	            else
	                sql.append("\n	and coalesce(m.id_contable,'') NOT in( select cd.clase_docto from arbol_clase_docto cd " +
	                		"where  cd.tipo_busqueda = 'D' and cd.tipo_arbol = " + dto.getIdEmpresa() + ")");
	            
	            
	            sql.append("\n       and m.id_chequera = ccb.id_chequera  ) as pm,");
	            
	            sql.append("\n	( SELECT coalesce(sum(m.importe),0)");
	            sql.append("\n       FROM movimiento m");
	            sql.append("\n   WHERE m.id_tipo_operacion = 3000");
	            sql.append("\n   AND m.id_forma_pago = 1");
	            sql.append("\n   AND m.no_folio_det not in(select cch.no_folio_det ");
	            sql.append("\n   from  control_fondeo_cheques cch where cch.id_chequera = m.id_chequera )");
	            sql.append("\n       and m.id_tipo_movto = 'E'");
	            
	            if(globalSingleton.obtenerValorConfiguraSet(3003).equals("SI"))
	            	sql.append("\n       and m.id_estatus_mov  IN('I','R','V','P')");
	            
	            if(dto.getSTipoBusqueda().equals("I"))
	                sql.append("\n	and  coalesce(m.id_contable,'') in( select cd.clase_docto from arbol_clase_docto cd " +
	                		"where cd.tipo_busqueda = 'I' and cd.tipo_arbol = " +dto.getIdEmpresa()+ ")");
	            else
	                sql.append("\n	and coalesce(m.id_contable,'') NOT in( select cd.clase_docto from arbol_clase_docto cd " +
	                		"where  cd.tipo_busqueda = 'I' and cd.tipo_arbol = " +dto.getIdEmpresa()+ ") ");
	            
	            sql.append("and m.id_chequera = ccb.id_chequera  ) as pmCheques ");
    	    }
    	    else
	        {
	            sql.append("\n	WHERE ( ( m.id_tipo_operacion = 3000 ");
	            sql.append("\n	and m.id_estatus_mov in ('N','C') ");
	            sql.append("\n	and m.fec_propuesta = '" + funciones.ponerFecha(dto.getFechaHoy())+ "' ");
	            sql.append("\n	and m.cve_control is not null ");
	            sql.append("\n	and ccb.id_chequera = ");
	            sql.append("\n	CASE WHEN left(m.cve_control, 1) = 'M' ");
	            sql.append("\n	THEN m.id_chequera ");
	            sql.append("\n	ELSE ");
	            sql.append("\n	( SELECT coalesce(ccbv.id_chequera, '') ");
	            sql.append("\n	FROM cat_cta_banco ccbv ");
	            sql.append("\n	WHERE ccbv.no_empresa = m.no_empresa ");
	            sql.append("\n	AND ccbv.id_divisa = m.id_divisa ");
	            sql.append("\n	AND ( CASE WHEN m.id_forma_pago = 1 ");
	            sql.append("\n	THEN ccbv.b_cheque ");
	            sql.append("\n	WHEN m.id_forma_pago = 3 ");
	            sql.append("\n	THEN ccbv.b_transferencia ");
	            sql.append("\n	WHEN m.id_forma_pago = 9 ");
	            sql.append("\n	THEN ccbv.b_cheque_ocurre ");
	            sql.append("\n	END ) = 'S' )");
	            sql.append("\n	END ");
	            sql.append("\n	) or ");
	
	            sql.append("\n	( m.id_tipo_operacion = 3200 ");
	            
	            if(ConstantesSet.gsDBM.equals("DB2"))
	            	
	                sql.append("\n	and m.fec_valor = '" + funciones.ponerFecha(dto.getFechaHoy())+ "' ");
	            else
	                sql.append("\n	and m.fec_valor = '" + funciones.ponerFecha(dto.getFechaHoy())+ "' ");
	            
	            
	            sql.append("\n	and m.id_estatus_mov not in ('A','X','Y','Z','W','G') ");
	           
	            sql.append("\n	and m.id_chequera = ccb.id_chequera ) or ");
	
	            sql.append("\n  ( m.id_tipo_operacion between 3701 and 3799 and id_tipo_operacion not in (3705, 3709) ");
	            
	            if(ConstantesSet.gsDBM.equals("DB2"))
	                sql.append("\n	and m.fec_valor = '" + funciones.ponerFecha(dto.getFechaHoy())+ "' ");
	            else
	                sql.append("\n	and m.fec_valor = '" + funciones.ponerFecha(dto.getFechaHoy())+ "' ");
	            
	            sql.append("\n	and m.id_estatus_mov = 'A' ");
	           
	            sql.append("\n	and m.id_chequera = ccb.id_chequera ) or ");
	            sql.append("\n	( m.id_tipo_operacion between 3801 and 3899 and m.id_tipo_operacion not in (3818, 3805, 3809) ");
	            
	            if(ConstantesSet.gsDBM.equals("DB2"))
	                sql.append("\n	and m.fec_valor = '" + funciones.ponerFecha(dto.getFechaHoy())+ "' ");
	            else
	                sql.append("\n	and m.fec_valor = '" + funciones.ponerFecha(dto.getFechaHoy())+ "' ");
	            
	            sql.append("\n	and m.id_estatus_mov = 'L' ");
	            sql.append("\n	and m.id_chequera = ccb.id_chequera ) ) ");
	
	            sql.append("\n	and m.id_tipo_movto = 'E' ");
	         
	            sql.append("\n	) as pm ");
	        }
	            
                sql.append("\n	FROM empresa e, empresa e1, arbol_empresa_fondeo c, cat_cta_banco ccb, cat_banco cb ");
			    sql.append("\n	WHERE c.tipo_arbol = " + dto.getIdEmpresa());
			    sql.append("\n	and c.tipo_valor = 'NECESIDAD'");
			    sql.append("\n	and ccb.no_empresa = e.no_empresa ");
			    sql.append("\n	and ccb.tipo_chequera in ('P','M') ");
			
			    if(!dto.isBTodaslasChequeras())
			        sql.append("\n	and ccb.b_traspaso = 'S' ");
			
			    sql.append("\n	and ccb.id_divisa = '" + dto.getIdDivisa() +  "' ");
			
			    if(dto.getIdBanco() > 0)
			        sql.append("\n	and ccb.id_banco = " + dto.getIdBanco());
			
			    sql.append("\n	and cb.id_banco = ccb.id_banco ");
			    sql.append("\n	and e.no_empresa in ");
			    sql.append("\n	( SELECT no_empresa ");
			    sql.append("\n	FROM usuario_empresa ");
			    sql.append("\n	WHERE no_usuario = " + dto.getIdUsuario()+ " ) ");
			    sql.append("\n	and c.empresa_destino = e.no_empresa");
			    sql.append("\n	and c.empresa_origen = e1.no_empresa");
			    sql.append("\n	and c.orden > 0");
			    sql.append("\n	GROUP BY orden,e.no_empresa, e.nom_empresa, e1.no_empresa,e1.nom_empresa,cb.id_banco, ");
			    sql.append("\n	cb.desc_banco, ccb.id_chequera, ccb.saldo_final, ");
			    sql.append("\n	ccb.saldo_minimo ");
			    sql.append("\n	ORDER BY e.no_empresa");
			    logger.info("Queri Fondeo Arbol "+ sql.toString());
			    listDatos =  jdbcTemplate.query(sql.toString(), new RowMapper<ParamRetornoFondeoAutDto>(){
			    	public ParamRetornoFondeoAutDto mapRow(ResultSet rs, int idx)throws SQLException{
			    		ParamRetornoFondeoAutDto rsDto = new ParamRetornoFondeoAutDto();
				    		rsDto.setOrden(rs.getInt("orden"));
				    		rsDto.setPrestamos(rs.getDouble("Prestamos"));
				    		rsDto.setNoEmpresaOrigen(rs.getInt("no_empresa_origen"));
				    		rsDto.setNomEmpresaOrigen(rs.getString("nom_empresa_origen"));
				    		rsDto.setNoEmpresaDestino(rs.getInt("no_empresa_destino"));
				    		rsDto.setNomEmpresaDestino(rs.getString("nom_empresa_destino"));
				    		rsDto.setDescBanco(rs.getString("desc_banco"));
				    		rsDto.setIdBanco(rs.getInt("id_banco"));
				    		rsDto.setIdChequera(rs.getString("id_chequera"));
				    		rsDto.setSaldoChequera(rs.getDouble("saldo_chequera"));
				    		rsDto.setSaldoMinimoChequera(rs.getDouble("saldo_minimo_chequera"));
				    		rsDto.setImporteF(rs.getDouble("importeF"));
				    		rsDto.setTipoCambio(rs.getDouble("TipoCambio"));
				    		rsDto.setPm(rs.getDouble("pm"));
				    		rsDto.setPmCheques(rs.getDouble("pmCheques"));
			    		return rsDto;
			    	}
			    });
	        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarFondeoArbol");
		}
		return listDatos;
	}
	
	/**
	 * Este metodo obtiene el tipo de cambio
	 * de la fecha de hoy de la base de datos
	 * 	Public Function FunTipoCambioHoy() As ADODB.Recordset
	 */
	public double consultarTipoCambioHoy(){
		StringBuffer sql = new StringBuffer();
		BigDecimal bdTipoCambio = new BigDecimal(2.1);
		double uTipoCambio = 0;
		try{
			sql.append("Select valor from valor_divisa " +
					"where id_divisa ='DLS' " +
					"and fec_divisa = (select fec_hoy from fechas)");
			bdTipoCambio = jdbcTemplate.queryForObject(sql.toString(), BigDecimal.class);
			uTipoCambio = bdTipoCambio.doubleValue();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarTipoCambioHoy");
			return 0;
		}
		return uTipoCambio;
	}
	
	/**
	 * Este metodo obtiene los bancos asignados al arbol raiz
	 * para mostralos directamente en un combo
	 * @param iIdRaiz
	 * @param sIdDivisa
	 * @return
	 */
	public List<LlenaComboGralDto> consultarBancosRaiz(int iIdRaiz, String sIdDivisa){
		List<LlenaComboGralDto> listBancos = new ArrayList<LlenaComboGralDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("SELECT c.id_banco as ID, cb.desc_banco as DESCRIPCION");
			sql.append("\n	FROM arbol_empresa_fondeo a, cat_cta_banco c, cat_banco cb");
			sql.append("\n	WHERE a.empresa_origen =" + iIdRaiz);
			sql.append("\n	AND a.orden = 0");
			sql.append("\n	AND a.empresa_origen = c.no_empresa");
		//	sql.append("\n	AND c.b_traspaso = 'S'");
			sql.append("\n	AND c.tipo_chequera <> 'I'");
			sql.append("\n	AND c.id_divisa = '"+ sIdDivisa +"'");
			sql.append("\n	AND cb.id_banco = c.id_banco ");
			sql.append("\n GROUP BY c.id_banco,cb.desc_banco ");
			
			
			System.out.println("Query Banco Fondeo: " + sql.toString());
			
			listBancos = jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException{
					LlenaComboGralDto rsDto = new LlenaComboGralDto();
						rsDto.setId(rs.getInt("ID"));
						rsDto.setDescripcion(rs.getString("DESCRIPCION"));
					return rsDto;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarBancosRaiz");
		}
		return listBancos;
	}
	
	/*Public Function FunSQLSelectIdDivisaSoin(ByVal pvs_idDivisa As String) As ADODB.Recordset*/
	public String consultarIdDivisaSoin(String sIdDivisa){
		StringBuffer sql = new StringBuffer();
		String sDivisa = "";
		try{
		    sql.append("select ");
		    sql.append("\n	coalesce(id_divisa_soin, '0') as id_divisa_soin ");
		    sql.append("\n	from ");
		    sql.append("\n	cat_divisa ");
		    sql.append("\n	where ");
		    sql.append("\n	clasificacion='D' ");
		    sql.append("\n	and id_divisa='" + sIdDivisa + "'");
			sDivisa = jdbcTemplate.queryForObject(sql.toString(), String.class);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarIdDivisaSoin");
			return "";
		}
		return sDivisa;
	}
	
	/**
	 * Public Function FunSQLSelect869(ByVal pvvValor1 As Integer, ByVal pvvValor2 As Integer, _
     *ByVal pvvValor3 As String) As ADODB.Recordset 
	 * @param noEmpresaCon
	 * @param noEmpresaDes
	 * @param idDivisa
	 * @return
	 */
	public double consultarSaldoCoinversora(int noEmpresaCon, int noEmpresaDes, String idDivisa){
		StringBuffer sql = new StringBuffer();
		BigDecimal bdImporte = new BigDecimal(0);
		double uImporte = 0;
		try{
			if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
			{
                sql.append("SELECT coalesce(s.importe,0) as suma  ");
                sql.append("\n	FROM saldo s ");
                sql.append("\n	WHERE s.no_empresa = " + noEmpresaCon);
                sql.append("\n	AND s.no_cuenta = " + noEmpresaDes);
                sql.append("\n	AND s.id_tipo_saldo in (91) ");
                sql.append("\n	AND s.no_linea = convert(int,(Select id_divisa_soin ");
                sql.append("\n	From cat_divisa ");
                sql.append("\n	Where id_divisa = '" + idDivisa + "' ");
                sql.append("\n	And clasificacion='D'))");

			}
			else if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
			{
				
	            sql.append("SELECT coalesce(s.importe,0) as suma ");
	            sql.append("\n	FROM saldo s ");
	            sql.append("\n	WHERE s.no_empresa =" + noEmpresaCon);
	            sql.append("\n	AND s.no_cuenta =" + noEmpresaDes);
	            sql.append("\n	AND s.id_tipo_saldo in (90,5) ");
	            
	            if(ConstantesSet.gsDBM.equals("DB2"))
	            {
	                sql.append("\n	AND s.no_linea = cast((Select id_divisa_soin ");
	                sql.append("\n	From cat_divisa ");
	                sql.append("\n	Where id_divisa = '" + idDivisa + "' ");
	                sql.append("\n	And clasificacion='D') as integer)");
	            }
	            else
	            {
	                sql.append("\n	AND s.no_linea = to_number((Select id_divisa_soin ");
	                sql.append("\n	From cat_divisa ");
	                sql.append("\n	Where id_divisa = '" + idDivisa + "' ");
	                sql.append("\n	And clasificacion='D'),'999999')");
	            }
	            
			}
			else if(ConstantesSet.gsDBM.equals("ORACLE"))
			{
	            sql.append("SELECT coalesce(sum(s.importe),0) as suma ");
	            sql.append("\n	FROM saldo s ");
	            sql.append("\n	WHERE s.no_empresa = " + noEmpresaCon);
	            sql.append("\n	AND s.no_cuenta = " + noEmpresaDes);
	            sql.append("\n	AND s.id_tipo_saldo in (90,5) ");
	            sql.append("\n	AND s.no_linea = to_number((Select id_divisa_soin ");
	            sql.append("\n	From cat_divisa ");
	            sql.append("\n	Where id_divisa = '" + idDivisa + "' ");
	            sql.append("\n	And clasificacion='D'))");
			}
			bdImporte = jdbcTemplate.queryForObject(sql.toString(), BigDecimal.class);
			uImporte = bdImporte.doubleValue();
			
		}
		catch(EmptyResultDataAccessException erd){
			return 0;
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarSaldoCoinversora");
			e.printStackTrace();
		}
		return uImporte;
	}
	/**
	 * Public Function FunSQLSelectSdoChequera(ByVal plEmpresa As Long, _
	 ByVal psChequera As String, ByVal plBanco As Long) As ADODB.Recordset
	 * @param iIdEmpresa
	 * @param sIdChequera
	 * @param iIdBanco
	 * @return
	 */
	public List<CatCtaBancoDto> consultarSaldoChequera(int iIdEmpresa, String sIdChequera, int iIdBanco){
		List<CatCtaBancoDto> listSaldoChe = new ArrayList<CatCtaBancoDto>();
		StringBuffer sql = new StringBuffer();
		try{
		    sql.append(" SELECT coalesce(saldo_final, 0) as saldo_final, coalesce(sobregiro, 0) as sobregiro ");
		    sql.append(" FROM cat_cta_banco ");
		    sql.append(" WHERE no_empresa = " + iIdEmpresa);
		    sql.append("     and id_chequera = '" + sIdChequera + "' ");
		    sql.append("     and id_banco = " + iIdBanco);
		    
		    listSaldoChe = jdbcTemplate.query(sql.toString(), new RowMapper<CatCtaBancoDto>(){
				public CatCtaBancoDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		CatCtaBancoDto dtoCons = new CatCtaBancoDto();
		    		dtoCons.setSaldoFinal(rs.getDouble("saldo_final"));
		    		dtoCons.setSobregiro(rs.getDouble("sobregiro"));
		    		return dtoCons;
		    	}
		    });
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarSaldoChequera");
		}
		return listSaldoChe;
	}
	
	/**
	 * Public Function FunSQLSelect869(ByVal pvvValor1 As Integer, ByVal pvvValor2 As Integer, _
     * ByVal pvvValor3 As String) As ADODB.Recordset
	 * @param iIdEmpresa
	 * @param iNocuenta
	 * @param sIdDivisa
	 * @return
	 */
	public double consultarImporteMovimientos(int iIdEmpresa, int iNocuenta, String sIdDivisa){
		StringBuffer sql = new StringBuffer();
		BigDecimal bdImporte = new BigDecimal(2.1);
		double uImporte = 0;
		try{
			if(ConstantesSet.gsDBM.equals("ORACLE"))
			{
	            sql.append("SELECT coalesce(sum(s.importe),0) as suma ");
	            sql.append("\n	FROM saldo s ");
	            sql.append("\n	WHERE s.no_empresa = " + iIdEmpresa);
	            sql.append("\n	AND s.no_cuenta = " + iNocuenta);
	            sql.append("\n	AND s.id_tipo_saldo in (90,5) ");
	            sql.append("\n	AND s.no_linea = to_number((Select id_divisa_soin ");
	            sql.append("\n	From cat_divisa ");
	            sql.append("\n	Where id_divisa = '" + sIdDivisa + "' ");
	            sql.append("\n	And clasificacion='D'))");
			}
			else if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
			{
				sql.append("SELECT coalesce(s.importe,0) as suma  ");
	            sql.append("\n	FROM saldo s ");
	            sql.append("\n	WHERE s.no_empresa = " + iIdEmpresa);
	            sql.append("\n	AND s.no_cuenta = " + iNocuenta);
	            sql.append("\n	AND s.id_tipo_saldo in (91) ");
	            sql.append("\n	AND s.no_linea = convert(int,(Select id_divisa_soin ");
	            sql.append("\n	From cat_divisa ");
	            sql.append("\n	Where id_divisa = '" + sIdDivisa + "' ");
	            sql.append("\n	And clasificacion='D'))");
			}
			else if(ConstantesSet.gsDBM.equals("POSTGRESQL") || ConstantesSet.gsDBM.equals("DB2"))
			{
	            sql.append("SELECT coalesce(s.importe,0) as suma ");
                sql.append("\n	FROM saldo s ");
                sql.append("\n	WHERE s.no_empresa =" + iIdEmpresa);
                sql.append("\n	AND s.no_cuenta =" + iNocuenta);
                sql.append("\n	AND s.id_tipo_saldo in (90,5) ");
                
                if(ConstantesSet.gsDBM.equals("DB2")) 
                {
                    sql.append("\n	AND s.no_linea = cast((Select id_divisa_soin ");
                    sql.append("\n	From cat_divisa ");
                    sql.append("\n	Where id_divisa = '" + sIdDivisa + "' ");
                    sql.append("\n	And clasificacion='D') as integer)");
                }
                else
                {
                    sql.append("\n	AND s.no_linea = to_number((Select id_divisa_soin ");
                    sql.append("\n	From cat_divisa ");
                    sql.append("\n	Where id_divisa = '" + sIdDivisa + "' ");
                    sql.append("\n	And clasificacion='D'),'999999')");
                }
			}
			bdImporte = jdbcTemplate.queryForObject(sql.toString(), BigDecimal.class);
			uImporte = bdImporte.doubleValue();
		}
		catch(EmptyResultDataAccessException erd){
			return 0;
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarImporteMovimientos");
		}
		return uImporte;
	} 
	
	/**
	*
	* @param tipoFolio
	* @return int
	*
	* Selecciona el numero de folio de la tabla folio
	*/
	public int seleccionarFolioReal(String tipoFolio) {
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT num_folio ");
		sb.append("\n FROM folio ");
		sb.append("\n WHERE tipo_folio = '" + tipoFolio + "'");
		try {
			return jdbcTemplate.queryForInt(sb.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "
					+ e.toString()+ "P:Coinversion, C:CoinversionDaoImpl, M:seleccionarFolioReal");
			return -1;
		}
	}

	/**
	 * 
	 * @param tipoFolio
	 * @return int
	 * 
	 * Actualiza el numero de folio de la tabla folio
	 */
	public int actualizarFolioReal(String tipoFolio) {
		StringBuffer sb = new StringBuffer();
		sb.append(" UPDATE folio ");
		sb.append("\n SET num_folio = num_folio + 1 ");
		sb.append("\n WHERE tipo_folio = '" + tipoFolio + "'");
		try {
			return jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "
					+ e.toString()+ "P:Coinversion, C:CoinversionDaoImpl, M:actualizarFolioReal");
			return -1;
		}
	}
	
	public int insertarParametro(ParametroDto dto){
		int iAfectados = 0;
		StringBuffer sql = new StringBuffer();
		try{
		    sql.append("INSERT INTO parametro ( ");
		    sql.append("\n	no_empresa,no_folio_param,aplica,secuencia,id_tipo_operacion,");
		    sql.append("\n	no_cuenta,id_estatus_mov,id_chequera,id_banco,id_tipo_saldo,importe,");
		    sql.append("\n	b_salvo_buen_cobro,fec_valor,fec_valor_original,id_estatus_reg,usuario_alta,");
		    sql.append("\n	fec_alta,id_divisa,no_factura,id_forma_pago,id_divisa_original,importe_original, ");
		    sql.append("\n	fec_operacion,id_banco_benef, id_chequera_benef,origen_mov,concepto, ");
		    sql.append("\n	id_caja,no_folio_mov,folio_ref,grupo,no_cliente,");
		    sql.append("beneficiario ");
		    
		    if(dto.getNoDocto() > 0)
	        	sql.append(",no_docto");
		    
		    if(dto.getNoLinea() > 0)
		        sql.append(",no_linea");
		    
		    sql.append(",tipo_cambio ");
    	    if(dto.getIdRubro() > 0)
    	        sql.append(",id_rubro ");
    	    
    	    sql.append(") ");
    	    sql.append("VALUES( ");
    	    sql.append(dto.getNoEmpresa() + "," + dto.getNoFolioParam() + "," + dto.getAplica() + "," + dto.getSecuencia());
    	    sql.append("," + dto.getIdTipoOperacion() + "," + dto.getCuenta() + ",'" + dto.getIdEstatusMov());
    	    sql.append("','" + dto.getIdChequera() + "'," + dto.getIdBanco() + ","+dto.getTipo_saldo()+ "," + dto.getImporte());
    	    //dto.getNoFactura() se envia el id de la Raiz
            sql.append(",'" + dto.getBSBC() + "','" + funciones.ponerFecha(dto.getFecValor()) + "','" + funciones.ponerFecha(dto.getFecValor()));
            sql.append("','" + dto.getIdEstatusReg() + "'," + dto.getIdUsuario() + ",'" + funciones.ponerFecha(dto.getFecValor()) + "','" + dto.getIdDivisa());
    		sql.append("','" + dto.getNoFactura() + "'," + dto.getIdFormaPago() + ",'" + dto.getIdDivisa() + "'," + dto.getImporte() + ",'");
    		sql.append(funciones.ponerFecha(dto.getFecValor()) + "'," + dto.getIdBancoBenef() + ",'" + dto.getIdChequeraBenef()+ "','" 
    					+ dto.getOrigenMov() + "','" + dto.getConcepto() + "'");
      	    
    	    sql.append("," + dto.getIdCaja() + "," + dto.getNoFolioMov() + "," + dto.getFolioRef() + "," + dto.getIdGrupo() + ",'"); 
    	    sql.append(dto.getNoCliente() + "', '" + dto.getBeneficiario() + "' ");
    	    
    	    if(dto.getNoDocto() > 0)
    	    {
    	      if(ConstantesSet.gsDBM.equals("SYBASE"))
    	        sql.append(",'" + dto.getNoDocto() + "'");
    	      else
    	        sql.append("," + dto.getNoDocto());
    	    } 
    	    if(dto.getNoLinea() > 0)
    	        sql.append("," + dto.getNoLinea());

    	    sql.append("," + dto.getTipoCambio());
    	    
    	    if(dto.getIdRubro() > 0)
    	        sql.append("," + dto.getIdRubro());
    	    
    	    sql.append(") ");
    	    //logger.info("Inserta Parametro: " +sql.toString());
    	    System.out.println("Inserta Parametro: " +sql.toString());
    	    iAfectados = jdbcTemplate.update(sql.toString());
    	    
		}
		catch(Exception e){
		bitacora.insertarRegistro(new Date().toString()+ " "
					+ e.toString()+ "P:Coinversion, C:CoinversionDaoImpl, M:insertarParametro");	
		}
		return iAfectados;
	}
	
	
 /**
  * Public Function FunSQLSelectCuenta(ByVal pvl_noEmpresa As Long) As ADODB.Recordset
  * @param idEmpresa
  * @return el nï¿½mero de cuenta
  */
	public int consultarCuenta(int idEmpresa){
		StringBuffer sql = new StringBuffer();
		int iCuenta = 0;
		try{
			sql.append("Select no_cuenta ");
			sql.append("\n	From cuenta ");
			sql.append("\n	Where no_empresa = " + idEmpresa);
			sql.append("\n	and id_tipo_cuenta='P'");
			
			iCuenta = jdbcTemplate.queryForObject(sql.toString(), Integer.class);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "
					+ e.toString()+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarCuenta");
			return 0;
		}
		return iCuenta;
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
				+ "P:Coinversion, C:CoinversionDaoImpl, M:ejecutarGenerador U:"+dto.getIdUsuario()
				+ "	F:"+dto.getNomForma());
		}
		return resultado; 
	}
	
	/**
	 * Public Function FunSQLSelect105(ByVal pvvValor1 As Variant) As ADODB.Recordset
	 * @param iIdEmpresa
	 * @return
	 */
	@Transactional
	public List<ArbolEmpresaDto> consultarParentesco(int iIdEmpresa){
		StringBuffer sql = new StringBuffer();
		List<ArbolEmpresaDto> listCons = new ArrayList<ArbolEmpresaDto>();
		try{
			
			sql.append("SELECT padre, hijo, nieto, bisnieto, tataranieto, chosno, chosno2, ");
		    sql.append("\n	chosno3, chosno4, empresa_raiz ");
		    sql.append("\n	FROM arbol_empresa ");
		    sql.append("\n	WHERE no_empresa = " + iIdEmpresa);
		    
		    listCons = jdbcTemplate.query(sql.toString(), new RowMapper<ArbolEmpresaDto>(){
		    	public ArbolEmpresaDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		ArbolEmpresaDto dtoRs = new ArbolEmpresaDto();
			    		dtoRs.setPadre(rs.getInt("padre"));
			    		dtoRs.setHijo(rs.getInt("hijo"));
			    		dtoRs.setNieto(rs.getInt("nieto"));
			    		dtoRs.setBisnieto(rs.getInt("bisnieto"));
			    		dtoRs.setTataranieto(rs.getInt("tataranieto"));
			    		dtoRs.setChosno(rs.getInt("chosno"));
			    		dtoRs.setChosno2(rs.getInt("chosno2"));
			    		dtoRs.setChosno3(rs.getInt("chosno3"));
			    		dtoRs.setChosno4(rs.getInt("chosno4"));
			    		dtoRs.setEmpresaRaiz(rs.getInt("empresa_raiz"));
			    	return dtoRs;
		    	}
		    });
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarParentesco");
		}
		return listCons;
	}
	/**
	 * Public Function FunSQLInsParAutPres
	 * @param dto
	 * @return
	 */
	public int insertarParAutPres(ParametroDto dto){
		StringBuffer sql = new StringBuffer();
		int iAfectados = 0;
		try{
		   
	        sql.append(" INSERT INTO parametro (no_empresa,no_folio_param,aplica,secuencia,id_tipo_operacion,");
	        sql.append("\n	no_cuenta,id_estatus_mov,id_chequera,id_banco,importe,b_salvo_buen_cobro,fec_valor,");
	        sql.append("\n	fec_valor_original,id_estatus_reg,usuario_alta,fec_alta,id_divisa,id_forma_pago,");
	        sql.append("\n	importe_original,fec_operacion,id_banco_benef, id_chequera_benef,origen_mov,");
	        sql.append("\n	concepto,id_caja,no_folio_mov,folio_ref,grupo,no_docto,no_cliente,beneficiario,");
	        sql.append("\n	valor_tasa,dias_inv )");
	        sql.append("\n	VALUES( " + dto.getNoEmpresa()+ "," + dto.getNoFolioParam()+ "," + dto.getAplica()+ "," + dto.getSecuencia()+ ",");
	        sql.append(dto.getIdTipoOperacion() + "," + dto.getCuenta());
	        sql.append(",'" + dto.getIdEstatusMov() + "','" + dto.getIdChequera() + "'," + dto.getIdBanco() + ",");
	        
      		sql.append(dto.getImporte() + ",'" + dto.getBSBC() + "','" + funciones.ponerFechaSola(dto.getFecValor()));
	        sql.append("','" + funciones.ponerFechaSola(dto.getFecValorOriginal()) + "','" + dto.getIdEstatusReg() + "',");
	        sql.append(dto.getIdUsuario() + ",'" + funciones.ponerFechaSola(dto.getFecValorAlta()) + "','" 
	        		+ dto.getIdDivisa() + "'," + dto.getIdFormaPago());
	        sql.append("," + dto.getImporteOriginal() + ",'" + dto.getFecOperacion() + "'," + dto.getIdBancoBenef());
	        
	        sql.append(",'" + dto.getIdChequeraBenef() + "','" + dto.getOrigenMov() + "','");
	        sql.append(dto.getConcepto() + "'," + dto.getIdCaja() + "," + dto.getNoFolioMov() + "," + dto.getFolioRef() + ",");
	        
	        if(ConstantesSet.gsDBM.equals("SYBASE")) 
	        	sql.append(dto.getIdGrupo() + ",'" + dto.getNoDocto() + "','" + dto.getNoCliente() + "','" + dto.getBeneficiario() + "',");
		    else
		       sql.append(dto.getIdGrupo() + "," + dto.getNoDocto() + ",'" + dto.getNoCliente() + "','" + dto.getBeneficiario()+ "',");
		  
		     sql.append(dto.getValorTasa() + "," + dto.getDiasInv()+ ")");
		     
		     iAfectados = jdbcTemplate.queryForInt(sql.toString());
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:insertarParAutPres");
		}
		return iAfectados;
	}
	
	/**
	 * Public Function FunSQLRevisaFondeo() As ADODB.Recordset
	 * @return
	 */
	public List<Map<String, Object>> consultarRevisionFondeo(){
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> mapCons = new ArrayList<Map<String, Object>>();
		try{
		    sql.append("select e.no_empresa as no_padre,e.nom_empresa as padre ,");
		    sql.append("\n	e1.no_empresa as no_hijo ,e1.nom_empresa as hijo,aef.tipo_operacion");
		    sql.append("\n	from tmp_traspaso_fondeo  tmp, arbol_empresa_fondeo aef, empresa e,empresa e1");
		    sql.append("\n	Where tmp.empresa_origen = aef.empresa_origen");
		    sql.append("\n	and tmp.empresa_destino = aef.empresa_destino");
		    sql.append("\n	and tmp.empresa_origen = e.no_empresa");
		    sql.append("\n	and tmp.empresa_destino = e1.no_empresa");
		    
		    mapCons = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, Object>>(){
		    	public Map<String, Object> mapRow(ResultSet rs, int idx)throws SQLException {
		    		Map<String, Object> map = new HashMap<String, Object>();
		    		map.put("no_padre", rs.getInt("no_padre"));
		    		map.put("padre", rs.getString("padre"));
		    		map.put("no_hijo", rs.getInt("no_hijo"));
		    		map.put("hijo", rs.getString("hijo"));
		    		map.put("tipo_operacion", rs.getInt("tipo_operacion"));
		    		return map;
		    	} 
		    });
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarRevisionFondeo");
		}
		return mapCons;
	}
	
	/**
	 * Public Function FunSQLRevisaChequera2
	 * @param iIdEmpresa
	 * @param iIdBanco
	 * @param sIdDivisa
	 * @return
	 */
	public List<CatCtaBancoDto> consultarRevisaChequera2(int iIdEmpresa, int iIdBanco,
			String sIdDivisa){
		StringBuffer sql = new StringBuffer();
		List<CatCtaBancoDto> listCons = new ArrayList<CatCtaBancoDto>();
		try{
			sql.append("SELECT a.*, b.desc_banco ");
			sql.append("\n	FROM cat_cta_banco a, cat_banco b ");
	        sql.append("\n	WHERE a.no_empresa = " + iIdEmpresa);
	        sql.append("\n	AND a.id_divisa = '" + sIdDivisa + "'");
	        
			if(iIdBanco > 0)
			{
		        sql.append("\n	AND a.id_banco = " + iIdBanco);
		        sql.append("\n	AND a.mismo_banco = 'S' ");
			}
			else
			{
		        sql.append("\n	AND a.b_traspaso = 'S' ");
		        sql.append("\n	AND a.id_banco = b.id_banco ");
			}
		       
	        listCons = jdbcTemplate.query(sql.toString(), new RowMapper<CatCtaBancoDto>(){
	        	public CatCtaBancoDto mapRow(ResultSet rs, int idx)throws SQLException {
	        		CatCtaBancoDto dtoRs = new CatCtaBancoDto();
	        		dtoRs.setNoEmpresa(rs.getInt("no_empresa"));
	        		dtoRs.setIdBanco(rs.getInt("id_banco"));
	        		dtoRs.setIdChequera(rs.getString("id_chequera"));
	        		dtoRs.setDescChequera(rs.getString("desc_chequera"));
	        		dtoRs.setIdDivisa(rs.getString("id_divisa"));
	        		dtoRs.setSaldoInicial(rs.getDouble("saldo_inicial"));
	        		dtoRs.setCargo(rs.getDouble("cargo"));
	        		dtoRs.setAbono(rs.getDouble("abono"));
	        		dtoRs.setSaldoFinal(rs.getDouble("saldo_final"));
	        		dtoRs.setUltCheqImpreso(rs.getInt("ult_cheq_impreso"));
	        		dtoRs.setCargoSbc(rs.getDouble("cargo_sbc"));
	        		dtoRs.setAbonoSbc(rs.getDouble("abono_sbc"));
	        		dtoRs.setDescPlaza(rs.getString("desc_plaza"));
	        		dtoRs.setDescSucursal(rs.getString("desc_sucursal"));
	        		dtoRs.setTipoChequera(rs.getString("tipo_chequera"));
	        		dtoRs.setBConcentradora(rs.getString("b_concentradora"));
	        		dtoRs.setSaldoBancoIni(rs.getDouble("saldo_banco_ini"));
	        		dtoRs.setUsuarioAlta(rs.getInt("usuario_alta"));
	        		dtoRs.setSubCuenta(rs.getInt("sub_cuenta"));
	        		dtoRs.setSsubCuenta(rs.getInt("ssub_cuenta"));
	        		dtoRs.setFecAlta(rs.getDate("fec_alta"));
	        		dtoRs.setUsuarioModif(rs.getInt("usuario_modif"));
	        		dtoRs.setFecModif(rs.getDate("fec_modif"));
	        		dtoRs.setNacExt(rs.getString("nac_ext"));
	        		dtoRs.setBTraspaso(rs.getString("b_traspaso"));
	        		dtoRs.setSaldoMinimo(rs.getDouble("saldo_minimo"));
	        		dtoRs.setSaldoContaIni(rs.getDouble("saldo_conta_ini"));
	        		dtoRs.setFecConciliacion(rs.getDate("fec_conciliacion"));
	        		dtoRs.setPeriodoConciliacion(rs.getInt("periodo_conciliacion"));
	        		dtoRs.setFecConciIni(rs.getDate("fec_conci_ini"));
	        		dtoRs.setBConcilia(rs.getString("b_concilia"));
	        		dtoRs.setUsuarioConci(rs.getInt("usuario_conci"));
	        		dtoRs.setIdEstatusCta(rs.getString("id_estatus_cta"));
	        		dtoRs.setFechaBanca(rs.getDate("fecha_banca"));
	        		dtoRs.setBCheque(rs.getString("b_cheque"));
	        		dtoRs.setBTransferencia(rs.getString("b_transferencia"));
	        		dtoRs.setBChequeOcurre(rs.getString("b_cheque_ocurre"));
	        		dtoRs.setBExporta(rs.getString("b_exporta"));
	        		dtoRs.setIdClabe(rs.getString("id_clabe"));
	        		dtoRs.setBImpreContinua(rs.getString("b_impre_continua"));
	        		dtoRs.setAba(rs.getString("aba"));
	        		dtoRs.setSwiftCode(rs.getString("swift_code"));
	        		dtoRs.setIdDivision(rs.getString("id_division"));
	        		dtoRs.setSobregiro(rs.getDouble("sobregiro"));
	        		dtoRs.setPagoMass(rs.getString("pago_mass"));
	        		dtoRs.setBCargoEnCuenta(rs.getString("b_cargo_en_cuenta"));
	        		dtoRs.setMismoBanco(rs.getString("mismo_banco"));
	        		dtoRs.setFecConciliacionBc(rs.getDate("fec_conciliacion_bc"));
	        		dtoRs.setPeriodoConciliacionBc(rs.getDate("periodo_conciliacion_bc"));
	        		dtoRs.setUsuarioConciliacionBc(rs.getInt("usuario_conciliacion_bc"));
	        		dtoRs.setDescBanco(rs.getString("desc_banco"));
	        	return dtoRs;
	        	}
	        });
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarRevisaChequera2");
		}
		return listCons;
	}
	
	/**
	 * *Public Function FunSQLtmp_arboltrasp() As ADODB.Recordset
	 * @return
	 */
	public List<Map<String, Object>> consutarTmpArbolTrasp(){
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> listCons = new ArrayList<Map<String, Object>>();
		try{
		    sql.append("Select   sum(importe_traspaso) as importeTraspaso,");
		    sql.append("\n	empresa_origen as empOrigen ,empresa_destino as empDestino, tipo_arbol as tipoArbol ,orden as orden,");
		    sql.append("\n	e.nom_empresa  as nomEmpresaCoin ,  e1.nom_empresa  as nomEmpresa ,tf.id_banco as idBanco,tf.id_chequera  as idChequera");
		    sql.append("\n	from tmp_traspaso_fondeo tf, empresa e, empresa e1");
		    sql.append("\n	Where empresa_origen = e.no_empresa");
		    sql.append("\n	and empresa_destino = e1.no_empresa");
		    sql.append("\n	group by empresa_origen,empresa_destino , tipo_arbol,orden ,e.nom_empresa,e1.nom_empresa,tf.id_banco ,tf.id_chequera ");
		    sql.append("\n	order by orden asc");
		    logger.info("importeTraspaso:: "+ sql.toString());
		    listCons = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, Object>>(){
		    	public Map<String, Object> mapRow(ResultSet rs, int idx) throws SQLException {
		    		Map<String, Object> mapRs = new HashMap<String, Object>();
		    		mapRs.put("importeTraspaso", rs.getDouble("importeTraspaso"));
		    		mapRs.put("empOrigen", rs.getInt("empOrigen"));
		    		mapRs.put("empDestino", rs.getInt("empDestino"));
		    		mapRs.put("tipoArbol", rs.getInt("tipoArbol"));
		    		mapRs.put("orden", rs.getInt("orden"));
		    		mapRs.put("nomEmpresaCoin", rs.getString("nomEmpresaCoin"));
		    		mapRs.put("nomEmpresa", rs.getString("nomEmpresa"));
		    		mapRs.put("idBanco", rs.getInt("idBanco"));
		    		mapRs.put("idChequera", rs.getString("idChequera"));
		    	return mapRs;
		    	}
		    });
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consutarTmpArbolTrasp");
		}
		return listCons;
	}
	
	/**
	 * Public Function FunSQLRevisaPagos(ByVal id_banco As Integer, ByVal id_chequera As String) As ADODB.Recordset
	 * @param iIdBanco
	 * @param sIdChequera
	 * @return
	 */
	public List<ControlFondeoChequesDto> consultarRevisionPagos(int iIdBanco, String sIdChequera){
		StringBuffer sql = new StringBuffer();
		List<ControlFondeoChequesDto> listRevPag = new ArrayList<ControlFondeoChequesDto>();
		try{
		    sql.append("Select *");
		    sql.append("\n	from control_fondeo_cheques cch");
		    sql.append("\n	Where ");
		    sql.append("\n	cch.id_banco = " + iIdBanco);
		    sql.append("\n	and cch.id_chequera = '" + sIdChequera + "'");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarRevisionPagos");
		}
		return listRevPag;
	}
	
	/**
	 * Public Function FunSQLObtieneTipoOper
	 */
	public List<ArbolEmpresaFondeoDto> consultarTipoOperacion(int iIdEmpresaOrigen, 
			int iIdEmpresaDestino, int iTipoArbol, int orden){
		StringBuffer sql = new StringBuffer();
		List<ArbolEmpresaFondeoDto> listCons = new ArrayList<ArbolEmpresaFondeoDto>();
		try{
		    sql.append("Select e.*, c.desc_tipo_operacion  ");
		    sql.append("\n	from arbol_empresa_fondeo e, cat_tipo_operacion  c ");
		    sql.append("\n	where e.tipo_operacion = c.id_tipo_operacion and e.empresa_origen = " + iIdEmpresaOrigen);
		    sql.append("\n	and e.empresa_destino  =  " + iIdEmpresaDestino);
		    sql.append("\n	and e.tipo_arbol = " + iTipoArbol);
		    sql.append("\n	and e.orden = " + orden);
		    
		    listCons = jdbcTemplate.query(sql.toString(), new RowMapper<ArbolEmpresaFondeoDto>(){
		    	public ArbolEmpresaFondeoDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		ArbolEmpresaFondeoDto dtoRs = new ArbolEmpresaFondeoDto();
		    		dtoRs.setTipoArbol(rs.getInt("tipo_arbol"));
		    		dtoRs.setEmpresaOrigen(rs.getInt("empresa_origen"));
		    		dtoRs.setEmpresaDestino(rs.getInt("empresa_destino"));
		    		dtoRs.setTipoOperacion(rs.getInt("tipo_operacion"));
		    		dtoRs.setTipoValor(rs.getString("tipo_valor"));
		    		dtoRs.setValor(rs.getString("valor"));
		    		dtoRs.setContinua(rs.getString("continua"));
		    		dtoRs.setOrden(rs.getInt("orden"));
		    		dtoRs.setDescArbol(rs.getString("desc_arbol"));
		    		dtoRs.setDescTipoOperacion(rs.getString("desc_tipo_operacion"));
		    		return dtoRs;
		    	}
		    });
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarTipoOperacion");
		}
		return listCons;
	}
	
	@Transactional
	@SuppressWarnings("unchecked")
	public Map ejecutarPagador(StoreParamsComunDto dto)
	{
		Map resultado= new HashMap();
		try{
			StoredProcedure storedProcedure = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_sql_Pagador") {};
			storedProcedure.declareParameter(new SqlParameter("parametro",Types.VARCHAR ));
			storedProcedure.declareParameter(new SqlInOutParameter("mensaje",Types.VARCHAR));
			storedProcedure.declareParameter(new SqlInOutParameter("result",Types.INTEGER));
			
			HashMap< Object, Object > inParams = new HashMap< Object, Object >();
			inParams.put("parametro",dto.getParametro());
			inParams.put("mensaje",dto.getMensaje());
			inParams.put("result",dto.getResult());
			
			logger.info(dto.getParametro());
			logger.info(dto.getMensaje());
			logger.info(dto.getResult());
			
			resultado = storedProcedure.execute((Map)inParams);
				
			}catch (Exception e) {
				e.printStackTrace();
				bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
						+ "P:GRAL, C:ConsultasGenerales, M:ejecutarPagador");
		}
		return resultado;
	}
	
	/**
	 * Public Function DELETEPagosCheques
	 * @param iNoFolioDet
	 * @param iIdBanco
	 * @param sIdChequera
	 * @return
	 */
	public int eliminarPagosCheques(int iNoFolioDet, int iIdBanco, String sIdChequera){
		StringBuffer sql = new StringBuffer();
		int resDel = 0;
		try{
		    if(ConstantesSet.gsDBM.equals("DB2") || ConstantesSet.gsDBM.equals("POSTGRESQL"))
		    	sql.append(" DELETE FROM control_fondeo_cheques ");
		    else
		    	sql.append(" DELETE control_fondeo_cheques ");
		    
		    sql.append(" Where id_banco = " + iIdBanco);
		    sql.append(" and id_chequera = '" + sIdChequera + "'");
		    sql.append(" and no_folio_det = " + iNoFolioDet);
		    
		    resDel = jdbcTemplate.update(sql.toString());
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:eliminarPagosCheques");
		}
		return resDel;
	}
	
	/**
	 * Public Function FunSQLSelectPagos
	 * @param dtoBus
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<MovimientoDto> consultarPagos(ParamBusquedaFondeoDto dtoBus){
		StringBuffer sql = new StringBuffer();
		List<MovimientoDto> listConsPag = new ArrayList<MovimientoDto>();
		try{
			globalSingleton = GlobalSingleton.getInstancia();
		    sql.append(" SELECT m.cve_control, sum(m.importe) as importe, ");
    	    sql.append(" coalesce((select sag.cupo_total from seleccion_automatica_grupo sag where sag.cve_control = m.cve_control ");
    	    sql.append(" ), 0)as importe_original");
    	    sql.append(" FROM movimiento m, empresa e, empresa e1, arbol_empresa_fondeo c, cat_cta_banco ccb, cat_banco cb, cat_forma_pago cfp");
    	    sql.append(" WHERE ((  m.id_tipo_operacion = 3200 and  m.fec_valor >= '" + funciones.ponerFechaSola(dtoBus.getFechaHoy()) + "')");
    	    sql.append(" or ( m.id_tipo_operacion = 3000");
    	    if(globalSingleton.obtenerValorConfiguraSet(3003).equals("SI"))
    	    	sql.append(" and m.id_estatus_mov in('I','R','V')");
    	    
    	    sql.append(" AND m.no_folio_det in(select cch.no_folio_det");
    	    sql.append(" from  control_fondeo_cheques cch where cch.id_chequera = m.id_chequera ))");
    	    sql.append(" or (m.id_tipo_operacion = 3701 and   m.fec_valor >= '" + funciones.ponerFechaSola(dtoBus.getFechaHoy()) + "' and (origen_mov is null or  origen_mov <> 'SET') ))");
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
    	    logger.info(" PagosPendientes: "+sql.toString());
    	    listConsPag = jdbcTemplate.query(sql.toString(), new RowMapper(){
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
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarPagos");
		}
		return listConsPag;
	}
	
	/**
	 * FunSQLSelectPagosDesglose
	 * @param dtoBus
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ParamRetornoFondeoAutDto> consultarDesglosePagos(ParamBusquedaFondeoDto dtoBus)
	{
		StringBuffer sql = new StringBuffer();
		List<ParamRetornoFondeoAutDto> listConsDes = new ArrayList<ParamRetornoFondeoAutDto>();
		try
		{
			globalSingleton = GlobalSingleton.getInstancia();
			sql.append(" SELECT m.cve_control, m.importe as importe, e1.no_empresa as no_empresa_origen,");
		    sql.append(" e1.nom_empresa as nom_empresa_origen, e.no_empresa as no_empresa_destino ,");
		    sql.append(" e.nom_empresa as nom_empresa_destino,");
		    sql.append(" coalesce(cb.desc_banco, '') as desc_banco,");
		    sql.append(" coalesce(cb.id_banco, 0) as id_banco,");
		    sql.append(" coalesce(ccb.id_chequera, '') as id_chequera,");
		    sql.append(" coalesce(ccb.saldo_final, 0) as saldo_chequera,");
		    sql.append(" coalesce(ccb.saldo_minimo, 0) As saldo_minimo_chequera,");
		    sql.append(" cfp.desc_forma_pago as forma_pago, m.concepto, m.beneficiario, m.no_folio_det");
		    
		    sql.append(" FROM movimiento m, empresa e, empresa e1, arbol_empresa_fondeo c, cat_cta_banco ccb, cat_banco cb, cat_forma_pago cfp");
		    sql.append(" WHERE ((  m.id_tipo_operacion = 3200 and  m.fec_valor >= '" 
		    		+ funciones.ponerFechaSola(dtoBus.getFechaHoy())+ "')");
		    sql.append(" or ( m.id_tipo_operacion = 3000");
		    if(globalSingleton.obtenerValorConfiguraSet(3003).equals("SI"))
		    	sql.append(" and m.id_estatus_mov in('I','R','V')");
		    sql.append(" AND m.no_folio_det in(select cch.no_folio_det");
		    sql.append(" from  control_fondeo_cheques cch where cch.id_chequera = m.id_chequera ))");
		    sql.append(" or (m.id_tipo_operacion = 3701 and   m.fec_valor >= '" + funciones.ponerFechaSola(dtoBus.getFechaHoy()) + 
		    		"' and (origen_mov is null or  origen_mov <> 'SET') ))");
		    sql.append(" and m.id_tipo_movto = 'E'");
		    sql.append(" and m.id_estatus_mov not in ('X','Y','Z','W','G')");
		    sql.append(" and m.no_empresa = e.no_empresa");
		    sql.append(" and coalesce(m.id_contable,'') NOT in( select cd.clase_docto from arbol_clase_docto cd where  ");
		    
		    if(dtoBus.getSTipoBusqueda() != null && dtoBus.getSTipoBusqueda().equals("I"))
		        sql.append(" cd.tipo_busqueda = 'I' and cd.tipo_arbol = " + dtoBus.getIdEmpresaRaiz() + ")");
		    else
		        sql.append(" cd.tipo_busqueda = 'D' and cd.tipo_arbol = " + dtoBus.getIdEmpresaRaiz() + ")");
		    
		    sql.append(" and m.id_chequera = ccb.id_chequera");
		    sql.append(" and c.tipo_arbol = " + dtoBus.getIdEmpresaRaiz() + "");
		    sql.append(" and c.tipo_valor = 'NECESIDAD'");
		    sql.append(" and ccb.no_empresa = e.no_empresa");
		    sql.append(" and ccb.tipo_chequera in ('P','M')");
		    sql.append(" and ccb.id_divisa = '" + dtoBus.getIdDivisa()+ "'");
		    sql.append(" and cb.id_banco = ccb.id_banco");
		    sql.append(" and e.no_empresa in");
		    sql.append(" ( SELECT no_empresa From usuario_empresa WHERE no_usuario = " + dtoBus.getIdUsuario()+ ")");
		    sql.append(" and c.empresa_destino = e.no_empresa");
		    sql.append(" and c.empresa_origen = e1.no_empresa");
		    sql.append(" and c.orden > 0");
		    sql.append(" and ccb.id_banco = " + dtoBus.getIdBanco()+ "");
		    sql.append(" and ccb.id_chequera = '" + dtoBus.getIdChequera()+ "'");
		    sql.append(" and m.id_forma_pago = cfp.id_forma_pago");
		    
		    if(dtoBus.getCveControl() != null && !dtoBus.getCveControl().equals(""))
		        sql.append(" and m.cve_control = '" + dtoBus.getCveControl() + "'");
		    else
		        sql.append(" and m.cve_control = ''");
		    
		    listConsDes = jdbcTemplate.query(sql.toString(), new RowMapper(){
		    	public ParamRetornoFondeoAutDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		ParamRetornoFondeoAutDto dtoRs = new ParamRetornoFondeoAutDto();
		    			dtoRs.setCveControl(rs.getString("cve_control"));
		    			dtoRs.setImporte(rs.getDouble("importe"));
		    			dtoRs.setNoEmpresaOrigen(rs.getInt("no_empresa_origen"));
		    			dtoRs.setNomEmpresaOrigen(rs.getString("nom_empresa_origen"));
		    			dtoRs.setNoEmpresaDestino(rs.getInt("no_empresa_destino"));
		    			dtoRs.setNomEmpresaDestino(rs.getString("nom_empresa_destino"));
		    			dtoRs.setDescBanco(rs.getString("desc_banco"));
		    			dtoRs.setIdBanco(rs.getInt("id_banco"));
		    			dtoRs.setIdChequera(rs.getString("id_chequera"));
		    			dtoRs.setSaldoChequera(rs.getDouble("saldo_chequera"));
		    			dtoRs.setSaldoMinimoChequera(rs.getDouble("saldo_minimo_chequera"));
		    			dtoRs.setDescFormaPago(rs.getString("forma_pago"));
		    			dtoRs.setConcepto(rs.getString("concepto"));
		    			dtoRs.setBeneficiario(rs.getString("beneficiario"));
		    			dtoRs.setNoFolioDet(rs.getInt("no_folio_det"));
		    		return dtoRs;
		    	}
		    });
		    
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarDesglosePagos");
		}
		return listConsDes;
	}
	
	/**
	 * FunSQLSelectFondeoCheques
	 * @param sIdChequera
	 * @param iIdBanco
	 * @return lConsFonChe
	 */
	@SuppressWarnings("unchecked")
	public List<MovimientoDto> consultarFondeoCheques(String sIdChequera, int iIdBanco){
		StringBuffer sql = new StringBuffer();
		List<MovimientoDto> lConsFonChe = new ArrayList<MovimientoDto>();
		try
		{
			globalSingleton = GlobalSingleton.getInstancia();
		    sql.append("Select cch.no_folio_det as folio_fondeo, m.*");
		    sql.append("\n	from movimiento m left join control_fondeo_cheques cch on (cch.no_folio_det = m.no_folio_det)");
		    sql.append("\n	Where m.id_tipo_operacion = 3000");
		    
		    sql.append("\n	and m.id_forma_pago = 1");
		    if(globalSingleton.obtenerValorConfiguraSet(3003).equals("SI"))
		    	sql.append("\n	and m.id_estatus_mov in('I','R','V','P')");
		    	
		    sql.append("\n	and m.id_banco = " + iIdBanco);
		    sql.append("\n	and m.id_chequera = '" + sIdChequera + "'");
		    sql.append("\n	order by importe asc");
		    
		    lConsFonChe = jdbcTemplate.query(sql.toString(), new RowMapper(){
		    	public MovimientoDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		MovimientoDto dtoRs = new MovimientoDto();
			    		dtoRs.setFolioFondeo(rs.getInt("folio_fondeo"));
			    		dtoRs.setNoFolioDet(rs.getInt("no_folio_det"));
			    		dtoRs.setFecValor(rs.getDate("fec_valor"));
			    		dtoRs.setFecEntregado(rs.getDate("fec_entregado"));
			    		dtoRs.setBeneficiario(rs.getString("beneficiario"));
			    		dtoRs.setImporte(rs.getDouble("importe"));
		    		return dtoRs;
		    	}
		    });
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarFondeoCheques");
		}
		return lConsFonChe;
	}
	
	/**
	 * FunSQLRevisaCheques
	 * @param iNoFolioDet
	 * @param iIdBanco
	 * @param sIdChequera
	 * @return lista de tipo ControlFondeoChequesDto
	 */
	@SuppressWarnings("unchecked")
	public List<ControlFondeoChequesDto> consultarRevisionCheques(int iNoFolioDet, int iIdBanco, String sIdChequera)
	{
		StringBuffer sql = new StringBuffer();
		List<ControlFondeoChequesDto> listConsRev = new ArrayList<ControlFondeoChequesDto>();
		try{
		    sql.append("Select * from control_fondeo_cheques cch");
		    sql.append("\n	Where ");
		    sql.append("\n	cch.id_banco = " + iIdBanco);
		    sql.append("\n	and cch.id_chequera = '" + sIdChequera + "'");
		    sql.append("\n	and cch.no_folio_det = " + iNoFolioDet);
		    
		    listConsRev =jdbcTemplate.query(sql.toString(), new RowMapper(){
		    	public ControlFondeoChequesDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		ControlFondeoChequesDto dtoRs = new ControlFondeoChequesDto();
			    		dtoRs.setNoFolioDet(rs.getInt("no_folio_det"));
			    		dtoRs.setIdBanco(rs.getInt("id_banco"));
			    		dtoRs.setIdChequera(rs.getString("id_chequera"));
			    	return dtoRs; 
		    	}
		    });
		    
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarRevisionCheques");
		}
		return listConsRev;
	}
	
	/**
	 * INSERTFondeoCheques
	 * @param iNoFolioDet
	 * @param idBanco
	 * @param sIdChequera
	 * @return
	 */
	public int insertarControlFondeoCheques(int iNoFolioDet, int idBanco, String sIdChequera){
		StringBuffer sql = new StringBuffer();
		try{
		    sql.append("INSERT INTO control_fondeo_cheques (");
		    sql.append("\n	no_folio_det, id_banco, id_chequera )");
		    sql.append("\n	VALUES (");
		    sql.append(iNoFolioDet);
		    sql.append("," + idBanco);
		    sql.append(",'" + sIdChequera);
		    sql.append("')");
		    
		    return jdbcTemplate.update(sql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:insertarControlFondeoCheques");
			return 0;
		}
	}
	
	/**
	 * DELETEFondeoCheques
	 * @param iNoFolioDet
	 * @param iIdBanco
	 * @param sIdChequera
	 * @return
	 */
	public int eliminarControlFondeoCheques(int iNoFolioDet, int iIdBanco, String sIdChequera){
		StringBuffer sql = new StringBuffer();
		try{
		    if(ConstantesSet.gsDBM.equals("DB2") || ConstantesSet.gsDBM.equals("POSTGRESQL"))
	        	sql.append(" DELETE from control_fondeo_cheques ");
		    else
		        sql.append(" DELETE control_fondeo_cheques ");
		    
		    sql.append(" Where id_banco = " + iIdBanco);
		    sql.append(" and id_chequera = '" + sIdChequera + "'");
		    sql.append(" and no_folio_det = " + iNoFolioDet);
		    
		    return jdbcTemplate.update(sql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:eliminarControlFondeoCheques");
			return 0;
		}
	}
	
	public int insertarTmpTraspasoFondeo(TmpTraspasoFondeoDto dto){
		StringBuffer sql = new StringBuffer();
		try{
		    sql.append("INSERT INTO tmp_traspaso_fondeo (");
		    sql.append("\n	empresa_origen,empresa_destino, importe_traspaso, tipo_arbol,orden,");
		    sql.append("\n	id_banco,id_chequera,id_chequera_padre)");
		    sql.append("\n	VALUES (");
		    sql.append(dto.getIdEmpresaOrigen());
		    sql.append("," + dto.getIdEmpresaDestino());
		    sql.append("," + dto.getImporteTraspaso());
		    sql.append("," + dto.getTipoArbol());
		    sql.append("," + dto.getOrden());
		    sql.append("," + dto.getIdBanco() + ",'" + dto.getIdChequeraHijo() + "','" + dto.getIdChequeraPadre() + "')");
		    
		    return jdbcTemplate.update(sql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:insertarTmpTraspasoFondeo");
			return 0;
		}
	}
	
	/**
	 *  Public Function delete_tmp_traspaso_fondeo()
	 */
	public void eliminarTmpTraspasoFondeo(){
		StringBuffer sql = new StringBuffer();
		try{
			if(ConstantesSet.gsDBM.equals("DB2") || ConstantesSet.gsDBM.equals("POSTGRESQL"))
				sql.append("Delete	from 	tmp_traspaso_fondeo");
			else
				sql.append("Delete	tmp_traspaso_fondeo");
			
			jdbcTemplate.update(sql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:eliminarTmpTraspasoFondeo");
		}
	} 
	
	//inician consultas mtto coinversionistas
	/**
	 * FunSQLSelectFilialesDivisaEncontrada
	 */
	@SuppressWarnings("unchecked")
	public List<DivisasEncontradasDto> consultarDivisasFilialesEncontradas(int iEmpresa){
		List<DivisasEncontradasDto> list = new ArrayList<DivisasEncontradasDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  SELECT DISTINCT e.no_empresa, e.nom_empresa, cd.id_divisa, ");
		    sbSQL.append("\n      desc_divisa ");
		    sbSQL.append("\n  FROM cat_divisa cd, empresa e, cuenta c ");
		    sbSQL.append("\n  WHERE cd.id_divisa = c.id_divisa ");
		    sbSQL.append("\n      and e.no_empresa = c.no_cuenta ");
		    sbSQL.append("\n      and c.no_cuenta = " + iEmpresa);
		    sbSQL.append("\n      and c.id_tipo_cuenta = 'T' ");
		    sbSQL.append("\n  ORDER BY cd.id_divisa, e.no_empresa");
		    
		    System.out.println("Query WebSet: "+sbSQL.toString());
		    list =jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
		    	public DivisasEncontradasDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		DivisasEncontradasDto divisas = new DivisasEncontradasDto();
			    		divisas.setNoEmpresa(rs.getInt("no_empresa"));
			    		divisas.setNomEmpresa(rs.getString("nom_empresa"));
			    		divisas.setIdDivisa(rs.getString("id_divisa"));
			    		divisas.setDescDivisa(rs.getString("desc_divisa"));
			    	return divisas; 
		    	}
		    });

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarDivisasFilialesEncontradas");
		}
		return list;
	}
	
	/**
	 * FunSQLSelectFilialesDivisaNoEncontrada
	 * @param iEmpresa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<DivisasEncontradasDto> consultarDivisasFilialesNoEncontradas(int iEmpresa){
		List<DivisasEncontradasDto> list = new ArrayList<DivisasEncontradasDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n SELECT DISTINCT e.no_empresa, e.nom_empresa, cd.id_divisa, cd.desc_divisa");
		    sbSQL.append("\n FROM cat_divisa cd, empresa e, cat_cta_banco cb ");
		    sbSQL.append("\n WHERE cd.id_divisa not in ");
		    sbSQL.append("\n     ( ");
		    sbSQL.append("\n       SELECT c1.id_divisa ");
		    sbSQL.append("\n       FROM cuenta c1 ");
		    sbSQL.append("\n       WHERE c1.id_divisa = cd.id_divisa ");
		    sbSQL.append("\n           and c1.no_cuenta = e.no_empresa ");
		    sbSQL.append("\n           and c1.id_tipo_cuenta = 'T' ");
		    sbSQL.append("\n     )");
		    sbSQL.append("\n     and e.no_empresa = " + iEmpresa);
		    sbSQL.append("\n     and cb.no_empresa = e.no_empresa ");
		    sbSQL.append("\n     and cb.id_divisa = cd.id_divisa ");
		    sbSQL.append("\n ORDER BY e.no_empresa ");
		    
		    System.out.println("Query WebSet: "+sbSQL.toString());
		    
		    list =jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
		    	public DivisasEncontradasDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		DivisasEncontradasDto divisas = new DivisasEncontradasDto();
			    		divisas.setNoEmpresa(rs.getInt("no_empresa"));
			    		divisas.setNomEmpresa(rs.getString("nom_empresa"));
			    		divisas.setIdDivisa(rs.getString("id_divisa"));
			    		divisas.setDescDivisa(rs.getString("desc_divisa"));
			    	return divisas; 
		    	}
		    });

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarDivisasFilialesNoEncontradas");
		}
		return list;
	}
	
	public int actualizarEmpresa(int controladora, int nvaEmpresa){
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  Update empresa set no_controladora= " + controladora);
		    sbSQL.append("\n  Where no_empresa= " + nvaEmpresa);
		    
		    return jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:actualizarEmpresa");
			return 0;
		}
	}
	
	public int insertaSaldos(int empresa, int linea, int cuenta, int tipoSaldo){
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  INSERT INTO saldo (");
		    sbSQL.append("\n  no_empresa, no_linea, no_cuenta, id_tipo_saldo, importe )");
		    sbSQL.append("\n  VALUES (");
			sbSQL.append("\n" + empresa);
		    sbSQL.append("\n ," + linea);
		    sbSQL.append("\n ," + cuenta);
		    sbSQL.append("\n ," + tipoSaldo);
		    sbSQL.append("\n ,0)");
		    return jdbcTemplate.update(sbSQL.toString());
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:insertaSaldos");
			return 0;
		}
	}
	
	/**
	 * FunSQLInsertFilial
	 */
	public int insertaFilial(int noEmpresa, String numProducto, int noPersona,
			int noUsuario, int noLinea, int noCuenta, Date fechaAlta,
			String idTipoCuenta, String descCuenta, String idDivisa){
		/*Public Function FunSQLInsertFilial(ByVal pvi_noEmpresa As Integer, ByVal pvl_noProducto As Long, ByVal pvl_noPersona As Long, _
				ByVal pvi_noUsuario As Integer, ByVal pvl_noLinea As Long, ByVal pvl_noCuenta As Long, ByVal pvt_fechaAlta As Date, _
				ByVal pvs_idTipoCuenta As String, ByVal pvs_descCuenta As String, ByVal pvs_idDivisa As String) As Long*/

		try{
			StringBuffer sbSQL = new StringBuffer();
			globalSingleton = GlobalSingleton.getInstancia();
		    sbSQL.append("\n  Insert ");
		    sbSQL.append("  Into cuenta ");
		    sbSQL.append("  ( ");

		    sbSQL.append("\n  no_empresa, ");
		    sbSQL.append("  no_producto, ");
		    sbSQL.append("  no_persona, ");
		    sbSQL.append("  usuario_alta, ");

		    sbSQL.append("  no_linea, ");
		    sbSQL.append("  no_cuenta, ");
		    sbSQL.append("  fec_alta, ");
		    sbSQL.append("  id_tipo_cuenta, ");
		    sbSQL.append("  desc_cuenta, ");
		    sbSQL.append("  id_divisa ");
		    sbSQL.append("  ) ");

		    sbSQL.append("\n  Values ");
		    sbSQL.append("  ( \n");

		    sbSQL.append(  noEmpresa + ", ");
		    sbSQL.append(  numProducto + ", ");
		    sbSQL.append(  noPersona + ", ");
		    sbSQL.append(  noUsuario + ", ");

		    sbSQL.append(  noLinea + ", ");
		    sbSQL.append(  noCuenta + ",");
		    sbSQL.append( "convert(datetime,'"+ funciones.ponerFechaSola(fechaAlta) + "',103), ");
		    sbSQL.append(  "'" + idTipoCuenta + "', ");
		    sbSQL.append(  "'" + descCuenta + "', '");
		    sbSQL.append(  idDivisa + "')");
		    
		    System.out.println("RRubio "+sbSQL.toString() );
		    return jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:insertaAltaFilial");
			return 0;
		}
	}
	
	/**
	 * FunSQLSelectMovtosInversoras 
	 * @param iEmpresa
	 * @param iCuenta
	 * @param iLinea
	 * @return
	 */
	public int consultaMovimientosInversoras(int iEmpresa, int iCuenta, int iLinea){
		int iSql = -1;
		StringBuffer sbSQL = new StringBuffer(); 
		try{
			sbSQL.append("\n  Select count(*) from movimiento where no_empresa =" + iEmpresa);
		    sbSQL.append("\n  and id_tipo_operacion in(3805,3806,3807) and id_estatus_mov='L' ");
		    sbSQL.append("\n  and no_cuenta= " + iCuenta);
		    sbSQL.append("\n  and no_linea=" + iLinea);
		    
		    iSql = jdbcTemplate.queryForInt(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultaMovimientosInversoras");
		}
		return iSql;
	}
	
	/**
	 * FunSQLSelectSaldoFiliales
	 * @param iEmpresa
	 * @param iCuenta
	 * @param iLinea
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SaldoDto> consultarSaldoFiliales(int iEmpresa, int iCuenta, int iLinea){
		StringBuffer sbSQL = new StringBuffer(); 
		List<SaldoDto> list = new ArrayList<SaldoDto>();
		try{
		    sbSQL.append("\n  SELECT coalesce(importe,0) as importe from saldo ");
		    sbSQL.append("\n  WHERE no_empresa = " + iEmpresa);
		    sbSQL.append("\n  And no_cuenta = " + iCuenta);
		    sbSQL.append("\n  And id_tipo_saldo = 91 ");
		    sbSQL.append("\n  And no_linea = " + iLinea);
		    
		    list =jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
		    	public SaldoDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		SaldoDto dto = new SaldoDto();
			    		dto.setImporte(rs.getDouble("importe"));
			    	return dto; 
		    	}
		    });

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarSaldoFiliales");
		}
		return list;
	}
	
	/**
	 * FunSQLUpdateControladora
	 * @param iEmpresa
	 * @return
	 */
	public int actualizarControladora(int iEmpresa){
		int iActualiza = -1;
		StringBuffer sbSQL = new StringBuffer(); 
		try{
			sbSQL.append("Update empresa set no_controladora = NULL where no_empresa=" + iEmpresa);
			iActualiza = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:actualizarControladora");
		}
		return iActualiza;
	}
	
	/**
	 * FunSQLDeleteSaldo
	 * @param iEmpresa
	 * @param iCuenta
	 * @param iLinea
	 * @return
	 */
	public int eliminarSaldo(int iEmpresa, int iCuenta, int iLinea){
		int iElimina = -1;
		StringBuffer sbSQL = new StringBuffer();
		try{
			if(ConstantesSet.gsDBM.equals("DB2") || ConstantesSet.gsDBM.equals("POSTGRESQL"))
		        sbSQL.append("  Delete from saldo where no_empresa = " + iEmpresa);
		    else
		        sbSQL.append("  Delete saldo where no_empresa = " + iEmpresa);
		    
		    sbSQL.append("\n  and no_cuenta = " + iCuenta);
		    sbSQL.append("\n  and id_tipo_saldo in(5,7,90,91) and no_linea = " + iLinea);
		    
		    iElimina = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:eliminarSaldo");
		}
		return iElimina;
	}
	
	/**
	 * FunSQLDeleteCuenta
	 * @param iEmpresa
	 * @param iCuenta
	 * @param iLinea
	 * @return
	 */
	public int eliminarCuenta(int iEmpresa, int iCuenta, int iLinea){
		int iElimina = -1;
		StringBuffer sbSQL = new StringBuffer();
		try{
			if(ConstantesSet.gsDBM.equals("DB2") || ConstantesSet.gsDBM.equals("POSTGRESQL"))
		        sbSQL.append("  Delete From cuenta ");
		    else
		        sbSQL.append("  Delete cuenta ");
		    
		    sbSQL.append("\n  Where no_cuenta = " + iCuenta);
		    sbSQL.append("\n  And no_empresa = " + iEmpresa);
		    sbSQL.append("\n  And no_linea = " + iLinea);
		    
		    iElimina = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:eliminarCuenta");
		}
		return iElimina;
	}
	
	/**
	 * FunSQLRepCoinversionistas
	 * @param plEmpresa
	 * @param plUsuario
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> consultarReporteCoinversoras(int plEmpresa, int plUsuario){
		List<Map<String, Object>> resultado = null;
		StringBuffer sbSQL = new StringBuffer();
		String sOp = "";
		try{
			if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
		        sOp = "'' || ''";
		    else
		        sOp = "''";

		    // Empresa Hijo
		    sbSQL.append("\n  SELECT distinct coalesce(ae.padre, 0) as padre, coalesce(ae.hijo, 0) as hijo, ");
		    sbSQL.append("\n      0 as nieto, 0 as bisnieto, 0 as tataranieto, 0 as chosno, 0 as chosno2, ");
		    sbSQL.append("\n      0 as chosno3, 0 as chosno4, e.no_empresa, coalesce(e.nom_empresa, '') as shijo, ");
		    sbSQL.append("\n      " + sOp + " as snieto, " + sOp + " as sbisnieto, " + sOp + " as stataranieto, ");
		    sbSQL.append("\n      " + sOp + " as schosno, " + sOp + " as schosno2, " + sOp + " as schosno3, ");
		    sbSQL.append("\n      " + sOp + " as schosno4, " + sOp + " as sDivisa, 1 as secuencia ");
		    sbSQL.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
		    sbSQL.append("\n      arbol_empresa ae ");
		    sbSQL.append("\n  WHERE e.no_empresa = p.no_empresa ");
		    sbSQL.append("\n      and p.id_tipo_persona in ('I','E') ");
		    sbSQL.append("\n      and ( e.no_controladora = " + plEmpresa);
		    sbSQL.append("\n          or e.no_controladora in ");
		    sbSQL.append("\n              ( SELECT e1.no_empresa ");
		    sbSQL.append("\n                FROM empresa e1 ");
		    sbSQL.append("\n                WHERE e1.no_controladora = " + plEmpresa);
		    sbSQL.append("\n                    and e1.b_concentradora = 'S' ");
		    sbSQL.append("\n              ) ");
		    sbSQL.append("\n          or e.no_empresa = " + plEmpresa);
		    sbSQL.append("\n          or e.no_empresa = ae.nieto ) ");
		    sbSQL.append("\n      and ae.hijo = e.no_empresa ");
		    sbSQL.append("\n      and e.no_empresa in ");
		    sbSQL.append("\n          ( SELECT no_empresa ");
		    sbSQL.append("\n            FROM usuario_empresa ");
		    sbSQL.append("\n            WHERE no_usuario = " + plUsuario + " ) ");
		    sbSQL.append("\n  GROUP BY ae.padre, ae.hijo, e.no_empresa, e.nom_empresa ");
		    sbSQL.append("\n  UNION ALL ");
		    // Divisa Empresa Hijo
		    sbSQL.append("\n  SELECT distinct coalesce(ae.padre, 0) as padre, coalesce(ae.hijo, 0) as hijo, ");
		    sbSQL.append("\n      0 as nieto, 0 as bisnieto, 0 as tataranieto, 0 as chosno, 0 as chosno2, ");
		    if(ConstantesSet.gsDBM.equals("SYBASE"))
		    {
		        sbSQL.append("\n      0 as chosno3, 0 as chosno4, e.no_empresa, " + sOp + " as shijo, ");
		        sbSQL.append("\n      cd.desc_divisa as snieto, " + sOp + " as sbisnieto, " + sOp + " as stataranieto, ");
		        sbSQL.append("\n      " + sOp + " as schosno, " + sOp + " as schosno2, " + sOp + " as schosno3, ");
		        sbSQL.append("\n      " + sOp + " as schosno4, " + sOp + " as sdivisa, 2 as secuencia ");
		    }
	        else
	        {
		        sbSQL.append("\n      0 as chosno3, 0 as chosno4, e.no_empresa, " + sOp + " as shijo, ");
		        sbSQL.append("\n      cd.desc_divisa as snieto, " + sOp + " as sbisnieto, " + sOp + " as stataranieto, ");
		        sbSQL.append("\n      " + sOp + " as schosno, " + sOp + " as schosno2, " + sOp + " as schosno3, ");
		        sbSQL.append("\n      " + sOp + " as schosno4, " + sOp + " as sdivisa, 2 as secuencia ");
	        }
		    sbSQL.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
		    sbSQL.append("\n      arbol_empresa ae, cat_divisa cd ");
		    sbSQL.append("\n  WHERE e.no_empresa = p.no_empresa ");
		    sbSQL.append("\n      and p.id_tipo_persona in ('I','E') ");
		    sbSQL.append("\n      and ( e.no_controladora = " + plEmpresa);
		    sbSQL.append("\n          or e.no_controladora in ");
		    sbSQL.append("\n              ( SELECT e1.no_empresa ");
		    sbSQL.append("\n                FROM empresa e1 ");
		    sbSQL.append("\n                WHERE e1.no_controladora = " + plEmpresa);
		    sbSQL.append("\n                    and e1.b_concentradora = 'S' ");
		    sbSQL.append("\n              ) ");
		    sbSQL.append("\n          or e.no_empresa = " + plEmpresa);
		    sbSQL.append("\n          or e.no_empresa = ae.nieto ) ");
		    sbSQL.append("\n      and ae.hijo = e.no_empresa ");
		    sbSQL.append("\n      and cd.id_divisa = c.id_divisa ");
		    sbSQL.append("\n      and e.no_empresa in ");
		    sbSQL.append("\n          ( SELECT no_empresa ");
		    sbSQL.append("\n            FROM usuario_empresa ");
		    sbSQL.append("\n            WHERE no_usuario = " + plUsuario + " ) ");
		    sbSQL.append("\n  GROUP BY ae.padre, ae.hijo, e.no_empresa, e.nom_empresa, cd.desc_divisa ");
		    sbSQL.append("\n  UNION ALL ");
		    // Empresa Nieto
		    sbSQL.append("\n  SELECT distinct coalesce(ae.padre, 0) as padre, coalesce(ae.hijo, 0) as hijo, coalesce(ae.nieto, 0) as nieto, ");
		    sbSQL.append("\n      0 as bisnieto, 0 as tataranieto, 0 as chosno, 0 as chosno2, 0 as chosno3, 0 as chosno4, e.no_empresa, " + sOp + " as shijo, coalesce(e.nom_empresa, '') as snieto, ");
		    sbSQL.append("\n      " + sOp + " as sbisnieto, " + sOp + " as stataranieto, " + sOp + " as schosno, ");
		    sbSQL.append("\n      " + sOp + " as schosno2, " + sOp + " as schosno3, ");
		    sbSQL.append("\n      " + sOp + " as schosno4, " + sOp + " as sDivisa, 3 as secuencia ");
		    sbSQL.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
		    sbSQL.append("\n      arbol_empresa ae ");
		    sbSQL.append("\n  WHERE e.no_empresa = p.no_empresa ");
		    sbSQL.append("\n      and p.id_tipo_persona in ('I','E') ");
		    sbSQL.append("\n      and ( e.no_controladora = " + plEmpresa);
		    sbSQL.append("\n          or e.no_controladora in ");
		    sbSQL.append("\n              ( SELECT e1.no_empresa ");
		    sbSQL.append("\n                FROM empresa e1 ");
		    sbSQL.append("\n                WHERE e1.no_controladora = " + plEmpresa);
		    sbSQL.append("\n                    and e1.b_concentradora = 'S' ");
		    sbSQL.append("\n              ) ");
		    sbSQL.append("\n          or e.no_empresa = " + plEmpresa);
		    sbSQL.append("\n          or e.no_empresa = ae.bisnieto ) ");
		    sbSQL.append("\n      and ae.nieto = e.no_empresa ");
		    sbSQL.append("\n      and e.no_empresa in ");
		    sbSQL.append("\n          ( SELECT no_empresa ");
		    sbSQL.append("\n            FROM usuario_empresa ");
		    sbSQL.append("\n            WHERE no_usuario = " + plUsuario + " ) ");
		    sbSQL.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, e.no_empresa, e.nom_empresa ");
		    sbSQL.append("\n  UNION ALL ");
		    // Divisa Empresa Nieto
		    sbSQL.append("\n  SELECT distinct coalesce(ae.padre, 0) as padre, coalesce(ae.hijo, 0) as hijo, coalesce(ae.nieto, 0) as nieto, ");
		    sbSQL.append("\n      0 as bisnieto, 0 as tataranieto, 0 as chosno, 0 as chosno2, 0 as chosno3, 0 as chosno4, e.no_empresa, " + sOp + " as shijo, " + sOp + " as snieto, ");
		    sbSQL.append("\n      cd.desc_divisa as sbisnieto, " + sOp + " as stataranieto, " + sOp + " as schosno, ");
		    sbSQL.append("\n      " + sOp + " as schosno2, " + sOp + " as schosno3, ");
		    sbSQL.append("\n      " + sOp + " as schosno4, " + sOp + " as sDivisa, 4 as secuencia ");
		    sbSQL.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
		    sbSQL.append("\n      arbol_empresa ae, cat_divisa cd ");
		    sbSQL.append("\n  WHERE e.no_empresa = p.no_empresa ");
		    sbSQL.append("\n      and p.id_tipo_persona in ('I','E') ");
		    sbSQL.append("\n      and ( e.no_controladora = " + plEmpresa);
		    sbSQL.append("\n          or e.no_controladora in ");
		    sbSQL.append("\n              ( SELECT e1.no_empresa ");
		    sbSQL.append("\n                FROM empresa e1 ");
		    sbSQL.append("\n                WHERE e1.no_controladora = " + plEmpresa);
		    sbSQL.append("\n                    and e1.b_concentradora = 'S' ");
		    sbSQL.append("\n              ) ");
		    sbSQL.append("\n          or e.no_empresa = " + plEmpresa);
		    sbSQL.append("\n          or e.no_empresa = ae.bisnieto ) ");
		    sbSQL.append("\n      and ae.nieto = e.no_empresa ");
		    sbSQL.append("\n      and cd.id_divisa = c.id_divisa ");
		    sbSQL.append("\n      and e.no_empresa in ");
		    sbSQL.append("\n          ( SELECT no_empresa ");
		    sbSQL.append("\n            FROM usuario_empresa ");
		    sbSQL.append("\n            WHERE no_usuario = " + plUsuario + " ) ");
		    sbSQL.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, e.no_empresa, e.nom_empresa, cd.desc_divisa ");
		    sbSQL.append("\n  UNION ALL ");
		    // Empresa bisnieto
		    sbSQL.append("\n  SELECT distinct coalesce(ae.padre, 0) as padre, coalesce(ae.hijo, 0) as hijo, coalesce(ae.nieto, 0) as nieto,");
		    sbSQL.append("\n      coalesce(ae.bisnieto, 0) as bisnieto, 0 as tataranieto, 0 as chosno, 0 as chosno2, 0 as chosno3, 0 as chosno4, e.no_empresa, " + sOp + " as shijo, " + sOp + " as snieto, ");
		    sbSQL.append("\n      coalesce(e.nom_empresa, '') as sbisnieto, " + sOp + " as stataranieto, " + sOp + " as schosno, ");
		    sbSQL.append("\n      " + sOp + " as schosno2, " + sOp + " as schosno3, " + sOp + " as schosno4, ");
		    sbSQL.append("\n      " + sOp + " as sDivisa, 5 as secuencia ");
		    sbSQL.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
		    sbSQL.append("\n      arbol_empresa ae ");
		    sbSQL.append("\n  WHERE e.no_empresa = p.no_empresa ");
		    sbSQL.append("\n      and p.id_tipo_persona in ('I','E') ");
		    sbSQL.append("\n      and ( e.no_controladora = " + plEmpresa);
		    sbSQL.append("\n          or e.no_controladora in ");
		    sbSQL.append("\n              ( SELECT e1.no_empresa ");
		    sbSQL.append("\n                FROM empresa e1 ");
		    sbSQL.append("\n                WHERE e1.no_controladora = " + plEmpresa);
		    sbSQL.append("\n                    and e1.b_concentradora = 'S' ");
		    sbSQL.append("\n              ) ");
		    sbSQL.append("\n          or e.no_empresa = " + plEmpresa);
		    sbSQL.append("\n          or e.no_empresa = ae.tataranieto ) ");
		    sbSQL.append("\n      and ae.bisnieto = e.no_empresa ");
		    sbSQL.append("\n      and e.no_empresa in ");
		    sbSQL.append("\n          ( SELECT no_empresa ");
		    sbSQL.append("\n            FROM usuario_empresa ");
		    sbSQL.append("\n            WHERE no_usuario = " + plUsuario + " ) ");
		    sbSQL.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, ae.bisnieto, e.no_empresa, e.nom_empresa ");
		    sbSQL.append("\n  UNION ALL ");
		    // Divisa Empresa bisnieto
		    sbSQL.append("\n  SELECT distinct coalesce(ae.padre, 0) as padre, coalesce(ae.hijo, 0) as hijo, coalesce(ae.nieto, 0) as nieto,");
		    sbSQL.append("\n      coalesce(ae.bisnieto, 0) as bisnieto, 0 as tataranieto, 0 as chosno, 0 as chosno2, 0 as chosno3, 0 as chosno4, e.no_empresa, " + sOp + " as shijo, ");
		    sbSQL.append("\n      " + sOp + " as snieto, ");
		    sbSQL.append("\n      " + sOp + " as sbisnieto, cd.desc_divisa as stataranieto, " + sOp + " as schosno, ");
		    sbSQL.append("\n      " + sOp + " as schosno2, " + sOp + " as schosno3, " + sOp + " as schosno4, ");
		    sbSQL.append("\n      " + sOp + " as sDivisa, 6 as secuencia ");
		    sbSQL.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
		    sbSQL.append("\n      arbol_empresa ae, cat_divisa cd ");
		    sbSQL.append("\n  WHERE e.no_empresa = p.no_empresa ");
		    sbSQL.append("\n      and p.id_tipo_persona in ('I','E') ");
		    sbSQL.append("\n      and ( e.no_controladora = " + plEmpresa);
		    sbSQL.append("\n          or e.no_controladora in ");
		    sbSQL.append("\n              ( SELECT e1.no_empresa ");
		    sbSQL.append("\n                FROM empresa e1 ");
		    sbSQL.append("\n                WHERE e1.no_controladora = " + plEmpresa);
		    sbSQL.append("\n                    and e1.b_concentradora = 'S' ");
		    sbSQL.append("\n              ) ");
		    sbSQL.append("\n          or e.no_empresa = " + plEmpresa);
		    sbSQL.append("\n          or e.no_empresa = ae.tataranieto ) ");
		    sbSQL.append("\n      and ae.bisnieto = e.no_empresa ");
		    sbSQL.append("\n      and cd.id_divisa = c.id_divisa ");
		    sbSQL.append("\n      and e.no_empresa in ");
		    sbSQL.append("\n          ( SELECT no_empresa ");
		    sbSQL.append("\n            FROM usuario_empresa ");
		    sbSQL.append("\n            WHERE no_usuario = " + plUsuario + " ) ");
		    sbSQL.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, ae.bisnieto, e.no_empresa, e.nom_empresa, cd.desc_divisa ");
		    sbSQL.append("\n  UNION ALL ");
		    // Empresa Tataranieto
		    sbSQL.append("\n  SELECT distinct coalesce(ae.padre, 0) as padre, coalesce(ae.hijo, 0) as hijo, coalesce(ae.nieto, 0) as nieto, ");
		    sbSQL.append("\n      coalesce(ae.bisnieto, 0) as bisnieto,  coalesce(ae.tataranieto, 0) as tataranieto, 0 as chosno, 0 as chosno2, 0 as chosno3, 0 as chosno4, e.no_empresa, ");
		    sbSQL.append("\n      " + sOp + " as shijo, " + sOp + " as snieto, " + sOp + " as sbisnieto, coalesce(e.nom_empresa, '') as stataranieto, ");
		    sbSQL.append("\n      " + sOp + " as schosno, " + sOp + " as schosno2, " + sOp + " as schosno3, " + sOp + " as schosno4, ");
		    sbSQL.append("\n      " + sOp + " as sDivisa, 7 as secuencia ");
		    sbSQL.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
		    sbSQL.append("\n      arbol_empresa ae ");
		    sbSQL.append("\n  WHERE e.no_empresa = p.no_empresa ");
		    sbSQL.append("\n      and p.id_tipo_persona in ('I','E') ");
		    sbSQL.append("\n      and ( e.no_controladora = " + plEmpresa);
		    sbSQL.append("\n          or e.no_controladora in ");
		    sbSQL.append("\n              ( SELECT e1.no_empresa ");
		    sbSQL.append("\n                FROM empresa e1 ");
		    sbSQL.append("\n                WHERE e1.no_controladora = " + plEmpresa);
		    sbSQL.append("\n                    and e1.b_concentradora = 'S' ");
		    sbSQL.append("\n              ) ");
		    sbSQL.append("\n          or e.no_empresa = " + plEmpresa);
		    sbSQL.append("\n          or e.no_empresa = ae.chosno ) ");
		    sbSQL.append("\n      and ae.tataranieto = e.no_empresa ");
		    sbSQL.append("\n      and e.no_empresa in ");
		    sbSQL.append("\n          ( SELECT no_empresa ");
		    sbSQL.append("\n            FROM usuario_empresa ");
		    sbSQL.append("\n            WHERE no_usuario = " + plUsuario + " ) ");
		    sbSQL.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, ae.bisnieto, ae.tataranieto, e.no_empresa, ");
		    sbSQL.append("\n      e.nom_empresa ");
		    sbSQL.append("\n  UNION ALL ");
		    // Divisa Empresa Tataranieto
		    sbSQL.append("\n  SELECT distinct coalesce(ae.padre, 0) as padre, coalesce(ae.hijo, 0) as hijo, coalesce(ae.nieto, 0) as nieto, ");
		    sbSQL.append("\n      coalesce(ae.bisnieto, 0) as bisnieto,  coalesce(ae.tataranieto, 0) as tataranieto, 0 as chosno, 0 as chosno2, 0 as chosno3, 0 as chosno4, e.no_empresa, ");
		    sbSQL.append("\n      " + sOp + " as shijo, " + sOp + " as snieto, " + sOp + " as sbisnieto, " + sOp + " as stataranieto, ");
		    sbSQL.append("\n      cd.desc_divisa as schosno, " + sOp + " as schosno2, " + sOp + " as schosno3, ");
		    sbSQL.append("\n      " + sOp + " as schosno4, " + sOp + " as sDivisa, 8 as secuencia ");
		    sbSQL.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta), persona p, ");
		    sbSQL.append("\n      arbol_empresa ae, cat_divisa cd ");
		    sbSQL.append("\n  WHERE e.no_empresa = p.no_empresa ");
		    sbSQL.append("\n      and p.id_tipo_persona in ('I','E') ");
		    sbSQL.append("\n      and ( e.no_controladora = " + plEmpresa);
		    sbSQL.append("\n          or e.no_controladora in ");
		    sbSQL.append("\n              ( SELECT e1.no_empresa ");
		    sbSQL.append("\n                FROM empresa e1 ");
		    sbSQL.append("\n                WHERE e1.no_controladora = " + plEmpresa);
		    sbSQL.append("\n                    and e1.b_concentradora = 'S' ");
		    sbSQL.append("\n              ) ");
		    sbSQL.append("\n          or e.no_empresa = " + plEmpresa);
		    sbSQL.append("\n          or e.no_empresa = ae.chosno ) ");
		    sbSQL.append("\n      and ae.tataranieto = e.no_empresa ");
		    sbSQL.append("\n      and cd.id_divisa = c.id_divisa ");
		    sbSQL.append("\n      and e.no_empresa in ");
		    sbSQL.append("\n          ( SELECT no_empresa ");
		    sbSQL.append("\n            FROM usuario_empresa ");
		    sbSQL.append("\n            WHERE no_usuario = " + plUsuario + " ) ");
		    sbSQL.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, ae.bisnieto, ae.tataranieto, e.no_empresa, ");
		    sbSQL.append("\n      e.nom_empresa, cd.desc_divisa ");
		    sbSQL.append("\n  UNION ALL ");
		    // Empresa Chosno
		    sbSQL.append("\n  SELECT distinct coalesce(ae.padre, 0) as padre, coalesce(ae.hijo, 0) as hijo, coalesce(ae.nieto, 0) as nieto, ");
		    sbSQL.append("\n      coalesce(ae.bisnieto, 0) as bisnieto, coalesce(ae.tataranieto, 0) as tataranieto, coalesce(ae.chosno, 0) as chosno, 0 as chosno2, ");
		    sbSQL.append("\n      0 as chosno3, 0 as chosno4, e.no_empresa, " + sOp + " as shijo, " + sOp + " as snieto, ");
		    sbSQL.append("\n      " + sOp + " as sbisnieto, " + sOp + " as stataranieto, ");
		    sbSQL.append("\n      coalesce(e.nom_empresa, '') as schosno, " + sOp + " as schosno2, ");
		    sbSQL.append("\n      " + sOp + " as schosno3, " + sOp + " as schosno4, ");
		    sbSQL.append("\n      " + sOp + " as sDivisa, 9 as secuencia ");
		    sbSQL.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
		    sbSQL.append("\n      arbol_empresa ae ");
		    sbSQL.append("\n  WHERE e.no_empresa = p.no_empresa ");
		    sbSQL.append("\n      and p.id_tipo_persona in ('I','E') ");
		    sbSQL.append("\n      and ( e.no_controladora = " + plEmpresa);
		    sbSQL.append("\n          or e.no_controladora in ");
		    sbSQL.append("\n              ( SELECT e1.no_empresa ");
		    sbSQL.append("\n                FROM empresa e1 ");
		    sbSQL.append("\n                WHERE e1.no_controladora = " + plEmpresa);
		    sbSQL.append("\n                    and e1.b_concentradora = 'S' ");
		    sbSQL.append("\n              ) ");
		    sbSQL.append("\n          or e.no_empresa = " + plEmpresa);
		    sbSQL.append("\n          or e.no_empresa = ae.chosno2 ) ");
		    sbSQL.append("\n      and ae.chosno = e.no_empresa ");
		    sbSQL.append("\n      and e.no_empresa in ");
		    sbSQL.append("\n          ( SELECT no_empresa ");
		    sbSQL.append("\n            FROM usuario_empresa ");
		    sbSQL.append("\n            WHERE no_usuario = " + plUsuario + " ) ");
		    sbSQL.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, ae.bisnieto, ae.tataranieto, ae.chosno, ");
		    sbSQL.append("\n      e.no_empresa, e.nom_empresa ");
		    sbSQL.append("\n  UNION ALL ");
		    // Divisa Empresa Chosno
		    sbSQL.append("\n  SELECT distinct coalesce(ae.padre, 0) as padre, coalesce(ae.hijo, 0) as hijo, coalesce(ae.nieto, 0) as nieto, ");
		    sbSQL.append("\n      coalesce(ae.bisnieto, 0) as bisnieto, coalesce(ae.tataranieto, 0) as tataranieto, coalesce(ae.chosno, 0) as chosno, 0 as chosno2, ");
		    sbSQL.append("\n      0 as chosno3, 0 as chosno4, e.no_empresa, " + sOp + " as shijo, " + sOp + " as snieto, ");
		    sbSQL.append("\n      " + sOp + " as sbisnieto, " + sOp + " as stataranieto, ");
		    sbSQL.append("\n      " + sOp + " as schosno, cd.desc_divisa as schosno2, " + sOp + " as schosno3, ");
		    sbSQL.append("\n      " + sOp + " as schosno4, " + sOp + " as sDivisa, 10 as secuencia ");
		    sbSQL.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
		    sbSQL.append("\n      arbol_empresa ae, cat_divisa cd ");
		    sbSQL.append("\n  WHERE e.no_empresa = p.no_empresa ");
		    sbSQL.append("\n      and p.id_tipo_persona in ('I','E') ");
		    sbSQL.append("\n      and ( e.no_controladora = " + plEmpresa);
		    sbSQL.append("\n          or e.no_controladora in ");
		    sbSQL.append("\n              ( SELECT e1.no_empresa ");
		    sbSQL.append("\n                FROM empresa e1 ");
		    sbSQL.append("\n                WHERE e1.no_controladora = " + plEmpresa);
		    sbSQL.append("\n                    and e1.b_concentradora = 'S' ");
		    sbSQL.append("\n              ) ");
		    sbSQL.append("\n          or e.no_empresa = " + plEmpresa);
		    sbSQL.append("\n          or e.no_empresa = ae.chosno2 ) ");
		    sbSQL.append("\n      and ae.chosno = e.no_empresa ");
		    sbSQL.append("\n      and cd.id_divisa = c.id_divisa ");
		    sbSQL.append("\n      and e.no_empresa in ");
		    sbSQL.append("\n          ( SELECT no_empresa ");
		    sbSQL.append("\n            FROM usuario_empresa ");
		    sbSQL.append("\n            WHERE no_usuario = " + plUsuario + " ) ");
		    sbSQL.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, ae.bisnieto, ae.tataranieto, ae.chosno, ");
		    sbSQL.append("\n      e.no_empresa, e.nom_empresa, cd.desc_divisa ");
		    sbSQL.append("\n  UNION ALL ");
		    // Empresa Chosno2
		    sbSQL.append("\n  SELECT distinct coalesce(ae.padre, 0) as padre, coalesce(ae.hijo, 0) as hijo, coalesce(ae.nieto, 0) as nieto, ");
		    sbSQL.append("\n      coalesce(ae.bisnieto, 0) as bisnieto,  coalesce(ae.tataranieto, 0) as tataranieto, coalesce(ae.chosno, 0) as chosno, ");
		    sbSQL.append("\n      coalesce(ae.chosno2, 0) as chosno2, 0 as chosno3, 0 as chosno4, e.no_empresa, " + sOp + " as shijo, " + sOp + " as snieto, ");
		    sbSQL.append("\n      " + sOp + " as sbisnieto, " + sOp + " as stataranieto, " + sOp + " as schosno, ");
		    sbSQL.append("\n      coalesce(e.nom_empresa, '') as schosno2, " + sOp + " as schosno3, ");
		    sbSQL.append("\n      " + sOp + " as schosno4, " + sOp + " as sDivisa, 11 as secuencia ");
		    sbSQL.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
		    sbSQL.append("\n      arbol_empresa ae ");
		    sbSQL.append("\n  WHERE e.no_empresa = p.no_empresa ");
		    sbSQL.append("\n      and p.id_tipo_persona in ('I','E') ");
		    sbSQL.append("\n      and ( e.no_controladora = " + plEmpresa);
		    sbSQL.append("\n          or e.no_controladora in ");
		    sbSQL.append("\n              ( SELECT e1.no_empresa ");
		    sbSQL.append("\n                FROM empresa e1 ");
		    sbSQL.append("\n                WHERE e1.no_controladora = " + plEmpresa);
		    sbSQL.append("\n                    and e1.b_concentradora = 'S' ");
		    sbSQL.append("\n              ) ");
		    sbSQL.append("\n          or e.no_empresa = " + plEmpresa);
		    sbSQL.append("\n          or e.no_empresa = ae.chosno3 ) ");
		    sbSQL.append("\n      and ae.chosno2 = e.no_empresa ");
		    sbSQL.append("\n      and e.no_empresa in ");
		    sbSQL.append("\n          ( SELECT no_empresa ");
		    sbSQL.append("\n            FROM usuario_empresa ");
		    sbSQL.append("\n            WHERE no_usuario = " + plUsuario + " ) ");
		    sbSQL.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, ae.bisnieto, ae.tataranieto, ae.chosno, ");
		    sbSQL.append("\n      ae.chosno2, e.no_empresa , e.nom_empresa ");
		    sbSQL.append("\n  UNION ALL ");
		    // Divisa Empresa Chosno2
		    sbSQL.append("\n  SELECT distinct coalesce(ae.padre, 0) as padre, coalesce(ae.hijo, 0) as hijo, coalesce(ae.nieto, 0) as nieto, ");
		    sbSQL.append("\n      coalesce(ae.bisnieto, 0) as bisnieto,  coalesce(ae.tataranieto, 0) as tataranieto, coalesce(ae.chosno, 0) as chosno, ");
		    sbSQL.append("\n      coalesce(ae.chosno2, 0) as chosno2, 0 as chosno3, 0 as chosno4, e.no_empresa, " + sOp + " as shijo, " + sOp + " as snieto, ");
		    sbSQL.append("\n      " + sOp + " as sbisnieto, " + sOp + " as stataranieto, " + sOp + " as schosno, ");
		    sbSQL.append("\n      " + sOp + " as schosno2, cd.desc_divisa as schosno3, " + sOp + " as schosno4, ");
		    sbSQL.append("\n      " + sOp + " as sDivisa, 12 as secuencia ");
		    sbSQL.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
		    sbSQL.append("\n      arbol_empresa ae, cat_divisa cd ");
		    sbSQL.append("\n  WHERE e.no_empresa = p.no_empresa ");
		    sbSQL.append("\n      and p.id_tipo_persona in ('I','E') ");
		    sbSQL.append("\n      and ( e.no_controladora = " + plEmpresa);
		    sbSQL.append("\n          or e.no_controladora in ");
		    sbSQL.append("\n              ( SELECT e1.no_empresa ");
		    sbSQL.append("\n                FROM empresa e1 ");
		    sbSQL.append("\n                WHERE e1.no_controladora = " + plEmpresa);
		    sbSQL.append("\n                    and e1.b_concentradora = 'S' ");
		    sbSQL.append("\n              ) ");
		    sbSQL.append("\n          or e.no_empresa = " + plEmpresa);
		    sbSQL.append("\n          or e.no_empresa = ae.chosno3 ) ");
		    sbSQL.append("\n      and ae.chosno2 = e.no_empresa ");
		    sbSQL.append("\n      and cd.id_divisa = c.id_divisa ");
		    sbSQL.append("\n      and e.no_empresa in ");
		    sbSQL.append("\n          ( SELECT no_empresa ");
		    sbSQL.append("\n            FROM usuario_empresa ");
		    sbSQL.append("\n            WHERE no_usuario = " + plUsuario + " ) ");
		    sbSQL.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, ae.bisnieto, ae.tataranieto, ae.chosno, ");
		    sbSQL.append("\n      ae.chosno2, e.no_empresa , e.nom_empresa, cd.desc_divisa ");
		    sbSQL.append("\n  UNION ALL ");
		    // Empresa Chosno3
		    sbSQL.append("\n  SELECT distinct coalesce(ae.padre, 0) as padre, coalesce(ae.hijo, 0) as hijo, coalesce(ae.nieto, 0) as nieto, ");
		    sbSQL.append("\n      coalesce(ae.bisnieto, 0) as bisnieto, coalesce(ae.tataranieto, 0) as tataranieto, coalesce(ae.chosno, 0) as chosno, ");
		    sbSQL.append("\n      coalesce(ae.chosno2, 0) as chosno2, coalesce(ae.chosno3, 0) as chosno3, 0 as chosno4, e.no_empresa, " + sOp + " as shijo, ");
		    sbSQL.append("\n      " + sOp + " as snieto, " + sOp + " as sbisnieto, " + sOp + " as stataranieto, ");
		    sbSQL.append("\n      " + sOp + " as schosno, " + sOp + " as schosno2, ");
		    sbSQL.append("\n      coalesce(e.nom_empresa, '') as schosno3, " + sOp + " as schosno4, " + sOp + " as sDivisa, 13 as secuencia ");
		    sbSQL.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
		    sbSQL.append("\n      arbol_empresa ae ");
		    sbSQL.append("\n  WHERE e.no_empresa = p.no_empresa ");
		    sbSQL.append("\n      and p.id_tipo_persona in ('I','E') ");
		    sbSQL.append("\n      and ( e.no_controladora = " + plEmpresa);
		    sbSQL.append("\n          or e.no_controladora in ");
		    sbSQL.append("\n              ( SELECT e1.no_empresa ");
		    sbSQL.append("\n                FROM empresa e1 ");
		    sbSQL.append("\n                WHERE e1.no_controladora = " + plEmpresa);
		    sbSQL.append("\n                    and e1.b_concentradora = 'S' ");
		    sbSQL.append("\n              ) ");
		    sbSQL.append("\n          or e.no_empresa = " + plEmpresa);
		    sbSQL.append("\n          or e.no_empresa = ae.chosno4 ) ");
		    sbSQL.append("\n      and ae.chosno3 = e.no_empresa ");
		    sbSQL.append("\n      and e.no_empresa in ");
		    sbSQL.append("\n          ( SELECT no_empresa ");
		    sbSQL.append("\n            FROM usuario_empresa ");
		    sbSQL.append("\n            WHERE no_usuario = " + plUsuario + " ) ");
		    sbSQL.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, ae.bisnieto, ae.tataranieto, ae.chosno, ");
		    sbSQL.append("\n      ae.chosno2, ae.chosno3, e.no_empresa, e.nom_empresa ");
		    sbSQL.append("\n  UNION ALL ");
		    // Divisa Empresa Chosno3
		    sbSQL.append("\n  SELECT distinct coalesce(ae.padre, 0) as padre, coalesce(ae.hijo, 0) as hijo, coalesce(ae.nieto, 0) as nieto, ");
		    sbSQL.append("\n      coalesce(ae.bisnieto, 0) as bisnieto, coalesce(ae.tataranieto, 0) as tataranieto, coalesce(ae.chosno, 0) as chosno, ");
		    sbSQL.append("\n      coalesce(ae.chosno2, 0) as chosno2, coalesce(ae.chosno3, 0) as chosno3, 0 as chosno4, e.no_empresa, " + sOp + " as shijo, ");
		    sbSQL.append("\n      " + sOp + " as snieto, " + sOp + " as sbisnieto, " + sOp + " as stataranieto, ");
		    sbSQL.append("\n      " + sOp + " as schosno, " + sOp + " as schosno2, ");
		    sbSQL.append("\n      " + sOp + " as schosno3, cd.desc_divisa as schosno4, " + sOp + " as sDivisa, 14 as secuencia ");
		    sbSQL.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
		    sbSQL.append("\n      arbol_empresa ae, cat_divisa cd ");
		    sbSQL.append("\n  WHERE e.no_empresa = p.no_empresa ");
		    sbSQL.append("\n      and p.id_tipo_persona in ('I','E') ");
		    sbSQL.append("\n      and ( e.no_controladora = " + plEmpresa);
		    sbSQL.append("\n          or e.no_controladora in ");
		    sbSQL.append("\n              ( SELECT e1.no_empresa ");
		    sbSQL.append("\n                FROM empresa e1 ");
		    sbSQL.append("\n                WHERE e1.no_controladora = " + plEmpresa);
		    sbSQL.append("\n                    and e1.b_concentradora = 'S' ");
		    sbSQL.append("\n              ) ");
		    sbSQL.append("\n          or e.no_empresa = " + plEmpresa);
		    sbSQL.append("\n          or e.no_empresa = ae.chosno4 ) ");
		    sbSQL.append("\n      and ae.chosno3 = e.no_empresa ");
		    sbSQL.append("\n      and cd.id_divisa = c.id_divisa ");
		    sbSQL.append("\n      and e.no_empresa in ");
		    sbSQL.append("\n          ( SELECT no_empresa ");
		    sbSQL.append("\n            FROM usuario_empresa ");
		    sbSQL.append("\n            WHERE no_usuario = " + plUsuario + " ) ");
		    sbSQL.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, ae.bisnieto, ae.tataranieto, ae.chosno, ");
		    sbSQL.append("\n      ae.chosno2, ae.chosno3, e.no_empresa, e.nom_empresa, cd.desc_divisa ");
		    sbSQL.append("\n  UNION ALL ");
		    // Empresa Chosno4
		    sbSQL.append("\n  SELECT distinct coalesce(ae.padre, 0) as padre, coalesce(ae.hijo, 0) as hijo, coalesce(ae.nieto, 0) as nieto, ");
		    sbSQL.append("\n      coalesce(ae.bisnieto, 0) as bisnieto, coalesce(ae.tataranieto, 0) as tataranieto, coalesce(ae.chosno, 0) as chosno, ");
		    sbSQL.append("\n      coalesce(ae.chosno2, 0) as chosno2, coalesce(ae.chosno3, 0) as chosno3, coalesce(ae.chosno4, 0) as chosno4, ");
		    sbSQL.append("\n      e.no_empresa, " + sOp + " as shijo, " + sOp + " as snieto, ");
		    sbSQL.append("\n      " + sOp + " as sbisnieto, " + sOp + " as stataranieto, ");
		    sbSQL.append("\n      " + sOp + " as schosno, " + sOp + " as schosno2, ");
		    sbSQL.append("\n      " + sOp + " as schosno3, coalesce(e.nom_empresa, '') as schosno4, ");
		    sbSQL.append("\n      " + sOp + " as sDivisa, 15 as secuencia ");
		    sbSQL.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
		    sbSQL.append("\n      arbol_empresa ae ");
		    sbSQL.append("\n  WHERE e.no_empresa = p.no_empresa ");
		    sbSQL.append("\n      and p.id_tipo_persona in ('I','E') ");
		    sbSQL.append("\n      and ( e.no_controladora = " + plEmpresa);
		    sbSQL.append("\n          or e.no_controladora in ");
		    sbSQL.append("\n              ( SELECT e1.no_empresa ");
		    sbSQL.append("\n                FROM empresa e1 ");
		    sbSQL.append("\n                WHERE e1.no_controladora = " + plEmpresa);
		    sbSQL.append("\n                    and e1.b_concentradora = 'S' ");
		    sbSQL.append("\n              ) ");
		    sbSQL.append("\n          or e.no_empresa = " + plEmpresa + " ) ");
		    sbSQL.append("\n      and ae.chosno4 = e.no_empresa ");
		    sbSQL.append("\n      and e.no_empresa in ");
		    sbSQL.append("\n          ( SELECT no_empresa ");
		    sbSQL.append("\n            FROM usuario_empresa ");
		    sbSQL.append("\n            WHERE no_usuario = " + plUsuario + " ) ");
		    sbSQL.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, ae.bisnieto, ae.tataranieto, ae.chosno, ae.chosno2, ");
		    sbSQL.append("\n      ae.chosno3, ae.chosno4, e.no_empresa, e.nom_empresa ");
		    sbSQL.append("\n  UNION ALL ");
		    // Divisa Empresa Chosno4
		    sbSQL.append("\n  SELECT distinct coalesce(ae.padre, 0) as padre, coalesce(ae.hijo, 0) as hijo, coalesce(ae.nieto, 0) as nieto, ");
		    sbSQL.append("\n      coalesce(ae.bisnieto, 0) as bisnieto, coalesce(ae.tataranieto, 0) as tataranieto, coalesce(ae.chosno, 0) as chosno, ");
		    sbSQL.append("\n      coalesce(ae.chosno2, 0) as chosno2, coalesce(ae.chosno3, 0) as chosno3, coalesce(ae.chosno4, 0) as chosno4, ");
		    sbSQL.append("\n      e.no_empresa, " + sOp + " as shijo, " + sOp + " as snieto, ");
		    sbSQL.append("\n      " + sOp + " as sbisnieto, " + sOp + " as stataranieto, ");
		    sbSQL.append("\n      " + sOp + " as schosno, " + sOp + " as schosno2, ");
		    sbSQL.append("\n      " + sOp + " as schosno3, " + sOp + " as schosno4, ");
		    sbSQL.append("\n      cd.desc_divisa as sDivisa, 16 as secuencia ");
		    sbSQL.append("\n  FROM empresa e LEFT JOIN cuenta c ON (e.no_empresa = c.no_cuenta),persona p, ");
		    sbSQL.append("\n      arbol_empresa ae, cat_divisa cd ");
		    sbSQL.append("\n  WHERE e.no_empresa = p.no_empresa ");
		    sbSQL.append("\n      and p.id_tipo_persona in ('I','E') ");
		    sbSQL.append("\n      and ( e.no_controladora = " + plEmpresa);
		    sbSQL.append("\n          or e.no_controladora in ");
		    sbSQL.append("\n              ( SELECT e1.no_empresa ");
		    sbSQL.append("\n                FROM empresa e1 ");
		    sbSQL.append("\n                WHERE e1.no_controladora = " + plEmpresa);
		    sbSQL.append("\n                    and e1.b_concentradora = 'S' ");
		    sbSQL.append("\n              ) ");
		    sbSQL.append("\n          or e.no_empresa = " + plEmpresa + " ) ");
		    sbSQL.append("\n      and ae.chosno4 = e.no_empresa ");
		    sbSQL.append("\n      and cd.id_divisa = c.id_divisa ");
		    sbSQL.append("\n      and e.no_empresa in ");
		    sbSQL.append("\n          ( SELECT no_empresa ");
		    sbSQL.append("\n            FROM usuario_empresa ");
		    sbSQL.append("\n            WHERE no_usuario = " + plUsuario + " ) ");
		    sbSQL.append("\n  GROUP BY ae.padre, ae.hijo, ae.nieto, ae.bisnieto, ae.tataranieto, ae.chosno, ae.chosno2, ");
		    sbSQL.append("\n      ae.chosno3, ae.chosno4, e.no_empresa, e.nom_empresa, cd.desc_divisa ");
		    
		    if(ConstantesSet.gsDBM.equals("POSTGRESQL") || ConstantesSet.gsDBM.equals("DB2"))
		        sbSQL.append("\n  ORDER BY padre, hijo, nieto, bisnieto, tataranieto, chosno, chosno2, chosno3, chosno4, no_empresa, secuencia ");
		    else
		        sbSQL.append("\n  ORDER BY padre, hijo, nieto, bisnieto, tataranieto, chosno, chosno2, chosno3, chosno4, e.no_empresa, secuencia ");

		    //logger.info("reporte coinversion " + sbSQL.toString());
		    resultado = (List<Map<String, Object>>)jdbcTemplate.query(sbSQL.toString(), new RowMapper(){

				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("padre", rs.getString("padre"));
			            results.put("hijo", rs.getString("hijo"));
			            results.put("nieto", rs.getString("nieto"));
			            results.put("bisnieto", rs.getString("bisnieto"));
			            results.put("tataranieto", rs.getString("tataranieto"));
			            results.put("chosno", rs.getString("chosno"));
			            results.put("chosno2", rs.getString("chosno2"));
			            results.put("chosno3", rs.getString("chosno3"));
			            results.put("chosno4", rs.getString("chosno4"));
			            results.put("no_empresa", rs.getString("no_empresa"));
			            results.put("shijo", rs.getString("shijo"));
			            results.put("snieto", rs.getString("snieto"));
			            results.put("sbisnieto", rs.getString("sbisnieto"));
			            results.put("stataranieto", rs.getString("stataranieto"));
			            results.put("schosno", rs.getString("schosno"));
			            results.put("schosno2", rs.getString("schosno2"));
			            results.put("schosno3", rs.getString("schosno3"));
			            results.put("schosno4", rs.getString("schosno4"));
			            results.put("sDivisa", rs.getString("sDivisa"));
			            results.put("secuencia", rs.getString("secuencia"));
			            return results;
					}
				});

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarReporteCoinversoras");
		}
		return resultado;
	}
	
	//inician consultas barrido automatico por saldos

	/**
	 * FunSQLComboBancoCuentas
	 * Metodo que consulta los bancos que contengan chequeras mixtas o concentradoras
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarBancosConcentradora(boolean bConcentradora,
			int iIdEmpresa, String idDivisa){
		StringBuffer sbSQL = new StringBuffer();
		List<LlenaComboGralDto> listDatos = new ArrayList<LlenaComboGralDto>();
		try{
			sbSQL.append(" Select distinct cb.id_banco as ID, cb.desc_banco as DESCRIPCION ");
		    sbSQL.append(" From cat_banco cb ,cat_cta_banco ccb ");
		    sbSQL.append(" Where ccb.id_banco = cb.id_banco ");
		    
		    if(bConcentradora)
		    {
		    	sbSQL.append(" and ccb.no_empresa = " + iIdEmpresa);
		        sbSQL.append(" and  ccb.tipo_chequera in ('M','C') ");
		        sbSQL.append(" and  ccb.id_divisa = '" + idDivisa + "'");
		    }
		    
		    listDatos = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
		    	public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		LlenaComboGralDto cons = new LlenaComboGralDto();
			    		cons.setId(rs.getInt("ID"));
			    		cons.setDescripcion(rs.getString("DESCRIPCION"));
		    		return cons;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarBancosConcentradora");
		}
		return listDatos;
	}
	
	/**
	 * FunSQLComboChequera
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboChequeraDto> consultarChequerasBarrido(boolean bPagadora, int iBanco, int iNoEmpresa){
		List<LlenaComboChequeraDto> list = new ArrayList<LlenaComboChequeraDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  Select id_chequera as ID, id_chequera as Descrip ");
	        sbSQL.append("\n  From cat_cta_banco ");
	        sbSQL.append("\n  Where ");
	        sbSQL.append("\n  id_banco = " + iBanco);
	        sbSQL.append("\n  and no_empresa =  " + iNoEmpresa);
	        if(bPagadora)
	            sbSQL.append("\n  and tipo_chequera in('M','C','P')");
	        else
	            sbSQL.append("\n  and tipo_chequera in('M', 'C')");
	        sbSQL.append("\n  Order by id_chequera");
	        
	        list = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
		    	public LlenaComboChequeraDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		LlenaComboChequeraDto cons = new LlenaComboChequeraDto();
			    		cons.setId(rs.getString("ID"));
			    		cons.setDescripcion(rs.getString("Descrip"));
		    		return cons;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarChequerasBarrido");
		}
		return list;
	}
	
	/**
	 * FunSQLSelectChequeras_Barrido_por_Saldo
	 * Metodo que consulta el barrido de las chequeras segun los bancos seleccionados
	 * @param iConcentradora
	 * @param sDivisa
	 * @param sBancos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<BarridoChequerasDto> consultarBarridoChequerasPorSaldo(int iConcentradora, String sDivisa, String sBancos){
		List<BarridoChequerasDto> listBarrido = new ArrayList<BarridoChequerasDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  SELECT distinct e.no_empresa,e.nom_empresa,c.id_divisa,");
		    sbSQL.append("\n  c.id_banco , b.desc_banco, c.id_chequera, ");
		    sbSQL.append("\n  s.saldo as saldo_chequera,c.saldo_final, ");
		    sbSQL.append("\n  Coalesce(c.saldo_minimo,0)as saldo_minimo, ");
		    sbSQL.append("\n  s.saldo as diferencia, ");
		    sbSQL.append("\n  s.secuencia, s.fec_valor, ");
		    sbSQL.append("\n  coalesce(c.sobregiro, 0) as sobregiro, 0 as monto_sobregiro, ");
		    
		    sbSQL.append("\n  ( SELECT s.importe ");
		    sbSQL.append("\n    FROM saldo s ");
		    sbSQL.append("\n    WHERE s.id_tipo_saldo = 91 ");
		    sbSQL.append("\n        and s.no_empresa = c.no_empresa ");
		    sbSQL.append("\n        and s.no_cuenta = ");
		    sbSQL.append("\n            ( SELECT l.no_cuenta ");
		    sbSQL.append("\n              FROM cuenta l ");
		    sbSQL.append("\n              WHERE l.id_tipo_cuenta = 'P' ");
		    sbSQL.append("\n                  and l.no_empresa = c.no_empresa ");
		    sbSQL.append("\n                  and l.no_linea = ");
		    if(ConstantesSet.gsDBM.equals("POSTGRESQL") || ConstantesSet.gsDBM.equals("DB2"))
		        sbSQL.append("\n                      ( SELECT cast(coalesce(id_divisa_soin, '0') as integer) ");
		    else
		        sbSQL.append("\n                      ( SELECT convert(int, coalesce(id_divisa_soin, '0') ) ");
		 
		    sbSQL.append("\n                        FROM cat_divisa ");
		    sbSQL.append("\n                        WHERE clasificacion = 'D' ");
		    sbSQL.append("\n                            and id_divisa = '" + sDivisa+ "' ) )");
		    sbSQL.append("\n        and s.no_linea = ");
		    if(ConstantesSet.gsDBM.equals("POSTGRESQL") || ConstantesSet.gsDBM.equals("DB2"))
		        sbSQL.append("\n                      ( SELECT cast(coalesce(id_divisa_soin, '0') as integer) ");
		    else
		        sbSQL.append("\n                      ( SELECT convert(int, coalesce(id_divisa_soin, '0') ) ");
		  
		    sbSQL.append("\n              FROM cat_divisa ");
		    sbSQL.append("\n              WHERE clasificacion = 'D' ");
		    sbSQL.append("\n                  and id_divisa = '" + sDivisa+ "' ) ");
		    sbSQL.append("\n  ) as saldo_credito ");
		    
		    sbSQL.append("\n  FROM cat_cta_banco c, cat_banco b, empresa e,");
		    sbSQL.append("\n       saldo_chequera s ");
		    sbSQL.append("\n  WHERE s.id_chequera = c.id_chequera ");
		    sbSQL.append("\n  and s.id_banco = c.id_banco ");
		    sbSQL.append("\n  and s.no_empresa = c.no_empresa ");
		    sbSQL.append("\n  and s.no_empresa = e.no_empresa ");
		    sbSQL.append("\n  and c.id_banco=b.id_banco ");
		    sbSQL.append("\n  and c.tipo_chequera = 'C' ");
		    sbSQL.append("\n  and s.id_estatus is null ");  //'Solo mostrar las que no se han procesado
		    sbSQL.append("\n  and e.no_controladora =  " + iConcentradora);
		    sbSQL.append("\n  and c.id_divisa = '" + sDivisa+ "'");
		    sbSQL.append("\n  and s.id_banco in(" + sBancos + ")");
		    
		    sbSQL.append("\n  and fec_valor in(Select max(s2.fec_valor) from saldo_chequera s2 where s2.id_banco =  s.id_banco and ");
		    
		    
		    sbSQL.append("\n      s2.id_chequera = s.id_chequera and s.id_estatus is null) ");
		    sbSQL.append("\n  ORDER BY e.no_empresa ");
		    //logger.info(sbSQL.toString());
		    
		    bitacora.insertarRegistro("El query "+ sbSQL.toString());
		    System.out.println("El query es::.. "+ sbSQL.toString());
		    listBarrido = jdbcTemplate.query(sbSQL.toString(), new RowMapper<BarridoChequerasDto>(){
		    	public BarridoChequerasDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		BarridoChequerasDto cons = new BarridoChequerasDto();
			    		cons.setNoEmpresa(rs.getInt("no_empresa"));
			    		cons.setNomEmpresa(rs.getString("nom_empresa"));
			    		cons.setIdDivisa(rs.getString("id_divisa"));
			    		cons.setIdBanco(rs.getInt("id_banco"));
			    		cons.setDescBanco(rs.getString("desc_banco"));
			    		cons.setIdChequera(rs.getString("id_chequera"));
			    		cons.setSaldoChequera(rs.getDouble("saldo_chequera"));
			    		cons.setSaldoFinal(rs.getDouble("saldo_final"));
			    		cons.setSaldoMinimo(rs.getDouble("saldo_minimo"));
			    		cons.setDiferencia(rs.getDouble("diferencia"));
			    		cons.setSecuencia(rs.getInt("secuencia"));
			    		cons.setFecValor(rs.getDate("fec_valor"));
			    		cons.setSobregiro(rs.getDouble("sobregiro"));
			    		cons.setMontoSobregiro(rs.getDouble("monto_sobregiro"));
			    		cons.setSaldoCredito(rs.getDouble("saldo_credito"));
		    		return cons;
		    	}
		    });
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarBarridoChequerasPorSaldo");
		}
		return listBarrido;
	}
	
	/**
	 * FunSQLDelete_saldos_chequeras
	 * Metodo que elimina registros de fechas anteriores a hoy
	 * @param dFechaHoy
	 * @return
	 */
	public int eliminarSaldosChequeras(Date dFechaHoy){
		int iElimina = -1;
		StringBuffer sbSQL = new StringBuffer();
		try{
			if(ConstantesSet.gsDBM.equals("POSTGRESQL") || ConstantesSet.gsDBM.equals("DB2"))
	            sbSQL.append("\n  DELETE FROM saldo_chequera ");
	        else
	            sbSQL.append("\n  DELETE saldo_chequera ");
	    
			if(ConstantesSet.gsDBM.equals("DB2"))
	            sbSQL.append("\n  WHERE fec_valor < '" + funciones.cambiarFecha(""+dFechaHoy, true) + "'");
	        else
	            sbSQL.append("\n  WHERE fec_valor < '" + funciones.ponerFechaSola(dFechaHoy) + "'");
		    
			logger.info("elimina saldos ");
		    iElimina = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:eliminarSaldosChequeras");
		}
		return iElimina;
	}
	
	/**
	 * FunSQLSelect_existe_archivo
	 * Metodo que consulta si ya esiste el archivo
	 * @param nomArchivo
	 * @return
	 */
	public int consultaExistenciaArchivo(String nomArchivo){
		int iExiste = -1;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  SELECT count(archivo) ");
		    sbSQL.append("\n  FROM archivo_saldo_chequera ");
		    sbSQL.append("\n  WHERE archivo = '" + nomArchivo + "'");
		    
		    iExiste = jdbcTemplate.queryForInt(sbSQL.toString());
		    logger.info("consultaExistenciaArchivo " + iExiste);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultaExistenciaArchivo");
		}
		return iExiste;
	}
	
	/**
	 * FunSQLSelectChequera_Concentradora
	 * @param iBanco
	 * @param sChequera
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<BarridoChequerasDto> consultarChequeraConcentradora(int iBanco, String sChequera){
		List<BarridoChequerasDto> list = new ArrayList<BarridoChequerasDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n SELECT c.no_empresa,c.id_chequera, e.no_controladora ");
	        sbSQL.append("\n  FROM cat_cta_banco c, empresa e");
	        sbSQL.append("\n  WHERE c.no_empresa = e.no_empresa ");
	        sbSQL.append("\n  and e.no_controladora is not null ");
	        sbSQL.append("\n  and tipo_chequera = 'C' ");
	        sbSQL.append("\n  and id_banco  =" + iBanco);
	        sbSQL.append("\n  and id_chequera like '%" + sChequera + "%'");
	        
	        list = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
		    	public BarridoChequerasDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		BarridoChequerasDto cons = new BarridoChequerasDto();
		    		cons.setNoEmpresa(rs.getInt("no_empresa"));
		    		cons.setIdChequera(rs.getString("id_chequera"));
		    		cons.setNoControladora(rs.getInt("no_controladora"));
		    		return cons;
		    	}
		    });
	        //logger.info("consultarChequeraConcentradora " + list.size());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarChequeraConcentradora");
		}
		return list;
	}
	
	/**
	 * FunSQLSelectSaldo_chequera
	 * Metodo que consulta si ya existe el movimiento con los parametros enviados
	 */
	@SuppressWarnings("unchecked")
	public List<SaldoChequeraDto> consultarSaldoChequera(int iBanco, String sChequera, String sFecha){
		List<SaldoChequeraDto> listSaldo = new ArrayList<SaldoChequeraDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  SELECT * ");
	        sbSQL.append("\n  FROM saldo_chequera ");
	        sbSQL.append("\n  WHERE ");
	        sbSQL.append("\n  id_banco  =" + iBanco);
	        sbSQL.append("\n  and id_chequera = '" + sChequera + "'");
	        
	        if(ConstantesSet.gsDBM.equals("DB2"))
	            sbSQL.append("\n  and fec_valor = '" + funciones.cambiarFecha(""+ sFecha, true) + "'");
	        else
	            sbSQL.append("\n  and fec_valor = '" + sFecha + "'");
	        logger.info("consultarSaldoChequera ");
	        listSaldo = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
		    	public SaldoChequeraDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		SaldoChequeraDto cons = new SaldoChequeraDto();
		    		cons.setNoEmpresa(rs.getInt("no_empresa"));
		    		cons.setIdBanco(rs.getInt("id_banco"));
		    		cons.setIdChequera(rs.getString("id_chequera"));
		    		cons.setFecValor(rs.getDate("fec_valor"));
		    		cons.setSecuencia(rs.getInt("secuencia"));
		    		cons.setSaldo(rs.getDouble("saldo"));
		    		cons.setArchivo(rs.getString("archivo"));
		    		cons.setIdEstatus(rs.getString("id_estatus"));
		    		return cons;
		    	}
		    });
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarSaldoChequera");
		}
		return listSaldo;
	}
	
	/**
	 * FunSQLInsert_Saldo_chequera
	 * @param dto
	 * @return
	 */
	public int insertaSaldoChequera(SaldoChequeraDto dto, String sFecha){
		int insert = -1;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  INSERT INTO saldo_chequera ");
	        sbSQL.append("\n  (no_empresa,id_banco,id_chequera,fec_valor,secuencia,saldo,archivo)");
	        sbSQL.append("\n  VALUES (" + dto.getNoEmpresa() + "," + dto.getIdBanco() + ",'" + dto.getIdChequera() + "',");
	        
	        if(ConstantesSet.gsDBM.equals("DB2"))
	            sbSQL.append("\n'" +  funciones.cambiarFecha(""+ dto.getFecValor(), true) + "'," + dto.getSecuencia() + "," + dto.getSaldo() + ",'" + dto.getArchivo() + "')");
	        else
	            sbSQL.append("\n'" + sFecha + "'," + dto.getSecuencia() + "," + dto.getSaldo() + ",'" + dto.getArchivo() + "')");
	        logger.info("insertaSaldoChequera ");
	        insert = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:insertaSaldoChequera");
		}
		return insert;
	}
	
	/**
	 * FunSQLInsertArchivos_saldos
	 * @param dto
	 * @param sFecha
	 * @return
	 */
	public int insertaArchivosSaldos(SaldoChequeraDto dto, String sFecha){
		int insert = -1;
		StringBuffer sbSQL = new StringBuffer();
		try{
			if(ConstantesSet.gsDBM.equals("DB2"))
			{
	            sbSQL.append("  INSERT INTO archivo_saldo_chequera (archivo,fec_valor,id_banco) ");
	            sbSQL.append("\n  values ('" + dto.getArchivo() + "','" + funciones.cambiarFecha(sFecha, true) + "'," + dto.getIdBanco() + ")");
			}
            else
            {
	            sbSQL.append("  INSERT INTO archivo_saldo_chequera (archivo,fec_valor,id_banco) ");
	            sbSQL.append("\n  values ('" + dto.getArchivo() + "','" + sFecha + "'," + dto.getIdBanco() + ")");
            }
			 logger.info("insertaArchivosSaldos ");
	        insert = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:insertaArchivosSaldos");
		}
		return insert;
	}
	
	/**
	 * Actualiza_estatus_saldo_chequera
	 * Metodo que actualiza las chequeras que ya fueron procesadas
	 * @param sSecuencias
	 * @return
	 */
	public int actualizaEstatusSaldoChequera(String sSecuencias){
		int iActualiza = -1;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  UPDATE saldo_chequera ");
	        sbSQL.append("\n  SET id_estatus = 'X' ");
	        sbSQL.append("\n  WHERE secuencia in(" + sSecuencias + ")");
	        System.out.println("Actualiza estatus X::.."+sbSQL.toString());
	        iActualiza = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:actualizaEstatusSaldoChequera");
		}
		return iActualiza;
	}
	
	/**
	 * FunSQLInsertParametro1
	 * @param dto
	 * @return
	 */
	public int insertarParametro1(ParametroDto dto){
		int iInserta = -1;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n INSERT INTO parametro  (");
		    sbSQL.append("\n  no_empresa, no_folio_param, aplica, secuencia, id_tipo_operacion");
		    sbSQL.append("\n ,no_cuenta, id_estatus_mov, id_chequera,id_tipo_saldo ,id_banco, importe");
		    sbSQL.append("\n ,b_salvo_buen_cobro, fec_valor, fec_valor_original, id_estatus_reg, usuario_alta");
		    sbSQL.append("\n ,fec_alta, id_divisa, id_forma_pago, importe_original, fec_operacion");
		    sbSQL.append("\n ,id_banco_benef, id_chequera_benef, origen_mov, id_caja, no_folio_mov");
		    sbSQL.append("\n ,folio_ref, grupo, concepto, beneficiario, no_cliente");
		    
		    if(dto.getMontoSobregiro() > 0)
		    	sbSQL.append("\n ,monto_sobregiro");
		    
		    sbSQL.append("\n )");
		    sbSQL.append("\n  VALUES");
		    sbSQL.append("\n ( ");
		    sbSQL.append( dto.getNoEmpresa());
		    sbSQL.append( "," + dto.getNoFolioParam());
		    sbSQL.append( ",1");
		    sbSQL.append( "," + dto.getSecuencia());
		    sbSQL.append( ",3805");
		    sbSQL.append( "," + dto.getCuenta());
		    sbSQL.append( ",'L'");
		    sbSQL.append( ",'" + dto.getIdChequera() + "'");
		    sbSQL.append( "," + dto.getTipo_saldo());
		    sbSQL.append( "," + dto.getIdBanco());
		    sbSQL.append( "," + dto.getImporte());
		    sbSQL.append( ",'S'");
		    
	        sbSQL.append( ",'" + funciones.ponerFechaSola(dto.getFecValor()) + " '");
	        sbSQL.append( ",'" + funciones.ponerFechaSola(dto.getFecValor()) + "'");
		    sbSQL.append( ",'P'");
		    sbSQL.append( "," + dto.getIdUsuario() + "");
		    
		    if(ConstantesSet.gsDBM.equals("DB2"))
		        sbSQL.append( ",'" + funciones.ponerFechaSola(dto.getFecValor()) + "'");
		    else
		        //sbSQL.append( ",'" + dto.getFecValor() + "'");
		    	sbSQL.append( ",'" + funciones.ponerFechaSola(dto.getFecValor()) + "'");
		    sbSQL.append( ",'" + dto.getIdDivisa() + "'");
		    sbSQL.append( ",3");
		    sbSQL.append( "," + dto.getImporte());
		    
	        sbSQL.append( ",'" + funciones.ponerFechaSola(dto.getFecValor()) + "'");
		    
		    sbSQL.append( "," + dto.getIdBancoBenef() + "");
		    sbSQL.append( ",'" + dto.getIdChequeraBenef() + "'");
		    sbSQL.append( ",'SET'");
		    sbSQL.append( "," + dto.getIdCaja());
		    sbSQL.append( "," + dto.getNoFolioMov());
		    sbSQL.append( "," + dto.getFolioRef());
		    sbSQL.append( "," + dto.getIdGrupo());
		    sbSQL.append( ",'APORTACIONES'");
		    sbSQL.append( ",'" + dto.getBeneficiario() + "'");
		    sbSQL.append( ",'" + dto.getNoCliente() + "'");
		    
		    if(dto.getMontoSobregiro() > 0)
		    	sbSQL.append( ",'" + dto.getMontoSobregiro() + "'");
		    
		    sbSQL.append( ")");
		    
		    iInserta = jdbcTemplate.update(sbSQL.toString());
		    System.out.println(iInserta);
		    System.out.println("Insert parametro:"+sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:insertarParametro1");
		}
		return iInserta;
	}
	
	/**consultas de solicitud de barrido**/
	/**
	 * FunSQLComboEmpresa
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboEmpresasDto> consultarEmpresasCoinversionistas(int iEmpresa, int iLinea, int iUsuario){
		StringBuffer sbSQL = new StringBuffer();
		List<LlenaComboEmpresasDto> list = new ArrayList<LlenaComboEmpresasDto>();
		try{
			sbSQL.append("\n  SELECT distinct e.no_empresa as ID, e.nom_empresa as Descrip ");
		    sbSQL.append("\n  FROM empresa e, cuenta c ");
		    sbSQL.append("\n  WHERE c.no_empresa=" +iEmpresa);
		    sbSQL.append("\n      and c.no_cuenta = e.no_empresa and c.no_linea = " + iLinea);
		    sbSQL.append("\n      and e.no_empresa in ");
		    sbSQL.append("\n          ( SELECT no_empresa ");
		    sbSQL.append("\n            FROM usuario_empresa ");
		    sbSQL.append("\n            WHERE no_usuario = " + iUsuario + " ) ");
		    sbSQL.append("\n  ORDER BY e.nom_empresa ");
		    System.out.println(sbSQL.toString());
		    list = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public LlenaComboEmpresasDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboEmpresasDto cons = new LlenaComboEmpresasDto();
					cons.setNoEmpresa(rs.getInt("ID"));
					cons.setNomEmpresa(rs.getString("Descrip"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarEmpresasCoinversionistas");	
		}
		return list;
	}
	
	/**
	 * FunSQLComboBanco3
	 * @param iEmpresa
	 * @param sDivisa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarBancos(int iEmpresa, String sDivisa){
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			  sbSQL.append("    Select distinct cb.id_banco as ID, cb.desc_banco as Descrip ");
		      sbSQL.append("\n  From cat_banco cb, cat_cta_banco ccb ");
		      sbSQL.append("\n  Where ccb.id_banco = cb.id_banco ");
		      sbSQL.append("\n  and ccb.no_empresa =" + iEmpresa);
		      sbSQL.append("\n  and tipo_chequera in ('C','M') ");
		      sbSQL.append("\n  and id_divisa = '" + sDivisa + "' ");
		      
		      list = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
					public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
						LlenaComboGralDto cons = new LlenaComboGralDto();
						cons.setId(rs.getInt("ID"));
						cons.setDescripcion(rs.getString("Descrip"));
						return cons;
					}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarBancos");
		}
		return list;
	}
 
	/**
	 * FunSQLSelectSaldoFinal
	 * @param iEmpresa
	 * @param iBanco
	 * @param sChequera
	 * @return
	 */
	public double consultarSaldoFinal(int iEmpresa, int iBanco, String sChequera){
		double saldo = 0;
		BigDecimal bdSaldo = new BigDecimal(0);
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("    Select saldo_final ");
		    sbSQL.append("\n  From cat_cta_banco ");
		    sbSQL.append("\n  Where id_banco = " + iBanco);
		    sbSQL.append("\n  And id_chequera = '" + sChequera + "'");
		    sbSQL.append("\n  And no_empresa = " + iEmpresa);
		    
		    bdSaldo = jdbcTemplate.queryForObject(sbSQL.toString(), BigDecimal.class);
		    saldo = bdSaldo.doubleValue();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarSaldoFinal");
		}
		return saldo;
	}
	
	/**
	 * FunSQLSelectChequesPorEntregar
	 */
	public double consultarChequesPorEntregar(int iEmpresa, int iBanco, String sDivisa, String sChequera){
		double suma = 0;
		BigDecimal bdSaldo = new BigDecimal(0);
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  Select coalesce(sum (importe),0) as suma ");
		    sbSQL.append("\n  From movimiento ");
		    sbSQL.append("\n  Where no_empresa=" + iEmpresa);
		    sbSQL.append("\n  and id_tipo_movto='E' and id_divisa='" + sDivisa + "' ");
		    sbSQL.append("\n  and id_chequera='" + sChequera + "' ");
		    sbSQL.append("\n  and id_banco=" + iBanco);
		    sbSQL.append("\n  and ((id_estatus_mov in('I','R') and b_entregado='N')");
		    sbSQL.append("\n  or (id_estatus_mov in ('P','J','T'))) ");
		    
		    bdSaldo = jdbcTemplate.queryForObject(sbSQL.toString(), BigDecimal.class);
		    suma = bdSaldo.doubleValue();
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarChequesPorEntregar");
		}
		return suma;
	}
	
	/**
	 * FunSQLSelectSaldoCredCoinvXChequera
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,String>> consultarSaldoCreditoCoinvPorChequera(int iCoinversora, int iEmpresa, String sDivisa){
		List<Map<String,String>>  saldos = new ArrayList<Map<String,String>> ();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n    Select");
			sbSQL.append("\n    (Select coalesce(sum(importe),0)");
		    sbSQL.append("\n    From saldo s");
		    sbSQL.append("\n    Where no_empresa = " + iEmpresa);
		    sbSQL.append("\n        and id_tipo_saldo in (91)");
		    sbSQL.append("\n        and no_linea = (Select id_divisa_soin");
		    sbSQL.append("\n                        From cat_divisa");
		    sbSQL.append("\n                        where id_divisa = '" + sDivisa + "')");
		    sbSQL.append("\n        and e.no_cuenta_emp = s.no_cuenta)");
		    sbSQL.append("\n     as Saldo_Cred,");
		    sbSQL.append("\n    (Select coalesce(sum(importe),0)");
		    sbSQL.append("\n    From saldo");
		    sbSQL.append("\n    Where no_empresa = " + iCoinversora);
		    sbSQL.append("\n        and id_tipo_saldo in (91)");
		    sbSQL.append("\n        and no_cuenta = " + iEmpresa);
		    sbSQL.append("\n        and no_linea = (Select id_divisa_soin");
		    sbSQL.append("\n                        From cat_divisa");
		    sbSQL.append("\n                        where id_divisa = '" + sDivisa + "'))");
		    sbSQL.append("\n    as Saldo_Coinv");
		    sbSQL.append("\n  From empresa e");
		    sbSQL.append("\n  Where ");
		    sbSQL.append("\n     no_empresa = " + iEmpresa);
		    
		    saldos = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public Map<String,String> mapRow(ResultSet rs, int idx) throws SQLException{
					HashMap resultado = new HashMap();
					resultado.put("saldoCredito", rs.getString("Saldo_Cred"));
					resultado.put("saldoCoinversion", rs.getString("Saldo_Coinv"));
				return resultado;
			}});
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarSaldoCreditoCoinvPorChequera");
		}
		return saldos;
	}
	
	/**
	 * FunSQLSelectDivisa
	 * @param iBanco
	 * @param sChequera
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<CatCtaBancoDto> consultaDivisa(int iBanco, String sChequera){
		List<CatCtaBancoDto> divisa = new ArrayList<CatCtaBancoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append(" Select id_divisa ");
		    sbSQL.append("\n  From cat_cta_banco ");
		    sbSQL.append("\n  Where id_banco = " + iBanco + " and id_chequera = '" + sChequera + "'");
		    
		    divisa = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public CatCtaBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
					CatCtaBancoDto cons = new CatCtaBancoDto();
					cons.setIdDivisa(rs.getString("id_divisa"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultaDivisa");
		}
		return divisa;
	}
	
	/**
	 * FunSQLComboBanco2
	 * @param iEmpresa
	 * @param sDivisa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarBancos2(int iEmpresa, String sDivisa){
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  SELECT DISTINCT cb.id_banco as ID, cb.desc_banco as Descrip ");
		    sbSQL.append("\n  FROM cat_cta_banco ccb ");
		    sbSQL.append("\n      LEFT JOIN cat_banco cb ON ( ");
		    sbSQL.append("\n          ccb.id_banco = cb.id_banco ");
		    sbSQL.append("\n          and ccb.no_empresa = " + iEmpresa + " ) ");
		    sbSQL.append("\n  WHERE id_divisa = '" + sDivisa + "' ");
		    sbSQL.append("\n      and tipo_chequera <> 'I' ");
		    sbSQL.append("\n      and cb.id_banco is not null ");
		      
		    list = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("Descrip"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarBancos2");
		}
		return list;
	}
	
	//consultas solicitud fondeo
	
	/**
	 * FunSQLComboEmpresasCuentas2
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarBancosSolFondeo2(int iEmpresa, String sDivisa){
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append(" Select distinct cb.id_banco as ID, cb.desc_banco as Descrip ");
		    sbSQL.append("\n  from cat_banco cb, cat_cta_banco ccb ");
		    sbSQL.append("\n  where cb.id_banco = ccb.id_banco ");
		    sbSQL.append("\n  and ccb.no_empresa = " + iEmpresa);
		    sbSQL.append("\n  and tipo_chequera in ('P','M','C','O') ");
		    sbSQL.append("\n  and id_divisa ='" + sDivisa + "' ");
		    sbSQL.append("\n      and cb.id_banco is not null ");
		      
		    list = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("Descrip"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarBancosSolFondeo2");
		}
		return list;
	}
	
	/**
	 * FunSQLComboChequera2
	 * @param bPagadora
	 * @param iBanco
	 * @param iNoEmpresa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboChequeraDto> consultarChequeras2(int iBanco, int iNoEmpresa){
		List<LlenaComboChequeraDto> list = new ArrayList<LlenaComboChequeraDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("Select id_chequera as ID, id_chequera as Descrip2");
		    sbSQL.append("\n  from cat_cta_banco ");
		    sbSQL.append("\n  Where ");
		    sbSQL.append("\n  id_banco = " + iBanco);
		    sbSQL.append("\n  and no_empresa =  " + iNoEmpresa);
		    sbSQL.append("\n  and tipo_chequera in ('P','M','C','O') ");
	        sbSQL.append("\n  Order by id_chequera");
	        
	        list = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
		    	public LlenaComboChequeraDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		LlenaComboChequeraDto cons = new LlenaComboChequeraDto();
			    		cons.setId(rs.getString("ID"));
			    		cons.setDescripcion(rs.getString("Descrip2"));
		    		return cons;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarChequeras2");
		}
		return list;
	}
	
	/**
	 * FunSQLSelectSaldoFinal2
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,String>> consultarSaldoFinal2(int iBanco, String sChequera, int iEmpresa, int iEmpInv, String sDivisa){
		List<Map<String,String>>  saldos = new ArrayList<Map<String,String>> ();
		StringBuffer sbSQL = new StringBuffer();
		try{
			if(ConstantesSet.gsDBM.equals("SYBASE"))
			{
			    sbSQL.append("\n  Select saldo_final , (Select coalesce(sum(importe),0)");
			    sbSQL.append("\n                   From saldo");
			    sbSQL.append("\n                   Where no_empresa = " + iEmpInv);
			    sbSQL.append("\n                   and id_tipo_saldo in (90,5) and no_cuenta=" + iEmpresa);
			    sbSQL.append("\n                   and convert(varchar,no_linea) = (Select id_divisa_soin");
			    sbSQL.append("\n                                  From cat_divisa");
			    sbSQL.append("\n                                  where id_divisa = '" + sDivisa + "')");
			    sbSQL.append("\n                   ) - (Select coalesce(importe,0)");
			    sbSQL.append("\n                        From saldo");
			    sbSQL.append("\n                        Where no_empresa =" + iEmpInv);
			    sbSQL.append("\n                             and id_tipo_saldo=7 and no_cuenta=" + iEmpresa);
			    sbSQL.append("\n                             and convert(varchar,no_linea) = (Select id_divisa_soin from cat_divisa where id_divisa='" + sDivisa + "')");
			    sbSQL.append("\n                       ) as Saldo_coinv");
			    sbSQL.append("\n  From cat_cta_banco");
			    sbSQL.append("\n  Where id_banco = " + iBanco);
			    sbSQL.append("\n        And id_chequera = '" + sChequera + "'");
			    sbSQL.append("\n        And no_empresa = " + iEmpresa);
			}
			else
			{
			    sbSQL.append("\n  Select saldo_final ");
			    sbSQL.append("\n , coalesce((Select coalesce(sum(importe),0) from saldo where no_empresa= " + iEmpInv);
			    sbSQL.append("\n  and id_tipo_saldo in (90,5) and no_cuenta=" + iEmpresa);
			    sbSQL.append("\n  and no_linea= (Select id_divisa_soin from cat_divisa where id_divisa = '" + sDivisa + "'");
			    sbSQL.append("\n )) - (Select coalesce(importe,0)  from saldo where no_empresa = " + iEmpInv);
			    sbSQL.append("\n  and id_tipo_saldo=7 and no_cuenta=" + iEmpresa);
			    sbSQL.append("\n  and no_linea=(Select id_divisa_soin from cat_divisa where id_divisa='" + sDivisa + "'");
			    sbSQL.append("\n )),0) as Saldo_coinv");
			    sbSQL.append("\n  From cat_cta_banco ");
			    sbSQL.append("\n  Where id_banco = " + iBanco);
			    sbSQL.append("\n  And id_chequera = '" + sChequera + "'");
			    sbSQL.append("\n  And no_empresa = " + iEmpresa);
			}
		    
			System.out.println("Query para el saldo de la chequera: " + sbSQL);
			
		    saldos = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public Map<String,String> mapRow(ResultSet rs, int idx) throws SQLException{
					HashMap resultado = new HashMap();
					resultado.put("saldoFinal", rs.getString("saldo_final"));
					resultado.put("saldoCoinversion", rs.getString("Saldo_coinv"));
				return resultado;
			}});
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarSaldoFinal2");
		}
		return saldos;
	}
	
	/**
	 * FunSQLInsertTraspaso
	 * @param dto
	 * @return
	 */
	public int insertarTraspaso(ParametroDto dto){
		int iInserta = -1;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append(" INSERT INTO parametro (no_empresa,no_folio_param,aplica,secuencia,id_tipo_operacion,");
		    sbSQL.append("\n  no_cuenta,id_estatus_mov,id_chequera,id_tipo_saldo,id_banco,importe, ");
		    sbSQL.append("\n  b_salvo_buen_cobro,fec_valor,fec_valor_original,id_estatus_reg,usuario_alta, ");
		    sbSQL.append("\n  fec_alta,id_divisa,id_forma_pago,importe_original,");
		    sbSQL.append("\n  fec_operacion,id_banco_benef, id_chequera_benef,origen_mov,concepto, ");
		    sbSQL.append("\n  id_caja,no_folio_mov,folio_ref,grupo,no_cliente, ");
		    sbSQL.append("\n  beneficiario) ");
		    
		    
	        sbSQL.append("\n  VALUES ( ");
	        sbSQL.append( dto.getNoEmpresa() + "," + dto.getNoFolioParam() + "," + dto.getAplica() + "," + dto.getSecuencia() + "," + dto.getIdTipoOperacion() + ",");
	        sbSQL.append( dto.getCuenta() + ",'" + dto.getIdEstatusMov() + "','" + dto.getIdChequera() +"',"+dto.getTipo_saldo()+","+ dto.getIdBanco() + "," + dto.getImporte() + ",");
	        sbSQL.append("\n '" + dto.getBSBC() + "','" + funciones.ponerFechaSola(dto.getFecValor()) + "','" + funciones.ponerFechaSola(dto.getFecValorOriginal()) + "','" + dto.getIdEstatusReg() + "'," + dto.getIdUsuario() + ",");
	        sbSQL.append("\n '" + funciones.ponerFechaSola(dto.getFecValorAlta()) + "','" + dto.getIdDivisa() + "'," + dto.getIdFormaPago() + "," + dto.getImporteOriginal() + ",");
	        sbSQL.append("\n '" + funciones.ponerFechaSola(dto.getFecOperacion()) + "'," + dto.getIdBancoBenef() + ",'" + dto.getIdChequeraBenef() + "','" + dto.getOrigenMov() + "','" + dto.getConcepto() + "',");
	        sbSQL.append( dto.getIdCaja() + "," + dto.getNoFolioMov() + "," + dto.getFolioRef() + "," + dto.getIdGrupo() + ",'" + dto.getNoCliente() + "',");
	        sbSQL.append("\n '" + dto.getBeneficiario() + "')");
		    
	        System.out.println("INSERT: "+ sbSQL.toString());
		    iInserta = jdbcTemplate.update(sbSQL.toString());
		    
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:insertarTraspaso");
		}
		return iInserta;
	}
	
	
	/**
	 * FunSQLSelect6997
	 * @param iEmpresa
	 * @param sDivisa
	 * @param sFechaAlta
	 * @param sFechaVenc
	 * @return
	 */
	public double consultarInteres (int iEmpresa, String sDivisa, Date dFechaAlta, Date dFechaVenc){
		StringBuffer sbSQL = new StringBuffer();
		double dInteres = 0;
		BigDecimal bdInteres = new BigDecimal(0);
		try{
			sbSQL.append(" select coalesce(sum(m.importe),0) as interes  ");
		    sbSQL.append("\n  From movimiento m, orden_inversion oi ");
		    sbSQL.append("\n  Where m.no_empresa = oi.no_empresa ");
		    sbSQL.append("\n  and m.no_docto=oi.no_orden ");
		    sbSQL.append("\n  and m.id_tipo_operacion = 4103 ");
		    sbSQL.append("\n  and m.no_empresa= " + iEmpresa);
		    
	        sbSQL.append("\n  and oi.fec_alta >='" + funciones.ponerFechaSola(dFechaAlta) + "'");
	        sbSQL.append("\n  and oi.fec_venc <='" + funciones.ponerFechaSola(dFechaVenc) + "'");
		    
		    sbSQL.append("\n  and id_divisa= '" + sDivisa + "'");
		    sbSQL.append("\n  and oi.id_estatus_ord<>'X' and m.id_estatus_mov<>'X'");
		    
		    bdInteres = jdbcTemplate.queryForObject(sbSQL.toString(), BigDecimal.class);
		    dInteres = bdInteres.doubleValue();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarInteres");
		}
		return dInteres;
	}
	
	/**
	 * FunSQLSelect6996
	 * @param iEmpresa
	 * @param sDivisa
	 * @param sFechaAlta
	 * @param sFechaVenc
	 * @return
	 */
	public double consultarInteres2 (int iEmpresa, String sDivisa, Date dFechaAlta, Date dFechaVenc){
		StringBuffer sbSQL = new StringBuffer();
		double dInteres = 0;
		BigDecimal bdInteres = new BigDecimal(0);
		try{
			sbSQL.append(" select coalesce(sum(m.importe),0) as interes  ");
		    sbSQL.append("\n  from hist_movimiento m, orden_inversion oi ");
		    sbSQL.append("\n  Where m.no_empresa = oi.no_empresa ");
		    sbSQL.append("\n  and m.no_docto=oi.no_orden ");
		    sbSQL.append("\n  and m.id_tipo_operacion = 4103 ");
		    sbSQL.append("\n  and m.no_empresa= " + iEmpresa);
		    
	        sbSQL.append("\n  and oi.fec_alta >='" + funciones.ponerFechaSola(dFechaAlta) + "'");
	        
	        sbSQL.append("\n  and oi.fec_venc <='" + funciones.ponerFechaSola(dFechaVenc) + "'");
		    
		    sbSQL.append("\n  and id_divisa= '" + sDivisa + "'");
		    sbSQL.append("\n  and oi.id_estatus_ord<>'X' and m.id_estatus_mov<>'X'");
		    
		    bdInteres = jdbcTemplate.queryForObject(sbSQL.toString(), BigDecimal.class);
		    dInteres = bdInteres.doubleValue();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarInteres2");
		}
		return dInteres;
	}
	
	/**
	 * FunSQLSelect6995
	 * @param iEmpresa
	 * @param sDivisa
	 * @param sFechaAlta
	 * @param sFechaVenc
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> consultarIntereses(int iEmpresa, String sDivisa, Date dFechaAlta, Date dFechaVenc){
		List<Map<String,Object>> listInt = new ArrayList<Map<String,Object>> ();
		StringBuffer sbSQL = new StringBuffer();
		try{
			if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
			{
			    sbSQL.append("\n  select interes,(convert(float,interes) / plazo) * " +
			                    " (datediff(day,case when oi.fec_alta > '" + funciones.ponerFechaSola(dFechaAlta) + "' ");
			    sbSQL.append("\n   then oi.fec_alta else '" + funciones.ponerFechaSola(dFechaAlta) + "' end, ");
			    sbSQL.append("\n   oi.fec_venc) + case when oi.fec_alta >= '" + funciones.ponerFechaSola(dFechaAlta) + "' then 0  else 1 end)+ " +
			                    " isnull(ajuste_int,0) as int_dev");
			    sbSQL.append("\n  from orden_inversion oi,cuenta c ");
			    sbSQL.append("\n  Where oi.no_cuenta = c.no_cuenta ");
			    sbSQL.append("\n  and oi.no_empresa=c.no_empresa ");
			    sbSQL.append("\n  and c.id_divisa='" + sDivisa + "'");
			    sbSQL.append("\n  and oi.no_empresa=" + iEmpresa + " and id_estatus_ord <> 'X' ");
			    sbSQL.append("\n  and oi.fec_alta < '" + funciones.ponerFechaSola(dFechaAlta) + "'");
			    sbSQL.append("\n  and oi.fec_venc between  '" + funciones.ponerFechaSola(dFechaAlta) + "'  and '" + funciones.ponerFechaSola(dFechaVenc) + "' ");
			}
			if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
			{
			    sbSQL.append("\n  select interes,(interes / plazo) * (INT4((case when oi.fec_alta > '" + funciones.ponerFechaSola(dFechaAlta) + "' ");
			    sbSQL.append("\n   then oi.fec_alta else '" + funciones.ponerFechaSola(dFechaAlta) + "' end) ");
			    sbSQL.append("\n   - oi.fec_venc) + case when oi.fec_alta >= '" + funciones.ponerFechaSola(dFechaAlta) + "' then 0  else 1 end)+ coalesce(ajuste_int,0) as int_dev");
			    sbSQL.append("\n  from orden_inversion oi,cuenta c ");
			    sbSQL.append("\n  Where oi.no_cuenta = c.no_cuenta ");
			    sbSQL.append("\n  and oi.no_empresa=c.no_empresa ");
			    sbSQL.append("\n  and c.id_divisa='" + sDivisa + "'");
			    sbSQL.append("\n  and oi.no_empresa=" + iEmpresa + " and id_estatus_ord <> 'X' ");
			    sbSQL.append("\n  and oi.fec_alta < '" + funciones.ponerFechaSola(dFechaAlta) + "'");
			    sbSQL.append("\n  and oi.fec_venc between  '" + funciones.ponerFechaSola(dFechaAlta) + "'  and '" + funciones.ponerFechaSola(dFechaVenc) + "' ");
			}
			if(ConstantesSet.gsDBM.equals("DB2"))
			{
			    sbSQL.append("\n  select interes,(interes / plazo) * ((case when oi.fec_alta > '" + funciones.ponerFechaSola(dFechaAlta) + "' ");
			    sbSQL.append("\n   then days(oi.fec_alta) else days('" + funciones.ponerFechaSola(dFechaAlta) + "') end) ");
			    sbSQL.append("\n   - days(oi.fec_venc) + case when oi.fec_alta >= '" + funciones.ponerFechaSola(dFechaAlta) + "' then 0  else 1 end)+ coalesce(ajuste_int,0) as int_dev");
			    sbSQL.append("\n  from orden_inversion oi,cuenta c ");
			    sbSQL.append("\n  Where oi.no_cuenta = c.no_cuenta ");
			    sbSQL.append("\n  and oi.no_empresa=c.no_empresa ");
			    sbSQL.append("\n  and c.id_divisa='" + sDivisa + "'");
			    sbSQL.append("\n  and oi.no_empresa=" + iEmpresa + " and id_estatus_ord <> 'X' ");
			    sbSQL.append("\n  and oi.fec_alta < '" + funciones.ponerFechaSola(dFechaAlta) + "'");
			    sbSQL.append("\n  and oi.fec_venc between  '" + funciones.ponerFechaSola(dFechaAlta) + "'  and '" + funciones.ponerFechaSola(dFechaVenc) + "' ");
			}
			
			listInt = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public Map<String,Object> mapRow(ResultSet rs, int idx) throws SQLException{
					HashMap resultado = new HashMap();
					resultado.put("interes", "interes");
					resultado.put("intDev", "int_dev");
				return resultado;
			}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarIntereses");
		}
		return listInt;
	}
	
	/**
	 * FunSQLSelect6994
	 * @param iEmpresa
	 * @param sDivisa
	 * @param dFechaIni
	 * @param dFechaFin
	 * @return
	 */
	public double consultarIsr(int iEmpresa, String sDivisa, Date dFechaIni, Date dFechaFin){
		StringBuffer sbSQL = new StringBuffer();
		double dIsr = 0;
		BigDecimal bdIsr = new BigDecimal(0);
		try{
			sbSQL.append("select Coalesce(sum(importe),0) as isr  from movimiento ");
		    sbSQL.append("\n  Where id_tipo_operacion = 4104 ");
		    sbSQL.append("\n  and no_empresa= " + iEmpresa);
		    
	        sbSQL.append("\n  and fec_valor between '" + funciones.ponerFechaSola(dFechaIni) + "' and '" + funciones.ponerFechaSola(dFechaFin) + "'");
		    
		    sbSQL.append("\n  and id_divisa= '" + sDivisa + "' and id_estatus_mov<>'X'");
		    
		    bdIsr = jdbcTemplate.queryForObject(sbSQL.toString(), BigDecimal.class);
		    dIsr = bdIsr.doubleValue();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarIsr");
		}
		return dIsr;
	}
	
	/**
	 * FunSQLSelect6993
	 * @param iEmpresa
	 * @param sDivisa
	 * @param dFechaIni
	 * @param dFechaFin
	 * @return
	 */
	public double consultarIsr2(int iEmpresa, String sDivisa, Date dFechaIni, Date dFechaFin){
		StringBuffer sbSQL = new StringBuffer();
		double dIsr = 0;
		BigDecimal bdIsr = new BigDecimal(0);
		try{
			sbSQL.append("select Coalesce(sum(importe),0) as isr  from hist_movimiento ");
		    sbSQL.append("\n  Where id_tipo_operacion = 4104 ");
		    sbSQL.append("\n  and no_empresa= " + iEmpresa);
		    
	        sbSQL.append("\n  and fec_valor between '" + funciones.ponerFechaSola(dFechaIni) + "' and '" + funciones.ponerFechaSola(dFechaFin) + "'");
		    
		    sbSQL.append("\n  and id_divisa= '" + sDivisa + "' and id_estatus_mov<>'X'");
		    
		    bdIsr = jdbcTemplate.queryForObject(sbSQL.toString(), BigDecimal.class);
		    dIsr = bdIsr.doubleValue();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarIsr2");
		}
		return dIsr;
	}
	
	/**
	 * FunSQLSelect6992
	 * @param iEmpresa
	 * @param sDivisa
	 * @param dFechaIni
	 * @param dFechaFin
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarInteresDevengado(int iEmpresa, String sDivisa, Date dFechaIni, Date dFechaFin){
		List<Map<String, Object>> listInteres = new ArrayList<Map<String, Object>>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
			{
	           sbSQL.append("\n  select (datediff(day, '" + funciones.ponerFechaSola(dFechaIni) + "' ,'" + funciones.ponerFechaSola(dFechaFin) + "')+1),");
	           sbSQL.append("\n  oi.no_orden,oi.plazo, oi.fec_venc,oi.fec_alta, convert(float,interes) as interes, ");
	           sbSQL.append("\n  (convert(float,interes)/ plazo) * (datediff(day,'" + funciones.ponerFechaSola(dFechaIni) + "' , '" + funciones.ponerFechaSola(dFechaFin) + "')+1) as int_dev, ");
	           sbSQL.append("\n  (convert(float,isr)/ plazo) * (datediff(day,'" + funciones.ponerFechaSola(dFechaIni) + "','" + funciones.ponerFechaSola(dFechaFin) + "')+1)  as isr_dev,plazo ");
	           sbSQL.append("\n  from orden_inversion oi,cuenta c ");
	           sbSQL.append("\n  Where oi.no_cuenta = c.no_cuenta ");
	           sbSQL.append("\n  and oi.no_empresa=c.no_empresa ");
	           sbSQL.append("\n  and c.id_divisa='" + sDivisa + "'");
	           sbSQL.append("\n  and oi.no_empresa=" + iEmpresa);
	           sbSQL.append("\n  and id_estatus_ord <> 'X' ");
	           sbSQL.append("\n  and oi.fec_alta < '" + funciones.ponerFechaSola(dFechaIni) + "'");
	           sbSQL.append("\n  and oi.fec_venc > '" + funciones.ponerFechaSola(dFechaFin) + "'");
	
	            
	           sbSQL.append("\n  union all ");      
	           sbSQL.append("\n  select (datediff(day,case when oi.fec_alta >= '" + funciones.ponerFechaSola(dFechaFin) + "' then '" + funciones.ponerFechaSola(dFechaFin) + "'  else oi. fec_alta end,");
	           sbSQL.append("\n '" + funciones.ponerFechaSola(dFechaFin) + " ')+ case when oi.fec_alta < '" + funciones.ponerFechaSola(dFechaFin) + "' then 0  else 1 end),");
	           sbSQL.append("\n  oi.no_orden,oi.plazo, oi.fec_venc,oi.fec_alta, interes, ");
	           sbSQL.append("\n  (interes/ plazo) * (datediff(day,case when oi.fec_alta >= '" + funciones.ponerFechaSola(dFechaFin) + "' then '" + funciones.ponerFechaSola(dFechaFin) + "'  else oi. fec_alta end,");
	           sbSQL.append("\n '" + funciones.ponerFechaSola(dFechaFin) + "')+ case when oi.fec_alta < '" + funciones.ponerFechaSola(dFechaFin) + "' then 0  else 1 end) as int_dev, ");
	           sbSQL.append("\n  (isr / plazo) * (datediff(day,case when oi.fec_alta >= '" + funciones.ponerFechaSola(dFechaFin) + "' then '" + funciones.ponerFechaSola(dFechaFin) + "'  else oi. fec_alta end,");
	           sbSQL.append("\n '" + funciones.ponerFechaSola(dFechaFin) + "')+ case when oi.fec_alta < '" + funciones.ponerFechaSola(dFechaFin) + "' then 0  else 1 end)  as isr_dev,plazo ");
	           sbSQL.append("\n  from orden_inversion oi,cuenta c ");
	           sbSQL.append("\n  Where oi.no_cuenta = c.no_cuenta ");
	           sbSQL.append("\n  and oi.no_empresa=c.no_empresa ");
	           sbSQL.append("\n  and c.id_divisa='" + sDivisa + "'");
	           sbSQL.append("\n  and oi.no_empresa=" + iEmpresa + " and id_estatus_ord <> 'X' ");
	           sbSQL.append("\n  and oi.fec_alta >=  '" + funciones.ponerFechaSola(dFechaIni) + "'  and  oi.fec_alta <'" + funciones.ponerFechaSola(dFechaFin) + "' ");
	           sbSQL.append("\n  and oi.fec_venc> '" + funciones.ponerFechaSola(dFechaFin) + "' ");
			}
			
			if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
			{
	           sbSQL.append("\n  SELECT (DATE_PART('days',TIMESTAMP '" + funciones.ponerFechaSola(dFechaIni) + "' - TIMESTAMP '" + funciones.ponerFechaSola(dFechaFin) + "')+1),");
	           sbSQL.append("\n  oi.no_orden,oi.plazo, oi.fec_venc,oi.fec_alta,interes as interes, ");
	           sbSQL.append("\n  (interes/ plazo) * (DATE_PART('days',TIMESTAMP '" + funciones.ponerFechaSola(dFechaIni) + "' - TIMESTAMP '" + funciones.ponerFechaSola(dFechaFin) + "') + 1) as int_dev, ");
	           sbSQL.append("\n  (isr/ plazo) * (DATE_PART('days',TIMESTAMP '" + funciones.ponerFechaSola(dFechaIni) + "' - TIMESTAMP '" + funciones.ponerFechaSola(dFechaFin) + "') + 1)  as isr_dev,plazo ");
	           sbSQL.append("\n  FROM orden_inversion oi,cuenta c ");
	           sbSQL.append("\n  WHERE oi.no_cuenta = c.no_cuenta ");
	           sbSQL.append("\n  and oi.no_empresa=c.no_empresa ");
	           sbSQL.append("\n  and c.id_divisa='" + sDivisa + "'");
	           sbSQL.append("\n  and oi.no_empresa=" + iEmpresa);
	           sbSQL.append("\n  and id_estatus_ord <> 'X' ");
	           sbSQL.append("\n  and oi.fec_alta >= '" + funciones.ponerFechaSola(dFechaIni) + "'");
	           sbSQL.append("\n  and oi.fec_venc < '" + funciones.ponerFechaSola(dFechaFin) + "'");
	           sbSQL.append("\n  and oi.no_orden not in (1743,1682,1778) ");
	            
	           sbSQL.append("\n  union all ");
	           sbSQL.append("\n  SELECT (DATE_PART('days',(case when oi.fec_alta >= '" + funciones.ponerFechaSola(dFechaFin) + "' then TIMESTAMP '" + funciones.ponerFechaSola(dFechaFin) + "'  else oi. fec_alta end) ");
	           sbSQL.append("\n  - TIMESTAMP '" + funciones.ponerFechaSola(dFechaFin) + " ') + case when oi.fec_alta < '" + funciones.ponerFechaSola(dFechaFin) + "' then 0  else 1 end),");
	           sbSQL.append("\n  oi.no_orden,oi.plazo, oi.fec_venc,oi.fec_alta, interes, ");
	           sbSQL.append("\n  (interes/ plazo) * (DATE_PART('days',(case when oi.fec_alta >= '" + funciones.ponerFechaSola(dFechaFin) + "' then TIMESTAMP '" + funciones.ponerFechaSola(dFechaFin) + "'  else  oi. fec_alta end) ");
	           sbSQL.append("\n  - TIMESTAMP '" + funciones.ponerFechaSola(dFechaFin) + "') + case when oi.fec_alta < '" + funciones.ponerFechaSola(dFechaFin) + "' then 0  else 1 end) as int_dev, ");
	           sbSQL.append("\n  (isr/ plazo) * (DATE_PART('days',(case when oi.fec_alta >= '" + funciones.ponerFechaSola(dFechaFin) + "' then TIMESTAMP '" + funciones.ponerFechaSola(dFechaFin) + "'  else oi. fec_alta end) ");
	           sbSQL.append("\n  - TIMESTAMP '" + funciones.ponerFechaSola(dFechaFin) + "') + case when oi.fec_alta < '" + funciones.ponerFechaSola(dFechaFin) + "' then 0  else 1 end)  ");
	           sbSQL.append("\n  as isr_dev,plazo ");
	           sbSQL.append("\n  FROM orden_inversion oi,cuenta c ");
	           sbSQL.append("\n  WHERE oi.no_cuenta = c.no_cuenta ");
	           sbSQL.append("\n  and oi.no_empresa=c.no_empresa ");
	           sbSQL.append("\n  and c.id_divisa='" + sDivisa + "'");
	           sbSQL.append("\n  and oi.no_empresa=" + iEmpresa + " and id_estatus_ord <> 'X' ");
	           sbSQL.append("\n  and oi.fec_alta >=  '" + funciones.ponerFechaSola(dFechaIni) + "'  and  oi.fec_alta <'" + funciones.ponerFechaSola(dFechaFin) + "' ");
	           sbSQL.append("\n  and oi.fec_venc> '" + funciones.ponerFechaSola(dFechaFin) + "' ");
			}
            
			if(ConstantesSet.gsDBM.equals("DB2"))
			{
	           sbSQL.append("\n  SELECT (DAYS('" + funciones.ponerFechaSola(dFechaIni) + "') - DAYS('" + funciones.ponerFechaSola(dFechaFin) + "')+1),");
	           sbSQL.append("\n  oi.no_orden,oi.plazo, oi.fec_venc,oi.fec_alta,interes as interes, ");
	           sbSQL.append("\n  (interes/ plazo) * (DAYS('" + funciones.ponerFechaSola(dFechaIni) + "') - DAYS('" + funciones.ponerFechaSola(dFechaFin) + "') + 1) as int_dev, ");
	           sbSQL.append("\n  (isr/ plazo) * (DAYS('" + funciones.ponerFechaSola(dFechaIni) + "') - DAYS('" + funciones.ponerFechaSola(dFechaFin) + "') + 1)  as isr_dev,plazo ");
	           sbSQL.append("\n  FROM orden_inversion oi,cuenta c ");
	           sbSQL.append("\n  WHERE oi.no_cuenta = c.no_cuenta ");
	           sbSQL.append("\n  and oi.no_empresa=c.no_empresa ");
	           sbSQL.append("\n  and c.id_divisa='" + sDivisa + "'");
	           sbSQL.append("\n  and oi.no_empresa=" + iEmpresa);
	           sbSQL.append("\n  and id_estatus_ord <> 'X' ");
	           sbSQL.append("\n  and oi.fec_alta >= '" + funciones.ponerFechaSola(dFechaIni) + "'");
	           sbSQL.append("\n  and oi.fec_venc < '" + funciones.ponerFechaSola(dFechaFin) + "'");
	           sbSQL.append("\n  and oi.no_orden not in (1743,1682,1778) ");
	            
	           sbSQL.append("\n  union all ");
	           sbSQL.append("\n  SELECT (case when oi.fec_alta >= '" + funciones.ponerFechaSola(dFechaFin) + "' then DAYS('" + funciones.ponerFechaSola(dFechaFin) + "')  else DAYS(oi.fec_alta) end ");
	           sbSQL.append("\n  - DAYS('" + funciones.ponerFechaSola(dFechaFin) + " ') + case when oi.fec_alta < '" + funciones.ponerFechaSola(dFechaFin) + "' then 0  else 1 end),");
	           sbSQL.append("\n  oi.no_orden,oi.plazo, oi.fec_venc,oi.fec_alta, interes, ");
	           sbSQL.append("\n  (interes/ plazo) * (case when oi.fec_alta >= '" + funciones.ponerFechaSola(dFechaFin) + "' then DAYS('" + funciones.ponerFechaSola(dFechaFin) + "')  else  DAYS(oi.fec_alta) end ");
	           sbSQL.append("\n  - DAYS('" + funciones.ponerFechaSola(dFechaFin) + "') + case when oi.fec_alta < '" + funciones.ponerFechaSola(dFechaFin) + "' then 0  else 1 end) as int_dev, ");
	           sbSQL.append("\n  (isr/ plazo) * (case when oi.fec_alta >= '" + funciones.ponerFechaSola(dFechaFin) + "' then DAYS('" + funciones.ponerFechaSola(dFechaFin) + "')  else DAYS(oi.fec_alta) end ");
	           sbSQL.append("\n  - DAYS('" + funciones.ponerFechaSola(dFechaFin) + "') + case when oi.fec_alta < '" + funciones.ponerFechaSola(dFechaFin) + "' then 0  else 1 end)  ");
	           sbSQL.append("\n  as isr_dev,plazo ");
	           sbSQL.append("\n  FROM orden_inversion oi,cuenta c ");
	           sbSQL.append("\n  WHERE oi.no_cuenta = c.no_cuenta ");
	           sbSQL.append("\n  and oi.no_empresa=c.no_empresa ");
	           sbSQL.append("\n  and c.id_divisa='" + sDivisa + "'");
	           sbSQL.append("\n  and oi.no_empresa=" + iEmpresa + " and id_estatus_ord <> 'X' ");
	           sbSQL.append("\n  and oi.fec_alta >=  '" + funciones.ponerFechaSola(dFechaIni) + "'  and  oi.fec_alta <'" + funciones.ponerFechaSola(dFechaFin) + "' ");
	           sbSQL.append("\n  and oi.fec_venc> '" + funciones.ponerFechaSola(dFechaFin) + "' ");
			}

			listInteres = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public Map<String,Object> mapRow(ResultSet rs, int idx) throws SQLException{
					HashMap resultado = new HashMap();
					resultado.put("noOrden", "no_orden");
					resultado.put("plazo", "plazo");
					resultado.put("fecVenc", "fec_venc");
					resultado.put("fecAlta", "fec_alta");
					resultado.put("interes", "interes");
					resultado.put("intDev", "int_dev");
					resultado.put("isrDev", "isr_dev");
				return resultado;
			}});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarInteresDevengado");
		}
		return listInteres;
	}
	
	/**
	 * FunSQLValoresSdoPromyCapitalizacion
	 * @param iEmpresa
	 * @param sDivisa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<InteresesDto> consultarValoresSdoPromyCapitalazacion(int iEmpresa, String sDivisa){
		List<InteresesDto> listValores = new ArrayList<InteresesDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  SELECT coalesce(porcentaje_iva, 0) as porcentaje_iva, ");
		    sbSQL.append("\n      coalesce(porcentaje_isr, 0) as porcentaje_isr, ");
		    sbSQL.append("\n      coalesce(porcentaje_comision, 0) as porcentaje_comision ");
		    sbSQL.append("\n  	  ,no_empresa ");
		    sbSQL.append("\n  FROM valor_empresa ");
		    sbSQL.append("\n  WHERE no_empresa = " + iEmpresa);
		    sbSQL.append("\n      and id_divisa = '" + sDivisa + "'");
		    
		    listValores = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public InteresesDto mapRow(ResultSet rs, int idx) throws SQLException{
					InteresesDto resultado = new InteresesDto();
					resultado.setIva(rs.getDouble("porcentaje_iva"));
					resultado.setIsr(rs.getDouble("porcentaje_isr"));
					resultado.setComision(rs.getDouble("porcentaje_comision"));
					resultado.setNoEmpresa(rs.getInt("no_empresa"));
				return resultado;
			}});

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarValoresSdoPromyCapitalazacion");
		}
		return listValores;
	}
	
	/**
	 * FunSQLObtenISR
	 * @return
	 */
	public double consultarTasaIsr(){
		StringBuffer sbSQL = new StringBuffer();
		double dIsr = 0;
		BigDecimal bdIsr = new BigDecimal(0);
		try{
			sbSQL.append("select coalesce(tasa_isr, 0) as tasa_isr from retencion");
		    
		    bdIsr = jdbcTemplate.queryForObject(sbSQL.toString(), BigDecimal.class);
	    	dIsr = bdIsr.doubleValue();
		}catch(Exception e){
			dIsr = 0;
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarTasaIsr");
		}
		return dIsr;
	}
	
	/**
	 * FunSQLTasasInversion
	 * @param iAnio
	 * @param sMes
	 * @param sDivisa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarTasasInversion(int iAnio, String sMes, String sDivisa){
		List<Map<String, Object>> listRet = new ArrayList<Map<String, Object>>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			if(ConstantesSet.gsDBM.equals("DB2"))
			{
			    sbSQL.append("\n  SELECT CASE WHEN (MONTH(oi.fec_alta) > MONTH(fec_venc) AND MONTHNAME(fec_venc) = '" + sMes + "') OR (MONTH(oi.fec_alta) < MONTH(fec_venc) AND MONTHNAME(fec_venc) = '" + sMes + "')");
			    sbSQL.append("\n         THEN CAST((CAST(YEAR(fec_venc) AS VARCHAR(4)) || '-' || CAST(MONTH(fec_venc) AS VARCHAR(2)) || '-01') AS DATE) ELSE oi.fec_alta END AS fec_alta,");
			    sbSQL.append("\n         CASE WHEN (MONTH(oi.fec_alta) > MONTH(fec_venc) AND MONTHNAME(oi.fec_alta) = '" + sMes + "') OR (MONTH(oi.fec_alta) < MONTH(fec_venc) AND MONTHNAME(oi.fec_alta) = '" + sMes + "')");
			    sbSQL.append("\n         THEN CAST((CAST(YEAR(fec_venc) AS VARCHAR(4)) || '-' || CAST(MONTH(fec_venc) AS VARCHAR(2)) || '-01') AS DATE) ELSE fec_venc END AS fec_venc,");
			    sbSQL.append("\n         plazo, tasa,");
			    sbSQL.append("\n         CASE WHEN (MONTH(oi.fec_alta) < MONTH(fec_venc) AND DAY(fec_venc) > 1) THEN DAYS(oi.fec_alta)-DAYS( fec_venc) - DAY(fec_venc) + 1 ELSE");
			    sbSQL.append("\n         CASE WHEN (MONTH(oi.fec_alta) < MONTH(fec_venc) AND YEAR(oi.fec_alta) = YEAR(fec_venc))");
			    sbSQL.append("\n         THEN (DAY((oi.fec_alta + 1 MONTHS) - DAY(oi.fec_alta + 1 MONTHS) DAYS ) - DAY(oi.fec_alta)) + 1");
			    sbSQL.append("\n         ELSE CASE WHEN (MONTH(oi.fec_alta) > MONTH(fec_venc) AND YEAR(oi.fec_alta) < YEAR(fec_venc)) THEN");
			    sbSQL.append("\n              (DAY((oi.fec_alta + 1 MONTHS) - DAY(oi.fec_alta + 1 MONTHS) DAYS ) - DAY(oi.fec_alta)) + 1 ELSE plazo END END END AS plazo2");
			    sbSQL.append("\n  FROM orden_inversion oi, cuenta c WHERE oi.no_cuenta = c.no_cuenta");
			    sbSQL.append("\n       AND ((MONTHNAME(oi.fec_alta) = '" + sMes + "' AND YEAR(oi.fec_alta) = " + iAnio + ")");
			    sbSQL.append("\n            OR (MONTHNAME(fec_venc) = '" + sMes + "' AND YEAR(oi.fec_alta) = " + iAnio + " AND MONTH(oi.fec_alta) < MONTH(fec_venc) AND DAY(fec_venc) > 1)");
			    sbSQL.append("\n       OR (MONTHNAME(m,fec_venc) = '" + sMes + "' AND YEAR(oi.fec_alta) + 1 = " + iAnio + " AND MONTH(oi.fec_alta) > MONTH(fec_venc)))");
			    sbSQL.append("\n       AND c.id_divisa = '" + sDivisa + "'");
			    sbSQL.append("\n       AND id_estatus_ord <> 'X'");
			    sbSQL.append("\n       GROUP BY oi.fec_alta, fec_venc, plazo, tasa");
			    sbSQL.append("\n       ORDER BY oi.fec_venc");
			}
		    else
		    {
			    sbSQL.append("\n  SELECT CASE WHEN (MONTH(oi.fec_alta) > MONTH(fec_venc) AND DATENAME(m,fec_venc) = '" + sMes + "') OR (MONTH(oi.fec_alta) < MONTH(fec_venc) AND DATENAME(m,fec_venc) = '" + sMes + "')");
			    sbSQL.append("\n         THEN CONVERT(DATETIME,'01/' + CONVERT(VARCHAR,MONTH(fec_venc)) + '/' + CONVERT(VARCHAR,YEAR(fec_venc))) ELSE oi.fec_alta END AS fec_alta,");
			    sbSQL.append("\n         CASE WHEN (MONTH(oi.fec_alta) > MONTH(fec_venc) AND DATENAME(m,oi.fec_alta) = '" + sMes + "') OR (MONTH(oi.fec_alta) < MONTH(fec_venc) AND DATENAME(m,oi.fec_alta) = '" + sMes + "')");
			    sbSQL.append("\n         THEN CONVERT(DATETIME, '01/' + CONVERT(VARCHAR,MONTH(fec_venc)) + '/' + CONVERT(VARCHAR,YEAR(fec_venc))) ELSE fec_venc END AS fec_venc,");
			    sbSQL.append("\n         plazo, tasa,");
			    sbSQL.append("\n         CASE WHEN (MONTH(oi.fec_alta) < MONTH(fec_venc) AND DAY(fec_venc) > 1) THEN DATEDIFF(DAY, oi.fec_alta, fec_venc) - DAY(fec_venc) + 1 ELSE");
			    sbSQL.append("\n         CASE WHEN (MONTH(oi.fec_alta) < MONTH(fec_venc) AND YEAR(oi.fec_alta) = YEAR(fec_venc))");
			    sbSQL.append("\n         THEN (DAY(DATEADD(d, -DAY(DATEADD(m,1,oi.fec_alta)),DATEADD(m,1,oi.fec_alta))) - DAY(oi.fec_alta)) + 1");
			    sbSQL.append("\n         ELSE CASE WHEN (MONTH(oi.fec_alta) > MONTH(fec_venc) AND YEAR(oi.fec_alta) < YEAR(fec_venc)) THEN");
			    sbSQL.append("\n         (DAY(DATEADD(d, -DAY(DATEADD(m,1,oi.fec_alta)),DATEADD(m,1,oi.fec_alta))) - DAY(oi.fec_alta)) + 1 ELSE plazo END END END AS plazo2");
			    sbSQL.append("\n  FROM orden_inversion oi, cuenta c WHERE oi.no_cuenta = c.no_cuenta");
			    sbSQL.append("\n       AND ((DATENAME(m,oi.fec_alta) = '" + sMes + "' AND YEAR(oi.fec_alta) = " + iAnio + ")");
			    sbSQL.append("\n            OR (DATENAME(m,fec_venc) = '" + sMes + "' AND YEAR(oi.fec_alta) = " + iAnio + " AND MONTH(oi.fec_alta) < MONTH(fec_venc) AND DAY(fec_venc) > 1)");
			    sbSQL.append("\n       OR (DATENAME(m,fec_venc) = '" + sMes + "' AND YEAR(oi.fec_alta) + 1 = " + iAnio + " AND MONTH(oi.fec_alta) > MONTH(fec_venc)))");
			    sbSQL.append("\n       AND c.id_divisa = '" + sDivisa + "'");
			    sbSQL.append("\n       AND id_estatus_ord <> 'X'");
			    sbSQL.append("\n       GROUP BY oi.fec_alta, fec_venc, plazo, tasa");
			    sbSQL.append("\n       ORDER BY oi.fec_venc");
			    
			    listRet = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
					public Map<String,Object> mapRow(ResultSet rs, int idx) throws SQLException{
						HashMap resultado = new HashMap();
						resultado.put("fec_alta", rs.getDate("fec_alta"));
						resultado.put("fec_venc", rs.getDate("fec_venc"));
						resultado.put("plazo", rs.getInt("plazo"));
						resultado.put("tasa", rs.getDouble("tasa"));
						resultado.put("plazo2", rs.getInt("plazo2"));
					return resultado;
				}});
		    }
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarTasasInversion");
		}
		return listRet;
	}
	
	/**
	 * FunSQLDeleteTmpSdoProm
	 * @param iUsuario
	 * @return
	 */
	public int eliminarTmpSdoProm(int iUsuario){
		StringBuffer sbSQL = new StringBuffer();
		int iDelete = 0;
		try{
			if(ConstantesSet.gsDBM.equals("DB2") || ConstantesSet.gsDBM.equals("POSTGRESQL"))
		        sbSQL.append("Delete from tmp_sdoprom Where usuario_alta=" + iUsuario);
		    else
		        sbSQL.append("Delete tmp_sdoprom Where usuario_alta=" + iUsuario);
			iDelete = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:eliminarTmpSdoProm");
		}
		return iDelete;
	}
	
	/**
	 * FunSQLInsertTmpSdoProm
	 * @return
	 */
	public int insertarTmpSdoProm(int iUsuario, int iEmpresa, int iDias, int iLinea, Date dFecha1, Date dFecha2){
		StringBuffer sbSQL = new StringBuffer();
		int iInsert = 0;
		try{
			sbSQL.append(" Insert into tmp_sdoprom select a.no_cuenta," + iUsuario);
		    sbSQL.append("\n ,e.nom_empresa ,round(coalesce(sum(importe),0)/" + iDias + ",2) as saldo_promedio,0,0,0, NULL, NULL, 0, 0, 0 ");
		    sbSQL.append("\n  from hist_saldo a,empresa e");
		    sbSQL.append("\n  Where a.id_tipo_saldo = 91 ");
		    sbSQL.append("\n  and a.no_linea=" + iLinea);
		    sbSQL.append("\n  and a.no_cuenta=e.no_empresa ");
		    
	        sbSQL.append("\n  and a.fec_valor between '" + funciones.ponerFechaSola(dFecha1)+ "' and '" + funciones.ponerFechaSola(dFecha2) + "'");
		    
		    sbSQL.append("\n  and a.no_empresa=" + iEmpresa + " and a.no_cuenta in ");
		    sbSQL.append("\n  (select no_cuenta from cuenta l where l.id_tipo_cuenta='T' and l.no_empresa=" + iEmpresa);
		    sbSQL.append("\n  and  no_cuenta <> no_empresa and no_linea=" + iLinea + ")");
		    sbSQL.append("\n  and no_cuenta not in ( ");
		    sbSQL.append("\n     select no_empresa from  empresa where no_empresa in ( ");
		    sbSQL.append("\n      select setemp from set006 where soiemp in (");
		    sbSQL.append("\n          select soiemp from set006 where setemp in (");
		    sbSQL.append("\n              select no_empresa from empresa where b_concentradora = 'S')");
		    sbSQL.append("\n      and siscod = 'CO') and siscod = 'CP')");
		    sbSQL.append("\n      )");
		    sbSQL.append("\n  group by a.no_empresa,a.no_cuenta,e.nom_empresa");
		    
		    iInsert = jdbcTemplate.update(sbSQL.toString());

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:insertarTmpSdoProm");
		}
		return iInsert;
	}
	
	/**
	 * FunSQLSelectTmpSdoProm
	 * FunSQLSelectTmpSdoPromT
	 * @param iUsuario
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<TmpSdoPromDto> consultarTmpSdoProm(int iUsuario){
		List<TmpSdoPromDto> listSdo = new ArrayList<TmpSdoPromDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append(" select * from tmp_sdoprom where usuario_alta=" + iUsuario);
			
			listSdo = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public TmpSdoPromDto mapRow(ResultSet rs, int idx) throws SQLException{
					TmpSdoPromDto resultado = new TmpSdoPromDto();
					resultado.setNoEmpresa(rs.getInt("no_empresa"));
					resultado.setIdUsuario(rs.getInt("usuario_alta"));
					resultado.setDescEmpresa(rs.getString("desc_empresa"));
					resultado.setSaldoProm(rs.getDouble("saldo_prom"));
					resultado.setInteres(rs.getDouble("interes"));
					resultado.setIsr(rs.getDouble("isr"));
					resultado.setComision(rs.getDouble("comision"));
					resultado.setIdBanco(rs.getInt("id_banco"));
					resultado.setIdChequera(rs.getString("id_chequera"));
					resultado.setIntDev(rs.getDouble("int_dev"));
					resultado.setIsrDev(rs.getDouble("isr_dev"));
					resultado.setIva(rs.getDouble("iva"));
				return resultado;
			}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarTmpSdoProm");
		}
		return listSdo;
	}
	
	/**
	 * FunSQLUpdateTmpSdoProm4
	 * @param dto
	 * @param iUsuario
	 * @return
	 */
	public int actualizarTmpSdoProm(InteresesDto dto, int iUsuario){
		int iUpdate = 0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  UPDATE tmp_sdoprom ");
		    sbSQL.append("\n  SET int_dev = " + dto.getInteresDev());
		    sbSQL.append("\n      ,isr_dev = " + dto.getIsrDev());
		    sbSQL.append("\n      ,comision = " + dto.getComision());
		    sbSQL.append("\n      ,iva = " + dto.getIva());
		    sbSQL.append("\n      ,interes = " + dto.getInteres());
		    sbSQL.append("\n      ,isr = " + dto.getIsr());
		    sbSQL.append("\n  WHERE usuario_alta = " + iUsuario);
		    sbSQL.append("\n      and no_empresa = " + dto.getNoEmpresa());
		    
		    iUpdate = jdbcTemplate.update(sbSQL.toString());
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:actualizarTmpSdoProm");
		}
		return iUpdate;
	}
	
	/**
	 * FunSQLSelect6998
	 * @param iEmpresa
	 * @param sDivisa
	 * @param dFecha1
	 * @param dFecha2
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<VencimientoInversionDto> consultarVencInversion(int iEmpresa, String sDivisa, Date dFecha1,Date dFecha2){
		List<VencimientoInversionDto> list = new ArrayList<VencimientoInversionDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("  Select * From vencimiento_inversion ");
	        sbSQL.append("\n  Where (fec_desde between '" + funciones.ponerFechaSola(dFecha1) + "' and '" + funciones.ponerFechaSola(dFecha2) + "'");
	        sbSQL.append("\n  or fec_hasta between '" + funciones.ponerFechaSola(dFecha1) + "' and '" + funciones.ponerFechaSola(dFecha2) + " ')");
	        sbSQL.append("\n  and no_empresa in( select no_cuenta from cuenta l where l.id_tipo_cuenta='T' ");
	        sbSQL.append("\n  and l.no_empresa=" + iEmpresa + " ) ");
	        sbSQL.append("\n  and id_divisa='" + sDivisa + "'");
	        
	        list = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public VencimientoInversionDto mapRow(ResultSet rs, int idx) throws SQLException{
					VencimientoInversionDto resultado = new VencimientoInversionDto();
					resultado.setNoEmpresa(rs.getInt("no_empresa"));
					resultado.setFecDesde(rs.getDate("fec_desde"));
					resultado.setFecHasta(rs.getDate("fec_hasta"));
					resultado.setIdDivisa(rs.getString("id_divisa"));
					resultado.setNoControladora(rs.getInt("no_controladora"));
					resultado.setInteres(rs.getDouble("interes"));
					resultado.setIsr(rs.getDouble("isr"));
					resultado.setComision(rs.getDouble("comision"));
					resultado.setTasaRend(rs.getDouble("tasa_rend"));
					resultado.setInteresDev(rs.getDouble("interes_dev"));
					resultado.setSaldoPromedio(rs.getDouble("saldo_promedio"));
					resultado.setBComisionCap(rs.getString("b_comision_cap"));
					resultado.setIva(rs.getDouble("iva"));
				return resultado;
			}});
	        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarVencInversion");
		}
		return list;
	}
	
	/**
	 * FunSQLSelectChequeraTraspCoinv
	 */
	@SuppressWarnings("unchecked")
	public List<CatCtaBancoDto> consultarChequerasTraspCoinv(int iEmpresa, String sDivisa){
		List<CatCtaBancoDto> list = new ArrayList<CatCtaBancoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  SELECT id_chequera, id_banco ");
		    sbSQL.append("\n  FROM cat_cta_banco ");
		    sbSQL.append("\n  WHERE no_empresa = " + iEmpresa);
		    sbSQL.append("\n      and tipo_chequera in ('P', 'M') ");
		    sbSQL.append("\n      and id_divisa = '" + sDivisa + "' ");
		    sbSQL.append("\n      and b_traspaso = 'S' ");
		    
		    list = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public CatCtaBancoDto mapRow(ResultSet rs, int idx) throws SQLException{
					CatCtaBancoDto resultado = new CatCtaBancoDto();
					resultado.setIdChequera(rs.getString("id_chequera"));
					resultado.setIdBanco(rs.getInt("id_banco"));
				return resultado;
			}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarChequerasTraspCoinv");
		}
		return list;
	}
	
	/**
	 * FunSQLIntCapit
	 * @param dto
	 * @return
	 */
	public int insertaCapitalizacion(ParametroDto dto){
		int iInsert = 0;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  INSERT INTO parametro (no_folio_param, no_empresa, id_banco, ");
		    sbSQL.append("\n      id_chequera, id_tipo_operacion, id_forma_pago, dias_inv, ");
		    sbSQL.append("\n      usuario_alta, id_tipo_docto, no_docto, grupo, no_folio_mov, ");
		    sbSQL.append("\n      folio_ref, fec_valor, fec_valor_original, no_cuenta, ");
		    sbSQL.append("\n      fec_operacion, fec_alta, importe, valor_tasa, ");
		    sbSQL.append("\n      importe_original, tipo_cambio, id_caja, id_divisa, ");
		    sbSQL.append("\n      aplica, id_estatus_mov, b_salvo_buen_cobro, id_estatus_reg, ");
		    
		    if(dto.getIdBancoBenef() != 0)
		        sbSQL.append("\n id_banco_benef, ");
		    
		    if(!dto.getBeneficiario().equals(""))
		        sbSQL.append("\n id_chequera_benef, ");
		    
		    if(dto.getSecuencia() != 0)
		        sbSQL.append("\n secuencia, ");
		    
		    if(!dto.getConcepto().equals(""))
		        sbSQL.append("\n concepto, ");
		        
		    sbSQL.append("\n origen_mov ) ");
		    
		    sbSQL.append("\n Values (");
		    
		    sbSQL.append("\n" +  dto.getNoFolioParam());
		    sbSQL.append("\n , " + dto.getNoEmpresa());
		    sbSQL.append("\n , 0");
		    sbSQL.append("\n , '" + dto.getIdChequera() + "'");
		    sbSQL.append("\n , " + dto.getIdTipoOperacion());
		    
		    sbSQL.append("\n , " + 6);
		    sbSQL.append("\n , " + 0);
		    sbSQL.append("\n , " + dto.getIdUsuario());
		    sbSQL.append("\n , " + 0);
		    
		    if(ConstantesSet.gsDBM.equals("SYBASE"))
		    	sbSQL.append("\n , '" + dto.getNoDocto() + "'");
		    else
		    	sbSQL.append("\n , " + dto.getNoDocto());
		        
		    sbSQL.append("\n , " + dto.getIdGrupo());
		    sbSQL.append("\n , " + dto.getNoFolioMov());
		    sbSQL.append("\n , " + dto.getFolioRef());
		    
	        sbSQL.append("\n ,'" + funciones.ponerFechaSola(dto.getFecValor()) + "'");
	        sbSQL.append("\n ,'" + funciones.ponerFechaSola(dto.getFecValor()) + "'");
	        sbSQL.append("\n , " + dto.getCuenta());
	        sbSQL.append("\n ,'" + funciones.ponerFechaSola(dto.getFecValor()) + "'");
	        sbSQL.append("\n ,'" + funciones.ponerFechaSola(dto.getFecValor()) + "'");
		    
		    sbSQL.append("\n , " + dto.getImporte());
		    sbSQL.append("\n , " + dto.getValorTasa());
		    sbSQL.append("\n , " + dto.getImporteOriginal());
		    sbSQL.append("\n , 1 ");
		    sbSQL.append("\n , " + dto.getIdCaja());
		    
		    sbSQL.append("\n , '" + dto.getIdDivisa() + "'");
		    sbSQL.append("\n , " + dto.getAplica());
		    sbSQL.append("\n , '" + dto.getIdEstatusMov() + "'");
		    sbSQL.append("\n , '" + "S" + "'");
		    sbSQL.append("\n , '" + "P" + "'");
		    
		    if(dto.getIdBancoBenef() != 0)
		        sbSQL.append("\n , " + 0);
		    
		    if(!dto.getBeneficiario().equals("")) //ctaBenef
		        sbSQL.append("\n , '" + dto.getBeneficiario() + "'");
		 
		    if(dto.getSecuencia() != 0)
		        sbSQL.append("\n , " + dto.getSecuencia() + "");
		    
		    if(!dto.getConcepto().equals(""))
		        sbSQL.append("\n , '" + dto.getConcepto() + "'");
		    
		    sbSQL.append("\n , '" + dto.getOrigenMov() + "'");
		    
		    sbSQL.append("\n )");
		    
		    iInsert = jdbcTemplate.update(sbSQL.toString());

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:insertaCapitalizacion");
		}
		return iInsert;
	}
	
	/**
	 * FunSQLSelectChequeraConcEmp
	 * @param iEmpresa
	 * @param sDivisa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<CatCtaBancoDto> consultarChequerasConcEmp(int iEmpresa, String sDivisa){
		List<CatCtaBancoDto> list = new ArrayList<CatCtaBancoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  SELECT id_chequera, id_banco ");
		    sbSQL.append("\n  FROM cat_cta_banco ");
		    sbSQL.append("\n  WHERE no_empresa = " + iEmpresa);
		    sbSQL.append("\n      and tipo_chequera in ('C', 'M') ");
		    sbSQL.append("\n      and id_divisa = '" + sDivisa + "' ");
		    sbSQL.append("\n      and b_concentradora = 'S' ");
		    
		    list = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public CatCtaBancoDto mapRow(ResultSet rs, int idx) throws SQLException{
					CatCtaBancoDto resultado = new CatCtaBancoDto();
					resultado.setIdChequera(rs.getString("id_chequera"));
					resultado.setIdBanco(rs.getInt("id_banco"));
				return resultado;
			}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarChequerasTraspCoinv");
		}
		return list;
	}
	
	/**
	 * FunSQLSelectCheqCoinvMismoBanco
	 * @param iEmpresa
	 * @param sDivisa
	 * @param iBanco
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<CatCtaBancoDto> consultarCheqCoinvMismoBanco(int iEmpresa, String sDivisa, int iBanco){
		List<CatCtaBancoDto> list = new ArrayList<CatCtaBancoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  SELECT cc.id_chequera, cc.id_banco ");
		    sbSQL.append("\n  FROM cat_cta_banco cc ");
		    sbSQL.append("\n  WHERE cc.no_empresa = " + iEmpresa);
		    sbSQL.append("\n      and cc.tipo_chequera in ('P', 'M') ");
		    sbSQL.append("\n      and cc.id_divisa = '" + sDivisa + "' ");
		    sbSQL.append("\n      and cc.id_chequera = ");
		    sbSQL.append("\n          ( SELECT ccb.id_chequera ");
		    sbSQL.append("\n            FROM cat_cta_banco ccb ");
		    sbSQL.append("\n            WHERE ccb.id_banco = " + iBanco);
		    sbSQL.append("\n                and ccb.no_empresa = cc.no_empresa ");
		    sbSQL.append("\n                and ccb.tipo_chequera = cc.tipo_chequera ");
		    sbSQL.append("\n                and ccb.id_divisa = cc.id_divisa ");
		    sbSQL.append("\n                and ccb.mismo_banco = 'S' ) ");
		    
		    list = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public CatCtaBancoDto mapRow(ResultSet rs, int idx) throws SQLException{
					CatCtaBancoDto resultado = new CatCtaBancoDto();
					resultado.setIdChequera(rs.getString("id_chequera"));
					resultado.setIdBanco(rs.getInt("id_banco"));
				return resultado;
			}});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarCheqCoinvMismoBanco");
		}
		return list;
	}
	
	/**
	 * FunSQLSelectSaldoCredito
	 * @param iEmpresa
	 * @param sDivisa
	 * @return
	 */
	public double consultarSaldoCredito(int iEmpresa, String sDivisa){
		StringBuffer sbSQL = new StringBuffer();
		double dSdoCredito = 0;
		BigDecimal bdSdoCredito = new BigDecimal(0);
		try{
			sbSQL.append("\n  SELECT coalesce(sum(s.importe), 0) as SdoCredito ");
		    sbSQL.append("\n  FROM saldo s, empresa e ");
		    sbSQL.append("\n  WHERE s.no_empresa = e.no_empresa ");
		    sbSQL.append("\n      AND e.no_empresa = " + iEmpresa);
		    sbSQL.append("\n      AND s.no_cuenta = e.no_cuenta_emp ");
		    sbSQL.append("\n      AND s.id_tipo_saldo = 91 ");
		    sbSQL.append("\n      AND s.no_linea = ");
		    sbSQL.append("\n          ( SELECT cd.id_divisa_soin ");
		    sbSQL.append("\n            FROM cat_divisa cd ");
		    sbSQL.append("\n            WHERE cd.id_divisa = '" + sDivisa + "' ) ");
		    
		    bdSdoCredito = jdbcTemplate.queryForObject(sbSQL.toString(), BigDecimal.class);
		    dSdoCredito = bdSdoCredito.doubleValue();

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarSaldoCredito");
		}
		return dSdoCredito;
	}
	
	/**
	 * FunSQLInsertVencInversion
	 * @param dto
	 * @return
	 */
	public int insertarVencimientoInversion(VencimientoInversionDto dto){
		StringBuffer sbSQL = new StringBuffer();
		int iInserta = 0;
		try{
			sbSQL.append("\n  INSERT INTO vencimiento_inversion ( no_controladora, ");
		    sbSQL.append("\n      no_empresa, fec_desde, fec_hasta, interes, interes_dev, ");
		    sbSQL.append("\n      isr, comision, tasa_rend, id_divisa, b_comision_cap, ");
		    sbSQL.append("\n      saldo_promedio, iva ) ");
		    
		    sbSQL.append("\n  VALUES ( " + dto.getNoControladora() + ", " + dto.getNoEmpresa());
		    
	        sbSQL.append("\n      , '" + funciones.ponerFechaSola(dto.getFecDesde()) + "', '" + funciones.ponerFechaSola(dto.getFecHasta()) + "', ");
		    
		    sbSQL.append("\n      " + dto.getInteres() + ", " + dto.getInteresDev() + ", " + dto.getIsr());
		    sbSQL.append("\n      , " + dto.getComision() + "," + dto.getTasaRend() + ", ");
		    sbSQL.append("\n      '" + dto.getIdDivisa() + "','N'," + dto.getSaldoPromedio());
		    sbSQL.append("\n      ," + dto.getIva() + " ) ");
		    
		    iInserta = jdbcTemplate.update(sbSQL.toString());
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:insertarVencimientoInversion");
		}
		return iInserta;
	}
	
	/**
	 * FunSQLReporteSdoPromGrid
	 * consulta del reporte de saldo promedio
	 * @param iUsuario
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> consultarReporteSaldoPromedio(int iUsuario){
		List<Map<String, Object>> resultado = null;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  SELECT no_empresa,desc_empresa, saldo_prom, interes, isr,");
		    sbSQL.append("\n      comision,id_banco,id_chequera, ");
		    sbSQL.append("\n      (interes - isr - comision + iva + int_dev) as importe_tras, ");
		    sbSQL.append("\n      isr_dev, int_dev, iva, (interes + int_dev) as int_tot ");
		    sbSQL.append("\n  FROM tmp_sdoprom ");
		    sbSQL.append("\n  WHERE usuario_alta = " + iUsuario);
		    
		    resultado = (List<Map<String, Object>>)jdbcTemplate.query(sbSQL.toString(), new RowMapper(){

				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("no_empresa", rs.getString("no_empresa"));
			            results.put("desc_empresa", rs.getString("desc_empresa"));
			            results.put("saldo_prom", rs.getString("saldo_prom"));
			            results.put("interes", rs.getString("interes"));
			            results.put("isr", rs.getString("isr"));
			            results.put("comision", rs.getString("comision"));
			            results.put("id_banco", rs.getString("id_banco"));
			            results.put("id_chequera", rs.getString("id_chequera"));
			            results.put("importe_tras", rs.getString("importe_tras"));
			            results.put("isr_dev", rs.getString("isr_dev"));
			            results.put("int_dev", rs.getString("int_dev"));
			            results.put("iva", rs.getString("iva"));
			            results.put("int_tot", rs.getString("int_tot"));
			            return results;
					}
				});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarReporteSaldoPromedio");
		}
		return resultado;
	}
	
	/**
	 * FunSQLObtenAnho
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<OrdenInversionDto> consultarAnio(){
		List<OrdenInversionDto> anios = new ArrayList<OrdenInversionDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  select distinct(year(fec_alta)) as anho ");
		    sbSQL.append("\n  From orden_inversion ");
		    sbSQL.append("\n  order by year(fec_alta) desc ");
		    
		    anios = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public OrdenInversionDto mapRow(ResultSet rs, int idx) throws SQLException{
					OrdenInversionDto resultado = new OrdenInversionDto();
					resultado.setAnio(rs.getInt("anho"));
				return resultado;
			}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarAnio");
		}
		return anios;
	}
	
	/**
	 * FunSQLObtenMes
	 */
	@SuppressWarnings("unchecked")
	public List<OrdenInversionDto> consultarMes(int iAnio){
		List<OrdenInversionDto> anios = new ArrayList<OrdenInversionDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			if(ConstantesSet.gsDBM.equals("DB2"))
		        sbSQL.append("\n  select distinct(month(fec_alta)) as num_mes,monthname(fec_alta)as nom_mes from orden_inversion");
		    else
		        sbSQL.append("\n  select distinct(month(fec_alta))as num_mes,datename(month, fec_alta)as nom_mes from orden_inversion");
		  
		    sbSQL.append("\n  Where Year(fec_alta) = " + iAnio);
		    sbSQL.append("\n  order by month(fec_alta)");
		    
		    anios = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public OrdenInversionDto mapRow(ResultSet rs, int idx) throws SQLException{
					OrdenInversionDto resultado = new OrdenInversionDto();
					resultado.setMes(rs.getString("nom_mes"));
					resultado.setNumMes(rs.getInt("num_mes"));
				return resultado;
			}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarMes");
		}
		return anios;
	}
	
	/**
	 * FunSQLSelectEmpresasConcen5
	 * @param iUsuario
	 * @param iEmpresa
	 * @param iLinea
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<EmpresaDto> consultarEmpresasCoinv(int iUsuario, int iEmpresa, int iLinea){
		StringBuffer sbSQL = new StringBuffer();
		List<EmpresaDto> listDatos = new ArrayList<EmpresaDto>();
		try{
			sbSQL.append("\n  Select ");
		    sbSQL.append("\n  e.no_empresa ");
		    sbSQL.append("\n ,e.nom_empresa ");
		    sbSQL.append("\n  From ");
		    sbSQL.append("\n  cuenta c");
		    sbSQL.append("\n ,empresa e");
		    sbSQL.append("\n  Where ");
		    sbSQL.append("\n  c.no_empresa = " + iEmpresa);
		    sbSQL.append("\n  and c.id_tipo_cuenta = 'T' ");
		    sbSQL.append("\n  and c.no_cuenta = e.no_empresa ");
		    sbSQL.append("\n  and c.no_linea = " + iLinea);
		    //Mostrar solo las empresas asignadas al usuario
		    sbSQL.append("\n  AND e.no_empresa in (select no_empresa from ");
		    sbSQL.append("\n         usuario_empresa where no_usuario = " + iUsuario + ")");
		    sbSQL.append("\n  order by e.no_empresa ");
		    
		    System.out.println("query para reporte de estado de cta llena el grid: " + sbSQL.toString());
		    
		    listDatos= jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public EmpresaDto mapRow(ResultSet rs, int idx) throws SQLException {
					EmpresaDto cons = new EmpresaDto();
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setNomEmpresa(rs.getString("nom_empresa"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarEmpresasCoinv");
		}
		
		return listDatos;
	}
	
	/**
	 * FunSQLDeleteTmpEdoCuenta
	 * @param iUsuarioAlta
	 * @return
	 */
	public int eliminarTmpEstadoCuenta(int iUsuarioAlta){
		StringBuffer sbSQL = new StringBuffer();
		int resDel = 0;
		try{
		    if(ConstantesSet.gsDBM.equals("DB2") || ConstantesSet.gsDBM.equals("POSTGRESQL"))
		    	sbSQL.append(" delete from ");
		    else
		    	sbSQL.append(" delete ");
		    
		    sbSQL.append(" tmp_edocuenta ");
		    sbSQL.append(" where ");
		    sbSQL.append(" usuario_alta = " + iUsuarioAlta);
		    
		    resDel = jdbcTemplate.update(sbSQL.toString());
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:eliminarTmpEstadoCuenta");
		}
		return resDel;
	}
	
	/**
	 * FunSQLSelectSaldoDepRet
	 * @param iEmpresa
	 * @param iCuenta
	 * @param iLinea
	 * @param Fecha
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<HistSaldoDto> consultarSaldoHistorico(int iEmpresa, int iCuenta, int iLinea, Date dFecha){
		List<HistSaldoDto> listSaldo = new ArrayList<HistSaldoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
            	if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
            	{
		            sbSQL.append("\n  select ");
		            sbSQL.append("\n  fec_valor");
		            sbSQL.append("\n ,'SALDO INICIAL' as concepto");
		            sbSQL.append("\n ,convert(char,importe) as sdo_ini");
		            sbSQL.append("\n ,0.00 as depositos");
		            sbSQL.append("\n ,0.00 as retiros");
		            sbSQL.append("\n ,0.00 as sdo_fin");
		            sbSQL.append("\n ,1 as etiqueta");
		            sbSQL.append("\n  From ");
		            sbSQL.append("\n  hist_saldo");
		            sbSQL.append("\n  Where ");
		            sbSQL.append("\n  no_empresa = " + iEmpresa);
		            sbSQL.append("\n  and no_cuenta = " + iCuenta);
		            sbSQL.append("\n  and no_linea = " + iLinea);
		            sbSQL.append("\n  and fec_valor = convert(date, '" + funciones.ponerFechaSola(dFecha) + "', 103)");
		            sbSQL.append("\n  and id_tipo_saldo = 90");
            	}
            	if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
    			{
		            sbSQL.append("\n  select ");
		            sbSQL.append("\n  fec_valor");
		            sbSQL.append("\n ,'SALDO INICIAL' as concepto");
		            sbSQL.append("\n ,to_char(importe,'999999999990D99') as sdo_ini");
		            sbSQL.append("\n ,0.00 as depositos");
		            sbSQL.append("\n ,0.00 as retiros");
		            sbSQL.append("\n ,0.00 as sdo_fin");
		            sbSQL.append("\n ,1 as etiqueta");
		            sbSQL.append("\n  From ");
		            sbSQL.append("\n  hist_saldo");
		            sbSQL.append("\n  Where ");
		            sbSQL.append("\n  no_empresa = " + iEmpresa);
		            sbSQL.append("\n  and no_cuenta = " + iCuenta);
		            sbSQL.append("\n  and no_linea = " + iLinea);
		            sbSQL.append("\n  and fec_valor = '" + dFecha + "'");
		            sbSQL.append("\n  and id_tipo_saldo = 90");
    			}
            	if(ConstantesSet.gsDBM.equals("DB2"))
            	{
		            sbSQL.append("\n  select ");
		            sbSQL.append("\n  fec_valor");
		            sbSQL.append("\n ,'SALDO INICIAL' as concepto");
		            sbSQL.append("\n ,cast(importe as varchar(18)) as sdo_ini");
		            sbSQL.append("\n ,0.00 as depositos");
		            sbSQL.append("\n ,0.00 as retiros");
		            sbSQL.append("\n ,0.00 as sdo_fin");
		            sbSQL.append("\n ,1 as etiqueta");
		            sbSQL.append("\n  From ");
		            sbSQL.append("\n  hist_saldo");
		            sbSQL.append("\n  Where ");
		            sbSQL.append("\n  no_empresa = " + iEmpresa);
		            sbSQL.append("\n  and no_cuenta = " + iCuenta);
		            sbSQL.append("\n  and no_linea = " + iLinea);
		            sbSQL.append("\n  and fec_valor = '" + funciones.ponerFechaSola(dFecha) + "'");
		            sbSQL.append("\n  and id_tipo_saldo = 90");
            	}
            	
            	listSaldo= jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
    				public HistSaldoDto mapRow(ResultSet rs, int idx) throws SQLException {
    					HistSaldoDto resultado = new HistSaldoDto();
    					resultado.setFecValor(rs.getDate("fec_valor"));
    					resultado.setConcepto(rs.getString("concepto"));
    					resultado.setSaldoIni(rs.getDouble("sdo_ini"));
    					resultado.setDepositos(rs.getDouble("depositos"));
    					resultado.setRetiros(rs.getDouble("retiros"));
    					resultado.setSaldoFin(rs.getDouble("sdo_fin"));
    					resultado.setEtiqueta(rs.getInt("etiqueta"));
    					return resultado;
    				}});

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarSaldoHistorico");
		}
		return listSaldo;
	}
	
	/**
	 * FunSQLInsertTmpEdoCuenta
	 * @param iUsuario
	 * @param listDatos
	 * @return
	 */
	public int insertarTmpEstadoCuenta(int iUsuario, HistSaldoDto dtoDatos){
		int insert = 0;
		StringBuffer sbSQL = new StringBuffer();
		try{
		    sbSQL.append("\n  Insert ");
		    sbSQL.append("\n  into ");
		    sbSQL.append("\n  tmp_edocuenta ");
		    sbSQL.append("\n  ( ");
		    sbSQL.append("\n  usuario_alta");
		    sbSQL.append("\n ,secuencia");
		    sbSQL.append("\n ,fecha");
		    sbSQL.append("\n ,concepto");
		    sbSQL.append("\n ,saldo_inicial");
		    sbSQL.append("\n ,depositos");
		    sbSQL.append("\n ,retiros");
		    sbSQL.append("\n ,saldo_final");
		    sbSQL.append("\n ,etiqueta");
		    sbSQL.append("\n  )");
		    sbSQL.append("\n  values ");
		    sbSQL.append("\n  ( ");
		    
		    sbSQL.append( iUsuario);
		    sbSQL.append("\n ," + dtoDatos.getSecuencia());
		    sbSQL.append("\n ,'" + funciones.ponerFechaSola(dtoDatos.getFecValor()) + "'");
		    sbSQL.append("\n ,'" + dtoDatos.getConcepto() + "'");
		    sbSQL.append("\n ," + dtoDatos.getSaldoIni());
		    sbSQL.append("\n ," + dtoDatos.getDepositos());
		    sbSQL.append("\n ," + dtoDatos.getRetiros());
		    sbSQL.append("\n ," + dtoDatos.getSaldoFin());
		    sbSQL.append("\n ," + dtoDatos.getEtiqueta());
		    sbSQL.append("\n  )");
		    
		    insert = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:insertarTmpEstadoCuenta");
		}
		return insert;
	}
	
	/**
	 * FunSQLSelectSaldoDepRetDetalle
	 * @param dFechaFinHoy
	 * @param iEmpresa
	 * @param iCuenta
	 * @param sDivisa
	 * @param dFechaIni
	 * @param dFechaFin
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<HistSaldoDto> consultarSaldoDepRetDetalle(int iEmpresa, int iCuenta, String sDivisa, 
			Date dFechaIni, Date dFechaFin){
		StringBuffer sbSQL = new StringBuffer();
		List<HistSaldoDto> listMovto = new ArrayList<HistSaldoDto>();
		try{
			//Muestra los movimientos que afectan el saldo
		    sbSQL.append("\n  SELECT no_folio_det, fec_valor_original as fec_valor, m.concepto, ");
		    sbSQL.append("\n      0.00 as sdo_ini, ");
		    
		    if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
		    {
		        sbSQL.append("\n  to_char(CASE WHEN m.id_tipo_movto = 'I' ");
		        sbSQL.append("\n                     THEN m.importe END, '999999999990D99') as depositos, ");
		        sbSQL.append("\n  to_char(CASE WHEN m.id_tipo_movto = 'E' ");
		        sbSQL.append("\n                     THEN m.importe end, '999999999990D99') as retiros, ");
		    }
		    else
		    {
		    	if(ConstantesSet.gsDBM.equals("DB2"))
		    	{
		            sbSQL.append("\n  cast(CASE WHEN m.id_tipo_movto = 'I' ");
		            sbSQL.append("\n                     THEN m.importe END as varchar(18)) as depositos, ");
		            sbSQL.append("\n  cast(CASE WHEN m.id_tipo_movto = 'E' ");
		            sbSQL.append("\n                     THEN m.importe end as varchar(18)) as retiros, ");
		    	}
		        else
		        {
		            sbSQL.append("\n  convert(char, CASE WHEN m.id_tipo_movto = 'I' ");
		            sbSQL.append("\n                     THEN m.importe END) as depositos, ");
		            sbSQL.append("\n  convert(char, CASE WHEN m.id_tipo_movto = 'E' ");
		            sbSQL.append("\n                     THEN m.importe end) as retiros, ");
		        }
		    }
		    
		    sbSQL.append("\n      0.00 as sdo_fin, 2 as etiqueta ");
		    sbSQL.append("\n  FROM movimiento m ");
		    sbSQL.append("\n  WHERE ( ( m.id_tipo_operacion = 3705 and id_tipo_movto = 'I') ");
		    sbSQL.append("\n      or  ( m.id_tipo_operacion = 3706 and id_tipo_movto = 'E') ");
		    sbSQL.append("\n      or  ( m.id_tipo_operacion = 7005 and id_tipo_movto = 'I') ");
		    sbSQL.append("\n      or  ( m.id_tipo_operacion = 8201 and id_tipo_movto = 'I') ");
		    sbSQL.append("\n      or  ( m.id_tipo_operacion = 8200 and id_tipo_movto = 'E') ) ");
		    sbSQL.append("\n      and no_empresa = " + iEmpresa);
		    sbSQL.append("\n      and no_cuenta =  " + iCuenta);
		    sbSQL.append("\n      and id_divisa like '" + sDivisa + "'");
		    
		    if(ConstantesSet.gsDBM.equals("DB2") || ConstantesSet.gsDBM.equals("SQL SERVER"))
		    {
		        sbSQL.append("\n      and fec_valor_original between '" + funciones.ponerFechaSola(dFechaIni) + "' ");
		        sbSQL.append("\n          and '" + funciones.ponerFechaSola(dFechaFin) + "'");
		    }
		    else
		    {
		        sbSQL.append("\n      and fec_valor_original between '" + dFechaIni + "' ");
		        sbSQL.append("\n          and '" + dFechaFin + "'");
		    }
		    
		    sbSQL.append("\n      and id_estatus_mov = 'A' ");
		    
		    sbSQL.append("\n  UNION ALL ");
		    
		    sbSQL.append("\n  SELECT no_folio_det, fec_valor_original as fec_valor, m.concepto, ");
		    sbSQL.append("\n      0.00 as sdo_ini, ");
		    
		    if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
		    {
		        sbSQL.append("\n  to_char(CASE WHEN m.id_tipo_movto = 'I' ");
		        sbSQL.append("\n                     THEN m.importe END, '999999999990D99') as depositos, ");
		        sbSQL.append("\n  to_char(CASE WHEN m.id_tipo_movto = 'E' ");
		        sbSQL.append("\n                     THEN m.importe end, '999999999990D99') as retiros, ");
		    }
		    else
		    {
		    	if(ConstantesSet.gsDBM.equals("DB2"))
		    	{
		            sbSQL.append("\n  cast(CASE WHEN m.id_tipo_movto = 'I' ");
		            sbSQL.append("\n                     THEN m.importe END as varchar(18)) as depositos, ");
		            sbSQL.append("\n  cast(CASE WHEN m.id_tipo_movto = 'E' ");
		            sbSQL.append("\n                     THEN m.importe end as varchar(18)) as retiros, ");
		    	}
		        else
		        {
		            sbSQL.append("\n  convert(char, CASE WHEN m.id_tipo_movto = 'I' ");
		            sbSQL.append("\n                     THEN m.importe END) as depositos, ");
		            sbSQL.append("\n  convert(char, CASE WHEN m.id_tipo_movto = 'E' ");
		            sbSQL.append("\n                     THEN m.importe end) as retiros, ");
		        }
		    }
		    
		    sbSQL.append("\n      0.00 as sdo_fin, 2 as etiqueta ");
		    sbSQL.append("\n  FROM movimiento m ");
		    sbSQL.append("\n  WHERE ( m.id_tipo_operacion = 3707 and id_tipo_movto = 'E' ) ");
		    sbSQL.append("\n      and no_empresa = " + iCuenta);
		    sbSQL.append("\n      and id_divisa like '" + sDivisa + "'");
		    
		    if(ConstantesSet.gsDBM.equals("DB2") || ConstantesSet.gsDBM.equals("SQL SERVER"))
		    {
		        sbSQL.append("\n      and fec_valor_original between '" + funciones.ponerFechaSola(dFechaIni) + "' ");
		        sbSQL.append("\n          and '" + funciones.ponerFechaSola(dFechaFin) + "'");
		    }
		    else
		    {
		        sbSQL.append("\n      and fec_valor_original between '" + dFechaIni + "' ");
		        sbSQL.append("\n          and '" + dFechaFin + "'");
		    }
		    sbSQL.append("\n      and id_estatus_mov = 'A'");
		    
		    sbSQL.append("\n  UNION ALL ");
		    
		    sbSQL.append("\n  SELECT no_folio_det, fec_valor_original as fec_valor, m.concepto, ");
		    sbSQL.append("\n      0.00 as sdo_ini, ");
		    
		    if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
		    {
		        sbSQL.append("\n  to_char(CASE WHEN m.id_tipo_movto = 'I' ");
		        sbSQL.append("\n                     THEN m.importe END, '999999999990D99') as depositos, ");
		        sbSQL.append("\n  to_char(CASE WHEN m.id_tipo_movto = 'E' ");
		        sbSQL.append("\n                     THEN m.importe end, '999999999990D99') as retiros, ");
		    }
		    else
		    {
		    	if(ConstantesSet.gsDBM.equals("DB2"))
		    	{
		            sbSQL.append("\n  cast(CASE WHEN m.id_tipo_movto = 'I' ");
		            sbSQL.append("\n                     THEN m.importe END as varchar(18)) as depositos, ");
		            sbSQL.append("\n  cast(CASE WHEN m.id_tipo_movto = 'E' ");
		            sbSQL.append("\n                     THEN m.importe END as varchar(18)) as retiros, ");
		    	}
		        else
		        {
		            sbSQL.append("\n  convert(char, CASE WHEN m.id_tipo_movto = 'I' ");
		            sbSQL.append("\n                     THEN m.importe END) as depositos, ");
		            sbSQL.append("\n  convert(char, CASE WHEN m.id_tipo_movto = 'E' ");
		            sbSQL.append("\n                     THEN m.importe end) as retiros, ");
		        }
		    }
		    
		    sbSQL.append("\n      0.00 as sdo_fin, 2 as etiqueta ");
		    sbSQL.append("\n  FROM hist_movimiento m ");
		    sbSQL.append("\n  WHERE ( ( m.id_tipo_operacion = 3705 and id_tipo_movto = 'I') ");
		    sbSQL.append("\n      or  ( m.id_tipo_operacion = 3706 and id_tipo_movto = 'E') ");
		    sbSQL.append("\n      or  ( m.id_tipo_operacion = 7005 and id_tipo_movto = 'I') ");
		    sbSQL.append("\n      or  ( m.id_tipo_operacion = 8201 and id_tipo_movto = 'I') ");
		    sbSQL.append("\n      or  ( m.id_tipo_operacion = 8200 and id_tipo_movto = 'E') ) ");
		    sbSQL.append("\n      and no_empresa = " + iEmpresa);
		    sbSQL.append("\n      and no_cuenta =  " + iCuenta);
		    sbSQL.append("\n      and id_divisa like '" + sDivisa + "'");
		    
		    if(ConstantesSet.gsDBM.equals("DB2") || ConstantesSet.gsDBM.equals("SQL SERVER"))
		    {
		        sbSQL.append("\n      and fec_valor_original between '" + funciones.ponerFechaSola(dFechaIni) + "' ");
		        sbSQL.append("\n          and '" + funciones.ponerFechaSola(dFechaFin) + "'");
		    }
		    else
		    {
		        sbSQL.append("\n      and fec_valor_original between '" + dFechaIni + "' ");
		        sbSQL.append("\n          and '" + dFechaFin + "'");
		    }
		    
		    sbSQL.append("\n      and id_estatus_mov = 'A' ");
		    
		    sbSQL.append("\n  UNION ALL ");
		    
		    sbSQL.append("\n  SELECT no_folio_det, fec_valor_original as fec_valor, m.concepto, ");
		    sbSQL.append("\n      0.00 as sdo_ini, ");
		    
		    if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
		    {
		        sbSQL.append("\n  to_char(CASE WHEN m.id_tipo_movto = 'I' ");
		        sbSQL.append("\n                     THEN m.importe END, '999999999990D99') as depositos, ");
		        sbSQL.append("\n  to_char(CASE WHEN m.id_tipo_movto = 'E' ");
		        sbSQL.append("\n                     THEN m.importe end, '999999999990D99') as retiros, ");
		    }
		    else
		    {
		    	if(ConstantesSet.gsDBM.equals("DB2"))
		    	{
		            sbSQL.append("\n  cast(CASE WHEN m.id_tipo_movto = 'I' ");
		            sbSQL.append("\n                     THEN m.importe END as varchar(18)) as depositos, ");
		            sbSQL.append("\n  cast(CASE WHEN m.id_tipo_movto = 'E' ");
		            sbSQL.append("\n                     THEN m.importe end as varchar(18)) as retiros, ");
		    	}
		        else
		        {
		            sbSQL.append("\n  convert(char, CASE WHEN m.id_tipo_movto = 'I' ");
		            sbSQL.append("\n                     THEN m.importe END) as depositos, ");
		            sbSQL.append("\n  convert(char, CASE WHEN m.id_tipo_movto = 'E' ");
		            sbSQL.append("\n                     THEN m.importe end) as retiros, ");
		        }
		    }
		    
		    sbSQL.append("\n      0.00 as sdo_fin, 2 as etiqueta ");
		    sbSQL.append("\n  FROM hist_movimiento m ");
		    sbSQL.append("\n  WHERE ( m.id_tipo_operacion = 3707 and id_tipo_movto = 'E' ) ");
		    sbSQL.append("\n      and no_empresa = " + iCuenta);
		    sbSQL.append("\n      and id_divisa like '" + sDivisa + "'");
		    
		    if(ConstantesSet.gsDBM.equals("DB2") || ConstantesSet.gsDBM.equals("SQL SERVER"))
		    {
		        sbSQL.append("\n      and fec_valor_original between '" + funciones.ponerFechaSola(dFechaIni) + "' ");
		        sbSQL.append("\n          and '" + funciones.ponerFechaSola(dFechaFin) + "'");
		    }
		    else
		    {
		        sbSQL.append("\n      and fec_valor_original between '" + dFechaIni + "' ");
		        sbSQL.append("\n          and '" + dFechaFin + "'");
		    }
		    
		    sbSQL.append("\n      and id_estatus_mov = 'A'");
		    
		    sbSQL.append("\n  UNION ALL ");
		    
		    // Muestra los movimientos de intereses
		    sbSQL.append("\n  select no_folio_det, fec_valor_original as fec_valor, m.concepto");
		    sbSQL.append("\n       ,0.00 as sdo_ini");
		    if(ConstantesSet.gsDBM.equals("SYBASE"))
		    {
		        sbSQL.append("\n ,convert(varchar, case when m.id_tipo_movto = 'I'  then m.importe end) as depositos ");
		        sbSQL.append("\n ,convert(varchar, case when m.id_tipo_movto = 'E'  then m.importe end) as retiros");
		    }
		    else
		    {
		        sbSQL.append("\n ,cast(case when m.id_tipo_movto = 'I'  then m.importe end as varchar(18)) as depositos ");
		        sbSQL.append("\n ,cast(case when m.id_tipo_movto = 'E'  then m.importe end as varchar(18)) as retiros");
		    }
		    
		    sbSQL.append("\n ,0.00 as sdo_fin, 2 as etiqueta");
		    sbSQL.append("\n  from movimiento m");
		    sbSQL.append("\n  Where((m.id_tipo_operacion in (7000,7004) and id_tipo_movto = 'I')");
		    sbSQL.append("\n  or (m.id_tipo_operacion in (7001,7002) and id_tipo_movto = 'E'))");
		    sbSQL.append("\n  and no_empresa = " + iEmpresa);
		    sbSQL.append("\n  and no_cuenta =  " + iCuenta);
		    sbSQL.append("\n  and id_divisa like '" + sDivisa + "'");
		    
		    if(ConstantesSet.gsDBM.equals("DB2") || ConstantesSet.gsDBM.equals("SQL SERVER"))
		    {
		        sbSQL.append("\n      and fec_valor_original between '" + funciones.ponerFechaSola(dFechaIni) + "' ");
		        sbSQL.append("\n          and '" + funciones.ponerFechaSola(dFechaFin) + "'");
		    }
		    else
		    {
		        sbSQL.append("\n      and fec_valor_original between '" + dFechaIni + "' ");
		        sbSQL.append("\n          and '" + dFechaFin + "'");
		    }
		    
		    sbSQL.append("\n  and id_estatus_mov = 'L'");
		    
		    sbSQL.append("\n  UNION ALL ");
		    
		    sbSQL.append("\n  select no_folio_det, fec_valor_original as fec_valor, m.concepto");
		    sbSQL.append("\n      ,0.00 as sdo_ini");
		    if(ConstantesSet.gsDBM.equals("SYBASE"))
		    {
		        sbSQL.append("\n ,convert(varchar, case when m.id_tipo_movto = 'I'  then m.importe end) as depositos ");
		        sbSQL.append("\n ,convert(varchar, case when m.id_tipo_movto = 'E'  then m.importe end) as retiros");
		    }
		    else
		    {
		        sbSQL.append("\n ,cast(case when m.id_tipo_movto = 'I'  then m.importe end as varchar(18)) as depositos ");
		        sbSQL.append("\n ,cast(case when m.id_tipo_movto = 'E'  then m.importe end as varchar(18)) as retiros");
		    }
		    
		    sbSQL.append("\n ,0.00 as sdo_fin,2 as etiqueta");
		    sbSQL.append("\n  from hist_movimiento m ");
		    sbSQL.append("\n  Where ((m.id_tipo_operacion in (7000,7004) and id_tipo_movto = 'I')");
		    sbSQL.append("\n  or (m.id_tipo_operacion in (7001,7002) and id_tipo_movto = 'E'))");
		    sbSQL.append("\n  and no_empresa = " + iEmpresa);
		    sbSQL.append("\n  and no_cuenta =  " + iCuenta);
		    sbSQL.append("\n  and id_divisa like '" + sDivisa + "'");
		    
		    if(ConstantesSet.gsDBM.equals("DB2") || ConstantesSet.gsDBM.equals("SQL SERVER"))
		    {
		        sbSQL.append("\n      and fec_valor_original between '" + funciones.ponerFechaSola(dFechaIni) + "' ");
		        sbSQL.append("\n          and '" + funciones.ponerFechaSola(dFechaFin) + "'");
		    }
		    else
		    {
		        sbSQL.append("\n      and fec_valor_original between '" + dFechaIni + "' ");
		        sbSQL.append("\n          and '" + dFechaFin + "'");
		    }
		    
		    sbSQL.append("\n  and id_estatus_mov = 'L'");
		    
		    sbSQL.append("\n  ORDER BY fec_valor, no_folio_det ");
		    
		    listMovto= jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public HistSaldoDto mapRow(ResultSet rs, int idx) throws SQLException {
					HistSaldoDto resultado = new HistSaldoDto();
					resultado.setNoLinea(rs.getInt("no_folio_det"));
					resultado.setFecValor(rs.getDate("fec_valor"));
					resultado.setConcepto(rs.getString("concepto"));
					resultado.setSaldoIni(rs.getDouble("sdo_ini"));
					resultado.setDepositos(rs.getDouble("depositos"));
					resultado.setRetiros(rs.getDouble("retiros"));
					resultado.setSaldoFin(rs.getDouble("sdo_fin"));
					resultado.setEtiqueta(rs.getInt("etiqueta"));
					return resultado;
				}});
		        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarSaldoDepRetDetalle");
		}
		return listMovto;
	}
	
	/**
	 * FunSQLSelectSaldoFin
	 * @param iEmpresa
	 * @param iCuenta
	 * @param sDivisa
	 * @param dFechaIni
	 * @param dFechaFin
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<HistSaldoDto> consultarSaldoFin(boolean bFechaFinHoy, int iEmpresa, int iLinea, int iCuenta, 
			Date dFechaHoy, Date dFechaFin){
		StringBuffer sbSQL = new StringBuffer();
		List<HistSaldoDto> listSaldo = new ArrayList<HistSaldoDto>();
		try{
			if(ConstantesSet.gsDBM.equals("SYBASE"))
			{
				if(bFechaFinHoy)
				{
		            sbSQL.append("\n  select ");
		            sbSQL.append("\n  fec_valor = convert(date, '" + dFechaHoy + "', 103)  ");
		            sbSQL.append("\n ,'SALDO FINAL' as concepto");
		            sbSQL.append("\n ,0.00 as sdo_ini");
		            sbSQL.append("\n ,0.00 as depositos");
		            sbSQL.append("\n ,0.00 as retiros");
		            sbSQL.append("\n ,convert(varchar, sum(case when id_tipo_saldo = 7 then  importe *-1 else importe end)) as sdo_fin ");
		            sbSQL.append("\n , 3 as etiqueta");
		            sbSQL.append("\n  From saldo");
		            sbSQL.append("\n  Where no_empresa = " + iEmpresa);
		            sbSQL.append("\n  and no_linea =  " + iLinea);
		            sbSQL.append("\n  and no_cuenta = " + iCuenta);
		            sbSQL.append("\n  and id_tipo_saldo in (90,5,7)");
				}
				else
				{
		            sbSQL.append("\n  select ");
		            sbSQL.append("\n  fec_valor");
		            sbSQL.append("\n ,'SALDO FINAL' as concepto");
		            sbSQL.append("\n ,0.00 as sdo_ini");
		            sbSQL.append("\n ,0.00 as depositos");
		            sbSQL.append("\n ,0.00 as retiros");
		            sbSQL.append("\n ,convert(varchar,importe) as sdo_fin ");
		            sbSQL.append("\n , 3 as etiqueta");
		            sbSQL.append("\n  From hist_saldo");
		            sbSQL.append("\n  Where no_empresa = " + iEmpresa);
		            sbSQL.append("\n  and no_linea =  " + iLinea);
		            sbSQL.append("\n  and no_cuenta = " + iCuenta);
		            sbSQL.append("\n  and fec_valor = '" + dFechaFin + "'");
		            sbSQL.append("\n  and id_tipo_saldo = 91");
				}
			}
			if(ConstantesSet.gsDBM.equals("SQL SERVER"))
			{
				if(bFechaFinHoy)
				{
		            sbSQL.append("\n  select ");
		            sbSQL.append("\n  fec_valor = convert(date, '" + funciones.ponerFechaSola(dFechaHoy) + "', 103)  ");
		            sbSQL.append("\n ,'SALDO FINAL' as concepto");
		            sbSQL.append("\n ,0.00 as sdo_ini");
		            sbSQL.append("\n ,0.00 as depositos");
		            sbSQL.append("\n ,0.00 as retiros");
		            sbSQL.append("\n ,cast(sum(case when id_tipo_saldo = 7 then  importe *-1 else importe end) as varchar) as sdo_fin ");
		            sbSQL.append("\n , 3 as etiqueta");
		            sbSQL.append("\n  From saldo");
		            sbSQL.append("\n  Where no_empresa = " + iEmpresa);
		            sbSQL.append("\n  and no_linea =  " + iLinea);
		            sbSQL.append("\n  and no_cuenta = " + iCuenta);
		            sbSQL.append("\n  and id_tipo_saldo in (90,5,7)");
				}
				else
				{
		            sbSQL.append("\n  select ");
		            sbSQL.append("\n  fec_valor");
		            sbSQL.append("\n ,'SALDO FINAL' as concepto");
		            sbSQL.append("\n ,0.00 as sdo_ini");
		            sbSQL.append("\n ,0.00 as depositos");
		            sbSQL.append("\n ,0.00 as retiros");
		            sbSQL.append("\n ,cast(importe as varchar) as sdo_fin ");
		            sbSQL.append("\n , 3 as etiqueta");
		            sbSQL.append("\n  From hist_saldo");
		            sbSQL.append("\n  Where no_empresa = " + iEmpresa);
		            sbSQL.append("\n  and no_linea =  " + iLinea);
		            sbSQL.append("\n  and no_cuenta = " + iCuenta);
		            sbSQL.append("\n  and fec_valor = '" + funciones.ponerFechaSola(dFechaFin) + "'");
		            sbSQL.append("\n  and id_tipo_saldo = 91");
				}
			}
			if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
			{
				if(bFechaFinHoy)
				{
		            sbSQL.append("\n  select ");
		            sbSQL.append("\n  convert(date, '" + dFechaHoy + "', 103) as fec_valor ");
		            sbSQL.append("\n ,'SALDO FINAL' as concepto");
		            sbSQL.append("\n ,0.00 as sdo_ini");
		            sbSQL.append("\n ,0.00 as depositos");
		            sbSQL.append("\n ,0.00 as retiros");
		            sbSQL.append("\n ,cast(sum(case when id_tipo_saldo = 7 then  importe *-1 else importe end) as varchar) as sdo_fin ");
		            sbSQL.append("\n , 3 as etiqueta");
		            sbSQL.append("\n  From saldo");
		            sbSQL.append("\n  Where no_empresa = " + iEmpresa);
		            sbSQL.append("\n  and no_linea =  " + iLinea);
		            sbSQL.append("\n  and no_cuenta = " + iCuenta);
		            sbSQL.append("\n  and id_tipo_saldo in (90,5,7)");
				}
				else
				{
		            sbSQL.append("\n  select ");
		            sbSQL.append("\n  fec_valor");
		            sbSQL.append("\n ,'SALDO FINAL' as concepto");
		            sbSQL.append("\n ,0.00 as sdo_ini");
		            sbSQL.append("\n ,0.00 as depositos");
		            sbSQL.append("\n ,0.00 as retiros");
		            sbSQL.append("\n ,cast(importe as varchar) as sdo_fin ");
		            sbSQL.append("\n , 3 as etiqueta");
		            sbSQL.append("\n  From hist_saldo");
		            sbSQL.append("\n  Where no_empresa = " + iEmpresa);
		            sbSQL.append("\n  and no_linea =  " + iLinea);
		            sbSQL.append("\n  and no_cuenta = " + iCuenta);
		            sbSQL.append("\n  and fec_valor = '" + dFechaFin + "'");
		            sbSQL.append("\n  and id_tipo_saldo = 91");
				}
			}
			if(ConstantesSet.gsDBM.equals("DB2"))
			{
				if(bFechaFinHoy)
				{
		            sbSQL.append("\n  select ");
		            sbSQL.append("\n  convert(date, '" + funciones.ponerFechaSola(dFechaHoy) + "', 103) as fec_valor ");
		            sbSQL.append("\n ,'SALDO FINAL' as concepto");
		            sbSQL.append("\n ,0.00 as sdo_ini");
		            sbSQL.append("\n ,0.00 as depositos");
		            sbSQL.append("\n ,0.00 as retiros");
		            sbSQL.append("\n ,cast(sum(case when id_tipo_saldo = 7 then  importe *-1 else importe end) as varchar(18)) as sdo_fin ");
		            sbSQL.append("\n , 3 as etiqueta");
		            sbSQL.append("\n  From saldo");
		            sbSQL.append("\n  Where no_empresa = " + iEmpresa);
		            sbSQL.append("\n  and no_linea =  " + iLinea);
		            sbSQL.append("\n  and no_cuenta = " + iCuenta);
		            sbSQL.append("\n  and id_tipo_saldo in (90,5,7)");
				}
				else
				{
		            sbSQL.append("\n  select ");
		            sbSQL.append("\n  fec_valor");
		            sbSQL.append("\n ,'SALDO FINAL' as concepto");
		            sbSQL.append("\n ,0.00 as sdo_ini");
		            sbSQL.append("\n ,0.00 as depositos");
		            sbSQL.append("\n ,0.00 as retiros");
		            sbSQL.append("\n ,cast(importe as varchar(18)) as sdo_fin ");
		            sbSQL.append("\n , 3 as etiqueta");
		            sbSQL.append("\n  From hist_saldo");
		            sbSQL.append("\n  Where no_empresa = " + iEmpresa);
		            sbSQL.append("\n  and no_linea =  " + iLinea);
		            sbSQL.append("\n  and no_cuenta = " + iCuenta);
		            sbSQL.append("\n  and fec_valor = '" + funciones.ponerFechaSola(dFechaFin) + "'");
		            sbSQL.append("\n  and id_tipo_saldo = 91");
				}
			}
			
			listSaldo= jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public HistSaldoDto mapRow(ResultSet rs, int idx) throws SQLException {
					HistSaldoDto resultado = new HistSaldoDto();
					resultado.setFecValor(rs.getDate("fec_valor"));
					resultado.setConcepto(rs.getString("concepto"));
					resultado.setSaldoIni(rs.getDouble("sdo_ini"));
					resultado.setDepositos(rs.getDouble("depositos"));
					resultado.setRetiros(rs.getDouble("retiros"));
					resultado.setSaldoFin(rs.getDouble("sdo_fin"));
					resultado.setEtiqueta(rs.getInt("etiqueta"));
					return resultado;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarSaldoFin");
		}
		return listSaldo;
	}
	
	/**
	 * FunSQLSelectDatosEmpresa
	 * @param iNoPersona
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarDatosEmpresa(int iNoPersona){
		List<Map<String, Object>> listDatos = null;
		StringBuffer sbSQL = new StringBuffer();
		try{
			if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
			{
		        sbSQL.append("\n  Select ");
		        sbSQL.append("\n  rfc ");
		        sbSQL.append("\n ,calle_no " + " " + "+");
		        sbSQL.append("\n  colonia " + " " + "+");
		        sbSQL.append("\n  ciudad  ");
		        sbSQL.append("\n  as direccion ");
		        sbSQL.append("\n  From ");
		        sbSQL.append("\n  persona p left join direccion d on (p.no_persona = d.no_empresa and p.no_persona = d.no_persona)");
		        sbSQL.append("\n  Where  ");
		        sbSQL.append("\n  p.no_persona = " + iNoPersona);
		        sbSQL.append("\n  And p.id_tipo_persona  in ('E','I')");
			}
			if(ConstantesSet.gsDBM.equals("POSTGRESQL") || ConstantesSet.gsDBM.equals("DB2"))
			{
		        sbSQL.append("\n  Select ");
		        sbSQL.append("\n  rfc ");
		        sbSQL.append("\n ,calle_no " + " " + "||");
		        sbSQL.append("\n  colonia "+ " " + "||");
		        sbSQL.append("\n  ciudad  ");
		        sbSQL.append("\n  as direccion ");
		        sbSQL.append("\n  From ");
		        sbSQL.append("\n  persona p");
		        sbSQL.append("\n ,direccion d");
		        sbSQL.append("\n  Where  ");
		        sbSQL.append("\n  p.no_persona = " + iNoPersona);
		        sbSQL.append("\n  And p.id_tipo_persona  in ('E','I')");
		        sbSQL.append("\n  And p.no_empresa = d.no_empresa");
		        sbSQL.append("\n  And p.no_persona = d.no_persona");
			}
			
			listDatos = (List<Map<String, Object>>)jdbcTemplate.query(sbSQL.toString(), new RowMapper(){

				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("rfc", rs.getString("rfc"));
			            results.put("direccion", rs.getString("direccion"));
			            return results;
					}
				});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarDatosEmpresa");
		}
		return listDatos;
	}
	
	/**
	 * FunSQLSelectInteresCapitalizado
	 * @param sMes
	 * @param iAnio
	 * @param iConcentradora
	 * @param iEmpresa
	 * @param sDivisa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<VencimientoInversionDto> consultarInteresCapitalizado(int iMes, int iAnio, 
						int iConcentradora,int iEmpresa, String sDivisa){
		List<VencimientoInversionDto> listIntereses = new ArrayList<VencimientoInversionDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  SELECT * ");
		    sbSQL.append("\n  FROM vencimiento_inversion ");
		    if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
		    {
		        sbSQL.append("\n  WHERE Date_Part('month', fec_hasta) = " + iMes);
		        sbSQL.append("\n      and  Date_Part('year', fec_hasta) = " + iAnio);
		    }
		    else
		    {
		    	if(ConstantesSet.gsDBM.equals("DB2"))
		    	{
		            sbSQL.append("\n  WHERE MONTH(fec_hasta) = " + iMes);
		            sbSQL.append("\n      and  YEAR(fec_hasta) = " + iAnio);
		    	}
		        else
		        {
		            sbSQL.append("\n  WHERE DatePart(m, fec_hasta) = " + iMes);
		            sbSQL.append("\n      and  DatePart(year, fec_hasta) = " + iAnio);
		        }
		    }
		    
		    sbSQL.append("\n      and no_controladora = " + iConcentradora);
		    sbSQL.append("\n      and no_empresa = " + iEmpresa);
		    sbSQL.append("\n      and id_divisa = '" + sDivisa + "' ");

		    listIntereses= jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public VencimientoInversionDto mapRow(ResultSet rs, int idx) throws SQLException {
					VencimientoInversionDto resultado = new VencimientoInversionDto();
					resultado.setNoEmpresa(rs.getInt("no_empresa"));
					resultado.setFecDesde(rs.getDate("fec_desde"));
					resultado.setFecHasta(rs.getDate("fec_hasta"));
					resultado.setIdDivisa(rs.getString("id_divisa"));
					resultado.setNoControladora(rs.getInt("no_controladora"));
					resultado.setInteres(rs.getDouble("interes"));
					resultado.setIsr(rs.getDouble("isr"));
					resultado.setComision(rs.getDouble("comision"));
					resultado.setTasaRend(rs.getDouble("tasa_rend"));
					resultado.setInteresDev(rs.getDouble("interes_dev"));
					resultado.setSaldoPromedio(rs.getDouble("saldo_promedio"));
					resultado.setBComisionCap(rs.getString("b_comision_cap"));
					resultado.setIva(rs.getDouble("iva"));
					
					return resultado;
				}});
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarInteresCapitalizado");
		}
		return listIntereses;
	}
	
	/**
	 * FunSQLReporteEdoCuenta
	 * @param iUsuario
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> consultarReporteEstadoCuenta(int iUsuarioAlta){
		List<Map<String, Object>> resultado = null;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  select ");
		    sbSQL.append("\n  fecha as fec_valor, concepto, saldo_inicial as sdo_ini,");
		    sbSQL.append("\n  depositos as depositos,retiros as retiros, ");
		    sbSQL.append("\n  saldo_final as sdo_fin, etiqueta ");
		    sbSQL.append("\n  From tmp_edocuenta ");
		    sbSQL.append("\n  Where usuario_alta = " + iUsuarioAlta);
		    
		    resultado = (List<Map<String, Object>>)jdbcTemplate.query(sbSQL.toString(), new RowMapper(){

				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("fec_valor", rs.getDate("fec_valor"));
			            results.put("concepto", rs.getString("concepto"));
			            results.put("sdo_ini", rs.getDouble("sdo_ini"));
			            results.put("depositos", rs.getDouble("depositos"));
			            results.put("retiros", rs.getDouble("retiros"));
			            results.put("sdo_fin", rs.getDouble("sdo_fin"));
			            results.put("etiqueta", rs.getString("etiqueta"));
			            return results;
					}
				});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarReporteEstadoCuenta");
		}
		return resultado;
	}
	
	/**
	 * FunSQLSelectSaldoPromedio2
	 * @param iControladora
	 * @param iEmpresa
	 * @param dFecha1
	 * @param dFecha2
	 * @param sDivisa
	 * @return
	 */
	public double consultarSaldoPromedio2(int iControladora,int iEmpresa, Date dFecha1, Date dFecha2, String sDivisa){
		StringBuffer sbSQL = new StringBuffer();
		double dSdoPromedio = 0;
		BigDecimal bdSdoPromedio = new BigDecimal(0);
		try{
			sbSQL.append("\nDeclare @saldo_promedio int");
			sbSQL.append("\n  Select ");
		    sbSQL.append("\n   @saldo_promedio = saldo_promedio ");
		    sbSQL.append("\n  From ");
		    sbSQL.append("\n  vencimiento_inversion ");
		    sbSQL.append("\n  where ");
		    sbSQL.append("\n  no_controladora = " + iControladora);		    
		    sbSQL.append("\n  and fec_desde ='" + funciones.ponerFechaSola(dFecha1) + "'");
	        sbSQL.append("\n  and fec_hasta ='" + funciones.ponerFechaSola(dFecha2) + "'");
 	        sbSQL.append("\n  and no_empresa = " + iEmpresa);
	        sbSQL.append("\n  and id_divisa ='" + sDivisa + "'");
	        sbSQL.append("\n IF @@rowcount = 0 ");
	        sbSQL.append("\n Set @saldo_promedio = 0");
	        sbSQL.append("\n Select @saldo_promedio 'saldo_promedio'");
	        bdSdoPromedio = jdbcTemplate.queryForObject(sbSQL.toString(), BigDecimal.class);
	        dSdoPromedio = bdSdoPromedio.doubleValue();

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarSaldoPromedio2");
			e.printStackTrace();
		}
		return dSdoPromedio;
	}
	
	/**
	 * FunSQLSelectSaldoPromedio
	 * @param iControladora
	 * @param iEmpresa
	 * @param dFecha1
	 * @param dFecha2
	 * @return
	 */
	public double consultarSaldoPromedio(int iControladora,int iEmpresa, Date dFecha1, Date dFecha2){
		StringBuffer sbSQL = new StringBuffer();
		double dSdoPromedio = 0;
		BigDecimal bdSdoPromedio = new BigDecimal(0);
		try{
			sbSQL.append("\n  select coalesce(sum(cast(saldo_promedio as decimal(15,2))),0) as sumsaldo ");
		    sbSQL.append("\n  from  vencimiento_inversion ");
		    sbSQL.append("\n  where no_controladora= " + iControladora);
		    sbSQL.append("\n  and no_empresa=" + iEmpresa);
		    
		    sbSQL.append("\n  and fec_desde >'" + funciones.ponerFechaSola(dFecha1) + "'");
	        sbSQL.append("\n  and fec_hasta <='" + funciones.ponerFechaSola(dFecha2) + "'");
	        
	        bdSdoPromedio = jdbcTemplate.queryForObject(sbSQL.toString(), BigDecimal.class);
	        dSdoPromedio = bdSdoPromedio.doubleValue();

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarSaldoPromedio");
		}
		return dSdoPromedio;
	}
	
	/**
	 * FunSQLSelectSaldoImporte
	 * @param sTabla
	 * @param iEmpresa
	 * @param iCuenta
	 * @param iLinea
	 * @param dFecha1
	 * @param dFecha2
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarSaldoImporte(String sTabla, int iEmpresa, int iCuenta, 
			int iLinea, Date dFecha1, Date dFecha2){
		List<Map<String, Object>> resultado = null;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  select ");
		    sbSQL.append("\n  coalesce(sum(importe),0) as suminteres");
		    sbSQL.append("\n  from " + sTabla);
		    sbSQL.append("\n  where ");
		    
		    sbSQL.append("\n  no_empresa=" + iEmpresa);
		    sbSQL.append("\n  and id_tipo_operacion=7000");
		    sbSQL.append("\n  and no_cuenta=" + iCuenta);
		    sbSQL.append("\n  and no_linea=" + iLinea);
		    
	        sbSQL.append("\n  and fec_valor between '" + funciones.ponerFechaSola(dFecha1) + "'");
	        sbSQL.append("\n  and '" + funciones.ponerFechaSola(dFecha2) + "'");
	        
	        resultado = (List<Map<String, Object>>)jdbcTemplate.query(sbSQL.toString(), new RowMapper(){

				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("sumInteres", rs.getString("suminteres"));
			            return results;
					}
				});
	        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarSaldoImporte");
		}
		return resultado;
	}
	
	/**
	 * FunSQLSelectMINMAXFecha
	 * @param bMinMax
	 * @param iEmpresa
	 * @param iCuenta
	 * @param iLinea
	 * @param dFecha1
	 * @param dFecha2
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<HistSaldoDto> consultarMinMaxFecha(boolean bMinMax, int iEmpresa, int iCuenta, 
			int iLinea, Date dFecha1, Date dFecha2){
		List<HistSaldoDto> lFecha = new ArrayList<HistSaldoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  select ");
		    if(bMinMax)
		        sbSQL.append("\n  min(fec_valor) as fecha");
		    else
		        sbSQL.append("\n  max(fec_valor) as fecha");
		    
		    sbSQL.append("\n  From hist_saldo");
		    sbSQL.append("\n  Where no_empresa = " + iEmpresa);
		    sbSQL.append("\n  and id_tipo_saldo = 90");
		    sbSQL.append("\n  and no_cuenta = " + iCuenta);
		    sbSQL.append("\n  and no_linea = " + iLinea);
		    
	        sbSQL.append("\n  and fec_valor >= '" + funciones.ponerFechaSola(dFecha1) + "'");
	        sbSQL.append("\n  and fec_valor <= '" + funciones.ponerFechaSola(dFecha2) + "'");
	        
	        lFecha= jdbcTemplate.query(sbSQL.toString(), new RowMapper<HistSaldoDto>(){
				public HistSaldoDto mapRow(ResultSet rs, int idx) throws SQLException {
					HistSaldoDto resultado = new HistSaldoDto();
					resultado.setFecValor(rs.getDate("fecha"));
					return resultado;
				}});
	        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarMinMaxFecha");
		}
		return lFecha;
	}
	
	/**
	 * FunSQLSelectSaldoInicial2
	 * @param iEmpresa
	 * @param iLinea
	 * @param iCuenta
	 * @param dFecha
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<HistSaldoDto> consultarSaldoInicial(int iEmpresa, int iCuenta, int iLinea,Date dFecha){
		StringBuffer sbSQL = new StringBuffer();
		List<HistSaldoDto> listSaldo = new ArrayList<HistSaldoDto>();
		try{
			
			sbSQL.append("\n  Select ");
		    sbSQL.append("\n  importe ");
		    sbSQL.append("\n  From ");
		    sbSQL.append("\n  hist_saldo ");
		    sbSQL.append("\n  where ");
		    sbSQL.append("\n  no_empresa = " + iEmpresa);
		    sbSQL.append("\n  and id_tipo_saldo = 90 ");
		    sbSQL.append("\n  and no_cuenta = " + iCuenta);
		    sbSQL.append("\n  and no_linea = " + iLinea);
		    sbSQL.append("\n  and fec_valor = '" + funciones.ponerFechaSola(dFecha) + "'");
			
			listSaldo= jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public HistSaldoDto mapRow(ResultSet rs, int idx) throws SQLException {
					HistSaldoDto resultado = new HistSaldoDto();
					resultado.setImporte(rs.getDouble("importe"));
					return resultado;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarSaldoInicial");
		}
		return listSaldo;
	}
	
	/**
	 * FunSQLReporteHistSaldo2
	 * @param iEmpresa
	 * @param iLinea
	 * @param dFechaIni
	 * @param dFechaFin
	 * @param iUsuario
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String,Object>> consultarReporteHistoricoSaldos(int iEmpresa, int iLinea, Date dFechaIni, Date dFechaFin, int iUsuario){
		List<Map<String, Object>> resultado = null;
		StringBuffer sbSQL = new StringBuffer();
		try{
			if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
			{
		        sbSQL.append("\n  select no_cuenta as no_empresa,e.nom_empresa,isnull(convert(char,importe),'0') as sdo_inicial, ");
		        sbSQL.append("\n  divisa=case when no_linea=1 then 'SALDOS EN MN' when no_linea=2 then 'SALDOS EN DLS' end,");
		        sbSQL.append("\n  convert(char,(select isnull(sum(importe),0) from hist_saldo b where b.no_empresa=a.no_empresa ");
		        sbSQL.append("\n  and b.no_cuenta =a.no_cuenta and b.no_linea=a.no_linea and id_tipo_saldo =5 ");
		        sbSQL.append("\n  and fec_valor between  '" + funciones.ponerFechaSola(dFechaIni) + "' and '" + funciones.ponerFechaSola(dFechaFin) + "' )) as abonos, ");
		        sbSQL.append("\n  convert(char,(select isnull(sum(importe),0) from hist_saldo c where c.no_empresa=a.no_empresa ");
		        sbSQL.append("\n  and c.no_cuenta =a.no_cuenta and c.no_linea=a.no_linea and id_tipo_saldo =7 ");
		        sbSQL.append("\n  and fec_valor between  '" + funciones.ponerFechaSola(dFechaIni) + "' and '" + funciones.ponerFechaSola(dFechaFin) + "' )) as cargos, ");
		        sbSQL.append("\n  convert(char,(select isnull(importe,0) from hist_saldo d where d.no_empresa=a.no_empresa ");
		        sbSQL.append("\n  and d.no_cuenta =a.no_cuenta and d.no_linea=a.no_linea and id_tipo_saldo =91 ");
		        sbSQL.append("\n  and fec_valor ='" + funciones.ponerFechaSola(dFechaFin) + "' )) as sdo_fin");
		        sbSQL.append("\n  FROM hist_saldo a, empresa e ");
		        sbSQL.append("\n  Where a.no_empresa =" + iEmpresa);
		        sbSQL.append("\n  and a.no_cuenta=e.no_empresa ");
		        sbSQL.append("\n  and a.no_linea =" + iLinea);
		        sbSQL.append("\n  and a.no_cuenta in (select no_cuenta  from cuenta where no_empresa=" + iEmpresa + " and id_tipo_cuenta='T') ");
		        sbSQL.append("\n  and a.id_tipo_saldo = 90 ");
		        sbSQL.append("\n  and a.fec_valor='" + funciones.ponerFechaSola(dFechaIni) + "' ");
		        //Buscar solo las empresas que tenga asignadas el usuario);
		        sbSQL.append("\n  AND e.no_empresa in (select no_empresa from ");
		        sbSQL.append("\n         usuario_empresa where no_usuario = " + iUsuario + ")");
			}
			else if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
			{
		        sbSQL.append("\n  select no_cuenta as no_empresa,e.nom_empresa,coalesce(importe,'0') as sdo_inicial, ");
		        sbSQL.append("\n  case when no_linea=1 then 'SALDOS EN MN' when no_linea=2 then 'SALDOS EN DLS' end as divisa,");
		        sbSQL.append("\n  (select coalesce(sum(importe),0) from hist_saldo b where b.no_empresa=a.no_empresa ");
		        sbSQL.append("\n  and b.no_cuenta =a.no_cuenta and b.no_linea=a.no_linea and id_tipo_saldo =5 ");
		        sbSQL.append("\n  and fec_valor between  '" + dFechaIni + "' and '" + dFechaFin + "' ) as abonos, ");
		        sbSQL.append("\n  (select coalesce(sum(importe),0) from hist_saldo c where c.no_empresa=a.no_empresa ");
		        sbSQL.append("\n  and c.no_cuenta =a.no_cuenta and c.no_linea=a.no_linea and id_tipo_saldo =7 ");
		        sbSQL.append("\n  and fec_valor between  '" + dFechaIni + "' and '" + dFechaFin + "' ) as cargos, ");
		        sbSQL.append("\n  (select coalesce(importe,0) from hist_saldo d where d.no_empresa=a.no_empresa ");
		        sbSQL.append("\n  and d.no_cuenta =a.no_cuenta and d.no_linea=a.no_linea and id_tipo_saldo =91 ");
		        sbSQL.append("\n  and fec_valor ='" + dFechaFin + "' ) as sdo_fin");
		        sbSQL.append("\n  FROM hist_saldo a, empresa e ");
		        sbSQL.append("\n  Where a.no_empresa =" + iEmpresa);
		        sbSQL.append("\n  and a.no_cuenta=e.no_empresa ");
		        sbSQL.append("\n  and a.no_linea =" + iLinea);
		        sbSQL.append("\n  and a.no_cuenta in (select no_cuenta  from cuenta where no_empresa=" + iEmpresa + " and id_tipo_cuenta='T') ");
		        sbSQL.append("\n  and a.id_tipo_saldo = 90 ");
		        sbSQL.append("\n  and a.fec_valor='" + dFechaIni + "' ");
		        //Buscar solo las empresas que tenga asignadas el usuario
		        sbSQL.append("\n  AND e.no_empresa in (select no_empresa from ");
		        sbSQL.append("\n         usuario_empresa where no_usuario = " + iUsuario + ")");
			}
			else if(ConstantesSet.gsDBM.equals("DB2"))
			{
		        sbSQL.append("\n  select no_cuenta as no_empresa,e.nom_empresa,coalesce(importe,'0') as sdo_inicial, ");
		        sbSQL.append("\n  case when no_linea=1 then 'SALDOS EN MN' when no_linea=2 then 'SALDOS EN DLS' end as divisa,");
		        sbSQL.append("\n  (select coalesce(sum(importe),0) from hist_saldo b where b.no_empresa=a.no_empresa ");
		        sbSQL.append("\n  and b.no_cuenta =a.no_cuenta and b.no_linea=a.no_linea and id_tipo_saldo =5 ");
		        sbSQL.append("\n  and fec_valor between  '" + funciones.ponerFechaSola(dFechaIni) + "' and '" + funciones.ponerFechaSola(dFechaFin) + "' ) as abonos, ");
		        sbSQL.append("\n  (select coalesce(sum(importe),0) from hist_saldo c where c.no_empresa=a.no_empresa ");
		        sbSQL.append("\n  and c.no_cuenta =a.no_cuenta and c.no_linea=a.no_linea and id_tipo_saldo =7 ");
		        sbSQL.append("\n  and fec_valor between  '" + funciones.ponerFechaSola(dFechaIni) + "' and '" + funciones.ponerFechaSola(dFechaFin) + "' ) as cargos, ");
		        sbSQL.append("\n  (select coalesce(importe,0) from hist_saldo d where d.no_empresa=a.no_empresa ");
		        sbSQL.append("\n  and d.no_cuenta =a.no_cuenta and d.no_linea=a.no_linea and id_tipo_saldo =91 ");
		        sbSQL.append("\n  and fec_valor ='" + funciones.ponerFechaSola(dFechaFin) + "' ) as sdo_fin");
		        sbSQL.append("\n  FROM hist_saldo a, empresa e ");
		        sbSQL.append("\n  Where a.no_empresa =" + iEmpresa);
		        sbSQL.append("\n  and a.no_cuenta=e.no_empresa ");
		        sbSQL.append("\n  and a.no_linea =" + iLinea);
		        sbSQL.append("\n  and a.no_cuenta in (select no_cuenta  from cuenta where no_empresa=" + iEmpresa + " and id_tipo_cuenta='T') ");
		        sbSQL.append("\n  and a.id_tipo_saldo = 90 ");
		        sbSQL.append("\n  and a.fec_valor='" + funciones.ponerFechaSola(dFechaIni) + "' ");
		        //Buscar solo las empresas que tenga asignadas el usuario
		        sbSQL.append("\n  AND e.no_empresa in (select no_empresa from ");
		        sbSQL.append("\n         usuario_empresa where no_usuario = " + iUsuario + ")");
			}

		    
		    resultado = (List<Map<String, Object>>)jdbcTemplate.query(sbSQL.toString(), new RowMapper(){

				public Object mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("no_empresa", rs.getInt("no_empresa"));
			            results.put("nom_empresa", rs.getString("nom_empresa"));
			            results.put("sdo_inicial", rs.getDouble("sdo_inicial"));
			            results.put("divisa", rs.getString("divisa"));
			            results.put("abonos", rs.getDouble("abonos"));
			            results.put("cargos", rs.getDouble("cargos"));
			            results.put("sdo_fin", rs.getDouble("sdo_fin"));
			            return results;
					}
				});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarReporteEstadoCuenta");
		}
		return resultado;
	}
	
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Coinversion, C:CoinversionDaoImpl, M:setDataSource");
		}
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<Map<String, Object>> reportePrueba(String parametroPrueba) {
		StringBuffer sbSQL = new StringBuffer();
		sbSQL.append("select f.*, '");
		sbSQL.append(parametroPrueba);
		sbSQL.append("' as parametro from fechas f");
		List<Map<String, Object>> resultado = null;
		try {
			resultado  = jdbcTemplate.query(sbSQL.toString(), new RowMapper<Map<String, Object>>(){

				public Map<String, Object> mapRow(ResultSet rs, int i)
					throws SQLException {
			            Map<String, Object> results = new HashMap<String, Object>();
			            results.put("fecha", rs.getString("fec_hoy"));
			            results.put("parametro", rs.getString("parametro"));
			            return results;
					}
				});

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarReporteCoinversoras");
		}
		return resultado;
	}

	@Override
	public List<List<Object>> obtenerExcel() {
		StringBuffer sbSQL = new StringBuffer();
		sbSQL.append("select f.* ");
		sbSQL.append(" from fechas f");
		List<List<Object>> resultado = null;
		try {
			resultado  = jdbcTemplate.query(sbSQL.toString(), new RowMapper<List<Object>>(){

				public List<Object> mapRow(ResultSet rs, int i)
					throws SQLException {
						List<Object> results = new ArrayList<Object>();
			            results.add(rs.getString("fec_hoy"));
			            results.add(rs.getString("parametro"));
			            return results;
					}
				});

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarReporteCoinversoras");
		}
		return resultado;
	}

	@Override
	public int consultarCuentaEmpresa(int noEmpresa) {
		StringBuffer sbSql = new StringBuffer();
		int resultado;
		
		sbSql.append("select no_cuenta_emp from empresa where no_empresa = ? ");
		System.out.println("query"+sbSql.toString());
		resultado = this.getJdbcTemplate().queryForInt(sbSql.toString(), new Object[] {Utilerias.validarCadenaSQL(noEmpresa)});
		return resultado;
	}
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> llenarTipoSaldo(){
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n  Select tipo_saldo as ID, operacion as Descripcion");
	        sbSQL.append("\n  From clasificacion_tipo_saldo ");
	        System.out.println("tipos de saldo "+sbSQL);
	        list = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
		    	public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		LlenaComboGralDto cons = new LlenaComboGralDto();
			    		cons.setId(rs.getInt("ID"));
			    		cons.setDescripcion(rs.getString("Descripcion"));
		    		return cons;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Coinversion, C:CoinversionDaoImpl, M:consultarChequerasBarrido");
		}
		return list;
	}
	
//Alejandra Velázquez	
	
	public int buscaAbono(int tipoSal) {
		StringBuffer sb = new StringBuffer();
	
		try {
			sb.append("select abonos from clasificacion_tipo_saldo where tipo_saldo=" + tipoSal);
		} catch (Exception e) {
			System.out.println(e);
//			bitacora.buscaCargo(new Date().toString()+ " "
//					+ e.toString()+ "P:Coinversion, C:CoinversionDaoImpl, M:buscaCargo");
//			return -1;
		}
		
		return jdbcTemplate.queryForInt(sb.toString());
	}

	@Override
	public int buscaCargo(int tipoSaldo) {
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("select cargos from clasificacion_tipo_saldo where tipo_saldo=" + tipoSaldo);
		} catch (Exception e) {
			System.out.println(e);
		}
		return jdbcTemplate.queryForInt(sb.toString());
	}

	@Override
	public List<Retorno> consultarConfiguraSet() {
		System.out.println("ALE3");
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet();
	}
	


	
	
}
