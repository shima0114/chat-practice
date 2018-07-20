
//-- ws socket connection --//
    var endpoint = 'ws://' + location.host + '/endpoint';
    var webSocket = null;

    var connect = function() {
        webSocket = new WebSocket(endpoint + '?' + encodeURIComponent($('#channel-id').val()));
        webSocket.onopen = function() {
            // channel last login update
            $.get('/channel/lastlogin', {channelId:$("#channel-id").val(), userId:$("#user-id").val()});
            // ボタンを有効化
            $("#msg-field").prop("disabled", false);
            $('#file-upload-form').prop("disabled", false);
        };

        webSocket.onclose = function() {
        };

        webSocket.onmessage = function(data) {
            var retMsg = JSON.parse(data.data);
            if (retMsg.user_id =="system") {
                var msgBodyTag = $("<div></div>").text(retMsg.message).addClass("clear-float lines-on-sides");
                $("#messages").append(msgBodyTag);
                if (retMsg.type == "force-close") {
                    var closeWait = 5;
                    var closeMsgTag = $("<div></div>").attr("id", "countdown-msg").text("5秒後に自動で退室します。");
                    $("#messages").append(closeMsgTag);
                    var countDown = function() {
                        closeWait--;
                        $("#countdown-msg").text(closeWait + "秒後に自動で退室します。");
                    }
                    setInterval(countDown, 1000);
                    var leavePage = function() {
                        location.href = "/enter";
                    }
                    setTimeout(leavePage, 5000);
                    webSocket.close();
                }
            } else {
                if (retMsg.type == "file-link") {
                    var fileInfo = retMsg.attr;
                    fileInfo.userName = fileInfo.userName + createSendTime();
                    $("#messages").append(fileLinkTag(fileInfo));
                    var linkParent = $("<div></div>");
                    var linkTag = $("<a></a>").attr("href", "/file/download/" + fileInfo.fileId)
                                        .text(fileInfo.fileName)
                                        .attr("download", fileInfo.fileName);
                    $("#attached-tab").append(linkParent.append(linkTag));
                    imageScroll();
                } else if (retMsg.type == "message") {
                    $("#messages").append(messageTag(retMsg.message, retMsg.user_name + createSendTime(), retMsg.user_id, retMsg.user_image));
                }
            }
            msgScroll();
        };

        webSocket.onerror = function() {
            alert('エラーが発生しました。');
        };
    };

    // メッセージ送信共通
    var sendMessage = function(message, userId, userName, type, attr) {
        if (!!!message) {
            return false;
        }
        var msg = {};
        msg.message = message;
        msg.user_id = userId;
        msg.user_name = userName;
        //msg.image = $("#user_image").attr("src");
        msg.type = type;
        if (!!attr) {
            msg.attr = attr;
        }
        webSocket.send(JSON.stringify(msg));
    }

    var conClose = function() {
        if (!!webSocket) {
            // channel last login update
            $.get('/channel/lastlogin', {channelId:$("#channel-id").val(), userId:$("#user-id").val()});
            $("#channel-" + $("#channel-id").val()).removeClass("active");
            sendLeaveMessage();
            webSocket.close();
            webSocket = null;
        }
        $("#messages").empty();
        $("#msg-field").prop("disabled", true);
        $('#file-upload-form').prop("disabled", true);
        $("#channel-information").hide();
        $("#user-table-body").empty();
        $("#attached-tab div.row").empty()
    }

    // enter押下
    $(document).keypress(function(e) {
        if (e.which === 13) {
            sendNormalMessage();
            return false;
        }
    });

    // User message
    function sendNormalMessage() {
        if (!webSocket) {
            alert('未接続です。');
            return;
        }
        sendMessage($("#message").val(), $("#user-id").val(), $("#user-name").val(), "message");
        $("#message").val("");
    }

    function sendWithdrawalMessage() {
        sendMessage($("#user-name").val() + "が脱退しました。", "system", "system", "system-message");
    }

    function sendLeaveMessage() {
        sendMessage($("#user-name").val() + "が退席しました。", "system", "system", "system-message");
    }

    // System message
    function sendConnectMessage() {
        sendMessage($("#user-name").val() + "が入室しました。", "system", "system", "system-message")
    }

    function sendCloseMessage() {
        sendMessage("チャンネルが作成者により閉鎖されました。", "system", "system", "force-close");
    }

    function sendFileUploadMessage(uploadMsg) {
        var attr = {};
        attr.fileName = uploadMsg.fileName;
        attr.fileId = uploadMsg.fileId;
        attr.saveFileName = uploadMsg.saveFileName;
        attr.userId = $("#user-id").val();
        attr.userName = $("#user-name").val();
        sendMessage(uploadMsg.saveFileName, $("#user-id").val(), $("#user-name").val(), "file-link", attr);
    }