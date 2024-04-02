var evidenceTagDataObj = {
	"BP1":{"tagType":"Benign","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Supporting","tagValue":1,"summary":"","infoURL":"","applicable":false},
	"BP2":{"tagType":"Benign","column":"ALLELIC DATA","tagDescriptor":"Supporting","tagValue":2, "summary":"", "infoURL":"","applicable":false},
	"BP3":{"tagType":"Benign","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Supporting","tagValue":3,"summary":"","infoURL":"","applicable":false},
	"BP4":{"tagType":"Benign","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Supporting","tagValue":4,"summary":"","infoURL":"","applicable":false},
	"BP5":{"tagType":"Benign","column":"OTHER DATA","tagDescriptor":"Supporting","tagValue":5,"summary":"","infoURL":"","applicable":false},
	"BP6":{"tagType":"Benign","column":"OTHER DATA","tagDescriptor":"Supporting","tagValue":6,"summary":"","infoURL":"","applicable":false},
	"BP7":{"tagType":"Benign","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Supporting","tagValue":7,"summary":"","infoURL":"","applicable":false},
    "BS1":{"tagType":"Benign","column":"POPULATION DATA","tagDescriptor":"Strong","tagValue":1,"summary":"","infoURL":"","applicable":false},
	"BS2":{"tagType":"Benign","column":"POPULATION DATA","tagDescriptor":"Strong","tagValue":2,"summary":"","infoURL":"","applicable":false},
	"BS3":{"tagType":"Benign","column":"FUNCTIONAL DATA","tagDescriptor":"Strong","tagValue":3,"summary":"","infoURL":"","applicable":false},
	"BS4":{"tagType":"Benign","column":"SEGREGATION DATA","tagDescriptor":"Strong","tagValue":4,"summary":"","infoURL":"","applicable":false},
	"BA1":{"tagType":"Benign","column":"POPULATION DATA","tagDescriptor":"Stand Alone","tagValue":1,"summary":"","infoURL":"","applicable":false},
	"PP1":{"tagType":"Pathogenic","column":"SEGREGATION DATA","tagDescriptor":"Supporting","tagValue":1,"summary":"","infoURL":"","applicable":false},
	"PP2":{"tagType":"Pathogenic","column":"FUNCTIONAL DATA","tagDescriptor":"Supporting","tagValue":2,"summary":"","infoURL":"","applicable":false},
	"PP3":{"tagType":"Pathogenic","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Supporting","tagValue":3,"summary":"","infoURL":"","applicable":false},
	"PP4":{"tagType":"Pathogenic","column":"OTHER DATA","tagDescriptor":"Supporting","tagValue":4,"summary":"","infoURL":"","applicable":false},
	"PP5":{"tagType":"Pathogenic","column":"OTHER DATABASE","tagDescriptor":"Supporting","tagValue":5,"summary":"","infoURL":"","applicable":false},
	"PM1":{"tagType":"Pathogenic","column":"FUNCTIONAL DATA","tagDescriptor":"Moderate","tagValue":1,"summary":"","infoURL":"","applicable":false},
	"PM2":{"tagType":"Pathogenic","column":"POPULATION DATA","tagDescriptor":"Moderate","tagValue":2,"summary":"","infoURL":"","applicable":false},
	"PM3":{"tagType":"Pathogenic","column":"ALLELIC DATA","tagDescriptor":"Moderate","tagValue":3,"summary":"","infoURL":"","applicable":false},
	"PM4":{"tagType":"Pathogenic","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Moderate","tagValue":4,"summary":"","infoURL":"","applicable":false},
	"PM5":{"tagType":"Pathogenic","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Moderate","tagValue":5,"summary":"","infoURL":"","applicable":false},
	"PM6":{"tagType":"Pathogenic","column":"DE NOVO DATA","tagDescriptor":"Moderate","tagValue":6,"summary":"","infoURL":"","applicable":false},
	"PS1":{"tagType":"Pathogenic","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Strong","tagValue":1,"summary":"","infoURL":"","applicable":false},
	"PS2":{"tagType":"Pathogenic","column":"DE NOVO DATA","tagDescriptor":"Strong","tagValue":2,"summary":"","infoURL":"","applicable":false},
	"PS3":{"tagType":"Pathogenic","column":"FUNCTIONAL DATA","tagDescriptor":"Strong","tagValue":3,"summary":"","infoURL":"","applicable":false},
	"PS4":{"tagType":"Pathogenic","column":"POPULATION DATA","tagDescriptor":"Strong","tagValue":4,"summary":"","infoURL":"","applicable":false},
	"PVS1":{"tagType":"Pathogenic","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Very Strong","tagValue":1,"summary":"","infoURL":"","applicable":false}
};

var basicEvidenceData_row = {
    "POPULATION DATA": {"indx":"0","evidenceValues":["BA1","BS1","BS2","PM2","PS4"]},
    "COMPUTATIONAL AND PREDICTIVE DATA": {"indx":"1","evidenceValues":["BP1", "BP3","BP4", "BP7","PM4","PM5","PP3","PS1","PVS1"]},
    "FUNCTIONAL DATA": {"indx":"2","evidenceValues":["BS3","PM1","PP2", "PS3"]},
    "SEGREGATION DATA": {"indx":"3","evidenceValues":["BS4","PP1"]},
    "DE NOVO DATA": {"indx":"4","evidenceValues":["PM6","PS2"]},
    "ALLELIC DATA": {"indx":"5","evidenceValues":["BP2","PM3"]},
    "OTHER DATABASE": {"indx":"6","evidenceValues":["BP6","PP5"]},
    "OTHER DATA": {"indx":"7","evidenceValues":["BP5","PP4"]}
}

var basicEvidenceTagTypes_columns = {
    "Benign":{"indx":"0", "cssColorClass":"benignGreen", "cssClass":"greenTD", "tagValues": ["Supporting","Strong","Stand Alone"]},
    "Pathogenic":{"indx":"1", "cssColorClass":"pathogenicityPurple", "cssClass":"pinkTD", "tagValues": ["Supporting","Moderate","Strong","Very Strong"]}
}

function getColumnGroupNameFromIndx(indx){
    let keys = Object.keys(basicEvidenceTagTypes_columns);
    for(let k in keys){
        if(Number(indx) == Number(k)){
            return keys[k];
        }
    }
}

function getRowObjectForIndx(rowIndx){
    return getObjectPropertyBasedOnIndex(rowIndx, basicEvidenceData_row);
}

function getColumnGroupObjectForIndx(columnGroupIndex){
    return getObjectPropertyBasedOnIndex(columnGroupIndex, basicEvidenceTagTypes_columns);
}

function getObjectPropertyBasedOnIndex(indx, object){
    let keys = Object.keys(object);
    for(let k in keys){
        if(Number(indx) == Number(k)){
            return object[keys[k]];
        }
    }
}

function updateSummariesInEvidenceTagDataObj(cspecengineId){
    return new Promise(function (resolve, reject) {
        var xhr = new XMLHttpRequest();	
        let url = "/rest/cspecengines/getRuleSetCriteriaCodes/"+cspecengineId;

        xhr.onload = function() {
            if (xhr.status === 200 && xhr.readyState == 4) {		
                if(xhr.responseText != null && xhr.responseText != ""){
                    let dataObj = JSON.parse(xhr.responseText);
                    updateEvidenceSummaries(dataObj);
                    resolve("ok");                   
                }else{
                    resolve("Unable to get data for CSpecEngine with ID: "+cspecengineId);                   
                }
            } else if (xhr.status !== 200) {
                resolve("Error: Request failed, unbale to get response from cspecengines API, returned status: "+xhr.status);
            }
        };
        xhr.open('GET', url, true);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.send();
    });
}

/* example criteriaCode object:
    {
		"infoURL": "https://cspec.genome.network/cspec/CriteriaCode/id/135638168",
		"genes": [
			"PTEN"
		],
		"name": "BS1",
		"applicable": true,
		"comment": "PTEN EP Specification: To be applied for variants with...."
    }
*/
function updateEvidenceSummaries(criteriaCodesList){
    if(criteriaCodesList == null || criteriaCodesList.length == 0){
        return;
    }

    for(let i in criteriaCodesList){
        let criteriaCode = criteriaCodesList[i];
    
        let evidenceTagName = criteriaCode.name.toUpperCase();
        let evidenceTagObj = evidenceTagDataObj[evidenceTagName];
        if(criteriaCode.comment != null && criteriaCode.comment != '' && criteriaCode.comment != 'null'){
            evidenceTagObj.summary = criteriaCode.comment;
        }
        if(criteriaCode.infoURL != null && criteriaCode.infoURL != ''){
            evidenceTagObj.infoURL = criteriaCode.infoURL;
        }
        if(criteriaCode.applicable != null){
            evidenceTagObj.applicable = criteriaCode.applicable;
        }
    }
}
