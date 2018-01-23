package com.webset.set.global.dao;

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

import com.webset.set.personas.dao.ConsultaPersonasDao;
import com.webset.set.seguridad.dto.EmpresaDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.dto.ConfiguraSetDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.UsuarioLoginDto;

/**
 * Esta clase ejecuta las sentencias SQL para obtener
 * valores globales que se utilizar�n durante
 * los procesos de la aplicaci�n 
 * @author Cristian Garcia Garcia
 * @since 02/Mayo/2011
 */
public class GlobalDao {
	
	private static Logger logger = Logger.getLogger(GlobalDao.class);
	private JdbcTemplate jdbcTemplate;
	private Bitacora bitacora = new Bitacora();
	private ConsultasGenerales consultasGenerales;
	
	public List<Map<String, Object>> obtenerPropiedadesUsuarios(){
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> mapPropUsuario = new ArrayList<Map<String,Object>>();
		try{
				sql.append("SELECT su.id_usuario, su.apellido_paterno, su.apellido_materno, su.nombre, ");
				sql.append("	su.no_empresa, e.nom_empresa, ");
				sql.append("	su.id_caja, cc.desc_caja, ");
				sql.append("	e.no_cuenta_emp ");
				sql.append("	FROM seg_usuario su, empresa e, cat_caja cc ");
				//sql.append("WHERE no_usuario = " + idUsuario);
				sql.append("	WHERE	su.no_empresa = e.no_empresa ");
				sql.append("	and su.id_caja = cc.id_caja");
				mapPropUsuario = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String,Object>>(){
					public Map<String,Object> mapRow(ResultSet rs, int idx) throws SQLException {
						Map<String,Object> cons = new HashMap<String,Object>();	
							cons.put("idUsuario",rs.getString("id_usuario"));
							cons.put("paterno",rs.getString("apellido_paterno"));
							cons.put("materno",rs.getString("apellido_materno"));
							cons.put("nombre",rs.getString("nombre"));
							cons.put("noEmpresa",rs.getInt("no_empresa"));
							cons.put("nomEmpresa",rs.getString("nom_empresa"));
							cons.put("idCaja",rs.getInt("id_caja"));
							cons.put("descCaja",rs.getString("desc_caja"));
							cons.put("noCuentaEmp",rs.getInt("no_cuenta_emp"));
						return cons;
					}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalDao, M:obtenerPropiedadesUsuario");
		}
		return !mapPropUsuario.isEmpty()?mapPropUsuario:null;
	}
	/**
	 * llena combo
	 * @param dto
	 * @return
	 */

	public List<LlenaComboGralDto>consultarDatosComboGral(LlenaComboGralDto dto){
		String sql="";
		List<LlenaComboGralDto> listDatos= new ArrayList<LlenaComboGralDto>();
		try{
			sql+= "	SELECT	";
			if(dto.getCampoUno()!=null && !dto.getCampoUno().trim().equals(""))
				sql+=""+dto.getCampoUno()+"	as ID,	";
			//aplica solo para combos que tengan el mismo campo como id y descripcion (String)
			//ejemplo: chequeras
			else
				sql+=""+2+"	as ID"+",	";	
			if(dto.getCampoDos()!=null && !dto.getCampoDos().trim().equals(""));
				sql+= dto.getCampoDos()+"	as DESCRIPCION";
		    sql+= "  FROM	";
		    if(dto.getTabla()!=null && !dto.getTabla().trim().equals(""))
		    	sql+=""+dto.getTabla();
		    if(dto.getCondicion()!=null && !dto.getCondicion().trim().equals(""))
		    	sql+="	WHERE	"+dto.getCondicion();
		    if(dto.getOrden()!=null && !dto.getOrden().trim().equals(""))
		    	sql+="	ORDER BY	"+dto.getOrden();
		    
		    System.out.println("que combo general" + sql);
		    
		    listDatos= jdbcTemplate.query(sql, new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Global, C:GlobalDao, M:consultarDatosComboGral");
		}
		return listDatos;
	}
	
	public Date obtenerFechaHoy(){
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.obtenerFechaHoy();
	}
	
	/*Public Function FunSQLComboEmpresasConcen2(Optional ByVal plNoUsuario As Long = -1, _
                        Optional ByVal pbMantenimiento As Boolean = False) As ADODB.Recordset*/
	
	public List<LlenaComboGralDto> consultarEmpresasConcentradoras(int idUsuario, boolean bMantenimiento){
		StringBuffer sql = new StringBuffer();
		List<LlenaComboGralDto> listDatos = new ArrayList<LlenaComboGralDto>();
		try{
			sql.append("SELECT no_empresa as ID, nom_empresa  as DESCRIPCION ");
		    sql.append("\n	FROM empresa ");
		    sql.append("\n	WHERE b_concentradora = 'S' ");
			
		    if(idUsuario > 0)
		    {
		    	sql.append("\n	AND no_empresa in");
		        
		        if(!bMantenimiento)
		        {
		        	sql.append("\n	(SELECT DISTINCT no_controladora ");
		            sql.append("\n	FROM empresa ");
		            sql.append("\n	WHERE no_empresa in ");
		        }
		            
		        sql.append("\n	( SELECT no_empresa ");
		        sql.append("\n	FROM usuario_empresa ");
		        sql.append("\n	WHERE no_usuario = " + idUsuario + " ) ");
		        
		        if(!bMantenimiento)
		            sql.append("\n	)");
		    }
		   
		    listDatos= jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}});
		    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalDao, M:consultarEmpresasConcentradoras");
		}
		return listDatos;
	}
	
	//PAEE
	
	
	public String consultarDatosGraficaBanco(int empresa){
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> listDatos = new ArrayList<Map<String, Object>>();
		String data = "";;
		try{
			sql.append("SELECT '[''' + B.DESC_BANCO +''',' + ");
		    sql.append("\n	convert (varchar,SUM(CCB.SALDO_FINAL) )+']' ");
		    sql.append("\n	FROM CAT_BANCO B, CAT_CTA_BANCO CCB ");
		    sql.append("\n  WHERE CCB.ID_BANCO = B.ID_BANCO ");
		    if(empresa > 0)
		    {
		    	sql.append("\n	AND CCB.NO_EMPRESA = " + empresa);	        
		       
		    }
		    sql.append("\n  GROUP  BY B.DESC_BANCO");
		    bitacora.insertarRegistro("Ernesto: " + sql.toString());
		    listDatos= jdbcTemplate.queryForList(sql.toString());
		    for(int i=0;i< listDatos.size();i++){
		    	data = data +listDatos.toArray()[i]+',';
		    }
		    bitacora.insertarRegistro("Ernesto: " + listDatos.toArray()); 
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalDao, M:consultarDatosGraficaBanco");
		}
		return data.substring(0, data.length()-1);
	}
	
	
	//PAEE
	
	
	
	public List<LlenaComboDivisaDto> consultarDivisas(String condicion)
	{
		StringBuffer sql = new StringBuffer();
		List<LlenaComboDivisaDto> listDivisas= new ArrayList<LlenaComboDivisaDto>();
		try
		{
			sql.append(" SELECT id_divisa as ID, desc_divisa as DESCRIB ");
			sql.append("\n	FROM cat_divisa ");
			sql.append("\n	WHERE clasificacion = 'D'");
			if(condicion != null && !condicion.equals(""))
				sql.append("\n	and " + condicion);
    		listDivisas= jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboDivisaDto>(){
				public LlenaComboDivisaDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboDivisaDto cons = new LlenaComboDivisaDto();
					cons.setIdDivisa(rs.getString("ID"));
					cons.setDescDivisa(rs.getString("DESCRIB"));
					return cons;
				}});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Utilerias, C:ConsultasGenerales, M:llenarComboDivisa");
		}
		return listDivisas;
	    
	}
	
	/**
	 * Este metodo hace la consulta de los valores
	 * de la tabla configura_set
	 * @return retorna una lista de tipo Dto
	 */
	
	public List<ConfiguraSetDto> consultarDatosConfiguraSet(){
		StringBuffer sql = new StringBuffer();
		List<ConfiguraSetDto> listDatos = new ArrayList<ConfiguraSetDto>();
		try{
			sql.append("Select * from configura_set");
			listDatos = jdbcTemplate.query(sql.toString(), new RowMapper<ConfiguraSetDto>(){
				public ConfiguraSetDto mapRow(ResultSet rs, int idx)throws SQLException{
					ConfiguraSetDto rsDto = new ConfiguraSetDto();
						rsDto.setIndice(rs.getInt("indice"));
						rsDto.setValor(rs.getString("valor"));
						rsDto.setDescripcion(rs.getString("descripcion"));
				return rsDto;
			}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Utilerias, C:ConsultasGenerales, M:llenarDatosConfiguraSet");
		}
		return listDatos;
	}
	
	/**
	 * La consulta sirve para validar que el usuario exista 
	 * y su contrase�a sea correcta
	 * @param idUsr identificador del usuario
	 * @param psw posible contrase�a valida
	 * @return true si existe y es valido de lo contrario false
	 */
	public boolean validarUsuarioAutenticado(int idUsr,String psw){
		String sql="";
		int res=0;
		boolean valido=false;
		try{
			sql+="SELECT count(*) FROM seg_usuario";
			sql+="\n	where id_usuario="+idUsr;
			sql+="\n	and contrasena='"+psw+"'";
			res=jdbcTemplate.queryForInt(sql);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
			+ "P:Global, C:GlobalDao, M:validarUsuarioAutenticado");
			e.printStackTrace();
		}
		if(res>0)
			valido=true;
		else
			valido=false;
		
		return valido;
	}
	
	/**
	 * FunSQLSelectFiliales2
	 * @param idUsuario
	 * @param idBanco
	 * @return
	 */
	public List<EmpresaDto> consultarEmpresasCoinversoras(int idUsuario, int concentradora, String idDivisa){
		StringBuffer sbSQL = new StringBuffer();
		List<EmpresaDto> listDatos = new ArrayList<EmpresaDto>();
		try{
			sbSQL.append("\n SELECT distinct e.no_empresa, e.nom_empresa, e.no_cuenta_emp, ");
		    sbSQL.append("\n     p.id_tipo_persona, c.no_cuenta, e.no_controladora ");
		    sbSQL.append("\n FROM empresa e ");
		    sbSQL.append("\n     LEFT JOIN cuenta c on(e.no_empresa = c.no_cuenta), persona p ");
		    sbSQL.append("\n WHERE e.no_empresa = p.no_empresa ");
		    sbSQL.append("\n     and p.id_tipo_persona in ('I','E') ");
		    sbSQL.append("\n     and (e.no_controladora is null or e.no_controladora = " + concentradora + ") ");
		    sbSQL.append("\n     and e.no_empresa <> " + concentradora);
		    sbSQL.append("\n     and e.no_empresa in ");
		    sbSQL.append("\n         ( SELECT no_empresa ");
		    sbSQL.append("\n           FROM usuario_empresa ");
		    sbSQL.append("\n           WHERE no_usuario = " + idUsuario + " ) ");
		    
		    System.out.println("query para llenar empresa de traspaso de: " + sbSQL.toString());
		    
		    listDatos= jdbcTemplate.query(sbSQL.toString(), new RowMapper<EmpresaDto>(){
				public EmpresaDto mapRow(ResultSet rs, int idx) throws SQLException {
					EmpresaDto cons = new EmpresaDto();
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setNomEmpresa(rs.getString("nom_empresa"));
					cons.setNoCuentaEmp(rs.getInt("no_cuenta_emp"));
					cons.setIdTipoPersona(rs.getString("id_tipo_persona"));
					cons.setNoCuenta(rs.getInt("no_cuenta"));
					cons.setNoControladora(rs.getInt("no_controladora"));
					return cons;
				}});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:Global, C:GlobalDao, M:consultarEmpresasCoinversoras");
		}
		
		return listDatos;
	}
	
	/**
	 * Este m�todo obtiene las empresas asignadas a un usuario
	 * FunSQLSelectEmpresasUsuario
	 * @param iIdUsuario
	 * @return
	 */
	
	public List<LlenaComboGralDto> consultarEmpresasUsuario(int iIdUsuario){
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listEmp = new ArrayList<LlenaComboGralDto>();
		try{
		    sbSql.append("SELECT e.no_empresa, e.nom_empresa");
		    sbSql.append("\n FROM empresa e,persona p");
		    sbSql.append("\n Where e.no_empresa > 1");
		    sbSql.append("\n and p.id_estatus_per <> 'I'");
		    sbSql.append("\n and p.id_tipo_persona = 'E'");
		    sbSql.append("\n and p.no_empresa = e.no_empresa");
		    sbSql.append("\n and e.no_empresa in  (select no_empresa from usuario_empresa ");
		    sbSql.append("\n where no_usuario = " + iIdUsuario + ") order by nom_empresa");
			
		    System.out.println("crear usuario (llenar empresa): " + sbSql.toString());
		    
		    listEmp = jdbcTemplate.query(sbSql.toString(), new RowMapper<LlenaComboGralDto>()
		    {
		    	public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		LlenaComboGralDto dtoCons = new LlenaComboGralDto();
		    			dtoCons.setId(rs.getInt("no_empresa"));
		    			dtoCons.setDescripcion(rs.getString("nom_empresa"));
		    		return dtoCons;
		    	}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:Global, C:GlobalDao, M:consultarEmpresasUsuario");
		}
		return listEmp;
	}
	
	/**
	 * Este m�todo obtiene las cajas asignadas
	 * a un usuario, es utilizado para llenar el combo de cajas 
	 * en la ventana de Cambio de Empresa
	 * FunSQLSelectCajasUsuario
	 * @param iIdUsuario
	 * @return
	 */
	
	public List<LlenaComboGralDto> consultarCajasUsuario(int iIdUsuario)
	{
		StringBuffer sbSql = new StringBuffer(); 
		List<LlenaComboGralDto> listCajas = new ArrayList<LlenaComboGralDto>();
		try
		{
		    sbSql.append("SELECT id_caja,");
		    sbSql.append("\n desc_caja");
		    sbSql.append("\n FROM cat_caja");
		    sbSql.append("\n WHERE id_caja in");
		    sbSql.append("\n     (select id_caja from caja_usuario");
		    sbSql.append("\n     where no_usuario = " + iIdUsuario + ")");
		    
		    listCajas = jdbcTemplate.query(sbSql.toString(), new RowMapper<LlenaComboGralDto>()
		    {
		    	public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
		    	{
		    		LlenaComboGralDto dtoCons = new LlenaComboGralDto();
		    			dtoCons.setId(rs.getInt("id_caja"));
		    			dtoCons.setDescripcion(rs.getString("desc_caja"));
		    		return dtoCons;
		    	}
		    });
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:Global, C:GlobalDao, M:consultarCajasUsuario");
		}
		return listCajas;
	}
	
	public List<Map<String, Object>> consultarPerfiles(){
		List<Map<String, Object>> listPerfiles = new ArrayList<Map<String, Object>>();
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("Select usu.id_usuario, usuper.id_perfil, per.descripcion");
			sbSql.append("\n from seg_usuario_perfil usuper, seg_usuario usu, seg_perfil per");
			sbSql.append("\n where	usuper.id_usuario = usu.id_usuario");
			sbSql.append("\n and per.id_perfil = usuper.id_perfil");
			
			listPerfiles = jdbcTemplate.query(sbSql.toString(), new RowMapper<Map<String, Object>>(){
				public Map<String, Object> mapRow(ResultSet rs, int idx)throws SQLException{
					Map<String, Object> mapCons = new HashMap<String, Object>();
						mapCons.put("idUsuario", rs.getInt("id_usuario"));
						mapCons.put("idPerfil", rs.getInt("id_perfil"));
						mapCons.put("descripcion", rs.getString("descripcion"));
					return mapCons;
				}
			});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:Global, C:GlobalDao, M:consultarPerfiles");
		}
		return listPerfiles;
	}
	
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Global, C:GlobalDao, M:setDataSource");
		}
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	public UsuarioLoginDto obtenerPropiedadesUsuario(int iIdUsuario,
			String sCodSession) {
		StringBuffer sql = new StringBuffer();
		List<UsuarioLoginDto> result = new ArrayList<UsuarioLoginDto>();
		
		try{
				sql.append("SELECT su.id_usuario, su.apellido_paterno, su.apellido_materno, su.nombre, ");
				sql.append("	su.no_empresa, e.nom_empresa, ");
				sql.append("	su.id_caja, cc.desc_caja, ");
				sql.append("	e.no_cuenta_emp, usuper.id_perfil, CLAVE_USUARIO");
				sql.append("	FROM seg_usuario su, empresa e, cat_caja cc, seg_usuario_perfil usuper ");
				sql.append("	WHERE	su.no_empresa = e.no_empresa ");
				sql.append("	and usuper.id_usuario = su.id_usuario ");
				sql.append("	and su.id_usuario = " + iIdUsuario);
				sql.append("	and su.id_caja = cc.id_caja");
				result = jdbcTemplate.query(sql.toString(), new RowMapper<UsuarioLoginDto>(){
					public UsuarioLoginDto mapRow(ResultSet rs, int idx) throws SQLException {
						UsuarioLoginDto cons = new UsuarioLoginDto();	
							cons.setIdUsuario(rs.getInt("id_usuario"));
							cons.setApellidoPaterno(rs.getString("apellido_paterno"));
							cons.setApellidoMaterno(rs.getString("apellido_materno"));
							cons.setNombre(rs.getString("nombre"));
							cons.setNumeroEmpresa(rs.getInt("no_empresa"));
							cons.setNombreEmpresa(rs.getString("nom_empresa"));
							cons.setIdCaja(rs.getInt("id_caja"));
							cons.setDescripcionCaja(rs.getString("desc_caja"));
							cons.setNoCuentaEmpresa(rs.getString("no_cuenta_emp"));
							cons.setIdPerfil(rs.getInt("id_perfil"));
							cons.setClaveUsuario(rs.getString("CLAVE_USUARIO"));
						return cons;
					}});
				if (result != null && !result.isEmpty()) {
					result.get(0).setCodigoSesion(sCodSession);
				} else {
					result = new ArrayList<UsuarioLoginDto>();
					UsuarioLoginDto usuarioLoginDto = new UsuarioLoginDto();
					usuarioLoginDto.setIdUsuario(iIdUsuario);
					usuarioLoginDto.setCodigoSesion(sCodSession);
					result.add(usuarioLoginDto);
				}
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalDao, M:obtenerPropiedadesUsuario");
		}
		return !result.isEmpty()?result.get(0):null;
	}
	public Map<Integer, Boolean> getFacultades(int perfil) {
		List<Integer> listPerfiles = new ArrayList<Integer>();
		Map<Integer, Boolean> res = new HashMap<Integer, Boolean>();
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("select * FROM SEG_PER_FAC_COM where ID_PERFIL = ");
			sbSql.append(perfil);
			
			listPerfiles = jdbcTemplate.query(sbSql.toString(), new RowMapper<Integer>(){
				public Integer mapRow(ResultSet rs, int idx)throws SQLException{
					return rs.getInt("id_componente");
				}
			});
			
			
			for (Integer integer : listPerfiles) {
				res.put(integer, true);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:Global, C:GlobalDao, M:consultarPerfiles");
		}
		return res;
	}
	//prueba para obtener folios
		public int obtenerFolioReal(String tipoFolio){
			System.out.print(tipoFolio);
			int r=0;
			int f=0;
			try{
				consultasGenerales = new ConsultasGenerales(jdbcTemplate);
				f=consultasGenerales.seleccionarFolioReal(tipoFolio);
//				System.out.println("folio seleccionado "+f);
				if(f>0){
					r=f;
					consultasGenerales.actualizarFolioReal(tipoFolio);
				}
				
			}
			catch(Exception e){
				
				e.printStackTrace();
				logger.error(e);
				//bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "F: Folios , C: ConsultaFolioDao, M: obtenerFolioReal");
			}
			return r;
			
		}
		//
		public int seleccionarFolio(String tipoFolio){
			int r=0;
			try{
				consultasGenerales = new ConsultasGenerales(jdbcTemplate);
				r=consultasGenerales.seleccionarFolioReal(tipoFolio);
			}
			catch(Exception e){
				logger.error(e);
				e.printStackTrace();
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: seleccionarFolio");
			}
			return r;	
		}
		
		//
		public int actualizarFolioReal(String tipoFolio){	
			int r=0;
			try{
				consultasGenerales = new ConsultasGenerales(jdbcTemplate);
				r = consultasGenerales.actualizarFolioReal(tipoFolio);
			}
			catch(Exception e){
				logger.error(e);
				e.printStackTrace();
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: actualizarFolioReal");
			}return r;
			
		}
		
		
}
