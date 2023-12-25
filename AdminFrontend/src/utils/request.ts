import axios, {AxiosInstance, AxiosError, AxiosResponse, AxiosRequestConfig} from 'axios';

const service: AxiosInstance = axios.create({
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json',
    },
    withCredentials: true
});

service.interceptors.request.use(
    (config: any) => {
        return config;
    },
    (error: AxiosError) => {
        console.log(error);
        return Promise.reject();
    }
);

service.interceptors.response.use(
    (response: AxiosResponse) => {
        if (response.status === 200) {
            if (response.data.code !== 200) {
                return Promise.reject();
            }
            return response;
        } else {
            Promise.reject();
        }
    },
    (error: AxiosError) => {
        // @ts-ignore
        if (error?.response?.data?.code === 401) {
            localStorage.removeItem('ms_username');
            localStorage.removeItem('ms_user_id');
            localStorage.removeItem('ms_email');
            localStorage.removeItem('ms_avatar');
            localStorage.removeItem('ms_role');
            localStorage.removeItem('ms_keys');
            window.location.href = '/auth/login';
            return Promise.reject();
        }

        console.log(error);
        return Promise.reject();
    }
);

export default service;
