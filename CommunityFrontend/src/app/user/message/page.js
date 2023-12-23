"use client"

import NavigationBar from "@/components/NavigationBar";
import {Empty} from "antd";
import {fetchApiWithAuth} from "@/utils/api";
import useSWR from "swr";
import data from "@emoji-mart/data";
import {Image, Tooltip} from "@nextui-org/react";
import {calcLevelColor} from "@/utils/level";
import {VerifiedOutlined} from "@ant-design/icons";
import {useEffect, useState} from "react";
import MessagePanel from "@/app/user/message/MessagePanel";

export default function MessagePage({searchParams}) {
    const {data: recentUser, isLoading: isRecentUserLoading} = useSWR(
        "/api/rest/user/message/target/list",
        (...args) => fetchApiWithAuth(...args).then(r => r.list),
        {
            refreshInterval: 5000
        }
    );
    const [selectedUser, setSelectedUser] = useState();

    useEffect(() => {
        if (searchParams?.to) {
            fetchApiWithAuth(`/api/rest/user/${searchParams?.to}`).then(r => {
                setSelectedUser(r.data);
            });
        }
    }, []);


    return <>
        <main>
            <NavigationBar/>
            <div className={"flex items-center justify-center"}>
                <div
                    className={"w-full lg:w-[1024px] bg-white m-5 rounded-md shadow-xl overflow-hidden flex h-[768px]"}>
                    <div className={"border-r border-gray-100 w-1/3 flex flex-col"}>
                        <div className={"text-gray-800 items-center gap-2 p-5 font-bold text-xl border-b pt-3 pb-3"}>
                            私信
                        </div>
                        <div className={"flex-grow flex flex-col"}>
                            {recentUser?.map((user, index) => {
                                return <div
                                    key={index}
                                    className={"flex items-center gap-2 p-5 cursor-pointer hover:bg-gray-50 transition-background" + (selectedUser?.userId === user?.userId ? " bg-gray-50" : "")}
                                    onClick={() => setSelectedUser(user)}
                                >
                                    <div className={"w-14 h-14 rounded-full bg-gray-100"}>
                                        <Image src={user?.avatar} alt={user?.username} loading={"lazy"}/>
                                    </div>
                                    <div className={"flex flex-col"}>
                                        <div
                                            className={"text-gray-800 font-bold text-lg"}>#{user?.userId} {user?.username}</div>
                                        <div className={"text-gray-500 flex items-center gap-2"}>
                                            <div
                                                className={`bg-${calcLevelColor(user?.level)}-100 text-${calcLevelColor(user?.level)}-500 font-bold w-fit pl-2.5 pr-2.5 pt-0.5 pb-0.5 rounded`}>
                                                Lv{user?.level}
                                            </div>
                                            {
                                                !user?.isVerified &&
                                                <div className={"text-green-500 text-xl flex items-center"}>
                                                    <Tooltip content="认证用户">
                                                        <VerifiedOutlined/>
                                                    </Tooltip>
                                                </div>
                                            }
                                        </div>
                                    </div>
                                </div>
                            })}
                        </div>

                        {(!data || data?.length === 0) &&
                            <div className={"flex-grow flex flex-col items-center justify-center"}>
                                <Empty description={"去社区发现用户吧"}/>
                            </div>}
                    </div>
                    <div className={"flex-grow h-full flex-col"}>
                        {!selectedUser &&
                            <div className={"flex items-center w-full justify-center flex-col h-full"}>
                                <Empty description={"请选择一个用户聊天"}/>
                            </div>}
                        {selectedUser && <MessagePanel user={selectedUser}/>}
                    </div>
                </div>
            </div>
        </main>
    </>
}
