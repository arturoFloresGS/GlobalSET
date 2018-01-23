package com.webset.set.inversiones.dao.impl;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.inversiones.dao.CotizacionesDao;
import com.webset.set.inversiones.dto.ContratoInstitucionDto;
import com.webset.set.inversiones.dto.CotizacionDto;
import com.webset.set.inversiones.dto.LlenaComboValoresDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.dto.FuncionesSql;
import com.webset.utils.tools.Utilerias;

public class CotizacionesDaoImpl implements CotizacionesDao {
	private JdbcTemplate jdbcTemplate;
	private Bitacora bitacora = new Bitacora();
	private static Logger logger = Logger.getLogger(CotizacionesDaoImpl.class);
	String gsDBM = ConstantesSet.gsDBM;
	
	@Override
	public List<LlenaComboValoresDto> consultarContactosInst(int idInstitucion) {
		StringBuffer sbSql = new StringBuffer();
		List <LlenaComboValoresDto> lista = null;

		logger.debug("Entra consultarContactosInst");
		if(ConstantesSet.gsDBM.equals("ORACLE") || ConstantesSet.gsDBM.equals("DB2") || ConstantesSet.gsDBM.equals("POSTGRESQL"))
			sbSql.append("SELECT p.no_persona, p.nombre || ' ' || p.paterno || ' '  || p.materno as Descrip");
		else // if(gsDBM.equals("SQL SERVER") || gsDBM.equals("SYBASE") )
			sbSql.append("SELECT p.no_persona, p.nombre + ' ' + p.paterno + ' '  + p.materno as Descrip");
		sbSql.append("FROM persona p, relacion r ");
		sbSql.append("WHERE r.no_empresa = 1 ");
		sbSql.append("AND r.no_persona = ? ");
		sbSql.append("AND p.no_empresa = r.no_empresa_rel ");
		sbSql.append("AND p.no_persona = r.no_persona_rel ");
		sbSql.append("AND r.id_tipo_relacion = 8");
		
		try{
			lista = jdbcTemplate.query(sbSql.toString(), new Object[] {Utilerias.validarCadenaSQL(idInstitucion)}, new RowMapper<LlenaComboValoresDto>(){
				public LlenaComboValoresDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboValoresDto cons = new LlenaComboValoresDto();
					cons.setIdDiv(rs.getString("no_persona"));
					cons.setDescripcion(rs.getString("Descrip"));
					return cons;
				}});
		}catch (Exception e){
			logger.error(e.getLocalizedMessage());
		}
		
		return lista;
	}

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.dao.CotizacionesDao#consultarContratos(int)
	 */
	@Override
	public List<ContratoInstitucionDto> consultarContratos(int noInstitucion) {
		StringBuffer sbSql = new StringBuffer();
		List <ContratoInstitucionDto> lista = null;

		logger.debug("Entra consultarContactosInst");
		
		sbSql.append("SELECT c.no_empresa, nom_empresa, contrato_institucion ");
		sbSql.append("FROM cuenta c ,empresa e ");
		sbSql.append("WHERE c.NO_EMPRESA = e.no_empresa ");
		sbSql.append("AND no_institucion = ? ");
		sbSql.append("AND id_tipo_cuenta='C'");
		
		try{
			lista = jdbcTemplate.query(sbSql.toString(), new Object[] {Utilerias.validarCadenaSQL(noInstitucion)}, new RowMapper<ContratoInstitucionDto>(){
				public ContratoInstitucionDto mapRow(ResultSet rs, int idx) throws SQLException {
					ContratoInstitucionDto cons = new ContratoInstitucionDto();
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setNomEmpresa(rs.getString("nom_empresa"));
					cons.setContrato(rs.getString("contrato_institucion"));
					return cons;
				}});
		}catch (Exception e){
			logger.error(e.getLocalizedMessage());
		}
		
		return lista;
	}

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.dao.CotizacionesDao#consultarDivisaTV(java.lang.String)
	 */
	@Override
	public String consultarDivisaTV(String idTipoValor) {
		StringBuffer sbSql = new StringBuffer();
		String valor = "";
		
		logger.debug("Entra consultarDivisaTV");
		
		sbSql.append("SELECT c.id_divisa ");
		sbSql.append("FROM cat_tipo_valor c, cat_divisa d ");
		sbSql.append("WHERE c.id_divisa = d.id_divisa ");
		sbSql.append("AND c.id_tipo_valor = ?");
		
		try{
			valor = this.getJdbcTemplate().queryForObject(sbSql.toString(), String.class, new Object[]{Utilerias.validarCadenaSQL(idTipoValor)});
		}catch (Exception e){
			logger.error(e.getLocalizedMessage());
		}

		return valor;
	}
	
	

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.dao.CotizacionesDao#validarCotizacion(int, java.lang.String, int, java.lang.String)
	 */
	@Override
	public boolean validarCotizacion(int plazo, String tipoValor, int idInstitucion, Date fecha) {
		int liCuenta = 0;
		StringBuffer sbSql = new StringBuffer();
		
		sbSql.append("SELECT count(*) ");
		sbSql.append("FROM cotizacion ");
		/*
		if(ConstantesSet.gsDBM.equals("ORACLE") || ConstantesSet.gsDBM.equals("DB2") || ConstantesSet.gsDBM.equals("POSTGRESQL")){
			sbSql.append("WHERE fec_alta >= to_date('" + Utilerias.validarCadenaSQL(fecha) + "', 'dd/mm/yyyy') ");
			sbSql.append("AND fec_alta < to_date('" + Utilerias.validarCadenaSQL(fecha) + "', 'dd/mm/yyyy') + 1 ");
		} else {
			sbSql.append("WHERE fec_alta >= convert(datetime,'" + Utilerias.validarCadenaSQL(fecha) + "', 103) ");
			sbSql.append("AND fec_alta < convert(datetime,'" + Utilerias.validarCadenaSQL(fecha) + "', 103) + 1 ");
		}*/
		sbSql.append("WHERE fec_alta >= '" + FuncionesSql.ponFechaDMY(fecha, false) + "' ");
		if(ConstantesSet.gsDBM.equals("ORACLE") || ConstantesSet.gsDBM.equals("DB2") || ConstantesSet.gsDBM.equals("POSTGRESQL")){
			sbSql.append("AND fec_alta < to_date('" + FuncionesSql.ponFechaDMY(fecha, false) + "', 'dd/mm/yyyy') + 1 ");
		} else {
			sbSql.append("AND fec_alta < dateadd(d,1,'" + FuncionesSql.ponFechaDMY(fecha, false) + "')  ");
		}
		sbSql.append("AND id_tipo_valor = '" + Utilerias.validarCadenaSQL(tipoValor) + "' ");
		sbSql.append("AND plazo = " + Utilerias.validarCadenaSQL(plazo));
		sbSql.append(" AND no_institucion = " + Utilerias.validarCadenaSQL(idInstitucion));
		try{
			liCuenta = this.getJdbcTemplate().queryForInt(sbSql.toString());
		}catch (Exception e){
			logger.error(e.getLocalizedMessage());
		}
		
		if (liCuenta > 0)
			return true;
		
		return false;
	}
	
	

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.dao.CotizacionesDao#insertarCotizacion(com.webset.set.inversiones.dto.CotizacionDto)
	 */
	@Override
	public void insertarCotizacion(CotizacionDto cotizacion) {
		StringBuffer sbSql = new StringBuffer(); 
		
		sbSql.append("INSERT INTO cotizacion (no_empresa, id_tipo_valor, no_institucion, fec_alta, plazo, tasa, hora, minuto, ");
		sbSql.append("equivale_28, usuario_alta, id_divisa ) VALUES (");
		sbSql.append(Utilerias.validarCadenaSQL(cotizacion.getNoEmpresa()) + ", '");
		sbSql.append(Utilerias.validarCadenaSQL(cotizacion.getTipoValor()) + "', ");
		sbSql.append(Utilerias.validarCadenaSQL(cotizacion.getIdInstitucion()) + ", ");
		//sbSql.append("to_date ('" + Utilerias.validarCadenaSQL(cotizacion.getFecha()));
		sbSql.append("'" + FuncionesSql.ponFechaDMY(cotizacion.getFecha(), false) + " " + cotizacion.getHora() + "', ");
		//sbSql.append(" " + Utilerias.validarCadenaSQL(cotizacion.getHora()) + "', 'dd/mm/yyyy hh24:mi'), ");
		sbSql.append(Utilerias.validarCadenaSQL(cotizacion.getPlazo()) + ", ");
		sbSql.append(Utilerias.validarCadenaSQL(cotizacion.getTasa()) + ", '");
		sbSql.append(Utilerias.validarCadenaSQL(cotizacion.getHora()).substring(0,2) + "', '");
		sbSql.append(Utilerias.validarCadenaSQL(cotizacion.getHora()).substring(3,5) + "', ");
		sbSql.append(Utilerias.validarCadenaSQL(cotizacion.getTasaEquiv28().toString()) + ", ");
		sbSql.append(Utilerias.validarCadenaSQL(cotizacion.getIdUsuario()) + ", '");
		sbSql.append(Utilerias.validarCadenaSQL(cotizacion.getIdDivisa()) + "')");
		
		try {
			this.getJdbcTemplate().execute(sbSql.toString());
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
		}
	}

	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.dao.CotizacionesDao#consultarRepCotizaciones(java.lang.String, int)
	 */
	@Override
	public List<Map<String, Object>> consultarRepCotizaciones(String sfecha, int noEmpresa) {
		StringBuffer sbSql = new StringBuffer();
		Date fecha;
		
		fecha = FuncionesSql.ponerFechaDate(sfecha);
		
		sbSql.append("SELECT a.no_institucion noInstitucion, b.razon_social razonSocial, c.id_tipo_valor idTipoValor, c.desc_tipo_valor tipoValor, ");
		sbSql.append("a.id_divisa, a.hora, a.minuto, a.tasa, a.plazo, a.equivale_28 equivale28 ");
		sbSql.append("FROM cotizacion a, persona b, cat_tipo_valor c ");
		sbSql.append("WHERE to_char(a.no_institucion) = b.equivale_persona ");
		sbSql.append("AND a.id_tipo_valor = c.id_tipo_valor ");
		sbSql.append("AND a.no_empresa = " + Utilerias.validarCadenaSQL(noEmpresa));
		//sbSql.append(" AND a.fec_alta >= to_date('" + Utilerias.validarCadenaSQL(fecha) + "','dd/mm/yyyy') ");
		//sbSql.append("AND a.fec_alta < to_date('" + Utilerias.validarCadenaSQL(fecha) + "','dd/mm/yyyy') + 1 ");
		sbSql.append(" AND a.fec_alta >= '" + FuncionesSql.ponFechaDMY(fecha, false) + "' ");
		if(ConstantesSet.gsDBM.equals("ORACLE") || ConstantesSet.gsDBM.equals("DB2") || ConstantesSet.gsDBM.equals("POSTGRESQL")){
			sbSql.append("AND fec_alta < to_date('" + FuncionesSql.ponFechaDMY(fecha, false) + "', 'dd/mm/yyyy') + 1 ");
		} else {
			sbSql.append("AND fec_alta < dateadd(d,1,'" + FuncionesSql.ponFechaDMY(fecha, false) + "')  ");
		}
		
		//System.out.println("El query es "+ sbSql.toString());
		
		return this.getJdbcTemplate().queryForList(sbSql.toString());
	}

	//set del data source
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e){
			bitacora.insertarRegistro(new java.util.Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Inversiones, C:MantenimientoDePapelDaoImpl, M:setDataSource");
		}
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
