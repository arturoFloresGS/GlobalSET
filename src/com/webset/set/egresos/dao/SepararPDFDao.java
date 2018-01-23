package com.webset.set.egresos.dao;

import com.webset.set.utilerias.dto.ConfiguraSetDto;

public interface SepararPDFDao {
	//Definicion de la interfaz
	ConfiguraSetDto consultaConfiguraSet(int indice);
	int consultaActualizaMovimiento(String sNoFolio, String sBenef, String sFecValorFormato, String sImporte, String sChequera);
}
