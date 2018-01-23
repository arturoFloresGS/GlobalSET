package com.webset.set.bancaelectronica.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.bancaelectronica.dto.ArchMovtoBancaeDto;
import com.webset.set.bancaelectronica.dto.CatCtaBancoDto;
import com.webset.set.bancaelectronica.dto.EquivaleConceptoDto;
import com.webset.set.bancaelectronica.dto.FechasDto;
import com.webset.set.bancaelectronica.dto.MovtoBancaEDto;
import com.webset.set.bancaelectronica.dto.ParametroLayoutDto;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.ComunDto;

/**
 * 
 * @author Jessica Arelly Cruz Cruz
 * @since 01/Noviembre/2010
 * @see
 *        <p>
 *        Tabla cat_cta_banco
 *        </p>
 */
public class AdministradorArchivosBancomerDao {
	private JdbcTemplate jdbcTemplate;
	private ConsultasGenerales gral;
	private Funciones funciones;
	private static Logger logger = Logger.getLogger(AdministradorArchivosBancomerDao.class);

	DateFormat fecInicio = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	DateFormat fecFinal = new SimpleDateFormat("dd/MM/yyyy");

	/**
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
	public List<CatCtaBancoDto> consultarEmpresa(CatCtaBancoDto empresa,
			int pi_banco, boolean movDiarios, boolean pbSwiftMT940_MN) {
		StringBuffer sb = new StringBuffer();
		String sql = "";
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
		sql = sb.toString();
		logger.info(sql);
		List<CatCtaBancoDto> empresas = jdbcTemplate.query(sql,
				new RowMapper() {
					public CatCtaBancoDto mapRow(ResultSet rs, int idx)
							throws SQLException {
						CatCtaBancoDto empresa = new CatCtaBancoDto();
						empresa.setNoEmpresa(rs.getInt("no_empresa"));
						empresa.setIdBanco(rs.getInt("id_banco"));
						empresa.setIdChequera(rs.getString("id_chequera"));

						return empresa;
					}
				});
		return empresas;
	}

	/**
	 * consulta la tabla configura_set y obtiene el valor sengun el indice
	 * 
	 * @param indice
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String consultarConfiguraSet(int indice) {
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT valor ");
		sb.append("\n FROM configura_set ");
		sb.append("\n WHERE indice = " + indice);
		List<String> valores = jdbcTemplate.query(sb.toString(),
				new RowMapper() {
					public String mapRow(ResultSet rs, int idx)
							throws SQLException {
						String valor = rs.getString("valor");
						return valor;
					}
				});
		return valores.get(0);
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
		res = jdbcTemplate.update(sb.toString(), new Object[] {
				banca.getArchivo(), banca.getIdChequera(), banca.getFecAlta(),
				banca.getIdBanco() });

		return res;
	}

	/**
	 * consultar la fecha de hoy en la tabla fechas
	 */
	@SuppressWarnings("unchecked")
	public List<FechasDto> obtenerFechaHoy() {
		StringBuffer sb = new StringBuffer();
		String sql = "";
		sb.append(" SELECT fec_hoy ");
		sb.append(" FROM fechas");
		sql = sb.toString();
		List<FechasDto> fechas = jdbcTemplate.query(sql, new RowMapper() {
			public FechasDto mapRow(ResultSet rs, int idx) throws SQLException {
				FechasDto fecha = new FechasDto();
				fecha.setFecHoy(rs.getDate("fec_hoy"));

				return fecha;
			}
		});
		return fechas;
	}

	public Date cambiarFecha(String fecha) {
		gral = new ConsultasGenerales(jdbcTemplate);
		return gral.cambiarFecha(fecha);

	}

	/**
	 * Public Function FunSQLSelectCat_banco() As ADODB.Recordset Selecciona
	 * b_saldo_banco segun el datos si es mayor a cero busca segun el id si no
	 * saca los saldos de todos los bancos
	 * 
	 * @param id
	 * @return List<ComunDto>
	 */
	@SuppressWarnings("unchecked")
	public List<ComunDto> seleccionarSaldoBanco(int id) {
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT id_banco, b_saldo_banco ");
		sb.append(" FROM cat_banco ");
		sb.append(" WHERE b_banca_elect IN ('I','A') ");
		if (id != 0)
			sb.append(" AND id_banco = " + id);
		else
			sb.append(" ORDER BY id_banco ");
		try {
			List<ComunDto> datos = jdbcTemplate.query(sb.toString(),
					new RowMapper() {
						public ComunDto mapRow(ResultSet rs, int idx)
								throws SQLException {
							ComunDto dato = new ComunDto();
							dato.setIdBanco(rs.getInt("id_banco"));
							dato.setDescripcion(rs.getString("b_saldo_banco"));
							return dato;
						}
					});
			return datos;
		} catch (Exception e) {
			logger.error(e.toString());
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
	public List<MovtoBancaEDto> selecionarMovtoBancaE(ParametroLayoutDto dto) {
		StringBuffer sb = new StringBuffer();
		String psSQLAdicional;
		funciones = new Funciones();
		if (dto.getPdSaldo() != 0)
			psSQLAdicional = "\n AND saldo_bancario = " + dto.getPdSaldo();
		else
			psSQLAdicional = "";
		if (dto.getContadorRenglon() > 0) {
			psSQLAdicional = psSQLAdicional + "\n AND (no_linea_arch = "
					+ dto.getContadorRenglon();
			psSQLAdicional = psSQLAdicional + " OR no_linea_arch IS NULL) ";
		}
		if (dto.getPlNoLineaArchivo() > 0
				&& !dto.getPsConcepto().equals("MISMO BANCO BANCOMER")
				&& !dto.getPsConcepto().equals("MISMO BANCO BANCOMER2"))
			psSQLAdicional += "\n AND no_linea_arch = "
					+ dto.getPlNoLineaArchivo();
		if (dto.getCargoAbono() != null && !dto.getCargoAbono().equals(""))
			psSQLAdicional += "\n AND cargo_abono = '" + dto.getCargoAbono()
					+ "' ";
		// Select Case gsDBM
		// Case "SQL SERVER", "SYBASE":
		sb.append(" SELECT movto_arch, secuencia, archivo ");
		sb.append("\n FROM movto_banca_e ");
		sb.append("\n WHERE movto_arch = 1 ");
		sb.append("\n AND no_empresa = " + dto.getPlNoEmpresa());

		if (dto.getPsConcepto().equals("MISMO BANCO BANCOMER")) {
			if (dto.getPsFolioBanco().substring(0, 1).equals("'"))
				sb.append("\n AND folio_banco in (" + dto.getPsFolioBanco()
						+ ") AND ( archivo like 'md%' OR archivo like 'SH%') ");
			else
				sb.append("\n AND folio_banco = '" + dto.getPsFolioBanco()
						+ "' AND ( archivo like 'md%' OR archivo like 'SH%') ");
		}
		if (!dto.getPsConcepto().equals("MISMO BANCO BANCOMER")
				&& !dto.getPsConcepto().equals("MISMO BANCO BANCOMER2")
				&& dto.getPiBanco() == 12)
			sb.append("\n AND concepto = '" + dto.getPsConcepto()
					+ "' AND ( archivo like 'md%' OR archivo like 'SH%') ");
		else if (dto.getPsConcepto().equals("MISMO BANCO BANCOMER2"))
			sb.append("\n AND referencia = '" + dto.getPsReferencia() + "'");
		sb.append("\n AND id_chequera = '" + dto.getPsIdChequera() + "'");
		sb.append("\n AND CONVERT(DATETIME,fec_valor, 103) = '"
				+ funciones.ponerFechaSola(dto.getPsFecValor()) + "'");
		sb.append("\n AND importe = " + dto.getPdImporte());
		sb.append(psSQLAdicional);
		sb.append("\n UNION ALl ");
		sb.append("\n SELECT movto_arch, secuencia, archivo");
		sb.append("\n FROM hist_movto_banca_e ");
		sb.append("\n WHERE movto_arch = 1 ");
		sb.append("\n AND no_empresa = " + dto.getPlNoEmpresa());
		if (dto.getPsConcepto().equals("MISMO BANCO BANCOMER")) {
			if (dto.getPsFolioBanco().substring(0, 1).equals("'"))
				sb.append("\n AND folio_banco IN (" + dto.getPsFolioBanco()
						+ ") AND ( archivo like 'md%' OR archivo like 'SH%') ");
			else
				sb.append("\n AND folio_banco = '" + dto.getPsFolioBanco()
						+ "' AND ( archivo like 'md%' OR archivo like 'SH%') ");
		}
		if (!dto.getPsConcepto().equals("MISMO BANCO BANCOMER")
				&& !dto.getPsConcepto().equals("MISMO BANCO BANCOMER2")
				&& dto.getPiBanco() == 12)
			sb.append("\n AND concepto = '" + dto.getPsConcepto()
					+ "' AND ( archivo like 'md%' or archivo like 'SH%') ");
		else if (dto.getPsConcepto().equals("MISMO BANCO BANCOMER2"))
			sb.append("\n AND referencia = '" + dto.getPsReferencia() + "'");
		sb.append("\n AND id_chequera = '" + dto.getPsIdChequera() + "'");
		sb.append("\n AND CONVERT(datetime, fec_valor, 103) = '"
				+ funciones.ponerFechaSola(dto.getPsFecValor()) + "'");
		sb.append("\n AND importe = " + dto.getPdImporte());
		sb.append(psSQLAdicional);
		sb.append("\n ORDER BY archivo ");
		try {
			List<MovtoBancaEDto> datos = jdbcTemplate.query(sb.toString(),
					new RowMapper() {
						public MovtoBancaEDto mapRow(ResultSet rs, int idx)	throws SQLException {
							MovtoBancaEDto dato = new MovtoBancaEDto();
							dato.setMovtoArch(rs.getInt("movto_arch"));
							dato.setSecuencia(rs.getInt("secuencia"));
							dato.setArchivo(rs.getString("archivo"));
							return dato;
						}
					});
			return datos;
		} catch (Exception e) {
			logger.error(e.toString());
			return null;
		}

	}

	public int insertarMovto(MovtoBancaEDto registro) throws Exception {
		int res = 0;
		String psEstatus = "";
		StringBuffer sb = new StringBuffer();
		gral = new ConsultasGenerales(jdbcTemplate);

		if (psEstatus == "")
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
		sb.append(" VALUES (" + registro.getNoEmpresa() + ", "
				+ registro.getIdBanco() + ", ");
		sb.append("'" + registro.getIdChequera() + "', "
				+ registro.getSecuencia() + ", ");
		sb.append("'" + funciones.ponerFecha(registro.getFecValor())
				+ "', '" + registro.getSucursal() + "', ");
		sb.append("'" + registro.getFolioBanco() + "', '"
				+ registro.getReferencia() + "', ");
		sb.append("'" + registro.getCargoAbono() + "', "
				+ registro.getImporte() + ", ");
		sb.append("'" + registro.getBSalvoBuenCobro() + "', '"
				+ registro.getConcepto() + "', ");
		sb.append("'" + psEstatus + "', '"
				+ funciones.ponerFecha(registro.getFecAlta())
				+ "', 'N', 'N', 1, 2, ");
		sb.append("'" + registro.getDescripcion() + "', '"
				+ registro.getArchivo() + "', ");
		if (registro.getObservacion() != null)
			sb.append("'" + registro.getObservacion() + "', ");
		if (registro.getIdCveConcepto() != null)
			sb.append("'" + registro.getIdCveConcepto() + "', ");
		if (registro.getSaldoBancario() != 0)
			sb.append(registro.getSaldoBancario() + ", ");
		if (registro.getNoLineaArch() > 0)
			sb.append(registro.getNoLineaArch() + ", ");

		sb.append("0 )");
		res = jdbcTemplate.update(sb.toString());

		return res;
	}

	public int getSeq(String tabla, String campo) {
		gral = new ConsultasGenerales(jdbcTemplate);
		return gral.getSeq(tabla, campo);
	}

	/**
	 * 
	 * @param tipoFolio
	 * @return int
	 * 
	 * Selecciona el numero de folio de la tabla folio
	 */
	public int seleccionarFolioReal(String tipoFolio) {
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT num_folio ");
		sb.append("\n FROM folio ");
		sb.append("\n WHERE tipo_folio = '" + tipoFolio + "'");
		try {
			return jdbcTemplate.queryForInt(sb.toString());
		} catch (Exception e) {
			logger.error(e.toString());
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
		StringBuffer sb = new StringBuffer();
		sb.append(" UPDATE folio ");
		sb.append("\n SET num_folio = num_folio + 1 ");
		sb.append("\n WHERE tipo_folio = '" + tipoFolio + "'");
		try {
			return jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			logger.error(e.toString());
			return -1;
		}
	}

	/**
	 * Public Function FunObtieneDescripcion(ByVal idBanco As Integer, ByVal
	 * psdescripcion As String, ByVal ps_registro As String, _ ByVal
	 * psCargoAbono As String, ByRef psconcepto As String, ByRef psFolio_banco
	 * As String) As Boolean
	 */

	public boolean obtenerDescripcion(EquivaleConceptoDto conceptoDto) {
		StringBuffer sb = new StringBuffer();
		// Dim prTabla As Recordset

		sb.append("");
		sb.append(" SELECT desc_concepto, coalesce(folio_banco_ini,0) as folio_banco_ini, coalesce(folio_banco_long,0) as folio_banco_long ");
		sb.append(" FROM equivale_concepto ");
		sb.append(" WHERE ");
		sb.append(" id_banco = " + conceptoDto.getIdBanco()
				+ " and cargo_abono = '" + conceptoDto.getCargoAbono() + "'");
		sb.append(" and substring('" + conceptoDto.getDescConcepto()
				+ "',1, len(desc_concepto)) = desc_concepto ");
		sb.append(" ORDER BY desc_concepto desc ");

		int resultado = jdbcTemplate.update(sb.toString());
		try {
			if (resultado > 0)
				return true;
			else
				return false;
		} catch (NumberFormatException nfe) {
			return false;
		}
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
		StringBuffer sb = new StringBuffer();
		sb.append(" UPDATE movto_banca_e SET ");
		sb.append("    archivo = '" + dto.getPsArchivo() + "'");
		sb.append(" Where ");
		sb.append(" no_empresa = " + dto.getPlNoEmpresa());
		sb.append(" And id_banco = " + dto.getPiBanco());
		sb.append(" And id_chequera = '" + dto.getPsIdChequera() + "' ");
		sb.append(" AND secuencia = " + dto.getPsSecuencia());

		if (dto.getPsDescripcion().length() > 20) {
			dto
					.setPsDescripcion(dto.getPsDescripcion().substring(0, 20)
							.trim());
		}
		sb = null;
		sb = new StringBuffer();
		sb.append(" UPDATE movimiento SET referencia_ban = '"
				+ dto.getPsDescripcion() + "'");
		sb.append(" Where ");
		sb.append(" no_empresa = " + dto.getPlNoEmpresa());
		sb.append(" And id_banco = " + dto.getPiBanco());
		sb.append(" And id_chequera = '" + dto.getPsIdChequera() + "' ");
		sb.append(" And id_inv_cbolsa = " + dto.getPsSecuencia());
		
		sb = null;
		sb = new StringBuffer();
		sb.append(" UPDATE hist_movimiento SET referencia_ban = '"
				+ dto.getPsDescripcion() + "'");
		sb.append(" Where ");
		sb.append(" no_empresa = " + dto.getPlNoEmpresa());
		sb.append(" And id_banco = " + dto.getPiBanco());
		sb.append(" And id_chequera = '" + dto.getPsIdChequera() + "' ");
		sb.append(" And id_inv_cbolsa = " + dto.getPsSecuencia());
		logger.info("actualiza: "+ sb);
		try {
			return jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			logger.error(e.toString());
			return -1;
		}
	}

	/**
	 * Public Function FunSQLUpdateRechazados(ByVal idBanco As Integer) As Long
	 */

	public int actualizarRechazados(ParametroLayoutDto dto) {
		StringBuffer sb = new StringBuffer();
		/** ACTUALIZA LAS DEVOLUCIONES SIN REFERENCIA */

		sb.append("UPDATE movto_banca_e set id_estatus_cancela = '3' ");
		sb.append(" Where secuencia in ( SELECT secuencia FROM movto_banca_e m, concepto_rechazo c ");

		sb.append("WHERE m.id_banco = c.id_banco and m.concepto like c.concepto_mass + '%' ");

		sb.append(" and m.cargo_abono = c.cargo_abono and m.id_estatus_cancela = '0' ");
		sb.append(" and m.id_banco = " + dto.getPiBanco());
		sb.append(" and (SELECT COUNT(*) FROM movto_banca_e m1 ");
		sb.append(" WHERE m.folio_banco=m1.folio_banco and m.importe=m1.importe and m1.cargo_abono='E')=0)");

		/** ACTUALIZA LOS EGRESOS DE LAS DEVOLUCIONES A PROCESAR */
		sb = null;
		sb = new StringBuffer();
		sb.append("UPDATE movto_banca_e SET id_estatus_cancela='1' ");
		sb.append("WHERE secuencia in ( SELECT m.secuencia FROM movto_banca_e m, movto_banca_e m1, concepto_rechazo c ");
		sb.append("WHERE m.folio_banco = m1.folio_banco and m.importe = m1.importe ");
		sb.append("and m.cargo_abono = 'E' and m.id_banco = "
				+ dto.getPiBanco() + " and m.id_estatus_cancela = '0' ");
		sb.append("and m1.id_banco = c.id_banco and m1.cargo_abono = c.cargo_abono ");

		sb.append("and m1.concepto like c.concepto_mass + '%')");

		/** ACTUALIZA LOS INGRESOS DE LAS DEVOLUCIONES A PROCESAR */
		sb = null;
		sb = new StringBuffer();
		sb.append("UPDATE movto_banca_e set id_estatus_cancela = '1' ");
		sb.append(" Where secuencia in ( SELECT secuencia FROM movto_banca_e m, concepto_rechazo c ");
		sb.append("WHERE m.id_banco = c.id_banco and m.concepto like ");

		sb.append("c.concepto_mass + '%' and m.cargo_abono = c.cargo_abono ");

		sb.append(" and m.cargo_abono = 'I' and m.id_estatus_cancela = '0' ");
		sb.append(" and m.id_banco = " + dto.getPiBanco() + ")");
		System.out.println("rechazados: "+ sb);
		try {
			return jdbcTemplate.update(sb.toString());
		} catch (Exception e) {
			logger.error(e.toString());
			return -1;
		}
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

	// getters && setters
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
}
