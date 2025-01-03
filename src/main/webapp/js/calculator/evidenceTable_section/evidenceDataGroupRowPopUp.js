function openEvidenceTagPopUp(divBtnElem){
    var rowID = divBtnElem.getAttribute("data-value");
    let pInDiv = divBtnElem.childNodes[1];
    let textVal = pInDiv.innerText.toLowerCase();
    document.getElementById("openEvidenceTagModal").click();
    document.getElementById("evidenceTagPopUpTitle").innerHTML = 'Edit Evidence Tags for: <span style="text-transform: capitalize;"><i>'+textVal+'</i></span>';

    renderEvidenceRowGroupTable(textVal, rowID);
}

function renderEvidenceRowGroupTable(textVal, rowID){
    var evidenceRowGroupTable = document.getElementById("evidenceRowGroupTable");
    clearSelectChooser(evidenceRowGroupTable);

    var eTableGroup = null; 
    var tr = null; 
    var th = null; 
    var td = null; 
    var button = null; 
    var input = null;
    var div = null;
    var p = null;
    var li = null;
   
    let bedKeys = Object.keys(basicEvidenceData_row);
    for(let bedKey in bedKeys){
        let eTableRowName = bedKeys[bedKey];
        var eTableRow = basicEvidenceData_row[eTableRowName];
        textVal = textVal.toUpperCase()
        if(eTableRowName == textVal){
            eTableGroup = eTableRow;
            break;
        }
    }

    if(eTableGroup == null){
        alert("Error: Unable to get evidenec values for this table row!")
        return;
    }

    //addHeaderToTable
    tr = document.createElement('tr');
        th = document.createElement('th');
        th.className = "editEvidenceTableExpandClm";
    tr.appendChild(th);
        th = document.createElement('th');
        th.className = "editEvidenceTableAddClm";
        th.innerText = "Add/Remove Tags";
    tr.appendChild(th);
        th = document.createElement('th');
        th.className = "editEvidenceTableAddClm";
        th.innerText = "Tag";
    tr.appendChild(th);
        th = document.createElement('th');
        th.className = "editEvidenceTableAddClm";
        th.innerText = "Summary";
    tr.appendChild(th);
    evidenceRowGroupTable.appendChild(tr);

    //add tag values that are valid for this evidence table row 
    var modifiedEvidenceTags = [];
    var evidenceValues = eTableGroup.evidenceValues;
    n = evidenceValues.length;
    for(var i=0; i<n; i++){
        var eTagVal = evidenceValues[i];
        if(evidenceTagDataObj[eTagVal].tagDescriptor == "Stand Alone"){
            continue;
        }
        var eTagObj = evidenceTagDataObj[eTagVal];
        modifiedEvidenceTags = modifiedEvidenceTags.concat(determineModifiedEvidenceTags(eTagObj, basicEvidenceTagTypes_columns[eTagObj.tagType].tagValues, eTagVal));
    }

    //add the addditional  evidence value tags to the basic ones for this row
    if(modifiedEvidenceTags.length > 0){
        evidenceValues = evidenceValues.concat(modifiedEvidenceTags);
    }

    n = evidenceValues.length;
    for(var i=0; i<n; i++){
        var eVal = evidenceValues[i];

        tr = document.createElement('tr');
            //first column, + indicator button, inert for now
            td = document.createElement('td');
            td.className = "assertionTDAlignCenter";
                button = document.createElement('button');
                button.className = "expandTableBtn";
                button.value = (i+1);
                button.innerHTML = "+";
            td.appendChild(button);
        tr.appendChild(td);   
            //add/remove 
            td = document.createElement('td');
            td.className = "assertionTDAlignCenter";
                input = document.createElement('input');
                input.id = getEditEvdcTagsTablePopupIds(rowID, eVal);
                input.type = "checkbox";
                input.name = "assertTagsCB";
            td.appendChild(input);
        tr.appendChild(td); 
            //tag     
            td = document.createElement('td');   
                div = document.createElement('div');   
                div.className = "evidTagTableTD";
                    p = document.createElement('p'); 
                    p.className = "evidenceTagLabel_"+(i+1);
                    p.style.color = "rgb(30, 150, 200)";
                    p.innerText = eVal;
                div.appendChild(p);
                    p = document.createElement('p'); 
                    p.style.backgroundColor = "rgba(30, 149, 200, 0.158)";
                    p.style.color = "black";
                    p.innerText = "0";
                div.appendChild(p);
            td.appendChild(div);  
        tr.appendChild(td);  
            //summary
            td = document.createElement('td'); 
            if(evidenceTagDataObj[eVal] != null){
                td.innerText = evidenceTagDataObj[eVal].summary; 
            }else{
                td.innerText = determineSummaryForModifiedEvidTag(eVal);
            }
        tr.appendChild(td);      
        evidenceRowGroupTable.appendChild(tr);    
    }
}

function getEditEvdcTagsTablePopupIds(tableRowIndex, evidenceTag){
    return 'evt_'+tableRowIndex+'_'+evidenceTag
}
