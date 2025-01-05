Date = (function (_date) {
    const _now = Date.now;
      const baseDate = (new Date("%%DATE_GOES_HERE%%")).getTime();
      const loadTime = (new Date()).getTime();
      const offset = loadTime - baseDate;
	  const newDate = function(x) {
	    if(x == null) {
	        const pure = new _date();
	      	pure.setTime(pure.getTime()-offset);
	      	return pure;
	    }
	    return new _date(x);
	  };

	  newDate.prototype = _date.prototype;
	  newDate.now = function(){
	    return _date.now() - offset;
	  }
	  return newDate;
})(Date);
