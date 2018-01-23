package com.webset.set.utilerias.dao.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.xmlbeans.impl.xb.xsdschema.impl.FullDerivationSetImpl;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.egresos.dto.ConsultaPropuestasDto;
import com.webset.set.egresos.dto.PagosPropuestosDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dao.ReglasNegocioDao;
import com.webset.set.utilerias.dto.AcreedorDoctoDto;
import com.webset.set.utilerias.dto.CondicionPagoDto;
import com.webset.set.utilerias.dto.DoctoTesoreriaDto;
import com.webset.set.utilerias.dto.EmpresaRegNegDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.PlanPagosDto;
import com.webset.set.utilerias.dto.ReglasNegocioDto;
import com.webset.set.utilerias.dto.RubroRegNeg;
import com.webset.set.utilerias.dto.SeleccionAutomaticaGrupoDto;
import com.webset.utils.tools.StoredProcedure;
import com.webset.utils.tools.Utilerias;

/** 
 * @author Eric Medina
 * @fecha_creacion: 14/10/2015
 */

public class ReglasNegocioDaoImpl implements ReglasNegocioDao {
 
	JdbcTemplate jdbcTemplate;
	private Bitacora bitacora = new Bitacora();
	private ConsultasGenerales consultasGenerales;
	private Funciones funciones = new Funciones();
	
	@Override
	public List<ReglasNegocioDto> obtenerRelacion() {
		
		StringBuffer sql= new StringBuffer();
		List<ReglasNegocioDto> list = new ArrayList<ReglasNegocioDto>();
		
		try{
			
            sql.append("\n SELECT RN.ID_REGLA, RN.REGLA_NEGOCIO, RN.TIPO_REGLA, ");
            sql.append("\n RN.FECHA_CAPTURA, RN.USUARIO_CAPTURA, RN.GEN_PROP_AUTO, ");
            sql.append("\n coalesce((select clave_usuario from seg_usuario where id_usuario = RN.USUARIO_CAPTURA), '') as CLAVE_USU, ");
            sql.append("\n RN.USUARIOS, RN.INDICADOR, RN.DESCUENTO, rn.long_chequera ");
            sql.append("\n FROM REGLA_NEGOCIO RN");
            sql.append("\n WHERE ESTATUS = 'A' ");
            sql.append("\n ORDER BY RN.REGLA_NEGOCIO ");
            
	        System.out.println("Regla negocios: " + sql.toString());
	        
	        list = jdbcTemplate.query(sql.toString(), new RowMapper<ReglasNegocioDto>(){
				public ReglasNegocioDto mapRow(ResultSet rs, int idx) throws SQLException {
					ReglasNegocioDto cons = new ReglasNegocioDto();
					cons.setIdRegla(rs.getInt("ID_REGLA"));
					cons.setReglaNegocio(rs.getString("REGLA_NEGOCIO"));
					cons.setTipoRegla(rs.getString("TIPO_REGLA"));
					cons.setFechaCaptura(rs.getString("FECHA_CAPTURA"));
					cons.setUsuarioCaptura(rs.getInt("USUARIO_CAPTURA"));
					cons.setClaveUsuarioCaptura(rs.getString("CLAVE_USU"));
					cons.setGeneraPropuestaAutomatica(rs.getString("GEN_PROP_AUTO"));
					cons.setUsuarios(rs.getString("USUARIOS"));
					cons.setIndicador(rs.getString("INDICADOR"));
					cons.setDescuento(rs.getString("DESCUENTO"));
					cons.setLongChequera(rs.getInt("long_chequera"));
					return cons;
				}});
            
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDao, M:obtenerRelacion");
		}
		return list;
		
	}

	/**********************************************************************/
	public void setDataSource(DataSource dataSource){
		try
		{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ReglasNegocioDaoImpl, M:setDataSource");
		}
	}
	/*************************************************************************/

	@Override
	public List<EmpresaRegNegDto> obtenerEmpresas(int idUsuario, boolean bExiste, int idRegla) {
		
		StringBuffer sql = new StringBuffer(); 
		
		//bExiste == true -> Indica que es el stored para las empresas asignadas.
		//bExiste == false -> Indica que es el stored para las empresas sin asignar.
		if(bExiste){
			sql.append("\n SELECT e.no_empresa, e.nom_empresa, ern.hora_limit_oper ");
			sql.append("\n FROM empresa e LEFT JOIN empresa_reg_neg ern on e.no_empresa = ern.no_empresa ");
			sql.append("\n JOIN regla_negocio rn on ern.id_regla = rn.id_regla");
			sql.append("\n WHERE rn.id_regla = "+ Utilerias.validarCadenaSQL(idRegla) +" ");
		}else{
			sql.append("\n SELECT e.no_empresa, e.nom_empresa, '' as hora_limit_oper ");
			sql.append("\n FROM empresa e  ");
			sql.append("\n WHERE e.no_empresa IN (SELECT distinct no_empresa FROM USUARIO_EMPRESA WHERE no_usuario = " + Utilerias.validarCadenaSQL(idUsuario) + " ) ");
			sql.append("\n AND e.NO_EMPRESA NOT IN ( ");
			sql.append("\n 							SELECT distinct ern2.no_empresa ");
			sql.append("\n 							FROM EMPRESA_REG_NEG ern2 JOIN regla_negocio rn2 on ern2.id_regla = rn2.id_regla ");
			sql.append("\n 							WHERE rn2.id_regla = "+Utilerias.validarCadenaSQL(idRegla)+") ");
			sql.append("\n ORDER BY e.nom_empresa");	
			
		}
		
		List<EmpresaRegNegDto> empresas=new ArrayList<EmpresaRegNegDto>();
		
		try{
			 empresas = jdbcTemplate.query(sql.toString(), new RowMapper<EmpresaRegNegDto>(){
				public EmpresaRegNegDto mapRow(ResultSet rs, int idx) throws SQLException {
					EmpresaRegNegDto empresa = new EmpresaRegNegDto();
					empresa.setNoEmpresa(rs.getInt("no_empresa"));
					empresa.setNombreEmpresa(rs.getString("nom_empresa"));
					empresa.setHoraLimiteOperacion(rs.getString("hora_limit_oper"));
					return empresa;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDao, M:obtenerEmpresas");
		}
		return empresas;
	}

	@Override
	public List<AcreedorDoctoDto> obtenerAcreedorDocumento(boolean bTipoAcre, int idRegla, String clasificacion) {
		
		StringBuffer sql = new StringBuffer(); 
		
		sql.append("\n SELECT ac.id_acre, ac.de_acre, ac.a_acre, ac.clasificacion_acre, ac.tipo_acre ");
		sql.append("\n FROM ACRE_DOCTO_REG_NEG ac ");
		sql.append("\n WHERE ac.id_regla = "+ Utilerias.validarCadenaSQL(idRegla) +" ");
		
		if(clasificacion.equals("A")){
			sql.append("\n AND CLASIFICACION_ACRE = 'A' ");
		}else{
			sql.append("\n AND CLASIFICACION_ACRE = 'D' ");
		}
		
		//bTipoAcre == true -> Indica que es el stored para los acreedores/documentos incluidos.
		//bTipoAcre == false -> Indica que es el stored para los acreedores/documentos excluidos.
		if(bTipoAcre){
			sql.append("\n AND TIPO_ACRE = 'I' ");
		}else{
			sql.append("\n AND TIPO_ACRE = 'E' ");	
		}

		sql.append("\n ORDER BY ac.id_acre ");	
		
		List<AcreedorDoctoDto> acreedores=new ArrayList<AcreedorDoctoDto>();
		
		try{
			acreedores = jdbcTemplate.query(sql.toString(), new RowMapper<AcreedorDoctoDto>(){
				public AcreedorDoctoDto mapRow(ResultSet rs, int idx) throws SQLException {
					AcreedorDoctoDto acreedor = new AcreedorDoctoDto();
					acreedor.setDeAcre(rs.getString("de_acre"));
					acreedor.setaAcre(rs.getString("a_acre"));
					acreedor.setIdAcreDocto(rs.getInt("id_acre"));
					acreedor.setClasificacion(rs.getString("clasificacion_acre"));
					acreedor.setTipoAcre(rs.getString("tipo_acre"));
					return acreedor;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDao, M:obtenerAcreedorDocumento");
		}
		return acreedores;
	
	}

	@Override
	public List<DoctoTesoreriaDto> obtenerDoctoTesoreria(int idRegla, String clasificacion) {
		
		StringBuffer sql = new StringBuffer(); 
		
		sql.append("\n SELECT dt.id_docto_tes, dt.incluir_docto_tes, dt.excluir_docto_tes, dt.clasificacion_docto_tes ");
		sql.append("\n FROM DOCTO_TESORERIA_REG_NEG dt ");
		sql.append("\n WHERE dt.id_regla = "+ Utilerias.validarCadenaSQL(idRegla) +" ");
		
		if(clasificacion.equals("D")){
			sql.append("\n AND dt.clasificacion_docto_tes = 'D' "); //Clase Documento
		}else{
			sql.append("\n AND dt.clasificacion_docto_tes = 'T' "); //Tesoreria
		}

		sql.append("\n ORDER BY dt.id_docto_tes ");	
		
		List<DoctoTesoreriaDto> list=new ArrayList<DoctoTesoreriaDto>();
		
		try{
			list = jdbcTemplate.query(sql.toString(), new RowMapper<DoctoTesoreriaDto>(){
				public DoctoTesoreriaDto mapRow(ResultSet rs, int idx) throws SQLException {
					DoctoTesoreriaDto doctoTes = new DoctoTesoreriaDto();
					doctoTes.setIdDoctoTes(rs.getInt("id_docto_tes"));
					doctoTes.setIncluir(rs.getString("incluir_docto_tes"));
					doctoTes.setExcluir(rs.getString("excluir_docto_tes"));
					doctoTes.setClasificacion(rs.getString("clasificacion_docto_tes"));
					return doctoTes;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDao, M:obtenerDoctoTesoreria");
		}
		return list;
	}

	@Override
	public List<CondicionPagoDto> obtenerCondicionesPago(int idRegla) {

		StringBuffer sql = new StringBuffer(); 
		
		sql.append("\n SELECT cp.id_condicion_pago, cp.fecha_base, cp.accion_dia, cp.dias_adicionales, cp.fin_mes, ");
		sql.append("\n cv.id_contab_ven, cv.dia_pago1, cv.dia_pago2, cv.dia_pago3, cv.dia_pago4, ");
		sql.append("\n cv.dia_pago5, cv.dia_pago6, cv.dia_pago7, ");
		sql.append("\n cv.semana1, cv.semana2, cv.semana3, cv.semana4, cv.semana5, cv.semana6, cv.semana7, ");
		sql.append("\n de.id_dias_excep, de.dia_esp_uno, de.dia_excep_uno, de.dia_pago_excep_uno, ");
		sql.append("\n de.rango_dia_desde_uno, de.rango_dia_hasta_uno, de.dia_esp_dos, de.dia_excep_dos, ");
		sql.append("\n de.dia_pago_excep_dos, de.rango_dia_desde_dos, de.rango_dia_hasta_dos, ");
		sql.append("\n cp.clase_dia, cp.banco_interlocutor ");
		sql.append("\n FROM CONDICION_PAGO_REG_NEG cp JOIN REGLA_NEGOCIO rn on cp.id_regla = rn.id_regla");
		sql.append("\n 								  LEFT JOIN CONTAB_VENCIMIENTO_REG_NEG cv on cp.id_condicion_pago = cv.id_condicion_pago");
		sql.append("\n 								  LEFT JOIN DIAS_EXCEP_REG_NEG de on cp.id_condicion_pago = de.id_condicion_pago");
		sql.append("\n WHERE rn.id_regla = "+ Utilerias.validarCadenaSQL(idRegla) +" ");

		System.out.println(sql.toString());
		
		List<CondicionPagoDto> list=new ArrayList<CondicionPagoDto>();
		
		try{
			list = jdbcTemplate.query(sql.toString(), new RowMapper<CondicionPagoDto>(){
				public CondicionPagoDto mapRow(ResultSet rs, int idx) throws SQLException {
					CondicionPagoDto conPago = new CondicionPagoDto();
					conPago.setIdCondicionPago(rs.getInt("id_condicion_pago"));
					conPago.setFechaBase(rs.getString("fecha_base"));
					conPago.setAccionDia(rs.getString("accion_dia"));
					conPago.setDiasAdicionales(rs.getInt("dias_adicionales"));
					conPago.setFinMes(rs.getString("fin_mes"));
					conPago.setIdContab(rs.getInt("id_contab_ven"));
					conPago.setDiaPago1(rs.getString("dia_pago1"));
					conPago.setDiaPago2(rs.getString("dia_pago2"));
					conPago.setDiaPago3(rs.getString("dia_pago3"));
					conPago.setDiaPago4(rs.getString("dia_pago4"));
					conPago.setDiaPago5(rs.getString("dia_pago5"));
					conPago.setDiaPago6(rs.getString("dia_pago6"));
					conPago.setDiaPago7(rs.getString("dia_pago7"));
					conPago.setSemana1(rs.getString("semana1"));
					conPago.setSemana2(rs.getString("semana2"));
					conPago.setSemana3(rs.getString("semana3"));
					conPago.setSemana4(rs.getString("semana4"));
					conPago.setSemana5(rs.getString("semana5"));
					conPago.setSemana6(rs.getString("semana6"));
					conPago.setSemana7(rs.getString("semana7"));
					conPago.setIdDiasExcep(rs.getInt("id_dias_excep"));
					conPago.setDiaEspecifico1(rs.getInt("dia_esp_uno"));
					conPago.setDiaExcep1(rs.getString("dia_excep_uno"));
					conPago.setDiaPagoExcep1(rs.getString("dia_pago_excep_uno"));
					conPago.setRangoDiaDesde1(rs.getInt("rango_dia_desde_uno"));
					conPago.setRangoDiaHasta1(rs.getInt("rango_dia_hasta_uno"));
					conPago.setDiaEspecifico2(rs.getInt("dia_esp_dos"));
					conPago.setDiaExcep2(rs.getString("dia_excep_dos"));
					conPago.setDiaPagoExcep2(rs.getString("dia_pago_excep_dos"));
					conPago.setRangoDiaDesde2(rs.getInt("rango_dia_desde_dos"));
					conPago.setRangoDiaHasta2(rs.getInt("rango_dia_hasta_dos"));
					conPago.setClaseDia(rs.getString("clase_dia"));
					conPago.setBancoInterlocutor(rs.getString("banco_interlocutor"));
					return conPago;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDao, M:obtenerCondicionesPago");
		}
		return list;
		
	}

	@Override
	public List<PlanPagosDto> obtenerPlanPago(int idCondicioPago) {

		StringBuffer sql = new StringBuffer(); 
		
		sql.append("\n SELECT pp.id_plan, pp.fecha ");
		sql.append("\n FROM PLAN_PAGOS_REG_NEG pp ");
		sql.append("\n WHERE pp.id_condicion_pago = "+ idCondicioPago +" ");

		List<PlanPagosDto> list=new ArrayList<PlanPagosDto>();
		
		try{
			list = jdbcTemplate.query(sql.toString(), new RowMapper<PlanPagosDto>(){
				public PlanPagosDto mapRow(ResultSet rs, int idx) throws SQLException {
					PlanPagosDto pp = new PlanPagosDto();
					pp.setIdPlan(rs.getInt("id_plan"));
					pp.setFecha(rs.getString("fecha"));
					return pp;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDao, M:obtenerPlanPago");
		}
		return list;
	}

	@Override
	public int insertarReglaNegocio(ReglasNegocioDto reglaNegocio, List<EmpresaRegNegDto> listEmpresas,
			List<AcreedorDoctoDto> listAcreDocto, List<DoctoTesoreriaDto> listDoctoTes, CondicionPagoDto condicionPago,
			List<PlanPagosDto> listPlanPagos, List<RubroRegNeg> listRubros, String tipoOperacion, boolean simulador) {
		
		int afec = 0;
		StringBuffer sql = new StringBuffer();
		
		try{
			//Inserta regla negocio, si esta la bandera simulador==true, guarda la regla en una tabla temporal.
			if(simulador){
				sql.append("\n INSERT INTO REGLA_NEGOCIO_SIM VALUES( ");
				sql.append("\n CASE WHEN (SELECT MAX(ID_REGLA) FROM REGLA_NEGOCIO) IS NOT NULL ");
				sql.append("\n THEN (SELECT MAX(ID_REGLA) + 1 FROM REGLA_NEGOCIO) ");
				sql.append("\n ELSE 1 END, ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(reglaNegocio.getReglaNegocio()) + "', "); 
				sql.append("\n '" + Utilerias.validarCadenaSQL(reglaNegocio.getTipoRegla()) + "', ");
				sql.append("\n getdate(), ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(reglaNegocio.getUsuarioCaptura()) + "', ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(reglaNegocio.getGeneraPropuestaAutomatica()) + "', ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(reglaNegocio.getUsuarios()) + "', ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(reglaNegocio.getIndicador()) + "', ");
				sql.append("\n 'A', ");
				//sql.append("\n '" + Utilerias.validarCadenaSQL(reglaNegocio.getDescuento()) + "' ) ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(reglaNegocio.getDescuento()) + "', ");
				sql.append("\n " + reglaNegocio.getLongChequera() + " ) ");
				
			}else{
				sql.append("\n INSERT INTO REGLA_NEGOCIO VALUES( ");
				sql.append("\n CASE WHEN (SELECT MAX(ID_REGLA) FROM REGLA_NEGOCIO) IS NOT NULL ");
				sql.append("\n THEN (SELECT MAX(ID_REGLA) + 1 FROM REGLA_NEGOCIO) ");
				sql.append("\n ELSE 1 END, ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(reglaNegocio.getReglaNegocio()) + "', "); 
				sql.append("\n '" + Utilerias.validarCadenaSQL(reglaNegocio.getTipoRegla()) + "', ");
				sql.append("\n getdate(), ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(reglaNegocio.getUsuarioCaptura()) + "', ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(reglaNegocio.getGeneraPropuestaAutomatica()) + "', ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(reglaNegocio.getUsuarios()) + "', ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(reglaNegocio.getIndicador()) + "', ");
				sql.append("\n 'A', ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(reglaNegocio.getDescuento()) + "', ");
				sql.append("\n " + reglaNegocio.getLongChequera() + " ) ");
				
			}
			
			afec = jdbcTemplate.update(sql.toString());
			
			if(afec == 0){
				return -1;
			}
			
			//INSERTA EMPRESAS
			for(int i=0; i < listEmpresas.size(); i++){
				sql.delete(0, sql.length());
				
				if(simulador){
					sql.append("\n INSERT INTO EMPRESA_REG_NEG_SIM VALUES( ");
					sql.append("\n "+listEmpresas.get(i).getNoEmpresa()+", ");
				//	sql.append("\n convert(char(10),'01/01/2015',3) + convert(datetime, ' "+Utilerias.validarCadenaSQL(listEmpresas.get(i).getHoraLimiteOperacion())+"', 103), ");
					sql.append("\n DEFAULT, ");
					sql.append("\n (SELECT MAX(ID_REGLA) FROM REGLA_NEGOCIO_SIM)) ");
				}else{
					sql.append("\n INSERT INTO EMPRESA_REG_NEG VALUES( ");
					sql.append("\n "+listEmpresas.get(i).getNoEmpresa()+", ");
				//	sql.append("\n convert(char(10),'01/01/2015',3 + convert(datetime,' "+Utilerias.validarCadenaSQL(listEmpresas.get(i).getHoraLimiteOperacion())+"', 103), ");
					sql.append("\n DEFAULT, ");
					sql.append("\n (SELECT MAX(ID_REGLA) FROM REGLA_NEGOCIO)) ");
				}
				
				System.out.println(sql.toString());
				afec = jdbcTemplate.update(sql.toString());
				
				if(afec == 0){
					return -2;
				}
			}
				
			//INSERTA ACREEDORES Y DOCTOS
			for(int i=0; i < listAcreDocto.size(); i++){
				sql.delete(0, sql.length());
				
				if(simulador){
					sql.append("\n INSERT INTO ACRE_DOCTO_REG_NEG_SIM VALUES( ");
					
					if(i==0){
						sql.append("\n CASE WHEN (SELECT MAX(ID_ACRE) FROM ACRE_DOCTO_REG_NEG) IS NOT NULL ");
						sql.append("\n THEN (SELECT MAX(ID_ACRE) + 1 FROM ACRE_DOCTO_REG_NEG) ");
						sql.append("\n ELSE 1 END, ");
						
					}else{
						sql.append("\n CASE WHEN (SELECT MAX(ID_ACRE) FROM ACRE_DOCTO_REG_NEG_SIM) IS NOT NULL ");
						sql.append("\n THEN (SELECT MAX(ID_ACRE) + 1 FROM ACRE_DOCTO_REG_NEG_SIM) ");
						sql.append("\n ELSE 1 END, ");
					}
					
					sql.append("\n "+Utilerias.validarCadenaSQL(listAcreDocto.get(i).getDeAcre())+", ");
					sql.append("\n "+Utilerias.validarCadenaSQL(listAcreDocto.get(i).getaAcre())+", ");
					sql.append("\n '"+Utilerias.validarCadenaSQL(listAcreDocto.get(i).getClasificacion())+"', ");
					sql.append("\n '"+Utilerias.validarCadenaSQL(listAcreDocto.get(i).getTipoAcre())+"', ");
					sql.append("\n (SELECT MAX(ID_REGLA) FROM REGLA_NEGOCIO_SIM)) ");
				}else{
					sql.append("\n INSERT INTO ACRE_DOCTO_REG_NEG VALUES( ");
					sql.append("\n CASE WHEN (SELECT MAX(ID_ACRE) FROM ACRE_DOCTO_REG_NEG) IS NOT NULL ");
					sql.append("\n THEN (SELECT MAX(ID_ACRE) + 1 FROM ACRE_DOCTO_REG_NEG) ");
					sql.append("\n ELSE 1 END, ");
					sql.append("\n "+Utilerias.validarCadenaSQL(listAcreDocto.get(i).getDeAcre())+", ");
					sql.append("\n "+Utilerias.validarCadenaSQL(listAcreDocto.get(i).getaAcre())+", ");
					sql.append("\n '"+Utilerias.validarCadenaSQL(listAcreDocto.get(i).getClasificacion())+"', ");
					sql.append("\n '"+Utilerias.validarCadenaSQL(listAcreDocto.get(i).getTipoAcre())+"', ");
					sql.append("\n (SELECT MAX(ID_REGLA) FROM REGLA_NEGOCIO)) ");
				}
				
				System.out.println(sql.toString());
				
				afec = jdbcTemplate.update(sql.toString());
				
				if(afec == 0){
					return -3;
				}
			}
			
			//INSERTA CLASE DOCTOS Y TESORERIA
			for(int i=0; i < listDoctoTes.size(); i++){
				sql.delete(0, sql.length());
				
				if(simulador){
					sql.append("\n INSERT INTO DOCTO_TESORERIA_REG_NEG_SIM VALUES( ");
					
					if(i==0){
						sql.append("\n CASE WHEN (SELECT MAX(ID_DOCTO_TES) FROM DOCTO_TESORERIA_REG_NEG) IS NOT NULL ");
						sql.append("\n THEN (SELECT MAX(ID_DOCTO_TES) + 1 FROM DOCTO_TESORERIA_REG_NEG) ");
						sql.append("\n ELSE 1 END, ");
						
					}else{
						sql.append("\n CASE WHEN (SELECT MAX(ID_DOCTO_TES) FROM DOCTO_TESORERIA_REG_NEG_SIM) IS NOT NULL ");
						sql.append("\n THEN (SELECT MAX(ID_DOCTO_TES) + 1 FROM DOCTO_TESORERIA_REG_NEG_SIM) ");
						sql.append("\n ELSE 1 END, ");
						
					}
					
					sql.append("\n '"+Utilerias.validarCadenaSQL(listDoctoTes.get(i).getIncluir())+"', ");
					sql.append("\n '"+Utilerias.validarCadenaSQL(listDoctoTes.get(i).getExcluir())+"', ");
					sql.append("\n '"+Utilerias.validarCadenaSQL(listDoctoTes.get(i).getClasificacion())+"', ");
					sql.append("\n (SELECT MAX(ID_REGLA) FROM REGLA_NEGOCIO_SIM)) ");
				}else{
					sql.append("\n INSERT INTO DOCTO_TESORERIA_REG_NEG VALUES( ");
					sql.append("\n CASE WHEN (SELECT MAX(ID_DOCTO_TES) FROM DOCTO_TESORERIA_REG_NEG) IS NOT NULL ");
					sql.append("\n THEN (SELECT MAX(ID_DOCTO_TES) + 1 FROM DOCTO_TESORERIA_REG_NEG) ");
					sql.append("\n ELSE 1 END, ");
					sql.append("\n '"+Utilerias.validarCadenaSQL(listDoctoTes.get(i).getIncluir())+"', ");
					sql.append("\n '"+Utilerias.validarCadenaSQL(listDoctoTes.get(i).getExcluir())+"', ");
					sql.append("\n '"+Utilerias.validarCadenaSQL(listDoctoTes.get(i).getClasificacion())+"', ");
					sql.append("\n (SELECT MAX(ID_REGLA) FROM REGLA_NEGOCIO)) ");
				}
				
				
				System.out.println(sql.toString());
				
				afec = jdbcTemplate.update(sql.toString());
				
				if(afec == 0){
					return -4;
				}
			}
			
			//INSERTA CONDICION PAGO
			sql.delete(0, sql.length());
			
			if(simulador){
				sql.append("\n INSERT INTO CONDICION_PAGO_REG_NEG_SIM VALUES( ");
				sql.append("\n CASE WHEN (SELECT MAX(ID_CONDICION_PAGO) FROM CONDICION_PAGO_REG_NEG) IS NOT NULL ");
				sql.append("\n THEN (SELECT MAX(ID_CONDICION_PAGO) + 1 FROM CONDICION_PAGO_REG_NEG) ");
				sql.append("\n ELSE 1 END, ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getFechaBase()) + "', "); 
				sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getAccionDia()) + "', ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getClaseDia()) + "', "); 
				sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiasAdicionales()) + "', ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getFinMes()) + "', ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getBancoInterlocutor()) + "', "); 
				sql.append("\n  (SELECT MAX(ID_REGLA) FROM REGLA_NEGOCIO_SIM)) ");
			}else{
				sql.append("\n INSERT INTO CONDICION_PAGO_REG_NEG VALUES( ");
				sql.append("\n CASE WHEN (SELECT MAX(ID_CONDICION_PAGO) FROM CONDICION_PAGO_REG_NEG) IS NOT NULL ");
				sql.append("\n THEN (SELECT MAX(ID_CONDICION_PAGO) + 1 FROM CONDICION_PAGO_REG_NEG) ");
				sql.append("\n ELSE 1 END, ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getFechaBase()) + "', "); 
				sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getAccionDia()) + "', ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getClaseDia()) + "', "); 
				sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiasAdicionales()) + "', ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getFinMes()) + "', ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getBancoInterlocutor()) + "', "); 
				sql.append("\n  (SELECT MAX(ID_REGLA) FROM REGLA_NEGOCIO)) ");
			}
			
			afec = jdbcTemplate.update(sql.toString());
			
			if(afec == 0){
				return -5;
			}
			
			//INSERTA DIA CONTAB VENCIMIENTO
			if(condicionPago.isAplicaContab()){
				sql.delete(0, sql.length());
				
				if(simulador){
					sql.append("\n INSERT INTO CONTAB_VENCIMIENTO_REG_NEG_SIM VALUES( ");
					sql.append("\n CASE WHEN (SELECT MAX(ID_CONTAB_VEN) FROM CONTAB_VENCIMIENTO_REG_NEG) IS NOT NULL ");
					sql.append("\n THEN (SELECT MAX(ID_CONTAB_VEN) + 1 FROM CONTAB_VENCIMIENTO_REG_NEG) ");
					sql.append("\n ELSE 1 END, ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago1()) + "', "); 
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago2()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago3()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago4()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago5()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago6()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago7()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getSemana1()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getSemana2()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getSemana3()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getSemana4()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getSemana5()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getSemana6()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getSemana7()) + "', ");
					sql.append("\n (SELECT MAX(ID_CONDICION_PAGO) FROM CONDICION_PAGO_REG_NEG_SIM)) ");
				}else{
					sql.append("\n INSERT INTO CONTAB_VENCIMIENTO_REG_NEG VALUES( ");
					sql.append("\n CASE WHEN (SELECT MAX(ID_CONTAB_VEN) FROM CONTAB_VENCIMIENTO_REG_NEG) IS NOT NULL ");
					sql.append("\n THEN (SELECT MAX(ID_CONTAB_VEN) + 1 FROM CONTAB_VENCIMIENTO_REG_NEG) ");
					sql.append("\n ELSE 1 END, ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago1()) + "', "); 
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago2()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago3()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago4()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago5()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago6()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago7()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getSemana1()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getSemana2()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getSemana3()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getSemana4()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getSemana5()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getSemana6()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getSemana7()) + "', ");
					sql.append("\n (SELECT MAX(ID_CONDICION_PAGO) FROM CONDICION_PAGO_REG_NEG)) ");
				}
				
				
				afec = jdbcTemplate.update(sql.toString());
				
				if(afec == 0){
					return -6;
				}
				
			}
			
			//INSERTA DIAS ESPECIFICOS
			if(condicionPago.isAplicaDiasExcep()){
				sql.delete(0, sql.length());
				
				if(simulador){
					sql.append("\n INSERT INTO DIAS_EXCEP_REG_NEG_SIM VALUES( ");
					sql.append("\n CASE WHEN (SELECT MAX(ID_DIAS_EXCEP) FROM DIAS_EXCEP_REG_NEG) IS NOT NULL ");
					sql.append("\n THEN (SELECT MAX(ID_DIAS_EXCEP) + 1 FROM DIAS_EXCEP_REG_NEG) ");
					sql.append("\n ELSE 1 END, ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaEspecifico1()) + "', "); 
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaExcep1()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPagoExcep1()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getRangoDiaDesde1()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getRangoDiaHasta1()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaEspecifico2()) + "', "); 
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaExcep2()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPagoExcep2()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getRangoDiaDesde2()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getRangoDiaHasta2()) + "', ");
					sql.append("\n (SELECT MAX(ID_CONDICION_PAGO) FROM CONDICION_PAGO_REG_NEG_SIM)) ");
				}else{
					sql.append("\n INSERT INTO DIAS_EXCEP_REG_NEG VALUES( ");
					sql.append("\n CASE WHEN (SELECT MAX(ID_DIAS_EXCEP) FROM DIAS_EXCEP_REG_NEG) IS NOT NULL ");
					sql.append("\n THEN (SELECT MAX(ID_DIAS_EXCEP) + 1 FROM DIAS_EXCEP_REG_NEG) ");
					sql.append("\n ELSE 1 END, ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaEspecifico1()) + "', "); 
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaExcep1()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPagoExcep1()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getRangoDiaDesde1()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getRangoDiaHasta1()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaEspecifico2()) + "', "); 
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaExcep2()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPagoExcep2()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getRangoDiaDesde2()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getRangoDiaHasta2()) + "', ");
					sql.append("\n (SELECT MAX(ID_CONDICION_PAGO) FROM CONDICION_PAGO_REG_NEG)) ");
				}

				afec = jdbcTemplate.update(sql.toString());
				
				if(afec == 0){
					return -7;
				}
			}
			
			//INSERTA PLAN DE PAGOS
			for(int i=0; i < listPlanPagos.size(); i++){
				sql.delete(0, sql.length());
				
				if(simulador){
					sql.append("\n INSERT INTO PLAN_PAGOS_REG_NEG_SIM VALUES( ");
					if(i==0){
						sql.append("\n CASE WHEN (SELECT MAX(ID_PLAN) FROM PLAN_PAGOS_REG_NEG) IS NOT NULL ");
						sql.append("\n THEN (SELECT MAX(ID_PLAN) + 1 FROM PLAN_PAGOS_REG_NEG) ");
						sql.append("\n ELSE 1 END, ");
					}else{
						sql.append("\n CASE WHEN (SELECT MAX(ID_PLAN) FROM PLAN_PAGOS_REG_NEG_SIM) IS NOT NULL ");
						sql.append("\n THEN (SELECT MAX(ID_PLAN) + 1 FROM PLAN_PAGOS_REG_NEG_SIM) ");
						sql.append("\n ELSE 1 END, ");
					}
					
					sql.append("\n convert(datetime,'"+Utilerias.validarCadenaSQL(listPlanPagos.get(i).getFecha().substring(0, 10))
											 +" 00:00:00', 103), ");
					sql.append("\n (SELECT MAX(ID_CONDICION_PAGO) FROM CONDICION_PAGO_REG_NEG_SIM)) ");
					
				}else{
					sql.append("\n INSERT INTO PLAN_PAGOS_REG_NEG_SIM VALUES( ");
					sql.append("\n CASE WHEN (SELECT MAX(ID_PLAN) FROM PLAN_PAGOS_REG_NEG) IS NOT NULL ");
					sql.append("\n THEN (SELECT MAX(ID_PLAN) + 1 FROM PLAN_PAGOS_REG_NEG) ");
					sql.append("\n ELSE 1 END, ");
					sql.append("\n convert(datetime,'"+Utilerias.validarCadenaSQL(listPlanPagos.get(i).getFecha().substring(0, 10))
											 +" 00:00:00', 103), ");
					sql.append("\n (SELECT MAX(ID_CONDICION_PAGO) FROM CONDICION_PAGO_REG_NEG_SIM)) ");
					
				}
				
				
				System.out.println(sql.toString());
				
				afec = jdbcTemplate.update(sql.toString());
				
				if(afec == 0){
					return -8;
				}
			}
			
			//INSERTA RUBROS
			for(int i=0; i < listRubros.size(); i++){
				
				sql.delete(0, sql.length());
				
				if(simulador){
					sql.append("\n INSERT INTO RUBRO_REG_NEG_SIM VALUES( ");
					sql.append("\n "+Utilerias.validarCadenaSQL(listRubros.get(i).getIdRubro())+", ");
					sql.append("\n '"+Utilerias.validarCadenaSQL(listRubros.get(i).getDescRubro())+"', ");
					sql.append("\n (SELECT MAX(ID_REGLA) FROM REGLA_NEGOCIO_SIM)) ");
					
				}else{
					sql.append("\n INSERT INTO RUBRO_REG_NEG VALUES( ");
					sql.append("\n "+Utilerias.validarCadenaSQL(listRubros.get(i).getIdRubro())+", ");
					sql.append("\n '"+Utilerias.validarCadenaSQL(listRubros.get(i).getDescRubro())+"', ");
					sql.append("\n (SELECT MAX(ID_REGLA) FROM REGLA_NEGOCIO)) ");
				}
				
				System.out.println(sql.toString());
				afec = jdbcTemplate.update(sql.toString());
				
				if(afec == 0){
					return -9;
				}
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDao, M:insertarReglaNegocio");
			return -1;
		}
		return afec;
	}

	@Override
	public int actualizarReglaNegocio(ReglasNegocioDto reglaNegocio, List<EmpresaRegNegDto> listEmpresas,
			List<AcreedorDoctoDto> listAcreDocto, List<DoctoTesoreriaDto> listDoctoTes, CondicionPagoDto condicionPago,
			List<PlanPagosDto> listPlanPagos, List<RubroRegNeg> listRubros, String tipoOperacion,
			List<AcreedorDoctoDto> lEliminarAcreDocto, List<DoctoTesoreriaDto> lEliminarDoctoTes,
			List<PlanPagosDto> lEliminarFechas,
			boolean eliminaDiasContabVenc, boolean eliminaDiasAdicionales, 
			boolean eliminaDiasespecificos, boolean eliminaPlanPagos) {
		
		int afec = 0;
		StringBuffer sql = new StringBuffer();
		
		try{
			//Inserta regla negocio
			if(reglaNegocio.getIdRegla() == 0 && tipoOperacion.equals("I")){
				sql.append("\n INSERT INTO REGLA_NEGOCIO VALUES( ");
				sql.append("\n CASE WHEN (SELECT MAX(ID_REGLA) FROM REGLA_NEGOCIO) IS NOT NULL ");
				sql.append("\n THEN (SELECT MAX(ID_REGLA) + 1 FROM REGLA_NEGOCIO) ");
				sql.append("\n ELSE 1 END, ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(reglaNegocio.getReglaNegocio()) + "', "); 
				sql.append("\n '" + Utilerias.validarCadenaSQL(reglaNegocio.getTipoRegla()) + "', ");
				sql.append("\n SYSDATE, ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(reglaNegocio.getUsuarioCaptura()) + "', ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(reglaNegocio.getGeneraPropuestaAutomatica()) + "', ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(reglaNegocio.getUsuarios()) + "', ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(reglaNegocio.getIndicador()) + "', ");
				//Estos dos campos son agregados, estatus y descuento EMS: 21/01/2016
				sql.append("\n 'A', ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(reglaNegocio.getDescuento()) + "', ");
				sql.append("\n " + reglaNegocio.getLongChequera() + " ) ");
				
			}else if(reglaNegocio.getIdRegla() != 0 && tipoOperacion.equals("U")){
				sql.append("\n UPDATE REGLA_NEGOCIO ");
				sql.append("\n SET REGLA_NEGOCIO = '" + Utilerias.validarCadenaSQL(reglaNegocio.getReglaNegocio()) + "', "); 
				sql.append("\n GEN_PROP_AUTO = '" + Utilerias.validarCadenaSQL(reglaNegocio.getGeneraPropuestaAutomatica()) + "', ");
				sql.append("\n USUARIOS = '" + Utilerias.validarCadenaSQL(reglaNegocio.getUsuarios()) + "', ");
				sql.append("\n INDICADOR = '" + Utilerias.validarCadenaSQL(reglaNegocio.getIndicador()) + "', ");
				sql.append("\n DESCUENTO = '" + Utilerias.validarCadenaSQL(reglaNegocio.getDescuento()) + "', ");
				sql.append("\n LONG_CHEQUERA = " + reglaNegocio.getLongChequera() + " ");
				sql.append("\n WHERE ID_REGLA = " + Utilerias.validarCadenaSQL(reglaNegocio.getIdRegla()) + " ");
			}else{
				return -1;
			}
			
			afec = jdbcTemplate.update(sql.toString());
			
			if(afec == 0)
				return -1;
			
			//DELETE DE EMPRESAS, VUELVE A INSERTAR EMPRESAS.
			sql.delete(0, sql.length());
			
			sql.append("\n DELETE FROM EMPRESA_REG_NEG ");
			sql.append("\n WHERE ID_REGLA = "+ Utilerias.validarCadenaSQL(reglaNegocio.getIdRegla()) +" ");
			System.out.println(sql.toString());
			jdbcTemplate.update(sql.toString());			
			
			//INSERTA EMPRESAS
			for(int i=0; i < listEmpresas.size(); i++){
				sql.delete(0, sql.length());
				
				sql.append("\n INSERT INTO EMPRESA_REG_NEG VALUES( ");
				sql.append("\n "+Utilerias.validarCadenaSQL(listEmpresas.get(i).getNoEmpresa())+", ");
				//sql.append("\n convert(char(10),'01/01/2015',3 + convert(datetime,' "+Utilerias.validarCadenaSQL(listEmpresas.get(i).getHoraLimiteOperacion())+"', 3), ");
				sql.append("\n DEFAULT,");
				sql.append("\n "+ Utilerias.validarCadenaSQL(reglaNegocio.getIdRegla())+" ) ");
				System.out.println(sql.toString());
				afec = jdbcTemplate.update(sql.toString());
				
				if(afec == 0)
					return -2;
			}
				
			//ACREEDORES Y DOCUMENTOS SE HACE UNA BUSQUEDA POR ID, SI EXISTE ACTUALIZA, SI NO EXISTE INSERTA.
			for(int i=0; i < listAcreDocto.size(); i++){
				sql.delete(0, sql.length());
				
				if(existeAcreDocto(listAcreDocto.get(i).getIdAcreDocto(), reglaNegocio.getIdRegla())){
					sql.append("\n UPDATE ACRE_DOCTO_REG_NEG ");
					sql.append("\n SET DE_ACRE = "+Utilerias.validarCadenaSQL(listAcreDocto.get(i).getDeAcre())+", ");
					sql.append("\n A_ACRE = "+Utilerias.validarCadenaSQL(listAcreDocto.get(i).getaAcre())+", ");
					sql.append("\n CLASIFICACION_ACRE = '"+Utilerias.validarCadenaSQL(listAcreDocto.get(i).getClasificacion())+"', ");
					sql.append("\n TIPO_ACRE = '"+Utilerias.validarCadenaSQL(listAcreDocto.get(i).getTipoAcre())+"' ");
					sql.append("\n WHERE ID_ACRE = "+Utilerias.validarCadenaSQL(listAcreDocto.get(i).getIdAcreDocto())+" ");
					sql.append("\n AND ID_REGLA = "+Utilerias.validarCadenaSQL(reglaNegocio.getIdRegla())+" ");
				}else{
					sql.append("\n INSERT INTO ACRE_DOCTO_REG_NEG VALUES( ");
					sql.append("\n CASE WHEN (SELECT MAX(ID_ACRE) FROM ACRE_DOCTO_REG_NEG) IS NOT NULL ");
					sql.append("\n THEN (SELECT MAX(ID_ACRE) + 1 FROM ACRE_DOCTO_REG_NEG) ");
					sql.append("\n ELSE 1 END, ");
					sql.append("\n "+Utilerias.validarCadenaSQL(listAcreDocto.get(i).getDeAcre())+", ");
					sql.append("\n "+Utilerias.validarCadenaSQL(listAcreDocto.get(i).getaAcre())+", ");
					sql.append("\n '"+Utilerias.validarCadenaSQL(listAcreDocto.get(i).getClasificacion())+"', ");
					sql.append("\n '"+Utilerias.validarCadenaSQL(listAcreDocto.get(i).getTipoAcre())+"', ");
					sql.append("\n "+Utilerias.validarCadenaSQL(reglaNegocio.getIdRegla()) +" ) ");
				}
				
				System.out.println(sql.toString());
				
				afec = jdbcTemplate.update(sql.toString());
				
				if(afec == 0)
					return -3;
			}
			
			//PARA CLASE DOCTOS Y TESORERIA SE HACE UNA BUSQUEDA POR ID, SI EXISTE ACTUALIZA, SI NO EXISTE INSERTA.
			for(int i=0; i < listDoctoTes.size(); i++){
				sql.delete(0, sql.length());
				
				if(existeDoctoTes(listDoctoTes.get(i).getIdDoctoTes(), reglaNegocio.getIdRegla())){
					sql.append("\n UPDATE DOCTO_TESORERIA_REG_NEG ");
					sql.append("\n SET INCLUIR_DOCTO_TES = '"+Utilerias.validarCadenaSQL(listDoctoTes.get(i).getIncluir())+"', ");
					sql.append("\n EXCLUIR_DOCTO_TES = '"+Utilerias.validarCadenaSQL(listDoctoTes.get(i).getExcluir())+"', ");
					sql.append("\n CLASIFICACION_DOCTO_TES = '"+Utilerias.validarCadenaSQL(listDoctoTes.get(i).getClasificacion())+"' ");
					sql.append("\n WHERE ID_DOCTO_TES = "+Utilerias.validarCadenaSQL(listDoctoTes.get(i).getIdDoctoTes())+" ");
					sql.append("\n AND ID_REGLA = "+Utilerias.validarCadenaSQL(reglaNegocio.getIdRegla())+" ");
				}else{
					sql.append("\n INSERT INTO DOCTO_TESORERIA_REG_NEG VALUES( ");
					sql.append("\n CASE WHEN (SELECT MAX(ID_DOCTO_TES) FROM DOCTO_TESORERIA_REG_NEG) IS NOT NULL ");
					sql.append("\n THEN (SELECT MAX(ID_DOCTO_TES) + 1 FROM DOCTO_TESORERIA_REG_NEG) ");
					sql.append("\n ELSE 1 END, ");
					sql.append("\n '"+Utilerias.validarCadenaSQL(listDoctoTes.get(i).getIncluir())+"', ");
					sql.append("\n '"+Utilerias.validarCadenaSQL(listDoctoTes.get(i).getExcluir())+"', ");
					sql.append("\n '"+Utilerias.validarCadenaSQL(listDoctoTes.get(i).getClasificacion())+"', ");
					sql.append("\n "+Utilerias.validarCadenaSQL(reglaNegocio.getIdRegla())+" ) ");
				}
				
				System.out.println(sql.toString());
				
				afec = jdbcTemplate.update(sql.toString());
				
				if(afec == 0)
					return -4;
			}
			
			//INSERTA CONDICION PAGO
			if(existeCondicionPago(condicionPago.getIdCondicionPago(), reglaNegocio.getIdRegla())){
				sql.delete(0, sql.length());
				
				sql.append("\n UPDATE CONDICION_PAGO_REG_NEG ");
				sql.append("\n SET FECHA_BASE = '" + Utilerias.validarCadenaSQL(condicionPago.getFechaBase()) + "', "); 
				sql.append("\n ACCION_DIA = '" + Utilerias.validarCadenaSQL(condicionPago.getAccionDia()) + "', ");
				sql.append("\n CLASE_DIA = '" + Utilerias.validarCadenaSQL(condicionPago.getClaseDia()) + "', ");
				sql.append("\n DIAS_ADICIONALES = '" + Utilerias.validarCadenaSQL(condicionPago.getDiasAdicionales()) + "', ");
				sql.append("\n FIN_MES = '" + Utilerias.validarCadenaSQL(condicionPago.getFinMes()) + "', ");
				sql.append("\n BANCO_INTERLOCUTOR = '" + Utilerias.validarCadenaSQL(condicionPago.getBancoInterlocutor()) + "' ");
				sql.append("\n WHERE ID_CONDICION_PAGO = "+Utilerias.validarCadenaSQL(condicionPago.getIdCondicionPago())+" ");
				sql.append("\n AND ID_REGLA = "+Utilerias.validarCadenaSQL(reglaNegocio.getIdRegla())+" ");
				
			}else{
				sql.delete(0, sql.length());
				
				sql.append("\n INSERT INTO CONDICION_PAGO_REG_NEG VALUES( ");
				sql.append("\n CASE WHEN (SELECT MAX(ID_CONDICION_PAGO) FROM CONDICION_PAGO_REG_NEG) IS NOT NULL ");
				sql.append("\n THEN (SELECT MAX(ID_CONDICION_PAGO) + 1 FROM CONDICION_PAGO_REG_NEG) ");
				sql.append("\n ELSE 1 END, ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getFechaBase()) + "', "); 
				sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getAccionDia()) + "', ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getClaseDia()) + "', "); 
				sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiasAdicionales()) + "', ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getFinMes()) + "', ");
				sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getBancoInterlocutor()) + "', "); 
				sql.append("\n "+Utilerias.validarCadenaSQL(reglaNegocio.getIdRegla())+") ");
			}
			
			System.out.println(sql.toString());
			
			afec = jdbcTemplate.update(sql.toString());
			
			if(afec == 0)
				return -5;
			
			//INSERTA DIA CONTAB VENCIMIENTO
			if(condicionPago.isAplicaContab()){
				
				if(existeContabVencimiento(condicionPago.getIdContab(),condicionPago.getIdCondicionPago())){
					sql.delete(0, sql.length());
					sql.append("\n UPDATE CONTAB_VENCIMIENTO_REG_NEG ");
					sql.append("\n SET DIA_PAGO1 = '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago1()) + "', "); 
					sql.append("\n DIA_PAGO2 = '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago2()) + "', ");
					sql.append("\n DIA_PAGO3 = '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago3()) + "', ");
					sql.append("\n DIA_PAGO4 = '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago4()) + "', ");
					sql.append("\n DIA_PAGO5 = '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago5()) + "', ");
					sql.append("\n DIA_PAGO6 = '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago6()) + "', ");
					sql.append("\n DIA_PAGO7 = '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago7()) + "', ");
					sql.append("\n SEMANA1 = '" + Utilerias.validarCadenaSQL(condicionPago.getSemana1()) + "', ");
					sql.append("\n SEMANA2 = '" + Utilerias.validarCadenaSQL(condicionPago.getSemana2()) + "', ");
					sql.append("\n SEMANA3 = '" + Utilerias.validarCadenaSQL(condicionPago.getSemana3()) + "', ");
					sql.append("\n SEMANA4 = '" + Utilerias.validarCadenaSQL(condicionPago.getSemana4()) + "', ");
					sql.append("\n SEMANA5 = '" + Utilerias.validarCadenaSQL(condicionPago.getSemana5()) + "', ");
					sql.append("\n SEMANA6 = '" + Utilerias.validarCadenaSQL(condicionPago.getSemana6()) + "', ");
					sql.append("\n SEMANA7 = '" + Utilerias.validarCadenaSQL(condicionPago.getSemana7()) + "' ");
					sql.append("\n WHERE ID_CONTAB_VEN = "+Utilerias.validarCadenaSQL(condicionPago.getIdContab())+" ");
					sql.append("\n AND ID_CONDICION_PAGO = "+Utilerias.validarCadenaSQL(condicionPago.getIdCondicionPago())+" ");
				}else{
					sql.delete(0, sql.length());
					sql.append("\n INSERT INTO CONTAB_VENCIMIENTO_REG_NEG VALUES( ");
					sql.append("\n CASE WHEN (SELECT MAX(ID_CONTAB_VEN) FROM CONTAB_VENCIMIENTO_REG_NEG) IS NOT NULL ");
					sql.append("\n THEN (SELECT MAX(ID_CONTAB_VEN) + 1 FROM CONTAB_VENCIMIENTO_REG_NEG) ");
					sql.append("\n ELSE 1 END, ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago1()) + "', "); 
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago2()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago3()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago4()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago5()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago6()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPago7()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getSemana1()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getSemana2()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getSemana3()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getSemana4()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getSemana5()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getSemana6()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getSemana7()) + "', ");
					sql.append("\n "+Utilerias.validarCadenaSQL(condicionPago.getIdCondicionPago())+" ) ");
				}
				
				System.out.println(sql.toString());
				
				afec = jdbcTemplate.update(sql.toString());
				
				if(afec == 0)
					return -6;
				
			}
			
			//INSERTA DIAS ESPECIFICOS
			if(condicionPago.isAplicaDiasExcep()){
				
				if(existeDiasEspecificos(condicionPago.getIdDiasExcep(), condicionPago.getIdCondicionPago())){
					sql.delete(0, sql.length());
					sql.append("\n UPDATE DIAS_EXCEP_REG_NEG ");
					sql.append("\n SET DIA_ESP_UNO = '" + Utilerias.validarCadenaSQL(condicionPago.getDiaEspecifico1()) + "', "); 
					sql.append("\n DIA_EXCEP_UNO = '" + Utilerias.validarCadenaSQL(condicionPago.getDiaExcep1()) + "', ");
					sql.append("\n DIA_PAGO_EXCEP_UNO = '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPagoExcep1()) + "', ");
					sql.append("\n RANGO_DIA_DESDE_UNO = '" + Utilerias.validarCadenaSQL(condicionPago.getRangoDiaDesde1()) + "', ");
					sql.append("\n RANGO_DIA_HASTA_UNO = '" + Utilerias.validarCadenaSQL(condicionPago.getRangoDiaHasta1()) + "', ");
					sql.append("\n DIA_ESP_DOS = '" + Utilerias.validarCadenaSQL(condicionPago.getDiaEspecifico2()) + "', "); 
					sql.append("\n DIA_EXCEP_DOS = '" + Utilerias.validarCadenaSQL(condicionPago.getDiaExcep2()) + "', ");
					sql.append("\n DIA_PAGO_EXCEP_DOS = '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPagoExcep2()) + "', ");
					sql.append("\n RANGO_DIA_DESDE_DOS = '" + Utilerias.validarCadenaSQL(condicionPago.getRangoDiaDesde2()) + "', ");
					sql.append("\n RANGO_DIA_HASTA_DOS = '" + Utilerias.validarCadenaSQL(condicionPago.getRangoDiaHasta2()) + "' ");
					sql.append("\n WHERE ID_DIAS_EXCEP = "+Utilerias.validarCadenaSQL(condicionPago.getIdCondicionPago())+" ");
					sql.append("\n AND ID_CONDICION_PAGO = "+Utilerias.validarCadenaSQL(condicionPago.getIdCondicionPago())+" ");
				}else{
					sql.delete(0, sql.length());
					sql.append("\n INSERT INTO DIAS_EXCEP_REG_NEG VALUES( ");
					sql.append("\n CASE WHEN (SELECT MAX(ID_DIAS_EXCEP) FROM DIAS_EXCEP_REG_NEG) IS NOT NULL ");
					sql.append("\n THEN (SELECT MAX(ID_DIAS_EXCEP) + 1 FROM DIAS_EXCEP_REG_NEG) ");
					sql.append("\n ELSE 1 END, ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaEspecifico1()) + "', "); 
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaExcep1()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPagoExcep1()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getRangoDiaDesde1()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getRangoDiaHasta1()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaEspecifico2()) + "', "); 
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaExcep2()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getDiaPagoExcep2()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getRangoDiaDesde2()) + "', ");
					sql.append("\n '" + Utilerias.validarCadenaSQL(condicionPago.getRangoDiaHasta2()) + "', ");
					sql.append("\n "+ Utilerias.validarCadenaSQL(condicionPago.getIdCondicionPago())+" ) ");
				}
				
				System.out.println(sql.toString());
				
				afec = jdbcTemplate.update(sql.toString());
				
				if(afec == 0)
					return -7;
			}
			
			//INSERTA PLAN DE PAGOS
			for(int i=0; i < listPlanPagos.size(); i++){
				
				if(existePlanPagos(listPlanPagos.get(i).getIdPlan(), condicionPago.getIdCondicionPago())){
					sql.delete(0, sql.length());
					
					sql.append("\n UPDATE PLAN_PAGOS_REG_NEG ");
					sql.append("\n SET FECHA = convert(datetime,'"
									+Utilerias.validarCadenaSQL(listPlanPagos.get(i).getFecha().substring(0, 10))
									+" 00:00:00', 103) ");
					sql.append("\n WHERE ID_PLAN = "+listPlanPagos.get(i).getIdPlan()+" ");
					sql.append("\n AND ID_CONDICION_PAGO = "+condicionPago.getIdCondicionPago()+" ");
					
				}else{
					sql.delete(0, sql.length());
					
					sql.append("\n INSERT INTO PLAN_PAGOS_REG_NEG VALUES( ");
					sql.append("\n CASE WHEN (SELECT MAX(ID_PLAN) FROM PLAN_PAGOS_REG_NEG) IS NOT NULL ");
					sql.append("\n THEN (SELECT MAX(ID_PLAN) + 1 FROM PLAN_PAGOS_REG_NEG) ");
					sql.append("\n ELSE 1 END, ");
					sql.append("\n convert(datetime'"
									+Utilerias.validarCadenaSQL(listPlanPagos.get(i).getFecha().substring(0, 10))
									+" 00:00:00', 103), ");
					sql.append("\n "+condicionPago.getIdCondicionPago()+" ) ");
				}
				
				System.out.println(sql.toString());
				
				afec = jdbcTemplate.update(sql.toString());
				
				if(afec == 0)
					return -8;
				
			}
			
			//DELETE DE RUBROS, VUELVE A INSERTAR RUBROS.
			sql.delete(0, sql.length());
			
			sql.append("\n DELETE FROM RUBRO_REG_NEG ");
			sql.append("\n WHERE ID_REGLA = "+ reglaNegocio.getIdRegla() +" ");
			System.out.println(sql.toString());
			jdbcTemplate.update(sql.toString());			
			
			//INSERTA RUBROS
			for(int i=0; i < listRubros.size(); i++){
				sql.delete(0, sql.length());
				
				sql.append("\n INSERT INTO RUBRO_REG_NEG VALUES( ");
				sql.append("\n "+Utilerias.validarCadenaSQL(listRubros.get(i).getIdRubro())+", ");
				sql.append("\n '"+Utilerias.validarCadenaSQL(listRubros.get(i).getDescRubro())+"', ");
				sql.append("\n "+ reglaNegocio.getIdRegla()+" ) ");
				System.out.println(sql.toString());
				
				afec = jdbcTemplate.update(sql.toString());
				
				if(afec == 0)
					return -12;
			}
			
			//Se eliminan los registros de la bd que se hayan eliminado en la vista.
			//ELIMINA ACRE_DOCTOS
			for(int i=0; i<lEliminarAcreDocto.size(); i++){
				
				sql.delete(0, sql.length()-1);

				sql.append("\n DELETE FROM ACRE_DOCTO_REG_NEG ");
				sql.append("\n WHERE ID_ACRE = "+lEliminarAcreDocto.get(i).getIdAcreDocto()+" ");
				sql.append("\n AND ID_REGLA = "+reglaNegocio.getIdRegla()+" ");
				
				System.out.println(sql.toString());
				
				afec = jdbcTemplate.update(sql.toString());
				
				if(afec == 0)
					return -9;
				
			}
			
			//ELIMINA CLASE_DOCTO Y GRUPO TESORERIA
			for(int i=0; i<lEliminarDoctoTes.size(); i++){
				
				sql.delete(0, sql.length()-1);
			
				sql.append("\n DELETE FROM DOCTO_TESORERIA_REG_NEG ");
				sql.append("\n WHERE ID_DOCTO_TES = "+lEliminarDoctoTes.get(i).getIdDoctoTes()+" ");
				sql.append("\n AND ID_REGLA = "+reglaNegocio.getIdRegla()+" ");
				
				System.out.println(sql.toString());
				
				afec = jdbcTemplate.update(sql.toString());
				
				if(afec == 0)
					return -10;
				
			}
			
			//ELIMINA FECHAS DE PLAN DE PAGOS
			for(int i=0; i<lEliminarFechas.size(); i++){
				
				sql.delete(0, sql.length()-1);
				
				sql.append("\n DELETE FROM PLAN_PAGOS_REG_NEG ");
				sql.append("\n WHERE ID_PLAN= "+lEliminarFechas.get(i).getIdPlan()+" ");
				sql.append("\n AND ID_CONDICION_PAGO = "+condicionPago.getIdCondicionPago()+" ");
				
				System.out.println(sql.toString());
				
				afec = jdbcTemplate.update(sql.toString());
				
				if(afec == 0) 
					return -11;
				
			}
			
			if(eliminaDiasContabVenc){
				
				if(sql.length() > 0)
					sql.delete(0, sql.length()-1);
					
				sql.append("\n DELETE FROM CONTAB_VENCIMIENTO_REG_NEG ");
				sql.append("\n WHERE ID_CONDICION_PAGO = "+condicionPago.getIdCondicionPago()+" ");
				
				System.out.println(sql.toString());
				
				afec = jdbcTemplate.update(sql.toString());
				if(afec == 0)
					return -13;
			}
			
			if(eliminaDiasAdicionales){
				if(sql.length() > 0)
					sql.delete(0, sql.length()-1);
					
				sql.append("\n UPDATE CONDICION_PAGO_REG_NEG ");
				sql.append("\n SET DIAS_ADICIONALES = '' ");
				sql.append("\n WHERE ID_CONDICION_PAGO = "+condicionPago.getIdCondicionPago()+" ");
				sql.append("\n AND ID_REGLA = "+reglaNegocio.getIdRegla()+" ");
				
				System.out.println(sql.toString());
				
				afec = jdbcTemplate.update(sql.toString());
				if(afec == 0)
					return -14;
			}
			
			if(eliminaDiasespecificos){
		
				if(sql.length() > 0)
					sql.delete(0, sql.length()-1);
					
				sql.append("\n DELETE FROM DIAS_EXCEP_REG_NEG ");
				sql.append("\n WHERE ID_CONDICION_PAGO = "+condicionPago.getIdCondicionPago()+" ");
				
				System.out.println(sql.toString());
				
				afec = jdbcTemplate.update(sql.toString());	
				if(afec == 0)
					return -15;
			}
			
			if(eliminaPlanPagos){
				if(sql.length() > 0)
					sql.delete(0, sql.length()-1);
					
				sql.append("\n DELETE FROM PLAN_PAGOS_REG_NEG ");
				sql.append("\n WHERE ID_CONDICION_PAGO = "+condicionPago.getIdCondicionPago()+" ");
				
				System.out.println(sql.toString());
				
				afec = jdbcTemplate.update(sql.toString());	
				if(afec == 0)
					return-16;
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDao, M:actualizarReglaNegocio");
			return -1;
		}
		return afec;
	}

	public boolean existeAcreDocto(int idAcreDocto, int idRegla) {

		StringBuffer sql = new StringBuffer(); 
		int count = 0;
		
		sql.append("\n SELECT COUNT(ID_ACRE) ");
		sql.append("\n FROM ACRE_DOCTO_REG_NEG ");
		sql.append("\n WHERE ID_ACRE = "+ idAcreDocto +" ");
		sql.append("\n AND ID_REGLA = "+ idRegla +" ");
		
		try{
			count=jdbcTemplate.queryForInt(sql.toString());
			return (count > 0)?true:false;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDao, M:existeAcreDocto");
			
			return false;
		}
		
	}
	
	public boolean existeDoctoTes(int idDoctoTes, int idRegla) {

		StringBuffer sql = new StringBuffer(); 
		int count = 0;
		
		sql.append("\n SELECT COUNT(ID_DOCTO_TES) ");
		sql.append("\n FROM DOCTO_TESORERIA_REG_NEG ");
		sql.append("\n WHERE ID_DOCTO_TES = "+ idDoctoTes +" ");
		sql.append("\n AND ID_REGLA = "+ idRegla +" ");
		
		try{
			count=jdbcTemplate.queryForInt(sql.toString());
			return (count > 0)?true:false;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDao, M:existeDoctoTes");
			
			return false;
		}
	}
	
	public boolean existeCondicionPago(int idCondicionPago, int idRegla) {

		StringBuffer sql = new StringBuffer(); 
		int count = 0;
		
		sql.append("\n SELECT COUNT(ID_CONDICION_PAGO) ");
		sql.append("\n FROM CONDICION_PAGO_REG_NEG ");
		sql.append("\n WHERE ID_CONDICION_PAGO = "+ idCondicionPago +" ");
		sql.append("\n AND ID_REGLA = "+ idRegla +" ");
		
		try{
			count=jdbcTemplate.queryForInt(sql.toString());
			return (count > 0)?true:false;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDao, M:existeDoctoTes");
			
			return false;
		}
	}
	
	public boolean existeContabVencimiento(int idContabVen, int idCondicionPago) {

		StringBuffer sql = new StringBuffer(); 
		int count = 0;
		
		sql.append("\n SELECT COUNT(ID_CONTAB_VEN) ");
		sql.append("\n FROM CONTAB_VENCIMIENTO_REG_NEG ");
		sql.append("\n WHERE ID_CONTAB_VEN = "+ idContabVen +" ");
		sql.append("\n AND ID_CONDICION_PAGO = "+ idCondicionPago +" ");
		
		try{
			count=jdbcTemplate.queryForInt(sql.toString());
			return (count > 0)?true:false;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDao, M:existeContabVencimiento");
			
			return false;
		}
	}
	
	public boolean existeDiasEspecificos(int idDiasExcep, int idCondicionPago) {

		StringBuffer sql = new StringBuffer(); 
		int count = 0;
		
		sql.append("\n SELECT COUNT(ID_DIAS_EXCEP) ");
		sql.append("\n FROM DIAS_EXCEP_REG_NEG ");
		sql.append("\n WHERE ID_DIAS_EXCEP = "+ idDiasExcep +" ");
		sql.append("\n AND ID_CONDICION_PAGO = "+ idCondicionPago +" ");
		
		try{
			count=jdbcTemplate.queryForInt(sql.toString());
			return (count > 0)?true:false;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDao, M:existeDiasEspecificos");
			
			return false;
		}
	}
	
	public boolean existePlanPagos(int idPlanPagos, int idCondicionPago) {

		StringBuffer sql = new StringBuffer(); 
		int count = 0;
		
		sql.append("\n SELECT COUNT(ID_PLAN) ");
		sql.append("\n FROM PLAN_PAGOS_REG_NEG ");
		sql.append("\n WHERE ID_PLAN = "+ idPlanPagos +" ");
		sql.append("\n AND ID_CONDICION_PAGO = "+ idCondicionPago +" ");
		
		try{
			count=jdbcTemplate.queryForInt(sql.toString());
			return (count > 0)?true:false;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDao, M:existePlanPagos");
			return false;
		}
	}

	@Override
	public int eliminarReglaNegocio(int idRegla) {
		
		StringBuffer sql = new StringBuffer(); 
		int count = 0;
		
		sql.append("\n UPDATE REGLA_NEGOCIO ");
		sql.append("\n SET ESTATUS = 'I' ");
		sql.append("\n WHERE ID_REGLA = "+ idRegla +" ");
		
		try{
			count=jdbcTemplate.update(sql.toString());
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDao, M:existePlanPagos");
		}
		
		return count;
	}

	@Override
	public Map<String, String> validarDocumento(List<AcreedorDoctoDto> list) {
		
		Map<String, String> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		//StringBuffer sql= new StringBuffer();
		int count = 0;
		
		try{
			/* VALIDACION DE DOCUMENTOS PARA SABER SI EXISTE EN ALGUNA OTRA REGLA DE NEGOCIO... SE COMENTA XQ SE QUITO LA VALIDACION....
			for(int i = 0; i < list.size(); i++){
				if(sql.length()>0)
					sql.delete(0, sql.length());
			
				sql.append("\n SELECT COUNT(RN.ID_REGLA) AS REG ");
	            sql.append("\n FROM REGLA_NEGOCIO RN JOIN ACRE_DOCTO_REG_NEG AD ON RN.ID_REGLA = AD.ID_REGLA ");
	            sql.append("\n WHERE ESTATUS = 'A' ");
	            sql.append("\n  --PARA DOCUMENTO SE BUSCA CLASIFICACION 'D', PARA ACREEDORES 'A' ");
	            
	            if(list.get(i).getClasificacion().equals("I")){
	            	sql.append("\n  AND AD.CLASIFICACION_ACRE = 'I' ");
	            }else if(list.get(i).getClasificacion().equals("D")){
	            	sql.append("\n  AND AD.CLASIFICACION_ACRE = 'D' ");
	            }
	            
	            //sql.append("\n AND AD.TIPO_ACRE = 'I'");
	            sql.append("\n AND AD.DE_ACRE BETWEEN "+list.get(i).getDeAcre()+" AND "+list.get(i).getaAcre()+" ");
	            sql.append("\n AND AD.A_ACRE BETWEEN "+list.get(i).getDeAcre()+" AND "+list.get(i).getaAcre()+" ");
	            
	            System.out.println("CONTEO PARA VALIDAR DOCUMENTOS: " + sql.toString());
	            
	            count = jdbcTemplate.queryForInt(sql.toString());
				
	            if(count > 0){
	            	//Hacemos otra consulta para obtener el nombre de la regla de negocio.
	            	sql.delete(0, sql.length());
	    			
					sql.append("\n SELECT RN.REGLA_NEGOCIO ");
		            sql.append("\n FROM REGLA_NEGOCIO RN JOIN ACRE_DOCTO_REG_NEG AD ON RN.ID_REGLA = AD.ID_REGLA ");
		            sql.append("\n WHERE ESTATUS = 'A' ");
		            sql.append("\n AND AD.CLASIFICACION_ACRE = 'D' ");
		            
		            //sql.append("\n AND AD.TIPO_ACRE = 'I'");
		            sql.append("\n AND AD.DE_ACRE BETWEEN "+list.get(i).getDeAcre()+" AND "+list.get(i).getaAcre()+" ");
		            sql.append("\n AND AD.A_ACRE BETWEEN "+list.get(i).getDeAcre()+" AND "+list.get(i).getaAcre()+" ");
		            
		            System.out.println("OBTENER REGLA:- VALIDAR DOCUMENTOS: " + sql.toString());
		            
		            List<ReglasNegocioDto> reglas = new ArrayList<>();
		            
		            try{
		            	reglas = jdbcTemplate.query(sql.toString(), new RowMapper<ReglasNegocioDto>(){
							public ReglasNegocioDto mapRow(ResultSet rs, int idx) throws SQLException {
								ReglasNegocioDto regla = new ReglasNegocioDto();
								regla.setReglaNegocio(rs.getString("regla_negocio"));
								return regla;
							}});
		            	
		            }catch(Exception e){
		            	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		    			+ "P:Utilerias, C:ReglasNegocioDao, M:validarAcreDocto");
		    			resultado.put("error", "Error al obtener regla negocio");
		            }
		            	            
	            	resultado.put("error", "El rango del registro "+(i+1)+" ya existe en la regla de negocio: "+reglas.get(0).getReglaNegocio()+" ");
	            	
	            	break;
	            }
				
			}
			*/
	        
			if(count == 0){
				resultado.put("mensaje", "Ok"); //Todos los registros de documentos fueron validados correctamente y no existen en ninguna otra regla de negocios
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDao, M:validarDocumento");
			resultado.put("error", "Error al validar los registros");
		}
		return resultado;
		
	}


	/*
	 * TipoOper: 'I', 'E' � 'R' (Individual, Excel, Rango), se utiliza si tipoOper es I para validar un acreedor existe en persona (Solo se valida si es individual).
	 */
	@Override
	public Map<String, Object> validarAcreedores(List<AcreedorDoctoDto> list, String tipoOper) {
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		List<AcreedorDoctoDto> acreExistenReglaNegocio = new ArrayList<>();
		List<AcreedorDoctoDto> acreExistenReglaNegocioGlob = new ArrayList<>();
		//Array guardara el rango original guardando "DE", "A" y el numero de coincidencias que se detecten en otra regla de negocios
		//El cual har� referencia a la lista retornada acreExistenReglaNegocioGlob.
		List<AcreedorDoctoDto> listaOriginal = new ArrayList<>(); 
		List<String> listaAcreedoresNoExisten = new ArrayList<>();
		
		resultado.put("existe",acreExistenReglaNegocioGlob);
		resultado.put("listaOriginal",listaOriginal);
		resultado.put("listaAcre",listaAcreedoresNoExisten);
		
		StringBuffer sql= new StringBuffer();
		int count = 0;
		
		try{
			
			for(int i = 0; i < list.size(); i++){
				if(sql.length()>0)
					sql.delete(0, sql.length());
				
				//Si son iguales es individual y valida si existe en persona, si son diferentes es rango no se valida que existan en persona
				if(list.get(i).getDeAcre().equals(list.get(i).getaAcre()) && tipoOper.equals("I") ){
					
					sql.append("\n	SELECT COUNT(p2.equivale_persona) as consecutivo");
		            sql.append("\n	FROM persona p2 ");
		            sql.append("\n	WHERE CAST(p2.equivale_persona AS INTEGER) = "+Utilerias.validarCadenaSQL(list.get(i).getDeAcre())+" ");
		            sql.append("\n	AND P2.id_tipo_persona = 'P'");
		            
		            System.out.println("Es individual - valida existe en persona: " + sql.toString());
		            
		            count = jdbcTemplate.queryForInt(sql.toString());
		            
		            if(count <= 0){
		            	listaAcreedoresNoExisten.add(list.get(i).getDeAcre());
		            	resultado.put("error", "El acreedor "+list.get(i).getDeAcre()+" no existe.");
		            	return resultado;
		            }
		            
				}
				
				if(sql.length()>0)
					sql.delete(0, sql.length());
				
				sql.append("\n SELECT AD.DE_ACRE AS DE, AD.A_ACRE AS A, RN.REGLA_NEGOCIO ");
				sql.append("\n FROM REGLA_NEGOCIO RN JOIN ACRE_DOCTO_REG_NEG AD ON RN.ID_REGLA = AD.ID_REGLA ");
				sql.append("\n WHERE ESTATUS = 'A' ");
				sql.append("\n AND AD.CLASIFICACION_ACRE = 'A' ");
				sql.append("\n AND (CAST(AD.DE_ACRE AS BIGINT) BETWEEN "+Utilerias.validarCadenaSQL(list.get(i).getDeAcre())
									+" AND "+Utilerias.validarCadenaSQL(list.get(i).getaAcre())+" ");
				sql.append("\n 		OR CAST(AD.A_ACRE AS BIGINT) BETWEEN "+Utilerias.validarCadenaSQL(list.get(i).getDeAcre())
									+" AND "+Utilerias.validarCadenaSQL(list.get(i).getaAcre())+")");
				sql.append("\n AND AD.TIPO_ACRE = '"+Utilerias.validarCadenaSQL(list.get(i).getTipoAcre())+"' ");
				
				System.out.println("Existe en otra regla negocio??: " + sql.toString());
				
				try{
	            	acreExistenReglaNegocio = jdbcTemplate.query(sql.toString(), new RowMapper<AcreedorDoctoDto>(){
						public AcreedorDoctoDto mapRow(ResultSet rs, int idx) throws SQLException {
							AcreedorDoctoDto acreedor = new AcreedorDoctoDto();
							acreedor.setDeAcre(rs.getString("DE"));
							acreedor.setaAcre(rs.getString("A"));
							acreedor.setClasificacion(rs.getString("REGLA_NEGOCIO")); 
							//Inserta el nombre de la regla negocio que se usara para notificar en que regla se repite el acreedor
							return acreedor;
						}});
	            	
	            	if(acreExistenReglaNegocio.size() > 0){
	            		
	            		AcreedorDoctoDto acre = new AcreedorDoctoDto();
	            		acre.setDeAcre(list.get(i).getDeAcre());
	            		acre.setaAcre(list.get(i).getaAcre());
	            		//Le paso el numero de registros que arrojo el query para despues saber cuantos registros estan asociados a este registro.
	            		acre.setIdAcreDocto(acreExistenReglaNegocio.size()); 
	            		listaOriginal.add(acre);
	            		acreExistenReglaNegocioGlob.addAll(acreExistenReglaNegocio);
	            	}
	            	
	            }catch(Exception e){
	            	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
	    			+ "P:Utilerias, C:ReglasNegocioDao, M:validarAcreedor");
	    			resultado.put("error", "Error al obtener acreedores ");
	    			return resultado;
	            }
	            
			}
	        
			resultado.put("listaAcre", listaAcreedoresNoExisten); //Proveedores que no existen.
			resultado.put("listaOriginal", listaOriginal);	//Registros originales para las validaciones
			
			if(acreExistenReglaNegocioGlob.size()>0){
				resultado.put("existe", acreExistenReglaNegocioGlob); //Rangos de las coincidencias que se encontraron por cada registro
				resultado.put("mensaje", "A2D2"); //Est� de moda StarWars
            	return resultado;
			}else{
				resultado.put("mensaje", "Ok");
			}
			
			System.out.println("Termino");
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDao, M:validarAcreedores");
			resultado.put("error", "Error al validar los registros");
		}
		return resultado;
	}
	
	@Override
	public Map<String, String> validarEmpresas(List<EmpresaRegNegDto> list, int idRegla, int idUsuario) {
		Map<String, String> resultado = new HashMap<>();
		resultado.put("error", "");
		resultado.put("mensaje", "");
		
		StringBuffer sql= new StringBuffer();
		int count = 0;
		
		try{
			
			for(int i = 0; i < list.size(); i++){
				if(sql.length()>0)
					sql.delete(0, sql.length());
			
				sql.append("\n SELECT COUNT(E.NO_EMPRESA) AS REG ");
	            sql.append("\n FROM EMPRESA E ");
	            sql.append("\n WHERE E.NO_EMPRESA = "+list.get(i).getNoEmpresa()+" ");
	            
	            System.out.println("CONTEO PARA VALIDAR Empresas: " + sql.toString());
	            
	            count = jdbcTemplate.queryForInt(sql.toString());
				
	            if(count == 0){
	            	resultado.put("error", "La empresa "+list.get(i).getNoEmpresa()+", "+list.get(i).getNombreEmpresa()+", No existe.");
	            	return resultado;
	            }
	            
	            if(sql.length()>0)
					sql.delete(0, sql.length());
	            
    			sql.append("\n SELECT COUNT(e.no_empresa) AS REG ");
    			sql.append("\n FROM empresa e LEFT JOIN empresa_reg_neg ern on e.no_empresa = ern.no_empresa ");
    			sql.append("\n JOIN regla_negocio rn on ern.id_regla = rn.id_regla");
    			sql.append("\n WHERE rn.id_regla = "+ idRegla +" ");
       			sql.append("\n AND ern.no_empresa = "+ list.get(i).getNoEmpresa() +" ");
    			
       			System.out.println("CONTEO PARA VALIDAR Empresas - eXISTE EN EMPRESAS_REG_NEG: " + sql.toString());
	            
	            count = jdbcTemplate.queryForInt(sql.toString());
				
	            if(count > 0){
	            	resultado.put("error", "La empresa "+list.get(i).getNoEmpresa()+", "+list.get(i).getNombreEmpresa()+", ya est� asignada.");
	            	return resultado;
	            }
       			
	            if(sql.length()>0)
					sql.delete(0, sql.length());
	            
    			sql.append("\n SELECT COUNT(e.no_empresa) AS REG ");
    			sql.append("\n FROM empresa e  ");
    			sql.append("\n WHERE e.no_empresa IN (SELECT distinct no_empresa FROM USUARIO_EMPRESA WHERE no_usuario = " + idUsuario + " ) ");
    			sql.append("\n AND e.NO_EMPRESA NOT IN ( ");
    			sql.append("\n 							SELECT distinct ern2.no_empresa ");
    			sql.append("\n 							FROM EMPRESA_REG_NEG ern2 JOIN regla_negocio rn2 on ern2.id_regla = rn2.id_regla ");
    			sql.append("\n 							WHERE rn2.id_regla = "+idRegla+") ");
    			sql.append("\n ORDER BY e.nom_empresa");	
	    			
    			System.out.println("CONTEO PARA VALIDAR Empresas - SIN ASIGNAR: " + sql.toString());
	            
	            count = jdbcTemplate.queryForInt(sql.toString());
				
	            if(count == 0){
	            	resultado.put("error", "La empresa "+list.get(i).getNoEmpresa()+", "+list.get(i).getNombreEmpresa()+", no est� asignada al usuario.");
	            	return resultado;
	            }
	            
			}
	      
			resultado.put("mensaje", "Ok");
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDao, M:validarEmpresas");
			resultado.put("error", "Error al validar las empresas");
		}
		return resultado;
	}

	@Override
	public List<RubroRegNeg> obtenerRubros(boolean bAsignados, int idRegla) {

		StringBuffer sql = new StringBuffer(); 
		
		//bAsignados == true -> Indica que es el stored para las empresas asignadas.
		//bAsignados == false -> Indica que es el stored para las empresas sin asignar.
		if(bAsignados){
			//RUBROS ASIGNADOS
			sql.append("\n SELECT R.ID_RUBRO, R.DESC_RUBRO ");
			sql.append("\n FROM CAT_RUBRO R LEFT JOIN RUBRO_REG_NEG RR ON R.ID_RUBRO = RR.ID_RUBRO ");
			sql.append("\n 							 JOIN REGLA_NEGOCIO RN ON RR.ID_REGLA = RN.ID_REGLA");
			sql.append("\n WHERE RN.ID_REGLA = "+ idRegla +" ");
		}else{
			//RUBROS SIN ASIGNAR
			sql.append("\n SELECT R.ID_RUBRO, R.DESC_RUBRO ");
			sql.append("\n FROM CAT_RUBRO R ");
			sql.append("\n WHERE R.ID_RUBRO NOT IN ( ");
			sql.append("\n 						    SELECT DISTINCT RR.ID_RUBRO ");
			sql.append("\n 							FROM RUBRO_REG_NEG RR JOIN REGLA_NEGOCIO RN ON RR.ID_REGLA = RN.ID_REGLA ");
			sql.append("\n 							WHERE RN.ID_REGLA = "+idRegla+" ) ");
			sql.append("\n ORDER BY R.DESC_RUBRO ");	
		}

		List<RubroRegNeg> rubros = new ArrayList<RubroRegNeg>();
		
		try{
			 rubros = jdbcTemplate.query(sql.toString(), new RowMapper<RubroRegNeg>(){
				public RubroRegNeg mapRow(ResultSet rs, int idx) throws SQLException {
					RubroRegNeg rubro = new RubroRegNeg();
					rubro.setIdRubro(rs.getString("id_rubro"));
					rubro.setDescRubro(rs.getString("desc_rubro"));
					return rubro;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDao, M:obtenerRubros");
		}
		return rubros;
	}
	
	@Override
	public String procesaSimulador(int idRegla){
		String idsFolioDet = "";
		
		/*consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		try{
			idsFolioDet = consultasGenerales.ejecutarBuscaMovRegla(idRegla).get("V_folios").toString();
			
			System.out.println(idsFolioDet);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDaoImpl, M:obtenerRenglonesConRegla");
		}*/
		
		Map<String, Object> resultado= new HashMap<String, Object>();
		StringBuffer sql = new StringBuffer(); 
		int cont = 0;
		
		try {
			
			//REALIZO UNA COPIA DE SELECCION_AUTOMATICA_GRUPO PARA DESPUES PROCESAR LOS MOVIMIENTOS SIN AFECTAR LAS PROPUESTAS ORIGINALES
			sql.append("\n DELETE SELECCION_AUTOMATICA_GRUPO_SIM ");
			
			cont = jdbcTemplate.update(sql.toString());
			
			if(sql.length()>0)
				sql.delete(0, sql.length());
			
			//POR SI NO SE BORRO CUANDO SE OCULTO LA VENTANA DEL SIMULADOR...
			sql.append("\n DELETE TMP_MOVIMIENTO_SIM ");
			
			cont = jdbcTemplate.update(sql.toString());
			
			if(sql.length()>0)
				sql.delete(0, sql.length());
			
			
			//
			sql.append("\n INSERT INTO SELECCION_AUTOMATICA_GRUPO_SIM ");
			sql.append("\n SELECT * from SELECCION_AUTOMATICA_GRUPO ");
			sql.append("\n WHERE FECHA_PAGO IS NULL ");
			sql.append("\n AND USUARIO_UNO IS NULL ");
			sql.append("\n AND USUARIO_DOS IS NULL ");
			sql.append("\n AND USUARIO_TRES IS NULL ");

			cont = jdbcTemplate.update(sql.toString());

			//jdbcTemplate.setQueryTimeout(180);
			
			//Tipo_oper == 1 - Movimiento que no existen en ninguna propuesta.
			StoredProcedure stored2MovNuevos = new StoredProcedure(jdbcTemplate, "SP_BUSCA_MOV_REGLA_SIMULADOR");
			stored2MovNuevos.declareParameter("V_id_regla", idRegla, StoredProcedure.IN);
			//stored2MovNuevos.declareParameter("V_folio", Types.CLOB, StoredProcedure.OUT);
			stored2MovNuevos.declareParameter("V_tipo_oper", 1, StoredProcedure.IN);
			resultado = stored2MovNuevos.execute();
			
			//APLICA REGLA, GENERA LAS PROPUESTAS Y MODIFICA LOS MOVIMIENTOS.
			StoredProcedure storedProcesa = new StoredProcedure(jdbcTemplate, "SP_CICLO_APLICA_REGLA_SIM");
			storedProcesa.declareParameter("V_id_regla", idRegla, StoredProcedure.IN);
			//storedProcesa.declareParameter("V_folios", idsFolioDet, StoredProcedure.IN);
			//storedProcesa.declareParameter("V_folios", clob, StoredProcedure.IN);
			storedProcesa.declareParameter("V_id_usuario", GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario(), StoredProcedure.IN);
			storedProcesa.declareParameter("V_tipo_oper", 1, StoredProcedure.IN);
			//storedProcesa.declareParameter("V_result", Types.INTEGER, StoredProcedure.OUT);
			//storedProcesa.declareParameter("V_mensaje", Types.VARCHAR, StoredProcedure.OUT);
			resultado = storedProcesa.execute();
			
			/*Clob clob = null;
			
			if(resultado.size()>0){
				clob = (Clob)resultado.get("V_folio");
				idsFolioDet = clobToString(clob);
				System.out.println("length: " + idsFolioDet.length());
			}*/
			
			
			//tipo_oper == 2 - Busca los movimientos que ya estan asignados a una propuesta
			//La ejecuto primero por si se encuentran movimientos marcarlos.
			StoredProcedure storedMovExisten = new StoredProcedure(jdbcTemplate, "SP_BUSCA_MOV_REGLA_SIMULADOR");
			storedMovExisten.declareParameter("V_id_regla", idRegla, StoredProcedure.IN);
			//storedMovExisten.declareParameter("V_folio", Types.CLOB, StoredProcedure.OUT);
			storedMovExisten.declareParameter("V_tipo_oper", 2, StoredProcedure.IN);
			resultado = storedMovExisten.execute();
			
			
			//APLICA REGLA, GENERA LAS PROPUESTAS Y MODIFICA LOS MOVIMIENTOS.
			StoredProcedure storedProcesa2 = new StoredProcedure(jdbcTemplate, "SP_CICLO_APLICA_REGLA_SIM");
			storedProcesa2.declareParameter("V_id_regla", idRegla, StoredProcedure.IN);
			//storedProcesa2.declareParameter("V_folios", idsFolioDet, StoredProcedure.IN);
			//storedProcesa2.declareParameter("V_folios", clob, StoredProcedure.IN);
			storedProcesa2.declareParameter("V_id_usuario", GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario(), StoredProcedure.IN);
			storedProcesa2.declareParameter("V_tipo_oper", 2, StoredProcedure.IN);
			//storedProcesa2.declareParameter("V_id_usuario", 2, StoredProcedure.IN);
			//storedProcesa2.declareParameter("V_result", Types.INTEGER, StoredProcedure.OUT);
			//storedProcesa2.declareParameter("V_mensaje", Types.VARCHAR, StoredProcedure.OUT);
			resultado = storedProcesa2.execute();
	    
			
			//Actualizo la sesion del usuario para que le de tiempo otros 5 minutos de revisar las propuestas
			updateLoginSimulador();
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDaoImpl, M:procesaSimulador");
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
			"P:Utilerias, C:ReglasNegocioDaoImpl, M:procesaSimulador");
		}
		
		return idsFolioDet;
	}
	
	/*
	private String clobToString(Clob data) {
	    StringBuilder sb = new StringBuilder();
	    try {
	        Reader reader = data.getCharacterStream();
	        BufferedReader br = new BufferedReader(reader);

	        String line;
	        while(null != (line = br.readLine())) {
	            sb.append(line);
	        }
	        br.close();
	    } catch (SQLException e) {
	        bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:clobToString");
	    } catch (IOException e) {
	    	 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:BancaElectronica, C:BancaBuzonesDaoImpl, M:clobToString");
	    }
	    return sb.toString();
	}
	*/
	
	@Override
	public List<SeleccionAutomaticaGrupoDto> consultarSeleccionAutomaticaSIM(ConsultaPropuestasDto dtoIn){
		StringBuffer sql= new StringBuffer();
		List<SeleccionAutomaticaGrupoDto> list= new ArrayList<SeleccionAutomaticaGrupoDto>();
		try{
            sql.append("SELECT sa.cve_control, sa.id_grupo_flujo, sa.cupo_total - sa.cupo_automatico as propuesto, ");
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
            sql.append("\n case when charindex(coalesce(sa.concepto,''),'MN',1) > 0 then 'MN' ");
            sql.append("\n else");
            sql.append("\n      case when charindex(coalesce(sa.concepto,''),'DLS',1) > 0 then 'DLS' ");
            sql.append("\n 		else ");
            sql.append("\n 	    	case when charindex(coalesce(sa.concepto,''),'EUR',1) > 0 then 'EUR' ");
            sql.append("\n 			else ");
            sql.append("\n   		   'OTR' ");
            sql.append("\n 			end ");
            sql.append("\n 		end ");
            sql.append("\n end as divisa, ");
            sql.append("\n case when charindex(coalesce(sa.cve_control,''),'MA',1) > 0 then 'MA' ");
            sql.append("\n else ");
            sql.append("\n 		case when charindex(coalesce(sa.cve_control,''),'MR',1) > 0 then 'MR' ");
            sql.append("\n 		else ");
            sql.append("\n 			'M' ");
            sql.append("\n 		end ");
            sql.append("\n end as origenProp, ");
            
            sql.append("\n ( SELECT CASE WHEN  (sa.concepto not like 'M-%' AND sa.concepto not like 'R-%') THEN DESC_FORMA_PAGO ");
            sql.append("\n          ELSE '' ");
            sql.append("\n          END ");
            sql.append("\n FROM CAT_FORMA_PAGO ");
            sql.append("\n WHERE TO_CHAR(ID_FORMA_PAGO) = SUBSTR(sa.concepto, LENGTH(sa.concepto), LENGTH(sa.concepto)) ");
            sql.append("\n  ) AS FORMA_PAGO ");
            
        
            
            /***************/
            sql.append("\n  FROM seleccion_automatica_grupo_sim sa ");
            sql.append("\n       LEFT JOIN cat_grupo_cupo c On (sa.id_grupo = c.id_grupo_cupo)");
            sql.append("\n       LEFT JOIN cat_grupo_flujo f On (sa.id_grupo_flujo = f.id_grupo_flujo) ");
            sql.append("\n where fecha_pago is null");
            sql.append("\n AND MOTIVO_RECHAZO IN ('X','U') ");
            
	        //system.out.println("\nQuery de las propuestas: " + sql);
	        
	        list= jdbcTemplate.query(sql.toString(), new RowMapper<SeleccionAutomaticaGrupoDto>(){
				public SeleccionAutomaticaGrupoDto mapRow(ResultSet rs, int idx) throws SQLException {
					SeleccionAutomaticaGrupoDto cons = new SeleccionAutomaticaGrupoDto();
					cons.setCveControl(rs.getString("cve_control"));
					cons.setIdGrupoFlujo(rs.getInt("id_grupo_flujo"));
					cons.setCupoTotal(rs.getDouble("cupo_total"));
					cons.setDescGrupoCupo(rs.getString("desc_grupo_cupo"));
					cons.setFechaPropuesta(funciones.ponerFechaSola(rs.getDate("fecha_propuesta")));
					cons.setDescGrupoFlujo(rs.getString("desc_grupo_flujo"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setUsuarioUno(rs.getInt("usuario_uno"));
					cons.setUser1(rs.getString("user1"));
					cons.setUsuarioDos(rs.getInt("usuario_dos"));
					cons.setUser2(rs.getString("user2"));
					cons.setUsuarioTres(rs.getInt("usuario_tres"));
					cons.setUser3(rs.getString("user3"));
					cons.setNivelAutorizacion(rs.getInt("nivel_autorizacion"));
					cons.setFecLimiteSelecc(funciones.ponerFechaSola(rs.getDate("fec_limite_selecc")));
					cons.setDivisa(rs.getString("divisa"));
					cons.setOrigenPropuesta(rs.getString("origenProp"));
					cons.setViaPago(rs.getString("FORMA_PAGO"));
					return cons;
				}});
            
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegociosDaoImp, M:consultarSeleccionAutomaticaSIM");
		}
		return list;
	}

	//COPIA DEL DETALLE DE BITACORA, RETORNA LOS DETALLES PARA EL SIMULADOR.
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
		    sql.append( "\n      m.agrupa3, '' as b_servicios, m.no_pedido, ");
		    sql.append( "\n      CASE WHEN m.id_divisa = 'MN' THEN m.importe ");
		    sql.append( "\n           ELSE m.importe * coalesce( ");
		    sql.append( "\n               ( SELECT max(vd.valor) ");
		    //sql.append( "\n               ( SELECT vd.valor ");
		    sql.append( "\n                 FROM valor_divisa vd ");
		    sql.append( "\n                 WHERE vd.id_divisa = m.id_divisa ");
		    sql.append( "\n                     and vd.fec_divisa =  TO_DATE('"+Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFecha()).substring(0,10)) +"', 'DD/MM/YYYY') ), ");
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
		    
		    sql.append( "\n      TO_DATE('"+Utilerias.validarCadenaSQL(funciones.ponerFecha(dto.getFecha()).substring(0,10)) +"', 'DD/MM/YYYY') - m.fec_valor_original as dias,");
		    
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
		    
		    sql.append( "	 ,m.invoice_type ");
		    sql.append( "	 ,m.COMENTARIO2 ");
		    
		    sql.append( "  FROM TMP_MOVIMIENTO_SIM m ");
		    sql.append( "      LEFT JOIN cat_banco b ON ( m.id_banco_benef = b.id_banco )");
		    sql.append( "      LEFT JOIN cat_estatus e ON (m.id_estatus_mov = e.id_estatus ");
		    sql.append( "          AND e.clasificacion = 'MOV' ) ");
		    sql.append( "      LEFT JOIN cat_caja cj ON (m.id_caja = cj.id_caja) ");
		    sql.append( "      LEFT JOIN autorizacionesMov am ON (m.no_folio_det = am.no_folio_det) ");
		    
	        sql.append("\n	  LEFT JOIN PERSONA p ON (cast(m.no_cliente as integer) = p.no_persona), ");
		    
		    sql.append("\n      cat_tipo_operacion co, cat_cve_operacion cv, ");
		    sql.append("\n      cat_forma_pago fp ");
		    
	        sql.append( "\n  WHERE ");
	    
		    if(dto.getIdGrupoEmpresa()>0){
		        sql.append( "  m.no_empresa in (select no_empresa from grupo_empresa ");
		        sql.append( "\n                   where id_grupo_flujo = "+Utilerias.validarCadenaSQL(dto.getIdGrupoEmpresa())+") AND");
		    }
		    
		    sql.append( "\n	   m.no_cliente not in ('"+"999999"+"','"+"999998"+"') ");
		    sql.append( "\n	  AND m.id_tipo_movto = 'E'  ");
		    sql.append( "\n	  AND (m.origen_mov <> 'INV' or m.origen_mov is NULL)");
		    sql.append( "\n	  AND m.id_tipo_operacion = co.id_tipo_operacion ");
		    sql.append( "\n	  AND co.no_empresa = 1 ");
		    sql.append( "\n	  AND m.id_cve_operacion = cv.id_cve_operacion ");
		    sql.append( "\n	  AND m.id_estatus_mov in ('N','C','F','L') ");
		    sql.append( "\n	  AND m.id_forma_pago = fp.id_forma_pago ");
		    sql.append( "\n	  AND m.id_forma_pago in (1,3,5,6,7,9,10) ");
		    sql.append( "\n	  AND p.id_tipo_persona = 'P' ");
		    
		    sql.append( "\n		  And m.cve_control = '"+Utilerias.validarCadenaSQL(dto.getCveControl())+"' ");
	        
	        sql.append( "\n  order by no_cheque, beneficiario, fec_valor_original "); //Se pidio ordenar por sociedad
	        
	        //system.out.println("query que saca el detalle: " + sql);
	        
	        listDetalle= jdbcTemplate.query(sql.toString(), new RowMapper<MovimientoDto>(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons = new MovimientoDto();
					cons.setNoDocto(rs.getString("no_docto"));
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setImporteOriginal(rs.getDouble("importe_original"));
					cons.setDescEstatus(rs.getString("desc_estatus"));
					cons.setNoFactura(rs.getString("no_factura"));
					cons.setFecValorStr(funciones.ponerFechaSola(rs.getDate("fec_valor")));
					//cons.setFecValorOriginalStr(funciones.ponerFechaSola(rs.getDate("fec_valor_original")));
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
					cons.setComentario2(rs.getString("comentario2"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConsultaPropuestasDao, M:consultarDetalle");
			 
		}
		return listDetalle;
	}

	@Override
	public List<SeleccionAutomaticaGrupoDto> existeSeleccionAutomaticaGrupoSIM(String cveControl) {

		StringBuffer sql = new StringBuffer();
		List<SeleccionAutomaticaGrupoDto> list = new ArrayList<>();
		//int count = 0;
		
		try{
				
			sql.append("\n SELECT fecha_propuesta, id_grupo_flujo, monto_maximo ");
			sql.append("\n 		  ,cupo_manual, cupo_automatico, cupo_total, fec_limite_selecc ");
			sql.append("\n 		  ,cve_control, concepto, usuario_uno, usuario_dos, usuario_tres  ");
			sql.append("\n        ,motivo_rechazo, fecha_pago ");
			sql.append("\n FROM SELECCION_AUTOMATICA_GRUPO_SIM ");
			sql.append("\n WHERE CVE_CONTROL = '"+cveControl+"' ");
			
			 list= jdbcTemplate.query(sql.toString(), new RowMapper<SeleccionAutomaticaGrupoDto>(){
					public SeleccionAutomaticaGrupoDto mapRow(ResultSet rs, int idx) throws SQLException {
						SeleccionAutomaticaGrupoDto cons = new SeleccionAutomaticaGrupoDto();
						cons.setCveControl(rs.getString("cve_control"));
						cons.setIdGrupoFlujo(rs.getInt("id_grupo_flujo"));
						//cons.setPropuesto(rs.getDouble("propuesto"));
						cons.setCupoTotal(rs.getDouble("cupo_total"));
						//cons.setDescGrupoCupo(rs.getString("desc_grupo_cupo"));
						cons.setFechaPropuesta(funciones.ponerFechaSola(rs.getDate("fecha_propuesta")));
						//cons.setIdGrupo(rs.getInt("id_grupo"));
						//cons.setDescGrupoFlujo(rs.getString("desc_grupo_flujo"));
						cons.setConcepto(rs.getString("concepto"));
						cons.setUsuarioUno(rs.getInt("usuario_uno"));
						//cons.setUser1(rs.getString("user1"));
						cons.setUsuarioDos(rs.getInt("usuario_dos"));
						//cons.setUser2(rs.getString("user2"));
						cons.setUsuarioTres(rs.getInt("usuario_tres"));
						//cons.setUser3(rs.getString("user3"));
						//cons.setNivelAutorizacion(rs.getInt("nivel_autorizacion"));
						//cons.setIdDivision(rs.getString("id_division"));
						//cons.setNumIntercos(rs.getInt("NumIntercos"));
						//cons.setTotalIntercos(rs.getInt("TotalIntercos"));
						cons.setFecLimiteSelecc(funciones.ponerFechaSola(rs.getDate("fec_limite_selecc")));
						//cons.setDivisa(rs.getString("divisa"));
						//cons.setOrigenPropuesta(rs.getString("origenProp"));
						//cons.setViaPago(rs.getString("FORMA_PAGO"));
						return cons;
					}});
			 
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDao, M:existeDoctoTes");
			
			return list;
		}
		
		return list;
	}
	
	//EMS 10/02/16 - Actualiza las tablas de seleccion automatica grupo y movimiento (desde seleccion_automatica_grupo_sim y tmp_movimiento_sim)
	//, en base a la simulaci�n de la regla de negocios creada.
	@Override
	public Map<String, Object> actualizarMovtosSimulador(int idRegla) {

		StringBuffer sql = new StringBuffer();
		List<MovimientoDto> listMovtos = new ArrayList<>();
		List<SeleccionAutomaticaGrupoDto> listSAG = new ArrayList<>();
		Map<String, Object> result = new HashMap<>();
		result.put("error", "");
		result.put("msgUsuario", "");
		int count = 0;
		
		try{
			
			//Antes de realizar cualquier cambio, se vuelve a revisar la sesi�n.
			//Si pas� de 5 minutos es posible que haya entrado otro usuario, se valida si el registro de la tabla
			//Es el usuario logueado, si es el usuario logueado no hay problema, pasa, si no se sale avisando.
			//que usuario se logue�
			result = validarUsuarioLogueado();
			
			if(!result.get("error").equals("")){
				return result;
			}
			
			//Actualizado 16/03/2016 - Se borran todas las propuestas de la regla y sus movimientos
			//Para insertar los nuevos.
			
			//Obtenemos la lista de SAG que se van a eliminar.
			sql.append("\n SELECT cve_control ");
			sql.append("\n FROM SELECCION_AUTOMATICA_GRUPO ");
			sql.append("\n WHERE CONCEPTO LIKE '%-'+(SELECT top 1 REGLA_NEGOCIO FROM REGLA_NEGOCIO_SIM )'-%' ");
			sql.append("\n AND FECHA_PAGO IS NULL ");
			
			 listSAG = jdbcTemplate.query(sql.toString(), new RowMapper<SeleccionAutomaticaGrupoDto>(){
					public SeleccionAutomaticaGrupoDto mapRow(ResultSet rs, int idx) throws SQLException {
						SeleccionAutomaticaGrupoDto cons = new SeleccionAutomaticaGrupoDto();
						cons.setCveControl(rs.getString("cve_control"));
						return cons;
					}});
			 
			//limpiamos los movimientos
			 if(listSAG.size()>0){
				 
				 for(int j=0; j<listSAG.size(); j++){
					 
					 if(sql.length() > 0)
							sql.delete(0, sql.length());
					 
					 sql.append("\n UPDATE MOVIMIENTO ");
					 sql.append("\n SET FEC_PROPUESTA = NULL ");
					 sql.append("\n ,CVE_CONTROL = NULL ");
					 sql.append("\n WHERE CVE_CONTROL = '"+listSAG.get(j).getCveControl()+"' ");
					 sql.append("\n AND ID_TIPO_OPERACION = 3000 ");
					 sql.append("\n AND ID_ESTATUS_MOV = 'N' ");
					 
					 count = jdbcTemplate.update(sql.toString());
					
					//sin error puede que no actualice nada.
					 
					 if(sql.length() > 0)
						 sql.delete(0, sql.length());
					 
				 	 sql.append("\n INSERT INTO BITACORA_PROPUESTAS VALUES( ");
					 sql.append("\n SYSDATE, "+GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+" ");
					 sql.append("\n ,'ElimPropuestas', '"+listSAG.get(j).getCveControl()+"' ");
					 sql.append("\n ,'ELIMINACI�N POR SIMULADOR','LIMPIA PROPUESTAS') ");
					
					 count = jdbcTemplate.update(sql.toString());
					
					 if(count <= 0){
					 	 result.put("error", "Error al insertar en bitacora.");
					 	 return result;
					 }
						
					 if(sql.length() > 0)
							sql.delete(0, sql.length());
					 
					 sql.append("\n DELETE FROM SELECCION_AUTOMATICA_GRUPO ");
					 sql.append("\n WHERE CVE_CONTROL = '"+listSAG.get(j).getCveControl()+"' ");
					 
					 count = jdbcTemplate.update(sql.toString());
					 
					 if(count <= 0){
						 result.put("error", "Error al limpiar propuestas.");
						 return result;
					 }
					 
				 }
			 }
			 
			 if(sql.length() > 0)
					sql.delete(0, sql.length());
			 
			 listSAG.clear();
			
			//Obtenemos la lista de SAG que se van a insertar/actualizar desde la tabla temporal.
			sql.append("\n SELECT fecha_propuesta, id_grupo_flujo, monto_maximo ");
			sql.append("\n 		  ,cupo_manual, cupo_automatico, cupo_total, fec_limite_selecc ");
			sql.append("\n 		  ,cve_control, concepto ");
			sql.append("\n FROM SELECCION_AUTOMATICA_GRUPO_SIM ");
			sql.append("\n WHERE MOTIVO_RECHAZO IS NOT NULL ");
			
			 listSAG = jdbcTemplate.query(sql.toString(), new RowMapper<SeleccionAutomaticaGrupoDto>(){
					public SeleccionAutomaticaGrupoDto mapRow(ResultSet rs, int idx) throws SQLException {
						SeleccionAutomaticaGrupoDto cons = new SeleccionAutomaticaGrupoDto();
						cons.setCveControl(rs.getString("cve_control"));
						cons.setIdGrupoFlujo(rs.getInt("id_grupo_flujo"));
						cons.setCupoTotal(rs.getDouble("cupo_total"));
						cons.setMontoMaximo(rs.getDouble("monto_maximo"));
						cons.setCupoManual(rs.getDouble("cupo_manual"));
						cons.setCupoAutomatico(rs.getDouble("cupo_automatico"));
						cons.setFechaPropuesta(funciones.ponerFechaSola(rs.getDate("fecha_propuesta")));
						cons.setConcepto(rs.getString("concepto"));
						cons.setFecLimiteSelecc(funciones.ponerFechaSola(rs.getDate("fec_limite_selecc")));
						
						return cons;
					}});
			
			//Actualizamos SAG
			if(listSAG.size()>0){
				
				for(int i=0; i<listSAG.size();i++){
					
					if(sql.length() > 0)
						sql.delete(0, sql.length());
					
					if(existeSeleccionAutomaticaGrupo(listSAG.get(i).getCveControl())){
						
						//SE REVISA QUE EL NUEVO SALDO MODIFICADO NO SEA 0, SI ES 0 SE INSERTA EN BITACORA Y SE ELIMINA EL REGISTRO.
						if(listSAG.get(i).getMontoMaximo() <= 0 
								&& listSAG.get(i).getCupoAutomatico() <= 0
								&& listSAG.get(i).getCupoTotal() <= 0 ){
						
							sql.append("\n INSERT INTO BITACORA_PROPUESTAS VALUES( ");
							sql.append("\n getdate(), "+GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+" ");
							sql.append("\n ,'ElimPropuestas', '"+listSAG.get(i).getCveControl()+"' ");
							sql.append("\n ,'ELIMINACI�N POR SIMULADOR','CUPO EN CERO') ");
							
							count = jdbcTemplate.update(sql.toString());
							
							if(count <= 0){
								result.put("error", "Error al insertar en bitacora.");
								return result;
							}
							

							if(sql.length() > 0)
								sql.delete(0, sql.length());
							
							sql.append("\n DELETE FROM SELECCION_AUTOMATICA_GRUPO ");
							sql.append("\n WHERE CVE_CONTROL = '"+listSAG.get(i).getCveControl()+"' ");
							
							count = jdbcTemplate.update(sql.toString());
							
							if(count <= 0){
								result.put("error", "Error al eliminar propuesta en cupo cero.");
								return result;
							}
							
							
						}else{
							
							sql.append("\n UPDATE SELECCION_AUTOMATICA_GRUPO ");
							sql.append("\n SET MONTO_MAXIMO = "+listSAG.get(i).getMontoMaximo()+" ");
							sql.append("\n ,CUPO_AUTOMATICO = "+listSAG.get(i).getCupoAutomatico()+" ");
							sql.append("\n ,CUPO_TOTAL = "+listSAG.get(i).getCupoTotal()+" ");
							sql.append("\n WHERE CVE_CONTROL = '"+listSAG.get(i).getCveControl()+"' ");
							
							count = jdbcTemplate.update(sql.toString());
							
							if(count <= 0){
								result.put("error", "Error al actualizar propuestas.");
								return result;
							}
							
						}
						
					}else{
						sql.append("\n INSERT INTO SELECCION_AUTOMATICA_GRUPO ");
						
						sql.append("\n (ID_GRUPO, FECHA_PROPUESTA, ID_GRUPO_FLUJO, MONTO_MAXIMO, CUPO_MANUAL ");
						sql.append("\n ,CUPO_AUTOMATICO, CUPO_TOTAL, FEC_LIMITE_SELECC, CVE_CONTROL ");
						sql.append("\n ,CONCEPTO) ");
						
						sql.append("\n SELECT 0 AS ID_GRUPO, FECHA_PROPUESTA, ID_GRUPO_FLUJO, MONTO_MAXIMO, CUPO_MANUAL ");
						sql.append("\n ,CUPO_AUTOMATICO, CUPO_TOTAL, FEC_LIMITE_SELECC, CVE_CONTROL ");
						sql.append("\n ,CONCEPTO ");
						sql.append("\n FROM SELECCION_AUTOMATICA_GRUPO_SIM ");
						sql.append("\n WHERE CVE_CONTROL = '"+listSAG.get(i).getCveControl()+"' ");
						
						count = jdbcTemplate.update(sql.toString());
						
						if(count <= 0){
							result.put("error", "Error al insertar nueva propuesta.");
							return result;
						}
						
					}
				}
			}
			
			 if(sql.length() > 0)
				 sql.delete(0, sql.length());
			 
			//OBTENGO LOS MOVIMIENTOS DE LA TEMPORAL PARA ACTUALIZARLOS EN LA TABLA MOVIMIENTO.
			//SOLO SE ACTUALIZA FECHA_PROPUESTA Y CVE_CONTROL
			
			sql.append("\n SELECT FEC_PROPUESTA, CVE_CONTROL, NO_FOLIO_DET, COMENTARIO2 ");
			sql.append("\n FROM TMP_MOVIMIENTO_SIM ");
			sql.append("\n WHERE COMENTARIO1 = 'X' ");
			
			 listMovtos = jdbcTemplate.query(sql.toString(), new RowMapper<MovimientoDto>(){
					public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
						MovimientoDto movto = new MovimientoDto();
						movto.setCveControl(rs.getString("CVE_CONTROL"));
						movto.setFecPropuestaStr(funciones.ponerFechaSola(rs.getDate("FEC_PROPUESTA")));
						movto.setNoFolioDet(rs.getInt("NO_FOLIO_DET"));
						movto.setComentario2(rs.getString("COMENTARIO2")); //SI TIENE DATO, TIENE GUARDADO LA CVE_CONTROL DE LA PROPUESTA ANTERIOR. 
						return movto;
					}});
			 
			 if(listMovtos.size()>0){
				 //Actualiza los movimientos
				 
				 for(int i=0; i < listMovtos.size(); i++){
					 
					 if(sql.length() > 0)
						 sql.delete(0, sql.length());
					
					//Se actualizan los movimientos que se eliminan de una propuesta
					//SOLO SE ACTUALIZA FECHA_PROPUESTA Y CVE_CONTROL
					 /*if(listMovtos.get(i).getComentario2() != null && !listMovtos.get(i).getComentario2().equals("")){
						 sql.append("\n UPDATE MOVIMIENTO ");
						 sql.append("\n SET FEC_PROPUESTA = NULL ");
						 sql.append("\n ,CVE_CONTROL = NULL ");
						 sql.append("\n WHERE CVE_CONTROL = '"+listMovtos.get(i).getComentario2()+"' ");
						 sql.append("\n AND ID_TIPO_OPERACION = 3000 ");
						 //NOT IN DE TODA LA TABLA TEMPORAL, YA QUE SI LO FILTRO POR CVE_CONTROL PUEDE SER QUE UN MOVIMIENTO
						 //QUE EST� QUITANDO CAIGA EN OTRA PROPUESTA DE LAS SIMULADAS, ENTONCES PUEDE DESCUADRAR LOS MOVIMIENTOS DE LAS PROPUESTAS.
						 sql.append("\n AND NO_FOLIO_DET NOT IN (SELECT NO_FOLIO_DET ");
						 sql.append("\n 						FROM TMP_MOVIMIENTO_SIM ");
						 sql.append("\n 						)");
						 
						 count = jdbcTemplate.update(sql.toString());
						 
					 }
					*/
					
					 if(sql.length() > 0)
						 sql.delete(0, sql.length());
					 
					 sql.append("\n UPDATE MOVIMIENTO ");
					 sql.append("\n SET FEC_PROPUESTA = '"+listMovtos.get(i).getFecPropuestaStr()+"' ");
					 sql.append("\n ,CVE_CONTROL = '"+listMovtos.get(i).getCveControl()+"' ");
					 sql.append("\n WHERE NO_FOLIO_DET = '"+listMovtos.get(i).getNoFolioDet()+"' ");
					 
					 /*if(listMovtos.get(i).getComentario2() != null && !listMovtos.get(i).getComentario2().equals("")){
						 sql.append("\n AND CVE_CONTROL = '"+listMovtos.get(i).getComentario2()+"' ");
					 }*/
						
					count = jdbcTemplate.update(sql.toString());
					
					if(count <= 0){
						result.put("error", "Error al actualizar movimiento.");
						return result;
					}
					
				 }
				 
			 }
			 
			 
			 //Se guarda la regla de negocio de la temporal.
			 if(sql.length() > 0)
				 sql.delete(0, sql.length());
			 
			 sql.append("\n INSERT INTO REGLA_NEGOCIO ");
			 sql.append("\n SELECT * FROM REGLA_NEGOCIO_SIM ");
			 
			 count = jdbcTemplate.update(sql.toString());
			 
			 if(count <= 0){
				result.put("error", "Error al actualizar la regla de negocio.");
				return result;
			}
			 
			 if(sql.length() > 0)
				 sql.delete(0, sql.length());
		 
			 sql.append("\n INSERT INTO EMPRESA_REG_NEG ");
			 sql.append("\n SELECT * FROM EMPRESA_REG_NEG_SIM ");
		 
			 count = jdbcTemplate.update(sql.toString());
		 
			 /*if(count <= 0){
					result.put("error", "Error al actualizar empresas de regla de negocio.");
					return result;
				}*/
			 
			 if(sql.length() > 0)
				 sql.delete(0, sql.length());
		 
			 sql.append("\n INSERT INTO ACRE_DOCTO_REG_NEG ");
			 sql.append("\n SELECT * FROM ACRE_DOCTO_REG_NEG_SIM ");
		 
			 count = jdbcTemplate.update(sql.toString());
			 /*
			 if(count <= 0){
					result.put("error", "Error al actualizar acreedores/documentos.");
					return result;
				}
			 */
			 
			 if(sql.length() > 0)
				 sql.delete(0, sql.length());
		 
			 sql.append("\n INSERT INTO DOCTO_TESORERIA_REG_NEG ");
			 sql.append("\n SELECT * FROM DOCTO_TESORERIA_REG_NEG_SIM ");
		 
			 count = jdbcTemplate.update(sql.toString());
			 
			 /*if(count <= 0){
				result.put("error", "Error al actualizar documentos/tesoreria.");
				return result;
			}*/
			 
			 if(sql.length() > 0)
				 sql.delete(0, sql.length());
		 
			 sql.append("\n INSERT INTO CONDICION_PAGO_REG_NEG ");
			 sql.append("\n SELECT * FROM CONDICION_PAGO_REG_NEG_SIM ");
		 
			 count = jdbcTemplate.update(sql.toString());
			 
			 /*if(count <= 0){
				result.put("error", "Error al actualizar condicion pago.");
				return result;
			}*/
			 
			 if(sql.length() > 0)
				 sql.delete(0, sql.length());
		 
			 sql.append("\n INSERT INTO CONTAB_VENCIMIENTO_REG_NEG ");
			 sql.append("\n SELECT * FROM CONTAB_VENCIMIENTO_REG_NEG_SIM ");
		 
			 count = jdbcTemplate.update(sql.toString());
			 
			 /*if(count <= 0){
				result.put("error", "Error al actualizar contabilizaci�n/vencimiento.");
				return result;
			}*/
			 
			 if(sql.length() > 0)
				 sql.delete(0, sql.length());
		 
			 sql.append("\n INSERT INTO DIAS_EXCEP_REG_NEG ");
			 sql.append("\n SELECT * FROM DIAS_EXCEP_REG_NEG_SIM ");
		 
			 count = jdbcTemplate.update(sql.toString());
			 
			 /*if(count <= 0){
					result.put("error", "Error al actualizar dias excepci�n.");
					return result;
				}*/
			 
			 if(sql.length() > 0)
				 sql.delete(0, sql.length());
		 
			 sql.append("\n INSERT INTO PLAN_PAGOS_REG_NEG ");
			 sql.append("\n SELECT * FROM PLAN_PAGOS_REG_NEG_SIM ");
		 
			 count = jdbcTemplate.update(sql.toString());
			 
			 /*if(count <= 0){
					result.put("error", "Error al actualizar plan de pagos.");
					return result;
				}*/
			 
			 if(sql.length() > 0)
				 sql.delete(0, sql.length());
		 
			 sql.append("\n INSERT INTO RUBRO_REG_NEG ");
			 sql.append("\n SELECT * FROM RUBRO_REG_NEG_SIM ");
		 
			 count = jdbcTemplate.update(sql.toString());
			 
			 /*if(count <= 0){
				result.put("error", "Error al actualizar rubros regla negocio.");
				return result;
			}*/
			 
			/*******	TERMINA ACTUALIZACI�N DE REGLA DE NEGOCIOS	********/ 
			 
			/*******	COMIENZA ELIMINACI�N DE TEMPORALES DE REGLA NEGOCIOS	********/
			 
			if(idRegla != 0){
				
				if(sql.length() > 0)
					 sql.delete(0, sql.length());
				 
				 sql.append("\n DELETE FROM RUBRO_REG_NEG WHERE ID_REGLA = "+idRegla+" ");
				 
				 count = jdbcTemplate.update(sql.toString());
				
				 if(sql.length() > 0)
					 sql.delete(0, sql.length());
				 
				 sql.append("\n DELETE FROM PLAN_PAGOS_REG_NEG ");
				 sql.append("\n WHERE ID_CONDICION_PAGO IN(SELECT ID_CONDICION_PAGO ");
				 sql.append("\n 							FROM CONDICION_PAGO_REG_NEG ");
				 sql.append("\n 							WHERE ID_REGLA = "+idRegla+") ");
				 
				 count = jdbcTemplate.update(sql.toString());
				
				 if(sql.length() > 0)
					 sql.delete(0, sql.length());
				 
				 sql.append("\n DELETE FROM DIAS_EXCEP_REG_NEG ");
				 sql.append("\n WHERE ID_CONDICION_PAGO IN(SELECT ID_CONDICION_PAGO ");
				 sql.append("\n 							FROM CONDICION_PAGO_REG_NEG ");
				 sql.append("\n 							WHERE ID_REGLA = "+idRegla+") ");
				 
				 count = jdbcTemplate.update(sql.toString());
				 
				 if(sql.length() > 0)
					 sql.delete(0, sql.length());
				 
				 sql.append("\n DELETE FROM CONTAB_VENCIMIENTO_REG_NEG ");
				 sql.append("\n WHERE ID_CONDICION_PAGO IN(SELECT ID_CONDICION_PAGO ");
				 sql.append("\n 							FROM CONDICION_PAGO_REG_NEG ");
				 sql.append("\n 							WHERE ID_REGLA = "+idRegla+") ");
				 
				 count = jdbcTemplate.update(sql.toString());
				 
				 if(sql.length() > 0)
					 sql.delete(0, sql.length());
				 
				 sql.append("\n DELETE FROM CONDICION_PAGO_REG_NEG ");
				 sql.append("\n WHERE ID_REGLA = "+idRegla+" ");
				 
				 count = jdbcTemplate.update(sql.toString());
				 
				 if(sql.length() > 0)
					 sql.delete(0, sql.length());
				 
				 sql.append("\n DELETE FROM DOCTO_TESORERIA_REG_NEG ");
				 sql.append("\n WHERE ID_REGLA = "+idRegla+" ");
				 
				 count = jdbcTemplate.update(sql.toString());
				 
				 if(sql.length() > 0)
					 sql.delete(0, sql.length());
				 
				 sql.append("\n DELETE FROM ACRE_DOCTO_REG_NEG ");
				 sql.append("\n WHERE ID_REGLA = "+idRegla+" ");
				 
				 count = jdbcTemplate.update(sql.toString());
				 
				 if(sql.length() > 0)
					 sql.delete(0, sql.length());
				 
				 sql.append("\n DELETE FROM EMPRESA_REG_NEG ");
				 sql.append("\n WHERE ID_REGLA = "+idRegla+" ");
				 
				 count = jdbcTemplate.update(sql.toString());
				 
				 if(sql.length() > 0)
					 sql.delete(0, sql.length());
				 
				 sql.append("\n DELETE FROM REGLA_NEGOCIO WHERE ID_REGLA = "+idRegla+" ");
				 
				 count = jdbcTemplate.update(sql.toString());
				 
				 //Borra todos los datos de las tablas temporales
				 deshacerCambiosSimulador();
				 
			} //FIN IF(idRegla!=0)
				
			eliminaLogueoUsuario();
			
			result.put("msgUsuario", "Regla aplicada correctamente!");
			
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDaoImpl, M:actualizarMovtosSimulador");
			
			result.put("error", "Error de conexi�n al servidor.");
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDao, M:actualizarMovtosSimulador");
			
			result.put("error", "Error en el procceso de actualizaci�n.");
			
		}
		
		return result;
	}
	
	public boolean existeSeleccionAutomaticaGrupo(String cveControl) {

		StringBuffer sql = new StringBuffer();
		int count = 0;
		
		try{
				
			sql.append("\n SELECT COUNT(CVE_CONTROL) ");
			sql.append("\n FROM SELECCION_AUTOMATICA_GRUPO ");
			sql.append("\n WHERE CVE_CONTROL = '"+cveControl+"' ");
			
			count = jdbcTemplate.queryForInt(sql.toString());
			 
			if(count <= 0)
				return false;
			else 
				return true;
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDaoImpl, M:existeSeleccionAutomaticaGrupo");
		}
		
		return false;
	}
	
	@Override
	public boolean deshacerCambiosSimulador() {

		StringBuffer sql = new StringBuffer();
		int count = 0;
		
		try{
				
			sql.append("\n DELETE SELECCION_AUTOMATICA_GRUPO_SIM ");
			
			count = jdbcTemplate.update(sql.toString());
			
			if(sql.length()>0)
				sql.delete(0, sql.length());
			
			sql.append("\n DELETE TMP_MOVIMIENTO_SIM ");
			
			count = jdbcTemplate.update(sql.toString());
			
			if(sql.length()>0)
				sql.delete(0, sql.length());
			
			sql.append("\n DELETE FROM RUBRO_REG_NEG_SIM ");
			
			count = jdbcTemplate.update(sql.toString());
			
			if(sql.length()>0)
				sql.delete(0, sql.length());
			
			sql.append("\n DELETE FROM PLAN_PAGOS_REG_NEG_SIM ");
			
			count = jdbcTemplate.update(sql.toString());
			
			if(sql.length()>0)
				sql.delete(0, sql.length());
			
			sql.append("\n DELETE FROM DIAS_EXCEP_REG_NEG_SIM ");
			
			count = jdbcTemplate.update(sql.toString());
			
			if(sql.length()>0)
				sql.delete(0, sql.length());
			
			sql.append("\n DELETE FROM CONTAB_VENCIMIENTO_REG_NEG_SIM ");
			
			count = jdbcTemplate.update(sql.toString());
			
			if(sql.length()>0)
				sql.delete(0, sql.length());
			
			sql.append("\n DELETE FROM CONDICION_PAGO_REG_NEG_SIM ");
			
			count = jdbcTemplate.update(sql.toString());
			
			if(sql.length()>0)
				sql.delete(0, sql.length());
			
			sql.append("\n DELETE FROM DOCTO_TESORERIA_REG_NEG_SIM ");
			
			count = jdbcTemplate.update(sql.toString());
			
			if(sql.length()>0)
				sql.delete(0, sql.length());
			
			sql.append("\n DELETE FROM ACRE_DOCTO_REG_NEG_SIM ");
			
			count = jdbcTemplate.update(sql.toString());
			
			if(sql.length()>0)
				sql.delete(0, sql.length());
			
			sql.append("\n DELETE FROM EMPRESA_REG_NEG_SIM ");
			
			count = jdbcTemplate.update(sql.toString());
			
			if(sql.length()>0)
				sql.delete(0, sql.length());
			
			sql.append("\n DELETE FROM REGLA_NEGOCIO_SIM ");
			
			count = jdbcTemplate.update(sql.toString());
			
			//eliminaLogueoUsuario();
			
			/*if(count <= 0)
				return false;
			else 
				return true;
			*/
			return true;
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDaoImpl, M:deshacerCambiosSimulador");
		}
		
		return false;
	}
	
	@Override
	public int obtenerIdReglaSim() {

		StringBuffer sql = new StringBuffer();
		List<ReglasNegocioDto> lstRegla = new ArrayList<>();
		
		try{
				
			sql.append("\n select id_regla from regla_negocio_sim ");
			sql.append("\n ORDER by id_regla DESC ");
			
			 lstRegla = jdbcTemplate.query(sql.toString(), new RowMapper<ReglasNegocioDto>(){
					public ReglasNegocioDto mapRow(ResultSet rs, int idx) throws SQLException {
						ReglasNegocioDto regla = new ReglasNegocioDto();
						regla.setIdRegla(rs.getInt("id_regla"));
						return regla;
					}});
			 
			 if(lstRegla.size()>0){
				 return lstRegla.get(0).getIdRegla();
			 }
			 
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDaoImpl, M:deshacerCambiosSimulador");
		}
		
		return 0;
	}

	@Override
	public Map<String, Object> loginSimulador() {
		
		StringBuffer sql = new StringBuffer();
		Map<String, Object> map = new HashMap<>();
		List<Date> lst = new ArrayList<>();
		
		try{
				
			sql.append("\n SELECT FECHA_LOGIN FROM LOGIN_SIMULADOR ");
			
			 lst = jdbcTemplate.query(sql.toString(), new RowMapper<Date>(){
					public Date mapRow(ResultSet rs, int idx) throws SQLException {
						Date dateLogin = rs.getDate("FECHA_LOGIN");
						return dateLogin;
					}});
			 
			 //Si hay algun registro, significa que hay un usuario en linea, para esto hay dos opciones.
			 //1.- Si hay registro y tiempo < 5 min a la hora actual... Error.
			 //2.- Si hay registro y tiempo > 5 min... Realiza la regla de negocios.
			 //Si no hay registro hace el simulador sin problema.
			 if(lst.size()>0){
				 
				 /* sacar diferencia de minutos
				    Date date1 = new Date(116+1900,2,3,2,5,0);
					Date date2 = new Date(116+1900,2,3,2,7,0);
					System.out.println(((date2.getTime() - date1.getTime())/1000)/60);
				  */
				 
				if( (((new Date().getTime() - lst.get(0).getTime())/1000)/60) < 5){
					map.put("error", "Hay un usuario usuando el simulador. <br> Por favor intente m�s tarde.");
				}else{
					//Si hay un usuario pero el tiempo es mayor a 5 minutos, se elimina pues su sesi�n ya expir�.
					
					eliminaLogueoUsuario();
					
					/*if(sql.length()>0)
						sql.delete(0, sql.length());
					
					sql.append("\n DELETE FROM LOGIN_SIMULADOR ");
					
					count = jdbcTemplate.update(sql.toString());
					
					if(count <= 0){
						map.put("error", "Error al eliminar sesi�n de la simulaci�n.");
					}*/
					
					
				}
				
			 }
			 
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDaoImpl, M:loginSimulador");
		}
		
		return map;
	}
	
	@Override
	public Map<String, Object> insertarLoginSimulador() {
		
		StringBuffer sql = new StringBuffer();
		Map<String, Object> map = new HashMap<>();
		int count = 0;
		
		try{
				
			sql.append("\n INSERT INTO LOGIN_SIMULADOR ");
			sql.append("\n VALUES("+GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+", SYSDATE) ");
			 
			count = jdbcTemplate.update(sql.toString()); 
			
			if(count <= 0){
				map.put("error", "Error al insertar sesi�n de la simulaci�n.");
			}
			
			deshacerCambiosSimulador();
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDaoImpl, M:insertarLoginSimulador");
		}
		
		return map;
	}
	
	
	public Map<String, Object> updateLoginSimulador() {
		
		StringBuffer sql = new StringBuffer();
		Map<String, Object> map = new HashMap<>();
		int count = 0;
		
		try{
				
			sql.append("\n UPDATE LOGIN_SIMULADOR ");
			sql.append("\n SET FECHA_LOGIN = getdate() ");
			sql.append("\n WHERE ID_USUARIO = "+GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+" ");
			
			count = jdbcTemplate.update(sql.toString()); 
			
			if(count <= 0){
				map.put("error", "Error al insertar sesi�n de la simulaci�n.");
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDaoImpl, M:insertarLoginSimulador");
		}
		
		return map;
	}
	
	
	
	public Map<String, Object> validarUsuarioLogueado() {
		
		StringBuffer sql = new StringBuffer();
		Map<String, Object> map = new HashMap<>();
		map.put("error", "");
		List<String> lst = new ArrayList<>();
		int count = 0;
		
		try{
				
			sql.append("\n SELECT COUNT(FECHA_LOGIN) FROM LOGIN_SIMULADOR ");
			sql.append("\n WHERE ID_USUARIO = "+GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+" ");
			
			count = jdbcTemplate.queryForInt(sql.toString()); 
			
			if(count <= 0){
				
				if(sql.length() > 0)
					sql.delete(0, sql.length());
				
				sql.append("\n SELECT CLAVE_USUARIO ");
				sql.append("\n FROM LOGIN_SIMULADOR ls JOIN SEG_USUARIO su ON ls.ID_USUARIO = su.ID_USUARIO ");
				sql.append("\n ");
				
				 lst = jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
					 	public String mapRow(ResultSet rs, int idx) throws SQLException {
					 		String cveUsu = rs.getString("CLAVE_USUARIO");
							return cveUsu;
						}});
				
				map.put("error", "Su sesi�n para la simulaci�n de propuestas ha expirado. "
						+ "<br>El usuario '"+lst.get(0)+"' se encuentra logueado."
						+ "<br>Por favor vuelva a cargar la simulaci�n.");
			}
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDaoImpl, M:insertarLoginSimulador");
		}
		
		return map;
	}
	 
	@Override
	public Map<String, Object> eliminaLogueoUsuario() {
		
		StringBuffer sql = new StringBuffer();
		Map<String, Object> map = new HashMap<>();
		int count = 0;
		
		try{
				
			sql.append("\n DELETE FROM LOGIN_SIMULADOR ");
			
			count = jdbcTemplate.update(sql.toString());
			
			if(count <= 0){
				map.put("error", "Error al eliminar sesi�n de la simulaci�n.");
			}
				
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDaoImpl, M:insertarLoginSimulador");
		}
		
		return map;
	}

	@Override
	public int buscaMovimientosTmp(String cveControl) {
			
		StringBuffer sql = new StringBuffer();
		int count = 0;
		
		try{
				
			sql.append("\n select count(comentario2) ");
			sql.append("\n from tmp_movimiento_sim ");
			sql.append("\n where cve_control = '"+cveControl+"' ");
			sql.append("\n AND comentario2 is not null ");
			
			count = jdbcTemplate.queryForInt(sql.toString());
			
			/*if(count <= 0){
				map.put("error", "Error al eliminar sesi�n de la simulaci�n.");
			}*/
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:ReglasNegocioDaoImpl, M:buscaMovimientosTmp");
		}
		
		return count;
	}

}
 