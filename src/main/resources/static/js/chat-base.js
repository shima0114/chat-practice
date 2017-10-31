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
    var $channelInfo = $("#channel-info-" + $this.parent("div.input-group").data("channel-id"));
    $("#channel-id").val($this.parent("div.input-group").data("channel-id"));
    connect();
    var channelName = $channelInfo.data("channel-name");
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
    var hrTag = $("<hr />");
    $.ajax({
        type: "GET",
        url: '/history/load',
        data: {
              channelId:$("#channel-id").val()
        },
        success: function(result){
            var msgArea = $("#messages");
            var senders = result.senders;
            var histories = result.history;
            var prevDay = "";
            $.each(histories, function(i, history) {
                var sender = senders[history.userId];
                var type = history.type;
                var sendTime = createSendTime(history.sendTime);
                if (prevDay !== history.sendTime.dayOfMonth) {
                    var sepTag = $("<div></div>").addClass("lines-on-sides")
                                        .addClass("clear-float").text(createSendYmd(history.sendTime));
                    msgArea.append(sepTag);
                    prevDay = history.sendTime.dayOfMonth;
                }
                if (type == "message") {
                    msgArea.append(messageTag(history.message, sender.name + sendTime, history.userId, sender.image));
                } else if (type == "file-link") {
                    var imgObj = {};
                    imgObj.saveFileName = history.message;
                    imgObj.fileName = history.message.substr(15);
                    imgObj.userName = sender.name + sendTime;
                    imgObj.userId = history.userId;
                    msgArea.append(fileLinkTag(imgObj));
                }
            });
            imageScroll();
            df.resolve();
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
                var trTag = $("<tr></tr>");
                var userImage = $("<img></img>").attr("src", e.image);
                var imageTag = $("<td></td>").append(userImage).addClass("icon-circle");
                var nameTag = $("<td></td>").text(e.name);
                var loginTag = $("<td></td>").text(e.lastLogin);
                $("#user-table-body").append(trTag.append(imageTag).append(nameTag).append(loginTag));
            });
            var attachments = result.attachments;
            $.each(attachments, function(i, e) {
                var divTag = $("<div></div>").addClass("col-md-4");
                var fileTag = $("<a></a>").attr("href", "/file/download/" + e.id)
                                    .attr("title", e.originalFileName)
                                    .attr("download", e.originalFileName)
                                    .addClass("thumbnail");
                var imgSrc = convertFileNameToSrc(e.saveFileName);
                var thumbTag = $("<img></img>").attr("src", imgSrc);
                $("#attached-tab div.row").append(divTag.append(fileTag.append(thumbTag)));
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
    $(".none-tag").remove();
    $.ajax({
        type: "GET",
        url: '/channel/listUpdate',
        data: {userId:$("#user-id").val()},
        success: function(result){
            var joiningList = result.joiningList;
            var invitationList = result.invitationList;
            var abstentionList = result.abstentionList;
            // 参加済リスト更新
            joiningList.forEach(function(channels){
                var channel = channels.channel;
                var channelTag = $("#reg-channel").find("#channel-" + channel.id);
                if (!!channelTag.length) {
                    channelTag.find("span.badge").text(channels.unread);
                } else {
                    var $clone = $("#join-list-base").clone().removeAttr("id");
                    $clone.attr("data-channel-id", channel.id);
                    var scope = channel.channelScope;
                    //$clone.find("a.withdrawal-btn").attr("data-channel-scope", scope);
                    $clone.find("span.badge").text(channels.unread);
                    $clone.find("a.open-channel")
                    //.attr("data-channel-name",channel.channelName)
                                    .attr("id", "channel-" + channel.id);
                    if (scope == "all") {
                        $clone.find("a.open-channel").addClass("fa fa-users");
                    } else if (scope == "user") {
                        $clone.find("a.open-channel").addClass("fa fa-user-secret");
                    } else if (scope == "group") {
                        $clone.find("a.open-channel").addClass("fa fa-user-plus");
                    }
                    $clone.find("label.join-channel-name").text(channel.channelName);
                    $("#reg-channel div.panel-body").append($clone);
                    if (channel.createUserId !== $("#user-id").val()) {
                        $clone.find(".conf-link").remove();
                    }
                    $clone.find("i").tooltip();
                    $clone.find("#channel-info-base").attr("id", "channel-info-" + channel.id)
                        .attr("data-channel-scope", scope)
                        .attr("data-channel-name", channel.channelName);
                    $clone.show();
                }
            });
            if (!!!$("#reg-channel div.input-group:not(#join-list-base)").length) {
                var noneTag = $("<label></label>").addClass("none-tag").text("登録済のチャンネルがありません。");
                $("#reg-channel div.panel-body").prepend(noneTag);
            }
            // 招待済リスト更新
            invitationList.forEach(function(channels){
                var channel = channels.channel;
                var channelTag = $("#inv-channel").find("#channel-" + channel.id);
                if (!!!channelTag.length) {
                    var addTag = $("#invitation-base a").clone();
                    addTag.attr("id", "channel-" + channel.id)
                        .attr("data-channel-id", channel.id)
                        .attr("data-channel-name", channel.channelName)
                        .attr("data-channel-scope", channel.channelScope);
                    addTag.find("label").text(channel.channelName);
                    if (!!!$("#reg-channel a#channel-" + channel.id).length) {
                        $("#inv-channel div.list-group").append(addTag);
                    }
                    //$("#inv-channel div.list-group").append(addTag);
                }
            });
            if (!!!$("#inv-channel div.list-group a:not(#invitation-base > a)").length) {
                var noneTag = $("<label></label>").addClass("none-tag").text("招待されたチャンネルがありません。");
                $("#inv-channel div.list-group").prepend(noneTag);
            }

            // 未参加リスト更新
            abstentionList.forEach(function(channels){
                var channel = channels.channel;
                var channelTag = $("#other-channel").find("#channel-" + channel.id);
                if (!!!channelTag.length) {
                    var addTag = $("#abstention-base a").clone();
                    addTag.attr("id", "channel-" + channel.id)
                        .attr("data-channel-id", channel.id)
                        .attr("data-channel-name", channel.channelName)
                        .attr("data-channel-scope", channel.channelScope);
                    addTag.find("label").text(channel.channelName);
                    $("#other-channel div.list-group").append(addTag);
                }
            });
            if (!!!$("#other-channel div.list-group a:not(#abstention-base > a)").length) {
                var noneTag = $("<label></label>").addClass("none-tag").text("登録可能なチャンネルがありません。");
                $("#other-channel div.list-group").prepend(noneTag);
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