async function generateReportData(viID){
    var postData = {
        "interpretationId": viID
    }
    
	return new Promise(function (resolve, reject) {
		postData = JSON.stringify(postData);

		var xhr = new XMLHttpRequest();
		var url = "/pcalc/rest/interpretation/generateReportData";
		xhr.onload = function() {
			if (xhr.status === 200 && xhr.readyState == 4) {
				if(xhr.responseText != null && xhr.responseText  != ''){
					var jsonObj = JSON.parse(xhr.responseText);
					if(jsonObj != null){					
						resolve(jsonObj);
					}						
				}
				resolve(null);				
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


async function generateReportDocument(mainReportDiv, reportData){
    if(mainReportDiv == null || reportData == null || reportData.viDTO == null){
        return;
    }

    let viObj = reportData.viDTO;

    var hgvsLinkString = "";
    let alleleRegResponse = await getAlleleRegistryDataForVariant(variantCAID_report);
    if(alleleRegResponse != null && isObject(alleleRegResponse)){
        //HGVS - Genomic Alleles
        if(alleleRegResponse.genomicAlleles != null && alleleRegResponse.genomicAlleles.length > 0){
            var genomicAlleles = alleleRegResponse.genomicAlleles;
            var n = genomicAlleles.length;
            for (var i = 0; i < n; i++){
                var obj = genomicAlleles[i];
                var hgvsLinks = obj.hgvs;
                var j = hgvsLinks.length;
                for (var k = 0; k < j; k++){
                    var hgvsLink =hgvsLinks[k];
                    hgvsLinkString = hgvsLinkString+hgvsLink+", ";
                }
            }
        }
    }

    //HGVS - Transcript Alleles
    if(alleleRegResponse.transcriptAlleles != null && alleleRegResponse.transcriptAlleles.length > 0){
        var transcriptAlleles = alleleRegResponse.transcriptAlleles;
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
                    hgvsLinkString = hgvsLinkString+hgvsLink+", ";
                }
            }
        }
    }

    let span = null;
    let mainDiv = document.createElement('div');

        let titleDiv = document.createElement('div');
        titleDiv.className = "section";
            span = document.createElement('span');
            span.className ="lvl1 title";
            span.innerHTML = "Report generated dynamically by BCM's <code>ClinGen Pathogenicity Calculator</code>.";
        titleDiv.appendChild(span);

            let createdByDiv = document.createElement('div');
            createdByDiv.className = "section";
                span = document.createElement('span');
                span.className ="lvl2 title";
                span.innerText = "Created by";
            createdByDiv.appendChild(span);
                span = document.createElement('span');
                span.className ="value";
                span.innerText = currentUserName;
            createdByDiv.appendChild(span);   
        titleDiv.appendChild(createdByDiv);  
        
            let lastUpdateDateDiv = document.createElement('div');
            lastUpdateDateDiv.className = "section";
                span = document.createElement('span');
                span.className ="lvl2 title";
                span.innerText = "Last update on";
            lastUpdateDateDiv.appendChild(span);
                span = document.createElement('span');
                span.className ="value";
                span.innerText = getFullTimeAndDate(viObj.lastUpdated);
            lastUpdateDateDiv.appendChild(span);   
        titleDiv.appendChild(lastUpdateDateDiv);   
    mainDiv.appendChild(titleDiv);

        let alleleInfoDiv = document.createElement('div');
        alleleInfoDiv.className = "section";
            span = document.createElement('span');
            span.className ="lvl1 title";
            span.innerHTML = "Allele Information";
        alleleInfoDiv.appendChild(span);  
            let caidDiv = document.createElement('div');
            caidDiv.className = "section";
                span = document.createElement('span');
                span.className ="lvl2 title";
                span.innerText = "CAID";
            caidDiv.appendChild(span);
                span = document.createElement('span');
                span.className ="value";
                span.innerText = variantCAID_report+" (http://reg.genome.network/allele/"+viObj.caid+")";
            caidDiv.appendChild(span);
        alleleInfoDiv.appendChild(caidDiv);
            let hgvsDiv = document.createElement('div');
            hgvsDiv.className = "section";
                span = document.createElement('span');
                span.className ="lvl2 title";
                span.innerText = "HGVS";
            hgvsDiv.appendChild(span);
                span = document.createElement('span');
                span.className ="value";
                span.innerText = hgvsLinkString;
            hgvsDiv.appendChild(span);        
        alleleInfoDiv.appendChild(hgvsDiv);
            let geneDiv = document.createElement('geneDiv');
            geneDiv.className = "section";
                span = document.createElement('span');
                span.className ="lvl2 title";
                span.innerText = "Gene";
            geneDiv.appendChild(span);
                span = document.createElement('span');
                span.className ="value";
                span.innerText = getGeneNameFromAlleleRegResponse(alleleRegResponse.communityStandardTitle[0]);
            geneDiv.appendChild(span);
        alleleInfoDiv.appendChild(geneDiv);
            let conditionDiv = document.createElement('div');
            conditionDiv.className = "section";
                span = document.createElement('span');
                span.className ="lvl2 title";
                span.innerText = "Condition";
            conditionDiv.appendChild(span);
                span = document.createElement('span');
                span.className ="value";
                span.innerText = viObj.condition;
            conditionDiv.appendChild(span);
        alleleInfoDiv.appendChild(conditionDiv);
            let inheritanceDiv = document.createElement('div');
            inheritanceDiv.className = "section";
                span = document.createElement('span');
                span.className ="lvl2 title";
                span.innerText = "Mode of Inheritance";
            inheritanceDiv.appendChild(span);
                span = document.createElement('span');
                span.className ="value";
                span.innerText = viObj.inheritance;
            inheritanceDiv.appendChild(span);
        alleleInfoDiv.appendChild(inheritanceDiv); 
    mainDiv.appendChild(alleleInfoDiv);

    
        let assertionsAndReasoningDiv = document.createElement('div');
        assertionsAndReasoningDiv.className = "section";
            span = document.createElement('span');
            span.className ="lvl1 title";
            span.innerHTML = "Assertions and Reasoning";
        assertionsAndReasoningDiv.appendChild(span);
            let finalClassDiv = document.createElement('div');
            finalClassDiv.className = "section";
                span = document.createElement('span');
                span.className ="lvl2 title";
                span.innerText = "Final Classification";
            finalClassDiv.appendChild(span);
                span = document.createElement('span');
                span.className ="value";
                span.innerText = viObj.calculatedFinalCall.term;
            finalClassDiv.appendChild(span);
        assertionsAndReasoningDiv.appendChild(finalClassDiv); 
            let determndClassDiv = document.createElement('div');
            determndClassDiv.className = "section";
                span = document.createElement('span');
                span.className ="lvl2 title";
                span.innerText = "Expert Classification";
            determndClassDiv.appendChild(span);
                span = document.createElement('span');
                span.className ="value";
                if(viObj.determinedFinalCall != null && viObj.determinedFinalCall.term != null && viObj.determinedFinalCall.term != ''){
                    span.innerText = viObj.determinedFinalCall.term;
                }else{
                    span.innerText = "Not specified!";
                }
            determndClassDiv.appendChild(span);
        assertionsAndReasoningDiv.appendChild(determndClassDiv);
            let rulesetDiv = document.createElement('div');
            rulesetDiv.className = "section";
                span = document.createElement('span');
                span.className ="lvl2 title";
                span.innerText = "Rules Passed";
            rulesetDiv.appendChild(span);
                span = document.createElement('span');
                span.className ="value";
                span.innerText = "Content";
            rulesetDiv.appendChild(span);
        assertionsAndReasoningDiv.appendChild(rulesetDiv); 

        if(viObj.viDescription != null && viObj.viDescription != ""){
                let viDescDiv = document.createElement('div');
                viDescDiv.className = "section";
                    span = document.createElement('span');
                    span.className ="lvl2 title";
                    span.innerText = "Classification comments";
                viDescDiv.appendChild(span);
                    span = document.createElement('span');
                    span.className ="value";
                    span.innerText = viObj.viDescription;
                viDescDiv.appendChild(span);
            assertionsAndReasoningDiv.appendChild(viDescDiv); 
        }
    mainDiv.appendChild(assertionsAndReasoningDiv);


        let vcepDiv = document.createElement('div');
        vcepDiv.className = "section";
            span = document.createElement('span');
            span.className ="lvl1 title";
            span.innerHTML = "VCEP";
        vcepDiv.appendChild(span);
            let vcepSummaryDiv = document.createElement('div');
            vcepSummaryDiv.className = "section";
                span = document.createElement('span');
                span.className ="lvl2 title";
                span.innerText = "Summary";
            vcepSummaryDiv.appendChild(span);
                span = document.createElement('span');
                span.className ="value";
                span.innerText = viObj.cspecEngineDTO.engineSummary;
            vcepSummaryDiv.appendChild(span);
        vcepDiv.appendChild(vcepSummaryDiv);
            let orgLinkDiv = document.createElement('div');
            orgLinkDiv.className = "section";
                span = document.createElement('span');
                span.className ="lvl2 title";
                span.innerText = "Organization";
            orgLinkDiv.appendChild(span);
                span = document.createElement('span');
                span.className ="value";
                span.innerText = viObj.cspecEngineDTO.organizationName;
            orgLinkDiv.appendChild(span);
        vcepDiv.appendChild(orgLinkDiv);

        if(viObj.cspecEngineDTO.genes != null && viObj.cspecEngineDTO.genes.length > 0){
                let vcepGenesDiv = document.createElement('div');
                vcepGenesDiv.className = "section";
                    span = document.createElement('span');
                    span.className ="lvl2 title";
                    span.innerText = "Related Genes";
                vcepGenesDiv.appendChild(span);

                let genes = viObj.cspecEngineDTO.genes;
                for(let i in genes){
                    let g = genes[i];
                    span = document.createElement('span');
                    span.className ="value";
                    span.innerHTML = g.geneName +" (<b>HGNC</b>: "+g.hgncId+", <b>NCBI</b>: "+g.ncbiId+")";
                vcepGenesDiv.appendChild(span);
                }
            vcepDiv.appendChild(vcepGenesDiv);
        }
    mainDiv.appendChild(vcepDiv);

    //Evidence
    if(viObj.evidenceList != null || viObj.evidenceList.length > 0){
        let evidenceSectionDiv = document.createElement('div');
        evidenceSectionDiv.className = "section";
            span = document.createElement('span');
            span.className ="lvl1 title";
            span.innerHTML = "Evidence";
        evidenceSectionDiv.appendChild(span);

        let evidenceDiv = null;
        let evdDescriptionDiv = null;
        let evidenceList = viObj.evidenceList;
        for(let i in evidenceList){
            let e = evidenceList[i];
            evidenceDiv = document.createElement('div');
            evidenceDiv.className = "section";
                let fullName = e.fullLabelForFE;
                let firstLetter = (fullName.charAt(0)).toUpperCase();
                span = document.createElement('span');
                if(firstLetter == 'B'){
                    span.className ="lvl3 benign title";
                }else if(firstLetter == 'P'){
                    span.className ="lvl3 pathogenic title";
                }
                span.innerText = fullName;
            evidenceDiv.appendChild(span);

            if(e.summary != null && e.summary != ''){
                    let evdSummaryDiv = document.createElement('div');
                    evdSummaryDiv.className = "section";
                        span = document.createElement('span');
                        span.className = "lvl4 title";
                        span.innerText = 'Evidence summary';
                    evdSummaryDiv.appendChild(span);  
                        span = document.createElement('span');
                        span.className = "value";
                        span.innerText = e.summary;
                    evdSummaryDiv.appendChild(span);     
                evidenceDiv.appendChild(evdSummaryDiv);
            }

            if(e.evidenceLinks != null && e.evidenceLinks.length > 0){
                evdLinksDiv = document.createElement('div');
                evdLinksDiv.className = "section";
                    span = document.createElement('span');
                    span.className ="lvl3 title";
                    span.innerText = "Resource Links";
                evdLinksDiv.appendChild(span); 

                let links = e.evidenceLinks;
                for(let l in links){
                    let link = links[l];
                    evdLinkDiv = document.createElement('div');
                    evdLinkDiv.className = "section";
                        span = document.createElement('span');
                        span.innerText = link.link+" - "+link.comment;
                    evdLinkDiv.appendChild(span);                
                    evdLinksDiv.appendChild(evdLinkDiv);
                }
                evidenceDiv.appendChild(evdLinksDiv);
            }
            evidenceSectionDiv.appendChild(evidenceDiv);
        }

        mainDiv.appendChild(evidenceSectionDiv); 
    }

    mainReportDiv.appendChild(mainDiv);
}

function saveAsPDF(){
    let mainReportDiv = document.getElementById('mainReportDiv');
    //portrait
	var opt =  {
		margin:       10,
		pagebreak:    {mode: ['css', 'legacy']},
		filename:     variantCAID_report+'_'+variantInterpretationID_report+'.pdf',
		image:        { type: 'jpeg', quality: 0.98 },
		html2canvas:  { scale: 2, logging: true, dpi: 192, letterRendering: true },
		jsPDF:        { unit: 'mm', format: 'a4', orientation: 'landscape' }
	};
	html2pdf(mainReportDiv, opt).then(function(){});
}

function createHeader(){
    var headerDiv = document.createElement("div");
    //mainResultSections headerDiv headerDivResults
    headerDiv.className = "headerDiv";

        var mainLogo = document.createElement("img");
            mainLogo.src = "../../images/clingen_logo.png";
            mainLogo.alt = "ClinGen";
            mainLogo.style.width = "130px";
            mainLogo.style.height = "45px";
        headerDiv.appendChild(mainLogo);
        var mainHeadingH2 = document.createElement("h2");
            mainHeadingH2.style.margin = "0px";
            mainHeadingH2.style.color = "white";
            mainHeadingH2.innerText = "Variant Classification Report"
        headerDiv.appendChild(mainHeadingH2);
    return headerDiv;
}

function createFooter(){
    var footerDiv = document.createElement("div");
    footerDiv.id = 'footerDiv';
        var date = new Date();
        var pDate = document.createElement("p");
        pDate.innerText = date.getDate()+"."+(Number(date.getMonth())+1)+"."+date.getFullYear();
    footerDiv.appendChild(pDate);
    return footerDiv;
}

function printReport(){
    var windowName = 'Print ' +(new Date()).getTime();
    let printContent = document.getElementById('mainReportDiv').outerHTML;

	var WinPrint = window.open('', windowName, 'left=0,top=0,width=800,height=900,toolbar=0,scrollbars=0,status=0');
	WinPrint.document.write('<html><head><title>Pathogenicity Calculator - Variant Classification</title>');
	WinPrint.document.write('<link rel="stylesheet" type="text/css" href="../style/pc_report.css" media="print">');
	WinPrint.document.write('<style></style>');
	WinPrint.document.write('</head><body>');
	WinPrint.document.write(printContent);
	WinPrint.document.write('<script type="text/javascript">'+
							'addEventListener("load", () => { print(); close(); });'+
							'</script>');
	WinPrint.document.write('</body></html>');
	WinPrint.document.close();
	WinPrint.focus();
}
