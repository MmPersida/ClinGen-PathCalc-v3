var currentRowObj = null;
var currentColumnGroupObj = null;
var currentColumnName = null;

var currentEvidenceCellId = null;
var selectedEvidenceTagRowId = null;
var addedEvidenceTags = [];
var pathogenicityEvidencesDoc = {};

function openEvidenceCellPopUp(tdElem){
    document.getElementById("openEvidenceCellModal").click();
    document.getElementById("evidenceCellPopUpTitle").innerText = "Evidence Tags for the cell:";

    currentEvidenceCellId = tdElem.id;
    var currentEvidenceCellIdArray = tdElem.id.split("_"); //example: 0_1_1 (row:0, columnGroup:1, columnsName: 1)
    var rowIndex = Number(currentEvidenceCellIdArray[0]);
    var columnGroupIndex = Number(currentEvidenceCellIdArray[1]);
    var columnsNameIndex = Number(currentEvidenceCellIdArray[2]);

    currentRowObj = getRowObjectForIndx(rowIndex);
    currentColumnGroupObj = getColumnGroupObjectForIndx(columnGroupIndex);
    currentColumnName = currentColumnGroupObj.tagValues[columnsNameIndex];

    addedEvidenceTags = []; ///restart this list, this is valid for one opening of this pop up window
    displayEvidenceCodesTable();
    displayEvidenceSummaryTableExpl();
}

function displayEvidenceSummaryTableExpl(){
    var evidenceSummaryTableExpl = document.getElementById("evidenceSummaryTableExpl");
    clearSelectChooser(evidenceSummaryTableExpl);
    var tr = null;
    var td = null;

    var validTags = getValidEvidenceTagsForThisCell(currentRowObj.evidenceValues, getColumnGroupNameFromIndx(currentColumnGroupObj.indx), currentColumnName);        
    for(let i in validTags){
        let validTagVal = validTags[i];   

        tr = document.createElement('tr');
            td = document.createElement('td');
            td.style.fontWeight = "bold";
            td.innerHTML = validTagVal;
        tr.appendChild(td);
            td = document.createElement('td');
            if(evidenceTagDataObj[validTagVal] != null){
                td.innerText = evidenceTagDataObj[validTagVal].summary; 
            }else{
                td.innerText = determineSummaryForModifiedEvidTag(validTagVal);
            }
        tr.appendChild(td);
        evidenceSummaryTableExpl.appendChild(tr);             
    }
}

function addEvidenceTag(){
    var evidenceCodesTablePopUp = document.getElementById("evidenceCodesTablePopUp");
    var newTR = document.createElement('tr');
    newTR.id = 'N/A';
        var tdEvCode = document.createElement('td');
        var tdEvStatus = document.createElement('td');            
        var tdEvSummary = document.createElement('td');
    newTR.appendChild(tdEvCode);
    newTR.appendChild(tdEvStatus);
    newTR.appendChild(tdEvSummary);
    evidenceCodesTablePopUp.appendChild(newTR);
 
    var previouseId = newTR.previousSibling.id;
    if(previouseId != null && previouseId != undefined && previouseId != ""){
        var idArray = previouseId.split("_");
        var newID = idArray[0]+"_"+(Number(idArray[1])+1);
        newTR.id = newID;
    }else{
        newTR.id = "evidenceTR_1";
    }
    editEvideneceData(newTR, 'add');
}

async function saveEvidenceTagEdits(){
    var numberOfNewEvidences = addedEvidenceTags.length;
    var evidencesNumDiv = document.getElementById(getEvidenceCellDiv(currentEvidenceCellId));
    var currentNumOfEvidence = Number(evidencesNumDiv.innerHTML.trim());

    if(currentNumOfEvidence != numberOfNewEvidences || numberOfNewEvidences != 0){
        evidencesNumDiv.innerHTML = addToPathogenicityEvidencesDoc(addedEvidenceTags, currentEvidenceCellId);
        addEvidenceTagApliedDivs(addedEvidenceTags);
        markSugestedValidTagValueAsUsed(currentEvidenceCellId, addedEvidenceTags);
        //markValueInTheEditEvidendeTagsTable(currentEvidenceCellId, addedEvidenceTags);
        //this is the entire evidence call, the td element of the specific row
        var currentEvdncCellNode = document.getElementById(currentEvidenceCellId);
        var classNames = currentEvdncCellNode.className;

        let currentColumnGroupName =  getColumnGroupNameFromIndx(currentColumnGroupObj.indx);
        if(currentColumnGroupName == "Pathogenic"){
            classNames = classNames.replace('whiteTD', 'pinkTD');
            currentEvdncCellNode.className = classNames;            
        }else if (currentColumnGroupName == "Benign"){
            classNames = classNames.replace('whiteTD', 'greenTD');
            currentEvdncCellNode.className = classNames;
        }               
    }
    document.getElementById("openEvidenceCellModal").click();

    let formatEvidenceDoc = formatEvidenceDocForCspecCall();
    let finalCallValue = await updateFinallCall(formatEvidenceDoc);
    updateFinalCallHTMLEleme(finalCallValue);  
    saveNewEvidences(finalCallValue, formatEvidenceDoc.allspecificEvidences);
}

async function removeEvidenceTagEdits(){
    if(selectedEvidenceTagRowId == null){
        return;
    }   
    var selectedEvidenceTagRowElement = document.getElementById(selectedEvidenceTagRowId);
    var evidencesNumDiv = document.getElementById(getEvidenceCellDiv(currentEvidenceCellId));
    var tdCell = document.getElementById(currentEvidenceCellId);

    let tdWithEvdTagValue = selectedEvidenceTagRowElement.children[0];
    let currentEvdTagValue = tdWithEvdTagValue.innerHTML.trim();

    var numOfEvdTagsUsed = removeFromPathogenicityEvidencesDoc(currentEvdTagValue, currentEvidenceCellId);
    if(numOfEvdTagsUsed < 1){
        evidencesNumDiv.innerHTML = "";
        var classNames = tdCell.className;

        let currentColumnGroupName =  getColumnGroupNameFromIndx(currentColumnGroupObj.indx);
        if(currentColumnGroupName == "Pathogenic"){
            classNames = classNames.replace('pinkTD', 'whiteTD');
            tdCell.className = classNames;            
        }else if (currentColumnGroupName == "Benign"){
            classNames = classNames.replace('greenTD', 'whiteTD');
            tdCell.className = classNames;
        } 
    }else{
        evidencesNumDiv.innerHTML = numOfEvdTagsUsed;
    }
    removeEvidenceFromTagApliedDivs(currentEvdTagValue);
    markSugestedValidTagValueAsUnused(currentEvidenceCellId, currentEvdTagValue);
    //unmarkValueInTheEditEvidendeTagsTable(currentEvidenceCellId, currentEvdTagValue);
    selectedEvidenceTagRowElement.parentElement.removeChild(selectedEvidenceTagRowElement);

    document.getElementById("openEvidenceCellModal").click();

    let formatEvidenceDoc = formatEvidenceDocForCspecCall();
    let finalCallValue = await updateFinallCall(formatEvidenceDoc);
    updateFinalCallHTMLEleme(finalCallValue); 
    let deletedEvidences = [];
    let tempObj = formatIndividualEvdTag(currentEvdTagValue);
    deletedEvidences.push(tempObj)
    deleteEvidences(finalCallValue, deletedEvidences);
}

function addToPathogenicityEvidencesDoc(newEvidenceTags, currentEvidenceCellId){
    var evidenceDataPerCell = pathogenicityEvidencesDoc[currentEvidenceCellId];
    if(evidenceDataPerCell != null){
        evidenceDataPerCell.evidenceTags = evidenceDataPerCell.evidenceTags.concat(newEvidenceTags);
        return evidenceDataPerCell.evidenceTags.length;
    }else{
        pathogenicityEvidencesDoc[currentEvidenceCellId] = {
            name: getColumnGroupNameFromIndx(currentColumnGroupObj.indx)+"."+currentColumnName,
            evidenceTags: newEvidenceTags
        };

        return newEvidenceTags.length;
    }
}

function removeFromPathogenicityEvidencesDoc(currentEvdTag, currentEvidenceCellId){
    var evidenceDataPerCell = pathogenicityEvidencesDoc[currentEvidenceCellId];
    if(evidenceDataPerCell != null){
        let index = evidenceDataPerCell.evidenceTags.indexOf(currentEvdTag);
        evidenceDataPerCell.evidenceTags.splice(index, 1);
        if(evidenceDataPerCell.evidenceTags.length < 1){
            delete pathogenicityEvidencesDoc[currentEvidenceCellId]
            return 0;
        }else{
            return evidenceDataPerCell.evidenceTags.length;
        }
    }
}

function addEvidenceTagApliedDivs(addedEvidenceTags){
    var tagApliedContainer = document.getElementById("tagApliedContainer");
    var div = null;
    for(i in addedEvidenceTags){
        div = document.createElement('div');
        div.id = "apliedTag_"+addedEvidenceTags[i];
        div.className = "finalConditionBtns "+currentColumnGroupObj.cssColorClass;
        div.innerHTML = addedEvidenceTags[i];
        tagApliedContainer.appendChild(div);
    }
}

function  removeEvidenceFromTagApliedDivs(currentEvdTagValue){
    var tagApliedContainer = document.getElementById("apliedTag_"+currentEvdTagValue);
    tagApliedContainer.parentElement.removeChild(tagApliedContainer);
}

function markSugestedValidTagValueAsUsed(currentEvidenceCellId, addedEvidenceTags){
    var currentEvidenceCellArray  = currentEvidenceCellId.split("_"); //example: 0_0_0 -> row_tagType_specificSubTagType
    var rowIndexNum = currentEvidenceCellArray[0]; 
    var basicTagType = currentEvidenceCellArray[1]; 

    for(i in addedEvidenceTags){
        markSingleTagValue(addedEvidenceTags[i], rowIndexNum, basicTagType, "red");
    }
}

function markSugestedValidTagValueAsUnused(currentEvidenceCellId, currentEvdTagValue){
    var currentEvidenceCellArray  = currentEvidenceCellId.split("_"); //example: 0_0_0 -> row_tagType_specificSubTagType
    var rowIndexNum = currentEvidenceCellArray[0]; 
    var basicTagType = currentEvidenceCellArray[1]; 
    
    markSingleTagValue(currentEvdTagValue, rowIndexNum, basicTagType, "black");
}

/*
function markValueInTheEditEvidendeTagsTable(currentEvidenceCellId, addedEvidenceTags){
    var currentEvidenceCellArray  = currentEvidenceCellId.split("_"); //example: 0_0_0 -> row_tagType_specificSubTagType
    var rowIndexNum = currentEvidenceCellArray[0];

    for(i in addedEvidenceTags){
        var cbInputElem = document.getElementById(getEditEvdcTagsTablePopupIds(rowIndexNum, addedEvidenceTags));
        cbInputElem.checked = true;    
    }
}

function unmarkValueInTheEditEvidendeTagsTable(currentEvidenceCellId, currentEvdTagValue){
    var currentEvidenceCellArray  = currentEvidenceCellId.split("_"); //example: 0_0_0 -> row_tagType_specificSubTagType
    var rowIndexNum = currentEvidenceCellArray[0];
    
    document.getElementById(getEditEvdcTagsTablePopupIds(rowIndexNum, currentEvdTagValue)).checked = false;
}*/

function markSingleTagValue(evdcTagValue, rowIndexNum, basicTagType, toColor){
    var basicEvdcValue = evdcTagValue.split("-")[0].trim(); //extracts, example BS1 from BS1 or BS1 - Supporting
    var eTagObj = evidenceTagDataObj[basicEvdcValue];

    var basicTagSubTypes = basicEvidenceTagTypes_columns[eTagObj.tagType].tagValues;

    var modifiedEvidenceTags = determineModifiedEvidenceTags(eTagObj, basicTagSubTypes, basicEvdcValue);
    modifiedEvidenceTags.push(basicEvdcValue);

    var li = null
    var n = basicTagSubTypes.length;
    for(var k=0; k<n; k++){
        var ulIdCellDesigantorValue = rowIndexNum+"_"+basicTagType+"_"+k;
        for(m in modifiedEvidenceTags){
            li = document.getElementById(ulIdCellDesigantorValue+"_"+modifiedEvidenceTags[m]);
            if(li != null){
                li.style.color = toColor;
            }
        } 
    }
}

function updateEvidenceData(updateDivBtnElem){
    var valueArray = updateDivBtnElem.value.split("|");
    var newTR_id = valueArray[0];
    var actionStatus = valueArray[1];

    var row = document.getElementById("update_edit_tr");
    if(row == null){
        return;
    }
    row.parentElement.removeChild(row);

    var newEvCode = document.getElementById("selectEvidenceCode").value.trim(); //the new selected evidence tag!!!
    var newEvStatus = document.getElementById("selectEvidenceStatus").value.trim();
    var newEvSummary = document.getElementById("evidenceSummaryInp").value.trim();

    var newTRow = document.getElementById(newTR_id);
    clearSelectChooser(newTRow)
        td = document.createElement('td');
        td.innerText = newEvCode;
    newTRow.appendChild(td);
        td = document.createElement('td');
        td.innerText = newEvStatus;
    newTRow.appendChild(td);
        td = document.createElement('td');
        td.innerText = newEvSummary;
    newTRow.appendChild(td);
    newTRow.addEventListener("dblclick", function(){ editEvideneceData(this); });

    if(actionStatus == "add"){
        addedEvidenceTags.push(newEvCode);
    }else if(actionStatus == "edit"){
        addedEvidenceTags.push(newEvCode);
        addedEvidenceTags.push(newEvCode);
    }
}
