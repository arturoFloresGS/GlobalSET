Ext.onReady(function(){
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init(); 
	var NS = Ext.namespace('apps.SET.Inversiones.Operacion.OrdenDeInversion');
	var PF = apps.SET.tabID+'.';
	NS.tabContId = apps.SET.tabContainerId;
	NS.sHora = "";
	NS.sFecHoy = apps.SET.FEC_HOY;
	NS.tipoChequera = '';
	NS.cveContrato = 0;
	NS.noEmpresaInv = 0;
	NS.isrIgualInt = 'S';
	NS.aplicaISR = 'S';
	
	function cambiarFecha(fecha) {
			var mesArreglo=new Array(11);
			mesArreglo[0]="Jan";mesArreglo[1]="Feb";mesArreglo[2]="Mar";mesArreglo[3]="Apr";
			mesArreglo[4]="May";mesArreglo[5]="Jun";mesArreglo[6]="Jul";mesArreglo[7]="Aug";mesArreglo[8]="Sep";
			mesArreglo[9]="Oct";mesArreglo[10]="Nov";mesArreglo[11]="Dec";
			var mesDate=fecha.substring(4,7);
			var dia=fecha.substring(8,10);
			var anio=fecha.substring(11,15);
			
			for(var i=0;i<12;i=i+1) {
				if(mesArreglo[i]===mesDate) {
				var mes=i+1;
					if(mes<10)
					mes = '0' + mes;
				}
			}
			var fechaString=''+dia+'/'+mes+'/'+anio;
			return fechaString;		
		};
	

	    NS.inicial = new Ext.form.NumberField({
		 	
			id: PF + 'inicial',
	        name: PF + 'inicial',
	    	value:'',
	        x: 120,
	        y: 60,
	        width: 430, 
	    	emptyText: '',

		  });
	    NS.ven = new Ext.form.NumberField({
		 	
			id: PF + 'ven',
	        name: PF + 'ven',
	    	value:'',
	        x: 120,
	        y: 60,
	        width: 430, 
	    	emptyText: '',

		  });

	    NS.bar = new Ext.form.NumberField({
		 	
			id: PF + 'bar',
	        name: PF + 'bar',
	    	value:'',
	        x: 120,
	        y: 60,
	        width: 430, 
	    	emptyText: '',

		  });

	    
NS.inv = new Ext.form.NumberField({
		 	
			id: PF + 'inv',
	        name: PF + 'inv',
	    	value:'',
	        x: 120,
	        y: 60,
	        width: 430, 
	    	emptyText: '',

		  });

	    NS.rec= new Ext.form.NumberField({
		 	
			id: PF + 'rec',
	        name: PF + 'rec',
	    	value:'',
	        x: 120,
	        y: 60,
	        width: 430, 
	    	emptyText: '',

		  });
	    NS.t= new Ext.form.NumberField({
		 	
			id: PF + 't',
	        name: PF + 't',
	    	value:'',
	        x: 120,
	        y: 60,
	        width: 430, 
	    	emptyText: '',

		  });
		

	

    NS.bandera = new Ext.form.TextField({
		id: PF + 'bandera',
        name: PF + 'bandera',
        x: 120,
        y: 60,
        value:2,
        width: 430, 
    	emptyText: '',

	  }); 
	    
	
    NS.chequera = new Ext.form.TextField({
		id: PF + 'chequera',
        name: PF + 'chequera',
        x: 120,
        y: 60,
        value:2,
        width: 430, 
    	emptyText: '',

	  }); 
	    
	
	
    
	
	 NS.txtFec = new Ext.form.DateField({
         id: PF+'txtFec',
         name: PF+'txtFec',
         format: 'd/m/Y',
         x: 95,
         y: 0,
         disabled :false, 
         width: 150
 });

	
	 NS.txtFecAux = new Ext.form.DateField({
         id: PF+'txtFecAux',
         name: PF+'txtFecAux',
         format: 'd/m/Y',
         x: 95,
         y: 0,
         disabled :false, 
         width: 150
 });
	 
	 
	 
	 
    NS.txtVersion1 = new Ext.form.TextField({
	 	
		id: PF + 'txtVersion',
        name: PF + 'txtVersion',
    	value:'',
        x: 120,
        y: 60,
        width: 430, 
    	emptyText: '',

	  });
	
    
    NS.txtVersion2 = new Ext.form.TextField({
	 	id: PF + 'txtVersion2',
        name: PF + 'txtVersion2',
        x: 120,
        y: 100,
    	value:'',
        width: 430, 
    	emptyText: '',

	  });

    
    NS.txtFec.setValue(NS.sFecHoy);
    
    
	PosicionInversionAction.institucion( function(res){
				if (res != null && res != undefined && res == "") {
				}else{
					Ext.getCmp(PF+'institucion').setValue(res);
				}
			});    
    

	
	
	
	//grid1I
	
	
	  NS.columnaSeleccion1 = new Ext.grid.CheckboxSelectionModel ({
			singleSelect:false
		});
	  
	  

	  
		NS.storeMod1 = new Ext.data.DirectStore({
			paramsAsHash: false,
			baseParams:{	
				fecha :'', 
				caja:0,
				institucion:'',
				empresa :0, 
				divisa:'',
				banco :0, 
				chequera:''	},
		         root: '',
		         paramOrder:['fecha','caja','institucion','empresa','divisa','banco','chequera'],
		         directFn: PosicionInversionAction.llenarGrid1, 
			fields: [
				{name: 'estatus'},
				{name: 'importe'},
				 
			],
			listeners: {
				load: function(s, records) {
					//if(records[6]!='')
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMod1, msg: "Cargando..."});
					if (records.length == null || records.length <= 0){
						//Ext.Msg.alert('SET', 'No existen Datos En la Fecha Seleccionada');
						myMask.hide();
					}else{
						
						var saldoFinal=0;
						for (var i = 0; i < records.length; i++){
							if( records[i].get('estatus')=="Vencidas")  {
								saldoFinal=saldoFinal+records[i].get('importe');	
							}
						}
				
						var e=NS.t.getValue();
		    			Ext.getCmp(PF + 'totalVen').setValue(saldoFinal);
						NS.ven.setValue(parseInt(Ext.getCmp(PF + 'totalVen').getValue()));
					
						NS.t.setValue(e+ NS.ven.setValue());
					
						
    					
					}
					

				}
				
			}
		});
	  

		NS.columnasMod1 = new Ext.grid.ColumnModel([
		    NS.columnaSeleccion1,
		    {header: 'Estatus', width: 120, dataIndex: 'estatus', sortable: true},
		  {header: 'Importe', width: 250, dataIndex: 'importe', sortable: true},
		
		    ]);
	  
		
		
		
		NS.gridMod1 = new Ext.grid.GridPanel({
			store:NS.storeMod1,
			id: 'gridMod1',
			name: 'gridMod1',
			cm: NS.columnasMod1,
			width: 340,
			height: 150,
			 x: 370,
           y: 45,
		    stripeRows: true,
		    columnLines: true,
		    cm:NS.columnasMod1,
		    sm: NS.columnaSeleccion1,
			listeners: {
				click: {
					fn: function(grid,valG){	
				
						var registroSeleccionado = NS.gridMod1.getSelectionModel().getSelections();
						//alert(registroSeleccionado);
						if (registroSeleccionado>=0){	
							Ext.getCmp(PF + 'btnMod').setVisible(true);
							
							//myMask.show(); 
						}
					}
				}
			}
		});
	
	
	//grid1F
	//************************************************************/
	
	
	//grid2I
	

		  NS.columnaSeleccion2 = new Ext.grid.CheckboxSelectionModel ({
				singleSelect:false
			});
		  
		  

		  
			NS.storeMod2 = new Ext.data.DirectStore({
				paramsAsHash: false,
				baseParams:{	
					fecha :'', 
					caja:0,
					institucion:'',
					empresa :0, 
					divisa:'',
					banco :0, 
					chequera:''	},
			         root: '',
			         paramOrder:['fecha','caja','institucion','empresa','divisa','banco','chequera'],
			         directFn: PosicionInversionAction.llenarGrid2, 
				fields: [
					{name: 'hora'},
					{name: 'importe'},
					{name: 'tipoOperacion'},
					{name: 'concepto'}
					 
				],
				listeners: {
					load: function(s, records) {
					//	if(records[6]!='')
						var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMod2, msg: "Cargando..."});
						if (records.length == null || records.length <= 0){
						//	Ext.Msg.alert('SET', 'No existen Datos En la Fecha Seleccionada');
							myMask.hide();
						}else{
							
							var saldoFinal=0;
							for (var i = 0; i < records.length; i++){
								if( records[i].get('tipoOperacion') > 4000 )  {
									saldoFinal=saldoFinal+records[i].get('importe');	
								}
							}
							Ext.getCmp(PF + 'totalApor').setValue(saldoFinal);
							NS.bar.setValue(parseInt(Ext.getCmp(PF + 'totalApor').getValue()));
							
							var ee=NS.t.getValue();
	    					NS.t.setValue(ee+NS.bar.setValue());
	    					
							
						}
				
					
						

					}
					
				}
			});
		  

			NS.columnasMod2 = new Ext.grid.ColumnModel([
			    NS.columnaSeleccion2,
			    {header: 'Hora', width: 100, dataIndex: 'hora', sortable: true},
			  {header: 'Operacion', width: 100, dataIndex: 'tipoOperacion', sortable: true},
			    {header: 'Importe', width: 100, dataIndex: 'importe', sortable: true},
			    {header: 'Concepto', width: 100, dataIndex: 'concepto', sortable: true}
			
			    ]);
		  
			
			
			
			NS.gridMod2 = new Ext.grid.GridPanel({
				store:NS.storeMod2,
				id: 'gridMod2',
				name: 'gridMod2',
				cm: NS.columnasMod2,
				width: 340,
				height: 150,
				 x: 370,
	           y: 215,
			    stripeRows: true,
			    columnLines: true,
			    cm:NS.columnasMod2,
			    sm: NS.columnaSeleccion2,
				listeners: {
					click: {
						fn: function(grid,valG){	
					
							var registroSeleccionado = NS.gridMod2.getSelectionModel().getSelections();
							//alert(registroSeleccionado);
							if (registroSeleccionado>=0){	
								Ext.getCmp(PF + 'btnMod').setVisible(true);
								
								//myMask.show(); 
							}
						}
					}
				}
			});
		
		
		
		
	//grid2F
	
	//************************************************************/
	
	//grid3I
	
			  NS.columnaSeleccion3 = new Ext.grid.CheckboxSelectionModel ({
					singleSelect:false
				});
			  
			  

			  
				NS.storeMod3 = new Ext.data.DirectStore({
					paramsAsHash: false,
					baseParams:{	
						fecha :'', 
						caja:0,
						institucion:'',
						empresa :0, 
						divisa:'',
						banco :0, 
						chequera:''	},
				         root: '',
				         paramOrder:['fecha','caja','institucion','empresa','divisa','banco','chequera'],
				         directFn: PosicionInversionAction.llenarGrid3,
					fields: [
						{name: 'estatus'},
						{name: 'importe'},
						 
					],
					listeners: {
						load: function(s, records) {
							//if(records[6]!='')
							var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMod3, msg: "Cargando..."});
							if (records.length == null || records.length <= 0){
								Ext.Msg.alert('SET', 'No existen Datos ');
								myMask.hide();
							}else{
								
								
								
                  				
							}
							
							
							

						}
						
					}
				});
			  

				NS.columnasMod3 = new Ext.grid.ColumnModel([
				    NS.columnaSeleccion3,
				    {header: 'Estatus', width: 120, dataIndex: 'estatus', sortable: true},
				  {header: 'Importe', width: 250, dataIndex: 'importe', sortable: true},
				
				    ]);
			  
				
				
				
				NS.gridMod3 = new Ext.grid.GridPanel({
					store:NS.storeMod3,
					id: 'gridMod3',
					name: 'gridMod3',
					cm: NS.columnasMod3,
					width: 340,
					height: 150,
					 x: 760,
		           y: 45,
				    stripeRows: true,
				    columnLines: true,
				    cm:NS.columnasMod3,
				    sm: NS.columnaSeleccion3,
					listeners: {
						click: {
							fn: function(grid,valG){	
						
								var registroSeleccionado = NS.gridMod3.getSelectionModel().getSelections();
								//alert(registroSeleccionado);
								if (registroSeleccionado>=0){	
									Ext.getCmp(PF + 'btnMod').setVisible(true);
									
									//myMask.show(); 
								}
							}
						}
					}
				});
			
			
			
	//grid3F
	
	
	//************************************************************/
	
	
	
	//grid4I
	
				  NS.columnaSeleccion4 = new Ext.grid.CheckboxSelectionModel ({
						singleSelect:false
					});
				  
				  

				  
					NS.storeMod4 = new Ext.data.DirectStore({
						paramsAsHash: false,
						baseParams:{	
							fecha :'', 
							caja:0,
							institucion:'',
							empresa :0, 
							divisa:'',
							banco :0, 
							chequera:''	},
					         root: '',
					         paramOrder:['fecha','caja','institucion','empresa','divisa','banco','chequera'],
					         directFn: PosicionInversionAction.llenarGrid4,
						fields: [
							{name: 'estatus'},
							{name: 'concepto'},
							{name: 'importe'},
							 
						],
						listeners: {
							load: function(s, records) {
							//	if(records[6]!='')
								var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMod4, msg: "Cargando..."});
								if (records.length == null || records.length <= 0){
									Ext.Msg.alert('SET', 'No existen Datos En la Fecha Seleccionada');
									myMask.hide();
								}else{
									var total=0;
									
									
									var saldoFinal=0;
									for (var i = 0; i < records.length; i++){
										saldoFinal=saldoFinal+records[i].get('importe');
									}
									
									var e1=NS.t.getValue();
									Ext.getCmp(PF + 'totalInver').setValue(saldoFinal);
									NS.inv.setValue(Ext.getCmp(PF + 'totalInver').getValue());
									NS.t.setValue(e1 - NS.inv.getValue() );
									
							
									Ext.getCmp(PF + 'saldoF').setValue(NS.t.getValue());
									
								
									
									
								}
						

							}
							
						}
					});
				  
					
					
					

					NS.columnasMod4 = new Ext.grid.ColumnModel([
					    NS.columnaSeleccion4,
					    {header: 'Estatus', width: 120, dataIndex: 'estatus', sortable: true},
					  {header: 'Concepto', width: 120, dataIndex: 'concepto', sortable: true},
					  {header: 'Importe', width: 120, dataIndex: 'importe', sortable: true}
					
					    ]);
				  
					
					
					
					NS.gridMod4 = new Ext.grid.GridPanel({
						store:NS.storeMod4,
						id: 'gridMod4',
						name: 'gridMod4',
						cm: NS.columnasMod4,
						width: 340,
						height: 150,
						 x: 760,
			           y: 215,
					    stripeRows: true,
					    columnLines: true,
					    cm:NS.columnasMod4,
					    sm: NS.columnaSeleccion4,
						listeners: {
							click: {
								fn: function(grid,valG){	
							
									var registroSeleccionado = NS.gridMod4.getSelectionModel().getSelections();
									//alert(registroSeleccionado);
									if (registroSeleccionado>=0){	
										//Ext.getCmp(PF + 'btnMod').setVisible(true);
										
										//myMask.show(); 
									}
								}
							}
						}
					});
				
					
					

				
				
	//grid4F
	
	//************************************************************/
	
	NS.storeEmpresa= new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{ bExistentes : true },
		root: '',
		paramOrder:['bExistentes'],
		directFn: PosicionInversionAction.llenarCmbEmpresa, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresa, msg:"Cargando..."});
				

				if(records.length === null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No Existen Empresas concentradoras');
					
					return;
				}
			}
		}
	}); 

	NS.storeEmpresa.load();
	
	  NS.cmbEmpresa = new Ext.form.ComboBox({
		  store:NS.storeEmpresa,
			name: PF + 'cmbEmpresa',
			id: PF + 'cmbEmpresa',	                                          
	   	 	x:120,
	        y: 0,
	        typeAhead: true,
			mode: 'local',
			minChars: 0,
			selecOnFocus: true,
			forceSelection: true,
	        width: 150,
			emptyText: 'Seleccione Empresa',
			valueField:'id',
			displayField:'descripcion',
			mode: 'local',
			autocomplete: true,
			triggerAction: 'all',
			value: '',
			listeners:
			{
				select:
				{
					fn:function(combo, valor)  
					{  
						var sDesc =  valor.get('descripcion');
						
						BFwrk.Util.updateComboToTextField(PF + 'noEmpresa', NS.cmbEmpresa.getId());
					
						var cveEmpresa= Ext.getCmp(PF+'noEmpresa').getValue();
						parseInt(cveEmpresa);
						NS.txtVersion1.setValue(cveEmpresa);
						
						parseInt(NS.txtVersion1.getValue());

						NS.storeDivisa.baseParams.idEmpresa =cveEmpresa;
					 	NS.storeDivisa.load();
					 	
					 	NS.storeBanco.baseParams.idEmp =cveEmpresa;
					 	

						    
					 	
						
					}
				}
			}
		}); 
	
	  


		NS.storeDivisa= new Ext.data.DirectStore({
			paramsAsHash: false,
			baseParams:{ idEmpresa:0 },
			root: '',
			paramOrder:['idEmpresa'],
			directFn: PosicionInversionAction.llenarCmbDivisa, 
			idProperty: 'idStr', 
			fields: [
				 {name: 'idStr'},
				 {name: 'descripcion'}
			],
			listeners: {
				load: function(s, records){
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisa, msg:"Cargando..."});				
					
					
					if(records.length === null || records.length <= 0)
					{
						Ext.Msg.alert('SET','No Existen Divisas');
						
						return;
					}
				}
			}
		}); 

		
		  NS.cmbDivisa = new Ext.form.ComboBox({
			  store:NS.storeDivisa,
				name: PF + 'cmbDivisa',
				id: PF + 'cmbDivisa',	                                          
		   	 	x:120,
		        y: 30,
		        typeAhead: true,
				mode: 'local',
				minChars: 0,
				selecOnFocus: true,
				forceSelection: true,
		        width: 150,
				emptyText: 'Seleccione Divisa',
				valueField:'idStr',
				displayField:'descripcion',
				mode: 'local',
				autocomplete: true,
				triggerAction: 'all',
				value: '',
				listeners:
				{
					select:
					{
						fn:function(combo, valor)  
						{  
					
							BFwrk.Util.updateComboToTextField(PF + 'divisa', NS.cmbDivisa.getId());
							var divisaa= Ext.getCmp(PF+'divisa').getValue();
						
							NS.storeBanco.baseParams.idDivisa =divisaa;
						 	NS.storeBanco.load();
						 	NS.txtVersion2.setValue(divisaa);
						 	
						 	

						}
					}
				}
			}); 
		

		  
		  

			NS.storeBanco= new Ext.data.DirectStore({
				paramsAsHash: false,
				baseParams:{ idEmp: 0, idDivisa:'' },
				root: '',
				paramOrder:['idEmp','idDivisa'],
				directFn: PosicionInversionAction.llenarCmbBanco, 
				idProperty: 'id', 
				fields: [
					 {name: 'id'},
					 {name: 'descripcion'}
				],
				listeners: {
					load: function(s, records){
						var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBanco, msg:"Cargando..."});
						
						if(records.length === null || records.length <= 0)
						{
							Ext.Msg.alert('SET','No Existen Banco');
							
							return;
						}
					}
				}
			}); 
			
			  NS.cmbBanco = new Ext.form.ComboBox({
				  store:NS.storeBanco,
					name: PF + 'cmbBanco',
					id: PF + 'cmbBanco',	                                          
			   	 	x:120,
			        y: 60,
			        typeAhead: true,
					mode: 'local',
					minChars: 0,
					selecOnFocus: true,
					forceSelection: true,
			        width: 150,
					emptyText: 'Seleccione Banco',
					valueField:'id',
					displayField:'descripcion',
					mode: 'local',
					autocomplete: true,
					triggerAction: 'all',
					value: '',
					listeners:
					{
						select:
						{
							fn:function(combo, valor)  
							{  
								var sDesc =  valor.get('descripcion');
								BFwrk.Util.updateComboToTextField(PF + 'banco', NS.cmbBanco.getId());

								var ban= Ext.getCmp(PF+'banco').getValue();
							    parseInt(ban);	
						
							
						    	var a=NS.txtVersion2.getValue();
							    NS.storeChequera.baseParams.idEmpresa=parseInt(NS.txtVersion1.getValue());
							 	NS.storeChequera.baseParams.idDivisa=a;
							    NS.storeChequera.baseParams.idBanco=parseInt(ban);
							    NS.storeChequera.load();							 
							    
						
								
		 	
							}
						}
					}
				}); 
			

			  
			  
			  
			  

				NS.storeChequera= new Ext.data.DirectStore({
					paramsAsHash: false,
					baseParams:{ idEmpresa: 0, idDivisa:'', idBanco:0 },
					root: '',
					paramOrder:['idEmpresa','idDivisa','idBanco'],
					directFn: PosicionInversionAction.llenarCmbChequera, 
					idProperty: 'id', 
					fields: [
						 {name: 'id'},
						 {name: 'descripcion'}
					],
					listeners: {
						load: function(s, records){
							var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequera, msg:"Cargando..."});
							
							if(records.length === null || records.length <= 0)
							{
								Ext.Msg.alert('SET','No Existen Banco');
								
								return;
							}
						}
					}
				}); 
				

			
				
				
				  NS.cmbChequera = new Ext.form.ComboBox({
					  store: NS.storeChequera,
						name: PF + 'cmbChequera',
						id: PF + 'cmbChequera',	                                          
				   	 	x:55,
				        y: 90,
				        typeAhead: true,
						mode: 'local',
						minChars: 0,
						selecOnFocus: true,
						forceSelection: true,
				        width: 210,
						emptyText: 'Seleccione chequera',
						valueField:'id',
						displayField:'descripcion',
						mode: 'local',
						autocomplete: true,
						triggerAction: 'all',
						value: '',
						listeners:
						{
							select:
							{
								fn:function(combo, valor)  
								{  
									var sDesc =  valor.get('descripcion');
								NS.chequera.setValue(sDesc);
								}
							}
						}
					}); 

			  
	  
	  
	
	  
/*Panel Vista */
NS.posInv = new Ext.form.FormPanel({
title: 'Posicion de inversion',
width: 1150,
height: 900,
padding: 10,
layout: 'absolute',
id: PF + 'posInv',
name: PF + 'posInv',
renderTo: NS.tabContId,
frame: true,
autoScroll: true,
   items : [
		{
            xtype: 'label',
             text: 'Fecha de inversion:',
            x: 1,
            y: 0
        },
        
        NS.txtFec,
        
        {   xtype: 'checkbox',
		id: PF + 'diasA',
		name: PF + 'diasA', 
		x: 275,
      y: 0,
		boxLabel: 'Dias Anteriores',
		
		listeners: {
     		check: {
     			fn:function(opt, valor) {
     				if(valor == true) {
     					var a=1;
     					parseInt(a)
     					NS.bandera.setValue(a);
     					

     				}else {
     					var b=2;
     					parseInt(b)
     					NS.bandera.setValue(b);
     					
     				}
     			}
     		}
     	}
	}, 
	
	{
        xtype: 'label',
         text: 'Institucion:',
        x: 440,
        y: 5
    },
	{
		xtype: 'textfield',
		x: 500,
		y: 0,
		width: 170,
		disabled:true,
		name: PF + 'institucion',
		id: PF + 'institucion',
		listeners: 
		{
			change:
			{
				fn: function(box, value)
				{
					//var idCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa',NS.cmbEmpresa.getId());
					//if(idCombo !== null && idCombo !== '')
				   // NS.accionarEmpresa(idCombo);
				}
			}
		}
	},
	
	{
        xtype: 'label',
         text: 'Vencimientos',
        x: 385,
        y: 25
       },
       NS.gridMod1,

   	{
           xtype: 'label',
            text: 'Barridos / Fondeos',
           x: 385,
           y: 195
          },
          
         NS.gridMod2,
          
      	{
              xtype: 'label',
               text: 'Falta Por Invertir',
              x: 760,
              y: 25
             },
             
             NS.gridMod3,
             
         	{
                 xtype: 'label',
                  text: 'Inversiones',
                 x: 760,
                 y: 195
                },
                
                
                NS.gridMod4,
                
                
                {
                    xtype: 'button',
                    text: '..::Buscar',
                    x: 600,
                    y:385,
                    width: 70,
                    id: PF + 'btnBuscar',
                    name: PF + 'btnBuscar',
                    disabled: false,
                    listeners:
                    {
                    	click:
                    	{
                    		fn: function(btn)
                    		{
                    			
                    			
                    			var  banco1  =Ext.getCmp(PF+'banco').getValue();
                    			parseInt(banco1);
                    			var chequera1=NS.chequera.getValue();
                    			PosicionInversionAction.saldoInicial(parseInt(banco1),chequera1,function(res){
                    				if (res != null && res != undefined && res == "") {
                    				}else{
                    				//	alert(res);
                    					
                    					Ext.getCmp(PF+'saldoInicial').setValue(res);
                    					NS.inicial.setValue(parseInt(res));
                    					NS.t.setValue(NS.inicial.setValue());
                    					//alert(NS.t.getValue());

                    					
            							
                    				}
                    			});    

                    			
       							var caja=NS.bandera.getValue();
                				parseInt(caja);
                				var institucion=Ext.getCmp(PF+'institucion').getValue();
                				var empresa  =Ext.getCmp(PF+'noEmpresa').getValue();
                				parseInt(empresa);
                				var divisa=Ext.getCmp(PF+'divisa').getValue();
                				var  banco  =Ext.getCmp(PF+'banco').getValue();
                				parseInt(banco);
                				var chequera=NS.chequera.getValue();
                    		     var fech=NS.txtFec.getValue();
                    			PosicionInversionAction.fecha(fech,function(res){
                    				if (res != null && res != undefined && res == "") {
                    				}else{
                    					
                    					if(  (res > NS.sFecHoy)  && (!res == NS.sFecHoy) ){
                    						Ext.Msg.alert('SET', 'Las fecha debe ser menor a la de hoy');
                            			}else{
                            				if(NS.bandera.getValue()==1){
                    	              		
                            					
                            					Ext.getCmp(PF+'txtFec').setDisabled(true);
                    							 
                    							 NS.storeMod1.baseParams.fecha=res;
			                    				 NS.storeMod1.baseParams.caja=parseInt(caja);
			                    				 NS.storeMod1.baseParams.institucion=institucion;
			                    				 NS.storeMod1.baseParams.empresa=parseInt(empresa);
			                    				 NS.storeMod1.baseParams.divisa=divisa;
			                    				 NS.storeMod1.baseParams.banco=parseInt(banco);
			                    				 NS.storeMod1.baseParams.chequera=chequera;
			                    		
			                    				 
			                    				 var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMod1, msg:"Buscando..."});
	                    						 NS.storeMod1.load();
            							
			                    				 
			                    				 NS.storeMod2.baseParams.fecha=res;
			                    				 NS.storeMod2.baseParams.caja=parseInt(caja);
			                    				 NS.storeMod2.baseParams.institucion=institucion;
			                    				 NS.storeMod2.baseParams.empresa=parseInt(empresa);
			                    				 NS.storeMod2.baseParams.divisa=divisa;
			                    				 NS.storeMod2.baseParams.banco=parseInt(banco);
			                    				 NS.storeMod2.baseParams.chequera=chequera;
			                    		
			                    		 
	                    						 var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMod2, msg:"Buscando..."});
	                    						 NS.storeMod2.load();
            							
			                    				 
			                    				 
			                    				 NS.storeMod3.baseParams.fecha=res;
			                    				 NS.storeMod3.baseParams.caja=parseInt(caja);
			                    				 NS.storeMod3.baseParams.institucion=institucion;
			                    				 NS.storeMod3.baseParams.empresa=parseInt(empresa);
			                    				 NS.storeMod3.baseParams.divisa=divisa;
			                    				 NS.storeMod3.baseParams.banco=parseInt(banco);
			                    				 NS.storeMod3.baseParams.chequera=chequera;
			                    		 
	                    						 var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMod3, msg:"Buscando..."});
	                    						 NS.storeMod3.load();
            							
	                    							Ext.getCmp(PF + 'totalRecu').setValue(0);
	                    	              			NS.rec.setValue(Ext.getCmp(PF + 'totalRecu').getValue());
			                    				 
			                    				 NS.storeMod4.baseParams.fecha=res;
			                    				 NS.storeMod4.baseParams.caja=parseInt(caja);
			                    				 NS.storeMod4.baseParams.institucion=institucion;
			                    				 NS.storeMod4.baseParams.empresa=parseInt(empresa);
			                    				 NS.storeMod4.baseParams.divisa=divisa;
			                    				 NS.storeMod4.baseParams.banco=parseInt(banco);
			                    				 NS.storeMod4.baseParams.chequera=chequera;
			                    				
			                    				 var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMod4, msg:"Buscando..."});
			                    						 NS.storeMod4.load();
                    						}else{

                    							
                    							 NS.storeMod1.baseParams.fecha=res;
			                    				 NS.storeMod1.baseParams.caja=parseInt(caja);
			                    				 NS.storeMod1.baseParams.institucion=institucion;
			                    				 NS.storeMod1.baseParams.empresa=parseInt(empresa);
			                    				 NS.storeMod1.baseParams.divisa=divisa;
			                    				 NS.storeMod1.baseParams.banco=parseInt(banco);
			                    				 NS.storeMod1.baseParams.chequera=chequera;
			                    		
			                    				 
			                    				 var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMod1, msg:"Buscando..."});
	                    						 NS.storeMod1.load();
            							
			                    				 
			                    				 NS.storeMod2.baseParams.fecha=res;
			                    				 NS.storeMod2.baseParams.caja=parseInt(caja);
			                    				 NS.storeMod2.baseParams.institucion=institucion;
			                    				 NS.storeMod2.baseParams.empresa=parseInt(empresa);
			                    				 NS.storeMod2.baseParams.divisa=divisa;
			                    				 NS.storeMod2.baseParams.banco=parseInt(banco);
			                    				 NS.storeMod2.baseParams.chequera=chequera;
			                    		
			                    		 
	                    						 var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMod2, msg:"Buscando..."});
	                    						 NS.storeMod2.load();
            							
			                    				 
			                    				 
			                    				 NS.storeMod3.baseParams.fecha=res;
			                    				 NS.storeMod3.baseParams.caja=parseInt(caja);
			                    				 NS.storeMod3.baseParams.institucion=institucion;
			                    				 NS.storeMod3.baseParams.empresa=parseInt(empresa);
			                    				 NS.storeMod3.baseParams.divisa=divisa;
			                    				 NS.storeMod3.baseParams.banco=parseInt(banco);
			                    				 NS.storeMod3.baseParams.chequera=chequera;
			                    		 
	                    						 var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMod3, msg:"Buscando..."});
	                    						 NS.storeMod3.load();
	                    						 
	                    						 Ext.getCmp(PF + 'totalRecu').setValue(0);
	                    	              			NS.rec.setValue(Ext.getCmp(PF + 'totalRecu').getValue());
			                    				 
			                    				 NS.storeMod4.baseParams.fecha=res;
			                    				 NS.storeMod4.baseParams.caja=parseInt(caja);
			                    				 NS.storeMod4.baseParams.institucion=institucion;
			                    				 NS.storeMod4.baseParams.empresa=parseInt(empresa);
			                    				 NS.storeMod4.baseParams.divisa=divisa;
			                    				 NS.storeMod4.baseParams.banco=parseInt(banco);
			                    				 NS.storeMod4.baseParams.chequera=chequera;
			                    				
			                    				 var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMod4, msg:"Buscando..."});
			                    						 NS.storeMod4.load();
                    							

			                    							
		                            					
		             						}
                            			
                            			} 
                    				}
                   				 
                    				}); 
                                
                                

                    		}
                    	}
                    }
                },
                
                {
                    xtype: 'button',
                    text: '..::Limpiar',
                    x: 700,
                    y:385,
                    width: 70,
                    id: PF + 'btnLimpiar',
                    name: PF + 'btnLimpiar',
                    disabled: false,
                    listeners:
                    {
                    	click:
                    	{
                    		fn: function(btn)
                    		{
                    			
                    			 NS.storeMod1.removeAll();
                    			 NS.storeMod2.removeAll();
                    			 NS.storeMod3.removeAll();
                    			 NS.storeMod4.removeAll();
                    			 
                    			 
                    			 Ext.getCmp(PF+'noEmpresa').setValue("");
                    			 Ext.getCmp(PF+'cmbEmpresa').setValue("");
                    			 
                    			 Ext.getCmp(PF+'divisa').setValue("");
                    			 Ext.getCmp(PF+'cmbDivisa').setValue("");
                    			 
                    			 Ext.getCmp(PF+'banco').setValue("");
                    			 Ext.getCmp(PF+'cmbBanco').setValue("");

                    			 Ext.getCmp(PF+'cmbChequera').setValue("");

                    			 
                    			 Ext.getCmp(PF+'saldoInicial').setValue("");
                    			 Ext.getCmp(PF+'totalVen').setValue("");
                    			 Ext.getCmp(PF+'totalApor').setValue("");
                    			 Ext.getCmp(PF+'totalRecu').setValue("");
                    			 Ext.getCmp(PF+'totalInver').setValue("");
                    			 Ext.getCmp(PF+'saldoF').setValue("");

                    			 Ext.getCmp(PF+'institucion').setValue("");
                    			 Ext.getCmp(PF+'txtFec').setDisabled(false);
                    			//alert("Limpiar");
                    		}
                    	}
                    }
                },
	
	
	
//gridsEnLaVista	
	{ 
        xtype: 'fieldset',
        title: 'caja1',
        width: 310,
        height: 150,
        x: 25,
        y: 30,
        layout: 'absolute',
         id: 'fSetAbajo',
        items: [
        	//////1
            {
                xtype: 'label',
                 text: 'Empresa:',
                x: 0,
                y: 0
               },
               {
                    xtype: 'numberfield',
                    x: 55,
                    y: 0,
                    width: 50,
                    disabled :false, 
                    name: PF + 'noEmpresa',
                    id: PF + 'noEmpresa',
                    listeners:
                    	
                    {
                    	change: 
                    	{
                    		fn: function(box, value)
                    		{
                    			
                    		
                     		 	
								
                    		}
                    	}
                    }
                 },
                 NS.cmbEmpresa,
                 
                 ////2
                 
                 
                 {
                     xtype: 'label',
                      text: 'Divisa:',
                     x: 0,
                     y: 30
                    },
                    {
                		xtype: 'textfield',
                		x: 55,
                		y: 30,
                		width: 50,
                		name: PF + 'divisa',
                		id: PF + 'divisa',
                		listeners: 
                		{
                			change:
                			{
                				fn: function(box, value)
                				{
                					//var idCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa',NS.cmbEmpresa.getId());
                					//if(idCombo !== null && idCombo !== '')
                				   // NS.accionarEmpresa(idCombo);
                				}
                			}
                		}
                	},
                	NS.cmbDivisa,
                	         	
            /////////3
                	
                	
                	{
                        xtype: 'label',
                         text: 'Banco:',
                        x: 0,
                        y: 60
                       },
                       {
                   		xtype: 'textfield',
                   		x: 55,
                   		y: 60,
                   		width: 50,
                   		name: PF + 'banco',
                   		id: PF + 'banco',
                   		listeners: 
                   		{
                   			change:
                   			{
                   				fn: function(box, value)
                   				{
                   					//var idCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa',NS.cmbEmpresa.getId());
                   					//if(idCombo !== null && idCombo !== '')
                   				   // NS.accionarEmpresa(idCombo);
                   				}
                   			}
                   		}
                   	},
                   	NS.cmbBanco,
        	
        	/////4
                   	
                   	{
                        xtype: 'label',
                         text: 'Chequera:',
                        x: 0,
                        y: 90
                       },
                  NS.cmbChequera,
        	
        ]
     },
     
     
     { 
         xtype: 'fieldset',
         title: 'caja2',
         width: 310,
         height: 235,
         x: 25,
         y: 190,
         layout: 'absolute',
          id: 'fSetAbajo2',
         items: [
        	 
        	 
        		{
                    xtype: 'label',
                     text: 'Saldo Inicial:',
                    x: 35,
                    y: 0
                   },
                   
                   {
                       xtype: 'numberfield',
                       x: 105,
                       y: 0,
                       width: 160,
                       disabled :false, 
                       name: PF + 'saldoInicial',
                       id: PF + 'saldoInicial',
                       listeners:
                       	
                       {
                       	change: 
                       	{
                       		fn: function(box, value)
                       		{
                       			
                       		
                        		 	
   								
                       		}
                       	}
                       }
                    },
                    
                   
                   
                   {
                       xtype: 'label',
                        text: '+ Total Vencimientos:',
                       x: 0,
                       y: 30
                      },
                      
                      
                      {
                          xtype: 'numberfield',
                          x: 105,
                          y: 30,
                          width: 160,
                          disabled :false, 
                          name: PF + 'totalVen',
                          id: PF + 'totalVen',
                          listeners:
                          	
                          {
                          	change: 
                          	{
                          		fn: function(box, value)
                          		{
                          			
                          		
                           		 	
      								
                          		}
                          	}
                          }
                       },
                      
                      
                      
                  		{
                          xtype: 'label',
                           text: '+ Total Aportacion:',
                          x: 0,
                          y: 60
                         },
                         
                         
                         {
                             xtype: 'numberfield',
                             x: 105,
                             y: 60,
                             width: 160,
                             disabled :false, 
                             name: PF + 'totalApor',
                             id: PF + 'totalApor',
                             listeners:
                             	
                             {
                             	change: 
                             	{
                             		fn: function(box, value)
                             		{
                             			
                             		
                              		 	
         								
                             		}
                             	}
                             }
                          },
                         
                         
                     		{
                             xtype: 'label',
                              text: '- Total Recuperacion:',
                             x: 0,
                             y: 90
                            },
                            
                            {
                                xtype: 'numberfield',
                                x: 105,
                                y: 90,
                                width: 160,
                                disabled :false, 
                                name: PF + 'totalRecu',
                                id: PF + 'totalRecu',
                                listeners:
                                	
                                {
                                	change: 
                                	{
                                		fn: function(box, value)
                                		{
                                			
                                		
                                 		 	
            								
                                		}
                                	}
                                }
                             },
                        		{
                                xtype: 'label',
                                 text: '- Total Inverciones:',
                                x: 0,
                                y: 120
                               },
                               
                               
                               {
                                   xtype: 'numberfield',
                                   x: 105,
                                   y: 120,
                                   width: 160,
                                   disabled :false, 
                                   name: PF + 'totalInver',
                                   id: PF + 'totalInver',
                                   listeners:
                                   	
                                   {
                                   	change: 
                                   	{
                                   		fn: function(box, value)
                                   		{
                                   			
                                   		
                                    		 	
               								
                                   		}
                                   	}
                                   }
                                },
                               
                           		{
                                   xtype: 'label',
                                    text: '_______________________________________________________',
                                   x: 0,
                                   y: 135
                                  },
                                  
                                  
                              		{
                                      xtype: 'label',
                                       text: 'Saldo Final:',
                                      x: 35,
                                      y: 165
                                     },
                                     {
                                         xtype: 'numberfield',
                                         x: 105,
                                         y: 165,
                                         width: 160,
                                         disabled :false, 
                                         name: PF + 'saldoF',
                                         id: PF + 'saldoF',
                                         listeners:
                                         	
                                         {
                                         	change: 
                                         	{
                                         		fn: function(box, value)
                                         		{
                                         			
                                         		
                                          		 	
                     								
                                         		}
                                         	}
                                         }
                                      },
        	 
        	 
        	 
         ]
      },
	   
	   
	   
	   
   ]
	   
	

   
});

NS.posInv.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());



});