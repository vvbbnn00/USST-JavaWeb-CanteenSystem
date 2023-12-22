export const formatDateTime = (date) => {
    const dateObj = new Date(date);
    const year = dateObj.getFullYear();
    let month = dateObj.getMonth() + 1;
    let day = dateObj.getDate();

    let hour = dateObj.getHours();
    let minute = dateObj.getMinutes();
    let second = dateObj.getSeconds();

    if (month < 10) month = "0" + month;
    if (day < 10) day = "0" + day;

    if (hour < 10) hour = "0" + hour;
    if (minute < 10) minute = "0" + minute;
    if (second < 10) second = "0" + second;

    return `${year}-${month}-${day} ${hour}:${minute}:${second}`;
}


export const formatDateTimeFromNow = (date) => {
    const now = new Date();
    date = new Date(date);
    const diff = now.getTime() - date.getTime();

    if (diff > 24 * 3600 * 1000 * 30) {
        return formatDateTime(date);
    }
    if (diff < 0) {
        return "未来";
    }

    const diffDay = Math.floor(diff / (24 * 3600 * 1000));
    const diffHour = Math.floor(diff / (3600 * 1000));
    const diffMinute = Math.floor(diff / (60 * 1000));
    const diffSecond = Math.floor(diff / 1000);

    if (diffDay > 0) {
        return `${diffDay}天前`;
    } else if (diffHour > 0) {
        return `${diffHour}小时前`;
    } else if (diffMinute > 0) {
        return `${diffMinute}分钟前`;
    } else if (diffSecond > 0) {
        return `${diffSecond}秒前`;
    } else {
        return "刚刚";
    }
}
