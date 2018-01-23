//@autor: Jessica Arelly Cruz Cruz
//@since: 05/01/2011
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.Traspasos.SolicitudTraspasos.Solicitud');
	NS.tabContId = apps.SET.tabContainerId;	// esta linea sirve para probar la pantalla con la aplicacion completa
	//NS.tabContId = 'contenedorTraspasos'; //esta linea sirve para probar la pantalla individual
	var PF = apps.SET.tabID+'.';
	NS.entreCuentas = true;
	NS.barrido = false;
	NS.interEmpresas = false;
	NS.bandera = false;
	NS.fechaAnterior = false;
	NS.bRegreso = false;
	NS.psTipoChequera = '';
	NS.psTipoChequera2 = '';
	NS.flagInvDe = false;
	NS.psDivisa = '';
	NS.noEmp = 0;
	NS.noBan = 0;
	NS.noChe = '';
	NS.ubicacion = 'DESC';
	NS.desc_usuario = apps.SET.NOMBRE+' '+apps.SET.APELLIDO_PATERNO+' '+apps.SET.APELLIDO_MATERNO;
	NS.GS_DESC_EMPRESA = apps.SET.NOM_EMPRESA;
	NS.GI_USUARIO = apps.SET.iUserId ;
	NS.GI_ID_CAJA = apps.SET.ID_CAJA;
	NS.fechaHoy = apps.SET.FEC_HOY,
	NS.empresaDC = '';
	NS.empresaAC = '';
	NS.empresaDI = '';
	NS.empresaAI = '';
	NS.psConcepto = '';
	NS.psGrupo = 0;
	
	//******************************************************************
	//           AGREGADO DE CONTROLES DE GRUPO Y RUBRO
	//******************************************************************
	
	//CONTROLES GRUPO INGRESOS
	NS.storeGrupoIngreso = new Ext.data.DirectStore( {
		paramOrder:['idTipoMovto'],
		paramsAsHash: false,
		baseParams:{
		idTipoMovto:'CC'			
		},		
		root: '',
		directFn: TraspasosAction.llenarComboGrupo,
		idProperty: 'idGrupo',  		
		fields: [
			{name: 'idGrupo' },
			{name: 'descGrupo'}
		]	
	}); 
	NS.storeGrupoIngreso.load();
	
	NS.lblGrupoIngreso = new Ext.form.Label({                
		text: 'Grupo:',
		x: 10,
		y: 0,
		width: 120
	});
	
	NS.cmbGrupoIngreso = new Ext.form.ComboBox({
		 store: NS.storeGrupoIngreso	
		,name: PF+'cmbGrupoIngreso'
		,id: PF+'cmbGrupoIngreso'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,hidden: false
		,x :60
		,y :15
		,width :240
		,valueField:'idGrupo'
		,displayField:'descGrupo'
		,autocomplete: true
		,emptyText: 'Seleccione el grupo ingreso'
		,triggerAction: 'all'
		,value: ''			
       ,listeners:{
				select:{
					    	fn:function() {
		
								
									
									BFwrk.Util.updateComboToTextField(PF + 'txtIdGrupoIngreso', NS.cmbGrupoIngreso.getId());																	
									Ext.getCmp(NS.txtIdRubroIngreso.getId()).setValue('');
									Ext.getCmp(NS.cmbRubroIngreso.getId()).setValue('');
									
									if(Ext.getCmp(PF + 'txtnoEmpresaAinter').getValue() == '') {
										Ext.Msg.alert('SET', 'Seleccione primero la empresa a la que va a realizar el traspaso');
										Ext.getCmp(NS.txtIdGrupoIngreso.getId()).setValue('');
										Ext.getCmp(NS.cmbGrupoIngreso.getId()).setValue('');
										return;
									}
									NS.storeRubroIngreso.removeAll();
									NS.storeRubroIngreso.baseParams.idGrupo = parseInt(NS.txtIdGrupoIngreso.getValue());
									NS.storeRubroIngreso.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtnoEmpresaAinter').getValue());
									NS.storeRubroIngreso.load();
									
									
					    	}//end function
					    							    	
						},//end select
				change:{
					    	fn:function(combo, newValue, oldValue) {						    		
					    		  
								 if( newValue == '' ){		
		              					              			
									Ext.getCmp( PF + 'txtIdGrupoIngreso' ).setValue('');
		              				Ext.getCmp(NS.txtIdRubroIngreso.getId()).setValue('');
									Ext.getCmp(NS.cmbRubroIngreso.getId()).setValue('');
									NS.storeRubroIngreso.removeAll();       				
		              				         				
		              			}else{
		              				
		              				Ext.getCmp( PF + 'txtIdRubroIngreso' ).forceSelection= true; 
		              				
		              			}	
		              															
					    	}//end function
					    	
						   }//end change

	}//end listeners

});
	
	NS.txtIdGrupoIngreso = new Ext.form.NumberField({
        name  : PF + 'txtIdGrupoIngreso',
		id    : PF + 'txtIdGrupoIngreso',    
        x     : 10,
        y     : 15,
        width : 50,
        listeners:{
        change:{
               		fn:function(){
               		
               			if( Ext.getCmp( PF + 'txtIdGrupoIngreso' ).getValue() !== "" ){
               			
              				var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdGrupoIngreso', NS.cmbGrupoIngreso.getId());
              				PF + 'txtIdGrupoIngreso'.focus();
              				
              			}else{
              						              			
              				Ext.getCmp(NS.cmbGrupoIngreso.getId()).setValue('');
              				Ext.getCmp(NS.txtIdGrupoIngreso.getId()).setValue('');
							Ext.getCmp(NS.cmbGrupoIngreso.getId()).setValue('');
							       							              				
              			
              			}//End if
              			
                    }//End fn
                    
               }//End change
               
      }//End listeners

    });
	
	//CONTROLES GRUPO INGRESO
	
	//CONTROLES RUBRO INGRESO
	
	NS.storeRubroIngreso = new Ext.data.DirectStore( {	
	root: '',
	paramOrder:['idGrupo', 'noEmpresa'],	            
		paramsAsHash: false,
		directFn: TraspasosAction.llenarComboRubros,
		idProperty: 'idRubro',  		
		fields: [
			{name: 'idRubro' },
			{name: 'descRubro'}
		]	
	});
		
	NS.lblRubroIngreso = new Ext.form.Label({                
		text: 'Rubro:',
		x: 10,
		y: 37,
		width: 120
	});
	
	NS.txtIdRubroIngreso = new Ext.form.NumberField({
        name  : PF + 'txtIdRubroIngreso',
		id    : PF + 'txtIdRubroIngreso',    
        x     : 10,
        y     : 53,
        width : 50        
        ,listeners:{
	    	change:{
	   			fn:function(){
	   		
	   				if( Ext.getCmp( PF + 'txtIdRubroIngreso' ).getValue() !== "" ){
	   			
	   					var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdRubroIngreso', NS.cmbRubroIngreso.getId());
	  					
	  				
	   				}else{
	  						              			
	  					Ext.getCmp(NS.cmbRubroIngreso.getId()).setValue('');
	  					Ext.getCmp(NS.txtIdRubroIngreso.getId()).setValue('');
					
	  				}//End if
	  			
	        	}//End fn
	        
			}//End change
	
		}//End listeners

    });
	
	
	NS.cmbRubroIngreso = new Ext.form.ComboBox({
		 store: NS.storeRubroIngreso	
		,name: PF+'cmbRubroIngreso'
		,id: PF+'cmbRubroIngreso'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
       ,hidden: false
		,x :60
		,y :53
		,width :240
		,valueField:'idRubro'
		,displayField:'descRubro'
		,autocomplete: true
		,emptyText: 'Seleccione el rubro ingreso'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				    	fn:function() {
								
								
								BFwrk.Util.updateComboToTextField(PF+'txtIdRubroIngreso', NS.cmbRubroIngreso.getId());
								
				    	}//end function
				    							    	
					},//end select
			change:{
				    	fn:function(combo, newValue, oldValue) {						    		
			    		  
							if( newValue == '' ){		
	             					              			
								//Ext.getCmp( PF + 'txtIdRubroIngreso' ).setValue('');
	             				         				
	             			}else{
	             				
	             				//Ext.getCmp( PF + 'txtIdRubroIngreso' ).forceSelection= true; 
	             				
	             			}	
             															
			    		}//end function

				    	
					   }//end change
	
			}//end listeners

			});
	
	//CONTROLES RUBRO INGRESO
			
	
	
	
	
	//-----CONTROLES DE EGRESOS-------------
	
	NS.storeGrupoEgreso = new Ext.data.DirectStore( {
		paramOrder:['idTipoMovto'],
		paramsAsHash: false,
		baseParams:{
		idTipoMovto:'CE'			
		},		
		root: '',
		directFn: TraspasosAction.llenarComboGrupo,
		idProperty: 'idGrupo',  		
		fields: [
			{name: 'idGrupo' },
			{name: 'descGrupo'}
		]	
	}); 
	
	NS.storeGrupoEgreso.load();
	
	NS.lblGrupoEgreso = new Ext.form.Label({                
		text: 'Grupo:',
		x: 10,
		y: 0,
		width: 120
	});

	
	NS.txtIdGrupoEgreso = new Ext.form.NumberField({
        name  : PF + 'txtIdGrupoEgreso',
		id    : PF + 'txtIdGrupoEgreso',    
        x     : 10,
        y     : 15,
        width : 50,
        listeners:{
        change:{
               		fn:function(){
               		
						
               			if( Ext.getCmp( PF + 'txtIdGrupoEgreso' ).getValue() !== '' ){
               				  
              				var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdGrupoEgreso', NS.cmbGrupoEgreso.getId());
              				
              				
              			}else{
              					              			
              				Ext.getCmp(NS.cmbGrupoEgreso.getId()).setValue('');
              				Ext.getCmp(NS.txtIdGrupoEgreso.getId()).setValue('');
              				
              				Ext.getCmp(NS.cmbRubroEgreso.getId()).setValue('');
              				Ext.getCmp(NS.txtIdRubroEgreso.getId()).setValue('');
              			
              			}//End if
              			
                    }//End fn
                    
               }//End change
               
      }//End listeners
    });
	
	NS.txtIdGrupoEgreso = new Ext.form.NumberField({
        name  : PF + 'txtIdGrupoEgreso',
		id    : PF + 'txtIdGrupoEgreso',    
        x     : 10,
        y     : 15,
        width : 50,
        listeners:{
        	change:{
               		fn:function(){
               		
               			if( Ext.getCmp( PF + 'txtIdGrupoEgreso' ).getValue() !== "" ){
               			
              				var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdGrupoEgreso', NS.cmbGrupoEgreso.getId());
              				PF + 'txtIdGrupoEgreso'.focus();
              				
              			}else{
              						              			
              				Ext.getCmp(NS.cmbRubroEgreso.getId()).setValue('');
              				Ext.getCmp(NS.txtIdRubroEgreso.getId()).setValue('');
              				
              				Ext.getCmp(NS.cmbGrupoEgreso.getId()).setValue('');
              				Ext.getCmp(NS.txtIdGrupoEgreso.getId()).setValue('');
              				              			
              			}//End if
              			
                    }//End fn
                    
               }//End change
               
      }//End listeners

    });


	
	NS.cmbGrupoEgreso = new Ext.form.ComboBox({
		 store: NS.storeGrupoEgreso	
		,name: PF+'cmbGrupoEgreso'
		,id: PF+'cmbGrupoEgreso'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,hidden: false
		,x :60
		,y :15
		,width :240
		,valueField:'idGrupo'
		,displayField:'descGrupo'
		,autocomplete: true
		,emptyText: 'Seleccione el grupo egreso'
		,triggerAction: 'all'
		,value: ''			
       ,listeners:{
				select:{
					    	fn:function() {
									BFwrk.Util.updateComboToTextField(PF + 'txtIdGrupoEgreso', NS.cmbGrupoEgreso.getId());																	
									Ext.getCmp(NS.txtIdRubroEgreso.getId()).setValue('');
									Ext.getCmp(NS.cmbRubroEgreso.getId()).setValue('');
									
									if(Ext.getCmp(PF + 'txtnoEmpresaDeinter').getValue() == '') {
										Ext.Msg.alert('SET', 'Seleccione primero la empresa de la que va a realizar el traspaso');
										Ext.getCmp(NS.txtIdGrupoEgreso.getId()).setValue('');
										Ext.getCmp(NS.cmbGrupoEgreso.getId()).setValue('');
										return;
									}
									NS.storeRubroEgreso.removeAll();
									NS.storeRubroEgreso.baseParams.idGrupo = parseInt(NS.txtIdGrupoEgreso.getValue());
									NS.storeRubroEgreso.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtnoEmpresaDeinter').getValue());
									NS.storeRubroEgreso.load();
									
									
					    	}//end function
					    							    	
						},//end select
				change:{
					    	fn:function(combo, newValue, oldValue) {						    		
					    		  
								 if( newValue == '' ){		
		              					              			
									Ext.getCmp( PF + 'txtIdGrupoEgreso' ).setValue('');
		              				Ext.getCmp(NS.txtIdRubroEgreso.getId()).setValue('');
									Ext.getCmp(NS.cmbRubroEgreso.getId()).setValue('');
									NS.storeRubroEgreso.removeAll();       				
		              				         				
		              			}else{
		              				
		              				Ext.getCmp( PF + 'txtIdRubroEgreso' ).forceSelection= true; 
		              				
		              			}	
		              															
					    	}//end function
					    	
						   }//end change

	}//end listeners

});
	
	//CONTROLES RUBRO INGRESO
	
	NS.storeRubroEgreso = new Ext.data.DirectStore( {	
		root: '',
		paramOrder:['idGrupo', 'noEmpresa'],
		paramsAsHash: false,
		directFn: TraspasosAction.llenarComboRubros,
		idProperty: 'idRubro',  		
		fields: [
			{name: 'idRubro' },
			{name: 'descRubro'}
		]	
	});
		
	NS.lblRubroEgreso = new Ext.form.Label({                
		text: 'Rubro:',
		x: 10,
		y: 37,
		width: 120
	});
	
	NS.txtIdRubroEgreso = new Ext.form.NumberField({
        name  : PF + 'txtIdRubroEgreso',
		id    : PF + 'txtIdRubroEgreso',    
        x     : 10,
        y     : 53,
        width : 50,
        listeners:{
    	change:{
           		fn:function(){
           		
           			if( Ext.getCmp( PF + 'txtIdRubroEgreso' ).getValue() !== "" ){
           			
          				var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtIdRubroEgreso', NS.cmbRubroEgreso.getId());
          				PF + 'txtIdRubroEgreso'.focus();
          				
          			}else{
          						              			
          				Ext.getCmp(NS.cmbRubroEgreso.getId()).setValue('');
          				Ext.getCmp(NS.txtIdRubroEgreso.getId()).setValue('');
						
          			}//End if
          			
                }//End fn
                
           }//End change
           
  }//End listeners

    });
	
	
	NS.cmbRubroEgreso = new Ext.form.ComboBox({
		 store: NS.storeRubroEgreso	
		,name: PF+'cmbRubroEgreso'
		,id: PF+'cmbRubroEgreso'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
       ,hidden: false
		,x :60
		,y :53
		,width :240
		,valueField:'idRubro'
		,displayField:'descRubro'
		,autocomplete: true
		,emptyText: 'Seleccione el rubro egreso'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
		select:{
			    	fn:function() {
							BFwrk.Util.updateComboToTextField(PF + 'txtIdRubroEgreso', NS.cmbRubroEgreso.getId());																	
							
			    	}//end function
			    							    	
				},//end select
		change:{
			    	fn:function(combo, newValue, oldValue) {						    		
			    		  
						 if( newValue == '' ){		
              					              			
							Ext.getCmp( PF + 'txtIdRubroEgreso' ).setValue('');
              				         				
              			}else{
              				
              				Ext.getCmp( PF + 'txtIdRubroEgreso' ).forceSelection= true; 
              				
              			}	
              															
			    	}//end function
			    	
				   }//end change

}//end listeners

			});
	
	//CONTROLES RUBRO EGRESOS

	
	NS.frmSetGrupoRubroEgreso = new Ext.form.FieldSet({	
		title: 'Clasifica Egreso',
        x: 680,
        y: 5,
        width: 330,
        height: 110,
        layout: 'absolute',
        id: PF+'frmSetGrupoRubroEgreso',
        disabled: true,
        items:[
               	NS.lblGrupoEgreso,
               	NS.txtIdGrupoEgreso,
               	NS.cmbGrupoEgreso,
               	NS.lblRubroEgreso,
               	NS.txtIdRubroEgreso,
               	NS.cmbRubroEgreso
               ]		
	});
	
	//???????FRAME CATALOGA INGRESO
	NS.frmSetGrupoRubroIngreso = new Ext.form.FieldSet({	
		title: 'Clasifica Ingreso',
        x: 680,
        y: 120,
        width: 330,
        height: 110,
        layout: 'absolute',
        id: PF+'frmSetGrupoRubroIngreso',        
        disabled: true,
        items: [
            
            	NS.lblGrupoIngreso,
            	NS.txtIdGrupoIngreso,
            	NS.cmbGrupoIngreso,
            	NS.lblRubroIngreso,
            	NS.txtIdRubroIngreso,
            	NS.cmbRubroIngreso
            ]
		
	});
	//???????FRAME CATALOGA INGRESO


	
	
	//******************************************************************
	//           AGREGADO DE CONTROLES DE GRUPO Y RUBRO
	//******************************************************************
	
	
	
	/**llena el combo de conceptos*/
	NS.storeConcepto = new Ext.data.DirectStore( {
		paramsAsHash: false,
		root: '',
		directFn: TraspasosAction.llenarComboConcepto,
		idProperty: 'idConcepto',  		
		fields: [
			{name: 'idConcepto' },
			{name: 'descConcepto'}
		],
		listeners: {
			load: function(s, records){
			
			}
		}
	}); 
	NS.storeConcepto.load();
	
	//combo fisico de conceptos
	NS.cmbConcepto = new Ext.form.ComboBox({
		 store: NS.storeConcepto 	
		,name: PF+'cmbConcepto'
		,id: PF+'cmbConcepto'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,hidden: true
		,x :110
		,y :20
		,width :190
		,valueField:'idConcepto'
		,displayField:'descConcepto'
		,autocomplete: true
		,emptyText: 'Seleccione el concepto'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{		
				fn:function(combo, value) {
						NS.psConcepto = NS.storeConcepto.getById(combo.getValue()).get('descConcepto');
						}
					}	
				}
			});
	
	
	/**llena el combo empresa*/
	NS.storeEmpresaDecta = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			usuario: NS.GI_USUARIO
		},
		root: '',
		paramOrder:['usuario'],
		directFn: TraspasosAction.obtenerEmpresas,
		idProperty: 'noEmpresa',  		
		fields: [
			{name: 'noEmpresa' },
			{name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records){
				
			}
		}
	}); 
	NS.storeEmpresaDecta.load();
	
	NS.accionarCmbEmpresaDecta = function(comboValue) {
		NS.noEmp = comboValue;
		Ext.getCmp(PF+'txtnoEmpresaActa').reset();
		Ext.getCmp(PF + 'txtEmpresaActa').reset();
		Ext.getCmp(PF+'cmbBancoDecta').setValue('');
		NS.storeBancoDecta.removeAll();
		Ext.getCmp(PF+'cmbBancoActa').setValue('');
		NS.storeBancoActa.removeAll();
		Ext.getCmp(PF+'cmbChequeraDecta').setValue('');
		NS.storeChequeraDecta.removeAll();
		Ext.getCmp(PF+'txtnoBancoDecta').setValue('');
		Ext.getCmp(PF+'txtSaldoiniDecta').setValue('');
		Ext.getCmp(PF+'txtSaldofinDecta').setValue('');
		Ext.getCmp(PF+'txtnoBancoActa').setValue('');
		Ext.getCmp(PF+'cmbChequeraActa').setValue('');
		NS.storeChequeraActa.removeAll();
		Ext.getCmp(PF+'txtSaldoiniActa').setValue('');
		Ext.getCmp(PF+'txtSaldofinActa').setValue('');
		
		if(comboValue != 0) {
			Ext.getCmp(PF+'txtnoEmpresaActa').setValue(NS.noEmp);
			NS.empresaDC = NS.storeEmpresaDecta.getById(comboValue).get('nomEmpresa');
			Ext.getCmp(PF + 'txtEmpresaActa').setValue(NS.empresaDC);
			
			NS.storeBancoDecta.baseParams.empresa = NS.noEmp;
			NS.storeBancoDecta.baseParams.bandera = NS.bandera;
			NS.storeBancoDecta.load();
		}
	}
	
	//combo fisico empresa
	NS.cmbEmpresaDecta = new Ext.form.ComboBox({
		 store: NS.storeEmpresaDecta 	
		,name: PF+'cmbEmpresaDecta'
		,id: PF+'cmbEmpresaDecta'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x: 120
		,y: 10
		,width: 180
		,valueField:'noEmpresa'
		,displayField:'nomEmpresa'
		,autocomplete: true
		,autoSelect: true
		,emptyText: 'Seleccione la Empresa'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{		
				fn:function(combo, value) {
					BFwrk.Util.updateComboToTextField(PF+'txtnoEmpresaDecta',NS.cmbEmpresaDecta.getId());					
					NS.accionarCmbEmpresaDecta(combo.getValue());
				}
			}	
		}
	});
	
	
	/**llena el combo bancos*/
	NS.storeBancoDecta = new Ext.data.DirectStore( {
		paramsAsHash: false,
		paramOrder:['empresa', 'bandera'],
		root: '',
		directFn: TraspasosAction.obtenerBanco1,
		idProperty: 'idBanco',  		
		fields: [
			{name: 'idBanco' },
			{name: 'descBanco'}
		],
		listeners: {
			load: function(s, records) {
		
				//alert(records.length);
				
				if(records.length == null || records.length <= 0)
					Ext.MessageBox.alert("SET","No hay bancos disponibles");
				
				if(records.length == 1) {
			    	Ext.getCmp(PF+'cmbBancoDecta').setValue(records[0].get('idBanco'));
			    	Ext.getCmp(PF+'txtnoBancoDecta').setValue(records[0].get('idBanco'));
			    	
			    	
			    	
			    	NS.storeChequeraDecta.baseParams.banco = records[0].get('idBanco');
					NS.storeChequeraDecta.baseParams.empresa = NS.noEmp;
					NS.storeChequeraDecta.baseParams.bandera = NS.bandera;
					NS.storeChequeraDecta.load();
		    	}
			}
		}
	});
	
	NS.accionarCmbBancoDecta = function(comboValue)	{
		var pi_no_intentos = 0;
		var ll_empresa;
		NS.noEmp = parseInt(Ext.getCmp(PF+'txtnoEmpresaDecta').getValue());
		ll_empresa = NS.noEmp;
		NS.noBan = comboValue;
		Ext.getCmp(PF+'txtnoBancoDecta').setValue(NS.noBan);
		Ext.getCmp(PF+'cmbChequeraDecta').setValue('');
		NS.storeChequeraDecta.removeAll();
		Ext.getCmp(PF+'txtSaldoiniDecta').setValue('');
		Ext.getCmp(PF+'txtSaldofinDecta').setValue('');
		
		if(comboValue != 0) {
		    NS.storeChequeraDecta.baseParams.banco = NS.noBan;
			NS.storeChequeraDecta.baseParams.empresa = NS.noEmp;
			NS.storeChequeraDecta.baseParams.bandera = NS.bandera;
			NS.storeChequeraDecta.load();
		}
	}
	
	//combo fisico bancos
	NS.cmbBancoDecta = new Ext.form.ComboBox({
		 store: NS.storeBancoDecta 	
		,name: PF+'cmbBancoDecta'
		,id: PF+'cmbBancoDecta'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :120
		,y :50
		,width :180
		,valueField:'idBanco'
		,displayField:'descBanco'
		,autocomplete: true
		,emptyText: 'Banco'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{		
				fn:function(combo, value) {
					BFwrk.Util.updateComboToTextField(PF+'txtnoBancoDecta',NS.cmbBancoDecta.getId());
					NS.accionarCmbBancoDecta(combo.getValue());
				}
			}	
		}
	});

	/**llena el combo bancos*/
	NS.storeBancoActa = new Ext.data.DirectStore( {
		paramsAsHash: false,
		paramOrder:['empresa', 'divisa', 'bandera', 'tipo'],
		root: '',
		directFn: TraspasosAction.obtenerBancoA,
		idProperty: 'idBanco',  		
		fields: [
			{name: 'idBanco' },
			{name: 'descBanco'}
		],
		listeners: {
			load: function(s, records){
				if(records.length == null || records.length <= 0){
					Ext.MessageBox.alert("SET"," No hay bancos disponibles para esta empresa");
					Ext.getCmp(PF+'cmbChequeraActa').setValue('');
					Ext.getCmp(PF+'txtSaldoiniActa').setValue('');
					Ext.getCmp(PF+'txtSaldofinActa').setValue('');
				}
				if(records.length == 1)//banco
				{
					Ext.getCmp(PF+'txtnoBancoActa').setValue(records[0].get('idBanco'));
					Ext.getCmp(PF+'cmbBancoActa').setValue(records[0].get('idBanco'));
				    NS.accionarCmbBancoActa(records[0].get('idBanco'));
				}
			}
		}
	}); 
	
	NS.accionarCmbBancoActa = function(valueCombo) {
			//NS.noEmp = Ext.getCmp(PF+'cmbEmpresaDecta').getValue();
			NS.noEmp = parseInt(Ext.getCmp(PF+'txtnoEmpresaDecta').getValue());
			//NS.noBan = Ext.getCmp(PF+'cmbBancoActa').getValue();
			NS.noBan = valueCombo;
			Ext.getCmp(PF+'txtnoBancoActa').setValue(NS.noBan);
			NS.piNoIntentos = 0;
		    NS.plEmpresa = 0;
		    NS.psDivisa = '';
		    NS.psCheqExcluye = '';
		    NS.arriba();

//			Valida si el banco es el mismo de los dos lados, la chequera seleccionada no se incluye en las cuentas del banco
//			hacia donde va el traspaso
			if(parseInt(Ext.getCmp(PF+'txtnoBancoDecta').getValue()) == parseInt(Ext.getCmp(PF+'txtnoBancoActa').getValue()))
	        {
	            NS.psCheqExcluye = Ext.getCmp(PF+'cmbChequeraDecta').getValue();
	        }
	        else{
	            NS.psCheqExcluye = '';
	        }
	        Ext.getCmp(PF+'cmbChequeraActa').setValue('');
			Ext.getCmp(PF+'txtSaldoiniActa').setValue('');
			Ext.getCmp(PF+'txtSaldofinActa').setValue('');
	       	NS.storeChequeraActa.baseParams.empresa = NS.noEmp;
	       	NS.storeChequeraActa.baseParams.banco = NS.noBan;
	       	NS.storeChequeraActa.baseParams.bandera = NS.bandera;
	       	NS.storeChequeraActa.baseParams.cheqEx = NS.psCheqExcluye;
	       	
	       	if(Ext.getCmp(PF+'txtSaldoiniDecta').getValue() == 0.00 || Ext.getCmp(PF+'txtSaldofinDecta').getValue() <= 0.00)
			{
				//Ext.MessageBox.alert('SET','No hay saldo suficiente para el traspaso');
				Ext.getCmp(PF+'txtSaldoiniActa').setValue(NS.formatNumber(0));
				Ext.getCmp(PF+'txtSaldofinActa').setValue(NS.formatNumber(0));
				//return;
				NS.storeChequeraActa.load();
			}
			else
			{
	       		NS.storeChequeraActa.load();
	       	}
	};
	
	//combo fisico bancos
	NS.cmbBancoActa = new Ext.form.ComboBox({
		 store: NS.storeBancoActa 	
		,name: PF+'cmbBancoActa'
		,id: PF+'cmbBancoActa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :120
		,y :50
		,width :180
		,valueField:'idBanco'
		,displayField:'descBanco'
		,autocomplete: true
		,emptyText: 'Banco'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{		
				fn: function(combo, valor)
				{
					BFwrk.Util.updateComboToTextField(PF+'txtnoBancoActa',NS.cmbBancoActa.getId());
					NS.accionarCmbBancoActa(combo.getValue());
				}
			}	
		}
	});
		
	
	/**llena el combo chequera*/
	NS.storeChequeraDecta = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams:{
			empresa:0,
			banco:0,
			bandera:false
		},
		paramOrder:['empresa','banco','bandera'],
		root: '',
		directFn: TraspasosAction.obtenerChequera1,
		idProperty: 'idChequera',  //1	
		fields: [
			{name: 'idChequera'}
		],
		listeners: {
			load: function(s, records){
			if(records.length == null || records.length <= 0){
				Ext.MessageBox.alert("SET"," No hay chequeras disponibles");
				return;
				}
			if(records.length === 1)
			    {
			    	Ext.getCmp(PF+'cmbChequeraDecta').setValue(records[0].get('idChequera'));
			    	NS.accionarCmbChequeraDecta(records[0].get('idChequera'));
		    	}
			}
		}
	}); 
	
	NS.accionarCmbChequeraDecta = function(comboValor) {
			if(Ext.getCmp(PF+'txtMonto').getValue() === '' || Ext.getCmp(PF+'txtMonto').getValue() <= 0)
		       	{
		            Ext.Msg.alert('SET','Escriba el monto a traspasar!');
		            Ext.getCmp(PF+'txtMonto').focus(true);
		            Ext.getCmp(PF+'cmbChequeraDecta').setValue('');
		            return;
		        }
				NS.psDivisa = '';
				NS.plEmpresa = 0;
				//NS.noEmp = Ext.getCmp(PF+'cmbEmpresaDecta').getValue();
				NS.noEmp = parseInt(Ext.getCmp(PF+'txtnoEmpresaDecta').getValue());
				NS.noBan = parseInt(Ext.getCmp(PF+'txtnoBancoDecta').getValue());
				//NS.noBan = Ext.getCmp(PF+'cmbBancoDecta').getValue();
				NS.monto = NS.unformatNumber(Ext.getCmp(PF+'txtMonto').getValue());
				Ext.getCmp(PF+'txtMonto').setReadOnly;
				//NS.noChe = Ext.getCmp(PF+'cmbChequeraDecta').getValue();
				NS.noChe = comboValor;
				NS.plEmpresa = NS.noEmp;
				Ext.getCmp(PF+'txtSaldoiniDecta').setValue('');
				Ext.getCmp(PF+'txtSaldofinDecta').setValue('');
				
				
				//alert(cambiarFecha(''+Ext.getCmp(PF+'txtFecha').getValue()) + '---' + NS.fechaHoy);
				if(cambiarFecha(''+Ext.getCmp(PF+'txtFecha').getValue()) < NS.fechaHoy)
				{
					NS.fechaAnterior = true;
				}
				else
				{
					NS.fechaAnterior = false;
				}
				NS.cmbChequeraDeValidate();    
				var reg = {};
				var matriz = new Array();
				reg.empresa = NS.noEmp;
				reg.banco = NS.noBan;
				reg.chequera = NS.noChe;
				reg.fecha = NS.fechaHoy;
				reg.pbInversion = NS.bandera;
				reg.bAnterior = NS.fechaAnterior;
				matriz[0] = reg;
				var jsonString = Ext.util.JSON.encode(matriz);
				TraspasosAction.obtenerSaldoFinal(jsonString, function(result, e)
				{	
					if(result !== null && result !== '' && result > 0)
					{												//esta funcion obtiene el saldo con 2 decimales y le pone el formato numerico
						Ext.getCmp(PF+'txtSaldoiniDecta').setValue(NS.formatNumber(Math.round((result) * 100) / 100));
						Ext.getCmp(PF+'txtSaldofinDecta').setValue(NS.formatNumber(Math.round((result - NS.monto) * 100) / 100));
					}
					else if(result <= 0)
					{
						Ext.MessageBox.alert("SET"," No hay saldo suficiente para el traspaso");
						//Ext.getCmp(PF+'txtSaldoiniDecta').setValue(NS.formatNumber(0));
						//Ext.getCmp(PF+'txtSaldofinDecta').setValue(NS.formatNumber(0));
						Ext.getCmp(PF+'txtSaldoiniDecta').setValue(NS.formatNumber(Math.round((result) * 100) / 100));
						Ext.getCmp(PF+'txtSaldofinDecta').setValue(NS.formatNumber(Math.round((result - NS.monto) * 100) / 100));
						//return;
					}
					if(result > 0 && (result - NS.monto) < 0)
					{
						Ext.MessageBox.alert("SET","No tiene saldo suficiente en la chequera");
						return;
					}
				});
				NS.cmbChequeraDeValidate();
	};	

	//combo fisico chequera
	NS.cmbChequeraDecta = new Ext.form.ComboBox({
		 store: NS.storeChequeraDecta 	
		,name: PF+'cmbChequeraDecta'
		,id: PF+'cmbChequeraDecta'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :120
		,y :90
		,width :180
		,valueField:'idChequera'
		,displayField:'idChequera'
		,autocomplete: true
		,emptyText: 'Chequera'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{		
				fn: function(combo, valor)
				{
					NS.accionarCmbChequeraDecta(combo.getValue());
				}
			}	
		}
	});
	
		/**llena el combo chequera*/
	NS.storeChequeraActa = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams:{
			empresa:0,
			banco:0,
			bandera:false
		},
		paramOrder:['empresa','banco','bandera', 'cheqEx'],
		root: '',
		directFn: TraspasosAction.obtenerChequeraA,
		idProperty: 'idChequera',  //2		
		fields: [
			{name: 'idChequera'}
		],
		listeners: {
			load: function(s, records){
				if(records.length == null || records.length <= 0){
					Ext.MessageBox.alert("SET"," No hay chequeras disponibles");
					Ext.getCmp(PF+'txtSaldoiniActa').setValue('');
		            Ext.getCmp(PF+'txtSaldofinActa').setValue('');
					return;
				}
				if(records.length === 1)
			    {
			    	Ext.getCmp(PF+'cmbChequeraActa').setValue(records[0].get('idChequera'));
			    	NS.llenarSaldosActa();
		    	}
			}
		}
	}); 
	
	NS.llenarSaldosActa = function(combo, value) {
		NS.noEmp = Ext.getCmp(PF+'cmbEmpresaDecta').getValue();
		NS.noBan = parseInt(Ext.getCmp(PF+'txtnoBancoActa').getValue());
		//NS.noBan = Ext.getCmp(PF+'cmbBancoActa').getValue();
		NS.noChe = Ext.getCmp(PF+'cmbChequeraActa').getValue();
	    NS.plEmpresa = 0;
	    NS.plBanco = 0;
	    NS.lchequera = '';
		Ext.getCmp(PF+'txtSaldoiniActa').setValue('');
		Ext.getCmp(PF+'txtSaldofinActa').setValue('');
        if(Ext.getCmp(PF+'cmbEmpresaDecta').getValue() == Ext.getCmp(PF+'txtnoEmpresaActa').getValue()){
            if(Ext.getCmp(PF+'cmbChequeraDecta').getValue() == Ext.getCmp(PF+'cmbChequeraActa').getValue()){
                Ext.MessageBox.alert("SET"," No puede transferir a la misma chequera! ");
                Ext.getCmp(PF+'cmbChequeraActa').setValue('');
                Ext.getCmp(PF+'txtSaldoiniActa').setValue('');
                Ext.getCmp(PF+'txtSaldofinActa').setValue('');
                return;
            }
        }
        Ext.getCmp(PF+'cmdEjecutar').setDisabled(false);
	    TraspasosAction.verificarDivisa(NS.noBan, NS.noChe, NS.bandera, function(resultado,e)
		{	
	        if(NS.bandera){
	        	if(resultado[0] == undefined)
	        		NS.psTipoChequera= '';
		        else if(resultado!== null && resultado !== '' && resultado !== undefined){
        			NS.psTipoChequera = resultado[0].tipo;
        			} 
	            if(NS.psTipoChequera === 'I'){
//				Cuando la chequera es de inversion en los 2 lados del traspaso el concepto cambia al siguiente
	                if(NS.flagInvDe){
	                    Ext.getCmp(PF+'txtConcepto').setValue('TRASPASO DE INVERSION');
	                    }
	                else{
	                    Ext.getCmp(PF+'txtConcepto').setValue('INCREMENTO DE INVERSION');
	                	}
	               			                
	                TraspasosAction.obtenerDatosReferencia(NS.noEmp, NS.noBan, NS.noChe, function(result,e)
                    {
                    	if(result !== null && result !== '' && result !== undefined){
	                    	Ext.getCmp(PF+'txtRef').setValue(result[0].referencia);
	                        Ext.getCmp(PF+'txtInst').setValue(result[0].razonSocial);
                        	}
                        else{
	                        Ext.getCmp(PF+'txtRef').setValue('');
	                        Ext.getCmp(PF+'txtInst').setValue('');
                    		}
                    });
	                
	            }
	            else{
	                if(NS.flagInvDe){
	                    Ext.getCmp(PF+'txtConcepto').setValue('RECUPERACION DE INVERSION');
	                }
	            }
	        }
	    
	    	NS.monto = 0;
	    	NS.monto = NS.unformatNumber(Ext.getCmp(PF+'txtMonto').getValue());
			Ext.getCmp(PF+'txtMonto').setReadOnly;
	    	var reg1 = {};
			var matriz1 = new Array();
			reg1.empresa = NS.noEmp;
			reg1.banco = NS.noBan;
			reg1.chequera = NS.noChe;
			reg1.fecha = NS.fechaHoy;
			reg1.pbInversion = NS.bandera;
			reg1.bAnterior = NS.fechaAnterior;
			matriz1[0] = reg1;
			var jsonString1 = Ext.util.JSON.encode(matriz1);
			TraspasosAction.obtenerSaldoFinal(jsonString1, function(result, e)
			{	
				if(result !== null && result !== '')
				{
					Ext.getCmp(PF+'txtSaldoiniActa').setValue(NS.formatNumber(Math.round((result) * 100) / 100));
					Ext.getCmp(PF+'txtSaldofinActa').setValue(NS.formatNumber(Math.round((result + parseFloat(NS.monto)) * 100) / 100));
				}
				else
				{
					Ext.getCmp(PF+'txtSaldoiniActa').setValue(NS.formatNumber(0));
					Ext.getCmp(PF+'txtSaldofinActa').setValue(NS.formatNumber(0));
				}
			});
		});
	};
	
	//combo fisico chequera A
	NS.cmbChequeraActa = new Ext.form.ComboBox({
		 store: NS.storeChequeraActa 	
		,name: PF+'cmbChequeraActa'
		,id: PF+'cmbChequeraActa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :120
		,y :90
		,width :180
		,valueField:'idChequera'
		,displayField:'idChequera'
		,autocomplete: true
		,emptyText: 'Chequera'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{		
				fn: NS.llenarSaldosActa 
			}	
		}
	});
	
	
	//*********************combos interempresas**********************************
	/**llena el combo interempresa*/
	NS.storeEmpresaDeInter = new Ext.data.DirectStore( {
		paramsAsHash: false,
		root: '',
		directFn: TraspasosAction.llenarComboInterEmpresas,
		idProperty: 'noEmpresa',  		
		fields: [
			{name: 'noEmpresa' },
			{name: 'razonSocial'}
		],
		listeners: {
			load: function(s, records){
				
			}
		}
	}); 
	NS.storeEmpresaDeInter.load();
	
	NS.accionarCmbEmpresaDeinter = function(comboValor) {
		NS.noEmp = comboValor;
		Ext.getCmp(PF+'cmbBancoDeinter').setValue('');
		NS.storeBancoDeinter.removeAll();
		Ext.getCmp(PF+'cmbChequeraDeinter').setValue('');
		NS.storeChequeraDeinter.removeAll();
		Ext.getCmp(PF+'txtnoBancoDeinter').setValue('');	
		Ext.getCmp(PF+'txtSaldoiniDeinter').setValue('');
		Ext.getCmp(PF+'txtSaldofinDeinter').setValue(''); 
		Ext.getCmp(PF+'cmbEmpresaAinter').setValue('');  
		//Ext.getCmp(PF+'txtnoEmpresaAinter').setValue(''); 
		Ext.getCmp(PF+'txtnoBancoAinter').setValue('');  
		Ext.getCmp(PF+'cmbBancoAinter').setValue('');
		NS.storeBancoAinter.removeAll();
		Ext.getCmp(PF+'cmbChequeraAinter').setValue('');
		NS.storeChequeraAinter.removeAll();
		Ext.getCmp(PF+'txtSaldoiniAinter').setValue('');
		Ext.getCmp(PF+'txtSaldofinAinter').setValue('');
		Ext.getCmp(PF+'txtnoEmpresaDeinter').reset();
		Ext.getCmp(PF+'txtnoEmpresaAinter').reset();
		Ext.getCmp(PF+'txtClabe').reset();
		
		if(comboValor != 0) {
			Ext.getCmp(PF+'txtnoEmpresaDeinter').setValue(NS.noEmp);
			NS.empresaDI = NS.storeEmpresaDeInter.getById(comboValor).get('razonSocial');
			NS.storeBancoDeinter.baseParams.empresa = NS.noEmp;
			NS.storeBancoDeinter.load();
			
			NS.storeEmpresaAinter.baseParams.empresa = NS.noEmp;
			NS.storeEmpresaAinter.load();
		}
	}
	
	//combo fisico interempresa2
	NS.cmbEmpresaDeinter = new Ext.form.ComboBox({
		 store: NS.storeEmpresaDeInter 	
		,name: PF+'cmbEmpresaDeinter'
		,id: PF+'cmbEmpresaDeinter'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :120
		,y :10
		,width :180
		,valueField:'noEmpresa'
		,displayField:'razonSocial'
		,autocomplete: true
		,emptyText: 'Seleccione la Empresa'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{		
				fn:function(combo, value) {
					BFwrk.Util.updateComboToTextField(PF+'txtnoEmpresaAinter',NS.cmbEmpresaDeinter.getId());
					
					NS.accionarCmbEmpresaDeinter(combo.getValue());
				}
			}	
		}
	});
				
				
	/**llena el combo interempresa2*/
	NS.storeEmpresaAinter = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			empresa: ''
		},
		root: '',
		paramOrder:['empresa'],
		directFn: TraspasosAction.llenarComboInterEmpresas2,
		idProperty: 'noEmpresa',  		
		fields: [
			{name: 'noEmpresa' },
			{name: 'razonSocial'}
		],
		listeners: {
			load: function(s, records){
			
			}
		}
	}); 
	NS.storeEmpresaAinter.load();
	
	NS.accionarCmbEmpresaAinter = function(comboValor)
	{
		NS.noEmp = comboValor;
		NS.empresaAI = NS.storeEmpresaAinter.getById(comboValor).get('razonSocial');
		//Ext.getCmp(PF+'txtnoEmpresaAinter').setValue(NS.noEmp); 
		var psDivisa = '';
	    var psTipoChequera = '';
	    
	    if(Ext.getCmp(PF+'cmbChequeraDeinter').getValue() === '')
	    {
	        Ext.getCmp(PF+'cmbEmpresaAinter').setValue('');
	        Ext.getCmp(PF+'txtnoEmpresaAinter').setValue(''); 
	        return;
	    }
	   
	    NS.arriba();
	    Ext.getCmp(PF+'cmbBancoAinter').setValue('');
	    Ext.getCmp(PF+'cmbChequeraAinter').setValue('');
	    Ext.getCmp(PF+'txtnoBancoAinter').setValue(''); 
	     
        NS.storeBancoAinter.baseParams.empresa2 = parseInt(Ext.getCmp(PF+'txtnoEmpresaAinter').getValue());
        NS.storeBancoAinter.baseParams.bandera = NS.bandera;
        NS.storeBancoAinter.baseParams.bancoDe = parseInt(Ext.getCmp(PF+'txtnoBancoDeinter').getValue());
        NS.storeBancoAinter.baseParams.chequeraDe = Ext.getCmp(PF+'cmbChequeraDeinter').getValue();
        NS.storeBancoAinter.load();
        
        var records = NS.storeBancoAinter.data.items;
	    if(records.length == 1)
	    {
	    	Ext.getCmp(PF+'txtnoBancoAinter').setValue(records[0].get('idBanco')); 
	    	Ext.getCmp(PF+'cmbBancoAinter').setValue(records[0].get('idBanco'));
	    }	
	    else 
	    {
	    	 Ext.getCmp(PF+'txtSaldoiniAinter').setValue('');
	    	 Ext.getCmp(PF+'txtSaldofinAinter').setValue('');
        }
       var empresa = parseInt(Ext.getCmp(PF+'txtnoEmpresaDeinter').getValue()); 
       NS.bConcentraA = '';
       NS.bConcentraDe = '';
       TraspasosAction.consultarConcentradora(empresa, function(result, e)
       {
	       	if(result !== null && result !== '')
	       	{
	       		NS.bConcentraA = result;
	       	}
	       	var empresa2 = comboValor;
	       	TraspasosAction.consultarConcentradora(empresa2, function(resultado, e)
	        {
	        	if(resultado !== null && resultado !== '')
	        	{
	        		NS.bConcentraDe = resultado;
	        	}
	        	if(NS.bConcentraA !== 'S' && NS.bConcentraDe !== 'S')
	        	{
	        		Ext.getCmp(PF+'txtConcepto').setValue('PRESTAMO');
	        	}
	        	else if(NS.bConcentraA === 'S' && NS.bConcentraDe !== 'S')
	        	{
	        		Ext.getCmp(PF+'txtConcepto').setValue('TRASPASO');
	        	}
	        	else if(NS.bConcentraA !== 'S' && NS.bConcentraDe === 'S')
	        	{
	        		Ext.getCmp(PF+'txtConcepto').setValue('INVERSION');
	        	}
	       	});
       });
	}
	
	//combo fisico interempresa1
	NS.cmbEmpresaAinter = new Ext.form.ComboBox({
		 store: NS.storeEmpresaAinter 	
		,name: PF+'cmbEmpresaAinter'
		,id: PF+'cmbEmpresaAinter'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :120
		,y :10
		,width :180
		,valueField:'noEmpresa'
		,displayField:'razonSocial'
		,autocomplete: true
		,emptyText: 'Seleccione la Empresa'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{		
				fn:function(combo, value) {
					BFwrk.Util.updateComboToTextField(PF+'txtnoEmpresaAinter',NS.cmbEmpresaAinter.getId());
					NS.accionarCmbEmpresaAinter(combo.getValue());
				}
			}	
		}
	});
				
	/**llena el combo bancos inter*/
	NS.storeBancoDeinter = new Ext.data.DirectStore( {
		paramsAsHash: false,
		paramOrder:['empresa'],
		root: '',
		directFn: TraspasosAction.llenarComboBancoInter,
		idProperty: 'idBancoI',  		
		fields: [
			{name: 'idBanco' },
			{name: 'descBanco'}
		],
		listeners: {
			load: function(s, records){
			if(records.length == null || records.length <= 0)
				Ext.MessageBox.alert("SET"," No hay bancos disponibles");
				
			if(records.length === 1)
		    {
		    	Ext.getCmp(PF+'cmbBancoDeinter').setValue(records[0].get('idBanco'));
		    	Ext.getCmp(PF+'txtnoBancoDeinter').setValue(records[0].get('idBanco'));
		    	NS.storeChequeraDeinter.baseParams.banco = records[0].get('idBanco');
				NS.storeChequeraDeinter.baseParams.empresa = NS.noEmp;
				NS.storeChequeraDeinter.baseParams.bandera = NS.bandera;
				
				NS.storeChequeraDeinter.load();
	    	}
			}
		}
	}); 
	
	NS.accionarCmbBancoDeinter = function(comboValor)
	{
		var pi_no_intentos = 0;
		var ll_empresa;
		NS.noEmp = parseInt(Ext.getCmp(PF+'txtnoEmpresaDeinter').getValue());
		ll_empresa = NS.noEmp;
		NS.noBan = comboValor;
		//Ext.getCmp(PF+'txtnoBancoDeinter').setValue(NS.noBan);
		Ext.getCmp(PF+'cmbChequeraDeinter').setValue('');
		Ext.getCmp(PF+'txtSaldoiniDeinter').setValue('');
		Ext.getCmp(PF+'txtSaldofinDeinter').setValue('');
		
	    NS.storeChequeraDeinter.baseParams.banco = NS.noBan;
		NS.storeChequeraDeinter.baseParams.empresa = NS.noEmp;
		NS.storeChequeraDeinter.baseParams.bandera = NS.bandera;
		
		NS.storeChequeraDeinter.load();
	}
	
	//combo fisico bancos inter
	NS.cmbBancoDeinter = new Ext.form.ComboBox({
		 store: NS.storeBancoDeinter 	
		,name: PF+'cmbBancoDeinter'
		,id: PF+'cmbBancoDeinter'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :120
		,y :50
		,width :180
		,valueField:'idBanco'
		,displayField:'descBanco'
		,autocomplete: true
		,emptyText: 'Banco'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{		
				fn:function(combo, value) {
					BFwrk.Util.updateComboToTextField(PF+'txtnoBancoDeinter',NS.cmbBancoDeinter.getId());
					NS.accionarCmbBancoDeinter(combo.getValue());
				}
			}	
		}
	});
	
	/**llena el combo chequera inter*/
	NS.storeChequeraDeinter = new Ext.data.DirectStore( {
		paramsAsHash: false,
		paramOrder:['empresa','banco','bandera'],
		root: '',
		directFn: TraspasosAction.obtenerChequera1,
		idProperty: 'idChequeraI',  //3		
		fields: [
			{name: 'idChequera'}
		],
		listeners: {
			load: function(s, records){
			if(records.length == null || records.length <= 0){
				Ext.MessageBox.alert("SET"," No hay chequeras disponibles");
				return;
				}
			if(records.length === 1)
			    {
			    	Ext.getCmp(PF+'cmbChequeraDeinter').setValue(records[0].get('idChequera'));
			    	NS.llenarSaldosinter();
		    	}
			}
		}
	}); 
	
	NS.llenarSaldosinter = function(combo, value) {
		if(Ext.getCmp(PF+'txtMonto').getValue() === ''|| Ext.getCmp(PF+'txtMonto').getValue() <= 0)
	       	{
	            Ext.Msg.alert('SET','Escriba el monto a traspasar!');
	            Ext.getCmp(PF+'txtMonto').focus(true);
	            Ext.getCmp(PF+'cmbChequeraDeinter').setValue('');
	            return;
	        } 
		NS.psDivisa = '';
		NS.plEmpresa = 0;
		NS.noEmp = parseInt(Ext.getCmp(PF+'txtnoEmpresaDeinter').getValue());
		NS.noBan = parseInt(Ext.getCmp(PF+'txtnoBancoDeinter').getValue());
		NS.monto = NS.unformatNumber(Ext.getCmp(PF+'txtMonto').getValue());
		Ext.getCmp(PF+'txtMonto').setReadOnly;
		NS.noChe = Ext.getCmp(PF+'cmbChequeraDeinter').getValue();
		NS.plEmpresa = NS.noEmp;
		Ext.getCmp(PF+'txtSaldoiniDecta').setValue('');
		Ext.getCmp(PF+'txtSaldofinDecta').setValue('');
		Ext.getCmp(PF+'txtClabe').setValue('')
//		alert(cambiarFecha(''+Ext.getCmp(PF+'txtFecha').getValue()) + '---' + NS.fechaHoy);
//		if(cambiarFecha(''+Ext.getCmp(PF+'txtFecha').getValue()) < NS.fechaHoy)
//		{	
//			NS.fechaAnterior = true;
//		}
//		else
//		{
//			NS.fechaAnterior = false;
//		}
		
		var reg = {};
		var matriz = new Array();
		reg.empresa = NS.noEmp;
		reg.banco = NS.noBan;
		reg.chequera = NS.noChe;
		reg.fecha = NS.fechaHoy;
		reg.pbInversion = NS.bandera;
		reg.bAnterior = NS.fechaAnterior;
		matriz[0] = reg;
		var jsonString = Ext.util.JSON.encode(matriz);
		TraspasosAction.obtenerSaldoFinal(jsonString, function(result, e)
		{	
			
			if(result !== null && result !== ''&& result > 0)
			{	
				Ext.getCmp(PF+'txtSaldoiniDeinter').setValue(NS.formatNumber(Math.round((result) * 100) / 100));
				Ext.getCmp(PF+'txtSaldofinDeinter').setValue(NS.formatNumber(Math.round((result - NS.monto) * 100) / 100));
			}
			else if(result <= 0)
			{
				Ext.MessageBox.alert("SET","No hay saldo suficiente para el traspaso");
				Ext.getCmp(PF+'txtSaldoiniDeinter').setValue(NS.formatNumber(0));
				Ext.getCmp(PF+'txtSaldofinDeinter').setValue(NS.formatNumber(0));
				//return;
			}
			if((result - NS.monto) < 0)
			{
				Ext.MessageBox.alert("SET","No tiene saldo suficiente en la chequera");
				return;
			}
				
		});
		NS.cmbChequeraDeValidate2();
	};
	
	//combo fisico chequera inter
	NS.cmbChequeraDeinter = new Ext.form.ComboBox({
		 store: NS.storeChequeraDeinter 	
		,name: PF+'cmbChequeraDeinter'
		,id: PF+'cmbChequeraDeinter'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :120
		,y :90
		,width :180
		,valueField:'idChequera'
		,displayField:'idChequera'
		,autocomplete: true
		,emptyText: 'Chequera'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
			fn: NS.llenarSaldosinter		
			}	
		}
	});
	
	
	/**llena el combo bancos inter hacia donde va el traspaso*/
	NS.storeBancoAinter = new Ext.data.DirectStore( {
		paramsAsHash: false,
		paramOrder:['empresa2', 'bandera', 'bancoDe', 'chequeraDe'],
		root: '',
		directFn: TraspasosAction.llenarComboBancoInter2,
		idProperty: 'idBanco',  		
		fields: [
			{name: 'idBanco' },
			{name: 'descBanco'}
		],
		listeners: {
			load: function(s, records){
				if(records.length == null || records.length <= 0)
					Ext.MessageBox.alert("SET"," No hay bancos disponibles");
				
				if(records.length == 1)
				{
					Ext.getCmp(PF+'txtnoBancoAinter').setValue(records[0].get('idBanco'));
					Ext.getCmp(PF+'cmbBancoAinter').setValue(records[0].get('idBanco'));
				    NS.accionarCmbBancoAinter(records[0].get('idBanco'));
				}
			}
		}
	}); 
	
	NS.accionarCmbBancoAinter = function(comboValor) {
		NS.noBan = parseInt(Ext.getCmp(PF+'txtnoBancoAinter').getValue());
		//Ext.getCmp(PF+'txtnoBancoAinter').setValue(NS.noBan);
		NS.piNoIntentos = 0;
	    NS.plEmpresa = 0;
	    NS.psDivisa = '';
	    NS.psCheqExcluye = '';

			NS.arriba();
//			Valida si el banco es el mismo de los dos lados, la chequera seleccionada no se incluye en las cuentas del banco
//			hacia donde va el traspaso
		if(parseInt(Ext.getCmp(PF+'txtnoBancoDeinter').getValue()) == parseInt(Ext.getCmp(PF+'txtnoBancoAinter').getValue()))
        {
            NS.psCheqExcluye = Ext.getCmp(PF+'cmbChequeraDeinter').getValue();
        }
        else{
            NS.psCheqExcluye = '';
        }
        Ext.getCmp(PF+'cmbChequeraAinter').setValue('');
		Ext.getCmp(PF+'txtSaldoiniAinter').setValue('');
		Ext.getCmp(PF+'txtSaldofinAinter').setValue('');
       	NS.storeChequeraAinter.baseParams.empresa = parseInt(Ext.getCmp(PF+'txtnoEmpresaAinter').getValue());
       	NS.storeChequeraAinter.baseParams.banco = NS.noBan;
       	NS.storeChequeraAinter.baseParams.bandera = NS.bandera;
       	NS.storeChequeraAinter.baseParams.cheqEx = NS.psCheqExcluye;
       	
       	if(NS.unformatNumber(Ext.getCmp(PF+'txtSaldofinDeinter').getValue()) < 0)
			{
				//Ext.MessageBox.alert("SET"," No hay saldo suficiente para el traspaso");
				Ext.getCmp(PF+'txtSaldoiniAinter').setValue(NS.formatNumber(0));
				Ext.getCmp(PF+'txtSaldofinAinter').setValue(NS.formatNumber(0));
				//return;
				NS.storeChequeraAinter.load();
			}
			else
			{
	       		NS.storeChequeraAinter.load();
	       	}
        var records = NS.storeChequeraAinter.data.items;
        
	    if(records.length == 1)
	    {
	    	Ext.getCmp(PF+'cmbChequeraAinter').setValue(''+records[0].get('idChequera'));
    	}
        else
        {
            Ext.getCmp(PF+'txtSaldoiniAinter').setValue('');
            Ext.getCmp(PF+'txtSaldofinAinter').setValue('');
        }
	};
	
	//combo fisico bancos inter
	NS.cmbBancoAinter = new Ext.form.ComboBox({
		 store: NS.storeBancoAinter 	
		,name: PF+'cmbBancoAinter'
		,id: PF+'cmbBancoAinter'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :120
		,y :50
		,width :180
		,valueField:'idBanco'
		,displayField:'descBanco'
		,autocomplete: true
		,emptyText: 'Banco'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{		
				fn: function(combo, valor)
				{
					BFwrk.Util.updateComboToTextField(PF+'txtnoBancoAinter',NS.cmbBancoAinter.getId());
					NS.accionarCmbBancoAinter(combo.getValue())
				}	
			}	
		}
	});
	
	/**llena el combo chequera A interempresas*/
	NS.storeChequeraAinter = new Ext.data.DirectStore( {
		paramsAsHash: false,
		paramOrder:['empresa','banco','bandera', 'cheqEx'],
		root: '',
		directFn: TraspasosAction.obtenerChequeraA,
		idProperty: 'idChequeraAI',  	//4	
		fields: [
			{name: 'idChequera'}
		],
		listeners: {
			load: function(s, records){
				if(records.length == null || records.length <= 0){
					Ext.MessageBox.alert("SET"," No hay chequeras disponibles");
					return;
					}
				if(records.length == 1)
				{
					Ext.getCmp(PF+'cmbChequeraAinter').setValue(records[0].get('idChequera'));
			    	NS.llenarSaldosinterDestino();
				}
			}
		}
	}); 
	
	NS.llenarSaldosinterDestino = function(combo, value) {
		NS.noEmp = parseInt(Ext.getCmp(PF+'txtnoEmpresaAinter').getValue());
		NS.noBan = parseInt(Ext.getCmp(PF+'txtnoBancoAinter').getValue());
		NS.noChe = Ext.getCmp(PF+'cmbChequeraAinter').getValue();
	    NS.plEmpresa = 0;
	    NS.plBanco = 0;
	    NS.lchequera = '';
		Ext.getCmp(PF+'txtSaldoiniAinter').setValue('');
		Ext.getCmp(PF+'txtSaldofinAinter').setValue('');
        Ext.getCmp(PF+'cmdEjecutar').setDisabled(false);
	    TraspasosAction.verificarDivisa(NS.noBan, NS.noChe, NS.bandera, function(resultado,e)
 				{	
	        if(NS.bandera){
	        	if(resultado[0] == undefined)
	        		NS.psTipoChequera= '';
		        else if(resultado!== null && resultado !== '' && resultado !== undefined){
        			NS.psTipoChequera = resultado[0].tipo;
        			} 
	            if(NS.psTipoChequera === 'I'){
	//				  Cuando la chequera es de inversion en los 2 lados del traspaso el concepto cambia al siguiente
	                if(NS.flagInvDe){
	                    Ext.getCmp(PF+'txtConcepto').setValue('TRASPASO DE INVERSION');
	                    }
	                else{
	                    Ext.getCmp(PF+'txtConcepto').setValue('INCREMENTO DE INVERSION');
	                	}
	               			                
	                TraspasosAction.obtenerDatosReferencia(NS.noEmp, NS.noBan, NS.noChe, function(result,e)
                    {
                    	if(result!==null && result !== '' && result !== undefined){
	                    	Ext.getCmp(PF+'txtRef').setValue(result[0].referencia);
	                        Ext.getCmp(PF+'txtInst').setValue(result[0].razonSocial);
                        	}
                        else{
	                        Ext.getCmp(PF+'txtRef').setValue('');
	                        Ext.getCmp(PF+'txtInst').setValue('');
                    		}
                    });
	                
	            }
	            else{
	                if(NS.flagInvDe){
	                    Ext.getCmp(PF+'txtConcepto').setValue('RECUPERACION DE INVERSION');
	                }
	            }
	        }
	       
	        TraspasosAction.obtenerClabe(NS.noEmp, NS.noBan, NS.noChe, function(res, e)
	        {	
	        	if(res[0] != undefined){
        			NS.divisa = res[0].divisa;
        			NS.clabe = res[0].clabe;
        			if(NS.divisa !== null && NS.divisa === 'MN')
		        	{	
		        		if(NS.clabe === null)
		        			Ext.getCmp(PF+'txtClabe').setValue('');
		        		else
		        		{
		        			Ext.getCmp(PF+'txtClabe').setValue(NS.clabe);
		        			NS.abajo();
		        		}	
		        	}
        		}
        		else
        		Ext.getCmp(PF+'txtClabe').setValue('');
	        });
	   
	    	NS.monto = NS.unformatNumber(Ext.getCmp(PF+'txtMonto').getValue());
			Ext.getCmp(PF+'txtMonto').setReadOnly;
	    	var reg1 = {};
			var matriz1 = new Array();
			reg1.empresa = NS.noEmp;
			reg1.banco = NS.noBan;
			reg1.chequera = NS.noChe;
			reg1.fecha = NS.fechaHoy;
			reg1.pbInversion = NS.bandera;
			reg1.bAnterior = NS.fechaAnterior;
			
			matriz1[0] = reg1;
			var jsonString1 = Ext.util.JSON.encode(matriz1);
			TraspasosAction.obtenerSaldoFinal(jsonString1, function(result, e)
			{	
				
				if(result !== null && result !== '')
				{
					Ext.getCmp(PF+'txtSaldoiniAinter').setValue(NS.formatNumber(Math.round((result) * 100) / 100));
					Ext.getCmp(PF+'txtSaldofinAinter').setValue(NS.formatNumber(result + parseFloat(NS.monto)));
				}
				else
				{
					Ext.getCmp(PF+'txtSaldoiniAinter').setValue(NS.formatNumber(0));
					Ext.getCmp(PF+'txtSaldofinAinter').setValue(NS.formatNumber(0));
				}
			});
		});
	}
	
	//combo fisico chequera A ineterempresas
	NS.cmbChequeraAinter = new Ext.form.ComboBox({
		 store: NS.storeChequeraAinter 	
		,name: PF+'cmbChequeraAinter'
		,id: PF+'cmbChequeraAinter'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x :120
		,y :90
		,width :180
		,valueField:'idChequera'
		,displayField:'idChequera'
		,autocomplete: true
		,emptyText: 'Chequera'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{		
				fn: NS.llenarSaldosinterDestino 
			}	
		}
	});
	

	
	
//***************************************FORMULARIO********************************************************	
	NS.contenedorTraspasos =new Ext.FormPanel({
    title: 'Solicitud de Traspasos',
    width: 684,
    height: 540,
    padding: 0,
    frame: true,
    autoScroll: true,
    layout: 'absolute',
    renderTo: NS.tabContId, 
        items: [
                NS.frmSetGrupoRubroEgreso,
                NS.frmSetGrupoRubroIngreso,
            {
                xtype: 'label',
                text: 'Empresa:',
                x: 10,
                y: 10,
                hidden: true
            },
            {
                xtype: 'textfield',
                x: 10,
                y: 30,
                width: 310,
                readOnly: true,
                value: NS.GS_DESC_EMPRESA,
                hidden: true
            },
            {
                xtype: 'label',
                text: 'Usuario:',
                x: 340,
                y: 10,
                hidden: true
            },
            {
                xtype: 'textfield',
                x: 340,
                y: 30,
                width: 210,
                readOnly: true,
                value: NS.desc_usuario,
                hidden: true
            },
            {
                xtype: 'label',
                text: 'Fecha:',
                x: 570,
                y: 10,
                hidden: true
            },
            {
                xtype: 'datefield',
                format: 'd/m/Y',
                value: apps.SET.FEC_HOY,
                id: PF+'Fecha',
                name: PF+'Fecha',
                x: 570,
                y: 30,
                width: 100,
                readOnly: true,
                hidden: true
            },
            {
                xtype: 'fieldset',
                title: '',
                x: 10,
                y: 10,
                layout: 'absolute',
                width: 330,
                height: 70,
                items: [
		                {
		            xtype: 'radiogroup',
		            columns: 2,
		            items: [
		            	{	
	                        boxLabel: 'Barrido de Cuentas',
	                        name: PF+'optTraspaso',
	                        inputValue: 1,
	                        hidden: true,
	                        listeners:{
								check:{
									fn:function(opt, valor)
									{
										if(valor==true){
										NS.entreCuentas = false;
										NS.interEmpresas = false;
										NS.barrido = true;
										Ext.getCmp(PF+'fieldBarrido').show();
										Ext.getCmp(PF+'fieldInter1').hide();
										Ext.getCmp(PF+'fieldInter2').hide();
										Ext.getCmp(PF+'fieldCuentas1').hide();
										Ext.getCmp(PF+'fieldCuentas2').hide();
										Ext.getCmp(PF+'cmbConcepto').hide();
										Ext.getCmp(PF+'txtConcepto').show();
										Ext.getCmp(PF+'cmdEjecutar').setDisabled(true);
										/*
										Dim permiso As String
										NS.contenedorTraspasos.getForm().reset();
									    Ext.getCmp(PF+'cmbConcepto').setValue('');PF+'chkInversion'
									   	Ext.getCmp(PF+'txtConcepto').setValue('TRASPASO');
									    
									    Ext.getCmp(PF+'chkInversion').hide();
									    chkInversion.Value = 0
									    
									    permiso = gobjVarGlobal.valor_Configura_set(244)
									    
									    If (permiso = "SI") Then
									        txtconcepto.Locked = False
									        txtconcepto.BackColor = &H80000005
									    Else
									        txtconcepto.Locked = True
									    End If
									        
									    
									    txtMonto.Enabled = False
									    Call modGlobal.gobjVarGlobal.llena_combo("empresa", "nom_empresa", "no_empresa", Me.CmbEmpresaBarrido, Mat_EmpresaBarre)
									    Call modGlobal.gobjVarGlobal.llena_combo("cat_divisa", "desc_divisa", "id_divisa", Me.CmbDivisa, Mat_Divisa)
									    
									    Frame1.Enabled = False
									    Frame2.Enabled = False
										*/
										}
									}
								}
							}
	                    },
	                    {	
	                    	boxLabel: 'Cuenta eje o Interempresas',
	                    	name: PF+'optTraspaso',
	                        inputValue: 2,
	                        listeners:{
								check:{
									fn:function(opt, valor)
									{
										if(valor==true){
										    //NS.obtenerFechaHoy();
											NS.entreCuentas = false;
											NS.interEmpresas = true;
											NS.barrido = false;
											Ext.getCmp(PF+'fieldInter1').show();
											Ext.getCmp(PF+'fieldInter2').show();
											Ext.getCmp(PF+'fieldCuentas1').hide();
											Ext.getCmp(PF+'fieldCuentas2').hide();
											Ext.getCmp(PF+'fieldBarrido').hide();
											Ext.getCmp(PF+'cmbConcepto').show();
											Ext.getCmp(PF+'txtConcepto').hide();
										    Ext.getCmp(PF+'cmbConcepto').setValue('');
										    Ext.getCmp(PF+'txtConcepto').setValue('');
										    Ext.getCmp(PF+'txtnoEmpresaDeinter').setValue('');
										    Ext.getCmp(PF+'txtnoEmpresaAinter').setValue('');
										    Ext.getCmp(PF+'txtnoBancoDeinter').setValue('');
										    Ext.getCmp(PF+'txtnoBancoAinter').setValue('');
										    Ext.getCmp(PF+'cmdEjecutar').setDisabled(true);
										    
										    NS.arriba();
										    //Ext.getCmp(PF+'chkInversion').hidden(false);
										    Ext.getCmp(PF+'chkInversion').setValue(0);
										    Ext.getCmp(PF+'txtMonto').setDisabled(false);
										   
										   	Ext.getCmp(PF+'txtFecha').setValue('');
										   	Ext.getCmp(PF+'txtMonto').setValue('');
										    Ext.getCmp(PF+'txtSaldoiniDeinter').setValue('');
										    Ext.getCmp(PF+'txtSaldofinDeinter').setValue('');
										    Ext.getCmp(PF+'txtSaldoiniAinter').setValue('');
										    Ext.getCmp(PF+'txtSaldofinAinter').setValue('');
											
										    Ext.getCmp(PF+'frmSetGrupoRubroEgreso').setDisabled(false);
										    Ext.getCmp(PF+'frmSetGrupoRubroIngreso').setDisabled(false);
										}
									}
								}
							}
	                        
	                    },
	                    {
	                    	boxLabel: 'Entre Cuentas',
	                    	name: PF+'optTraspaso',
	                        inputValue: 3,
	                        checked:true,
	                        listeners:{
								check:{
									fn:function(opt, valor)
									{
										if(valor==true){
											NS.entreCuentas = true;
											NS.interEmpresas = false;
											NS.barrido = false;
											Ext.getCmp(PF+'fieldCuentas1').show();
					                        Ext.getCmp(PF+'fieldCuentas2').show();
											Ext.getCmp(PF+'fieldBarrido').hide();
											Ext.getCmp(PF+'fieldInter1').hide();
											Ext.getCmp(PF+'fieldInter2').hide();
											Ext.getCmp(PF+'cmbConcepto').hide();
											Ext.getCmp(PF+'txtConcepto').show();
											Ext.getCmp(PF+'cmdEjecutar').setDisabled(true);
											Ext.getCmp(PF+'txtFecha').setValue('');
										   	Ext.getCmp(PF+'txtMonto').setValue('');
										   	
											var permiso;
										    NS.contenedorTraspasos.getForm().reset();
										    //NS.obtenerFechaHoy();
										    Ext.getCmp(PF+'txtMonto').setDisabled(false);
										    Ext.getCmp(PF+'txtConcepto').setValue('TRASPASO');
										    TraspasosAction.configuraSET(244, function(result, e){
										    	 if(result){
										    	 	Ext.getCmp(PF+'txtConcepto').setReadOnly(false);
										        	//pendiente
										    	 }
										    	 else
										    	 	Ext.getCmp(PF+'txtConcepto').setReadOnly(true);
										    });
										    Ext.getCmp(PF+'cmbEmpresaDecta').setValue('');
										    NS.storeEmpresaDecta.load();
										    //Ext.getCmp(PF+'chkInversion').show();
										    
										    Ext.getCmp(PF+'frmSetGrupoRubroEgreso').setDisabled(true);
										    Ext.getCmp(PF+'frmSetGrupoRubroIngreso').setDisabled(true);
										    
											NS.arriba();
										}
									}
								}
							}
	                    }
		            ]
		        }
                ]
            },
            {
                xtype: 'fieldset',
                title: '',
                x: 340,
                y: 10,
                layout: 'absolute',
                width: 330,
                height: 70,
                items: [
                    {
                        xtype: 'label',
                        text: 'Monto:',
                        x: 10,
                        y: 0,
                        width: 30
                    },
                    {
                        xtype: 'textfield',
                        id: PF+'txtMonto',
                        x: 10,
                        y: 20,
                        width: 90,
                        listeners:{
		                	change:{
		               	         fn:function(caja, valor){
		               	        	 
		               	        	var cadena = String(Ext.getCmp(PF+'txtMonto').getValue());
		               	        	var pos = cadena.charAt(0); 
		               	        	
		               	        	if ((Ext.getCmp(PF+'txtMonto').getValue().match('^[0-9]{1,15}(\.[0-9]{1,2})?$'))==null || pos ==0){
                        				Ext.getCmp(PF+'txtMonto').setValue('');
                        				 Ext.Msg.alert('Información SET', 'El valor del monto tiene que ser numerico, o tener una longitud de 15 caracteres con dos decimales.');
                        			 }else{
                        				 Ext.getCmp(PF+'txtMonto').setValue(NS.formatNumber(valor));
                        				 
                        			 }
		               	         	
		               	         }
	               		   	}
           			 	}
                    },
                    {
                        xtype: 'label',
                        text: 'Concepto:',
                        x: 140,
                        y: 0
                    },
                    {
                        xtype: 'uppertextfield',
                        value: 'TRASPASO',
                        id: PF+'txtConcepto',
                        name: PF+'txtConcepto',
                        x: 110,
                        y: 20,
                        width: 190
                    },
                    NS.cmbConcepto                    
                ]
            },
            {
                xtype: 'fieldset',
                title: 'Traspaso de:',
                x: 10,
                y: 90,
                width: 330,
                height: 230,
                layout: 'absolute',
                id: PF+'fieldCuentas1',
                items: [
                    {
                        xtype: 'label',
                        text: 'Empresa:',
                        x: 0,
                        y: 10,
                        width: 50
                    },
                    {
                        xtype: 'label',
                        text: 'Banco:',
                        x: 0,
                        y: 50
                    },
                    {
                        xtype: 'label',
                        text: 'Chequera:',
                        x: 0,
                        y: 90
                    },
                    {
                        xtype: 'label',
                        text: 'Saldo Disponible:',
                        x: 0,
                        y: 130
                    },
                    {
                        xtype: 'label',
                        text: 'Nuevo Saldo Disp.:',
                        x: 0,
                        y: 170
                    },
                    {
                        xtype: 'textfield',
                        id: PF+'txtnoEmpresaDecta',
                        name: PF+'txtnoEmpresaDecta',
                        x: 60,
                        y: 10,
                        width: 50,
                        listeners:{
                        	change:{
                        		fn:function(caja, valor){
                    				if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
	                        			var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtnoEmpresaDecta', NS.cmbEmpresaDecta.getId());
	                        			
	                        			if(comboValue != null && comboValue != undefined && comboValue != '')
	                        				NS.accionarCmbEmpresaDecta(comboValue);
	                        			else {
	                        				Ext.getCmp(PF+'txtnoEmpresaDecta').reset();
	                        				NS.accionarCmbEmpresaDecta(0);
	                        			}
                    				}else {
                    					NS.cmbEmpresaDecta.reset();
                    					NS.accionarCmbEmpresaDecta(0);
                    				}
                        		}
                        	}
                        }
                    },
                    NS.cmbEmpresaDecta,
                    {
                        xtype: 'textfield',
                        id: PF+'txtnoBancoDecta',
                        name: PF+'txtnoBancoDecta',
                        x: 60,
                        y: 50,
                        width: 50,
                        listeners:{
                        	change:{
                        		fn: function(caja,valor){
                    				if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
                        				var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtnoBancoDecta', NS.cmbBancoDecta.getId());
                        				
                        				if(comboValue != null && comboValue != undefined && comboValue != '')
                        					NS.accionarCmbBancoDecta(valueCombo);
                        				else {
	                        				Ext.getCmp(PF + 'txtnoBancoDecta').reset();
	                        				NS.accionarCmbBancoDecta(0);
	                        			}
                        			}else {
                        					NS.cmbBancoDecta.reset();
                        					NS.accionarCmbBancoDecta(0);
                        			}
                    					
                        		}
                        	}
                        }
                    },
                    NS.cmbBancoDecta,
                    NS.cmbChequeraDecta,
                    {
                        xtype: 'textfield',
                        id: PF+'txtSaldoiniDecta',
                        name: PF+'txtSaldoiniDecta',
                        readOnly: true,
                        x: 120,
                        y: 130,
                        width: 180
                    },
                    {
                        xtype: 'textfield',
                        id: PF+'txtSaldofinDecta',
                        name: PF+'txtSaldofinDecta',
                        readOnly: true,
                        x: 120,
                        y: 170,
                        width: 180
                    }
                ]
            },
            {
                xtype: 'fieldset',
                title: 'Traspaso a:',
                x: 340,
                y: 90,
                layout: 'absolute',
                width: 330,
                height: 230,
                id: PF+'fieldCuentas2',
                items: [
                    {
                        xtype: 'label',
                        text: 'Empresa:',
                        x: 0,
                        y: 10
                    },
                    {
                        xtype: 'label',
                        text: 'Banco:',
                        x: 0,
                        y: 50
                    },
                    {
                        xtype: 'label',
                        text: 'Chequera:',
                        x: 0,
                        y: 90
                    },
                    {
                        xtype: 'label',
                        text: 'Saldo Disponible:',
                        x: 0,
                        y: 130
                    },
                    {
                        xtype: 'label',
                        text: 'Nuevo Saldo Disp.:',
                        x: 0,
                        y: 170
                    },
                    {
                        xtype: 'textfield',
                        id: PF+'txtnoEmpresaActa',
                        name: PF+'txtnoEmpresaActa',
                        readOnly: true,
                        x: 60,
                        y: 10,
                        width: 50
                    },
                    {
                        xtype: 'textfield',
                        id: PF+'txtEmpresaActa',
                        name: PF+'txtEmpresaActa',
                        readOnly: true,
                        x: 120,
                        y: 10,
                        width: 180
                    },
                    {
                        xtype: 'textfield',
                        id: PF+'txtnoBancoActa',
                        name: PF+'txtnoBancoActa',
                        x: 60,
                        y: 50,
                        width: 50,
                        listeners:{
                        	change:{
                        		fn: function(caja,valor){
                        				var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtnoBancoActa',NS.cmbBancoActa.getId());
                        				NS.accionarCmbBancoActa(valueCombo);
                        		}
                        	}
                        }
                    },
                    NS.cmbBancoActa,
                    NS.cmbChequeraActa,
                    
                    {
                        xtype: 'textfield',
                        id: PF+'txtSaldofinActa',
                        name: PF+'txtSaldofinActa',
                        readOnly: true,
                        x: 120,
                        y: 170,
                        width: 180
                    },{
                        xtype: 'textfield',
                        id: PF+'txtSaldoiniActa',
                        name: PF+'txtSaldoiniActa',
                        readOnly: true,
                        x: 120,
                        y: 130,
                        width: 180
                    }
                ]
            },
            {
                xtype: 'fieldset',
                title: 'Traspaso de:',
                layout: 'absolute',
                width: 330,
                x: 10,
                y: 110,
                height: 250,
                hidden: true,
                id: PF+'fieldInter1',
                items: [
                    {
                        xtype: 'label',
                        text: 'Empresa:',
                        x: 0,
                        y: 10
                    },
                    {
                        xtype: 'label',
                        text: 'Banco:',
                        x: 0,
                        y: 50
                    },
                    {
                        xtype: 'label',
                        text: 'Chequera:',
                        x: 0,
                        y: 90
                    },
                    {
                        xtype: 'label',
                        text: 'Saldo Disponible:',
                        x: 0,
                        y: 130,
                        width: 110
                    },
                    {
                        xtype: 'label',
                        text: 'Nuevo Saldo Disp.:',
                        x: 0,
                        y: 170,
                    },
                    {
                        xtype: 'textfield',
                        id: PF+'txtnoEmpresaDeinter',
                        name: PF+'txtnoEmpresaDeinter',
                        x: 60,
                        y: 10,
                        width: 50,
                        listeners:{
                        	change:{
                        		fn: function(caja,valor){
                    				if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
                        				var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtnoEmpresaDeinter',NS.cmbEmpresaDeinter.getId());
                        				
                        				if(valueCombo != null && valueCombo != undefined && valueCombo != '')
                        					NS.accionarCmbEmpresaDeinter(valueCombo);
                        				else {
                        					Ext.getCmp(PF+'txtnoEmpresaDeinter').reset();
                        					NS.accionarCmbEmpresaDeinter(0);
                        				}
                    				}else {
                    					NS.cmbEmpresaDeinter.reset();
                    					NS.accionarCmbEmpresaDeinter(0);
                    				}
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'textfield',
                        id: PF+'txtnoBancoDeinter',
                        name: PF+'txtnoBancoDeinter',
                        x: 60,
                        y: 50,
                        width: 50,
                        listeners:{
                        	change:{
                        		fn: function(caja,valor){
                        				var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtnoBancoDeinter',NS.cmbBancoDeinter.getId());
                        				NS.accionarCmbBancoDeinter(valueCombo);
                        		}
                        	}
                        }
                    },
                    NS.cmbEmpresaDeinter,
                    NS.cmbBancoDeinter,
                    NS.cmbChequeraDeinter,
                    {
                        xtype: 'textfield',
                        id: PF+'txtSaldoiniDeinter',
                        name: PF+'txtSaldoiniDeinter',
                        readOnly: true,
                        x: 120,
                        y: 130,
                        width: 180
                    },
                    {
                        xtype: 'textfield',
                        id: PF+'txtSaldofinDeinter',
                        name: PF+'txtSaldofinDeinter',
                        readOnly: true,
                        x: 120,
                        y: 170,
                        width: 180
                    }
                ]
            },
            {
                xtype: 'fieldset',
                title: 'Traspaso a:',
                layout: 'absolute',
                width: 330,
                x: 340,
                y: 110,
                height: 250,
                hidden: true,
                id: PF+'fieldInter2',
                items: [
                    {
                        xtype: 'label',
                        text: 'Empresa:',
                        x: 0,
                        y: 10
                    },
                    {
                        xtype: 'label',
                        text: 'Banco:',
                        x: 0,
                        y: 50
                    },
                    {
                        xtype: 'label',
                        text: 'Chequera:',
                        x: 0,
                        y: 90
                    },
                    {
                        xtype: 'label',
                        text: 'Clabe:',
                        id: PF+'lblClabe',
                        name: PF+'lblClabe',
                        hidden: true,
                        x: 0,
                        y: 130
                    },
                    {
                        xtype: 'label',
                        text: 'Saldo Disponible:',
                        id: PF+'lblSaldoini',
                        name: PF+'lblSaldoini',
                        x: 0,
                        y: 130
                    },
                    {
                        xtype: 'label',
                        text: 'Nuevo Saldo Disp.:',
                        id: PF+'lblSaldofin',
                        name: PF+'lblSaldofin',
                        x: 0,
                        y: 170
                    },
                    {
                        xtype: 'textfield',
                        id: PF+'txtnoEmpresaAinter',
                        name: PF+'txtnoEmpresaAinter',
                        x: 60,
                        y: 10,
                        width: 50,
                        listeners:{
                        	change:{
                        		fn: function(caja,valor){
                        				var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtnoEmpresaAinter',NS.cmbEmpresaAinter.getId());
                        				NS.accionarCmbEmpresaAinter(valueCombo);
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'textfield',
                        id: PF+'txtnoBancoAinter',
                        name: PF+'txtnoBancoAinter',
                        x: 60,
                        y: 50,
                        width: 50,
                        listeners:{
                        	change:{
                        		fn: function(caja,valor){
                        				var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtnoBancoAinter',NS.cmbBancoAinter.getId());
                        				NS.accionarCmbBancoAinter(valueCombo);
                        		}
                        	}
                        }
                    },
                    NS.cmbEmpresaAinter,
                    NS.cmbBancoAinter,
                    NS.cmbChequeraAinter,
                    {
                        xtype: 'textfield',
                        id: PF+'txtClabe',
                        name: PF+'txtClabe',
                        hidden: true,
                        x: 120,
                        y: 130,
                        width: 180
                    },
                    {
                        xtype: 'textfield',
                        id: PF+'txtSaldoiniAinter',
                        name: PF+'txtSaldoiniAinter',
                        readOnly: true,
                        x: 120,
                        y: 130,
                        width: 180
                    },
                    {
                        xtype: 'textfield',
                        id: PF+'txtSaldofinAinter',
                        name: PF+'txtSaldofinAinter',
                        readOnly: true,
                        x: 120,
                        y: 170,
                        width: 180
                    }
                ]
            },
            {
                xtype: 'fieldset',
                title: 'Barrido Automatico de Chequeras',
                layout: 'absolute',
                id: PF+'fieldBarrido',
                x: 10,
                y: 110,
                height: 260,
                hidden: true,
                items: [
                    {
                        xtype: 'label',
                        text: 'Empresa a Barrer:',
                        x: 0,
                        y: 0
                    },
                    {
                        xtype: 'label',
                        text: 'CLABE:',
                        x: 490,
                        y: 50
                    },
                    {
                        xtype: 'label',
                        text: 'Chequera Destino:',
                        x: 320,
                        y: 50
                    },
                    {
                        xtype: 'label',
                        text: 'Banco Destino:',
                        x: 0,
                        y: 50
                    },
                    {
                        xtype: 'label',
                        text: 'Divisa:',
                        x: 320,
                        y: 0
                    },
                    {
                        xtype: 'textfield',
                        x: 0,
                        y: 20,
                        width: 50
                    },
                    {
                        xtype: 'combo',
                        x: 60,
                        y: 20,
                        width: 250
                    },
                    {
                        xtype: 'combo',
                        x: 320,
                        y: 20,
                        width: 150
                    },
                    {
                        xtype: 'textfield',
                        x: 0,
                        y: 70,
                        width: 50
                    },
                    {
                        xtype: 'combo',
                        x: 60,
                        y: 70,
                        width: 250
                    },
                    {
                        xtype: 'combo',
                        x: 320,
                        y: 70,
                        width: 150
                    },
                    {
                        xtype: 'textfield',
                        x: 480,
                        y: 70,
                        width: 130
                    },
                    {
                        xtype: 'button',
                        text: 'Buscar',
                        x: 540,
                        y: 20,
                        width: 70
                    },
                    {
                        xtype: 'label',
                        text: 'Total de Registros:',
                        x: 130,
                        y: 200
                    },
                    {
                        xtype: 'label',
                        text: 'Monto Total:',
                        x: 340,
                        y: 200
                    },
                    {
                        xtype: 'numberfield',
                        x: 230,
                        y: 200,
                        width: 70
                    },
                    {
                        xtype: 'numberfield',
                        x: 420,
                        y: 200
                    }
                ]
            },
            {
                xtype: 'checkbox',
                id: PF+'chkInversion',
                name: PF+'chkInversion',
                boxLabel: 'Traspaso de Inversion',
                checked: false,
                hidden: true,
                x: 10,
                y: 60,
                listeners:{
					check:{
							fn:function(opt, valor)
							{
								if(valor==true){
									Ext.getCmp(PF+'lblFecha').show();
									Ext.getCmp(PF+'lblRef').show();
									Ext.getCmp(PF+'lblInst').show();
									Ext.getCmp(PF+'txtFecha').show();
									Ext.getCmp(PF+'txtRef').show();
									Ext.getCmp(PF+'txtInst').show();
								}
								else{
									Ext.getCmp(PF+'lblFecha').hide();
									Ext.getCmp(PF+'lblRef').hide();
									Ext.getCmp(PF+'lblInst').hide();
									Ext.getCmp(PF+'txtFecha').hide();
									Ext.getCmp(PF+'txtRef').hide();
									Ext.getCmp(PF+'txtInst').hide();
								}
							}
						}
						}
            },
            {
                xtype: 'label',
                text: 'Fecha:',
                id: PF+'lblFecha',
                hidden: true,
                x: 180,
                y: 0
            },
            {
                xtype: 'label',
                text: 'Referencia:',
                id: PF+'lblRef',
                hidden: true,
                x: 300,
                y: 0
            },
            {
                xtype: 'label',
                text: 'Institucion:',
                id: PF+'lblInst',
                hidden: true,
                x: 460,
                y: 0
            },
            {
                xtype: 'datefield',
                id: PF+'txtFecha',
                format: 'd/m/Y',
                value: apps.SET.FEC_HOY,
                hidden: true,
                x: 180,
                y: 15,
                listeners:{
                	change:{
               	         fn:function(caja, valor){
               	         	if(cambiarFecha(''+valor) > cambiarFecha(''+Ext.getCmp(PF+'Fecha').getValue()))
							{	
								Ext.Msg.alert('SET','La fecha no puede ser mayor a hoy');
								Ext.getCmp(PF+'txtFecha').setValue(NS.fechaHoy);
								return;
							} 
               	         }
               		   }
           			 }
            },
            {
                xtype: 'textfield',
                id: PF+'txtRef',
                hidden: true,
                x: 300,
                y: 15
            },
            {
                xtype: 'textfield',
                id: PF+'txtInst',
                hidden: true,
                x: 460,
                y: 15,
                width: 210
            },
            {
                xtype: 'button',
                text: 'Ejecutar',
                id:PF+'cmdEjecutar',
                name:PF+'cmdEjecutar',
                disabled: true,
                x: 510,
                y: 390,
                width: 70,
                listeners:{
                		click:{
                	fn:function(e){
            	
						NS.ldImporte = 0.0;
					    NS.ldResp = 0.0;
					    NS.a1 = 0.0;
					    NS.pdFolio = 0.0;
					    NS.a2 = 0.0;
					    
					    NS.lsChequera = '';
					    NS.lsChequeraBenef = '';
					    NS.psDivisaDe = '';
					    NS.psDivisaA = '';
					    NS.psError = '';
					    NS.psEmpresa = '';
					    NS.psEmpresaA  = '';
					    NS.i = 0;					        
					    NS.llEmpresa = 0;
					    NS.llEmpresaA = 0;
					    NS.piFolioParametro = 0;
					    NS.piFolioParametroA = 0;
					    NS.plTipoOperacion = 0;
					    NS.plEmpresa = 0;
					    
					    NS.lAfectados = 0;
					    NS.liBancoBenef = 0;
					    NS.LI_BANCO = 0;
					    //MSDESC
					   // NS.psConcepto = '';
					    NS.pdResp = '';					    
					    NS.TipoCambio = 0.0;
					    NS.banTrans = false;
					    //NS.bandera = true;
					    //MSDESC
					    
						/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
		            	//INIT: VALIDACION DE LOS CONTROLES DE GRUPO Y RUBRO
		            	/*---------------------------------------------------------------------------*/
						
					    //****VARIABLES PARA GRUPOS Y RUBROS
					    NS.idGrupoIngreso = 0;
					    NS.idRubroIngreso = 0;
					    NS.idGrupoEgreso = 0;
					    NS.idRubroEgreso = 0;
					    //****VARIABLES PARA GRUPOS Y RUBROS

					    if(NS.interEmpresas) {
			            	if( Ext.getCmp(PF+'txtIdGrupoIngreso').getValue() == '' )
			            	{	
			            			Ext.Msg.alert('SET', 'Debe seleccionar el grupo de ingreso.');
			            			return;
			            			
			            	}else{
			            		
			            			NS.idGrupoIngreso =  parseInt( Ext.getCmp(PF+'txtIdGrupoIngreso').getValue() );
			            			
			            	}
			            		
			            		
		            		if( Ext.getCmp(PF+'txtIdRubroIngreso').getValue() == '' )
		            		{	
		            			Ext.Msg.alert('SET', 'Debe seleccionar el rubro del ingreso.');
		            			return;
		            			
		            		}else{	
		            			NS.idRubroIngreso = parseInt( Ext.getCmp(PF+'txtIdRubroIngreso').getValue() );
		            		}
		            		
		            		if( Ext.getCmp(PF+'txtIdGrupoEgreso').getValue() == '' )
		            		{	
		            			Ext.Msg.alert('SET', 'Debe seleccionar el grupo del egreso.');
		            			return;
		            			
		            		}else{
		            			NS.idGrupoEgreso = parseInt( Ext.getCmp(PF+'txtIdGrupoEgreso').getValue() );
		            		}
		            		
		            		
		            		if( Ext.getCmp(PF+'txtIdRubroEgreso').getValue() == '' )
		            		{	
		            			Ext.Msg.alert('SET', 'Debe seleccionar el rubro del ingreso.');
		            			return;
		            			
		            		}else{	
		            			
		            			NS.idRubroEgreso = parseInt( Ext.getCmp(PF+'txtIdRubroEgreso').getValue() );
		            		
		            		}
					    }
	            		
	            		
						
	            		
	                	/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	                	//INIT: VALIDACION DE LOS CONTROLES DE GRUPO Y RUBRO
	                	/*---------------------------------------------------------------------------*/
					    
					    var myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Registrando Solicitud..."});
						myMask.show();
					    TraspasosAction.verificarDivisa(NS.noBan, NS.noChe, NS.bandera, function(result,e)
   						{	
	   						if(NS.bandera){
		   						if(result !== null && result !== '' && result !== undefined){
						        			NS.psTipoChequera = result[0].tipo;
				        		}
						        if(NS.psTipoChequera === 'I')
						         {   
							        if(cambiarFecha(''+Ext.getCmp(PF+'txtFecha').getValue()) !== NS.fechaHoy)
									{	
										Ext.Msg.alert('SET', 'La fecha no puede ser diferente a hoy');
										Ext.getCmp(PF+'txtFecha').setValue(NS.fechaHoy);
										return;
									}
						        }
						    }
					    
						    if(NS.entreCuentas)
						    {
						        NS.psConcepto = Ext.getCmp(PF+'txtConcepto').getValue();
					       	}
						    else if(NS.interEmpresas)
						    {
						        if(Ext.getCmp(PF+'cmbConcepto') === '')
						        {
						        	Ext.Msg.alert('SET', 'Debe seleccionar un concepto');
						            return;
						        }
					       	}
					       	else if(NS.barrido)
						    	NS.psConcepto = Ext.getCmp(PF+'txtConcepto').getValue();
					    
							if(NS.barrido == false)
							{
						        NS.ldImporte = NS.unformatNumber(Ext.getCmp(PF+'txtMonto').getValue());
					        }
					    
						    if(NS.entreCuentas)
						    {	
						        if(NS.psTipoChequera === 'I' && NS.bRegreso == true)
						        {
						            NS.plTipoOperacion = 3700;
					            }
						        else
						        {
						            NS.plTipoOperacion = 3800;
					            }
					      	}  
						    else if(NS.interEmpresas)
						    {
						        NS.plTipoOperacion = 3801;
					        }
						    else if(NS.barrido)
						    {
						        NS.plTipoOperacion = 3800;
					        }
						    
							if(NS.barrido)
							{
								Ext.Msg.alert('SET','Esta opcion no esta disponible!');
							}    
							else
							{
							    // Obtener banco y chequera
						    	if(NS.entreCuentas){
								    NS.lsChequera = Ext.getCmp(PF+'cmbChequeraDecta').getValue();
								    NS.LI_BANCO = parseInt(Ext.getCmp(PF+'txtnoBancoDecta').getValue());
								        
								    // Obtener Banco  y chequera  benef
								    NS.lsChequeraBenef = Ext.getCmp(PF+'cmbChequeraActa').getValue();
								    NS.liBancoBenef = parseInt(Ext.getCmp(PF+'txtnoBancoActa').getValue());
								    
								    //Determina el emisor
								    NS.llEmpresa = parseInt(Ext.getCmp(PF+'txtnoEmpresaDecta').getValue());
							        NS.psEmpresa = NS.empresaDC;
							        
							        //Determina el receptor
							        NS.llEmpresaA = parseInt(Ext.getCmp(PF+'txtnoEmpresaDecta').getValue());
							        NS.psEmpresaA = NS.empresaDC;
							    }    
							    
							    if(NS.interEmpresas)		
							    {
							    	NS.lsChequera = Ext.getCmp(PF+'cmbChequeraDeinter').getValue();
								    NS.LI_BANCO = parseInt(Ext.getCmp(PF+'txtnoBancoDeinter').getValue());
								        
								    // Obtener Banco  y chequera  benef
								    NS.lsChequeraBenef = Ext.getCmp(PF+'cmbChequeraAinter').getValue();
								    NS.liBancoBenef = parseInt(Ext.getCmp(PF+'txtnoBancoAinter').getValue());
								    
							    	//Determina el emisor
							        NS.llEmpresa = parseInt(Ext.getCmp(PF+'txtnoEmpresaDeinter').getValue());
							        NS.psEmpresa = NS.empresaDI;
							        
							        //Determina el receptor
							        NS.llEmpresaA = parseInt(Ext.getCmp(PF+'txtnoEmpresaAinter').getValue());
							        NS.psEmpresaA = NS.empresaAI;
							    }
							    
					      	}//termina la if de la opcion para barridos
							
			            
							
							parseInt(Ext.getCmp(PF+'txtnoBancoAinter').getValue());
					 		
               				var matriz2 = new Array (); 
	       					var reg = {};
	       					reg.empresa = NS.llEmpresa;
	       					reg.operacion = NS.plTipoOperacion;
	       					reg.chequera = NS.lsChequera;
	       					reg.banco = NS.LI_BANCO;
	       					reg.importe = NS.ldImporte;
	       					reg.fecha = NS.fechaHoy;
	       					reg.usuario = NS.GI_USUARIO;
	       					reg.bancoBenef = NS.liBancoBenef;
	       					reg.chequeraBenef = NS.lsChequeraBenef;
	       					reg.concepto = NS.psConcepto;
	       					reg.caja = NS.GI_ID_CAJA;
	       					reg.beneficiario = NS.psEmpresaA;
	       					reg.beneficiarion = NS.psEmpresa;
	       					reg.cliente = NS.llEmpresaA;
	       					reg.regreso = NS.bRegreso;
	       					reg.bandera = NS.bandera;
	       					reg.referencia = Ext.getCmp(PF+'txtRef').getValue();
	       					if(Ext.getCmp(PF+'txtFecha').getValue() !== '')
	       						reg.fechaInversion = cambiarFecha(''+Ext.getCmp(PF+'txtFecha').getValue());
	       					reg.ubicacion = NS.ubicacion;
	       					reg.idGrupoIngreso=NS.idGrupoIngreso; 
	    	       			reg.idRubroIngreso=NS.idRubroIngreso; 
	    	       			reg.idGrupoEgreso=NS.idGrupoEgreso; 
	    	       			reg.idRubroEgreso=NS.idRubroEgreso; 
	       					
	       					matriz2[0] = reg; 
	        				var jsonString2 = Ext.util.JSON.encode(matriz2);
	        				
							TraspasosAction.insertarSolicitudTraspaso(jsonString2, function(result, e){
							myMask.hide();
								if(result.msgUsuario!==null  &&  result.msgUsuario!=='') {
									Ext.Msg.alert('SET',''+result.msgUsuario);
									NS.contenedorTraspasos.getForm().reset();
								}
							});
						});
            		}
           		}
           	}
   		},
          {
              xtype: 'button',
              text: 'Limpiar',
              id:PF+'btnCancelar',
              name:PF+'btnCancelar',
              x: 600,
              y: 390,
              width: 60,
              listeners:{
              	click:{
              		fn:function(e){
              			NS.contenedorTraspasos.getForm().reset();
              			//NS.obtenerFechaHoy();
              		}
              	}
              }
          }
      ] 
    });
	
	
    
    //cmbChequeraDe_Validate
	NS.cmbChequeraDeValidate = function(){
	TraspasosAction.verificarDivisa(NS.noBan, NS.noChe, NS.bandera, function(result,e)
   	{	
		NS.psDivisa = '';
		if(Ext.getCmp(PF+'cmbChequeraDecta').getValue()!=='') {
				Ext.getCmp(PF+'cmbChequeraActa').setValue('');
				Ext.getCmp(PF+'cmbBancoActa').setValue('');
				Ext.getCmp(PF+'txtnoBancoActa').setValue('');
				Ext.getCmp(PF+'txtSaldoiniActa').setValue('');
				Ext.getCmp(PF+'txtSaldofinActa').setValue('');
	        						
	        	if(Ext.getCmp(PF+'txtnoBancoDecta').getValue()!==''){
	        		if(result!==null && result !== '' && result !== undefined){
	        			NS.psDivisa = result[0].divisa;
	        		}
	        		//parametros para llena el combo bancoA
	        	NS.storeBancoActa.baseParams.empresa = NS.noEmp;
			  	NS.storeBancoActa.baseParams.divisa = NS.psDivisa;
			  	NS.storeBancoActa.baseParams.bandera = NS.bandera;
			  	NS.storeBancoActa.baseParams.tipo = NS.psTipoChequera;
	            if(NS.bandera){
	            	if(result!==null && result !== '' && result !== undefined){
	        			NS.psTipoChequera = result[0].tipo;
	        		}
	                if(NS.psTipoChequera === 'I'){
	                    NS.bRegreso = true;
	                    NS.flagInvDe = true //ECOC 19/09/05 Para saber si se ha seleccionado una chequera de inversion
	                    TraspasosAction.obtenerDatosReferencia(NS.noEmp, NS.noBan, NS.noChe, function(result,e)
	                    {
	                    	if(result!==null && result !== '' && result !== undefined){
		                    	Ext.getCmp(PF+'txtRef').setValue(result[0].referencia);
		                        Ext.getCmp(PF+'txtInst').setValue(result[0].razonSocial);
		                        Ext.getCmp(PF+'txtConcepto').setValue('RECUPERACión DE INVERSION');
	                        }
	                        else{
		                        Ext.getCmp(PF+'txtRef').setValue('');
		                        Ext.getCmp(PF+'txtInst').setValue('');
	                    }
	                    });
	                    
	                     if(cambiarFecha(''+Ext.getCmp(PF+'txtFecha').getValue()) > NS.fechaHoy)
							{	
								Ext.Msg.alert('SET', 'La fecha no puede ser mayor a hoy');
								Ext.getCmp(PF+'txtFecha').setValue(NS.fechaHoy);
								return;
							}                
					
	                }
	                else{
	                    NS.flagInvDe = false; //ECOC 19/09/05
	                    NS.bRegreso = false;
	                    Ext.getCmp(PF+'txtRef').setValue('');
	                    Ext.getCmp(PF+'txtInst').setValue('');
	                    Ext.getCmp(PF+'txtConcepto').setValue('INCREMENTO DE INVERSION');
	                    
	                    if(cambiarFecha(''+Ext.getCmp(PF+'txtFecha').getValue()) > NS.fechaHoy)
							{	
								Ext.Msg.alert('SET', 'La fecha no puede ser mayor a hoy');
								Ext.getCmp(PF+'txtFecha').setValue(NS.fechaHoy);
								return;
							}  
	                }
					NS.storeBancoActa.load();
	            }
	            else{
	                NS.bRegreso = false;
		        	NS.storeBancoActa.load();
	            }
	        }
	        
	    }
    });	
    }
    
    //cmbChequeraDe_Validate 2
	NS.cmbChequeraDeValidate2 = function(){
	TraspasosAction.verificarDivisa(NS.noBan, NS.noChe, NS.bandera, function(result,e)
   	{	
		NS.psDivisa = '';
		if(Ext.getCmp(PF+'cmbChequeraDeinter').getValue()!=='') {
				Ext.getCmp(PF+'cmbChequeraAinter').setValue('');
				Ext.getCmp(PF+'cmbBancoAinter').setValue('');
				Ext.getCmp(PF+'txtnoBancoAinter').setValue('');
				Ext.getCmp(PF+'txtSaldoiniAinter').setValue('');
				Ext.getCmp(PF+'txtSaldofinAinter').setValue('');
	        						
	        if(Ext.getCmp(PF+'txtnoBancoDeinter').getValue()!==''){
	        		if(result!==null && result !== '' && result !== undefined){
	        			NS.psDivisa = result[0].divisa;
	        		}
	        			        	
	            if(NS.bandera){
	            	if(result!==null && result !== '' && result !== undefined){
	        			NS.psTipoChequera = result[0].tipo;
	        		}
	                if(NS.psTipoChequera === 'I'){
	                    NS.bRegreso = true;
	                    NS.flagInvDe = true //ECOC 19/09/05 Para saber si se ha seleccionado una chequera de inversion
	                    TraspasosAction.obtenerDatosReferencia(NS.noEmp, NS.noBan, NS.noChe, function(result,e)
	                    {
	                    	if(result!==null && result !== '' && result !== undefined){
		                    	Ext.getCmp(PF+'txtRef').setValue(result[0].referencia);
		                        Ext.getCmp(PF+'txtInst').setValue(result[0].razonSocial);
		                        Ext.getCmp(PF+'txtConcepto').setValue('RECUPERACión DE INVERSION');
	                        }
	                        else{
		                        Ext.getCmp(PF+'txtRef').setValue('');
		                        Ext.getCmp(PF+'txtInst').setValue('');
	                    }
	                    });
	                                        
	                   if(cambiarFecha(''+Ext.getCmp(PF+'txtFecha').getValue()) > NS.fechaHoy)
							{	
								Ext.Msg.alert('SET', 'La fecha no puede ser mayor a hoy');
								Ext.getCmp(PF+'txtFecha').setValue(NS.fechaHoy);
								return;
							}  
					
	                }
	                else{
	                    NS.flagInvDe = false; //ECOC 19/09/05
	                    NS.bRegreso = false;
	                    Ext.getCmp(PF+'txtRef').setValue('');
	                    Ext.getCmp(PF+'txtInst').setValue('');
	                    Ext.getCmp(PF+'txtConcepto').setValue('INCREMENTO DE INVERSION');
	                    
	                    
	                    if(cambiarFecha(''+Ext.getCmp(PF+'txtFecha').getValue()) > NS.fechaHoy)
							{	
								Ext.Msg.alert('SET', 'La fecha no puede ser mayor a hoy');
								Ext.getCmp(PF+'txtFecha').setValue(NS.fechaHoy);
								return;
							}  
	                }
					
	            }
	            else{
	                NS.bRegreso = false;
	            }
	        }
	        
	    }
    });	
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
	
	//Quitar formato a las cantidades
	NS.unformatNumber=function(num) {
		return num.replace(/(,)/g,''); //num.replace(/([^0-9\.\-])/g,''*1);
	};
	
	//funciones para subir y bajar los componentes
	NS.arriba = function(){
		Ext.getCmp(PF+'fieldInter1').setHeight(250);
		Ext.getCmp(PF+'fieldInter2').setHeight(250);
		Ext.getCmp(PF+'txtSaldoiniAinter').setPosition(120,130);
		Ext.getCmp(PF+'txtSaldofinAinter').setPosition(120,170);
		Ext.getCmp(PF+'lblSaldoini').setPosition(0,130);
		Ext.getCmp(PF+'lblSaldofin').setPosition(0,170); 
		Ext.getCmp(PF+'txtClabe').hide();
		Ext.getCmp(PF+'lblClabe').hide();
	}
	NS.abajo = function(){
		Ext.getCmp(PF+'fieldInter1').setHeight(270);
		Ext.getCmp(PF+'fieldInter2').setHeight(270);
		Ext.getCmp(PF+'txtClabe').show();
		Ext.getCmp(PF+'lblClabe').show();
		Ext.getCmp(PF+'txtSaldoiniAinter').setPosition(120,170);
		Ext.getCmp(PF+'txtSaldofinAinter').setPosition(120,210);
		Ext.getCmp(PF+'lblSaldoini').setPosition(0,170);
		Ext.getCmp(PF+'lblSaldofin').setPosition(0,210); 
	}
	
	function cambiarFecha(fecha)
	{	
		var mesArreglo=new Array(11);
		mesArreglo[0]="Jan";mesArreglo[1]="Feb";mesArreglo[2]="Mar";mesArreglo[3]="Apr";
		mesArreglo[4]="May";mesArreglo[5]="Jun";mesArreglo[6]="Jul";mesArreglo[7]="Aug";
		mesArreglo[8]="Sep";mesArreglo[9]="Oct";mesArreglo[10]="Nov";mesArreglo[11]="Dec";
		var mesDate=fecha.substring(4,7);
		var dia=fecha.substring(8,10);
		var anio=fecha.substring(11,15);
		for(var i=0;i<12;i=i+1)
		{
			if(mesArreglo[i]===mesDate)
			{
			var mes=i+1;
			if(mes<10)
				var mes='0'+mes;
			}
		}
		var fechaString=''+dia+'/'+mes+'/'+anio;
		return fechaString;
	}
	
	NS.contenedorTraspasos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
