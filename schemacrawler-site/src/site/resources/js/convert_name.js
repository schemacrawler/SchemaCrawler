$( document ).ready(function() {
  $("*[name]").map(function(i, v){
    $(v).attr("id", $(v).attr("name"));
  });
});
