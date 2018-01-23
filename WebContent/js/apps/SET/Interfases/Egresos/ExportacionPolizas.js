/**
 * 
 */

Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.Interfaces.Egresos.ExportacionPolizas');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	var PF = apps.SET.tabID+'.'; 
    Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init();
	
	///variables
	NS.sm = new Ext.grid.CheckboxSelectionModel
	NS.idGrupoEmpresa = -1;
	
	//////////////funciones////////////////////
	
	function cambiarFecha(fecha){
		var mesArreglo=new Array(11);
		mesArreglo[0]="Jan";mesArreglo[1]="Feb";mesArreglo[2]="Mar";mesArreglo[3]="Apr";
		mesArreglo[4]="May";mesArreglo[5]="Jun";mesArreglo[6]="Jul";mesArreglo[7]="Aug";mesArreglo[8]="Sep";
		mesArreglo[9]="Oct";mesArreglo[10]="Nov";mesArreglo[11]="Dec";
		var mesDate=fecha.substring(4,7);
		var dia=fecha.substring(8,10);
		var anio=fecha.substring(11,15);
		for(var i=0;i<12;i=i+1){
			if(mesArreglo[i]===mesDate){
			var mes=i+1;
				if(mes<10)
					var mes='0'+mes;
			}
		}		
		var fechaString=''+dia+'/'+mes+'/'+anio;
		return fechaString;		
	}
	
	//Formato de un numero a monetario
	NS.formatNumber = function(num,prefix){
		num = num.toString();
		if(num.indexOf('.')>-1){
			if(num.substring(num.indexOf('.')).length<3){
				num = num + '0';
			}
		}
		else{
			num = num + '.00';
		}
		prefix = prefix || '';
		var splitStr = num.split('.');
		var splitLeft = splitStr[0];
		var splitRight = splitStr.length > 1 ? '.' + splitStr[1] : '';
		var regx = /(\d+)(\d{3})/;
		while (regx.test(splitLeft)) {
			splitLeft = splitLeft.replace(regx, '$1' + ',' + '$2');
		}
			return prefix + splitLeft + splitRight;
	};
	
	NS.buscar = function() {
		if(NS.idGrupoEmpresa<0) {
			Ext.Msg.alert('Información SET','Seleccione un Grupos de empresas.');
			return;
		}
		NS.storeExportacionPolizas.baseParams.idGrupoEmpresa = NS.idGrupoEmpresa + '';
		NS.storeExportacionPolizas.baseParams.origen = NS.idOrigen + '';
		NS.storeExportacionPolizas.baseParams.fecIni = cambiarFecha('' + Ext.getCmp(PF + 'fechaInicial').getValue());
		NS.storeExportacionPolizas.baseParams.fecFin = cambiarFecha('' + Ext.getCmp(PF + 'fechaFinal').getValue());
		NS.storeExportacionPolizas.load();
		
	}
	
	//////////////Stores///////////////////////
	
	//store  GrupoEmpresas
    NS.storeGrupoEmpresas = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			idUsuario:NS.idUsuario
		},
		root: '',
		paramOrder:['idUsuario'],
		root: '',
		directFn: ExportacionPolizasAction.llenarComboGrupoEmpresas, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGrupoEmpresas, msg:"Cargando..."});
				if(records.length == null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen empresas asignadas al usuario: '+NS.idUsuario);
					return;
				}
		 		//Se agrega la opcion todas a storeGrupoEmpresas
	 			var recordsStoreGruEmp = NS.storeGrupoEmpresas.recordType;	
				var todas = new recordsStoreGruEmp({
			       	id : 0,
			       	descripcion : '***************TODAS***************'
		       	});
		   		NS.storeGrupoEmpresas.insert(0,todas);
			}
		}
	}); 
    
    NS.datosComboTipo = [
                  	   ['SET', 'SET'],
                  	   ['SAP', 'SAP']		       			
                  	];	       		
                  	                   	 
  	NS.storeTipo = new Ext.data.SimpleStore({
  		idProperty: 'idTipo',
  	    fields: [
  	             {name: 'idTipo'},
  	             {name: 'descTipo'}
  	             ]
  	});

  	NS.storeTipo.loadData(NS.datosComboTipo);

    //Store para llenar el grid principal
 	NS.storeExportacionPolizas = new Ext.data.DirectStore({
 	 	paramsAsHash: false,
 	 	baseParams: {
			idGrupoEmpresa:-1,
			origen: '',
			fecIni:'',
			fecFin:''
		},
		root: '',
		paramOrder:['idGrupoEmpresa','origen','fecIni','fecFin'],
		directFn: ExportacionPolizasAction.consultaPolizasExportar,
		fields: [
			{name: 'bServicios'},
			{name: 'descGrupo'},
			{name: 'descRubro'},
			{name: 'descGrupoFlujo'},
			{name: 'descBancoBenef'},
			{name: 'idChequeraBenef'},
			{name: 'noDocto'},
			{name: 'referencia'},
			{name: 'importe'},
			{name: 'idDivisa'},
			{name: 'fecValor'},
			{name: 'descFormaPago'},
			{name: 'noFolioDet'},
			{name: 'idEstatusMov'},
			{name: 'idBanco'},
			{name: 'idRubro'},
			{name: 'idGrupoCupo'},
			{name: 'noEmpresa'},
			{name: 'idFormaPago'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeExportacionPolizas, msg:"Buscando..."});
				Ext.MessageBox.hide();
				
				if(records.length==null || records.length<=0){
					Ext.Msg.alert('Información  SET','No existen datos con los parametros de búsqueda');
				}
				else{
					var sumMn=0;
          			var sumDls=0;
          			for(var k=0; k<records.length; k++) {
          				if(records[k].get('idDivisa')=='MN')
          					sumMn = sumMn + records[k].get('importe');
          				else
          					sumDls = sumDls + records[k].get('importe');
          			}
          			NS.montoTotalMN=sumMn;
          			NS.montoTotalDls=sumDls;
          			Ext.getCmp(PF+'txtImporteTotalMn').setValue(NS.formatNumber(NS.montoTotalMN));
          			Ext.getCmp(PF+'txtImporteTotalDlls').setValue(NS.formatNumber(NS.montoTotalDls));
          			Ext.getCmp(PF+'txtNumeroRegistros').setValue(records.length);
				}
			}
		}
	});
	
	
	//////////////componenetes////////////////
	NS.lblEmpresa = new Ext.form.Label({
		text: 'Empresa',
		x: 10,
		y: 0
	});
	
	NS.lblOrigen = new Ext.form.Label({
		text: 'Origen',
		x: 330,
		y: 0
	});
	
	NS.lblRangoFechas = new Ext.form.Label({
		text: 'Rango de Fechas',
		x: 500,
		y: 0
	});
	
	NS.lblNumeroRegistros = new Ext.form.Label({
		text: 'Numero de Registros',
		x: 10,
		y: 350
	});
	
	NS.lblTotalMn = new Ext.form.Label({
		text: 'Importe Total MN.',
		x: 140,
		y: 350
	});
	
	NS.lblTotalDlls = new Ext.form.Label({
		text: 'Importe Total DLS.',
        x: 310,
        y: 350
    });
	
	NS.txtNumeroRegistros = new Ext.form.TextField({
		id: PF + 'txtNumeroRegistros',
		name: PF + 'txtNumeroRegistros',
		x: 10,
		y: 365,
		width: 100,
		readOnly: true
	});
	
	NS.txtImporteTotalMn = new Ext.form.TextField({
		id: PF + 'txtImporteTotalMn',
		name: PF + 'txtImporteTotalMn',
		x: 140,
		y: 365,
		width: 150,
		readOnly: true
	});
	
	NS.txtImporteTotalDlls = new Ext.form.TextField({
		id: PF + 'txtImporteTotalDlls',
		name: PF + 'txtImporteTotalDlls',
		x: 310,
		y: 365,
		width: 150,
		readOnly: true
	});
	//combo Grupo
	NS.cmbGrupoEmpresas = new Ext.form.ComboBox({
		store: NS.storeGrupoEmpresas
		,name: PF+'cmbGrupoEmpresas'
		,id: PF+'cmbGrupoEmpresas'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 10
        ,y: 15
        ,width: 290
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un grupo empresa'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
					NS.idGrupoEmpresa=combo.getValue();
				}
			}
		}
	});
	
	NS.storeGrupoEmpresas.load();
	
	NS.cmbOrigen = new Ext.form.ComboBox({
		store: NS.storeTipo
		,name: PF+'cmbOrigen'
		,id: PF+'cmbOrigen'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x: 330
        ,y: 15
        ,width: 150
		,valueField:'idTipo'
		,displayField:'descTipo'
		,autocomplete: true
		,emptyText: 'Seleccione un origen'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
					NS.idOrigen=combo.getValue();
				}
			}
		}
	});
	
	//grid principal
	NS.gridExportacionPolizas = new Ext.grid.GridPanel({
        store : NS.storeExportacionPolizas,
        id : 'gridExportacionPolizas',
        cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            },
            columns: [
                NS.sm,
                {header :'Poliza', width :80, sortable :true, dataIndex :'bServicios'},//, hidden: true},
                {header :'Grupo', width :90, sortable :true, dataIndex :'descGrupo' },
                {header :'Rubro', width :110, sortable :true, dataIndex :'descRubro'},
                {header :'Empresa', width :180, sortable :true, dataIndex :'descGrupoFlujo'},
                {header :'Banco', width :88, sortable :true, dataIndex :'descBancoBenef'},
                {header :'Chequera', width :80, sortable :true, dataIndex :'idChequeraBenef', hidden: true},
                {header :'No. Docto', width :200, sortable :true, dataIndex :'noDocto'},
                {header :'Referencia', width :200, sortable :true, dataIndex :'referencia'},
                {header :'Importe', width :100, sortable :true, dataIndex :'importe', css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
                {header :'Divisa', width :200, sortable :true, dataIndex :'idDivisa'},
                {header :'Fecha valor', width :200, sortable :true, dataIndex :'fecValor'},
                {header :'Forma de pago', width :200, sortable :true, dataIndex :'descFormaPago'},
                {header :'Folio Detalle', width :200, sortable :true, dataIndex :'noFolioDet', hidden: true},
                {header :'Id Estatus Movimiento', width :200, sortable :true, dataIndex :'idEstatusMov', hidden: true},
                {header :'Id Banco', width :200, sortable :true, dataIndex :'idBanco', hidden: true},
                {header :'Id Rubro', width :200, sortable :true, dataIndex :'idRubro', hidden: true},
                {header :'Id Grupo', width :200, sortable :true, dataIndex :'idGrupoCupo', hidden: true},
                {header :'No. Empresa', width :200, sortable :true, dataIndex :'noEmpresa', hidden: true},
                {header :'Id forma de pago', width :200, sortable :true, dataIndex :'idFormaPago', hidden: true},
                {header :'No. Poliza', width :200, sortable :true, dataIndex :'noPoliza', hidden: true},
                {header :'Fecha alta', width :200, sortable :true, dataIndex :'fecAlta', hidden: true},
                {header :'Division', width :200, sortable :true, dataIndex :'division', hidden: true},
                {header :'Libro de mayor', width :200, sortable :true, dataIndex :'equivale', hidden: true},
                {header :'Divisa Original', width :200, sortable :true, dataIndex :'idDivisaOriginal', hidden: true},
                {header :'No. Cliente', width :200, sortable :true, dataIndex :'noCliente', hidden: true},
                {header :'Tipo de Cambio', width :200, sortable :true, dataIndex :'cab', hidden: true},
                {header :'Concepto', width :200, sortable :true, dataIndex :'concepto', hidden: true},
                //
            ]
        }),
        sm: NS.sm,
        columnLines: true,
        x:0,
        y:0,
        width:940,
        height:175,
        frame:true,
        listeners:{
        	click: {
        		fn:function(e) {
          			
        		}
        	} 
        }
    });
	
	
	/////contenedor
	NS.ExportacionDePolizas = new Ext.FormPanel({
	    title: 'Exportacion de Polizas',
	    width: 1020,
	    height: 700,
	    frame: true,
	    //padding: 10,
	    autoScroll: true,
	    layout: 'absolute',
	    id: PF + 'exportacionDePolizas',
	    name: PF+ 'exportacionDePolizas',
	    renderTo: NS.tabContId,
	    items :[
	            {     	
	                xtype: 'fieldset',
	                title: '',
	                width: 1010,
	                height: 480,
	                x: 20,
	                y: 5,
	                layout: 'absolute',
	                items: [
	                        {
		                        xtype: 'fieldset',
		    	                title: 'Búsqueda',
		    	                width: 970,
		    	                height: 100,
		    	                x: 10,
		    	                y: 5,
		    	                layout: 'absolute',
		    	                items: [ 
			                        	NS.lblEmpresa,
			                        	NS.lblOrigen,
			                        	NS.lblRangoFechas,
			                        	NS.cmbGrupoEmpresas,
			                        	NS.cmbOrigen,
			                        	{
			                           		xtype:'datefield',
			                           		format:'d/m/Y',
			                           		id:PF+'fechaInicial',
			                           		name:PF+'fechaInicial',
			                           		value: apps.SET.FEC_HOY,
			                           		x: 500,
			                           		y: 15,
			                           		width:100,
			                           		allowBlank: false,
			                           		blankText:'La fecha inicial es requerida',
			                           		listeners:{
			                           			change:{
			                           				fn:function(caja,valor){
			        									
			                           				}
			                           			}
			                           		}
			                           	},
			                              {
			                           		xtype:'datefield',
			                           		format:'d/m/Y',
			                           		id:PF+'fechaFinal',
			                           		name:PF+'fechaFinal',
			                           		value: apps.SET.FEC_HOY,
			                           		x:630,
			                           		y:15,
			                           		width:100,
			                           		allowBlank: false,
			                           		blankText:'La fecha final es requerida',
			                           		listeners:{
			                           			change:{
			                           				fn:function(caja,valor){
			                           					if(Ext.getCmp(PF+'fechaInicial').getValue()>valor)
			        									{
			        										Ext.Msg.alert('información SET','La fecha inicial debe ser menor a la final');
			        										return;
			        									}	
			        								
			                           				}
			                           			}
			                           		}
			                           	},
			                           	{
			                                xtype: 'button',
			                                text: 'Buscar',
			                                x: 750,
			                                y: 15,
			                                width: 80,
			                                height: 22,
			                                id: PF+'btnBuscar',
			                                name: PF+'btnBuscar',
			                                listeners:{
			                                	click:{
			                                		fn:function(e){
			                                			NS.buscar();
			                                		}
			                                	}
			                                }
			                            }
			                        ]	
	                         },
	                         {
	                        	xtype: 'fieldset',
		    	                title: 'Polizas',
		    	                width: 970,
		    	                height: 220,
		    	                x: 10,
		    	                y: 110,
		    	                layout: 'absolute',
		    	                items: [ 
		    	                        NS.gridExportacionPolizas
		    	                        ]
	                         },
	                         NS.lblTotalDlls,
	                         NS.lblTotalMn,
	                         NS.lblNumeroRegistros,
	                         NS.txtImporteTotalDlls,
	                         NS.txtImporteTotalMn,
	                         NS.txtNumeroRegistros,
	                         {
								xtype: 'button',
								text: 'Limpiar',
								x: 680,
								y: 365,
								width: 80,
								height: 22,
								id: PF+'btnLimpiar',
								name: PF+'btnLimpiar',
								listeners:{
									click:{
										fn:function(e){
											NS.cmbGrupoEmpresas.reset();
											NS.cmbOrigen.reset();
											NS.gridExportacionPolizas.store.removeAll();
											NS.gridExportacionPolizas.getView().refresh();
											NS.montoTotalMN=0;
						          			NS.montoTotalDls=0;
						          			NS.idGrupoEmpresa=0;
						          			Ext.getCmp(PF+'txtImporteTotalMn').setValue(NS.formatNumber(NS.montoTotalMN));
						          			Ext.getCmp(PF+'txtImporteTotalDlls').setValue(NS.formatNumber(NS.montoTotalDls));
						          			Ext.getCmp(PF+'txtNumeroRegistros').setValue(0);
						          			Ext.getCmp(PF+'fechaInicial').setValue(apps.SET.FEC_HOY);
						          			Ext.getCmp(PF+'fechaFinal').setValue(apps.SET.FEC_HOY);
										}
									}
								}
	                         },
	                         {
								xtype: 'button',
								text: 'Imprimir',
								x: 780,
								y: 365,
								width: 80,
								height: 22,
								id: PF+'btnImprimir',
								name: PF+'btnImprimir',
								listeners:{
									click:{
										fn:function(e){
											
										}
									}
								}
	                         },
	                         {
									xtype: 'button',
									text: 'Ejecutar',
									x: 880,
									y: 365,
									width: 80,
									height: 22,
									id: PF+'btnEjecutar',
									name: PF+'btnEjecutar',
									listeners:{
										click:{
											fn:function(e){
												var matrizValidar = new Array ();
				                				var registrosSelec = NS.gridExportacionPolizas.getSelectionModel().getSelections();
				                				if(registrosSelec.length===0) {
				                					Ext.Msg.alert('Información SET','Es necesario seleccionar algun registro');
				                					return;
				                				}
				                				
				                				Ext.MessageBox.show({
											       title : 'Información SET',
											       msg : 'Procesando Polizas, espere por favor...',
											       width : 300,
											       wait : true,
											       progress:true,
											       waitConfig : {interval:200}
										   		});
				                				
				                				for(var i=0; i<registrosSelec.length; i++) {
													var regValidar = {};
					               					regValidar.noPoliza = registrosSelec[i].get('bServicios'); 
					               					regValidar.descGrupo = registrosSelec[i].get('descGrupo');
					               					regValidar.descRubro = registrosSelec[i].get('descRubro');
					               					regValidar.descGrupoFlujo = registrosSelec[i].get('descGrupoFlujo');
					               					regValidar.descBancoBenef = registrosSelec[i].get('descBancoBenef');
					               					regValidar.idChequeraBenef = registrosSelec[i].get('idChequeraBenef');
					               					regValidar.noDocto = registrosSelec[i].get('noDocto');
					               					regValidar.referencia = registrosSelec[i].get('referencia');
					               					regValidar.importe = registrosSelec[i].get('importe');
					               					regValidar.idDivisa = registrosSelec[i].get('idDivisa');
					               					regValidar.fecValor = registrosSelec[i].get('fecValor');
					               					regValidar.descFormaPago = registrosSelec[i].get('descFormaPago');
					               					regValidar.noFolioDet = registrosSelec[i].get('noFolioDet');
					               					regValidar.idEstatusMov = registrosSelec[i].get('idEstatusMov');
					               					regValidar.idBanco = registrosSelec[i].get('idBanco');
					               					regValidar.idRubro = registrosSelec[i].get('idRubro');
					               					regValidar.idGrupoCupo = registrosSelec[i].get('idGrupoCupo');
					               					regValidar.noEmpresa = registrosSelec[i].get('noEmpresa');
					               					regValidar.idFormaPago = registrosSelec[i].get('idFormaPago');
					               					regValidar.noPoliza = registrosSelec[i].get('noPoliza');
					               					regValidar.fecAlta = registrosSelec[i].get('fecAlta');
					               					regValidar.division = registrosSelec[i].get('division');
					               					regValidar.equivale = registrosSelec[i].get('equivale');
					               					regValidar.idDivisaOriginal = registrosSelec[i].get('idDivisaOriginal');
					               					regValidar.noCliente = registrosSelec[i].get('noCliente');
					               					regValidar.cab = registrosSelec[i].get('cab');
					               					regValidar.concepto = registrosSelec[i].get('concepto');
					               					
					               					matrizValidar[i] = regValidar;
				                				}
				                				
				                				var jsonString = Ext.util.JSON.encode(matrizValidar);	
				                				ExportacionPolizasAction.ejecutarExportacionPolizas(jsonString, function(result, e){
													if(result!=null && result!=undefined && result!=''){
														//alert('parte verdadera');
														Ext.Msg.alert('Información SET',' '+result.mensaje);	
														//result.put("mensaje
													}else{
														Ext.Msg.alert('Información SET',' '+result.mensaje);
													}
												});
												
												
											}
										}
									}
		                         }
	                        ]
	            }
	           ]
	});
	                    
	NS.ExportacionDePolizas.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});