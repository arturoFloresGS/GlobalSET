package com.webset.set.inversiones.dao.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.webset.set.egresos.dto.ConfirmacionCargoCtaDto;
import com.webset.set.inversiones.dao.InversionesDao;
import com.webset.set.inversiones.dto.BancoCheContratoDto;
import com.webset.set.inversiones.dto.ComunInversionesDto;
import com.webset.set.inversiones.dto.ConsultaOrdenInversionDto;
import com.webset.set.inversiones.dto.CtasContratoDto;
import com.webset.set.inversiones.dto.CuentaPropiaDto;
import com.webset.set.inversiones.dto.MovimientoDto;
import com.webset.set.inversiones.dto.OrdenInversionDto;
import com.webset.set.inversiones.dto.ParamReportesDto;
import com.webset.set.inversiones.dto.ParametroDto;
import com.webset.set.inversiones.dto.RetencionDto;
import com.webset.set.inversiones.dto.RubroDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.CatCtaBancoDto;
import com.webset.set.utilerias.dto.CatPapelDto;
import com.webset.set.utilerias.dto.CatTipoValorDto;
import com.webset.set.utilerias.dto.CuentaDto;
import com.webset.set.utilerias.dto.FuncionesSql;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.RevividorDto;

import mx.com.gruposalinas.Poliza.DT_Polizas_OBPolizas;
import mx.com.gruposalinas.Poliza.DT_Polizas_ResponseResponse;

/**
 * 
 * @author Cristian Garcia Garcia
 * @since 29/Jun/2011
 *
 */
@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
public class InversionesDaoImpl implements InversionesDao{
	private JdbcTemplate jdbcTemplate;
	private Bitacora bitacora = new Bitacora();
	private Funciones funciones = new Funciones();
	ConsultasGenerales consultasGenerales;
	private static Logger logger = Logger.getLogger(InversionesDaoImpl.class);
	
	
	/**
	* Selecciona el numero de folio de la tabla folio
	* @param tipoFolio
	* @return int
	*/
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int seleccionarFolioReal(String tipoFolio) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT num_folio ");
		sbSql.append("\n FROM folio ");
		sbSql.append("\n WHERE tipo_folio = '" + tipoFolio + "'");
		try {
			return jdbcTemplate.queryForInt(sbSql.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
					"P:Inversiones, C:InversionesDaoImpl, M:seleccionarFolioReal");
			return -1;
		}
	}

	/**
	 * Actualiza el numero de folio de la tabla folio
	 * @param tipoFolio
	 * @return int
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int actualizarFolioReal(String tipoFolio) {
		StringBuffer sbSQL = new StringBuffer();
		sbSQL.append(" UPDATE folio ");
		sbSQL.append("\n SET num_folio = num_folio + 1 ");
		sbSQL.append("\n WHERE tipo_folio = '" + tipoFolio + "'");
		try {
			return jdbcTemplate.update(sbSQL.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "
					+ Bitacora.getStackTrace(e) + "P:Inversiones, C:InversionesDaoImpl, M:actualizarFolioReal");
			return -1;
		}
	}
	
	public int obtenerFolioReal(String sTipo){
		int iFolio = 0;
		
		try {
			actualizarFolioReal(sTipo);
			iFolio = seleccionarFolioReal(sTipo);
			return iFolio;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesBusinessImpl, M:obtenerFolioReal");
			return -1;
		}
	}
	
	/**
	 * Este metodo consulta los bancos asignados a una institucion financiera, en
	 * caso de que asi se requiera, es utilizado en mantenimieno de contratos
	 * FunSQLCombo160
	 * @param iNoEmpresa
	 * @param iInstitucion
	 * @param sIdDivisa se recibe la divisa para consultar solo las chequeras,
	 * con tipo de divisa del contrato.
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarBancosDep(int iNoEmpresa, int iInstitucion, String sIdDivisa){
		List<LlenaComboGralDto> listBanc = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("\n SELECT id_banco as ID, desc_banco as Descrip ");
		    sbSql.append("\n FROM cat_banco ");
		    sbSql.append("\n WHERE id_banco in ");
		    sbSql.append("\n     ( SELECT DISTINCT id_banco ");
		    sbSql.append("\n       FROM cat_cta_banco ");
		    sbSql.append("\n       WHERE no_empresa = " + iNoEmpresa);
		    sbSql.append("\n           and nac_ext = 'N'  ");
		    
		    if(sIdDivisa != null && !sIdDivisa.equals(""))
		    	sbSql.append("\n and id_divisa = '"+ sIdDivisa +"'");
		    
		    sbSql.append("\n           and tipo_chequera in ('I','M') ) ");
		    //sbSql.append("\n           and tipo_chequera in ('I','C','M') ) ");
		    
		    if(iInstitucion > 0) 
		    {
		    	sbSql.append("\n UNION ALL ");
		        sbSql.append("\n SELECT id_banco as ID, desc_banco as Descrip ");
		        sbSql.append("\n FROM cat_banco ");
		        sbSql.append("\n WHERE id_banco in ");
		        sbSql.append("\n     ( SELECT DISTINCT id_banco ");
		        sbSql.append("\n       FROM cat_cta_banco ");
		        sbSql.append("\n       WHERE no_empresa = ");
		        sbSql.append("\n               ( SELECT no_empresa ");
		        sbSql.append("\n                 FROM empresa ");
		        sbSql.append("\n                 WHERE institucion_financiera = " + iInstitucion + ") ");
		        sbSql.append("\n           and nac_ext = 'N'  ");
		        sbSql.append("\n           and tipo_chequera in ('I','O','M') ) ");
		        sbSql.append("\n     and id_banco not in ");
		        sbSql.append("\n         ( SELECT DISTINCT id_banco ");
		        sbSql.append("\n           FROM cat_cta_banco ");
		        sbSql.append("\n           WHERE no_empresa = " + iNoEmpresa);
		        sbSql.append("\n               and nac_ext = 'N'  ");
		        
		        if(sIdDivisa != null && !sIdDivisa.equals(""))
			    	sbSql.append("\n 		   and id_divisa = '"+ sIdDivisa +"'");
			    
			    sbSql.append("\n          	   and tipo_chequera in ('I','M') ) ");
		        //sbSql.append("\n               and tipo_chequera in ('I','C','M') ) ");
		    }
		    
		    listBanc = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		LlenaComboGralDto dtoBanc = new LlenaComboGralDto();
			    		dtoBanc.setId(rs.getInt("ID"));
			    		dtoBanc.setDescripcion(rs.getString("Descrip"));
			    	return dtoBanc;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarBancosDep");
		}
		return listBanc;
	}
	/**
	 * Metodo que consulta las instituciones dadas de alta en personas
	 * Public Function FunSQLCombo161() As ADODB.Recordset
	 * @return
	 */
	
	@Override
	public List<ConfirmacionCargoCtaDto> consultarInstitucion () {
		
		String sql= "";
		List<ConfirmacionCargoCtaDto> list = null;
		
		try{ 
			
			sql +="\n SELECT CCC.NO_PROV_INST AS ID, CCC.NOMBRE AS DESCRIP"				;
			sql +="\n FROM CAT_INST_FIN CCC"								 				;
			sql +="\n 							INNER JOIN PERSONA P"			 				;
			// MS: debido a que utiliza el dto de ConfirmacionCargoCtaDto y ahi esta definida el id de la institucion como 
			// numerico no se puede usar el equivale persona, este modulo todavia no se implementa en radio asi que
			// lo cambiamos al no_persona para solo afectar este modulo donde pidan que se presente el equivale se sacara 
			// solo para presentacion en los js
			//sql +="\n 							ON CCC.NO_PROV_INST = P.EQUIVALE_PERSONA"	;
			sql +="\n 							ON CCC.NO_PROV_INST = P.NO_PERSONA"	;
			sql +="\n 							AND P.ID_TIPO_PERSONA = 'P'"					;
			
			list = jdbcTemplate.query(sql, new RowMapper<ConfirmacionCargoCtaDto>(){
				public ConfirmacionCargoCtaDto mapRow(ResultSet rs, int idx) throws SQLException {
					ConfirmacionCargoCtaDto cons = new ConfirmacionCargoCtaDto();
					cons.setIdCasaCambio(rs.getInt("ID"));   
					cons.setDescCasaCambio(rs.getString("Descrip"));
					return cons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaDaoImpl, M:obtenerCasaCambioVta");
		}
		return list;
	}
	
	/**
	 * Funcion que consulta contratos de la tabla cuenta y que es
	 * utilizada el el formulario Mantenimiento de Contratos
	 * Function FunSQLSelect287
	 * @param iNoEmpresa
	 * @return lista de tipo CuentaDto
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<CuentaDto> consultarContratos(int iNoEmpresa){
		StringBuffer sbSql = new StringBuffer();
		List<CuentaDto> listConsContratos = new ArrayList<CuentaDto>();
		try{
			sbSql.append("Select ");
			sbSql.append("\n	A.valor_salida, ");    
	        sbSql.append("\n	A.id_divisa, ");
	        sbSql.append("\n	A.no_cuenta, ");
	        sbSql.append("\n	A.plazo_inv, ");
	        sbSql.append("\n	A.desc_cuenta, ");
	        sbSql.append("\n	A.no_institucion, ");
	        sbSql.append("\n 	A.no_contacto1 , ");
	        sbSql.append("\n 	A.no_contacto2, ");
	        sbSql.append("\n 	A.no_contacto3, ");
	        sbSql.append("\n 	coalesce(A.soc_gl, '') as soc_gl, ");
	        sbSql.append("\n 	coalesce(A.sub_cuenta, '') as sub_cuenta, ");
	        sbSql.append("\n 	coalesce(A.contrato_institucion,'ND') As contrato_institucion, ");
	        sbSql.append("\n	B.desc_estatus, coalesce(A.b_isr_bisiesto, 'N') As b_isr_bisiesto ");
	        sbSql.append("\n	,A.persona_autoriza, coalesce(A.condicion_alt, '') as condicion_alt ");
	        sbSql.append("\n	, coalesce(A.aplica_isr, 'S') as apilca_isr, ");
	        sbSql.append("\n	coalesce(A.isr_igual_int, 'N') as isr_igual_int ");
	        sbSql.append("\n	From cuenta A, cat_estatus B ");
	        sbSql.append("\n	Where ");
	        sbSql.append("\n 	A.no_empresa = " + iNoEmpresa);
	        
	        if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
	        	sbSql.append("\n	 And B.id_estatus = A.id_estatus_cta ");
	        else if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
	        	sbSql.append("\n 	And CAST(B.id_estatus as varchar) = A.id_estatus_cta ");
	        else if(ConstantesSet.gsDBM.equals("DB2"))
	        	sbSql.append("\n 	And CAST(B.id_estatus as varchar(3)) = A.id_estatus_cta ");
	        	
	        sbSql.append("\n 	And B.clasificacion = 'CTA'");
	      
	        //System.out.println( sbSql.toString());
	        listConsContratos = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
	        	public CuentaDto mapRow(ResultSet rs, int idx) throws SQLException	{
	        		CuentaDto dtoCons = new CuentaDto();
	        		
	        			dtoCons.setValorSalida(rs.getDouble("valor_salida"));
	        			dtoCons.setIdDivisa(rs.getString("id_divisa"));
	        			dtoCons.setNoCuenta(rs.getInt("no_cuenta"));
	        			dtoCons.setPlazoInv(rs.getInt("plazo_inv"));
	        			dtoCons.setDescCuenta(rs.getString("desc_cuenta"));
	        			dtoCons.setNoInstitucion(rs.getInt("no_institucion"));
	        			dtoCons.setNoContacto1(rs.getInt("no_contacto1"));
	        			dtoCons.setNoContacto2(rs.getInt("no_contacto2"));
	        			dtoCons.setNoContacto3(rs.getLong("no_contacto3"));
	        			dtoCons.setContratoInstitucion(rs.getString("contrato_institucion"));
	        			dtoCons.setDescEstatus(rs.getString("desc_estatus"));
	        			dtoCons.setBIsrBisiesto(rs.getString("b_isr_bisiesto"));
	        			dtoCons.setPersonaAutoriza(rs.getString("persona_autoriza"));
	        			dtoCons.setCondicionAlt(rs.getString("condicion_alt"));
	        			dtoCons.setAplicaISR(rs.getString("apilca_isr"));
	        			dtoCons.setIsrIgualInt(rs.getString("isr_igual_int"));
	        			dtoCons.setSocGl(rs.getString("soc_gl"));
	        			dtoCons.setSubCuenta(rs.getString("sub_cuenta"));
	        			
	        		return dtoCons;
	        	}
	        });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarContratos");
		}
		return listConsContratos;
	}
	
	/**
	 * Este metodo obtiene el nombre de los contactos de la tabla persona
	 * FunSQLSelect292
	 * @param sContactos
	 * @return Retorna el nombre de la persona
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public String consultarContactos(int iContacto){
		String sNombre = "";
		StringBuffer sbSql = new StringBuffer();
		List<String> listNombre = new ArrayList<String>();
		try{
		    if(ConstantesSet.gsDBM.equals("POSTGRESQL") || ConstantesSet.gsDBM.equals("DB2"))
		    	sbSql.append("Select nombre || ' ' || paterno || ' ' || materno as nombre ");
		    else
		    	sbSql.append("Select nombre + ' ' + paterno + ' ' + materno as nombre ");
    			
		    sbSql.append("\n	FROM persona where no_persona = " + iContacto);
		    //MS se agrega el tipo de persona, se llamo desde el contrato
		    sbSql.append("\n	AND id_tipo_persona = 'F' " );
		    
		    listNombre = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public String mapRow(ResultSet rs, int idx)throws SQLException{
		    		return rs.getString("nombre");
		    	}
		    });
		    sNombre = listNombre.size() > 0 ? listNombre.get(0) : "";
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarContactos");
			e.printStackTrace();
			
		}
		return sNombre;
	}
	
	/**
	 * FunSQLSelect290
	 * @param iIdEmpresa
	 * @return
	 */
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int consultarLineaEmpresa(int iIdEmpresa){
		StringBuffer sbSql = new StringBuffer();
		int iLinea = 0;
		try{
		   sbSql.append("Select no_linea_emp ");
		   sbSql.append("\n	From empresa ");
		   sbSql.append("\n	Where ");
		   sbSql.append("\n	no_empresa =  " + iIdEmpresa);
		   
		   iLinea = jdbcTemplate.queryForObject(sbSql.toString(), Integer.class);
		
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarLineaEmpresa");
			e.printStackTrace();
		}
		return iLinea;
	}
	
	/**
	 * FunSQLSelect293
	 * Este metodo consulta los bancos asignados
	 * a los contratos de inversion
	 * @param iNoEmpresa
	 * @param iNoLinea
	 * @param iNoCuenta
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<BancoCheContratoDto> consultarBancosContrato(int iNoEmpresa, int iNoLinea, int iNoCuenta){
		List<BancoCheContratoDto> listCons = new ArrayList<BancoCheContratoDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
		    sbSql.append(" Select a.id_banco, b.desc_banco, a.id_chequera");
		    sbSql.append("\n	from ctas_contrato  a,cat_banco  b");
    		sbSql.append("\n	where a.id_banco=b.id_banco");
			sbSql.append("\n	and a.no_empresa= " + iNoEmpresa);
			sbSql.append("\n	and no_linea= " + iNoLinea);
			sbSql.append("\n	and no_cuenta=" + iNoCuenta);
			
			listCons = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
				public BancoCheContratoDto mapRow(ResultSet rs, int idx) throws SQLException{
					BancoCheContratoDto dtoCons = new BancoCheContratoDto();
						dtoCons.setIdBanco(rs.getInt("id_banco"));
						dtoCons.setDescBanco(rs.getString("desc_banco"));
						dtoCons.setIdChequera(rs.getString("id_chequera"));
					return dtoCons;
				}
			});

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarBancosContrato");
		}
		return listCons;
	}
	
	/**
	 * Este metodo consulta en la tabla persona,
	 * con respecto a la institucion asociada
	 * FunSQLCombo158
	 * @param iInstitucion
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<LlenaComboGralDto> consultarContactosInstitucion(int iInstitucion){
		List<LlenaComboGralDto> listContac = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSql = new StringBuffer();
 		try{
 			if(ConstantesSet.gsDBM.equals("POSTGRESQL") || ConstantesSet.gsDBM.equals("DB2") || ConstantesSet.gsDBM.equals("ORACLE"))
		    	sbSql.append("Select r.no_persona_rel as ID, p.nombre || ' ' || p.paterno || ' ' || p.materno as Descrip From relacion r,persona p ");
		    else
		    	sbSql.append("Select r.no_persona_rel as ID, p.nombre + ' ' + p.paterno + ' ' + p.materno as Descrip From relacion r,persona p ");
 			
 			sbSql.append("\n	Where  exists(select no_persona_rel from relacion)");
 			sbSql.append("\n	and p.id_tipo_persona = 'F' and r.no_persona = " + iInstitucion + " ");
 			sbSql.append("\n	and p.no_persona = r.no_persona_rel and id_tipo_relacion = 8 ");
 			//System.out.println(sbSql);
 			listContac = jdbcTemplate.query(sbSql.toString(), new RowMapper(){ 
 				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException{
 					LlenaComboGralDto dtoCon = new LlenaComboGralDto();
	 						dtoCon.setId(rs.getInt("ID"));
	 						dtoCon.setDescripcion(rs.getString("Descrip"));
 						return dtoCon;
 				}
 			});
 		        
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarContactosInstitucion");
		}
		return listContac;
	}
	
	/**
	 * Este metodo obtiene las chequeras del banco y si se requiere tambien que tenga la institucion
	 * es utilizado en MantenimientoDeContratos, LiquidacionDeInversiones
	 * FunSQLCombo253
	 * @param iIdBanco : id banco del que se requieren sus chequeras
	 * @param iIdEmp : id de la empresa donde esta el usuario
	 * @param iIns : id de la institucion financiera
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<LlenaComboChequeraDto> consultarChequeras(int iIdBanco, int iIdEmp, int iIns, String sIdDivisa){
		List<LlenaComboChequeraDto> listCheq = new ArrayList<LlenaComboChequeraDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("SELECT id_chequera as ID, id_chequera as Descrip, id_banco, tipo_chequera ");
			sbSql.append("\n FROM cat_cta_banco ");
			sbSql.append("\n WHERE id_banco = " + iIdBanco);
			sbSql.append("\n     and no_empresa = " + iIdEmp);
			//sbSql.append("\n     and tipo_chequera in ('I' ,'O','M','V') ");
			sbSql.append("\n     and tipo_chequera in ('I','M','C')");
			
			if(!sIdDivisa.equals(""))
				sbSql.append("\n and  id_divisa = '" + sIdDivisa + "'");
			
			if(iIns > 0)
			{
				sbSql.append("\n UNION ALL ");
				sbSql.append("\n SELECT id_chequera as ID, id_chequera as Descrip, id_banco, tipo_chequera ");
				sbSql.append("\n FROM cat_cta_banco ");
				sbSql.append("\n WHERE id_banco = " + iIdBanco);
				sbSql.append("\n     and no_empresa = ");
				sbSql.append("\n         ( SELECT no_empresa ");
				sbSql.append("\n           FROM empresa ");
				sbSql.append("\n           WHERE institucion_financiera = " + iIns + ") ");
				//sbSql.append("\n     and tipo_chequera in ('I' ,'O','M','V') ");
				sbSql.append("\n     and tipo_chequera in ('I','M','C')");
			}
			
			listCheq = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
				public LlenaComboChequeraDto mapRow(ResultSet rs, int idx) throws SQLException{
					LlenaComboChequeraDto dtoChe = new LlenaComboChequeraDto();
						dtoChe.setId(rs.getString("ID"));
						dtoChe.setDescripcion(rs.getString("Descrip"));
						dtoChe.setIdBanco(rs.getInt("id_banco"));
						dtoChe.setTipoChequera(rs.getString("tipo_chequera"));
					return dtoChe;
				}
			});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarChequeras");
		}
		return listCheq;
	}
	
	/**
	 * Este metodo inserta en la tabla cuenta
	 * FunSQLInsert107
	 * @param dtoIns
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int insertarContratos(CuentaDto dtoIns){
		StringBuffer sbSql = new StringBuffer();
		int iAfec = 0;
		try{
		        sbSql.append("Insert Into cuenta	( valor_salida, no_empresa,	no_producto,	id_divisa,	no_persona, ");
		        sbSql.append("\n	usuario_alta,	no_linea,	no_cuenta,	id_estatus_cta, ");
		        sbSql.append("\n	plazo_inv,	desc_cuenta,	id_tipo_cuenta,	no_institucion, ");
		        sbSql.append("\n	no_contacto1,	no_contacto2,	no_contacto3, soc_gl, sub_cuenta,	contrato_institucion, ");
		        sbSql.append("\n	aplica_isr,	isr_igual_int ");
		        
		        if(dtoIns.getBIsrBisiesto() != null && !dtoIns.getBIsrBisiesto().equals(""))
		        	sbSql.append("\n	,b_isr_bisiesto	");
		        if(dtoIns.getPersonaAutoriza() != null && !dtoIns.getPersonaAutoriza().equals(""))		
		        	sbSql.append("	,persona_autoriza");
		        if(dtoIns.getCondicionAlt() != null && !dtoIns.getCondicionAlt().equals(""))		
		        	sbSql.append("	,condicion_alt");
		        sbSql.append("	)");
		        sbSql.append("\n	Values (" + dtoIns.getValorSalida() + ", " + dtoIns.getNoEmpresa() + ", " + dtoIns.getNoProducto());
		        sbSql.append(", '" + dtoIns.getIdDivisa() +"', " + dtoIns.getNoEmpresa());
		        sbSql.append("\n, " + dtoIns.getUsuarioAlta() + ", " + dtoIns.getNoLinea());
		        sbSql.append(", " + dtoIns.getNoCuenta() + ", '" + dtoIns.getIdEstatusCta() + "'");
		        sbSql.append("\n, " + dtoIns.getPlazoInv() + ", '" + dtoIns.getDescCuenta() + "'");
		        sbSql.append(", '" + dtoIns.getIdTipoCuenta() + "', " + dtoIns.getNoInstitucion());
		        sbSql.append("\n," + dtoIns.getNoContacto1() + ",	" +dtoIns.getNoContacto2());
		        sbSql.append(", " + dtoIns.getNoContacto3() + ", '" + dtoIns.getSocGl() + "'");
		        sbSql.append(", '" + dtoIns.getSubCuenta() + "', '" + dtoIns.getContratoInstitucion() + "'");
		        sbSql.append(", '" + dtoIns.getAplicaISR() + "', '" + dtoIns.getIsrIgualInt() + "'");
		        
		        if(dtoIns.getBIsrBisiesto() != null && !dtoIns.getBIsrBisiesto().equals(""))
		        	sbSql.append("\n,'" + dtoIns.getBIsrBisiesto() + "'");
		        if(dtoIns.getPersonaAutoriza() != null && !dtoIns.getPersonaAutoriza().equals(""))		
		        	sbSql.append("	, '" + dtoIns.getPersonaAutoriza() + "'");
		        if(dtoIns.getCondicionAlt() != null && !dtoIns.getCondicionAlt().equals(""))		
		        	sbSql.append("	, '" + dtoIns.getCondicionAlt() + "'");
		        sbSql.append("	)");
		        
		        iAfec = jdbcTemplate.update(sbSql.toString());

		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:insertarContratos");
		}
		return iAfec;
	}
	
	/**
	 * Este metodo inserta en ctas_contrato, es utilizada en
	 * mantenimiento de contratos
	 * FunSQLInsert108
	 * @param dtoIns
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int insertarCuentas(CtasContratoDto dtoIns){
		int iAfec = 0;
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("Insert Into ctas_contrato ( no_empresa, no_linea, no_cuenta, id_banco, id_chequera ) ");
            sbSql.append("\n	Values (" + dtoIns.getNoEmpresa() + ", " + dtoIns.getNoLinea());
            sbSql.append("\n	," + dtoIns.getNoCuenta() + ", " + dtoIns.getIdBanco());
            sbSql.append("\n	,'" + dtoIns.getIdChequera() + "' )");

            iAfec = jdbcTemplate.update(sbSql.toString());	            
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:insertarCuentas");
		}
		return iAfec;
	}
	
	/**
	 * FunSQLSelect291
	 * @param iNoCuenta
	 * @param iNoEmpresa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<OrdenInversionDto> consultarOrdenInversion(int iNoCuenta, int iNoEmpresa){
		List<OrdenInversionDto> listOrd = new ArrayList<OrdenInversionDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("Select * from orden_inversion ");
			sbSql.append("\n	where id_estatus_ord not in('X','V') and ");
			sbSql.append("\n	no_cuenta = " + iNoCuenta + " And no_empresa = " + iNoEmpresa);
			
			listOrd = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
				public OrdenInversionDto mapRow(ResultSet rs, int idx) throws SQLException{
					OrdenInversionDto dtoCons = new OrdenInversionDto();
						dtoCons.setNoEmpresa(rs.getInt("no_empresa"));
						dtoCons.setNoOrden(rs.getString("no_orden"));
						dtoCons.setIdPapel(rs.getString("id_papel"));
						dtoCons.setIdTipoValor(rs.getString("id_tipo_valor"));
						dtoCons.setNoCuenta(rs.getInt("no_cuenta"));
						dtoCons.setNoInstitucion(rs.getInt("no_institucion"));
						dtoCons.setNoContacto(rs.getInt("no_contacto"));
						dtoCons.setFecVenc(rs.getDate("fec_venc"));
						dtoCons.setHora(rs.getInt("hora"));
						dtoCons.setMinuto(rs.getInt("minuto"));
						dtoCons.setImporte(rs.getDouble("importe"));
						dtoCons.setPlazo(rs.getInt("plazo"));
					    dtoCons.setIsr(rs.getDouble("isr"));
					    dtoCons.setUsuarioAlta(rs.getInt("usuario_alta"));
						dtoCons.setFecAlta(rs.getDate("fec_alta"));
						dtoCons.setUsuarioModif(rs.getInt("usuario_modif"));
						dtoCons.setIdEstatusOrd(rs.getString("id_estatus_ord"));
						dtoCons.setInteres(rs.getDouble("interes"));
						dtoCons.setDiasAnual(rs.getInt("dias_anual"));
						dtoCons.setNoContactoV(rs.getInt("no_contacto_v"));
						dtoCons.setHoraV(rs.getInt("hora_v"));
						dtoCons.setMinutoV(rs.getInt("minuto_v"));
						dtoCons.setAjusteInt(rs.getDouble("ajuste_int"));
						dtoCons.setNota(rs.getString("nota"));
						dtoCons.setAjusteIsr(rs.getDouble("ajuste_isr"));
						dtoCons.setTasaIsr(rs.getDouble("tasa_isr"));
						dtoCons.setTasa(rs.getDouble("tasa"));
						dtoCons.setImporteTraspaso(rs.getDouble("importe_traspaso"));
						dtoCons.setTraspasoEjecutado(rs.getDouble("traspaso_ejecutado"));
						dtoCons.setIdBanco(rs.getInt("id_banco"));
						dtoCons.setIdChequera(rs.getString("id_chequera"));
						dtoCons.setTasaCurva28(rs.getDouble("tasa_curva28"));
						dtoCons.setIdBancoReg(rs.getInt("id_banco_reg"));
						dtoCons.setIdChequeraReg(rs.getString("id_chequera_reg"));
						dtoCons.setBGarantia(rs.getString("b_garantia"));
						dtoCons.setNoOrdenRef(rs.getInt("no_orden_ref"));
						dtoCons.setBAutoriza(rs.getString("b_autoriza"));
						dtoCons.setNoAvisoRef(rs.getInt("no_aviso_ref"));
						dtoCons.setGrupoInversion(rs.getInt("grupo_inversion"));
						dtoCons.setTipoOrden(rs.getString("tipo_orden"));
					return dtoCons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarOrdenInversion");
		}
		return listOrd;
	}
	
	/**
	 * Este metodo elimina contratos de la tabla cuentas, es
	 * utilizada en mantenimiento de contratos
	 * FunSQLDelete46
	 * @param iNoCuenta
	 * @param iNoEmpresa
	 * @return 
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int eliminarContratos(int iNoCuenta, int iNoEmpresa){
		StringBuffer sbSql = new StringBuffer();
		int iAfec = 0;
		try{
			sbSql.append("Delete  from cuenta where  no_cuenta =" + iNoCuenta + " and no_empresa = " + iNoEmpresa);
			iAfec = jdbcTemplate.update(sbSql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:eliminarContratos");
		}
		return iAfec;
	}
	
	/**
	 * FunSQLDelete47
	 * @param iNoCuenta
	 * @param iNoEmpresa
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int eliminarCuentasContrato(int iNoCuenta, int iNoEmpresa){
		StringBuffer sbSql = new StringBuffer();
		int iAfec = 0;
		try{
			sbSql.append("Delete from ctas_contrato where  no_cuenta =" + iNoCuenta + " and no_empresa = " + iNoEmpresa);
			iAfec = jdbcTemplate.update(sbSql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:eliminarCuentasContrato");
		}
		return iAfec;
	}
	
	/**
	 * Este metodo para eliminar de ctas_contrato
	 * para cuando el usuario modifique sus cuentas en mantenimiento de contratos.
	 * @param dtoCtas
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int eliminarCuentasContrato(CtasContratoDto dtoCtas){
		StringBuffer sbSql = new StringBuffer();
		int iAfec = 0;
		try{
			sbSql.append("Delete from ctas_contrato where  no_cuenta =" + dtoCtas.getNoCuenta());
			sbSql.append("\n	and no_empresa = " + dtoCtas.getNoEmpresa());
			sbSql.append("\n	and no_linea = " + dtoCtas.getNoLinea());
			sbSql.append("\n	and id_banco = " + dtoCtas.getIdBanco());
			sbSql.append("\n    and id_chequera = '" + dtoCtas.getIdChequera() + "'");
		 
			iAfec = jdbcTemplate.update(sbSql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:eliminarCuentasContrato");
		}
		return iAfec;
	}
	
	
	/**
	 * FunSQLUpdate92
	 * Metodo utilizado para realizar modificaciones a la tabla cuenta
	 * utilizado en mantenimiento de contratos
	 * @param listCuenta
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int modificarContratos(CuentaDto dtoCuenta){
		StringBuffer sbSql = new StringBuffer();
		int iAfec = 0;
		try{

		sbSql.append("update cuenta set ");
        sbSql.append("\n	contrato_institucion  = ");
        sbSql.append("\n	'" + dtoCuenta.getContratoInstitucion() + "', ");
        sbSql.append("\n	id_divisa = '" + dtoCuenta.getIdDivisa() + "', ");
        sbSql.append("\n	usuario_modif = " + dtoCuenta.getUsuarioModif() + ", ");
        sbSql.append("\n	plazo_inv = " + dtoCuenta.getPlazoInv() + ", ");
        sbSql.append("\n 	desc_cuenta = '" + dtoCuenta.getDescCuenta() + "', ");
        sbSql.append("\n	no_institucion = " + dtoCuenta.getNoInstitucion() + ", ");
        sbSql.append("\n	no_contacto1 = " + dtoCuenta.getNoContacto1() + ", ");
        sbSql.append("\n	no_contacto2 = " + dtoCuenta.getNoContacto2() + ", ");
        sbSql.append("\n	no_contacto3 = " + dtoCuenta.getNoContacto3() + ", ");
      	if( ! dtoCuenta.getSocGl().equals("") ){
      		sbSql.append("\n	soc_gl	 = '" + dtoCuenta.getSocGl() + "', ");
      	}else{
      		sbSql.append("\n	soc_gl	 = null," );
      	}
        
      	if( ! dtoCuenta.getSubCuenta().equals("")){
      		sbSql.append("\n	sub_cuenta = '" + dtoCuenta.getSubCuenta() + "', ");
      	}else{
      		sbSql.append("\n	sub_cuenta = null,");
      	}
        
        
        sbSql.append("\n	valor_salida = " + dtoCuenta.getValorSalida() + " ");
      	sbSql.append("\n	,aplica_isr = '" + dtoCuenta.getAplicaISR() + "' ");
      	sbSql.append("\n	,isr_igual_int = '" + dtoCuenta.getIsrIgualInt() + "' ");
        if(dtoCuenta.getBIsrBisiesto() != null && !dtoCuenta.getBIsrBisiesto().equals(""))
        	sbSql.append("\n, 	b_isr_bisiesto = '" + dtoCuenta.getBIsrBisiesto()+ "' ");
        
        if(dtoCuenta.getPersonaAutoriza() != null && !dtoCuenta.getPersonaAutoriza().equals(""))
        sbSql.append("\n, 	persona_autoriza = '" + dtoCuenta.getPersonaAutoriza() + "' ");
        
        if(dtoCuenta.getCondicionAlt() != null && !dtoCuenta.getCondicionAlt().equals(""))
        	sbSql.append("\n, 	condicion_alt = '" + dtoCuenta.getCondicionAlt() + "' ");
        sbSql.append("\n where no_cuenta =" + dtoCuenta.getNoCuenta() + "  and no_empresa = " + dtoCuenta.getNoEmpresa());
        
        iAfec = jdbcTemplate.update(sbSql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:modificarContratos");
			return -1;
		}
		return iAfec;
	}
	
	/**
	 * FunSQLSelect650
	 * Este metodo consulta los contratos, es utilizado en OrdenDeInversion.js
	 * @param iNoEmpresa
	 * @param bInternas
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<CuentaDto> consultarNumeroContratos(int iNoEmpresa, boolean bInternas){
		StringBuffer sbSql = new StringBuffer();
		List<CuentaDto> listConsContratos = new ArrayList<CuentaDto>();
		try{
			//se comento  porque  no coincide el el no de institucion en empresa con cuenta y no arroja ningun relustado
		    /*sbSql.append("Select ");
		    sbSql.append("\n	A.desc_cuenta, ");
		    sbSql.append("\n	A.no_cuenta, ");
		    sbSql.append("\n	A.id_divisa, ");
		    sbSql.append("\n	A.no_institucion, ");
		    sbSql.append("\n	coalesce(A.no_contacto1,0)	as no_contacto1, ");
		    sbSql.append("\n	coalesce(A.no_contacto2,0)	as no_contacto2, ");
		    sbSql.append("\n	coalesce(A.no_contacto3,0)	as no_contacto3, ");
		    sbSql.append("\n	B.razon_social, ");
		    sbSql.append("\n	A.plazo_inv, ");
		    sbSql.append("\n	coalesce(A.contrato_institucion,'ND')As contrato_institucion  ");
		    sbSql.append("\n	From ");
		    sbSql.append("\n	cuenta A, persona B  ");
		    sbSql.append("\n	Where ");
		    sbSql.append("\n	A.no_empresa = " + iNoEmpresa);
		    sbSql.append("\n 	And A.id_tipo_cuenta = 'C' ");
		    sbSql.append("\n 	And B.no_empresa = 1 ");
		    sbSql.append("\n	And A.no_institucion = B.no_persona ");
		    if(bInternas)
		        sbSql.append("\n	And A.no_institucion in (select institucion_financiera from empresa where institucion_financiera is not null) ");
		    else
		        sbSql.append("\n	And A.no_institucion not in (select institucion_financiera from empresa where institucion_financiera is not null) ");
	
		    sbSql.append("\n	Order by 1");*/
			
			sbSql.append("Select ");
		    sbSql.append("\n	A.desc_cuenta, ");
		    sbSql.append("\n	A.no_cuenta, ");
		    sbSql.append("\n	A.id_divisa, ");
		    sbSql.append("\n	A.no_institucion, ");
		    sbSql.append("\n	coalesce(A.no_contacto1,0)	as no_contacto1, ");
		    sbSql.append("\n	coalesce(A.no_contacto2,0)	as no_contacto2, ");
		    sbSql.append("\n	coalesce(A.no_contacto3,0)	as no_contacto3, ");
		    sbSql.append("\n	B.razon_social, ");
		    sbSql.append("\n	A.plazo_inv, ");
		    sbSql.append("\n	coalesce(A.contrato_institucion,'ND')As contrato_institucion, A.valor_salida  ");
	        sbSql.append("\n	, coalesce(A.aplica_isr, 'S') as apilca_isr, ");
	        sbSql.append("\n	coalesce(A.isr_igual_int, 'N') as isr_igual_int ");
		    sbSql.append("\n	From ");
		    sbSql.append("\n	cuenta A, persona B  ");
		    sbSql.append("\n	Where ");
		    sbSql.append("\n	A.no_empresa = " + iNoEmpresa + " ");
		    sbSql.append("\n 	And A.id_tipo_cuenta = 'C' ");
		    if (ConstantesSet.gsDBM.equals("ORACLE"))
		    	sbSql.append("\n	And to_char(A.no_institucion) = B.equivale_persona");
		    else
		    	sbSql.append("\n	And A.no_institucion = B.no_persona");
		    sbSql.append("\n 	And B.id_tipo_persona = 'B' ");
		    
		    listConsContratos = jdbcTemplate.query(sbSql.toString(), new RowMapper<CuentaDto>(){
	        	public CuentaDto mapRow(ResultSet rs, int idx) throws SQLException	{
	        		CuentaDto dtoCons = new CuentaDto();
	        			dtoCons.setNoCuenta(rs.getInt("no_cuenta"));
	        			dtoCons.setContratoInstitucion(rs.getString("contrato_institucion"));
	        			dtoCons.setDescCuenta(rs.getString("desc_cuenta"));
	        			dtoCons.setDescCuenta(rs.getString("desc_cuenta"));
	        			dtoCons.setRazonSocial(rs.getString("razon_social"));
	        			dtoCons.setIdDivisa(rs.getString("id_divisa"));
//	        			dtoCons.setNoContacto1(rs.getInt("no_contacto1"));
//	        			dtoCons.setNoContacto2(rs.getInt("no_contacto2"));
//	        			dtoCons.setNoContacto3(rs.getInt("no_contacto3"));
	        			dtoCons.setNoInstitucion(rs.getInt("no_institucion"));
	        			dtoCons.setFormaPago(rs.getInt("valor_salida"));
	        			dtoCons.setPlazoInv(rs.getInt("plazo_inv"));
	        			dtoCons.setAplicaISR(rs.getString("apilca_isr"));
	        			dtoCons.setIsrIgualInt(rs.getString("isr_igual_int"));
	        			
	        		return dtoCons;
	        	}
	        });

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarNumeroContratos");
		}
		return listConsContratos;
	}
	
	/**
	 * FunSQLSelect652
	 * Este metodo consulta la tabla cat_tipo_valor, es 
	 * utilizada en OrdenDeInversion.js
	 * @param sIdDivisa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<CatTipoValorDto> consultarTipoValor(String sIdDivisa){
		List<CatTipoValorDto> listTipoVal = new ArrayList<CatTipoValorDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("Select ");
			sbSql.append("id_tipo_valor, ");
			sbSql.append("desc_tipo_valor, ");
			sbSql.append("b_isr ");
			sbSql.append("From cat_tipo_valor ");
			sbSql.append("Where ");
			sbSql.append("id_divisa =  '" + sIdDivisa + "' ");
			sbSql.append("Order by 1");
			
			listTipoVal = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
				public CatTipoValorDto mapRow(ResultSet rs, int idx) throws SQLException{
					CatTipoValorDto dtoCons = new CatTipoValorDto();
						dtoCons.setIdTipoValor(rs.getString("id_tipo_valor"));
						dtoCons.setDescTipoValor(rs.getString("desc_tipo_valor"));
						dtoCons.setBIsr(rs.getString("b_isr"));
					return dtoCons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarTipoValor");
		}
		return listTipoVal;
	}
	
	/**
	 * Este metodo obtiene el numero y nombre del contacto
	 * de la tabla persona, es utilizado en OrdenDeInversion.js
	 * @param iContacto1
	 * @param iContacto2
	 * @param iContacto3
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<LlenaComboGralDto> consultarIdNomContactos(int iContacto1, int iContacto2, int iContacto3){
		List<LlenaComboGralDto> listContac = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("Select no_persona, nombre");
			sbSql.append("\n	From persona	");
			ArrayList<String> cond = new ArrayList<String>();
			if(iContacto1 > 0)
				cond.add("\n	no_persona = " + iContacto1);
			if(iContacto2 > 0)
				cond.add("\n	no_persona = " + iContacto2);
			if(iContacto3 > 0)
				cond.add("\n	no_persona = " + iContacto2);
			if(cond.size() > 0)
			{
				sbSql.append("	WHERE " + StringUtils.join(cond, "OR"));
				sbSql.append("\n and	id_tipo_persona like 'F' ");
			}
			
			listContac = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						dtoCons.setId(rs.getInt("no_persona"));
						dtoCons.setDescripcion(rs.getString("nombre"));
					return dtoCons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarIdNomContactos");
		}
		return listContac;
	}
	
	/**
	 * FunSQLSelect653
	 * Este metodo consulta el id del papel con respecto al tipo de valor,
	 * es utilizado en OrdenDeInversion
	 * @param sIdTipoValor
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<CatPapelDto> consultarPapel(String sIdTipoValor){
		StringBuffer sbSql = new StringBuffer();
		List<CatPapelDto> listPapel = new ArrayList<CatPapelDto>();
		try{
			sbSql.append("Select ");
		    sbSql.append("\n	id_papel,	id_tipo_valor");
		    sbSql.append("\n	From cat_papel ");
		    sbSql.append("\n	Where id_tipo_valor = '" + sIdTipoValor + "'");
		    
		    listPapel = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public CatPapelDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		CatPapelDto dtoCons = new CatPapelDto();
		    			dtoCons.setIdPapel(rs.getString("id_papel"));
		    			dtoCons.setIdTipoValor(rs.getString("id_tipo_valor"));
		    		return dtoCons;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarPapel");
		}
		return listPapel;
	}
	
	/**
	 * Este metodo consulta la empresa concentradora, es utilizado
	 * en OrdenDeInversion
	 * FunSQLCombo411
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<LlenaComboGralDto> consultarEmpresaConcentradora(){
		List<LlenaComboGralDto> listCombo = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
		    sbSql.append("Select distinct e.no_empresa as id, nom_empresa as descrip");
		    sbSql.append("\n	from empresa e, ctas_contrato cc");
		    sbSql.append("\n	Where e.no_empresa = cc.no_empresa");
		    listCombo = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		LlenaComboGralDto dtoCons = new LlenaComboGralDto();
		    			dtoCons.setId(rs.getInt("id"));
		    			dtoCons.setDescripcion(rs.getString("descrip"));
		    		return dtoCons;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarEmpresaConcentradora");
		}
		return listCombo;
	}
	
	/**
	 * Este metodo consulta los banco de cat_banco y cat_cta_banco,
	 * es utilizado en OrdenDeInversion,(frmTraspcredito)
	 * FunSQLBancosMonitor
	 * @param iIdEmpresa
	 * @param sIdDivisa
	 * @param sBancos
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<LlenaComboGralDto> consultarBancosMonitor(int iIdEmpresa, String sIdDivisa, String sBancos){
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listBancos = new ArrayList<LlenaComboGralDto>();
		try{
		    sbSql.append("SELECT distinct cat_banco.id_banco as ID, ");
		    sbSql.append("\n    cat_banco.desc_banco as DESCRIP");
		    sbSql.append("\n	FROM cat_banco ,cat_cta_banco");
		    sbSql.append("\n 	WHERE cat_cta_banco.id_banco = cat_banco.id_banco ");
		    sbSql.append("\n    and cat_cta_banco.no_empresa = " + iIdEmpresa);

		    sbSql.append("\n    and cat_cta_banco.id_divisa = '" + sIdDivisa + "' ");
		    sbSql.append("\n    and cat_cta_banco.tipo_chequera in ('I', 'M') ");
		    
		    listBancos = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		LlenaComboGralDto listBan = new LlenaComboGralDto();
			    		listBan.setId(rs.getInt("ID"));
			    		listBan.setDescripcion(rs.getString("DESCRIP"));
		    		return listBan;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarBancosMonitor");
		}
		return listBancos;
	}
	
	/**
	 * Este metodo consulta las chequeras de cat_cta_banco donde el tipo
	 * de chequera es I o M, es utilizado en OrdenInversion, (frminversiones)
	 * FunSQLChequerasMonitor
	 * @param iIdEmpresa
	 * @param sIdDivisa
	 * @param iIdBanco
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<LlenaComboGralDto> consultarChequerasMonitor(int iIdEmpresa, String sIdDivisa, int iIdBanco){
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listChe = new ArrayList<LlenaComboGralDto>();
		try{
			sbSql.append("SELECT id_chequera as ID, id_chequera as DESCRIP, tipo_chequera ");
		    sbSql.append("\n FROM cat_cta_banco");
		    sbSql.append("\n WHERE id_banco = " + iIdBanco);
		    sbSql.append("\n     and no_empresa = " + iIdEmpresa);
		    sbSql.append("\n     and id_divisa = '" + sIdDivisa + "' ");
		    sbSql.append("\n     and tipo_chequera in ('I', 'M')");
		    sbSql.append("\n order by tipo_chequera asc");
		    
		    listChe = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		LlenaComboGralDto listBan = new LlenaComboGralDto();
			    		listBan.setDescripcion(rs.getString("DESCRIP"));
		    		return listBan;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarChequerasMonitor");
		}
		return listChe;
	}
	
	/**
	 * Metodo que consulta el saldo inicial de la tabla cat_cta_banco o
	 * hist_cat_cta_banco, es utilizado en Oreden de Inversion
	 * FunSQLSaldoInicialMonitor
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<CatCtaBancoDto> consultarSaldoInicialMonitor(ComunInversionesDto dto){
		StringBuffer sbSql = new StringBuffer();
		List<CatCtaBancoDto> listSaldo = new ArrayList<CatCtaBancoDto>();
		try{
		    sbSql.append("SELECT saldo_inicial ");
		    
		    if(dto.isAnterior())
		    {
		    	sbSql.append("\n	FROM hist_cat_cta_banco ");
		        sbSql.append("\n	WHERE fec_valor = '" + funciones.ponerFecha(dto.getFecValor())+ "' and");
		    }
		    else
		    {
		        sbSql.append("\n	FROM cat_cta_banco ");
		        sbSql.append("\n	WHERE");
		    }
		    
		    sbSql.append("\n	id_banco = " + dto.getIdBanco());
		    sbSql.append("\n	and id_chequera = '" + dto.getIdChequera()+ "' ");
		    
		    logger.info("saldoInnicial: " +sbSql.toString());
		    listSaldo = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public CatCtaBancoDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		CatCtaBancoDto dtoCons = new CatCtaBancoDto();
		    			dtoCons.setSaldoInicial(rs.getDouble("saldo_inicial"));
		    		return dtoCons;
		    	}
		    });
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarSaldoInicialMonitor");
		}
		return listSaldo;
	}
	
	/**
	 * Metodo que consulta a la tabla de movimiento, es utilizado 
	 * en OrdenDeInversion
	 * FunSQLBarridoMonitor
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<MovimientoDto> consultarBarridoMonitor(ComunInversionesDto dto){
		List<MovimientoDto> listBarrido = new ArrayList<MovimientoDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
		    sbSql.append("\n SELECT fec_valor as fec_alta, id_tipo_operacion, ");
		    //'''    sbSql.append("\n case when id_estatus_mov in ('X') then importe *-1 "
		    //'''    sbSql.append("\n     else importe end as importe, concepto"
		    sbSql.append("\n   importe, concepto");
		        
		    sbSql.append("\n FROM movimiento ");
		    
		    sbSql.append("\n WHERE id_tipo_movto = 'I'");
		    sbSql.append("\n and (((id_estatus_mov = 'A' and fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "') ");
		    //'''    sbSql.append("\n and ((id_estatus_mov = 'A' and fec_valor = '" & PonFechaDMA(ptFecha) & "') ");
		    sbSql.append("\n         and not (id_tipo_operacion between 3100 and 3199 and b_salvo_buen_cobro='N'))");
		    sbSql.append("\n or (id_estatus_mov='A' ");
		    sbSql.append("\n     and id_tipo_operacion between 3100 and 3199 ");
		    sbSql.append("\n     and b_salvo_buen_cobro='S' ");
		    //'''    sbSql.append("\n     and fec_modif = '" & PonFechaDMA(ptFecha) & "')"
		    sbSql.append("\n     and fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "')");
		    sbSql.append("\n )");
		    sbSql.append("\n and no_empresa = " + dto.getIdEmpresa());
		    sbSql.append("\n and id_banco=" + dto.getIdBanco());
		    sbSql.append("\n AND id_estatus_mov <> 'X'");
		    sbSql.append("\n and id_chequera='" + dto.getIdChequera() + "'");
		    sbSql.append("\n and id_tipo_operacion not in(1013,1014)");
		    sbSql.append("\n and id_tipo_operacion not between 4000 and 4199   ");
		    
		    sbSql.append("\n union all");
		    
		    sbSql.append("\n SELECT fec_valor as fec_alta, id_tipo_operacion, ");
		    //'''    sbSql.append("\n case when id_estatus_mov in ('X') then importe *-1 ");
		    //'''    sbSql.append("\n     else importe end as importe, concepto");
		    sbSql.append("\n importe, concepto");
		    
		    sbSql.append("\n FROM hist_movimiento ");
		    
		    sbSql.append("\n WHERE id_tipo_movto = 'I'");
		    sbSql.append("\n and (((id_estatus_mov = 'A' and fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "') ");
		    //'''    sbSql.append("\n and ((id_estatus_mov = 'A' and fec_valor = '" & PonFechaDMA(ptFecha) & "') ");
		    sbSql.append("\n         and not (id_tipo_operacion between 3100 and 3199 and b_salvo_buen_cobro='N'))");
		    sbSql.append("\n or (id_estatus_mov ='A' ");
		    sbSql.append("\n     and id_tipo_operacion between 3100 and 3199 ");
		    sbSql.append("\n     and b_salvo_buen_cobro='S' ");
		    //'''    sbSql.append("\n     and fec_modif = '" & PonFechaDMA(ptFecha) & "')");
		    sbSql.append("\n     and fec_valor = '" + funciones.ponerFecha(dto.getFecValor())+ "')");
		    sbSql.append("\n )");
		    sbSql.append("\n and no_empresa = " + dto.getIdEmpresa());
		    sbSql.append("\n and id_banco = " + dto.getIdBanco());
		    sbSql.append("\n AND id_estatus_mov <> 'X'");
		    sbSql.append("\n and id_chequera='" + dto.getIdChequera()+ "'");
		    sbSql.append("\n and id_tipo_operacion not in(1013,1014)");
		    sbSql.append("\n and id_tipo_operacion not between 4000 and 4199   ");
		    
		    listBarrido = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public MovimientoDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		MovimientoDto dtoBarr = new MovimientoDto();
		    			dtoBarr.setFecAlta(rs.getDate("fec_alta"));
		    			dtoBarr.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
		    			dtoBarr.setImporte(rs.getDouble("importe"));
		    			dtoBarr.setConcepto(rs.getString("concepto"));
		    		return dtoBarr;
		    	}
		    });
		     
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarBarridoMonitor");
		}
		return listBarrido;
	}
	
	/**
	 * Este metodo es utilizado en OrdenDeInversion
	 * FunSQLFondeoMonitor
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<MovimientoDto> consultarFondeoMonitor(ComunInversionesDto dto){
		List<MovimientoDto> listFondeo = new ArrayList<MovimientoDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
	        sbSql.append("SELECT importe, fec_valor as fec_alta, id_tipo_operacion, concepto");
	      
	        sbSql.append("\n FROM hist_movimiento ");
	        
	        sbSql.append("\n WHERE id_tipo_movto = 'E'");
	        sbSql.append("\n and ((id_estatus_mov in('P','R','I','T','K','J','G'))");
	        sbSql.append("\n or (id_estatus_mov='A' and id_tipo_operacion not between 3000 and 3099))");
	        sbSql.append("\n and no_empresa = " + dto.getIdEmpresa());
	        sbSql.append("\n and (fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "')");
  
         
	        sbSql.append("\n and id_estatus_mov not in ('X', 'Z')");
         
	        if(dto.getIdBanco() > 0)
            	sbSql.append("\n and id_banco = " + dto.getIdBanco());
         
	        if(dto.getIdChequera() != null && !dto.getIdChequera().equals(""))
            	sbSql.append("\n and id_chequera = '" + dto.getIdChequera() + "' ");
         
	        sbSql.append("\n and ((origen_mov <> 'MIG') or (origen_mov is null))  ");
	        sbSql.append("\n and id_tipo_operacion not between 4000 and 4199");
         
	        sbSql.append("\n union all");
         
	        sbSql.append("\n SELECT importe, fec_valor as fec_alta, id_tipo_operacion, concepto");
         
            sbSql.append("\n FROM movimiento ");
         
            sbSql.append("\n WHERE id_tipo_movto = 'E'");
            sbSql.append("\n and ((id_estatus_mov in('P','R','I','T','K','J','G'))");
            sbSql.append("\n or (id_estatus_mov='A' and id_tipo_operacion not between 3000 and 3099))");
            sbSql.append("\n and no_empresa = " + dto.getIdEmpresa());
            sbSql.append("\n and (fec_valor = '" + funciones.ponerFecha(dto.getFecValor())+ "')");
            sbSql.append("\n and id_estatus_mov not in ('X', 'Z')");
         
            if(dto.getIdBanco() > 0)
            	sbSql.append("\n and id_banco = " + dto.getIdBanco());
         
	        if(dto.getIdChequera() != null && !dto.getIdChequera().equals(""))
            	sbSql.append("\n and id_chequera = '" + dto.getIdChequera() + "' ");
         
            sbSql.append("\n and ((origen_mov <> 'MIG') or (origen_mov is null))  ");
            sbSql.append("\n and id_tipo_operacion not between 4000 and 4199");
            sbSql.append("\n order by concepto");
            
		    listFondeo = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public MovimientoDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		MovimientoDto dtoBarr = new MovimientoDto();
		    			dtoBarr.setFecAlta(rs.getDate("fec_alta"));
		    			dtoBarr.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
		    			dtoBarr.setImporte(rs.getDouble("importe"));
		    			dtoBarr.setConcepto(rs.getString("concepto"));
		    		return dtoBarr;
		    	}
		    });
		     
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarFondeoMonitor");
		}
		return listFondeo;
	}

	/**
	 * Este metodo se utiliza en OrdenInversion
	 * FunSQLVencimientosMonitor
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<OrdenInversionDto> consultarVencimientoMonitor(ComunInversionesDto dto){
		List<OrdenInversionDto> listVenc = new ArrayList<OrdenInversionDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("SELECT case when id_estatus_ord = 'V' then 'Vencidas' else 'Por Vencer' end as estatus, ");
		    sbSql.append("\n     o.importe As importe, ");
		    sbSql.append("\n     (o.interes) As interes, ");
		    sbSql.append("\n     o.isr As isr, ");
		    sbSql.append("\n     o.ajuste_isr As ajuste_isr ");
		    sbSql.append("\n FROM orden_inversion o");
		    sbSql.append("\n  WHERE o.fec_venc = '" + funciones.ponerFecha(dto.getFecValor())+ "'");
		    
		    sbSql.append("\n     and o.id_estatus_ord in ('V', 'A')");
		    sbSql.append("\n     and (o.id_banco_reg = " + dto.getIdBanco());
		    sbSql.append("\n         or (o.id_banco = " + dto.getIdBanco());
		    sbSql.append("\n         and o.id_banco_reg is NULL))");
		    sbSql.append("\n     and (o.id_chequera = '" + dto.getIdChequera() + "' ");
		    sbSql.append("\n         or o.id_chequera_reg = '" + dto.getIdChequera() + "') ");
		    sbSql.append("\n ORDER BY 1 desc, 2, 3 ");
			
			listVenc = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public OrdenInversionDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		OrdenInversionDto dtoVenc = new OrdenInversionDto();
		    			dtoVenc.setIdEstatusOrd(rs.getString("estatus"));
		    			dtoVenc.setImporte(rs.getDouble("importe"));
		    			dtoVenc.setInteres(rs.getDouble("interes"));
		    			dtoVenc.setIsr(rs.getDouble("isr"));
		    			dtoVenc.setAjusteIsr(rs.getDouble("ajuste_isr"));
		    		return dtoVenc;
		    	}
		    });
		     
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarVencimientoMonitor");
		}
		return listVenc;
	}
	
	/**
	 * Este metodo es utilizado en OrdenDeInversion
	 * FunSQLPorInvertirMonitor
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<CatCtaBancoDto> consultarPorInvertirMonitor(ComunInversionesDto dto){
		List<CatCtaBancoDto> listInv = new ArrayList<CatCtaBancoDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
	        if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
	            sbSql.append("\n SELECT DISTINCT '' as estatus, ");
	        else if(ConstantesSet.gsDBM.equals("POSTGRESQL") || ConstantesSet.gsDBM.equals("DB2"))
	            sbSql.append("\n SELECT distinct '' || '' as estatus, "); 
		    
		    sbSql.append("\n   coalesce(b.saldo_final, 0) as importe");
		    sbSql.append("\n FROM  ");
		    
		    if(dto.isAnterior())
		    {
		    	sbSql.append("\n hist_cat_cta_banco b");
		        sbSql.append("\n WHERE b.fec_valor = '" + dto.getFecValor() + "' and");
		    }
		    else{
		    	sbSql.append("\n cat_cta_banco b");
				sbSql.append("\n where");
		    }  
		    sbSql.append("\n   b.no_empresa = " + dto.getIdEmpresa());
		    sbSql.append("\n   and b.id_divisa = '" + dto.getIdDivisa()+ "' ");
		    sbSql.append("\n   and b.id_banco = " + dto.getIdBanco() + "");
		    sbSql.append("\n   and b.id_chequera = '" + dto.getIdChequera() + "'");
			
			listInv = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public CatCtaBancoDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		CatCtaBancoDto dtoVenc = new CatCtaBancoDto();
		    			dtoVenc.setIdEstatusCta(rs.getString("estatus"));
		    			dtoVenc.setSaldoFinal(rs.getDouble("importe"));
		    		return dtoVenc;
		    	}
		    });
		     
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarPorInvertirMonitor");
		}
		return listInv;
	}

	/**
	 * Este metodo consulta a la tabla de movimiento,
	 * es utillizado es en OrdenDeInversion
	 * FunSQLInversionesMonitor
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<MovimientoDto> consultarInversionesMonitor(ComunInversionesDto dto){
		List<MovimientoDto> listInv = new ArrayList<MovimientoDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("SELECT m.id_banco, m.id_chequera, m.id_estatus_mov as estatus,  ");
		    sbSql.append("\n case when m.id_estatus_mov in ('X') then m.importe *-1      else m.importe end as importe, ");
		    sbSql.append("\n m.id_tipo_operacion, concepto as nombre_corto ");
		    
		    sbSql.append("\n FROM movimiento m ");
		    
		    sbSql.append("\n WHERE no_folio_det in ( ");
		    sbSql.append("\n         select no_folio_det from movimiento m1 where ");
		    sbSql.append("\n         m1.id_tipo_movto = 'E' and m1.id_estatus_mov =  'A' and m1.id_tipo_operacion = 4001 ");
		    sbSql.append("\n         and m1.id_chequera='" + dto.getIdChequera()+ "' and m1.id_banco= " + dto.getIdBanco());
		    sbSql.append("\n         and m1.fec_valor = '" + dto.getFecValor() + "' ");
		    sbSql.append("\n         ) ");
		    
		    if(dto.getContrato() > 0)
		    {
		    	sbSql.append("\n     or no_folio_det in (");
		        sbSql.append("\n         select no_folio_det from movimiento m1 where ");
		        sbSql.append("\n         m1.id_tipo_movto = 'E' and m1.id_estatus_mov =  'O' ");
		        sbSql.append("\n         and m1.id_tipo_operacion = 4000 and m1.fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "' ");
		        sbSql.append("\n         AND m1.no_cuenta = " + dto.getContrato());
		        sbSql.append("\n         ) ");
		    }
		    
		    sbSql.append("\n union all");
		    
		    sbSql.append("\n SELECT m.id_banco, m.id_chequera, m.id_estatus_mov as estatus,  ");
		    sbSql.append("\n case when m.id_estatus_mov in ('X') then m.importe *-1      else m.importe end as importe, ");
		    sbSql.append("\n m.id_tipo_operacion, concepto as nombre_corto ");
		    
		    sbSql.append("\n FROM hist_movimiento m ");
		    
		    sbSql.append("\n WHERE no_folio_det in ( ");
		    sbSql.append("\n         select no_folio_det from hist_movimiento m1 where ");
		    sbSql.append("\n         m1.id_tipo_movto = 'E' and m1.id_estatus_mov =  'A' and m1.id_tipo_operacion = 4001 ");
		    sbSql.append("\n         and m1.id_chequera='" + dto.getIdChequera() + "' and m1.id_banco= " + dto.getIdBanco());
		    sbSql.append("\n         and m1.fec_valor = '" + funciones.ponerFecha(dto.getFecValor()) + "' ");
		    sbSql.append("\n         ) ");

			listInv = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public MovimientoDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		MovimientoDto dtoInv = new MovimientoDto();
		    			dtoInv.setIdBanco(rs.getInt("id_banco"));
		    			dtoInv.setIdChequera(rs.getString("id_chequera"));
		    			dtoInv.setIdEstatusMov(rs.getString("estatus"));
		    			dtoInv.setImporte(rs.getDouble("importe"));
		    			dtoInv.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
		    			dtoInv.setConcepto(rs.getString("nombre_corto"));
		    		return dtoInv;
		    	}
		    });
		     
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarInversionesMonitor");
		}
		return listInv;
	}
	
	/**
	 * 
	 * FunSQLTipoCambioDivisa
	 * @param sIdDivisa
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public double consultarTipoCambio(String sIdDivisa){
		StringBuffer sbSql = new StringBuffer();
		BigDecimal bdTipoCambio = new BigDecimal(2.1);
		double uValorCambio = 0;
		try{
			
		    sbSql.append("SELECT valor as TipoCambio ");
		    sbSql.append("\n FROM valor_divisa ");
		    sbSql.append("\n WHERE id_divisa = '" + sIdDivisa + "' ");
		    sbSql.append("\n     and fec_divisa = ");
		    sbSql.append("\n         ( SELECT fec_hoy ");
		    sbSql.append("\n           FROM fechas ) ");
System.out.println("consultarTipoCambio"+sbSql.toString());
		    bdTipoCambio = jdbcTemplate.queryForObject(sbSql.toString(), BigDecimal.class);
		    uValorCambio = bdTipoCambio.doubleValue();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarTipoCambio");
			return -1;
		}
		return uValorCambio;
	}
	
	/**
	 * Este metodo consulta en la tabla de orden_inversion con diferentes
	 * condiciones del metodo anterior de esta clase, es utilizado en OrdenDeInversion
	 * FunSQLSELECTOrden
	 * @param iNoCuenta
	 * @param iNoEmpresa
	 * @return
	 */
	
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<OrdenInversionDto> consultarOrdenInversionDos(int iNoAviso, int iNoEmpresa, String sTipoOrden, 
			int iGpoInv, Date dFecha){
		List<OrdenInversionDto> listOrd = new ArrayList<OrdenInversionDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
			
			sbSql.append("SELECT * FROM orden_inversion WHERE no_empresa = " + iNoEmpresa);
			sbSql.append("\n AND tipo_orden = '" + sTipoOrden + "'");
			sbSql.append("\n AND grupo_inversion = " + iGpoInv);
					
			if(iNoAviso > 0)
				sbSql.append("\n AND no_aviso_ref = " + iNoAviso);
			
			if(ConstantesSet.gsDBM.equals("DB2"))
				sbSql.append("\n AND fec_alta = '" + funciones.ponerFecha(dFecha) + "'");
			else
				sbSql.append("\n AND fec_alta = '" + funciones.ponerFecha(dFecha) + "'");
			
			listOrd = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
				public OrdenInversionDto mapRow(ResultSet rs, int idx) throws SQLException{
					OrdenInversionDto dtoCons = new OrdenInversionDto();
						dtoCons.setNoEmpresa(rs.getInt("no_empresa"));
						dtoCons.setNoOrden(rs.getString("no_orden"));
						dtoCons.setIdPapel(rs.getString("id_papel"));
						dtoCons.setIdTipoValor(rs.getString("id_tipo_valor"));
						dtoCons.setNoCuenta(rs.getInt("no_cuenta"));
						dtoCons.setNoInstitucion(rs.getInt("no_institucion"));
						dtoCons.setNoContacto(rs.getInt("no_contacto"));
						dtoCons.setFecVenc(rs.getDate("fec_venc"));
						dtoCons.setHora(rs.getInt("hora"));
						dtoCons.setMinuto(rs.getInt("minuto"));
						dtoCons.setImporte(rs.getDouble("importe"));
						dtoCons.setPlazo(rs.getInt("plazo"));
					    dtoCons.setIsr(rs.getDouble("isr"));
					    dtoCons.setUsuarioAlta(rs.getInt("usuario_alta"));
						dtoCons.setFecAlta(rs.getDate("fec_alta"));
						dtoCons.setUsuarioModif(rs.getInt("usuario_modif"));
						dtoCons.setIdEstatusOrd(rs.getString("id_estatus_ord"));
						dtoCons.setInteres(rs.getDouble("interes"));
						dtoCons.setDiasAnual(rs.getInt("dias_anual"));
						dtoCons.setNoContactoV(rs.getInt("no_contacto_v"));
						dtoCons.setHoraV(rs.getInt("hora_v"));
						dtoCons.setMinutoV(rs.getInt("minuto_v"));
						dtoCons.setAjusteInt(rs.getDouble("ajuste_int"));
						dtoCons.setNota(rs.getString("nota"));
						dtoCons.setAjusteIsr(rs.getDouble("ajuste_isr"));
						dtoCons.setTasaIsr(rs.getDouble("tasa_isr"));
						dtoCons.setTasa(rs.getDouble("tasa"));
						dtoCons.setImporteTraspaso(rs.getDouble("importe_traspaso"));
						dtoCons.setTraspasoEjecutado(rs.getDouble("traspaso_ejecutado"));
						dtoCons.setIdBanco(rs.getInt("id_banco"));
						dtoCons.setIdChequera(rs.getString("id_chequera"));
						dtoCons.setTasaCurva28(rs.getDouble("tasa_curva28"));
						dtoCons.setIdBancoReg(rs.getInt("id_banco_reg"));
						dtoCons.setIdChequeraReg(rs.getString("id_chequera_reg"));
						dtoCons.setBGarantia(rs.getString("b_garantia"));
						dtoCons.setNoOrdenRef(rs.getInt("no_orden_ref"));
						dtoCons.setBAutoriza(rs.getString("b_autoriza"));
						dtoCons.setNoAvisoRef(rs.getInt("no_aviso_ref"));
						dtoCons.setGrupoInversion(rs.getInt("grupo_inversion"));
						dtoCons.setTipoOrden(rs.getString("tipo_orden"));
					return dtoCons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarOrdenInversionDos");
		}
		return listOrd;
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int insertarOrdenInversion(OrdenInversionDto dto){
		StringBuffer sbSql = new StringBuffer();
		int iRegAfec = 0;
		try{
			sbSql.append("\n INSERT INTO orden_inversion ( ");
		    sbSql.append("\n     no_empresa, no_orden, id_papel, id_tipo_valor, no_cuenta, ");
		    sbSql.append("\n     no_institucion, no_contacto, no_contacto_v, fec_venc, ");
		    sbSql.append("\n     hora, minuto, importe, interes, plazo, tasa, tasa_curva28, ");
		    sbSql.append("\n     isr, usuario_alta, fec_alta, id_estatus_ord, dias_anual, ");
		    sbSql.append("\n     tasa_isr, traspaso_ejecutado, b_garantia, importe_traspaso, ");
		    sbSql.append("\n     b_autoriza, tipo_orden ");
		    
		    if(dto.getNoOrdenRef() > 0)
		        sbSql.append("\n ,no_orden_ref ");
		    
		    if(dto.getNoAvisoRef() > 0) 
		        sbSql.append("\n ,no_aviso_ref ");
		    
		    if(dto.getGrupoInversion() > 0)
		        sbSql.append("\n, grupo_inversion ");
		    
		    sbSql.append("\n ) ");
		    sbSql.append("\n VALUES ");
		    sbSql.append("\n ( ");
		    
		    if(ConstantesSet.gsDBM.equals("SYBASE"))
		    	sbSql.append("\n " + dto.getNoEmpresa() +", '" + dto.getNoOrden() + "' , '" + dto.getIdPapel() + "',");
		    else 
		    	sbSql.append("\n " + dto.getNoEmpresa() + ", " + dto.getNoOrden() + ", '" + dto.getIdPapel() + "',");
		        
		    sbSql.append("\n	'" + dto.getIdTipoValor() + "', " + dto.getNoCuenta() + ", " + dto.getNoInstitucion() + ", ");
		    sbSql.append("\n	" + dto.getNoContacto() + ", " + dto.getNoContactoV() + ", ");
		    
		    sbSql.append("\n	'" + funciones.ponerFecha(dto.getFecVenc()) + "'," + dto.getHora() + ",");

		    sbSql.append("\n	" + dto.getMinuto() + ", " + dto.getImporte() + ", " + dto.getInteres() + ", ");
		    sbSql.append("\n	" + dto.getPlazo() + ", " + dto.getTasa() + ", " + dto.getTasaCurva28() + ", ");
		    sbSql.append("\n	" + dto.getIsr()+ ", " + dto.getUsuarioAlta() + ", ");
		    
		    sbSql.append("\n	'" + funciones.ponerFecha(dto.getFecAlta()) + "',");
		            
		    if(dto.isNuevoInversion())
		        sbSql.append("\n	'P', " + dto.getDiasAnual() + ", ");
		    else
		        sbSql.append("\n	'C', " + dto.getDiasAnual() + ", ");

		    sbSql.append("\n	" + dto.getTasaIsr() + ", 0 " + ",'" + dto.getBGarantia() + "', " + dto.getImporteTraspaso());
		    
		    if(dto.isNuevoInversion())
		    	sbSql.append("\n	,'S', '" + dto.getTipoOrden() + "'");
		    else
		    	sbSql.append("\n	,'N', '" + dto.getTipoOrden() + "'");
		    
		    if(dto.getNoOrdenRef() > 0)
		        sbSql.append("\n	," + dto.getNoOrdenRef());
		    
		    if(dto.getNoAvisoRef() > 0) 
		        sbSql.append("\n	," + dto.getNoAvisoRef());
		    
		    if(dto.getGrupoInversion() > 0)
		        sbSql.append("\n	, " + dto.getGrupoInversion());
		    
		    sbSql.append("\n) ");
		    
		    iRegAfec = jdbcTemplate.update(sbSql.toString());

		    //System.out.println(sbSql);
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:insertarOrdenInversion");
			e.printStackTrace();
		}
		return iRegAfec;
	}
	
	/**
	 * Este metodo verifica si una fecha es inhabil retorna true,
	 * es utilizado en OrdenDeInversion
	 * FunSQLSelect647
	 * @param sFecha parametro de tipo string
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	public boolean consultarFechaInhabil(String sFecha){
		List<Date> listFec = new ArrayList<Date>();
		StringBuffer sbSql = new StringBuffer();
		boolean bInahabil = false;
		try{
		    sbSql.append("SELECT fec_inhabil ");
		    sbSql.append("\n	FROM dia_inhabil ");
		    
		    if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
		    	sbSql.append("\n 	WHERE fec_inhabil = '" + sFecha + "'");
		    else if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
		    	sbSql.append("\n 	WHERE fec_inhabil = to_date('" + sFecha + "','DD/MM/YYYY')");
		    else if(ConstantesSet.gsDBM.equals("DB2"))
		    	sbSql.append("\n 	WHERE fec_inhabil = cast('" + funciones.ponerFechaDate(sFecha) + "' as date)");
		    listFec = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public Date mapRow(ResultSet rs, int idx)throws SQLException{
		    		return rs.getDate("fec_inhabil");
		    	}
		    });
		    
		    if(listFec.size() > 0)
		    	bInahabil = true;
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarFechaInhabil");
		}
		return bInahabil;
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int insertarParametro(ParametroDto dto){
		StringBuffer sbSql = new StringBuffer();
		int regAfec = 0;
		try{
		   sbSql.append("INSERT INTO parametro (");
		   sbSql.append("\n     no_folio_param as no_folio_param, no_empresa, id_tipo_operacion, id_forma_pago, ");
		   sbSql.append("\n     dias_inv, usuario_alta, id_tipo_docto, no_docto, grupo, ");
		   sbSql.append("\n     no_folio_mov, folio_ref, fec_valor, fec_valor_original, ");   
		   sbSql.append("\n     no_cuenta, fec_operacion, fec_alta, importe, valor_tasa, ");
		   sbSql.append("\n     importe_original, tipo_cambio, id_caja, id_divisa, aplica, ");
		   sbSql.append("\n     id_estatus_mov, b_salvo_buen_cobro, id_estatus_reg, ");

		   if(dto.getIdBanco() > 0) 
			   sbSql.append("\n	id_banco, ");
	    
		   if(dto.getIdChequera() != null && !dto.getIdChequera().equals(""))
		       sbSql.append("\n id_chequera, ");

		   if(dto.getIdBancoReg() > 0) 
			   sbSql.append("\n	ID_BANCO_REG, ");
	    
		   if(dto.getIdChequeraReg() != null && !dto.getIdChequeraReg().equals(""))
		       sbSql.append("\n ID_CHEQUERA_REG, ");

		   if(dto.getIdBancoBenef() > 0) 
			   sbSql.append("\n	id_banco_benef, ");
	    
		   if(dto.getIdChequeraBenef() != null && !dto.getIdChequeraBenef().equals(""))
		       sbSql.append("\n id_chequera_benef, ");
		    
		   if(dto.getSecuencia() > 0)
			   sbSql.append("\n	secuencia, ");
		    
		   if(dto.getConcepto() != null && !dto.getConcepto().equals(""))
			   sbSql.append("\n	concepto, ");
		        
		    sbSql.append("\n     origen_mov ");
		    
		   if(dto.getReferencia() != null && !dto.getReferencia().equals(""))
			   sbSql.append("\n     , referencia ");
		    
		   if(dto.getRubro() != null)
			   sbSql.append("\n     , ID_RUBRO ");
		   
		   if(dto.getIdGrupoInv() != null)
			   sbSql.append("\n     , ID_GRUPO ");
			   
		   if(dto.getNoCliente() != null)
			   sbSql.append("\n     , NO_CLIENTE ");

		   sbSql.append("\n     ) ");
		    
		   sbSql.append("\n VALUES ( ");
		   sbSql.append("\n     " + dto.getNoFolioParam() + ", " + dto.getNoEmpresa() + ", " + dto.getIdTipoOperacion());
		   sbSql.append("\n     , " + dto.getIdFormaPago() + ", " + dto.getDiasInv() + ", " + dto.getUsuarioAlta());
		   sbSql.append("\n     , " + dto.getIdTipoDocto() + ", '" + dto.getNoDocto() + "', " + dto.getGrupo());
		   sbSql.append("\n     , " + dto.getNoFolioMov() + ", " + dto.getFolioRef());
		   
		  /* if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
		   {
			   sbSql.append("\n, convert(datetime,'" + funciones.ponerFecha(dto.getFecValor()) + "')");
	           sbSql.append("\n, convert(datetime,'" + funciones.ponerFecha(dto.getFecValorOriginal()) + "')");
	           sbSql.append("\n, " + dto.getNoCuenta());
	           sbSql.append("\n, convert(datetime,'" + funciones.ponerFecha(dto.getFecOperacion()) + "')");
	           sbSql.append("\n, convert(datetime,'" + funciones.ponerFecha(dto.getFecAlta()) + "')" );
		   }
		   else if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
		   {*/
			   sbSql.append("\n, '" + funciones.ponerFechaSola(dto.getFecValor()) + "'");
	           sbSql.append("\n, '" + funciones.ponerFechaSola(dto.getFecValorOriginal()) + "'");
	           sbSql.append("\n, " + dto.getNoCuenta());
	           sbSql.append("\n, '" + funciones.ponerFechaSola(dto.getFecOperacion()) + "'");
	           sbSql.append("\n, '" + funciones.ponerFechaSola(dto.getFecAlta()) + "'");
		   /*}
		   else if(ConstantesSet.gsDBM.equals("DB2"))
		   {
			   sbSql.append("\n, cast('" + dto.getFecValor() + "' as date)");
	           sbSql.append("\n, cast('" + dto.getFecValorOriginal() + "' as date)");
	           sbSql.append("\n, " + dto.getNoCuenta());
	           sbSql.append("\n, cast('" + dto.getFecOperacion() + "' as date)");
	           sbSql.append("\n, cast('" + dto.getFecAlta() + "' as date)");
		   }*/
		   
		   sbSql.append("\n     , " + dto.getImporte() + ", " + dto.getValorTasa() + ", " + dto.getImporteOriginal());
		   sbSql.append("\n     , " + dto.getTipoCambio() + ", " + dto.getIdCaja() + ", '" + dto.getIdDivisa() + "'");
		   sbSql.append("\n     , " + dto.getAplica() + ", '" + dto.getIdEstatusMov() + "'");
		   sbSql.append("\n     , '" + dto.getBSalvoBuenCobro() + "', '" + dto.getIdEstatusReg() + "'");
		    
		   if(dto.getIdBanco() > 0) 
			   sbSql.append("\n	, "+ dto.getIdBanco());
	    
		   if(dto.getIdChequera() != null && !dto.getIdChequera().equals(""))
		       sbSql.append("\n , '"+ dto.getIdChequera() +"' ");

		   if(dto.getIdBancoReg() > 0) 
			   sbSql.append("\n	, "+ dto.getIdBancoReg());
	    
		   if(dto.getIdChequeraReg() != null && !dto.getIdChequeraReg().equals(""))
		       sbSql.append("\n , '"+dto.getIdChequeraReg()+"' ");


		   if(dto.getIdBancoBenef() > 0)
		       sbSql.append("\n, " + dto.getIdBancoBenef());
		    
		   if(dto.getIdChequeraBenef() != null && !dto.getIdChequeraBenef().equals(""))
		       sbSql.append("\n, '" + dto.getIdChequeraBenef() + "'");
		    
		   if(dto.getSecuencia() > 0)
		       sbSql.append("\n, " + dto.getSecuencia() + "");
		    
		   if(dto.getConcepto() != null && !dto.getConcepto().equals(""))
		       sbSql.append("\n, '" + dto.getConcepto()+ "'");
		    
		   sbSql.append("\n, '" + dto.getOrigenMov() + "'");
		    
		   if(dto.getReferencia() != null && !dto.getReferencia().equals(""))
		       sbSql.append("\n     ,'" + dto.getReferencia() + "' ");
		    
		   if(dto.getRubro() != null)
			   sbSql.append("\n     , "+ dto.getRubro() );
		   
		   if(dto.getIdGrupoInv() != null)
			   sbSql.append("\n     , "+ dto.getIdGrupoInv());

		   if(dto.getNoCliente() != null)
			   sbSql.append("\n     , "+ dto.getNoCliente());

		   sbSql.append("\n )");
		   
		   regAfec = jdbcTemplate.update(sbSql.toString());
		   System.out.println("query inserta parametro: " + sbSql.toString());
		   
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:insertarParametro");
		}
		return regAfec;
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public Map<String, Object> ejecutarGenerador(GeneradorDto dto)
	{
		Map<String, Object> resultado= new HashMap<String,Object>();
		try{
			System.out.println("En ejecutarGenerador.::. ");
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			resultado = consultasGenerales.ejecutarGenerador(dto);
		}catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:InversionesDaoImpl, M:ejecutarGenerador U:"+dto.getIdUsuario()
			+ "	F:"+dto.getNomForma());
		}
		return resultado; 
	}
	
	/**
	 * Este metodo consulta la tasa_isr de la tabla retencion,
	 * es utilizado en OrdenDeInversion, VencimientoDeInversion
	 * FunSQLSelect648
	 * @param sPlazo
	 * @param sContratoIns
	 * @param iCuenta
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public List<RetencionDto> consultarIsrRetencion(int iPlazo, String sContratoIns, int iCuenta){
		StringBuffer sbSql = new StringBuffer();
		List<RetencionDto> listImp = new ArrayList<RetencionDto>();
		try{
		    sbSql.append("Select ");
		    sbSql.append("\n tasa_isr, (select condicion_alt from cuenta where contrato_institucion = '" + sContratoIns+ "' ");
		    sbSql.append("\n and no_cuenta = " + iCuenta + "  and id_tipo_cuenta = 'C') as condicion");
		    sbSql.append("\n From ");
		    sbSql.append("\n retencion ");
		    sbSql.append("\n Where " + iPlazo);
		    sbSql.append("\n Between plazo_min And plazo_max ");
		    
		    ////System.out.println(sbSql);
		    listImp = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public RetencionDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		RetencionDto dtoCons = new RetencionDto();
			    		dtoCons.setTasaIsr(rs.getDouble("tasa_isr"));
			    		dtoCons.setCondicionAlt(rs.getString("condicion"));
			    	return dtoCons;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarIsrRetencion");
		}
		return listImp;
	}
	
	/**
	 * FunSQLManejaBisiesto
	 * @param iNoInstitucion
	 * @return
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public boolean verificarManejaAnioBisiesto(int iNoInstitucion){
		boolean bManejaAB = false;
		int iRegCons = 0;
		StringBuffer sbSql = new StringBuffer();
		try{
		    sbSql.append("select count(*) from cuenta ");
		    sbSql.append("\n	where id_tipo_cuenta = 'C' ");
		    sbSql.append("\n	and b_isr_bisiesto = 'S'");
		    sbSql.append("\n	and no_institucion = " + iNoInstitucion);
		    
		    iRegCons = jdbcTemplate.queryForInt(sbSql.toString());
		    if(iRegCons > 0)
		    	bManejaAB = true;
		    else
		    	bManejaAB = false;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:verificarManejaAnioBisiesto");
			return false;
		}
		return bManejaAB;
	}
	
	/**
	 * Este metodo consulta la tabla orden_inversion,movimienton, cat_tipo_valor,
	 * es utilizado en ConsultaOrdenesInversion.js
	 * FunSQLSelect234
	 * @param iIdEmpresa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ConsultaOrdenInversionDto> consultarOrdenesInversion(int iIdEmpresa){
		List<ConsultaOrdenInversionDto> listConsOrden = new ArrayList<ConsultaOrdenInversionDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("Select ");
		    sbSql.append("\n A.no_orden, ");
		    sbSql.append("\n A.id_tipo_valor, ");
		    sbSql.append("\n A.no_cuenta, ");
		    sbSql.append("\n A.no_institucion, ");
		    sbSql.append("\n A.no_contacto, ");
		    
		    sbSql.append("\n A.importe, ");
		    sbSql.append("\n A.plazo, ");
		    sbSql.append("\n A.tasa, ");
		    sbSql.append("\n A.interes, ");
		    sbSql.append("\n A.isr, ");
		    
		    sbSql.append("\n A.id_papel, ");
		    sbSql.append("\n A.fec_alta, ");
		    sbSql.append("\n A.fec_venc, ");
		    sbSql.append("\n B.nombre_corto, ");
		    
//		    if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
//		    	sbSql.append("\n C.paterno + ' ' + C.materno + ' ' + C.nombre as nombre_completo, ");
//		    else if(ConstantesSet.gsDBM.equals("POSTGRESQL") || ConstantesSet.gsDBM.equals("DB2"))
//		    	sbSql.append("\n C.paterno || ' ' || C.materno || ' ' || C.nombre as nombre_completo, ");

		    sbSql.append("\n D.desc_tipo_valor, ");
		    sbSql.append("\n A.b_autoriza, d.id_divisa, ");
		    
		    sbSql.append("\n m.no_folio_det, m.id_tipo_operacion, m.origen_mov,");
		    sbSql.append("\n m.id_forma_pago, m.id_tipo_movto, m.lote_entrada,");
		    sbSql.append("\n m.b_salvo_buen_cobro, m.id_estatus_mov, m.b_entregado");
		    
		    sbSql.append("\n From ");
//		    sbSql.append("\n orden_inversion A, persona B, persona C, cat_tipo_valor D, ");
		    sbSql.append("\n orden_inversion A, persona B, cat_tipo_valor D, ");
		    sbSql.append("\n movimiento m");
		    
		    sbSql.append("\n Where ");
		    
		    sbSql.append("\n A.id_estatus_ord = 'C'");
		    sbSql.append("\n And A.no_empresa = " + iIdEmpresa);
		    sbSql.append("\n And A.no_institucion = B.no_persona ");
		    sbSql.append("\n And B.id_tipo_persona = 'B' ");
//		    sbSql.append("\n And A.no_contacto = C.no_persona ");
		    sbSql.append("\n And D.id_tipo_valor = A.id_tipo_valor ");
//		    sbSql.append("\n And B.no_empresa = 1 ");
//		    sbSql.append("\n And C.no_empresa = 1 ");
		    
		    sbSql.append("\n And A.no_empresa = m.no_empresa");
		    sbSql.append("\n And A.no_orden = m.no_docto");
		    sbSql.append("\n and  m.id_estatus_mov = 'O' and m.id_tipo_operacion = 4000");
//		    sbSql.append("\n and B.id_tipo_persona = 'B'");
//		    sbSql.append(" and C.id_tipo_persona = 'F'");
		    
		    sbSql.append("\n Order by B.nombre_corto");
		    System.out.println(sbSql.toString());
		    
		    listConsOrden = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public ConsultaOrdenInversionDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		ConsultaOrdenInversionDto dtoCons = new ConsultaOrdenInversionDto();
		    			dtoCons.setNoOrden(rs.getString("no_orden"));
		    			dtoCons.setIdTipoValor(rs.getString("id_tipo_valor"));
		    			dtoCons.setNoCuenta(rs.getInt("no_cuenta"));
		    			dtoCons.setNoInstitucion(rs.getInt("no_institucion"));
		    			dtoCons.setNoContacto(rs.getInt("no_contacto"));
		    			dtoCons.setImporte(rs.getDouble("importe"));
		    			dtoCons.setPlazo(rs.getInt("plazo"));
		    			dtoCons.setTasa(rs.getDouble("tasa"));
		    			dtoCons.setInteres(rs.getDouble("interes"));
		    			dtoCons.setIsr(rs.getDouble("isr"));
		    			dtoCons.setIdPapel(rs.getString("id_papel"));
		    			dtoCons.setFecAlta(rs.getDate("fec_alta"));
		    			dtoCons.setFecVenc(rs.getDate("fec_venc"));
		    			dtoCons.setNombreCorto(rs.getString("nombre_corto"));
//		    			dtoCons.setNombreCompleto(rs.getString("nombre_completo"));
		    			dtoCons.setDescTipoValor(rs.getString("desc_tipo_valor"));
		    			dtoCons.setBAutoriza(rs.getString("b_autoriza"));
		    			dtoCons.setIdDivisa(rs.getString("id_divisa"));
		    			dtoCons.setNoFolioDet(rs.getInt("no_folio_det"));
		    			dtoCons.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
		    			dtoCons.setOrigenMov(rs.getString("origen_mov"));
		    			dtoCons.setIdFormaPago(rs.getInt("id_forma_pago"));
		    			dtoCons.setIdTipoMovto(rs.getString("id_tipo_movto"));
		    			dtoCons.setLoteEntrada(rs.getInt("lote_entrada"));
		    			dtoCons.setBSalvoBuenCobro(rs.getString("b_salvo_buen_cobro"));
		    			dtoCons.setIdEstatusMov(rs.getString("id_estatus_mov"));
		    			dtoCons.setBEntregado(rs.getString("b_entregado"));
		    		return dtoCons;
		    	}
		    });

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarOrdenesInversion");
		}
		return listConsOrden;
	}
	
	/**
	 * Este metodo actualiza el campo b_autoriza de orden_inversion,
	 * es utilizado en ConsultaOrdenesInversion.js
	 * FunSQLUpdate72, FunSQLUpdateDes
	 * @param iNoOrden
	 * @return un entero mayor a cero si se modifico correctamente
	 */
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public int actualizarAutorizaOrdenInversion(String sAutoriza, int iNoOrden){
		int iRegAfec = 0;
		StringBuffer sbSql = new StringBuffer();
		try{
		    sbSql.append("\n UPDATE orden_inversion ");
		    sbSql.append("\n SET b_autoriza = '" + sAutoriza +"'");
		    sbSql.append("\n WHERE no_orden = '" + iNoOrden + "'");
		    
		    iRegAfec = jdbcTemplate.update(sbSql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:actualizarAutorizaOrdenInversion");
		}
		return iRegAfec;
	}
	
	/**
	 * FunSQLSelect236
	 * FunSQLSelect582
	 * @param iNoCuenta
	 * @param iIdEmpresa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<CuentaPropiaDto> consultarCuentaPropia(int iNoCuenta, int iIdEmpresa){
		List<CuentaPropiaDto>  listCons = new ArrayList<CuentaPropiaDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
//			if(ConstantesSet.gsDBM.equals("ORACLE"))
//			{
//		        sbSql.append("Select A.id_banco, A.id_chequera, nvl(C.id_chequera,'ND') as id_chequera_cta_banco ");
//		        sbSql.append("\n From ");
//		        sbSql.append("\n ctas_contrato A, cat_banco B, cat_cta_banco C ");
//		        sbSql.append("\n Where");
//		        sbSql.append("\n A.no_cuenta = " + iNoCuenta);
//		        sbSql.append("\n And A.no_empresa = " + iIdEmpresa);
//		        sbSql.append("\n And C.tipo_chequera <> 'U' ");
//		        sbSql.append("\n And B.id_banco = A.id_banco ");
//		        sbSql.append("\n And A.id_chequera = C.id_chequera(+) ");
//		        sbSql.append("\n Order by 1");
//			}
//			else if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
//			{
//				sbSql.append("Select A.id_banco, A.id_chequera, ISNULL(C.id_chequera,'ND') as id_chequera_cta_banco");
//		        sbSql.append("\n From ");
//		        sbSql.append("\n ctas_contrato A, cat_banco B, cat_cta_banco C ");
//		        sbSql.append("\n Where");
//		        sbSql.append("\n A.no_cuenta = " + iNoCuenta);
//		        sbSql.append("\n And A.no_empresa = " + iIdEmpresa);
//		        sbSql.append("\n And C.tipo_chequera <> 'U' ");
//		        sbSql.append("\n And B.id_banco = A.id_banco ");
//		        sbSql.append("\n And A.id_chequera *= C.id_chequera ");
//		        sbSql.append("\n Order by 1");
//			}else if(ConstantesSet.gsDBM.equals("POSTGRESQL") || ConstantesSet.gsDBM.equals("DB2"))
//			{
				sbSql.append("Select A.id_banco, A.id_chequera,coalesce(C.id_chequera,'ND') as id_chequera_cta_banco");
		        sbSql.append("\n From ");
		        sbSql.append("\n ctas_contrato A left join cat_cta_banco C");
		        sbSql.append("\n  on (A.id_chequera = C.id_chequera)");
		        sbSql.append("\n , cat_banco B ");
		        sbSql.append("\n Where");
		        sbSql.append("\n A.no_cuenta = " + iNoCuenta);
		        sbSql.append("\n And A.no_empresa = " + iIdEmpresa);
		        sbSql.append("\n And C.tipo_chequera <> 'U' ");
		        sbSql.append("\n And B.id_banco = A.id_banco ");
		        sbSql.append("\n Order by 1");
		        System.out.println(".::"+sbSql.toString());
//			}
			logger.info("borrar Query cuentaP :  " + sbSql.toString());
			listCons = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
				public  CuentaPropiaDto mapRow(ResultSet rs, int idx)throws SQLException{
					CuentaPropiaDto dtoCuenta = new CuentaPropiaDto();
						dtoCuenta.setIdBanco(rs.getInt("id_banco"));
						dtoCuenta.setIdChequera(rs.getString("id_chequera"));
						dtoCuenta.setIdChequeraDos(rs.getString("id_chequera_cta_banco"));
					return dtoCuenta;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarCuentaPropia");
		}
		return listCons;
	}
	
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public Map<String, Object> ejecutarRevividor(RevividorDto dtoRev)
	{
		int result = 1;
		Map<String, Object> resultado = new HashMap<String, Object>();
		String resultadoRevividor = "";	
		try{
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
			result = consultasGenerales.ejecutarRevividorOR(dtoRev.getPsRevividor(), dtoRev.getNoFolioDet(),  dtoRev.getIdTipoOperacion(),
					dtoRev.getPsTipoCancelacion(), dtoRev.getIdEstatusMov(), dtoRev.getPsOrigenMov(), dtoRev.getIdFormaPago(), 
					dtoRev.getBEntregado(), dtoRev.getIdTipoMovto(), dtoRev.getImporte(), dtoRev.getNoEmpresa(), 
					dtoRev.getNoCuenta(), dtoRev.getIdChequera(), dtoRev.getIdBanco(), dtoRev.getIdUsuario(), 
					dtoRev.getNoDocto() + "", dtoRev.getLote(), dtoRev.getBSalvoBuenCobro(), dtoRev.getFecConfTrans(), 
					dtoRev.getIdDivisa(), resultadoRevividor, false);
			
			if(result==0)				
				resultado.put("result","0");
				
			else				
				resultado.put("result","1");
			
		}
		catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
			+ "P:Inversiones, C: InversionesDaoImpl, M: ejecutarRevividor,	U: " + dtoRev.getIdUsuario() + ", F: " + dtoRev.getNomForma());
			e.printStackTrace();
		}
		return resultado;
	}
	
	//Inician metodos Liquidacion de Inversiones
	
	/**
	 * Este metodo consulta las ordenes de inversion que ya estan autorizadas,
	 * de la tabla orden de inversion y que esten en movimiento, es utilizado en
	 * LiquidacionDeInversiones.js
	 * FunSQLSelect580
	 * @param iIdEmpresa
	 * @param iAvisoInversion
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ConsultaOrdenInversionDto> consultarLiquidaOrdenInversion(Integer iIdEmpresa, Integer iAvisoInversion, Integer idUsuario){
		List<ConsultaOrdenInversionDto> listLiqOrd = new ArrayList<ConsultaOrdenInversionDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
			System.out.println("entra al qry oracle");

			
			if(ConstantesSet.gsDBM.equals("ORACLE")){
				System.out.println("..::A");
	sbSql.append("\n Select  to_char(A.no_orden,'999') As no_orden,  ");
			}else{
				System.out.println("..::b");
				sbSql.append("\n Select  cast(A.no_orden as varchar(15)) As no_orden,  ");
            sbSql.append("\n  A.id_tipo_valor,  ");
            sbSql.append("\n  A.no_cuenta As no_cuenta,  ");
            sbSql.append("\n  A.no_institucion As no_institucion,  ");
//            sbSql.append("\n  to_char(A.no_contacto) As no_contacto,  ");
            sbSql.append("\n  A.importe As importe,  ");
            sbSql.append("\n  A.plazo As plazo,  ");
            sbSql.append("\n  A.tasa As tasa,  ");
            sbSql.append("\n  A.interes As interes,  ");
            sbSql.append("\n  A.isr As isr,  ");
            sbSql.append("\n  A.id_papel,  ");
            sbSql.append("\n  A.fec_alta as fec_alta,  ");
            sbSql.append("\n  A.fec_venc as fec_venc,  ");
            sbSql.append("\n  B.nombre_corto,  ");
            sbSql.append("\n  D.desc_tipo_valor,  ");
            sbSql.append("\n  M.no_folio_det,  ");
            sbSql.append("\n m.ID_TIPO_OPERACION,   ");
            sbSql.append("\n A.ID_BANCO, A.ID_CHEQUERA,   ");
            sbSql.append("\n A.NO_EMPRESA,   ");
            sbSql.append("\n A.ID_BANCO_REG,A.ID_CHEQUERA_REG ,   ");
            sbSql.append("\n A.ID_BANCO_BENEF,A.ID_CHEQUERA_BENEF,   ");
            sbSql.append("\n A.DIVISA, e.NOM_EMPRESA, A.IMPORTE + A.INTERES - A.ISR as NETO   ");
            sbSql.append("\n From orden_inversion A left outer join movimiento m ");
            sbSql.append("\n   on(a.no_orden = m.no_docto and M.id_tipo_operacion = 4000 ) ");
			}if(ConstantesSet.gsDBM.equals("ORACLE")){
				System.out.println("..::C");
            	sbSql.append("\n join persona b on(to_char(A.no_institucion) = B.EQUIVALE_PERSONA) ");
			}else{
				System.out.println("..::D");
            	sbSql.append("\n join persona b on(cast(A.no_institucion as varchar(15)) = b.no_persona and id_tipo_persona = 'B') ");
            	sbSql.append("\n join cat_tipo_valor D on (D.id_tipo_valor = A.id_tipo_valor) ");
            	sbSql.append("\n join empresa e on (A.NO_EMPRESA = E.NO_EMPRESA) ");
            
			}if(iIdEmpresa != -1){
            	sbSql.append("\n join USUARIO_EMPRESA ue on  a.no_empresa = ue.no_empresa and ue.no_usuario = "+ idUsuario);
                sbSql.append("\n where ");
            	sbSql.append(" A.no_empresa =  "+ iIdEmpresa);
	        
			}else{
	            sbSql.append("\n where ");
	            sbSql.append(" (a.id_estatus_ord = 'C' ");
            sbSql.append("\n   or a.id_estatus_ord = 'P') ");
            sbSql.append("\n And A.tipo_orden = 'E' ");
//            sbSql.append("\n and B.id_tipo_persona = 'B' ");
			} 
		    System.out.println("..::qry"+sbSql.toString());
			logger.info("Query Liq : " + sbSql.toString());
			listLiqOrd = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public ConsultaOrdenInversionDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		ConsultaOrdenInversionDto dtoCons = new ConsultaOrdenInversionDto();
		    			dtoCons.setNoOrden(rs.getString("no_orden"));
		    			dtoCons.setIdTipoValor(rs.getString("id_tipo_valor"));
		    			dtoCons.setNoCuenta(rs.getInt("no_cuenta"));
		    			dtoCons.setNoInstitucion(rs.getInt("no_institucion"));
//		    			dtoCons.setNoContacto(rs.getInt("no_contacto"));
		    			dtoCons.setImporte(rs.getDouble("importe"));
		    			dtoCons.setPlazo(rs.getInt("plazo"));
		    			dtoCons.setTasa(rs.getDouble("tasa"));
		    			dtoCons.setInteres(rs.getDouble("interes"));
		    			dtoCons.setIsr(rs.getDouble("isr"));
		    			dtoCons.setIdPapel(rs.getString("id_papel"));
		    			dtoCons.setFecAlta(rs.getDate("fec_alta"));
		    			dtoCons.setFecVenc(rs.getDate("fec_venc"));
		    			
		    			if(dtoCons.getFecAlta()!=null)
		    				dtoCons.setsFecAlta(funciones.ponerFecha(dtoCons.getFecAlta()));
		    			if(dtoCons.getFecVenc()!=null)
		    				dtoCons.setsFecVenc(funciones.ponerFecha(dtoCons.getFecVenc()));
		    			
		    			dtoCons.setNombreCorto(rs.getString("nombre_corto"));
		    			//dtoCons.setNombreCompleto(rs.getString("nombre_completo"));
		    			dtoCons.setDescTipoValor(rs.getString("desc_tipo_valor"));
		    			dtoCons.setNoFolioDet(rs.getInt("no_folio_det"));
		    			
		    			dtoCons.setIdTipoOperacion(rs.getInt("ID_TIPO_OPERACION"));
		    			dtoCons.setIdBanco(rs.getInt("ID_BANCO"));
		    			dtoCons.setIdChequera(rs.getString("ID_CHEQUERA"));
		    			dtoCons.setNoEmpresa(rs.getInt("NO_EMPRESA"));
		    			dtoCons.setNomEmpresa(rs.getString("NOM_EMPRESA"));
		    			
		    			dtoCons.setIdBancoReg(rs.getInt("ID_BANCO_REG"));
		    			dtoCons.setIdChequeraReg(rs.getString("ID_CHEQUERA_REG"));
		    			dtoCons.setIdBancoInst(rs.getInt("ID_BANCO_BENEF"));
		    			dtoCons.setIdChequeraInst(rs.getString("ID_CHEQUERA_BENEF"));
		    			
		    			dtoCons.setIdDivisa(rs.getString("DIVISA"));
		    			
		    			dtoCons.setNeto(rs.getDouble("NETO"));
		    			
		    		return dtoCons;
		    	}
		    });

		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarLiquidaOrdenInversion");
		}
		return listLiqOrd;
	}
	
	/**
	 * Este metodo consulta a cat_banco, es utilizado para obtener los
	 * bancos de regreso de inversion en liquidacion de inversiones 
	 * FunSQLCombo256
	 * @param iIdEmpresa
	 * @param sIdDivisa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarBancosRegresoInversion(int iIdEmpresa, String sIdDivisa){
		List<LlenaComboGralDto> listBancos = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
			
		    sbSql.append("SELECT id_banco as ID, desc_banco as Descrip ");
		    sbSql.append("\n FROM cat_banco ");
		    sbSql.append("\n WHERE id_banco in ");
		    sbSql.append("\n     ( SELECT DISTINCT id_banco ");
		    sbSql.append("\n       FROM cat_cta_banco ");
		    //sbSql.append("\n       WHERE tipo_chequera <> 'U' ");
		    //sbSql.append("\n           and tipo_chequera <> 'V' ");
		    sbSql.append("\n       WHERE tipo_chequera in ('I','M','C') ");
		    
		    if(!sIdDivisa.equals(""))
		        sbSql.append("\n	and id_divisa = '" + sIdDivisa + "' ");
		    
		    sbSql.append("\n	and no_empresa = " + iIdEmpresa + " )");
		    
		    listBancos = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		LlenaComboGralDto dtoBan = new LlenaComboGralDto();
		    			dtoBan.setId(rs.getInt("ID"));
		    			dtoBan.setDescripcion(rs.getString("Descrip"));
		    		return dtoBan;
		    	}
		    });
		
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarBancosRegresoInversion");
		}
		return listBancos;
	}
	
	/**
	 * Este metodo obtiene los contratos de cuenta, es utilizado en
	 * liquidacion de ordenes de inversion
	 * FunSQLSelect574
	 * @param iIdEmpresa
	 * @param iNumCta
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<CuentaDto> consultarContratosLiq(int iIdEmpresa, int iNumCta){
		StringBuffer sbSql = new StringBuffer();
		List<CuentaDto> listContratos = new ArrayList<CuentaDto>();
		try{
			sbSql.append("Select	");
//		    if(ConstantesSet.gsDBM.equals("ORACLE"))
//		    {
		        sbSql.append("\n	A.desc_cuenta , ");
		        sbSql.append("\n	A.no_cuenta, ");
		        sbSql.append("\n	A.id_divisa, ");
		        sbSql.append("\n	D.id_banco, ");
		        sbSql.append("\n	D.id_chequera, ");
		        sbSql.append("\n	B.desc_banco, ");
		        sbSql.append("\n	nvl(C.id_chequera,' ') as id_chequera, ");
		        sbSql.append("\n	nvl(A.no_linea,0) as no_linea");
		        
		        sbSql.append("\n	From ");
		        sbSql.append("\n	cuenta A, cat_banco B, cat_cta_banco C, ctas_contrato D ");
		        
		        sbSql.append("\n	Where");
		        sbSql.append("\n	A.no_cuenta = " + iNumCta);
		        sbSql.append("\n	And A.no_empresa = " + iIdEmpresa);
		        sbSql.append("\n 	And A.id_tipo_cuenta = 'C' ");
		        sbSql.append("\n 	And C.tipo_chequera <> 'U' ");
		        sbSql.append("\n	And B.id_banco = D.id_banco ");
		        
		        sbSql.append("\n	And A.no_cuenta = D.no_cuenta ");
		        sbSql.append("\n	And A.no_linea = D.no_linea ");
		        sbSql.append("\n	And A.no_empresa = D.no_empresa ");
		        sbSql.append("\n	And D.id_chequera = C.id_chequera(+) ");
		        
		        sbSql.append("\n	Order by 1");
//		    }
//		    else if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
//		    {
//		        sbSql.append("\n	A.desc_cuenta , ");
//		        sbSql.append("\n	A.no_cuenta, ");
//		        sbSql.append("\n	A.id_divisa, ");
//		        sbSql.append("\n	D.id_banco, ");
//		        sbSql.append("\n	D.id_chequera, ");
//		        sbSql.append("\n	B.desc_banco, ");
//		        sbSql.append("\n	isnull(C.id_chequera,' ') as id_chequera_ccb, ");
//		        sbSql.append("\n	isnull(A.no_linea,0) as no_linea");
//		        
//		        sbSql.append("\n 	From ");
//		        sbSql.append("\n 	cuenta A, cat_banco B, cat_cta_banco C, ctas_contrato D ");
//		        
//		        sbSql.append("\n 	Where");
//		        sbSql.append("\n 	A.no_cuenta = " + iNumCta);
//		        sbSql.append("\n 	And A.no_empresa = " + iIdEmpresa);
//		        sbSql.append("\n 	And A.id_tipo_cuenta = 'C' ");
//		        sbSql.append("\n 	And C.tipo_chequera <> 'U' ");
//		        sbSql.append("\n 	And B.id_banco = D.id_banco ");
//		         
//		        sbSql.append("\n 	And A.no_cuenta = D.no_cuenta ");
//		        sbSql.append("\n 	And A.no_linea = D.no_linea ");
//		        sbSql.append("\n 	And A.no_empresa = D.no_empresa ");
//		        sbSql.append("\n 	And D.id_chequera *= C.id_chequera");
//		        
//		        sbSql.append("\n 	Order by 1");
//		    }
//		    else if(ConstantesSet.gsDBM.equals("POSTGRESQL") || ConstantesSet.gsDBM.equals("DB2"))
//		    {
//		        sbSql.append("\n	A.desc_cuenta , ");
//		        sbSql.append("\n	A.no_cuenta, ");
//		        sbSql.append("\n	A.id_divisa, ");
//		        sbSql.append("\n	D.id_banco, ");
//		        sbSql.append("\n	D.id_chequera, ");
//		        sbSql.append("\n	B.desc_banco, ");
//		        sbSql.append("\n	COALESCE(C.id_chequera,' ') as id_chequera_ccb, ");
//		        sbSql.append("\n	COALESCE(A.no_linea,0) as no_linea");
//		        
//		        sbSql.append("\n 	From ");
//		        sbSql.append("\n 	cuenta A, cat_banco B, ");
//		        sbSql.append("\n	ctas_contrato D left join cat_cta_banco C on (D.id_chequera = C.id_chequera)  ");
//		        
//		        sbSql.append("\n 	Where");
//		        sbSql.append("\n 	A.no_cuenta = " + iNumCta);
//		        sbSql.append("\n 	And A.no_empresa = " + iIdEmpresa);
//		        sbSql.append("\n 	And A.id_tipo_cuenta = 'C' ");
//		        sbSql.append("\n 	And C.tipo_chequera <> 'U' ");
//		        sbSql.append("\n 	And B.id_banco = D.id_banco ");
//		         
//		        sbSql.append("\n 	And A.no_cuenta = D.no_cuenta ");
//		        sbSql.append("\n 	And A.no_linea = D.no_linea ");
//		        sbSql.append("\n 	And A.no_empresa = D.no_empresa ");
//		                
//		        sbSql.append("\n 	Order by 1");
//		    }
		    
		    listContratos = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public CuentaDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		CuentaDto dtoCons = new CuentaDto();
			    		dtoCons.setDescCuenta(rs.getString("desc_cuenta"));
			    		dtoCons.setNoCuenta(rs.getInt("no_cuenta"));
			    		dtoCons.setIdDivisa(rs.getString("id_divisa"));
			    		dtoCons.setIdBancoDep(rs.getInt("id_banco"));
			    		dtoCons.setIdChequeraDep(rs.getString("id_chequera"));
			    		dtoCons.setDescBanco(rs.getString("desc_banco"));
			    		dtoCons.setIdChequeraDos(rs.getString("id_chequera_ccb"));
				        dtoCons.setNoLinea(rs.getInt("no_linea"));
			        return dtoCons;
		    	}
		    });
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarContratosLiq");
		}
		return listContratos;
	}
	
	/**
	 * Este metodo consulta las chequeras en ctas_contrato,
	 * es utilizado en  liquidacionDeInversiones
	 * FunSQLCombo254
	 * @param iIdBancoLiq
	 * @param iNoContrato
	 * @param bInterna
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboChequeraDto> consultarChequerasLiq(int iIdBancoLiq, int iNoCuenta, boolean bInterna){
		List<LlenaComboChequeraDto> listChe = new ArrayList<LlenaComboChequeraDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("Select id_chequera as ID, id_chequera as DESCRIPCION");
			sbSql.append("\n From ctas_contrato ");
			sbSql.append("\n Where id_banco = " + iIdBancoLiq);
			sbSql.append("\n and no_cuenta = " + iNoCuenta);
				
		    if(bInterna)
		    {
		    	sbSql.append("\n and id_chequera in (select id_chequera from cat_cta_banco where no_empresa in (");
		    	sbSql.append("\n  select no_empresa from empresa where institucion_financiera in ( ");
		    	sbSql.append("\n  select no_institucion from cuenta where id_tipo_cuenta = 'C' and no_cuenta = " + iNoCuenta + ")))");
		    }
		    
		    listChe = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public LlenaComboChequeraDto mapRow(ResultSet rs, int idx)throws SQLException {
		    		LlenaComboChequeraDto dtoChe = new LlenaComboChequeraDto();
		    			dtoChe.setId(rs.getString("ID"));
		    			dtoChe.setDescripcion(rs.getString("DESCRIPCION"));
		    		return dtoChe;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarChequerasLiq");
		}
		return listChe;
	}
	
	/**
	 * Este metodo consulta los bancos de cat_banco, cat_cta_banco,
	 * es utilizado en LiquidacionDeInversiones
	 * FunSQLSelect569
	 * @param iIdEmpresa
	 * @param sIdDivisa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarBancosCargo(int iIdEmpresa, String sIdDivisa)
	{
		List<LlenaComboGralDto> listBancos = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
		    sbSql.append("SELECT DISTINCT A.id_banco, A.desc_banco, B.no_empresa ");
		    sbSql.append("\n FROM cat_banco A, cat_cta_banco B ");
		    sbSql.append("\n WHERE A.id_banco = B.id_banco ");
		    //sbSql.append("\n     And B.tipo_chequera <> 'U' ");
		    //sbSql.append("\n     And B.tipo_chequera <> 'V' ");
		    sbSql.append("\n     And B.tipo_chequera in('C','M') ");
		    sbSql.append("\n     And B.no_empresa = " + iIdEmpresa);
		    sbSql.append("\n     And B.id_divisa = '" + sIdDivisa + "' ");
		    sbSql.append("\n ORDER BY 2 ");
		    
		    listBancos = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		LlenaComboGralDto dtoCons = new LlenaComboGralDto();
		    			dtoCons.setId(rs.getInt("id_banco"));
		    			dtoCons.setDescripcion(rs.getString("desc_banco"));
		    		return dtoCons;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarBancosCargo");
		}
		return listBancos;
	}
	
	/**
	 * Este metodo consulta a cat_cta_banco, es utilizado en LiquidacionDeInversiones
	 * FunSQLSelect570
	 * @param iIdEmpresa
	 * @param iIdBanco
	 * @param sIdDivisa
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<CatCtaBancoDto> consultarChequerasCargo(int iIdEmpresa, int iIdBanco, String sIdDivisa){
		List<CatCtaBancoDto> listChe = new ArrayList<CatCtaBancoDto>();
		StringBuffer sbSql = new StringBuffer();
		
		try {
		    sbSql.append(" SELECT id_banco, id_chequera, saldo_final, id_divisa, tipo_chequera, desc_chequera ");
		    sbSql.append("\n FROM cat_cta_banco ");
		    sbSql.append("\n WHERE tipo_chequera in ('C','M')");
		    sbSql.append("\n  	And no_empresa = " + iIdEmpresa);
		    
		    if(iIdBanco != 0)
		    	sbSql.append("\n  	And id_banco = " + iIdBanco);
		    
		    sbSql.append("\n  	And id_divisa = '" + sIdDivisa + "'");
		    sbSql.append("\n Order by 1, 5");
		    
		    listChe = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public CatCtaBancoDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		CatCtaBancoDto dtoCons = new CatCtaBancoDto();
		    			dtoCons.setIdChequera(rs.getString("id_chequera"));
		    			dtoCons.setSaldoFinal(rs.getDouble("saldo_final"));
		    			dtoCons.setIdDivisa(rs.getString("id_divisa"));
		    			dtoCons.setDescChequera(rs.getString("desc_chequera"));
		    			dtoCons.setIdBanco(rs.getInt("id_banco"));
		    			dtoCons.setTipoChequera(rs.getString("tipo_chequera"));
		    		return dtoCons;
		    	}
		    });
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarChequerasCargo");
		}
		return listChe;
	}
	
	/**
	 * Este metodo inserta en parametro, es utilizado en liquidacion de inversiones
	 * FunSQLInsert173
	 * @param dto
	 * @return
	 */
	public int insertarParametroLiquidacion(ParametroDto dto){
		StringBuffer sbSql = new StringBuffer();
		int iRegAfec = 0;
		try{
			sbSql.append("INSERT INTO parametro (");
		    sbSql.append("\n     no_folio_param, no_empresa, beneficiario, no_cliente, id_banco, ");
		    sbSql.append("\n     id_banco_reg, id_chequera, id_chequera_reg, id_tipo_operacion, ");
		    sbSql.append("\n     id_forma_pago, " );
		    
		    if(dto.getDiasInv() > 0)
		    	sbSql.append("\n	dias_inv,");
		    
		    sbSql.append("\n usuario_alta, id_tipo_docto, no_docto, ");
		    
		    sbSql.append("\n     grupo, no_folio_mov, folio_ref, fec_valor, fec_valor_original, ");     
		    sbSql.append("\n     no_cuenta, fec_operacion, fec_alta, importe, " );
		    
		    if(dto.getValorTasa() > 0)	
		    	sbSql.append("\n	valor_tasa, ");
		    
		    sbSql.append("\n     importe_original, tipo_cambio, id_caja, id_divisa, aplica, ");
		    sbSql.append("\n     id_estatus_mov, origen_reg, b_salvo_buen_cobro, id_estatus_reg, ");
		    
		    if(dto.getIdBancoBenef() > 0)
		        sbSql.append("\n	id_banco_benef, ");
		    
		    if(dto.getIdChequeraBenef() != null && !dto.getIdChequeraBenef().equals(""))
		        sbSql.append("\n	id_chequera_benef, ");
		    
		    if(dto.getSecuencia() > 0)
		        sbSql.append("\n	secuencia, ");
		    
		    if(dto.getConcepto() != null && !dto.getConcepto().equals("")) 
		        sbSql.append("\n	concepto, ");
		        
		   if(dto.getRubro() != null)
			   sbSql.append("\n     ID_RUBRO, ");
		   
		   if(dto.getIdGrupoInv() != null)
			   sbSql.append("\n     ID_GRUPO, ");

		   sbSql.append("\n     origen_mov )"); 
		    sbSql.append("\n VALUES (");

		    sbSql.append("\n     " + dto.getNoFolioParam() + ", " + dto.getNoEmpresa() + ", '" + dto.getBeneficiario() + "' ");
		    sbSql.append("\n     , '" + dto.getNoCliente() + "', " + dto.getIdBanco() + ", " + dto.getIdBancoReg());
		    sbSql.append("\n     , '" + dto.getIdChequera()+ "', '" + dto.getIdChequeraReg() + "', " + dto.getIdTipoOperacion());
		    sbSql.append("\n	 , " + dto.getIdFormaPago());
		    
		    if(dto.getDiasInv() > 0)
		        sbSql.append("\n , " + dto.getDiasInv());
		    
		    sbSql.append("\n     , " + dto.getUsuarioAlta() + ", " + dto.getIdTipoDocto() + ", '" + dto.getNoDocto() + "'");
		    sbSql.append("\n     , " + dto.getGrupo());   
		    sbSql.append("\n, " + dto.getNoFolioMov() + ", " + dto.getFolioRef());
		    
//		    if(ConstantesSet.gsDBM.equals("DB2"))
//		    {
//		        sbSql.append("\n     , '" + funciones.ponerFecha(dto.getFecValor()) + "', '" + funciones.ponerFecha(dto.getFecValorOriginal()) + "'");
//		        sbSql.append("\n     , " + dto.getNoCuenta() + ", '" + funciones.ponerFecha(dto.getFecOperacion()) + "', '" + funciones.ponerFecha(dto.getFecAlta()) + "'");
//		    }
//		    else
//		    {
			    
		        sbSql.append("\n     , '" + FuncionesSql.ponFechaDMY(new java.sql.Date(dto.getFecValor().getTime()), true) + "', '" + FuncionesSql.ponFechaDMY(new java.sql.Date(dto.getFecValorOriginal().getTime()), true) + "'");
		        sbSql.append("\n     , " + dto.getNoCuenta() + ", '" + funciones.ponerFecha(dto.getFecOperacion()) + "', '" + funciones.ponerFecha(dto.getFecAlta()) + "' ");
		        
		        //sbSql.append("\n     , TO_DATE('" + funciones.ponerFecha(dto.getFecValor()) + "','dd/MM/yyyy HH24:mi:ss'), TO_DATE('" + funciones.ponerFecha(dto.getFecValorOriginal()) + "','dd/MM/yyyy HH24:mi:ss')");
		        //sbSql.append("\n     , " + dto.getNoCuenta() + ", TO_DATE('" + funciones.ponerFecha(dto.getFecOperacion()) + "','dd/MM/yyyy HH24:mi:ss'), TO_DATE('" + funciones.ponerFecha(dto.getFecAlta()) + "','dd/MM/yyyy HH24:mi:ss')");
//		    }

		    sbSql.append("\n, " + dto.getImporte());
	//	    
		    if(dto.getValorTasa() > 0)
		        sbSql.append("\n, " + dto.getValorTasa());
		    
		    sbSql.append("\n     , " + dto.getImporteOriginal() + ", " + dto.getTipoCambio() + ", " + dto.getIdCaja());
		    sbSql.append("\n     , '" + dto.getIdDivisa() + "', " + dto.getAplica() + ", '" + dto.getIdEstatusMov() + "'");
		    
		    if(dto.getOrigenReg() != null && !dto.getOrigenReg().equals(""))
		        sbSql.append("\n     , '" + dto.getOrigenReg() + "'");
		    else
		        sbSql.append("\n     , 'LIQ'");
		    
		    sbSql.append(", '" + dto.getBSalvoBuenCobro() + "', '" + dto.getIdEstatusReg() + "'");
		    
		    if(dto.getIdBancoBenef() > 0)
		    	sbSql.append("\n, " + dto.getIdBancoBenef());
		    
		    if(dto.getIdChequeraBenef() != null && !dto.getIdChequeraBenef().equals("")) 
		        sbSql.append("\n, '" + dto.getIdChequeraBenef() + "'");
		    
		    if(dto.getSecuencia() > 0)
		        sbSql.append("\n, " + dto.getSecuencia() + "");
		    
		    if(dto.getConcepto() != null && !dto.getConcepto().equals(""))
		        sbSql.append("\n, '" + dto.getConcepto() + "'");
		    
		   if(dto.getRubro() != null)
			   sbSql.append("\n   , "+ dto.getRubro());

		   if(dto.getIdGrupoInv() != null)
			   sbSql.append("\n   , "+ dto.getIdGrupoInv());
		   
		    sbSql.append("\n, '" + dto.getOrigenMov() + "'");
		    
		    sbSql.append("\n)");
		    System.out.println(sbSql.toString());
		    logger.info("insertarrrr parametro..::: \n" + sbSql.toString());
		    iRegAfec = jdbcTemplate.update(sbSql.toString());
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:insertarParametroLiquidacion");
			e.printStackTrace();
		}
		return iRegAfec;
	}
	
	/**
	 * Esteb metodo obtiene el numero de cuenta de la tabla empresa,
	 * es utilizado en LiquidacionDeInversiones
	 * FunSQLSelect572
	 * @param iNoEmpresa
	 * @return
	 */
	public int consultarNoCuentaEmpresa(int iNoEmpresa){
		StringBuffer sbSql = new StringBuffer();
		int noCuenta = 0;
		try{
		    sbSql.append("Select ");
		    sbSql.append("\n	no_cuenta_emp  ");
		    sbSql.append("\n	From ");
		    sbSql.append("\n	empresa ");
		    sbSql.append("\n	Where ");
		    sbSql.append("\n	no_empresa = " + iNoEmpresa);
		    noCuenta = jdbcTemplate.queryForInt(sbSql.toString());
		    logger.info("no cuenta: " + sbSql.toString());		
		    }catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarNoCuentaEmpresa");
		}
		return noCuenta;
	}
	
	/**
	 * Este metodo actualiza el estatus de la orden de inversion a 'X',
	 * es utilizado para cancelacion de ordenes en LiquidacionDeInversiones.js
	 * @param iNoOrden
	 * @return
	 */
	public int actualizarEstatusOrdenInversion(int iNoOrden){
		StringBuffer sbSql = new StringBuffer();
		int iRegAfec = 0;
		try{
		    sbSql.append("UPDATE orden_inversion ");
		    sbSql.append("\n	SET id_estatus_ord = 'X' ");
		    sbSql.append("\n	WHERE no_orden = '" + iNoOrden + "'");
		    iRegAfec = jdbcTemplate.update(sbSql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:actualizarEstatusOrdenInversion");
		}
		return iRegAfec;
	}

	public int actualizarEstatusOrdenInversion(int iNoOrden, String estatus){
		StringBuffer sbSql = new StringBuffer();
		int iRegAfec = 0;
		try{
		    sbSql.append("UPDATE orden_inversion ");
		    sbSql.append("\n	SET id_estatus_ord = '"+ estatus +"' ");
		    sbSql.append("\n	WHERE no_orden = '" + iNoOrden + "'");
		    iRegAfec = jdbcTemplate.update(sbSql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:actualizarEstatusOrdenInversion");
		}
		return iRegAfec;
	}

	public int actualizarOrdenInversion(int iNoOrden, OrdenInversionDto dto){
		StringBuffer sbSql = new StringBuffer();
		int iRegAfec = 0;
		try{
		    sbSql.append(" UPDATE  orden_inversion    ");
		    sbSql.append(" SET	id_estatus_ord = 'A',	 ");
		    sbSql.append("     id_banco =  "+ dto.getIdBanco() +",	 ");
		    sbSql.append("     id_chequera = '"+ dto.getIdChequera() +"',	 ");
		    sbSql.append("     id_banco_reg = "+ dto.getIdBancoReg() +",	 ");
		    sbSql.append("     id_chequera_reg = '"+ dto.getIdChequeraReg() +"' ");
		    sbSql.append(" WHERE  no_orden  = "+ iNoOrden);

		    iRegAfec = jdbcTemplate.update(sbSql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:actualizarEstatusOrdenInversion");
		}
		return iRegAfec;
	}

	
	//Inician llamadas a sql de VencimientoDeInversion.js
	
	/**
	 * Este metodo consulta las inversiones a vencer de movimiento, 
	 * es utilizado en VencimientoDeInversion.js
	 * FunSQLSelect933
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<MovimientoDto> consultarVencimientoInversion(ComunInversionesDto dto){
		List<MovimientoDto> listVenc = new ArrayList<MovimientoDto>();
		StringBuffer sbSql = new StringBuffer();
		
		try{
			
			sbSql.append("	SELECT A.fec_valor, A.id_banco, a.id_chequera, A.valor_tasa, A.importe,o.no_institucion,o.no_contacto, ");
			sbSql.append("\n	o.interes as interes, o.isr as isr, (A.importe + o.interes - o.isr) as totalImporte, A.id_cve_operacion, bb.id_cve_operacion as id_cve_operacionB, ");
			sbSql.append("\n	cc.id_cve_operacion as id_cve_operacionC, A.no_folio_det, bb.no_folio_det as no_folio_detB, cc.no_folio_det as no_folio_detC, ");
			sbSql.append("\n	A.folio_ref, A.id_divisa, A.no_cuenta, A.id_tipo_operacion, bb.id_tipo_operacion as id_tipo_operacionB, ");
			sbSql.append("\n	cc.id_tipo_operacion as id_tipo_operacionC, A.no_docto, A.dias_inv as plazo, A.no_empresa, ");
			sbSql.append("\n	o.interes as ajuste_int, o.isr as ajuste_isr, coalesce(o.nota, '') as nota");

			
			
		    
		    sbSql.append("\n 	FROM movimiento A, orden_inversion o, movimiento bb, movimiento cc, USUARIO_EMPRESA ue ");
//		    sbSql.append("\n 	WHERE  o.fec_venc = to_date('" + funciones.ponerFechaSola(dto.getFecValor()) + "', 'dd/MM/yyyy')");
		    sbSql.append("\n 	WHERE  A.id_tipo_operacion IN (4002) ");
		    sbSql.append("\n	And o.ID_ESTATUS_ORD != 'X' ");
		    //sbSql.append("\n    And A.id_banco = B.id_banco ");
		    sbSql.append("\n    And a.id_estatus_mov = 'D' ");
		    if(ConstantesSet.gsDBM.equals("ORACLE"))
		    	sbSql.append("\n    And to_number(a.no_docto) = o.no_orden ");
		    else
		    	sbSql.append("\n    And cast(a.no_docto as integer) = o.no_orden ");
		    sbSql.append("\n    And ue.no_usuario = "+ dto.getIdUsuario());
		    sbSql.append("\n    And a.no_empresa = ue.no_empresa ");
		    
		    if(!dto.isBInversionInterna()) 
		    {
		    	if(dto.getIdEmpresa() > 0)
		        	sbSql.append("\n	And A.no_empresa = " + dto.getIdEmpresa());
		        sbSql.append("\n	And A.origen_mov in ('INV', 'SET') ");
		    }
		    else
		        sbSql.append("\n	And A.origen_mov = 'IVT' ");
		    
//		    if(dto.getFecValor() != null && !dto.getFecValor().equals(""))
//		    {
//		    	if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE")){
		    		sbSql.append("\n	And A.no_folio_mov = BB.no_folio_mov ");
			    	sbSql.append("\n	And a.no_folio_mov = cc.no_folio_mov ");
			    	sbSql.append("\n	And bb.id_tipo_operacion = 4003 ");
			    	sbSql.append("\n	And cc.id_tipo_operacion = 4004 ");
//		    		sbSql.append("\n	And A.fec_valor = TO_DATE('" + funciones.ponerFechaSola(dto.getFecValor()) + "', 'dd/MM/yyyy')");
//		    	}
//		    	else if (ConstantesSet.gsDBM.equals("POSTGRESQL"))
//		    		sbSql.append("\n	And A.fec_valor = '" + dto.getFecValor() + "'");
//		    	else if(ConstantesSet.gsDBM.equals("DB2"))
//		    		sbSql.append("\n	And A.fec_valor = '" + funciones.ponerFechaSola(dto.getFecValor()) + "'");
//		    }
		        
		    if(dto.isBInversionInterna())
		    {
		        sbSql.append("\n    And A.no_docto in ");
		        sbSql.append("\n        ( Select no_orden ");
		        sbSql.append("\n          From orden_inversion ");
		        sbSql.append("\n          Where no_orden_ref = " + dto.getNoOrden() + " ) ");
		    }
		    
		    if(dto.getSrefmov() != null && !dto.getSrefmov().equals(""))
		    {
		    	
		    	sbSql.append("\n 	And A.no_docto in (select no_docto from movimiento where "); 
		    	if(dto.getIdEmpresa() > 0)
		    		sbSql.append("\n                no_empresa = " + dto.getIdEmpresa() );
	    		sbSql.append("\n                 and id_tipo_operacion between 4000 and 4099 and  ");
		        sbSql.append("\n                referencia='" + dto.getSrefmov() + "'");
		        sbSql.append("\n    Union all ");
		        sbSql.append("\n                select no_docto from hist_movimiento where ");
		    	if(dto.getIdEmpresa() > 0)
		    		sbSql.append("\n                no_empresa = " + dto.getIdEmpresa());
	    		sbSql.append("\n                and id_tipo_operacion between 4000 and 4099 and  ");
		        sbSql.append("\n                referencia='" + dto.getSrefmov() + "' )");
		    }
		    
		    sbSql.append("\n	ORDER BY A.fec_valor, A.no_docto, A.id_tipo_operacion ");
		    
		    //System.out.println(sbSql.toString());
		    
		    if(dto.isBInversionInterna())
		    {
		    	listVenc = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
			    	public MovimientoDto mapRow(ResultSet rs, int idx)throws SQLException{
			    		MovimientoDto dtoCons = new MovimientoDto();
			    			dtoCons.setFecValor(rs.getDate("fec_valor"));
			    			dtoCons.setsFecValor(funciones.ponerFecha(dtoCons.getFecValor()));
			    			dtoCons.setIdBanco(rs.getInt("id_banco"));
			    			dtoCons.setDescBancoBenef(rs.getString("id_banco"));
			    			dtoCons.setIdChequera(rs.getString("id_chequera"));
			    			dtoCons.setValorTasa(rs.getDouble("valor_tasa"));
			    			dtoCons.setImporte(rs.getDouble("importe"));
			    			dtoCons.setInteres(rs.getDouble("interes"));
			    			dtoCons.setIsr(rs.getDouble("isr"));
			    			dtoCons.setIdCveOperacion(rs.getInt("id_cve_operacion"));
			    			dtoCons.setCveOperacionB(rs.getInt("id_cve_operacionB"));
			    			dtoCons.setCveOperacionC(rs.getInt("id_cve_operacionC"));
			    			dtoCons.setNoFolioDet(rs.getInt("no_folio_det"));
			    			dtoCons.setFolioDetB(rs.getInt("no_folio_detB"));
			    			dtoCons.setFolioDetC(rs.getInt("no_folio_detC"));
			    			dtoCons.setFolioRef(rs.getInt("folio_ref"));
			    			dtoCons.setIdDivisa(rs.getString("id_divisa"));
			    			dtoCons.setNoCuenta(rs.getInt("no_cuenta"));
			    			dtoCons.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
			    			dtoCons.setTipoOperacionB(rs.getInt("id_tipo_operacionB"));
			    			dtoCons.setTipoOperacionC(rs.getInt("id_tipo_operacionC"));
			    			dtoCons.setNoDocto(rs.getString("no_docto"));
			    			dtoCons.setDiasInv(rs.getInt("plazo"));
			    			dtoCons.setNoEmpresa(rs.getInt("no_empresa"));
			    			dtoCons.setTotalImporte(rs.getDouble("totalImporte"));
			    			dtoCons.setInstFinan(rs.getString("no_institucion"));
			    			dtoCons.setContacto(rs.getString("no_contacto"));
			    			dtoCons.setInteresAnt(rs.getDouble("ajuste_int"));
			    			dtoCons.setImpuestoAnt(rs.getDouble("ajuste_isr"));
			    			dtoCons.setNota(rs.getString("nota"));
			    		return dtoCons;
			    	}
			    });
		    }
		    else
		    {
		    	listVenc = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
			    	public MovimientoDto mapRow(ResultSet rs, int idx)throws SQLException{
			    		MovimientoDto dtoCons = new MovimientoDto();
				    		dtoCons.setFecValor(rs.getDate("fec_valor"));
			    			dtoCons.setsFecValor(funciones.ponerFecha(dtoCons.getFecValor()));
			    			dtoCons.setIdBanco(rs.getInt("id_banco"));
			    			dtoCons.setDescBancoBenef(rs.getString("id_banco"));
			    			dtoCons.setIdChequera(rs.getString("id_chequera"));
			    			dtoCons.setValorTasa(rs.getDouble("valor_tasa"));
			    			dtoCons.setImporte(rs.getDouble("importe"));
			    			dtoCons.setInteres(rs.getDouble("interes"));
			    			dtoCons.setIsr(rs.getDouble("isr"));
			    			dtoCons.setIdCveOperacion(rs.getInt("id_cve_operacion"));
			    			dtoCons.setCveOperacionB(rs.getInt("id_cve_operacionB"));
			    			dtoCons.setCveOperacionC(rs.getInt("id_cve_operacionC"));
			    			dtoCons.setNoFolioDet(rs.getInt("no_folio_det"));
			    			dtoCons.setFolioDetB(rs.getInt("no_folio_detB"));
			    			dtoCons.setFolioDetC(rs.getInt("no_folio_detC"));
			    			dtoCons.setFolioRef(rs.getInt("folio_ref"));
			    			dtoCons.setIdDivisa(rs.getString("id_divisa"));
			    			dtoCons.setNoCuenta(rs.getInt("no_cuenta"));
			    			dtoCons.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
			    			dtoCons.setTipoOperacionB(rs.getInt("id_tipo_operacionB"));
			    			dtoCons.setTipoOperacionC(rs.getInt("id_tipo_operacionC"));
			    			dtoCons.setNoDocto(rs.getString("no_docto"));
			    			dtoCons.setDiasInv(rs.getInt("plazo"));
			    			dtoCons.setNoEmpresa(rs.getInt("no_empresa"));
			    			dtoCons.setTotalImporte(rs.getDouble("totalImporte"));
			    			dtoCons.setInstFinan(rs.getString("no_institucion"));
			    			dtoCons.setContacto(rs.getString("no_contacto"));
			    			dtoCons.setInteresAnt(rs.getDouble("ajuste_int"));
			    			dtoCons.setImpuestoAnt(rs.getDouble("ajuste_isr"));
			    			dtoCons.setNota(rs.getString("nota"));
			    		return dtoCons;
			    	}
			    });
		    }
		    logger.info("Query Venc : " + sbSql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarVencimientoInversion");
		}
		return listVenc;
	}
	
	/**
	 * Este metodo consulta el numero de orden, institucion y contacto, de orden de inversion,
	 * utilizado en VencimientoDeInversion.js
	 * FunSQLSelect937
	 * @param iNoOrden
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarContactoInstitucion(int iNoOrden){
		List<Map<String, Object>> mapConIns = null;
		StringBuffer sbSql = new StringBuffer();
		try{
			if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
			{
				sbSql.append("Select ");
		        sbSql.append("\n	A.no_orden, ");
		        sbSql.append("\n	B.nombre_corto as institucion, ");
		        sbSql.append("\n	C.paterno + ' ' + C.materno + ' ' + C.nombre as contacto");
			}
		    else
		    {
		    	sbSql.append("\n	Select ");
		        sbSql.append("\n	A.no_orden, ");
		        sbSql.append("\n	B.nombre_corto as institucion, ");
		        sbSql.append("\n	C.paterno || ' ' || C.materno || ' ' || C.nombre as contacto");
		        sbSql.append("\n	, A.no_contacto ");
		    }
		    
		    sbSql.append("\n	From ");
		    sbSql.append("\n	orden_inversion A, persona B, persona C ");
		    
		    sbSql.append("\n	Where ");
		    
		    sbSql.append("\n	A.no_orden = '" + iNoOrden + "'");
		    sbSql.append("\n	And B.no_persona = A.no_institucion ");
		    sbSql.append("\n 	And C.no_persona = A.no_contacto and C.id_tipo_persona = 'F' ");
		    
		    mapConIns = (List<Map<String, Object>>) jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public Map mapRow(ResultSet rs, int idx)throws SQLException{
		    		Map<String, Object> map = new HashMap<String, Object>();
		    			map.put("noOrden", rs.getInt("no_orden"));
		    			map.put("institucion", rs.getString("institucion"));
		    			map.put("contacto", rs.getString("contacto"));
		    		return map;
		    	}
		    });
		
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarContactoInstitucion");
		}
		return mapConIns;
	}
	
	/**
	 * Este metodo obtiene el nombre del contacto de la orden de inversion,
	 * es utilizado en VencimientoDeInversiones.js
	 * FunSQLSelect939
	 * @param iNoOrden
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarContactoOrden(int iNoOrden){
		List<LlenaComboGralDto> listContac = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
		    if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
		        sbSql.append("Select  C.no_persona, C.nombre + ' ' + C.paterno + ' ' + materno as contacto");
		    else
		        sbSql.append("Select  C.no_persona, C.nombre || ' ' || C.paterno || ' ' || materno as contacto");
		    
		    sbSql.append("\n	From orden_inversion A, relacion B, persona C ");
		    sbSql.append("\n	Where ");
		    sbSql.append("\n	A.no_orden = '" + iNoOrden + "'");
		    sbSql.append("\n 	And B.no_persona = A.no_institucion ");
		    sbSql.append("\n 	And C.no_persona = B.no_persona_rel  ");
		    sbSql.append("\n 	And C.no_empresa  = 1 ");
		    
		    listContac = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		LlenaComboGralDto dtoCons = new LlenaComboGralDto();
		    			dtoCons.setDescripcion(rs.getString("contacto"));
		    		return dtoCons;
		    	}
		    });
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarContactoOrden");
		}
		return listContac;
	}
	
	/**
	 * Este metodo consulta los dias_anual de orden de inversion,
	 * es utilizado en VencimientoDeInversion.js
	 * FunSQLSelectDias
	 * @param dFechaVen
	 * @param iBanco
	 * @param sChequera
	 * @param uTasa
	 * @param iNoCuenta
	 * @param iNoDocto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<OrdenInversionDto> consultarDiasAnual(String dFechaVen, int iBanco, String sChequera, double uTasa, int iNoCuenta, int iNoDocto){
		List<OrdenInversionDto> listOrd = new ArrayList<OrdenInversionDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
		    sbSql.append("\n   select dias_anual, no_institucion, ");
		    sbSql.append("\n   (select distinct c.contrato_institucion from cuenta c, movimiento m where ");
		    sbSql.append("\n   c.no_cuenta = '" + iNoCuenta + "' and m.no_docto = '" + iNoDocto + "' and c.no_cuenta = m.no_cuenta and c.id_tipo_cuenta = 'C') as contrato from orden_inversion ");
		    sbSql.append("\n   where "); //fec_venc = '" + dFechaVen + "'");
		    sbSql.append("\n   id_banco = " + iBanco + "");
		    sbSql.append("\n   and id_chequera = '" + sChequera + "'");
		    sbSql.append("\n   and no_orden = '" + iNoDocto + "'");
		    sbSql.append("\n   and no_cuenta = '" + iNoCuenta + "'");
		    logger.info("borrar diasAnual : " + sbSql.toString());
		    
		    //System.out.println("dias Anual : " + sbSql.toString());
		    
		    listOrd = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public OrdenInversionDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		OrdenInversionDto dtoCons = new OrdenInversionDto();
		    			dtoCons.setDiasAnual(rs.getInt("dias_anual"));
		    			dtoCons.setNoInstitucion(rs.getInt("no_institucion"));
		    			dtoCons.setContrato(rs.getString("contrato"));
		    		return dtoCons;
		    	}
		    });
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarDiasAnual");
		}
		return listOrd;
	}
	
	/**
	 * Este metodo actualiza el valor de la tasa en orden_inversion,
	 * es utilizado en VencimientoDeInversion
	 * FunSQLUpdateTasa
	 * @param iEmpresa
	 * @param uTasa
	 * @param sNoOrden
	 * @param sFecha
	 * @return
	 */
	public int actualizarTasa(int iEmpresa, double uTasa, String sNoOrden, String sFecha){
		StringBuffer sbSql = new StringBuffer();
		int iRegAfec = 0;
		try{
		    sbSql.append("UPDATE orden_inversion ");
		    sbSql.append("\n SET tasa = '" + uTasa + "'");
		    sbSql.append("\n WHERE no_orden = '" + sNoOrden + "'");
		    sbSql.append("\n and no_empresa = " + iEmpresa + "");
		    //sbSql.append("\n and fec_venc = '" + sFecha + "'");
		    
		    //System.out.println("update a orden: " + sbSql);
		    
		    iRegAfec = jdbcTemplate.update(sbSql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:actualizarTasa");
		}
		return iRegAfec;
	}
	
	/**
	 * Este metodo obtiene la cveOperacion contraria de cat_cve_operacion,
	 * es utilizado en VencimientoDeInversion
	 * FunSQLSelect936
	 * @param iCveOperacion
	 * @return
	 */
	public int consultarCveOperacionContraria(int iCveOperacion){
		int iCveContraria = 0;
		StringBuffer sbSql = new StringBuffer();
		try{
		    sbSql.append("Select ");
		    sbSql.append("\n	id_cve_contraria ");
		    sbSql.append("\n	From ");
		    sbSql.append("\n	cat_cve_operacion ");
		    sbSql.append("\n	Where ");
		    sbSql.append("\n	id_cve_operacion = " + iCveOperacion);
		    
		    iCveContraria = jdbcTemplate.queryForInt(sbSql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarCveOperacionContraria");
		}
		return iCveContraria;
	}
	
	/**
	 * Este metodo inserta en parametro, es utilizado en 
	 * VencimientoDeInversion
	 * FunSQLInsert248
	 * @param dto
	 * @return
	 */
	public int insertarParametroVencInv(ParametroDto dto){
		int iRegAfec = 0;
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("INSERT INTO parametro (");
		    sbSql.append("\n     origen_reg, no_folio_param, no_empresa, id_banco, id_chequera, ");
		    sbSql.append("\n     id_banco_benef, ");
		    if(dto.getIdChequeraBenef() != null && !dto.getIdChequeraBenef().equals(""))
		    	sbSql.append("id_chequera_benef,"); 
		    sbSql.append("\n	 id_tipo_operacion, id_forma_pago, ");
		    sbSql.append("\n     usuario_alta, grupo, no_folio_mov, folio_ref, fec_valor, ");                 
		    sbSql.append("\n     fec_valor_original, no_cuenta, fec_operacion, fec_alta, importe, ");
		    sbSql.append("\n     valor_tasa, importe_original, tipo_cambio, id_caja, id_divisa, ");
		    sbSql.append("\n     aplica, concepto, no_docto, id_estatus_mov, b_salvo_buen_cobro, ");
		    sbSql.append("\n     id_estatus_reg, origen_mov ");
		    
		    if(dto.getNoCliente() != null && !dto.getNoCliente().equals(""))
		        sbSql.append("\n	,no_cliente, beneficiario ");
		    
		    if(dto.getSecuencia() > 0)
		        sbSql.append("\n	,secuencia ");
		    
		    if(dto.getIdTipoOperacion() == 3811)
		        sbSql.append("\n,	importe_desglosado ");

		   if(dto.getRubro() != null)
			   sbSql.append("\n     , ID_RUBRO ");

		   if(dto.getIdGrupoInv() != null)
			   sbSql.append("\n     , ID_GRUPO ");

		   sbSql.append("\n ) Values ('VEN'," + dto.getNoFolioParam() + ", " + dto.getNoEmpresa() + ", " + dto.getIdBanco());
		   sbSql.append("\n     , '" + dto.getIdChequera() + "'" + ", " + dto.getIdBancoBenef());
		    
		    if(dto.getIdChequeraBenef() != null && !dto.getIdChequeraBenef().equals(""))
		        sbSql.append("\n, '" + dto.getIdChequeraBenef()+ "'");
		    
		    sbSql.append("\n, " + dto.getIdTipoOperacion() + ", " + dto.getIdFormaPago() + ", " + dto.getUsuarioAlta() + ", " + dto.getIdGrupo());
		    
		    /*if(ConstantesSet.gsDBM.equals("DB2"))
		    {
		    	sbSql.append("\n, " + dto.getNoFolioMov() + ", " + dto.getFolioRef() + ", '" + funciones.ponerFecha(dto.getFecValor()) + "'");
		        sbSql.append("\n, '" + funciones.ponerFecha(dto.getFecValorOriginal()) + "'" + ", " + dto.getNoCuenta() + ", '" + funciones.ponerFecha(dto.getFecOperacion()) + "'");
		        sbSql.append("\n, '" + funciones.ponerFecha(dto.getFecAlta()) + "'" + ", " + dto.getImporte() + ", " + dto.getValorTasa());
		    }
		    else
		    {*/
		    	sbSql.append("\n, " + dto.getNoFolioMov() + ", " + dto.getFolioRef() + ", '" + funciones.ponerFecha(dto.getFecValor()) + "'");
		        sbSql.append("\n, '" + funciones.ponerFecha(dto.getFecValorOriginal()) + "'" + ", " + dto.getNoCuenta() + ", '" + funciones.ponerFecha(dto.getFecOperacion()) + "'");
		        sbSql.append("\n, '" + funciones.ponerFecha(dto.getFecAlta()) + "'" + ", " + dto.getImporte() + ", " + dto.getValorTasa());
		        
		        //sbSql.append("\n, " + dto.getNoFolioMov() + ", " + dto.getFolioRef() + ", TO_DATE('" + funciones.ponerFecha(dto.getFecValor()) + "', 'dd/MM/yyyy HH24:mi:ss')");
		        //sbSql.append("\n, TO_DATE('" + funciones.ponerFecha(dto.getFecValorOriginal()) + "', 'dd/MM/yyyy HH24:mi:ss')" + ", " + dto.getNoCuenta() + ", TO_DATE('" + funciones.ponerFecha(dto.getFecOperacion()) + "', 'dd/MM/yyyy HH24:mi:ss')");
		        //sbSql.append("\n, TO_DATE('" + funciones.ponerFecha(dto.getFecAlta()) + "', 'dd/MM/yyyy HH24:mi:ss')" + ", " + dto.getImporte() + ", " + dto.getValorTasa());
//		    }
		    
		    sbSql.append("\n, " + dto.getImporteOriginal() + ", " + dto.getTipoCambio());
		    sbSql.append("\n, " + dto.getIdCaja() + ", '" + dto.getIdDivisa() + "'");
		    sbSql.append("\n, " + dto.getAplica() + ", '" + dto.getConcepto() + "'" + ", '" + dto.getNoDocto() + "'");
		    sbSql.append("\n, '" + dto.getIdEstatusMov() + "'" + ", '" + dto.getBSalvoBuenCobro() + "'");
		    sbSql.append("\n, '" + dto.getIdEstatusReg() + "'" + ", '" + dto.getOrigenMov() + "'");
		    
		    if(dto.getNoCliente() != null && !dto.getNoCliente().equals(""))
		        sbSql.append("\n, " + dto.getNoCliente() + " , '" + dto.getBeneficiario() + "' ");
		    
		    if(dto.getSecuencia() > 0)
		        sbSql.append("\n, " + dto.getSecuencia());
		    
		    if(dto.getIdTipoOperacion() == 3811)
		        sbSql.append("\n, " + dto.getImporte());//Cambiar atributo
		    
		   if(dto.getRubro() != null)
			   sbSql.append("\n     , "+ dto.getRubro());

		   if(dto.getIdGrupoInv() != null)
			   sbSql.append("\n     , "+ dto.getIdGrupoInv());

		    sbSql.append("\n )");
		    
		    iRegAfec = jdbcTemplate.update(sbSql.toString());
		           
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:insertarParametroVencInv");
			return -1;
		}
		return iRegAfec;
	}
	
	/**
	 * Este metodo consulta el banco y chequera de regreso de orden_inversion,
	 * es utilizado en VencimientoDeInversion
	 * FunSQLSelect935
	 * @param sCampo
	 * @param iNoOrden
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ComunInversionesDto> consultarBancoCheqRegreso(int iNoOrden){
		List<ComunInversionesDto> listBanChe = new ArrayList<ComunInversionesDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
		    sbSql.append("SELECT id_banco_reg, id_chequera_reg");
		    sbSql.append("\n FROM orden_inversion ");
		    sbSql.append("\n WHERE no_orden = '" + iNoOrden + "'");

		    listBanChe = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public ComunInversionesDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		ComunInversionesDto dtoCons = new ComunInversionesDto();
		    			dtoCons.setIdBanco(rs.getInt("id_banco_reg"));
		    			dtoCons.setIdChequera(rs.getString("id_chequera_reg"));
		    		return dtoCons;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarBancoCheqRegreso");
		}
		return listBanChe;
	}
	
	/**
	 * Este metodo obtiene el id de la empresa y su descripcion,
	 * es utilizado en VencimientoDeInversion para cuando la inversion sea interna
	 * FunSQLSelectDatosEmp
	 * @param iIdBanco
	 * @param sIdChequera
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ComunInversionesDto> consultarEmpresaInterna(int iIdBanco, String sIdChequera){
		List<ComunInversionesDto> listBanChe = new ArrayList<ComunInversionesDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
		    sbSql.append("SELECT c.no_empresa, e.nom_empresa ");
		    sbSql.append("\n FROM cat_cta_banco c, empresa e ");
		    sbSql.append("\n WHERE c.no_empresa = e.no_empresa ");
		    sbSql.append("\n     AND c.id_banco = " + iIdBanco);
		    sbSql.append("\n     AND c.id_chequera = '" + sIdChequera + "' ");
		    
			listBanChe = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
				public ComunInversionesDto mapRow(ResultSet rs, int idx)throws SQLException{
					ComunInversionesDto dtoCons = new ComunInversionesDto();
						dtoCons.setIdEmpresa(rs.getInt("no_empresa"));
						dtoCons.setDescEmpresa(rs.getString("nom_empresa"));
					return dtoCons;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarEmpresaInterna");
		}
		return listBanChe;
	}
	
	/**
	 * Este metodo obtiene el estatus de la orden de inversion,
	 * es utilizado en VencimientoDeInversion
	 * FunSQLSelect932
	 * @param iNoOrden
	 * @return
	 */
	public String consultarEstatusOrden(int iNoOrden){
		String sIdEstatusOrd = "";
		
		
		
		StringBuffer sbSql = new StringBuffer();
		try{
		    sbSql.append("Select id_estatus_ord ");
		    sbSql.append("\n	From orden_inversion ");
		    sbSql.append("\n	where no_orden = '" + iNoOrden + "'");
		    
		    sIdEstatusOrd = jdbcTemplate.queryForObject(sbSql.toString(), String.class);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarEstatusOrden");
		}
		return sIdEstatusOrd;
	}
	
	/**
	 * FunSQLInsertReinversion
	 * @param dto
	 * @return
	 */
	public int insertarOrdenReinversion(OrdenInversionDto dto){
		StringBuffer sbSql = new StringBuffer();
		int iRegAfec = 0;
		try{
		    sbSql.append("\n INSERT INTO orden_inversion ");
		    sbSql.append("\n     ( no_empresa, no_orden, id_papel, id_tipo_valor, ");
		    sbSql.append("\n       no_cuenta, no_institucion, no_contacto, fec_venc, ");
		    sbSql.append("\n       hora, minuto, importe, plazo, isr, usuario_alta, ");
		    sbSql.append("\n       fec_alta, usuario_modif, id_estatus_ord, interes, ");
		    sbSql.append("\n       dias_anual, no_contacto_v, hora_v, minuto_v, ");
		    sbSql.append("\n       ajuste_int, nota, ajuste_isr, tasa_isr, tasa, ");
		    sbSql.append("\n       importe_traspaso, traspaso_ejecutado, id_banco, ");
		    sbSql.append("\n       id_chequera, tasa_curva28, id_banco_reg, ");
		    sbSql.append("\n       id_chequera_reg, b_garantia, no_orden_ref, ");
		    sbSql.append("\n       b_autoriza, tipo_orden ) ");
		    sbSql.append("\n SELECT no_empresa, " + dto.getNoOrdenNva() + " as no_orden, id_papel, ");
		    sbSql.append("\n       id_tipo_valor, ");
		    sbSql.append("\n       no_cuenta, no_institucion, no_contacto, ");
		    sbSql.append("\n       '" + funciones.ponerFecha(dto.getFecVenc()) + "' as fec_venc, ");
		    
		    if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
		    {
		    	sbSql.append("\n   date_part('hour', now()) as hora, ");
		        sbSql.append("\n   date_part('minute', now()) as minuto, ");
		    }
		    else if(ConstantesSet.gsDBM.equals("SYBASE"))
		    {
		    	sbSql.append("\n   datepart(hh, getdate()) as hora, ");
		        sbSql.append("\n   datepart(minute, getdate()) as minuto, ");
		    }
		    else if(ConstantesSet.gsDBM.equals("DB2"))
		    {
		    	sbSql.append("\n   hour(now()) as hora, ");
		        sbSql.append("\n   minute(now()) as minuto, ");
		    }
		    else
		    {
		    	sbSql.append("\n   datepart(hh, getdate()) as hora, ");
		        sbSql.append("\n   datepart(n, getdate()) as minuto, ");
		    }
		    
		    sbSql.append("\n       (importe + interes - isr) as importe_reinversion, ");
		    sbSql.append("\n       " + dto.getPlazo() + " as plazo, ");
		    sbSql.append("\n       (importe + interes - isr) * (tasa_isr/100/360) * " + dto.getPlazo() + " as isr, ");
		    sbSql.append("\n       " + dto.getUsuarioAlta() + " as usuario_alta, ");
		    sbSql.append("\n       '" + funciones.ponerFecha(dto.getFecAlta()) + "' as fec_alta, ");
		    sbSql.append("\n       " + funciones.ponerFecha(dto.getFecAlta()) + " as usuario_modif, ");
		    sbSql.append("\n       'C' as id_estatus_ord, ");
		    sbSql.append("\n       (importe + interes - isr) * (" + dto.getTasa() + "/100/360) * " + dto.getPlazo() + " as interes, ");
		    sbSql.append("\n       dias_anual, no_contacto_v, ");
		    
		    if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
		    {
		    	sbSql.append("\n   date_part('hour', now()) as hora_v, ");
		        sbSql.append("\n   date_part('minute', now()) as minuto_v, ");
		    }
		    else if(ConstantesSet.gsDBM.equals("DB2"))
		    {
		    	sbSql.append("\n   hour(now()) as hora_v, ");
	            sbSql.append("\n   minute(now()) as minuto_v, ");
		    }
		    else
		    {
		    	sbSql.append("\n   datepart(hh, getdate()) as hora_v, ");
	            sbSql.append("\n   datepart(n, getdate()) as minuto_v, ");
		    }
		    
		    sbSql.append("\n       0 as ajuste_int, ");
		    sbSql.append("\n       nota, 0 as ajuste_isr, tasa_isr, " + dto.getTasa() + " as tasa, ");
		    sbSql.append("\n       0 as importe_traspaso, 0 as traspaso_ejecutado, id_banco, ");
		    sbSql.append("\n       id_chequera, " + dto.getTasaCurva28() + " as tasa_curva28, ");
		    sbSql.append("\n       id_banco_reg, id_chequera_reg, b_garantia, no_orden_ref, ");
		    sbSql.append("\n       null As b_autoriza, 'E' as tipo_orden ");
		    sbSql.append("\n FROM orden_inversion ");
		    sbSql.append("\n WHERE no_orden = " + dto.getNoOrden());//Checar orden con ordenNva
		    sbSql.append("\n       AND no_empresa = " + dto.getNoEmpresa());
		    
		    iRegAfec = jdbcTemplate.update(sbSql.toString());
		
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:insertarReinversion");
		}
		return iRegAfec;
	}
	
	/**
	 * Este metodo inserta en parametro, es utilizado en 
	 * VencimientoDeInversion
	 * FunSQLInsert189
	 * @param dto
	 * @return
	 */
	public int insertarParametroReinversion(ParametroDto dto){
		StringBuffer sbSql = new StringBuffer();
		int iRegAfec = 0;
		try{

		    sbSql.append("INSERT INTO parametro (");
		    sbSql.append("\n     no_folio_param, no_empresa, id_tipo_operacion, id_forma_pago, ");
		    sbSql.append("\n     dias_inv, usuario_alta, id_tipo_docto, no_docto, grupo, ");
		    sbSql.append("\n     no_folio_mov, folio_ref, fec_valor, fec_valor_original, ");   
		    sbSql.append("\n     no_cuenta, fec_operacion, fec_alta, importe, valor_tasa, ");
		    sbSql.append("\n     importe_original, tipo_cambio, id_caja, id_divisa, aplica, ");
		    sbSql.append("\n     id_estatus_mov, b_salvo_buen_cobro, id_estatus_reg, ");
		
		    if(dto.getIdBancoBenef() > 0)  
		        sbSql.append("\n	id_banco_benef, ");
		    
		    if(dto.getIdChequeraBenef() != null && !dto.getIdChequeraBenef().equals(""))
		        sbSql.append("\n	id_chequera_benef, ");
		    
		    if(dto.getSecuencia() > 0)
		        sbSql.append("\n	secuencia, ");
		    
		    if(dto.getConcepto() != null && !dto.getConcepto().equals(""))
		        sbSql.append("\n	concepto, ");
		        
		    sbSql.append("\n     origen_mov ");
		    
		    if(dto.getReferencia() != null && !dto.getReferencia().equals(""))
		        sbSql.append("\n     , referencia ");
		    
		    sbSql.append("\n     ) ");
		    
		    sbSql.append("\n VALUES ( ");
		    sbSql.append("\n     " + dto.getNoFolioParam() + ", " + dto.getNoEmpresa() + ", " + dto.getIdTipoOperacion());
		    sbSql.append("\n     , " + dto.getIdFormaPago() + ", " + dto.getDiasInv() + ", " + dto.getUsuarioAlta());
		    sbSql.append("\n     , " + dto.getIdTipoDocto() + ", '" + dto.getNoDocto() + "', " + dto.getGrupo());
		    sbSql.append("\n     , " + dto.getNoFolioMov() + ", " + dto.getFolioRef());

		    if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
		    {
		    	sbSql.append("\n, convert(datetime,'" + funciones.ponerFecha(dto.getFecValor()) + "')");
	            sbSql.append("\n, convert(datetime,'" + funciones.ponerFecha(dto.getFecValorOriginal()) + "')");
	            sbSql.append("\n, " + dto.getNoCuenta());
	            sbSql.append("\n, convert(datetime,'" + funciones.ponerFecha(dto.getFecOperacion()) + "')");
	            sbSql.append("\n, convert(datetime,'" + funciones.ponerFecha(dto.getFecAlta()) + "')");
		    }
		    if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
		    {
		    	sbSql.append("\n, to_date('" + dto.getFecValor() + "','dd/mm/yyyy')");
	            sbSql.append("\n, to_date('" + dto.getFecValorOriginal() + "','dd/mm/yyyy')");
	            sbSql.append("\n, " + dto.getNoCuenta());
	            sbSql.append("\n, to_date('" + dto.getFecOperacion() + "','dd/mm/yyyy')");
	            sbSql.append("\n, to_date('" + dto.getFecAlta() + "','dd/mm/yyyy')");
		    }
		    if(ConstantesSet.gsDBM.equals("DB2"))
		    {
		    	sbSql.append("\n, cast('" + funciones.ponerFecha(dto.getFecValor()) + "' as date)");
	            sbSql.append("\n, cast('" + funciones.ponerFecha(dto.getFecValorOriginal()) + "' as date)");
	            sbSql.append("\n, " + dto.getNoCuenta());
	            sbSql.append("\n, cast('" + funciones.ponerFecha(dto.getFecOperacion()) + "' as date)");
	            sbSql.append("\n, cast('" + funciones.ponerFecha(dto.getFecAlta()) + "' as date)");
		    }
		  
		    sbSql.append("\n     , " + dto.getImporte() + ", " + dto.getValorTasa() + ", " + dto.getImporteOriginal());
		    sbSql.append("\n     , " + dto.getTipoCambio() + ", " + dto.getIdCaja() + ", '" + dto.getIdDivisa() + "'");
		    sbSql.append("\n     , " + dto.getAplica() + ", '" + dto.getIdEstatusMov() + "'");
		    sbSql.append("\n     , '" + dto.getBSalvoBuenCobro() + "', '" + dto.getIdEstatusReg() + "'");
		    
		    if(dto.getIdBancoBenef() > 0)
		        sbSql.append("\n, " + dto.getIdBancoBenef());
		    
		    if(dto.getIdChequeraBenef() != null && !dto.getIdChequeraBenef().equals(""))
		        sbSql.append("\n, '" + dto.getIdChequeraBenef() + "'");
		    
		    if(dto.getSecuencia() > 0)
		        sbSql.append("\n, " + dto.getSecuencia() + "");
		    
		    if(dto.getConcepto() != null && !dto.getConcepto().equals(""))
		        sbSql.append("\n, '" + dto.getConcepto() + "'");
		    
		    sbSql.append("\n, '" + dto.getOrigenMov() + "'");
		    
		    if(dto.getReferencia() != null && !dto.getReferencia().equals(""))
		        sbSql.append("\n     ,'" + dto.getReferencia() + "' ");
		    
		    sbSql.append("\n )");
		    
		    iRegAfec = jdbcTemplate.update(sbSql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:insertarParametroReinversion");
		}
		return iRegAfec;
	}
	
	/**
	 * Este metodo consulta trapasos de movimiento,
	 * utilizado en el reporte de LiquidacionDeInversion
	 * FunSQLSelect576, FunSQLSelect989
	 * @param iNoDocto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarTraspasos(int iIdTipoOperacion, String sIdTipoMovto, Map<String, Object> parametros){
		StringBuffer sbSql = new StringBuffer();
		List<Map<String, Object>> listTras = null;
		try{
		    sbSql.append("SELECT desc_banco, id_chequera, importe as importe ");
		    sbSql.append("\n	FROM movimiento m, cat_banco c ");
		    sbSql.append("\n 	WHERE m.id_banco = c.id_banco ");
		    sbSql.append("\n    And id_tipo_operacion in ("+ iIdTipoOperacion +", 3810, 3811) ");
		    sbSql.append("\n    And no_docto = '" + parametros.get("noDocto").toString() + "'");
		    sbSql.append("\n    And m.id_tipo_movto = '"+ sIdTipoMovto + "' ");
		    logger.info("Borrar Query traspasos " + sbSql.toString());
		    
		    listTras = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public Map<String, Object> mapRow(ResultSet rs, int idx)throws SQLException{
		    		Map<String, Object> mapCons = new HashMap<String, Object>();
		    			mapCons.put("desc_banco", rs.getString("desc_banco"));
		    			mapCons.put("id_chequera", rs.getString("id_chequera"));
		    			mapCons.put("importe", rs.getDouble("importe"));
		    		return mapCons;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarTraspasos");
			e.printStackTrace();
		}
		return listTras;
	}
	
	/**
	 * FunSQLSelect577
	 * @param parametros
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarOrdenInversionReport(Map parametros){
		StringBuffer sbSql = new StringBuffer();
		List<Map<String, Object>> listOrd = null;
		try
		{
		    sbSql.append("\n SELECT desc_banco, id_chequera, importe ");
		    sbSql.append("\n FROM movimiento m, cat_banco c ");
		    sbSql.append("\n WHERE m.id_banco = c.id_banco ");
		    sbSql.append("\n     and id_tipo_operacion = 4001 ");
		    sbSql.append("\n     and no_docto = '" + parametros.get("noDocto").toString() + "'");

		    listOrd = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public Map mapRow(ResultSet rs, int idx)throws SQLException{
		    		Map<String, Object> mapCons = new HashMap<String, Object>();
		    			mapCons.put("desc_banco", rs.getString("desc_banco"));
		    			mapCons.put("id_chequera", rs.getString("id_chequera"));
		    			mapCons.put("importe", rs.getDouble("importe"));
		    		return mapCons;
		    	}
		    });
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarOrdenInversionReport");
		}
		return listOrd;
	}
	
	/**
	 * FunSQLSelect577
	 * @param parametros
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> obtenerReporteLiquidacionInversion(Map parametros){
		StringBuffer sbSql = new StringBuffer();
		List<Map<String, Object>> listOrd = null;
		try
		{
			System.out.println("..:en ejecutar el qry:");
			
		    sbSql.append("\n select e.NOM_EMPRESA, i.NO_ORDEN, ");
		    if(ConstantesSet.gsDBM.equals("ORACLE"))
		    	sbSql.append("\n to_char(m.FEC_VALOR, 'dd/MM/yyyy') as FEC_VALOR, m.id_tipo_operacion, ");
		    else
		    	sbSql.append("\n CONVERT(varchar(10), m.FEC_VALOR, 103) as FEC_VALOR, m.id_tipo_operacion, ");
		    sbSql.append("\n c.CONTRATO_INSTITUCION, c.DESC_CUENTA, ");
		    sbSql.append("\n i.IMPORTE, i.DIVISA,  ");
		    sbSql.append("\n bc.DESC_BANCO as BANCO_CARGO, i.ID_CHEQUERA,  ");
		    sbSql.append("\n br.DESC_BANCO as BANCO_REGRESO, i.ID_CHEQUERA_REG, ");
		    sbSql.append("\n coalesce(bi.DESC_BANCO, ' ') as BANCO_LIQ, i.ID_CHEQUERA_BENEF, ");
		    sbSql.append("\n coalesce(i.ID_CHEQUERA_BENEF, ' ') as ID_CHEQUERA_BENEF ");
		    sbSql.append("\n , '" + parametros.get("nomreUsuario").toString() +"' as USUARIO");
		    sbSql.append("\n from ORDEN_INVERSION i join ");
		    sbSql.append("\n EMPRESA e on (i.NO_EMPRESA = e.NO_EMPRESA) ");
		    sbSql.append("\n join MOVIMIENTO m on (to_number(i.NO_ORDEN) = m.NO_DOCTO ");
		    sbSql.append("\n   AND m.ID_TIPO_OPERACION = 4000) ");
		    sbSql.append("\n join CUENTA c on (i.NO_CUENTA = c.NO_CUENTA ");
		    sbSql.append("\n   AND m.NO_EMPRESA = c.NO_EMPRESA ");
		    sbSql.append("\n   AND c.ID_TIPO_CUENTA = 'C') ");
		    sbSql.append("\n join CAT_BANCO bc on (i.ID_BANCO = bc.ID_BANCO) ");
		    sbSql.append("\n join CAT_BANCO br on (i.ID_BANCO_REG = br.ID_BANCO) ");
		    sbSql.append("\n left join CAT_BANCO bi on (i.ID_BANCO_BENEF = bi.ID_BANCO) ");
		    sbSql.append("\n where i.NO_ORDEN in ( "+ parametros.get("noDocto").toString() + ")");
		    
		    System.out.println("El query para el REPORTE es "+ sbSql.toString());

		    listOrd = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public Map mapRow(ResultSet rs, int idx)throws SQLException{
		    		Map<String, Object> mapCons = new HashMap<String, Object>();
		    			mapCons.put("nomEmpresa", rs.getString("NOM_EMPRESA"));
		    			mapCons.put("fechaLiquidacion", rs.getString("FEC_VALOR"));
		    			mapCons.put("noOrden", rs.getString("NO_ORDEN"));
		    			mapCons.put("noContrato", rs.getString("CONTRATO_INSTITUCION"));
		    			mapCons.put("descContrato", rs.getString("DESC_CUENTA"));
		    			mapCons.put("importeP", funciones.obtenerFormatoPesos(rs.getDouble("IMPORTE")));
		    			mapCons.put("idDivisa", rs.getString("DIVISA"));
		    			mapCons.put("idBancoLiquida", rs.getString("BANCO_LIQ"));
		    			mapCons.put("idChequeraLiquida", rs.getString("ID_CHEQUERA_BENEF"));

		    			mapCons.put("idBancoRegreso", rs.getString("BANCO_REGRESO"));
		    			mapCons.put("idChequeraRegreso", rs.getString("ID_CHEQUERA_REG"));
		    			
		    			mapCons.put("desc_banco", rs.getString("BANCO_CARGO"));
		    			mapCons.put("id_chequera", rs.getString("ID_CHEQUERA"));
		    			mapCons.put("nomUsuario", rs.getString("USUARIO"));

		    			return mapCons;
		    	}
		    });
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarOrdenInversionReport");
		}
		
		//System.out.println("El tamaño de la lista es "+listOrd.size());
		return listOrd;
	}

	
	/**
	 * FunSQLSelect578
	 * @param parametros
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarSolicitudPago(Map parametros){
		List<Map<String, Object>> listSol = null;
		StringBuffer sbSql = new StringBuffer();
		try{
		    sbSql.append("    SELECT desc_banco, id_chequera, importe ");
		    sbSql.append("\n  FROM movimiento m, cat_banco c ");
		    sbSql.append("\n  WHERE m.id_banco=c.id_banco ");
		    sbSql.append("\n      and id_tipo_operacion = 3000 ");
		    sbSql.append("\n      and no_docto = '" + parametros.get("noDocto").toString() + "'");
		    
		    listSol = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public Map mapRow(ResultSet rs, int idx)throws SQLException{
		    		Map<String, Object> mapCons = new HashMap<String, Object>();
		    			mapCons.put("desc_banco", rs.getString("desc_banco"));
		    			mapCons.put("id_chequera", rs.getString("id_chequera"));
		    			mapCons.put("importe", rs.getDouble("importe"));
		    		return mapCons;
		    	}
		    });
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarOrdenInversionReport");
		}
		return listSol;
	}
	
	//Inician metodos de consulta para la forma ReportesDeInversiones.js
	
	/**
	 * Este metodo es utilizado para realizar un reporte cuyo nombre es
	 * Inversiones Establecidas Hoy, utilizado en ReportesDeInversiones.js,
	 * sin embargo puede ser utilizado para otra funcion.(FunSQLSelect992)
	 * @param dto
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarInvEstHoy(ParamReportesDto dto)
	{
		List<Map<String, Object>> listInvHoy = new ArrayList<Map<String, Object>>();
		StringBuffer sbSql = new StringBuffer();
		try
		{
		    sbSql.append("Select distinct ");
		    sbSql.append("\n oi.fec_venc, oi.no_orden, oi.no_cuenta,");
		    sbSql.append("\n id_papel, ce.desc_estatus, c.id_divisa, ");
		    sbSql.append("\n id_tipo_valor, coalesce(oi.id_chequera,'')as id_chequera, ");
		    sbSql.append("\n coalesce(oi.id_banco,0) as id_banco, ");
		    sbSql.append("\n coalesce(cb.desc_banco,'') as desc_banco, ");
		    
//		    if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
//		    {
//		    	sbSql.append("\n p.razon_social, pp.paterno + ' ' + pp.materno + ' ' + pp.nombre as contacto, ");
//	            sbSql.append("\n oi.importe+interes-isr as total, oi.plazo, oi.tasa, ");
//	            sbSql.append("\n convert(varchar,oi.hora) + ':' + convert(varchar,oi.minuto) as hora, ");
//	            sbSql.append("\n id_estatus_ord , oi.importe, interes, isr ");
//	            sbSql.append("\n FROM ");
//	            sbSql.append("\n orden_inversion oi, persona p, persona pp, ");
//	            sbSql.append("\n cat_estatus ce, cat_banco cb, cuenta c ");
//	            sbSql.append("\n Where ");
//	            sbSql.append("\n oi.no_institucion = p.no_persona ");
//	            sbSql.append("\n and p.id_tipo_persona ='B'");
//	            sbSql.append("\n and oi.tipo_orden = 'E' ");
//	            sbSql.append("\n and oi.no_empresa = c.no_empresa ");
//	            sbSql.append("\n and oi.no_empresa = c.no_persona ");
//	            sbSql.append("\n and oi.no_cuenta = c.no_cuenta ");
//	            sbSql.append("\n and oi.id_estatus_ord = ce.id_estatus ");
//	            sbSql.append("\n and oi.id_banco *= cb.id_banco ");
//		    }
//		    else if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
//		    {
		    if(ConstantesSet.gsDBM.equals("ORACLE"))
		    	sbSql.append("\n p.razon_social, pp.paterno || ' ' || pp.materno || ' ' || pp.nombre as contacto, ");
		    else
		    	sbSql.append("\n p.razon_social, pp.paterno + ' ' + pp.materno + ' ' + pp.nombre as contacto, ");
	        sbSql.append("\n oi.importe+interes-isr as total, oi.plazo, oi.tasa, ");
	        if(ConstantesSet.gsDBM.equals("ORACLE"))
	            sbSql.append("\n to_char(oi.hora,'99') || ':' || to_char(oi.minuto,'99') as hora, ");
	        else
	        	sbSql.append("\n cast(oi.hora as char(2)) + ':' + cast(oi.minuto as char(2)) as hora, ");
	        
            sbSql.append("\n id_estatus_ord , oi.importe, interes, isr ");
            sbSql.append("\n FROM ");
            sbSql.append("\n orden_inversion oi left join cat_banco cb ");
            sbSql.append("\n on(oi.id_banco = cb.id_banco), persona p, persona pp, ");
            sbSql.append("\n cat_estatus ce, cuenta c ");
            sbSql.append("\n Where ");
            sbSql.append("\n oi.no_institucion = p.no_persona ");
//	            sbSql.append("\n and p.id_tipo_persona ='B'");
            sbSql.append("\n and oi.tipo_orden = 'E' ");
            sbSql.append("\n and oi.no_empresa = c.no_empresa ");
            sbSql.append("\n and oi.no_empresa = c.no_persona ");
            sbSql.append("\n and oi.no_cuenta = c.no_cuenta ");
            sbSql.append("\n and oi.id_estatus_ord = ce.id_estatus ");
//		    }
//		    else if(ConstantesSet.gsDBM.equals("DB2"))
//		    {
//		    	sbSql.append("\n p.razon_social, pp.paterno || ' ' || pp.materno || ' ' || pp.nombre as contacto, ");
//	            sbSql.append("\n oi.importe+interes-isr as total, oi.plazo, oi.tasa, ");
//	            sbSql.append("\n cast(oi.hora as char(29)) || ':' || cast(oi.minuto as char(2)) as hora, ");
//	            sbSql.append("\n id_estatus_ord , oi.importe, interes, isr ");
//	            sbSql.append("\n FROM ");
//	            sbSql.append("\n orden_inversion oi left join cat_banco cb ");
//	            sbSql.append("\n on(oi.id_banco = cb.id_banco), persona p, persona pp, ");
//	            sbSql.append("\n cat_estatus ce, cuenta c ");
//	            sbSql.append("\n Where ");
//	            sbSql.append("\n oi.no_institucion = p.no_persona ");
//	            sbSql.append("\n and p.id_tipo_persona ='B'");
//	            sbSql.append("\n and oi.tipo_orden = 'E' ");
//	            sbSql.append("\n and oi.no_empresa = c.no_empresa ");
//	            sbSql.append("\n and oi.no_empresa = c.no_persona ");
//	            sbSql.append("\n and oi.no_cuenta = c.no_cuenta ");
//	            sbSql.append("\n and oi.id_estatus_ord = ce.id_estatus ");
//		    }
		            
		            
		    sbSql.append("\n and ce.clasificacion = 'ORD' ");
		    sbSql.append("\n and oi.id_estatus_ord <> 'X' ");
		    sbSql.append("\n and oi.no_contacto = pp.no_persona ");
		
		    sbSql.append("\n   and oi.no_empresa in (select setemp from set006 ");
		    sbSql.append("\n   where soiemp in(select soiemp from set006 ");
		    sbSql.append("\n   where setemp in(select no_empresa from empresa where b_concentradora = 'S')))");
		    sbSql.append("\n and oi.fec_alta between '" + funciones.ponerFecha(dto.getFecInicial()) + "' and '" + funciones.ponerFecha(dto.getFecFinal()) + "' ");
		    sbSql.append("\n and c.id_divisa= '" + dto.getIdDivisa() + "'");
		    sbSql.append("\n ORDER BY desc_estatus, no_orden, p.razon_social");
		    
		    //System.out.println(sbSql.toString());
		    
		    listInvHoy = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public Map mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		Map map = new HashMap();
		    			map.put("fecVenc", rs.getDate("fec_venc"));
		    			map.put("noOrden", rs.getString("no_orden"));
		    			map.put("noCuenta", rs.getInt("no_cuenta"));
		    			map.put("idPapel", rs.getString("id_papel"));
		    			map.put("descEstatus", rs.getString("desc_estatus"));
		    			map.put("idDivisa", rs.getString("id_divisa"));
		    			map.put("idTipoValor", rs.getString("id_tipo_valor"));
		    			map.put("idChequera", rs.getString("id_chequera"));
		    			map.put("idBanco", rs.getInt("id_banco"));
		    			map.put("descBanco", rs.getString("desc_banco"));
		    			map.put("razonSocial", rs.getString("razon_social"));
		    			map.put("contacto", rs.getString("contacto"));
		    			map.put("total", rs.getBigDecimal("total").doubleValue());
		    			map.put("plazo", rs.getInt("plazo"));
		    			map.put("tasa", rs.getBigDecimal("tasa").doubleValue());
		    			map.put("sHora", rs.getString("hora"));
		    			map.put("idEstatusOrd", rs.getString("id_estatus_ord"));
		    			map.put("importe", rs.getBigDecimal("importe").doubleValue());
		    			map.put("interes", rs.getBigDecimal("interes").doubleValue());
		    			map.put("isr", rs.getBigDecimal("isr").doubleValue());
		    		return map;
		    	}
		    });
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarInvEstHoy");
		}
		return listInvHoy;
	}
	
	/**
	 * Este metodo es utilizado para realizar un reporte cuyo nombre es
	 * Vencimiento de Inversiones, utilizado en ReportesDeInversiones.js,
	 * sin embargo puede ser utilizado para otra funcion.(FunSQLSelect995)
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarVencimientoInversiones(ParamReportesDto dto)
	{
		List<Map<String, Object>> listVenc = new ArrayList<Map<String, Object>>();
		StringBuffer sbSql = new StringBuffer();
		try
		{
			sbSql.append("Select distinct");
			sbSql.append("\n oi.fec_alta, no_orden, oi.no_cuenta, ");
			sbSql.append("\n id_papel, ce.desc_estatus, coalesce(c.id_divisa,'') as id_divisa,");
			sbSql.append("\n oi.fec_venc, id_tipo_valor, coalesce(oi.id_chequera,'') as id_chequera,");
			
//			if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
//			{
//				sbSql.append("\n p.razon_social, pp.paterno + ' ' + pp.materno + ' ' + ");
//				sbSql.append("\n pp.nombre as contacto, oi.importe+interes-isr as total,");
//				sbSql.append("\n oi.plazo,oi.tasa, convert(varchar,oi.hora) + ':' + convert(varchar,oi.minuto) as hora, ");
//				sbSql.append("\n id_estatus_ord, coalesce(cb.desc_banco,'') as desc_banco, oi.importe,");
//				sbSql.append("\n interes , isr ");
//				sbSql.append("\n From ");
//				sbSql.append("\n  orden_inversion oi ,persona p, cat_banco cb,persona pp,cat_estatus ce, cuenta c ");
//				sbSql.append("\n Where ");
//				sbSql.append("\n oi.id_banco *= cb.id_banco ");
//				sbSql.append("\n and oi.no_empresa = c.no_empresa ");
//			}
//			else if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
//			{
			if(ConstantesSet.gsDBM.equals("ORACLE"))
				sbSql.append("\n p.razon_social, pp.paterno || ' ' || pp.materno || ' ' || ");
			else
				sbSql.append("\n p.razon_social, pp.paterno + ' ' + pp.materno + ' ' + ");
				sbSql.append("\n pp.nombre as contacto, oi.importe+interes-isr as total,");
			if(ConstantesSet.gsDBM.equals("ORACLE"))
				sbSql.append("\n oi.plazo,oi.tasa, to_char(oi.hora,'99') || ':' || to_char(oi.minuto,'99') as hora, ");
			else
				sbSql.append("\n oi.plazo,oi.tasa, cast(oi.hora as char(2)) + ':' + cast(oi.minuto as char(2)) as hora, ");
			
				sbSql.append("\n id_estatus_ord, coalesce(cb.desc_banco,'') as desc_banco, oi.importe,");
				sbSql.append("\n interes , isr ");
				sbSql.append("\n From ");
				sbSql.append("\n  orden_inversion oi left join cat_banco cb on ");
				sbSql.append("\n (oi.id_banco = cb.id_banco),persona p,persona pp,cat_estatus ce, cuenta c ");
				sbSql.append("\n Where ");
				sbSql.append("\n oi.no_empresa = c.no_empresa ");
//			}
//			else if(ConstantesSet.gsDBM.equals("DB2"))
//			{
//				sbSql.append("\n p.razon_social, pp.paterno || ' ' || pp.materno || ' ' || ");
//				sbSql.append("\n pp.nombre as contacto, oi.importe+interes-isr as total,");
//				sbSql.append("\n oi.plazo,oi.tasa, cast(oi.hora as char(2)) || ':' || cast(oi.minuto as char(2)) as hora, ");
//				sbSql.append("\n id_estatus_ord, coalesce(cb.desc_banco,'') as desc_banco, oi.importe,");
//				sbSql.append("\n interes , isr ");
//				sbSql.append("\n From ");
//				sbSql.append("\n  orden_inversion oi left join cat_banco cb on ");
//				sbSql.append("\n (oi.id_banco = cb.id_banco),persona p,persona pp,cat_estatus ce, cuenta c ");
//				sbSql.append("\n Where ");
//				sbSql.append("\n oi.no_empresa = c.no_empresa ");
//			}
			
			sbSql.append("\n and oi.tipo_orden = 'E' ");
			sbSql.append("\n and oi.no_empresa = c.no_persona ");
			sbSql.append("\n and oi.no_cuenta = c.no_cuenta ");
			sbSql.append("\n and oi.id_estatus_ord = ce.id_estatus ");
			sbSql.append("\n and ce.clasificacion = 'ORD' ");
			sbSql.append("\n and oi.id_estatus_ord<>'X' ");
			sbSql.append("\n and oi.no_institucion = p.no_persona ");
//			sbSql.append("\n and p.id_tipo_persona='B' ");
			sbSql.append("\n and oi.no_contacto = pp.no_persona ");
			sbSql.append("\n and pp.id_tipo_persona='F' ");
			sbSql.append("\n and oi.no_empresa in (select setemp from set006 ");
			sbSql.append("\n   where soiemp in(select soiemp from set006 ");
			sbSql.append("\n   where setemp in(select no_empresa from empresa where b_concentradora = 'S')))");
			
			sbSql.append("\n and oi.fec_venc between '" + funciones.ponerFechaSola(dto.getFecInicial()) + "' ");
			sbSql.append("\n and '" + funciones.ponerFechaSola(dto.getFecFinal()) + "' ");
			sbSql.append("\n and c.id_divisa= '" + dto.getIdDivisa() + "' ");
			sbSql.append("\n order by id_estatus_ord, p.razon_social");
			
			//System.out.println(sbSql.toString());
			
			listVenc = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
				public Map mapRow(ResultSet rs, int idx)throws SQLException
				{
					Map map = new HashMap();
		    			map.put("fecAlta", rs.getDate("fec_alta"));
		    			map.put("noOrden", rs.getString("no_orden"));
		    			map.put("noCuenta", rs.getInt("no_cuenta"));
		    			map.put("idPapel", rs.getString("id_papel"));
		    			map.put("descEstatus", rs.getString("desc_estatus"));
		    			map.put("idDivisa", rs.getString("id_divisa"));
		    			map.put("fecVenc", rs.getDate("fec_venc"));
		    			map.put("idTipoValor", rs.getString("id_tipo_valor"));
		    			map.put("idChequera", rs.getString("id_chequera"));
		    			map.put("descBanco", rs.getString("desc_banco"));
		    			map.put("razonSocial", rs.getString("razon_social"));
		    			map.put("contacto", rs.getString("contacto"));
		    			map.put("total", rs.getBigDecimal("total").doubleValue());
		    			map.put("plazo", rs.getInt("plazo"));
		    			map.put("tasa", rs.getBigDecimal("tasa").doubleValue());
		    			map.put("sHora", rs.getString("hora"));
		    			map.put("idEstatusOrd", rs.getString("id_estatus_ord"));
		    			map.put("importe", rs.getBigDecimal("importe").doubleValue());
		    			map.put("interes", rs.getBigDecimal("interes").doubleValue());
		    			map.put("isr", rs.getBigDecimal("isr").doubleValue());
		    		return map;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarVencimientoInversiones");
		}
		return listVenc;
	}
	
	/**
	 * Este metodo es utilizado para realizar un reporte cuyo nombre es
	 * Vencimiento de Inversiones Saldos Promedio, utilizado en ReportesDeInversiones.js,
	 * sin embargo puede ser utilizado para otra funcion.(FunSQLVenSal2Prom)
	 * @param dto
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarVencInvSal2Prom(ParamReportesDto dto)
	{
		List<Map<String, Object>> listVenc = new ArrayList<Map<String, Object>>();
		StringBuffer sbSql = new StringBuffer();
		try
		{
		    sbSql.append("SELECT DISTINCT oi.fec_alta, no_orden, oi.no_cuenta, ");
		    sbSql.append("\n id_papel, ce.desc_estatus, COALESCE(c.id_divisa,'') AS id_divisa,");
		    sbSql.append("\n oi.fec_venc, id_tipo_valor, COALESCE(oi.id_chequera,'') AS id_chequera,");
			
//			if(ConstantesSet.gsDBM.equals("SQL SERVER") || ConstantesSet.gsDBM.equals("SYBASE"))
//			{
//				sbSql.append("\n p.razon_social, pp.paterno + ' ' + pp.materno + ' ' + ");
//	            sbSql.append("\n pp.nombre as contacto, oi.importe+interes-isr as total,");
//	            sbSql.append("\n oi.plazo, oi.tasa - oi.tasa_isr AS tasa, ");
//	            sbSql.append("\n convert(varchar,oi.hora) + ':' + convert(varchar,oi.minuto) as hora, ");
//	            sbSql.append("\n id_estatus_ord, coalesce(cb.desc_banco,'') as desc_banco, oi.importe,");
//	            sbSql.append("\n interes , isr ");
//	            sbSql.append("\n From ");
//	            sbSql.append("\n  orden_inversion oi ,persona p, cat_banco cb,persona pp,cat_estatus ce, cuenta c ");
//	            sbSql.append("\n Where ");
//	            sbSql.append("\n oi.id_banco *= cb.id_banco ");
//	            sbSql.append("\n and oi.no_empresa = c.no_empresa ");
//			}
//			else if(ConstantesSet.gsDBM.equals("POSTGRESQL"))
//			{
		    if(ConstantesSet.gsDBM.equals("ORACLE"))
				sbSql.append("\n p.razon_social, pp.paterno || ' ' || pp.materno || ' ' || ");
		    else
		    	sbSql.append("\n p.razon_social, pp.paterno + ' ' + pp.materno + ' ' + ");
	            sbSql.append("\n pp.nombre as contacto, oi.importe+interes-isr as total,");
	            sbSql.append("\n oi.plazo, oi.tasa - oi.tasa_isr AS tasa, ");
	       if(ConstantesSet.gsDBM.equals("ORACLE"))
	            sbSql.append("\n to_char(oi.hora,'99') || ':' || to_char(oi.minuto,'99') as hora, ");
	       else
	    	   sbSql.append("\n cast(oi.hora char(2)) + ':' + cast(oi.minuto as char(2)) as hora, ");
	            sbSql.append("\n id_estatus_ord, coalesce(cb.desc_banco,'') as desc_banco, oi.importe,");
	            sbSql.append("\n interes , isr ");
	            sbSql.append("\n From ");
	            sbSql.append("\n  orden_inversion oi left join cat_banco cb on ");
	            sbSql.append("\n(oi.id_banco = cb.id_banco),persona p,persona pp,cat_estatus ce, cuenta c ");
	            sbSql.append("\n Where ");
	            sbSql.append("\n oi.no_empresa = c.no_empresa ");
//			}
//			else if(ConstantesSet.gsDBM.equals("DB2"))
//			{
//				sbSql.append("\n p.razon_social ||' '|| COALESCE(oi.id_chequera,'') AS razon_social, pp.paterno || ' ' || pp.materno || ' ' || ");
//	            sbSql.append("\n pp.nombre AS contacto, oi.importe + interes - isr AS total,");
//	            sbSql.append("\n oi.plazo, oi.tasa - oi.tasa_isr AS tasa, ");
//	            sbSql.append("\n CAST(oi.hora AS CHAR(2)) || ':' || CAST(oi.minuto AS CHAR(2)) AS hora, ");
//	            sbSql.append("\n id_estatus_ord, COALESCE(cb.desc_banco,'') AS desc_banco, oi.importe, interes, isr");
//	            
//	            if(!funciones.validarCadena(dto.getIdDivisa()).equals("") && !dto.getIdDivisa().equals("MN"))
//	            {
//	            	sbSql.append("\n ,vd.valor, oi.importe * vd.valor AS importe_mn, interes * vd.valor AS interes_mn, isr * vd.valor AS isr_mn, ");
//	                sbSql.append("\n (oi.importe * vd.valor) + (interes * vd.valor) - (isr * vd.valor) AS total_mn");
//	            }
//	            
//	            sbSql.append("\n FROM orden_inversion oi LEFT JOIN cat_banco cb ON ");
//	            sbSql.append("\n (oi.id_banco = cb.id_banco), persona p, persona pp, cat_estatus ce, cuenta c");
//	            
//	            if(!funciones.validarCadena(dto.getIdDivisa()).equals("") && !dto.getIdDivisa().equals("MN"))
//	                sbSql.append("\n, valor_divisa vd ");
//	            
//	            sbSql.append("\n WHERE oi.no_empresa = c.no_empresa ");
//			}
			
		    sbSql.append("\n AND oi.tipo_orden = 'E' ");
		    sbSql.append("\n AND oi.no_empresa = c.no_persona ");
		    sbSql.append("\n AND oi.no_cuenta = c.no_cuenta ");
		    sbSql.append("\n AND oi.id_estatus_ord = ce.id_estatus ");
		    sbSql.append("\n AND ce.clasificacion = 'ORD' ");
		    sbSql.append("\n AND oi.id_estatus_ord <> 'X' ");
		    sbSql.append("\n AND oi.no_institucion = p.no_persona ");
//		    sbSql.append("\n AND p.id_tipo_persona = 'B' ");
		    sbSql.append("\n AND oi.no_contacto = pp.no_persona ");
		    sbSql.append("\n AND pp.id_tipo_persona = 'F' ");
		    sbSql.append("\n AND oi.no_empresa IN (SELECT setemp FROM set006 ");
		    sbSql.append("\n   WHERE soiemp IN (SELECT soiemp FROM set006 ");
		    sbSql.append("\n   WHERE setemp IN (SELECT no_empresa FROM empresa WHERE b_concentradora = 'S')))");

		    sbSql.append("\n AND oi.fec_venc between '" + funciones.ponerFechaSola(dto.getFecInicial()) + "'");
		    sbSql.append("   AND '" + funciones.ponerFechaSola(dto.getFecFinal())+ "'");
		    sbSql.append("\n AND c.id_divisa = '" + dto.getIdDivisa() + "' ");
		    
		    if(!funciones.validarCadena(dto.getIdDivisa()).equals("") && !dto.getIdDivisa().equals("MN"))
		    {
		    	sbSql.append("\n AND c.id_divisa = vd.id_divisa");
		        sbSql.append("\n AND oi.fec_alta = vd.fec_divisa");
		    }
		    
		    sbSql.append("\n ORDER BY id_estatus_ord, p.razon_social ");
			
		    //System.out.println(sbSql.toString());
		    
			listVenc = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
				public Map mapRow(ResultSet rs, int idx)throws SQLException
				{
					Map map = new HashMap();
		    			map.put("fecAlta", rs.getDate("fec_alta"));
		    			map.put("noOrden", rs.getString("no_orden"));
		    			map.put("noCuenta", rs.getInt("no_cuenta"));
		    			map.put("idPapel", rs.getString("id_papel"));
		    			map.put("descEstatus", rs.getString("desc_estatus"));
		    			map.put("idDivisa", rs.getString("id_divisa"));
		    			map.put("fecVenc", rs.getDate("fec_venc"));
		    			map.put("idTipoValor", rs.getString("id_tipo_valor"));
		    			map.put("idChequera", rs.getString("id_chequera"));
		    			map.put("descBanco", rs.getString("desc_banco"));
		    			map.put("razonSocial", rs.getString("razon_social"));
		    			map.put("contacto", rs.getString("contacto"));
		    			map.put("total", rs.getBigDecimal("total").doubleValue());
		    			map.put("plazo", rs.getInt("plazo"));
		    			map.put("tasa", rs.getBigDecimal("tasa").doubleValue());
		    			map.put("sHora", rs.getString("hora"));
		    			map.put("idEstatusOrd", rs.getString("id_estatus_ord"));
		    			map.put("importe", rs.getBigDecimal("importe").doubleValue());
		    			map.put("interes", rs.getBigDecimal("interes").doubleValue());
		    			map.put("isr", rs.getBigDecimal("isr").doubleValue());
		    		return map;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarVencInvSal2Prom");
		}
		return listVenc;
	}
	
	/**
	 * Este metodo es utilizado para realizar un reporte llamado InversionesDiarias,
	 * que se llama en ReportesDeInversiones.js
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> consultarInversionesDiarias(ParamReportesDto dto)
	{
		List<Map<String, Object>> listInvDia = new ArrayList<Map<String, Object>>();
		StringBuffer sbSql = new StringBuffer();
		try
		{
			if(dto.isBTodasDivisas())
			{
				sbSql.append("\n   select ");
		        sbSql.append("\n   p.razon_social as institucion, ccb.id_chequera, ccb.tipo_chequera, ccb.id_banco,");
		        sbSql.append("\n   oi.importe, coalesce(ccb.saldo_final, 0) as remanente,");
		        sbSql.append("\n   (oi.tasa - oi.tasa_isr) as tasa, oi.fec_alta, oi.fec_venc,");
		        if(ConstantesSet.gsDBM.equals("DB2"))
		            sbSql.append("\n   ((oi.interes - oi.isr)/(date(oi.fec_venc) - date(oi.fec_alta))) as interes, oi.interes - oi.isr as total ");
		        else
		            sbSql.append("\n   ((oi.interes - oi.isr)/(datediff(dd, oi.fec_venc,oi.fec_alta))) as interes, oi.interes - oi.isr as total ");
		        
		        sbSql.append("\n   From orden_inversion oi, persona p, ");
		        if(dto.isBActual())
		        {
		        	sbSql.append("\n   cat_cta_banco ccb");
		            sbSql.append("\n   Where");
		            sbSql.append("\n   oi.fec_alta <= '" + funciones.ponerFechaSola(dto.getFecInicial()) + "' and oi.fec_venc > '" + funciones.ponerFechaSola(dto.getFecFinal()) + "' and");
		        }
		        else
		        {
		        	sbSql.append("\n   hist_cat_cta_banco ccb");
		            sbSql.append("\n   Where");
		            sbSql.append("\n   oi.fec_alta <= '" + funciones.ponerFechaSola(dto.getFecInicial()) + "' and oi.fec_venc > '" + funciones.ponerFechaSola(dto.getFecFinal()) + "' ");
		            sbSql.append("\n   and ccb.fec_valor = oi.fec_alta and");
		        }
		            
			      sbSql.append("\n   oi.no_institucion = p.no_persona");
			      sbSql.append("\n   and p.id_tipo_persona ='B'");
			      sbSql.append("\n   and oi.tipo_orden = 'E'");
			      sbSql.append("\n   and oi.no_empresa = ccb.no_empresa");
			      sbSql.append("\n   and oi.id_estatus_ord in ('A', 'V')");
			      sbSql.append("\n   and oi.no_empresa in (select setemp from set006 ");
			      sbSql.append("\n   where soiemp in(select soiemp from set006 ");
			      sbSql.append("\n   where setemp in(select no_empresa from empresa where b_concentradora = 'S')))");
			      
			      sbSql.append("\n   and ccb.id_divisa = 'MN'");
			      
			      sbSql.append("\n   and ccb.id_chequera = oi.id_chequera");
			      if(dto.isBTodasDivisas() || 
			    		  (!funciones.validarCadena(dto.getIdDivisa()).equals("") && !dto.getIdDivisa().equals("MN")))
			      {
			    	 sbSql.append("\n   Union All");
			         sbSql.append("\n   select  ");
			         sbSql.append("\n   p.razon_social as institucion, ccb.id_chequera, ccb.tipo_chequera, ccb.id_banco,");
			         sbSql.append("\n   (oi.importe * vd.valor) as importe,");
			         sbSql.append("\n   coalesce(ccb.saldo_final * (select valor from valor_divisa where id_divisa = 'DLS' and fec_divisa = '" + funciones.ponerFechaSola(dto.getFecInicial()) + "'), 0) as remanente,");
			         sbSql.append("\n   (oi.tasa - oi.tasa_isr) as tasa, oi.fec_alta, oi.fec_venc,");
			         
			         if(ConstantesSet.gsDBM.equals("DB2"))
			            sbSql.append("\n   (((oi.interes - oi.isr)/(date(oi.fec_venc) - date(oi.fec_alta)))) * vd.valor as interes, (oi.interes - oi.isr) * vd.valor as total ");
			         else
			            sbSql.append("\n   (((oi.interes - oi.isr)/(datediff(dd, oi.fec_venc, oi.fec_alta)))) * vd.valor as interes, (oi.interes - oi.isr) * vd.valor as total ");
			         
			         sbSql.append("\n   From orden_inversion oi, persona p, valor_divisa vd,");
			         
			         if(dto.isBActual())
			         {
			        	 sbSql.append("\n   cat_cta_banco ccb");
			        	 sbSql.append("\n   Where");
			        	 sbSql.append("\n   oi.fec_alta <= '" + funciones.ponerFechaSola(dto.getFecInicial()) + "' and oi.fec_venc > '" + funciones.ponerFechaSola(dto.getFecFinal()) + "' and");
			         }
			         else
			         {
			        	sbSql.append("\n   hist_cat_cta_banco ccb");
			            sbSql.append("\n   Where");
			            sbSql.append("\n   oi.fec_alta <= '" + funciones.ponerFechaSola(dto.getFecInicial()) + "' and oi.fec_venc > '" + funciones.ponerFechaSola(dto.getFecFinal()) + "' ");
			            sbSql.append("\n   and ccb.fec_valor = oi.fec_alta and");
			         }
			                 
			         sbSql.append("\n   oi.no_institucion = p.no_persona");
			         sbSql.append("\n   and p.id_tipo_persona ='B'");
			         sbSql.append("\n   and oi.tipo_orden = 'E'");
			         sbSql.append("\n   and oi.no_empresa = ccb.no_empresa");
			         sbSql.append("\n   and oi.id_estatus_ord in ('A', 'V')");
			         sbSql.append("\n   and oi.no_empresa in (select setemp from set006 ");
			         sbSql.append("\n   where soiemp in(select soiemp from set006 ");
			         sbSql.append("\n   where setemp in(select no_empresa from empresa where b_concentradora = 'S')))");
			        
			         sbSql.append("\n   and ccb.id_divisa <> 'MN'");
			         
			         sbSql.append("\n   and ccb.id_chequera = oi.id_chequera");
			         sbSql.append("\n   and ccb.id_divisa = vd.id_divisa");
			         sbSql.append("\n   and oi.fec_alta = vd.fec_divisa");
			      }
			      
			      if(ConstantesSet.gsDBM.equals("DB2"))
			    	  sbSql.append("\n   order by id_chequera, fec_alta, tipo_chequera, id_banco");
			      else
			    	  sbSql.append("\n   order by ccb.id_chequera, oi.fec_alta, ccb.tipo_chequera, ccb.id_banco");
			      
			}
			else
			{
				sbSql.append("\n   select  ");
		        sbSql.append("\n   p.razon_social as institucion, ccb.id_chequera, ccb.tipo_chequera, ccb.id_banco,");
		        
		        if(!funciones.validarCadena(dto.getIdDivisa()).equals("") && dto.getIdDivisa().equals("MN"))
		        {
		        	sbSql.append("\n   oi.importe, coalesce(ccb.saldo_final, 0) as remanente,");
		            sbSql.append("\n   (oi.tasa - oi.tasa_isr) as tasa, oi.fec_alta, oi.fec_venc,");
		            if(ConstantesSet.gsDBM.equals("DB2"))
		                sbSql.append("\n   ((oi.interes - oi.isr)/(date(oi.fec_venc) - date(oi.fec_alta))) as interes, oi.interes - oi.isr as total ");
		            else
		                sbSql.append("\n   ((oi.interes - oi.isr)/(datediff(dd, oi.fec_venc, oi.fec_alta))) as interes, oi.interes - oi.isr as total ");
		           
		            sbSql.append("\n   From orden_inversion oi, persona p,");
		        }
		        else
		        {
		        	sbSql.append("\n   (oi.importe * vd.valor) as importe,");
		            sbSql.append("\n   coalesce(ccb.saldo_final * (select valor from valor_divisa where id_divisa = '" + dto.getIdDivisa() + "' and fec_divisa = '" + funciones.ponerFechaSola(dto.getFecInicial())+ "'), 0) as remanente,");
		            sbSql.append("\n   (oi.tasa - oi.tasa_isr) as tasa, oi.fec_alta, oi.fec_venc,");
		            
		            if(ConstantesSet.gsDBM.equals("DB2"))
		            	sbSql.append("\n   (((oi.interes - oi.isr)/(date(oi.fec_venc) - date(oi.fec_alta)))) * vd.valor as interes, (oi.interes - oi.isr) * vd.valor as total ");
		            else
		            	sbSql.append("\n   (((oi.interes - oi.isr)/(datediff(dd, oi.fec_venc, oi.fec_alta)))) * vd.valor as interes, (oi.interes - oi.isr) * vd.valor as total ");
		            
		            sbSql.append("\n   From orden_inversion oi, persona p, valor_divisa vd,");
		        }
		       
		        if(dto.isBActual())
		        {
		        	sbSql.append("\n   cat_cta_banco ccb");
		            sbSql.append("\n   Where");
		            sbSql.append("\n   oi.fec_alta <= '" + funciones.ponerFechaSola(dto.getFecInicial()) + "' and oi.fec_venc > '" + funciones.ponerFechaSola(dto.getFecFinal()) + "' and ");
		        }
		        else
		        {
		        	sbSql.append("\n   hist_cat_cta_banco ccb");
		            sbSql.append("\n   Where");
		            sbSql.append("\n   oi.fec_alta <= '" + funciones.ponerFechaSola(dto.getFecInicial()) + "' and oi.fec_venc > '" + funciones.ponerFechaSola(dto.getFecFinal())+ "' ");
		            sbSql.append("\n   and ccb.fec_valor = oi.fec_alta and");
		        }
		        
		        sbSql.append("\n   oi.no_institucion = p.no_persona");
		        sbSql.append("\n   and p.id_tipo_persona ='B'");
		        sbSql.append("\n   and oi.tipo_orden = 'E'");
		        sbSql.append("\n   and oi.no_empresa = ccb.no_empresa");
		        sbSql.append("\n   and oi.id_estatus_ord in ('A', 'V')");
		        sbSql.append("\n   and oi.no_empresa in (select setemp from set006 ");
		        sbSql.append("\n   where soiemp in(select soiemp from set006 ");
		        sbSql.append("\n   where setemp in(select no_empresa from empresa where b_concentradora = 'S')))");
		        sbSql.append("\n   and ccb.id_divisa = '" + dto.getIdDivisa() + "'");
		        
		        if(ConstantesSet.gsDBM.equals("MN"))
		        {
		        	sbSql.append("\n   and ccb.id_divisa = vd.id_divisa");
			        sbSql.append("\n   and oi.fec_alta = vd.fec_divisa");
		        }
		           
		        sbSql.append("\n   and ccb.id_chequera = oi.id_chequera ");
		        
		        if(ConstantesSet.gsDBM.equals("DB2"))
		            sbSql.append("\n   order by id_chequera, fec_alta, tipo_chequera, id_banco");
		        else
		            sbSql.append("\n   order by ccb.id_chequera, oi.fec_alta, ccb.tipo_chequera, ccb.id_banco");
			}
			
			//System.out.println(sbSql.toString());
			
			listInvDia = jdbcTemplate.query(sbSql.toString(), new RowMapper()
			{
				public Map mapRow(ResultSet rs, int idx)throws SQLException
				{
					Map map = new HashMap();
						map.put("razonSocial", rs.getString("institucion"));
						map.put("idChequera", rs.getString("id_chequera"));
						map.put("tipoChequera", rs.getString("tipo_chequera"));
						map.put("idBanco", rs.getInt("id_banco"));
						map.put("importe", rs.getBigDecimal("importe").doubleValue());
						map.put("remanente", rs.getBigDecimal("remanente").doubleValue());
						map.put("tasa", rs.getBigDecimal("tasa").doubleValue());
						map.put("fecAlta", rs.getDate("fec_alta"));
						map.put("fecVenc", rs.getDate("fec_venc"));
						map.put("interes", rs.getBigDecimal("interes").doubleValue());
						map.put("total", rs.getBigDecimal("total").doubleValue());
					return map;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarInversionesDiarias");

		}
		return listInvDia;
	}
	
	public List<LlenaComboEmpresasDto> llenarComboEmpresas(int idUsuario) {
			consultasGenerales = new ConsultasGenerales (jdbcTemplate);
			return consultasGenerales.llenarComboEmpresas(idUsuario);
	}
	
	public int modificarIntImpInv(List<Map<String, String>> intImpInv) {
		StringBuffer sql = new StringBuffer();
		int resp = 0;
		
		try {
			sql.append(" UPDATE orden_inversion SET nota = '"+ intImpInv.get(0).get("nota") +"' \n");
			sql.append(" WHERE no_empresa = "+ intImpInv.get(0).get("noEmpresa") +" \n");
			sql.append("	And no_orden = "+ intImpInv.get(0).get("noOrden") +" \n");
			
			resp = jdbcTemplate.update(sql.toString());
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Inversiones, C:InversionesDaoImpl, M:modificarIntImpInv");
		}
		return resp;
	}
	
	public int modificarMoviIntIsr(List<Map<String, String>> intImpInv) {
		StringBuffer sql = new StringBuffer();
		int resp = 0;
		
		try {
			//Modifica el interes de la inversion
			if(Double.parseDouble(intImpInv.get(0).get("interes")) != 0) {
				sql.append(" UPDATE movimiento SET importe = '"+ intImpInv.get(0).get("interes") +"' \n");
				sql.append(" WHERE no_empresa = "+ intImpInv.get(0).get("noEmpresa") +" \n");
				sql.append("	And no_docto = "+ intImpInv.get(0).get("noOrden") +" \n");
				sql.append("	And no_folio_det = "+ intImpInv.get(0).get("noFolioDetInt") +" \n");
				sql.append("	And id_tipo_operacion = 4003 \n");
				
				resp = jdbcTemplate.update(sql.toString());
			}
			//Modifica el isr de la inversion
			if(Double.parseDouble(intImpInv.get(0).get("isr")) != 0) {
				sql.append(" UPDATE movimiento SET importe = '"+ intImpInv.get(0).get("isr") +"' \n");
				sql.append(" WHERE no_empresa = "+ intImpInv.get(0).get("noEmpresa") +" \n");
				sql.append("	And no_docto = "+ intImpInv.get(0).get("noOrden") +" \n");
				sql.append("	And no_folio_det = "+ intImpInv.get(0).get("noFolioDetIsr") +" \n");
				sql.append("	And id_tipo_operacion = 4004 \n");
				
				resp = jdbcTemplate.update(sql.toString());
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Inversiones, C:InversionesDaoImpl, M:modificarIntImpInv");
		}
		return resp;
	}
	public int validaFacultad(int facultad) {
		consultasGenerales = new ConsultasGenerales (jdbcTemplate);
		return consultasGenerales.validaFacultad(facultad);
	}
	
	//set del data source
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:InversionesDaoImpl, M:setDataSource");
		}
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@SuppressWarnings("unchecked")
	public List<ConsultaOrdenInversionDto> obtenerMontoInvertidoXFecha(
			String noEmpresa, String divisa, String fecIni, String fecFin)
	{
		List<ConsultaOrdenInversionDto> listConsOrden = new ArrayList<ConsultaOrdenInversionDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("SELECT sum(a.importe) as total_inversion, a.fec_alta ");
		    sbSql.append("\n FROM orden_inversion a, hist_cat_cta_banco b ");
		    sbSql.append("\n WHERE a.no_empresa = b.no_empresa ");
		    sbSql.append("\n and a.id_chequera = b.id_chequera ");
		    sbSql.append("\n and a.tipo_orden = 'E' ");
		    sbSql.append("\n and a.id_estatus_ord in ('A', 'V') ");
		    sbSql.append("\n and a.fec_alta >= convert(DATE, '"+fecIni+"', 103) and a.fec_venc <= convert(DATE, '"+fecFin+"', 103) ");
		    sbSql.append("\n and a.no_empresa = '"+noEmpresa+"' ");
		    sbSql.append("\n and b.id_divisa = '"+divisa+"' ");
		    sbSql.append("\n GROUP BY a.fec_alta ");
		    
		    //System.out.println(sbSql.toString());
		    
		    listConsOrden = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public ConsultaOrdenInversionDto mapRow(ResultSet rs, int idx) throws SQLException{
		    		ConsultaOrdenInversionDto dtoCons = new ConsultaOrdenInversionDto();
		    			dtoCons.setFecAlta(rs.getDate("fec_alta"));
		    			dtoCons.setImporte(rs.getDouble("total_inversion"));
		    		return dtoCons;
		    	}
		    });
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:obtenerMontoInvertidoXFecha");
		}
		return listConsOrden;
	}
	
	public String consultarbIsr(String isr){
		StringBuffer sbSql = new StringBuffer();
		String sRes = "";
		try{
			sbSql.append("SELECT b_isr ");
			sbSql.append("from cat_tipo_valor ");
			sbSql.append("where id_tipo_valor = '" + isr + "'");
			sRes = jdbcTemplate.queryForObject(sbSql.toString(), String.class);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarbIsr");
		}
		return sRes;
	}
	
	@SuppressWarnings("unchecked")
	public List<LlenaComboGralDto> consultarBancoOrdenInversion(Integer cveContrato, 
			Integer idEmpresa, String tipoChequera){
		
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listBancos = null;
		
		try{
		    sbSql.append("\n SELECT distinct cat_banco.id_banco as ID,   ");
		    sbSql.append("\n        cat_banco.desc_banco as Descrip   ");
		    sbSql.append("\n   FROM cat_banco,ctas_contrato, CAT_CTA_BANCO  ");
		    sbSql.append("\n  WHERE cat_banco.id_banco = ctas_contrato.id_banco  ");
		    sbSql.append("\n    AND ctas_contrato.no_empresa = ?  ");
		    sbSql.append("\n    AND ctas_contrato.no_cuenta = ? ");
		    sbSql.append("\n    AND ctas_contrato.ID_BANCO = CAT_CTA_BANCO.ID_BANCO ");
		    sbSql.append("\n    and ctas_contrato.ID_CHEQUERA = CAT_CTA_BANCO.ID_CHEQUERA ");
		    sbSql.append("\n    AND CAT_CTA_BANCO.tipo_chequera in ('I','M','C')  ");
		    
		    Object[] parametros = new Object[2];
		    
		    parametros[0] = idEmpresa;
		    parametros[1] = cveContrato;
//		    parametros[2] = tipoChequera;
		    
		    //System.out.println("Los parametros son "+ parametros[0] +","+ parametros[1]);

		    listBancos = jdbcTemplate.query(sbSql.toString(), parametros, new RowMapper(){
		    	public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		LlenaComboGralDto dtoCons = new LlenaComboGralDto();
		    			dtoCons.setId(rs.getInt("ID"));
		    			dtoCons.setDescripcion(rs.getString("Descrip"));
		    		return dtoCons;
		    	}
		    });
		    //System.out.println("El query "+ sbSql.toString());
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarBancosCargo");
		}
		return listBancos;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<LlenaComboGralDto> consultarBancoOrdenIinstitucion(Integer idEmpresa, Integer cveContrato){
		
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listBancos = null;
		
		try{
		    sbSql.append  (" select distinct cb.id_banco,cb.desc_banco   ");
		    sbSql.append("\n from ctas_banco ctas,cat_banco cb, persona p ");
		    sbSql.append("\n where ctas.no_persona = p.no_persona ");
		    if(ConstantesSet.gsDBM.equals("ORACLE"))
		    	sbSql.append("\n and p.equivale_persona in(select to_char(no_institucion) from cuenta c ");
		    else
		    	sbSql.append("\n and p.no_persona in(select cast(no_institucion as varchar(15)) from cuenta c ");
		    
		    sbSql.append("\n                      where c.no_empresa= ?  ");
		    sbSql.append("\n                      and c.no_cuenta= ?)  ");
		    sbSql.append("\n and p.id_tipo_persona = ctas.id_tipo_persona ");
		    sbSql.append("\n and p.id_tipo_persona = 'B' ");
		    sbSql.append("\n and ctas.id_banco=cb.id_banco ");
		    
		    Object[] parametros = new Object[2];
		    
		    parametros[0] = idEmpresa;
		    parametros[1] = cveContrato;
		    System.out.println("+.::   +"+parametros[0]+","+parametros[1]+"\nPara el query\n"+sbSql.toString());
		    
		    listBancos = jdbcTemplate.query(sbSql.toString(), parametros, new RowMapper(){
		    	public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		LlenaComboGralDto dtoCons = new LlenaComboGralDto();
		    			dtoCons.setId(rs.getInt("id_banco"));
		    			dtoCons.setDescripcion(rs.getString("desc_banco"));
		    		return dtoCons;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarBancosCargo");
		}
		return listBancos;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<LlenaComboGralDto> consultarChequeraOrdenInversion(Integer idBanco, 
			Integer idEmpresa, String tipoChequera){
		
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listBancos = null;
		
		try{
		    sbSql.append("\n select distinct cc.id_banco as ID,cc.id_chequera as chequera ");
		    sbSql.append("\n from CAT_CTA_BANCO cb  ");
		    sbSql.append("\n join ctas_contrato cc on (cb.ID_BANCO = cc.ID_BANCO  ");
		    sbSql.append("\n                        and cb.ID_CHEQUERA = cc.ID_CHEQUERA and ");
		    sbSql.append("\n                        cb.NO_EMPRESA = cc.no_empresa) ");
		    sbSql.append("\n and cb.TIPO_CHEQUERA in ('I','M','C') ");
		    sbSql.append("\n and cc.id_banco  = ? ");
		    sbSql.append("\n and cc.no_empresa= ? ");
		    sbSql.append("\n and no_cuenta = ? ");
		    
		    Object[] parametros = new Object[3];
		    
		    parametros[0] = idBanco;
		    parametros[1] = idEmpresa;
		    parametros[2] = new Integer(tipoChequera);
		    
System.out.println("cmbChequeraInv ["+ parametros[0] +","+ parametros[1]+ ","+ parametros[2]+"] query "+ sbSql.toString());
		    
		    listBancos = jdbcTemplate.query(sbSql.toString(), parametros, new RowMapper(){
		    	public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		LlenaComboGralDto dtoCons = new LlenaComboGralDto();
		    			dtoCons.setId(rs.getInt("ID"));
		    			dtoCons.setDescripcion(rs.getString("chequera"));
		    		return dtoCons;
		    	}
		    });
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarBancosCargo");
		}
		return listBancos;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<LlenaComboGralDto> consultarChequeraOrdenInstitucion(Integer idBanco, 
			Integer idEmpresa, Integer cuenta){
		
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listBancos = null;
		
		try{
		    sbSql.append("\n select distinct id_banco ,id_chequera   ");
		    sbSql.append("\n from ctas_banco ctas, persona p ");
		    sbSql.append("\n where ctas.no_persona  = p.NO_PERSONA ");
		    if(ConstantesSet.gsDBM.equals("ORACLE"))
		    	sbSql.append("\n and p.equivale_persona in(select to_char(no_institucion) from cuenta c ");
		    else
		    	sbSql.append("\n and p.no_persona in(select cast(no_institucion as varchar(15)) from cuenta c ");
		    sbSql.append("\n                      where c.no_empresa= ?  ");
		    sbSql.append("\n                      and c.no_cuenta= ?)  ");
		    sbSql.append("\n and ctas.id_banco= ? ");
		    
		    Object[] parametros = new Object[3];
		    
		    parametros[2] = idBanco;
		    parametros[0] = idEmpresa;
		    parametros[1] = cuenta;
		    
		    //System.out.println("Los parametros son "+ parametros[0] +","+ parametros[1]+", "+ parametros[2]);
		    
		    listBancos = jdbcTemplate.query(sbSql.toString(), parametros, new RowMapper(){
		    	public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		LlenaComboGralDto dtoCons = new LlenaComboGralDto();
		    			dtoCons.setId(rs.getInt("id_banco"));
		    			dtoCons.setDescripcion(rs.getString("id_chequera"));
		    		return dtoCons;
		    	}
		    });
		    //System.out.println("El query es "+ sbSql.toString());
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarBancosCargo");
		}
		return listBancos;
	}
	
	@SuppressWarnings("unchecked")
	public List<RubroDto> consultarRubro(String idTipoValor){
		StringBuffer sbSql = new StringBuffer();
		List<RubroDto> listaRubros = null;
		
		try{
		
		    sbSql.append(" SELECT  ID_GRUPO_INV, ID_RUBRO_INV,ID_GRUPO_REGR,ID_RUBRO_REGR,  ");
		    sbSql.append(" ID_GRUPO_INT,ID_RUBRO_INT,ID_GRUPO_ISR,ID_RUBRO_ISR  ");
		    sbSql.append("\n From CAT_TIPO_VALOR ");
		    sbSql.append("\n WHERE ID_TIPO_VALOR = '"+ idTipoValor +"'");
		    
		    listaRubros = jdbcTemplate.query(sbSql.toString(),  new RowMapper(){
		    	public RubroDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		RubroDto dtoRubro = new RubroDto();

		    		dtoRubro.setIdGrupoInv(rs.getBigDecimal("ID_GRUPO_INV"));
		    		dtoRubro.setIdRubroInv(rs.getBigDecimal("ID_RUBRO_INV"));

		    		dtoRubro.setIdGrupoReg(rs.getBigDecimal("ID_GRUPO_REGR"));
		    		dtoRubro.setIdRubroReg(rs.getBigDecimal("ID_RUBRO_REGR"));

		    		dtoRubro.setIdGrupoInt(rs.getBigDecimal("ID_GRUPO_INT"));
		    		dtoRubro.setIdRubroInt(rs.getBigDecimal("ID_RUBRO_INT"));

		    		dtoRubro.setIdGrupoISR(rs.getBigDecimal("ID_GRUPO_ISR"));
		    		dtoRubro.setIdRubroISR(rs.getBigDecimal("ID_RUBRO_ISR"));

		    		return dtoRubro;
		    	}
		    });
		    //System.out.println("El query es "+ sbSql.toString());
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarBancosCargo");
		}
		return listaRubros;
	}

	public Integer insertarTmpInvSipo(OrdenInversionDto dto){
		StringBuffer sbSql = new StringBuffer();
		int iRegAfec = 0;
		try{
		    sbSql.append("\n INSERT INTO TMP_INV_SIPO ");
		    sbSql.append("\n  (  ");
		    sbSql.append("\n  FOLIO, FOLIO_SEQ, FECHA, HORA, MONTO,  ");
		    sbSql.append("\n  EMISION_DE, EMISION_HASTA, TASA,  ");
		    sbSql.append("\n  ESTATUS, CONTRATO, PLAZO, GARANTIA, TIPO_PAPEL,  ");
		    sbSql.append("\n  TIPO_VALOR, CVE_CONTRATO, CONTACTO, FEC_VEN, MINUTO,  ");
		    sbSql.append("\n  IMP_ESPERADO, NO_INSTITUCION, PLAZO_INV, IMPUESTO,  ");
		    sbSql.append("\n  INTERES, DIVISA, INSTITUCION  ");
		    sbSql.append("\n  ,ID_GRUPO_INV, ID_RUBRO_INV,ID_GRUPO_REGR,ID_RUBRO_REGR, ");
		    sbSql.append("\n  ID_GRUPO_INT,ID_RUBRO_INT,ID_GRUPO_ISR,ID_RUBRO_ISR, ");
		    sbSql.append("\n  NO_EMPRESA,ID_BANCO,ID_CHEQUERA,ID_BANCO_REG,ID_CHEQUERA_REG" );
    	    
		    if(dto.getIdBancoInst() != null && 
		    		dto.getIdChequeraInst() != null && 
		    		!dto.getIdChequeraInst().equals("")){
		    	sbSql.append("\n ,ID_BANCO_BENEF,ID_CHQ_BENEF");
		    }
		    
		    Integer diasInvertir = new Funciones().diasEntreFechas(dto.getFecAlta(), dto.getFecVenc());
		    
    	    sbSql.append("\n  ) ");
		    sbSql.append("\n VALUES ");
		    sbSql.append("\n  (  ");
		    sbSql.append("\n  ").append(dto.getFolio()).append(", ");/*FOLIO Siempre 0*/
		    
		    if(ConstantesSet.gsDBM.equals("ORACLE"))
		    	sbSql.append("\n  SEQTMP_INV_SIPO.nextval, ");/*FOLIO Siempre 0*/
		    else
		    	sbSql.append("\n " + obtenerFolioReal("SEQTMP_INV_SIPO")  + ", ");/*FOLIO Siempre 0*/
		    
		    //sbSql.append("\n  TO_DATE('").append(funciones.ponerFechaSola(dto.getFecAlta())).append("', 'dd/MM/yyyy'), "); /*FECHA*/
		    sbSql.append("\n  '").append(FuncionesSql.ponFechaDMY(new java.sql.Date(dto.getFecAlta().getTime()), false)+ "',"); /*FECHA*/
		    
		    sbSql.append("\n  ").append(dto.getHora()).append(", "); /*HORA*/
//		    sbSql.append("\n  '").append(dto.getTipoOrden()).append("', ");/* TIPO_OPERACION Siempre es ""*/ 
//		    sbSql.append("\n  '").append(dto.getContraparte()).append("', ");/* CONTRAPARTE Siempre es ""*/ 
		    sbSql.append("\n  ").append(dto.getImporte()).append(", "); /*MONTO*/
//		    sbSql.append("\n  '").append(dto.getInstrumento()).append("', ");/* INSTRUMENTO Siempre es ""*/ 
//		    sbSql.append("\n  ").append("''").append(", ");
		    sbSql.append("\n  ").append("1").append(", "); /*EMISION_DE*/
		    sbSql.append("\n  ").append(diasInvertir).append(", "); /*EMISION_HASTA*/
//		    sbSql.append("\n  ").append("''").append(", "); /*FECHA_VALOR*/
		    sbSql.append("\n  ").append(dto.getTasa()).append(", ");/*TASA*/
//		    sbSql.append("\n  ").append("''").append(", ");/*ORIGEN*/
		    sbSql.append("\n  ").append("'P'").append(", ");/*ESTATUS*/
		    sbSql.append("\n  '").append(dto.getContrato()).append("', ");/*CONTRATO*/
		    sbSql.append("\n  ").append(dto.getPlazo()).append(", ");/*PLAZO*/
	    	sbSql.append("\n  '").append(dto.getbGarantia()).append("', ");/*GARANTIA N o S*/
		    sbSql.append("\n  '").append(dto.getIdPapel()).append("', ");/*TIPO_PAPEL*/
		    sbSql.append("\n  '").append(dto.getIdTipoValor()).append("', ");/*TIPO_VALOR*/
		    sbSql.append("\n  ").append(dto.getCveContrato()).append(", ");/*CVE_CONTRATO*/
		    sbSql.append("\n  ").append(dto.getNoContacto()).append(", ");/*CONTACTO*/
		    
		    sbSql.append("\n  '").append(FuncionesSql.ponFechaDMY(new java.sql.Date(dto.getFecVenc().getTime()), false) + "',");/*FEC_VEN*/
		    //sbSql.append("\n  TO_DATE('").append(funciones.ponerFechaSola(dto.getFecVenc())).append("', 'dd/MM/yyyy'), ");/*FEC_VEN*/
		    sbSql.append("\n  ").append(dto.getMinuto()).append(", ");/*MINUTO*/
		    sbSql.append("\n  ").append(dto.getImpuestoEsperado()).append(", ");/*IMP_ESPERADO*/
		    sbSql.append("\n  ").append(dto.getNoInstitucion()).append(", ");/*NO_INSTITUCION*/
		    sbSql.append("\n  ").append(dto.getDiasAnual()).append(", ");/*PLAZO_INV*/
		    sbSql.append("\n  ").append(dto.getFactorImpuesto()).append(", ");/*IMPUESTO*/
		    sbSql.append("\n  ").append(dto.getInteres()).append(", ");/*INTERES*/
		    sbSql.append("\n  '").append(dto.getMoneda()).append("', ");/*DIVISA*/
		    
		    if(dto.getInstitucion() != null && dto.getInstitucion().length() > 30)
		    	sbSql.append("\n  '").append(dto.getInstitucion().substring(0, 30)).append("', ");/*INSTITUCION*/
		    else
		    	sbSql.append("\n  '").append(dto.getInstitucion()).append("', ");/*INSTITUCION*/
		    
		    
		    List<RubroDto> rubros = this.consultarRubro(dto.getIdTipoValor());
		    RubroDto rubro = rubros.get(0);
		    
		    sbSql.append("\n  ").append(rubro.getIdGrupoInv()).append(", ");/*ID_GRUPO_INV*/
		    sbSql.append("\n  ").append(rubro.getIdRubroInv()).append(", ");/*ID_RUBRO_INV*/
		    
		    sbSql.append("\n  ").append(rubro.getIdGrupoReg()).append(", ");/*ID_GRUPO_REG*/
		    sbSql.append("\n  ").append(rubro.getIdRubroReg()).append(", ");/*ID_RUBRO_REG*/
		    
		    sbSql.append("\n  ").append(rubro.getIdGrupoInt()).append(", ");/*ID_GRUPO_INT*/
		    sbSql.append("\n  ").append(rubro.getIdRubroInt()).append(", ");/*ID_RUBRO_INT*/

		    sbSql.append("\n  ").append(rubro.getIdGrupoISR()).append(", ");/*ID_GRUPO_ISR*/
		    sbSql.append("\n  ").append(rubro.getIdRubroISR()).append(", ");/*ID_RUBRO_ISR*/

		    sbSql.append("\n  ").append(dto.getNoEmpresa()).append(", ");/*NO_EMPRESA*/
		    sbSql.append("\n  ").append(dto.getIdBanco()).append(", ");/*ID_BANCO*/
		    sbSql.append("\n  '").append(dto.getIdChequera()).append("', ");/*ID_CHEQUERA*/
		    sbSql.append("\n  ").append(dto.getIdBancoReg()).append(", ");/*ID_BANCO_REG*/
		    sbSql.append("\n  '").append(dto.getIdChequeraReg()).append("' ");/*ID_CHEQUERA_REG*/
		    
		    if(dto.getIdBancoInst() != null && 
		    		dto.getIdChequeraInst() != null && 
		    		!dto.getIdChequeraInst().equals("")){
			    sbSql.append("\n  ,").append(dto.getIdBancoInst()).append(", ");/*ID_BANCO_BENEF*/
			    sbSql.append("\n  '").append(dto.getIdChequeraInst()).append("'");/*ID_CHQ_BENEF*/
		    }		    
		    sbSql.append("\n  ) ");
		    
		    System.out.println(".:qry del inseert tmpvl ::::..."+sbSql.toString());
		    
		    iRegAfec = jdbcTemplate.update(sbSql.toString());
		
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:insertarReinversion");
		}
		return iRegAfec;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<OrdenInversionDto> consultarOrdenesInvPendientes(){
		List<OrdenInversionDto> lista = null;
		
		try{
			StringBuffer sbSql = new StringBuffer();
			
		    sbSql.append("\n SELECT FOLIO, FOLIO_SEQ, FECHA, HORA AS SIHORA, MONTO, ");
		    sbSql.append("\n EMISION_DE, EMISION_HASTA, TASA, NO_EMPRESA, ");
		    sbSql.append("\n ESTATUS, CONTRATO, PLAZO, GARANTIA, TIPO_PAPEL, ");
		    sbSql.append("\n CVE_CONTRATO, CONTACTO, FEC_VEN, MINUTO, ");
		    sbSql.append("\n IMP_ESPERADO, NO_INSTITUCION, PLAZO_INV, IMPUESTO, ");
		    sbSql.append("\n id_rubro_inv,no_empresa, ID_GRUPO_INV, ");
		    sbSql.append("\n INTERES, DIVISA, INSTITUCION,ID_BANCO,ID_CHEQUERA,ID_BANCO_REG, ");
		    sbSql.append("\n ID_CHEQUERA_REG,coalesce(ID_BANCO_BENEF,0) id_banco_benef, ");
		    sbSql.append("\n coalesce(ID_CHQ_BENEF,'0') id_chequera_benef, TIPO_VALOR, ");
		    sbSql.append("\n MONTO + INTERES - IMP_ESPERADO AS NETO");
		    sbSql.append("\n FROM TMP_INV_SIPO ");
		    sbSql.append("\n WHERE ESTATUS = 'P' ");
		    
		    lista = jdbcTemplate.query(sbSql.toString(),  new RowMapper(){
		    	public OrdenInversionDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		OrdenInversionDto dto = new OrdenInversionDto();
		    		
		    		dto.setFolio(rs.getInt("FOLIO"));
		    		dto.setFolioSeq(rs.getInt("FOLIO_SEQ"));
		    		dto.setFecAlta(rs.getDate("FECHA"));
		    		if(rs.getDate("FECHA") != null)
		    			dto.setsFecAlta(funciones.ponerFechaSola(rs.getDate("FECHA")));
		    		dto.setHora(rs.getInt("SIHORA"));
//		    		dto.setContraparte(rs.getString("CONTRAPARTE"));
		    		dto.setImporte(rs.getDouble("MONTO"));
//		    		dto.setInstrumento(rs.getString("INSTRUMENTO"));
		    		dto.setEmisionDe(rs.getInt("EMISION_DE"));
		    		dto.setDiasInvertir(rs.getInt("EMISION_HASTA"));
//		    		dto.setFechaValor(rs.getDate("FECHA_VALOR"));
		    		
		    		if(rs.getDate("FECHA") != null)
		    			dto.setsFechaValor(funciones.ponerFechaSola(rs.getDate("FECHA")));
		    		
		    		dto.setTasa(rs.getDouble("TASA"));
//		    		dto.setOrigen(rs.getString("ORIGEN"));
		    		dto.setEstatus(rs.getString("ESTATUS"));
		    		dto.setContrato(rs.getString("CONTRATO"));
		    		dto.setPlazo(rs.getInt("PLAZO"));
		    		dto.setGarantia(rs.getString("GARANTIA"));
		    		dto.setIdPapel(rs.getString("TIPO_PAPEL"));
		    		dto.setIdTipoValor(rs.getString("TIPO_VALOR"));
		    		dto.setCveContrato(rs.getInt("CVE_CONTRATO"));
		    		dto.setNoContacto(0);
		    		dto.setFecVenc(rs.getDate("FEC_VEN"));

		    		if(rs.getDate("FEC_VEN") != null)
		    			dto.setsFecVenc(funciones.ponerFechaSola(rs.getDate("FEC_VEN")));

		    		dto.setMinuto(rs.getInt("MINUTO"));
		    		dto.setImpuestoEsperado(rs.getDouble("IMP_ESPERADO"));
		    		dto.setNoInstitucion(rs.getInt("NO_INSTITUCION"));
		    		dto.setPlazo(rs.getInt("PLAZO_INV"));
		    		dto.setFactorImpuesto(rs.getDouble("IMPUESTO"));
		    		dto.setRubro(rs.getBigDecimal("id_rubro_inv"));
		    		dto.setGrupo(rs.getBigDecimal("ID_GRUPO_INV"));
		    		dto.setNoEmpresa(rs.getInt("no_empresa"));
		    		dto.setInteres(rs.getDouble("INTERES"));
		    		dto.setMoneda(rs.getString("DIVISA"));
		    		dto.setInstitucion(rs.getString("INSTITUCION"));
		    		
		    		dto.setIdBanco(rs.getInt("ID_BANCO"));
		    		dto.setIdChequera(rs.getString("ID_CHEQUERA"));
		    		
		    		dto.setIdBancoReg(rs.getInt("ID_BANCO_REG"));
		    		dto.setIdChequeraReg(rs.getString("ID_CHEQUERA_REG"));
		    		
		    		dto.setIdBancoInst(rs.getInt("id_banco_benef"));
		    		dto.setIdChequeraInst(rs.getString("id_chequera_benef"));
		    		
		    		dto.setNoEmpresa(rs.getInt("NO_EMPRESA"));
		    		
		    		dto.setNeto(rs.getDouble("NETO"));
		    		
		    		return dto;
		    	}
		    });
		    //System.out.println("El query es "+ sbSql.toString());
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarBancosCargo");
		}
		return lista;

	}
	
	public Integer insertarOrdenInversion(Integer folioSeqTpInvSipo, Integer usuario, Integer folio, String fechaHoy){
		StringBuffer sbSql = new StringBuffer();
		int iRegAfec = 0;
		try{
		    sbSql.append("\n INSERT INTO orden_inversion (  " );
		    sbSql.append("\n     deposito, retiro, tipo_inversion, no_empresa, no_orden, id_papel, id_tipo_valor, no_cuenta,  " );
		    sbSql.append("\n     no_institucion, no_contacto, no_contacto_v, fec_venc,  " );
		    sbSql.append("\n     hora, minuto, importe, interes, plazo, tasa, tasa_curva28,  " );
		    sbSql.append("\n     isr, usuario_alta, fec_alta, id_estatus_ord, dias_anual,  " );
		    sbSql.append("\n     tasa_isr, traspaso_ejecutado, b_garantia, importe_traspaso,  " );
		    sbSql.append("\n     b_autoriza, tipo_orden, nota,id_banco,id_chequera,id_banco_reg,");
		    sbSql.append("\n     id_chequera_reg,id_banco_benef,id_chequera_benef, DIVISA, " );
		    sbSql.append("\n  ID_GRUPO_INV, ID_RUBRO_INV,ID_GRUPO_REGR,ID_RUBRO_REGR, ");
		    sbSql.append("\n  ID_GRUPO_INT,ID_RUBRO_INT,ID_GRUPO_ISR,ID_RUBRO_ISR ");
		    sbSql.append("\n     ) " );
		    sbSql.append("\n SELECT 0, 0,'P', NO_EMPRESA, "+ folio +" , TIPO_PAPEL, TIPO_VALOR, CVE_CONTRATO,  " );
		    sbSql.append("\n NO_INSTITUCION, CONTACTO, CONTACTO, FEC_VEN, " );
		    sbSql.append("\n HORA,MINUTO, MONTO, interes, plazo, tasa,  " );
		    sbSql.append("\n round((power(((tasa/36000)*plazo)+1, (28/PLAZO))-1)*(36000/28),4), " );
		    sbSql.append("\n IMP_ESPERADO, "+usuario+" , FECHA, 'C', PLAZO_INV, " );
		    sbSql.append("\n IMPUESTO,NULL, GARANTIA, 0, 'S', 'E', 'NOTA', ID_BANCO, ID_CHEQUERA, " );
		    sbSql.append("\n ID_BANCO_REG, ID_CHEQUERA_REG,id_banco_benef,ID_CHQ_BENEF, DIVISA, " );
		    sbSql.append("\n  ID_GRUPO_INV, ID_RUBRO_INV,ID_GRUPO_REGR,ID_RUBRO_REGR, ");
		    sbSql.append("\n  ID_GRUPO_INT,ID_RUBRO_INT,ID_GRUPO_ISR,ID_RUBRO_ISR ");
		    sbSql.append("\n FROM TMP_INV_SIPO " );
		    sbSql.append("\n WHERE FOLIO_SEQ = "+ folioSeqTpInvSipo );
		    System.out.println("insertarOrdenInversion..::"+sbSql.toString());
		    iRegAfec = jdbcTemplate.update(sbSql.toString());
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:insertarReinversion");
		}
		return iRegAfec;
	}
	
	public Integer insertarEstadoCtaInv(Integer folioSeqTpInvSipo, Integer usuario, Integer folio, String fechaHoy){
		StringBuffer sbSql = new StringBuffer();
		int iRegAfec = 0;
		java.sql.Date fecha;
		try{
			
		    sbSql.append("\n INSERT INTO estado_cta_inv (  ");
		    sbSql.append("\n consecutivo, id_divisa,institucion,tipo_inversion,no_empresa, no_orden, id_papel, id_tipo_valor, no_cuenta,  ");
		    sbSql.append("\n no_institucion, no_contacto, no_contacto_v, fec_venc,  ");
		    sbSql.append("\n hora, minuto, importe, interes, plazo, tasa,  ");
		    sbSql.append("\n tasa_curva28,  ");
		    sbSql.append("\n isr, usuario_alta, fec_alta, id_estatus_ord, dias_anual,  ");
		    sbSql.append("\n tasa_isr, traspaso_ejecutado, b_garantia, importe_traspaso,  ");
		    sbSql.append("\n b_autoriza, tipo_orden, capital, deposito, retiro, inversion_del_dia, ");
		    sbSql.append("\n monto_total, tasas,nota) ");
		    sbSql.append("\n SELECT  ");
		    sbSql.append("\n 1, divisa, NO_INSTITUCION, 'P', NO_EMPRESA,"+ folio +", TIPO_PAPEL, TIPO_VALOR, CVE_CONTRATO, ");
		    sbSql.append("\n NO_INSTITUCION, CONTACTO, CONTACTO, FEC_VEN, ");
		    sbSql.append("\n HORA,MINUTO, MONTO, interes, plazo, tasa,  ");
		    sbSql.append("\n round((power(((tasa/36000)*plazo)+1, (28/PLAZO))-1)*(36000/28),2), ");
		    fecha = FuncionesSql.ponerFechaDate(fechaHoy);
		    sbSql.append("\n IMP_ESPERADO, "+ usuario +", '"+FuncionesSql.ponFechaDMY(fecha, false)+"', 'P', PLAZO_INV, ");
		    //sbSql.append("\n IMP_ESPERADO, "+ usuario +", TO_DATE('"+fechaHoy+"', 'dd/MM/yyyy'), 'P', PLAZO_INV, ");
		    
		    sbSql.append("\n IMPUESTO,NULL, GARANTIA, 0, ");
		    sbSql.append("\n 'N', 'E',MONTO, 0,0,MONTO, ");
		    sbSql.append("\n MONTO+INTERES, TASA, 'NOTA' ");
		    sbSql.append("\n from TMP_INV_SIPO ");
		    sbSql.append("\n WHERE FOLIO_SEQ = 10 ");
		    
		    iRegAfec = jdbcTemplate.update(sbSql.toString());
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:insertarReinversion");
		}
		return iRegAfec;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<OrdenInversionDto> consultarTmpOrdenInv(Integer folioSeq){
		List<OrdenInversionDto> lista = null;
		
		try{
			StringBuffer sbSql = new StringBuffer();
			
		    sbSql.append("\n SELECT FOLIO, FOLIO_SEQ, FECHA, HORA AS SIHORA, MONTO, ");
		    sbSql.append("\n EMISION_DE, EMISION_HASTA, TASA, ");
		    sbSql.append("\n ESTATUS, CONTRATO, PLAZO, GARANTIA, TIPO_PAPEL, ");
		    sbSql.append("\n TIPO_VALOR, CVE_CONTRATO, CONTACTO, FEC_VEN, MINUTO, ");
		    sbSql.append("\n IMP_ESPERADO, NO_INSTITUCION, PLAZO_INV, IMPUESTO, ");
		    sbSql.append("\n id_rubro_inv,no_empresa, ");
		    sbSql.append("\n INTERES, DIVISA, INSTITUCION,ID_BANCO,ID_CHEQUERA,ID_BANCO_REG, ");
		    sbSql.append("\n ID_CHEQUERA_REG,coalesce(ID_BANCO_BENEF,0) id_banco_benef, ");
		    sbSql.append("\n coalesce(ID_CHQ_BENEF,'0') id_chequera_benef, ID_RUBRO_INV, ");
		    sbSql.append("\n ID_GRUPO_INV ");
		    sbSql.append("\n FROM TMP_INV_SIPO ");
		    sbSql.append("\n WHERE FOLIO_SEQ = "+ folioSeq);
System.out.println("consultarTmpOrdenInv"+sbSql.toString());
		    lista = jdbcTemplate.query(sbSql.toString(),  new RowMapper(){
		    	public OrdenInversionDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		OrdenInversionDto dto = new OrdenInversionDto();
		    		
		    		dto.setFolio(rs.getInt("FOLIO"));
		    		dto.setFolioSeq(rs.getInt("FOLIO_SEQ"));
		    		dto.setFecAlta(rs.getDate("FECHA"));
		    		if(rs.getDate("FECHA") != null)
		    			dto.setsFecAlta(funciones.ponerFechaSola(rs.getDate("FECHA")));
		    		dto.setHora(rs.getInt("SIHORA"));
//		    		dto.setContraparte(rs.getString("CONTRAPARTE"));
		    		dto.setImporte(rs.getDouble("MONTO"));
//		    		dto.setInstrumento(rs.getString("INSTRUMENTO"));
		    		dto.setEmisionDe(rs.getInt("EMISION_DE"));
		    		dto.setDiasInvertir(rs.getInt("EMISION_HASTA"));
//		    		dto.setFechaValor(rs.getDate("FECHA_VALOR"));
		    		
		    		if(rs.getDate("FECHA") != null)
		    			dto.setsFechaValor(funciones.ponerFechaSola(rs.getDate("FECHA")));
		    		
		    		dto.setTasa(rs.getDouble("TASA"));
//		    		dto.setOrigen(rs.getString("ORIGEN"));
		    		dto.setEstatus(rs.getString("ESTATUS"));
		    		dto.setContrato(rs.getString("CONTRATO"));
		    		dto.setPlazo(rs.getInt("PLAZO"));
		    		dto.setGarantia(rs.getString("GARANTIA"));
		    		dto.setIdPapel(rs.getString("TIPO_PAPEL"));
		    		dto.setIdTipoValor(rs.getString("TIPO_VALOR"));
		    		dto.setCveContrato(rs.getInt("CVE_CONTRATO"));
		    		dto.setNoCuenta(rs.getInt("CVE_CONTRATO"));
		    		dto.setNoContacto(rs.getInt("CONTACTO"));
		    		dto.setFecVenc(rs.getDate("FEC_VEN"));

		    		if(rs.getDate("FEC_VEN") != null)
		    			dto.setsFecVenc(funciones.ponerFechaSola(rs.getDate("FEC_VEN")));

		    		dto.setMinuto(rs.getInt("MINUTO"));
		    		dto.setImpuestoEsperado(rs.getDouble("IMP_ESPERADO"));
		    		dto.setNoInstitucion(rs.getInt("NO_INSTITUCION"));
		    		dto.setPlazo(rs.getInt("PLAZO_INV"));
		    		dto.setFactorImpuesto(rs.getDouble("IMPUESTO"));
		    		dto.setRubro(rs.getBigDecimal("id_rubro_inv"));
//		    		dto.setSubRubro(rs.getInt("id_sub_rubro_inv"));
		    		dto.setNoEmpresa(rs.getInt("no_empresa"));
		    		dto.setInteres(rs.getDouble("INTERES"));
		    		dto.setMoneda(rs.getString("DIVISA"));
		    		dto.setInstitucion(rs.getString("INSTITUCION"));
		    		
		    		dto.setIdBanco(rs.getInt("ID_BANCO"));
		    		dto.setIdChequera(rs.getString("ID_CHEQUERA"));
		    		
		    		dto.setIdBancoReg(rs.getInt("ID_BANCO_REG"));
		    		dto.setIdChequeraReg(rs.getString("ID_CHEQUERA_REG"));
		    		
		    		dto.setIdBancoInst(rs.getInt("id_banco_benef"));
		    		dto.setIdChequeraInst(rs.getString("id_chequera_benef"));
		    		dto.setRubro(rs.getBigDecimal("ID_RUBRO_INV"));
		    		dto.setGrupo(rs.getBigDecimal("ID_GRUPO_INV"));
		    		
		    		return dto;
		    	}
		    });
		    //System.out.println("El query es "+ sbSql.toString());
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarBancosCargo");
		}
		return lista;
	}

	public Integer actualizarTmpInvSipo(Integer folioSeqTpInvSipo){
		StringBuffer sbSql = new StringBuffer();
		int iRegAfec = 0;
		try{
		    sbSql.append("\n UPDATE TMP_INV_SIPO ");
		    sbSql.append("\n SET ESTATUS = 'I' ");
		    sbSql.append("\n WHERE FOLIO_SEQ = "+ folioSeqTpInvSipo);
		    System.out.println("actualizarTmpInvSipo..::"+sbSql.toString());
		    iRegAfec = jdbcTemplate.update(sbSql.toString());
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:insertarReinversion");
		}
		return iRegAfec;
	}

	@SuppressWarnings("unchecked")
	public List<RubroDto> consultarRubroOrdenInversion(Integer noOrden){
		StringBuffer sbSql = new StringBuffer();
		List<RubroDto> listaRubros = null;
		
		try{
		
		    sbSql.append(" SELECT  ID_GRUPO_INV, ID_RUBRO_INV,ID_GRUPO_REGR,ID_RUBRO_REGR,  ");
		    sbSql.append(" ID_GRUPO_INT,ID_RUBRO_INT,ID_GRUPO_ISR,ID_RUBRO_ISR  ");
		    sbSql.append("\n From orden_inversion ");
		    sbSql.append("\n WHERE NO_ORDEN = "+ noOrden );
		    
		    listaRubros = jdbcTemplate.query(sbSql.toString(),  new RowMapper(){
		    	public RubroDto mapRow(ResultSet rs, int idx)throws SQLException{
		    		RubroDto dtoRubro = new RubroDto();

		    		dtoRubro.setIdGrupoInv(rs.getBigDecimal("ID_GRUPO_INV"));
		    		dtoRubro.setIdRubroInv(rs.getBigDecimal("ID_RUBRO_INV"));

		    		dtoRubro.setIdGrupoReg(rs.getBigDecimal("ID_GRUPO_REGR"));
		    		dtoRubro.setIdRubroReg(rs.getBigDecimal("ID_RUBRO_REGR"));

		    		dtoRubro.setIdGrupoInt(rs.getBigDecimal("ID_GRUPO_INT"));
		    		dtoRubro.setIdRubroInt(rs.getBigDecimal("ID_RUBRO_INT"));

		    		dtoRubro.setIdGrupoISR(rs.getBigDecimal("ID_GRUPO_ISR"));
		    		dtoRubro.setIdRubroISR(rs.getBigDecimal("ID_RUBRO_ISR"));

		    		return dtoRubro;
		    	}
		    });
		    //System.out.println("El query es "+ sbSql.toString());
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarBancosCargo");
		}
		return listaRubros;
	}

	@SuppressWarnings("unchecked")
	public Integer consultarFormaPagoContrato(Integer noCuenta, Integer noEmpresa){
		StringBuffer sbSql = new StringBuffer();
		List<Integer> listaFormas = null;
		
		try{
		
		    sbSql.append(" select valor_salida from cuenta where no_cuenta = "+ noCuenta + " and no_empresa = "+ noEmpresa);
		    System.out.println("consultarFormaPagoContrato"+sbSql.toString());
		    listaFormas = jdbcTemplate.query(sbSql.toString(),  new RowMapper(){
		    	public Integer mapRow(ResultSet rs, int idx)throws SQLException{
		    		Integer formaPago = rs.getInt("valor_salida");

		    		return formaPago;
		    	}
		    });
		    //System.out.println("El query es "+ sbSql.toString());
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarBancosCargo");
		}
		return listaFormas.get(0);
	}
	
	
	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.dao.InversionesDao#consultarEmpresaInversion(int)
	 */
	@Override
	public int consultarEmpresaInversion(int noOrden) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT no_empresa ");
		sbSql.append("FROM orden_inversion ");
		sbSql.append("WHERE no_orden = " + noOrden);
		
		return this.getJdbcTemplate().queryForInt(sbSql.toString());
	}

	/**
	 * FunSQLSelect577
	 * @param parametros
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> obtenerReporteVencimientoInversion(Map parametros){
		StringBuffer sbSql = new StringBuffer();
		List<Map<String, Object>> listOrd = null;
		try
		{
		    sbSql.append("\n select p.RAZON_SOCIAL, coalesce(d.calle_no, ' ') AS calle_no, ");
		    sbSql.append("\n coalesce(d.colonia, ' ') AS colonia, ");
		    sbSql.append("\n o.NO_CUENTA, p.NO_PERSONA, o.ID_CHEQUERA_BENEF as chequera_inst, ");
		    sbSql.append("\n bb.DESC_BANCO as banco_inst, ");
		    sbSql.append("\n coalesce(chb.DESC_SUCURSAL, ' ') as sucursal_inst,  ");
		    sbSql.append("\n o.ID_CHEQUERA_REG as chequera_reg, br.DESC_BANCO as banco_reg,  ");
		    sbSql.append("\n coalesce(chr.DESC_SUCURSAL, ' ') as sucursal_regreso, ");
		    if(ConstantesSet.gsDBM.equals("ORACLE")){
		    	sbSql.append("\n to_char(o.FEC_ALTA, 'dd/MM/yyyy') as fecha_inicio, ");
			    sbSql.append("\n to_char(o.FEC_VENC, 'dd/MM/yyyy') as fecha_venc, ");
			    sbSql.append("\n concat(concat(o.hora,':'),o.MINUTO) as hora, ");
		    } else {
		    	sbSql.append("\n CONVERT(varchar(10), o.FEC_ALTA, 103) as fecha_inicio, ");
			    sbSql.append("\n CONVERT(varchar(10), o.FEC_VENC, 103) as fecha_venc, ");
			    sbSql.append("\n cast(o.hora as varchar(5)) + ':' cast(o.MINUTO as varchar(5)) as hora, ");
		    }
		    
		    sbSql.append("\n o.id_papel as instrumento, o.TASA, o.PLAZO, ");
		    sbSql.append("\n o.IMPORTE, o.INTERES, m_int.importe - o.INTERES as ajute_int,  ");
		    sbSql.append("\n o.ISR, coalesce(m_isr.importe, 0)  - o.ISR as AJUSTE_ISR, ");
		    sbSql.append("\n o.IMPORTE + m_int.IMPORTE - coalesce(m_isr.importe, 0) AS TOTAL, ");
		    sbSql.append("\n  '"+ parametros.get("fechaHoy") + "' as HOY,");
		    sbSql.append("\n  e.RAZON_SOCIAL as nombreEmpresa");
		    sbSql.append("\n from orden_inversion o ");
		    sbSql.append("\n join persona p on  ");
		    if(ConstantesSet.gsDBM.equals("ORACLE"))
		    	sbSql.append("\n   (to_char(o.NO_INSTITUCION) = p.EQUIVALE_PERSONA) ");
		    else
		    	sbSql.append("\n   (cast(o.NO_INSTITUCION as varchar(15)) = p.EQUIVALE_PERSONA) ");
		    sbSql.append("\n join direccion d on ");
		    sbSql.append("\n   (p.NO_PERSONA = d.NO_PERSONA ");
		    sbSql.append("\n   and d.ID_TIPO_DIRECCION = 'OFNA') ");
		    sbSql.append("\n left join CAT_CTA_BANCO chb on  ");
		    sbSql.append("\n   (o.NO_EMPRESA = chb.NO_EMPRESA ");
		    sbSql.append("\n   and o.ID_BANCO_BENEF = chb.ID_BANCO ");
		    sbSql.append("\n   and o.ID_CHEQUERA_BENEF = chb.ID_CHEQUERA) ");
		    sbSql.append("\n left join CAT_BANCO bb on  ");
		    sbSql.append("\n   (o.ID_BANCO_BENEF = bb.ID_BANCO) ");
		    sbSql.append("\n left join CAT_CTA_BANCO chr on  ");
		    sbSql.append("\n   (o.NO_EMPRESA = chr.NO_EMPRESA ");
		    sbSql.append("\n   and o.ID_BANCO_BENEF = chr.ID_BANCO ");
		    sbSql.append("\n   and o.ID_CHEQUERA_BENEF = chr.ID_CHEQUERA) ");
		    sbSql.append("\n join CAT_BANCO br on  ");
		    sbSql.append("\n   (o.ID_BANCO_REG = br.ID_BANCO) ");
		    sbSql.append("\n join movimiento m_int on  ");
		    if(ConstantesSet.gsDBM.equals("ORACLE"))
		    	sbSql.append("\n   (to_number(o.no_orden) = m_int.no_docto ");
		    else
		    	sbSql.append("\n   (cast(o.no_orden as integer) = m_int.no_docto ");
		    
		    sbSql.append("\n   and m_int.id_tipo_operacion = 4103) ");
		    sbSql.append("\n   join persona e on ");
    		sbSql.append("\n   (o.NO_EMPRESA = e.NO_PERSONA) ");
		    sbSql.append("\n   left join movimiento m_isr on  ");
		    if(ConstantesSet.gsDBM.equals("ORACLE"))
		    	sbSql.append("\n   (to_number(o.no_orden) = m_isr.no_docto ");
		    else
		    	sbSql.append("\n   (cast(o.no_orden as integer) = m_isr.no_docto ");
		    sbSql.append("\n   and m_isr.id_tipo_operacion = 4104) ");
		    sbSql.append("\n where o.NO_ORDEN = "+ parametros.get("noDocto").toString());
		    
		    //System.out.println("El query para el REPORTE es "+ sbSql.toString());

		    listOrd = jdbcTemplate.query(sbSql.toString(), new RowMapper(){
		    	public Map mapRow(ResultSet rs, int idx)throws SQLException{
		    		Map<String, Object> mapCons = new HashMap<String, Object>();
	    			mapCons.put("nomreInstitucion", rs.getString("RAZON_SOCIAL"));
	    			mapCons.put("calleNo", rs.getString("calle_no"));
	    			mapCons.put("colonia", rs.getString("colonia"));
	    			mapCons.put("hoy", rs.getString("HOY"));
	    			mapCons.put("noCuenta", rs.getString("NO_CUENTA"));
	    			mapCons.put("chequeraBenef", rs.getString("chequera_inst"));
	    			mapCons.put("bancoBenef", rs.getString("banco_inst"));
	    			mapCons.put("sucursalBenef", rs.getString("sucursal_inst"));
	    			mapCons.put("bancoReg", rs.getString("banco_reg"));
	    			mapCons.put("chequeraReg", rs.getString("chequera_reg"));
	    			mapCons.put("sucursalReg", rs.getString("sucursal_regreso"));
	    			mapCons.put("hora", rs.getString("hora"));
	    			mapCons.put("inicio", rs.getString("fecha_inicio"));
	    			mapCons.put("vencimiento", rs.getString("fecha_venc"));
	    			mapCons.put("instrumento", rs.getString("instrumento"));
	    			mapCons.put("liquidacion", "TRASPASO");
	    			mapCons.put("tasa", rs.getString("TASA"));
	    			mapCons.put("plazo", rs.getString("PLAZO"));
	    			mapCons.put("notas", "NOTAS");
	    			mapCons.put("monto", rs.getString("IMPORTE"));
	    			mapCons.put("interes", rs.getString("INTERES"));
	    			mapCons.put("ajusteInteres", rs.getString("ajute_int"));
	    			mapCons.put("comision", "0");
	    			mapCons.put("impuesto", rs.getString("ISR"));
	    			mapCons.put("ajusteImpuesto", rs.getString("AJUSTE_ISR"));
	    			mapCons.put("total", rs.getString("TOTAL"));
	    			mapCons.put("nombreEmpresa", rs.getString("nombreEmpresa"));

		    			return mapCons;
		    	}
		    });
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarOrdenInversionReport");
		}
		
		//System.out.println("El tamaño de la lista es "+listOrd.size());
		return listOrd;
	}
	
	@SuppressWarnings("unchecked")
	public Integer consultarNoClinente(Integer noCuenta){
		StringBuffer sbSql = new StringBuffer();
		List<Integer> listaFormas = null;
		
		try{
		
		    sbSql.append(" select p.no_persona ");
		    sbSql.append(" from CUENTA c  ");
		    sbSql.append(" join persona p on  ");
		    if(ConstantesSet.gsDBM.equals("ORACLE"))
		    	sbSql.append("   (to_char(c.NO_INSTITUCION) = p.EQUIVALE_PERSONA) ");
		    else
		    	sbSql.append("   (cast(c.NO_INSTITUCION as varchar(15)) = p.no_persona) ");
		    	//sbSql.append("   (cast(c.NO_INSTITUCION as varchar(15)) = p.EQUIVALE_PERSONA) ");
		    sbSql.append(" where NO_CUENTA = "+ noCuenta);
System.out.println("consultarNoClinente"+sbSql.toString());
		    listaFormas = jdbcTemplate.query(sbSql.toString(),  new RowMapper(){
		    	public Integer mapRow(ResultSet rs, int idx)throws SQLException{
		    		Integer formaPago = rs.getInt("no_persona");

		    		return formaPago;
		    	}
		    });
		    //System.out.println("El query es "+ sbSql.toString());
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:consultarBancosCargo");
		}
		return listaFormas.get(0);
	}

	
	public Integer actualizarTmpInvSipo(OrdenInversionDto dto){
		StringBuffer sbSql = new StringBuffer();
		int iRegAfec = 0;
		try{
			
//		    sbSql.append("\n  FOLIO, FOLIO_SEQ, FECHA, HORA, MONTO,  ");
//		    sbSql.append("\n  EMISION_DE, EMISION_HASTA, TASA,  ");
//		    sbSql.append("\n  ESTATUS, CONTRATO, PLAZO, GARANTIA, TIPO_PAPEL,  ");
//		    sbSql.append("\n  TIPO_VALOR, CVE_CONTRATO, CONTACTO, FEC_VEN, MINUTO,  ");
//		    sbSql.append("\n  IMP_ESPERADO, NO_INSTITUCION, PLAZO_INV, IMPUESTO,  ");
//		    sbSql.append("\n  INTERES, DIVISA, INSTITUCION  ");
//		    sbSql.append("\n  ,ID_GRUPO_INV, ID_RUBRO_INV,ID_GRUPO_REGR,ID_RUBRO_REGR, ");
//		    sbSql.append("\n  ID_GRUPO_INT,ID_RUBRO_INT,ID_GRUPO_ISR,ID_RUBRO_ISR, ");
//		    sbSql.append("\n  NO_EMPRESA,ID_BANCO,ID_CHEQUERA,ID_BANCO_REG,ID_CHEQUERA_REG" );

			Integer diasInvertir = new Funciones().diasEntreFechas(dto.getFecAlta(), dto.getFecVenc());
			
			sbSql.append("\n UPDATE TMP_INV_SIPO ");
		    sbSql.append("\n SET MONTO = "+ dto.getImporte());
		    sbSql.append("\n , TASA = "+ dto.getTasa());
		    sbSql.append("\n , EMISION_HASTA = "+ diasInvertir);
		    sbSql.append("\n , IMPUESTO = "+ dto.getFactorImpuesto());
		    sbSql.append("\n , INTERES = "+ dto.getInteres());
		    sbSql.append("\n , IMP_ESPERADO = "+ dto.getImpuestoEsperado());
		    
		    sbSql.append("\n , FEC_VEN = '"+ FuncionesSql.ponFechaDMY(new java.sql.Date(dto.getFecVenc().getTime()), false) + "'");
		    sbSql.append("\n , FECHA = '"+ FuncionesSql.ponFechaDMY(new java.sql.Date(dto.getFecAlta().getTime()), false) + "'");
		    //sbSql.append("\n , FEC_VEN = TO_DATE('"+ funciones.ponerFechaSola(dto.getFecVenc()) + "', 'dd/MM/yyyy')");
		    //sbSql.append("\n , FECHA = TO_DATE('"+ funciones.ponerFechaSola(dto.getFecAlta()) + "', 'dd/MM/yyyy')");
		    sbSql.append("\n WHERE FOLIO_SEQ = "+ dto.getFolioSeq());
		    
		    //System.out.println("El query es "+ sbSql.toString());
		    
		    iRegAfec = jdbcTemplate.update(sbSql.toString());
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:insertarReinversion");
		}
		return iRegAfec;
	}

	public Integer eliminarTmpInvSipo(Integer folioSeqTpInvSipo){
		StringBuffer sbSql = new StringBuffer();
		int iRegAfec = 0;
		try{
		    sbSql.append("\n UPDATE TMP_INV_SIPO ");
		    sbSql.append("\n SET ESTATUS = 'X' ");
		    sbSql.append("\n WHERE FOLIO_SEQ = "+ folioSeqTpInvSipo);
		    
		    iRegAfec = jdbcTemplate.update(sbSql.toString());
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:insertarReinversion");
		}
		return iRegAfec;
	}
	
	public String obtenerNoCliente(String noDocto) {
		StringBuffer sbSql = new StringBuffer();

		sbSql.append("SELECT b.no_persona ");
		sbSql.append("FROM orden_inversion a JOIN persona b ON (a.no_institucion = b.equivale_persona) ");
		sbSql.append("WHERE no_orden = '"+ noDocto + "' ");
		
		try {
			return jdbcTemplate.queryForObject(sbSql.toString(), String.class);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " "+ Bitacora.getStackTrace(e) + 
					"P:Inversiones, C:InversionesDaoImpl, M:seleccionarFolioReal");
			return "0";
		}
	}

	@Override
	public List<Map<String, Object>> obtenerInversionesLiqVencidas(Integer usuario, Integer empresa, Integer institucion, String fechaInicial, String fechaFinal){
		StringBuffer sbSql = new StringBuffer();
		List<Map<String, Object>> resultado = null;
		
		try{
			/*
			Falta de presentar los campos de : 
				Nombre del contrato , ok
				Fecha de registro, ok
				interés bruto, ok
				impuesto retenido, ok
				el nombre de Razón Social, debe ser Institución Financiera
			*/
			
			sbSql.append("\n SELECT o.ID_ESTATUS_ORD as idEstatusOrd, A.PO_HEADERS as poHeaders,");
			sbSql.append("\n   p.RAZON_SOCIAL as razonSocial, o.no_orden as noOrden, ");
			if(ConstantesSet.gsDBM.equals("ORACLE"))
				sbSql.append("\n   to_char(o.FEC_ALTA,'dd/MM/yyyy') as FEC_ALTA,");
			else
				sbSql.append("\n   CONVERT(varchar(10), o.FEC_ALTA, 103) as  FEC_ALTA,");
			sbSql.append("\n   o.FEC_VENC - o.FEC_ALTA plazo,");
			sbSql.append("\n   o.IMPORTE importe, o.TASA as tasa, ");
			sbSql.append("\n   o.INTERES - o.ISR as interesNeto,");
			sbSql.append("\n   o.ISR as isr, o.INTERES as interes,");
			sbSql.append("\n   o.IMPORTE - o.ISR + o.INTERES as importeNeto, cta.desc_cuenta, ");
			sbSql.append("\n   CASE WHEN id_tipo_operacion = 4102 THEN 'VENCIMIENTO' ELSE 'INVERSION' END  AS tipoopera");
			
			sbSql.append("\n FROM movimiento A, orden_inversion o,");
			sbSql.append("\n USUARIO_EMPRESA ue, persona p, cuenta cta");
			sbSql.append("\n WHERE  A.id_tipo_operacion IN (3200, 4001, 4102) ");
			if(ConstantesSet.gsDBM.equals("ORACLE"))
				sbSql.append("\n   And to_number(a.no_docto) = o.no_orden ");
			else
				sbSql.append("\n   And cast(a.no_docto as integer) = o.no_orden ");
			
			sbSql.append("\n   And ue.no_usuario = "+ usuario);
			sbSql.append("\n   And a.no_empresa = ue.no_empresa ");
			sbSql.append("\n   And o.no_empresa = cta.no_empresa ");
			sbSql.append("\n   And o.no_cuenta = cta.no_cuenta ");
			sbSql.append("\n   And a.no_empresa = "+ empresa);
			sbSql.append("\n   And A.origen_mov in ('INV', 'SET') ");
			sbSql.append("\n   AND o.NO_INSTITUCION = p.EQUIVALE_PERSONA");
			sbSql.append("\n   AND o.NO_INSTITUCION = "+ institucion);
			sbSql.append("\n   AND ((O.ID_ESTATUS_ORD = 'A' ");
			
			
			if(ConstantesSet.gsDBM.equals("ORACLE")){
				sbSql.append("\n       AND o.FEC_ALTA >= TO_DATE('"+ fechaInicial +"','dd/MM/yyyy')");
				sbSql.append("\n       AND o.FEC_ALTA < TO_DATE('"+ fechaFinal +"','dd/MM/yyyy')+1)");
			}
			else {
				sbSql.append("\n       AND o.FEC_ALTA >= '"+ fechaInicial +"'");
				sbSql.append("\n       AND o.FEC_ALTA < datedif(d,1,'" + fechaFinal + "'),");
			}
			
			//sbSql.append("\n       AND o.FEC_ALTA >= TO_DATE('"+ fechaInicial +"','dd/MM/yyyy')");
			//sbSql.append("\n       AND o.FEC_ALTA < TO_DATE('"+ fechaFinal +"','dd/MM/yyyy')+1)");
			sbSql.append("\n     OR (O.ID_ESTATUS_ORD = 'V'");
			if(ConstantesSet.gsDBM.equals("ORACLE")){
				sbSql.append("\n       AND o.FEC_VENC >= TO_DATE('"+ fechaInicial +"','dd/MM/yyyy')");
				sbSql.append("\n       AND o.FEC_VENC < TO_DATE('"+ fechaFinal +"','dd/MM/yyyy')+1))");
			}
			else {
				sbSql.append("\n       AND o.FEC_VENC >= '"+ fechaInicial +"'");
				sbSql.append("\n       AND o.FEC_VENC < datedif(d,1,'" + fechaFinal + "'),");
			}
			sbSql.append("\n ORDER BY A.fec_valor, A.no_docto, A.id_tipo_operacion ");
		    
		    //
			
			System.out.println("El query es "+ sbSql.toString());
		    
		    resultado = jdbcTemplate.queryForList(sbSql.toString());
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:insertarReinversion");
		}
		return resultado;
	}

	@Override
	public List<Map<String, Object>> obtenerInversionesVigentes(Integer usuario, Integer empresa){
		StringBuffer sbSql = new StringBuffer();
		List<Map<String, Object>> resultado = null;
		
		try{
			
			sbSql.append("\n  SELECT p.RAZON_SOCIAL, o.ID_CHEQUERA, ");
			if(ConstantesSet.gsDBM.equals("ORACLE")) {
				sbSql.append("\n  o.NO_ORDEN, o.IMPORTE, to_char(o.FEC_ALTA,'dd/MM/yyyy') FEC_ALTA, ");
				sbSql.append("\n  to_char(o.FEC_VENC,'dd/MM/yyyy') FEC_VENC, o.FEC_VENC-o.fec_alta plazo, ");
			} else {
				sbSql.append("\n  o.NO_ORDEN, o.IMPORTE, CONVERT(varchar(10), o.FEC_ALTA, 103) as FEC_ALTA, ");
				sbSql.append("\n  CONVERT(varchar(10), o.FEC_VENC, 103) as FEC_VENC, o.FEC_VENC-o.fec_alta plazo, ");
			}
			sbSql.append("\n  o.tasa, o.INTERES, o.isr, ");
			sbSql.append("\n  o.INTERES - o.ISR interes_neto, ");
			sbSql.append("\n  o.IMPORTE + o.INTERES - o.ISR monto_neto, ");
			sbSql.append("\n  o.ID_ESTATUS_ORD, o.NO_EMPRESA,  ");
			sbSql.append("\n  i.razon_social institucion, ");
			sbSql.append("\n  i.EQUIVALE_PERSONA no_institucion ");
			sbSql.append("\n  FROM orden_inversion o,  ");
			sbSql.append("\n  USUARIO_EMPRESA ue, persona p, ");
			sbSql.append("\n  persona i ");
			sbSql.append("\n 	WHERE  o.ID_ESTATUS_ORD = 'A' ");
			sbSql.append("\n  And ue.no_usuario = "+ usuario);
			
			if(empresa != null && empresa > 0 )
				sbSql.append("\n  And o.no_empresa = "+ empresa);
			
			sbSql.append("\n  And o.no_empresa = ue.no_empresa  ");
			sbSql.append("\n  and o.no_empresa = p.no_empresa ");
			sbSql.append("\n  and o.NO_INSTITUCION = i.EQUIVALE_PERSONA ");
			sbSql.append("\n ORDER BY i.razon_social  ");
		    
		    System.out.println("El query es "+ sbSql.toString());
		    
		    resultado = jdbcTemplate.queryForList(sbSql.toString());
			
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:Inversiones, C:InversionesDaoImpl, M:insertarReinversion");
		}
		return resultado;
	}
	
	public String configuraSet(int indice){
		try {
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		} catch (Exception e) {
			//System.out.println("Error en: configuraSet");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: configuraSet, M: llenaComboArchivos");
		}return consultasGenerales.consultarConfiguraSet(indice);
		
	}
	
	public Map<String, Object> insertaBitacoraPoliza(DT_Polizas_ResponseResponse[]  dt_Polizas_ResponseResponse){
		Map<String, Object> mapReturn= new HashMap<String, Object>();
		mapReturn.put("estatus", false);
		mapReturn.put("mensaje", "Error desconocido.");
		try {
			for (int i = 0; i < dt_Polizas_ResponseResponse.length; i++) {
				StringBuffer sqlInsert = new StringBuffer();
				sqlInsert.append("Insert Into BITACORA_POLIZAS Values( '");
				sqlInsert.append(dt_Polizas_ResponseResponse[i].getNO_EMPRESA());
				sqlInsert.append("','");
				sqlInsert.append(dt_Polizas_ResponseResponse[i].getNO_DOC_SAP());
				sqlInsert.append("','");
				sqlInsert.append(dt_Polizas_ResponseResponse[i].getSECUENCIA());
				sqlInsert.append("','");
				sqlInsert.append(dt_Polizas_ResponseResponse[i].getID_POLIZA_SAP());
				sqlInsert.append("','");
				sqlInsert.append(dt_Polizas_ResponseResponse[i].getMENSAJE());
				sqlInsert.append("', sysdate)");
				
				if(jdbcTemplate.update(sqlInsert.toString())!=0){
					if (dt_Polizas_ResponseResponse[i].getMENSAJE() != null &&
							!dt_Polizas_ResponseResponse[i].getMENSAJE().equalsIgnoreCase("null")&&
							!dt_Polizas_ResponseResponse[i].getMENSAJE().equalsIgnoreCase(" ") &&
							dt_Polizas_ResponseResponse[i].getMENSAJE().equalsIgnoreCase("El documento ha sido Anulado")) {
						
						mapReturn.put("estatus", true);
						
					} else{
						if (dt_Polizas_ResponseResponse[i].getMENSAJE() != null &&
								!dt_Polizas_ResponseResponse[i].getMENSAJE().equalsIgnoreCase("null")&&
								!dt_Polizas_ResponseResponse[i].getMENSAJE().equalsIgnoreCase(" ")) {
							mapReturn.put("mensaje", dt_Polizas_ResponseResponse[i].getMENSAJE());
							
						}else{
							mapReturn.put("mensaje", "Sin mensaje de respuesta.");
						}
					}
						
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Egresos, C:ExportacionPolizasDaoImpl, M:insertaBitacoraPoliza");
		}
		return mapReturn;
	}
	
	@Override
	public List<DT_Polizas_OBPolizas> obtieneListaCancelar(int iNoOrden, String estatus, int empresa) {
		
		StringBuffer sql = new StringBuffer();
		List<DT_Polizas_OBPolizas> dt_Polizas_OBPolizas = new ArrayList<>();
		try {
			
			
			
			if (estatus.equals("A")) {
				if(ConstantesSet.gsDBM.equals("ORACLE"))
					sql.append(" select no_empresa, po_headers, min(to_char(fec_valor,'DD/MM/YYYY')) as fec_valor");
				else
					sql.append(" select no_empresa, po_headers, min(CONVERT(varchar(10), fec_valor, 103)) as fec_valor");
				sql.append("\n	from movimiento");
				sql.append("\n	where no_docto='");
				sql.append(iNoOrden+"'");
				sql.append("\n and no_empresa = "+empresa);
				sql.append("group by no_empresa, po_headers");
			} else {
				/*List<String> resDoc = new ArrayList<>();
				StringBuffer sqlDoc = new StringBuffer();
				sqlDoc.append(" select distinct no_docto ");
				sqlDoc.append("\n	from movimiento");
				sqlDoc.append("\n	no_docto=");
				sqlDoc.append(iNoOrden);
				
				resDoc= jdbcTemplate.query(sqlDoc.toString(),  new RowMapper<String>(){
			    	public String mapRow(ResultSet rs, int idx)throws SQLException{
			    		String formaPago = rs.getString("no_docto");

			    		return formaPago;
			    	}
			    });
				*/
				if(ConstantesSet.gsDBM.equals("ORACLE"))
					sql.append(" select distinct no_empresa, po_headers, min(to_char(fec_valor,'DD/MM/YYYY')) as fec_valor");
				else
					sql.append(" select distinct no_empresa, po_headers, min(CONVERT(varchar(10), fec_valor, 103)) as fec_valor");
				
				sql.append("\n	from movimiento");
				sql.append("\n	where no_docto='");
				sql.append(iNoOrden+"'");
				sql.append("\n and no_empresa = "+empresa);
				sql.append("\n	and ORIGEN_MOV = 'INV' ");
				sql.append("\n	and id_tipo_operacion in(3200,4001,4102)");
				sql.append("group by no_empresa, po_headers");
				
			}
			sql.append("\n	");
			
			
			dt_Polizas_OBPolizas= jdbcTemplate.query(sql.toString(),  new RowMapper<DT_Polizas_OBPolizas>(){
		    	public DT_Polizas_OBPolizas mapRow(ResultSet rs, int idx)throws SQLException{
		    		DT_Polizas_OBPolizas dt_Polizas_OBPolizas = new DT_Polizas_OBPolizas();
		    		dt_Polizas_OBPolizas.setNO_EMPRESA(rs.getString("no_empresa")!= null ? funciones.ajustarLongitudCampo(rs.getString("no_empresa"), 4, "D", "", "0"):"");
					dt_Polizas_OBPolizas.setFEC_VALOR(rs.getString("fec_valor"));
					dt_Polizas_OBPolizas.setNO_DOC_SAP(rs.getString("po_headers"));
					dt_Polizas_OBPolizas.setID_POLIZA("000");
					
					dt_Polizas_OBPolizas.setID_BANCO("000");
					dt_Polizas_OBPolizas.setIMP_PAGO("000");
					dt_Polizas_OBPolizas.setIMP_USA("000");
					dt_Polizas_OBPolizas.setID_DIVISA("000");
					dt_Polizas_OBPolizas.setNO_FOLIO_SET("000");
					dt_Polizas_OBPolizas.setFORMA_PAGO("T");
					dt_Polizas_OBPolizas.setREFERENCIA("000");
					dt_Polizas_OBPolizas.setID_DIVISA_ORIGINAL("000");
					dt_Polizas_OBPolizas.setTIPO_CAMB("000");
					dt_Polizas_OBPolizas.setDIVISION("000");
					dt_Polizas_OBPolizas.setID_CHEQUE("000");
					dt_Polizas_OBPolizas.setFEC_VALOR(rs.getString("fec_valor"));
					dt_Polizas_OBPolizas.setFEC_FACTURA(rs.getString("fec_valor"));
					dt_Polizas_OBPolizas.setCONCEPTO("000");
					dt_Polizas_OBPolizas.setNO_PERSONA("000");
					dt_Polizas_OBPolizas.setTIPO_OPERACION("000");
					dt_Polizas_OBPolizas.setTIPO_MOVTO("000");
					dt_Polizas_OBPolizas.setCHEQUERA_INV("000");
					dt_Polizas_OBPolizas.setDIVISION("000");
					dt_Polizas_OBPolizas.setCAPITAL("000");
					dt_Polizas_OBPolizas.setCARGO("000");
					dt_Polizas_OBPolizas.setABONO("000");
					dt_Polizas_OBPolizas.setASIGNACION("000");
					dt_Polizas_OBPolizas.setTIPO_CAMB("000");
					dt_Polizas_OBPolizas.setIMP_MES_NVO("000");
					
		    		return dt_Polizas_OBPolizas;
		    	}
		    });
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " +
			Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosBusiness, M: regenerar");
		}
		return dt_Polizas_OBPolizas;
	}

	@Override
	public String consultarCajas() {
		String resultado="";
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto> listListas = new ArrayList<LlenaComboGralDto>();
		try {
			sb.append("  select valor from configura_set where indice=5004 ");
	
	System.out.println(sb.toString());
			System.out.println("query"+sb.toString());
			listListas = jdbcTemplate.query(sb.toString(), new RowMapper(){
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
					dtoCons.setDescripcion(rs.getString("valor"));

					return dtoCons;
					
				}
			});
	
		} catch (Exception e) {
			resultado= "Error al validar el componente ";
		}
		
	for (LlenaComboGralDto l : listListas) {
		resultado=l.getDescripcion();
	}
		return resultado;
	}

}

