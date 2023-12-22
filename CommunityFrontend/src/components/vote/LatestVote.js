"use client";
import {Skeleton} from "@nextui-org/react";
import VoteSmallItem from "@/components/vote/VoteSmallItem";
import useSWR from "swr";
import {fetchApi} from "@/utils/api";


export default function LatestVote() {
    const {data: latestVote, isLoading: isLatestVoteLoading} = useSWR(
        ["/api/rest/vote/list", {
            method: "POST",
            body: JSON.stringify({
                page: 1,
                pageSize: 1,
                orderBy: "startTime",
                asc: false
            })
        }],
        (args) => fetchApi(...args)
    )

    return <div className={"bg-white rounded shadow-xl h-fit"}>
        <div className={"font-bold text-lg text-gray-700 p-2.5 pb-0"}>
            最新投票
        </div>
        <div className={"pt-2.5"}>
            {isLatestVoteLoading && <div className={"p-5"}>
                <div className="w-full flex flex-col gap-2 pt-2">
                    <Skeleton className="h-5 w-3/5 rounded-lg"/>
                    <Skeleton className="h-3 w-full rounded-lg"/>
                    <Skeleton className="h-3 w-full rounded-lg"/>
                </div>
            </div>}
            {
                latestVote?.list?.map(vote => {
                    return <VoteSmallItem vote={vote} key={vote?.voteId}/>
                })
            }
        </div>
    </div>
}