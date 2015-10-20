var guideRandom = (function() {
    return "xxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g,
    function(c) {
        var r = Math.random() * 16 | 0,
        v = c == "x" ? r: (r & 3 | 8);
        return v.toString(16);
    }).toUpperCase();
})();

var token;
var getToken = function(rsp) {
    token = rsp.data.token;
};