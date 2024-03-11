var evidenceTagDataObj = {
	"BP1":{"tagType":"Benign","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Supporting","tagValue":1,"summary":"","infoURL":"","applicable":true},
	"BP2":{"tagType":"Benign","column":"ALLELIC DATA","tagDescriptor":"Supporting","tagValue":2, "summary":"", "infoURL":"","applicable":true},
	"BP3":{"tagType":"Benign","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Supporting","tagValue":3,"summary":"","infoURL":"","applicable":true},
	"BP4":{"tagType":"Benign","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Supporting","tagValue":4,"summary":"","infoURL":"","applicable":true},
	"BP5":{"tagType":"Benign","column":"OTHER DATA","tagDescriptor":"Supporting","tagValue":5,"summary":"","infoURL":"","applicable":true},
	"BP6":{"tagType":"Benign","column":"OTHER DATA","tagDescriptor":"Supporting","tagValue":6,"summary":"","infoURL":"","applicable":true},
	"BP7":{"tagType":"Benign","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Supporting","tagValue":7,"summary":"","infoURL":"","applicable":true},
	"BS1":{"tagType":"Benign","column":"POPULATION DATA","tagDescriptor":"Strong","tagValue":1,"summary":"","infoURL":"","applicable":true},
	"BS2":{"tagType":"Benign","column":"POPULATION DATA","tagDescriptor":"Strong","tagValue":2,"summary":"","infoURL":"","applicable":true},
	"BS3":{"tagType":"Benign","column":"FUNCTIONAL DATA","tagDescriptor":"Strong","tagValue":3,"summary":"","infoURL":"","applicable":true},
	"BS4":{"tagType":"Benign","column":"SEGREGATION DATA","tagDescriptor":"Strong","tagValue":4,"summary":"","infoURL":"","applicable":true},
	"BA1":{"tagType":"Benign","column":"POPULATION DATA","tagDescriptor":"Stand Alone","tagValue":1,"summary":"","infoURL":"","applicable":true},
	"PP1":{"tagType":"Pathogenic","column":"SEGREGATION DATA","tagDescriptor":"Supporting","tagValue":1,"summary":"","infoURL":"","applicable":true},
	"PP2":{"tagType":"Pathogenic","column":"FUNCTIONAL DATA","tagDescriptor":"Supporting","tagValue":2,"summary":"","infoURL":"","applicable":true},
	"PP3":{"tagType":"Pathogenic","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Supporting","tagValue":3,"summary":"","infoURL":"","applicable":true},
	"PP4":{"tagType":"Pathogenic","column":"OTHER DATA","tagDescriptor":"Supporting","tagValue":4,"summary":"","infoURL":"","applicable":true},
	"PP5":{"tagType":"Pathogenic","column":"OTHER DATABASE","tagDescriptor":"Supporting","tagValue":5,"summary":"","infoURL":"","applicable":true},
	"PM1":{"tagType":"Pathogenic","column":"FUNCTIONAL DATA","tagDescriptor":"Moderate","tagValue":1,"summary":"","infoURL":"","applicable":true},
	"PM2":{"tagType":"Pathogenic","column":"POPULATION DATA","tagDescriptor":"Moderate","tagValue":2,"summary":"","infoURL":"","applicable":true},
	"PM3":{"tagType":"Pathogenic","column":"ALLELIC DATA","tagDescriptor":"Moderate","tagValue":3,"summary":"","infoURL":"","applicable":true},
	"PM4":{"tagType":"Pathogenic","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Moderate","tagValue":4,"summary":"","infoURL":"","applicable":true},
	"PM5":{"tagType":"Pathogenic","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Moderate","tagValue":5,"summary":"","infoURL":"","applicable":true},
	"PM6":{"tagType":"Pathogenic","column":"DE NOVO DATA","tagDescriptor":"Moderate","tagValue":6,"summary":"","infoURL":"","applicable":true},
	"PS1":{"tagType":"Pathogenic","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Strong","tagValue":1,"summary":"","infoURL":"","applicable":true},
	"PS2":{"tagType":"Pathogenic","column":"DE NOVO DATA","tagDescriptor":"Strong","tagValue":2,"summary":"","infoURL":"","applicable":true},
	"PS3":{"tagType":"Pathogenic","column":"FUNCTIONAL DATA","tagDescriptor":"Strong","tagValue":3,"summary":"","infoURL":"","applicable":true},
	"PS4":{"tagType":"Pathogenic","column":"POPULATION DATA","tagDescriptor":"Strong","tagValue":4,"summary":"","infoURL":"","applicable":true},
	"PVS1":{"tagType":"Pathogenic","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Very Strong","tagValue":1,"summary":"","infoURL":"","applicable":true}
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
        if(criteriaCode.comment != null && criteriaCode.comment != ''){
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
