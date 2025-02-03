//get the specific cspec engine rules for this intepretation
function determineRuleSetAssertions(cspecengineId, evidenceMap){
    var postData = {
        "cspecengineId": cspecengineId,
        "evidenceMap":  evidenceMap
    }
    postData = JSON.stringify(postData);

    var xhr = new XMLHttpRequest();	
    let url = "/pcalc/rest/cspecengines/getAssertionsFromRuleSet";

    xhr.onload = function() {
        if (xhr.status === 200 && xhr.readyState == 4) {
            if(xhr.responseText != null && xhr.responseText  != ''){
                let assertedRules = JSON.parse(xhr.responseText);
                if(assertedRules != null){
                    displayCSpecRuleSetForGuidlinesTable(assertedRules); //now we edit the Assertions table in Guidlines Conclusions section                                
                }else{
                    clearCurrentEvidencesAndAssertionsTable();
                }					
            }else{
                console.log("Value of response text is null!")
            }				
        }else if (xhr.status !== 200) {
            alert('Request failed, returned status of ' + xhr.status);
        }
    };
    xhr.open("POST", url, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.withCredentials = true;
    xhr.send(postData);
}

function displayCSpecRuleSetForGuidlinesTable(assertedRules){
    if(assertedRules.reachedRuleSetMap != null){
        //display reached rules
        displayCustomRuleSet(assertedRules.reachedRuleSetMap, "reachedAssertionTable", "pass", "Combining Criteria Reached");
    }
    if(assertedRules.failedRuleSetMap != null){
        //display NOT reached rules
        displayCustomRuleSet(assertedRules.failedRuleSetMap, "assertionRequiringEvidenceTable", "fail", "Combining Criteria Requiring Additional Evidence")
    }
}

function clearCurrentEvidencesAndAssertionsTable(){
    clearSelectChooser(document.getElementById("reachedAssertionTable"));
    clearSelectChooser(document.getElementById("assertionRequiringEvidenceTable"));
    clearSelectChooser(document.getElementById("evidenceTable"));
    clearSelectChooser(document.getElementById("tagApliedContainer"));
}

function displayCustomRuleSet(customRuleSet, tableID, tableRulesType, tableName){
    var assertionsTable = document.getElementById(tableID);
    clearSelectChooser(assertionsTable);

    var tr = null;
    var th = null;
    var td = null;
    var innerTable = null
    var innerTR = null
    var innerTD = null
    var innerTdInput = null; 

    if(customRuleSet == null){
        return;
    }

    var myKeys = Object.keys(customRuleSet)
    var n = myKeys.length;
    if(n == 0){
        return;
    }

    tr = document.createElement('tr');
        th = document.createElement('th');
        th.colSpan=2;
        th.innerHTML = tableName;
    tr.appendChild(th);   
    assertionsTable.appendChild(tr); 

    for (var i = 0; i < n; i++){
        var key = myKeys[i];
        var rulesArray =customRuleSet[key];

        tr = document.createElement('tr');
            td = document.createElement('td');
            td.className = "guidlinesTDConclusion";
            td.innerHTML = key;
        tr.appendChild(td); 
        
            td = document.createElement('td');
            td.className = "guidlinesTDCombined";
            td.colspan=2;
            td.style.fontWeight = "600";
                innerTable = document.createElement('table');
                innerTable.className="assertionsSubTable";
            
                var j = rulesArray.length;
                for (var k = 0; k < j; k++){
                    var rulesSetObj = rulesArray[k];
                    innerTR = document.createElement('tr');
                        innerTD = document.createElement('td');
                        innerTD.className = "guidlinesTDConditionCombined assertionTDAlignCenter";
                        if(tableRulesType == "fail"){
                            innerTD.innerHTML = rulesSetObj.conditionsLeft;
                        }
                    innerTR.appendChild(innerTD);   
                        innerTD = document.createElement('td');
                        innerTD.className = "guidlinesTDRulesCombined";
                        innerTD.innerHTML = rulesSetObj.label;
                    innerTR.appendChild(innerTD);    
                        innerTD = document.createElement('td');
                        innerTD.className = "assertionsSubTableInput assertionTDAlignCenter";
                            innerTdInput = document.createElement('input');
                            innerTdInput.type = "checkbox";
                            innerTdInput.name = "guidelineAssertCB";
                            innerTdInput.value = rulesSetObj.evidenceTableMarkers;
                            innerTdInput.addEventListener("click", function(){ selectEvidenceColumnToBeMarked(this); });
                            //innerTdInput.addEventListener("mouseover", function(){ selectEvidenceColumnToBeMarked(this); });
                        innerTD.appendChild(innerTdInput)
                    innerTR.appendChild(innerTD);    
                    innerTable.appendChild(innerTR);
                }

            td.appendChild(innerTable);
        tr.appendChild(td); 

        assertionsTable.appendChild(tr);
    }
}
