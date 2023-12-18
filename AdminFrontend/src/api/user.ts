import request from "../utils/request";
import {BASE_URL} from "./index";

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