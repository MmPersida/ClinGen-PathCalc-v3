function openPredictorChartPopUp(thisDivElem){
    var variantHGVS = thisDivElem.getAttribute("data-value").trim();
    if(variantHGVS == null || variantHGVS == ''){
        return;
    }
    loadPredictorChart(variantHGVS);
}

/*   Methods written by Ronak and Sameer
 * 
 *
 */

// function to help with mapping of reference sequence to chromosome 
function refSeqMapping(inSeq){
    return {
      "NC_000001.10":  "chr1" , "NC_000002.11":  "chr2" , "NC_000003.11":  "chr3" ,
      "NC_000004.11":  "chr4" , "NC_000005.9" :  "chr5" , "NC_000006.11":  "chr6" ,
      "NC_000007.13":  "chr7" , "NC_000008.10":  "chr8" , "NC_000009.11":  "chr9" ,
      "NC_000010.10":  "chr10", "NC_000011.9" :  "chr11", "NC_000012.11":  "chr12",
      "NC_000013.10":  "chr13", "NC_000014.8" :  "chr14", "NC_000015.9" :  "chr15",
      "NC_000016.9" :  "chr16", "NC_000017.10":  "chr17", "NC_000018.9" :  "chr18",
      "NC_000019.9" :  "chr19", "NC_000020.10":  "chr20", "NC_000021.8" :  "chr21",
      "NC_000022.10":  "chr22", "NC_000023.10":  "chrX" , "NC_000024.9" :  "chrY"
    }[inSeq] ;
}


async function loadPredictorChart(inputHGVS) {
    // static url of myvariant
    var linkProtocol = window.location.protocol ;
    var mvidBase = linkProtocol+'//myvariant.info/v1/variant/' ;
  
    // Change the input hgvs to what requried by myvariant
    //var transformedHGVS = refSeqMapping(inputHGVS.split(':')[0])+':'+inputHGVS.split(':')[1] ;
  
    // Generate full URL
    //var mvAPI = mvidBase+transformedHGVS ;

    var mvAPI = mvidBase+inputHGVS;

    let response = await getResponseFromMyVariant(mvAPI);
    if( response.dbnsfp !== undefined ){
        var predictors = ["sift","provean","lrt","metalr","metasvm","ma","mt","hdiv","hvar","fathmm"] ;
        // Function to convert predictors abbreviation to displayable name
        var predictorsFullName =function (inSoftware){
            return {
                 sift: 'SIFT', provean: 'PROVEAN', lrt: 'LRT', metalr: 'MetaLR',
                 metasvm: 'MetaSVM', hdiv: 'PolyPhen-2 HDIV', hvar: 'PolyPhen-2 HVAR', fathmm: 'FATHMM',
                 ma: 'Mutation Assessor', mt: 'MutationTaster' }[inSoftware] 
            } ;
         // This will handle when the JSON response does not have information about the predictions
         jQuery.each(predictors,function(element){
           if(response.dbnsfp[predictors[element]] === undefined){
             response.dbnsfp[predictors[element]] = {} ;
           }
         });
         // Some of the software that might be left out in the previous loop
         if(response.dbnsfp['polyphen2'] === undefined) {
           response.dbnsfp['polyphen2'] = {hdiv: {}, hvar: {}} ;
         }
         if(response.dbnsfp['mutationassessor'] === undefined) {
           response.dbnsfp['mutationassessor'] = {} ;
         }
         if(response.dbnsfp['mutationtaster'] === undefined) {
           response.dbnsfp['mutationtaster'] = {} ;
         }


         // Key prediction hash that stores actual predictions
         var prediction = {
           sift    : response.dbnsfp.sift.pred ,
           provean : response.dbnsfp.provean.pred ,
           fathmm : response.dbnsfp.fathmm.pred,
           lrt : response.dbnsfp.lrt.pred ,
           metalr : response.dbnsfp.metalr.pred ,
           metasvm : response.dbnsfp.metasvm.pred ,
           hdiv : response.dbnsfp.polyphen2.hdiv.pred ,
           hvar : response.dbnsfp.polyphen2.hvar.pred ,
           mt : response.dbnsfp.mutationtaster.pred ,
           ma : response.dbnsfp.mutationassessor.pred  
         } ;// end prediction


         // The scores are cryptic and difficult to understand, however we have mapping
         // This hash stores that mapping
         var score_mapping = {
           sift   : { D: 'Deleterious', T: 'Tolerated' , '.':'Unknown', 'U':'Unknown'},
           fathmm :  { D: 'Deleterious', T: 'Tolerated' ,'.':'Unknown', 'U':'Unknown'},
           ma     : {H: 'High Impact', M: 'Medium Impact', 
             L: 'Low Impact', N: 'Neutral', '.':'Unknown', 'U':'Unknown'},
           hdiv   : {D: 'Probably damaging', P:'Possibly damaging', B: 'Benign', '.':'Unknown', 'U':'Unknown'},
           hvar   : {D: 'Probably damaging', P:'Possibly damaging', B: 'Benign', '.':'Unknown', 'U':'Unknown'},
           mt     : {A: 'Disease Causing Automatic', D: 'Disease Causing', 
             N: 'Polymorphic', P: 'Polymorphic' , '.':'Unknown', 'U':'Unknown'},
           provean :  { D: 'Deleterious', N: 'Neutral' , '.':'Unknown' , 'U':'Unknown'},
           lrt     :  { D: 'Deleterious', N: 'Neutral', T: 'Tolerated' , '.':'Unknown' , 'U':'Unknown'},
           metasvm :  { D: 'Deleterious', N: 'Neutral', T: 'Tolerated' , '.':'Unknown' , 'U':'Unknown'},
           metalr  :  { D: 'Deleterious', N: 'Neutral', T: 'Tolerated' , '.':'Unknown' , 'U':'Unknown'}
         } ;// end score mapping

         var totalcount = {} ;
         var software   = {} ;

         // The bening predictions should be displayed green and pathogenic red.
         var customColors = {
           'Deleterious'               : '#ff7f7f',
           'High Impact'               : '#ff9999',
           'Possibly damaging'         : '#ffb2b2',
           'Disease Causing'           : '#ffcccc',
           'Probably damaging'         : '#f3b877',
           'Disease Causing Automatic' : '#f5c48e',
           'Medium Impact'             : '#f7cfa4',
           'Tolerated'                 : '#66ff66',
           'Low Impact'                : '#ccffcc',
           'Neutral'                   : '#b2ffb2',
           'Benign'                    : '#7fff7f',
           'Polymorphic'               : '#99ff99',
       'Unknown'                   : '#d3d3d3'
         } ;// end customColors

         // All mumble jumble to get information out
         var finalColors = [];
           for(var j = 0 ; j < predictors.length ; j++){
             if( prediction[predictors[j]] !== undefined ){
               if(prediction[predictors[j]].constructor !== Array){
                 if( totalcount[score_mapping[predictors[j]][prediction[predictors[j]]]] == undefined ){ totalcount[score_mapping[predictors[j]][prediction[predictors[j]]]] = 1 ;} 
                 else {totalcount[score_mapping[predictors[j]][prediction[predictors[j]]]] += 1 ;}
                 if(software[score_mapping[predictors[j]][prediction[predictors[j]]]] == undefined ){ software[score_mapping[predictors[j]][prediction[predictors[j]]]] = [predictors[j]] ;}
                 else {software[score_mapping[predictors[j]][prediction[predictors[j]]]].push(predictors[j]) ;}
               }
               if(prediction[predictors[j]].constructor === Array){
                 for(var k = 0 ; k < prediction[predictors[j]].length ; k++ ){
                   if(totalcount[score_mapping[predictors[j]][prediction[predictors[j]][k]]] == undefined ){ totalcount[score_mapping[predictors[j]][prediction[predictors[j]][k]]] = 1 ;} 
                   else {totalcount[score_mapping[predictors[j]][prediction[predictors[j]][k]]] += 1 ;}
                   if(software[score_mapping[predictors[j]][prediction[predictors[j]][k]]]  == undefined ) { software[score_mapping[predictors[j]][prediction[predictors[j]][k]]] = [predictors[j]] ;}
                   else {software[score_mapping[predictors[j]][prediction[predictors[j]][k]]].push(predictors[j]) ;}

                 }// for for k
               } // end if array
             }// end of outer if when not undefined 
           } // end out for loop

         jQuery.each(totalcount,function(name,value){
           finalColors.push(customColors[name]);
         });

         jQuery.each(software,function(name,value){
           jQuery.each(value,function(j){
             software[name][j] =  predictorsFullName(value[j]) ;
           });
         });

         google.charts.load('current', {'packages':['corechart']});
         google.charts.setOnLoadCallback(drawChart);

         function drawChart() {
           var data = new google.visualization.DataTable();
           data.addColumn('string','Predictor');
           data.addColumn('number','Percentage');
           jQuery.each(totalcount, function(name,value){
             data.addRow([name+' ( '+[].concat(jQuery.unique(software[name])).join(", ")+' )',value]) ;
           });

           makePredictorChartWindow();
           var options = {
            colors: finalColors, 
            pieSliceTextStyle: { color: 'black'} , 
            legend: {position: 'right', textStyle: {color: 'black', fontSize: 12}}, 
            pieHole: 0.4, 
            title: 'Predictor Scores' , 
            width:750,
            height:300,
            chartArea: {
              left: 10,
              right: 30
            }
          } ;

           var chart = new google.visualization.PieChart(document.getElementById('piechart'));
           chart.draw(data,options);
        } // end draw chart
    }else{
      var message = "Failed to get the allele data.<br> Either this variant does not exist in myvariant.info or dbNSFP annotations are missing.";
      openNotificationPopUp(message, null);
    }
}

function getResponseFromMyVariant(url){
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

function makePredictorChartWindow(){
    let html = '<div class="chartContainer" id="piechart"></div>'; 
    openNotificationPopUp(html, "Predictor Chart");
}
