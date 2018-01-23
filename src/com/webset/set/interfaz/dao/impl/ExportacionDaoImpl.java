package com.webset.set.interfaz.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.conciliacionbancoset.dto.CriteriosBusquedaDto;
import com.webset.set.interfaz.dao.ExportacionDao;
import com.webset.set.interfaz.dto.GuiaContableDto;
import com.webset.set.interfaz.dto.InterfazDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;

@SuppressWarnings("unchecked")
public class ExportacionDaoImpl implements ExportacionDao{
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora;
	String sql = "";
	
	Funciones funciones = new Funciones();
	ConsultasGenerales consultasGenerales = new ConsultasGenerales(jdbcTemplate);
	
	ConsultasGenerales objConsultasGenerales;
	GlobalSingleton globalSingleton;
	
	//Logger
	private Logger logger = Logger.getLogger(ExportacionDaoImpl.class);

	public List<InterfazDto> llenaComboEmpresa(){
		List<InterfazDto> listaResultado = new ArrayList<InterfazDto>();
		sql = "";
		
		try{
			sql = "Select * from empresa";
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public InterfazDto mapRow(ResultSet rs, int idx)throws SQLException{
					InterfazDto campos = new InterfazDto();
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setNomEmpresa(rs.getString("nom_empresa"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Interfaz, C: ExportacionDaoImpl, M: llenaComboEmpresa");
		}return listaResultado;
	}
	
	public List<InterfazDto> llenaGridRealizados(int noEmpresa, String fecHoy){
		List<InterfazDto> listaResultado = new ArrayList<InterfazDto>();
		sql = "";
		try{
			sql = "SELECT m.no_empresa, e.nom_empresa, m.id_caja,m.fec_entregado,m.id_chequera_benef,m.referencia, ";
			sql += "\n 	m.no_folio_mov,m.no_folio_det, m.no_docto, ";
			sql += "\n 	case when m.id_estatus_mov = 'H' then '' else ce.desc_estatus ";
			sql += "\n 	end as desc_estatus,cb.desc_banco," ;
			sql += "\n 	m.fec_valor,m.id_cve_operacion,m.beneficiario, m.id_divisa_original, ";
			sql += "\n 	m.tipo_cambio,m.no_cheque,m.id_chequera,m.id_banco, ";
			sql += "\n 	m.importe,m.importe_original,cd.desc_divisa,m.id_tipo_operacion, ";
			sql += "\n 	m.id_divisa,m.id_forma_pago,cfp.desc_forma_pago,cc.desc_caja, ";
			sql += "\n 	(select desc_banco from cat_banco cb ";
			sql += "\n 	where cb.id_banco = m.id_banco_benef) as desc_banco_benef, ";
			sql += "\n 	(select sum(m.importe) from movimiento m " ;
			sql += "\n 	where  m.lote_entrada=2900 and m.id_divisa='MN') as tmn, ";
			sql += "\n 	(select sum(importe) from movimiento where id_divisa<>'MN') as TExtr, coalesce(m.concepto, '') as concepto, m.id_tipo_movto, ";
			sql += "\n 	coalesce(p.razon_social, '') as razon_social, ";
			sql += "\n 	coalesce(case when m.concepto = '' then 'DOCUMENTO SIN CONCEPTO, FAVOR DE CLASIFICAR' ";
			sql += "\n 			when CHARINDEX('/', coalesce(m.concepto, '')) <= 0 and CHARINDEX(coalesce(m.no_factura, ''), coalesce(m.concepto, '')) <= 0 and m.id_tipo_movto = 'I' and m.origen_mov = 'BCE' and m.id_tipo_operacion != 3154 "; 
			sql += "\n 			then 'DOCUMENTO SIN RECLASIFICAR' ";
//			sql += "\n 			when m.origen_mov != 'BCE' and m.id_tipo_movto = 'E' and (select count(*) from config_contable c where c.no_folio_det_pag = m.no_folio_det) = 0 ";
//			sql += "\n 			then 'SIN CONFIGURACION CONTABLE' ";
			sql += "\n 			end, '') as causaRech ";
			sql += "\n FROM movimiento m ";
			sql += "\n 	LEFT JOIN cat_divisa cd ON(m.id_divisa = cd.id_divisa) ";
			sql += "\n 	LEFT JOIN cat_caja cc ON(m.id_caja = cc.id_caja) ";
			sql += "\n 	LEFT JOIN cat_cve_operacion cco ON(m.id_cve_operacion=cco.id_cve_operacion) ";
			sql += "\n 	LEFT JOIN cat_banco cb ON(m.id_banco = cb.id_banco) ";
			sql += "\n 	LEFT JOIN cat_estatus ce ON(m.id_estatus_mov=ce.id_estatus and ce.clasificacion='MOV') ";
			sql += "\n 	LEFT JOIN cat_forma_pago cfp ON(m.id_forma_pago = cfp.id_forma_pago) ";
			sql += "\n 	LEFT JOIN persona p on (m.no_cliente = p.no_persona), empresa e ";
			sql += "\n WHERE ";
			sql += "\n m.id_forma_pago = cfp.id_forma_pago ";
			
			if (noEmpresa > 0)
				sql += "\n and  m.no_empresa= "+ noEmpresa +" ";
			
			sql += "\n and  m.no_empresa = e.no_empresa ";
			sql += "\n and ((((m.id_estatus_mov in ('I','R') ";
			sql += "\n and (m.b_entregado='S' OR m.b_entregado='N') ";
			sql += "\n and (grupo_pago IS NULL OR grupo_pago = 0)) ";
			sql += "\n or (m.id_estatus_mov='G')) ";
			sql += "\n or (m.id_estatus_mov='K' ";
			sql += "\n and (convert(date, m.fec_conf_trans, 103) <= '"+ fecHoy +"') ";
			sql += "\n and (grupo_pago IS NULL OR grupo_pago = 0)) ";
			sql += "\n and id_tipo_operacion=3200) ";
			sql += "\n or (m.id_tipo_operacion in (1016, 1017, 1018, 1023, 1026, 1029, 3700, 3701, 3101, 3154, 4104) and "; //se comenta la 3115 por que se mandaran por la interfaz de cobranza
			sql += "\n m.id_estatus_mov = case when grupo_pago = 0 then 'A' ";
			sql += "\n Else '*' end) ";
			sql += "\n or (id_tipo_operacion = 1021 and id_estatus_mov = 'A')";
			sql += "\n or (id_tipo_operacion = 3201 ";
			sql += "\n 		and ( id_estatus_mov = 'H' AND ((b_entregado = 'S' and m.id_forma_pago=1) ";
			sql += "\n or (convert(date, m.fec_conf_trans, 103) <= '"+ fecHoy +"' and m.id_forma_pago =3))) ";
			sql += "\n and (grupo_pago IS NOT NULL AND grupo_pago <> 0)) ";
			sql += "\n OR ( id_tipo_operacion = 3200  ";
			sql += "\n      AND id_estatus_mov = 'K'  ";
			sql += "\n      AND convert(date, m.fec_conf_trans, 103) <= '"+ fecHoy +"' ";
			sql += "\n 	    AND m.id_forma_pago =3 ";
			sql += "\n      AND grupo_pago IS NOT NULL AND grupo_pago <> 0) ";
			sql += "\n or (id_tipo_operacion in (4102)) or (id_tipo_operacion in (3801) and id_estatus_mov in ('L'))) ";
			sql += "\n and (lote_salida is null or lote_salida=0) ";
			
			//sql += "\n and (origen_mov is null or origen_mov='' or origen_mov='SET' or origen_mov='SAE' ";
			//sql += "\n or origen_mov='ADU' or origen_mov = 'CVD') ";
			
			sql += "\n and (origen_mov is null or origen_mov = (SELECT com.origen_mov FROM cat_origen_mov com WHERE com.origen_mov = m.origen_mov))";
			
			sql += "\n or (id_estatus_mov = 'A' and id_tipo_operacion = 8100) ";
			sql += "\n and m.id_tipo_movto='E' ";
			sql += "\n ORDER BY m.no_folio_det ";	
			
			System.out.println("consulta para exportar: " + sql);
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public InterfazDto mapRow(ResultSet rs, int idx)throws SQLException{
					InterfazDto campos = new InterfazDto();
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setNomEmpresa(rs.getString("nom_empresa"));
					campos.setNoDocto(rs.getString("no_docto"));
					campos.setNoCheque(rs.getString("no_cheque"));
					campos.setIdChequera(rs.getString("id_chequera"));
					campos.setFecValor(rs.getString("fec_valor"));
					campos.setImporte(rs.getDouble("importe"));
					campos.setIdDivisa(rs.getString("id_divisa"));
					campos.setDescDivisa(rs.getString("desc_divisa"));
					campos.setFormaPago(rs.getInt("id_forma_pago"));
					campos.setDescFormaPago(rs.getString("desc_forma_pago"));
					campos.setIdBanco(rs.getInt("id_banco"));
					campos.setDescBanco(rs.getString("desc_banco"));
					campos.setEstatus(rs.getString("desc_estatus"));
					campos.setTipoCambio(rs.getDouble("tipo_cambio"));
					campos.setNoFolioDet(rs.getString("no_folio_det"));
					campos.setReferencia(rs.getString("referencia"));
					campos.setCausaRech(rs.getString("causaRech"));
					campos.setConcepto(rs.getString("concepto"));
					campos.setIdTipoMovto(rs.getString("id_tipo_movto"));
					campos.setIdTipoOperacion(rs.getString("id_tipo_operacion"));
					campos.setRazonSocial(rs.getString("razon_social"));
					return campos;
				}
			});
		}catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Intefaz, C: ExportacionDaoImpl, M: llenaGridRealizados");
		}return listaResultado;
	}
	
	public List<InterfazDto> llenaGridCancelados(int noEmpresa, String fecHoy){
		List<InterfazDto> listaResultado = new ArrayList<InterfazDto>();
		sql = "";
		try{
			sql = "SELECT m.no_empresa, m.no_folio_mov, m.no_docto, m.fec_modif, m.id_tipo_operacion, ";
			sql += "\n m.no_folio_det, case when m.id_estatus_mov = 'H' ";
			sql += "\n then '' else ce.desc_estatus end as desc_estatus, ";
			sql += "\n case when m.id_forma_pago = 2  then cc.desc_caja ";
			sql += "\n else cb.desc_banco end  as desc_banco, ";
			sql += "\n m.id_caja, m.fec_valor, m.id_cve_operacion, m.beneficiario, m.id_divisa_original, ";
			sql += "\n m.tipo_cambio, m.no_cheque, m.id_chequera, m.id_banco, ";
			sql += "\n m.importe, m.importe_original, cd.desc_divisa, m.id_tipo_operacion, m.id_divisa, ";
			sql += "\n m.id_forma_pago, cfp.desc_forma_pago, cc.desc_caja, ";
			sql += "\n (select sum(m.importe) from movimiento m where m.id_divisa = 'MN') as tmn, "; //where  m.no_factura='" & pvvValor1 & "'"			
			sql += "\n (select sum(importe) from movimiento where id_divisa<>'MN') as TExtr ";
			sql += "\n FROM movimiento m ";
			sql += "\n LEFT JOIN cat_divisa cd ON(m.id_divisa = cd.id_divisa) ";
			sql += "\n LEFT JOIN cat_forma_pago cfp ON(m.id_forma_pago = cfp.id_forma_pago) ";
			sql += "\n LEFT JOIN cat_caja cc ON(m.id_caja = cc.id_caja) ";
			sql += "\n LEFT JOIN cat_cve_operacion cco ON(m.id_cve_operacion=cco.id_cve_operacion) ";
			sql += "\n LEFT JOIN cat_banco cb ON(m.id_banco=cb.id_banco) ";
			sql += "\n LEFT JOIN cat_estatus ce ";
			sql += "\n ON(m.id_estatus_mov=ce.id_estatus and ce.clasificacion='MOV') ";
			sql += "\n WHERE ";
			
			if (noEmpresa > 0)
				sql += "\n m.no_empresa = "+ noEmpresa +" ";
			
			sql += "\n and m.id_estatus_mov in ('X','Z','Y') ";
			sql += "\n and (origen_mov is null or origen_mov='' or origen_mov='SET' ";
			sql += "\n or origen_mov='ADU') and m.id_tipo_movto='E' ";
			sql += "\n and m.id_tipo_operacion in(3200,3701,3700,1021) ";
			sql += "\n and tipo_cancelacion in ('C','O') ";
			sql += "\n and (lote_salida is null or lote_salida=0) ";
			sql += "\n ORDER BY m.no_folio_det ";
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public InterfazDto mapRow(ResultSet rs, int idx)throws SQLException{
					InterfazDto campos = new InterfazDto();
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setNoDocto(rs.getString("no_docto"));
					campos.setNoCheque(rs.getString("no_cheque"));
					campos.setIdChequera(rs.getString("id_chequera"));
					campos.setFecValor(rs.getString("fec_valor"));
					campos.setImporte(rs.getDouble("importe"));
					campos.setIdDivisa(rs.getString("id_divisa"));
					campos.setFormaPago(rs.getInt("id_forma_pago"));
					campos.setIdBanco(rs.getInt("id_banco"));
					campos.setEstatus(rs.getString("desc_estatus"));
					campos.setTipoCambio(rs.getDouble("tipo_cambio"));
					campos.setNoFolioDet(rs.getString("no_folio_det"));
		
					return campos;
		
				}
			});		
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Interfaz, C: ExportacionDaoImpl, M: llenaGridCancelados");
		}return listaResultado;
	}
	
	public int insertComisionesAgrup(List<InterfazDto> listMovtos, int i, double importe, int folioDetConta, int noOper) {
		StringBuffer sbSQL = new StringBuffer();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		int res = 0;
		
		try {
			sbSQL.append(" INSERT INTO zexp_fact");
			sbSQL.append("\n SELECT m.no_empresa, '', 0, 0, ");
			
			if(noOper == 1)
				sbSQL.append(" 	no_folio_det, ");
			else
				sbSQL.append(" 	"+ folioDetConta +", ");
			
			sbSQL.append("\n 	isnull(no_docto, '') as no_docto, convert(char(10), fec_valor, 103) as fec_valor,");
			sbSQL.append("\n 	convert(char(10), fec_operacion, 103), isnull(origen_mov, '') as origen_mov, ");
			sbSQL.append("\n 	coalesce((select p.equivale_persona from persona p where p.no_persona = no_cliente and p.id_tipo_persona = 'P'), '') as no_persona, ");
			
			if(noOper == 1)
				sbSQL.append("\n 	importe, ");
			else
				sbSQL.append("\n 	"+ importe +", ");
			
			sbSQL.append(" 		'MN', m.id_divisa_original, tipo_cambio, id_forma_pago, m.id_banco, m.id_chequera, referencia, ");
			sbSQL.append("\n 	concepto, 0, '', '0', '', '', '"+ funciones.ponerFechaSola(consultasGenerales.obtenerFechaHoy()) +"', 'P', '', convert(char(10), fec_valor, 103), '', importe, "); 
			sbSQL.append("\n 	id_tipo_operacion, id_tipo_movto, 0, 0, 0, '', 0, 0, null, null, null, 0, '', null, null, g.id_grupo, g.id_rubro, id_caja, observacion, "); 
			sbSQL.append("\n 	id_area, null, coalesce(beneficiario, '') as beneficiario, null, usuario_alta, 0, '', 0, 0, "); 
			
			if(listMovtos.get(i).getIdTipoMovto().equals("E"))
				sbSQL.append("\n 	replace(g.cuenta_contable, '-', '') as cargo, (ce.libro_mayor + ce.cuenta_bancaria + ce.depto + ce.id_cuenta) as abono, ");
			else
				sbSQL.append("\n 	(ce.libro_mayor + ce.cuenta_bancaria + ce.depto + ce.id_cuenta) as cargo, replace(g.cuenta_contable, '-', '') as abono, ");
			
			if(noOper == 1)
				sbSQL.append("\n 	0, 0, 0, 0, '', '' ");
			else
				sbSQL.append("\n 	"+ folioDetConta +", 0, 0, 0, '', '' ");
			
			sbSQL.append("\n FROM movimiento m left join GUIA_CONTABLE g on (m.id_cve_operacion = g.id_cve_operacion) "); 
			sbSQL.append("\n 		left join cat_equivalencia_cta_contable ce on (m.no_empresa = ce.no_empresa "); 
			sbSQL.append("\n 		And m.id_chequera = ce.id_Chequera And ce.id_divisa = case when m.id_divisa != 'MN' then m.id_divisa else '' end) "); 
			sbSQL.append("\n WHERE no_folio_det = "+ listMovtos.get(i).getNoFolioDet() +" ");
			
			res = jdbcTemplate.update(sbSQL.toString());
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:insertComisionesAgrup");
			return -1;
		}
		return res;
	}
	
	public int insertComisionesAgrupDet(List<InterfazDto> listMovtos, int i, double tipoCambio, int folioDetConta, 
			boolean complementaria, int folioComple, List<InterfazDto> ctasConta, boolean regTotal, double importeTotal) {
		StringBuffer sql = new StringBuffer();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		int res = 0;
		
		try {
			sql.append(" INSERT INTO zexp_fact_det(no_empresa, no_doc_sap, secuencia, no_pedido, no_folio_set, no_factura, \n");
			sql.append(" 	fec_valor, origen, no_persona, imp_pago, id_divisa, id_divisa_original, tipo_camb, forma_pago, \n");
			sql.append(" 	id_banco, id_cheque, referencia, concepto, mandante, fecha_exp, estatus, fechap, tipo_operacion, \n");
			sql.append(" 	tipo_movto, no_cheque, cve_control, id_rubro, id_grupo, observacion, beneficiario, id_banco_benef, \n");
			sql.append(" 	cargo, abono, folio_ref, id_departamento, id_proyecto, id_centro_costo, contabilizado) \n");
			sql.append(" VALUES("+ listMovtos.get(i).getNoEmpresa() +", '', 0, 0, \n");
			
			if(complementaria)
				sql.append(" "+ folioComple +" \n");
			else if(regTotal)
				sql.append(" "+ folioDetConta +" \n");
			else
				sql.append(" "+ listMovtos.get(i).getNoFolioDet() +" \n");
			
			sql.append(" 	, '"+ listMovtos.get(i).getNoDocto() +"', \n");
			sql.append("  	'"+ listMovtos.get(i).getFecValor() +"', '"+ listMovtos.get(i).getOrigenMov() +"', '"+ listMovtos.get(i).getNoPersona() +"', \n");
			
			if(complementaria)
				sql.append("	"+ ((listMovtos.get(i).getImporte() * tipoCambio) - listMovtos.get(i).getImporte()) +", 'MN' \n");
			else if(regTotal)
				sql.append("	"+ importeTotal +", 'MN' \n");
			else
				sql.append("	"+ listMovtos.get(i).getImporte() +", '"+ listMovtos.get(i).getIdDivisa() +"' \n");
			
			sql.append("  	, '"+ listMovtos.get(i).getIdDivisaOriginal() +"', \n");
			sql.append("  	"+ tipoCambio +", "+ listMovtos.get(i).getFormaPago() +", "+ listMovtos.get(i).getIdBanco() +", '"+ listMovtos.get(i).getIdChequera() +"', \n");
			sql.append("	'"+ listMovtos.get(i).getReferencia() +"', '"+ listMovtos.get(i).getConcepto() +"', '000', '"+ funciones.ponerFechaSola(consultasGenerales.obtenerFechaHoy()) +"', \n");
			sql.append("  	'P', '"+ funciones.ponerFechaSola(listMovtos.get(i).getFecValor()) +"', "+ listMovtos.get(i).getIdTipoOperacion() +", '"+ listMovtos.get(i).getIdTipoMovto() +"', \n");
			sql.append(" 	"+ listMovtos.get(i).getNoCheque() +", '"+ listMovtos.get(i).getCveControl() +"', "+ listMovtos.get(i).getIdRubro() +", "+ listMovtos.get(i).getIdGrupo() +", \n");
			sql.append(" 	'', '"+ listMovtos.get(i).getBeneficiario() +"', "+ listMovtos.get(i).getIdBancoBenef() +", \n");
			
/*			if(complementaria) {
				if(listMovtos.get(i).getIdTipoMovto().equals("E"))
					sql.append(" 	'', '"+ ctasConta.get(0).getCtaContaBanco() +"', \n");
				else
					sql.append(" 	'"+ ctasConta.get(0).getCtaContaBanco() +"', '', \n");
			}else */
			
			if(regTotal){
				if(listMovtos.get(i).getIdTipoMovto().equals("E"))
					sql.append(" 	'"+ ctasConta.get(0).getCtaContaProv() +"', '', \n");
				else
					sql.append(" 	'', '"+ ctasConta.get(0).getCtaContaProv() +"', \n");
			}else {
				if(listMovtos.get(i).getIdTipoMovto().equals("E"))
					sql.append(" 	'', '"+ ctasConta.get(0).getCtaContaBanco() +"', \n");
				else
					sql.append(" 	'"+ ctasConta.get(0).getCtaContaBanco() +"', '', \n");
			}
			sql.append(" 	"+ folioDetConta +", 0, 0, 0, '') \n");
			
			res = jdbcTemplate.update(sql.toString());
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:insertComisionesAgrupDet");
			return -1;
		}
		return res;
	}
	
	public int updateMovComisiones(int noFolioDet) {
		StringBuffer sql = new StringBuffer();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		int res = 0;
		
		try {
			sql.append("\n UPDATE movimiento SET lote_salida = 1, fec_exportacion = '"+ funciones.ponerFechaSola(consultasGenerales.obtenerFechaHoy()) +"' "); 
			sql.append("\n WHERE no_folio_det in ("+ noFolioDet +") "); 
			
			res = jdbcTemplate.update(sql.toString());
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:updateMovComisiones");
			return -1;
		}
		return res;
	}
	
	/*
	public Map llamaExportador(String folios, String prefijo)
	{
		logger.debug("Entra llamaExportador");
		objConsultasGenerales = new ConsultasGenerales(jdbcTemplate);
		Map result = objConsultasGenerales.llamaExportador(folios, prefijo);
		logger.debug("Sale llamaExportador");
		return result;
	}
	*/
	
	public int llamaExportador(String folios, String prefijo) {
		int res = 0;
		
		try {
			
		}
		catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Interfaz, C: ExportacionDaoImpl, M:setDataSource");
		}
		return res;
	}
	
	//**********************************************************************************************************************************************
	public void setDataSource(DataSource dataSource) {
		try {
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
		catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Interfaz, C: ExportacionDaoImpl, M:setDataSource");
		}
	}

	@Override
	public List<InterfazDto> llenaGridRechazados(int noEmpresa, String fecHoy) {
		
		List<InterfazDto> listaResultado = new ArrayList<InterfazDto>();
		
		sql = "";
		
		try{
			sql = "SELECT m.no_empresa, m.no_folio_mov, m.no_docto, m.fec_modif, m.id_tipo_operacion, ";
			sql += "\n m.no_folio_det, case when m.id_estatus_mov = 'H' ";
			sql += "\n then '' else ce.desc_estatus end as desc_estatus, ";
			sql += "\n case when m.id_forma_pago = 2  then cc.desc_caja ";
			sql += "\n else cb.desc_banco end  as desc_banco, ";
			sql += "\n m.id_caja, m.fec_valor, m.id_cve_operacion, m.beneficiario, m.id_divisa_original, ";
			sql += "\n m.tipo_cambio, m.no_cheque, m.id_chequera, m.id_banco, ";
			sql += "\n m.importe, m.importe_original, cd.desc_divisa, m.id_tipo_operacion, m.id_divisa, ";
			sql += "\n m.id_forma_pago, cfp.desc_forma_pago, cc.desc_caja, ";
			sql += "\n (select sum(m.importe) from movimiento m where m.id_divisa = 'MN') as tmn, "; //where  m.no_factura='" & pvvValor1 & "'"			
			sql += "\n (select sum(importe) from movimiento where id_divisa<>'MN') as TExtr ";
			sql += "\n ,m.CONCEPTO, m.REFERENCIA, ze.CTA_CONTABLEA, m.ID_TIPO_MOVTO";
			sql += "\n FROM movimiento m ";
			sql += "\n INNER JOIN ZEXP_FACT ZE ON(m.NO_FOLIO_DET = ZE.NO_FOLIO_SET AND ZE.ESTATUS = 'R') ";
			sql += "\n LEFT JOIN cat_divisa cd ON(m.id_divisa = cd.id_divisa) ";
			sql += "\n LEFT JOIN cat_forma_pago cfp ON(m.id_forma_pago = cfp.id_forma_pago) ";
			sql += "\n LEFT JOIN cat_caja cc ON(m.id_caja = cc.id_caja) ";
			sql += "\n LEFT JOIN cat_cve_operacion cco ON(m.id_cve_operacion=cco.id_cve_operacion) ";
			sql += "\n LEFT JOIN cat_banco cb ON(m.id_banco=cb.id_banco) ";
			sql += "\n LEFT JOIN cat_estatus ce ";
			sql += "\n ON(m.id_estatus_mov=ce.id_estatus and ce.clasificacion='MOV') ";
			sql += "\n WHERE m.fec_valor = '" + fecHoy + "'";
			
			if (noEmpresa > 0)
				sql += "\n and m.no_empresa = "+ noEmpresa +" ";
			

			sql += "\n ORDER BY m.no_folio_det ";
			System.out.println(sql);
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public InterfazDto mapRow(ResultSet rs, int idx)throws SQLException{
					InterfazDto campos = new InterfazDto();
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setNoDocto(rs.getString("no_docto"));
					campos.setNoCheque(rs.getString("no_cheque"));
					campos.setIdChequera(rs.getString("id_chequera"));
					campos.setFecValor(rs.getString("fec_valor"));
					campos.setImporte(rs.getDouble("importe"));
					campos.setIdDivisa(rs.getString("id_divisa"));
					campos.setFormaPago(rs.getInt("id_forma_pago"));
					campos.setIdBanco(rs.getInt("id_banco"));
					campos.setEstatus(rs.getString("desc_estatus"));
					campos.setTipoCambio(rs.getDouble("tipo_cambio"));
					campos.setNoFolioDet(rs.getString("no_folio_det"));
					campos.setConcepto(rs.getString("concepto"));
					campos.setReferencia(rs.getString("referencia"));
					campos.setIdTipoMovto(rs.getString("id_tipo_movto"));
					campos.setCtaContablea(rs.getString("cta_contablea"));
		
					return campos;
		
				}
			});		
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Interfaz, C: ExportacionDaoImpl, M: llenaGridCancelados");
		}return listaResultado;
	}

	@Override
	public List<GuiaContableDto> llenaComboCuentas(String noCuenta, String idTipoMovto) {
		
		String sql = "";
		StringBuffer sb = new StringBuffer();
				
		sb.append( " SELECT	*" );
		sb.append( " FROM GUIA_CONTABLE	");                             sb.append( "\n");
		sb.append( " WHERE cuenta_contable like '%" + noCuenta + "%'");	sb.append( "\n");
		sb.append( " AND ID_RUBRO IN(");	                            sb.append( "\n");
		sb.append( " SELECT ID_RUBRO FROM CAT_RUBRO WHERE INGRESO_EGRESO = '" + idTipoMovto + "'");	                            sb.append( "\n");
		sb.append( "                 )");	                            sb.append( "\n");
	  	
				
	    sql=sb.toString();
	    
	    System.out.println( sql );
	    
		List <GuiaContableDto> guiasContables = null;

		guiasContables = jdbcTemplate.query(sql, new RowMapper(){
			
			public GuiaContableDto mapRow(ResultSet rs, int idx){
				
				GuiaContableDto guiaContable = new GuiaContableDto();
					try {
						
						guiaContable.setIdGuia(rs.getInt("id_guia"));
						guiaContable.setIdGrupo(rs.getInt("id_grupo"));
						guiaContable.setIdRubro(rs.getInt("id_rubro"));
						guiaContable.setNoEmpresa(rs.getInt("no_empresa"));
						guiaContable.setCuentaContable(rs.getString("cuenta_contable"));
						
					} catch (SQLException e) {
						
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Traspasos, C:ReporteDeCuposDao, M:llenarComboProveedores");
						
					}
					
				return guiaContable;				
		}});
			
		return guiasContables;		
	}
	
	public int updateMovimientoSET(String noFolioDet, CriteriosBusquedaDto dto){
		
		StringBuffer sbSQL = new StringBuffer();
		
		sbSQL.append(" UPDATE ZEXP_FACT SET");
		sbSQL.append("\n CTA_CONTABLEA = '" + dto.getCuentaContable().replace("-", "") + "',");
		sbSQL.append("\n ESTATUS = 'P'");
		sbSQL.append("\n WHERE no_folio_set = " + noFolioDet + "");
		sbSQL.append("\n AND ESTATUS = 'R'");
		
		try {
			
			return jdbcTemplate.update(sbSQL.toString());
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:updateMovimientoSETVIngresos");
			return -1;
		}

		
	}
	
	public List<InterfazDto> llenaGridPendientesCXC(int noEmpresa, String fecHoy){
		List<InterfazDto> listaResultado = new ArrayList<InterfazDto>();
		String sql = "";
		
		try{
			sql = "SELECT m.no_empresa, e.nom_empresa, m.id_caja,m.fec_entregado,m.id_chequera_benef,m.referencia, ";
			sql += "\n 	m.no_folio_mov,m.no_folio_det, m.no_docto, ";
			sql += "\n 	case when m.id_estatus_mov = 'H' then '' else ce.desc_estatus ";
			sql += "\n 	end as desc_estatus,cb.desc_banco," ;
			sql += "\n 	m.fec_valor,m.id_cve_operacion,m.beneficiario, m.id_divisa_original, ";
			sql += "\n 	m.tipo_cambio,m.no_cheque,m.id_chequera,m.id_banco, ";
			sql += "\n 	m.importe,m.importe_original,cd.desc_divisa,m.id_tipo_operacion, ";
			sql += "\n 	m.id_divisa,m.id_forma_pago,cfp.desc_forma_pago,cc.desc_caja, ";
			sql += "\n 	(select desc_banco from cat_banco cb ";
			sql += "\n 	where cb.id_banco = m.id_banco_benef) as desc_banco_benef, ";
			sql += "\n 	(select sum(m.importe) from movimiento m " ;
			sql += "\n 	where  m.lote_entrada=2900 and m.id_divisa='MN') as tmn, ";
			sql += "\n 	(select sum(importe) from movimiento where id_divisa<>'MN') as TExtr, coalesce(m.concepto, '') as concepto, m.id_tipo_movto, ";
			sql += "\n 	coalesce(p.razon_social, '') as razon_social, ";
			sql += "\n 	coalesce(case when m.concepto = '' then 'DOCUMENTO SIN CONCEPTO, FAVOR DE CLASIFICAR' ";
			sql += "\n 			when CHARINDEX('/', coalesce(m.concepto, '')) <= 0 and CHARINDEX(coalesce(m.no_factura, ''), coalesce(m.concepto, '')) <= 0 and m.id_tipo_movto = 'I' and m.origen_mov = 'BCE' "; 
			sql += "\n 			then 'DOCUMENTO SIN RECLASIFICAR' ";
			sql += "\n 			end, '') as causaRech ";
			sql += "\n FROM movimiento m ";
			sql += "\n 	LEFT JOIN cat_divisa cd ON(m.id_divisa = cd.id_divisa) ";
			sql += "\n 	LEFT JOIN cat_caja cc ON(m.id_caja = cc.id_caja) ";
			sql += "\n 	LEFT JOIN cat_cve_operacion cco ON(m.id_cve_operacion=cco.id_cve_operacion) ";
			sql += "\n 	LEFT JOIN cat_banco cb ON(m.id_banco = cb.id_banco) ";
			sql += "\n 	LEFT JOIN cat_estatus ce ON(m.id_estatus_mov=ce.id_estatus and ce.clasificacion='MOV') ";
			sql += "\n 	LEFT JOIN cat_forma_pago cfp ON(m.id_forma_pago = cfp.id_forma_pago) ";
			sql += "\n 	LEFT JOIN persona p on (m.no_cliente = p.no_persona), empresa e ";
			sql += "\n WHERE m.id_forma_pago = cfp.id_forma_pago  ";
			
			if (noEmpresa > 0)
				sql += "\n and  m.no_empresa= "+ noEmpresa +" ";
			
			sql += "\n and  m.no_empresa = e.no_empresa  ";
			sql += "\n and m.id_tipo_operacion in (3101, 3115) ";
			sql += "\n and id_estatus_mov = 'A' ";
			sql += "\n and origen_mov = 'BCE' ";
			sql += "\n and m.id_tipo_movto = 'I' ";
			sql += "\n and (lote_salida is null or lote_salida = 0) ";
			sql += "\n ORDER BY m.no_folio_det ";
			
			System.out.println("consulta para exportar CXC: " + sql);
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper(){
				public InterfazDto mapRow(ResultSet rs, int idx)throws SQLException{
					InterfazDto campos = new InterfazDto();
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setNomEmpresa(rs.getString("nom_empresa"));
					campos.setNoDocto(rs.getString("no_docto"));
					campos.setNoCheque(rs.getString("no_cheque"));
					campos.setIdChequera(rs.getString("id_chequera"));
					campos.setFecValor(rs.getString("fec_valor"));
					campos.setImporte(rs.getDouble("importe"));
					campos.setIdDivisa(rs.getString("id_divisa"));
					campos.setDescDivisa(rs.getString("desc_divisa"));
					campos.setFormaPago(rs.getInt("id_forma_pago"));
					campos.setDescFormaPago(rs.getString("desc_forma_pago"));
					campos.setIdBanco(rs.getInt("id_banco"));
					campos.setDescBanco(rs.getString("desc_banco"));
					campos.setEstatus(rs.getString("desc_estatus"));
					campos.setTipoCambio(rs.getDouble("tipo_cambio"));
					campos.setNoFolioDet(rs.getString("no_folio_det"));
					campos.setReferencia(rs.getString("referencia"));
					campos.setCausaRech(rs.getString("causaRech"));
					campos.setConcepto(rs.getString("concepto"));
					campos.setIdTipoMovto(rs.getString("id_tipo_movto"));
					campos.setIdTipoOperacion(rs.getString("id_tipo_operacion"));
					campos.setRazonSocial(rs.getString("razon_social"));
					return campos;
				}
			});
		}catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Intefaz, C: ExportacionDaoImpl, M: llenaGridPendientesCXC");
		}return listaResultado;
	}
	
	public List<InterfazDto> listaRegistrosCXC(String folios) {
		List<InterfazDto> listaResultado = new ArrayList<InterfazDto>();
		String sbSQL = "";
		int res = 0;
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		
		try {
			sbSQL = "SELECT m.no_folio_det, '' as no_cliente, m.no_empresa, '' as no_poliza, m.id_banco, m.id_chequera, m.fec_valor, m.no_cheque,";
			sbSQL += "\n 	m.referencia, m.importe, m.id_divisa, m.tipo_cambio, m.id_forma_pago, m.id_tipo_operacion,";
			sbSQL += "\n 	m.fec_alta, '" + funciones.ponerFechaSola(consultasGenerales.obtenerFechaHoy()) + "' as fecha_exp, 'P' as estatus, '' as causa_rech, m.concepto, ";
			sbSQL += "\n 	0 as id_proyecto, 0 as id_departamento, 0 as id_centro_costo, ce.libro_mayor + ce.cuenta_bancaria + ce.depto + ce.id_cuenta as cargo, c.cta_cargo as abono, m.no_folio_det";
			sbSQL += "\n 	FROM movimiento m, config_contable c, cat_equivalencia_cta_contable ce";
			sbSQL += "\n 	WHERE m.no_folio_det = c.no_folio_det_pag";
			sbSQL += "\n 	And m.no_empresa = ce.no_empresa";
			sbSQL += "\n 	And m.id_banco = ce.id_banco";
			sbSQL += "\n 	And m.id_chequera = ce.id_Chequera";
			sbSQL += "\n 	And ce.id_divisa = case when m.id_divisa != 'MN' then m.id_divisa else '' end";
			sbSQL += "\n 	And m.no_folio_det in ("+ folios +")";
			System.out.println("lista datos "+sbSQL.toString());
			listaResultado = jdbcTemplate.query(sbSQL, new RowMapper(){
				public InterfazDto mapRow(ResultSet rs, int idx)throws SQLException{
					InterfazDto campos = new InterfazDto();
					campos.setNoFolioDet2(rs.getInt("no_folio_det"));
					campos.setNoCliente(rs.getString("no_cliente"));
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setNoPoliza(rs.getString("no_poliza"));
					campos.setIdBanco(rs.getInt("id_banco"));
					campos.setIdChequera(rs.getString("id_chequera"));
					campos.setFecValor(rs.getString("fec_valor"));
					campos.setNoCheque(rs.getString("no_cheque"));
					campos.setReferencia(rs.getString("referencia"));
					campos.setImporte(rs.getDouble("importe"));
					campos.setIdDivisa(rs.getString("id_divisa"));
					campos.setTipoCambio(rs.getDouble("tipo_cambio"));
					campos.setFormaPago(rs.getInt("id_forma_pago"));
					campos.setIdTipoOperacion(rs.getString("id_tipo_operacion"));
					campos.setFecImp(rs.getString("fec_alta"));
					campos.setFechaExp(rs.getString("fecha_exp"));
					campos.setEstatus(rs.getString("estatus"));
					campos.setCausaRech(rs.getString("causa_rech"));
					campos.setConcepto(rs.getString("concepto"));
					campos.setCargo(rs.getString("cargo"));
					campos.setAbono(rs.getString("abono"));
					campos.setFolioRef(rs.getInt("no_folio_det"));
					return campos;
				}
			});
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:listaRegistrosCXC");
		}
		return listaResultado;
	}
	
	public int exportaRegistrosCXC(List<InterfazDto> listaDatos,int i) {
		StringBuffer sbSQL = new StringBuffer();
		int res = 0;
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		
		try {
			sbSQL.append(" INSERT INTO zdepositos (no_folio_det,no_cliente,no_empresa,no_poliza,id_banco,");
			sbSQL.append("\n  cta_banco,fecha_pago,no_cheque,referencia,importe,ietu,iva,id_divisa,tipo_camb,id_forma_pago,id_tipo_operacion,fecha_imp,fecha_exp,estatus,");
			sbSQL.append("\n causa_rech,concepto,id_proyecto,id_departamento,id_centro_costo,cargo,abono,folio_ref) values (");
			sbSQL.append("\n "+listaDatos.get(i).getNoFolioDet2()+",'"+listaDatos.get(i).getNoCliente()+"',"+listaDatos.get(i).getNoEmpresa()+",");
			sbSQL.append("\n '"+listaDatos.get(i).getNoPoliza()+"',"+listaDatos.get(i).getIdBanco()+",'"+listaDatos.get(i).getIdChequera()+"',");
			sbSQL.append("\n CONVERT(date,'"+listaDatos.get(i).getFecValor()+"'),"+Integer.parseInt(listaDatos.get(i).getNoCheque())+",'"+listaDatos.get(i).getReferencia()+"',");
			sbSQL.append("\n "+listaDatos.get(i).getImporte()+",0,0,");
			sbSQL.append("\n '"+listaDatos.get(i).getIdDivisa()+"',"+listaDatos.get(i).getTipoCambio()+",'"+listaDatos.get(i).getFormaPago()+"',");
			sbSQL.append("\n "+Integer.parseInt(listaDatos.get(i).getIdTipoOperacion())+",CONVERT(date,'"+listaDatos.get(i).getFecImp()+"'),'"+listaDatos.get(i).getFechaExp()+"',");
			sbSQL.append("\n '"+listaDatos.get(i).getEstatus()+"','"+listaDatos.get(i).getCausaRech()+"','"+listaDatos.get(i).getConcepto()+"',");
			sbSQL.append("\n "+listaDatos.get(i).getIdProyecto()+","+listaDatos.get(i).getIdDepto()+","+listaDatos.get(i).getCentroCosto()+",");
			sbSQL.append("\n '"+listaDatos.get(i).getCargo()+"','"+listaDatos.get(i).getAbono()+"',"+listaDatos.get(i).getFolioRef()+"");
			sbSQL.append("\n )");
			System.out.println(sbSQL.toString());
			res = jdbcTemplate.update(sbSQL.toString());
			
			if(res != 0) {
				sbSQL = new StringBuffer();
				sbSQL.append(" UPDATE movimiento SET lote_salida = 1, fec_exportacion = '" + funciones.ponerFechaSola(consultasGenerales.obtenerFechaHoy()) + "' ");
				sbSQL.append("\n WHERE no_folio_det = "+ listaDatos.get(i).getNoFolioDet2()+"");
				System.out.println("update "+sbSQL.toString());
				res = jdbcTemplate.update(sbSQL.toString());
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:exportaRegistrosCXC");
			return -1;
		}
		return res;
	}
	
	
	public List<InterfazDto> totalFolioRef(int noEmpresa, double tipoCambio) {
		StringBuffer sbSQL = new StringBuffer();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		List<InterfazDto> listRes = new ArrayList<InterfazDto>();
		
		try {
			sbSQL.append(" select case when id_divisa = 'MN' then SUM(importe) else SUM(importe * "+ tipoCambio +") end as importe, ");
			sbSQL.append(" 		id_tipo_operacion, id_divisa, (select coalesce(num_folio, 0) from folio ");
			sbSQL.append(" 		where tipo_folio = 'folio_ref_conta') as folio_ref_conta, id_tipo_movto ");
			sbSQL.append("\n FROM movimiento ");
			sbSQL.append("\n WHERE id_tipo_operacion in (1016, 1017, 4104, 3154)");
			sbSQL.append("\n 	And origen_mov = 'BCE' ");
			sbSQL.append("\n 	And (lote_salida is null or lote_salida = 0)");
			sbSQL.append("\n 	And no_empresa = "+ noEmpresa +"");
			sbSQL.append("\n GROUP BY id_divisa, id_tipo_operacion, id_tipo_movto");
			sbSQL.append("\n ORDER BY id_tipo_operacion ");
			
			listRes = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public InterfazDto mapRow(ResultSet rs, int idx)throws SQLException{
					InterfazDto campos = new InterfazDto();
					campos.setImporte(rs.getDouble("importe"));
					campos.setIdTipoOperacion(rs.getString("id_tipo_operacion"));
					campos.setIdDivisa(rs.getString("id_divisa"));
					campos.setFolioDetConta(rs.getInt("folio_ref_conta"));
					campos.setIdTipoMovto(rs.getString("id_tipo_movto"));
					return campos;
				}
			});
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:totalFolioRef");
		}
		return listRes;
	}
	
	public List<InterfazDto> listaRegistrosDetalleCXC(int folio) {
		List<InterfazDto> listaResultado = new ArrayList<InterfazDto>();
		String sbSQL = "";
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		
		try {
			sbSQL = "SELECT m.no_folio_det, c.no_benef, c.no_empresa, '' as no_poliza, m.id_banco, m.id_chequera, m.fec_valor, m.no_cheque,";
			sbSQL += "\n 	m.referencia, c.importe, c.ietu, c.iva, c.id_divisa, m.tipo_cambio, c.forma_pago, m.id_tipo_operacion,";
			sbSQL += "\n 	c.fecha_imp, '" + funciones.ponerFechaSola(consultasGenerales.obtenerFechaHoy()) + "' as fecha_exp, c.estatus, '' as causa_rech, ";
			sbSQL += "\n 	c.concepto, c.id_proyecto, c.id_departamento, c.id_centro_costo, ";
			sbSQL += "\n 	ce.libro_mayor + ce.cuenta_bancaria + ce.depto + ce.id_cuenta as cargo, c.cta_contable as abono, c.folio_ref, c.no_factura ";
			sbSQL += "\n   FROM cobranza c, cat_equivalencia_cta_contable ce, movimiento m";
			sbSQL += "\n   WHERE c.folio_ref = m.no_folio_det";
			sbSQL += "\n 	And m.no_empresa = ce.no_empresa";
			sbSQL += "\n 	And m.id_banco = ce.id_banco";
			sbSQL += "\n 	And m.id_chequera = ce.id_Chequera";
			sbSQL += "\n 	And c.estatus = 'A'";
			sbSQL += "\n 	And ce.id_divisa = case when m.id_divisa != 'MN' then m.id_divisa else '' end";
			sbSQL += "\n 	And c.folio_ref  = "+ folio +"";
			System.out.println("lista detalle "+sbSQL.toString());
			listaResultado = jdbcTemplate.query(sbSQL, new RowMapper(){
				public InterfazDto mapRow(ResultSet rs, int idx)throws SQLException{
					InterfazDto campos = new InterfazDto();
					campos.setNoFolioDet2(rs.getInt("no_folio_det"));
					campos.setNoCliente(rs.getString("no_benef"));
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setNoPoliza(rs.getString("no_poliza"));
					campos.setIdBanco(rs.getInt("id_banco"));
					campos.setIdChequera(rs.getString("id_chequera"));
					campos.setFecValor(rs.getString("fec_valor"));
					campos.setNoCheque(rs.getString("no_cheque"));
					campos.setReferencia(rs.getString("referencia"));
					campos.setImporte(rs.getDouble("importe"));
					campos.setIetuDet(rs.getString("ietu"));
					campos.setIvaDet(rs.getString("iva"));
					campos.setIdDivisa(rs.getString("id_divisa"));
					campos.setTipoCambio(rs.getDouble("tipo_cambio"));
					campos.setFormaPago(rs.getInt("forma_pago"));
					campos.setIdTipoOperacion(rs.getString("id_tipo_operacion"));
					campos.setFecImp(rs.getString("fecha_imp"));
					campos.setFechaExp(rs.getString("fecha_exp"));
					campos.setEstatus(rs.getString("estatus"));
					campos.setCausaRech(rs.getString("causa_rech"));
					campos.setConcepto(rs.getString("concepto"));
					campos.setIdProyecto(rs.getInt("id_proyecto"));
					campos.setIdDepto(rs.getInt("id_departamento"));
					campos.setCentroCosto(rs.getInt("id_centro_costo"));
					campos.setCargo(rs.getString("cargo"));
					campos.setAbono(rs.getString("abono"));
					campos.setFolioRef(rs.getInt("folio_ref"));
					campos.setNoDocto(rs.getString("no_factura"));
					return campos;
				}
			});
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:listaRegistrosDetalleCXC");
		
		}
		return listaResultado;
	}
	
	public int exportaRegistrosDetalleCXC(List<InterfazDto> listaDatosDet,int i, int ban) {
		StringBuffer sbSQL = new StringBuffer();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		int res = 0;
		
		try {
			
			sbSQL.append(" INSERT INTO zdepositos_det (no_folio_det,no_cliente,no_empresa,no_poliza,id_banco,cta_banco,fecha_pago,no_cheque,");
			sbSQL.append("\n referencia,importe,ietu,iva,id_divisa,tipo_camb,id_forma_pago,id_tipo_operacion,fecha_imp,fecha_exp,estatus,causa_rech,");
			sbSQL.append("\n concepto,id_proyecto,id_departamento,id_centro_costo,cargo,abono,folio_ref,no_factura) values (");
			sbSQL.append("\n "+listaDatosDet.get(i).getNoFolioDet2()+",'"+listaDatosDet.get(i).getNoCliente()+"',"+listaDatosDet.get(i).getNoEmpresa()+",");
			sbSQL.append("\n '"+listaDatosDet.get(i).getNoPoliza()+"',"+listaDatosDet.get(i).getIdBanco()+",'"+listaDatosDet.get(i).getIdChequera()+"',");
			sbSQL.append("\n '"+ funciones.cambiarFecha(listaDatosDet.get(i).getFecValor()) +"', "+Integer.parseInt(listaDatosDet.get(i).getNoCheque())+", '"+listaDatosDet.get(i).getReferencia()+"',");
			
			switch (ban) {
			case 0:
				sbSQL.append("\n "+listaDatosDet.get(i).getImporte()+", ");
				break;
			case 1:
				sbSQL.append("\n 	convert(decimal(15, 2), ("+listaDatosDet.get(i).getImporte()+"/1.16)), ");
				break;
			case 2:
				sbSQL.append("\n 	convert(decimal(15, 2), (convert(decimal(15, 2), ("+listaDatosDet.get(i).getImporte()+" / 1.16)) * .16)), "); 
				break;
			case 3:
				sbSQL.append("\n "+listaDatosDet.get(i).getImporte()+", ");
				break;
			}
			
			sbSQL.append("\n 0,0,'"+listaDatosDet.get(i).getIdDivisa()+"', "+listaDatosDet.get(i).getTipoCambio()+", "+listaDatosDet.get(i).getFormaPago()+", ");
			sbSQL.append("\n "+Integer.parseInt(listaDatosDet.get(i).getIdTipoOperacion())+", '"+ funciones.cambiarFecha(listaDatosDet.get(i).getFecImp()) +"', '"+listaDatosDet.get(i).getFechaExp()+"',");
			sbSQL.append("\n 'P','',");
			
			switch (ban) {
			case 0:
				sbSQL.append("\n 	'"+listaDatosDet.get(i).getConcepto()+"', ");
				break;
			case 1:
				sbSQL.append("\n 	'CALCULO IETU', ");
				break;
			case 2:
				sbSQL.append("\n 	'CALCULO IVA', "); 
				break;
			case 3:
				sbSQL.append("\n 	'"+listaDatosDet.get(i).getConcepto()+"', ");
				break;
			}
			sbSQL.append("\n "+listaDatosDet.get(i).getIdProyecto()+","+listaDatosDet.get(i).getIdDepto()+","+listaDatosDet.get(i).getCentroCosto()+",");
			
			switch (ban) {
			case 0:
				sbSQL.append("\n '','"+listaDatosDet.get(i).getAbono()+"',");
				break;
			case 1:		// Cargo, abono
				sbSQL.append("\n 	'909020100', '909020200', ");
				break;
			case 2:
				sbSQL.append("\n 	case when "+listaDatosDet.get(i).getNoEmpresa()+" = 11 then '222220201' else '222220200' end, ");	// Cargo
				sbSQL.append("\n 	case when "+listaDatosDet.get(i).getNoEmpresa()+" = 11 then '222220101' else '222220100' end, ");	// Abono
				break;
			case 3:
				sbSQL.append("\n '"+listaDatosDet.get(i).getCargo()+"','',");
				break;
			}
			
			sbSQL.append("\n "+listaDatosDet.get(i).getFolioRef()+", ");
			if(ban == 3)
				sbSQL.append("\n '') ");
			else
				sbSQL.append("\n '"+ listaDatosDet.get(i).getNoDocto() +"') ");
			
			System.out.println("INSERT DETALLE "+sbSQL.toString());
			
			res = jdbcTemplate.update(sbSQL.toString());
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:exportaRegistrosDetalleCXC");
			return -1;
		}
		return res;
	}
	
	public int insertaRegistrosIETU_IVACXC(String folios, int tipoMovto) {
		StringBuffer sbSQL = new StringBuffer();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		int res = 0;
		
		try {
			sbSQL.append(" INSERT INTO zdepositos_det");
			sbSQL.append("\n SELECT TOP 1 m.no_folio_det, c.no_benef, c.no_empresa, '', m.id_banco, m.id_chequera, m.fec_valor, m.no_cheque, m.referencia, ");
			
			switch (tipoMovto) {
				case 1:
					sbSQL.append("\n 	convert(decimal(15, 2), (m.importe/1.16)) as ietu, ");
					break;
				case 2:
					sbSQL.append("\n 	convert(decimal(15, 2), (convert(decimal(15, 2), (m.importe/1.16)) * .16)) as iva, "); 
					break;
			}
			sbSQL.append("\n 	0, 0, c.id_divisa, m.tipo_cambio, c.forma_pago, m.id_tipo_operacion, ");
			sbSQL.append("\n 	c.fecha_imp, '" + funciones.ponerFechaSola(consultasGenerales.obtenerFechaHoy()) + "', 'P', '', ");
			
			switch (tipoMovto) {
				case 1:
					sbSQL.append("\n 	'CALCULO IETU', ");
					break;
				case 2:
					sbSQL.append("\n 	'CALCULO IVA', "); 
					break;
			}
			sbSQL.append("\n 	c.id_proyecto, c.id_departamento, c.id_centro_costo, ");
			
			switch (tipoMovto) {
				case 1:
					sbSQL.append("\n 	'909020100' as cargo, '909020200' as abono, ");
					break;
				case 2:
					sbSQL.append("\n 	case when c.no_empresa = 11 then '222220201' else '222220200' end as cargo, ");
					sbSQL.append("\n 	case when c.no_empresa = 11 then '222220101' else '222220100' end as abono, ");
					break;
			}
			
			
			sbSQL.append("\n 	c.folio_ref");
			sbSQL.append("\n FROM cobranza c, cat_equivalencia_cta_contable ce, movimiento m");
			sbSQL.append("\n WHERE c.folio_ref = m.no_folio_det");
			sbSQL.append("\n 	And m.no_empresa = ce.no_empresa");
			sbSQL.append("\n 	And m.id_banco = ce.id_banco");
			sbSQL.append("\n 	And m.id_chequera = ce.id_Chequera");
			sbSQL.append("\n 	And c.estatus = 'A'");
			sbSQL.append("\n 	And ce.id_divisa = case when m.id_divisa != 'MN' then m.id_divisa else '' end");
			sbSQL.append("\n 	And c.folio_ref in ("+ folios +")");
				
			switch (tipoMovto) {
				case 1:
					sbSQL.append("\n 	And c.ietu = 'S' ");
					break;
				case 2:
					sbSQL.append("\n 	And c.ietu = 'S' ");
					break;
			}
			res = jdbcTemplate.update(sbSQL.toString());
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:exportaRegistrosDetalleCXC");
			return -1;
		}
		return res;
	}
	
	public List<InterfazDto> totalEmpresas() {
		StringBuffer sbSQL = new StringBuffer();
		List<InterfazDto> listRes = new ArrayList<InterfazDto>();
		globalSingleton = GlobalSingleton.getInstancia();
		
		try {
			sbSQL.append(" SELECT * FROM empresa \n");
			sbSQL.append(" WHERE no_empresa in (select no_empresa from usuario_empresa ");
			sbSQL.append(" 		where no_usuario = "+ globalSingleton.getUsuarioLoginDto().getIdUsuario() +" ) \n");
			sbSQL.append(" ORDER BY no_empresa");
			
			listRes = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public InterfazDto mapRow(ResultSet rs, int idx)throws SQLException{
					InterfazDto campos = new InterfazDto();
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					return campos;
				}
			});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:totalEmpresas");
		}
		return listRes;
	}
	
	public List<InterfazDto> buscaMovtos(String foliosDet, boolean comisiones) {
		StringBuffer sql = new StringBuffer();
		List<InterfazDto> listRes = new ArrayList<InterfazDto>();
		
		try {
			sql.append(" SELECT folio_ref, no_folio_mov, no_empresa, isnull(no_cheque, '0') as no_cheque, no_folio_det, \n"); 
			sql.append(" 	isnull(folio_banco, 0) as referencia, id_banco, id_chequera, id_banco_benef, \n");
			sql.append(" 	coalesce(descripcion, '') as descripcion, id_chequera_benef, convert(char(10), fec_valor, 103) as fec_valor, \n");
			sql.append(" 	id_divisa, isnull(tipo_cambio, 1) as tipo_cambio, id_forma_pago, id_rubro, id_grupo, importe, \n");
			sql.append(" 	isnull(no_docto, '') as no_docto, isnull(convert(char(10), fec_entregado, 103), '') as fec_entregado,  \n");
			sql.append(" 	coalesce(usuario_alta, '') as usuario_alta, isnull(convert(char(10), fec_modif, 103), '') as fec_modif, \n");
			sql.append(" 	case when concepto like '%ANTICIPO%' then 3235 else id_tipo_operacion end as 'id_tipo_operacion', \n");
			sql.append(" 	case when origen_mov = 'CVT' then no_cliente_ant else no_cliente end as no_cliente, isnull(origen_mov, '') as origen_mov, \n");
			sql.append(" 	isnull(no_pedido, 0) as no_pedido, id_tipo_movto, \n");
			sql.append(" 	isnull(invoice_type, '') as invoice_type, isnull(grupo_pago, 0) as grupo_pago, isnull(id_estatus_mov, '') as id_estatus_mov, \n");
			sql.append(" 	case when patindex('%/%', concepto) = 0 then coalesce(beneficiario, '') + ' / ' + isnull(no_factura, '') + ' / ' + isnull(concepto, '') \n");
			sql.append(" 	else isnull(concepto, '') end as concepto, convert(char(10), fec_operacion,103) as fec_operacion, \n");
			sql.append(" 	importe_original, ISNULL(fec_conf_trans, '') as fec_conf_trans, isnull(no_factura, '') as no_factura, \n");
			sql.append(" 	isnull(id_divisa_original, '') as id_divisa_original, isnull(cve_control, '') as cve_control, \n");
			sql.append(" 	coalesce(beneficiario, '') as beneficiario, coalesce(no_cliente_ant, 0) as no_cliente_ant, isnull(importe_parcial, 0) as importe_parcial, \n");
			sql.append(" 	coalesce((select p.equivale_persona from persona p where p.no_persona = no_cliente and p.id_tipo_persona = 'P'), '') as no_persona \n");
			sql.append(" FROM movimiento \n");
			sql.append(" WHERE no_folio_det in ("+ foliosDet +") \n");
			
			if(!comisiones){
				sql.append(" 	And not id_tipo_operacion in (1016, 1017, 3154, 4104) \n");
				sql.append(" ORDER BY id_divisa desc, grupo_pago, id_tipo_operacion \n");
			}else {
				sql.append(" 	And id_tipo_operacion in (1016, 1017, 3154, 4104) \n");
				sql.append(" ORDER BY id_tipo_operacion, id_divisa desc \n");
			}
			System.out.println("Query exportador de CXP: " + sql.toString());
			
			listRes = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public InterfazDto mapRow(ResultSet rs, int idx)throws SQLException{
					InterfazDto campos = new InterfazDto();
					campos.setFolioRef(rs.getInt("folio_ref"));
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setNoCheque(rs.getString("no_cheque"));
					campos.setNoFolioDet(rs.getString("no_folio_det"));
					campos.setReferencia(rs.getString("referencia"));
					campos.setIdBanco(rs.getInt("id_banco"));
					campos.setIdChequera(rs.getString("id_chequera"));
					campos.setIdBancoBenef(rs.getInt("id_banco_benef"));
					campos.setIdChequeraBenef(rs.getString("id_chequera_benef"));
					campos.setDescripcion(rs.getString("descripcion"));
					campos.setFecValor(rs.getString("fec_valor"));
					campos.setIdDivisa(rs.getString("id_divisa"));
					campos.setTipoCambio(rs.getDouble("tipo_cambio"));
					campos.setFormaPago(rs.getInt("id_forma_pago"));
					campos.setIdGrupo(rs.getInt("id_grupo"));
					campos.setIdRubro(rs.getInt("id_rubro"));
					campos.setImporte(rs.getDouble("importe"));
					campos.setNoDocto(rs.getString("no_factura"));
					campos.setFecEntregado(rs.getString("fec_entregado"));
					campos.setIdTipoOperacion(rs.getString("id_tipo_operacion"));
					campos.setNoCliente(rs.getString("no_cliente"));
					campos.setFecModif(rs.getString("fec_modif"));
					campos.setOrigenMov(rs.getString("origen_mov"));
					campos.setIdTipoMovto(rs.getString("id_tipo_movto"));
					campos.setGrupoPago(rs.getInt("grupo_pago"));
					campos.setEstatus(rs.getString("id_estatus_mov"));
					campos.setConcepto(rs.getString("concepto"));
					campos.setImporteOriginal(rs.getDouble("importe_original"));
					campos.setFecConfTrans(rs.getString("fec_conf_trans"));
					campos.setIdDivisaOriginal(rs.getString("id_divisa_original"));
					campos.setBeneficiario(rs.getString("beneficiario"));
					campos.setNoPersona(rs.getString("no_persona"));
					campos.setCveControl(rs.getString("cve_control"));
					return campos;
				}
			});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:buscaMovtos");
		}
		return listRes;
	}
	
	public int insertZexpFact(List<InterfazDto> listMovtos, int i, List<InterfazDto> ctasConta, double tipoCambio, List<InterfazDto> listDepCCPro) {
		StringBuffer sbSQL = new StringBuffer();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		int res = 0;
		
		try {
			sbSQL.append(" INSERT INTO zexp_fact(no_pedido, mandante, no_empresa, secuencia, no_doc_sap, fec_valor, imp_pago, id_divisa, tipo_camb, \n");
			sbSQL.append(" 		forma_pago, id_banco, id_cheque, estatus, causa_rech, fechap, no_persona, origen, concepto, fecha_exp, \n");
			sbSQL.append(" 		imp_usa, no_folio_set, no_factura, id_divisa_original, referencia, tipo_operacion, tipo_movto, fec_factura, \n");
			sbSQL.append(" 		interes, isr, chequera_inv, no_cheque, cve_control, beneficiario, id_grupo, id_rubro, \n");
			sbSQL.append(" 		id_banco_benef, cargo, abono, folio_ref, id_departamento, id_proyecto, id_centro_costo) \n");
			
			sbSQL.append(" VALUES(0, '000', "+ listMovtos.get(i).getNoEmpresa() +", 0, '', '"+ listMovtos.get(i).getFecValor() +"', \n");
			
			if(listMovtos.get(i).getIdDivisa().equals("MN"))
				sbSQL.append("  	"+ listMovtos.get(i).getImporte() +", 'MN', "+ tipoCambio +", \n");
			else
				sbSQL.append("  	"+ (listMovtos.get(i).getImporte() * tipoCambio) +", 'MN', "+ tipoCambio +", \n");
			
			sbSQL.append("  	"+ listMovtos.get(i).getFormaPago() +", "+ listMovtos.get(i).getIdBanco() +", '"+ listMovtos.get(i).getIdChequera() +"', \n");
			sbSQL.append("  	'P', '', '"+ listMovtos.get(i).getFecValor() +"', '"+ listMovtos.get(i).getNoPersona() +"', '"+ listMovtos.get(i).getOrigenMov() +"', \n");
			sbSQL.append("  	'"+ listMovtos.get(i).getConcepto() +"', '"+ funciones.ponerFechaSola(consultasGenerales.obtenerFechaHoy()) +"', 0, \n");
			sbSQL.append("  	"+ listMovtos.get(i).getNoFolioDet() +", '"+ listMovtos.get(i).getNoDocto() +"', '"+ listMovtos.get(i).getIdDivisaOriginal() +"', \n");
			sbSQL.append(" 		'"+ listMovtos.get(i).getReferencia() +"', "+ listMovtos.get(i).getIdTipoOperacion() +", '"+ listMovtos.get(i).getIdTipoMovto() +"', \n");
			sbSQL.append(" 		'"+ listMovtos.get(i).getFecValor() +"', 0, 0, '', '"+ listMovtos.get(i).getNoCheque() +"', '"+ listMovtos.get(i).getCveControl() +"', \n");
			sbSQL.append(" 		'"+ listMovtos.get(i).getBeneficiario() +"', "+ listMovtos.get(i).getIdGrupo() +", "+ listMovtos.get(i).getIdRubro() +", \n");
			sbSQL.append(" 		"+ listMovtos.get(i).getIdBancoBenef() +", '"+ ctasConta.get(0).getCtaContaProv() +"', '"+ ctasConta.get(0).getCtaContaBanco() +"', \n");
			
			if(listMovtos.get(i).getIdDivisa().equals("MN"))
				sbSQL.append(" 		"+ listMovtos.get(i).getGrupoPago() +", \n");
			else
				sbSQL.append(" 		"+ listMovtos.get(i).getNoFolioDet() +", \n");
			
			if(listDepCCPro.size() != 0)
				sbSQL.append(" 		"+ listDepCCPro.get(0).getIdDepto() +", "+ listDepCCPro.get(0).getIdProyecto() +", "+ listDepCCPro.get(0).getCentroCosto() +") \n");
			else
				sbSQL.append(" 		0,0,0) \n");
			
			res = jdbcTemplate.update(sbSQL.toString());
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:insertZexpFact");
			return -1;
		}
		return res;
	}
	
	public int insertZexpFactDet(List<InterfazDto> listMovtos, int i, double tipoCambio, int noFolioDet, List<InterfazDto> ctasConta,
			boolean complementaria, boolean grupoPago, List<InterfazDto> listDepCCPro) {
		
		StringBuffer sbSQL = new StringBuffer();
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		int res = 0;
		
		try {
			sbSQL.append(" INSERT INTO zexp_fact_det(no_pedido, mandante, no_empresa, secuencia, no_doc_sap, fec_valor, tipo_camb, id_divisa, imp_pago, no_folio_set, \n");
			sbSQL.append(" 		forma_pago, id_banco, id_cheque, estatus, causa_rech, fechap, no_persona, origen, concepto, fecha_exp, \n");
			sbSQL.append(" 		imp_usa, no_factura, id_divisa_original, referencia, tipo_operacion, tipo_movto, fec_factura, \n");
			sbSQL.append(" 		interes, isr, chequera_inv, no_cheque, cve_control, beneficiario, id_grupo, id_rubro, \n");
			sbSQL.append(" 		id_banco_benef, cargo, abono, folio_ref, id_departamento, id_proyecto, id_centro_costo) \n");
			
			sbSQL.append(" VALUES(0, '000', "+ listMovtos.get(i).getNoEmpresa() +", 0, '', '"+ listMovtos.get(i).getFecValor() +"', "+ tipoCambio +", 'MN', \n");
			
			if(complementaria)
				sbSQL.append("		"+ ((listMovtos.get(i).getImporte() * tipoCambio) - listMovtos.get(i).getImporte()) +", "+ noFolioDet +", \n");
			else if(grupoPago && !listMovtos.get(i).getIdDivisa().equals("MN"))
				sbSQL.append("		"+ (listMovtos.get(i).getImporte() * tipoCambio) +", "+ noFolioDet +", \n");
			else
				sbSQL.append("		"+ listMovtos.get(i).getImporte() +", "+ listMovtos.get(i).getNoFolioDet() +", \n");
			
			sbSQL.append("  	"+ listMovtos.get(i).getFormaPago() +", "+ listMovtos.get(i).getIdBanco() +", '"+ listMovtos.get(i).getIdChequera() +"', \n");
			sbSQL.append("  	'P', '', '"+ listMovtos.get(i).getFecValor() +"', '"+ listMovtos.get(i).getNoPersona() +"', '"+ listMovtos.get(i).getOrigenMov() +"', \n");
			sbSQL.append("  	'"+ listMovtos.get(i).getConcepto() +"', '"+ funciones.ponerFechaSola(consultasGenerales.obtenerFechaHoy()) +"', 0, \n");
			sbSQL.append("  	'"+ listMovtos.get(i).getNoDocto() +"', '"+ listMovtos.get(i).getIdDivisaOriginal() +"', \n");
			sbSQL.append(" 		'"+ listMovtos.get(i).getReferencia() +"', "+ listMovtos.get(i).getIdTipoOperacion() +", '"+ listMovtos.get(i).getIdTipoMovto() +"', \n");
			sbSQL.append(" 		'"+ listMovtos.get(i).getFecValor() +"', 0, 0, '', '"+ listMovtos.get(i).getNoCheque() +"', '"+ listMovtos.get(i).getCveControl() +"', \n");
			sbSQL.append(" 		'"+ listMovtos.get(i).getBeneficiario() +"', "+ listMovtos.get(i).getIdGrupo() +", "+ listMovtos.get(i).getIdRubro() +", \n");
			sbSQL.append(" 		"+ listMovtos.get(i).getIdBancoBenef() +", \n");
			
			if(grupoPago)
				sbSQL.append(" 	'', '"+ ctasConta.get(0).getCtaContaBanco() +"', \n");
			else
				sbSQL.append(" 	'"+ ctasConta.get(0).getCtaContaProv() +"', '', \n");
			
			if(complementaria || grupoPago)
				sbSQL.append(" 		"+ listMovtos.get(i).getNoFolioDet() +", \n");
			else
				sbSQL.append(" 		"+ listMovtos.get(i).getGrupoPago() +", \n");
			
			if(listDepCCPro.size() != 0)
				sbSQL.append(" 		"+ listDepCCPro.get(0).getIdDepto() +", "+ listDepCCPro.get(0).getIdProyecto() +", "+ listDepCCPro.get(0).getCentroCosto() +") \n");
			else
				sbSQL.append(" 		0,0,0) \n");
			
			res = jdbcTemplate.update(sbSQL.toString());
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:insertZexpFactDet");
			return -1;
		}
		return res;
	}
	public int updateMovimiento(List<InterfazDto> listMovtos, int i) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		StringBuffer sbSQL = new StringBuffer();
		int res = 0;
		
		try {
			sbSQL.append(" UPDATE movimiento \n");
			sbSQL.append(" SET lote_salida = 1, fec_exportacion = '"+ funciones.ponerFecha(consultasGenerales.obtenerFechaHoy()) +"' \n");
			sbSQL.append(" WHERE no_empresa = "+ listMovtos.get(i).getNoEmpresa() +" \n");
			sbSQL.append(" 		And no_folio_det = "+ listMovtos.get(i).getNoFolioDet() +" \n");
			
			res = jdbcTemplate.update(sbSQL.toString());
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:updateMovimiento");
			return -1;
		}
		return res;
	}
	
	public double obtenTipoCambio() {
		StringBuffer sbSQL = new StringBuffer();
		double resultado = 0.0;
		List<InterfazDto> listRes = new ArrayList<InterfazDto>();
		
		try {
			sbSQL.append(" SELECT top 1 coalesce(valor, 0) as tipo_cambio \n");
			sbSQL.append(" FROM valor_divisa \n");
			sbSQL.append(" WHERE id_divisa = 'DLS' ");
			sbSQL.append(" ORDER BY fec_divisa DESC");
			
			listRes = jdbcTemplate.query(sbSQL.toString(), new RowMapper(){
				public InterfazDto mapRow(ResultSet rs, int idx)throws SQLException{
					InterfazDto campos = new InterfazDto();
					campos.setTipoCambio(rs.getDouble("tipo_cambio"));
					return campos;
				}
			});
			
			if(listRes.size() > 0) resultado = listRes.get(0).getTipoCambio(); 
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:totalEmpresas");
		}
		return resultado;
	}
	
	public List<InterfazDto> ctasContables(int noFolioDet, boolean divisaDLS) {
		List<InterfazDto> list = new ArrayList<InterfazDto>();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT coalesce((c.libro_mayor + c.cuenta_bancaria + c.depto + c.id_cuenta), '') as ctaBanco, \n");
			sql.append(" 	case when id_tipo_operacion = 1016 or id_tipo_operacion = 1017 or id_tipo_operacion = 4104 or id_tipo_operacion = 3154 \n");
			sql.append(" 		 	then coalesce((select REPLACE(cuenta_contable, '-', '') from guia_contable where id_cve_operacion = m.id_cve_operacion), '') \n");
			sql.append(" 		 when id_tipo_operacion = 3200 and grupo_pago != 0 \n"); 
			sql.append(" 		 	then coalesce((select top 1 cta_cargo from config_contable where no_folio_det_pag in ( \n");
			sql.append(" 		 				select no_folio_det from movimiento where grupo_pago = "+ noFolioDet +")), '') \n");
			sql.append(" 		 else coalesce((select top 1 cta_cargo from config_contable where no_folio_det = m.no_folio_det or no_folio_det_pag = m.no_folio_det), '') \n");
			sql.append(" 	end as ctaProveedor \n");
			sql.append(" FROM movimiento m, cat_equivalencia_cta_contable c \n");
			sql.append(" WHERE m.id_chequera = c.id_Chequera \n");
			
			if(divisaDLS)
				sql.append(" 	And c.id_divisa = m.id_divisa \n");
			else
				sql.append(" 	And c.id_divisa = '' \n");
			
			sql.append(" 	And no_folio_det in ("+ noFolioDet +") \n");
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public InterfazDto mapRow(ResultSet rs, int idx)throws SQLException{
					InterfazDto campos = new InterfazDto();
					campos.setCtaContaBanco(rs.getString("ctaBanco"));
					campos.setCtaContaProv(rs.getString("ctaProveedor"));
					return campos;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString() + "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:ctasContables");
		}
		return list;
	}
	
	public int seleccionarFolioReal(String tipoFolio) {
		StringBuffer sql = new StringBuffer();
		int res = 0;
		
		try {
			sql.append(" SELECT num_folio \n");
			sql.append(" FROM folio \n");
			sql.append(" WHERE tipo_folio = '" + tipoFolio + "'");
			
			res = jdbcTemplate.queryForInt(sql.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()+ "P:BE, C:ConsultasDao, M:seleccionarFolioReal");
			return -1;
		}
		return res;
	}

	public int actualizarFolioReal(String tipoFolio) {
		StringBuffer sql = new StringBuffer();
		int res = 0;
		
		try {
			sql.append(" UPDATE folio \n");
			sql.append(" SET num_folio = num_folio + 1 \n");
			sql.append(" WHERE tipo_folio = '" + tipoFolio + "'");
			
			res = jdbcTemplate.update(sql.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BE, C:ConsultasGenerales, M:actualizarFolioReal");
			return -1;
		}
		return res;
	}
	
	public List<InterfazDto> buscaDeptoCCProyect(List<InterfazDto> listMovtos, int i) {
		List<InterfazDto> list = new ArrayList<InterfazDto>();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT top 1 id_departamento, centro_costo, id_proyecto \n");
			sql.append(" FROM config_contable \n");
			sql.append(" WHERE ");
			
			if(Integer.parseInt(listMovtos.get(i).getIdTipoOperacion()) == 3200 && listMovtos.get(i).getGrupoPago() != 0)
				sql.append(" no_folio_det_pag in (select no_folio_det from movimiento where grupo_pago = "+ listMovtos.get(i).getGrupoPago() +") \n");
			else
				sql.append(" no_folio_det = "+ listMovtos.get(i).getNoFolioDet() +" or no_folio_det_pag = "+ listMovtos.get(i).getNoFolioDet() +" \n");
			
			sql.append(" ORDER BY case when id_proyecto != 0 then id_proyecto \n");
			sql.append(" 				when id_departamento != 0 then id_departamento \n"); 
			sql.append(" 				when centro_costo != 0 then centro_costo \n"); 
			sql.append(" 		end desc \n");
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public InterfazDto mapRow(ResultSet rs, int idx)throws SQLException{
					InterfazDto campos = new InterfazDto();
					campos.setIdDepto(rs.getInt("id_departamento"));
					campos.setCentroCosto(rs.getInt("centro_costo"));
					campos.setIdProyecto(rs.getInt("id_proyecto"));
					return campos;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString() + "P:ConciliacionBancoSet, C:ConciliacionBancoSetDaoImpl, M:buscaDeptoCCProyect");
		}
		return list;
	}
	
	public int buscaComisionesAgrup(int noFolioDet) {
		StringBuffer sql = new StringBuffer();
		int res = 0;
		
		try {
			sql.append(" SELECT count(*) \n");
			sql.append(" FROM zexp_fact \n");
			sql.append(" WHERE no_folio_set = " + noFolioDet + "");
			
			res = jdbcTemplate.queryForInt(sql.toString());
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()+ "P:BE, C:ConsultasDao, M:buscaComisionesAgrup");
			return -1;
		}
		return res;
	}
	
	/*
	public int colocaCtaCargo(List<InterfazDto> listMovtos, int i, List<InterfazDto> ctasConta) {
		StringBuffer sql = new StringBuffer();
		int res = 0;
		
		try {
			sql.append(" UPDATE zexp_fact \n");
			sql.append(" SET cargo = '"+ ctasConta.get(0).getCtaContaProv() +"' \n");
			sql.append(" WHERE folio_ref = "+ listMovtos.get(i).getGrupoPago() +" ");
			
			res = jdbcTemplate.update(sql.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BE, C:ConsultasGenerales, M:colocaCtaCargo");
			return -1;
		}
		return res;
	}
	*/
}
