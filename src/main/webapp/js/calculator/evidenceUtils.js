var pathogenicityEvidencesDoc = {};

function getEvidenceCellDiv(cellID){
    return "evdCellDiv_"+cellID;
}

function getTagValueIds(evidenceCellId){
    return "tagValues_"+evidenceCellId;
}

function addToPathogenicityEvidencesDoc(newEvidenceTags, currentEvidenceCellId){
    var evidenceDataPerCell = pathogenicityEvidencesDoc[currentEvidenceCellId];
    if(evidenceDataPerCell != null){
        for(i in evidenceDataPerCell.evidenceTags){
            let existingEvdTag =  evidenceDataPerCell.evidenceTags[i].evdTag;

            let indxToRemove = null;
            for(j in newEvidenceTags){
                if(newEvidenceTags[j].evdTag == existingEvdTag){
                    evidenceDataPerCell.evidenceTags[i] = newEvidenceTags[j];
                    indxToRemove = j;
                    break;
                }
            }

            if(indxToRemove != null && !isNaN(indxToRemove)){
                newEvidenceTags.splice(indxToRemove, 1);
            }
        }

        if(newEvidenceTags.length > 0){
            //if anything remains at this point it's only new
            evidenceDataPerCell.evidenceTags = evidenceDataPerCell.evidenceTags.concat(newEvidenceTags);
        }
        return evidenceDataPerCell.evidenceTags.length;
    }else{
        //this is a new evidence for this cell
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
        let index = null;

        let evidenceTags = evidenceDataPerCell.evidenceTags;
        for(let i in evidenceTags){
            if(evidenceTags[i].evdTag == currentEvdTag){
                index = i;
                break;
            }
        }

        if(index != null && !isNaN(index)){
            evidenceDataPerCell.evidenceTags.splice(index, 1);
        }else{
            return -1;
        }
        
        if(evidenceDataPerCell.evidenceTags.length < 1){
            delete pathogenicityEvidencesDoc[currentEvidenceCellId]
            return 0;
        }else{
            return evidenceDataPerCell.evidenceTags.length;
        }
    }
    return -1;
}

function isThisEvidencePartOfPathogenicityEvidencesDoc(currentEvdTag, currentEvidenceCellId){
    var evidenceDataPerCell = pathogenicityEvidencesDoc[currentEvidenceCellId];
    if(evidenceDataPerCell != null){      
        let evidenceTags = evidenceDataPerCell.evidenceTags;
        for(let i in evidenceTags){
            if(evidenceTags[i].evdTag == currentEvdTag){
                return true;
            }
        } 
    }
    return false;
}

function formatEvidenceDocForCspecCall(){
    var formatEvdncDoc = {
        "evidence": {},
        "allspecificEvidences": []
    };

    let keys = Object.keys(pathogenicityEvidencesDoc);
    for(let i in keys){
        let key = keys[i];
        var cellEvidenceData = pathogenicityEvidencesDoc[key];
        if(cellEvidenceData.evidenceTags.length > 0){
            if(formatEvdncDoc.evidence[cellEvidenceData.name] == null){
                formatEvdncDoc.evidence[cellEvidenceData.name] = cellEvidenceData.evidenceTags.length  
            }else{
                formatEvdncDoc.evidence[cellEvidenceData.name] = Number(formatEvdncDoc.evidence[cellEvidenceData.name]) + cellEvidenceData.evidenceTags.length ;
            }    

            var theseEvidenceTags  = cellEvidenceData.evidenceTags;
            for(let eIndx in theseEvidenceTags){
                let evObj = formatIndividualEvdTagForCSpec(theseEvidenceTags[eIndx]);
                formatEvdncDoc.allspecificEvidences.push(evObj);
            }
        }
    }
    return formatEvdncDoc;
}

function formatIndividualEvdTagForCSpec(evTagObj){
    let evdTag = evTagObj.evdTag;
    let evidValArray = evdTag.split("-");

    let evObj = {
        "type": evidValArray[0].trim(),       
        "fullLabelForFE": evdTag 
    }

    if(evTagObj.evdSummary != null && evTagObj.evdSummary != ''){
        evObj.summary = evTagObj.evdSummary;
    }

    if(evidValArray.length == 2 && evidValArray[1].trim() != ''){
        evObj.modifier = evidValArray[1].trim();
    }
    return evObj;
}
