"use client";

import NavigationBar from "@/components/NavigationBar";
import {Button, Chip, Pagination, Select, SelectItem, Spinner} from "@nextui-org/react";
import {
    EditFilled,
    FilterFilled,
} from "@ant-design/icons";
import {useEffect, useState} from "react";
import {getMe} from "@/utils/auth";
import useSWR from "swr";
import {fetchApiWithAuth} from "@/utils/api";
import {formatDateTime, formatDateTimeFromNow} from "@/utils/string";
import Link from "next/link";
import {Empty} from "antd";
import CanteenSelect from "@/app/complaint/CanteenSelect";

export default function ComplaintPage() {
    const [page, setPage] = useState(1);
    const [userId, setUserId] = useState();
    const [me, setMe] = useState();
    const [status, setStatus] = useState();
    const [canteenId, setCanteenId] = useState();
    const {data, error, isLoading} = useSWR(
        [`/api/rest/canteen/complaint/list`, {
            method: 'POST',
            body: JSON.stringify({
                currentPage: page,
                pageSize: 5,
                createdBy: userId || undefined,
                status: status || undefined,
                canteenId: canteenId || undefined,
                orderBy: "updatedAt",
                asc: false,
            })
        }],
        (args) => fetchApiWithAuth(...args)
    );


    useEffect(() => {
        getMe().then(r => {
            setMe(r);
        });
    }, []);


    return <>
        <NavigationBar/>
        <main className={"w-full"}>
            <div className={"m-auto w-full lg:w-[1024px] flex flex-col gap-5"}>
                <div className={"max-w-[1024px] w-auto bg-white rounded-lg overflow-hidden shadow-xl m-5 mb-0"}>
                    <div className={"flex flex-wrap pb-5 sm:pb-0"}>
                        <div className={"p-5 lg:w-1/3"}>
                            <h1 className={"text-2xl font-bold text-gray-900"}>投诉中心</h1>
                            <p className={"text-gray-500"}>所有的用户投诉将在此公开展示。</p>
                        </div>
                    </div>
                </div>

                <div className={"max-w-[1024px] w-auto bg-white rounded-lg overflow-hidden shadow-xl m-5 mb-5 mt-0"}>
                    <div className={"flex items-center border-b"}>
                        <div className={"flex flex-grow"}>
                            <Button
                                color={"danger"}
                                size={"md"}
                                className={"m-5"}
                                variant={"flat"}
                                startContent={<EditFilled />}
                                as={Link}
                                href={"/complaint/new"}
                            >
                                我要投诉
                            </Button>
                        </div>
                        <div className={"flex justify-end items-center"}>
                            <CanteenSelect onChange={setCanteenId}/>
                            <Select
                                label="投诉状态"
                                className={"w-[150px] text-gray-500 ml-5"}
                                size={"sm"}
                                defaultSelectedKeys={[status || "all"]}
                                onChange={e => {
                                    setStatus(e.target.value === "all" ? undefined : e.target.value);
                                }}
                                classNames={{
                                    value: "text-gray-500"
                                }}
                                selectionMode={"single"}
                                disallowEmptySelection={true}
                            >
                                <SelectItem key={"all"} value={undefined}>
                                    全部
                                </SelectItem>
                                <SelectItem key={"pending"} value={"pending"} textValue={"待回复"}>
                                    <div className={"flex items-center gap-2 text-red-700"}>
                                        <div className={"w-3 h-3 rounded-full bg-red-200"}/>
                                        待回复
                                    </div>
                                </SelectItem>
                                <SelectItem key={"processing"} value={"processing"} textValue={"处理中"}>
                                    <div className={"flex items-center gap-2 text-orange-700"}>
                                        <div className={"w-3 h-3 rounded-full bg-orange-200"}/>
                                        处理中
                                    </div>
                                </SelectItem>
                                <SelectItem key={"replied"} value={"replied"} textValue={"已回复"}>
                                    <div className={"flex items-center gap-2 text-green-700"}>
                                        <div className={"w-3 h-3 rounded-full bg-green-200"}/>
                                        已回复
                                    </div>
                                </SelectItem>
                                <SelectItem key={"finished"} value={"finished"} textValue={"已完成"}>
                                    <div className={"flex items-center gap-2 text-gray-700"}>
                                        <div className={"w-3 h-3 rounded-full bg-gray-200"}/>
                                        已完成
                                    </div>
                                </SelectItem>
                            </Select>
                            <Button
                                color={"default"}
                                size={"md"}
                                className={"m-5"}
                                variant={"flat"}
                                onClick={() => {
                                    setPage(1);
                                    if (userId) {
                                        setUserId(undefined);
                                    } else {
                                        setUserId(me?.userId);
                                    }
                                }}
                                startContent={<FilterFilled/>}
                            >
                                {userId ? "我发起的" : "所有投诉"}
                            </Button>
                        </div>
                    </div>
                    {isLoading && <div className={"flex flex-col gap-5 p-5"}>
                        <Spinner color={"default"}/>
                    </div>}
                    {!isLoading && error && <div className={"flex flex-col gap-5 p-5 items-center"}>
                        <div className={"flex flex-col items-center gap-2"}>
                            <span className={"text-gray-500 font-bold font-xl"}>出错了</span>
                            <span className={"text-gray-500 line-clamp-1"}>{error?.message}</span>
                        </div>
                    </div>}
                    {!isLoading && !error && data?.total === 0 &&
                        <div className={"flex flex-col gap-5 p-5 items-center"}>
                            <Empty description={"没有找到相关投诉"} className={"text-gray-500"}/>
                        </div>}
                    {!isLoading && <div className={"flex flex-col"}>
                        {data?.list?.map((item, index) => {
                            return <Link href={`/complaint/${item?.complaintId}`} key={item?.complaintId}>
                                <div key={item?.complaintId}
                                     className={"flex flex-col gap-0 border-b p-5 hover:bg-gray-100 cursor-pointer transition-background"}>
                                    <div className={"flex flex-row items-center gap-2 mb-2"}>
                                        <div className={"flex flex-row items-center gap-2"}>
                                            {item?.status === 'pending' &&
                                                <Chip color={"danger"} variant={"flat"}>待回复</Chip>}
                                            {item?.status === 'processing' &&
                                                <Chip color={"warning"} variant={"flat"}>处理中</Chip>}
                                            {item?.status === 'replied' &&
                                                <Chip color={"success"} variant={"flat"}>已回复</Chip>}
                                            {item?.status === 'finished' &&
                                                <Chip color={"default"} variant={"flat"}>已关闭</Chip>}
                                        </div>
                                        <span className={"text-gray-750 font-bold"}>{item?.title}</span>
                                        <span className={"text-gray-500"}>#{item?.complaintId}</span>
                                        {item?.createdBy === me?.userId &&
                                            <Chip color={"default"} variant={"flat"} radius={"none"}
                                                  size={"sm"}>我发起的</Chip>}
                                    </div>
                                    {item?.status !== 'finished' &&
                                        <div className={"flex flex-row items-center gap-2 text-sm"}>
                                            <span className={"text-gray-500 font-bold"}>最近更新</span>
                                            <span
                                                className={"text-gray-500 line-clamp-1 font-bold"}>
                                                {formatDateTimeFromNow(item?.updatedAt)}
                                            </span>
                                        </div>}
                                    <div className={"flex flex-row items-center gap-2 text-sm"}>
                                        <span className={"text-gray-500"}>创建时间</span>
                                        <span
                                            className={"text-gray-500 line-clamp-1"}>{formatDateTime(item?.createdAt)}</span>
                                    </div>
                                    <div className={"flex flex-row items-center gap-2 text-sm"}>
                                        <span className={"text-gray-500"}>投诉内容</span>
                                        <span className={"text-gray-500 line-clamp-1"}>{item?.content}</span>
                                    </div>
                                </div>
                            </Link>
                        })}
                    </div>
                    }
                    {!isLoading && <div className={"flex flex-row items-center justify-center gap-2 p-5"}>
                        <Pagination
                            showControls
                            total={Math.ceil(data?.total / 5) || 1}
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
