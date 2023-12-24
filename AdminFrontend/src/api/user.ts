import request from "../utils/request";
import {BASE_URL} from "./index";
import {usePermissStore} from "../store/permiss";

export const getUserList = (query: any) => {
    if (query.role === "") {
        delete query.role;
    }
    return request({
        url: `${BASE_URL}/api/rest/user/list`,
        method: 'POST',
        data: JSON.stringify(query)
    });
}

export const updateUser = (form: any) => {
    return request({
        url: `${BASE_URL}/api/rest/user/${form.userId}`,
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        data: JSON.stringify(form)
    });
}

export const deleteUser = (id: number) => {
    return request({
        url: `${BASE_URL}/api/rest/user/${id}`,
        method: 'DELETE'
    });
}


export const updateCurrentUser = async () => {
    const permission = usePermissStore();
    const userData = await request({
        url: `${BASE_URL}/api/rest/user/me`,
        method: 'GET'
    });
    const data = userData?.data;
    localStorage.setItem('ms_username', data.data.username);
    localStorage.setItem('ms_user_id', data.data.userId);
    localStorage.setItem('ms_email', data.data.email);
    localStorage.setItem('ms_avatar', data.data.avatar);
    const role = data.data.role;
    localStorage.setItem('ms_role', role);
    const keys = permission.defaultList[role === 'admin' ? 'admin' : 'canteen_admin'];
    permission.handleSet(keys);
    return data;
}
