"use client"

import NavigationBar from "@/components/NavigationBar";
import BackButton from "@/components/common/BackButton";
import {Empty} from "antd";
import useSWR from "swr";
import {fetchApiWithAuth} from "@/utils/api";
import {Button, Image, Spinner, Tooltip} from "@nextui-org/react";
import {MessageIcon} from "@/components/icons/MessageIcon";
import {useEffect, useState} from "react";
import {getMe} from "@/utils/auth";
import {formatDateTime} from "@/utils/string";
import {calcLevelColor} from "@/utils/level";
import {VerifiedOutlined} from "@ant-design/icons";
import TopicList from "@/components/community/TopicList";
import Link from "next/link";

export default function UserPage({params}) {
    const {userId} = params;

    const {data, error, isLoading} = useSWR(
        `/api/rest/user/${userId}`,
        (...args) => fetchApiWithAuth(...args).then(r => r.data)
    );
    const [me, setMe] = useState();

    useEffect(() => {
        getMe().then(r => {
            setMe(r);
        });
    }, []);


    return <>
        <NavigationBar/>
        <main className={"flex justify-center"}>
            <div className={"w-full lg:w-[768px] bg-white m-5 rounded-md shadow-xl overflow-hidden pb-10"}>
                {isLoading && <div>
                    <div className={"flex flex-row items-center"}>
                        <BackButton/>
                        <div className={"flex-grow"}>
                            <h1 className={"text-lg font-bold truncate line-clamp-1 w-fit"}>
                                请稍候
                            </h1>
                        </div>
                    </div>
                    <div className={"h-[300px] flex items-center justify-center"}>
                        <Spinner color={"default"}/>
                    </div>
                </div>}
                {error && <div>
                    <div className={"flex flex-row items-center"}>
                        <BackButton/>
                        <div className={"flex-grow"}>
                            <h1 className={"text-lg font-bold truncate line-clamp-1 w-fit"}>
                                出错了
                            </h1>
                        </div>
                    </div>
                    <Empty description={error?.message || "加载失败"} className={"mt-5"}/>
                </div>}
                {!error && !isLoading && <div className={"flex flex-col"}>
                    <div className={"flex flex-row items-center"}>
                        <BackButton/>
                        <div className={"flex-grow"}>
                            <h1 className={"text-lg font-bold truncate line-clamp-1 w-fit"}>
                                #{data?.userId} {data?.username}
                            </h1>
                        </div>
                    </div>
                    <div className={"w-full relative"}>
                        <div className={"w-full h-[200px] overflow-hidden"}>
                            <Image
                                loading={"lazy"}
                                src={data?.avatar}
                                width={"100%"}
                                height={"200px"}
                                radius={"none"}
                                className={"object-cover w-full h-[200px] blur-xl"}
                            />
                        </div>
                        <div className={"w-full flex flex-col p-5"}>
                            <div className={"flex h-20 items-baseline relative"}>
                                <div
                                    className={"block w-[150px] h-[150px] rounded-full overflow-hidden absolute bottom-5 left-5 bg-white z-10"}>
                                    <div className={"w-[100%] h-[100%] items-center justify-center flex"}>
                                        <Image
                                            loading={"lazy"}
                                            src={data?.avatar}
                                            width={"100%"}
                                            height={"100%"}
                                            radius={"full"}
                                            className={"object-cover w-[138px] h-[138px] flex-grow"}
                                        />
                                    </div>
                                </div>
                                <div className={"flex justify-end items-center w-full"}>
                                    <Link href={`/user/message?to=${userId}`}>
                                        <div
                                            className={"rounded-full w-10 h-10 m-5 mt-0 mb-0 mr-3 border-gray-300 border-2 items-center justify-center flex text-lg hover:bg-gray-100 active:bg-gray-200 transition-background cursor-pointer"}>
                                            <MessageIcon/>
                                        </div>
                                    </Link>
                                    {
                                        data?.userId === me?.userId &&
                                        <Button
                                            className={"rounded-full"}
                                            variant={"bordered"}
                                        >
                                            编辑个人资料
                                        </Button>
                                    }
                                </div>
                            </div>
                            <h1 className={"pl-5 flex items-center gap-3"}>
                                <span className={"text-2xl font-bold text-gray-950"}>{data?.username}</span>
                                <div
                                    className={`text-sm bg-${calcLevelColor(data?.level)}-100 text-${calcLevelColor(data?.level)}-500 font-bold w-fit pl-2.5 pr-2.5 pt-0.5 pb-0.5 rounded`}>
                                    Lv{data?.level}
                                </div>
                                {
                                    data?.isVerified &&
                                    <div className={"text-green-500 text-xl flex items-center"}>
                                        <Tooltip content="认证用户">
                                            <VerifiedOutlined/>
                                        </Tooltip>
                                    </div>
                                }
                            </h1>
                            <div className={"text-gray-500 text-sm pl-5"}>
                                注册于 {formatDateTime(data?.createdAt)}
                            </div>
                            <div>
                                <h2 className={"text-lg font-bold p-5 pl-3 pb-3 border-b "}>发布话题</h2>
                                <TopicList filterUserId={data?.userId}/>
                            </div>
                        </div>
                    </div>
                </div>}
            </div>
        </main>
    </>
}
