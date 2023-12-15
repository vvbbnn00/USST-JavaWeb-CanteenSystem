import request from '../utils/request';

export const getUserList = () => {
    return request({
        url: './user.json',
        method: 'get'
    });
};

export const getCanteenList = () => {
    return request({
        url: './canteen.json',
        method: 'get'
    });
}