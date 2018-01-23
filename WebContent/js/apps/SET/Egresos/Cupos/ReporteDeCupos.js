Ext.onReady(function(){

	Ext.QuickTips.init();	
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.Egresos.Cupos.ReporteDeCupos');
	NS.tabContId = apps.SET.tabContainerId;			
	var PF = apps.SET.tabID+'.';
	var sName = "";
	
	NS.idUsuario = apps.SET.iUserId;
	
	/* VARIABLES */
	
	NS.textProveedor='';
	
	function cambiarFecha(fecha)
	{
		var mesArreglo=new Array(11);
		mesArreglo[0]="Jan";mesArreglo[1]="Feb";mesArreglo[2]="Mar";mesArreglo[3]="Apr";
		mesArreglo[4]="May";mesArreglo[5]="Jun";mesArreglo[6]="Jul";mesArreglo[7]="Aug";mesArreglo[8]="Sep";
		mesArreglo[9]="Oct";mesArreglo[10]="Nov";mesArreglo[11]="Dec";
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
	
	/* [[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[ */
	//             CONTROLES BUSCAR 
	/* ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]] */
	
	
	NS.optionsBusqueda = new Ext.form.RadioGroup({
		id: PF + 'optionsBusqueda',
		name: PF + 'optionsBusqueda',
		x: 10,
		y: 0,
		columns: 1, 
		items: [  
	          {boxLabel: 'Empresas', name: 'optionsBusqueda', inputValue: 1, checked: true},
	          {boxLabel: 'Por Proveedor', name: 'optionsBusqueda', inputValue: 2}  
	          //{boxLabel: 'Clave de Propuestas', name: 'optionsBusqueda', inputValue: 3}
	          ],
	    listeners:{
			change:{
				fn:function(){
		
					var opcion = Ext.getCmp(PF + 'optionsBusqueda').getValue().getGroupValue();

					//PRESENTA UN DETERMINADO FRAME DE BUSQUEDA SEGUN OPCION
					switch( parseInt( opcion ) )
					{
					
						case 1:
							NS.fldSetEmpresas.setVisible(  true ) ;							
							NS.fldSetProveedor.setVisible(  false );
							NS.fldSetCvePropuestas.setVisible(  false );
							
							Ext.getCmp(NS.cmbEmpresa.getId()).setValue('');
							Ext.getCmp(NS.txtNoEmpresa.getId()).setValue('');
							NS.storeMaestro.removeAll();
							
							Ext.getCmp(NS.calFecIni.getId()).setValue(apps.SET.FEC_HOY);
							Ext.getCmp(NS.calFecFin.getId()).setValue(apps.SET.FEC_HOY);
							
							break;
							
						case 2:
							
							NS.fldSetEmpresas.setVisible(  false ) ;
							NS.fldSetProveedor.setVisible(  true );
							NS.fldSetCvePropuestas.setVisible(  false );
							NS.storeProveedor.removeAll();
							NS.storeMaestro.removeAll();
							
							Ext.getCmp(NS.cmbProveedor.getId()).setValue('');
							Ext.getCmp(NS.txtIdProveedor.getId()).setValue('');
							Ext.getCmp(NS.calFecIni.getId()).setValue(apps.SET.FEC_HOY);
							Ext.getCmp(NS.calFecFin.getId()).setValue(apps.SET.FEC_HOY);
							
							break;
						case 3:							
							NS.fldSetEmpresas.setVisible(  false ) ;
							NS.fldSetProveedor.setVisible(  false );
							NS.fldSetCvePropuestas.setVisible(  true );
							break;
							
					}//End switch
					
				}//End function
		
			} //End change
		
		} // End listeners	
	});
	
	
	/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	//  CONTROLES PARA OPCION POR EMPRESA
	/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	
	NS.lblEmpresa = new Ext.form.Label({
		text: 'EMPRESA:',
		x: 20,
		y: 10
	});
	
    NS.txtNoEmpresa = new Ext.form.NumberField({
    	name  : PF + 'txtNoEmpresa',
		id    : PF + 'txtNoEmpresa',                
        x: 20,
        y: 30,
        width: 50,
        listeners:{
			   change:{
		          		fn:function(){
				          		
		          			if( Ext.getCmp( PF + 'txtNoEmpresa' ).getValue() !== "" ){
		          			
		         				var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa', NS.cmbEmpresa.getId());
		         				PF + 'txtNoEmpresa'.focus();
		         				
		         			}else{
		         			
		         				Ext.getCmp(NS.cmbEmpresa.getId()).setValue('');			              				
		         			
		         			}//End if
				         			
				        }//End fn
				       
					  }//End change
    
				 }//End listeners
    
	});//End NS.txtNoEmpresa
    
 	
    NS.storeEmpresa = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: {
			noUsuario: 2
		},
		root: '',
		paramOrder:['noUsuario'],
		directFn: ReporteDeCuposAction.llenaComboEmpresas,
		idProperty: 'noEmpresa',
		fields: [
			{name: 'noEmpresa'},
			{name: 'nomEmpresa'}			
		]
	});
    
    NS.storeEmpresa.load();
    
    NS.cmbEmpresa = new Ext.form.ComboBox({  
	   name         : PF+'cmbEmpresa',
 	   id           : PF+'cmbEmpresa', 
 	   store        : NS.storeEmpresa,	   
 	   mode         : 'local',	   
 	   valueField   : 'noEmpresa',
 	   displayField : 'nomEmpresa',
 	   emptyText    : 'Seleccione una empresa',
 	   triggerAction: 'all',	            
 	   x: 81,
 	   y: 30,
       width: 310,
       listeners:{
			select:{
				    	fn:function() {
    	
				    		BFwrk.Util.updateComboToTextField(PF + 'txtNoEmpresa', NS.cmbEmpresa.getId());
				    		
				    	}//end function
    
					},//end select
			change:{
				    	fn:function(combo, newValue, oldValue) {						    		
				    		  
							 if( newValue == "" ){		
	              					              			
	              				Ext.getCmp(NS.txtNoEmpresa.getId()).setValue('');
	              				
	              			}//end if
	              													
				    	}//end function
				    	
					}//end change
					   
			}//end listeners
    });

	/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	//  CONTROLES PARA OPCION POR EMPRESA
	/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

	/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	//  CONTROLES PARA OPCION POR PROVEEDOR
	/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
		NS.lblProveedor = new Ext.form.Label({
			text: 'PROVEEDOR:',
			x: 20,
			y: 10
		});
		
		
       NS.txtIdProveedor = new Ext.form.TextField({
    	   x: 20,
           y: 30,
           width: 80,
           tabIndex: 3,
           id: PF+'txtIdProveedor',
           name: PF+'txtIdProveedor',
           listeners:{
    	   		change:{
    	   			fn:function(ov, nv){
    	   				
    	   				NS.cmbProveedor.reset();
    	   				
    	   				if( ov.getValue != '' ){
    	   					NS.storeProveedor.removeAll();
    	   					NS.storeProveedor.baseParams.noPersona= parseInt(ov.getValue());
    	   					NS.storeProveedor.baseParams.nomPersona = '';
    	   					NS.storeProveedor.load();
    	   				}

       				}
       			}//End change
    	   			
    	   	
       	   }//End listeners
    	   
       });
       
       NS.storeProveedor = new Ext.data.DirectStore({
   			paramsAsHash: false,
   			baseParams:{
    	    	noPersona:0,
    	    	nomPersona:''
   			},
   			root: '',
   			paramOrder:['noPersona','nomPersona'],
   			root: '',
   			directFn: ReporteDeCuposAction.llenaComboProveedor, 
   			idProperty: 'id', 
   			fields: [
   			         	{name: 'noPersona'},
   			         	{name: 'nomPersona'},
   			         ],
   			listeners: {
   				load: function(s, records){
   				
   					var myMask = new Ext.LoadMask( Ext.getBody(), {store: NS.storeProveedor, msg:"Cargando..."});
   					
   					if(records.length==null || records.length<=0) {
   						
   						Ext.Msg.alert('SET','No Existen proveeedores con ese nombre o id. ');   						
   						Ext.getCmp(PF + 'txtIdProveedor').setValue('');
   						NS.cmbProveedor.reset();
   						
   					}else {
   						
   						NS.cmbProveedor.setValue(records[0].get('nomPersona'));   						
   						Ext.getCmp(PF+'txtIdProveedor').setValue(records[0].get('noPersona'));
   						
   					}
   					
   					
   			}
   			
   		}
   	});
       
		NS.cmbProveedor = new Ext.form.ComboBox({			
			name: PF+'cmbProveedor'			
			,id: PF+'cmbProveedor'
			,store: NS.storeProveedor
			,valueField:'noPersona'
			,displayField:'nomPersona'
			,typeAhead: true
			,mode: 'local'   		
			,minChars: 0
			,selecOnFocus: true
			,forceSelection: true
			,pageSize: 10
			,x: 110
			,y: 30
			,width: 230
			,autocomplete: true
			,emptyText: 'Seleccione proveedor'
			,triggerAction: 'all'			
			,visible: false
			,editable: true
			,autocomplete: true
			,triggerAction: 'all',
			listeners:{
				select:{
					fn:function(combo, valor) {
									
							BFwrk.Util.updateComboToTextField(PF+'txtIdProveedor',NS.cmbProveedor.getId());
						
					}//End function
		
				},//End select				
				beforequery: function(qe){
					NS.textProveedor = '';
					NS.textProveedor= qe.combo.getRawValue();
				},
				change:{
					fn:function(nv,ov){
					
						if(ov == '' ){							
								Ext.getCmp(PF+'txtIdProveedor').setValue('');
						}
						
					}
				}
		
			}//End listeners
			
		});
		
		NS.btnSearchProveedor = new Ext.Button({
			id: PF+'btnSearchProveedor',
	        name: PF+'btnSearchProveedor',
			text: '...',
	        x: 345,
	        y: 30,
	        width: 20,
	        height: 22,
	        listeners:{
				click:{
						fn:function(){
							NS.storeProveedor.removeAll();
							var textProveedor = NS.textProveedor;
							var numProveedor =  parseInt( Ext.getCmp(PF + 'txtIdProveedor').getValue() ) ;
							
							if( textProveedor == '' && isNaN(numProveedor) )
							{
                    			Ext.Msg.alert('Información SET','El texto y caja de selección estan vacios.');
                    			return;
							}
															
							if ( isNaN(numProveedor) ){
								
								NS.storeProveedor.baseParams.noPersona= 0;
                    			NS.storeProveedor.baseParams.nomPersona = textProveedor;
                    			
							}
								
							if(textProveedor == ''){
								
								NS.storeProveedor.baseParams.noPersona= numProveedor;
	                    		NS.storeProveedor.baseParams.nomPersona = '';									
							}
							
                    		NS.storeProveedor.load();
                    		
							
						}//End function
		
				}//End click
		
			}//End listeners
	        		
		});
	/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	//  CONTROLES PARA OPCION POR PROVEEDOR
	/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	
	/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	//  CONTROLES PARA OPCION POR CVE CONTROL
	/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	   	
	/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	//  CONTROLES PARA OPCION POR CVE CONTROL
	/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
		
	/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	//  CONTROLES FIJOS
	/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	   NS.lblRangoDeFechas = new Ext.form.Label({
		  text:'RANGO DE FECHAS',
		  x:20,
		  y:0
	   });
	   
	   NS.calFecIni = new Ext.form.DateField({
		   	  id:PF + 'calFecIni',
		   	  name: PF + 'calFecIni',		   	  
			  x:20,
			  y:20,
			  width:110,
			  format:'d/m/Y',
			  allowBlank:false,
			  blankText:'Fecha inicial requerida',
			  value: apps.SET.FEC_HOY
		   });
	   
	   NS.calFecFin = new Ext.form.DateField({
		   	  id: PF + 'calFecFin',
		   	  name: PF + 'calFecFin',
			  x:180,
			  y:20,
			  width: 110,
			  format:'d/m/Y',
			  allowBlank: false,
			  blankText: 'Fecha final requerida',
		      value: apps.SET.FEC_HOY,
		      listeners:{
         			change:{
         				fn:function(caja,valor){
		   
         					if(Ext.getCmp(PF+'calFecIni').getValue()> valor)
							{
								Ext.Msg.alert('SET','La fecha inicial debe ser menor a la final');
								return;
							}					
         					
         				}//End function
         			}//End change
         		}//End Listeners
		   });
	   
	   NS.btnGrafica = new Ext.Button({
		   text: 'Grafica',
		   x: 115,
		   y: 53,
		   width: 80,
		   height: 22,
		   disabled: true,
		   id: PF + 'cmdGrafica',
		   name: PF + 'cmdGrafica',
		   listeners:{
	       	click:{
			   		fn:function(e){
						Ext.getCmp('panel3').show();
						Ext.getCmp('panel2').show();
						Ext.getCmp('panel1').show();
						Ext.getCmp('panel1').update('<img src="'+window.location.protocol+'//'+window.location.host+'/graficaSet/'+apps.SET.iUserId+'PropuestasPagadas'+sName+'PC.jpg" border="0"/>');
						Ext.getCmp('panel2').update('<img src="'+window.location.protocol+'//'+window.location.host+'/graficaSet/'+apps.SET.iUserId+'PropuestasPagadas'+sName+'BG.jpg" border="0"/>');
						Ext.getCmp('panel3').update('<img src="'+window.location.protocol+'//'+window.location.host+'/graficaSet/'+apps.SET.iUserId+'PropuestasPagadas'+sName+'LG.jpg" border="0"/>');
						Ext.Msg.alert('SET', 'Graficas listas');
		   			}
		   		 }
	   		}
	   });
	   
	   NS.btnBuscar = new Ext.Button({
		   text: 'Buscar',
		   x: 215,
		   y: 53,
		   width: 80,
		   height: 22,
		   id: PF + 'btnBuscar',
		   name: PF + 'btnBuscar',
		   listeners:{
	       	click:{
			   		fn:function(e){
		   				NS.buscar();
		   			}
		   		 }
	   		}
	   });
	   
		NS.buscar = function() {
			
			
			
			if((Ext.getCmp(PF+'calFecIni').getValue()=="") && (Ext.getCmp(PF+'calFecIni').getValue()=="")) {
				
				BFwrk.Util.msgShow('Debe establecer el rango de fechas','INFO');
				return;
			}
			
			var opcion =  Ext.getCmp(PF + 'optionsBusqueda').getValue().getGroupValue() ;
			
			NS.storeMaestro.baseParams.tipoBusqueda = parseInt(opcion != '' ? opcion : 0);
			NS.storeMaestro.baseParams.noEmpresa    = parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue() != '' ? Ext.getCmp(PF + 'txtNoEmpresa').getValue() : 0);
			NS.storeMaestro.baseParams.noProveedor  = parseInt(Ext.getCmp(PF + 'txtIdProveedor').getValue() != '' ? Ext.getCmp(PF + 'txtIdProveedor').getValue() : 0);
			NS.storeMaestro.baseParams.fecIni       = cambiarFecha('' + Ext.getCmp(PF+'calFecIni').getValue());
			NS.storeMaestro.baseParams.fecFin       = cambiarFecha('' + Ext.getCmp(PF+'calFecFin').getValue());
			NS.storeMaestro.load();
			
		}
	   
	/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	//  CONTROLES FIJOS
	/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */


	/* *********************************************************************************************** */
	//  FIELD SET INTERNOS SEGUN OPCION DE BUSQUEDA
	/* *********************************************************************************************** */	
	
	NS.fldSetEmpresas = new Ext.form.FieldSet({    	
        title: 'Empresas',
        x: 150,
        y: 0,
        layout: 'absolute',
        width: 450,
        height: 110,
        hidden:false,
        items:[
               	NS.lblEmpresa,
               	NS.txtNoEmpresa,
               	NS.cmbEmpresa
              ]
              
    });
	
		
	NS.fldSetProveedor = new Ext.form.FieldSet({    	
        title: 'Proveedor',
        x: 150,
        y: 0,
        layout: 'absolute',
        width: 450,
        height: 110,
        hidden:true,
        items:[
               	NS.lblProveedor,
               	NS.txtIdProveedor,
               	NS.cmbProveedor,
               	NS.btnSearchProveedor
              ]
              
    });
	
	
	NS.fldSetCvePropuestas = new Ext.form.FieldSet({    	
        title: 'Cve Propuesta',
        x: 150,
        y: 0,
        layout: 'absolute',
        width: 450,
        height: 110,
        hidden:true,
        items:[
              	
              ]
              
    });
	
	NS.fldSetControlesFijos = new Ext.form.FieldSet({    	
        title: 'Fijos',
        x: 610,
        y: 0,
        layout: 'absolute',
        width: 350,
        height: 110,        
        items:[
               	NS.lblRangoDeFechas,
               	NS.calFecIni,
               	NS.calFecFin,
               	NS.btnGrafica,
               	NS.btnBuscar
              ]
              
    });
	
	
	

	/* *********************************************************************************************** */
	//  FIELD SET INTERNOS SEGUN OPCION DE BUSQUEDA
	/* *********************************************************************************************** */
		
	
	/* [[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[ */
	//             CONTROLES BUSCAR 
	/* ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]] */
	
	
	
	/* [[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[ */
	//             CONTROLES RESULTADOS 
	/* ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]] */
	
	//Store para llenar el grid maestro
 	 NS.storeMaestro = new Ext.data.DirectStore({
 	 	paramsAsHash: false,
 	 	baseParams: {
			tipoBusqueda:0,
			noEmpresa:0,
			noProveedor:0,			
	        fecIni: apps.SET.FEC_HOY,
	        fecFin: apps.SET.FEC_HOY,
	        iUserId: NS.idUsuario},
		root: '',
		paramOrder:['tipoBusqueda','noEmpresa','noProveedor','fecIni','fecFin','iUserId'],
		directFn: ReporteDeCuposAction.getReporteCupos,
		fields: [
			{name: 'noEmpresa'},
			{name: 'nomEmpresa'},
			{name: 'idBanco'},
			{name: 'descBanco'},
			{name: 'idChequera'},
			{name: 'fechaPropuesta'},
			{name: 'fechaPago'},
			{name: 'importe'},
			{name: 'idFormaPago'},
			{name: 'descFormaPago'},
			{name: 'idFormaPago'},
			{name: 'noBeneficiario'},
			{name: 'nomBeneficiario'},
			{name: 'concepto'},
			{name: 'idBancoBenef'},
			{name: 'idChequeraBenef'},
			{name: 'noDocto'},
			{name: 'cveControl'}
		],
		listeners: {
			load: function(s, records){
			
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMaestro, msg:"Cargando..."});
				
				if(records.length==null || records.length<=0)
				{
					//Ext.getCmp('panel1').update('<img src="'+window.location.protocol+'//'+window.location.host+'/graficaSet/back.png" border="0"/>');
					//Ext.getCmp('panel2').update('<img src="'+window.location.protocol+'//'+window.location.host+'/graficaSet/back.png" border="0"/>');
					//Ext.getCmp('panel3').update('<img src="'+window.location.protocol+'//'+window.location.host+'/graficaSet/back.png" border="0"/>');
					Ext.getCmp(PF + 'cmdGrafica').setDisabled(true);
					Ext.Msg.alert('SET','No existen propuestas con los parametros de búsqueda');
					return;
				}
				else
				{
					//Graficas
		            Ext.getCmp(PF + 'cmdGrafica').setDisabled(false);
		            
		            if(NS.storeMaestro.baseParams.fecIni!="") {
		            	NS.storeMaestro.baseParams.fecIni = NS.storeMaestro.baseParams.fecIni.trim();
		            	NS.storeMaestro.baseParams.fecFin = NS.storeMaestro.baseParams.fecFin.trim();
						
						var sAux1 = NS.storeMaestro.baseParams.fecIni.replace("/", "");
						sAux1 = sAux1.replace("/", "");
						var sAux2 = NS.storeMaestro.baseParams.fecFin.replace("/", "");
						sAux2 = sAux2.replace("/", "");
						sName += sAux1 + sAux2;
					}
		            
					sName = NS.storeMaestro.baseParams.tipoBusqueda+""+NS.storeMaestro.baseParams.noEmpresa+""+NS.storeMaestro.baseParams.noProveedor+sAux1+sAux2;
				}
			}
		}
	});

	
  	//Para multiple seleccion en el grid
 	NS.seleccion = new Ext.grid.CheckboxSelectionModel
 	({
 		singleSelect: false
 	});

 	//Columnas del grid	    	
 	NS.columnas = new Ext.grid.ColumnModel
 	([
 	  	{header :'Empresa', width :180, sortable :true, dataIndex :'nomEmpresa'},
 	  	{header :'No Docto', width :60, sortable :true, dataIndex :'noDocto'},
	    {header :'Beneficiario', width :200, sortable :true, dataIndex :'nomBeneficiario'},
	    {header :'Importe', width :100, sortable :true, dataIndex :'importe', renderer: BFwrk.Util.rendererMoney},
	    {header :'Concepto', width :250, sortable :true, dataIndex :'concepto'},
	    {header :'Desc. Forma Pago', width :120, sortable :true, dataIndex :'descFormaPago'},	    
	    {header :'Id Chequera', width :100, sortable :true, dataIndex :'idChequera'},
	    {header :'Chequera Benef', width :100, sortable :true, dataIndex :'idChequeraBenef'},
 	  	{
 			header :'No. Empresa',width :60,sortable :true,dataIndex :'noEmpresa',renderer: function (value, meta, record) {
 	            meta.attr = 'style=' + record.get('color');
 	            return value;
 	        	}
 		},
 	  	{header :'Id Banco', width :75, sortable :true, dataIndex :'idBanco', renderer: function (value, meta, record) { 			
            meta.attr = 'style=' + record.get('color');
            return value;
            }
	    },
 		{
 			header :'Banco',width :100,sortable :true,dataIndex :'descBanco',renderer: function (value, meta, record) {
	            meta.attr = 'style=' + record.get('color');
	            return value;
	        	}
		},
	    {header :'Fecha propuesta',width :80,sortable :true,dataIndex :'fechaPropuesta'},
	    {header :'Fecha pago',width :80,sortable :true,dataIndex :'fechaPago'},
	    {header :'Forma Pago', width :80, sortable :true, dataIndex :'idFormaPago'},
	    {header :'Banco Benef',width :80,sortable :true,dataIndex :'idBancoBenef',hidden: false,renderer: function (value, meta, record) {
            meta.attr = 'style=' + record.get('color');
            return value;
        	}
	    },
	    {header :'Cve Control',width :80,sortable :true,dataIndex :'cveControl',hidden: false,renderer: function (value, meta, record) {
            meta.attr = 'style=' + record.get('color');
            return value;
        	}
	    }
 	  ]); 	

	//GRID DE DATOS
	 NS.gridMaestroPropuestas = new Ext.grid.GridPanel({
       store : NS.storeMaestro,       
       viewConfig: {emptyText: 'Resumen de propuestas'},
       x:0,
       y:0,
       cm: NS.columnas,
	   sm: NS.seleccion,
       width:960,
       height:200,
       frame:true
	 });
	 
	 /* [[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[ */
	//             CONTROLES RESULTADOS
	/* ]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]]] */

       
	
	
	/* {{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{ */
	//             FIELDSET BUSCAR 
	/* }}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}} */
    NS.fldSetBuscar = new Ext.form.FieldSet({    	
        title: 'Buscar',
        x: 10,
        y: 0,
        layout: 'absolute',
        width: 990,
        height: 150,
        items:[
              	NS.optionsBusqueda,              	
            	NS.fldSetEmpresas,
            	NS.fldSetProveedor,
            	NS.fldSetCvePropuestas,
            	NS.fldSetControlesFijos
            	
              ]
              
    });
    /* {{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{ */
	//             FIELDSET BUSCAR 
    /* }}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}} */
    
    
    
    /* {{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{ */
	//             FIELDSET RESULTADOS
    /* }}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}} */
    NS.fldSetResults = new Ext.form.FieldSet({    	
        title: 'Resultados',
        x: 10,
        y: 155,        
        layout: 'absolute',
        width: 990,
        height: 240,
        items:[
               	NS.gridMaestroPropuestas
               ]
    });
    /* {{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{ */
	//             FIELDSET RESULTADOS 
    /* }}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}} */

	NS.btnImprimir = new Ext.Button({
		text: 'Imprimir',
        x: 750,
        y: 400,
        width: 80,
        id: 'cmbImprimir',
        listeners: {
   			click: {
   				fn: function(e) {
   					NS.imprimirRepPropuestas();
   				}
   			}
   		}
	});
	
	NS.imprimirRepPropuestas = function() {
		
		var recSelectProp = NS.storeMaestro.data.items;
			
		if(recSelectProp.length == 0) {
			Ext.Msg.alert('SET','No existen propuestas para el reporte!!');
			return;
		}
		
		var opcion =  Ext.getCmp(PF + 'optionsBusqueda').getValue().getGroupValue() ;
		
		if ( opcion == 1 ){
			strParams = '?nomReporte=reporteCupos';
		}else{
			strParams = '?nomReporte=reporteCuposProvr';
		}
		
		strParams += '&'+'nomParam1=tipoBusqueda'; 
		strParams += '&'+'valParam1=' +  parseInt(opcion != '' ? opcion : 0);
		strParams += '&'+'nomParam2=noEmpresa';    
		strParams += '&'+'valParam2=' + parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue() != '' ? Ext.getCmp(PF + 'txtNoEmpresa').getValue() : 0);
		strParams += '&'+'nomParam3=noProveedor';  
		strParams += '&'+'valParam3=' + parseInt(Ext.getCmp(PF + 'txtIdProveedor').getValue() != '' ? Ext.getCmp(PF + 'txtIdProveedor').getValue() : 0);
		strParams += '&'+'nomParam4=fecIni';
		strParams += '&'+'valParam4=' + cambiarFecha('' + Ext.getCmp(PF + 'calFecIni').getValue());
		strParams += '&'+'nomParam5=fecFin';
		strParams += '&'+'valParam5=' + cambiarFecha('' + Ext.getCmp(PF + 'calFecFin').getValue());
		
		window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
		
	};
	
	
	
	
	/* {{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{ */
	//             FIELDSET GENERAL 
	/* }}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}} */
    NS.fldSetGeneral = new Ext.form.FieldSet({    	
        title: 'General',
        x: 20,
        y: 5,
        layout: 'absolute',
        width: 1030,
        height: 650,
        items:[
               NS.fldSetBuscar,
               NS.fldSetResults,
               NS.btnImprimir
               ]
    });
    /* {{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{ */
	//             FIELDSET GENERAL 
	/* }}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}} */
    

	
	/* (((((((((((((((((((((((((((((((((((((((((((( */
	//             FOMULARIO GENERAL 
	/* ))))))))))))))))))))))))))))))))))))))))))))) */
	NS.frmContentGeneral =new Ext.FormPanel({
	    title: 'Reporte de Cupos',
	    renderTo: NS.tabContId,
	    width: 1082,
	    height: 800,
	    padding: 10,
    	frame: true,
    	autoScroll: true,
	    layout: 'absolute',	    
	    items: [
	            	NS.fldSetGeneral
	           ]
	});
	
	/* (((((((((((((((((((((((((((((((((((((((((((( */
	//             FOMULARIO GENERAL 
	/* ))))))))))))))))))))))))))))))))))))))))))))) */
	
});