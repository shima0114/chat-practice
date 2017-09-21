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