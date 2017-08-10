function heartBeatCheck() {
    $.ajax({
        type: "GET",
        url: "/heartbeat",
        data: {roomId:$("#room_id").val()},
        success: function(result){
            if (result["result"] === "success") {
                // 閉鎖メッセージを送信
                var msg = {};
                msg.message ="ルームが作成者により閉鎖されました。";
                msg.user_name = "system";
                msg.proc = "force_close";
                chatContext.sendMessage(msg);
            } else {
                alert("削除に失敗しました。");
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
        }
    });
}
