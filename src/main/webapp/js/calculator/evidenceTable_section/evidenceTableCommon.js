function determineModifiedEvidenceTags(evidenceTagType, basicEvidenceTagTypes, evidenceTagVal){
    var modifiedEvidenceTags = [];
    var j = basicEvidenceTagTypes.length;
    for(var k=0; k<j; k++){
        var evidenceType = basicEvidenceTagTypes[k];
        if(evidenceTagType.tagDescriptor != evidenceType && evidenceType != "Stand Alone"){
            modifiedEvidenceTags.push(evidenceTagVal+" - "+evidenceType);
        }    
    }
    return modifiedEvidenceTags;
}

function getValidEvidenceTagsForThisCell(allValidEvidenceTagsForThisRow, evidenceColumnGroupName, curentColumnName){
    var eTagObj = null;
    var modifiedEvidenceTags = [];
    var validTags = [];

    var n = allValidEvidenceTagsForThisRow.length;
    for(var i=0; i<n; i++){
        eTagObj =  evidenceTagDataObj[allValidEvidenceTagsForThisRow[i]];

        if(evidenceColumnGroupName != eTagObj.tagType){
            continue;
        }

        if(curentColumnName == eTagObj.tagDescriptor){
            validTags.push(allValidEvidenceTagsForThisRow[i]);
        }

        if(eTagObj.tagDescriptor == "Stand Alone"){
            continue;
        }

        modifiedEvidenceTags = determineModifiedEvidenceTags(eTagObj, basicEvidenceTagTypes_columns[eTagObj.tagType].tagValues, allValidEvidenceTagsForThisRow[i]);
        var j = modifiedEvidenceTags.length;
        for(var k=0; k<j; k++){
            var currentTagModifier = (modifiedEvidenceTags[k].split(" - "))[1];
            if(currentTagModifier == curentColumnName){
                validTags.push(modifiedEvidenceTags[k]);
            }
        }
    }
    return validTags;
}

function determineSummaryForModifiedEvidTag(eVal){
    var eValArray = eVal.split(" - ");

    var basicTagDescriptor = evidenceTagDataObj[eValArray[0]].tagDescriptor;
    var basictagType = evidenceTagDataObj[eValArray[0]].tagType;
    var evidTagDescriptor = basicEvidenceTagTypes_columns[basictagType].tagValues;
    var j = evidTagDescriptor.length;
    var basicVal = 0;
    var additonalVal = 0;
    for(var k=0; k<j; k++){
        var evidDescirpot = evidTagDescriptor[k];
        if(basicTagDescriptor == evidDescirpot){
            basicVal = k;
        }
        if(eValArray[1] == evidDescirpot){
            additonalVal = k;
        }
    }

    if(basicVal > additonalVal){
        return eValArray[0]+" downgraded in strength to "+eValArray[1];
    }else if(basicVal < additonalVal){
        return eValArray[0]+" upgraded in strength to "+eValArray[1];
    } 
}

function editEvideneceAndLinksData(trElem, actionStatus){
    removeNewTagAndUpdateBtnRowsIfExists(null);

    let linkCodes = ["Supports","Unknown","Disputes"];

    var optionElem = null;
    var selectElem = null;

    let thisIsforEvidenceTags = false;
    let thisIsforEvidenceLinks = false;

    let n = trElem.childNodes.length;
    for(let i=0; n>i; i++){
        let tdElem = trElem.childNodes[i];
        let currentText = tdElem.innerText;
        tdElem.innerText = '';

        if(tdElem.id == "evidenceTagTD"){
            thisIsforEvidenceTags = true;

            selectElem = document.createElement('select');
            selectElem.id = 'selectEvidenceCode';  
            selectElem.style.width = '90%';

            if(actionStatus == 'edit'){
                optionElem = document.createElement('option');
                optionElem.innerText = currentText;
                selectElem.appendChild(optionElem); 
                selectElem.disabled = "true";
            }else if(actionStatus == "add"){
                //get all of the valid elements for this cell and disable (non-selectable) the one marked red
                var ulElemWithCelTagValues = document.getElementById(getTagValueIds(currentEvidenceCellId));
                var liElemArrya = ulElemWithCelTagValues.childNodes;
                liElemArrya = Array.from(liElemArrya); //get the valid evidence tag for this cell as an array

                for(let j in liElemArrya){
                    var li = liElemArrya[j];
                    optionElem = document.createElement('option');
                    optionElem.id = 'optVal_'+li.innerHTML;
                    optionElem.innerText = li.innerHTML;
                    optionElem.style.color = li.style.color;
                    if(optionElem.style.color == 'red'){
                        optionElem.disabled = 'true';
                    }else if(addedEvidenceTags != null && addedEvidenceTags.length > 0){
                        for(let indx in addedEvidenceTags){
                            var aet = addedEvidenceTags[indx];
                            if(li.innerHTML == aet.evdTag){
                                optionElem.style.backgroundColor = 'yellow';
                                optionElem.disabled = 'true';
                                break;
                            }
                        }
                    }                
                    selectElem.appendChild(optionElem);   
                }
            }
            tdElem.appendChild(selectElem);

        }else if(tdElem.id == "evidenceStatusTD"){
            selectElem = document.createElement('select');
            selectElem.id = 'selectEvidenceStatus'; 
            selectElem.style.width = '90%';                                
                optionElem = document.createElement('option');
                 optionElem.innerText = 'On';
            selectElem.appendChild(optionElem);
                optionElem = document.createElement('option');
                 optionElem.innerText = 'Off';
            selectElem.appendChild(optionElem); 
            tdElem.appendChild(selectElem);
            
        }else if(tdElem.id == "evidenceSummaryTD"){
            var input = document.createElement('input');
            input.id="evidenceSummaryInp"
            input.type = "text";
            input.placeholder = "Provide optional summary";
            input.style.width = '100%'; 
            input.value = currentText;
            tdElem.appendChild(input);

        }else if(tdElem.id == "linkValueTD"){
            thisIsforEvidenceLinks = true;

            var input = document.createElement('input');
            input.id="linkValueInp"
            input.type = "text";
            input.placeholder = "Set link value";
            input.style.width = '100%'; 
            input.value = currentText;
            tdElem.appendChild(input);

        }else if(tdElem.id == "linkCodeTD"){
            selectElem = document.createElement('select');
            selectElem.id = 'selectLinkCode';  
            selectElem.style.width = '90%';

            for(let l in linkCodes){
                var lc = linkCodes[l];
                optionElem = document.createElement('option');
                optionElem.innerText = lc;
                optionElem.value = lc;
                if(optionElem.innerText == currentText){
                    optionElem.selected = "true";
                }                
                selectElem.appendChild(optionElem);   
            }
            tdElem.appendChild(selectElem);
        }else if(tdElem.id == "linkCommentTD"){
            var input = document.createElement('input');
            input.id="linkCommentInp"
            input.type = "text";
            input.placeholder = "Provide optional comment";
            input.style.width = '100%'; 
            input.value = currentText;
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
                updateBtn.value = trElem.id;

                if(thisIsforEvidenceTags){
                    updateBtn.addEventListener("click", function(){ updateEvidenceData(this); });
                }else if(thisIsforEvidenceLinks){
                    updateBtn.addEventListener("click", function(){ updateEvdLinksData(this); });
                }               
            containerDiv.appendChild(updateBtn);    

                var cancelBtn = document.createElement('div');
                cancelBtn.id = "cancelUpdateBtn";
                cancelBtn.className = "calcMainMenuBtns evidenceMenuBtn";
                cancelBtn.style.fontSize = '14px';
                cancelBtn.innerHTML = "Cancel" 
                cancelBtn.value = trElem.id;
            
                if(actionStatus == 'edit'){
                    cancelBtn.addEventListener("click", function(){ 
                        if(thisIsforEvidenceTags){
                            removeUpdateBtnRowIfExistsAnndResetTagRow(this, 'evidence');
                        }else if(thisIsforEvidenceLinks){
                            removeUpdateBtnRowIfExistsAnndResetTagRow(this, 'link');
                        }
                    });
                }else if(actionStatus == "add"){
                    cancelBtn.addEventListener("click", function(){ removeNewTagAndUpdateBtnRowsIfExists(this); });
                }
           
            containerDiv.appendChild(cancelBtn);

        singleTD.appendChild(containerDiv);
    newTRforBtns.appendChild(singleTD);
    trElem.parentNode.insertBefore(newTRforBtns, trElem.nextSibling);
}

function removeUpdateBtnRowIfExistsAnndResetTagRow(btnElem, valueType){
    var editBtnsRow = document.getElementById("update_edit_tr");
    if(editBtnsRow != null){
        editBtnsRow.parentElement.removeChild(editBtnsRow);
    }

    if(btnElem == null){
        return;
    }
    //get the row that was edited
    var newTRow = document.getElementById(btnElem.value.trim());
    if(newTRow == null){
        return;
    }

    //get the inputed/selected values for evidence tags or their links
    if(valueType == "evidence"){
        var newEvCode = document.getElementById("selectEvidenceCode").value.trim();
        var newEvStatus = document.getElementById("selectEvidenceStatus").value.trim();
        var newEvSummary = document.getElementById("evidenceSummaryInp").value.trim();

        clearSelectChooser(newTRow);
            td = document.createElement('td');
            td.id = "evidenceTagTD";
            td.className = "editCellTableTagClm";
            td.innerText = newEvCode;
        newTRow.appendChild(td);
            td = document.createElement('td');
            td.id = "evidenceStatusTD";
            td.className = "editCellTableStatusClm";
            td.innerText = newEvStatus;
        newTRow.appendChild(td);
            td = document.createElement('td');
            td.id = "evidenceSummaryTD";
            td.className = "editCellTableSummaryClm";
            td.innerText = newEvSummary;
        newTRow.appendChild(td);

    }else if(valueType == "link"){
        var newLinkValue = document.getElementById("linkValueInp").value.trim(); //linkValueTD
        var newLinkCode = document.getElementById("selectLinkCode").value.trim(); //linkCodeTD
        var newLinkComment = document.getElementById("linkCommentInp").value.trim(); //linkCommentTD
        
        clearSelectChooser(newTRow);
            td = document.createElement('td');
            td.id = "linkValueTD";
            td.className = "editCellTableTagClm";
            td.innerText = newLinkValue;
        newTRow.appendChild(td);
            td = document.createElement('td');
            td.id = "linkCodeTD";
            td.className = "editCellTableStatusClm";
            td.innerText = newLinkCode;
        newTRow.appendChild(td);
            td = document.createElement('td');
            td.id = "linkCommentTD";
            td.className = "editCellTableSummaryClm";
            td.innerText = newLinkComment;
        newTRow.appendChild(td);
    }
}

function removeNewTagAndUpdateBtnRowsIfExists(btnElem){
    var editBtnsRow = document.getElementById("update_edit_tr");
    if(editBtnsRow != null){
        editBtnsRow.parentElement.removeChild(editBtnsRow);

    }
    if(btnElem == null){
        return;
    }
    var newInputRow = document.getElementById(btnElem.value.trim());
    if(newInputRow != null){
        newInputRow.parentElement.removeChild(newInputRow);
    }  
}

function markSelectedEvidenceTagOrLinkRow(trElem, type){ 
    let trList = trElem.parentElement.childNodes;
    if(trList != null && trList.length > 0){
        let n = trList.length; 
        for(let i=0; n>i; i++){
            let tr = trList[i];
            if(tr != null && tr.id != '' && tr.style.backgroundColor != "white"){
                tr.style.backgroundColor = "white";
                break;
            }
        }
    }

    trElem.style.backgroundColor = "rgb(232,232,232)";
    if(type == 'tag'){
        selectedEvidenceTagRowId = trElem.id; //this is neccessary for the removal of the selected tag (row) in the popup window
    }else if(type == 'link'){
        selectedEvidenceLinkRowId = trElem.id; //this is neccessary for the removal of the selected link (row) in the popup window
    } 
}
