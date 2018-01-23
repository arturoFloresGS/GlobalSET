package com.webset.set.utilerias;

/**
 * Clase que convierte una cantidad expresada en numeros a su equivalente en letra
 * ademas de leer centavos
 * @author USUARIO
 *
 */
public class ConvertirNumeroEnLetra {
    private static final String[] UNIDADES = { "", "UN ", "DOS ", "TRES ",
        "CUATRO ", "CINCO ", "SEIS ", "SIETE ", "OCHO ", "NUEVE ", "DIEZ ",
        "ONCE ", "DOCE ", "TRECE ", "CATORCE ", "QUINCE ", "DIECISEIS",
        "DIECISIETE", "DIECIOCHO", "DIECINUEVE", "VEINTE" };

	private static final String[] DECENAS = { "VEINTI", "TREINTA ", "CUARENTA ",
	        "CINCUENTA ", "SESENTA ", "SETENTA ", "OCHENTA ", "NOVENTA ",
	        "CIEN " };
	
	private static final String[] CENTENAS = { "CIENTO ", "DOSCIENTOS ",
	        "TRESCIENTOS ", "CUATROCIENTOS ", "QUINIENTOS ", "SEISCIENTOS ",
	        "SETECIENTOS ", "OCHOCIENTOS ", "NOVECIENTOS " };
	
	public static String convertNumberToLetter(double number) {
	return convertNumberToLetter(doubleToString(number));
	}

	private static String doubleToString(double numero){;
	return String.format("%.2f",numero);
	}

	public static String convertNumberToLetter(String number)
	        throws NumberFormatException {
	String converted = new String();

	String splitNumber[] = number.replace('.', '#')
	                .split("#");

	//Descompone el trio de billones
	int billon = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[0],
	        11))
	        + String.valueOf(getDigitAt(splitNumber[0], 10))
	        + String.valueOf(getDigitAt(splitNumber[0], 9)));
	if (billon == 1)
	        converted = "UN BILLON ";
	if (billon > 1)
	converted = convertNumber(String.valueOf(billon)) + "BILLONES ";
	
	// Descompone el trio de millones - ¡SGT!
	int millon = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[0],
	                8))
	                + String.valueOf(getDigitAt(splitNumber[0], 7))
	                + String.valueOf(getDigitAt(splitNumber[0], 6)));
	if (millon == 1)
	        converted += "UN MILLON ";
	if (millon > 1)
	        converted += convertNumber(String.valueOf(millon)) + "MILLONES ";
	
	// Descompone el trio de miles - ¡SGT!
	int miles = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[0],
	                5))
	                + String.valueOf(getDigitAt(splitNumber[0], 4))
	                + String.valueOf(getDigitAt(splitNumber[0], 3)));
	if (miles == 1)
	        converted += "MIL ";
	if (miles > 1)
	        converted += convertNumber(String.valueOf(miles)) + "MIL ";
	
	// Descompone el ultimo trio de unidades - ¡SGT!
	int cientos = Integer.parseInt(String.valueOf(getDigitAt(
	                splitNumber[0], 2))
	                + String.valueOf(getDigitAt(splitNumber[0], 1))
	                + String.valueOf(getDigitAt(splitNumber[0], 0)));
	if (cientos == 1)
	        converted += "UN";
	
	if (millon + miles + cientos == 0)
	        converted += "CERO";
	if (cientos > 1)
	        converted += convertNumber(String.valueOf(cientos));
	
	if((billon >= 1 || millon >= 1) && (miles == 0 && cientos == 0))
		converted += "DE PESOS";
	else
		converted += "PESOS";

	String centavos = (String.valueOf(getDigitAt(splitNumber[1], 1))
	        + String.valueOf(getDigitAt(splitNumber[1], 0)));
	
	converted += " " + centavos + "/100 M.N.";
	
	return converted;
	}

	private static String convertNumber(String number) {
	if (number.length() > 3)
	        throw new NumberFormatException(
	                        "La longitud maxima debe ser 3 digitos");

	String output = new String();
	if (getDigitAt(number, 2) != 0)
	        output = CENTENAS[getDigitAt(number, 2) - 1];
	
	int k = Integer.parseInt(String.valueOf(getDigitAt(number, 1))
	                + String.valueOf(getDigitAt(number, 0)));
	
	if (k <= 20)
	        output += UNIDADES[k];
	else {
	        if (k > 30 && getDigitAt(number, 0) != 0)
	                output += DECENAS[getDigitAt(number, 1) - 2] + "Y "
	                                + UNIDADES[getDigitAt(number, 0)];
	        else
	                output += DECENAS[getDigitAt(number, 1) - 2]
	                                + UNIDADES[getDigitAt(number, 0)];
	}

	// Caso especial con el 100
	if (getDigitAt(number, 2) == 1 && k == 0)
	        output = "CIEN";
	
	return output;
	}

	private static int getDigitAt(String origin, int position) {
	if (origin.length() > position && position >= 0)
	        return origin.charAt(origin.length() - position - 1) - 48;
	return 0;
	}
}
