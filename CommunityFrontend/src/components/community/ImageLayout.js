"use client";
import {Image} from "antd"
import {useEffect, useRef, useState} from "react";

export default function ImageLayout({imageInfoList}) {
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
            default:
                tmpContainerClass = "grid grid-cols-2 grid-rows-2 gap-1.5";
                break;
        }
        setContainerClass(tmpContainerClass);
    }, [sliceCount]);

    return <div className={"rounded-md overflow-hidden"}>
        <div className={containerClass}>
            {imageInfoList?.slice(0, sliceCount).map((image, index) => (
                <div key={index}
                     className={`relative ${sliceCount === 3 && index === 0 ? 'col-span-2' : ''} ${sliceCount === 1 ? 'h-auto max-h-[384px]' : 'h-[200px]'}`}>
                    <Image
                        src={image?.x384Url}
                        alt={image.name}
                        className={"object-cover"}
                        width={"100%"}
                        height={sliceCount === 1 ? 384 : 200}
                        preview={{
                            src: image?.originalUrl,
                        }}
                    />
                </div>
            ))}
        </div>
    </div>;
}
