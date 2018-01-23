package com.webset.set.seguridad.exceptions;

public class BusinessException extends Exception{


	private static final long serialVersionUID = 1L;

	public BusinessException(String mensaje){
		super(mensaje);
	}
}
