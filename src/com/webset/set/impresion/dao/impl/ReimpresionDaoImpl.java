package com.webset.set.impresion.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.webset.set.bancaelectronica.dto.CatBancoDto;
import com.webset.set.bancaelectronica.dto.CatEquivaleCuentaContableDto;
import com.webset.set.bancaelectronica.dto.DetArchTransferDto;
import com.webset.set.egresos.dto.ControlProyectoDto;
import com.webset.set.impresion.dao.ReimpresionDao;
import com.webset.set.impresion.dto.CatFirmasDto;
import com.webset.set.impresion.dto.ConfiguracionChequeDto;
import com.webset.set.impresion.dto.ConsultaChequesDto;
import com.webset.set.impresion.dto.ControlPapelDto;
import com.webset.set.impresion.dto.InsertaBitacoraChequesDto;
import com.webset.set.impresion.dto.LayoutProteccionDto;
import com.webset.set.ingresos.dto.CatCtaBancoDto;
import com.webset.set.interfaz.dto.GuiaContableDto;
import com.webset.set.traspasos.dto.MovimientoDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.LlenaComboImpresora;
import com.webset.utils.tools.Utilerias;


@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
public class ReimpresionDaoImpl implements ReimpresionDao{

	private JdbcTemplate jdbcTemplate;
	private Funciones funciones = new Funciones();
	private static Logger logger = Logger.getLogger(ReimpresionDaoImpl.class);
	Bitacora bitacora=new Bitacora();
	ConsultasGenerales consultasGenerales;

	
	/****************	getters && setters	*********************/
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ImpresionDaoImpl, M:setDataSource");
		}
	}
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	/*************************************************************/
	
	
	/**
	 * consulta de cheques pendientes
	 */

	public List<MovimientoDto>consultarChequesPendientes(ConsultaChequesDto dtoCheques){
		
		List<MovimientoDto>listRetorno = new ArrayList<MovimientoDto>();
		StringBuffer sql = new StringBuffer();
		String bHabilita = seleccionarBHabilita();
		boolean banco = false;
		boolean chequera = false;
		
		try{
			
			sql.append("\n SELECT desc_caja,m.no_empresa,");
			sql.append("\n        m.no_folio_det as no_solicitud,");
			sql.append("\n        m.importe,");
			sql.append("\n        m.division,");
			sql.append("\n        m.no_cliente, m.beneficiario,");
			sql.append("\n        cb.desc_banco,");
			sql.append("\n        m.id_chequera  as cta_alias,");
			sql.append("\n        cc.desc_caja,");
			sql.append("\n        m.concepto,");
			sql.append("\n        (select p.razon_social");
			sql.append("\n         from persona p");
			sql.append("\n         where p.no_persona = (select ccb_h.no_empresa");
			sql.append("\n                               from cat_cta_banco ccb_h");
			sql.append("\n                               where m.id_banco = ccb_h.id_banco");
			sql.append("\n                                     and m.id_chequera = ccb_h.id_chequera)");
			sql.append("\n               and p.no_empresa = (select ccb_h.no_empresa");
			sql.append("\n                                   from cat_cta_banco ccb_h");
			sql.append("\n                                   where m.id_banco = ccb_h.id_banco");
			sql.append("\n                                         and m.id_chequera = ccb_h.id_chequera)");
			sql.append("\n               and p.id_tipo_persona = 'E'");
			sql.append("\n        ) as cia_nmbr,");
			sql.append("\n        m.agrupa1 as centro_costo,");
			sql.append("\n        cast(m.id_banco as integer) as bco_cve,");
			//sql.append("\n        convert(int,m.id_banco) as bco_cve,"); COMENTADO EMS
			sql.append("\n        m.id_tipo_movto as tipo_egreso,");
			sql.append("\n        cast(m.importe_original as number) as importe_original,");
			//sql.append("\n        convert(int,m.importe_original) as importe_original,"); COMENTADO EMS
			sql.append("\n        case when (m.no_docto = 'null' or m.no_docto is null) then m.no_factura else m.no_docto end as no_contrarecibo,");
			//sql.append("\n        case when m.no_docto = 'null' then m.no_factura else m.no_docto end as no_contrarecibo,"); COMENTADO EMS
			sql.append("\n        pp.rfc,");
			sql.append("\n        m.id_divisa as moneda,");
			sql.append("\n        m.id_contable,");
			sql.append("\n        m.id_chequera as cta_no,");
			sql.append("\n        m.id_leyenda as leyenda_prote,");
			sql.append("\n        ccb.desc_plaza as plaza,");
			sql.append("\n        ccb.desc_sucursal as sucursal,");
			sql.append("\n        cb.b_protegido,");
			sql.append("\n        m.id_chequera as no_cuenta,");
			sql.append("\n        cb.codigo_seguridad,");
			sql.append("\n        cb.cve_plaza as cve_plaza,");
			sql.append("\n        cb.id_banco as no_banco,");
			
			sql.append("\n        (select d.calle_no + ' ' + d.colonia");
			
			/*COMENTADO EMS
			 * if(gsDBM.equals("ORACLE") || gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2"))
				sql.append("\n        (select d.calle_no || ' ' || d.colonia");
			if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE"))
				sql.append("\n        (select d.calle_no + ' ' + d.colonia");
			*/
			
			sql.append("\n         from direccion d");
			sql.append("\n         where d.no_persona = (select ccb_h.no_empresa");
			sql.append("\n                               from cat_cta_banco ccb_h");
			sql.append("\n                               where m.id_banco = ccb_h.id_banco");
			sql.append("\n                                     and m.id_chequera = ccb_h.id_chequera)");
			sql.append("\n               and d.no_empresa = (select ccb_h.no_empresa");
			sql.append("\n                                   from cat_cta_banco ccb_h");
			sql.append("\n                                   where m.id_banco = ccb_h.id_banco");
			sql.append("\n                                         and m.id_chequera = ccb_h.id_chequera)");
			sql.append("\n               and d.id_tipo_persona = 'E'");
			sql.append("\n               and d.id_tipo_direccion='OFNA'");
			sql.append("\n        ) as cia_dir,");
			
			sql.append("\n        (select d.deleg_municipio + ' ' + d.ciudad");
			
			/* COMENTADO EMS
			if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE"))
				sql.append("\n        (select d.deleg_municipio + ' ' + d.ciudad");
			if(gsDBM.equals("ORACLE") || gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2"))
				sql.append("\n        (select d.deleg_municipio || ' ' || d.ciudad");
			*/
			
			sql.append("\n         from direccion d");
			sql.append("\n         where d.no_persona = (select ccb_h.no_empresa");
			sql.append("\n                               from cat_cta_banco ccb_h");
			sql.append("\n                               where m.id_banco = ccb_h.id_banco");
			sql.append("\n                                     and m.id_chequera = ccb_h.id_chequera)");
			sql.append("\n               and d.no_empresa = (select ccb_h.no_empresa");
			sql.append("\n                                   from cat_cta_banco ccb_h");
			sql.append("\n                                   where m.id_banco = ccb_h.id_banco");
			sql.append("\n                                         and m.id_chequera = ccb_h.id_chequera)");
			sql.append("\n               and d.id_tipo_persona = 'E'");
			sql.append("\n               and d.id_tipo_direccion='OFNA'");
			sql.append("\n        ) as cia_dir2,");
			sql.append("\n        (select p.rfc");
			sql.append("\n         from persona p");
			sql.append("\n         where p.no_persona = (select ccb_h.no_empresa");
			sql.append("\n                               from cat_cta_banco ccb_h");
			sql.append("\n                               where m.id_banco = ccb_h.id_banco");
			sql.append("\n                                     and m.id_chequera = ccb_h.id_chequera)");
			sql.append("\n               and p.no_empresa = (select ccb_h.no_empresa");
			sql.append("\n                                   from cat_cta_banco ccb_h");
			sql.append("\n                                   where m.id_banco = ccb_h.id_banco");
			sql.append("\n                                         and m.id_chequera = ccb_h.id_chequera)");
			sql.append("\n               and p.id_tipo_persona = 'E'");
			sql.append("\n        ) as cia_rfc,");
			sql.append("\n        m.no_cheque as no_cheque,");
//				'    sql.append("\n        firmante1,"
//				'    sql.append("\n        firmante2,"

			sql.append("\n (SELECT RTRIM(RTRIM(LTRIM(cat.nombre)) + ' ' + RTRIM(LTRIM(cat.paterno)) + ' ' + RTRIM(LTRIM(cat.materno)))");
			sql.append("\n    FROM cat_usuario cat");
			sql.append("\n    WHERE cat.no_usuario=m.firmante1) as firmante1,");
			sql.append("\n (SELECT RTRIM(RTRIM(LTRIM(cat.nombre)) + ' ' + RTRIM(LTRIM(cat.paterno)) + ' ' + RTRIM(LTRIM(cat.materno)))");
			sql.append("\n    FROM cat_usuario cat");
			sql.append("\n    WHERE cat.no_usuario=m.firmante2) as firmante2,");
			
				/*COMENTADO EMS
				 * if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE"))
				{
					sql.append("\n (SELECT RTRIM(RTRIM(LTRIM(cat.nombre)) + ' ' + RTRIM(LTRIM(cat.paterno)) + ' ' + RTRIM(LTRIM(cat.materno)))");
					sql.append("\n    FROM cat_usuario cat");
					sql.append("\n    WHERE cat.no_usuario=m.firmante1) as firmante1,");
					sql.append("\n (SELECT RTRIM(RTRIM(LTRIM(cat.nombre)) + ' ' +RTRIM(LTRIM(cat.paterno)) + ' ' + RTRIM(LTRIM(cat.materno)))");
					sql.append("\n    FROM cat_usuario cat");
					sql.append("\n    WHERE cat.no_usuario=m.firmante2) as firmante2,");
				}
				if(gsDBM.equals("ORACLE"))
				{
					sql.append("\n (SELECT RTRIM(RTRIM(LTRIM(cat.nombre)) || ' ' || RTRIM(LTRIM(cat.paterno)) || ' ' || RTRIM(LTRIM(cat.materno)))");
					sql.append("\n    FROM cat_usuario cat");
					sql.append("\n    WHERE cat.no_usuario=m.firmante1) as firmante1,");
					sql.append("\n (SELECT RTRIM(RTRIM(LTRIM(cat.nombre)) || ' ' || RTRIM(LTRIM(cat.paterno)) || ' ' || RTRIM(LTRIM(cat.materno)))");
					sql.append("\n    FROM cat_usuario cat");
					sql.append("\n    WHERE cat.no_usuario=m.firmante2) as firmante2,");
				}
				if(gsDBM.equals("DB2") || gsDBM.equals("POSTGRESQL"))
				{
					sql.append("\n (SELECT TRIM(TRIM(cat.nombre) || ' ' || TRIM(cat.paterno) || ' ' || TRIM(cat.materno))");
					sql.append("\n    FROM cat_usuario cat");
					sql.append("\n    WHERE cat.no_usuario=m.firmante1) as firmante1,");
					sql.append("\n (SELECT TRIM(TRIM(cat.nombre) || ' ' || TRIM(cat.paterno) || ' ' || TRIM(cat.materno))");
					sql.append("\n    FROM cat_usuario cat");
					sql.append("\n    WHERE cat.no_usuario=m.firmante2) as firmante2,");
				}
				*/
			
			sql.append("\n        m.PO_HEADERS,");
			sql.append("\n        m.fec_valor,");
			sql.append("\n        m.INVOICE_TYPE,");
			
			sql.append("\n        m.fec_valor as fec_cheque, ");
			sql.append("\n        pp.equivale_persona as equivale_benef,");
			sql.append("\n        m.id_grupo,");
			sql.append("\n        m.id_rubro,");
			sql.append("\n        m.no_pedido,");
			sql.append("\n        (select p.equivale_persona");
			sql.append("\n         from persona p");
			sql.append("\n         where p.no_persona = (select ccb_h.no_empresa");
			sql.append("\n                               from cat_cta_banco ccb_h");
			sql.append("\n                               where m.id_banco = ccb_h.id_banco");
			sql.append("\n                                     and m.id_chequera = ccb_h.id_chequera)");
			sql.append("\n               and p.no_empresa = (select ccb_h.no_empresa");
			sql.append("\n                                   from cat_cta_banco ccb_h");
			sql.append("\n                                   where m.id_banco = ccb_h.id_banco");
			sql.append("\n                                         and m.id_chequera = ccb_h.id_chequera)");
			sql.append("\n               and p.id_tipo_persona = 'E'");
			sql.append("\n        ) as equivale_empresa,");
			sql.append("\n        m.id_divisa_original,");
			sql.append("\n        (select d.id_cp");
			sql.append("\n         from direccion d");
			sql.append("\n         where d.no_persona = (select ccb_h.no_empresa");
			sql.append("\n                               from cat_cta_banco ccb_h");
			sql.append("\n                               where m.id_banco = ccb_h.id_banco");
			sql.append("\n                                     and m.id_chequera = ccb_h.id_chequera)");
			sql.append("\n               and d.no_empresa = (select ccb_h.no_empresa");
			sql.append("\n                                   from cat_cta_banco ccb_h");
			sql.append("\n                                   where m.id_banco = ccb_h.id_banco");
			sql.append("\n                                         and m.id_chequera = ccb_h.id_chequera)");
			sql.append("\n               and d.id_tipo_persona = 'E'");
			sql.append("\n               and d.id_tipo_direccion='OFNA'");
			sql.append("\n        ) as id_cp,");
			sql.append("\n        cb.dir_logo as logo_banco,");
			sql.append("\n        e.dir_logo as logo_empresa,");
			sql.append("\n        e.dir_logo_alterno,");
			sql.append("\n        m.tipo_cambio,");
			sql.append("\n        m.id_tipo_operacion,");
			sql.append("\n        m.id_caja,");
			sql.append("\n        case");
			sql.append("\n            when ccb.b_impre_continua = 'N' then 'LASER'");
			sql.append("\n            when ccb.b_impre_continua = 'S' then 'CONTINUA'");
			sql.append("\n        end As tipo_impresion");
				
			//EMS: Realizo la consulta con los joins implicitos, ya que hay *= que no funcionen en ORACLE.
			sql.append("\n FROM   movimiento m LEFT JOIN cat_banco cb ON (m.id_banco = cb.id_banco)");
			sql.append("\n        LEFT JOIN cat_caja cc ON (m.id_caja = cc.id_caja)");
			
			
				if(!dtoCheques.getPsEquivalePersona().equals("")){
					
					sql.append("\n        INNER JOIN persona pp ON (m.no_cliente = pp.no_persona ");
					sql.append("\n                                  AND pp.no_empresa = 552 ");
					sql.append("\n                                  AND pp.id_tipo_persona = 'P' ");
					sql.append("\n                                  AND pp.equivale_persona = '" + Utilerias.validarCadenaSQL(dtoCheques.getPsEquivalePersona()) + "')");
					
				}else{
										
					sql.append("\n        LEFT JOIN persona pp ON (m.no_cliente = pp.no_persona ");
				    sql.append("\n                                 AND pp.no_empresa = 552 ");
				    sql.append("\n                                 AND pp.id_tipo_persona = 'P')");

				}
				
				sql.append("\n        LEFT JOIN cat_cta_banco ccb  ON (m.id_banco = ccb.id_banco  AND m.id_chequera = ccb.id_chequera)");
				sql.append("\n        INNER JOIN empresa e ON (m.no_empresa = e.no_empresa)");
				
				if(!dtoCheques.getPsEquivalePersona().equals(""))
					sql.append("\n        AND pp.equivale_persona = '" + Utilerias.validarCadenaSQL(dtoCheques.getPsEquivalePersona()) + "'");
				
				sql.append("\n WHERE ");
				/*sql.append("\n        ((m.id_tipo_operacion=3200 and m.id_estatus_mov = 'P') ");
				//Agregado EMS 05/02/2016: estatus J 
				sql.append("\n         or (m.id_tipo_operacion=3200 and m.id_estatus_mov = 'J') ");
				*/
				//Reimpresion
				sql.append("\n        ((m.id_tipo_operacion=3200 and m.id_estatus_mov = 'I') ");
				//Agregado EMS 05/02/2016: estatus J 
				sql.append("\n         or (m.id_tipo_operacion=3200 and m.id_estatus_mov = 'R')) ");
				
				/*sql.append("\n          or (m.id_tipo_operacion=3000 and id_estatus_mov = 'V'");
				sql.append("\n             and (id_servicio_be is null or id_servicio_be <> 'CVT')))");
				*/
				sql.append("\n        AND m.id_forma_pago in (1,7)");
				
				//Posible cambio de condicion a fecha between fec_hoy y fec_mañana, dependiendo el critero del radio.
				if(dtoCheques.isOptSeleccion() == false){
					sql.append("\n        AND m.fec_cheque <= (select fec_manana from fechas)");
				}
				
				sql.append("\n        AND m.id_caja in (select id_caja ");
				sql.append("\n                          from caja_usuario ");
				sql.append("\n                          where no_usuario = " + dtoCheques.getIdUsuario() + ") ");
				sql.append("\n        AND m.no_empresa in (select no_empresa");
				sql.append("\n                             from usuario_empresa ");
				sql.append("\n                             where no_usuario = " + dtoCheques.getIdUsuario() + ") ");
				
				if(!dtoCheques.getPsbImpreContinua().equals("")){
					sql.append("\n        AND ccb.b_impre_continua = '" +Utilerias.validarCadenaSQL(dtoCheques.getPsbImpreContinua()) + "' ");
				}
					
				if(bHabilita.equals("S")){
					
					if(dtoCheques.isOptPendientes()){
						
						sql.append("\n        AND m.b_autoriza_impre is null");
						sql.append("\n        AND m.firmante1 is null");
						sql.append("\n        AND m.firmante2 is null");
					
					}else if(dtoCheques.isOptPorConfirmar()){
						
						sql.append("\n        AND m.b_autoriza_impre = 'N'");
						sql.append("\n        AND (m.firmante1 is null or m.firmante2 is null)");
					
					}else if(dtoCheques.isOptFirmados()){
						
						sql.append("\n        AND m.b_autoriza_impre = 'S'");
						sql.append("\n        AND m.firmante1 is not null");
						sql.append("\n        AND m.firmante2 is not null");
						
					}
				}
				
				if(dtoCheques.getCriterioBanco() != 0){
				    sql.append("\n        AND m.id_banco = " + dtoCheques.getCriterioBanco());
				    banco = true;
				}
				
				if(dtoCheques.getCriterioChequera() != null && !dtoCheques.getCriterioChequera().trim().equals("")){
					sql.append("\n        AND m.id_chequera = '" + Utilerias.validarCadenaSQL(dtoCheques.getCriterioChequera()) + "'");
					chequera = true;
				}
				
				/*	Se quita criterio division
				 * if(dtoCheques.getCriterioDivision() != 0){
				    sql.append("\n        AND m.division = '" + dtoCheques.getCriterioDivision() + "'");
				}*/
				
				/* Se quita criterio tipo empresa
				 * if(dtoCheques.getCriterioEmpresa() != 0){
				    sql.append("\n        AND m.no_empresa = " + dtoCheques.getCriterioEmpresa());
				}*/
				
				if(dtoCheques.getCriterioCajaPago() != 0){
				    sql.append("\n        AND m.id_caja = " + dtoCheques.getCriterioCajaPago());
				}
				
				if(dtoCheques.getCriterioLote() != 0){
				    sql.append("\n        AND m.lote_entrada = " + dtoCheques.getCriterioLote());
				}
				
				if(dtoCheques.getCriterioNoDocto() != null && !dtoCheques.getCriterioNoDocto().trim().equals("")){
				    sql.append("\n        AND m.no_docto = '" + Utilerias.validarCadenaSQL(dtoCheques.getCriterioNoDocto()) + "'");
				}
				
				if(dtoCheques.getCriterioSolicitudIni() != 0){
					
					if(dtoCheques.getCriterioSolicitudFin() != 0){
						sql.append("\n        AND m.no_folio_det >= " + dtoCheques.getCriterioSolicitudIni());
						sql.append("\n        and m.no_folio_det <= " + dtoCheques.getCriterioSolicitudFin());
					}else{
						sql.append("\n        AND m.no_folio_det = " + dtoCheques.getCriterioSolicitudIni());
					}
					        
				}
				
				if(dtoCheques.getCriterioFechaIni() != null && !dtoCheques.getCriterioFechaIni().equals("")){
					
					if(dtoCheques.getCriterioFechaFin() != null && !dtoCheques.getCriterioFechaFin().equals("")){
						
				        sql.append("\n        AND m.fec_operacion >= '" + Utilerias.validarCadenaSQL(funciones.ponerFechaSola(dtoCheques.getCriterioFechaIni())) +"'"+
				                                "       and m.fec_operacion <= '" + Utilerias.validarCadenaSQL(funciones.ponerFechaSola(dtoCheques.getCriterioFechaFin()))+"'");
					}else{
						sql.append("\n        AND m.fec_operacion = '" + funciones.ponerFechaSola(dtoCheques.getCriterioFechaIni()) +"'");
					}
				        
				}
				
				/* Se quita criterio tipo Egresos
				 * if(dtoCheques.getCriterioTipoEgreso() != null && !dtoCheques.getCriterioTipoEgreso().equals("")){
				    sql.append("\n ");
				}*/
				
				if(dtoCheques.getCriterioCentroCostos() != 0){
				    sql.append("\n        AND m.agrupa1 = " + dtoCheques.getCriterioCentroCostos());
				}
				
				if(dtoCheques.getCriterioImporteIni() != 0){
					
					if(dtoCheques.getCriterioImporteFin() != 0){
						
				        sql.append("\n        AND m.importe >= " + dtoCheques.getCriterioImporteIni() +
				                                "       and m.importe <= " + dtoCheques.getCriterioImporteFin());
					}else{
						sql.append("\n        AND m.importe = " + dtoCheques.getCriterioImporteIni());
					}
				}
				
				if(dtoCheques.getNoEmpresa() != 0){
					sql.append("\n        and m.id_banco in (select distinct ccbh.id_banco");
					sql.append("\n                           from cat_cta_banco ccbh");
					sql.append("\n                           where ccbh.no_empresa = " + dtoCheques.getNoEmpresa() + ")");
					sql.append("\n        and m.id_chequera in (select ccbh.id_chequera");
					sql.append("\n                              from cat_cta_banco ccbh");
					sql.append("\n                              where ccbh.no_empresa = " + dtoCheques.getNoEmpresa() + ")");
				}else if(dtoCheques.getNoEmpresa() != 0 && banco == true && chequera == true){
					sql.append("\n        AND m.no_empresa = " + dtoCheques.getNoEmpresa());
				}
				
				if(dtoCheques.getPsEquivalePersona() != null && !dtoCheques.getPsEquivalePersona().equals("")){
					sql.append("\n AND pp.equivale_persona = " + Utilerias.validarCadenaSQL(dtoCheques.getPsEquivalePersona()) + " ");
				}
				    
				sql.append("\n ORDER BY m.no_folio_det ");
				
				/*if(gsDBM.equals("SQL SERVER"))
					sql.append("\n ORDER BY m.no_empresa, no_contrarecibo ");
				if(gsDBM.equals("ORACLE") || gsDBM.equals("POSTGRESQL") || gsDBM.equals("SYBASE") || gsDBM.equals("DB2"))
					sql.append("\n ORDER BY m.no_folio_det ");
			*/
				
				System.out.println("consulta de cheques: "+sql.toString());
				
				listRetorno = jdbcTemplate.query(sql.toString(), new RowMapper<MovimientoDto>(){
					public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
						MovimientoDto movimiento = new MovimientoDto();
						movimiento.setDescCaja(rs.getString("desc_caja") == null ? "" : rs.getString("desc_caja"));
						movimiento.setNoEmpresa(rs.getInt("no_empresa"));
						movimiento.setNoSolicitud(rs.getInt("no_solicitud"));
						movimiento.setImporte(rs.getDouble("importe"));
						movimiento.setDivision(rs.getString("division"));
						movimiento.setNoCliente(rs.getString("no_cliente"));
						movimiento.setBeneficiario(rs.getString("beneficiario"));
						movimiento.setDescBanco(rs.getString("desc_banco"));
						movimiento.setCtaAlias(rs.getString("cta_alias"));
						movimiento.setConcepto(rs.getString("concepto"));
						movimiento.setCiaNmbr(rs.getString("cia_nmbr") == null ? "" : rs.getString("cia_nmbr"));
						movimiento.setCentroCosto(rs.getInt("centro_costo"));
						movimiento.setBcoCve(rs.getInt("bco_cve"));
						movimiento.setTipoEgreso(rs.getString("tipo_egreso"));
						movimiento.setImporteOriginal(rs.getDouble("importe_original"));
						movimiento.setNoContrarecibo(rs.getString("no_contrarecibo"));
						movimiento.setRfc(rs.getString("rfc") == null ? "" : rs.getString("rfc"));
						movimiento.setMoneda(rs.getString("moneda"));
						movimiento.setIdContable(rs.getString("id_contable") == null ? "" : rs.getString("id_contable"));
						movimiento.setCtaNo(rs.getString("cta_no"));
						movimiento.setLeyendaProte(rs.getString("leyenda_prote"));
						movimiento.setPlaza(rs.getInt("plaza"));
						movimiento.setSucursal(rs.getInt("sucursal"));
						movimiento.setBProtegido(rs.getString("b_protegido"));
						movimiento.setNoCuentaS(rs.getString("no_cuenta"));
						movimiento.setCodigoSeguridad(rs.getString("codigo_seguridad"));
						movimiento.setCvePlaza(rs.getString("cve_plaza"));
						movimiento.setIdBanco(rs.getInt("no_banco"));
						movimiento.setCiaDir(rs.getString("cia_dir") == null ? "" : rs.getString("cia_dir"));
						movimiento.setCiaDir2(rs.getString("cia_dir2") == null ? "" : rs.getString("cia_dir2"));
						movimiento.setCiaRfc(rs.getString("cia_rfc"));
						movimiento.setNoCheque(rs.getInt("no_cheque"));
						movimiento.setFirmante1(rs.getString("firmante1") == null ? "" : rs.getString("firmante1"));
						movimiento.setFirmante2(rs.getString("firmante2") == null ? "" : rs.getString("firmante2"));
						movimiento.setFecCheque(rs.getDate("fec_cheque"));
						movimiento.setFecChequeStr(funciones.ponerFechaSola(rs.getDate("fec_cheque"))); //Se obtiene para efectos de visualizacion, fecCehque de tipo Date ya estaba.
						movimiento.setEquivaleBenef(rs.getString("equivale_benef"));
						movimiento.setIdGrupo(rs.getInt("id_grupo"));
						movimiento.setIdRubroStr(rs.getString("id_rubro"));
						
						movimiento.setNoPedido(rs.getInt("no_pedido"));
						movimiento.setEquivaleEmpresa(rs.getInt("equivale_empresa"));
						movimiento.setIdDivisaOriginal(rs.getString("id_divisa_original"));
						movimiento.setIdCp(rs.getString("id_cp"));
						movimiento.setLogoBanco(rs.getString("logo_banco") == null ? "" : rs.getString("logo_banco"));
						movimiento.setLogoEmpresa(rs.getString("logo_empresa") == null ? "" : rs.getString("logo_empresa"));
						movimiento.setDirLogoAlterno(rs.getString("dir_logo_alterno") == null ? "" : rs.getString("dir_logo_alterno"));
						movimiento.setTipoCambio(rs.getDouble("tipo_cambio"));
						movimiento.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
						movimiento.setIdCaja(rs.getInt("id_caja"));
						movimiento.setTipoImpresion(rs.getString("tipo_impresion") == null ? "" : rs.getString("tipo_impresion"));
						movimiento.setPoHeaders(rs.getString("PO_HEADERS"));
						movimiento.setFecValor(rs.getDate("fec_valor"));
						movimiento.setInvoiceType(rs.getString("INVOICE_TYPE"));
						return movimiento;
					}
				});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ImpresionDaoImpl, M:consultarChequesPendientes");	
			e.printStackTrace();
		}
		return listRetorno;
	}

	
	public List<MovimientoDto>consultarChequesPendientes2(ConsultaChequesDto dtoCheques){
		
		List<MovimientoDto>listRetorno = new ArrayList<MovimientoDto>();
		StringBuffer sql = new StringBuffer();
		String bHabilita = seleccionarBHabilita();
		boolean banco = false;
		boolean chequera = false;
		
		try{
			

			sql.append("\n SELECT 'GENERAL' AS desc_caja,m.no_empresa, ");
			sql.append("\n        m.po_headers as no_solicitud, ");
			//sql.append("\n        m.importe,");
			sql.append("\n        sum(m.IMPORTE) as importe, ");
			sql.append("\n        m.no_cliente, m.beneficiario,");
			sql.append("\n        cb.desc_banco,");
			sql.append("\n        m.id_chequera  as cta_alias,");
			sql.append("\n        'GENERAL' desc_caja, ");
			sql.append("\n        'CHEQUE' AS  concepto, ");
			sql.append("\n		  e.nom_empresa as cia_nmbr, ");
			sql.append("\n        0 as centro_costo, ");
			sql.append("\n        m.id_banco as bco_cve, ");
			sql.append("\n        m.id_tipo_movto as tipo_egreso,");
			sql.append("\n        sum(m.IMPORTE_ORIGINAL) as importe_original, ");
			//sql.append("\n        m.importe_original as importe_original, ");
			
			sql.append("\n        po_headers as no_contrarecibo, ");
			sql.append("\n        pp.rfc,");
			sql.append("\n        m.id_divisa as moneda,");
			sql.append("\n        ' ' as id_contable,");
			sql.append("\n        m.id_chequera as cta_no,");
			sql.append("\n        ' ' as leyenda_prote, ");
			sql.append("\n        ccb.desc_plaza as plaza,");
			sql.append("\n        ccb.desc_sucursal as sucursal,");
			sql.append("\n        cb.b_protegido,");
			sql.append("\n        m.id_chequera as no_cuenta,");
			sql.append("\n        cb.codigo_seguridad,");
			sql.append("\n        cb.cve_plaza as cve_plaza,");
			sql.append("\n        cb.id_banco as no_banco,");
			sql.append("\n        '' as cia_dir, ");
			sql.append("\n        ''  as cia_dir2, ");
			sql.append("\n        '' as cia_rfc, ");
			sql.append("\n        m.no_cheque as no_cheque,");
			sql.append("\n 		  '' as firmante1, ");
			sql.append("\n        '' as firmante2, ");
			sql.append("\n        m.PO_HEADERS,");
			sql.append("\n        m.fec_propuesta as fec_valor, ");
			sql.append("\n        ' ' as INVOICE_TYPE,");
			sql.append("\n        m.fec_propuesta as fec_cheque, ");
			sql.append("\n        pp.equivale_persona as equivale_benef,");
			sql.append("\n        ' ' as id_grupo,");
			sql.append("\n        ' ' as id_rubro,");
			sql.append("\n        ' ' as no_pedido,");
			sql.append("\n        m.no_empresa as equivale_empresa,");
			sql.append("\n        m.id_divisa_original,");
			sql.append("\n        '' as id_cp, ");
			sql.append("\n        cb.dir_logo as logo_banco,");
			sql.append("\n        e.dir_logo as logo_empresa,");
			sql.append("\n        e.dir_logo_alterno,");
			sql.append("\n        m.tipo_cambio,");
			sql.append("\n        m.id_tipo_operacion,");
			sql.append("\n        m.id_caja,");
			sql.append("\n        case");
			sql.append("\n            when ccb.b_impre_continua = 'N' then 'LASER'");
			sql.append("\n            when ccb.b_impre_continua = 'S' then 'CONTINUA'");
			sql.append("\n        end As tipo_impresion,");
			sql.append("\n        m.c_periodo as periodo,");
			sql.append("\n        m.no_folio_mov");
			//EMS: Realizo la consulta con los joins implicitos, ya que hay *= que no funcionen en ORACLE.
			sql.append("\n FROM   movimiento m LEFT JOIN cat_banco cb ON (m.id_banco = cb.id_banco)");
			sql.append("\n        LEFT JOIN cat_caja cc ON (m.id_caja = cc.id_caja)");
			
			
				if(!dtoCheques.getPsEquivalePersona().equals("")){
					
					sql.append("\n        INNER JOIN persona pp ON (m.no_cliente = pp.no_persona ");
					sql.append("\n                                  AND pp.no_empresa = 552 ");
					sql.append("\n                                  AND pp.id_tipo_persona = 'P' ");
					sql.append("\n                                  AND pp.equivale_persona = '" + Utilerias.validarCadenaSQL(dtoCheques.getPsEquivalePersona()) + "')");
					
				}else{
										
					sql.append("\n        LEFT JOIN persona pp ON (m.no_cliente = pp.no_persona ");
				    sql.append("\n                                 AND pp.no_empresa = 552 ");
				    sql.append("\n                                 AND pp.id_tipo_persona = 'P')");

				}
				
				sql.append("\n        LEFT JOIN cat_cta_banco ccb  ON (m.id_banco = ccb.id_banco  AND m.id_chequera = ccb.id_chequera)");
				sql.append("\n        INNER JOIN empresa e ON (m.no_empresa = e.no_empresa)");
				
				if(!dtoCheques.getPsEquivalePersona().equals(""))
					sql.append("\n        AND pp.equivale_persona = '" + Utilerias.validarCadenaSQL(dtoCheques.getPsEquivalePersona()) + "'");
				
				sql.append("\n WHERE ");
				sql.append("\n        ((m.id_tipo_operacion=3200 and m.id_estatus_mov = 'I') ");
				sql.append("\n         or (m.id_tipo_operacion=3200 and m.id_estatus_mov = 'R') ");
				sql.append("\n         or (m.id_tipo_operacion=3000 and m.id_estatus_mov = 'I') ");
				sql.append("\n         or (m.id_tipo_operacion=3000 and m.id_estatus_mov = 'R')) ");
				
				//sql.append("\n        AND m.id_forma_pago in (1,7,10)");
				sql.append("\n        AND m.id_forma_pago = 1");
				//Posible cambio de condicion a fecha between fec_hoy y fec_mañana, dependiendo el critero del radio.
				if(dtoCheques.isOptSeleccion() == false){
					sql.append("\n        AND m.fec_cheque <= (select fec_manana from fechas)");
				}
				
				sql.append("\n        AND m.id_caja in (select id_caja ");
				sql.append("\n                          from caja_usuario ");
				sql.append("\n                          where no_usuario = " + dtoCheques.getIdUsuario() + ") ");
				sql.append("\n        AND m.no_empresa in (select no_empresa");
				sql.append("\n                             from usuario_empresa ");
				sql.append("\n                             where no_usuario = " + dtoCheques.getIdUsuario() + ") ");
				
				if(!dtoCheques.getPsbImpreContinua().equals("")){
					sql.append("\n        AND ccb.b_impre_continua = '" +Utilerias.validarCadenaSQL(dtoCheques.getPsbImpreContinua()) + "' ");
				}
					
				if(bHabilita.equals("S")){
					
					if(dtoCheques.isOptPendientes()){
						
						sql.append("\n        AND m.b_autoriza_impre is null");
						sql.append("\n        AND m.firmante1 is null");
						sql.append("\n        AND m.firmante2 is null");
					
					}else if(dtoCheques.isOptPorConfirmar()){
						
						sql.append("\n        AND m.b_autoriza_impre = 'N'");
						sql.append("\n        AND (m.firmante1 is null or m.firmante2 is null)");
					
					}else if(dtoCheques.isOptFirmados()){
						
						sql.append("\n        AND m.b_autoriza_impre = 'S'");
						sql.append("\n        AND m.firmante1 is not null");
						sql.append("\n        AND m.firmante2 is not null");
						
					}
				}
				
				if(dtoCheques.getCriterioBanco() != 0){
				    sql.append("\n        AND m.id_banco = " + dtoCheques.getCriterioBanco());
				    banco = true;
				}
				
				if(dtoCheques.getCriterioChequera() != null && !dtoCheques.getCriterioChequera().trim().equals("")){
					sql.append("\n        AND m.id_chequera = '" + Utilerias.validarCadenaSQL(dtoCheques.getCriterioChequera()) + "'");
					chequera = true;
				}
				if(dtoCheques.getCriterioCveControl() != null && !dtoCheques.getCriterioCveControl().trim().equals("")){
					sql.append("\n		  AND m.cve_control = '"+ Utilerias.validarCadenaSQL(dtoCheques.getCriterioCveControl()) +"'");
				}
				
				if(dtoCheques.getCriterioCajaPago() != 0){
				    sql.append("\n        AND m.id_caja = " + dtoCheques.getCriterioCajaPago());
				}
				
				if(dtoCheques.getCriterioLote() != 0){
				    sql.append("\n        AND m.lote_entrada = " + dtoCheques.getCriterioLote());
				}
				
				if(dtoCheques.getCriterioNoDocto() != null && !dtoCheques.getCriterioNoDocto().trim().equals("")){
				    sql.append("\n        AND m.no_docto = '" + Utilerias.validarCadenaSQL(dtoCheques.getCriterioNoDocto()) + "'");
				}
				
				if(dtoCheques.getCriterioSolicitudIni() != 0){
					
					if(dtoCheques.getCriterioSolicitudFin() != 0){
						sql.append("\n        AND m.no_folio_det >= " + dtoCheques.getCriterioSolicitudIni());
						sql.append("\n        and m.no_folio_det <= " + dtoCheques.getCriterioSolicitudFin());
					}else{
						sql.append("\n        AND m.no_folio_det = " + dtoCheques.getCriterioSolicitudIni());
					}
					        
				}
				
				if(dtoCheques.getCriterioFechaIni() != null && !dtoCheques.getCriterioFechaIni().equals("")){
					
					if(dtoCheques.getCriterioFechaFin() != null && !dtoCheques.getCriterioFechaFin().equals("")){
						
				        sql.append("\n        AND m.fec_operacion >= '" + Utilerias.validarCadenaSQL(funciones.ponerFechaSola(dtoCheques.getCriterioFechaIni())) +"'"+
				                                "       and m.fec_operacion <= '" + Utilerias.validarCadenaSQL(funciones.ponerFechaSola(dtoCheques.getCriterioFechaFin()))+"'");
					}else{
						sql.append("\n        AND m.fec_operacion = '" + funciones.ponerFechaSola(dtoCheques.getCriterioFechaIni()) +"'");
					}
				        
				}
				
				if(dtoCheques.getCriterioNoChequeIni()!=0){
					if(dtoCheques.getCriterioNoChequeFin()!=0){
						sql.append("\n		 AND m.no_cheque >= " + dtoCheques.getCriterioNoChequeIni() +
						                 "         AND m.no_cheque <= " + dtoCheques.getCriterioNoChequeFin());
					}else{
						sql.append("\n       AND m.no_cheque = " + dtoCheques.getCriterioNoChequeIni());
					}
				}
				
				
				if(dtoCheques.getCriterioImporteIni() != 0){
					
					if(dtoCheques.getCriterioImporteFin() != 0){
						
				        sql.append("\n        AND m.importe >= " + dtoCheques.getCriterioImporteIni() +
				                                "       and m.importe <= " + dtoCheques.getCriterioImporteFin());
					}else{
						sql.append("\n        AND m.importe = " + dtoCheques.getCriterioImporteIni());
					}
				}
				
				if(dtoCheques.getNoEmpresa() != 0){
					sql.append("\n        and m.id_banco in (select distinct ccbh.id_banco");
					sql.append("\n                           from cat_cta_banco ccbh");
					sql.append("\n                           where ccbh.no_empresa = " + dtoCheques.getNoEmpresa() + ")");
					sql.append("\n        and m.id_chequera in (select ccbh.id_chequera");
					sql.append("\n                              from cat_cta_banco ccbh");
					sql.append("\n                              where ccbh.no_empresa = " + dtoCheques.getNoEmpresa() + ")");
				}else if(dtoCheques.getNoEmpresa() != 0 && banco == true && chequera == true){
					sql.append("\n        AND m.no_empresa = " + dtoCheques.getNoEmpresa());
				}
				
				if(dtoCheques.getPsEquivalePersona() != null && !dtoCheques.getPsEquivalePersona().equals("")){
					sql.append("\n AND pp.equivale_persona = " + Utilerias.validarCadenaSQL(dtoCheques.getPsEquivalePersona()) + " ");
				}
				    
				sql.append("\n AND m.PO_HEADERS IS NOT NULL ");
				
				sql.append("\n GROUP BY m.no_empresa, m.po_headers, m.no_cliente,         m.beneficiario,        cb.desc_banco,        m.id_chequera, ");
				//sql.append("\n e.nom_empresa ,        m.id_banco ,        m.id_tipo_movto ,        m.importe_original , ");
				sql.append("\n e.nom_empresa ,        m.id_banco ,        m.id_tipo_movto , ");
				sql.append("\n pp.rfc,        m.id_divisa , ");
				sql.append("\n m.id_chequera ,        ccb.desc_plaza ,        ccb.desc_sucursal , ");
				sql.append("\n cb.b_protegido,        m.id_chequera ,        cb.codigo_seguridad,        cb.cve_plaza , ");
				sql.append("\n cb.id_banco ,        m.no_cheque , ");
				sql.append("\n m.PO_HEADERS,        m.fec_propuesta,        m.fec_propuesta , ");
				sql.append("\n pp.equivale_persona ,        m.no_empresa , ");
				sql.append("\n m.id_divisa_original,        cb.dir_logo ,        e.dir_logo , ");
				sql.append("\n e.dir_logo_alterno,        m.tipo_cambio,        m.id_tipo_operacion,        m.id_caja, ccb.b_impre_continua, ");
				sql.append("\n m.c_periodo 		,		m.no_folio_mov");
		        
				System.out.println("consulta de cheques: "+sql.toString());
				
				listRetorno = jdbcTemplate.query(sql.toString(), new RowMapper<MovimientoDto>(){
					public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
						MovimientoDto movimiento = new MovimientoDto();
						movimiento.setDescCaja(rs.getString("desc_caja") == null ? "" : rs.getString("desc_caja"));
						movimiento.setNoEmpresa(rs.getInt("no_empresa"));
						//movimiento.setNoSolicitud(rs.getInt("no_solicitud"));
						movimiento.setNoSolicitud(rs.getInt("no_solicitud"));
						movimiento.setImporte(rs.getDouble("importe"));
						//movimiento.setDivision(rs.getString("division"));
						movimiento.setNoCliente(rs.getString("no_cliente"));
						movimiento.setBeneficiario(rs.getString("beneficiario"));
						movimiento.setDescBanco(rs.getString("desc_banco"));
						movimiento.setCtaAlias(rs.getString("cta_alias"));
						movimiento.setConcepto(rs.getString("concepto"));
						movimiento.setCiaNmbr(rs.getString("cia_nmbr") == null ? "" : rs.getString("cia_nmbr"));
						movimiento.setCentroCosto(rs.getInt("centro_costo"));
						movimiento.setBcoCve(rs.getInt("bco_cve"));
						movimiento.setTipoEgreso(rs.getString("tipo_egreso"));
						movimiento.setImporteOriginal(rs.getDouble("importe_original"));
						movimiento.setNoContrarecibo(rs.getString("no_contrarecibo"));
						movimiento.setRfc(rs.getString("rfc") == null ? "" : rs.getString("rfc"));
						movimiento.setMoneda(rs.getString("moneda"));
						movimiento.setIdContable(rs.getString("id_contable") == null ? "" : rs.getString("id_contable"));
						movimiento.setCtaNo(rs.getString("cta_no"));
						movimiento.setLeyendaProte(rs.getString("leyenda_prote"));
						movimiento.setPlaza(rs.getInt("plaza"));
						movimiento.setSucursal(rs.getInt("sucursal"));
						movimiento.setBProtegido(rs.getString("b_protegido"));
						movimiento.setNoCuentaS(rs.getString("no_cuenta"));
						movimiento.setCodigoSeguridad(rs.getString("codigo_seguridad"));
						movimiento.setCvePlaza(rs.getString("cve_plaza"));
						movimiento.setIdBanco(rs.getInt("no_banco"));
						movimiento.setCiaDir(rs.getString("cia_dir") == null ? "" : rs.getString("cia_dir"));
						movimiento.setCiaDir2(rs.getString("cia_dir2") == null ? "" : rs.getString("cia_dir2"));
						movimiento.setCiaRfc(rs.getString("cia_rfc"));
						movimiento.setNoCheque(rs.getInt("no_cheque"));
						movimiento.setFirmante1(rs.getString("firmante1") == null ? "" : rs.getString("firmante1"));
						movimiento.setFirmante2(rs.getString("firmante2") == null ? "" : rs.getString("firmante2"));
						movimiento.setFecCheque(rs.getDate("fec_cheque"));
						movimiento.setFecChequeStr(funciones.ponerFechaSola(rs.getDate("fec_cheque"))); //Se obtiene para efectos de visualizacion, fecCehque de tipo Date ya estaba.
						movimiento.setEquivaleBenef(rs.getString("equivale_benef"));
						//movimiento.setIdGrupo(rs.getInt("id_grupo"));
						movimiento.setIdRubroStr(rs.getString("id_rubro"));
						
						//movimiento.setNoPedido(rs.getInt("no_pedido"));
						movimiento.setEquivaleEmpresa(rs.getInt("equivale_empresa"));
						movimiento.setIdDivisaOriginal(rs.getString("id_divisa_original"));
						movimiento.setIdCp(rs.getString("id_cp"));
						movimiento.setLogoBanco(rs.getString("logo_banco") == null ? "" : rs.getString("logo_banco"));
						movimiento.setLogoEmpresa(rs.getString("logo_empresa") == null ? "" : rs.getString("logo_empresa"));
						movimiento.setDirLogoAlterno(rs.getString("dir_logo_alterno") == null ? "" : rs.getString("dir_logo_alterno"));
						movimiento.setTipoCambio(rs.getDouble("tipo_cambio"));
						movimiento.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
						movimiento.setIdCaja(rs.getInt("id_caja"));
						movimiento.setTipoImpresion(rs.getString("tipo_impresion") == null ? "" : rs.getString("tipo_impresion"));
						movimiento.setPoHeaders(rs.getString("PO_HEADERS"));
						movimiento.setFecValor(rs.getDate("fec_valor"));
						movimiento.setInvoiceType(rs.getString("INVOICE_TYPE"));
						movimiento.setCPeriodo(rs.getInt("periodo"));
						movimiento.setNoFolioMov(rs.getInt("no_folio_mov"));
						return movimiento;
					}
				});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ImpresionDaoImpl, M:consultarChequesPendientes");	
			e.printStackTrace();
		}
		return listRetorno;
	}
	
	public String seleccionarBHabilita() {
		String sql = "";
		StringBuffer sb = new StringBuffer();
		List <ControlProyectoDto> result = new ArrayList<ControlProyectoDto>();
		try{
			sb.append( " SELECT b_habilita");
			sb.append( "   FROM control_proyecto");
			sb.append( "  WHERE id_funcionalidad = 4");
		    sql=sb.toString();
				result = jdbcTemplate.query(sql, new RowMapper<ControlProyectoDto>(){
				public ControlProyectoDto mapRow(ResultSet rs, int idx) throws SQLException {
					ControlProyectoDto control = new ControlProyectoDto();
					control.setBHabilita(rs.getString("b_habilita"));
				return control;
				}});
			}catch(Exception e){
				e.printStackTrace();
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Impresion, C:ImpresionDaoImpl, M:seleccionarBHabilita");
			} 
		return result.isEmpty()? "" : result.get(0).getBHabilita();
	}
	
	/**
	 * consulta de empresas
	 * @param usuario
	 * @return List<LlenaComboGralDto>
	 */

	//Modificado EMS: 15/12/2015
	public List<LlenaComboEmpresasDto>llenarComboEmpresa(int usuario)
	{
		StringBuffer sql = new StringBuffer();
		List<LlenaComboEmpresasDto> listEmpresa = new ArrayList<LlenaComboEmpresasDto>();
		try
		{
			sql.append( " SELECT no_empresa as ID, nom_empresa as describ ");
			sql.append( "\n   FROM empresa ");
			sql.append( "\n  WHERE no_empresa > 1");
			sql.append( "\n  AND no_empresa in (SELECT no_empresa");
			sql.append( "\n   FROM usuario_empresa ");
			sql.append( "\n   WHERE no_usuario = "+ usuario + ")");
			sql.append( "\n   ORDER BY  nom_empresa");
			listEmpresa= jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboEmpresasDto>(){
			public LlenaComboEmpresasDto mapRow(ResultSet rs, int idx) throws SQLException {
				LlenaComboEmpresasDto cons = new LlenaComboEmpresasDto();
				cons.setNoEmpresa(rs.getInt("ID"));
				cons.setNomEmpresa(rs.getString("describ"));
				return cons;
			}});
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ImpresionDaoImpl, M:llenarComboEmpresa");
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:llenarComboEmpresa");
		}
		return listEmpresa;
	    
	}
	
	/**
	 * consulta impresoras disponibles
	 * @param caja
	 * @return
	 */
	//Modificado EMS 15/12/2015
	public List<LlenaComboImpresora>llenarComboImpresora(int caja)
	{
		
		StringBuffer sql = new StringBuffer();
		List<LlenaComboImpresora> listImpresora = new ArrayList<LlenaComboImpresora>();
		try
		{
			
			sql.append( "SELECT convert(numeric,NO_IMPRESORA) as NO_IMPRESORA,");
			sql.append( "       convert(numeric,CJA_CVE) as CJA_CVE,");
			sql.append( "       convert(numeric,NUM_CHAROLAS) as NUM_CHAROLA ");
            
			/*if(gsDBM.equals("ORACLE"))
			{
				sb.append( "SELECT to_number(NO_IMPRESORA) as NO_IMPRESORA,");
	            sb.append( "       to_number(CJA_CVE) as CJA_CVE,");
	            sb.append( "       to_number(NUM_CHAROLAS) as NUM_CHAROLA ");
			}
			if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE"))
			{
	            sb.append( "SELECT convert(int,NO_IMPRESORA) as NO_IMPRESORA, ");
	            sb.append( "       convert(int,CJA_CVE) as CJA_CVE,");
	            sb.append( "       convert(int,NUM_CHAROLAS) as NUM_CHAROLA ");
			} 
            if(gsDBM.equals("POSTGRESQL"))
            {
	            sb.append( "SELECT no_impresora, cja_cve,");
	            sb.append( "       num_charolas as num_charola ");
            }      
            */
            
			sql.append( "  FROM cat_impresora ");
			sql.append( " WHERE cja_cve = " + caja);
		    		
			System.out.println("select de caja :" + sql.toString());
			
			listImpresora= jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboImpresora>(){
			public LlenaComboImpresora mapRow(ResultSet rs, int idx) throws SQLException {
				LlenaComboImpresora cons = new LlenaComboImpresora();
				cons.setNoImpresora(rs.getInt("NO_IMPRESORA"));
				cons.setCjaCve(rs.getInt("CJA_CVE"));
				cons.setNumCharola(rs.getInt("NUM_CHAROLA"));
				cons.setDescripcion("IMPRESORA " + rs.getInt("NO_IMPRESORA"));
				return cons;
			}});
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ImpresionDaoImpl, M:llenarComboImpresora");
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:llenarComboImpresora");
		}
		return listImpresora;
	    
	}
	
	//Sustituye metodo por todos los bancos que tengan chequera. EMS - 29/01/2016
	public List<LlenaComboGralDto> llenarComboBancos(int noEmpresa){
		
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try
		{

			/*sb.append( "\n SELECT DISTINCT ca.id_banco as ID, ca.desc_banco as describ ");
			sb.append( "\n FROM cat_banco ca JOIN CTAS_BANCO cb ON ca.id_banco = cb.id_banco");
			sb.append( "\n ORDER by desc_banco ");
				*/
			sb.append( "\n select distinct ccb.id_banco as ID, cb.desc_banco as descrip ");
			sb.append( "\n FROM CAT_CTA_BANCO ccb JOIN CAT_BANCO cb ON ccb.id_banco = CB.ID_BANCO ");
			sb.append( "\n where tipo_chequera IN ('P','M') ");
			
			if(noEmpresa != 0) sb.append( "\n AND ccb.no_empresa = "+noEmpresa+" ");
			
			sb.append( "\n ORDER by cb.desc_banco ");
			
			list= jdbcTemplate.query(sb.toString(), new RowMapper<LlenaComboGralDto>(){
			public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
				LlenaComboGralDto cons = new LlenaComboGralDto();
				cons.setId(rs.getInt("ID"));
				cons.setDescripcion(rs.getString("descrip"));
				return cons;
			}});
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:llenarComboBancos");
		}
		return list;
	    
	}
	
	public List<LlenaComboGralDto> llenarComboBancosCC(int idBanco){
		
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try{

			sb.append( "\n SELECT DISTINCT ID_BANCO AS ID, DESC_BANCO AS DESCRIP ");
			sb.append( "\n FROM CAT_BANCO ");
			sb.append( "\n WHERE ID_BANCO = "+idBanco+" ");
			
			sb.append( "\n ORDER by desc_banco ");
			
			list= jdbcTemplate.query(sb.toString(), new RowMapper<LlenaComboGralDto>(){
			public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
				LlenaComboGralDto cons = new LlenaComboGralDto();
				cons.setId(rs.getInt("ID"));
				cons.setDescripcion(rs.getString("descrip"));
				return cons;
			}});
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Reimpresion, C:ReimpresionDaoImpl, M:llenarComboBancosCC");
		}
		return list;
	    
	}

	public List<LlenaComboGralDto>llenarComboChequera(int idBanco, int noEmpresa) {
		StringBuffer sql = new StringBuffer();
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		
		try {
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			
			sql.append(" SELECT id_chequera as ID, id_chequera as descrip ");
			sql.append("\n FROM cat_cta_banco ");
			sql.append("\n WHERE id_banco = " + idBanco +" "); 
			sql.append("\n 	AND tipo_chequera IN ('P','M') ");
			
			if(noEmpresa != 0)	sql.append("\n 	And no_empresa = "+ noEmpresa +"");
			
			sql.append("\n 	ORDER BY descrip ");
			//system.out.println("query cheque pago: " + sql);
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>() {
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto con = new LlenaComboGralDto();
					con.setDescripcion(rs.getString("descrip"));
					return con;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C: ImpresionDaoImpl, M: llenarComboChequera");
		}return list;
	}
	
	
	public List<LlenaComboGralDto>llenarComboGral(LlenaComboGralDto dto){
		String sql="";
		List<LlenaComboGralDto> listDatos= new ArrayList<LlenaComboGralDto>();
		try{
			sql+= "	SELECT	";
			if(dto.getCampoUno()!=null && !dto.getCampoUno().trim().equals(""))
				sql+=""+Utilerias.validarCadenaSQL(dto.getCampoUno())+"	as ID";
			//aplica solo para s que tengan el mismo campo como id y descripcion (String)
			//ejemplo: chequeras
			else
				sql+=""+2+"	as ID"+",	";	
			if(dto.getCampoDos()!=null && !dto.getCampoDos().trim().equals(""));
				sql+=Utilerias.validarCadenaSQL(dto.getCampoDos())+"	as DESCRIPCION";
		    sql+= "  FROM	";
		    if(dto.getTabla()!=null && !dto.getTabla().trim().equals(""))
		    	sql+=""+Utilerias.validarCadenaSQL(dto.getTabla());
		    if(dto.getCondicion()!=null && !dto.getCondicion().trim().equals(""))
		    	sql+="	WHERE	"+Utilerias.validarCadenaSQL(dto.getCondicion());
		    if(dto.getOrden()!=null && !dto.getOrden().trim().equals(""))
		    	sql+="	ORDER BY	"+Utilerias.validarCadenaSQL(dto.getOrden());
		 
		    listDatos= jdbcTemplate.query(sql, new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}});
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:llenarComboGral");
		}
		return listDatos;
	}
	

	/**
	 * Se Validan los usuarios sean los mismos que los que se encuentran para realizar
	 * la firma de autorizacion de cheques
	 */

	public List<CatFirmasDto>consultarFirmasChequera(String cuenta, int banco){
		String sql="";
		StringBuffer sb = new StringBuffer();
		List<CatFirmasDto> list= new ArrayList<CatFirmasDto>();
		try
		{
			sb.append( " SELECT nombre, cta_no, tipo_firma, b_deter, a.no_persona ");
			sb.append( "\n  FROM cat_firma a, cat_persona b ");
			sb.append( "\n  WHERE a.no_persona = b.no_persona");
			sb.append( "\n  AND bco_cve = " + banco);
			sb.append( "\n  AND cta_no = '" + Utilerias.validarCadenaSQL(cuenta) + "'");
			sql = sb.toString();
			//logger.info(sql);
			list= jdbcTemplate.query(sql, new RowMapper<CatFirmasDto>(){
			public CatFirmasDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatFirmasDto cons = new CatFirmasDto();
				cons.setNombre(rs.getString("nombre"));
				cons.setCtaNo(rs.getString("cta_no"));
				cons.setTipoFirma(rs.getString("tipo_firma"));
				cons.setBDeter(rs.getInt("b_deter"));
				cons.setNoPersona(rs.getInt("no_persona"));
				return cons;
			}});
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:consultarFirmasChequera");
		}
		return list;
	    
	}
	
	//Modificado EMS: 15/12/2015
	public List<LlenaComboGralDto>consultarProveedores(String texto)
	{
		StringBuffer sql = new StringBuffer();
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		try
		{
			
			sql.append( "SELECT p.no_persona as ID, ");
			sql.append( "       case when p.pers_juridica = 'F' then rtrim(ltrim(p.nombre + ' ' + p.paterno  + ' ' + p.materno)) ");
			sql.append( "       else rtrim(ltrim(p.razon_social)) end as Descrip ");
			sql.append( "  FROM persona p");
			sql.append( " WHERE p.id_tipo_persona = 'P' ");
			sql.append( "   AND (p.razon_social like '" + Utilerias.validarCadenaSQL(texto) + "%'");
			sql.append( "    OR p.nombre + ' ' + p.paterno  + ' ' + p.materno like '" + Utilerias.validarCadenaSQL(texto) + "%')");
			
			/*if(ConstantesSet.gsDBM.equals("ORACLE") || ConstantesSet.gsDBM.equals("POSTGRESQL"))
			{
				sql.append( "SELECT p.no_persona as ID, ");
				sql.append( "       case when p.pers_juridica = 'F' then rtrim(ltrim(p.nombre || ' ' || p.paterno  || ' ' || p.materno)) ");
				sql.append( "       else rtrim(ltrim(p.razon_social)) end as Descrip ");
				sql.append( "  FROM persona p");
				sql.append( " WHERE p.id_tipo_persona = 'P' ");
				sql.append( "   AND (p.razon_social like '" + texto + "%'");
				sql.append( "    OR p.nombre || ' ' || p.paterno  || ' ' || p.materno like '" + texto + "%')");
			}
			
			if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
			{
				sql.append( "SELECT p.no_persona as ID, ");
				sql.append( "       case when p.pers_juridica = 'F' then rtrim(ltrim(p.nombre + ' ' + p.paterno  + ' ' + p.materno)) ");
				sql.append( "       else rtrim(ltrim(p.razon_social)) end as Descrip ");
				sql.append( "  FROM persona p");
				sql.append( " WHERE p.id_tipo_persona = 'P' ");
				sql.append( "   AND (p.razon_social like '" + texto + "%'");
				sql.append( "    OR p.nombre + ' ' + p.paterno  + ' ' + p.materno like '" + texto + "%')");
			}*/
			
			System.out.println("consultarProveedores: " + sql);
			
			list= jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("Descrip"));
					return cons;
			}});
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ImpresionDaoImpl, M:consultarProveedores");
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:consultarProveedores");
			System.err.println(e);
		}
		return list;
	    
	}
	/**
	 * FunSQLSelect560
	 * @param noImpresora
	 * @param caja
	 * @return
	 */
	
	public List<LlenaComboImpresora>consultarCharolasImpresora(int noImpresora, int caja){
		List<LlenaComboImpresora>list = new ArrayList<LlenaComboImpresora>();
		StringBuffer sql = new StringBuffer();
		try{
			
            sql.append("\n SELECT convert(numeric,num_charolas) as num_charola, tipo_impresora  ");
                     
        	/*if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
	            sql.append("\n SELECT convert(int,num_charolas) as num_charola, tipo_impresora  ");
	            
            if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
	            sql.append("\n SELECT num_charolas as num_charola, tipo_impresora  ");
	        */
            
		    sql.append("\n FROM cat_impresora ");
		    sql.append("\n WHERE no_impresora = " + noImpresora);
		    sql.append("\n    AND cja_cve = " + caja);
		    
		    System.out.println("charrolas impresora: " + sql.toString());
		    
		    //logger.info(sql.toString());
		    list= jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboImpresora>(){
				public LlenaComboImpresora mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboImpresora cons = new LlenaComboImpresora();
					cons.setNumCharola(rs.getInt("num_charola"));
					cons.setTipoImpresora(rs.getString("tipo_impresora"));
					return cons;
				}});
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:consultarCharolasImpresora");
		}
		return list;
	}
	
	/**
	 * FunSQLSelect152
	 * @param banco
	 * @param chequera
	 * @return
	 */
	public List<CatCtaBancoDto>consultarImpresionContinua(int banco, String chequera){
		List<CatCtaBancoDto> list = new ArrayList<CatCtaBancoDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("\n SELECT b_impre_continua");
		    sql.append("\n   FROM cat_cta_banco ");
		    sql.append("\n  WHERE id_banco = " + banco);
		    sql.append("\n    AND id_chequera = '" + Utilerias.validarCadenaSQL(chequera) + "'");
		    //logger.info(sql.toString());
		    list= jdbcTemplate.query(sql.toString(), new RowMapper<CatCtaBancoDto>(){
			public CatCtaBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatCtaBancoDto cons = new CatCtaBancoDto();
				cons.setBImpreContinua(rs.getString("b_impre_continua"));
				return cons;
			}});
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:consultarImpresionContinua");
		}
		return list;
	}
	
	/**
	 * FunSQLSelect145
	 * @param banco
	 * @param moneda
	 * @param empresa
	 * @return
	 */
	//Modificado EMS: 29/01/16 - agregado parametro idConfiguracion.
	public List<ConfiguracionChequeDto>consultarConfiguracionCheques(int banco, String moneda, int empresa, String idConfiguracion){
		List<ConfiguracionChequeDto> list = new ArrayList<ConfiguracionChequeDto>();
		StringBuffer sql = new StringBuffer();
		try{
	        sql.append("\n SELECT c.campo, c.tipo_letra, c.tamano_letra, ");
		    sql.append("\n        c.coord_x, c.coord_y, c.formato, a.driver ");
		    sql.append("\n   FROM cat_cheque a, conf_cheque c ");
		    /*sql.append("\n  WHERE a.id_banco = " + banco);
		    sql.append("\n    AND a.id_divisa = '" + Utilerias.validarCadenaSQL(moneda) + "'");
		    sql.append("\n    AND a.no_empresa = " + empresa);*/
		    
		    sql.append("\n    WHERE a.id_conf = c.id_conf");
		    sql.append("\n    AND a.id_conf = "+idConfiguracion+" ");
		    
		    //logger.info(sql.toString());
		    list= jdbcTemplate.query(sql.toString(), new RowMapper<ConfiguracionChequeDto>(){
				public ConfiguracionChequeDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfiguracionChequeDto cons = new ConfiguracionChequeDto();
					cons.setCampo(rs.getString("campo"));
					cons.setTipoLetra(rs.getString("tipo_letra"));
					cons.setTamanoLetra(rs.getInt("tamano_letra"));
					cons.setCoordX(rs.getDouble("coord_x"));
					cons.setCoordY(rs.getDouble("coord_y"));
					cons.setFormato(rs.getString("formato"));
					cons.setDriver(rs.getString("driver"));
					return cons;
				}});
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:consultarConfiguracionCheques");
		}
		return list;
	}
	
	public String obtieneDescripcionCatCheque(String idConfiguracion){
		
		List<String> list = new ArrayList<>();
		StringBuffer sql = new StringBuffer();
		
		try{
	        sql.append("\n SELECT DESC_CONF ");
		    sql.append("\n FROM CAT_CHEQUE ");
		    sql.append("\n WHERE ID_CONF = "+idConfiguracion+" ");
		    
		    //logger.info(sql.toString());
		    list= jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
				public String mapRow(ResultSet rs, int idx) throws SQLException {
					String desc = rs.getString("DESC_CONF");
					return desc;
				}});
		    
		    if(list.size() > 0){
		    	return list.get(0);
		    }
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:consultarConfiguracionCheques");
		}
		return "";
	}
	
	public List<LlenaComboImpresora>consultarBancoDeCharola(int impresora, int idBanco){
		List<LlenaComboImpresora> list = new ArrayList<LlenaComboImpresora>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append(" \n SELECT ch.no_charola, ch.bco_cve, cb.desc_banco");
			sql.append(" \n   FROM charola ch, cat_banco cb");
			sql.append(" \n  WHERE ch.no_impresora = " + impresora);
			sql.append(" \n    and ch.bco_cve = cb.id_banco");
			sql.append(" \n    and ch.bco_cve = "+idBanco+" ");
			
			System.out.println("Query para llenar charolas: \n " + sql.toString());
			
		    list= jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboImpresora>(){
			public LlenaComboImpresora mapRow(ResultSet rs, int idx) throws SQLException {
				LlenaComboImpresora cons = new LlenaComboImpresora();
				cons.setNumCharola(rs.getInt("no_charola"));
				cons.setNoBanco(rs.getInt("bco_cve"));
				cons.setDescBanco(rs.getString("desc_banco"));
				return cons;
			}});
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:consultarBancoDeCharola");
		}
		return list;
	}
	
	/**
	 * FunSQLSelect161
	 * @param banco
	 * @param caja
	 * @return
	 */
	public List<ControlPapelDto> consultarFoliosPapel(int banco, int caja){
		List<ControlPapelDto> list = new ArrayList<ControlPapelDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("\n SELECT folio_inv_ini, ");
		    sql.append("\n        folio_inv_fin, ");
		    sql.append("\n        folio_ult_impreso ");
		    sql.append("\n   FROM control_papel ");
		    sql.append("\n  WHERE bco_cve = " + banco);
		    sql.append("\n    AND cja_cve = " + caja);
//		    'sql.append("\n    AND (folio_ult_impreso between folio_inv_ini and folio_inv_fin ");
//		    'sql.append("\n     OR  folio_ult_impreso = folio_inv_ini - 1)");
		    
		    //Agregado EMS 12/01/2016 - Para obtener folios de tipo papel y no chequeras.
		    sql.append("\n    AND tipo_folio = 'P' ");
		    sql.append("\n    AND estatus = 'A' ");
		    sql.append("\n    ORDER BY fec_alta DESC");
		    
		    list= jdbcTemplate.query(sql.toString(), new RowMapper<ControlPapelDto>(){
				public ControlPapelDto mapRow(ResultSet rs, int idx) throws SQLException {
					ControlPapelDto cons = new ControlPapelDto();
					cons.setFolioInvIni(rs.getInt("folio_inv_ini"));
					cons.setFolioInvFin(rs.getInt("folio_inv_fin"));
					cons.setFolioUltImpreso(rs.getInt("folio_ult_impreso"));
					return cons;
				}});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:consultarFoliosPapel");
		}
		return list;
	}
	
	//Agregado EMS 30/12/2015 - Se agregó la chequera, por el momento no se busca por chequera cuando es folio_papel. 
	//si retorna más de un dato, en business se tomará el último que se registro.
	public List<ControlPapelDto> consultarFoliosPapel(int banco, int caja, String idChequera){
		List<ControlPapelDto> list = new ArrayList<ControlPapelDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("\n SELECT folio_inv_ini, ");
		    sql.append("\n        folio_inv_fin, ");
		    sql.append("\n        folio_ult_impreso ");
		    sql.append("\n   FROM control_papel ");
		    sql.append("\n  WHERE bco_cve = " + banco);
		    sql.append("\n    AND cja_cve = " + caja);
		    //sql.append("\n    AND id_chequera = '"+idChequera+"' ");
		    
		    sql.append("\n    AND tipo_folio = 'P' ");
		    sql.append("\n    AND estatus = 'A' ");
		    
		    //sql.append("\n     ORDER BY folio_inv_fin ");
		    sql.append("\n     ORDER BY fec_alta DESC ");
		    
		    //logger.info("folios" + sql.toString());
		    list= jdbcTemplate.query(sql.toString(), new RowMapper<ControlPapelDto>(){
				public ControlPapelDto mapRow(ResultSet rs, int idx) throws SQLException {
					ControlPapelDto cons = new ControlPapelDto();
					cons.setFolioInvIni(rs.getInt("folio_inv_ini"));
					cons.setFolioInvFin(rs.getInt("folio_inv_fin"));
					cons.setFolioUltImpreso(rs.getInt("folio_ult_impreso"));
					return cons;
				}});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:consultarFoliosPapel");
		}
		return list;
	}
	
	/**
	 * FunSQLUpdate48
	 * @param banco
	 * @param impresora
	 * @return
	 */
	public int actualizarCharolas(int banco, int impresora){
		int actualizado = -1;
		StringBuffer sql = new StringBuffer();
		try{
			sql.append( " \n UPDATE charola set bco_cve = " + banco);
			sql.append( " \n WHERE no_charola = 1");
			sql.append( " \n AND no_impresora = " + impresora);
			//logger.info(sql.toString());
			actualizado = jdbcTemplate.update(sql.toString());
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:actualizarCharolas");
		}
		return actualizado;
	}
	
	/**
	 * FunSQLSelect562
	 * @param banco
	 * @param cuenta
	 * @return
	 */
	
	public List<CatFirmasDto> consultarContarFirmas(int banco, String cuenta){
		List<CatFirmasDto> list = new ArrayList<CatFirmasDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append(" \n SELECT count(*) as firmas ");
		    sql.append(" \n   FROM cat_firma ");
		    sql.append(" \n  WHERE bco_cve = " + banco);
		    sql.append(" \n    AND cta_no = '" + Utilerias.validarCadenaSQL(cuenta) + "'");
		    sql.append(" \n    AND b_deter = '1'");
		    
			list= jdbcTemplate.query(sql.toString(), new RowMapper<CatFirmasDto>(){
			public CatFirmasDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatFirmasDto cons = new CatFirmasDto();
				cons.setNumFirmas(rs.getInt("firmas"));
				return cons;
			}});
		    
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:consultarContarFirmas");
		}
		return list;
	}
	
	/**
	 * 
	 * @param fechaHoy
	 * @param usuario
	 * @param fechaChequeCapturada
	 * @param fechaCheque
	 * @param solicitud
	 * @param noCheque
	 * @return
	 */
	//Modificado EMS: 29/12/2015
	//@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	@Override
	public int actualizarMovimiento(Date fechaHoy, int usuario, Date fechaChequeCapturada, Date fechaCheque,
									int noCheque, String motivoReimpresion,
									int noFolioMov){
		
		int updateMov = -1;
		
		StringBuffer sql = new StringBuffer();
		
		try{

			sql.append("\n UPDATE movimiento ");
			sql.append("\n    SET id_estatus_mov = 'R' ");
			sql.append("\n        ,fec_imprime = convert(date,'" + Utilerias.validarCadenaSQL(funciones.ponerFechaSola(fechaHoy)) + "',103)");
			sql.append("\n        ,FEC_REIMPRIME = convert(date,'" + Utilerias.validarCadenaSQL(funciones.ponerFechaSola(fechaHoy)) + "',103)");
			sql.append("\n        ,usuario_imprime = " + usuario);
			sql.append("\n        ,observacion = '"+motivoReimpresion+"' ");
			
			
			if(noCheque != 0)
				sql.append("\n  ,no_cheque = " + noCheque);
			
			if(!fechaChequeCapturada.equals(""))
				sql.append("\n  ,fec_cheque = convert(date,'" + Utilerias.validarCadenaSQL(funciones.ponerFechaSola(fechaChequeCapturada)) + "',103)");
			else
				sql.append("\n  ,fec_cheque = convert(date,'" + Utilerias.validarCadenaSQL(funciones.ponerFechaSola(fechaCheque)) + "',103)");
			
			//Agregado 05/05/2016
			sql.append("\n        ,FEC_ENTREGADO = NULL ");
			sql.append("\n        ,B_ENTREGADO = 'N' ");
			sql.append("\n  WHERE no_folio_mov = '"+noFolioMov+"' ");
			//sql.append("\n  WHERE no_folio_det = " + solicitud);
			/*sql.append("\n  WHERE PO_HEADERS = '" +Utilerias.validarCadenaSQL(solicitud)+"' ");
			sql.append("\n  AND NO_EMPRESA = "+noEmpresa+" ");
			sql.append("\n  AND FEC_PROPUESTA = convert(datetime,'"+Utilerias.validarCadenaSQL(fechaPropuesta)+"',103) ");
			sql.append("\n  AND C_PERIODO = "+periodo+" ");
			sql.append("\n  AND NO_CLIENTE = "+noCliente+" ");
			*/
			sql.append("\n  AND ID_TIPO_OPERACION IN(3000,3200) ");
			sql.append("\n  AND ID_ESTATUS_MOV IN('I','R','A') ");
			System.out.println(sql.toString());
			
			updateMov = jdbcTemplate.update(sql.toString());
			
			if(sql.length()>0)
				sql.delete(0, sql.length());
			
			sql.append("\n UPDATE movimiento ");
			sql.append("\n    SET ");
			sql.append("\n        fec_imprime = convert(date,'" + Utilerias.validarCadenaSQL(funciones.ponerFechaSola(fechaHoy)) + "',103)");
			sql.append("\n        ,usuario_imprime = " + usuario);
			
			if(noCheque != 0)
				sql.append("\n  ,no_cheque = " + noCheque);
			
			if(!fechaChequeCapturada.equals(""))
				sql.append("\n  ,fec_cheque = convert(date,'" + Utilerias.validarCadenaSQL(funciones.ponerFechaSola(fechaChequeCapturada)) + "',103)");
			else
				sql.append("\n  ,fec_cheque = convert(date,'" + Utilerias.validarCadenaSQL(funciones.ponerFechaSola(fechaCheque)) + "',103)");
			
			sql.append("\n  WHERE no_folio_mov = " + noFolioMov);
			//sql.append("\n  WHERE grupo_pago = " + solicitud);
			sql.append("\n  and id_tipo_operacion in (3001,3201) and id_estatus_mov = 'H'");

			updateMov += jdbcTemplate.update(sql.toString());
			
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ImpresionDaoImpl, M:actualizarMovimiento");
		}
		return updateMov;
	}
	
	/**
	 * FunSQLInserta_bitacora_cheques
	 * @param dto
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int insertarBitacoraCheques(InsertaBitacoraChequesDto dto){
		int insertBitacora = -1;
		StringBuffer sql = new StringBuffer();
		funciones = new Funciones();
		try
		{
			/*Public Function FunSQLInserta_bitacora_cheques(ByVal empresa As Integer, ByVal folio_det As Long, _
	                ByVal Cheque_Ant As Long, ByVal banco As Integer, ByVal chequera As String, _
	                ByVal cheque As Long, ByVal importe As Double, ByVal estatus As String, _
	                ByVal divisa As String, ByVal fecha As Date, ByVal usuario As Integer, _
	                ByVal folio As Long, ByVal causa As Integer, ByVal caja As Integer, _
	                ByVal beneficiario As String, ByVal concepto As String, ByVal docto As String, _
	                ByVal fec_cheque As String) As Boolean*/
	    
	    sql.append("\n INSERT INTO bitacora_cheques ");
	    sql.append("\n             (no_empresa, no_folio_det, id_banco, id_chequera, no_cheque, importe, id_estatus, ");
	    sql.append("\n             id_divisa, fec_impresion, usuario, folio, causa, no_cheque_ant, fecha_bitacora, ");
	    sql.append("\n             id_caja, beneficiario, concepto, no_docto, fec_cheque) ");
	    sql.append("\n VALUES ");
	    sql.append("\n             ( " + dto.getNoEmpresa() + ", " + dto.getFolioDet() + ", " + dto.getNoBanco() + ", '" + Utilerias.validarCadenaSQL(dto.getChequera()) + "', " + dto.getNoCheque());
	    sql.append("\n             , " + dto.getImporte() + ", '" + Utilerias.validarCadenaSQL(dto.getEstatus())+ "', '" + Utilerias.validarCadenaSQL(dto.getDivisa()) + "', '" + Utilerias.validarCadenaSQL(funciones.ponerFechaSola(dto.getFechaHoy())) + "'");
	    sql.append("\n             , " + dto.getUsuario() + ", " + dto.getFolio() + ", " + dto.getCausa() + ", " + dto.getNoChequeAnt() + ", ");
	    
	    if(ConstantesSet.gsDBM.equals("SQL SERVER"))
	      sql.append("\n      getdate(), ");
	    
	    if(ConstantesSet.gsDBM.equals("DB2"))
	      sql.append("\n      current date, ");
	    
	    sql.append("\n              " + dto.getCaja());
	    sql.append("\n             , '" + Utilerias.validarCadenaSQL(dto.getBeneficiario()) 
	    							+ "', '" + Utilerias.validarCadenaSQL(dto.getConcepto()) 
	    							+ "', '" + Utilerias.validarCadenaSQL(dto.getNoDocto()) 
	    							+ "', '" + Utilerias.validarCadenaSQL(funciones.ponerFechaSola(dto.getFecCheque())) + "') ");
	    
	    logger.info("insertarBitacoraCheques :");
	    insertBitacora = jdbcTemplate.update(sql.toString());

		}catch(Exception e)
		{
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ImpresionDaoImpl, M:insertarBitacoraCheques");
		}
		return insertBitacora;
	}
	
	/**
	 * FunSQLUpdateCaja
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int actualizarCaja(int caja, int noFolioMov){
		int updateCaja = -1;
		StringBuffer sql = new StringBuffer();
		
		try{
			sql.append("\n UPDATE movimiento " );
		    sql.append("\n    SET id_caja = " + caja);
		    sql.append("\n  WHERE no_folio_mov="+noFolioMov);
		    /*sql.append("\n  WHERE PO_HEADERS = '" +Utilerias.validarCadenaSQL(solicitud)+"' ");
			sql.append("\n  AND NO_EMPRESA = "+noEmpresa+" ");
			sql.append("\n  AND FEC_PROPUESTA = convert(datetime,'"+Utilerias.validarCadenaSQL(fechaPropuesta)+"',103) ");
			sql.append("\n  AND C_PERIODO = "+periodo+" ");
			sql.append("\n  AND NO_CLIENTE = "+noCliente+" ");*/
		    
		    updateCaja = jdbcTemplate.update(sql.toString());
			    
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Reimpresion, C:ReimpresionDaoImpl, M:actualizarCaja");
		}
		return updateCaja;
	}
	
	/**
	 * FunSQLUpdate180
	 * @param banco
	 * @param caja
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int actualizaControlPapel(int banco, int caja){
		int updateCP = -1;
		StringBuffer sql = new StringBuffer();
		try
		{
			sql.append("\n UPDATE control_papel ");
		    sql.append("\n    SET folio_ult_impreso = folio_ult_impreso + 1");
		    sql.append("\n  WHERE bco_cve = " + banco);
		    sql.append("\n    AND cja_cve = " + caja); 
		    		   
		    //Agregar chequera o hacer busqueda por nuevo id_control_cheque
		    
		    updateCP = jdbcTemplate.update(sql.toString());
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ImpresionDaoImpl, M:actualizaControlPapel");
		}
		return updateCP;
	}
	
	//Agregado EMS: 30/01/2016
	public int actualizaControlPapel(int idControlCheque){
		int updateCP = -1;
		StringBuffer sql = new StringBuffer();
		try
		{
			sql.append("\n UPDATE control_papel ");
		    sql.append("\n SET folio_ult_impreso = folio_ult_impreso + 1");
		    sql.append("\n WHERE id_control_cheque = " + idControlCheque); 
		    		   
		    updateCP = jdbcTemplate.update(sql.toString());
		    
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ImpresionDaoImpl, M:actualizaControlPapel");
		}
		return updateCP;
	}
	
	
	/**
	 * FunSQLSelectCatalogoFirmas
	 * @param banco
	 * @param chequera
	 * @return
	 */
	
	public List<CatFirmasDto>consultarCatalogoFirmas(int banco, String chequera){
		List<CatFirmasDto> list = new ArrayList<CatFirmasDto>();
		StringBuffer sql = new StringBuffer();
		try
		{
			sql.append("\n SELECT p.nombre, " );
		    sql.append("\n        f.tipo_firma, ");
		    sql.append("\n        p.path_firma");
		    sql.append("\n   FROM cat_firma f, cat_persona p");
		    sql.append("\n  WHERE f.bco_cve = " + banco);
		    sql.append("\n    AND f.cta_no = '" + Utilerias.validarCadenaSQL(chequera) + "'");
		    sql.append("\n    AND f.b_deter = '1' ");
		    sql.append("\n    AND f.no_persona = p.no_persona");
		    
		    //logger.info(sql.toString());
			list= jdbcTemplate.query(sql.toString(), new RowMapper<CatFirmasDto>(){
			public CatFirmasDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatFirmasDto cons = new CatFirmasDto();
				cons.setNombre(rs.getString("nombre"));
				cons.setTipoFirma(rs.getString("tipo_firma"));
				cons.setPathFirma(rs.getString("path_firma"));
				return cons;
			}});
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ImpresionDaoImpl, M:consultarCatalogoFirmas");
		}
		return list;
	}
	
	
	/**inician consultas correspondientes a la generacion del archivo de seguridad
	 * 27/06/2011
	 * **/
	
	/**
	 * FunSQLCombo240
	 */
	public List<CatCtaBancoDto>consultarChequeras(int banco, int empresa){
		List<CatCtaBancoDto> list = new ArrayList<CatCtaBancoDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("\n SELECT id_chequera as ID,");
		    sql.append("\n        id_chequera as Descrip ");
		    sql.append("\n   FROM cat_cta_banco");
		    sql.append("\n  WHERE no_empresa = " + empresa);
		    sql.append("\n    AND id_banco = " + banco);
		    list= jdbcTemplate.query(sql.toString(), new RowMapper<CatCtaBancoDto>(){
			public CatCtaBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatCtaBancoDto cons = new CatCtaBancoDto();
				cons.setIdChequera(rs.getString("ID"));
				cons.setDescChequera(rs.getString("Descrip"));
				return cons;
			}});
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:consultarChequeras");
		}
		return list;
	}
	
	
	/**
	 * FunSQLSelect977
	 * @param dtoCheques
	 * @return List<MovimientoDto>
	 */
	//Modificado A.A.G
	public List<MovimientoDto>consultarDocumentosCheques(ConsultaChequesDto dtoCheques){
		List<MovimientoDto>listRetorno = new ArrayList<MovimientoDto>();
		StringBuffer sSQL = new StringBuffer();
		try{
			sSQL.append("\n SELECT DISTINCT m.id_banco");
		    sSQL.append("\n        ,m.id_chequera");
		    sSQL.append("\n        ,m.no_cheque");
		    sSQL.append("\n        ,m.beneficiario");
		    sSQL.append("\n        ,m.importe");
		    sSQL.append("\n        ,m.fec_imprime");
		    sSQL.append("\n        ,m.no_folio_det");
		    sSQL.append("\n        ,c.desc_banco");
		    sSQL.append("\n        ,m.concepto ");
		    sSQL.append("\n        ,ccb.desc_plaza  as plaza ");
		    sSQL.append("\n        ,m.id_divisa" );//'ACM 02/05/07 - Se agrega para insertar este dato en el archivo de seguridad de Scotia
		    sSQL.append("\n        ,m.no_docto" );
		    
		    sSQL.append("\n   FROM movimiento m, cat_banco c, cat_cta_banco ccb");
		    sSQL.append("\n  WHERE m.id_banco = c.id_banco");
		    sSQL.append("\n    AND m.id_chequera = ccb.id_chequera ");
		    sSQL.append("\n    AND m.no_empresa = ccb.no_empresa ");
		    sSQL.append("\n    AND id_forma_pago in (1,7) ");
		    sSQL.append("\n    AND id_tipo_operacion in(3000,3200)");
		    sSQL.append("\n    AND id_estatus_mov in ('I','R')");
		    
		    if(!dtoCheques.isChkTodas())
		        sSQL.append("\n   AND m.no_empresa=" + dtoCheques.getNoEmpresa());
		 
		    if(dtoCheques.isOptSeleccion())//pbxGenerar
		        sSQL.append("\n    AND (m.b_arch_seg is null or m.b_arch_seg = 'N' or m.b_arch_seg = '')");
		    else
		        sSQL.append("\n    AND (m.b_arch_seg = 'S')");
		   
		    sSQL.append("\n    AND (m.b_entregado = 'N'");
		    sSQL.append("\n     OR id_estatus_cb in ('P'))");

		    if(dtoCheques.getCriterioBanco() != 0)
		    {
		    	sSQL.append("\n    AND m.id_banco = " + dtoCheques.getCriterioBanco());
		    }
		    if(!dtoCheques.getCriterioChequera().equals(""))
		    {
		    	sSQL.append("\n    AND m.id_chequera = '" + Utilerias.validarCadenaSQL(dtoCheques.getCriterioChequera()) + "'");
		    }
		    if(dtoCheques.getCriterioImporteIni() != 0)
			{
		    	
					if(dtoCheques.getCriterioImporteFin() != 0)
					{
				        sSQL.append("\n        AND m.importe >= " + dtoCheques.getCriterioImporteIni() +
	                                "          AND m.importe <= " + dtoCheques.getCriterioImporteFin());
					}else
				        sSQL.append("\n        AND m.importe = " + dtoCheques.getCriterioImporteIni());
		    	
		    	
			}
		    
		    if(dtoCheques.getCriterioFechaIni() != null && !dtoCheques.getCriterioFechaIni().equals("")){
		    	if(dtoCheques.getCriterioFechaFin() != null && !dtoCheques.getCriterioFechaFin().equals("")){
			        sSQL.append("\n        AND m.fec_imprime >= '" + Utilerias.validarCadenaSQL(funciones.ponerFechaSola(dtoCheques.getCriterioFechaIni())) +"'"+
                                "          AND m.fec_imprime <= '" + Utilerias.validarCadenaSQL(funciones.ponerFechaSola(dtoCheques.getCriterioFechaFin()))+"'");
				}else
			        sSQL.append("\n        AND m.fec_imprime = '" + Utilerias.validarCadenaSQL(funciones.ponerFechaSola(dtoCheques.getCriterioFechaIni())) +"'");
			}

		    sSQL.append("\n  UNION ALL");
		    sSQL.append("\n  SELECT DISTINCT m.id_banco");
		    sSQL.append("\n         ,m.id_chequera");
		    sSQL.append("\n         ,m.no_cheque");
		    sSQL.append("\n         ,m.beneficiario");
		    sSQL.append("\n         ,m.importe");
		    sSQL.append("\n         ,m.fec_imprime");
		    sSQL.append("\n         ,m.no_folio_det");
		    sSQL.append("\n         ,c.desc_banco");
		    sSQL.append("\n         ,m.concepto");
		    sSQL.append("\n         ,ccb.desc_plaza as plaza ");
		    sSQL.append("\n         ,m.id_divisa");// 'ACM 02/05/07 - Se agrega para insertar este dato en el archivo de seguridad de Scotia
		    sSQL.append("\n         ,m.no_docto" );
		    
		    sSQL.append("\n   FROM hist_movimiento m, cat_banco c, cat_cta_banco ccb");
		    sSQL.append("\n  WHERE m.id_banco = c.id_banco");
		    sSQL.append("\n    AND m.id_chequera = ccb.id_chequera ");
		    sSQL.append("\n    AND m.no_empresa = ccb.no_empresa ");
		    sSQL.append("\n    AND id_forma_pago in (1,7) ");
		    sSQL.append("\n    AND id_tipo_operacion = 3200");
		    sSQL.append("\n    AND id_estatus_mov in ('I','R')");
		    
		    if(!dtoCheques.isChkTodas())
		        sSQL.append("\n   AND m.no_empresa=" + dtoCheques.getNoEmpresa());
		    
		    if(dtoCheques.isOptSeleccion())//pbxGenerar
		        sSQL.append("\n     AND (m.b_arch_seg is null or m.b_arch_seg = 'N')");
		    else
		        sSQL.append("\n     AND (m.b_arch_seg = 'S')");
		   
		    sSQL.append("\n     AND (m.b_entregado = 'N'");
		    sSQL.append("\n      OR id_estatus_cb in ('P'))");

		    if(dtoCheques.getCriterioBanco() != 0)
		    {
		    	sSQL.append("\n    AND m.id_banco = " + dtoCheques.getCriterioBanco());
		    }
		    if(!dtoCheques.getCriterioChequera().equals(""))
		    {
		    	sSQL.append("\n    AND m.id_chequera = '" + Utilerias.validarCadenaSQL(dtoCheques.getCriterioChequera()) + "'");
		    }
		    if(dtoCheques.getCriterioImporteIni() != 0)
			{
		    	
					if(dtoCheques.getCriterioImporteFin() != 0)
					{
				        sSQL.append("\n        AND m.importe >= " + dtoCheques.getCriterioImporteIni() +
	                                "          AND m.importe <= " + dtoCheques.getCriterioImporteFin());
					}else
				        sSQL.append("\n        AND m.importe = " + dtoCheques.getCriterioImporteIni());
		    	
		    	
			}
		    if(dtoCheques.getCriterioFechaIni() != null && !dtoCheques.getCriterioFechaIni().equals(""))
			{
				if(dtoCheques.getCriterioFechaFin() != null && !dtoCheques.getCriterioFechaFin().equals(""))
				{
			        sSQL.append("\n        AND m.fec_imprime >= '" + Utilerias.validarCadenaSQL(funciones.ponerFechaSola(dtoCheques.getCriterioFechaIni())) +"'"+
                                "          AND m.fec_imprime <= '" + Utilerias.validarCadenaSQL(funciones.ponerFechaSola(dtoCheques.getCriterioFechaFin()))+"'");
				}else
			        sSQL.append("\n        AND m.fec_imprime = '" + Utilerias.validarCadenaSQL(funciones.ponerFechaSola(dtoCheques.getCriterioFechaIni())) +"'");
			}
		    
		    //logger.info("consulta de cheques: "+sSQL.toString());
		    System.out.println("esta es la consulta"+sSQL.toString()+"temina");
			listRetorno = jdbcTemplate.query(sSQL.toString(), new RowMapper<MovimientoDto>(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto movimiento = new MovimientoDto();
					movimiento.setIdBanco(rs.getInt("id_banco"));
					movimiento.setIdChequera(rs.getString("id_chequera"));
					movimiento.setNoCheque(rs.getInt("no_cheque"));
					movimiento.setBeneficiario(rs.getString("beneficiario"));
					movimiento.setImporte(rs.getDouble("importe"));
					movimiento.setFecImprime(rs.getDate("fec_imprime"));
					movimiento.setNoFolioDet(rs.getInt("no_folio_det"));
					movimiento.setDescBanco(rs.getString("desc_banco"));
					movimiento.setConcepto(rs.getString("concepto"));
					movimiento.setPlaza(rs.getInt("plaza"));
					movimiento.setIdDivisa(rs.getString("id_divisa"));
					movimiento.setNoDocto(rs.getString("no_docto"));
					return movimiento;
				}
			});

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:consultarDocumentosCheques");
		}
		return listRetorno;
	}
	
	/**
	 * FunSQLComboempresas
	 */
	//modifcado A.A.G
	public List<LlenaComboEmpresasDto> consultarEmpresas(int usuario){
		List<LlenaComboEmpresasDto> list = new ArrayList<LlenaComboEmpresasDto>();
		StringBuffer sSQL = new StringBuffer();
		try{
			sSQL.append("\n  SELECT no_empresa as ID, nom_empresa as describ ");
		    sSQL.append("\n    FROM empresa");
		    sSQL.append("\n   WHERE no_empresa > 1");
		    sSQL.append("\n     AND no_empresa in (SELECT no_empresa ");
		    sSQL.append("\n                          FROM usuario_empresa ");
		    sSQL.append("\n                         WHERE no_usuario = " + usuario + ")");
		   
			list = jdbcTemplate.query(sSQL.toString(), new RowMapper<LlenaComboEmpresasDto>(){
				public LlenaComboEmpresasDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboEmpresasDto empresa = new LlenaComboEmpresasDto();
					empresa.setNoEmpresa(rs.getInt("ID"));
					empresa.setNomEmpresa(rs.getString("describ"));
					return empresa;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:consultarEmpresas");
		}
		return list;
	}
	
	public List<DetArchTransferDto> consultarTipoEnvio(String documento, double importe, int folios){
		List<DetArchTransferDto> list = new ArrayList<DetArchTransferDto>();
		StringBuffer sSQL = new StringBuffer();
		try{
			sSQL.append("\n select a.tipo_envio_layout, d.no_folio_det");
		    sSQL.append("\n from det_arch_transfer d,arch_transfer a ");
		    sSQL.append("\n where  a.id_banco = d.id_banco And d.importe = " + importe + "");
		    sSQL.append("\n and d.no_docto='" + Utilerias.validarCadenaSQL(documento) + "'");
		    sSQL.append("\n and d.nom_arch=a.nom_arch");
		    sSQL.append("\n and no_folio_det='" + Utilerias.validarCadenaSQL(folios) + "'");
		    sSQL.append("\n and id_estatus_arch='T'");
			list = jdbcTemplate.query(sSQL.toString(), new RowMapper<DetArchTransferDto>(){
				public DetArchTransferDto mapRow(ResultSet rs, int idx) throws SQLException {
					DetArchTransferDto envio = new DetArchTransferDto();
					envio.setTipoEnvioLayout(rs.getString("tipo_envio_layout"));
					envio.setNoFolioDet(rs.getInt("no_folio_det"));
					return envio;
				}
			});
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:consultarTipoEnvio");
		}
		return list;
	}
	
	/**
	 * Updatedet_arch
	 */
	public int actualizarDetArchTransfer(int folios){
		int actualiza = -1;
		StringBuffer sSQL = new StringBuffer();
		try{
			sSQL.append("\n update Det_Arch_Transfer d");
		    sSQL.append("\n Set Id_Estatus_Arch='X'");
		    sSQL.append("\n  Where Exists( Select '1'");
		    sSQL.append("\n From Arch_Transfer A");
		    sSQL.append("\n where");
		    sSQL.append("\n A.Nom_Arch=D.Nom_Arch");
		    sSQL.append("\n and a.id_banco=d.id_banco");
    		sSQL.append("\n and d.no_folio_det='" + Utilerias.validarCadenaSQL(folios) + "')");
    		if (jdbcTemplate.update(sSQL.toString()) != 0) {
    			sSQL = new StringBuffer();
				sSQL.append("\n Update Arch_Transfer A");
				sSQL.append("\n Set A.Importe=( Select Sum(D.Importe)");
				sSQL.append("\n From Arch_Transfer A,Det_Arch_Transfer D");
				sSQL.append("\n where a.nom_arch in (select a.nom_arch from det_arch_transfer d where d.no_folio_det= '" + Utilerias.validarCadenaSQL(folios) + "') ");
				sSQL.append("\n And D.Id_Estatus_Arch='T' And A.Id_Banco=D.Id_Banco");
				sSQL.append("\n And A.Nom_Arch=D.Nom_Arch),");
				sSQL.append("\n A.Registros = A.Registros-1");
				sSQL.append("\n Where a.nom_arch in (Select a.nom_arch");
				sSQL.append("\n from arch_transfer a,det_arch_transfer d");
				sSQL.append("\n Where A.Nom_Arch=D.Nom_Arch And A.Id_Banco=D.Id_Banco");
				sSQL.append("\n and d.no_folio_det='" + Utilerias.validarCadenaSQL(folios) + "') ");
				actualiza = jdbcTemplate.update(sSQL.toString());
				return actualiza;
			}
    		return actualiza;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:actualizarDetArchTransfer");
			return actualiza;
		}
		
	}
	
	/**
	 * FunSQLInsert_det_arch_transfer
	 * @param sArchivo2
	 * @param sBenef
	 * @param dImporte
	 * @param sFolios
	 * @param dFecha
	 * @param sChequera
	 * @param iBanco
	 * @param sConcepto
	 * @param sDocto
	 * @param iCheque
	 * @return
	 */
	public int insertarDetArchTransfer(String sArchivo2, String sBenef, double dImporte, int sFolios, Date dFecha,
									String sChequera, int iBanco, String sConcepto, String sDocto, int iCheque){
		int inserta = -1;
		StringBuffer sSQL = new StringBuffer();
		try{
		    sSQL.append("\n INSERT INTO det_Arch_transfer");
		    sSQL.append("\n (nom_arch,beneficiario,importe,no_folio_det,fec_valor,id_chequera,");
    		sSQL.append("\nid_banco,concepto,id_estatus_arch,no_docto,id_chequera_benef)");
		        
		    sSQL.append("\n values (");
		    sSQL.append("\n '" + Utilerias.validarCadenaSQL(sArchivo2) + "','" + Utilerias.validarCadenaSQL(sBenef) + "'," + dImporte + ", ");
		    sSQL.append("\n" +  sFolios + ",'" + Utilerias.validarCadenaSQL(funciones.ponerFormatoDate(dFecha)) 
		    				 + "','" + Utilerias.validarCadenaSQL(sChequera) + "',");
		    sSQL.append("\n" +  iBanco + ",'" + Utilerias.validarCadenaSQL(sConcepto) 
		    				 +  "','T','" + Utilerias.validarCadenaSQL(sDocto) + "'," + iCheque + ")");
		     
		    logger.info("inserta detarchtransfer");
		    inserta = jdbcTemplate.update(sSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:insertarDetArchTransfer");
		}
		return inserta;
	}
	
	// Actualizado A.A.G
	public int insertarArchTransfer(String sArchivo2, int iBanco, Date dFecha, double dImporte, int iUsuario, int iNreg){
		int inserta = -1;
		StringBuffer sSQL = new StringBuffer();
		try{
			sSQL.append("\n INSERT INTO arch_transfer ");
		    sSQL.append("\n values (");
		    sSQL.append("\n '" + Utilerias.validarCadenaSQL(sArchivo2) + "'," + iBanco + ",");
		    sSQL.append("\n '" + Utilerias.validarCadenaSQL(funciones.ponerFormatoDate(dFecha))  + "'," + dImporte + ",");
		    sSQL.append("\n" + iNreg + ",null," + iUsuario + ",null,null,'CHE')");
			inserta = jdbcTemplate.update(sSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:insertarArchTransfer");
		}
	return inserta;
	}

	public List<Map<String, Object>> consultarDetalleChaqueAzteca(String noFolioDet){
		 List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
		 StringBuffer sSQL = new StringBuffer();
		 try {
			sSQL.append("");
			sSQL.append("Select E.Nom_Empresa,p.rfc, m.solicita");
			sSQL.append("\n From Movimiento M Join Empresa E On E.No_Empresa = M.No_Empresa");
			sSQL.append("\n join persona p on p.no_empresa = e.no_empresa");
			sSQL.append("\n Where m.No_Folio_Det = ");
			sSQL.append(noFolioDet);
			sSQL.append("   UNION ALL ");
			sSQL.append("Select E.Nom_Empresa,p.rfc, m.solicita");
			sSQL.append("\n From hist_Movimiento M Join Empresa E On E.No_Empresa = M.No_Empresa");
			sSQL.append("\n join persona p on p.no_empresa = e.no_empresa");
			sSQL.append("\n Where m.No_Folio_Det = ");
			sSQL.append(noFolioDet);
			System.out.println("consulta detalla"+sSQL);
			return jdbcTemplate.query(sSQL.toString(), new RowMapper<Map<String, Object>>(){
				public Map<String, Object> mapRow(ResultSet rs, int idx) throws SQLException {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("rfc", rs.getString("RFC"));
					map.put("noEmpresa", rs.getString("nom_empresa"));
					map.put("solicita", rs.getString("solicita"));
					return map;
				}
			});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:consultarDetalleChaqueAzteca");
		}
		 return listResult;
	}
	public int actualizarMovimientoHistMovimiento(String sArchProtegido, String sFolios){
		int actualiza = -1;
		StringBuffer sSQL = new StringBuffer();
		try{
			sSQL.append("\n UPDATE movimiento " );
		    sSQL.append("\n    SET b_arch_seg = 'S'");
		    sSQL.append("\n    , arch_protegido = '" + Utilerias.validarCadenaSQL(sArchProtegido) + "'");
		    sSQL.append("\n  WHERE no_folio_det in (" + sFolios + ")");
		    if(jdbcTemplate.update(sSQL.toString()) != 0){
		    	sSQL.append("\n UPDATE hist_movimiento ");
			    sSQL.append("\n    SET b_arch_seg = 'S'");
			    sSQL.append("\n    , arch_protegido = '" + Utilerias.validarCadenaSQL(sArchProtegido) + "'");
			    sSQL.append("\n  WHERE no_folio_det in (" + sFolios + ")");
			    return jdbcTemplate.update(sSQL.toString());
		    }else
		    	return -1;
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:actualizarMovimiento");
		}
		return actualiza;
	}
	
	// Actualizado A.A.G
	public List<CatBancoDto> consultarPropiedadesArchivo(int banco){
		List<CatBancoDto> list = new ArrayList<CatBancoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n SELECT arch_protegido");
		    sbSQL.append("\n        ,no_protegido, ");
		    sbSQL.append("\n        path_protegido, ");
		    sbSQL.append("\n        fec_protegido ");
		    sbSQL.append("\n   FROM cat_banco ");
		    sbSQL.append("\n  WHERE id_banco = " + banco);
		  
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper<CatBancoDto>(){
				public CatBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
					CatBancoDto archivo = new CatBancoDto();
					archivo.setArchProtegido(rs.getString("arch_protegido"));
					archivo.setNoProtegido(rs.getInt("no_protegido"));
					archivo.setPathProtegido(rs.getString("path_protegido"));
					archivo.setFecProtegido(rs.getString("fec_protegido"));
					return archivo;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:consultarPropiedadesArchivo");
		}
		return list;
	}
	
	/**
	 * FunSQLUpdate146
	 * @param banco
	 * @return
	 */
	public int actualizarCatBanco(int banco){
		int actualiza = -1;
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n UPDATE cat_banco ");
		    sbSQL.append("\n    SET no_protegido = no_protegido + 1");
		    sbSQL.append("\n  WHERE id_banco = " + banco);
		    
		    logger.info("actualiza catbanco");
		    actualiza = jdbcTemplate.update(sbSQL.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:actualizarCatBanco");
		}
		return actualiza;
	}
	
	/**consultas de reimpresion de cheques
	 * 04/07/2011
	 * **/
	
	/**
	 * FunSQLSelect721
	 */
	//Modificado EMS 04/01/2016
	public List<MovimientoDto>consultarSolicitudesReimpresion(ConsultaChequesDto dtoCheques){
		List<MovimientoDto>listRetorno = new ArrayList<MovimientoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			
			
			//if(ConstantesSet.gsDBM.equals("ORACLE")){
				
	            sbSQL.append("\n SELECT cc.desc_caja,m.no_empresa,");
	            sbSQL.append("\n        m.no_folio_det as no_solicitud,");
	            sbSQL.append("\n        m.importe,");
	            sbSQL.append("\n        m.beneficiario,");
	            sbSQL.append("\n        cb.desc_banco,");
	            sbSQL.append("\n        m.id_chequera as cta_alias,");
	            sbSQL.append("\n        cc.desc_caja as cja_ubic,");
	            sbSQL.append("\n        m.concepto,");
	            sbSQL.append("\n        p.razon_social as cia_nmbr,");
	            sbSQL.append("\n        m.agrupa1 as centro_costo,");
	            sbSQL.append("\n        m.id_banco as bco_cve,");
	            sbSQL.append("\n        m.id_tipo_movto as tipo_egreso,");
	            sbSQL.append("\n        m.importe_original,");
	            sbSQL.append("\n        m.no_docto as no_contrarecibo,");
	            sbSQL.append("\n        pp.rfc,");
	            sbSQL.append("\n        m.id_divisa as moneda,");
	            sbSQL.append("\n        m.id_contable,");
	            sbSQL.append("\n        m.id_chequera as cta_no,");
	            sbSQL.append("\n        m.id_leyenda as leyenda_prote,");
	            sbSQL.append("\n        ccb.desc_plaza as plaza,");
	            sbSQL.append("\n        ccb.desc_sucursal as sucursal,");
	            sbSQL.append("\n        cb.b_protegido,");
	            sbSQL.append("\n        m.id_chequera as no_cuenta,");
	            sbSQL.append("\n        cb.codigo_seguridad,");
	            sbSQL.append("\n        cb.cve_plaza,");
	            sbSQL.append("\n        cb.id_banco as no_banco,");
	            //sbSQL.append("\n        d.calle_no || ' ' || d.colonia as cia_dir,");
	            //sbSQL.append("\n        d.deleg_municipio || ' ' || d.ciudad as cia_dir2,");
	            sbSQL.append("\n        p.rfc as cia_rfc,");
	            sbSQL.append("\n        m.firmante1,");
	            sbSQL.append("\n        m.firmante2,");
	            sbSQL.append("\n        m.no_cheque,");
	            sbSQL.append("\n        m.fec_cheque,");
	            sbSQL.append("\n        pp.equivale_persona as equivale_benef,");
	            sbSQL.append("\n        m.id_grupo,");
	            sbSQL.append("\n        m.id_rubro,");
	            sbSQL.append("\n        m.no_pedido,");
	            sbSQL.append("\n        p.equivale_persona as equivale_empresa,");
	            sbSQL.append("\n        m.id_divisa_original,");
	            sbSQL.append("\n        m.tipo_cambio,");
	            sbSQL.append("\n        m.division,");
	            sbSQL.append("\n        e.dir_logo as logo_empresa,");
	            //sbSQL.append("\n        d.id_cp,");
	            sbSQL.append("\n        cb.dir_logo as logo_banco,");
	            sbSQL.append("\n        e.dir_logo_alterno");
	            
	            sbSQL.append("\n   FROM movimiento m, cat_banco cb, cat_caja cc, persona p, persona pp, ");
	            sbSQL.append("\n        cat_cta_banco ccb, empresa e");            //'se agregó la tabla movimiento
	            //sbSQL.append("\n        direccion d, cat_cta_banco ccb, empresa e");            //'se agregó la tabla movimiento
	            
	            sbSQL.append("\n   WHERE m.id_estatus_mov in ('I','R') ");
	            
	            if(dtoCheques.getCriterioSolicitudIni() != 0 || dtoCheques.getCriterioSolicitudFin()!= 0)
	            {
	            	if(dtoCheques.getCriterioSolicitudFin() != 0)
	            	{
	                    sbSQL.append("\n    AND (m.no_cheque >= " + dtoCheques.getCriterioSolicitudIni());
	                    sbSQL.append("\n    AND m.no_cheque <= " + dtoCheques.getCriterioSolicitudFin() + ")");
	            	}
	                else
	                    sbSQL.append("\n    AND m.no_cheque = " + dtoCheques.getCriterioSolicitudIni());
	            }
	            
	            sbSQL.append("\n   AND m.id_banco = " + dtoCheques.getCriterioBanco());
	            sbSQL.append("\n   AND m.no_empresa = ccb.no_empresa");
	            sbSQL.append("\n   AND m.id_banco = ccb.id_banco");
	            sbSQL.append("\n   AND m.id_chequera = ccb.id_chequera");
	            sbSQL.append("\n   AND m.id_banco = cb.id_banco");
	            
	            sbSQL.append("\n   AND m.no_empresa = p.no_empresa");
	            sbSQL.append("\n   AND m.no_empresa = p.no_persona");
	            sbSQL.append("\n   AND p.id_tipo_persona = 'E'");
	            
	            sbSQL.append("\n   AND m.no_empresa = e.no_empresa ");
	            sbSQL.append("\n   AND m.no_cliente = pp.no_persona");
	            sbSQL.append("\n   AND pp.id_tipo_persona = 'P'");
	            
	            //sbSQL.append("\n   AND m.no_empresa = d.no_empresa");
	            //sbSQL.append("\n   AND m.no_empresa = d.no_persona");
	            //sbSQL.append("\n   AND d.id_tipo_persona = 'E'");
	            
	            sbSQL.append("\n   AND m.id_caja = cc.id_caja ");
	            //sbSQL.append("\n   AND d.id_tipo_direccion = 'OFNA' ");
	                  
	            if(!dtoCheques.getCriterioChequera().equals(""))
	                sbSQL.append("\n   AND ccb.id_chequera = '" + Utilerias.validarCadenaSQL(dtoCheques.getCriterioChequera()) + "'");
	                  
	            sbSQL.append("\n   AND m.no_empresa in (Select no_empresa from usuario_empresa ");
	            sbSQL.append("\n                        where no_usuario = " + dtoCheques.getIdUsuario() + ") ");
	            sbSQL.append("\n   AND m.id_caja = " + dtoCheques.getIdCaja());
	            
	            if(dtoCheques.getCriterioEmpresa() != 0)
	                sbSQL.append("\n   AND m.no_empresa = " + dtoCheques.getCriterioEmpresa());
	            
	            if(!dtoCheques.getCriterioNoDocto().equals(""))
	                sbSQL.append("\n   AND m.no_docto = '" + Utilerias.validarCadenaSQL(dtoCheques.getCriterioNoDocto()) + "'");
	            
			// } fin If ORACLE
		
			/*
			if(ConstantesSet.gsDBM.equals("SQL SERVER"))
			{
	            sbSQL.append("\n SELECT desc_caja,m.no_empresa,");
	            sbSQL.append("\n        m.no_folio_det as no_solicitud,");
	            sbSQL.append("\n        m.importe,");
	            sbSQL.append("\n        m.beneficiario,");
	            sbSQL.append("\n        cb.desc_banco,");
	            sbSQL.append("\n        m.id_chequera as cta_alias,");
	            sbSQL.append("\n        cc.desc_caja as cja_ubic,");
	            sbSQL.append("\n        m.concepto,");
	            sbSQL.append("\n        p.razon_social as cia_nmbr,");
	            sbSQL.append("\n        m.agrupa1 as centro_costo,");
	            sbSQL.append("\n        m.id_banco as bco_cve,");
	            sbSQL.append("\n        m.id_tipo_movto as tipo_egreso,");
	            sbSQL.append("\n        m.importe_original,");
	            sbSQL.append("\n        m.no_docto as no_contrarecibo,");
	            sbSQL.append("\n        pp.rfc,");
	            sbSQL.append("\n        m.id_divisa as moneda,");
	            sbSQL.append("\n        m.id_contable,");
	            sbSQL.append("\n        m.id_chequera as cta_no,");
	            sbSQL.append("\n        m.id_leyenda as leyenda_prote,");
	            sbSQL.append("\n        ccb.desc_plaza as plaza,");
	            sbSQL.append("\n        ccb.desc_sucursal as sucursal,");
	            sbSQL.append("\n        cb.b_protegido,");
	            sbSQL.append("\n        m.id_chequera as no_cuenta,");
	            sbSQL.append("\n        cb.codigo_seguridad,");
	            sbSQL.append("\n        cb.cve_plaza,");
	            sbSQL.append("\n        cb.id_banco as no_banco,");
	            sbSQL.append("\n        d.calle_no + ' ' + d.colonia as cia_dir,");
	            sbSQL.append("\n        d.deleg_municipio + ' ' + d.ciudad as cia_dir2,");
	            sbSQL.append("\n        p.rfc as cia_rfc,");
	            sbSQL.append("\n        m.firmante1, ");
	            sbSQL.append("\n        m.firmante2,");
	            sbSQL.append("\n        m.no_cheque,");
	            sbSQL.append("\n        m.fec_cheque,");
	            sbSQL.append("\n        pp.equivale_persona as equivale_benef,");
	            sbSQL.append("\n        m.id_grupo,");
	            sbSQL.append("\n        m.id_rubro,");
	            sbSQL.append("\n        m.no_pedido,");
	            sbSQL.append("\n        p.equivale_persona as equivale_empresa,");
	            sbSQL.append("\n        m.id_divisa_original,");
	            sbSQL.append("\n        m.tipo_cambio,");
	            sbSQL.append("\n        m.division,");
	            sbSQL.append("\n        d.id_cp,");
	            sbSQL.append("\n        e.dir_logo as logo_empresa,");
	            sbSQL.append("\n        cb.dir_logo as logo_banco,");
	            sbSQL.append("\n        e.dir_logo_alterno, m.ind_iva ");
	            
	            sbSQL.append("\n   FROM movimiento m, cat_banco cb, cat_caja cc, persona p, persona pp, ");
	            sbSQL.append("\n        direccion d, cat_cta_banco ccb, empresa e");            //'se agregó la tabla movimiento
	            sbSQL.append("\n   WHERE m.id_estatus_mov in ('I','R') ");
	            
	            if(dtoCheques.getCriterioSolicitudIni() != 0 || dtoCheques.getCriterioSolicitudFin()!= 0)
	            {
	            	if(dtoCheques.getCriterioSolicitudFin() != 0)
	            	{
	                    sbSQL.append("\n    AND (m.no_cheque >= " + dtoCheques.getCriterioSolicitudIni());
	                    sbSQL.append("\n    AND m.no_cheque <= " + dtoCheques.getCriterioSolicitudFin() + ")");
	            	}
	                else
	                    sbSQL.append("\n    AND m.no_cheque = " + dtoCheques.getCriterioSolicitudIni());
	            }
	            
	            
	            sbSQL.append("\n   AND m.id_banco = " + dtoCheques.getCriterioBanco());
	            sbSQL.append("\n   AND m.no_empresa = ccb.no_empresa");
	            sbSQL.append("\n   AND m.id_banco = ccb.id_banco");
	            sbSQL.append("\n   AND m.id_chequera = ccb.id_chequera");
	            sbSQL.append("\n   AND m.id_banco = cb.id_banco");
	            
	            sbSQL.append("\n   AND m.no_empresa = p.no_empresa");
	            sbSQL.append("\n   AND m.no_empresa = p.no_persona");
	            sbSQL.append("\n   AND p.id_tipo_persona = 'E'");
	            
	            sbSQL.append("\n   AND m.no_empresa = e.no_empresa ");
	            
	            sbSQL.append("\n   AND m.no_empresa *= d.no_empresa");
	            sbSQL.append("\n   AND m.no_empresa *= d.no_persona");
	            sbSQL.append("\n   AND d.id_tipo_persona = 'E'");
	            
	            sbSQL.append("\n   AND pp.no_empresa = 552");
	            sbSQL.append("\n   AND m.no_cliente = pp.no_persona");
	            sbSQL.append("\n   AND pp.id_tipo_persona = 'P'");
	            
	            sbSQL.append("\n   AND m.id_caja *= cc.id_caja ");
	            sbSQL.append("\n   AND d.id_tipo_direccion = 'OFNA' ");
	            
	            if(!dtoCheques.getCriterioChequera().equals(""))
	                sbSQL.append("\n   AND ccb.id_chequera = '" + dtoCheques.getCriterioChequera() + "'");
	                  
	            sbSQL.append("\n   AND m.no_empresa in (Select no_empresa from usuario_empresa ");
	            sbSQL.append("\n                         where no_usuario = " + dtoCheques.getIdUsuario() + ") ");
	            sbSQL.append("\n   AND m.id_caja = " + dtoCheques.getIdCaja());
	            
	            if(dtoCheques.getCriterioEmpresa() != 0)
	                sbSQL.append("\n   AND m.no_empresa = " + dtoCheques.getCriterioEmpresa());
	            
	            if(!dtoCheques.getCriterioNoDocto().equals(""))
	                sbSQL.append("\n   AND m.no_docto = '" + dtoCheques.getCriterioNoDocto() + "'");//se cambio no_docto por no_cheque
	            
	            if(dtoCheques.getChequeNomina() == 1) 			//Se agrega if para determinar si el usuario puede ver los cheques de nómina o gastos
	            	sbSQL.append("\n   AND m.id_area = 1" );	//'Visualiza sólo cheques de nómina
                else if(dtoCheques.getChequeNomina() == 2)
                	sbSQL.append("\n   AND m.id_area = 2"); 	//'Visualiza sólo cheques de gastos
                else if(dtoCheques.getChequeNomina() == 0)
	            	sbSQL.append("\n   AND m.id_area IS NOT NULL");	//'se cambio IS NULL por IS NOT NULL
	            
	            sbSQL.append("\n  UNION ");
	            
	            sbSQL.append("\n SELECT desc_caja,m.no_empresa,");
	            sbSQL.append("\n        m.no_folio_det as no_solicitud,");
	            sbSQL.append("\n        m.importe,");
	            sbSQL.append("\n        m.beneficiario,");
	            sbSQL.append("\n        cb.desc_banco,");
	            sbSQL.append("\n        m.id_chequera as cta_alias,");
	            sbSQL.append("\n        cc.desc_caja as cja_ubic,");
	            sbSQL.append("\n        m.concepto,");
	            sbSQL.append("\n        p.razon_social as cia_nmbr,");
	            sbSQL.append("\n        m.agrupa1 as centro_costo,");
	            sbSQL.append("\n        m.id_banco as bco_cve,");
	            sbSQL.append("\n        m.id_tipo_movto as tipo_egreso,");
	            sbSQL.append("\n        m.importe_original,");
	            sbSQL.append("\n        m.no_docto as no_contrarecibo,");
	            sbSQL.append("\n        pp.rfc,");
	            sbSQL.append("\n        m.id_divisa as moneda,");
	            sbSQL.append("\n        m.id_contable,");
	            sbSQL.append("\n        m.id_chequera as cta_no,");
	            sbSQL.append("\n        m.id_leyenda as leyenda_prote,");
	            sbSQL.append("\n        ccb.desc_plaza as plaza,");
	            sbSQL.append("\n        ccb.desc_sucursal as sucursal,");
	            sbSQL.append("\n        cb.b_protegido,");
	            sbSQL.append("\n        m.id_chequera as no_cuenta,");
	            sbSQL.append("\n        cb.codigo_seguridad,");
	            sbSQL.append("\n        cb.cve_plaza,");
	            sbSQL.append("\n        cb.id_banco as no_banco,");
	            sbSQL.append("\n        d.calle_no + ' ' + d.colonia as cia_dir,");
	            sbSQL.append("\n        d.deleg_municipio + ' ' + d.ciudad as cia_dir2,");
	            sbSQL.append("\n        p.rfc as cia_rfc,");
	            sbSQL.append("\n        m.firmante1, ");
	            sbSQL.append("\n        m.firmante2,");
	            sbSQL.append("\n        m.no_cheque,");
	            sbSQL.append("\n        m.fec_cheque,");
	            sbSQL.append("\n        pp.equivale_persona as equivale_benef,");
	            sbSQL.append("\n        m.id_grupo,");
	            sbSQL.append("\n        m.id_rubro,");
	            sbSQL.append("\n        m.no_pedido,");
	            sbSQL.append("\n        p.equivale_persona as equivale_empresa,");
	            sbSQL.append("\n        m.id_divisa_original,");
	            sbSQL.append("\n        m.tipo_cambio,");
	            sbSQL.append("\n        m.division,");
	            sbSQL.append("\n        d.id_cp,");
	            sbSQL.append("\n        e.dir_logo as logo_empresa,");
	            sbSQL.append("\n        cb.dir_logo as logo_banco,");
	            sbSQL.append("\n        e.dir_logo_alterno, m.ind_iva ");
	            
	            sbSQL.append("\n   FROM hist_movimiento m, cat_banco cb, cat_caja cc, persona p, persona pp, ");
	            sbSQL.append("\n        direccion d, cat_cta_banco ccb, empresa e" );           //'se agregó la tabla movimiento
	            sbSQL.append("\n   WHERE m.id_estatus_mov in ('I','R') ");
	            
	            if(dtoCheques.getCriterioSolicitudIni() != 0 || dtoCheques.getCriterioSolicitudFin()!= 0)
	            {
	            	if(dtoCheques.getCriterioSolicitudFin() != 0)
	            	{
	                    sbSQL.append("\n    AND (m.no_cheque >= " + dtoCheques.getCriterioSolicitudIni());
	                    sbSQL.append("\n    AND m.no_cheque <= " + dtoCheques.getCriterioSolicitudFin() + ")");
	            	}
	                else
	                    sbSQL.append("\n    AND m.no_cheque = " + dtoCheques.getCriterioSolicitudIni());
	            }
	            
	            sbSQL.append("\n   AND m.id_banco = " + dtoCheques.getCriterioBanco());
	            sbSQL.append("\n   AND m.no_empresa = ccb.no_empresa");
	            sbSQL.append("\n   AND m.id_banco = ccb.id_banco");
	            sbSQL.append("\n   AND m.id_chequera = ccb.id_chequera");
	            sbSQL.append("\n   AND m.id_banco = cb.id_banco");
	            
	            sbSQL.append("\n   AND m.no_empresa = p.no_empresa");
	            sbSQL.append("\n   AND m.no_empresa = p.no_persona");
	            sbSQL.append("\n   AND p.id_tipo_persona = 'E'");
	            
	            sbSQL.append("\n   AND m.no_empresa = e.no_empresa ");
	            
	            sbSQL.append("\n   AND m.no_empresa *= d.no_empresa");
	            sbSQL.append("\n   AND m.no_empresa *= d.no_persona");
	            sbSQL.append("\n   AND d.id_tipo_persona = 'E'");
	            
	            sbSQL.append("\n   AND pp.no_empresa = 552");
	            sbSQL.append("\n   AND m.no_cliente = pp.no_persona");
	            sbSQL.append("\n   AND pp.id_tipo_persona = 'P'");
	            
	            sbSQL.append("\n   AND m.id_caja *= cc.id_caja ");
	            sbSQL.append("\n   AND d.id_tipo_direccion = 'OFNA' ");
	            
	            if(!dtoCheques.getCriterioChequera().equals(""))
	                sbSQL.append("\n   AND ccb.id_chequera = '" + dtoCheques.getCriterioChequera() + "'");
	                  
	            sbSQL.append("\n   AND m.no_empresa in (Select no_empresa from usuario_empresa ");
	            sbSQL.append("\n                         where no_usuario = " + dtoCheques.getIdUsuario() + ") ");
	            sbSQL.append("\n   AND m.id_caja = " + dtoCheques.getIdCaja());
	            
	            if(dtoCheques.getCriterioEmpresa() != 0)
	                sbSQL.append("\n   AND m.no_empresa = " + dtoCheques.getCriterioEmpresa());
	            
	            if(!dtoCheques.getCriterioNoDocto().equals(""))
	                sbSQL.append("\n   AND m.no_docto = '" + dtoCheques.getCriterioNoDocto() + "'");
	            
	            if(dtoCheques.getChequeNomina() == 1) 			//Se agrega if para determinar si el usuario puede ver los cheques de nómina o gastos
	            	sbSQL.append("\n   AND m.id_area = 1" );	//'Visualiza sólo cheques de nómina
                else if(dtoCheques.getChequeNomina() == 2)
                	sbSQL.append("\n   AND m.id_area = 2"); 	//'Visualiza sólo cheques de gastos
                else if(dtoCheques.getChequeNomina() == 0)
	            	sbSQL.append("\n   AND m.id_area IS NOT NULL");	//'Visualiza sólo cheques donde id_area IS NULL
	            
			}
			if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
			{
	            sbSQL.append("\n  SELECT desc_caja,m.no_empresa,");
	            sbSQL.append("\n         m.no_folio_det as no_solicitud,");
	            sbSQL.append("\n         m.importe,");
	            sbSQL.append("\n         m.beneficiario,");
	            sbSQL.append("\n         cb.desc_banco,");
	            sbSQL.append("\n         m.id_chequera  as cta_alias,");
	            sbSQL.append("\n         cc.desc_caja as cja_ubic,");
	            sbSQL.append("\n         m.concepto,");
	            sbSQL.append("\n         p.razon_social as cia_nmbr,");
	            sbSQL.append("\n         m.agrupa1 as centro_costo,");
	            sbSQL.append("\n         m.id_banco as bco_cve,");
	            sbSQL.append("\n         m.id_tipo_movto as tipo_egreso,");
	            sbSQL.append("\n         m.importe_original as importe_original,");
	            sbSQL.append("\n         m.no_docto as no_contrarecibo,");
	            sbSQL.append("\n         pp.rfc,");
	            sbSQL.append("\n         m.id_divisa as moneda,");
	            sbSQL.append("\n         m.id_contable as id_contable,");
	            sbSQL.append("\n         m.id_chequera as cta_no,");
	            sbSQL.append("\n         m.id_leyenda as leyenda_prote,");
	            sbSQL.append("\n         ccb.desc_plaza as plaza,");
	            sbSQL.append("\n         ccb.desc_sucursal as sucursal,");
	            sbSQL.append("\n         cb.b_protegido,");
	            sbSQL.append("\n         m.id_chequera as no_cuenta,");
	            sbSQL.append("\n         cb.codigo_seguridad,");
	            sbSQL.append("\n         cb.cve_plaza as cve_plaza,");
	            sbSQL.append("\n         cb.id_banco as no_banco,");
	            sbSQL.append("\n         d.calle_no || ' ' || d.colonia as cia_dir,");
	            sbSQL.append("\n         d.deleg_municipio || ' ' || d.ciudad as cia_dir2,");
	            sbSQL.append("\n         p.rfc as cia_rfc,");
	            sbSQL.append("\n         m.no_cheque as no_cheque,");
	            sbSQL.append("\n         m.firmante1, ");
	            sbSQL.append("\n         m.firmante2,");
	            sbSQL.append("\n         m.fec_valor as fec_cheque, ");
	            sbSQL.append("\n         pp.equivale_persona as equivale_benef,");
	            sbSQL.append("\n         m.id_grupo,");
	            sbSQL.append("\n         m.id_rubro,");
	            sbSQL.append("\n         m.no_pedido,");
	            sbSQL.append("\n         p.equivale_persona as equivale_empresa,");
	            sbSQL.append("\n         m.id_divisa_original,");
	            sbSQL.append("\n         m.division,");
	            sbSQL.append("\n         d.id_cp,");
	            sbSQL.append("\n         cb.dir_logo as logo_banco,");
	            sbSQL.append("\n         e.dir_logo as logo_empresa,");
	            sbSQL.append("\n         m.tipo_cambio,");
	            sbSQL.append("\n         e.dir_logo_alterno");
	            sbSQL.append("\n    FROM movimiento m LEFT JOIN cat_banco cb ON (m.id_banco = cb.id_banco)");
	            sbSQL.append("\n         LEFT JOIN cat_caja cc ON (m.id_caja = cc.id_caja)");
	            sbSQL.append("\n         LEFT JOIN persona p ON (m.no_empresa = p.no_empresa AND m.no_empresa = p.no_persona ");
	            sbSQL.append("\n                                 AND p.id_tipo_persona = 'E')");
	            sbSQL.append("\n         LEFT JOIN persona pp ON (m.no_cliente = pp.no_persona AND pp.no_empresa = 552 ");
	            sbSQL.append("\n                                  AND pp.id_tipo_persona = 'P')");
	            sbSQL.append("\n         LEFT JOIN direccion d ON (m.no_empresa = d.no_empresa AND m.no_empresa = d.no_persona ");
	            sbSQL.append("\n                                   AND d.id_tipo_persona = 'E' AND d.id_tipo_direccion='OFNA')");
	            sbSQL.append("\n         LEFT JOIN cat_cta_banco ccb ON (m.id_banco = ccb.id_banco ");
	            sbSQL.append("\n                                         AND m.id_chequera = ccb.id_chequera");
	            sbSQL.append("\n  ), ");
	            sbSQL.append("\n         empresa e ");
	            sbSQL.append("\n   WHERE m.id_estatus_mov in ('I','R') ");
	            
	            if(dtoCheques.getCriterioSolicitudIni() != 0 || dtoCheques.getCriterioSolicitudFin() != 0)
	            {
	            	if(dtoCheques.getCriterioSolicitudFin() != 0)
	                    sbSQL.append("\n  AND m.no_cheque between " + dtoCheques.getCriterioSolicitudIni() + " and " + dtoCheques.getCriterioSolicitudFin());
	                else
	                    sbSQL.append("\n  AND m.no_cheque = " + dtoCheques.getCriterioSolicitudIni());
	            }
	            
	            sbSQL.append("\n   AND m.id_banco = " + dtoCheques.getCriterioBanco());
	            sbSQL.append("\n   AND m.id_caja in (select id_caja from caja_usuario ");
	            sbSQL.append("\n                      where no_usuario = " + dtoCheques.getIdUsuario() + ") ");
	            sbSQL.append("\n   AND m.id_tipo_operacion = 3200 ");
	            sbSQL.append("\n   AND m.no_empresa = e.no_empresa ");
	            sbSQL.append("\n   AND m.id_forma_pago in (1,7)");
	            
	            if(!dtoCheques.getCriterioChequera().equals(""))
	                sbSQL.append("\n   AND ccb.id_chequera = '" + dtoCheques.getCriterioChequera() + "'");
	            
	            sbSQL.append("\n   AND m.no_empresa in (select no_empresa from usuario_empresa ");
	            sbSQL.append("\n                         where no_usuario = " + dtoCheques.getIdUsuario() + ") ");
	            
	            if(dtoCheques.getCriterioEmpresa() != 0)
	                sbSQL.append("\n   AND m.no_empresa = " + dtoCheques.getCriterioEmpresa());
	            
	            if(!dtoCheques.getCriterioNoDocto().equals(""))
	                sbSQL.append("\n   AND m.no_docto = '" + dtoCheques.getCriterioNoDocto() + "'");
	            
	            //se agrega union a hist_movimiento para poder reimprimir cheques del historico
	            
	            sbSQL.append("\n union ");
	            sbSQL.append("\n  SELECT desc_caja,m.no_empresa,");
	            sbSQL.append("\n         m.no_folio_det as no_solicitud,");
	            sbSQL.append("\n         m.importe,");
	            sbSQL.append("\n         m.beneficiario,");
	            sbSQL.append("\n         cb.desc_banco,");
	            sbSQL.append("\n         m.id_chequera  as cta_alias,");
	            sbSQL.append("\n         cc.desc_caja as cja_ubic,");
	            sbSQL.append("\n         m.concepto,");
	            sbSQL.append("\n         p.razon_social as cia_nmbr,");
	            sbSQL.append("\n         m.agrupa1 as centro_costo,");
	            sbSQL.append("\n         m.id_banco as bco_cve,");
	            sbSQL.append("\n         m.id_tipo_movto as tipo_egreso,");
	            sbSQL.append("\n         m.importe_original as importe_original,");
	            sbSQL.append("\n         m.no_docto as no_contrarecibo,");
	            sbSQL.append("\n         pp.rfc,");
	            sbSQL.append("\n         m.id_divisa as moneda,");
	            sbSQL.append("\n         m.id_contable as id_contable,");
	            sbSQL.append("\n         m.id_chequera as cta_no,");
	            sbSQL.append("\n         m.id_leyenda as leyenda_prote,");
	            sbSQL.append("\n         ccb.desc_plaza as plaza,");
	            sbSQL.append("\n         ccb.desc_sucursal as sucursal,");
	            sbSQL.append("\n         cb.b_protegido,");
	            sbSQL.append("\n         m.id_chequera as no_cuenta,");
	            sbSQL.append("\n         cb.codigo_seguridad,");
	            sbSQL.append("\n         cb.cve_plaza as cve_plaza,");
	            sbSQL.append("\n         cb.id_banco as no_banco,");
	            sbSQL.append("\n         d.calle_no || ' ' || d.colonia as cia_dir,");
	            sbSQL.append("\n         d.deleg_municipio || ' ' || d.ciudad as cia_dir2,");
	            sbSQL.append("\n         p.rfc as cia_rfc,");
	            sbSQL.append("\n         m.no_cheque as no_cheque,");
	            sbSQL.append("\n         m.firmante1, ");
	            sbSQL.append("\n         m.firmante2,");
	            sbSQL.append("\n         m.fec_valor as fec_cheque, ");
	            sbSQL.append("\n         pp.equivale_persona as equivale_benef,");
	            sbSQL.append("\n         m.id_grupo,");
	            sbSQL.append("\n         m.id_rubro,");
	            sbSQL.append("\n         m.no_pedido,");
	            sbSQL.append("\n         p.equivale_persona as equivale_empresa,");
	            sbSQL.append("\n         m.id_divisa_original,");
	            sbSQL.append("\n         m.division,");
	            sbSQL.append("\n         d.id_cp,");
	            sbSQL.append("\n         cb.dir_logo as logo_banco,");
	            sbSQL.append("\n         e.dir_logo as logo_empresa,");
	            sbSQL.append("\n         m.tipo_cambio,");
	            sbSQL.append("\n         e.dir_logo_alterno");
	            sbSQL.append("\n    FROM hist_movimiento m LEFT JOIN cat_banco cb ON (m.id_banco = cb.id_banco)");
	            sbSQL.append("\n         LEFT JOIN cat_caja cc ON (m.id_caja = cc.id_caja)");
	            sbSQL.append("\n         LEFT JOIN persona p ON (m.no_empresa = p.no_empresa AND m.no_empresa = p.no_persona ");
	            sbSQL.append("\n                                 AND p.id_tipo_persona = 'E')");
	            sbSQL.append("\n         LEFT JOIN persona pp ON (m.no_cliente = pp.no_persona AND pp.no_empresa = 552 ");
	            sbSQL.append("\n                                  AND pp.id_tipo_persona = 'P')");
	            sbSQL.append("\n         LEFT JOIN direccion d ON (m.no_empresa = d.no_empresa AND m.no_empresa = d.no_persona ");
	            sbSQL.append("\n                                   AND d.id_tipo_persona = 'E' AND d.id_tipo_direccion='OFNA')");
	            sbSQL.append("\n         LEFT JOIN cat_cta_banco ccb ON (m.id_banco = ccb.id_banco ");
	            sbSQL.append("\n                                         AND m.id_chequera = ccb.id_chequera");
	            sbSQL.append("\n  ), ");
	            sbSQL.append("\n         empresa e ");
	            sbSQL.append("\n   WHERE m.id_estatus_mov in ('I','R') ");
	            
	            if(dtoCheques.getCriterioSolicitudIni() != 0 || dtoCheques.getCriterioSolicitudFin() != 0)
	            {
	            	if(dtoCheques.getCriterioSolicitudFin() != 0)
	                    sbSQL.append("\n  AND m.no_cheque between " + dtoCheques.getCriterioSolicitudIni() + " and " + dtoCheques.getCriterioSolicitudFin());
	                else
	                    sbSQL.append("\n  AND m.no_cheque = " + dtoCheques.getCriterioSolicitudIni());
	            }
	            
	            sbSQL.append("\n   AND m.id_banco = " + dtoCheques.getCriterioBanco());
	            sbSQL.append("\n   AND m.id_caja in (select id_caja from caja_usuario ");
	            sbSQL.append("\n                      where no_usuario = " + dtoCheques.getIdUsuario() + ") ");
	            sbSQL.append("\n   AND m.id_tipo_operacion = 3200 ");
	            sbSQL.append("\n   AND m.no_empresa = e.no_empresa ");
	            sbSQL.append("\n   AND m.id_forma_pago in (1,7)");
	            
	            if(!dtoCheques.getCriterioChequera().equals(""))
	                sbSQL.append("\n   AND ccb.id_chequera = '" + dtoCheques.getCriterioChequera() + "'");
	            
	            sbSQL.append("\n   AND m.no_empresa in (select no_empresa from usuario_empresa ");
	            sbSQL.append("\n                         where no_usuario = " + dtoCheques.getIdUsuario() + ") ");
	            
	            if(dtoCheques.getCriterioEmpresa() != 0)
	                sbSQL.append("\n   AND m.no_empresa = " + dtoCheques.getCriterioEmpresa());
	            
	            if(!dtoCheques.getCriterioNoDocto().equals(""))
	                sbSQL.append("\n   AND m.no_docto = '" + dtoCheques.getCriterioNoDocto() + "'");
			}
			*/
			
			//logger.info("consulta: " + sbSQL.toString());
			System.out.println("consulta: " + sbSQL.toString());
			
			listRetorno = jdbcTemplate.query(sbSQL.toString(), new RowMapper<MovimientoDto>(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto movimiento = new MovimientoDto();
					movimiento.setDescCaja(rs.getString("desc_caja"));
					movimiento.setNoEmpresa(rs.getInt("no_empresa"));
					movimiento.setNoSolicitud(rs.getInt("no_solicitud"));
					movimiento.setImporte(rs.getDouble("importe"));
					movimiento.setBeneficiario(rs.getString("beneficiario"));
					movimiento.setDescBanco(rs.getString("desc_banco"));
					movimiento.setCtaAlias(rs.getString("cta_alias"));
					movimiento.setCjaUbic(rs.getString("cja_ubic"));
					movimiento.setConcepto(rs.getString("concepto"));
					movimiento.setCiaNmbr(rs.getString("cia_nmbr"));
					movimiento.setCentroCosto(rs.getInt("centro_costo"));
					movimiento.setBcoCve(rs.getInt("bco_cve"));
					movimiento.setTipoEgreso(rs.getString("tipo_egreso"));
					movimiento.setImporteOriginal(rs.getDouble("importe_original"));
					movimiento.setNoContrarecibo(rs.getString("no_contrarecibo"));
					movimiento.setRfc(rs.getString("rfc"));
					movimiento.setMoneda(rs.getString("moneda"));
					movimiento.setIdContable(rs.getString("id_contable"));
					movimiento.setCtaNo(rs.getString("cta_no"));
					movimiento.setLeyendaProte(rs.getString("leyenda_prote"));
					movimiento.setPlaza(rs.getInt("plaza"));
					movimiento.setSucursal(rs.getInt("sucursal"));
					movimiento.setBProtegido(rs.getString("b_protegido"));
					movimiento.setNoCuentaS(rs.getString("no_cuenta"));
					movimiento.setCodigoSeguridad(rs.getString("codigo_seguridad"));
					movimiento.setCvePlaza(rs.getString("cve_plaza"));
					movimiento.setIdBanco(rs.getInt("no_banco"));
					
					//movimiento.setCiaDir(rs.getString("cia_dir"));
					//movimiento.setCiaDir2(rs.getString("cia_dir2"));
					movimiento.setCiaRfc(rs.getString("cia_rfc"));
					movimiento.setFirmante1(rs.getString("firmante1"));
					movimiento.setFirmante2(rs.getString("firmante2"));
					movimiento.setNoCheque(rs.getInt("no_cheque"));
					movimiento.setFecCheque(rs.getDate("fec_cheque"));
					movimiento.setEquivaleBenef(rs.getString("equivale_benef"));
					movimiento.setIdGrupo(rs.getInt("id_grupo"));
					movimiento.setIdRubroStr(rs.getString("id_rubro"));
					movimiento.setNoPedido(rs.getInt("no_pedido"));
					
					movimiento.setEquivaleEmpresa(rs.getInt("equivale_empresa"));
					movimiento.setIdDivisaOriginal(rs.getString("id_divisa_original"));
					movimiento.setTipoCambio(rs.getDouble("tipo_cambio"));
					movimiento.setDivision(rs.getString("division"));
					//movimiento.setIdCp(rs.getString("id_cp"));
					
					movimiento.setLogoEmpresa(rs.getString("logo_empresa"));
					movimiento.setLogoBanco(rs.getString("logo_banco"));
					movimiento.setDirLogoAlterno(rs.getString("dir_logo_alterno"));
					//movimiento.setIndIva(rs.getString("ind_iva"));
					
					return movimiento;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:consultarSolicitudesReimpresion");
			System.err.println(e);
		}
		return listRetorno;
	}
	
	
	public List<MovimientoDto>consultarEntregaCheque(String folio){
		List<MovimientoDto> list = new ArrayList<MovimientoDto>();
		StringBuffer sbSQL = new StringBuffer();
		try{
			sbSQL.append("\n SELECT no_folio_det ");
		    sbSQL.append("\n   FROM movimiento ");
		    sbSQL.append("\n  WHERE b_entregado = 'S' ");
		    sbSQL.append("\n    AND no_folio_det = " + folio);
		    
		    list = jdbcTemplate.query(sbSQL.toString(), new RowMapper<MovimientoDto>(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto movimiento = new MovimientoDto();
					movimiento.setNoFolioDet(rs.getInt("no_folio_det"));
					return movimiento;
				}
		    });
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ImpresionDaoImpl, M:consultarEntregaCheque");
		}
		return list;
	}
	
	/**
	 * Devuelve la cuenta contable y la descripcion del rubro asociado a 
	 * la empresa, el grupo y rubro deseado.
	 * @param idEmpresa
	 * @param idGrupo
	 * @param idRubro
	 * @return
	 */
	//Modificado para Oracle EMS: 29/12/2015
	/*public List<GuiaContableDto> consultarCtaContableRubro(int noFolioDet) {
		List<GuiaContableDto> list = new ArrayList<GuiaContableDto>();
		StringBuffer sbSQL = new StringBuffer();
		
		try {
			//sbSQL.append("SELECT substring(cta_cargo, 1, 2) + '-' + substring(cta_cargo, 3, 3) + '-' + substring(cta_cargo, 6, 2) + '-' + substring(cta_cargo, 8, 2) as cta_cargo, cr.id_rubro, cr.desc_rubro \n");
			sbSQL.append("SELECT substr(cta_cargo, 1, 2) || '-' || substr(cta_cargo, 3, 3) || '-' || substr(cta_cargo, 6, 2) || '-' || substr(cta_cargo, 8, 2) as cta_cargo, cr.id_rubro, cr.desc_rubro \n");
			sbSQL.append("FROM config_contable cc, cat_rubro cr \n");
			sbSQL.append("WHERE cc.id_rubro = cr.id_rubro \n");
			sbSQL.append("	And cc.id_grupo = cr.id_grupo \n");
			sbSQL.append("	And (no_folio_det = "+ noFolioDet +" or no_folio_det_pag = "+ noFolioDet +") \n");
						
			System.out.println("Consulta cuenta contable rubro: " + sbSQL.toString());
			
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper<GuiaContableDto>(){
				public GuiaContableDto mapRow(ResultSet rs, int idx) throws SQLException {
					GuiaContableDto guia = new GuiaContableDto();
					guia.setCuentaContable(rs.getString("cta_cargo"));
					guia.setNoFolioDet(rs.getString("desc_rubro"));
					return guia;
				}
		    });
		}
		catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Impresion, C:ImpresionDaoImpl, M:consultarCtaContableRubro");
			System.err.println("Ocurrio un error: " + e);
		}
		return list;
	}
	*/
	
	public List<GuiaContableDto> consultarCtaContableRubroAgrupado(int noCheque) {
		List<GuiaContableDto> list = new ArrayList<GuiaContableDto>();
		StringBuffer sbSQL = new StringBuffer();
		
		try {
			sbSQL.append("select no_folio_det from movimiento \n");
			sbSQL.append("where not grupo_pago is null and grupo_pago <> 0 \n");
			sbSQL.append("	and (id_tipo_operacion = 3201) \n");
			sbSQL.append("	and no_cheque = "+ noCheque + " \n");
						
			
			
			List<Integer> folios = jdbcTemplate.query(sbSQL.toString(), new RowMapper<Integer>(){
				public Integer mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getInt("no_folio_det");
				}
		    });
			
			String sFolios = "";
			for (Integer integer : folios) {
				sFolios += integer + ",";
			}
			sFolios = sFolios.substring(0, sFolios.length()-1);
			
			sbSQL = new StringBuffer();
			sbSQL.append("SELECT substring(cta_cargo, 1, 2) + ");
			sbSQL.append("'-' + substring(cta_cargo, 3, 3) + '-' + ");
			sbSQL.append("substring(cta_cargo, 6, 2) + '-' + ");
			sbSQL.append("substring(cta_cargo, 8, 2) as cta_cargo, ");
			sbSQL.append("cr.id_rubro, cr.desc_rubro, m.importe \n");
			sbSQL.append("FROM config_contable cc, cat_rubro cr, movimiento m \n");
			sbSQL.append("WHERE cc.id_rubro = cr.id_rubro \n");
			sbSQL.append("	and (cc.no_folio_det = m.no_folio_det \n");
			sbSQL.append("		or cc.no_folio_det_pag = m.no_folio_det)");
			sbSQL.append("	And cc.id_grupo = cr.id_grupo \n");
			sbSQL.append("	And (cc.no_folio_det in \n");
			sbSQL.append("("+ sFolios +") \n");
			sbSQL.append("or no_folio_det_pag in \n");
			sbSQL.append("("+ sFolios +")) \n");
			
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper<GuiaContableDto>(){
				public GuiaContableDto mapRow(ResultSet rs, int idx) throws SQLException {
					GuiaContableDto guia = new GuiaContableDto();
					guia.setCuentaContable(rs.getString("cta_cargo"));
					guia.setNoFolioDet(rs.getString("desc_rubro")+"\n"+
							rs.getString("importe"));
					return guia;
				}
		    });		
			
		}
		catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Impresion, C:ImpresionDaoImpl, M:consultarCtaContableRubro");
			System.err.println("Ocurrio un error: " + e);
		}
		return list;
	}
	public boolean esAgrupado(int noCheque) {
		StringBuffer sbSQL = new StringBuffer();
		
		try {
			sbSQL.append("select COUNT(*) from movimiento \n");
			sbSQL.append("where not grupo_pago is null and grupo_pago <> 0 \n");
			sbSQL.append("	and (id_tipo_operacion = 3200 or id_tipo_operacion = 3201) \n");
			sbSQL.append("	and no_cheque = "+ noCheque + " \n");
						
			System.out.println("Consulta si es agrupado: " + sbSQL.toString());
			
			int count = jdbcTemplate.queryForInt(sbSQL.toString());
			
			return count > 0;
		}
		catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Impresion, C:ImpresionDaoImpl, M:consultarCtaContableRubro");
			System.err.println("Ocurrio un error: " + e);
		}
		return false;
	}
	
	/**
	 * Recupera la cuenta contable asociada a la chequera y empresa enviadas.
	 * 
	 * @param idEmpresa
	 * @param chequera
	 * @return
	 */
	public List<CatEquivaleCuentaContableDto> consultarEquivalenciaCtaContable(int idEmpresa, String chequera)
	{
		List<CatEquivaleCuentaContableDto> list = new ArrayList<CatEquivaleCuentaContableDto>();
		StringBuffer sbSQL = new StringBuffer();
		try
		{
			sbSQL.append("SELECT * FROM cat_equivalencia_cta_contable ");
			sbSQL.append("WHERE no_empresa =  " + idEmpresa);
			sbSQL.append(" AND id_Chequera = '"+Utilerias.validarCadenaSQL(chequera)+"' ");
			
			System.out.println("Consulta equivalencia cuenta contable: " + sbSQL.toString());
			
			list = jdbcTemplate.query(sbSQL.toString(), new RowMapper<CatEquivaleCuentaContableDto>(){
				public CatEquivaleCuentaContableDto mapRow(ResultSet rs, int idx) throws SQLException {
					CatEquivaleCuentaContableDto equivale = new CatEquivaleCuentaContableDto();
					equivale.setIdEquivale(rs.getInt("id"));
					equivale.setNoEmpresa(rs.getString("no_empresa"));
					equivale.setIdBanco(rs.getString("id_banco"));
					equivale.setIdChequera(rs.getString("id_Chequera"));
					equivale.setLibroMayor(rs.getString("libro_mayor"));
					equivale.setCuentaBancaria(rs.getString("cuenta_bancaria"));
					equivale.setDepto(rs.getString("depto"));
					equivale.setIdCuenta(rs.getString("id_cuenta"));
					equivale.setCargoAbono(rs.getString("cargo_abono"));
					equivale.setIdConcepto(rs.getInt("idConcepto"));
					return equivale;
				}
		    });
		}
		catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Impresion, C:ImpresionDaoImpl, M:consultarEquivalenciaCtaContable");
			System.err.println("Ocurrio un error: " + e);
		}
		return list;
	}
	
	/**
	 * FunSQLUpdate185
	 * @param fechaCheque
	 * @param fechaReimprime
	 * @param noCheque
	 * @param folioDet
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int actualizarMovimientoReimpresion(Date fechaCheque, Date fechaReimprime, int noCheque, int folioDet){
		int updateMov = -1;
		StringBuffer sbSQL = new StringBuffer();
		try
		{
			/*Public Function FunSQLUpdate185(ByVal pvvValor1 As Variant, ByVal pvvValor2 As Variant, _
                                ByVal pvvValor3 As Variant, ByVal pvvValor4 As Long) As Long*/

			sbSQL.append("\n UPDATE movimiento ");
		    sbSQL.append("\n    SET fec_cheque = '" + Utilerias.validarCadenaSQL(funciones.ponerFechaSola(fechaCheque)) + "'");
		    sbSQL.append("\n        ,fec_reimprime = '" + Utilerias.validarCadenaSQL(funciones.ponerFechaSola(fechaReimprime)) + "'");
		    sbSQL.append("\n        ,id_estatus_mov = 'R'");
		    sbSQL.append("\n        ,b_arch_seg = NULL");
		    sbSQL.append("\n        ,arch_protegido = NULL");
		  
		    if(noCheque != 0)
		        sbSQL.append("\n        ,no_cheque = " + noCheque);
		   
		    sbSQL.append("\n  WHERE no_folio_det = " + folioDet);
		    
		    if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
		    sbSQL.append("\n ;");

		    sbSQL.append("\n  UPDATE movimiento ");
		    sbSQL.append("\n    SET fec_cheque = '" + Utilerias.validarCadenaSQL(funciones.ponerFechaSola(fechaCheque))  + "'");
		    sbSQL.append("\n        ,fec_reimprime = '" + Utilerias.validarCadenaSQL(funciones.ponerFechaSola(fechaReimprime)) + "'");
		    
		    if(noCheque != 0)
		        sbSQL.append("\n        ,no_cheque = " + noCheque);
		    
		    sbSQL.append("\n  WHERE grupo_pago = " + folioDet);
		    sbSQL.append("\n  and id_tipo_operacion = 3201 and id_estatus_mov = 'H'");
		    
			logger.info("actualizarMovimiento: ");
			updateMov = jdbcTemplate.update(sbSQL.toString());
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ImpresionDaoImpl, M:actualizarMovimiento");
		}
		return updateMov;
	}
	
	
	public List<LayoutProteccionDto> obtieneFechaHoy() 
	{
		List<LayoutProteccionDto> lista = new ArrayList<LayoutProteccionDto>();		
		String sql = "";		
		try
		{
			sql += "Select * from fechas ";
									
			lista = jdbcTemplate.query(sql, new RowMapper<LayoutProteccionDto>() 
			{
				public LayoutProteccionDto mapRow(ResultSet rs, int idx) throws SQLException 
				{
					LayoutProteccionDto campos = new LayoutProteccionDto();					
					campos.setFecHoy(rs.getDate("fec_hoy"));										
					return campos;			
				}
			});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Layout, C:LayoutsDao, M:obtieneFechaHoy");
		}
		return lista;
	}
	
	public int seleccionarFolioReal(String tipoFolio)
	{
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.seleccionarFolioReal(tipoFolio);
	}
	
	public int actualizarFolioReal(String tipoFolio)
	{
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.actualizarFolioReal(tipoFolio);
	}
	
	public String consultarConfiguraSet(int indice){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
	}
	
	@Override
	public int obtieneUltimoCheqImp(String chequera) {
		StringBuffer sql = new StringBuffer();
		int noCheque = 0;
		
		try {
			sql.append(" SELECT coalesce(ult_cheq_impreso, 0)+1 as ult_cheq_impreso \n");
			sql.append(" FROM  cat_cta_banco \n");
			sql.append(" WHERE id_chequera = '"+ Utilerias.validarCadenaSQL(chequera) +"' \n");
			
			noCheque = jdbcTemplate.queryForInt(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ImpresionDaoImpl, M:obtieneUltimoCheqImp");
		}
		return noCheque;
	}
	
	public int getUltimoImpCtrlCheque(String noEmpresa, String noBanco, String chequera) {
		StringBuffer sql = new StringBuffer();
		int noCheque = 0;
		int existe = 0;
		
		try {
			
			//Si es igual a 1 verificamos que si exista en control_papel, de lo contrario retornar -1 
			sql.append(" SELECT count(folio_ult_impreso) as existe \n");
			sql.append(" FROM  CONTROL_PAPEL \n");
			sql.append(" WHERE id_chequera = '"+ Utilerias.validarCadenaSQL(chequera) +"' \n");
			sql.append(" AND NO_EMPRESA = '"+noEmpresa+"' \n");
			sql.append(" AND bco_cve = '"+noBanco+"' \n");
			sql.append(" AND estatus = 'A' \n");
			sql.append(" AND tipo_folio = 'C' \n");
			
			existe = jdbcTemplate.queryForInt(sql.toString());
			
			if(existe==0){
				return (noCheque=-1);
			}
			
			if(sql.length() >0)
				sql.delete(0, sql.length());
			
			sql.append(" SELECT coalesce(folio_ult_impreso, 0)+1 as ult_cheq_impreso \n");
			sql.append(" FROM  CONTROL_PAPEL \n");
			sql.append(" WHERE id_chequera = '"+ Utilerias.validarCadenaSQL(chequera) +"' \n");
			sql.append(" AND NO_EMPRESA = '"+noEmpresa+"' \n");
			sql.append(" AND bco_cve = '"+noBanco+"' \n");
			sql.append(" AND estatus = 'A' \n");
			sql.append(" AND tipo_folio = 'C' \n");
			
			noCheque = jdbcTemplate.queryForInt(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ImpresionDaoImpl, M:getUltimoImpCtrlCheque");
		}
		return noCheque;
	}
	
	
	public int actualizaCheque(int noCheque, String sCuenta) {
		StringBuffer sql = new StringBuffer();
		int resp = 0;
		
		try {
			sql.append(" UPDATE cat_cta_banco SET ult_cheq_impreso  = "+ noCheque +" \n");
			sql.append(" WHERE id_chequera = '"+ Utilerias.validarCadenaSQL(sCuenta) +"' \n");
			
			resp = jdbcTemplate.update(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ImpresionDaoImpl, M:obtieneUltimoCheqImp");
		}
		return resp;
	}
	
	
	public String obtieneNomCarpeta(int noEmpresa) {
		List<CatEquivaleCuentaContableDto> list = new ArrayList<CatEquivaleCuentaContableDto>();
		StringBuffer sql = new StringBuffer();
		String resp = "";
		
		try {
			sql.append(" SELECT desc_grupo_flujo FROM cat_grupo_flujo \n");
			sql.append(" WHERE id_grupo_flujo = "+ noEmpresa +" \n");
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper<CatEquivaleCuentaContableDto>(){
				public CatEquivaleCuentaContableDto mapRow(ResultSet rs, int idx) throws SQLException {
					CatEquivaleCuentaContableDto equivale = new CatEquivaleCuentaContableDto();
					equivale.setNoEmpresa(rs.getString("desc_grupo_flujo"));
					return equivale;
				}
		    });
			
			if(list.size() > 0) resp = list.get(0).getNoEmpresa();
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ImpresionDaoImpl, M:obtieneUltimoCheqImp");
		}
		return resp;
	}
	
	//Metodo agregado EMS 15/12/2015
		public List<LlenaComboGralDto>llenarComboBeneficiario(LlenaComboGralDto dto){
			
			String cond="";
			cond=dto.getCondicion();
			dto.setCampoDos("rTRIM(COALESCE(razon_social,'')) ");
			
			cond = Utilerias.validarCadenaSQL(cond);
			
			if(dto.isRegistroUnico()){
				dto.setCondicion("no_persona="+cond);
			}else{
				dto.setCondicion("id_tipo_persona='P'	"
						+"	AND no_empresa in(552,217)"
						+"	AND ((razon_social like '"+cond+"%'"     
						+"	or paterno like '"+cond+"%'" 
						+"	or materno like '"+cond+"%'"   
						+"	or nombre like '"+cond+"%' )" 
						+"	or (no_persona like '"+cond+"%'))");	
			}
			
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			return consultasGenerales.llenarComboGral(dto);
		}
		
		//Agregado EMS 16/12/2015
		@Override
		public List<LlenaComboGralDto> llenarComboLeyenda() {
			StringBuffer sql = new StringBuffer();
			List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
			
			try {
				consultasGenerales = new ConsultasGenerales(jdbcTemplate);
				
				sql.append(" SELECT distinct cl.id_leyenda as ID, cl.desc_leyenda as descrip ");
				sql.append("\n FROM cat_leyenda cl ");
				
				list = jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>() {
					public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
						LlenaComboGralDto con = new LlenaComboGralDto();
						con.setId(rs.getInt("ID"));
						con.setDescripcion(rs.getString("descrip"));
						return con;
					}
				});
			}catch(CannotGetJdbcConnectionException e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C:ImpresionDaoImpl, M:llenarComboLeyenda");
			}catch(Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C:ImpresionDaoImpl, M:llenarComboLeyenda");
			}
			
			return list;
		}
		
		//Agregado YEC 28.01.16
		public List<LlenaComboGralDto> llenarComboConfiguracion() {
			StringBuffer sql = new StringBuffer();
			List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
			try {
				consultasGenerales = new ConsultasGenerales(jdbcTemplate);
				
				sql.append("\n SELECT distinct ch.ID_CONF as ID, ch.ID_CONF as descrip ");
				sql.append("\n FROM CAT_CHEQUE ch JOIN CAT_BANCO b ON ch.ID_BANCO = b.ID_BANCO ");
				sql.append("\n JOIN CAT_DIVISA d ON ch.ID_DIVISA = d.ID_DIVISA JOIN EMPRESA e ON ch. NO_EMPRESA = e.NO_EMPRESA ");
				sql.append("\n ORDER BY ch.ID_CONF ");
				
				System.out.println("llenarComboConfiguracion: "+ sql.toString());
				
				list = jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>() {
					public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
						LlenaComboGralDto con = new LlenaComboGralDto();
						con.setId(rs.getInt("ID"));
						con.setDescripcion(rs.getString("descrip"));
						return con;
					}
				});
			}catch(CannotGetJdbcConnectionException e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C:ImpresionDaoImpl, M:llenarComboConfiguracion");
			}catch(Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C:ImpresionDaoImpl, M:llenarComboConfiguracion");
			}
			
			return list;
		}
		
		
		public List<GuiaContableDto> obtenerCtaContable(String idChequera) {
			List<GuiaContableDto> list = new ArrayList<GuiaContableDto>();
			StringBuffer sql = new StringBuffer();
			
			try {
				sql.append("\n SELECT libro_mayor ");
				sql.append("\n FROM cat_ctas_contables_erp ");
				sql.append("\n WHERE id_chequera = '"+Utilerias.validarCadenaSQL(idChequera)+"' ");
				sql.append("\n AND cargo_abono = 'E' ");
				
				System.out.println("Consulta cuenta contable: " + sql.toString());
				
				list = jdbcTemplate.query(sql.toString(), new RowMapper<GuiaContableDto>(){
					public GuiaContableDto mapRow(ResultSet rs, int idx) throws SQLException {
						GuiaContableDto guia = new GuiaContableDto();
						guia.setCuentaContable(rs.getString("libro_mayor"));
						return guia;
					}
			    });
			}
			catch(Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
						+ "P:Impresion, C:ImpresionDaoImpl, M:consultarCtaContableRubro");
				System.err.println("Ocurrio un error: " + e);
			}
			return list;
		}
	
		//Agregado EMS 19/01/16 - Verifica que exista otra chequera en estatus I para la impresión
		//Si existe y no alcanza el papel con la chequera en estatus A, seguira imprimiendo con folios del registro con esttus I.
		@Override
		public List<ControlPapelDto> consultarSigChequera(int idBanco, String tipoFolio) {
			List<ControlPapelDto> list = new ArrayList<>();
			StringBuffer sql = new StringBuffer();
			try {
				sql.append("\n SELECT folio_inv_ini, folio_inv_fin, folio_ult_impreso ");
				sql.append("\n FROM control_papel ");
				sql.append("\n WHERE bco_cve = "+idBanco+" ");
				sql.append("\n AND tipo_folio = '"+Utilerias.validarCadenaSQL(tipoFolio)+"' ");
				sql.append("\n AND estatus = 'I' ");
				
				System.out.println("Consulta sig chequera papel: " + sql.toString());
				
				list = jdbcTemplate.query(sql.toString(), new RowMapper<ControlPapelDto>(){
					public ControlPapelDto mapRow(ResultSet rs, int idx) throws SQLException {
						ControlPapelDto ctrl = new ControlPapelDto();
						ctrl.setFolioInvIni(rs.getInt("folio_inv_ini"));
						ctrl.setFolioInvFin(rs.getInt("folio_inv_fin"));
						ctrl.setFolioUltImpreso(rs.getInt("folio_ult_impreso"));
						return ctrl;
					}
			    });
			}catch(CannotGetJdbcConnectionException e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Impresion, C:ImpresionDaoImpl, M:consultarSigChequera");
			}catch(Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
						+ "P:Impresion, C:ImpresionDaoImpl, M:consultarSigChequera");
				System.err.println("Ocurrio un error: " + e);
			}
			return list;
		}
		
		
	/*********Metodos agregados 22/01/2016 ****************/
	
	/*
	 * @Fecha: 26 de enero del 2016
	 * @Metodo: existeControlPapel
	 * @Parametros: idbanco
	 * @Parametros: tipoFolio , P para laser y C para continua
	 * @Parametros: idChequera
	 * @Parametros: noEmpresa
	 * @Parametros: estatus A activo, I en inventario o T terminado.
	 * @Objetivo: Verifica la existencia de chqueras en controlpapel  .
	 */
	public boolean existeControlPapel(int idBanco, String tipoFolio, String idChequera , String noEmpresa, String estatus){
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("SELECT count(*)");
			sql.append("\n FROM control_papel");
			sql.append("\n WHERE bco_cve = "+idBanco+" ");
			sql.append("\n AND tipo_folio = '"+Utilerias.validarCadenaSQL(tipoFolio)+"' ");
			sql.append("\n AND estatus = '"+ Utilerias.validarCadenaSQL(estatus) +"'");
			if(!idChequera.equals(""))
				sql.append("\n AND id_chequera = '"+Utilerias.validarCadenaSQL(idChequera)+"' ");
			if(!noEmpresa.equals(""))
				sql.append("\n AND no_empresa = '"+noEmpresa+"' ");
			System.out.println(sql.toString());
			int resp = jdbcTemplate.queryForInt(sql.toString());
			if(resp!=0)
				return true;
			else{
				return false;
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Impresion, C:ImpresionDaoImpl, M:alertaStock");
			return false;
		}
	}
	
	/*
	 * @Fecha: 26 de enero del 2016
	 * @Metodo: actualizarEstatusControlPapel
	 * @Parametros: ControlPapelDto cp
	 * @Parametros: String estatus // se refiere al nuevo estatus que se asignara
	 * @Objetivo: Actualizar los estatus para chequera en este caso de A a T o de I a A..
	 */
	
	public	int actualizarEstatusControlPapel(ControlPapelDto cp, String estatus){
		int updateCP = -1;
		StringBuffer sql = new StringBuffer();
		try
		{
			sql.append("\n UPDATE top(1) control_papel ");
			sql.append("\n    SET estatus = '" + Utilerias.validarCadenaSQL(estatus)+"'");
			if(estatus.equals("T"))
				sql.append("\n WHERE  id_control_cheque = "+ cp.getIdControlCheque());
			if(estatus.equals("A")){
				sql.append("\n WHERE bco_cve = "+cp.getIdBanco()+" ");
				sql.append("\n AND tipo_folio = '"+Utilerias.validarCadenaSQL(cp.getTipoFolio())+"' ");
				sql.append("\n ");
				sql.append("\n AND estatus = 'I'");
				if(!cp.getTipoFolio().equals("P"))
					sql.append("\n AND id_chequera = '"+Utilerias.validarCadenaSQL(cp.getIdChequera())+"' ");
				if(!cp.getTipoFolio().equals("P"))
					sql.append("\n AND no_empresa = '"+Utilerias.validarCadenaSQL(cp.getNoEmpresa())+"' ");
				//sql.append("\n order by id_control_cheque ASC");
			}
				
			System.out.println(sql.toString());
		    updateCP = jdbcTemplate.update(sql.toString());
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ImpresionDaoImpl, M:actualizarEstatusControlPapel");
		}
		return updateCP;
	}
	
	/*
	 * @Fecha: 26 de enero del 2016
	 * @Metodo: actualizarEstatusControlPapel
	 * @Parametros: idBanco obligatorio
	 * @Parametros: tipoFolio  , "C" para continua o "P" para laser
	 * @Parametros: idChequera , no necesario para laser
	 * @Parametros: noEmpresa , no necesario para laser
	 * @Parametros: estatus , si es requerido un estatus en particular
	 * @Objetivo: Obtener los datos de control papel, para obtener folios , e id del registro
	 */
	
	public 	ControlPapelDto datosControlPapel(int idBanco, String tipoFolio, String idChequera , String noEmpresa, String estatus){
		StringBuffer sql = new StringBuffer();
		List<ControlPapelDto> list = new ArrayList<>();
		try {
			sql.append("SELECT folio_inv_ini,folio_inv_fin, folio_ult_impreso,stock,id_control_cheque, estatus, tipo_folio,id_chequera,no_empresa, bco_cve");
			sql.append("\n FROM control_papel");
			sql.append("\n WHERE bco_cve = "+idBanco+" ");
			sql.append("\n AND tipo_folio = '"+Utilerias.validarCadenaSQL(tipoFolio)+"' ");
			sql.append("\n AND estatus = '"+ Utilerias.validarCadenaSQL(estatus) +"'");
			if(!idChequera.equals(""))
				sql.append("\n AND id_chequera = '"+Utilerias.validarCadenaSQL(idChequera)+"' ");
			if(!noEmpresa.equals(""))
				sql.append("\n AND no_empresa = '"+Utilerias.validarCadenaSQL(noEmpresa)+"' ");
			sql.append("ORDER BY id_control_cheque");
			System.out.println(sql.toString());
			list= jdbcTemplate.query(sql.toString(), new RowMapper<ControlPapelDto>(){
				public ControlPapelDto mapRow(ResultSet rs, int idx) throws SQLException {
					ControlPapelDto cons = new ControlPapelDto();
					cons.setFolioInvIni(rs.getInt("folio_inv_ini"));
					cons.setFolioInvFin(rs.getInt("folio_inv_fin"));
					cons.setFolioUltImpreso(rs.getInt("folio_ult_impreso"));
					cons.setStock(rs.getInt("stock"));
					cons.setIdControlCheque(rs.getInt("id_control_cheque"));
					cons.setEstatus(rs.getString("estatus"));
					cons.setTipoFolio(rs.getString("tipo_folio"));
					cons.setIdChequera(rs.getString("id_chequera"));
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setIdBanco(rs.getInt("bco_cve"));
					return cons;
			}});
			}catch(CannotGetJdbcConnectionException e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Impresion, C:ImpresionDaoImpl, M:alertaStock");
			}catch(Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:ImpresionDaoImpl, M:alertaStock");
				System.err.println("Ocurrio un error: " + e);
			}return list.get(0);
	}
	
	/*
	 * @Fecha: 26 de enero del 2016
	 * @Metodo: actualizarEstatusControlPapel
	 * @Parametros: idBanco obligatorio
	 * @Parametros: idChequera , no necesario para laser
	 * @Parametros: noEmpresa , no necesario para laser
	 * @Parametros: continua ,  true si es impresion continua o de lo contrario false
	 * @Objetivo: Obtener total de hojas , incluyendo los de estatus inactivo para los datos actuales
	 */
	
	public int totalHojasControlPapel(int idBanco,String idChequera , String noEmpresa , boolean continua){
		StringBuffer sql = new StringBuffer();
		int total=0;
		try{
			sql.append("SELECT COALESCE( SUM (folio_inv_fin-folio_ult_impreso) ,0) as total");
			sql.append("\n FROM control_papel ");
			sql.append("\n WHERE bco_cve="+ idBanco);
			if(continua){
				sql.append("\n AND tipo_folio='C'");
				sql.append("\n AND id_chequera='" + Utilerias.validarCadenaSQL(idChequera) +"'");
				sql.append("\n AND no_empresa= " + Utilerias.validarCadenaSQL(noEmpresa));
			}else
				sql.append("\n and tipo_folio='P'");
			
			total= jdbcTemplate.queryForInt(sql.toString());
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Impresion, C:ImpresionDaoImpl, M:alertaStock");
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:ImpresionDaoImpl, M:alertaStock");
			System.err.println("Ocurrio un error: " + e);
		}return total;
	}
	
	/*
	 * @Fecha: 26 de enero del 2016
	 * @Metodo:obtenerFirmantes
	 * @Parametros: idbanco
	 * @Parametros: idChequera
	 * @Parametros: numero cheque
	 * @Parametros: numero de firmante , puede ser 1 o 2.
	 * @Objetivo: Obtener de movimiento los firmantes 1 y 2 para efectos de impresión.
	 */
	public int obtenerFirmantes(int idBanco, String idChequera, String solicitud , int noFirmante){
		StringBuffer sql = new StringBuffer();
		int existen=0;
		try{
			sql.append("SELECT COUNT(firmante"+noFirmante+") FROM movimiento ");
			sql.append("\n where id_banco= "+ idBanco);
			sql.append("\n AND id_chequera ='"+ Utilerias.validarCadenaSQL(idChequera)+"'");
			sql.append("\n AND no_folio_det="+ Utilerias.validarCadenaSQL(solicitud));
			System.out.println("obtenerFirmantes ->"+sql.toString());
			int cont=jdbcTemplate.queryForInt(sql.toString());
			if(cont!=0 && cont==1){
				sql.delete(0, sql.length());
				sql.append("SELECT sum(firmante"+noFirmante+") FROM movimiento ");
				sql.append("\n where id_banco= "+ idBanco);
				sql.append("\n AND id_chequera ='"+ Utilerias.validarCadenaSQL(idChequera) +"'");
				sql.append("\n AND no_folio_det="+ Utilerias.validarCadenaSQL(solicitud));
				System.out.println("obtenerFirmantes ->"+sql.toString());
				existen=jdbcTemplate.queryForInt(sql.toString());
			}
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Impresion, C:ImpresionDaoImpl, M:obtenerFirmantes");
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:ImpresionDaoImpl, M:obtenerFirmantes");
			System.err.println("Ocurrio un error: " + e);
		}return existen;
	}
			
	
	@Override
	public String obtieneCuentaContable(String idChequera, int idBanco, int noEmpresa, double importe) {
		
		StringBuffer sql = new StringBuffer();
		List<String> list = new ArrayList<>();
		String res = "";
		
		try{
			sql.append("\n SELECT DISTINCT LIBRO_MAYOR ");
			sql.append("\n FROM CAT_CTAS_CONTABLES_ERP c LEFT JOIN MOVIMIENTO m ON m.id_chequera = c.id_chequera ");
			sql.append("\n WHERE m.id_banco = c.id_banco ");
			sql.append("\n AND m.no_empresa = c.no_empresa ");
			sql.append("\n AND c.cargo_abono = 'E' ");
			sql.append("\n AND m.id_chequera ='"+ Utilerias.validarCadenaSQL(idChequera) +"' ");
			sql.append("\n AND m.id_banco = "+ idBanco +" ");
			sql.append("\n AND m.no_empresa = "+noEmpresa+" ");
			sql.append("\n AND m.importe = "+importe+" ");
			
			System.out.println("obtener cuenta contable: "+sql.toString());
			
			list= jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
				public String mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getString("libro_mayor");
			}});
			
			if(list.size() > 0){
				res = list.get(0);
			}
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Impresion, C:ImpresionDaoImpl, M:obtieneCuentaContable");
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:ImpresionDaoImpl, M:obtieneCuentaContableobtenerFirmantes");
			System.err.println("Ocurrio un error: " + e);
		}
		
		return res;
		
	}
	
	@Override
	public String obtenerNombreEmpresa(int noEmpresa) {
		
		StringBuffer sql = new StringBuffer();
		List<String> list = new ArrayList<>();
		String res = "";
		
		try{
			sql.append("\n SELECT NOM_EMPRESA ");
			sql.append("\n FROM EMPRESA ");
			sql.append("\n WHERE NO_EMPRESA = "+noEmpresa+" ");
			
			System.out.println("obtener nombre empresa: "+sql.toString());
			
			list= jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
				public String mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getString("nom_empresa");
			}});
			
			if(list.size() > 0){
				res = list.get(0);
			}
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Impresion, C:ImpresionDaoImpl, M:obtieneCuentaContable");
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:ImpresionDaoImpl, M:obtieneCuentaContableobtenerFirmantes");
			System.err.println("Ocurrio un error: " + e);
		}
		
		return res;
		
	}
	
	public List<LlenaComboGralDto>llenarComboMotivos(){
		
		StringBuffer sql = new StringBuffer();
		List<LlenaComboGralDto> listMotivos = new ArrayList<LlenaComboGralDto>();
		
		try{
			
			sql.append( " SELECT causa as id, descripcion ");
			sql.append( "\n   FROM CAUSAS_NOVALIDEZ_CHEQUE ");
			sql.append( "\n   ORDER BY descripcion ");
			listMotivos= jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>(){
			public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
				LlenaComboGralDto cons = new LlenaComboGralDto();
				cons.setId(rs.getInt("ID"));
				cons.setDescripcion(rs.getString("descripcion"));
				return cons;
			}});
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ReimpresionDaoImpl, M:llenarComboMotivos");
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Impresion, C:ReimpresionDaoImpl, M:llenarComboMotivos");
		}
		return listMotivos;
	}
	
}
