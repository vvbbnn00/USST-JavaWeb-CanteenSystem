"use client"
import NavigationBar from "@/components/NavigationBar";
import BackButton from "@/components/common/BackButton";
import {Empty, Rate} from "antd";
import useSWR from "swr";
import {fetchApiWithAuth} from "@/utils/api";
import {Button, ScrollShadow, Skeleton} from "@nextui-org/react";
import CommentPanel from "@/app/canteen/[canteenId]/CommentPanel";
import AnnouncementPanel from "@/app/canteen/[canteenId]/AnnouncementPanel";
import {WarningOutlined} from "@ant-design/icons";
import Link from "next/link";

export default function CanteenDetailPage({params}) {
    const {data, error, isLoading} = useSWR(
        `/api/rest/canteen/${params?.canteenId}`,
        (...args) => fetchApiWithAuth(...args).then(r => r.data)
    );

    return <>
        <NavigationBar/>
        <main className={"flex justify-center flex-col items-center"}>
            <div className={"w-full lg:w-[1200px] flex flex-wrap items-start"}>
                <div className={"bg-white m-5 rounded-md shadow-xl overflow-hidden pb-0 w-full"}>
                    {error && <div>
                        <div className={"flex flex-row items-center"}>
                            <BackButton/>
                            <div className={"flex-grow"}>
                                <h1 className={"text-lg font-bold truncate line-clamp-1 w-fit"}>
                                    出错了
                                </h1>
                            </div>
                        </div>
                        <Empty description={error?.message || "加载失败"} className={"mt-5 pb-10"}/>
                    </div>}
                    {!error && <div className={"flex flex-col"}>
                        <div className={"flex flex-row items-center"}>
                            <BackButton/>
                            <div className={"flex-grow"}>
                                {isLoading && <Skeleton className={"h-6 w-3/5 rounded-lg"}/>}
                                {!isLoading && <h1 className={"text-lg font-bold truncate line-clamp-1 w-fit"}>
                                    {data?.name}
                                </h1>}
                            </div>
                            <div className={"mr-5"}>
                                <Button
                                    startContent={<WarningOutlined />}
                                    color={"danger"}
                                    variant={"flat"}
                                    as={Link}
                                    href={`/complaint/new?canteenId=${params?.canteenId}`}
                                >
                                    我要投诉
                                </Button>
                            </div>
                        </div>
                    </div>}
                </div>
                <div className={"w-full lg:w-[768px] m-5 mt-0 mr-5 lg:mr-2.5"}>
                    <div className={"bg-white rounded-md shadow-xl flex-1 p-5 flex-col"}>
                        <div className={"flex flex-row items-start"}>
                            <div className={"w-[100px] font-bold text-lg"}>食堂简介</div>
                            <div className={"flex-1 flex-grow"}>
                                {isLoading && <div className={"flex flex-col gap-2 w-full"}>
                                    <Skeleton className={"h-4 w-4/5 rounded-lg"}/>
                                    <Skeleton className={"h-4 w-4/5 rounded-lg"}/>
                                    <Skeleton className={"h-4 w-4/5 rounded-lg"}/>
                                    <Skeleton className={"h-4 w-4/5 rounded-lg"}/>
                                    <Skeleton className={"h-4 w-3/5 rounded-lg"}/>
                                </div>}
                                <ScrollShadow className={"max-h-[350px]"}>
                                <pre className={"font-sans break-all text-wrap leading-loose"}>
                                    {data?.introduction}
                                </pre>
                                </ScrollShadow>
                            </div>
                        </div>
                        <div className={"flex flex-row items-center mt-2"}>
                            <div className={"w-[100px] font-bold text-lg"}>食堂地址</div>
                            <div className={"flex-1 flex-grow"}>
                                <ScrollShadow className={"max-h-[350px]"}>
                                    {isLoading && <Skeleton className={"h-4 w-2/5 rounded-lg"}/>}
                                    <pre className={"font-sans break-all text-wrap"}>
                                    {data?.location}
                                </pre>
                                </ScrollShadow>
                            </div>
                        </div>
                        <div className={"flex flex-row items-center mt-2"}>
                            <div className={"w-[100px] font-bold text-lg"}>综合评分</div>
                            {isLoading && <Skeleton className={"h-4 w-1/5 rounded-lg"}/>}
                            <div className={"flex items-center gap-1.5"}>
                                {!isLoading && <><Rate disabled defaultValue={data?.compScore || 0}
                                                       allowHalf/>
                                    <span
                                        className={"text-gray-700 font-bold"}>{data?.compScore?.toFixed(2)}</span>
                                </>}
                            </div>
                        </div>
                        {!isLoading &&
                            <div className={"mt-5 border-t "}>
                                <div className={"pt-5 font-bold text-lg"}>🤔 这个食堂怎么样？看看评论吧！</div>
                                <CommentPanel canteenId={data?.canteenId}/>
                            </div>
                        }
                    </div>
                </div>
                {!isLoading && <AnnouncementPanel canteenId={data?.canteenId}/>}
            </div>

        </main>
    </>
}
