"use client"

import NavigationBar from "@/components/NavigationBar";
import {Button, Image, Input, Pagination, Select, SelectItem, Spinner} from "@nextui-org/react";
import {
    CaretUpFilled,
    ClockCircleFilled,
    CloseCircleFilled,
    EnvironmentFilled,
    FilterOutlined,
    SearchOutlined
} from "@ant-design/icons";
import useSWR from "swr";
import {fetchApiWithAuth} from "@/utils/api";
import {useState} from "react";
import {Empty, Rate} from "antd";
import {formatDateTime} from "@/utils/string";
import Link from "next/link";

export default function CanteenPage() {
    const [sort, setSort] = useState("compScore");
    const [asc, setAsc] = useState(false);
    const [page, setPage] = useState(1);
    const [kw, setKw] = useState();

    const {data, error, isLoading} = useSWR(
        [`/api/rest/canteen/list`, {
            method: 'POST',
            body: JSON.stringify({
                kw: kw,
                currentPage: page,
                pageSize: 20,
                orderBy: sort,
                asc: asc,
            })
        }],
        (args) => fetchApiWithAuth(...args));

    return <>
        <NavigationBar/>
        <main className={"w-full"}>
            <div className={"m-auto w-full lg:w-[1024px] flex flex-col gap-5"}>
                <div className={"max-w-[1024px] w-auto bg-white rounded-lg overflow-hidden shadow-xl m-5 mb-0"}>
                    <Image src={"/banner2.webp"} alt={"发现新食堂？"} radius={"none"} loading={"eager"}
                           className={"select-none pointer-events-none"}/>
                    <div className={"flex flex-wrap pb-5 sm:pb-0"}>
                        <div className={"p-5 lg:w-1/3"}>
                            <h1 className={"text-2xl font-bold text-gray-900"}>发现新食堂</h1>
                            <p className={"text-gray-500"}>在这里，你可以发现上理的每一个食堂</p>
                        </div>
                        <div className={"pl-5 pr-5 flex-grow flex items-center"}>
                            <form className={"w-full"}>
                                <Input
                                    placeholder={"搜索食堂"}
                                    name={"search"}
                                    startContent={<div className={"flex items-center text-lg text-gray-500"}>
                                        <SearchOutlined/>
                                    </div>}
                                />
                            </form>
                        </div>
                    </div>
                </div>
                <div className={"max-w-[1024px] w-auto bg-white rounded-lg overflow-hidden shadow-xl m-5 mt-0"}>
                    <div className={"flex flex-row items-center border-b justify-between flex-wrap"}>
                        <div className={"flex flex-row items-center gap-2 p-5 text-gray-500"}>
                            <span>🔍 共找到</span>
                            {isLoading ?
                                <span className={"bg-gray-200 rounded-md animate-pulse w-[20px]"}>&nbsp;</span> :
                                <span className={"text-gray-900 font-bold"}>{data?.total}</span>
                            }
                            <span>个食堂。</span>
                        </div>
                        <div className={"flex flex-row items-center gap-2 p-5 border-b flex-wrap"}>
                            <div className={"flex items-center gap-2 flex-wrap"}>
                                <Select
                                    label="排序条件"
                                    className={"w-[150px] text-gray-500"}
                                    size={"sm"}
                                    defaultSelectedKeys={[sort]}
                                    onChange={e => {
                                        setSort(e.target.value);
                                    }}
                                    classNames={{
                                        value: "text-gray-500"
                                    }}
                                >
                                    <SelectItem key={"compScore"} value={"compScore"}>
                                        🍜 综合评分
                                    </SelectItem>
                                    <SelectItem key={"updatedAt"} value={"updatedAt"}>
                                        📅 创建时间
                                    </SelectItem>
                                </Select>
                                <Button
                                    size={"lg"}
                                    auto
                                    onClick={() => setAsc(!asc)}
                                    className={"text-gray-500"}
                                    radius={"sm"}
                                    variant={"flat"}
                                    endContent={<div className={
                                        "transition-all text-gray-500 flex items-center" + (asc ? " rotate-360" : " rotate-180")
                                    }>
                                        <CaretUpFilled/>
                                    </div>}
                                >
                                    {asc ? "升序" : "降序"}
                                </Button>
                            </div>
                        </div>
                    </div>
                    {isLoading && <div className={"w-full p-10 h-[200px] flex items-center justify-center"}>
                        <Spinner color={"default"}/>
                    </div>}
                    {error && <div className={"w-full p-10 h-[200px] flex items-center justify-center"}>
                        <div className={"text-gray-500 text-center"}>
                            <div className={"text-2xl font-bold"}>🤔️</div>
                            <div>出错了，请稍后再试</div>
                            <div className={"text-sm text-gray-400"}>错误信息：{error?.message}</div>
                        </div>
                    </div>}
                    {!isLoading && !error && data?.list?.length === 0 && <div className={"w-full p-10 h-[200px] flex items-center justify-center"}>
                        <Empty description={"没有找到相关食堂"} className={"text-gray-500"}/>
                    </div>}
                    <div className={"w-full"}>
                        {data?.list?.map((canteen, index) => {
                            return <Link
                                key={index}
                                href={`/canteen/${canteen?.canteenId}`}
                            >
                                <div
                                    className={"hover:bg-gray-100 pl-5 pr-5 pb-3 pt-3 cursor-pointer transition-background"}>
                                    <div className={"flex items-end gap-2 flex-wrap"}>
                                        <div className={"flex items-end gap-1.5"}>
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
                                        食堂简介：{canteen?.introduction}
                                    </div>
                                    <div className={"text-gray-500 text-sm mt-0.5"}>
                                        <EnvironmentFilled/> <span>{canteen?.location}</span>
                                    </div>
                                    <div className={"text-gray-500 text-sm mt-0.5"}>
                                        <ClockCircleFilled/> <span>{formatDateTime(canteen?.updatedAt)}</span>
                                    </div>
                                </div>
                            </Link>
                        })}
                    </div>
                    {!isLoading && <div className={"flex flex-row items-center justify-center gap-2 p-5"}>
                        <Pagination
                            showControls
                            total={Math.ceil(data?.total / 20) || 1}
                            initialPage={page}
                            page={page}
                            onChange={setPage}
                        />
                    </div>}
                </div>
            </div>
        </main>
    </>
}
