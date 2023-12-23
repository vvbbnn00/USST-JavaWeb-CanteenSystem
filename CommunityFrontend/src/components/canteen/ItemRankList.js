"use client";
import {Image, ScrollShadow, Skeleton} from "@nextui-org/react";
import Link from "next/link";
import {Rate} from "antd";
import {EnvironmentFilled, LikeFilled, StarFilled} from "@ant-design/icons";
import useSWR from "swr";
import {fetchApiWithAuth} from "@/utils/api";

export default function FoodRankList() {
    const {data, error, isLoading} = useSWR(
        ["/api/rest/canteen/item/list", {
            method: "POST",
            body: JSON.stringify({
                "pageSize": 10,
                "currentPage": 1,
                "orderBy": "compScore",
                "asc": false
            })
        }],
        (args) => fetchApiWithAuth(...args).then(r => r.list)
    )
    return (
        <div className={"bg-white rounded shadow-xl h-fit"}>
            <div className={"font-bold text-lg text-gray-700 p-2.5 pb-0"}>
                🥘美食推荐
            </div>
            <div className={"mt-2.5 pb-5 max-h-[500px]"}>
                {isLoading && <div className={"pt-3 pl-5 pr-5"}>
                    <div className="w-[150px] flex flex-col gap-2 pt-2">
                        <Skeleton className="h-[150px] rounded-lg"/>
                        <Skeleton className="h-3 w-[100px] rounded-lg"/>
                        <Skeleton className="h-3 w-[50px] rounded-lg"/>
                        <Skeleton className="h-3 w-[120px] rounded-lg"/>
                    </div>
                </div>}
                <ScrollShadow className={"max-h-[500px]"}>
                    <div className={"grid grid-cols-2 mt-3"}>
                        {data?.map((item, index) => {
                            return <Link href={"/item/" + item?.itemId}>
                                <div
                                    className={"flex flex-col items-center hover:bg-gray-100 cursor-pointer transition-background pt-3 pb-3"}
                                    key={index}>
                                    <div className={"flex items-start flex-col w-[150px]"}>
                                        <Image src={item?.image?.x256Url} alt={item?.name} loading={"lazy"}
                                               className={"object-cover w-[150px] h-[150px]"} isZoomed={true}/>
                                        <div
                                            className={"text-left text-gray-800 text-sm mt-1 pl-1 flex items-center gap-1 w-full line-clamp-1 break-all "}>
                                            <text className={"truncate font-bold text-medium"}>{item?.name}</text>
                                            {item?.recommended && <span className={"text-red-500"}><LikeFilled/></span>}
                                        </div>
                                        <div
                                            className={"text-left text-gray-800 text-sm pl-1 flex items-center gap-1 w-full line-clamp-1 break-all "}>
                                            <span className={"text-orange-500"}><StarFilled/></span>
                                            <text
                                                className={"text-gray-500"}>{item?.compScore > 0 ? item?.compScore?.toFixed(2) : "暂无评分"}</text>
                                        </div>
                                        <div>
                                            <text className={"text-red-500 text-medium font-bold"}>
                                                ￥{(item?.promotionPrice || item?.price)?.toFixed(2)}
                                            </text>
                                            <text className={"text-gray-500 text-sm ml-1 line-through"}>
                                                ￥{item?.price?.toFixed(2)}
                                            </text>
                                        </div>
                                    </div>
                                </div>
                            </Link>
                        })}
                    </div>
                </ScrollShadow>
            </div>
        </div>
    )
}
