import request from "../utils/request";
import {BASE_URL} from "./index";

export const getCuisineList = (query: any) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/${query.canteenId}/cuisine/list`,
        method: 'POST',
        data: JSON.stringify(query)
    });
}

export const createCuisine = (form: any) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/${form.canteenId}/cuisine`,
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        data: JSON.stringify(form)
    });
}

export const updateCuisine = (form: any) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/${form.canteenId}/cuisine/${form.cuisineId}`,
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        data: JSON.stringify(form)
    });
}

export const deleteCuisine = (form: any) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/${form.canteenId}/cuisine/${form.cuisineId}`,
        method: 'DELETE'
    })
}