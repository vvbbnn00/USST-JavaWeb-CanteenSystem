"use client";
import NavigationBar from "@/components/NavigationBar";
import {Button, Image, Input, Pagination, Select, SelectItem, Spinner} from "@nextui-org/react";
import {
    CaretUpFilled, CloseCircleFilled,
    FilterOutlined,
    LikeFilled,
    SearchOutlined, StarFilled,
} from "@ant-design/icons";
import {useEffect, useState} from "react";
import useSWR from "swr";
import {fetchApiWithAuth} from "@/utils/api";
import {Cascader, Empty} from "antd";
import Link from "next/link";

export default function ItemPage({params}) {
    const [filterCanteen, setFilterCanteen] = useState();
    const [filterCuisine, setFilterCuisine] = useState();
    const [sort, setSort] = useState("compScore");
    const [asc, setAsc] = useState(false);
    const [page, setPage] = useState(1);
    const [kw, setKw] = useState();
    const [canteenOptions, setCanteenOptions] = useState([]);
    const [filterText, setFilterText] = useState("所有美食");

    const {data, error, isLoading} = useSWR(
        [`/api/rest/canteen/item/list`, {
            method: "POST",
            body: JSON.stringify({
                kw: kw,
                currentPage: page,
                pageSize: 20,
                orderBy: sort,
                asc: asc,
                canteenId: filterCanteen,
                cuisineId: filterCuisine
            })
        }],
        (args) => fetchApiWithAuth(...args)
    );


    useEffect(() => {
        fetchApiWithAuth("/api/rest/canteen/list", {
            method: "POST",
            body: JSON.stringify({
                currentPage: 1,
                pageSize: 100
            })
        }).then(r => {
            setCanteenOptions(r?.list?.map(item => {
                return {
                    value: item?.canteenId,
                    label: item?.name,
                    isLeaf: false
                }
            }));
        });
    }, []);

    const loadCuisineData = async (selectedOptions) => {
        const targetOption = selectedOptions[selectedOptions.length - 1];

        targetOption.loading = true;
        const {list: cuisineData} = await fetchApiWithAuth(
            `/api/rest/canteen/${targetOption?.value}/cuisine/list`, {
                method: "POST",
                body: JSON.stringify({
                    currentPage: 1,
                    pageSize: 100,
                })
            }
        );
        targetOption.loading = false;

        targetOption.children = cuisineData?.map(item => {
            return {
                value: item?.cuisineId,
                label: item?.name,
                isLeaf: true
            }
        });
        setCanteenOptions([...canteenOptions]);
    };

    const onFilterChanged = (_, selectedOptions) => {
        if (selectedOptions?.length === 0) {
            setFilterCanteen(null);
            setFilterCuisine(null);
            setFilterText("所有美食")
        } else if (selectedOptions?.length === 1) {
            setFilterCanteen(selectedOptions[0]?.value);
            setFilterCuisine(null);
            setFilterText(selectedOptions[0]?.label);
        } else if (selectedOptions?.length === 2) {
            setFilterCanteen(selectedOptions[0]?.value);
            setFilterCuisine(selectedOptions[1]?.value);
            setFilterText(selectedOptions[0]?.label + " / " + selectedOptions[1]?.label);
        }
    }

    const onSearch = (formData) => {
        let kw = formData?.get("search");
        kw = kw?.trim();
        if (kw?.length === 0) {
            kw = null;
        }
        setKw(kw);
    }

    return <>
        <NavigationBar/>
        <main className={"w-full"}>
            <div className={"m-auto w-full lg:w-[1024px] flex flex-col gap-5"}>
                <div className={"max-w-[1024px] w-auto bg-white rounded-lg overflow-hidden shadow-xl m-5 mb-0"}>
                    <Image src={"/banner.webp"} alt={"今天想吃什么？"} radius={"none"} loading={"eager"}
                           className={"select-none pointer-events-none"}/>
                    <div className={"flex flex-wrap pb-5 sm:pb-0"}>
                        <div className={"p-5 lg:w-1/3"}>
                            <h1 className={"text-2xl font-bold text-gray-900"}>今天想吃什么？</h1>
                            <p className={"text-gray-500"}>在搜索框中搜索美食，查看相关评价。</p>
                        </div>
                        <div className={"pl-5 pr-5 flex-grow flex items-center"}>
                            <form action={onSearch} className={"w-full"}>
                                <Input
                                    placeholder={"在此探索美食"}
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
                            <span>份美食。</span>
                        </div>
                        <div className={"flex flex-row items-center gap-2 p-5 border-b flex-wrap"}>
                            <div className={"flex items-center gap-2 flex-wrap"}>
                                <Cascader
                                    options={canteenOptions}
                                    onChange={onFilterChanged}
                                    loadData={loadCuisineData}
                                    changeOnSelect
                                >
                                    <div
                                        className={"relative text-sm text-gray-500 w-[160px] overflow-hidden items-center flex break-all bg-gray-100 pt-3.5 pb-3.5 rounded-md pl-2 pr-2 hover:bg-gray-200 transition-background cursor-pointer"}>
                                        <FilterOutlined/>
                                        <span className={"pl-1 truncate"}>{filterText}</span>
                                        {
                                            filterCanteen &&
                                            <span
                                                className={"ml-2 absolute right-2"}
                                                onClick={(e) => {
                                                    e.stopPropagation();
                                                    onFilterChanged(null, [])
                                                }}
                                            >
                                                <CloseCircleFilled/>
                                            </span>
                                        }
                                    </div>
                                </Cascader>
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
                                    selectionMode={"single"}
                                    disallowEmptySelection={true}
                                >
                                    <SelectItem key={"compScore"} value={"compScore"}>
                                        🍜 综合评分
                                    </SelectItem>
                                    <SelectItem key={"promotionPrice"} value={"promotionPrice"}>
                                        💰 价格
                                    </SelectItem>
                                    <SelectItem key={"updatedAt"} value={"updatedAt"}>
                                        📅 上架时间
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
                        <Empty description={"没有找到相关美食"} className={"text-gray-500"}/>
                    </div>}
                    <div className={"grid sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4  gap-5 p-5 border-b"}>
                        {data?.list?.map(item => {
                            return <Link key={item?.itemId} href={`/item/${item?.itemId}`}>
                                <div
                                    className={"flex flex-row items-center gap-2 p-5 cursor-pointer hover:bg-gray-50 transition-background rounded-md justify-center text-center"}
                                >
                                    <div className={"flex items-start flex-col text-center"}>
                                        <Image src={item?.image?.x256Url} alt={item?.name} loading={"lazy"}
                                               className={"object-cover w-[180px] h-[180px]"} isZoomed={true}/>
                                        <div
                                            className={"text-left text-gray-800 text-medium font-bold mt-2 pl-1 flex items-center gap-1 w-full line-clamp-1 break-all "}>
                                            <span className={"line-clamp-1"}>
                                                {item?.recommended &&
                                                    <span
                                                        className={"text-red-500 mr-1"}
                                                    >
                                                    <LikeFilled/>
                                                </span>}
                                                {item?.name}
                                            </span>
                                        </div>
                                        <div
                                            className={"text-left text-gray-800 text-medium pl-1 flex items-center gap-1 w-full line-clamp-1 break-all "}>
                                            <span className={"text-orange-500"}>
                                                <StarFilled/>
                                            </span>
                                            <span className={"text-gray-500"}>
                                                {item?.compScore > 0 ?
                                                    <span
                                                        className={"text-orange-500 font-bold"}>{item?.compScore?.toFixed(2)}</span> :
                                                    <span className={"text-sm"}>暂无评分</span>
                                                }
                                            </span>
                                        </div>
                                        <div>
                                            <span className={"text-red-500 text-lg font-bold"}>
                                                ￥{(item?.promotionPrice || item?.price)?.toFixed(2)}
                                            </span>
                                            <span className={"text-gray-500 text-sm ml-1 line-through"}>
                                                ￥{item?.price?.toFixed(2)}
                                            </span>
                                        </div>
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
