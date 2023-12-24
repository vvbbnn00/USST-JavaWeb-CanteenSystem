import request from "../utils/request";
import {BASE_URL} from "./index";

export const getItemList = (query: any) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/item/list`,
        method: 'POST',
        data: JSON.stringify(query)
    });
};

export const deleteItem = (itemId: number) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/item/${itemId}`,
        method: 'DELETE'
    });
}

export const createItem = (form: any,canteenId: number) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/${canteenId}/cuisine/${form.cuisineId}/item`,
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        data: JSON.stringify(form)
    });
}

export const updateItem = (form: any) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/item/${form.itemId}`,
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        data: JSON.stringify(form)
    });
}

export const getItemComment = (itemId: number) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/item/${itemId}/comment`,
        method: 'GET'
    });
}

export const deleteItemComment = (itemId: number,commentId: number) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/item/${itemId}/comment/${commentId}`,
        method: 'DELETE'
    });
}


export const getItemInfo = (itemId: number) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/item/${itemId}`,
        method: 'GET'
    });
}