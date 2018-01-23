/**
 * DT_CancelacionCompensacion_ResponseCancelaciones.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package mx.com.gruposalinas.CancelacionCompensacion;

public class DT_CancelacionCompensacion_ResponseCancelaciones  implements java.io.Serializable {
    private java.lang.String NO_EMPRESA;

    private java.lang.String NO_DOC_SAP;

    private java.lang.String EJERCICIO;

    private java.lang.String MSG_ERROR;

    private java.lang.String DOC_POLIZA_SAP;

    public DT_CancelacionCompensacion_ResponseCancelaciones() {
    }

    public DT_CancelacionCompensacion_ResponseCancelaciones(
           java.lang.String NO_EMPRESA,
           java.lang.String NO_DOC_SAP,
           java.lang.String EJERCICIO,
           java.lang.String MSG_ERROR,
           java.lang.String DOC_POLIZA_SAP) {
           this.NO_EMPRESA = NO_EMPRESA;
           this.NO_DOC_SAP = NO_DOC_SAP;
           this.EJERCICIO = EJERCICIO;
           this.MSG_ERROR = MSG_ERROR;
           this.DOC_POLIZA_SAP = DOC_POLIZA_SAP;
    }


    /**
     * Gets the NO_EMPRESA value for this DT_CancelacionCompensacion_ResponseCancelaciones.
     * 
     * @return NO_EMPRESA
     */
    public java.lang.String getNO_EMPRESA() {
        return NO_EMPRESA;
    }


    /**
     * Sets the NO_EMPRESA value for this DT_CancelacionCompensacion_ResponseCancelaciones.
     * 
     * @param NO_EMPRESA
     */
    public void setNO_EMPRESA(java.lang.String NO_EMPRESA) {
        this.NO_EMPRESA = NO_EMPRESA;
    }


    /**
     * Gets the NO_DOC_SAP value for this DT_CancelacionCompensacion_ResponseCancelaciones.
     * 
     * @return NO_DOC_SAP
     */
    public java.lang.String getNO_DOC_SAP() {
        return NO_DOC_SAP;
    }


    /**
     * Sets the NO_DOC_SAP value for this DT_CancelacionCompensacion_ResponseCancelaciones.
     * 
     * @param NO_DOC_SAP
     */
    public void setNO_DOC_SAP(java.lang.String NO_DOC_SAP) {
        this.NO_DOC_SAP = NO_DOC_SAP;
    }


    /**
     * Gets the EJERCICIO value for this DT_CancelacionCompensacion_ResponseCancelaciones.
     * 
     * @return EJERCICIO
     */
    public java.lang.String getEJERCICIO() {
        return EJERCICIO;
    }


    /**
     * Sets the EJERCICIO value for this DT_CancelacionCompensacion_ResponseCancelaciones.
     * 
     * @param EJERCICIO
     */
    public void setEJERCICIO(java.lang.String EJERCICIO) {
        this.EJERCICIO = EJERCICIO;
    }


    /**
     * Gets the MSG_ERROR value for this DT_CancelacionCompensacion_ResponseCancelaciones.
     * 
     * @return MSG_ERROR
     */
    public java.lang.String getMSG_ERROR() {
        return MSG_ERROR;
    }


    /**
     * Sets the MSG_ERROR value for this DT_CancelacionCompensacion_ResponseCancelaciones.
     * 
     * @param MSG_ERROR
     */
    public void setMSG_ERROR(java.lang.String MSG_ERROR) {
        this.MSG_ERROR = MSG_ERROR;
    }


    /**
     * Gets the DOC_POLIZA_SAP value for this DT_CancelacionCompensacion_ResponseCancelaciones.
     * 
     * @return DOC_POLIZA_SAP
     */
    public java.lang.String getDOC_POLIZA_SAP() {
        return DOC_POLIZA_SAP;
    }


    /**
     * Sets the DOC_POLIZA_SAP value for this DT_CancelacionCompensacion_ResponseCancelaciones.
     * 
     * @param DOC_POLIZA_SAP
     */
    public void setDOC_POLIZA_SAP(java.lang.String DOC_POLIZA_SAP) {
        this.DOC_POLIZA_SAP = DOC_POLIZA_SAP;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof DT_CancelacionCompensacion_ResponseCancelaciones)) return false;
        DT_CancelacionCompensacion_ResponseCancelaciones other = (DT_CancelacionCompensacion_ResponseCancelaciones) obj;
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
            ((this.MSG_ERROR==null && other.getMSG_ERROR()==null) || 
             (this.MSG_ERROR!=null &&
              this.MSG_ERROR.equals(other.getMSG_ERROR()))) &&
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
        if (getEJERCICIO() != null) {
            _hashCode += getEJERCICIO().hashCode();
        }
        if (getMSG_ERROR() != null) {
            _hashCode += getMSG_ERROR().hashCode();
        }
        if (getDOC_POLIZA_SAP() != null) {
            _hashCode += getDOC_POLIZA_SAP().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(DT_CancelacionCompensacion_ResponseCancelaciones.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://gruposalinas.com.mx/CancelacionCompensacion", ">DT_CancelacionCompensacion_Response>Cancelaciones"));
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
        elemField.setFieldName("MSG_ERROR");
        elemField.setXmlName(new javax.xml.namespace.QName("", "MSG_ERROR"));
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
