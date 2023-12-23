"use client";

import TopicItem from "@/components/community/TopicItem";
import {Button, Skeleton} from "@nextui-org/react";
import useSWRInfinite from "swr/infinite";
import {fetchApi, fetchApiWithAuth} from "@/utils/api";
import {useEffect} from "react";

const getKey = (pageIndex, previousPageData, sort, filterUserId) => {
    pageIndex = pageIndex + 1;
    if (previousPageData && previousPageData?.list?.length < 5) return null;

    const requestData = {
        currentPage: pageIndex,
        pageSize: 5,
        orderBy: sort,
        asc: false
    };

    if (filterUserId) {
        requestData.userId = filterUserId;
    }


    return ["/api/rest/topic/list", {
        method: "POST",
        body: JSON.stringify(requestData)
    }]
}


export default function TopicList({sort = "createdAt", filterUserId = null}) {
    const {
        data: topicData, error: topicError,
        isLoading: isTopicLoading, size: topicSize,
        setSize: setTopicSize
    } = useSWRInfinite(
        (pageIndex, previousPageData) => getKey(pageIndex, previousPageData, sort, filterUserId),
        (args) => fetchApiWithAuth(...args),
        {
            revalidateAll: true
        }
    )

    useEffect(() => {
        console.log("sort change", sort);
        setTopicSize(1).then(r => console.log(r));
    }, [sort])

    const onLikeChange = (topicPage, topicId, isLiked) => {
        console.log("topicPage", topicPage);
        setTopicSize(topicSize).then(r => console.log(r));
    }

    return <>
        {isTopicLoading && <div className={"flex pt-5 w-full h-[150px] pl-5"}>
            <div className={"flex w-[56px]"}>
                <Skeleton className="w-[56px] h-[56px] rounded-full"/>
            </div>
            <div className={"flex w-full flex-col gap-3 pl-5"}>
                <Skeleton className="h-5 w-1/2 rounded-lg"/>
                <Skeleton className="h-4 w-3/4 rounded-lg"/>
                <Skeleton className="h-4 w-3/4 rounded-lg"/>
                <Skeleton className="h-3 w-[120px] rounded-lg"/>
            </div>
        </div>}
        {
            topicData?.map((topicResp, pIndex) => {
                return topicResp?.list?.map((topic, index) => {
                    return <div className={"border-b-2 border-gray-100"} key={topic?.topicId}>
                        <TopicItem topic={topic} topicPage={pIndex} onLikeChange={onLikeChange}/>
                    </div>
                })
            })
        }
        {
            topicData?.length > 0 &&
            <div className={"flex justify-center pt-5"}>
                <Button
                    onClick={() => {
                        setTopicSize(topicSize + 1).then(r => console.log(r));
                    }}
                    loading={isTopicLoading}
                    disabled={isTopicLoading}
                    auto>
                    加载更多
                </Button>
            </div>
        }
    </>
}
