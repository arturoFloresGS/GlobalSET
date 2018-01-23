package com.webset.set.egresos.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.egresos.dao.CompensarPropuestasDao;
import com.webset.set.egresos.dto.BloqueoPagoCruzadoDto;
import com.webset.set.egresos.dto.PagosPropuestosDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.ComunEgresosDto;
import com.webset.set.utilerias.dto.GrupoEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.ParamMovto3000Dto;
import com.webset.set.utilerias.dto.ParametroFactorajeDto;
import com.webset.set.utilerias.dto.SaldosChequerasDto;
import com.webset.set.utilerias.dto.StoreParamsComunDto;
import com.webset.utils.tools.Utilerias;
import com.webset.set.utilerias.ConstantesSet;

import mx.com.gruposalinas.Pagos.DT_Pagos_OBPagos;
import mx.com.gruposalinas.Pagos.DT_Pagos_ResponsePagosResponse;

public class CompensarPropuestasDaoImpl implements CompensarPropuestasDao {
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	ConsultasGenerales consultasGenerales;
	Funciones funciones = new Funciones();
	GlobalSingleton globalSingleton;
	String gsDBM = ConstantesSet.gsDBM;
	@Override
	public List<LlenaComboGralDto> llenarComboGrupoEmpresa(GrupoEmpresasDto dto) {
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.llenarComboGrupoEmpresa(dto);
	}

	@Override
	public List<LlenaComboGralDto> llenarComboDivXEmp(int idUsuario) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SaldosChequerasDto> obtenerSaldosChequeras(SaldosChequerasDto dto) {
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.obtenerSaldosChequeras(dto);
	}
	//revisado
	public Date obtenerFechaHoy(){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.obtenerFechaHoy();
	}

	public double obtenerTipoCambio(String idDivisa, Date fecha){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.obtenerTipoCambio(idDivisa, fecha);
	}

	@Override
	public int seleccionarFolioReal(String tipoFolio) {
		// TODO Auto-generated method stub
		return 0;
	}


	public Map<String, Object> ejecutarPagador(StoreParamsComunDto dto){
		try {

			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			return consultasGenerales.ejecutarPagador(dto);
		} catch (Exception e) {
			return null;
		}
	}

	public Map<String, Object> cambiarCuadrantes(StoreParamsComunDto dto)
	{
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.cambiarCuadrantes(dto);
	}

	@Override
	public Map<String, Object> ejecutarGenerador(int emp, int folParam, int folMovi, int folDeta, int result, String mensajeResp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int actualizarSolicitudFactoraje(int noFolioDet) {
		// TODO Auto-generated method stub
		return 0;
	}
	public Date consultarFechaHoy(){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.obtenerFechaHoy();
	}
	
	public int existeChequeraBenef(MovimientoDto movimiento) {
		
		int cont = 0;
		StringBuffer sql = new StringBuffer();

		try{
			if(movimiento.getIdChequeraBenef() != null && !movimiento.getIdChequeraBenef().equals("") 
					&& movimiento.getDescFormaPago().equals("TRANSFERENCIA")){
				//sql.append("\n select count(id_chequera) from CTAS_BANCO ");
				sql.append("\n select count(cb.id_chequera) from CTAS_BANCO cb JOIN MOVIMIENTO m ON cb.no_persona = m.no_cliente ");
				sql.append("\n where cb.id_chequera = '"+Utilerias.validarCadenaSQL(movimiento.getIdChequeraBenef())+"' ");
				sql.append("\n AND cb.no_persona = '"+Utilerias.validarCadenaSQL(movimiento.getNoCliente())+"' ");
				sql.append("\n AND cb.id_tipo_persona = 'P' ");
				sql.append("\n AND m.ID_FORMA_PAGO = 3 ");					
						
				cont = jdbcTemplate.queryForInt(sql.toString());
				
			}else{
				cont = -1; //Chequera vacia, -1 para que no imprima el color.
			}
				
		}catch(Exception e){
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Egresos, C:ConsultaPropuestasDao, M:existeChequeraBenef ");
		}
		return cont;		 
	}
	

	public List<MovimientoDto>consultarDetallePropuestasNoPagadas(PagosPropuestosDto dto){
		StringBuffer sql= new StringBuffer();
		
		List<MovimientoDto>listDetalle= new ArrayList<MovimientoDto>();
		try{
			sql.append( "  SELECT distinct m.no_docto, m.no_empresa, importe_original, ");
		    sql.append( "\n      e.desc_estatus, m.no_factura, m.fec_valor, ");
		    sql.append( "\n      m.fec_valor_original, cv.desc_cve_operacion, ");
		    sql.append( "\n      m.importe AS importe, ");
		    sql.append( "\n      m.id_divisa, m.id_forma_pago, fp.desc_forma_pago, ");
		    sql.append( "\n      b.desc_banco, m.id_chequera_benef, m.beneficiario, ");
		    sql.append( "\n      m.concepto, m.no_folio_det, m.no_folio_mov, ");
		    sql.append( "\n      m.no_cuenta, m.id_chequera, cv.id_cve_operacion, ");
		    sql.append( "\n      m.id_forma_pago, m.id_banco_benef, m.id_caja, ");
		    sql.append( "\n      m.id_leyenda, m.id_estatus_mov, m.no_cliente, ");
		    sql.append( "\n      m.tipo_cambio, m.origen_mov, m.solicita, ");
		    sql.append( "\n      m.autoriza, m.observacion, m.lote_entrada, ");
		    sql.append( "\n      cj.desc_caja, m.agrupa1, m.agrupa2, m.id_rubro, ");
		    sql.append( "\n      m.agrupa3, p.b_servicios, m.no_pedido, ");
		    sql.append( "\n      CASE WHEN m.id_divisa = 'MN' THEN m.importe ");
		    sql.append( "\n           ELSE m.importe * coalesce( ");
		    sql.append( "\n               ( SELECT max(vd.valor) ");
		    //sql.append( "\n               ( SELECT vd.valor ");
		    sql.append( "\n                 FROM valor_divisa vd ");
		    sql.append( "\n                 WHERE vd.id_divisa = m.id_divisa ");
		    sql.append( "\n                     and vd.fec_divisa =  convert(datetime,'"+Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFecha()).substring(0,10)) +"', 103) ), ");
		    sql.append( "\n           1) end as importe_mn, ");
		    sql.append( "\n      ( SELECT max(per.nombre_corto) ");
		    //sql.append( "\n      ( SELECT per.nombre_corto ");
		    sql.append( "\n        FROM persona per ");
		    
		    sql.append( "\n	 WHERE cast(m.no_cliente as integer) = per.no_persona ");
		    
		    sql.append( "\n            and per.id_tipo_persona = 'P' ) as nom_proveedor, ");
		    sql.append( "\n      m.fec_propuesta, m.id_divisa_original,  ");
		    sql.append( "\n      ( SELECT max(e.nom_empresa) ");
		    //sql.append( "\n      ( SELECT e.nom_empresa ");
		    sql.append( "\n        FROM empresa e ");
		    sql.append( "\n        WHERE e.no_empresa = m.no_empresa ) as nom_empresa, ");
		    sql.append( "\n      m.id_banco as id_banco_pago, ");
		    sql.append( "\n      m.id_chequera as id_chequera_pago, ");
		    sql.append( "\n      ( SELECT max(cb.desc_banco) ");
		    //sql.append( "\n      ( SELECT cb.desc_banco ");
		    sql.append( "\n        FROM cat_banco cb ");
		    sql.append( "\n        WHERE cb.id_banco = m.id_banco) as banco_pago, ");
		    sql.append( "\n      ( SELECT max(cg.desc_grupo_cupo) ");
		    //sql.append( "\n      ( SELECT cg.desc_grupo_cupo ");
		    sql.append( "\n        FROM cat_grupo_cupo cg ");
		    sql.append( "\n        WHERE cg.id_grupo_cupo = ");
		    sql.append( "\n            ( SELECT max(gfc.id_grupo_cupo) ");
		    //sql.append( "\n            ( SELECT gfc.id_grupo_cupo ");
		    sql.append( "\n              FROM grupo_flujo_cupo gfc ");
		    sql.append( "\n             WHERE gfc.id_rubro in ");
		    sql.append( "\n                  ( SELECT max(mm.id_rubro) ");
		    //sql.append( "\n                  ( SELECT mm.id_rubro ");
		    sql.append( "\n                    FROM movimiento mm ");
		    sql.append( "\n                    WHERE mm.folio_ref = m.no_folio_det ) ) ) ");
		    sql.append( "\n      as desc_grupo_cupo, ");
		    
		    sql.append( "\n      convert(datetime,'"+Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFecha()).substring(0,10)) +"', 103) - m.fec_valor_original as dias,");
		    
		    sql.append( "\n     ( SELECT coalesce(count(ccbv.id_divisa), 0) ");
		    sql.append( "\n       FROM cat_cta_banco ccbv ");
		    sql.append( "\n       Where ccbv.no_empresa = m.no_empresa ");
		    sql.append( "\n           AND ccbv.id_divisa = 'MN' ) ");
		    sql.append( "\n     as NumCheq_PagoMN, ");
		    
		    sql.append( "\n     ( SELECT coalesce(count(ccbv.id_divisa), 0) ");
		    sql.append( "\n       FROM cat_cta_banco ccbv ");
		    sql.append( "\n       Where ccbv.no_empresa = m.no_empresa ");
		    sql.append( "\n           AND ccbv.id_divisa = 'DLS' ) ");
		    sql.append( "\n     as NumCheq_PagoDLS, ");

		    sql.append( "\n      ( SELECT coalesce(count(cbpc.id_divisa), 0) ");
		    sql.append( "\n        FROM ctas_banco cbpc ");
		    
	        sql.append("\n    WHERE cbpc.no_persona = cast(m.no_cliente as integer)");
		    
		    sql.append("\n      and cbpc.id_divisa = 'MN' ) ");
		    sql.append("\n      as NumCheq_CruceMN, ");
		    
		    sql.append("\n      ( SELECT coalesce(count(cbpc.id_divisa), 0) ");
		    sql.append("\n        FROM ctas_banco cbpc ");
		     
	        sql.append( "    WHERE cbpc.no_persona = cast(m.no_cliente as integer)");
		    
		    sql.append( "            and cbpc.id_divisa = 'DLS' ) ");
		    sql.append( "      as NumCheq_CruceDLS, ");
		    
		    sql.append( "      ( SELECT coalesce(count(c.id_chequera), 0) ");
		    sql.append( "        FROM cat_cta_banco c ");
		    sql.append( "        WHERE c.no_empresa = m.no_empresa ");
		    sql.append( "            and c.id_divisa = m.id_divisa ) as NumCheqEmp, ");
		              
		    sql.append( "      ( SELECT coalesce(count(b.id_chequera), 0) ");
		    sql.append( "        FROM ctas_banco b ");
		    sql.append( "        WHERE b.no_persona = m.no_cliente ");
		    sql.append( "            and b.id_divisa = m.id_divisa ) as NumCheqCte, m.id_servicio_be, m.id_contable ");
		    
		    sql.append( "        ,(select equivale_persona from persona p where p.no_persona = m.no_cliente and p.id_tipo_persona = 'P') as equivale,");
		    sql.append( "       coalesce(m.no_cheque, 0) as no_cheque, coalesce(am.usuario_uno, 0) as usuario_uno, coalesce(am.usuario_dos, 0) as usuario_dos ");
		    
		    //Agregado EMS 18/12/2015: Nuevas fechas solicitadas
		    sql.append( " ,m.fecha_contabilizacion, m.fec_operacion ");
		    //Color EMS 28/01/2016
		    sql.append( " ,case when m.NO_COBRADOR is not null then");
		    sql.append( "	'NARANJA' ");
		    sql.append( "	 END as color ");
		    
		    sql.append( "	 ,invoice_type ");
		    
		    sql.append( "  FROM movimiento m ");
		    sql.append( "      LEFT JOIN cat_banco b ON ( m.id_banco_benef = b.id_banco )");
		    sql.append( "      LEFT JOIN cat_estatus e ON (m.id_estatus_mov = e.id_estatus ");
		    sql.append( "          AND e.clasificacion = 'MOV' ) ");
		    sql.append( "      LEFT JOIN cat_caja cj ON (m.id_caja = cj.id_caja) ");
		    sql.append( "      LEFT JOIN autorizacionesMov am ON (m.no_folio_det = am.no_folio_det) ");
		    
	        sql.append("\n	  LEFT JOIN proveedor p ON (cast(m.no_cliente as integer) = p.no_proveedor), ");
		    
		    sql.append("\n      cat_tipo_operacion co, cat_cve_operacion cv, ");
		    sql.append("\n      cat_forma_pago fp ");
		    
		    /*if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
		    	sql.append( "   ,cat_cta_banco_division ccbd ");
	    	*/
		    
	        sql.append( "\n  WHERE ");
	    
		    if(dto.getIdGrupoEmpresa()>0)
		    {
		        sql.append( "  m.no_empresa in (select no_empresa from grupo_empresa ");
		        sql.append( "\n                   where id_grupo_flujo = "+Utilerias.validarCadenaSQL(dto.getIdGrupoEmpresa())+") ");
		    }
		    
		    sql.append( "\n	  AND m.no_cliente not in (" + Utilerias.validarCadenaSQL(consultarConfiguraSet(206))+") ");
		    sql.append( "\n	  AND m.id_tipo_movto = 'E'  ");
		    sql.append( "\n	  AND (m.origen_mov <> 'INV' or m.origen_mov is NULL)");
		    sql.append( "\n	  AND m.id_tipo_operacion = co.id_tipo_operacion ");
		    sql.append( "\n	  AND co.no_empresa = 1 ");
		    sql.append( "\n	  AND m.id_cve_operacion = cv.id_cve_operacion ");
		    sql.append( "\n	  AND m.id_estatus_mov in ('N','C','F','L') ");
		    sql.append( "\n	  AND m.id_forma_pago = fp.id_forma_pago ");
		    sql.append( "\n	  AND m.id_forma_pago in (1,3,5,6,7,9,10) ");
		    sql.append("\n   and (m.PO_HEADERS = '' or m.PO_HEADERS is null)");
		    /*sql.append( "\n	  AND m.no_folio_det in ");//se comento porque no permitia ver los detalles de pagos parcializados porque no coincidian los folios
		    sql.append( "\n	      ( SELECT mm.folio_ref ");
		    sql.append( "\n	        FROM movimiento mm ");
		    sql.append( "\n	        WHERE mm.folio_ref = m.no_folio_det ) ");*/
		    
		    sql.append( "\n		  And m.cve_control = '"+Utilerias.validarCadenaSQL(dto.getCveControl())+"' ");

		    /*if(dto.getPsDivision()!=null && !dto.getPsDivision().equals("")) 
		    {
		        sql.append( "\n	  AND m.no_empresa = ccbd.no_empresa ");
		        sql.append( "\n	  AND m.id_chequera = ccbd.id_chequera ");
		        sql.append( "\n	  AND m.id_banco = ccbd.id_banco ");
		        sql.append( "\n	  AND m.division = ccbd.id_division ");
		    }*/
		   
	        if(dto.getPdOrigenMov()!=null && !dto.getPdOrigenMov().equals(""))
	            sql.append( "\n  AND m.origen_mov='"+Utilerias.validarCadenaSQL(dto.getPdOrigenMov())+"'");
	        
	        sql.append( "\n	  UNION ALL ");
            
	        sql.append( "\n	  SELECT distinct m.no_docto, m.no_empresa, importe_original, ");
	        sql.append( "\n      e.desc_estatus, m.no_factura, m.fec_valor, ");
	        sql.append( "\n      m.fec_valor_original, cv.desc_cve_operacion, ");
	        sql.append( "\n      m.importe AS importe, ");
	        sql.append( "\n      m.id_divisa, m.id_forma_pago, fp.desc_forma_pago, ");
	        sql.append( "\n      b.desc_banco, m.id_chequera_benef, m.beneficiario, ");
	        sql.append( "\n      m.concepto, m.no_folio_det, m.no_folio_mov, ");
	        sql.append( "\n      m.no_cuenta, m.id_chequera, cv.id_cve_operacion, ");
	        sql.append( "\n      m.id_forma_pago, m.id_banco_benef, m.id_caja, ");
	        sql.append( "\n      m.id_leyenda, m.id_estatus_mov, m.no_cliente, ");
	        sql.append( "\n      m.tipo_cambio, m.origen_mov, m.solicita, ");
	        sql.append( "\n      m.autoriza, m.observacion, m.lote_entrada, ");
	        sql.append( "\n      cj.desc_caja, m.agrupa1, m.agrupa2, m.id_rubro, ");
	        sql.append( "\n      m.agrupa3, '' as b_servicios, m.no_pedido, ");
	        sql.append( "\n      CASE WHEN m.id_divisa = 'MN' THEN m.importe ");
	        sql.append( "\n           ELSE m.importe * coalesce( ");
	        sql.append( "\n              ( SELECT DISTINCT vd.valor ");
	        sql.append( "\n                 FROM valor_divisa vd ");
	        sql.append( "\n                 WHERE vd.id_divisa = m.id_divisa ");
	        sql.append( "\n                     and vd.fec_divisa = convert(datetime,'"+Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFecha()).substring(0,10)) 
	        														    +"', 103) ), ");
	        sql.append( "\n           1) end as importe_mn, ");
	        sql.append( "\n      'PROVEEDOR INCIDENTAL' as nom_proveedor, ");
	        sql.append( "\n      m.fec_propuesta, m.id_divisa_original,  ");
	        sql.append( "\n      ( SELECT e.nom_empresa ");
	        sql.append( "\n        FROM empresa e ");
	        sql.append( "\n        WHERE e.no_empresa = m.no_empresa ) as nom_empresa, ");
	        sql.append( "\n      m.id_banco as id_banco_pago, ");
	        sql.append( "\n      m.id_chequera as id_chequera_pago, ");
	        sql.append( "\n      ( SELECT cb.desc_banco ");
	        sql.append( "\n        FROM cat_banco cb ");
	        sql.append( "\n        WHERE cb.id_banco = m.id_banco) as banco_pago, ");
	        sql.append( "\n      ( SELECT cg.desc_grupo_cupo ");
	        sql.append( "\n        FROM cat_grupo_cupo cg ");
	        sql.append( "\n        WHERE cg.id_grupo_cupo = ");
	        sql.append( "\n           ( SELECT gfc.id_grupo_cupo ");
	        sql.append( "\n              FROM grupo_flujo_cupo gfc ");
	        sql.append( "\n              WHERE gfc.id_rubro in ");
	        sql.append( "\n                  ( SELECT mm.id_rubro ");
	        sql.append( "\n                    FROM movimiento mm ");
	        sql.append( "\n                    WHERE mm.folio_ref = m.no_folio_det ) ) ) ");
	        sql.append( "\n      as desc_grupo_cupo, ");

	        sql.append( "\n      convert(datetime,'"+Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFecha()).substring(0,10)) 
	        						 +"', 103) - m.fec_valor_original as dias,");
	        
	        sql.append( "\n     ( SELECT coalesce(count(ccbv.id_divisa), 0) ");
	        sql.append( "\n       FROM cat_cta_banco ccbv ");
	        sql.append( "\n       Where ccbv.no_empresa = m.no_empresa ");
	        sql.append( "\n           AND ccbv.id_divisa = 'MN' ) ");
	        sql.append( "     as NumCheq_PagoMN, ");
	        
	        sql.append( "\n     ( SELECT coalesce(count(ccbv.id_divisa), 0) ");
	        sql.append( "\n      FROM cat_cta_banco ccbv ");
	        sql.append( "\n       Where ccbv.no_empresa = m.no_empresa ");
	        sql.append( "\n           AND ccbv.id_divisa = 'DLS' ) ");
	        sql.append( "     as NumCheq_PagoDLS, ");

	        sql.append( "\n      ( SELECT coalesce(count(cbpc.id_divisa), 0) ");
	        sql.append( "\n        FROM ctas_banco cbpc ");
	        

        	sql.append( "\n    WHERE cbpc.no_persona = cast(m.no_cliente as integer)");
 	        
	        sql.append("\n            and cbpc.id_divisa = 'MN' ) ");
	        sql.append("\n      as NumCheq_CruceMN, ");
	        
	        sql.append("\n      ( SELECT coalesce(count(cbpc.id_divisa), 0) ");
	        sql.append("\n        FROM ctas_banco cbpc ");
	        
           
	        sql.append("\n    WHERE cbpc.no_persona = cast(m.no_cliente as integer) ");	       
	        
	        sql.append("\n            and cbpc.id_divisa = 'DLS' ) ");
	        sql.append("          as NumCheq_CruceDLS, ");
	        
	        sql.append("\n      ( SELECT coalesce(count(c.id_chequera), 0) ");
	        sql.append("\n        FROM cat_cta_banco c ");
	        sql.append("\n        WHERE c.no_empresa = m.no_empresa ");
	        sql.append("\n            and c.id_divisa = m.id_divisa ) as NumCheqEmp, ");
	                  
	        sql.append("\n      ( SELECT coalesce(count(b.id_chequera), 0) ");
	        sql.append("\n        FROM ctas_banco b ");
	        sql.append("\n        WHERE b.no_persona = m.no_cliente ");
	        sql.append("\n            and b.id_divisa = m.id_divisa ) as NumCheqCte, m.id_servicio_be, m.id_contable ");
	        
	        sql.append("\n  ,(select equivale_persona from persona p where p.no_persona = m.no_cliente and p.id_tipo_persona = 'P') as equivale,");
	        sql.append("\n  coalesce(m.no_cheque, 0) as no_cheque, coalesce(am.usuario_uno, 0) as usuario_uno, coalesce(am.usuario_dos, 0) as usuario_dos ");
	        
	      //Agregado EMS 18/12/2015: Nuevas fechas solicitadas
	        sql.append( " ,m.fecha_contabilizacion, m.fec_operacion ");
		    
	        //Color - EMS 28/01/2016
		    sql.append( " ,case when m.NO_COBRADOR is not null then");
		    sql.append( "	'NARANJA' ");
		    sql.append( "	 END as color ");
		    //31/01/2016
		    sql.append( "	 ,invoice_type ");
		    
	        sql.append("\n  FROM movimiento m ");
	        sql.append("\n      LEFT JOIN cat_banco b ON ( m.id_banco_benef = b.id_banco )");
	        sql.append("\n      LEFT JOIN cat_estatus e ON (m.id_estatus_mov = e.id_estatus ");
	        sql.append("\n          AND e.clasificacion = 'MOV' ) ");
	        sql.append("\n      LEFT JOIN cat_caja cj ON (m.id_caja = cj.id_caja) ");
	        sql.append("\n      LEFT JOIN autorizacionesMov am ON (m.no_folio_det = am.no_folio_det), ");
	        sql.append("\n      cat_tipo_operacion co, cat_cve_operacion cv, ");
	        sql.append("\n      cat_forma_pago fp ");
	        
	        /*if(dto.getPsDivision()!=null && !dto.getPsDivision().equals("")) 
	        	sql.append( "\n   ,cat_cta_banco_division ccbd ");
	    */
        	sql.append( "\n  WHERE ");
	    
		    if(dto.getIdGrupoEmpresa() > 0) 
		    {
		        sql.append( "\n  m.no_empresa in (select no_empresa from grupo_empresa ");
		        sql.append( "\n                   where id_grupo_flujo = "+Utilerias.validarCadenaSQL(dto.getIdGrupoEmpresa())+") and ");
		    }
		    sql.append( "\n  m.no_cliente in ("+Utilerias.validarCadenaSQL(consultarConfiguraSet(206))+") ");
		    sql.append( "\n  AND m.id_tipo_movto = 'E'  ");
		    sql.append( "\n  AND (m.origen_mov <> 'INV' or m.origen_mov is null)");
		    sql.append( "\n  AND m.id_tipo_operacion = co.id_tipo_operacion ");
		    sql.append( "\n  AND co.no_empresa = 1 ");
		    sql.append( "\n  AND m.id_cve_operacion = cv.id_cve_operacion ");
		    sql.append( "\n  AND m.id_estatus_mov in ('N','C','F','L') ");
		    sql.append( "\n  AND m.id_forma_pago = fp.id_forma_pago ");
		    sql.append( "\n  AND m.id_forma_pago in (1,3,5,7,9,10)");
		    sql.append("\n   and (m.PO_HEADERS = '' or m.PO_HEADERS is null)");
		    /*sql.append( "\n  AND m.no_folio_det in ");
		    sql.append( "\n      ( SELECT mm.folio_ref ");
		    sql.append( "\n       FROM movimiento mm ");
		    sql.append( "\n        WHERE mm.folio_ref = m.no_folio_det ) ");*/
		    
		    sql.append( "\n  And m.cve_control = '"+Utilerias.validarCadenaSQL(dto.getCveControl())+"' ");
		    
		    /*if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
		    {
		        sql.append( "\n  AND m.no_empresa = ccbd.no_empresa ");
		        sql.append( "\n  AND m.id_chequera = ccbd.id_chequera ");
		        sql.append( "\n  AND m.id_banco = ccbd.id_banco ");
		        sql.append( "\n  AND m.division = ccbd.id_division ");
		    }*/
		    
		    if(dto.getPdOrigenMov()!=null && !dto.getPdOrigenMov().equals("0"))
		            sql.append( "\n	  AND m.origen_mov='"+Utilerias.validarCadenaSQL(dto.getPdOrigenMov())+"'");
	        sql.append( "\n  order by no_cheque, beneficiario, fec_valor_original "); //Se pidio ordenar por sociedad
	        
	        System.out.println("query que saca el detalle: " + sql);
	        
	        listDetalle= jdbcTemplate.query(sql.toString(), new RowMapper<MovimientoDto>(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons = new MovimientoDto();
					cons.setNoDocto(rs.getString("no_docto"));
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setImporteOriginal(rs.getDouble("importe_original"));
					cons.setDescEstatus(rs.getString("desc_estatus"));
					cons.setNoFactura(rs.getString("no_factura"));
					cons.setFecValorStr(funciones.ponerFechaSola(rs.getDate("fec_valor")));
					cons.setFecValorOriginalStr(funciones.ponerFechaSola(rs.getDate("fec_valor_original")));
					cons.setDescCveOperacion(rs.getString("desc_cve_operacion"));
					cons.setImporte(rs.getDouble("importe"));
					cons.setIdDivisa(rs.getString("id_divisa"));
					cons.setIdFormaPago(rs.getInt("id_forma_pago"));
					cons.setDescFormaPago(rs.getString("desc_forma_pago"));
					cons.setDescBancoBenef(rs.getString("desc_banco"));//Descripcion banco beneficiario
					cons.setIdChequeraBenef(rs.getString("id_chequera_benef"));
					cons.setBeneficiario(rs.getString("beneficiario"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setNoFolioDet(rs.getInt("no_folio_det"));
					cons.setNoFolioMov(rs.getInt("no_folio_mov"));
					cons.setNoCuenta(rs.getInt("no_cuenta"));
					cons.setIdChequera(rs.getString("id_chequera"));
					cons.setIdCveOperacion(rs.getInt("id_cve_operacion"));
					cons.setIdFormaPago(rs.getInt("id_forma_pago"));
					cons.setIdBancoBenef(rs.getInt("id_banco_benef"));
					cons.setIdCaja(rs.getInt("id_caja"));
					cons.setIdLeyenda(rs.getString("id_leyenda"));
					cons.setIdEstatusMov(rs.getString("id_estatus_mov"));
					cons.setNoCliente(rs.getString("no_cliente"));
					cons.setTipoCambio(rs.getDouble("tipo_cambio"));
					cons.setOrigenMov(rs.getString("origen_mov"));
					cons.setSolicita(rs.getString("solicita"));
					cons.setAutoriza(rs.getString("autoriza"));
					cons.setObservacion(rs.getString("observacion"));
					cons.setLoteEntrada(rs.getInt("lote_entrada"));
					cons.setDescCaja(rs.getString("desc_caja"));
					cons.setAgrupa1(rs.getString("agrupa1"));
					cons.setAgrupa2(rs.getString("agrupa2"));
					cons.setIdRubro(rs.getDouble("id_rubro"));
					cons.setAgrupa3(rs.getString("agrupa3"));
					cons.setBServicios(rs.getString("b_servicios"));
					cons.setNoPedido(rs.getInt("no_pedido"));
					cons.setImporteMn(rs.getDouble("importe_mn"));
					cons.setNomProveedor(rs.getString("nom_proveedor"));
					cons.setFecPropuestaStr(funciones.ponerFechaSola(rs.getDate("fec_propuesta")));
					cons.setIdDivisaOriginal(rs.getString("id_divisa_original"));
					cons.setNomEmpresa(rs.getString("nom_empresa"));
					cons.setIdBanco(rs.getInt("id_banco_pago"));
					cons.setIdChequera(rs.getString("id_chequera_pago"));
					cons.setBancoPago(rs.getString("banco_pago"));
					cons.setDescGrupoCupo(rs.getString("desc_grupo_cupo")); 
					cons.setDiasInv(rs.getInt("dias"));
					cons.setNumCheqPagoMN(rs.getInt("NumCheq_PagoMN"));
					cons.setNumCheqPagoDLS(rs.getInt("NumCheq_PagoDLS"));	
					cons.setNumCheqCruceMN(rs.getInt("NumCheq_CruceMN"));
					cons.setNumCheqCruceDLS(rs.getInt("NumCheq_CruceDLS"));
					cons.setNumCheqEmp(rs.getInt("NumCheqEmp"));
					cons.setNumCheqCte(rs.getInt("NumCheqCte"));
					cons.setIdServicioBe(rs.getString("id_servicio_be"));
					cons.setIdContable(rs.getString("id_contable"));
					cons.setEquivale(rs.getString("equivale"));
					cons.setNoCheque(rs.getInt("no_cheque"));
					cons.setUsr1(rs.getInt("usuario_uno"));
					cons.setUsr2(rs.getInt("usuario_dos"));
					cons.setFecOperacionStr(funciones.ponerFechaSola(rs.getDate("fec_operacion")));
					cons.setFecContabilizacionStr((rs.getDate("fecha_contabilizacion")!= null)?funciones.ponerFechaSola(rs.getDate("fecha_contabilizacion" )):"");
					cons.setColor(rs.getString("color"));
					cons.setInvoiceType(rs.getString("invoice_type"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:consultarDetalle");
			 
		}
		return listDetalle;
	}


	
public List<BloqueoPagoCruzadoDto> obtenerBloqueoSAP(String cveControl, String noDocto) {
		
		StringBuffer sql= new StringBuffer();
		List<BloqueoPagoCruzadoDto> result = new ArrayList<BloqueoPagoCruzadoDto>();
		
		try{
			
            sql.append("\n SELECT m.cod_bloqueo ");
            sql.append("\n FROM MOVIMIENTO m ");
            sql.append("\n WHERE m.cve_control = '" + Utilerias.validarCadenaSQL(cveControl)+ "' ");
            sql.append("\n AND m.no_docto = '" + Utilerias.validarCadenaSQL(noDocto) + "' ");
            sql.append("\n AND m.cod_bloqueo = 'S' ");
            
	        //system.out.println("Query de buscar bloqueo SAP : " + sql.toString());
	        
	        result = jdbcTemplate.query(sql.toString(), new RowMapper<BloqueoPagoCruzadoDto>(){
				public BloqueoPagoCruzadoDto mapRow(ResultSet rs, int idx) throws SQLException {
					BloqueoPagoCruzadoDto cons = new BloqueoPagoCruzadoDto();
					cons.setMotivo(rs.getString("cod_bloqueo")); //retorna S o N o null
					return cons;
				}});
            
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:obtenerBloqueoProveedor");
		}
		return result;
	}
	
	
public List<BloqueoPagoCruzadoDto> obtenerBloqueoProveedor(String noEmpresa, String noProv, String cveControl) {
		
		StringBuffer sql= new StringBuffer();
		List<BloqueoPagoCruzadoDto> result = new ArrayList<BloqueoPagoCruzadoDto>();
		
		try{
			
			sql.append("\n SELECT b.motivo, b.no_docto ");
            sql.append("\n FROM BLOQUEO_PROVEEDOR b ");
            sql.append("\n WHERE b.no_empresa = '" + Utilerias.validarCadenaSQL(noEmpresa) + "' ");
            sql.append("\n AND b.no_proveedor = '" + Utilerias.validarCadenaSQL(noProv) + "' ");
            
            //Para identificar bloqueos de SAP
            if(!cveControl.equals("")){
            	sql.append("\n UNION ALL ");
                
                sql.append("\n SELECT DISTINCT 'SAP' as motivo, null as no_docto ");			
                sql.append("\n FROM MOVIMIENTO m JOIN PERSONA p on m.no_cliente = p.no_persona AND p.id_tipo_persona = 'P' ");
                sql.append("\n WHERE m.no_empresa in("+ Utilerias.validarCadenaSQL(noEmpresa) +")");
                sql.append("\n AND m.CVE_CONTROL = '" + Utilerias.validarCadenaSQL(cveControl) + "' ");
                sql.append("\n AND p.equivale_persona = '" + Utilerias.validarCadenaSQL(noProv) + "' ");
                sql.append("\n AND (p.id_estatus_per = 'I' OR m.COD_BLOQUEO = 'S') ");  
                sql.append("\n AND m.ID_TIPO_OPERACION = 3000 ");
                
            }
            
	        //system.out.println("Query de buscar bloqueo provedor 1: " + sql.toString());
	        
	        result = jdbcTemplate.query(sql.toString(), new RowMapper<BloqueoPagoCruzadoDto>(){
				public BloqueoPagoCruzadoDto mapRow(ResultSet rs, int idx) throws SQLException {
					BloqueoPagoCruzadoDto cons = new BloqueoPagoCruzadoDto();
					cons.setMotivo(rs.getString("motivo"));
					cons.setNoDocumento(rs.getString("no_docto"));
					return cons;
				}});
            
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:obtenerBloqueoProveedor");
		}
		return result;
	}


	public List<MovimientoDto> consultarPagProp(PagosPropuestosDto dto){
		System.out.println("1");
		String sql="";
	    String sEnc="";
	    String sCND="";
	    double pdTipoCambio=0;
	    String conSoloCheques="";
	    List<MovimientoDto>listRet= new ArrayList<MovimientoDto>();
	    
		try{
			
		    if(dto.getCveControl()!=null && dto.getCveControl().equals(""))
		    {
		        if(obtenerTipoCambio(consultarConfiguraSet(772),obtenerFechaHoy())>0)
		            pdTipoCambio = obtenerTipoCambio(consultarConfiguraSet(772),obtenerFechaHoy());
		        else
		            pdTipoCambio = 1;
		    }
		    // esto se manda llamar solo si la peticion viene con la opcion de que el usuario solo puede ver propuestas que
		    // contengan unicamente cheques
		    
		    conSoloCheques = "\n	where cve_control in (select cve_control from movimiento ";
		    conSoloCheques +="\n    where id_forma_pago = 1 and id_tipo_operacion = 3000 ";
		    conSoloCheques +="\n     and id_estatus_mov in ('N', 'C') and no_empresa in ";
		    conSoloCheques +="\n     (SELECT no_empresa FROM usuario_empresa WHERE no_usuario = " + dto.getIdUsuario() +")";
		    conSoloCheques +="\n     and cve_control is not null) ";
		    conSoloCheques +="\n	 and cve_control not in (select cve_control from movimiento ";
		    conSoloCheques +="\n     where id_forma_pago <> 1 and id_tipo_operacion = 3000 ";
		    conSoloCheques +="\n     and id_estatus_mov in ('N', 'C') and no_empresa in ";
		    conSoloCheques +="\n     (SELECT no_empresa FROM usuario_empresa WHERE no_usuario = "+ dto.getIdUsuario() +")";
		    conSoloCheques +="\n     and cve_control is not null)";

		    sEnc = "";
	        if(dto.getCveControl()!=null && dto.getCveControl().equals("")) 
	        {
	            sEnc+="\n        SELECT max(e.emp_che) as AgrupaCheEmp , m.cve_control, max(fecha_propuesta) as fec_propuesta, ";
	            
            	if(dto.getCveControl()!=null && !dto.getCveControl().equals("")) //este if asi esta el codigo VB
	                sEnc+="\n            sag.id_division as desc_grupo_flujo, cgc.desc_grupo_cupo, ";
                else
	                sEnc+="\n            cgf.desc_grupo_flujo, cgc.desc_grupo_cupo, ";
            
	            
	            sEnc+="\n            sum(case when m.id_divisa = 'MN' then importe else 0 end) as importe_mn, ";
	            sEnc+="\n            sum(case when m.id_divisa = 'DLS' then importe else 0 end) as importe_dls, ";
	            sEnc+="\n            sum(case when m.id_divisa = 'DLS' then m.importe else case when m.id_divisa = 'MN' then m.importe / "+pdTipoCambio+" else m.importe end end) as total_dls, ";
	            sEnc+="\n            sag.id_grupo_flujo, gfc.id_grupo_cupo, coalesce(sag.concepto,'') as concepto ";
	        }else
	        	sEnc+="\n        SELECT m.cve_control, m.id_banco, m.id_chequera, m.fec_propuesta, m.no_empresa ";
	        
	        sEnc+="\n             ,( SELECT count(*) ";
            sEnc+="\n                FROM movimiento m ";
            sEnc+="\n                WHERE m.id_estatus_mov = 'L' ";
            sEnc+="\n                    AND m.id_tipo_movto = 'E' ";
            sEnc+="\n                    AND m.id_tipo_operacion between 3800 and 3899 ";
            sEnc+="\n                    AND m.cve_control = sag.cve_control ";
            sEnc+="\n              ) as NumIntercos, ";
            sEnc+="\n              ( SELECT coalesce(sum(m.importe),0) ";
            sEnc+="\n                FROM movimiento m ";
            sEnc+="\n                WHERE m.id_estatus_mov = 'L' ";
            sEnc+="\n                    AND m.id_tipo_movto = 'E' ";
            sEnc+="\n                    AND m.id_tipo_operacion between 3800 and 3899 ";
            sEnc+="\n                    AND m.cve_control = sag.cve_control ";
            sEnc+="\n              ) as TotalIntercos ";
          
            sEnc+="\n              , max(m.id_banco_benef) as id_banco_benef, max(m.fec_recalculo) as fec_recalculo ";
            sEnc+="\n			   ,MAX(Case When Sag.Fecha_Pago Is not Null Or Sag.Fecha_Pago <>''  Then 'AZUL'  End) As Color ";
            sEnc+="\n          ,(case when cp.no_empresa is null and cp.bco_cve is null and cp.id_chequera  is null and";
    		sEnc+="\n 			m.id_forma_pago in (1,10) then 'ROJO' else 'BLANCO' end) as chequera_cheque ";
            
            sEnc+="\n        FROM ";
            
            if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
            	sEnc+="\n            movimiento m, grupo_flujo_cupo gfc,";
	        else{  
	            sEnc+="\n Movimiento M Left Join Control_Papel Cp On (M.No_Empresa = Cp.No_Empresa And";
        		sEnc+="\n        M.Id_Banco = Cp.Bco_Cve And";
				sEnc+="\n		 M.Id_Chequera = Cp.Id_Chequera And";
				sEnc+="\n        M.id_forma_pago In (1,10) and cp.estatus = 'A' and cp.tipo_folio = 'C'), cat_grupo_flujo cgf, grupo_flujo_cupo gfc, ";
	        }
	        sEnc+="\n            cat_grupo_cupo cgc, seleccion_automatica_grupo sag, empresa e ";
	        
	        if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
	            sEnc+="\n        WHERE ";
	        else
	            sEnc+="\n        WHERE sag.id_grupo_flujo = cgf.id_grupo_flujo ";
	        
	        sCND = "";

	        if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
               sCND+="\n m.cve_control = sag.cve_control ";
            else
               sCND+="\n AND m.cve_control = sag.cve_control ";
            
           sCND+="\n     AND m.id_rubro = gfc.id_rubro ";
           sCND+="\n     AND gfc.id_grupo_cupo = cgc.id_grupo_cupo ";
           sCND+="\n     AND m.id_tipo_movto = 'E' ";
           sCND+="\n     AND (m.origen_mov <> 'INV' or m.origen_mov is null) ";
           sCND+="\n	 AND m.origen_mov <> 'CVT'";
           sCND+="\n     AND m.no_empresa = e.no_empresa ";
           
           if(dto.isBLocal())//Se seleccionan s�lo empresas locales
        	   sCND+="\n     AND e.b_local = 'S' ";
	        
	       if(dto.isBTambienIntercos())
	           sCND+="\n AND m.id_estatus_mov in ('N','C','F','L') ";
	       else
	           sCND+="\n AND m.id_estatus_mov in ('N','C','F') ";
	       
	       sCND+="\n	and (m.po_headers is null or m.po_headers ='')";
	       sCND+="\n     AND ((m.id_forma_pago = 3 and m.id_leyenda <> '*') or ";
	       sCND+="\n	(m.no_cliente = (select max(p.no_persona) from  persona p  join Conf_Pago_Cruzado cxp on p.equivale_persona = cxp.no_proveedor where p.id_tipo_persona ='P')) or";
    	   sCND+="\n         (m.id_forma_pago in (1, 5, 6, 7, 9, 10))) ";
    	   sCND+="\n     AND (m.id_tipo_operacion = 3000 or (m.id_tipo_operacion between 3800 and 3899)) ";
	       
		    if((dto.getFechaIni()!=null && !dto.getFechaIni().equals(""))&&(dto.getFechaFin()!=null && !dto.getFechaFin().equals(""))) 
		       sCND+="\n AND sag.fecha_propuesta between convert(datetime,'"+Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFechaIni()))+"', 103) and convert(datetime,'"+Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFechaFin()))+"', 103) ";
		    else if (dto.getFechaFin()!=null && !dto.getFechaFin().equals(""))
		       sCND+="\n AND sag.fecha_propuesta <= convert(datetime,'"+Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFechaFin()))+"', 103) ";
		    else if (dto.getFechaIni()!=null && !dto.getFechaIni().equals(""))
		       sCND+="\n AND sag.fecha_propuesta >= convert(datetime,'"+Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFechaIni()))+"', 103) ";
		    
		   	sCND+="\n     AND m.cve_control is not null ";
		    
		    if (dto.getIdGrupoEmpresa()>0)
		       sCND+="\n AND sag.id_grupo_flujo = "+dto.getIdGrupoEmpresa();
		    
		    if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
		    {
		        if(dto.getPsDivision().equals("TODAS LAS DIVISIONES"))
		           sCND+="\n AND ( not sag.id_division is null and sag.id_division <> '' ) ";
		        else
		           sCND+="\n AND sag.id_division = '"+Utilerias.validarCadenaSQL(dto.getPsDivision())+"' ";
		    }
		    
		    if(dto.getCveControl()!=null && !dto.getCveControl().equals(""))
		       sCND+="\n AND sag.cve_control = '"+Utilerias.validarCadenaSQL(dto.getCveControl())+"'";
		 
		    if(dto.getIdUsuario() > 0 )
		    {
		      if(dto.getPsDivision()!=null && !dto.getPsDivision().equals("")) 
		      {
		    	  
		    	  sCND+="\n AND m.division in ( SELECT id_division ";
		    	  sCND+="\n                        FROM usuario_division ";
		    	  sCND+="\n        WHERE no_usuario = "+ dto.getIdUsuario() +")";
		      }
		      else
		      {
	           sCND+="\n AND m.no_empresa in ( SELECT no_empresa ";
	           sCND+="\n                       FROM usuario_empresa ";
	           sCND+="\n                       WHERE no_usuario = "+ dto.getIdUsuario() +")";
		      }
		      //sCND = sCND & Chr(10) & " AND (((select count(*) from usuario_area WHERE no_usuario = " & pvi_Usuario & ") = 0) or "
		      //sCND = sCND & Chr(10) & "     (id_area in (select id_area from usuario_area where no_usuario = " & pvi_Usuario & ")))"
		    }
		    
		    if(dto.isBSoloUsrAct())
		    {
		        if(dto.isBSoloManuales())
		           sCND+="\n AND m.cve_control like 'M%"+ Utilerias.validarCadenaSQL(dto.getIdUsuario()) +"' ";
		        else
		           sCND+="\n AND m.cve_control like '%"+ Utilerias.validarCadenaSQL(dto.getIdUsuario()) +"' ";
		    }
		    
		    if(dto.getCveControl()!=null && dto.getCveControl().equals(""))
		    {
		    	if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
		           sCND+="\n GROUP BY m.cve_control, sag.id_division, cgc.desc_grupo_cupo, ";
		        else
		           sCND+="\n GROUP BY m.cve_control, cgf.desc_grupo_flujo, cgc.desc_grupo_cupo, ";
		        
		       sCND+="\n     sag.id_grupo_flujo, gfc.id_grupo_cupo, sag.concepto, sag.cve_control";
		       sCND+="\n, (Case When Cp.No_Empresa Is Null And Cp.Bco_Cve Is Null And Cp.Id_Chequera  Is Null And M.Id_Forma_Pago In (1,10) Then 'ROJO' Else 'BLANCO' End) ";
		    }
		    else{
		       sCND+="\n GROUP BY m.cve_control, m.id_banco, m.id_chequera, m.fec_propuesta, m.no_empresa, sag.cve_control";
		       sCND+="\n, (Case When Cp.No_Empresa Is Null And Cp.Bco_Cve Is Null And Cp.Id_Chequera  Is Null And M.Id_Forma_Pago In (1,10) Then 'ROJO' Else 'BLANCO' End) ";
		    }
		    if(dto.getAutorizaPropuesta()!= null && dto.getAutorizaPropuesta().equals("SI"))
		    {
		    	sql="";
		    	if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
		    	{
		    		sql+= sEnc + sCND;
		    		if(dto.getCveControl()!= null && dto.getCveControl().equals(""));
	                	sql+=" ORDER BY fec_propuesta ";
		    	}else{
		    		if(dto.isBSoloUsrAct())
		    		{
		    			if(gsDBM!=null && !gsDBM.equals("SYBASE")) 
		                {
		                	sql+= " SELECT max(case when AgrupaCheEmp is null then '' when AgrupaCheEmp = 0 then 'N' else 'S' end) as AgrupaCheEmp, cve_control, max(fec_propuesta) as fec_propuesta, ";
		                    sql+= "     desc_grupo_flujo, desc_grupo_cupo, sum(importe_mn) as importe_mn, ";
		                    sql+= "     sum(importe_dls) as importe_dls, sum(total_dls) as total_dls, ";
		                    sql+= "     id_grupo_flujo , id_grupo_cupo, concepto, ";
		                    sql+= "     sum(NumIntercos) as NumIntercos, ";
		                    //sql+= "     coalesce(sum(TotalIntercos),0) as TotalIntercos, max(color) as color ";  
		                    sql+= "     coalesce(sum(TotalIntercos),0) as TotalIntercos, max(color) as color, max(chequera_cheque) as chequera_cheque   ";
		                    sql+= " FROM ( ";
		                }
		                    
		                sql+= sEnc;
		                sql+= " AND cgf.nivel_autorizacion = 0 ";
		                sql+= sCND;
		                sql+= " UNION ALL ";
		                sql+= sEnc;
		                sql+= " AND cgf.nivel_autorizacion = 1 ";
		                sql+= " AND sag.usuario_uno is null ";
		                sql+= sCND;
		                sql+= " UNION ALL ";
		                sql+= sEnc;
		                sql+= " AND cgf.nivel_autorizacion = 2 ";
		                sql+= " AND sag.usuario_uno is null ";
		                sql+= " AND sag.usuario_dos is null ";
		                sql+= sCND;
		                sql+= " UNION ALL ";
		                sql+= sEnc;
		                sql+= " AND cgf.nivel_autorizacion = 3 ";
		                sql+= " AND sag.usuario_uno is null ";
		                sql+= " AND sag.usuario_dos is null ";
		                sql+= " AND sag.usuario_tres is null ";
		                sql+= sCND;
		                
		                if(gsDBM!=null && !gsDBM.equals("SYBASE"))
		                {
		                	sql+= " ) a ";
		                    if(dto.isBSoloCheques()) 
		                    	sql+= conSoloCheques;
		                    
		                    sql+=" GROUP BY cve_control, desc_grupo_flujo, desc_grupo_cupo, ";
		                    sql+="     id_grupo_flujo, id_grupo_cupo, concepto ";
		                    sql+=" ORDER BY fec_propuesta ";
		                }
		    		}else{
		                if(gsDBM!=null && !gsDBM.equals("SYBASE")) 
		                {
		                	sql+= " SELECT max(case when AgrupaCheEmp is null then '' when AgrupaCheEmp = 0 then 'N' else 'S' end) as AgrupaCheEmp, cve_control, max(fec_propuesta) as fec_propuesta, ";
		                    sql+= "     desc_grupo_flujo, desc_grupo_cupo, sum(importe_mn) as importe_mn, ";
		                    sql+= "     sum(importe_dls) as importe_dls, sum(total_dls) as total_dls, ";
		                    sql+= "     id_grupo_flujo, id_grupo_cupo, concepto, ";
		                    sql+= "     sum(NumIntercos) as NumIntercos, ";
		                    //sql+= "     coalesce(sum(TotalIntercos),0) as TotalIntercos ";
		                    //sql+= "     coalesce(sum(TotalIntercos),0) as TotalIntercos, max(color) as color";//, max(chequera_cheque) as chequera_cheque ";
		                    sql+= "     coalesce(sum(TotalIntercos),0) as TotalIntercos, max(color) as color, max(chequera_cheque) as chequera_cheque   ";
		                    sql+= " FROM ( ";
		                }
		                
		                sql+= sEnc;
		                sql+= " AND cgf.nivel_autorizacion = 0 ";
		                sql+= sCND;
		                sql+= " UNION ALL ";
		                sql+= sEnc;
		                sql+= " AND cgf.nivel_autorizacion = 1 ";
		                sql+= " AND not sag.usuario_uno is null ";
		                sql+= sCND;
		                sql+= " UNION ALL ";
		                sql+= sEnc;
		                sql+= " AND cgf.nivel_autorizacion = 2 ";
		                sql+= " AND not sag.usuario_uno is null ";
		                sql+= " AND not sag.usuario_dos is null ";
		                sql+= sCND;
		                sql+= " UNION ALL ";
		                sql+= sEnc;
		                sql+= " AND cgf.nivel_autorizacion = 3 ";
		                sql+= " AND not sag.usuario_uno is null ";
		                sql+= " AND not sag.usuario_dos is null ";
		                sql+= " AND not sag.usuario_tres is null ";
		                sql+= sCND;
		                
		                if(gsDBM!=null && !gsDBM.equals("SYBASE")) 
		                {
		                    sql+= " ) a ";
		                    if(dto.isBSoloCheques())
		                        sql+= conSoloCheques;

		                    sql+= " GROUP BY cve_control, desc_grupo_flujo, desc_grupo_cupo, ";
		                    sql+= "     id_grupo_flujo, id_grupo_cupo, concepto ";
		                    sql+= " ORDER BY fec_propuesta ";
		                }
		                else
		                  sql+= " AT ISOLATION READ UNCOMMITTED";	
		    		}
		    	}
		    }else{
		    	sql = "";
	            sql+= sEnc + sCND;
	            if(dto.getCveControl()!=null && dto.getCveControl().equals(""))
	                sql+=" ORDER BY fec_propuesta ";
		    }
		   System.out.println("consulta del grid "+ sql);
		    if(dto.getAutorizaPropuesta()!= null && dto.getAutorizaPropuesta().equals("SI"))//se realiza if por id_banco_benef y fec_recalculo
		    {
		    	listRet= jdbcTemplate.query(sql, new RowMapper<MovimientoDto>(){
					public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
						MovimientoDto cons = new MovimientoDto();
						
							cons.setEmpCheStr(rs.getString("AgrupaCheEmp"));
							cons.setCveControl(rs.getString("cve_control"));
							cons.setFecPropuesta(rs.getDate("fec_propuesta"));
							System.out.println(rs.getDate("fec_propuesta"));
							cons.setDescGrupoFlujo(rs.getString("desc_grupo_flujo"));
							cons.setDescGrupoCupo(rs.getString("desc_grupo_cupo"));
							cons.setImporteMn(rs.getDouble("importe_mn"));
							cons.setImporteDls(rs.getDouble("importe_dls"));
							cons.setTotalDls(rs.getDouble("total_dls"));
							cons.setIdGrupoCupo(rs.getString("id_grupo_cupo"));
							cons.setIdGrupoFlujo(rs.getString("id_grupo_flujo"));
							cons.setConcepto(rs.getString("concepto"));
							cons.setNumIntercos(rs.getInt("NumIntercos"));
							cons.setTotalIntercos(rs.getInt("TotalIntercos"));
							cons.setColor(rs.getString("color"));
							cons.setIdChequeraCte(rs.getString("chequera_cheque"));
						return cons;
					}});
		    }else{
				listRet= jdbcTemplate.query(sql, new RowMapper<MovimientoDto>(){
					public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
						MovimientoDto cons = new MovimientoDto();
						
							cons.setEmpCheStr(rs.getString("AgrupaCheEmp"));
							cons.setCveControl(rs.getString("cve_control"));
							cons.setFecPropuesta(rs.getDate("fec_propuesta"));
							cons.setDescGrupoFlujo(rs.getString("desc_grupo_flujo"));
							cons.setDescGrupoCupo(rs.getString("desc_grupo_cupo"));
							cons.setImporteMn(rs.getDouble("importe_mn"));
							cons.setImporteDls(rs.getDouble("importe_dls"));
							cons.setTotalDls(rs.getDouble("total_dls"));
							cons.setIdGrupoCupo(rs.getString("id_grupo_cupo"));
							cons.setIdGrupoFlujo(rs.getString("id_grupo_flujo"));
							cons.setConcepto(rs.getString("concepto"));
						    cons.setIdBancoBenef(rs.getInt("id_banco_benef"));
							cons.setFecRecalculo(rs.getDate("fec_recalculo"));
							cons.setNumIntercos(rs.getInt("NumIntercos"));
							cons.setTotalIntercos(rs.getInt("TotalIntercos"));
							cons.setColor(rs.getString("color"));
							cons.setIdChequeraCte(rs.getString("chequera_cheque"));
						return cons;
					}});
		    }
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagoPropuestasDao, M:consultarPagProp");
			e.printStackTrace();
		}
		return listRet;
	}
	
	public List<Map<String, String>> consultaPagosExcel(String clave){
		List<Map<String, String>> listaResultado = new ArrayList<Map<String, String>>();
		StringBuffer sql= new StringBuffer();	
		System.out.println("clabe dao"+clave);
		try{
			clave = clave.substring(0,clave.length()-1 );
			sql.append( "  SELECT distinct m.cve_control,m.no_docto, m.no_empresa, importe_original, ");
		    sql.append( "\n      e.desc_estatus, m.no_factura, m.fec_valor, ");
		    sql.append( "\n      m.fec_valor_original, cv.desc_cve_operacion, ");
		    sql.append( "\n      m.importe AS importe, ");
		    sql.append( "\n      m.id_divisa, m.id_forma_pago, fp.desc_forma_pago, ");
		    sql.append( "\n      b.desc_banco, m.id_chequera_benef, m.beneficiario, ");
		    sql.append( "\n      m.concepto, m.no_folio_det, m.no_folio_mov, ");
		    sql.append( "\n      m.no_cuenta, m.id_chequera, cv.id_cve_operacion, ");
		    sql.append( "\n      m.id_forma_pago, m.id_banco_benef, m.id_caja, ");
		    sql.append( "\n      m.id_leyenda, m.id_estatus_mov, m.no_cliente, ");
		    sql.append( "\n      m.tipo_cambio, m.origen_mov, m.solicita, ");
		    sql.append( "\n      m.autoriza, m.observacion, m.lote_entrada, ");
		    sql.append( "\n      cj.desc_caja, m.agrupa1, m.agrupa2, m.id_rubro, ");
		    sql.append( "\n      m.agrupa3, p.b_servicios, m.no_pedido, ");
		    sql.append( "\n      CASE WHEN m.id_divisa = 'MN' THEN m.importe ");
		    sql.append( "\n           ELSE m.importe * coalesce( ");
		    sql.append( "\n               ( SELECT max(vd.valor) ");
		    //sql.append( "\n               ( SELECT vd.valor ");
		    sql.append( "\n                 FROM valor_divisa vd ");
		    sql.append( "\n                 WHERE vd.id_divisa = m.id_divisa ");
		    sql.append( "\n                     and vd.fec_divisa =  convert(datetime,'"+Utilerias.validarCadenaSQL(funciones.ponerFecha(consultarFechaHoy()).substring(0,10)) +"', 103) ), ");
		    sql.append( "\n           1) end as importe_mn, ");
		    sql.append( "\n      ( SELECT max(per.nombre_corto) ");
		    //sql.append( "\n      ( SELECT per.nombre_corto ");
		    sql.append( "\n        FROM persona per ");
		    
		    sql.append( "\n	 WHERE cast(m.no_cliente as integer) = per.no_persona ");
		    
		    sql.append( "\n            and per.id_tipo_persona = 'P' ) as nom_proveedor, ");
		    sql.append( "\n      m.fec_propuesta, m.id_divisa_original,  ");
		    sql.append( "\n      ( SELECT max(e.nom_empresa) ");
		    //sql.append( "\n      ( SELECT e.nom_empresa ");
		    sql.append( "\n        FROM empresa e ");
		    sql.append( "\n        WHERE e.no_empresa = m.no_empresa ) as nom_empresa, ");
		    sql.append( "\n      m.id_banco as id_banco_pago, ");
		    sql.append( "\n      m.id_chequera as id_chequera_pago, ");
		    sql.append( "\n      ( SELECT max(cb.desc_banco) ");
		    //sql.append( "\n      ( SELECT cb.desc_banco ");
		    sql.append( "\n        FROM cat_banco cb ");
		    sql.append( "\n        WHERE cb.id_banco = m.id_banco) as banco_pago, ");
		    sql.append( "\n      ( SELECT max(cg.desc_grupo_cupo) ");
		    //sql.append( "\n      ( SELECT cg.desc_grupo_cupo ");
		    sql.append( "\n        FROM cat_grupo_cupo cg ");
		    sql.append( "\n        WHERE cg.id_grupo_cupo = ");
		    sql.append( "\n            ( SELECT max(gfc.id_grupo_cupo) ");
		    //sql.append( "\n            ( SELECT gfc.id_grupo_cupo ");
		    sql.append( "\n              FROM grupo_flujo_cupo gfc ");
		    sql.append( "\n             WHERE gfc.id_rubro in ");
		    sql.append( "\n                  ( SELECT max(mm.id_rubro) ");
		    //sql.append( "\n                  ( SELECT mm.id_rubro ");
		    sql.append( "\n                    FROM movimiento mm ");
		    sql.append( "\n                    WHERE mm.folio_ref = m.no_folio_det ) ) ) ");
		    sql.append( "\n      as desc_grupo_cupo, ");
		    
		    sql.append( "\n      convert(datetime,'"+Utilerias.validarCadenaSQL(funciones.ponerFecha(consultarFechaHoy()).substring(0,10)) +"', 103) - m.fec_valor_original as dias,");
		    
		    sql.append( "\n     ( SELECT coalesce(count(ccbv.id_divisa), 0) ");
		    sql.append( "\n       FROM cat_cta_banco ccbv ");
		    sql.append( "\n       Where ccbv.no_empresa = m.no_empresa ");
		    sql.append( "\n           AND ccbv.id_divisa = 'MN' ) ");
		    sql.append( "\n     as NumCheq_PagoMN, ");
		    
		    sql.append( "\n     ( SELECT coalesce(count(ccbv.id_divisa), 0) ");
		    sql.append( "\n       FROM cat_cta_banco ccbv ");
		    sql.append( "\n       Where ccbv.no_empresa = m.no_empresa ");
		    sql.append( "\n           AND ccbv.id_divisa = 'DLS' ) ");
		    sql.append( "\n     as NumCheq_PagoDLS, ");

		    sql.append( "\n      ( SELECT coalesce(count(cbpc.id_divisa), 0) ");
		    sql.append( "\n        FROM ctas_banco cbpc ");
		    
	        sql.append("\n    WHERE cbpc.no_persona = cast(m.no_cliente as integer)");
		    
		    sql.append("\n      and cbpc.id_divisa = 'MN' ) ");
		    sql.append("\n      as NumCheq_CruceMN, ");
		    
		    sql.append("\n      ( SELECT coalesce(count(cbpc.id_divisa), 0) ");
		    sql.append("\n        FROM ctas_banco cbpc ");
		     
	        sql.append( "    WHERE cbpc.no_persona = cast(m.no_cliente as integer)");
		    
		    sql.append( "            and cbpc.id_divisa = 'DLS' ) ");
		    sql.append( "      as NumCheq_CruceDLS, ");
		    
		    sql.append( "      ( SELECT coalesce(count(c.id_chequera), 0) ");
		    sql.append( "        FROM cat_cta_banco c ");
		    sql.append( "        WHERE c.no_empresa = m.no_empresa ");
		    sql.append( "            and c.id_divisa = m.id_divisa ) as NumCheqEmp, ");
		              
		    sql.append( "      ( SELECT coalesce(count(b.id_chequera), 0) ");
		    sql.append( "        FROM ctas_banco b ");
		    sql.append( "        WHERE b.no_persona = m.no_cliente ");
		    sql.append( "            and b.id_divisa = m.id_divisa ) as NumCheqCte, m.id_servicio_be, m.id_contable ");
		    
		    sql.append( "        ,(select equivale_persona from persona p where p.no_persona = m.no_cliente and p.id_tipo_persona = 'P') as equivale,");
		    sql.append( "       coalesce(m.no_cheque, 0) as no_cheque, coalesce(am.usuario_uno, 0) as usuario_uno, coalesce(am.usuario_dos, 0) as usuario_dos ");
		    
		    //Agregado EMS 18/12/2015: Nuevas fechas solicitadas
		    sql.append( " ,m.fecha_contabilizacion, m.fec_operacion ");
		    //Color EMS 28/01/2016
		    sql.append( " ,case when m.NO_COBRADOR is not null then");
		    sql.append( "	'NARANJA' ");
		    sql.append( "	 END as color ");
		    
		    sql.append( "	 ,invoice_type ");
		    
		    sql.append( "  FROM movimiento m ");
		    sql.append( "      LEFT JOIN cat_banco b ON ( m.id_banco_benef = b.id_banco )");
		    sql.append( "      LEFT JOIN cat_estatus e ON (m.id_estatus_mov = e.id_estatus ");
		    sql.append( "          AND e.clasificacion = 'MOV' ) ");
		    sql.append( "      LEFT JOIN cat_caja cj ON (m.id_caja = cj.id_caja) ");
		    sql.append( "      LEFT JOIN autorizacionesMov am ON (m.no_folio_det = am.no_folio_det) ");
		    
	        sql.append("\n	  LEFT JOIN proveedor p ON (cast(m.no_cliente as integer) = p.no_proveedor), ");
		    
		    sql.append("\n      cat_tipo_operacion co, cat_cve_operacion cv, ");
		    sql.append("\n      cat_forma_pago fp ");
		    
		    /*if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
		    	sql.append( "   ,cat_cta_banco_division ccbd ");
	    	*/
		    
	        sql.append( "\n  WHERE ");
	    
		   
		    
		    sql.append( "\n	   m.no_cliente not in (" + Utilerias.validarCadenaSQL(consultarConfiguraSet(206))+") ");
		    sql.append( "\n	  AND m.id_tipo_movto = 'E'  ");
		    sql.append( "\n	  AND (m.origen_mov <> 'INV' or m.origen_mov is NULL)");
		    sql.append( "\n	  AND m.id_tipo_operacion = co.id_tipo_operacion ");
		    sql.append( "\n	  AND co.no_empresa = 1 ");
		    sql.append( "\n	  AND m.id_cve_operacion = cv.id_cve_operacion ");
		    sql.append( "\n	  AND m.id_estatus_mov in ('N','C','F','L') ");
		    sql.append( "\n	  AND m.id_forma_pago = fp.id_forma_pago ");
		    sql.append( "\n	  AND m.id_forma_pago in (1,3,5,6,7,9,10) ");
		    sql.append("\n   and (m.PO_HEADERS = '' or m.PO_HEADERS is null)");
		    /*sql.append( "\n	  AND m.no_folio_det in ");//se comento porque no permitia ver los detalles de pagos parcializados porque no coincidian los folios
		    sql.append( "\n	      ( SELECT mm.folio_ref ");
		    sql.append( "\n	        FROM movimiento mm ");
		    sql.append( "\n	        WHERE mm.folio_ref = m.no_folio_det ) ");*/
		    
		    //sql.append( "\n		  And m.cve_control in ("+Utilerias.validarCadenaSQL(clave)+") ");
		    sql.append( "\n		  And m.cve_control in ("+clave+") ");

		    /*if(dto.getPsDivision()!=null && !dto.getPsDivision().equals("")) 
		    {
		        sql.append( "\n	  AND m.no_empresa = ccbd.no_empresa ");
		        sql.append( "\n	  AND m.id_chequera = ccbd.id_chequera ");
		        sql.append( "\n	  AND m.id_banco = ccbd.id_banco ");
		        sql.append( "\n	  AND m.division = ccbd.id_division ");
		    }*/
		   
//	        if(dto.getPdOrigenMov()!=null && !dto.getPdOrigenMov().equals(""))
//	            sql.append( "\n  AND m.origen_mov='"+Utilerias.validarCadenaSQL(dto.getPdOrigenMov())+"'");
//	        
	        sql.append( "\n	  UNION ALL ");
            
	        sql.append( "\n	  SELECT distinct m.cve_control,m.no_docto, m.no_empresa, importe_original, ");
	        sql.append( "\n      e.desc_estatus, m.no_factura, m.fec_valor, ");
	        sql.append( "\n      m.fec_valor_original, cv.desc_cve_operacion, ");
	        sql.append( "\n      m.importe AS importe, ");
	        sql.append( "\n      m.id_divisa, m.id_forma_pago, fp.desc_forma_pago, ");
	        sql.append( "\n      b.desc_banco, m.id_chequera_benef, m.beneficiario, ");
	        sql.append( "\n      m.concepto, m.no_folio_det, m.no_folio_mov, ");
	        sql.append( "\n      m.no_cuenta, m.id_chequera, cv.id_cve_operacion, ");
	        sql.append( "\n      m.id_forma_pago, m.id_banco_benef, m.id_caja, ");
	        sql.append( "\n      m.id_leyenda, m.id_estatus_mov, m.no_cliente, ");
	        sql.append( "\n      m.tipo_cambio, m.origen_mov, m.solicita, ");
	        sql.append( "\n      m.autoriza, m.observacion, m.lote_entrada, ");
	        sql.append( "\n      cj.desc_caja, m.agrupa1, m.agrupa2, m.id_rubro, ");
	        sql.append( "\n      m.agrupa3, '' as b_servicios, m.no_pedido, ");
	        sql.append( "\n      CASE WHEN m.id_divisa = 'MN' THEN m.importe ");
	        sql.append( "\n           ELSE m.importe * coalesce( ");
	        sql.append( "\n              ( SELECT DISTINCT vd.valor ");
	        sql.append( "\n                 FROM valor_divisa vd ");
	        sql.append( "\n                 WHERE vd.id_divisa = m.id_divisa ");
	        sql.append( "\n                     and vd.fec_divisa = convert(datetime,'"+Utilerias.validarCadenaSQL(funciones.ponerFecha(consultarFechaHoy()).substring(0,10)) 
	        														    +"', 103) ), ");
	        sql.append( "\n           1) end as importe_mn, ");
	        sql.append( "\n      'PROVEEDOR INCIDENTAL' as nom_proveedor, ");
	        sql.append( "\n      m.fec_propuesta, m.id_divisa_original,  ");
	        sql.append( "\n      ( SELECT e.nom_empresa ");
	        sql.append( "\n        FROM empresa e ");
	        sql.append( "\n        WHERE e.no_empresa = m.no_empresa ) as nom_empresa, ");
	        sql.append( "\n      m.id_banco as id_banco_pago, ");
	        sql.append( "\n      m.id_chequera as id_chequera_pago, ");
	        sql.append( "\n      ( SELECT cb.desc_banco ");
	        sql.append( "\n        FROM cat_banco cb ");
	        sql.append( "\n        WHERE cb.id_banco = m.id_banco) as banco_pago, ");
	        sql.append( "\n      ( SELECT cg.desc_grupo_cupo ");
	        sql.append( "\n        FROM cat_grupo_cupo cg ");
	        sql.append( "\n        WHERE cg.id_grupo_cupo = ");
	        sql.append( "\n           ( SELECT gfc.id_grupo_cupo ");
	        sql.append( "\n              FROM grupo_flujo_cupo gfc ");
	        sql.append( "\n              WHERE gfc.id_rubro in ");
	        sql.append( "\n                  ( SELECT mm.id_rubro ");
	        sql.append( "\n                    FROM movimiento mm ");
	        sql.append( "\n                    WHERE mm.folio_ref = m.no_folio_det ) ) ) ");
	        sql.append( "\n      as desc_grupo_cupo, ");

	        sql.append( "\n      convert(datetime,'"+Utilerias.validarCadenaSQL(funciones.ponerFecha(consultarFechaHoy()).substring(0,10)) 
	        						 +"', 103) - m.fec_valor_original as dias,");
	        
	        sql.append( "\n     ( SELECT coalesce(count(ccbv.id_divisa), 0) ");
	        sql.append( "\n       FROM cat_cta_banco ccbv ");
	        sql.append( "\n       Where ccbv.no_empresa = m.no_empresa ");
	        sql.append( "\n           AND ccbv.id_divisa = 'MN' ) ");
	        sql.append( "     as NumCheq_PagoMN, ");
	        
	        sql.append( "\n     ( SELECT coalesce(count(ccbv.id_divisa), 0) ");
	        sql.append( "\n      FROM cat_cta_banco ccbv ");
	        sql.append( "\n       Where ccbv.no_empresa = m.no_empresa ");
	        sql.append( "\n           AND ccbv.id_divisa = 'DLS' ) ");
	        sql.append( "     as NumCheq_PagoDLS, ");

	        sql.append( "\n      ( SELECT coalesce(count(cbpc.id_divisa), 0) ");
	        sql.append( "\n        FROM ctas_banco cbpc ");
	        

        	sql.append( "\n    WHERE cbpc.no_persona = cast(m.no_cliente as integer)");
 	        
	        sql.append("\n            and cbpc.id_divisa = 'MN' ) ");
	        sql.append("\n      as NumCheq_CruceMN, ");
	        
	        sql.append("\n      ( SELECT coalesce(count(cbpc.id_divisa), 0) ");
	        sql.append("\n        FROM ctas_banco cbpc ");
	        
           
	        sql.append("\n    WHERE cbpc.no_persona = cast(m.no_cliente as integer) ");	       
	        
	        sql.append("\n            and cbpc.id_divisa = 'DLS' ) ");
	        sql.append("          as NumCheq_CruceDLS, ");
	        
	        sql.append("\n      ( SELECT coalesce(count(c.id_chequera), 0) ");
	        sql.append("\n        FROM cat_cta_banco c ");
	        sql.append("\n        WHERE c.no_empresa = m.no_empresa ");
	        sql.append("\n            and c.id_divisa = m.id_divisa ) as NumCheqEmp, ");
	                  
	        sql.append("\n      ( SELECT coalesce(count(b.id_chequera), 0) ");
	        sql.append("\n        FROM ctas_banco b ");
	        sql.append("\n        WHERE b.no_persona = m.no_cliente ");
	        sql.append("\n            and b.id_divisa = m.id_divisa ) as NumCheqCte, m.id_servicio_be, m.id_contable ");
	        
	        sql.append("\n  ,(select equivale_persona from persona p where p.no_persona = m.no_cliente and p.id_tipo_persona = 'P') as equivale,");
	        sql.append("\n  coalesce(m.no_cheque, 0) as no_cheque, coalesce(am.usuario_uno, 0) as usuario_uno, coalesce(am.usuario_dos, 0) as usuario_dos ");
	        
	      //Agregado EMS 18/12/2015: Nuevas fechas solicitadas
	        sql.append( " ,m.fecha_contabilizacion, m.fec_operacion ");
		    
	        //Color - EMS 28/01/2016
		    sql.append( " ,case when m.NO_COBRADOR is not null then");
		    sql.append( "	'NARANJA' ");
		    sql.append( "	 END as color ");
		    //31/01/2016
		    sql.append( "	 ,invoice_type ");
		    
	        sql.append("\n  FROM movimiento m ");
	        sql.append("\n      LEFT JOIN cat_banco b ON ( m.id_banco_benef = b.id_banco )");
	        sql.append("\n      LEFT JOIN cat_estatus e ON (m.id_estatus_mov = e.id_estatus ");
	        sql.append("\n          AND e.clasificacion = 'MOV' ) ");
	        sql.append("\n      LEFT JOIN cat_caja cj ON (m.id_caja = cj.id_caja) ");
	        sql.append("\n      LEFT JOIN autorizacionesMov am ON (m.no_folio_det = am.no_folio_det), ");
	        sql.append("\n      cat_tipo_operacion co, cat_cve_operacion cv, ");
	        sql.append("\n      cat_forma_pago fp ");
	        
	        /*if(dto.getPsDivision()!=null && !dto.getPsDivision().equals("")) 
	        	sql.append( "\n   ,cat_cta_banco_division ccbd ");
	    */
        	sql.append( "\n  WHERE ");
	    
		   /* if(dto.getIdGrupoEmpresa() > 0) 
		    {
		        sql.append( "\n  m.no_empresa in (select no_empresa from grupo_empresa ");
		        sql.append( "\n                   where id_grupo_flujo = "+Utilerias.validarCadenaSQL(dto.getIdGrupoEmpresa())+") and ");
		    }*/
		    sql.append( "\n  m.no_cliente in ("+Utilerias.validarCadenaSQL(consultarConfiguraSet(206))+") ");
		    sql.append( "\n  AND m.id_tipo_movto = 'E'  ");
		    sql.append( "\n  AND (m.origen_mov <> 'INV' or m.origen_mov is null)");
		    sql.append( "\n  AND m.id_tipo_operacion = co.id_tipo_operacion ");
		    sql.append( "\n  AND co.no_empresa = 1 ");
		    sql.append( "\n  AND m.id_cve_operacion = cv.id_cve_operacion ");
		    sql.append( "\n  AND m.id_estatus_mov in ('N','C','F','L') ");
		    sql.append( "\n  AND m.id_forma_pago = fp.id_forma_pago ");
		    sql.append( "\n  AND m.id_forma_pago in (1,3,5,7,9,10)");
		    sql.append("\n   and (m.PO_HEADERS = '' or m.PO_HEADERS is null)");
		    
		    /*sql.append( "\n  AND m.no_folio_det in ");
		    sql.append( "\n      ( SELECT mm.folio_ref ");
		    sql.append( "\n       FROM movimiento mm ");
		    sql.append( "\n        WHERE mm.folio_ref = m.no_folio_det ) ");*/
		    
		    //sql.append( "\n		  And m.cve_control in ("+Utilerias.validarCadenaSQL(clave)+") ");
		    sql.append( "\n		  And m.cve_control in ("+clave+") ");
		    /*if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
		    {
		        sql.append( "\n  AND m.no_empresa = ccbd.no_empresa ");
		        sql.append( "\n  AND m.id_chequera = ccbd.id_chequera ");
		        sql.append( "\n  AND m.id_banco = ccbd.id_banco ");
		        sql.append( "\n  AND m.division = ccbd.id_division ");
		    }*/
		    
		   /* if(dto.getPdOrigenMov()!=null && !dto.getPdOrigenMov().equals("0"))
		            sql.append( "\n	  AND m.origen_mov='"+Utilerias.validarCadenaSQL(dto.getPdOrigenMov())+"'");*/
	        sql.append( "\n  order by no_cheque, beneficiario, fec_valor_original "); //Se pidio ordenar por sociedad
	        
	        System.out.println("query que saca el detalle: " + sql);
	        listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>(){
				public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException{
					Map<String, String> campos = new HashMap<String, String>();
					campos.put("cve_control", rs.getString("cve_control"));
					campos.put("no_docto", rs.getString("no_docto"));
					campos.put("importe", rs.getString("importe"));
					campos.put("id_divisa", rs.getString("id_divisa"));
					campos.put("nom_empresa", rs.getString("nom_empresa"));
					campos.put("beneficiario", rs.getString("beneficiario"));
					return campos;
				}				
				
			});	

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagoPropuestasDao, M:consultarPagProp");
			e.printStackTrace();
		}
		return listaResultado;
	}
	
	public List<MovimientoDto>consultarDetalle(PagosPropuestosDto dto){
		StringBuffer sql= new StringBuffer();
		
		List<MovimientoDto>listDetalle= new ArrayList<MovimientoDto>();
		try{
			sql.append( "  SELECT distinct m.no_docto, m.no_empresa, importe_original, ");
		    sql.append( "\n      e.desc_estatus, m.no_factura, m.fec_valor, ");
		    sql.append( "\n      m.fec_valor_original, cv.desc_cve_operacion, ");
		    sql.append( "\n      m.importe AS importe, ");
		    sql.append( "\n      m.id_divisa, m.id_forma_pago, fp.desc_forma_pago, ");
		    sql.append( "\n      b.desc_banco, m.id_chequera_benef, m.beneficiario, ");
		    sql.append( "\n      m.concepto, m.no_folio_det, m.no_folio_mov, ");
		    sql.append( "\n      m.no_cuenta, m.id_chequera, cv.id_cve_operacion, ");
		    sql.append( "\n      m.id_forma_pago, m.id_banco_benef, m.id_caja, ");
		    sql.append( "\n      m.id_leyenda, m.id_estatus_mov, m.no_cliente, ");
		    sql.append( "\n      m.tipo_cambio, m.origen_mov, m.solicita, ");
		    sql.append( "\n      m.autoriza, m.observacion, m.lote_entrada, ");
		    sql.append( "\n      cj.desc_caja, m.agrupa1, m.agrupa2, m.id_rubro, ");
		    sql.append( "\n      m.agrupa3, p.b_servicios, m.no_pedido, ");
		    sql.append( "\n      CASE WHEN m.id_divisa = 'MN' THEN m.importe ");
		    sql.append( "\n           ELSE m.importe * coalesce( ");
		    sql.append( "\n               ( SELECT max(vd.valor) ");
		    //sql.append( "\n               ( SELECT vd.valor ");
		    sql.append( "\n                 FROM valor_divisa vd ");
		    sql.append( "\n                 WHERE vd.id_divisa = m.id_divisa ");
		    sql.append( "\n                     and vd.fec_divisa =  convert(datetime,'"+Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFecha()).substring(0,10)) +"', 103) ), ");
		    sql.append( "\n           1) end as importe_mn, ");
		    sql.append( "\n      ( SELECT max(per.nombre_corto) ");
		    //sql.append( "\n      ( SELECT per.nombre_corto ");
		    sql.append( "\n        FROM persona per ");
		    
		    sql.append( "\n	 WHERE cast(m.no_cliente as integer) = per.no_persona ");
		    
		    sql.append( "\n            and per.id_tipo_persona = 'P' ) as nom_proveedor, ");
		    sql.append( "\n      m.fec_propuesta, m.id_divisa_original,  ");
		    sql.append( "\n      ( SELECT max(e.nom_empresa) ");
		    //sql.append( "\n      ( SELECT e.nom_empresa ");
		    sql.append( "\n        FROM empresa e ");
		    sql.append( "\n        WHERE e.no_empresa = m.no_empresa ) as nom_empresa, ");
		    sql.append( "\n      m.id_banco as id_banco_pago, ");
		    sql.append( "\n      m.id_chequera as id_chequera_pago, ");
		    sql.append( "\n      ( SELECT max(cb.desc_banco) ");
		    //sql.append( "\n      ( SELECT cb.desc_banco ");
		    sql.append( "\n        FROM cat_banco cb ");
		    sql.append( "\n        WHERE cb.id_banco = m.id_banco) as banco_pago, ");
		    sql.append( "\n      ( SELECT max(cg.desc_grupo_cupo) ");
		    //sql.append( "\n      ( SELECT cg.desc_grupo_cupo ");
		    sql.append( "\n        FROM cat_grupo_cupo cg ");
		    sql.append( "\n        WHERE cg.id_grupo_cupo = ");
		    sql.append( "\n            ( SELECT max(gfc.id_grupo_cupo) ");
		    //sql.append( "\n            ( SELECT gfc.id_grupo_cupo ");
		    sql.append( "\n              FROM grupo_flujo_cupo gfc ");
		    sql.append( "\n             WHERE gfc.id_rubro in ");
		    sql.append( "\n                  ( SELECT max(mm.id_rubro) ");
		    //sql.append( "\n                  ( SELECT mm.id_rubro ");
		    sql.append( "\n                    FROM movimiento mm ");
		    sql.append( "\n                    WHERE mm.folio_ref = m.no_folio_det ) ) ) ");
		    sql.append( "\n      as desc_grupo_cupo, ");
		    
		    sql.append( "\n      convert(datetime,'"+Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFecha()).substring(0,10)) +"', 103) - m.fec_valor_original as dias,");
		    
		    sql.append( "\n     ( SELECT coalesce(count(ccbv.id_divisa), 0) ");
		    sql.append( "\n       FROM cat_cta_banco ccbv ");
		    sql.append( "\n       Where ccbv.no_empresa = m.no_empresa ");
		    sql.append( "\n           AND ccbv.id_divisa = 'MN' ) ");
		    sql.append( "\n     as NumCheq_PagoMN, ");
		    
		    sql.append( "\n     ( SELECT coalesce(count(ccbv.id_divisa), 0) ");
		    sql.append( "\n       FROM cat_cta_banco ccbv ");
		    sql.append( "\n       Where ccbv.no_empresa = m.no_empresa ");
		    sql.append( "\n           AND ccbv.id_divisa = 'DLS' ) ");
		    sql.append( "\n     as NumCheq_PagoDLS, ");

		    sql.append( "\n      ( SELECT coalesce(count(cbpc.id_divisa), 0) ");
		    sql.append( "\n        FROM ctas_banco cbpc ");
		    
	        sql.append("\n    WHERE cbpc.no_persona = cast(m.no_cliente as integer)");
		    
		    sql.append("\n      and cbpc.id_divisa = 'MN' ) ");
		    sql.append("\n      as NumCheq_CruceMN, ");
		    
		    sql.append("\n      ( SELECT coalesce(count(cbpc.id_divisa), 0) ");
		    sql.append("\n        FROM ctas_banco cbpc ");
		     
	        sql.append( "    WHERE cbpc.no_persona = cast(m.no_cliente as integer)");
		    
		    sql.append( "            and cbpc.id_divisa = 'DLS' ) ");
		    sql.append( "      as NumCheq_CruceDLS, ");
		    
		    sql.append( "      ( SELECT coalesce(count(c.id_chequera), 0) ");
		    sql.append( "        FROM cat_cta_banco c ");
		    sql.append( "        WHERE c.no_empresa = m.no_empresa ");
		    sql.append( "            and c.id_divisa = m.id_divisa ) as NumCheqEmp, ");
		              
		    sql.append( "      ( SELECT coalesce(count(b.id_chequera), 0) ");
		    sql.append( "        FROM ctas_banco b ");
		    sql.append( "        WHERE b.no_persona = m.no_cliente ");
		    sql.append( "            and b.id_divisa = m.id_divisa ) as NumCheqCte, m.id_servicio_be, m.id_contable ");
		    
		    sql.append( "        ,(select equivale_persona from persona p where p.no_persona = m.no_cliente and p.id_tipo_persona = 'P') as equivale,");
		    sql.append( "       coalesce(m.no_cheque, 0) as no_cheque, coalesce(am.usuario_uno, 0) as usuario_uno, coalesce(am.usuario_dos, 0) as usuario_dos ");
		    
		    //Agregado EMS 18/12/2015: Nuevas fechas solicitadas
		    sql.append( " ,m.fecha_contabilizacion, m.fec_operacion ");
		    //Color EMS 28/01/2016
		    sql.append( " ,case when m.NO_COBRADOR is not null then");
		    sql.append( "	'NARANJA' ");
		    sql.append( "	 END as color ");
		    
		    sql.append( "	 ,invoice_type ");
		    
		    sql.append( "  FROM movimiento m ");
		    sql.append( "      LEFT JOIN cat_banco b ON ( m.id_banco_benef = b.id_banco )");
		    sql.append( "      LEFT JOIN cat_estatus e ON (m.id_estatus_mov = e.id_estatus ");
		    sql.append( "          AND e.clasificacion = 'MOV' ) ");
		    sql.append( "      LEFT JOIN cat_caja cj ON (m.id_caja = cj.id_caja) ");
		    sql.append( "      LEFT JOIN autorizacionesMov am ON (m.no_folio_det = am.no_folio_det) ");
		    
	        sql.append("\n	  LEFT JOIN proveedor p ON (cast(m.no_cliente as integer) = p.no_proveedor), ");
		    
		    sql.append("\n      cat_tipo_operacion co, cat_cve_operacion cv, ");
		    sql.append("\n      cat_forma_pago fp ");
		    
		    /*if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
		    	sql.append( "   ,cat_cta_banco_division ccbd ");
	    	*/
		    
	        sql.append( "\n  WHERE ");
	    
		    if(dto.getIdGrupoEmpresa()>0)
		    {
		        sql.append( "  m.no_empresa in (select no_empresa from grupo_empresa ");
		        sql.append( "\n                   where id_grupo_flujo = "+Utilerias.validarCadenaSQL(dto.getIdGrupoEmpresa())+") ");
		    }
		    
		    sql.append( "\n	  AND m.no_cliente not in (" + Utilerias.validarCadenaSQL(consultarConfiguraSet(206))+") ");
		    sql.append( "\n	  AND m.id_tipo_movto = 'E'  ");
		    sql.append( "\n	  AND (m.origen_mov <> 'INV' or m.origen_mov is NULL)");
		    sql.append( "\n	  AND m.id_tipo_operacion = co.id_tipo_operacion ");
		    sql.append( "\n	  AND co.no_empresa = 1 ");
		    sql.append( "\n	  AND m.id_cve_operacion = cv.id_cve_operacion ");
		    sql.append( "\n	  AND m.id_estatus_mov in ('N','C','F','L') ");
		    sql.append( "\n	  AND m.id_forma_pago = fp.id_forma_pago ");
		    sql.append( "\n	  AND m.id_forma_pago in (1,3,5,6,7,9,10) ");
		    sql.append("\n   and (m.PO_HEADERS = '' or m.PO_HEADERS is null)");
		    /*sql.append( "\n	  AND m.no_folio_det in ");//se comento porque no permitia ver los detalles de pagos parcializados porque no coincidian los folios
		    sql.append( "\n	      ( SELECT mm.folio_ref ");
		    sql.append( "\n	        FROM movimiento mm ");
		    sql.append( "\n	        WHERE mm.folio_ref = m.no_folio_det ) ");*/
		    
		    sql.append( "\n		  And m.cve_control = '"+Utilerias.validarCadenaSQL(dto.getCveControl())+"' ");

		    /*if(dto.getPsDivision()!=null && !dto.getPsDivision().equals("")) 
		    {
		        sql.append( "\n	  AND m.no_empresa = ccbd.no_empresa ");
		        sql.append( "\n	  AND m.id_chequera = ccbd.id_chequera ");
		        sql.append( "\n	  AND m.id_banco = ccbd.id_banco ");
		        sql.append( "\n	  AND m.division = ccbd.id_division ");
		    }*/
		   
	        if(dto.getPdOrigenMov()!=null && !dto.getPdOrigenMov().equals(""))
	            sql.append( "\n  AND m.origen_mov='"+Utilerias.validarCadenaSQL(dto.getPdOrigenMov())+"'");
	        
	        sql.append( "\n	  UNION ALL ");
            
	        sql.append( "\n	  SELECT distinct m.no_docto, m.no_empresa, importe_original, ");
	        sql.append( "\n      e.desc_estatus, m.no_factura, m.fec_valor, ");
	        sql.append( "\n      m.fec_valor_original, cv.desc_cve_operacion, ");
	        sql.append( "\n      m.importe AS importe, ");
	        sql.append( "\n      m.id_divisa, m.id_forma_pago, fp.desc_forma_pago, ");
	        sql.append( "\n      b.desc_banco, m.id_chequera_benef, m.beneficiario, ");
	        sql.append( "\n      m.concepto, m.no_folio_det, m.no_folio_mov, ");
	        sql.append( "\n      m.no_cuenta, m.id_chequera, cv.id_cve_operacion, ");
	        sql.append( "\n      m.id_forma_pago, m.id_banco_benef, m.id_caja, ");
	        sql.append( "\n      m.id_leyenda, m.id_estatus_mov, m.no_cliente, ");
	        sql.append( "\n      m.tipo_cambio, m.origen_mov, m.solicita, ");
	        sql.append( "\n      m.autoriza, m.observacion, m.lote_entrada, ");
	        sql.append( "\n      cj.desc_caja, m.agrupa1, m.agrupa2, m.id_rubro, ");
	        sql.append( "\n      m.agrupa3, '' as b_servicios, m.no_pedido, ");
	        sql.append( "\n      CASE WHEN m.id_divisa = 'MN' THEN m.importe ");
	        sql.append( "\n           ELSE m.importe * coalesce( ");
	        sql.append( "\n              ( SELECT DISTINCT vd.valor ");
	        sql.append( "\n                 FROM valor_divisa vd ");
	        sql.append( "\n                 WHERE vd.id_divisa = m.id_divisa ");
	        sql.append( "\n                     and vd.fec_divisa = convert(datetime,'"+Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFecha()).substring(0,10)) 
	        														    +"', 103) ), ");
	        sql.append( "\n           1) end as importe_mn, ");
	        sql.append( "\n      'PROVEEDOR INCIDENTAL' as nom_proveedor, ");
	        sql.append( "\n      m.fec_propuesta, m.id_divisa_original,  ");
	        sql.append( "\n      ( SELECT e.nom_empresa ");
	        sql.append( "\n        FROM empresa e ");
	        sql.append( "\n        WHERE e.no_empresa = m.no_empresa ) as nom_empresa, ");
	        sql.append( "\n      m.id_banco as id_banco_pago, ");
	        sql.append( "\n      m.id_chequera as id_chequera_pago, ");
	        sql.append( "\n      ( SELECT cb.desc_banco ");
	        sql.append( "\n        FROM cat_banco cb ");
	        sql.append( "\n        WHERE cb.id_banco = m.id_banco) as banco_pago, ");
	        sql.append( "\n      ( SELECT cg.desc_grupo_cupo ");
	        sql.append( "\n        FROM cat_grupo_cupo cg ");
	        sql.append( "\n        WHERE cg.id_grupo_cupo = ");
	        sql.append( "\n           ( SELECT gfc.id_grupo_cupo ");
	        sql.append( "\n              FROM grupo_flujo_cupo gfc ");
	        sql.append( "\n              WHERE gfc.id_rubro in ");
	        sql.append( "\n                  ( SELECT mm.id_rubro ");
	        sql.append( "\n                    FROM movimiento mm ");
	        sql.append( "\n                    WHERE mm.folio_ref = m.no_folio_det ) ) ) ");
	        sql.append( "\n      as desc_grupo_cupo, ");

	        sql.append( "\n      convert(datetime,'"+Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFecha()).substring(0,10)) 
	        						 +"', 103) - m.fec_valor_original as dias,");
	        
	        sql.append( "\n     ( SELECT coalesce(count(ccbv.id_divisa), 0) ");
	        sql.append( "\n       FROM cat_cta_banco ccbv ");
	        sql.append( "\n       Where ccbv.no_empresa = m.no_empresa ");
	        sql.append( "\n           AND ccbv.id_divisa = 'MN' ) ");
	        sql.append( "     as NumCheq_PagoMN, ");
	        
	        sql.append( "\n     ( SELECT coalesce(count(ccbv.id_divisa), 0) ");
	        sql.append( "\n      FROM cat_cta_banco ccbv ");
	        sql.append( "\n       Where ccbv.no_empresa = m.no_empresa ");
	        sql.append( "\n           AND ccbv.id_divisa = 'DLS' ) ");
	        sql.append( "     as NumCheq_PagoDLS, ");

	        sql.append( "\n      ( SELECT coalesce(count(cbpc.id_divisa), 0) ");
	        sql.append( "\n        FROM ctas_banco cbpc ");
	        

        	sql.append( "\n    WHERE cbpc.no_persona = cast(m.no_cliente as integer)");
 	        
	        sql.append("\n            and cbpc.id_divisa = 'MN' ) ");
	        sql.append("\n      as NumCheq_CruceMN, ");
	        
	        sql.append("\n      ( SELECT coalesce(count(cbpc.id_divisa), 0) ");
	        sql.append("\n        FROM ctas_banco cbpc ");
	        
           
	        sql.append("\n    WHERE cbpc.no_persona = cast(m.no_cliente as integer) ");	       
	        
	        sql.append("\n            and cbpc.id_divisa = 'DLS' ) ");
	        sql.append("          as NumCheq_CruceDLS, ");
	        
	        sql.append("\n      ( SELECT coalesce(count(c.id_chequera), 0) ");
	        sql.append("\n        FROM cat_cta_banco c ");
	        sql.append("\n        WHERE c.no_empresa = m.no_empresa ");
	        sql.append("\n            and c.id_divisa = m.id_divisa ) as NumCheqEmp, ");
	                  
	        sql.append("\n      ( SELECT coalesce(count(b.id_chequera), 0) ");
	        sql.append("\n        FROM ctas_banco b ");
	        sql.append("\n        WHERE b.no_persona = m.no_cliente ");
	        sql.append("\n            and b.id_divisa = m.id_divisa ) as NumCheqCte, m.id_servicio_be, m.id_contable ");
	        
	        sql.append("\n  ,(select equivale_persona from persona p where p.no_persona = m.no_cliente and p.id_tipo_persona = 'P') as equivale,");
	        sql.append("\n  coalesce(m.no_cheque, 0) as no_cheque, coalesce(am.usuario_uno, 0) as usuario_uno, coalesce(am.usuario_dos, 0) as usuario_dos ");
	        
	      //Agregado EMS 18/12/2015: Nuevas fechas solicitadas
	        sql.append( " ,m.fecha_contabilizacion, m.fec_operacion ");
		    
	        //Color - EMS 28/01/2016
		    sql.append( " ,case when m.NO_COBRADOR is not null then");
		    sql.append( "	'NARANJA' ");
		    sql.append( "	 END as color ");
		    //31/01/2016
		    sql.append( "	 ,invoice_type ");
		    
	        sql.append("\n  FROM movimiento m ");
	        sql.append("\n      LEFT JOIN cat_banco b ON ( m.id_banco_benef = b.id_banco )");
	        sql.append("\n      LEFT JOIN cat_estatus e ON (m.id_estatus_mov = e.id_estatus ");
	        sql.append("\n          AND e.clasificacion = 'MOV' ) ");
	        sql.append("\n      LEFT JOIN cat_caja cj ON (m.id_caja = cj.id_caja) ");
	        sql.append("\n      LEFT JOIN autorizacionesMov am ON (m.no_folio_det = am.no_folio_det), ");
	        sql.append("\n      cat_tipo_operacion co, cat_cve_operacion cv, ");
	        sql.append("\n      cat_forma_pago fp ");
	        
	        /*if(dto.getPsDivision()!=null && !dto.getPsDivision().equals("")) 
	        	sql.append( "\n   ,cat_cta_banco_division ccbd ");
	    */
        	sql.append( "\n  WHERE ");
	    
		    if(dto.getIdGrupoEmpresa() > 0) 
		    {
		        sql.append( "\n  m.no_empresa in (select no_empresa from grupo_empresa ");
		        sql.append( "\n                   where id_grupo_flujo = "+Utilerias.validarCadenaSQL(dto.getIdGrupoEmpresa())+") and ");
		    }
		    sql.append( "\n  m.no_cliente in ("+Utilerias.validarCadenaSQL(consultarConfiguraSet(206))+") ");
		    sql.append( "\n  AND m.id_tipo_movto = 'E'  ");
		    sql.append( "\n  AND (m.origen_mov <> 'INV' or m.origen_mov is null)");
		    sql.append( "\n  AND m.id_tipo_operacion = co.id_tipo_operacion ");
		    sql.append( "\n  AND co.no_empresa = 1 ");
		    sql.append( "\n  AND m.id_cve_operacion = cv.id_cve_operacion ");
		    sql.append( "\n  AND m.id_estatus_mov in ('N','C','F','L') ");
		    sql.append( "\n  AND m.id_forma_pago = fp.id_forma_pago ");
		    sql.append( "\n  AND m.id_forma_pago in (1,3,5,7,9,10)");
		    sql.append("\n   and (m.PO_HEADERS = '' or m.PO_HEADERS is null)");
		    
		    /*sql.append( "\n  AND m.no_folio_det in ");
		    sql.append( "\n      ( SELECT mm.folio_ref ");
		    sql.append( "\n       FROM movimiento mm ");
		    sql.append( "\n        WHERE mm.folio_ref = m.no_folio_det ) ");*/
		    
		    sql.append( "\n  And m.cve_control = '"+Utilerias.validarCadenaSQL(dto.getCveControl())+"' ");
		    
		    /*if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
		    {
		        sql.append( "\n  AND m.no_empresa = ccbd.no_empresa ");
		        sql.append( "\n  AND m.id_chequera = ccbd.id_chequera ");
		        sql.append( "\n  AND m.id_banco = ccbd.id_banco ");
		        sql.append( "\n  AND m.division = ccbd.id_division ");
		    }*/
		    
		    if(dto.getPdOrigenMov()!=null && !dto.getPdOrigenMov().equals("0"))
		            sql.append( "\n	  AND m.origen_mov='"+Utilerias.validarCadenaSQL(dto.getPdOrigenMov())+"'");
	        sql.append( "\n  order by no_cheque, beneficiario, fec_valor_original "); //Se pidio ordenar por sociedad
	        
	        System.out.println("query que saca el detalle: " + sql);
	        
	        listDetalle= jdbcTemplate.query(sql.toString(), new RowMapper<MovimientoDto>(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons = new MovimientoDto();
					cons.setNoDocto(rs.getString("no_docto"));
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setImporteOriginal(rs.getDouble("importe_original"));
					cons.setDescEstatus(rs.getString("desc_estatus"));
					cons.setNoFactura(rs.getString("no_factura"));
					cons.setFecValorStr(funciones.ponerFechaSola(rs.getDate("fec_valor")));
					cons.setFecValorOriginalStr(funciones.ponerFechaSola(rs.getDate("fec_valor_original")));
					cons.setDescCveOperacion(rs.getString("desc_cve_operacion"));
					cons.setImporte(rs.getDouble("importe"));
					cons.setIdDivisa(rs.getString("id_divisa"));
					cons.setIdFormaPago(rs.getInt("id_forma_pago"));
					cons.setDescFormaPago(rs.getString("desc_forma_pago"));
					cons.setDescBancoBenef(rs.getString("desc_banco"));//Descripcion banco beneficiario
					cons.setIdChequeraBenef(rs.getString("id_chequera_benef"));
					cons.setBeneficiario(rs.getString("beneficiario"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setNoFolioDet(rs.getInt("no_folio_det"));
					cons.setNoFolioMov(rs.getInt("no_folio_mov"));
					cons.setNoCuenta(rs.getInt("no_cuenta"));
					cons.setIdChequera(rs.getString("id_chequera"));
					cons.setIdCveOperacion(rs.getInt("id_cve_operacion"));
					cons.setIdFormaPago(rs.getInt("id_forma_pago"));
					cons.setIdBancoBenef(rs.getInt("id_banco_benef"));
					cons.setIdCaja(rs.getInt("id_caja"));
					cons.setIdLeyenda(rs.getString("id_leyenda"));
					cons.setIdEstatusMov(rs.getString("id_estatus_mov"));
					cons.setNoCliente(rs.getString("no_cliente"));
					cons.setTipoCambio(rs.getDouble("tipo_cambio"));
					cons.setOrigenMov(rs.getString("origen_mov"));
					cons.setSolicita(rs.getString("solicita"));
					cons.setAutoriza(rs.getString("autoriza"));
					cons.setObservacion(rs.getString("observacion"));
					cons.setLoteEntrada(rs.getInt("lote_entrada"));
					cons.setDescCaja(rs.getString("desc_caja"));
					cons.setAgrupa1(rs.getString("agrupa1"));
					cons.setAgrupa2(rs.getString("agrupa2"));
					cons.setIdRubro(rs.getDouble("id_rubro"));
					cons.setAgrupa3(rs.getString("agrupa3"));
					cons.setBServicios(rs.getString("b_servicios"));
					cons.setNoPedido(rs.getInt("no_pedido"));
					cons.setImporteMn(rs.getDouble("importe_mn"));
					cons.setNomProveedor(rs.getString("nom_proveedor"));
					cons.setFecPropuestaStr(funciones.ponerFechaSola(rs.getDate("fec_propuesta")));
					cons.setIdDivisaOriginal(rs.getString("id_divisa_original"));
					cons.setNomEmpresa(rs.getString("nom_empresa"));
					cons.setIdBanco(rs.getInt("id_banco_pago"));
					cons.setIdChequera(rs.getString("id_chequera_pago"));
					cons.setBancoPago(rs.getString("banco_pago"));
					cons.setDescGrupoCupo(rs.getString("desc_grupo_cupo")); 
					cons.setDiasInv(rs.getInt("dias"));
					cons.setNumCheqPagoMN(rs.getInt("NumCheq_PagoMN"));
					cons.setNumCheqPagoDLS(rs.getInt("NumCheq_PagoDLS"));	
					cons.setNumCheqCruceMN(rs.getInt("NumCheq_CruceMN"));
					cons.setNumCheqCruceDLS(rs.getInt("NumCheq_CruceDLS"));
					cons.setNumCheqEmp(rs.getInt("NumCheqEmp"));
					cons.setNumCheqCte(rs.getInt("NumCheqCte"));
					cons.setIdServicioBe(rs.getString("id_servicio_be"));
					cons.setIdContable(rs.getString("id_contable"));
					cons.setEquivale(rs.getString("equivale"));
					cons.setNoCheque(rs.getInt("no_cheque"));
					cons.setUsr1(rs.getInt("usuario_uno"));
					cons.setUsr2(rs.getInt("usuario_dos"));
					cons.setFecOperacionStr(funciones.ponerFechaSola(rs.getDate("fec_operacion")));
					cons.setFecContabilizacionStr((rs.getDate("fecha_contabilizacion")!= null)?funciones.ponerFechaSola(rs.getDate("fecha_contabilizacion" )):"");
					cons.setColor(rs.getString("color"));
					cons.setInvoiceType(rs.getString("invoice_type"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:consultarDetalle");
			 
		}
		return listDetalle;
	}



	@Override
	public List<ComunEgresosDto> obtenerSumaMass(ComunEgresosDto dto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int obtenerCtaMovsCpaVtaDiv(String cveControl) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String consultarConfiguraSet(int indice){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
	}
	//ok
	public String obtieneOrigenMov(String cveControl){
		StringBuffer sql = new StringBuffer();
		List<String> retorno = new ArrayList<String>();
		try {
			sql.append("select distinct origen_mov ");
			sql.append("\n from movimiento ");
			sql.append("\n where cve_control in('");
			sql.append(Utilerias.validarCadenaSQL(cveControl));
			sql.append("')");
			retorno = jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
					public String mapRow(ResultSet rs, int idx) throws SQLException {
						return rs.getString("origen_mov");              
						
					}});
			System.out.println("\n Consulta obtieneOrigenMov -->"+sql);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagoPropuestasDaoImpl, M:obtieneOrigenMov");
			e.printStackTrace();
		}
		return retorno != null && !retorno.isEmpty() ?retorno.get(0): "";
	}
	

/*	@Override
	public List<MovimientoDto> consultarPagosCpaVtaTransferPropManual(String cveControl) {
		// TODO Auto-generated method stub
		return null;
	}
	*/
	/*Public Function funSQLPagosCpaVtaTransferPropManual(ByVal psCveControl As String) _
    As ADODB.Recordset*///funSQLPagosCpaDivisaMetales
	public List<MovimientoDto> consultarPagosCpaVtaTransferPropManual(String cveControl){
		String sql="";
		List<MovimientoDto>listMovto=new ArrayList<MovimientoDto>();
		try{
			sql+= "  SELECT m.no_docto, m.no_empresa, importe_original, m.fec_valor, ";
		    
		    //if(gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2")) 
		      //  sql+= " m.importe AS importe, m.id_divisa, m.id_forma_pago,";
		    //else
		        sql+= " Cast(m.importe as Varchar(30)) As importe, m.id_divisa, m.id_forma_pago, ";
		    
		    sql+= "     m.id_chequera_benef, m.beneficiario, m.no_folio_det, m.no_folio_mov, ";
		    sql+= "     m.id_chequera, m.id_forma_pago, m.id_banco_benef, m.id_caja, ";
		    sql+= "     m.id_leyenda, m.id_estatus_mov, m.no_cliente, m.origen_mov, ";
		    sql+= "     cbp.id_banco as id_banco_cte, cbp.id_chequera as id_chequera_cte, ";
		    sql+= "     cbp.id_divisa as id_divisa_cte, m.id_banco as id_banco_pago, ";
		    sql+= "     ( SELECT cbp.desc_banco ";
		    sql+= "       FROM cat_banco cbp ";
		    sql+= "       WHERE cbp.id_banco = m.id_banco ) as banco_pago,";
		    sql+= "     m.id_chequera as id_chequera_pago, m.id_divisa as id_divisa_pago,";
		              
		    sql+= "      ( SELECT coalesce(count(c.id_chequera), 0) ";
		    sql+= "        FROM cat_cta_banco c ";
		    sql+= "        WHERE c.no_empresa = m.no_empresa ";
		    sql+= "            and c.id_divisa = m.id_divisa ) as NumCheqEmp, ";
		              
		    sql+= "      ( SELECT coalesce(count(b.id_chequera), 0) ";
		    sql+= "        FROM ctas_banco b ";
		    sql+= "        WHERE b.no_persona = m.no_cliente ";
		    sql+= "            and b.id_divisa = m.id_divisa ) as NumCheqCte, m.id_servicio_be  ";
		    //nuevo parte del codigo
		    //
		    sql+= "\n	 ,( select count(*) ";
		    sql+= "\n	 from  persona p ";
		    sql+= "\n	 join Conf_Pago_Cruzado cxp on p.equivale_persona = cxp.no_proveedor ";
		    sql+= "\n	 where p.id_tipo_persona ='P' ";
		    sql+= "\n	 and m.id_forma_pago = 3 ";
		    sql+= "\n	 and rtrim(ltrim(m.id_divisa))= rtrim(ltrim(cxp.id_divisa_original))";
		    sql+= "\n	 and p.no_persona = m.no_cliente ";
		    sql+= "\n	 and cxp.id_divisa_pago= 'MN' ) as pago_cruzado,";
		    
		    sql+= "\n	(select valor from valor_divisa where fec_divisa = ";
		    sql+= "\n   (select fec_hoy from fechas) and id_divisa =  m.id_divisa ) as tipo_cambio ";
		    //////
		    /*sql+= "\n	,(select count(*) ";
		    sql+= "\n	from persona per,conf_pago_cruzado cfc ";
		    sql+= "\n 	Where per.equivale_persona = cfc.no_proveedor ";
		    sql+= "\n	and per.id_tipo_persona='P' ";
		    sql+= "\n	and per.no_persona = m.no_cliente ";
		    sql+= "\n	and m.id_forma_pago = 3 ";
		    sql+= "\n	and m.id_divisa= cfc.id_divisa_original ";
		    sql+= "\n	and cfc.id_divisa_pago= 'MN') as pago_cruzado";
		    sql+= "\n";*/
		  //fin parte del codigo
		    
		    sql+= " FROM movimiento m";
		    
		   // if(gsDBM.equals("SYBASE")) 
		   //     sql+= " LEFT JOIN ctas_banco cbp ON ( cbp.no_persona = convert(int, m.no_cliente) ";
		    //else if (gsDBM.equals("DB2")) 
		    //    sql+= " LEFT JOIN ctas_banco cbp ON ( cbp.no_persona = cast(m.no_cliente as integer)";
		    //else
		        sql+= " LEFT JOIN ctas_banco cbp ON ( cbp.no_persona = m.no_cliente ";
		    
		    sql+= "         and cbp.id_banco = m.id_banco_benef ";
		    sql+= "         and cbp.id_chequera = m.id_chequera_benef ";
		    sql+= "         and cbp.id_divisa = m.id_divisa ) ";
		    
		    if (gsDBM.equals("POSTGRESQL"))
		        sql+= " LEFT JOIN persona per ON (m.no_cliente = per.no_persona ";
		    else if (gsDBM.equals("DB2"))
		        sql+= " LEFT JOIN persona per ON (m.no_cliente = cast(per.no_persona as varchar(15)) ";
		    else
		        //sql+= " LEFT JOIN persona per ON (m.no_cliente = convert(varchar, per.no_persona) ";        
		    	sql+= " LEFT JOIN persona per ON (m.no_cliente = Cast(Per.No_Persona as Varchar(15))";
		    sql+= "         and per.id_tipo_persona = 'P')";
		    sql+= " WHERE m.id_tipo_movto = 'E'";
		    sql+= "     AND id_forma_pago in (3,5) ";
		    sql+= "     AND ( m.origen_mov <> 'INV' or m.origen_mov is null ) ";
		    sql+= "     and (m.po_headers is null or m.po_headers ='')";
		    sql+= "     AND m.id_estatus_mov in ('N','C','F')";
		    sql+= "     AND m.no_folio_det in ";
		    sql+= "         ( SELECT mm.folio_ref ";
		    sql+= "           FROM movimiento mm ";
		    sql+= "           WHERE mm.folio_ref = m.no_folio_det ) ";
		    sql+= "     AND m.cve_control = '" +Utilerias.validarCadenaSQL(cveControl)+"' ";
		    sql+= "     AND m.id_divisa <> 'MN'";

		    System.out.println("\n Consulta consultarPagosCpaVtaTransferPropManual --->"+sql);
		    listMovto= jdbcTemplate.query(sql, new RowMapper<MovimientoDto>(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons= new MovimientoDto();
					cons.setImporte(rs.getDouble("importe"));
		    		cons.setIdDivisa(rs.getString("id_divisa"));
		    		cons.setIdFormaPago(rs.getInt("id_forma_pago"));
		    		cons.setIdChequeraBenef(rs.getString("id_chequera_benef"));
		    		cons.setBeneficiario(rs.getString("beneficiario"));
		    		cons.setNoFolioDet(rs.getInt("no_folio_det"));
		    		cons.setNoFolioMov(rs.getInt("no_folio_mov"));
		    		cons.setIdChequera(rs.getString("id_chequera"));
		    		cons.setIdFormaPago(rs.getInt("id_forma_pago"));
		    		cons.setIdBancoBenef(rs.getInt("id_banco_benef"));
					cons.setIdCaja(rs.getInt("id_caja"));
					cons.setIdLeyenda(rs.getString("id_leyenda"));
					cons.setIdEstatusMov(rs.getString("id_estatus_mov"));
					cons.setNoCliente(rs.getString("no_cliente"));
					cons.setOrigenMov(rs.getString("origen_mov"));
					cons.setIdBanco(rs.getInt("id_banco_cte"));
					cons.setIdChequera(rs.getString("id_chequera_cte"));
					cons.setIdDivisaCte(rs.getString("id_divisa_cte"));
					cons.setBancoPago(rs.getString("banco_pago"));
					cons.setIdBancoPago(rs.getInt("id_banco_pago")); 
					cons.setIdChequeraPago(rs.getString("id_chequera_pago"));
					cons.setIdDivisaPago(rs.getString("id_divisa_pago"));
					cons.setNumCheqCte(rs.getInt("NumCheqCte"));
					cons.setNumCheqEmp(rs.getInt("NumCheqEmp"));
					cons.setIdServicioBe(rs.getString("id_servicio_be")); 
					cons.setBcoPagoCruzado(rs.getInt("pago_cruzado"));
					cons.setNoDocto(rs.getString("no_docto"));
					cons.setTipoCambio(rs.getDouble("tipo_cambio"));
					return cons;
				}});
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagoPropuestasDao, M:consultarPagosCpaVtaTransferPropManual");
		}
		return listMovto;		
	}

	public List<MovimientoDto> consultarPagosCruzadosAut(ComunEgresosDto dto){
		String sql="";
		List<MovimientoDto>listDatos= new ArrayList<MovimientoDto>();
		try{
			sql+= "  SELECT m.no_docto, m.no_empresa, importe_original, m.fec_valor, ";
		    
		    if(gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2")) 
		        sql+= "\n     m.importe AS importe, m.id_divisa, m.id_forma_pago,";
		    else
		        sql+= "\n     convert(varchar,m.importe)AS importe, m.id_divisa, m.id_forma_pago, ";
		    
		    sql+= "\n     m.id_chequera_benef, m.beneficiario, m.no_folio_det, m.no_folio_mov, ";
		    sql+= "\n     m.id_chequera, m.id_forma_pago, m.id_banco_benef, m.id_caja, ";
		    sql+= "\n    m.id_leyenda, m.id_estatus_mov, m.no_cliente, m.origen_mov, ";
		    sql+= "\n     cbp.id_banco as id_banco_cte, cbp.id_chequera as id_chequera_cte, ";
		    sql+= "\n     cbp.id_divisa as id_divisa_cte,";
		    sql+= "\n     ( SELECT coalesce(ccbv.id_banco, 0)";
		    sql+= "\n       FROM cat_cta_banco ccbv ";
		    
		    if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
		    	sql+= "       ,cat_cta_banco_division ccbd";
		    
		    sql+= "\n       WHERE ccbv.no_empresa = m.no_empresa";
		    
		    if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
		    {
		        sql+="\n       AND ccbv.no_empresa = ccbd.no_empresa";
		        sql+="\n       AND ccbv.id_chequera = ccbd.id_chequera";
		        sql+="\n       AND ccbv.id_banco = ccbd.id_banco";
		        sql+="\n       AND ccbv.id_division = ccbd.id_division";
		    }
		    
		    sql+= "\n           AND ccbv.id_divisa = m.id_divisa";
		    sql+= "\n          AND ( CASE WHEN m.id_forma_pago = 1 THEN ccbv.b_cheque ";
		    sql+= "\n                      WHEN m.id_forma_pago = 3 THEN ccbv.b_transferencia ";
		    sql+= "\n                     WHEN m.id_forma_pago = 5 THEN ccbv.b_cargo_en_cuenta ";
		    sql+= "\n                      WHEN m.id_forma_pago = 9 THEN ccbv.b_cheque_ocurre END ) = 'S' ) ";
		    sql+= "\n     as id_banco_pago,";
		    sql+= "\n     ( SELECT cbp.desc_banco";
		    sql+= "\n       FROM cat_cta_banco ccbv, cat_banco cbp";
		    
		    if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
		        sql+= "       ,cat_cta_banco_division ccbd";
		    
		    sql+= "\n       WHERE ccbv.no_empresa = m.no_empresa";
		    
		    if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
		    {
		    	sql+= "\n       AND ccbv.no_empresa = ccbd.no_empresa";
		        sql+= "\n       AND ccbv.id_chequera = ccbd.id_chequera";
		        sql+= "\n       AND ccbv.id_banco = ccbd.id_banco";
		        sql+= "\n       AND ccbv.id_division = ccbd.id_division";
		    }
		    
		    sql+= "\n           and ccbv.id_banco = cbp.id_banco";
		    sql+= "\n           and ccbv.id_divisa = m.id_divisa";
		    sql+= "\n           and ( CASE WHEN m.id_forma_pago = 1 THEN ccbv.b_cheque";
		    sql+= "\n                      WHEN m.id_forma_pago = 3 THEN ccbv.b_transferencia";
		    sql+= "\n                      WHEN m.id_forma_pago = 5 THEN ccbv.b_cargo_en_cuenta ";
		    sql+= "\n                      WHEN m.id_forma_pago = 9 THEN ccbv.b_cheque_ocurre END ) = 'S' )";
		    sql+= "\n     as banco_pago,";
		    sql+= "\n     ( SELECT coalesce(ccbv.id_chequera, '')";
		    sql+= "\n       FROM cat_cta_banco ccbv";
		    
		    if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
		        sql+= "\n       ,cat_cta_banco_division ccbd";
		    
		    sql+= "\n       Where ccbv.no_empresa = m.no_empresa";
		    sql+= "\n           AND ccbv.id_divisa = m.id_divisa";
		    
		    if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
		    {
		        sql+= "\n       AND ccbv.no_empresa = ccbd.no_empresa";
		        sql+= "\n       AND ccbv.id_chequera = ccbd.id_chequera";
		        sql+= "\n       AND ccbv.id_banco = ccbd.id_banco";
		        sql+= "\n       AND ccbv.id_division = ccbd.id_division";
		    }
		        
	        sql+= "\n           and ( CASE WHEN m.id_forma_pago = 1 THEN ccbv.b_cheque";
	        sql+= "\n                      WHEN m.id_forma_pago = 3 THEN ccbv.b_transferencia";
	        sql+= "\n                      WHEN m.id_forma_pago = 5 THEN ccbv.b_cargo_en_cuenta ";
	        sql+= "\n                      WHEN m.id_forma_pago = 9 THEN ccbv.b_cheque_ocurre END ) = 'S' )";
	        sql+= "\n     as id_chequera_pago,";
	        sql+= "\n     ( SELECT coalesce(ccbv.id_divisa, '')";
	        sql+= "\n       FROM cat_cta_banco ccbv ";
	        
	        if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
	            sql+= "\n       ,cat_cta_banco_division ccbd";
	     
	        
	        sql+= "\n       WHERE ccbv.no_empresa = m.no_empresa";
	        
	        if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
	        {
	            sql+= "\n       AND ccbv.no_empresa = ccbd.no_empresa";
	            sql+= "\n       AND ccbv.id_chequera = ccbd.id_chequera";
	            sql+= "\n       AND ccbv.id_banco = ccbd.id_banco";
	            sql+= "\n       AND ccbv.id_division = ccbd.id_division";
	        }
	        
	        sql+= "\n           AND ccbv.id_divisa = m.id_divisa";
	        sql+= "\n           AND ( CASE WHEN m.id_forma_pago = 1 THEN ccbv.b_cheque";
	        sql+= "\n                      WHEN m.id_forma_pago = 3 THEN ccbv.b_transferencia";
	        sql+= "\n                      WHEN m.id_forma_pago = 5 THEN ccbv.b_cargo_en_cuenta ";
	        sql+= "\n                      WHEN m.id_forma_pago = 9 THEN ccbv.b_cheque_ocurre END ) = 'S' )";
	        sql+= "\n     as id_divisa_pago,";
	        
	        if(!dto.isCompraVtaTransfer())
	        {
	            sql+= "\n     ( SELECT coalesce(count(ccbv.id_divisa), 0)";
                sql+= "\n       FROM cat_cta_banco ccbv";
                sql+= "\n       Where ccbv.no_empresa = m.no_empresa";
                sql+= "\n           AND ccbv.id_divisa = 'MN' )";
                sql+= "\n     as NumCheq_PagoMN,";
                sql+= "\n     ( SELECT coalesce(count(ccbv.id_divisa), 0)";
                sql+= "\n       FROM cat_cta_banco ccbv";
                sql+= "\n       Where ccbv.no_empresa = m.no_empresa";
                sql+= "\n           AND ccbv.id_divisa = 'DLS' )";
                sql+= "\n     as NumCheq_PagoDLS,";
                sql+= "\n     ( SELECT CASE WHEN ( ( cbp.id_banco is not null and m.id_forma_pago in (3,5) )";
                sql+= "\n                       or ( cbp.id_banco is null and m.id_forma_pago = 9 ) )";
                sql+= "\n                       AND m.id_forma_pago <> 1 ";
                sql+= "\n           THEN null ELSE";
                
                if(gsDBM.equals("POSTGRESQL")) 
                {
                    sql+= "\n                   ( SELECT cbpc.id_banco";
                    sql+= "\n                     FROM ctas_banco cbpc";
                    sql+= "\n                     WHERE cbpc.no_persona = m.no_cliente ";
                    sql+= "\n                         AND m.no_empresa in ";
                    sql+= "\n                             ( SELECT no_empresa ";
                    sql+= "\n                               FROM empresa ";
                    sql+= "\n                               WHERE b_pag_cruzados_aut = 'S') ";
                    sql+= "\n                     LIMIT 1 ) END )";
                }
                else if(gsDBM.equals("SQL SERVER"))
                {
                    sql+= "\n                   ( SELECT TOP 1 cbpc.id_banco";
                    sql+= "\n                     FROM ctas_banco cbpc";
                    sql+= "\n                     WHERE cbpc.no_persona = m.no_cliente ";
                    sql+= "\n                         AND m.no_empresa in ";
                    sql+= "\n                             ( SELECT no_empresa ";
                    sql+= "\n                               FROM empresa ";
                    sql+= "\n                               WHERE b_pag_cruzados_aut = 'S') ";
                    sql+= "\n                   ) END )";
                }
                else if(gsDBM.equals("DB2"))
                {
                    sql+= "\n                   ( SELECT cbpc.id_banco";
                    sql+= "\n                     FROM ctas_banco cbpc";
                    sql+= "\n                     WHERE cbpc.no_persona = m.no_cliente ";
                    sql+= "\n                         AND m.no_empresa in ";
                    sql+= "\n                             ( SELECT no_empresa ";
                    sql+= "\n                               FROM empresa ";
                    sql+= "\n                               WHERE b_pag_cruzados_aut = 'S') ";
                    sql+= "\n                   ) END fetch first 1 row only)";
                }
                
                sql+= "\n     as bco_pago_cruzado,";
                sql+= "\n     ( SELECT CASE WHEN ( ( cbp.id_banco is not null and m.id_forma_pago in (3,5) )";
                sql+= "\n                       or ( cbp.id_banco is null and m.id_forma_pago = 9 ) )";
                sql+= "\n                       AND m.id_forma_pago <> 1 ";
                sql+= "\n           THEN null ELSE";
                
                if(gsDBM.equals("POSTGRESQL"))
                {
                    sql+= "\n                   ( SELECT cbpc.id_chequera";
                    sql+= "\n                     FROM ctas_banco cbpc";
                    sql+= "\n                     WHERE cbpc.no_persona = m.no_cliente ";
                    sql+= "\n                         AND m.no_empresa in ";
                    sql+= "\n                             ( SELECT no_empresa ";
                    sql+= "\n                               FROM empresa ";
                    sql+= "\n                               WHERE b_pag_cruzados_aut = 'S') ";
                    sql+= "\n                     LIMIT 1 ) END )";
                }
                else if(gsDBM.equals("SQL SERVER"))
                {
                    sql+= "\n                   ( SELECT TOP 1 cbpc.id_chequera";
                    sql+= "\n                     FROM ctas_banco cbpc";
                    sql+= "\n                     WHERE cbpc.no_persona = m.no_cliente ";
                    sql+= "\n                         AND m.no_empresa in ";
                    sql+= "\n                             ( SELECT no_empresa ";
                    sql+= "\n                               FROM empresa ";
                    sql+= "\n                               WHERE b_pag_cruzados_aut = 'S') ";
                    sql+= "\n                   ) END )";
                }
                else if(gsDBM.equals("DB2")) 
                    {
                    sql+= "\n                   ( SELECT cbpc.id_chequera";
                    sql+= "\n                    FROM ctas_banco cbpc";
                    sql+= "\n                     WHERE cbpc.no_persona = m.no_cliente ";
                    sql+= "\n                         AND m.no_empresa in ";
                    sql+= "\n                             ( SELECT no_empresa ";
                    sql+= "\n                               FROM empresa ";
                    sql+= "\n                               WHERE b_pag_cruzados_aut = 'S') ";
                    sql+= "\n                   ) END fetch first 1 row only)";
                    }
                    
                sql+= "\n     as cheq_pago_cruzado,";
                sql+= "\n     ( SELECT CASE WHEN ( ( cbp.id_banco is not null and m.id_forma_pago in (3,5) )";
                sql+= "\n                       or ( cbp.id_banco is null and m.id_forma_pago = 9 ) )";
                sql+= "\n                       AND m.id_forma_pago <> 1 ";
                sql+= "\n           THEN null ELSE";
                
                if(gsDBM.equals("POSTGRESQL")) 
                {
                    sql+= " \n                  ( SELECT cbpc.id_divisa";
                    sql+= " \n                    FROM ctas_banco cbpc";
                    sql+= " \n                    WHERE cbpc.no_persona = m.no_cliente ";
                    sql+= " \n                        AND m.no_empresa in ";
                    sql+= " \n                            ( SELECT no_empresa ";
                    sql+= "                               FROM empresa ";
                    sql+= "                               WHERE b_pag_cruzados_aut = 'S') ";
                    sql+= "                     LIMIT 1 ) END )";
                }
                else if(gsDBM.equals("SQL SERVER")) 
                {
                    sql+= "\n                   ( SELECT TOP 1 cbpc.id_divisa";
                    sql+= "\n                     FROM ctas_banco cbpc";
                    sql+= "\n                     WHERE cbpc.no_persona = m.no_cliente ";
                    sql+= "\n                         AND m.no_empresa in ";
                    sql+= "\n                             ( SELECT no_empresa ";
                    sql+= "\n                               FROM empresa ";
                    sql+= "\n                               WHERE b_pag_cruzados_aut = 'S') ";
                    sql+= "\n                   ) END )";
                }
                else if (gsDBM.equals("DB2"))
                {
                    sql+= "\n                   ( SELECT cbpc.id_divisa";
                    sql+= "\n                     FROM ctas_banco cbpc";
                    sql+= "\n                     WHERE cbpc.no_persona = m.no_cliente ";
                    sql+= "\n                         AND m.no_empresa in ";
                    sql+= "\n                             ( SELECT no_empresa ";
                    sql+= "\n                               FROM empresa ";
                    sql+= "\n                               WHERE b_pag_cruzados_aut = 'S') ";
                    sql+= "\n                   ) END fetch first 1 row only)";
                }
                
                sql+= "\n     as div_pago_cruzado, ";
            
                sql+= "\n     ( SELECT CASE WHEN ( ( cbp.id_banco is not null and m.id_forma_pago in (3,5) )";
                sql+= "\n                       or ( cbp.id_banco is null and m.id_forma_pago = 9 ) )";
                sql+= "\n                       AND m.id_forma_pago <> 1 ";
                sql+= "\n            THEN null ELSE";
                sql+= "\n              CASE WHEN m.id_divisa = 'MN' THEN m.importe / coalesce(";
                sql+= "\n               ( SELECT DISTINCT vd.valor";
                sql+= "\n                 FROM valor_divisa vd";
                sql+= "\n                 Where vd.id_divisa = 'DLS' ";
                sql+= "\n                     and vd.fec_divisa = '" +Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFecha()))+ "'), 1)";
                sql+= "\n              ELSE m.importe * coalesce(";
                sql+= "\n               ( SELECT DISTINCT vd.valor";
                sql+= "\n                 FROM valor_divisa vd";
                sql+= "\n                 Where vd.id_divisa = 'DLS' ";
                sql+= "\n                     and vd.fec_divisa = '" +Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFecha()))+ "'), 1)";
                sql+= "\n               END END )";
                sql+= "\n      as imp_pago_cruzado, ";
                
                sql+= "\n      ( SELECT DISTINCT vd.valor";
                sql+= "\n        FROM valor_divisa vd";
                sql+= "\n        WHERE vd.id_divisa = 'DLS' ";
                sql+= "\n            and vd.fec_divisa = '" +Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFecha()))+ "') ";
                sql+= "\n      as tipo_cambio_cruzado, ";
                
                sql+= "\n      ( SELECT coalesce(count(cbpc.id_divisa), 0)";
                sql+= "\n        FROM ctas_banco cbpc";
                sql+= "\n        Where cbpc.no_persona = m.no_cliente";
                sql+= "\n            and cbpc.id_divisa = 'MN' )";
                sql+= "\n      as NumCheq_CruceMN,";
                
                sql+= "\n      ( SELECT coalesce(count(cbpc.id_divisa), 0)";
                sql+= "\n        FROM ctas_banco cbpc";
                sql+= "\n        Where cbpc.no_persona = m.no_cliente";
                sql+= "\n            and cbpc.id_divisa = 'DLS' )";
                sql+= "\n      as NumCheq_CruceDLS";
	        	
	        }else{
	        	sql+= "      ( SELECT coalesce(count(c.id_chequera), 0) ";
    	        sql+= "        FROM cat_cta_banco c ";
    	        sql+= "        WHERE c.no_empresa = m.no_empresa ";
    	        sql+= "            and c.id_divisa = m.id_divisa ) as NumCheqEmp, ";
    	                  
    	        sql+= "      ( SELECT coalesce(count(b.id_chequera), 0) ";
    	        sql+= "        FROM ctas_banco b ";
    	        sql+= "        WHERE b.no_persona = m.no_cliente ";
    	        sql+= "            and b.id_divisa = m.id_divisa ) as NumCheqCte ";
	        }
		    
	        	sql+= " FROM movimiento m";
	            sql+= "     LEFT JOIN ctas_banco cbp ON (cbp.no_persona = m.no_cliente ";
	            sql+= "         and cbp.id_banco = m.id_banco_benef and cbp.id_chequera = m.id_chequera_benef ";
	            sql+= "         and cbp.id_divisa = m.id_divisa) ";
	            
            if(gsDBM.equals("POSTGRESQL")) 
                sql+= "     LEFT JOIN persona per ON (m.no_cliente = per.no_persona";
            else if(gsDBM.equals("DB2"))  
                sql+= "     LEFT JOIN persona per ON (m.no_cliente = cast(per.no_persona as varchar(15))";
            else
                sql+= "     LEFT JOIN persona per ON (m.no_cliente = convert(varchar, per.no_persona)";
            
            sql+= "         and per.id_tipo_persona = 'P')";
            sql+= " WHERE m.id_tipo_movto = 'E'";
            
            if(dto.isCompraVtaTransfer())
            {
	            sql+= " AND id_forma_pago in (3,5) ";
	            sql+= " AND m.id_divisa <> 'MN' ";
            }
	        else
	        {
	            sql+= " AND ( ( cbp.id_banco is null ";
	            sql+= "     AND cbp.id_chequera is null ";
	            sql+= "     AND m.id_forma_pago in (3, 5) ) or m.id_forma_pago = 1 ) ";
	        }
	        
	        sql+= "     AND (m.origen_mov <> 'INV' or m.origen_mov is null) ";
	        sql+= "     AND m.id_estatus_mov in ('N','C','F') ";
	        sql+= "     AND m.no_folio_det in ";
	        sql+= "         ( select mm.folio_ref from movimiento mm where mm.folio_ref = m.no_folio_det )";
	        sql+= "     AND m.cve_control = '" +Utilerias.validarCadenaSQL(dto.getCveControl())+ "' ";
	        sql+= "     AND not m.id_chequera_benef like 'CONV_______' ";
	        
	        if(gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2")) 
	            sql+= " AND len(m.id_chequera_benef) <> 16 ";
	        else
	            sql+= " AND len(m.id_chequera_benef) <> 16 ";
	        System.out.println("Consulta consultarPagosCruzadosAut ---->"+sql);
	        if(!dto.isCompraVtaTransfer())
	        {
	        	listDatos= jdbcTemplate.query(sql, new RowMapper<MovimientoDto>(){
					public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
						MovimientoDto cons= new MovimientoDto();
						cons.setNoDocto(rs.getString("no_docto"));
						cons.setNoEmpresa(rs.getInt("no_empresa"));
						cons.setImporteOriginal(rs.getDouble("importe_original"));
						cons.setFecValor(rs.getDate("fec_valor"));
						cons.setImporte(rs.getDouble("importe"));
						cons.setIdDivisa(rs.getString("id_divisa"));
						cons.setIdFormaPago(rs.getInt("id_forma_pago"));
						cons.setIdChequeraBenef(rs.getString("id_chequera_benef"));
						cons.setBeneficiario(rs.getString("beneficiario"));
						cons.setNoFolioDet(rs.getInt("no_folio_det"));
						cons.setNoFolioMov(rs.getInt("no_folio_mov"));
						cons.setIdChequera(rs.getString("id_chequera"));
						cons.setIdBancoBenef(rs.getInt("id_banco_benef"));
						cons.setIdCaja(rs.getInt("id_caja"));
						cons.setIdLeyenda(rs.getString("id_leyenda"));
						cons.setIdEstatusMov(rs.getString("id_estatus_mov"));
						cons.setNoCliente(rs.getString("no_cliente"));
						cons.setOrigenMov(rs.getString("origen_mov"));
						cons.setIdBancoCte(rs.getInt("id_banco_cte"));
						cons.setIdChequeraCte(rs.getString("id_chequera_cte"));
						cons.setIdDivisaCte(rs.getString("id_divisa_cte"));
						cons.setIdBancoPago(rs.getInt("id_banco_pago"));
						cons.setBancoPago(rs.getString("banco_pago"));
						cons.setIdChequeraPago(rs.getString("id_chequera_pago"));
						cons.setIdDivisaPago(rs.getString("id_divisa_pago"));
						cons.setNumCheqPagoMN(rs.getInt("NumCheq_PagoMN"));
						cons.setNumCheqPagoDLS(rs.getInt("NumCheq_PagoDLS"));
						cons.setBcoPagoCruzado(rs.getInt("bco_pago_cruzado"));
						cons.setCheqPagoCruzado(rs.getString("cheq_pago_cruzado"));
						cons.setDivPagoCruzado(rs.getString("div_pago_cruzado"));
						cons.setImpPagoCruzado(rs.getDouble("imp_pago_cruzado"));
						cons.setTipoCambioCruzado(rs.getDouble("tipo_cambio_cruzado"));
						cons.setNumCheqCruceMN(rs.getInt("NumCheq_CruceMN"));
						cons.setNumCheqCruceDLS(rs.getInt("NumCheq_CruceDLS"));
						return cons;
					}});
	        }else{
	        	listDatos= jdbcTemplate.query(sql, new RowMapper<MovimientoDto>(){
					public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
						MovimientoDto cons= new MovimientoDto();
						cons.setNoDocto(rs.getString("no_docto"));
						cons.setNoEmpresa(rs.getInt("no_empresa"));
						cons.setImporteOriginal(rs.getDouble("importe_original"));
						cons.setFecValor(rs.getDate("fec_valor"));
						cons.setImporte(rs.getDouble("importe"));
						cons.setIdDivisa(rs.getString("id_divisa"));
						cons.setIdFormaPago(rs.getInt("id_forma_pago"));
						cons.setIdChequeraBenef(rs.getString("id_chequera_benef"));
						cons.setBeneficiario(rs.getString("beneficiario"));
						cons.setNoFolioDet(rs.getInt("no_folio_det"));
						cons.setNoFolioMov(rs.getInt("no_folio_mov"));
						cons.setIdChequera(rs.getString("id_chequera"));
						cons.setIdBancoBenef(rs.getInt("id_banco_benef"));
						cons.setIdCaja(rs.getInt("id_caja"));
						cons.setIdLeyenda(rs.getString("id_leyenda"));
						cons.setIdEstatusMov(rs.getString("id_estatus_mov"));
						cons.setNoCliente(rs.getString("no_cliente"));
						cons.setOrigenMov(rs.getString("origen_mov"));
						cons.setIdBancoCte(rs.getInt("id_banco_cte"));
						cons.setIdChequeraCte(rs.getString("id_chequera_cte"));
						cons.setIdDivisaCte(rs.getString("id_divisa_cte"));
						cons.setIdBancoPago(rs.getInt("id_banco_pago"));
						cons.setBancoPago(rs.getString("banco_pago"));
						cons.setIdChequeraPago(rs.getString("id_chequera_pago"));
						cons.setIdDivisaPago(rs.getString("id_divisa_pago"));
						cons.setNumCheqEmp(rs.getInt("NumCheqEmp"));
						cons.setNumCheqCte(rs.getInt("NumCheqCte"));
						return cons;
					}});
	        }
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagoPropuestasDao, M:consultarPagosCruzadosAut");
		}
		return listDatos;
	}
	

	public List<MovimientoDto> consultarPagosBenefDatosAlt(ComunEgresosDto dtoIn){
		List<MovimientoDto>listDatos= new ArrayList<MovimientoDto>();
		String sql="";
		try{
			sql+= "  SELECT m.no_docto, m.no_empresa, m.id_divisa, m.no_folio_det, ";
		    sql+= "\n     ( SELECT CASE WHEN (cbp.id_banco is not null and m.id_forma_pago = 3)";
		    sql+= "\n                 or (cbp.id_banco is null and m.id_forma_pago = 1)";
		    sql+= "\n                 or (cbp.id_banco is null and m.id_forma_pago = 9)";
		    sql+= "\n           THEN null ELSE";
		    
		    if(gsDBM.equals("POSTGRESQL")) 
		    {
		        sql+= "\n                   ( SELECT cbpc.id_banco";
		        sql+= "\n                     FROM ctas_banco cbpc";
		        sql+= "\n                     WHERE cbpc.no_persona = m.no_cliente ";
		        sql+= "\n                         and cbpc.id_divisa = m.id_divisa LIMIT 1 ) END )";
		    }
		    else if(gsDBM.equals("SQL SERVER"))
		    {
		        sql+= "\n                   ( SELECT TOP 1 cbpc.id_banco";
		        sql+= "\n                     FROM ctas_banco cbpc";
		        sql+= "\n                     WHERE cbpc.no_persona = m.no_cliente ";
		        sql+= "\n                         and cbpc.id_divisa = m.id_divisa ) END )";
		    }
		    else if(gsDBM.equals("DB2")) 
		    {
		        sql+= "\n                   ( SELECT cbpc.id_banco";
		        sql+= "\n                     FROM ctas_banco cbpc";
		        sql+= "\n                     WHERE cbpc.no_persona = m.no_cliente ";
		        sql+= "\n                         and cbpc.id_divisa = m.id_divisa ) END fetch first 1 row only ) ";
		    }
		    
		    sql+= "\n     as bco_Benef_alt,";
		    sql+= "\n     ( SELECT CASE WHEN (cbp.id_banco is not null and m.id_forma_pago = 3)";
		    sql+= "\n                 or (cbp.id_banco is null and m.id_forma_pago = 1)";
		    sql+= "\n                 or (cbp.id_banco is null and m.id_forma_pago = 9)";
		    sql+= "\n           THEN null ELSE";
		    
		    if(gsDBM.equals("POSTGRESQL"))  
		    {
		        sql+= "\n                   ( SELECT cbpc.id_chequera";
		        sql+= "\n                     FROM ctas_banco cbpc";
		        sql+= "\n                     WHERE cbpc.no_persona = m.no_cliente ";
		        sql+= "\n                         and cbpc.id_divisa = m.id_divisa LIMIT 1 ) END )";
		    }
		    else if(gsDBM.equals("SQL SERVER")) 
		    {
		        sql+= "\n                   ( SELECT TOP 1 cbpc.id_chequera";
		        sql+= "\n                     FROM ctas_banco cbpc";
		        sql+= "\n                     WHERE cbpc.no_persona = m.no_cliente ";
		        sql+= "\n                         and cbpc.id_divisa = m.id_divisa ) END )";
		    }
		    else if(gsDBM.equals("DB2"))  
		    {
		        sql+= "\n                   ( SELECT cbpc.id_chequera";
		        sql+= "\n                     FROM ctas_banco cbpc";
		        sql+= "\n                     WHERE cbpc.no_persona = m.no_cliente ";
		        sql+= "\n                         and cbpc.id_divisa = m.id_divisa ) END fetch first 1 row only) ";
		    }
		        
		    sql+= "\n     as cheq_benef_alt ";
		    
		    sql+= "\n FROM movimiento m";
		    sql+= "\n     LEFT JOIN ctas_banco cbp ON (cbp.no_persona = m.no_cliente";
		    sql+= "\n         and cbp.id_banco = m.id_banco_benef and cbp.id_chequera = m.id_chequera_benef)";
		    
		    if(gsDBM.equals("POSTGRESQL")) 
	        sql+= "     LEFT JOIN persona per ON (m.no_cliente = per.no_persona";
		    else if(gsDBM.equals("DB2"))  
		        sql+= "     LEFT JOIN persona per ON (m.no_cliente = cast(per.no_persona as varchar(15))";
		    else
		        sql+= "     LEFT JOIN persona per ON (m.no_cliente = convert(varchar, per.no_persona)";
		 
		    
		    sql+= "         and per.id_tipo_persona = 'P')";
		    sql+= " WHERE m.id_tipo_movto = 'E'";
		    sql+= "     AND ( ( cbp.id_banco is null";
		    sql+= "           AND cbp.id_chequera is null";
		    
		    sql+= "           AND ( SELECT CASE WHEN (cbp.id_banco is not null and m.id_forma_pago = 3)";
		    sql+= "                           or (cbp.id_banco is null and m.id_forma_pago = 1)";
		    sql+= "                           or (cbp.id_banco is null and m.id_forma_pago = 9)";
		    sql+= "                     THEN null ELSE";
		    
		    if(gsDBM.equals("POSTGRESQL"))
		    {
		        sql+= "                     ( SELECT cbpc.id_chequera";
		        sql+= "                       FROM ctas_banco cbpc";
		        sql+= "                       WHERE cbpc.no_persona = m.no_cliente ";
		        sql+= "                           and cbpc.id_divisa = m.id_divisa LIMIT 1 ) END ) is not null ";
		    }
		    else if(gsDBM.equals("SQL SERVER")) 
		    {
		        sql+= "                     ( SELECT TOP 1 cbpc.id_chequera";
		        sql+= "                       FROM ctas_banco cbpc";
		        sql+= "                       WHERE cbpc.no_persona = m.no_cliente ";
		        sql+= "                           and cbpc.id_divisa = m.id_divisa ) END ) is not null ";
		    }
		    else if(gsDBM.equals("DB2"))
		    {
		        sql+= "                     ( SELECT cbpc.id_chequera";
		        sql+= "                       FROM ctas_banco cbpc";
		        sql+= "                       WHERE cbpc.no_persona = m.no_cliente ";
		        sql+= "                           and cbpc.id_divisa = m.id_divisa ) END fetch first 1 row only) is not null ";
		    }
		    
		    sql+= "         ) OR ( cbp.id_banco is null ";
		    sql+= "              AND cbp.id_chequera is null ";
		    sql+= "              AND m.id_leyenda = '*' ) ) ";
		    sql+= "     AND (m.origen_mov <> 'INV' or m.origen_mov is null)";
		    sql+= "     AND m.id_estatus_mov in ('N','C','F')";
		    sql+= "     AND m.id_forma_pago = 3";
		    sql+= "     AND m.no_folio_det in";
		    sql+= "         ( SELECT mm.folio_ref ";
		    sql+= "           FROM movimiento mm ";
		    sql+= "           WHERE mm.folio_ref = m.no_folio_det )";
		    sql+= "     AND m.cve_control = '" +Utilerias.validarCadenaSQL(dtoIn.getCveControl())+ "'";
		    
		    listDatos= jdbcTemplate.query(sql, new RowMapper<MovimientoDto>(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons= new MovimientoDto();
					cons.setNoDocto(rs.getString("no_docto"));
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setIdDivisa(rs.getString("id_divisa"));
					cons.setIdBancoBenef(rs.getInt("bco_Benef_alt"));
					cons.setIdChequeraBenef(rs.getString("cheq_benef_alt"));
					return cons;
				}});
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagoPropuestasDao, M:consultarPagosCruzadosAut");	
		}
		return listDatos;
		
	}
	

	@Override
	public int contarMovsPropMAut(ComunEgresosDto dto){
		String sql="";
		try{
			sql+= " SELECT count(*) as TotalRegs ";
		    sql+="\n  FROM movimiento m LEFT JOIN cat_estatus e ON (m.id_estatus_mov = e.id_estatus ) ";
		    
		    if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
		        sql+="\n ,cat_cta_banco_division ccbd ";
		    
		    if(dto.getPsBandera()!=null && !dto.getPsBandera().equals(""))
		        sql+="\n ,cat_cta_banco ccb ";
		    
		    sql+="\n WHERE m.id_tipo_movto = 'E' ";
		    sql+="\n     AND ( m.origen_mov <> 'INV' or m.origen_mov is null )";
		    sql+="\n     AND m.id_estatus_mov in ('N','C','F','L','V') ";
		    sql+="\n     AND m.id_forma_pago in (" +dto.getIdFormaPago()+ ") ";
		    sql+="\n     AND e.clasificacion = 'MOV' ";
		    sql+="\n     AND m.no_folio_det in ";
		    sql+="\n         ( SELECT mm.folio_ref ";
		    sql+="\n           FROM movimiento mm ";
		    sql+="\n           WHERE mm.folio_ref = m.no_folio_det ) ";
		    sql+="\n     AND m.cve_control = '" +Utilerias.validarCadenaSQL(dto.getCveControl())+"' ";
		    
		    if(dto.getIdGrupoEmpresas()>0)
		    {
		        sql+="\n and m.no_empresa in (select no_empresa from grupo_empresa ";
		        sql+="\n where id_grupo_flujo = " +dto.getIdGrupoEmpresas()+ ")";
		    }
		    if(dto.getPsDivision()!=null && !dto.getPsDivision().equals("")) 
		    {
		    	sql+="\n AND m.no_empresa = ccbd.no_empresa ";
		        sql+="\n AND m.id_chequera = ccbd.id_chequera ";
		        sql+="\n AND m.id_banco = ccbd.id_banco ";
		        sql+="\n AND m.division = ccbd.id_division ";
		    }
		    
		    if(dto.getPsOrigenMov()!=null && dto.getPsOrigenMov().equals("0")) 
	            sql+="\n  AND m.origen_mov='" +Utilerias.validarCadenaSQL(dto.getPsOrigenMov())+ "'";
		        
		    if(dto.getPsBandera()!=null && !dto.getPsBandera().equals(""))
		    {
		        sql+="\n AND ccb.no_empresa = m.no_empresa ";
		        sql+="\n AND ccb.id_divisa = m.id_divisa";
		    }
		    if(dto.getPsBandera()!=null && dto.getPsBandera().trim().equals("cheque"))
		    	sql+="\n AND ( m.id_forma_pago = 1 and ccb.b_cheque ='S' ) ";
		    else if(dto.getPsBandera()!=null && dto.getPsBandera().trim().equals("transferencia"))
		    	sql+="\n AND ( m.id_forma_pago = 3 and ccb.b_transferencia = 'S' ) ";
		    else if(dto.getPsBandera()!=null && dto.getPsBandera().trim().equals("cheque_ocurre"))
		    	sql+="\n AND ( m.id_forma_pago = 9 and ccb.b_cheque_ocurre = 'S' ) ";
		    
		    return jdbcTemplate.queryForInt(sql);
		 
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagoPropuestasDao, M:contarMovsPropMAut");
			return 0;
		}
	}



	public List<MovimientoDto>consultarPagPropAut1(ComunEgresosDto dtoIn){
		ConsultasGenerales consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarPagPropAut1(dtoIn);
	}

	public int actualizarBancoCheqBenef(int noFolioDet, int idBanco, String idChequera){
		ConsultasGenerales consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.actualizarBancoCheqBenef(noFolioDet, idBanco, idChequera);
	}

	public List<ComunDto> consultarBancoCheqBenef(ComunDto dto){
		ConsultasGenerales consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarBancoCheqBenef(dto);
	}

	public int actualizarBancoCheqBenef(ComunDto dto){
		ConsultasGenerales consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.actualizarBancoCheqBenef(dto);
	}

	/**
	 *Public Function funSQLUpdateEstatusComVtaTransf(ByVal psFolios As String, Optional ByVal bCiti As Boolean) As Long
	 */
	public int actualizarEstatusComVtaTransfer(String psFolios, boolean bCiti){
		String sql="";
		try{
			if(bCiti){//'ACM 18/04/06 - Se actualiza fec_valor = fec_hoy cuando Banco = CitiBank, Casa_Cambio = S e id_tipo_operacion = 3000 o 3001
				sql+= " UPDATE movimiento ";
				sql+= " SET fec_valor ='"+Utilerias.validarCadenaSQL(funciones.ponerFechaSola(obtenerFechaHoy()))+"'";
		        sql+= " WHERE no_folio_det IN (" + psFolios + ") ";
		        sql+= " OR (folio_ref IN (" + psFolios + ") AND id_tipo_operacion = 3001)";
			}else{
	        	sql+= " UPDATE movimiento ";
		        sql+= "    SET id_estatus_mov = 'V' ";
		        sql+= "  WHERE no_folio_det in (" + psFolios + ") ";
	        }
			return jdbcTemplate.queryForInt(sql);      
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagoPropuestasDao, M:actualizarEstatusComVtaTransfer");
			return 0;
		}
	}

	public List<String> consultarPagoMassPayment(int noFolioDet, Date fechaHoy){
		List<String> listRet= new ArrayList<String>();
		String sql="";
		try{
		    sql+= "SELECT m.no_folio_det ";
		    sql+="\n  FROM movimiento m , cat_cta_banco c ";
		    sql+="\n WHERE m.no_empresa = c.no_empresa";
		    sql+="\n   AND m.id_banco = c.id_banco";
		    sql+="\n   AND m.id_chequera = c.id_chequera";
		    sql+="\n   AND c.pago_mass = 'S'";
		    sql+="\n   AND m.id_tipo_operacion = 3000";
		    sql+="\n   AND m.id_estatus_mov in ('N','C')";
		    sql+="\n   AND m.id_banco = 2";
		    sql+="\n   AND m.id_banco_benef in (2, 12)";
		    sql+="\n   AND m.id_forma_pago = 3";
		    sql+="\n   AND m.fec_valor > '" +Utilerias.validarCadenaSQL(funciones.ponerFecha(fechaHoy))+ "'";
		    sql+="\n   AND m.no_folio_det = " + noFolioDet;
		    
		    listRet=jdbcTemplate.query(sql, new RowMapper<String>(){
		    	public String mapRow(ResultSet rs,int idx)throws SQLException{
		    		return rs.getString("no_folio_det");
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagoPropuestasDao, M:consultarEsMass");
		}
		return listRet;
	}

	public int actualizarEstatusMass(String folios){
		String sql="";
		try{	
		    sql= "UPDATE movimiento ";
		    sql+= "\n   SET id_estatus_mov = 'M' ";
		    sql+= "\n WHERE no_folio_det in (" + folios + ")";
		    return jdbcTemplate.update(sql);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagoPropuestasDao, M:actualizarEstatusMass");
			return 0;
		}
	}

	public int insertarDato(int noEmpresa, String cadenaPagador){
		String sql="";
		try{
			sql+="INSERT INTO datos_pagador_cheq";
			sql+="\n values ("+noEmpresa+","+cadenaPagador+")";
			return jdbcTemplate.update(sql);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagoPropuestasDao, M:insertarDato");
			return 0;
		}
	}

	public int actualizarCveControl(int noEmpresa, String psFolios, String cveControl, Date fecPropuesta){
		String sql="";
		try{
			
			sql= "UPDATE movimiento ";
		    sql+="\n   SET cve_control = '" +cveControl+ "', ";
		    sql+="\n       fec_propuesta = convert(datetime,'" +funciones.ponerFecha(fecPropuesta)+ "' ,103)";
		    sql+="\n WHERE no_folio_mov in (select no_folio_mov ";
		    sql+="\n                          from movimiento ";
		    sql+="\n                         where folio_ref in (" + psFolios + ") ";
		    sql+="\n                           and id_tipo_operacion = 3200)";
		    sql+="\n   AND no_empresa = " + noEmpresa;
		    
		    System.out.println("Sql nuevo \n"+sql);
		    
		    return jdbcTemplate.update(sql);
		}catch(Exception e){
			System.out.println("M:actualizarCveControl");
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagoPropuestasDao, M:actualizarCveControl");
			return 0;
		}
	}
	

	@Override
	public int insertarParametroFactoraje(ParametroFactorajeDto dto) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int actualizarMovto3000(ParamMovto3000Dto dto) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> seleccionarBImpreContinua(int idBanco, String sChequera) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int ejecutarSpPagadorAgrupaCheques3000(int iIdUsuario, String sFolios, int iResult) {
		// TODO Auto-generated method stub
		return 0;
	}

	public List<Map<String, Object>> reportePagoPropuestas(PagosPropuestosDto dto){
		System.out.println("busisbes reporte");
		String sql="";
	    String sEnc="";
	    String sCND="";
	    double pdTipoCambio=0;
	    String conSoloCheques="";
	    List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
	    
		try{
			
		    if(dto.getCveControl()!=null && dto.getCveControl().equals(""))
		    {
		        if(obtenerTipoCambio(consultarConfiguraSet(772),obtenerFechaHoy())>0)
		            pdTipoCambio = obtenerTipoCambio(consultarConfiguraSet(772),obtenerFechaHoy());
		        else
		            pdTipoCambio = 1;
		    }
		    // esto se manda llamar solo si la peticion viene con la opcion de que el usuario solo puede ver propuestas que
		    // contengan unicamente cheques
		    
		    conSoloCheques = "\n	where cve_control in (select cve_control from movimiento ";
		    conSoloCheques +="\n    where id_forma_pago = 1 and id_tipo_operacion = 3000 ";
		    conSoloCheques +="\n     and id_estatus_mov in ('N', 'C') and no_empresa in ";
		    conSoloCheques +="\n     (SELECT no_empresa FROM usuario_empresa WHERE no_usuario = " + dto.getIdUsuario() +")";
		    conSoloCheques +="\n     and cve_control is not null) ";
		    conSoloCheques +="\n	 and cve_control not in (select cve_control from movimiento ";
		    conSoloCheques +="\n     where id_forma_pago <> 1 and id_tipo_operacion = 3000 ";
		    conSoloCheques +="\n     and id_estatus_mov in ('N', 'C') and no_empresa in ";
		    conSoloCheques +="\n     (SELECT no_empresa FROM usuario_empresa WHERE no_usuario = "+ dto.getIdUsuario() +")";
		    conSoloCheques +="\n     and cve_control is not null)";

		    sEnc = "";
	        if(dto.getCveControl()!=null && dto.getCveControl().equals("")) 
	        {
	            sEnc+="\n        SELECT max(e.emp_che) as AgrupaCheEmp , m.cve_control, max(fecha_propuesta) as fec_propuesta, ";
	            
            	if(dto.getCveControl()!=null && !dto.getCveControl().equals("")) //este if asi esta el codigo VB
	                sEnc+="\n            sag.id_division as desc_grupo_flujo, cgc.desc_grupo_cupo, ";
                else
	                sEnc+="\n            cgf.desc_grupo_flujo, cgc.desc_grupo_cupo, ";
            
	            
	            sEnc+="\n            sum(case when m.id_divisa = 'MN' then importe else 0 end) as importe_mn, ";
	            sEnc+="\n            sum(case when m.id_divisa = 'DLS' then importe else 0 end) as importe_dls, ";
	            sEnc+="\n            sum(case when m.id_divisa = 'DLS' then m.importe else case when m.id_divisa = 'MN' then m.importe / "+pdTipoCambio+" else m.importe end end) as total_dls, ";
	            sEnc+="\n            sag.id_grupo_flujo, gfc.id_grupo_cupo, coalesce(sag.concepto,'') as concepto";
	        }else
	        	sEnc+="\n        SELECT m.cve_control, m.id_banco, m.id_chequera, m.fec_propuesta, m.no_empresa ";
	        
	        sEnc+="\n             ,( SELECT count(*) ";
            sEnc+="\n                FROM movimiento m ";
            sEnc+="\n                WHERE m.id_estatus_mov = 'L' ";
            sEnc+="\n                    AND m.id_tipo_movto = 'E' ";
            sEnc+="\n                    AND m.id_tipo_operacion between 3800 and 3899 ";
            sEnc+="\n                    AND m.cve_control = sag.cve_control ";
            sEnc+="\n              ) as NumIntercos, ";
            sEnc+="\n              ( SELECT coalesce(sum(m.importe),0)";  
            //sEnc+="\n              ( SELECT isnull(sum(m.importe),0) ";
            sEnc+="\n                FROM movimiento m ";
            sEnc+="\n                WHERE m.id_estatus_mov = 'L' ";
            sEnc+="\n                    AND m.id_tipo_movto = 'E' ";
            sEnc+="\n                    AND m.id_tipo_operacion between 3800 and 3899 ";
            sEnc+="\n                    AND m.cve_control = sag.cve_control ";
            sEnc+="\n              ) as TotalIntercos ";
          
            sEnc+="\n              , max(m.id_banco_benef) as id_banco_benef, max(m.fec_recalculo) as fec_recalculo";
            sEnc+="\n        FROM ";
            
            if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
            	sEnc+="\n            movimiento m, grupo_flujo_cupo gfc,";
	        else
	            sEnc+="\n            movimiento m, cat_grupo_flujo cgf, grupo_flujo_cupo gfc, ";
	        
	        sEnc+="\n            cat_grupo_cupo cgc, seleccion_automatica_grupo sag, empresa e ";
	        
	        if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
	            sEnc+="\n        WHERE ";
	        else
	            sEnc+="\n        WHERE sag.id_grupo_flujo = cgf.id_grupo_flujo ";
	        
	        sCND = "";

	        if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
               sCND+="\n m.cve_control = sag.cve_control ";
            else
               sCND+="\n AND m.cve_control = sag.cve_control ";
            
           sCND+="\n     AND m.id_rubro = gfc.id_rubro ";
           sCND+="\n     AND gfc.id_grupo_cupo = cgc.id_grupo_cupo ";
           sCND+="\n     AND m.id_tipo_movto = 'E' ";
           sCND+="\n     AND (m.origen_mov <> 'INV' or m.origen_mov is null) ";
           sCND+="\n     AND m.no_empresa = e.no_empresa ";
           
           if(dto.isBLocal())//Se seleccionan s�lo empresas locales
        	   sCND+="\n     AND e.b_local = 'S' ";
	        
	       if(dto.isBTambienIntercos())
	           sCND+="\n AND m.id_estatus_mov in ('N','C','F','L') ";
	       else
	           sCND+="\n AND m.id_estatus_mov in ('N','C','F') ";
	       
	       //sCND+="\n     AND ((m.id_forma_pago = 3 and m.id_leyenda <> '*') or ";
    	   //sCND+="\n         (m.id_forma_pago in (1, 5, 6, 7, 9,10))) ";
	       sCND+="\n     and (m.po_headers is null or m.po_headers ='') ";
    	   sCND+="\n     AND ((m.id_forma_pago = 3 and m.id_leyenda <> '*') or ";
	       sCND+="\n	(m.no_cliente = (select max(p.no_persona) from  persona p  join Conf_Pago_Cruzado cxp on p.equivale_persona = cxp.no_proveedor where p.id_tipo_persona ='P')) or";
    	   sCND+="\n         (m.id_forma_pago in (1, 5, 6, 7, 9,10))) ";
    	   
    	   
    	   
    	   
    	   
    	   sCND+="\n     AND (m.id_tipo_operacion = 3000 or (m.id_tipo_operacion between 3800 and 3899)) ";
	       
    	   sCND+="\n     AND (m.id_tipo_operacion = 3000 or (m.id_tipo_operacion between 3800 and 3899)) ";
	       
		    if((dto.getFechaIni()!=null && !dto.getFechaIni().equals(""))&&(dto.getFechaFin()!=null && !dto.getFechaFin().equals(""))) 
		       sCND+="\n AND sag.fecha_propuesta between convert(datetime,'"+Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFechaIni()))+"', 103) and convert(datetime,'"+Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFechaFin()))+"', 103) ";
		    else if (dto.getFechaFin()!=null && !dto.getFechaFin().equals(""))
		       sCND+="\n AND sag.fecha_propuesta <= convert(datetime,'"+Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFechaFin()))+"', 103)";
		    else if (dto.getFechaIni()!=null && !dto.getFechaIni().equals(""))
		       sCND+="\n AND sag.fecha_propuesta >= convert(datetime,'"+Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFechaIni()))+"', 103) ";
		    
		   	sCND+="\n     AND m.cve_control is not null ";
		   
    	   /*/
		    if((dto.getFechaIni()!=null && !dto.getFechaIni().equals(""))&&(dto.getFechaFin()!=null && !dto.getFechaFin().equals(""))) 
		       sCND+="\n AND sag.fecha_propuesta between '"+funciones.ponerFecha(dto.getFechaIni())+"' and '"+funciones.ponerFecha(dto.getFechaFin())+"' ";
		    else if (dto.getFechaFin()!=null && !dto.getFechaFin().equals(""))
		       sCND+="\n AND sag.fecha_propuesta <= '"+funciones.ponerFecha(dto.getFechaFin())+"'";
		    else if (dto.getFechaIni()!=null && !dto.getFechaIni().equals(""))
		       sCND+="\n AND sag.fecha_propuesta >= '"+funciones.ponerFecha(dto.getFechaIni())+"'";
		    
		   	sCND+="\n     AND m.cve_control is not null ";
		   */ 
		    if (dto.getIdGrupoEmpresa()>0)
		       sCND+="\n AND sag.id_grupo_flujo = "+dto.getIdGrupoEmpresa();
		    
		    if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
		    {
		        if(dto.getPsDivision().equals("TODAS LAS DIVISIONES"))
		           sCND+="\n AND ( not sag.id_division is null and sag.id_division <> '' ) ";
		        else
		           sCND+="\n AND sag.id_division = '"+dto.getPsDivision()+"' ";
		    }
		    
		    if(dto.getCveControl()!=null && !dto.getCveControl().equals(""))
		       sCND+="\n AND sag.cve_control = '"+dto.getCveControl()+"'";
		 
		    if(dto.getIdUsuario() > 0 )
		    {
		      if(dto.getPsDivision()!=null && !dto.getPsDivision().equals("")) 
		      {
		    	  
		    	  sCND+="\n AND m.division in ( SELECT id_division ";
		    	  sCND+="\n                        FROM usuario_division ";
		    	  sCND+="\n        WHERE no_usuario = "+ dto.getIdUsuario() +")";
		      }
		      else
		      {
	           sCND+="\n AND m.no_empresa in ( SELECT no_empresa ";
	           sCND+="\n                       FROM usuario_empresa ";
	           sCND+="\n                       WHERE no_usuario = "+ dto.getIdUsuario() +")";
		      }
		      //sCND = sCND & Chr(10) & " AND (((select count(*) from usuario_area WHERE no_usuario = " & pvi_Usuario & ") = 0) or "
		      //sCND = sCND & Chr(10) & "     (id_area in (select id_area from usuario_area where no_usuario = " & pvi_Usuario & ")))"
		    }
		    
		    if(dto.isBSoloUsrAct())
		    {
		        if(dto.isBSoloManuales())
		           sCND+="\n AND m.cve_control like 'M%"+ dto.getIdUsuario() +"' ";
		        else
		           sCND+="\n AND m.cve_control like '%"+ dto.getIdUsuario() +"' ";
		    }
		    
		    if(dto.getCveControl()!=null && dto.getCveControl().equals(""))
		    {
		    	if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
		           sCND+="\n GROUP BY m.cve_control, sag.id_division, cgc.desc_grupo_cupo, ";
		        else
		           sCND+="\n GROUP BY m.cve_control, cgf.desc_grupo_flujo, cgc.desc_grupo_cupo, ";
		        
		       sCND+="\n     sag.id_grupo_flujo, gfc.id_grupo_cupo, sag.concepto, sag.cve_control";
		    }
		    else
		       sCND+="\n GROUP BY m.cve_control, m.id_banco, m.id_chequera, m.fec_propuesta, m.no_empresa, sag.cve_control";
	      
		    if(dto.getAutorizaPropuesta()!= null && dto.getAutorizaPropuesta().equals("SI"))
		    {
		    	sql="";
		    	if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
		    	{
		    		sql+= sEnc + sCND;
		    		if(dto.getCveControl()!= null && dto.getCveControl().equals(""));
	                	sql+=" ORDER BY fec_propuesta ";
		    	}else{
		    		if(dto.isBSoloUsrAct())
		    		{
		    			if(gsDBM!=null && !gsDBM.equals("SYBASE")) 
		                {
		                	sql+= " SELECT max(case when AgrupaCheEmp is null then '' when AgrupaCheEmp = 0 then 'N' else 'S' end) as AgrupaCheEmp, cve_control, max(fec_propuesta) as fec_propuesta, ";
		                    sql+= "     desc_grupo_flujo, desc_grupo_cupo, sum(importe_mn) as importe_mn, ";
		                    sql+= "     sum(importe_dls) as importe_dls, sum(total_dls) as total_dls, ";
		                    sql+= "     id_grupo_flujo , id_grupo_cupo, concepto, ";
		                    sql+= "     sum(NumIntercos) as NumIntercos, ";
		                    //sql+= "     isnull(sum(TotalIntercos),0) as TotalIntercos ";
		                    sql+= "     coalesce(sum(TotalIntercos),0) as TotalIntercos ";
		                    sql+= " FROM ( ";
		                }
		                    
		                sql+= sEnc;
		                sql+= " AND cgf.nivel_autorizacion = 0 ";
		                sql+= sCND;
		                sql+= " UNION ALL ";
		                sql+= sEnc;
		                sql+= " AND cgf.nivel_autorizacion = 1 ";
		                sql+= " AND sag.usuario_uno is null ";
		                sql+= sCND;
		                sql+= " UNION ALL ";
		                sql+= sEnc;
		                sql+= " AND cgf.nivel_autorizacion = 2 ";
		                sql+= " AND sag.usuario_uno is null ";
		                sql+= " AND sag.usuario_dos is null ";
		                sql+= sCND;
		                sql+= " UNION ALL ";
		                sql+= sEnc;
		                sql+= " AND cgf.nivel_autorizacion = 3 ";
		                sql+= " AND sag.usuario_uno is null ";
		                sql+= " AND sag.usuario_dos is null ";
		                sql+= " AND sag.usuario_tres is null ";
		                sql+= sCND;
		                
		                if(gsDBM!=null && !gsDBM.equals("SYBASE"))
		                {
		                	sql+= " ) a ";
		                    if(dto.isBSoloCheques()) 
		                    	sql+= conSoloCheques;
		                    
		                    sql+=" GROUP BY cve_control, desc_grupo_flujo, desc_grupo_cupo, ";
		                    sql+="     id_grupo_flujo, id_grupo_cupo, concepto ";
		                    sql+=" ORDER BY fec_propuesta ";
		                }
		    		}else{
		                if(gsDBM!=null && !gsDBM.equals("SYBASE")) 
		                {
		                	sql+= " SELECT max(case when AgrupaCheEmp is null then '' when AgrupaCheEmp = 0 then 'N' else 'S' end) as AgrupaCheEmp, cve_control, max(fec_propuesta) as fec_propuesta, ";
		                    sql+= "     desc_grupo_flujo, desc_grupo_cupo, sum(importe_mn) as importe_mn, ";
		                    sql+= "     sum(importe_dls) as importe_dls, sum(total_dls) as total_dls, ";
		                    sql+= "     id_grupo_flujo, id_grupo_cupo, concepto, ";
		                    sql+= "     sum(NumIntercos) as NumIntercos, ";
		                    //sql+= "     isnull(sum(TotalIntercos),0) as TotalIntercos ";
		                    sql+= "     coalesce(sum(TotalIntercos),0) as TotalIntercos ";
		                    sql+= " FROM ( ";
		                }
		                
		                sql+= sEnc;
		                sql+= " AND cgf.nivel_autorizacion = 0 ";
		                sql+= sCND;
		                sql+= " UNION ALL ";
		                sql+= sEnc;
		                sql+= " AND cgf.nivel_autorizacion = 1 ";
		                sql+= " AND not sag.usuario_uno is null ";
		                sql+= sCND;
		                sql+= " UNION ALL ";
		                sql+= sEnc;
		                sql+= " AND cgf.nivel_autorizacion = 2 ";
		                sql+= " AND not sag.usuario_uno is null ";
		                sql+= " AND not sag.usuario_dos is null ";
		                sql+= sCND;
		                sql+= " UNION ALL ";
		                sql+= sEnc;
		                sql+= " AND cgf.nivel_autorizacion = 3 ";
		                sql+= " AND not sag.usuario_uno is null ";
		                sql+= " AND not sag.usuario_dos is null ";
		                sql+= " AND not sag.usuario_tres is null ";
		                sql+= sCND;
		                
		                if(gsDBM!=null && !gsDBM.equals("SYBASE")) 
		                {
		                    sql+= " ) a ";
		                    if(dto.isBSoloCheques())
		                        sql+= conSoloCheques;

		                    sql+= " GROUP BY cve_control, desc_grupo_flujo, desc_grupo_cupo, ";
		                    sql+= "     id_grupo_flujo, id_grupo_cupo, concepto ";
		                    sql+= " ORDER BY fec_propuesta ";
		                }
		                else
		                  sql+= " AT ISOLATION READ UNCOMMITTED";	
		    		}
		    	}
		    }else{
		    	sql = "";
	            sql+= sEnc + sCND;
	            if(dto.getCveControl()!=null && dto.getCveControl().equals(""))
	                sql+=" ORDER BY fec_propuesta ";
		    }
		   System.out.println("Cons PagProp: "+ sql.toString());
		   
		    if(dto.getAutorizaPropuesta()!= null && dto.getAutorizaPropuesta().equals("SI"))//se realiza if por id_banco_benef y fec_recalculo
		    {
		    	listResult = jdbcTemplate.query(sql, new RowMapper(){
					public Map mapRow(ResultSet rs, int idx) throws SQLException {
						Map map = new HashMap();
						map.put("empCheStr", rs.getString("AgrupaCheEmp"));
						map.put("cveControl", rs.getString("cve_control"));
						map.put("fecPropuesta", rs.getDate("fec_propuesta"));
						map.put("descGrupoFlujo", rs.getString("desc_grupo_flujo"));
						map.put("descGrupoCupo", rs.getString("desc_grupo_cupo"));
						map.put("importeMn", rs.getBigDecimal("importe_mn").doubleValue());
						map.put("importeDls", rs.getBigDecimal("importe_dls").doubleValue());
						map.put("totalDls", rs.getDouble("total_dls"));
						map.put("idGrupoCupo", rs.getString("id_grupo_cupo"));
						map.put("idGrupoFlujo", rs.getString("id_grupo_flujo"));
						map.put("concepto", rs.getString("concepto"));
						map.put("numIntercos", rs.getInt("NumIntercos"));
						map.put("totalIntercos", rs.getInt("TotalIntercos"));
		    			return map;
					}});
		    }else{
		    	listResult = jdbcTemplate.query(sql, new RowMapper(){
		    		public Map mapRow(ResultSet rs, int idx) throws SQLException {
						Map map = new HashMap();
						map.put("empCheStr", rs.getString("AgrupaCheEmp"));
						map.put("cveControl", rs.getString("cve_control"));
						map.put("fecPropuesta", rs.getString("fec_propuesta"));
						map.put("descGrupoFlujo", rs.getString("desc_grupo_flujo"));
						map.put("descGrupoCupo", rs.getString("desc_grupo_cupo"));
						map.put("importeMn", rs.getDouble("importe_mn"));
						map.put("importeDls", rs.getDouble("importe_dls"));
						map.put("totalDls", rs.getDouble("total_dls"));
						map.put("idGrupoCupo", rs.getString("id_grupo_cupo"));
						map.put("idGrupoFlujo", rs.getString("id_grupo_flujo"));
						map.put("concepto", rs.getString("concepto"));
						map.put("idBancoBenef", rs.getInt("id_banco_benef"));
						map.put("fecRecalculo", rs.getDate("fec_recalculo"));
						map.put("numIntercos", rs.getInt("NumIntercos"));
						map.put("totalIntercos", rs.getInt("TotalIntercos"));
		    			return map;
					}
		    	});
		    }
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PagoPropuestasDao, M:reportePagoPropuestas");
			e.printStackTrace();
		}
		return listResult;
	}

	public List<Map<String, Object>> reportePagoPropuestasDetalle(PagosPropuestosDto dto){
		StringBuffer sql = new StringBuffer();
		StringBuffer sEnc = new StringBuffer();
		StringBuffer sCND = new StringBuffer();
		double pdTipoCambio=0;
		List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
		
		try {
			if(dto.getCveControl() != null && !dto.getCveControl().equals(""))
				pdTipoCambio = obtenerTipoCambio(consultarConfiguraSet(772), obtenerFechaHoy());
			
			if(!dto.getCveControl().equals("")) { //esta condicion en el codigo de visual siempre entra aqui, no le veo sentido al else lo dejo de todas maneras VT
		        sEnc.append(" SELECT m.cve_control, fec_propuesta, \n");
		        sEnc.append(" case when m.id_divisa = 'MN' then importe else 0 end as importe_mn, \n");
		        sEnc.append(" case when m.id_divisa = 'DLS' then importe else 0 end as importe_dls, \n");
		        sEnc.append(" case when m.id_divisa = 'DLS' then m.importe else case when m.id_divisa = 'MN' then m.importe / " + pdTipoCambio + " else m.importe end end as total_dls \n");
			}else
		        sEnc.append(" SELECT m.cve_control, m.id_banco, m.fec_propuesta, m.no_empresa \n");
		    
		    sEnc.append(" ,m.id_banco_benef, cfp.desc_forma_pago, e.no_empresa, e.nom_empresa, \n");
		    sEnc.append(" case when m.id_banco_benef is null or m.id_banco_benef = 0 then '' \n");
		    sEnc.append(" when m.id_banco = m.id_banco_benef then 'Mismo banco' when m.id_banco <> m.id_banco_benef then 'Interbancario' end as mismo_banco, \n");
		    sEnc.append(" m.id_chequera, cb.desc_banco \n");
		    sEnc.append(" FROM movimiento m, empresa e, cat_forma_pago cfp, cat_banco cb \n");
		    sEnc.append(" WHERE \n");
		    
		    sCND.append(" m.cve_control in (" + dto.getCveControl() + ") \n");
	        sCND.append(" and cb.id_banco = m.id_banco \n");
	        sCND.append(" and m.id_forma_pago = cfp.id_forma_pago \n");
	        sCND.append(" and m.id_tipo_movto = 'E' \n");
	        sCND.append(" and m.no_empresa = e.no_empresa \n");
	        
	        if(dto.isBLocal())
	            sCND.append(" and e.b_local = 'S' \n");
	        
	        if(dto.getTipoReporte() == 2) {
		        if(dto.isBTambienIntercos())
		            sCND.append(" and m.id_estatus_mov in ('N','C','F','L') \n");
		        else
		            sCND.append(" and m.id_estatus_mov in ('N','C','F') \n");
	        }else if(dto.getTipoReporte() == 3) {
	        	if(dto.isBTambienIntercos())
		            sCND.append(" and m.id_estatus_mov in ('N','A','V','L') \n");
		        else
		            sCND.append(" and m.id_estatus_mov in ('N','A','V') \n");
	        }
	        
	        sCND.append(" and ((m.id_forma_pago = 3 and m.id_leyenda <> '*') or \n");
	        sCND.append(" (m.id_forma_pago in (1, 5, 6, 7, 9, 10))) \n");
	        sCND.append(" and (m.id_tipo_operacion = 3000 or (m.id_tipo_operacion between 3800 and 3899)) \n");
	        
	        sCND.append(" and m.no_empresa in ( SELECT no_empresa \n");
	        sCND.append("                      FROM usuario_empresa \n");
	        sCND.append("                      WHERE no_usuario = " + dto.getIdUsuario() + ") \n");
	        
	        if(dto.getAutorizaPropuesta().equals("SI")) {
	        	if(dto.isBSoloUsrAct()) {
	        		if(!gsDBM.equals("SYBASE")) {
	        			sql.append("  SELECT cve_control, fec_propuesta, \n");
		                sql.append("          sum(importe_mn) as importe_mn, \n");
		                sql.append("          sum(importe_dls) as importe_dls, sum(total_dls) as total_dls, \n");
		                sql.append("          desc_forma_pago as forma_pago, no_empresa, nom_empresa, mismo_banco, id_chequera, desc_banco \n"); //', desc_banco_benef, id_chequera_benef \n");
		                sql.append("        FROM ( \n");
	        		}
		            sql.append(sEnc);
		            sql.append(sCND);
		            
		            if(!gsDBM.equals("SYBASE")) sql.append(" ) a \n");
	        	}else {
	        		if(!gsDBM.equals("SYBASE")) {
		        		sql.append("      SELECT cve_control, fec_propuesta, \n");
		                sql.append("              sum(importe_mn) as importe_mn, \n");
		                sql.append("              sum(importe_dls) as importe_dls, sum(total_dls) as total_dls, \n");
		                sql.append("              desc_forma_pago, no_empresa, nom_empresa, mismo_banco, id_chequera, desc_banco \n");
		                sql.append("            FROM ( \n");
	        		}
	        		sql.append(sEnc);
		            sql.append(sCND);
	        	}
	        	if(!gsDBM.equals("SYBASE")) {
	        		sql.append("  ) a group by cve_control, desc_forma_pago, no_empresa, nom_empresa, \n");
		        	sql.append("  id_chequera, desc_banco , fec_propuesta, mismo_banco order by desc_forma_pago \n");
		        	//sql.append("  }  AS cmdFormaPago COMPUTE cmdFormaPago By 'cve_control', 'desc_banco', 'id_chequera' \n");
	        	}//else
	        		//sql.append("  AT ISOLATION READ UNCOMMITTED \n");
	        }
	        System.out.println("reporte detallado"+sql);
	        listResult = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public Map mapRow(ResultSet rs, int idx) throws SQLException {
					Map map = new HashMap();
					map.put("cve_control", rs.getString("cve_control"));
					map.put("fec_propuesta", rs.getDate("fec_propuesta"));
					map.put("importe_mn", rs.getBigDecimal("importe_mn").doubleValue());
					map.put("importe_dls", rs.getBigDecimal("importe_dls").doubleValue());
					map.put("desc_banco", rs.getString("desc_banco"));
					map.put("id_chequera", rs.getString("id_chequera"));
					map.put("nom_empresa", rs.getString("nom_empresa"));
	    			return map;
				}
			});
	        
	        System.out.println("Query: " + sql.toString());
	        
	    }catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PagoPropuestasDao, M:reportePagoPropuestasDetalle");
			e.printStackTrace();
		}
		return listResult;
	}
	

	public void actualizarFecPago(String cveControl, String fecHoy) {
		StringBuffer sql = new StringBuffer();
		
		try {
			System.out.println("  ultimi update ");
			sql.append(" UPDATE seleccion_automatica_grupo SET fecha_pago = '" + fecHoy + "' \n");
			sql.append(" WHERE cve_control = '" + cveControl + "'");
			System.out.println(" ultimi update "+ sql);
			jdbcTemplate.update(sql.toString());
			
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasDao, M:actualizarFecPago");
		}
	}
	
	public List<MovimientoDto> buscaDatosConfigContable(String cveControl) {
		StringBuffer sql = new StringBuffer();
		List<MovimientoDto> listResp = new ArrayList<MovimientoDto>();
		
		try {
			sql.append(" SELECT cc.no_folio_det, m.importe, m.no_docto, m.no_factura, m.no_empresa \n");
			sql.append(" FROM movimiento m, seleccion_automatica_grupo sag, config_contable cc \n");
			sql.append(" WHERE m.cve_control = sag.cve_control \n");
			sql.append(" 	And m.no_folio_det = cc.no_folio_det \n");
			sql.append(" 	And m.cve_control = '"+ cveControl +"' \n");
			sql.append(" 	And m.id_tipo_operacion = 3000 \n");
			sql.append(" 	And not sag.fecha_pago is null \n");
			
			listResp= jdbcTemplate.query(sql.toString(), new RowMapper<MovimientoDto>(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons = new MovimientoDto();
					cons.setNoFolioDet(rs.getInt("no_folio_det"));
					cons.setImporte(rs.getDouble("importe"));
					cons.setNoDocto(rs.getString("no_docto"));
					cons.setNoFactura(rs.getString("no_factura"));
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PagoPropuestasDaoImpl, M:buscaDatosConfigContable");
		}
		return listResp;
	}

	public List<MovimientoDto> buscaFolioDatosPagados(String cveControl, List<MovimientoDto> listDatPag, int i) {
		StringBuffer sql = new StringBuffer();
		List<MovimientoDto> listResp = new ArrayList<MovimientoDto>();
		
		try {
			sql.append(" SELECT no_folio_det \n");
			sql.append(" FROM movimiento \n");
			sql.append(" WHERE cve_control = '"+ cveControl +"' \n");
			sql.append(" 	And importe = "+ listDatPag.get(i).getImporte() +" \n");
			sql.append(" 	And no_factura = '"+ listDatPag.get(i).getNoFactura() +"' \n");
			sql.append(" 	And no_empresa = "+ listDatPag.get(i).getNoEmpresa() +" \n");
			sql.append(" 	And ((id_tipo_operacion = 3200 and (grupo_pago is null or grupo_pago = 0) and id_estatus_mov = 'P') \n");
			sql.append(" 		Or  (id_tipo_operacion = 3201 and grupo_pago != 0)) \n");
			
			listResp= jdbcTemplate.query(sql.toString(), new RowMapper<MovimientoDto>(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons = new MovimientoDto();
					cons.setNoFolioDet(rs.getInt("no_folio_det"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PagoPropuestasDaoImpl, M:buscaFolioDatosPagados");
		}
		return listResp;
	}
	

	public int actFolioPagConfigCont(int noFolioDetPag, int noFolioDet) {
		StringBuffer sql = new StringBuffer();
		int resp = 0;
		
		try {
			sql.append(" UPDATE config_contable SET no_folio_det_pag = "+ noFolioDetPag +" \n");
			sql.append(" WHERE no_folio_det = "+ noFolioDet +" ");
			
			jdbcTemplate.update(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PagoPropuestasDaoImpl, M:actFolioPagConfigCont");
		}
		return resp;
	}
	
	public int actualizaPagosCruzados (MovimientoDto folios){
		StringBuffer sql = new StringBuffer();
		int result = 0;
		try{
			sql.append("UPDATE movimiento ");
			sql.append("\n   SET Importe = Importe_Original, tipo_cambio = 1, Id_Divisa = Id_Divisa_Original");
			sql.append(" WHERE (no_folio_det in( "	);
			sql.append(folios.getPsFolios());
			sql.append( ")");
			sql.append(" or folio_ref in( ");
			sql.append(folios.getPsFolios());
			sql.append("))");
			sql.append(" and id_estatus_mov in('N','C','H')");
			sql.append(" and id_tipo_operacion in(3000,3001)");
			
			System.out.println("Update "+sql);
			return jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " +
		e.toString() + "P:Egresos, C:PagoPropuestasDaoImpl, M:actualizaPagosCruzados");
		}
		return result;
	}
	
	public int inserPagosZexpFact (MovimientoDto folios , int idUsuario){
		StringBuffer sqlInsert = new StringBuffer();
		int result = 0;
		try {
			sqlInsert.append("\n INSERT INTO ZEXP_FACT (no_empresa,no_doc_sap,secuencia,no_pedido,no_folio_set,no_factura,fec_valor,fec_factura,origen,no_persona,");
			sqlInsert.append("\n imp_pago,id_divisa,id_divisa_original,tipo_camb,forma_pago,id_banco,id_cheque,referencia,concepto,mandante,fecha_exp, ");
			sqlInsert.append("\n Estatus,Causa_Rech,Fechap,Cheque_Trn,Imp_Usa,Tipo_Operacion,Tipo_Movto,Capital,Interes,Isr,Chequera_Inv,Casa_Bolsa,Hora_Recibo,");
			sqlInsert.append("\n id_usuario_seg,terminalusr,rubro_erp,no_cheque,cve_control)");
		    
			sqlInsert.append("\n Select S.Soiemp,M.No_Docto,1 as secuencia,0 as no_pedido,M.No_Folio_Det,M.No_Factura,");
			sqlInsert.append("\n convert(char(10),m.fec_valor,103) as fec_valor , convert(char(10),m.fec_valor,103) as fec_factura,m.origen_mov,"); 
			sqlInsert.append("\n case id_tipo_operacion ");
			sqlInsert.append("\n when 3000 ");
			sqlInsert.append("\n then  (select equivale_persona from persona where no_persona=m.no_cliente)");
			sqlInsert.append("\n when 3801 ");
			sqlInsert.append("\n then (select distinct no_prov_as400 from zimp_fact where no_doc_sap=m.no_docto)end as no_persona,");
			sqlInsert.append("\n m.importe as importe,m.id_divisa,m.id_divisa_original,1 as tipo_camb,");
			sqlInsert.append("\n case when M.id_forma_pago in (3,6,8,5,10)  then 3 ");
			sqlInsert.append("\n else case when M.id_forma_pago = 1 then 1  ");
			sqlInsert.append("\n Else M.Id_Forma_Pago End  End as forma_pago,M.Id_Banco,");
			sqlInsert.append("\n m.id_chequera,'' as referencia, m.concepto,'000' as mandante, convert(char(10),m.fec_valor,103) as fecha_exp,'P','',");
			sqlInsert.append("\n convert(char(10),m.fec_propuesta,103) as Fechap,0 as Cheque_Trn,M.Importe as Imp_Usa,3200 as Tipo_Operacion,'E' as Tipo_Movto,0.0 as capital,0.0 as interes,0.0 as isr,M.Id_Chequera,0 as Casa_Bolsa,");
			sqlInsert.append("\n (Select convert(char(10),getdate(), 108)),");
			sqlInsert.append("\n (select substring(clave_usuario,1,10) ");
			sqlInsert.append("\n From seg_usuario ");
			sqlInsert.append("\n where id_usuario="+idUsuario+"),"); 
			sqlInsert.append("\n (select host_name()),case when m.id_forma_pago in (3,6,8,5,10)  then 103 else case when m.id_forma_pago = 1 then 101  else 0 end  end,0 as no_cheque,Coalesce(cve_control,'') as cve_control ");
			sqlInsert.append("\n from movimiento m  join Cat_Forma_Pago fp on M.Id_Forma_Pago=Fp.Id_Forma_Pago, set006 s ");
			sqlInsert.append("\n where m.no_empresa=s.setemp ");
			sqlInsert.append("\n and (m.PO_HEADERS = '' or m.PO_HEADERS is null)");
			sqlInsert.append("\n And S.Siscod='CP'");
			sqlInsert.append("\n And Id_Tipo_Operacion In (3000,3801)");
			//sqlInsert.append("\n And No_Folio_Det In( Select No_Folio_Det From Movimiento Where Cve_Control in ('"+ folios.getCveControl()+"'))"); //folio del detalle
			sqlInsert.append("\n And No_Folio_Det In( "+folios.getPsFolios()  +")"); //folio del detalle
			System.out.println("consulta sql de insert "+sqlInsert.toString());
			result =  jdbcTemplate.update(sqlInsert.toString());
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:PagoPropuestasDaoImpl, M:inserPagosZexpFact");
		}
		return result;
	}
	
	public DT_Pagos_OBPagos pagosParaWebservice (MovimientoDto folios, String fecHoy, int idUsuario){
		StringBuffer sql = new StringBuffer();
		List<DT_Pagos_OBPagos> listDtPagos = new ArrayList<DT_Pagos_OBPagos>();
		DT_Pagos_OBPagos dt_Pagos_OBPagos = null;
		try {
			if(folios.getCveControl()!=null && !folios.getCveControl().equals("")){
				
				sql.append("\n  Select top 1 SOIEMP, NO_DOCTO, SECUENCIA, NO_PEDIDO, NO_FOLIO_DET, ");
       			sql.append("\n  NO_FACTURA, FEC_VALOR, FEC_FACTURA, ORIGEN_MOV, ");
       			sql.append("\n  NO_PERSONA, IMPORTE, ID_DIVISA, ID_DIVISA_ORIGINAL, "); 
       			sql.append("\n  TIPO_CAMB, FORMA_PAGO, ID_BANCO, REFERENCIA, ");
       			sql.append("\n  CONCEPTO, MANDANTE, FECHAP, CHEQUE_TRN, IMP_USA, "); 
       			sql.append("\n  TIPO_OPERACION, TIPO_MOVTO, INTERES, ISR, ");
       			sql.append("\n  ID_CHEQUERA, CASA_BOLSA, NO_CHEQUE, CVE_CONTROL, "); 
       			sql.append("\n  CLASE_DOCTO, LIBRO_MAYOR, OPER_MAY_ESP, NO_COBRADOR, IMPORTE_DES,"); 
      			sql.append("\n  (Case When Id_Divisa <> Id_Divisa_Original Then   ABS( Round(Importe_Des - Importe, 2)) Else 0 End) As Capital,");
      			sql.append("\n  (case when id_divisa <> id_divisa_original then (Case When(Importe_Des - Importe)  >= 0 Then ");
      			sql.append("\n  (select clave_contable from Conf_Pago_Cruzado_Sap where Id_Conf = 2)  Else");
      			sql.append("\n  (Select Clave_Contable From Conf_Pago_Cruzado_Sap Where Id_Conf = 1) ");
      			sql.append("\n  End) Else '' End ) As Clave_Contable,");
      			sql.append("\n  (case when id_divisa <> id_divisa_original then");
       			sql.append("\n  (Case When(Importe_Des - Importe)  >= 0 Then ");
      			sql.append("\n  (select CUENTA from Conf_Pago_Cruzado_Sap where Id_Conf = 2) Else");
      			sql.append("\n  (Select Cuenta From Conf_Pago_Cruzado_Sap Where Id_Conf = 1)");
      			sql.append("\n  END) else '' end) AS cargo");
      			sql.append("\n  FROM(");
				
				
				sql.append("\n  Select S.Soiemp, '");
				//sql.append(folios.getBGenContable());
				sql.append("\n	' as no_docto");
				sql.append("\n	,1 As Secuencia,0 As No_Pedido,M.No_Folio_Det,substring(M.No_Factura,1,10) as No_Factura,");
				sql.append("\n  convert(char(10),m.fec_valor, 103)  as fec_valor, convert(char(10),m.FEC_VALOR,103) as fec_factura,m.origen_mov,"); 
				sql.append("\n  case id_tipo_operacion ");
				sql.append("\n  when 3000 ");
				sql.append("\n  then  (select equivale_persona from persona where no_persona=m.no_cliente)");
				sql.append("\n  when 3801 ");
				sql.append("\n  then (select distinct no_prov_as400 from zimp_fact where no_doc_sap=m.no_docto)end as no_persona,");
				sql.append("\n  (select sum(importe) from movimiento where No_Folio_Det In( "+folios.getPsFolios() +")) as importe");
				sql.append("\n	,m.id_divisa,m.id_divisa_original,  (select valor from valor_divisa vd where vd.fec_divisa = (select fec_hoy from fechas) and rtrim(ltrim(vd.id_divisa)) = rtrim(ltrim(m.id_divisa_original))) as tipo_camb  ,");
				sql.append("\n  case when M.id_forma_pago in (3,6,8,5,10)  then 3 ");
				sql.append("\n  else case when M.id_forma_pago = 1 then 1  ");
				sql.append("\n  Else M.Id_Forma_Pago End  End as forma_pago,M.Id_Banco,");
				sql.append("\n  '' As Referencia, M.Concepto,'000' As Mandante,");
				sql.append("\n  convert(char(10),m.fec_propuesta,103) As Fechap,0 As Cheque_Trn,");
				sql.append("\n  (select sum(importe) from movimiento where No_Folio_Det In( "+folios.getPsFolios() +")) as Imp_Usa,");
				sql.append("\n  3200 As Tipo_Operacion,'E' As Tipo_Movto,");
				sql.append("\n  0.0 As Interes,0.0 As Isr,M.Id_Chequera,0 As Casa_Bolsa,");
				sql.append("\n  0 as no_cheque,Coalesce(cve_control,'') as cve_control,");
				sql.append("\n  Fp.Equiv_Forma_Pago As Clase_Docto, Cerp.Libro_Mayor, ");
				sql.append("\n	(Case When Oper_May_Esp Is  Null or Oper_May_Esp = ''"); 
				sql.append("\n	Then ''"); 
				sql.append("\n	Else Oper_May_Esp End)  As Oper_May_Esp, M.no_cobrador ");
				
				sql.append("\n	,(Case When M.Id_Divisa = 'MN' And M.Id_Divisa_Original = 'DLS' ");
  				sql.append("\n	Then ");
  				sql.append("\n	(Select Sum(Importe_Desglosado) From Movimiento Where No_Folio_Det In("+folios.getPsFolios() +"))");
  				sql.append("\n	Else ");
  				sql.append("\n	0 ");
  				sql.append("\n	End ");
  				sql.append("\n	) As Importe_Des ");
 
				
				
				
				sql.append("\n  from movimiento m  join Cat_Forma_Pago fp on M.Id_Forma_Pago=Fp.Id_Forma_Pago Join Cat_Ctas_Contables_Erp Cerp On Cerp.Id_Chequera = M.Id_Chequera and m.id_banco = Cerp.id_banco, set006 s ");
				sql.append("\n  where m.no_empresa=s.setemp ");
				sql.append("\n  And Cerp.cargo_abono='E'");
				sql.append("\n 	and (m.PO_HEADERS = '' or m.PO_HEADERS is null)");
				sql.append("\n  And S.Siscod='CP'");
				sql.append("\n  And Id_Tipo_Operacion In (3000,3801)");
				sql.append("\n 	And No_Folio_Det In( "+folios.getPsFolios() +") "); 
				sql.append("\n	");
				sql.append("\n	) x");
				System.out.println("consulta sql de insert dto"+sql.toString());
				
				listDtPagos = jdbcTemplate.query(sql.toString(), new RowMapper<DT_Pagos_OBPagos>(){
					public DT_Pagos_OBPagos mapRow(ResultSet rs, int idx) throws SQLException {
						return new DT_Pagos_OBPagos(
								rs.getString("Soiemp"), rs.getString("No_Docto"), rs.getString("secuencia"), 
								rs.getString("no_pedido"), rs.getString("No_Folio_Det"), rs.getString("No_Factura"), 
								rs.getString("fec_valor"), rs.getString("fec_factura"), rs.getString("origen_mov"), 
								rs.getString("no_persona"), rs.getString("importe"), rs.getString("id_divisa"), 
								rs.getString("id_divisa_original"), rs.getString("tipo_camb"), rs.getString("forma_pago"), 
								rs.getString("Id_Banco"), rs.getString("libro_mayor"), rs.getString("referencia"), 
								rs.getString("concepto"), rs.getString("mandante"), rs.getString("Oper_May_Esp") != null && !rs.getString("Oper_May_Esp").equals("")? rs.getString("Oper_May_Esp"):"", 
								"", /*ind iva*/rs.getString("Clave_Contable"), rs.getString("Fechap"),   
								rs.getString("Cheque_Trn"), rs.getString("Imp_Usa"), rs.getString("Tipo_Operacion"),
								rs.getString("Tipo_Movto"), rs.getString("Capital"), rs.getString("interes"),
								rs.getString("isr"), rs.getString("libro_mayor"), rs.getString("Casa_Bolsa"),
								"", "", "",
								rs.getString("no_cobrador") != null && !rs.getString("no_cobrador").equals("") ?
										funciones.ajustarLongitudCampo(rs.getString("no_cobrador"), 4, "D", "", "0"):""
											, rs.getString("no_cheque"), rs.getString("cve_control"),
								"", "", "",
								"", "", "",
								"", "", "",
								"", "", "",
								"", "", rs.getString("cargo"),
								"", "", "",
								"", "", "", rs.getString("clase_docto"));
					}
				});
				System.out.println("dto "+listDtPagos);
				
				//dt_Pagos_OBPagos = listDtPagos.isEmpty() ? null : listDtPagos.get(0);
				
				if (listDtPagos.isEmpty()) {
					dt_Pagos_OBPagos = null;
				} else {
					listDtPagos.get(0).setNO_DOC_SAP(folios.getBGenContable());
					dt_Pagos_OBPagos = listDtPagos.get(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:PagoPropuestasDaoImpl, M:pagosParaWebservice");
		}
		return dt_Pagos_OBPagos;
	}
	
	public int actualizaMovimientoCompensado(MovimientoDto folios, DT_Pagos_ResponsePagosResponse pagosRespuestaq,String fecHoy){
		String sqlActulizaMob = "";
		int res = 0;
		try {
			sqlActulizaMob = "update movimiento set fec_exportacion = convert(datetime,'"+ Utilerias.validarCadenaSQL(fecHoy) +"',103) ,lote_salida = '1', ";
			sqlActulizaMob += "PO_HEADERS = '"+pagosRespuestaq.getDOC_POLIZA_SAP()+"'";
			sqlActulizaMob += " where no_folio_det  in ( "+folios.getPsFolios() +") ";
			res = jdbcTemplate.update(sqlActulizaMob);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:PagoPropuestasDaoImpl, M:actualizaMovimientoCompensado");
		}
		return res;
	}
	
	public int insertaBitacoraPagos (DT_Pagos_ResponsePagosResponse pagosRespuestaq){
		//StringBuffer sqlBitacora = new StringBuffer();
		int result = 0;
		List<Map<String, String>> mensaje = null;
		try {
			String cadanajson = "["+pagosRespuestaq.getMensaje().substring(0, pagosRespuestaq.getMensaje().length()-1) + "]";
			if(Utilerias.parseaJsonExcel(cadanajson)){
				Gson gson = new Gson();
				mensaje = gson.fromJson(cadanajson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			
			
				for (Map<String, String> map : mensaje) {
					StringBuffer sqlBitacora = new StringBuffer();
					sqlBitacora.append("Insert Into Bitacora_Pago_Propuestas Values('");
					sqlBitacora.append(pagosRespuestaq.getNO_EMPRESA());
					sqlBitacora.append("','");
					sqlBitacora.append(pagosRespuestaq.getNO_DOC_SAP());
					sqlBitacora.append("','");
					sqlBitacora.append(pagosRespuestaq.getSECUENCIA());
					sqlBitacora.append("','");
					sqlBitacora.append(map.get("MENSAJE"));
					sqlBitacora.append("',getdate())");
					result = jdbcTemplate.update(sqlBitacora.toString());
				}
			}else{
				StringBuffer sqlBitacora = new StringBuffer();
				sqlBitacora.append("Insert Into Bitacora_Pago_Propuestas Values('");
				sqlBitacora.append(pagosRespuestaq.getNO_EMPRESA());
				sqlBitacora.append("','");
				sqlBitacora.append(pagosRespuestaq.getNO_DOC_SAP());
				sqlBitacora.append("','");
				sqlBitacora.append(pagosRespuestaq.getSECUENCIA());
				sqlBitacora.append("','");
				sqlBitacora.append(pagosRespuestaq.getMensaje());
				sqlBitacora.append("',getdate())");
				result = jdbcTemplate.update(sqlBitacora.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:PagoPropuestasDaoImpl, M:pagosParaWebservice");
		}
		return result;
	}
	

	public List<MovimientoDto> consultarPagPropApagar(ComunEgresosDto dtoIn){
		String sql="";
		List<MovimientoDto>listDatos= new ArrayList<MovimientoDto>();
		try{
			sql+= " SELECT m.beneficiario, m.no_empresa, no_cliente as no_proveedor, st.soiemp as emp, ";
		    sql+= "\n        id_divisa, id_forma_pago, m.id_banco as id_banco_pag, m.id_chequera_benef, ";
		    sql+= "\n        m.id_chequera as id_chequera_pago, no_folio_det, no_docto, m.Invoice_Type, coalesce (m.C_Periodo, 0) as C_Periodo, c_lote, m.cve_control ";
		        
		    if(dtoIn.getPsDivision()!=null && dtoIn.getPsDivision().equals(""))
		    {
		        sql+= "\n   FROM movimiento m, grupo_empresa g, cat_grupo_flujo cgf, ";
		        sql+= "\n       empresa s, grupo_flujo_cupo gfc, cat_grupo_cupo cgc , set006 st ";
		        sql+= "\n  WHERE m.no_empresa = g.no_empresa ";
		        sql+= "\n    AND g.no_empresa = s.no_empresa ";
		        sql+= "\n    AND g.id_grupo_flujo = cgf.id_grupo_flujo ";
		    }
		    else
		    {
		        sql+= "\n   FROM movimiento m, empresa s, grupo_flujo_cupo gfc, ";
		        sql+= "\n        cat_grupo_cupo cgc, cat_cta_banco_division ccbd ";
		        sql+= "\n  WHERE m.no_empresa = s.no_empresa ";
		        sql+= "\n    AND m.division = '" +Utilerias.validarCadenaSQL(dtoIn.getPsDivision())+"' ";
		        sql+= "\n    AND m.no_empresa = ccbd.no_empresa ";
		        sql+= "\n    AND m.id_banco = ccbd.id_banco ";
		        sql+= "\n    AND m.id_chequera = ccbd.id_chequera ";
		        sql+= "\n    AND m.division = ccbd.id_division ";
		    }
		    
		    sql+= "\n    AND m.id_rubro = gfc.id_rubro ";
		    sql+= "\n    AND gfc.id_grupo_cupo = cgc.id_grupo_cupo ";
		    sql+= "\n    AND m.id_tipo_movto = 'E' ";
		    sql+= "\n    AND (m.origen_mov <> 'INV' or m.origen_mov is null) ";
		    sql+= "\n    And M.No_Empresa=St.Setemp ";
    		sql+= "\n    and St.Siscod='CP' ";
		    sql+= "\n    AND m.id_estatus_mov in ('N','C','F') ";
		    sql+= "\n    and (m.po_headers is null or m.po_headers ='')  ";	
		    sql+= "\n    AND ((m.id_forma_pago = 3 and coalesce(m.id_leyenda, ' ') <> '*') or ";
		    sql+= "\n	(M.No_Cliente = (Select Max(P.No_Persona) From  Persona P  ";
		    sql+= "\n	Join Conf_Pago_Cruzado Cxp On P.Equivale_Persona = Cxp.No_Proveedor ";
		    sql+= "\n	where p.id_tipo_persona ='P' and ltrim(rtrim(m.id_divisa)) = ltrim(rtrim(cxp.id_divisa_pago)))) or ";

		    sql+= "\n        (m.id_forma_pago in (1, 5, 9, 7, 10))) ";
//		    sql+= "\n    AND m.fec_propuesta = '"+funciones.ponerFecha(dtoIn.getFechaPago())+"'";
		    sql+= "\n    AND m.cve_control = '"+Utilerias.validarCadenaSQL(dtoIn.getCveControl())+"'";
		//    sql+= "\n    AND gfc.id_grupo_cupo = "+dtoIn.getIdGrupoRubros();
		    //parte verdadera
		    /*if(dtoIn.getPsAgrupaCheques().equals("SI") || dtoIn.getPsAgrupaTransfers().equals("SI")) {
		    	sql+= "\n Order by m.no_empresa, no_proveedor, m.beneficiario, id_divisa, id_forma_pago, ";
		    	sql+= "\n          id_banco_pag, id_chequera_pago ";
		    }else {*/
		    	sql+= "\n Order by m.no_empresa, no_proveedor, m.beneficiario, id_divisa, id_forma_pago, ";
		    	sql+= "\n          id_banco_pag, id_chequera_pago, id_chequera_benef ";
		    	/*
		    	sql+= "\n Order by m.no_empresa, id_forma_pago, no_folio_det, no_proveedor, m.beneficiario, id_divisa, ";
		    	sql+= "\n          id_banco_pag, id_chequera_pago ";*/ //se comento por que se quito el check de agrupar asi siempre los va agrupar
		    //}
		    
		    System.out.println("\n Consulta consultarPagPropApagar -->"+sql);
		    listDatos= jdbcTemplate.query(sql, new RowMapper<MovimientoDto>(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons= new MovimientoDto();
					cons.setBeneficiario(rs.getString("beneficiario"));
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setDeudor(rs.getString("emp"));
					cons.setIdProveedor(rs.getInt("no_proveedor"));
					cons.setIdDivisa(rs.getString("id_divisa"));
					cons.setIdFormaPago(rs.getInt("id_forma_pago"));
					cons.setIdBancoPago(rs.getInt("id_banco_pag"));
					cons.setIdChequeraPago(rs.getString("id_chequera_pago"));
					cons.setNoFolioDet(rs.getInt("no_folio_det"));
					cons.setNoDocto(rs.getString("no_docto"));
					cons.setInvoiceType(rs.getString("Invoice_Type") != null
										&& !rs.getString("Invoice_Type").equals("")? rs.getString("Invoice_Type"):"000");
					cons.setCPeriodo(rs.getInt("C_Periodo") != 0 ? Integer.parseInt(rs.getString("C_Periodo").substring(2, 4)):00);
					cons.setCLote(rs.getInt("c_lote") != 0 ? rs.getInt("c_lote"):0 );
					cons.setCveControl(rs.getString("cve_control"));
					cons.setIdChequeraBenef(rs.getString("id_chequera_benef"));
					
					return cons;
				}});
		  
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagoPropuestasDao, M:consultarPagPropApagar");
			e.printStackTrace();
		}
		return listDatos;
	}
	//funSQLUpdateMovtoTC
	public int actualizaMovtoTC(int idUsuario, String fecModif, Double tipoCambio, String foliosDet,
			 String noEmpresa, String noDocto){
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("UPDATE movimiento ");
			sql.append("\n   SET usuario_modif = ");
			sql.append(idUsuario);
			sql.append(" ,fec_modif = getdate() ");
	
			sql.append(",importe = Round(importe * ");
			sql.append(tipoCambio);
			sql.append(" ,2) ,id_divisa = 'MN'");
			sql.append(" ,tipo_cambio = ");
			sql.append(tipoCambio);
			sql.append(" WHERE (no_folio_det in( "	);
			sql.append(foliosDet);
			sql.append( ")");
			sql.append(" or folio_ref in( ");
			sql.append(foliosDet);
			sql.append("))");
			sql.append(" and id_estatus_mov in('N','C','H')");
			sql.append(" and id_tipo_operacion in(3000,3001)");
			sql.append(" and no_docto in('");
			sql.append(noDocto);
			sql.append("')");
			System.out.println("Update "+sql);
			return jdbcTemplate.update(sql.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagoPropuestasDaoImpl, M:actualizaMovtoTC");
			e.printStackTrace();
			return 0;
		}
	}
	//FunSQLSelectPropAgrup
	public List<ComunEgresosDto> consultarPropAgrup(List<ComunEgresosDto> dtoIn){
		List<ComunEgresosDto> listRet = new ArrayList<ComunEgresosDto>();
		StringBuffer sql = new StringBuffer();
		try {
			for (int i = 0; i < dtoIn.size(); i++) {
				sql.append("");
				sql.append(" select distinct substring(cve_control,1,1) as cveControl,id_grupo,");
				sql.append(" id_grupo_flujo,fecha_propuesta ");
				sql.append(" from seleccion_automatica_grupo");
				sql.append(" where cve_control in('" );
				sql.append(Utilerias.validarCadenaSQL(dtoIn.get(i).getCveControl()));
				sql.append("')");
				listRet= jdbcTemplate.query(sql.toString(), new RowMapper<ComunEgresosDto>(){
					public ComunEgresosDto mapRow(ResultSet rs, int idx) throws SQLException {
						ComunEgresosDto cons= new ComunEgresosDto();
						cons.setCveControl(rs.getString("cveControl"));
						cons.setIdGrupoEmpresas(Integer.parseInt(rs.getString("id_grupo")));
						cons.setIdGrupoRubros(Integer.parseInt(rs.getString("id_grupo_flujo")));
						cons.setFecha(rs.getDate(rs.getString("fecha_propuesta")));
						return cons;
					}});
				sql.delete(0, sql.length());
			}
	
		} catch(CannotGetJdbcConnectionException k){
			k.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + k.toString()
			+ "P:Egresos, C:ConsultaPropuestasDaoImpl, M:consultarPropAgrup");
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDaoImpl, M:consultarPropAgrup");
			e.printStackTrace();
		}
		return listRet;
	}
	/*
	public List<MovimientoDto> consultarPagPropApagar1(ComunEgresosDto dtoIn, List<ComunEgresosDto> listDtoIn){
		String sql="";
		List<MovimientoDto>listDatos= new ArrayList<MovimientoDto>();
		try{
			for (int i = 0; i < listDtoIn.size(); i++) {
				
				
				sql+= " SELECT m.beneficiario, m.no_empresa, no_cliente as no_proveedor, ";
			    sql+= "\n        id_divisa, id_forma_pago, m.id_banco as id_banco_pag, ";
			    sql+= "\n        m.id_chequera as id_chequera_pago, no_folio_det ";
			        
			    if(dtoIn.getPsDivision()!=null && dtoIn.getPsDivision().equals(""))
			    {
			        sql+= "\n   FROM movimiento m, grupo_empresa g, cat_grupo_flujo cgf, ";
			        sql+= "\n       empresa s, grupo_flujo_cupo gfc, cat_grupo_cupo cgc ";
			        sql+= "\n  WHERE m.no_empresa = g.no_empresa ";
			        sql+= "\n    AND g.no_empresa = s.no_empresa ";
			        sql+= "\n    AND g.id_grupo_flujo = cgf.id_grupo_flujo ";
			    }
			    else
			    {
			        sql+= "\n   FROM movimiento m, empresa s, grupo_flujo_cupo gfc, ";
			        sql+= "\n        cat_grupo_cupo cgc, cat_cta_banco_division ccbd ";
			        sql+= "\n  WHERE m.no_empresa = s.no_empresa ";
			        sql+= "\n    AND m.division = '" +dtoIn.getPsDivision()+"' ";
			        sql+= "\n    AND m.no_empresa = ccbd.no_empresa ";
			        sql+= "\n    AND m.id_banco = ccbd.id_banco ";
			        sql+= "\n    AND m.id_chequera = ccbd.id_chequera ";
			        sql+= "\n    AND m.division = ccbd.id_division ";
			    }
			    
			    sql+= "\n    AND m.id_rubro = gfc.id_rubro ";
			    sql+= "\n    AND gfc.id_grupo_cupo = cgc.id_grupo_cupo ";
			    sql+= "\n    AND m.id_tipo_movto = 'E' ";
			    sql+= "\n    AND (m.origen_mov <> 'INV' or m.origen_mov is null) ";
			    sql+= "\n    AND m.id_estatus_mov in ('N','C','F') ";
			    sql+= "\n    AND ((m.id_forma_pago = 3 and m.id_leyenda <> '*') or ";
			    sql+= "\n        (m.id_forma_pago in (1, 5, 9, 7))) ";
	//VT		    sql+= "\n    AND m.fec_propuesta = '"+funciones.ponerFecha(dtoIn.getFechaPago())+"'";
			    sql+= "\n    AND m.cve_control = '"+dtoIn.getCveControl()+"'";
			//    sql+= "\n    AND gfc.id_grupo_cupo = "+dtoIn.getIdGrupoRubros();
			    //parte verdadera
			    if(dtoIn.getPsAgrupaCheques().equals("SI") || dtoIn.getPsAgrupaTransfers().equals("SI")) {
			    	sql+= "\n Order by m.no_empresa, no_proveedor, m.beneficiario, id_divisa, id_forma_pago, ";
			    	sql+= "\n          id_banco_pag, id_chequera_pago ";
			    }else {
			    	sql+= "\n Order by m.no_empresa, id_forma_pago, no_folio_det, no_proveedor, m.beneficiario, id_divisa, ";
			    	sql+= "\n          id_banco_pag, id_chequera_pago ";
			    }
			    
			 
			    listDatos= jdbcTemplate.query(sql, new RowMapper<MovimientoDto>(){
					public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
						MovimientoDto cons= new MovimientoDto();
						cons.setBeneficiario(rs.getString("beneficiario"));
						cons.setNoEmpresa(rs.getInt("no_empresa"));
						cons.setIdProveedor(rs.getInt("no_proveedor"));
						cons.setIdDivisa(rs.getString("id_divisa"));
						cons.setIdFormaPago(rs.getInt("id_forma_pago"));
						cons.setIdBancoPago(rs.getInt("id_banco_pag"));
						cons.setIdChequeraPago(rs.getString("id_chequera_pago"));
						cons.setNoFolioDet(rs.getInt("no_folio_det"));
						return cons;
					}});
			  
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagoPropuestasDao, M:consultarPagPropApagar");
			e.printStackTrace();
		}
		return listDatos;
	}
	
	*/
	
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

}
