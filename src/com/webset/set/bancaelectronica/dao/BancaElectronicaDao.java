package com.webset.set.bancaelectronica.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.ReferenciaEncDto;
import com.webset.set.utilerias.dto.Retorno;
/**
 * 
 * @author Sergio Vaca
 *
 */
public class BancaElectronicaDao {
	private JdbcTemplate jdbcTemplate;
	private StringBuffer sb;
	private Bitacora bitacora = new Bitacora();
	private ConsultasGenerales gral;
	/**
	 * 
	 * @return List<ComunDto>
	 * 
	 * Obtiene los bancos activos
	 * 
	 * Public Function FunSQLCombo367() As ADODB.Recordset
	 */
	@SuppressWarnings("unchecked")
	public List<ComunDto> seleccionarBancosActivos(){
		sb = new StringBuffer();
		List<ComunDto> datos = null;
		sb.append("\n SELECT id_banco as ID, case when id_banco = 72 then 'BANORTE' else desc_banco end as Descrip ");
		sb.append("\n FROM cat_banco ");
		sb.append("\n WHERE b_banca_elect in ('I','A') ");
		sb.append("\n ORDER BY id_banco ");
		try{
			datos = jdbcTemplate.query(sb.toString(), new RowMapper() {
				public ComunDto mapRow(ResultSet rs, int idx){
					ComunDto dato = new ComunDto();
					try {
						dato.setIdBanco(rs.getInt("ID"));
						dato.setDescripcion(rs.getString("Descrip"));
					} catch (SQLException e) {
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
								+ "P:BE, C:BancaElectronicaDao, M:seleccionarBancosActivos");
					}
					return dato;
				}
			});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BE, C:BancaElectronicaDao, M:seleccionarBancosActivos");
		}
		return datos;
	}
	
	/**
	 * 
	 * @return List<ReferenciaEncDto>
	 * 
	 * Public Function FunSQLLevantaRefEnc() As ADODB.Recordset
    	Se llama en la forma frmTransIng
	 */
	@SuppressWarnings("unchecked")
	public List<ReferenciaEncDto> levantarRefEnc(){
		List<ReferenciaEncDto> datos = null;
		sb = new StringBuffer();
		sb.append(" SELECT id_banco, no_empresa, no_digitos, campo_verificador, base_calculo, ");
		sb.append("\n 	num_alfa, repara1, repara2 "); 
		sb.append("\n FROM referencia_enc "); 
		sb.append("\n ORDER BY id_banco, no_empresa ");
		try{
			datos = jdbcTemplate.query(sb.toString(), new RowMapper() {
				public ReferenciaEncDto mapRow(ResultSet rs, int idx){
					ReferenciaEncDto dato = new ReferenciaEncDto();
					try {
						dato.setIdBanco(rs.getInt("id_banco"));
						dato.setNoEmpresa(rs.getInt("no_empresa"));
						dato.setNoDigitos(rs.getInt("no_digitos"));
						dato.setCampoVerificador(rs.getInt("campo_verificador"));
						dato.setBaseCalculo(rs.getString("num_alfa"));
						dato.setRepara1(rs.getString("repara1"));
						dato.setRepara2(rs.getString("repara2"));
					} catch (SQLException e) {
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
								+ "P:BE, C:AdministradorArchivosDao, M:levantarRefEnc");
					}
					return dato;
				}
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BE, C:AdministradorArchivosDao, M:levantarRefEnc");
		}
		return datos;
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
							+ "P:BE, C:BancaElectronicaDao, M:consultarConfiguraSet");
			return null;
		}
	}
	
	public List<Retorno> consultarConfiguraSet() {
		gral = new ConsultasGenerales(jdbcTemplate);
		try {
			return gral.consultarConfiguraSet();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString()
							+ " "
							+ e.toString()
							+ "P:BE, C:BancaElectronicaDao, M:consultarConfiguraSet");
			return null;
		}
	}
	
	public void setDataSource(DataSource dataSource){
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
}
