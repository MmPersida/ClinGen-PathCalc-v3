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

    let engineEnabled = "No";
    if(engineInfo.enabled){
        engineEnabled = "Yes";
    }

    let htmlContentMessage ='<span style="font-weight:bold; color:rgba(50,110,150);">Engine ID:</span> '+engineInfo.engineId+'</br></br>'+
                            '<span style="font-weight:bold; color:rgba(50,110,150);">Organization:</span> '+engineInfo.organizationName+'</br></br>'+
                            '<span style="font-weight:bold; color:rgba(50,110,150);">Summary:</span> '+engineInfo.engineSummary+'</br></br>'+
                            '<span style="font-weight:bold; color:rgba(50,110,150);">Enabled:</span> '+engineEnabled+'</br></br>'+
                            '<span style="font-weight:bold; color:rgba(50,110,150);">RuleSet URL:</span> '+engineInfo.ruleSetURL+'</br></br>'+
                            '<span style="font-weight:bold; color:rgba(50,110,150);">Related genes:</span></br>'+relatedGenes;
    return htmlContentMessage;
}


