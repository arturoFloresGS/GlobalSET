package com.webset.set.impresion.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.impresion.dto.CartasChequesDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface CartasChequesDao {

	public List<CartasChequesDto> llenaEmpresa(String idClave);

	public List<CartasChequesDto> llenaProveedor();

	public List<CartasChequesDto> llenaSolicitante();

	public List<CartasChequesDto> llenaAutorizaciones();

	public List<CartasChequesDto> llenaAutorizaciones2();

	public List<CartasChequesDto> llenaClave(String fechaIni, String fechaFin);

	public List<LlenaComboGralDto> llenarComboBeneficiario(LlenaComboGralDto dto);

	public List<CartasChequesDto> obtieneDatos(String idProveedor, String banco, String tipo);

	public List<CartasChequesDto> llenaBanco(String idEmpresa);

	public int guardarCartaEmitida(Map<String, String> valor, String dif, String fechaImp);

	public String obtenerIdEmision();

	public int guardarDetalle(Map<String, String> beneficiario, List<Map<String, String>> valor);

	public List<CartasChequesDto> llenaChequera(String idEmpresa, String idBanco);

	public List<CartasChequesDto> llenaGrid(String idEmpresa, String idClave, String tipo, String idBanco, String tipoC,
			String op, String idChequera);

	public int eliminaEmision(String string);

	public int certificarCheque(String string);

	public List<CartasChequesDto> llenaBancoSP();


}
