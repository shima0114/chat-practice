function messageTag(message, userName, userId) {
    var parent = $("<div></div>");
    var msgTag = $("<div></div>").text(message);
    var userTag = $("<div></div>").text(userName);
    if (userId == $("#user-id").val()) {
        msgTag.addClass("send");
        userTag.addClass("send-name").addClass("clear-float");
    } else {
        msgTag.addClass("receive");
        userTag.addClass("receive-name").addClass("clear-float");
    }
    parent.append(msgTag).append(userTag);
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
    var parent = $("<div></div>");
    var imgTag = $("<img></img>")
            .addClass("img-responsive img-rounded")
            .attr("src", srcStr)
            .attr("width", "150px")
            .attr("title", imgObj.fileName)
            .attr("name", imgObj.saveFileName);
    var msgSenderTag = $("<div></div>").text(imgObj.userName);
    if (imgObj.userId === $("#user-id").val()) {
        imgTag.addClass("send");
        msgSenderTag.addClass("send-name clear-float");
    } else {
        imgTag.addClass("receive");
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