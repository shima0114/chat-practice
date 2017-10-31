function tglBtn(elm) {
    var $this = $(elm);
    $this.find('.btn').toggleClass('active');

    if ($this.find('.btn-primary').size()>0) {
        $this.find('.btn').toggleClass('btn-primary');
    }
    $this.find('.btn').toggleClass('btn-default');

    if ($("#tgl-on").hasClass("active")) {
        $("#new_password").attr("disabled", false);
        $("#new_password_c").attr("disabled", false);
    } else {
        $("#new_password").attr("disabled", true);
        $("#new_password_c").attr("disabled", true);
    }
}

function userUpdate() {
    if (!checkNewPassword()) {
        return false;
    }
    if (!checkConnection()) {
        return false;
    }
    $("#user_upd_form").submit();
}

function checkNewPassword() {

    $(".has-error").each(function(i, elm) {
        $(elm).removeClass("has-error");
    });
    $("label.text-danger").remove();

    if ($("#chg-pwd-input").hasClass("in")) {
        var errMsg = "";
        if (!!!$("#new_password").val()) {
            $("#new_password").parent("div.form-group").addClass("has-error");
            errMsg = "「新パスワード」が入力されていません。";
        }
        if (!!!$("#new_password_c").val()) {
            $("#new_password_c").parent("div.form-group").addClass("has-error");
            var sep = "";
            if (!!errMsg) {
                var sep = "<br />";
            }
            errMsg = errMsg + sep + "「新パスワード（確認）」が入力されていません。";
        }
        if (!!!errMsg && $("#new_password").val() !== $("#new_password_c").val()) {
            $("#new_password").parent("div.form-group").addClass("has-error");
            $("#new_password_c").parent("div.form-group").addClass("has-error");
            errMsg = "「新パスワード」と「新パスワード（確認）」が一致していません。";
        }
        if (!!errMsg) {
            var msgTag = $("<label></label>").addClass("text-danger").html(errMsg)
            $("#chg-pwd-input").prepend(msgTag);
            return false;
        }
    }
    return true;
}

function checkConnection() {
    if (!!webSocket) {
        if (!confirm("現在接続中のチャンネルは切断されますがよろしいですか？")) {
            return false;
        }
        conClose();
    }
    return true;
}

$(".list-group-menu").on("click", function() {
    var $iconTag = $(this).find("span.glyphicon");
    if ($iconTag.hasClass("glyphicon-plus")) {
        $iconTag.removeClass("glyphicon-plus");
        $iconTag.addClass("glyphicon-minus");
    } else {
        $iconTag.removeClass("glyphicon-minus");
        $iconTag.addClass("glyphicon-plus");
    }
});

$('#image-upload-form').on('dragover', function(evt) {
  evt.preventDefault();
  $('#drag-drop-area').addClass('dragover');
});

$('#image-upload-form').on('drop', function(evt) {
    evt.preventDefault();
    $("#upload-img").attr("src", "");
    $('#drag-drop-area').removeClass('dragover');
    //userImageUpload(evt)
    var files = evt.originalEvent.dataTransfer.files[0];
    //$('#prevbox ul li').remove();
    if (!files || files.type.indexOf('image/') < 0) {
        return;
    }
    var fileReader = new FileReader();
    fileReader.onload = function( event ) {
        var loadedImageUri = event.target.result;
        $("#upload-img").on("load", function() {
            base64ImageResize(loadedImageUri);
        });
        $("#upload-img").attr("src", loadedImageUri);
   };
    fileReader.readAsDataURL(files);
    $(".upload-target").show();
});

function base64ImageResize(imgB64_src) {
    // CSSで調整済みのサイズを取得
    var height = $("#upload-img").height();
    var width = $("#upload-img").width();

    // Image Type
    var img_type = imgB64_src.substring(5, imgB64_src.indexOf(";"));
    // Source Image
    var img = new Image();
    img.onload = function() {
        // New Canvas
        var canvas = document.createElement('canvas');
        canvas.width = width;
        canvas.height = height;
        // Draw (Resize)
        var ctx = canvas.getContext('2d');
        ctx.drawImage(img, 0, 0, width, height);
        // Destination Image
        var imgB64_dst = canvas.toDataURL(img_type);
        $("#image-src").val(imgB64_dst);
    };
    img.src = imgB64_src;
}

function userImageUpload() {
    if (!checkConnection()) {
        return false;
    }
    // base64文字列を登録（ファイル実体は不要）
//    $("#image-src").val($("#upload-img").attr("src"));
    $("#image-upload-form").append($("#user-id"));
    $("#image-upload-form").submit();
}

function updateGroupList() {
    $(".none-group-tag").remove();
    $.ajax({
        type: "GET",
        url: '/group/listUpdate',
        data: {userId:$("#user-id").val()},
        success: function(result){
            var joiningList = result.joiningList;
            var invitationList = result.invitationList;
            var abstentionList = result.abstentionList;
            // 参加済リスト更新
            joiningList.forEach(function(group){
                var groupTag = $("#reg-group").find("#group-" + group.id);
                if (!!!groupTag.length) {
                    var $clone = $("#group-join-list-base").clone().removeAttr("id").attr("id", "group-" + group.id);
                    $clone.attr("data-group-id", group.id);
                    var scope = group.scope;
                    $clone.find("div.joining-group-name").text(group.groupName);
                    $("#reg-group div.list-group").append($clone);
                    if (group.createUserId !== $("#user-id").val()) {
                        $clone.find(".conf-link").remove();
                    }
                    $clone.find("i").tooltip();
                    $clone.find("#group-info-base").attr("id", "group-info-" + group.id)
                        .attr("data-group-scope", scope)
                        .attr("data-group-name", group.groupName)
                        .attr("data-group-need-auth", group.needAuthorized);
                    $clone.show();
                }
            });
            if (!!!$("#reg-group div.input-group:not(#group-join-list-base)").length) {
                var noneTag = $("<label></label>").addClass("none-group-tag").text("登録済のグループはありません。");
                $("#reg-group div.panel-body").prepend(noneTag);
            }
            // 招待済リスト更新
            invitationList.forEach(function(group){
                var groupTag = $("#inv-group").find("#group-" + group.id);
                if (!!!groupTag.length) {
                    var addTag = $("#group-invitation-base a").clone();
                    addTag.attr("id", "group-" + group.id)
                        .attr("data-group-id", group.id)
                        .attr("data-group-name", group.groupName)
                        .attr("data-group-scope", group.scope);
                    addTag.find("label").text(group.groupName);
                    if (!!!$("#reg-group a#group-" + group.id).length) {
                        $("#inv-group div.list-group").append(addTag);
                    }
                }
            });
            if (!!!$("#inv-group div.list-group a:not(#group-invitation-base > a)").length) {
                var noneTag = $("<label></label>").addClass("none-group-tag").text("参加待ちのグループはありません。");
                $("#inv-group div.list-group").prepend(noneTag);
            }

            // 未参加リスト更新
            abstentionList.forEach(function(group){
                var groupTag = $("#other-group").find("#group-" + group.id);
                if (!!!groupTag.length) {
                    var addTag = $("#group-abstention-base a").clone();
                    addTag.attr("id", "group-" + group.id)
                        .attr("data-group-id", group.id)
                        .attr("data-group-name", group.groupName)
                        .attr("data-group-scope", group.scope);
                    addTag.find("label").text(group.groupName);
                    $("#other-group div.list-group").append(addTag);
                }
            });
            if (!!!$("#other-group div.list-group a:not(#group-abstention-base > a)").length) {
                var noneTag = $("<label></label>").addClass("none-group-tag").text("参加可能なグループはありません。");
                $("#other-group div.list-group").prepend(noneTag);
            }

        },
        error: function(XMLHttpRequest, textStatus, errorThrown){
        }
    });
}