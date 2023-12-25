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

async function displayEvidenceCodesTable(){
    var evidenceCodesTablePopUp = document.getElementById("evidenceCodesTablePopUp");

    clearSelectChooser(evidenceCodesTablePopUp);
    //set table header
    var tr = null;
    var th = null;
    var td = null;

    evidenceCodesTablePopUp
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
    evidenceCodesTablePopUp.appendChild(tr);


    //set table content
    var cellData = pathogenicityEvidencesDoc[currentEvidenceCellId];
    if(cellData != null && cellData.evidenceTags.length != 0){
        var preSelectedEvdTags = cellData.evidenceTags;
        //let evidenceSummariesPerTagValue = await getEvidenceSummariesForThisTags(preSelectedEvdTags); //array of selected evidence tags for this table cell, example ['PP2 - Moderate', 'BS1'...]

        var tr = null;
        var td = null;
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
                td.innerText = eTagObj.evdTag;
            tr.appendChild(td);
                td = document.createElement('td');
                td.innerText = 'On';
            tr.appendChild(td);
                td = document.createElement('td');
                if(eTagObj.evdSummary != null && eTagObj.evdSummary != ''){
                    //td.id = summaryObj.summaryId;
                    td.innerText = eTagObj.evdSummary;
                }else{
                    td.innerText = '';
                }               
            tr.appendChild(td);
            tr.addEventListener("click", function(){ markSelectedEvidenceTagRow(this); });
            tr.addEventListener("dblclick", function(){ editEvideneceData(this, 'edit'); });

            evidenceCodesTablePopUp.appendChild(tr);
        }
    }
}

/*
async function getEvidenceSummariesForThisTags(preSelectedEvdTags){
    var postData = {
        "interpretationId": variantInterpretationID,
        "evidenceTags": preSelectedEvdTags
    }

	return new Promise(function (resolve, reject) {
		postData = JSON.stringify(postData);

		var xhr = new XMLHttpRequest();
		var url = "/rest/evidence/getEvdSummaryForVIIdAndEvdTags";
		xhr.onload = function() {
			if (xhr.status === 200 && xhr.readyState == 4) {
				if(xhr.responseText != null && xhr.responseText  != ''){
					var jsonObj = JSON.parse(xhr.responseText);
					resolve(jsonObj);						
				}
				resolve(null);				
			}else if (xhr.status !== 200) {
				resolve(null);
			}
		};
		xhr.open("POST", url, true);
		xhr.setRequestHeader('Content-Type', 'application/json');
		xhr.send(postData);
	});
}*/

function openNotificationPopUp(message){
    document.getElementById("openNotificationModal").click();
    document.getElementById("notificationContent").innerHTML = message;
}   

function closeNotificationPopUp(){
    document.getElementById("openNotificationModal").click();
}

function resetNotificationContent(){
    clearSelectChooser(document.getElementById("notificationContent"));
}

async function createCSpecEngineInfoContent(cspecengineId){
    let engineInfo = await getCSpecEngineInfo(cspecengineId);
    if(engineInfo == null || !isObject(engineInfo)){
        return "No data available!";
    }

    let relatedGenes = "None"
    if(engineInfo.genes != null){
        relatedGenes = '';
        let genesList = engineInfo.genes;
        for(let i in genesList){
            var g = genesList[i];
            /*
            let condListStr = '';
            if(g.conditions != null && g.conditions.length > 0){
                condListStr += '(';
                let condList = g.conditions;
                for(let k in condList){
                    let cond = condList[k];
                    condListStr += cond.term+', ';
                }
                condListStr += ')';
            }*/
            relatedGenes += '<a href="https://genboree.org/cfde-gene-dev/Gene/id/'+g.geneName+'" target="_blank"><p>'+g.geneName+'</p><a/><br>';
        }
    }
    let htmlContentMessage ='<span style="font-weight:bold; color:rgba(50,110,150);">Engine ID:</span> '+engineInfo.engineId+'</br></br>'+
                            '<span style="font-weight:bold; color:rgba(50,110,150);">Organization:</span> '+engineInfo.organizationName+'</br></br>'+
                            '<span style="font-weight:bold; color:rgba(50,110,150);">Summary:</span> '+engineInfo.engineSummary+'</br></br>'+
                            '<span style="font-weight:bold; color:rgba(50,110,150);">RuleSet URL:</span> '+engineInfo.ruleSetURL+'</br></br>'+
                            '<span style="font-weight:bold; color:rgba(50,110,150);">Related genes:</span></br>'+relatedGenes;
    return htmlContentMessage;
}


