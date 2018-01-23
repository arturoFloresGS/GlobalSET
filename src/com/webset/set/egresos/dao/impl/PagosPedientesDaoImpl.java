package com.webset.set.egresos.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.egresos.dao.PagosPedientesDao;
import com.webset.set.egresos.dto.PagosPendientesDto;
import com.webset.set.egresos.dto.PlantillaDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.GrupoEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.StoredProcedure;
import com.webset.utils.tools.Utilerias;

/*
 * Clase creada por Luis Alfredo Serrato Montes de Oca
 * 14092015
 */

public class PagosPedientesDaoImpl implements PagosPedientesDao {

	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	ConsultasGenerales consultasGenerales;
	GlobalSingleton gb = GlobalSingleton.getInstancia();
	SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

	@Override
	public List<PagosPendientesDto> obtenerPagosPendientes(PlantillaDto plantilla,List<Map<String, String>> rangosDocumentos ,List<Map<String, String>> rangosProveedores, boolean proveedores, boolean documentos) {
		StringBuffer sb = new StringBuffer();
		List<PagosPendientesDto> pagosPendietnes = new ArrayList<PagosPendientesDto>();
		String renglonesRegla = "";
		StringBuffer condicion = new StringBuffer();
		
		//System.out.println("datos biss "+plantilla.getConcepto()+plantilla.getMontoUno()+plantilla.getMontoDos()+plantilla.getFactura());
		
		if(plantilla.getIdReglaNegocio() > 0){
			renglonesRegla = obtenerRenglonesConRegla(plantilla.getIdReglaNegocio());
			System.out.println(renglonesRegla);
		} 
		
		if(plantilla.getTipoBusqueda().equals("P"))
		{
			condicion.append(" WHERE ID_TIPO_OPERACION = 3000\n");  
			condicion.append(" AND ID_ESTATUS_MOV IN ('N','C') \n"); 
			//condicion.append(" AND (CVE_CONTROL IS NULL OR CVE_CONTROL = '')\n");
		}
		else{
			condicion.append("\n WHERE ID_TIPO_OPERACION = 3801 ");
			condicion.append("\n AND ID_ESTATUS_MOV = 'L' ");
			condicion.append("\n AND (ORIGEN_MOV = '' OR ORIGEN_MOV IS NULL) ");
			condicion.append("\n AND ID_TIPO_MOVTO = 'E' ");

			//condicion.append("\n AND (CVE_CONTROL IS NULL OR CVE_CONTROL = '') ");
		}

		condicion.append("\n AND (CVE_CONTROL IS NULL OR CVE_CONTROL = '')");
	
		if(!plantilla.getDivisa().equals("")){
			condicion.append("\n AND ID_DIVISA = '" + Utilerias.validarCadenaSQL(plantilla.getDivisa()) + "' \n");
		}
		/*if(!plantilla.getNumeroDocumento().equals("")){
			condicion.append(" AND NO_DOCTO = '"+ Utilerias.validarCadenaSQL(plantilla.getNumeroDocumento()) +"' \n");
		}*/
		if((!plantilla.getNumDocA().equals("") || !plantilla.getNumDocB().equals("")) && rangosDocumentos.size()<=0){
			if(!plantilla.getNumDocB().equals(""))
				condicion.append(" AND CONVERT(INT, NO_DOCTO) = "+ Utilerias.validarCadenaSQL(plantilla.getNumDocB()) +" \n");	
			else
				condicion.append(" AND CONVERT(INT, NO_DOCTO) = "+ Utilerias.validarCadenaSQL(plantilla.getNumDocA()) +" \n");	
		}else if(rangosDocumentos.size()>0){
			condicion.append(" AND (");
			if(rangosDocumentos.size()==1){
				condicion.append("CONVERT(INT, NO_DOCTO) BETWEEN "+ rangosDocumentos.get(0).get("de") +"  AND "+ rangosDocumentos.get(0).get("a")  +" \n");
			}else{
				for (int i = 0; i < rangosDocumentos.size(); i++) {
					condicion.append(" CONVERT(INT, NO_DOCTO) BETWEEN "+ rangosDocumentos.get(i).get("de") +"  AND "+ rangosDocumentos.get(i).get("a")  +" \n");
					if((i+1)< rangosDocumentos.size()){
						condicion.append("OR");
					}
				}
			}
			if (documentos) {
				condicion.append("OR CONVERT(INT, NO_DOCTO) IN (");
				condicion.append("SELECT CONVERT(INT, REFERENCIA) FROM RENTASTMP WHERE OBSERVACION = '");
				condicion.append(GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
				condicion.append("' AND FORMA_PAGO = 'D'");
				condicion.append("))");
			} else {
				condicion.append(")");
			}
		} else if (documentos) {
			condicion.append(" AND (");
			condicion.append("CONVERT(INT, NO_DOCTO) IN (");
			condicion.append("SELECT CONVERT(INT, REFERENCIA) FROM RENTASTMP WHERE OBSERVACION = '");
			condicion.append(GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
			condicion.append("' AND FORMA_PAGO = 'D'");
			condicion.append("))");
		}
		
		//Rango de personas
		if((!plantilla.getEquivalePersonaA().equals("") || !plantilla.getEquivalePersonaB().equals("")) && rangosProveedores.size()<=0){
			if(!plantilla.getNumDocB().equals(""))
				condicion.append(" AND EQUIVALE_PERSONA = '"+ Utilerias.validarCadenaSQL(plantilla.getEquivalePersonaB()) +"' \n");	
			else
				condicion.append(" AND EQUIVALE_PERSONA = '"+ Utilerias.validarCadenaSQL(plantilla.getEquivalePersonaA()) +"' \n");	
		}else if(rangosProveedores.size()>0){
			condicion.append(" AND (");
			if(rangosProveedores.size()==1){
				condicion.append("CONVERT(INT, REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE"
						+ "(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE"
						+ "(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE"
						+ "(REPLACE('ASDGFHFDSAFGHJFDSFGHH','A',0),'B',0),'C',0),'D',0),'E',0),'F',0),'G',0),'H',0),'I',0),"
						+ "'J',0),'K',0),'L',0),'M',0),'N',0),'O',0),'P',0),'Q',0),'R',0),'S',0),'T',0),'U',0),"
						+ "	'V',0),'W',0),'X',0),'Y',0),'Z',0),'0',0)) BETWEEN "
				+ rangosProveedores.get(0).get("de") +"  AND "+ rangosProveedores.get(0).get("a")  +" \n");
			}else{
				for (int i = 0; i < rangosProveedores.size(); i++) {
					condicion.append(" CONVERT(INT, REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE"
						+ "(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE"
						+ "(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE"
						+ "(REPLACE('ASDGFHFDSAFGHJFDSFGHH','A',0),'B',0),'C',0),'D',0),'E',0),'F',0),'G',0),'H',0),'I',0),"
						+ "'J',0),'K',0),'L',0),'M',0),'N',0),'O',0),'P',0),'Q',0),'R',0),'S',0),'T',0),'U',0),"
						+ "	'V',0),'W',0),'X',0),'Y',0),'Z',0),'0',0)) BETWEEN "
				+  rangosProveedores.get(i).get("de") +"  AND "+ rangosProveedores.get(i).get("a")  +" \n");
					if((i+1)< rangosProveedores.size()){
						condicion.append("OR");
					}
				}
			}
			if (proveedores) {
				condicion.append("OR CONVERT(INT, REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE"
						+ "(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE"
						+ "(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE"
						+ "(REPLACE('ASDGFHFDSAFGHJFDSFGHH','A',0),'B',0),'C',0),'D',0),'E',0),'F',0),'G',0),'H',0),'I',0),"
						+ "'J',0),'K',0),'L',0),'M',0),'N',0),'O',0),'P',0),'Q',0),'R',0),'S',0),'T',0),'U',0),"
						+ "	'V',0),'W',0),'X',0),'Y',0),'Z',0),'0',0)) BETWEEN  ) IN (");
				condicion.append("SELECT REFERENCIA FROM RENTASTMP WHERE OBSERVACION = '");
				condicion.append(GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
				condicion.append("' AND FORMA_PAGO = 'P'");
				condicion.append("))");
			} else {
				condicion.append(")");
			}
		} else if (proveedores) {
			condicion.append(" AND (");
			condicion.append("CONVERT(INT, REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE"
						+ "(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE"
						+ "(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE"
						+ "(REPLACE('ASDGFHFDSAFGHJFDSFGHH','A',0),'B',0),'C',0),'D',0),'E',0),'F',0),'G',0),'H',0),'I',0),"
						+ "'J',0),'K',0),'L',0),'M',0),'N',0),'O',0),'P',0),'Q',0),'R',0),'S',0),'T',0),'U',0),"
						+ "	'V',0),'W',0),'X',0),'Y',0),'Z',0),'0',0)) BETWEEN  ) IN (");
			condicion.append("SELECT REFERENCIA FROM RENTASTMP WHERE OBSERVACION = '");
			condicion.append(GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
			condicion.append("' AND FORMA_PAGO = 'P'");
			condicion.append("))");
		}
		
		if(!plantilla.getTipoDocumento().equals("")){
			condicion.append(" AND ID_CONTABLE ='" +Utilerias.validarCadenaSQL(plantilla.getTipoDocumento())+ "' \n");
		}
		if(!plantilla.getIndicadores().equals("")){
			condicion.append(" AND B_GEN_CONTA ='" +Utilerias.validarCadenaSQL(plantilla.getIndicadores())+ "' \n");
		}
		if(!plantilla.getFechaContableInicio().equals("") && !plantilla.getFechaContableInicio().equals("/undefined/")  
				&& !plantilla.getFechaContableFin().equals("") && !plantilla.getFechaContableFin().equals("/undefined/")){
			condicion.append("AND coalesce(FECHA_CONTABILIZACION,(select fec_hoy from fechas)) BETWEEN convert(datetime, '"+ Utilerias.validarCadenaSQL(plantilla.getFechaContableInicio()) +"', 103) ");
			condicion.append("AND convert(datetime, '"+ Utilerias.validarCadenaSQL(plantilla.getFechaContableFin()) +"', 103) \n");
		}
		
		if(!plantilla.getFechaPropuestaPagoInicio().equals("") && !plantilla.getFechaPropuestaPagoInicio().equals("/undefined/") 
				&& !plantilla.getFechaPropuestaPagoFin().equals("") && !plantilla.getFechaPropuestaPagoFin().equals("/undefined/")){
			condicion.append("AND FEC_VALOR BETWEEN convert(datetime, '"+ Utilerias.validarCadenaSQL(plantilla.getFechaPropuestaPagoInicio()) +"', 103) ");
			condicion.append("AND convert(datetime, '"+ Utilerias.validarCadenaSQL(plantilla.getFechaPropuestaPagoFin()) +"', 103) \n");
		}
		
		if(!renglonesRegla.equals("") && !renglonesRegla.equals("Error")){
			condicion.append("AND NO_FOLIO_DET IN (SELECT NO_FOLIO_DET FROM TMP_FOLIOS_BUSCA_MOV_REG WHERE ID_USUARIO = "+GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+" ) \n");
		}
		
		if(!plantilla.getConcepto().equals("")){
			condicion.append(" AND m.concepto LIKE '%"+plantilla.getConcepto()+"%'");
			
		}
		
		if(!plantilla.getFactura().equals("")){
			condicion.append(" AND m.no_factura LIKE '%"+plantilla.getFactura()+"%'");
			
		}
		
		if( plantilla.getMontoUno()!=0 && plantilla.getMontoDos()!=0){
			condicion.append("AND m.importe BETWEEN "+plantilla.getMontoUno()+" AND "+plantilla.getMontoDos());
		}else{
			if(plantilla.getMontoUno()!=0 && plantilla.getMontoDos()==0){
				condicion.append("AND m.importe<="+plantilla.getMontoUno());
			}else{
				if( plantilla.getMontoUno()==0 && plantilla.getMontoDos()!=0){
					condicion.append("AND m.importe>="+plantilla.getMontoDos());
				}
			}
		}
		
		
    
		condicion.append("AND  UE.NO_USUARIO = '"+gb.getUsuarioLoginDto().getIdUsuario()+"'\n");
		
		
		sb.append(" SELECT distinct nom_empresa,NO_EMPRESA, SUM(SQ.VEN) AS TOTAL , SUM(SQ.POR_VEN) AS POR_VENCER , sum(total) AS total_global ");
		sb.append("\n FROM (SELECT m.NO_EMPRESA, ");
	    sb.append("\n 		case when m.fec_valor <(select fec_hoy from fechas) then sum(m.importe) else 0 end as VEN, ");
	    sb.append("\n	case when m.fec_valor >= (select fec_hoy from fechas) then sum(m.importe) else 0 end as POR_VEN, ");
	    sb.append("\n SUM(importe) as total, e.nom_empresa as nom_empresa ");
	    sb.append("\n FROM MOVIMIENTO m join usuario_empresa ue ");
	    sb.append("\n on m.NO_EMPRESA = ue.NO_EMPRESA JOIN EMPRESA E ON m.NO_EMPRESA=E.NO_EMPRESA ");
	    if(!plantilla.getGrupoEmpresas().equals(""))
			sb.append("join grupo_Empresa ge on ge.NO_EMPRESA = e.NO_EMPRESA \n");
	    if(!plantilla.getEquivalePersonaA().equals("")||!plantilla.getEquivalePersonaB().equals("")||rangosProveedores.size()>0 || proveedores)
			sb.append("join persona p on m.NO_CLIENTE = p.NO_PERSONA \n");
	    sb.append(condicion);
	    if(!plantilla.getGrupoEmpresas().equals("")){
			sb.append(" and ge.id_grupo_flujo =" +Utilerias.validarCadenaSQL(plantilla.getGrupoEmpresas())+ " \n");
		}
	    sb.append("\n group by  m.NO_EMPRESA,fec_valor,e.nom_empresa) SQ  ");
	    sb.append("\n	group by NO_EMPRESA,NOM_EMPRESA ");
	    sb.append("\n ORDER BY nom_empresa,NO_EMPRESA ");
		
		
		
		System.out.println("----------------obtenerPagosPendientes------ \n");
		System.out.println("----------------Primer grid------------------------- \n");
		System.out.println(sb.toString());
		System.out.println("\n ----------------primer grid------------------------ ");
		System.out.println("-------------------Fin nivel 1----------------------- \n");
		
		
		try {
			
			pagosPendietnes = jdbcTemplate.query(sb.toString(), new RowMapper<PagosPendientesDto>() {
				public PagosPendientesDto mapRow(ResultSet rs, int idx) throws SQLException{
					PagosPendientesDto pagosPendienteDto = new PagosPendientesDto();
					pagosPendienteDto.setTotalGlobal(rs.getDouble("total_global"));
					pagosPendienteDto.setNumeroEmpresa(rs.getInt("NO_EMPRESA"));
					pagosPendienteDto.setNombreEmpresa(rs.getString("NOM_EMPRESA"));
					pagosPendienteDto.setTotal(rs.getDouble("TOTAL"));
					pagosPendienteDto.setPorVencer(rs.getDouble("POR_VENCER"));
								
				return pagosPendienteDto;
			}});
			
			
			
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Egresos, C:PagosPedientesDaoImpl, M:obtenerPagosPendientes");
		}
		
		return pagosPendietnes;
	}
	
	
	private String obtenerRenglonesConRegla(int idRegla){
		String idsFolioDet = "";
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		try{
			idsFolioDet = consultasGenerales.ejecutarBuscaMovRegla(idRegla).get("V_folios").toString();
			
		}catch(Exception e){
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:obtenerRenglonesConRegla");
		}
		
		
		
		return idsFolioDet;
	}

	
	@Override
	public List<PagosPendientesDto> obtenerPagosPendientesNivDos(int numeroEmpresa, PlantillaDto plantilla,List<Map<String, String>> rangosDocumentos ,List<Map<String, String>> rangosProveedores, boolean proveedores, boolean documentos){
		StringBuffer sb = new StringBuffer();
		List<PagosPendientesDto> pagosPendietnes = new ArrayList<PagosPendientesDto>();
		String renglonesRegla = "";
		//int cont = hayFoliosRegla();
		
		if(plantilla.getIdReglaNegocio() > 0){
			renglonesRegla = obtenerRenglonesConRegla(plantilla.getIdReglaNegocio());
			System.out.println(renglonesRegla);
		}
		
		//sb.append(" select  NO_EMPRESA,EQUIVALE_PERSONA,BENEFICIARIO,sum(VENCIDO) as VENCIDO,sum(POR_VENCER) as POR_VENCER,CHEQUERA,ID_BANCO,DESC_FORMA_PAGO \n");
		sb.append(" select  NO_EMPRESA,EQUIVALE_PERSONA,BENEFICIARIO,sum(VENCIDO) as VENCIDO,sum(POR_VENCER) as POR_VENCER,max(CHEQUERA)as CHEQUERA,max(ID_BANCO)as ID_BANCO,'VARIOS' as DESC_FORMA_PAGO \n");
		
		sb.append(" from  ( \n");
		sb.append(" SELECT m.NO_EMPRESA, p.EQUIVALE_PERSONA, BENEFICIARIO, \n");	
		sb.append(" case when m.fec_valor < (select fec_hoy from fechas) then m.importe else 0 end as VENCIDO, \n");
		sb.append(" case when m.fec_valor  >= (select fec_hoy from fechas) then m.importe else 0 end as POR_VENCER, \n");
		
		sb.append(" ID_CHEQUERA_BENEF AS CHEQUERA, ID_BANCO_BENEF AS ID_BANCO \n");
		sb.append(" FROM MOVIMIENTO m JOIN PERSONA p ON m.NO_CLIENTE = p.NO_PERSONA  \n");
		sb.append(" WHERE m.NO_EMPRESA = " + numeroEmpresa + " \n");
		if(plantilla.getTipoBusqueda().equals("P")){
			sb.append("\n AND P.ID_TIPO_PERSONA = 'P' ");	
			sb.append("\n AND ID_TIPO_OPERACION = 3000 ");
			sb.append("\n AND ID_ESTATUS_MOV IN ('N','C') ");
			//sb.append("\n AND (CVE_CONTROL IS NULL OR CVE_CONTROL = '') ");
			}
			else{
				sb.append("\n AND P.ID_TIPO_PERSONA = 'E' ");
				sb.append("\n AND ID_TIPO_MOVTO='E' ");
				sb.append("\n AND ID_TIPO_OPERACION = 3801 ");
				sb.append("\n AND ID_ESTATUS_MOV = 'L' ");
				sb.append("\n AND (ORIGEN_MOV = '' OR ORIGEN_MOV IS NULL) ");
				//sb.append("\n AND (CVE_CONTROL IS NULL OR CVE_CONTROL = '') ");
			}
		sb.append("\n AND (CVE_CONTROL IS NULL OR CVE_CONTROL = '')");
		
		if(!plantilla.getDivisa().equals("")){
			sb.append(" AND ID_DIVISA = '" + Utilerias.validarCadenaSQL(plantilla.getDivisa()) + "' \n");
		}
		
		if((!plantilla.getNumDocA().equals("") || !plantilla.getNumDocB().equals("")) && rangosDocumentos.size()<=0){
			if(!plantilla.getNumDocB().equals(""))
				sb.append(" AND CONVERT(INT, NO_DOCTO) = "+ Utilerias.validarCadenaSQL(plantilla.getNumDocB()) +" \n");	
			else
				sb.append(" AND CONVERT(INT, NO_DOCTO) = "+ Utilerias.validarCadenaSQL(plantilla.getNumDocA()) +" \n");	
		}else if(rangosDocumentos.size()>0){
			sb.append(" AND (");
			if(rangosDocumentos.size()==1){
				sb.append("CONVERT(INT, NO_DOCTO) BETWEEN "+ rangosDocumentos.get(0).get("de") +"  AND "+ rangosDocumentos.get(0).get("a")  +" \n");
			}else{
				for (int i = 0; i < rangosDocumentos.size(); i++) {
					sb.append(" CONVERT(INT, NO_DOCTO) BETWEEN "+ rangosDocumentos.get(i).get("de") +"  AND "+ rangosDocumentos.get(i).get("a")  +" \n");
					if((i+1)< rangosDocumentos.size()){
						sb.append("OR");
					}
				}
			}
			if (documentos) {
				sb.append("OR CONVERT(INT, NO_DOCTO) IN (");
				sb.append("SELECT CONVERT(INT, REFERENCIA) FROM RENTASTMP WHERE OBSERVACION = '");
				sb.append(GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
				sb.append("' AND FORMA_PAGO = 'D'");
				sb.append("))");
			} else {
				sb.append(")");
			}
		} else if (documentos) {
			sb.append(" AND (");
			sb.append("CONVERT(INT, NO_DOCTO) IN (");
			sb.append("SELECT CONVERT(INT, REFERENCIA) FROM RENTASTMP WHERE OBSERVACION = '");
			sb.append(GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
			sb.append("' AND FORMA_PAGO = 'D'");
			sb.append("))");
		}
			
			//Rango de personas
			if((!plantilla.getEquivalePersonaA().equals("") || !plantilla.getEquivalePersonaB().equals("")) && rangosProveedores.size()<=0){
				if(!plantilla.getNumDocB().equals(""))
					sb.append(" AND EQUIVALE_PERSONA = '"+ Utilerias.validarCadenaSQL(plantilla.getEquivalePersonaB()) +"' \n");	
				else
					sb.append(" AND EQUIVALE_PERSONA = '"+ Utilerias.validarCadenaSQL(plantilla.getEquivalePersonaA()) +"' \n");	
			}else if(rangosProveedores.size()>0){
				sb.append(" AND (");
				if(rangosProveedores.size()==1){
					sb.append("CONVERT(INT, REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE"
						+ "(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE"
						+ "(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE"
						+ "(REPLACE('ASDGFHFDSAFGHJFDSFGHH','A',0),'B',0),'C',0),'D',0),'E',0),'F',0),'G',0),'H',0),'I',0),"
						+ "'J',0),'K',0),'L',0),'M',0),'N',0),'O',0),'P',0),'Q',0),'R',0),'S',0),'T',0),'U',0),"
						+ "	'V',0),'W',0),'X',0),'Y',0),'Z',0),'0',0)) BETWEEN "+ rangosProveedores.get(0).get("de") +"  AND "+ rangosProveedores.get(0).get("a")  +" \n");
				}else{
					for (int i = 0; i < rangosProveedores.size(); i++) {
						sb.append(" CONVERT(INT, REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE"
						+ "(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE"
						+ "(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE"
						+ "(REPLACE('ASDGFHFDSAFGHJFDSFGHH','A',0),'B',0),'C',0),'D',0),'E',0),'F',0),'G',0),'H',0),'I',0),"
						+ "'J',0),'K',0),'L',0),'M',0),'N',0),'O',0),'P',0),'Q',0),'R',0),'S',0),'T',0),'U',0),"
						+ "	'V',0),'W',0),'X',0),'Y',0),'Z',0),'0',0)) BETWEEN "+ rangosProveedores.get(i).get("de") +"  AND "+ rangosProveedores.get(i).get("a")  +" \n");
						if((i+1)< rangosProveedores.size()){
							sb.append("OR");
						}
					}
				}
				if (proveedores) {
					sb.append("OR CONVERT(INT, REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE"
						+ "(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE"
						+ "(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE"
						+ "(REPLACE('ASDGFHFDSAFGHJFDSFGHH','A',0),'B',0),'C',0),'D',0),'E',0),'F',0),'G',0),'H',0),'I',0),"
						+ "'J',0),'K',0),'L',0),'M',0),'N',0),'O',0),'P',0),'Q',0),'R',0),'S',0),'T',0),'U',0),"
						+ "	'V',0),'W',0),'X',0),'Y',0),'Z',0),'0',0)) IN (");
					sb.append("SELECT REFERENCIA FROM RENTASTMP WHERE OBSERVACION = '");
					sb.append(GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
					sb.append("' AND FORMA_PAGO = 'P'");
					sb.append("))");
				} else {
					sb.append(")");
				}
			} else if (proveedores) {
				sb.append(" AND (");
				sb.append("CONVERT(INT, REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE"
						+ "(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE"
						+ "(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE"
						+ "(REPLACE('ASDGFHFDSAFGHJFDSFGHH','A',0),'B',0),'C',0),'D',0),'E',0),'F',0),'G',0),'H',0),'I',0),"
						+ "'J',0),'K',0),'L',0),'M',0),'N',0),'O',0),'P',0),'Q',0),'R',0),'S',0),'T',0),'U',0),"
						+ "	'V',0),'W',0),'X',0),'Y',0),'Z',0),'0',0)) IN (");
				sb.append("SELECT REFERENCIA FROM RENTASTMP WHERE OBSERVACION = '");
				sb.append(GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
				sb.append("' AND FORMA_PAGO = 'P'");
				sb.append("))");
			}
			
		if(!plantilla.getTipoDocumento().equals("")){
			sb.append(" AND ID_CONTABLE ='" +Utilerias.validarCadenaSQL(plantilla.getTipoDocumento())+ "' \n");
		}
		if(!plantilla.getIndicadores().equals("")){
			sb.append(" AND B_GEN_CONTA ='" +Utilerias.validarCadenaSQL(plantilla.getIndicadores())+ "' \n");
		}
		if(!plantilla.getFechaContableInicio().equals("") && !plantilla.getFechaContableInicio().equals("/undefined/")  
				&& !plantilla.getFechaContableFin().equals("") && !plantilla.getFechaContableFin().equals("/undefined/")){
			sb.append("AND FECHA_CONTABILIZACION BETWEEN '"+ Utilerias.validarCadenaSQL(plantilla.getFechaContableInicio()) +"' AND '"+ Utilerias.validarCadenaSQL(plantilla.getFechaContableFin()) +"' \n");
		}
		
		if(!plantilla.getFechaPropuestaPagoInicio().equals("") && !plantilla.getFechaPropuestaPagoInicio().equals("/undefined/") 
				&& !plantilla.getFechaPropuestaPagoFin().equals("") && !plantilla.getFechaPropuestaPagoFin().equals("/undefined/")){
			sb.append("AND FEC_VALOR BETWEEN '"+ Utilerias.validarCadenaSQL(plantilla.getFechaPropuestaPagoInicio()) +"' AND '"+ Utilerias.validarCadenaSQL(plantilla.getFechaPropuestaPagoFin()) +"' \n");
		}
		
		if(!renglonesRegla.equals("")){
			sb.append("AND NO_FOLIO_DET IN (SELECT NO_FOLIO_DET FROM TMP_FOLIOS_BUSCA_MOV_REG WHERE ID_USUARIO = "+GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+" ) \n");
		}
		
		sb.append(" ) A \n");
		sb.append(" GROUP BY NO_EMPRESA,EQUIVALE_PERSONA,BENEFICIARIO  \n");
		//sb.append(" ORDER BY BENEFICIARIO, NO_EMPRESA, EQUIVALE_PERSONA  \n");
		sb.append(" ORDER BY BENEFICIARIO, NO_EMPRESA \n");
		
		
		
		System.out.println("----------------obtenerPagosPendientesNivDos------ \n");
		System.out.println("----------------Nivel dos------------------------- \n");
		System.out.println(sb.toString());
		System.out.println("\n ----------------Nivel dos------------------------ ");
		System.out.println("-------------------Fin nivel 2----------------------- \n");
		try {
			pagosPendietnes = jdbcTemplate.query(sb.toString(), new RowMapper<PagosPendientesDto>() {
				public PagosPendientesDto mapRow(ResultSet rs, int idx) throws SQLException{
					PagosPendientesDto pagosPendienteDto = new PagosPendientesDto();
					
					pagosPendienteDto.setNumeroEmpresa(rs.getInt("NO_EMPRESA"));
					pagosPendienteDto.setEquivalePersona(rs.getString("EQUIVALE_PERSONA"));
					pagosPendienteDto.setBeneficiario(rs.getString("BENEFICIARIO"));
					pagosPendienteDto.setVencido(rs.getDouble("VENCIDO"));
					pagosPendienteDto.setPorVencer(rs.getDouble("POR_VENCER"));
					pagosPendienteDto.setFormaPago(rs.getString("DESC_FORMA_PAGO"));
					pagosPendienteDto.setChequera(rs.getString("CHEQUERA"));
					pagosPendienteDto.setIdBanco(rs.getInt("ID_BANCO"));
					pagosPendienteDto.setVencidoS(0);
					pagosPendienteDto.setVencidoP(rs.getDouble("VENCIDO"));
					pagosPendienteDto.setPorVencerS(0);
					pagosPendienteDto.setPorVencerP(rs.getDouble("POR_VENCER"));
					pagosPendienteDto.setReferenciaBanc("");
					pagosPendienteDto.setBandera(false);
					pagosPendienteDto.setbPorVencer(false);
					
					
				return pagosPendienteDto;
			}});
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:obtenerPagosPendientesNivDos");
		}
		
		//System.out.println("tamaño de pagos "+pagosPendietnes.size());
		return pagosPendietnes;
	}
	
	
	@Override
	public List<PagosPendientesDto> obtenerPagosPendientesNivTres(int numeroEmpresa, String acredor, PlantillaDto plantilla, List<Map<String, String>> rangosDocumentos ,List<Map<String, String>> rangosProveedores, boolean documentos, String beneficiario) {
		StringBuffer sb = new StringBuffer();
		List<PagosPendientesDto> pagosPendietnes = new ArrayList<PagosPendientesDto>();
		String renglonesRegla = "";
		//int cont = hayFoliosRegla();
		
		if(plantilla.getIdReglaNegocio() > 0){
			renglonesRegla = obtenerRenglonesConRegla(plantilla.getIdReglaNegocio());
			System.out.println(renglonesRegla);
		}
		
		sb.append(" SELECT ID_CONTABLE as CLASEDOCUMENTO,NO_FOLIO_DET, NO_FACTURA, NO_DOCTO ,IMPORTE, CONCEPTO, ID_DIVISA,DESC_FORMA_PAGO,FEC_VALOR_ORIGINAL, \n");
		sb.append(" CASE WHEN(FEC_VALOR < (SELECT FEC_HOY FROM FECHAS)) THEN 'VENCIDO'  \n");
		sb.append(" WHEN(FEC_VALOR >= (SELECT FEC_HOY FROM FECHAS)) THEN 'POR VENCER' END AS ESTATUS, \n");
		sb.append(" FEC_VALOR,FEC_VALOR_ORIGINAL FROM MOVIMIENTO m  JOIN PERSONA P ON m.NO_CLIENTE = p.NO_PERSONA JOIN CAT_FORMA_PAGO f ON m.ID_FORMA_PAGO = f.ID_FORMA_PAGO \n");
		sb.append(" WHERE m.NO_EMPRESA = "+Utilerias.validarCadenaSQL(numeroEmpresa)+" ");
		sb.append(" AND EQUIVALE_PERSONA IN ('"+Utilerias.validarCadenaSQL(acredor)+"') "
				+ " and beneficiario = '" + beneficiario + "' \n");
		
		if(plantilla.getTipoBusqueda().equals("P")){		
		    sb.append("\n AND P.ID_TIPO_PERSONA = 'P' ");
			sb.append("\n AND ID_TIPO_OPERACION = 3000 ");
			sb.append("\n AND ID_ESTATUS_MOV IN ('N','C') ");
			sb.append("\n AND (CVE_CONTROL IS NULL OR CVE_CONTROL = '') ");	
		}
		else{	
			sb.append("\n AND P.ID_TIPO_PERSONA = 'E' ");
			sb.append("\n AND ID_TIPO_MOVTO='E' ");
			sb.append("\n AND ID_TIPO_OPERACION = 3801 ");
			sb.append("\n AND ID_ESTATUS_MOV = 'L' ");
			sb.append("\n AND (CVE_CONTROL IS NULL OR CVE_CONTROL = '')");
			sb.append("\n AND (ORIGEN_MOV = '' OR ORIGEN_MOV IS NULL) ");
		}
		
		if(!plantilla.getDivisa().equals("")){
			sb.append(" AND ID_DIVISA = '" + Utilerias.validarCadenaSQL(plantilla.getDivisa()) + "' \n");
		}
		
		if((!plantilla.getNumDocA().equals("") || !plantilla.getNumDocB().equals("")) && rangosDocumentos.size()<=0){
			if(!plantilla.getNumDocB().equals(""))
				sb.append(" AND CONVERT(INT, NO_DOCTO) = "+ Utilerias.validarCadenaSQL(plantilla.getNumDocB()) +" \n");	
			else
				sb.append(" AND CONVERT(INT, NO_DOCTO) = "+ Utilerias.validarCadenaSQL(plantilla.getNumDocA()) +" \n");	
		}else if(rangosDocumentos.size()>0){
			sb.append(" AND (");
			if(rangosDocumentos.size()==1){
				sb.append("CONVERT(INT, NO_DOCTO) BETWEEN "+ rangosDocumentos.get(0).get("de") +"  AND "+ rangosDocumentos.get(0).get("a")  +" \n");
			}else{
				for (int i = 0; i < rangosDocumentos.size(); i++) {
					sb.append(" CONVERT(INT, NO_DOCTO) BETWEEN "+ rangosDocumentos.get(i).get("de") +"  AND "+ rangosDocumentos.get(i).get("a")  +" \n");
					if((i+1)< rangosDocumentos.size()){
						sb.append("OR");
					}
				}
			}
			if (documentos) {
				sb.append("OR CONVERT(INT, NO_DOCTO) IN (");
				sb.append("SELECT CONVERT(INT, REFERENCIA) FROM RENTASTMP WHERE OBSERVACION = '");
				sb.append(GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
				sb.append("' AND FORMA_PAGO = 'D'");
				sb.append("))");
			} else {
				sb.append(")");
			}
		} else if (documentos) {
			sb.append(" AND (");
			sb.append("CONVERT(INT, NO_DOCTO) IN (");
			sb.append("SELECT CONVERT(INT, REFERENCIA) FROM RENTASTMP WHERE OBSERVACION = '");
			sb.append(GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
			sb.append("' AND FORMA_PAGO = 'D'");
			sb.append("))");
		}
		
		if(!plantilla.getTipoDocumento().equals("")){
			sb.append(" AND ID_CONTABLE ='" +Utilerias.validarCadenaSQL(plantilla.getTipoDocumento())+ "' \n");
		}
		if(!plantilla.getIndicadores().equals("")){
			sb.append(" AND B_GEN_CONTA ='" +Utilerias.validarCadenaSQL(plantilla.getIndicadores())+ "' \n");
		}
		if(!plantilla.getFechaContableInicio().equals("") && !plantilla.getFechaContableInicio().equals("/undefined/")  
				&& !plantilla.getFechaContableFin().equals("") && !plantilla.getFechaContableFin().equals("/undefined/")){
			sb.append("AND FECHA_CONTABILIZACION BETWEEN '"+ plantilla.getFechaContableInicio() +"' AND '"+ plantilla.getFechaContableFin() +"' \n");
		}
		
		if(!plantilla.getFechaPropuestaPagoInicio().equals("") && !plantilla.getFechaPropuestaPagoInicio().equals("/undefined/") 
				&& !plantilla.getFechaPropuestaPagoFin().equals("") && !plantilla.getFechaPropuestaPagoFin().equals("/undefined/")){
			sb.append("AND FEC_VALOR BETWEEN '"+ Utilerias.validarCadenaSQL(plantilla.getFechaPropuestaPagoInicio()) +"' AND '"+ Utilerias.validarCadenaSQL(plantilla.getFechaPropuestaPagoFin()) +"' \n");
		}
		
		if(!renglonesRegla.equals("")){
			//sb.append("AND NO_FOLIO_DET IN "+renglonesRegla+" \n");
			sb.append("AND NO_FOLIO_DET IN (SELECT NO_FOLIO_DET FROM TMP_FOLIOS_BUSCA_MOV_REG WHERE ID_USUARIO = "+GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+" ) \n");
		}
		
		sb.append("order by NO_DOCTO");
		
		System.out.println("----------------obtenerPagosPendientesNivTres------ \n");
		System.out.println("----------------Nivel tres------------------------- \n");
		System.out.println(sb.toString());
		System.out.println("\n ----------------Nivel tres------------------------ ");
		System.out.println("-------------------Fin nivel 3----------------------- \n");
		
		try {
			
			pagosPendietnes = jdbcTemplate.query(sb.toString(), new RowMapper<PagosPendientesDto>() {
				public PagosPendientesDto mapRow(ResultSet rs, int idx) throws SQLException{
					SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd/MM/yyyy");
					PagosPendientesDto pagosPendienteDto = new PagosPendientesDto();
					pagosPendienteDto.setTexto(rs.getString("CONCEPTO"));
					pagosPendienteDto.setImporteNeto(rs.getDouble("IMPORTE"));
					pagosPendienteDto.setFactura(rs.getString("NO_FOLIO_DET"));
					pagosPendienteDto.setCveControl(rs.getString("NO_FACTURA"));
					pagosPendienteDto.setNumeroDcoumento(rs.getString("NO_DOCTO"));
					pagosPendienteDto.setDivisa(rs.getString("ID_DIVISA"));
					pagosPendienteDto.setEstatus(rs.getString("ESTATUS"));
					pagosPendienteDto.setClaseDocumento(rs.getString("CLASEDOCUMENTO"));
					pagosPendienteDto.setFechaPropuesta(formatoDelTexto.format(rs.getDate("FEC_VALOR")));
					pagosPendienteDto.setIndex(idx);
					pagosPendienteDto.setFormaPago(rs.getString("DESC_FORMA_PAGO"));
					pagosPendienteDto.setFechaFactura(formatoDelTexto.format(rs.getDate("FEC_VALOR_ORIGINAL")));
					pagosPendienteDto.setSeleccionado("");
				return pagosPendienteDto;
			}});
			
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Egresos, C:PagosPedientesDaoImpl, M:obtenerPagosPendientesNivTres");
		}
		
		return pagosPendietnes;
		
	}
	
	@Override
	public int obtenerFolioCupos(int claveUsuario){
		StringBuffer sb = new StringBuffer();
		int folio = 0;
		
		try {
			sb.append(" SELECT FOLIO_CONTROL FROM CAT_USUARIO \n");
			sb.append(" WHERE NO_USUARIO = "  + Utilerias.validarCadenaSQL(claveUsuario) + " \n");
			
			folio = jdbcTemplate.queryForInt(sb.toString());
			actualizarFolioCupos(claveUsuario);
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:obtenerFolioCupos");
		}
		
		folio++;
		
		return folio;
	}
	
	public int actualizarFolioCupos(int claveUsuario){
		StringBuffer sb = new StringBuffer();
		int res = -1;
		
		try {
			sb.append(" UPDATE cat_usuario \n");
			sb.append(" SET folio_control = folio_control + 1 \n");
			sb.append(" WHERE no_usuario = " + Utilerias.validarCadenaSQL(claveUsuario));
			
			res = jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:actualizarFolioCupos");
		}
		
		
		return res;
		
	}
	
	@Override
	public int insertarPropuestaPago(String claveControl, int sociedad, String fecha, String concepto){
		
		StringBuffer sb = new StringBuffer();
		int res = -1;
		try {
			sb.append(" INSERT into seleccion_automatica_grupo ( id_grupo, fecha_propuesta, \n");
			sb.append(" id_grupo_flujo, monto_maximo, cupo_manual, cupo_automatico,  \n");
			sb.append(" cupo_total, fec_limite_selecc, cve_control, id_division, concepto)  \n");
			sb.append(" VALUES( " + 0 + ", convert(datetime, '");
			sb.append(Utilerias.validarCadenaSQL(fecha) + "', 103), ");
			sb.append(Utilerias.validarCadenaSQL(sociedad) + ", 0.0, ");
			sb.append(0 + ", 0.0, 0.0, convert(datetime, '");
			sb.append(Utilerias.validarCadenaSQL(fecha) + "', 103), '");
			sb.append(Utilerias.validarCadenaSQL(claveControl) + "', null, '");
			sb.append(Utilerias.validarCadenaSQL(concepto) + "' )");
			System.out.println("insertarPropuestaPago "+sb);
			res = jdbcTemplate.update(sb.toString());
			
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:insertarPropuestaPago");
		}
		
		return res;
	}
	
	@Override
	public int actualizaMvimiento(String facturas, String chequera, int idBanco, String referenciaBanc, int idBancoP , String chequeraP , String fecha, String claveControl, String tipoBusqueda){
		StringBuffer sb = new StringBuffer();
		int res = -1;
		try {
				if(tipoBusqueda.equals("I")){
					sb.append(" UPDATE movimiento \n");
					sb.append(" SET fec_propuesta = convert(datetime, '" + fecha + "', 103) ,\n");
					
					if (referenciaBanc != null && !referenciaBanc.equals("")) 
						sb.append(" REFERENCIA = '" + Utilerias.validarCadenaSQL(referenciaBanc) + "' ,\n");
					
					if(chequera != null && !chequera.equals("")){
						sb.append(" ID_CHEQUERA_BENEF = '" + Utilerias.validarCadenaSQL(chequera) + "' ,\n");
						sb.append(" ID_BANCO_BENEF = " + Utilerias.validarCadenaSQL(idBanco) + " ,\n");
						
						
						if(chequeraP != null && !chequeraP.equals("")){
							System.out.println("CHEQUERA --> " + chequeraP);
							sb.append(" ID_CHEQUERA = '" + Utilerias.validarCadenaSQL(chequeraP) + "', \n");
							sb.append(" ID_BANCO =  " + Utilerias.validarCadenaSQL(idBancoP) +",  \n");
						}
							
					}
					sb.append(" cve_control = '" + Utilerias.validarCadenaSQL(claveControl) + "' \n");
					sb.append(" WHERE (no_folio_det in ("+facturas+") OR folio_ref in ("+facturas+")) \n");
					sb.append(" AND id_tipo_operacion in (3000, 3001,3801) AND id_tipo_movto='E' \n");
					System.out.println("actualiza mvimiento1 "+sb.toString());
					res = jdbcTemplate.update(sb.toString());
					sb.delete(0, sb.length());
					
					sb.append(" UPDATE movimiento \n");
					sb.append(" SET fec_propuesta = convert(datetime, '" + fecha + "', 103) ,\n");
					
					if (referenciaBanc != null && !referenciaBanc.equals("")) 
						sb.append(" REFERENCIA = '" + Utilerias.validarCadenaSQL(referenciaBanc) + "' ,\n");
					
					if(chequeraP != null && !chequeraP.equals("")){
						sb.append(" ID_CHEQUERA_BENEF = '" + Utilerias.validarCadenaSQL(chequeraP) + "' ,\n");
						sb.append(" ID_BANCO_BENEF = " + Utilerias.validarCadenaSQL(idBancoP) + " ,\n");
						
						
						if(chequera != null && !chequera.equals("")){
							System.out.println("CHEQUERA --> " + chequera);
							sb.append(" ID_CHEQUERA = '" + Utilerias.validarCadenaSQL(chequera) + "', \n");
							sb.append(" ID_BANCO =  " + Utilerias.validarCadenaSQL(idBanco) +",  \n");
						}
							
					}
					sb.append(" cve_control = '" + Utilerias.validarCadenaSQL(claveControl) + "' \n");
					sb.append(" WHERE (no_folio_det in ("+facturas+") OR folio_ref in ("+facturas+")) \n");
					sb.append(" AND id_tipo_operacion in (3000, 3001,3801) AND id_tipo_movto='I' \n");
					System.out.println( "actualiza mvimiento2 "+sb.toString());
					res = jdbcTemplate.update(sb.toString());
				}
				else{
					sb.append(" UPDATE movimiento \n");
					sb.append(" SET fec_propuesta = convert(datetime, '" + fecha + "', 103) ,\n");
					
					if (referenciaBanc != null && !referenciaBanc.equals("")) 
						sb.append(" REFERENCIA = '" + Utilerias.validarCadenaSQL(referenciaBanc) + "' ,\n");
					
					if(chequera != null && !chequera.equals("")){
						sb.append(" ID_CHEQUERA_BENEF = '" + Utilerias.validarCadenaSQL(chequera) + "' ,\n");
						sb.append(" ID_BANCO_BENEF = " + Utilerias.validarCadenaSQL(idBanco) + " ,\n");
						
						
						if(chequeraP != null && !chequeraP.equals("")){
							System.out.println("CHEQUERA --> " + chequeraP);
							sb.append(" ID_CHEQUERA = '" + Utilerias.validarCadenaSQL(chequeraP) + "', \n");
							sb.append(" ID_BANCO =  " + Utilerias.validarCadenaSQL(idBancoP) +",  \n");
						}
							
					}
					sb.append(" cve_control = '" + Utilerias.validarCadenaSQL(claveControl) + "' \n");
					sb.append(" WHERE (no_folio_det in ("+facturas+") OR folio_ref in ("+facturas+")) \n");
					sb.append(" AND id_tipo_operacion in (3000, 3001,3801) \n");
					System.out.println("actualiza mvimiento3 "+sb.toString());
					res = jdbcTemplate.update(sb.toString());
				}
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:actualizaMvimiento");
		}
		
		
		return res;

	}
	
	@Override
	public List<LlenaComboGralDto> obtenerListaBancos(String proveedor, String tipoBusqueda) {
		List<LlenaComboGralDto> listBancos = new ArrayList<LlenaComboGralDto>();
		StringBuffer sb = new StringBuffer();
		
		try {
			if(tipoBusqueda.equals("I")){
				sb.append(" SELECT c.ID_BANCO, DESC_BANCO FROM CAT_BANCO c ");
				sb.append("\n JOIN CAT_CTA_BANCO b ON c.ID_BANCO = b.ID_BANCO ");
				sb.append("\n JOIN PERSONA P ON P.no_empresa = b.no_empresa ");
				sb.append("\n WHERE EQUIVALE_PERSONA = '"+Utilerias.validarCadenaSQL(proveedor)+ "'");
				sb.append("\n  GROUP BY c.ID_BANCO, DESC_BANCO " );
			}
			else{	
				sb.append(" SELECT c.ID_BANCO, DESC_BANCO FROM CAT_BANCO c \n");
				sb.append(" JOIN CTAS_BANCO b ON c.ID_BANCO = b.ID_BANCO JOIN PERSONA P ON P.NO_PERSONA = b.NO_PERSONA \n");
				sb.append(" WHERE EQUIVALE_PERSONA = '"+Utilerias.validarCadenaSQL(proveedor)+ "'\n");
			}
			System.out.println(sb.toString());
			listBancos = jdbcTemplate.query(sb.toString(), new RowMapper<LlenaComboGralDto>() {
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException{
					LlenaComboGralDto llenarComboBancosDto = new LlenaComboGralDto();
					
					llenarComboBancosDto.setId(rs.getInt("ID_BANCO"));
					llenarComboBancosDto.setDescripcion(rs.getString("DESC_BANCO"));
					
				return llenarComboBancosDto;
			}});
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:obtenerListaBancos");
		} 
		return listBancos;
	}
	
	@Override
	public List<LlenaComboGralDto> obtenerListaChequeras(String proveedor, int idBanco, String tipoBusqueda){
		List<LlenaComboGralDto> listChequeras = new ArrayList<LlenaComboGralDto>();
		StringBuffer sb = new StringBuffer();
		
		try {
			if(tipoBusqueda.equals("I"))
			{
				sb.append(" SELECT ID_CHEQUERA FROM CAT_CTA_BANCO b ");
				sb.append("\n JOIN PERSONA P ON P.NO_EMPRESA = b.NO_EMPRESA " );
				sb.append("\n WHERE EQUIVALE_PERSONA = '"+ proveedor +"' ");
				sb.append("\n AND ID_BANCO = "+idBanco+"");
				sb.append("\n AND b.TIPO_CHEQUERA IN ('C','M')");
			}
			else{
				sb.append(" SELECT ID_CHEQUERA FROM CTAS_BANCO b JOIN PERSONA P ON P.NO_PERSONA = b.NO_PERSONA\n");
				sb.append(" WHERE EQUIVALE_PERSONA = '" + proveedor + "' AND ID_BANCO = "+ idBanco +" \n");
			}
			System.out.println(sb.toString());
			listChequeras = jdbcTemplate.query(sb.toString(), new RowMapper<LlenaComboGralDto>() {
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException{
					LlenaComboGralDto llenarComboChequerasDto = new LlenaComboGralDto();
					
					llenarComboChequerasDto.setDescripcion(rs.getString("ID_CHEQUERA"));
					
				return llenarComboChequerasDto;
			}});

		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:obtenerListaChequeras");
		}
		
		return listChequeras;
	}
	
	@Override
	public List<LlenaComboGralDto> obtenerListaBancosPagador(int noEmpresa, String divisa){
		List<LlenaComboGralDto> listBancos = new ArrayList<LlenaComboGralDto>();
		StringBuffer sb = new StringBuffer();
		
		try {
			
			sb.append(" SELECT DISTINCT c.ID_BANCO, DESC_BANCO \n");
			sb.append(" FROM CAT_CTA_BANCO c JOIN CAT_BANCO b \n");
			sb.append(" ON c.ID_BANCO = b.ID_BANCO WHERE NO_EMPRESA = " + Utilerias.validarCadenaSQL(noEmpresa) + " \n");
			sb.append(" AND TIPO_CHEQUERA IN ('P', 'M') \n");
			System.out.println(sb.toString());
			
			listBancos = jdbcTemplate.query(sb.toString(), new RowMapper<LlenaComboGralDto>() {
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException{
					LlenaComboGralDto llenarComboBancosDto = new LlenaComboGralDto();
					
					llenarComboBancosDto.setId(rs.getInt("ID_BANCO"));
					llenarComboBancosDto.setDescripcion(rs.getString("DESC_BANCO"));
					
				return llenarComboBancosDto;
			}});	
					
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:obtenerListaBancosPagador");
		}
		
		return listBancos;
	}
	
	@Override
	public List<LlenaComboGralDto> obtenerListaChequerasPagadoras(int noEmpresa, String divisa, int idBanco, String beneficiario, String acreedor){
		List<LlenaComboGralDto> listaChequerasPagadoras = new ArrayList<LlenaComboGralDto>();
		StringBuffer sb = new StringBuffer();
		
		try{
			sb.append(" SELECT DISTINCT CCB.ID_CHEQUERA FROM CAT_CTA_BANCO CCB, movimiento M JOIN PERSONA P ON m.NO_CLIENTE = p.NO_PERSONA JOIN CAT_FORMA_PAGO f ON m.ID_FORMA_PAGO = f.ID_FORMA_PAGO    \n");
			sb.append(" WHERE  CCB.NO_EMPRESA = "+ Utilerias.validarCadenaSQL(noEmpresa)+ "\n");	
			sb.append(" AND CCB.TIPO_CHEQUERA IN ('P', 'M') AND CCB.ID_BANCO = "+ Utilerias.validarCadenaSQL(idBanco) +" \n");
			sb.append(" AND EQUIVALE_PERSONA IN ('"+ Utilerias.validarCadenaSQL(acreedor) +"') \n");
			sb.append(" AND BENEFICIARIO = '"+ beneficiario + "' \n");
			sb.append(" AND M.id_divisa= ccb.id_divisa" +" \n");
			System.out.println(sb.toString());
			
			
			listaChequerasPagadoras = jdbcTemplate.query(sb.toString(), new RowMapper<LlenaComboGralDto>() {
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException{
					LlenaComboGralDto llenarComboChequerasPagadorasDto = new LlenaComboGralDto();		
					
					llenarComboChequerasPagadorasDto.setDescripcion(rs.getString("ID_CHEQUERA"));
					
				return llenarComboChequerasPagadorasDto;
			}});

			
		}catch(Exception e){
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:obtenerListaChequerasPagadoras");
		}
		return listaChequerasPagadoras;
	}
	
	@Override
	public List<LlenaComboDivisaDto> obtenerDivisas(){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.llenarComboDivisa();
	}
	
	@Override
	public PlantillaDto obtenerFechasIni() {
		List<PlantillaDto> platillas = new ArrayList<PlantillaDto>();
		StringBuffer sb = new StringBuffer();


		try{
		sb.append("SELECT MIN(FEC_VALOR) AS propuesta, ");
		sb.append("CASE WHEN MIN(FECHA_CONTABILIZACION)=null THEN MIN(FECHA_CONTABILIZACION) ELSE '' END AS contable, ");
		sb.append("MIN(DATEADD(MONTH,3,FEC_HOY)) as fec_fin  ");
		sb.append("FROM MOVIMIENTO, FECHAS \n");
		
		System.out.println(sb.toString());
		
		platillas = jdbcTemplate.query(sb.toString(), new RowMapper<PlantillaDto>() {
			public PlantillaDto mapRow(ResultSet rs, int idx) throws SQLException{
				PlantillaDto plantilla = new PlantillaDto();
				System.out.println(rs.getDate("contable").toString());
				plantilla.setFechaContableInicio(rs.getDate("contable").toString() != null ? rs.getDate("contable").toString() : "");
				plantilla.setFechaPropuestaPagoInicio(rs.getDate("propuesta").toString());
				plantilla.setFechaContableFin(rs.getDate("fec_fin").toString());
				
			return plantilla;
		}});

		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:obtenerFechasIni");
		}
		
		return platillas.get(0);
		
	}
	
	@Override
	public String guardarPlantilla(PlantillaDto plantillas, int bandera) {
		StringBuffer sb = new StringBuffer();
		String mensaje = "";
		try{
			sb.append("INSERT INTO ASIGNACION_PLANTILLAS (	ID_PLANTILLA,  ID_REGLA_NEGOCIO,\n");
			sb.append("		FECHA_CONTABLE_INICIO , FECHA_CONTABLE_FIN , \n");
			sb.append(" 	FECHA_PROP_PAGO_INICIO , FECHA_PROP_PAGO_FIN, \n");
			sb.append("     ID_DIVISA, INDICADORES , TIPO_DOCUMENTO, ID_USUARIO,  \n");
			sb.append("     NOMBRE_PLANTILLA ,grupoempresa , b_rango , NOMBRE_REGLA ) \n");
			
			sb.append("VALUES(CASE WHEN (SELECT MAX(ID_PLANTILLA) FROM ASIGNACION_PLANTILLAS) \n");
			sb.append(" IS NOT NULL THEN (SELECT MAX(ID_PLANTILLA) + 1 FROM ASIGNACION_PLANTILLAS) ELSE 1 END, \n");
			sb.append(" "+Utilerias.validarCadenaSQL(plantillas.getIdReglaNegocio())+" , \n");
			sb.append(" '"+Utilerias.validarCadenaSQL(plantillas.getFechaContableInicio())+"' , \n");
			sb.append(" '"+Utilerias.validarCadenaSQL(plantillas.getFechaContableFin())+"' , \n");
			sb.append(" '"+Utilerias.validarCadenaSQL(plantillas.getFechaPropuestaPagoInicio())+"' , \n");
			sb.append(" '"+Utilerias.validarCadenaSQL(plantillas.getFechaPropuestaPagoFin())+"' , \n");
			sb.append(" '"+Utilerias.validarCadenaSQL(plantillas.getDivisa())+"' , \n");
			sb.append(" '"+ Utilerias.validarCadenaSQL(plantillas.getIndicadores()) +"', \n");
			sb.append(" '"+ Utilerias.validarCadenaSQL(plantillas.getTipoDocumento())+ "', \n");
			sb.append(Utilerias.validarCadenaSQL(plantillas.getIdUsuario())+", \n");
			sb.append(" '"+Utilerias.validarCadenaSQL(plantillas.getNombrePlantilla())+"', \n");
			sb.append(" '"+Utilerias.validarCadenaSQL(plantillas.getGrupoEmpresas())+"' , \n");
			sb.append("'"+ bandera +"', \n");
			sb.append(" '"+plantillas.getNombreRegla()+"' )\n");
			
			System.out.println( "insert \n" + sb.toString() + "fin insert \n");
			int res = jdbcTemplate.update(sb.toString());
			
			if(res == 1){
				mensaje = "Plantilla guardada correctamnte";
			}else{
				mensaje = "Ocurrio un error al guardar la plantilla";
			}
			
		}catch(Exception e){
			e.getStackTrace();
			mensaje = "Ocurrio un error al guardar la plantilla";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:guardarPlantilla");
		}
		
		
		return mensaje;
	}
	
	public String guardarRangos(List<Map<String, String>> rangosDocumentos ,List<Map<String, String>> rangosProveedores){
		StringBuffer sb = new StringBuffer();
		String mensaje = "";
		try{
			sb.append("SELECT MAX(ID_PLANTILLA) FROM ASIGNACION_PLANTILLAS");
			int id_plantilla = jdbcTemplate.queryForInt(sb.toString());
			
			sb.delete(0, sb.length());
			
			sb.append("select case when (MAX(ID_RANGO) IS not NULL) then MAX(ID_RANGO) else 1 end as id_rango  FROM ASIG_PLANT_RANG");
			int id_rango = jdbcTemplate.queryForInt(sb.toString());
			
			sb.delete(0, sb.length());
			sb.append("INSERT all   \n");
			
			for (int i = 0; i < rangosDocumentos.size(); i++) {
				sb.append("INTO ASIG_PLANT_RANG (ID_RANGO , ID_PLANTILLA, rangoA, rangoB, TIPO) values ");
				sb.append("( "+id_rango +","+id_plantilla+",'"+rangosDocumentos.get(i).get("de")+ "','" +rangosDocumentos.get(i).get("a")+"','D') \n" );
				id_rango++;
			}
			
			for (int i = 0; i < rangosProveedores.size(); i++) {
				sb.append("INTO ASIG_PLANT_RANG (ID_RANGO , ID_PLANTILLA, rangoA, rangoB, TIPO) values ");
				sb.append("( "+id_rango +","+id_plantilla+",'"+rangosProveedores.get(i).get("de")+ "','" +rangosProveedores.get(i).get("a")+"','P') \n" );
				id_rango++;
			}
			
			sb.append("select * from dual  \n");
			
			
			System.out.println( "insert_RANGO \n" + sb.toString() + "fin insert_RANGO \n");
			int res = jdbcTemplate.update(sb.toString());
			
			if(res > 0 ){
				mensaje = "Plantilla guardada correctamnte";
			}else{
				mensaje = "La plantilla se ha guardado  correctamente <br> pero ocurrio un error al guardar los rangos de documentos y proveedores.";
			}
			
		}catch(Exception e){
			e.getStackTrace();
			mensaje = "Ocurrio un error al guardar la plantilla";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:guardarPlantilla");
		}
		
		
		return mensaje;
	}

	@Override
	public List<PlantillaDto> obtenerListaPlantillas() {
		List<PlantillaDto> listPlantillas = new ArrayList<PlantillaDto>();
		StringBuffer sb = new StringBuffer();
		
		try{
			sb.append("SELECT ID_PLANTILLA, NOMBRE_PLANTILLA FROM ASIGNACION_PLANTILLAS \n");
			sb.append("WHERE ID_USUARIO = "+ gb.getUsuarioLoginDto().getIdUsuario() +" \n");
			
			listPlantillas = jdbcTemplate.query(sb.toString(), new RowMapper<PlantillaDto>() {
				public PlantillaDto mapRow(ResultSet rs, int idx) throws SQLException{
					PlantillaDto plantilla = new PlantillaDto();
					plantilla.setIdPlantilla(rs.getInt("ID_PLANTILLA"));
					plantilla.setNombrePlantilla(rs.getString("NOMBRE_PLANTILLA"));
				return plantilla;
			}});
		}catch(Exception e){
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:obtenerListaPlantillas");
		}
		return listPlantillas;
	}

	@Override
	public PlantillaDto obtenerPlantilla(int idPlantilla){
		List<PlantillaDto> plantilla = new ArrayList<PlantillaDto>();
		StringBuffer sb = new StringBuffer();
		
		try {
			
			sb.append("SELECT ID_PLANTILLA,ID_REGLA_NEGOCIO, \n");
			sb.append( "to_char(FECHA_CONTABLE_INICIO , 'dd/mm/yyyy') as FECHA_CONTABLE_INICIO,\n");
			sb.append( "to_char(FECHA_CONTABLE_FIN , 'dd/mm/yyyy') as FECHA_CONTABLE_FIN,  \n");
			sb.append("to_char(FECHA_PROP_PAGO_INICIO , 'dd/mm/yyyy') as FECHA_PROP_PAGO_INICIO,\n");
			sb.append( "to_char(FECHA_PROP_PAGO_FIN , 'dd/mm/yyyy') as FECHA_PROP_PAGO_FIN,  \n");
			sb.append(" ID_DIVISA,INDICADORES, TIPO_DOCUMENTO, grupoEmpresa, NOMBRE_REGLA, b_rango \n");
			sb.append("FROM ASIGNACION_PLANTILLAS \n");
			sb.append("WHERE ID_PLANTILLA = "+ idPlantilla +" \n");
			
			System.out.println(sb.toString());
			plantilla = jdbcTemplate.query(sb.toString(), new RowMapper<PlantillaDto>() {
				public PlantillaDto mapRow(ResultSet rs, int idx) throws SQLException{
					PlantillaDto plantillaDto = new PlantillaDto();
					
					plantillaDto.setIdPlantilla(rs.getInt("ID_PLANTILLA"));
					plantillaDto.setIdReglaNegocio(rs.getInt("ID_REGLA_NEGOCIO"));
					plantillaDto.setFechaContableInicio(rs.getString("FECHA_CONTABLE_INICIO"));
					plantillaDto.setFechaContableFin(rs.getString("FECHA_CONTABLE_FIN"));
					plantillaDto.setFechaPropuestaPagoInicio(rs.getString("FECHA_PROP_PAGO_INICIO"));
					plantillaDto.setFechaPropuestaPagoFin(rs.getString("FECHA_PROP_PAGO_FIN"));
					plantillaDto.setDivisa(rs.getString("ID_DIVISA"));
					plantillaDto.setIndicadores(rs.getString("INDICADORES"));
					plantillaDto.setTipoDocumento(rs.getString("TIPO_DOCUMENTO"));
					plantillaDto.setGrupoEmpresas(rs.getString("grupoEmpresa"));
					plantillaDto.setbRango(rs.getString("b_rango"));
					plantillaDto.setNombreRegla(rs.getString("NOMBRE_REGLA"));
					
				return plantillaDto;
			}});
			
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:obtenerPlantilla");
		}
		
		
		return plantilla.get(0);
		
	}
	
	//obtener tablas de rangos
	public List<Map<String,String>> obtenerPlantillaRangos(int idPlantilla){
		List<Map<String,String>> resultado = new ArrayList<Map<String,String>>();
		StringBuffer sb = new StringBuffer();
	
		try {
			
			sb.append("SELECT RANGOA , RANGOB , TIPO \n");
			sb.append("FROM ASIG_PLANT_RANG \n");
			sb.append("WHERE ID_PLANTILLA = "+ idPlantilla +" \n");
			sb.append("ORDER BY TIPO \n");
			
			System.out.println(sb.toString());
			resultado = jdbcTemplate.query(sb.toString(), new RowMapper<Map<String,String>>() {
				public Map<String,String> mapRow(ResultSet rs, int idx) throws SQLException{
					Map<String,String> mapa = new HashMap<String,String>();
					
					mapa.put("de", rs.getString("RANGOA"));
					mapa.put("a", rs.getString("RANGOB"));
					mapa.put("tipo", rs.getString("TIPO"));
					
				return mapa;
			}});
			
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:obtenerPlantilla");
		}
		return resultado;
	}
	/*
	 * Cambios Extraï¿½os LASM
	 * 19/11/2015
	 */
	
	
	@Override
	public boolean horaLimmite(){
		StringBuffer sb = new StringBuffer();
		Calendar calendario = Calendar.getInstance();
		int horaLimite = 0;

		sb.append("SELECT VALOR FROM CONFIGURA_SET WHERE INDICE = 666");
		try {
			horaLimite = jdbcTemplate.queryForInt(sb.toString());
			
			
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:horaLimmite");
		}
		
		if(calendario.get(Calendar.HOUR_OF_DAY) < horaLimite){
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public String existeConcepto(String concepto){
		StringBuffer sb = new StringBuffer();
		int coincidencias = 0;
		String claveControl = "";
		
		sb.append("SELECT COUNT(*) FROM SELECCION_AUTOMATICA_GRUPO \n");
		sb.append("WHERE CONCEPTO = '"+ Utilerias.validarCadenaSQL(concepto) +"' AND (USUARIO_UNO IS NULL OR USUARIO_UNO = '') \n");
		sb.append("AND (USUARIO_DOS IS NULL OR USUARIO_DOS = '') \n");
		sb.append("AND (USUARIO_TRES IS NULL OR USUARIO_TRES = '') \n");
		
		try {
			coincidencias = jdbcTemplate.queryForInt(sb.toString());
			
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:horaLimmite");
		}
	
		if(coincidencias > 0){
			claveControl = obtenerCveControl(concepto);
		}
		
		return claveControl;
	}


	private String obtenerCveControl(String concepto){
		StringBuffer sb = new StringBuffer();
		List<String> listaClaves = new ArrayList<String>();
		
		sb.append("SELECT MAX(CVE_CONTROL) AS CVE_CONTROL FROM SELECCION_AUTOMATICA_GRUPO \n");
		sb.append("WHERE CONCEPTO = '"+ Utilerias.validarCadenaSQL(concepto) +"'  \n");
		sb.append(" and (usuario_uno is null or usuario_uno  like ' ')");
		
		try {
			listaClaves = jdbcTemplate.query(sb.toString(), new RowMapper<String>() {
				public String mapRow(ResultSet rs, int idx) throws SQLException{
				return rs.getString("CVE_CONTROL");
			}});
			
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:obtenerCveControl");
		}
		
		return listaClaves.get(0);
	}
	
	@Override
	public int agregarPropuestaPago(String claveControl, double total, String concepto){
		StringBuffer sb = new StringBuffer();
		int res = 0;
		
		sb.append("UPDATE SELECCION_AUTOMATICA_GRUPO SET \n");
		sb.append("MONTO_MAXIMO = (MONTO_MAXIMO + "+ total +"), \n");
		sb.append("CUPO_AUTOMATICO = (CUPO_AUTOMATICO + "+total+"), \n");
		sb.append("CUPO_TOTAL = (CUPO_TOTAL + "+ total  +")  \n");
		sb.append("WHERE CVE_CONTROL = '"+claveControl+"' \n");
		sb.append("AND CONCEPTO = '"+concepto+"' \n");
		sb.append(" and (usuario_uno is null or usuario_uno  like ' ')");
		//sb.append(" and rownum=1");
		
		System.out.println(sb.toString());
		try {
			
			res = jdbcTemplate.update(sb.toString());
			
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:agregarPropuestaPago");
		}

		
		return res;
	}
	
	
	@Override
	public List<LlenaComboGralDto> llenarComboReglaNegocio(LlenaComboGralDto dto) {
		
		dto.setCondicion("tipo_regla = '"+dto.getCondicion()+"' AND GEN_PROP_AUTO = 'N'");
		
		List<LlenaComboGralDto> list = new ArrayList<>();
		try{
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			list = consultasGenerales.llenarComboGral(dto);
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:llenarComboReglaNegocio");
		}
		return list;
	}

	@Override
	public int obtenerGrupoFlujo(int sociedad) {
		int idGrupoFlujo = 0;
		StringBuffer sb = new StringBuffer();
		
		try{
			sb.append("SELECT ID_GRUPO_FLUJO FROM GRUPO_EMPRESA WHERE NO_EMPRESA = " + sociedad);
			idGrupoFlujo = jdbcTemplate.queryForInt(sb.toString());
		}catch(Exception e){
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:RentasDaoImpl, M:obtenerGrupoFlujo");
		}
		
		return idGrupoFlujo;
	}
	
	@Override
	public int obtenerDiaInhabil(String fecha){
		int contadorFechas = 0;
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT count(*) FROM dia_inhabil WHERE fec_inhabil = convert(datetime, '"+fecha+"', 103) \n");
		try {
			contadorFechas = jdbcTemplate.queryForInt(sb.toString());
		
		} catch (CannotGetJdbcConnectionException e){
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:RentasDaoImpl, M:obtenerDiaInhabil");
			
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:RentasDaoImpl, M:obtenerDiaInhabil");
		}
		return contadorFechas;
	}

	
	public int hayFoliosRegla(){
		int contador = 0;
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT count(no_folio_det) FROM TMP_FOLIOS_BUSCA_MOV_REG WHERE ID_USUARIO = "+GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+" \n");
		try {
			contador = jdbcTemplate.queryForInt(sb.toString());
		
		} catch (CannotGetJdbcConnectionException e){
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:RentasDaoImpl, M:obtenerDiaInhabil");
			
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:RentasDaoImpl, M:obtenerDiaInhabil");
		}
		return contador;
	}
	
	/*
	 * Agregado por Yoseline E.C.
	 * 24 DE FEBRERO DEL 2015
	 */
	
	public boolean estaDisponible(String concepto, String claveControl,String fechaPropuesta){
		boolean resultado=false;
		StringBuffer sb= new StringBuffer();
		int contador=0;
		try{
			sb.append("select count(id_grupo) as c");
			sb.append("\n from seleccion_automatica_grupo");
			sb.append("\n where concepto = '"+concepto+"'");
			sb.append("\n and cve_control= '"+claveControl+"'");
			sb.append("\n and usuario_uno is null");
			
			System.out.println(sb.toString());
			
			contador=jdbcTemplate.queryForInt(sb.toString());
			
			if(contador>=0)
				return true;
			
		}catch (CannotGetJdbcConnectionException e){
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPendientesDaoImpl, M:estaAutorizado");
			
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPendientesDaoImpl, M:estaAutorizado");
		}return resultado;
		
	}
	
	
	public Map<String, String> pagosProgramados(String fecha, String usuario){
		StringBuffer sb = new StringBuffer();
		Map<String, String> resultado = new HashMap<>();
		List<Map<String, String>> listaResultado = new ArrayList<Map<String,String>>();
		
		try {
			sb.append("select sum(mn) as pesos , sum(dls) as dolares from (");
			sb.append("\n	SELECT sum(cupo_automatico) as mn , 0 as dls ");
			sb.append("\n	FROM seleccion_automatica_grupo");
			//sb.append("\n	WHERE fecha_propuesta = '17/12/2015'");
			sb.append("\n	WHERE fecha_propuesta = '"+fecha+"'");
			//sb.append("\n	AND CONCEPTO LIKE '%ADMIN%'");
			sb.append("\n	AND CONCEPTO LIKE '%"+usuario+"%'");
			sb.append("\n	AND CONCEPTO LIKE '%MN%'");
			sb.append("\n	union all");
			sb.append("\n	SELECT 0 as mn , sum(cupo_automatico) as dls ");
			sb.append("\n	FROM seleccion_automatica_grupo");
			//sb.append("\n	WHERE fecha_propuesta = '17/12/2015'");
			sb.append("\n	WHERE fecha_propuesta = '"+fecha+"'");
			//sb.append("\n	AND CONCEPTO LIKE '%ADMIN%'");
			sb.append("\n	AND CONCEPTO LIKE '%"+usuario+"%'");
			sb.append("\n	AND CONCEPTO LIKE '%DLS%'");
			sb.append(") importe");
			System.out.println(""+sb.toString());
			listaResultado = jdbcTemplate.query(sb.toString(),new RowMapper<Map<String, String>>() {
				public Map<String, String> mapRow(ResultSet rs, int idx)throws SQLException {
					Map<String, String> map = new HashMap<String, String>();
					map.put("pesos", rs.getString("pesos"));
					map.put("dolares", rs.getString("dolares"));
					return map;
	        	}
			});
			
			if(listaResultado.size()!=0)
				resultado=listaResultado.get(0);
		}catch (CannotGetJdbcConnectionException e){
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPendientesDaoImpl, M:pagosProgramados");
			
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPendientesDaoImpl, M:pagosProgramados");
		}return resultado;
	}
	
	public List<LlenaComboGralDto> llenarComboGrupoFlujo(GrupoEmpresasDto dto){
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		try{
			consultasGenerales= new ConsultasGenerales(jdbcTemplate);
			return consultasGenerales.llenarComboGrupoEmpresa(dto);
		}
		catch(CannotGetJdbcConnectionException e){
			e.getStackTrace();
			LlenaComboGralDto cons = new LlenaComboGralDto();
			cons.setCampoUno("Error de conexion");
			listDatos.add(cons);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PagosPendientesDaoImpl, M:llenarComboGrupoFlujo");
		}
		catch(Exception e){
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PagosPendientesDaoImpl, M:llenarComboGrupoFlujo");
		}return listDatos;
	}
	
	
	public List<PagosPendientesDto> obtenerTodosPagosPendientesPorEmpresa(int numeroEmpresa, String acredor, PlantillaDto plantilla, String  tipo){
		StringBuffer sb = new StringBuffer();
		List<PagosPendientesDto> pagosPendietnes = new ArrayList<PagosPendientesDto>();
		String renglonesRegla = "";
		//int cont = hayFoliosRegla();
		
		if(plantilla.getIdReglaNegocio() > 0){
			renglonesRegla = obtenerRenglonesConRegla(plantilla.getIdReglaNegocio());
			System.out.println(renglonesRegla);
		}
		 
		sb.append(" SELECT EQUIVALE_PERSONA,NO_FOLIO_DET,F.DESC_FORMA_PAGO, NO_FACTURA, NO_DOCTO ,IMPORTE, CONCEPTO, ID_DIVISA, \n");
		sb.append(" CASE WHEN(FEC_OPERACION < (SELECT CONVERT(DATETIME, GETDATE(), 103))) THEN 'VENCIDO'  \n");
		sb.append(" WHEN(FEC_OPERACION >= (SELECT CONVERT(DATETIME, GETDATE(), 103))) THEN 'POR VENCER' END AS ESTATUS, \n");
		sb.append(" FEC_VALOR FROM MOVIMIENTO m  JOIN PERSONA P ON m.NO_CLIENTE = p.NO_PERSONA JOIN CAT_FORMA_PAGO f ON m.ID_FORMA_PAGO = f.ID_FORMA_PAGO\n");
		sb.append(" JOIN CAT_FORMA_PAGO f ON m.ID_FORMA_PAGO = f.ID_FORMA_PAGO  \n");
		sb.append(" WHERE m.NO_EMPRESA = "+Utilerias.validarCadenaSQL(numeroEmpresa)+" AND P.ID_TIPO_PERSONA = 'P' AND EQUIVALE_PERSONA IN ('"+Utilerias.validarCadenaSQL(acredor)+"') AND ID_TIPO_OPERACION \n");
		sb.append(" IN (3000, 3801) AND ID_ESTATUS_MOV IN ('N','C','L') \n");
		sb.append(" AND (CVE_CONTROL IS NULL OR CVE_CONTROL = '') AND ID_TIPO_MOVTO='E' \n");
		if(tipo.equals("VENCIDO"))
			sb.append(" AND FEC_OPERACION < (SELECT CONVERT(DATETIME, GETDATE(), 103)) \n");
		if(tipo.equals("POR VENCER"))
			sb.append(" AND FEC_OPERACION >= (SELECT CONVERT(DATETIME, GETDATE(), 103)) \n");
		
		if(!plantilla.getDivisa().equals("")){
			sb.append(" AND ID_DIVISA = '" + Utilerias.validarCadenaSQL(plantilla.getDivisa()) + "' \n");
		}
		/*if(!plantilla.getNumeroDocumento().equals("")){
			sb.append(" AND NO_DOCTO = '"+ Utilerias.validarCadenaSQL(plantilla.getNumeroDocumento()) +"' \n");
		}*/
		if(!plantilla.getNumDocA().equals("")){
			//sb.append(" AND NO_DOCTO = '"+ Utilerias.validarCadenaSQL(plantilla.getNumeroDocumento()) +"' \n");
			if(!plantilla.getNumDocB().equals(""))
				sb.append(" AND CONVERT(INT, NO_DOCTO) BETWEEN '"+ Utilerias.validarCadenaSQL(plantilla.getNumDocA()) +"'  AND '"+ Utilerias.validarCadenaSQL(plantilla.getNumDocB())  +"' \n");
			else
				sb.append(" AND CONVERT(INT, NO_DOCTO) = '"+ Utilerias.validarCadenaSQL(plantilla.getNumDocA()) +"' \n");
			
		}
		
		/*if(!plantilla.getEquivalePersonaA().equals("")){
			if(!plantilla.getEquivalePersonaB().equals(""))
				sb.append(" AND EQUIVALE_PERSONA BETWEEN '"+ Utilerias.validarCadenaSQL(plantilla.getEquivalePersonaA()) +"'  AND '"+ Utilerias.validarCadenaSQL(plantilla.getEquivalePersonaB())  +"' \n");
			else
				sb.append(" AND EQUIVALE_PERSONA = '"+ Utilerias.validarCadenaSQL(plantilla.getEquivalePersonaA()) +"' \n");
			
		}*/
		
		if(!plantilla.getTipoDocumento().equals("")){
			sb.append(" AND ID_CONTABLE ='" +Utilerias.validarCadenaSQL(plantilla.getTipoDocumento())+ "' \n");
		}
		if(!plantilla.getIndicadores().equals("")){
			sb.append(" AND B_GEN_CONTA ='" +Utilerias.validarCadenaSQL(plantilla.getIndicadores())+ "' \n");
		}
		if(!plantilla.getFechaContableInicio().equals("") && !plantilla.getFechaContableInicio().equals("/undefined/")  
				&& !plantilla.getFechaContableFin().equals("") && !plantilla.getFechaContableFin().equals("/undefined/")){
			sb.append("AND FECHA_CONTABILIZACION BETWEEN '"+ plantilla.getFechaContableInicio() +"' AND '"+ plantilla.getFechaContableFin() +"' \n");
		}
		
		if(!plantilla.getFechaPropuestaPagoInicio().equals("") && !plantilla.getFechaPropuestaPagoInicio().equals("/undefined/") 
				&& !plantilla.getFechaPropuestaPagoFin().equals("") && !plantilla.getFechaPropuestaPagoFin().equals("/undefined/")){
			sb.append("AND FEC_VALOR BETWEEN '"+ Utilerias.validarCadenaSQL(plantilla.getFechaPropuestaPagoInicio()) +"' AND '"+ Utilerias.validarCadenaSQL(plantilla.getFechaPropuestaPagoFin()) +"' \n");
		}
		
		/*if(cont>0){
			sb.append("AND NO_FOLIO_DET IN (SELECT NO_FOLIO_DET FROM TMP_FOLIOS_BUSCA_MOV_REG ) \n");
		}*/
		
	
		if(!renglonesRegla.equals("")){
			//sb.append("AND NO_FOLIO_DET IN "+renglonesRegla+" \n");
			sb.append("AND NO_FOLIO_DET IN (SELECT NO_FOLIO_DET FROM TMP_FOLIOS_BUSCA_MOV_REG WHERE ID_USUARIO = "+GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+" ) \n");
		}
		sb.append("order by EQUIVALE_PERSONA,NO_DOCTO");
		
		System.out.println("----------------obtenerPagosPendientesporEmpresa------ \n");
		System.out.println("----------------Nivel 4------------------------- \n");
		System.out.println(sb.toString());
		System.out.println("\n ----------------Nivel 4------------------------ ");
		System.out.println("-------------------Fin nivel 4----------------------- \n");
		
		try {
			
			pagosPendietnes = jdbcTemplate.query(sb.toString(), new RowMapper<PagosPendientesDto>() {
				public PagosPendientesDto mapRow(ResultSet rs, int idx) throws SQLException{
					SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd/MM/yyyy");
					PagosPendientesDto pagosPendienteDto = new PagosPendientesDto();
					
					pagosPendienteDto.setTexto(rs.getString("CONCEPTO"));
					pagosPendienteDto.setImporteNeto(rs.getDouble("IMPORTE"));
					pagosPendienteDto.setFactura(rs.getString("NO_FOLIO_DET"));
					pagosPendienteDto.setCveControl(rs.getString("NO_FACTURA"));
					pagosPendienteDto.setNumeroDcoumento(rs.getString("NO_DOCTO"));
					pagosPendienteDto.setDivisa(rs.getString("ID_DIVISA"));
					pagosPendienteDto.setEstatus(rs.getString("ESTATUS"));
					pagosPendienteDto.setEquivalePersona(rs.getString("EQUIVALE_PERSONA"));
					pagosPendienteDto.setFechaPropuesta(formatoDelTexto.format(rs.getDate("FEC_VALOR")));
					pagosPendienteDto.setIndex(idx);
					pagosPendienteDto.setFormaPago(rs.getString("DESC_FORMA_PAGO"));
				return pagosPendienteDto;
			}});
			
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Egresos, C:PagosPedientesDaoImpl, M:obtenerPagosPendientesNivTres");
		}
		
		Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		return pagosPendietnes;
		
	}
	/****************************Getters and Setters **********************/
	
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
	public List<Map<String, String>> obtenerPagosPendientesNivTresJson(int numeroEmpresa, String acreedor,
			PlantillaDto plantilla, List<Map<String, String>> rangosDocumentos,
			List<Map<String, String>> rangosProveedores, int marca, boolean documentos) {
		StringBuffer sb = new StringBuffer();
		List<Map<String, String>> pagosPendietnes = new ArrayList<Map<String, String>>();
		String renglonesRegla = "";
		//int cont = hayFoliosRegla();
		
		if(plantilla.getIdReglaNegocio() > 0){
			renglonesRegla = obtenerRenglonesConRegla(plantilla.getIdReglaNegocio());
			System.out.println(renglonesRegla);
		}
		
		sb.append(" SELECT ID_CONTABLE as CLASEDOCUMENTO,NO_FOLIO_DET, NO_FACTURA, NO_DOCTO ,IMPORTE, CONCEPTO, ID_DIVISA,DESC_FORMA_PAGO, \n");
		sb.append(" CASE WHEN(FEC_OPERACION < (SELECT CONVERT(DATETIME, GETDATE(), 103))) THEN 'VENCIDO'  \n");
		sb.append(" WHEN(FEC_OPERACION >= (SELECT CONVERT(DATETIME, GETDATE(), 103))) THEN 'POR VENCER' END AS ESTATUS, \n");
		sb.append(" FEC_VALOR FROM MOVIMIENTO m  JOIN PERSONA P ON m.NO_CLIENTE = p.NO_PERSONA JOIN CAT_FORMA_PAGO f ON m.ID_FORMA_PAGO = f.ID_FORMA_PAGO \n");
		sb.append(" WHERE m.NO_EMPRESA = "+Utilerias.validarCadenaSQL(numeroEmpresa)+" "
				+ " AND EQUIVALE_PERSONA IN ('"+Utilerias.validarCadenaSQL(acreedor)+"')\n");
	
		if(plantilla.getTipoBusqueda().equals("P"))
		{
			sb.append("\n  AND P.ID_TIPO_PERSONA = 'P' ");	
			sb.append("\n AND ID_TIPO_OPERACION = 3000 " ); 
		    sb.append("\n AND ID_ESTATUS_MOV IN ('N','C') ");
			sb.append("\n AND (CVE_CONTROL IS NULL OR CVE_CONTROL = '') ");
		}
		else{
			sb.append("\n AND P.ID_TIPO_PERSONA = 'E' ");	
			sb.append("\n AND ID_TIPO_MOVTO='E' ");
			sb.append("\n AND ID_TIPO_OPERACION = 3801 ");
			sb.append("\n AND ID_ESTATUS_MOV = 'L' ");
			sb.append("\n AND (CVE_CONTROL IS NULL OR CVE_CONTROL = '')");
			sb.append("\n AND (ORIGEN_MOV = '' OR ORIGEN_MOV IS NULL) ");
		}
		
		if(!plantilla.getDivisa().equals("")){
			sb.append("\n AND ID_DIVISA = '" + Utilerias.validarCadenaSQL(plantilla.getDivisa()) + "' \n");
		}
		if((!plantilla.getNumDocA().equals("") || !plantilla.getNumDocB().equals("")) && rangosDocumentos.size()<=0){
			if(!plantilla.getNumDocB().equals(""))
				sb.append(" AND CONVERT(INT, NO_DOCTO) = "+ Utilerias.validarCadenaSQL(plantilla.getNumDocB()) +" \n");	
			else
				sb.append(" AND CONVERT(INT, NO_DOCTO) = "+ Utilerias.validarCadenaSQL(plantilla.getNumDocA()) +" \n");	
		}else if(rangosDocumentos.size()>0){
			sb.append(" AND (");
			if(rangosDocumentos.size()==1){
				sb.append("CONVERT(INT, NO_DOCTO) BETWEEN "+ rangosDocumentos.get(0).get("de") +"  AND "+ rangosDocumentos.get(0).get("a")  +" \n");
			}else{
				for (int i = 0; i < rangosDocumentos.size(); i++) {
					sb.append(" CONVERT(INT, NO_DOCTO) BETWEEN "+ rangosDocumentos.get(i).get("de") +"  AND "+ rangosDocumentos.get(i).get("a")  +" \n");
					if((i+1)< rangosDocumentos.size()){
						sb.append("OR");
					}
				}
			}
			if (documentos) {
				sb.append("OR CONVERT(INT, NO_DOCTO) IN (");
				sb.append("SELECT CONVERT(INT, REFERENCIA) FROM RENTASTMP WHERE OBSERVACION = '");
				sb.append(GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
				sb.append("' AND FORMA_PAGO = 'D'");
				sb.append("))");
			} else {
				sb.append(")");
			}
		} else if (documentos) {
			sb.append(" AND (");
			sb.append("CONVERT(INT, NO_DOCTO) IN (");
			sb.append("SELECT CONVERT(INT, REFERENCIA) FROM RENTASTMP WHERE OBSERVACION = '");
			sb.append(GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario());
			sb.append("' AND FORMA_PAGO = 'D'");
			sb.append("))");
		}
		
		if(!plantilla.getTipoDocumento().equals("")){
			sb.append(" AND ID_CONTABLE ='" +Utilerias.validarCadenaSQL(plantilla.getTipoDocumento())+ "' \n");
		}
		if(!plantilla.getIndicadores().equals("")){
			sb.append(" AND B_GEN_CONTA ='" +Utilerias.validarCadenaSQL(plantilla.getIndicadores())+ "' \n");
		}
		if(!plantilla.getFechaContableInicio().equals("") && !plantilla.getFechaContableInicio().equals("/undefined/")  
				&& !plantilla.getFechaContableFin().equals("") && !plantilla.getFechaContableFin().equals("/undefined/")){
			sb.append("AND FECHA_CONTABILIZACION BETWEEN '"+ plantilla.getFechaContableInicio() +"' AND '"+ plantilla.getFechaContableFin() +"' \n");
		}
		
		if(!plantilla.getFechaPropuestaPagoInicio().equals("") && !plantilla.getFechaPropuestaPagoInicio().equals("/undefined/") 
				&& !plantilla.getFechaPropuestaPagoFin().equals("") && !plantilla.getFechaPropuestaPagoFin().equals("/undefined/")){
			sb.append("AND FEC_VALOR BETWEEN '"+ Utilerias.validarCadenaSQL(plantilla.getFechaPropuestaPagoInicio()) +"' AND '"+ Utilerias.validarCadenaSQL(plantilla.getFechaPropuestaPagoFin()) +"' \n");
		}
		
		if(!renglonesRegla.equals("")){
			//sb.append("AND NO_FOLIO_DET IN "+renglonesRegla+" \n");
			sb.append("AND NO_FOLIO_DET IN (SELECT NO_FOLIO_DET FROM TMP_FOLIOS_BUSCA_MOV_REG WHERE ID_USUARIO = "+GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+" ) \n");
		}
		
		sb.append("order by NO_DOCTO");
		
		System.out.println("----------------obtenerPagosPendientesNivTres------ \n");
		System.out.println("----------------Nivel tres------------------------- \n");
		System.out.println(sb.toString());
		System.out.println("\n ----------------Nivel tres------------------------ ");
		System.out.println("-------------------Fin nivel 3----------------------- \n");
		
		try {
			
			pagosPendietnes = jdbcTemplate.query(sb.toString(), 
					new PagosPendientesNivTresMarcado(marca));
			
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Egresos, C:PagosPedientesDaoImpl, M:obtenerPagosPendientesNivTres");
		}
		
		return pagosPendietnes;
	}

	private class PagosPendientesNivTresMarcado 
				implements RowMapper<Map<String, String>> {
		
		private int marca;
		
		public PagosPendientesNivTresMarcado(int marca) {
			this.marca = marca;
		}

		@Override
		public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException{
			SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd/MM/yyyy");
			Map<String, String> pagosPendienteDto = new HashMap<String, String>();
			
			pagosPendienteDto.put("texto", rs.getString("CONCEPTO"));
			pagosPendienteDto.put("importeNeto", rs.getString("IMPORTE"));
			pagosPendienteDto.put("factura", rs.getString("NO_FOLIO_DET"));
			pagosPendienteDto.put("cveControl", rs.getString("NO_FACTURA"));
			pagosPendienteDto.put("numeroDcoumento", rs.getString("NO_DOCTO"));
			pagosPendienteDto.put("divisa", rs.getString("ID_DIVISA"));
			pagosPendienteDto.put("estatus", rs.getString("ESTATUS"));
			pagosPendienteDto.put("claseDocumento", rs.getString("CLASEDOCUMENTO"));
			pagosPendienteDto.put("fechaPropuesta", formatoDelTexto.format(rs.getDate("FEC_VALOR")));
			pagosPendienteDto.put("index", String.valueOf(idx));
			pagosPendienteDto.put("formaPago", rs.getString("DESC_FORMA_PAGO"));
			pagosPendienteDto.put("chequera", "");
			pagosPendienteDto.put("idBanco", "0");
			pagosPendienteDto.put("referencaBanc", "");
			pagosPendienteDto.put("idBancoP", "0");
			pagosPendienteDto.put("chequeraP", "");
			
			switch (marca) {
			//Sin marca
			case 0:
				pagosPendienteDto.put("seleccionado", "");
				break;
				
			//vencido
			case 1:
				pagosPendienteDto.put("seleccionado", "");
				
				if (pagosPendienteDto.get("estatus").equals("VENCIDO")) {
					pagosPendienteDto.put("seleccionado", "X");
				}
				break;
				
			//por vencer
			case 2:
				pagosPendienteDto.put("seleccionado", "");
				
				if (pagosPendienteDto.get("estatus").equals("POR VENCER")) {
					pagosPendienteDto.put("seleccionado", "X");
				}
				break;

			default:
				break;
			}
			
			
			
			return pagosPendienteDto;
		}
		
	}

	
	@Override
	public int actualizaImporte(String claveControl) {
		int res = -1;
		try {
			
			StringBuffer sb = new StringBuffer();
			
			sb.append("UPDATE SELECCION_AUTOMATICA_GRUPO SET \n");
			sb.append("MONTO_MAXIMO = (SELECT SUM(IMPORTE) FROM MOVIMIENTO WHERE CVE_CONTROL = '"+claveControl+"' AND ID_TIPO_OPERACION IN('3000','3801') AND ID_ESTATUS_MOV IN('C','N','L') AND ID_TIPO_MOVTO='E'), \n");
			sb.append("CUPO_AUTOMATICO = (SELECT SUM(IMPORTE) FROM MOVIMIENTO WHERE CVE_CONTROL = '"+claveControl+"' AND ID_TIPO_OPERACION IN('3000','3801') AND ID_ESTATUS_MOV IN('C','N','L') AND ID_TIPO_MOVTO='E'), \n");
			sb.append("CUPO_TOTAL = (SELECT SUM(IMPORTE) FROM MOVIMIENTO WHERE CVE_CONTROL = '"+claveControl+"' AND ID_TIPO_OPERACION IN('3000','3801') AND ID_ESTATUS_MOV IN('C','N','L') AND ID_TIPO_MOVTO='E') \n");
			sb.append("WHERE CVE_CONTROL = '");
			sb.append(claveControl);
			sb.append("'");
			System.out.println("actualiza importe "+sb.toString());
			res = jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:actualizaImporte");
		} return res;
	}


	@Override
	public void limpiarIndividuales(int noUsuario, String tipo) {
		int res = -1;
		try {
			
			StringBuffer sb = new StringBuffer();
			
			sb.append("DELETE FROM RENTASTMP WHERE OBSERVACION = '");
			sb.append(noUsuario);
			sb.append("' AND FORMA_PAGO = '");
			sb.append(tipo);
			sb.append("'");
			
			res = jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:limpiarIndividuales");
		} System.out.println(res);
		
	}


	@Override
	public void insertaIndividual(Map<String, String> datosExcel, int noUsuario, String tipo) {
		int res = -1;
		try {
			
			StringBuffer sb = new StringBuffer();
			
			sb.append("INSERT INTO RENTASTMP ");
			sb.append("(OBSERVACION, FORMA_PAGO, NO_USUARIO, REFERENCIA)");
			sb.append("VALUES ('");
			sb.append(noUsuario + "', '");
			sb.append(tipo + "', '-1', '");
			sb.append(datosExcel.get("de") + "')");
			
			res = jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:insertaIndividual");
		} System.out.println(res);
		
	}
	
	@Override
	public int ejecutarCambioDatosMovimiento(String fechaPago, String claves, int idBancoBenef, String idChequeraBenef, int idBancoPag, String idChequeraPag, String refBancaria, String valida, String tipoBusqueda) {
		int res=-1;
		StringBuffer sb = new StringBuffer();
		try {
			if(tipoBusqueda.equals("I")){
					sb.append(" UPDATE movimiento \n");
					if(idChequeraBenef != null && !idChequeraBenef.equals("") && idBancoBenef!=0){
						sb.append("SET ID_CHEQUERA_BENEF = '" + Utilerias.validarCadenaSQL(idChequeraBenef) + "'\n");
						sb.append(" ,ID_BANCO_BENEF = " + Utilerias.validarCadenaSQL(idBancoBenef) + "\n");
		
					}else{
						sb.append("SET ID_CHEQUERA_BENEF = ID_CHEQUERA_BENEF \n");
						sb.append(" ,ID_BANCO_BENEF = ID_BANCO_BENEF \n");
					}
						
					if(idChequeraPag != null && !idChequeraPag.equals("") && idBancoPag!=0){
						sb.append(" ,ID_CHEQUERA = '" + Utilerias.validarCadenaSQL(idChequeraPag) + "'\n");
						sb.append(" ,ID_BANCO =  " + Utilerias.validarCadenaSQL(idBancoPag) +"\n");
					}else{
						sb.append(" ,ID_CHEQUERA = ID_CHEQUERA \n");
						sb.append(" ,ID_BANCO = ID_BANCO \n");
					}
					
					//if (refBancaria != null && !refBancaria.equals("")) 
						//sb.append(" ,REFERENCIA = '" + Utilerias.validarCadenaSQL(refBancaria) + "'\n");
					
					if (fechaPago!= null && !fechaPago.equals("")) 
						sb.append(" ,fec_valor = convert(datetime, '" + fechaPago + "', 103) \n");
						
					sb.append(" WHERE (no_folio_det in ("+claves+") OR folio_ref in ("+claves+")) \n");
					sb.append(" AND id_tipo_operacion in (3000, 3001,3801) AND id_tipo_movto='E' \n");
					System.out.println(sb.toString());
					res = jdbcTemplate.update(sb.toString());
					sb.delete(0, sb.length());
					
					sb.append(" UPDATE movimiento \n");
					if(idChequeraPag != null && !idChequeraPag.equals("") && idBancoPag!=0){
						sb.append("SET ID_CHEQUERA_BENEF = '" + Utilerias.validarCadenaSQL(idChequeraPag) + "'\n");
						sb.append(" ,ID_BANCO_BENEF = " + Utilerias.validarCadenaSQL(idBancoPag) + "\n");
						
					}else{
						sb.append("SET ID_CHEQUERA_BENEF = ID_CHEQUERA_BENEF \n");
						sb.append(" ,ID_BANCO_BENEF = ID_BANCO_BENEF \n");
					}
						
					if(idChequeraBenef != null && !idChequeraBenef.equals("") && idBancoBenef!=0){
						sb.append(" ,ID_CHEQUERA = '" + Utilerias.validarCadenaSQL(idChequeraBenef) + "'\n");
						sb.append(" ,ID_BANCO =  " + Utilerias.validarCadenaSQL(idBancoBenef) +"\n");
					}else{
						sb.append(" ,ID_CHEQUERA = ID_CHEQUERA \n");
						sb.append(" ,ID_BANCO = ID_BANCO \n");
					}
					
					//if (refBancaria != null && !refBancaria.equals("")) 
					//	sb.append(" ,REFERENCIA = '" + Utilerias.validarCadenaSQL(refBancaria) + "'\n");
					
					if (fechaPago != null && !fechaPago.equals("")) 
						sb.append(" ,fec_valor = convert(datetime, '" + fechaPago + "', 103) \n");
						
					sb.append(" WHERE (no_folio_det in ("+claves+") OR folio_ref in ("+claves+")) \n");
					sb.append(" AND id_tipo_operacion in (3000, 3001,3801) AND id_tipo_movto='I' \n");
					System.out.println(sb.toString());
					res = jdbcTemplate.update(sb.toString());
				
				}else{
					sb.append(" UPDATE movimiento \n");
					if(idChequeraBenef != null && !idChequeraBenef.equals("") && idBancoBenef!=0){
						sb.append("SET ID_CHEQUERA_BENEF = '" + Utilerias.validarCadenaSQL(idChequeraBenef) + "'\n");
						sb.append(" ,ID_BANCO_BENEF = " + Utilerias.validarCadenaSQL(idBancoBenef) + "\n");
						
					}else{
						sb.append("SET ID_CHEQUERA_BENEF = ID_CHEQUERA_BENEF \n");
						sb.append(" ,ID_BANCO_BENEF = ID_BANCO_BENEF \n");
					}
						
					if(idChequeraPag != null && !idChequeraPag.equals("") && idBancoPag!=0){
						sb.append(" ,ID_CHEQUERA = '" + Utilerias.validarCadenaSQL(idChequeraPag) + "'\n");
						sb.append(" ,ID_BANCO =  " + Utilerias.validarCadenaSQL(idBancoPag) +"\n");
					}else{
						sb.append(" ,ID_CHEQUERA = ID_CHEQUERA \n");
						sb.append(" ,ID_BANCO = ID_BANCO \n");
					}
					
					if (refBancaria != null && !refBancaria.equals("")) 
						sb.append(" ,"+valida+" = '" + Utilerias.validarCadenaSQL(refBancaria) + "'\n");
					
					if (fechaPago != null && !fechaPago.equals("")) 
						sb.append(" ,fec_valor = convert(datetime, '" + fechaPago + "', 103) \n");
						
					sb.append(" WHERE (no_folio_det in ("+claves+") OR folio_ref in ("+claves+")) \n");
					sb.append(" AND id_tipo_operacion in (3000, 3001,3801)\n");
					System.out.println(sb.toString());
					res = jdbcTemplate.update(sb.toString());
				}



		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:ejecutarCambioFechaPago");
		}
		return res;
	}

	@Override
	public int asignarChequeraPagadora(int idBancoPag, String idChequeraPag) {
		int res=-1;
		StringBuffer sb = new StringBuffer();
		try {
//			if(tipoBusqueda.equals("I")){
//					sb.append(" UPDATE movimiento \n");
//					if(idChequeraBenef != null && !idChequeraBenef.equals("") && idBancoBenef!=0){
//						sb.append("SET ID_CHEQUERA_BENEF = '" + Utilerias.validarCadenaSQL(idChequeraBenef) + "'\n");
//						sb.append(" ,ID_BANCO_BENEF = " + Utilerias.validarCadenaSQL(idBancoBenef) + "\n");
//		
//					}else{
//						sb.append("SET ID_CHEQUERA_BENEF = ID_CHEQUERA_BENEF \n");
//						sb.append(" ,ID_BANCO_BENEF = ID_BANCO_BENEF \n");
//					}
//						
//					if(idChequeraPag != null && !idChequeraPag.equals("") && idBancoPag!=0){
//						sb.append(" ,ID_CHEQUERA = '" + Utilerias.validarCadenaSQL(idChequeraPag) + "'\n");
//						sb.append(" ,ID_BANCO =  " + Utilerias.validarCadenaSQL(idBancoPag) +"\n");
//					}else{
//						sb.append(" ,ID_CHEQUERA = ID_CHEQUERA \n");
//						sb.append(" ,ID_BANCO = ID_BANCO \n");
//					}
				
					
//					if (fechaPago!= null && !fechaPago.equals("")) 
//						sb.append(" ,fec_valor = convert(datetime, '" + fechaPago + "', 103) \n");
//						
//					sb.append(" WHERE (no_folio_det in ("+claves+") OR folio_ref in ("+claves+")) \n");
//					sb.append(" AND id_tipo_operacion in (3000, 3001,3801) AND id_tipo_movto='E' \n");
//					System.out.println(sb.toString());
//					res = jdbcTemplate.update(sb.toString());
//					sb.delete(0, sb.length());
//					
//					sb.append(" UPDATE movimiento \n");
//					if(idChequeraPag != null && !idChequeraPag.equals("") && idBancoPag!=0){
//						sb.append("SET ID_CHEQUERA_BENEF = '" + Utilerias.validarCadenaSQL(idChequeraPag) + "'\n");
//						sb.append(" ,ID_BANCO_BENEF = " + Utilerias.validarCadenaSQL(idBancoPag) + "\n");
//						
//					}else{
//						sb.append("SET ID_CHEQUERA_BENEF = ID_CHEQUERA_BENEF \n");
//						sb.append(" ,ID_BANCO_BENEF = ID_BANCO_BENEF \n");
//					}
//						
//					if(idChequeraBenef != null && !idChequeraBenef.equals("") && idBancoBenef!=0){
//						sb.append(" ,ID_CHEQUERA = '" + Utilerias.validarCadenaSQL(idChequeraBenef) + "'\n");
//						sb.append(" ,ID_BANCO =  " + Utilerias.validarCadenaSQL(idBancoBenef) +"\n");
//					}else{
//						sb.append(" ,ID_CHEQUERA = ID_CHEQUERA \n");
//						sb.append(" ,ID_BANCO = ID_BANCO \n");
//					}
//					
//					
//					if (fechaPago != null && !fechaPago.equals("")) 
//						sb.append(" ,fec_valor = convert(datetime, '" + fechaPago + "', 103) \n");
//						
//					sb.append(" WHERE (no_folio_det in ("+claves+") OR folio_ref in ("+claves+")) \n");
//					sb.append(" AND id_tipo_operacion in (3000, 3001,3801) AND id_tipo_movto='I' \n");
//					System.out.println(sb.toString());
//					res = jdbcTemplate.update(sb.toString());
//				
//				}else{
					sb.append(" UPDATE movimiento \n");
					//if(idChequeraBenef != null && !idChequeraBenef.equals("") && idBancoBenef!=0){
//						sb.append("SET ID_CHEQUERA_BENEF = '" + Utilerias.validarCadenaSQL(idChequeraBenef) + "'\n");
//						sb.append(" ,ID_BANCO_BENEF = " + Utilerias.validarCadenaSQL(idBancoBenef) + "\n");
//						
//					}else{
//						sb.append("SET ID_CHEQUERA_BENEF = ID_CHEQUERA_BENEF \n");
//						sb.append(" ,ID_BANCO_BENEF = ID_BANCO_BENEF \n");
//					}
						
				//	if(idChequeraPag != null && !idChequeraPag.equals("") && idBancoPag!=0){
						sb.append(" ,ID_CHEQUERA = '" + Utilerias.validarCadenaSQL(idChequeraPag) + "'\n");
						sb.append(" ,ID_BANCO =  " + Utilerias.validarCadenaSQL(idBancoPag) +"\n");
//					}else{
//						sb.append(" ,ID_CHEQUERA = ID_CHEQUERA \n");
//						sb.append(" ,ID_BANCO = ID_BANCO \n");
//					}
//					
//					if (refBancaria != null && !refBancaria.equals("")) 
//						sb.append(" ,"+valida+" = '" + Utilerias.validarCadenaSQL(refBancaria) + "'\n");
//					
//					if (fechaPago != null && !fechaPago.equals("")) 
//						sb.append(" ,fec_valor = convert(datetime, '" + fechaPago + "', 103) \n");
//						
			//		sb.append(" WHERE (no_folio_det in ("+claves+") OR folio_ref in ("+claves+")) \n");
					sb.append(" AND id_tipo_operacion in (3000, 3001,3801)\n");
					System.out.println(sb.toString());
					res = jdbcTemplate.update(sb.toString());
//				}



		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:ejecutarCambioFechaPago");
		}
		return res;
	}
	@Override
	public String ejecutarPagoParcial(String parametros) {
		Map<String, Object> resultStored = new HashMap<>();
		String result = "Error al parcializar.";
		
		try{
			
			//int idUsuario = GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario();
			
			StoredProcedure storedDetalles = new StoredProcedure(jdbcTemplate, "sp_Solicitud_parcial");
			storedDetalles.declareParameter("cadena", parametros, StoredProcedure.IN);
			storedDetalles.declareParameter("mensaje", Types.VARCHAR, StoredProcedure.OUT);
			storedDetalles.declareParameter("result", Types.INTEGER, StoredProcedure.OUT);
			resultStored = storedDetalles.execute();
	        
	        result = (String)resultStored.get("mensaje");
	        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:ejecutarPagoParcial");
			 
		}
		return result;
	}


	@Override
	public int claveControlValida(String claveControl) {
		StringBuffer sb = new StringBuffer();
		int coincidencias = 0;
		
		sb.append("SELECT COUNT(*) FROM SELECCION_AUTOMATICA_GRUPO \n");
		sb.append("WHERE CVE_CONTROL = '"+claveControl+"'\n");
		
		try {
			coincidencias = jdbcTemplate.queryForInt(sb.toString());
			
		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:claveControlValida");
		}
	
		return coincidencias;
	}


	@Override
	public String obtenProveedor(String[] folioDet) {
		StringBuffer sb = new StringBuffer();
		List<String> proveedores = new ArrayList<String>();
		
		sb.append("SELECT no_cliente FROM movimiento \n");
		sb.append("WHERE no_folio_det = "+folioDet[0]+"\n");
		
		try {
			proveedores = jdbcTemplate.query(sb.toString(), new RowMapper<String>() {
				public String mapRow(ResultSet rs, int idx) throws SQLException{
				return rs.getString("no_cliente");
			}});

		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:obtenProveedor");
		}
		return proveedores.get(0);
	}


	@Override
	public String validaReferenciaCte(String noCliente) {
		StringBuffer sb = new StringBuffer();
		List<String> referencias = new ArrayList<String>();
		
		sb.append("SELECT RTRIM(LTRIM(referencia_cte)) as referencia_cte FROM persona \n");
		sb.append("WHERE no_persona = '"+noCliente+"'\n");
		
		try {
			referencias = jdbcTemplate.query(sb.toString(), new RowMapper<String>() {
				public String mapRow(ResultSet rs, int idx) throws SQLException{
				return rs.getString("referencia_cte") != null ? rs.getString("referencia_cte"):"";
			}});

		} catch (Exception e) {
			e.getStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:obtenProveedor");
		}
		return referencias.get(0);
	}


	@Override
	public void actualizaProveedor(String noCliente) {
		int res = -1;
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE persona SET referencia_cte='concepto' ");
			sb.append("WHERE no_persona = '"+noCliente+"'\n");			
			res = jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PagosPedientesDaoImpl, M:actualizaProveedor");
		} System.out.println(res);
		
	}

}
