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
        var alleleFreqSelectorDiv = document.createElement("div");
        alleleFreqSelectorDiv.id = "alleleFreqSelectorDiv";
    chartMainContainer.appendChild(alleleFreqSelectorDiv);  

    //total/male/female div's
        var totalSubcohortDiv = document.createElement("div");
        totalSubcohortDiv.className = "alleleFreqSexSubcohortDiv";
        totalSubcohortDiv.id = "totalDiv";
        totalSubcohortDiv.style.display = 'flex';
    chartMainContainer.appendChild(totalSubcohortDiv); 
        var maleSubcohortDiv = document.createElement("div");
        maleSubcohortDiv.className = "alleleFreqSexSubcohortDiv";
        maleSubcohortDiv.id = "maleDiv";
        maleSubcohortDiv.style.display = 'none';
    chartMainContainer.appendChild(maleSubcohortDiv);   
        var femaleSubcohortDiv = document.createElement("div");
        femaleSubcohortDiv.className = "alleleFreqSexSubcohortDiv";
        femaleSubcohortDiv.id = "femaleDiv";
        femaleSubcohortDiv.style.display = 'none';
    chartMainContainer.appendChild(femaleSubcohortDiv);  
    
    var afChartDiv = null;

    let totalOK = false;
    let maleOK = false;
    let femaleOK = false;

    if(exomeData.gks_va_freq != null && exomeData.gks_va_freq.subcohortFrequency != null){
        var hcExomeDataObj = createChartData(exomeData.gks_va_freq);

        //total
        afChartDiv = document.createElement("div");
        afChartDiv.className = 'alleleFreqChartDiv';
        afChartDiv.id="totalExomeChart";
        totalSubcohortDiv.appendChild(afChartDiv);
        if(hcExomeDataObj.total != null){
            afChartDiv.style.display = "block";
            if(!totalOK){
                totalSubcohortDiv.style.display = "flex";
                alleleFreqSelectorDiv.appendChild(createChartSelectorRadioInp("totalDiv","Total",true));
                totalOK = true;
            }
            renderAlleleFreqChart(hcExomeDataObj.total,hcExomeDataObj.labels,"totalExomeChart","Exome","Total");
        }
        //male
        afChartDiv = document.createElement("div");
        afChartDiv.className = 'alleleFreqChartDiv';
        afChartDiv.id="maleExomeChart";
        maleSubcohortDiv.appendChild(afChartDiv);
        if(hcExomeDataObj.male != null){
            afChartDiv.style.display = "block";
            if(!maleOK){
                alleleFreqSelectorDiv.appendChild(createChartSelectorRadioInp("maleDiv","Male",false));
                maleOK = true;
            }
            renderAlleleFreqChart(hcExomeDataObj.male,hcExomeDataObj.labels,"maleExomeChart","Exome","Male");
        }
        //female
        afChartDiv = document.createElement("div");
        afChartDiv.className = 'alleleFreqChartDiv';
        afChartDiv.id="femaleExomeChart";
        femaleSubcohortDiv.appendChild(afChartDiv);
        if(hcExomeDataObj.female != null){
            afChartDiv.style.display = "block";
            if(!femaleOK){
                alleleFreqSelectorDiv.appendChild(createChartSelectorRadioInp("femaleDiv","Female",false));
                femaleOK = true;
            }
            renderAlleleFreqChart(hcExomeDataObj.female,hcExomeDataObj.labels,"femaleExomeChart","Exome","Female");
        }
    }
    if(genomeData.gks_va_freq != null && genomeData.gks_va_freq.subcohortFrequency != null){
        var hcGenomeDataObj = createChartData(genomeData.gks_va_freq);

        //total
        afChartDiv = document.createElement("div");
        afChartDiv.className = 'alleleFreqChartDiv';
        afChartDiv.id="totalGenomeChart";
        totalSubcohortDiv.appendChild(afChartDiv);
        if(hcGenomeDataObj.total != null){
            afChartDiv.style.display = "block";
            if(!totalOK){
                totalSubcohortDiv.style.display = "flex";
                alleleFreqSelectorDiv.appendChild(createChartSelectorRadioInp("totalDiv","Total",true));
                totalOK = true;
            }
            renderAlleleFreqChart(hcGenomeDataObj.total,hcGenomeDataObj.labels,"totalGenomeChart","Genome","Total");
        }
        //male
        afChartDiv = document.createElement("div");
        afChartDiv.className = 'alleleFreqChartDiv';
        afChartDiv.id="maleGenomeChart";
        maleSubcohortDiv.appendChild(afChartDiv);
        if(hcGenomeDataObj.male != null){
            afChartDiv.style.display = "block";
            if(!maleOK){
                alleleFreqSelectorDiv.appendChild(createChartSelectorRadioInp("maleDiv","Male",false));
                maleOK = true;
            }
            renderAlleleFreqChart(hcGenomeDataObj.male,hcGenomeDataObj.labels,"maleGenomeChart","Genome","Male");
        }
        //female
        afChartDiv = document.createElement("div");
        afChartDiv.className = 'alleleFreqChartDiv';
        afChartDiv.id="femaleGenomeChart";
        femaleSubcohortDiv.appendChild(afChartDiv);
        if(hcGenomeDataObj.female != null){
            afChartDiv.style.display = "block";
            if(!femaleOK){
                alleleFreqSelectorDiv.appendChild(createChartSelectorRadioInp("femaleDiv","Female",false));
                femaleOK = true;
            }
            renderAlleleFreqChart(hcGenomeDataObj.female,hcGenomeDataObj.labels,"femaleGenomeChart","Genome","Female");
        }
    }
}

function createChartSelectorRadioInp(value, label, checked){
    var radioInputDiv = document.createElement("div");
    radioInputDiv.className = "alleleFreqRadioInputDiv";
        var radioBtn = document.createElement('input');
        radioBtn.type = "radio";
        radioBtn.name = "frequencySelectRadioGroup";
        if(checked){
            radioBtn.checked = true;
        }
        radioBtn.addEventListener("click", function(){ displaySelectedChart(value); });
        radioBtn.value = value;
    radioInputDiv.appendChild(radioBtn);
        var pLabel = document.createElement('p');
        pLabel.style.margin = "0px";
        pLabel.style.padding = "0px";
        pLabel.innerText = label;
    radioInputDiv.appendChild(pLabel);
    return radioInputDiv;
}

function displaySelectedChart(value){
    var chartDivs = ["totalDiv","maleDiv","femaleDiv"];
    let selectedDiv = null;
    for(let i in chartDivs){
        var chartDiv = chartDivs[i];
        var frewChartDiv = document.getElementById(chartDiv);

        if(value == chartDiv){
            selectedDiv = frewChartDiv;
            continue;
        }
        
        if(frewChartDiv.style.display == "flex"){
            frewChartDiv.style.display = "none";
        }
    }

    if(selectedDiv != null){
        selectedDiv.style.display = "flex";
    }
}

function createChartData(alleleFreqData){
    if(!Array.isArray(alleleFreqData.subcohortFrequency) || alleleFreqData.subcohortFrequency.length == 0){
        return null;
    } 

    var alleleSubcohortFreq = alleleFreqData.subcohortFrequency;
    var totalHCDataObj = [];
    var maleHCDataObj = [];
    var femaleHCDataObj = [];
    var afLabels= [];

    //inicate and object for "Total"
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
    totalHCDataObj.push(totalSubcohort);
    afLabels.push("Total");

    var label = "Unknown";
    var ii = 0;
    for(let i in alleleSubcohortFreq){
        var subcohortObj = alleleSubcohortFreq[i];
        var af = 0;

        if(subcohortObj.subcohortFrequency == null){
            continue;
        }

        af = subcohortObj.alleleFrequency;
        determinePositonInTheChart(af, ii, totalHCDataObj);
        if(ii == 0){
            //for total freq
            determinePositonInTheChart(af, ii, maleHCDataObj);
            determinePositonInTheChart(af, ii, femaleHCDataObj);
        }

        if(subcohortObj.subcohortFrequency != null){
            var maleFemaleSubcohort = subcohortObj.subcohortFrequency;
            for(let j in maleFemaleSubcohort){
                let maleFemaleData = maleFemaleSubcohort[j];
                if(maleFemaleData.cohort != null){
                    var cohortId = maleFemaleData.cohort.id;
                    var sexId = cohortId.split(".")[1];
                    if(sexId == "XY"){
                        af = maleFemaleData.alleleFrequency;
                        determinePositonInTheChart(af, ii, maleHCDataObj);
                    }else if(sexId == "XX"){
                        af = maleFemaleData.alleleFrequency;
                        determinePositonInTheChart(af, ii, femaleHCDataObj);
                    }
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
        'total': totalHCDataObj,
        'male': maleHCDataObj,
        'female': femaleHCDataObj,
        'labels': afLabels
    }
    return obj;
}

function determinePositonInTheChart(af, ii, hcDataObj){
    // categories: <0.1%, 0.1%-1%, 1%-5%, and >5%
    if(af === null || af === undefined || af === ""){
        hcDataObj.push([0, ii, 1]) ;
        hcDataObj.push([1, ii, 0]) ;
        hcDataObj.push([2, ii, 0]) ;
        hcDataObj.push([3, ii, 0]) ;
        hcDataObj.push([4, ii, 0]) ;
    }else{
        af = af * 100 ;
        if(af >= 0 && af < 0.1){
            hcDataObj.push([1, ii, 1]) ;
            hcDataObj.push([0, ii, 0]) ;
            hcDataObj.push([2, ii, 0]) ;
            hcDataObj.push([3, ii, 0]) ;
            hcDataObj.push([4, ii, 0]) ;
        }else if(af >= 0.1 && af <= 1){
            hcDataObj.push([2, ii, 1]) ;
            hcDataObj.push([0, ii, 0]) ;
            hcDataObj.push([1, ii, 0]) ;
            hcDataObj.push([3, ii, 0]) ;
            hcDataObj.push([4, ii, 0]) ;
        }else if(af > 1 && af <= 5){
            hcDataObj.push([3, ii, 1]) ;
            hcDataObj.push([0, ii, 0]) ;
            hcDataObj.push([1, ii, 0]) ;
            hcDataObj.push([2, ii, 0]) ;
            hcDataObj.push([4, ii, 0]) ;
        }else{
            hcDataObj.push([4, ii, 1]) ;
            hcDataObj.push([0, ii, 0]) ;
            hcDataObj.push([1, ii, 0]) ;
            hcDataObj.push([2, ii, 0]) ;
            hcDataObj.push([3, ii, 0]) ;
        }
    }
}

function renderAlleleFreqChart(hcDataObj, afLabels, afChartDivid, afType, chromozomeType){
    if(hcDataObj !== null) {
        var color = "black";

        $('#'+afChartDivid).highcharts({
                chart: {
                    type: 'heatmap',
                    marginTop: 70,
                    marginBottom: 70
                },  
                title: {
                    text: afType+'s Allele Frequency - '+chromozomeType
                },    
                xAxis: {
                    categories: ['Absent', '0 - 0.1 %', '0.1 - 1%', '1 - 5 %', '> 5%']
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
