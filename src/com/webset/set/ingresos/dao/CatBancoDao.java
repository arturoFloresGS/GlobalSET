package com.webset.set.ingresos.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.ingresos.dto.CatArmaIngresoDto;
import com.webset.set.ingresos.dto.CatBancoDto;
import com.webset.set.ingresos.dto.CatCtaBancoDto;
import com.webset.set.ingresos.dto.CatGrupoRubroDto;
import com.webset.set.seguridad.dto.EmpresaDto;
import com.webset.set.utilerias.Bitacora;
/**
 * 
 * @author Jessica Arelly 
 *
 */
public class CatBancoDao {
	private JdbcTemplate jdbcTemplate;
	private Bitacora bitacora = new Bitacora();
	private static Logger logger = Logger.getLogger(CatBancoDao.class);
	
	/**
	 * FunSQLComboEmpresasUsuario(GI_USUARIO))
	 * Metodo que consulta la tabla de empresa 
	 * @param empresa
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<EmpresaDto> consultarEmpresa(EmpresaDto empresa) throws Exception{
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT ea.no_empresa, ea.nom_empresa ");
		sb.append(" FROM empresa ea");
		//sb.append(" WHERE ea.no_empresa in ");
		//sb.append(" ( SELECT u.no_empresa ");
		//sb.append(" FROM usuario_empresa u ");
		//sb.append(" WHERE u.no_usuario = " + piNoUsuario + " )");
		sb.append(" ORDER BY ea.nom_empresa ");
		sql=sb.toString();
		List <EmpresaDto> empresas = null;
		try{
			empresas = jdbcTemplate.query(sql, new RowMapper(){
			
			public EmpresaDto mapRow(ResultSet rs, int idx) throws SQLException {
				EmpresaDto empresa = new EmpresaDto();
				empresa.setNoEmpresa(rs.getInt("no_empresa"));
				empresa.setNomEmpresa(rs.getString("nom_empresa"));
				//empresa.setNoUsuario(rs.getInt("no_usuario"));
			return empresa;
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Ingresos, C:CatBancoDao, M:consultarEmpresa");
			}
		return empresas;
	}
	
	
	/**
	funcion que busca empresas FID
	Public Function FunSQLSelectEmpresasFIDDes() As ADODB.Recordset
    */
	    @SuppressWarnings("unchecked")
		public List<EmpresaDto> consultarEmpresaFID(EmpresaDto empresa) throws Exception{
			String sql = "";
			StringBuffer sb = new StringBuffer();
			sb.append( " SELECT ea.no_empresa as ID, ea.nom_empresa as Descrip " );
			sb.append( " FROM empresa ea " );
			sb.append( " WHERE ea.no_empresa in " );
			sb.append( " (SELECT no_empresa FROM cat_cta_banco " );
			sb.append( " WHERE tipo_chequera = 'F' " );
			sb.append( ")" );
			sb.append( "ORDER BY Descrip " );
			    sql=sb.toString();
				List <EmpresaDto> empresas = null;
				try{
					empresas = jdbcTemplate.query(sql, new RowMapper(){
					
					public EmpresaDto mapRow(ResultSet rs, int idx) throws SQLException {
						EmpresaDto empresa = new EmpresaDto();
						empresa.setNoEmpresa(rs.getInt("no_empresa"));
						empresa.setNomEmpresa(rs.getString("nom_empresa"));
					return empresa;
					}});
				
					}catch(Exception e){
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
						+ "P:Ingresos, C:CatBancoDao, M:consultarEmpresaFID");
					}
				return empresas;
			}
	
	
	/**
	 * Metodo que consulta la tabla de cat_banco 
	 * Public Function FunSQLCombo369(ByVal plNoEmpresa As Integer) As ADODB.Recordset
     * On Error GoTo HayError
     * Dim sSQL As String
	 * @param banco
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<CatBancoDto> consultarBanco(int emp) throws Exception{
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT distinct b.id_banco as ID, b.desc_banco as Descrip ");
		sb.append(" FROM cat_banco b, cat_cta_banco c");
		sb.append(" WHERE b.id_banco = c.id_banco");
		sb.append(" AND c.no_empresa = " + emp);
		sb.append(" ORDER BY Descrip ");
		sql=sb.toString();
		List <CatBancoDto> bancos = null;
		try{
			bancos = jdbcTemplate.query(sql, new RowMapper(){
			
			public CatBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatBancoDto banco = new CatBancoDto();
				
				banco.setIdBanco(rs.getInt("ID"));
				banco.setDescBanco(rs.getString("Descrip"));
				
			return banco;
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Ingresos, C:CatBancoDao, M:consultarBanco");
			}
		return bancos;
	}
	
	/**
	 * funcion que busca  bancos FID
		Public Function FunSQLSelectBancosFIDDes(ByVal pi_empresa As Integer) As ADODB.Recordset
	    Dim sSQL As String
	    On Error GoTo HayError
	 * */
	@SuppressWarnings("unchecked")
	public List<CatBancoDto> consultarBancoFID(int emp) throws Exception{
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append( " SELECT distinct b.id_banco as ID, b.desc_banco as Descrip ");
		sb.append( " FROM cat_banco b ");
		sb.append( " WHERE b.id_banco in (SELECT c.id_banco ");
		sb.append( " FROM cat_cta_banco c ");
		sb.append( " WHERE c.no_empresa = " + emp + " ");
		sb.append( " AND c.tipo_chequera = 'F')");
		sql=sb.toString();
		List <CatBancoDto> bancos = null;
		try{
			bancos = jdbcTemplate.query(sql, new RowMapper(){
			
			public CatBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatBancoDto banco = new CatBancoDto();
				
				banco.setIdBanco(rs.getInt("ID"));
				banco.setDescBanco(rs.getString("Descrip"));
				
			return banco;
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Ingresos, C:CatBancoDao, M:consultarBancoFID");
			}
		return bancos;
	}
	
	/**
	 * Metodo que consulta la tabla de cat_cta_banco 
	 * @param banco
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<CatCtaBancoDto> consultarChequera(int ban, int emp) throws Exception{
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT id_chequera as ID ");
		sb.append(" FROM cat_cta_banco ");
		sb.append(" WHERE id_banco =" + ban);
		sb.append(" AND no_empresa = " + emp);
		sb.append(" ORDER BY id_chequera ");
		sql=sb.toString();
		List <CatCtaBancoDto> bancos = null;
		try{
			bancos = jdbcTemplate.query(sql, new RowMapper(){
			
			public CatCtaBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatCtaBancoDto banco = new CatCtaBancoDto();
				banco.setIdChequera(rs.getString("ID"));
			return banco;
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Ingresos, C:CatBancoDao, M:consultarChequera");
			}
		return bancos;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<CatCtaBancoDto> consultarChequeraFID(int ban, int emp) throws Exception{
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append( " SELECT id_chequera as ID, id_chequera as Descrip " );
		sb.append( " FROM cat_cta_banco " );
		sb.append( " WHERE id_banco = " + ban + " " );
		sb.append( " AND no_empresa = " + emp + " " );
		sb.append( " AND tipo_chequera = 'F'" );
		sql=sb.toString();
		List <CatCtaBancoDto> bancos = null;
		try{
			bancos = jdbcTemplate.query(sql, new RowMapper(){
			
			public CatCtaBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatCtaBancoDto banco = new CatCtaBancoDto();
				banco.setIdChequera(rs.getString("ID"));
			return banco;
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Ingresos, C:CatBancoDao, M:consultarChequera");
			}
		return bancos;
	}
	
	
	/**FunSQLSelectTipoIngresos
	 * funcion para llenar el grid de consulta
	 * */
	@SuppressWarnings("unchecked")
	public List<CatArmaIngresoDto> llenarGrid(int ban, int emp, String cheque) throws Exception{
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT cai.id_banco, cai.id_chequera, cc.id_corresponde, cc.desc_corresponde, cai.long_referencia, cai.long_empresa, cai.orden_empresa, ");
		sb.append(" cai.long_cliente, cai.orden_cliente, cai.long_codigo, cai.orden_codigo,cai.long_subcodigo, ");
		sb.append(" cai.orden_subcodigo, cai.long_division, cai.orden_division, cai.const1, cai.orden_const1, ");
		sb.append(" cai.const2, cai.orden_const2, cai.const3, cai.orden_const3, cai.id_chequera_destino,  ");
		sb.append(" cai.base_calculo, cai.b_cambia_origen, cai.b_cambia_destino, cai.id_rubro, cai.destino_empresa, ");
		sb.append(" cai.destino_const1, cai.destino_const2, cai.destino_const3, cai.orden_var1, cai.orden_var2, ");
		sb.append(" cai.orden_var3, cai.long_var1, cai.long_var2, cai.long_var3, cai.destino_var1, ");
		sb.append(" cai.destino_var2,  cai.destino_var3, cai.b_predeterminada ");
		sb.append(" FROM cat_corresponde cc JOIN cat_arma_ingreso cai ");
		sb.append(" ON (cc.id_corresponde = cai.id_corresponde ");
		sb.append(" AND cai.no_empresa= " + emp );
		sb.append(" AND cai.id_banco= " + ban );
		sb.append(" AND cai.id_chequera= '" + cheque + "')");
		sb.append(" ORDER BY cc.desc_corresponde ");
	sql=sb.toString();
	List <CatArmaIngresoDto> cais = null;
	try{
		cais = jdbcTemplate.query(sql, new RowMapper(){
		
		public CatArmaIngresoDto mapRow(ResultSet rs, int idx) throws SQLException {
			CatArmaIngresoDto cai = new CatArmaIngresoDto();
			cai.setIdBanco(rs.getInt("id_banco"));
			cai.setIdChequera(rs.getString("id_chequera"));
			cai.setIdCorrespondeCC(rs.getString("id_corresponde"));
			cai.setDescCorrespondeCC(rs.getString("desc_corresponde"));
			cai.setLongReferencia(rs.getInt("long_referencia"));
			cai.setLongEmpresa(rs.getInt("long_empresa"));
			cai.setOrdenEmpresa(rs.getInt("orden_empresa"));
			cai.setLongCliente(rs.getInt("long_cliente"));
			cai.setOrdenCliente(rs.getInt("orden_cliente"));
			cai.setLongCodigo(rs.getInt("long_codigo"));
			cai.setOrdenCodigo(rs.getInt("orden_codigo"));
			cai.setLongSubcodigo(rs.getInt("long_subcodigo"));
			cai.setOrdenSubcodigo(rs.getInt("orden_subcodigo"));
			cai.setLongDivision(rs.getInt("long_division"));
			cai.setOrdenDivision(rs.getInt("orden_division"));
			cai.setConst1(rs.getString("const1"));
			cai.setOrdenConst1(rs.getInt("orden_const1"));
			cai.setConst2(rs.getString("const2"));
			cai.setOrdenConst2(rs.getInt("orden_const2"));
			cai.setConst3(rs.getString("const3"));
			cai.setOrdenConst3(rs.getInt("orden_const3"));
			cai.setIdChequeraDestino(rs.getString("id_chequera_destino"));
			cai.setBaseCalculo(rs.getString("base_calculo"));
			cai.setBCambiaOrigen(rs.getString("b_cambia_origen"));
			cai.setBCambiaDestino(rs.getString("b_cambia_destino"));
			cai.setIdRubro(rs.getInt("id_rubro"));
			cai.setDestinoEmpresa(rs.getString("destino_empresa"));
			cai.setDestinoConst1(rs.getString("destino_const1"));
			cai.setDestinoConst2(rs.getString("destino_const2"));
			cai.setDestinoConst3(rs.getString("destino_const3"));
			cai.setOrdenVar1(rs.getInt("orden_var1"));
			cai.setOrdenVar2(rs.getInt("orden_var2"));
			cai.setOrdenVar3(rs.getInt("orden_var3"));
			cai.setLongVar1(rs.getInt("long_var1"));
			cai.setLongVar2(rs.getInt("long_var2"));
			cai.setLongVar3(rs.getInt("long_var3"));
			cai.setDestinoVar1(rs.getString("destino_var1"));
			cai.setDestinoVar2(rs.getString("destino_var2"));
			cai.setDestinoVar3(rs.getString("destino_var3"));
			cai.setBPredeterminada(rs.getString("b_predeterminada"));
		return cai;
		}});
	
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Ingresos, C:CatBancoDao, M:llenarGrid");
		}
	return cais;
}
	
	/**
	 * Metodo que llena el combo TipoIngreso
	 * FunSQLComboTipoIngreso
	 * @param ingreso
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<CatArmaIngresoDto> llenarTipoIngreso(int plNoEmpresa, int plIdBanco, String psIDChequera) throws Exception{
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT cc.id_corresponde, cc.desc_corresponde " );
		sb.append(" FROM cat_corresponde cc ");
		 /*if( plNoEmpresa != 0 && plIdBanco != 0 && !psIDChequera.equals("") )
		 {
			sb.append("JOIN cat_arma_ingreso cai " );
			sb.append("ON (cc.id_corresponde=cai.id_corresponde AND " );
			sb.append("cai.no_empresa=" + plNoEmpresa + " AND " );
			sb.append("cai.id_banco=" + plIdBanco + " AND " );
			sb.append("cai.id_chequera='" + psIDChequera + "') ");
		 }*/
		 sb.append( "ORDER BY cc.desc_corresponde " );
		 sql=sb.toString();
	List <CatArmaIngresoDto> ingresos = null;
	try{
		ingresos = jdbcTemplate.query(sql, new RowMapper(){
		public CatArmaIngresoDto mapRow(ResultSet rs, int idx) throws SQLException {
			CatArmaIngresoDto ingreso = new CatArmaIngresoDto();
			ingreso.setIdCorrespondeCC(rs.getString("id_corresponde"));
			ingreso.setDescCorrespondeCC(rs.getString("desc_corresponde"));
		return ingreso;
		}});
	
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Ingresos, C:CatBancoDao, M:llenarTipoIngreso");
		}
	return ingresos;
}
	
	/**
	 * metodo que llena el combo grupo
	 * FunSQLComboGrupo()
	 * */
	@SuppressWarnings("unchecked")
	public List<CatGrupoRubroDto> llenarGrupo() throws Exception{
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT id_grupo as ID, desc_grupo as Descrip " );
		sb.append(" FROM cat_grupo  " );
		sql=sb.toString();
		List <CatGrupoRubroDto> gpoRubro = null;
		try{
			gpoRubro = jdbcTemplate.query(sql, new RowMapper(){
			public CatGrupoRubroDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatGrupoRubroDto grupo = new CatGrupoRubroDto();
				grupo.setIdGrupoG(rs.getInt("ID"));
				grupo.setDescGrupoG(rs.getString("Descrip"));
			return grupo;
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Ingresos, C:CatBancoDao, M:llenarGrupo");
			}
		return gpoRubro;
	}
	
	/**metodo que llena el combo rubro
	 * FunSQLComboRubro
	 * */
	@SuppressWarnings("unchecked")
	public List<CatGrupoRubroDto> llenarRubro(int idGrupo) throws Exception{
		String sql = "";
		StringBuffer sb = new StringBuffer();
		sb.append( " SELECT id_rubro as ID, desc_rubro as Descrip " );
		sb.append( " FROM cat_rubro " );
		sb.append( " WHERE id_grupo=" + idGrupo );
		sql=sb.toString();
		List <CatGrupoRubroDto> gpoRubro = null;
		try{
			gpoRubro = jdbcTemplate.query(sql, new RowMapper(){
			public CatGrupoRubroDto mapRow(ResultSet rs, int idx) throws SQLException {
				CatGrupoRubroDto rubro = new CatGrupoRubroDto();
				
				rubro.setIdRubroR(rs.getInt("ID"));
				rubro.setDescRubroR(rs.getString("Descrip"));
				
			return rubro;
			}});
		
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+ "P:Ingresos, C:CatBancoDao, M:llenarRubro");
			}
		return gpoRubro;
	}
	
	/**
	 * metodo que inserta el armado de referencia
	 * Public Function FunSQLInsertaArmaIngreso
    */
	
	@SuppressWarnings("unchecked")
	public int insertarArmaIngreso(CatArmaIngresoDto ingreso, boolean pbCambiaCtas) throws Exception{
		int res = -1;
		int resIn = -1;
		StringBuffer sb = new StringBuffer();
		StringBuffer sb2 = new StringBuffer(); 
		
				/**Valid if the record already exists*/
			    try{
				sb2.append( "SELECT no_empresa " );
			    sb2.append( "FROM cat_arma_ingreso " );
			    sb2.append( "WHERE no_empresa = " + ingreso.getNoEmpresa() + " AND " );
			    sb2.append( "id_banco = " + ingreso.getIdBanco() + " AND " );
			    sb2.append( "id_chequera = '" + ingreso.getIdChequera() + "' AND " );
			    sb2.append( "id_corresponde = '" + ingreso.getIdCorresponde() + "'" );
			    //System.out.println(sb2.toString());
			    res = jdbcTemplate.queryForInt((sb2.toString()));
			    jdbcTemplate = null;
			    }catch(Exception e){
					bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Ingresos, C:CatBancoDao, M:insertarArmaIngreso:consulta");
				}
			    if (res <= 0)
			    {
			    	try{
				    /**insert new record*/
				   
				    sb.append( "insert into cat_arma_ingreso (" );
				    sb.append( "\n no_empresa, id_banco, id_chequera, id_corresponde," );
				    sb.append( "\n long_referencia, orden_empresa, orden_cliente," );
				    sb.append( "\n orden_codigo, orden_subcodigo, orden_division," );
				    sb.append( "\n orden_const1, orden_const2, orden_const3," );
				    sb.append( "\n long_empresa, long_cliente, long_codigo," );
				    sb.append( "\n long_subcodigo, long_division, const1, const2," );
				    sb.append( "\nconst3, base_calculo, id_rubro, id_chequera_destino," );
				    sb.append( "\n b_cambia_origen, b_cambia_destino," );
				    sb.append( "\n destino_empresa,destino_const1,destino_const2," );
				    sb.append( "\n destino_const3,orden_var1,orden_var2,orden_var3," );
				    sb.append( "\n long_var1,long_var2,long_var3,destino_var1," );
				    sb.append( "\n destino_var2,destino_var3,b_predeterminada" );
				    sb.append( ")" );
				    sb.append( "\n values (" + ingreso.getNoEmpresa() + "," + ingreso.getIdBanco() + "," );
				    sb.append( "'" + ingreso.getIdChequera() + "', '" + ingreso.getIdCorresponde() + "', " );//idCorresponde viene del combo de tipoIngreso
				    sb.append( + ingreso.getLongReferencia() + ", " + ingreso.getOrdenEmpresa() + ", " + ingreso.getOrdenCliente() + ", " );
				    sb.append( + ingreso.getOrdenCodigo() + ", " + ingreso.getOrdenSubcodigo() + ", " + ingreso.getOrdenDivision() + ", " );
				    sb.append( + ingreso.getOrdenConst1() + ", " + ingreso.getOrdenConst2() + ", " + ingreso.getOrdenConst3() + ", " );
				    sb.append( + ingreso.getLongEmpresa() + ", " + ingreso.getLongCliente() + ", " + ingreso.getLongCodigo() + ", " );
				    sb.append( + ingreso.getLongSubcodigo() + ", " + ingreso.getLongDivision() + ", '" );
				    sb.append( ingreso.getConst1() + "', '" + ingreso.getConst2() + "', '" );
				    sb.append( ingreso.getConst3() + "', 'b10', " );
				    
				    if( ingreso.getIdCorresponde().equals("C") )
				    {
				    	sb.append( + ingreso.getIdRubro() + ", '" + ingreso.getIdChequeraDestino() + "', " ); //rubro viene del combo rubro
				        if( pbCambiaCtas ) //pbCambiaCtas viene del boton aceptar
				        	sb.append( "'S', 'S'," );
				        else
				        	sb.append( "'N', 'N'," );
				    }   
				    else
				    	sb.append( ingreso.getIdRubro() + ", '', 'N', 'N'," ); //combo Rubro
					    sb.append( "'" + ingreso.getDestinoEmpresa() + "'," ); // combo EmpresaD
					    sb.append( "'" + ingreso.getDestinoConst1() + "'," );// todos estos valores vienen de los valores que forman la referencia
					    sb.append( "'" + ingreso.getDestinoConst2() + "'," );
					    sb.append( "'" + ingreso.getDestinoConst3() + "'," );
					    sb.append( ingreso.getOrdenVar1() + "," );
					    sb.append( ingreso.getOrdenVar2() + "," );
					    sb.append( ingreso.getOrdenVar3() + "," );
					    sb.append( ingreso.getLongVar1() + "," );
					    sb.append( ingreso.getLongVar2() + "," );
					    sb.append( ingreso.getLongVar3() + "," );
					    sb.append( "'" + ingreso.getDestinoVar1() + "'," );
					    sb.append( "'" + ingreso.getDestinoVar2() + "'," );
					    sb.append( "'" + ingreso.getDestinoVar3() + "'," );
					    sb.append( "'" + ingreso.getBPredeterminada() + "' " );// check box predeterminada
					    sb.append( ")" );
					    //System.out.println(sb.toString());
						resIn = jdbcTemplate.update(sb.toString()
						);
			    	} catch(Exception e){
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
						+ "P:Ingresos, C:CatBancoDao, M:insertarArmaIngreso");
						}
				return resIn;
		    }
			    
		    else 
		    	return res;
		}
	
	
	/**metodo para modificar el armado de referencia
	 * FunSQLActualizaArmaIngreso
	 * */
	public int modificarArmaIngreso(CatArmaIngresoDto ingreso, boolean pbCambiaCtas) throws Exception{
		StringBuffer sb = new StringBuffer();
		int res = 0;
		try{
		    sb.append( " UPDATE cat_arma_ingreso SET " );
		    sb.append( "\n long_referencia = " + ingreso.getLongReferencia() + "," );
		    sb.append( "\n orden_empresa = " + ingreso.getOrdenEmpresa() + "," );
		    sb.append( "\n orden_cliente = " + ingreso.getOrdenCliente() + "," );
		    sb.append( "\n orden_codigo = " + ingreso.getOrdenCodigo() + "," );
		    sb.append( "\n orden_subcodigo = " + ingreso.getOrdenSubcodigo() + "," );
		    sb.append( "\n orden_division = " + ingreso.getOrdenDivision() + "," );
		    sb.append( "\n orden_const1 = " + ingreso.getOrdenConst1() + "," );
		    sb.append( "\n orden_const2 = " + ingreso.getOrdenConst2() + "," );
		    sb.append( "\n orden_const3 = " + ingreso.getOrdenConst3() + "," );
		    
		    sb.append( "\n long_empresa = " + ingreso.getLongEmpresa() + "," );
		    sb.append( "\n long_cliente = " + ingreso.getLongCliente() + "," );
		    sb.append( "\n long_codigo = " + ingreso.getLongCodigo() + "," );
		    sb.append( "\n long_subcodigo = " + ingreso.getLongSubcodigo() + "," );
		    sb.append( "\n long_division = " + ingreso.getLongDivision() + "," );
		    sb.append( "\n const1 = '" + ingreso.getConst1() + "'," );
		    sb.append( "\n const2 = '" + ingreso.getConst2() + "'," );
		    sb.append( "\n const3 = '" + ingreso.getConst3() + "'," );
		   
		    sb.append( "\n base_calculo = 'b10'," );
		    if( ingreso.getIdCorresponde().equals("C") )
		    	{
		    		sb.append( "\n id_rubro =" + ingreso.getIdRubro() + "," );
		    		sb.append( "\n id_chequera_destino = '" + ingreso.getIdChequeraDestino() + "'," );
		    		if( pbCambiaCtas )
		    		{
			        	sb.append( "\n b_cambia_origen = 'S'," );
			        	sb.append( "\n b_cambia_destino = 'S'," );
		    		}
			        else
			        {
		        		sb.append( "\n b_cambia_origen = 'N'," );
		        		sb.append( "\n b_cambia_destino = 'N'," );
			        }
		    	}
		    else
		    {
		    	sb.append( "\n id_rubro =" + ingreso.getIdRubro() + "," );
		    	sb.append( "\n id_chequera_destino = ''," );
		    	sb.append( "\n b_cambia_origen = 'N'," );
		    	sb.append( "\n b_cambia_destino = 'N'," );
		    }
		    sb.append( "\n destino_empresa = '" + ingreso.getDestinoEmpresa() + "'," );
		    sb.append( "\n destino_const1 = '" + ingreso.getDestinoConst1() + "'," );
		    sb.append( "\n destino_const2 = '" + ingreso.getDestinoConst2() + "'," );
		    sb.append( "\n destino_const3 = '" + ingreso.getDestinoConst3() + "'," );
		    sb.append( "\n orden_var1 = " + ingreso.getOrdenVar1() + "," );
		    sb.append( "\n orden_var2 = " + ingreso.getOrdenVar2() + "," );
		    sb.append( "\n orden_var3 = " + ingreso.getOrdenVar3() + "," );
		    sb.append( "\n long_var1 = " + ingreso.getLongVar1() + "," );
		    sb.append( "\n long_var2 = " + ingreso.getLongVar2() + "," );
		    sb.append( "\n long_var3 = " + ingreso.getLongVar3() + "," );
		    sb.append( "\n destino_var1 = '" + ingreso.getDestinoVar1() + "'," );
		    sb.append( "\n destino_var2 = '" + ingreso.getDestinoVar2() + "'," );
		    sb.append( "\n destino_var3 = '" + ingreso.getDestinoVar3() + "'," );
		    sb.append( "\n b_predeterminada = '" + ingreso.getBPredeterminada() + "' " );
		    
		    sb.append( "\n WHERE no_empresa = " + ingreso.getNoEmpresa() + " AND " );
		    sb.append( "\n id_banco = " + ingreso.getIdBanco() + " AND " );
		    sb.append( "\n id_chequera = '" + ingreso.getIdChequera() + "' AND " );
		    sb.append( "\n id_corresponde = '" + ingreso.getIdCorresponde() + "'" );
		    
			//logger.info("update: "+sb.toString());
			res = jdbcTemplate.update(sb.toString());//,
//			new Object[]{ingreso.getNoEmpresa(), ingreso.getIdBanco(), ingreso.getIdChequera(), ingreso.getIdCorresponde(), ingreso.getLongReferencia(),
//						ingreso.getOrdenEmpresa(), ingreso.getOrdenCliente(), ingreso.getOrdenCodigo(), ingreso.getOrdenSubcodigo(),ingreso.getOrdenDivision(),
//						ingreso.getOrdenConst1(), ingreso.getOrdenConst2(), ingreso.getOrdenConst3(), ingreso.getLongEmpresa(), ingreso.getLongCliente(),
//						ingreso.getLongCodigo(), ingreso.getLongSubcodigo(), ingreso.getLongDivision(), ingreso.getConst1(), ingreso.getConst2(),
//						ingreso.getConst3(), ingreso.getBaseCalculo(), ingreso.getIdRubro(), ingreso.getIdChequeraDestino(), ingreso.getBCambiaOrigen(),
//						ingreso.getBCambiaDestino(), ingreso.getDestinoEmpresa(), ingreso.getDestinoConst1(), ingreso.getDestinoConst2(), ingreso.getDestinoConst3(),
//						ingreso.getOrdenVar1(), ingreso.getOrdenVar2(), ingreso.getOrdenVar3(), ingreso.getDestinoVar1(), ingreso.getDestinoVar2(),
//						ingreso.getDestinoVar3(), ingreso.getBPredeterminada()}
//			);
			}catch(Exception e){
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
						+ "P:Ingresos, C:CatBancoDao, M:modificarArmaIngreso");
					}
			//logger.info("update dao : "+res);
	return res;
}
	
	public int eliminarArmaIngreso(CatArmaIngresoDto ingreso){
		int res =-1;
		StringBuffer sb = new StringBuffer();
		try{
			sb.append( " DELETE FROM  cat_arma_ingreso " );
			sb.append( " WHERE no_empresa = " + ingreso.getNoEmpresa() );
			sb.append( " AND id_banco = " + ingreso.getIdBanco() );
			sb.append( " AND id_chequera = '" + ingreso.getIdChequera() + "'" );
			sb.append( " AND id_corresponde = '" + ingreso.getIdCorresponde() + "'" );
			//logger.info(sb.toString());
			res = jdbcTemplate.update(sb.toString());
    
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Ingresos, C:CatBancoDao, M:eliminarArmaIngreso");
					}
		return res;
	}

	
	//getters && setters
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

}
