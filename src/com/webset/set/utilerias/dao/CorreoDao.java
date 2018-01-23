package com.webset.set.utilerias.dao;

import java.util.List;
import java.util.Map;

/***********
 * @author YOSELINE E.C
 * Fecha: 04 de febrero del 2015
 *****/
public interface CorreoDao {
	public int funSQLActEmailGenerado(String psFoli_set, String pdImp_pago, String psNoDocto, String psNoempresa, String psBenef,String psMail);
	public List<Map<String, String>> funSQLDetallePagoAgrupado(String psNoDocto, String pdGrupo);
	public List<Map<String, String>> funSQLDatosCorreo(String string);
	public List<Map<String, String>> enviarCorreo();
	public String consultarConfiguraSet(int indice);
	public String consultarCorreoPorNoCliente(int no_cliente);
	public List<String> correosAdicionalesPorNoCliente(Integer noCliente);
	public List<Map<String, String>> funSQLDetalleNoPagoAgrupado(String psNoDocto, String pdGrupo);
}
