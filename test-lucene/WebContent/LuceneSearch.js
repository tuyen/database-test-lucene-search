/**
 * this is lucene searcher tool make by anh tuyen pro
 */

var allText;
//JavaScript Document
function readAndWriteTextFileToElement(file){
	var rawFile = new XMLHttpRequest();
	rawFile.open("GET", file, false);
	rawFile.onreadystatechange = function ()
	{
		if(rawFile.readyState === 4)
		{
			if(rawFile.status === 200 || rawFile.status == 0)
			{
				allText = rawFile.responseText;				
			}
		}
	}
	rawFile.send(null);
}

$(document).ready(function(){
	readAndWriteTextFileToElement("server-config.txt");
	$(".form-search").html("<input class = 'frm_search_lucene' name = 'search_lucene' type='text' width='300px'>" +
					"<button type='submit' class = 'btn_search_lucene'>Search</button>");   
});

$(document).ready(function(){
	$(".btn_search_lucene").click(function(){
		$.ajax({
			type : "POST",
			url : allText,
			data : {
				search_lucene : $(".frm_search_lucene").val()
			},
			success : function(data) {
				$("#results_search_lucene").html(data);
			}
		});
	})
});