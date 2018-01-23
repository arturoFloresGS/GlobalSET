Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	var NS = Ext.namespace('apps.SET.Inversiones.Operacion.ConsultaOrdenesInversion');
	var PF = apps.SET.tabID+'.';
	NS.tabContId = apps.SET.tabContainerId;
	
	NS.idUsuario = apps.SET.iUserId;
	var sName = "";
	NS.smCheck = new Ext.grid.CheckboxSelectionModel({
				singleSelect : true		
	});
	 
	NS.txtNoEmpresa = new Ext.form.TextField({
		x: 60,
	    y: 0,
	    width: 55,
	    tabIndex: 0,
	    name: PF + 'txtNoEmpresa',
	    id: PF + 'txtNoEmpresa',
	    value: apps.SET.NO_EMPRESA,
	    listeners:{
	    	change:{
	    		fn: function(caja, valor){
	    			var noEmp = BFwrk.Util.updateTextFieldToCombo(PF+'txtNoEmpresa', NS.cmbEmpresa.getId());
	    			Ext.getCmp(PF + 'cmdGrafica').setVisible(false);	
	    			    			
	    			if(noEmp == null) {
	    				Ext.getCmp(PF+'txtNoEmpresa').reset();
	    				NS.cmbEmpresa.reset();
	    			}
	    		}
	    	}
	    }
	});
	//store de cmbEmpresa
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
				Ext.getCmp(PF + 'cmdGrafica').setVisible(false);	
				
				if(records.length === null || records.length <= 0)
					Ext.Msg.alert('SET','No Existen empresas asignadas a este usuario, consulte a su administrador...');
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
    	x: 120,
        y: 0,
        width: 300,
		valueField:'noEmpresa',
		displayField:'nomEmpresa',
		autocomplete: true,
		emptyText: 'Seleccione una empresa',
		triggerAction: 'all',
		value: apps.SET.NOM_EMPRESA,
		disabled: false,
		maskDisabled: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
				 	BFwrk.Util.updateComboToTextField(PF+'txtNoEmpresa', NS.cmbEmpresa.getId());
				 	Ext.getCmp(PF + 'cmdGrafica').setVisible(false);	
					
				}
			}
		}
	});
	
	
	NS.storeOrdenesInversion = new Ext.data.DirectStore({
		baseParams:{
			noEmpresa: apps.SET.NO_EMPRESA,
			iUserId: NS.idUsuario
		},
		paramOrder:['noEmpresa','iUserId'],
		paramsAsHash: false,
		root: '',
		directFn: InversionesAction.obtenerOrdenesInversion, 
		//idProperty: 'noOrden', 
		fields: [
			{name: 'noOrden'},
			{name: 'bAutoriza'},
			{name: 'nombreCorto'},
			{name: 'plazo'},
			{name: 'importe'},
			{name: 'tasa'},
			{name: 'interes'},
			{name: 'isr'},
			{name: 'noContacto'},
			{name: 'fecAlta'},
			{name: 'fecVenc'},
			{name: 'idTipoValor'},
			{name: 'descTipoValor'},
			{name: 'idPapel'},
			{name: 'idDivisa'},
			{name: 'noCuenta'},
			{name: 'noFolioDet'},
			{name: 'idTipoOperacion'},
			{name: 'origenMov'},
			{name: 'idFormaPago'},
			{name: 'idTipoMovto'},
			{name: 'loteEntrada'},
			{name: 'bSalvoBuenCobro'},
			{name: 'idEstatusMov'},
			{name: 'bEntregado'},
			{name: 'idBanco'},
			{name: 'idChequera'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeOrdenesInversion, msg:"Buscando..."});
				var uImporte = 0;
				
				if(records.length === null || records.length <= 0)
				{
					Ext.getCmp(PF + 'cmdGrafica').setDisabled(true);
					Ext.Msg.alert('SET','No existen ordenes de inversión...');
					return;
				}
				else
				{
					for(var i = 0; i< records.length; i = i + 1)
					{
						uImporte = uImporte + records[i].get('importe');
					}
					Ext.getCmp(PF + 'txtTotal').setValue(BFwrk.Util.formatNumber(uImporte));
					
					//Graficas
		            Ext.getCmp(PF + 'cmdGrafica').setDisabled(false);
		            
		            sName = apps.SET.NO_EMPRESA;
				}
			}
		}
	});
	
	
	//Llamada para autorizar o desautorizar
	NS.autorizar = function(sAutorizar, iOrden){
		InversionesAction.actualizarAutorizaOrdenInversion(sAutorizar, parseInt(iOrden),function(response, e){
			if(response !== null && response !== undefined && response !== '' && response > 0)
			{
				NS.storeOrdenesInversion.baseParams.noEmpresa = Ext.getCmp(PF + 'txtNoEmpresa').getValue();
				NS.storeOrdenesInversion.load();				
				NS.gridOrdenesInversion.getView().refresh();
				if(sAutorizar === 'N')
					BFwrk.Util.msgShow('Se ha quitado la autorización a la orden de inversión...','INFO');
				else if(sAutorizar === 'S')
					BFwrk.Util.msgShow('La Orden de Inversión ha sido Autorizada...','INFO');
			}
		});
	};
	
	NS.gridOrdenesInversion = new Ext.grid.GridPanel({
    store : NS.storeOrdenesInversion,
    id: 'gridOrdenesInversion',
    cm: new Ext.grid.ColumnModel({
        defaults: {
            width: 120,
            value:true,
            sortable: true
        },
           columns: [
               NS.smCheck,
                {
				header :'Núm Orden',
				width :80,
				sortable :true,
				dataIndex :'noOrden'
			},{
				header :'Autorizado',
				width :100,
				sortable :true,
				dataIndex :'bAutoriza'
			},{
				header :'Institución',
				width :130,
				sortable :true,
				dataIndex :'nombreCorto'
			},{
				header :'Plazo',
				width :50,
				sortable :true,
				dataIndex :'plazo'
			},{
				header :'Importe',
				width :90,
				sortable :true,
				dataIndex :'importe',
				renderer: BFwrk.Util.rendererMoney
			},{
				header :'Tasa',
				width :70,
				sortable :true,
				dataIndex :'tasa'
			},{
				header :'Interés',
				width :90,
				sortable :true,
				dataIndex :'interes',
				renderer: BFwrk.Util.rendererMoney
			},{
				header :'Impuesto',
				width :90,
				sortable :true,
				dataIndex :'isr',
				renderer: BFwrk.Util.rendererMoney
			},{
				header :'Contacto',
				width :70,
				sortable :true,
				dataIndex :'noContacto'
			},{
				header :'Fecha Orden',
				width :100,
				sortable :true,
				dataIndex :'fecAlta'
			},{
				header :'Fecha Vencimiento',
				width :100,
				sortable :true,
				dataIndex :'fecVenc'
			},{
				header :'Id Tipo Valor',
				width :80,
				sortable :true,
				dataIndex :'idTipoValor',
				hidden : true
			},{
				header :'Descripción',
				width :80,
				sortable :true,
				dataIndex :'descTipoValor'
			},{
				header :'Tipo Papel',
				width :80,
				sortable :true,
				dataIndex :'idPapel'
			},{
				header :'Divisa',
				width :80,
				sortable :true,
				dataIndex :'idDivisa'
			},{
				header :'Cuenta',
				width :80,
				sortable :true,
				dataIndex :'noCuenta',
				hidden : true
			},{
				header :'No Folio Det',
				width :80,
				sortable :true,
				dataIndex :'noFolioDet',
				hidden : true
			},{
				header :'Id Tipo operación',
				width :80,
				sortable :true,
				dataIndex :'idTipoOperacion',
				hidden : true
			},{
				header :'No Folio Det',
				width :80,
				sortable :true,
				dataIndex :'noFolioDet',
				hidden : true
			},{
				header :'Origen Mov',
				width :80,
				sortable :true,
				dataIndex :'origenMov',
				hidden : true
			},{
				header :'Id Forma Pago',
				width :80,
				sortable :true,
				dataIndex :'idFormaPago',
				hidden : true
			},{
				header :'Id Tipo Movto',
				width :80,
				sortable :true,
				dataIndex :'idTipoMovto',
				hidden : true
			},{
				header :'Lote entrada',
				width :80,
				sortable :true,
				dataIndex :'loteEntrada',
				hidden : true
			},{
				header :'Salvo Buen Cobro',
				width :80,
				sortable :true,
				dataIndex :'bSalvoBuenCobro',
				hidden : true
			},{
				header :'Descripción Estatus',
				width :80,
				sortable :true,
				dataIndex :'idEstatusMov',
				hidden:true
			},{
				header :'Entregado',
				width :80,
				sortable :true,
				dataIndex :'bEntregado',
				hidden : true
			},{
				header :'Banco',
				width :80,
				sortable :true,
				dataIndex :'idBanco',
				hidden : true
			},{
				header :'Chequera',
				width :80,
				sortable :true,
				dataIndex :'idChequera',
				hidden : true
			}
           ]
        }),
        sm: NS.smCheck,
        columnLines: true,
        x:0,
        y:0,
        width:960,
        height:345,
        frame:true,
        listeners:{
        	click:{
        		fn:function(e){
          			
        		}
        	}
        }
    });
	
	InversionesAction.validaFacultad(151, function(resp){
		if(resp == 1) {
			Ext.getCmp(PF + 'btnEjecutar').setDisabled(false);
			Ext.getCmp(PF + 'btnQuitar').setDisabled(false);
		}
	});
	
	//Inicia formulario principal ConsultaOrdenesInversion
	NS.ConsultaOrdenesInversion = new Ext.form.FormPanel({
	    title: 'Consulta de Ordenes de Inversión',
	    width: 1020,
	    height: 700,
	    padding: 10,
	    layout: 'absolute',
	    id: PF + 'ConsultaOrdenInversion',
	    name: PF + 'ConsultaOrdenInversion',
	    renderTo: NS.tabContId,
	    frame: true,
	    autoScroll: true,
	    items : [
            {
                xtype: 'fieldset',
                title: '',
                width: 1010,
                height: 490,
                x: 20,
                y: 5,
                layout: 'absolute',
                id: 'framePrinConOrdeInver',
                items: [
                    {
                        xtype: 'label',
                        text: 'Empresa:',
                        x: 10,
                        y: 0
                    },
                    NS.cmbEmpresa,
                    NS.txtNoEmpresa,
                    {
                        xtype: 'textfield',
                        x: 60,
                        y: 0,
                        width: 320,
                        hidden: true,
                        name: PF +'txtNomEmpresa',
                        id: PF +'txtNomEmpresa',
                        value: apps.SET.NOM_EMPRESA
                    },
                    {
                        xtype: 'button',
                        text: 'Grafica',
                        disabled:true,
                        x: 790,
                        y: 0,
                        width: 80,
                        
                        id: PF + 'cmdGrafica',
                        name: PF + 'cmdGrafica',
                        listeners: {
                        	click:{
                        		fn: function(e){
			                    	Ext.getCmp('panel1').update('<img src="'+window.location.protocol+"//"+window.location.host+"/graficaSet/"+apps.SET.iUserId+'OrdenesInversion'+sName+'PC.jpg" border="0"/>');
									Ext.getCmp('panel2').update('<img src="'+window.location.protocol+"//"+window.location.host+"/graficaSet/"+apps.SET.iUserId+'OrdenesInversion'+sName+'BG.jpg" border="0"/>');
									Ext.getCmp('panel3').update('<img src="'+window.location.protocol+"//"+window.location.host+"/graficaSet/"+apps.SET.iUserId+'OrdenesInversion'+sName+'LG.jpg" border="0"/>');
									Ext.Msg.alert('SET', 'Graficas listas');
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Buscar',
                        x: 880,
                        y: 0,
                        width: 80,
                        id: PF + 'btnBuscar',
                        name: PF + 'btnBuscar',
                        listeners: {
                        	click:{
                        		fn: function(e){
                    				NS.storeOrdenesInversion.baseParams.noEmpresa = Ext.getCmp(PF + 'txtNoEmpresa').getValue();
                        			NS.storeOrdenesInversion.load();
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'fieldset',
                        title: 'Liquidación de inversiones',
                        x: 0,
                        y: 40,
                        height: 380,
                        width: 985,
                        id: PF +'frameLiquidacionInv',
                        name: PF +'frameLiquidacionInv',
                        items: [
                        	NS.gridOrdenesInversion
                        ]
                    },
                    {
                        xtype: 'label',
                        text: 'Total :',
                        x: 10,
                        y: 430
                    },
                    {
                        xtype: 'textfield',
                        x: 10,
                        y: 445,
                        width: 230,
                        name: PF + 'txtTotal',
                        id: PF + 'txtTotal'
                    },
                    {
                        xtype: 'button',
                        text: 'Autorizar',
                        x: 660,
                        y: 445,
                        width: 80,
                        disabled: true,
                        id: PF + 'btnEjecutar',
                        name: PF + 'btnEjecutar',
                        listeners: {
                        	click:{
                        		fn: function(e){
                        			var regGridSelec = NS.gridOrdenesInversion.getSelectionModel().getSelections();
                        			var iOrden = 0;
                        			
                        			if(regGridSelec.length <= 0)
                        			{
                        				BFwrk.Util.msgShow('Debe selecionar una orden...','WARNING');
                        				return;
                        			}
                        			iOrden = regGridSelec[0].get('noOrden');
                        			if(regGridSelec[0].get('bAutoriza') === 'SI')
                        			{
                        				BFwrk.Util.msgShow('Esta orden ya esta autorizada','INFO');
                        				return;
                        			}
                        			NS.autorizar('S',iOrden);
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Quitar Autorización',
                        x: 750,
                        y: 445,
                        width: 120,
                        id: PF + 'btnQuitar',
                        disabled: true,
                        listeners: {
                        	click:{
                        		fn: function(e){
                        			var regGridSelec = NS.gridOrdenesInversion.getSelectionModel().getSelections();
                        			var iOrden = 0;
                        			if(regGridSelec.length <= 0)
                        			{
                        				BFwrk.Util.msgShow('Debe selecionar una orden...','WARNING');
                        				return;
                        			}
                        			iOrden = regGridSelec[0].get('noOrden');
                        			if(regGridSelec[0].get('bAutoriza') === 'NO')
                        			{
                        				BFwrk.Util.msgShow('Esta orden no esta autorizada','INFO');
                        				return;
                        			}
                        			NS.autorizar('N',iOrden);
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Eliminar',
                        x: 880,
                        y: 445,
                        width: 80,
                        id: 'btnEliminar',
                        listeners: {
                        	click:{
                        		fn: function(e){
                        			var regGridSelec = NS.gridOrdenesInversion.getSelectionModel().getSelections();
                        			var iOrden = 0;
                        			if(regGridSelec.length <= 0)
                        			{
                        				BFwrk.Util.msgShow('Debe selecionar una orden...','WARNING');
                        				return;
                        			}
                        			iOrden = regGridSelec[0].get('noOrden');
	                        		Ext.Msg.show({
									title: 'Información SET',
									msg: '¿Desea cancelar esta Orden de Inversión?',
										buttons: {
											yes: true,
											no: true,
											cancel: false
										},
										icon: Ext.MessageBox.QUESTION,
										fn: function(btn) {
											if(btn === 'yes')
											{
												if(regGridSelec[0].get('bAutoriza') === 'SI')
												{
													BFwrk.Util.msgShow('La Orden de Inversión Autorizada, No se puede cancelar... ','INFO');
													return;
												}
												else
												{
													NS.llamarRevividor();
												}
											}
											else if(btn === 'no')	
												return;
											else
												return;
										}
									});
									
									//Llamada para realizar la eliminacion de la orden
									NS.llamarRevividor = function(){
										var matRegOrden = new Array();
										
										for(var i = 0;i < regGridSelec.length; i = i +1)
										{
											var arrayReg = {};
												arrayReg.noOrden = regGridSelec[i].get('noOrden');
												arrayReg.bAutoriza = regGridSelec[i].get('bAutoriza');
												arrayReg.nombreCorto = regGridSelec[i].get('nombreCorto');
												arrayReg.plazo = regGridSelec[i].get('plazo');
												arrayReg.importe = regGridSelec[i].get('importe');
												arrayReg.tasa = regGridSelec[i].get('tasa');
												arrayReg.interes = regGridSelec[i].get('interes');
												arrayReg.isr = regGridSelec[i].get('isr');
												arrayReg.noContacto = regGridSelec[i].get('noContacto');
												arrayReg.fecAlta = regGridSelec[i].get('fecAlta');
												arrayReg.fecVenc = regGridSelec[i].get('fecVenc');
												arrayReg.idTipoValor = regGridSelec[i].get('idTipoValor');
												arrayReg.descTipoValor = regGridSelec[i].get('descTipoValor');
												arrayReg.idDivisa = regGridSelec[i].get('idDivisa');
												arrayReg.noCuenta = regGridSelec[i].get('noCuenta');
												arrayReg.noFolioDet = regGridSelec[i].get('noFolioDet');
												arrayReg.idTipoOperacion = regGridSelec[i].get('idTipoOperacion');
												arrayReg.origenMov = regGridSelec[i].get('origenMov');
												arrayReg.idFormaPago = regGridSelec[i].get('idFormaPago');
												arrayReg.idTipoMovto = regGridSelec[i].get('idTipoMovto');
												arrayReg.loteEntrada = regGridSelec[i].get('loteEntrada');
												arrayReg.bSalvoBuenCobro = regGridSelec[i].get('bSalvoBuenCobro');
												arrayReg.idEstatusMov = regGridSelec[i].get('idEstatusMov');
												arrayReg.bEntregado = regGridSelec[i].get('bEntregado');
												arrayReg.idChequera = regGridSelec[i].get('idChequera');
												arrayReg.idBanco = regGridSelec[i].get('idBanco');
											matRegOrden[i] = arrayReg;
										}
										var jsonStringOrden = Ext.util.JSON.encode(matRegOrden);
										InversionesAction.ejecutarRevividor(jsonStringOrden, parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue()), function(response, e){
											if(response !== null && response !== undefined && response !== '')
											{
												BFwrk.Util.msgShow('' + response.msgUsuario,'INFO');
												NS.storeOrdenesInversion.baseParams.noEmpresa = Ext.getCmp(PF + 'txtNoEmpresa').getValue();
												NS.storeOrdenesInversion.load();
												NS.gridOrdenesInversion.getView().refresh();
											}
											
										});
									};
                        		}
                        	}
                        }
                    }
                ]
            }
        ]
	});
	NS.ConsultaOrdenesInversion.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	 //staticCheck("#gridOrdenesInversion div div[class='x-panel-ml']","#gridOrdenesInversion div div[class='x-panel-ml']",8,".x-grid3-scroller",false);
	
});