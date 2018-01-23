package com.webset.set.financiamiento.dao;

import java.util.Date;
import java.util.List;

import com.webset.set.financiamiento.dto.AmortizacionCreditoDto;
import com.webset.set.financiamiento.dto.Amortizaciones;
import com.webset.set.financiamiento.dto.ContratoCreditoDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface FinanciamientoModificacionCDao {
	public List<AmortizacionCreditoDto> selectAmortizaciones(String psIdContrato,int piDisposicion,boolean pbCambioTasa ,String psTipoMenu ,String psProyecto ,int piCapital);
	public List<LlenaComboGralDto> obtenerContratos(int noEmpresa);
	public List<LlenaComboGralDto> obtenerEmpresas(int idUsuario, boolean bMantenimiento);	
	public List<LlenaComboGralDto> obtenerDisposiciones(String linea, boolean estatus);
	public List<AmortizacionCreditoDto> selectNombreTasa(String trim);
	public List<LlenaComboGralDto> consultarTasa();
	public List<ContratoCreditoDto>funSQLTasa(String psTasa);
	public int updTasaVariableDisp(String vsLinea, int vlDisp, String vsTipoTasa, String vsIdTasaBasa,
			String vdValorTasa, String vdPuntos);
	public int updateAmortizacion(String contrato, int disposicion, int i, double j, double vdInteres,
			String vdValTasaVig, String vdValTasaFij, String vsIdTasaBasa, String vdBase, String vdPuntos,
			String vsTipoTasa, Date vdFecCorte, String string, boolean vbRenta, String txtRenta, String txtIva,
			Date vdFecVen, boolean bFecha, Date vdFecIni);
	public int obtieneFolioAmort(String contrato, int disposicion);
}
