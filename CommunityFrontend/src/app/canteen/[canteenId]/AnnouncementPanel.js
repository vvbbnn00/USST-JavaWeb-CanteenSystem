"use client"
import useSWR from "swr";
import {fetchApiWithAuth} from "@/utils/api";
import {formatDateTimeFromNow} from "@/utils/string";
import {ClockCircleFilled} from "@ant-design/icons";
import {Empty, Modal} from "antd";
import {useState} from "react";
import {ScrollShadow, Skeleton} from "@nextui-org/react";

export default function AnnouncementPanel({canteenId}) {
    const {data, isLoading, error} = useSWR(
        `/api/rest/canteen/${canteenId}/announcement/list`,
        (...args) => fetchApiWithAuth(...args)
            .then(r => r.list)
            .then(r => r.sort((a, b) => b?.updatedAt - a?.updatedAt))
    )
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");

    return <div
        className={"flex-grow bg-white rounded-md shadow-xl flex-1 flex-col mt-0 ml-5 lg:ml-2.5 mr-5 pb-5"}>
        <Modal title={title} open={isModalOpen} closeIcon={null} footer={null} maskClosable={true} onCancel={() => {
            setIsModalOpen(false);
        }}>
            <pre className={"font-sans break-all text-wrap leading-loose"}>
                {content}
            </pre>
        </Modal>
        <div className={"font-bold text-lg p-5 pb-5"}>
            食堂公告
        </div>
        {isLoading && <div className={"flex flex-col gap-2 w-full pl-5 pr-5"}>
            <Skeleton className={"h-4 w-2/5 rounded-lg animate-pulse"}/>
            <Skeleton className={"h-4 w-4/5 rounded-lg animate-pulse"}/>
        </div>}
        {error && <div className={"flex flex-col gap-2 w-full pl-5 pr-5"}>
            <div className={"text-gray-500"}>加载失败</div>
        </div>}
        {!isLoading && !error && data?.length === 0 && <div className={"flex flex-col gap-2 w-full pl-5 pr-5 pb-5"}>
            <Empty description={"暂无公告"}/>
        </div>}
        <ScrollShadow className={"max-h-[350px]"}>
            {data?.map((item, index) => {
                return <div key={index}
                            className={"hover:bg-gray-100 transition-background cursor-pointer pl-5 pr-5 pt-2 pb-2"}
                            onClick={() => {
                                setTitle(item?.title);
                                setContent(item?.content);
                                setIsModalOpen(true);
                            }}
                >
                    <div className={"flex flex-row items-center gap-2"}>
                        <div className={"flex-grow"}>
                            <span className={"font-bold"}>{item?.title}</span>
                        </div>
                        <div className={"text-gray-500 line-clamp-1 flex items-center gap-1"}>
                            <ClockCircleFilled/>
                            {formatDateTimeFromNow(item?.updatedAt)}
                        </div>
                    </div>
                    <div className={"mt-1 line-clamp-1 text-gray-500"}>
                        {item?.content?.slice(0, 100)}
                    </div>
                </div>
            })}
        </ScrollShadow>
    </div>
}
