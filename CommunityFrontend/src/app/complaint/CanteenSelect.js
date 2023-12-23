"use client"
import React from "react";
import {Select, SelectItem} from "@nextui-org/react";
import {fetchApiWithAuth} from "@/utils/api";
import useSWR from "swr";

export default function CanteenSelect({onChange, value=undefined, className="w-[150px]"}) {
    const [isOpen, setIsOpen] = React.useState(false);
    const {data, isLoading, error} = useSWR(
        [`/api/rest/canteen/list`, {
            method: 'POST',
            body: JSON.stringify({
                kw: null,
                currentPage: 1,
                pageSize: 100,
                orderBy: "compScore",
                asc: false,
            })
        }],
        (args) => fetchApiWithAuth(...args).then(r => r.list)
    );

    return (
        <Select
            className={className}
            isLoading={isLoading}
            label="投诉食堂"
            selectionMode="single"
            size={"sm"}
            onOpenChange={setIsOpen}
            open={isOpen}
            defaultSelectedKeys={value ? [value] : ['all']}
            onChange={(e) => {
                if (e.target.value === "all") {
                    onChange(undefined);
                    return;
                }
                onChange(e.target.value);
            }}
            disallowEmptySelection={true}
        >
            <SelectItem key={"all"} value={undefined}>请选择</SelectItem>
            {data?.map((item) => (
                <SelectItem key={item?.canteenId} value={item?.canteenId}>
                    {item?.name}
                </SelectItem>
            ))}
        </Select>
    );
}
