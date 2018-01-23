// funcion para mantener fija la primera coluna del Checkbox
		// div : indica sobre que grid se removera la clases para ajustar la posicion y permitir el reacomodo
		// vleft : refiere al objeto de donde se tomara referencia para mantener posicion fija
		// ajusteOffset : sirve para que la posicion del objeto se manteja  con respecto a la posicion oroginal
						// en caso de que no se requiera colocar 0 u omitirla ne la definicion de vLeft
		// toOffeset : Sera la referencia sobre el objeto el cual se aplicara el offset
		//scrollerClass : la clase sobre la cual se aplicara Scroll
		//ex. staticCheck("div [id*='gridPropuesta'] div","div [id*='gridPropuesta']",2,".x-grid3-scroller");
		function staticCheck(div,toOffeset,ajusteOffset,scrollerClass,header){
			//var div ="div [id*='gridPropuesta'] div";
			//var toOffeset ="div [id*='gridPropuesta']";
			//var ajusteOffset = 2;
			//var obj = $("div [id*='contMovimientos'] div div div div div div")[1]
			var  vLeft = $(toOffeset).offset().left+(ajusteOffset-2);
			$(scrollerClass).scroll(function(){
				var  sLeft =$('.x-grid3-col-checker').offset().left;
				$(div).removeClass('x-grid3-cell-inner');
				$('.x-grid3-cell-first').attr('style','position:absolue');
				$('.x-grid3-row-checker').attr('style','position:absolue');
				if(header)
					$('.x-grid3-hd-checker').offset({left:vLeft });
				
				$('.x-grid3-col-checker').offset({left:vLeft });
				//alert(sLeft)
				if(vLeft == $(toOffeset).offset().left)
					$('.x-grid3-col-checker').addClass('classScrollGral')
				else
					$('.x-grid3-col-checker').removeClass('classScrollGral')
			});
		}