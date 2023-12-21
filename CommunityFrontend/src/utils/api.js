"use client";
const BASE_URL = "http://10.100.164.6:60001/backend"

const fetchApi = (...args) => {
    const [url, options] = args
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
    const [url, options] = api
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
                window.location.href = "/login";
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