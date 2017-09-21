$(function () {
    $('[data-toggle="tooltip"]').tooltip();
});

/* Logout */
$("#logout-btn").on("click", function() {
    conClose();
    $("#logout-form").submit();
});

$(".list-menu").on("click", function() {
    var $iconTag = $(this).find("span.glyphicon");
    if ($iconTag.hasClass("glyphicon-plus")) {
        $iconTag.removeClass("glyphicon-plus");
        $iconTag.addClass("glyphicon-minus");
    } else {
        $iconTag.removeClass("glyphicon-minus");
        $iconTag.addClass("glyphicon-plus");
    }
});

/* Channel open */
var openChannel = function ($this) {
    if ($this.hasClass("active")) {
        return false;
    }
    conClose();
    $("#channel-id").val($this.data("channel-id"));
    connect();
    var channelName = $this.data("channel-name");
    $("#status-msg").html(channelName + "に接続中");
    $this.addClass("active");
    $this.find("span.badge").text("0");
    $.when(
        loadHistory(),
        loadChannelInfo()
    )
    .done(function() {
        $("#channel-information").show();
        msgScroll();
        sendConnectMessage();
    });
}

// 送信
$('#send-message').click(function() {
    sendNormalMessage();
});

// 離席
$('#leave-channel').on("click", function() {
    conClose();
    $("#status-msg").html("未接続");
});

// 閉鎖
function deleteChannel() {
    $("#delete-channel-btn").attr("disabled", true);
    $.ajax({
        type: "GET",
        url: '/channel/delete',
        data: {channelId:$("#channel-id").val()},
        success: function(result){
            if (result["result"] === "success") {
                // 閉鎖メッセージを送信
                sendCloseMessage();
            } else {
                alert("閉鎖に失敗しました。");
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
        }
    });
};

/* Restore closed Channel */
function restoreChannel() {
      $.ajax({
          type: "GET",
          url: '/channel/restore',
          data: {channelId:$("#channel option:selected").val()},
          success: function(result){
              if (result["result"] === "success") {
                  $("#channel option:selected").attr("data-invalid", "false").change();
                  listUpdate();
              } else {
                  $("#create-err-msg").text("チャンネルの復旧に失敗しました。");
                  $("#make-channel-name").val("");
              }
          },
          error: function(XMLHttpRequest, textStatus, errorThrown){
          }
      });
};

/* message history load */
function loadHistory() {
    var df = $.Deferred();
    $.ajax({
        type: "GET",
        url: '/history/load',
        data: {
              channelId:$("#channel-id").val()
        },
        success: function(result){
            var msgArea = $("#messages");
            var imageArray = result.user_images;
            var histories = result.history;
            $.each(histories, function(i, history) {
                var type = history.type;
                if (type == "message") {
                    msgArea.append(messageTag(history.message, history.userName, history.userId, imageArray[history.userId]));
                } else if (type == "file-link") {
                    var imgObj = {};
                    imgObj.saveFileName = history.message;
                    imgObj.fileName = history.message.substr(15);
                    imgObj.userName = history.userName;
                    imgObj.userId = history.userId;
                    msgArea.append(fileLinkTag(imgObj));
                }
            });
            /*var $lastImg = msgArea.find("img").last();
            if (!!$lastImg) {
                $lastImg.on("load", function() {
                    df.resolve();
                })
            }*/
            imageScroll();
            df.resolve();
            //msgScroll();
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            df.resolve();
        }
    });
    return df.promise();
}

function loadChannelInfo() {
    var df = $.Deferred();
    // チャンネル情報を取得
    $.ajax({
        type: "GET",
        url: '/channel/info',
        data: {
          channelId: $("#channel-id").val()
        },
        success: function(result){
            var joiners = result.joiners;
            $.each(joiners, function(i, e) {
                var joinersTag = $("<div></div>").text(e.name);
                $("#joiners-tab").append(joinersTag);
            });
            var attachments = result.attachments;
            $.each(attachments, function(i, e) {
                var divTag = $("<div></div>");
                var fileTag = $("<a></a>").attr("href", "/file/download/" + e.id).text(e.originalFileName).attr("download", e.originalFileName);
                $("#attached-tab").append(divTag.append(fileTag));
            });
            df.resolve();
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            df.resolve();
        }
    });
    return df.promise();
}

/* Update Channel List */
var timerId = null;
var lastInterval = "0";
var listUpdate = function() {
    $.ajax({
        type: "GET",
        url: '/channel/listupdate',
        data: {userId:$("#user-id").val()},
        success: function(result){
            var joiningList = result.joiningList;
            var invitationList = result.invitationList;
            var abstentionList = result.abstentionList;
            // 参加済リスト更新
            joiningList.forEach(function(channels){
                var channel = channels.channel;
                var channelTag = $("#channel-" + channel.id);
                if (!!channelTag.length) {
                    channelTag.find("span.badge").text(channels.unread);
                }
            });
            // 招待済リスト更新
            invitationList.forEach(function(channels){
                var channel = channels.channel;
                var channelTag = $("#inv-grp").find("#channel-" + channel.id);
                if (!!!channelTag.length) {
                    var addTag = $("#invitation-base a").clone();
                    addTag.attr("id", "channel-" + channel.id)
                        .attr("data-channel-id", channel.id)
                        .attr("data-channel-name", channel.channelName)
                        .attr("data-channel-scope", channel.channelScope);
                    addTag.find("label").text(channel.channelName);
                    $("#inv-grp div.list-group").append(addTag);
                }
            });
            if (!!!$("#inv-grp div.list-group a:not(#invitation-base > a) a").length) {
                // TODO 登録無し表示
            }

            // 未参加リスト更新
            abstentionList.forEach(function(channels){
                var channel = channels.channel;
                var channelTag = $("#other-grp").find("#channel-" + channel.id);
                if (!!!channelTag.length) {
                    var addTag = $("#abstention-base a").clone();
                    addTag.attr("id", "channel-" + channel.id)
                        .attr("data-channel-id", channel.id)
                        .attr("data-channel-name", channel.channelName)
                        .attr("data-channel-scope", channel.channelScope);
                    addTag.find("label").text(channel.channelName);
                    $("#other-grp div.list-group").append(addTag);
                }
            });
            if (!!!$("#other-grp div.list-group a:not(#abstention-base > a)").length) {
                // TODO 登録無し表示
            }

        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
        }
    });
}
var doListUpdate = function() {
    listUpdate();
    timerId = setTimeout(doListUpdate, lastInterval * 1000);
}

$(function() {
    var intervalChange = function() {
        var selInterval = $("#update-interval li a[selected]").data("interval");
        if (!!timerId
                && (lastInterval !== selInterval)) {
            console.log("clear setTimeout");
            clearTimeout(timerId);
        }
        lastInterval = selInterval;
        if (selInterval == "0") {
            return;
        }
        doListUpdate();
    }

    $("#update-interval").on("click", "li a", function(event) {
        // 初期化
        $("#update-interval li a").removeAttr("selected");
        $("#check-icon").remove();
        // selectedを目印につける
        $(this).attr("selected", true);
        // チェックアイコンを付ける
        var checkIconTag = $("<span></span>").addClass("glyphicon glyphicon-ok").attr("aria-hidden", true).attr("id", "check-icon");
        $(this).prepend(checkIconTag);
        intervalChange();
    });
    intervalChange();
});

$('#file-upload-form').on('dragover', function(evt) {
  evt.preventDefault();
  $('#drag-drop-area').addClass('dragover').addClass("dg-area");
});

$('#file-upload-form').on('drop', function(evt) {
    $('#drag-drop-area').removeClass('dragover').removeClass("dg-area");
    doUploadFile(evt)
});