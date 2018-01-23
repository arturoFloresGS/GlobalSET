package com.webset.set.bancaelectronica.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.bancaelectronica.dto.CriterioBusquedaDto;
import com.webset.set.bancaelectronica.dto.MovimientoDto;
import com.webset.set.bancaelectronica.dto.ParametroBusquedaMovimientoDto;

/**
 * Clase que contiene los metodos "consultarBusqueda", "consultarEnvioMssPayment", "seleccionarNafin", "actualizarMovimientoTipoEnvio", 
 *	"insertaMovimiento", "obtenerCasaCambio" 
 *  
 */
public class MovimientoDao {
	private static Logger log = Logger.getLogger(MovimientoDao.class);
	private JdbcTemplate jdbcTemplate;
	private ConsultasGenerales gral;
	
	
	/**Public Function FunSQLSelect858(ByVal plNo_empresa As Long, ByVal pbOptMismo As Boolean, _
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
	 * query del boton busqueda
	 * @param dto
	 * @return List<MovimientoDto>
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")	
	public List<MovimientoDto> consultarBusqueda(ParametroBusquedaMovimientoDto dto) throws Exception{
		StringBuffer sb = new StringBuffer();
		boolean pbH2HBital;
		boolean locales=false;
		int i=dto.getPsDivisaA().indexOf("|");
		CriterioBusquedaDto aux = new CriterioBusquedaDto();
        aux.setPdMontoIni(dto.getPdMontoIni());
        aux.setPdMontoFin(dto.getPdMontoFin());
        aux.setPlBancoReceptor(dto.getPlBancoReceptor());
        aux.setPsFechaValor(dto.getPsfechaValor());
        aux.setPsFechaValorOrig(dto.getPsFechaValorOrig());
        aux.setPsDivisa(dto.getPsDivisa());
        aux.setPsDivision(dto.getPsDivision());
		if (dto.getPsDivisaA().substring(i+1).equals("True"))
			locales = true;
		if(i!=0)
			dto.setPsDivisaA(dto.getPsDivisaA().substring(0,i));
	    pbH2HBital=true;
	    if(gral.consultarConfiguraSet(232).equals("SI") && dto.getPiBanco()== 21){
	    	pbH2HBital = true;
	    }
	    sb=null;
	    //Select Case gsDBM
        	//Case "SQL SERVER", "SYBASE":
	    sb.append(" SELECT DISTINCT m.fec_valor_original, m.id_tipo_operacion, b2.id_banco_city, b2.inst_finan, m.observacion, ");
	    sb.append("\n  	c.desc_sucursal as suc_origen, ctas.sucursal as suc_destino, m.id_banco, b.b_layout_comerica, ");
	    sb.append("\n 	m.no_docto, m.fec_operacion, o.desc_cve_operacion, ");
	    sb.append("\n 	CASE WHEN COALESCE(b.transfer_casa_cambio, 'N') = 'S' AND m.origen_mov='CVT' ");
	    sb.append("\n		THEN m.importe_original ");
	    sb.append("\n 		ELSE m.importe END as importe, ");
	    sb.append("\n  	CASE WHEN COALESCE(b.transfer_casa_cambio, 'N') = 'S' AND m.origen_mov='CVT' ");
	    sb.append("\n		THEN m.id_divisa_original ");
	    sb.append("\n		ELSE m.id_divisa END as id_divisa, ");
	    sb.append("\n 	m.id_chequera_benef, m.origen_mov, m.division, ");
	    sb.append("\n 	'' as nombre_banco_benef, b2.desc_banco as desc_banco_benef,");
	    sb.append("\n 	CASE WHEN ");
	    sb.append("\n 		( SELECT COALESCE(b.transfer_casa_cambio, 'N') ");
	    sb.append("\n 			FROM cat_banco b ");
	    sb.append("\n 			WHERE b.id_banco = m.id_banco ) = 'S' and m.origen_mov='CVT' ");
	    sb.append("\n 		THEN ( SELECT COALESCE(pp.razon_social, '') ");
	    sb.append("\n 				FROM persona pp ");
	    sb.append("\n 				WHERE pp.no_persona = m.no_cliente ");
	    sb.append("\n 					AND pp.id_tipo_persona = 'P' ) ");
	    sb.append("\n 		ELSE m.beneficiario END as beneficiario, ");
	    sb.append("\n 	m.concepto, m.no_folio_det, m.no_empresa, m.id_chequera, m.fec_valor, ");
	    sb.append("\n 	m.id_banco_benef, ctas.plaza as plaza_benef, c.desc_plaza as plaza, m.no_cliente, b2.desc_banco_inbursa, ");
	    sb.append("\n 	ctas.id_clabe as clabe_benef, ctas.aba, ctas.swift_code, ctas.aba_intermediario, ctas.swift_intermediario, ");
	    sb.append("\n 	ctas.aba_corresponsal, ctas.swift_corresponsal, ");
	    if(dto.getPsDivisa().equals("DLS")){
	    	sb.append("\n 	(SELECT b3.desc_banco FROM cat_banco b3 where b3.id_banco = ctas.id_bank_true) AS  nom_banco_intermediario, ");
		    sb.append("\n 	(SELECT b4.desc_banco FROM cat_banco b4 where b4.id_banco = ctas.id_bank_corresponding) AS nom_banco_corresponsal, ");
	    }
	    else{
		    sb.append("\n 	' ' AS nom_banco_intermediario,  ");
		    sb.append("\n 	' ' AS nom_banco_corresponsal, ");
	    }
	    sb.append("\n 	b.valida_CLABE, e.id_contrato_mass, e.id_contrato_wlink, p.rfc as rfc_benef, m.no_factura,e.nom_empresa, ");
	    sb.append("\n 	CASE WHEN ctas.id_banco is null THEN 'N' ELSE 'S' END AS existe_chequera_prov, pp.rfc, ");
	    if(pbH2HBital)
	    	sb.append("\n 	u.desc_usuario_bital,s.desc_servicio_bital, ");
	    sb.append("\n	ctas.especiales, p.equivale_persona, ctas.complemento, (select max(id_tipo_envio) from cat_tipo_envio_layout ctt where ctt.id_banco = m.id_banco and tipo_envio_layout = ctas.tipo_envio_layout) as tipo_envio_layout, ");
	    //MAS 04/09/02007 se agrega para validar la divisa de la chequera de pago contra la del docto
	    sb.append("\n 	(SELECT id_divisa FROM cat_cta_banco ccb WHERE ccb.no_empresa = m.no_empresa, ");
	    sb.append("\n 		AND ccb.id_banco = m.id_banco AND ccb.id_chequera = m.id_chequera) AS divisa_chequera, ");
	    sb.append("\n 	d.ciudad +  ', ' +  d.id_estado as direccion_benef , (SELECT pa.desc_pais FROM cat_pais pa WHERE pa.id_pais = 'MX') AS pais_benef, m.cve_control AS clave, ");
	    sb.append("\n 				FROM movimiento m, cat_cve_operacion o, ");
	    
	    sb.append("\n 				cat_banco b, cat_cta_banco c , ctas_banco ctas, cat_banco b2, ");
	    sb.append("\n				empresa e,persona p, persona pp, direccion d");
	    if(pbH2HBital)
	    	sb.append(", cat_usuario_bital u, cat_servicio_bital s ");
	    sb.append("\n 				WHERE CONVERT(int, m.no_cliente) = p.no_persona ");
	    sb.append("\n 				AND p.id_tipo_persona in('P','K') ");
	    if(pbH2HBital){
	        sb.append("\n 				AND e.id_usuario_bital *= u.id_usuario_bital ");
		    sb.append("\n 				AND e.id_servicio_bital *= s.id_servicio_bital ");
	    }
	    sb.append("\n 				AND m.no_empresa = pp.no_empresa ");
	    sb.append("\n 				AND m.no_empresa = pp.no_persona ");
	    sb.append("\n 				AND pp.id_tipo_persona = 'E' ");
	    sb.append("\n 				AND m.no_empresa = e.no_empresa ");
	    sb.append("\n 				AND m.id_banco_benef *= b2.id_banco ");
	    sb.append("\n 				AND m.id_banco_benef *= ctas.id_banco ");
	    sb.append("\n 				AND m.id_chequera_benef *= ctas.id_chequera ");
	    sb.append("\n 				AND CONVERT(int, m.no_cliente) *= ctas.no_persona ");
	    sb.append("\n 				AND ((b.transfer_casa_cambio = 'S' AND m.id_tipo_operacion = 3000 AND m.id_estatus_mov = 'V' AND (m.id_divisa <> 'DLS' AND m.id_divisa <> 'MN')) OR m.id_estatus_mov = 'P') ");
	    sb.append("\n 				AND m.id_forma_pago = 3 ");
	    sb.append("\n 				AND m.id_cve_operacion = o.id_cve_operacion ");
	    sb.append("\n 				AND m.id_banco = b.id_banco ");
	    //sb.append("\n 				AND m.no_empresa = c.no_empresa ");
	    sb.append("\n 				AND m.id_banco = c.id_banco ");
	    sb.append("\n 				AND m.id_chequera = c.id_chequera ");
	    sb.append("\n 				AND m.id_tipo_movto = 'E' ");
	    sb.append("\n 				AND b.b_banca_elect  IN ('A','E') ");
	    sb.append("\n 				AND m.id_banco = " + dto.getPiBanco());
	    if(dto.getPlNoEmpresa()== 0)
	    	sb.append("\n 				AND m.no_empresa IN (SELECT no_empresa FORM usuario_empresa WHERE no_usuario = " + dto.getPiNoUsuario() + ")");
	    else
        sb.append("\n 					AND m.no_empresa = " + dto.getPlNoEmpresa() );
	    if(!dto.getPsDivisaA().equals("")){
	    	sb.append("\n 				AND CASE WHEN coalesce(b.transfer_casa_cambio, 'N') = 'S' and m.origen_mov='CVT' ");
		    sb.append("\n 					THEN m.id_divisa_original ");
		    sb.append("\n 					ELSE m.id_divisa END = '" + dto.getPsDivisaA() + "' ");
	    }
	    if(dto.isPbConvenioCIE())
	    	sb.append("\n 				AND SUBSTRING(id_chequera_benef,1,4) = 'CONV' ");
        else if (dto.getPiBanco() == 12 && dto.isPbOptMismo())
        	sb.append("\n 				AND SUBSTRING(id_chequera_benef,1,4) <> 'CONV' ");
	    if(dto.isPbSoloComericaACH())
	    	sb.append("\n 				AND m.id_servicio_be = 'ACH' ");
	    if(locales)
	    	sb.append("\n 				AND e.b_local = 'S' ");
	    sb.append(compararCriterioBusqueda(aux));
	    if(dto.isPbSpeua()){
	    	sb.append("\n 				AND m.id_banco <> m.id_banco_benef ");
	    	sb.append("\n 				AND m.importe >= 50000 ");
	    }
	    else if (dto.isPbOptMismo()){
	    	sb.append("\n 				AND m.id_banco = m.id_banco_benef ");
	    }
        else if (dto.isPbInternacional()){
        	sb.append("\n 				AND m.id_banco <> m.id_banco_benef ");
        }
        else{
	    	if(dto.getPiBanco()!= 12 && dto.getPsDivisaA().equals("MN")){
	    		sb.append("\n 			AND ((b.nac_ext = 'N' and b2.nac_ext = 'N') ");
	    		sb.append("\n 			OR (b2.nac_ext = 'N' and b.nac_ext = 'N')) ");
	    	}
	    	sb.append("\n 				AND m.id_banco <> m.id_banco_benef ");
        }
	    sb.append("\n 				AND m.origen_mov <> 'FIL' ");
	    sb.append("\n 				AND no_cliente not in (" + gral.consultarConfiguraSet(206) + ")" ); //'<> '999999' 
	    sb.append("\n 				AND CONVERT(int, m.no_cliente) *= d.no_persona ");
	    sb.append("\n 				AND d.id_tipo_direccion = 'OFNA' ");
	    sb.append("\n 				AND d.no_direccion = 1 ");
	    sb.append("\n 				AND d.id_tipo_persona = 'P' ");
	    sb.append("\n 				AND d.no_empresa = 552 ");
	    //********************************* ORIGEN MOV = FIL
	    sb.append("\n 	UNION ALL ");
	    sb.append("\n 	SELECT DISTINCT m.fec_valor_original, m.id_tipo_operacion, b2.id_banco_city, b2.inst_finan, m.observacion, c.desc_sucursal AS suc_origen, ");
	    sb.append("\n 			m.sucursal AS suc_destino, m.id_banco, b.b_layout_comerica, ");
	    sb.append("\n 			m.no_docto, m.fec_operacion, o.desc_cve_operacion, ");
	    sb.append("\n 			CASE WHEN COALESCE(b.transfer_casa_cambio, 'N') = 'S' AND m.origen_mov='CVT' ");
	    sb.append("\n 				THEN m.importe_original ");
	    sb.append("\n 				ELSE m.importe END AS importe, ");
	    sb.append("\n 			CASE WHEN COALESCE(b.transfer_casa_cambio, 'N') = 'S' and m.origen_mov='CVT' ");
	    sb.append("\n 				THEN m.id_divisa_original ");
	    sb.append("\n 				ELSE m.id_divisa END as id_divisa, ");
	    sb.append("\n 			m.id_chequera_benef, m.origen_mov, m.division, ");
	    sb.append("\n 			'' AS nombre_banco_benef, b2.desc_banco AS desc_banco_benef, ");
	    sb.append("\n 			CASE WHEN ");
	    sb.append("\n 					( SELECT COALESCE(b.transfer_casa_cambio, 'N') ");
	    sb.append("\n 						FROM cat_banco b ");
	    sb.append("\n 						WHERE b.id_banco = m.id_banco ) = 'S' AND m.origen_mov='CVT' ");
	    sb.append("\n 				THEN ( SELECT COALESCE(pp.razon_social, '') ");
	    sb.append("\n 						FROM persona pp ");
	    sb.append("\n 						WHERE pp.no_persona = m.no_cliente ");
	    sb.append("\n 						 	AND pp.id_tipo_persona = 'P' ) ");
	    sb.append("\n 				ELSE m.beneficiario END as beneficiario, ");
	    sb.append("\n 			m.concepto, m.no_folio_det, m.no_empresa, ");
	    sb.append("\n 			m.id_chequera, m.fec_valor, m.id_banco_benef, m.plaza as plaza_benef, ");
	    sb.append("\n 			c.desc_plaza AS plaza, m.no_cliente, b2.desc_banco_inbursa, ");
	    sb.append("\n 			m.clabe AS clabe_benef, '' AS aba, '' AS swift_code, ");
	    sb.append("\n 			'' as aba_intermediario, '' as Swift_intermediario, ");
	    sb.append("\n 			'' as Aba_corresponsal, '' as Swift_corresponsal, ");
	    sb.append("\n 			'' as nom_banco_intermediario, ");
	    sb.append("\n 			'' as nom_banco_corresponsal, ");
	    sb.append("\n 			b.valida_CLABE, e.id_contrato_mass, e.id_contrato_wlink,");
	    sb.append("\n 			CASE WHEN m.origen_mov = 'FIL' THEN 'AAA999999AAA' "); //'RFC Moral comodin
	    sb.append("\n 					ELSE 'AAAA999999XXX'  "); //'Para origen FYE incidentales 'RFC Fisico comodin
	    sb.append("\n 				END as rfc_benef, ");
	    sb.append("\n 			m.no_factura,e.nom_empresa, 'S' AS existe_chequera_prov, pp.rfc, ");
	    if(pbH2HBital)
	    	sb.append("\n 			u.desc_usuario_bital,s.desc_servicio_bital, ");
	    sb.append("\n 			'' as especiales, '' as equivale_persona, ''  as complemento, 0 as tipo_envio_layout, ");
        //MAS 04/09/02007 se agrega para validar la divisa de la chequera de pago contra la del docto
	    sb.append("\n 			(SELECT id_divisa FROM cat_cta_banco ccb WHERE ccb.no_empresa = m.no_empresa, ");
	    sb.append("\n 				AND ccb.id_banco = m.id_banco AND ccb.id_chequera = m.id_chequera) AS divisa_chequera, ");
	    sb.append("\n 			d.ciudad + ', ' + d.id_estado AS direccion_benef, (SELECT pa.desc_pais FROM cat_pais pa WHERE pa.id_pais = 'MX') AS pais_benef, m.cve_control AS clave ");
	    sb.append("\n 	FROM movimiento m, cat_cve_operacion o, ");
	    sb.append("\n 		cat_banco b, cat_cta_banco c , cat_banco b2,empresa e, persona pp, direccion d");
	    if(pbH2HBital)
		    sb.append(", cat_usuario_bital u, cat_servicio_bital s");
	    sb.append("\n 		WHERE  m.no_empresa = e.no_empresa ");
	    if(pbH2HBital) {
            sb.append("\n 			AND e.id_usuario_bital *= u.id_usuario_bital ");
            sb.append("\n 			AND e.id_servicio_bital *= s.id_servicio_bital ");
		}
	    sb.append("\n 			AND m.no_empresa = pp.no_empresa ");
	    sb.append("\n 			AND m.no_empresa = pp.no_persona ");
	    sb.append("\n 			AND pp.id_tipo_persona = 'E' ");
	    sb.append("\n 			AND m.id_banco_benef *= b2.id_banco ");
	    sb.append("\n 			AND ((b.transfer_casa_cambio = 'S' AND m.id_tipo_operacion = 3000 AND m.id_estatus_mov = 'V' AND (m.id_divisa <> 'DLS' AND m.id_divisa <> 'MN')) OR m.id_estatus_mov = 'P') ");
	    sb.append("\n 			AND m.id_forma_pago = 3 ");
	    sb.append("\n 			AND m.id_cve_operacion = o.id_cve_operacion ");
	    sb.append("\n 			AND m.id_banco = b.id_banco ");
	    sb.append("\n 			AND m.id_banco = c.id_banco ");
	    sb.append("\n 			AND m.id_chequera = c.id_chequera AND m.id_tipo_movto = 'E' ");
	    sb.append("\n 			AND b.b_banca_elect  in ('A','E') ");
	    sb.append("\n 			AND m.id_banco = " + dto.getPiBanco());
	    if(dto.getPlNoEmpresa() == 0)
	    	sb.append("\n 			AND m.no_empresa IN (SELECT no_empresa FROM usuario_empresa WHERE no_usuario = " + dto.getPiNoUsuario() + ")");
	    else
	    	sb.append("\n 			AND m.no_empresa = " + dto.getPlNoEmpresa());
	   	if(!dto.getPsDivisaA().equals("")){
	   		sb.append("\n 			AND CASE WHEN COALESCE(b.transfer_casa_cambio, 'N') = 'S' AND m.origen_mov='CVT' ");
	   		sb.append("\n 					THEN m.id_divisa_original ");
	   		sb.append("\n 					ELSE m.id_divisa END = '" + dto.getPsDivisaA() + "' ");
	   	}
	    if(dto.isPbSoloComericaACH())
	    	sb.append("\n 			AND m.id_servicio_be = 'ACH' ");
	    if(locales)
	    	sb.append("\n 			AND e.b_local = 'S' ");
	    sb.append(compararCriterioBusqueda(aux));
	    if(dto.isPbSpeua()){
	    	sb.append("\n 			AND m.id_banco <> m.id_banco_benef ");
	    	sb.append("\n 			AND m.importe >= 50000 ");
	    }
	    else if(dto.isPbOptMismo())
	    	sb.append("\n 			AND m.id_banco = m.id_banco_benef ");
        else if(dto.isPbInternacional()){
        	sb.append("\n 			AND b.nac_ext = 'N' and b2.nac_ext = 'E' ");
        	sb.append("\n 			AND m.id_banco <> m.id_banco_benef ");
        }
	    else{
	    	sb.append("\n 			AND ((b.nac_ext = 'N' and b2.nac_ext = 'N') ");
	    	sb.append("\n 			OR (b2.nac_ext = 'N' and b.nac_ext = 'N')) ");
	    	sb.append("\n 			AND m.id_banco <> m.id_banco_benef ");
	    }
	    sb.append("\n 			AND (m.origen_mov = 'FIL' ");
	    sb.append("\n 			OR no_cliente IN(" + gral.consultarConfiguraSet(206)+ "))"); //' = '999999') 
	    sb.append("\n 			AND m.no_empresa = d.no_persona ");
	    sb.append("\n 			AND pp.no_persona = d.no_persona ");
	    sb.append("\n 			AND d.id_tipo_direccion = 'OFNA' ");
	    sb.append("\n 			AND d.no_direccion = 1 ");
	    sb.append("\n 			AND d.id_tipo_persona = 'P' ");
	    sb.append("\n 			AND d.no_empresa = 552 ");
	    sb.append("\n 		ORDER BY m.importe ");
	    //if(gsDBM=="SYBASE")
	    	//sb.append("\n 	AT ISOLATION READ UNCOMMITTED ");
	   
	    	//case DB2, POSTGRESQL
	    /*sb.append(" SELECT DISTINCT m.fec_valor_original, m.id_tipo_operacion, b2.id_banco_city, b2.inst_finan, m.observacion, ");
	    sb.append("\n  	c.desc_sucursal as suc_origen, ctas.sucursal as suc_destino, m.id_banco, b.b_layout_comerica, ");
	    sb.append("\n 	m.no_docto, m.fec_operacion, o.desc_cve_operacion, ");
	    sb.append("\n 	CASE WHEN COALESCE(b.transfer_casa_cambio, 'N') = 'S' AND m.origen_mov='CVT' ");
	    sb.append("\n		THEN m.importe_original ");
	    sb.append("\n 		ELSE m.importe END as importe, ");
	    sb.append("\n  	CASE WHEN COALESCE(b.transfer_casa_cambio, 'N') = 'S' AND m.origen_mov='CVT' ");
	    sb.append("\n		THEN m.id_divisa_original ");
	    sb.append("\n		ELSE m.id_divisa END as id_divisa, ");
	    sb.append("\n 	m.id_chequera_benef, m.origen_mov, m.division, ");
	    sb.append("\n 	'' as nombre_banco_benef, b2.desc_banco as desc_banco_benef,");
	    sb.append("\n 	CASE WHEN ");
	    sb.append("\n 		( SELECT COALESCE(b.transfer_casa_cambio, 'N') ");
	    sb.append("\n 			FROM cat_banco b ");
	    sb.append("\n 			WHERE b.id_banco = m.id_banco ) = 'S' and m.origen_mov='CVT' ");
	    sb.append("\n 		THEN ( SELECT COALESCE(pp.razon_social, '') ");
	    sb.append("\n 				FROM persona pp ");
	    sb.append("\n 				WHERE pp.no_persona = m.no_cliente ");
	    sb.append("\n 					AND pp.id_tipo_persona = 'P' ) ");
	    sb.append("\n 		ELSE m.beneficiario END as beneficiario, ");
	    sb.append("\n 	m.concepto, m.no_folio_det, m.no_empresa, m.id_chequera, m.fec_valor, ");
	    sb.append("\n 	m.id_banco_benef, ctas.plaza as plaza_benef, c.desc_plaza as plaza, m.no_cliente, b2.desc_banco_inbursa, ");
	    sb.append("\n 	ctas.id_clabe as clabe_benef, ctas.aba, ctas.swift_code, ctas.aba_intermediario, ctas.swift_intermediario, ");
	    sb.append("\n 	ctas.aba_corresponsal, ctas.swift_corresponsal, ");
	    if(dto.getPsDivisa().equals("DLS")){
	    	sb.append("\n 	(SELECT b3.desc_banco FROM cat_banco b3 where b3.id_banco = ctas.id_bank_true) AS  nom_banco_intermediario, ");
		    sb.append("\n 	(SELECT b4.desc_banco FROM cat_banco b4 where b4.id_banco = ctas.id_bank_corresponding) AS nom_banco_corresponsal, ");
	    }
	    else{
		    sb.append("\n 	' ' AS nom_banco_intermediario,  ");
		    sb.append("\n 	' ' AS nom_banco_corresponsal, ");
	    }
	    sb.append("\n 	b.valida_CLABE, e.id_contrato_mass, e.id_contrato_wlink, p.rfc as rfc_benef, m.no_factura,e.nom_empresa, ");
	    sb.append("\n 	CASE WHEN ctas.id_banco is null THEN 'N' ELSE 'S' END AS existe_chequera_prov, pp.rfc, ");
	    if(pbH2HBital)
	    	sb.append("\n 	u.desc_usuario_bital,s.desc_servicio_bital, ");
	    sb.append("\n	ctas.especiales, p.equivale_persona, ctas.complemento, (select max(id_tipo_envio) from cat_tipo_envio_layout ctt where ctt.id_banco = m.id_banco and tipo_envio_layout = ctas.tipo_envio_layout) as tipo_envio_layout, ");
	    //MAS 04/09/02007 se agrega para validar la divisa de la chequera de pago contra la del docto
	    sb.append("\n 	(SELECT id_divisa FROM cat_cta_banco ccb WHERE ccb.no_empresa = m.no_empresa, ");
	    sb.append("\n 		AND ccb.id_banco = m.id_banco AND ccb.id_chequera = m.id_chequera) AS divisa_chequera, ");
	    sb.append("\n 		d.ciudad ||  ', ' || d.id_estado as direccion_benef , (SELECT pa.desc_pais FROM cat_pais pa WHERE pa.id_pais = 'MX') AS pais_benef, m.cve_control AS clave, ");
	    sb.append("\n 				FROM movimiento m ");
	    if( gsDBM == "DB2") {
	    	sb.append("\n 		LEFT JOIN persona p ON(cast(m.no_cliente as integer) = p.no_persona and p.id_tipo_persona in('P','K') ) ");
	    	sb.append("\n 		LEFT JOIN cat_banco b2 ON(m.id_banco_benef = b2.id_banco) ");
	    	sb.append("\n 		LEFT JOIN ctas_banco ctas ON(cast(m.no_cliente as integer) = ctas.no_persona ");
	    }
	    else {
	    	sb.append("\n 		LEFT JOIN persona p ON(TO_NUMBER(m.no_cliente,'999999999') = p.no_persona and p.id_tipo_persona in('P','K') ) ");
	    	sb.append("\n 		LEFT JOIN cat_banco b2 ON(m.id_banco_benef = b2.id_banco) ");
	    	sb.append("\n 		LEFT JOIN ctas_banco ctas ON(TO_NUMBER(m.no_cliente,'999999999') = ctas.no_persona ");
	    }
	    sb.append("\n 			AND m.id_banco_benef = ctas.id_banco ");
	    sb.append("\n 			AND m.id_chequera_benef = ctas.id_chequera ) ");
	    if(pbH2HBital){
	    	sb.append("\n 		INNER JOIN (empresa e ");
	    	sb.append("\n 		LEFT JOIN cat_usuario_bital u ON(e.id_usuario_bital = u.id_usuario_bital) ");
	    	sb.append("\n 		LEFT JOIN cat_servicio_bital s ON (e.id_servicio_bital = s.id_servicio_bital) ");
	    	sb.append("\n 		)ON (m.no_empresa = e.no_empresa) ");
	    }
	    else
	    	sb.append("\n 	INNER JOIN empresa e  ON(m.no_empresa = e.no_empresa) ");
	    sb.append("\n	INNER JOIN persona pp  ON(m.no_empresa = pp.no_empresa and m.no_empresa=pp.no_persona and pp.id_tipo_persona = 'E') ");
	    sb.append("\n 	,cat_cve_operacion o,cat_banco b,cat_cta_banco c, direccion d ");
	    sb.append("\n 	WHERE ");
	    sb.append("\n 		m.id_forma_pago = 3 ");
	    sb.append("\n 		AND ((b.transfer_casa_cambio = 'S' AND m.id_tipo_operacion = 3000 AND m.id_estatus_mov = 'V' AND (m.id_divisa <> 'DLS' AND m.id_divisa <> 'MN')) OR m.id_estatus_mov = 'P') ");
	    sb.append("\n 		AND m.id_cve_operacion = o.id_cve_operacion ");
	    sb.append("\n 		AND m.id_banco = b.id_banco ");
	    //sb.append("\n 		AND m.no_empresa = c.no_empresa ");
	    sb.append("\n  		AND m.id_banco = c.id_banco ");
	    sb.append("\n 		AND m.id_chequera = c.id_chequera ");
	    sb.append("\n 		AND m.id_tipo_movto = 'E' ");
	    sb.append("\n 		AND b.b_banca_elect  in ('A','E') ");
	    sb.append("\n 		AND m.id_banco = " + dto.getPiBanco());
	    if(dto.getPlNoEmpresa()== 0)
	    	sb.append("\n 				AND m.no_empresa IN (SELECT no_empresa FORM usuario_empresa WHERE no_usuario = " + dto.getPiNoUsuario() + ")");
	    else
        sb.append("\n 					AND m.no_empresa = " + dto.getPlNoEmpresa() );
	    if(!dto.getPsDivisaA().equals("")){
	    	sb.append("\n 				AND CASE WHEN coalesce(b.transfer_casa_cambio, 'N') = 'S' and m.origen_mov='CVT' ");
		    sb.append("\n 					THEN m.id_divisa_original ");
		    sb.append("\n 					ELSE m.id_divisa END = '" + dto.getPsDivisaA() + "' ");
	    }
	    if(gsDBM = "DB2"){
	        if(dto.isPbConvenioCIE())
	        	sb.append("\n			AND SUBSTR(id_chequera_benef,1,4) = 'CONV' ");
	        else if (dto.getPiBanco() == 12 && dto.isPbOptMismo())
	        	sb.append("\n			AND SUBSTR(id_chequera_benef,1,4) <> 'CONV' ");
	    }
	    else{
	        if(dto.isPbConvenioCIE())
	        	sb.append("\n			AND SUBSTRING(id_chequera_benef,1,4) = 'CONV' "); 
	        else if (dto.getPiBanco() == 12 && dto.isPbOptMismo())
	        	sb.append("\n  			AND SUBSTRING(id_chequera_benef,1,4) <> 'CONV' ");
	    }
        if(dto.isPbSoloComericaACH())
        	sb.append("\n			AND m.id_servicio_be = 'ACH' ");
        if(locales)
        	sb.append("\n		  	AND e.b_local = 'S' " );
        sb.append(compararCriterioBusqueda(aux));
        if(dto.isPbSpeua()){
        	sb.append("\n		  	AND m.id_banco <> m.id_banco_benef " );
        	sb.append("\n		  	AND m.importe >= 50000 " );
        }
	    else if(dto.isPbOptMismo())
	    	sb.append("\n		  	AND m.id_banco = m.id_banco_benef " );
        else if (dto.isPbInternacional())
        	sb.append("\n		  	AND m.id_banco <> m.id_banco_benef ");
        else{
        	if(dto.getPiBanco() != 12 && dto.getPsDivisaA().equals("MN")){
        		sb.append("\n		  	AND ((b.nac_ext = 'N' and b2.nac_ext = 'N') ");
        		sb.append("\n		  	OR (b2.nac_ext = 'N' and b.nac_ext = 'N')) ");
        	}
        	sb.append("\n		  	AND m.id_banco <> m.id_banco_benef ");
        }
        sb.append("\n		  	AND m.origen_mov <> 'FIL' ");
        sb.append("\n		  	AND no_cliente NOT IN(" + gral.consultarConfiguraSet(206) + ") "); //'<> '999999' "
        sb.append("\n		  	AND m.no_empresa = d.no_persona ");
        sb.append("\n		  	AND pp.no_persona = d.no_persona ");
        sb.append("\n		  	AND d.id_tipo_direccion = 'OFNA' ");
        sb.append("\n		  	AND d.no_direccion = 1 ");
        sb.append("\n		  	AND d.id_tipo_persona = 'P' ");
        sb.append("\n		  	AND d.no_empresa = 552 ");
        //******************************ORIGEN MOV FIL
        sb.append("\n  	UNION ");
        sb.append("\n  	SELECT DISTINCT m.fec_valor_original, m.id_tipo_operacion, b2.id_banco_city,b2.inst_finan,m.observacion, c.desc_sucursal AS suc_origen, ");
        sb.append("\n  		m.sucursal AS suc_destino, m.id_banco,");
        sb.append("\n  		b.b_layout_comerica, ");
        sb.append("\n  		m.no_docto,m.fec_operacion,o.desc_cve_operacion, ");
        sb.append("\n  		CASE WHEN coalesce(b.transfer_casa_cambio, 'N') = 'S' ");
        sb.append("\n  	      THEN m.importe_original ");
        sb.append("\n  	      ELSE m.importe END as importe, ");
        sb.append("\n  		CASE WHEN coalesce(b.transfer_casa_cambio, 'N') = 'S' ");
        sb.append("\n  	      THEN m.id_divisa_original ");
        sb.append("\n  	      ELSE m.id_divisa END as id_divisa, ");
        sb.append("\n  	 	m.id_chequera_benef, m.origen_mov, m.division, ");
        sb.append("\n  		'' || '' as nombre_banco_benef, b2.desc_banco AS desc_banco_benef, ");
        sb.append("\n  		CASE WHEN ");
        sb.append("\n  	          ( SELECT coalesce(b.transfer_casa_cambio, 'N') ");
        sb.append("\n  		         FROM cat_banco b " );
        sb.append("\n  			       WHERE b.id_banco = m.id_banco ) = 'S' ");
        sb.append("\n  	      THEN ( SELECT coalesce(pp.razon_social, '') " );
        sb.append("\n  		         FROM persona pp " );
        sb.append("\n  				 WHERE pp.no_persona = m.no_cliente ");
        sb.append("\n  					AND pp.id_tipo_persona = 'P' ) ");
        sb.append("\n  	      ELSE m.beneficiario END as beneficiario, ");
        sb.append("\n  		m.concepto, m.no_folio_det, m.no_empresa, ");
        sb.append("\n  	 	m.id_chequera, m.fec_valor, ");
        sb.append("\n  		m.id_banco_benef, ");
        sb.append("\n  		m.plaza AS plaza_benef, ");
        sb.append("\n  		c.desc_plaza AS plaza, ");
        sb.append("\n  		m.no_cliente, ");
        sb.append("\n  		b2.desc_banco_inbursa, ");
        sb.append("\n  		m.clabe AS clabe_benef, ");
        sb.append("\n  	  	'' || '' AS aba, '' || '' AS swift_code, ");
        sb.append("\n  		'' || '' AS aba_intermediario,'' || '' AS Swift_intermediario, ");
        sb.append("\n  		'' || '' AS Aba_corresponsal, '' || '' AS Swift_corresponsal, ");
        sb.append("\n  		'' || '' AS nom_banco_intermediario, ");
        sb.append("\n  		'' || '' as nom_banco_corresponsal ");
        sb.append("\n  		,b.valida_CLABE, e.id_contrato_mass, e.id_contrato_wlink ");
        sb.append("\n  		,CASE WEHN m.origen_mov = 'FIL' THEN '' || 'AAA999999AAA' "); 	//'RFC Moral comodin
        sb.append("\n  			   ELSE '' || 'AAAA999999XXX' "); 							//'Para origen FYE incidentales 'RFC Fisico comodin
        sb.append("\n  				END AS rfc_benef ");
        sb.append("\n  		,m.no_factura,e.nom_empresa ");
        sb.append("\n  		,'S' || '' AS existe_chequera_prov, pp.rfc ");
        if(pbH2HBital)
        	sb.append("\n  		,u.desc_usuario_bital,s.desc_servicio_bital ");
        sb.append("\n  		, '' AS especiales, '' AS equivale_persona, '' AS complemento, 0 AS tipo_envio_layout ");
        //'MAS 04/09/02007 se agrega para validar la divisa de la chequera de pago contra la del docto
        sb.append("\n  		,(SELECT id_divisa FROM cat_cta_banco ccb WHERE ccb.no_empresa = m.no_empresa ");
        sb.append("\n  			AND ccb.id_banco = m.id_banco and ccb.id_chequera = m.id_chequera) as divisa_chequera ");
        sb.append("\n  	  	,d.ciudad || ', ' || d.id_estado as direccion_benef , (SELECT pa.desc_pais FROM cat_pais pa WHERE pa.id_pais = 'MX') AS pais_benef, m.cve_control AS clave" );
        sb.append("\n  		FROM movimiento m ");
        sb.append("\n  		   LEFT JOIN cat_banco b2 ON(m.id_banco_benef = b2.id_banco) ");
        if(pbH2HBital){
        	sb.append("\n  			INNER JOIN (empresa e ");
        	sb.append("\n  	        LEFT JOIN cat_usuario_bital u ON(e.id_usuario_bital = u.id_usuario_bital) ");
        	sb.append("\n  	        LEFT JOIN cat_servicio_bital s ON (e.id_servicio_bital = s.id_servicio_bital) ");
        	sb.append("\n  	  	)ON (m.no_empresa = e.no_empresa) ");
        }
        else
        	sb.append("\n  		INNER JOIN empresa e  ON(m.no_empresa = e.no_empresa) ");
        sb.append("\n  		 INNER JOIN persona pp  ON(m.no_empresa = pp.no_empresa and m.no_empresa=pp.no_persona and pp.id_tipo_persona = 'E')");
        sb.append("\n  	   	,cat_cve_operacion o,cat_banco b,cat_cta_banco c, direccion d");
        sb.append("\n  			 WHERE ");
        sb.append("\n  				 m.id_forma_pago = 3 ");
        sb.append("\n  				 AND m.id_estatus_mov = 'P' ");
        sb.append("\n  				 AND m.id_cve_operacion = o.id_cve_operacion ");
        sb.append("\n  				 AND m.id_banco = b.id_banco ");
     //sb.append("\n  				 AND m.no_empresa = c.no_empresa ");
        sb.append("\n  				 AND m.id_banco = c.id_banco ");
        sb.append("\n  	 			 AND m.id_chequera = c.id_chequera ");
        sb.append("\n  				 AND m.id_tipo_movto = 'E' ");
        sb.append("\n  				 AND b.b_banca_elect  in ('A','E') ");
        sb.append("\n  				 AND m.id_banco = " + dto.getPiBanco());
        if(dto.getPlNoEmpresa() == 0)
        	sb.append("\n  					AND m.no_empresa in (SELECT no_empresa FROM usuario_empresa WHERE no_usuario = " + dto.getPiNoUsuario() + ")");
        else
        	sb.append("\n  				AND m.no_empresa = " + dto.getPlNoEmpresa());
        if(!dto.getPsDivisa().equals("")){
        	sb.append("\n  				AND CASE WHEN coalesce(b.transfer_casa_cambio, 'N') = 'S' ");
        	sb.append("\n  					THEN m.id_divisa_original ");
        	sb.append("\n  		        	ELSE m.id_divisa END = '" + dto.getPsDivisaA() + "' ");
        }
        if(dto.isPbSoloComericaACH())
        	sb.append("\n  				AND m.id_servicio_be = 'ACH' ");
        if (locales)
        	sb.append("\n  			 AND e.b_local = 'S' ");
        sb.append(compararCriterioBusqueda(aux));
        if(dto.isPbSpeua()){
        	sb.append("\n  			    AND m.id_banco <> m.id_banco_benef ");
        	sb.append("\n  			 	AND m.importe >= 50000 ");
        }
        else if(dto.isPbOptMismo())
        	sb.append("\n  			 	AND m.id_banco = m.id_banco_benef ");
        else if (dto.isPbInternacional())
        	sb.append("\n  			 	AND m.id_banco <> m.id_banco_benef ");
        else{
	         if (dto.getPiBanco() != 12){
	        	 sb.append("\n  		AND ((b.nac_ext = 'N' and b2.nac_ext = 'N') ");
	        	 sb.append("\n  		OR (b2.nac_ext = 'N' and b.nac_ext = 'N'))");
	         }
	         sb.append("\n  			AND m.id_banco <> m.id_banco_benef");
        }
        sb.append("\n  			 AND (m.origen_mov = 'FIL' ");
        sb.append("\n  			 OR no_cliente in (" + gral.consultarConfiguraSet(206) + "))"); //' = '999999') "
        sb.append("\n  			 AND m.no_empresa = d.no_persona ");
        sb.append("\n  			 AND pp.no_persona = d.no_persona ");
        sb.append("\n  			 AND d.id_tipo_direccion = 'OFNA' ");
        sb.append("\n  			 AND d.no_direccion = 1 ");
        sb.append("\n  			 AND d.id_tipo_persona = 'P' ");
        sb.append("\n  			 AND d.no_empresa = 552 ");
        sb.append("\n  	 ORDER BY importe ");*/
	    try{
		    List<MovimientoDto> movimientos = jdbcTemplate.query(sb.toString(), new RowMapper(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto movi = new MovimientoDto();
					movi.setFecValorOriginal(rs.getDate("fec_valor_original"));
					movi.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
					movi.setIdBancoCity(rs.getInt("id_banco_city"));
					movi.setInstFinan(rs.getString("inst_finan"));
					movi.setObservacion(rs.getString("observacion"));
					movi.setSucOrigen(rs.getString("suc_origen"));
					movi.setSucDestino(rs.getString("suc_destino"));
					movi.setIdBanco(rs.getInt("id_banco"));
					movi.setBLayoutComerica(rs.getString("b_layout_comerica"));
					movi.setNoDocto(rs.getString("no_docto"));
					movi.setFecOperacion(rs.getDate("fec_operacion"));
					movi.setDescCveOperacion(rs.getString("desc_cve_operacion"));
					movi.setImporte(rs.getDouble("importe"));
					movi.setIdDivisa(rs.getString("id_divisa"));
					movi.setIdChequeraBenef(rs.getString("id_chequera_benef"));
					movi.setOrigenMov(rs.getString("origen_mov"));
					movi.setDivision(rs.getString("division"));
					movi.setNombreBancoBenef(rs.getString("nombre_banco_benef"));
					movi.setDescBancoBenef(rs.getString("desc_banco_benef"));
					movi.setBeneficiario(rs.getString("beneficiario"));
					movi.setConcepto(rs.getString("concepto"));
					movi.setNoFolioDet(rs.getInt("no_folio_det"));
					movi.setNoEmpresa(rs.getInt("no_empresa"));
					movi.setIdChequera(rs.getString("id_chequera"));
					movi.setFecValor(rs.getDate("fec_valor"));
					movi.setIdBancoBenef(rs.getInt("id_banco_benef"));
					movi.setPlazaBenef(rs.getString("plaza_benef"));
					movi.setPlaza(rs.getInt("plaza"));
					movi.setNoCliente(rs.getString("no_cliente"));
					movi.setDescBancoInbursa(rs.getString("desc_banco_inbursa"));
					movi.setClabeBenef(rs.getString("clabe_benef"));
					movi.setAba(rs.getString("aba"));
					movi.setSwiftCode(rs.getString("swift_code"));
					movi.setAbaIntermediario(rs.getString("aba_intermediario"));
					movi.setSwiftIntermediario(rs.getString("swift_intermediario"));
					movi.setAbaCorresponsal(rs.getString("aba_corresponsal"));
					movi.setSwiftCorresponsal(rs.getString("swift_corresponsal"));
					movi.setNomBancoIntermediario(rs.getString("nom_banco_intermediario"));
					movi.setNomBancoCorresponsal(rs.getString("nom_banco_corresponsal"));
					movi.setValidaCLABE(rs.getString("valida_CLABE"));
					movi.setIdContratoMass(rs.getInt("id_contrato_mass"));
					movi.setIdContratoWlink(rs.getString("id_contrato_wlink"));
					movi.setRfcBenef(rs.getString("rfc_benef"));
					movi.setNoFactura(rs.getString("rfc_benef"));
					movi.setNomEmpresa(rs.getString("nom_empresa"));
					movi.setExisteChequeraProv(rs.getString("existe_chequera_prov"));
					movi.setRfc(rs.getString("rfc"));
					movi.setEspeciales(rs.getString("especiales"));
					movi.setEquivalePersona(rs.getString("equivale_persona"));
					movi.setComplemento(rs.getString("complemento"));
					movi.setTipoEnvioLayout(rs.getInt("tipo_envio_layout"));
					movi.setDivisaChequera(rs.getString("divisa_chequera"));
					movi.setDireccionBenef(rs.getString("direccion_benef"));
					movi.setPaisBenef(rs.getString("pais_benef"));
					movi.setClave(rs.getString("clave"));
					return movi;
				}});
			return movimientos;
	    }catch(Exception e){
	    	log.error(new Date().toString() + " " + Bitacora.getStackTrace(e) + " P:BE, C:MovimientoDao, M:consultarBusqueda");
	    	return null;
	    }
	}
	/**
	 * Public Function FunSQLSelectEnvioMassPayment(ByVal plNo_empresa As Long, _
                                ByVal pbOptMismo As Boolean, ByVal piNoUsuario As Integer, _
                                ByVal ps_divisa As String, ByVal piBanco As Integer, _
                                ByVal pdMontoIni As Double, ByVal pdMontoFin As Double, _
                                ByVal plBancoReceptor As Long, ByVal psfechaValor As String, _
                                ByVal psFechaValorOrig As String, ByVal psDivisa As String, _
                                ByVal psDivision As String, _
                                Optional ByVal pbMassPorCheq As Boolean = False) 
	 */
	/**
	 * 
	 * @param dto
	 * @return List<MovimientoDto>
	 */
	@SuppressWarnings("unchecked")
	public List<MovimientoDto> consultarEnvioMssPayment(ParametroBusquedaMovimientoDto dto){
		StringBuffer sb = new StringBuffer();
		int i;
		boolean locales;
		CriterioBusquedaDto aux = new CriterioBusquedaDto();
        aux.setPdMontoIni(dto.getPdMontoIni());
        aux.setPdMontoFin(dto.getPdMontoFin());
        aux.setPlBancoReceptor(dto.getPlBancoReceptor());
        aux.setPsFechaValor(dto.getPsfechaValor());
        aux.setPsFechaValorOrig(dto.getPsFechaValorOrig());
        aux.setPsDivisa(dto.getPsDivisa());
        aux.setPsDivision(dto.getPsDivision());
		//Se envi� con un pipe en psDivisa si se quieren solo locales
	    locales = false;
	    i = dto.getPsDivisa().indexOf("|");
	    if(dto.getPsDivisa().substring(i+1).equals("True")) 
	    	locales = true;
	    if(i!=0)
	    	dto.setPsDivisa(dto.getPsDivisa().substring(0,i));
	    	sb.append(" SELECT DISTINCT m.fec_valor_original, m.id_tipo_operacion, b2.id_banco_city, b2.inst_finan, m.observacion, c.desc_sucursal as suc_origen, ");
	    	sb.append("\n 	ctas.sucursal AS suc_destino, m.id_banco, ");
	    	sb.append("\n 	b.b_layout_comerica, ");
	    	sb.append("\n 	m.no_docto, m.fec_operacion, o.desc_cve_operacion, ");
	    	sb.append("\n 	m.importe, m.id_divisa, m.id_chequera_benef, m.origen_mov, m.division, ");
	    	sb.append("\n 	'' AS nombre_banco_benef, b2.desc_banco AS desc_banco_benef, ");
	    	sb.append("\n 	m.beneficiario, m.concepto, m.no_folio_det, m.no_empresa, ");
	    	sb.append("\n 	m.id_chequera, m.fec_valor, ");
	    	sb.append("\n 	m.id_banco_benef, ");
	    	sb.append("\n 	ctas.plaza AS plaza_benef, ");
	    	sb.append("\n 	c.desc_plaza AS plaza, ");
	    	sb.append("\n 	m.no_cliente, ");
	    	sb.append("\n 	b2.desc_banco_inbursa, ");
	    	sb.append("\n 	ctas.id_clabe AS clabe_benef, ");
	    	sb.append("\n 	ctas.aba, ctas.swift_code, ");
	    	sb.append("\n 	ctas.aba_intermediario, ctas.swift_intermediario, ");
	    	sb.append("\n 	ctas.aba_corresponsal, ctas.swift_corresponsal, ");
	    	sb.append("\n 	b3.desc_banco AS nom_banco_intermediario, ");
	    	sb.append("\n 	b4.desc_banco  AS nom_banco_corresponsal, ");
	    	sb.append("\n 	b.valida_CLABE, e.id_contrato_mass, ");
	    	sb.append("\n 	p.rfc AS rfc_benef, ");
	    	sb.append("\n 	m.no_factura, e.nom_empresa, ");
	    	sb.append("\n 	p.equivale_persona, ");
	    	sb.append("\n 	ctas.especiales,  ctas.complemento ");
	    	sb.append("\n 	FROM movimiento m, cat_cve_operacion o, ");
	    	sb.append("\n 		cat_banco b, cat_cta_banco c , ctas_banco ctas, cat_banco b2, cat_banco b3, cat_banco b4, ");
	    	sb.append("\n 		empresa e, persona p ");
	    	sb.append("\n 	WHERE ");
	    	sb.append("\n 		CONVERT(int,m.no_cliente) = p.no_persona ");
	    	sb.append("\n 		AND p.id_tipo_persona in('P','K') ");
	    	sb.append("\n 		AND m.no_empresa = e.no_empresa ");
	    	sb.append("\n 		AND ctas.id_bank_true *= b3.id_banco ");
	    	sb.append("\n 		AND ctas.id_bank_corresponding *= b4.id_banco ");
	    	sb.append("\n 		AND m.id_banco_benef *= b2.id_banco ");
	    	sb.append("\n 		AND m.id_banco_benef = ctas.id_banco ");
	    	sb.append("\n 		AND m.id_chequera_benef = ctas.id_chequera ");
	    	sb.append("\n 		AND CONVERT(int, m.no_cliente) = ctas.no_persona ");
	    	sb.append("\n 		AND m.id_estatus_mov in('M','P') "); //M--> Mass Payment
	    	sb.append("\n 		AND m.id_tipo_operacion in(3000,3200) ");
	    	sb.append("\n 		AND m.id_forma_pago = 3 ");
	    	sb.append("\n 		AND m.id_cve_operacion = o.id_cve_operacion ");
	    	sb.append("\n 		AND m.id_banco = b.id_banco ");
	    	sb.append("\n 		AND m.no_empresa = c.no_empresa AND m.id_banco = c.id_banco ");
	    	sb.append("\n 		AND m.id_chequera = c.id_chequera AND m.id_tipo_movto = 'E'");
	    	sb.append("\n 		AND b.b_banca_elect  in ('A','E') ");
	    	sb.append("\n 		AND ((m.id_banco_benef<1000 and m.id_divisa = 'MN') OR m.id_divisa <> 'MN') ");
	    	sb.append("\n 		AND m.id_banco = " + dto.getPiBanco());
	    	if(dto.isPbMassPorCheq())
	    		sb.append("\n 		AND c.pago_mass = 'S' ");
	    	if(dto.getPlNoEmpresa() == 0)
	    		sb.append("\n 		AND m.no_empresa IN (SELECT no_empresa FROM usuario_empresa WHERE no_usuario = " + dto.getPiNoUsuario() + ")");
	    	else
	    		sb.append("\n 		AND m.no_empresa = " + dto.getPlNoEmpresa());
	    	if(!dto.getPsDivisaA().equals(""))
	    		sb.append("\n 		AND m.id_divisa = '" + dto.getPsDivisaA() + "' ");
            if (locales)
            	sb.append("\n 		AND e.b_local = 'S' ");
            sb.append(compararCriterioBusqueda(aux));
            if(dto.isPbOptMismo())
            	sb.append("\n 		AND m.id_banco = m.id_banco_benef ");
            else
            	sb.append("\n 		AND m.id_banco <> m.id_banco_benef ");
            if(!dto.isPbOptMismo()){
		        //Considerar solo los interbancarios que tengan fecha mayor a hoy de origen banamex
            	sb.append("\n UNION ");
            	sb.append("\n SELECT DISTINCT m.fec_valor_original, m.id_tipo_operacion,b2.id_banco_city, b2.inst_finan, m.observacion, c.desc_sucursal AS suc_origen, ");
            	sb.append("\n 	ctas.sucursal as suc_destino,m.id_banco, ");
            	sb.append("\n 	b.b_layout_comerica, ");
            	sb.append("\n 	m.no_docto, m.fec_operacion, o.desc_cve_operacion,  ");
            	sb.append("\n 	m.importe, m.id_divisa, m.id_chequera_benef, m.origen_mov, m.division, ");
            	sb.append("\n 	'' AS nombre_banco_benef, b2.desc_banco AS desc_banco_benef, ");
            	sb.append("\n 	m.beneficiario, m.concepto, m.no_folio_det, m.no_empresa, ");
            	sb.append("\n 	m.id_chequera,m.fec_valor, ");
            	sb.append("\n 	m.id_banco_benef, ");
            	sb.append("\n 	ctas.plaza AS plaza_benef, ");
            	sb.append("\n 	c.desc_plaza AS plaza, ");
            	sb.append("\n 	m.no_cliente, ");
            	sb.append("\n 	b2.desc_banco_inbursa, ");
            	sb.append("\n 	ctas.id_clabe AS clabe_benef,  ");
            	sb.append("\n 	ctas.aba, ctas.swift_code, ");
            	sb.append("\n 	ctas.aba_intermediario, ctas.swift_intermediario, ");
            	sb.append("\n 	ctas.aba_corresponsal, ctas.swift_corresponsal, ");
            	sb.append("\n 	b3.desc_banco AS nom_banco_intermediario, ");
            	sb.append("\n 	b4.desc_banco AS nom_banco_corresponsal, ");
            	sb.append("\n 	b.valida_CLABE,e.id_contrato_mass, ");
            	sb.append("\n 	p.rfc as rfc_benef, ");
            	sb.append("\n 	m.no_factura,e.nom_empresa, ");
            	sb.append("\n 	p.equivale_persona, ");
            	sb.append("\n 	ctas.especiales,  ctas.complemento ");
            	sb.append("\n FROM movimiento m, cat_cve_operacion o, ");
            	sb.append("\n 	cat_banco b, cat_cta_banco c, ctas_banco ctas, cat_banco b2, cat_banco b3, cat_banco b4, ");
            	sb.append("\n 	empresa e,persona p ");
            	sb.append("\n WHERE ");
            	sb.append("\n 		CONVERT(int,m.no_cliente) = p.no_persona ");
            	sb.append("\n 		AND p.id_tipo_persona in ('P','K') ");
            	sb.append("\n 		AND m.no_empresa = e.no_empresa ");
            	sb.append("\n 		AND ctas.id_bank_true *= b3.id_banco ");
            	sb.append("\n 		AND ctas.id_bank_corresponding *= b4.id_banco ");
            	sb.append("\n 		AND m.id_banco_benef *= b2.id_banco ");
            	sb.append("\n 		AND m.id_banco_benef = ctas.id_banco ");
            	sb.append("\n 		AND m.id_chequera_benef = ctas.id_chequera ");
            	sb.append("\n 		AND CONVERT(int, m.no_cliente) = ctas.no_persona ");
            	sb.append("\n 		AND m.id_estatus_mov = 'P' ");
            	sb.append("\n 		AND m.id_tipo_operacion = 3200 ");
            	sb.append("\n 		AND m.id_forma_pago = 3 ");
            	sb.append("\n 		AND m.id_cve_operacion = o.id_cve_operacion ");
            	sb.append("\n 		AND m.id_banco = b.id_banco ");
            	sb.append("\n 		AND m.no_empresa = c.no_empresa AND m.id_banco = c.id_banco");
            	sb.append("\n 		AND m.id_chequera = c.id_chequera ");
            	sb.append("\n 		AND c.pago_mass = 'S' ");
            	sb.append("\n 		AND m.id_tipo_movto = 'E'");
            	sb.append("\n 		AND b.b_banca_elect  in ('A','E') ");
            	sb.append("\n 		AND ((m.id_banco_benef<1000 and m.id_divisa = 'MN') OR m.id_divisa <> 'MN') ");
            	sb.append("\n 		AND m.id_banco = " + dto.getPiBanco());
		        if(dto.getPlNoEmpresa() == 0)
		        	sb.append("\n 		AND m.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " + dto.getPiNoUsuario() + ")");
		        else
		        	sb.append("\n 		AND m.no_empresa = " + dto.getPlNoEmpresa());
		        if(!dto.getPsDivisaA().equals(""))
		        	sb.append("\n 		AND m.id_divisa = '" + dto.getPsDivisaA() + "' ");
		        if(locales)
		        	sb.append("\n 		AND e.b_local = 'S' ");
		        sb.append(compararCriterioBusqueda(aux));
		        sb.append("\n 		AND m.id_banco <> m.id_banco_benef ");
		        if(!dto.isPbMassPorCheq())
		        	sb.append("\n 		AND m.fec_valor_original > (Select fec_hoy from fechas) ");
            }
            sb.append("\n 		ORDER BY m.importe ");
            /*if(gsDBM = "SYBASE")
            	sb.append("\n 		AT ISOLATION READ UNCOMMITTED ");*/
            try{
		        List<MovimientoDto> movimientos = jdbcTemplate.query(sb.toString(), new RowMapper(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto movi = new MovimientoDto();
					movi.setFecValorOriginal(rs.getDate("fec_valor_original"));
					movi.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
					movi.setIdBancoCity(rs.getInt("id_banco_city"));
					movi.setInstFinan(rs.getString("inst_finan"));
					movi.setObservacion(rs.getString("observacion"));
					movi.setSucOrigen(rs.getString("suc_origen"));
					movi.setSucDestino(rs.getString("suc_destino"));
					movi.setIdBanco(rs.getInt("id_banco"));
					movi.setBLayoutComerica(rs.getString("b_layout_comerica"));
					movi.setNoDocto(rs.getString("no_docto"));
					movi.setFecOperacion(rs.getDate("fec_operacion"));
					movi.setDescCveOperacion(rs.getString("desc_cve_operacion"));
					movi.setImporte(rs.getDouble("importe"));
					movi.setIdDivisa(rs.getString("id_divisa"));
					movi.setIdChequeraBenef(rs.getString("id_chequera_benef"));
					movi.setOrigenMov(rs.getString("origen_mov"));
					movi.setDivision(rs.getString("division"));
					movi.setNombreBancoBenef(rs.getString("nombre_banco_benef"));
					movi.setDescBancoBenef(rs.getString("desc_banco_benef"));
					movi.setBeneficiario(rs.getString("beneficiario"));
					movi.setConcepto(rs.getString("concepto"));
					movi.setNoFolioDet(rs.getInt("no_folio_det"));
					movi.setNoEmpresa(rs.getInt("no_empresa"));
					movi.setIdChequera(rs.getString("id_chequera"));
					movi.setFecValor(rs.getDate("fec_valor"));
					movi.setIdBancoBenef(rs.getInt("id_banco_benef"));
					movi.setPlazaBenef(rs.getString("plaza_benef"));
					movi.setPlaza(rs.getInt("plaza"));
					movi.setNoCliente(rs.getString("no_cliente"));
					movi.setDescBancoInbursa(rs.getString("desc_banco_inbursa"));
					movi.setClabeBenef(rs.getString("clabe_benef"));
					movi.setAba(rs.getString("aba"));
					movi.setSwiftCode(rs.getString("swift_code"));
					movi.setAbaIntermediario(rs.getString("aba_intermediario"));
					movi.setSwiftIntermediario(rs.getString("swift_intermediario"));
					movi.setAbaCorresponsal(rs.getString("aba_corresponsal"));
					movi.setSwiftCorresponsal(rs.getString("swift_corresponsal"));
					movi.setNomBancoIntermediario(rs.getString("nom_banco_intermediario"));
					movi.setNomBancoCorresponsal(rs.getString("nom_banco_corresponsal"));
					movi.setValidaCLABE(rs.getString("valida_CLABE"));
					movi.setIdContratoMass(rs.getInt("id_contrato_mass"));
					movi.setIdContratoWlink(rs.getString("id_contrato_wlink"));
					movi.setRfcBenef(rs.getString("rfc_benef"));
					movi.setNoFactura(rs.getString("rfc_benef"));
					movi.setNomEmpresa(rs.getString("nom_empresa"));
					movi.setEspeciales(rs.getString("especiales"));
					movi.setEquivalePersona(rs.getString("equivale_persona"));
					movi.setComplemento(rs.getString("complemento"));
					return movi;
				}});
		        return movimientos;
            }catch(Exception e){
            	log.error(new Date().toString() + " " + Bitacora.getStackTrace(e) + " P:BE, C:MovimientoDao, M:consultarEnvioMssPayment");
            	return null;
            }
	}
	
	
	/**Public Function FunSQLSelectNAFIN(ByVal plNo_empresa As Long, ByVal piTodas As Integer, _
    ByVal plNoUsuario As Long, ByVal ps_divisa As String, _
    ByVal pdMontoIni As Double, ByVal pdMontoFin As Double, _
    ByVal plBancoReceptor As Long, ByVal psfechaValor As String, _
    ByVal psFechaValorOrig As String, ByVal psDivisa As String, _
    ByVal psDivision As String) As ADODB.Recordset*/
	/**
	 * tipos de busquedas
	 * @param dto
	 * @return List<MovimientoDto>
	 */
	@SuppressWarnings("unchecked")
	public List<MovimientoDto> seleccionarNafin(ParametroBusquedaMovimientoDto dto){
		StringBuffer sb = new StringBuffer();
		CriterioBusquedaDto aux = new CriterioBusquedaDto();
		aux.setPdMontoIni(dto.getPdMontoIni());
        aux.setPdMontoFin(dto.getPdMontoFin());
        aux.setPlBancoReceptor(dto.getPlBancoReceptor());
        aux.setPsFechaValor(dto.getPsfechaValor());
        aux.setPsFechaValorOrig(dto.getPsFechaValorOrig());
        aux.setPsDivisa(dto.getPsDivisa());
        aux.setPsDivision(dto.getPsDivision());
		sb.append(" SELECT DISTINCT '' AS inst_finan,m.observacion,'' AS suc_origen, ");
		sb.append("\n 	'' AS suc_destino, m.id_banco, ");
		sb.append("\n 	'' AS b_layout_comerica, ");
		sb.append("\n 	m.no_docto, m.fec_operacion, o.desc_cve_operacion, ");
		sb.append("\n 	m.importe, m.id_divisa, m.id_chequera_benef, m.origen_mov, m.division, ");
		sb.append("\n 	'' AS nombre_banco_benef, '' AS desc_banco_benef, ");
		sb.append("\n 	m.beneficiario, m.concepto, m.no_folio_det, m.no_empresa, ");
		sb.append("\n 	m.id_chequera, m.fec_valor, m.id_banco_benef, ");
		sb.append("\n 	'' AS plaza_benef, ");
		sb.append("\n 	'' AS plaza,m.no_cliente, ");
		sb.append("\n 	'' desc_banco_inbursa, ");
		sb.append("\n 	'' AS clabe_benef, ");
		sb.append("\n 	'' AS aba,'' AS swift_code, ");
		sb.append("\n 	'' AS aba_intermediario,'' AS swift_intermediario, ");
		sb.append("\n 	'' AS aba_corresponsal, '' AS swift_corresponsal, ");
		sb.append("\n 	'' AS nom_banco_intermediario, ");
		sb.append("\n 	'' AS nom_banco_corresponsal,");
		sb.append("\n 	'' AS valida_CLABE,'' AS id_contrato_mass, ");
		sb.append("\n 	COALESCE(referencia_ban, '') + m.no_factura AS no_factura, p.equivale_persona, ");
		sb.append("\n 	'' AS id_banco_city, ");
		sb.append("\n 	/*m.no_factura,*/'' AS nom_empresa, CASE WHEN COALESCE(p.rfc, '') = '' THEN COALESCE(m.rfc, '') ");
		sb.append("\n	ELSE p.rfc  END AS rfc_benef, '' AS RFC, '' AS especiales, '' AS complemento, '' AS tipo_envio_layout, ");
		sb.append("\n 		'' AS divisa_chequera, '' AS pais_benef, '' AS direccion_benef, '' AS clave ");
		sb.append("\n FROM movimiento m, cat_cve_operacion o, persona p ");
		sb.append("\n WHERE m.no_cliente = p.no_persona ");
		sb.append("\n 	AND p.id_tipo_persona = 'P' ");
		sb.append("\n 	AND m.id_cve_operacion = o.id_cve_operacion ");
		sb.append("\n 	AND m.id_forma_pago = 7 ");
		sb.append("\n 	AND m.id_tipo_movto = 'E' ");
		sb.append("\n 	AND id_tipo_operacion = 3000 ");
		sb.append("\n 	AND id_estatus_mov in('C','N') ");
		if(dto.getPiTodas() == 0)
			sb.append("\n 	AND m.no_empresa = " + dto.getPlNoEmpresa());
		else
			sb.append("\n 	AND m.no_empresa IN (SELECT no_empresa FROM usuario_empresa WHERE no_usuario = " + dto.getPlNoUsuario() + ")");
	    if(!dto.getPsDivisa().equals(""))
	    	sb.append("\n 	AND m.id_divisa = '" + dto.getPsDivisa() + "' ");
	    sb.append(compararCriterioBusqueda(aux));
	    try{
		    List<MovimientoDto> movimientos = jdbcTemplate.query(sb.toString(), new RowMapper(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto movimiento = new MovimientoDto();
					movimiento.setInstFinan(rs.getString("inst_finan"));
					movimiento.setObservacion(rs.getString("observacion"));
					movimiento.setSucOrigen(rs.getString("suc_origen"));
					movimiento.setSucDestino(rs.getString("suc_destino"));
					movimiento.setIdBanco(rs.getInt("id_banco"));
					movimiento.setBLayoutComerica(rs.getString("b_layout_comerica"));
					movimiento.setNoDocto(rs.getString("no_docto"));
					movimiento.setFecOperacion(rs.getDate("no_docto"));
					movimiento.setDescCveOperacion(rs.getString("desc_cve_operacion"));
					movimiento.setImporte(rs.getDouble("importe"));
					movimiento.setIdDivisa(rs.getString("id_divisa"));
					movimiento.setIdChequeraBenef(rs.getString("id_chequera_benef"));
					movimiento.setOrigenMov(rs.getString("origen_mov"));
					movimiento.setDivision(rs.getString("division"));
					movimiento.setNombreBancoBenef(rs.getString("nombre_banco_benef"));
					movimiento.setDescBancoBenef(rs.getString("desc_banco_benef"));
					movimiento.setBeneficiario(rs.getString("beneficiario"));
					movimiento.setConcepto(rs.getString("concepto"));
					movimiento.setNoFolioDet(rs.getInt("no_folio_det"));
					movimiento.setNoEmpresa(rs.getInt("no_empresa"));
					movimiento.setIdChequera(rs.getString("id_chequera"));
					movimiento.setFecValor(rs.getDate("fec_valor"));
					movimiento.setIdBancoBenef(rs.getInt("id_banco_benef"));
					movimiento.setPlazaBenef(rs.getString("plaza_benef"));
					movimiento.setPlaza(rs.getInt("plaza"));
					movimiento.setNoCliente(rs.getString("no_cliente"));
					movimiento.setDescBancoInbursa(rs.getString("desc_banco_inbursa"));
					movimiento.setClabeBenef(rs.getString("clabe_benef"));
					movimiento.setAba(rs.getString("aba"));
					movimiento.setSwiftCode(rs.getString("swift_code"));
					movimiento.setAbaIntermediario(rs.getString("aba_intermediario"));
					movimiento.setSwiftIntermediario(rs.getString("swift_intermediario"));
					movimiento.setAbaCorresponsal(rs.getString("aba_corresponsal"));
					movimiento.setSwiftCorresponsal(rs.getString("swift_corresponsal"));
					movimiento.setNomBancoIntermediario(rs.getString("nom_banco_intermediario"));
					movimiento.setNomBancoCorresponsal(rs.getString("nom_banco_corresponsal"));
					movimiento.setValidaCLABE(rs.getString("valida_CLABE"));
					movimiento.setIdContratoMass(rs.getInt("id_contrato_mass"));
					movimiento.setNoFactura(rs.getString("no_factura"));
					movimiento.setEquivalePersona(rs.getString("equivale_persona"));
					movimiento.setIdBancoCity(rs.getInt("id_banco_city"));
					movimiento.setNomEmpresa(rs.getString("nom_empresa"));
					movimiento.setRfcBenef(rs.getString("rfc_benef"));
					movimiento.setRfc(rs.getString("RFC"));
					movimiento.setEspeciales(rs.getString("especiales"));
					movimiento.setComplemento(rs.getString("complemento"));
					movimiento.setTipoEnvioLayout(rs.getInt("tipo_envio_layout"));
					movimiento.setDivisaChequera(rs.getString("divisa_chequera"));
					movimiento.setPaisBenef(rs.getString("pais_benef"));
					movimiento.setDireccionBenef(rs.getString("direccion_benef"));
					movimiento.setClave(rs.getString("clave"));
					return movimiento;
				}});
			return movimientos;
	    }catch(Exception e){
	    	log.error(new Date().toString() + " " + Bitacora.getStackTrace(e) + " P:BE, C:MovimientoDao, M:seleccionarNafin");
	    	return null;
	    }
	}
	
	/**
	 * Parametros para la busqueda 
	 * @param dto
	 * @return String
	 */
	public String compararCriterioBusqueda(CriterioBusquedaDto dto){
		StringBuffer sb = new StringBuffer();
		gral=new ConsultasGenerales(jdbcTemplate);
		if (!dto.getPsDivision().equals("")){
			sb.append("\n AND m.division = '" + dto.getPsDivision() + "'");
		}
		if (dto.getPdMontoIni() > 0 && dto.getPdMontoFin() > 0){
			sb.append("\n AND m.importe between " + dto.getPdMontoIni() + " AND " + dto.getPdMontoFin());
		}
		if (dto.getPlBancoReceptor() != 0){
			sb.append("\n AND m.id_banco_benef = " + dto.getPlBancoReceptor());
		}
		if(!dto.getPsFechaValor().equals("")){
			sb.append("\n AND m.fec_valor = convert(datetime,'" + gral.cambiarFecha(dto.getPsFechaValor()) + "',103)");
		}
		if(!dto.getPsFechaValorOrig().equals("")){
			sb.append("\n AND m.fec_valor_origen = convert(datetime,'" + gral.cambiarFecha(dto.getPsFechaValorOrig()) + "',103)");
		}
		return sb.toString();
	}
	/**
	 * Public Function FunSQLUpdate_movimiento_tipoEnvio(ByVal psTipoEnvio As String, ByVal psNomArchivos As String, _
     *       Optional ByVal pbMasspayment As Boolean) As Long
     * form transferencia BancaElectronica   
	 * @param dto
	 * @return int
	 */
	public int actualizarMovimientoTipoEnvio(ParametroBusquedaMovimientoDto dto){
		String psArchivos;
		StringBuffer sb = new StringBuffer();
		int res;
		psArchivos = dto.getPsNomArchivos().replace(".txt", "");
		psArchivos = psArchivos.replace(".sha", "");
		psArchivos = psArchivos.replace(".slk", "");
		if(psArchivos.substring(psArchivos.length()-1,psArchivos.length()).equals(","))
			psArchivos=psArchivos.substring(0,psArchivos.length()-1);
		psArchivos = "'" + psArchivos.replace(",", "', '") + "'";
		sb.append(" UPDATE movimiento SET ");
		sb.append("\n 	id_estatus_mov = 'T', ");
		sb.append("\n 	id_servicio_be = ?, ");
		sb.append("\n 	fec_trans = (SELECT MAX(fec_valor) FROM det_arch_transfer d WHERE d.no_folio_det = movimiento.no_folio_det ");
		sb.append("\n								AND id_estatus_arch <> 'X' ) ");
		sb.append("\n 	WHERE no_folio_det IN( SELECT no_folio_det FROM det_arch_transfer ");
		sb.append("\n  				WHERE nom_arch IN( ? )) ");
		try{
			res = jdbcTemplate.update(sb.toString(), new Object[]{dto.getPsTipoEnvio(), psArchivos });
			return res;
		}catch(Exception e){
			log.error(new Date().toString() + " " + Bitacora.getStackTrace(e) + " P:BE, C:MovimientoDao, M:actualizarMovimientoTipoEnvio");
			return -1;
		}
	}
	/**
	 * 
	 * @param dto
	 * @return int
	 */
	public int insertaMovimiento(MovimientoDto dto){
		StringBuffer sb = new StringBuffer();
		String signos="";
		int res;
		ArrayList<Object> ob = new ArrayList<Object>();
		ob.add(dto.getIdEstatusMov());
		ob.add(dto.getIdChequera());
		ob.add(dto.getBSalvoBuenCobro());
		ob.add(dto.getNoDocto());
		ob.add(dto.getFecOperacion());
		ob.add(dto.getFecValor());
		ob.add(dto.getFecValorOriginal());
		ob.add(dto.getFecRecalculo());
		ob.add(dto.getIdTipoMovto());
		ob.add(dto.getIdDivisa());
		ob.add(dto.getIdDivisaOriginal());
		ob.add(dto.getFecExportacion());
		ob.add(dto.getFecReimprime());
		ob.add(dto.getFecImprime());
		ob.add(dto.getFecCheque());
		ob.add(dto.getReferencia());
		ob.add(dto.getFolioBanco());
		ob.add(dto.getBeneficiario());
		ob.add(dto.getIdLeyenda());
		ob.add(dto.getIdChequeraBenef());
		ob.add(dto.getConcepto());
		ob.add(dto.getIdContable());
		ob.add(dto.getArchProtegido());
		ob.add(dto.getFecAlta());
		ob.add(dto.getFecModif());
		ob.add(dto.getIdEstatusCb());
		ob.add(dto.getIdEstatusCc());
		ob.add(dto.getNoCliente());
		ob.add(dto.getObservacion());
		ob.add(dto.getOrigenMov());
		ob.add(dto.getNoDoctoCus());
		ob.add(dto.getSolicita());
		ob.add(dto.getAutoriza());
		ob.add(dto.getBEntregado());
		ob.add(dto.getFecEntregado());
		ob.add(dto.getFecConfTrans());
		ob.add(dto.getFecRechTrans());
		ob.add(dto.getTipoCancelacion());
		ob.add(dto.getFecExportaFlujo());
		ob.add(dto.getFecTrans());
		ob.add(dto.getBGenContable());
		ob.add(dto.getIdCodigo());
		ob.add(dto.getIdSubcodigo());
		ob.add(dto.getFisicaMoral());
		ob.add(dto.getNacExt());
		ob.add(dto.getIdEstatusCheq());
		ob.add(dto.getInvoiceType());
		ob.add(dto.getNoFactura());
		ob.add(dto.getPoHeaders());
		ob.add(dto.getAlternateVendorIdEft());
		ob.add(dto.getAlternateVendorSiteIdEft());
		ob.add(dto.getInvoiceTypeLookupCode());
		ob.add(dto.getBAutorizaImpre());
		ob.add(dto.getOperMayEsp());
		ob.add(dto.getCodBloqueo());
		ob.add(dto.getRfc());
		ob.add(dto.getBGenConta());
		ob.add(dto.getDivision());
		ob.add(dto.getIndIva());
		ob.add(dto.getContrarecibo());
		ob.add(dto.getDeudor());
		ob.add(dto.getDescripcion());
		ob.add(dto.getRango());
		ob.add(dto.getReferenciaBan());
		ob.add(dto.getHoraRecibo());
		ob.add(dto.getFecConciliaCaja());
		ob.add(dto.getFecPropuesta());
		ob.add(dto.getBArchSeg());
		ob.add(dto.getCveControl());
		ob.add(dto.getIdChequeraAnt());
		ob.add(dto.getOrigenMovAnt());
		ob.add(dto.getNoClienteAnt());
		ob.add(dto.getIdServicioBe());
		ob.add(dto.getEMail());
		ob.add(dto.getClabe());
		ob.add(dto.getMontoSobregiro());
		ob.add(dto.getTablaOrigen());
		ob.add(dto.getFechaContabilizacion());
		ob.add(dto.getBCertificado());
		ob.add(dto.getFecCertificacion());
		ob.add(dto.getAbba());
		ob.add(dto.getSwift());
		ob.add(dto.getNombreProvDivisas());
		ob.add(dto.getBancoDivisas());
		ob.add(dto.getDireccionDivisas());
		ob.add(dto.getCuentaDivisas());
		ob.add(dto.getSortCode());
		ob.add(dto.getAbi());
		ob.add(dto.getCab());
		ob.add(dto.getComentario1());
		ob.add(dto.getComentario2());
		sb.append(" INSERT INTO movimiento ( ID_ESTATUS_MOV, ID_CHEQUERA, B_SALVO_BUEN_COBRO, NO_DOCTO, FEC_OPERACION, ");
		sb.append("\n 		FEC_VALOR, FEC_VALOR_ORIGINAL, FEC_RECALCULO, ID_TIPO_MOVTO, ID_DIVISA, ");
		sb.append("\n 		ID_DIVISA_ORIGINAL,	FEC_EXPORTACION, FEC_REIMPRIME, FEC_IMPRIME, FEC_CHEQUE2 ");
		sb.append("\n 		REFERENCIA, FOLIO_BANCO, BENEFICIARIO, ID_LEYENDA, ID_CHEQUERA_BENEF, ");
		sb.append("\n 		CONCEPTO, ID_CONTABLE, ARCH_PROTEGIDO, FEC_ALTA, FEC_MODIF, ");
		sb.append("\n 		ID_ESTATUS_CB, ID_ESTATUS_CC, NO_CLIENTE, OBSERVACION, ORIGEN_MOV, ");
		sb.append("\n 		NO_DOCTO_CUS, SOLICITA, AUTORIZA, B_ENTREGADO, FEC_ENTREGADO, " );
		sb.append("\n 		FEC_CONF_TRANS, FEC_RECH_TRANS, TIPO_CANCELACION, FEC_EXPORTA_FLUJO,");
		sb.append("\n 		FEC_TRANS, B_GEN_CONTABLE, ID_CODIGO, ID_SUBCODIGO, FISICA_MORAL, "); 
		sb.append("\n 		NAC_EXT, ID_ESTATUS_CHEQ, INVOICE_TYPE,	NO_FACTURA,	PO_HEADERS, ");
		sb.append("\n 		ALTERNATE_VENDOR_ID_EFT, ALTERNATE_VENDOR_SITE_ID_EFT, INVOICE_TYPE_LOOKUP_CODE, ");
		sb.append("\n 		B_AUTORIZA_IMPRE, OPER_MAY_ESP, COD_BLOQUEO, RFC, B_GEN_CONTA, ");
		sb.append("\n 		DIVISION, IND_IVA, CONTRARECIBO, DEUDOR, DESCRIPCION, ");
		sb.append("\n 		RANGO, REFERENCIA_BAN, HORA_RECIBO, FEC_CONCILIA_CAJA, FEC_PROPUESTA, ");
		sb.append("\n 		B_ARCH_SEG, CVE_CONTROL, ID_CHEQUERA_ANT, ORIGEN_MOV_ANT, NO_CLIENTE_ANT, ");
		sb.append("\n 		ID_SERVICIO_BE, E_MAIL, CLABE, MONTO_SOBREGIRO, TABLA_ORIGEN, ");
		sb.append("\n 		FECHA_CONTABILIZACION, B_CERTIFICADO, FEC_CERTIFICACION, ABBA, SWIFT, ");
		sb.append("\n 		NOMBRE_PROV_DIVISAS, BANCO_DIVISAS, DIRECCION_DIVISAS, CUENTA_DIVISAS, SORT_CODE, ");
		sb.append("\n 		ABI, CAB, COMENTARIO1, COMENTARIO2 ");
		if(dto.getNoEmpresa()>0){
			sb.append(", NO_EMPRESA");
			signos+=",? ";
			ob.add(dto.getNoEmpresa());
		}
		if(dto.getNoFolioDet()>0){
			sb.append(", \n	 	NO_FOLIO_DET");
			signos+=",? ";
			ob.add(dto.getNoFolioDet());
		}
		if(dto.getIdTipoOperacion()>0){
			sb.append(", \n 	ID_TIPO_OPERACION");
			signos+=",? ";
			ob.add(dto.getIdTipoOperacion());
		}
		if(dto.getIdCveOperacion()>0){
			sb.append(", \n 	ID_CVE_OPERACION");
			signos+=",? ";
			ob.add(dto.getIdCveOperacion());
		}
		if(dto.getIdTipoSaldo()>0){
			sb.append(", \n 	ID_TIPO_SALDO");
			signos+=",? ";
			ob.add(dto.getIdTipoSaldo());
		}
		if(dto.getNoLinea()>0){
			sb.append(", \n 	NO_LINEA");
			signos+=",? ";
			ob.add(dto.getNoLinea());
		}
		if(dto.getIdInvCbolsa()>0){
			sb.append(", \n 	ID_INV_CBOLSA");
			signos+=",? ";
			ob.add(dto.getIdInvCbolsa());
		}
		if(dto.getNoCuenta()>0){
			sb.append(", \n 	NO_CUENTA");
			signos+=",? ";
			ob.add(dto.getNoCuenta());
		}
		if(dto.getNoCheque()>0){
			sb.append(", \n 	NO_CHEQUE");
			signos+=",? ";
			ob.add(dto.getNoCheque());
		}
		if(dto.getIdBanco()>0){
			sb.append(", \n 	ID_BANCO");
			signos+=",? ";
			ob.add(dto.getIdBanco());
		}
		if(dto.getIdFormaPago()>0){
			sb.append(", \n 	ID_FORMA_PAGO");
			signos+=",? ";
			ob.add(dto.getIdFormaPago());
		}
		if(dto.getImporte()>0){
			sb.append(", \n 	IMPORTE");
			signos+=",? ";
			ob.add(dto.getImporte());
		}
		if(dto.getImporteOriginal()>0){
			sb.append(", \n 	IMPORTE_ORIGINAL");
			signos+=",? ";
			ob.add(dto.getImporteOriginal());
		}
		if(dto.getIdTipoDocto()>0){
			sb.append(", \n 	ID_TIPO_DOCTO");
			signos+=",? ";
			ob.add(dto.getIdTipoDocto());
		}
		if(dto.getValorTasa()>0){
			sb.append(", \n 	VALOR_TASA");
			signos+=",? ";
			ob.add(dto.getValorTasa());
		}
		if(dto.getFolioRef()>0){
			sb.append(", \n 	FOLIO_REF");
			signos+=",? ";
			ob.add(dto.getFolioRef());
		}
		if(dto.getDiasInv()>0){
			sb.append(", \n 	DIAS_INV");
			signos+=",? ";
			ob.add(dto.getDiasInv());
		}
		if(dto.getIdCaja()>0){
			sb.append(", \n 	ID_CAJA");
			signos+=",? ";
			ob.add(dto.getIdCaja());
		}
		if(dto.getLoteEntrada()>0){
			sb.append(", \n 	LOTE_ENTRADA");
			signos+=",? ";
			ob.add(dto.getLoteEntrada());
		}
		if(dto.getLoteSalida()>0){
			sb.append(", \n 	LOTE_SALIDA");
			signos+=",? ";
			ob.add(dto.getLoteSalida());
		}
		if(dto.getIdBancoBenef()>0){
			sb.append(", \n 	ID_BANCO_BENEF");
			signos+=",? ";
			ob.add(dto.getIdBancoBenef());
		}
		if(dto.getFolioSeg()>0){
			sb.append(", \n 	FOLIO_SEG");
			signos+=",? ";
			ob.add(dto.getFolioSeg());
		}
		if(dto.getUsuarioImprime()>0){
			sb.append(", \n 	USUARIO_IMPRIME");
			signos+=",? ";
			ob.add(dto.getUsuarioImprime());
		}
		if(dto.getUsuarioAlta()>0){
			sb.append(", \n 	USUARIO_ALTA");
			signos+=",? ";
			ob.add(dto.getUsuarioAlta());
		}
		if(dto.getUsuarioModif()>0){
			sb.append(", \n 	USUARIO_MODIF");
			signos+=",? ";
			ob.add(dto.getUsuarioModif());
		}
		if(dto.getNoCuentaRef()>0){
			sb.append(", \n 	NO_CUENTA_REF");
			signos+=",? ";
			ob.add(dto.getNoCuentaRef());
		}
		if(dto.getNoCobrador()>0){
			sb.append(", \n 	NO_COBRADOR");
			signos+=",? ";
			ob.add(dto.getNoCobrador());
		}
		if(dto.getSucursal()>0){
			sb.append(", \n 	SUCURSAL");
			signos+=",? ";
			ob.add(dto.getSucursal());
		}
		if(dto.getPlaza()>0){
			sb.append(", \n 	PLAZA");
			signos+=",? ";
			ob.add(dto.getPlaza());
		}
		if(dto.getNoRecibo()>0){
			sb.append(", \n 	NO_RECIBO");
			signos+=",? ";
			ob.add(dto.getNoRecibo());
		}
		if(dto.getImporteDesglosado()>0){
			sb.append(", \n 	IMPORTE_DESGLOSADO");
			signos+=",? ";
			ob.add(dto.getImporteDesglosado());
		}
		if(dto.getTipoCambio()>0){
			sb.append(", \n 	TIPO_CAMBIO");
			signos+=",? ";
			ob.add(dto.getTipoCambio());
		}
		if(dto.getInvoiceId()>0){
			sb.append(", \n 	INVOICE_ID");
			signos+=",? ";
			ob.add(dto.getInvoiceId());
		}
		if(dto.getVendorId()>0){
			sb.append(", \n 	VENDOR_ID");
			signos+=",? ";
			ob.add(dto.getVendorId());
		}
		if(dto.getVendorSiteId()>0){
			sb.append(", \n 	VENDOR_SITE_ID");
			signos+=",? ";
			ob.add(dto.getVendorSiteId());
		}
		if(dto.getGrupoPago()>0){
			sb.append(", \n 	GRUPO_PAGO");
			signos+=",? ";
			ob.add(dto.getGrupoPago());
		}
		if(dto.getIdRubro()>0){
			sb.append(", \n 	ID_RUBRO");
			signos+=",? ";
			ob.add(dto.getIdRubro());
		}
		if(dto.getAgrupa1()>0){
			sb.append(", \n 	AGRUPA1");
			signos+=",? ";
			ob.add(dto.getAgrupa1());
		}
		if(dto.getAgrupa2()>0){
			sb.append(", \n 	AGRUPA2");
			signos+=",? ";
			ob.add(dto.getAgrupa2());
		}
		if(dto.getAgrupa3()>0){
			sb.append(", \n 	AGRUPA3");
			signos+=",? ";
			ob.add(dto.getAgrupa3());
		}
		if(dto.getFirmante1()>0){
			sb.append(", \n 	FIRMANTE1");
			signos+=",? ";
			ob.add(dto.getFirmante1());
		}
		if(dto.getFirmante2()>0){
			sb.append(", \n 	FIRMANTE2");
			signos+=",? ";
			ob.add(dto.getFirmante2());
		}
		if(dto.getNoPoliza()>0){
			sb.append(", \n 	NO_POLIZA");
			signos+=",? ";
			ob.add(dto.getNoPoliza());
		}
		if(dto.getCPoliza()>0){
			sb.append(", \n 	C_POLIZA");
			signos+=",? ";
			ob.add(dto.getCPoliza());
		}
		if(dto.getCLote()>0){
			sb.append(", \n 	C_LOTE");
			signos+=",? ";
			ob.add(dto.getCLote());
		}
		if(dto.getCPeriodo()>0){
			sb.append(", \n 	C_PERIODO");
			signos+=",? ";
			ob.add(dto.getCPeriodo());
		}
		if(dto.getCMes()>0){
			sb.append(", \n 	C_MES");
			signos+=",? ";
			ob.add(dto.getCMes());
		}
		if(dto.getNoPedido()>0){
			sb.append(", \n 	NO_PEDIDO");
			signos+=",? ";
			ob.add(dto.getNoPedido());
		}
		if(dto.getImporteParcial()>0){
			sb.append(", \n 	IMPORTE_PARCIAL");
			signos+=",? ";
			ob.add(dto.getImporteParcial());
		}
		if(dto.getIdBancoAnt()>0){
			sb.append(", \n 	ID_BANCO_ANT");
			signos+=",? ";
			ob.add(dto.getIdBancoAnt());
		}
		if(dto.getIdArea()>0){
			sb.append(", \n 	ID_AREA");
			signos+=",? ";
			ob.add(dto.getIdArea());
		}
		if(dto.getIdGrupo()>0){
			sb.append(", \n 	ID_GRUPO");
			signos+=",? ";
			ob.add(dto.getIdGrupo());
		}
		sb.append(" ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "); 
		sb.append("\n 			?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
		sb.append("\n 			?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
		sb.append("\n 			?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " );
		sb.append("\n 			?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
		sb.append("\n 			?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
		sb.append("\n 			?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
		sb.append("\n 			?, ?, ?, ?, ?, ?, ? ");
		sb.append(signos);
		sb.append(" )");
		Object[] arr = ob.toArray();
		try{
			res = jdbcTemplate.update(sb.toString(), arr);
			return res;
		}catch(Exception e){
			log.error(new Date().toString() + " " + Bitacora.getStackTrace(e) + " P:BE, C:MovimientoDao, M:insertaMovimiento");
			return -1;
		}
	}
	
	/**
	 * Function ObtenCasaCambio(iBanco As Integer) As String 'ACM 16/02/06
	 * form transferencia
	 * modulo Banca Electronica 
	 * consulta cat_banco
	 * @param idBanco int
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public String obtenerCasaCambio(int idBanco){
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT transfer_casa_cambio ");
		sb.append(" FROM cat_banco ");
		sb.append(" WHERE id_banco = " + idBanco);
		try{
			List<String> datos = jdbcTemplate.query(sb.toString(), new RowMapper(){
			public String mapRow(ResultSet rs, int idx) throws SQLException {
				return rs.getString("transfer_casa_cambio");
			}});
			if(datos.size()==0)
				return "";
			else{
				if(datos.get(0)!=null && !datos.get(0).equals("") && !datos.get(0).equals("NULL"))
					return datos.get(0);
				else 
					return "";
			}
		}catch(Exception e){
			log.error(new Date().toString() + " " + Bitacora.getStackTrace(e) + " P:BE, C:MovimientoDao, M:obtenerCasaCambio");
			return null;
		}
	}
	//setters
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
}
