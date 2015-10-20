var bkn;
var getBkn = function() {
		for (var e = 5381,
		t = skey, r = 0, a = t.length; a > r; ++r) e += (e << 5) + t.charAt(r).charCodeAt();
		bkn = 2147483647 & e;
		bkn = new Number(bkn);
	};