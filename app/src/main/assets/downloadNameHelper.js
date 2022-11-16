(function () {
	  function download(a) {
	      Android.download(a.href, a.download, a.type);
	  }
	  document.addEventListener("click", function(e){
	      if(e.target.tagName.toLowerCase() == "a" && e.target.download) {
              e.preventDefault();
              e.stopImmediatePropagation();
              download(e.target);
	      }
	  })
	  function clicklisten(e){
	      if (this.download) {
              e.preventDefault();
              e.stopImmediatePropagation();
              download(this);
	      }
	  }
	  const x = document.createElement;
	  document.createElement = function (tagname, options){
	      var res = x.apply(document, [tagname, options]);
	      if(tagname == "a"){
              res.addEventListener("click", clicklisten);
	      }
	      return res;
	  }
})();
