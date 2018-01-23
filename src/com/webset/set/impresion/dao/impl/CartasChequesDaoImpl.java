package com.webset.set.impresion.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.poi.ss.formula.functions.Today;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.impresion.dao.CartasChequesDao;
import com.webset.set.impresion.dto.CartasChequesDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;

public class CartasChequesDaoImpl implements CartasChequesDao{
	
	Funciones funciones = new Funciones();
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	String sql = "";
	
	public void setDataSource(DataSource dataSource){
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	

	
	public List<CartasChequesDto> llenaEmpresa(String idClave){
		List<CartasChequesDto> listaResultado = new ArrayList<CartasChequesDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("select * from movimiento mv join empresa e on mv.no_empresa=e.no_empresa \n");
			sql.append("where mv.cve_control='"+idClave+"'");
			
			System.out.println(sql);
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<CartasChequesDto>(){
				public CartasChequesDto mapRow(ResultSet rs, int idx)throws SQLException{
					CartasChequesDto campos = new CartasChequesDto();
					campos.setIdEmpresa(rs.getString("no_empresa"));
					campos.setDescEmpresa(rs.getString("nom_empresa"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesDaoImpl, M: llenaEmpresa");
		}return listaResultado;
	}


	@Override
	public List<CartasChequesDto> llenaProveedor() {
		List<CartasChequesDto> listaResultado = new ArrayList<CartasChequesDto>();
		StringBuffer sql= new StringBuffer();
		try{
			sql.append("select * from persona where id_tipo_persona='P'");
	
			System.out.println(sql);
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<CartasChequesDto>(){
				public CartasChequesDto mapRow(ResultSet rs, int idx)throws SQLException{
					CartasChequesDto campos = new CartasChequesDto();
					campos.setIdProveedor(rs.getString("no_persona"));
					campos.setDescProveedor(rs.getString("nombre_corto"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesDaoImpl, M: llenaProveedor");
		}return listaResultado;
	}


	@Override
	public List<CartasChequesDto> llenaSolicitante() {
		List<CartasChequesDto> listaResultado = new ArrayList<CartasChequesDto>();
		StringBuffer sql= new StringBuffer();
		
		try{
			
			
			sql.append("SELECT IDENTIFICACIONC1_SOL, NOMBREC1_SOL FROM CAT_SOLICITANTES \n");
			
			System.out.println(sql);
			
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<CartasChequesDto>(){
				public CartasChequesDto mapRow(ResultSet rs, int idx)throws SQLException{
					CartasChequesDto campos = new CartasChequesDto();
					campos.setIdSolicitante(rs.getString("IDENTIFICACIONC1_SOL"));
					campos.setDescSolicitante(rs.getString("NOMBREC1_SOL"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesDaoImpl, M: llenaSolicitante");
		}return listaResultado;
	}


	@Override
	public List<CartasChequesDto> llenaAutorizaciones() {
		List<CartasChequesDto> listaResultado = new ArrayList<CartasChequesDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("select no_persona, nombre from cat_persona");
			
			System.out.println(sql);
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<CartasChequesDto>(){
				public CartasChequesDto mapRow(ResultSet rs, int idx)throws SQLException{
					CartasChequesDto campos = new CartasChequesDto();
					campos.setIdSolicitante(rs.getString("no_persona"));
					campos.setDescSolicitante(rs.getString("nombre"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesDaoImpl, M: llenaAutorizaciones");
		}return listaResultado;
	}


	@Override
	public List<CartasChequesDto> llenaAutorizaciones2() {
		List<CartasChequesDto> listaResultado = new ArrayList<CartasChequesDto>();
		StringBuffer sql = new StringBuffer();
		try{
			
			sql.append("select no_persona, nombre from cat_persona");
			
			
			System.out.println(sql);
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<CartasChequesDto>(){
				public CartasChequesDto mapRow(ResultSet rs, int idx)throws SQLException{
					CartasChequesDto campos = new CartasChequesDto();
					campos.setIdSolicitante(rs.getString("no_persona"));
					campos.setDescSolicitante(rs.getString("nombre"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesDaoImpl, M: llenaAutorizaciones2");
		}return listaResultado;
	}


	@Override
	public List<CartasChequesDto> llenaClave(String fechaIni, String fechaFin) {
		List<CartasChequesDto> listaResultado = new ArrayList<CartasChequesDto>();
		StringBuffer sql= new StringBuffer();
		
		try{
			sql.append("select * from seleccion_automatica_grupo where fecha_propuesta >= convert(datetime,'"+fechaIni+"' , 103) and fecha_propuesta < convert(datetime,'"+fechaFin+"', 103) +1 ");
		
			System.out.println(sql);
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<CartasChequesDto>(){
				public CartasChequesDto mapRow(ResultSet rs, int idx)throws SQLException{
					CartasChequesDto campos = new CartasChequesDto();
					campos.setIdClave(rs.getString("cve_control"));
					campos.setDescClave(rs.getString("concepto"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesDaoImpl, M: llenaClave");
		}return listaResultado;
	}


	@Override
	public List<CartasChequesDto> llenaGrid(String idEmpresa, String idClave, 
			 String tipo, String idBanco, String tipoC, String op,  String idChequera) {
		List<CartasChequesDto> listaResultado = new ArrayList<CartasChequesDto>();
		StringBuffer sql = new StringBuffer();
		System.out.println("Llena Grid daoImpl: "+op);
		try{	
			if(op.equals("CC")){
				sql.append("select e.no_empresa,e.nom_empresa,p.equivale_persona,p.razon_social,case when m.fec_cheque is null then m.fec_propuesta else m.fec_cheque end as fec_cheque, m.no_docto,m.id_divisa,m.concepto,m.cve_control,m.importe,m.no_cheque,m.no_factura,m.beneficiario,m.id_chequera,m.no_folio_det from movimiento m join empresa e on m.no_empresa=e.no_empresa \n");
				sql.append("join persona p on p.no_persona=m.no_cliente where id_forma_pago=1 \n");
				sql.append("and m.no_empresa="+Utilerias.validarCadenaSQL(idEmpresa)+" \n");
				sql.append("and m.id_banco="+Utilerias.validarCadenaSQL(idBanco)+"\n");
				sql.append("and cve_control='"+Utilerias.validarCadenaSQL(idClave)+"' \n");
				sql.append("and m.id_chequera='"+Utilerias.validarCadenaSQL(idChequera)+"' \n");
				sql.append("AND ID_TIPO_OPERACION NOT IN(3001,3201) \n");
				sql.append("AND ID_ESTATUS_MOV NOT IN('H','A')\n");
				sql.append("AND PO_HEADERS IS NOT NULL \n");
				sql.append("AND COALESCE(b_certificado,' ') <> 'S' ");
									
			}else{
			sql.append("select e.no_empresa,e.nom_empresa,p.equivale_persona,p.razon_social,case when m.fec_cheque is null then m.fec_propuesta else m.fec_cheque end as fec_cheque, m.no_docto,m.id_divisa,m.concepto,m.cve_control,m.importe,m.no_cheque,m.no_factura,m.beneficiario,m.id_chequera,m.no_folio_det from movimiento m join empresa e on m.no_empresa=e.no_empresa \n");
			sql.append("join persona p on p.no_persona=m.no_cliente \n");
			if(op.equals("CA") || op.equals("ECA")){
				sql.append("where id_forma_pago=10 \n");
			}else{
				sql.append("where id_forma_pago=1 \n");
			}
			sql.append("and m.no_empresa="+Utilerias.validarCadenaSQL(idEmpresa)+" \n");
			sql.append("and m.id_banco="+Utilerias.validarCadenaSQL(idBanco)+"\n");
			sql.append("and cve_control='"+Utilerias.validarCadenaSQL(idClave)+"' \n");
			sql.append("and m.id_chequera='"+Utilerias.validarCadenaSQL(idChequera)+"' \n");
			sql.append("AND ID_TIPO_OPERACION NOT IN(3001,3201) \n");
			sql.append("AND ID_ESTATUS_MOV NOT IN('H','A')\n");
			sql.append("AND PO_HEADERS IS NOT NULL \n");
			if(op.equals("CE") || op.equals("ECE")){
				//sql.append("\n AND ID_TIPO_OPERACION NOT IN(3001,3201)");
				//sql.append("\n AND ID_ESTATUS_MOV NOT IN('H','A')");
				sql.append("\n AND B_CERTIFICADO='S'");
				sql.append("\n AND CVE_CONTROL IS NOT NULL");
				//sql.append("\n and m.no_folio_det ");
			}
				System.out.println(tipoC);	
				if (op.equals("CE") || op.equals("CA")) {
					System.out.println("verdadero");
					sql.append("\n and m.no_folio_det not in \n");
					
				} else {
					sql.append("\n and m.no_folio_det in \n");
				}
				
				sql.append("(select dc.no_folio_det from emision_cartas ec join det_emision_cartas  dc \n");
				sql.append("on ec.id_emision= dc.id_emision where ec.tipo='"+Utilerias.validarCadenaSQL(tipoC)+"')");
			}
			
			System.out.println(sql);
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<CartasChequesDto>(){
				public CartasChequesDto mapRow(ResultSet rs, int idx)throws SQLException{
					CartasChequesDto campos = new CartasChequesDto();
					campos.setIdEmpresa(rs.getString("no_empresa"));
					campos.setDescEmpresa(rs.getString("nom_empresa"));
					campos.setIdProveedor(rs.getString("equivale_persona"));
					campos.setDescProveedor(rs.getString("razon_social"));
					campos.setFecha(rs.getString("fec_cheque"));
					campos.setDocumento(rs.getString("no_docto"));
					campos.setDivisa(rs.getString("id_divisa"));
					campos.setConcepto(rs.getString("concepto"));
					campos.setClaveP(rs.getString("cve_control"));
					campos.setImporte(rs.getString("importe"));
					campos.setNoCheque(rs.getString("no_cheque"));
					campos.setTipo(rs.getString("no_factura"));
					campos.setBeneficiario(rs.getString("beneficiario"));
					campos.setCuenta(rs.getString("id_chequera"));
					campos.setFolio(rs.getString("no_folio_det"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesDaoImpl, M: llenaGrid");
		}return listaResultado;
	}


	@Override
	public List<LlenaComboGralDto> llenarComboBeneficiario(LlenaComboGralDto dto) {
		String cond="";
		dto.setTabla("persona p");
		cond=dto.getCondicion();
		dto.setCampoDos("rTRIM(COALESCE(p.razon_social,'')) +' '+ rTRIM(COALESCE(p.NOMBRE,'')) + ' ' + rTRIM(COALESCE(p.PATERNO,'')) " +
				"+ ' ' + rTRIM(COALESCE(p.MATERNO,''))");
		
		if(dto.isRegistroUnico()){
			dto.setCondicion("p.equivale_persona="+cond);
		}else{
			dto.setCondicion("p.id_tipo_persona='P'	"
					+"	AND p.no_empresa in(552,217)"
					+"	AND ((p.razon_social like '"+cond+"%'"     
					+"	or p.paterno like '"+cond+"%'" 
					+"	or p.materno like '"+cond+"%'"   
					+"	or p.nombre like '"+cond+"%' )" 
					+"	or (p.equivale_persona like '"+cond+"%'))");	
		}
		
		return llenarComboProveedor(dto);
	}
	
	public List<LlenaComboGralDto>llenarComboProveedor(LlenaComboGralDto dto){
		String sql="";
		List<LlenaComboGralDto> listDatos= new ArrayList<LlenaComboGralDto>();
		try{
			sql+= "	SELECT	";
			
			if(dto.getCampoUno()!=null && !dto.getCampoUno().trim().equals(""))
				sql+=""+dto.getCampoUno()+"	as ID";
			if(dto.getCampoDos()!=null && !dto.getCampoDos().trim().equals(""));
				sql+=",	"+dto.getCampoDos()+"	as DESCRIPCION";
				
		    sql+= " ,no_persona FROM	";
		    if(dto.getTabla()!=null && !dto.getTabla().trim().equals(""))
		    	sql+=""+dto.getTabla();
		    if(dto.getCondicion()!=null && !dto.getCondicion().trim().equals(""))
		    	sql+="	WHERE	"+dto.getCondicion();
		    if(dto.getOrden()!=null && !dto.getOrden().trim().equals(""))
		    	sql+="	ORDER BY	"+dto.getOrden();
		 
		    System.out.print("llena combo beneficiario: " + sql);
		    
		    listDatos= jdbcTemplate.query(sql, new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					cons.setCampoDos(rs.getString("no_persona"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Utilerias, C:ConsultasGenerales, M:llenarComboGral");
		}
		return listDatos;
	}


	@Override
	public List<CartasChequesDto> obtieneDatos(String idEmpresa, String banco, String tipo) {
			
		List<CartasChequesDto> listaResultado = new ArrayList<CartasChequesDto>();
		StringBuffer sql= new StringBuffer();
		try{
			sql.append("select pc.textob1,pc.textob2,pc.textob3,pc.textob4,pc.textoc1,case when pc.textoc2 is null then ' ' else pc.textoc2 end as textoc2,pc.textoc3,pc.textoc4,pc.textoc5,pc.textoc6,d.calle_no,d.colonia,d.ciudad,d.id_cp,d.id_estado,pc.lugar_fecha from persona p join direccion d \n");
			sql.append("on p.no_empresa = d.no_empresa, parametros_carta pc where \n");
			sql.append("p.no_empresa = "+Utilerias.validarCadenaSQL(idEmpresa));		
			sql.append("and tipo_carta = '" + Utilerias.validarCadenaSQL(tipo)+ "' \n");		
			sql.append("and id_banco = "+Utilerias.validarCadenaSQL(banco));		
					
			
			
			System.out.println(sql);
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<CartasChequesDto>(){
				public CartasChequesDto mapRow(ResultSet rs, int idx)throws SQLException{
					CartasChequesDto campos = new CartasChequesDto();
				
					campos.setB1(rs.getString("textob1"));
					campos.setB2(rs.getString("textob2"));
					campos.setB3(rs.getString("textob3"));
					campos.setB4(rs.getString("textob4"));
					campos.setC1(rs.getString("textoc1"));
					campos.setC2(rs.getString("textoc2"));
					campos.setC3(rs.getString("textoc3"));
					campos.setC4(rs.getString("textoc4"));
					campos.setC5(rs.getString("textoc5"));
					campos.setC6(rs.getString("textoc6"));
					campos.setCalle(rs.getString("calle_no"));
					campos.setColonia(rs.getString("colonia"));
					campos.setCiudad(rs.getString("ciudad"));
					campos.setCp(rs.getString("id_cp"));
					campos.setEstado(rs.getString("id_estado"));
					campos.setLugarFecha(rs.getString("lugar_fecha"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesDaoImpl, M: obtieneDatos");
		}return listaResultado;
	}


	@Override
	public List<CartasChequesDto> llenaBanco(String idEmpresa) {
		
		List<CartasChequesDto> listaResultado = new ArrayList<CartasChequesDto>();
		StringBuffer sql= new StringBuffer();
		
		try{
			sql.append("SELECT distinct b.ID_BANCO, DESC_BANCO FROM CAT_BANCO b ");
			sql.append("JOIN CAT_CTA_BANCO cb ON b.id_banco= cb.id_banco WHERE NO_EMPRESA="+Utilerias.validarCadenaSQL(idEmpresa)+"");
				
			System.out.println(sql);	
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<CartasChequesDto>(){
				public CartasChequesDto mapRow(ResultSet rs, int idx)throws SQLException{
					CartasChequesDto campos = new CartasChequesDto();
					campos.setIdBanco(rs.getString("id_banco"));
					campos.setDescBanco(rs.getString("desc_banco"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesDaoImpl, M: llenaBanco");
		}return listaResultado;
	}


	@Override
	public int guardarCartaEmitida(Map<String, String> valor, String dif, String fechaImp) {
		int resultado=-1;
		try {
			GlobalSingleton globalSingleton = GlobalSingleton.getInstancia();
			System.out.println(dif);
			System.out.println(globalSingleton.getUsuarioLoginDto().getIdUsuario());
			if (dif.equals("can")) {
				
				StringBuffer sql = new StringBuffer();
				sql.append("insert into emision_cartas values('");
				sql.append(Utilerias.validarCadenaSQL(valor.get("banco")));
				sql.append("','");
				sql.append(Utilerias.validarCadenaSQL(valor.get("identificacion")));
				sql.append("','");
				System.out.println("---------------------------------");
				sql.append(Utilerias.validarCadenaSQL(valor.get("autorizacion11").toString().equals("")?"-1":valor.get("autorizacion11")));
				sql.append("','");
				sql.append(Utilerias.validarCadenaSQL(valor.get("autorizacion22").toString().equals("")?"-1":valor.get("autorizacion22")));
				sql.append("',convert(char(10),'"+Utilerias.validarCadenaSQL(fechaImp)+"', 103),'");
				sql.append(Utilerias.validarCadenaSQL(globalSingleton.getUsuarioLoginDto().getIdUsuario()));
				sql.append("','");
				sql.append(Utilerias.validarCadenaSQL(valor.get("motivo")));
				sql.append("',convert(char(10),'"+Utilerias.validarCadenaSQL(fechaImp)+"', 103),'");
				sql.append(Utilerias.validarCadenaSQL(globalSingleton.getUsuarioLoginDto().getIdUsuario()));
				sql.append("','I','");
				sql.append(Utilerias.validarCadenaSQL(valor.get("idEmision")));
				sql.append("','");
				sql.append(Utilerias.validarCadenaSQL(valor.get("tipo")));
				sql.append("','','')");
				System.out.println(sql.toString());
				resultado= jdbcTemplate.update(sql.toString());
			} else {
//				GlobalSingleton globalSingleton = GlobalSingleton.getInstancia();
				StringBuffer sql = new StringBuffer();
				sql.append("insert into emision_cartas values('");
				sql.append(Utilerias.validarCadenaSQL(valor.get("banco")));
				sql.append("','");
				sql.append(Utilerias.validarCadenaSQL(valor.get("identificacion")));
				sql.append("','");
				System.out.println("---------------------------------");
				sql.append(Utilerias.validarCadenaSQL(valor.get("autorizacion11").toString().equals("")?"-1":valor.get("autorizacion11")));
				sql.append("','");
				sql.append(Utilerias.validarCadenaSQL(valor.get("autorizacion22").toString().equals("")?"-1":valor.get("autorizacion22")));
				sql.append("',convert(datetime,'"+Utilerias.validarCadenaSQL(fechaImp)+"', 103),'");
				sql.append(Utilerias.validarCadenaSQL(globalSingleton.getUsuarioLoginDto().getIdUsuario()));
				sql.append("',null,null,null,'I','");
				sql.append(Utilerias.validarCadenaSQL(valor.get("idEmision")));
				sql.append("','");
				sql.append(Utilerias.validarCadenaSQL(valor.get("tipo")));
				sql.append("','','')");
				System.out.println(sql.toString());
				resultado= jdbcTemplate.update(sql.toString());
			}
			
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesDaoImpl, M: guardarCartaEmitida");
		}return resultado;
	}


	@Override
	public String obtenerIdEmision() {
		String resultado= "1";
		try {
			StringBuffer sql= new StringBuffer();
			sql.append("SELECT CASE WHEN (SELECT MAX(id_emision) FROM emision_cartas) IS NOT NULL ");
			sql.append("\n THEN (SELECT MAX(id_emision)+ 1 FROM emision_cartas)");
			sql.append("\n ELSE 1 END AS id FROM DUAL");
			
			List<String> listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<String>(){
				public String mapRow(ResultSet rs, int idx)throws SQLException{
					return rs.getString("id");
				}
			});
			resultado =listaResultado.get(0);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesDaoImpl, M: obtenerIdEmision");
		}return resultado;
	}

	@Override
	public int guardarDetalle(Map<String, String> beneficiarios, List<Map<String, String>> valor) {
		int resultado=-1;
		try {
			
				StringBuffer sql= new StringBuffer();
				sql.append("insert into det_emision_cartas values('");
				sql.append(Utilerias.validarCadenaSQL(valor.get(0).get("idEmision")));
				sql.append("','");
				sql.append(Utilerias.validarCadenaSQL(beneficiarios.get("folio")));
				sql.append("')");
				System.out.println(sql.toString());
				resultado= jdbcTemplate.update(sql.toString());
	
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesDaoImpl, M: guardarDetalle");
		}return resultado;
	}


	@Override
	public List<CartasChequesDto> llenaChequera(String idEmpresa, String idBanco) {
		List<CartasChequesDto> listaResultado = new ArrayList<CartasChequesDto>();
		sql = "";
		try{
			sql = "select no_empresa ,id_chequera, desc_chequera from cat_cta_banco"
					+ "\n where no_empresa="+Utilerias.validarCadenaSQL(idEmpresa)+" and id_banco="+Utilerias.validarCadenaSQL(idBanco)+"";
					
			
			System.out.println(sql);
			
			listaResultado = jdbcTemplate.query(sql, new RowMapper<CartasChequesDto>(){
				public CartasChequesDto mapRow(ResultSet rs, int idx)throws SQLException{
					CartasChequesDto campos = new CartasChequesDto();
					campos.setIdChequera(rs.getString("id_chequera"));
					campos.setDescChequera(rs.getString("desc_chequera"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesDaoImpl, M: llenaChequera");
		}return listaResultado;
	}


	@Override
	public int eliminaEmision(String folio) {
		int r=-1;
		try {
			StringBuffer sql = new StringBuffer();
			
			sql.append("delete from det_emision_cartas where no_folio_det="+Utilerias.validarCadenaSQL(folio));
			sql.append(" and id_emision IN(select ec.id_emision from emision_cartas ec join det_emision_cartas dc ON ec.id_emision=dc.id_emision where ec.tipo IN('ACERT','ACHEQ'))");
			
			System.out.println(sql);
			
			r= jdbcTemplate.update(sql.toString());
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesDaoImpl, M: eliminaEmision");
		}return r;
	}



	@Override
	public int certificarCheque(String cadenaFolios) {
		System.out.println("Llegaron los folios a DaoImpl "+cadenaFolios);
		int res = 0;
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE MOVIMIENTO SET B_CERTIFICADO='S', FEC_CERTIFICACION=getdate() \n");
		sb.append("WHERE NO_FOLIO_DET IN("+cadenaFolios+") \n");
		sb.append("AND ID_TIPO_OPERACION NOT IN(3001,3201) \n");
		System.out.println(sb.toString());
		try {
			res = jdbcTemplate.update(sb.toString());
		}catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesDaoImpl, M: certificarCheque");
		
		}
		return res;
	}



	@Override
	public List<CartasChequesDto> llenaBancoSP() {
		List<CartasChequesDto> listaResultado = new ArrayList<CartasChequesDto>();
		StringBuffer sql= new StringBuffer();
		
		try{
			sql.append("SELECT distinct b.ID_BANCO, DESC_BANCO FROM CAT_BANCO b ");
			sql.append("JOIN CAT_CTA_BANCO cb ON b.id_banco= cb.id_banco");
			System.out.println(sql);
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<CartasChequesDto>(){
				public CartasChequesDto mapRow(ResultSet rs, int idx)throws SQLException{
					CartasChequesDto campos = new CartasChequesDto();
					campos.setIdBanco(rs.getString("id_banco"));
					campos.setDescBanco(rs.getString("desc_banco"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesDaoImpl, M: llenaBancoSP");
		}return listaResultado;
	}



}
