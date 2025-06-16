var evdLinks = null;

async function openSelectedEvidenceLinks(){
    if(selectedEvidenceTagRowId == null || variantInterpretationID == 0){
        return;
    } 

    let currentEvdTagValueFull = getSelectedEvidenceTagValue();
    if(currentEvdTagValueFull == null || currentEvdTagValueFull == ''){
        return;
    }

    let evidenceTagBasic = null;
    let evidenceTagModifier = null;
    let evdTagArray = currentEvdTagValueFull.split(" - ");
   
    evidenceTagBasic = evdTagArray[0].trim();
    if(evdTagArray.length == 2){
        evidenceTagModifier = evdTagArray[1].trim();
    }

    await getEvidneLinksForThisEvdTag(variantInterpretationID, evidenceTagBasic, evidenceTagModifier);
    closeEvdTagTableOpenEvdLinksTable();
    displayEvidenceLinksTable();
}

function closeEvdTagTableOpenEvdLinksTable(){
    let evidTagsContainer = document.getElementById("evidTagsContainer");  
    let evidLinksContainer = document.getElementById("evidLinksContainer");
    evidTagsContainer.style.display = "none";
    evidLinksContainer.style.display = "block";
}

function closeEvdLinksTableOpenEvdTagTable(){
    let evidTagsContainer = document.getElementById("evidTagsContainer");  
    let evidLinksContainer = document.getElementById("evidLinksContainer");
    evidLinksContainer.style.display = "none";
    evidTagsContainer.style.display = "block";
}

function getSelectedEvidenceTagValue(){
    var selectedEvidenceTagRowElement = document.getElementById(selectedEvidenceTagRowId);
    let tdWithEvdTagValue = selectedEvidenceTagRowElement.childNodes[0];
    let currentEvdTagValueFull = tdWithEvdTagValue.innerHTML.trim();

    let b = isThisEvidencePartOfPathogenicityEvidencesDoc(currentEvdTagValueFull, currentEvidenceCellId);
    if(!b){
        alert("You must first save the added evidence tag before attempting to add evidence link to it!");
        return null;
    }
    return currentEvdTagValueFull;
}

function backToEvidences(){
    let evidTagsContainer = document.getElementById("evidTagsContainer");  
    let evidLinksContainer = document.getElementById("evidLinksContainer");
    evidLinksContainer.style.display = "none";
    evidTagsContainer.style.display = "block";
}

function addEvidenceLink(){
    let evidenceLinksTablePopUp = document.getElementById("evidenceLinksTablePopUp");

    //create a new row
    let td = null;
    let newTR = document.createElement('tr');
    newTR.id = 'N/A';
        td = document.createElement('td');       
        td.id = "linkValueTD";  
        td.className="editCellTableLinkClm";      
    newTR.appendChild(td);
        td = document.createElement('td');       
        td.id = "linkCodeTD"; 
        td.className="editCellTableLinkCodeClm";       
    newTR.appendChild(td);
        td = document.createElement('td');
        td.id = "linkCommentTD";
        td.className="editCellTableCommentClm";
    newTR.appendChild(td);
    newTR.addEventListener("click", function(){ markSelectedEvidenceTagOrLinkRow(this, 'link'); });
    newTR.addEventListener("dblclick", function(){ editEvideneceAndLinksData(this, 'edit'); });
    evidenceLinksTablePopUp.appendChild(newTR);

    var previouseId = "";
    if(newTR.previousSibling != null){
        previouseId = newTR.previousSibling.id;
    }
    
    if(previouseId != null && previouseId != undefined && previouseId != ""){
        var idArray = previouseId.split("_");
        var newID = "newLink_"+(Number(idArray[1])+1);
        newTR.id = newID;
    }else{
        newTR.id = "newLink_1";
    }

    //disableLinkTagButton();
    editEvideneceAndLinksData(newTR, "add");  //the second param was null berpfe the bug fix !!!!!!!!!!!!!!!!!!!!!!!
}

async function removeEvidenceLink(){
    if(selectedEvidenceLinkRowId == null){
        return;
    }   
    var selectedEvidenceLinkRow = document.getElementById(selectedEvidenceLinkRowId)
    if(selectedEvidenceLinkRow == null){
        return;
    }
    disableCalculatorPageForMillis(1500);
    
    let tdWithEvdLinkIdValue = selectedEvidenceLinkRow.id;
    let evdLinkId = tdWithEvdLinkIdValue.split("_")[1];
    if(evdLinkId == null || isNaN(evdLinkId)){
        selectedEvidenceLinkRow.parentElement.removeChild(selectedEvidenceLinkRow);
        return;
    }

    await deleteThisEvidenceLink(evdLinkId);
    selectedEvidenceLinkRow.parentElement.removeChild(selectedEvidenceLinkRow);
}

async function saveEvidenceLink(){
    if(selectedEvidenceTagRowId == null || variantInterpretationID == 0){
        return;
    }
    let currentEvdTagValueFull = getSelectedEvidenceTagValue();
    if(currentEvdTagValueFull == null || currentEvdTagValueFull == ''){
        return;
    }

    disableCalculatorPageForMillis(1500);

    let evidenceTagBasic = null;
    let evidenceTagModifier = null;
    let evdTagArray = currentEvdTagValueFull.split(" - ");
   
    evidenceTagBasic = evdTagArray[0].trim();
    if(evdTagArray.length == 2){
        evidenceTagModifier = evdTagArray[1].trim();
    }
   
    let evidenceLinksTablePopUp = document.getElementById("evidenceLinksTablePopUp");
    let trList = evidenceLinksTablePopUp.childNodes;
    if(trList.length == 0){
        alert("No evidence links to save!");
        return;
    }
    let evidenceLinks = [];

    let n = trList.length;
    for(let i=0; n>i; i++){
        let trElem = trList[i];
        if(trElem.id != null && trElem.id != ''){
       
            let linkObj = {};
            let evdLinkIdArray = (trElem.id).split("_");
            let evdLinkId = evdLinkIdArray[1]
            if(evdLinkIdArray[0] != 'newLink' && evdLinkId != null && !isNaN(evdLinkId)){
                linkObj.linkId = evdLinkId;
            }

            let tdList = trElem.childNodes;
            let k = tdList.length;
            for(let j=0; k>j; j++){
                let linkTD = tdList[j];
                if(linkTD.id == "linkValueTD"){
                    linkObj.link = linkTD.innerText;
                }else if(linkTD.id == "linkCodeTD"){
                    linkObj.linkCode = linkTD.innerText;
                }else if(linkTD.id == "linkCommentTD"){
                    linkObj.comment = linkTD.innerText;
                }
            }
            evidenceLinks.push(linkObj);
        }
    }

    let data = {
        'interpretationId': variantInterpretationID,
        'evidenceTag': evidenceTagBasic,
        'evidenceModifier': evidenceTagModifier,
        'evidenceLinks': evidenceLinks
    }
    await saveNewEvidneLinkForThisEvdTag(data);
    closeEvdLinksTableOpenEvdTagTable();
}

function displayEvidenceLinksTable(){
    if(evdLinks == null && evdLinks.length == 0){
        return;
    }
    let evidenceLinksTablePopUp = document.getElementById("evidenceLinksTablePopUp");

    clearSelectChooser(evidenceLinksTablePopUp);
    //set table header
    var tr = null;

    displayEvidenceLinksTableHeader();

    if(evdLinks == null || evdLinks.length == 0){
        return;
    }

    var td = null;
    for(let i in evdLinks){
        var eLinkObj = evdLinks[i];

        tr = document.createElement('tr');
        tr.id = "evidenceLink_"+(eLinkObj.linkId); //Number function avoids values like: 01, 02, 05 etc.
            td = document.createElement('td');
            td.id= "linkValueTD";
            td.className="editCellTableLinkClm";
            td.innerText = eLinkObj.link;
        tr.appendChild(td);
            td = document.createElement('td');
            td.id= "linkCodeTD";
            td.className="editCellTableLinkCodeClm";
            td.innerText = eLinkObj.linkCode;
        tr.appendChild(td);
            td = document.createElement('td');
            td.id= "linkCommentTD";
            td.className="editCellTableCommentClm";
            td.innerText = eLinkObj.comment;
        tr.appendChild(td);
        tr.addEventListener("click", function(){ markSelectedEvidenceTagOrLinkRow(this, 'link'); });
        tr.addEventListener("dblclick", function(){ editEvideneceAndLinksData(this, 'edit'); });

        evidenceLinksTablePopUp.appendChild(tr);
    }   
}

function displayEvidenceLinksTableHeader(){
    let evdncLinksTablePopUpHeader = document.getElementById("evdncLinksTablePopUpHeader");
    clearSelectChooser(evdncLinksTablePopUpHeader);
    var tr = null;
    var th = null;
    tr = document.createElement('tr');
        th = document.createElement('th');
        th.className = "editCellTableLinkClm";     
        th.innerText = "Link";
    tr.appendChild(th);
        th = document.createElement('th');
        th.className = "editCellTableLinkCodeClm";
        th.innerText = "Link Code";
    tr.appendChild(th);
        th = document.createElement('th');
        th.className = "editCellTableCommentClm";
        th.innerText = "Comment";
    tr.appendChild(th);

    evdncLinksTablePopUpHeader.appendChild(tr);
}

function updateEvdLinksData(updateDivBtnElem){
    var newTR_id = updateDivBtnElem.value.trim();
    removeNewTagAndUpdateBtnRowsIfExists(null);

    var newLinkValue = document.getElementById("linkValueInp").value.trim();
    var newLinkCode = document.getElementById("selectLinkCode").value.trim();
    var newCommentValue = document.getElementById("linkCommentInp").value.trim();
    

    //get the row that was edited
    var newTRow = document.getElementById(newTR_id);
    if(newTRow == null){
        return;
    }
    clearSelectChooser(newTRow)
        td = document.createElement('td');
        td.id = "linkValueTD";
        td.className="editCellTableLinkClm";
        td.innerText = newLinkValue;
    newTRow.appendChild(td);
        td = document.createElement('td');
        td.id = "linkCodeTD";
        td.className="editCellTableLinkCodeClm";
        td.innerText = newLinkCode;
    newTRow.appendChild(td);
        td = document.createElement('td');
        td.id = "linkCommentTD";
        td.className="editCellTableCommentClm";
        td.innerText = newCommentValue;
    newTRow.appendChild(td);
}

function  getEvidneLinksForThisEvdTag(variantInterpretationID, currentEvdTagValue, currentEvdTagModifier){
    return new Promise(function (resolve, reject) {
        var postData = {
          'interpretationId': variantInterpretationID,
          'evidenceTag': currentEvdTagValue,
          'evidenceModifier': currentEvdTagModifier
        };
        postData = JSON.stringify(postData);
      
        var xhr = new XMLHttpRequest();
        let url = "/pcalc/rest/evidence/getLinksFroVIIdAndEvdTag";
        xhr.onload = function() {
            if (xhr.status === 200 && xhr.readyState == 4) {
                if(xhr.responseText != null && xhr.responseText  != ''){
                  evdLinks = JSON.parse(xhr.responseText);
                  resolve(null)                                                                   
                }else{
                  resolve(null);
                }
            }else if (xhr.status !== 200) {
              resolve(null);
            }
        };
        xhr.open("POST", url, true);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.withCredentials = true;
        xhr.send(postData);
      });
}

function saveNewEvidneLinkForThisEvdTag(postData){
    return new Promise(function (resolve, reject) {
        postData = JSON.stringify(postData);
      
        var xhr = new XMLHttpRequest();
        let url = "/pcalc/rest/evidence/saveEvidenceLinks";
        xhr.onload = function() {
            if (xhr.status === 200 && xhr.readyState == 4) {
                if(xhr.responseText != null && xhr.responseText  != ''){
                  resolve(xhr.responseText)                                                                   
                }else{
                  resolve(null);
                }
            }else if (xhr.status !== 200) {
              resolve(null);
            }
        };
        xhr.open("POST", url, true);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.withCredentials = true;
        xhr.send(postData);
      });
}

function deleteThisEvidenceLink(evdLinkId){
    return new Promise(function (resolve, reject) {
        var postData = {
          'evdLinkId': evdLinkId
        };
        postData = JSON.stringify(postData);
      
        var xhr = new XMLHttpRequest();
        let url = "/pcalc/rest/evidence/deleteEvidenceLinkById";
        xhr.onload = function() {
            if (xhr.status === 200 && xhr.readyState == 4) {
                if(xhr.responseText != null && xhr.responseText  != ''){
                  resolve(xhr.responseText)                                                                   
                }else{
                  resolve(null);
                }
            }else if (xhr.status !== 200) {
              resolve(null);
            }
        };
        xhr.open("POST", url, true);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.withCredentials = true;
        xhr.send(postData);
      });
}

function disableLinkTagButton(addLinkBtnElem){
    if(addLinkBtnElem == null){
        addLinkBtnElem = document.getElementById("addLinkBtn");
    }}
function enableLinkTagButton(addLinkBtnElem){
    if(addLinkBtnElem == null){
        addLinkBtnElem = document.getElementById("addLinkBtn");
    }
}
