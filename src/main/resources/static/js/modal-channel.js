
/* Join Channel */
$("#modal-join-channel").on("show.bs.modal", function (event) {
    var $target = $(event.relatedTarget);
    $("#modal-channel-id").val($target.data("channel-id"));
});

function joinChannel() {
    var channelId = $("#modal-channel-id").val();
    $.ajax({
        type: "GET",
        url: '/channel/join',
        data: {
              channelId:channelId,
              userId: $("#user-id").val()
        },
        success: function(result){
            $("#channel-" + channelId).remove();
            listUpdate();
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
        }
    });
}

/* withdrawal Channel */
$("#modal-withdrawal-channel").on("show.bs.modal", function (event) {
    var $target = $(event.relatedTarget);
    var channelId = $target.parent("div.input-group").data("channel-id");
    var $infoTag = $("#channel-info-" + channelId);
    $("#modal-channel-id").val(channelId);
    var scope = $infoTag.data("channel-scope");
    $("#modal-channel-scope").val(scope);
    if (!!scope && scope === "user") {
      $("#modal-withdrawal-msg-invitation").show();
      $("#modal-withdrawal-msg-normal").hide();
    } else {
      $("#modal-withdrawal-msg-invitation").hide();
      $("#modal-withdrawal-msg-normal").show();
    }
});

function withdrawalChannel() {
    $.ajax({
        type: "GET",
        url: '/channel/withdrawal',
        data: {
              channelId:$("#modal-channel-id").val(),
              userId: $("#user-id").val()
        },
        success: function(result){
            $("#channel-" + $("#modal-channel-id").val()).parent("div.input-group").remove();
            listUpdate();
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            alert(textStatus);
        }
    });
}

/* Create Channel modal */
$("#modal-create-channel").on("show.bs.modal", function (event) {
    var $target = $(event.relatedTarget);
    var $modal = $("#modal-create-channel");
    if ($target.hasClass("conf-link")) {
        // configure channel
        $modal.find(".modal-title").text("チャンネル編集");
        $modal.find("#create-channel-btn").text("更新");
        var channelId = $target.parent("div.input-group").data("channel-id");
        $("#create-channel-id").val(channelId);
        var infoTag = $("#channel-info-" + channelId);
        $modal.find("#make-channel-name").val(infoTag.data("channel-name"));
        toggleScopeTarget(infoTag.data("channel-scope"));
        if (infoTag.data("channel-scope") !== "all") {
            getChannelInfo($modal, channelId);
        }
    } else {
        $modal.find(".modal-title").text("チャンネル作成");
        $modal.find("#create-channel-btn").text("作成");
        $modal.find("#make-channel-name").val("");
        $modal.find("input[name=scope_target]").prop("checked", false);
        toggleScopeTarget("all");
    }
});

function toggleScopeTarget(scope) {
    if (!$("#set-" + scope).hasClass("in")) {
        $("#modal-create-channel .scope-group").removeClass("active").addClass("collapsed");
        $("#modal-create-channel .scope-group[href='#set-" + scope + "']").removeClass("collapsed").addClass("active");
        $("#open-set-" + scope).trigger("click");
    }
}

function getChannelInfo($modal, channelId) {
    var df = $.Deferred();
    // チャンネル情報を取得
    $.ajax({
        type: "GET",
        url: '/channel/scopeTarget',
        data: {
          channelId: channelId
        },
        success: function(result){
            //var joiners = result.joiners;
            $.each(result, function(i, e) {
                var $target = $modal.find("input[name=scope_target][value=" + e.targetId + "]");
                $target.prop("checked", true);
                if (e.disabled === "true") {
                    $target.prop("disabled", true);
                }
            });
            df.resolve();
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            df.resolve();
        }
    });
    return df.promise();
}

/* Create new Channel */
function createChannel() {
    var channelScope = $($("#modal-create-channel").find(".scope-group.active").attr("href"))
                          .find("input[name=channel_scope]").val();
    var scopeTargetArray = $($("#modal-create-channel").find(".scope-group.active").attr("href")).find("input[name=scope_target]:checked");
    var scopeTarget = "";
    var sep = "";
    $.each(scopeTargetArray, function(i, target) {
        scopeTarget = scopeTarget + sep + $(target).val();
        sep = ",";
    });
    if (valCheckChannel()) {
        $.ajax({
            type: "GET",
            url: '/channel/make',
            data: {
                    channelName:$("#make-channel-name").val(),
                    userId:$("#user-id").val(),
                    channelScope: channelScope,
                    scopeTarget: scopeTarget,
                    channelId:$("#create-channel-id").val()
            },
            success: function(result){
                if (result["result"] === "success") {
                    listUpdate();
                } else {
                    $("#create-err-msg").text("同名のチャンネルがすでに存在するため、別の名前を入力してください。");
                    $("#make-channel-name").val("");
                }
            },
            error: function(XMLHttpRequest, textStatus, errorThrown){
            }
        });
    }
}

function valCheckChannel() {
    if (!!!$("#make-channel-name").val()) {
        alert("チャンネル名を入力してください。");
        return false;
    }
    if ($("#make-channel-name").val().length > 18) {
    }
    return true;
}