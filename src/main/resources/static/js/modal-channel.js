
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
            // 登録済へ移動
            var $this = $("#channel-" + channelId);
            var $clone = $("#join-list-base").clone().removeAttr("id");
            $clone.find("a.withdrawal-btn").attr("data-channel-id", $this.data("channel-id"))
                            .attr("data-channel-scope", $this.data("channel-scope"));
            $clone.find("a.open-channel").attr("data-channel-id",$this.data("channel-id"))
                            .attr("data-channel-name",$this.data("channel-name"))
                            .attr("id", "channel-" + $this.data("channel-id"));
            $clone.find("label.join-channel-name").text($this.data("channel-name"));
            $("#reg-grp div.panel-body").append($clone);
            $clone.find("i").tooltip();
            $clone.show();
            $this.remove();
            listUpdate();
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
        }
    });
}

/* withdrawal Channel */
$("#modal-withdrawal-channel").on("show.bs.modal", function (event) {
    var $target = $(event.relatedTarget);
    $("#modal-channel-id").val($target.data("channel-id"));
    var scope = $target.data("channel-scope");
    $("#modal-channel-scope").val(scope);
    if (!!scope && scope !== "all") {
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
            var $this = $("#channel-" + $("#modal-channel-id").val());
            if ($("#modal-channel-scope").val() == 'all') {
                // 未登録へ移動
                var $clone = $this.clone();
                //$this.remove();
                // クラスを変更
                $clone.addClass("btn-xs").removeClass("open-channel").removeClass("form-control").addClass("list-group-item");
                // modal表示を変更
                $clone.attr("data-target", "#modal-join-channel");
                $clone.attr("data-toggle", "modal");
                // バッジ削除
                $clone.find("span.badge").remove();
                // onclick削除
                $clone.removeAttr("onclick");
                $("#other-grp div.list-group").append($clone);
            }
            $this.parent("div.input-group").remove();
            listUpdate();
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            alert(textStatus);
        }
    });
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
    if (valCheck()) {
        $.ajax({
            type: "GET",
            url: '/channel/make',
            data: {
                    channelName:$("#make-channel-name").val(),
                    userId:$("#user-id").val(),
                    channelScope: channelScope,
                    scopeTarget: scopeTarget
            },
            success: function(result){
                if (result["result"] === "success") {
                    //listUpdate();
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

function valCheck() {
    if (!!!$("#make-channel-name").val()) {
        alert("チャンネル名を入力してください。");
        return false;
    }
    if ($("#make-channel-name").val().length > 18) {
    }
    return true;
}