import request from "../utils/request";
import {BASE_URL} from "./index";

export const getAnnouncementList = (canteenId : number) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/${canteenId}/announcement/list`,
        method: 'GET',
    });
}

export const deleteAnnouncement = (canteenId : number, announcementId : number) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/${canteenId}/announcement/${announcementId}`,
        method: 'DELETE'
    });
}

export const updateAnnouncement = (canteenId : number, announcementId : number, form : any) => {
    delete form.announcementId;
    return request({
        url: `${BASE_URL}/api/rest/canteen/${canteenId}/announcement/${announcementId}`,
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        data: JSON.stringify(form)
    });

}

export const newAnnouncement = (canteenId : number, createForm : any) => {

    return request({
        url: `${BASE_URL}/api/rest/canteen/${canteenId}/announcement`,
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        data: JSON.stringify(createForm)
    });
}