"use client";
import NavigationBar from "@/components/NavigationBar";
import useSWR from "swr";
import {fetchApiWithAuth} from "@/utils/api";
import {Avatar, Button, Chip, Input, Spinner} from "@nextui-org/react";
import {useFormStatus} from "react-dom";
import {message, Modal} from "antd";
import {useState} from "react";
import {ArrowRightOutlined} from "@ant-design/icons";
import Link from "next/link";

const {confirm} = Modal;

const SubmitButton = () => {
    const {pending} = useFormStatus();

    return <Button
        color={"primary"}
        className={"w-full mt-5"}
        type={"submit"}
        isLoading={pending}
    >提交</Button>
}


export default function UserProfile() {
    const [messageApi, contextHolder] = message.useMessage();

    const {data, isLoading, error} = useSWR(
        `/api/rest/user/me`,
        (...args) => fetchApiWithAuth(...args).then(r => r.data)
    );

    return (<>
            <main className={"min-h-[100vh]"}>
                {contextHolder}
                <NavigationBar/>
                <div className={"flex items-center justify-center"}>
                    <div className={"w-full lg:w-[500px] bg-white m-5 rounded-md shadow-xl overflow-hidden"}>
                        <h1 className={"p-5 text-center font-bold text-gray-900 text-2xl pb-2"}>用户信息</h1>

                        {isLoading &&
                            <div className={"p-5 text-center"}>
                                <Spinner color={"default"}/>
                            </div>
                        }

                        {!isLoading && <div className={"flex flex-col gap-1 p-5"}>
                            <div
                                className={"flex flex-row hover:bg-gray-50 p-5 pt-3 pb-3 rounded-md cursor-pointer transition-all"}
                                onClick={() => {
                                    messageApi.open({
                                        type: 'info',
                                        content: '头像通过Gravatar获取，您可以在Gravatar官网上注册并上传头像，然后使用相同的邮箱登录本站，即可显示您的头像。'
                                    })
                                }}
                            >
                                <div className={"flex flex-row gap-2 items-center text-gray-600 font-bold"}>
                                    用户头像
                                </div>
                                <div className={"flex-grow"}></div>
                                <div className={"flex flex-row gap-2 items-center"}>
                                    <Avatar src={data?.avatar} size={"md"}/>
                                </div>
                            </div>
                            <div
                                className={"flex flex-row hover:bg-gray-50 p-5 pt-3 pb-3 rounded-md cursor-default transition-all"}>
                                <div className={"flex flex-row gap-2 items-center text-gray-600 font-bold"}>
                                    用户ID
                                </div>
                                <div className={"flex-grow"}></div>
                                <div className={"flex flex-row gap-2 items-center"}>
                                    <span className={"text-gray-500"}>#{data?.userId}</span>
                                </div>
                            </div>
                            <div
                                className={"flex flex-row hover:bg-gray-50 p-5 pt-3 pb-3 rounded-md cursor-default transition-all"}>
                                <div className={"flex flex-row gap-2 items-center text-gray-600 font-bold"}>
                                    用户名
                                </div>
                                <div className={"flex-grow"}></div>
                                <div className={"flex flex-row gap-2 items-center"}>
                                    <span className={"text-gray-500"}>{data?.username}</span>
                                </div>
                            </div>
                            <Link href={"/user/verify"}>
                                <div
                                    className={"flex flex-row hover:bg-gray-50 p-5 pt-3 pb-3 rounded-md cursor-pointer transition-all"}>
                                    <div className={"flex flex-row gap-2 items-center text-gray-600 font-bold"}>
                                        用户认证
                                    </div>
                                    <div className={"flex-grow"}></div>
                                    <div className={"flex flex-row gap-2 items-center"}>
                                        {data?.isVerified && <Chip color={"success"} variant={"flat"}>已认证</Chip>}
                                        {!data?.isVerified && <Chip color={"error"} variant={"flat"}>未认证</Chip>}
                                        <ArrowRightOutlined/>
                                    </div>
                                </div>
                            </Link>
                            <Link href={"/user/profile/email"}>
                                <div
                                    className={"flex flex-row hover:bg-gray-50 p-5 pt-3 pb-3 rounded-md cursor-pointer transition-all"}>
                                    <div className={"flex flex-row gap-2 items-center text-gray-600 font-bold"}>
                                        用户邮箱
                                    </div>
                                    <div className={"flex-grow"}></div>
                                    <div className={"flex flex-row gap-2 items-center"}>
                                        <span className={"text-gray-500"}>{data?.email}</span>
                                        <ArrowRightOutlined/>
                                    </div>
                                </div>
                            </Link>
                            <Link href={"/user/profile/password"}>
                                <div
                                    className={"flex flex-row hover:bg-gray-50 p-5 pt-3 pb-3 rounded-md cursor-pointer transition-all"}>
                                    <div className={"flex flex-row gap-2 items-center text-gray-600 font-bold"}>
                                        修改密码
                                    </div>
                                    <div className={"flex-grow"}></div>
                                    <div className={"flex flex-row gap-2 items-center"}>
                                        <ArrowRightOutlined/>
                                    </div>
                                </div>
                            </Link>
                        </div>}

                    </div>
                </div>
            </main>
        </>
    )
}
