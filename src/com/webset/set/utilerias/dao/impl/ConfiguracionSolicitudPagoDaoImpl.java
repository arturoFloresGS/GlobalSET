package com.webset.set.utilerias.dao.impl;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dao.ConfiguracionSolicitudPagoDao;
import com.webset.set.utileriasmod.dto.ConfiguracionSolicitudPagoDto;
import com.webset.utils.tools.Utilerias;
/**
 * Clase para guardar los parametros de una solicitud de pago y sus beneficiarios.
 * @author YEC
 * @since  26/11/2015
 */
public class ConfiguracionSolicitudPagoDaoImpl implements ConfiguracionSolicitudPagoDao{
	private JdbcTemplate jdbcTemplate = new JdbcTemplate();
	private Bitacora bitacora = new Bitacora();
	
	public List<ConfiguracionSolicitudPagoDto> llenaGridBeneficiarios(String idPoliza, String idGrupo, String idRubro, String beneficiarios){
		List<ConfiguracionSolicitudPagoDto> listaResultado = new ArrayList<ConfiguracionSolicitudPagoDto>();
		StringBuffer sql = new StringBuffer();
		try{
			if(beneficiarios.equals("")){
				sql.delete(0, sql.length());
				sql.append("SELECT equivale_persona,razon_social ");
				sql.append( "\n FROM persona ");
				sql.append( "\n where equivale_persona in ( ");
				sql.append( "\n select beneficiario ");
				sql.append( "\n FROM BENEFICIARIOS_SOLICITUD_PAGO ");
				sql.append( "\n WHERE id_poliza= " + Utilerias.validarCadenaSQL(idPoliza));
				sql.append( "\n AND id_grupo= " + Utilerias.validarCadenaSQL(idGrupo));
				sql.append( "\n AND id_rubro= " + Utilerias.validarCadenaSQL(idRubro)+" ) ");
				sql.append( "\n order by razon_social");
			}else{
				sql.delete(0, sql.length());
				sql.append("SELECT equivale_persona,razon_social");
				sql.append( "\n  FROM persona  ");
				sql.append( "\n WHERE EQUIVALE_PERSONA in ( " );
				sql.append( "\n "+ Utilerias.validarCadenaSQL(beneficiarios.substring(0, beneficiarios.length()-2))+ " )");
				sql.append( "\n and ID_TIPO_PERSONA = 'P'");	
			}
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<ConfiguracionSolicitudPagoDto>(){
				public ConfiguracionSolicitudPagoDto mapRow(ResultSet rs, int idx) throws SQLException{
					ConfiguracionSolicitudPagoDto campos = new ConfiguracionSolicitudPagoDto();
					campos.setIdBeneficiario(rs.getString("equivale_persona"));
					campos.setNombreBeneficiario(rs.getString("razon_social"));
					return campos;
				}
			});
		}	
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoDaoImpl, M:llenaGridBeneficiarios");
		}return listaResultado;
	}
	
	public List<ConfiguracionSolicitudPagoDto> llenaGridPolizas(){
		List<ConfiguracionSolicitudPagoDto> listaResultado = new ArrayList<ConfiguracionSolicitudPagoDto>();
		StringBuffer sql = new StringBuffer();
		 
		try{
			/*sql.append("SELECT p.ID_POLIZA,p.nombre_poliza,g.ID_GRUPO,g.desc_grupo,r.ID_RUBRO,r.desc_rubro ");
			sql.append( "\n  FROM  ZIMP_POLIZAMANUAL p, cat_grupo g,cat_RUBRO r ");
			sql.append( "\n where p.id_poliza||g.id_grupo|| r.id_rubro IN ( ");
			sql.append( "\n SELECT csp.id_poliza||csp.id_grupo|| csp.id_rubro ");
			sql.append( "\n FROM configura_solicitud_pago csp)");
			System.out.println(sql.toString());
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<ConfiguracionSolicitudPagoDto>(){
				public ConfiguracionSolicitudPagoDto mapRow(ResultSet rs, int idx) throws SQLException{
					ConfiguracionSolicitudPagoDto campos = new ConfiguracionSolicitudPagoDto();
					campos.setIdPoliza(rs.getString("id_poliza"));
					campos.setDescPoliza(rs.getString("nombre_poliza"));
					campos.setIdGrupo(rs.getString("id_grupo"));
					campos.setDescGrupo(rs.getString("desc_grupo"));
					campos.setIdRubro(rs.getString("id_rubro"));
					campos.setDescRubro(rs.getString("desc_rubro"));
					return campos;
				}
			});*/
			
			sql.append("select p.ID_POLIZA,p.nombre_poliza,g.ID_GRUPO,g.desc_grupo,r.ID_RUBRO,r.desc_rubro , ");
			sql.append( "\n   sp.asignacion,sp.centro_costos,sp.division,sp.fecha_pago,sp.observaciones,sp.orden,sp.pago_sin_poliza");
			sql.append( "\n   ,sp.referencia_pago,sp.referencia,sp.sociedad_gl,sp.texto,sp.texto_cabecera,sp.banco_interlocutor,sp.clase_doc");
			sql.append( "\n ,sp.transaccion,sp.cargo_en_cuenta,sp.cheque,sp.cheque_de_caja,sp.transferencia, ( ");
			sql.append( "\n   		select count(beneficiario) ");
			sql.append( "\n  		from BENEFICIARIOS_SOLICITUD_PAGO  bsp");
			sql.append( "\n  	    where bsp.id_poliza=sp.id_poliza ");
			sql.append( "\n 	    and bsp.id_grupo= sp.id_grupo  ");
			sql.append( "\n  	    and bsp.id_rubro=sp.id_rubro)as beneficiarios");
			sql.append( "\n  from configura_solicitud_pago sp, ZIMP_POLIZAMANUAL p, cat_grupo g,cat_RUBRO r ");
			sql.append( "\n  where  sp.id_poliza=p.id_poliza");
			sql.append( "\n    AND  sp.id_grupo= g.id_grupo");
			sql.append( "\n   AND sp.id_rubro = r.id_rubro");
			
			System.out.println(sql.toString());
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<ConfiguracionSolicitudPagoDto>(){
				public ConfiguracionSolicitudPagoDto mapRow(ResultSet rs, int idx) throws SQLException{
					ConfiguracionSolicitudPagoDto campos = new ConfiguracionSolicitudPagoDto();
					campos.setIdPoliza(rs.getString("id_poliza"));
					campos.setDescPoliza(rs.getString("nombre_poliza"));
					campos.setIdGrupo(rs.getString("id_grupo"));
					campos.setDescGrupo(rs.getString("desc_grupo"));
					campos.setIdRubro(rs.getString("id_rubro"));
					campos.setDescRubro(rs.getString("desc_rubro"));
					campos.setTexto(rs.getString("texto"));
					campos.setTextoCabecera(rs.getString("TEXTO_CABECERA"));
					campos.setAsignacion(rs.getString("ASIGNACION"));
					campos.setReferenciaPago(rs.getString("REFERENCIA_PAGO"));
					campos.setReferencia(rs.getString("REFERENCIA"));
					campos.setDivision(rs.getString("DIVISION"));
					campos.setCentroCostos(rs.getString("CENTRO_COSTOS"));
					campos.setOrden(rs.getString("ORDEN"));
					campos.setFechaPago(rs.getString("FECHA_PAGO"));
					campos.setObservaciones(rs.getString("OBSERVACIONES"));
					campos.setClaseDoc(rs.getString("clase_doc"));
					campos.setTransaccion(rs.getString("TRANSACCION" ));
					campos.setPagoSinPoliza(rs.getString("PAGO_SIN_POLIZA"));
					campos.setSociedadGL(rs.getString("sociedad_gl" ));
					campos.setTransaccion(rs.getString("TRANSACCION"));
					campos.setChequeCaja(rs.getString("CHEQUE_DE_CAJA"));
					campos.setCheque(rs.getString("CHEQUE"));
					campos.setTransferencia(rs.getString("TRANSFERENCIA"));
					campos.setCargoCuenta(rs.getString("CARGO_EN_CUENTA"));
					campos.setBancoInterlocutor(rs.getString("BANCO_INTERLOCUTOR" ));
					return campos;
				}
			});
		}	
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoDaoImpl, M:llenaGridPolizas");
		}return listaResultado;
	}
	
	public List<ConfiguracionSolicitudPagoDto> llenaComboPoliza(String numUsuario){
		List<ConfiguracionSolicitudPagoDto> listaResultado = new ArrayList<ConfiguracionSolicitudPagoDto>();
		StringBuffer sql = new StringBuffer();
		try{
			if(!numUsuario.equals("")&&!numUsuario.equals(null)){
				sql.append("SELECT id_poliza, nombre_poliza ");
				sql.append( "\n  FROM ZIMP_POLIZAMANUAL  ");
				//sql.append( "\n WHERE ID_POLIZA IN (");
				//sql.append( "\n SELECT ID_POLIZA ");
				//sql.append( "\n FROM USUARIO_POLIZA ");
				//sql.append( "\n where ID_USUARIO =" +numUsuario+") ");
				listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<ConfiguracionSolicitudPagoDto>(){
					public ConfiguracionSolicitudPagoDto mapRow(ResultSet rs, int idx) throws SQLException{
						ConfiguracionSolicitudPagoDto campos = new ConfiguracionSolicitudPagoDto();
						campos.setIdPoliza(rs.getString("id_poliza"));
						campos.setDescPoliza(rs.getString("nombre_poliza"));
						return campos;
					}
				});
			}
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoDaoImpl, M:llenaComboPoliza");
		}return listaResultado;
	}
	
	public List<ConfiguracionSolicitudPagoDto> llenaComboGrupo(String idRubro){
		List<ConfiguracionSolicitudPagoDto> listaResultado = new ArrayList<ConfiguracionSolicitudPagoDto>();
		StringBuffer sql = new StringBuffer();
		try{
			if(!idRubro.equals("")&&!idRubro.equals(null)){
				sql.append("SELECT id_grupo, desc_grupo ");
				sql.append( "\n  FROM cat_grupo  ");
				sql.append( "\n WHERE id_grupo IN (");
				sql.append( "\n SELECT id_grupo ");
				sql.append( "\n FROM cat_rubro ");
				sql.append( "\n where id_rubro=" +Utilerias.validarCadenaSQL(idRubro)+") ");
				System.out.println(sql.toString());
				listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<ConfiguracionSolicitudPagoDto>(){
					public ConfiguracionSolicitudPagoDto mapRow(ResultSet rs, int idx) throws SQLException{
						ConfiguracionSolicitudPagoDto campos = new ConfiguracionSolicitudPagoDto();
						campos.setIdGrupo(rs.getString("id_grupo"));
						campos.setDescGrupo(rs.getString("desc_grupo"));
						return campos;
					}
				});
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoDaoImpl, M:llenaComboGrupo");
		}return listaResultado;
	}
	
	public List<ConfiguracionSolicitudPagoDto> llenaComboRubro(String idPoliza){
		List<ConfiguracionSolicitudPagoDto> listaResultado = new ArrayList<ConfiguracionSolicitudPagoDto>();
		StringBuffer sql = new StringBuffer();
		try{
			if(!idPoliza.equals("")&&!idPoliza.equals(null)){
				sql.append("SELECT id_rubro,desc_rubro  ");
				sql.append( "\n  FROM cat_rubro  ");
				sql.append( "\n WHERE id_rubro IN (");
				sql.append( "\n select id_grupo ");
				sql.append( "\n from asignacion_poliza_grupo ");
				sql.append( "\n where id_poliza= "+Utilerias.validarCadenaSQL(idPoliza)+")");
				
				System.out.println(sql.toString());
				listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<ConfiguracionSolicitudPagoDto>(){
					public ConfiguracionSolicitudPagoDto mapRow(ResultSet rs, int idx) throws SQLException{
						ConfiguracionSolicitudPagoDto campos = new ConfiguracionSolicitudPagoDto();
						campos.setIdRubro(rs.getString("id_rubro"));
						campos.setDescRubro(rs.getString("desc_rubro"));
						return campos;
					}
				});
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoDaoImpl, M:llenaComboRubro");
		}return listaResultado;
	}
	
	public String existeConfiguracionSolicitudPago(ConfiguracionSolicitudPagoDto dto){
		String resultado="Error";
		try{
			StringBuffer sql = new StringBuffer();
			int resp;
			sql.append("select count(*)  ");
			sql.append("\n FROM CONFIGURA_SOLICITUD_PAGO");
			sql.append("\n  WHERE ");
			sql.append("\n id_poliza= " + Utilerias.validarCadenaSQL(dto.getIdPoliza()));
			sql.append("\n AND id_grupo= " + Utilerias.validarCadenaSQL(dto.getIdGrupo()));
			sql.append("\n AND id_rubro= " + Utilerias.validarCadenaSQL(dto.getIdRubro()));
			System.out.println(sql.toString());
			resp=jdbcTemplate.queryForInt(sql.toString());
			if(resp==1){
				resultado="Existe";
			}else{
				resultado="No existe";
			}
		}
		catch(CannotGetJdbcConnectionException e){
			resultado="Error al conectar con la base de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoDaoImpl, M:existeConfiguracionSolicitudPago");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoDaoImpl, M:existeConfiguracionSolicitudPago");
		}return resultado;
	}
	
	public ConfiguracionSolicitudPagoDto consultarConfiguracionSolicitudPago(ConfiguracionSolicitudPagoDto dto){
		List<ConfiguracionSolicitudPagoDto> listaResultado = new ArrayList<ConfiguracionSolicitudPagoDto>();
		StringBuffer sql = new StringBuffer();
		try{
			if(!dto.getIdPoliza().equals("")&&!dto.getIdGrupo().equals("")&&!dto.getIdRubro().equals("")){
				sql.append("select *  ");
				sql.append("\n FROM CONFIGURA_SOLICITUD_PAGO");
				sql.append("\n  WHERE ");
				sql.append("\n id_poliza= " + Utilerias.validarCadenaSQL(dto.getIdPoliza()));
				sql.append("\n AND id_grupo= " + Utilerias.validarCadenaSQL(dto.getIdGrupo()));
				sql.append("\n AND id_rubro= " + Utilerias.validarCadenaSQL(dto.getIdRubro()));
				listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<ConfiguracionSolicitudPagoDto>(){
					public ConfiguracionSolicitudPagoDto mapRow(ResultSet rs, int idx) throws SQLException{
						ConfiguracionSolicitudPagoDto campos = new ConfiguracionSolicitudPagoDto();
						campos.setTexto(rs.getString("texto"));
						campos.setTextoCabecera(rs.getString("TEXTO_CABECERA"));
						campos.setAsignacion(rs.getString("ASIGNACION"));
						campos.setReferenciaPago(rs.getString("REFERENCIA_PAGO"));
						campos.setReferencia(rs.getString("REFERENCIA"));
						campos.setDivision(rs.getString("DIVISION"));
						campos.setCentroCostos(rs.getString("CENTRO_COSTOS"));
						campos.setOrden(rs.getString("ORDEN"));
						campos.setFechaPago(rs.getString("FECHA_PAGO"));
						campos.setObservaciones(rs.getString("OBSERVACIONES"));
						campos.setDivisaPago(rs.getString("DIVISA_PAGO"));
						campos.setAreaDestino(rs.getString("AREA_DESTINO"));
						campos.setClaseDoc(rs.getString("clase_doc"));
						campos.setTransaccion(rs.getString("TRANSACCION" ));
						campos.setPagoSinPoliza(rs.getString("PAGO_SIN_POLIZA"));
						campos.setSociedadGL(rs.getString("SOCIEDAD_GL" ));
						campos.setChequeCaja(rs.getString("CHEQUE_DE_CAJA"));
						campos.setCheque(rs.getString("CHEQUE"));
						campos.setTransferencia(rs.getString("TRANSFERENCIA"));
						campos.setCargoCuenta(rs.getString("CARGO_EN_CUENTA"));
						campos.setBancoInterlocutor(rs.getString("BANCO_INTERLOCUTOR" ));
						return campos;
					}
				});
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoDaoImpl, M:consultarConfiguracionSolicitudPago");
		}return listaResultado.get(0);
	}
	
	public String insertaConfiguracionSolicitudPago(ConfiguracionSolicitudPagoDto dto){
		String resultado="Error";
		try{
			StringBuffer sql = new StringBuffer();
			int resp;
			sql.append("INSERT into CONFIGURA_SOLICITUD_PAGO values( ");
			sql.append("\n" + Utilerias.validarCadenaSQL(dto.getIdPoliza()) + ",");
			sql.append("\n" + Utilerias.validarCadenaSQL(dto.getIdGrupo()) + ",");
			sql.append("\n" + Utilerias.validarCadenaSQL(dto.getIdRubro()) + ",");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getTexto())+ "',");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getTextoCabecera())+ "',");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getAsignacion())+ "',");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getReferenciaPago())+ "',");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getReferencia())+ "',");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getSociedadGL())+ "',");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getCentroCostos())+ "',");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getDivision())+ "',");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getOrden())+ "',");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getBancoInterlocutor())+ "',");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getFechaPago())+ "',");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getObservaciones())+ "',");
			sql.append("\n 'NO',"); //DOCUMENTO
			sql.append("\n 'NO',"); //IMPORTE_PAGO
			sql.append("\n'NO',"); //TIPO_CAMBIO
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getDivisaPago())+ "',");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getAreaDestino())+ "',");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getClaseDoc())+ "',");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getPagoSinPoliza())+ "',");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getTransaccion())+ "',");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getChequeCaja())+ "',");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getCheque())+ "',");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getTransferencia())+ "',");
			sql.append("\n'" + Utilerias.validarCadenaSQL(dto.getCargoCuenta())+ "')");
			System.out.println(sql.toString());
			resp=jdbcTemplate.update(sql.toString());
			if(resp==1){
				resultado="Exito";
			}
		}
		catch(CannotGetJdbcConnectionException e){
			e.printStackTrace();
			resultado="Error al conectar con la base de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoDaoImpl, M:insertaConfiguracionSolicitudPago");
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoDaoImpl, M:insertaConfiguracionSolicitudPago");
		}return resultado;
	}
	
	public String updateConfiguracionSolicitudPago(ConfiguracionSolicitudPagoDto dto){
		String resultado="Error";
		try{
			StringBuffer sql = new StringBuffer();
			int resp;
			
			
			sql.append("UPDATE CONFIGURA_SOLICITUD_PAGO ");
			sql.append("\n set texto='" + Utilerias.validarCadenaSQL(dto.getTexto()) + "',");
			sql.append("\n  texto_cabecera='" + Utilerias.validarCadenaSQL(dto.getTextoCabecera()) + "',");
			sql.append("\n  asignacion='" + Utilerias.validarCadenaSQL(dto.getAsignacion()) + "',");
			sql.append("\n  referencia_pago='" + Utilerias.validarCadenaSQL(dto.getReferenciaPago()) + "',");
			sql.append("\n  referencia='" + Utilerias.validarCadenaSQL(dto.getReferencia()) + "',");
			sql.append("\n  SOCIEDAD_GL='" + Utilerias.validarCadenaSQL(dto.getSociedadGL()) + "',");
			sql.append("\n centro_costos='" + Utilerias.validarCadenaSQL(dto.getCentroCostos()) + "',");
			sql.append("\n division='" + Utilerias.validarCadenaSQL(dto.getDivision()) + "',");
			sql.append("\n orden='" + Utilerias.validarCadenaSQL(dto.getOrden()) + "',");
			sql.append("\n BANCO_INTERLOCUTOR='" + Utilerias.validarCadenaSQL(dto.getBancoInterlocutor()) + "',");
			sql.append("\n fecha_pago='" + Utilerias.validarCadenaSQL(dto.getFechaPago()) + "',");
			sql.append("\n observaciones='" + Utilerias.validarCadenaSQL(dto.getObservaciones()) + "',");
			sql.append("\n divisa_pago='" + Utilerias.validarCadenaSQL(dto.getDivisaPago()) + "',");
			sql.append("\n area_destino='" + Utilerias.validarCadenaSQL(dto.getAreaDestino()) + "',");
			sql.append("\n clase_doc ='" + Utilerias.validarCadenaSQL(dto.getClaseDoc()) + "',");
			sql.append("\n transaccion='" + Utilerias.validarCadenaSQL(dto.getTransaccion()) + "',");
			sql.append("\n PAGO_SIN_POLIZA='" + Utilerias.validarCadenaSQL(dto.getPagoSinPoliza())+ "',");
			sql.append("\n CHEQUE_DE_CAJA='" + Utilerias.validarCadenaSQL(dto.getChequeCaja()) + "',");
			sql.append("\n CHEQUE='" + Utilerias.validarCadenaSQL(dto.getCheque()) + "',");
			sql.append("\n TRANSFERENCIA='" + Utilerias.validarCadenaSQL(dto.getTransferencia()) + "',");
			sql.append("\n CARGO_EN_CUENTA='" + Utilerias.validarCadenaSQL(dto.getCargoCuenta()) + "'");
			sql.append("\n  WHERE ");
			sql.append("\n id_poliza= " + Utilerias.validarCadenaSQL(dto.getIdPoliza()));
			sql.append("\n AND id_grupo= " + Utilerias.validarCadenaSQL(dto.getIdGrupo()));
			sql.append("\n AND id_rubro= " + Utilerias.validarCadenaSQL(dto.getIdRubro()));
			resp=jdbcTemplate.update(sql.toString());
			if(resp==1){resultado="Exito";}
		}
		catch(CannotGetJdbcConnectionException e){
			resultado="Error al conectar con la base de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoDaoImpl, M:updateConfiguracionSolicitudPago");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoDaoImpl, M:updateConfiguracionSolicitudPago");
		}return resultado;
	}

	public String deleteConfiguracionSolicitudPago(ConfiguracionSolicitudPagoDto dto){
		String resultado="Error";
		try{
			StringBuffer sql = new StringBuffer();
			int resp;
			sql.append("delete CONFIGURA_SOLICITUD_PAGO ");
			sql.append("\n  WHERE ");
			sql.append("\n id_poliza= " + Utilerias.validarCadenaSQL(dto.getIdPoliza()));
			sql.append("\n AND id_grupo= " + Utilerias.validarCadenaSQL(dto.getIdGrupo()));
			sql.append("\n AND id_rubro= " + Utilerias.validarCadenaSQL(dto.getIdRubro()));
			resp=jdbcTemplate.update(sql.toString());
			if(resp==1){
				resultado="Exito";
			}
		}
		catch(CannotGetJdbcConnectionException e){
			resultado="Error al conectar con la base de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoDaoImpl, M:deleteConfiguracionSolicitudPago");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoDaoImpl, M:deleteConfiguracionSolicitudPago");
		}return resultado;
	}
	
	public String existenBeneficiarios(String[] beneficiarios){
		String resultado="Exito al cargar el archivo";
		String noBeneficiarios="";
		StringBuffer sql = new StringBuffer();
		try{
			for (int i = 0; i < beneficiarios.length; i++) {
				int resp=0;
				sql.delete(0, sql.length());
				sql.append("SELECT count(*)");
				sql.append( "\n  FROM persona  ");
				sql.append( "\n WHERE EQUIVALE_PERSONA = "+ Utilerias.validarCadenaSQL(beneficiarios[i]));
				sql.append( "\n and ID_TIPO_PERSONA = 'P'");
				resp=jdbcTemplate.queryForInt(sql.toString());
				if(resp!=1){
					resultado="Error los siguientes beneficiarios no existen: \n";
					noBeneficiarios+=" "+beneficiarios[i]+" ,";
				}
			}
			resultado=resultado+noBeneficiarios;
		} 
		catch(CannotGetJdbcConnectionException e){
			resultado="Error al conectar con la base de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoDaoImpl, M:existenBeneficiarios");
			
		}
		catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoDaoImpl, M:existenBeneficiarios");
		}return resultado;
		
	}
	
	
	
	public String guardarBeneficiarios(String[] beneficiarios, ConfiguracionSolicitudPagoDto dto){
		String resultado="Error";
		StringBuffer sql = new StringBuffer();
		int resp;
		try{
			sql.append("delete BENEFICIARIOS_SOLICITUD_PAGO ");
			sql.append("\n  WHERE ");
			sql.append("\n id_poliza= " + Utilerias.validarCadenaSQL(dto.getIdPoliza()));
			sql.append("\n AND id_grupo= " + Utilerias.validarCadenaSQL(dto.getIdGrupo()));
			sql.append("\n AND id_rubro= " + Utilerias.validarCadenaSQL(dto.getIdRubro()));
			resp=jdbcTemplate.update(sql.toString());
			
				for (int i = 0; i < beneficiarios.length; i++) {
					resp=0;
					sql.delete(0, sql.length());
					sql.append("select count(*)  ");
					sql.append("\n FROM BENEFICIARIOS_SOLICITUD_PAGO");
					sql.append("\n  WHERE ");
					sql.append("\n id_poliza= " + Utilerias.validarCadenaSQL(dto.getIdPoliza()));
					sql.append("\n AND id_grupo= " + Utilerias.validarCadenaSQL(dto.getIdGrupo()));
					sql.append("\n AND id_rubro= " + Utilerias.validarCadenaSQL(dto.getIdRubro()));
					sql.append("\n AND beneficiario= " + Utilerias.validarCadenaSQL(beneficiarios[i]));
					resp=jdbcTemplate.queryForInt(sql.toString());
					if(resp!=1){
						//insert
						sql.delete(0, sql.length());
						sql.append("INSERT into BENEFICIARIOS_SOLICITUD_PAGO values( ");
						sql.append("\n" + Utilerias.validarCadenaSQL(dto.getIdPoliza()) + ",");
						sql.append("\n" + Utilerias.validarCadenaSQL(dto.getIdGrupo()) + ",");
						sql.append("\n" + Utilerias.validarCadenaSQL(dto.getIdRubro()) + ",");
						sql.append("\n" + Utilerias.validarCadenaSQL(beneficiarios[i])+ ")");
						resp=jdbcTemplate.update(sql.toString());
						if(resp==1){resultado="Exito";}
					}
				}
			
		}
		catch(CannotGetJdbcConnectionException e){
			resultado="Error al conectar con la base de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoDaoImpl, M:guardarBeneficiarios");
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoDaoImpl, M:guardarBeneficiarios");
		}return resultado;
		
	}
	//Fin metodos para bene
	
	//**************************************************************		
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoDaoImpl, M:setDataSource");
		}
	}
	
}
