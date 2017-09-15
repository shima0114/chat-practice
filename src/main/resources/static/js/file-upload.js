
// file upload
function doUploadFile(evt) {
    evt.preventDefault();
    if (!!!webSocket) {
        alert("チャンネルに接続されていません");
        return false;
    }
    var files = evt.originalEvent.dataTransfer.files;
    var upFile = files[0];
    var form = $("#file-upload-form")[0];
    var formData = new FormData(form);
    formData.append("file", upFile);
    formData.append("channelId", $("#channel-id").val());
    formData.append("userId", $("#user-id").val());

    $.ajax({
        type : "POST",
        dataType : "text",
        data : formData,
        enctype: 'multipart/form-data',
        url  : "/file/upload",
        processData : false,
        contentType : false,
    success: function(jsonMsg){
        var msg = JSON.parse(jsonMsg);
        if (msg.result == "failure") {
            var msg = "ERROR    :: " + msg.message;
            if (!!msg.fileName) {
                msg = msg + "<br>" + "FileName :: " + msg.fileName;
            }
            alert(msg);
        } else {
            sendFileUploadMessage(msg);
        }
    },
    error: function(XMLHttpRequest, textStatus, errorThrown){
        alert("アップロードが失敗しました。");
    }});
}

function addImageTagAndSendMessage(msg) {

}