"use client";
import NavigationBar from "@/components/NavigationBar";
import useSWR from "swr";
import {fetchApiWithAuth} from "@/utils/api";
import {formatDateTimeFromNow} from "@/utils/string";
import {useState} from "react";
import {Empty} from "antd";
import DOMPurify from 'dompurify';


const readNotification = (notificationId) => {
    return fetchApiWithAuth(`/api/rest/user/notification/${notificationId}`, {
        method: "PUT"
    }).then(r => {
    });
}

const readAllNotification = () => {
    return fetchApiWithAuth(`/api/rest/user/notification/all`, {
        method: "PUT"
    }).then(r => {
    });
}


export default function UserNotificationPage() {
    const [filterMode, setFilterMode] = useState("unread");
    const [refresh, setRefresh] = useState(0);
    const {data, isLoading, error} = useSWR(
        `/api/rest/user/notification/${filterMode}?${refresh}`,
        (...args) => fetchApiWithAuth(...args).then(r => r.list)
    );


    function Notification({data}) {
        const userRegex = /<user id=(\d+)>(.*?)<\/user>/g;
        const topicRegex = /<topic id=(\d+)>(.*?)<\/topic>/g;
        const canteenRegex = /<canteen id=(\d+)>(.*?)<\/canteen>/g;
        const complaintRegex = /<complaint id=(\d+)>(.*?)<\/complaint>/g;
        const itemRegex = /<item id=(\d+)>(.*?)<\/item>/g;
        const linkClass = "text-blue-500 hover:text-blue-400 active:text-blue-200 cursor-pointer select-none ml-1.5 mr-1.5 hover:underline transition-all";

        const parsedContent = data?.content.replaceAll(userRegex, (match, userId, username) => {
            return `<a href="/user/${userId}" class="${linkClass}">@${username}</a>`;
        }).replaceAll(topicRegex, (match, topicId, title) => {
            return `<a href="/topic/${topicId}" class="${linkClass}">${title}</a>`;
        }).replaceAll(canteenRegex, (match, canteenId, name) => {
            return `<a href="/canteen/${canteenId}" class="${linkClass}">${name}</a>`;
        }).replaceAll(complaintRegex, (match, complaintId, title) => {
            return `<a href="/complaint/${complaintId}" class="${linkClass}">${title}</a>`;
        }).replaceAll(itemRegex, (match, itemId, name) => {
            return `<a href="/item/${itemId}" class="${linkClass}">${name}</a>`;
        }).replaceAll("<", "&lt;").replaceAll(">", "&gt;")
            .replaceAll("&lt;a href=\"/user/", `<a href="/user/`)
            .replaceAll("&lt;a href=\"/topic/", `<a href="/topic/`)
            .replaceAll("&lt;a href=\"/canteen/", `<a href="/canteen/`)
            .replaceAll("&lt;a href=\"/complaint/", `<a href="/complaint/`)
            .replaceAll("&lt;a href=\"/item/", `<a href="/item/`)
            .replaceAll("&lt;/a&gt;", `</a>`)
            .replaceAll(`${linkClass}"&gt;`, `${linkClass}">`);

        const sanitizedContent = DOMPurify.sanitize(parsedContent);


        return <div
            className={"border-t border-gray-100 hover:bg-gray-50 transition-background cursor-pointer"}
            onClick={async () => {
                if (data?.isRead) {
                    return;
                }
                await readNotification(data?.notificationId);
                setRefresh(refresh + 1);
            }}
        >
            <div className={"flex flex-col items-start p-5 gap-1.5"}>
                <div className="flex flex-wrap items-center gap-2 text-gray-750 text-medium">
                    {
                        !data?.isRead &&
                        <span className="bg-red-500 rounded-full w-2.5 h-2.5 min-w-2.5 min-h-2.5"></span>
                    }
                    <span className="flex-1">
                    <span dangerouslySetInnerHTML={{__html: sanitizedContent}}/>
                </span>
                </div>
                <div className={"text-gray-500 text-sm flex items-center gap-2"}>
                    <span className={"flex items-center"}>{formatDateTimeFromNow(data?.createdAt)}</span>
                </div>
            </div>
        </div>
    }


    return <>
        <main className={"min-h-[100vh]"}>
            <NavigationBar/>
            <div className={"flex items-center justify-center"}>
                <div className={"w-full lg:w-[768px] bg-white m-5 rounded-md shadow-xl overflow-hidden"}>
                    <h1 className={"p-5 text-center font-bold text-gray-900 text-2xl pb-2"}>通知中心</h1>
                    <div className={"flex items-center gap-3 pr-5 justify-end p-3 text-blue-500 pt-0"}>
                        <div
                            className={"hover:text-blue-400 active:text-blue-200 cursor-pointer select-none"}
                            onClick={() => setFilterMode(filterMode === "unread" ? "list" : "unread")}
                        >
                            {filterMode === "unread" ? "仅看未读" : "全部"}
                        </div>
                        <div
                            className={"hover:text-blue-400 active:text-blue-200 cursor-pointer select-none"}
                            onClick={async () => {
                                await readAllNotification();
                                setRefresh(refresh + 1);
                            }}
                        >
                            全部标为已读
                        </div>
                    </div>
                    <div className={"flex flex-col divide-y min-h-[500px]"}>
                        {data?.length === 0 &&
                            <Empty className={"p-5"} description={"没有通知"}/>
                        }
                        {
                            data?.map((notification, index) => {
                                return <Notification key={index} data={notification}/>
                            })
                        }
                    </div>
                </div>
            </div>
        </main>
    </>
}
