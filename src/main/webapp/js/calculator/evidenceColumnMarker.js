var paintedColumns = [];

//marks columns in the evidence table based on the conditions that are checked in the "Gudlines & Conclusions" table
function selectEvidenceColumnToBeMarked(checkboxElem) {
    var checkboxes = document.getElementsByName('guidelineAssertCB')
    checkboxes.forEach((item) => {
        if (item !== checkboxElem) item.checked = false
    })

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
        //no columns are previously marked
        markEvidenceColumns(paintedColumns, null);
    } 

    var paint = true;
    try {
        if(paintedColumns.length > 0 && paintedColumns.length == listOfColumns.length){
            if(paintedColumns.length == 1 && paintedColumns[0] == listOfColumns[0]){
                paint = false;
            }else if(paintedColumns.length == 2 && paintedColumns[0] == listOfColumns[0] && paintedColumns[1] == listOfColumns[1]){
                paint = false;
            }
        }
    }catch(err) {
    }

    if(paint){
        markEvidenceColumns(listOfColumns, 'lightblue');
        paintedColumns = listOfColumns;
    }
}

function markEvidenceColumns(listOfColumns, color){          
    for(let i in listOfColumns){
        let columnClassName = listOfColumns[i];
        let columnsByClassName = document.getElementsByClassName(columnClassName);

        let n = columnsByClassName.length;
        for(var k = 0; k<n; k++){
            let tdElem = columnsByClassName[k];

            if(color == null){
                var classList = tdElem.classList;
                var colorClassFound = '';
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
            }else{
                tdElem.style.backgroundColor = color; 
            }                                
        }
    }
}

