"use client";

import {Chip} from "@nextui-org/react";
import {FieldTimeOutlined} from "@ant-design/icons";
import {formatDateTime} from "@/utils/string";
import Link from "next/link";

export default function VoteSmallItem({vote}) {

    return <Link href={"/vote/" + vote?.voteId}>
        <div className={"flex flex-col p-2.5 hover:bg-gray-100 cursor-pointer transition-background"}>
            <div className={"flex flex-row items-center"}>
                {vote?.isStarted ?
                    <Chip color={"success"} variant={"flat"}>进行中</Chip> :
                    <Chip color={"warning"} variant={"flat"}>未开始</Chip>
                }
                <span className={"text-gray-600 pl-1.5 font-bold"}>{vote?.voteName}</span>
            </div>
            <div className={"text-gray-500 text-sm line-clamp-2 mt-1.5 mb-1.5"}>
                投票简介：{vote?.voteIntro}
            </div>
            <div className={"text-gray-500 text-sm"}>
                <FieldTimeOutlined/>
                <span>{formatDateTime(vote?.startTime)} </span>
                <span className={"ml-0.5 mr-0.5"}>~</span>
                <span>{formatDateTime(vote?.endTime)}</span>
            </div>
        </div>
    </Link>
}
