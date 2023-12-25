import {sha256} from 'js-sha256'

export function doSign(data) {
    const key = "msgDigest2023";
    let bytes = "";
    const timestamp = Date.now().toString();
    bytes += timestamp + "\0";

    for (let i = 0; i < data.length; i++) {
        bytes += String.fromCharCode((data.charCodeAt(i) % 256) ^ 10);
    }
    bytes += key;
    const sha256Data = sha256(bytes);
    const reversedSha1 = sha256Data.slice(20,) + sha256Data.slice(0, 20);
    return timestamp + "\0" + reversedSha1;
}
