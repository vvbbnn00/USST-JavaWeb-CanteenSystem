import request from "../utils/request";
import {BASE_URL} from "./index";

export const getCanteenList = (query: any) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/list`,
        method: 'POST',
        data: JSON.stringify(query)
    });
};

export const getUserCanteen = async () => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/managed`,
        method: 'GET'
    });
}

export const deleteCanteen = (id: number) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/${id}`,
        method: 'DELETE'
    });
}

export const updateCanteen = (form: any) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/${form.canteenId}`,
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        data: JSON.stringify(form)
    });
}

export const newCanteen = (createForm: any) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen`,
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        data: JSON.stringify(createForm)
    });
}

export const getCanteenCommentList = (canteenId: number) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/${canteenId}/comment`,
        method: 'GET',
    });
}

export const deleteCanteenComment = (canteenId: number, commentId: number) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/${canteenId}/comment/${commentId}`,
        method: 'DELETE'
    });
}

export const replyCanteenComment = (canteenId: number, commentId: number, content: string) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/${canteenId}/comment/${commentId}/reply`,
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        data: JSON.stringify({
            content: content
        })
    });
}