function changeSelectState(checkbox, rowIndex, hiddenInputId){
	var selections = document.getElementById(hiddenInputId);
	var row = checkbox.parentNode.parentNode.parentNode.parentNode;
	if(checkbox.checked){
		if(selections.value.indexOf(","+rowIndex+",") == -1){
			row.className += ' x-grid3-row-selected';
			selections.value += rowIndex+",";
		}
	}else{
		row.className = row.className.replace(' x-grid3-row-selected','');
		selections.value = selections.value.replace(","+rowIndex+"," ,",");
	}
}

function selectAllRows(headerCheckbox, gridId, hiddenInputId) {
	var checkboxes = document.getElementById(gridId).getElementsByTagName("input");
	var selections = document.getElementById(hiddenInputId);
	for(var i=0; i<checkboxes.length; i++) {
		var checkbox = checkboxes[i];
		if(checkbox != headerCheckbox && checkbox.type.toLowerCase() == "checkbox"){
			var row = checkbox.parentNode.parentNode.parentNode.parentNode;
			if(headerCheckbox.checked){
				if(selections.value.indexOf(","+checkbox.value+",") == -1){
					row.className += ' x-grid3-row-selected';
					selections.value += checkbox.value+",";
				}
			}else{
				row.className = row.className.replace(' x-grid3-row-selected','');
				selections.value = ",";
			}
			checkbox.checked = headerCheckbox.checked;
		}
	}
}