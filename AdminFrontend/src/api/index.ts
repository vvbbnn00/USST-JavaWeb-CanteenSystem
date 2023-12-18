import request from "../utils/request";

export const BASE_URL = "http://10.100.164.6:60001/backend";
import {doSign} from "../lib/security/release";

export const passwordLogin = async (username: string, password: string) => {
    const signData = doSign(username + "\0" + password);
    const [timestamp, sign] = signData.split("\0");
    const response = await fetch(`${BASE_URL}/auth/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username: username,
            password: password,
            sign,
            timestamp
        }),
        credentials: 'include'
    });
    return await response.json();
}

export const getUploadUrl = () => {
    return request({
        url: `${BASE_URL}/api/rest/image/upload`,
        method: 'post'
    });
}

