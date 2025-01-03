function renderEvidenceTable(loadedEvidenceSet){
    let evidenceTable = document.getElementById("evidenceTable");

    createEvidenceTableHeader(evidenceTable);
    createEvidenceTableRows(evidenceTable, loadedEvidenceSet);
}

function createEvidenceTableHeader(evidenceTable){
    var tr = null;
    var td = null;

    let conditionValue  = document.getElementById("evidenceDocValue").innerHTML.trim();

    tr = document.createElement("tr"); 
        td = document.createElement("td");
        td.rowSpan = "2";
        td.style.backgroundColor = "rgb(14, 120, 240)";
        td.style.textAlign = "left";
        td.style.width = "25%";
            let pPhenotype = document.createElement("p");
            pPhenotype.id = "phenotypeLabel";
            pPhenotype.innerHTML = "Phenotype: "+conditionValue;
        td.appendChild(pPhenotype);
    tr.appendChild(td);

    let columnNamesKeys = Object.keys(basicEvidenceTagTypes_columns);
    for(let cnIndx in columnNamesKeys){    
        var columnName = columnNamesKeys[cnIndx];   
        var evidenceColumnGroup = basicEvidenceTagTypes_columns[columnName];

        td = document.createElement("td");
        td.colSpan = evidenceColumnGroup.tagValues.length;
        td.innerHTML = columnName;
        td.className = evidenceColumnGroup.cssClass;
        tr.appendChild(td);
    }
    evidenceTable.appendChild(tr);

    tr = document.createElement("tr"); 
    for(let cnIndx in columnNamesKeys){    
        var columnName = columnNamesKeys[cnIndx];   
        var evidenceColumnGroup = basicEvidenceTagTypes_columns[columnName];

        var eColumnsNames = evidenceColumnGroup.tagValues;
        var j = eColumnsNames.length;
        for(var k=0; k<j; k++){
           td = document.createElement("td");
            td.className = "lightblueTD finalEvidenceColumns";
            td.innerHTML = eColumnsNames[k];
           tr.appendChild(td);           
        }
    }
    evidenceTable.appendChild(tr);
}

function createEvidenceTableRows(evidenceTable, loadedEvidenceSet){
    let evidenceTagsToBeDisabled = null;
    if(loadedEvidenceSet != null && loadedEvidenceSet.length > 0){
        evidenceTagsToBeDisabled =  addOtherModifiedTagValuesToBeMarkedAsUsed(loadedEvidenceSet);
    }

    var tr = null; 
    var td = null; 
    var divNumOfEvdTags; //this div needs its innerHTML value to be set dynamically
    var div = null; 
    var img = null;
    var p = null;
    var ul = null;
    var cellID = null;
    var validTags = null;

    let bedKeys = Object.keys(basicEvidenceData_row);
    for(let bedKey in bedKeys){
        //start the row, add the first column with the buttons
        let eTableRowName = bedKeys[bedKey];
        var eTableRow = basicEvidenceData_row[eTableRowName];

        //creating the actual table row
        tr = document.createElement("tr");
            //adding the buttons in the far left columns
            td = document.createElement("td");
                td.className = "lightblueTD";
                    div = document.createElement("div");            
                    div.className = "evidenceTableBtnDiv"; 
                    div.setAttribute("data-value", (eTableRow.indx));
                    div.addEventListener("click", function(){ openEvidenceTagPopUp(this) });
                        img = document.createElement("img");
                        img.src = "../images/data_button.png";
                    div.appendChild(img);    
                        p = document.createElement("p");
                        p.innerHTML = eTableRowName; 
                    div.appendChild(p); 
                td.appendChild(div); 
        tr.appendChild(td);

        let bettKeys = Object.keys(basicEvidenceTagTypes_columns);
        for(let bettKey in bettKeys){  
            //continue adding columns (tabl cells) to the created row
            //two main column groups, example: Benign and Pathogenic, this is just to get the IDs of the column groups for referencing
            let columnGroupName = bettKeys[bettKey];  
            var evidenceColumnGroup = basicEvidenceTagTypes_columns[columnGroupName];
            currentColumnGroupObj = evidenceColumnGroup;

            var eColumnsNames = evidenceColumnGroup.tagValues;
            //in each iteration this loop handles a single cell in the table row that was just created, one single <td> element 
            var m = eColumnsNames.length;
            for(var v=0; v<m; v++){    
                currentColumnName = eColumnsNames[v];

                //add the actual columns (<td>) to the row from the main column grups, example:  Supporting, Strong, Moderate... etc.                
                cellID = (eTableRow.indx)+"_"+(evidenceColumnGroup.indx)+"_"+v //example: 0_1_1 (row:0, columnGroup:1, columnsName:1)
                td = document.createElement("td");
                td.id = cellID;               
                td.addEventListener("click", function(){ openEvidenceCellPopUp(this) });  
                    //add the div that will hold the number of evd. tags used, if any            
                    divNumOfEvdTags = document.createElement("div");
                    divNumOfEvdTags.id = getEvidenceCellDiv(cellID);
                td.appendChild(divNumOfEvdTags);   
                    //add the div that will hold the allowed evidence tag names/values for this cell only   
                    div = document.createElement("div");
                    div.className="dropdownEvidenceCodes"
                
                        //ul elemenet that will hold the allowed evid. tag values for this cell
                        ul = document.createElement("ul");
                        ul.id= getTagValueIds(cellID);  //example: tagValues_0_1_1

                        //get all of the valid evidence tag names for this table cell, example: BS1 - Supporting, PM2 - Strong... etc.
                        validTags = getValidEvidenceTagsForThisCell(eTableRow.evidenceValues, columnGroupName, currentColumnName)
                        if(validTags != null && validTags.length > 0){
                            let markTheCellForUsedTags = false; //needs to be triggered once if any of the allowed evd. tags in this cell are actually used
                            let countUsedEvdTags = 0; //counts the number of used evd. tags for this cell
                            for(let g in validTags){
                                let evidenceTagName = validTags[g];
                                let usedEvidenceObj = returnThisEvidenceIfUsed(evidenceTagName, loadedEvidenceSet);
                                let tagToBeDiasabled = false;

                                li = document.createElement("li");
                                li.id = cellID+"_"+evidenceTagName; //example: 0_1_1_BS1 - Supporting  -- this can now be referenced back to the cell                              
                                //set css class names for visual effect
                                if(usedEvidenceObj != null){
                                    countUsedEvdTags++;
                                    if(!markTheCellForUsedTags){
                                        markTheCellForUsedTags = true;
                                    }

                                    var evdData = {
                                        'evdTag': usedEvidenceObj.fullLabelForFE,
                                        'evdSummary': usedEvidenceObj.summary
                                    }
                                    let tempArray = [evdData];                                   
                                    addEvidenceTagApliedDivs(tempArray);
                                    addToPathogenicityEvidencesDoc(tempArray, cellID); //add the current tag (as part of an array) to the global object thats holds all of the added tags  
                                }else{
                                    //the exact evidence tag might not be used but it might need to be disabled
                                    tagToBeDiasabled = shouldThisEvidenceBeDisabled(evidenceTagName, evidenceTagsToBeDisabled);
                                }

                                if(usedEvidenceObj != null || tagToBeDiasabled){
                                    li.style.color = "red";                            
                                }else{
                                    li.style.color = "black";
                                }

                                li.innerText = evidenceTagName;
                                ul.appendChild(li);
                            }
                            
                            if(markTheCellForUsedTags){
                                if(countUsedEvdTags > 0){
                                    divNumOfEvdTags.innerHTML = countUsedEvdTags; //set a value only if a evg. tag is used
                                } 
                                //set the css class for color of the cell acording to the main column group, example: bening is green                             
                                td.className = evidenceColumnGroup.cssClass+" "+columnGroupName+""+removeWhiteSpace(eColumnsNames[v]);
                            }else{
                                td.className = "whiteTD "+columnGroupName+""+removeWhiteSpace(eColumnsNames[v]);
                            }

                            div.innerText = "Evidence tags:";
                            div.appendChild(ul);   
                        }else{
                            div.innerText = "No valid evidence tags!";
                            td.className = "lightGrayTD "+columnGroupName+""+removeWhiteSpace(eColumnsNames[v]);
                        }
                td.appendChild(div);

                tr.appendChild(td);           
            }
        }
        evidenceTable.appendChild(tr);
    }
}

function addOtherModifiedTagValuesToBeMarkedAsUsed(loadedEvidenceSet){
    let evidenceTagsToBeDisabled = [];
    for(let g in loadedEvidenceSet){
        let basicEvdcValue = loadedEvidenceSet[g].type;
        var eTagObj = evidenceTagDataObj[basicEvdcValue];
        var basicTagSubTypes = basicEvidenceTagTypes_columns[eTagObj.tagType].tagValues;
        let modEvdTagVals = determineModifiedEvidenceTags(eTagObj, basicTagSubTypes, basicEvdcValue);
        modEvdTagVals.push(basicEvdcValue);
        evidenceTagsToBeDisabled = evidenceTagsToBeDisabled.concat(modEvdTagVals);
    }
    return evidenceTagsToBeDisabled;
}

function returnThisEvidenceIfUsed(evidenceTag, loadedEvidenceTags){
    if(loadedEvidenceTags == null || loadedEvidenceTags.length == 0){
        return null;
    }
    for(let i in loadedEvidenceTags){
        if(loadedEvidenceTags[i].fullLabelForFE == evidenceTag){
            return loadedEvidenceTags[i];
        }
    }
    return null;
} 

function shouldThisEvidenceBeDisabled(evidenceTag, evidenceTagsToBeDisabled){
    if(evidenceTagsToBeDisabled == null || evidenceTagsToBeDisabled.length == 0){
        return false;
    }
    for(let i in evidenceTagsToBeDisabled){
        if(evidenceTagsToBeDisabled[i] == evidenceTag){
            return true;
        }
    }
    return false;
}

