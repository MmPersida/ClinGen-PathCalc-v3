function openDeleteCassificationPopUp(){
    document.getElementById("openDeleteClassificationModalBtn").click();
} 

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


function openSpecificationDetailsPoPup(){
    document.getElementById("openSpecificationDetailsModal").click();
}

function closeSpecificationDetailsPoPup(){
  document.getElementById("specificationDetailsDiv").innerHTML = '';
  document.getElementById("openSpecificationDetailsModal").click();
}

async function createCSpecEngineInfoContent(cspecengineId){   
    let specificationDetailsDiv = document.getElementById("specificationDetailsDiv");
    if(specificationDetailsDiv == null){
        return;
    }

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
            relatedGenes += '<a style="display:inline-block;" href="https://genboree.org/cfde-gene-dev/Gene/id/'+g.geneName+'" target="_blank"><p>'+g.geneName+'</p><a/><br>';
        }
    }

    let engineEnabled = 'This specification is currently not available for Variant Classification!  <img style=\"width: 15px; height: 15px; margin-left:5px;\"} src=\"../images/warning_button.png\">';
    if(engineInfo.enabled){
        engineEnabled = "This specification is approved for Variant Classification";
    }

    let htmlContentMessage ='<span style="font-weight:bold; color:rgba(50,110,150);">Specification ID:</span> '+engineInfo.engineId+'</br></br>'+
                            '<span style="font-weight:bold; color:rgba(50,110,150);">Affiliation:</span> <a style="display:inline-block;" href="'+engineInfo.organizationLink+'" target=_blank><p>'+engineInfo.organizationName+'</p><a/></br></br>'+
                            '<span style="font-weight:bold; color:rgba(50,110,150);">Title:</span> '+engineInfo.engineSummary+'</br></br>'+
                            '<span style="font-weight:bold; color:rgba(50,110,150);">Status:</span> '+engineEnabled+'</br></br>'+
                            '<span style="font-weight:bold; color:rgba(50,110,150);">Specification details:</span> <a style="display:inline-block;" href=https://cspec.genome.network/cspec/ui/svi/doc/'+engineInfo.engineId+' target=_blank><p>Specification Link</p><a/></br></br>'+
                            '<span style="font-weight:bold; color:rgba(50,110,150);">Related genes:</span> '+relatedGenes;
    
    specificationDetailsDiv.innerHTML = htmlContentMessage;
}
