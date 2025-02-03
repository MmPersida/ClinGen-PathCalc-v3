var paintedColumns = [];

//marks columns in the evidence table based on the conditions that are checked in the "Gudlines & Conclusions" table
function selectEvidenceColumnToBeMarked(checkboxElem) {
    //deselect all other checkboxes
    var checkboxes = document.getElementsByName('guidelineAssertCB')
    checkboxes.forEach((item) => {
        if (item !== checkboxElem) item.checked = false
    })

    if(checkboxElem.checked == false){
        //the box is unchecked, deselect all fields and return
        markEvidenceColumns(paintedColumns, null);
        return;
    }


    let columnsToMark = [];
    let cbValueArray = checkboxElem.value.split("_");
    let n = cbValueArray.length;
    for(var i=0; i<n; i++){
        var value = cbValueArray[i];
        if(value == null || value == ''){
            continue;
        }

        switch(value){
            case '11': columnsToMark.push("BenignSupporting"); break;
            case '12': columnsToMark.push("BenignStrong"); break;
            case '13': columnsToMark.push("BenignStandAlone"); break;
            case '21': columnsToMark.push("PathogenicSupporting"); break;
            case '22': columnsToMark.push("PathogenicModerate"); break;
            case '23': columnsToMark.push("PathogenicStrong"); break;
            case '24': columnsToMark.push("PathogenicVeryStrong"); break;
        }
    }

    preapairToMarkPatEvidenceColumns(columnsToMark);
}

function preapairToMarkPatEvidenceColumns(listOfColumns){   
    if(paintedColumns != null && paintedColumns.length > 0){
        //deselect all previous columns that are not in the list

        //get the column class names that are not in the current list, if any, and deselect them now
        
        let columnsToDeselect = [];
        for(let i in listOfColumns){
            let columnClassName = listOfColumns[i];

            for(let j in paintedColumns){
                let paintedColumnClassName = paintedColumns[j];
                if(columnClassName != paintedColumnClassName){
                    columnsToDeselect.push(paintedColumnClassName);
                }
            }
        }
        markEvidenceColumns(columnsToDeselect, null);
    } 

    markEvidenceColumns(listOfColumns, 'lightblue');
    paintedColumns = listOfColumns;   
}

function markEvidenceColumns(listOfColumns, color){          
    for(let i in listOfColumns){
        let columnClassName = listOfColumns[i];
        let columnsByClassName = document.getElementsByClassName(columnClassName);

        let n = columnsByClassName.length;
        for(var k = 0; k<n; k++){
            let tdElem = columnsByClassName[k];

            if(color == null){
                deselectTheTableField(tdElem);
            }else if(color == 'lightblue'){
                tdElem.style.backgroundColor = color;
            }                                
        }
    }
}

function deselectTheTableField(tdElem){
    var classList = tdElem.classList;
    for(let j in classList){
        var cName = classList[j];
        if(cName == 'whiteTD'){
            tdElem.style.backgroundColor = 'white'; 
            break;
        } 
        if(cName == 'lightGrayTD'){
            tdElem.style.backgroundColor = 'rgb(232,232,232)'; 
            break;
        }
        if(cName == 'greenTD'){
            tdElem.style.backgroundColor = 'rgb(0, 153, 0)'; 
            break;
        }
    }
}

