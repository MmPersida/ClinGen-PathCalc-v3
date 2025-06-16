var currentRowObj = null;
var currentColumnGroupObj = null;
var currentColumnName = null;

var currentEvidenceCellId = null;
var selectedEvidenceTagRowId = null;
var selectedEvidenceLinkRowId = null;
var addedEvidenceTags = [];

function openEvidencePopUp(){
    document.getElementById("openEvidenceCellModal").click();
}

function closeEvidencePopUp(){
    resetEvidenceTagsPopUpContent();
    document.getElementById("openEvidenceCellModal").click();
    addedEvidenceTags = []; ///restart this list, this is valid for one opening of this pop up window
}

function resetEvidenceTagsPopUpContent(){
    selectedEvidenceTagRowId = null;
    selectedEvidenceLinkRowId = null;
    backToEvidences();   
}

function openEvidenceCellPopUp(tdElem){
    openEvidencePopUp();
    document.getElementById("evidenceCellPopUpTitle").innerText = "Manage Evidence Tags";

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

async function displayEvidenceCodesTable(){
    var evidenceCodesTablePopUp = document.getElementById("evidenceCodesTablePopUp");

    clearSelectChooser(evidenceCodesTablePopUp);

    var tr = null;
    var td = null;

    //set independent table header
    createEvidenceCodesTableHeader();

    //set table content
    var cellData = pathogenicityEvidencesDoc[currentEvidenceCellId];
    if(cellData != null && cellData.evidenceTags.length != 0){
        var preSelectedEvdTags = cellData.evidenceTags;
        //let evidenceSummariesPerTagValue = await getEvidenceSummariesForThisTags(preSelectedEvdTags); //array of selected evidence tags for this table cell, example ['PP2 - Moderate', 'BS1'...]

        for(let i in preSelectedEvdTags){
            var eTagObj = preSelectedEvdTags[i];

            /*
            let summaryObj = null;
            if(evidenceSummariesPerTagValue !=null && !isObjectEmpty(evidenceSummariesPerTagValue)){
                summaryObj = evidenceSummariesPerTagValue[eTagObj.evdTag];
            }*/

            tr = document.createElement('tr');
            tr.id = "evidenceTR_"+(Number(i)+1); //Number function avoids values like: 01, 02, 05 etc.
                td = document.createElement('td');
                td.id = "evidenceTagTD";
                td.className = "editCellTableTagClm";
                td.innerText = eTagObj.evdTag;
            tr.appendChild(td);
                td = document.createElement('td');
                td.id = "evidenceStatusTD";
                td.className = "editCellTableStatusClm";
                td.innerText = 'On';
            tr.appendChild(td);
                td = document.createElement('td');
                td.id = "evidenceSummaryTD";
                td.className = "editCellTableSummaryClm";
                if(eTagObj.evdSummary != null && eTagObj.evdSummary != ''){
                    td.innerText = eTagObj.evdSummary;
                }else{
                    td.innerText = '';
                }               
            tr.appendChild(td);
            tr.addEventListener("click", function(){ markSelectedEvidenceTagOrLinkRow(this, 'tag'); });
            tr.addEventListener("dblclick", function(){ editEvideneceAndLinksData(this, 'edit'); });

            evidenceCodesTablePopUp.appendChild(tr);
        }
    }
}

function createEvidenceCodesTableHeader(){
    var evdncCodesTablePopUpHeader = document.getElementById("evdncCodesTablePopUpHeader");
    clearSelectChooser(evdncCodesTablePopUpHeader);

    let tr = null;
    let th = null;
    tr = document.createElement('tr');
        th = document.createElement('th');
        th.className = "editCellTableTagClm"
        th.innerText = "Tag";
    tr.appendChild(th);
        th = document.createElement('th');
        th.className = "editCellTableStatusClm"     
        th.innerText = "Status";
    tr.appendChild(th);
        th = document.createElement('th');
        th.className = "editCellTableSummaryClm"
        th.innerText = "Summary";
    tr.appendChild(th);
    evdncCodesTablePopUpHeader.appendChild(tr);
}

function displayEvidenceSummaryTableExpl(){
    var evidenceSummaryTableExpl = document.getElementById("evidenceSummaryTableExpl");
    clearSelectChooser(evidenceSummaryTableExpl);
    var tr = null;
    var td = null;
    let tagDataObj = null

    var validTags = getValidEvidenceTagsForThisCell(currentRowObj.evidenceValues, getColumnGroupNameFromIndx(currentColumnGroupObj.indx), currentColumnName);        
    for(let i in validTags){
        let validTagVal = validTags[i]; 
        
        let validTagValArray = validTagVal.split("-");
        let basicTagValue = validTagValArray[0].trim();

        if(evidenceTagDataObj[basicTagValue] != null){
            tagDataObj = evidenceTagDataObj[basicTagValue];
        }

        tr = document.createElement('tr');
            td = document.createElement('td');
            td.style.fontWeight = "bold";
            td.style.width = "20%";
            let warningSign = "";
            if(tagDataObj != null && !tagDataObj.applicable){
                warningSign = "<img style=\"width: 15px; height: 15px; margin-left:5px;\"} src=\"../images/warning_button.png\">";
            }
            td.innerHTML = validTagVal+warningSign;
        tr.appendChild(td);
            td = document.createElement('td');
            td.style.width = "79%";
            var evdTagDescDiv = document.createElement('div');
            evdTagDescDiv.style.padding = "0px";
            evdTagDescDiv.style.margin = "0px";
            evdTagDescDiv.style.maxHeight = "200px";
            evdTagDescDiv.style.overflowY = "auto";
            if(tagDataObj != null){
                var summary = tagDataObj.summary;
                if(!isObjectEmpty(tagDataObj.applicableModifiers)){
                    var currentTagModifier = tagDataObj.applicableModifiers[currentColumnName];
                    if(currentTagModifier != null && currentTagModifier.text != null){
                        summary = currentTagModifier.text;
                    }else if(currentTagModifier != null && currentTagModifier.instructions != null){
                        summary = currentTagModifier.instructions;
                    }
                }
                evdTagDescDiv.innerText = summary; 
            }else{
                evdTagDescDiv.innerText = determineSummaryForModifiedEvidTag(validTagVal);
            }
            td.appendChild(evdTagDescDiv);
        tr.appendChild(td);
        evidenceSummaryTableExpl.appendChild(tr);             
    }
}

function addEvidenceTag(){
    //get the table that holds all of the evidence tags in the popup
    var evidenceCodesTablePopUp = document.getElementById("evidenceCodesTablePopUp");

    //create a new row
    let td = null;
    var newTR = document.createElement('tr');
    newTR.id = 'N/A';
        td = document.createElement('td');
        td.id = "evidenceTagTD";
        td.className = "editCellTableTagClm";
    newTR.appendChild(td);    
        td = document.createElement('td');    
        td.id = "evidenceStatusTD";  
        td.className = "editCellTableStatusClm";
    newTR.appendChild(td);          
        td = document.createElement('td');
        td.id = "evidenceSummaryTD";
        td.className = "editCellTableSummaryClm";
    newTR.appendChild(td);
    newTR.addEventListener("click", function(){ markSelectedEvidenceTagOrLinkRow(this, 'tag'); });
    newTR.addEventListener("dblclick", function(){ editEvideneceAndLinksData(this, 'edit'); });
    evidenceCodesTablePopUp.appendChild(newTR);
 
    //set the id of the new table row based on the id value of other rows in the table, "evidenceTR_1" if new
    var previouseId = "";
    if(newTR.previousSibling != null){
        previouseId = newTR.previousSibling.id;
    }

    if(previouseId != null && previouseId != undefined && previouseId != ""){
        var idArray = previouseId.split("_");
        var newID = idArray[0]+"_"+(Number(idArray[1])+1);
        newTR.id = newID;
    }else{
        newTR.id = "evidenceTR_1";
    }

    //disableAddTagButton();
    editEvideneceAndLinksData(newTR, 'add');
}

async function saveEvidenceTagEdits(){
    var numberOfNewEvidences = addedEvidenceTags.length;
    if(numberOfNewEvidences == 0){
        return;
    }
    disableCalculatorPageForMillis(1500);

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
    closeEvidencePopUp(); //this might be unnecessary 

    let formatEvidenceDoc = formatEvidenceDocForCspecCall();
    let calculatedFCObj = await updateCalculatedFinallCallAndProcessRuleSets(formatEvidenceDoc);
    updateCalculatedFinalCallHTML(calculatedFCObj);
    deselectAllEvidenceColumns();
    saveNewEvidences(calculatedFCObj, formatEvidenceDoc.allspecificEvidences);
}

async function removeEvidenceTagEdits(){
    if(selectedEvidenceTagRowId == null){
        return;
    }   
    disableCalculatorPageForMillis(1500);

    var selectedEvidenceTagRowElement = document.getElementById(selectedEvidenceTagRowId);
    var evidencesNumDiv = document.getElementById(getEvidenceCellDiv(currentEvidenceCellId));
    var tdCell = document.getElementById(currentEvidenceCellId);

    let tdWithEvdTagValue = selectedEvidenceTagRowElement.childNodes[0];
    let currentEvdTagValue = tdWithEvdTagValue.innerHTML.trim();

    var numOfEvdTagsUsed = removeFromPathogenicityEvidencesDoc(currentEvdTagValue, currentEvidenceCellId);
    if(numOfEvdTagsUsed == 0){
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
    }else if(numOfEvdTagsUsed > 0){
        evidencesNumDiv.innerHTML = numOfEvdTagsUsed;
    }else if(numOfEvdTagsUsed == -1){
        //tag was never saved to DB, just remove the tag from the addedEvidenceTags array end remove the tr element from table
        removeFromAddedEvidenceTags(currentEvdTagValue);
        selectedEvidenceTagRowElement.parentElement.removeChild(selectedEvidenceTagRowElement);
        return;
    }
    removeEvidenceFromTagApliedDivs(currentEvdTagValue);
    markSugestedValidTagValueAsUnused(currentEvidenceCellId, currentEvdTagValue);
    //unmarkValueInTheEditEvidendeTagsTable(currentEvidenceCellId, currentEvdTagValue);
    selectedEvidenceTagRowElement.parentElement.removeChild(selectedEvidenceTagRowElement);

    //closeEvidencePopUp(); //this might be unnecessary

    let formatEvidenceDoc = formatEvidenceDocForCspecCall();
    let calculatedFCObj = await updateCalculatedFinallCallAndProcessRuleSets(formatEvidenceDoc);
    updateCalculatedFinalCallHTML(calculatedFCObj); 

    let deletedEvidences = [];
    let evdObj = {
        'evdTag':currentEvdTagValue
    }
    let tempObj = formatIndividualEvdTagForCSpec(evdObj);
    deletedEvidences.push(tempObj)
    deselectAllEvidenceColumns();
    deleteEvidences(calculatedFCObj, deletedEvidences);
}

function  removeFromAddedEvidenceTags(currentEvdTagValue){
    if(addedEvidenceTags != null && addedEvidenceTags.length > 0){
        let indx = null;
        for(let i in addedEvidenceTags){
            var aet = addedEvidenceTags[i];
            if(currentEvdTagValue == aet.evdTag){
                indx = i;
                break;
            }
        }

        if(indx != null && !isNaN(indx)){
            addedEvidenceTags.splice(indx, 1);
        }
    }
}

function addEvidenceTagApliedDivs(addedEvidenceTags){
    var tagApliedContainer = document.getElementById("tagApliedContainer");
    var div = null;
    for(i in addedEvidenceTags){
        div = document.createElement('div');
        div.id = "apliedTag_"+addedEvidenceTags[i].evdTag;
        div.className = "finalConditionBtns "+currentColumnGroupObj.cssColorClass;
        div.innerHTML = addedEvidenceTags[i].evdTag;
        tagApliedContainer.appendChild(div);
    }
}

function  removeEvidenceFromTagApliedDivs(currentEvdTagValue){
    var tagApliedContainer = document.getElementById("apliedTag_"+currentEvdTagValue);
    if(tagApliedContainer != null){
        tagApliedContainer.parentElement.removeChild(tagApliedContainer);
    }
}

function markSugestedValidTagValueAsUsed(currentEvidenceCellId, addedEvidenceTags){
    var currentEvidenceCellArray  = currentEvidenceCellId.split("_"); //example: 0_0_0 -> row_tagType_specificSubTagType
    var rowIndexNum = currentEvidenceCellArray[0]; 
    var basicTagType = currentEvidenceCellArray[1]; 

    for(i in addedEvidenceTags){
        markSingleTagValue(addedEvidenceTags[i].evdTag, rowIndexNum, basicTagType, "red");
    }
}

function markSugestedValidTagValueAsUnused(currentEvidenceCellId, currentEvdTagValue){
    var currentEvidenceCellArray  = currentEvidenceCellId.split("_"); //example: 0_0_0 -> row_tagType_specificSubTagType
    var rowIndexNum = currentEvidenceCellArray[0]; 
    var basicTagType = currentEvidenceCellArray[1]; 
    
    markSingleTagValue(currentEvdTagValue, rowIndexNum, basicTagType, "black");
}

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
    //potencial check to be added: check addedEvidenceTags array first to make sure that there are no double evidence tags added 
    var newTR_id = updateDivBtnElem.value.trim();
    removeNewTagAndUpdateBtnRowsIfExists(null);

    //get the row that was edited
    var newTRow = document.getElementById(newTR_id);

    //get the inputed/selected values
    var newEvCode = document.getElementById("selectEvidenceCode").value.trim(); //the new selected evidence tag!!!
    if(newEvCode == null || newEvCode == ''){
        clearSelectChooser(newTRow);
        return;
    }
    var newEvStatus = document.getElementById("selectEvidenceStatus").value.trim();
    var newEvSummary = document.getElementById("evidenceSummaryInp").value.trim();

    if(newTRow == null){
        return;
    }
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

    if(addedEvidenceTags != null && addedEvidenceTags.length > 0){
        //this is special case when an already set evidence tag is updated, only the evidence summary can be updated
        for(let indx in addedEvidenceTags){
            var aet = addedEvidenceTags[indx];
            if(newEvCode == aet.evdTag){
                aet.evdSummary = newEvSummary;
                return;
            }
        }
    }

    var evdData = {
        'evdTag': newEvCode,
        'evdSummary':newEvSummary
    }
    addedEvidenceTags.push(evdData);
}

function disableAddTagButton(addTagBtnElem){
    if(addTagBtnElem == null){
        addTagBtnElem = document.getElementById("addTagBtn");
    }
}
function enableAddTagButton(addTagBtnElem){
    if(addTagBtnElem == null){
        addTagBtnElem = document.getElementById("addTagBtn");
    }
}
