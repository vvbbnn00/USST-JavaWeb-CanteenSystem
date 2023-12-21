"use client";
import store from "@/utils/store";

const BASE_URL = "http://10.100.164.6:60001/backend"

const fetchApi = (...args) => {
    let [url, options] = args
    options = options || {}
    options.headers = {
        "Content-Type": "application/json",
        "Accept": "application/json",
        ...options?.headers
    }
    options.credentials = "include"
    const reqUrl = url.startsWith("http") ? url : BASE_URL + url
    return fetch(reqUrl, options)
        .then(res => res.json())
        .then(res => {
            if (res.code === 200) {
                return res
            } else {
                throw {
                    code: res.code,
                    message: res.message
                }
            }
        }).catch(err => {
            throw {
                code: 500,
                message: err.message
            }
        })
}

const fetchApiWithAuth = (...api) => {
    let [url, options] = api
    options = options || {}
    options.headers = {
        "Content-Type": "application/json",
        "Accept": "application/json",
        ...options?.headers
    }
    options.credentials = "include"
    const reqUrl = url.startsWith("http") ? url : BASE_URL + url
    return fetch(reqUrl, options)
        .then(res => res.json())
        .then(res => {
            if (res.code === 200) {
                return res
            } else {
                throw {
                    code: res.code,
                    message: res.message
                };
            }
        }).catch(err => {
            if (err.code === 401) {
                store.session.clear();
                window.location.href = "/auth/login";
            }
            throw {
                code: 500,
                message: err.message
            }
        })
}

export {
    BASE_URL,
    fetchApi,
    fetchApiWithAuth
}
