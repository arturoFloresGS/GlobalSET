package com.webset.set.derivados.service;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.webset.set.derivados.dto.DatosExcelFwsDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.LlenarConsultaFws;

public interface ForwardsService {

	public List<LlenaComboEmpresasDto> obtenerEmpresas(int noEmpresaRaiz);

	public List<DatosExcelFwsDto> validarDatosExcel(String datos);

	public String insertaForward(String matrizDatos, int usuario);

	public String exportaExcel(String datos, String opcion);

	public List<LlenaComboGralDto> obtenerBanco(int idEmp, String idDiv);

	public List<LlenaComboGralDto> obtenerdivisas(int bExistentes, int idDiv);

	public List<LlenaComboGralDto> obtenerChequeraBanco(int noEmpresa, String idDivisa, int idBanco);

	public List<LlenarConsultaFws> llenarGrid(boolean foco, int tipo, String fecA, String fecV, int noEm);


	public HSSFWorkbook reportFwd(String fwd);


	public HSSFWorkbook reportePersonas2(String tipoPersona, String foco, String tipo, String fecA, String fecV);

	String agregarFwd(int noEmpresa, String idDivVenta, int bancoCargo, String cheCargo, String idDivCompra,
			int bancoAbono, String cheAbono, int formaPago, double importePago, double importeFwd, double tipoCambio,
			String fecAlta, String fecVencimiento, int noInstitucion, String nomContacto, int idBancoBenef,
			String idCheBenef, String rubroCarg, String subRubroCarg, String rubroAbono, String subRubroAbono,
			String estatusMov, String estatusImporte, String firmante1, String firmante2, String noDocto, double spot,
			double pntsFwd, String desc_institucion,int usuario);

	public String consultaBanco(int id_banco);

	public String eliminaForward(String matrizDatos, int usuario);

	public String modificaForward(String matrizDatos, int usuario);

	public String swapForward(String matrizDatos, int usuario);


}
