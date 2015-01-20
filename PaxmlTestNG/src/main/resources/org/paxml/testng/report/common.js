function fetchJson(name, factoryIndex, handler){
	var src=getJsonSrc(name, factoryIndex);
	$.getJSON(getJsonSrc(name, factoryIndex), function(json) {
		handler(json);
	}).error(function(xhr){
		if(xhr.status!=0){
			alert('Cannot get json from: '+src+"\r\nStatus:\r\n"+xhr.status);
		}
	});
}

function getJsonSrc(name, factoryIndex){
	if(!factoryIndex){
		factoryIndex=0;
	}
	
	return '../results/'+factoryIndex+'/'+name+'.js?'+new Date().getTime();
}


function getUrlQueryMap(url){

    var vars = [], hash;
    var hashes = url.substring(url.indexOf('?') + 1).split('&');
    for(var i = 0; i < hashes.length; i++)
    {
        hash = hashes[i].split('=');
        vars.push(hash[0]);
        vars[hash[0]] = hash[1];
    }
    return vars;
	
}
function toggleView(title){
	var p=$(title).parent();
	if(p.hasClass('closed')){
		p.removeClass('closed');
	}else{
		p.addClass('closed');
	}
}
function renderToggleView(titleText, content, closed){
	return '<div class="toggleView'+(closed?' closed':'')+'"><h4 class="toggleViewTitle" onclick="toggleView(this)">'
		+' <span class="toggleViewTitlePlus">+</span><span class="toggleViewTitleMinus">-</span> <span>'+titleText+'</span></h4><div class="togglable">'+content+'</div></div>';
	
}
function getDuration(diff){
	var str='';
	var s=Math.round(diff / 1000);
	var m=Math.floor(s / 60);
	var h=Math.floor(m / 60);
	if(h>=1){
		str+=h+ ' h ';
		m=m%60;
	}
	if(h>=1 || m>=1){
		str+=m+ ' m ';
		s=s%60;
	}
	str+=s+' s';
	return str;
	
}
function escapeHtml(str){
	return $('<div/>').text(str).html();
}

function inspect(obj, level){
	alert(JSON.stringify(obj, null, true));	
}
