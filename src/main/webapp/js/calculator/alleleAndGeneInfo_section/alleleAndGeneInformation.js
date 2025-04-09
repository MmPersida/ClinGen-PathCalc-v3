async function displayAlleleAndGeneInformation(variantCAID, alleleDataObj){
    //Allele
    let externalRecordsNameAndLink = null;
    if(alleleDataObj != null){
        let externalRecords_1 = document.getElementById("externalRecords_1");
        externalRecordsNameAndLink = extractAlleleExtRecordsNameAndLink(variantCAID, alleleDataObj);
        if(externalRecordsNameAndLink != null){
            createPCExternalLinks(externalRecordsNameAndLink, externalRecords_1, "alleleGeneLinksCalc");

            let predictorScoreDivBtn = document.getElementById("predictorScoreDivBtn");
            let alleleFerquencyDivBtn = document.getElementById("alleleFerquencyDivBtn");

            if(externalRecordsNameAndLink.MyVariantInfo_hg19 != null){
                let hgvsValue = externalRecordsNameAndLink.MyVariantInfo_hg19.id;
                predictorScoreDivBtn.setAttribute('data-value', hgvsValue);
                alleleFerquencyDivBtn.setAttribute('data-value', variantCAID);
            }else{
                predictorScoreDivBtn.style.display = "none";
                alleleFerquencyDivBtn.style.display = "none";
            }
        }
    }

    createSecondSectionOfExternalRecords(variantCAID);          

    //HGVS - Genomic Alleles
    document.getElementById("hgvsLinksForGenomicAlleles").appendChild(createGenomicAllelesDiv(alleleDataObj));
    //HGVS - Transcript Alleles
    document.getElementById("hgvsLinksForTranscriptAlleles").appendChild(crateVariantTranscriptAllelesDiv(alleleDataObj));

    //realated genes table
     if(externalRecordsNameAndLink != null){
        createRelatedGeneTable(externalRecordsNameAndLink, alleleDataObj.communityStandardTitle[0]);
    }
}

function createGenomicAllelesDiv(variantData){
    var externalRecordsDiv = document.createElement("div");
    externalRecordsDiv.className = "externalRecordsDiv";

    var hgvsLinkString = "";
    if(variantData.genomicAlleles != null && variantData.genomicAlleles.length > 0){
        var genomicAlleles = variantData.genomicAlleles;
        var n = genomicAlleles.length;
        for (var i = 0; i < n; i++){
            var obj = genomicAlleles[i];
            var hgvsLinks = obj.hgvs;
            var j = hgvsLinks.length;
            for (var k = 0; k < j; k++){
                var hgvsLink = "<p>"+hgvsLinks[k]+"</p>";
                hgvsLinkString = hgvsLinkString+hgvsLink+" ";
            }
        }
    }

    if(hgvsLinkString != ""){
        externalRecordsDiv.innerHTML = hgvsLinkString;
    }
    return externalRecordsDiv;
}

function crateVariantTranscriptAllelesDiv(variantData){
    var externalRecordsDiv = document.createElement("div");
    externalRecordsDiv.className = "externalRecordsDiv";
    
    var hgvsLinkString = "";
    if(variantData.transcriptAlleles != null && variantData.transcriptAlleles.length > 0){
        var transcriptAlleles = variantData.transcriptAlleles;
        var n = transcriptAlleles.length;
        for (var i = 0; i < n; i++){
            var obj = transcriptAlleles[i];
            if(obj.hgvs != null && obj.hgvs.length > 0){

                var strValue = '';
                if(obj.proteinEffect != null && obj.proteinEffect.hgvs != null){
                    var strValue = obj.proteinEffect.hgvs;
                    let indx = strValue.lastIndexOf(':');
                    strValue = strValue.substring((indx+1),strValue.length);
                }

                var hgvsLinks = obj.hgvs;
                var j = hgvsLinks.length;
                for (var k = 0; k < j; k++){
                    var hgvsLink =hgvsLinks[k];
                    if(strValue != ''){
                        hgvsLink = hgvsLink+" ("+strValue+")";
                    }
                    hgvsLinkString = hgvsLinkString+"<p>"+hgvsLink+"</p> ";
                }
            }
        }
    }

    if(hgvsLinkString != ""){
        externalRecordsDiv.innerHTML = hgvsLinkString;
    }
    return externalRecordsDiv;
}

function createSecondSectionOfExternalRecords(variantCAID){
    let externalRecords2 = document.getElementById("externalRecords_2");

    let urlDataForLinks = {
        'Google':{'link':'https://www.google.com/search?q=Variant '+variantCAID},
        'ClinGen LDH':{'link':'https://ldh.genome.network/ldh/Variant/id/'+variantCAID},
        'CFDE LDH':{'link':'https://ldh.genome.network/cfde/ldh/Variant/id/'+variantCAID}
    }        
    createPCExternalLinks(urlDataForLinks, externalRecords2, "alleleGeneLinksCalc");
}

async function createRelatedGeneTable(externalRecordsNameAndLink, communityStandardTitle){
    let associatedGeensMap = null;
    
    if(externalRecordsNameAndLink.MyVariantInfo_hg38 != null){
        let erMyVariantInfoHG38 = externalRecordsNameAndLink.MyVariantInfo_hg38;
        if(erMyVariantInfoHG38.id != null){
            let myVariantInfoHG38Data = await getMyVariantInfoHG38Data(encodeURIComponent(erMyVariantInfoHG38.id));        
            if (myVariantInfoHG38Data != null){
                associatedGeensMap = processHG38ExternalRecordsResponse(myVariantInfoHG38Data);
            }
        }        
    }else{
        let cstGeneName = getGeneNameFromAlleleRegResponse(communityStandardTitle);
        if(cstGeneName != null && cstGeneName != ''){
            associatedGeensMap = {};
            associatedGeensMap[cstGeneName] = {};
        }
    }
    
    if(associatedGeensMap == null){
        return;
    }

    var geneList = Object.keys(associatedGeensMap)

    let engineDataForGenes = await getEngineDataForGeneList(geneList);

    let td = null;
    let tr = null;
    let radioBtn = null;
    let aLink = null;
    let relatedGenesTable = document.getElementById("relatedGenesTable");
    if(relatedGenesTable == null){
        return;
    }

    let imageSrc = null;
    let title = null;
    let message = null;
    let engineId = null

    var n = geneList.length;
    for (var i = 0; i < n; i++){
        let geneName = geneList[i];

        let hgncLink = "https://www.genenames.org/tools/search/#!/?query="+geneName;
        let ncbiLink = "https://www.ncbi.nlm.nih.gov/search/all/?term="+geneName;
        let hgncAndNCBIgeneIdentifiers = await getHGNCandNCBIgeneIdentifiers(geneName);
    
        if(hgncAndNCBIgeneIdentifiers != null && Array.isArray(hgncAndNCBIgeneIdentifiers)){
            if(hgncAndNCBIgeneIdentifiers[0] != ''){
                hgncLink = creteHgncLink(hgncAndNCBIgeneIdentifiers[0]);
            }
            if(hgncAndNCBIgeneIdentifiers[1] != ''){
                ncbiLink = 'http://www.ncbi.nlm.nih.gov/gene/'+hgncAndNCBIgeneIdentifiers[1];
            }        
        }

        tr = document.createElement('tr');
            td = document.createElement('td');
                let imgElem = document.createElement('img'); 
                if(engineDataForGenes != null && engineDataForGenes[geneName] != null && engineDataForGenes[geneName].enabled){
                    imageSrc ="../images/green_check_button.png";
                    title = "This gene has an available specification (rule set)!"
                    message = "Specification available";
                    engineId = engineDataForGenes[geneName].engineId;
                }else if(engineDataForGenes != null && engineDataForGenes[geneName] != null && !engineDataForGenes[geneName].enabled){
                    imageSrc ="../images/yellow_warning_button.png";
                    title = "The specification for this gene is available but not yet validated for use!"
                    message = "Specification is available but not yet validated";
                    engineId = engineDataForGenes[geneName].engineId;
                }else{
                    imageSrc ="../images/warning_button.png";
                    title = "This gene has no currently available specification (rule set)!"
                    message = "No specification available";
                    engineId = "GN001";
                }
                imgElem.src= imageSrc;
                imgElem.title = title;
                imgElem.style.width='15px';
                imgElem.style.height='15px';
                imgElem.style.marginLeft='5px';
            td.appendChild(imgElem);
        tr.appendChild(td);
            td = document.createElement('td');
            td.className = "calculatorBasicTableLeftTD";
                radioBtn = document.createElement('input');
                radioBtn.type = "radio";
                radioBtn.name = "mainGeneSelectRadioGroup";
                if(i == 0){
                    radioBtn.checked = true;
                }
                radioBtn.value = geneName;
            td.appendChild(radioBtn);
                aLink = document.createElement('a');
                aLink.className = "alleleGeneLinksCalc";
                aLink.href = hgncLink;

                aLink.target = "_blank"
                    let img = document.createElement('img');
                    img.src = "../images/got_link_icon.png";
                aLink.appendChild(img);
                    let  p = document.createElement('p');
                    p.style.margin = "0px";
                    p.innerHTML = geneName;
                aLink.appendChild(p);
            td.appendChild(aLink);
        tr.appendChild(td);
            td = document.createElement('td');
                let messageSpan = document.createElement("span");
                messageSpan.innerText = message;
            td.appendChild(messageSpan); 
        tr.appendChild(td);    
            td = document.createElement('td');
                let engineInfoBtn = document.createElement("button");
                engineInfoBtn.className = "calcMainMenuBtns"; 
                engineInfoBtn.innerText = "Specification Info";
                engineInfoBtn.value = engineId;
                engineInfoBtn.addEventListener("click", function(){ displayEngineInfoFromBtn(this) });          
            td.appendChild(engineInfoBtn);
        tr.appendChild(td);
            td = document.createElement('td');
        
            let externalGeneSourcesNameAndLinks = {
                'UCSC':{'link':'http://genome.ucsc.edu/cgi-bin/hgGene?org=human&db=hg38&hgg_gene='+geneName},
                'HGNC':{'link':hgncLink},
                'NCBI':{'link':ncbiLink},
                'gnomAD':{'link':'http://gnomad.broadinstitute.org/awesome?query='+geneName},
                'GTR':{'link':'https://www.ncbi.nlm.nih.gov/gtr/all/genes/?term='+geneName},
                'OMIM':{'link':'https://www.omim.org/search?index=entry&start=1&limit=10&sort=score+desc%2C+prefix_sort+desc&search='+geneName+'+'+geneName.toLowerCase()}
            }        
            createPCExternalLinks(externalGeneSourcesNameAndLinks, td, "alleleGeneLinksCalc");
            
        tr.appendChild(td);
        relatedGenesTable.appendChild(tr);
    }
}

function processHG38ExternalRecordsResponse(apiResponse){
    let geneID = "";
    var associatedGeensMap = {};
    var associatedGeens = apiResponse.snpeff.ann;
    if(Array.isArray(associatedGeens)){
        for(let agIdx in associatedGeens){
            let associatedGeenObj = associatedGeens[agIdx];
            geneID = associatedGeenObj.gene_id+"";

            let effect = associatedGeenObj.effect;
            if(effect == 'intergenic_region'){
                let geneIdArray = geneID.split("-");
                if(geneIdArray[0] != null && geneIdArray[0] != "" && !associatedGeensMap.hasOwnProperty(geneIdArray[0])){
                    associatedGeensMap[geneIdArray[0]] = {};
                }
            }
            if(!associatedGeensMap.hasOwnProperty(geneID) && geneID != ""){
                associatedGeensMap[geneID] = {};
            }
        }
    }else{
        geneID = associatedGeens.gene_id+"";

        let effect = associatedGeens.effect;
        if(effect == 'intergenic_region'){
            let geneIdArray = geneID.split("-");
            if(geneIdArray[0] != null && geneIdArray[0] != ''){
                associatedGeensMap[geneIdArray[0]] = {};
            }
        }
        associatedGeensMap[geneID] = {};
    }

    return associatedGeensMap;
  }

function getMyVariantInfoHG38Data(myVariantInfoHG38DataIdentifier){
    if(myVariantInfoHG38DataIdentifier == null || myVariantInfoHG38DataIdentifier == ''){
        return null;
    }
    return new Promise(function (resolve, reject) {
        var xhr = new XMLHttpRequest();	
        var url = "/pcalc/rest/calculator/getMyVariantInfoHG38Data/"+myVariantInfoHG38DataIdentifier;
        xhr.onload = function() {
            if (xhr.status === 200 && xhr.readyState == 4) {		
                if(xhr.responseText != null && xhr.responseText != ""){
                    let dataObj = JSON.parse(xhr.responseText);
                    resolve(dataObj);
                }
                resolve(null);                                   
            } else if (xhr.status !== 200) {
                resolve(null);
            }
        };
        xhr.open('GET', url, true);
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.send();
    });
}

function getHGNCandNCBIgeneIdentifiers(geneNameID){
    if(geneNameID == null || geneNameID == ''){
        return null;
    }
    return new Promise(function (resolve, reject) {
        var xhr = new XMLHttpRequest();	
        var url = "/pcalc/rest/genes/getGeneHGNCandNCBIids/"+geneNameID;
        xhr.onload = function() {
            if (xhr.status === 200 && xhr.readyState == 4) {		
                if(xhr.responseText != null && xhr.responseText != ""){
                    let dataObj = JSON.parse(xhr.responseText);
                    if(dataObj != null && Array.isArray(dataObj)){
                        resolve(new Array(dataObj[0], dataObj[1]));  
                    }
                    resolve(null);
                }else{
                    resolve(null);                   
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

function getEngineDataForGeneList(geneList){ 
    return new Promise(function (resolve, reject) {
            var postData = {
              'genes': geneList,
            } 
          
            postData = JSON.stringify(postData);
          
            var xhr = new XMLHttpRequest();
            let url = "/pcalc/rest/genes/engineDataForGenes";
            xhr.onload = function() {
                if (xhr.status === 200 && xhr.readyState == 4) {
                    if(xhr.responseText != null && xhr.responseText  != ''){
                        var jsonObj = JSON.parse(xhr.responseText);   
                        resolve(jsonObj);                                                                  
                    }else{
                      resolve(null);
                    }
                }else if (xhr.status !== 200) {
                  resolve(null);
                }
            };
            xhr.open("POST", url, true);
            xhr.setRequestHeader('Content-Type', 'application/json');
            xhr.withCredentials = true;
            xhr.send(postData);
    });
}
