function openAlleleFrequencyChartPopUp(thisDivElem){
    var caid = thisDivElem.getAttribute("data-value").trim();
    if(caid == null || caid == ''){
        return;
    }    
    loadHighChartForAF(caid);
}

async function loadHighChartForAF(caid){
    let exomeData = await getResponseFromLDH("https://ldh.clinicalgenome.org/ldh-dss/cg/ns/gnomad_v4_1/set/exome/id/"+caid+"/data");
    let genomeData = await getResponseFromLDH("https://ldh.clinicalgenome.org/ldh-dss/cg/ns/gnomad_v4_1/set/genome/id/"+caid+"/data");
    
    if(exomeData == null && genomeData == null){
        openNotificationPopUp( "Both exome and genome fields missing in response from ldh.clinicalgenome.org. Cannot render chart!", null);
        return;
    }
    
    makeAFChartWindow();
    var chartMainContainer = document.getElementById("chartMainContainer");
        var maleSubcohortDiv = document.createElement("div");
        maleSubcohortDiv.className = "alleleFreqSexSubcohortDiv";
    chartMainContainer.appendChild(maleSubcohortDiv);   
        var femaleSubcohortDiv = document.createElement("div");
        femaleSubcohortDiv.className = "alleleFreqSexSubcohortDiv";
    chartMainContainer.appendChild(femaleSubcohortDiv);  
    
    var afChartDiv = null;

    if(exomeData.gks_va_freq != null && exomeData.gks_va_freq.subcohortFrequency != null){
        var hcExomeDataObj = createChartData(exomeData.gks_va_freq);
        
        afChartDiv = document.createElement("div");
        afChartDiv.className = 'alleleFreqChartDiv';
        afChartDiv.id="maleExomeChart";
        maleSubcohortDiv.appendChild(afChartDiv);
        if(hcExomeDataObj.male != null){
            afChartDiv.style.display = "block";
            renderAlleleFreqChart(hcExomeDataObj.male,hcExomeDataObj.labels,"maleExomeChart","Exome","XX");
        }

        afChartDiv = document.createElement("div");
        afChartDiv.className = 'alleleFreqChartDiv';
        afChartDiv.id="femaleExomeChart";
        femaleSubcohortDiv.appendChild(afChartDiv);
        if(hcExomeDataObj.female != null){
            afChartDiv.style.display = "block";
            renderAlleleFreqChart(hcExomeDataObj.female,hcExomeDataObj.labels,"femaleExomeChart","Exome","XY");
        }
    }
    if(genomeData.gks_va_freq != null && genomeData.gks_va_freq.subcohortFrequency != null){
        var hcGenomeDataObj = createChartData(genomeData.gks_va_freq);
        
        afChartDiv = document.createElement("div");
        afChartDiv.className = 'alleleFreqChartDiv';
        afChartDiv.id="maleGenomeChart";
        maleSubcohortDiv.appendChild(afChartDiv);
        if(hcGenomeDataObj.male != null){
            afChartDiv.style.display = "block";
            renderAlleleFreqChart(hcGenomeDataObj.male,hcGenomeDataObj.labels,"maleGenomeChart","Genome","XX");
        }

        afChartDiv = document.createElement("div");
        afChartDiv.className = 'alleleFreqChartDiv';
        afChartDiv.id="femaleGenomeChart";
        femaleSubcohortDiv.appendChild(afChartDiv);
        if(hcGenomeDataObj.female != null){
            afChartDiv.style.display = "block";
            renderAlleleFreqChart(hcGenomeDataObj.female,hcGenomeDataObj.labels,"femaleGenomeChart","Genome","XY");
        }
    }
}

function createChartData(alleleFreqData){
    if(!Array.isArray(alleleFreqData.subcohortFrequency) || alleleFreqData.subcohortFrequency.length == 0){
        return null;
    } 

    var alleleSubcohortFreq = alleleFreqData.subcohortFrequency;
    var maleHCDataObj = [];
    var femaleHCDataObj = [];
    var afLabels= [];

    //inicate and object for "Total"
    /*
    var totalSubcohort = {
        "alleleFrequency": alleleFreqData.alleleFrequency,
        "cohort":{
            "id": "ALL",
            "characteristics":[
                {
                    "value": "Total"
                }
            ]
        }
    }
    alleleSubcohortFreq.push(totalSubcohort); */

    var label = "Unknown";
    var ii = 0;
    for(let i in alleleSubcohortFreq){
        var subcohortObj = alleleSubcohortFreq[i];
        var af = 0;

        if(subcohortObj.subcohortFrequency == null){
            continue;
        }

        var maleFemaleSubcohort = subcohortObj.subcohortFrequency;
        for(let j in maleFemaleSubcohort){
            let maleFemaleData = maleFemaleSubcohort[j];
            if(maleFemaleData.cohort != null){
                var cohortId = maleFemaleData.cohort.id;
                var sexId = cohortId.split(".")[1];
                if(sexId == "XX"){
                    af = maleFemaleData.alleleFrequency;
                    determinePositonInTheChart(af, ii, maleHCDataObj);
                }else if(sexId == "XY"){
                    af = maleFemaleData.alleleFrequency;
                    determinePositonInTheChart(af, ii, femaleHCDataObj);
                }
            }
        }
        
        if(subcohortObj.cohort != null){
            if(subcohortObj.cohort.characteristics != null && subcohortObj.cohort.characteristics.length > 0){
                label = (subcohortObj.cohort.characteristics[0]).value;
                afLabels.push(label);
            } 
        }
        ii++;
    }

    var obj = {
        'male': maleHCDataObj,
        'female': femaleHCDataObj,
        'labels': afLabels
    }
    return obj;
}

function determinePositonInTheChart(af, ii, hcDataObj){
    if(af === null || af === undefined || af === ""){
        hcDataObj.push([0, ii, 1]) ;
        hcDataObj.push([1, ii, 0]) ;
        hcDataObj.push([2, ii, 0]) ;
        hcDataObj.push([3, ii, 0]) ;
    }else{
        af = af * 100 ;
        if(af >= 0 && af <= 1){
            hcDataObj.push([1, ii, 1]) ;
            hcDataObj.push([0, ii, 0]) ;
            hcDataObj.push([2, ii, 0]) ;
            hcDataObj.push([3, ii, 0]) ;
        }else if(af > 1 && af <= 5){
            hcDataObj.push([2, ii, 1]) ;
            hcDataObj.push([0, ii, 0]) ;
            hcDataObj.push([1, ii, 0]) ;
            hcDataObj.push([3, ii, 0]) ;
        }else{
            hcDataObj.push([3, ii, 1]) ;
            hcDataObj.push([0, ii, 0]) ;
            hcDataObj.push([1, ii, 0]) ;
            hcDataObj.push([2, ii, 0]) ;
        }
    }
}

function renderAlleleFreqChart(hcDataObj, afLabels, afChartDivid, afType, chromozomeType){
    if(hcDataObj !== null) {
        var color = "black";
        if(chromozomeType == "XX"){
            color = "#24478f";//rgb(36, 71, 143)
        }else if(chromozomeType == "XY"){
            color = "#b30086";//rgb(179, 0, 134)
        }

        $('#'+afChartDivid).highcharts({
                chart: {
                    type: 'heatmap',
                    marginTop: 70,
                    marginBottom: 40
                },  
                title: {
                    text: 'Allele Frequency Of Type '+afType+' For '+chromozomeType
                },    
                xAxis: {
                    categories: ['Absent', '0 - 1 %', '1 - 5 %', '> 5%']
                },    
                yAxis: {
                    categories: afLabels,
                    title: null
                },
                colorAxis: {
                    min: 0,
                    minColor: '#FFFFFF',
                    maxColor: '#F434G2',
                    max: 1
                },    
                legend: {
                    enabled: false,
                    align: 'right',
                    layout: 'vertical',
                    margin: 0,
                    verticalAlign: 'top',
                    y: 25,
                    symbolHeight: 320
                },
                tooltip: {
                    formatter: function() {
                    if (this.point.value === 1) {
                        if (this.point.x !== 0) {
                            return '<b> Allele frequency is in the range of ' + this.series.xAxis.categories[this.point.x] + '</b> for <br><b>' +
                            this.series.yAxis.categories[this.point.y] + '</b>';
                        }else {
                            return '<b>This variant is absent in ' + this.series.yAxis.categories[this.point.y] + '</b>';
                        }
                    }
                    else {
                        return false ;
                    }
                    }
                },
                series: [{
                    name: 'Categories',
                    borderWidth: 0,
                    pointPadding: 10,
                    borderRadius: 3,
                    width: 0,
                    data: hcDataObj,
                    color: color,
                    dataLabels: {
                        enabled: false,
                        color: color,
                        style: {
                            textShadow: 'none'
                        }
                    }
                }]
        });
    }
}

function getResponseFromLDH(url){
    if(url == null || url == ''){
        return null;
    }
    return new Promise(function (resolve, reject) {
        var xhr = new XMLHttpRequest();	
        xhr.onload = function() {
            if (xhr.status === 200 && xhr.readyState == 4) {		
                if(xhr.responseText != null && xhr.responseText != ""){
                    let dataObj = JSON.parse(xhr.responseText);
                    if(dataObj != null){
                        resolve(dataObj);
                    }
                }                   
            }
            resolve(null);
        };
        xhr.open('GET', url, true);
        xhr.send();
    });
}

function makeAFChartWindow(){
    let html = '<div class="chartContainer" id="chartMainContainer">'+
                        /*
                        '<div class="alleleFreqChartDiv af-chart-container" style="display:none;" id="afChartExome"></div>'+
                        '<div class="alleleFreqChartDiv af-chart-container" style="display:none;" id="afChartGenome"></div>'+
                        */
                '</div>';
    openNotificationPopUp(html, "Allele Frequency");
}
