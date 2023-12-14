import request from '../utils/request';

export const getUserList = () => {
    return request({
        url: './user.json',
        method: 'get'
    });
};