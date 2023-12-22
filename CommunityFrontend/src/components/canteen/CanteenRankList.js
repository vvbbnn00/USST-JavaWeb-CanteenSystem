"use client";
import {ScrollShadow, Skeleton} from "@nextui-org/react";
import Link from "next/link";
import {Rate} from "antd";
import {EnvironmentFilled} from "@ant-design/icons";
import useSWR from "swr";
import {fetchApiWithAuth} from "@/utils/api";

export default function CanteenRankList() {
    const {data: canteenList, error: canteenError, isLoading: isCanteenListLoading} = useSWR(
        ["/api/rest/canteen/list", {
            method: "POST",
            body: JSON.stringify({
                page: 1,
                pageSize: 10,
                orderBy: "compScore",
                asc: false
            })
        }],
        (args) => fetchApiWithAuth(...args)
    )
    return (
        <div className={"bg-white rounded shadow-xl h-fit"}>
            <div className={"font-bold text-lg text-gray-700 p-2.5 pb-0"}>
                最新食堂排名
            </div>
            <div className={"mt-2.5 pb-5 max-h-[500px]"}>
                {isCanteenListLoading && <div className={"p-5"}>
                    <div className="w-full flex flex-col gap-2 pt-2">
                        <Skeleton className="h-5 w-3/5 rounded-lg"/>
                        <Skeleton className="h-3 w-full rounded-lg"/>
                        <Skeleton className="h-3 w-full rounded-lg"/>
                    </div>
                </div>}
                <ScrollShadow className={"max-h-[500px]"}>
                    {
                        canteenList?.list?.map((canteen, index) => {
                            const rank = index + 1;
                            let extraClass = "";
                            switch (rank) {
                                case 1:
                                    extraClass = "text-red-500 font-bold text-2xl";
                                    break;
                                case 2:
                                    extraClass = "text-orange-500 font-bold text-xl";
                                    break;
                                case 3:
                                    extraClass = "text-blue-500 font-bold text-lg";
                                    break;
                                default:
                                    extraClass = "text-gray-500 font-bold";
                                    break;
                            }
                            return <Link href={"/canteen/" + canteen?.canteenId}
                                         key={canteen?.canteenId}>
                                <div
                                    className={"hover:bg-gray-100 pl-5 pr-5 pb-3 pt-3 cursor-pointer transition-background"}>
                                    <div className={"flex items-end gap-2 flex-wrap"}>
                                        <div className={"flex items-end gap-1.5"}>
                                            <span className={extraClass}>#{rank}</span>
                                            <span
                                                className={"text-gray-700 font-bold"}>{canteen?.name}</span>
                                        </div>
                                        <div className={"flex items-center gap-1.5"}>
                                            <Rate disabled defaultValue={canteen?.compScore || 0}
                                                  allowHalf/>
                                            <span
                                                className={"text-gray-500"}>{canteen?.compScore?.toFixed(2)}</span>
                                        </div>
                                    </div>
                                    <div className={"mt-1.5 text-gray-600 text-sm line-clamp-2"}>
                                        {canteen?.introduction}
                                    </div>
                                    <div className={"text-gray-500 text-sm mt-0.5"}>
                                        <EnvironmentFilled/> <span>{canteen?.location}</span>
                                    </div>
                                </div>
                            </Link>
                        })}
                </ScrollShadow>
            </div>
        </div>
    )
}
