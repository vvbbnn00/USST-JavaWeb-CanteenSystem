"use client";
import {Image} from "antd"
import {useEffect, useRef, useState} from "react";
import {FALLBACK_IMG} from "@/utils/fallback";

export default function ImageLayout({imageInfoList, maxSlice = 4}) {
    if (!imageInfoList) {
        return <></>;
    }
    const [sliceCount, setSliceCount] = useState(4);
    const [containerClass, setContainerClass] = useState("grid grid-cols-2 grid-rows-2 gap-1.5");
    const windowWidth = useRef(window.innerWidth);

    useEffect(() => {
        const imageCount = imageInfoList?.length;
        if (windowWidth.current < 640) {
            setSliceCount(Math.min(imageCount, 1));
        }
        if (windowWidth.current >= 640) {
            setSliceCount(Math.min(imageCount, 4));
        }

    }, [windowWidth.current]);

    useEffect(() => {
        let tmpContainerClass = "";
        switch (sliceCount) {
            case 1:
                tmpContainerClass = "grid grid-cols-1";
                break;
            case 2:
                tmpContainerClass = "grid grid-cols-2 gap-1.5";
                break;
            case 3:
                tmpContainerClass = "grid grid-cols-3 gap-1.5";
                break;
            case 4:
                tmpContainerClass = "grid grid-cols-2 grid-rows-2 gap-1.5";
                break;
            case 5:
                tmpContainerClass = "grid grid-cols-3 grid-rows-2 gap-1.5";
                break;
            case 6:
                tmpContainerClass = "grid grid-cols-3 grid-rows-2 gap-1.5";
                break;
            case 7:
                tmpContainerClass = "grid grid-cols-3 grid-rows-3 gap-1.5";
                break;
            case 8:
                tmpContainerClass = "grid grid-cols-3 grid-rows-3 gap-1.5";
                break;
            default:
                tmpContainerClass = "grid grid-cols-3 grid-rows-3 gap-1.5";
                break;
        }
        setContainerClass(tmpContainerClass);
    }, [sliceCount]);

    return <div className={"rounded-md overflow-hidden"}>
        <div className={containerClass}>
            {imageInfoList?.slice(0, maxSlice).map((image, index) => (
                <div key={index}
                     className={`relative max-h-[500px] ${sliceCount === 1 ? 'h-auto max-h-[384px]' : 'h-auto'}`}>
                    <Image
                        src={image?.x384Url}
                        alt={image.name}
                        width={sliceCount === 1 ? 'auto' : '100%'}
                        className={sliceCount === 1 ? 'object-contain' : 'object-cover'}
                        height={sliceCount === 1 ? 'auto' : '100%'}
                        fallback={FALLBACK_IMG}
                        preview={{
                            src: image?.originalUrl,
                        }}
                    />
                </div>
            ))}
        </div>
    </div>;
}
