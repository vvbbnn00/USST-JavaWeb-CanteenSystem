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
        return window.sessionStorage.getItem(key);
    },
    set(key, value) {
        key = getKey(key);
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
        return window.sessionStorage.clear();
    }
}

const localStore = {
    get(key) {
        key = getKey(key);
        if (typeof window === "undefined") {
            return null;
        }
        return window.localStorage.getItem(key);
    },
    set(key, value) {
        key = getKey(key);
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
        return window.localStorage.clear();
    }
}

const store = {
    session: sessionStore,
    local: localStore,
}

export default store;