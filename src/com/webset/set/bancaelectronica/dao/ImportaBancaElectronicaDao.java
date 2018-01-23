package com.webset.set.bancaelectronica.dao;

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
import org.springframework.jdbc.object.StoredProcedure;

import com.webset.set.bancaelectronica.dto.CatAfiliacionIngresosDto;
import com.webset.set.bancaelectronica.dto.CatArmaIngresoDto;
import com.webset.set.bancaelectronica.dto.CatBancoDto;
import com.webset.set.bancaelectronica.dto.CatCtaBancoDto;
import com.webset.set.bancaelectronica.dto.CatDivisionDto;
import com.webset.set.bancaelectronica.dto.CatReferenciaChequeraDto;
import com.webset.set.bancaelectronica.dto.CatReferenciaIngresosDto;
import com.webset.set.bancaelectronica.dto.EquivaleConceptoDto;
import com.webset.set.bancaelectronica.dto.MovimientoDto;
import com.webset.set.bancaelectronica.dto.ParamBusImpBancaDto;
import com.webset.set.bancaelectronica.dto.ParamCompraVentaDivisaDto;
import com.webset.set.bancaelectronica.dto.ParamConceptoDescDto;
import com.webset.set.bancaelectronica.dto.ParamDeterminaCorrespondeDto;
import com.webset.set.bancaelectronica.dto.ParamReglaConceptoDto;
import com.webset.set.bancaelectronica.dto.ParamRetImpBancaDto;
import com.webset.set.bancaelectronica.dto.ParametroDto;
import com.webset.set.bancaelectronica.dto.PersonasDto;
import com.webset.set.bancaelectronica.dto.SeleccionGeneralDto;
import com.webset.set.seguridad.dto.EmpresaDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.GeneradorDto;

//import sun.org.mozilla.javascript.internal.regexp.SubString;

/**
 * 
 * @author Cristian Garcia Garcia
 *@since 30/11/2010
 */
public class ImportaBancaElectronicaDao {
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora =new Bitacora();
	private String gsDBM="SQL SERVER";
	ConsultasGenerales consultasGenerales;
	Funciones funciones=new Funciones();
	private static Logger logger = Logger.getLogger(ImportaBancaElectronicaDao.class);
	
	public String consultarConfiguraSet(int indice) 
	{
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
	}
	
	public int seleccionarFolioReal(String tipoFolio) {
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.seleccionarFolioReal(tipoFolio);
	}
	public void actualizarFolioReal(String tipoFolio)
	{
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		consultasGenerales.actualizarFolioReal(tipoFolio);		
	}
	
	public Date obtenerFechaHoy() {
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.obtenerFechaHoy();
	}
	
	public Map<String, Object> ejecutarGenerador(GeneradorDto dto){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.ejecutarGenerador(dto);
	}
	
	public Map ejecutarRevividor(String psRevividor, int noFolioDet, int idTipoOperacion,String psTipoCancelacion,
			String idEstatusMov, String psOrigenMov, int idFormaPago, String bEntregado, String idTipoMovto, double importe,
			int noEmpresa, int noCuenta, String idChequera, int idBanco,int idUsuario, String noDocto, int lote,
			String bSalvoBuenCobro, String fecConfTrans, String idDivisa, String psResultado, boolean pbAutomatico)
	{
		int res = 0;
		Map resultado = new HashMap();
		
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		res = consultasGenerales.ejecutarRevividorOR(psRevividor, noFolioDet, idTipoOperacion, psTipoCancelacion,
				idEstatusMov, psOrigenMov, idFormaPago, bEntregado, idTipoMovto, importe, noEmpresa, noCuenta, 
				idChequera, idBanco,idUsuario, noDocto, lote, bSalvoBuenCobro, fecConfTrans, idDivisa, 
				psResultado, pbAutomatico);
		
		if(res > 0)
			resultado.put("mensage", "Registro Eliminado corretamente!!");
		else
			resultado.put("mensage", "Error al tratar de eliminar el registro!!");
		
		return resultado;
	}
	// FunSQLCombo365 
	@SuppressWarnings("unchecked")
	public List<CatBancoDto> obtenerBancosCombo(boolean pbInversion)
	{
		List<CatBancoDto> bancos=new ArrayList<CatBancoDto>();
		String sql="";
	    sql += " SELECT c.id_banco as ID, c.desc_banco as Descrip ";
	    sql +=" FROM cat_banco c ,cat_cta_banco cb ";
	    sql += " WHERE c.id_banco = cb.id_banco ";
	    if(pbInversion)
	        sql+=" and cb.tipo_chequera in ('I') ";
	    else
	        sql+=" and cb.tipo_chequera in ('C','P','M') ";
	    
	    sql+=" GROUP BY c.id_banco,c.desc_banco ";
	    try{
			bancos = jdbcTemplate.query(sql, new RowMapper(){
			public CatBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatBancoDto ban = new CatBancoDto();
				ban.setIdBanco(rs.getInt("ID"));
				ban.setDescBanco(rs.getString("Descrip"));
				return ban;
			}});
		}
	catch(Exception e){
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:obtenerBancosCombo");
	}  
	return bancos;
	}
	//Public Function FunSQLCombo366 Se agrega par�metro pb_TipoCheq para seleccionar solo chequeras Concentradoras
	@SuppressWarnings("unchecked")
	public List<CatCtaBancoDto>obtenerChequeras(CatCtaBancoDto dto,boolean pbTipoChequera, boolean pbInversion)
	{
		List<CatCtaBancoDto> chequeras=new ArrayList<CatCtaBancoDto>();
		String sql="";
        	try{
        	sql +=" SELECT id_chequera as ID, desc_chequera as Descrip ";
        	sql +=" FROM cat_cta_banco ";
        	if(dto.getNoEmpresa()>0)
        		sql +=" WHERE  no_empresa = "+dto.getNoEmpresa();
        	if(dto.getIdBanco()>0)
        		sql+=" AND id_banco= "+dto.getIdBanco();
        	
        	if(pbTipoChequera)
        	sql +=" AND tipo_chequera = 'C' ";
        	if(pbInversion) 	
        	sql +=" AND tipo_chequera = 'I' ";
        	else if(!pbInversion)
        	sql +=" AND tipo_chequera not in ('I') ";
        	
			chequeras= jdbcTemplate.query(sql, new RowMapper(){
			public CatCtaBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatCtaBancoDto cheq = new CatCtaBancoDto();
				cheq.setIdChequera(rs.getString("ID"));
				cheq.setDescChequera(rs.getString("Descrip"));
				return cheq;
			}});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:obtenerChequeras");
		}  
		return chequeras;
	}
	@SuppressWarnings("unchecked")
	public List<EmpresaDto>obtenerEmpresas()
	{
		List<EmpresaDto>empresas=new ArrayList<EmpresaDto>();
		String sql="";
		sql=" SELECT * FROM EMPRESA ";
		try
		{
		empresas= jdbcTemplate.query(sql, new RowMapper(){
			public EmpresaDto mapRow(ResultSet rs, int idx) throws SQLException {
				EmpresaDto emp = new EmpresaDto();
				emp.setNoEmpresa(rs.getInt("no_empresa"));
				emp.setNomEmpresa(rs.getString("nom_empresa"));
				return emp;
			}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:obtenerEmpresas");
		} 
		return empresas;
	}
	
	/*Public Function FunSQLComboConcepto(ByVal pi_banco1 As Integer, ByVal pi_banco2 As Integer, ByVal psTipo_movto As String) As ADODB.Recordset
    'Recibe los par�metros necesarios para ejecutar el llena_combo_where de la forma frmTransIng*/
	
	@SuppressWarnings("unchecked")
	public List<EquivaleConceptoDto>obtenerConcepto(int bancoUno,int bancoDos,String psTipoMovto)
	{
		List<EquivaleConceptoDto>equivaleDto=new ArrayList<EquivaleConceptoDto>();
		String sql="";
		try{
			sql+=" SELECT  0 as ID, concepto_set as Descrip ";
			sql+=" FROM equivale_concepto ";
			sql+=" WHERE id_banco in ( "+bancoUno+","+bancoDos+") ";
			
			if(psTipoMovto!=null && !psTipoMovto.trim().equals("A"))
				sql+=" and cargo_abono='"+psTipoMovto+"' ";
			
			sql+=" GROUP BY concepto_set order by concepto_set";
			equivaleDto= jdbcTemplate.query(sql, new RowMapper(){
				public EquivaleConceptoDto mapRow(ResultSet rs, int idx) throws SQLException {
					EquivaleConceptoDto equi = new EquivaleConceptoDto();
					equi.setConceptoSet(rs.getString("Descrip"));
					return equi;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:obtenerConcepto");
		} 
		return equivaleDto;
	}

/*Public Function FunSQLSelect862(ByVal pvOption As Variant, ByVal empresa As Variant, _
            ByVal pbTodasEmp As Boolean, ByVal psTipo_movto As String, _
            ByVal plNoUsuario As Long, ByVal plBanco As Long, _
            ByVal psChequera As String, ByVal pdMontoIni As Double, _
            ByVal pdMontoFin As Double, ByVal psFechaValorIni As String, _
            ByVal psFechaValorFin As String, ByVal psconcepto As String, _
            Optional pb_reporte As Boolean, _
            Optional pbTipoCheq As Boolean, Optional pbInversion As Boolean) As ADODB.Recordset
Dim sSQL As String
Dim i As Long
Dim psFecha As String
Dim piBanco As Integer
On Error GoTo HayError*/
	
	@SuppressWarnings("unchecked")
	public List<ParamRetImpBancaDto> consultarMovimientoBancaE(ParamBusImpBancaDto dto, String gsDBM)
	{
		String sql="";
		List <ParamRetImpBancaDto> paramRetorno=new ArrayList<ParamRetImpBancaDto>();
		logger.info("tipo movto "+dto.getPsTipoMovto());
	try{
	if(dto.isOptCapturados()) //Movimientos de BE CAPTURADOS
		{
			if(gsDBM.trim().equals("ORACLE"))
			{
				
			}
			else if((gsDBM.trim().equals("SQL SERVER"))||(gsDBM.trim().equals("SYBASE")))
			{
				sql="";
				sql+=" SELECT distinct ";
				sql+=" m.descripcion,q.b_rechazo,'S' as despliega,'S' as importa,c.libera_aut,q.concepto_set as desc_concepto, ";
				sql+=" c.b_banca_elect,q.concepto_set as concepto,q.concepto_set, m.referencia, ";
				sql+=" m.importe, m.fec_valor,m.sucursal,m.secuencia,m.observacion, ";
				sql+=" m.folio_banco, m.id_banco,m.id_chequera, c.desc_banco, ";
				sql+=" case when m.b_salvo_buen_cobro = 'S' then 'N' else 'S'end as b_salvo_buen_cobro, ";
				sql+=" m.cargo_abono,m.concepto as concepto2, m.no_empresa, ";
				sql+=" e.nom_empresa,m.secuencia,e.no_cuenta_emp, ";
				sql+=" m.id_rubro ";
				if(gsDBM.trim().equals("SYBASE"))
				{
					
					sql+= ",case when m.cargo_abono = 'I' then";
					sql+= " (Select case when max(r.id_chequera) is null then 'N' else 'S' end ";
					sql+= " from  cat_referencia_chequera r where r.no_empresa = m.no_empresa ";
					sql+= " and r.id_banco = r.id_banco and r.id_chequera = m.id_chequera) ";
					sql+= " Else 'N' end as valida_referencia ";
					sql+= ",case when m.cargo_abono = 'I' then ";
					sql+= " (Select case when max(r.id_chequera) is null then 'N' else r.b_valida_cheq end ";
					sql+= " from cat_referencia_chequera r where r.no_empresa = m.no_empresa ";
					sql+= " and r.id_banco = r.id_banco and r.id_chequera = m.id_chequera group by r.id_chequera, r.b_valida_cheq ) ";
					sql+= " Else 'N' end as valida_cheq_ref ";
				}else{
					sql+= " ,'N' as valida_referencia ";
					sql+= " ,'N' as valida_cheq_ref ";
				}
				
				sql+= " FROM movto_banca_e m,cat_banco c,empresa e,equivale_concepto q ";
				if(dto.isPbTipoCheq())// Se agrega par�metro pb_TipoCheq para seleccionar solo chequeras Concentradoras
					sql+=", cat_cta_banco ccb";
				sql+= " WHERE m.id_banco = c.id_banco ";
				sql+= " AND (m.id_banco = q.id_banco OR q.id_banco = 0) ";
				if (dto.getPsTipoMovto()!=null && !dto.getPsTipoMovto().trim().equals("A"))
					sql +=" AND m.cargo_abono = '"+dto.getPsTipoMovto().trim()+"' ";
				sql+=" AND m.cargo_abono = q.cargo_abono ";
				sql+=" AND m.id_estatus_trasp = 'N' ";
				sql+=" AND m.movto_arch is null ";
				sql+=" AND e.no_empresa = m.no_empresa ";
				sql+=" AND m.concepto =  q.concepto_set ";
				if(!dto.isPbTodasEmp())
					sql+=" AND m.no_empresa = " +dto.getNoEmpresa();
				else{
					sql+= " and m.no_empresa in(select u.no_empresa ";
			 		sql+= "      from usuario_empresa u ";
					sql+= "      where u.no_usuario = "+dto.getPlNoUsuario()+")";
				}
				
			}
			if((gsDBM.trim().equals("POSTGRESQL"))||(gsDBM.trim().equals("DB2")))
			{
				sql = "";
				sql+= " SELECT distinct ";
				sql+= " coalesce(m.observacion, '') as descripcion ,q.b_rechazo,'S' || '' as despliega,'S' || '' as importa,c.libera_aut,q.concepto_set as desc_concepto,";
				sql+= " c.b_banca_elect,q.concepto_set as concepto,q.concepto_set, ";
				sql+= " m.referencia,m.importe, m.fec_valor, m.sucursal,m.secuencia,";
				sql+= " m.observacion,m.folio_banco, m.id_banco, m.id_chequera,c.desc_banco,";
				sql+= " case when m.b_salvo_buen_cobro = 'S' then 'N' else 'S'end as b_salvo_buen_cobro, ";
				sql+= " m.cargo_abono, m.concepto as concepto2, m.no_empresa,";
				sql+= " e.nom_empresa,m.secuencia,e.no_cuenta_emp,";
				sql+= " m.id_rubro ";
	
				sql+= ",case when m.cargo_abono = 'I' then";
				sql+= "   (Select case when max(r.id_chequera) is null then 'N' else 'S' end";
				sql+= "         from  cat_referencia_chequera r where r.no_empresa = m.no_empresa";
				sql+= "         and r.id_banco = r.id_banco and r.id_chequera = m.id_chequera)";
				sql+= " Else 'N' end as valida_referencia";
	
				sql+= ",case when m.cargo_abono = 'I' then";
				sql+= "   (Select case when max(r.id_chequera) is null then 'N' else r.b_valida_cheq end";
				sql+= "         from  cat_referencia_chequera r where r.no_empresa = m.no_empresa";
				sql+= "         and r.id_banco = r.id_banco and r.id_chequera = m.id_chequera group by r.id_chequera, r.b_valida_cheq )";
				sql+= " Else 'N' end as valida_cheq_ref";
	
				sql+= " FROM movto_banca_e m,cat_banco c,empresa e,equivale_concepto q";
	
				if(dto.isPbTipoCheq()||dto.isPbInversion())//ACM 27/02/06 Se agrega par�metro pb_TipoCheq para seleccionar solo chequeras Concentradoras
					sql+=" , cat_cta_banco ccb ";
				sql+= " WHERE m.id_banco = c.id_banco ";
				sql+= " AND (m.id_banco = q.id_banco OR q.id_banco = 0) ";
				if(dto.getPsTipoMovto()!=null && !dto.getPsTipoMovto().trim().equals("A"))
					sql+= " AND m.cargo_abono = '"+dto.getPsTipoMovto()+"' ";
				sql+= " AND m.cargo_abono = q.cargo_abono ";
				sql+= " AND m.id_estatus_trasp = 'N' ";
				sql+= " AND m.movto_arch is null ";
				sql+= " AND e.no_empresa = m.no_empresa ";
				sql+= " AND m.concepto = q.concepto_set ";
				if(!dto.isPbTodasEmp())
					sql+= " AND m.no_empresa = "+dto.getNoEmpresa();
				else{
					sql+= " and m.no_empresa in(select u.no_empresa ";
					sql+= " from usuario_empresa u ";
					sql+= " where u.no_usuario = "+dto.getPlNoUsuario()+")";
				}
	
				if(dto.isPbInversion()){
					sql+= " and ccb.id_chequera = m.id_chequera ";
					sql+= " and ccb.id_banco = m.id_banco ";
					sql+= " and ccb.tipo_chequera in ('I') ";
				}

			}
			
		}else{//Movimientos de BE IMPORTADOS
			if(gsDBM.trim().equals("ORACLE"))
			{
				
			}
			else if(gsDBM.trim().equals("SQL SERVER")||gsDBM.trim().equals("SYBASE"))
			{
				sql="";
				sql+=" SELECT distinct ";
				sql+=" m.descripcion,q.b_rechazo,q.despliega,q.importa,c.libera_aut,c.b_banca_elect,";
				sql+=" q.desc_concepto,q.concepto_set as concepto, m.referencia,";
				sql+=" m.importe, m.fec_valor, m.sucursal,m.secuencia,";
				sql+=" m.observacion, m.folio_banco, m.id_banco, m.id_chequera, c.desc_banco,";
				sql+=" case when m.b_salvo_buen_cobro = 'S' then 'N' else 'S'end as b_salvo_buen_cobro,";
				sql+=" m.cargo_abono, m.concepto as concepto2, m.no_empresa,";
				sql+=" e.nom_empresa, m.secuencia, e.no_cuenta_emp ";
				sql+=" ,m.id_rubro";
				    
				sql+=",case when m.cargo_abono = 'I' then";
				sql+="   (Select case when max(r.id_chequera) is null then 'N' else 'S' end";
				sql+="         from  cat_referencia_chequera r where r.no_empresa = m.no_empresa";
				sql+="         and r.id_banco = r.id_banco and r.id_chequera = m.id_chequera)";
				sql+=" Else 'N' end as valida_referencia";

				sql+=",case when m.cargo_abono = 'I' then";
				sql+="   (Select case when max(r.id_chequera) is null then 'N' else r.b_valida_cheq end";
				sql+="         from  cat_referencia_chequera r where r.no_empresa = m.no_empresa";
				sql+="         and r.id_banco = r.id_banco and r.id_chequera = m.id_chequera group by r.id_chequera, r.b_valida_cheq )";
				sql+=" Else 'N' end as valida_cheq_ref";

				sql+=" FROM movto_banca_e m, cat_banco c, equivale_concepto q,  empresa e";
				if(dto.isPbTipoCheq())//ACM 27/02/06 Se agrega parametro pb_TipoCheq para seleccionar solo chequeras Concentradoras
					sql+=", cat_cta_banco ccb";
				sql+=" WHERE m.id_banco = c.id_banco";
				sql+=" AND m.id_estatus_trasp = 'N'  ";
				sql+=" AND m.movto_arch is not null  ";
				sql+=" AND m.id_banco = q.id_banco";
				sql+=" AND m.cargo_abono=q.cargo_abono ";
				if(dto.getPsTipoMovto()!=null && !dto.getPsTipoMovto().trim().equals("A"))
					sql+=" AND m.cargo_abono = '"+dto.getPsTipoMovto()+"' ";
				sql+=" AND e.no_empresa = m.no_empresa ";
				sql+=" AND m.concepto like q.desc_concepto + '%' ";
				if(!dto.isPbTodasEmp())
					sql+=" AND m.no_empresa = " +dto.getNoEmpresa();
				else{
					sql+=" and m.no_empresa in(select u.no_empresa ";
					sql+=" from usuario_empresa u ";
					sql+=" where u.no_usuario = "+dto.getPlNoUsuario()+")";
				}

			}else if(gsDBM.trim().equals("POSTGRESQL")||gsDBM.trim().equals("DB2")){
				sql = "";
				sql+= " SELECT distinct ";
				sql+= " m.descripcion, q.b_rechazo,q.despliega,q.importa,c.libera_aut,c.b_banca_elect,";
				sql+= " q.desc_concepto,q.concepto_set as concepto, m.referencia,";
				sql+= " m.importe, m.fec_valor, m.sucursal, m.secuencia,m.observacion,";
				sql+= " m.folio_banco, m.id_banco, m.id_chequera, c.desc_banco,";
				sql+= " case when m.b_salvo_buen_cobro = 'S' then 'N' else 'S'end as b_salvo_buen_cobro,";
				sql+= " m.cargo_abono, m.concepto as concepto2, m.no_empresa,";
				sql+= " e.nom_empresa, m.secuencia,e.no_cuenta_emp";
				sql+= " ,m.id_rubro ";

				sql+= ",case when m.cargo_abono = 'I' then";
				sql+= "   (Select case when max(r.id_chequera) is null then 'N' else 'S' end";
				sql+= "         from  cat_referencia_chequera r where r.no_empresa = m.no_empresa";
				sql+= "         and r.id_banco = r.id_banco and r.id_chequera = m.id_chequera)";
				sql+= " Else 'N' end as valida_referencia";

				sql+= ",case when m.cargo_abono = 'I' then";
				sql+= "   (Select case when max(r.id_chequera) is null then 'N' else r.b_valida_cheq end";
				sql+= "         from  cat_referencia_chequera r where r.no_empresa = m.no_empresa";
				sql+= "         and r.id_banco = r.id_banco and r.id_chequera = m.id_chequera group by r.id_chequera, r.b_valida_cheq )";
				sql+= " Else 'N' end as valida_cheq_ref";

				sql+= " FROM movto_banca_e m, cat_banco c, equivale_concepto q,  empresa e";
				if(dto.isPbTipoCheq()) //Se agrega parametro pb_TipoCheq para seleccionar solo chequeras Concentradoras
					sql+= " , cat_cta_banco ccb ";
				sql+= " WHERE m.id_banco = c.id_banco ";
				sql+= " AND m.id_estatus_trasp = 'N' ";
				sql+= " AND m.movto_arch is not null ";
				sql+= " AND m.id_banco = q.id_banco";
				sql+= " AND m.cargo_abono=q.cargo_abono ";

				if(dto.getPsTipoMovto()!=null && !dto.getPsTipoMovto().trim().equals("A"))
					sql+= " AND m.cargo_abono = '"+dto.getPsTipoMovto()+"' ";
				sql+= " AND e.no_empresa = m.no_empresa ";
				sql+= " AND m.concepto = q.desc_concepto ";
				if(!dto.isPbTodasEmp())
					sql+= " AND m.no_empresa = "+ dto.getNoEmpresa();
				else{
					sql+= " and m.no_empresa in(select u.no_empresa ";
					sql+= "      from usuario_empresa u ";
					sql+= "      where u.no_usuario = "+dto.getPlNoUsuario()+")";	
				}
			}
		}
	
		if(dto.getPlBanco()>0)
			sql+=" and m.id_banco = "+dto.getPlBanco();
		if(dto.isPbTipoCheq()) //Se agrega par�metro pb_TipoCheq para seleccionar solo chequeras Concentradoras
			sql+=" AND (ccb.id_banco = m.id_banco AND ccb.tipo_chequera = 'C') ";
		if(dto.getPlBanco()==2)
		{
			if(gsDBM.trim().equals("POSTGRESQL"))
				sql+=" and POSITION ('AUT.' in m.referencia) = 0 ";
				else if(gsDBM.trim().equals("DB2"))
				sql+=" and locate('AUT.', m.referencia) = 0 ";
				else{
				sql+=" and PATINDEX('%AUT.%', m.referencia) = 0 ";
				}
		}
		int sqlComplemento=0;
		if(dto.getPsChequera()!=null && !dto.getPsChequera().trim().equals(""))
			sql+=" and m.id_chequera = '"+dto.getPsChequera()+"'";
	
		if(dto.getPdMontoIni()>0 || dto.getPdMontoFin()>0)
			sql+=" and m.importe between "+dto.getPdMontoIni()+" and " +dto.getPdMontoFin();
		
		if((dto.getPsFechaValorIni()!=null && !dto.getPsFechaValorIni().equals(""))||(dto.getPsFechaValorIni()!=null && !dto.getPsFechaValorFin().equals("")))
		{
			sql+=" and m.fec_valor between '"+dto.getPsFechaValorIni()+"'" ;
			sql+=" and '"+dto.getPsFechaValorFin()+" 23:59"+"'";
		}
		if(dto.getPsConcepto()!=null && !dto.getPsConcepto().trim().equals(""))
		sql+=" and q.concepto_set = '"+dto.getPsConcepto()+"'";

		if(gsDBM.trim().equals("POSTGRESQL"))
		{
			sql+= " AND  (q.despliega = 'S' or (q.despliega = 'N' and concepto ='5423' ";
			sql+= " and '0' + substring(descripcion,25,10) not in (select id_chequera from cat_cta_banco)) ";
			sql+= " OR (concepto in ('6100') and (descripcion like '%IVA%' or descripcion like '%COM%' or descripcion = '') ";
			sql+= " OR (concepto =7100 and descripcion not like '%AUT%')) ";
			sql+= " OR (m.id_banco=2 and m.concepto =1100 and m.referencia <>'0074604034')) ";
			sql+= " AND (rtrim (ltrim(m.concepto)) ";
			sql+= " not in ('INTERBANCARIO ENVIADO','SPEUA ENVIADO')) ";
			sql+= " and referencia not like '%70005%'";
	
			sql+= " order by m.id_banco, m.no_empresa ";
		}else if(gsDBM.trim().equals("DB2")) 
		{
				sql+= " AND  (q.despliega = 'S' ";
				if((dto.getPlBanco()==2) && (!dto.isOptCapturados()) )
				{
				sql+= " or (q.despliega = 'N' and concepto ='5423' ";
				sql+= " and '0' || substring(descripcion,25,10) not in (select id_chequera from cat_cta_banco))";
				sql+= " OR (concepto in ('6100') and (descripcion like '%IVA%' or descripcion like '%COM%' or descripcion = '')";
				sql+= " OR (concepto =7100 and descripcion not like '%AUT%'))";
				}
			sql+= " )";
	
			sql+= " AND (rtrim (ltrim(m.concepto)) ";
			sql+= " not in ('INTERBANCARIO ENVIADO','SPEUA ENVIADO')) ";
			sql+= " and referencia not like '%70005%'";
			sqlComplemento=complementoMovimientoBancaElectronica(dto,gsDBM);
			if(sqlComplemento>0)
				sql+= "  And secuencia not in ("+sqlComplemento+") ";
			if((dto.getPlBanco()==1026) && (!dto.isOptCapturados()))
			{
			// para citibank, que no tome las inversiones de este banco
			//sql+= " AND (folio_banco NOT LIKE 'S0%') "
			}
		}
		else//MAS 07 feb 2007 vamos a verificar el siguiente query ya que en cie esta algo similar pero no tenemos la certeza que aplique en todos lados asi
		{
			sql+= " AND  (q.despliega = 'S' or (q.despliega = 'N' and concepto ='5423'";
			sql+= " and '0' + substring(descripcion,25,10) not in (select id_chequera from cat_cta_banco))";
			sql+= " OR (concepto in ('6100') and (descripcion like '%IVA%' or descripcion like '%COM%' or descripcion = '')";
			sql+= " )";
			sql+= " )"; // que onda con esto por que esta clavado revisar;
	
			sql+= " AND (rtrim (ltrim(m.concepto)) ";
			sql+= " not in ('INTERBANCARIO ENVIADO','SPEUA ENVIADO')) ";
			sql+= " and referencia not like '%70005%'";
			//esto es para que no tome los movimientos que ya existen en set
			//El subquery que se ejecutaba fue cambiado por la funcion FunSQLSelect862a()
			sqlComplemento=complementoMovimientoBancaElectronica(dto,gsDBM);
			//logger.info("valor Complemento"+sqlComplemento);
			if(sqlComplemento>0)
				sql+= "  And secuencia not in ("+sqlComplemento+") ";
			sql+= "order by m.id_banco, m.no_empresa ";
		}
		System.out.println("consulta banca "+sql);
	 logger.info("Query Coonsulta banca: "+sql);
			paramRetorno = jdbcTemplate.query(sql, new RowMapper(){
			public ParamRetImpBancaDto mapRow(ResultSet rs, int idx) throws SQLException {
				ParamRetImpBancaDto cons = new ParamRetImpBancaDto();
					cons.setDescripcion(rs.getString("descripcion"));
					cons.setBRechazo(rs.getString("b_rechazo"));
					cons.setDespliega(rs.getString("despliega"));
					cons.setImporta(rs.getString("importa"));
					cons.setLiberaAut(rs.getString("libera_aut"));
					cons.setBBancaElect(rs.getString("b_banca_elect"));
					cons.setDescConcepto(rs.getString("desc_concepto"));
					cons.setConceptoSet(rs.getString("concepto"));
					cons.setConcepto(rs.getString("concepto2"));
					cons.setReferencia(rs.getString("referencia"));
					cons.setImporte(rs.getDouble("importe"));
					cons.setFecValor(rs.getDate("fec_valor"));
					cons.setSucursal(rs.getString("sucursal"));
					cons.setSecuencia(rs.getInt("secuencia"));
					cons.setObservacion(rs.getString("observacion"));
					cons.setFolioBanco(rs.getString("folio_banco"));
					cons.setIdBanco(rs.getInt("id_banco"));
					cons.setIdChequera(rs.getString("id_chequera"));
					cons.setDescBanco(rs.getString("desc_banco"));
					cons.setBSalvoBuenCobro(rs.getString("b_salvo_buen_cobro"));
					cons.setCargoAbono(rs.getString("cargo_abono"));
					cons.setConcepto2(rs.getString("concepto2"));
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setNomEmpresa(rs.getString("nom_empresa"));
					//La secuencia se repite en el query
					cons.setNoCuentaEmp(rs.getInt("no_cuenta_emp"));
					cons.setIdRubro(rs.getInt("id_rubro"));
					cons.setValidaReferencia(rs.getString("valida_referencia"));
					cons.setValidaCheqRef(rs.getString("valida_cheq_ref"));
				return cons;
			}});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:consultarMovimientoBancaE");
		} 
		return paramRetorno ;
	}
	/*Private Function FunSQLSelect862a(ByVal empresa As Variant, ByVal pbTodasEmp As Boolean, _
        ByVal plNoUsuario As Long, ByVal plBanco As Long, pbTipoCheq As Boolean, _
        ByVal psChequera As String, ByVal pdMontoIni As Double, ByVal pdMontoFin As Double, _
        ByVal psFechaValorIni As String, ByVal psFechaValorFin As String) As String*/
  @SuppressWarnings("unchecked")
public int complementoMovimientoBancaElectronica(ParamBusImpBancaDto dto, String gsDBM)
  {
    String sql="";
    int res=-1;
    List<String>listSecuencia=null;
    try{
		    sql+= "Select secuencia from movto_banca_e b, cat_cta_banco c, movimiento m ";
		    sql+= "Where ";
		    if(!dto.isPbTodasEmp())
		        sql+= "b.no_empresa = "+dto.getNoEmpresa();
		    else{
		        sql+= "b.no_empresa in (select u.no_empresa ";
		        sql+= "from usuario_empresa u ";
		        sql+= "where u.no_usuario = "+dto.getPlNoUsuario()+")";
		  	}
		    if (dto.getPlBanco()>0){ 
		        sql+= "   and b.id_banco = " +dto.getPlBanco();
		        if(dto.isPbTipoCheq())
		            sql+= " AND c.tipo_chequera = 'C'";
		  	}
		    if(dto.getPsChequera()!=null && !dto.getPsChequera().trim().equals(""))
		        sql+= " and b.id_chequera = '"+dto.getPsChequera().trim()+"'";
		    sql+= "   and b.no_empresa = c.no_empresa and b.id_banco = c.id_banco and b.id_chequera = c.id_chequera";
		    sql+= "   and b.no_empresa = m.no_empresa and b.id_banco = m.id_banco and b.id_chequera = m.id_chequera";
		    
		    if(gsDBM.trim().equals("DB2"))
		        sql+= "   and CAST(b.fec_valor AS DATE) = m.fec_valor and b.cargo_abono = m.id_tipo_movto and b.importe = m.importe";
		    else
		        sql+= "   and b.fec_valor = m.fec_valor and b.cargo_abono = m.id_tipo_movto and b.importe = m.importe";
		    
		    
		    sql+= "   and b.referencia = m.referencia and b.secuencia =  m.id_inv_cbolsa";
		    sql+= "   and (id_tipo_operacion = 3200 or id_tipo_operacion between 3700 and 3799 ";
		    sql+= "      or id_tipo_operacion between 3100 and 3199) and id_estatus_mov in ('P', 'I', 'R', 'K', 'A')";
		    sql+= "   and b.id_estatus_trasp = 'N'";
		    if(dto.getPdMontoIni()>0 || dto.getPdMontoFin()>0)
		        sql+= " and b.importe between "+dto.getPdMontoIni()+" and "+dto.getPdMontoFin();
		    
		    if((dto.getPsFechaValorIni()!=null && !dto.getPsFechaValorIni().trim().equals(""))&& (dto.getPsFechaValorFin()!=null && !dto.getPsFechaValorFin().trim().equals("")))
		        sql+= " and b.fec_valor between '"+dto.getPsFechaValorIni().trim()+ "'" +
		            " and '"+dto.getPsFechaValorFin()+" 23:59"+"'";
		    sql+= " Union All ";
		    sql+= "Select secuencia from movto_banca_e b, cat_cta_banco c, hist_movimiento m ";
		    sql+= "Where ";
		    if(!dto.isPbTodasEmp())
		        sql+= "b.no_empresa = "+dto.getNoEmpresa();
		    else{
		        sql+= "b.no_empresa in(select u.no_empresa ";
		        sql+= "from usuario_empresa u ";
		        sql+= "where u.no_usuario = "+dto.getPlNoUsuario()+")";
		    	}
		    if(dto.getPlBanco()>0){
		        sql+= " and b.id_banco = "+dto.getPlBanco();
		        if(dto.isPbTipoCheq())
		            sql+= " AND c.tipo_chequera = 'C'";
		  	}
		    if(dto.getPsChequera()!=null && !dto.getPsChequera().trim().equals(""))
		        sql+= " and b.id_chequera = '"+dto.getPsChequera().trim()+"'";
		    sql+= "   and b.no_empresa = c.no_empresa and b.id_banco = c.id_banco and b.id_chequera = c.id_chequera";
		    sql+= "   and b.no_empresa = m.no_empresa and b.id_banco = m.id_banco and b.id_chequera = m.id_chequera";
		    if(gsDBM.trim().equals("DB2"))
		        sql+= "   and CAST(b.fec_valor AS DATE) = m.fec_valor and cargo_abono = id_tipo_movto and b.importe = m.importe";
		    else
		        sql+= "   and b.fec_valor = m.fec_valor and cargo_abono = id_tipo_movto and b.importe = m.importe";
		    sql+= "   and b.referencia = m.referencia and b.secuencia =  m.id_inv_cbolsa";
		    sql+= "   and ( id_tipo_operacion = 3200 or id_tipo_operacion between 3700 and 3799  ";
		    sql+= "       or id_tipo_operacion between 3100 and 3199) and id_estatus_mov in ('P', 'I', 'R', 'K', 'A')";
		    sql+= "   and b.id_estatus_trasp = 'N'";
		    if(dto.getPdMontoIni()>0 || dto.getPdMontoFin()>0)
			  sql+= " and b.importe between "+dto.getPdMontoIni()+" and "+dto.getPdMontoFin();
		    if((!dto.getPsFechaValorIni().trim().equals(""))&& (!dto.getPsFechaValorFin().trim().equals("")))
		    	sql+= " and b.fec_valor between '"+dto.getPsFechaValorIni().trim()+ "'" +
		        " and '"+dto.getPsFechaValorFin()+" 23:59"+"'";
		    listSecuencia=jdbcTemplate.query(sql, new RowMapper(){
				public String mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getString("secuencia");
				}});
		    
	}catch(Exception e){
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:complementoMovimientoBancaElectronica");
	} 
    if(listSecuencia!=null && listSecuencia.size()>0)
    	res=Integer.parseInt(listSecuencia.get(0));
	
    return res;
  }
    /*Set rstSecuencias = New ADODB.Recordset
    Set rstSecuencias = gobjBD.obtenrecordset(sSQL)
    FunSQLSelect862a = "-1"
    i = 0
    Do While Not rstSecuencias.EOF
        If i = 10 Then
            FunSQLSelect862a = FunSQLSelect862a & ", " & Chr(10) & rstSecuencias(0)
            i = 0
        Else
            FunSQLSelect862a = FunSQLSelect862a & ", " & rstSecuencias(0)
        End If
        i = i + 1
        rstSecuencias.MoveNext
    Loop
    Exit Function*/
	 
/*Public Function FunSQLSelectImportaBancomer(ByVal empresa As Variant, _
                ByVal pbTodasEmp As Boolean, ByVal psTipo_movto As String, _
                ByVal plNoUsuario As Long, ByVal plBanco As Long, _
                ByVal psChequera As String, ByVal pdMontoIni As Double, _
                ByVal pdMontoFin As Double, ByVal psFechaValorIni As String, _
                ByVal psFechaValorFin As String, ByVal psconcepto As String, _
                Optional pb_reporte As Boolean, _
                Optional pbTipoCheq As Boolean, Optional pbInversion As Boolean) As ADODB.Recordset*/
	@SuppressWarnings("unchecked")
	public List<ParamRetImpBancaDto> consultarMovimientoBancaImportaBancomer(ParamBusImpBancaDto dto)
	{
		String sql="";
		List<ParamRetImpBancaDto> paramRetorno=new ArrayList<ParamRetImpBancaDto>();
		try{
			sql+= " SELECT distinct ";
			sql+= " coalesce(m.descripcion, '') as descripcion, q.b_rechazo, q.despliega, q.importa, c.libera_aut, c.b_banca_elect,";
			sql+= " q.desc_concepto, q.concepto_set as concepto, m.referencia, m.importe, m.fec_valor, m.sucursal, m.secuencia,";
			sql+= " m.observacion, m.folio_banco, m.id_banco, m.id_chequera, c.desc_banco, ";
			sql+= " case when m.b_salvo_buen_cobro = 'S' then 'N' else 'S'end as b_salvo_buen_cobro, ";
			sql+= " case when m.cargo_abono = 'I' then 'INGRESO' else 'EGRESO' end as cargo_abono, m.concepto as concepto2, m.no_empresa,";
			sql+= " e.nom_empresa, m.secuencia, e.no_cuenta_emp, m.id_rubro";
			
			sql+= ",case when m.cargo_abono = 'I' then";
			sql+= "   (Select case when max(r.id_chequera) is null then 'N' else 'S' end";
			sql+= "         from  cat_referencia_chequera r where r.no_empresa = m.no_empresa";
			sql+= "         and r.id_banco = r.id_banco and r.id_chequera = m.id_chequera)";
			sql+= " Else 'N' end as valida_referencia";
			
			sql+= ",case when m.cargo_abono = 'I' then";
			sql+= "   (Select case when max(r.id_chequera) is null then 'N' else r.b_valida_cheq end";
			sql+= "         from  cat_referencia_chequera r where r.no_empresa = m.no_empresa";
			sql+= "         and r.id_banco = r.id_banco and r.id_chequera = m.id_chequera group by r.id_chequera, r.b_valida_cheq )";
			sql+= " Else 'N' end as valida_cheq_ref, m.id_cve_concepto as cveConceptoBanco, m.concepto as conceptoBancario";
			
			sql+= " FROM movto_banca_e m, cat_banco c, equivale_concepto q,  empresa e";
			
			if(dto.isPbTipoCheq()||dto.isPbInversion()) //Se agrega par�metro pb_TipoCheq para seleccionar solo chequeras Concentradoras
				sql+= ", cat_cta_banco ccb";
			
			sql+= " WHERE m.id_banco = c.id_banco";
			sql+= " AND m.id_estatus_trasp = 'N'  ";
			sql+= " AND m.movto_arch is not null  ";
			sql+= " AND m.id_banco = q.id_banco";
			sql+= " AND m.cargo_abono=q.cargo_abono ";
			
			if(dto.getPsTipoMovto()!=null && !dto.getPsTipoMovto().trim().equals("A"))
				sql+= " AND m.cargo_abono = '"+dto.getPsTipoMovto().trim()+"' ";
			
			sql+= " AND e.no_empresa = m.no_empresa ";
			sql+= " AND (m.id_cve_concepto = q.id_cve_leyenda AND q.id_cve_leyenda <> '' ";
			sql+= "       OR (m.concepto = q.desc_concepto and q.id_cve_leyenda = '' and m.id_cve_concepto = ''))";
			
			if(!dto.isPbTodasEmp())
				sql+= " AND m.no_empresa = "+dto.getNoEmpresa();
			else
			{
				sql+= " and m.no_empresa in(select u.no_empresa ";
				sql+= "      from usuario_empresa u ";
				sql+= "      where u.no_usuario = "+dto.getPlNoUsuario()+")";
			}
			
			if(dto.getPlBanco()>0)
			{
				sql+= " and m.id_banco = "+dto.getPlBanco();
				if(dto.isPbTipoCheq()) //Se agrega par�metro pb_TipoCheq para seleccionar solo chequeras Concentradoras
					sql+="  AND (ccb.id_banco = m.id_banco AND ccb.tipo_chequera = 'C')";
			}
			if(dto.getPsChequera()!=null && !dto.getPsChequera().trim().equals(""))
				sql+= " and m.id_chequera = '"+dto.getPsChequera()+"'";
			
			if(dto.getPdMontoIni()>0 || dto.getPdMontoFin()>0)
				sql+= " and m.importe between "+dto.getPdMontoIni()+" and "+dto.getPdMontoFin();
			
			if((dto.getPsFechaValorIni()!=null && !dto.getPsFechaValorIni().trim().equals(""))||(dto.getPsFechaValorFin()!=null && !dto.getPsFechaValorFin().trim().equals("")))
				sql+= " and m.fec_valor between '"+dto.getPsFechaValorIni()+"'" +
			    " and '"+dto.getPsFechaValorFin()+" 23:59"+"'";
			
			if (dto.getPsConcepto()!=null && !dto.getPsConcepto().trim().equals(""))
				sql+=" and q.concepto_set = '"+dto.getPsConcepto()+"'";
			
			if(dto.isPbInversion())
			{
				sql+= " and ccb.id_chequera = m.id_chequera";
				sql+= " and ccb.id_banco = m.id_banco";
				sql+= " and ccb.tipo_chequera in ('I')";
			}
			
			System.out.println("Query Coonsulta Bancomer: "+sql);
			
			 logger.info("Query Coonsulta Bancomer: "+sql);
			paramRetorno = jdbcTemplate.query(sql, new RowMapper(){
			public ParamRetImpBancaDto mapRow(ResultSet rs, int idx) throws SQLException {
				ParamRetImpBancaDto cons = new ParamRetImpBancaDto();
					cons.setDescripcion(rs.getString("descripcion"));
					cons.setBRechazo(rs.getString("b_rechazo"));
					cons.setDespliega(rs.getString("despliega"));
					cons.setImporta(rs.getString("importa"));
					cons.setLiberaAut(rs.getString("libera_aut"));
					cons.setBBancaElect(rs.getString("b_banca_elect"));
					cons.setDescConcepto(rs.getString("desc_concepto"));
					cons.setConceptoSet(rs.getString("concepto"));
					cons.setReferencia(rs.getString("referencia"));
					cons.setImporte(rs.getDouble("importe"));
					cons.setFecValor(rs.getDate("fec_valor"));
					cons.setSucursal(rs.getString("sucursal"));
					cons.setSecuencia(rs.getInt("secuencia"));
					cons.setObservacion(rs.getString("observacion"));
					cons.setFolioBanco(rs.getString("folio_banco"));
					cons.setIdBanco(rs.getInt("id_banco"));
					cons.setIdChequera(rs.getString("id_chequera"));
					cons.setDescBanco(rs.getString("desc_banco"));
					cons.setBSalvoBuenCobro(rs.getString("b_salvo_buen_cobro"));
					cons.setCargoAbono(rs.getString("cargo_abono"));
					cons.setConcepto2(rs.getString("concepto2"));
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setNomEmpresa(rs.getString("nom_empresa"));
					//La secuencia se repite en el query
					cons.setNoCuentaEmp(rs.getInt("no_cuenta_emp"));
					cons.setIdRubro(rs.getInt("id_rubro"));
					cons.setValidaReferencia(rs.getString("valida_referencia"));
					cons.setValidaCheqRef(rs.getString("valida_cheq_ref"));
					cons.setIdCveConcepto(rs.getString("cveConceptoBanco"));
					cons.setConceptoBancario(rs.getString("conceptoBancario"));
				return cons;
			}});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:consultarMovimientoBancaImportaBancomer");
		} 
		return paramRetorno;
	}
	
	
	/*Public Function FunSQLCat_Afiliacion_Ingreso(ByVal plNoEmpresa As Long, _
    ByVal plIdBanco As Long, _
    ByVal psIDChequera As String, _
    ByVal psAfiliacion As String)*/
	@SuppressWarnings("unchecked")
	public List<CatAfiliacionIngresosDto>obtenerAfiliacionIngresos(CatAfiliacionIngresosDto dto)
	{
		List<CatAfiliacionIngresosDto>catAfiliacioList=new ArrayList<CatAfiliacionIngresosDto>();
		String sql="";
		try{
			sql+=" SELECT * ";
			sql+=" FROM cat_afiliacion_ingresos ";
			sql+=" WHERE no_empresa = "+dto.getNoEmpresa();
			sql+=" AND id_banco = "+dto.getIdBanco();
			sql+=" AND id_chequera = '"+dto.getIdChequera()+"'";
			sql+=" AND afiliacion = '"+dto.getAfiliacion()+"'"	;	
			
			System.out.println("obtenerAfiliacionIngresos "+sql);
			catAfiliacioList = jdbcTemplate.query(sql, new RowMapper(){
				public CatAfiliacionIngresosDto mapRow(ResultSet rs, int idx) throws SQLException {
					CatAfiliacionIngresosDto cons = new CatAfiliacionIngresosDto();
						cons.setNoEmpresa(rs.getInt("no_empresa"));
						cons.setIdBanco(rs.getInt("id_banco"));
						cons.setIdChequera(rs.getString("id_chequera"));
						cons.setAfiliacion(rs.getString("afiliacion"));
						cons.setConcepto(rs.getString("concepto"));
						cons.setCuentaPuente(rs.getString("cuenta_puente"));
						cons.setIdDivision(rs.getString("id_division"));
						cons.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
					return cons;
			}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:obtenerAfiliacionIngresos");
		} 
		return catAfiliacioList;
	}
	
	/*Public Function FunSQLCat_Referencia_Ingresos(ByVal plNoEmpresa As Long, _
            ByVal plIdBanco As Long,ByVal psIDChequera As String,ByVal psreferencia As String)*/
@SuppressWarnings("unchecked")
public List<CatReferenciaIngresosDto>obtenerReferenciaIngresos(CatReferenciaIngresosDto dtoRef)
{
	String sql="";
	List<CatReferenciaIngresosDto>refList= new ArrayList<CatReferenciaIngresosDto>();
	try{
		sql+= " SELECT * ";
		sql+= " FROM cat_referencia_ingresos ";
		sql+= " WHERE no_empresa = "+dtoRef.getNoEmpresa();
		sql+= "     AND id_banco = "+dtoRef.getIdBanco();
		sql+= "     AND id_chequera = '"+dtoRef.getIdChequera()+"'";
		sql+= "     AND referencia = '"+dtoRef.getReferencia()+"'";
		
		refList = jdbcTemplate.query(sql, new RowMapper(){
			public CatReferenciaIngresosDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatReferenciaIngresosDto cons = new CatReferenciaIngresosDto();
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setIdBanco(rs.getInt("id_banco"));
					cons.setIdChequera(rs.getString("id_chequera"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setReferencia(rs.getString("referencia"));
					cons.setNoProveedor(rs.getString("no_proveedor"));
					cons.setDivision(rs.getString("division"));
					cons.setIdRubro(rs.getInt("id_rubro"));
					cons.setBCliente(rs.getString("b_cliente"));
				return cons;
		}});
	}catch(Exception e){
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:obtenerReferenciaIngresos");
	} 
	return refList;
}

/*Public Function FunSQLSelectCat_referencia_chequera(ByVal plNoEmpresa As Long,
        ByVal plIdBanco As Long, ByVal psIDChequera As String) As ADODB.Recordset*/
@SuppressWarnings("unchecked")
public List<CatReferenciaChequeraDto>obtenerReferenciaChequera(CatReferenciaChequeraDto dtoRefChe)
{
	String sql="";
	List<CatReferenciaChequeraDto>refCheqList=new ArrayList<CatReferenciaChequeraDto>();
	try{
		sql+= " SELECT longitud, no_caso, formato, id_rubro, long_cliente,";
		sql+= " b_division, b_fecha, b_pertenencia, valor_default, b_proveedor, inicio_cliente, b_parque ";
		sql+= " FROM cat_referencia_chequera ";
		sql+= " WHERE no_empresa = "+dtoRefChe.getNoEmpresa();
		sql+= " and id_banco = "+dtoRefChe.getIdBanco();
		sql+= " and id_chequera = '"+dtoRefChe.getIdChequera()+"'";
		sql+= " order by no_caso ";
		
		refCheqList = jdbcTemplate.query(sql, new RowMapper(){
			public CatReferenciaChequeraDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatReferenciaChequeraDto cons = new CatReferenciaChequeraDto();
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setIdBanco(rs.getInt("id_banco"));
					cons.setIdChequera(rs.getString("id_chequera"));
					cons.setNoCaso(rs.getInt("no_caso"));
					cons.setLongitud(rs.getInt("longitud"));
					cons.setFormato(rs.getString("formato"));
					cons.setIdRubro(rs.getInt("id_rubro"));
					cons.setLongCliente(rs.getInt("long_cliente"));
					cons.setBDivision(rs.getString("b_division"));
					cons.setBFecha(rs.getString("b_fecha"));
					cons.setBPertenencia(rs.getString("b_pertenencia"));
					cons.setValorDefault(rs.getString("valor_default"));
					cons.setBValidaCheq(rs.getString("b_valida_cheq"));
					cons.setBProveedor(rs.getString("b_proveedor"));
					cons.setInicioCliente(rs.getInt("inicio_cliente"));
					cons.setBParque(rs.getString("b_parque"));
				return cons;
		}});
	}catch(Exception e){
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:obtenerReferenciaChequera");
	} 
	return refCheqList;
}
/*Public Function FunSQLBusca_Inmueble_Parque(ByVal plNoEmpresa As Long, _
        ByVal psInmueble As String) _
        As ADODB.Recordset
*/
	public int buscarInmuebleParque(int noEmpresa, String inmueble)
	{
		int idDivision=0;
		String sql="";
		try{
			sql+= " SELECT id_division ";
			sql+= " FROM inmueble_division ";
			sql+= " WHERE no_empresa = " +noEmpresa;
			sql+= " AND inmueble = '"+inmueble+"'";
			idDivision = jdbcTemplate.queryForInt(sql);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:obtenerDivision");
		} 
		return idDivision;
	}	
	
	/*Public Function FunSQLSelect_busca_cliente(ByVal psEquivale_persona As String, _
            ByVal plNoEmpresa As Long, _
            Optional ByVal pbProveedor As Boolean = False)*/
	@SuppressWarnings("unchecked")
	public List<PersonasDto>buscarCliente(PersonasDto dtoPersonas, boolean pbProveedor)
	{
	List<PersonasDto>clienteList=new ArrayList<PersonasDto>();
	String sql="";
	try{	
		sql+=" SELECT no_persona, razon_social ";
		sql+=" FROM persona ";

		if(pbProveedor)
		sql+=" WHERE id_tipo_persona = 'P' ";
		else
		sql+=" WHERE id_tipo_persona = 'C' ";

		sql+="     AND equivale_persona = '"+dtoPersonas.getEquivalePersona()+"' ";

		if (pbProveedor)
		sql+=" AND no_empresa = 552 ";
		else
		sql+=" AND no_empresa = "+dtoPersonas.getNoEmpresa();
		
		clienteList = jdbcTemplate.query(sql, new RowMapper(){
			public PersonasDto mapRow(ResultSet rs, int idx) throws SQLException {
				PersonasDto cons = new PersonasDto();
				cons.setNoPersona(rs.getInt("no_persona"));
				cons.setRazonSocial(rs.getString("razon_social"));
				return cons;
		}});
	}catch(Exception e){
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:buscarCliente");
	} 
	return clienteList;
		
	}
	
	/*Public Function FunSQLSelect_cliente_division_sin_validar(ByVal psNoPersona As Long))*/
	@SuppressWarnings("unchecked")
	public List<CatDivisionDto>obtenerClienteDivSinValidar(int psNoPersona)
	{
		List<CatDivisionDto>listCatDiv=new ArrayList<CatDivisionDto>();
		String sql="";
		try{
			sql+=" SELECT c.id_division, cd.desc_division ";
			sql+=" FROM cliente_division c, cat_division cd ";
			sql+=" WHERE no_persona = " + psNoPersona;
			sql+=" AND c.id_division = cd.id_division ";
			listCatDiv = jdbcTemplate.query(sql, new RowMapper(){
			public CatDivisionDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatDivisionDto cons = new CatDivisionDto();
				cons.setIdDivision(rs.getString("id_division"));
				cons.setDescDivision(rs.getString("desc_division"));
				return cons;
			}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:obtenerClienteDivSinValidar");
		} 
		return listCatDiv;
	}
	/*Public Function FunSQLSelect_busca_inmueble(ByVal plNoEmpresa As Long, _
                                            ByVal psreferencia As String, _
                                            Optional ByVal piDigitosAdic As Integer = 0)*/
	public int buscarInmueble(int noEmpresa, String psReferencia,int piDigitoAdicional)
	{
		int idDivision=0;
        String sql="";
	    try
	    {
	        sql = " SELECT id_division ";
		    sql+= " FROM inmueble_division ";
		    sql+= " WHERE no_empresa = "+noEmpresa;
		    sql+= " AND inmueble = '"+psReferencia.substring(10+piDigitoAdicional,10+piDigitoAdicional+2)+"'";
		    idDivision=jdbcTemplate.queryForInt(sql);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:buscarInmueble");
		} 
		return idDivision;
	}
	

	/*Public Function FunSQLObtieneLongitud(ByVal ps_banco As Integer, ByVal ps_Chequera As String, ByVal ps_empresa As Integer)*/
	
	@SuppressWarnings("unchecked")
	public List<CatArmaIngresoDto> obtenerLongitud(CatArmaIngresoDto dto)
	{
		String sql="";
		List<CatArmaIngresoDto>listLongitud=new ArrayList<CatArmaIngresoDto>();
		try
		{
			sql+=" Select long_referencia from cat_arma_ingreso ";
		    sql+=" where id_banco=" + dto.getIdBanco();
		    sql+=" and id_chequera='" +dto.getIdChequera()+"'";
		    sql+=" and no_empresa=" + dto.getNoEmpresa();
		    listLongitud = jdbcTemplate.query(sql, new RowMapper(){
				public CatArmaIngresoDto mapRow(ResultSet rs, int idx) throws SQLException {
					CatArmaIngresoDto cons = new CatArmaIngresoDto();
					cons.setLongReferencia(rs.getInt("long_referencia"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:obtenerLongitud");
		}  
		return listLongitud;
	}
	@SuppressWarnings("unchecked")
	public List<ParamDeterminaCorrespondeDto>obtenerTipoIngreso(ParamDeterminaCorrespondeDto dto)
	{
		String sql="",sLen="", gsDBM="SQL SERVER";
		List<ParamDeterminaCorrespondeDto>listReDetCorresponde=new ArrayList<ParamDeterminaCorrespondeDto>();
		if(gsDBM.equals("SQL SERVER")) 
	        sLen = "len";
	    else
	        sLen = "length";
	    try{
	    	sql+=" SELECT id_corresponde, long_referencia, base_calculo," +
		           "     orden_empresa as Orden, long_empresa as Longitud," +
		           "     'Empresa' as Campo, 0 as Valor, orden_empresa,"+
		           "     b_cambia_origen, b_cambia_destino, destino_empresa as destino "+
		           " FROM cat_arma_ingreso"+
		           " WHERE no_empresa = "+dto.getNoEmpresa()+" AND" +
		           "     id_banco = "+dto.getIdBanco()+" AND" +
		           "     id_chequera = '"+dto.getIdChequera()+"' AND" +
		           "     long_referencia = " +dto.getLongReferencia()+ " AND" +
		           "     orden_empresa <> 0"+
		           " UNION";
	    	sql = sql +
		           " SELECT id_corresponde, long_referencia, base_calculo," +
		           "     orden_cliente as Orden, long_cliente as Longitud," +
		           "     'Cliente' as Campo, 0 as Valor, orden_empresa," +
		           "     b_cambia_origen, b_cambia_destino, 'no_cliente' as destino " +
		           " FROM cat_arma_ingreso" +
		           " WHERE no_empresa = "+dto.getNoEmpresa()+" AND" +
		           "     id_banco = "+dto.getIdBanco()+" AND" +
		           "     id_chequera = '"+dto.getIdChequera()+"' AND" +
		           "     long_referencia = "+dto.getLongReferencia()+" AND" +
		           "     orden_cliente <> 0" +
		           " UNION";
		   sql = sql +
		           " SELECT id_corresponde, long_referencia, base_calculo," +
		           "     orden_codigo as Orden, long_codigo as Longitud," +
		           "     'Codigo' as Campo, 0 as Valor, orden_empresa," +
		           "     b_cambia_origen, b_cambia_destino, 'id_codigo' as destino " +
		           " FROM cat_arma_ingreso" +
		           " WHERE no_empresa = " +dto.getNoEmpresa()+" AND" +
		           "     id_banco = " +dto.getIdBanco()+ " AND" +
		           "     id_chequera = '" +dto.getIdChequera()+"' AND" +
		           "     long_referencia = "+dto.getLongReferencia()+" AND" +
		           "     orden_codigo <> 0" +
		           " UNION";
		   sql = sql +
		           " SELECT id_corresponde, long_referencia, base_calculo," +
		           "     orden_subcodigo as Orden, long_subcodigo as Longitud," +
		           "     'SubCodigo' as Campo, 0 as Valor, orden_empresa," +
		           "     b_cambia_origen, b_cambia_destino, 'id_subcodigo' as destino " +
		           " FROM cat_arma_ingreso" +
		           " WHERE no_empresa = " +dto.getNoEmpresa()+ " AND" +
		           "     id_banco = " +dto.getIdBanco()+ " AND" +
		           "     id_chequera = '" +dto.getIdChequera()+ "' AND" +
		           "     long_referencia = " +dto.getLongReferencia()+ " AND" +
		           "     orden_subcodigo <> 0" +
		           " UNION";
		    sql = sql +
		           " SELECT id_corresponde, long_referencia, base_calculo," +
		           "     orden_division as Orden, long_division as Longitud," +
		           "     'Division' as Campo, 0 as Valor, orden_empresa," +
		           "     b_cambia_origen, b_cambia_destino, 'division' as destino " +
		           " FROM cat_arma_ingreso" +
		           " WHERE no_empresa = "+dto.getNoEmpresa()+ " AND" +
		           "     id_banco = " +dto.getIdBanco()+ " AND" +
		           "     id_chequera = '"+dto.getIdChequera()+"' AND" +
		           "     long_referencia = "+dto.getLongReferencia()+" AND" +
		           "     orden_division <> 0" +
		           " UNION";
		    sql = sql +
		           " SELECT id_corresponde, long_referencia, base_calculo," +
		           "     orden_const1 as Orden, " + sLen + "(const1) as Longitud," +
		           "     'Const1' as Campo, const1 as Valor, orden_empresa," +
		           "     b_cambia_origen, b_cambia_destino, destino_const1 as destino " +
		           " FROM cat_arma_ingreso" +
		           " WHERE no_empresa = " +dto.getNoEmpresa()+ " AND" +
		           "     id_banco = " +dto.getIdBanco()+ " AND" +
		           "     id_chequera = '" +dto.getIdChequera()+ "' AND" +
		           "     long_referencia = " +dto.getLongReferencia()+ " AND" +
		           "     orden_const1 <> 0" +
		           " UNION";
		    sql = sql +
		           " SELECT id_corresponde, long_referencia, base_calculo," +
		           "     orden_const2 as Orden, " + sLen + "(const2) as Longitud," +
		           "     'Const2' as Campo, const2 as Valor, orden_empresa," +
		           "     b_cambia_origen, b_cambia_destino, destino_const2 as destino " +
		           " FROM cat_arma_ingreso" +
		           " WHERE no_empresa = " +dto.getNoEmpresa()+ " AND" +
		           "     id_banco = " +dto.getIdBanco()+ " AND" +
		           "     id_chequera = '" +dto.getIdChequera()+"' AND" +
		           "     long_referencia = " +dto.getLongReferencia()+ " AND" +
		           "     orden_const2 <> 0" +
		           " UNION";
		    sql = sql +
		           " SELECT id_corresponde, long_referencia, base_calculo," +
		           "     orden_const3 as Orden, " + sLen + "(const3) as Longitud," +
		           "     'Const3' as Campo, const3 as Valor, orden_empresa," +
		           "     b_cambia_origen, b_cambia_destino, destino_const3 as destino " +
		           " FROM cat_arma_ingreso" +
		           " WHERE no_empresa = " +dto.getNoEmpresa()+ " AND" +
		           "     id_banco = " +dto.getIdBanco()+  " AND" +
		           "     id_chequera = '" +dto.getIdChequera()+ "' AND" +
		           "     long_referencia = "+dto.getLongReferencia()+" AND" +
		           "     orden_const3 <> 0" +
		           " UNION";
		    sql = sql +
		           " SELECT id_corresponde, long_referencia, base_calculo," +
		           "     orden_var1 as Orden, long_var1 as Longitud," +
		           "     'Var1' as Campo, 0 as Valor, orden_empresa," +
		           "     b_cambia_origen, b_cambia_destino, destino_var1 as destino " +
		           " FROM cat_arma_ingreso" +
		           " WHERE no_empresa = " +dto.getNoEmpresa()+ " AND" +
		           "     id_banco = " +dto.getIdBanco()+ " AND" +
		           "     id_chequera = '" +dto.getIdChequera()+ "' AND" +
		           "     long_referencia = " +dto.getLongReferencia()+ " AND" +
		           "     orden_var1 <> 0" +
		           " UNION";
		    sql = sql +
		           " SELECT id_corresponde, long_referencia, base_calculo," +
		           "     orden_var2 as Orden, long_var2 as Longitud," +
		           "     'Var2' as Campo, 0 as Valor, orden_empresa," +
		           "     b_cambia_origen, b_cambia_destino, destino_var2 as destino " +
		           " FROM cat_arma_ingreso" +
		           " WHERE no_empresa = " +dto.getNoEmpresa()+ " AND" +
		           "     id_banco = " +dto.getIdBanco()+ " AND" +
		           "     id_chequera = '" +dto.getIdChequera()+ "' AND" +
		           "     long_referencia = " +dto.getLongReferencia()+ " AND" +
		           "     orden_var2 <> 0" +
		           " UNION";
		    sql = sql +
		           " SELECT id_corresponde, long_referencia, base_calculo," +
		           "     orden_var3 as Orden, long_var3 as Longitud," +
		           "     'Var3' as Campo, 0 as Valor, orden_empresa," +
		           "     b_cambia_origen, b_cambia_destino, destino_var3 as destino " +
		           " FROM cat_arma_ingreso" +
		           " WHERE no_empresa = " +dto.getNoEmpresa()+ " AND" +
		           "     id_banco = " +dto.getIdBanco()+ " AND" +
		           "     id_chequera = '" +dto.getIdChequera()+ "' AND" +
		           "     long_referencia = " +dto.getLongReferencia()+ " AND" +
		           "     orden_var3 <> 0";
		    sql = sql + " ORDER BY id_corresponde, orden";
		    
		    listReDetCorresponde = jdbcTemplate.query(sql, new RowMapper(){
				public ParamDeterminaCorrespondeDto mapRow(ResultSet rs, int idx) throws SQLException {
					ParamDeterminaCorrespondeDto cons = new ParamDeterminaCorrespondeDto();
					cons.setIdCorresponde(rs.getString("id_corresponde"));
					cons.setLongReferencia(rs.getInt("long_referencia"));
					cons.setBaseCalculo(rs.getString("base_calculo"));
					cons.setOrden(rs.getInt("Orden"));
					cons.setLongEmpresa(rs.getInt("Longitud"));
					cons.setCampo(rs.getString("Campo"));
					cons.setValor(rs.getInt("Valor"));
					cons.setOrdenEmpresa(rs.getInt("orden_empresa"));
					cons.setBCambiaOrigen(rs.getString("b_cambia_origen"));
					cons.setBCambiaDestino(rs.getString("b_cambia_destino"));
					cons.setDestinoEmpresa(rs.getString("destino"));
					return cons;
				}});
		    
	    }catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:obtenerTipoIngreso");
	    } 
	    return listReDetCorresponde;
	}
	
    /*Public Function FunSQLObtenCuentaEmpresa(ByVal plNoEmpresa As Long) As ADODB.Recordset*/
	@SuppressWarnings("unchecked")
	public List<EmpresaDto>obtenerCuentaEmpresa(int noEmpresa)
	{
		List<EmpresaDto>listEmpresa=new ArrayList<EmpresaDto>();
		String sql="";	
		try{
				sql+=" SELECT no_cuenta_emp ";
			    sql+=" FROM empresa  ";
			    sql+=" WHERE no_empresa = " +noEmpresa;
			    listEmpresa = jdbcTemplate.query(sql, new RowMapper(){
				public EmpresaDto mapRow(ResultSet rs, int idx) throws SQLException {
					EmpresaDto cons = new EmpresaDto();
					cons.setNoCuentaEmp(rs.getInt("no_cuenta_emp"));
					return cons;
			}});
		    }catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:obtenerCuentaEmpresa");
		    }
	    return listEmpresa;
	}
  
	/*Public Function FunSQLObtenDatosArmaRef(ByVal plNoEmpresa As Long, _
    ByVal plIdBanco As Long, ByVal psIDChequera As String, _
    ByVal psIdCorresponde) As ADODB.Recordset*/

@SuppressWarnings("unchecked")
public List<CatArmaIngresoDto>ObtenerDatosArmaRef(int noEmpresa, int idBanco, String idChequera, String sCorresponde)
	{
		List<CatArmaIngresoDto>listArmaRef=new ArrayList<CatArmaIngresoDto>();
		String sql="";
		
		try{
			sql += "SELECT long_referencia, orden_empresa, orden_cliente, orden_codigo, ";
			sql+= " orden_subcodigo, orden_division, orden_const1, orden_const2, orden_const3, ";
			sql+= " long_empresa, long_cliente, long_codigo, long_subcodigo, long_division, ";
			sql+= " const1, const2, const3, base_calculo, id_rubro, id_chequera_destino, b_cambia_origen, ";
			sql+= " b_cambia_destino, destino_empresa,destino_const1,destino_const2,destino_const3,";
			sql+= " orden_var1,orden_var2,orden_var3,long_var1,long_var2 , long_var3, destino_var1, ";
			sql+= " destino_var2, destino_var3, b_predeterminada";
			sql+= " FROM cat_arma_ingreso WHERE no_empresa=" +noEmpresa;
			sql+= " AND id_banco="+idBanco+" AND id_chequera='"+idChequera+"'";
			sql+= " AND id_corresponde='"+sCorresponde+"'";
			
			listArmaRef = jdbcTemplate.query(sql, new RowMapper(){
				public CatArmaIngresoDto mapRow(ResultSet rs, int idx) throws SQLException {
					CatArmaIngresoDto cons = new CatArmaIngresoDto();
						cons.setLongReferencia(rs.getInt("long_referencia"));
						cons.setOrdenEmpresa(rs.getInt("orden_empresa"));
						cons.setOrdenCliente(rs.getInt("orden_empresa"));
						cons.setOrdenCodigo(rs.getInt("orden_codigo"));
						cons.setOrdenSubcodigo(rs.getInt("orden_subcodigo")); 
						cons.setOrdenDivision(rs.getInt("orden_division"));
						cons.setOrdenConst1(rs.getInt("orden_const1"));
						cons.setOrdenConst2(rs.getInt("orden_const2"));
						cons.setOrdenConst3(rs.getInt("orden_const3"));
						cons.setLongEmpresa(rs.getInt("long_empresa"));
						cons.setLongCliente(rs.getInt("long_cliente"));
						cons.setLongCodigo(rs.getInt("long_codigo"));
						cons.setLongSubcodigo(rs.getInt("long_subcodigo"));
						cons.setLongDivision(rs.getInt("long_division"));
						cons.setConst1(rs.getString("const1"));
						cons.setConst2(rs.getString("const2"));
						cons.setConst3(rs.getString("const3"));
						cons.setBaseCalculo(rs.getString("base_calculo"));
						cons.setIdRubro(rs.getInt("id_rubro"));
						cons.setIdChequeraDestino(rs.getString("id_chequera_destino"));
						cons.setBCambiaDestino(rs.getString("b_cambia_destino"));
						cons.setBCambiaOrigen(rs.getString("b_cambia_origen"));
						cons.setDestinoEmpresa(rs.getString("destino_empresa"));
						cons.setConst1(rs.getString("destino_const1"));
						cons.setConst2(rs.getString("destino_const2"));
						cons.setConst3(rs.getString("destino_const3"));
						cons.setOrdenVar1(rs.getInt("orden_var1"));
						cons.setOrdenVar2(rs.getInt("orden_var2"));
						cons.setOrdenVar3(rs.getInt("orden_var3"));
						cons.setLongVar1(rs.getInt("long_var1"));
						cons.setLongVar2(rs.getInt("long_var2"));
						cons.setLongVar3(rs.getInt("long_var3"));
						cons.setDestinoVar1(rs.getString("destino_var1"));
						cons.setDestinoVar2(rs.getString("destino_var2"));
						cons.setDestinoVar3(rs.getString("destino_var3"));
						cons.setBPredeterminada(rs.getString("b_predeterminada"));
					return cons;
				}});
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:ObtenerDatosArmaRef");
			} 
		return listArmaRef;
	}
/**
 * Public Function FunVerificaEmpresa_CobranzaCliente(ByVal psreferencia As String, _
 * ByVal piBanco As Integer, ByVal psChequera As String) As Boolean, Se llama en la forma frmTransIng
 * @param referencia
 * @param idBanco
 * @param idChequera
 * @return boolean false si no lo encontro, de lo contrario true
 */
public boolean verificarEmpresaCobranzaCliente(String sReferencia, int idBanco, String idChequera)
{
	int res=0;
	String sql="";
    try{
		sql+= " SELECT referencia_cte,no_persona, c.id_chequera ";
	    sql+= " FROM persona p, cat_cta_banco c ";
	    sql+= " WHERE p.no_empresa = c.no_empresa ";
	    sql+= " and p.id_tipo_persona = 'C' ";
	    sql+= " and p.referencia_cte = '" + sReferencia + "' ";
	    sql+= " and c.id_banco = " + idBanco;
	    sql+= " and c.id_chequera = '" + idChequera + "'";
	    res=jdbcTemplate.queryForInt(sql);
	}catch(Exception e){
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:verificarEmpresaCobranzaCliente");
	} 
    if(res>0)
    	return true;
    else 
    	return false;
}
/**
 * 
 * @param list Obtiene una lista de tipo ParamRetImpBancaDto del business del metodo 
 * consultarMovimientoBancaE para agregar el valor de b_trasp_dep que sera utilizado
 * para el boton de importar para optimizar el proceso
 * @return la misma lista mas el campo traspasoDeposito  
 */
@SuppressWarnings("unchecked")
public List<ParamRetImpBancaDto>obtenerTraspasoDeposito(List<ParamRetImpBancaDto> list)
{
	String sql="";
	if(!list.isEmpty()){
		for(int i=0; i<list.size(); i++){
			sql="";
			sql+= " SELECT b_trasp_dep "; 
			sql+= "\n  FROM equivale_concepto "; 
			sql+= "\n WHERE id_banco = "+list.get(i).getIdBanco();
			sql+= "\n	AND desc_concepto = '"+list.get(i).getConcepto()+"'";
		 
			List<String> datos = jdbcTemplate.query(sql, new RowMapper(){
				public String mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getString("b_trasp_dep");
			}});
 			list.get(i).setTranspasoDeposito(!datos.isEmpty()?datos.get(0):"");
		}
	}
	return list;
}
/*
 Public Function FunSQLSelectReglaConcepto(ByVal psIngreso_Egreso As String, _
            ByVal ps_concepto_set As String, _
            ByVal ps_Id_corresponde As String) As ADODB.Recordset
*/

/**
 * Funcion automatica para reemplazar el uso de sentencias SQL y, en cambio, regresar un Recordset
 * Recibe los parametros necesarios para ejecutar la sentencia de la forma frmAdmor
 */
@SuppressWarnings("unchecked")
public List<ParamReglaConceptoDto>obtenerReglaConcepto(ParamReglaConceptoDto dto) {
	String sql="";
	String cargo = "";
	List<ParamReglaConceptoDto>listReglaConcepto=new ArrayList<ParamReglaConceptoDto>();
	if (dto.getIngresoEgreso().equals("INGRESO"))
	{
		dto.setIngresoEgreso("I");
		
	}
	if (dto.getIngresoEgreso().equals("EGRESO"))
	{
		dto.setIngresoEgreso("E");
		
	}
	
	System.out.println(dto.getIngresoEgreso());	
	try{
	    sql+= " select distinct origen_mov,ingreso_egreso,concepto_set, ";
	    sql+= " \n	id_forma_pago , id_tipo_operacion, origen_mov, id_corresponde ";
	    sql+= " \n	FROM regla_concepto ";
	    sql+= " \n	WHERE ingreso_egreso = '"+dto.getIngresoEgreso()+"' ";
	    sql+= " \n	and concepto_set = '"+dto.getConceptoSet()+"'  ";
	    sql+= " \n	and Coalesce(origen_mov,'') = case when ingreso_egreso = 'E' then ";
	    sql+= " \n	'SET' ";
	    sql+= " \n	Else ";
	    sql+= " \n	'' ";
	    sql+= " \n	End ";
	    sql+= " \n	and Coalesce(id_corresponde,'') = case when ingreso_egreso = 'E' then ";
	    sql+= " \n	Coalesce(id_corresponde,'') ";
	    sql+= " \n	Else ";
	    sql+= " \n	'"+dto.getIdCorresponde()+"' ";
	    sql+= " \n	End ";
	    
	    System.out.println("obteenrRegla concepto "+sql);
	    
	    listReglaConcepto = jdbcTemplate.query(sql, new RowMapper(){
			public ParamReglaConceptoDto mapRow(ResultSet rs, int idx) throws SQLException {
				ParamReglaConceptoDto cons = new ParamReglaConceptoDto();
				cons.setOrigenMov(rs.getString("origen_mov"));
				cons.setIngresoEgreso(rs.getString("ingreso_egreso"));
				cons.setConceptoSet(rs.getString("concepto_set"));
				cons.setIdFormaPago(rs.getInt("id_forma_pago"));
				cons.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
				cons.setOrigenMov(rs.getString("origen_mov"));
				cons.setIdCorresponde(rs.getString("id_corresponde"));
				return cons;
			}});
	}catch(Exception e){
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:obtenerReglaConcepto");
	} 
	return listReglaConcepto;
}

//Public Function FunSQLSelectDivisa(ByVal pi_empresa As Integer, ByVal pi_banco As Integer, ByVal ps_Chequera As String) As ADODB.Recordset
@SuppressWarnings("unchecked")
public String obtenerDivisa(int piEmpresa,int piBanco,String psChequera)
{
	String sql="", idDivisa="";
	try{
		sql+= " SELECT id_divisa from cat_cta_banco ";
	    sql+= " WHERE no_empresa = " +piEmpresa;
	    sql+= " And id_banco = " +piBanco;
	    sql+= " And id_chequera = '" +psChequera+ "'";
	    
	    List<String> datos = jdbcTemplate.query(sql, new RowMapper(){
			public String mapRow(ResultSet rs, int idx) throws SQLException {
				return rs.getString("id_divisa");
		}});
	    idDivisa=datos.size()>0?datos.get(0):"";
	}catch(Exception e){
		/*bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:obtenerDivisa");*/
		e.printStackTrace();
	}
	return idDivisa;
}

public int insertarParametroUno(ParametroDto dto)
{
	//System.out.println("parametro 1");
	/*
	 Public Function FunSQLInserta1(ByVal pvvEmpresa As Variant, ByVal pvvFolio As Variant, _
                               ByVal pvvTipoDocto As Variant, ByVal pvvFormaPago As Variant, _
                               ByVal pvvTipoOperacion As Variant, ByVal pvvUsuario As Variant, _
                               ByVal pvvNoCuenta As Variant, ByVal pvvNoCliente As Variant, _
                               ByVal pvvFecValor As Variant, ByVal pvvFecOriginal As Variant, _
                               ByVal pvvFecOperacion As Variant, ByVal pvvFecAlta As Variant, _
                               ByVal pvvImporte As Variant, ByVal pvvImporteOriginal As Variant, _
                               ByVal pvvIdCaja As Variant, ByVal pvvIdDivisa As Variant, _
                               ByVal pvvIdDivisaOriginal As Variant, ByVal pvvOrigenReg As Variant, _
                               ByVal pvvReferencia As Variant, ByVal pvvConcepto As Variant, _
                               ByVal pvvAplica As Variant, ByVal pvvIdEstatusMov As Variant, _
                               ByVal pvvBSalvoBC As Variant, ByVal pvvIdEstatusReg As Variant, _
                               ByVal pvvIdBanco As Variant, ByVal pvvIdChequera As Variant, _
                               ByVal pvvFolioBanco As Variant, ByVal pvvOrigenMov As Variant, _
                               ByVal pvvObservacion As Variant, ByVal pvvIdInvCBolsa As Variant, _
                               ByVal pvvNoFolioMov As Variant, ByVal pvvFolioRef As Variant, _
                               ByVal pvvGrupo As Variant, ByVal pvvImporteDesglosado As Variant, _
                               ByVal pvvLote As Variant, ByVal pvvValor1 As Variant, _
                               ByVal pvvValor2 As Variant, ByVal sHora_recibo As String, _
                               ByVal plIdRubro As Long, ByVal psIdDivision As String, _
                               Optional pvvCodigo As String = "", Optional pvvSubCodigo As String = "", _
                               Optional psNoDocto As String = "", Optional psBeneficiario As String = "", _
                               Optional psRestoRef As String = "", Optional piIdbancoBenef As Integer = 0, _
                               Optional psIdChequeraBenef As String = "", Optional psInvoiceType As String = "", _
                               Optional psRFC As String = "", Optional psNoFactura As String) As Long
    
    */
        int res=0;
        String sql = "";
   try{
        sql+= "\n	INSERT INTO parametro(no_empresa, no_folio_param, id_tipo_docto, ";
        sql+= "\n	id_forma_pago, id_tipo_operacion, usuario_alta, no_cuenta, no_cliente, ";
        sql+= "\n	fec_valor, fec_valor_original, fec_operacion, fec_alta, importe, ";
        sql+= "\n	importe_original, id_caja, id_divisa, id_divisa_original, origen_reg, ";
        sql+= "\n	referencia, concepto, aplica, id_estatus_mov, b_salvo_buen_cobro, ";
        sql+= "\n	id_estatus_reg,id_banco,id_chequera,folio_banco,origen_mov,observacion, ";
        sql+= "\n	id_inv_cbolsa,no_folio_mov,folio_ref,grupo,importe_desglosado,lote,hora_recibo ";
        sql+= "\n	,division, descripcion ";
        
        if((consultarConfiguraSet(216).equals("SI")|| dto.getIdRubro()!=0) && (dto.getPvvValor1()!=null && dto.getPvvValor1().equals("")))//Referencias de clientes por chequera
            sql+= " ,id_rubro ";
        else if(dto.getPvvValor1()!=null && !dto.getPvvValor1().equals(""))
            sql+= dto.getPvvValor1();
        
        if(dto.getNoDocto()!=null && !dto.getNoDocto().trim().equals("")) 
        	sql+= " ,no_docto ";
        
        if(dto.getBeneficiario()!=null && !dto.getBeneficiario().equals(""))
            sql+= " ,beneficiario";
        
        if(dto.getIdBancoBenef()!=0)
            sql+= " ,id_banco_benef";
        
        if(dto.getIdChequeraBenef()!=null && !dto.getIdChequeraBenef().equals("")) 
            sql+= " ,id_chequera_benef";
        
        if(dto.getInvoiceType()!=null && !dto.getInvoiceType().equals(""))
            sql+= " ,invoice_type";
        
        if(dto.getRfc()!=null && !dto.getRfc().equals(""))
            sql+= " ,rfc";
        
        if(dto.getNoFactura()!=null && !dto.getNoFactura().equals(""))  
            sql+= " ,no_factura";
        
        if(dto.getSecuencia() != 0)  
            sql+= " ,folio_seg";
        
        sql+= " ,deudor ) ";                
        sql+= "VALUES("+dto.getNoEmpresa()+"," +dto.getNoFolioParam();
        sql+= ","+dto.getNoDocto()+","+dto.getIdFormaPago()+","+dto.getIdTipoOperacion();
        sql+= ","+dto.getUsuarioAlta()+",'"+dto.getNoCuenta()+"'"+",'"+dto.getNoCliente()+"'";
        sql+= ",convert(datetime,'" +funciones.ponerFecha(dto.getFecValor())+"',103),convert(datetime,'"+funciones.ponerFechaSola(dto.getFecValorOriginal())+"',103)";
        sql+= ",convert(datetime,'"+funciones.ponerFecha(dto.getFecOperacion())+"',103),convert(datetime,'"+funciones.ponerFecha(dto.getFecAlta())+"',103),"+dto.getImporte()+",";
        sql+= dto.getImporteOriginal()+","+dto.getIdCaja()+",'"+dto.getIdDivisa()+"','"+dto.getIdDivisaOriginal()+"','"+dto.getOrigenReg()+"',";
        sql+= "substring('"+dto.getReferencia()+"',1,30),'"+dto.getConcepto()+"',"+dto.getAplica()+",'"+dto.getIdEstatusMov()+"','"+dto.getBSalvoBuenCobro()+"',";
        sql+= "'"+dto.getIdEstatusReg()+"',"+dto.getIdBanco()+",'"+dto.getIdChequera()+"','"+dto.getFolioBanco()+"','"+dto.getOrigenMov()+"','"+dto.getObservacion()+"',";
        sql+= dto.getIdInvCbolsa()+","+dto.getNoFolioMov()+","+dto.getFolioRef()+","+dto.getIdGrupo()+","+dto.getImporteDesglosado()+","+dto.getLote();
        sql+= ",'"+dto.getHoraRecibo()+"', '"+dto.getDivision()+"', '" + dto.getDescripcion() + "' ";
        
        if((consultasGenerales.consultarConfiguraSet(216).equals("SI")|| dto.getIdRubro()!=0) && (dto.getPvvValor1()!=null && dto.getPvvValor1().equals("")))
                sql+= ", "+dto.getIdRubro();
        else if(dto.getPvvValor1()!=null && !dto.getPvvValor1().equals(""))
           sql+=dto.getPvvValor2();
        
        if(dto.getNoDocto()!=null && !dto.getNoDocto().trim().equals("")) 
            sql+= ", '"+dto.getNoDocto()+"'";
        
        if(dto.getBeneficiario()!=null && !dto.getBeneficiario().equals(""))
            sql+= ", '"+dto.getBeneficiario()+"'";
        
        if(dto.getIdBancoBenef()!=0)
            sql+= " ,"+dto.getIdBancoBenef()+" ";
        
        if(dto.getIdChequeraBenef()!=null && !dto.getIdChequeraBenef().equals(""))
            sql+= " ,'"+dto.getIdChequeraBenef()+"' ";
        
        if(dto.getInvoiceType()!=null && !dto.getInvoiceType().equals(""))
            sql+= " ,'"+dto.getInvoiceType()+"' ";
        
        if(dto.getRfc()!=null && !dto.getRfc().equals(""))
            sql+= " ,'"+dto.getRfc()+"' ";
        
        if(dto.getNoFactura()!=null && !dto.getNoFactura().equals(""))
            sql+= " ,'"+dto.getNoFactura()+"' ";
        
        if(dto.getSecuencia() != 0)  
            sql+= " ,"+ dto.getSecuencia() +" ";
        
        sql+= ", '"+dto.getReferencia()+"' ) ";
        
        System.out.println("insercion en parametro: " + sql);
        
        res=jdbcTemplate.update(sql);
	}catch(Exception e){
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:insertarParametroUno");
	}
        
        return res;
}
public int insertarParametroDos(ParametroDto dto)
{
	//System.out.println("parametro 2");
	int res=0;
	String sql="";
	try{
	        sql+= " INSERT INTO parametro(no_empresa, no_folio_param, id_tipo_docto, ";
	        sql+= " id_forma_pago, id_tipo_operacion, usuario_alta, no_cuenta, no_cliente, ";
	        sql+= " fec_valor, fec_valor_original, fec_operacion, fec_alta, importe, ";
	        sql+= " importe_original, id_caja, id_divisa, id_divisa_original, origen_reg, ";
	        sql+= " referencia, concepto, aplica, id_estatus_mov, b_salvo_buen_cobro, ";
	        sql+= " id_estatus_reg,id_banco,id_chequera,folio_banco,origen_mov,observacion, ";
	        sql+= " id_inv_cbolsa,grupo,no_folio_mov,folio_ref,id_banco_benef,id_chequera_benef,lote,";
	        sql+= " hora_recibo,division, descripcion ";
	        
	        if(gsDBM.equals("SYBASE"))
	        	sql+= " ,id_rubro ";
	        else if(dto.getPvvValor1()!=null && !dto.getPvvValor1().equals(""))
	           sql+= dto.getPvvValor1();
	        
	        sql+= " ,beneficiario ,deudor, folio_seg )";
	                            
	        sql+= " VALUES(" +dto.getNoEmpresa()+","+dto.getNoFolioParam();
	        sql+= ","+dto.getNoDocto()+","+dto.getIdFormaPago()+","+dto.getIdTipoOperacion();
	        sql+= ","+dto.getUsuarioAlta()+","+dto.getNoCuenta()+",'"+dto.getNoCliente()+"'";
	        sql+= ",'"+funciones.ponerFecha(dto.getFecValor())+"','"+funciones.ponerFecha(dto.getFecValorOriginal())+"'";
	        sql+= ",'"+funciones.ponerFecha(dto.getFecOperacion())+"','"+funciones.ponerFecha(dto.getFecAlta())+"'," +dto.getImporte()+",";
	        sql+= dto.getImporteOriginal()+","+dto.getIdCaja()+",'"+dto.getIdDivisa()+"','" +dto.getIdDivisaOriginal()+"','"+dto.getOrigenReg()+"','";
	        sql+= dto.getReferencia()+"','"+dto.getConcepto()+"',"+dto.getAplica()+",'"+dto.getIdEstatusMov()+"','"+dto.getBSalvoBuenCobro()+"','";
	        sql+= dto.getIdEstatusReg()+"',"+dto.getIdBanco()+",'" +dto.getIdChequera()+"','"+dto.getFolioBanco()+"','"+dto.getOrigenMov()+"','"+dto.getObservacion()+"',";
	        sql+= dto.getIdInvCbolsa()+","+dto.getGrupo()+","+dto.getNoFolioMov()+","+dto.getFolioRef()+","+dto.getIdBancoBenef()+",'"+dto.getIdChequeraBenef()+"',"+dto.getLote();
	        sql+= ",'"+dto.getHoraRecibo()+"','"+dto.getDivision()+"','" + dto.getDescripcion() + "' ";
	        
	        if(gsDBM.equals("SYBASE"))
	            sql+= "," +dto.getIdRubro();
	        else if(dto.getPvvValor1()!=null && !dto.getPvvValor1().equals(""))
	           sql+= dto.getPvvValor2();
	        
	        sql+= ",'"+dto.getBeneficiario()+ "', '"+dto.getReferencia()+"', "+ dto.getSecuencia() +")";//checar resto de referencia
	        
	        bitacora.insertarRegistro(sql);
	        
	        System.out.println("Segundo insert parametro: " + sql);
	        
	        res=jdbcTemplate.update(sql);
		}catch(Exception e){
			//bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			//+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:insertarParametroDos");
			e.printStackTrace();
		}
		return res;
}
/*Public Function FunSQLSelect863(ByVal ptFechaInicial As Date, _
                                ByVal ptFechaFinal As Date, _
                                ByVal plBanco As Long, _
                                ByVal plNoUsuario As Long) As ADODB.Recordset*/
@SuppressWarnings("unchecked")
public List<ParamRetImpBancaDto>prepararImportaPendientes(String fecInicial, String fecFinal, int idBanco,int idUsuario)
{
    //Recibe los par�metros necesarios para ejecutar la sentencia de la forma frmTransIng
	
	String sql="";
	List<ParamRetImpBancaDto>listaRetorno=new ArrayList<ParamRetImpBancaDto>();
	try{
		if(gsDBM.equals("ORACLE"))
		{
			
		}
		else if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE"))
		{
			sql="";
			sql+="     SELECT DISTINCT ";
	        sql+="\n         m.descripcion,isnull(q.importa,'S') as importa_concepto, ";
	        sql+="\n         isnull(q.despliega,'S') as despliega_concepto, ";
	        sql+="\n         CASE WHEN movto_arch is null ";
	        sql+="\n              THEN 'N' ";
	        sql+="\n              ELSE 'S' ";
	        sql+="\n         END as importado, ";
	        sql+="\n         c.libera_aut,q.desc_concepto,c.b_banca_elect, ";
	        sql+="\n         q.concepto_set as concepto,q.concepto_set, ";
	        sql+="\n         m.referencia, m.importe, m.fec_valor, m.sucursal, ";
	        sql+="\n         m.observacion, m.folio_banco, m.id_banco, ";
	        sql+="\n         m.id_chequera, c.desc_banco, ";
	        sql+="\n         CASE WHEN m.b_salvo_buen_cobro = 'S' ";
	        sql+="\n              THEN 'N' ";
	        sql+="\n             ELSE 'S' ";
	        sql+="\n         END as b_salvo_buen_cobro, ";
	        sql+="\n         m.cargo_abono, m.concepto as concepto2, ";
	        sql+="\n         m.no_empresa, e.nom_empresa, m.secuencia, ";
	        sql+="\n         e.no_cuenta_emp, m.id_rubro ";
	        if(gsDBM.equals("SYBASE"))
	        {
	            sql+="\n	,case when m.cargo_abono = 'I' then";
	            sql+="\n   (Select case when max(r.id_chequera) is null then 'N' else 'S' end";
	            sql+="\n         from  cat_referencia_chequera r where r.no_empresa = m.no_empresa";
	            sql+="\n         and r.id_banco = r.id_banco and r.id_chequera = m.id_chequera)";
	            sql+="\n	Else 'N' end as valida_referencia";
	        }
	        sql+="\n     FROM movto_banca_e m, cat_banco c, empresa e, ";
	        sql+="\n         equivale_concepto q ";
	        sql+="\n     WHERE m.id_banco = c.id_banco ";
	        sql+="\n         and (m.id_banco = q.id_banco OR q.id_banco = 0) ";
	        sql+="\n         and m.cargo_abono = q.cargo_abono ";
	        sql+="\n         and m.id_banco = " +idBanco;
	        sql+="\n         and m.id_estatus_trasp = 'N' ";
	        sql+="\n         and e.no_empresa = m.no_empresa ";
	        sql+="\n         and m.concepto like ";
	        sql+="\n             CASE WHEN m.movto_arch = 1 ";
	        sql+="\n                  THEN q.desc_concepto + '%' ";
	        sql+="\n                  ELSE q.concepto_set + '%' ";
	        sql+="\n             END ";
	        sql+="\n         and m.referencia <> isnull((";
	        sql+="\n             SELECT max(valor) ";
	        sql+="\n             FROM referencia_det ";
	        sql+="\n             WHERE id_corresponde = 'T' ";
	        sql+="\n                 and b_importa = 'N'), '0') ";
	        sql+="\n         and m.fec_valor between '" +fecInicial+ "' ";
	        sql+="\n             and '"+fecFinal+"' ";
	        sql+="\n      and m.no_empresa in(select u.no_empresa ";
	        sql+="\n          from usuario_empresa u ";
	        sql+="\n          where no_usuario = "+idUsuario+")";
	        sql+="\n      and isnull(q.importa,'S') = 'S' ";
	        
	        if(idBanco==2)//No se importan los movimiento que empiezan con AUT. ya que son traspasos
	            sql+="\n	 and PATINDEX('%AUT.%', m.referencia) = 0 ";
	
	            sql+="\n     ORDER BY m.no_empresa,m.id_banco ";
		}
		else if(gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2"))
		{
			sql="";
	        sql+="\n     SELECT distinct ";
	        sql+="\n         m.descripcion,Coalesce(q.importa,'S') as importa_concepto, ";
	        sql+="\n         Coalesce(q.despliega,'S') as despliega_concepto, ";
	        sql+="\n         CASE WHEN movto_arch is null ";
	        sql+="\n              THEN 'N' ";
	        sql+="\n              ELSE 'S' ";
	        sql+="\n         END as importado, ";
	        sql+="\n         c.libera_aut,q.desc_concepto, c.b_banca_elect, ";
	        sql+="\n         q.concepto_set as concepto, q.concepto_set, ";
	        sql+="\n         m.referencia, m.importe, m.fec_valor, ";
	        sql+="\n         m.sucursal, m.observacion, m.folio_banco, ";
	        sql+="\n         m.id_banco, m.id_chequera, c.desc_banco, ";
	        sql+="\n         CASE WHEN m.b_salvo_buen_cobro = 'S' ";
	        sql+="\n              THEN 'N' ";
	        sql+="\n              ELSE 'S' ";
	        sql+="\n         END as b_salvo_buen_cobro, ";
	        sql+="\n         m.cargo_abono, m.concepto as concepto2, ";
	        sql+="\n         m.no_empresa, e.nom_empresa, m.secuencia, ";
	        sql+="\n         e.no_cuenta_emp, m.id_rubro ";
	        sql+="\n	,case when m.cargo_abono = 'I' then";
	            
	        sql+="\n   (Select case when max(r.id_chequera) is null then 'N' else 'S' end";
	        sql+="\n         from  cat_referencia_chequera r where r.no_empresa = m.no_empresa";
	        sql+="\n         and r.id_banco = r.id_banco and r.id_chequera = m.id_chequera)";
	        sql+="\n	Else 'N' end as valida_referencia";
	            
	        sql+="\n     FROM movto_banca_e m, cat_banco c, empresa e, ";
	        sql+="\n         equivale_concepto q ";
	        sql+="\n     WHERE m.id_banco = c.id_banco ";
	        sql+="\n         and (m.id_banco = q.id_banco OR q.id_banco = 0) ";
	        sql+="\n         and m.cargo_abono = q.cargo_abono ";
	        sql+="\n         and m.id_banco = "+idBanco;
	        sql+="\n         and m.id_estatus_trasp = 'N' ";
	        sql+="\n         and e.no_empresa = m.no_empresa ";
	        sql+="\n         and m.concepto like ";
	        sql+="\n             CASE WHEN m.movto_arch = 1 ";
	        sql+="\n                  THEN q.desc_concepto || '%' ";
	        sql+="\n                  ELSE q.concepto_set || '%' ";
	        sql+="\n             END ";
	        sql+="\n         and m.referencia <> coalesce((";
	        
	        if(gsDBM.equals("DB2"))
	            sql+="\n            SELECT min(valor) ";
	        else
	            sql+="\n            SELECT valor ";
	        
	        sql+="\n             FROM referencia_det ";
	        sql+="\n             WHERE id_corresponde = 'T' ";
	        //''''duda: si esta bien
	        if(gsDBM.equals("DB2"))
	            sql+="\n                 and b_importa = 'N'), '0')  ";
	            //'sql+="                 and b_importa = 'N')fetch first 1 row only, '0')  ";
	        else
	            sql+="\n                 and b_importa = 'N' LIMIT 1), '0') ";
	        
	        sql+="\n         and m.fec_valor between '"+fecInicial+"' ";
	        sql+="\n             and '"+fecFinal +" 23:59"+"' ";
	        sql+="\n      and m.no_empresa in(select u.no_empresa ";
	        sql+="\n          from usuario_empresa u ";
	        sql+="\n          where no_usuario = "+idUsuario+")";
	        sql+="\n      and coalesce(q.importa,'S') = 'S' ";
	        if(idBanco==2)//'No se importan los movimiento que empiezan con AUT. ya que son traspasos
	            sql+="\n	 and POSITION ('AUT.' in m.referencia) = 0";
	
	        sql+="\n      ORDER BY no_empresa,id_banco ";
		}
		listaRetorno = jdbcTemplate.query(sql, new RowMapper(){
			public ParamRetImpBancaDto mapRow(ResultSet rs, int idx) throws SQLException {
				ParamRetImpBancaDto cons = new ParamRetImpBancaDto();
				cons.setDescripcion(rs.getString("descripcion"));
				cons.setImporta(rs.getString("importa_concepto"));
				cons.setDespliega(rs.getString("despliega_concepto"));
				cons.setImportado(rs.getString("importado"));
				cons.setLiberaAut(rs.getString("libera_aut"));
				cons.setDescConcepto(rs.getString("desc_concepto"));
				cons.setBBancaElect(rs.getString("b_banca_elect"));
				cons.setConcepto(rs.getString("concepto"));
				cons.setConceptoSet(rs.getString("concepto_set"));
				cons.setReferencia(rs.getString("referencia"));
				cons.setImporte(rs.getDouble("importe"));
				cons.setFecValor(rs.getDate("fec_valor"));
				cons.setSucursal(rs.getString("sucursal"));
				cons.setObservacion(rs.getString("observacion"));
				cons.setFolioBanco(rs.getString("folio_banco"));	
				cons.setIdBanco(rs.getInt("id_banco"));		
				cons.setIdChequera(rs.getString("id_chequera"));
				cons.setDescBanco(rs.getString("desc_banco"));
				cons.setBSalvoBuenCobro(rs.getString("b_salvo_buen_cobro"));
				cons.setCargoAbono(rs.getString("cargo_abono"));
				cons.setConcepto2(rs.getString("concepto2"));
				cons.setNoEmpresa(rs.getInt("no_empresa"));
				cons.setNomEmpresa(rs.getString("nom_empresa"));
				cons.setSecuencia(rs.getInt("secuencia"));
				cons.setNoCuentaEmp(rs.getInt("no_cuenta_emp"));
				cons.setIdRubro(rs.getInt("id_rubro"));
				cons.setValidaReferencia(rs.getString("valida_referencia"));
				return cons;
			}});
	}catch(Exception e){
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:prepararImportaPendientes");
			}
	return listaRetorno;
}

/*Public Function FunSQLVerificaImportacionMovSet(psIDChequera As String, _
                                                plIdBanco As Long, _
                                                plNoEmpresa As Long, _
                                                psconcepto As String) As ADODB.Recordset*/

@SuppressWarnings("unchecked")
public String verificarImportaMovSet(String idChequera, int idBanco, int noEmpresa, String concepto)
{
	  String sql = "",impConcepto="";
	 try{
	    sql+="\n	 SELECT coalesce( ( ";
	    sql+="\n        CASE WHEN tipo_chequera in ('M') ";
	    sql+="\n             THEN CASE WHEN e.b_concentradora = 'S' ";
	    sql+="\n                       THEN coalesce(substring( ( q.clas_chequera ) ,3 ,1 ), '') ";
	    sql+="\n                       ELSE coalesce(substring( ( q.clas_chequera ) ,4 ,1 ), '') ";
	    sql+="\n                  END ";
	    sql+="\n             WHEN tipo_chequera in ('C') ";
	    sql+="\n             THEN CASE WHEN e.b_concentradora = 'S' ";
	    sql+="\n                       THEN coalesce(substring( ( q.clas_chequera ) ,3 ,1 ), '') ";
	    sql+="\n                       ELSE coalesce(substring( ( q.clas_chequera ) ,1 ,1 ), '') ";
	    sql+="\n                  END ";
	    sql+="\n             WHEN tipo_chequera in ('P') ";
	    sql+="\n             THEN CASE WHEN e.b_concentradora = 'S' ";
	    sql+="\n                       THEN coalesce(substring( ( q.clas_chequera ) ,3 ,1 ), '') ";
	    sql+="\n                       ELSE coalesce(substring( ( q.clas_chequera ) ,2 ,1 ), '') ";
	    sql+="\n                  END ";
	    sql+="\n             ELSE '' ";
	    sql+="\n       END ), '') AS ImpConcepto ";
	    sql+="\n	 FROM cat_cta_banco ccb, empresa e, equivale_concepto q ";
	    sql+="\n	WHERE ccb.no_empresa = e.no_empresa ";
	    sql+="\n     and ccb.id_chequera = '"+idChequera+"' ";
	    sql+="\n     and ccb.id_banco = "+idBanco;
	    sql+="\n     and ccb.no_empresa = "+noEmpresa;
	    sql+="\n     and q.desc_concepto = '"+concepto+"' ";
	    sql+="\n     and q.id_banco = "+idBanco;
	    List<String> datos = jdbcTemplate.query(sql, new RowMapper(){
			public String mapRow(ResultSet rs, int idx) throws SQLException {
				return rs.getString("ImpConcepto");
		}});
	    impConcepto=datos.get(0);
	}catch(Exception e){
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:obtenerDivisa");
	}
	return impConcepto;
}

/*FunSQLSelectGral*/
@SuppressWarnings("unchecked")
public List<SeleccionGeneralDto> seleccionarGeneral(String psQuery)
{
	String sql="";
	List<SeleccionGeneralDto>listSeleccion=new ArrayList<SeleccionGeneralDto>();
	try{
		sql=psQuery;
		listSeleccion = jdbcTemplate.query(sql, new RowMapper(){
			public SeleccionGeneralDto mapRow(ResultSet rs, int idx) throws SQLException {
				SeleccionGeneralDto cons = new SeleccionGeneralDto();
				cons.setCuenta(rs.getInt("cuenta"));
				cons.setNoPersona(rs.getString("no_persona"));
				return cons;
			}});
		
	}catch(Exception e){
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:seleccionarGeneral");
	}
	return listSeleccion;
	
}

/*Public Function FunSQLSelectReglaConcepto(ByVal psIngreso_Egreso As String, _
            ByVal ps_concepto_set As String, _
            ByVal ps_Id_corresponde As String) As ADODB.Recordset
'Funci�n autom�tica para reemplazar el uso de sentencias SQL y, en cambio, regresar un Recordset
'Recibe los par�metros necesarios para ejecutar la sentencia de la forma frmAdmor
Dim sSQL As String
*/

@SuppressWarnings("unchecked")
public List<ParamRetImpBancaDto>seleccionarReglaConcepto(String psIngresoEgreso,String psConceptoSet, String psIdCorresponde)
{
	String sql="";
	List<ParamRetImpBancaDto>list=new ArrayList<ParamRetImpBancaDto>();
	try{
		sql+= "\n	select distinct origen_mov,ingreso_egreso,concepto_set, ";
	    sql+= "\n	id_forma_pago , id_tipo_operacion, origen_mov, id_corresponde ";
	    sql+= "\n	FROM regla_concepto ";
	    sql+= "\n	WHERE ingreso_egreso = '" + psIngresoEgreso + "' ";
	    sql+= "\n	and concepto_set = '" + psConceptoSet + "'  ";
	    sql+= "\n	and Coalesce(origen_mov,'') = case when ingreso_egreso = 'E' then ";
	    sql+= "\n                 'SET' ";
	    sql+= "\n              Else ";
	    sql+= "\n                 '' ";
	    sql+= "\n              End ";
	    sql+= "\n	and Coalesce(id_corresponde,'') = case when ingreso_egreso = 'E' then ";
	    sql+= "\n                 Coalesce(id_corresponde,'') ";
	    sql+= "\n              Else ";
	    sql+= "\n                 '" + psIdCorresponde +"' ";
	    sql+= "\n              End ";
	    
	    list= jdbcTemplate.query(sql, new RowMapper(){
			public ParamRetImpBancaDto mapRow(ResultSet rs, int idx) throws SQLException {
				ParamRetImpBancaDto cons = new ParamRetImpBancaDto();
				cons.setOrigenMov(rs.getString("origen_mov"));
				cons.setIngresoEgreso(rs.getString("ingreso_egreso"));
				cons.setConceptoSet(rs.getString("concepto_set"));
				cons.setIdFormaPago(rs.getInt("id_forma_pago"));
				cons.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
				cons.setIdCorresponde(rs.getString("id_corresponde"));
				return cons;
			}});
	}catch(Exception e){
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:seleccionarReglaConcepto");
	}
	return list;
}
/*
 Public Function FunSQLConceptoDesconocido(ByVal ptFechaInicial As Date, _
                                          ByVal ptFechaFinal As Date, _
                                          ByVal plBanco As Long) As ADODB.Recordset*/
@SuppressWarnings("unchecked")
public List<ParamConceptoDescDto>conceptoDesconocido(String ptFechaInicial,String ptFechaFinal, int plBanco)
{
	String sql="";
	List<ParamConceptoDescDto>list=new ArrayList<ParamConceptoDescDto>();
	try{
	    if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE"))
	    {
	      
	            sql = "";
	            sql+= " SELECT COUNT(m.no_empresa) as cuenta, m.no_empresa ";
	            sql+= " FROM movto_banca_e m ";
	            sql+= " WHERE Not concepto in ";
	            sql+= "     ( SELECT desc_concepto ";
	            sql+= "       FROM equivale_concepto e ";
	            sql+= "       WHERE e.id_banco = m.id_banco OR e.id_banco = 0";
	            sql+= "           and e.cargo_abono = m.cargo_abono) ";
	            sql+= "     and m.id_estatus_trasp = 'N' ";
	            sql+= "     and movto_arch = 1 ";
	            sql+= "     and m.id_banco in (" +plBanco+", 0) ";
	            sql+= "     and m.referencia <> isnull((";
	            sql+= "         SELECT max(valor) ";
	            sql+= "         FROM referencia_det ";
	            sql+= "         WHERE id_corresponde = 'T' ";
	            sql+= "             and b_importa = 'N'), '0') ";
	            sql+= "     and m.fec_valor between '" +ptFechaInicial+" 00:00"+ "' ";
	            sql+= "         and '" +ptFechaFinal+" 23:59"+ "' ";
	            sql+= " GROUP BY m.no_empresa ";
	    }
	    else if(gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2"))   
	    {
	            sql = "";
	            sql+= " SELECT COUNT(m.no_empresa) as cuenta, m.no_empresa ";
	            sql+= " FROM movto_banca_e m ";
	            sql+= " WHERE Not concepto in ";
	            sql+= "     ( SELECT desc_concepto ";
	            sql+= "       FROM equivale_concepto e ";
	            sql+= "       WHERE e.id_banco = m.id_banco OR e.id_banco = 0";
	            sql+= "           and e.cargo_abono = m.cargo_abono) ";
	            sql+= "     and m.id_estatus_trasp = 'N' ";
	            sql+= "     and movto_arch = 1 ";
	            sql+= "     and m.id_banco in (" + plBanco + ", 0) ";
	            sql+= "     and m.referencia <> coalesce((";
	            sql+= "         SELECT valor ";
	            sql+= "         FROM referencia_det ";
	            sql+= "         WHERE id_corresponde = 'T' ";
	            
	            if(gsDBM.equals("POSTGRESQL"))  
	               sql+= "             and b_importa = 'N' LIMIT 1), '0') ";
	            else
	               sql+= "             and b_importa = 'N'), '0') ";
	            
	            sql+= "     and m.fec_valor between '"+ptFechaInicial+" 00:00" + "' ";
	            sql+= "         and '"+ptFechaFinal+" 23:59"+ "' ";
	            sql+= " GROUP BY m.no_empresa ";
	            
	            if(gsDBM.equals("DB2")) 
	               sql+= " fetch first 1 row only";
	    }
	    list= jdbcTemplate.query(sql, new RowMapper(){
			public ParamConceptoDescDto mapRow(ResultSet rs, int idx) throws SQLException {
				ParamConceptoDescDto cons = new ParamConceptoDescDto();
					cons.setCuenta(rs.getInt("cuenta"));
					cons.setNoCuenta(rs.getInt("no_empresa"));
				return cons;
			}});	    
	}catch(Exception e){
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:conceptoDesconocido");
	}
	
	return list;
}
/*Este store esta en la comer y no es necesario por el momento(req user)Public Function ejecutaStored(ByVal psStored As String) As Long*/
public int ejecutarStored(String psStored)
{
	int res=0;
	String sql="";
	try{
		sql=" execute "+psStored;
		if(psStored!=null && psStored.equals("sp_datos_importa"))
		{
			res=jdbcTemplate.queryForInt(sql);
		}
	}catch(Exception e){
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:ejecutarStored");
	}
		
	return res;
}

/*--gobjVarGlobal.Generador("USRSETDB", "USRSETDB", GI_USUARIO,
--"frmTransing", pl_empresa, pl_folio, pd_FolioMov, pd_FolioDet, 1)
@emp		SMALLINT OUTPUT, 
	@fol_para	INTEGER OUTPUT, 
	@movi		INTEGER OUTPUT, 
	@deta		INTEGER OUTPUT,
	@result		INTEGER OUTPUT,
 	@mensajeResp 

public int ejecutarStoreGenerador()
{
	int res=0;
	String sql="";
	try{
		sql=" execute "+store;
		if(store!=null && store.equals("sp_datos_importa"))
		{
			res=jdbcTemplate.queryForInt(sql);
		}
	}catch(Exception e){
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:ejecutarStored");
	}
		
	return res;
	
}*/

/*Public Function FunSqlInsertReconstruyeSaldoChe(ByVal piNoEmpresa As Integer, ByVal piIdBanco As Long, ByVal psIDChequera As String, _
                                                ByVal pdFecValor As String) As Long)*/
public int insertarReconstruyeSaldoChe(int piNoEmpresa, int piIdBanco, String psIdChequera, String pdFecValor)
{
	//Prepara los datos para poder reconstruir los saldos de las chequeras
	int res=0;
	String sql="";
	try{	
		sql+= " insert into reconstruye_saldo_chequera ";
	    sql+= " (no_empresa,id_banco,id_chequera,fec_valor)";
	    sql+= " values(";
	    sql+= piNoEmpresa + "," + piIdBanco + ",'" + psIdChequera + "','" + pdFecValor + "')";
	    
	    res=jdbcTemplate.queryForInt(sql);
	}catch(Exception e){
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:insertarReconstruyeSaldoChe");
	}
	
	return res;
}
/*Public Function FunSQLUpdate_Compra_venta_div(ByVal piBanco As Integer, _
            ByVal psChequera As String, ByVal pdImporte As Double, _
            ByVal plNoEmpresa As Long, _
            ByVal plFolioDet_reparado As Long) As Long*/
@SuppressWarnings("unchecked")
public int actualizarCompraVenta(int piBanco, String psChequera, double pdImporte, int plNoEmpresa, double plFolioDetReparado)
{
	
	int plNofolioDet1000=0, res=0;;
	String psNoDocto1000="", sql="";
	List<ParamCompraVentaDivisaDto>list=new ArrayList<ParamCompraVentaDivisaDto>();
	try{
	    sql+= " Select no_folio_det, no_docto ";
	    sql+= " FROM movimiento ";
	    sql+= " WHERE ";
	    sql+= " id_estatus_mov = 'L' ";
	    sql+= " and id_tipo_operacion = 1000 ";
	    sql+= " and origen_mov = 'CVD' ";
	    sql+= " and id_chequera = '" + psChequera + "'";
	    sql+= " and id_banco = " + piBanco;
	    sql+= " and importe = " + pdImporte;
	    sql+= " and no_empresa = " + plNoEmpresa;
	    
	    list= jdbcTemplate.query(sql, new RowMapper(){
			public ParamCompraVentaDivisaDto mapRow(ResultSet rs, int idx) throws SQLException {
				ParamCompraVentaDivisaDto cons = new ParamCompraVentaDivisaDto();
					cons.setNoFolioDet(rs.getInt("no_folio_det"));
					cons.setNoDocto(rs.getString("no_docto"));
				return cons;
			}});	 
	    
	    
	    if(list.size()>0)
	    {
	    	plNofolioDet1000 = list.get(0).getNoFolioDet(); 
	    	psNoDocto1000 = list.get(0).getNoDocto();
	       
	       sql = "";
	       sql+= " UPDATE movimiento set id_estatus_mov = 'Q' ";
	       sql+= " WHERE no_folio_det = " + plNofolioDet1000;
	       sql+= "; ";
	        
	        //Actualizar el no_docto del ingreso reparado, asi como que apunte al egreso
	       sql+= " UPDATE movimiento set no_docto = '" + psNoDocto1000 + "' ";
	       sql+= "     ,folio_ref = ( SELECT no_folio_det ";
	       sql+= "                   FROM movimiento ";
	       sql+= "                   WHERE id_tipo_operacion = 3200 ";
	       sql+= "                       and origen_mov = 'CVD' ";
	       sql+= "                       and no_docto = '" + psNoDocto1000 + "'";
	       sql+= "                       and id_tipo_movto = 'E' )";
	       sql+= " WHERE no_folio_det = " + plFolioDetReparado;
	        
	        res=jdbcTemplate.queryForInt(sql);
	    } 
	}catch(Exception e){
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:actualizarCompraVenta");
	}
	
	return res;
}
/*Public Function FunSQLSelect_pagoRechazado(ByVal plNoFolioBanco As Long, _
            ByVal piBanco As Integer, ByVal psChequera As String, _
            ByVal pdImporte As Double)    )*/
@SuppressWarnings("unchecked")
public List<MovimientoDto>consultarPagoRechazado(String referencia, int piBanco, String psChequera, double pdImporte)
{
	String sql="", sqlWhere="";
	int plNoFolioBanco=0;
	plNoFolioBanco=Integer.parseInt(referencia);
	List<MovimientoDto>listRetorno=new ArrayList<MovimientoDto>();
	try{
		if(gsDBM.equals("SQL SERVER")|| gsDBM.equals("SYBASE"))
		{
	        sqlWhere+=" WHERE no_folio_det = (Select convert(int,e.referencia) from movto_banca_e e ";
	        sqlWhere+="            where e.folio_banco = " + plNoFolioBanco;
	        sqlWhere+="            and e.id_banco = m.id_banco ";
	        sqlWhere+="            and e.id_chequera = m.id_chequera ";
	        sqlWhere+="            and e.importe = m.importe) ";
	        sqlWhere+=" and m.id_banco = " + piBanco;
	        sqlWhere+=" and m.id_chequera = '" + psChequera + "'";
	        sqlWhere+=" and m.importe = " + pdImporte;
	        
	        sql+="";
	        sql+=" SELECT * from movimiento m ";
	        sql+= sqlWhere;
	        sql+=" UNION ";
	        sql+=" SELECT * from hist_movimiento m ";
	        sql+=sqlWhere;
		}
		else if(gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2") )
		{
			if(gsDBM.equals("DB2"))
				sqlWhere+=" WHERE no_folio_det = (Select cast(e.referencia as integer) ";
			else
				sqlWhere+=" WHERE no_folio_det = (Select TO_NUMBER(e.referencia,'999999999') ";
			
			sqlWhere+=" from movto_banca_e e ";
            //'sqlWhere+= sSQL_Where & Chr(10) & "            where TO_NUMBER(e.folio_banco,'999999999') = " & plNoFolioBanco
            sqlWhere+="            where e.folio_banco = " + plNoFolioBanco;
            sqlWhere+="            and e.id_banco = m.id_banco ";
            sqlWhere+="            and e.id_chequera = m.id_chequera ";
            sqlWhere+="            and e.importe = m.importe) ";
            sqlWhere+=" and m.id_banco = " + piBanco;
            sqlWhere+=" and m.id_chequera = '" + psChequera + "'";
            sqlWhere+=" and m.importe = " + pdImporte;
            
            sql+="";
            sql+="SELECT * from movimiento m ";
            sql+=sqlWhere;
            sql+=" UNION ";
            sql+=" SELECT * from hist_movimiento m ";
            sql+=sqlWhere;
		}
		//se agregaron a la lista los campos que se utilizan en caso de utilizar mas se agregan ya que es un * from 
		 listRetorno= jdbcTemplate.query(sql, new RowMapper(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons = new MovimientoDto();
					cons.setNoFolioDet(rs.getInt("no_folio_det"));
					cons.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
                    cons.setTipoCancelacion(rs.getString("ps_tipo_cancelacion"));
                    cons.setIdEstatusMov(rs.getString("id_estatus_mov"));
                    cons.setIdFormaPago(rs.getInt("id_forma_pago"));
                    cons.setBEntregado(rs.getString("b_entregado"));
                    cons.setIdTipoMovto(rs.getString("id_tipo_movto"));
                    cons.setImporte(rs.getDouble(("importe")));
                    cons.setNoEmpresa(rs.getInt("no_empresa"));
                    cons.setNoCuenta(rs.getInt("no_cuenta"));
                    cons.setIdChequera(rs.getString("id_chequera"));
                    cons.setIdBanco(rs.getInt("id_banco"));
                    cons.setNoDocto(rs.getString("no_docto"));
                    cons.setLoteEntrada(rs.getInt("lote_entrada"));
                    cons.setBSalvoBuenCobro(rs.getString("b_salvo_buen_cobro"));
                    cons.setFecConfTrans(rs.getDate("fec_conf_trans"));
                    cons.setIdDivisa(rs.getString("id_divisa"));
					return cons;
				}});	 
	}catch(Exception e){
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:consultarPagoRechazado");
	}
	 return listRetorno;
}

/*Public Function FunSQLUpdate_docto_rechazo(ByVal psNoDocto As String, ByVal plNoFolioDet As Long) As Long*/
public void actualizarDoctoRechazado(String psNoDocto, double plNoFolioDet)
{
	String sql="";
	try
	{
		sql = "";
		sql+=" UPDATE  ";
		sql+=" movimiento ";
		sql+=" SET ";
		sql+=" no_docto = '" + psNoDocto + "' ";
		sql+=" WHERE ";
		sql+=" no_folio_det = " + plNoFolioDet;
		
		jdbcTemplate.update(sql);
		
		sql = "";
		sql+=" UPDATE  ";
		sql+=" hist_movimiento ";
		sql+=" SET ";
		sql+=" no_docto = '" + psNoDocto + "' ";
		sql+=" WHERE ";
		sql+=" no_folio_det = " + plNoFolioDet;

		jdbcTemplate.update(sql);
			 
	}catch(Exception e){
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:actualizarDoctoRechazado");
	}
	
}

/*Public Function FunSQLUpdate215(ByVal pvvValor1 As Variant) As Long
'Funci�n autom�tica para reemplazar el uso de sentencias SQL
'Recibe los par�metros necesarios para ejecutar la sentencia de la forma frmTransIng
 Dim sSQL As String
On Error GoTo HayError*/
    public int cancelarMovimientos(int[] secuencia)
	{
	   	int upDateUno=0,upDateDos=0,cont=0;
    	String sql = "";
	   	try{
	   		for(int i=0; i<secuencia.length;i++){
		   		sql = "";
			    sql+= " UPDATE movto_banca_e ";
			    sql+= " SET id_estatus_trasp = 'X'";
			    sql+= " WHERE secuencia = " +secuencia[i];
			    	upDateUno=jdbcTemplate.update(sql);
			    sql="";
			    sql+= " UPDATE movto_banca_e ";
			    sql+= " SET id_estatus_trasp = 'X'";
			    sql+= " WHERE secuencia = " +secuencia[i];
			    	upDateDos=jdbcTemplate.update(sql);	
			    if(upDateUno>0 && upDateDos>0)
			    	cont++;   
	   		}
	   		
		    
	   	}
	   	catch(Exception e){
	   		bitacora.insertarRegistro(new Date().toString() + " "+ e.toString()
			+ "P:BancaElectronica, C:ImportaBancaElectronicaDao, M:cancelarMovimientos");
	   	}
	   	return cont;
	   
	}
    
    public int actualizaMovtoBancaE(int secuencia) {
	   	int res = 0;
    	StringBuffer sql = new StringBuffer();
    	
	   	try{
	   		sql.append("UPDATE movto_banca_e ");
	   		sql.append(" SET id_estatus_trasp = 'T'");
	   		sql.append(" WHERE secuencia in (" + secuencia + ")");
		    System.out.println("actualiaMovtoBancaE "+sql);
		    res = jdbcTemplate.update(sql.toString());
	   	}
	   	catch(Exception e){
	   		bitacora.insertarRegistro(new Date().toString() + " "+ e.toString() + "P:BancaElectronica, C:ImportaBancaElectronicaDao, M:actualizaMovtoBancaE");
	   	}
	   	return res;
	}
    
    public int existeMovto(int secuencia) {
	   	int res = 0;
    	StringBuffer sql = new StringBuffer();
    	
	   	try {
	   		sql.append(" SELECT COUNT(*) FROM parametro WHERE folio_seg = "+ secuencia +" \n");
	   		sql.append("and id_estatus_reg='A' ");
	   		System.out.println("existe movto "+sql.toString());
	   		res = jdbcTemplate.queryForInt(sql.toString());
	   	}catch(Exception e) {
	   		bitacora.insertarRegistro(new Date().toString() + " "+ e.toString() + "P:BancaElectronica, C:ImportaBancaElectronicaDao, M:existeMovto");
	   	}
	   	return res;
	}
    
    @SuppressWarnings("unchecked")
    public List<ParamReglaConceptoDto>obtenerGrupoRubro(String idTipoOpe) {
    	StringBuffer sql = new StringBuffer();
    	List<ParamReglaConceptoDto> listGrupoRubro = new ArrayList<ParamReglaConceptoDto>();
    	
    	try{
    		sql.append(" SELECT top 1 ltrim(rtrim(id_grupo)) as id_grupo, ltrim(rtrim(id_rubro)) as id_rubro \n");
    		sql.append(" FROM cve_tipo_operacion cto, cat_cve_operacion cco \n");
    		sql.append(" WHERE cto.id_cve_operacion = cco.id_cve_operacion \n");
    		sql.append(" 	And cto.id_tipo_operacion = "+ idTipoOpe +" \n");
    		sql.append(" ORDER BY cto.secuencia \n");
    		
    	    System.out.println("Query para buscar grupo y rubro en la tabla cat_cve_operacion: \n" + sql.toString());
    	    
    	    listGrupoRubro = jdbcTemplate.query(sql.toString(), new RowMapper(){
    			public ParamReglaConceptoDto mapRow(ResultSet rs, int idx) throws SQLException {
    				ParamReglaConceptoDto cons = new ParamReglaConceptoDto();
    				cons.setIdGrupo(rs.getString("id_grupo"));
    				cons.setIdRubro(rs.getString("id_rubro"));
    				return cons;
    			}});
    	}catch(Exception e){
    		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
    		+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:obtenerGrupoRubro");
    	} 
    	return listGrupoRubro;
    }
    
	public int importaBancaElectronica() {
		int result = 0;
		
		try{
			HashMap<Object, Object> inParams = new HashMap<Object, Object>();
			System.out.println("ejecutando....");
		
			StoredProcedure sp = new StoredProcedure(jdbcTemplate.getDataSource(), "set_dalton.dbo.sp_consultaMovimientosBancaE") {};
			sp.execute(inParams);
			
			result = 1;
		}
		catch(Exception e){
			e.printStackTrace();
			logger.error(e);
			result = -1;
		}
		return result;
	}
    
/**
 * 
 * @param dataSource
 */
public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Banca Electronica, C:ImportaBancaElectronicaDao, M:setDataSource");
		}
	}
}
