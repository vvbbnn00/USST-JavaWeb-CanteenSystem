import axios, {AxiosInstance, AxiosError, AxiosResponse, AxiosRequestConfig} from 'axios';

const service: AxiosInstance = axios.create({
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json',
    }
});

service.interceptors.request.use(
    (config: AxiosRequestConfig) => {
        const token = localStorage.getItem('ms_token');
        if (token) {
            // @ts-ignore
            config.headers['Authorization'] = token;
        }
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
            return response;
        } else {
            Promise.reject();
        }
    },
    (error: AxiosError) => {
        // @ts-ignore
        if (error?.response?.data?.code === 401) {
            localStorage.removeItem('ms_username');
            localStorage.removeItem('ms_token');
            location.reload();
            return Promise.reject();
        }

        console.log(error);
        return Promise.reject();
    }
);

export default service;
