export const BASE_URL = "http://192.168.19.10:60001/backend";

export const passwordLogin = async (username: string, password: string) => {
    const response = await fetch(`${BASE_URL}/auth/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username: username,
            password: password
        }),
        credentials: 'include'
    });
    return await response.json();
}


