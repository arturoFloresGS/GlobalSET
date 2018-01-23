/* 
 * @author: Victor H. Tello
 * @since: 17/Noviembre/2014
 */
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	var NS = Ext.namespace('apps.SET.Inversiones.CanastaInversion.Reportes');
	var PF = apps.SET.tabID + '.';
	NS.tabContId = apps.SET.tabContainerId;
	
	NS.labEmpresa = new Ext.form.Label({
		text: 'Empresa',
        x: 10
	});
	
	NS.storeEmpresa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: InversionesAction.llenarComboEmpresas, 
		idProperty: 'noEmpresa', 
		fields: [
			 {name: 'noEmpresa'},
			 {name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresa, msg:"Cargando..."});
				
				if(records.length === null || records.length <= 0)
					Ext.Msg.alert('SET', 'No existen empresas asignadas, consulte a su administrador de sistemas');
			}
		}
	}); 
	NS.storeEmpresa.load();
	
	NS.cmbEmpresa = new Ext.form.ComboBox({
		store: NS.storeEmpresa,
		name: PF+'cmbEmpresa',
		id: PF+'cmbEmpresa',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
    	x: 10,
        y: 15,
        width: 380,
		valueField:'noEmpresa',
		displayField:'nomEmpresa',
		autocomplete: true,
		emptyText: 'Seleccione una empresa',
		triggerAction: 'all',
		disabled: false,
		maskDisabled: false
	});
	
	NS.labDivisa = new Ext.form.Label({
		text: 'Divisa',
		x: 10,
		y: 50
	});
	
	NS.storeDivisas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: TraspasosAction.llenarComboDivisa,
		idProperty: 'idDivisa', 
		fields: [
			 {name: 'idDivisa'},
			 {name: 'descDivisa'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisa, msg:"Cargando..."});
			
				if(records.length==null || records.length<=0)
					Ext.Msg.alert('SET','No hay divisas asignadas, consulte a su administrador de sistemas');
			}
		}
	});
	NS.storeDivisas.load();
	
	NS.cmbDivisa = new Ext.form.ComboBox({
		store: NS.storeDivisas,
		name: PF + 'cmbDivisa',
		id: PF + 'cmbDivisa',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
        x: 10,
        y: 65,
        width: 150,
		valueField:'idDivisa',
		displayField:'descDivisa',
		autocomplete: true,
		emptyText: 'Seleccione una divisa',
		triggerAction: 'all'
	});
	
	NS.labFechas = new Ext.form.Label({
		text: 'Fechas',
		x: 180,
		y: 50
	});
	
	NS.txtFecIni = new Ext.form.DateField({
		id: PF + 'txtFecIni',
		x: 180,
		y: 65,
		width: 100,
		format: 'd/m/Y',
		value : apps.SET.FEC_HOY,
		allowBlank: false,
   		blankText:'Fecha inicial',
   		editable: false,
   		listeners:{
        	change:{
        		fn:function(caja, valor){
        			var fechaFin = Ext.getCmp(PF + 'txtFecFin').getValue();
        			
        			if(fechaFin < caja.getValue()){
        				BFwrk.Util.msgShow('La fecha inicial no puede ser mayor a la fecha final', 'INFO');
        			}
				}
			}
		}
	});
	
	NS.txtFecFin = new Ext.form.DateField({
		id: PF + 'txtFecFin',
		x: 290,
		y: 65,
		width: 100,
		format: 'd/m/Y',
		value: apps.SET.FEC_HOY,
		allowBlank: false,
   		blankText:'Fecha final',
   		editable: false,
   		listeners:{
        	change:{
        		fn:function(caja, valor){
        			var fechaIni = Ext.getCmp(PF + 'txtFecIni').getValue();
        			
        			if(fechaIni > caja.getValue()){
        				BFwrk.Util.msgShow('La fecha inicial no puede ser mayor a la fecha final', 'INFO');
        			}
				}
			}
		}
	});
	
	NS.optReportes = new Ext.form.RadioGroup({
		id: PF + 'optReportes',
		x: 70,
		columns: 2,
		items: [
	          {boxLabel: 'Barridos realizados', name: 'optSi', inputValue: 0, checked: true},  
	          {boxLabel: 'Barridos pendientes', name: 'optSi', inputValue: 1},
	          {boxLabel: 'Canastas activas', name: 'optSi', inputValue: 2},  
	          {boxLabel: 'Canastas vencidas', name: 'optSi', inputValue: 3},
	          {boxLabel: 'Inversiones realizadas a detalle', name: 'optSi', inputValue: 4}
	     ]
	});
	
	NS.contReportes = new Ext.form.FieldSet({
		title: 'Tipos de reporte',
		x: 10,
		y: 130,
		padding: 0,
		height: 120,
		layout: 'absolute',
		items: [
		    NS.optReportes
		]
	});
	
	NS.limpiar = function() {
    	NS.Reportes.getForm().reset();
    };
    
	NS.imprimir = function(){
		var nomReporte = '';
		var estatus = '';
		var desc = '';
		
		if(Ext.getCmp(PF + 'txtFecIni').getValue() > Ext.getCmp(PF + 'txtFecFin').getValue()) {
			BFwrk.Util.msgShow('La fecha inicial no puede ser mayor a la fecha final', 'INFO');
			return;
		}
		if(NS.cmbDivisa.getValue() === '') {
			BFwrk.Util.msgShow('Debe seleccionar una divisa','INFO');
			return;
		}
		
		var value = Ext.getCmp(PF + 'optReportes').getValue();
		var valor = parseInt(value.getGroupValue());
		var tipoRep = '0';
		
		if(valor === 0 || valor === 1) {
			nomReporte = 'Barridos';
			desc = 'realizados del';
			tipoRep = '8';
			
			if(valor === 1) {
				estatus = 'P';
				desc = 'pendientes del';
				tipoRep = '0';
			}
		}else if(valor === 2) {
    		nomReporte = 'CanastasPendientes';
		}else if(valor === 3) {
    		nomReporte = 'CanastasVencimientos';    		
		}else if(valor === 4)
    		nomReporte = 'InversionesDetalladas';
		
		if(valor === 2 || valor === 3 ) {
			var strParam = '?nomReporte=' + nomReporte;
			
			if(valor === 2){
				strParam += '&'+'nomParam1=pFecIni';
				strParam += '&'+'valParam1=' + BFwrk.Util.changeDateToString(''+Ext.getCmp(PF + 'txtFecIni').getValue());
				strParam += '&'+'nomParam2=pFecFin';
				strParam += '&'+'valParam2=' + BFwrk.Util.changeDateToString(''+Ext.getCmp(PF + 'txtFecFin').getValue());
				}
			
			if(valor === 3){
				strParam += '&'+'nomParam1=FECHA_INI';
				strParam += '&'+'valParam1=' + BFwrk.Util.changeDateToString(''+Ext.getCmp(PF + 'txtFecIni').getValue());
				strParam += '&'+'nomParam2=FECHA_FIN';
				strParam += '&'+'valParam2=' + BFwrk.Util.changeDateToString(''+Ext.getCmp(PF + 'txtFecFin').getValue());
				}			
			
			strParam += '&'+'nomParam3=noConcentradora';
			strParam += '&'+'valParam3=' + NS.cmbEmpresa.getValue();				
			strParam += '&'+'nomParam4=nomReporte';
			strParam += '&'+'valParam4=' + nomReporte;			
			strParam += '&'+'nomParam5=solic';
			strParam += '&'+'valParam5=' + '';
			
			window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParam);
			return;						
		}
		else{	
			var strParams = '?nomReporte=' + nomReporte;
			strParams += '&'+'nomParam1=noEmpresa';
			strParams += '&'+'valParam1=' + NS.cmbEmpresa.getValue();
			strParams += '&'+'nomParam2=pFecIni';
			strParams += '&'+'valParam2=' + BFwrk.Util.changeDateToString(''+Ext.getCmp(PF + 'txtFecIni').getValue());
			strParams += '&'+'nomParam3=pFecFin';
			strParams += '&'+'valParam3=' + BFwrk.Util.changeDateToString(''+Ext.getCmp(PF + 'txtFecFin').getValue());
			strParams += '&'+'nomParam4=idDivisa';
			strParams += '&'+'valParam4=' + NS.cmbDivisa.getValue();
			strParams += '&'+'nomParam5=nomReporte';
			strParams += '&'+'valParam5=' + nomReporte;
			strParams += '&'+'nomParam6=estatus';
			strParams += '&'+'valParam6=' + estatus;
			strParams += '&'+'nomParam7=usuario';
			strParams += '&'+'valParam7=' + apps.SET.iUserId;
			strParams += '&'+'nomParam8=desc';
			strParams += '&'+'valParam8=' + desc;
			strParams += '&'+'nomParam9=tipoRep';
			strParams += '&'+'valParam9=' + tipoRep;
			strParams += '&'+'nomParam10=solic';
			strParams += '&'+'valParam10=' + '';
			
			window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
			return;			
		}
	};
	
	NS.Reportes = new Ext.form.FormPanel({
	    title: 'Reportes de canasta',
	    width: 1020,
	    height: 700,
	    padding: 10,
	    layout: 'absolute',
	    renderTo: NS.tabContId,
	    autoScroll: true,
	    frame: true,
        items: [
            {
                xtype: 'fieldset',
                width: 670,
                height: 450,
                x: 200,
                y: 5,
                layout: 'absolute',
                items: [
                    NS.labEmpresa,
                    NS.cmbEmpresa,
                    NS.labDivisa,
                    NS.cmbDivisa,
                    NS.labFechas,
                    NS.txtFecIni,
                    NS.txtFecFin,
                    NS.contReportes,
                    {
		                xtype: 'button',
		                text: 'Imprimir',
		                x: 470,
		                y: 270,
		                width: 80,
		                listeners: {
		                	click: {
		                		fn:function(e) {
                    				NS.imprimir();
		                		}
		                	}
		                }
		            },
		            {
		                xtype: 'button',
		                text: 'Limpiar',
		                x: 560,
		                y: 270,
		                width: 80,
		                listeners: {
		                	click: {
		                		fn:function() {
		                			NS.limpiar();
		                		}
		                	}
		                }
		            }
                ]
            }
        ]
	});
	NS.Reportes.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});