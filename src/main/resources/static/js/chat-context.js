/**
 * WebSocketのエンドポイントと配信先のプレフィックスを初期化します。
 * @param endpoint WebSocketのエンドポイント
 * @param subscribePath メッセージの配信先のプレフィックス
 */
var ChatContext = function(endpoint, subscribePath) {
    this.endpoint = endpoint;
    this.subscribePath = subscribePath;
};

ChatContext.prototype = {
    /**
     * チャンネルIDを指定して接続します。
     * @param channelId チャンネルID
     */
    connect : function(channelId) {
        //WebSocketの接続をSTOMPでラップします。stomp-websocket の JS が必要です。
        this.stompClient = Stomp.over(new WebSocket(this.endpoint));
        //接続後に部屋IDをパラメーターとしてコールバック関数を呼び出すように設定
        this.stompClient.connect({}, this.onConnected.bind(this, channelId));
    },
    /**
     * 接続後に指定した部屋の配信メッセージの購読を開始します。
     * @param channelId メッセージを購読するチャンネルID
     */
    onConnected : function(channelId) {
        this.channelId = channelId;
        //「/topic/channel/1」のような宛先のメッセージの購読を開始します。メッセージを受信した際のコールバック関数も設定
        this.stompClient.subscribe(this.subscribePath + channelId, this.onAcceptMessage.bind(this));
        // 接続中ルーム名を表示
        var channelName = $("#channel_name").val();
        $("#status_msg").html(channelName + "に接続中");
        $("#status").val("connect");
        $("#form").show();
        $("#leave_form").show();
        // 入室メッセージを送信
        var msg = {};
        msg.message = $("#user_name").val() + "が入室しました。";
        msg.user_name = "system";
        msg.proc = "channel_in";
        this.sendMessage(msg);
    },
    /**
     * 受信したメッセージを表示します。
     */
    onAcceptMessage : function(message) {
        var retMsg = JSON.parse(message.body);
        if (retMsg.user_name === "system") {
            var msgBodyTag = $("<div></div>").text(retMsg.message);
            $("#messages").append(msgBodyTag);
            if (retMsg.proc == "force_close") {
                var closeWait = 5;
                var closeMsgTag = $("<div></div>").attr("id", "countdown_msg").text("5秒後に自動で退室します。");
                $("#messages").append(closeMsgTag);
                $("#status").val("close");
                var countDown = function() {
                    closeWait--;
                    $("#countdown_msg").text(closeWait + "秒後に自動で退室します。");
                }
                setInterval(countDown, 1000);
                var leavePage = function() {
                    location.href = "/enter";
                }
                setTimeout(leavePage, 5000);
                chatContext.close();
            }
        } else {
            var msgBodyTag = $("<div></div>").text(retMsg.message);
            var msgSenderTag = $("<div></div>").text(retMsg.user_name);
            var msgTag = $("<p></p>").addClass("clear_float");
            if (retMsg.user_name === $("#user_name").val()) {
                msgBodyTag.addClass("send");
                msgSenderTag.addClass("send_name clear_float");
            } else {
                msgBodyTag.addClass("receive");
                msgSenderTag.addClass("receive_name clear_float");
            }
            $("#messages").append(msgTag.append(msgBodyTag).append(msgSenderTag));
        }
        msgScroll();
    },
    /**
     * 参加している部屋に対して、接続済のクライアントを通じて、メッセージを送信します。
     * @param message メッセージ
     */
    sendMessage : function(message) {
        if (!this.stompClient) {
            alert("接続されていません。");
            return false;
        }
        this.stompClient.send(this.subscribePath + this.channelId, {}, JSON.stringify(message));
    },
    /**
     * 接続を切断します。
     */
    close : function() {
        if (this.stompClient) {
            this.stompClient.disconnect();
            this.stompClient = null;
            this.channelId = null;
        }
        $("#status_msg").html("未接続");
        $("#status").val("close");
        $("#form").hide();
    }
};

function msgScroll() {
    var psconsole = $('#messages');
    if (!!psconsole) {
        psconsole.scrollTop(
                psconsole[0].scrollHeight - psconsole.height()
            );
    }
}