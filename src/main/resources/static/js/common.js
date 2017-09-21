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

    var srcStr = "";
    if (typeImage.test(imgObj.saveFileName)) {
        srcStr = "/files/" + imgObj.saveFileName;
    } else {
        srcStr = "/image/other_file.jpg";
    }
    var parent = $("<div></div>").addClass("chat-talk").addClass("clear-float");
    var imgTag = $("<img></img>")
            .addClass("img-responsive img-rounded")
            .attr("src", srcStr)
            .attr("width", "150px")
            .attr("title", imgObj.fileName)
            .attr("name", imgObj.saveFileName);
    var msgSenderTag = $("<div></div>").text(imgObj.userName);
    if (imgObj.userId === $("#user-id").val()) {
        parent.addClass("talk-send");
        msgSenderTag.addClass("send-name clear-float");
    } else {
        parent.addClass("talk-receive");
        msgSenderTag.addClass("receive-name clear-float");
    }
    parent.append(imgTag).append(msgSenderTag);
    return parent;
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