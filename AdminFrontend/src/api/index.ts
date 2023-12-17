export const BASE_URL = "http://192.168.19.10:60001/backend";
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


