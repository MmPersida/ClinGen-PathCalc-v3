function openEvidenceTagPopUp(divBtnElem){
    var rowID = divBtnElem.getAttribute("data-value");
    let pInDiv = divBtnElem.children[1];
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

function editEvideneceData(trElem, actionStatus){
    removeUpdateEditTR();

    var tdList = trElem.children;
    for(let i in tdList){
        let tdElem = tdList[i];
        var currentText = tdElem.innerText;
        tdElem.innerText = '';
        if(i == 0 || i == 1){
            var optionElem = null;
            var selectElem = document.createElement('select');
            selectElem.style.width = '90%'; 
            //selectElem.addEventListener("change", function(){  });
            if(i == 0){
                var ulElemWithCelTagValues = document.getElementById(getTagValueIds(currentEvidenceCellId));
                var liElemArrya = ulElemWithCelTagValues.childNodes;
                liElemArrya = Array.from(liElemArrya);
                selectElem.id = 'selectEvidenceCode';  
                for(let j in liElemArrya){
                    var li = liElemArrya[j];
                    optionElem = document.createElement('option');
                    optionElem.id = 'optVal_'+li.innerHTML;
                    optionElem.innerText = li.innerHTML;
                    optionElem.style.color = li.style.color;
                    if(optionElem.style.color == 'red'){
                        optionElem.disabled = 'true';
                    }                
                    selectElem.appendChild(optionElem);   
                }
            }
            if(i == 1){
                selectElem.id = 'selectEvidenceStatus';                       
                optionElem = document.createElement('option');
                     optionElem.innerText = 'On';
                selectElem.appendChild(optionElem);
                optionElem = document.createElement('option');
                     optionElem.innerText = 'Off';
                selectElem.appendChild(optionElem);  
            }
            tdElem.appendChild(selectElem);
        }else if(i == 2){
            var input = document.createElement('input');
            input.id="evidenceSummaryInp"
            input.type = "text";
            input.placeholder = "Provide optional summary";
            input.style.width = '100%'; 
            tdElem.appendChild(input);
        }    
    }

    var newTRforBtns = document.createElement('tr');
    newTRforBtns.id = "update_edit_tr";
        var singleTD = document.createElement('td');
        singleTD.colSpan = "3";
            var containerDiv = document.createElement('div');
            containerDiv.style.width = '100%';
            containerDiv.style.display = 'flex';
            containerDiv.style.flexDirection = 'row';
            containerDiv.style.alignItems = 'center';
            containerDiv.style.justifyContent = 'center';
            containerDiv.style.borderBottom = "1px solid rgb(30, 150, 200)";
            containerDiv.style.paddingBottom = '3px';
                var updateBtn = document.createElement('div');
                updateBtn.className = "calcMainMenuBtns evidenceMenuBtn";
                updateBtn.style.fontSize = '14px';
                updateBtn.innerHTML = "Update";
                updateBtn.value = trElem.id+"|"+actionStatus;
                updateBtn.addEventListener("click", function(){ updateEvidenceData(this); });
            containerDiv.appendChild(updateBtn);    
                var cancelBtn = document.createElement('div');
                cancelBtn.className = "calcMainMenuBtns evidenceMenuBtn";
                cancelBtn.style.fontSize = '14px';
                cancelBtn.innerHTML = "Cancel" 
                cancelBtn.value = trElem.id;
                cancelBtn.addEventListener("click", function(){ removeUpdateEditTR(this); });
            containerDiv.appendChild(cancelBtn);
        singleTD.appendChild(containerDiv);
    newTRforBtns.appendChild(singleTD);
    trElem.parentNode.insertBefore(newTRforBtns, trElem.nextSibling);
}

function removeUpdateEditTR(cancelDivBtnElem){
    var row = document.getElementById("update_edit_tr");
    if(row == null){
        return;
    }
    row.parentElement.removeChild(row);

    displayEvidenceCodesTable(); 
}

function markSelectedEvidenceTagRow(trElem){ 
    let evidenceTagTRList = trElem.parentElement.children;
    if(evidenceTagTRList.length > 2){
        let n = evidenceTagTRList.length; 
        for(let i=1; n>i; i++){
            let trID = "evidenceTR_"+(Number(i));
            let evdTR = document.getElementById(trID);
            if(evdTR.style.backgroundColor != "white"){
                evdTR.style.backgroundColor = "white";
            }
        }
    }

    trElem.style.backgroundColor = "rgb(232,232,232)";
    selectedEvidenceTagRowId = trElem.id; //this is neccessary for the removal of the selected tab in the popup window
}

