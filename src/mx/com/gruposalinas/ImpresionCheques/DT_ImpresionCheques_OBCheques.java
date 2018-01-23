/**
 * DT_ImpresionCheques_OBCheques.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package mx.com.gruposalinas.ImpresionCheques;

public class DT_ImpresionCheques_OBCheques  implements java.io.Serializable {
    private java.lang.String NO_EMPRESA;

    private java.lang.String NO_DOC_SAP;

    private java.lang.String EJERCICIO;

    private java.lang.String NO_CHEQUE;

    private java.lang.String CAUSA_REIMPRESION;

    private java.lang.String SECUENCIA;

    public DT_ImpresionCheques_OBCheques() {
    }

    public DT_ImpresionCheques_OBCheques(
           java.lang.String NO_EMPRESA,
           java.lang.String NO_DOC_SAP,
           java.lang.String EJERCICIO,
           java.lang.String NO_CHEQUE,
           java.lang.String CAUSA_REIMPRESION,
           java.lang.String SECUENCIA) {
           this.NO_EMPRESA = NO_EMPRESA;
           this.NO_DOC_SAP = NO_DOC_SAP;
           this.EJERCICIO = EJERCICIO;
           this.NO_CHEQUE = NO_CHEQUE;
           this.CAUSA_REIMPRESION = CAUSA_REIMPRESION;
           this.SECUENCIA = SECUENCIA;
    }


    /**
     * Gets the NO_EMPRESA value for this DT_ImpresionCheques_OBCheques.
     * 
     * @return NO_EMPRESA
     */
    public java.lang.String getNO_EMPRESA() {
        return NO_EMPRESA;
    }


    /**
     * Sets the NO_EMPRESA value for this DT_ImpresionCheques_OBCheques.
     * 
     * @param NO_EMPRESA
     */
    public void setNO_EMPRESA(java.lang.String NO_EMPRESA) {
        this.NO_EMPRESA = NO_EMPRESA;
    }


    /**
     * Gets the NO_DOC_SAP value for this DT_ImpresionCheques_OBCheques.
     * 
     * @return NO_DOC_SAP
     */
    public java.lang.String getNO_DOC_SAP() {
        return NO_DOC_SAP;
    }


    /**
     * Sets the NO_DOC_SAP value for this DT_ImpresionCheques_OBCheques.
     * 
     * @param NO_DOC_SAP
     */
    public void setNO_DOC_SAP(java.lang.String NO_DOC_SAP) {
        this.NO_DOC_SAP = NO_DOC_SAP;
    }


    /**
     * Gets the EJERCICIO value for this DT_ImpresionCheques_OBCheques.
     * 
     * @return EJERCICIO
     */
    public java.lang.String getEJERCICIO() {
        return EJERCICIO;
    }


    /**
     * Sets the EJERCICIO value for this DT_ImpresionCheques_OBCheques.
     * 
     * @param EJERCICIO
     */
    public void setEJERCICIO(java.lang.String EJERCICIO) {
        this.EJERCICIO = EJERCICIO;
    }


    /**
     * Gets the NO_CHEQUE value for this DT_ImpresionCheques_OBCheques.
     * 
     * @return NO_CHEQUE
     */
    public java.lang.String getNO_CHEQUE() {
        return NO_CHEQUE;
    }


    /**
     * Sets the NO_CHEQUE value for this DT_ImpresionCheques_OBCheques.
     * 
     * @param NO_CHEQUE
     */
    public void setNO_CHEQUE(java.lang.String NO_CHEQUE) {
        this.NO_CHEQUE = NO_CHEQUE;
    }


    /**
     * Gets the CAUSA_REIMPRESION value for this DT_ImpresionCheques_OBCheques.
     * 
     * @return CAUSA_REIMPRESION
     */
    public java.lang.String getCAUSA_REIMPRESION() {
        return CAUSA_REIMPRESION;
    }


    /**
     * Sets the CAUSA_REIMPRESION value for this DT_ImpresionCheques_OBCheques.
     * 
     * @param CAUSA_REIMPRESION
     */
    public void setCAUSA_REIMPRESION(java.lang.String CAUSA_REIMPRESION) {
        this.CAUSA_REIMPRESION = CAUSA_REIMPRESION;
    }


    /**
     * Gets the SECUENCIA value for this DT_ImpresionCheques_OBCheques.
     * 
     * @return SECUENCIA
     */
    public java.lang.String getSECUENCIA() {
        return SECUENCIA;
    }


    /**
     * Sets the SECUENCIA value for this DT_ImpresionCheques_OBCheques.
     * 
     * @param SECUENCIA
     */
    public void setSECUENCIA(java.lang.String SECUENCIA) {
        this.SECUENCIA = SECUENCIA;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DT_ImpresionCheques_OBCheques)) return false;
        DT_ImpresionCheques_OBCheques other = (DT_ImpresionCheques_OBCheques) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.NO_EMPRESA==null && other.getNO_EMPRESA()==null) || 
             (this.NO_EMPRESA!=null &&
              this.NO_EMPRESA.equals(other.getNO_EMPRESA()))) &&
            ((this.NO_DOC_SAP==null && other.getNO_DOC_SAP()==null) || 
             (this.NO_DOC_SAP!=null &&
              this.NO_DOC_SAP.equals(other.getNO_DOC_SAP()))) &&
            ((this.EJERCICIO==null && other.getEJERCICIO()==null) || 
             (this.EJERCICIO!=null &&
              this.EJERCICIO.equals(other.getEJERCICIO()))) &&
            ((this.NO_CHEQUE==null && other.getNO_CHEQUE()==null) || 
             (this.NO_CHEQUE!=null &&
              this.NO_CHEQUE.equals(other.getNO_CHEQUE()))) &&
            ((this.CAUSA_REIMPRESION==null && other.getCAUSA_REIMPRESION()==null) || 
             (this.CAUSA_REIMPRESION!=null &&
              this.CAUSA_REIMPRESION.equals(other.getCAUSA_REIMPRESION()))) &&
            ((this.SECUENCIA==null && other.getSECUENCIA()==null) || 
             (this.SECUENCIA!=null &&
              this.SECUENCIA.equals(other.getSECUENCIA())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getNO_EMPRESA() != null) {
            _hashCode += getNO_EMPRESA().hashCode();
        }
        if (getNO_DOC_SAP() != null) {
            _hashCode += getNO_DOC_SAP().hashCode();
        }
        if (getEJERCICIO() != null) {
            _hashCode += getEJERCICIO().hashCode();
        }
        if (getNO_CHEQUE() != null) {
            _hashCode += getNO_CHEQUE().hashCode();
        }
        if (getCAUSA_REIMPRESION() != null) {
            _hashCode += getCAUSA_REIMPRESION().hashCode();
        }
        if (getSECUENCIA() != null) {
            _hashCode += getSECUENCIA().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DT_ImpresionCheques_OBCheques.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://gruposalinas.com.mx/ImpresionCheques", ">DT_ImpresionCheques_OB>cheques"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("NO_EMPRESA");
        elemField.setXmlName(new javax.xml.namespace.QName("", "NO_EMPRESA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("NO_DOC_SAP");
        elemField.setXmlName(new javax.xml.namespace.QName("", "NO_DOC_SAP"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("EJERCICIO");
        elemField.setXmlName(new javax.xml.namespace.QName("", "EJERCICIO"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("NO_CHEQUE");
        elemField.setXmlName(new javax.xml.namespace.QName("", "NO_CHEQUE"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CAUSA_REIMPRESION");
        elemField.setXmlName(new javax.xml.namespace.QName("", "CAUSA_REIMPRESION"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SECUENCIA");
        elemField.setXmlName(new javax.xml.namespace.QName("", "SECUENCIA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
