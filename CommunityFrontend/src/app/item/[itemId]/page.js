"use client"
import NavigationBar from "@/components/NavigationBar";
import BackButton from "@/components/common/BackButton";
import {Empty, Image} from "antd";
import {fetchApiWithAuth} from "@/utils/api";
import useSWR from "swr";
import {Chip, Skeleton} from "@nextui-org/react";
import {FALLBACK_IMG} from "@/utils/fallback";
import {EnvironmentOutlined, ProfileOutlined, StarFilled} from "@ant-design/icons";
import CommentPanel from "@/app/item/[itemId]/CommentPanel";

export default function ItemDetailPage({params}) {
    const {data, error, isLoading} = useSWR(
        `/api/rest/canteen/item/${params?.itemId}`,
        (...args) => fetchApiWithAuth(...args).then(r => r.data)
    );

    return <>
        <NavigationBar/>
        <main className={"flex justify-center"}>
            <div className={"w-full lg:w-[768px] bg-white m-5 rounded-md shadow-xl overflow-hidden pb-10"}>
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
                {!error && <div className={"flex flex-col"}>
                    <div className={"flex flex-row items-center"}>
                        <BackButton/>
                        <div className={"flex-grow"}>
                            {isLoading && <Skeleton className={"h-10 w-3/5 rounded-lg"}/>}
                            {!isLoading && <h1 className={"text-lg font-bold truncate line-clamp-1 w-fit"}>
                                🍲 {data?.name}
                            </h1>}
                        </div>
                    </div>
                    <div className={"flex flex-row flex-wrap border-b pb-5"}>
                        <div className={"overflow-hidden pl-10 pr-10"}>
                            <Image src={data?.image?.x384Url}
                                   alt={data?.name}
                                   height={"256px"}
                                   width={"256px"}
                                   className={"border-none object-cover rounded-lg bg-gray-200" + (isLoading ? " animate-pulse" : "")}
                                   preview={{
                                       src: data?.image?.originalUrl,
                                   }}
                                   fallback={FALLBACK_IMG}
                            />
                        </div>
                        <div className={"flex-grow p-5 md:p-0"}>
                            <div className={"flex items-center gap-2"}>
                                {data?.recommended && <span>
                                    <Chip color={"danger"} variant={"flat"}>推荐</Chip>
                                </span>}
                                {isLoading && <Skeleton className={"h-8 w-3/5 rounded-lg"}/>}
                                <span className={"font-bold text-2xl"}>
                                    {data?.name}
                                </span>
                            </div>
                            <div className={"flex items-center gap-2 mt-2"}>
                                {isLoading && <div className={"w-full flex flex-col gap-2"}>
                                    <Skeleton className={"h-5 w-4/5 rounded-lg"}/>
                                    <Skeleton className={"h-5 w-4/5 rounded-lg"}/>
                                </div>}
                                <span className={"text-gray-800 text-medium"}>
                                    {data?.introduction}
                                </span>
                            </div>
                            <div className={"flex items-start gap-2 mt-2 flex-col"}>
                                <div className={"flex items-center gap-1"}>
                                    <span className={"text-orange-500 text-medium"}>
                                        <StarFilled/>
                                    </span>
                                    <span className={"text-gray-500 text-medium"}>
                                        {data?.compScore > 0 ?
                                            <span
                                                className={"text-orange-500 font-bold"}>{data?.compScore?.toFixed(2)}</span> :
                                            <span>暂无评分</span>
                                        }
                                    </span>
                                </div>
                                <div className={"flex items-center gap-2 flex-wrap w-full"}>
                                    {isLoading && <Skeleton className={"h-5 w-3/5 rounded-lg"}/>}
                                    {!isLoading && <>
                                        <div className={"flex items-center gap-1"}>
                                        <span className={"text-gray-500 text-medium"}>
                                            <EnvironmentOutlined/>
                                        </span>
                                            <span className={"text-gray-500 text-medium"}>
                                            {data?.canteen?.name} ({data?.canteen?.location})
                                        </span>
                                        </div>
                                        <div className={"flex items-center gap-1"}>
                                        <span className={"text-gray-500 text-medium"}>
                                            <ProfileOutlined/>
                                        </span>
                                            <span className={"text-gray-500 text-medium"}>
                                            {data?.cuisine?.name}
                                        </span>
                                        </div>
                                    </>}
                                </div>
                            </div>
                            <div className={"flex items-end gap-2 mt-2"}>
                                <span className={"text-red-500 text-2xl font-bold flex items-center"}>
                                    ￥{(data?.promotionPrice || data?.price)?.toFixed(2)}
                                    {isLoading && <Skeleton className={"h-8 w-[80px] rounded-lg"}/>}
                                </span>
                                <span className={"text-gray-500 text-base ml-1 line-through flex items-center"}>
                                    ￥{data?.price?.toFixed(2)}
                                    {isLoading && <Skeleton className={"h-6 w-[30px] rounded-lg"}/>}
                                </span>
                            </div>
                        </div>
                    </div>
                    {data && <div>
                        <div className={"pl-5 text-gray-600 font-bold pt-5"}>
                            🤔 菜品怎么样？快来评价一下吧！
                        </div>
                        <CommentPanel itemId={params?.itemId} type={"canteen/item"}/>
                    </div>}
                </div>}
            </div>
        </main>
    </>
}
