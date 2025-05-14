function openEvidenceTagPopUp(divBtnElem){
    var rowID = divBtnElem.getAttribute("data-value");
    let pInDiv = divBtnElem.childNodes[1];
    let textVal = pInDiv.innerText.toLowerCase();
    document.getElementById("openEvidenceTagModal").click();
    document.getElementById("evidenceTagPopUpTitle").innerHTML = 'Evidence Tags for: <span style="text-transform: capitalize;"><i>'+textVal+'</i></span>';

    renderEvidenceRowGroupTable(textVal, rowID);
}

function renderEvidenceRowGroupTable(textVal, rowID){
    var evidenceRowGroupTable = document.getElementById("evidenceRowGroupTable");
    clearSelectChooser(evidenceRowGroupTable);

    var evidenceValues = null; 
    var tr = null; 
    var th = null; 
    var td = null; 
    //var button = null; 
    var input = null;
    var div = null;
    var p = null;
    //var li = null;
   
    //get all possible basic evidence tag for this group based on the name of it, BS2, BS2, etc.
    let bedKeys = Object.keys(basicEvidenceData_row);
    for(let bedKey in bedKeys){
        let eTableRowName = bedKeys[bedKey];
        var eTableRow = basicEvidenceData_row[eTableRowName];
        textVal = textVal.toUpperCase()
        if(eTableRowName == textVal){
            evidenceValues = eTableRow.evidenceValues;
            break;
        }
    }

    if(evidenceValues == null){
        alert("Error: Unable to get evidenec values for this table row!")
        return;
    }

    //add modified tag values like BS1-Supporting, BP1-Strong, etc. that are valid for this evidence table row 
    var modifiedEvidenceTags = [];
    var n = evidenceValues.length;
    for(var i=0; i<n; i++){
        var eTagVal = evidenceValues[i];
        if(evidenceTagDataObj[eTagVal].tagDescriptor == "Stand Alone"){
            continue;
        }
        var eTagObj = evidenceTagDataObj[eTagVal];
        modifiedEvidenceTags = modifiedEvidenceTags.concat(determineModifiedEvidenceTags(eTagObj, basicEvidenceTagTypes_columns[eTagObj.tagType].tagValues, eTagVal));
    }

    //add the modiified evidence tags to the basic ones for this row, all in one array 
    if(modifiedEvidenceTags.length > 0){
        evidenceValues = evidenceValues.concat(modifiedEvidenceTags);
    }

    var usedEvdTagsFullName = getUsedEvdTagsForThisGroupType(rowID);
    var blockedEvdTagsFullName = getBlockedEvdTagsForUsedOnes(usedEvdTagsFullName);

    //table header
    tr = document.createElement('tr');
    /*
        th = document.createElement('th');
        th.className = "editEvidenceTableExpandClm";
    tr.appendChild(th);
    */
        th = document.createElement('th');
        th.className = "editEvidenceTableAddClm";
        th.innerText = "Status";
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


    //process all of the evd. tags (basic and modified) for this row/grouop
    n = evidenceValues.length;
    for(var i=0; i<n; i++){
        var eVal = evidenceValues[i];

        var evdTagIsUsed = isThisEvdTagUsedOrBlocked(usedEvdTagsFullName, eVal);
        var evdTagIsBlocked = isThisEvdTagUsedOrBlocked(blockedEvdTagsFullName, eVal);
        
        tr = document.createElement('tr');
        /*
            //first column, + indicator button, inert for now
            td = document.createElement('td');
            td.className = "assertionTDAlignCenter";
                button = document.createElement('button');
                button.className = "expandTableBtn";
                button.value = (i+1);
                button.innerHTML = "+";
            td.appendChild(button);
        tr.appendChild(td); 
        */
            //add/remove 
            td = document.createElement('td');
            td.className = "assertionTDAlignCenter";
            /*
                input = document.createElement('input');
                input.id = getEditEvdcTagsTablePopupIds(rowID, eVal);
                input.type = "checkbox";
                input.name = "assertTagsCB";
                if(evdTagIsUsed){
                    input.checked = true;
                }else if(evdTagIsBlocked){
                    input.disabled = true;
                    tr.style.backgroundColor = "lightgray";
                }*/
                p = document.createElement('p'); 
                p.style.margin = '0px';
                if(evdTagIsUsed){
                    p.innerText = "Added";
                }else if(evdTagIsBlocked){
                    p.innerText = "Blocked";
                }else{
                    p.innerText = "Unused";
                }
            td.appendChild(p);
        tr.appendChild(td); 
            //tag     
            td = document.createElement('td');   
                div = document.createElement('div');   
                div.className = "evidTagTableTD";
                    p = document.createElement('p'); 
                    p.className = "evidenceTagLabel_"+(i+1);
                    if(evdTagIsUsed || evdTagIsBlocked){
                        p.style.color = "red";
                    }else{
                        p.style.color = "rgb(30, 150, 200)";
                    }
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
                var evdTagDescDiv = document.createElement('div');
                evdTagDescDiv.style.padding = "0px";
                evdTagDescDiv.style.margin = "0px";
                evdTagDescDiv.style.maxHeight = "150px";
                evdTagDescDiv.style.overflowY = "auto";


                let eValArray = eVal.split(" - ");
                let basicEvdTag = eValArray[0].trim();
                if(evidenceTagDataObj[basicEvdTag] != null){
                    evdTagDescDiv.innerText = evidenceTagDataObj[basicEvdTag].summary; 
                }else{
                    evdTagDescDiv.innerText = determineSummaryForModifiedEvidTag(eVal);
                }
            td.appendChild(evdTagDescDiv);
        tr.appendChild(td);      
        evidenceRowGroupTable.appendChild(tr);    
    }
}

function getEditEvdcTagsTablePopupIds(tableRowIndex, evidenceTag){
    return 'evt_'+tableRowIndex+'_'+evidenceTag
}

function getUsedEvdTagsForThisGroupType(rowID){
    let usedEvdTagsFullName = [];
    let bettKeys = Object.keys(basicEvidenceTagTypes_columns);
    var cellFullId;
    var c = 0
    for(let bettKey in bettKeys){ 
        let columnGroupName = bettKeys[bettKey];  
        var eColumnNames = basicEvidenceTagTypes_columns[columnGroupName].tagValues;
       
        var m = eColumnNames.length;
        for(var v=0; v<m; v++){    
            currentColumnName = eColumnNames[v];
            cellFullId = rowID+"_"+c+"_"+v;

            var evidenceDataPerCell = pathogenicityEvidencesDoc[cellFullId];
            if(evidenceDataPerCell != null){   
                let evidenceTags = evidenceDataPerCell.evidenceTags;
                for(let i in evidenceTags){
                    /*
                    let obj = {
                        "eName": evidenceTags[i].evdTag,
                        "eSummary": evidenceTags[i].evdSummary
                    }*/
                    usedEvdTagsFullName.push(evidenceTags[i].evdTag);
                } 
            }
        }
        c++
    }
    return usedEvdTagsFullName;
}

function getBlockedEvdTagsForUsedOnes(usedEvdTagObj){
    let blockedEvdTagsFullName = [];
    for(let e in usedEvdTagObj){
        let evdTagFullName = usedEvdTagObj[e];
        if(evdTagFullName == "BA1"){
            continue;
        }
        let eValArray = evdTagFullName.split(" - ");
        let basicEvdTag = eValArray[0].trim();

        let evdTagObj = evidenceTagDataObj[basicEvdTag];
        let tagValues = basicEvidenceTagTypes_columns[evdTagObj.tagType].tagValues;
        let addedBacisEType = false;
        for(var t in tagValues){
            let modifiedTagValue = basicEvdTag+" - "+tagValues[t];

            if(eValArray.length == 1){
                blockedEvdTagsFullName.push(modifiedTagValue);
            }else if(eValArray.length == 2 && modifiedTagValue != evdTagFullName){
                if(!addedBacisEType){
                    blockedEvdTagsFullName.push(basicEvdTag);
                    addedBacisEType = true;
                }
                blockedEvdTagsFullName.push(modifiedTagValue);
            }
        }
    }
    return blockedEvdTagsFullName;
}

function isThisEvdTagUsedOrBlocked(usedOrBlockedEvdTagsFullName, eVal){
    for(let i in usedOrBlockedEvdTagsFullName){
        let val = usedOrBlockedEvdTagsFullName[i];
        if(isObject(val) && val.eName == eVal){
            return true;
        }else if(val == eVal){
            return true;
        }
    } 
    return false;
}
