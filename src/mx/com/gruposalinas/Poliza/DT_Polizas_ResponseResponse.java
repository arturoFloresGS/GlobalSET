/**
 * DT_Polizas_ResponseResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package mx.com.gruposalinas.Poliza;

public class DT_Polizas_ResponseResponse  implements java.io.Serializable {
    private java.lang.String NO_EMPRESA;

    private java.lang.String NO_DOC_SAP;

    private java.lang.String SECUENCIA;

    private java.lang.String ID_POLIZA_SAP;

    private java.lang.String MENSAJE;

    private java.lang.String DOC_POLIZA_SAP;

    public DT_Polizas_ResponseResponse() {
    }

    public DT_Polizas_ResponseResponse(
           java.lang.String NO_EMPRESA,
           java.lang.String NO_DOC_SAP,
           java.lang.String SECUENCIA,
           java.lang.String ID_POLIZA_SAP,
           java.lang.String MENSAJE,
           java.lang.String DOC_POLIZA_SAP) {
           this.NO_EMPRESA = NO_EMPRESA;
           this.NO_DOC_SAP = NO_DOC_SAP;
           this.SECUENCIA = SECUENCIA;
           this.ID_POLIZA_SAP = ID_POLIZA_SAP;
           this.MENSAJE = MENSAJE;
           this.DOC_POLIZA_SAP = DOC_POLIZA_SAP;
    }


    /**
     * Gets the NO_EMPRESA value for this DT_Polizas_ResponseResponse.
     * 
     * @return NO_EMPRESA
     */
    public java.lang.String getNO_EMPRESA() {
        return NO_EMPRESA;
    }


    /**
     * Sets the NO_EMPRESA value for this DT_Polizas_ResponseResponse.
     * 
     * @param NO_EMPRESA
     */
    public void setNO_EMPRESA(java.lang.String NO_EMPRESA) {
        this.NO_EMPRESA = NO_EMPRESA;
    }


    /**
     * Gets the NO_DOC_SAP value for this DT_Polizas_ResponseResponse.
     * 
     * @return NO_DOC_SAP
     */
    public java.lang.String getNO_DOC_SAP() {
        return NO_DOC_SAP;
    }


    /**
     * Sets the NO_DOC_SAP value for this DT_Polizas_ResponseResponse.
     * 
     * @param NO_DOC_SAP
     */
    public void setNO_DOC_SAP(java.lang.String NO_DOC_SAP) {
        this.NO_DOC_SAP = NO_DOC_SAP;
    }


    /**
     * Gets the SECUENCIA value for this DT_Polizas_ResponseResponse.
     * 
     * @return SECUENCIA
     */
    public java.lang.String getSECUENCIA() {
        return SECUENCIA;
    }


    /**
     * Sets the SECUENCIA value for this DT_Polizas_ResponseResponse.
     * 
     * @param SECUENCIA
     */
    public void setSECUENCIA(java.lang.String SECUENCIA) {
        this.SECUENCIA = SECUENCIA;
    }


    /**
     * Gets the ID_POLIZA_SAP value for this DT_Polizas_ResponseResponse.
     * 
     * @return ID_POLIZA_SAP
     */
    public java.lang.String getID_POLIZA_SAP() {
        return ID_POLIZA_SAP;
    }


    /**
     * Sets the ID_POLIZA_SAP value for this DT_Polizas_ResponseResponse.
     * 
     * @param ID_POLIZA_SAP
     */
    public void setID_POLIZA_SAP(java.lang.String ID_POLIZA_SAP) {
        this.ID_POLIZA_SAP = ID_POLIZA_SAP;
    }


    /**
     * Gets the MENSAJE value for this DT_Polizas_ResponseResponse.
     * 
     * @return MENSAJE
     */
    public java.lang.String getMENSAJE() {
        return MENSAJE;
    }


    /**
     * Sets the MENSAJE value for this DT_Polizas_ResponseResponse.
     * 
     * @param MENSAJE
     */
    public void setMENSAJE(java.lang.String MENSAJE) {
        this.MENSAJE = MENSAJE;
    }


    /**
     * Gets the DOC_POLIZA_SAP value for this DT_Polizas_ResponseResponse.
     * 
     * @return DOC_POLIZA_SAP
     */
    public java.lang.String getDOC_POLIZA_SAP() {
        return DOC_POLIZA_SAP;
    }


    /**
     * Sets the DOC_POLIZA_SAP value for this DT_Polizas_ResponseResponse.
     * 
     * @param DOC_POLIZA_SAP
     */
    public void setDOC_POLIZA_SAP(java.lang.String DOC_POLIZA_SAP) {
        this.DOC_POLIZA_SAP = DOC_POLIZA_SAP;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DT_Polizas_ResponseResponse)) return false;
        DT_Polizas_ResponseResponse other = (DT_Polizas_ResponseResponse) obj;
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
            ((this.SECUENCIA==null && other.getSECUENCIA()==null) || 
             (this.SECUENCIA!=null &&
              this.SECUENCIA.equals(other.getSECUENCIA()))) &&
            ((this.ID_POLIZA_SAP==null && other.getID_POLIZA_SAP()==null) || 
             (this.ID_POLIZA_SAP!=null &&
              this.ID_POLIZA_SAP.equals(other.getID_POLIZA_SAP()))) &&
            ((this.MENSAJE==null && other.getMENSAJE()==null) || 
             (this.MENSAJE!=null &&
              this.MENSAJE.equals(other.getMENSAJE()))) &&
            ((this.DOC_POLIZA_SAP==null && other.getDOC_POLIZA_SAP()==null) || 
             (this.DOC_POLIZA_SAP!=null &&
              this.DOC_POLIZA_SAP.equals(other.getDOC_POLIZA_SAP())));
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
        if (getSECUENCIA() != null) {
            _hashCode += getSECUENCIA().hashCode();
        }
        if (getID_POLIZA_SAP() != null) {
            _hashCode += getID_POLIZA_SAP().hashCode();
        }
        if (getMENSAJE() != null) {
            _hashCode += getMENSAJE().hashCode();
        }
        if (getDOC_POLIZA_SAP() != null) {
            _hashCode += getDOC_POLIZA_SAP().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DT_Polizas_ResponseResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://gruposalinas.com.mx/Poliza", ">DT_Polizas_Response>Response"));
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ID_POLIZA_SAP");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ID_POLIZA_SAP"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MENSAJE");
        elemField.setXmlName(new javax.xml.namespace.QName("", "MENSAJE"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("DOC_POLIZA_SAP");
        elemField.setXmlName(new javax.xml.namespace.QName("", "DOC_POLIZA_SAP"));
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
