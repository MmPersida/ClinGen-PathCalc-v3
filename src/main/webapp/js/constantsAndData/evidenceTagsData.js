var evidenceTagDataObj = {
	"BP1":{"tagType":"Benign","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Supporting","tagValue":1,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}},
	"BP2":{"tagType":"Benign","column":"ALLELIC DATA","tagDescriptor":"Supporting","tagValue":2, "summary":"", "infoURL":"","applicable":false,"applicableModifiers":{}},
	"BP3":{"tagType":"Benign","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Supporting","tagValue":3,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}},
	"BP4":{"tagType":"Benign","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Supporting","tagValue":4,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}},
	"BP5":{"tagType":"Benign","column":"OTHER DATA","tagDescriptor":"Supporting","tagValue":5,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}},
	"BP6":{"tagType":"Benign","column":"OTHER DATA","tagDescriptor":"Supporting","tagValue":6,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}},
	"BP7":{"tagType":"Benign","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Supporting","tagValue":7,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}},
    "BS1":{"tagType":"Benign","column":"POPULATION DATA","tagDescriptor":"Strong","tagValue":1,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}},
	"BS2":{"tagType":"Benign","column":"POPULATION DATA","tagDescriptor":"Strong","tagValue":2,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}},
	"BS3":{"tagType":"Benign","column":"FUNCTIONAL DATA","tagDescriptor":"Strong","tagValue":3,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}},
	"BS4":{"tagType":"Benign","column":"SEGREGATION DATA","tagDescriptor":"Strong","tagValue":4,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}},
	"BA1":{"tagType":"Benign","column":"POPULATION DATA","tagDescriptor":"Stand Alone","tagValue":1,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}},
	"PP1":{"tagType":"Pathogenic","column":"SEGREGATION DATA","tagDescriptor":"Supporting","tagValue":1,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}},
	"PP2":{"tagType":"Pathogenic","column":"FUNCTIONAL DATA","tagDescriptor":"Supporting","tagValue":2,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}},
	"PP3":{"tagType":"Pathogenic","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Supporting","tagValue":3,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}},
	"PP4":{"tagType":"Pathogenic","column":"OTHER DATA","tagDescriptor":"Supporting","tagValue":4,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}},
	"PP5":{"tagType":"Pathogenic","column":"OTHER DATABASE","tagDescriptor":"Supporting","tagValue":5,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}},
	"PM1":{"tagType":"Pathogenic","column":"FUNCTIONAL DATA","tagDescriptor":"Moderate","tagValue":1,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}},
	"PM2":{"tagType":"Pathogenic","column":"POPULATION DATA","tagDescriptor":"Moderate","tagValue":2,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}},
	"PM3":{"tagType":"Pathogenic","column":"ALLELIC DATA","tagDescriptor":"Moderate","tagValue":3,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}},
	"PM4":{"tagType":"Pathogenic","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Moderate","tagValue":4,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}},
	"PM5":{"tagType":"Pathogenic","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Moderate","tagValue":5,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}},
	"PM6":{"tagType":"Pathogenic","column":"DE NOVO DATA","tagDescriptor":"Moderate","tagValue":6,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}},
	"PS1":{"tagType":"Pathogenic","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Strong","tagValue":1,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}},
	"PS2":{"tagType":"Pathogenic","column":"DE NOVO DATA","tagDescriptor":"Strong","tagValue":2,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}},
	"PS3":{"tagType":"Pathogenic","column":"FUNCTIONAL DATA","tagDescriptor":"Strong","tagValue":3,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}},
	"PS4":{"tagType":"Pathogenic","column":"POPULATION DATA","tagDescriptor":"Strong","tagValue":4,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}},
	"PVS1":{"tagType":"Pathogenic","column":"COMPUTATIONAL AND PREDICTIVE DATA","tagDescriptor":"Very Strong","tagValue":1,"summary":"","infoURL":"","applicable":false,"applicableModifiers":{}}
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
        let url = "/pcalc/rest/cspecengines/getRuleSetCriteriaCodes/"+cspecengineId;

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
		"applicableTags": {
			"Supporting": {
				"instructions": "",
				"applicability": "Applicable",
				"text": "Also applicable to **intronic variants outside the splice consensus sequence (-4 and +7 outward)** for which splicing prediction algorithms predict no impact to the splice consensus sequence NOR the creation of a new splice site AND the nucleotide is not highly conserved.\n\nRule can be combined with BP4 to make a variant likely benign per Richards _et al._ 2015[<sup>1</sup>](#pmid_25741868).",
				"status": "approved"
			}
		},
		"infoURL": "https://cspec.genome.network/cspec/CriteriaCode/id/135637794",
		"genes": [
			"MYH7"
		],
		"name": "BP7",
		"applicable": true,
		"diseases": [
			"MONDO:0004994",
			"MONDO:0005021",
			"MONDO:0005045",
			"MONDO:0005201",
			"MONDO:0009144"
		],
		"comment": "A synonymous variant for which splicing prediction algorithms predict no impact to the splice consensus sequence nor the creation of a new splice site AND the nucleotide is not highly conserved."
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
        if(criteriaCode.applicableTags != null && !isObjectEmpty(criteriaCode.applicableTags)){
            evidenceTagObj.applicableModifiers = criteriaCode.applicableTags;
        }
    }
}
