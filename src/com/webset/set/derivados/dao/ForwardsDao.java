package com.webset.set.derivados.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.derivados.dto.DatosExcelFwsDto;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.LlenarConsultaFws;



public interface ForwardsDao {

	public List<LlenaComboEmpresasDto> consultarEmpresas(int iEmpRaiz);

	public List<DatosExcelFwsDto> validarDatosExcel(String datos);

	public List<DatosExcelFwsDto> datosFwsFaltantes(String matrizDatos, int usuario);


	void insertarFwdCarga(int folio, String unidad_negocio, String divisa_venta, int banco_cargo,
			String chequera_cargo, String divisa_compra, int banco_abono, String chequera_abono, String forma_pago,
			Double importe_pago, Double importe_compra, Double tc, String fec_compra, String fec_vto, int institucion,
			String nom_contacto, int banco_benef, String chequera_benef, String rubro_cargo, String subrubro_cargo,
			String rubro_abono, String subrubro_abono, char estatus_mov, char estatus_imp, String firmante1,
			String firmante2, String no_docto, Double spot, Double puntos_forward,String referencia,String concepto);
	
	void funSQLInsert244(String unidad_negocio, String no_docto, int no_folio_param, int id_tipo_docto,
			String forma_pago, int usuario_alta, int id_tipo_operacion, int no_cuenta, String no_factura,
			String fec_valor, String fec_valor_original, String fec_operacion, String id_divisa, String fec_alta,
			Double importe, Double importe_original, Double tipo_cambio, int id_caja, String id_divisa_original,
			String referencia, String beneficiario, String concepto, int id_banco_benef, String id_chequera_benef,
			String id_leyenda, String id_chequera, int id_banco, String observacion, Boolean adu,
			int no_cliente, String solicita, String autoriza, int plaza, int sucursal, int grupo, int agrupa1,
			int agrupa2, int agrupa3, String id_rubro, Boolean cvDivisa, int no_pedido, int id_area, String id_grupo,
			String no_cheque);
	
	void funSQLInsert245(String unidad_negocio, String no_docto, int no_folio_param, int id_tipo_docto,
			String forma_pago, int usuario_alta, int id_tipo_operacion, int no_cuenta, String no_factura,
			String fec_valor, String fec_valor_original, String fec_operacion, String id_divisa, String fec_alta,
			Double importe, Double importe_original, Double tipo_cambio, int id_caja, String id_divisa_original,
			String referencia, String beneficiario, String concepto, int id_banco_benef, String id_chequera_benef,
			String id_leyenda, String id_chequera, int id_banco, String observacion, Boolean adu,
			int no_cliente, String solicita, String autoriza, int plaza, int sucursal, int grupo, int agrupa1,
			int agrupa2, int agrupa3, String id_rubro, Boolean cvDivisa, int no_pedido, int id_area, String id_grupo,
			String no_cheque);
	
	void funSQLInsert247(String unidad_negocio, String no_docto, int no_folio_param, int id_tipo_docto,
			String forma_pago, int usuario_alta, int id_tipo_operacion, int no_cuenta, String no_factura,
			String fec_valor, String fec_valor_original, String fec_operacion, String id_divisa, String fec_alta,
			Double importe, Double importe_original, Double tipo_cambio, int id_caja, String id_divisa_original,
			String referencia, String beneficiario, String concepto, int id_banco_benef, String id_chequera_benef,
			String id_leyenda, String id_chequera, int id_banco, String observacion, Boolean aDU, int no_cliente,
			String solicita, String autoriza, int plaza, int sucursal, int grupo, int agrupa1, int agrupa2, int agrupa3,
			String id_rubro, Boolean cVDivisa, int no_pedido, int id_area, String id_grupo, String no_cheque);

	
	void funSQLInsertFlujoProgramado(String id_grupo, int id_cuadrante, String id_rubro, String id_divisa,
			String fec_valor, int agrupa1, int agrupa2, int agrupa3, String unidad_negocio, Double importe);
	
	
	int funSqlGetFlujoProgramado(String id_grupo, int id_cuadrante, String id_rubro, String id_divisa,
			String fec_valor, String unidad_negocio);

	public int actualizarFolioReal(String string);

	public int seleccionarFolioReal(String string);

	void funSQLUpdateFlujoProgramado(String id_grupo, int id_cuadrante, String id_rubro, String id_divisa,
			String fec_valor, int agrupa1, int agrupa2, int agrupa3, String unidad_negocio, Double importePago);
	
	
	public Map<String, Object> ejecutarGenerador(GeneradorDto generadorDto);

	public List<LlenaComboGralDto> consultarBancos(int idEmp, String idDiv);

	public List<LlenaComboGralDto> consultarDivisas(int bExistentes, int idDiv);




	public List<LlenaComboGralDto> obtenerChequeraAbo(int noEmpresa, String idDivisa, int idBanco);


	

	public void insertarFwd(int noEmpresa, String idDivVenta, int bancoCargo, String cheCargo, String idDivCompra,
			int bancoAbono, String cheAbono, int formaPago, double importePago, double importeFwd, double tipoCambio,
			String fecAlta, String fecVencimiento, int noInstitucion, String nomContacto, int idBancoBenef,
			String idCheBenef, String rubroCarg, String subRubroCarg, String rubroAbono, String subRubroAbono,
			String estatusMov, String estatusImporte, String firmante1, String firmante2, String noDocto, double spot,
			double pntsFwd);


	public List<LlenarConsultaFws> consultarGrid(boolean foco, int tipo, String fecA, String fecV, int noEm);


	public List<Map<String, String>> reporteFwd(String fwd);


	public List<Map<String, String>> reportePersonas2(String tipoPersona, String foco, String tipo, String fecA,
			String fecV);

	public int obtenerCaja(int usuario);

	public String consultaBanco(int id_banco);

	public void eliminarForward(int folio, String unidad_negocio, String chequera_cargo, String chequera_abono,
			Double importe_pago, Double importe_compra, Double tc, String fec_compra, String fec_vto);

	public void modificaForward(int folio, String unidad_negocio, String chequera_cargo, String chequera_abono,
			Double spot, Double pts_forward,Double importe_pago, Double importe_compra, Double tc, String fec_compra, String fec_vto);


	public void swapForward(int folio, String unidad_negocio, String chequera_cargo, String chequera_abono,
			Double spot, Double puntos_forward,Double importe_pago, Double importe_compra, String fec_compra, String fec_vto);


	
	

	

	

	

	

	

	


	
}
