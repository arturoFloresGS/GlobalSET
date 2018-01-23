package com.webset.set.bancaelectronica.dao;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;

import com.webset.set.bancaelectronica.dto.ArchMovtoBancaeDto;
import com.webset.set.bancaelectronica.dto.CatCtaBancoDto;
import com.webset.set.bancaelectronica.dto.EquivaleConceptoDto;
import com.webset.set.bancaelectronica.dto.MovtoBancaEDto;
import com.webset.set.bancaelectronica.dto.ParametroBancomerDto;
import com.webset.set.bancaelectronica.dto.ParametroBuscarEmpresaDto;
import com.webset.set.bancaelectronica.dto.ParametroLayoutDto;
import com.webset.set.impresion.business.ImpresionBusinessImpl;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.ConstantesSet;

/**
 * 
 * @author Sergio Vaca
 *
 */
public class AdministradorArchivosDao {
	//private static Logger log = Logger.getLogger(AdministradorArchivosDao.class);
	private JdbcTemplate jdbcTemplate;
	private ConsultasGenerales gral;
	private Funciones funciones = new Funciones();
	private StringBuffer sb = null;
	private Bitacora bitacora = new Bitacora();
	private static Logger logger = Logger.getLogger(AdministradorArchivosDao.class);

	
	//Cristian 
	DateFormat fecInicio = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	DateFormat fecFinal = new SimpleDateFormat("dd/MM/yyyy");

	/**
	 * 
	 * @param idBanco
	 * @return List<String>
	 */
	@SuppressWarnings("unchecked")
	public List<String> obtenerCasaCambio(int idBanco) {
		sb = new StringBuffer();
		sb.append(" SELECT transfer_casa_cambio ");
		sb.append(" FROM cat_banco ");
		sb.append(" WHERE id_banco = " + idBanco);
		try {
			List<String> datos = jdbcTemplate.query(sb.toString(), new RowMapper() {
						public String mapRow(ResultSet rs, int idx)	throws SQLException {
							return rs.getString("transfer_casa_cambio");
						}
					});
			return datos;
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BE, C:AdministradorArchivosDao, M:obtenerCasaCambio");
			return null;
		}
	}

	/**
	 * Consulta la tabla del configura_set
	 * 
	 * @param indice
	 * @return
	 */
	public String consultarConfiguraSet(int indice) {
		gral = new ConsultasGenerales(jdbcTemplate);
		try {
			return gral.consultarConfiguraSet(indice);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()
							+ " "
							+ e.toString()
							+ "P:BE, C:AdministradorArchivosDao, M:consultarConfiguraSet");
			return null;
		}
	}

	/**
	 * Public Function FunSQLSelectCat_banco() As ADODB.Recordset Selecciona
	 * b_saldo_banco segun el datos si es mayor a cero busca segun el id sino
	 * saca los saldos de todos los bancos
	 * 
	 * @param id
	 * @return List<ComunDto>
	 */
	@SuppressWarnings("unchecked")
	public List<ComunDto> seleccionarSaldoBanco(int id) {
		sb = new StringBuffer();
		sb.append(" SELECT id_banco, b_saldo_banco ");
		sb.append(" FROM cat_banco ");
		sb.append(" WHERE b_banca_elect IN ('I','A') ");
		if (id > 0)
			sb.append(" AND id_banco = " + id);
		else
			sb.append(" ORDER BY id_banco ");
		
		try {
			List<ComunDto> datos = jdbcTemplate.query(sb.toString(),
					new RowMapper() {
						public ComunDto mapRow(ResultSet rs, int idx)throws SQLException {
							ComunDto dato = new ComunDto();
							dato.setIdBanco(rs.getInt("id_banco"));
							dato.setDescripcion(rs.getString("b_saldo_banco"));
							return dato;
						}
					});
			return datos;
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()
							+ " "
							+ e.toString()
							+ "P:BE, C:AdministradorArchivosDao, M:seleccionarSaldoBanco");
			return null;
		}
	}

	/**
	 * @param dto
	 * @return List<CatCtaBancoDto>
	 * 
	 * Busca el numero de empresa, id del banco el id de chequera Segun los
	 * datos recibidos en el dto (ParametroBuscarEmpresaDto dto) y retorna
	 * getNoEmpresa(); getIdBanco(); getIdChequera();
	 * Public Function FunSQLBuscar_empresa(ByVal psChequera As String, Optional
	 * ByVal pi_banco As Integer, _ Optional pbSwiftMT940_MN As Boolean,
	 * Optional movDiarios As Boolean) As ADODB.Recordset formAdmor
	 */
	@SuppressWarnings("unchecked")
	public List<CatCtaBancoDto> buscarEmpresa(ParametroBuscarEmpresaDto dto) {
		sb = new StringBuffer();
		sb.append(" SELECT no_empresa, id_banco, id_chequera ");
		sb.append("\n FROM cat_cta_banco ");
		if (dto.isMovDiarios())
			sb.append("\n WHERE id_clabe = '" + dto.getPsChequera() + "' ");
		else if (dto.isPbSwiftMT940MN())
			sb.append("\n WHERE id_chequera like '%" + dto.getPsChequera()
					+ "' ");
		else
			sb.append("\n WHERE id_chequera = '" + dto.getPsChequera() + "'");
		if (dto.getPiBanco() != 0)
			sb.append("\n	AND id_banco = " + dto.getPiBanco());
		//logger.info("buscarEmpresa" + sb.toString());
		
		
		try {
			List<CatCtaBancoDto> datos = jdbcTemplate.query(sb.toString(),
					new RowMapper() {
						public CatCtaBancoDto mapRow(ResultSet rs, int idx)
								throws SQLException {
							CatCtaBancoDto dato = new CatCtaBancoDto();
							dato.setNoEmpresa(rs.getInt("no_empresa"));
							dato.setIdBanco(rs.getInt("id_banco"));
							dato.setIdChequera(rs.getString("id_chequera"));
							return dato;
						}
					});
			return datos;
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BE, C:AdministradorArchivosDao, M:buscarEmpresa");
			return null;
		}

	}

	/**
	 * 
	 * @param dto
	 * @return int
	 * 
	 * Metodo que busca en la tabla usuario_empresa segun el id_de_usuario y
	 * no_empresa
	 * 
	 * Public Function FunSQLBuscaEmpresaUsu(ByVal piUsuario As Integer, ByVal
	 * piNoEmpresa As Integer) As ADODB.Recordset
	 */
	@SuppressWarnings("unchecked")
	public int buscarEmpresaUsu(ParametroBuscarEmpresaDto dto) {
		sb = new StringBuffer();
		sb.append(" SELECT no_empresa ");
		sb.append("\n FROM usuario_empresa ");
		sb.append("\n WHERE no_usuario = " + dto.getIdUsuario());
		sb.append("\n 	AND no_empresa = " + dto.getNoEmpresa());
		try {
			List<ParametroBuscarEmpresaDto> datos = jdbcTemplate.query(sb
					.toString(), new RowMapper() {
				public ParametroBuscarEmpresaDto mapRow(ResultSet rs, int idx)
						throws SQLException {
					ParametroBuscarEmpresaDto dato = new ParametroBuscarEmpresaDto();
					dato.setNoEmpresa(rs.getInt("no_empresa"));
					return dato;
				}
			});
			return datos.get(0).getNoEmpresa();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BE, C:AdministradorArchivosDao, M:buscarEmpresaUsu");
			return -1;
		}
	}

	/**
	 * 
	 * @param dto
	 * @return List<MovtoBancaEDto>
	 * 
	 * Public Function FunSQLSelectMovto_banca_e(ByVal psFolio_banco As String,
	 * ByVal psconcepto As String, _ Seleciona de la tabla movto_banca_e
	 * movto_arch, secuencia, archivo
	 */
	@SuppressWarnings("unchecked")
	public List<MovtoBancaEDto> selecionarMovtoBancaE(ParametroLayoutDto dto) {
		sb = new StringBuffer();
		String psSQLAdicional;
		
		if (dto.getPdSaldo() != 0)
			psSQLAdicional = "\n 	AND saldo_bancario = " + dto.getPdSaldoBancario();
		else
			psSQLAdicional = "";
		
		if (dto.getContadorRenglon() > 0) {
			psSQLAdicional = psSQLAdicional + "\n	AND (no_linea_arch = " + dto.getContadorRenglon();
			psSQLAdicional = psSQLAdicional + " OR no_linea_arch IS NULL) ";
		}
		if (dto.getPlNoLineaArchivo() > 0
				&& !dto.getPsConcepto().equals("MISMO BANCO BANCOMER")
				&& !dto.getPsConcepto().equals("MISMO BANCO BANCOMER2"))
			psSQLAdicional += "\n 	AND no_linea_arch = "
					+ dto.getPlNoLineaArchivo();
		
		if (dto.getCargoAbono() != null && !dto.getCargoAbono().equals(""))
			psSQLAdicional += "\n	AND cargo_abono = '" + dto.getCargoAbono()
					+ "' ";
		// Select Case gsDBM
		// Case "SQL SERVER", "SYBASE":
		sb.append(" SELECT movto_arch, secuencia, archivo ");
		sb.append("\n FROM movto_banca_e ");
		sb.append("\n WHERE movto_arch = 1 ");
		sb.append("\n 	AND no_empresa = " + dto.getPlNoEmpresa());
		
		if (dto.getPsConcepto().equals("MISMO BANCO BANCOMER")) {
			if (dto.getPsFolioBanco().substring(0, 1).equals("'"))
				sb.append("\n 	AND folio_banco in (" + dto.getPsFolioBanco() + ") AND ( archivo like 'md%' OR archivo like 'SH%') ");
			else
				sb.append("\n 	AND folio_banco = '" + dto.getPsFolioBanco() + "' AND ( archivo like 'md%' OR archivo like 'SH%') ");
		}
		if (!dto.getPsConcepto().equals("MISMO BANCO BANCOMER")
				&& !dto.getPsConcepto().equals("MISMO BANCO BANCOMER2")
				&& dto.getPiBanco() == 12)
			sb.append("\n 	AND concepto = '" + dto.getPsConcepto() + "' AND ( archivo like 'md%' OR archivo like 'SH%') ");
		else if (dto.getPsConcepto().equals("MISMO BANCO BANCOMER2"))
			sb.append("\n 	AND referencia = '" + dto.getPsReferencia() + "'");
		sb.append("\n 	AND id_chequera = '" + dto.getPsIdChequera() + "'");
		sb.append("\n 	AND CONVERT(datetime, fec_valor, 103) = '"
				+ funciones.ponerFechaSola(dto.getPsFecValor()) + "'");
		sb.append("\n 	AND importe = " + dto.getPdImporte());
		sb.append(psSQLAdicional);
		sb.append("\n UNION ALl ");
		sb.append("\n SELECT movto_arch, secuencia, archivo");
		sb.append("\n FROM hist_movto_banca_e ");
		sb.append("\n WHERE movto_arch = 1 ");
		sb.append("\n 	AND no_empresa = " + dto.getPlNoEmpresa());
		if (dto.getPsConcepto().equals("MISMO BANCO BANCOMER")) {
			if (dto.getPsFolioBanco().substring(0, 1).equals("'"))
				sb.append("\n 	AND  folio_banco IN (" + dto.getPsFolioBanco()
						+ ") AND ( archivo like 'md%' OR archivo like 'SH%') ");
			else
				sb.append("\n 	AND folio_banco = '" + dto.getPsFolioBanco()
						+ "' AND ( archivo like 'md%' OR archivo like 'SH%') ");
		}
		if (!dto.getPsConcepto().equals("MISMO BANCO BANCOMER")
				&& !dto.getPsConcepto().equals("MISMO BANCO BANCOMER2")
				&& dto.getPiBanco() == 12)
			sb.append("\n 	AND  concepto = '" + dto.getPsConcepto()
					+ "' AND ( archivo like 'md%' or archivo like 'SH%') ");
		else if (dto.getPsConcepto().equals("MISMO BANCO BANCOMER2"))
			sb.append("\n 	AND  referencia = '" + dto.getPsReferencia() + "'");
		sb.append("\n 	AND id_chequera = '" + dto.getPsIdChequera() + "'");
		sb.append("\n 	AND CONVERT(datetime, fec_valor, 103) = '"
				+ funciones.ponerFechaSola(dto.getPsFecValor()) + "'");
		sb.append("\n 	AND importe = " + dto.getPdImporte());
		sb.append(psSQLAdicional);
		sb.append("\n ORDER BY archivo ");
		
		try {
			List<MovtoBancaEDto> datos = jdbcTemplate.query(sb.toString(),
					new RowMapper() {
						public MovtoBancaEDto mapRow(ResultSet rs, int idx)
								throws SQLException {
							MovtoBancaEDto dato = new MovtoBancaEDto();
							dato.setMovtoArch(rs.getInt("movto_arch"));
							dato.setSecuencia(rs.getInt("secuencia"));
							dato.setArchivo(rs.getString("archivo"));
							return dato;
						}
					});
			return datos;
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()
							+ " "
							+ e.toString()
							+ "P:BE, C:AdministradorArchivosDao, M:seleccionarMovtoBancaE");
			return null;
		}

	}

	/**
	 * 
	 * @param dto
	 * @return List<MovtoBancaEDto>
	 * 
	 * Public Function FunSQLSelectMovto_banca_e(ByVal psFolio_banco As String,
	 * ByVal psconcepto As String, _ Seleciona de la tabla movto_banca_e
	 * movto_arch, secuencia, archivo
	 */
	@SuppressWarnings("unchecked")
	public List<MovtoBancaEDto> selecionarMovtoBancaE(MovtoBancaEDto dto){
		StringBuffer sb = new StringBuffer();
		String psSQLAdicional;
		if(dto.getSaldoBancario()!= 0)
			psSQLAdicional = "\n AND saldo_bancario = " + dto.getSaldoBancario();
		else
			psSQLAdicional = "";
		if(dto.getNoLineaArch() > 0){
			psSQLAdicional = psSQLAdicional + "\n AND (no_linea_arch = " + dto.getNoLineaArch();
			psSQLAdicional = psSQLAdicional + " OR no_linea_arch IS NULL) ";
		}
		if(dto.getNoLineaArch() > 0 && !dto.getConcepto().equals("MISMO BANCO BANCOMER") && !dto.getConcepto().equals("MISMO BANCO BANCOMER2"))
			psSQLAdicional+="\n AND no_linea_arch = " + dto.getNoLineaArch();
		if(dto.getCargoAbono()!=null && !dto.getCargoAbono().equals(""))
			psSQLAdicional+= "\n AND cargo_abono = '" + dto.getCargoAbono() + "' ";
		// Select Case gsDBM
		// Case "SQL SERVER", "SYBASE":
		sb.append(" SELECT movto_arch, secuencia, archivo ");
		sb.append("\n FROM movto_banca_e ");
		sb.append("\n WHERE movto_arch = 1 ");
		sb.append("\n AND no_empresa = " + dto.getNoEmpresa());
		if(dto.getConcepto().equals("MISMO BANCO BANCOMER")){
			if(dto.getFolioBanco().substring(0, 1).equals("'"))
				sb.append("\n AND folio_banco in (" + dto.getFolioBanco() + ") AND ( archivo like 'md%' OR archivo like 'SH%') ");
			else
				sb.append("\n AND folio_banco = '" + dto.getFolioBanco() + "' AND ( archivo like 'md%' OR archivo like 'SH%') ");
		}
		if(!dto.getConcepto().equals("MISMO BANCO BANCOMER") && !dto.getConcepto().equals("MISMO BANCO BANCOMER2") && dto.getIdBanco() == 12)
			sb.append("\n AND concepto = '" + dto.getConcepto() + "' AND ( archivo like 'md%' OR archivo like 'SH%') ");
		else if(dto.getConcepto().equals("MISMO BANCO BANCOMER2"))
			sb.append("\n AND referencia = '" + dto.getReferencia() + "'");
		sb.append("\n AND id_chequera = '" + dto.getIdChequera() + "'");
		sb.append("\n AND CONVERT(datetime, fec_valor, 103) = '" + funciones.ponerFechaSola(dto.getFecValor()) + "'");
		sb.append("\n AND importe = " + dto.getImporte());
		sb.append(psSQLAdicional);
		sb.append("\n UNION ALl ");
		sb.append("\n SELECT movto_arch, secuencia, archivo");
		sb.append("\n FROM hist_movto_banca_e ");
		sb.append("\n WHERE movto_arch = 1 ");
		sb.append("\n AND no_empresa = " + dto.getNoEmpresa());
		if(dto.getConcepto().equals("MISMO BANCO BANCOMER")){
			if(dto.getFolioBanco().substring(0, 1).equals("'"))
				sb.append("\n AND folio_banco IN (" + dto.getFolioBanco() + ") AND ( archivo like 'md%' OR archivo like 'SH%') ");
			else
				sb.append("\n AND folio_banco = '" + dto.getFolioBanco() + "' AND ( archivo like 'md%' OR archivo like 'SH%') ");
		}
		if(!dto.getConcepto().equals("MISMO BANCO BANCOMER") && !dto.getConcepto().equals("MISMO BANCO BANCOMER2") && dto.getIdBanco() == 12)
			sb.append("\n AND concepto = '" + dto.getConcepto() + "' AND ( archivo like 'md%' or archivo like 'SH%') ");
		else if(dto.getConcepto().equals("MISMO BANCO BANCOMER2"))
			sb.append("\n AND referencia = '" + dto.getReferencia() + "'");
		sb.append("\n AND id_chequera = '" + dto.getIdChequera() + "'");
		sb.append("\n AND CONVERT(datetime, fec_valor, 103) = '" + funciones.ponerFechaSola(dto.getFecValor()) + "'");
		sb.append("\n AND importe = " + dto.getImporte());
		sb.append(psSQLAdicional);
		sb.append("\n ORDER BY archivo ");
		//logger.info("selecionarMovtoBancaE" + sb.toString());
		try{
			List<MovtoBancaEDto> datos = jdbcTemplate.query(sb.toString(), new RowMapper(){
				public MovtoBancaEDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovtoBancaEDto dato = new MovtoBancaEDto();
					dato.setMovtoArch(rs.getInt("movto_arch"));
					dato.setSecuencia(rs.getInt("secuencia"));
					dato.setArchivo(rs.getString("archivo"));
					return dato;
				}
			});
			return datos;
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BE, C:AdministradorArchivosDao, M:selecionarMovtoBancaE");
			return null;
		}
	}

	/**
	 * 
	 * @param tipoFolio
	 * @return int
	 * 
	 * Selecciona el numero de folio de la tabla folio
	 */
	public int seleccionarFolioReal(String tipoFolio) {
		sb = new StringBuffer();
		sb.append(" SELECT num_folio ");
		sb.append("\n FROM folio ");
		sb.append("\n WHERE tipo_folio = '" + tipoFolio + "'");
		try {
			return jdbcTemplate.queryForInt(sb.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()
							+ " "
							+ e.toString()
							+ "P:BE, C:AdministradorArchivosDao, M:seleccionarFolioReal");
			return -1;
		}
	}

	/**
	 * 
	 * @param tipoFolio
	 * @return int
	 * 
	 * Actualiza el numero de folio de la tabla folio
	 */
	public int actualizarFolioReal(String tipoFolio) {
		sb = new StringBuffer();
		sb.append(" UPDATE folio ");
		sb.append("\n SET num_folio = num_folio + 1  ");
		sb.append("\n WHERE tipo_folio = '" + tipoFolio + "'");
		try {
			return jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()
							+ " "
							+ e.toString()
							+ "P:BE, C:AdministradorArchivosDao, M:actualizarFolioReal");
			return -1;
		}
	}

	/**
	 * 
	 * @param dto
	 * @return int
	 * 
	 * Inserta en movto_banca_e /*Public Function FunSQLInsert12(ByVal
	 * plNo_empresa As Long, ByVal psId_chequera As String, _ ByVal psSecuencia
	 * As String, ByVal psFec_Valor As String, _ ByVal psFolio_banco As String,
	 * ByVal psreferencia As String, _ ByVal psB_salvo_buen_cobro As String,
	 * ByVal psconcepto As String, _ ByVal psFec_alta As String, ByVal
	 * psCargo_Abono As String, _ ByVal psUsuario_alta As String, ByVal
	 * psdescripcion As String, _ ByVal psArchivo As String, ByVal psSucursal As
	 * String, _ ByVal pdImporte As Double, Optional pdSaldo_bancario As Double, _
	 * Optional ByVal psCve_concepto As String) As Long
	 */
	public int insertarMovtoBancaEHSBC(ParametroLayoutDto dto) {
		int res = 0;
		sb = new StringBuffer();
		sb.append(" INSERT INTO movto_banca_e ");
		sb.append("\n	(no_empresa, id_banco, id_chequera, secuencia, fec_valor, ");
		sb.append("\n 	sucursal, folio_banco, referencia, importe, b_salvo_buen_cobro,");
		sb.append("\n 	concepto, id_estatus_trasp, fec_alta, b_trasp_banco, ");
		sb.append("\n 	b_trasp_conta, movto_arch, cargo_abono, usuario_alta, descripcion, archivo, ");
		sb.append("\n saldo_bancario ");
		if (dto.getPsCveConcepto() != null)
			sb.append(", id_cve_concepto");
		if (dto.getContadorRenglon() > 0)
			sb.append(", no_linea_arch ");
		sb.append(", id_estatus_cancela ) ");
		sb.append("\n VALUES(" + dto.getPlNoEmpresa() + ", 21, '" + dto.getPsIdChequera() + "', ");
		sb.append(dto.getPsSecuencia() + ", '" + dto.getPsFecValor() + "', '");
		sb.append(dto.getPsSucursal() + "', '" + dto.getPsFolioBanco());
		sb.append("',\n		'" + dto.getPsReferencia() + "', ");
		sb.append(dto.getPdImporte());
		sb.append(", '" + dto.getPsBSalvoBuenCobro() + "', '" + dto.getPsConcepto());
		sb.append("',\n		'N', '" + dto.getPsFecAlta() + "', 'N', 'N', 1, '" + dto.getPsCargoAbono() + "', ");
		sb.append(dto.getPsUsuarioAlta() + ", '" + dto.getPsDescripcion() + "', '" + dto.getPsArchivo() + "', ");
		sb.append(dto.getPdSaldoBancario());
		if (dto.getPsCveConcepto() != null)
			sb.append(", '" + dto.getPsCveConcepto() + "' ");
		if (dto.getContadorRenglon() > 0)
			sb.append(", " + dto.getContadorRenglon());
		sb.append(", 0 )");
		try {
			res = jdbcTemplate.update(sb.toString());
			if (dto.getPsCargoAbono().equals("S")) {
				sb = null;
				sb = new StringBuffer();
				Funciones funcion = new Funciones();
				if (funcion.ponerFechaSola(dto.getPsFecValor()).equals(
						funcion.ponerFechaSola(dto.getPsFecAlta())))
					sb.append(" UPDATE cat_cta_banco ");
				else
					sb.append(" UPDATE hist_cat_cta_banco ");
				sb.append("\n 	SET saldo_banco_ini = " + dto.getPdImporte());
				sb.append("\n WHERE no_empresa = " + dto.getPlNoEmpresa());
				sb.append("\n	AND id_banco = 21 ");
				sb.append("\n	AND id_chequera = '" + dto.getPsIdChequera() + "'");
				if (!funcion.ponerFechaSola(dto.getPsFecValor()).equals(funcion.ponerFechaSola(dto.getPsFecAlta())))
				sb.append("\n	AND fec_valor = convert(datetime,'" + funcion.ponerFecha(dto.getPsFecValor())+"',103)");
				res = jdbcTemplate.update(sb.toString());
			}
			return res;
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()
							+ " "
							+ e.toString()
							+ "P:BE, C:AdministradorArchivosDao, M:InsertarMovtoBancaE");
			return -1;
		}
		

	}
	/**
	 * 
	 * @param dto
	 * @return int 
	 * 
	 * Public Function FunSQLInserta_movto_banca_e(ByVal lempresa As Long, ByVal iBanco As Integer, _
     *	ByVal sChequera As String, ByVal sSecuencia As String, ByVal sFec_valor As String, _
     *	ByVal sSucursal As String, ByVal sFolio_banco As String, ByVal sReferencia As String, _
	 *  ByVal sCargo_abono As String, ByVal dImporte As Double, ByVal sB_salvo_buen_cobro As String, _
	 *  ByVal sConcepto As String, ByVal lUsuario_alta As Long, sDescripcion As String, _
	 *  ByVal sArchivo As String, ByVal psFecha_hoy As String, _
	 *  Optional psObservacion As String, Optional psCve_concepto As String, _
	 *  Optional pdSaldoBancario As Double, Optional ByVal psEstatus As String, _
	 *  Optional plNoLinea_archivo As Long = 0) As Long
	 */
	public int insertarMovtoBancaE(ParametroLayoutDto dto){
		sb = new StringBuffer();
		if(dto.getPsEstatus()==null || dto.getPsEstatus().equals(""))
			dto.setPsEstatus("N");
		sb.append(" INSERT INTO movto_banca_e ( ");
		sb.append(" no_empresa, id_banco, id_chequera, secuencia, fec_valor, ");
		sb.append("\n	sucursal, folio_banco, referencia, cargo_abono, importe, b_salvo_buen_cobro, ");
		sb.append("\n	concepto, id_estatus_trasp, fec_alta, b_trasp_banco, b_trasp_conta, movto_arch, ");
		sb.append("\n 	usuario_alta, descripcion, archivo");
		if(dto.getPsObservacion()!=null)
			sb.append(", observacion");
		if(dto.getPsCveConcepto()!=null) 
			sb.append(", id_cve_concepto");
		if(dto.getPdSaldoBancario()>0)
			sb.append(", saldo_bancario");
		if(dto.getPlNoLineaArchivo() > 0 )
			sb.append(", no_linea_arch");
		sb.append(" ) ");
		sb.append(" VALUES (" + dto.getPlNoEmpresa() + ", " + dto.getPiBanco() + ", '" + dto.getPsIdChequera() + "', ");
		sb.append("\n	" + dto.getPsSecuencia() + ", '" + dto.getPsFecValor() + "', '" + dto.getPsSucursal() + "', '" + dto.getPsFolioBanco() + "', ");
		sb.append("\n	'" + dto.getPsReferencia() + "', '" + dto.getPsCargoAbono() + "', " + dto.getPdImporte() + ", ");
		sb.append("\n	'" + dto.getPsBSalvoBuenCobro() + "', '" + dto.getPsConcepto() + "', '" + dto.getPsEstatus() + "', ");
		sb.append("\n	'" + dto.getPsFecAlta() + "', 'N', 'N', 1, " + dto.getPsUsuarioAlta() + ", ");
		sb.append("\n	'" + dto.getPsDescripcion() + "', '" + dto.getPsArchivo() + "'");
		if(dto.getPsObservacion()!=null)
			sb.append(", '" + dto.getPsObservacion() + "' ");
        if(dto.getPsCveConcepto()!=null) 
        	sb.append(", '" + dto.getPsCveConcepto() + "' ");
        if(dto.getPdSaldoBancario()>0)
        	sb.append(", " + dto.getPdSaldoBancario());
        if(dto.getPlNoLineaArchivo() > 0 )
        	sb.append("," + dto.getPlNoLineaArchivo());
       	sb.append(" ) ");
       	try{
       		return jdbcTemplate.update(sb.toString());
       	}catch(Exception e){
       		bitacora.insertarRegistro(new Date().toString()
					+ " "
					+ e.toString()
					+ "P:BE, C:AdministradorArchivosDao, M:InsertarMovtoBancaE");
       		return -1;
       	}
	}
	
	/**
	 * 
	 * @param dto
	 * @return int 
	 * 
	 * Public Function FunSQLInsertatmpMovtoEdoCta(ByVal psChequera As String, ByVal psNoCheque As String, _
     *                                    ByVal pdImporte As Double, ByVal psconcepto As String) As Long
	 */
	public int insertarTmpMovtoEdoCta(ParametroLayoutDto dto){
		sb = new StringBuffer();
	    sb.append(" INSERT INTO tmp_MovtoEdoCta ");
		sb.append(" VALUES ( ");
		sb.append("'" + dto.getPsIdChequera() + "','" + dto.getPsFolioBanco() + "', " + dto.getPdImporte() + ", '" + dto.getPsConcepto() + "' )");
		try{
			return jdbcTemplate.update(sb.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()
					+ " "
					+ e.toString()
					+ "P:BE, C:AdministradorArchivosDao, M:InsertarTmpMovtoEdoCta");
       		return -1;
		}
	}
	/**
	 * 
	 * @param psArchivos
	 * @param piBanco
	 * @return int
	 * 
	 * Actualiza la fecha_banca de cat_cta_banco
	 * 
	 * Sub actualizar_fecha_banca(ByVal psArchivos As String, piBanco As
	 * Integer) VB
	 * 
	 * FunSQLUpdateCatCtaBanco(ByVal psFecha As String,
	 * 		ByVal psArchivos As String, ByVal piBanco As Integer) As Long
	 */
	public int actualizarFechaBanca(String psArchivos, int piBanco) {
		String fecha = null;
		sb = new StringBuffer();
		fecha = funciones.ponerFecha(obtenerFechaHoy());
		sb.append(" UPDATE cat_cta_banco ");
		sb.append(" SET fecha_banca = '" + fecha + "' ");
		sb.append("\n WHERE ");
		sb.append(" id_chequera IN ");
		sb.append("\n     	(SELECT DISTINCT id_chequera FROM movto_banca_e ");
		sb.append("\n             WHERE archivo IN('" + psArchivos + "') )");
		sb.append("\n	AND id_banco = " + piBanco);
		try {
			return jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()
							+ " "
							+ e.toString()
							+ "P:BE, C:AdministradorArchivosDao, M: ActualizarFechaBanca");
			return -1;
		}
	}

	/**
	 * 
	 * @param piBanco
	 * @param piArchivos
	 * @return int
	 * 
	 * Actualizar saldo de banco en chequeras con el maximo saldo del registro
	 * cuya fecha sea la mayor agrupando por banco, chequera y empresa
	 * 
	 * Sub actualizar_saldo_chequera_version2(pi_banco As Integer, Optional
	 * pi_archivos As String) se cambia la forma de actualizar los saldos en
	 * cat_cta_banco se agrego la funcion
	 * FunSQLUpdateSaldos_CatCtaBanco_version3 para que la actualizacion de
	 * saldos sea mas sencilla
	 */
	public int actualizarSaldoChequeraV2(int piBanco, String piArchivos) {
		int res = 0;
		ComunDto dto = null;
		if (piBanco == 21) {
			List<ComunDto> datos = seleccionarChequerasActualizar(piBanco,
					piArchivos);
			if (datos.size() > 0) {
				for (int z = 0; z < datos.size(); z++) {
					dto = new ComunDto();
					dto.setIdBanco(piBanco);
					dto.setSaldo(datos.get(z).getSaldo());
					dto.setIdChequera(datos.get(z).getIdChequera());
					res = actualizarSaldosCatCtaBancoV3(dto);
					dto = null;
				}
			}
		} else {
			res = actualizarSaldosCatCtaBancoV2(piBanco);
		}
		return res;
	}

	/**
	 * 
	 * @param piBanco
	 * @param piArchivos
	 * @return List<ComunDto>
	 * 
	 * Public Function FunSQLSelectChequerasActualizar(ByVal pi_banco As
	 * Integer, ByVal pi_archivos As String) As ADODB.Recordset
	 */
	@SuppressWarnings("unchecked")
	public List<ComunDto> seleccionarChequerasActualizar(int piBanco, String piArchivos) {
		sb = new StringBuffer();
		sb.append(" SELECT COALESCE(saldo_bancario, 0.00) AS saldo, cat_cta_banco.id_chequera AS id_chequera ");
		sb.append("\n FROM movto_banca_e m, cat_cta_banco cat_cta_banco ");
		sb.append("\n WHERE secuencia = (SELECT MAX(secuencia) FROM movto_banca_e m2 ");
		sb.append("\n				 WHERE m2.no_empresa = cat_cta_banco.no_empresa ");
		sb.append("\n 	AND m2.id_banco = cat_cta_banco.id_banco ");
		sb.append("\n 	AND m2.id_chequera = cat_cta_banco.id_chequera ");
		sb.append("\n 	AND m2.saldo_bancario IS NOT NULL ");
		if (piArchivos.equals(""))
			sb.append("\n	AND m2.id_chequera IN (SELECT DISTINCT id_chequera FROM movto_banca_e WHERE archivo IN ('')) ");
		else
			sb.append("\n 	AND m2.id_chequera IN (SELECT DISTINCT id_chequera FROM movto_banca_e WHERE archivo IN ('"
							+ piArchivos.substring(0, piArchivos.length())
							+ "'))");
		sb.append(" )\n 	AND cat_cta_banco.id_banco = " + piBanco);
		try {
			List<ComunDto> datos = jdbcTemplate.query(sb.toString(), new RowMapper() {
						public ComunDto mapRow(ResultSet rs, int idx)
								throws SQLException {
							ComunDto dato = new ComunDto();
							dato.setSaldo(rs.getDouble("saldo"));
							dato.setIdChequera(rs.getString("id_chequera"));
							return dato;
						}
					});
			return datos;
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()
							+ " "
							+ e.toString()
							+ "P:BE, C:AdministradorArchivosDao, M:seleccionarChequerasActualizar");
			return null;
		}
	}

	/**
	 * 
	 * @param dto
	 * @return int
	 * 
	 * recibe setSaldo() setIdChequera() setIdBanco() Public Function
	 * FunSQLUpdateSaldos_CatCtaBanco_version3(ByVal piBanco As Long, ByVal
	 * saldo As Double, ByVal chequera As String) As Long
	 */
	public int actualizarSaldosCatCtaBancoV3(ComunDto dto) {
		sb = new StringBuffer();
		sb.append(" UPDATE cat_cta_banco SET saldo_banco_ini = "
				+ dto.getSaldo());
		sb.append("\n WHERE id_chequera = '" + dto.getIdChequera() + "' ");
		sb.append("\n 	AND id_banco = " + dto.getIdBanco());
		try {
			return jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()
							+ " "
							+ e.toString()
							+ "P:BE, C:AdministradorArchivosDao, M:actualizarSaldosCatCtaBancoV3");
			return -1;
		}
	}

	/**
	 * 
	 * @param piBanco
	 * @return int
	 * 
	 * Funci�n autom�tica para reemplazar el uso de sentencias SQL Recibe los
	 * par�metros necesarios para ejecutar la sentencia de la forma frmAdmor
	 * Public Function FunSQLUpdateSaldos_CatCtaBanco_version2(ByVal piBanco As
	 * Long) As Long
	 */
	public int actualizarSaldosCatCtaBancoV2(int piBanco) {
		sb = new StringBuffer();
		sb.append(" UPDATE cat_cta_banco SET saldo_banco_ini = ( ");
		sb.append("\n	SELECT saldo_bancario FROM movto_banca_e m ");
		sb.append("\n	WHERE secuencia = (SELECT MAX(secuencia) FROM movto_banca_e m2 ");
		sb.append("\n                  WHERE m2.no_empresa = cat_cta_banco.no_empresa ");
		sb.append("\n                   AND m2.id_banco = cat_cta_banco.id_banco ");
		sb.append("\n                   AND m2.id_chequera = cat_cta_banco.id_chequera ");
		sb.append("\n                   AND m2.saldo_bancario IS NOT NULL ) ");
		sb.append("\n ) ");
		sb.append("\n WHERE id_banco = " + piBanco);
		try {
			return jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()
							+ " "
							+ e.toString()
							+ "P:BE, C:AdministradorArchivosDao, M:actualizarSaldosCatCtaBancoV2");
			return -1;
		}
	}
	/**
	 * 
	 * @return
	 * 
	 * Obtiene la fecha de hoy segun la tabla fechas de la BD 
	 */
	public Date obtenerFechaHoy() {
		gral = new ConsultasGenerales(jdbcTemplate);
		//return gral.obtenerFechaHoy()==null ? new Date() : gral.obtenerFechaHoy();
		return gral.obtenerFechaHoy();
	}
	
	/**
	 * 
	 * @param dto
	 * @return List<ComunDto>
	 *
	 * Public Function FunSQLBuscar_empresa(ByVal psChequera As String, Optional ByVal pi_banco As Integer, _
                          Optional pbSwiftMT940_MN As Boolean, Optional movDiarios As Boolean) As ADODB.Recordset
	 */
	@SuppressWarnings("unchecked")
	public List<ComunDto> buscarEmpresa(ParametroBancomerDto dto){
		List<ComunDto> datos=null;
		sb = new StringBuffer();
		sb.append(" SELECT no_empresa, id_banco, id_chequera, coalesce(b_exporta, '') as b_exporta");
		sb.append("\n FROM cat_cta_banco ");
	    if(dto.isMovDiarios())
	    	sb.append("\n WHERE id_clabe = '" + dto.getIdChequera() + "'");
	    else if(dto.isPbSwiftMT940MN())
	    	sb.append("\n WHERE id_chequera like '%" + dto.getIdChequera() + "'");
	    else
	    	sb.append("\n WHERE id_chequera = '" + dto.getIdChequera() + "'");
	    if(dto.getIdBanco()>0)
	    	sb.append("\n 	AND id_banco = " + dto.getIdBanco());
	    /*If gsDBM = "SYBASE" Then
	        sSQL = sSQL & Chr(10) & " AT ISOLATION READ UNCOMMITTED "
	    End If*/
	    try{
		    datos = jdbcTemplate.query(sb.toString(), new RowMapper() {
				public ComunDto mapRow(ResultSet rs, int idx) {
					ComunDto dato = new ComunDto();
					try{
						dato.setIdEmpresa(rs.getInt("no_empresa"));
						dato.setIdBanco(rs.getInt("id_banco"));
						dato.setIdChequera(rs.getString("id_chequera"));
						dato.setBExporta(rs.getString("b_exporta"));
					}catch(SQLException e){
						bitacora.insertarRegistro(new Date().toString()
								+ " "
								+ e.toString()
								+ "P:BE, C:AdministradorArchivosDao, M:buscarEmpresa");
					}
					return dato;
				}
			});
	    }catch(Exception e){
	    	bitacora.insertarRegistro(new Date().toString()
					+ " "
					+ e.toString()
					+ "P:BE, C:AdministradorArchivosDao, M:buscarEmpresa");
	    }
		return datos;
	}
	
	/**
	 * 
	 * @param dto
	 * @return String
	 *
	 * 'FunSQLInserta_movto_banca_e_str funcion a la cual se le agrego un campo (ps_hora) el cual
		'trae la hora del archivo, con motivos de identificacion del registro para el fideicomiso
		Public Function FunSQLInserta_movto_banca_e_STR(ByVal lempresa As Long, ByVal iBanco As Integer, _
		    ByVal sChequera As String, ByVal sSecuencia As String, ByVal sFec_valor As String, _
		    ByVal sSucursal As String, ByVal sFolio_banco As String, ByVal sReferencia As String, _
		    ByVal sCargo_abono As String, ByVal dImporte As Double, ByVal sB_salvo_buen_cobro As String, _
		    ByVal sConcepto As String, ByVal lUsuario_alta As Long, sDescripcion As String, _
		    ByVal sArchivo As String, ByVal psFecha_hoy As String, _
		    Optional psObservacion As String, Optional psCve_concepto As String, _
		    Optional pdSaldoBancario As Double, Optional ByVal psEstatus As String, _
		    Optional plNoLinea_archivo As Long = 0, Optional ps_hora As String = "") As String
	 * Solo para Bancomer 
	 */
	public String insertarMovtoBancaEStr(ParametroLayoutDto dto){
		sb=new StringBuffer();
		if(dto.getPsEstatus()==null || dto.getPsEstatus().equals(""))
			dto.setPsEstatus("N");
		
		sb.append(" insert into movto_banca_e( ");
		sb.append("\n 	no_empresa, id_banco, id_chequera, secuencia, fec_valor, ");
		sb.append("\n 	sucursal, folio_banco, referencia, cargo_abono, importe, b_salvo_buen_cobro, ");
		sb.append("\n 	concepto, id_estatus_trasp,fec_alta, b_trasp_banco, b_trasp_conta, movto_arch, ");
		sb.append("\n 	usuario_alta, descripcion, archivo");
		if(dto.getPsObservacion()!=null) 
	        sb.append(", observacion");
	    if(dto.getPsCveConcepto()!=null)
	        sb.append(", id_cve_concepto ");
	    if(dto.getPdSaldoBancario()>0)
	        sb.append(", saldo_bancario ");
	    if(dto.getPlNoLineaArchivo() > 0)
	        sb.append(", no_linea_arch ");
	    sb.append(", id_estatus_cancela  ");
	    sb.append(") ");
	    sb.append("\n values(" + dto.getPlNoEmpresa());
	    sb.append("," + dto.getPiBanco()+ ",'" + dto.getPsIdChequera()+  "'," + dto.getPsSecuencia()+  ",'" + dto.getPsFecValor()+  "',");
		sb.append("'" + dto.getPsSucursal()+  "','" + dto.getPsFolioBanco()+  "'");
		sb.append(",\n	'" + dto.getPsReferencia()+ "','" + dto.getPsCargoAbono()+ "'");
		sb.append("," + dto.getPdImporte());
		sb.append(",'" + dto.getPsBSalvoBuenCobro()+  "','" + dto.getPsConcepto()+ "','" + dto.getPsEstatus()+ "',");
		sb.append("\n	'" + dto.getPsFechaHoy() +  "', 'N', 'N', 1, " + dto.getPsUsuarioAlta()+ ",");
		sb.append("'" + dto.getPsDescripcion()+ "','" + dto.getPsArchivo() +  "'");
		if(dto.getPsObservacion()!=null) 
			sb.append(", \n'" + dto.getPsObservacion() + "'");
	    if(dto.getPsCveConcepto()!=null)
	    	sb.append(",'" + dto.getPsCveConcepto()+ "'");
	    if(dto.getPdSaldoBancario()>0)
	    	sb.append("," + dto.getPdSaldoBancario());
	    if(dto.getPlNoLineaArchivo() > 0)
	    	sb.append("," + dto.getPlNoLineaArchivo());
	    sb.append(", 0");
	    sb.append(");");
	    //logger.info("insertarMovtoBancaEStr" + sb.toString());
	    //System.out.println(sb.toString());
		return sb.toString();
	}
	
	public int ejecutaSentencia(String sentencia){
		try{
			return jdbcTemplate.update(sentencia);
		}catch(Exception e){
	    	bitacora.insertarRegistro(new Date().toString()
					+ " "
					+ e.toString()
					+ "P:BE, C:AdministradorArchivosDao, M:ejecutaSentencia");
	    	return -1;
	    }
		
	}
	/**
	 * 
	 * @param dto
	 * @return ParametroLayoutDto
	 * 
	 *	Public Function FunObtieneDescripcion(ByVal idBanco As Integer, ByVal psdescripcion As String, ByVal ps_registro As String, _
	                                    ByVal psCargoAbono As String, ByRef psconcepto As String, ByRef psFolio_banco As String) As Boolean 
	 */
	@SuppressWarnings("unchecked")
	public boolean obtenerDescripcion(ParametroLayoutDto dto){
		boolean retorno = false;
		List<ParametroLayoutDto> datos = null;
		sb = new StringBuffer();
		sb.append(" SELECT desc_concepto, coalesce(folio_banco_ini,0) as folio_banco_ini, coalesce(folio_banco_long,0) as folio_banco_long ");
		sb.append("\n FROM equivale_concepto ");
		sb.append("\n WHERE id_banco = " + dto.getPiBanco()); 
		sb.append("\n 	and cargo_abono = '" + dto.getPsCargoAbono() + "'");
	    //if(gsDBM.equals("DB2"))
	    //    sSQL = sSQL & " and substring('" & psdescripcion & "',1, length(desc_concepto)) = desc_concepto "
	    //Else
			sb.append("\n 	and substring('" + dto.getPsDescripcion() + "',1, len(desc_concepto)) = desc_concepto ");
		sb.append("\n ORDER BY desc_concepto desc  ");
	    //logger.info("obtenerDescripcion" + sb.toString());
	    try{
	    	 datos = jdbcTemplate.query(sb.toString(), new RowMapper() {
					public ParametroLayoutDto mapRow(ResultSet rs, int idx) {
						ParametroLayoutDto dato = new ParametroLayoutDto();
						try{
							dato.setPsConcepto(rs.getString("desc_concepto"));
							dato.setFolioIni(rs.getInt("folio_banco_ini"));
							dato.setFolioLong(rs.getInt("folio_banco_long"));
						}catch(SQLException e){
							bitacora.insertarRegistro(new Date().toString()
									+ " "
									+ e.toString()
									+ "P:BE, C:AdministradorArchivosDao, M:obtenerDescripcion");
						}
						return dato;
					}
				});
	    }catch(Exception e){
	    	bitacora.insertarRegistro(new Date().toString()
					+ " "
					+ e.toString()
					+ "P:BE, C:AdministradorArchivosDao, M:obtenerDescripcion");
	    }
	    if(datos!=null&&datos.size()>0){
	        dto.setPsConcepto(datos.get(0).getPsConcepto());
	        if(datos.get(0).getFolioIni() > 0 && datos.get(0).getFolioLong() > 0)
	            dto.setPsFolioBanco(dto.getPsRegistro().substring(datos.get(0).getFolioIni()-1,(datos.get(0).getFolioIni()+datos.get(0).getFolioLong())-1));
	        retorno = true;
	    }
	    else
	    	retorno = false;
		return retorno;
	}
	
	/**
	 * 
	 * @param dto
	 * @return int
	 *
	 * Public Function FunSQLUpdateDatosEDO_CTA_EXTRAS(ByVal NoEmpresa As Long, _
        ByVal idBanco As Integer, ByVal IdChequera As String, ByVal Secuencia As Long, _
        ByVal sucursal As String, ByVal referencia As String, ByVal BSBC As String, _
        ByVal Concepto As String, ByVal IdCveConcepto As String, ByVal psArchivo As String, _
        ByVal Descripcion As String, ByVal CargoAbono As String) As Long
	 */
	public int actualizarDatosEDOCtaExtras(ParametroLayoutDto dto){
		sb = new StringBuffer();
		int res=-1;
		sb.append(" UPDATE movto_banca_e SET ");
	    if(dto.getPsConcepto().equals("MD")){
	    	dto.setPsSucursal("'" + dto.getPsSucursal() + "', ");
	    	sb.append(dto.getPsSucursal());
	        if(dto.getCargoAbono().equals("E") || Long.parseLong(dto.getPsReferencia()) == 70005)
	        	sb.append(" referencia = '" + dto.getPsReferencia() + "',");
	        sb.append("    b_salvo_buen_cobro = '" + dto.getPsBSalvoBuenCobro() + "', ");
	        //'concepto = '" & Concepto & "', "
	        sb.append("    id_cve_concepto = '" + dto.getPsCveConcepto() + "', ");
	    }
	    if(dto.getPsArchivo()!=null && !dto.getPsArchivo().equals(""))
	    	sb.append("    archivo = '" + dto.getPsArchivo() + "'");
	    sb.append(" Where ");
	    sb.append(" no_empresa = " + dto.getPlNoEmpresa());
	    sb.append("\n 	And id_banco = " + dto.getPiBanco());
	    sb.append("\n 	And id_chequera = '" + dto.getPsIdChequera() + "' ");
	    sb.append("\n 	AND secuencia = " + dto.getPsSecuencia());
	    /*if(gsDBM.equals("DB2")){
			res=jdbcTemplate.update(sb.toString());
			sb = null;
			sb = new StringBuffer();
		}else*/
	    sb.append(" ;");
	    if(dto.getPsDescripcion().trim().length() > 20 )
	    	dto.setPsDescripcion(dto.getPsDescripcion().trim().substring(0, 20));
	    sb.append("\n UPDATE movimiento set referencia_ban = '" + dto.getPsDescripcion().trim() + "'");
	    sb.append("\n Where ");
	    sb.append(" no_empresa = " + dto.getPlNoEmpresa());
	    sb.append("\n	And id_banco = " + dto.getPiBanco());
	    sb.append("\n	And id_chequera = '" + dto.getPsIdChequera() + "' ");
	    sb.append("\n	And id_inv_cbolsa = " + dto.getPsSecuencia());
	    /*if(gsDBM.equals("DB2")){
			res=jdbcTemplate.update(sb.toString());
			sb = null;
			sb = new StringBuffer();
		}else*/
	    sb.append(" ;");	    
	    sb.append("\n UPDATE hist_movimiento set referencia_ban = '" + dto.getPsDescripcion().trim() + "'");
	    sb.append("\n Where ");
	    sb.append(" no_empresa = " + dto.getPlNoEmpresa());
	    sb.append("\n	And id_banco = " + dto.getPiBanco());
	    sb.append("\n	And id_chequera = '" + dto.getPsIdChequera() + "' ");
	    sb.append("\n	And id_inv_cbolsa = " + dto.getPsSecuencia());
	    //logger.info("actualizarDatosEDOCtaExtras" + sb.toString());
	    try{
	    	res = jdbcTemplate.update(sb.toString());
	    }catch(Exception e){
	    	bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
			+ "P:BE, C:AdministradorArchivosDao, M:actualizarDatosEdoCtaExtras");
	    }
		return res;
	}
		
	/**
	 * @author Cristian Garcia Garcia
	 * @since 26/Octubre/2010
	 * @see <p>Aqui se realizaran las consultas necesarias en la lectura del layout</p>
	 */
	
	/**
	 * Metodo que consulta el numero de empresa en la tabla cat_cta_banco
	 * 
	 * @param Obtiene
	 *            un objeto Dto para realizar la condicion en base a eso
	 * @return List<CatCtaBancoDto> lista que retorna el numero de empresa
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<CatCtaBancoDto> consultarEmpresa(CatCtaBancoDto empresa){
		sb = new StringBuffer();
		List<CatCtaBancoDto> noEmpre = null;
		sb.append("SELECT  no_empresa,pago_mass, id_banco from cat_cta_banco");
		ArrayList<String> cond = new ArrayList<String>();
		if (empresa != null) {
			if (empresa.getIdBanco() > 0) {
				cond.add(" id_banco= " + empresa.getIdBanco());
			}
			if (empresa.getIdChequera() != null
					&& !empresa.getIdChequera().equals("")) {
				cond.add(" id_chequera= '" + empresa.getIdChequera() + "'");
			}
			if (cond.size() > 0) {
				sb.append(" WHERE" + StringUtils.join(cond, " AND"));
			}
		}
		try{
			noEmpre = jdbcTemplate.query(sb.toString(), new RowMapper() {
				public CatCtaBancoDto mapRow(ResultSet rs, int idx){
					CatCtaBancoDto empresa = new CatCtaBancoDto();
					try {
						empresa.setNoEmpresa(rs.getInt("no_empresa"));
						empresa.setPagoMass(rs.getString("pago_mass"));
						empresa.setIdBanco(rs.getInt("id_banco"));
					}catch (SQLException e){
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
								+ "P:BE, C:AdministradorArchivosDao, M:consultarEmpresa");
					}
					return empresa;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
						+ "P:BE, C:AdministradorArchivosDao, M:consultarEmpresa");
		}
		return noEmpre;
	}
	
	@SuppressWarnings("unchecked")
	public int consultarMovimiento(MovtoBancaEDto movimiento){
		sb = new StringBuffer();
		List<String> movimientos = null;
		sb.append(" SELECT movto_arch from movto_banca_e ");
		ArrayList<String> cond = new ArrayList<String>();
		if (movimiento != null) {
			if (movimiento.getFolioBanco() != null && !movimiento.getFolioBanco().equals("")) {
				cond.add(" folio_banco= '" + movimiento.getFolioBanco() + "'");
			}
			if (movimiento.getConcepto() != null && !movimiento.getConcepto().equals("")) {
				cond.add(" concepto= '" + movimiento.getConcepto() + "'");
			}
			if (movimiento.getMovtoArch() > 0) {
				cond.add(" movto_arch=" + movimiento.getMovtoArch());
			}
			if (movimiento.getIdChequera() != null && !movimiento.getIdChequera().equals("")) {
				cond.add(" id_chequera= '" + movimiento.getIdChequera() + "'");
			}
			if (movimiento.getFecValor() != null && !movimiento.getFecValor().equals("")) {
				cond.add(" fec_valor between '"+ fecInicio.format(movimiento.getFecValor()) + "'"
						+ " and '" + fecFinal.format(movimiento.getFecValor()) + " 23:59'");
			}
			if (movimiento.getImporte() > 0) {
				cond.add(" importe= " + movimiento.getImporte());
			}
			if (movimiento.getNoLineaArch() > 0) {
				cond.add(" no_linea_Arch= " + movimiento.getNoLineaArch());
			}
			if (movimiento.getDescripcion() != null && !movimiento.getDescripcion().equals("N")	&& !movimiento.getDescripcion().equals("")) {
				cond.add(" descripcion= '" + movimiento.getDescripcion() + "'");
			}
			if (cond.size() > 0) {
				sb.append(" WHERE " + StringUtils.join(cond, " AND "));
			}
			sb.append(" UNION SELECT movto_arch from hist_movto_banca_e");
		}
		try{
			movimientos = jdbcTemplate.query(sb.toString(), new RowMapper() {
				public String mapRow(ResultSet rs, int idx){
					String dato=null;
					try {
						rs.getString("movto_arch");
					} catch (SQLException e) {
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
								+ "P:BE, C:AdministradorArchivosDao, M:consultarMovimiento");
					}
					return dato;
				}
			});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BE, C:AdministradorArchivosDao, M:consultarMovimiento");
		}
		if (movimientos.size() > 0)
			return 1;
		else
			return 0;

	}
	
	/**
	 * 
	 * @param registro
	 * @return int
	 * 
	 * Call gobjSQL.FunSQLInserta_movto_banca_e(pi_noempresa, BANAMEX, _
	 * ps_Chequera, pl_folio, ps_Fecha, _ psSucursal, ps_foliobanco,
	 * psReferencia, _ psCargoAbono, pdImporte, ps_SBCobro, _ psConcepto,
	 * -GI_USUARIO, psDescripcion, _ ps_Archivo, Format(GT_FECHA_HOY,
	 * "dd/mm/yyyy"), _ psObservacion, "", 0, "", plContadorRenglon)
	 */
	public int insertar(MovtoBancaEDto registro){
		int res = -1;
		sb = new StringBuffer();
		gral = new ConsultasGenerales(jdbcTemplate);
		sb.append(" INSERT INTO movto_banca_e(no_empresa,id_banco,id_chequera,secuencia,fec_valor,sucursal,");
		sb.append("\n	folio_banco, referencia, cargo_abono, importe, concepto, " );
		sb.append("\n	descripcion, archivo, usuario_alta, fec_alta, no_linea_arch)");
		sb.append(" VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
		try{
			res = jdbcTemplate.update(sb.toString(), new Object[] {
					registro.getNoEmpresa(), registro.getIdBanco(),
					registro.getIdChequera(), registro.getSecuencia(),
					registro.getFecValor(), registro.getSucursal(),
					registro.getFolioBanco(), registro.getReferencia(),
					registro.getCargoAbono(), registro.getImporte(),
					registro.getConcepto(), registro.getDescripcion(),
					registro.getArchivo(),registro.getUsuarioAlta(), registro.getFecAlta(),
					registro.getNoLineaArch() });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BE, C:AdministradorArchivosDao, M:insertar");
		}
		return res;
	}
	
	/**
	 * @param psConcepto,
	 *            idBanco
	 * @return String Obtiene el cargo abono de la tabla equivale_concepto
	 */
	@SuppressWarnings("unchecked")
	public List<String> analizarConceptoBanamex(String psConcepto, int idBanco) {
		List<String> cargoAbono = null;
		StringBuffer sb = new StringBuffer();
		sb.append(" select cargo_abono");
		sb.append("\n from equivale_concepto");
		sb.append("\n WHERE id_banco = " + idBanco);
		sb.append("\n and desc_concepto = '" + psConcepto + "'");
		try{
			cargoAbono = jdbcTemplate.query(sb.toString(), new RowMapper() {
				public String mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getString("cargo_abono");
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BE, C:AdministradorArchivosDao, M:analizarConceptoBanamex");
		}
		return cargoAbono;
	}
	
	/**
	 * 
	 * @param dto
	 * @return int
	 * 
	 * inserta movto_banca_e BANAMEX
	 */
	public int insertaMovimientoBanca(MovtoBancaEDto dto){
		int res=-1;
		if (dto.getEstatusExp()!=null && dto.getEstatusExp().equals(""))
			dto.setEstatusExp("N");      
		StringBuffer sSQL = new StringBuffer();     
		StringBuffer sSQL2 = new StringBuffer(); 
	    sSQL.append(" INSERT INTO movto_banca_e(");
	    sSQL.append(" no_empresa, id_banco, id_chequera, secuencia, fec_valor,");
	    sSQL.append(" sucursal, folio_banco, referencia, cargo_abono, importe, b_salvo_buen_cobro,");
	    sSQL.append(" concepto, id_estatus_trasp, fec_alta, b_trasp_banco, b_trasp_conta, movto_arch,");
	    sSQL.append(" usuario_alta, descripcion, archivo");
	    if (dto.getObservacion()!=null && !dto.getObservacion().equals("") ) 
	    	sSQL.append(" ,observacion");
	   
	    if (dto.getIdCveConcepto()!=null && !dto.getIdCveConcepto().equals(""))
	        sSQL.append(" ,id_cve_concepto ");
	     
	    if(dto.getSaldoBancario()>0) 
	        sSQL.append(" ,saldo_bancario ");
	    if (dto.getNoLineaArch()>0)
	        sSQL.append(" ,no_linea_arch ");
	    if(dto.getIdEstatusCancela()>0)
	    	sSQL.append(" , id_estatus_cancela ");
	    if(dto.getIdEstatusTrasp()==null)
	    	dto.setIdEstatusTrasp("");
	    sSQL.append(") ");
	    sSQL.append(" VALUES (");
	    sSQL.append(dto.getNoEmpresa());
	    sSQL.append(","+dto.getIdBanco()+",'"+dto.getIdChequera()+"',"+dto.getSecuencia()+",'"+funciones.ponerFecha(dto.getFecValor())+"',");
	    sSQL.append("'"+dto.getSucursal()+"','" +dto.getFolioBanco()+ "'");
	    sSQL.append(",'"+dto.getReferencia()+"','"+dto.getCargoAbono()+"'");
	    sSQL.append(","+dto.getImporte());
	    sSQL.append(",'"+dto.getBSalvoBuenCobro()+ "','"+dto.getConcepto()+"','"+dto.getIdEstatusTrasp()+"',");
	   	sSQL.append("'"+funciones.ponerFecha(dto.getFecAlta())+"','N','N',1," +dto.getUsuarioAlta()+",");
	    sSQL.append("'"+dto.getDescripcion()+"','"+dto.getArchivo()+"'");
	    if (dto.getObservacion()!=null && !dto.getObservacion().equals("") ) 
	        sSQL.append(",'" + dto.getObservacion() + "'");
	    if (dto.getIdCveConcepto()!=null && !dto.getIdCveConcepto().equals(""))
	        sSQL.append(",'"+dto.getIdCveConcepto()+"'");
	    if(dto.getSaldoBancario()>0) 
	        sSQL.append("," +dto.getSaldoBancario());
	    if (dto.getNoLineaArch()>0)
	        sSQL.append("," + dto.getNoLineaArch());
	    if(dto.getIdEstatusCancela()>0)
	    	sSQL.append(","+dto.getIdEstatusCancela());
	    sSQL.append(")");
	    try{
	    	res = jdbcTemplate.update(sSQL.toString());
	    }catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BE, C:AdministradorArchivosDao, M:insertaMovimientoBanca");
		}
	    //Actualiza el saldo_banco_ini si cargo_abono es S
	    if(dto.getCargoAbono()!=null&&dto.getCargoAbono().equals("S")){
	        if (dto.getFecValor()==dto.getFecAlta())
	            sSQL2.append("UPDATE cat_cta_banco");
	        else
	            sSQL2.append("UPDATE hist_cat_cta_banco");
	        sSQL2.append(" SET saldo_banco_ini = "+dto.getImporte());
	        sSQL2.append(" WHERE no_empresa = " +dto.getNoEmpresa());
	        sSQL2.append(" AND id_banco = " +dto.getIdBanco());
	        sSQL2.append(" AND id_chequera = '"+dto.getIdChequera()+"'");
	        if (dto.getFecValor()!=dto.getFecAlta())
	            sSQL2.append(" AND fec_valor = '"+fecInicio.format(dto.getFecValor())+"'");
	        try{
	        	jdbcTemplate.update(sSQL2.toString());
	        }catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
						+ "P:BE, C:AdministradorArchivosDao, M:insertaMovimientoBancaA");
			}
	    }
	    return res;
	}
	
	/**
	 * 
	 * @param referencia
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<MovtoBancaEDto>verificarPago(String referencia)
	{
		sb = new StringBuffer();
		List<MovtoBancaEDto> pago = null;
		sb.append(" SELECT * FROM movimiento ");
		ArrayList<String> cond = new ArrayList<String>();
		cond.add(" id_tipo_operacion = 3200 ");
		if (!referencia.equals(""))
			cond.add(" folio_ref = '" + referencia + "' ");
		cond.add(" id_estatus_mov='T' ");
		if (cond.size() > 0) {
			sb.append(" WHERE " + StringUtils.join(cond, " AND"));
		}
		try{
			pago = jdbcTemplate.query(sb.toString(), new RowMapper() {
				public MovtoBancaEDto mapRow(ResultSet rs, int idx)throws SQLException {				 
					MovtoBancaEDto dto = new MovtoBancaEDto();
					dto.setNoEmpresa(rs.getInt("no_empresa"));
					dto.setFecValor(rs.getDate("fec_valor"));
					dto.setFolioDetConf(rs.getInt("no_folio_det"));
					return dto;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BE, C:AdministradorArchivosDao, M:verificarPago");
		}
		return pago;
	}
	
	/**
	 * 
	 * @param referencia
	 * @param estatusMov
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<MovtoBancaEDto> consultarCliente(String referencia, boolean estatusMov )
	{
		StringBuffer sb=new StringBuffer();
		ArrayList<String> cond=new ArrayList<String>();
		List<MovtoBancaEDto> cliente = null;
		sb.append(" SELECT no_cliente, id_banco_benef, id_chequera_benef FROM movimiento ");
		if(!referencia.equals(""))
			cond.add(" no_folio_det= '"+referencia+"'");
		if(estatusMov)
			cond.add(" id_estatus_mov='A' ");
		if(cond.size()>0)
			sb.append(" WHERE " + StringUtils.join(cond, " AND"));
		try{
			cliente = jdbcTemplate.query(sb.toString(), new RowMapper() {
				public MovtoBancaEDto mapRow(ResultSet rs, int idx)
						throws SQLException {				 
					MovtoBancaEDto dto = new MovtoBancaEDto();
					 
					return dto;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BE, C:AdministradorArchivosDao, M:consultarCliente");
		}
		return cliente;
			
		
	}
	
	/**
	 * 
	 * @param dto
	 * @param movDiarios
	 * @param pbSwiftMT940MN
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<MovtoBancaEDto>buscarEmpresa(CatCtaBancoDto dto, boolean movDiarios,boolean pbSwiftMT940MN)
	{
		StringBuffer sb=new StringBuffer();
		ArrayList<String> cond= new ArrayList<String>();
		List<MovtoBancaEDto> empresa = null;
		sb.append("Select no_empresa,id_banco,id_chequera ");
		sb.append(" from cat_cta_banco ");
	    if (movDiarios)
	        cond.add("id_clabe = '" +dto.getIdChequera()+ "'");
	    else if (pbSwiftMT940MN)
	        cond.add(" id_chequera like '%" +dto.getIdChequera()+ "' ");
	    else
	        cond.add(" id_chequera = '"+ dto.getIdChequera()+"' ");
	    if (dto.getIdBanco() != 0) 
	        cond.add(" id_banco = " + dto.getIdBanco());
	    if(cond.size()>0)
	    	sb.append(" WHERE " + StringUtils.join(cond, " AND "));
	    try{
		    empresa = jdbcTemplate.query(sb.toString(), new RowMapper() {
				public MovtoBancaEDto mapRow(ResultSet rs, int idx) throws SQLException {				 
					MovtoBancaEDto dto = new MovtoBancaEDto();
					dto.setNoEmpresa(rs.getInt("no_empresa"));
					dto.setIdBanco(rs.getInt("id_banco"));
					dto.setIdChequera(rs.getString("id_chequera"));				 
					return dto;
				}
			});
	    }catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BE, C:AdministradorArchivosDao, M:buscarEmpresa");
		}
		return empresa;
	    
	    /*If gsDBM = "SYBASE" Then
	        sSQL = sSQL & Chr(10) & " AT ISOLATION READ UNCOMMITTED "
	    End If*/
	}
	
	public Date obtenerFechaHoyBase(){
		gral = new ConsultasGenerales(jdbcTemplate);
		return gral.obtenerFechaHoy();
	}

	/**
	 * 
	 * @author Jessica Arelly Cruz Cruz
	 * @since 01/Noviembre/2010
	 * @see
	 *        <p>
	 *        Tabla cat_cta_banco
	 *        </p>
	 * 
	 * Metodo que consulta la tabla de cat_cta_banco de acuerdo a la funcion
	 * buscarEmpresa
	 * 
	 * @param empresa,
	 *            movDiarios, pbSwiftMT940_MN
	 * @return empresa, empresas
	 * @throws Exception
	 */

	@SuppressWarnings("unchecked")
	public List<CatCtaBancoDto> consultarEmpresa(CatCtaBancoDto empresa, int pi_banco, boolean movDiarios, boolean pbSwiftMT940_MN) {
		StringBuffer sb = new StringBuffer();
		List<CatCtaBancoDto> empresas = null;
		sb.append(" SELECT no_empresa, id_banco, id_chequera ");
		sb.append(" FROM cat_cta_banco ");
		ArrayList<String> cond = new ArrayList<String>();
		if (empresa != null) {
			if (movDiarios == true) {
				cond.add(" id_clabe = '" + empresa.getIdClabe() + "' ");
			}
			/*
			 * if(pbSwiftMT940_MN == true){ cond.add(" id_chequera like '%" +
			 * empresa.getIdChequera()+"' "); }
			 */
			else {
				cond.add(" id_chequera = '" + empresa.getIdChequera() + "' ");
			}
			if (pi_banco != 0) {
				cond.add("id_banco = " + pi_banco);
			}
			/*
			 * If gsDBM = "SYBASE" Then sSQL = sSQL & Chr(10) & " AT ISOLATION
			 * READ UNCOMMITTED " End If
			 */

			if (cond.size() > 0) {
				sb.append(" WHERE " + StringUtils.join(cond, " AND "));
			}
		}
		try{
			empresas = jdbcTemplate.query(sb.toString(), new RowMapper() {
					public CatCtaBancoDto mapRow(ResultSet rs, int idx)	throws SQLException {
						CatCtaBancoDto empresa = new CatCtaBancoDto();
						empresa.setNoEmpresa(rs.getInt("no_empresa"));
						empresa.setIdBanco(rs.getInt("id_banco"));
						empresa.setIdChequera(rs.getString("id_chequera"));
						return empresa;
					}
				});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BE, C:AdministradorArchivosDao, M:buscarEconsultarEmpresampresaJ");
		}
		return empresas;
	}
	
	/**
	 * Inserta un nuevo registro en arch_movto_banca_e
	 * 
	 * @param usuario
	 * @return int
	 */
	public int insertarArchMovto(ArchMovtoBancaeDto banca) {
		int res = -1;
		StringBuffer sb = new StringBuffer();
		sb.append(" INSERT INTO arch_movto_banca_e( archivo, id_chequera, fec_alta, id_banco ) ");
		sb.append(" VALUES ( ?, ?, ?, ? ) ");
		try{
			res = jdbcTemplate.update(sb.toString(), new Object[] {
					banca.getArchivo(), banca.getIdChequera(), banca.getFecAlta(),
					banca.getIdBanco() });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BE, C:AdministradorArchivosDao, M:insertarArchMovto");
		}
		return res;
	}
	
	public int insertarMovto(MovtoBancaEDto registro) throws Exception {
		int res = -1;
		String psEstatus = "";
		StringBuffer sb = new StringBuffer();
		gral = new ConsultasGenerales(jdbcTemplate);
		if (psEstatus==null||psEstatus.equals(""))
			psEstatus = "N";
		sb.append(" INSERT INTO movto_banca_e(no_empresa,id_banco,id_chequera,secuencia,fec_valor,");
		sb.append(" sucursal,folio_banco,referencia,cargo_abono,importe,");
		sb.append(" b_salvo_buen_cobro,concepto,id_estatus_trasp,fec_alta,b_trasp_banco,");
		sb.append(" b_trasp_conta,movto_arch,usuario_alta,descripcion,archivo");
		if (registro.getObservacion() != null)
			sb.append(",observacion");
		if (registro.getIdCveConcepto() != null)
			sb.append(",id_cve_concepto");
		if (registro.getSaldoBancario() != 0)
			sb.append(",saldo_bancario");
		if (registro.getNoLineaArch() > 0)
			sb.append(",no_linea_arch");
		sb.append(",id_estatus_cancela )");
		sb.append(" VALUES (" + registro.getNoEmpresa() + ", "+ registro.getIdBanco() + ", ");
		sb.append("'" + registro.getIdChequera() + "', "+ registro.getSecuencia() + ", ");
		sb.append("'" + funciones.ponerFecha(registro.getFecValor())+ "', '" + registro.getSucursal() + "', ");
		sb.append("'" + registro.getFolioBanco() + "', '" + registro.getReferencia() + "', ");
		sb.append("'" + registro.getCargoAbono() + "', "+ registro.getImporte() + ", ");
		sb.append("'" + registro.getBSalvoBuenCobro() + "', '"+ registro.getConcepto() + "', ");
		sb.append("'" + psEstatus + "', '"+ funciones.ponerFecha(registro.getFecAlta())	+ "', 'N', 'N', 1, 2, ");
		sb.append("'" + registro.getDescripcion() + "', '"	+ registro.getArchivo() + "', ");
		if (registro.getObservacion() != null)
			sb.append("'" + registro.getObservacion() + "', ");
		if (registro.getIdCveConcepto() != null)
			sb.append("'" + registro.getIdCveConcepto() + "', ");
		if (registro.getSaldoBancario() != 0)
			sb.append(registro.getSaldoBancario() + ", ");
		if (registro.getNoLineaArch() > 0)
			sb.append(registro.getNoLineaArch() + ", ");
		sb.append("0 )");
		try{
			res = jdbcTemplate.update(sb.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BE, C:AdministradorArchivosDao, M:insertarMovto");
		}
		return res;
	}
	
	/**
	 * 
	 * @param conceptoDto
	 * @return boolean
	 * 
	 * Public Function FunObtieneDescripcion(ByVal idBanco As Integer, ByVal
	 * psdescripcion As String, ByVal ps_registro As String, _ ByVal
	 * psCargoAbono As String, ByRef psconcepto As String, ByRef psFolio_banco
	 * As String) As Boolean
	 */
	public boolean obtenerDescripcion(EquivaleConceptoDto conceptoDto) {
		StringBuffer sb = new StringBuffer();
		int resultado = -1;
		sb.append(" SELECT desc_concepto, coalesce(folio_banco_ini,0) as folio_banco_ini, coalesce(folio_banco_long,0) as folio_banco_long ");
		sb.append(" FROM equivale_concepto ");
		sb.append(" WHERE ");
		sb.append(" id_banco = " + conceptoDto.getIdBanco()	+ " and cargo_abono = '" + conceptoDto.getCargoAbono() + "'");
		sb.append(" and substring('" + conceptoDto.getDescConcepto() + "',1, len(desc_concepto)) = desc_concepto ");
		sb.append(" ORDER BY desc_concepto desc ");
		try {
			resultado = jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BE, C:AdministradorArchivosDao, M:obtenerDescripcion");
		}
		return resultado>0;
		/**
		 * Set prTabla = gobjBD.obtenrecordset(sSQL)
		 * 
		 * If Not prTabla.EOF Then psconcepto = prTabla!desc_concepto If
		 * prTabla!folio_banco_ini <> 0 And prTabla!folio_banco_long <> 0 Then
		 * psFolio_banco = Val(Mid(ps_registro, prTabla!folio_banco_ini,
		 * prTabla!folio_banco_long)) End If FunObtieneDescripcion = True Else
		 * FunObtieneDescripcion = False End If prTabla.Close
		 */
	}
	
	/**
	 * Public Function FunSQLUpdateDatosEDO_CTA_EXTRAS(ByVal NoEmpresa As Long, _
	 * ByVal idBanco As Integer, ByVal IdChequera As String, ByVal Secuencia As
	 * Long, _ ByVal sucursal As String, ByVal referencia As String, ByVal BSBC
	 * As String, _ ByVal Concepto As String, ByVal IdCveConcepto As String,
	 * ByVal psArchivo As String, _ ByVal Descripcion As String, ByVal
	 * CargoAbono As String) As Long
	 */
	public int actualizarDatosEdoCtaExtras(ParametroLayoutDto dto) {
		int res=-1;
		StringBuffer sb = new StringBuffer();
		sb.append(" UPDATE movto_banca_e SET ");
		sb.append("    archivo = '" + dto.getPsArchivo() + "'");
		sb.append(" Where ");
		sb.append(" no_empresa = " + dto.getPlNoEmpresa());
		sb.append(" And id_banco = " + dto.getPiBanco());
		sb.append(" And id_chequera = '" + dto.getPsIdChequera() + "' ");
		sb.append(" AND secuencia = " + dto.getPsSecuencia());
		try {
			if (dto.getPsDescripcion().length() > 20) 
				dto.setPsDescripcion(dto.getPsDescripcion().substring(0, 20).trim());
			/*if(gsDBM.equals("DB2")){
				res=jdbcTemplate.update(sb.toString());
				sb = null;
				sb = new StringBuffer();
			}else*/
				sb.append(";\n");
			sb.append(" UPDATE movimiento SET referencia_ban = '" + dto.getPsDescripcion() + "'");
			sb.append(" Where ");
			sb.append(" no_empresa = " + dto.getPlNoEmpresa());
			sb.append(" And id_banco = " + dto.getPiBanco());
			sb.append(" And id_chequera = '" + dto.getPsIdChequera() + "' ");
			sb.append(" And id_inv_cbolsa = " + dto.getPsSecuencia());
			/*if(gsDBM.equals("DB2")){
				res=jdbcTemplate.update(sb.toString());
				sb = null;
				sb = new StringBuffer();
			}else*/
				sb.append(";\n");
			sb.append(" UPDATE hist_movimiento SET referencia_ban = '"	+ dto.getPsDescripcion() + "'");
			sb.append(" Where ");
			sb.append(" no_empresa = " + dto.getPlNoEmpresa());
			sb.append(" And id_banco = " + dto.getPiBanco());
			sb.append(" And id_chequera = '" + dto.getPsIdChequera() + "' ");
			sb.append(" And id_inv_cbolsa = " + dto.getPsSecuencia());
			res=jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BE, C:AdministradorArchivosDao, M:actualizarDatosEdoCtaExtras");
		}
		return res;
	}
	
	/**
	 * 
	 * @param dto
	 * @return int
	 * 
	 * Public Function FunSQLUpdateRechazados(ByVal idBanco As Integer) As Long
	 */
	public int actualizarRechazados(ParametroLayoutDto dto) {
		int res=-1;
		StringBuffer sb = new StringBuffer();
		/** ACTUALIZA LAS DEVOLUCIONES SIN REFERENCIA */
		sb.append("UPDATE movto_banca_e set id_estatus_cancela = '3' ");
		sb.append(" Where secuencia in ( SELECT secuencia FROM movto_banca_e m, concepto_rechazo c ");
		sb.append("WHERE m.id_banco = c.id_banco and m.concepto like c.concepto_mass + '%' ");
		sb.append(" and m.cargo_abono = c.cargo_abono and m.id_estatus_cancela = '0' ");
		sb.append(" and m.id_banco = " + dto.getPiBanco());
		sb.append(" and (SELECT COUNT(*) FROM movto_banca_e m1 ");
		sb.append(" WHERE m.folio_banco=m1.folio_banco and m.importe=m1.importe and m1.cargo_abono='E')=0)");
		try {
			/*if(gsDBM.equals("DB2")){
				res=jdbcTemplate.update(sb.toString());
				sb = null;
				sb = new StringBuffer();
			}else*/
				sb.append(";\n");
			/** ACTUALIZA LOS EGRESOS DE LAS DEVOLUCIONES A PROCESAR */
			sb.append("UPDATE movto_banca_e SET id_estatus_cancela='1' ");
			sb.append("WHERE secuencia in ( SELECT m.secuencia FROM movto_banca_e m, movto_banca_e m1, concepto_rechazo c ");
			sb.append("WHERE m.folio_banco = m1.folio_banco and m.importe = m1.importe ");
			sb.append("and m.cargo_abono = 'E' and m.id_banco = "+ dto.getPiBanco() + " and m.id_estatus_cancela = '0' ");
			sb.append("and m1.id_banco = c.id_banco and m1.cargo_abono = c.cargo_abono ");
			sb.append("and m1.concepto like c.concepto_mass + '%')");
			/*if(gsDBM.equals("DB2")){
				res=jdbcTemplate.update(sb.toString());
				sb = null;
				sb = new StringBuffer();
			}else*/
				sb.append(";\n");
			/** ACTUALIZA LOS INGRESOS DE LAS DEVOLUCIONES A PROCESAR */
			sb.append("UPDATE movto_banca_e set id_estatus_cancela = '1' ");
			sb.append(" Where secuencia in ( SELECT secuencia FROM movto_banca_e m, concepto_rechazo c ");
			sb.append("WHERE m.id_banco = c.id_banco and m.concepto like ");
			sb.append("c.concepto_mass + '%' and m.cargo_abono = c.cargo_abono ");
			sb.append(" and m.cargo_abono = 'I' and m.id_estatus_cancela = '0' ");
			sb.append(" and m.id_banco = " + dto.getPiBanco() + ")");
			res = jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BE, C:AdministradorArchivosDao, M:actualizarRechazados");
		}
		return res;
	}
	
	/**
	 * Public Function FunSqlEliminaLiberacionSBC(ByVal piIdBanco As Long) As
	 * Long
	 */

	/**
	 * Manda a llamar el store que reconstruye los saldos para la posicion de
	 * chequeras en SET
	 */
	public int eliminaLiberacionSBC(int banco) {
		/**
		 * Set rdsData = gobjBD.LlamaStore("spEliminaLiberacionSBC", piIdBanco &
		 * "|@result output")
		 * 
		 * If Not rdsData.EOF Then If rdsData.Fields(0) <> 0 Then
		 * FunSqlEliminaLiberacionSBC = 1 Else FunSqlEliminaLiberacionSBC = 0
		 * End If Else FunSqlEliminaLiberacionSBC = 2 End If rdsData.Close Else
		 * FunSqlEliminaLiberacionSBC = 0 End If
		 */
		return 0;
	}
	
	public Date cambiarFecha(String fecha) {
		gral = new ConsultasGenerales(jdbcTemplate);
		return gral.cambiarFecha(fecha);
	}
	
	@SuppressWarnings("unchecked")
	public List<CatCtaBancoDto> buscaEmpresaNetCash(ParametroBuscarEmpresaDto dto) {
		String sql = "";
		String gsDBM = ConstantesSet.gsDBM;
		List<CatCtaBancoDto> list = new ArrayList<CatCtaBancoDto>();
		
		try{
		    sql+= "SELECT no_empresa, id_banco, id_chequera";
		    sql+= "\n	FROM cat_cta_banco";
		    
		    if (dto.isPbSwiftMT940MN()) {
		    	sql+= "\n	WHERE id_chequera like '%" + dto.getPsChequera() + "'";
		    }else {
		    	sql+= "\n	WHERE id_chequera = '" + dto.getPsChequera() + "'";
		    }
		    
		    if (dto.getPiBanco() != 0)
				sql+= "\n		AND id_banco = " + dto.getPiBanco() + "";
		    //Se comenta esta linea por que no existe esta tabla VT 300112 para iussa 
//		    sql+= "\n		AND id_chequera not in (select id_chequera from bloqueo_cta)";
		    
		    if(gsDBM == "SYBASE")
		    	sql+= "\n		AT ISOLATION READ UNCOMMITTED";
		    
			list = jdbcTemplate.query(sql, new RowMapper(){
		    	public CatCtaBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
		    		CatCtaBancoDto result = new CatCtaBancoDto();
		    		result.setNoEmpresa(rs.getInt("no_empresa"));
		    		result.setIdBanco(rs.getInt("id_banco"));
		    		result.setIdChequera(rs.getString("id_chequera"));
		    		
					return result;
				}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Banca Electronica, C:AdministradorArchivosBancomerDao, M:buscarEmpresa");
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<MovtoBancaEDto> seleccionarMovtoBancaE(ParametroLayoutDto dto) {
		String sql = "";
		String sqlAdicional = "";
		String gsDBM = ConstantesSet.gsDBM;
		List<MovtoBancaEDto> list = new ArrayList<MovtoBancaEDto>();
		
		try{
			if(dto.getPdSaldo() != 0)
				sqlAdicional = " AND saldo_bancario = " + dto.getPdSaldo() + "";
			else
				sqlAdicional = "";
			
			if(dto.getPlNoLineaArchivo() > 0) {
				sqlAdicional += " AND (no_linea_arch = " + dto.getPlNoLineaArchivo() + "";
				sqlAdicional += " 	OR no_linea_arch is null) ";
			}
			
			if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE")) {
				sql += " SELECT movto_arch, no_empresa, id_banco, id_chequera ";
				sql += " FROM movto_banca_e";
				sql += " WHERE folio_banco = '" + dto.getPsFolioBanco() + "'";
				sql += " 	And concepto = '" + dto.getPsConcepto() + "'";
				sql += " 	And referencia = '" + dto.getPsReferencia() + "'";
				sql += " 	And movto_arch = 1 ";
				sql += " 	And no_empresa = " + dto.getPlNoEmpresa() + "";
				sql += " 	And id_chequera = '" + dto.getPsIdChequera() + "'";
				//sSQL = sSql & chr(10) & " And convert(char(10),fec_valor,103) = '" & psFec_valor & "'"
				sql += " 	And fec_valor = '" + dto.getPsFecValor() + "'";
				sql += " 	And importe = " + dto.getPdImporte() + "";
				sql += sqlAdicional;
				
				sql += " UNION ALL";
				sql += " SELECT movto_arch, no_empresa, id_banco, id_chequera ";
				sql += " FROM hist_movto_banca_e";
				sql += " WHERE folio_banco = '" + dto.getPsFolioBanco() + "'";
				sql += " 	And concepto = '" + dto.getPsConcepto() + "'";
				sql += " 	And referencia = '" + dto.getPsReferencia() + "'";
				sql += " 	And movto_arch = 1 ";
				sql += " 	And no_empresa = " + dto.getPlNoEmpresa() + "";
				sql += " 	And id_chequera = '" + dto.getPsIdChequera() + "'";
				sql += " 	And fec_valor = '" + dto.getPsFecValor() + "'";
				sql += " 	And importe = " + dto.getPdImporte() + "";
				sql += sqlAdicional;
			}
			if(gsDBM.equals("POSTGRESQL")) {
				sql += " SELECT movto_arch, no_empresa, id_banco, id_chequera ";
				sql += " FROM movto_banca_e";
				sql += " WHERE rtrim(ltrim(folio_banco, ' ')) = '" + dto.getPsFolioBanco() + "'";
				sql += " 	And concepto = '" + dto.getPsConcepto() + "'";
				sql += " 	And referencia = '" + dto.getPsReferencia() + "'";
				sql += " 	And movto_arch = 1 ";
				sql += " 	And no_empresa = " + dto.getPlNoEmpresa() + "";
				sql += " 	And id_chequera = '" + dto.getPsIdChequera() + "'";
				//sSQL = sSql & chr(10) & " And convert(char(10),fec_valor,103) = '" & psFec_valor & "'"
				sql += " 	And convert(datetime,fec_valor, 103) = '" + funciones.ponerFechaSola(dto.getPsFecValor()) + "'";
				sql += " 	And float8(importe) = " + dto.getPdImporte() + "";
				sql += sqlAdicional;
				
				sql += " UNION ALL";
				sql += " SELECT movto_arch, no_empresa, id_banco, id_chequera ";
				sql += " FROM hist_movto_banca_e";
				sql += " WHERE rtrim(ltrim(folio_banco, ' ')) = '" + dto.getPsFolioBanco() + "'";
				sql += " 	And concepto = '" + dto.getPsConcepto() + "'";
				sql += " 	And referencia = '" + dto.getPsReferencia() + "'";
				sql += " 	And movto_arch = 1 ";
				sql += " 	And no_empresa = " + dto.getPlNoEmpresa() + "";
				sql += " 	And id_chequera = '" + dto.getPsIdChequera() + "'";
				sql += " 	And convert(datetime,fec_valor, 103) = '" + funciones.ponerFechaSola(dto.getPsFecValor()) + "'";
				sql += " 	And float8(importe) = " + dto.getPdImporte() + "";
				sql += sqlAdicional;
			} 
			
			list = jdbcTemplate.query(sql, new RowMapper(){
		    	public CatCtaBancoDto mapRow(ResultSet rs, int idx) throws SQLException {
		    		CatCtaBancoDto result = new CatCtaBancoDto();
		    		result.setNoEmpresa(rs.getInt("no_empresa"));
		    		result.setIdBanco(rs.getInt("id_banco"));
		    		result.setIdChequera(rs.getString("id_chequera"));
		    		
					return result;
				}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:Banca Electronica, C:AdministradorArchivosDao, M:seleccionarMovtoBancaE");
		}
		return list;
	}
	
	public String insertarMovtoBancaEStrNet(ParametroLayoutDto dto){
		String sql = "";
		
		if(dto.getPsEstatus() == null || dto.getPsEstatus().equals("")) dto.setPsEstatus("N");
		
		sql += "INSERT INTO movto_banca_e( ";
		sql += "	no_empresa, id_banco, id_chequera, secuencia, fec_valor, sucursal,";
		sql += "\n	folio_banco, referencia, cargo_abono, importe, b_salvo_buen_cobro, concepto,";
		sql += "\n	id_estatus_trasp, fec_alta, b_trasp_banco, b_trasp_conta, movto_arch, usuario_alta,";
		sql += "\n	descripcion, archivo";
		
		if(dto.getPsObservacion()!=null) sql += ", observacion";
	    if(dto.getPsCveConcepto()!=null) sql += ", id_cve_concepto";
	    if(dto.getPdSaldoBancario()>0) sql += ", saldo_bancario";
	    if(dto.getPlNoLineaArchivo() > 0) sql += ", no_linea_arch";
	    sql += ", id_estatus_cancela)";
	    
		sql += "\n	values(" + dto.getPlNoEmpresa() + ", " + dto.getPiBanco() + ", '" + dto.getPsIdChequera() +  "',";
		sql += " " + dto.getPsSecuencia() + ", '" + dto.getPsFecValor() + "', '" + dto.getPsSucursal() + "',";
		sql += "\n '" + dto.getPsFolioBanco() + "', '" + dto.getPsReferencia() + "', '" + dto.getPsCargoAbono() + "',";
		sql += " " + dto.getPdImporte() + ", '" + dto.getPsBSalvoBuenCobro() + "', '" + dto.getPsConcepto() + "',";
		sql += "\n '" + dto.getPsEstatus() + "', '" + dto.getPsFechaHoy() + "', 'N', 'N', 1, " + dto.getPsUsuarioAlta() + ",";
		sql += "\n '" + dto.getPsDescripcion() + "', '" + dto.getPsArchivos() + "'";
		
		if(dto.getPsObservacion() != null) sql += ", '" + dto.getPsObservacion() + "'";
	    if(dto.getPsCveConcepto() != null) sql += ", '" + dto.getPsCveConcepto() + "'";
	    if(dto.getPdSaldoBancario() > 0) sql += ", " + dto.getPdSaldoBancario() + "";
	    if(dto.getPlNoLineaArchivo() > 0) sql += ", " + dto.getPlNoLineaArchivo() + "";
	    sql += ", 0)";
	    
		return sql;
	}
	
	@SuppressWarnings ("unchecked")
	public List<MovtoBancaEDto> buscaBancoChequera(String psChequera)
	{
		String sql = "";
		List <MovtoBancaEDto> lista = new ArrayList<MovtoBancaEDto>();
		
		try
		{	
			sql += "Select ccb.no_empresa, ccb.id_banco, ccb.id_chequera, cb.desc_banco ";
			sql += "\n from cat_cta_banco ccb, cat_banco cb ";
			sql += "\n where ccb.id_chequera like '%" + psChequera + "' ";
			sql += "\n and ccb.id_banco = cb.id_banco ";
			
			lista = jdbcTemplate.query(sql, new RowMapper()
			{
				public MovtoBancaEDto mapRow(ResultSet rs, int idx) throws SQLException
				{
					MovtoBancaEDto result = new MovtoBancaEDto();
					result.setNoEmpresa(rs.getInt("no_empresa"));
					result.setIdBanco(rs.getInt("id_banco"));
					result.setIdChequera(rs.getString("id_chequera"));
					result.setDescChequera(rs.getString("desc_banco"));
					
					return result;
				}
			});			
					
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + toString() + "P:Banca Electronica, C:AdministradorArchivosDao, M:buscaBancoChequera");			
		}
		return lista;
	}	
	
	@SuppressWarnings ("unchecked")
	public List<MovtoBancaEDto> selectEquivaleDivisa(String psDivisa, String origen, boolean caracter)
	{
		String sql = "";
		List<MovtoBancaEDto> lista = new ArrayList<MovtoBancaEDto>();
	
		try
		{			
			sql += "Select codDivisaSET, codDivisaERP from desc_refcr_divisas ";
			sql += "where codEmp = '" + origen + "' ";
			
			if (caracter == true)
			{
				sql += "and codDivisaERP like '%" + psDivisa + "'";
			}
			else
			{
				sql += "and codDivisaERP = '" + psDivisa + "'";
			}			
			lista = jdbcTemplate.query(sql, new RowMapper()
			{
				public MovtoBancaEDto mapRow(ResultSet rs, int idx) throws SQLException
				{
					MovtoBancaEDto objCampos = new MovtoBancaEDto();
					objCampos.setCodDivisaSET(rs.getString("codDivisaSET"));
					objCampos.setCodDivisaERP(rs.getString("codDivisaERP"));
					return objCampos;					
				}
			});	
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + toString() + "P:BancaElectronica, C:AdministradorArchivosDao, M:selectEquivaleDivisa");
		}return lista;
	}
	
	@SuppressWarnings ("unchecked")
	public List<MovtoBancaEDto> selectMovtoBancaE(String psFolioBanco, String psConcepto, String psReferencia, 
												  int piNoEmpresa, String psChequera, String psFechaOperacion, String pdMonto)
	{		
		String sql = "";
		List<MovtoBancaEDto> lista = new ArrayList<MovtoBancaEDto>();
	
		try
		{
			sql += "Select movto_arch, secuencia, archivo ";
			sql += "\n from movto_banca_e ";
			sql += "\n where movto_arch = 1 and no_empresa = " + piNoEmpresa + " ";
			
			if (psConcepto.equals("MISMO BANCO BANCOMER"))
			{
				if (psFolioBanco.substring(0, 1).equals("'"))
				{
					sql += "\n and folio_banco in (" + psFolioBanco + ") and (archivo like 'md%' or archivo like 'SH%') ";
				}
				else
				{
					sql += "\n and folio_banco = '" + psFolioBanco + "' and (archivo like 'md%' or archivo like 'SH%') ";
				}
			}
			
			if (!psConcepto.equals("MISMO BANCO BANCOMER") && !psConcepto.equals("MISMO BANCO BANCOMER2"))
			{
				sql += "\n and concepto = '" + psConcepto + "' and (archivo like 'md%' or archivo like 'SH%') ";
			}
			else if (psConcepto.equals("MISMO BANCO BANCOMER2"))
			{
				sql += "\n and observacion = '" + psReferencia + "'";
			}
			
			sql += "\n and id_chequera = '" + psChequera + "' ";
			sql += "\n and fec_valor = '" + psFechaOperacion + "' ";
			sql += "\n and importe = " + pdMonto + " ";
			
			if (!psReferencia.equals(""))
			{
				sql += "\n and referencia like '%" + psReferencia + "%' ";
			}
			
			if (!psFolioBanco.equals(""))
			{
				sql += "\n and folio_banco = '" + psFolioBanco + "' ";
			}
			
			sql += "\n union all ";
			
			sql += "\n Select movto_arch, secuencia, archivo ";
			sql += "\n from hist_movto_banca_e ";
			sql += "\n where movto_arch = 1 and no_empresa = " + piNoEmpresa + " ";
			
			if (psConcepto.equals("MISMO BANCO BANCOMER"))
			{
				if (psFolioBanco.substring(0, 1).equals("'"))
				{
					sql += "\n and folio_banco in (" + psFolioBanco + ") and (archivo like 'md%' or archivo like 'SH%') ";
				}
				else
				{
					sql += "\n and folio_banco = '" + psFolioBanco + "' and (archivo like 'md%' or archivo like 'SH%') ";
				}
			}
			
			if (!psConcepto.equals("MISMO BANCO BANCOMER") && !psConcepto.equals("MISMO BANCO BANCOMER2"))
			{
				sql += "\n and concepto = '" + psConcepto + "' and (archivo like 'md%' or archivo like 'SH%') ";
			}
			else if (psConcepto.equals("MISMO BANCO BANCOMER2"))
			{
				sql += "\n and observacion = '" + psReferencia + "'";
			}
			
			sql += "\n and id_chequera = '" + psChequera + "' ";
			sql += "\n and fec_valor = '" + psFechaOperacion + "' ";
			sql += "\n and importe = " + pdMonto + " ";
			
			if (!psReferencia.equals(""))
			{
				sql += "\n and referencia like '%" + psReferencia + "%' ";
			}
			
			if (!psFolioBanco.equals(""))
			{
				sql += "\n and folio_banco = '" + psFolioBanco + "' ";
			}
						
			sql += "\n order by archivo ";
			
			lista = jdbcTemplate.query(sql, new RowMapper()
			{
				public MovtoBancaEDto mapRow(ResultSet rs, int idx) throws SQLException
				{					
					MovtoBancaEDto objCampos = new MovtoBancaEDto();
					objCampos.setMovtoArch(rs.getInt("movto_arch"));
					objCampos.setSecuencia(rs.getInt("secuencia"));
					objCampos.setArchivo(rs.getString("archivo"));
					return objCampos;					
				}
			});	
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + toString() + "P:BancaElectronica, C:AdministradorArchivosDao, M:selectEquivaleDivisa");
		}return lista;
	}
	
	
	@SuppressWarnings ("unchecked")
	public List<MovtoBancaEDto> selectRubro(String psConcepto)
	{
		String sql = "";
		List<MovtoBancaEDto> lista = new ArrayList<MovtoBancaEDto>();
		
		try
		{
			sql += "Select id_rubro ";
			sql += "\n from cat_rubro ";
			sql += "\n where cve_bancaria like '%" + psConcepto + "%' ";
			
			
			lista = jdbcTemplate.query(sql, new RowMapper()
			{
				public MovtoBancaEDto mapRow(ResultSet rs, int idx) throws SQLException
				{
					MovtoBancaEDto objCampos = new MovtoBancaEDto();
					objCampos.setIdRubro(rs.getDouble("id_rubro"));
					return objCampos;
				}
			});		  	
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + toString() + "P:BancaElectronica, C:AdministradorArchivosDao, M:selectRubro");
		}return lista;
	}
	
	@SuppressWarnings("unchecked")	
	public Date obtieneFechaHoy() 
	{		
		sb = new StringBuffer();
		sb.append(" SELECT fec_hoy FROM FECHAS ");
		try{
			 List<Date> fechas = jdbcTemplate.query(sb.toString(), new RowMapper() {
				public Date mapRow(ResultSet rs, int idx) throws SQLException {
					return rs.getDate("fec_hoy");
				}
			});
			return fechas.get(0);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BancaElectronica, C:AdministradorArchivosDao, M:obtieneFechaHoy");
			return null;
		}
	}
	
	public int insertarMovtoBancaE(MovtoBancaEDto dto)
	{
		String sql = "";
		
		try
		{
			if(dto.getIdEstatusTrasp() == null || dto.getIdEstatusTrasp().equals("")) dto.setIdEstatusTrasp("N");
			
			sql += "INSERT INTO movto_banca_e( ";
			sql += "	no_empresa, id_banco, id_chequera, secuencia, fec_valor, sucursal,";
			sql += "\n	folio_banco, referencia, cargo_abono, importe, b_salvo_buen_cobro, concepto,";
			sql += "\n	id_estatus_trasp, fec_alta, b_trasp_banco, b_trasp_conta, movto_arch, usuario_alta,";
			sql += "\n	descripcion, archivo";
						
			if(dto.getObservacion() != null) sql += ", observacion";
		    if(dto.getConcepto() != null) sql += ", id_cve_concepto";
		    if(dto.getSaldoBancario() > 0) sql += ", saldo_bancario";
		    if (dto.getIdRubro() > 0) sql += ", id_rubro";
		    if(dto.getNoLineaArch() > 0) sql += ", no_linea_arch";
		    sql += ", id_estatus_cancela)";	    
		    
			sql += "\n	values(" + dto.getNoEmpresa() + ", " + dto.getIdBanco() + ", '" + dto.getIdChequera() +  "',";
			sql += " " + dto.getSecuencia() + ", '" + dto.getFechaOperacion() + "', '" + dto.getSucursal() + "',";
			sql += "\n '" + dto.getFolioBanco() + "', '" + dto.getReferencia() + "', '" + dto.getCargoAbono() + "',";
			
			sql += "\n " + dto.getImporte() + ", ";
			
			sql += "\n '" + dto.getBSalvoBuenCobro() + "', '" + dto.getConcepto() + "',";
			sql += "\n '" + dto.getIdEstatusTrasp() + "', '" + funciones.ponerFecha(dto.getFecAlta()) + "', 'N', 'N', 1, " + dto.getUsuarioAlta() + ",";
			sql += "\n '" + dto.getDescripcion() + "', '" + dto.getArchivo() + "'";
			
			if(dto.getObservacion() != null) sql += ", '" + dto.getObservacion() + "'";
		    if(dto.getConcepto() != null) sql += ", '" + dto.getConcepto() + "'";
		    if(dto.getSaldoBancario() > 0) sql += ", " + dto.getSaldoBancario() + "";
		    if(dto.getIdRubro() > 0) sql += ", " + dto.getIdRubro() + "";
		    if(dto.getNoLineaArch() > 0) sql += ", " + dto.getNoLineaArch() + "";
		    sql += ", 0)";
		    	    
			return jdbcTemplate.update(sql);
		}
		catch(Exception e)
		{			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectronica, C:AdministradorArchivosDao, M:insertaMovtoBancaE");
			return 0;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<MovtoBancaEDto>selectMovtoBanca(Date fecHoy)
	{
		String sql = "";
		List<MovtoBancaEDto> lista = new ArrayList<MovtoBancaEDto>();
		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

		try
		{
			sql += "Select * from movto_banca_e ";
			sql += "\n where fec_alta = '" + formatoFecha.format(fecHoy) + "'";
			sql += "\n and cargo_abono not in ('', 'S', 'I') ";
			sql += "\n and referencia not in ('SALDO ULTIMA TRANS')";
			sql += "\n and id_estatus_trasp = 'N' ";
			
			System.out.println("query que busca en movto_banca_e la info: " + sql);
			
			lista = jdbcTemplate.query(sql, new RowMapper()
			{
				public MovtoBancaEDto mapRow(ResultSet rs, int idx) throws SQLException
				{
					MovtoBancaEDto objCampos = new MovtoBancaEDto();
					objCampos.setIdChequera(rs.getString("id_chequera"));
					objCampos.setObservacion(rs.getString("observacion"));
					objCampos.setImporte(rs.getDouble("importe"));
					objCampos.setSecuencia(rs.getInt("secuencia"));
					objCampos.setFecValor(rs.getDate("fec_valor"));
					objCampos.setFolioBanco(rs.getString("folio_banco"));
					return objCampos;
				}
			});
		}
		catch(Exception e)
		{
			System.out.println("esta entrando al catch");
			bitacora.insertarRegistro(new Date().toString() + " " + toString() + "P:BancaElectronica, C:AdministradorArchivosDao, M:selectMovtoBanca" );
		}
		System.out.println("tam�ao de lista return: " + lista.size());
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<MovtoBancaEDto>selectMovtoBancaIngresos(Date fecHoy) {
		String sql = "";
		List<MovtoBancaEDto> lista = new ArrayList<MovtoBancaEDto>();
		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

		try {
			sql += "select m.no_empresa, e.nom_empresa, cb.desc_banco, m.id_chequera, m.concepto, m.importe, 'DEPOSITO' as formaPago, m.referencia, cd.desc_divisa";
			sql += "\n from movto_banca_e m, empresa e, cat_banco cb, cat_cta_banco ccb, cat_divisa cd";
			sql += "\n where m.no_empresa = e.no_empresa";
			sql += "\n 		And m.id_banco = cb.id_banco";
			sql += "\n 		And m.id_chequera = ccb.id_chequera";
			sql += "\n 		And ccb.id_divisa = cd.id_divisa";
			sql += "\n 		And m.fec_alta = '" + formatoFecha.format(fecHoy) + "'";
			sql += "\n 		And m.cargo_abono in ('I')";
			sql += "\n 		And m.no_empresa not in (15, 16, 22, 23)";
			
			System.out.println("Query que busca en movto_banca_e los ingresos: " + sql);
			
			lista = jdbcTemplate.query(sql, new RowMapper(){
				public MovtoBancaEDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovtoBancaEDto objCampos = new MovtoBancaEDto();
					objCampos.setNoEmpresa(rs.getInt("no_empresa"));
					objCampos.setNomEmpresa(rs.getString("nom_empresa"));
					objCampos.setDescBanco(rs.getString("desc_banco"));
					objCampos.setIdChequera(rs.getString("id_chequera"));
					objCampos.setConcepto(rs.getString("concepto"));
					objCampos.setImporte(rs.getDouble("importe"));
					objCampos.setDescFormaPago(rs.getString("formaPago"));
					objCampos.setReferencia(rs.getString("referencia"));
					objCampos.setIdDivisa(rs.getString("desc_divisa"));
					return objCampos;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + toString() + "P:BancaElectronica, C:AdministradorArchivosDao, M:selectMovtoBancaIngresos" );
		}
		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public int confirmaMovimientos(List<MovtoBancaEDto> datos, int pos)
	{			
		List<MovtoBancaEDto> lista = new ArrayList<MovtoBancaEDto>();
		List<MovtoBancaEDto> lista2 = new ArrayList<MovtoBancaEDto>();
		String sql = "";
		String descripcion = "";
		String equivalePersona = "";
		String folioDet = "";
		int resultado = 0;
		int encuentraCadena = 0;
		double resultadoDouble = 0;
		
		try
		{
			//Buscamos en movimiento e hist_movimiento los registros por importe
			sql += "Select p.equivale_persona, m.* ";
			sql += "\n from movimiento m, persona p ";
			sql += "\n where id_chequera = '" + datos.get(pos).getIdChequera() + "' ";
			sql += "\n and m.importe = " + datos.get(pos).getImporte() + " ";
			sql += "\n and m.origen_mov_ant <> '' ";
			sql += "\n and m.id_estatus_mov = 'T' " ;
			sql += "\n and m.id_tipo_operacion = 3200 ";
			sql += "\n and m.no_cliente = p.no_persona ";
			sql += "\n and p.id_tipo_persona = 'P' ";
			sql += "\n union ";
			sql += "Select p.equivale_persona, m.* ";
			sql += "\n from hist_movimiento m, persona p ";
			sql += "\n where id_chequera = '" + datos.get(pos).getIdChequera() + "' ";
			sql += "\n and m.importe = " + datos.get(pos).getImporte() + " ";
			sql += "\n and m.origen_mov_ant <> '' ";
			sql += "\n and m.id_estatus_mov = 'T' " ;
			sql += "\n and m.id_tipo_operacion = 3200 ";
			sql += "\n and m.no_cliente = p.no_persona ";
			sql += "\n and p.id_tipo_persona = 'P' ";
			
			lista = jdbcTemplate.query(sql, new RowMapper()
			{
				public MovtoBancaEDto mapRow(ResultSet rs, int idx) throws SQLException
				{
					MovtoBancaEDto objCampos = new MovtoBancaEDto();
					objCampos.setDescripcion(rs.getString("descripcion"));
					objCampos.setObservacion(rs.getString("observacion"));
					objCampos.setFolioDet(rs.getInt("no_folio_det"));
					objCampos.setEquivalePersona(rs.getString("equivale_persona"));
					objCampos.setImporte(rs.getDouble("importe"));
					return objCampos;
				}
			});
			
			//Si encontro datos actualiza movto_banca, movimiento e hist_movimiento
			if (lista.size() > 0)
			{				
				for (int clista = 0; clista < lista.size(); clista++)
				{
					descripcion = lista.get(clista).getDescripcion();
					descripcion = descripcion.replace(" ", "");
					descripcion = descripcion.replace("/", "");
					descripcion = descripcion.replace("-", "");
					descripcion = funciones.elmiminarEspaciosAlaDerecha(descripcion);
					
					folioDet = Integer.toString(lista.get(clista).getFolioDet());
					
					equivalePersona = lista.get(clista).getEquivalePersona();			
					
					for(int k = 0; k < equivalePersona.length()-1; k++)
					{
						if (equivalePersona.substring(0, 1).equals("0"))
						{
							equivalePersona = equivalePersona.substring(1);
						}
						else
						{
							break;
						}
					}					
					descripcion = funciones.elmiminarEspaciosAlaDerecha(equivalePersona) + descripcion;
					encuentraCadena = datos.get(pos).getReferencia().indexOf(descripcion);
					if (encuentraCadena > 0 && datos.get(pos).getImporte() == lista.get(clista).getImporte())
					{
						//Confirma MovtoBancaE
						sql = "";
						sql += "Update movto_banca_e set id_estatus_trasp = 'T' ";
						sql += "where importe = " + datos.get(pos).getImporte() + "";
						sql += "and secuencia = '" + datos.get(pos).getSecuencia() + "'";
						
						resultado = jdbcTemplate.update(sql);
						
						//Confirma Movimiento o Hist_movimiento
						sql = "";
						sql += "Update movimiento Set ";
						sql += "id_estatus_mov = 'K', b_gen_contable = 'C', ";
						sql += "fec_valor = '" + datos.get(pos).getFecValor() + "', ";
						sql += "fec_conf_trans = '" + datos.get(pos).getFecValor() + "' ";
						sql += "where no_folio_det = " + folioDet + "";
						
						resultado = jdbcTemplate.queryForInt(sql);
						
						if (resultado <= 0)
						{
							sql = "";
							sql += "Update hist_movimiento Set ";
							sql += "id_estatus_mov = 'K', b_gen_contable = 'C', ";
							sql += "fec_valor = '" + datos.get(pos).getFecValor() + "', ";
							sql += "fec_conf_trans = '" + datos.get(pos).getFecValor() + "' ";
							sql += "where no_folio_det = " + folioDet + "";
							
							resultado = jdbcTemplate.update(sql);
						}
					}
				}
			}
			else
			{//Si no encontro informacion con el importe especificado, se busca solo los diferentes foliadores
				sql = "";
				sql += "Select distinct origen_mov_ant  ";
				sql += "\n from movimiento ";
				sql += "\n where id_chequera = '" + datos.get(pos).getIdChequera() + "' ";
				sql += "\n and fec_operacion = '" + datos.get(pos).getFecValor() + "' ";
				sql += "\n and m.origen_mov_ant <> '' ";
				sql += "\n and m.id_estatus_mov = 'T' " ;
				sql += "\n and m.id_tipo_operacion = 3200 ";
				sql += "\n union ";
				sql += "Select distinct origen_mov_ant  ";
				sql += "\n from hist_movimiento ";
				sql += "\n where id_chequera = '" + datos.get(pos).getIdChequera() + "' ";
				sql += "\n and fec_operacion = '" + datos.get(pos).getFecValor() + "' ";
				sql += "\n and m.origen_mov_ant <> '' ";
				sql += "\n and m.id_estatus_mov = 'T' " ;
				sql += "\n and m.id_tipo_operacion = 3200 ";
				
				lista2 = jdbcTemplate.query(sql, new RowMapper()
				{
					public MovtoBancaEDto mapRow(ResultSet rs, int idx) throws SQLException
					{
						MovtoBancaEDto objCampos = new MovtoBancaEDto();
						objCampos.setOrigenMovAnt(rs.getString("origen_mov_ant"));
						return objCampos;
					}
				});
				
				if (lista2.size() > 0)
				{
					for (int clista2 = 0; clista2 < lista2.size() - 1; clista2++)
					{
						sql = "";
						sql += "Select sum(importe) as importe ";
						sql += "\n from movimiento ";
						sql += "\n where id_chequera = '" + datos.get(pos).getIdChequera() + "' ";
						sql += "\n and fec_operacion = '" + datos.get(pos).getFecValor() + "' ";
						sql += "\n and origen_mov_ant = '" + lista2.get(clista2).getOrigenMovAnt() + "' ";
						sql += "\n and id_estatus_mov = 'T' ";
						sql += "\n and id_tipo_operacion = 3200 ";
						sql += "\n union ";
						sql += "\n Select sum(importe) as importe ";
						sql += "\n from hist_movimiento ";
						sql += "\n where id_chequera = '" + datos.get(pos).getIdChequera() + "' ";
						sql += "\n and fec_operacion = '" + datos.get(pos).getFecValor() + "' ";
						sql += "\n and origen_mov_ant = '" + lista2.get(clista2).getOrigenMovAnt() + "' ";
						sql += "\n and id_estatus_mov = 'T' ";
						sql += "\n and id_tipo_operacion = 3200 ";
						
						resultadoDouble = jdbcTemplate.queryForInt(sql);
						
						if (resultadoDouble > 0)
						{
							if (datos.get(pos).getImporte() == resultadoDouble)
							{
								//Confirma MovtoBancaE
								sql = "";
								sql += "Update movto_banca_e set id_estatus_trasp = 'T' ";
								sql += "where importe = " + datos.get(pos).getImporte() + " ";
								sql += "and secuencia = '" + datos.get(pos).getSecuencia() + "' ";
								
								resultado = jdbcTemplate.update(sql);
								
								//Confirma movimiento o hist_movimiento
								sql = "";
								sql += "Update movimiento Set ";
								sql += "id_estatus_mov = 'K', ";
								sql += "b_gen_contable = 'C', ";
								sql += "fec_valor = '" + datos.get(pos).getFecValor() + "', ";
								sql += "fec_conf_trans = '" + datos.get(pos).getFecValor() + "' ";
								sql += "where no_folio_det in (select no_folio_det from movimiento ";
								sql += "\n where id_chequera = '" + datos.get(pos).getIdChequera() + "' ";
								sql += "\n and fec_operacion = '" + datos.get(pos).getFecValor() + "' ";
								sql += "\n and origen_mov_ant = '" + lista2.get(clista2).getOrigenMovAnt() + "' ";
								sql += "\n and id_estatus_mov = 'T' and id_tipo_operacion = 3200 ";
								sql += "\n union ";
								sql += "\n select no_folio_det from hist_movimiento ";
								sql += "\n where id_chequera = '" + datos.get(pos).getIdChequera() + "' ";
								sql += "\n and fec_operacion = '" + datos.get(pos).getFecValor() + "' ";
								sql += "\n and origen_mov_ant = '" + lista2.get(clista2).getOrigenMovAnt() + "' ";
								sql += "\n and id_estatus_mov = 'T' and id_tipo_operacion = 3200) ";
								
								resultado = jdbcTemplate.update(sql);
								
								if (resultado <= 0)
								{
									sql = "";
									sql += "Update hist_movimiento Set ";
									sql += "id_estatus_mov = 'K', ";
									sql += "b_gen_contable = 'C', ";
									sql += "fec_valor = '" + datos.get(pos).getFecValor() + "', ";
									sql += "fec_conf_trans = '" + datos.get(pos).getFecValor() + "' ";
									sql += "where no_folio_det in (select no_folio_det from movimiento ";
									sql += "\n where id_chequera = '" + datos.get(pos).getIdChequera() + "' ";
									sql += "\n and fec_operacion = '" + datos.get(pos).getFecValor() + "' ";
									sql += "\n and origen_mov_ant = '" + lista2.get(clista2).getOrigenMovAnt() + "' ";
									sql += "\n and id_estatus_mov = 'T' and id_tipo_operacion = 3200 ";
									sql += "\n union ";
									sql += "\n select no_folio_det from hist_movimiento ";
									sql += "\n where id_chequera = '" + datos.get(pos).getIdChequera() + "' ";
									sql += "\n and fec_operacion = '" + datos.get(pos).getFecValor() + "' ";
									sql += "\n and origen_mov_ant = '" + lista2.get(clista2).getOrigenMovAnt() + "' ";
									sql += "\n and id_estatus_mov = 'T' and id_tipo_operacion = 3200) ";
									
									resultado = jdbcTemplate.update(sql);
								}
							}
						}
					}
				}
				
			}
		}
		catch(Exception e)
		{			
			bitacora.insertarRegistro(new Date().toString() + " " + toString() + "P:BancaElectronica, C:AdministradorArchivosDao, M:confirmaMovimientos");
			return 0;
		}
		return resultado;
	}
	
	public int getSeq(String tabla, String campo) {
		gral = new ConsultasGenerales(jdbcTemplate);
		return gral.getSeq(tabla, campo);
	}
	
	@SuppressWarnings("unchecked")
	public int confirmacionAutomatica(List<MovtoBancaEDto> datos, int totalRegistros) {
		List<MovtoBancaEDto> lista = new ArrayList<MovtoBancaEDto>();
		String sql = "";
		String equivalePersona = "";
		String folioDet = "";
		int resultado = 0;
		SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
		
		StringBuffer sql2 = new StringBuffer();
		
		try {
			for(int pos = 0; pos <= totalRegistros; pos++){
				//Buscamos en movimiento e hist_movimiento los registros por importe
				sql = "";
				sql += "Select p.equivale_persona, m.* ";
				sql += "\n from movimiento m, persona p ";
				sql += "\n where id_chequera = '" + datos.get(pos).getIdChequera() + "' ";
				sql += "\n and m.importe = " + datos.get(pos).getImporte() + " ";
				sql += "\n and m.id_estatus_mov = 'T' " ;
				sql += "\n and m.id_tipo_operacion = 3200 ";
				sql += "\n and m.no_cliente = p.no_persona ";
				sql += "\n and p.id_tipo_persona = 'P' ";
				sql += "\n union ";
				sql += "Select p.equivale_persona, m.* ";
				sql += "\n from hist_movimiento m, persona p ";
				sql += "\n where id_chequera = '" + datos.get(pos).getIdChequera() + "' ";
				sql += "\n and m.importe = " + datos.get(pos).getImporte() + " ";				
				sql += "\n and m.id_estatus_mov = 'T' " ;
				sql += "\n and m.id_tipo_operacion = 3200 ";
				sql += "\n and m.no_cliente = p.no_persona ";
				sql += "\n and p.id_tipo_persona = 'P' ";
				
				lista = jdbcTemplate.query(sql, new RowMapper()
				{
					public MovtoBancaEDto mapRow(ResultSet rs, int idx) throws SQLException
					{
						MovtoBancaEDto objCampos = new MovtoBancaEDto();
						objCampos.setDescripcion(rs.getString("descripcion"));
						objCampos.setObservacion(rs.getString("observacion"));
						objCampos.setFolioDet(rs.getInt("no_folio_det"));
						objCampos.setEquivalePersona(rs.getString("equivale_persona"));
						objCampos.setImporte(rs.getDouble("importe"));
						return objCampos;
					}
				});
				
				//Si encontro datos actualiza movto_banca, movimiento e hist_movimiento
				if (lista.size() > 0) {
					for (int clista = 0; clista < lista.size(); clista++) {
						folioDet = Integer.toString(lista.get(clista).getFolioDet());
						
						equivalePersona = lista.get(clista).getEquivalePersona();			
						
						for(int k = 0; k < equivalePersona.length()-1; k++) {
							if (equivalePersona.substring(0, 1).equals("0")) {
								equivalePersona = equivalePersona.substring(1);
							}else { break; }
						}
						
						if (datos.get(pos).getImporte() == lista.get(clista).getImporte()) {
							//Confirma MovtoBancaE
							sql = "";
							sql += "Update movto_banca_e set id_estatus_trasp = 'T' ";
							sql += "where importe = " + datos.get(pos).getImporte() + "";
							sql += "and secuencia = '" + datos.get(pos).getSecuencia() + "'";
							
							resultado = jdbcTemplate.update(sql);
							
							//Confirma Movimiento o Hist_movimiento
							sql = "";
							sql += "Update movimiento Set ";
							sql += "id_estatus_mov = 'K', b_gen_contable = 'C', ";
							sql += "fec_valor = '" + formato.format(datos.get(pos).getFecValor()) + "', ";
							sql += "fec_conf_trans = '" + formato.format(datos.get(pos).getFecValor()) + "' ";
							sql += "where no_folio_det = " + folioDet + "";
							
							resultado = jdbcTemplate.update(sql);
							
							if (resultado <= 0) {
								sql = "";
								sql += "Update hist_movimiento Set ";
								sql += "id_estatus_mov = 'K', b_gen_contable = 'C', ";
								sql += "fec_valor = '" + datos.get(pos).getFecValor() + "', ";
								sql += "fec_conf_trans = '" + datos.get(pos).getFecValor() + "' ";
								sql += "where no_folio_det = " + folioDet + "";
								
								resultado = jdbcTemplate.update(sql);
								
								sql2 = new StringBuffer();
								sql2.append(" UPDATE hist_movimiento SET \n");
								sql2.append(" 	fec_conf_trans = '" + formato.format(datos.get(pos).getFecValor()) + "' \n");
								sql2.append(" WHERE grupo_pago = "+ folioDet +" \n");
								sql2.append(" 	And id_tipo_operacion = 3201 \n");
								
								resultado = jdbcTemplate.update(sql2.toString());
							}
							sql2 = new StringBuffer();
							sql2.append(" UPDATE movimiento SET \n");
							sql2.append(" 	fec_conf_trans = '" + formato.format(datos.get(pos).getFecValor()) + "' \n");
							sql2.append(" WHERE grupo_pago = "+ folioDet +" \n");
							sql2.append(" 	And id_tipo_operacion = 3201 \n");
							
							resultado = jdbcTemplate.update(sql2.toString());
							
							if(buscaEmpresa(folioDet) != 0)
								envioMail(folioDet);
						}
					}
				}				
			}
		} catch(Exception e) {			
			bitacora.insertarRegistro(new Date().toString() + " " + toString() + "P:BancaElectronica, C:AdministradorArchivosDao, M: confirmacionAutomatica");
			return 0;
		}
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public int confirmacionAutomaticaCheques(List<MovtoBancaEDto> datos, int totalRegistros)
	{			
		List<MovtoBancaEDto> lista = new ArrayList<MovtoBancaEDto>();
		String sql = "";
		String folioDet = "";
		int resultado = 0;
		SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
		
		try
		{
			for(int pos = 0; pos <= totalRegistros; pos++){
				//Busca;mos en movimiento e hist_movimiento los registros por importe
				sql = "";
				sql += "Select m.* ";
				sql += "\n from vista_movimiento m";
				sql += "\n where m.id_chequera = '" + datos.get(pos).getIdChequera() + "' ";
				sql += "\n and m.importe = " + datos.get(pos).getImporte() + " ";				
				sql += "\n and m.id_estatus_mov = 'I' " ;
				sql += "\n and m.id_forma_pago = 1 ";
				sql += "\n and m.id_tipo_operacion = 3200 ";
				sql += "\n and (m.b_gen_contable <> 'C' or m.b_gen_contable IS NULL) ";
				sql += "\n and m.fec_valor between '01-12-2008' and '"+ formato.format(datos.get(pos).getFecValor()) +"' ";
								
				lista = jdbcTemplate.query(sql, new RowMapper()
				{
					public MovtoBancaEDto mapRow(ResultSet rs, int idx) throws SQLException
					{
						MovtoBancaEDto objCampos = new MovtoBancaEDto();
						/*objCampos.setDescripcion(rs.getString("descripcion"));
						objCampos.setObservacion(rs.getString("observacion"));*/
						objCampos.setFolioDet(rs.getInt("no_folio_det"));
						//objCampos.setEquivalePersona(rs.getString("equivale_persona"));
						objCampos.setImporte(rs.getDouble("importe"));
						objCampos.setNoCheque(rs.getInt("no_cheque"));
						return objCampos;
					}
				});
				
				//Si encontro datos actualiza movto_banca, movimiento e hist_movimiento
				if (lista.size() > 0)
				{					
					for (int clista = 0; clista < lista.size(); clista++)
					{				
						folioDet = Integer.toString(lista.get(clista).getFolioDet());
						
						if (datos.get(pos).getImporte() == lista.get(clista).getImporte())
						{
							//Confirma MovtoBancaE
							sql = "";
							sql += "Update movto_banca_e set id_estatus_trasp = 'T', ";
							sql += "\n folio_det_conf = "+ folioDet +" ";
							sql += "\n where importe = " + datos.get(pos).getImporte() + "";
							sql += "\n and secuencia = '" + datos.get(pos).getSecuencia() + "'";
							
							resultado = jdbcTemplate.update(sql);
							
							//Confirma Movimiento o Hist_movimiento
							sql = "";
							sql += "Update movimiento Set ";
							sql += "\n b_gen_contable = 'C', b_entregado = 'S', ";
							sql += "\n fec_valor_original = '" + formato.format(datos.get(pos).getFecValor()) + "', ";
							sql += "\n referencia = '"+ lista.get(clista).getNoCheque() +"' ";
							sql += "\n where no_folio_det = " + folioDet + "";
														
							resultado = jdbcTemplate.update(sql);
							
							if (resultado <= 0)
							{
								sql = "";
								sql += "Update hist_movimiento Set ";
								sql += "\n b_gen_contable = 'C', b_entregado = 'S', ";
								sql += "\n fec_valor_original = '" + formato.format(datos.get(pos).getFecValor()) + "', ";
								sql += "\n referencia = '"+ datos.get(pos).getNoCheque() +"' ";
								sql += "\n where no_folio_det = " + folioDet + "";								
								
								resultado = jdbcTemplate.update(sql);
							}
							
							if(buscaEmpresa(folioDet) != 0)
								envioMail(folioDet);
						}
					}
				}				
			}
		}catch(Exception e) {			
			bitacora.insertarRegistro(new Date().toString() + " " + toString() + "P:BancaElectronica, C:AdministradorArchivosDao, M: confirmacionAutomatica");
			return 0;
		}
		return resultado;
	}
	
	@SuppressWarnings("unchecked")
	public void envioMail(String folioDet) {
		StringBuffer sql = new StringBuffer();
		List<MovtoBancaEDto> listPago = new ArrayList<MovtoBancaEDto>(); 
		List<MovtoBancaEDto> listMail = new ArrayList<MovtoBancaEDto>();
		List<MovtoBancaEDto> listReceptor = new ArrayList<MovtoBancaEDto>();
		List<MovtoBancaEDto> listUsrCopia = new ArrayList<MovtoBancaEDto>();
		List<MovtoBancaEDto> listCopia = new ArrayList<MovtoBancaEDto>();
		
		String mailReceptor;
		String mailCopia;
		String asunto;
		String cuerpo;
		
		gral = new ConsultasGenerales(jdbcTemplate);
		
		try {
			//datos del mail
			sql.append(" SELECT top 1 m.no_folio_det,m.fec_valor, case when m.id_forma_pago = 3 then cb.desc_banco else '' end as desc_banco, m.referencia, coalesce(m.id_chequera_benef, '') as id_chequera_benef,  \n");
			sql.append(" 	cd.desc_divisa,m.tipo_cambio, m.beneficiario, m.no_factura, m.importe, m.concepto, m.no_cliente, e.nom_empresa, cfp.desc_forma_pago \n");
			sql.append(" FROM movimiento m, cat_banco cb, cat_divisa cd, empresa e, cat_forma_pago cfp \n");
			sql.append(" WHERE m.id_banco_benef = case when m.id_forma_pago = 3 then cb.id_banco else 0 end \n");
			sql.append(" 	And m.id_divisa = cd.id_divisa \n");
			sql.append(" 	And m.no_empresa = e.no_empresa \n");
			sql.append(" 	And m.id_forma_pago = cfp.id_forma_pago \n");
			sql.append(" 	And m.no_folio_det = "+ folioDet +" \n");
			sql.append(" UNION ALL \n");
			sql.append(" SELECT top 1 m.no_folio_det,m.fec_valor, case when m.id_forma_pago = 3 then cb.desc_banco else '' end as desc_banco, m.referencia, coalesce(m.id_chequera_benef, '') as id_chequera_benef,  \n");
			sql.append(" 	cd.desc_divisa,m.tipo_cambio, m.beneficiario, m.no_factura, m.importe, m.concepto, m.no_cliente, e.nom_empresa, cfp.desc_forma_pago \n");
			sql.append(" FROM hist_movimiento m, cat_banco cb, cat_divisa cd, empresa e, cat_forma_pago cfp \n");
			sql.append(" WHERE m.id_banco_benef = case when m.id_forma_pago = 3 then cb.id_banco else 0 end \n");
			sql.append(" 	And m.id_divisa = cd.id_divisa \n");
			sql.append(" 	And m.no_empresa = e.no_empresa \n");
			sql.append(" 	And m.id_forma_pago = cfp.id_forma_pago \n");
			sql.append(" 	And m.no_folio_det = "+ folioDet +" \n");
			sql.append(" ORDER BY m.no_folio_det ");
			
			listPago = jdbcTemplate.query(sql.toString(), new RowMapper() {
				public MovtoBancaEDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovtoBancaEDto objCampos = new MovtoBancaEDto();
					gral = new ConsultasGenerales(jdbcTemplate);
					
					//objCampos.setFecValor(gral.fechaHoy());
					objCampos.setDescBanco(rs.getString("desc_banco"));
					objCampos.setReferencia(rs.getString("referencia"));
					objCampos.setIdChequera(rs.getString("id_chequera_benef"));
					objCampos.setIdDivisa(rs.getString("desc_divisa"));
					objCampos.setTipoCambio(rs.getString("tipo_cambio"));
					objCampos.setBeneficiario(rs.getString("beneficiario"));
					objCampos.setNoFactura(rs.getString("no_factura"));
					objCampos.setImporte(rs.getDouble("importe"));
					objCampos.setConcepto(rs.getString("concepto"));
					objCampos.setNoPersona(rs.getInt("no_cliente"));
					objCampos.setNomEmpresa(rs.getString("nom_empresa"));
					objCampos.setDescFormaPago(rs.getString("desc_forma_pago"));
					return objCampos;
				}
			});
			
			//armado del mail
			if(listPago.size() > 0) {
				listMail = cuerpoMail();
				
				listReceptor = mailsReceptores(listPago.get(0).getNoPersona());
				
				listUsrCopia = usuarioMailsCopia();
				
				listCopia = mailsCopia(listUsrCopia.get(0).getCorreosCopia());
				
				if(listReceptor.size() <= 0){
					mailReceptor = listCopia.get(0).getCorreosCopia();
					mailCopia = "";
				}else{
					mailReceptor = listReceptor.get(0).getCorreoElectronico();
					mailCopia = listCopia.get(0).getCorreosCopia();
				}
				asunto = listMail.get(0).getAvisoMail();
				
				cuerpo = armaMails(listReceptor, listMail, listPago);
				
				funciones.enviaMail(mailReceptor,mailCopia,asunto,cuerpo);
			}
			
		}catch(Exception e) {			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectronica, C:AdministradorArchivosDao, M: envioMail");
		}
	}
	
	@SuppressWarnings("unchecked")
	public int buscaEmpresa(String folioDet) {
		int resp = 0;
		StringBuffer sql = new StringBuffer();
		List<MovtoBancaEDto> list = new ArrayList<MovtoBancaEDto>();
		
		try {
			sql.append(" SELECT * FROM movimiento \n");
			sql.append(" WHERE no_folio_det = "+ folioDet +" \n");
			sql.append(" 	And no_empresa not in (15, 16, 22, 23) \n");
			sql.append(" UNION ALL \n");
			sql.append(" SELECT * FROM hist_movimiento \n");
			sql.append(" WHERE no_folio_det = "+ folioDet +" \n");
			sql.append(" 	And no_empresa not in (15, 16, 22, 23) ");
			
			System.out.print("Query busca empresa: " + sql.toString());
			
			list = jdbcTemplate.query(sql.toString(), new RowMapper() {
				public MovtoBancaEDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovtoBancaEDto campos = new MovtoBancaEDto();
					campos.setNoEmpresa(rs.getInt("no_empresa"));
					return campos;
				}
			});
		
			if(list.size() > 0) resp = 1;
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectronica, C:AdministradorArchivosDao, M: buscaEmpresa");
			return 0;
		}
		return resp;
	}
	
	@SuppressWarnings("unchecked")
	public List<MovtoBancaEDto> cuerpoMail() {
		StringBuffer sqlMail = new StringBuffer();
		List<MovtoBancaEDto> listMail = new ArrayList<MovtoBancaEDto>();
		
		try {
			//cuerpo del mail
			sqlMail.append(" SELECT parrafo_1 as aviso, parrafo_2 as introduccion, parrafo_3 as fecha, \n");
			sqlMail.append(" 		parrafo_5 as referencia, parrafo_7 as divisa, parrafo_8 as cambio, \n");
			sqlMail.append(" 		parrafo_9 as beneficiario, parrafo_11 as factura, parrafo_15 as importe, \n");
			sqlMail.append(" 		parrafo_18 as formaPago, parrafo_19 as caracter, parrafo_20 as despedida, \n");
			sqlMail.append(" 		parrafo_21 as empresaPag, parrafo_23 as banco, parrafo_24 as chequera, parrafo_25 as concepto \n");
			sqlMail.append(" FROM configura_mail \n");
			sqlMail.append(" WHERE no_correo = 1 ");
			
			listMail = jdbcTemplate.query(sqlMail.toString(), new RowMapper() {
				public MovtoBancaEDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovtoBancaEDto objCampos2 = new MovtoBancaEDto();
					
					objCampos2.setAvisoMail(rs.getString("aviso"));
					objCampos2.setIntroMail(rs.getString("introduccion"));
					objCampos2.setFecMail(rs.getString("fecha"));
					objCampos2.setReferenciaMail(rs.getString("referencia"));
					objCampos2.setDivisaMail(rs.getString("divisa"));
					objCampos2.setTipoCambioMail(rs.getString("cambio"));
					objCampos2.setBeneficMail(rs.getString("beneficiario"));
					objCampos2.setNoFacturaMail(rs.getString("factura"));
					objCampos2.setImporteMail(rs.getString("importe"));
					objCampos2.setDescFormaPago(rs.getString("formaPago"));
					objCampos2.setCaracterMail(rs.getString("caracter"));
					objCampos2.setDespedidaMail(rs.getString("despedida"));
					objCampos2.setNomEmpresa(rs.getString("empresaPag"));
					objCampos2.setBancoMail(rs.getString("banco"));
					objCampos2.setCuentaMail(rs.getString("chequera"));
					objCampos2.setConceptoMail(rs.getString("concepto"));
					return objCampos2;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectronica, C:AdministradorArchivosDao, M: cuerpoMail");
			return null;
		}
		return listMail;
	}
	
	@SuppressWarnings("unchecked")
	public List<MovtoBancaEDto> mailsReceptores(int noPersona) {
		StringBuffer sqlReceptor = new StringBuffer();
		List<MovtoBancaEDto> listMailReceptor = new ArrayList<MovtoBancaEDto>();
		
		try {
			//mail receptor
			sqlReceptor.append(" select contacto from medios_contacto mc, persona p \n");
			sqlReceptor.append(" where mc.no_persona = p.no_persona \n");
			sqlReceptor.append("  AND p.no_persona = "+ noPersona+" ");
			
			listMailReceptor = jdbcTemplate.query(sqlReceptor.toString(), new RowMapper() {
				public MovtoBancaEDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovtoBancaEDto objCampos3 = new MovtoBancaEDto();
					
					objCampos3.setCorreoElectronico(rs.getString("contacto"));
					
					return objCampos3;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectronica, C:AdministradorArchivosDao, M: mailsReceptores");
			return null;
		}
		return listMailReceptor;
	}
	
	@SuppressWarnings("unchecked")
	public List<MovtoBancaEDto> usuarioMailsCopia() {
		StringBuffer sqlCopia = new StringBuffer();
		List<MovtoBancaEDto> listMailCopia = new ArrayList<MovtoBancaEDto>();
		
		try {
			//mail copia
			sqlCopia.append(" select valor from configura_set where indice = 3010 \n");
			
			listMailCopia = jdbcTemplate.query(sqlCopia.toString(), new RowMapper() {
				public MovtoBancaEDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovtoBancaEDto objCampos4 = new MovtoBancaEDto();
					
					objCampos4.setCorreosCopia(rs.getString("valor"));
					
					return objCampos4;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectronica, C:AdministradorArchivosDao, M: usuarioMailsCopia");
			return null;
		}
		return listMailCopia;
	}
	
	@SuppressWarnings("unchecked")
	public List<MovtoBancaEDto> mailsCopia(String usuariosM) {
		StringBuffer sqlCopia = new StringBuffer();
		List<MovtoBancaEDto> listMailCopia = new ArrayList<MovtoBancaEDto>();
		
		try {
			sqlCopia.append(" DECLARE @result VARCHAR(MAX) = ''\n");
			sqlCopia.append(" SELECT @result = @result +';' + su.correo_electronico FROM SEG_USUARIO su where clave_usuario in("+ usuariosM +")\n");
			sqlCopia.append(" SET @result = SUBSTRING(@result,2,LEN(@result))\n");
			sqlCopia.append(" select @result as correos");
			
			System.out.print("query de mail copia: " + sqlCopia.toString());
			
			listMailCopia = jdbcTemplate.query(sqlCopia.toString(), new RowMapper() {
				public MovtoBancaEDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovtoBancaEDto objCampos4 = new MovtoBancaEDto();
					
					objCampos4.setCorreosCopia(rs.getString("correos"));
					
					return objCampos4;
				}
			});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectronica, C:AdministradorArchivosDao, M: mailsCopia");
			return null;
		}
		return listMailCopia;
	}
	
	public String armaMails(List<MovtoBancaEDto> listReceptor, List<MovtoBancaEDto> listMail, 
			List<MovtoBancaEDto> listPago) {
		
		String cuerpo = "";
		
		try {
			cuerpo = listMail.get(0).getIntroMail() + "\n\n";
			cuerpo += listMail.get(0).getNomEmpresa() + "  " + listPago.get(0).getNomEmpresa() + "\n\n";
			cuerpo += listMail.get(0).getBeneficMaill() + " " + listPago.get(0).getBeneficiario() + "\n";
			cuerpo += listMail.get(0).getNoFacturaMail() + " " + listPago.get(0).getNoFactura() + "\n";
			cuerpo += listMail.get(0).getConceptoMail() + " "+ listPago.get(0).getConcepto() + "\n";
			cuerpo += listMail.get(0).getImporteMail() + " " + listPago.get(0).getImporte() + "\n";
			cuerpo += listMail.get(0).getDescFormaPago() + "  " + listPago.get(0).getDescFormaPago() + "\n";
			cuerpo += listMail.get(0).getBancoMail() + "  " + listPago.get(0).getDescBanco() + "\n";
			cuerpo += listMail.get(0).getCuentaMail() + "  " + listPago.get(0).getIdChequera() + "\n";
			cuerpo += listMail.get(0).getReferenciaMail() + "  " + listPago.get(0).getReferencia() + "\n";
			cuerpo += listMail.get(0).getDivisaMail() + " " + listPago.get(0).getIdDivisa() + "\n";
			cuerpo += listMail.get(0).getTipoCambioMail() + " " + listPago.get(0).getTipoCambio() + "\n\n";
			cuerpo += listMail.get(0).getCaracterMail() + "\n\n";
			cuerpo += listMail.get(0).getDespedidaMail();

		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectronica, C:AdministradorArchivosDao, M: armaMails");
			return "";
		}
		return cuerpo;
	}
	
	public int selecionarMovtoBancaEProcedimiento() {
		int result = 0;
		String var = "''";
		try{
			HashMap<Object, Object> inParams = new HashMap<Object, Object>();
			HashMap<Object, Object> inParam = new HashMap<Object, Object>();
			HashMap<Object, Object> inParamsImporta = new HashMap<Object, Object>();
	
			
			StoredProcedure sp = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_cob_lee_citibank") {};
			sp.execute(inParams);
			
			StoredProcedure spc = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_llamaConfirmador") {};
			spc.declareParameter(new SqlParameter("result", Types.INTEGER)); 
            inParam.put("result", 0);
            spc.declareParameter(new SqlParameter("mensaje", Types.VARCHAR)); 
            inParam.put("mensaje", var);
			spc.execute((Map)inParam);
			
			StoredProcedure spImporta = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_consultaMovimientosBancaE") {};
			spImporta.execute(inParamsImporta);
				  
			result = 1;
		}
		catch(Exception e){
			e.printStackTrace();
			logger.error(e);
			result = -1;
		}
		return result;
	}
	@SuppressWarnings("unchecked")
	public int insertRutaCompleta(File nombresArch) {
		int result=0;
		
		try {
			sb = new StringBuffer();
			sb.append(" Insert into archivos");
			sb.append("\n	(archivo)");
			sb.append("\n VALUES('" + nombresArch.getAbsolutePath()+ "')");
			
			System.out.print("insertRutaCompleta: " + sb);
			logger.info("insertRutaCompleta: " + sb);

			result = jdbcTemplate.update(sb.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			
		}
		return result;
	}
	
	public int insertLinea(String psRegistro) {
		int result=0;
		
		sb = new StringBuffer();
		sb.append(" Insert into cob_lineas_archivos");
		sb.append("\n	(linea)");
		sb.append("\n VALUES('" + psRegistro+ "')");
		
		//System.out.print(sb);
		
		try {
			//for(int i=0; i<linea.length; i++){
			result = jdbcTemplate.update(sb.toString());
			//}
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return result;
	}
	
	
	public String consultarRutaUno(int indice) {
		gral = new ConsultasGenerales(jdbcTemplate);
		try {
			return gral.consultarRutaUno(indice);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()
							+ " "
							+ e.toString()
							+ "P:BE, C:AdministradorArchivosDao, M:consultarRutaUno");
			return null;
		}
	}
	public String consultarRutaDos(int indice) {
		gral = new ConsultasGenerales(jdbcTemplate);
		try {
			return gral.consultarRutaDos(indice);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()
							+ " "
							+ e.toString()
							+ "P:BE, C:AdministradorArchivosDao, M:consultarRutaDos");
			return null;
		}
	}
	
	
	public int obtenerResultado() {
		int result=0;
		
		try {
			sb = new StringBuffer();
			sb.append("select sum(sin_cheq) ");
			sb.append("\n from temp_lineas");
			
			System.out.print("obtenerResultado: " + sb);
			logger.info("obtenerResultado: " + sb);

			result = jdbcTemplate.queryForInt(sb.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			
		}
		return result;
	}
	
	
	public int obtenerGuardados() {
		int result=0;
		
		try {
			sb = new StringBuffer();
			sb.append("select sum(con_cheq) ");
			sb.append("\n from temp_lineas");
			
			System.out.print("obtenerGuardados: " + sb);
			logger.info("obtenerGuardados: " + sb);

			result = jdbcTemplate.queryForInt(sb.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			
		}
		return result;
	}
	
	public void limpiarTabla() {
		 
		try {
			sb = new StringBuffer();
			sb.append("\n truncate table cob_lineas_archivos");
			sb.append("\n truncate table archivos");
			
			
			System.out.print("limpiarTabla: " + sb);
			logger.info("limpiarTabla: " + sb);

			jdbcTemplate.execute(sb.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			
		}
		 
		
	}
	
	
	public void limpiarTablaBanamex() {
		 
		try {
			sb = new StringBuffer();
			sb.append("\n truncate table temp_lineas");
			sb.append("\n truncate table chequeras");
			
			
			System.out.print("limpiarTablaBanamex: " + sb);
			logger.info("limpiarTablaBanamex: " + sb);

			jdbcTemplate.execute(sb.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			
		}
		 
		
	}
	
	
	public List<String> obtenerChequeras() {
		sb = new StringBuffer();
		//sb.append(" DECLARE @chequeras varchar(5000) ");
		sb.append(" select  chequeras from chequeras group by chequeras ");
		//sb.append(" select @chequeras ");
		try {
			List<String> datos = jdbcTemplate.query(sb.toString(), new RowMapper() {
						public String mapRow(ResultSet rs, int idx)	throws SQLException {
							return rs.getString("chequeras");
						}
					});
			return datos;
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BE, C:AdministradorArchivosDao, M:obtenerChequeras");
			return null;
		}
	}
	public int DatosMantenimientoMovimiento() {
		int result = 0;
		
		try{
			HashMap<Object, Object> inParams = new HashMap<Object, Object>();
			
		
			StoredProcedure sp = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_ObtenerDatosMantenimientoMovimiento") {};
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
	
	public int insertRutaCompletaBancomer(File nombresArch) {
		int result=0;
		
		try {
			sb = new StringBuffer();
			sb.append(" Insert into lineas_archivos_bancomer");
			sb.append("\n	(linea)");
			sb.append("\n VALUES('" + nombresArch.getAbsolutePath()+ "')");
			
			System.out.print("insertRutaCompletaBancomer: " + sb);
			logger.info("insertRutaCompletaBancomer: " + sb);

			result = jdbcTemplate.update(sb.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			
		}
		return result;
	}
 
	public int insertLineaBancomer(String psRegistro) {
		int result=0;
		
		sb = new StringBuffer();
		sb.append(" Insert into cob_lineas_edo_cta_bancomer");
		sb.append("\n	(linea)");
		sb.append("\n VALUES('" + psRegistro+ "')");
		
		//System.out.print(sb);
		
		try {
			//for(int i=0; i<linea.length; i++){
			result = jdbcTemplate.update(sb.toString());
			//}
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}
		return result;
	}
	
	public int selecionarMovtoBancaEProcedimientoBancomer() {
		int result = 0;
		String var = "''";
		try{
			HashMap<Object, Object> inParams = new HashMap<Object, Object>();
			HashMap<Object, Object> inParam = new HashMap<Object, Object>();
			
			
			StoredProcedure sp = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_cob_lee_bancomer_edo_cta_2") {};
			sp.execute(inParams);
			
			StoredProcedure spc = new StoredProcedure(jdbcTemplate.getDataSource(), "sp_llamaConfirmador") {};
			
			spc.declareParameter(new SqlParameter("result", Types.INTEGER)); 
            inParam.put("result", 0);
            spc.declareParameter(new SqlParameter("mensaje", Types.VARCHAR)); 
            inParam.put("mensaje", var);
			
			
			spc.execute((Map)inParam);
				  
			result = 1;
		}
		catch(Exception e){
			e.printStackTrace();
			logger.error(e);
			result = -1;
		}
		return result;
	}
	
	public void limpiarTablaBancomer() {
		 
		try {
			sb = new StringBuffer();
			sb.append("\n truncate table lineas_archivos_bancomer");
			sb.append("\n truncate table cob_lineas_edo_cta_bancomer");
			//sb.append("\n truncate table archivos");
			//sb.append("\n truncate table chequeras");
			
			
			System.out.print("limpiarTabla: " + sb);
			logger.info("limpiarTabla: " + sb);

			jdbcTemplate.execute(sb.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			
		}
		 
		
	}
	
	public void limpiarTablasBancomer() {
		 
		try {
			sb = new StringBuffer();
			sb.append("\n truncate table temp_lineas");
			sb.append("\n truncate table chequeras");
			
			
			System.out.print("limpiarTabla: " + sb);
			logger.info("limpiarTabla: " + sb);

			jdbcTemplate.execute(sb.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
			
		}
		 
		
	}
	
	
		
	// getters and setters
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public JdbcTemplate getJdbcTemplate(){
		return this.jdbcTemplate;
	}
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate){
		this.jdbcTemplate = jdbcTemplate;
	}
}
