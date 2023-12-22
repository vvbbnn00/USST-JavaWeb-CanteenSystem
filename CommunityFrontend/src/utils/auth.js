"use client";
import {fetchApi} from "@/utils/api";
import {sha256} from 'js-sha256'
import store from "@/utils/store";

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


export const login = async (username, password) => {
    const raw = username + "\0" + password;
    const [timestamp, sign] = doSign(raw).split("\0");

    const data = {
        username: username,
        password: password,
        timestamp: timestamp,
        sign: sign
    }

    try {
        const result = await fetchApi("/auth/login", {
            method: "POST",
            body: JSON.stringify(data)
        });
        const userData = result.data;
        store.local.set("userData", userData);
        location.href = "/";

        return result;
    } catch (e) {
        return {error: e.message || "登录失败"}
    }
}

export const register = async (username, password, email) => {
    const raw = username + "\0" + password + "\0" + email;
    const [timestamp, sign] = doSign(raw).split("\0");

    const data = {
        username: username,
        password: password,
        confirmPassword: password,
        email: email,
        timestamp: timestamp,
        sign: sign
    }

    try {
        const result = await fetchApi("/auth/register", {
            method: "POST",
            body: JSON.stringify(data)
        });
        const userData = result.data;
        store.local.set("userData", userData);
        location.href = "/";

        return result;
    } catch (e) {
        return {error: e.message || "注册失败"}
    }
}


export const logout = async () => {
    await fetchApi("/auth/logout", {
        method: "GET"
    });
    store.local.clear();
    store.session.clear();
    location.href = "/auth/login";
}
