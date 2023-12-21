"use client"
import NavigationBar from "@/components/NavigationBar";
import {Avatar, Button, Chip, Skeleton, Textarea, User} from "@nextui-org/react";
import store from "@/utils/store";
import {useEffect, useState} from "react";
import PublishForm from "@/components/community/PublishForm";
import {fetchApi} from "@/utils/api";
import {formatDateTimeFromNow} from "@/utils/string";
import Link from "next/link";
import ImageLayout from "@/components/community/ImageLayout";
import {CommentIcon} from "@/components/icons/CommentIcon";
import {LikeFilledIcon} from "@/components/icons/LikeFilledIcon";


export default function Home() {
    const [user, setUser] = useState();
    const [hotTopic, setHotTopic] = useState();

    useEffect(() => {
        const meUserData = store.session.get("meUserData");
        if (meUserData) {
            setUser(meUserData);
            return;
        }
        fetch("/api/rest/user/me").then(userData => {
            const {data} = userData;
            store.session.set("meUserData", data);
            setUser(data);
        });
    }, []);

    useEffect(() => {
        fetchApi("/api/rest/topic/list", {
            method: "POST",
            body: JSON.stringify({
                page: 1,
                pageSize: 1,
                orderBy: "compValue",
                asc: false
            })
        }).then(r => {
            const list = r.list;
            if (list.length > 0) {
                setHotTopic(list[0]);
                console.log(list[0]);
            }
        });
    }, []);

    return (
        <>
            <NavigationBar/>
            <main className={"max-w-screen-lg mx-auto"}>
                <div className="flex justify-center p-5 flex-row gap-5 flex-wrap">
                    <div className={"flex flex-col items-center lg:w-2/3 w-full"}>
                        <PublishForm user={user}/>
                        <div className={"bg-white mt-5 w-full rounded shadow-xl pb-5"}>
                            <div className={"p-5 font-bold text-lg text-gray-700 pb-5 border-b border-gray-200"}>
                                社区话题
                            </div>
                            {
                                !hotTopic ?
                                    <div className={"flex flex-row gap-5 p-5"}>
                                        <div>
                                            <Skeleton className="flex rounded-full w-16 h-16"/>
                                        </div>
                                        <div className="w-full flex flex-col gap-2 pt-2">
                                            <Skeleton className="h-5 w-3/5 rounded-lg"/>
                                            <Skeleton className="h-3 w-4/5 rounded-lg"/>
                                            <Skeleton className="h-3 w-4/5 rounded-lg"/>
                                        </div>
                                    </div> :
                                    <div className={"flex flex-row hover:bg-gray-50 cursor-pointer transition-background pb-3"}>
                                        <div className={"p-5 w-fit flex items-start justify-center"}>
                                            <Link href={"/user/" + hotTopic?.user?.userId}>
                                                <Avatar src={hotTopic?.user?.avatar} size={"lg"}/>
                                            </Link>
                                        </div>
                                        <div className={"flex-col pt-5 pr-5 grow"}>
                                            <div className={"text-gray-500 text-base font-normal"}>
                                                <Chip color="warning" variant={"flat"}>热门</Chip>
                                                <span
                                                    className={"break-all pl-1.5 font-bold text-gray-700 text-lg"}>{hotTopic?.title}</span>
                                                <Link href={"/user/" + hotTopic?.user?.userId}>
                                            <span className={"text-gray-500 pl-1.5 font-normal"}>
                                                @{hotTopic?.user?.username}
                                            </span>
                                                </Link>
                                                <span className={"pl-0.5 pr-0.5 text-lg"}>·</span>
                                                <span className={"text-gray-500 font-normal"}>
                                            {formatDateTimeFromNow(hotTopic?.createdAt)}
                                        </span>
                                            </div>
                                            <div className={"text-gray-700 text-base pt-1 line-clamp-5"}>
                                                {hotTopic?.content}
                                            </div>
                                            <div className={"pt-2"}>
                                                <Link href={"/topic/" + hotTopic?.topicId}>
                                            <span
                                                className={"text-blue-500 text-base font-normal cursor-pointer hover:underline"}>
                                                查看详情
                                            </span>
                                                </Link>
                                            </div>
                                            {
                                                hotTopic?.imageInfoList?.length > 0 &&
                                                <div className={"pt-2 pb-3"}>
                                                    <ImageLayout
                                                        imageInfoList={[...hotTopic?.imageInfoList]}/>
                                                </div>
                                            }
                                            <div className={"flex flex-row items-center gap-5"}>
                                                <div
                                                    className={"text-gray-500 flex items-center gap-2 hover:bg-gray-100 rounded-full p-2 cursor-pointer transition-all active:bg-gray-200"}>
                                                    <CommentIcon/> <span className={"select-none"}>{hotTopic?.commentCount}</span>
                                                </div>
                                                <div
                                                    className={
                                                        "flex items-center gap-2 hover:bg-red-100 rounded-full p-2 cursor-pointer transition-all active:bg-red-200"
                                                        + (hotTopic?.isLiked ? " text-red-500" : "text-gray-500")
                                                    }
                                                >
                                                    {hotTopic?.isLiked ? <LikeFilledIcon/> :
                                                        <LikeFilledIcon/>} <span className={"select-none"}>{hotTopic?.likeCount}</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                            }
                        </div>
                    </div>
                    <div className={"grow"}>
                        <div className={"bg-white rounded p-5 font-bold text-lg text-gray-700"}>
                            最新食堂排名
                        </div>
                    </div>
                </div>
            </main>
        </>
    )
}
