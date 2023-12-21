const KEY_PREFIX = "canteenSystem";
const VERSION = "1";

const getKey = (key) => {
    return `${KEY_PREFIX}|${VERSION}|${key}`;
}

const sessionStore = {
    get(key) {
        key = getKey(key);
        if (typeof window === "undefined") {
            return null;
        }
        const value = window.sessionStorage.getItem(key);
        if (value === null) {
            return null;
        }
        return JSON.parse(value);
    },
    set(key, value) {
        key = getKey(key);
        value = JSON.stringify(value);
        if (typeof window === "undefined") {
            return null;
        }
        return window.sessionStorage.setItem(key, value);
    },
    remove(key) {
        key = getKey(key);
        if (typeof window === "undefined") {
            return null;
        }
        return window.sessionStorage.removeItem(key);
    },
    clear() {
        if (typeof window === "undefined") {
            return null;
        }
        for (let i = 0; i < window.sessionStorage.length; i++) {
            const key = window.sessionStorage.key(i);
            if (key.startsWith(KEY_PREFIX)) {
                window.sessionStorage.removeItem(key);
            }
        }
    }
}

const localStore = {
    get(key) {
        key = getKey(key);
        if (typeof window === "undefined") {
            return null;
        }
        const value = window.localStorage.getItem(key);
        if (value === null) {
            return null;
        }
        return JSON.parse(value);
    },
    set(key, value) {
        key = getKey(key);
        value = JSON.stringify(value);
        if (typeof window === "undefined") {
            return null;
        }
        return window.localStorage.setItem(key, value);
    },
    remove(key) {
        key = getKey(key);
        if (typeof window === "undefined") {
            return null;
        }
        return window.localStorage.removeItem(key);
    },
    clear() {
        if (typeof window === "undefined") {
            return null;
        }
        for (let i = 0; i < window.localStorage.length; i++) {
            const key = window.localStorage.key(i);
            if (key.startsWith(KEY_PREFIX)) {
                window.localStorage.removeItem(key);
            }
        }
    }
}

const store = {
    session: sessionStore,
    local: localStore,
}

export default store;
