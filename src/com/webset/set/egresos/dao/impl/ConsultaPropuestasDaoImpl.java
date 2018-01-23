package com.webset.set.egresos.dao.impl;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.mozilla.javascript.Undefined;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.egresos.dao.ConsultaPropuestasDao;
import com.webset.set.egresos.dto.BitacoraPropuestasDto;
import com.webset.set.egresos.dto.BloqueoPagoCruzadoDto;
import com.webset.set.egresos.dto.ColoresBitacoraDto;
import com.webset.set.egresos.dto.ComunEgresosDto;
import com.webset.set.egresos.dto.ConsultaPropuestasDto;
import com.webset.set.egresos.dto.PagosPropuestosDto;
import com.webset.set.egresos.dto.ParamConsultaPropuestasDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.GrupoEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.SeleccionAutomaticaGrupoDto;
import com.webset.utils.tools.StoredProcedure;
import com.webset.utils.tools.Utilerias;
import com.webset.set.utilerias.ConstantesSet;

public class ConsultaPropuestasDaoImpl implements ConsultaPropuestasDao{

	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora=new Bitacora();
	ConsultasGenerales consultasGenerales;
	Funciones funciones= new Funciones();
	private String gsDBM = ConstantesSet.gsDBM;
	private Logger logger = Logger.getLogger(ConsultaPropuestasDaoImpl.class);
	private String importeTotal;
	
	public List<LlenaComboGralDto>llenarComboGral(LlenaComboGralDto dto){	
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.llenarComboGral(dto);
	}
	
	public List<LlenaComboGralDto>llenarComboGralB(LlenaComboGralDto dto){	
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.llenarComboGralB(dto);
	}

	//Metodo creado EMS 140915
	public List<LlenaComboGralDto>llenarComboBeneficiario(LlenaComboGralDto dto){
		
		String cond="";
		cond=dto.getCondicion();
		dto.setCampoDos("COALESCE(razon_social,'')");
		
		if(dto.isRegistroUnico())
		{
			dto.setCondicion("equivale_persona="+Utilerias.validarCadenaSQL(cond));
		}else{
			dto.setCondicion("id_tipo_persona='P'	"
					+"	AND no_empresa in(552,217)"
					+"	AND ((razon_social like '"+Utilerias.validarCadenaSQL(cond)+"%'"     
					+"	or paterno like '"+Utilerias.validarCadenaSQL(cond)+"%'" 
					+"	or materno like '"+Utilerias.validarCadenaSQL(cond)+"%'"   
					+"	or nombre like '"+Utilerias.validarCadenaSQL(cond)+"%' )" 
					+"	or (equivale_persona like '"+Utilerias.validarCadenaSQL(cond)+"%'))");	
		}
		
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.llenarComboGral(dto);
	}
	
	public Date consultarFechaHoy(){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.obtenerFechaHoy();
	}


	public String consultarConfiguraSet(int indice){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
	}


	public Date obtenerFechaHoy(){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.obtenerFechaHoy();
	}


	public boolean validarUsuarioAutenticado(int idUsr, String psw){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.validarUsuarioAutenticado(idUsr, psw);
	}
	
	/**
	 * Obtiene las propuestas de pago
	 */
	public List<SeleccionAutomaticaGrupoDto> consultarSeleccionAutomatica(ConsultaPropuestasDto dtoIn){
		StringBuffer sql= new StringBuffer();
		List<SeleccionAutomaticaGrupoDto> list= new ArrayList<SeleccionAutomaticaGrupoDto>();
		
		try{
            sql.append("SELECT sa.motivo_rechazo,sa.cve_control, sa.id_grupo_flujo, sa.cupo_total - sa.cupo_automatico as propuesto, ");
            sql.append("\n       cupo_total, c.desc_grupo_cupo, sa.fec_limite_selecc, sa.fecha_propuesta, sa.id_grupo, f.desc_grupo_flujo, ");
            sql.append("\n 		 sa.concepto as concepto, ");
            
            //*****************************************************************************************//
            sql.append("\n       sa.usuario_uno, coalesce((select clave_usuario from seg_usuario where id_usuario = sa.usuario_uno), '') as user1, sa.usuario_dos, ");
            sql.append("\n       coalesce((select clave_usuario from seg_usuario where id_usuario = sa.usuario_dos), '') as user2, sa.usuario_tres, coalesce((select clave_usuario from seg_usuario where id_usuario = sa.usuario_tres), '') as user3, f.nivel_autorizacion, sa.id_division, ");
            sql.append("\n       ( SELECT count(*) ");
            sql.append("\n         FROM movimiento m ");
            sql.append("\n         WHERE m.id_estatus_mov = 'L' ");
            sql.append("\n             AND m.id_tipo_movto = 'E' ");
            sql.append("\n             AND m.id_tipo_operacion between 3800 and 3899 ");
            sql.append("\n             AND m.cve_control = sa.cve_control ");
            sql.append("\n       ) as NumIntercos, ");
            sql.append("\n       ( SELECT sum(m.importe) ");
            sql.append("\n         FROM movimiento m ");
            sql.append("\n         WHERE m.id_estatus_mov = 'L' ");
            sql.append("\n             AND m.id_tipo_movto = 'E' ");
            sql.append("\n             AND m.id_tipo_operacion between 3800 and 3899 ");
            sql.append("\n             AND m.cve_control = sa.cve_control ");
            sql.append("\n       ) as TotalIntercos,");
            
            //***** AGREGADO EMS: 03/11/2015 ******
            //divisa
    		if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE")){
	            sql.append("\n case when CHARINDEX('MN', coalesce(sa.concepto, ''), 0) > 0 then 'MN' ");
	            sql.append("\n else ");
		        sql.append("\n 	case when CHARINDEX('DLS', coalesce(sa.concepto, ''), 0) > 0 then 'DLS'  ");
	            sql.append("\n 	else	 ");
		        sql.append("\n 		case when CHARINDEX('EUR', coalesce(sa.concepto, ''), 0) > 0 then 'EUR' ");
		        sql.append("\n 		else 'OTR'  ");
			    sql.append("\n 		end ");
		        sql.append("\n 	end  ");
	            sql.append("\n  end as divisa, ");
	            
	            sql.append("\n  case when SUBSTRING ( coalesce(sa.concepto, '') ,0 , 2 ) = 'MA' then 'MA' ");
			    sql.append("\n  else ");
		        sql.append("\n 	case when SUBSTRING ( coalesce(sa.concepto, '') ,0 , 2 ) = 'MR' then 'MR' "); 
			    sql.append("\n 	else  ");
		        sql.append("\n 		case when SUBSTRING ( coalesce(sa.concepto, '') ,0 , 2 ) = 'MN' then 'MN' ");
				sql.append("\n 		else 'M'  ");
			    sql.append("\n 		end ");
		        sql.append("\n 	end  ");
	            sql.append("\n end as origenProp, ");
	            
	            sql.append("\n ( SELECT CASE WHEN  (sa.concepto not like 'M-%' AND sa.concepto not like 'R-%') THEN DESC_FORMA_PAGO ");
	            sql.append("\n          ELSE '' ");
	            sql.append("\n          END ");
	            sql.append("\n FROM CAT_FORMA_PAGO ");
	            sql.append("\n WHERE convert(char,ID_FORMA_PAGO) = substring(sa.concepto, Len(sa.concepto), Len(sa.concepto)) ");
	            sql.append("\n  ) AS FORMA_PAGO ");
    		}
    		else if(gsDBM.equals("ORACLE") || gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2")){
	            sql.append("\n case when INSTR(coalesce(sa.concepto, ''),'MN', 0, 1) > 0 then 'MN' ");
	            sql.append("\n else ");
		        sql.append("\n 	case when INSTR(coalesce(sa.concepto, ''),'DLS', 0, 1) > 0 then 'DLS'  ");
	            sql.append("\n 	else	 ");
		        sql.append("\n 		case when INSTR(coalesce(sa.concepto, ''),'EUR', 0, 1) > 0 then 'EUR' ");
		        sql.append("\n 		else 'OTR'  ");
			    sql.append("\n 		end ");
		        sql.append("\n 	end  ");
	            sql.append("\n  end as divisa, ");
	            
	            sql.append("\n  case when SUBSTR ( coalesce(sa.concepto, ''), 0, 2 ) = 'MA' then 'MA' ");
			    sql.append("\n  else ");
		        sql.append("\n 	case when SUBSTR ( coalesce(sa.concepto, ''), 0, 2 ) = 'MR' then 'MR' "); 
			    sql.append("\n 	else  ");
		        sql.append("\n 		case when SUBSTR ( coalesce(sa.concepto, ''), 0, 2 ) = 'MN' then 'MN' ");
				sql.append("\n 		else 'M' ");
			    sql.append("\n 		end ");
		        sql.append("\n 	end  ");
	            sql.append("\n end as origenProp, ");
	            
	            sql.append("\n ( SELECT CASE WHEN  (sa.concepto not like 'M-%' AND sa.concepto not like 'R-%') THEN DESC_FORMA_PAGO ");
	            sql.append("\n          ELSE '' ");
	            sql.append("\n          END ");
	            sql.append("\n FROM CAT_FORMA_PAGO ");
	            sql.append("\n WHERE to_char(ID_FORMA_PAGO) = SUBSTR(sa.concepto, Length(sa.concepto), Length(sa.concepto)) ");
	            sql.append("\n  ) AS FORMA_PAGO ");
    		}
    		
            //Agregado EMS 15/03/2016 - Para compra de transferencias.
            sql.append("\n ,habilitado ");
            //se agrego esta instrucción el 05/12/2017
            sql.append("\n ,case when usuario_uno IS NOT NULL AND usuario_dos IS NOT NULL AND usuario_tres IS NOT NULL then 0 else 1 end as tiene_autorizaciones");
            sql.append("\n ,( SELECT COUNT(m.ID_CHEQUERA_BENEF) as sin_chequera  FROM MOVIMIENTO m ");
            sql.append("\n WHERE m.cve_control = sa.cve_control AND m.ID_FORMA_PAGO = 3 ");
            sql.append("\n AND m.ID_CHEQUERA_BENEF NOT IN (SELECT cb.id_chequera FROM CTAS_BANCO cb WHERE m.NO_CLIENTE = cb.NO_PERSONA AND cb.ID_TIPO_PERSONA = 'P')");
            sql.append("\n AND m.ID_TIPO_OPERACION = 3000  AND m.ID_ESTATUS_MOV = 'N' ) as sin_chequera_benef");
            sql.append("\n ,(SELECT COUNT(m.ID_CHEQUERA_BENEF) FROM MOVIMIENTO m WHERE m.cve_control = sa.cve_control ");
            sql.append("\n AND m.ID_FORMA_PAGO = 3 AND ((id_banco <> 12 AND m.ID_CHEQUERA_BENEF like 'CONV%') OR (id_banco <> 14 AND m.ID_CHEQUERA_BENEF like 'SANT%')) ");
            sql.append("\n AND m.ID_TIPO_OPERACION = 3000 AND m.ID_ESTATUS_MOV = 'N' ) as sin_banco_pagador_correcto ");
            sql.append("\n ,(SELECT count(P.RFC) FROM PERSONA P WHERE ( patindex (P.rfc,'^(([A-Z]|[a-z]){4})([0-9]{6})((([A-Z]|[a-z]|[0-9]){3}))')!=0 ");
            sql.append("\n  AND  patindex (P.rfc,'^(([A-Z]|[a-z]){3})([0-9]{6})((([A-Z]|[a-z]|[0-9]){3}))')!=0) ");
            sql.append("\n AND P.id_tipo_persona = 'P'  AND P.no_empresa = 552 ");
            sql.append("\n AND P.no_persona in (select no_cliente from movimiento m where m.cve_control = sa.cve_control and id_tipo_operacion = 3000 and id_estatus_mov in ('N', 'C')) ) AS sin_rfc ");
            
            /***************/
            sql.append("\n  FROM seleccion_automatica_grupo sa ");
            sql.append("\n       LEFT JOIN cat_grupo_cupo c On (sa.id_grupo = c.id_grupo_cupo)");
            sql.append("\n       LEFT JOIN cat_grupo_flujo f On (sa.id_grupo_flujo = f.id_grupo_flujo) ");
            sql.append("\n where 1 <= (SELECT COUNT(*) 			 ");																							
            sql.append("\n  			FROM movimiento m 		 ");																								
            sql.append("\n 			WHERE m.id_tipo_movto = 'E'		 ");																						
            sql.append("\n    				AND m.cve_control  = sa.cve_control		 ");																																			
            sql.append("\n   				AND m.id_estatus_mov IN ('N','C','F','L')	 ");																														
            sql.append("\n   				AND m.id_tipo_operacion IN(3000,3801)		 ");																														
            sql.append("\n    				AND m.id_forma_pago IN (1,3,5,6,7,9,10))	 ");																		
            sql.append("\n  		AND sa.id_grupo_flujo in (SELECT id_grupo_flujo  	 ");																			
            sql.append("\n 						FROM grupo_empresa 					 ");																		
            sql.append("\n 					WHERE no_empresa IN (SELECT no_empresa		 ");																
            sql.append("\n 										FROM usuario_empresa  			 ");																
            sql.append("\n 										WHERE no_usuario IN (SELECT id_usuario   ");													
            sql.append("\n 															FROM SEG_usuario  	 ");													
            sql.append("\n 															WHERE clave_usuario = (select clave_usuario from seg_usuario where id_usuario = "+Utilerias.validarCadenaSQL(dtoIn.getIdUsuario())+")))) ");
            sql.append("\n AND");
                        
           if(dtoIn.getPvsDivision()!=null && dtoIn.getPvsDivision().equals("")){
	             sql.append( "\n    sa.id_grupo_flujo in ");
	             sql.append( "\n       ( SELECT id_grupo_flujo ");
	             sql.append( "\n         FROM grupo_empresa ");
	             sql.append( "\n         WHERE no_empresa in ");
	             sql.append( "\n             ( SELECT no_empresa ");
	             sql.append( "\n               FROM usuario_empresa ");
	             sql.append( "\n               WHERE no_usuario = "+Utilerias.validarCadenaSQL(dtoIn.getIdUsuario())+" ) )");
            }else{
	            sql.append( "\n    sa.id_division in ");
            	sql.append( "\n       ( SELECT id_division ");
	            sql.append( "\n         FROM usuario_division ");
	            sql.append( "\n         WHERE no_usuario = "+Utilerias.validarCadenaSQL(dtoIn.getIdUsuario())+" ) ");
            }
            
           if(!dtoIn.getIdCliente().equals("") && dtoIn.getIdCliente()!=null)
            {
	            sql.append( "\n AND sa.cve_control in (Select distinct cve_control ");
	            sql.append( "\n                        From movimiento ");
	            sql.append( "\n                        Where no_cliente in (SELECT NO_PERSONA ");
	            sql.append( "\n                         					FROM PERSONA ");
	            sql.append( "\n                        						WHERE EQUIVALE_PERSONA = '"+Utilerias.validarCadenaSQL(dtoIn.getIdCliente())+"') )");
            }
	        
	        if(dtoIn.getGrupoEmpresa()>0)
	            sql.append( "\n AND sa.id_grupo_flujo = " +Utilerias.validarCadenaSQL(dtoIn.getGrupoEmpresa()));
	        
	        if(dtoIn.getPvsDivision()!=null && !dtoIn.getPvsDivision().trim().equals(""))
	        {
	            if(dtoIn.getPvsDivision().trim().equals("TODAS LAS DIVISIONES")) 
	                sql.append( "\n AND ( not sa.id_division is null and sa.id_division <> '' ) ");
            	else
	                sql.append( "\n AND sa.id_division = '"+Utilerias.validarCadenaSQL(dtoIn.getPvsDivision())+"' ");
	        }
	        
	        if(dtoIn.getPvGrupoRubro()>0)
	            sql.append( "\n AND sa.id_grupo = " +Utilerias.validarCadenaSQL(dtoIn.getPvGrupoRubro()));
	        
	        //Todas las propusetas
    		if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE")){
		        if(!dtoIn.isTodasPropuestas()){
			        if((dtoIn.getFechaFin()!=null && !dtoIn.getFechaFin().trim().equals("")) && (dtoIn.getFechaIni()!=null && !dtoIn.getFechaIni().trim().equals("")))
			            sql.append( "\n AND sa.fecha_propuesta between convert(datetime,'"+Utilerias.validarCadenaSQL(dtoIn.getFechaIni())+"', 103)  AND convert(datetime,'"+Utilerias.validarCadenaSQL(dtoIn.getFechaFin())+"',103) ");
			        else if(dtoIn.getFechaIni()!=null && !dtoIn.getFechaIni().trim().equals(""))
			            sql.append( "\n AND sa.fecha_propuesta >= convert(datetime,'"+Utilerias.validarCadenaSQL(dtoIn.getFechaIni())+"',103) ");
		        }
    		}
    		else if(gsDBM.equals("ORACLE") || gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2")){
		        if(!dtoIn.isTodasPropuestas()){
			        if((dtoIn.getFechaFin()!=null && !dtoIn.getFechaFin().trim().equals("")) && (dtoIn.getFechaIni()!=null && !dtoIn.getFechaIni().trim().equals("")))
			            sql.append( "\n AND sa.fecha_propuesta between to_date('"+Utilerias.validarCadenaSQL(dtoIn.getFechaIni())+"', 'dd/mm/yyyy')  AND to_date('"+Utilerias.validarCadenaSQL(dtoIn.getFechaFin())+"', 'dd/mm/yyyy') ");
			        else if(dtoIn.getFechaIni()!=null && !dtoIn.getFechaIni().trim().equals(""))
			            sql.append( "\n AND sa.fecha_propuesta >= to_date('"+Utilerias.validarCadenaSQL(dtoIn.getFechaIni())+"', 'dd/mm/yyyy') ");
		        }
    			
    		}
	        
	        /*if(dtoIn.isTodasPropuestas())
	        	sql.append( "\n AND sa.fecha_propuesta <= '" + dtoIn.getFechaFin() + "' ");
	        else{
		        if((dtoIn.getFechaFin()!=null && !dtoIn.getFechaFin().trim().equals("")) && (dtoIn.getFechaIni()!=null && !dtoIn.getFechaIni().trim().equals("")))
		            sql.append( "\n AND sa.fecha_propuesta between TO_DATE('"+dtoIn.getFechaIni()+"', 'DD/MM/YYYY')  AND TO_DATE('"+dtoIn.getFechaFin()+"','DD/MM/YYYY') ");
		        else if(dtoIn.getFechaIni()!=null && !dtoIn.getFechaIni().trim().equals(""))
		            sql.append( "\n AND sa.fecha_propuesta >= TO_DATE('"+dtoIn.getFechaIni()+"','DD/MM/YYYY') ");
	        }*/
	        
	        //sql.append( "\n AND fecha_pago is null ");
	        
	        //Agregado EMS: 09/11/2015 - Para que solo muestre propuestas con la firma 0 ï¿½ 1 firma, no 2.... 23/12/15 - Que siempre no, se deben de mostrar aunque ya tenga todas las firmas.
	        //sql.append( "\n AND (coalesce(sa.usuario_uno,0) = 0 OR coalesce(sa.usuario_uno,0) <> 0 OR coalesce(sa.usuario_uno,0) IS NULL) "); //Para que no muestre los usuarios con dos firmas (Ya autorizados).
	        //sql.append( "\n AND (coalesce(sa.usuario_dos,0) = 0 OR coalesce(sa.usuario_dos,0) IS NULL ) ");
	        
	        //Agregadoo EMS: 10/11/2015 - No muestra las propuestas con cupo cero, nueva modificacion agregada
	        sql.append( "\n AND sa.concepto not like 'CUPO EN CERO%'");
	        
	        /*if(dtoIn.isSoloMisProp()){
	        	//sql.append( "\n AND sa.cve_control like '%"+dtoIn.getIdUsuario()+"' ");
	        	sql.append( "\n AND sa.concepto like '%'||(select clave_usuario from seg_usuario where id_usuario = "+dtoIn.getIdUsuario()+")||'%' ");
		    }*/
	        
		        /**** Agregado Divisas EMS: 1709150*/
		        
		        StringBuffer cadDivisas= new StringBuffer();
		        cadDivisas.append(" and (");
		        
		        if(dtoIn.isDivMN()){
		        	cadDivisas.append("\n sa.concepto like '%MN%' ");
		        }
		        
		        if(dtoIn.isDivDLS()){
		        	if(cadDivisas.toString().equals(" and (")){
		        		cadDivisas.append( "\n sa.concepto like '%DLS%' ");
		        	}else{
		        		cadDivisas.append( "\n or concepto like '%DLS%'");
		        	}
		        }
		        
		        if(dtoIn.isDivEUR()){
		        	if(cadDivisas.toString().equals(" and (")){
		        		cadDivisas.append( "\n sa.concepto like '%EUR%' ");
		        	}else{
		        		cadDivisas.append( "\n or concepto like '%EUR%' ");
		        	}
		        }
		        
		        if(dtoIn.isDivOtras()){
		        	if(cadDivisas.toString().equals(" and (")){
		        		cadDivisas.append( "\n concepto not like '%MN%' and concepto not like '%DLS%' and concepto not like '%EUR%' ");
		        	}else{
		        		cadDivisas.append( "\n or (concepto not like '%MN%' and concepto not like '%DLS%' and concepto not like '%EUR%') ");
		        	}
		        }
		        
		        if(!cadDivisas.toString().equals(" and (")){
		        	sql.append("\n" + cadDivisas.append(")"));
		        }
		        if(dtoIn.isNominaRH()){
		        	sql.append("\n AND sa.cve_control like 'MAN%' ");
		        }else if(!dtoIn.isNominaTes()){ //Si no es nominaTes muestra solo las propuestas a proveedores
		        	sql.append("\n AND sa.cve_control not like 'MAN%' ");
		        } //Si resulta falso (Si tiene nominaTes muestra propuestas de proveedores y nominas)
		        
	        /****	DEJAR LAS CONDICIONES DE REGLAS DE NEGOCIOS AL FINAL	****/
	        if(dtoIn.getTipoRegla().equals("S")){
	        	//Si se llegara a pedir buscar solo por regla de negocios, quitar el OR
	        	if(!dtoIn.getReglaNegocio().equals("")){
	        		
	        		if(dtoIn.getReglaNegocio().equals("***** TODAS *****")){
	        			sql.append("\n AND (sa.concepto like 'S-%' ");
	        			sql.append("\n OR sa.concepto like 'D-S-%' ");
	        			sql.append("\n OR sa.concepto like 'C-S-%' ");
	        			sql.append("\n OR sa.CONCEPTO like 'M%' ");
	        			sql.append("\n " +(dtoIn.isSoloMisProp()?
								"AND sa.concepto like '%'+(select clave_usuario from seg_usuario where id_usuario = "+dtoIn.getIdUsuario()+")+'%' "
								:""));
	        			sql.append("\n ) ");
	        			sql.append("\n AND (sa.concepto NOT like 'L-%' AND sa.concepto NOT like 'D-L-%'  AND sa.concepto NOT like 'C-L-%') ");
	        			
	        		}else{
	        			//sql.append("\n AND (sa.concepto like '"+ dtoIn.getTipoRegla() +"-%' AND sa.concepto like '%-"+dtoIn.getReglaNegocio()+"-%' AND sa.concepto not like 'L-%')");
		        		sql.append("\n AND (((sa.concepto like '"+ Utilerias.validarCadenaSQL(dtoIn.getTipoRegla()) 
		        								+"-%' AND sa.concepto like '%-"+Utilerias.validarCadenaSQL(dtoIn.getReglaNegocio())
		        								+"-%') OR (sa.concepto like '"+ Utilerias.validarCadenaSQL(dtoIn.getTipoRegla()) 
		        								+"-%' AND sa.concepto like '%-"
		        								+Utilerias.validarCadenaSQL(dtoIn.getReglaNegocio())
		        								+"-%' OR (sa.concepto not like 'L-%' AND sa.concepto not like 'S-%'"
		        								+ " AND sa.concepto not like 'D-S-%' "
												+ " AND sa.concepto not like 'C-S-%' "
												+ " AND sa.concepto not like 'D-L-%' "
												+ " AND sa.concepto not like 'C-L-%' )"
												+ " " +(dtoIn.isSoloMisProp()? //Solo mis propuestas
														"AND sa.concepto like '%'+(select clave_usuario from seg_usuario where id_usuario = "+dtoIn.getIdUsuario()+")+'%' "
														:"") +""
												+ "))");
		        		
		        		// NUEVA MODIFICACIï¿½N A REGLAS DE NEGOCIOS, SE VALIDA PARA DESCUENTOS Y REEMBOLSOS.
		        		//** DESCUENTO
		        		sql.append("\n OR (sa.concepto like 'D-"+ dtoIn.getTipoRegla() 
											+"-%' AND sa.concepto like '%-"+Utilerias.validarCadenaSQL(dtoIn.getReglaNegocio())
											+"-%')");
		        		
		        		// CON PAGO
		        		sql.append("\n OR (sa.concepto like 'C-"+Utilerias.validarCadenaSQL(dtoIn.getTipoRegla()) 
											+"-%' AND sa.concepto like '%-"+Utilerias.validarCadenaSQL(dtoIn.getReglaNegocio())
											+"-%'))");
		        		 
	        		}//fin else TODAS
	        		
	        		
		        }else{
		        	//Modificado para descuentos y reembolsos EMS: 22/01/2016
		        	//sql.append("\n AND (sa.concepto like '"+ dtoIn.getTipoRegla() +"-%' OR sa.concepto not like 'L-%' ) ");
		        	
		        	//EMS 02/09/2016 - Se comenta para cargar solo las propuestas manuales.
		        	/*sql.append("\n AND ((sa.concepto like 'S-%' OR sa.concepto like 'D-S-%' "
		        			+ " OR sa.concepto like 'C-S-%' AND sa.concepto not like 'L-%' "
		        			+ " AND sa.concepto NOT like 'D-L-%' AND sa.concepto NOT like 'C-L-%' )");
		        	*/
		        	
		        	sql.append("\n AND sa.concepto not like 'S-%' AND sa.concepto not like 'D-S-%'  and sa.concepto not like 'C-S-%' ");
		        	sql.append("\n AND sa.concepto NOT like 'L-%' AND sa.concepto NOT like 'D-L-%'  AND sa.concepto NOT like 'C-L-%' ");
		        	sql.append("\n " +(dtoIn.isSoloMisProp()?
											"AND sa.concepto like '%'+(select clave_usuario from seg_usuario where id_usuario = "+dtoIn.getIdUsuario()+")+'%' "
											:""));
		        	
		        	/*sql.append("\n OR (sa.concepto NOT like 'S-%' AND sa.concepto NOT like 'D-S-%'  "
		        			+ " AND sa.concepto NOT like 'C-S-%' AND sa.concepto not like 'L-%' "
		        			+ " AND sa.concepto NOT like 'D-L-%' AND sa.concepto NOT like 'C-L-%' "
		        			+ " " +(dtoIn.isSoloMisProp()?
													"AND sa.concepto like '%'||(select clave_usuario from seg_usuario where id_usuario = "+dtoIn.getIdUsuario()+")||'%' "
													:"") +""
		        			+ ")) ");
		        	*/
		        	
		        }
	        	
	        	//Si se llegarar a considerar buscar solo por regla de negocios... quitar el OR
	        }
	        else if(dtoIn.getTipoRegla().equals("L")){
	        	if(!dtoIn.getReglaNegocio().equals("")){
	        		if(dtoIn.getReglaNegocio().equals("***** TODAS *****")){
	        			
	        			sql.append("\n AND (sa.concepto like 'L-%' ");
	        			sql.append("\n OR sa.concepto like 'D-L-%' ");
	        			sql.append("\n OR sa.concepto like 'C-L-%' ");
	        			sql.append("\n OR sa.CONCEPTO like 'M%' ");
	        			sql.append("\n " +(dtoIn.isSoloMisProp()?
								"AND sa.concepto like '%'+(select clave_usuario from seg_usuario where id_usuario = "+dtoIn.getIdUsuario()+")+'%' "
								:""));
	        			sql.append("\n )");
	        			
	        			sql.append("\n AND (sa.concepto NOT like 'S-%' AND sa.concepto NOT like 'D-S-%'  AND sa.concepto NOT like 'C-S-%') ");
	        		}
	        		else{
	        			//sql.append("\n AND (sa.concepto like '"+ dtoIn.getTipoRegla() +"-%' AND sa.concepto like '%-"+dtoIn.getReglaNegocio()+"-%' AND sa.concepto not like 'S-%')");
		        		sql.append("\n AND (((sa.concepto like '"+ Utilerias.validarCadenaSQL(dtoIn.getTipoRegla() )
		        							+"-%' AND sa.concepto like '%-"
		        							+Utilerias.validarCadenaSQL(dtoIn.getReglaNegocio())+"-%') OR (sa.concepto like '"
		        							+ Utilerias.validarCadenaSQL(dtoIn.getTipoRegla()) +"-%' AND sa.concepto like '%-"
		        							+Utilerias.validarCadenaSQL(dtoIn.getReglaNegocio())
		        							+"-%' OR (sa.concepto not like 'L-%' AND sa.concepto not like 'S-%' "
		        							+ " AND sa.concepto not like 'D-S-%' "
											+ " AND sa.concepto not like 'C-S-%' "
											+ " AND sa.concepto not like 'D-L-%' "
											+ " AND sa.concepto not like 'C-L-%' )"
											+ " " +(dtoIn.isSoloMisProp()? //Solo mis propuestas
													"AND sa.concepto like '%'+(select clave_usuario from seg_usuario where id_usuario = "+dtoIn.getIdUsuario()+")+'%' "
													:"") +""
											+ "))");
		        		
		        		// NUEVA MODIFICACIï¿½N A REGLAS DE NEGOCIOS, SE VALIDA PARA DESCUENTOS Y REEMBOLSOS.
		        		//** DESCUENTO
		        		sql.append("\n OR (sa.concepto like 'D-"+ Utilerias.validarCadenaSQL(dtoIn.getTipoRegla()) 
											+"-%' AND sa.concepto like '%-"+Utilerias.validarCadenaSQL(dtoIn.getReglaNegocio())
											+"-%')");
		        		
		        		// CON PAGO
		        		sql.append("\n OR (sa.concepto like 'C-"+ Utilerias.validarCadenaSQL(dtoIn.getTipoRegla()) 
										+"-%' AND sa.concepto like '%-"+Utilerias.validarCadenaSQL(dtoIn.getReglaNegocio())
										+"-%'))");
		        		
	        		} //fin else TODAS
	        	}
	        	else{
		        	//sql.append("\n AND (sa.concepto like '"+ dtoIn.getTipoRegla() +"-%' OR sa.concepto not like 'S-%')");
		        	
		        	//EMS 02/09/2016 - Se comenta para cargar solo las propuestas manuales.
		        	/*sql.append("\n AND ((sa.concepto like 'L-%' OR sa.concepto like 'D-L-%' "
		        			+ " OR sa.concepto like 'C-L-%' AND sa.concepto not like 'S-%' "
		        			+ " AND sa.concepto NOT like 'D-S-%' AND sa.concepto NOT like 'C-S-%' )");
		        	*/
		        	
		        	sql.append("\n AND sa.concepto not like 'S-%' AND sa.concepto not like 'D-S-%'  and sa.concepto not like 'C-S-%' ");
		        	sql.append("\n AND sa.concepto NOT like 'L-%' AND sa.concepto NOT like 'D-L-%'  AND sa.concepto NOT like 'C-L-%' ");
		        	sql.append("\n " +(dtoIn.isSoloMisProp()?
											"AND sa.concepto like '%'+(select clave_usuario from seg_usuario where id_usuario = "+dtoIn.getIdUsuario()+")+'%' "
											:""));
		        }
	        }
	        //------------------------------------------------------------------------------------------//
	        System.out.println("consultarSeleccionAutomatica: " + sql);
	        logger.info("consultarSeleccionAutomatica: " + sql);
	        
	        list= jdbcTemplate.query(sql.toString(), new RowMapper<SeleccionAutomaticaGrupoDto>(){
				public SeleccionAutomaticaGrupoDto mapRow(ResultSet rs, int idx) throws SQLException {
					SeleccionAutomaticaGrupoDto cons = new SeleccionAutomaticaGrupoDto();
					String tmp[];
					cons.setCveControl(rs.getString("cve_control"));
					cons.setIdGrupoFlujo(rs.getInt("id_grupo_flujo"));
					//cons.setPropuesto(rs.getDouble("propuesto"));
					cons.setCupoTotal(rs.getDouble("cupo_total"));
					cons.setDescGrupoCupo(rs.getString("desc_grupo_cupo"));
					cons.setFechaPropuesta(funciones.ponerFechaSola(rs.getDate("fecha_propuesta")));
					//cons.setIdGrupo(rs.getInt("id_grupo"));
					cons.setDescGrupoFlujo(rs.getString("desc_grupo_flujo"));
					cons.setConcepto(rs.getString("concepto"));
					
					tmp = rs.getString("concepto").split("-");
					
					if(tmp[0].equals("M") || tmp[0].equals("MA") || tmp[0].equals("R") || tmp[0].equals("MAN")){
						cons.setNombreRegla(rs.getString("concepto"));
					}else{
						
						if(tmp.length>1){
							System.out.println("tmp2 "+tmp[3]);
							cons.setNombreRegla((tmp[0].equals("S") 
									|| tmp[0].equals("L")
								)?tmp[2]:tmp[3]);
						}
						
		
					}
					cons.setUsuarioUno(rs.getInt("usuario_uno"));
					cons.setUser1(rs.getString("user1"));
					cons.setUsuarioDos(rs.getInt("usuario_dos"));
					cons.setUser2(rs.getString("user2"));
					cons.setUsuarioTres(rs.getInt("usuario_tres"));
					cons.setUser3(rs.getString("user3"));
//					cons.setNivelAutorizacion(1); comente esta linea
					cons.setNivelAutorizacion(rs.getInt("nivel_autorizacion"));
					//cons.setIdDivision(rs.getString("id_division"));
					//cons.setNumIntercos(rs.getInt("NumIntercos"));
					//cons.setTotalIntercos(rs.getInt("TotalIntercos"));
					String dia = "", mes = "", anio = "", fechaFinal = "", fechaAux;
					if(tmp[0].equals("M")){
						dia = tmp[4].substring(0,2);
						mes = tmp[4].substring(2,4);
						anio = tmp[4].substring(4,8);
						fechaFinal = dia+"/"+mes+"/"+anio;
						cons.setFecLimiteSelecc(fechaFinal);
					}else if(tmp[0].equals("MAN")){
						fechaAux = rs.getString("cve_control").substring(rs.getString("cve_control").length()-11, rs.getString("cve_control").length()-3);
						dia = fechaAux.substring(0,2);
						mes = fechaAux.substring(2,4);
						anio = fechaAux.substring(4,8);
						fechaFinal = dia+"/"+mes+"/"+anio;
						cons.setFecLimiteSelecc(fechaFinal);
					}else{
						cons.setFecLimiteSelecc(funciones.ponerFechaSola(rs.getDate("fec_limite_selecc")));	
					}
					cons.setDivisa(rs.getString("divisa"));
					cons.setOrigenPropuesta(rs.getString("origenProp"));
					cons.setViaPago(rs.getString("FORMA_PAGO"));
					cons.setHabilitado(rs.getString("habilitado"));
					cons.setMotivoRechazo(rs.getString("motivo_rechazo"));
					return cons;
				}});
            
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:consultarSeleccionAutomatica");
		}
		return list;
	}

	
	public List<SeleccionAutomaticaGrupoDto> consultarSeleccionAutomaticaStored(ConsultaPropuestasDto dtoIn){
		StringBuffer sql= new StringBuffer();
		List<SeleccionAutomaticaGrupoDto> list= new ArrayList<SeleccionAutomaticaGrupoDto>();
		//Map<String, Object> resultStored = new HashMap<>();
		new HashMap<>();
		try{
			int idUsuario = GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario();
			
			StoredProcedure storedPropuestas = new StoredProcedure(jdbcTemplate, "SP_CONSULTAR_PROPUESTAS");
			storedPropuestas.declareParameter("V_id_usuario", idUsuario, StoredProcedure.IN);
			storedPropuestas.declareParameter("V_no_prov", dtoIn.getIdCliente() , StoredProcedure.IN);
			storedPropuestas.declareParameter("V_id_grupo_emp", dtoIn.getGrupoEmpresa(), StoredProcedure.IN);
			storedPropuestas.declareParameter("V_fecha_ini", dtoIn.getFechaIni(), StoredProcedure.IN);
			storedPropuestas.declareParameter("V_fecha_fin", dtoIn.getFechaFin(), StoredProcedure.IN);
			storedPropuestas.declareParameter("V_div_mn", (dtoIn.isDivMN()?1:0) , StoredProcedure.IN);
			storedPropuestas.declareParameter("V_div_dls", (dtoIn.isDivDLS()?1:0), StoredProcedure.IN);
			storedPropuestas.declareParameter("V_div_eur", (dtoIn.isDivEUR()?1:0) , StoredProcedure.IN);
			storedPropuestas.declareParameter("V_div_otr", (dtoIn.isDivOtras()?1:0) , StoredProcedure.IN);
			storedPropuestas.declareParameter("V_regla_negocio",dtoIn.getReglaNegocio() , StoredProcedure.IN);
			storedPropuestas.declareParameter("V_tipo_regla", dtoIn.getTipoRegla(), StoredProcedure.IN);
			storedPropuestas.declareParameter("V_solo_mis_prop", (dtoIn.isSoloMisProp()?1:0), StoredProcedure.IN);
			storedPropuestas.execute();
			
            sql.append("\n SELECT CVE_CONTROL, ID_GRUPO_FLUJO, PROPUESTO, CUPO_TOTAL, ");
            sql.append("\n 	   DESC_GRUPO_CUPO, FEC_LIMITE_SELECC, FECHA_PROPUESTA, ");
            sql.append("\n 		ID_GRUPO, DESC_GRUPO_FLUJO, CONCEPTO, USUARIO_UNO, USER1, ");
            sql.append("\n 		USUARIO_DOS, USER2, USUARIO_TRES, USER3, NIVEL_AUTORIZACION, ");
            sql.append("\n 		ID_DIVISION, NUMINTERCOS, TOTALINTERCOS, DIVISA, ORIGENPROP, ");
            sql.append("\n 		FORMA_PAGO, ESTATUS, COLOR, ID_USUARIO ");
            sql.append("\n FROM TMP_CARGA_PROPUESTAS ");
            sql.append("\n WHERE ID_USUARIO = "+GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+" ");
            sql.append("\n "); 
	       
            System.out.println("\nQuery de las propuestas: " + sql);
	        
	        list= jdbcTemplate.query(sql.toString(), new RowMapper<SeleccionAutomaticaGrupoDto>(){
				public SeleccionAutomaticaGrupoDto mapRow(ResultSet rs, int idx) throws SQLException {
					SeleccionAutomaticaGrupoDto cons = new SeleccionAutomaticaGrupoDto();
					String tmp[];
					
					cons.setCveControl(rs.getString("cve_control"));
					cons.setIdGrupoFlujo(rs.getInt("id_grupo_flujo"));
					cons.setCupoTotal(rs.getDouble("cupo_total"));
					cons.setDescGrupoCupo(rs.getString("desc_grupo_cupo"));
					cons.setFechaPropuesta(funciones.ponerFechaSola(rs.getDate("fecha_propuesta")));
					cons.setDescGrupoFlujo(rs.getString("desc_grupo_flujo"));
					cons.setConcepto(rs.getString("concepto"));
					
					tmp = rs.getString("concepto").split("-");
					
					if(tmp[0].equals("M") || tmp[0].equals("MA")){
						cons.setNombreRegla(rs.getString("concepto"));
					}else{
						cons.setNombreRegla((tmp[0].equals("S") 
								|| tmp[0].equals("L")
							)?tmp[2]:tmp[3]);
		
					}
					
					cons.setUsuarioUno(rs.getInt("usuario_uno"));
					cons.setUser1(rs.getString("user1"));
					cons.setUsuarioDos(rs.getInt("usuario_dos"));
					cons.setUser2(rs.getString("user2"));
					cons.setUsuarioTres(rs.getInt("usuario_tres"));
					cons.setUser3(rs.getString("user3"));
					cons.setNivelAutorizacion(1);
					cons.setFecLimiteSelecc(funciones.ponerFechaSola(rs.getDate("fec_limite_selecc")));
					cons.setDivisa(rs.getString("divisa"));
					cons.setOrigenPropuesta(rs.getString("origenProp"));
					cons.setViaPago(rs.getString("FORMA_PAGO"));
					cons.setEstatus(rs.getString("ESTATUS"));
					cons.setColor(rs.getString("COLOR"));
					 
					return cons;
				}});
	        
	        //if(!list.isEmpty()){
	        	//obtener nivel de prioridad
	        	
	       // }
            
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:consultarSeleccionAutomatica");
		}
		return list;
	}
	
		//revisado
	public List<LlenaComboGralDto> llenarComboGrupoEmpresa(GrupoEmpresasDto dto){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.llenarComboGrupoEmpresa(dto);
	}

	//Revisado EMS 150915
	public int seleccionarPropuestos(String cveControl){
		 String sql="";
		 int count=0;
			try{
			    sql+=" SELECT count(*) as regs";
		    	sql+="   FROM movimiento";
			    sql+="  WHERE cve_control = '"+Utilerias.validarCadenaSQL(cveControl)+"' and id_estatus_mov not in ('X', 'Y', 'Z', 'H') ";
			    count=jdbcTemplate.queryForInt(sql);
			 }catch(Exception e){
				 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Egresos, C:ConsultaPropuestasDao, M:seleccionarPropuestos");
			 }
		 return count;
	}

	//revisado EMS 150915
	public int seleccionarPagados(String cveControl){
		 String sql="";
		 int count=0;
			try{
				sql+= " SELECT count(*) as regs";
			    sql+= "   FROM movimiento";
			    sql+= "  WHERE cve_control = '"+Utilerias.validarCadenaSQL(cveControl)+"'";
			    sql+= "    AND ((id_tipo_operacion = 3200 and id_estatus_mov not in ('X', 'Y', 'Z'))";
			    sql+= "    OR (id_tipo_operacion = 3000 and id_estatus_mov in ('V', 'I', 'R')))";
			    count=jdbcTemplate.queryForInt(sql);
			 }catch(Exception e){
				 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Egresos, C:ConsultaPropuestasDao, M:seleccionarPagados");
			 }
		 return count;
	}

	//revisado EMS 150915
	public String seleccionarRechazados(String cveControl){
		 String sql="";
		 List<String>listDatos=new ArrayList<String>();
			try{
			    sql+= " SELECT motivo_rechazo ";
			    sql+= "   FROM seleccion_automatica_grupo";
			    sql+= "  WHERE cve_control = '"+Utilerias.validarCadenaSQL(cveControl)+"'";
			    sql+= "    AND motivo_rechazo <> '' OR motivo_rechazo is not null ";
			    
			    listDatos= jdbcTemplate.query(sql, new RowMapper<String>(){
					public String mapRow(ResultSet rs, int idx) throws SQLException {
						return rs.getString("motivo_rechazo");
					}});
			 }catch(Exception e){
				 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Egresos, C:ConsultaPropuestasDao, M:seleccionarRechazados");
			 }
		
		 return listDatos.isEmpty()?"":listDatos.get(0);
	}


	public List<String> seleccionarNombresUsuarios(int idUsuario1, int idUsuario2, int idUsuario3){
		String sql="";
		List<String>list= new ArrayList<String>();
		try{
			
			sql+=" select (coalesce(cc.nombre, '') + ' ' + coalesce(cc.paterno, '') + ' ' + coalesce(cc.materno,'')) as nombre from cat_usuario cc ";
	        sql+=" where cc.no_usuario in (" +Utilerias.validarCadenaSQL(idUsuario1)
	        		+", " +Utilerias.validarCadenaSQL(idUsuario2)
	        		+", "+Utilerias.validarCadenaSQL(idUsuario3)+")";
	        
	        list= jdbcTemplate.query(sql, new RowMapper<String>(){
				public String mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getString("nombre");
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:seleccionarNombresUsuarios");
		}
		return list;
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
		    sql.append( "\n                     and vd.fec_divisa =  convert(datetime,'"+Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFecha()).substring(0,10)) +"', 130) ), ");
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
		    
		    sql.append( "\n      convert(datetime,'"+Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFecha()).substring(0,10)) +"', 130) - m.fec_valor_original as dias,");
		    
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
		    sql.append( "\n	  AND m.id_forma_pago in (1,3,5,6,7,9) ");
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
	        														    +"', 130) ), ");
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

	        sql.append( "\n      convert(datetime'"+Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFecha()).substring(0,10)) 
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
		    sql.append( "\n  AND m.id_forma_pago in (1,3,5,7,9)");
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

	public List<MovimientoDto>consultarDetalleStored(PagosPropuestosDto dto){
		StringBuffer sql= new StringBuffer();
	
		List<MovimientoDto>listDetalle= new ArrayList<MovimientoDto>();
		//Map<String, Object> resultStored = new HashMap<>();
		new HashMap<>();
		try{
				
			int idUsuario = GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario();
			
			StoredProcedure storedDetalles = new StoredProcedure(jdbcTemplate, "SP_CONSULTAR_DET_PROPUESTA");
			storedDetalles.declareParameter("V_id_grupo_emp", dto.getIdGrupoEmpresa(), StoredProcedure.IN);
			storedDetalles.declareParameter("V_cve_control", Utilerias.validarCadenaSQL(dto.getCveControl()), StoredProcedure.IN);
			storedDetalles.declareParameter("V_fecha_hoy", Utilerias.validarCadenaSQL(funciones.ponerFechaSola(new Date())), StoredProcedure.IN);
			storedDetalles.declareParameter("V_id_usuario", idUsuario, StoredProcedure.IN);
			//resultStored = 
			storedDetalles.execute();
		
			//sql.append("\n Select * from TMP_MOVIMIENTO_BIT "); 
			sql.append("\n Select distinct no_docto, no_empresa, importe_original,  ");
			sql.append("\n 			desc_estatus, no_factura, fec_valor,");
			sql.append("\n   		fec_valor_original, desc_cve_operacion, ");
			sql.append("\n 			importe, ");
			sql.append("\n 			id_divisa, id_forma_pago, desc_forma_pago, ");
			sql.append("\n 			desc_banco, id_chequera_benef, beneficiario, ");
			sql.append("\n 			concepto, no_folio_det, no_folio_mov, ");
			sql.append("\n 			no_cuenta, id_chequera, id_cve_operacion,");
			sql.append("\n 			id_forma_pago, id_banco_benef, id_caja,");
			sql.append("\n 			id_leyenda, id_estatus_mov, no_cliente, ");
			sql.append("\n 			tipo_cambio, origen_mov, solicita, ");
			sql.append("\n 			autoriza, observacion, lote_entrada, ");
			sql.append("\n 			desc_caja, agrupa1, agrupa2, id_rubro, ");
			sql.append("\n 			agrupa3, b_servicios, no_pedido, ");
			sql.append("\n 			importe_mn, nom_proveedor, ");
			sql.append("\n 			fec_propuesta, id_divisa_original, ");
			sql.append("\n 			nom_empresa, id_banco_pago, ");
			sql.append("\n 			id_chequera_pago, ");
			sql.append("\n 			desc_banco_pago, desc_grupo_cupo,");
			sql.append("\n 			dias, NumCheq_PagoMN, NumCheq_PagoDLS, ");
			sql.append("\n 			NumCheq_CruceMN, NumCheq_CruceDLS, ");
			sql.append("\n 			NumCheqEmp, NumCheqCte, id_servicio_be, id_contable, ");
			sql.append("\n 			equivale, no_cheque,usuario_uno, usuario_dos  ");
			sql.append("\n 			,fecha_contabilizacion, fec_operacion ");
			sql.append("\n 			,color, invoice_type, id_clabe, rfc, referencia_cte ");
			//sql.append("\n 			,color, invoice_type, id_clabe ");
			
			sql.append("\n ");
			//sql.append("\n FROM TMP_MOVIMIENTO_BIT "); 
			sql.append("\n FROM TMP_DETALLE_PROV ");
			
			sql.append("\n WHERE ID_USUARIO = "+idUsuario+" ");
			
			sql.append("\n ORDER BY NO_EMPRESA, BENEFICIARIO ");
			
	        System.out.println("Sacar detalle: " + sql.toString());
	        
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
				//	System.out.println("obser "+rs.getString("observacion"));
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
					cons.setBancoPago(rs.getString("desc_banco_pago"));
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
					cons.setIdClabe(rs.getString("id_clabe"));
					cons.setRfc(rs.getString("rfc"));
					cons.setReferenciaCte(rs.getString("referencia_cte"));
					
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:consultarDetalle");
			 
		}
		//System.out.println("observacion "+listDetalle.get(0).getObservacion());
		return listDetalle;
	}
	
	public List<SeleccionAutomaticaGrupoDto> seleccionarPropGral(int idGrupoEmpresa, int idGrupo, String cveControl){
		List<SeleccionAutomaticaGrupoDto> listDatos= new ArrayList<SeleccionAutomaticaGrupoDto>();
		String sql="";
		try{
			sql+= " SELECT s.*, (cupo_total - cupo_automatico) as propuesto ";
		    sql+= "\n	FROM seleccion_automatica_grupo s";
		    sql+= "\n	WHERE id_grupo_flujo = "+Utilerias.validarCadenaSQL(idGrupoEmpresa);
		    sql+= "\n	AND id_grupo = "+Utilerias.validarCadenaSQL(idGrupo);
		    sql+= "\n	AND cve_control = '"+Utilerias.validarCadenaSQL(cveControl)+"' ";
		    System.out.println("propuesta general "+sql);
		    listDatos= jdbcTemplate.query(sql, new RowMapper<SeleccionAutomaticaGrupoDto>(){
				public SeleccionAutomaticaGrupoDto mapRow(ResultSet rs, int idx) throws SQLException {
					SeleccionAutomaticaGrupoDto cons = new SeleccionAutomaticaGrupoDto();
					cons.setCveControl(rs.getString("cve_control"));//la consulta hace un * pero solo ocupamos 2 campos
					cons.setCupoTotal(rs.getDouble("cupo_total"));
					return cons;
				}});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:seleccionarPropGral");
		}
		return listDatos;
	}


	public int actualizarAutorizacionPop(String campo, String valor, int idGrupo, String cveControl){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.actualizarAutorizacionPop(campo, valor, idGrupo, cveControl);
	}


	public int selectCveCtrl(String cveControl,int noEmpresa) {
		StringBuffer sql = new StringBuffer();
		int resp = 0;
		
		try {
			sql.append("SELECT SUM(cont) as regs \n");
			sql.append("FROM(SELECT count(cve_control) AS cont \n");
			sql.append("	 FROM movimiento WHERE cve_control = '" + Utilerias.validarCadenaSQL(cveControl) + "' \n");
			sql.append("	 AND no_empresa = " +Utilerias.validarCadenaSQL(noEmpresa) + " \n");
			sql.append("	 And id_estatus_mov in ('P','T','K') \n");
			sql.append("	 UNION \n" );
			sql.append("	 SELECT count(cve_control) AS cont FROM hist_movimiento WHERE cve_control = '" +Utilerias.validarCadenaSQL(cveControl)+ "' \n");
			sql.append("	 UNION \n" );
			sql.append("	 SELECT count(cve_control) AS cont FROM hist_solicitud WHERE cve_control = '" + Utilerias.validarCadenaSQL(cveControl) + "' \n");
			sql.append("	 UNION \n" );
			sql.append("	 SELECT count(cve_control) AS cont FROM hist_mov_det WHERE cve_control = '" + Utilerias.validarCadenaSQL(cveControl) + "' ) X \n");
			
			//system.out.println("query Clave control: " + sql);
			
			resp = jdbcTemplate.queryForInt(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasDao, M:selectCveCtrl");
		}
		return resp;
	}


	public int confirmaEliminar(String cveControl, int noEmpresa) {
		StringBuffer sql = new StringBuffer();
		int resp = 0;
		
		try {
			sql.append("SELECT COUNT(cve_control) as res \n");
			sql.append("FROM seleccion_automatica_grupo \n");
			sql.append("WHERE cve_control = '" +Utilerias.validarCadenaSQL(cveControl) + "' \n");
			sql.append("AND id_grupo_flujo = " + Utilerias.validarCadenaSQL(noEmpresa) + " \n");
			//sql.append("AND cupo_total = 0.0");
			
			//system.out.println("confirmacion "+sql.toString());
			
			resp = jdbcTemplate.queryForInt(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasDao, M:confirmaEliminar");
		}
		return resp;
	}

	public int deletePropuesta(String cveControl) {
		StringBuffer sql = new StringBuffer();
		int resp = 0;
		
		try {
			sql.append("DELETE FROM seleccion_automatica_grupo WHERE cve_control = '" + Utilerias.validarCadenaSQL(cveControl) + "'");
			
			resp = jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasDao, M:deletePropuesta");
		}
		return resp;
	}

/*
	@Override
	public List<MovimientoDto> buscaChequeras(List<MovimientoDto> listDetProp) {
		return null;
	}
 */
	public List<ParamConsultaPropuestasDto> nombreUsuarios(String usr1, String usr2, String usr3) {
		StringBuffer sql = new StringBuffer();
		List<ParamConsultaPropuestasDto> listRes = new ArrayList<ParamConsultaPropuestasDto>();
		
		try {
			sql.append(" SELECT (coalesce(cc.nombre, '') + ' ' + coalesce(cc.paterno, '') + ' ' + coalesce(cc.materno,'')) as nombre \n");
			sql.append(" FROM cat_usuario cc \n");
			sql.append(" WHERE cc.no_usuario in (" + Utilerias.validarCadenaSQL(usr1) 
												    + ", " + Utilerias.validarCadenaSQL(usr2) 
												    + ", " + Utilerias.validarCadenaSQL(usr3) 
												    + ")\n");
			
			//system.out.println("saca nombre de autorizador: " + sql.toString());
			
			listRes = jdbcTemplate.query(sql.toString(), new RowMapper<ParamConsultaPropuestasDto>() {
				public ParamConsultaPropuestasDto mapRow(ResultSet rs, int idx) throws SQLException {
					ParamConsultaPropuestasDto cons = new ParamConsultaPropuestasDto();
					cons.setNomUsuarios(rs.getString("nombre"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasDao, M:nombreUsuarios");
		}
		return listRes;
	}


	/*Reporte de detallado de propuestas*/
	public List<Map<String, Object>> reporteDetalleCupos(PagosPropuestosDto dto){
		String sql = "";
		List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
		
		try {
			sql+= "  SELECT distinct m.no_docto, m.no_empresa, importe_original, ";
		    sql+= "\n      e.desc_estatus, m.no_factura, m.fec_valor, ";
		    sql+= "\n      m.fec_valor_original, cv.desc_cve_operacion, ";
		    sql+= "\n      m.importe AS importe, ";
		    sql+= "\n      m.id_divisa, m.id_forma_pago, fp.desc_forma_pago, ";
		    sql+= "\n      b.desc_banco, m.id_chequera_benef, m.beneficiario, ";
		    sql+= "\n      m.concepto, m.no_folio_det, m.no_folio_mov, ";
		    sql+= "\n      m.no_cuenta, m.id_chequera, cv.id_cve_operacion, ";
		    sql+= "\n      m.id_forma_pago, m.id_banco_benef, m.id_caja, ";
		    sql+= "\n      m.id_leyenda, m.id_estatus_mov, m.no_cliente, ";
		    sql+= "\n      m.tipo_cambio, m.origen_mov, m.solicita, ";
		    sql+= "\n      m.autoriza, m.observacion, m.lote_entrada, ";
		    sql+= "\n      cj.desc_caja, m.agrupa1, m.agrupa2, m.id_rubro, ";
		    sql+= "\n      m.agrupa3, p.b_servicios, m.no_pedido, ";
		    sql+= "\n      CASE WHEN m.id_divisa = 'MN' THEN m.importe ";
		    sql+= "\n           ELSE m.importe * coalesce( ";
		    sql+= "\n               ( SELECT max(vd.valor) ";
		    sql+= "\n                 FROM valor_divisa vd ";
		    sql+= "\n                 WHERE vd.id_divisa = m.id_divisa ";
		    sql+= "\n                     and vd.fec_divisa = '"
		    							  +Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFecha()))+"' ), ";
		    sql+= "\n           1) end as importe_mn, ";
		    sql+= "\n      ( SELECT max(per.nombre_corto) ";
		    sql+= "\n        FROM persona per ";
		    //oracle
	        sql+= "\n	 WHERE cast(m.no_cliente as integer) = per.no_persona ";
		    
		    sql+= "\n            and per.id_tipo_persona = 'P' ) as nom_proveedor, ";
		    sql+= "\n      m.fec_propuesta, m.id_divisa_original,  ";
		    sql+= "\n      ( SELECT max(e.nom_empresa) ";
		    sql+= "\n        FROM empresa e ";
		    sql+= "\n        WHERE e.no_empresa = m.no_empresa ) as nom_empresa, ";
		    sql+= "\n      m.id_banco as id_banco_pago, ";
		    sql+= "\n      m.id_chequera as id_chequera_pago, ";
		    sql+= "\n      ( SELECT max(cb.desc_banco) ";
		    sql+= "\n        FROM cat_banco cb ";
		    sql+= "\n        WHERE cb.id_banco = m.id_banco) as banco_pago, ";
		    sql+= "\n      ( SELECT max(cg.desc_grupo_cupo) ";
		    sql+= "\n        FROM cat_grupo_cupo cg ";
		    sql+= "\n        WHERE cg.id_grupo_cupo = ";
		    sql+= "\n            ( SELECT max(gfc.id_grupo_cupo) ";
		    sql+= "\n              FROM grupo_flujo_cupo gfc ";
		    sql+= "\n             WHERE gfc.id_rubro in ";
		    sql+= "\n                  ( SELECT max(mm.id_rubro) ";
		    sql+= "\n                    FROM movimiento mm ";
		    sql+= "\n                    WHERE mm.folio_ref = m.no_folio_det ) ) ) ";
		    sql+= "\n      as desc_grupo_cupo, ";
		    	

		    //oracle 
		    sql+= "\n      m.fec_valor_original - convert(char(10),'"
		    				+Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFecha()))
		    				+"', 103)+' '+convert(char(8),'"
		    				+Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFecha()))
		    				+"', 108) as dias,";
		    
		    sql+= "\n     ( SELECT coalesce(count(ccbv.id_divisa), 0) ";
		    sql+= "\n       FROM cat_cta_banco ccbv ";
		    sql+= "\n       Where ccbv.no_empresa = m.no_empresa ";
		    sql+= "\n           AND ccbv.id_divisa = 'MN' ) ";
		    sql+= "\n     as NumCheq_PagoMN, ";
		    
		    sql+= "\n     ( SELECT coalesce(count(ccbv.id_divisa), 0) ";
		    sql+= "\n       FROM cat_cta_banco ccbv ";
		    sql+= "\n       Where ccbv.no_empresa = m.no_empresa ";
		    sql+= "\n           AND ccbv.id_divisa = 'DLS' ) ";
		    sql+= "\n     as NumCheq_PagoDLS, ";

		    sql+= "\n      ( SELECT coalesce(count(cbpc.id_divisa), 0) ";
		    sql+= "\n        FROM ctas_banco cbpc ";
		    
		    //oracle
	        sql+="\n    WHERE cbpc.no_persona = cast(m.no_cliente as integer)";
		    
		    sql+="\n      and cbpc.id_divisa = 'MN' ) ";
		    sql+="\n      as NumCheq_CruceMN, ";
		    
		    sql+="\n      ( SELECT coalesce(count(cbpc.id_divisa), 0) ";
		    sql+="\n        FROM ctas_banco cbpc ";
		    
		    //Oracle
	        sql+= "    WHERE cbpc.no_persona = cast(m.no_cliente as integer)";
		    
		    sql+= "            and cbpc.id_divisa = 'DLS' ) ";
		    sql+= "      as NumCheq_CruceDLS, ";
		    
		    sql+= "      ( SELECT coalesce(count(c.id_chequera), 0) ";
		    sql+= "        FROM cat_cta_banco c ";
		    sql+= "        WHERE c.no_empresa = m.no_empresa ";
		    sql+= "            and c.id_divisa = m.id_divisa ) as NumCheqEmp, ";
		              
		    sql+= "      ( SELECT coalesce(count(b.id_chequera), 0) ";
		    sql+= "        FROM ctas_banco b ";
		    sql+= "        WHERE b.no_persona = m.no_cliente ";
		    sql+= "            and b.id_divisa = m.id_divisa ) as NumCheqCte, m.id_servicio_be, m.id_contable ";
		    
		    sql+= "        ,(select equivale_persona from persona p where p.no_persona = m.no_cliente and p.id_tipo_persona = 'P') as equivale,";
		    sql+= "       coalesce(m.no_cheque, 0) as no_cheque ";
		    
		    sql+= "  FROM movimiento m ";
		    sql+= "      LEFT JOIN cat_banco b ON ( m.id_banco_benef = b.id_banco )";
		    sql+= "      LEFT JOIN cat_estatus e ON (m.id_estatus_mov = e.id_estatus ";
		    sql+= "          AND e.clasificacion = 'MOV' ) ";
		    sql+= "      LEFT JOIN cat_caja cj ON (m.id_caja = cj.id_caja) ";
		    
		    //oracle
	        sql+="\n	  LEFT JOIN proveedor p ON (cast(m.no_cliente as integer) = p.no_proveedor), ";
        	
		    sql+="\n      cat_tipo_operacion co, cat_cve_operacion cv, ";
		    sql+="\n      cat_forma_pago fp ";
		    
		    if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
		    	sql+= "   ,cat_cta_banco_division ccbd ";
	    
	        sql+= "\n  WHERE ";
	    
		    if(dto.getIdGrupoEmpresa()>0)
		    {
		        sql+= "  m.no_empresa in (select no_empresa from grupo_empresa ";
		        sql+= "\n                   where id_grupo_flujo = "+Utilerias.validarCadenaSQL(dto.getIdGrupoEmpresa())+") ";
		    }
		    
		    sql+= "\n	  AND m.no_cliente not in (" + Utilerias.validarCadenaSQL(consultarConfiguraSet(206))+") ";
		    sql+= "\n	  AND m.id_tipo_movto = 'E'  ";
		    sql+= "\n	  AND (m.origen_mov <> 'INV' or m.origen_mov is NULL)";
		    sql+= "\n	  AND m.id_tipo_operacion = co.id_tipo_operacion ";
		    sql+= "\n	  AND co.no_empresa = 1 ";
		    sql+= "\n	  AND m.id_cve_operacion = cv.id_cve_operacion ";
		    sql+= "\n	  AND m.id_estatus_mov in ('N','C','F','L') ";
		    sql+= "\n	  AND m.id_forma_pago = fp.id_forma_pago ";
		    sql+= "\n	  AND m.id_forma_pago in (1,3,5,6,7,9) ";
		    sql+= "\n	  AND m.no_folio_det in ";
		    sql+= "\n	      ( SELECT mm.folio_ref ";
		    sql+= "\n	        FROM movimiento mm ";
		    sql+= "\n	        WHERE mm.folio_ref = m.no_folio_det ) ";
		    
		    sql+= "\n		  And m.cve_control = '"+Utilerias.validarCadenaSQL(dto.getCveControl())+"' ";

		    if(dto.getPsDivision()!=null && !dto.getPsDivision().equals("")) 
		    {
		        sql+= "\n	  AND m.no_empresa = ccbd.no_empresa ";
		        sql+= "\n	  AND m.id_chequera = ccbd.id_chequera ";
		        sql+= "\n	  AND m.id_banco = ccbd.id_banco ";
		        sql+= "\n	  AND m.division = ccbd.id_division ";
		    }
		   
	        if(dto.getPdOrigenMov()!=null && !dto.getPdOrigenMov().equals(""))
	            sql+= "\n  AND m.origen_mov='"+Utilerias.validarCadenaSQL(dto.getPdOrigenMov())+"'";
	        
	        sql+= "\n	  UNION ALL ";
            
	        sql+= "\n	  SELECT distinct m.no_docto, m.no_empresa, importe_original, ";
	        sql+= "\n      e.desc_estatus, m.no_factura, m.fec_valor, ";
	        sql+= "\n      m.fec_valor_original, cv.desc_cve_operacion, ";
	        sql+= "\n      m.importe AS importe, ";
	        sql+= "\n      m.id_divisa, m.id_forma_pago, fp.desc_forma_pago, ";
	        sql+= "\n      b.desc_banco, m.id_chequera_benef, m.beneficiario, ";
	        sql+= "\n      m.concepto, m.no_folio_det, m.no_folio_mov, ";
	        sql+= "\n      m.no_cuenta, m.id_chequera, cv.id_cve_operacion, ";
	        sql+= "\n      m.id_forma_pago, m.id_banco_benef, m.id_caja, ";
	        sql+= "\n      m.id_leyenda, m.id_estatus_mov, m.no_cliente, ";
	        sql+= "\n      m.tipo_cambio, m.origen_mov, m.solicita, ";
	        sql+= "\n      m.autoriza, m.observacion, m.lote_entrada, ";
	        sql+= "\n      cj.desc_caja, m.agrupa1, m.agrupa2, m.id_rubro, ";
	        sql+= "\n      m.agrupa3, '' as b_servicios, m.no_pedido, ";
	        sql+= "\n      CASE WHEN m.id_divisa = 'MN' THEN m.importe ";
	        sql+= "\n           ELSE m.importe * coalesce( ";
	        sql+= "\n              ( SELECT DISTINCT vd.valor ";
	        sql+= "\n                 FROM valor_divisa vd ";
	        sql+= "\n                 WHERE vd.id_divisa = m.id_divisa ";
	        sql+= "\n                     and vd.fec_divisa = '"
	        							 +Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFecha()))+"' ), ";
	        sql+= "\n           1) end as importe_mn, ";
	        sql+= "\n      'PROVEEDOR INCIDENTAL' as nom_proveedor, ";
	        sql+= "\n      m.fec_propuesta, m.id_divisa_original,  ";
	        sql+= "\n      ( SELECT e.nom_empresa ";
	        sql+= "\n        FROM empresa e ";
	        sql+= "\n        WHERE e.no_empresa = m.no_empresa ) as nom_empresa, ";
	        sql+= "\n      m.id_banco as id_banco_pago, ";
	        sql+= "\n      m.id_chequera as id_chequera_pago, ";
	        sql+= "\n      ( SELECT cb.desc_banco ";
	        sql+= "\n        FROM cat_banco cb ";
	        sql+= "\n        WHERE cb.id_banco = m.id_banco) as banco_pago, ";
	        sql+= "\n      ( SELECT cg.desc_grupo_cupo ";
	        sql+= "\n        FROM cat_grupo_cupo cg ";
	        sql+= "\n        WHERE cg.id_grupo_cupo = ";
	        sql+= "\n           ( SELECT gfc.id_grupo_cupo ";
	        sql+= "\n              FROM grupo_flujo_cupo gfc ";
	        sql+= "\n              WHERE gfc.id_rubro in ";
	        sql+= "\n                  ( SELECT mm.id_rubro ";
	        sql+= "\n                    FROM movimiento mm ";
	        sql+= "\n                    WHERE mm.folio_ref = m.no_folio_det ) ) ) ";
	        sql+= "\n      as desc_grupo_cupo, ";

	        //TO_DATE -> oracle
	        sql+= "\n      convert(char(10),m.fec_valor_original, 103)+' '+convert(char(8),m.fec_valor_original, 108)  - convert(char(10),'"
	        				+Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFecha()))
	        				+"', 103)+' '+convert(char(8),'"
	        				+Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFecha()))
	        				+"', 108) as dias,";
	        
	        sql+= "\n     ( SELECT coalesce(count(ccbv.id_divisa), 0) ";
	        sql+= "\n       FROM cat_cta_banco ccbv ";
	        sql+= "\n       Where ccbv.no_empresa = m.no_empresa ";
	        sql+= "\n           AND ccbv.id_divisa = 'MN' ) ";
	        sql+= "     as NumCheq_PagoMN, ";
	        
	        sql+= "\n     ( SELECT coalesce(count(ccbv.id_divisa), 0) ";
	        sql+= "\n      FROM cat_cta_banco ccbv ";
	        sql+= "\n       Where ccbv.no_empresa = m.no_empresa ";
	        sql+= "\n           AND ccbv.id_divisa = 'DLS' ) ";
	        sql+= "     as NumCheq_PagoDLS, ";

	        sql+= "\n      ( SELECT coalesce(count(cbpc.id_divisa), 0) ";
	        sql+= "\n        FROM ctas_banco cbpc ";
	        
	        //oracle
        	sql+= "\n    WHERE cbpc.no_persona = cast(m.no_cliente as integer)";
	        
	        sql+="\n            and cbpc.id_divisa = 'MN' ) ";
	        sql+="\n      as NumCheq_CruceMN, ";
	        
	        sql+="\n      ( SELECT coalesce(count(cbpc.id_divisa), 0) ";
	        sql+="\n        FROM ctas_banco cbpc ";
	        
	        //Oracle
	        sql+="\n    WHERE cbpc.no_persona = cast(m.no_cliente as integer) ";
	        
	        sql+="\n            and cbpc.id_divisa = 'DLS' ) ";
	        sql+="          as NumCheq_CruceDLS, ";
	        
	        sql+="\n      ( SELECT coalesce(count(c.id_chequera), 0) ";
	        sql+="\n        FROM cat_cta_banco c ";
	        sql+="\n        WHERE c.no_empresa = m.no_empresa ";
	        sql+="\n            and c.id_divisa = m.id_divisa ) as NumCheqEmp, ";
	                  
	        sql+="\n      ( SELECT coalesce(count(b.id_chequera), 0) ";
	        sql+="\n        FROM ctas_banco b ";
	        sql+="\n        WHERE b.no_persona = m.no_cliente ";
	        sql+="\n            and b.id_divisa = m.id_divisa ) as NumCheqCte, m.id_servicio_be, m.id_contable ";
	        
	        sql+="\n  ,(select equivale_persona from persona p where p.no_persona = m.no_cliente and p.id_tipo_persona = 'P') as equivale,";
	        sql+="\n  coalesce(m.no_cheque, 0) as no_cheque";
	        
	        sql+="\n  FROM movimiento m ";
	        sql+="\n      LEFT JOIN cat_banco b ON ( m.id_banco_benef = b.id_banco )";
	        sql+="\n      LEFT JOIN cat_estatus e ON (m.id_estatus_mov = e.id_estatus ";
	        sql+="\n          AND e.clasificacion = 'MOV' ) ";
	        sql+="\n      LEFT JOIN cat_caja cj ON (m.id_caja = cj.id_caja), ";
	        sql+="\n      cat_tipo_operacion co, cat_cve_operacion cv, ";
	        sql+="\n      cat_forma_pago fp ";
	        
	        if(dto.getPsDivision()!=null && !dto.getPsDivision().equals("")) 
	        	sql+= "\n   ,cat_cta_banco_division ccbd ";
	    
        	sql+= "\n  WHERE ";
	    
		    if(dto.getIdGrupoEmpresa() > 0) {
		        sql+= "\n  m.no_empresa in (select no_empresa from grupo_empresa ";
		        sql+= "\n                   where id_grupo_flujo = "+Utilerias.validarCadenaSQL(dto.getIdGrupoEmpresa())+") and ";
		    }
		    sql+= "\n  m.no_cliente in ("+Utilerias.validarCadenaSQL(consultarConfiguraSet(206))+") ";
		    sql+= "\n  AND m.id_tipo_movto = 'E'  ";
		    sql+= "\n  AND (m.origen_mov <> 'INV' or m.origen_mov is null)";
		    sql+= "\n  AND m.id_tipo_operacion = co.id_tipo_operacion ";
		    sql+= "\n  AND co.no_empresa = 1 ";
		    sql+= "\n  AND m.id_cve_operacion = cv.id_cve_operacion ";
		    sql+= "\n  AND m.id_estatus_mov in ('N','C','F','L') ";
		    sql+= "\n  AND m.id_forma_pago = fp.id_forma_pago ";
		    sql+= "\n  AND m.id_forma_pago in (1,3,5,7,9)";
		    sql+= "\n  AND m.no_folio_det in ";
		    sql+= "\n      ( SELECT mm.folio_ref ";
		    sql+= "\n       FROM movimiento mm ";
		    sql+= "\n        WHERE mm.folio_ref = m.no_folio_det ) ";
		    
		    sql+= "\n  And m.cve_control = '"+Utilerias.validarCadenaSQL(dto.getCveControl())+"' ";
		    
		    if(dto.getPsDivision()!=null && !dto.getPsDivision().equals(""))
		    {
		        sql+= "\n  AND m.no_empresa = ccbd.no_empresa ";
		        sql+= "\n  AND m.id_chequera = ccbd.id_chequera ";
		        sql+= "\n  AND m.id_banco = ccbd.id_banco ";
		        sql+= "\n  AND m.division = ccbd.id_division ";
		    }
		    if(dto.getPdOrigenMov()!=null && !dto.getPdOrigenMov().equals("0"))
		            sql+= "\n	  AND m.origen_mov='"+Utilerias.validarCadenaSQL(dto.getPdOrigenMov())+"'";
	        sql+= "\n  order by no_cheque";
	        
	        //system.out.println("query de reporte: " + sql);
			
	        listResult = jdbcTemplate.query(sql, new RowMapper<Map<String, Object>>() {
				public Map<String, Object> mapRow(ResultSet rs, int idx) throws SQLException {
					Map<String, Object> cons = new HashMap<String, Object>();
					cons.put("noDocto", rs.getString("no_docto"));
					cons.put("noEmpresa", rs.getInt("no_empresa"));
					cons.put("importeOriginal", rs.getDouble("importe_original"));
					cons.put("descEstatus", rs.getString("desc_estatus"));
					cons.put("noFactura", rs.getString("no_factura"));
					cons.put("fecValor", rs.getDate("fec_valor"));
					cons.put("fecValorOriginal", rs.getDate("fec_valor_original"));
					cons.put("descCveOperacion", rs.getString("desc_cve_operacion"));
					cons.put("importe", rs.getDouble("importe"));
					cons.put("idDivisa", rs.getString("id_divisa"));
					cons.put("idFormaPago", rs.getInt("id_forma_pago"));
					cons.put("descFormaPago", rs.getString("desc_forma_pago"));
					cons.put("descBancoBenef", rs.getString("desc_banco"));//Descripcion banco beneficiario
					cons.put("idChequeraBenef", rs.getString("id_chequera_benef"));
					cons.put("beneficiario", rs.getString("beneficiario"));
					cons.put("concepto", rs.getString("concepto"));
					cons.put("noFolioDet", rs.getInt("no_folio_det"));
					cons.put("noFolioMov", rs.getInt("no_folio_mov"));
					cons.put("noCuenta", rs.getInt("no_cuenta"));
					cons.put("idChequera", rs.getString("id_chequera"));
					cons.put("idCveOperacion", rs.getInt("id_cve_operacion"));
					cons.put("idFormaPago", rs.getInt("id_forma_pago"));
					cons.put("idBancoBenef", rs.getInt("id_banco_benef"));
					cons.put("idCaja", rs.getInt("id_caja"));
					cons.put("idLeyenda", rs.getString("id_leyenda"));
					cons.put("idEstatusMov", rs.getString("id_estatus_mov"));
					cons.put("noCliente", rs.getString("no_cliente"));
					cons.put("tipoCambio", rs.getDouble("tipo_cambio"));
					cons.put("origenMov", rs.getString("origen_mov"));
					cons.put("solicita", rs.getString("solicita"));
					cons.put("autoriza", rs.getString("autoriza"));
					cons.put("observacion", rs.getString("observacion"));
					cons.put("loteEntrada", rs.getInt("lote_entrada"));
					cons.put("descCaja", rs.getString("desc_caja"));
					cons.put("agrupa1", rs.getInt("agrupa1"));
					cons.put("agrupa2", rs.getInt("agrupa2"));
					cons.put("idRubro", rs.getDouble("id_rubro"));
					cons.put("agrupa3", rs.getInt("agrupa3"));
					cons.put("bServicios", rs.getString("b_servicios"));
					cons.put("noPedido", rs.getInt("no_pedido"));
					cons.put("importeMn", rs.getDouble("importe_mn"));
					cons.put("nomProveedor", rs.getString("nom_proveedor"));
					cons.put("fecPropuesta", rs.getDate("fec_propuesta"));
					cons.put("idDivisaOriginal", rs.getString("id_divisa_original"));
					cons.put("nomEmpresa", rs.getString("nom_empresa"));
					cons.put("idBanco", rs.getInt("id_banco_pago"));
					cons.put("idChequera", rs.getString("id_chequera_pago"));
					cons.put("bancoPago", rs.getString("banco_pago"));
					cons.put("descGrupoCupo", rs.getString("desc_grupo_cupo")); 
					cons.put("diasInv", rs.getInt("dias"));
					cons.put("numCheqPagoMN", rs.getInt("NumCheq_PagoMN"));
					cons.put("numCheqPagoDLS", rs.getInt("NumCheq_PagoDLS"));	
					cons.put("numCheqCruceMN", rs.getInt("NumCheq_CruceMN"));
					cons.put("numCheqCruceDLS", rs.getInt("NumCheq_CruceDLS"));
					cons.put("numCheqEmp", rs.getInt("NumCheqEmp"));
					cons.put("numCheqCte", rs.getInt("NumCheqCte"));
					cons.put("idServicioBe", rs.getString("id_servicio_be"));
					cons.put("idContable", rs.getString("id_contable"));
					cons.put("equivale", rs.getString("equivale"));
					cons.put("noCheque", rs.getInt("no_cheque"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasDao, M:reporteDetalleCupos");
		}
		return listResult;
	}

	//Modificado EMS 07/10/15
	//Querys para Cancelacion de detalle de propuesta	
	public int actualizaPropuesta(String foliosCancela, String fecHoy, int usuario, String cveControl, int bandera){
		String sql = "";
		int respuestaEntera = 0;
		try{
			sql = "Update movimiento SET ";
			sql += "\n fec_propuesta = NULL, ";
			sql += "\n cve_control = NULL, ";
			sql += "\n fec_valor = fec_valor_original, ";
			sql += "\n fec_modif = '"+ Utilerias.validarCadenaSQL(fecHoy) +"', " ;
			/***** Agregado Ems *****/
			sql += "\n id_chequera_benef = '', " ;
			sql += "\n id_banco_benef = '', " ;
			sql += "\n referencia = '', " ;
			/************************/
			
			if (bandera == 0){				
				sql += "\n id_servicio_be = '', ";
			}
			else{							
				sql += "\n id_banco = 0, ";
				sql += "\n id_chequera = '', ";			
			}
			
			sql += "\n usuario_modif = "+ Utilerias.validarCadenaSQL(usuario) +", ";
			sql += "\n contrarecibo = '"+ Utilerias.validarCadenaSQL(cveControl) +"' ";
			sql += "\n WHERE (no_folio_det in ("+foliosCancela+")) ";
//			sql += "\n OR folio_ref in ("+ foliosCancela +"))";
			
			if (bandera == 1)
				sql += "\n and id_tipo_operacion = 3801 ";
						
			respuestaEntera = jdbcTemplate.update(sql);	
		}
		catch(Exception e){			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: actualizaPropuesta");			
		}return respuestaEntera;
	}


	public List<ParamConsultaPropuestasDto> buscaRegistroBitacora(String foliosCancela){
		List<ParamConsultaPropuestasDto> listaResultado = new ArrayList<ParamConsultaPropuestasDto>();
		String sql = "";
		try{
			sql = "SELECT no_empresa, no_docto, invoice_type ";
			sql += "\n FROM movimiento ";
			sql += "\n where no_folio_det in ("+ foliosCancela +") ";
			sql += "\n and id_tipo_operacion = 3000 ";
						
			listaResultado = jdbcTemplate.query(sql, new RowMapper<ParamConsultaPropuestasDto>(){
				public ParamConsultaPropuestasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ParamConsultaPropuestasDto campos = new ParamConsultaPropuestasDto();
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setNoDocto(rs.getString("no_docto"));
					campos.setInvoice(rs.getString("invoice_type"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: buscaRegistrosBitacora");
		}return listaResultado;
	}


	public int insertaLogPropuestas(int noEmpresa, String noDocto, String invoice, String concepto, int usuario, String fecha, String hora){
		int resultado = 0;
		String sql = "";
		try{
			sql = " Insert into log_propuesta_egresos (no_empresa,no_docto,invoice_type,no_modificacion,desc_modificacion,no_usuario,fecha,hora) ";
			sql += "\n Select "+ Utilerias.validarCadenaSQL(noEmpresa) 
							  +", '"+ Utilerias.validarCadenaSQL(noDocto) 
							  +"', '"+ Utilerias.validarCadenaSQL(invoice) +"', ";
			sql += "\n (SELECT case when max(coalesce(no_modificacion,0)) + 1 is null then 1 else max(coalesce(no_modificacion,0)) +1 end ";
			sql += "\n From log_propuesta_egresos ";
			sql += "\n where no_empresa = "+ Utilerias.validarCadenaSQL(noEmpresa) +" ";
			sql += "\n and no_docto ='"+ Utilerias.validarCadenaSQL(noDocto) +"' ";
			sql += "\n and invoice_type = '"+ Utilerias.validarCadenaSQL(invoice) +"'), ";
			sql += "\n '"+ Utilerias.validarCadenaSQL(concepto) 
						+"', "+ Utilerias.validarCadenaSQL(usuario) 
						+", '"+ Utilerias.validarCadenaSQL(fecha) 
						+"', '"+ Utilerias.validarCadenaSQL(hora) +"' ";
			sql += "\n from fechas ";
			System.out.println("rrubio"+sql.toString());
			resultado = jdbcTemplate.update(sql);			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: insertaLogPropuestas");
		}return resultado;
	}


	public List<ParamConsultaPropuestasDto> buscaMovimiento(String foliosCancela){
		List<ParamConsultaPropuestasDto> listaResultado = new ArrayList<ParamConsultaPropuestasDto>();
		String sql = "";
		try{
			sql = "Select no_empresa, no_folio_det, id_banco, id_chequera, no_cheque, ";
			sql += "\n importe, id_estatus_mov, id_divisa, coalesce(usuario_modif, 0) as usuario_modif, ";
			sql += "\n id_caja, beneficiario, concepto, no_docto, fec_cheque ";
			sql += "\n from movimiento ";
			sql += "\n where no_folio_det in ("+ foliosCancela +")"; 
			//									+") or folio_ref in ("+ foliosCancela +") ";
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper<ParamConsultaPropuestasDto>(){
				public ParamConsultaPropuestasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ParamConsultaPropuestasDto campos = new ParamConsultaPropuestasDto();
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setNoFolioDet(rs.getInt("no_folio_det"));
					campos.setIdBanco(rs.getInt("id_banco"));
					campos.setIdChequera(rs.getString("id_chequera"));
					campos.setNoCheque(rs.getInt("no_cheque"));
					campos.setImporte(rs.getDouble("importe"));
					campos.setIdEstatusMov(rs.getString("id_estatus_mov"));
					campos.setIdDivisa(rs.getString("id_divisa"));
					campos.setUsuarioModif(rs.getInt("usuario_modif"));
					campos.setIdCaja(rs.getInt("id_caja"));
					campos.setBeneficiario(rs.getString("beneficiario"));
					campos.setConcepto(rs.getString("concepto"));
					campos.setNoDocto(rs.getString("no_docto"));
					campos.setFecCheque(rs.getString("fec_cheque"));
					return campos;
				}
			});			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: buscaMovimiento");
		}return listaResultado;
	}


	public int insertaBitacoraCheque(ParamConsultaPropuestasDto objPropuesta){
		int resultado = 0;
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("INSERT INTO bitacora_cheques (no_empresa, no_folio_det, id_banco, ");
			sql.append("\n id_chequera, no_cheque, importe, id_estatus, id_divisa, ");
			sql.append("\n usuario, causa, fecha_bitacora, id_caja, beneficiario, ");
			sql.append("\n concepto, no_docto, fec_cheque) ");
			sql.append("\n values( ");
			sql.append("\n "+ Utilerias.validarCadenaSQL(objPropuesta.getNoEmpresa()) 
							+", "+ Utilerias.validarCadenaSQL(objPropuesta.getNoFolioDet()) 
							+", "+ Utilerias.validarCadenaSQL(objPropuesta.getIdBanco()) +", ");
			sql.append("\n '"+ Utilerias.validarCadenaSQL(objPropuesta.getIdChequera()) 
							+"', "+ Utilerias.validarCadenaSQL(objPropuesta.getNoCheque()) 
							+", "+ Utilerias.validarCadenaSQL(objPropuesta.getImporte()) +",");
			sql.append("\n '"+ Utilerias.validarCadenaSQL(objPropuesta.getIdEstatusMov()) 
							+"', '"+ Utilerias.validarCadenaSQL(objPropuesta.getIdDivisa()) 
							+"', "+ Utilerias.validarCadenaSQL(objPropuesta.getUsuarioModif()) +", ");
			sql.append("\n "+ Utilerias.validarCadenaSQL(objPropuesta.getConcepto()) 
							+", '"+ Utilerias.validarCadenaSQL(objPropuesta.getNoDocto()) 
							+"', '"+ Utilerias.validarCadenaSQL(objPropuesta.getFecCheque()) +"')");
			System.out.println("rrubio"+sql.toString());
			resultado = jdbcTemplate.update(sql.toString());
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: insertaBitacoraCheques");
		}return resultado;
	}


	public int actualizaNoCheque(ParamConsultaPropuestasDto objPropuesta){
		int respuesta = 0;
		List<ParamConsultaPropuestasDto> listaResultado = new ArrayList<ParamConsultaPropuestasDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("Select no_folio_mov from movimiento where no_folio_det in ("+ Utilerias.validarCadenaSQL(objPropuesta.getNoFolioDet()) +")");
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<ParamConsultaPropuestasDto>(){
				public ParamConsultaPropuestasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ParamConsultaPropuestasDto campos = new ParamConsultaPropuestasDto();
					campos.setNoFolioMov(rs.getInt("no_folio_mov"));
					return campos;
				}
			});
			
			if (listaResultado.size() > 0){				
				sql = new StringBuffer();
				sql.append("UPDATE movimiento");
				sql.append("\n SET no_cheque = 0");
				sql.append("\n WHERE no_docto = '"+ Utilerias.validarCadenaSQL(objPropuesta.getNoDocto()) +"' ");
				sql.append("\n AND no_folio_det in ("+ objPropuesta.getNoFolioDet() 
													 +") or no_folio_mov in ("+ listaResultado.get(0).getNoFolioMov() +")");
				
				respuesta = jdbcTemplate.update(sql.toString());
			}			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: actualizaNoCheque");
		}return respuesta;
	}
	


	public List<ParamConsultaPropuestasDto> buscaSolicitudACancelar (String foliosCancela){
		List<ParamConsultaPropuestasDto> listaResultado = new ArrayList<ParamConsultaPropuestasDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("Select origen_mov,no_folio_det,id_tipo_operacion,id_estatus_mov,id_forma_pago, ");
			sql.append("\n b_entregado,id_tipo_movto,no_empresa,no_cuenta,id_chequera,id_banco,no_docto, ");
			sql.append("\n lote_entrada,b_salvo_buen_cobro,fec_conf_trans,id_divisa, importe ");
			sql.append("\n from movimiento");
			sql.append("\n where id_tipo_operacion = 3000 ");
			sql.append("\n and id_estatus_mov <> 'X' ");
			sql.append("\n and no_folio_det in ("+ foliosCancela +") ");
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<ParamConsultaPropuestasDto>(){
				public ParamConsultaPropuestasDto mapRow(ResultSet rs, int idx)throws SQLException{
					ParamConsultaPropuestasDto campos = new ParamConsultaPropuestasDto();
					campos.setOrigenMov(rs.getString("origen_mov"));
					campos.setNoFolioDet(rs.getInt("no_folio_det"));
					campos.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
					campos.setIdEstatusMov(rs.getString("id_estatus_mov"));
					campos.setIdFormaPago(rs.getInt("id_forma_pago"));
					campos.setBEntregado(rs.getString("b_entregado"));
					campos.setIdTipoMovto(rs.getString("id_tipo_movto"));
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					campos.setNoCuenta(rs.getInt("no_cuenta"));
					campos.setIdChequera(rs.getString("id_chequera"));
					campos.setIdBanco(rs.getInt("id_banco"));
					campos.setNoDocto(rs.getString("no_docto"));
					campos.setLoteEntrada(rs.getInt("lote_entrada"));
					campos.setBSBC(rs.getString("b_salvo_buen_cobro"));
					campos.setFecConfTrans(rs.getString("fec_conf_trans"));
					campos.setIdDivisa(rs.getString("id_divisa"));
					campos.setImporte(rs.getDouble("importe"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: buscaSolicitudACancelar");
		}return listaResultado;
	}
	

	public Map<String, Object> ejecutarRevividor(ParamConsultaPropuestasDto objPro){
		Map<String, Object> resultado = new HashMap<String, Object>();
		int res = 0;
		
		try{						
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			
			res = consultasGenerales.ejecutarRevividorOR(objPro.getTipoRevividor(), objPro.getNoFolioDet(), objPro.getIdTipoOperacion(),
			objPro.getTipoCancelacion(), objPro.getIdEstatusMov(), objPro.getOrigenMov(), objPro.getIdFormaPago(), 
			objPro.getBEntregado(), objPro.getIdTipoMovto(), objPro.getImporte(), objPro.getNoEmpresa(), 
			objPro.getNoCuenta(), objPro.getIdChequera(), objPro.getIdBanco(), objPro.getUsuarioModif(), 
			objPro.getNoDocto(), objPro.getLoteEntrada(), objPro.getBSBC(), objPro.getFecConfTrans(), 
			objPro.getIdDivisa(), objPro.getResultado(), false);
			
			if(res > 0)
				resultado.put("mensage", "Registro Eliminado corretamente!!");
			else
				resultado.put("mensage", "Error al tratar de eliminar el registro!!");
			
			return resultado;
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: ejecutarRevividor");
		}return resultado;
	}


	public boolean verificaEstatusProp(String cveControl) {
		StringBuffer sql = new StringBuffer();
		int res;
		
		try {
			sql.append(" select COUNT(*) from  \n");
			sql.append(" 	(SELECT count(*) as x FROM movimiento m \n");
			sql.append(" WHERE m.cve_control = '"+ Utilerias.validarCadenaSQL(cveControl) +"'  \n");
			sql.append(" 		And m.id_estatus_mov in ('P', 'T', 'K') \n"); 
			sql.append(" 		And m.id_tipo_operacion = 3200) x,  \n");
			sql.append(" 	(SELECT count(*) as x1 FROM hist_movimiento hm \n");
			sql.append(" 	 WHERE hm.cve_control = '"+ Utilerias.validarCadenaSQL(cveControl) +"'  \n");
			sql.append(" 		And hm.id_estatus_mov in ('P', 'T', 'K')  \n");
			sql.append(" 		And hm.id_tipo_operacion = 3200) x1  \n");
			sql.append(" where x > 0 or x1>0 \n");
			
			System.out.println("verificaEstatusProp "+sql);
			res = jdbcTemplate.queryForInt(sql.toString());
			
			if(res != 0) return true;
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: ejecutarRevividor");
		}
		return false;
	}


	public int updateMontosCupo(String cveControl, double importeResta) {
		int resultado = 0;
		StringBuffer sql = new StringBuffer();		
		
		try {
			sql.append(" UPDATE seleccion_automatica_grupo ");
			sql.append("\n	SET monto_maximo = " + importeResta + ", ");
			sql.append("\n		cupo_automatico =  " + importeResta + ", ");
			sql.append("\n		cupo_total =  " + importeResta + "");
			sql.append("\n WHERE cve_control = '" + Utilerias.validarCadenaSQL(cveControl) + "' ");
			
			resultado = jdbcTemplate.update(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: updateMontosCupo");
		}return resultado;
	}


	public int updateMontosAut(boolean suma, double importe, String cveControl,double importeS) {
		int resultado = 0;
		StringBuffer sql = new StringBuffer();
		String sMasMenos;
		
		try {
			if(suma)
				sMasMenos = "+";
			else
				sMasMenos = "-";
			
			sql.append(" UPDATE seleccion_automatica_grupo ");
			sql.append("\n	SET cupo_automatico = cupo_automatico " + sMasMenos + importe);
			sql.append("\n		, monto_maximo = monto_maximo " + sMasMenos + importe);
			sql.append("\n		, cupo_total = cupo_total " + sMasMenos + importe);
			sql.append("\n WHERE cve_control = '" + Utilerias.validarCadenaSQL(cveControl) + "' ");
			
			resultado = jdbcTemplate.update(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: updateMontosAut");
		}return resultado;
	}


	public int updatePropSinRech(String cveControl) {
		int resultado = 0;
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" UPDATE seleccion_automatica_grupo ");
			sql.append("\n	SET motivo_rechazo = '' ");
			sql.append("\n WHERE cve_control = '" + Utilerias.validarCadenaSQL(cveControl) + "' ");
			
			resultado = jdbcTemplate.update(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: updatePropSinRech");
		}return resultado;
	}


	public List<Map<String, Object>> reporteConSelecAut(ParamConsultaPropuestasDto dtoIn) {
		String sql="";
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		try {
            sql+= "SELECT sa.cve_control, sa.id_grupo_flujo, sa.cupo_total - sa.cupo_automatico as propuesto, ";
            sql+= "\n       cupo_total, c.desc_grupo_cupo, sa.fec_limite_selecc, sa.fecha_propuesta, sa.id_grupo, f.desc_grupo_flujo, ";
            sql+= "\n       coalesce(sa.concepto,'') as concepto, sa.usuario_uno, coalesce((select clave_usuario from seg_usuario where id_usuario = sa.usuario_uno), '') as user1, sa.usuario_dos, ";
            sql+= "\n       coalesce((select clave_usuario from seg_usuario where id_usuario = sa.usuario_dos), '') as user2, sa.usuario_tres, coalesce((select clave_usuario from seg_usuario where id_usuario = sa.usuario_tres), '') as user3, f.nivel_autorizacion, sa.id_division, ";
            sql+= "\n       ( SELECT count(*) ";
            sql+= "\n         FROM movimiento m ";
            sql+= "\n         WHERE m.id_estatus_mov = 'L' ";
            sql+= "\n             AND m.id_tipo_movto = 'E' ";
            sql+= "\n             AND m.id_tipo_operacion between 3800 and 3899 ";
            sql+= "\n             AND m.cve_control = sa.cve_control ";
            sql+= "\n       ) as NumIntercos, ";
            sql+= "\n       ( SELECT sum(m.importe) ";
            sql+= "\n         FROM movimiento m ";
            sql+= "\n         WHERE m.id_estatus_mov = 'L' ";
            sql+= "\n             AND m.id_tipo_movto = 'E' ";
            sql+= "\n             AND m.id_tipo_operacion between 3800 and 3899 ";
            sql+= "\n             AND m.cve_control = sa.cve_control ";
            sql+= "\n       ) as TotalIntercos";
            
            if(gsDBM.trim().equals("SQL SERVER") || gsDBM.trim().equals("SYBASE"))  
            {
//               sql+= "\n  FROM seleccion_automatica_grupo sa, cat_grupo_cupo c, cat_grupo_flujo f ";
//               sql+= "\n WHERE sa.id_grupo *= c.id_grupo_cupo ";
//               sql+= "\n   AND sa.id_grupo_flujo *= f.id_grupo_flujo ";
            	sql+= "\n  FROM seleccion_automatica_grupo sa";
                sql+= "\n LEFT JOIN cat_grupo_cupo c ON (sa.id_grupo=c.id_grupo_cupo)";
                sql+= "\n LEFT JOIN cat_grupo_flujo f ON (sa.id_grupo_flujo = f.id_grupo_flujo) ";
            }
            else if(gsDBM.trim().equals("POSTGRESQL") || gsDBM.trim().equals("DB2")) 
            {
               sql+= "\n  FROM seleccion_automatica_grupo sa ";
               sql+= "\n       LEFT JOIN cat_grupo_cupo c On (sa.id_grupo = c.id_grupo_cupo)";
               sql+= "\n       LEFT JOIN cat_grupo_flujo f On (sa.id_grupo_flujo = f.id_grupo_flujo) ";
            }
             
            if(gsDBM.trim().equals("DB2")) 
               sql+= "\n where";
            
            if(dtoIn.getPvsDivision() != null && dtoIn.getPvsDivision().equals(""))
            {
            	
            	if(gsDBM.trim().equals("DB2")) 
	               sql+= "\n   sa.id_grupo_flujo in ";
	            else
	               sql+= "\n   AND sa.id_grupo_flujo in ";
	         
	             sql+= "\n       ( SELECT id_grupo_flujo ";
	             sql+= "\n         FROM grupo_empresa ";
	             sql+= "\n         WHERE no_empresa in ";
	             sql+= "\n             ( SELECT no_empresa ";
	             sql+= "\n               FROM usuario_empresa ";
	             sql+= "\n               WHERE no_usuario = "+Utilerias.validarCadenaSQL(dtoIn.getIdUsuario())+" ) )";
            }
            else{
            	if(gsDBM.trim().equals("DB2")) 
	               sql+= "\n   sa.id_division in ";
            	else
	               sql+= "\n   AND sa.id_division in ";

            	sql+= "\n       ( SELECT id_division ";
	            sql+= "\n         FROM usuario_division ";
	            sql+= "\n         WHERE no_usuario = "+Utilerias.validarCadenaSQL(dtoIn.getIdUsuario())+" ) ";
            }
            
            if(dtoIn.getIdCliente()>0)
            {
	            sql+= "\n AND sa.cve_control in (Select distinct cve_control ";
	            sql+= "\n                        From movimiento ";
	            sql+= "\n                        Where no_cliente = '"+Utilerias.validarCadenaSQL(dtoIn.getIdCliente())+"')";
            }
	        
	        if(dtoIn.getGrupoEmpresa()>0)
	            sql+= "\n AND sa.id_grupo_flujo = " +Utilerias.validarCadenaSQL(dtoIn.getGrupoEmpresa());
	        
	        if(dtoIn.getPvsDivision()!=null && !dtoIn.getPvsDivision().trim().equals(""))
	        {
	            if(dtoIn.getPvsDivision().trim().equals("TODAS LAS DIVISIONES")) 
	                sql+= "\n AND ( not sa.id_division is null and sa.id_division <> '' ) ";
            	else
	                sql+= "\n AND sa.id_division = '"+Utilerias.validarCadenaSQL(dtoIn.getPvsDivision())+"' ";
	        }
	        
	        if(dtoIn.getPvGrupoRubro()>0)
	            sql+= "\n AND sa.id_grupo = " +Utilerias.validarCadenaSQL(dtoIn.getPvGrupoRubro());
	   
	        if((dtoIn.getFechaFin()!=null && !dtoIn.getFechaFin().trim().equals("")) && (dtoIn.getFechaIni()!=null && !dtoIn.getFechaIni().trim().equals("")))
	            sql+= "\n AND sa.fecha_propuesta between convert(datetime,'"+Utilerias.validarCadenaSQL(dtoIn.getFechaIni())
	            		+"',103) AND convert(datetime,'"+Utilerias.validarCadenaSQL(dtoIn.getFechaFin())+"',103)";
	        else if(dtoIn.getFechaIni()!=null && !dtoIn.getFechaIni().trim().equals(""))
	            sql+= "\n AND sa.fecha_propuesta >= convert(datetime,'"+Utilerias.validarCadenaSQL(dtoIn.getFechaIni())+"',103)";
	        
	        if(dtoIn.isSoloMisProp())
	            sql+= "\n AND sa.cve_control like '%"+Utilerias.validarCadenaSQL(dtoIn.getIdUsuario())+"' ";
	        
	        //system.out.println("Query de reporte: " + sql);
	        
	        list= jdbcTemplate.query(sql, new RowMapper<Map<String, Object>>(){
				public Map<String, Object> mapRow(ResultSet rs, int idx) throws SQLException {
					Map<String, Object> cons = new HashMap<String, Object>();
					cons.put("pNoEmpresa", rs.getString("id_grupo_flujo"));
					cons.put("pNomEmpresa", rs.getString("desc_grupo_flujo"));
					cons.put("pCveControl", rs.getString("cve_control"));
					cons.put("pFechaPago", rs.getString("fecha_propuesta"));
					cons.put("pImporteMN", rs.getDouble("cupo_total"));
					cons.put("pImporteDLS", rs.getDouble("propuesto"));
					return cons;
				}});
            
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasDao, M:reporteConSelecAut");
		}
		return list;
	}


	public int validaFacultad(int idFacultad) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.validaFacultad(idFacultad);
	}

	public List<LlenaComboDivisaDto>llenarComboDivisa() {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.llenarComboDivisa();
	}


	public double obtenerTipoCambio(String sDivisa, Date fecHoy) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.obtenerTipoCambio(sDivisa, fecHoy);
	}

	public List<LlenaComboGralDto>llenarComboBancoPag(String sDivisa, String noEmpresa) {
		StringBuffer sql = new StringBuffer();
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		
		try {
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			
			sql.append(" SELECT distinct ccb.id_banco as ID, desc_banco as descrip ");
			sql.append("\n FROM cat_cta_banco ccb, cat_banco cb ");
			sql.append("\n WHERE ccb.id_banco = cb.id_banco ");
			
			sql.append("\n 	AND id_divisa = '" + Utilerias.validarCadenaSQL(sDivisa) + "'");
			
			sql.append("\n 	AND ccb.no_empresa = '" + Utilerias.validarCadenaSQL(noEmpresa) + "'");
			sql.append("\n 	AND ccb.tipo_chequera IN ('P','M') ");
			
			
			//sql.append("\n 		And ccb.tipo_chequera not in (" + consultasGenerales.consultarConfiguraSet(202) + ") ");
			
			//system.out.println("query banco paga: " + sql);
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>() {
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto con = new LlenaComboGralDto();
					con.setId(rs.getInt("ID"));
					con.setDescripcion(rs.getString("descrip"));
					return con;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: llenarComboBancoPag");
		}return list;
	}

	public List<LlenaComboGralDto>llenarComboBancoPagBenef(String sDivisa, int noPersona, String descFormaPago) {
		StringBuffer sql = new StringBuffer();
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		
		try {
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			if(descFormaPago.equals("TRASPASO")){
				sql.append(" SELECT c.ID_BANCO as ID, DESC_BANCO as descrip FROM CAT_BANCO c ");
				sql.append("\n JOIN CAT_CTA_BANCO b ON c.ID_BANCO = b.ID_BANCO ");
				sql.append("\n JOIN PERSONA P ON P.no_empresa = b.no_empresa ");
				sql.append("\n WHERE EQUIVALE_PERSONA = '"+Utilerias.validarCadenaSQL(noPersona)+ "'");
				sql.append("\n  GROUP BY c.ID_BANCO, DESC_BANCO " );
			}else{
				sql.append(" SELECT distinct ccb.id_banco as ID, cb.desc_banco as descrip ");
				sql.append("\n FROM ctas_banco ccb, cat_banco cb ");
				sql.append("\n WHERE ccb.id_banco = cb.id_banco ");
			//Consultar si tiene pago cruzado, en caso afirmativo no carga la condiciï¿½n de divisas.
			//List<BloqueoPagoCruzadoDto> lstBloqueo = consultaPagosCruzadosDivisa(""+noPersona);
			
			/*if(lstBloqueo.size()<=0){
				sql.append("\n 		And ccb.id_divisa = '" + Utilerias.validarCadenaSQL(sDivisa) + "'");
			}*/
			
			sql.append("\n 		And ccb.id_tipo_persona = 'P' ");
			sql.append("\n 		And ccb.no_persona = "+ noPersona +" ");
			}
			//sql.append("\n 		And ccb.tipo_chequera not in (" + consultasGenerales.consultarConfiguraSet(202) + ") ");
			
			System.out.println("query banco Benef 123: " + sql.toString());
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>() {
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto con = new LlenaComboGralDto();
					con.setId(rs.getInt("ID"));
					con.setDescripcion(rs.getString("descrip"));
					return con;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M:llenarComboBancoPagBenef");
		}return list;
	}
	
	public List<LlenaComboGralDto>llenarComboChequeraPag(int idBanco, int noEmpresa, String sDivisa) {
		StringBuffer sql = new StringBuffer();
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		
		try {
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			
			sql.append(" SELECT id_chequera as ID, id_chequera as descrip ");
			sql.append("\n FROM cat_cta_banco ");
			sql.append("\n WHERE id_banco = " + idBanco +" "); 
			sql.append("\n 	AND tipo_chequera IN ('P', 'M') ");
			
			sql.append("\n 		And id_divisa like '" +Utilerias.validarCadenaSQL(sDivisa)+ "'"); 
			
			//COMENTADO 03/02/2016
			
			//sql.append("\n 		And tipo_chequera not in (" + consultasGenerales.consultarConfiguraSet(202) + ") ");
			
			if(noEmpresa != 0)	sql.append("\n 		And no_empresa = "+ noEmpresa +"");
			
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
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: llenarComboChequeraPag");
		}return list;
	}

	//Agregado EMS: 04/11/2015
	public List<LlenaComboGralDto>llenarComboChequeraPagBenef(int idBanco, int noEmpresa, String sDivisa, int idBenef, String descFormaPago) {
		StringBuffer sql = new StringBuffer();
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		
		try {
			if(descFormaPago.equals("TRASPASO")){
				sql.append(" SELECT ID_CHEQUERA as descrip, b.id_divisa FROM CAT_CTA_BANCO b ");
				sql.append("\n JOIN PERSONA P ON P.NO_EMPRESA = b.NO_EMPRESA " );
				sql.append("\n WHERE EQUIVALE_PERSONA = '"+ idBenef +"' ");
				sql.append("\n AND ID_BANCO = "+idBanco+"");
				sql.append("\n AND b.TIPO_CHEQUERA IN ('C','M')");
				
			}else{
				sql.append(" SELECT CB.id_chequera as ID, CB.id_chequera as descrip, cb.id_divisa ");
				sql.append("\n FROM CTAS_BANCO CB JOIN PERSONA P ON CB.NO_PERSONA = P.NO_PERSONA ");
				sql.append("\n WHERE CB.id_banco = " + idBanco +" ");
				sql.append("\n 		And P.no_persona = "+idBenef+" ");
			//Consultar si tiene pago cruzado, en caso afirmativo no carga la condiciï¿½n de divisas.
			/*List<BloqueoPagoCruzadoDto> lstBloqueo = consultaPagosCruzadosDivisa(""+idBenef);
			
			if(lstBloqueo.size()<=0){
				sql.append("\n 		And CB.id_divisa like '" + Utilerias.validarCadenaSQL(sDivisa) + "'");
			}*/
			
			sql.append("\n 		And CB.id_tipo_persona = 'P' ");
			
			sql.append("\n 	ORDER BY descrip ");
			
			System.out.println("query chequera Benef: " + sql);
			}
			list = jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>() {
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto con = new LlenaComboGralDto();
					con.setDescripcion("[" + rs.getString("id_divisa") + "] " + rs.getString("descrip"));
					return con;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: llenarComboChequeraPag");
		}return list;
	}
	
	public List<ComunEgresosDto>buscaBancoChequera(int noEmpresa, int formaPago) {
		StringBuffer sql = new StringBuffer();
		List<ComunEgresosDto> list = new ArrayList<ComunEgresosDto>();
		
		try {
			sql.append(" SELECT ccb.id_banco, ccb.id_chequera, cb.desc_banco \n");
			sql.append(" FROM cat_banco cb, cat_cta_banco ccb \n");
			sql.append(" WHERE ccb.no_empresa = " + noEmpresa + " \n");
			sql.append(" 	And ccb.id_banco = cb.id_banco \n");
			
			switch(formaPago) {
				case 1:
					sql.append(" And ccb.b_cheque = 'S' \n");
					break;
				case 3:
					sql.append(" And ccb.b_transferencia = 'S' \n");
					break;
				case 9:
					sql.append(" And ccb.b_cheque_ocurre = 'S' \n");
					break;
			}
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper<ComunEgresosDto>() {
				public ComunEgresosDto mapRow(ResultSet rs, int idx) throws SQLException {
					ComunEgresosDto cons = new ComunEgresosDto();
					cons.setIdBanco(rs.getInt("id_banco"));
					cons.setIdChequera(rs.getString("id_chequera"));
					cons.setDescBanco(rs.getString("desc_banco"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasDao, M:buscaBancoChequera");
		}
		return list;
	}
	

	public int actualizarBCBenef(int noEmpresa, int noFolioDet, int idBanco, String idChequera, int formaPago) {
		StringBuffer sql = new StringBuffer();
		int resp = 0;
		
		try {
			sql.append(" UPDATE movimiento \n");
			sql.append(" SET id_banco_benef = "+ idBanco +", id_chequera_benef = '"+ Utilerias.validarCadenaSQL(idChequera) 
						+"', id_forma_pago = "+ formaPago +", id_leyenda = 0, id_caja = 1 \n");
			sql.append(" WHERE no_empresa = " + noEmpresa + " \n");
			sql.append(" 	And no_folio_det = " + noFolioDet + " \n");
			sql.append(" 	Or folio_ref = " + noFolioDet + " \n");
			
			resp = jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasDao, M:actualizarBCBenef");
		}
		return resp;
	}


	public List<ComunEgresosDto>buscaBancoChequeraBenef(int noCliente, String idDivisa) {
		StringBuffer sql = new StringBuffer();
		List<ComunEgresosDto> list = new ArrayList<ComunEgresosDto>();
		
		try {
			sql.append(" SELECT id_banco, id_chequera \n");
			sql.append(" FROM ctas_banco \n");
			sql.append(" WHERE no_persona = " + noCliente + " \n");
			sql.append(" 	And id_divisa = '"+ Utilerias.validarCadenaSQL(idDivisa) +"' \n");
			sql.append(" 	And id_tipo_persona in ('P', 'K') \n");
			
			/*
			if(noEmpresa != 0) {
				sql.append(" 	And id_banco = "+ noEmpresa +" \n");
			}
			*/
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper<ComunEgresosDto>() {
				public ComunEgresosDto mapRow(ResultSet rs, int idx) throws SQLException {
					ComunEgresosDto cons = new ComunEgresosDto();
					cons.setIdBanco(rs.getInt("id_banco"));
					cons.setIdChequera(rs.getString("id_chequera"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasDao, M:buscaBancoChequeraBenef");
		}
		return list;
	}
	

	public int actualizarFecPropuesta(String fecPago, String sFolios) {
		StringBuffer sql = new StringBuffer();
		int resp = 0;
		
		try {
			sql.append(" UPDATE movimiento \n");
			sql.append(" SET fec_propuesta = '"+ Utilerias.validarCadenaSQL(fecPago) +"' \n");
			sql.append(" WHERE no_folio_det in (" + sFolios + ") \n"); //no aplica validarCadenas ya que se pasa un formato tipo '','',
			sql.append(" 	Or folio_ref in (" + sFolios + ") \n");
			
			resp = jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasDao, M:actualizarFecPropuesta");
		}
		return resp;
	}

	public int actualizarFecPropuestaCupo(String fecPago, String cveControl) {
		StringBuffer sql = new StringBuffer();
		int resp = 0;
		
		try {
			sql.append(" UPDATE seleccion_automatica_grupo \n");
			sql.append(" SET fecha_propuesta = '"+ Utilerias.validarCadenaSQL(fecPago) +"' \n");//, fec_limite_selecc = '"+ fecPago +"' \n");
			sql.append(" WHERE cve_control = '"+ Utilerias.validarCadenaSQL(cveControl) +"' \n");
			
			resp = jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasDao, M:actualizarFecPropuestaCupo");
		}
		return resp;
	}


	public int actualizarBanCheqPropuesta(String sFolios, int formaPago, int idBanco, String idChequera, int idBancoBenef, String idChequeraBenef, boolean actualizaBeneficiario, String idDivisa, double tipoCambio,
			String bTraspaso, String campoRef, String referencia1, String referencia2, String referencia3) {
		StringBuffer sql = new StringBuffer();
		int resp = 0;
		if (idChequeraBenef.length() > 0)
			idChequeraBenef = idChequeraBenef.split(" ")[1];
		
		try {
		//	System.out.println("entro a modificar daoo");
			if(bTraspaso.equals("TRASPASO")){
				sql.append(" UPDATE movimiento \n");
				if(idBanco != 0){
					sql.append(" SET id_banco = "+ idBanco +", id_chequera  = '"+ Utilerias.validarCadenaSQL(idChequera) +"' \n");				
				}else if(actualizaBeneficiario){
					sql.append(" SET id_banco_benef = "+ idBancoBenef +", id_chequera_benef  = '"+ Utilerias.validarCadenaSQL(idChequeraBenef) +"'\n");
				}
				if (!idDivisa.equals("")) {
					if (idDivisa.equals("MN")) {//divide
						sql.append(" , importe = CASE WHEN id_divisa <> '" + idDivisa + "' THEN importe * " + tipoCambio + " ELSE importe end \n");	
					} else if(!idDivisa.equals("MN")) {
						sql.append(" , importe = CASE WHEN id_divisa <> '" + idDivisa + "' THEN importe / " + tipoCambio + " ELSE importe end \n");	
					} else {//multiplica					
					}
					sql.append(" , id_divisa = '" + idDivisa + "' \n");	
				}
				sql.append(" WHERE ((no_folio_det in (" + sFolios + ") \n");
				sql.append(" 	OR folio_ref in (" + sFolios + ")) AND id_tipo_operacion = 3801 AND id_tipo_movto='E') \n");
				resp = jdbcTemplate.update(sql.toString());
	
				sql = new StringBuffer();
				
				sql.append(" UPDATE movimiento \n");
				if(idBanco != 0){
					sql.append(" SET id_banco_benef = "+ idBanco +", id_chequera_benef  = '"+ Utilerias.validarCadenaSQL(idChequera) +"' \n");				
				}else if(actualizaBeneficiario){
					sql.append(" SET id_banco = "+ idBancoBenef +", id_chequera  = '"+ Utilerias.validarCadenaSQL(idChequeraBenef) +"'\n");
				}
				if (idDivisa != "") {
					if (idDivisa.equals("MN")) {//divide
						sql.append(" , importe = CASE WHEN id_divisa <> '" + idDivisa + "' THEN importe * " + tipoCambio + " ELSE importe end \n");	
					} else if(!idDivisa.equals("MN")) {
						sql.append(" , importe = CASE WHEN id_divisa <> '" + idDivisa + "' THEN importe / " + tipoCambio + " ELSE importe end \n");	
					} else {//multiplica					
					}
					sql.append(" , id_divisa = '" + idDivisa + "' \n");	
				}
				sql.append(" WHERE ((no_folio_det in (" + sFolios + ") \n");
				sql.append(" 	OR folio_ref in (" + sFolios + ")) AND id_tipo_operacion = 3801 AND id_tipo_movto='I') \n");
				
				resp = jdbcTemplate.update(sql.toString());	
			}else{
			//	System.out.println("entro a dao modif");
				sql.append(" UPDATE movimiento \n");
				if(idBanco != 0){
					sql.append(" SET id_banco = "+ idBanco +", id_chequera  = '"+ Utilerias.validarCadenaSQL(idChequera) +"' \n");
					
					if(formaPago != 0){
						sql.append(" ,id_forma_pago = "+ formaPago +" \n");
					}
					
					if(actualizaBeneficiario){
						sql.append(" ,id_banco_benef = "+ idBancoBenef +", id_chequera_benef  = '"+ Utilerias.validarCadenaSQL(idChequeraBenef) +"'\n");
					}
					if (idDivisa != "") {
						if (idDivisa.equals("MN")) {//divide
							sql.append(" , importe = CASE WHEN id_divisa <> '" + idDivisa + "' THEN importe * " + tipoCambio + " ELSE importe end \n");	
						} else if(!idDivisa.equals("MN")) {
							sql.append(" , importe = CASE WHEN id_divisa <> '" + idDivisa + "' THEN importe / " + tipoCambio + " ELSE importe end \n");	
						} else {//multiplica					
						}
						sql.append(" , id_divisa = '" + idDivisa + "' \n");	
					}		
					if(!referencia1.equals("") || !referencia2.equals("") || !referencia3.equals("")){
						switch (campoRef.trim()) {
							case "":
								sql.append(", concepto='"+referencia2+"'");
								break;
							case "concepto":
								sql.append(", concepto='"+referencia2+"'");
								break;
							case "no_factura":
								sql.append(", no_factura='"+referencia2+"'");
								break;
							case "no_docto":
								sql.append(", no_docto='"+referencia2+"'");
								break;
							case "concepto y no_factura":
								sql.append(", concepto='"+referencia1+"'\n");
								sql.append(", no_factura='"+referencia2+"'");
								break;
							case "referencia_dalton":
								System.out.println("entro a referencia dalton1");
								sql.append(", observacion='"+referencia1+"|"+referencia2+"|"+referencia3+"'");
								break;
						}
					}
				}else if(actualizaBeneficiario){
					
						sql.append(" SET id_banco_benef = "+ idBancoBenef +", id_chequera_benef  = '"+ Utilerias.validarCadenaSQL(idChequeraBenef) +"'\n");
						
						if(formaPago != 0){
							sql.append(" ,id_forma_pago = "+ formaPago +" \n");
						}
						
				}else if(formaPago != 0){
					sql.append(" SET id_forma_pago = "+ formaPago +" \n");
					
					if(!referencia1.equals("") || !referencia2.equals("") || !referencia3.equals("")){
						switch (campoRef.trim()) {
							case "":
								sql.append(", concepto='"+referencia2+"'");
								break;
							case "concepto":
								sql.append(", concepto='"+referencia2+"'");
								break;
							case "no_factura":
								sql.append(", no_factura='"+referencia2+"'");
								break;
							case "no_docto":
								sql.append(", no_docto='"+referencia2+"'");
								break;
							case "concepto y no_factura":
								sql.append(", concepto='"+referencia1+"'\n");
								sql.append(", no_factura='"+referencia2+"'");
								break;
							case "referencia_dalton":
								System.out.println("entro a referencia dalton2");
								sql.append(", observacion='"+referencia1+"|"+referencia2+"|"+referencia3+"'");
								break;
						}
					}
				}else if(!referencia1.equals("") || !referencia2.equals("") || !referencia3.equals("")){
					switch (campoRef.trim()) {
						case "":
							sql.append("SET concepto='"+referencia2+"'");
							break;
						case "concepto":
							sql.append("SET concepto='"+referencia2+"'");
							break;
						case "no_factura":
							sql.append("SET no_factura='"+referencia2+"'");
							break;
						case "no_docto":
							sql.append("SET no_docto='"+referencia2+"'");
							break;
						case "concepto y no_factura":
							sql.append("SET concepto='"+referencia1+"'\n");
							sql.append(", no_factura='"+referencia2+"'");
							break;
						case "referencia_dalton":
							System.out.println("entro a referencia dalton3");
							sql.append("SET observacion='"+referencia1+"|"+referencia2+"|"+referencia3+"'");
							break;
					}
				}
						
				if(tipoCambio!=0){
					sql.append(", tipo_cambio="+tipoCambio);
				}
							
				sql.append(" WHERE (no_folio_det in (" + sFolios + ") And id_tipo_operacion = 3000) \n");
				sql.append(" 	Or (folio_ref in (" + sFolios + ") And id_tipo_operacion = 3001) \n");
				
				System.out.println("Modifica Beneficiario: " + sql.toString());
				
				resp = jdbcTemplate.update(sql.toString());
				}
			}catch(Exception e) {
				e.printStackTrace();
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasDao, M:actualizarBanCheqPropuesta");
			}
		return resp;
	}


	public int autorizarDetalle(int iFolio, int usr1) {
		StringBuffer sql = new StringBuffer();
		int resp = 0;
		
		try {
			sql.append(" INSERT INTO autorizacionesMov \n");
			sql.append(" VALUES("+ iFolio +", "+ usr1 +", 0) \n");
		System.out.println("rubio"+sql.toString());	
			resp = jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasDao, M:autorizarDetalle");
		}
		return resp;
	}

	public boolean existeReg(String iFolio, int usuario, boolean ban, boolean ban2, boolean ban3) {
		StringBuffer sql = new StringBuffer();
		boolean resp = false;
		
		try {
			sql.append(" SELECT COUNT(*) FROM autorizacionesMov \n");
			sql.append(" WHERE no_folio_det in ("+ iFolio +") ");
			
			if(ban)
				sql.append(" And (usuario_uno = "+ usuario +" OR usuario_dos = "+ usuario +")\n");
			
			if(ban2)
				sql.append(" And usuario_uno <> 0 ");
			
			if(ban3)
				sql.append(" And usuario_dos <> 0 ");
			
			System.out.println("ExisteReg "+sql);
			
			if(jdbcTemplate.queryForInt(sql.toString()) > 0) resp = true;
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasDao, M:existeReg");
		}
		return resp;
	}


	public int modificarDetalle(int iFolio, String psCampo, int usuario) {
		StringBuffer sql = new StringBuffer();
		int resp = 0;
		
		try {
			sql.append(" UPDATE autorizacionesMov SET "+ Utilerias.validarCadenaSQL(psCampo) +" = "+ usuario +" \n");
			sql.append(" WHERE no_folio_det = "+ Utilerias.validarCadenaSQL(iFolio) +" ");
			
			resp = jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasDao, M:modificarDetalle");
		}
		return resp;
	}


	/*@Override
	public int validaTodasAutorizadas(String cveControl, String caso) {
		return 0;
	}


	@Override
	public int valTodasDesAutorizadas(String cveControl, String psCampo) {
		return 0;
	}*/


	public List<MovimientoDto> consultarDetPagos(int noCliente, String fecIni, String fecFin) {
		StringBuffer sql = new StringBuffer();
		List<MovimientoDto> list = new ArrayList<MovimientoDto>();
		
		try {
			sql.append(" SELECT m.fec_valor, m.no_docto, m.concepto, m.importe, m.id_divisa, \n");
			sql.append(" 		m.no_cheque, m.id_chequera, cb.desc_banco as bancoPago, cfp.desc_forma_pago as formaPago \n");
			sql.append(" FROM movimiento m, cat_banco cb, cat_forma_pago cfp \n");
			sql.append(" WHERE m.id_banco = cb.id_banco \n");
			sql.append(" 	And m.id_forma_pago = cfp.id_forma_pago \n");
			sql.append(" 	And no_cliente = "+ noCliente +" \n");
			sql.append(" 	And fec_valor between convert(datetime,'"+ Utilerias.validarCadenaSQL(funciones.cambiarFecha(fecIni, true)) 
							+"',103) And convert(datetime,'"+ Utilerias.validarCadenaSQL(funciones.cambiarFecha(fecFin, true)) +"',103) \n");
			sql.append(" 	And (id_tipo_operacion = 3200 And id_estatus_mov = 'K')");
			
			//system.out.println("Query de consulta detalle pagos: " + sql);
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper<MovimientoDto>() {
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons = new MovimientoDto();
					cons.setFecValor(rs.getDate("fec_valor"));
					cons.setNoFactura(rs.getString("no_docto"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setImporte(rs.getDouble("importe"));
					cons.setIdDivisa(rs.getString("id_divisa"));
					cons.setNoCheque(rs.getInt("no_cheque"));
					cons.setIdChequeraPago(rs.getString("id_chequera"));
					cons.setBancoPago(rs.getString("bancoPago"));
					cons.setDescFormaPago(rs.getString("formaPago"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasDao, M:consultarDetPagos");
		}
		return list;
	}


	public int updateMontoPropuesta(double importeResta, String cveControl) {
		int resultado = 0;
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" UPDATE seleccion_automatica_grupo ");
			sql.append("\n	SET cupo_automatico = "+ importeResta +"");
			sql.append("\n		, monto_maximo = "+ importeResta +"");
			sql.append("\n		, cupo_total = "+ importeResta + "");
			sql.append("\n WHERE cve_control = '" + Utilerias.validarCadenaSQL(cveControl) + "' ");
			
			resultado = jdbcTemplate.update(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: updateMontoPropuesta");
		}return resultado;
	}

	public List<MovimientoDto> selectMonto(String usr, String cveControl) {
		StringBuffer sql = new StringBuffer();
		List<MovimientoDto> list = new ArrayList<MovimientoDto>();
		
		try {
			sql.append(" SELECT coalesce(SUM(m.importe), 0) as importe \n");
			sql.append(" FROM movimiento m, autorizacionesMov a \n");
			sql.append(" WHERE m.no_folio_det = a.no_folio_det \n");
			sql.append(" 	And m.cve_control = '"+ Utilerias.validarCadenaSQL(cveControl) +"' \n");
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper<MovimientoDto>() {
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons = new MovimientoDto();
					cons.setImporte(rs.getDouble("importe"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: selectMonto");
			return list;
		}
		return list;
	}


	public List<MovimientoDto> selectMontoOriginal(String cveControl) {
		List<MovimientoDto> list = new ArrayList<MovimientoDto>();
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT coalesce(SUM(importe), 0) as importe \n");
			sql.append(" FROM movimiento \n");
			sql.append(" WHERE cve_control = '"+ Utilerias.validarCadenaSQL(cveControl) +"' \n");
			sql.append(" 	And id_tipo_operacion = 3000 \n");
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper<MovimientoDto>() {
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons = new MovimientoDto();
					cons.setImporte(rs.getDouble("importe"));
					return cons;
				}
			});
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: selectMonto");
			return list;
		}
		return list;
	}


	public int buscarRegPend(String cveControl) {
		int resultado = 0;
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT case when count(*) != (select count(*) from movimiento where cve_control = '"+ Utilerias.validarCadenaSQL(cveControl) 
						+"' and id_tipo_operacion = 3000) then 1 else 0 end as valor \n");
			sql.append("\n FROM movimiento m, autorizacionesMov a \n");
			sql.append("\n WHERE m.no_folio_det = a.no_folio_det \n");
			sql.append("\n		And m.cve_control = '"+ Utilerias.validarCadenaSQL(cveControl) +"' \n");
			sql.append("\n 		And a.usuario_uno <> 0 \n");
			sql.append("\n 		And a.usuario_dos <> 0 \n");
			
			resultado = jdbcTemplate.queryForInt(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: buscarRegPend");
			return 0;
		}
		return resultado;
	}


	public int updateFirmaDos(String cveControl, int usr) {
		int resultado = 0;
		StringBuffer sql = new StringBuffer();
		
		try {
			
			sql.append(" UPDATE seleccion_automatica_grupo \n");
			
			if(usr != 0)
				sql.append("\n SET usuario_dos = "+ usr +" \n");
			else
				sql.append("\n SET usuario_dos = NULL \n");
			sql.append("\n WHERE cve_control = '"+ Utilerias.validarCadenaSQL(cveControl) +"' \n");
			sql.append("\n 		And (not usuario_uno is null and usuario_uno <> 0) ");
			
			resultado = jdbcTemplate.update(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: updateFirmaDos");
			return 0;
		}
		return resultado;
	}


	public int verificaFormaPago(int noFolioDet) {
		int resultado = 0;
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT COUNT(*) FROM movimiento \n");
			sql.append("\n WHERE no_folio_det = "+ Utilerias.validarCadenaSQL(noFolioDet) +" \n");
			sql.append("\n 		And id_forma_pago = 1 ");
			
			resultado = jdbcTemplate.queryForInt(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: verificaFormaPago");
			return 0;
		}
		return resultado;
	}


	public int verificaRegPend(String cveControl, int noEmpresa) {
		int resultado = 0;
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT count(*) \n");
			sql.append("\n FROM autorizacionesMov \n");
			sql.append("\n WHERE no_folio_det in (select no_folio_det from movimiento where cve_control = '"+ Utilerias.validarCadenaSQL(cveControl) 
						+"' and id_tipo_operacion = 3000 and no_empresa = "+ Utilerias.validarCadenaSQL(noEmpresa) +") \n");
			sql.append("\n		And usuario_uno = 0 \n");
			sql.append("\n 		And usuario_dos = 0 \n");
			
			resultado = jdbcTemplate.queryForInt(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: verificaRegPend");
			return 0;
		}
		return resultado;
	}

/*
	@Override
	public List<GrupoDTO> llenarComboGrupo(String idTipoGrupo, int noEmpresa) {
		return null;
	}


	@Override
	public List<RubroDTO> llenarComboRubros(int idGrupo) {
		return null;
	}


	@Override
	public int insertarFlujoMovtos(int noFolioMov, int idGrupo, int idRubro) {
		return 0;
	}


	@Override
	public int insertaDatosConta(List<Map<String, String>> listMovtos, int i) {
		return 0;
	}

	@Override
	public int existeFlujoMovtos(int noFolioDet) {
		return 0;
	}


	@Override
	public int updateDatosConta(List<Map<String, String>> listMovtos, int i) {
		return 0;
	}
	*/
 
	public int eliminarDetalle(int iFolio) {
		StringBuffer sbSql = new StringBuffer();
		int iAfec = 0;
		
		try {
			sbSql.append(" DELETE FROM autorizacionesMov \n");
			sbSql.append(" WHERE no_folio_det in ("+ iFolio +") "); //No aplica verifica cadena por formato in('','',)
			
			iAfec = jdbcTemplate.update(sbSql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: eliminarDetalle");
			return 0;
		}
		return iAfec;
	}
	
	
	public String eliminarDetalleStored(String cveControl, double importeTotal, double importeResta,
													String folios, int noUsuario, int noCheque, String estatus){
		StringBuffer sql= new StringBuffer();
		Map<String, Object> resultStored = new HashMap<>();
		String result = "Error al eliminar.";
		
		try{
			
			//int idUsuario = GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario();
			
			StoredProcedure storedDetalles = new StoredProcedure(jdbcTemplate, "SP_ELIMINA_DETALLE_PROP");
			storedDetalles.declareParameter("V_cve_control", cveControl, StoredProcedure.IN);
			storedDetalles.declareParameter("V_importe_total", importeTotal, StoredProcedure.IN);
			storedDetalles.declareParameter("V_importe_resta", importeResta, StoredProcedure.IN);
			storedDetalles.declareParameter("V_folios", folios, StoredProcedure.IN);
			storedDetalles.declareParameter("V_no_usuario", noUsuario, StoredProcedure.IN);
			storedDetalles.declareParameter("V_no_cheque", noCheque, StoredProcedure.IN);
			storedDetalles.declareParameter("V_estatus", estatus, StoredProcedure.IN);
			storedDetalles.declareParameter("V_resultado", Types.VARCHAR, StoredProcedure.OUT);
			resultStored = storedDetalles.execute();
			System.out.println("Parametros"+cveControl+" "+importeTotal+" "+importeResta+" "+folios+" "+noUsuario+" "+noCheque+" "+estatus);
	        System.out.println("Sacar detalle: " + sql.toString());
	        
	        result = (String)resultStored.get("V_resultado");
	        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:eliminarDetalleStored");
			 
		}
		return result;
	}
	
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

	//Agregado EMS: 21/09/15
	public int updateFecProp(String cveControls, String fecha) {
		int resultado = 0;
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" UPDATE seleccion_automatica_grupo \n");
			sql.append(" SET fecha_propuesta = convert(datetime,'"+ Utilerias.validarCadenaSQL(fecha) +"',103) \n");
			sql.append(" WHERE cve_control in(" + cveControls + ")");
			
			resultado = jdbcTemplate.update(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: verificaFormaPago");
			return 0;
		}
		return resultado;
	}
	
	public int updateFecDetProp(String cveControls, String fecha) {
		int resultado = 0;
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" UPDATE movimiento \n");
			sql.append(" SET fec_propuesta = '"+ Utilerias.validarCadenaSQL(fecha) 
							+"', fec_valor = '" + Utilerias.validarCadenaSQL(fecha) + "' \n");
			sql.append(" WHERE cve_control in(" + cveControls + ") \n");
			sql.append(" AND id_tipo_operacion in(3000,3001,3800,3801) \n" );
			sql.append(" and id_estatus_mov in('N','C','L','H')" );
			
			resultado = jdbcTemplate.update(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: verificaFormaPago");
			return 0;
		}
		return resultado;
	}

	
	public List<BloqueoPagoCruzadoDto> consultaPagosCruzados(String noPersona, boolean esNumero) {
		
		StringBuffer sql= new StringBuffer();
		
		List<BloqueoPagoCruzadoDto> list= new ArrayList<BloqueoPagoCruzadoDto>();
		
		try{
            sql.append("SELECT no_proveedor as ID, ");
            sql.append("\n       razon_social AS proveedor, CP.id_divisa_original as Divisa_original,CP.id_divisa_pago as Divisa_pago,CP.consecutivo ");
            sql.append("\n FROM PERSONA P JOIN conf_pago_cruzado CP ON(P.equivale_persona = CP.no_proveedor) ");
           
            
            if(!noPersona.equals("")){
        		if(esNumero)
        			sql.append("\n WHERE p.equivale_persona = '" + Utilerias.validarCadenaSQL(noPersona) + "' ");
        		else
        			sql.append("\n WHERE p.nombre = '%" + Utilerias.validarCadenaSQL(noPersona) 
        						+ "%' or p.razon_social like '%" + Utilerias.validarCadenaSQL(noPersona) + "%'"
        						+ "   or p.equivale_persona like '%" + Utilerias.validarCadenaSQL(noPersona) + "%' ");
            }
            	 
            sql.append("\n order by proveedor ");
            	    
	        //system.out.println("Query de pagos cruzados: " + sql.toString());
	        
	        list = jdbcTemplate.query(sql.toString(), new RowMapper<BloqueoPagoCruzadoDto>(){
				public BloqueoPagoCruzadoDto mapRow(ResultSet rs, int idx) throws SQLException {
					BloqueoPagoCruzadoDto cons = new BloqueoPagoCruzadoDto();
					cons.setId(rs.getString("ID"));
					cons.setProveedor(rs.getString("proveedor"));
					cons.setDivisaOrig(rs.getString("Divisa_original"));
					cons.setDivisaPago(rs.getString("Divisa_pago"));
					//cons.setConsecutivo(rs.getInt("consecutivo"));
					
					return cons;
				}});
            
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:consultaPagosCruzados");
		}
		return list;
	}

public List<BloqueoPagoCruzadoDto> consultaPagosCruzadosDivisa(String noPersona) {
		
		StringBuffer sql= new StringBuffer();
		
		List<BloqueoPagoCruzadoDto> list= new ArrayList<BloqueoPagoCruzadoDto>();
		
		try{
            sql.append("SELECT no_proveedor as ID, ");
            sql.append("\n       razon_social AS proveedor, CP.id_divisa_original as Divisa_original,CP.id_divisa_pago as Divisa_pago,CP.consecutivo ");
            sql.append("\n FROM PERSONA P JOIN conf_pago_cruzado CP ON(P.equivale_persona = CP.no_proveedor) ");
        	sql.append("\n WHERE p.no_persona = '" + Utilerias.validarCadenaSQL(noPersona) + "' ");
            sql.append("\n order by proveedor ");
            	    
	        //system.out.println("Query de pagos cruzados: " + sql.toString());
	        
	        list = jdbcTemplate.query(sql.toString(), new RowMapper<BloqueoPagoCruzadoDto>(){
				public BloqueoPagoCruzadoDto mapRow(ResultSet rs, int idx) throws SQLException {
					BloqueoPagoCruzadoDto cons = new BloqueoPagoCruzadoDto();
					cons.setId(rs.getString("ID"));
					cons.setProveedor(rs.getString("proveedor"));
					cons.setDivisaOrig(rs.getString("Divisa_original"));
					cons.setDivisaPago(rs.getString("Divisa_pago"));
					//cons.setConsecutivo(rs.getInt("consecutivo"));
					
					return cons;
				}});
            
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:consultaPagosCruzados");
		}
		return list;
	}

	@Override
	public List<LlenaComboGralDto> llenarComboBenefPagoCruzado(String noPersona, boolean esNumero) {

		StringBuffer sql= new StringBuffer();
		
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		
		try{
			
            sql.append("\n SELECT no_proveedor as id, ");
            sql.append("\n       razon_social as descripcion ");
            sql.append("\n FROM PERSONA P JOIN conf_pago_cruzado CP ON(P.equivale_persona = CP.no_proveedor) ");
           
            
            if(!noPersona.equals("")){
            		if(esNumero)
            			sql.append("\n WHERE p.equivale_persona = '" + Utilerias.validarCadenaSQL(noPersona) + "' ");
            		else
            			sql.append("\n WHERE p.nombre = '%" + Utilerias.validarCadenaSQL(noPersona) 
            			+ "%' or p.razon_social like '%" + Utilerias.validarCadenaSQL(noPersona) + "%'"
            			+ " or p.equivale_persona like '%" + Utilerias.validarCadenaSQL(noPersona) + "%' ");
            }
            	 
            sql.append("\n order by descripcion ");
            	    
	        //system.out.println("Query de combo pagos cruzados: " + sql.toString());
	        
	        list = jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setIdStr(rs.getString("ID"));
					cons.setDescripcion(rs.getString("descripcion").trim());
					
					return cons;
				}});
            
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:consultaPagosCruzados");
		}
		return list;
	}

	public List<LlenaComboDivisaDto> obtenerDivisas(){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.llenarComboDivisa();
	}

	@Override
	public int insertaNuevoProvPagoCruzado(BloqueoPagoCruzadoDto pagoCruzado) {
		int resultado = 0;
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" INSERT INTO CONF_PAGO_CRUZADO \n");
			sql.append(" VALUES(1,'" + Utilerias.validarCadenaSQL(pagoCruzado.getProveedor()) +"' \n");
			sql.append(" ,'" + Utilerias.validarCadenaSQL(pagoCruzado.getDivisaOrig() )+ "' \n");
			sql.append(" ,'" + Utilerias.validarCadenaSQL(pagoCruzado.getDivisaPago()) + "') \n");
			
			resultado = jdbcTemplate.update(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: insertaNuevoProvPagoCruzado");
			return 0;
		}
		return resultado;
	}

	@Override
	public boolean existeProvPagoCruzado(String proveedor, String divisaOrig) {
		int resultado = 0;
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" SELECT COUNT(*) ");
			sql.append("\n FROM CONF_PAGO_CRUZADO ");
			sql.append("\n WHERE NO_PROVEEDOR = '" + Utilerias.validarCadenaSQL(proveedor) + "' ");
			sql.append("\n AND ID_DIVISA_ORIGINAL = '" + Utilerias.validarCadenaSQL(divisaOrig) + "' \n");

			resultado = jdbcTemplate.queryForInt(sql.toString());
			
			if(resultado == 0){
				return false;
			}else{
				return true;
			}
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: existeProvPagoCruzado");
			return false;
		}
	}
	
	public int eliminarProvPagoCruzado(String noProv, String divoriginal) {
		StringBuffer sql = new StringBuffer();
		int afec = 0;
		
		try {
			sql.append(" DELETE FROM CONF_PAGO_CRUZADO \n");
			sql.append(" WHERE NO_PROVEEDOR = '" + Utilerias.validarCadenaSQL(noProv) + "' ");
			sql.append(" AND ID_DIVISA_ORIGINAL = '" + Utilerias.validarCadenaSQL(divoriginal) + "' ");
			
			afec = jdbcTemplate.update(sql.toString());
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: eliminarProvPagoCruzado");
			return 0;
		}
		return afec;
	}

	
	@Override
	public List<BloqueoPagoCruzadoDto> buscarBloqueo(String noEmpresa, String noProv, 
													String noDocto, boolean existe,
													String periodo) {
		
		StringBuffer sql= new StringBuffer();
		
		List<BloqueoPagoCruzadoDto> result = new ArrayList<BloqueoPagoCruzadoDto>();
		
		try{
			
			if(noDocto.equals("")){
				sql.append("\n SELECT m.importe, m.fec_valor, m.concepto, '' as motivo"); //Agrego motivo vacio para evitar error en mapeo
	            sql.append("\n FROM MOVIMIENTO m JOIN PERSONA p on m.no_cliente = p.no_persona AND p.id_tipo_persona = 'P' ");
	            
			}else{
				
				if(existe){
					sql.append("\n SELECT m.importe, m.fec_valor, m.concepto, b.motivo ");
		            sql.append("\n FROM MOVIMIENTO m JOIN BLOQUEO_PROVEEDOR b ON(m.no_docto = b.no_docto) ");
		            sql.append("\n  			     JOIN PERSONA p on m.no_cliente = p.no_persona AND p.id_tipo_persona = 'P' ");
				}else{
					sql.append("\n SELECT m.importe, m.fec_valor, m.concepto, '' as motivo"); 
					sql.append("\n FROM MOVIMIENTO m JOIN PERSONA p on m.no_cliente = p.no_persona AND p.id_tipo_persona = 'P' ");
				}
			}
			//sql.append("\n WHERE m.no_cliente = '" + noProv + "' ");
            sql.append("\n WHERE p.equivale_persona = '" + Utilerias.validarCadenaSQL(noProv) + "' ");
            sql.append("\n AND m.no_docto = '" + Utilerias.validarCadenaSQL(noDocto) + "' ");
            sql.append("\n AND m.id_tipo_operacion = 3000 ");
            
            if(!periodo.equals("")){
            	sql.append("\n AND YEAR(m.fec_valor) = '"+Utilerias.validarCadenaSQL(periodo)+"' ");
            }            
            		
           // sql.append("\n order by descripcion ");
            	    
	        //system.out.println("Query de buscar bloqueo provedor: " + sql.toString());
	        
	        result = jdbcTemplate.query(sql.toString(), new RowMapper<BloqueoPagoCruzadoDto>(){
				public BloqueoPagoCruzadoDto mapRow(ResultSet rs, int idx) throws SQLException {
					BloqueoPagoCruzadoDto cons = new BloqueoPagoCruzadoDto();
					cons.setImporte(rs.getDouble("importe"));
					cons.setFechaPago(rs.getString("fec_valor"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setMotivo(rs.getString("motivo"));
					
					return cons;
				}});
            
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:buscarBloqueo");
		}
		return result;
	}

	//Agregado EMS 29/09/15
	@Override
	public int existeBloqueo(String noEmpresa, String noProv, String noDocto) {
		 StringBuffer sql = new StringBuffer();
		 int count = 0;
			try{
			    sql.append(" SELECT count(*) as regs");
		    	sql.append("   FROM bloqueo_proveedor");
		    	sql.append("  WHERE no_empresa = '"+Utilerias.validarCadenaSQL(noEmpresa)+"' ");
	    		sql.append("  AND no_proveedor = '"+Utilerias.validarCadenaSQL(noProv)+"' ");
	    		
		    	if(!noDocto.equals("")){
		    		sql.append("  AND no_docto = '"+Utilerias.validarCadenaSQL(noDocto)+"' ");
		    	}else{
		    		sql.append("  AND no_docto is null ");
		    	}
		    	
		    	//system.out.println("count existe proveedor: " +sql.toString());
		    	
			    count = jdbcTemplate.queryForInt(sql.toString());
			 }catch(Exception e){
				 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Egresos, C:ConsultaPropuestasDao, M:existeBloqueo");
			 }
			
		 return count;
	}

	//Agregado EMS 29/09/15
	@Override
	public List<BloqueoPagoCruzadoDto> obtenerBloqueoProveedor(String noEmpresa, String noProv, String cveControl, String noDocto) {
		
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
                
                if(noDocto != null && !noDocto.equals("")){
                	sql.append("\n AND m.no_docto = '"+noDocto+"' ");
                }
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

	@Override
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
	
	//Agregado EMS 29/09/15
	@Override
	public int insertarBloqueo(String noEmpresa, String noProv, String noDocto, String motivo, int noUsuario) {
		int resultado = 0;
		StringBuffer sql = new StringBuffer();
		
		try {
			sql.append(" INSERT INTO BLOQUEO_PROVEEDOR \n");
			sql.append(" VALUES('" + Utilerias.validarCadenaSQL(noEmpresa) +"' \n");
			sql.append(" ,'" + Utilerias.validarCadenaSQL(noProv) + "' \n");
			sql.append(" ,'" + Utilerias.validarCadenaSQL(noDocto) + "' \n");
			sql.append(" ,getdate() \n");
			sql.append(" ,'" + Utilerias.validarCadenaSQL(noUsuario) + "' \n");
			sql.append(" ,'" + Utilerias.validarCadenaSQL(motivo) + "') \n");
			
			resultado = jdbcTemplate.update(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: ConsultaPropuestasDaoImpl, M: insertaNuevoProvPagoCruzado");
			return 0;
		}
		return resultado;
	}

	@Override
	public int eliminarBloqueo(String noEmpresa, String noProv, String noDocto) {
		
		StringBuffer sql = new StringBuffer();
		 int count = 0;
			try{
		    	sql.append("  DELETE FROM bloqueo_proveedor");
		    	sql.append("  WHERE no_empresa = '"+Utilerias.validarCadenaSQL(noEmpresa)+"' ");
	    		sql.append("  AND no_proveedor = '"+Utilerias.validarCadenaSQL(noProv)+"' ");
	    		
		    	if(!noDocto.equals("")){
		    		sql.append("  AND no_docto = '"+Utilerias.validarCadenaSQL(noDocto)+"' ");
		    	}else{
		    		sql.append("  AND no_docto is null ");
		    	}
		    	
			    count = jdbcTemplate.update(sql.toString());
			    
			 }catch(Exception e){
				 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Egresos, C:ConsultaPropuestasDao, M:eliminarBloqueo");
			 }
			
		 return count;
	}
	
	/**
	 * @param num, indica si la consulta sera 1.- solo set, 2.- solo SAP, 3.- ambos
	 */
	public List<List<Object>> datosExcelBloqueados(int num){
	
		StringBuffer sql= new StringBuffer();
		
		List<List<Object>> list = new ArrayList<List<Object>>();
		
		try{
			if(num == 1){
				sql.append("\n SELECT DISTINCT null as cve_control, e.nom_empresa as empresa,  ");
	            sql.append("\n  b.no_proveedor as proveedor,  null as NO_DOCTO, ");
	            sql.append("\n null as fecha_pago, null as importe, ");
	            sql.append("\n null as concepto, b.fecha_bloqueo, b.usuario, b.motivo ");
	            sql.append("\n FROM bloqueo_proveedor b JOIN empresa e ON e.no_empresa = b.no_empresa ");
	            sql.append("\n WHERE b.NO_DOCTO = '' or b.NO_DOCTO IS NULL ");
	           
	            sql.append("\n UNION ALL ");
	            
	            sql.append("\n SELECT DISTINCT m.cve_control as cve_Control, e.nom_empresa as empresa, ");			
	            sql.append("\n b.no_proveedor as proveedor,  m.no_docto, ");
	            sql.append("\n case when b.no_docto <> '' then m.fec_valor end as fecha_pago, ");
	            sql.append("\n case when b.no_docto <> '' then m.importe end as importe, ");
	            sql.append("\n m.concepto, b.fecha_bloqueo, b.usuario, b.motivo ");
	            
	            sql.append("\n FROM cat_grupo_flujo cgf JOIN grupo_empresa ge ON cgf.id_grupo_flujo = ge.id_grupo_flujo ");
	            sql.append("\n JOIN empresa e on ge.no_empresa = e.no_empresa ");
	            sql.append("\n JOIN bloqueo_proveedor b ON e.no_empresa = b.no_empresa ");
	            sql.append("\n LEFT JOIN movimiento m ON b.no_empresa = m.no_empresa ");
	            sql.append("\n JOIN PERSONA p on m.no_cliente = p.no_persona AND p.id_tipo_persona = 'P' ");
	            sql.append("\n WHERE b.no_proveedor = p.equivale_persona ");
	            sql.append("\n AND ID_TIPO_OPERACION = 3000 ");
	            sql.append("\n AND b.no_docto = m.NO_DOCTO ");
	            
			}else if(num == 2){
				//Bloqueos SAP
	            sql.append("\n SELECT DISTINCT m.cve_control, ");			
	            sql.append("\n  e.nom_empresa as empresa, m.no_cliente as proveedor, ");
	            sql.append("\n case when m.cod_bloqueo = 'S' then m.no_docto else null end as no_docto, ");
	            sql.append("\n case when m.cod_bloqueo = 'S' then m.fec_valor else null end as fecha_pago, ");
	            sql.append("\n case when m.cod_bloqueo = 'S' then m.importe else null end as importe, ");
	            sql.append("\n case when m.cod_bloqueo = 'S' then m.concepto else null end as concepto, ");
	            sql.append("\n null as fecha_bloqueo, 0 as usuario, null as motivo ");
	            
	            sql.append("\n FROM grupo_empresa ge JOIN empresa e on ge.no_empresa = e.no_empresa ");
	            sql.append("\n 					 JOIN MOVIMIENTO m on e.no_empresa = m.no_empresa ");
	            sql.append("\n 					 JOIN PERSONA p on m.no_cliente = p.no_persona AND p.id_tipo_persona = 'P' ");
	            sql.append("\n WHERE (p.id_estatus_per = 'I' OR m.COD_BLOQUEO = 'S') ");  
	            sql.append("\n AND m.ID_TIPO_OPERACION = 3000 ");
	            //sql.append("\n AND p.id_tipo_persona = 'P' ");
			}else if(num == 3){
				sql.append("\n SELECT DISTINCT null as cve_control, e.nom_empresa as empresa,  ");
	            sql.append("\n  b.no_proveedor as proveedor,  null as NO_DOCTO, ");
	            sql.append("\n null as fecha_pago, null as importe, ");
	            sql.append("\n null as concepto, b.fecha_bloqueo, b.usuario, b.motivo ");
	            sql.append("\n FROM bloqueo_proveedor b JOIN empresa e ON e.no_empresa = b.no_empresa ");
	            sql.append("\n WHERE b.NO_DOCTO = '' or b.NO_DOCTO IS NULL ");

	            sql.append("\n UNION ALL ");
	            
	            //--Por documentos bloqueados
	            
	            sql.append("\n SELECT DISTINCT m.cve_control as cve_Control, e.nom_empresa as empresa, ");			
	            sql.append("\n b.no_proveedor as proveedor,  m.no_docto, ");
	            sql.append("\n case when b.no_docto <> '' then m.fec_valor end as fecha_pago, ");
	            sql.append("\n case when b.no_docto <> '' then m.importe end as importe, ");
	            sql.append("\n m.concepto, b.fecha_bloqueo, b.usuario, b.motivo ");
	            sql.append("\n FROM cat_grupo_flujo cgf JOIN grupo_empresa ge ON cgf.id_grupo_flujo = ge.id_grupo_flujo ");
	            sql.append("\n JOIN empresa e on ge.no_empresa = e.no_empresa ");
	            sql.append("\n JOIN bloqueo_proveedor b ON e.no_empresa = b.no_empresa ");
	            sql.append("\n LEFT JOIN movimiento m ON b.no_empresa = m.no_empresa ");
	            sql.append("\n JOIN PERSONA p on m.no_cliente = p.no_persona AND p.id_tipo_persona = 'P' ");
	            sql.append("\n WHERE b.no_proveedor = p.equivale_persona ");
	            sql.append("\n AND ID_TIPO_OPERACION = 3000 ");
	            sql.append("\n AND b.no_docto = m.NO_DOCTO ");
	            
	            
				//bloqueados SAP
	            sql.append("\n UNION ALL ");
	            
	            sql.append("\n SELECT DISTINCT m.cve_control, ");			
	            sql.append("\n  e.nom_empresa as empresa, m.no_cliente as proveedor, ");
	            sql.append("\n case when m.cod_bloqueo = 'S' then m.no_docto else null end as no_docto, ");
	            sql.append("\n case when m.cod_bloqueo = 'S' then m.fec_valor else null end as fecha_pago, ");
	            sql.append("\n case when m.cod_bloqueo = 'S' then m.importe else null end as importe, ");
	            sql.append("\n case when m.cod_bloqueo = 'S' then m.concepto else null end as concepto, ");
	            sql.append("\n null as fecha_bloqueo, 0 as usuario, null as motivo ");
	            
	            sql.append("\n FROM grupo_empresa ge JOIN empresa e on ge.no_empresa = e.no_empresa ");
	            sql.append("\n 					 JOIN MOVIMIENTO m on e.no_empresa = m.no_empresa ");
	            sql.append("\n 					 JOIN PERSONA p on m.no_cliente = p.no_persona AND p.id_tipo_persona = 'P' ");
	            sql.append("\n WHERE (p.id_estatus_per = 'I' OR m.COD_BLOQUEO = 'S') ");  
	            sql.append("\n AND m.ID_TIPO_OPERACION = 3000 ");
			}
			
			//system.out.println("query exporta Excel: " + sql.toString());
			
	        list = jdbcTemplate.query(sql.toString(), new RowMapper<List<Object>>(){
				public List<Object> mapRow(ResultSet rs, int idx) throws SQLException {
					List<Object> cons = new ArrayList<Object>();
					cons.add(rs.getString("cve_control"));
					cons.add(rs.getString("empresa"));
					cons.add(rs.getString("proveedor"));
					cons.add(rs.getString("no_docto"));
					cons.add(rs.getString("fecha_pago"));
					cons.add(String.valueOf(rs.getDouble("importe")));
					cons.add(rs.getString("concepto"));
					cons.add(rs.getString("fecha_bloqueo"));
					cons.add((rs.getInt("usuario")==0?"SAP":""+rs.getInt("usuario")));
					cons.add(rs.getString("motivo"));

					return cons;
				}});
	        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:datosExcelBloqueados");
		}
		
		return list;
	}

	//Agregado EMS 01/10/15 - Obtiene todos los proveedores bloqueados, tanto documentos como de SAP.
	public List<BloqueoPagoCruzadoDto> consultaProveedoresBloqueados(String empresas, String propuestas) {

		StringBuffer sql= new StringBuffer();
		List<BloqueoPagoCruzadoDto> result = new ArrayList<BloqueoPagoCruzadoDto>();
		
		try{
			
			// Solo Proveedores
			
			sql.append("\n SELECT DISTINCT m.cve_control, e.nom_empresa as empresa, ");
            sql.append("\n  b.no_proveedor as proveedor,  null as NO_DOCTO, ");
            sql.append("\n null as fecha_pago, null as importe, ");
            sql.append("\n null as concepto, b.fecha_bloqueo, b.usuario, b.motivo ");
            sql.append("\n FROM cat_grupo_flujo cgf JOIN grupo_empresa ge ON cgf.id_grupo_flujo = ge.id_grupo_flujo ");
            sql.append("\n                          JOIN empresa e on ge.no_empresa = e.no_empresa ");
            sql.append("\n                          JOIN bloqueo_proveedor b ON e.no_empresa = b.no_empresa ");
            sql.append("\n                          JOIN MOVIMIENTO m on m.no_empresa = b.no_empresa ");
            sql.append("\n                          JOIN PERSONA p on m.no_cliente = p.no_persona AND p.id_tipo_persona = 'P' ");
            sql.append("\n WHERE cgf.id_grupo_flujo in(" +empresas + ")");
            sql.append("\n AND (b.NO_DOCTO = '' or b.NO_DOCTO IS NULL) ");
            sql.append("\n AND b.no_proveedor = p.equivale_persona ");
            sql.append("\n AND m.ID_TIPO_OPERACION = 3000 ");
            sql.append("\n AND m.ID_ESTATUS_MOV = 'N' ");
            sql.append("\n AND m.CVE_CONTROL in (" + propuestas + ") ");

            sql.append("\n UNION ALL ");
            //Documento bloqueados
            sql.append("\n SELECT DISTINCT m.cve_control as cve_Control, e.nom_empresa as empresa, ");			
            sql.append("\n b.no_proveedor as proveedor,  m.no_docto, ");
            sql.append("\n case when b.no_docto <> '' then m.fec_valor end as fecha_pago, ");
            sql.append("\n case when b.no_docto <> '' then m.importe end as importe, ");
            sql.append("\n m.concepto, b.fecha_bloqueo, b.usuario, b.motivo ");
            sql.append("\n FROM cat_grupo_flujo cgf JOIN grupo_empresa ge ON cgf.id_grupo_flujo = ge.id_grupo_flujo ");
            sql.append("\n JOIN empresa e on ge.no_empresa = e.no_empresa ");
            sql.append("\n JOIN bloqueo_proveedor b ON e.no_empresa = b.no_empresa ");
            sql.append("\n JOIN movimiento m ON b.no_empresa = m.no_empresa ");
          //Agregado para hacer equivalencia con no_persona
            sql.append("\n JOIN persona p ON m.no_cliente = p.no_persona AND p.id_tipo_persona = 'P' "); 
            
            sql.append("\n WHERE cgf.id_grupo_flujo in("+ empresas +")");
            sql.append("\n AND m.CVE_CONTROL in (" + propuestas + ") ");
            //sql.append("\n AND b.no_proveedor = m.no_cliente ");
            sql.append("\n AND b.no_proveedor = p.equivale_persona ");
            sql.append("\n AND m.ID_TIPO_OPERACION = 3000 ");
            sql.append("\n AND m.ID_ESTATUS_MOV = 'N' ");
            sql.append("\n AND b.no_docto = m.NO_DOCTO ");
            
			//bloqueados SAP
            sql.append("\n UNION ALL ");
            
            sql.append("\n SELECT DISTINCT m.cve_control, ");			
            sql.append("\n  e.nom_empresa as empresa, m.no_cliente as proveedor, ");
            sql.append("\n case when m.cod_bloqueo = 'S' then m.no_docto else null end as no_docto, ");
            sql.append("\n case when m.cod_bloqueo = 'S' then m.fec_valor else null end as fecha_pago, ");
            sql.append("\n case when m.cod_bloqueo = 'S' then m.importe else null end as importe, ");
            sql.append("\n case when m.cod_bloqueo = 'S' then m.concepto else null end as concepto, ");
            sql.append("\n null as fecha_bloqueo, 0 as usuario, null as motivo ");
            
            sql.append("\n FROM grupo_empresa ge JOIN empresa e on ge.no_empresa = e.no_empresa ");
            sql.append("\n 					 JOIN MOVIMIENTO m on e.no_empresa = m.no_empresa ");
            sql.append("\n 					 JOIN PERSONA p on m.no_cliente = p.no_persona AND p.id_tipo_persona = 'P' ");
            sql.append("\n WHERE ge.id_grupo_flujo in("+ empresas +")");
            sql.append("\n AND m.CVE_CONTROL in (" + propuestas + ") ");
            sql.append("\n AND (p.id_estatus_per = 'I' OR m.COD_BLOQUEO = 'S') ");  
            sql.append("\n AND m.ID_TIPO_OPERACION = 3000 ");
            sql.append("\n AND m.ID_ESTATUS_MOV = 'N' ");
       
	        //System.out.println("Query de buscar bloqueo provedor 2: " + sql.toString());
	        System.out.println("buscar bloqueos "+sql);
	        result = jdbcTemplate.query(sql.toString(), new RowMapper<BloqueoPagoCruzadoDto>(){
				public BloqueoPagoCruzadoDto mapRow(ResultSet rs, int idx) throws SQLException {
					BloqueoPagoCruzadoDto cons = new BloqueoPagoCruzadoDto();
					cons.setCveControl(rs.getString("cve_control"));
					cons.setEmpresa(rs.getString("empresa"));
					cons.setProveedor(rs.getString("proveedor"));
					cons.setNoDocumento(rs.getString("no_docto"));
					cons.setFechaPago(rs.getString("fecha_pago"));
					cons.setImporte(rs.getDouble("importe"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setFechaBloqueo(rs.getString("fecha_bloqueo"));
					cons.setUsuario((rs.getInt("usuario")==0?"SAP":""+rs.getInt("usuario")));
					cons.setMotivo(rs.getString("motivo"));

					return cons;
				}});
            
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:consultaProveedoresBloqueados");
		}
		return result;
		
	}

	@Override
	public List<LlenaComboGralDto> llenarComboPeriodos(LlenaComboGralDto dto) {
		if(gsDBM.equals("ORACLE") || gsDBM.equals("POSTGRESQL") || gsDBM.equals("DB2")){
			dto.setCampoUno(" DISTINCT rtrim(ltrim(EXTRACT(YEAR FROM fec_valor))) ");
			dto.setCampoDos(" rtrim(ltrim(EXTRACT(YEAR FROM fec_valor))) ");
		}
		else if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE")){
			dto.setCampoUno(" DISTINCT rtrim(ltrim(year(fec_valor))) ");
			dto.setCampoDos(" rtrim(ltrim(year(fec_valor))) ");
		}
			
		dto.setCondicion("");
		dto.setTabla(" MOVIMIENTO ");
		dto.setOrden(" DESCRIPCION ");
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.llenarComboGral(dto);
	}

	@Override
	public List<LlenaComboGralDto> llenarComboReglaNegocio(LlenaComboGralDto dto) {
		
		dto.setCondicion("tipo_regla = '"+dto.getCondicion()+"'");
		
		List<LlenaComboGralDto> list = new ArrayList<>();
		try{
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			list = consultasGenerales.llenarComboGral(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:llenarComboReglaNegocio");
		}
		return list;
	}

	@Override
	public Map<String, String> validaCvePropuesta(String cveControl) {
		
		Map<String, String> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		StringBuffer sql = new StringBuffer();
		int count = 0;
		try{
	    	do{
	    		if(sql.length() > 0){
	    			sql.delete(0, sql.length());
	    		}
	    		
	    		sql.append("\n SELECT coalesce(count(cve_control),0) as cve_control ");
		    	sql.append("\n FROM seleccion_automatica_grupo ");
		    	sql.append("\n  WHERE cve_control like '%" + Utilerias.validarCadenaSQL(cveControl) + "%' ");
		    		    
			    count = jdbcTemplate.queryForInt(sql.toString());
			  
			    if(count > 0){
			    	int posGuionIni = cveControl.indexOf('-');
			        int numCve = Integer.parseInt(cveControl.substring(posGuionIni+1, cveControl.length()))+1;
			        cveControl = cveControl.substring(0, posGuionIni+1) + numCve;        
			    }
			  
	    	}while(count>0);
		    
	    	resultado.put("mensaje", cveControl);
	    	
		 }catch(Exception e){
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:eliminarBloqueo");
		 }
		
		return resultado;
	}
	
	@Override
	public Map<String, String> insertaSubPropuesta(double montoMaximo, String nvaCveControl, String oldCveControl, int idUsuario, String fecha){
		
		Map<String, String> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		StringBuffer sql = new StringBuffer();
		int count = 0;
		try{
	    	
    		sql.append("\n INSERT INTO seleccion_automatica_grupo ");
    		sql.append("\n ( id_grupo, fecha_propuesta, ");
    		sql.append("\n id_grupo_flujo, monto_maximo, cupo_manual, cupo_automatico, ");
    		sql.append("\n cupo_total, fec_limite_selecc, cve_control, id_division ,concepto " );
    		sql.append("\n ) "); //Se quita la primera autorizaciï¿½n EMS: 27/01/16
    		//sql.append("\n ,usuario_uno ) ");
    		sql.append("\n select id_grupo,'" +Utilerias.validarCadenaSQL(fecha)+"',id_grupo_flujo, " + montoMaximo );
    		sql.append("\n ,cupo_manual," + montoMaximo + "," + montoMaximo + ",fec_limite_selecc");
    		sql.append("\n ,'" + Utilerias.validarCadenaSQL(nvaCveControl) + "', id_division ,concepto");
    		//sql.append("\n ,'" + nvaCveControl + "', id_division ,concepto + '*"+1+"'"); //original, pasan parametro indice que siempre trae 1, no tiene caso.
    		
    		//sql.append("\n ," + idUsuario );
    		sql.append("\n from seleccion_automatica_grupo ");
    		sql.append("\n where cve_control ='"+ Utilerias.validarCadenaSQL(oldCveControl) +"' ");

    		count = jdbcTemplate.update(sql.toString());
    		    
    		if(count > 0){
    			resultado.put("mensaje", "ok");
    		}else{
    			resultado.put("error", "No se generï¿½ la subpropuesta.");
    		}
    		
		 }catch(Exception e){
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:insertaSubPropuesta");
			 resultado.put("error", "Error al generar la subpropuesta.");
		 }
		
		return resultado;
	}

	@Override
	public Map<String, String> actualizaMontoPropuesta(double montoMaximo, String cveControl, String stencero) {
		
		Map<String, String> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		StringBuffer sql = new StringBuffer();
		int count = 0;
		try{
	    	
    		sql.append("\n UPDATE seleccion_automatica_grupo ");
    		sql.append("\n SET monto_maximo = monto_maximo-" + montoMaximo);
    		sql.append("\n ,cupo_automatico = cupo_automatico - " + montoMaximo);
    		sql.append("\n ,cupo_total = cupo_total - " + montoMaximo );
    		
    		if(!stencero.equals("")){
    			sql.append("\n , concepto = '" + Utilerias.validarCadenaSQL(stencero) + "'+concepto ");
    		}
    		
    		sql.append("\n where cve_control ='" + Utilerias.validarCadenaSQL(cveControl) + "' ");
    			    
    		count = jdbcTemplate.update(sql.toString());
    			    
    		if(count > 0){
    			resultado.put("mensaje", "ok");
    		}else{
    			resultado.put("error", "Error al actualizar montos en la nueva propuesta de pago.");
    		}
    		
		 }catch(Exception e){
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:actualizaMontoPropuesta");
			 resultado.put("error", "Error al actualizar monto.");
		 }
		
		return resultado;
		
	}

	@Override
	public Map<String, String> actualizaPropuesta(String nvaCveControl, String noDoctos, String oldCveControl, String fecha) {
		
		Map<String, String> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		StringBuffer sql = new StringBuffer();
		int count = 0;
		try{
	    	
    		sql.append("\n UPDATE movimiento ");
    		sql.append("\n set cve_control='"+Utilerias.validarCadenaSQL(nvaCveControl)+"' ");
    		sql.append("\n ,fec_propuesta = '"+Utilerias.validarCadenaSQL(fecha)+"' ");
    		sql.append("\n ,fec_valor = '"+Utilerias.validarCadenaSQL(fecha)+"'");
    		sql.append("\n  ,autoriza = null ");
    		//sql.append("\n ,hora_recibo = null ");
    		sql.append("\n where no_docto in ("+noDoctos+")");
    		sql.append("\n and cve_control = '"+Utilerias.validarCadenaSQL(oldCveControl)+"' ");
    		
    		count = jdbcTemplate.update(sql.toString());
    			    
    		if(count > 0){
    			resultado.put("mensaje", "ok");
    		}else{
    			resultado.put("error", "Error al actualizar montos en la nueva propuesta de pago.");
    		}
    		
		 }catch(Exception e){
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:actualizaPropuesta");
			 resultado.put("error", "Error al actualizar la propuesta original.");
		 }
		
		return resultado;
	}
	
	public List<LlenaComboGralDto> llenarComboGrupoEmpresaUnica(GrupoEmpresasDto dto){
		//system.out.println("sql");
		dto.setPagoEmpresa("");//Se inicializa asi de acuerdo a la migracion
		String sql="";
		List<LlenaComboGralDto>listDatos=new ArrayList<LlenaComboGralDto>();
			try{

			    	sql+=" Select e.no_empresa,e.nom_empresa from empresa e, usuario_empresa ue";
			        sql+=" Where e.no_empresa = ue.no_empresa";
			        sql+=" and ue.no_usuario = "+Utilerias.validarCadenaSQL(dto.getIdUsuario());
			        sql+=" and e.no_empresa = "+Utilerias.validarCadenaSQL(dto.getIdEmpresa());
			        sql+=" order by e.no_empresa";
			    
				//system.out.println(sql.toString());
				 listDatos= jdbcTemplate.query(sql, new RowMapper<LlenaComboGralDto>(){
						public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
							LlenaComboGralDto cons = new LlenaComboGralDto();
							cons.setId(rs.getInt("no_empresa"));
							cons.setDescripcion(rs.getString("nom_empresa"));
							return cons;
						}});
			            
			}catch(Exception e){
				//system.out.println("error sql");
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Egresos, C:ConsultaPropuestasDao , M:llenarComboGrupoEmpresaUnica");
			}
		    return listDatos;
	}

	@Override
	public Map<String, String> insertaBitacoraPropuesta(List<BitacoraPropuestasDto> listBitacora) {
		
		Map<String, String> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		StringBuffer sql = new StringBuffer();
		int count = 0;
		try{
	    	
			for(int i=0; i<listBitacora.size(); i++){
				if(sql.length() > 0)
					sql.delete(0, sql.length());
				
				sql.append("\n INSERT INTO BITACORA_PROPUESTAS ");
				sql.append("\n VALUES(getdate(), "+Utilerias.validarCadenaSQL(listBitacora.get(i).getUsuario())+", "); 
				sql.append("\n '"+Utilerias.validarCadenaSQL(listBitacora.get(i).getOperacion())+"', ");
				sql.append("\n '"+Utilerias.validarCadenaSQL(listBitacora.get(i).getPropuesta())+"', ");
				sql.append("\n '"+Utilerias.validarCadenaSQL(listBitacora.get(i).getValorAnt())+"', ");
				sql.append("\n '"+Utilerias.validarCadenaSQL(listBitacora.get(i).getValorNuevo())+"' )");
				
				System.out.println("Inserta bitacora Propuesta: "+ sql.toString());
				count = jdbcTemplate.update(sql.toString());
				
				if(count > 0){
	    			resultado.put("mensaje", "ok");
	    		}else{
	    			resultado.put("error", "Error al insertar en bitacora propuesta.");
	    			return resultado;
	    		}
				
			}
    		
		 }catch(Exception e){
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:insertaBitacoraPropuesta");
			 resultado.put("error", "Error al insertar bitacora propuesta.");
		 }
		
		return resultado;
		
	}

	public int validaFacultadUsuario(String facultad) {
		
		int cont = 0;
		StringBuffer sql = new StringBuffer();

		try{
				sql.append("\n SELECT COUNT(id_boton) ");
				sql.append("\n FROM BOTON_USUARIO ");
				sql.append("\n WHERE id_boton = '" + Utilerias.validarCadenaSQL(facultad) + "' ");
				sql.append("\n AND id_usuario = " + GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario() + " ");
			    System.out.println("sql valida facultad "+sql);
				cont = jdbcTemplate.queryForInt(sql.toString());
			
		}catch(Exception e){
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Egresos, C:ConsultaPropuestasDao, M:validaFacultadUsuario");
		}
		
		return cont;		 
	}
	
	//Agregado EMS  06/01/2016 - Para identificar las chequeras benef que no existen en ctas_banco, para no poder ser autorizadas.
	@Override
	public int noExisteChequeraBenef(String cveControl) {
		
		int cont = 0;
		StringBuffer sql = new StringBuffer();

		try{
			sql.append(" SELECT no_cliente, no_folio_det \n");
			sql.append(" FROM  movimiento ");
			sql.append("\n WHERE cve_control = '" +Utilerias.validarCadenaSQL(cveControl)+ "' ");
			sql.append("\n AND ID_FORMA_PAGO = 3 ");
			sql.append("\n AND ID_TIPO_OPERACION = 3000 ");
	        sql.append("\n AND ID_ESTATUS_MOV = 'N' ");

	        System.out.println("\n Query para obtener folios: " + sql.toString()); 

	        List<String[]> folios = jdbcTemplate.query(sql.toString(), new RowMapper<String[]>(){
				public String[] mapRow(ResultSet rs, int idx) throws SQLException {
					String[] folio = new String[2];
					folio[0] = rs.getString("no_cliente"); 
					folio[1] = rs.getString("no_folio_det"); 
					return folio;
				}});
	        
			for (int i = 0; i < folios.size(); i++) {
				if (!folios.get(i)[0].equals("999999") && !folios.get(i)[0].equals("999998")) {
					sql = new StringBuffer();
					sql.append("\n SELECT COUNT(m.ID_CHEQUERA_BENEF) ");
					sql.append("\n FROM MOVIMIENTO m ");
					sql.append("\n WHERE m.cve_control = '" +Utilerias.validarCadenaSQL(cveControl)+ "' ");
					sql.append("\n AND m.ID_FORMA_PAGO = 3 ");
					sql.append("\n AND m.ID_CHEQUERA_BENEF NOT IN (SELECT cb.id_chequera ");
					sql.append("\n                                FROM CTAS_BANCO cb ");
					sql.append("\n                                WHERE m.NO_CLIENTE = cb.NO_PERSONA ");
					sql.append("\n                                AND cb.ID_TIPO_PERSONA = 'P') ");
					sql.append("\n AND m.ID_TIPO_OPERACION = 3000 ");
			        sql.append("\n AND m.ID_ESTATUS_MOV = 'N' ");
			        sql.append("\n AND m.NO_FOLIO_DET = " + folios.get(i)[1]);
			        
			        System.out.println("\n Query para verficar chequeras: " + sql.toString());
			            
					cont = jdbcTemplate.queryForInt(sql.toString());
					
					if (cont > 0) return cont;
				} else cont = 0;
			}			
		}catch(Exception e){
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Egresos, C:ConsultaPropuestasDao, M:noExisteChequeraBenef ");
		}
		return cont;		 
	}
	
	@Override
	public int chequeraCIE(String cveControl) {
		
		int cont = 0;
		StringBuffer sql = new StringBuffer();

		try{
			sql.append(" SELECT no_cliente, no_folio_det \n");
			sql.append(" FROM  movimiento ");
			sql.append("\n WHERE cve_control = '" +Utilerias.validarCadenaSQL(cveControl)+ "' ");
			sql.append("\n AND ID_FORMA_PAGO = 3 ");
			sql.append("\n AND ID_TIPO_OPERACION = 3000 ");
	        sql.append("\n AND ID_ESTATUS_MOV = 'N' ");

	        System.out.println("\n Query para obtener folios: " + sql.toString()); 

	        List<String[]> folios = jdbcTemplate.query(sql.toString(), new RowMapper<String[]>(){
				public String[] mapRow(ResultSet rs, int idx) throws SQLException {
					String[] folio = new String[2];
					folio[0] = rs.getString("no_cliente"); 
					folio[1] = rs.getString("no_folio_det"); 
					return folio;
				}});
	        
			for (int i = 0; i < folios.size(); i++) {
				if (!folios.get(i)[0].equals("999999") && !folios.get(i)[0].equals("999998")) {
					sql = new StringBuffer();
					sql.append("\n SELECT COUNT(m.ID_CHEQUERA_BENEF) ");
					sql.append("\n FROM MOVIMIENTO m ");
					sql.append("\n WHERE m.cve_control = '" +Utilerias.validarCadenaSQL(cveControl)+ "' ");
					sql.append("\n AND m.ID_FORMA_PAGO = 3 ");
					sql.append("\n AND ((id_banco <> 12 AND m.ID_CHEQUERA_BENEF like 'CONV%') "
							+ "			OR (id_banco <> 14 AND m.ID_CHEQUERA_BENEF like 'SANT%'))");
					sql.append("\n AND m.ID_TIPO_OPERACION = 3000 ");
			        sql.append("\n AND m.ID_ESTATUS_MOV = 'N' ");
			        sql.append("\n AND m.NO_FOLIO_DET = " + folios.get(i)[1]);
			        
			        System.out.println("\n Query para verficar chequerasCIE: " + sql.toString());
			            
					cont = jdbcTemplate.queryForInt(sql.toString());
					
					if (cont > 0) return cont;
				} else cont = 0;
			}			
		}catch(Exception e){
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Egresos, C:ConsultaPropuestasDao, M:chequeraCIE ");
		}
		return cont;		 
	}

	//Agregado EMS  06/01/2016 - Para identificar las chequeras benef que si existen en ctas_banco
		@Override
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
				sql.append( "\n   WHERE no_usuario = "+ Utilerias.validarCadenaSQL(usuario) + ")");
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
				+ "P:Egresos, C:ConsultaPropuestasDaoImpl, M:llenarComboEmpresa");
			}
			return listEmpresa;
		    
		}

		@Override
		public List<String> validarBancoChequeraPagadoraBenef(String clavesControl, int tipoConsulta) {
			
			StringBuffer sql = new StringBuffer();
			//int cont = 0;
			List<String> listClaves = new ArrayList<>();
			
			try{
				
				//tipoConsulta - 1: Revisa banco/chequera pagadoras, 2: revisa banco/chequera beneficiaria.
				sql.append( " SELECT DISTINCT CVE_CONTROL FROM MOVIMIENTO ");
				sql.append( "\n where cve_control in("+clavesControl+") ");
				sql.append( "\n  AND ID_TIPO_OPERACION = 3000 ");
				
				if(tipoConsulta == 1){
					sql.append( "\n  AND (ID_BANCO is null OR ID_BANCO = 0 OR ID_CHEQUERA IS NULL OR ID_CHEQUERA = '') ");
				}else if(tipoConsulta == 2){
					sql.append( "\n  AND (ID_BANCO_BENEF is null OR ID_BANCO_BENEF = 0 OR ID_CHEQUERA_BENEF IS NULL OR ID_CHEQUERA_BENEF = '') ");
					sql.append( "\n  AND ID_FORMA_PAGO = 3 ");
				}
				System.out.println("sql revisa banco/chequera "+sql);
				listClaves = jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
					public String mapRow(ResultSet rs, int idx) throws SQLException {
						String clave = "";
						clave = rs.getString("CVE_CONTROL");
						return clave;
					}});
				
			}catch(CannotGetJdbcConnectionException e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Impresion, C:ImpresionDaoImpl, M:validarBancoChequeraPagadoraBenef");
			}catch(Exception e){
				e.printStackTrace();
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Egresos, C:ConsultaPropuestasDaoImpl, M:validarBancoChequeraPagadoraBenef");
			}
			
			return listClaves;
		}
		
		/**
		 * ACTUALIZA EL BANCO Y CHEQUERA PAGADORA DE OTRA EMPRESA, 
		 * LA EMPRESA DE DONDE SE ACTUALIZARAN LOS DATOS QUEDA GUARDADA EN NO_COBRADOR.
		 */
		@Override
		public Map<String, String> actualizaMultisociedad(String cveControl, 
														  StringBuffer folios, 
														  MovimientoDto movDatos) {
			
			StringBuffer sql = new StringBuffer();
			int cont = 0;
			
			Map<String, String> resultado = new HashMap<>();
			resultado.put("error", "");
			resultado.put("msgUsuario", "");
			
			try{
				
				sql.append( "\n UPDATE MOVIMIENTO ");
				sql.append( "\n SET ID_BANCO = "+movDatos.getIdBanco()+" ");
				sql.append( "\n , ID_CHEQUERA = '"+Utilerias.validarCadenaSQL(movDatos.getIdChequera())+"' ");
				sql.append( "\n , NO_COBRADOR = '"+movDatos.getNoEmpresa()+"' ");
				
				sql.append( "\n WHERE CVE_CONTROL = '"+Utilerias.validarCadenaSQL(cveControl)+"' ");
				sql.append( "\n AND NO_FOLIO_DET IN("+folios+") ");
				
				cont = jdbcTemplate.update(sql.toString());
				
				if(cont <= 0){
					resultado.put("error", "Error al actualizar, favor de revisar los datos.");
				}else{
					resultado.put("msgUsuario", "Banco/Chequera multisociedad actualizada con ï¿½xito.");
				}
				
			}catch(CannotGetJdbcConnectionException e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Impresion, C:ImpresionDaoImpl, M:validarBancoChequeraPagadoraBenef");
			}catch(Exception e){
				e.printStackTrace();
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
				+ "P:Egresos, C:ConsultaPropuestasDaoImpl, M:validarBancoChequeraPagadoraBenef");
			}
			
			return resultado;
		}
		
		//Agregado EMS 31/01/2016 - valida si la propuesta tiene un movimiento que fue modificado por multisociedad
		@Override
		public int modificadoMultisociedad(String cveControl) {
			
			StringBuffer sql = new StringBuffer();
			int count = 0;
			
			try{
		    	
				
				
	    		sql.append("\n SELECT coalesce(count(no_cobrador),0) as no_cobrador ");
		    	sql.append("\n FROM movimiento ");
		    	sql.append("\n  WHERE cve_control = '"+ Utilerias.validarCadenaSQL(cveControl) +"' ");
		    	sql.append("\n AND ID_TIPO_OPERACION = 3000 ");
		        sql.append("\n AND ID_ESTATUS_MOV = 'N' ");
		        
			    count = jdbcTemplate.queryForInt(sql.toString());
			  
			 }catch(Exception e){
				 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Egresos, C:ConsultaPropuestasDao, M:modificadoMultisociedad");
			 }
			
			return count;
		}

		@Override
		public int consultaPropInterbancario(String cveControl) {
			
			StringBuffer sql = new StringBuffer();
			int count = 0;
			
			try{
		    	
	    		sql.append("\n select count(cb.ID_BANCO) ");
	    		sql.append("\n from MOVIMIENTO m JOIN CTAS_BANCO cb ON (m.ID_BANCO_BENEF = cb.ID_BANCO AND m.ID_CHEQUERA_BENEF = cb.ID_CHEQUERA) ");
	    		sql.append("\n JOIN PERSONA p ON (m.no_cliente = p.no_persona AND p.no_persona = cb.no_persona) ");
	    		sql.append("\n where m.id_banco <> m.id_banco_benef ");
	    		sql.append("\n AND m.id_tipo_operacion IN (3000, 3801) ");
	    		sql.append("\n AND m.id_estatus_mov IN ('N', 'L') ");
	    		sql.append("\n AND m.ID_banco_benef <> 0 ");
	    		sql.append("\n AND m.ID_DIVISA = 'MN' ");
	    		sql.append("\n AND m.ID_CHEQUERA_BENEF IS NOT NULL ");
	    		sql.append("\n AND m.CVE_CONTROL = '"+Utilerias.validarCadenaSQL(cveControl)+"' ");
	    		sql.append("\n AND cb.ID_CLABE IS NULL ");
	    		sql.append("\n AND LEN(cb.id_chequera) <> 16 ");
	    		sql.append("\n AND cb.ID_TIPO_PERSONA = 'P' ");
	    		
			    count = jdbcTemplate.queryForInt(sql.toString());
			  
			 }catch(Exception e){
				 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Egresos, C:ConsultaPropuestasDao, M:consultaPropInterbancario");
			 }
			
			return count;
		}
		
		
		@Override
		public List<ColoresBitacoraDto> obtenerColoresPropuesta() {
			
			StringBuffer sql = new StringBuffer();
			List<ColoresBitacoraDto> result = new ArrayList<>();
			
			try{
		    	
	    		sql.append("\n select tipo, color, descripcion_uno, descripcion_dos ");
	    		sql.append("\n from cat_colores_bit ");
	    		sql.append("\n where tipo = 'P' ");
	    		sql.append("\n order by color, descripcion_uno desc ");
		    	
	    		result = jdbcTemplate.query(sql.toString(), new RowMapper<ColoresBitacoraDto>(){
					public ColoresBitacoraDto mapRow(ResultSet rs, int idx) throws SQLException {
						ColoresBitacoraDto colores = new ColoresBitacoraDto();
						colores.setTipo(rs.getString("tipo"));
						colores.setColor(rs.getString("color"));
						colores.setDescripcionUno(rs.getString("descripcion_uno"));
						colores.setDescripcionDos(rs.getString("descripcion_dos"));
						return colores;
					}});
			  
			 }catch(Exception e){
				 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Egresos, C:ConsultaPropuestasDao, M:obtenerColoresPropuesta");
			 }
			
			return result;
		}
		
		@Override
		public List<ColoresBitacoraDto> obtenerColoresDetalle() {
			
			StringBuffer sql = new StringBuffer();
			List<ColoresBitacoraDto> result = new ArrayList<>();
			
			try{
		    	
	    		sql.append("\n select tipo, color, descripcion_uno, descripcion_dos ");
	    		sql.append("\n from cat_colores_bit ");
	    		sql.append("\n where tipo = 'D' ");
	    		sql.append("\n order by color, descripcion_uno desc ");
		    	
	    		result = jdbcTemplate.query(sql.toString(), new RowMapper<ColoresBitacoraDto>(){
					public ColoresBitacoraDto mapRow(ResultSet rs, int idx) throws SQLException {
						ColoresBitacoraDto colores = new ColoresBitacoraDto();
						colores.setTipo(rs.getString("tipo"));
						colores.setColor(rs.getString("color"));
						colores.setDescripcionUno(rs.getString("descripcion_uno"));
						colores.setDescripcionDos(rs.getString("descripcion_dos"));
						return colores;
					}});
			  
			 }catch(Exception e){
				 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Egresos, C:ConsultaPropuestasDao, M:obtenerColoresDetalle");
			 }
			
			return result;
		}
		
		@Override
		public int consultaRFC(String cveControl) {
			
			StringBuffer sql = new StringBuffer();
			List<MovimientoDto> lstMovto = new ArrayList<>();
			int count = 0;
			
			try{
		    	//
				
				sql.append("\n SELECT DISTINCT m.no_cliente ");
				sql.append("\n FROM MOVIMIENTO m ");
				sql.append("\n WHERE m.CVE_CONTROL = '"+Utilerias.validarCadenaSQL(cveControl)+"' ");
				sql.append("\n AND m.ID_TIPO_OPERACION = 3000 ");
				sql.append("\n AND m.ID_ESTATUS_MOV = 'N' ");
				System.out.println("Query"+sql.toString());
				/*
				sql.append("\n SELECT DISTINCT m.no_cliente ");
				sql.append("\n FROM MOVIMIENTO m join persona p on m.no_cliente = p.no_persona ");
				sql.append("\n WHERE m.CVE_CONTROL = '"+Utilerias.validarCadenaSQL(cveControl)+"' ");
				sql.append("\n AND m.ID_TIPO_OPERACION = 3000 ");
				sql.append("\n AND m.ID_ESTATUS_MOV = 'N' ");
				sql.append("\n AND p.id_tipo_persona = 'P' ");
				sql.append("\n AND (NOT regexp_like (p.rfc,'^(([A-Z]|[a-z]){4})([0-9]{6})((([A-Z]|[a-z]|[0-9]){3}))') ");
				sql.append("\n AND NOT regexp_like (p.rfc,'^(([A-Z]|[a-z]){3})([0-9]{6})((([A-Z]|[a-z]|[0-9]){3}))')) ");
				
				//count = jdbcTemplate.queryForInt(sql.toString());
				*/
				lstMovto = jdbcTemplate.query(sql.toString(), new RowMapper<MovimientoDto>(){
					public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
						MovimientoDto movto = new MovimientoDto();
						movto.setNoCliente(rs.getString("no_cliente"));
						return movto;
					}});
				
				//count = lstMovto.size();
				
				
				for(int i=0; i< lstMovto.size(); i++){
					
					if(sql.length() > 0)
						sql.delete(0, sql.length());
					
					sql.append("\n SELECT count(P.RFC) ");
		    		sql.append("\n FROM PERSONA P ");
		    		sql.append("\n WHERE ( patindex (P.rfc,'^(([A-Z]|[a-z]){4})([0-9]{6})((([A-Z]|[a-z]|[0-9]){3}))')!=0 ");
		    		sql.append("\n AND  patindex (P.rfc,'^(([A-Z]|[a-z]){3})([0-9]{6})((([A-Z]|[a-z]|[0-9]){3}))')!=0) ");
		    		sql.append("\n AND P.id_tipo_persona = 'P' ");
		    		sql.append("\n AND P.no_persona = '"+lstMovto.get(i).getNoCliente()+"' ");
		    		//sql.append("\n AND P.no_persona = '"+lstMovto.get(i).getNoCliente()+"' ");
		    		System.out.println("Query"+sql.toString());
		    		count = jdbcTemplate.queryForInt(sql.toString());
		    		
		    		if(count > 0)
		    			break;
				}
			  
			 }catch(Exception e){
				 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Egresos, C:ConsultaPropuestasDao, M:consultaRFC");
			 }
		
			return count;
		}
		
		@Override
		public Map<String, String> actualizaCompraTransfer(String claves, String operacion) {
			
			Map<String, String> result = new HashMap<>();
			result.put("error", "");
			result.put("mensaje", "");
			
			StringBuffer sql = new StringBuffer();
			int count = 0;
			
			try{
				
				sql.append("\n UPDATE SELECCION_AUTOMATICA_GRUPO ");
				
				if(operacion.equals("M")){
					sql.append("\n SET HABILITADO = 'T' ");
				}else if(operacion.equals("D")){
					sql.append("\n SET HABILITADO = NULL ");
				}
				
				sql.append("\n WHERE CVE_CONTROL IN ("+claves+") ");
				
				count = jdbcTemplate.update(sql.toString());
				
				if(count > 0 ){
					result.put("mensaje", "Propuestas marcadas con ï¿½xito para compra de transferencias. ");
				}else{
					result.put("error", "Error al marcar las propuestas para compra de transferencias.");
				}
				
			 }catch(Exception e){
				 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Egresos, C:ConsultaPropuestasDaoImpl, M:actualizaCompraTransfer");
				 
				 result.put("error", "Error al marcar las propuestas para compra de transferencias.");
				 
			 }
			
			return result;
		}

	@Override
	public String sumaTotalDetalles(String cveControl) {
		
		String result = "";
		StringBuffer sql = new StringBuffer();
		//int count = 0;
		
		List<String> lst = new ArrayList<>();
		
		try{
			
			sql.append("\n SELECT SUM(IMPORTE) AS SUMA ");
			sql.append("\n FROM MOVIMIENTO ");
			sql.append("\n WHERE CVE_CONTROL = '"+Utilerias.validarCadenaSQL(cveControl)+"' ");
			sql.append("\n AND ID_TIPO_OPERACION = 3000 ");
			sql.append("\n AND ID_ESTATUS_MOV = 'N' ");
			
			lst = jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
				public String mapRow(ResultSet rs, int idx) throws SQLException {
					String sum = new String();
					sum = rs.getString("suma");
					return sum;
				}});
			
			if(lst.size() > 0 ){
				result = lst.get(0);
				
				if(result.equals("null"))
					result = "0";
					
			}else{
				result = "0";
			}
			
		 }catch(Exception e){
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDaoImpl, M:actualizaCompraTransfer");
			 result = "0";
		 }
		
		return result;
	}
	
	public List<Map<String,String>> obtenerDetallesReporte(int idUsuario){
		List<Map<String, String>> resultado = new ArrayList<Map<String,String>>();
		StringBuffer sql = new StringBuffer();
		
		try {
			
			sql.append("\n Select distinct cve_control, no_docto, no_empresa, importe_original,  ");
			sql.append("\n 			desc_estatus, no_factura, fec_valor,");
			sql.append("\n   		fec_valor_original, desc_cve_operacion, ");
			sql.append("\n 			importe, ");
			sql.append("\n 			id_divisa, id_forma_pago, desc_forma_pago, ");
			sql.append("\n 			desc_banco, id_chequera_benef, beneficiario, ");
			sql.append("\n 			concepto, no_folio_det, no_folio_mov, ");
			sql.append("\n 			no_cuenta, id_chequera, id_cve_operacion,");
			sql.append("\n 			id_forma_pago, id_banco_benef, id_caja,");
			sql.append("\n 			id_leyenda, id_estatus_mov, no_cliente, ");
			sql.append("\n 			tipo_cambio, origen_mov, solicita, ");
			sql.append("\n 			autoriza, observacion, lote_entrada, ");
			sql.append("\n 			desc_caja, agrupa1, agrupa2, id_rubro, ");
			sql.append("\n 			agrupa3, b_servicios, no_pedido, ");
			sql.append("\n 			importe_mn, nom_proveedor, ");
			sql.append("\n 			fec_propuesta, id_divisa_original, ");
			sql.append("\n 			nom_empresa, id_banco_pago, ");
			sql.append("\n 			id_chequera_pago, ");
			sql.append("\n 			desc_banco_pago, desc_grupo_cupo,");
			sql.append("\n 			dias, NumCheq_PagoMN, NumCheq_PagoDLS, ");
			sql.append("\n 			NumCheq_CruceMN, NumCheq_CruceDLS, ");
			sql.append("\n 			NumCheqEmp, NumCheqCte, id_servicio_be, id_contable, ");
			sql.append("\n 			equivale, no_cheque,usuario_uno, usuario_dos  ");
			sql.append("\n 			,fecha_contabilizacion, fec_operacion ");
			//sql.append("\n 			,color, invoice_type, id_clabe, rfc ");
			sql.append("\n 			,color, invoice_type, id_clabe ");
			
			sql.append("\n ");
			//sql.append("\n FROM TMP_MOVIMIENTO_BIT "); 
			sql.append("\n FROM TMP_DETALLE_PROV ");
			sql.append("\n WHERE ID_USUARIO = "+idUsuario+" ");
			sql.append("\n ORDER BY NO_EMPRESA, BENEFICIARIO ");
			
			
	        System.out.println("Sacar detalle Excel: " + sql.toString());
	        
	        resultado = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String> >(){
				public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException {
					Map<String, String> map = new HashMap<>();
					DecimalFormatSymbols symbols = new DecimalFormatSymbols();
					symbols.setDecimalSeparator('.');
					symbols.setGroupingSeparator(',');
					String pattern = "###,##0.00";
					DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
					
					map.put("noEmpresa", String.valueOf(rs.getInt("no_empresa"))); 
					map.put("nomEmpresa", rs.getString("nom_empresa")); 
					map.put("equivale", rs.getString("equivale"));
					map.put("beneficiario", rs.getString("beneficiario"));
					map.put("noFactura", rs.getString("no_factura"));
					map.put("noDocto", rs.getString("no_docto")); 
					map.put("idContable", rs.getString("id_contable"));
					map.put("importe", decimalFormat.format(rs.getDouble("importe"))+"");
					map.put("idDivisa", rs.getString("id_divisa"));
					map.put("descFormaPago", rs.getString("desc_forma_pago")); 
					map.put("concepto", rs.getString("concepto")); 
					map.put("fecPropuesta", funciones.ponerFechaSola(rs.getDate("fec_propuesta")));
					map.put("fecValor", funciones.ponerFechaSola(rs.getDate("fec_valor")));
					map.put("fecContabilizacion", (rs.getDate("fecha_contabilizacion")!= null)?funciones.ponerFechaSola(rs.getDate("fecha_contabilizacion" )):"");
					map.put("fecOperacion", funciones.ponerFechaSola(rs.getDate("fec_operacion")));
					map.put("bancoPago", rs.getString("desc_banco_pago")); 
					map.put("idChequera", rs.getString("id_chequera")); 
					map.put("idBancoBenef", rs.getString("desc_banco"));
					map.put("idChequeraBenef", rs.getString("id_chequera_benef")); 
					
					return map;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:obtenerDetallesReporte");
			 
		}
		
		return resultado;
	}
		
	@Override
	public Map<String, String> actualizarChequeraNoAgrupa(String cveControl, String folios) {
		
		Map<String, String> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		StringBuffer sql = new StringBuffer();
		int count = 0;
		try{
    		
    		sql.append("\n UPDATE MOVIMIENTO ");
	    	sql.append("\n SET C_LOTE = '1' ");
	    	sql.append("\n WHERE cve_control = '" + Utilerias.validarCadenaSQL(cveControl) + "' ");
	    	sql.append("\n AND NO_FOLIO_DET IN("+folios+") ");
	    		    
		    count = jdbcTemplate.update(sql.toString());
		  
		    if(count > 0){
		    	resultado.put("mensaje", "Datos modificados correctamente.");
		    }else{
		    	resultado.put("error", "Error al actualizar los datos.");
		    }

		 }catch(Exception e){
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:eliminarBloqueo");
		 }
		
		return resultado;
	}
	@Override
	public String verificaFacultadUsuario(String facultad) {
		
		String result = "";
		
		StringBuffer sql = new StringBuffer();
		List<String> res = new ArrayList<>();
		
		try{
    		
    		sql.append("\n SELECT ID_BOTON ");
	    	sql.append("\n FROM BOTON_USUARIO ");
	    	sql.append("\n WHERE ID_BOTON = '"+Utilerias.validarCadenaSQL(facultad)+"' ");
	    	sql.append("\n AND ID_USUARIO = '"+GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+"' ");
	    	
	    	System.out.println("obtiene perfil: " + sql.toString());
	        
	    	res = jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
				public String mapRow(ResultSet rs, int idx) throws SQLException {
					String per = rs.getString("ID_BOTON");
					return per;
				}});
	        
	        if(res.size()>0){
	        	result = res.get(0);
	        }
	        	
		 }catch(Exception e){
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:verificaFacultadUsuario");
		 }
		
		return result;
	}

	@Override
	public boolean verificaPropuesta(String cveControl) {	
		try{    		
			StringBuffer sql = new StringBuffer();	
			
    		sql.append(" SELECT COUNT(*) 							\n");
    		sql.append(" FROM movimiento 							\n");
    		sql.append(" WHERE cve_control = '" + cveControl + "' 	\n");
    		sql.append(" AND id_tipo_operacion = 3000 				\n");  		
    		
    		return jdbcTemplate.queryForInt(sql.toString()) > 0 ? false : true;
		} catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:verificaFacultadUsuario");
			return false;
		}
	}

	@Override
	public Map<String, String> eliminaPropuesta(String cveControl) {
		Map<String, String> respuesta = new HashMap<String, String>();
		try{    		
			StringBuffer sql = new StringBuffer();	
			
    		sql.append(" DELETE FROM seleccion_automatica_grupo 	\n");
    		sql.append(" WHERE cve_control = '" + cveControl + "' 	\n");
    		
    		int res = jdbcTemplate.update(sql.toString());
    		if (res > 0) respuesta.put("mensaje", "Propuestas Eliminadas");
    		else respuesta.put("mensaje", "Propuestas Eliminadas");
    		return respuesta;
		} catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:verificaFacultadUsuario");
			respuesta.put("error", "0");
			respuesta.put("mensaje", "No se puedo eliminar");
			return respuesta;
		}
	}

	@Override
	public int pagoCruzado(String cveControl) {
		try{    		
			StringBuffer sql = new StringBuffer();	
			
    		sql.append(" SELECT COUNT(*) 							\n");
    		sql.append(" FROM movimiento 							\n");
    		sql.append(" WHERE cve_control = '" + cveControl + "' 	\n");
    		sql.append(" AND id_leyenda  = '*' 				\n");  		
    		System.out.println(sql.toString());
    		
    		return jdbcTemplate.queryForInt(sql.toString());    		
		} catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:pagoCrzuado");
			return 0;
		}
	}

	@Override
	public int confPagoCruzado(String cveControl) {
		try{    		
			StringBuffer sql = new StringBuffer();	
			
    		sql.append(" SELECT COUNT(*)  							\n");
    		sql.append(" FROM Conf_Pago_Cruzado  					\n");
    		sql.append(" WHERE NO_PROVEEDOR IN ( 					\n");
    		sql.append(" 	SELECT equivale_persona  				\n");
    		sql.append(" 	FROM persona  							\n");
    		sql.append(" 	WHERE no_persona IN ( 					\n");
    		sql.append(" 		SELECT TOP 1 no_cliente  			\n");
    		sql.append(" 		FROM movimiento  					\n");
    		sql.append(" 		WHERE cve_control = '" + cveControl + "')) \n");  		
    		System.out.println(sql.toString());
    		
    		return jdbcTemplate.queryForInt(sql.toString());    		
		} catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:confPagoCruzado");
			return 0;
		}
	}

	@Override
	public int verificaCompraTransfer(String cveControl) {
		try{    		
			StringBuffer sql = new StringBuffer();	
			
    		sql.append(" SELECT TOP 1 											\n");
    		sql.append(" 	CASE WHEN (SELECT TOP 1 id_divisa  					\n");
    		sql.append(" 				FROM cat_cta_banco  					\n");
    		sql.append(" 				WHERE id_chequera IN (M.id_chequera)) 	\n");
    		sql.append(" 	<> (SELECT TOP 1 id_divisa  						\n");
    		sql.append(" 		FROM ctas_banco  								\n");
    		sql.append(" 		WHERE id_chequera IN (M.id_chequera_benef))		\n");
    		sql.append(" 	THEN 1 ELSE 0 END AS TRANSF 						\n");
    		sql.append(" FROM movimiento M  									\n");
    		sql.append(" 		WHERE cve_control = '" + cveControl + "' 		\n");  		
    		System.out.println(sql.toString());
    		
    		return jdbcTemplate.queryForInt(sql.toString());    		
		} catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:confPagoCruzado");
			return 0;
		}
	}
	public List<Map<String, String>>consultarDetalleStoredParaExcel(PagosPropuestosDto dto){
		StringBuffer sql= new StringBuffer();
	
		List<Map<String, String>>listDetalle= new ArrayList<Map<String, String>>();
		//Map<String, Object> resultStored = new HashMap<>();
		new HashMap<>();
		try{
				
			int idUsuario = GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario();
			
			StoredProcedure storedDetalles = new StoredProcedure(jdbcTemplate, "SP_CONSULTAR_DET_PROPUESTA");
			storedDetalles.declareParameter("V_id_grupo_emp", dto.getIdGrupoEmpresa(), StoredProcedure.IN);
			storedDetalles.declareParameter("V_cve_control", Utilerias.validarCadenaSQL(dto.getCveControl()), StoredProcedure.IN);
			storedDetalles.declareParameter("V_fecha_hoy", Utilerias.validarCadenaSQL(funciones.ponerFechaSola(new Date())), StoredProcedure.IN);
			storedDetalles.declareParameter("V_id_usuario", idUsuario, StoredProcedure.IN);
			//resultStored = 
			storedDetalles.execute();
		
			//sql.append("\n Select * from TMP_MOVIMIENTO_BIT "); 
			sql.append("\n Select distinct cve_control, no_docto, no_empresa, importe_original,  ");
			sql.append("\n 			desc_estatus, no_factura, fec_valor,");
			sql.append("\n   		fec_valor_original, desc_cve_operacion, ");
			sql.append("\n 			importe, ");
			sql.append("\n 			id_divisa, id_forma_pago, desc_forma_pago, ");
			sql.append("\n 			desc_banco, id_chequera_benef, ");
			sql.append("\n          CASE WHEN cve_control LIKE 'MAN%' THEN CASE WHEN (SELECT COUNT(*) FROM boton_usuario WHERE id_boton = 'VERNOMTES' AND id_usuario="+idUsuario+")> 0 THEN '' ELSE beneficiario END ELSE beneficiario END AS beneficiario,");
			sql.append("\n 			concepto, no_folio_det, no_folio_mov, ");
			sql.append("\n 			no_cuenta, id_chequera, id_cve_operacion,");
			sql.append("\n 			id_forma_pago, id_banco_benef, id_caja,");
			sql.append("\n 			id_leyenda, id_estatus_mov, no_cliente, ");
			sql.append("\n 			tipo_cambio, origen_mov, solicita, ");
			sql.append("\n 			autoriza, observacion, lote_entrada, ");
			sql.append("\n 			desc_caja, agrupa1, agrupa2, id_rubro, ");
			sql.append("\n 			agrupa3, b_servicios, no_pedido, ");
			sql.append("\n 			importe_mn, nom_proveedor, ");
			sql.append("\n 			fec_propuesta, id_divisa_original, ");
			sql.append("\n 			nom_empresa, id_banco_pago, ");
			sql.append("\n 			id_chequera_pago, ");
			sql.append("\n 			desc_banco_pago, desc_grupo_cupo,");
			sql.append("\n 			dias, NumCheq_PagoMN, NumCheq_PagoDLS, ");
			sql.append("\n 			NumCheq_CruceMN, NumCheq_CruceDLS, ");
			sql.append("\n 			NumCheqEmp, NumCheqCte, id_servicio_be, id_contable, ");
			sql.append("\n 			equivale, no_cheque,usuario_uno, usuario_dos  ");
			sql.append("\n 			,fecha_contabilizacion, fec_operacion ");
			sql.append("\n 			,color, invoice_type, id_clabe, rfc , referencia_cte");
			//sql.append("\n 			,color, invoice_type, id_clabe ");
			
			sql.append("\n ");
			//sql.append("\n FROM TMP_MOVIMIENTO_BIT "); 
			sql.append("\n FROM TMP_DETALLE_PROV ");
			
			sql.append("\n WHERE ID_USUARIO = "+idUsuario+" ");
			
			sql.append("\n ORDER BY NO_EMPRESA, BENEFICIARIO ");
			
	        System.out.println("Sacar detalle: " + sql.toString());
	        
	        listDetalle= jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, String>>(){
				public Map<String, String> mapRow(ResultSet rs, int idx) throws SQLException {
					Map<String, String> cons = new HashMap<String, String>();
					cons.put("noEmpresa",rs.getString("no_empresa")); 
					cons.put("nomEmpresa", rs.getString("nom_empresa"));
					cons.put("equivale",rs.getString("equivale"));
					cons.put("beneficiario",rs.getString("beneficiario"));
					cons.put("noFactura",rs.getString("no_factura"));
					cons.put("noDocto",	rs.getString("no_docto"));													
					cons.put("idContable",rs.getString("id_contable"));
					cons.put("importe", rs.getString("importe"));
					cons.put("idDivisa", rs.getString("id_divisa"));
					cons.put("descFormaPago",rs.getString("desc_forma_pago"));
					cons.put("concepto",rs.getString("concepto"));
					cons.put("fecPropuesta",funciones.ponerFechaSola(rs.getDate("fec_propuesta")));
					cons.put("fecValor",funciones.ponerFechaSola(rs.getDate("fec_valor")));
					cons.put("fecContabilizacion",(rs.getDate("fecha_contabilizacion")!= null)?funciones.ponerFechaSola(rs.getDate("fecha_contabilizacion" )):"");
					cons.put("fecOperacion",funciones.ponerFechaSola(rs.getDate("fec_operacion")));
					cons.put("bancoPago",rs.getString("desc_banco_pago"));
					cons.put("idChequera",rs.getString("id_chequera"));
					cons.put("idBancoBenef",rs.getString("id_banco_benef"));
					cons.put("idChequeraBenef",rs.getString("id_chequera_benef"));
					cons.put("clabe",rs.getString("id_clabe"));
					cons.put("referenciaCte",rs.getString("referencia_cte"));
					cons.put("idRubro",rs.getString("id_rubro"));
					cons.put("nombreRegla",rs.getString("cve_control"));
					//cons.put("descRubro",rs.getString("desc_rubro"));
					return cons;
				}});
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:consultarDetalle");
			 
		}
		return listDetalle;
	}

	@Override
	public int consultaAutorizada(String cveControl) {
		try{    		
			StringBuffer sql = new StringBuffer();	
			
    		sql.append(" SELECT COUNT(*)  							\n");
    		sql.append(" FROM SELECCION_AUTOMATICA_GRUPO			\n");
    		sql.append(" WHERE cve_control = '" + cveControl + "'	\n"); 
    		sql.append(" AND usuario_uno IS NOT NULL				\n");  
    		sql.append(" AND usuario_dos IS NOT NULL				\n");  
    		sql.append(" AND usuario_tres IS NOT NULL				\n");  		
    		System.out.println("consulta autorizada "+sql.toString());
    		
    		return jdbcTemplate.queryForInt(sql.toString());    		
		} catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:consultaAutorizada");
			return 0;
		}
	}

	@Override
	public String verificaChequerabenef(String idDivisa, String folio) {
		StringBuffer sql = new StringBuffer();
		String retorno = "";
		try{    					
    		sql.append(" SELECT COUNT(id_divisa)  								\n");
    		sql.append(" FROM ctas_banco										\n");
    		sql.append(" WHERE id_divisa = '" + idDivisa + "'					\n"); 
    		sql.append(" AND no_persona = (SELECT no_cliente					\n");  
    		sql.append("					FROM movimiento						\n");  
    		sql.append(" 					WHERE no_folio_det = " + folio + ")	\n");  
    		
    		if (jdbcTemplate.queryForInt(sql.toString()) <= 0 ) {
    			sql = new StringBuffer();
    			sql.append(" SELECT no_docto,id_divisa 					\n");
    			sql.append(" FROM movimiento 					\n");
    			sql.append(" WHERE no_folio_det = " + folio + "	\n");
    			
    			List<String> noDocto = jdbcTemplate.query(sql.toString(), new RowMapper<String>() {
					@Override
					public String mapRow(ResultSet rs, int arg1) throws SQLException {
						
						return rs.getString("no_docto")+rs.getString("id_divisa");
					}
    			});
    			retorno = noDocto.get(0);
    		}
		} catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:verificaChequerabenef");
		}
		return retorno;
	}
	
	@Override
	public void actualizaReferenciaCte(String noCliente) {
		StringBuffer sql = new StringBuffer();
		try{
    		
    		sql.append("\n UPDATE PERSONA ");
	    	sql.append("\n SET REFERENCIA_CTE = 'concepto' ");
	    	sql.append("\n WHERE NO_PERSONA = '" + Utilerias.validarCadenaSQL(noCliente) + "' ");
		   jdbcTemplate.update(sql.toString());
		  

		 }catch(Exception e){
			 bitacora.insertarRegistro(new Date().toString() + " " + e.toString()
			+ "P:Egresos, C:ConsultaPropuestasDao, M:actualizaReferenciaCte");
		 }
		
	}
	
	@Override
	public String obtenerDivisaChequera(String chequera) {
		StringBuffer sql = new StringBuffer();
		String retorno = "";
		try{    					
    		sql.append(" SELECT id_divisa     								\n");
    		sql.append(" FROM cat_cta_banco										\n");
    		sql.append(" WHERE id_chequera = '" + chequera + "'					\n"); 

    		List<String> result = jdbcTemplate.query(sql.toString(),new RowMapper<String>() {
    			@Override
    			public String mapRow(ResultSet rs, int arg1)throws SQLException {
    				return rs.getString("id_divisa");
    			}
    		});
    		
    		retorno = result.get(0);
		} catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:verificaChequerabenef");
		}
		return retorno;
	}
	
	@Override
	public boolean verificaAutorizacion3801(String cveControl) {
		StringBuffer sql = new StringBuffer();
		boolean retorno = false;
		try{    					
    		sql.append(" SELECT distinct id_tipo_operacion			\n");
    		sql.append(" FROM movimiento							\n");
    		sql.append(" WHERE cve_control = '" + cveControl + "'	\n"); 
    		System.out.println("VerificaAutorizacion "+sql);
    		List<String> result = jdbcTemplate.query(sql.toString(),new RowMapper<String>() {
    			@Override
    			public String mapRow(ResultSet rs, int arg1)throws SQLException {
    				return rs.getString("id_tipo_operacion");
    			}
    		});
    		
    		retorno = result.get(0).equals("3801") ? true : false;
		} catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:verificaChequerabenef");
		}
		return retorno;
	}

	@Override
	public int actualizarAutorizacionProp3801(String psCampo, String string, int idGrupoFlujo, String cveControl) {
		String sql="";
		int res=0;
		try{
			sql+= "UPDATE seleccion_automatica_grupo ";
		    sql+= "		SET usuario_uno = " + string + ", usuario_dos = " + string + ", usuario_tres = " + string + ", ";
		    sql+= "		motivo_rechazo  = null";
		    sql+= " 	WHERE id_grupo_flujo = " +idGrupoFlujo;
		    sql+= "   	and cve_control = '" +cveControl+ "'";
		    System.out.println("actualizaAutorizacion "+sql);
		    res=jdbcTemplate.update(sql);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
			+ "P:Utilerias, C:ConsultasGenerales, M:actualizarAutorizacionPop");
		}
		return res;
	}
	
	public List<SeleccionAutomaticaGrupoDto> obtenerNiveles(GrupoEmpresasDto dto)
	{
		String sql="";
		List<SeleccionAutomaticaGrupoDto> niveles = new ArrayList<SeleccionAutomaticaGrupoDto>();
		try
		{
			//sql.append( " SELECT *FROM cat_grupo_flujo ");
			
	        sql+="SELECT *FROM ";
	        sql+="\n cat_grupo_flujo c, grupo_empresa g, usuario_empresa  u";
	        sql+="\n	WHERE c.id_grupo_flujo = g.id_grupo_flujo";
	        sql+="\n	AND g.no_empresa = u.no_empresa";
	        sql+="\n	AND u.no_usuario = "+dto.getIdUsuario();
	        sql+="\n	ORDER BY desc_grupo_flujo";
			System.out.println("niveles de auto "+sql );
			niveles= jdbcTemplate.query(sql.toString(), new RowMapper<SeleccionAutomaticaGrupoDto>(){
			public SeleccionAutomaticaGrupoDto mapRow(ResultSet rs, int idx) throws SQLException {
				SeleccionAutomaticaGrupoDto cons = new SeleccionAutomaticaGrupoDto();
				cons.setNivelAutorizacion(rs.getInt("nivel_autorizacion"));
				//cons.setNomEmpresa(rs.getString("describ"));
				return cons;
			}});
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ImpresionDaoImpl, M:llenarComboEmpresa");
		}
		return niveles;
	    
	}
	
	
	
	public boolean existeAutorizacion(String cveControl,int usuario, boolean ban, boolean ban2, boolean ban3) {
		StringBuffer sql = new StringBuffer();
		boolean resp = false;
		
		try {
			sql.append(" SELECT COUNT(*) FROM seleccion_automatica_grupo \n");
			sql.append(" WHERE cve_control='"+cveControl+"'");
			
			if(ban)
				sql.append(" And (usuario_uno = "+ usuario +" OR usuario_dos = "+ usuario +")\n");
			
			if(ban2)
				sql.append(" And usuario_uno <> 0 ");
			
			if(ban3)
				sql.append(" And usuario_dos <> 0 ");
			
			System.out.println("ExisteAutorizacion "+sql);
			
			if(jdbcTemplate.queryForInt(sql.toString()) > 0) resp = true;
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConsultaPropuestasDao, M:existeReg");
		}
		return resp;
	}
	public List<Map<String, Object>> obtenerReportePropuestasAutorizadas(PagosPropuestosDto dto){
		StringBuffer sql= new StringBuffer();
	
		List<Map<String, Object>> listResult= new ArrayList<Map<String, Object>>();
		//Map<String, Object> resultStored = new HashMap<>();
		new HashMap<>();
		importeTotal="";
		try{
				
			int idUsuario = GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario();
			StoredProcedure storedDetalles = new StoredProcedure(jdbcTemplate, "SP_CONSULTAR_DET_PROPUESTA");
			storedDetalles.declareParameter("V_id_grupo_emp", dto.getIdGrupoEmpresa(), StoredProcedure.IN);
			storedDetalles.declareParameter("V_cve_control", Utilerias.validarCadenaSQL(dto.getCveControl()), StoredProcedure.IN);
			storedDetalles.declareParameter("V_fecha_hoy", Utilerias.validarCadenaSQL(funciones.ponerFechaSola(new Date())), StoredProcedure.IN);
			storedDetalles.declareParameter("V_id_usuario", idUsuario, StoredProcedure.IN);
			//resultStored = 
			storedDetalles.execute();
			importeTotal=dto.getTotal();
			System.out.println("importee "+importeTotal);
			sql.append("\n Select distinct no_docto, d.no_empresa, importe_original,  ");
			sql.append("\n 			desc_estatus, no_factura, fec_valor,");
			sql.append("\n   		fec_valor_original, desc_cve_operacion, ");
			sql.append("\n 			importe, ");
			sql.append("\n 			id_divisa, id_forma_pago, desc_forma_pago, ");
			sql.append("\n 			desc_banco, id_chequera_benef, beneficiario, ");
			sql.append("\n 			d.concepto, no_folio_det, no_folio_mov, ");
			sql.append("\n 			no_cuenta, id_chequera, id_cve_operacion,");
			sql.append("\n 			id_forma_pago, id_banco_benef, id_caja,");
			sql.append("\n 			id_leyenda, id_estatus_mov, no_cliente, ");
			sql.append("\n 			tipo_cambio, origen_mov, solicita, ");
			sql.append("\n 			autoriza, observacion, lote_entrada, ");
			sql.append("\n 			desc_caja, agrupa1, agrupa2, d.id_rubro, ");
			sql.append("\n 			agrupa3, b_servicios, no_pedido, ");
			sql.append("\n 			importe_mn, nom_proveedor, ");
			sql.append("\n 			fec_propuesta, id_divisa_original, ");
			sql.append("\n 			nom_empresa, id_banco_pago, ");
			sql.append("\n 			id_chequera_pago, ");
			sql.append("\n 			desc_banco_pago, desc_grupo_cupo,");
			sql.append("\n 			dias, NumCheq_PagoMN, NumCheq_PagoDLS, ");
			sql.append("\n 			NumCheq_CruceMN, NumCheq_CruceDLS, ");
			sql.append("\n 			NumCheqEmp, NumCheqCte, id_servicio_be, id_contable, ");
			sql.append("\n 			equivale, no_cheque,d.usuario_uno, d.usuario_dos  ");
			sql.append("\n 			,fecha_contabilizacion, fec_operacion ");
			sql.append("\n 			,color, invoice_type, id_clabe, d.rfc, d.referencia_cte ");
			sql.append("\n 			,s.cve_control,s.concepto as concepto2,p.razon_social");
			//sql.append("\n 			,color, invoice_type, id_clabe ");
			
			sql.append("\n ");
			//sql.append("\n FROM TMP_MOVIMIENTO_BIT "); 
			sql.append("\n FROM TMP_DETALLE_PROV d join seleccion_automatica_grupo s on s.cve_control=d.cve_control");
			sql.append("\n   join persona p on d.equivale=p.equivale_persona");

			
			sql.append("\n WHERE ID_USUARIO = "+idUsuario+" ");
			sql.append("\n and p.id_tipo_persona in('P') ");
			//sql.append("\n ORDER BY NO_EMPRESA, BENEFICIARIO ");
			
	        System.out.println("reporte detalles de propuestas autorizadas: " + sql.toString());
	        listResult = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, Object>>(){
				public Map<String, Object> mapRow(ResultSet rs, int idx) throws SQLException {
					Map<String, Object> map = new HashMap<String, Object>();
					String[] vin;
					String[] con;
					map.put("noEmpresa",rs.getInt("no_empresa"));
					map.put("importeOriginal",funciones.ponerFormatoImporte(rs.getDouble("importe_original")));
					map.put("descEstatus",rs.getString("desc_estatus"));
					map.put("noFactura",rs.getString("no_factura"));
					map.put("fecValorStr",funciones.ponerFechaSola(rs.getDate("fec_valor")));
					map.put("fecValorOriginalStr",funciones.ponerFechaSola(rs.getDate("fec_valor_original")));
					map.put("descCveOperacion",rs.getString("desc_cve_operacion"));
					map.put("importe",rs.getDouble("importe"));
				//	cons.setIdDivisa(rs.getString("id_divisa"));
				//	cons.setIdFormaPago(rs.getInt("id_forma_pago"));
					map.put("fechaHoy",funciones.ponerFechaSola(consultasGenerales.obtenerFechaHoy()));
					map.put("descFormaPago",rs.getString("desc_forma_pago"));
			 //		cons.setDescBancoBenef(rs.getString("desc_banco"));//Descripcion banco beneficiario
					map.put("idChequeraBenef",rs.getString("id_chequera_benef"));
					map.put("beneficiario",rs.getString("beneficiario"));
					con=rs.getString("concepto").split("/");
					//System.out.println("tamaño array concepto "+vin.length);
					
					if(con.length>2){
						//System.out.println("posicion 3 "+con[2].toString());
						map.put("concepto",con[2].toString());
					}else{
						if(con.length>1){
							//System.out.println("posicion 2 "+con[1].toString());
							map.put("concepto",con[1].toString());
						}else{
							map.put("concepto",con[0].toString());
						}	
					}
					
					
					vin=rs.getString("concepto").split("/");
					//System.out.println("tamaño array concepto "+vin.length);
					
					if(vin.length>2){
						//System.out.println("posicion 2 "+vin[1].toString());
						map.put("recep",vin[1].toString());
					}else{
						//System.out.println("posicion 2 vacio");
						map.put("recep","");
					}
		//			cons.setNoFolioDet(rs.getInt("no_folio_det"));
		//			cons.setNoFolioMov(rs.getInt("no_folio_mov"));
					map.put("noCuenta",rs.getInt("no_cuenta"));
					map.put("idChequera",rs.getString("id_chequera"));
		//			cons.setIdCveOperacion(rs.getInt("id_cve_operacion"));
		//			cons.setIdFormaPago(rs.getInt("id_forma_pago"));
		//			cons.setIdBancoBenef(rs.getInt("id_banco_benef"));
		//			cons.setIdCaja(rs.getInt("id_caja"));
		//			cons.setIdLeyenda(rs.getString("id_leyenda"));
		//			cons.setIdEstatusMov(rs.getString("id_estatus_mov"));
		//			cons.setNoCliente(rs.getString("no_cliente"));
		//			cons.setTipoCambio(rs.getDouble("tipo_cambio"));
		//			cons.setOrigenMov(rs.getString("origen_mov"));
					map.put("solicita",rs.getString("solicita"));
		//			cons.setAutoriza(rs.getString("autoriza"));
		//			cons.setObservacion(rs.getString("observacion"));
				//	System.out.println("obser "+rs.getString("observacion"));
	//				cons.setLoteEntrada(rs.getInt("lote_entrada"));
		//			cons.setDescCaja(rs.getString("desc_caja"));
//					cons.setAgrupa1(rs.getString("agrupa1"));
//					cons.setAgrupa2(rs.getString("agrupa2"));
//					cons.setIdRubro(rs.getDouble("id_rubro"));
//					cons.setAgrupa3(rs.getString("agrupa3"));
//					cons.setBServicios(rs.getString("b_servicios"));
//					cons.setNoPedido(rs.getInt("no_pedido"));
//					cons.setImporteMn(rs.getDouble("importe_mn"));
					map.put("nomProveedor",rs.getString("nom_proveedor"));
					map.put("fecPropuestaStr",funciones.ponerFechaSola(rs.getDate("fec_propuesta")));
				//	cons.setIdDivisaOriginal(rs.getString("id_divisa_original"));
					map.put("nomEmpresa",rs.getString("nom_empresa"));
//					cons.setIdBanco(rs.getInt("id_banco_pago"));
//					cons.setIdChequera(rs.getString("id_chequera_pago"));
//					cons.setBancoPago(rs.getString("desc_banco_pago"));
//					cons.setDescGrupoCupo(rs.getString("desc_grupo_cupo")); 
//					cons.setDiasInv(rs.getInt("dias"));
//					cons.setNumCheqPagoMN(rs.getInt("NumCheq_PagoMN"));
//					cons.setNumCheqPagoDLS(rs.getInt("NumCheq_PagoDLS"));	
//					cons.setNumCheqCruceMN(rs.getInt("NumCheq_CruceMN"));
//					cons.setNumCheqCruceDLS(rs.getInt("NumCheq_CruceDLS"));
//					cons.setNumCheqEmp(rs.getInt("NumCheqEmp"));
//					cons.setNumCheqCte(rs.getInt("NumCheqCte"));
//					cons.setIdServicioBe(rs.getString("id_servicio_be"));
//					cons.setIdContable(rs.getString("id_contable"));
//					cons.setEquivale(rs.getString("equivale"));
//					cons.setNoCheque(rs.getInt("no_cheque"));
//					cons.setUsr1(rs.getInt("usuario_uno"));
//					cons.setUsr2(rs.getInt("usuario_dos"));
//					cons.setFecOperacionStr(funciones.ponerFechaSola(rs.getDate("fec_operacion")));
//					cons.setFecContabilizacionStr((rs.getDate("fecha_contabilizacion")!= null)?funciones.ponerFechaSola(rs.getDate("fecha_contabilizacion" )):"");
//					cons.setColor(rs.getString("color"));
//					cons.setInvoiceType(rs.getString("invoice_type"));
//					cons.setIdClabe(rs.getString("id_clabe"));
//					cons.setRfc(rs.getString("rfc"));
//					cons.setReferenciaCte(rs.getString("referencia_cte"));
				map.put("conceptoSeleccionA", rs.getString("concepto2"));
				map.put("razonSocial", rs.getString("razon_social"));
				map.put("importeTotal", importeTotal);
					return map;
				}});
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:consultarDetalle");
			 
		}
		
		return listResult;
	}

	@Override
	public Map<String, String> actualizaCVT(String folios) {

		Map<String, String> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		StringBuffer sql = new StringBuffer();
		int count = 0;
		try{

    		sql.append("\n UPDATE MOVIMIENTO ");
	    	sql.append("\n SET id_servicio_be = 'CVT' ");
	    	sql.append("\n WHERE ");
	    	sql.append("\n NO_FOLIO_DET IN("+folios+") ");
	    	System.out.println(sql.toString());
		    count = jdbcTemplate.update(sql.toString());
		  
		    if(count > 0){
		    	resultado.put("mensaje", "Datos modificados correctamente.");
		    }else{
		    	resultado.put("error", "Error al actualizar los datos.");
		    }

		 }catch(Exception e){
			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:actualizaCVT");
		 }
		
		return resultado;

	}
	

}
