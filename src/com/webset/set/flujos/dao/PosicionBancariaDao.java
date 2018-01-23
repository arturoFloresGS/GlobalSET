package com.webset.set.flujos.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.flujos.dto.PosicionBancariaDto;

public interface PosicionBancariaDao {
	public List<PosicionBancariaDto> buscaDivisas();
	public List<PosicionBancariaDto> posicionBancariaBE();
	public List<PosicionBancariaDto> posicionBancariaB();
	public List<PosicionBancariaDto> posicionBancariaC();
	public List<PosicionBancariaDto> posicionBancariaE();
	public List<PosicionBancariaDto> posicionBancariaR();
	public List<PosicionBancariaDto> buscaDetalleBE(List<Map<String, String>> datosGrid, List<Map<String, String>> parametros);
	public List<PosicionBancariaDto> obtenerCuentas(String bancos, String empresas, int iSelec);
	public List<PosicionBancariaDto> obtieneFlujoEmp(String bancos, String ctas);
	public List<PosicionBancariaDto> obtieneCuentaEmpresa(String sTipoMovto, String sBancos, String ctas, int iTipoSelec, List<Map<String, String>> parametros);
	public List<PosicionBancariaDto> obtieneSaldodiario(String bancos, String ctas, boolean chkSaldos, String fecha, int noEmpresa, String sDivisa);
	public List<PosicionBancariaDto> obtieneFichaCuenta(String sTipoMovto, String sBancos, String ctas, int iTipoSelec, List<Map<String, String>> parametros);
}
