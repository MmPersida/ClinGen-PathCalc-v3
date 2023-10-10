function processCSpecRuleSet(assertionsRulesObj, evidenceSet){
    if(assertionsRulesObj.data == null || assertionsRulesObj.data.entContent == null){
        alert("Error: Unable to get CSpec Engine rule set!");
        return
    }

    var entContent = assertionsRulesObj.data.entContent;
    if(entContent.rules == null || entContent.rules.mainRules == null || entContent.rules.mainRules.length == 0){
        alert("Error: Unable to get CSpec Engine rule set!");
        return
    }
    
    var reachedRuleSet = {};
    var failedRuleSet = {};

    var mainRules = entContent.rules.mainRules;
    var n = mainRules.length;
    for(var i=0; i<n; i++){
        var rule = mainRules[i];
        var currentInference = rule.inference;

        //individual rules
        var extractedCondLabels = '';
        var extractedEvidenceTableColumnMarkers = '';
        var totalCondVal = 0;
        var conditions = rule.conditions;
        var passedConditions = conditions.length;
        var j = passedConditions;
        for(var k=0; k<j; k++){
            var c = conditions[k];
            var cValue = c.condition; 
            var currentCondNumValue = getValueForCondition(cValue);
            var partitionPathVal = c.partitionPath;
            var evidenceFailed = true;

            if(evidenceSet != null){
                //evidenceSet co tines all of the currently add evidences
                if(evidenceSet[partitionPathVal] != null){
                    var totalNumOfEvidenceAdded = Number(evidenceSet[partitionPathVal]);
                    if(totalNumOfEvidenceAdded < currentCondNumValue){
                        //there arent enough added evidences to satisfy this condition, there for the condition failed to pass
                        passedConditions--;
                    }else{
                        evidenceFailed = false;
                    }             
                }else{
                    passedConditions--;               
                }
            }else{
                passedConditions--;
            }

            if(evidenceFailed){
                totalCondVal = totalCondVal + currentCondNumValue;
            }         

            extractedEvidenceTableColumnMarkers = extractedEvidenceTableColumnMarkers + determineEvidenceTableColumnMarkerValue(partitionPathVal)+"_";            

            if(k == (j-1)){
                extractedCondLabels = extractedCondLabels + partitionPathVal+""+cValue;
            }else{
                extractedCondLabels = extractedCondLabels + partitionPathVal+""+cValue+" & ";
            }
        }

        if(totalCondVal > 2){
            continue;
        }

        if(evidenceSet != null){
            if(passedConditions == j){
                if(reachedRuleSet[currentInference] == null){
                    reachedRuleSet[currentInference] = [];
                }
                var conditionData = {
                    "label": extractedCondLabels,
                    "evidenceTableMarkers": extractedEvidenceTableColumnMarkers,
                }
                reachedRuleSet[currentInference].push(conditionData);      
            }else{
                if(failedRuleSet[currentInference] == null){
                    failedRuleSet[currentInference] = [];
                }
                var conditionData = {
                    "label": extractedCondLabels,
                    "evidenceTableMarkers": extractedEvidenceTableColumnMarkers,
                    "conditionsLeft": totalCondVal
                }
                failedRuleSet[currentInference].push(conditionData);  
            }
        }else{
            if(passedConditions < j){
                if(failedRuleSet[currentInference] == null){
                    failedRuleSet[currentInference] = [];
                }
                var conditionData = {
                    "label": extractedCondLabels,
                    "evidenceTableMarkers": extractedEvidenceTableColumnMarkers,
                    "conditionsLeft": totalCondVal
                }
                failedRuleSet[currentInference].push(conditionData);  
            }
        }
    }

    if(reachedRuleSet){
        displayReachedRules(reachedRuleSet);
    }
    if(failedRuleSet != null){
        displayFailedRules(sortFailedRuleSet(failedRuleSet));
    }
}

function getValueForCondition(cValue){
    var baseVal = 0;
    switch(cValue){
        case '==1': baseVal = 1; break;
        case '>=1': baseVal = 1; break;
        case '==2': baseVal = 2; break;
        case '>=2': baseVal = 2; break;
        case '==3': baseVal = 3; break;
        case '>=3': baseVal = 3; break;
        case '==4': baseVal = 4; break;
        case '>=4': baseVal = 4; break;
        case '==5': baseVal = 5; break;
        case '>=5': baseVal = 5; break;
        case '==6': baseVal = 6; break;
        case '>=6': baseVal = 6; break;
    }
    return baseVal;
}

function sortFailedRuleSet(ruleSet){
    for(let r in ruleSet){
        var inferenceRuleSet = ruleSet[r];
        if(inferenceRuleSet.length < 1){
            continue;
        }

        while(true){
            var switchMade = false;
            var n = inferenceRuleSet.length;
            for(let i=0; n>i; i++){                
                var rule = inferenceRuleSet[i];
                var currentCondLeft = Number(rule.conditionsLeft);
                if(inferenceRuleSet[(i+1)] != null && currentCondLeft > Number(inferenceRuleSet[(i+1)].conditionsLeft)){
                    var tempRule = inferenceRuleSet[(i+1)];
                    inferenceRuleSet[(i+1)] = rule;   
                    inferenceRuleSet[i] = tempRule;                                 
                    switchMade = true;
                }
            }
            if(!switchMade){
                break;
            }
        }
    }
    return ruleSet;
}

function determineEvidenceTableColumnMarkerValue(partitionPathVal){
    let partitionPathValArray = partitionPathVal.split('.');
    var pathBasic = partitionPathValArray[0];
    var pathDetail = partitionPathValArray[1];

    var markerValue = '';

    if(pathBasic == 'Benign'){
        markerValue = markerValue + '1';

        if(pathDetail == 'Supporting'){
            markerValue = markerValue + '1';
        }else if(pathDetail == 'Strong'){
            markerValue = markerValue + '2';
        }else if(pathDetail == 'Stand Alone'){
            markerValue = markerValue + '3';
        }

    }else if(pathBasic == 'Pathogenic'){
        markerValue = markerValue + '2';

        if(pathDetail == 'Supporting'){
            markerValue = markerValue + '1';
        }else if(pathDetail == 'Moderate'){
            markerValue = markerValue + '2';
        }else if(pathDetail == 'Strong'){
            markerValue = markerValue + '3';
        }else if(pathDetail == 'Very Strong'){
            markerValue = markerValue + '4';
        }
    }

    return markerValue;
}

function displayReachedRules(customRuleSet){
    displayCustomRuleSet(customRuleSet, "reachedAssertionTable", "pass", "Assertion(s) Reached");
}

function displayFailedRules(customRuleSet){
    displayCustomRuleSet(customRuleSet, "assertionRequiringEvidenceTable", "fail", "Assertion(s) Requiring Additional Evidence")
}

function displayCustomRuleSet(customRuleSet, tableID, tableRulesType, tableName){
    var assertionRequiringEvidenceTable = document.getElementById(tableID);
    clearSelectChooser(assertionRequiringEvidenceTable);

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

    //this th does not exapand to 4 columns for some reasone
    tr = document.createElement('tr');
        th = document.createElement('th');
        th.colSpan=2;
        th.innerHTML = tableName;
    tr.appendChild(th);   
    assertionRequiringEvidenceTable.appendChild(tr); 

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
                            innerTdInput.addEventListener("click", function(){ selectEvidenceColumnToBeMarked(this); });
                            //innerTdInput.addEventListener("mouseover", function(){ selectEvidenceColumnToBeMarked(this); });
                            innerTdInput.value = rulesSetObj.evidenceTableMarkers;
                        innerTD.appendChild(innerTdInput)
                    innerTR.appendChild(innerTD);    
                    innerTable.appendChild(innerTR);
                }

            td.appendChild(innerTable);
        tr.appendChild(td); 

        assertionRequiringEvidenceTable.appendChild(tr);
    }
}
