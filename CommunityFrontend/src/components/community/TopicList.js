"use client";

import TopicItem from "@/components/community/TopicItem";
import {Button} from "@nextui-org/react";
import useSWRInfinite from "swr/infinite";
import {fetchApi} from "@/utils/api";
import {useEffect} from "react";

const getKey = (pageIndex, previousPageData, sort) => {
    pageIndex = pageIndex + 1;
    if (previousPageData && previousPageData?.list?.length < 5) return null;

    return ["/api/rest/topic/list", {
        method: "POST",
        body: JSON.stringify({
            currentPage: pageIndex,
            pageSize: 5,
            orderBy: sort,
            asc: false
        })
    }]
}


export default function TopicList({sort}) {
    const {
        data: topicData, error: topicError,
        isLoading: isTopicLoading, size: topicSize,
        setSize: setTopicSize
    } = useSWRInfinite(
        (pageIndex, previousPageData) => getKey(pageIndex, previousPageData, sort),
        (args) => fetchApi(...args),
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