// @ts-ignore
import md5 from 'crypto-js/md5';

export const GRAVATAR_URL = "https://gravatar.kuibu.net/avatar/"

export function parseDateTime(date: string) {
    const dateObj = new Date(date);
    const year = dateObj.getFullYear();
    const month = dateObj.getMonth() + 1;
    const day = dateObj.getDate();
    const hours = dateObj.getHours();
    const minutes = dateObj.getMinutes();
    const seconds = dateObj.getSeconds();

    return `${year}-${month < 10 ? "0" + month : month}-${day < 10 ? "0" + day : day} ${hours < 10 ? "0" + hours : hours}:${minutes < 10 ? "0" + minutes : minutes}:${seconds < 10 ? "0" + seconds : seconds}`;
}

export function deepCopy(obj: any) {
    if (obj === null || typeof obj !== 'object') {
        return obj;
    }

    let copy: any = Array.isArray(obj) ? [] : {};

    for (let key in obj) {
        if (obj.hasOwnProperty(key)) {
            copy[key] = deepCopy(obj[key]);
        }
    }

    return copy;
}

export function getAvatarUrl(email: string) {
    const hash = md5(email);
    return `${GRAVATAR_URL}${hash}?d=identicon`;
}
