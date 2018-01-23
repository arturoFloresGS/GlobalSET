/**
 * DT_ImpresionCheques_ResponseCheques.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package mx.com.gruposalinas.ImpresionCheques;

public class DT_ImpresionCheques_ResponseCheques  implements java.io.Serializable {
    private java.lang.String NO_EMPRESA;

    private java.lang.String NO_DOC_SAP;

    private java.lang.String EJERCICIO;

    private java.lang.String NO_CHEQUE;

    private java.lang.String MSG_ERROR;

    private java.lang.String SECUENCIA;

    public DT_ImpresionCheques_ResponseCheques() {
    }

    public DT_ImpresionCheques_ResponseCheques(
           java.lang.String NO_EMPRESA,
           java.lang.String NO_DOC_SAP,
           java.lang.String EJERCICIO,
           java.lang.String NO_CHEQUE,
           java.lang.String MSG_ERROR,
           java.lang.String SECUENCIA) {
           this.NO_EMPRESA = NO_EMPRESA;
           this.NO_DOC_SAP = NO_DOC_SAP;
           this.EJERCICIO = EJERCICIO;
           this.NO_CHEQUE = NO_CHEQUE;
           this.MSG_ERROR = MSG_ERROR;
           this.SECUENCIA = SECUENCIA;
    }


    /**
     * Gets the NO_EMPRESA value for this DT_ImpresionCheques_ResponseCheques.
     * 
     * @return NO_EMPRESA
     */
    public java.lang.String getNO_EMPRESA() {
        return NO_EMPRESA;
    }


    /**
     * Sets the NO_EMPRESA value for this DT_ImpresionCheques_ResponseCheques.
     * 
     * @param NO_EMPRESA
     */
    public void setNO_EMPRESA(java.lang.String NO_EMPRESA) {
        this.NO_EMPRESA = NO_EMPRESA;
    }


    /**
     * Gets the NO_DOC_SAP value for this DT_ImpresionCheques_ResponseCheques.
     * 
     * @return NO_DOC_SAP
     */
    public java.lang.String getNO_DOC_SAP() {
        return NO_DOC_SAP;
    }


    /**
     * Sets the NO_DOC_SAP value for this DT_ImpresionCheques_ResponseCheques.
     * 
     * @param NO_DOC_SAP
     */
    public void setNO_DOC_SAP(java.lang.String NO_DOC_SAP) {
        this.NO_DOC_SAP = NO_DOC_SAP;
    }


    /**
     * Gets the EJERCICIO value for this DT_ImpresionCheques_ResponseCheques.
     * 
     * @return EJERCICIO
     */
    public java.lang.String getEJERCICIO() {
        return EJERCICIO;
    }


    /**
     * Sets the EJERCICIO value for this DT_ImpresionCheques_ResponseCheques.
     * 
     * @param EJERCICIO
     */
    public void setEJERCICIO(java.lang.String EJERCICIO) {
        this.EJERCICIO = EJERCICIO;
    }


    /**
     * Gets the NO_CHEQUE value for this DT_ImpresionCheques_ResponseCheques.
     * 
     * @return NO_CHEQUE
     */
    public java.lang.String getNO_CHEQUE() {
        return NO_CHEQUE;
    }


    /**
     * Sets the NO_CHEQUE value for this DT_ImpresionCheques_ResponseCheques.
     * 
     * @param NO_CHEQUE
     */
    public void setNO_CHEQUE(java.lang.String NO_CHEQUE) {
        this.NO_CHEQUE = NO_CHEQUE;
    }


    /**
     * Gets the MSG_ERROR value for this DT_ImpresionCheques_ResponseCheques.
     * 
     * @return MSG_ERROR
     */
    public java.lang.String getMSG_ERROR() {
        return MSG_ERROR;
    }


    /**
     * Sets the MSG_ERROR value for this DT_ImpresionCheques_ResponseCheques.
     * 
     * @param MSG_ERROR
     */
    public void setMSG_ERROR(java.lang.String MSG_ERROR) {
        this.MSG_ERROR = MSG_ERROR;
    }


    /**
     * Gets the SECUENCIA value for this DT_ImpresionCheques_ResponseCheques.
     * 
     * @return SECUENCIA
     */
    public java.lang.String getSECUENCIA() {
        return SECUENCIA;
    }


    /**
     * Sets the SECUENCIA value for this DT_ImpresionCheques_ResponseCheques.
     * 
     * @param SECUENCIA
     */
    public void setSECUENCIA(java.lang.String SECUENCIA) {
        this.SECUENCIA = SECUENCIA;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DT_ImpresionCheques_ResponseCheques)) return false;
        DT_ImpresionCheques_ResponseCheques other = (DT_ImpresionCheques_ResponseCheques) obj;
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
            ((this.MSG_ERROR==null && other.getMSG_ERROR()==null) || 
             (this.MSG_ERROR!=null &&
              this.MSG_ERROR.equals(other.getMSG_ERROR()))) &&
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
        if (getMSG_ERROR() != null) {
            _hashCode += getMSG_ERROR().hashCode();
        }
        if (getSECUENCIA() != null) {
            _hashCode += getSECUENCIA().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DT_ImpresionCheques_ResponseCheques.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://gruposalinas.com.mx/ImpresionCheques", ">DT_ImpresionCheques_Response>cheques"));
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
        elemField.setFieldName("MSG_ERROR");
        elemField.setXmlName(new javax.xml.namespace.QName("", "MSG_ERROR"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SECUENCIA");
        elemField.setXmlName(new javax.xml.namespace.QName("", "SECUENCIA"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
