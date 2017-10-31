function tglConfirmPasswordBtn(elm) {
    var $this = $(elm);
    $this.find('.btn').toggleClass('active');

    if ($this.find('.btn-primary').size()>0) {
        $this.find('.btn').toggleClass('btn-primary');
    }
    $this.find('.btn').toggleClass('btn-default');

    if ($("#group-toggle-on").hasClass("active")) {
        $("#make-group-pass").attr("disabled", false);
        $("#make-group-pass-check").attr("disabled", false);
    } else {
        $("#make-group-pass").attr("disabled", true);
        $("#make-group-pass-check").attr("disabled", true);
    }
}
/* Join Channel */
$("#modal-join-group").on("show.bs.modal", function (event) {
    var $target = $(event.relatedTarget);
    $("#modal-group-id").val($target.data("group-id"));
    needAuthorized($target.data("group-id"));
});

function joinGroup() {
    if ($("#need-authorized").val() == "true") {
        if (!!!$("#join-group-password").val()) {
            alert("参加用の認証パスワードを入力してください。");
            return false;
        }
    }
    var groupId = $("#modal-group-id").val();
    $.ajax({
        type: "GET",
        url: '/group/join',
        data: {
              groupId: groupId,
              userId: $("#user-id").val(),
              password: $("#join-group-password").val()
        },
        success: function(result){
            // 登録済へ移動
            var $this = $("#group-" + groupId);
            $this.remove();
            updateGroupList();
            $("#modal-join-group").modal("hide");
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
        }
    });
}

/* withdrawal Channel */
$("#modal-withdrawal-group").on("show.bs.modal", function (event) {
    var $target = $(event.relatedTarget);
    var $parent = $target.parent(".input-group");
    $("#modal-group-id").val($parent.data("group-id"));
    var $groupInfo = $("#group-info-" + $parent.data("group-id"));
    var scope = $groupInfo.data("group-scope");
    $("#modal-group-scope").val(scope);
    if (!!scope && scope !== "OPEN") {
      $("#modal-group-msg-invitation").show();
      $("#modal-group-msg-normal").hide();
    } else {
      $("#modal-group-msg-invitation").hide();
      $("#modal-group-msg-normal").show();
    }
});

function withdrawalGroup() {
    $.ajax({
        type: "GET",
        url: '/group/withdrawal',
        data: {
              groupId:$("#modal-group-id").val(),
              userId: $("#user-id").val(),
              scope: $("#modal-group-scope").val()
        },
        success: function(result){
            var $this = $("#group-" + $("#modal-group-id").val());
            $this.remove();
            updateGroupList();
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            alert(textStatus);
        }
    });
}

$("#modal-create-group").on("show.bs.modal", function(event) {
    var $target = $(event.relatedTarget);
    var $modal = $("#modal-create-group");
    if ($target.hasClass("conf-link")) {
        var groupId = $target.parent(".input-group").data("group-id");
        $("#target-group-id").val(groupId);
        var $groupInfo = $("#group-info-" + groupId);
        $modal.find(".modal-title").text("グループ更新");
        $modal.find("#make-group-name").val($groupInfo.data("group-name"));
        if ($groupInfo.data("group-need-auth") == "true") {
            tglConfirmPasswordBtn($("group-toggle-on"));
        } else {
            tglConfirmPasswordBtn($("group-toggle-off"));
        }
        toggleGroupScopeTarget($groupInfo.data("group-scope"));
        if ($groupInfo.data("group-scope") == "CLOSED") {
            // target set
        }
        $("#create-group-btn").text("更新").prop("disabled", false);
    } else {
        $modal.find(".modal-title").text("グループ作成");
        $modal.find("#make-group-name").val("");
        tglConfirmPasswordBtn($("group-toggle-off"));
        toggleGroupScopeTarget("OPEN");
        $("#create-group-btn").text("作成").prop("disabled", false);
    }
});

function toggleGroupScopeTarget(scope) {
    if (!$("#group-scope-" + scope).hasClass("in")) {
        $("#modal-create-group .scope-group").removeClass("active").addClass("collapsed");
        $("#modal-create-group .scope-group[href='#group-scope-" + scope + "']").removeClass("collapsed").addClass("active");
        $("#scope-" + scope).trigger("click");
    }
}

/* Create new Group */
function createGroup() {
    var groupScope = $($("#modal-create-group").find(".group-scope.active").attr("href"))
                          .find("input[name=group_scope]").val();
    var scopeTargetArray = $($("#modal-create-group").find(".group-scope.active").attr("href")).find("input[name=scope_target]:checked");
    var scopeTarget = "";
    var sep = "";
    $.each(scopeTargetArray, function(i, target) {
        scopeTarget = scopeTarget + sep + $(target).val();
        sep = ",";
    });
    if (valCheckGroup()) {
        $.ajax({
            type: "GET",
            url: '/group/create',
            data: {
                    groupName: $("#make-group-name").val(),
                    password: $("#make-group-pass").val(),
                    groupScope: groupScope,
                    scopeTarget: scopeTarget,
                    groupId: $("#target-group-id").val()
            },
            success: function(result){
                if (result["result"] === "success") {
                    updateGroupList();
                } else {
                    $("#create-err-msg").text("同名のグループがすでに存在するため、別の名前を入力してください。");
                    $("#make-group-name").val("");
                    $("#make-group-pass").val("");
                    $("#make-group-pass-check").val("")
                }
            },
            error: function(XMLHttpRequest, textStatus, errorThrown){
            }
        });
    }
}

function valCheckGroup() {
    if (!!!$("#make-group-name").val()) {
        alert("チャンネル名を入力してください。");
        return false;
    }
    if ($("#group-toggle-on").hasClass("active")) {
        if (!!!$("#make-group-pass").val()) {
            alert("パスワードを入力してください。");
            return false;
        }
        if ($("#make-group-pass").val() !== $("#make-group-pass-check").val()) {
            alert("パスワードが確認用パスワードと一致していません。");
            return false;
        }
    }
    return true;
}

function needAuthorized(groupId) {
    $.ajax({
        type: "GET",
        url: '/group/needAuth',
        async: false,
        data: {
                groupId: groupId
        },
        success: function(result){
            if (result.needAuthorized) {
                $("#modal-join-group-none-auth").hide();
                $("#modal-join-group-need-auth").show();
                $("#need-authorized").val("true");
            } else {
                $("#modal-join-group-none-auth").show();
                $("#modal-join-group-need-auth").hide();
                $("#need-authorized").val("false");
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
            console.log("Method needAuthorized() has error.");
        }
    });
}