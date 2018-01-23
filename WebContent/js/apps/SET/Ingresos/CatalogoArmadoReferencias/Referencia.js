//@author Jessica Arelly Cruz Cruz
 Ext.onReady(function(){
		Ext.QuickTips.init();
		Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
		var NS = Ext.namespace('apps.SET.Ingresos.CatalogoArmadoReferencias.Referencia');
		NS.tabContId = apps.SET.tabContainerId;
		var PF = apps.SET.tabID+'.'; 
			
		NS.pBanModificar = false;
		NS.cambiaCtas = false;
		NS.chequeDestino = '';
		NS.sumaLongitud = 0;
		NS.predeterminada = '';
		NS.strTipoIngreso = '';
		NS.noBan = 0;
		NS.noEmp = 0;
		NS.noChe = '';
 	/**
   NS.function(valor) {
				//llamar la funcion de verificacombo
				
				var i;
				var band = 0;
				var band2 = 0;
				//var valor = value.get('desc');
				var matcombos = new Array();
				//alert(value.get('desc'));
				//alert(Ext.getCmp('cmbConstante1D').getStore().getById('desc'));
				matcombos[0] = value.get('desc');
				matcombos[1] = value.get('desc');
				matcombos[2] = value.get('desc');
				matcombos[3] = value.get('desc');
				
				if(valor === 'id_caja, no_cuenta_ref')
				{
				    for(i = 0; i <= 7; i++)
				    {
				    	if(matcombos[i].indexOf('id_caja', 0) > 0) 
				            band = band + 1;
				        
				        if(matcombos[i].indexOf('no_cuenta_ref', 0) > 0) 
				            band2 = band2 + 1
				    }
				    if (band2 == 2) 
				     band = 2;
				}
				else
				{
				    for(i = 1; i <= 7; i++)
				    {	
				    	if(matcombos[i].indexOf(valor,0) > 0)
				            band = band + 1 ;
				    }
				 }
				 if(band == 2)
				 Ext.MessageBox.alert("SET"," No puede asignar mas de un valor a un campo");
				
				}
 	*/
 	
 	
 	/**data del store combos destino*/
    NS.dataDestino = [  [ '0', 'id_caja' ],
						[ '1', 'no_docto' ], 
					 	[ '2', 'no_cuenta_ref' ],
						[ '3', 'id_caja, no_cuenta_ref' ],
						[ '4', 'no_cliente' ],
						[ '5', 'no_cobrador' ],
						[ '6', 'no_recibo' ],
						[ '7', 'id_codigo' ],
						[ '8', 'id_subcodigo' ],
						[ '9', 'no_factura' ],
						[ '10', 'division' ]
					  ];
	
	/**store destino*/
	NS.storeDestino = new Ext.data.SimpleStore( {
			idProperty: 'id',  		//identificador del store
			fields : [ 
						{name :'id'}, 
						{name :'desc'}
					 ]
		});
		NS.storeDestino.loadData(NS.dataDestino);
		
	NS.verificaCombos = function() {
				//llamar la funcion de verificacombo
				var matcombos = new Array();
				var aux = new Array();
				var i;
				var band ;
				var band2;
				matcombos[0]=Ext.getCmp(PF+'cmbEmpresaD').getValue();
				matcombos[1]=Ext.getCmp(PF+'cmbConstante1D').getValue();
				matcombos[2]=Ext.getCmp(PF+'cmbConstante2D').getValue();
				matcombos[3]=Ext.getCmp(PF+'cmbConstante3D').getValue();
				matcombos[4]=Ext.getCmp(PF+'cmbVariable1D').getValue();
				matcombos[5]=Ext.getCmp(PF+'cmbVariable2D').getValue();
				matcombos[6]=Ext.getCmp(PF+'cmbVariable3D').getValue();
				//aux = matcombos;
					if(matcombos[i] == 3)
				{
				    for(i = 0; i <= 7; i++)
				    {
				    	if(matcombos[i] == 0) 
				            band = band + 1;
				        
				        if(matcombos[i] == 2) 
				            band2 = band2 + 1
				    }
				    if (band2 == 2) 
				     band = 2;
				}
				/*else
				{
				    for(i = 1; i <= 7; i++)
				    {	
				    	if(matcombos[i] == aux[i])
				            band = band + 1 ;
				    }
				 }*/
				 if(band == 2)
				{
					Ext.MessageBox.alert("SET"," No puede asignar mas de un valor a un campo");
					combo.setValue('');
				}
			  }	
		
		/**combo fisico empresaDestino*/
	NS.cmbEmpresaD = new Ext.form.ComboBox({
		 store: NS.storeDestino 	//llamada al store
		,name: PF+'cmbEmpresaD'
		,id: PF+'cmbEmpresaD'
		,mode: 'local'
		,disabled: true
		,width: 100
        ,x: 270
        ,y: 30
		,valueField:'id'
		,displayField:'desc'
		,autocomplete: true
		,emptyText: 'Empresa'
		,listeners:{
			select:{		//evento click
				fn: function(combo, value) {
				NS.contenedorReferencia.baseParams.empresaD = combo.getValue();
				//llamar la funcion de verificacombo
				
				NS.select1=Ext.getCmp(PF+'cmbEmpresaD').getValue();
				NS.select2=Ext.getCmp(PF+'cmbConstante1D').getValue();
				NS.select3=Ext.getCmp(PF+'cmbConstante2D').getValue();
				NS.select4=Ext.getCmp(PF+'cmbConstante3D').getValue();
				NS.select5=Ext.getCmp(PF+'cmbVariable1D').getValue();
				NS.select6=Ext.getCmp(PF+'cmbVariable2D').getValue();
				NS.select7=Ext.getCmp(PF+'cmbVariable3D').getValue();
				
				if(NS.select1==NS.select2 || NS.select1==NS.select3 || NS.select1==NS.select4 
				|| NS.select1==NS.select5 || NS.select1==NS.select6 || NS.select1==NS.select7)
				{
					Ext.MessageBox.alert("SET"," No puede asignar mas de un valor a un campo");
					combo.setValue('');
				}
			  }	
			}
		}
	});

	
	/**combo fisico constante1D*/
	NS.cmbConstante1D = new Ext.form.ComboBox({
		 store: NS.storeDestino 	//llamada al store
		,name: PF+'cmbConstante1D'
		,id: PF+'cmbConstante1D'
		,mode: 'local'
		,disabled: true
		,width: 100
        ,x: 270
        ,y: 180
		,valueField:'id'
		,displayField:'desc'
		,autocomplete: true
		,emptyText: 'Constante 1'
		,listeners:{
			select:{		//evento click
				fn: function(combo, value) {
				NS.contenedorReferencia.baseParams.const1 = combo.getValue();
				//llamar la funcion de verificacombo
				NS.select1=Ext.getCmp(PF+'cmbEmpresaD').getValue();
				NS.select2=Ext.getCmp(PF+'cmbConstante1D').getValue();
				NS.select3=Ext.getCmp(PF+'cmbConstante2D').getValue();
				NS.select4=Ext.getCmp(PF+'cmbConstante3D').getValue();
				NS.select5=Ext.getCmp(PF+'cmbVariable1D').getValue();
				NS.select6=Ext.getCmp(PF+'cmbVariable2D').getValue();
				NS.select7=Ext.getCmp(PF+'cmbVariable3D').getValue();
				
				if(NS.select2==NS.select1 || NS.select2==NS.select3 || NS.select2==NS.select4 
				|| NS.select2==NS.select5 || NS.select2==NS.select6 || NS.select2==NS.select7)
				{
					Ext.MessageBox.alert("SET"," No puede asignar mas de un valor a un campo");
					combo.setValue('');
				}
			  }
			}
		}
	});
	
	/**combo fisico constante2D*/
	NS.cmbConstante2D = new Ext.form.ComboBox({
		 store: NS.storeDestino 	//llamada al store
		,name: PF+'cmbConstante2D'
		,id: PF+'cmbConstante2D'
		,mode: 'local'
		,disabled: true
		,width: 100
        ,x: 270
        ,y: 210
		,valueField:'id'
		,displayField:'desc'
		,autocomplete: true
		,emptyText: 'Constante 2'
		,listeners:{
			select:{		//evento click
				fn: function(combo, value) {
				NS.contenedorReferencia.baseParams.const2 = combo.getValue();
				//llamar la funcion de verificacombo
				NS.select1=Ext.getCmp(PF+'cmbEmpresaD').getValue();
				NS.select2=Ext.getCmp(PF+'cmbConstante1D').getValue();
				NS.select3=Ext.getCmp(PF+'cmbConstante2D').getValue();
				NS.select4=Ext.getCmp(PF+'cmbConstante3D').getValue();
				NS.select5=Ext.getCmp(PF+'cmbVariable1D').getValue();
				NS.select6=Ext.getCmp(PF+'cmbVariable2D').getValue();
				NS.select7=Ext.getCmp(PF+'cmbVariable3D').getValue();
				
				if(NS.select3==NS.select2 || NS.select3==NS.select1 || NS.select3==NS.select4 
				|| NS.select3==NS.select5 || NS.select3==NS.select6 || NS.select3==NS.select7)
				{
					Ext.MessageBox.alert("SET"," No puede asignar mas de un valor a un campo");
					combo.setValue('');
				}
			  }
			}
		}
	});
	
	/**combo fisico constante3D*/
	NS.cmbConstante3D = new Ext.form.ComboBox({
		 store: NS.storeDestino 	//llamada al store
		,name: PF+'cmbConstante3D'
		,id: PF+'cmbConstante3D'
		,mode: 'local'
		,disabled: true
		,width: 100
        ,x: 270
        ,y: 240
		,valueField:'id'
		,displayField:'desc'
		,autocomplete: true
		,emptyText: 'Constante 3'
		,listeners:{
			select:{		//evento click
				fn: function(combo, value) {
				NS.contenedorReferencia.baseParams.const3 = combo.getValue();
				//llamar la funcion de verificacombo
				NS.select1=Ext.getCmp(PF+'cmbEmpresaD').getValue();
				NS.select2=Ext.getCmp(PF+'cmbConstante1D').getValue();
				NS.select3=Ext.getCmp(PF+'cmbConstante2D').getValue();
				NS.select4=Ext.getCmp(PF+'cmbConstante3D').getValue();
				NS.select5=Ext.getCmp(PF+'cmbVariable1D').getValue();
				NS.select6=Ext.getCmp(PF+'cmbVariable2D').getValue();
				NS.select7=Ext.getCmp(PF+'cmbVariable3D').getValue();
				
				if(NS.select4==NS.select2 || NS.select4==NS.select3 || NS.select4==NS.select1 
				|| NS.select4==NS.select5 || NS.select4==NS.select6 || NS.select4==NS.select7)
				{
					Ext.MessageBox.alert("SET"," No puede asignar mas de un valor a un campo");
					combo.setValue('');
				}
			  }
			}
		}
	});
	
	/**combo fisico variable1D*/
	NS.cmbVariable1D = new Ext.form.ComboBox({
		 store: NS.storeDestino 	//llamada al store
		,name: PF+'cmbVariable1D'
		,id: PF+'cmbVariable1D'
		,mode: 'local'
		,disabled: true
		,width: 100
        ,x: 270
        ,y: 270
		,valueField:'id'
		,displayField:'desc'
		,autocomplete: true
		,emptyText: 'Variable 1'
		,listeners:{
			select:{		//evento click
				fn: function(combo, value) {
				NS.contenedorReferencia.baseParams.var1 = combo.getValue();
				//llamar la funcion de verificacombo
				NS.select1=Ext.getCmp(PF+'cmbEmpresaD').getValue();
				NS.select2=Ext.getCmp(PF+'cmbConstante1D').getValue();
				NS.select3=Ext.getCmp(PF+'cmbConstante2D').getValue();
				NS.select4=Ext.getCmp(PF+'cmbConstante3D').getValue();
				NS.select5=Ext.getCmp(PF+'cmbVariable1D').getValue();
				NS.select6=Ext.getCmp(PF+'cmbVariable2D').getValue();
				NS.select7=Ext.getCmp(PF+'cmbVariable3D').getValue();
				
				if(NS.select5==NS.select2 || NS.select5==NS.select3 || NS.select5==NS.select4 
				|| NS.select5==NS.select1 || NS.select5==NS.select6 || NS.select5==NS.select7)
				{
					Ext.MessageBox.alert("SET"," No puede asignar mas de un valor a un campo");
					combo.setValue('');
				}
			  }
			}
		}
	});
 	
 	/**combo fisico variable2D*/
	NS.cmbVariable2D = new Ext.form.ComboBox({
		 store: NS.storeDestino 	//llamada al store
		,name: PF+'cmbVariable2D'
		,id: PF+'cmbVariable2D'
		,mode: 'local'
		,disabled: true
		,width: 100
        ,x: 270
        ,y: 300
		,valueField:'id'
		,displayField:'desc'
		,autocomplete: true
		,emptyText: 'Variable 2'
		,listeners:{
			select:{		//evento click
				fn: function(combo, value) {
				NS.contenedorReferencia.baseParams.var2 = combo.getValue();
				//llamar la funcion de verificacombo
				NS.select1=Ext.getCmp(PF+'cmbEmpresaD').getValue();
				NS.select2=Ext.getCmp(PF+'cmbConstante1D').getValue();
				NS.select3=Ext.getCmp(PF+'cmbConstante2D').getValue();
				NS.select4=Ext.getCmp(PF+'cmbConstante3D').getValue();
				NS.select5=Ext.getCmp(PF+'cmbVariable1D').getValue();
				NS.select6=Ext.getCmp(PF+'cmbVariable2D').getValue();
				NS.select7=Ext.getCmp(PF+'cmbVariable3D').getValue();
				
				if(NS.select6==NS.select2 || NS.select6==NS.select3 || NS.select6==NS.select4 
				|| NS.select6==NS.select5 || NS.select6==NS.select1 || NS.select6==NS.select7)
				{
					Ext.MessageBox.alert("SET"," No puede asignar mas de un valor a un campo");
					combo.setValue('');
				}
			  }
			}
		}
	});
	
	/**combo fisico variable3D*/
	NS.cmbVariable3D = new Ext.form.ComboBox({
		 store: NS.storeDestino 	//llamada al store
		,name: PF+'cmbVariable3D'
		,id: PF+'cmbVariable3D'
		,mode: 'local'
		,disabled: true
		,width: 100
        ,x: 270
        ,y: 330
		,valueField:'id'
		,displayField:'desc'
		,autocomplete: true
		,emptyText: 'Variable 3'
		,listeners:{
			select:{		//evento click
				fn: function(combo, value) {
				NS.contenedorReferencia.baseParams.var3 = combo.getValue();
				//llamar la funcion de verificacombo
				NS.select1=Ext.getCmp(PF+'cmbEmpresaD').getValue();
				NS.select2=Ext.getCmp(PF+'cmbConstante1D').getValue();
				NS.select3=Ext.getCmp(PF+'cmbConstante2D').getValue();
				NS.select4=Ext.getCmp(PF+'cmbConstante3D').getValue();
				NS.select5=Ext.getCmp(PF+'cmbVariable1D').getValue();
				NS.select6=Ext.getCmp(PF+'cmbVariable2D').getValue();
				NS.select7=Ext.getCmp(PF+'cmbVariable3D').getValue();
				
				if(NS.select7==NS.select2 || NS.select7==NS.select3 || NS.select7==NS.select4 
				|| NS.select7==NS.select1 || NS.select7==NS.select6 || NS.select7==NS.select5)
				{
					Ext.MessageBox.alert("SET"," No puede asignar mas de un valor a un campo");
					combo.setValue('');
				}
			  }
			}
		}
	});
 	
 	/**llena el combo bancos*/
	NS.storeBanco = new Ext.data.DirectStore( {
		paramsAsHash: false,
		paramOrder:['noEmpresa'],
		root: '',
		directFn: GeneraReferenciaAction.obtenerBanco,
		idProperty: 'idBanco',  		//identificador del store
		fields: [
			{name: 'idBanco' },
			{name: 'descBanco'}
		],
		listeners: {
			load: function(s, records){
			if(records.length == null || records.length <= 0)
				Ext.MessageBox.alert("SET"," No hay bancos disponibles");
			}
		}
	}); 
	
	NS.accionarCmbBanco = function(comboValue)
	{
		Ext.getCmp(PF+'cmbChequera').setValue('');
		NS.cmbChequera.setDisabled(false);
		NS.noBan = comboValue;
		NS.storeChequera.baseParams.idBanco = comboValue;
		NS.storeChequera.baseParams.noEmpresa = NS.noEmp;
		NS.storeChequera.load();
		NS.contenedorReferencia.baseParams.banco = comboValue;
	}
	
	//combo fisico bancos
	NS.cmbBanco = new Ext.form.ComboBox({
		 store: NS.storeBanco 	//llamada al store
		,name: PF+'cmbBanco'
		,id: PF+'cmbBanco'
		,disabled: true
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :150
		,y :30
		,width :175
		,valueField:'idBanco'
		,displayField:'descBanco'
		,autocomplete: true
		,emptyText: 'Banco'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{		//evento click
				fn:function(combo, value) 
				{
					BFwrk.Util.updateComboToTextField(PF+'txtBanco',NS.cmbBanco.getId());
					NS.accionarCmbBanco(combo.getValue());
				}
			}	
		}
	});
	
	/**llena el combo bancos FID*/
	NS.storeBancoFID = new Ext.data.DirectStore( {
		paramsAsHash: false,
		paramOrder:['noEmpresa'],
		root: '',
		directFn: GeneraReferenciaAction.obtenerBancoFID,
		idProperty: 'idBancoFID',  		//identificador del store
		fields: [
			{name: 'idBanco' },
			{name: 'descBanco'}
		],
		listeners: {
			load: function(s, records){
			if(records.length == null || records.length <= 0)
				Ext.MessageBox.alert("SET"," No hay bancos disponibles");
			}
		}
	}); 
	
	//combo fisico bancos FID
	NS.cmbBancoFID = new Ext.form.ComboBox({
		 store: NS.storeBancoFID 	//llamada al store
		,name: PF+'cmbBancoFID'
		,id: PF+'cmbBancoFID'
		,disabled: true
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :110
		,y :40
		,width :140
		,valueField:'idBanco'
		,displayField:'descBanco'
		,autocomplete: true
		,emptyText: 'Banco'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{		//evento click
				fn:function(combo, value) {
						Ext.getCmp(PF+'cmbChequeraFID').setValue('');
						Ext.getCmp(PF+'txtBancoFID').setValue(combo.getValue());
						NS.cmbChequeraFID.setDisabled(false);
						NS.noBanFID = combo.getValue();
						NS.storeChequeraFID.baseParams.idBanco = NS.noBanFID;
						NS.storeChequeraFID.baseParams.noEmpresa = NS.noEmpFID;
						NS.storeChequeraFID.load();
								}
						}	
					}
	});
	/**llena el combo empresa*/
	NS.storeEmpresa = new Ext.data.DirectStore( {
		paramsAsHash: false,
		root: '',
		directFn: GeneraReferenciaAction.obtenerEmpresa,
		idProperty: 'noEmpresa',  		//identificador del store
		fields: [
			{name: 'noEmpresa' },
			{name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records){
						//Ext.getCmp(PF+'cmbEmpresa').setValue(101);
						//Ext.getCmp(PF+'txtEmpresa').setValue(101);
			}
		}
	}); 
	NS.storeEmpresa.load();
	
	NS.accionarCmbEmpresa = function(comboValue)
	{
		Ext.getCmp(PF+'cmbBanco').setValue('');
		Ext.getCmp(PF+'txtBanco').setValue('');
		Ext.getCmp(PF+'cmbChequera').setValue('');
		NS.cmbBanco.setDisabled(false);
		Ext.getCmp(PF+'txtBanco').setDisabled(false);
		NS.noEmp = comboValue;
		NS.storeBanco.baseParams.noEmpresa = NS.noEmp;
		NS.storeBanco.load();
		NS.contenedorReferencia.baseParams.empresa = comboValue;
	}
	
	//combo fisico empresa
	NS.cmbEmpresa = new Ext.form.ComboBox({
		 store: NS.storeEmpresa 	//llamada al store
		,name: PF+'cmbEmpresa'
		,id: PF+'cmbEmpresa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :150
		,y :0
		,width :400
		,valueField:'noEmpresa'
		,displayField:'nomEmpresa'
		,autocomplete: true
		,emptyText: 'Seleccione la Empresa'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{		//evento click
				fn:function(combo, value) {
					BFwrk.Util.updateComboToTextField(PF+'txtEmpresa',NS.cmbEmpresa.getId());
					NS.accionarCmbEmpresa(combo.getValue());
				}
			}	
		}
	});
	
	
	/**llena el combo empresa*/
	NS.storeEmpresaFID = new Ext.data.DirectStore( {
		paramsAsHash: false,
		root: '',
		directFn: GeneraReferenciaAction.obtenerEmpresaFID,
		idProperty: 'noEmpresaFID',  		//identificador del store
		fields: [
			{name: 'noEmpresa' },
			{name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records){
						
			}
		}
	}); 
	NS.storeEmpresaFID.load();
	
	//combo fisico empresa FID
	NS.cmbEmpresaFID = new Ext.form.ComboBox({
		 store: NS.storeEmpresaFID 	//llamada al store
		,name: PF+'cmbEmpresaFID'
		,id: PF+'cmbEmpresaFID'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x: 110
		,y: 0
		,width :140
		,valueField:'noEmpresa'
		,displayField:'nomEmpresa'
		,autocomplete: true
		,emptyText: 'Empresa'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{		//evento click
				fn:function(combo, value) {
						Ext.getCmp(PF+'txtEmpresaFID').setValue(combo.getValue());
						Ext.getCmp(PF+'cmbBancoFID').setValue('');
						Ext.getCmp(PF+'cmbChequeraFID').setValue('');
						
						NS.cmbBancoFID.setDisabled(false);
						NS.noEmpFID = combo.getValue();
						NS.storeBancoFID.baseParams.noEmpresaFID = NS.noEmpFID;
						NS.storeBancoFID.load();
								}
						}	
					}
	});
	
	/**llena el combo chequera*/
	NS.storeChequera = new Ext.data.DirectStore( {
		paramsAsHash: false,
		paramOrder:['idBanco','noEmpresa'],
		root: '',
		directFn: GeneraReferenciaAction.obtenerChequera,
		idProperty: 'idChequera',  		//identificador del store
		fields: [
			{name: 'idChequera'}
		],
		listeners: {
			load: function(s, records){
			if(records.length == null || records.length <= 0)
				Ext.MessageBox.alert("SET"," No hay chequeras disponibles");
			}
		}
	}); 
	
	//combo fisico chequera
	NS.cmbChequera = new Ext.form.ComboBox({
		 store: NS.storeChequera 	//llamada al store
		,name: PF+'cmbChequera'
		,id: PF+'cmbChequera'
		,disabled: true
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :390
		,y :30
		,width :175
		,valueField:'idChequera'
		,displayField:'idChequera'
		,autocomplete: true
		,emptyText: 'Chequera'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{		//evento click
				fn:function(combo, value) {
								NS.noChe = Ext.getCmp(PF+'cmbChequera').getValue();
								NS.storeIngreso.baseParams.noEmpresa = NS.noEmp;
			 					NS.storeIngreso.baseParams.idBanco = NS.noBan;
			 					NS.storeIngreso.baseParams.idChequera = NS.noChe;
								NS.storeGeneraReferencia.baseParams.idBanco = NS.noBan;
								NS.storeGeneraReferencia.baseParams.noEmpresa = NS.noEmp;
								NS.storeGeneraReferencia.baseParams.idChequera = NS.noChe;
								NS.contenedorReferencia.baseParams.chequera = combo.getValue();
								}
						}	
					}
	});
	
	/**llena el combo chequera FID*/
	NS.storeChequeraFID = new Ext.data.DirectStore( {
		paramsAsHash: false,
		paramOrder:['idBanco','noEmpresa'],
		root: '',
		directFn: GeneraReferenciaAction.obtenerChequeraFID,
		idProperty: 'idChequeraFID',  		//identificador del store
		fields: [
			{name: 'idChequera'}
		],
		listeners: {
			load: function(s, records){
			if(records.length == null || records.length <= 0)
				Ext.MessageBox.alert("SET"," No hay chequeras disponibles");
			}
		}
	}); 
	
	//combo fisico chequera
	NS.cmbChequeraFID = new Ext.form.ComboBox({
		 store: NS.storeChequera 	//llamada al store
		,name: PF+'cmbChequeraFID'
		,id: PF+'cmbChequeraFID'
		,disabled: true
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :60
		,y :80
		,width :120
		,valueField:'idChequera'
		,displayField:'idChequera'
		,autocomplete: true
		,emptyText: 'Chequera'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{		//evento click
				fn:function(combo, value) {
								NS.noCheFID = Ext.getCmp(PF+'cmbChequeraFID').getValue();
								}
						}	
					}
	});
	
	NS.campos = [
			{name: 'noEmpresa', mapping : 'noEmpresa' },
 			{name: 'idBanco', mapping : 'idBanco' },
 			{name: 'idChequera', mapping : 'idChequera' },
 			{name: 'idCorrespondeCC', mapping : 'idCorrespondeCC' },
			{name: 'descCorrespondeCC', mapping : 'descCorrespondeCC' },
			{name: 'longReferencia', mapping : 'longReferencia' },
			{name: 'longEmpresa', mapping : 'longEmpresa' },
			{name: 'longCliente', mapping: 'longCliente'},
			{name: 'longCodigo', mapping: 'longCodigo'},
			{name: 'longSubcodigo', mapping: 'longSubcodigo'},
			{name: 'longDivision', mapping: 'longDivision'},
			{name: 'const1', mapping: 'const1'},
			{name: 'const2', mapping: 'const2'},
			{name: 'const3', mapping: 'const3'},
			{name: 'idChequeraDestino', mapping: 'idChequeraDestino'},
			{name: 'longVar1', mapping: 'longVar1'},
			{name: 'longVar2', mapping: 'longVar2'},
			{name: 'longVar3', mapping: 'longVar3'},
			{name: 'ordenEmpresa', mapping : 'ordenEmpresa' },
			{name: 'ordenCliente', mapping: 'ordenCliente'},
			{name: 'ordenCodigo', mapping: 'ordenCodigo'},
			{name: 'ordenSubcodigo', mapping: 'ordenSubcodigo'},
			{name: 'ordenDivision', mapping: 'ordenDivision'},
			{name: 'ordenConst1', mapping: 'ordenConst1'},
			{name: 'ordenConst2', mapping: 'ordenConst2'},
			{name: 'ordenConst3', mapping: 'ordenConst3'},
			{name: 'ordenVar1', mapping: 'ordenVar1'},
			{name: 'ordenVar2', mapping: 'ordenVar2'},
			{name: 'ordenVar3', mapping: 'ordenVar3'},
			{name: 'idRubro', mapping: 'idRubro'},
			{name: 'destinoEmpresa', mapping: 'destinoEmpresa'},
			{name: 'destinoConst1', mapping: 'destinoConst1'},
			{name: 'destinoConst2', mapping: 'destinoConst2'},
			{name: 'destinoConst3', mapping: 'destinoConst3'},
			{name: 'ordenVar1', mapping: 'ordenVar1'},
			{name: 'ordenVar2', mapping: 'ordenVar2'},
			{name: 'ordenVar3', mapping: 'ordenVar3'},
			{name: 'destinoVar1', mapping: 'destinoVar1'},
			{name: 'destinoVar2', mapping: 'destinoVar2'},
			{name: 'destinoVar3', mapping: 'destinoVar3'},
			{name: 'bPredeterminada', mapping: 'bPredeterminada'},
			{name: 'bCambiaOrigen', mapping: 'bCambiaOrigen'},
			{name: 'baseCalculo', mapping: 'baseCalculo'},
			{name: 'idGrupoG', mapping: 'idGrupoG'}
			];
 			
 			//store del grid
	NS.storeGeneraReferencia = new Ext.data.DirectStore( {
			paramsAsHash: false,
			root: '',
			idProperty: 'idCorrespondeCC',
			paramOrder: ['idBanco', 'noEmpresa', 'idChequera'],
			directFn: GeneraReferenciaAction.llenarGrid,
			fields: NS.campos,
			listeners: {
				load: function(s, records){
				if(records.length == null || records.length <= 0)
				Ext.MessageBox.alert("SET"," No Se Obtuvieron Resultados Para la Cuenta de Cheques");
				}
			}
		});
		
		/*var ocultarCampos = function(records) {
		var idC = NS.storeGeneraReferencia.getById(records[0].get('idCorrespondeCC')).get('longEmpresa');
   			if(idC != 0)
   				campo = 'X';
   			else
   				campo = '';
   			return campo;
			};*/
	
		// create the grid
	NS.gridGeneraReferencia = new Ext.grid.GridPanel( {
		store : NS.storeGeneraReferencia
		,stripeRows :true
		,height :125
		,x :0
		,y :0
		,tbar:[
			{
			text: 'Nuevo',
			iconCls:'forma_alta',
			handler: function() {
					NS. pBanModificar = false;
					if(NS.noEmp == ''){
                      	Ext.MessageBox.alert("SET"," Seleccione una empresa");
                      	return;
                      	}
                    else if(NS.noBan == ''){
                       	Ext.MessageBox.alert("SET"," Seleccione el banco");
                       	return;
                       	}
                    else if(NS.noChe == ''){
                       	Ext.MessageBox.alert("SET"," Seleccione la chequera");
                       	return;
                       	}
                    else
					Ext.getCmp(PF+'valoresRef').setDisabled(false);
					Ext.getCmp(PF+'cmbTipoIngreso').setDisabled(false);
					Ext.getCmp(PF+'cmbGrupo').setDisabled(false);
					Ext.getCmp(PF+'cmbRubro').setDisabled(false);
					Ext.getCmp(PF+'aceptar').setDisabled(false);
					NS.storeIngreso.load();
					Ext.getCmp(PF+'fBaseCalculo').setDisabled(false);
					NS.contenedorReferencia.getForm().reset();
				}
			}
			,' | '
			,{
			text:'Modificar',
			iconCls:'forma_cambio',
			handler: function(){
					Ext.getCmp(PF+'chkEmpresa').setValue(0);
					Ext.getCmp(PF+'chkCliente').setValue(0);
					Ext.getCmp(PF+'chkCodigo').setValue(0);
					Ext.getCmp(PF+'chkSubcodigo').setValue(0);
					Ext.getCmp(PF+'chkDivision').setValue(0);
					Ext.getCmp(PF+'chkConstante1').setValue(0);
					Ext.getCmp(PF+'chkConstante2').setValue(0);
					Ext.getCmp(PF+'chkConstante3').setValue(0);
					Ext.getCmp(PF+'chkVariable1').setValue(0);
					Ext.getCmp(PF+'chkVariable2').setValue(0);
					Ext.getCmp(PF+'chkVariable3').setValue(0);
					var records = NS.gridGeneraReferencia.getSelectionModel().getSelections();
					Ext.getCmp(PF+'valoresRef').setDisabled(false);
					NS. pBanModificar = true;
					if(records.length>0){
 					var vlEmpresa = records[0].get('longEmpresa');
 					var vlCliente = records[0].get('longCliente');
 					var vlCodigo = records[0].get('longCodigo');
 					var vlSubcodigo = records[0].get('longSubcodigo');
 					var vlDivision = records[0].get('longDivision');
 					var vlCons1 = records[0].get('const1');
 					var vlCons2 = records[0].get('const2');
 					var vlCons3 = records[0].get('const3');
 					var vlVar1 = records[0].get('longVar1');
 					var vlVar2 = NS.storeGeneraReferencia.getById(records[0].get('idCorrespondeCC')).get('longVar2');
 					var vlVar3 = NS.storeGeneraReferencia.getById(records[0].get('idCorrespondeCC')).get('longVar3');
 					
 					var voEmpresa = records[0].get('ordenEmpresa');
 					var voCliente = records[0].get('ordenCliente');
 					var voCodigo = records[0].get('ordenCodigo');
 					var voSubcodigo = records[0].get('ordenSubcodigo');
 					var voDivision = records[0].get('ordenDivision');
 					var voCons1 = records[0].get('ordenConst1');
 					var voCons2 = records[0].get('ordenConst2');
 					var voCons3 = records[0].get('ordenConst3');
 					var voVar1 = records[0].get('ordenVar1');
 					var voVar2 = NS.storeGeneraReferencia.getById(records[0].get('idCorrespondeCC')).get('ordenVar2');
 					var voVar3 = NS.storeGeneraReferencia.getById(records[0].get('idCorrespondeCC')).get('ordenVar3');
 					
 					Ext.getCmp(PF+'txtlEmpresa').setValue(vlEmpresa);
 					Ext.getCmp(PF+'txtlCliente').setValue(vlCliente);
 					Ext.getCmp(PF+'txtlCodigo').setValue(vlCodigo);
 					Ext.getCmp(PF+'txtlSubcodigo').setValue(vlSubcodigo);
 					Ext.getCmp(PF+'txtlDivision').setValue(vlDivision);
 					Ext.getCmp(PF+'txtlCons1').setValue(vlCons1);
 					Ext.getCmp(PF+'txtlCons2').setValue(vlCons2);
 					Ext.getCmp(PF+'txtlCons3').setValue(vlCons3);
 					Ext.getCmp(PF+'txtlVar1').setValue(vlVar1);
 					Ext.getCmp(PF+'txtlVar2').setValue(vlVar2);
 					Ext.getCmp(PF+'txtlVar3').setValue(vlVar3);
 					Ext.getCmp(PF+'txtoEmpresa').setValue(voEmpresa);
 					Ext.getCmp(PF+'txtoCliente').setValue(voCliente);
 					Ext.getCmp(PF+'txtoCodigo').setValue(voCodigo);
 					Ext.getCmp(PF+'txtoSubcodigo').setValue(voSubcodigo);
 					Ext.getCmp(PF+'txtoDivision').setValue(vlDivision);
 					Ext.getCmp(PF+'txtoCons1').setValue(voCons1);
 					Ext.getCmp(PF+'txtoCons2').setValue(voCons2);
 					Ext.getCmp(PF+'txtoCons3').setValue(voCons3);
 					Ext.getCmp(PF+'txtoVar1').setValue(voVar1);
 					Ext.getCmp(PF+'txtoVar2').setValue(voVar2);
 					Ext.getCmp(PF+'txtoVar3').setValue(voVar3);
 					
 					if(vlEmpresa > 0)
 					{
	 					Ext.getCmp(PF+'chkEmpresa').setValue(true);
	 					Ext.getCmp(PF+'cmbEmpresaD').setDisabled(false);
 					}
 					if(vlCliente > 0)
 					Ext.getCmp(PF+'chkCliente').setValue(true);
 					if(vlCodigo > 0)
 					Ext.getCmp(PF+'chkCodigo').setValue(true);
 					if(vlSubcodigo > 0)
 					Ext.getCmp(PF+'chkSubcodigo').setValue(true);
 					if(vlDivision > 0)
 					Ext.getCmp(PF+'chkDivision').setValue(true);
 					if(vlCons1 > 0)
 					{
	 					Ext.getCmp(PF+'chkConstante1').setValue(true);
	 					Ext.getCmp(PF+'cmbConstante1D').setDisabled(false);
 					}
 					if(vlCons2 > 0)
 					{
	 					Ext.getCmp(PF+'chkConstante2').setValue(true);
	 					Ext.getCmp(PF+'cmbConstante2D').setDisabled(false);
 					}
 					if(vlCons3 > 0)
	 				{
	 					Ext.getCmp(PF+'chkConstante3').setValue(true);
	 					Ext.getCmp(PF+'cmbConstante3D').setDisabled(false);
 					}
 					if(vlVar1 > 0)
 					{
	 					Ext.getCmp(PF+'chkVariable1').setValue(true);
	 					Ext.getCmp(PF+'cmbVariable1D').setDisabled(false);
 					}
 					if(vlVar2 > 0)
 					{
	 					Ext.getCmp(PF+'chkVariable2').setValue(true);
	 					Ext.getCmp(PF+'cmbVariable2D').setDisabled(false);
 					}
 					if(vlVar3 > 0)
 					{
	 					Ext.getCmp(PF+'chkVariable3').setValue(true);
	 				}
	 					Ext.getCmp(PF+'cmbTipoIngreso').setDisabled(false);
	 					Ext.getCmp(PF+'cmbGrupo').setDisabled(false);
	 					Ext.getCmp(PF+'cmbRubro').setDisabled(false);
	 					Ext.getCmp(PF+'aceptar').setDisabled(false);
	 					NS.storeIngreso.baseParams.noEmpresa = NS.noEmp;
	 					NS.storeIngreso.baseParams.idBanco = NS.noBan;
	 					NS.storeIngreso.baseParams.idChequera = NS.noChe;
						NS.storeIngreso.load();
						
						var idrubro = NS.storeGeneraReferencia.getById(records[0].get('idCorrespondeCC')).get('idRubro');
						NS.storeGrupo.baseParams.rubro = idrubro;
						Ext.getCmp(PF+'fBaseCalculo').setDisabled(false);
									
								}
								else{
									Ext.Msg.alert('SET','Seleccione un Registro');
								}
							}
						}
			,' | '
			,{
				text: 'Eliminar',
				iconCls:'forma_baja',
				handler: function() {
				     Ext.Msg.confirm('SET','¿Esta seguro de eliminar?',function(btn){  
				         if(btn === 'yes'){  
							var records = NS.gridGeneraReferencia.getSelectionModel().getSelections();
							if(records.length>0){
								var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Eliminando..."});
								myMask.show();
								
								var i=0;
								for(i=0; i<records.length; i=i+1){
									var emp = NS.noEmp;
									var ban = NS.storeGeneraReferencia.getById(records[i].get('idCorrespondeCC')).get('idBanco');
									var che = NS.storeGeneraReferencia.getById(records[i].get('idCorrespondeCC')).get('idChequera');
									var idC = NS.storeGeneraReferencia.getById(records[i].get('idCorrespondeCC')).get('idCorrespondeCC');
									GeneraReferenciaAction.eliminarArmaIngreso(emp, ban, che, idC, function(result, e){
										if(!Ext.encode(result)){
											Ext.Msg.alert('SET','No se puede eliminar el Perfil por referencia');
											myMask.hide();
										}
										else{
											myMask.hide();
											Ext.Msg.alert('SET','Registro Eliminado');
											NS.storeGeneraReferencia.remove(emp, ban, che, idC);
											NS.storeGeneraReferencia.load();
									        NS.gridGeneraReferencia.getView().refresh();
											NS.contenedorReferencia.getForm().reset();
										}
									});
								}
							}
							else
								Ext.Msg.alert('SET','Seleccione un Registro');
						 }
						
				     });
					}
				}, '->']
		,columns : [ 
		{
			id :'idCorrespondeCC',
			header :'Tipo de Ingreso',
			width :85,
			sortable :true,
			dataIndex :'descCorrespondeCC'
		},
		{
			header :'Empresa',
			width :75,
			sortable :true,
			dataIndex :'longEmpresa',
			//renderer: ocultarCampos
		}, {
			
			header :'Cliente',
			width :65,
			sortable :true,
			dataIndex :'longCliente'
		}, {
			
			header :'Codigo',
			width :65,
			sortable :true,
			dataIndex :'longCodigo'
		}, {
			
			header :'Subcodigo',
			width :65,
			sortable :true,
			dataIndex :'longSubcodigo'
		}
		,{
			header :'Division'
			,width :45
			,sortable :true
			,dataIndex :'longDivision'
		}
		,{
			header :'Constante1'
			,width :65
			,sortable :true
			,dataIndex :'const1'
		}
		,{
			header :'Constante2'
			,width :65
			,sortable :true
			,dataIndex :'const2'
		} 
		,{
			header :'Constante3'
			,width :65
			,sortable :true
			,dataIndex :'const3'
		} 
		,{
			header :'Chequera Destino'
			,width :100
			,sortable :true
			,dataIndex :'idChequeraDestino'
		} 
		,{
			header :'Variable1'
			,width :55
			,sortable :true
			,dataIndex :'longVar1'
		} 
	  ]
	  	,listeners:{
			dblclick:{
				fn:function(grid){
					Ext.getCmp(PF+'chkEmpresa').setValue(0);
					Ext.getCmp(PF+'chkCliente').setValue(0);
					Ext.getCmp(PF+'chkCodigo').setValue(0);
					Ext.getCmp(PF+'chkSubcodigo').setValue(0);
					Ext.getCmp(PF+'chkDivision').setValue(0);
					Ext.getCmp(PF+'chkConstante1').setValue(0);
					Ext.getCmp(PF+'chkConstante2').setValue(0);
					Ext.getCmp(PF+'chkConstante3').setValue(0);
					Ext.getCmp(PF+'chkVariable1').setValue(0);
					Ext.getCmp(PF+'chkVariable2').setValue(0);
					Ext.getCmp(PF+'chkVariable3').setValue(0);
					Ext.getCmp(PF+'valoresRef').setDisabled(false);
					NS. pBanModificar = true;
	        		var records = NS.gridGeneraReferencia.getSelectionModel().getSelections();
 					var vlEmpresa = records[0].get('longEmpresa');
 					var vlCliente = records[0].get('longCliente');
 					var vlCodigo = records[0].get('longCodigo');
 					var vlSubcodigo = records[0].get('longSubcodigo');
 					var vlDivision = records[0].get('longDivision');
 					var vlCons1 = records[0].get('const1');
 					var vlCons2 = records[0].get('const2');
 					var vlCons3 = records[0].get('const3');
 					var vlVar1 = records[0].get('longVar1');
 					var vlVar2 = NS.storeGeneraReferencia.getById(records[0].get('idCorrespondeCC')).get('longVar2');
 					var vlVar3 = NS.storeGeneraReferencia.getById(records[0].get('idCorrespondeCC')).get('longVar3');
 					
 					var voEmpresa = records[0].get('ordenEmpresa');
 					var voCliente = records[0].get('ordenCliente');
 					var voCodigo = records[0].get('ordenCodigo');
 					var voSubcodigo = records[0].get('ordenSubcodigo');
 					var voDivision = records[0].get('ordenDivision');
 					var voCons1 = records[0].get('ordenConst1');
 					var voCons2 = records[0].get('ordenConst2');
 					var voCons3 = records[0].get('ordenConst3');
 					var voVar1 = records[0].get('ordenVar1');
 					var voVar2 = NS.storeGeneraReferencia.getById(records[0].get('idCorrespondeCC')).get('ordenVar2');
 					var voVar3 = NS.storeGeneraReferencia.getById(records[0].get('idCorrespondeCC')).get('ordenVar3');
 					
 					var vbCalculo = NS.storeGeneraReferencia.getById(records[0].get('idCorrespondeCC')).get('baseCalculo');
 					Ext.getCmp(PF+'txtlEmpresa').setValue(vlEmpresa);
 					Ext.getCmp(PF+'txtlCliente').setValue(vlCliente);
 					Ext.getCmp(PF+'txtlCodigo').setValue(vlCodigo);
 					Ext.getCmp(PF+'txtlSubcodigo').setValue(vlSubcodigo);
 					Ext.getCmp(PF+'txtlDivision').setValue(vlDivision);
 					Ext.getCmp(PF+'txtlCons1').setValue(vlCons1);
 					Ext.getCmp(PF+'txtlCons2').setValue(vlCons2);
 					Ext.getCmp(PF+'txtlCons3').setValue(vlCons3);
 					Ext.getCmp(PF+'txtlVar1').setValue(vlVar1);
 					Ext.getCmp(PF+'txtlVar2').setValue(vlVar2);
 					Ext.getCmp(PF+'txtlVar3').setValue(vlVar3);
 					Ext.getCmp(PF+'txtoEmpresa').setValue(voEmpresa);
 					Ext.getCmp(PF+'txtoCliente').setValue(voCliente);
 					Ext.getCmp(PF+'txtoCodigo').setValue(voCodigo);
 					Ext.getCmp(PF+'txtoSubcodigo').setValue(voSubcodigo);
 					Ext.getCmp(PF+'txtoDivision').setValue(vlDivision);
 					Ext.getCmp(PF+'txtoCons1').setValue(voCons1);
 					Ext.getCmp(PF+'txtoCons2').setValue(voCons2);
 					Ext.getCmp(PF+'txtoCons3').setValue(voCons3);
 					Ext.getCmp(PF+'txtoVar1').setValue(voVar1);
 					Ext.getCmp(PF+'txtoVar2').setValue(voVar2);
 					Ext.getCmp(PF+'txtoVar3').setValue(voVar3);
 					
 					if(vlEmpresa > 0)
 					{
	 					Ext.getCmp(PF+'chkEmpresa').setValue(true);
	 					Ext.getCmp(PF+'cmbEmpresaD').setDisabled(false);
 					}
 					if(vlCliente > 0)
 					Ext.getCmp(PF+'chkCliente').setValue(true);
 					if(vlCodigo > 0)
 					Ext.getCmp(PF+'chkCodigo').setValue(true);
 					if(vlSubcodigo > 0)
 					Ext.getCmp(PF+'chkSubcodigo').setValue(true);
 					if(vlDivision > 0)
 					Ext.getCmp(PF+'chkDivision').setValue(true);
 					if(vlCons1 > 0)
 					{
	 					Ext.getCmp(PF+'chkConstante1').setValue(true);
	 					Ext.getCmp(PF+'cmbConstante1D').setDisabled(false);
 					}
 					if(vlCons2 > 0)
 					{
	 					Ext.getCmp(PF+'chkConstante2').setValue(true);
	 					Ext.getCmp(PF+'cmbConstante2D').setDisabled(false);
 					}
 					if(vlCons3 > 0)
	 				{
	 					Ext.getCmp(PF+'chkConstante3').setValue(true);
	 					Ext.getCmp(PF+'cmbConstante3D').setDisabled(false);
 					}
 					if(vlVar1 > 0)
 					{
	 					Ext.getCmp(PF+'chkVariable1').setValue(true);
	 					Ext.getCmp(PF+'cmbVariable1D').setDisabled(false);
 					}
 					if(vlVar2 > 0)
 					{
	 					Ext.getCmp(PF+'chkVariable2').setValue(true);
	 					Ext.getCmp(PF+'cmbVariable2D').setDisabled(false);
 					}
 					if(vlVar3 > 0)
 					{
	 					Ext.getCmp(PF+'chkVariable3').setValue(true);
	 				}
	 				
 					Ext.getCmp(PF+'cmbEmpresaD').setValue(records[0].get('destinoEmpresa'));
	 				Ext.getCmp(PF+'cmbConstante1D').setValue(records[0].get('destinoConst1'));
	 				Ext.getCmp(PF+'cmbConstante2D').setValue(records[0].get('destinoConst2'));
	 				Ext.getCmp(PF+'cmbConstante3D').setValue(records[0].get('destinoConst3'));
	 				Ext.getCmp(PF+'cmbVariable1D').setValue(records[0].get('destinoVar1'));
	 				Ext.getCmp(PF+'cmbVariable2D').setValue(records[0].get('destinoVar2'));
	 				Ext.getCmp(PF+'cmbVariable3D').setValue(records[0].get('destinoVar3'));
	 				Ext.getCmp(PF+'cmbTipoIngreso').setValue(records[0].get('idCorrespondeCC'));
	 				Ext.getCmp(PF+'cmbGrupo').setValue(records[0].get('idGrupoG'));
	 				Ext.getCmp(PF+'cmbRubro').setValue(records[0].get('idRubroR'));
	 				
	 				//Agregar conforme surjan bases de calculo
	 				if(vbCalculo === 'b10')
	 					Ext.getCmp(PF+'optB10').setValue(true);
 					Ext.getCmp(PF+'fieldTraspasoFID').setDisabled(true);
	 				
	 				var idCD = NS.storeGeneraReferencia.getById(records[0].get('idCorrespondeCC')).get('idChequeraDestino');
	 				var cambiaO = NS.storeGeneraReferencia.getById(records[0].get('idCorrespondeCC')).get('bCambiaOrigen');
	 				if(idCD != ''){
		                //fija el destino
		                if(cambiaO === 'S')
		                	Ext.getCmp(PF+'chkCambiaCtas').setValue(1);
               			else	
               				Ext.getCmp(PF+'chkCambiaCtas').setValue(0);
                	}
	 					Ext.getCmp(PF+'cmbTipoIngreso').setDisabled(false);
	 					Ext.getCmp(PF+'cmbGrupo').setDisabled(false);
	 					Ext.getCmp(PF+'cmbRubro').setDisabled(false);
	 					Ext.getCmp(PF+'aceptar').setDisabled(false);
	 					NS.storeIngreso.baseParams.noEmpresa = NS.noEmp;
	 					NS.storeIngreso.baseParams.idBanco = NS.noBan;
	 					NS.storeIngreso.baseParams.idChequera = NS.noChe;
						NS.storeIngreso.load();
						
						var idrubro = NS.storeGeneraReferencia.getById(records[0].get('idCorrespondeCC')).get('idRubro');
						NS.storeGrupo.baseParams.rubro = idrubro;
				}
				
			}
		}
		
	}); //termina grid
	
	/**llena el combo Tipo Ingreso*/
	NS.storeIngreso = new Ext.data.DirectStore( {
			paramsAsHash: false,
			root: '',
			paramOrder: ['noEmpresa', 'idBanco', 'idChequera'],
			directFn: GeneraReferenciaAction.obtenerTipoIngreso,
			fields: [
					{name: 'idCorrespondeCC'},
					{name: 'descCorrespondeCC'}
					],
			listeners: {
				load: function(s, records){
				}
			}
		});
		
	//combo fisico ingreso
	NS.cmbTipoIngreso = new Ext.form.ComboBox({
		 store: NS.storeIngreso 	//llamada al store
		,name: PF+'cmbTipoIngreso'
		,id: PF+'cmbTipoIngreso'
		,disabled: true
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :100
		,y :0
		,width :170
		,valueField:'idCorrespondeCC'
		,displayField:'descCorrespondeCC'
		,autocomplete: true
		,emptyText: 'Tipo Ingreso'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{		//evento click
				fn:function(combo, value) {
								Ext.getCmp(PF+'cmbGrupo').setValue('');
								Ext.getCmp(PF+'cmbRubro').setValue('');
								NS.storeGrupo.load();
								NS.contenedorReferencia.baseParams.idCorresponde = combo.getValue();
								}
						}	
					}
	});
	
	/**llena el combo Grupo*/
	NS.storeGrupo = new Ext.data.DirectStore( {
			paramsAsHash: false,
			root: '',
			paramOrder: ['rubro'],
			directFn: GeneraReferenciaAction.obtenerGrupo,
			idProperty: 'idGrupoG',
			fields: [
					{name: 'idGrupoG'},
					{name: 'descGrupoG'}
					],
			listeners: {
				load: function(s, records){
				}
			}
		});
		//NS.storeGrupo.load();
	//combo fisico grupo
	NS.cmbGrupo = new Ext.form.ComboBox({
		 store: NS.storeGrupo 	//llamada al store
		,name: PF+'cmbGrupo'
		,id: PF+'cmbGrupo'
		,disabled: true
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :100
		,y :30
		,width :170
		,valueField:'idGrupoG'
		,displayField:'descGrupoG'
		,autocomplete: true
		,emptyText: 'Grupo'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{		//evento click
				fn:function(combo, value) {
								Ext.getCmp(PF+'cmbRubro').setValue('');
								var idGrupo = Ext.getCmp(PF+'cmbGrupo').getValue();
	 							NS.storeRubro.baseParams.grupo = idGrupo;
	 							NS.storeRubro.load();
								}
						}	
					}
	});
	
	
	/**llena el combo Rubro*/
	NS.storeRubro = new Ext.data.DirectStore( {
			paramsAsHash: false,
			root: '',
			paramOrder: ['grupo'],
			directFn: GeneraReferenciaAction.obtenerRubro,
			idProperty: 'idRubroR',
			fields: [
					{name: 'idRubroR'},
					{name: 'descRubroR'}
					],
			listeners: {
				load: function(s, records){
				}
			}
		});

	//combo fisico rubro
	NS.cmbRubro = new Ext.form.ComboBox({
		 store: NS.storeRubro 	//llamada al store
		,name: PF+'cmbRubro'
		,id: PF+'cmbRubro'
		,disabled: true
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :100
		,y :60
		,width :170
		,valueField:'idRubroR'
		,displayField:'descRubroR'
		,autocomplete: true
		,emptyText: 'Rubro'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{		//evento click
				fn:function(combo, value) {
							NS.contenedorReferencia.baseParams.rubro = combo.getValue();
								}
						}	
					}
	});
	
	NS.contenedorReferencia =new Ext.FormPanel({
	    title: 'Catalogo Armado de Referencia',
	    width: 786,
	    height: 726,
	    padding: 10,
    	frame: true,
    	autoScroll: true,
	    layout: 'absolute',
	    renderTo: NS.tabContId,
	    baseParams: {
			 	empresa:'',
			 	banco: '',
			 	chequera: '',
			 	empresaD: '',
			 	const1: '',
			 	const2: '',
			 	const3: '',
			 	var1: '',
			 	var2: '',
			 	var3:'',
			 	rubro: '',
			 	idCorresponde: '',
			 	predet: ''
		},
		paramOrder: ['empresa','banco','chequera','empresaD','const1','const2','const3','var1','var2','var3', 'rubro','idCorresponde','predet'],
	    api: {submit: GeneraReferenciaAction.insertarModificarIngreso},
	    items:[
            {
                xtype: 'fieldset',
                title: 'Cuenta de Deposito',
                x: 10,
                y: 10,
                width: 760,
                height: 90,
                layout: 'absolute',
                items: [
                    {
                        xtype: 'label',
                        text: 'Empresa:',
                        x: 30,
                        y: 0
                    },
                    {
                        xtype: 'textfield',
                        x: 100,
                        y: 0,
                        width: 40
                        ,id: PF+'txtEmpresa'
                        ,name: PF+'txtEmpresa'
                        ,listeners:{
                        	change:{
                        		fn:function(caja, valor){
                        			var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresa',NS.cmbEmpresa.getId());
                        			NS.accionarCmbEmpresa(comboValue);
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'label',
                        text: 'Banco:',
                        x: 40,
                        y: 30
                    },
                    {
                        xtype: 'textfield',
                        x: 100,
                        y: 30,
                        width: 40
                        ,id: PF+'txtBanco'
                        ,name: PF+'txtBanco'
                        ,disabled: true
                        ,listeners:{
                        	change:{
                        		fn:function(caja, valor){
                        			var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtBanco',NS.cmbBanco.getId());
                        			NS.accionarCmbBanco(comboValue);
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'label',
                        text: 'Chequera:',
                        x: 330,
                        y: 30
                    },
                     NS.cmbBanco
                    ,NS.cmbChequera
                    ,NS.cmbEmpresa
                    ,{
                        xtype: 'button',
                        text: 'Buscar',
                        x: 590,
                        y: 20,
                        width: 70,
                        height: 22,
                        handler: function(){
								if(Ext.getCmp(PF+'cmbEmpresa').getValue() == '')
		                       		Ext.MessageBox.alert("SET"," Seleccione una empresa");
		                        else if(Ext.getCmp(PF+'cmbBanco').getValue() == '')
		                        	Ext.MessageBox.alert("SET"," Seleccione el banco");
		                        else if(Ext.getCmp(PF+'cmbChequera').getValue() == '')
		                        	Ext.MessageBox.alert("SET"," Seleccione la chequera");
		                        else
									NS.storeGeneraReferencia.load();
						}
                    }
                ]
            },
            {
                xtype: 'fieldset',
                //title: 'Grid',
                x: 10,
                y: 110,
                height: 150,
                width: 760,
                items: [NS.gridGeneraReferencia]
            },
            {
                xtype: 'fieldset',
                title: '',
                x: 10,
                y: 275,
                width: 760,
                height: 415,
                layout: 'absolute',
                items: [
                    {
                        xtype: 'fieldset',
                        title: 'Valores que arman la referencia',
                        name: PF+'valoresRef',
                        id: PF+'valoresRef',
                        x: 10,
                        y: 0,
                        width: 400,
                        height: 390,
                        layout: 'absolute',
                        disabled: true,
                        maskDisabled: false,
                        items: [
                            {
                                xtype: 'checkbox'
                                ,name: PF+'chkEmpresa'
                                ,id: PF+'chkEmpresa'
                                ,x: 10
                                ,y: 30
                                ,boxLabel: 'Empresa'
                                ,value: false
                                ,listeners:{
                                	check:{
                                		fn: function(box){
                                			if(box.getValue())
                                			{
					 					 		Ext.getCmp(PF+'cmbEmpresaD').setDisabled(false);
					 					 	}
							 				else
							 					Ext.getCmp(PF+'cmbEmpresaD').setDisabled(true);
                                		}
                                	}
                                }
                            },
                            {
                                xtype: 'checkbox'
                                ,name: PF+'chkCliente'
                                ,id: PF+'chkCliente'
                                ,x: 10
                                ,y: 60
                                ,boxLabel: 'Cliente'
                                ,value: false
                            },
                            {
                                xtype: 'checkbox'
                                ,name: PF+'chkCodigo'
                                ,id: PF+'chkCodigo'
                                ,x: 10
                                ,y: 90
                                ,boxLabel: 'Codigo'
                                ,value: false
                            },
                            {
                                xtype: 'checkbox'
                                ,name: PF+'chkSubcodigo'
                                ,id: PF+'chkSubcodigo'
                                ,x: 10
                                ,y: 120
                                ,boxLabel: 'Subcodigo'
                                ,value: false
                            },
                            {
                                xtype: 'checkbox'
                                ,name: PF+'chkDivision'
                                ,id: PF+'chkDivision'
                                ,x: 10
                                ,y: 150
                                ,boxLabel: 'Division'
                                ,value: false
                            },
                            {
                                xtype: 'checkbox'
                                ,name: PF+'chkConstante1'
                                ,id: PF+'chkConstante1'
                                ,x: 10
                                ,y: 180
                                ,boxLabel: 'Constante 1'
                                 ,value: false
                                ,listeners:{
                                	check:{
                                		fn: function(box){
                                			if(box.getValue())
					 					 		Ext.getCmp(PF+'cmbConstante1D').setDisabled(false);
							 				else
							 					Ext.getCmp(PF+'cmbConstante1D').setDisabled(true);
                                		}
                                	}
                                }
                            },
                            {
                                xtype: 'checkbox'
                                ,name: PF+'chkConstante2'
                                ,id: PF+'chkConstante2'
                                ,x: 10
                                ,y: 210
                                ,boxLabel: 'Constante 2'
                                 ,value: false
                                ,listeners:{
                                	check:{
                                		fn: function(box){
                                			if(box.getValue())
					 					 		Ext.getCmp(PF+'cmbConstante2D').setDisabled(false);
							 				else
							 					Ext.getCmp(PF+'cmbConstante2D').setDisabled(true);
                                		}
                                	}
                                }
                            },
                            {
                                xtype: 'checkbox'
                                ,name: PF+'chkConstante3'
                                ,id: PF+'chkConstante3'
                                ,x: 10
                                ,y: 240
                                ,boxLabel: 'Constante 3'
                                ,value: false
                                ,listeners:{
                                	check:{
                                		fn: function(box){
                                			if(box.getValue())
					 					 		Ext.getCmp(PF+'cmbConstante3D').setDisabled(false);
							 				else
							 					Ext.getCmp(PF+'cmbConstante3D').setDisabled(true);
                                		}
                                	}
                                }
                            },
                            {
                                xtype: 'checkbox'
                                ,name: PF+'chkVariable1'
                                ,id: PF+'chkVariable1'
                                ,x: 10
                                ,y: 270
                                ,boxLabel: 'Variable 1'
                                ,value: false
                                ,listeners:{
                                	check:{
                                		fn: function(box){
                                			if(box.getValue())
					 					 		Ext.getCmp(PF+'cmbVariable1D').setDisabled(false);
							 				else
							 					Ext.getCmp(PF+'cmbVariable1D').setDisabled(true);
                                		}
                                	}
                                }
                            },
                            {
                                xtype: 'checkbox'
                                ,name: PF+'chkVariable2'
                                ,id: PF+'chkVariable2'
                                ,x: 10
                                ,y: 300
                                ,boxLabel: 'Variable 2'
                                ,value: false
                                ,listeners:{
                                	check:{
                                		fn: function(box){
                                			if(box.getValue())
					 					 		Ext.getCmp(PF+'cmbVariable2D').setDisabled(false);
							 				else
							 					Ext.getCmp(PF+'cmbVariable2D').setDisabled(true);
                                		}
                                	}
                                }
                            },
                            {
                                xtype: 'checkbox'
                                ,name: PF+'chkVariable3'
                                ,id: PF+'chkVariable3'
                                ,x: 10
                                ,y: 330
                                ,boxLabel: 'Variable 3'
                                ,value: false
                                ,listeners:{
                                	check:{
                                		fn: function(box){
                                			if(box.getValue())
					 					 		Ext.getCmp(PF+'cmbVariable3D').setDisabled(false);
							 				else
							 					Ext.getCmp(PF+'cmbVariable3D').setDisabled(true);
							 					
                                		}
                                	}
                                }
                            },
                            {
                                xtype: 'label',
                                text: 'Valor',
                                x: 20,
                                y: -2
                            },
                            {
                                xtype: 'label',
                                text: 'Longitud',
                                x: 110,
                                y: 0
                            },
                            {
                                xtype: 'label',
                                text: 'Orden',
                                x: 210,
                                y: 0
                            },
                            {
                                xtype: 'label',
                                text: 'Destino',
                                x: 300,
                                y: 0
                            },
                            {
                                xtype: 'numberfield'
                                ,name: PF+'txtlEmpresa'
                                ,id: PF+'txtlEmpresa'
                                ,value: 0
                                ,x: 120
                                ,y: 30
                                ,width: 30
                            },
                            {
                                xtype: 'numberfield'
                                ,name: PF+'txtoEmpresa'
                                ,id: PF+'txtoEmpresa'
                                ,value: 0
                                ,x: 210
                                ,y: 30
                                ,width: 30
                            },
                            NS.cmbEmpresaD,
                            {
                                xtype: 'numberfield'
                                ,name: PF+'txtlCliente'
                                ,id: PF+'txtlCliente'
                                ,value: 0
                                ,x: 120
                                ,y: 60
                                ,width: 30
                            },
                            {
                                xtype: 'numberfield'
                                ,name: PF+'txtoCliente'
                                ,id: PF+'txtoCliente'
                                ,value: 0
                                ,x: 210
                                ,y: 60
                                ,width: 30
                            },
                            {
                                xtype: 'numberfield'
                                ,name: PF+'txtlCodigo'
                                ,id: PF+'txtlCodigo'
                                ,value: 0
                                ,x: 120
                                ,y: 90
                                ,width: 30
                            },
                            {
                                xtype: 'numberfield'
                                ,name: PF+'txtoCodigo'
                                ,id: PF+'txtoCodigo'
                                ,value: 0
                                ,x: 210
                                ,y: 90
                                ,width: 30
                            },
                            {
                                xtype: 'numberfield'
                                ,name: PF+'txtlSubcodigo'
                                ,id: PF+'txtlSubcodigo'
                                ,value: 0
                                ,x: 120
                                ,y: 120
                                ,width: 30
                            },
                            {
                                xtype: 'numberfield'
                                ,name: PF+'txtoSubcodigo'
                                ,id: PF+'txtoSubcodigo'
                                ,value: 0
                                ,x: 210
                                ,y: 120
                                ,width: 30
                            },
                            {
                                xtype: 'numberfield'
                                ,name: PF+'txtlDivision'
                                ,id: PF+'txtlDivision'
                                ,value: 0
                                ,x: 120
                                ,y: 150
                                ,width: 30
                            },
                            {
                                xtype: 'numberfield'
                                ,name: PF+'txtoDivision'
                                ,id: PF+'txtoDivision'
                                ,value: 0
                                ,x: 210
                                ,y: 150
                                ,width: 30
                            },
                            {
                                xtype: 'numberfield'
                                ,name: PF+'txtlCons1'
                                ,id: PF+'txtlCons1'
                                ,x: 120
                                ,y: 180
                                ,width: 30
                            },
                            {
                                xtype: 'numberfield'
                                ,name: PF+'txtoCons1'
                                ,id: PF+'txtoCons1'
                                ,value: 0
                                ,x: 210
                                ,y: 180
                                ,width: 30
                            },
                            NS.cmbConstante1D,
                            {
                                xtype: 'numberfield'
                                ,name: PF+'txtlCons2'
                                ,id: PF+'txtlCons2'
                                ,x: 120
                                ,y: 210
                                ,width: 30
                            },
                            {
                                xtype: 'numberfield'
                                ,name: PF+'txtoCons2'
                                ,id: PF+'txtoCons2'
                                ,value: 0
                                ,x: 210
                                ,y: 210
                                ,width: 30
                            },
                             NS.cmbConstante2D,
                            {
                                xtype: 'numberfield'
                                ,name: PF+'txtlCons3'
                                ,id: PF+'txtlCons3'
                                ,x: 120
                                ,y: 240
                                ,width: 30
                            },
                            {
                                xtype: 'numberfield'
                                ,name: PF+'txtoCons3'
                                ,id: PF+'txtoCons3'
                                ,value: 0
                                ,x: 210
                                ,y: 240
                                ,width: 30
                            },
                            NS.cmbConstante3D,
                            {
                                xtype: 'numberfield'
                                ,name: PF+'txtlVar1'
                                ,id: PF+'txtlVar1'
                                ,value: 0
                                ,x: 120
                                ,y: 270
                                ,width: 30
                            },
                            {
                                xtype: 'numberfield'
                                ,name: PF+'txtoVar1'
                                ,id: PF+'txtoVar1'
                                ,value: 0
                                ,x: 210
                                ,y: 270
                                ,width: 30
                            },
                            NS.cmbVariable1D,
                            {
                                xtype: 'numberfield'
                                ,name: PF+'txtlVar2'
                                ,id: PF+'txtlVar2'
                                ,value: 0
                                ,x: 120
                                ,y: 300
                                ,width: 30
                            },
                            {
                                xtype: 'numberfield'
                                ,name: PF+'txtoVar2'
                                ,id: PF+'txtoVar2'
                                ,value: 0
                                ,x: 210
                                ,y: 300
                                ,width: 30
                            },
                            NS.cmbVariable2D,
                            {
                                xtype: 'numberfield'
                                ,name: PF+'txtlVar3'
                                ,id: PF+'txtlVar3'
                                ,value: 0
                                ,disabled: true
                                ,x: 120
                                ,y: 330
                                ,width: 30
                            },
                            {
                                xtype: 'numberfield'
                                ,name: PF+'txtoVar3'
                                ,id: PF+'txtoVar3'
                                ,value: 0
                                ,disabled: true
                                ,x: 210
                                ,y: 330
                                ,width: 30
                            },
                            NS.cmbVariable3D
                        ]
                    },
                    {
                        xtype: 'fieldset',
                        title: '-',
                        width: 310,
                        height: 390,
                        x: 420,
                        y: 0,
                        layout: 'absolute',
                        items: [
                            {
                                xtype: 'label',
                                text: 'Tipo de ingreso:',
                                x: 0,
                                y: 0
                            },
                            {
                                xtype: 'label',
                                text: 'Grupo:',
                                x: 0,
                                y: 30
                            },
                            {
                                xtype: 'label',
                                text: 'Rubro:',
                                x: 0,
                                y: 60
                            }
                            ,NS.cmbTipoIngreso
                            ,NS.cmbGrupo
                            ,NS.cmbRubro
                            ,
                            {
                                xtype: 'fieldset',
                                title: 'Base de calculo',
                                name: PF+'fBaseCalculo',
                                id: PF+'fBaseCalculo',
                                width: 120,
                                height: 60,
                                x: 0,
                                y: 100,
                                layout: 'absolute',
                                disabled: true,
                                maskDisabled: false,
                                items: [
                                    {
                                        xtype: 'radio',
                                        name: PF+'optB10',
                                        id: PF+'optB10',
                                        boxLabel: 'Base 10',
                                        x: 0,
                                        y: 0,
                                        width: 80
                                    }
                                ]
                            },
                            {
                                xtype: 'fieldset',
                                title: 'Referencia Automatica',
                                id: PF+'fieldRefAutomatica',
                                name: PF+'fieldRefAutomatica',
                                width: 150,
                                height: 60,
                                x: 130,
                                y: 100,
                                disabled: true,
                                maskDisabled: false,
                                layout: 'absolute',
                                items: [
                                    {
                                        xtype: 'checkbox',
                                        boxLabel: 'Predeterminada',
                                        id: PF+'chkPredeterminada',
                                        name: PF+'chkPredeterminada',
                                        x: 0,
                                        y: 0
                                    }
                                ]
                            },
                            {
                                xtype: 'fieldset',
                                title: 'Cuenta de Traspaso FID',
                                id: PF+'fieldTraspasoFID',
                                name: PF+'fieldTraspasoFID',
                                x: 0,
                                y: 175,
                                width: 280,
                                height: 150,
                                layout: 'absolute',
                                hidden: true,
                                items: [
                                    {
                                        xtype: 'label',
                                        text: 'Empresa',
                                        x: 0,
                                        y: 0
                                    },
                                    {
                                        xtype: 'label',
                                        text: 'Banco',
                                        x: 0,
                                        y: 40
                                    },
                                    {
                                        xtype: 'label',
                                        text: 'Chequera',
                                        x: 0,
                                        y: 80
                                    },
                                    NS.cmbEmpresaFID,
                                    NS.cmbBancoFID,
                                    NS.cmbChequeraFID,
                                    {
                                        xtype: 'textfield',
                                        name: PF+'txtEmpresaFID',
                                        id: PF+'txtEmpresaFID',
                                        disabled: true,
                                        width: 40,
                                        x: 60,
                                        y: 0
                                    },
                                    {
                                        xtype: 'textfield',
                                        name: PF+'txtBancoFID',
                                        id: PF+'txtBancoFID',
                                        disabled: true,
                                        x: 60,
                                        y: 40,
                                        width: 40
                                    },
                                    {
                                        xtype: 'checkbox',
                                        boxLabel: 'Cambia Cuentas',
                                        id: PF+'chkCambiaCtas',
                                        name: PF+'chkCambiaCtas',
                                        value: 0,
                                        x: 190,
                                        y: 80
                                    }
                                ]
                            }
                        ]
                        ///botones
                            ,buttons:[
									{ 
									text:'Ejecutar' 
									,name: PF+'aceptar'
									,id: PF+'aceptar'
									,x: 140
                                	,y: 330
			                        ,disabled: true
									,formBind: true 
										// Función que se ejecuta cuando el usuario pulsa el botón. 
									,handler:function(){ 
									if(NS.contenedorReferencia.getForm().isValid()){
										var records = NS.storeGeneraReferencia.data.items; 
										NS.bytLongitud = 0;
										var lstOrdenValores = new Array();
										var ordenaValores = new Array();
										var i = 0;
										
										if( NS.valorReferenciaCompleto(Ext.getCmp(PF+'chkEmpresa').getValue(), Ext.getCmp(PF+'txtlEmpresa').getValue(), Ext.getCmp(PF+'txtoEmpresa').getValue(), 'Empresa') ||
										    NS.valorReferenciaCompleto(Ext.getCmp(PF+'chkCliente').getValue(), Ext.getCmp(PF+'txtlCliente').getValue(), Ext.getCmp(PF+'txtoCliente').getValue(), 'Cliente') ||
										   	NS.valorReferenciaCompleto(Ext.getCmp(PF+'chkCodigo').getValue(), Ext.getCmp(PF+'txtlCodigo').getValue(), Ext.getCmp(PF+'txtoCodigo').getValue(), 'Codigo') ||
										    NS.valorReferenciaCompleto(Ext.getCmp(PF+'chkSubcodigo').getValue(), Ext.getCmp(PF+'txtlSubcodigo').getValue(), Ext.getCmp(PF+'txtoSubcodigo').getValue(), 'Subcodigo') || 
										   	NS.valorReferenciaCompleto(Ext.getCmp(PF+'chkDivision').getValue(), Ext.getCmp(PF+'txtlDivision').getValue(), Ext.getCmp(PF+'txtoDivision').getValue(), 'Division') ||
										    NS.valorReferenciaCompleto(Ext.getCmp(PF+'chkConstante1').getValue(), Ext.getCmp(PF+'txtlCons1').getValue(), Ext.getCmp(PF+'txtoCons1').getValue(), 'Constante1') ||
										    NS.valorReferenciaCompleto(Ext.getCmp(PF+'chkConstante2').getValue(), Ext.getCmp(PF+'txtlCons2').getValue(), Ext.getCmp(PF+'txtoCons2').getValue(), 'Constante2') ||
										    NS.valorReferenciaCompleto(Ext.getCmp(PF+'chkConstante3').getValue(), Ext.getCmp(PF+'txtlCons3').getValue(), Ext.getCmp(PF+'txtoCons3').getValue(), 'Constante3') ){ 
			    								return;
		    							}
		    							
									    if(Ext.getCmp(PF+'chkEmpresa').getValue() == 1) {
									        NS.bytLongitud = NS.bytLongitud + Ext.getCmp(PF+'txtlEmpresa').getValue();
									        ordenaValores[i] = Ext.getCmp(PF+'txtoEmpresa').getValue();
									        i = i + 1;
									        }
									    else{
									        Ext.getCmp(PF+'txtlEmpresa').setValue(0);
									        Ext.getCmp(PF+'txtoEmpresa').setValue(0);
									        }
									    
									    if(Ext.getCmp(PF+'chkCliente').getValue() == 1){
									        NS.bytLongitud = NS.bytLongitud + Ext.getCmp(PF+'txtlCliente').getValue();
									        ordenaValores[i] = Ext.getCmp(PF+'txtoCliente').getValue();
									        i = i + 1;
									        }
									    else{
									        Ext.getCmp(PF+'txtlCliente').setValue(0);
									        Ext.getCmp(PF+'txtoCliente').setValue(0);
									        }
									    
									    if(Ext.getCmp(PF+'chkCodigo').getValue() == 1){
									        NS.bytLongitud = NS.bytLongitud + Ext.getCmp(PF+'txtlCodigo').getValue();
									        ordenaValores[i] = Ext.getCmp(PF+'txtoCodigo').getValue();
									        i = i + 1;
									        }
									    else{
									        Ext.getCmp(PF+'txtlCodigo').setValue(0);
									        Ext.getCmp(PF+'txtoCodigo').setValue(0);
									        }
									        
									    if(Ext.getCmp(PF+'chkSubcodigo').getValue() == 1){
									        NS.bytLongitud = NS.bytLongitud + Ext.getCmp(PF+'txtlSubcodigo').getValue();
									        ordenaValores[i] = Ext.getCmp(PF+'txtoSubcodigo').getValue();
									        i = i + 1;
									        }
									    else{
									        Ext.getCmp(PF+'txtlSubcodigo').setValue(0);
									        Ext.getCmp(PF+'txtoSubcodigo').setValue(0);
									        }
									        
									    if(Ext.getCmp(PF+'chkDivision').getValue() == 1){
									        NS.bytLongitud = NS.bytLongitud + Ext.getCmp(PF+'txtlDivision').getValue();
									        ordenaValores[i] = Ext.getCmp(PF+'txtoDivision').getValue();
									        i = i + 1;
									        }
									    else{
									        Ext.getCmp(PF+'txtlDivision').setValue(0);
									        Ext.getCmp(PF+'txtoDivision').setValue(0);
									        }
									        
									   if(Ext.getCmp(PF+'chkConstante1').getValue() == 1){
									        NS.bytLongitud = NS.bytLongitud + NS.obtenerTamano(Ext.getCmp(PF+'txtlCons1').getValue());
									        ordenaValores[i] = Ext.getCmp(PF+'txtoCons1').getValue();
									        i = i + 1;
									        }
									    else{
									        Ext.getCmp(PF+'txtlCons1').setValue('');
									        Ext.getCmp(PF+'txtoCons1').setValue(0);
									        }
									        
									     if(Ext.getCmp(PF+'chkConstante2').getValue() == 1){
									        NS.bytLongitud = NS.bytLongitud + NS.obtenerTamano(Ext.getCmp(PF+'txtlCons2').getValue());
									        ordenaValores[i] = Ext.getCmp(PF+'txtoCons2').getValue();
									        i = i + 1;
									        }
									    else{
									        Ext.getCmp(PF+'txtlCons2').setValue('');
									        Ext.getCmp(PF+'txtoCons2').setValue(0);
									        }
									        
									    if(Ext.getCmp(PF+'chkConstante3').getValue() == 1){
									        NS.bytLongitud = NS.bytLongitud + NS.obtenerTamano(Ext.getCmp(PF+'txtlCons3').getValue());
									        ordenaValores[i] = Ext.getCmp(PF+'txtoCons3').getValue();
									        i = i + 1;
									        }
									    else{
									        Ext.getCmp(PF+'txtlCons3').setValue('');
									        Ext.getCmp(PF+'txtoCons3').setValue(0);
									        }
									    
									    if(Ext.getCmp(PF+'chkVariable1').getValue() == 1){
									        NS.bytLongitud = NS.bytLongitud + Ext.getCmp(PF+'txtlVar1').getValue();
									        ordenaValores[i] = Ext.getCmp(PF+'txtoVar1').getValue();
									        i = i + 1;
									        }
									    else{
									        Ext.getCmp(PF+'txtlVar1').setValue(0);
									        Ext.getCmp(PF+'txtoVar1').setValue(0);
									        }
									    
									    if(Ext.getCmp(PF+'chkVariable2').getValue() == 1){
									        NS.bytLongitud = NS.bytLongitud + Ext.getCmp(PF+'txtlVar2').getValue();
									        ordenaValores[i] = Ext.getCmp(PF+'txtoVar2').getValue();
									        i = i + 1;
									        }
									    else{
									        Ext.getCmp(PF+'txtlVar2').setValue(0);
									        Ext.getCmp(PF+'txtoVar2').setValue(0);
									        }
									    
									    if(Ext.getCmp(PF+'chkVariable3').getValue() == 1){
									        NS.bytLongitud = NS.bytLongitud + Ext.getCmp(PF+'txtlVar3').getValue();
									        ordenaValores[i] = Ext.getCmp(PF+'txtoVar3').getValue();
									        i = i + 1;
									        }
									    else{
									        Ext.getCmp(PF+'txtlVar3').setValue(0);
									        Ext.getCmp(PF+'txtoVar3').setValue(0);
									        }
									    if(parseInt(NS.bytLongitud) == 0)
											{
											Ext.Msg.alert('SET','La referencia debe estar formada por al menos un elemento');
											Ext.getCmp(PF+'txtlEmpresa').focus();
											return;
											}
										else if(parseInt(NS.bytLongitud) > 17)
											{
											Ext.Msg.alert('SET','La longitud de la referencia no debe rebasar las 17 posiciones');
											Ext.getCmp(PF+'txtlEmpresa').focus();
											return;
											}
									    
									    NS.bytLongitud = NS.bytLongitud + 1; //Por el digito verificador en la referencia

									      //El orden de los elementos es correcto ?
									    lstOrdenValores = ordenaValores.sort();
										    for(var y = 0; y <= lstOrdenValores.length - 1; y = y + 1){  
										    	if(lstOrdenValores[y] != (y + 1) ){
									    			if(lstOrdenValores[y] > (y + 1)){
									    	 		Ext.Msg.alert('SET','Revise el orden de los elementos seleccionados Falta el orden ' + (y + 1) );
									    	 		return;
									    	 		}
									    	 		else{
									            	Ext.Msg.alert('SET','Revise el orden de los elementos seleccionados Repitio el orden ' + lstOrdenValores[y] );
									            	return;
									            	}
										    	}
										    }
									    
										NS.strTipoIngreso = Ext.getCmp(PF+'cmbTipoIngreso').getValue();
										
										//verifica seleccion de tipo ingreso
										if(NS.strTipoIngreso == '' || NS.strTipoIngreso == null)
										{
											Ext.Msg.alert('SET','Seleccione el tipo de origen');
											Ext.getCmp(PF+'cmbTipoIngreso').focus();
											return;
										}
										//verifica seleccion de grupo
										else if(Ext.getCmp(PF+'cmbGrupo').getValue() == '' || Ext.getCmp(PF+'cmbGrupo').getValue() == null)
										{
											Ext.Msg.alert('SET','Seleccione el grupo');
											Ext.getCmp(PF+'cmbGrupo').focus();
											return;
										}
										//verifica seleccion de rubro
										else if(Ext.getCmp(PF+'cmbRubro').getValue() == '' || Ext.getCmp(PF+'cmbRubro').getValue() == null)
										{
											Ext.Msg.alert('SET','Seleccione el rubro');
											Ext.getCmp(PF+'cmbRubro').focus();
											return;
										}
										
										//Datos de Destino seleccionados ?
										//if((PF+'fieldTraspasoFID').isVisible()){
									        if(NS.strTipoIngreso === 'C'){
									        if(NS.noEmpFID == 0 || NS.noBanFID == 0 || NS.noCheFID == ''){
									        		Ext.Msg.alert('SET','Verifique los datos de la cuenta de traspaso FID');
									                return;
									         	}
									        }
									   // }
										
									    if(NS.strTipoIngreso === 'C' || !Ext.getCmp(PF+'fieldTraspasoFID').isVisible())
									    {
									        NS.cambiaCtas = false;
									        NS.chequeDestino = '';
									    }
									    else
									    {
									        if( Ext.getCmp(PF+'chkCambiaCtas').getValue() == 0 )
									            NS.cambiaCtas = false
									        else
									            NS.cambiaCtas = true
									        NS.chequeDestino = Ext.getCmp(PF+'cmbChequeraFID').getValue();
									    }
									    
									    NS.predeterminada = '';
									    if(Ext.getCmp(PF+'chkPredeterminada').getValue() == 0)
									     	NS.predeterminada = 'N';
									    else
									    	NS.predeterminada = 'S';
											    	
										NS.contenedorReferencia.getForm().submit({ 
											params: {
												bolCambiaCtas: NS.cambiaCtas
												,banderaF: NS.pBanModificar
												,chequeD: NS.chequeDestino
												,sumLong: NS.bytLongitud
												,predet: NS.predeterminada
												,corresponde: NS.strTipoIngreso
												,prefijo: PF
											}
											,method:'POST' 
											,waitTitle:'Conectando' 
											,waitMsg:'Verificando datos...' 
											,success:function(){
												
										    	var records = NS.storeGeneraReferencia.data.items;
												if(!NS. pBanModificar)		//insertar
												{
										            NS.storeGeneraReferencia.load();
										            NS.gridGeneraReferencia.getView().refresh();
													
									            	Ext.Msg.alert('SET','Registro Insertado');
												}
												else			//modificar
												{
													NS.storeGeneraReferencia.load();
											        NS.gridGeneraReferencia.getView().refresh();
														
													Ext.Msg.alert('SET','Registro Modificado');
													NS. pBanModificar = false;
												}
												//NS.contenedorReferencia.getForm().reset();
												//NS.storeGeneraReferencia.load();
											},
											failure:function(form, action){ 
												if(action.failureType == 'server'){ 
												if(NS.pBanModificar){ 
													Ext.Msg.alert('SET', 'No se puede actualizar el registro por alguna referencia');
													NS.pBanModificar = false;
												}
												else
												Ext.Msg.alert('SET', 'Error al insertar el registro'); 
												 }
												else{ 
												Ext.Msg.alert(	'SET', 'Fallo de conexión con el servidor de autenticación'); 
												} 
											NS.contenedorReferencia.getForm().reset(); 
											NS.storeGeneraReferencia.load();
									} 
								}); //cierra submit
							  } 
							 }
							}
							,{
								text:'Limpiar' 
								,x: 220
	                            ,y: 330
								,formBind: true,
								handler: function(){
			               			NS.contenedorReferencia.getForm().reset();
			               			NS.pBanModificar = false;
			               			NS.storeGeneraReferencia.removeAll();
             			   			NS.gridGeneraReferencia.getView().refresh();
		            			}
							}
						] //cierra buttons
                    }
                ]
            }
        ]
    });
    
    NS.obtenerTamano = function(num){
    	num = ''+num;
    	return num.length;
    };
    
    NS.valorReferenciaCompleto =  function(chkValor, txtLong, txtOrden, strNomValor){
    	var salir = false;
	    if(chkValor == 1){
	        if(txtLong == 0) {
	            Ext.Msg.alert('SET','La longitud de ' + strNomValor + ' es incorrecto');
	            txtLong.focus;
	            salir = true;
	        }
	        else if(txtOrden == 0){
	            Ext.Msg.alert('SET','El orden de ' + strNomValor + ' es incorrecto');
	            txtOrden.focus;
	            salir = true;
	        }
	    }
	    return salir;
      };
      
	NS.contenedorReferencia.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
      
});