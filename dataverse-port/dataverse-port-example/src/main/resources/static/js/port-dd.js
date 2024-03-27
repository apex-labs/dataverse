
function query() {
    let param = {}
    param["query"] = document.getElementById("sql").value
    param["storageInfo"] = document.getElementById("storageInfo").value

    $.ajax({
        url: location.protocol + "//" + location.host + "/api/query",
        method: "POST",
        data: JSON.stringify({
            "sql": param["query"],
            "storeInfos": JSON.parse(param["storageInfo"])
        }),
        dataType: "json",
        contentType: "application/json;charset=utf-8",
        success: function (data, dataTextStatus, jqxhr) {
            document.getElementById("odpcResultContent").innerText = JSON.stringify(data, null, 2)
        },
        error: function (jqxhr, textStatus, error) {
            alert(JSON.stringify(error, null, 2))
        }
    })
}