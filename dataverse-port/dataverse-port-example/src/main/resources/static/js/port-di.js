function di() {
    let jdbcUrl = document.getElementById("jdbcUrl").value;
    let jdbcUser = document.getElementById("jdbcUser").value;
    let jdbcPwd = document.getElementById("jdbcPwd").value;
    let query = document.getElementById("query").value;
    let storageName = document.getElementById("storageName").value;

    $.ajax({
        url: location.protocol + "//" + location.host + "/api/di",
        method: "POST",
        data: JSON.stringify({
            "query": query,
            "storeName": storageName,
            "url": jdbcUrl,
            "user": jdbcUser,
            "password": jdbcPwd
        }),
        dataType: "json",
        contentType: "application/json;charset=utf-8",
        success: function (data, dataTextStatus, jqxhr) {
            document.getElementById("diResult").innerText = JSON.stringify(data, null, 2)
        },
        error: function (jqxhr, textStatus, error) {
            alert(JSON.stringify(error, null, 2))
        }
    })
}