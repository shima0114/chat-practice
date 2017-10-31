function messageTag(message, userName, userId, userImage) {
    var parent = $("<div></div>").addClass("chat-talk").addClass("clear-float");
    var msgTag = $("<div></div>").text(message).addClass("talk-message");
    var userTag = $("<div></div>").text(userName).addClass("talk-name").addClass("clear-float");
    if (userId == $("#user-id").val()) {
        parent.addClass("talk-send");
    } else {
        parent.addClass("talk-receive");
    }
    parent.append(msgTag).append(userTag);
    if (!!userImage) {
        var iconTag = $("<div></div>").addClass("user-icon").addClass("icon-circle");
        var imgTag = $("<img></img>").attr("src", userImage).attr("width", "80px");
        parent.prepend(iconTag.append(imgTag));
    }
    return parent;
}

var typeImage = new RegExp(".*\.(jpg|jpeg|png|gif|bmp|ico|tif|tiff)", "i");

function fileLinkTag(imgObj) {

    var srcStr = convertFileNameToSrc(imgObj.saveFileName);
    var parent = $("<div></div>").addClass("chat-talk").addClass("clear-float");
    var imgTag = $("<img></img>")
            .addClass("img-responsive img-rounded")
            .attr("src", srcStr)
            .attr("width", "150px")
            .attr("title", imgObj.fileName)
            .attr("name", imgObj.saveFileName);
    var msgSenderTag = $("<div></div>").text(imgObj.userName).addClass("talk-name").addClass("clear-float");
    if (imgObj.userId === $("#user-id").val()) {
        parent.addClass("talk-send");
        //msgSenderTag.addClass("send-name clear-float");
    } else {
        parent.addClass("talk-receive");
        //msgSenderTag.addClass("receive-name clear-float");
    }
    parent.append(imgTag).append(msgSenderTag);
    return parent;
}

function convertFileNameToSrc(fileName) {
    if (typeImage.test(fileName)) {
        return "/files/" + fileName;
    }
    return "/image/other_file.jpg";
}

function imageScroll() {
    var $lastImg = $("#messages").find("img").last();
    if (!!$lastImg) {
        $lastImg.on("load", function() {
            msgScroll();
        })
    }
}

function msgScroll() {
    var msgArea = $('#messages');
    if (!!msgArea) {
        msgArea.scrollTop(msgArea[0].scrollHeight - msgArea.height());
    }
}

$(document).on("drop", "body:not(.drag-drop-area)", function(e) {
    e.preventDefault();
});

$(document).on('dragover', "body:not(.drag-drop-area)", function(evt) {
  evt.preventDefault();
});

function createSendTime(sendTime) {
    if (!!!sendTime) {
        var now = new Date();
        return " [ " + zeroPad(now.getHours(), 2) + ":" + zeroPad(now.getMinutes(), 2) + " ]";
    }
    return " [ " + zeroPad(sendTime.hour, 2) + ":" + zeroPad(sendTime.minute, 2) + " ]";
}

function createSendYmd(sendTime) {
    if (!!!sendTime) {
        var now = new Date();
        return now.getFullYear() + "/" + zeroPad(now.getMonth() + 1, 2) + "/" + zeroPad(now.getDate(), 2);
    }
    return sendTime.year + "/" + zeroPad(sendTime.monthValue, 2) + "/" + zeroPad(sendTime.dayOfMonth, 2);
}

function zeroPad(num, digit) {
    return (Math.pow(10, digit) + String(num)).slice(digit * -1);
}