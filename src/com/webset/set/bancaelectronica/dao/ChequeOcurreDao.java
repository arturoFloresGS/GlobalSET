package com.webset.set.bancaelectronica.dao;

import java.util.Date;
import java.util.List;

import com.webset.set.bancaelectronica.dto.CriterioBusquedaDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.UsuarioLoginDto;

public interface ChequeOcurreDao {
	List<LlenaComboEmpresasDto> obtenerEmpresas(int idUsuario);
	List<LlenaComboGralDto> llenaComboBanco(int noEmpresa);
	List<MovimientoDto> consultarCheques(int noEmpresa, int idBanco);
	String consultarConfiguraSet(int i);
	int obtenFolio(String string);
	List<MovimientoDto> seleccionarRegistros(String ps_folios);
	boolean insertarDetArchTransfer(String ps_nom_archivo, int noFolioDet, String noDocto, Date fecValor,
			String idChequera, int idBanco, int i, String noCliente, String string, double importe, String beneficiario,
			int sucursal, int plaza, String ps_concepto);
	boolean insertarArchTransfer(String ps_nom_archivo, int idBanco, double pd_importe, int pi_registros,
			UsuarioLoginDto usuarioLoginDto);
	boolean actulizaFolios(String ps_folios);
	int actualizarMovimiento(int i);
	int regresaEstatusMovimiento(int folio);
	int eliminaArchivosErroneos(String nomArch);
	List<MovimientoDto> consultaCheques(CriterioBusquedaDto dtoBus);
}
