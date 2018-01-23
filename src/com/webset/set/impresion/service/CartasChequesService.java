package com.webset.set.impresion.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.webset.set.impresion.dto.CartasChequesDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

import net.sf.jasperreports.engine.JRDataSource;

public interface CartasChequesService {

	List<CartasChequesDto> llenaEmpresa(String idClave);

	List<CartasChequesDto> llenaProveedor();

	List<CartasChequesDto> llenaSolicitante();

	List<CartasChequesDto> llenaAutorizaciones();

	List<CartasChequesDto> llenaAutorizaciones2();

	List<CartasChequesDto> llenaClave(String fechaIni, String fechaFin);

	List<CartasChequesDto> llenaGrid(String idEmpresa, String idClave, String tipo, String idBanco, String tipoC, String op, String idChequera);

	List<LlenaComboGralDto> llenarComboBeneficiario(LlenaComboGralDto dto);

	List<CartasChequesDto> obtieneDatos(String idProveedor, String banco, String tipo);

	public String generaPDF(List<Map<String, String>> valorGson, List<Map<String, String>> beneficiarios, String dif, String tipo, String fechaImp);

	List<CartasChequesDto> llenaBanco(String idEmpresa);

	List<CartasChequesDto> llenaChequera(String idEmpresa, String idBanco);

	String certificarCheque(List<String> listFolios);

	String exportaExcel(String datos);

	List<CartasChequesDto> llenaBancoSP();

	//String generaPDF(List<Map<String, String>> valor, List<Map<String, String>> beneficiarios, String dif, String tipo);


}
